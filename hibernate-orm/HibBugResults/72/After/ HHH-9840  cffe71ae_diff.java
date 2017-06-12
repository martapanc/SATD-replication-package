diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
index 3c070df5ba..eab5916640 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
@@ -1,200 +1,199 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.action.spi.Executable;
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.CollectionCacheKey;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * Any action relating to insert/update/delete of a collection
  *
  * @author Gavin King
  */
 public abstract class CollectionAction implements Executable, Serializable, Comparable {
 	private transient CollectionPersister persister;
 	private transient SessionImplementor session;
 	private final PersistentCollection collection;
 
 	private final Serializable key;
 	private final String collectionRole;
 
 	protected CollectionAction(
 			final CollectionPersister persister,
 			final PersistentCollection collection, 
 			final Serializable key, 
 			final SessionImplementor session) {
 		this.persister = persister;
 		this.session = session;
 		this.key = key;
 		this.collectionRole = persister.getRole();
 		this.collection = collection;
 	}
 
 	protected PersistentCollection getCollection() {
 		return collection;
 	}
 
 	/**
 	 * Reconnect to session after deserialization...
 	 *
 	 * @param session The session being deserialized
 	 */
 	public void afterDeserialize(SessionImplementor session) {
 		if ( this.session != null || this.persister != null ) {
 			throw new IllegalStateException( "already attached to a session." );
 		}
 		// IMPL NOTE: non-flushed changes code calls this method with session == null...
 		// guard against NullPointerException
 		if ( session != null ) {
 			this.session = session;
 			this.persister = session.getFactory().getCollectionPersister( collectionRole );
 		}
 	}
 
 	@Override
 	public final void beforeExecutions() throws CacheException {
 		// we need to obtain the lock before any actions are executed, since this may be an inverse="true"
 		// bidirectional association and it is one of the earlier entity actions which actually updates
 		// the database (this action is responsible for second-level cache invalidation only)
 		if ( persister.hasCache() ) {
 			final CollectionRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-			final CollectionCacheKey ck = cache.generateCacheKey(
+			final Object ck = cache.generateCacheKey(
 					key,
 					persister,
 					session.getFactory(),
 					session.getTenantIdentifier()
 			);
 			final SoftLock lock = cache.lockItem( ck, null );
 			// the old behavior used key as opposed to getKey()
 			afterTransactionProcess = new CacheCleanupProcess( key, persister, lock );
 		}
 	}
 
 	@Override
 	public BeforeTransactionCompletionProcess getBeforeTransactionCompletionProcess() {
 		return null;
 	}
 
 	private AfterTransactionCompletionProcess afterTransactionProcess;
 
 	@Override
 	public AfterTransactionCompletionProcess getAfterTransactionCompletionProcess() {
 		return afterTransactionProcess;
 	}
 
 	@Override
 	public Serializable[] getPropertySpaces() {
 		return persister.getCollectionSpaces();
 	}
 
 	protected final CollectionPersister getPersister() {
 		return persister;
 	}
 
 	protected final Serializable getKey() {
 		Serializable finalKey = key;
 		if ( key instanceof DelayedPostInsertIdentifier ) {
 			// need to look it up from the persistence-context
 			finalKey = session.getPersistenceContext().getEntry( collection.getOwner() ).getId();
 			if ( finalKey == key ) {
 				// we may be screwed here since the collection action is about to execute
 				// and we do not know the final owner key value
 			}
 		}
 		return finalKey;
 	}
 
 	protected final SessionImplementor getSession() {
 		return session;
 	}
 
 	protected final void evict() throws CacheException {
 		if ( persister.hasCache() ) {
 			final CollectionRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-			final CollectionCacheKey ck = cache.generateCacheKey(
+			final Object ck = cache.generateCacheKey(
 					key, 
 					persister,
 					session.getFactory(),
 					session.getTenantIdentifier()
 			);
 			cache.remove( ck );
 		}
 	}
 
 	@Override
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + MessageHelper.infoString( collectionRole, key );
 	}
 
 	@Override
 	public int compareTo(Object other) {
 		final CollectionAction action = (CollectionAction) other;
 
 		// sort first by role name
 		final int roleComparison = collectionRole.compareTo( action.collectionRole );
 		if ( roleComparison != 0 ) {
 			return roleComparison;
 		}
 		else {
 			//then by fk
 			return persister.getKeyType().compare( key, action.key );
 		}
 	}
 
 	private static class CacheCleanupProcess implements AfterTransactionCompletionProcess {
 		private final Serializable key;
 		private final CollectionPersister persister;
 		private final SoftLock lock;
 
 		private CacheCleanupProcess(Serializable key, CollectionPersister persister, SoftLock lock) {
 			this.key = key;
 			this.persister = persister;
 			this.lock = lock;
 		}
 
 		@Override
 		public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
 			final CollectionRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-			final CollectionCacheKey ck = cache.generateCacheKey(
+			final Object ck = cache.generateCacheKey(
 					key,
 					persister,
 					session.getFactory(),
 					session.getTenantIdentifier()
 			);
 			cache.unlockItem( ck, lock );
 		}
 	}
 
 	protected <T> EventListenerGroup<T> listenerGroup(EventType<T> eventType) {
 		return getSession()
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( eventType );
 	}
 
 	protected EventSource eventSource() {
 		return (EventSource) getSession();
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
index d2ca5f9b8f..2ac9736be8 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
@@ -1,214 +1,213 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostCommitDeleteEventListener;
 import org.hibernate.event.spi.PostDeleteEvent;
 import org.hibernate.event.spi.PostDeleteEventListener;
 import org.hibernate.event.spi.PreDeleteEvent;
 import org.hibernate.event.spi.PreDeleteEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * The action for performing an entity deletion.
  */
 public class EntityDeleteAction extends EntityAction {
 	private final Object version;
 	private final boolean isCascadeDeleteEnabled;
 	private final Object[] state;
 
 	private SoftLock lock;
 	private Object[] naturalIdValues;
 
 	/**
 	 * Constructs an EntityDeleteAction.
 	 *
 	 * @param id The entity identifier
 	 * @param state The current (extracted) entity state
 	 * @param version The current entity version
 	 * @param instance The entity instance
 	 * @param persister The entity persister
 	 * @param isCascadeDeleteEnabled Whether cascade delete is enabled
 	 * @param session The session
 	 */
 	public EntityDeleteAction(
 			final Serializable id,
 			final Object[] state,
 			final Object version,
 			final Object instance,
 			final EntityPersister persister,
 			final boolean isCascadeDeleteEnabled,
 			final SessionImplementor session) {
 		super( session, id, instance, persister );
 		this.version = version;
 		this.isCascadeDeleteEnabled = isCascadeDeleteEnabled;
 		this.state = state;
 
 		// before remove we need to remove the local (transactional) natural id cross-reference
 		naturalIdValues = session.getPersistenceContext().getNaturalIdHelper().removeLocalNaturalIdCrossReference(
 				getPersister(),
 				getId(),
 				state
 		);
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		final Serializable id = getId();
 		final EntityPersister persister = getPersister();
 		final SessionImplementor session = getSession();
 		final Object instance = getInstance();
 
 		final boolean veto = preDelete();
 
 		Object version = this.version;
 		if ( persister.isVersionPropertyGenerated() ) {
 			// we need to grab the version value from the entity, otherwise
 			// we have issues with generated-version entities that may have
 			// multiple actions queued during the same flush
 			version = persister.getVersion( instance );
 		}
 
-		final EntityCacheKey ck;
+		final Object ck;
 		if ( persister.hasCache() ) {
 			final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
 			ck = cache.generateCacheKey( id, persister, session.getFactory(), session.getTenantIdentifier() );
 			lock = cache.lockItem( ck, version );
 		}
 		else {
 			ck = null;
 		}
 
 		if ( !isCascadeDeleteEnabled && !veto ) {
 			persister.delete( id, version, instance, session );
 		}
 		
 		//postDelete:
 		// After actually deleting a row, record the fact that the instance no longer 
 		// exists on the database (needed for identity-column key generation), and
 		// remove it from the session cache
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityEntry entry = persistenceContext.removeEntry( instance );
 		if ( entry == null ) {
 			throw new AssertionFailure( "possible nonthreadsafe access to session" );
 		}
 		entry.postDelete();
 
 		persistenceContext.removeEntity( entry.getEntityKey() );
 		persistenceContext.removeProxy( entry.getEntityKey() );
 		
 		if ( persister.hasCache() ) {
 			persister.getCacheAccessStrategy().remove( ck );
 		}
 
 		persistenceContext.getNaturalIdHelper().removeSharedNaturalIdCrossReference( persister, id, naturalIdValues );
 
 		postDelete();
 
 		if ( getSession().getFactory().getStatistics().isStatisticsEnabled() && !veto ) {
 			getSession().getFactory().getStatisticsImplementor().deleteEntity( getPersister().getEntityName() );
 		}
 	}
 
 	private boolean preDelete() {
 		boolean veto = false;
 		final EventListenerGroup<PreDeleteEventListener> listenerGroup = listenerGroup( EventType.PRE_DELETE );
 		if ( listenerGroup.isEmpty() ) {
 			return veto;
 		}
 		final PreDeleteEvent event = new PreDeleteEvent( getInstance(), getId(), state, getPersister(), eventSource() );
 		for ( PreDeleteEventListener listener : listenerGroup.listeners() ) {
 			veto |= listener.onPreDelete( event );
 		}
 		return veto;
 	}
 
 	private void postDelete() {
 		final EventListenerGroup<PostDeleteEventListener> listenerGroup = listenerGroup( EventType.POST_DELETE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostDeleteEvent event = new PostDeleteEvent(
 				getInstance(),
 				getId(),
 				state,
 				getPersister(),
 				eventSource()
 		);
 		for ( PostDeleteEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostDelete( event );
 		}
 	}
 
 	private void postCommitDelete(boolean success) {
 		final EventListenerGroup<PostDeleteEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_DELETE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostDeleteEvent event = new PostDeleteEvent(
 				getInstance(),
 				getId(),
 				state,
 				getPersister(),
 				eventSource()
 		);
 		for ( PostDeleteEventListener listener : listenerGroup.listeners() ) {
 			if ( PostCommitDeleteEventListener.class.isInstance( listener ) ) {
 				if ( success ) {
 					listener.onPostDelete( event );
 				}
 				else {
 					((PostCommitDeleteEventListener) listener).onPostDeleteCommitFailed( event );
 				}
 			}
 			else {
 				//default to the legacy implementation that always fires the event
 				listener.onPostDelete( event );
 			}
 		}
 	}
 
 	@Override
 	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) throws HibernateException {
 		EntityPersister entityPersister = getPersister();
 		if ( entityPersister.hasCache() ) {
 			EntityRegionAccessStrategy cache = entityPersister.getCacheAccessStrategy();
-			final EntityCacheKey ck = cache.generateCacheKey(
+			final Object ck = cache.generateCacheKey(
 					getId(),
 					entityPersister,
 					session.getFactory(),
 					session.getTenantIdentifier()
 			);
 			cache.unlockItem( ck, lock );
 		}
 		postCommitDelete( success );
 	}
 
 	@Override
 	protected boolean hasPostCommitEventListeners() {
 		final EventListenerGroup<PostDeleteEventListener> group = listenerGroup( EventType.POST_COMMIT_DELETE );
 		for ( PostDeleteEventListener listener : group.listeners() ) {
 			if ( listener.requiresPostCommitHanding( getPersister() ) ) {
 				return true;
 			}
 		}
 
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
index 4061fa29bc..8b64f155c3 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
@@ -1,256 +1,255 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostCommitInsertEventListener;
 import org.hibernate.event.spi.PostInsertEvent;
 import org.hibernate.event.spi.PostInsertEventListener;
 import org.hibernate.event.spi.PreInsertEvent;
 import org.hibernate.event.spi.PreInsertEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * The action for performing an entity insertion, for entities not defined to use IDENTITY generation.
  *
  * @see EntityIdentityInsertAction
  */
 public final class EntityInsertAction extends AbstractEntityInsertAction {
 	private Object version;
 	private Object cacheEntry;
 
 	/**
 	 * Constructs an EntityInsertAction.
 	 *
 	 * @param id The entity identifier
 	 * @param state The current (extracted) entity state
 	 * @param instance The entity instance
 	 * @param version The current entity version value
 	 * @param persister The entity's persister
 	 * @param isVersionIncrementDisabled Whether version incrementing is disabled.
 	 * @param session The session
 	 */
 	public EntityInsertAction(
 			Serializable id,
 			Object[] state,
 			Object instance,
 			Object version,
 			EntityPersister persister,
 			boolean isVersionIncrementDisabled,
 			SessionImplementor session) {
 		super( id, state, instance, isVersionIncrementDisabled, persister, session );
 		this.version = version;
 	}
 
 	@Override
 	public boolean isEarlyInsert() {
 		return false;
 	}
 
 	@Override
 	protected EntityKey getEntityKey() {
 		return getSession().generateEntityKey( getId(), getPersister() );
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		nullifyTransientReferencesIfNotAlready();
 
 		final EntityPersister persister = getPersister();
 		final SessionImplementor session = getSession();
 		final Object instance = getInstance();
 		final Serializable id = getId();
 
 		final boolean veto = preInsert();
 
 		// Don't need to lock the cache here, since if someone
 		// else inserted the same pk first, the insert would fail
 
 		if ( !veto ) {
 			
 			persister.insert( id, getState(), instance, session );
 			PersistenceContext persistenceContext = session.getPersistenceContext();
 			final EntityEntry entry = persistenceContext.getEntry( instance );
 			if ( entry == null ) {
 				throw new AssertionFailure( "possible non-threadsafe access to session" );
 			}
 			
 			entry.postInsert( getState() );
 	
 			if ( persister.hasInsertGeneratedProperties() ) {
 				persister.processInsertGeneratedProperties( id, instance, getState(), session );
 				if ( persister.isVersionPropertyGenerated() ) {
 					version = Versioning.getVersion( getState(), persister );
 				}
 				entry.postUpdate( instance, getState(), version );
 			}
 
 			persistenceContext.registerInsertedKey( persister, getId() );
 		}
 
 		final SessionFactoryImplementor factory = session.getFactory();
 
 		if ( isCachePutEnabled( persister, session ) ) {
 			final CacheEntry ce = persister.buildCacheEntry(
 					instance,
 					getState(),
 					version,
 					session
 			);
 			cacheEntry = persister.getCacheEntryStructure().structure( ce );
 			final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-			final EntityCacheKey ck = cache.generateCacheKey( id, persister, factory, session.getTenantIdentifier() );
+			final Object ck = cache.generateCacheKey( id, persister, factory, session.getTenantIdentifier() );
 
 			final boolean put = cacheInsert( persister, ck );
 
 			if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor().secondLevelCachePut( cache.getRegion().getName() );
 			}
 		}
 
 		handleNaturalIdPostSaveNotifications( id );
 
 		postInsert();
 
 		if ( factory.getStatistics().isStatisticsEnabled() && !veto ) {
 			factory.getStatisticsImplementor().insertEntity( getPersister().getEntityName() );
 		}
 
 		markExecuted();
 	}
 
-	private boolean cacheInsert(EntityPersister persister, EntityCacheKey ck) {
+	private boolean cacheInsert(EntityPersister persister, Object ck) {
 		try {
 			getSession().getEventListenerManager().cachePutStart();
 			return persister.getCacheAccessStrategy().insert( ck, cacheEntry, version );
 		}
 		finally {
 			getSession().getEventListenerManager().cachePutEnd();
 		}
 	}
 
 	private void postInsert() {
 		final EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostInsertEvent event = new PostInsertEvent(
 				getInstance(),
 				getId(),
 				getState(),
 				getPersister(),
 				eventSource()
 		);
 		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostInsert( event );
 		}
 	}
 
 	private void postCommitInsert(boolean success) {
 		final EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostInsertEvent event = new PostInsertEvent(
 				getInstance(),
 				getId(),
 				getState(),
 				getPersister(),
 				eventSource()
 		);
 		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
 			if ( PostCommitInsertEventListener.class.isInstance( listener ) ) {
 				if ( success ) {
 					listener.onPostInsert( event );
 				}
 				else {
 					((PostCommitInsertEventListener) listener).onPostInsertCommitFailed( event );
 				}
 			}
 			else {
 				//default to the legacy implementation that always fires the event
 				listener.onPostInsert( event );
 			}
 		}
 	}
 
 	private boolean preInsert() {
 		boolean veto = false;
 
 		final EventListenerGroup<PreInsertEventListener> listenerGroup = listenerGroup( EventType.PRE_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return veto;
 		}
 		final PreInsertEvent event = new PreInsertEvent( getInstance(), getId(), getState(), getPersister(), eventSource() );
 		for ( PreInsertEventListener listener : listenerGroup.listeners() ) {
 			veto |= listener.onPreInsert( event );
 		}
 		return veto;
 	}
 
 	@Override
 	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) throws HibernateException {
 		final EntityPersister persister = getPersister();
 		if ( success && isCachePutEnabled( persister, getSession() ) ) {
 			final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
 			SessionFactoryImplementor sessionFactoryImplementor = session.getFactory();
-			final EntityCacheKey ck = cache.generateCacheKey( getId(), persister, sessionFactoryImplementor, session.getTenantIdentifier() );
+			final Object ck = cache.generateCacheKey( getId(), persister, sessionFactoryImplementor, session.getTenantIdentifier() );
 			final boolean put = cacheAfterInsert( cache, ck );
 
 			if ( put && sessionFactoryImplementor.getStatistics().isStatisticsEnabled() ) {
 				sessionFactoryImplementor.getStatisticsImplementor()
 						.secondLevelCachePut( cache.getRegion().getName() );
 			}
 		}
 		postCommitInsert( success );
 	}
 
-	private boolean cacheAfterInsert(EntityRegionAccessStrategy cache, EntityCacheKey ck) {
+	private boolean cacheAfterInsert(EntityRegionAccessStrategy cache, Object ck) {
 		final SessionEventListenerManager eventListenerManager = getSession().getEventListenerManager();
 		try {
 			eventListenerManager.cachePutStart();
 			return cache.afterInsert( ck, cacheEntry, version );
 		}
 		finally {
 			eventListenerManager.cachePutEnd();
 		}
 	}
 
 	@Override
 	protected boolean hasPostCommitEventListeners() {
 		final EventListenerGroup<PostInsertEventListener> group = listenerGroup( EventType.POST_COMMIT_INSERT );
 		for ( PostInsertEventListener listener : group.listeners() ) {
 			if ( listener.requiresPostCommitHanding( getPersister() ) ) {
 				return true;
 			}
 		}
 
 		return false;
 	}
 	
 	private boolean isCachePutEnabled(EntityPersister persister, SessionImplementor session) {
 		return persister.hasCache()
 				&& !persister.isCacheInvalidationRequired()
 				&& session.getCacheMode().isPutEnabled();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
index 4bffb3afb0..ea449655fa 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
@@ -1,349 +1,348 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostCommitUpdateEventListener;
 import org.hibernate.event.spi.PostUpdateEvent;
 import org.hibernate.event.spi.PostUpdateEventListener;
 import org.hibernate.event.spi.PreUpdateEvent;
 import org.hibernate.event.spi.PreUpdateEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.TypeHelper;
 
 /**
  * The action for performing entity updates.
  */
 public final class EntityUpdateAction extends EntityAction {
 	private final Object[] state;
 	private final Object[] previousState;
 	private final Object previousVersion;
 	private final int[] dirtyFields;
 	private final boolean hasDirtyCollection;
 	private final Object rowId;
 	private final Object[] previousNaturalIdValues;
 	private Object nextVersion;
 	private Object cacheEntry;
 	private SoftLock lock;
 
 	/**
 	 * Constructs an EntityUpdateAction
 	 *
 	 * @param id The entity identifier
 	 * @param state The current (extracted) entity state
 	 * @param dirtyProperties The indexes (in reference to state) properties with dirty state
 	 * @param hasDirtyCollection Were any collections dirty?
 	 * @param previousState The previous (stored) state
 	 * @param previousVersion The previous (stored) version
 	 * @param nextVersion The incremented version
 	 * @param instance The entity instance
 	 * @param rowId The entity's rowid
 	 * @param persister The entity's persister
 	 * @param session The session
 	 */
 	public EntityUpdateAction(
 			final Serializable id,
 			final Object[] state,
 			final int[] dirtyProperties,
 			final boolean hasDirtyCollection,
 			final Object[] previousState,
 			final Object previousVersion,
 			final Object nextVersion,
 			final Object instance,
 			final Object rowId,
 			final EntityPersister persister,
 			final SessionImplementor session) {
 		super( session, id, instance, persister );
 		this.state = state;
 		this.previousState = previousState;
 		this.previousVersion = previousVersion;
 		this.nextVersion = nextVersion;
 		this.dirtyFields = dirtyProperties;
 		this.hasDirtyCollection = hasDirtyCollection;
 		this.rowId = rowId;
 
 		this.previousNaturalIdValues = determinePreviousNaturalIdValues( persister, previousState, session, id );
 		session.getPersistenceContext().getNaturalIdHelper().manageLocalNaturalIdCrossReference(
 				persister,
 				id,
 				state,
 				previousNaturalIdValues,
 				CachedNaturalIdValueSource.UPDATE
 		);
 	}
 
 	private Object[] determinePreviousNaturalIdValues(
 			EntityPersister persister,
 			Object[] previousState,
 			SessionImplementor session,
 			Serializable id) {
 		if ( ! persister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		if ( previousState != null ) {
 			return session.getPersistenceContext().getNaturalIdHelper().extractNaturalIdValues( previousState, persister );
 		}
 
 		return session.getPersistenceContext().getNaturalIdSnapshot( id, persister );
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		final Serializable id = getId();
 		final EntityPersister persister = getPersister();
 		final SessionImplementor session = getSession();
 		final Object instance = getInstance();
 
 		final boolean veto = preUpdate();
 
 		final SessionFactoryImplementor factory = session.getFactory();
 		Object previousVersion = this.previousVersion;
 		if ( persister.isVersionPropertyGenerated() ) {
 			// we need to grab the version value from the entity, otherwise
 			// we have issues with generated-version entities that may have
 			// multiple actions queued during the same flush
 			previousVersion = persister.getVersion( instance );
 		}
 		
-		final EntityCacheKey ck;
+		final Object ck;
 		if ( persister.hasCache() ) {
 			final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
 			ck = cache.generateCacheKey(
 					id, 
 					persister,
 					factory,
 					session.getTenantIdentifier()
 			);
 			lock = cache.lockItem( ck, previousVersion );
 		}
 		else {
 			ck = null;
 		}
 
 		if ( !veto ) {
 			persister.update( 
 					id, 
 					state, 
 					dirtyFields, 
 					hasDirtyCollection, 
 					previousState, 
 					previousVersion, 
 					instance, 
 					rowId, 
 					session 
 			);
 		}
 
 		final EntityEntry entry = session.getPersistenceContext().getEntry( instance );
 		if ( entry == null ) {
 			throw new AssertionFailure( "possible nonthreadsafe access to session" );
 		}
 		
 		if ( entry.getStatus()==Status.MANAGED || persister.isVersionPropertyGenerated() ) {
 			// get the updated snapshot of the entity state by cloning current state;
 			// it is safe to copy in place, since by this time no-one else (should have)
 			// has a reference  to the array
 			TypeHelper.deepCopy(
 					state,
 					persister.getPropertyTypes(),
 					persister.getPropertyCheckability(),
 					state,
 					session
 			);
 			if ( persister.hasUpdateGeneratedProperties() ) {
 				// this entity defines proeprty generation, so process those generated
 				// values...
 				persister.processUpdateGeneratedProperties( id, instance, state, session );
 				if ( persister.isVersionPropertyGenerated() ) {
 					nextVersion = Versioning.getVersion( state, persister );
 				}
 			}
 			// have the entity entry doAfterTransactionCompletion post-update processing, passing it the
 			// update state and the new version (if one).
 			entry.postUpdate( instance, state, nextVersion );
 		}
 
 		if ( persister.hasCache() ) {
 			if ( persister.isCacheInvalidationRequired() || entry.getStatus()!= Status.MANAGED ) {
 				persister.getCacheAccessStrategy().remove( ck );
 			}
 			else {
 				//TODO: inefficient if that cache is just going to ignore the updated state!
 				final CacheEntry ce = persister.buildCacheEntry( instance,state, nextVersion, getSession() );
 				cacheEntry = persister.getCacheEntryStructure().structure( ce );
 
 				final boolean put = cacheUpdate( persister, previousVersion, ck );
 				if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 					factory.getStatisticsImplementor().secondLevelCachePut( getPersister().getCacheAccessStrategy().getRegion().getName() );
 				}
 			}
 		}
 
 		session.getPersistenceContext().getNaturalIdHelper().manageSharedNaturalIdCrossReference(
 				persister,
 				id,
 				state,
 				previousNaturalIdValues,
 				CachedNaturalIdValueSource.UPDATE
 		);
 
 		postUpdate();
 
 		if ( factory.getStatistics().isStatisticsEnabled() && !veto ) {
 			factory.getStatisticsImplementor().updateEntity( getPersister().getEntityName() );
 		}
 	}
 
-	private boolean cacheUpdate(EntityPersister persister, Object previousVersion, EntityCacheKey ck) {
+	private boolean cacheUpdate(EntityPersister persister, Object previousVersion, Object ck) {
 		try {
 			getSession().getEventListenerManager().cachePutStart();
 			return persister.getCacheAccessStrategy().update( ck, cacheEntry, nextVersion, previousVersion );
 		}
 		finally {
 			getSession().getEventListenerManager().cachePutEnd();
 		}
 	}
 
 	private boolean preUpdate() {
 		boolean veto = false;
 		final EventListenerGroup<PreUpdateEventListener> listenerGroup = listenerGroup( EventType.PRE_UPDATE );
 		if ( listenerGroup.isEmpty() ) {
 			return veto;
 		}
 		final PreUpdateEvent event = new PreUpdateEvent(
 				getInstance(),
 				getId(),
 				state,
 				previousState,
 				getPersister(),
 				eventSource()
 		);
 		for ( PreUpdateEventListener listener : listenerGroup.listeners() ) {
 			veto |= listener.onPreUpdate( event );
 		}
 		return veto;
 	}
 
 	private void postUpdate() {
 		final EventListenerGroup<PostUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_UPDATE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostUpdateEvent event = new PostUpdateEvent(
 				getInstance(),
 				getId(),
 				state,
 				previousState,
 				dirtyFields,
 				getPersister(),
 				eventSource()
 		);
 		for ( PostUpdateEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostUpdate( event );
 		}
 	}
 
 	private void postCommitUpdate(boolean success) {
 		final EventListenerGroup<PostUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_UPDATE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostUpdateEvent event = new PostUpdateEvent(
 				getInstance(),
 				getId(),
 				state,
 				previousState,
 				dirtyFields,
 				getPersister(),
 				eventSource()
 		);
 		for ( PostUpdateEventListener listener : listenerGroup.listeners() ) {
 			if ( PostCommitUpdateEventListener.class.isInstance( listener ) ) {
 				if ( success ) {
 					listener.onPostUpdate( event );
 				}
 				else {
 					((PostCommitUpdateEventListener) listener).onPostUpdateCommitFailed( event );
 				}
 			}
 			else {
 				//default to the legacy implementation that always fires the event
 				listener.onPostUpdate( event );
 			}
 		}
 	}
 
 	@Override
 	protected boolean hasPostCommitEventListeners() {
 		final EventListenerGroup<PostUpdateEventListener> group = listenerGroup( EventType.POST_COMMIT_UPDATE );
 		for ( PostUpdateEventListener listener : group.listeners() ) {
 			if ( listener.requiresPostCommitHanding( getPersister() ) ) {
 				return true;
 			}
 		}
 
 		return false;
 	}
 
 	@Override
 	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) throws CacheException {
 		final EntityPersister persister = getPersister();
 		if ( persister.hasCache() ) {
 			final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-			final EntityCacheKey ck = cache.generateCacheKey(
+			final Object ck = cache.generateCacheKey(
 					getId(),
 					persister,
 					session.getFactory(),
 					session.getTenantIdentifier()
 					
 			);
 
 			if ( success && cacheEntry!=null /*!persister.isCacheInvalidationRequired()*/ ) {
 				final boolean put = cacheAfterUpdate( cache, ck );
 
 				if ( put && getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
 					getSession().getFactory().getStatisticsImplementor().secondLevelCachePut( cache.getRegion().getName() );
 				}
 			}
 			else {
 				cache.unlockItem( ck, lock );
 			}
 		}
 		postCommitUpdate( success );
 	}
 
-	private boolean cacheAfterUpdate(EntityRegionAccessStrategy cache, EntityCacheKey ck) {
+	private boolean cacheAfterUpdate(EntityRegionAccessStrategy cache, Object ck) {
 		SessionEventListenerManager eventListenerManager = getSession().getEventListenerManager();
 		try {
 			eventListenerManager.cachePutStart();
 			return cache.afterUpdate( ck, cacheEntry, nextVersion, previousVersion, lock );
 		}
 		finally {
 			eventListenerManager.cachePutEnd();
 		}
 	}
 
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/CollectionCacheInvalidator.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/CollectionCacheInvalidator.java
index 83b5254a2a..773ce619e6 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/CollectionCacheInvalidator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/CollectionCacheInvalidator.java
@@ -1,152 +1,151 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.internal;
 
 import java.io.Serializable;
 import java.util.Set;
 
 import org.hibernate.boot.Metadata;
-import org.hibernate.cache.spi.CollectionCacheKey;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostDeleteEvent;
 import org.hibernate.event.spi.PostDeleteEventListener;
 import org.hibernate.event.spi.PostInsertEvent;
 import org.hibernate.event.spi.PostInsertEventListener;
 import org.hibernate.event.spi.PostUpdateEvent;
 import org.hibernate.event.spi.PostUpdateEventListener;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 import org.jboss.logging.Logger;
 
 /**
  * Allows the collection cache to be automatically evicted if an element is inserted/removed/updated *without* properly
  * managing both sides of the association (ie, the ManyToOne collection is changed w/o properly managing the OneToMany).
  * 
  * For this functionality to be used, {@link org.hibernate.cfg.AvailableSettings#AUTO_EVICT_COLLECTION_CACHE} must be
  * enabled.  For performance reasons, it's disabled by default.
  * 
  * @author Andreas Berger
  */
 public class CollectionCacheInvalidator
 		implements Integrator, PostInsertEventListener, PostDeleteEventListener, PostUpdateEventListener {
 	private static final Logger LOG = Logger.getLogger( CollectionCacheInvalidator.class.getName() );
 
 	@Override
 	public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory,
 			SessionFactoryServiceRegistry serviceRegistry) {
 		integrate( serviceRegistry, sessionFactory );
 	}
 
 	@Override
 	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 	}
 
 	@Override
 	public void onPostInsert(PostInsertEvent event) {
 		evictCache( event.getEntity(), event.getPersister(), event.getSession(), null );
 	}
 
 	@Override
 	public boolean requiresPostCommitHanding(EntityPersister persister) {
 		return true;
 	}
 
 	@Override
 	public void onPostDelete(PostDeleteEvent event) {
 		evictCache( event.getEntity(), event.getPersister(), event.getSession(), null );
 	}
 
 	@Override
 	public void onPostUpdate(PostUpdateEvent event) {
 		evictCache( event.getEntity(), event.getPersister(), event.getSession(), event.getOldState() );
 	}
 
 	private void integrate(SessionFactoryServiceRegistry serviceRegistry, SessionFactoryImplementor sessionFactory) {
 		if ( !sessionFactory.getSettings().isAutoEvictCollectionCache() ) {
 			// feature is disabled
 			return;
 		}
 		if ( !sessionFactory.getSettings().isSecondLevelCacheEnabled() ) {
 			// Nothing to do, if caching is disabled
 			return;
 		}
 		EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 		eventListenerRegistry.appendListeners( EventType.POST_INSERT, this );
 		eventListenerRegistry.appendListeners( EventType.POST_DELETE, this );
 		eventListenerRegistry.appendListeners( EventType.POST_UPDATE, this );
 	}
 
 	private void evictCache(Object entity, EntityPersister persister, EventSource session, Object[] oldState) {
 		try {
 			SessionFactoryImplementor factory = persister.getFactory();
 
 			Set<String> collectionRoles = factory.getCollectionRolesByEntityParticipant( persister.getEntityName() );
 			if ( collectionRoles == null || collectionRoles.isEmpty() ) {
 				return;
 			}
 			for ( String role : collectionRoles ) {
 				CollectionPersister collectionPersister = factory.getCollectionPersister( role );
 				if ( !collectionPersister.hasCache() ) {
 					// ignore collection if no caching is used
 					continue;
 				}
 				// this is the property this OneToMany relation is mapped by
 				String mappedBy = collectionPersister.getMappedByProperty();
 				if ( mappedBy != null ) {
 					int i = persister.getEntityMetamodel().getPropertyIndex( mappedBy );
 					Serializable oldId = null;
 					if ( oldState != null ) {
 						// in case of updating an entity we perhaps have to decache 2 entity collections, this is the
 						// old one
 						oldId = session.getIdentifier( oldState[i] );
 					}
 					Object ref = persister.getPropertyValue( entity, i );
 					Serializable id = null;
 					if ( ref != null ) {
 						id = session.getIdentifier( ref );
 					}
 					// only evict if the related entity has changed
 					if ( id != null && !id.equals( oldId ) ) {
 						evict( id, collectionPersister, session );
 						if ( oldId != null ) {
 							evict( oldId, collectionPersister, session );
 						}
 					}
 				}
 				else {
 					LOG.debug( "Evict CollectionRegion " + role );
 					collectionPersister.getCacheAccessStrategy().evictAll();
 				}
 			}
 		}
 		catch ( Exception e ) {
 			// don't let decaching influence other logic
 			LOG.error( "", e );
 		}
 	}
 
 	private void evict(Serializable id, CollectionPersister collectionPersister, EventSource session) {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debug( "Evict CollectionRegion " + collectionPersister.getRole() + " for id " + id );
 		}
 		CollectionRegionAccessStrategy cache = collectionPersister.getCacheAccessStrategy();
-		CollectionCacheKey key = cache.generateCacheKey(
+		Object key = cache.generateCacheKey(
 				id,
 				collectionPersister,
 				session.getFactory(),
 				session.getTenantIdentifier()
 		);
 		cache.evict( key );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/DefaultCacheKeysFactory.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/DefaultCacheKeysFactory.java
index fcc060dd6f..d70bca553e 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/DefaultCacheKeysFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/DefaultCacheKeysFactory.java
@@ -1,59 +1,65 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.internal;
 
-import java.io.Serializable;
-
-import org.hibernate.cache.spi.CollectionCacheKey;
-import org.hibernate.cache.spi.EntityCacheKey;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Second level cache providers now have the option to use custom key implementations.
  * This was done as the default key implementation is very generic and is quite
  * a large object to allocate in large quantities at runtime.
  * In some extreme cases, for example when the hit ratio is very low, this was making the efficiency
  * penalty vs its benefits tradeoff questionable.
  * <p/>
  * Depending on configuration settings there might be opportunities to
  * use simpler key implementations, for example when multi-tenancy is not being used to
  * avoid the tenant identifier, or when a cache instance is entirely dedicated to a single type
  * to use the primary id only, skipping the role or entity name.
  * <p/>
  * Even with multiple types sharing the same cache, their identifiers could be of the same
  * {@link org.hibernate.type.Type}; in this case the cache container could
  * use a single type reference to implement a custom equality function without having
  * to look it up on each equality check: that's a small optimisation but the
  * equality function is often invoked extremely frequently.
  * <p/>
  * Another reason is to make it more convenient to implement custom serialization protocols when the
  * implementation supports clustering.
  *
  * @see org.hibernate.type.Type#getHashCode(Object, SessionFactoryImplementor)
  * @see org.hibernate.type.Type#isEqual(Object, Object)
  * @author Sanne Grinovero
  * @since 5.0
  */
 public class DefaultCacheKeysFactory {
 
-	public static CollectionCacheKey createCollectionKey(Serializable id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+	public static Object createCollectionKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
 		return new OldCacheKeyImplementation( id, persister.getKeyType(), persister.getRole(), tenantIdentifier, factory );
 	}
 
-	public static EntityCacheKey createEntityKey(Serializable id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+	public static Object createEntityKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
 		return new OldCacheKeyImplementation( id, persister.getIdentifierType(), persister.getRootEntityName(), tenantIdentifier, factory );
 	}
 
-	public static NaturalIdCacheKey createNaturalIdKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
+	public static Object createNaturalIdKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
 		return new OldNaturalIdCacheKey( naturalIdValues, persister, session );
 	}
 
+	public static Object getEntityId(Object cacheKey) {
+		return ((OldCacheKeyImplementation) cacheKey).getId();
+	}
+
+	public static Object getCollectionId(Object cacheKey) {
+		return ((OldCacheKeyImplementation) cacheKey).getId();
+	}
+
+	public static Object[] getNaturalIdValues(Object cacheKey) {
+		return ((OldNaturalIdCacheKey) cacheKey).getNaturalIdValues();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/OldCacheKeyImplementation.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/OldCacheKeyImplementation.java
index 2b2a00c2ce..20098a1d89 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/OldCacheKeyImplementation.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/OldCacheKeyImplementation.java
@@ -1,115 +1,95 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.internal;
 
 import java.io.Serializable;
 
-import org.hibernate.cache.spi.CollectionCacheKey;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.type.Type;
 
 /**
  * Allows multiple entity classes / collection roles to be stored in the same cache region. Also allows for composite
  * keys which do not properly implement equals()/hashCode().
  *
  * This was named org.hibernate.cache.spi.CacheKey in Hibernate until version 5.
  * Temporarily maintained as a reference while all components catch up with the refactoring to the caching interfaces.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 @Deprecated
-final class OldCacheKeyImplementation implements EntityCacheKey, CollectionCacheKey, Serializable {
-	private final Serializable key;
+final class OldCacheKeyImplementation implements Serializable {
+	private final Object id;
 	private final Type type;
 	private final String entityOrRoleName;
 	private final String tenantId;
 	private final int hashCode;
 
 	/**
 	 * Construct a new key for a collection or entity instance.
 	 * Note that an entity name should always be the root entity
 	 * name, not a subclass entity name.
 	 *
 	 * @param id The identifier associated with the cached data
 	 * @param type The Hibernate type mapping
 	 * @param entityOrRoleName The entity or collection-role name.
 	 * @param tenantId The tenant identifier associated this data.
 	 * @param factory The session factory for which we are caching
 	 */
 	OldCacheKeyImplementation(
-			final Serializable id,
+			final Object id,
 			final Type type,
 			final String entityOrRoleName,
 			final String tenantId,
 			final SessionFactoryImplementor factory) {
-		this.key = id;
+		this.id = id;
 		this.type = type;
 		this.entityOrRoleName = entityOrRoleName;
 		this.tenantId = tenantId;
 		this.hashCode = calculateHashCode( type, factory );
 	}
 
 	private int calculateHashCode(Type type, SessionFactoryImplementor factory) {
-		int result = type.getHashCode( key, factory );
+		int result = type.getHashCode(id, factory );
 		result = 31 * result + (tenantId != null ? tenantId.hashCode() : 0);
 		return result;
 	}
 
-	@Override
-	public Serializable getKey() {
-		return key;
-	}
-
-	@Override
-	public String getEntityName() {
-		//defined exclusively on EntityCacheKey
-		return entityOrRoleName;
-	}
-
-	@Override
-	public String getCollectionRole() {
-		//defined exclusively on CollectionCacheKey
-		return entityOrRoleName;
-	}
-
-	@Override
-	public String getTenantId() {
-		return tenantId;
+	public Object getId() {
+		return id;
 	}
 
 	@Override
 	public boolean equals(Object other) {
 		if ( other == null ) {
 			return false;
 		}
 		if ( this == other ) {
 			return true;
 		}
 		if ( hashCode != other.hashCode() || !( other instanceof OldCacheKeyImplementation ) ) {
 			//hashCode is part of this check since it is pre-calculated and hash must match for equals to be true
 			return false;
 		}
 		final OldCacheKeyImplementation that = (OldCacheKeyImplementation) other;
 		return EqualsHelper.equals( entityOrRoleName, that.entityOrRoleName )
-				&& type.isEqual( key, that.key )
+				&& type.isEqual(id, that.id)
 				&& EqualsHelper.equals( tenantId, that.tenantId );
 	}
 
 	@Override
 	public int hashCode() {
 		return hashCode;
 	}
 
 	@Override
 	public String toString() {
 		// Used to be required for OSCache
-		return entityOrRoleName + '#' + key.toString();
+		return entityOrRoleName + '#' + id.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/OldNaturalIdCacheKey.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/OldNaturalIdCacheKey.java
index 8d3ca82f56..8acf9de9f9 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/OldNaturalIdCacheKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/OldNaturalIdCacheKey.java
@@ -1,160 +1,159 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.internal;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
 import java.util.Arrays;
 
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.ValueHolder;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Defines a key for caching natural identifier resolutions into the second level cache.
  *
  * This was named org.hibernate.cache.spi.NaturalIdCacheKey in Hibernate until version 5.
  * Temporarily maintained as a reference while all components catch up with the refactoring to the caching interfaces.
  *
  * @author Eric Dalquist
  * @author Steve Ebersole
  */
 @Deprecated
-public class OldNaturalIdCacheKey implements NaturalIdCacheKey, Serializable {
+public class OldNaturalIdCacheKey implements Serializable {
 	private final Serializable[] naturalIdValues;
 	private final String entityName;
 	private final String tenantId;
 	private final int hashCode;
 	// "transient" is important here -- NaturalIdCacheKey needs to be Serializable
 	private transient ValueHolder<String> toString;
 
 	/**
 	 * Construct a new key for a caching natural identifier resolutions into the second level cache.
 	 *
 	 * @param naturalIdValues The naturalIdValues associated with the cached data
 	 * @param persister The persister for the entity
 	 * @param session The originating session
 	 */
 	public OldNaturalIdCacheKey(
 			final Object[] naturalIdValues,
 			final EntityPersister persister,
 			final SessionImplementor session) {
 
 		this.entityName = persister.getRootEntityName();
 		this.tenantId = session.getTenantIdentifier();
 
 		this.naturalIdValues = new Serializable[naturalIdValues.length];
 
 		final SessionFactoryImplementor factory = session.getFactory();
 		final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 		final Type[] propertyTypes = persister.getPropertyTypes();
 
 		final int prime = 31;
 		int result = 1;
 		result = prime * result + ( ( this.entityName == null ) ? 0 : this.entityName.hashCode() );
 		result = prime * result + ( ( this.tenantId == null ) ? 0 : this.tenantId.hashCode() );
 		for ( int i = 0; i < naturalIdValues.length; i++ ) {
 			final int naturalIdPropertyIndex = naturalIdPropertyIndexes[i];
 			final Type type = propertyTypes[naturalIdPropertyIndex];
 			final Object value = naturalIdValues[i];
 
 			result = prime * result + (value != null ? type.getHashCode( value, factory ) : 0);
 
 			// The natural id may not be fully resolved in some situations.  See HHH-7513 for one of them
 			// (re-attaching a mutable natural id uses a database snapshot and hydration does not resolve associations).
 			// TODO: The snapshot should probably be revisited at some point.  Consider semi-resolving, hydrating, etc.
 			if (type instanceof EntityType && type.getSemiResolvedType( factory ).getReturnedClass().isInstance( value )) {
 				this.naturalIdValues[i] = (Serializable) value;
 			}
 			else {
 				this.naturalIdValues[i] = type.disassemble( value, session, null );
 			}
 		}
 
 		this.hashCode = result;
 		initTransients();
 	}
 
 	private void initTransients() {
 		this.toString = new ValueHolder<String>(
 				new ValueHolder.DeferredInitializer<String>() {
 					@Override
 					public String initialize() {
 						//Complex toString is needed as naturalIds for entities are not simply based on a single value like primary keys
 						//the only same way to differentiate the keys is to included the disassembled values in the string.
 						final StringBuilder toStringBuilder = new StringBuilder( entityName ).append( "##NaturalId[" );
 						for ( int i = 0; i < naturalIdValues.length; i++ ) {
 							toStringBuilder.append( naturalIdValues[i] );
 							if ( i + 1 < naturalIdValues.length ) {
 								toStringBuilder.append( ", " );
 							}
 						}
 						toStringBuilder.append( "]" );
 
 						return toStringBuilder.toString();
 					}
 				}
 		);
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getEntityName() {
 		return entityName;
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getTenantId() {
 		return tenantId;
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public Serializable[] getNaturalIdValues() {
 		return naturalIdValues;
 	}
 
 	@Override
 	public String toString() {
 		return toString.getValue();
 	}
 
 	@Override
 	public int hashCode() {
 		return this.hashCode;
 	}
 
 	@Override
 	public boolean equals(Object o) {
 		if ( o == null ) {
 			return false;
 		}
 		if ( this == o ) {
 			return true;
 		}
 
 		if ( hashCode != o.hashCode() || !( o instanceof OldNaturalIdCacheKey ) ) {
 			//hashCode is part of this check since it is pre-calculated and hash must match for equals to be true
 			return false;
 		}
 
 		final OldNaturalIdCacheKey other = (OldNaturalIdCacheKey) o;
 		return EqualsHelper.equals( entityName, other.entityName )
 				&& EqualsHelper.equals( tenantId, other.tenantId )
 				&& Arrays.deepEquals( this.naturalIdValues, other.naturalIdValues );
 	}
 
 	private void readObject(ObjectInputStream ois)
 			throws ClassNotFoundException, IOException {
 		ois.defaultReadObject();
 		initTransients();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheKey.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheKey.java
deleted file mode 100644
index d6583060d4..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheKey.java
+++ /dev/null
@@ -1,18 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.cache.spi;
-
-/**
- * Allows multiple entity roles to be stored in the same cache region. Also allows for composite
- * keys which do not properly implement equals()/hashCode().
- *
- * @author Gavin King
- * @author Steve Ebersole
- */
-public interface CacheKey {
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/CollectionCacheKey.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/CollectionCacheKey.java
deleted file mode 100644
index 7a9154b795..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/CollectionCacheKey.java
+++ /dev/null
@@ -1,26 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.cache.spi;
-
-import java.io.Serializable;
-
-/**
- * Allows multiple collection roles to be stored in the same cache region. Also allows for composite
- * keys which do not properly implement equals()/hashCode().
- *
- * @author Sanne Grinovero
- * @since 5.0
- */
-public interface CollectionCacheKey extends CacheKey {
-
-	public Serializable getKey();
-
-	public String getCollectionRole();
-
-	public String getTenantId();
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/EntityCacheKey.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/EntityCacheKey.java
deleted file mode 100755
index cee5a9319b..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/EntityCacheKey.java
+++ /dev/null
@@ -1,26 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.cache.spi;
-
-import java.io.Serializable;
-
-/**
- * Allows multiple entity roles to be stored in the same cache region. Also allows for composite
- * keys which do not properly implement equals()/hashCode().
- *
- * @author Gavin King
- * @author Steve Ebersole
- */
-public interface EntityCacheKey extends CacheKey {
-
-	public Serializable getKey();
-
-	public String getEntityName();
-
-	public String getTenantId();
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java
deleted file mode 100644
index 0f22f37e7b..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/NaturalIdCacheKey.java
+++ /dev/null
@@ -1,23 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
- * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
- */
-package org.hibernate.cache.spi;
-
-/**
- * Defines a key for caching natural identifier resolutions into the second level cache.
- *
- * @author Sanne Grinovero
- * @since 5.0
- */
-public interface NaturalIdCacheKey extends CacheKey {
-
-	String getEntityName();
-
-	Object[] getNaturalIdValues();
-
-	String getTenantId();
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/CollectionRegionAccessStrategy.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/CollectionRegionAccessStrategy.java
index 8813d6a2cd..621dc3c992 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/CollectionRegionAccessStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/CollectionRegionAccessStrategy.java
@@ -1,49 +1,54 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.spi.access;
 
-import java.io.Serializable;
-
-import org.hibernate.cache.spi.CollectionCacheKey;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
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
-public interface CollectionRegionAccessStrategy extends RegionAccessStrategy<CollectionCacheKey> {
+public interface CollectionRegionAccessStrategy extends RegionAccessStrategy {
 
 	/**
 	 * To create instances of CollectionCacheKey for this region, Hibernate will invoke this method
 	 * exclusively so that generated implementations can generate optimised keys.
 	 * @param id the primary identifier of the Collection
 	 * @param persister the persister for the type for which a key is being generated
 	 * @param factory a reference to the current SessionFactory
 	 * @param tenantIdentifier the tenant id, or null if multi-tenancy is not being used.
 	 * @return a key which can be used to identify this collection on this same region
 	 */
-	public CollectionCacheKey generateCacheKey(Serializable id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier);
+	public Object generateCacheKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier);
+
+	/**
+	 * Performs reverse operation to {@link #generateCacheKey(Object, CollectionPersister, SessionFactoryImplementor, String)}
+	 *
+	 * @param cacheKey key previously returned from {@link #generateCacheKey(Object, CollectionPersister, SessionFactoryImplementor, String)}
+	 * @return original key passed to {@link #generateCacheKey(Object, CollectionPersister, SessionFactoryImplementor, String)}
+	 */
+	public Object getCacheKeyId(Object cacheKey);
 
 	/**
 	 * Get the wrapped collection cache region
 	 *
 	 * @return The underlying region
 	 */
 	public CollectionRegion getRegion();
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/EntityRegionAccessStrategy.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/EntityRegionAccessStrategy.java
index a47eedfe55..32c5382b94 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/EntityRegionAccessStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/EntityRegionAccessStrategy.java
@@ -1,107 +1,112 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.spi.access;
 
-import java.io.Serializable;
-
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Contract for managing transactional and concurrent access to cached entity
  * data.  The expected call sequences related to various operations are:<ul>
  *     <li><b>INSERTS</b> : {@link #insert} -> {@link #afterInsert}</li>
  *     <li><b>UPDATES</b> : {@link #lockItem} -> {@link #update} -> {@link #afterUpdate}</li>
  *     <li><b>DELETES</b> : {@link #lockItem} -> {@link #remove} -> {@link #unlockItem}</li>
  *     <li><b>LOADS</b> : {@link @putFromLoad}</li>
  * </ul>
  * <p/>
  * There is another usage pattern that is used to invalidate entries
  * after performing "bulk" HQL/SQL operations:
  * {@link #lockRegion} -> {@link #removeAll} -> {@link #unlockRegion}
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
-public interface EntityRegionAccessStrategy extends RegionAccessStrategy<EntityCacheKey> {
+public interface EntityRegionAccessStrategy extends RegionAccessStrategy {
 
 	/**
 	 * To create instances of EntityCacheKey for this region, Hibernate will invoke this method
 	 * exclusively so that generated implementations can generate optimised keys.
 	 * @param id the primary identifier of the entity
 	 * @param persister the persister for the type for which a key is being generated
 	 * @param factory a reference to the current SessionFactory
 	 * @param tenantIdentifier the tenant id, or null if multi-tenancy is not being used.
 	 * @return a key which can be used to identify this entity on this same region
 	 */
-	public EntityCacheKey generateCacheKey(Serializable id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier);
+	public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier);
+
+	/**
+	 * Performs reverse operation to {@link #generateCacheKey(Object, EntityPersister, SessionFactoryImplementor, String)}
+	 *
+	 * @param cacheKey key previously returned from {@link #generateCacheKey(Object, EntityPersister, SessionFactoryImplementor, String)}
+	 * @return original id passed to {@link #generateCacheKey(Object, EntityPersister, SessionFactoryImplementor, String)}
+	 */
+	public Object getCacheKeyId(Object cacheKey);
 
 	/**
 	 * Get the wrapped entity cache region
 	 *
 	 * @return The underlying region
 	 */
 	public EntityRegion getRegion();
 
 	/**
 	 * Called after an item has been inserted (before the transaction completes),
 	 * instead of calling evict().
 	 * This method is used by "synchronous" concurrency strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param version The item's version value
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propagated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
-	public boolean insert(EntityCacheKey key, Object value, Object version) throws CacheException;
+	public boolean insert(Object key, Object value, Object version) throws CacheException;
 
 	/**
 	 * Called after an item has been inserted (after the transaction completes),
 	 * instead of calling release().
 	 * This method is used by "asynchronous" concurrency strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param version The item's version value
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propagated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
-	public boolean afterInsert(EntityCacheKey key, Object value, Object version) throws CacheException;
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException;
 
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
 	 * @throws CacheException Propagated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
-	public boolean update(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion) throws CacheException;
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion) throws CacheException;
 
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
 	 * @throws CacheException Propagated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
-	public boolean afterUpdate(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException;
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) throws CacheException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/NaturalIdRegionAccessStrategy.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/NaturalIdRegionAccessStrategy.java
index 044d51829e..bbf60eb8c9 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/NaturalIdRegionAccessStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/NaturalIdRegionAccessStrategy.java
@@ -1,107 +1,114 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.spi.access;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Contract for managing transactional and concurrent access to cached naturalId
  * data.  The expected call sequences related to various operations are:<ul>
  *     <li><b>INSERTS</b> : {@link #insert} -> {@link #afterInsert}</li>
  *     <li><b>UPDATES</b> : {@link #lockItem} -> {@link #remove} -> {@link #update} -> {@link #afterUpdate}</li>
  *     <li><b>DELETES</b> : {@link #lockItem} -> {@link #remove} -> {@link #unlockItem}</li>
  *     <li><b>LOADS</b> : {@link @putFromLoad}</li>
  * </ul>
  * Note the special case of <b>UPDATES</b> above.  Because the cache key itself has changed here we need to remove the
  * old entry as well as
  * <p/>
  * There is another usage pattern that is used to invalidate entries
  * after performing "bulk" HQL/SQL operations:
  * {@link #lockRegion} -> {@link #removeAll} -> {@link #unlockRegion}
  * <p/>
  * IMPORTANT : NaturalIds are not versioned so {@code null} will always be passed to the version parameter to:<ul>
  *     <li>{@link #putFromLoad(Object, Object, long, Object)}</li>
  *     <li>{@link #putFromLoad(Object, Object, long, Object, boolean)}</li>
  *     <li>{@link #lockItem(Object, Object)}</li>
  * </ul>
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Eric Dalquist
  */
-public interface NaturalIdRegionAccessStrategy extends RegionAccessStrategy<NaturalIdCacheKey> {
+public interface NaturalIdRegionAccessStrategy extends RegionAccessStrategy {
 
 	/**
 	 * To create instances of NaturalIdCacheKey for this region, Hibernate will invoke this method
 	 * exclusively so that generated implementations can generate optimised keys.
 	 * @param naturalIdValues the sequence of values which unequivocally identifies a cached element on this region
 	 * @param persister the persister of the element being cached
 	 * @param session
 	 * @return a key which can be used to identify this an element unequivocally on this same region
 	 */
-	public NaturalIdCacheKey generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session);
+	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session);
+
+	/**
+	 * Performs reverse operation to {@link #generateCacheKey(Object[], EntityPersister, SessionImplementor)}, returning
+	 * the original naturalIdValues.
+	 * @param cacheKey key returned from {@link #generateCacheKey(Object[], EntityPersister, SessionImplementor)}
+	 * @return the sequence of values which unequivocally identifies a cached element on this region
+	 */
+	public Object[] getNaturalIdValues(Object cacheKey);
 
 	/**
 	 * Get the wrapped naturalId cache region
 	 *
 	 * @return The underlying region
 	 */
 	public NaturalIdRegion getRegion();
 
 	/**
 	 * Called after an item has been inserted (before the transaction completes),
 	 * instead of calling evict().
 	 * This method is used by "synchronous" concurrency strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propagated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
-	public boolean insert(NaturalIdCacheKey key, Object value) throws CacheException;
+	public boolean insert(Object key, Object value) throws CacheException;
 
 	/**
 	 * Called after an item has been inserted (after the transaction completes),
 	 * instead of calling release().
 	 * This method is used by "asynchronous" concurrency strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propagated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
-	public boolean afterInsert(NaturalIdCacheKey key, Object value) throws CacheException;
+	public boolean afterInsert(Object key, Object value) throws CacheException;
 
 	/**
 	 * Called after an item has been updated (before the transaction completes),
 	 * instead of calling evict(). This method is used by "synchronous" concurrency
 	 * strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propagated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
-	public boolean update(NaturalIdCacheKey key, Object value) throws CacheException;
+	public boolean update(Object key, Object value) throws CacheException;
 
 	/**
 	 * Called after an item has been updated (after the transaction completes),
 	 * instead of calling release().  This method is used by "asynchronous"
 	 * concurrency strategies.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param lock The lock previously obtained from {@link #lockItem}
 	 * @return Were the contents of the cache actual changed by this operation?
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
-	public boolean afterUpdate(NaturalIdCacheKey key, Object value, SoftLock lock) throws CacheException;
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/RegionAccessStrategy.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/RegionAccessStrategy.java
index 91ccd03f56..42e4b8d710 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/RegionAccessStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/RegionAccessStrategy.java
@@ -1,141 +1,140 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.spi.access;
 
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.CacheKey;
 
 /**
  * Base access strategy for all regions.
  *
  * @author Gail Badner
  */
-public interface RegionAccessStrategy<T extends CacheKey> {
+public interface RegionAccessStrategy {
 
 	/**
 	 * Attempt to retrieve an object from the cache. Mainly used in attempting
 	 * to resolve entities/collections from the second level cache.
 	 *
 	 * @param key The key of the item to be retrieved.
 	 * @param txTimestamp a timestamp prior to the transaction start time
 	 * @return the cached object or <tt>null</tt>
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
-	Object get(T key, long txTimestamp) throws CacheException;
+	Object get(Object key, long txTimestamp) throws CacheException;
 
 	/**
 	 * Attempt to cache an object, after loading from the database.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param txTimestamp a timestamp prior to the transaction start time
 	 * @param version the item version number
 	 * @return <tt>true</tt> if the object was successfully cached
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	boolean putFromLoad(
-			T key,
+			Object key,
 			Object value,
 			long txTimestamp,
 			Object version) throws CacheException;
 
 	/**
 	 * Attempt to cache an object, after loading from the database, explicitly
 	 * specifying the minimalPut behavior.
 	 *
 	 * @param key The item key
 	 * @param value The item
 	 * @param txTimestamp a timestamp prior to the transaction start time
 	 * @param version the item version number
 	 * @param minimalPutOverride Explicit minimalPut flag
 	 * @return <tt>true</tt> if the object was successfully cached
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	boolean putFromLoad(
-			T key,
+			Object key,
 			Object value,
 			long txTimestamp,
 			Object version,
 			boolean minimalPutOverride) throws CacheException;
 
 	/**
 	 * We are going to attempt to update/delete the keyed object. This
 	 * method is used by "asynchronous" concurrency strategies.
 	 * <p/>
 	 * The returned object must be passed back to {@link #unlockItem}, to release the
 	 * lock. Concurrency strategies which do not support client-visible
 	 * locks may silently return null.
 	 *
 	 * @param key The key of the item to lock
 	 * @param version The item's current version value
 	 * @return A representation of our lock on the item; or null.
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
-	SoftLock lockItem(T key, Object version) throws CacheException;
+	SoftLock lockItem(Object key, Object version) throws CacheException;
 
 	/**
 	 * Lock the entire region
 	 *
 	 * @return A representation of our lock on the item; or null.
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	SoftLock lockRegion() throws CacheException;
 
 	/**
 	 * Called when we have finished the attempted update/delete (which may or
 	 * may not have been successful), after transaction completion.  This method
 	 * is used by "asynchronous" concurrency strategies.
 	 *
 	 * @param key The item key
 	 * @param lock The lock previously obtained from {@link #lockItem}
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
-	void unlockItem(T key, SoftLock lock) throws CacheException;
+	void unlockItem(Object key, SoftLock lock) throws CacheException;
 
 	/**
 	 * Called after we have finished the attempted invalidation of the entire
 	 * region
 	 *
 	 * @param lock The lock previously obtained from {@link #lockRegion}
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	void unlockRegion(SoftLock lock) throws CacheException;
 
 	/**
 	 * Called after an item has become stale (before the transaction completes).
 	 * This method is used by "synchronous" concurrency strategies.
 	 *
 	 * @param key The key of the item to remove
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
-	void remove(T key) throws CacheException;
+	void remove(Object key) throws CacheException;
 
 	/**
 	 * Called to evict data from the entire region
 	 *
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	void removeAll() throws CacheException;
 
 	/**
 	 * Forcibly evict an item from the cache immediately without regard for transaction
 	 * isolation.
 	 *
 	 * @param key The key of the item to remove
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
-	void evict(T key) throws CacheException;
+	void evict(Object key) throws CacheException;
 
 	/**
 	 * Forcibly evict all items from the cache immediately without regard for transaction
 	 * isolation.
 	 *
 	 * @throws org.hibernate.cache.CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 */
 	void evictAll() throws CacheException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/CacheHelper.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/CacheHelper.java
index 06cfcc0cb8..6eb0ff52da 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/CacheHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/CacheHelper.java
@@ -1,41 +1,40 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.internal;
 
 import java.io.Serializable;
 
-import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * @author Steve Ebersole
  * @author Sanne Grinovero
  */
 public final class CacheHelper {
 
 	private CacheHelper() {
 	}
 
-	public static <T extends CacheKey> Serializable fromSharedCache(
+	public static Serializable fromSharedCache(
 			SessionImplementor session,
-			T cacheKey,
-			RegionAccessStrategy<T> cacheAccessStrategy) {
+			Object cacheKey,
+			RegionAccessStrategy cacheAccessStrategy) {
 		final SessionEventListenerManager eventListenerManager = session.getEventListenerManager();
 		Serializable cachedValue = null;
 		eventListenerManager.cacheGetStart();
 		try {
 			cachedValue = (Serializable) cacheAccessStrategy.get( cacheKey, session.getTimestamp() );
 		}
 		finally {
 			eventListenerManager.cacheGetEnd( cachedValue != null );
 		}
 		return cachedValue;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java
index bc1f9f5040..0ef204f9c8 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java
@@ -1,494 +1,493 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.hibernate.AssertionFailure;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 /**
  * Maintains a {@link org.hibernate.engine.spi.PersistenceContext}-level 2-way cross-reference (xref) between the 
  * identifiers and natural ids of entities associated with the PersistenceContext.
  * <p/>
  * Most operations resolve the proper {@link NaturalIdResolutionCache} to use based on the persister and 
  * simply delegate calls there.
  * 
  * @author Steve Ebersole
  */
 public class NaturalIdXrefDelegate {
 	private static final Logger LOG = Logger.getLogger( NaturalIdXrefDelegate.class );
 
 	private final StatefulPersistenceContext persistenceContext;
 	private final ConcurrentHashMap<EntityPersister, NaturalIdResolutionCache> naturalIdResolutionCacheMap = new ConcurrentHashMap<EntityPersister, NaturalIdResolutionCache>();
 
 	/**
 	 * Constructs a NaturalIdXrefDelegate
 	 *
 	 * @param persistenceContext The persistence context that owns this delegate
 	 */
 	public NaturalIdXrefDelegate(StatefulPersistenceContext persistenceContext) {
 		this.persistenceContext = persistenceContext;
 	}
 
 	/**
 	 * Access to the session (via the PersistenceContext) to which this delegate ultimately belongs.
 	 *
 	 * @return The session
 	 */
 	protected SessionImplementor session() {
 		return persistenceContext.getSession();
 	}
 
 	/**
 	 * Creates needed cross-reference entries between the given primary (pk) and natural (naturalIdValues) key values
 	 * for the given persister.  Returns an indication of whether entries were actually made.  If those values already
 	 * existed as an entry, {@code false} would be returned here.
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param pk The primary key value
 	 * @param naturalIdValues The natural id value(s)
 	 *
 	 * @return {@code true} if a new entry was actually added; {@code false} otherwise.
 	 */
 	public boolean cacheNaturalIdCrossReference(EntityPersister persister, Serializable pk, Object[] naturalIdValues) {
 		validateNaturalId( persister, naturalIdValues );
 
 		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		if ( entityNaturalIdResolutionCache == null ) {
 			entityNaturalIdResolutionCache = new NaturalIdResolutionCache( persister );
 			NaturalIdResolutionCache previousInstance = naturalIdResolutionCacheMap.putIfAbsent( persister, entityNaturalIdResolutionCache );
 			if ( previousInstance != null ) {
 				entityNaturalIdResolutionCache = previousInstance;
 			}
 		}
 		return entityNaturalIdResolutionCache.cache( pk, naturalIdValues );
 	}
 
 	/**
 	 * Handle removing cross reference entries for the given natural-id/pk combo
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param pk The primary key value
 	 * @param naturalIdValues The natural id value(s)
 	 * 
 	 * @return The cached values, if any.  May be different from incoming values.
 	 */
 	public Object[] removeNaturalIdCrossReference(EntityPersister persister, Serializable pk, Object[] naturalIdValues) {
 		persister = locatePersisterForKey( persister );
 		validateNaturalId( persister, naturalIdValues );
 
 		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		Object[] sessionCachedNaturalIdValues = null;
 		if ( entityNaturalIdResolutionCache != null ) {
 			final CachedNaturalId cachedNaturalId = entityNaturalIdResolutionCache.pkToNaturalIdMap
 					.remove( pk );
 			if ( cachedNaturalId != null ) {
 				entityNaturalIdResolutionCache.naturalIdToPkMap.remove( cachedNaturalId );
 				sessionCachedNaturalIdValues = cachedNaturalId.getValues();
 			}
 		}
 
 		if ( persister.hasNaturalIdCache() ) {
 			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister
 					.getNaturalIdCacheAccessStrategy();
-			final NaturalIdCacheKey naturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey( naturalIdValues, persister, session() );
+			final Object naturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey( naturalIdValues, persister, session() );
 			naturalIdCacheAccessStrategy.evict( naturalIdCacheKey );
 
 			if ( sessionCachedNaturalIdValues != null
 					&& !Arrays.equals( sessionCachedNaturalIdValues, naturalIdValues ) ) {
-				final NaturalIdCacheKey sessionNaturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey( sessionCachedNaturalIdValues, persister, session() );
+				final Object sessionNaturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey( sessionCachedNaturalIdValues, persister, session() );
 				naturalIdCacheAccessStrategy.evict( sessionNaturalIdCacheKey );
 			}
 		}
 
 		return sessionCachedNaturalIdValues;
 	}
 
 	/**
 	 * Are the naturals id values cached here (if any) for the given persister+pk combo the same as the given values?
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param pk The primary key value
 	 * @param naturalIdValues The natural id value(s) to check
 	 * 
 	 * @return {@code true} if the given naturalIdValues match the current cached values; {@code false} otherwise.
 	 */
 	public boolean sameAsCached(EntityPersister persister, Serializable pk, Object[] naturalIdValues) {
 		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		return entityNaturalIdResolutionCache != null
 				&& entityNaturalIdResolutionCache.sameAsCached( pk, naturalIdValues );
 	}
 
 	/**
 	 * It is only valid to define natural ids at the root of an entity hierarchy.  This method makes sure we are 
 	 * using the root persister.
 	 *
 	 * @param persister The persister representing the entity type.
 	 * 
 	 * @return The root persister.
 	 */
 	protected EntityPersister locatePersisterForKey(EntityPersister persister) {
 		return persistenceContext.getSession().getFactory().getEntityPersister( persister.getRootEntityName() );
 	}
 
 	/**
 	 * Invariant validate of the natural id.  Checks include<ul>
 	 *     <li>that the entity defines a natural id</li>
 	 *     <li>the number of natural id values matches the expected number</li>
 	 * </ul>
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param naturalIdValues The natural id values
 	 */
 	protected void validateNaturalId(EntityPersister persister, Object[] naturalIdValues) {
 		if ( !persister.hasNaturalIdentifier() ) {
 			throw new IllegalArgumentException( "Entity did not define a natrual-id" );
 		}
 		if ( persister.getNaturalIdentifierProperties().length != naturalIdValues.length ) {
 			throw new IllegalArgumentException( "Mismatch between expected number of natural-id values and found." );
 		}
 	}
 
 	/**
 	 * Given a persister and primary key, find the locally cross-referenced natural id.
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param pk The entity primary key
 	 * 
 	 * @return The corresponding cross-referenced natural id values, or {@code null} if none 
 	 */
 	public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk) {
 		persister = locatePersisterForKey( persister );
 		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		if ( entityNaturalIdResolutionCache == null ) {
 			return null;
 		}
 
 		final CachedNaturalId cachedNaturalId = entityNaturalIdResolutionCache.pkToNaturalIdMap.get( pk );
 		if ( cachedNaturalId == null ) {
 			return null;
 		}
 
 		return cachedNaturalId.getValues();
 	}
 
 	/**
 	 * Given a persister and natural-id value(s), find the locally cross-referenced primary key.  Will return
 	 * {@link PersistenceContext.NaturalIdHelper#INVALID_NATURAL_ID_REFERENCE} if the given natural ids are known to
 	 * be invalid (see {@link #stashInvalidNaturalIdReference}).
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param naturalIdValues The natural id value(s)
 	 * 
 	 * @return The corresponding cross-referenced primary key, 
 	 * 		{@link PersistenceContext.NaturalIdHelper#INVALID_NATURAL_ID_REFERENCE},
 	 * 		or {@code null} if none 
 	 */
 	public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues) {
 		persister = locatePersisterForKey( persister );
 		validateNaturalId( persister, naturalIdValues );
 
 		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 
 		Serializable pk;
 		final CachedNaturalId cachedNaturalId = new CachedNaturalId( persister, naturalIdValues );
 		if ( entityNaturalIdResolutionCache != null ) {
 			pk = entityNaturalIdResolutionCache.naturalIdToPkMap.get( cachedNaturalId );
 
 			// Found in session cache
 			if ( pk != null ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.trace(
 							"Resolved natural key -> primary key resolution in session cache: " +
 									persister.getRootEntityName() + "#[" +
 									Arrays.toString( naturalIdValues ) + "]"
 					);
 				}
 
 				return pk;
 			}
 
 			// if we did not find a hit, see if we know about these natural ids as invalid...
 			if ( entityNaturalIdResolutionCache.containsInvalidNaturalIdReference( naturalIdValues ) ) {
 				return PersistenceContext.NaturalIdHelper.INVALID_NATURAL_ID_REFERENCE;
 			}
 		}
 
 		// Session cache miss, see if second-level caching is enabled
 		if ( !persister.hasNaturalIdCache() ) {
 			return null;
 		}
 
 		// Try resolution from second-level cache
 		final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
-		final NaturalIdCacheKey naturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey( naturalIdValues, persister, session() );
+		final Object naturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey( naturalIdValues, persister, session() );
 
 		pk = CacheHelper.fromSharedCache( session(), naturalIdCacheKey, naturalIdCacheAccessStrategy );
 
 		// Found in second-level cache, store in session cache
 		final SessionFactoryImplementor factory = session().getFactory();
 		if ( pk != null ) {
 			if ( factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor().naturalIdCacheHit(
 						naturalIdCacheAccessStrategy.getRegion().getName()
 				);
 			}
 
 			if ( LOG.isTraceEnabled() ) {
 				// protected to avoid Arrays.toString call unless needed
 				LOG.tracef(
 						"Found natural key [%s] -> primary key [%s] xref in second-level cache for %s",
 						Arrays.toString( naturalIdValues ),
 						pk,
 						persister.getRootEntityName()
 				);
 			}
 
 			if ( entityNaturalIdResolutionCache == null ) {
 				entityNaturalIdResolutionCache = new NaturalIdResolutionCache( persister );
 				NaturalIdResolutionCache existingCache = naturalIdResolutionCacheMap.putIfAbsent( persister, entityNaturalIdResolutionCache );
 				if ( existingCache != null ) {
 					entityNaturalIdResolutionCache = existingCache;
 				}
 			}
 
 			entityNaturalIdResolutionCache.pkToNaturalIdMap.put( pk, cachedNaturalId );
 			entityNaturalIdResolutionCache.naturalIdToPkMap.put( cachedNaturalId, pk );
 		}
 		else if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().naturalIdCacheMiss( naturalIdCacheAccessStrategy.getRegion().getName() );
 		}
 
 		return pk;
 	}
 
 	/**
 	 * Return all locally cross-referenced primary keys for the given persister.  Used as part of load
 	 * synchronization process.
 	 *
 	 * @param persister The persister representing the entity type.
 	 * 
 	 * @return The primary keys
 	 * 
 	 * @see org.hibernate.NaturalIdLoadAccess#setSynchronizationEnabled
 	 */
 	public Collection<Serializable> getCachedPkResolutions(EntityPersister persister) {
 		persister = locatePersisterForKey( persister );
 
 		Collection<Serializable> pks = null;
 
 		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		if ( entityNaturalIdResolutionCache != null ) {
 			pks = entityNaturalIdResolutionCache.pkToNaturalIdMap.keySet();
 		}
 
 		if ( pks == null || pks.isEmpty() ) {
 			return java.util.Collections.emptyList();
 		}
 		else {
 			return java.util.Collections.unmodifiableCollection( pks );
 		}
 	}
 
 	/**
 	 * As part of "load synchronization process", if a particular natural id is found to have changed we need to track
 	 * its invalidity until after the next flush.  This method lets the "load synchronization process" indicate
 	 * when it has encountered such changes.
 	 *
 	 * @param persister The persister representing the entity type.
 	 * @param invalidNaturalIdValues The "old" natural id values.
 	 *
 	 * @see org.hibernate.NaturalIdLoadAccess#setSynchronizationEnabled
 	 */
 	public void stashInvalidNaturalIdReference(EntityPersister persister, Object[] invalidNaturalIdValues) {
 		persister = locatePersisterForKey( persister );
 
 		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		if ( entityNaturalIdResolutionCache == null ) {
 			throw new AssertionFailure( "Expecting NaturalIdResolutionCache to exist already for entity " + persister.getEntityName() );
 		}
 
 		entityNaturalIdResolutionCache.stashInvalidNaturalIdReference( invalidNaturalIdValues );
 	}
 
 	/**
 	 * Again, as part of "load synchronization process" we need to also be able to clear references to these
 	 * known-invalid natural-ids after flush.  This method exposes that capability.
 	 */
 	public void unStashInvalidNaturalIdReferences() {
 		for ( NaturalIdResolutionCache naturalIdResolutionCache : naturalIdResolutionCacheMap.values() ) {
 			naturalIdResolutionCache.unStashInvalidNaturalIdReferences();
 		}
 	}
 
 	/**
 	 * Used to put natural id values into collections.  Useful mainly to apply equals/hashCode implementations.
 	 */
 	private static class CachedNaturalId implements Serializable {
 		private final EntityPersister persister;
 		private final Object[] values;
 		private final Type[] naturalIdTypes;
 		private int hashCode;
 
 		public CachedNaturalId(EntityPersister persister, Object[] values) {
 			this.persister = persister;
 			this.values = values;
 
 			final int prime = 31;
 			int hashCodeCalculation = 1;
 			hashCodeCalculation = prime * hashCodeCalculation + persister.hashCode();
 
 			final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 			naturalIdTypes = new Type[ naturalIdPropertyIndexes.length ];
 			int i = 0;
 			for ( int naturalIdPropertyIndex : naturalIdPropertyIndexes ) {
 				final Type type = persister.getPropertyType( persister.getPropertyNames()[ naturalIdPropertyIndex ] );
 				naturalIdTypes[i] = type;
 				final int elementHashCode = values[i] == null ? 0 :type.getHashCode( values[i], persister.getFactory() );
 				hashCodeCalculation = prime * hashCodeCalculation + elementHashCode;
 				i++;
 			}
 
 			this.hashCode = hashCodeCalculation;
 		}
 
 		public Object[] getValues() {
 			return values;
 		}
 
 		@Override
 		public int hashCode() {
 			return this.hashCode;
 		}
 
 		@Override
 		public boolean equals(Object obj) {
 			if ( this == obj ) {
 				return true;
 			}
 			if ( obj == null ) {
 				return false;
 			}
 			if ( getClass() != obj.getClass() ) {
 				return false;
 			}
 
 			final CachedNaturalId other = (CachedNaturalId) obj;
 			return persister.equals( other.persister ) && isSame( other.values );
 		}
 
 		private boolean isSame(Object[] otherValues) {
 			// lengths have already been verified at this point
 			for ( int i = 0; i < naturalIdTypes.length; i++ ) {
 				if ( ! naturalIdTypes[i].isEqual( values[i], otherValues[i], persister.getFactory() ) ) {
 					return false;
 				}
 			}
 			return true;
 		}
 	}
 
 	/**
 	 * Represents the persister-specific cross-reference cache.
 	 */
 	private static class NaturalIdResolutionCache implements Serializable {
 		private final EntityPersister persister;
 		private final Type[] naturalIdTypes;
 
 		private Map<Serializable, CachedNaturalId> pkToNaturalIdMap = new ConcurrentHashMap<Serializable, CachedNaturalId>();
 		private Map<CachedNaturalId, Serializable> naturalIdToPkMap = new ConcurrentHashMap<CachedNaturalId, Serializable>();
 
 		private List<CachedNaturalId> invalidNaturalIdList;
 
 		private NaturalIdResolutionCache(EntityPersister persister) {
 			this.persister = persister;
 
 			final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 			naturalIdTypes = new Type[ naturalIdPropertyIndexes.length ];
 			int i = 0;
 			for ( int naturalIdPropertyIndex : naturalIdPropertyIndexes ) {
 				naturalIdTypes[i++] = persister.getPropertyType( persister.getPropertyNames()[ naturalIdPropertyIndex ] );
 			}
 		}
 
 		public EntityPersister getPersister() {
 			return persister;
 		}
 
 		public boolean sameAsCached(Serializable pk, Object[] naturalIdValues) {
 			if ( pk == null ) {
 				return false;
 			}
 			final CachedNaturalId initial = pkToNaturalIdMap.get( pk );
 			if ( initial != null ) {
 				if ( initial.isSame( naturalIdValues ) ) {
 					return true;
 				}
 			}
 			return false;
 		}
 
 		public boolean cache(Serializable pk, Object[] naturalIdValues) {
 			if ( pk == null ) {
 				return false;
 			}
 			final CachedNaturalId initial = pkToNaturalIdMap.get( pk );
 			if ( initial != null ) {
 				if ( initial.isSame( naturalIdValues ) ) {
 					return false;
 				}
 				naturalIdToPkMap.remove( initial );
 			}
 
 			final CachedNaturalId cachedNaturalId = new CachedNaturalId( persister, naturalIdValues );
 			pkToNaturalIdMap.put( pk, cachedNaturalId );
 			naturalIdToPkMap.put( cachedNaturalId, pk );
 			
 			return true;
 		}
 
 		public void stashInvalidNaturalIdReference(Object[] invalidNaturalIdValues) {
 			if ( invalidNaturalIdList == null ) {
 				invalidNaturalIdList = new ArrayList<CachedNaturalId>();
 			}
 			invalidNaturalIdList.add( new CachedNaturalId( persister, invalidNaturalIdValues ) );
 		}
 
 		public boolean containsInvalidNaturalIdReference(Object[] naturalIdValues) {
 			return invalidNaturalIdList != null
 					&& invalidNaturalIdList.contains( new CachedNaturalId( persister, naturalIdValues ) );
 		}
 
 		public void unStashInvalidNaturalIdReferences() {
 			if ( invalidNaturalIdList != null ) {
 				invalidNaturalIdList.clear();
 			}
 		}
 	}
 
 	/**
 	 * Clear the resolution cache
 	 */
 	public void clear() {
 		naturalIdResolutionCacheMap.clear();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
index b6b1d8cec6..becb76f8b4 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
@@ -1,1997 +1,1996 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.internal;
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Map.Entry;
 import java.util.concurrent.ConcurrentMap;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.loading.internal.LoadContexts;
 import org.hibernate.engine.spi.AssociationKey;
 import org.hibernate.engine.spi.BatchFetchQueue;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.ConcurrentReferenceHashMap;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.tuple.ElementWrapper;
 import org.hibernate.type.CollectionType;
 
 import org.jboss.logging.Logger;
 
 /**
  * A <strong>stateful</strong> implementation of the {@link PersistenceContext} contract meaning that we maintain this
  * state throughout the life of the persistence context.
  * <p/>
  * IMPL NOTE: There is meant to be a one-to-one correspondence between a {@link org.hibernate.internal.SessionImpl}
  * and a PersistentContext.  Event listeners and other Session collaborators then use the PersistentContext to drive
  * their processing.
  *
  * @author Steve Ebersole
  */
 public class StatefulPersistenceContext implements PersistenceContext {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			StatefulPersistenceContext.class.getName()
 	);
 
 	private static final boolean TRACE_ENABLED = LOG.isTraceEnabled();
 	private static final int INIT_COLL_SIZE = 8;
 
 	private SessionImplementor session;
 
 	// Loaded entity instances, by EntityKey
 	private Map<EntityKey, Object> entitiesByKey;
 
 	// Loaded entity instances, by EntityUniqueKey
 	private Map<EntityUniqueKey, Object> entitiesByUniqueKey;
 
 	private EntityEntryContext entityEntryContext;
 //	private Map<Object,EntityEntry> entityEntries;
 
 	// Entity proxies, by EntityKey
 	private ConcurrentMap<EntityKey, Object> proxiesByKey;
 
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
 
 	private int cascading;
 	private int loadCounter;
 	private int removeOrphanBeforeUpdatesCounter;
 	private boolean flushing;
 
 	private boolean defaultReadOnly;
 	private boolean hasNonReadOnlyEntities;
 
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
 		proxiesByKey = new ConcurrentReferenceHashMap<EntityKey, Object>( INIT_COLL_SIZE, .75f, 1, ConcurrentReferenceHashMap.ReferenceType.STRONG, ConcurrentReferenceHashMap.ReferenceType.WEAK, null );
 		entitySnapshotsByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
 
 		entityEntryContext = new EntityEntryContext();
 //		entityEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		collectionEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		parentsByChild = new IdentityHashMap<Object,Object>( INIT_COLL_SIZE );
 
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
 			unownedCollections = new HashMap<CollectionKey,PersistentCollection>(INIT_COLL_SIZE);
 		}
 		unownedCollections.put( key, collection );
 	}
 
 	@Override
 	public PersistentCollection useUnownedCollection(CollectionKey key) {
 		return ( unownedCollections == null ) ? null : unownedCollections.remove( key );
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
 			if ( o == null ) {
 				//entry may be GCd
 				continue;
 			}
 			((HibernateProxy) o).getHibernateLazyInitializer().unsetSession();
 		}
 		for ( Map.Entry<PersistentCollection, CollectionEntry> aCollectionEntryArray : IdentityMap.concurrentEntries( collectionEntries ) ) {
 			aCollectionEntryArray.getKey().unsetSession( getSession() );
 		}
 		arrayHolders.clear();
 		entitiesByKey.clear();
 		entitiesByUniqueKey.clear();
 		entityEntryContext.clear();
 //		entityEntries.clear();
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
 		naturalIdXrefDelegate.clear();
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
 		entry.setStatus( status );
 		setHasNonReadOnlyEnties( status );
 	}
 
 	private void setHasNonReadOnlyEnties(Status status) {
 		if ( status==Status.DELETED || status==Status.MANAGED || status==Status.SAVING ) {
 			hasNonReadOnlyEntities = true;
 		}
 	}
 
 	@Override
 	public void afterTransactionCompletion() {
 		cleanUpInsertedKeysAfterTransaction();
 		entityEntryContext.downgradeLocks();
 //		// Downgrade locks
 //		for ( EntityEntry o : entityEntries.values() ) {
 //			o.setLockMode( LockMode.NONE );
 //		}
 	}
 
 	/**
 	 * Get the current state of the entity as known to the underlying
 	 * database, or null if there is no corresponding row
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister) throws HibernateException {
 		final EntityKey key = session.generateEntityKey( id, persister );
 		final Object cached = entitySnapshotsByKey.get( key );
 		if ( cached != null ) {
 			return cached == NO_ROW ? null : (Object[]) cached;
 		}
 		else {
 			final Object[] snapshot = persister.getDatabaseSnapshot( id, session );
 			entitySnapshotsByKey.put( key, snapshot == null ? NO_ROW : snapshot );
 			return snapshot;
 		}
 	}
 
 	@Override
 	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister) throws HibernateException {
 		if ( !persister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		persister = locateProperPersister( persister );
 
 		// let's first see if it is part of the natural id cache...
 		final Object[] cachedValue = naturalIdHelper.findCachedNaturalId( persister, id );
 		if ( cachedValue != null ) {
 			return cachedValue;
 		}
 
 		// check to see if the natural id is mutable/immutable
 		if ( persister.getEntityMetamodel().hasImmutableNaturalId() ) {
 			// an immutable natural-id is not retrieved during a normal database-snapshot operation...
 			final Object[] dbValue = persister.getNaturalIdentifierSnapshot( id, session );
 			naturalIdHelper.cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					id,
 					dbValue
 			);
 			return dbValue;
 		}
 		else {
 			// for a mutable natural there is a likelihood that the the information will already be
 			// snapshot-cached.
 			final int[] props = persister.getNaturalIdentifierProperties();
 			final Object[] entitySnapshot = getDatabaseSnapshot( id, persister );
 			if ( entitySnapshot == NO_ROW || entitySnapshot == null ) {
 				return null;
 			}
 
 			final Object[] naturalIdSnapshotSubSet = new Object[ props.length ];
 			for ( int i = 0; i < props.length; i++ ) {
 				naturalIdSnapshotSubSet[i] = entitySnapshot[ props[i] ];
 			}
 			naturalIdHelper.cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					id,
 					naturalIdSnapshotSubSet
 			);
 			return naturalIdSnapshotSubSet;
 		}
 	}
 
 	private EntityPersister locateProperPersister(EntityPersister persister) {
 		return session.getFactory().getEntityPersister( persister.getRootEntityName() );
 	}
 
 	@Override
 	public Object[] getCachedDatabaseSnapshot(EntityKey key) {
 		final Object snapshot = entitySnapshotsByKey.get( key );
 		if ( snapshot == NO_ROW ) {
 			throw new IllegalStateException(
 					"persistence context reported no row snapshot for "
 							+ MessageHelper.infoString( key.getEntityName(), key.getIdentifier() )
 			);
 		}
 		return (Object[]) snapshot;
 	}
 
 	@Override
 	public void addEntity(EntityKey key, Object entity) {
 		entitiesByKey.put( key, entity );
 		getBatchFetchQueue().removeBatchLoadableEntityKey( key );
 	}
 
 	@Override
 	public Object getEntity(EntityKey key) {
 		return entitiesByKey.get( key );
 	}
 
 	@Override
 	public boolean containsEntity(EntityKey key) {
 		return entitiesByKey.containsKey( key );
 	}
 
 	@Override
 	public Object removeEntity(EntityKey key) {
 		final Object entity = entitiesByKey.remove( key );
 		final Iterator itr = entitiesByUniqueKey.values().iterator();
 		while ( itr.hasNext() ) {
 			if ( itr.next() == entity ) {
 				itr.remove();
 			}
 		}
 		// Clear all parent cache
 		parentsByChild.clear();
 		entitySnapshotsByKey.remove( key );
 		nullifiableEntityKeys.remove( key );
 		getBatchFetchQueue().removeBatchLoadableEntityKey( key );
 		getBatchFetchQueue().removeSubselect( key );
 		return entity;
 	}
 
 	@Override
 	public Object getEntity(EntityUniqueKey euk) {
 		return entitiesByUniqueKey.get( euk );
 	}
 
 	@Override
 	public void addEntity(EntityUniqueKey euk, Object entity) {
 		entitiesByUniqueKey.put( euk, entity );
 	}
 
 	@Override
 	public EntityEntry getEntry(Object entity) {
 		return entityEntryContext.getEntityEntry( entity );
 	}
 
 	@Override
 	public EntityEntry removeEntry(Object entity) {
 		return entityEntryContext.removeEntityEntry( entity );
 	}
 
 	@Override
 	public boolean isEntryFor(Object entity) {
 		return entityEntryContext.hasEntityEntry( entity );
 	}
 
 	@Override
 	public CollectionEntry getCollectionEntry(PersistentCollection coll) {
 		return collectionEntries.get( coll );
 	}
 
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
 
 		final EntityEntryFactory entityEntryFactory = persister.getEntityEntryFactory();
 		final EntityEntry e = entityEntryFactory.createEntityEntry(
 				status,
 				loadedState,
 				rowId,
 				id,
 				version,
 				lockMode,
 				existsInDatabase,
 				persister,
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched,
 				this
 		);
 
 		entityEntryContext.addEntityEntry( entity, e );
 //		entityEntries.put(entity, e);
 
 		setHasNonReadOnlyEnties( status );
 		return e;
 	}
 
 	@Override
 	public boolean containsCollection(PersistentCollection collection) {
 		return collectionEntries.containsKey( collection );
 	}
 
 	@Override
 	public boolean containsProxy(Object entity) {
 		return proxiesByKey.containsValue( entity );
 	}
 
 	@Override
 	public boolean reassociateIfUninitializedProxy(Object value) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
 		if ( !Hibernate.isInitialized( value ) ) {
 			final HibernateProxy proxy = (HibernateProxy) value;
 			final LazyInitializer li = proxy.getHibernateLazyInitializer();
 			reassociateProxy( li, proxy );
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	@Override
 	public void reassociateProxy(Object value, Serializable id) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
 		if ( value instanceof HibernateProxy ) {
 			LOG.debugf( "Setting proxy identifier: %s", id );
 			final HibernateProxy proxy = (HibernateProxy) value;
 			final LazyInitializer li = proxy.getHibernateLazyInitializer();
 			li.setIdentifier( id );
 			reassociateProxy( li, proxy );
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
 			proxiesByKey.putIfAbsent( key, proxy );
 			proxy.getHibernateLazyInitializer().setSession( session );
 		}
 	}
 
 	@Override
 	public Object unproxy(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof ElementWrapper ) {
 			maybeProxy = ( (ElementWrapper) maybeProxy ).getElement();
 		}
 
 		if ( maybeProxy instanceof HibernateProxy ) {
 			final HibernateProxy proxy = (HibernateProxy) maybeProxy;
 			final LazyInitializer li = proxy.getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				throw new PersistentObjectException(
 						"object was an uninitialized proxy for " + li.getEntityName()
 				);
 			}
 			//unwrap the object and return
 			return li.getImplementation();
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
 	@Override
 	public Object unproxyAndReassociate(Object maybeProxy) throws HibernateException {
 		if ( maybeProxy instanceof ElementWrapper ) {
 			maybeProxy = ( (ElementWrapper) maybeProxy ).getElement();
 		}
 
 		if ( maybeProxy instanceof HibernateProxy ) {
 			final HibernateProxy proxy = (HibernateProxy) maybeProxy;
 			final LazyInitializer li = proxy.getHibernateLazyInitializer();
 			reassociateProxy( li, proxy );
 			//initialize + unwrap the object and return it
 			return li.getImplementation();
 		}
 		else {
 			return maybeProxy;
 		}
 	}
 
 	@Override
 	public void checkUniqueness(EntityKey key, Object object) throws HibernateException {
 		final Object entity = getEntity( key );
 		if ( entity == object ) {
 			throw new AssertionFailure( "object already associated, but no entry was found" );
 		}
 		if ( entity != null ) {
 			throw new NonUniqueObjectException( key.getIdentifier(), key.getEntityName() );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object)
 			throws HibernateException {
 
 		final Class concreteProxyClass = persister.getConcreteProxyClass();
 		final boolean alreadyNarrow = concreteProxyClass.isInstance( proxy );
 
 		if ( !alreadyNarrow ) {
 			LOG.narrowingProxy( concreteProxyClass );
 
 			// If an impl is passed, there is really no point in creating a proxy.
 			// It would just be extra processing.  Just return the impl
 			if ( object != null ) {
 				proxiesByKey.remove( key );
 				return object;
 			}
 
 			// Similarly, if the original HibernateProxy is initialized, there
 			// is again no point in creating a proxy.  Just return the impl
 			final HibernateProxy originalHibernateProxy = (HibernateProxy) proxy;
 			if ( !originalHibernateProxy.getHibernateLazyInitializer().isUninitialized() ) {
 				final Object impl = originalHibernateProxy.getHibernateLazyInitializer().getImplementation();
 				// can we return it?
 				if ( concreteProxyClass.isInstance( impl ) ) {
 					proxiesByKey.remove( key );
 					return impl;
 				}
 			}
 
 
 			// Otherwise, create the narrowed proxy
 			final HibernateProxy narrowedProxy = (HibernateProxy) persister.createProxy( key.getIdentifier(), session );
 
 			// set the read-only/modifiable mode in the new proxy to what it was in the original proxy
 			final boolean readOnlyOrig = originalHibernateProxy.getHibernateLazyInitializer().isReadOnly();
 			narrowedProxy.getHibernateLazyInitializer().setReadOnly( readOnlyOrig );
 
 			return narrowedProxy;
 		}
 		else {
 
 			if ( object != null ) {
 				final LazyInitializer li = ( (HibernateProxy) proxy ).getHibernateLazyInitializer();
 				li.setImplementation( object );
 			}
 			return proxy;
 		}
 	}
 
 	@Override
 	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl) throws HibernateException {
 		if ( !persister.hasProxy() ) {
 			return impl;
 		}
 		final Object proxy = proxiesByKey.get( key );
 		return ( proxy != null ) ? narrowProxy( proxy, persister, key, impl ) : impl;
 	}
 
 	@Override
 	public Object proxyFor(Object impl) throws HibernateException {
 		final EntityEntry e = getEntry( impl );
 		if ( e == null ) {
 			return impl;
 		}
 		return proxyFor( e.getPersister(), e.getEntityKey(), impl );
 	}
 
 	@Override
 	public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister) throws MappingException {
 		// todo : we really just need to add a split in the notions of:
 		//		1) collection key
 		//		2) collection owner key
 		// these 2 are not always the same.  Same is true in the case of ToOne associations with property-ref...
 		final EntityPersister ownerPersister = collectionPersister.getOwnerEntityPersister();
 		if ( ownerPersister.getIdentifierType().getReturnedClass().isInstance( key ) ) {
 			return getEntity( session.generateEntityKey( key, collectionPersister.getOwnerEntityPersister() ) );
 		}
 
 		// we have a property-ref type mapping for the collection key.  But that could show up a few ways here...
 		//
 		//		1) The incoming key could be the entity itself...
 		if ( ownerPersister.isInstance( key ) ) {
 			final Serializable owenerId = ownerPersister.getIdentifier( key, session );
 			if ( owenerId == null ) {
 				return null;
 			}
 			return getEntity( session.generateEntityKey( owenerId, ownerPersister ) );
 		}
 
 		final CollectionType collectionType = collectionPersister.getCollectionType();
 
 		//		2) The incoming key is most likely the collection key which we need to resolve to the owner key
 		//			find the corresponding owner instance
 		//			a) try by EntityUniqueKey
 		if ( collectionType.getLHSPropertyName() != null ) {
 			final Object owner = getEntity(
 					new EntityUniqueKey(
 							ownerPersister.getEntityName(),
 							collectionType.getLHSPropertyName(),
 							key,
 							collectionPersister.getKeyType(),
 							ownerPersister.getEntityMode(),
 							session.getFactory()
 					)
 			);
 			if ( owner != null ) {
 				return owner;
 			}
 
 			//		b) try by EntityKey, which means we need to resolve owner-key -> collection-key
 			//			IMPL NOTE : yes if we get here this impl is very non-performant, but PersistenceContext
 			//					was never designed to handle this case; adding that capability for real means splitting
 			//					the notions of:
 			//						1) collection key
 			//						2) collection owner key
 			// 					these 2 are not always the same (same is true in the case of ToOne associations with
 			// 					property-ref).  That would require changes to (at least) CollectionEntry and quite
 			//					probably changes to how the sql for collection initializers are generated
 			//
 			//			We could also possibly see if the referenced property is a natural id since we already have caching
 			//			in place of natural id snapshots.  BUt really its better to just do it the right way ^^ if we start
 			// 			going that route
 			final Serializable ownerId = ownerPersister.getIdByUniqueKey( key, collectionType.getLHSPropertyName(), session );
 			return getEntity( session.generateEntityKey( ownerId, ownerPersister ) );
 		}
 
 		// as a last resort this is what the old code did...
 		return getEntity( session.generateEntityKey( key, collectionPersister.getOwnerEntityPersister() ) );
 	}
 
 	@Override
 	public Object getLoadedCollectionOwnerOrNull(PersistentCollection collection) {
 		final CollectionEntry ce = getCollectionEntry( collection );
 		if ( ce.getLoadedPersister() == null ) {
 			return null;
 		}
 
 		Object loadedOwner = null;
 		// TODO: an alternative is to check if the owner has changed; if it hasn't then
 		// return collection.getOwner()
 		final Serializable entityId = getLoadedCollectionOwnerIdOrNull( ce );
 		if ( entityId != null ) {
 			loadedOwner = getCollectionOwner( entityId, ce.getLoadedPersister() );
 		}
 		return loadedOwner;
 	}
 
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
 
 	@Override
 	public void addUninitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) {
 		final CollectionEntry ce = new CollectionEntry( collection, persister, id, flushing );
 		addCollection( collection, ce, id );
 		if ( persister.getBatchSize() > 1 ) {
 			getBatchFetchQueue().addBatchLoadableCollection( collection, ce );
 		}
 	}
 
 	@Override
 	public void addUninitializedDetachedCollection(CollectionPersister persister, PersistentCollection collection) {
 		final CollectionEntry ce = new CollectionEntry( persister, collection.getKey() );
 		addCollection( collection, ce, collection.getKey() );
 		if ( persister.getBatchSize() > 1 ) {
 			getBatchFetchQueue().addBatchLoadableCollection( collection, ce );
 		}
 	}
 
 	@Override
 	public void addNewCollection(CollectionPersister persister, PersistentCollection collection)
 			throws HibernateException {
 		addCollection( collection, persister );
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
 		final CollectionKey collectionKey = new CollectionKey( entry.getLoadedPersister(), key );
 		final PersistentCollection old = collectionsByKey.put( collectionKey, coll );
 		if ( old != null ) {
 			if ( old == coll ) {
 				throw new AssertionFailure( "bug adding collection twice" );
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
 		final CollectionEntry ce = new CollectionEntry( persister, collection );
 		collectionEntries.put( collection, ce );
 	}
 
 	@Override
 	public void addInitializedDetachedCollection(CollectionPersister collectionPersister, PersistentCollection collection)
 			throws HibernateException {
 		if ( collection.isUnreferenced() ) {
 			//treat it just like a new collection
 			addCollection( collection, collectionPersister );
 		}
 		else {
 			final CollectionEntry ce = new CollectionEntry( collection, session.getFactory() );
 			addCollection( collection, ce, collection.getKey() );
 		}
 	}
 
 	@Override
 	public CollectionEntry addInitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id)
 			throws HibernateException {
 		final CollectionEntry ce = new CollectionEntry( collection, persister, id, flushing );
 		ce.postInitialize( collection );
 		addCollection( collection, ce, id );
 		return ce;
 	}
 
 	@Override
 	public PersistentCollection getCollection(CollectionKey collectionKey) {
 		return collectionsByKey.get( collectionKey );
 	}
 
 	@Override
 	public void addNonLazyCollection(PersistentCollection collection) {
 		nonlazyCollections.add( collection );
 	}
 
 	@Override
 	public void initializeNonLazyCollections() throws HibernateException {
 		if ( loadCounter == 0 ) {
 			if ( TRACE_ENABLED ) {
 				LOG.trace( "Initializing non-lazy collections" );
 			}
 
 			//do this work only at the very highest level of the load
 			//don't let this method be called recursively
 			loadCounter++;
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
 
 	@Override
 	public PersistentCollection getCollectionHolder(Object array) {
 		return arrayHolders.get( array );
 	}
 
 	@Override
 	public void addCollectionHolder(PersistentCollection holder) {
 		//TODO:refactor + make this method private
 		arrayHolders.put( holder.getValue(), holder );
 	}
 
 	@Override
 	public PersistentCollection removeCollectionHolder(Object array) {
 		return arrayHolders.remove( array );
 	}
 
 	@Override
 	public Serializable getSnapshot(PersistentCollection coll) {
 		return getCollectionEntry( coll ).getSnapshot();
 	}
 
 	@Override
 	public CollectionEntry getCollectionEntryOrNull(Object collection) {
 		PersistentCollection coll;
 		if ( collection instanceof PersistentCollection ) {
 			coll = (PersistentCollection) collection;
 			//if (collection==null) throw new TransientObjectException("Collection was not yet persistent");
 		}
 		else {
 			coll = getCollectionHolder( collection );
 			if ( coll == null ) {
 				//it might be an unwrapped collection reference!
 				//try to find a wrapper (slowish)
 				final Iterator<PersistentCollection> wrappers = collectionEntries.keyIterator();
 				while ( wrappers.hasNext() ) {
 					final PersistentCollection pc = wrappers.next();
 					if ( pc.isWrapper( collection ) ) {
 						coll = pc;
 						break;
 					}
 				}
 			}
 		}
 
 		return (coll == null) ? null : getCollectionEntry( coll );
 	}
 
 	@Override
 	public Object getProxy(EntityKey key) {
 		return proxiesByKey.get( key );
 	}
 
 	@Override
 	public void addProxy(EntityKey key, Object proxy) {
 		proxiesByKey.put( key, proxy );
 	}
 
 	@Override
 	public Object removeProxy(EntityKey key) {
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.removeBatchLoadableEntityKey( key );
 			batchFetchQueue.removeSubselect( key );
 		}
 		return proxiesByKey.remove( key );
 	}
 
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
 	public int getNumberOfManagedEntities() {
 		return entityEntryContext.getNumberOfManagedEntities();
 	}
 
 	@Override
 	public Map getEntityEntries() {
 		return null;
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
 		final boolean afterFlush = this.flushing && ! flushing;
 		this.flushing = flushing;
 		if ( afterFlush ) {
 			getNaturalIdHelper().cleanupFromSynchronizations();
 		}
 	}
 
 	public boolean isRemovingOrphanBeforeUpates() {
 		return removeOrphanBeforeUpdatesCounter > 0;
 	}
 
 	public void beginRemoveOrphanBeforeUpdates() {
 		if ( getCascadeLevel() < 1 ) {
 			throw new IllegalStateException( "Attempt to remove orphan when not cascading." );
 		}
 		if ( removeOrphanBeforeUpdatesCounter >= getCascadeLevel() ) {
 			throw new IllegalStateException(
 					String.format(
 							"Cascade level [%d] is out of sync with removeOrphanBeforeUpdatesCounter [%d] before incrementing removeOrphanBeforeUpdatesCounter",
 							getCascadeLevel(),
 							removeOrphanBeforeUpdatesCounter
 					)
 			);
 		}
 		removeOrphanBeforeUpdatesCounter++;
 	}
 
 	public void endRemoveOrphanBeforeUpdates() {
 		if ( getCascadeLevel() < 1 ) {
 			throw new IllegalStateException( "Finished removing orphan when not cascading." );
 		}
 		if ( removeOrphanBeforeUpdatesCounter > getCascadeLevel() ) {
 			throw new IllegalStateException(
 					String.format(
 							"Cascade level [%d] is out of sync with removeOrphanBeforeUpdatesCounter [%d] before decrementing removeOrphanBeforeUpdatesCounter",
 							getCascadeLevel(),
 							removeOrphanBeforeUpdatesCounter
 					)
 			);
 		}
 		removeOrphanBeforeUpdatesCounter--;
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
 
 	@Override
 	public String toString() {
 		return "PersistenceContext[entityKeys=" + entitiesByKey.keySet()
 				+ ",collectionKeys=" + collectionsByKey.keySet() + "]";
 	}
 
 	@Override
 	public Entry<Object,EntityEntry>[] reentrantSafeEntityEntries() {
 		return entityEntryContext.reentrantSafeEntityEntries();
 	}
 
 	@Override
 	public Serializable getOwnerId(String entityName, String propertyName, Object childEntity, Map mergeMap) {
 		final String collectionRole = entityName + '.' + propertyName;
 		final EntityPersister persister = session.getFactory().getEntityPersister( entityName );
 		final CollectionPersister collectionPersister = session.getFactory().getCollectionPersister( collectionRole );
 
 	    // try cache lookup first
 		final Object parent = parentsByChild.get( childEntity );
 		if ( parent != null ) {
 			final EntityEntry entityEntry = entityEntryContext.getEntityEntry( parent );
 			//there maybe more than one parent, filter by type
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() )
 					&& isFoundInParent( propertyName, childEntity, persister, collectionPersister, parent ) ) {
 				return getEntry( parent ).getId();
 			}
 			else {
 				// remove wrong entry
 				parentsByChild.remove( childEntity );
 			}
 		}
 
 		//not found in case, proceed
 		// iterate all the entities currently associated with the persistence context.
 		for ( Entry<Object,EntityEntry> me : reentrantSafeEntityEntries() ) {
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
 					final Object unmergedInstance = mergeMap.get( entityEntryInstance );
 					final Object unmergedChild = mergeMap.get( childEntity );
 					if ( unmergedInstance != null && unmergedChild != null ) {
 						found = isFoundInParent(
 								propertyName,
 								unmergedChild,
 								persister,
 								collectionPersister,
 								unmergedInstance
 						);
 						LOG.debugf(
 								"Detached object being merged (corresponding with a managed entity) has a collection that [%s] the detached child.",
 								( found ? "contains" : "does not contain" )
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
 						LOG.debugf(
 								"Detached proxy being merged has a collection that [%s] the managed child.",
 								(found ? "contains" : "does not contain")
 						);
 						if ( !found ) {
 							found = isFoundInParent(
 									propertyName,
 									mergeMap.get( childEntity ),
 									persister,
 									collectionPersister,
 									mergeMap.get( proxy )
 							);
 							LOG.debugf(
 									"Detached proxy being merged has a collection that [%s] the detached child being merged..",
 									(found ? "contains" : "does not contain")
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
 		final Object collection = persister.getPropertyValue( potentialParent, property );
 		return collection != null
 				&& Hibernate.isInitialized( collection )
 				&& collectionPersister.getCollectionType().contains( collection, childEntity, session );
 	}
 
 	@Override
 	public Object getIndexInOwner(String entity, String property, Object childEntity, Map mergeMap) {
 		final EntityPersister persister = session.getFactory().getEntityPersister( entity );
 		final CollectionPersister cp = session.getFactory().getCollectionPersister( entity + '.' + property );
 
 	    // try cache lookup first
 		final Object parent = parentsByChild.get( childEntity );
 		if ( parent != null ) {
 			final EntityEntry entityEntry = entityEntryContext.getEntityEntry( parent );
 			//there maybe more than one parent, filter by type
 			if ( persister.isSubclassEntityName( entityEntry.getEntityName() ) ) {
 				Object index = getIndexInParent( property, childEntity, persister, cp, parent );
 
 				if (index==null && mergeMap!=null) {
 					final Object unMergedInstance = mergeMap.get( parent );
 					final Object unMergedChild = mergeMap.get( childEntity );
 					if ( unMergedInstance != null && unMergedChild != null ) {
 						index = getIndexInParent( property, unMergedChild, persister, cp, unMergedInstance );
 						LOG.debugf(
 								"A detached object being merged (corresponding to a parent in parentsByChild) has an indexed collection that [%s] the detached child being merged. ",
 								( index != null ? "contains" : "does not contain" )
 						);
 					}
 				}
 				if ( index != null ) {
 					return index;
 				}
 			}
 			else {
 				// remove wrong entry
 				parentsByChild.remove( childEntity );
 			}
 		}
 
 		//Not found in cache, proceed
 		for ( Entry<Object, EntityEntry> me : reentrantSafeEntityEntries() ) {
 			final EntityEntry ee = me.getValue();
 			if ( persister.isSubclassEntityName( ee.getEntityName() ) ) {
 				final Object instance = me.getKey();
 
 				Object index = getIndexInParent( property, childEntity, persister, cp, instance );
 				if ( index==null && mergeMap!=null ) {
 					final Object unMergedInstance = mergeMap.get( instance );
 					final Object unMergedChild = mergeMap.get( childEntity );
 					if ( unMergedInstance != null && unMergedChild!=null ) {
 						index = getIndexInParent( property, unMergedChild, persister, cp, unMergedInstance );
 						LOG.debugf(
 								"A detached object being merged (corresponding to a managed entity) has an indexed collection that [%s] the detached child being merged. ",
 								(index != null ? "contains" : "does not contain" )
 						);
 					}
 				}
 
 				if ( index != null ) {
 					return index;
 				}
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
 		final Object collection = persister.getPropertyValue( potentialParent, property );
 		if ( collection != null && Hibernate.isInitialized( collection ) ) {
 			return collectionPersister.getCollectionType().indexOf( collection, childEntity );
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
 	public void addNullProperty(EntityKey ownerKey, String propertyName) {
 		nullAssociations.add( new AssociationKey( ownerKey, propertyName ) );
 	}
 
 	@Override
 	public boolean isPropertyNull(EntityKey ownerKey, String propertyName) {
 		return nullAssociations.contains( new AssociationKey( ownerKey, propertyName ) );
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
 			isReadOnly = ( (HibernateProxy) entityOrProxy ).getHibernateLazyInitializer().isReadOnly();
 		}
 		else {
 			final EntityEntry ee =  getEntry( entityOrProxy );
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
 			final HibernateProxy proxy = (HibernateProxy) object;
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
 			final Object maybeProxy = getSession().getPersistenceContext().proxyFor( object );
 			if ( maybeProxy instanceof HibernateProxy ) {
 				setProxyReadOnly( (HibernateProxy) maybeProxy, readOnly );
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
 		final EntityEntry entry = getEntry( entity );
 		if ( entry == null ) {
 			throw new TransientObjectException( "Instance was not associated with this persistence context" );
 		}
 		entry.setReadOnly( readOnly, entity );
 		hasNonReadOnlyEntities = hasNonReadOnlyEntities || ! readOnly;
 	}
 
 	@Override
 	public void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId) {
 		final Object entity = entitiesByKey.remove( oldKey );
 		final EntityEntry oldEntry = entityEntryContext.removeEntityEntry( entity );
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
 		if ( tracing ) {
 			LOG.trace( "Serializing persisatence-context" );
 		}
 
 		oos.writeBoolean( defaultReadOnly );
 		oos.writeBoolean( hasNonReadOnlyEntities );
 
 		oos.writeInt( entitiesByKey.size() );
 		if ( tracing ) {
 			LOG.trace( "Starting serialization of [" + entitiesByKey.size() + "] entitiesByKey entries" );
 		}
 		for ( Map.Entry<EntityKey,Object> entry : entitiesByKey.entrySet() ) {
 			entry.getKey().serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitiesByUniqueKey.size() );
 		if ( tracing ) {
 			LOG.trace( "Starting serialization of [" + entitiesByUniqueKey.size() + "] entitiesByUniqueKey entries" );
 		}
 		for ( Map.Entry<EntityUniqueKey,Object> entry : entitiesByUniqueKey.entrySet() ) {
 			entry.getKey().serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( proxiesByKey.size() );
 		if ( tracing ) {
 			LOG.trace( "Starting serialization of [" + proxiesByKey.size() + "] proxiesByKey entries" );
 		}
 		for ( Map.Entry<EntityKey,Object> entry : proxiesByKey.entrySet() ) {
 			entry.getKey().serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitySnapshotsByKey.size() );
 		if ( tracing ) {
 			LOG.trace( "Starting serialization of [" + entitySnapshotsByKey.size() + "] entitySnapshotsByKey entries" );
 		}
 		for ( Map.Entry<EntityKey,Object> entry : entitySnapshotsByKey.entrySet() ) {
 			entry.getKey().serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		entityEntryContext.serialize( oos );
 
 		oos.writeInt( collectionsByKey.size() );
 		if ( tracing ) {
 			LOG.trace( "Starting serialization of [" + collectionsByKey.size() + "] collectionsByKey entries" );
 		}
 		for ( Map.Entry<CollectionKey,PersistentCollection> entry : collectionsByKey.entrySet() ) {
 			entry.getKey().serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( collectionEntries.size() );
 		if ( tracing ) {
 			LOG.trace( "Starting serialization of [" + collectionEntries.size() + "] collectionEntries entries" );
 		}
 		for ( Map.Entry<PersistentCollection,CollectionEntry> entry : collectionEntries.entrySet() ) {
 			oos.writeObject( entry.getKey() );
 			entry.getValue().serialize( oos );
 		}
 
 		oos.writeInt( arrayHolders.size() );
 		if ( tracing ) {
 			LOG.trace( "Starting serialization of [" + arrayHolders.size() + "] arrayHolders entries" );
 		}
 		for ( Map.Entry<Object,PersistentCollection> entry : arrayHolders.entrySet() ) {
 			oos.writeObject( entry.getKey() );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( nullifiableEntityKeys.size() );
 		if ( tracing ) {
 			LOG.trace( "Starting serialization of [" + nullifiableEntityKeys.size() + "] nullifiableEntityKey entries" );
 		}
 		for ( EntityKey entry : nullifiableEntityKeys ) {
 			entry.serialize( oos );
 		}
 	}
 
 	/**
 	 * Used by the owning session to explicitly control deserialization of the persistence context.
 	 *
 	 * @param ois The stream from which the persistence context should be read
 	 * @param session The owning session
 	 *
 	 * @return The deserialized StatefulPersistenceContext
 	 *
 	 * @throws IOException deserialization errors.
 	 * @throws ClassNotFoundException deserialization errors.
 	 */
 	public static StatefulPersistenceContext deserialize(
 			ObjectInputStream ois,
 			SessionImplementor session) throws IOException, ClassNotFoundException {
 		final boolean tracing = LOG.isTraceEnabled();
 		if ( tracing ) {
 			LOG.trace( "Serializing persistent-context" );
 		}
 		final StatefulPersistenceContext rtn = new StatefulPersistenceContext( session );
 		SessionFactoryImplementor sfi = session.getFactory();
 
 		// during deserialization, we need to reconnect all proxies and
 		// collections to this session, as well as the EntityEntry and
 		// CollectionEntry instances; these associations are transient
 		// because serialization is used for different things.
 
 		try {
 			rtn.defaultReadOnly = ois.readBoolean();
 			// todo : we can actually just determine this from the incoming EntityEntry-s
 			rtn.hasNonReadOnlyEntities = ois.readBoolean();
 
 			int count = ois.readInt();
 			if ( tracing ) {
 				LOG.trace( "Starting deserialization of [" + count + "] entitiesByKey entries" );
 			}
 			rtn.entitiesByKey = new HashMap<EntityKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByKey.put( EntityKey.deserialize( ois, sfi ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) {
 				LOG.trace( "Starting deserialization of [" + count + "] entitiesByUniqueKey entries" );
 			}
 			rtn.entitiesByUniqueKey = new HashMap<EntityUniqueKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByUniqueKey.put( EntityUniqueKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) {
 				LOG.trace( "Starting deserialization of [" + count + "] proxiesByKey entries" );
 			}
 			//noinspection unchecked
 			rtn.proxiesByKey = new ConcurrentReferenceHashMap<EntityKey, Object>(
 					count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count,
 					.75f,
 					1,
 					ConcurrentReferenceHashMap.ReferenceType.STRONG,
 					ConcurrentReferenceHashMap.ReferenceType.WEAK,
 					null
 			);
 			for ( int i = 0; i < count; i++ ) {
 				final EntityKey ek = EntityKey.deserialize( ois, sfi );
 				final Object proxy = ois.readObject();
 				if ( proxy instanceof HibernateProxy ) {
 					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setSession( session );
 					rtn.proxiesByKey.put( ek, proxy );
 				}
 				else {
 					// otherwise, the proxy was pruned during the serialization process
 					if ( tracing ) {
 						LOG.trace( "Encountered pruned proxy" );
 					}
 				}
 			}
 
 			count = ois.readInt();
 			if ( tracing ) {
 				LOG.trace( "Starting deserialization of [" + count + "] entitySnapshotsByKey entries" );
 			}
 			rtn.entitySnapshotsByKey = new HashMap<EntityKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitySnapshotsByKey.put( EntityKey.deserialize( ois, sfi ), ois.readObject() );
 			}
 
 			rtn.entityEntryContext = EntityEntryContext.deserialize( ois, rtn );
 
 			count = ois.readInt();
 			if ( tracing ) {
 				LOG.trace( "Starting deserialization of [" + count + "] collectionsByKey entries" );
 			}
 			rtn.collectionsByKey = new HashMap<CollectionKey,PersistentCollection>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.collectionsByKey.put( CollectionKey.deserialize( ois, session ), (PersistentCollection) ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) {
 				LOG.trace( "Starting deserialization of [" + count + "] collectionEntries entries" );
 			}
 			rtn.collectionEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				final PersistentCollection pc = (PersistentCollection) ois.readObject();
 				final CollectionEntry ce = CollectionEntry.deserialize( ois, session );
 				pc.setCurrentSession( session );
 				rtn.collectionEntries.put( pc, ce );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) {
 				LOG.trace( "Starting deserialization of [" + count + "] arrayHolders entries" );
 			}
 			rtn.arrayHolders = new IdentityHashMap<Object, PersistentCollection>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.arrayHolders.put( ois.readObject(), (PersistentCollection) ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) {
 				LOG.trace( "Starting deserialization of [" + count + "] nullifiableEntityKey entries" );
 			}
 			rtn.nullifiableEntityKeys = new HashSet<EntityKey>();
 			for ( int i = 0; i < count; i++ ) {
 				rtn.nullifiableEntityKeys.add( EntityKey.deserialize( ois, sfi ) );
 			}
 
 		}
 		catch ( HibernateException he ) {
 			throw new InvalidObjectException( he.getMessage() );
 		}
 
 		return rtn;
 	}
 
 	@Override
 	public void addChildParent(Object child, Object parent) {
 		parentsByChild.put( child, parent );
 	}
 
 	@Override
 	public void removeChildParent(Object child) {
 		parentsByChild.remove( child );
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
 				final List<Serializable> insertedEntityIds = insertedKeysMap.get( persister.getRootEntityName() );
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
 
 	private final NaturalIdXrefDelegate naturalIdXrefDelegate = new NaturalIdXrefDelegate( this );
 
 	private final NaturalIdHelper naturalIdHelper = new NaturalIdHelper() {
 		@Override
 		public void cacheNaturalIdCrossReferenceFromLoad(
 				EntityPersister persister,
 				Serializable id,
 				Object[] naturalIdValues) {
 			if ( !persister.hasNaturalIdentifier() ) {
 				// nothing to do
 				return;
 			}
 
 			persister = locateProperPersister( persister );
 
 			// 'justAddedLocally' is meant to handle the case where we would get double stats jounaling
 			//	from a single load event.  The first put journal would come from the natural id resolution;
 			// the second comes from the entity loading.  In this condition, we want to avoid the multiple
 			// 'put' stats incrementing.
 			final boolean justAddedLocally = naturalIdXrefDelegate.cacheNaturalIdCrossReference( persister, id, naturalIdValues );
 
 			if ( justAddedLocally && persister.hasNaturalIdCache() ) {
 				managedSharedCacheEntries( persister, id, naturalIdValues, null, CachedNaturalIdValueSource.LOAD );
 			}
 		}
 
 		@Override
 		public void manageLocalNaturalIdCrossReference(
 				EntityPersister persister,
 				Serializable id,
 				Object[] state,
 				Object[] previousState,
 				CachedNaturalIdValueSource source) {
 			if ( !persister.hasNaturalIdentifier() ) {
 				// nothing to do
 				return;
 			}
 
 			persister = locateProperPersister( persister );
 			final Object[] naturalIdValues = extractNaturalIdValues( state, persister );
 
 			// cache
 			naturalIdXrefDelegate.cacheNaturalIdCrossReference( persister, id, naturalIdValues );
 		}
 
 		@Override
 		public void manageSharedNaturalIdCrossReference(
 				EntityPersister persister,
 				final Serializable id,
 				Object[] state,
 				Object[] previousState,
 				CachedNaturalIdValueSource source) {
 			if ( !persister.hasNaturalIdentifier() ) {
 				// nothing to do
 				return;
 			}
 
 			if ( !persister.hasNaturalIdCache() ) {
 				// nothing to do
 				return;
 			}
 
 			persister = locateProperPersister( persister );
 			final Object[] naturalIdValues = extractNaturalIdValues( state, persister );
 			final Object[] previousNaturalIdValues = previousState == null ? null : extractNaturalIdValues( previousState, persister );
 
 			managedSharedCacheEntries( persister, id, naturalIdValues, previousNaturalIdValues, source );
 		}
 
 		private void managedSharedCacheEntries(
 				EntityPersister persister,
 				final Serializable id,
 				Object[] naturalIdValues,
 				Object[] previousNaturalIdValues,
 				CachedNaturalIdValueSource source) {
 			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
-			final NaturalIdCacheKey naturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey( naturalIdValues, persister, session );
+			final Object naturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey( naturalIdValues, persister, session );
 
 			final SessionFactoryImplementor factory = session.getFactory();
 
 			switch ( source ) {
 				case LOAD: {
 					if ( CacheHelper.fromSharedCache( session, naturalIdCacheKey, naturalIdCacheAccessStrategy ) != null ) {
 						// prevent identical re-cachings
 						return;
 					}
 					final boolean put = naturalIdCacheAccessStrategy.putFromLoad(
 							naturalIdCacheKey,
 							id,
 							session.getTimestamp(),
 							null
 					);
 
 					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 						factory.getStatisticsImplementor().naturalIdCachePut(
 								naturalIdCacheAccessStrategy.getRegion()
 										.getName()
 						);
 					}
 
 					break;
 				}
 				case INSERT: {
 					final boolean put = naturalIdCacheAccessStrategy.insert( naturalIdCacheKey, id );
 					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 						factory.getStatisticsImplementor()
 								.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
 					}
 
 					( (EventSource) session ).getActionQueue().registerProcess(
 							new AfterTransactionCompletionProcess() {
 								@Override
 								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
 									if (success) {
 										final boolean put = naturalIdCacheAccessStrategy.afterInsert( naturalIdCacheKey, id );
 
 										if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 											factory.getStatisticsImplementor()
 												.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
 										}
 									}
 									else {
 										naturalIdCacheAccessStrategy.evict( naturalIdCacheKey );
 									}
 								}
 							}
 					);
 
 					break;
 				}
 				case UPDATE: {
-					final NaturalIdCacheKey previousCacheKey = naturalIdCacheAccessStrategy.generateCacheKey( previousNaturalIdValues, persister, session );
+					final Object previousCacheKey = naturalIdCacheAccessStrategy.generateCacheKey( previousNaturalIdValues, persister, session );
 					if ( naturalIdCacheKey.equals( previousCacheKey ) ) {
 						// prevent identical re-caching, solves HHH-7309
 						return;
 					}
 					final SoftLock removalLock = naturalIdCacheAccessStrategy.lockItem( previousCacheKey, null );
 					naturalIdCacheAccessStrategy.remove( previousCacheKey );
 
 					final SoftLock lock = naturalIdCacheAccessStrategy.lockItem( naturalIdCacheKey, null );
 					final boolean put = naturalIdCacheAccessStrategy.update( naturalIdCacheKey, id );
 					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 						factory.getStatisticsImplementor()
 								.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
 					}
 
 					( (EventSource) session ).getActionQueue().registerProcess(
 							new AfterTransactionCompletionProcess() {
 								@Override
 								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
 									naturalIdCacheAccessStrategy.unlockItem( previousCacheKey, removalLock );
 									if (success) {
 										final boolean put = naturalIdCacheAccessStrategy.afterUpdate(
 												naturalIdCacheKey,
 												id,
 												lock
 										);
 
 										if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 											factory.getStatisticsImplementor()
 												.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
 										}
 									}
 									else {
 										naturalIdCacheAccessStrategy.unlockItem( naturalIdCacheKey, lock );
 									}
 								}
 							}
 					);
 
 					break;
 				}
 				default: {
 					LOG.debug( "Unexpected CachedNaturalIdValueSource [" + source + "]" );
 				}
 			}
 		}
 
 		@Override
 		public Object[] removeLocalNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] state) {
 			if ( !persister.hasNaturalIdentifier() ) {
 				// nothing to do
 				return null;
 			}
 
 			persister = locateProperPersister( persister );
 			final Object[] naturalIdValues = getNaturalIdValues( state, persister );
 
 			final Object[] localNaturalIdValues = naturalIdXrefDelegate.removeNaturalIdCrossReference( 
 					persister, 
 					id, 
 					naturalIdValues 
 			);
 
 			return localNaturalIdValues != null ? localNaturalIdValues : naturalIdValues;
 		}
 
 		@Override
 		public void removeSharedNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] naturalIdValues) {
 			if ( !persister.hasNaturalIdentifier() ) {
 				// nothing to do
 				return;
 			}
 
 			if ( ! persister.hasNaturalIdCache() ) {
 				// nothing to do
 				return;
 			}
 
 			// todo : couple of things wrong here:
 			//		1) should be using access strategy, not plain evict..
 			//		2) should prefer session-cached values if any (requires interaction from removeLocalNaturalIdCrossReference
 
 			persister = locateProperPersister( persister );
 			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
-			final NaturalIdCacheKey naturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey( naturalIdValues, persister, session );
+			final Object naturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey( naturalIdValues, persister, session );
 			naturalIdCacheAccessStrategy.evict( naturalIdCacheKey );
 
 //			if ( sessionCachedNaturalIdValues != null
 //					&& !Arrays.equals( sessionCachedNaturalIdValues, deletedNaturalIdValues ) ) {
 //				final NaturalIdCacheKey sessionNaturalIdCacheKey = new NaturalIdCacheKey( sessionCachedNaturalIdValues, persister, session );
 //				naturalIdCacheAccessStrategy.evict( sessionNaturalIdCacheKey );
 //			}
 		}
 
 		@Override
 		public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk) {
 			return naturalIdXrefDelegate.findCachedNaturalId( locateProperPersister( persister ), pk );
 		}
 
 		@Override
 		public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues) {
 			return naturalIdXrefDelegate.findCachedNaturalIdResolution( locateProperPersister( persister ), naturalIdValues );
 		}
 
 		@Override
 		public Object[] extractNaturalIdValues(Object[] state, EntityPersister persister) {
 			final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 			if ( state.length == naturalIdPropertyIndexes.length ) {
 				return state;
 			}
 
 			final Object[] naturalIdValues = new Object[naturalIdPropertyIndexes.length];
 			for ( int i = 0; i < naturalIdPropertyIndexes.length; i++ ) {
 				naturalIdValues[i] = state[naturalIdPropertyIndexes[i]];
 			}
 			return naturalIdValues;
 		}
 
 		@Override
 		public Object[] extractNaturalIdValues(Object entity, EntityPersister persister) {
 			if ( entity == null ) {
 				throw new AssertionFailure( "Entity from which to extract natural id value(s) cannot be null" );
 			}
 			if ( persister == null ) {
 				throw new AssertionFailure( "Persister to use in extracting natural id value(s) cannot be null" );
 			}
 
 			final int[] naturalIdentifierProperties = persister.getNaturalIdentifierProperties();
 			final Object[] naturalIdValues = new Object[naturalIdentifierProperties.length];
 
 			for ( int i = 0; i < naturalIdentifierProperties.length; i++ ) {
 				naturalIdValues[i] = persister.getPropertyValue( entity, naturalIdentifierProperties[i] );
 			}
 
 			return naturalIdValues;
 		}
 
 		@Override
 		public Collection<Serializable> getCachedPkResolutions(EntityPersister entityPersister) {
 			return naturalIdXrefDelegate.getCachedPkResolutions( entityPersister );
 		}
 
 		@Override
 		public void handleSynchronization(EntityPersister persister, Serializable pk, Object entity) {
 			if ( !persister.hasNaturalIdentifier() ) {
 				// nothing to do
 				return;
 			}
 
 			persister = locateProperPersister( persister );
 
 			final Object[] naturalIdValuesFromCurrentObjectState = extractNaturalIdValues( entity, persister );
 			final boolean changed = ! naturalIdXrefDelegate.sameAsCached(
 					persister,
 					pk,
 					naturalIdValuesFromCurrentObjectState
 			);
 
 			if ( changed ) {
 				final Object[] cachedNaturalIdValues = naturalIdXrefDelegate.findCachedNaturalId( persister, pk );
 				naturalIdXrefDelegate.cacheNaturalIdCrossReference( persister, pk, naturalIdValuesFromCurrentObjectState );
 				naturalIdXrefDelegate.stashInvalidNaturalIdReference( persister, cachedNaturalIdValues );
 
 				removeSharedNaturalIdCrossReference(
 						persister,
 						pk,
 						cachedNaturalIdValues
 				);
 			}
 		}
 
 		@Override
 		public void cleanupFromSynchronizations() {
 			naturalIdXrefDelegate.unStashInvalidNaturalIdReferences();
 		}
 
 		@Override
 		public void handleEviction(Object object, EntityPersister persister, Serializable identifier) {
 			naturalIdXrefDelegate.removeNaturalIdCrossReference(
 					persister,
 					identifier,
 					findCachedNaturalId( persister, identifier )
 			);
 		}
 	};
 
 	@Override
 	public NaturalIdHelper getNaturalIdHelper() {
 		return naturalIdHelper;
 	}
 
 	private Object[] getNaturalIdValues(Object[] state, EntityPersister persister) {
 		final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 		final Object[] naturalIdValues = new Object[naturalIdPropertyIndexes.length];
 
 		for ( int i = 0; i < naturalIdPropertyIndexes.length; i++ ) {
 			naturalIdValues[i] = state[naturalIdPropertyIndexes[i]];
 		}
 
 		return naturalIdValues;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
index 444612d0c5..432610c58c 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
@@ -1,393 +1,392 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PostLoadEventListener;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.event.spi.PreLoadEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.access.internal.PropertyAccessStrategyBackRefImpl;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 import org.jboss.logging.Logger;
 
 /**
  * Functionality relating to the Hibernate two-phase loading process, that may be reused by persisters
  * that do not use the Loader framework
  *
  * @author Gavin King
  */
 public final class TwoPhaseLoad {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			TwoPhaseLoad.class.getName()
 	);
 
 	private TwoPhaseLoad() {
 	}
 
 	/**
 	 * Register the "hydrated" state of an entity instance, after the first step of 2-phase loading.
 	 *
 	 * Add the "hydrated state" (an array) of an uninitialized entity to the session. We don't try
 	 * to resolve any associations yet, because there might be other entities waiting to be
 	 * read from the JDBC result set we are currently processing
 	 *
 	 * @param persister The persister for the hydrated entity
 	 * @param id The entity identifier
 	 * @param values The entity values
 	 * @param rowId The rowId for the entity
 	 * @param object An optional instance for the entity being loaded
 	 * @param lockMode The lock mode
 	 * @param lazyPropertiesAreUnFetched Whether properties defined as lazy are yet un-fetched
 	 * @param session The Session
 	 */
 	public static void postHydrate(
 			final EntityPersister persister,
 			final Serializable id,
 			final Object[] values,
 			final Object rowId,
 			final Object object,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnFetched,
 			final SessionImplementor session) {
 		final Object version = Versioning.getVersion( values, persister );
 		session.getPersistenceContext().addEntry(
 				object,
 				Status.LOADING,
 				values,
 				rowId,
 				id,
 				version,
 				lockMode,
 				true,
 				persister,
 				false,
 				lazyPropertiesAreUnFetched
 			);
 
 		if ( version != null && LOG.isTraceEnabled() ) {
 			final String versionStr = persister.isVersioned()
 					? persister.getVersionType().toLoggableString( version, session.getFactory() )
 					: "null";
 			LOG.tracef( "Version: %s", versionStr );
 		}
 	}
 
 	/**
 	 * Perform the second step of 2-phase load. Fully initialize the entity
 	 * instance.
 	 * <p/>
 	 * After processing a JDBC result set, we "resolve" all the associations
 	 * between the entities which were instantiated and had their state
 	 * "hydrated" into an array
 	 *
 	 * @param entity The entity being loaded
 	 * @param readOnly Is the entity being loaded as read-only
 	 * @param session The Session
 	 * @param preLoadEvent The (re-used) pre-load event
 	 */
 	public static void initializeEntity(
 			final Object entity,
 			final boolean readOnly,
 			final SessionImplementor session,
 			final PreLoadEvent preLoadEvent) {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityEntry entityEntry = persistenceContext.getEntry( entity );
 		if ( entityEntry == null ) {
 			throw new AssertionFailure( "possible non-threadsafe access to the session" );
 		}
 		doInitializeEntity( entity, entityEntry, readOnly, session, preLoadEvent );
 	}
 
 	private static void doInitializeEntity(
 			final Object entity,
 			final EntityEntry entityEntry,
 			final boolean readOnly,
 			final SessionImplementor session,
 			final PreLoadEvent preLoadEvent) throws HibernateException {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityPersister persister = entityEntry.getPersister();
 		final Serializable id = entityEntry.getId();
 		final Object[] hydratedState = entityEntry.getLoadedState();
 
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debugf(
 					"Resolving associations for %s",
 					MessageHelper.infoString( persister, id, session.getFactory() )
 			);
 		}
 
 		final Type[] types = persister.getPropertyTypes();
 		for ( int i = 0; i < hydratedState.length; i++ ) {
 			final Object value = hydratedState[i];
 			if ( value!=LazyPropertyInitializer.UNFETCHED_PROPERTY && value!= PropertyAccessStrategyBackRefImpl.UNKNOWN ) {
 				hydratedState[i] = types[i].resolve( value, session, entity );
 			}
 		}
 
 		//Must occur after resolving identifiers!
 		if ( session.isEventSource() ) {
 			preLoadEvent.setEntity( entity ).setState( hydratedState ).setId( id ).setPersister( persister );
 
 			final EventListenerGroup<PreLoadEventListener> listenerGroup = session
 					.getFactory()
 					.getServiceRegistry()
 					.getService( EventListenerRegistry.class )
 					.getEventListenerGroup( EventType.PRE_LOAD );
 			for ( PreLoadEventListener listener : listenerGroup.listeners() ) {
 				listener.onPreLoad( preLoadEvent );
 			}
 		}
 
 		persister.setPropertyValues( entity, hydratedState );
 
 		final SessionFactoryImplementor factory = session.getFactory();
 		if ( persister.hasCache() && session.getCacheMode().isPutEnabled() ) {
 
 			if ( debugEnabled ) {
 				LOG.debugf(
 						"Adding entity to second-level cache: %s",
 						MessageHelper.infoString( persister, id, session.getFactory() )
 				);
 			}
 
 			final Object version = Versioning.getVersion( hydratedState, persister );
 			final CacheEntry entry = persister.buildCacheEntry( entity, hydratedState, version, session );
 			final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-			final EntityCacheKey cacheKey = cache.generateCacheKey( id, persister, factory, session.getTenantIdentifier() );
+			final Object cacheKey = cache.generateCacheKey( id, persister, factory, session.getTenantIdentifier() );
 
 			// explicit handling of caching for rows just inserted and then somehow forced to be read
 			// from the database *within the same transaction*.  usually this is done by
 			// 		1) Session#refresh, or
 			// 		2) Session#clear + some form of load
 			//
 			// we need to be careful not to clobber the lock here in the cache so that it can be rolled back if need be
 			if ( session.getPersistenceContext().wasInsertedDuringTransaction( persister, id ) ) {
 				cache.update(
 						cacheKey,
 						persister.getCacheEntryStructure().structure( entry ),
 						version,
 						version
 				);
 			}
 			else {
 				final SessionEventListenerManager eventListenerManager = session.getEventListenerManager();
 				try {
 					eventListenerManager.cachePutStart();
 					final boolean put = cache.putFromLoad(
 							cacheKey,
 							persister.getCacheEntryStructure().structure( entry ),
 							session.getTimestamp(),
 							version,
 							useMinimalPuts( session, entityEntry )
 					);
 
 					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 						factory.getStatisticsImplementor().secondLevelCachePut( cache.getRegion().getName() );
 					}
 				}
 				finally {
 					eventListenerManager.cachePutEnd();
 				}
 			}
 		}
 
 		if ( persister.hasNaturalIdentifier() ) {
 			persistenceContext.getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					id,
 					persistenceContext.getNaturalIdHelper().extractNaturalIdValues( hydratedState, persister )
 			);
 		}
 
 		boolean isReallyReadOnly = readOnly;
 		if ( !persister.isMutable() ) {
 			isReallyReadOnly = true;
 		}
 		else {
 			final Object proxy = persistenceContext.getProxy( entityEntry.getEntityKey() );
 			if ( proxy != null ) {
 				// there is already a proxy for this impl
 				// only set the status to read-only if the proxy is read-only
 				isReallyReadOnly = ( (HibernateProxy) proxy ).getHibernateLazyInitializer().isReadOnly();
 			}
 		}
 		if ( isReallyReadOnly ) {
 			//no need to take a snapshot - this is a
 			//performance optimization, but not really
 			//important, except for entities with huge
 			//mutable property values
 			persistenceContext.setEntryStatus( entityEntry, Status.READ_ONLY );
 		}
 		else {
 			//take a snapshot
 			TypeHelper.deepCopy(
 					hydratedState,
 					persister.getPropertyTypes(),
 					persister.getPropertyUpdateability(),
 					//after setting values to object
 					hydratedState,
 					session
 			);
 			persistenceContext.setEntryStatus( entityEntry, Status.MANAGED );
 		}
 
 		persister.afterInitialize(
 				entity,
 				entityEntry.isLoadedWithLazyPropertiesUnfetched(),
 				session
 		);
 
 		if ( debugEnabled ) {
 			LOG.debugf(
 					"Done materializing entity %s",
 					MessageHelper.infoString( persister, id, session.getFactory() )
 			);
 		}
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().loadEntity( persister.getEntityName() );
 		}
 	}
 	
 	/**
 	 * PostLoad cannot occur during initializeEntity, as that call occurs *before*
 	 * the Set collections are added to the persistence context by Loader.
 	 * Without the split, LazyInitializationExceptions can occur in the Entity's
 	 * postLoad if it acts upon the collection.
 	 *
 	 * HHH-6043
 	 * 
 	 * @param entity The entity
 	 * @param session The Session
 	 * @param postLoadEvent The (re-used) post-load event
 	 */
 	public static void postLoad(
 			final Object entity,
 			final SessionImplementor session,
 			final PostLoadEvent postLoadEvent) {
 		
 		if ( session.isEventSource() ) {
 			final PersistenceContext persistenceContext
 					= session.getPersistenceContext();
 			final EntityEntry entityEntry = persistenceContext.getEntry( entity );
 
 			postLoadEvent.setEntity( entity ).setId( entityEntry.getId() ).setPersister( entityEntry.getPersister() );
 
 			final EventListenerGroup<PostLoadEventListener> listenerGroup = session.getFactory()
 							.getServiceRegistry()
 							.getService( EventListenerRegistry.class )
 							.getEventListenerGroup( EventType.POST_LOAD );
 			for ( PostLoadEventListener listener : listenerGroup.listeners() ) {
 				listener.onPostLoad( postLoadEvent );
 			}
 		}
 	}
 
 	private static boolean useMinimalPuts(SessionImplementor session, EntityEntry entityEntry) {
 		return ( session.getFactory().getSettings().isMinimalPutsEnabled()
 				&& session.getCacheMode()!=CacheMode.REFRESH )
 				|| ( entityEntry.getPersister().hasLazyProperties()
 				&& entityEntry.isLoadedWithLazyPropertiesUnfetched()
 				&& entityEntry.getPersister().isLazyPropertiesCacheable() );
 	}
 
 	/**
 	 * Add an uninitialized instance of an entity class, as a placeholder to ensure object
 	 * identity. Must be called before <tt>postHydrate()</tt>.
 	 *
 	 * Create a "temporary" entry for a newly instantiated entity. The entity is uninitialized,
 	 * but we need the mapping from id to instance in order to guarantee uniqueness.
 	 *
 	 * @param key The entity key
 	 * @param object The entity instance
 	 * @param persister The entity persister
 	 * @param lockMode The lock mode
 	 * @param lazyPropertiesAreUnFetched Are lazy properties still un-fetched?
 	 * @param session The Session
 	 */
 	public static void addUninitializedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnFetched,
 			final SessionImplementor session) {
 		session.getPersistenceContext().addEntity(
 				object,
 				Status.LOADING,
 				null,
 				key,
 				null,
 				lockMode,
 				true,
 				persister,
 				false,
 				lazyPropertiesAreUnFetched
 		);
 	}
 
 	/**
 	 * Same as {@link #addUninitializedEntity}, but here for an entity from the second level cache
 	 *
 	 * @param key The entity key
 	 * @param object The entity instance
 	 * @param persister The entity persister
 	 * @param lockMode The lock mode
 	 * @param lazyPropertiesAreUnFetched Are lazy properties still un-fetched?
 	 * @param version The version
 	 * @param session The Session
 	 */
 	public static void addUninitializedCachedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnFetched,
 			final Object version,
 			final SessionImplementor session) {
 		session.getPersistenceContext().addEntity(
 				object,
 				Status.LOADING,
 				null,
 				key,
 				version,
 				lockMode,
 				true,
 				persister,
 				false,
 				lazyPropertiesAreUnFetched
 			);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
index 0d13051b82..af8624032a 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
@@ -1,391 +1,389 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.loading.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
-import org.hibernate.cache.spi.CollectionCacheKey;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CollectionCacheEntry;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * Represents state associated with the processing of a given {@link ResultSet}
  * in regards to loading collections.
  * <p/>
  * Another implementation option to consider is to not expose {@link ResultSet}s
  * directly (in the JDBC redesign) but to always "wrap" them and apply a
  * [series of] context[s] to that wrapper.
  *
  * @author Steve Ebersole
  */
 public class CollectionLoadContext {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( CollectionLoadContext.class );
 
 	private final LoadContexts loadContexts;
 	private final ResultSet resultSet;
 	private Set<CollectionKey> localLoadingCollectionKeys = new HashSet<CollectionKey>();
 
 	/**
 	 * Creates a collection load context for the given result set.
 	 *
 	 * @param loadContexts Callback to other collection load contexts.
 	 * @param resultSet The result set this is "wrapping".
 	 */
 	public CollectionLoadContext(LoadContexts loadContexts, ResultSet resultSet) {
 		this.loadContexts = loadContexts;
 		this.resultSet = resultSet;
 	}
 
 	public ResultSet getResultSet() {
 		return resultSet;
 	}
 
 	public LoadContexts getLoadContext() {
 		return loadContexts;
 	}
 
 	/**
 	 * Retrieve the collection that is being loaded as part of processing this
 	 * result set.
 	 * <p/>
 	 * Basically, there are two valid return values from this method:<ul>
 	 * <li>an instance of {@link org.hibernate.collection.spi.PersistentCollection} which indicates to
 	 * continue loading the result set row data into that returned collection
 	 * instance; this may be either an instance already associated and in the
 	 * midst of being loaded, or a newly instantiated instance as a matching
 	 * associated collection was not found.</li>
 	 * <li><i>null</i> indicates to ignore the corresponding result set row
 	 * data relating to the requested collection; this indicates that either
 	 * the collection was found to already be associated with the persistence
 	 * context in a fully loaded state, or it was found in a loading state
 	 * associated with another result set processing context.</li>
 	 * </ul>
 	 *
 	 * @param persister The persister for the collection being requested.
 	 * @param key The key of the collection being requested.
 	 *
 	 * @return The loading collection (see discussion above).
 	 */
 	public PersistentCollection getLoadingCollection(final CollectionPersister persister, final Serializable key) {
 		final EntityMode em = persister.getOwnerEntityPersister().getEntityMetamodel().getEntityMode();
 		final CollectionKey collectionKey = new CollectionKey( persister, key, em );
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Starting attempt to find loading collection [{0}]",
 					MessageHelper.collectionInfoString( persister.getRole(), key ) );
 		}
 		final LoadingCollectionEntry loadingCollectionEntry = loadContexts.locateLoadingCollectionEntry( collectionKey );
 		if ( loadingCollectionEntry == null ) {
 			// look for existing collection as part of the persistence context
 			PersistentCollection collection = loadContexts.getPersistenceContext().getCollection( collectionKey );
 			if ( collection != null ) {
 				if ( collection.wasInitialized() ) {
 					LOG.trace( "Collection already initialized; ignoring" );
 					// ignore this row of results! Note the early exit
 					return null;
 				}
 				LOG.trace( "Collection not yet initialized; initializing" );
 			}
 			else {
 				final Object owner = loadContexts.getPersistenceContext().getCollectionOwner( key, persister );
 				final boolean newlySavedEntity = owner != null
 						&& loadContexts.getPersistenceContext().getEntry( owner ).getStatus() != Status.LOADING;
 				if ( newlySavedEntity ) {
 					// important, to account for newly saved entities in query
 					// todo : some kind of check for new status...
 					LOG.trace( "Owning entity already loaded; ignoring" );
 					return null;
 				}
 				// create one
 				LOG.tracev( "Instantiating new collection [key={0}, rs={1}]", key, resultSet );
 				collection = persister.getCollectionType().instantiate(
 						loadContexts.getPersistenceContext().getSession(), persister, key );
 			}
 			collection.beforeInitialize( persister, -1 );
 			collection.beginRead();
 			localLoadingCollectionKeys.add( collectionKey );
 			loadContexts.registerLoadingCollectionXRef( collectionKey, new LoadingCollectionEntry( resultSet, persister, key, collection ) );
 			return collection;
 		}
 		if ( loadingCollectionEntry.getResultSet() == resultSet ) {
 			LOG.trace( "Found loading collection bound to current result set processing; reading row" );
 			return loadingCollectionEntry.getCollection();
 		}
 		// ignore this row, the collection is in process of
 		// being loaded somewhere further "up" the stack
 		LOG.trace( "Collection is already being initialized; ignoring row" );
 		return null;
 	}
 
 	/**
 	 * Finish the process of collection-loading for this bound result set.  Mainly this
 	 * involves cleaning up resources and notifying the collections that loading is
 	 * complete.
 	 *
 	 * @param persister The persister for which to complete loading.
 	 */
 	public void endLoadingCollections(CollectionPersister persister) {
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 		if ( !loadContexts.hasLoadingCollectionEntries()
 				&& localLoadingCollectionKeys.isEmpty() ) {
 			return;
 		}
 
 		// in an effort to avoid concurrent-modification-exceptions (from
 		// potential recursive calls back through here as a result of the
 		// eventual call to PersistentCollection#endRead), we scan the
 		// internal loadingCollections map for matches and store those matches
 		// in a temp collection.  the temp collection is then used to "drive"
 		// the #endRead processing.
 		List<LoadingCollectionEntry> matches = null;
 		final Iterator itr = localLoadingCollectionKeys.iterator();
 		while ( itr.hasNext() ) {
 			final CollectionKey collectionKey = (CollectionKey) itr.next();
 			final LoadingCollectionEntry lce = loadContexts.locateLoadingCollectionEntry( collectionKey );
 			if ( lce == null ) {
 				LOG.loadingCollectionKeyNotFound( collectionKey );
 			}
 			else if ( lce.getResultSet() == resultSet && lce.getPersister() == persister ) {
 				if ( matches == null ) {
 					matches = new ArrayList<LoadingCollectionEntry>();
 				}
 				matches.add( lce );
 				if ( lce.getCollection().getOwner() == null ) {
 					session.getPersistenceContext().addUnownedCollection(
 							new CollectionKey(
 									persister,
 									lce.getKey(),
 									persister.getOwnerEntityPersister().getEntityMetamodel().getEntityMode()
 							),
 							lce.getCollection()
 					);
 				}
 				LOG.tracev( "Removing collection load entry [{0}]", lce );
 
 				// todo : i'd much rather have this done from #endLoadingCollection(CollectionPersister,LoadingCollectionEntry)...
 				loadContexts.unregisterLoadingCollectionXRef( collectionKey );
 				itr.remove();
 			}
 		}
 
 		endLoadingCollections( persister, matches );
 		if ( localLoadingCollectionKeys.isEmpty() ) {
 			// todo : hack!!!
 			// NOTE : here we cleanup the load context when we have no more local
 			// LCE entries.  This "works" for the time being because really
 			// only the collection load contexts are implemented.  Long term,
 			// this cleanup should become part of the "close result set"
 			// processing from the (sandbox/jdbc) jdbc-container code.
 			loadContexts.cleanup( resultSet );
 		}
 	}
 
 	private void endLoadingCollections(CollectionPersister persister, List<LoadingCollectionEntry> matchedCollectionEntries) {
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( matchedCollectionEntries == null ) {
 			if ( debugEnabled ) {
 				LOG.debugf( "No collections were found in result set for role: %s", persister.getRole() );
 			}
 			return;
 		}
 
 		final int count = matchedCollectionEntries.size();
 		if ( debugEnabled ) {
 			LOG.debugf( "%s collections were found in result set for role: %s", count, persister.getRole() );
 		}
 
 		for ( LoadingCollectionEntry matchedCollectionEntry : matchedCollectionEntries ) {
 			endLoadingCollection( matchedCollectionEntry, persister );
 		}
 
 		if ( debugEnabled ) {
 			LOG.debugf( "%s collections initialized for role: %s", count, persister.getRole() );
 		}
 	}
 
 	private void endLoadingCollection(LoadingCollectionEntry lce, CollectionPersister persister) {
 		LOG.tracev( "Ending loading collection [{0}]", lce );
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 
 		// warning: can cause a recursive calls! (proxy initialization)
 		final boolean hasNoQueuedAdds = lce.getCollection().endRead();
 
 		if ( persister.getCollectionType().hasHolder() ) {
 			getLoadContext().getPersistenceContext().addCollectionHolder( lce.getCollection() );
 		}
 
 		CollectionEntry ce = getLoadContext().getPersistenceContext().getCollectionEntry( lce.getCollection() );
 		if ( ce == null ) {
 			ce = getLoadContext().getPersistenceContext().addInitializedCollection( persister, lce.getCollection(), lce.getKey() );
 		}
 		else {
 			ce.postInitialize( lce.getCollection() );
 //			if (ce.getLoadedPersister().getBatchSize() > 1) { // not the best place for doing this, moved into ce.postInitialize
 //				getLoadContext().getPersistenceContext().getBatchFetchQueue().removeBatchLoadableCollection(ce); 
 //			}
 		}
 
 
 		// add to cache if:
 		boolean addToCache =
 				// there were no queued additions
 				hasNoQueuedAdds
 				// and the role has a cache
 				&& persister.hasCache()
 				// and this is not a forced initialization during flush
 				&& session.getCacheMode().isPutEnabled() && !ce.isDoremove();
 		if ( addToCache ) {
 			addCollectionToCache( lce, persister );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Collection fully initialized: %s",
 					MessageHelper.collectionInfoString( persister, lce.getCollection(), lce.getKey(), session )
 			);
 		}
 		if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 			session.getFactory().getStatisticsImplementor().loadCollection( persister.getRole() );
 		}
 	}
 
 	/**
 	 * Add the collection to the second-level cache
 	 *
 	 * @param lce The entry representing the collection to add
 	 * @param persister The persister
 	 */
 	private void addCollectionToCache(LoadingCollectionEntry lce, CollectionPersister persister) {
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 		final SessionFactoryImplementor factory = session.getFactory();
 
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debugf( "Caching collection: %s", MessageHelper.collectionInfoString( persister, lce.getCollection(), lce.getKey(), session ) );
 		}
 
 		if ( !session.getLoadQueryInfluencers().getEnabledFilters().isEmpty() && persister.isAffectedByEnabledFilters( session ) ) {
 			// some filters affecting the collection are enabled on the session, so do not do the put into the cache.
 			if ( debugEnabled ) {
 				LOG.debug( "Refusing to add to cache due to enabled filters" );
 			}
 			// todo : add the notion of enabled filters to the CacheKey to differentiate filtered collections from non-filtered;
 			//      but CacheKey is currently used for both collections and entities; would ideally need to define two seperate ones;
 			//      currently this works in conjuction with the check on
 			//      DefaultInitializeCollectionEventHandler.initializeCollectionFromCache() (which makes sure to not read from
 			//      cache with enabled filters).
 			// EARLY EXIT!!!!!
 			return;
 		}
 
 		final Object version;
 		if ( persister.isVersioned() ) {
 			Object collectionOwner = getLoadContext().getPersistenceContext().getCollectionOwner( lce.getKey(), persister );
 			if ( collectionOwner == null ) {
 				// generally speaking this would be caused by the collection key being defined by a property-ref, thus
 				// the collection key and the owner key would not match up.  In this case, try to use the key of the
 				// owner instance associated with the collection itself, if one.  If the collection does already know
 				// about its owner, that owner should be the same instance as associated with the PC, but we do the
 				// resolution against the PC anyway just to be safe since the lookup should not be costly.
 				if ( lce.getCollection() != null ) {
 					final Object linkedOwner = lce.getCollection().getOwner();
 					if ( linkedOwner != null ) {
 						final Serializable ownerKey = persister.getOwnerEntityPersister().getIdentifier( linkedOwner, session );
 						collectionOwner = getLoadContext().getPersistenceContext().getCollectionOwner( ownerKey, persister );
 					}
 				}
 				if ( collectionOwner == null ) {
 					throw new HibernateException(
 							"Unable to resolve owner of loading collection [" +
 									MessageHelper.collectionInfoString( persister, lce.getCollection(), lce.getKey(), session ) +
 									"] for second level caching"
 					);
 				}
 			}
 			version = getLoadContext().getPersistenceContext().getEntry( collectionOwner ).getVersion();
 		}
 		else {
 			version = null;
 		}
 
 		final CollectionCacheEntry entry = new CollectionCacheEntry( lce.getCollection(), persister );
 		final CollectionRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-		final CollectionCacheKey cacheKey = cache.generateCacheKey(
+		final Object cacheKey = cache.generateCacheKey(
 				lce.getKey(),
 				persister,
 				session.getFactory(),
 				session.getTenantIdentifier()
 		);
 
 		boolean isPutFromLoad = true;
 		if ( persister.getElementType().isAssociationType() ) {
 			for ( Serializable id : entry.getState() ) {
 				EntityPersister entityPersister = ( (QueryableCollection) persister ).getElementPersister();
 				if ( session.getPersistenceContext().wasInsertedDuringTransaction( entityPersister, id ) ) {
 					isPutFromLoad = false;
 					break;
 				}
 			}
 		}
 
 		// CollectionRegionAccessStrategy has no update, so avoid putting uncommitted data via putFromLoad
 		if (isPutFromLoad) {
 			try {
 				session.getEventListenerManager().cachePutStart();
 				final boolean put = cache.putFromLoad(
 						cacheKey,
 						persister.getCacheEntryStructure().structure( entry ),
 						session.getTimestamp(),
 						version,
 						factory.getSettings().isMinimalPutsEnabled() && session.getCacheMode()!= CacheMode.REFRESH
 				);
 
 				if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 					factory.getStatisticsImplementor().secondLevelCachePut( persister.getCacheAccessStrategy().getRegion().getName() );
 				}
 			}
 			finally {
 				session.getEventListenerManager().cachePutEnd();
 			}
 		}
 	}
 
 	void cleanup() {
 		if ( !localLoadingCollectionKeys.isEmpty() ) {
 			LOG.localLoadingCollectionKeysCount( localLoadingCollectionKeys.size() );
 		}
 		loadContexts.cleanupCollectionXRefs( localLoadingCollectionKeys );
 		localLoadingCollectionKeys.clear();
 	}
 
 
 	@Override
 	public String toString() {
 		return super.toString() + "<rs=" + resultSet + ">";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/BatchFetchQueue.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/BatchFetchQueue.java
index 245b0ae98b..b28669d43b 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/BatchFetchQueue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/BatchFetchQueue.java
@@ -1,337 +1,335 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.spi;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.LinkedHashMap;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.Map.Entry;
 
 import org.hibernate.EntityMode;
-import org.hibernate.cache.spi.CollectionCacheKey;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.CacheHelper;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.jboss.logging.Logger;
 
 /**
  * Tracks entity and collection keys that are available for batch
  * fetching, and the queries which were used to load entities, which
  * can be re-used as a subquery for loading owned collections.
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Guenther Demetz
  */
 public class BatchFetchQueue {
 	private static final Logger LOG = CoreLogging.logger( BatchFetchQueue.class );
 
 	private final PersistenceContext context;
 
 	/**
 	 * A map of {@link SubselectFetch subselect-fetch descriptors} keyed by the
 	 * {@link EntityKey) against which the descriptor is registered.
 	 */
 	private final Map<EntityKey, SubselectFetch> subselectsByEntityKey = new HashMap<EntityKey, SubselectFetch>(8);
 
 	/**
 	 * Used to hold information about the entities that are currently eligible for batch-fetching.  Ultimately
 	 * used by {@link #getEntityBatch} to build entity load batches.
 	 * <p/>
 	 * A Map structure is used to segment the keys by entity type since loading can only be done for a particular entity
 	 * type at a time.
 	 */
 	private final Map <String,LinkedHashSet<EntityKey>> batchLoadableEntityKeys = new HashMap <String,LinkedHashSet<EntityKey>>(8);
 	
 	/**
 	 * Used to hold information about the collections that are currently eligible for batch-fetching.  Ultimately
 	 * used by {@link #getCollectionBatch} to build collection load batches.
 	 */
 	private final Map<String, LinkedHashMap<CollectionEntry, PersistentCollection>> batchLoadableCollections =
 			new HashMap<String, LinkedHashMap <CollectionEntry, PersistentCollection>>(8);
 
 	/**
 	 * Constructs a queue for the given context.
 	 *
 	 * @param context The owning context.
 	 */
 	public BatchFetchQueue(PersistenceContext context) {
 		this.context = context;
 	}
 
 	/**
 	 * Clears all entries from this fetch queue.
 	 * <p/>
 	 * Called after flushing or clearing the session.
 	 */
 	public void clear() {
 		batchLoadableEntityKeys.clear();
 		batchLoadableCollections.clear();
 		subselectsByEntityKey.clear();
 	}
 
 
 	// sub-select support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Retrieve the fetch descriptor associated with the given entity key.
 	 *
 	 * @param key The entity key for which to locate any defined subselect fetch.
 	 * @return The fetch descriptor; may return null if no subselect fetch queued for
 	 * this entity key.
 	 */
 	public SubselectFetch getSubselect(EntityKey key) {
 		return subselectsByEntityKey.get( key );
 	}
 
 	/**
 	 * Adds a subselect fetch decriptor for the given entity key.
 	 *
 	 * @param key The entity for which to register the subselect fetch.
 	 * @param subquery The fetch descriptor.
 	 */
 	public void addSubselect(EntityKey key, SubselectFetch subquery) {
 		subselectsByEntityKey.put( key, subquery );
 	}
 
 	/**
 	 * After evicting or deleting an entity, we don't need to
 	 * know the query that was used to load it anymore (don't
 	 * call this after loading the entity, since we might still
 	 * need to load its collections)
 	 */
 	public void removeSubselect(EntityKey key) {
 		subselectsByEntityKey.remove( key );
 	}
 
 	// entity batch support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * If an EntityKey represents a batch loadable entity, add
 	 * it to the queue.
 	 * <p/>
 	 * Note that the contract here is such that any key passed in should
 	 * previously have been been checked for existence within the
 	 * {@link PersistenceContext}; failure to do so may cause the
 	 * referenced entity to be included in a batch even though it is
 	 * already associated with the {@link PersistenceContext}.
 	 */
 	public void addBatchLoadableEntityKey(EntityKey key) {
 		if ( key.isBatchLoadable() ) {
 			LinkedHashSet<EntityKey> set =  batchLoadableEntityKeys.get( key.getEntityName());
 			if (set == null) {
 				set = new LinkedHashSet<EntityKey>(8);
 				batchLoadableEntityKeys.put( key.getEntityName(), set);
 			}
 			set.add(key);
 		}
 	}
 	
 
 	/**
 	 * After evicting or deleting or loading an entity, we don't
 	 * need to batch fetch it anymore, remove it from the queue
 	 * if necessary
 	 */
 	public void removeBatchLoadableEntityKey(EntityKey key) {
 		if ( key.isBatchLoadable() ) {
 			LinkedHashSet<EntityKey> set =  batchLoadableEntityKeys.get( key.getEntityName());
 			if (set != null) {
 				set.remove(key);
 			}
 		}
 	}
 
 	/**
 	 * Get a batch of unloaded identifiers for this class, using a slightly
 	 * complex algorithm that tries to grab keys registered immediately after
 	 * the given key.
 	 *
 	 * @param persister The persister for the entities being loaded.
 	 * @param id The identifier of the entity currently demanding load.
 	 * @param batchSize The maximum number of keys to return
 	 * @return an array of identifiers, of length batchSize (possibly padded with nulls)
 	 */
 	public Serializable[] getEntityBatch(
 			final EntityPersister persister,
 			final Serializable id,
 			final int batchSize,
 			final EntityMode entityMode) {
 		Serializable[] ids = new Serializable[batchSize];
 		ids[0] = id; //first element of array is reserved for the actual instance we are loading!
 		int i = 1;
 		int end = -1;
 		boolean checkForEnd = false;
 
 		// TODO: this needn't exclude subclasses...
 
 		LinkedHashSet<EntityKey> set =  batchLoadableEntityKeys.get( persister.getEntityName() );
 		if ( set != null ) {
 			for ( EntityKey key : set ) {
 				if ( checkForEnd && i == end ) {
 					//the first id found after the given id
 					return ids;
 				}
 				if ( persister.getIdentifierType().isEqual( id, key.getIdentifier() ) ) {
 					end = i;
 				}
 				else {
 					if ( !isCached( key, persister ) ) {
 						ids[i++] = key.getIdentifier();
 					}
 				}
 				if ( i == batchSize ) {
 					i = 1; // end of array, start filling again from start
 					if ( end != -1 ) {
 						checkForEnd = true;
 					}
 				}
 			}
 		}
 		return ids; //we ran out of ids to try
 	}
 
 	private boolean isCached(EntityKey entityKey, EntityPersister persister) {
 		final SessionImplementor session = context.getSession();
 		if ( context.getSession().getCacheMode().isGetEnabled() && persister.hasCache() ) {
 			final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-			final EntityCacheKey key = cache.generateCacheKey(
+			final Object key = cache.generateCacheKey(
 					entityKey.getIdentifier(),
 					persister,
 					session.getFactory(),
 					session.getTenantIdentifier()
 			);
 			return CacheHelper.fromSharedCache( session, key, cache ) != null;
 		}
 		return false;
 	}
 	
 
 	// collection batch support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * If an CollectionEntry represents a batch loadable collection, add
 	 * it to the queue.
 	 */
 	public void addBatchLoadableCollection(PersistentCollection collection, CollectionEntry ce) {
 		final CollectionPersister persister = ce.getLoadedPersister();
 
 		LinkedHashMap<CollectionEntry, PersistentCollection> map =  batchLoadableCollections.get( persister.getRole() );
 		if ( map == null ) {
 			map = new LinkedHashMap<CollectionEntry, PersistentCollection>( 16 );
 			batchLoadableCollections.put( persister.getRole(), map );
 		}
 		map.put( ce, collection );
 	}
 	
 	/**
 	 * After a collection was initialized or evicted, we don't
 	 * need to batch fetch it anymore, remove it from the queue
 	 * if necessary
 	 */
 	public void removeBatchLoadableCollection(CollectionEntry ce) {
 		LinkedHashMap<CollectionEntry, PersistentCollection> map =  batchLoadableCollections.get( ce.getLoadedPersister().getRole() );
 		if ( map != null ) {
 			map.remove( ce );
 		}
 	}
 
 	/**
 	 * Get a batch of uninitialized collection keys for a given role
 	 *
 	 * @param collectionPersister The persister for the collection role.
 	 * @param id A key that must be included in the batch fetch
 	 * @param batchSize the maximum number of keys to return
 	 * @return an array of collection keys, of length batchSize (padded with nulls)
 	 */
 	public Serializable[] getCollectionBatch(
 			final CollectionPersister collectionPersister,
 			final Serializable id,
 			final int batchSize) {
 
 		Serializable[] keys = new Serializable[batchSize];
 		keys[0] = id;
 
 		int i = 1;
 		int end = -1;
 		boolean checkForEnd = false;
 
 		final LinkedHashMap<CollectionEntry, PersistentCollection> map =  batchLoadableCollections.get( collectionPersister.getRole() );
 		if ( map != null ) {
 			for ( Entry<CollectionEntry, PersistentCollection> me : map.entrySet() ) {
 				final CollectionEntry ce = me.getKey();
 				final PersistentCollection collection = me.getValue();
 				
 				if ( ce.getLoadedKey() == null ) {
 					// the loadedKey of the collectionEntry might be null as it might have been reset to null
 					// (see for example Collections.processDereferencedCollection()
 					// and CollectionEntry.afterAction())
 					// though we clear the queue on flush, it seems like a good idea to guard
 					// against potentially null loadedKeys (which leads to various NPEs as demonstrated in HHH-7821).
 					continue;
 				}
 
 				if ( collection.wasInitialized() ) {
 					// should never happen
 					LOG.warn( "Encountered initialized collection in BatchFetchQueue, this should not happen." );
 					continue;
 				}
 
 				if ( checkForEnd && i == end ) {
 					return keys; //the first key found after the given key
 				}
 
 				final boolean isEqual = collectionPersister.getKeyType().isEqual(
 						id,
 						ce.getLoadedKey(),
 						collectionPersister.getFactory()
 				);
 
 				if ( isEqual ) {
 					end = i;
 					//checkForEnd = false;
 				}
 				else if ( !isCached( ce.getLoadedKey(), collectionPersister ) ) {
 					keys[i++] = ce.getLoadedKey();
 					//count++;
 				}
 
 				if ( i == batchSize ) {
 					i = 1; //end of array, start filling again from start
 					if ( end != -1 ) {
 						checkForEnd = true;
 					}
 				}
 			}
 		}
 		return keys; //we ran out of keys to try
 	}
 
 	private boolean isCached(Serializable collectionKey, CollectionPersister persister) {
 		SessionImplementor session = context.getSession();
 		if ( session.getCacheMode().isGetEnabled() && persister.hasCache() ) {
 			CollectionRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-			CollectionCacheKey cacheKey = cache.generateCacheKey(
+			Object cacheKey = cache.generateCacheKey(
 					collectionKey,
 					persister,
 					session.getFactory(),
 					session.getTenantIdentifier()
 			);
 			return CacheHelper.fromSharedCache( session, cacheKey, cache ) != null;
 		}
 		return false;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryDelegatingImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryDelegatingImpl.java
index 6dc65a61a2..86cd592ba9 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryDelegatingImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryDelegatingImpl.java
@@ -1,414 +1,414 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.spi;
 
+import javax.naming.NamingException;
+import javax.naming.Reference;
 import java.sql.Connection;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
-import javax.naming.NamingException;
-import javax.naming.Reference;
 
-import org.hibernate.Cache;
-import org.hibernate.CustomEntityDirtinessStrategy;
-import org.hibernate.EntityNameResolver;
-import org.hibernate.HibernateException;
-import org.hibernate.Interceptor;
-import org.hibernate.MappingException;
-import org.hibernate.Session;
-import org.hibernate.SessionFactory;
-import org.hibernate.SessionFactoryObserver;
-import org.hibernate.StatelessSession;
-import org.hibernate.StatelessSessionBuilder;
-import org.hibernate.TypeHelper;
+import org.hibernate.*;
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
+import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.cfg.Settings;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.NamedQueryRepository;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Base delegating implementation of the SessionFactory and SessionFactoryImplementor
  * contracts for intended for easier implementation of SessionFactory.
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings({"deprecation", "unused"})
 public class SessionFactoryDelegatingImpl implements SessionFactoryImplementor, SessionFactory {
 	private final SessionFactoryImplementor delegate;
 
 	public SessionFactoryDelegatingImpl(SessionFactoryImplementor delegate) {
 		this.delegate = delegate;
 	}
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return delegate.getSessionFactoryOptions();
 	}
 
 	@Override
 	public SessionBuilderImplementor withOptions() {
 		return delegate.withOptions();
 	}
 
 	@Override
 	public Session openSession() throws HibernateException {
 		return delegate.openSession();
 	}
 
 	@Override
 	public Session getCurrentSession() throws HibernateException {
 		return delegate.getCurrentSession();
 	}
 
 	@Override
 	public StatelessSessionBuilder withStatelessOptions() {
 		return delegate.withStatelessOptions();
 	}
 
 	@Override
 	public StatelessSession openStatelessSession() {
 		return delegate.openStatelessSession();
 	}
 
 	@Override
 	public StatelessSession openStatelessSession(Connection connection) {
 		return delegate.openStatelessSession( connection );
 	}
 
 	@Override
 	public ClassMetadata getClassMetadata(Class entityClass) {
 		return delegate.getClassMetadata( entityClass );
 	}
 
 	@Override
 	public ClassMetadata getClassMetadata(String entityName) {
 		return delegate.getClassMetadata( entityName );
 	}
 
 	@Override
 	public CollectionMetadata getCollectionMetadata(String roleName) {
 		return delegate.getCollectionMetadata( roleName );
 	}
 
 	@Override
 	public Map<String, ClassMetadata> getAllClassMetadata() {
 		return delegate.getAllClassMetadata();
 	}
 
 	@Override
 	public Map getAllCollectionMetadata() {
 		return delegate.getAllCollectionMetadata();
 	}
 
 	@Override
 	public Statistics getStatistics() {
 		return delegate.getStatistics();
 	}
 
 	@Override
 	public void close() throws HibernateException {
 		delegate.close();
 	}
 
 	@Override
 	public boolean isClosed() {
 		return delegate.isClosed();
 	}
 
 	@Override
 	public Cache getCache() {
 		return delegate.getCache();
 	}
 
 	@Override
 	public Set getDefinedFilterNames() {
 		return delegate.getDefinedFilterNames();
 	}
 
 	@Override
 	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
 		return delegate.getFilterDefinition( filterName );
 	}
 
 	@Override
 	public boolean containsFetchProfileDefinition(String name) {
 		return delegate.containsFetchProfileDefinition( name );
 	}
 
 	@Override
 	public TypeHelper getTypeHelper() {
 		return delegate.getTypeHelper();
 	}
 
 	@Override
 	public TypeResolver getTypeResolver() {
 		return delegate.getTypeResolver();
 	}
 
 	@Override
 	public Properties getProperties() {
 		return delegate.getProperties();
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(String entityName) throws MappingException {
 		return delegate.getEntityPersister( entityName );
 	}
 
 	@Override
 	public Map<String, EntityPersister> getEntityPersisters() {
 		return delegate.getEntityPersisters();
 	}
 
 	@Override
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
 		return delegate.getCollectionPersister( role );
 	}
 
 	@Override
 	public Map<String, CollectionPersister> getCollectionPersisters() {
 		return delegate.getCollectionPersisters();
 	}
 
 	@Override
 	public JdbcServices getJdbcServices() {
 		return delegate.getJdbcServices();
 	}
 
 	@Override
 	public Dialect getDialect() {
 		return delegate.getDialect();
 	}
 
 	@Override
 	public Interceptor getInterceptor() {
 		return delegate.getInterceptor();
 	}
 
 	@Override
 	public QueryPlanCache getQueryPlanCache() {
 		return delegate.getQueryPlanCache();
 	}
 
 	@Override
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		return delegate.getReturnTypes( queryString );
 	}
 
 	@Override
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		return delegate.getReturnAliases( queryString );
 	}
 
 	@Override
 	public String[] getImplementors(String className) throws MappingException {
 		return delegate.getImplementors( className );
 	}
 
 	@Override
 	public String getImportedClassName(String name) {
 		return delegate.getImportedClassName( name );
 	}
 
 	@Override
 	public QueryCache getQueryCache() {
 		return delegate.getQueryCache();
 	}
 
 	@Override
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		return delegate.getQueryCache( regionName );
 	}
 
 	@Override
 	public UpdateTimestampsCache getUpdateTimestampsCache() {
 		return delegate.getUpdateTimestampsCache();
 	}
 
 	@Override
 	public StatisticsImplementor getStatisticsImplementor() {
 		return delegate.getStatisticsImplementor();
 	}
 
 	@Override
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return delegate.getNamedQuery( queryName );
 	}
 
 	@Override
 	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
 		delegate.registerNamedQueryDefinition( name, definition );
 	}
 
 	@Override
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return delegate.getNamedSQLQuery( queryName );
 	}
 
 	@Override
 	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
 		delegate.registerNamedQueryDefinition( name, definition );
 	}
 
 	@Override
 	public ResultSetMappingDefinition getResultSetMapping(String name) {
 		return delegate.getResultSetMapping( name );
 	}
 
 	@Override
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
 		return delegate.getIdentifierGenerator( rootEntityName );
 	}
 
 	@Override
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return delegate.getSecondLevelCacheRegion( regionName );
 	}
 
 	@Override
+	public RegionAccessStrategy getSecondLevelCacheRegionAccessStrategy(String regionName) {
+		return delegate.getSecondLevelCacheRegionAccessStrategy(regionName);
+	}
+
+	@Override
 	public Region getNaturalIdCacheRegion(String regionName) {
 		return delegate.getNaturalIdCacheRegion( regionName );
 	}
 
 	@Override
+	public RegionAccessStrategy getNaturalIdCacheRegionAccessStrategy(String regionName) {
+		return delegate.getNaturalIdCacheRegionAccessStrategy(regionName);
+	}
+
+	@Override
 	public Map getAllSecondLevelCacheRegions() {
 		return delegate.getAllSecondLevelCacheRegions();
 	}
 
 	@Override
 	public SQLExceptionConverter getSQLExceptionConverter() {
 		return delegate.getSQLExceptionConverter();
 	}
 
 	@Override
 	public SqlExceptionHelper getSQLExceptionHelper() {
 		return delegate.getSQLExceptionHelper();
 	}
 
 	@Override
 	public Settings getSettings() {
 		return delegate.getSettings();
 	}
 
 	@Override
 	public Session openTemporarySession() throws HibernateException {
 		return delegate.openTemporarySession();
 	}
 
 	@Override
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
 		return delegate.getCollectionRolesByEntityParticipant( entityName );
 	}
 
 	@Override
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return delegate.getEntityNotFoundDelegate();
 	}
 
 	@Override
 	public SQLFunctionRegistry getSqlFunctionRegistry() {
 		return delegate.getSqlFunctionRegistry();
 	}
 
 	@Override
 	public FetchProfile getFetchProfile(String name) {
 		return delegate.getFetchProfile( name );
 	}
 
 	@Override
 	public ServiceRegistryImplementor getServiceRegistry() {
 		return delegate.getServiceRegistry();
 	}
 
 	@Override
 	public void addObserver(SessionFactoryObserver observer) {
 		delegate.addObserver( observer );
 	}
 
 	@Override
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 		return delegate.getCustomEntityDirtinessStrategy();
 	}
 
 	@Override
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return delegate.getCurrentTenantIdentifierResolver();
 	}
 
 	@Override
 	public NamedQueryRepository getNamedQueryRepository() {
 		return delegate.getNamedQueryRepository();
 	}
 
 	@Override
 	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
 		return delegate.iterateEntityNameResolvers();
 	}
 
 	@Override
 	public EntityPersister locateEntityPersister(Class byClass) {
 		return delegate.locateEntityPersister( byClass );
 	}
 
 	@Override
 	public EntityPersister locateEntityPersister(String byName) {
 		return delegate.locateEntityPersister( byName );
 	}
 
 	@Override
 	public DeserializationResolver getDeserializationResolver() {
 		return delegate.getDeserializationResolver();
 	}
 
 	@Override
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return delegate.getIdentifierGeneratorFactory();
 	}
 
 	@Override
 	public Type getIdentifierType(String className) throws MappingException {
 		return delegate.getIdentifierType( className );
 	}
 
 	@Override
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return delegate.getIdentifierPropertyName( className );
 	}
 
 	@Override
 	public Type getReferencedPropertyType(String className, String propertyName) throws MappingException {
 		return delegate.getReferencedPropertyType( className, propertyName );
 	}
 
 	@Override
 	public Reference getReference() throws NamingException {
 		return delegate.getReference();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
index a889d82dc4..6bb7ae38fe 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
@@ -1,315 +1,330 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.spi;
 
 import java.io.Serializable;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
+import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.cfg.Settings;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.NamedQueryRepository;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Defines the internal contract between the <tt>SessionFactory</tt> and other parts of
  * Hibernate such as implementors of <tt>Type</tt>.
  *
  * @see org.hibernate.SessionFactory
  * @see org.hibernate.internal.SessionFactoryImpl
  * @author Gavin King
  */
 public interface SessionFactoryImplementor extends Mapping, SessionFactory {
 	@Override
 	SessionBuilderImplementor withOptions();
 
 	/**
 	 * Retrieve the {@link Type} resolver associated with this factory.
 	 *
 	 * @return The type resolver
 	 */
 	TypeResolver getTypeResolver();
 
 	/**
 	 * Get a copy of the Properties used to configure this session factory.
 	 *
 	 * @return The properties.
 	 */
 	Properties getProperties();
 
 	/**
 	 * Get the persister for the named entity
 	 *
 	 * @param entityName The name of the entity for which to retrieve the persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that name.
 	 */
 	EntityPersister getEntityPersister(String entityName) throws MappingException;
 
 	/**
 	 * Get all entity persisters as a Map, which entity name its the key and the persister is the value.
 	 *
 	 * @return The Map contains all entity persisters.
 	 */
 	Map<String,EntityPersister> getEntityPersisters();
 
 	/**
 	 * Get the persister object for a collection role.
 	 *
 	 * @param role The role (name) of the collection for which to retrieve the
 	 * persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that role.
 	 */
 	CollectionPersister getCollectionPersister(String role) throws MappingException;
 
 	/**
 	 * Get all collection persisters as a Map, which collection role as the key and the persister is the value.
 	 *
 	 * @return The Map contains all collection persisters.
 	 */
 	Map<String, CollectionPersister> getCollectionPersisters();
 
 	/**
 	 * Get the JdbcServices.
 	 *
 	 * @return the JdbcServices
 	 *
 	 * @deprecated since 5.0; use {@link #getServiceRegistry()} instead to locate the JdbcServices
 	 */
 	@Deprecated
 	JdbcServices getJdbcServices();
 
 	/**
 	 * Get the SQL dialect.
 	 * <p/>
 	 * Shorthand for {@code getJdbcServices().getDialect()}
 	 *
 	 * @return The dialect
 	 */
 	Dialect getDialect();
 
 	/**
 	 * Get the factory scoped interceptor for this factory.
 	 *
 	 * @return The factory scope interceptor, or null if none.
 	 */
 	Interceptor getInterceptor();
 
 	QueryPlanCache getQueryPlanCache();
 
 	/**
 	 * Get the return types of a query
 	 */
 	Type[] getReturnTypes(String queryString) throws HibernateException;
 
 	/**
 	 * Get the return aliases of a query
 	 */
 	String[] getReturnAliases(String queryString) throws HibernateException;
 
 	/**
 	 * Get the names of all persistent classes that implement/extend the given interface/class
 	 */
 	String[] getImplementors(String className) throws MappingException;
 	/**
 	 * Get a class name, using query language imports
 	 */
 	String getImportedClassName(String name);
 
 	/**
 	 * Get the default query cache
 	 */
 	QueryCache getQueryCache();
 	/**
 	 * Get a particular named query cache, or the default cache
 	 * @param regionName the name of the cache region, or null for the default query cache
 	 * @return the existing cache, or a newly created cache if none by that region name
 	 */
 	QueryCache getQueryCache(String regionName) throws HibernateException;
 
 	/**
 	 * Get the cache of table update timestamps
 	 */
 	UpdateTimestampsCache getUpdateTimestampsCache();
 	/**
 	 * Statistics SPI
 	 */
 	StatisticsImplementor getStatisticsImplementor();
 
 	NamedQueryDefinition getNamedQuery(String queryName);
 
 	void registerNamedQueryDefinition(String name, NamedQueryDefinition definition);
 
 	NamedSQLQueryDefinition getNamedSQLQuery(String queryName);
 
 	void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition);
 
 	ResultSetMappingDefinition getResultSetMapping(String name);
 
 	/**
 	 * Get the identifier generator for the hierarchy
 	 */
 	IdentifierGenerator getIdentifierGenerator(String rootEntityName);
 
 	/**
 	 * Get a named second-level cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	Region getSecondLevelCacheRegion(String regionName);
+
+	/**
+	 * Get access strategy to second-level cache region
+	 * @param regionName
+	 * @return
+	 */
+	RegionAccessStrategy getSecondLevelCacheRegionAccessStrategy(String regionName);
 	
 	/**
 	 * Get a named naturalId cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	Region getNaturalIdCacheRegion(String regionName);
 
 	/**
+	 * Get access strategy to naturalId cache region
+	 * @param regionName
+	 * @return
+	 */
+	RegionAccessStrategy getNaturalIdCacheRegionAccessStrategy(String regionName);
+
+	/**
 	 * Get a map of all the second level cache regions currently maintained in
 	 * this session factory.  The map is structured with the region name as the
 	 * key and the {@link Region} instances as the values.
 	 *
 	 * @return The map of regions
 	 */
 	Map getAllSecondLevelCacheRegions();
 
 	/**
 	 * Retrieves the SQLExceptionConverter in effect for this SessionFactory.
 	 *
 	 * @return The SQLExceptionConverter for this SessionFactory.
 	 *
 	 * @deprecated since 5.0; use {@link JdbcServices#getSqlExceptionHelper()} ->
 	 * {@link SqlExceptionHelper#getSqlExceptionConverter()} instead as obtained from {@link #getServiceRegistry()}
 	 */
 	@Deprecated
 	SQLExceptionConverter getSQLExceptionConverter();
 
 	/**
 	 * Retrieves the SqlExceptionHelper in effect for this SessionFactory.
 	 *
 	 * @return The SqlExceptionHelper for this SessionFactory.
 	 *
 	 * @deprecated since 5.0; use {@link JdbcServices#getSqlExceptionHelper()} instead as
 	 * obtained from {@link #getServiceRegistry()}
 	 */
 	@Deprecated
 	SqlExceptionHelper getSQLExceptionHelper();
 
 	/**
 	 * @deprecated since 5.0; use {@link #getSessionFactoryOptions()} instead
 	 */
 	@Deprecated
 	@SuppressWarnings("deprecation")
 	Settings getSettings();
 
 	/**
 	 * Get a non-transactional "current" session for Hibernate EntityManager
 	 */
 	Session openTemporarySession() throws HibernateException;
 
 	/**
 	 * Retrieves a set of all the collection roles in which the given entity
 	 * is a participant, as either an index or an element.
 	 *
 	 * @param entityName The entity name for which to get the collection roles.
 	 * @return set of all the collection roles in which the given entityName participates.
 	 */
 	Set<String> getCollectionRolesByEntityParticipant(String entityName);
 
 	EntityNotFoundDelegate getEntityNotFoundDelegate();
 
 	SQLFunctionRegistry getSqlFunctionRegistry();
 
 	/**
 	 * Retrieve fetch profile by name.
 	 *
 	 * @param name The name of the profile to retrieve.
 	 * @return The profile definition
 	 */
 	FetchProfile getFetchProfile(String name);
 
 	ServiceRegistryImplementor getServiceRegistry();
 
 	void addObserver(SessionFactoryObserver observer);
 
 	CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();
 
 	CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();
 
 	/**
 	 * Provides access to the named query repository
 	 *
 	 * @return The repository for named query definitions
 	 */
 	NamedQueryRepository getNamedQueryRepository();
 
 	Iterable<EntityNameResolver> iterateEntityNameResolvers();
 
 	/**
 	 * Locate an EntityPersister by the entity class.  The passed Class might refer to either
 	 * the entity name directly, or it might name a proxy interface for the entity.  This
 	 * method accounts for both, preferring the direct named entity name.
 	 *
 	 * @param byClass The concrete Class or proxy interface for the entity to locate the persister for.
 	 *
 	 * @return The located EntityPersister, never {@code null}
 	 *
 	 * @throws org.hibernate.UnknownEntityTypeException If a matching EntityPersister cannot be located
 	 */
 	EntityPersister locateEntityPersister(Class byClass);
 
 	/**
 	 * Locate the entity persister by name.
 	 *
 	 * @param byName The entity name
 	 *
 	 * @return The located EntityPersister, never {@code null}
 	 *
 	 * @throws org.hibernate.UnknownEntityTypeException If a matching EntityPersister cannot be located
 	 */
 	EntityPersister locateEntityPersister(String byName);
 
 	/**
 	 * Contract for resolving this SessionFactory on deserialization
 	 */
 	interface DeserializationResolver<T extends SessionFactoryImplementor> extends Serializable {
 		T resolve();
 	}
 
 	DeserializationResolver getDeserializationResolver();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractLockUpgradeEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractLockUpgradeEventListener.java
index bcf05f8147..66169048d2 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractLockUpgradeEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractLockUpgradeEventListener.java
@@ -1,97 +1,96 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.event.internal;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.ObjectDeletedException;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.jboss.logging.Logger;
 
 /**
  * A convenience base class for listeners that respond to requests to perform a
  * pessimistic lock upgrade on an entity.
  *
  * @author Gavin King
  */
 public abstract class AbstractLockUpgradeEventListener extends AbstractReassociateEventListener {
 	private static final Logger log = CoreLogging.logger( AbstractLockUpgradeEventListener.class );
 
 	/**
 	 * Performs a pessimistic lock upgrade on a given entity, if needed.
 	 *
 	 * @param object The entity for which to upgrade the lock.
 	 * @param entry The entity's EntityEntry instance.
 	 * @param lockOptions contains the requested lock mode.
 	 * @param source The session which is the source of the event being processed.
 	 */
 	protected void upgradeLock(Object object, EntityEntry entry, LockOptions lockOptions, EventSource source) {
 
 		LockMode requestedLockMode = lockOptions.getLockMode();
 		if ( requestedLockMode.greaterThan( entry.getLockMode() ) ) {
 			// The user requested a "greater" (i.e. more restrictive) form of
 			// pessimistic lock
 
 			if ( entry.getStatus() != Status.MANAGED ) {
 				throw new ObjectDeletedException(
 						"attempted to lock a deleted instance",
 						entry.getId(),
 						entry.getPersister().getEntityName()
 				);
 			}
 
 			final EntityPersister persister = entry.getPersister();
 
 			if ( log.isTraceEnabled() ) {
 				log.tracev(
 						"Locking {0} in mode: {1}",
 						MessageHelper.infoString( persister, entry.getId(), source.getFactory() ),
 						requestedLockMode
 				);
 			}
 
 			final boolean cachingEnabled = persister.hasCache();
 			SoftLock lock = null;
-			EntityCacheKey ck = null;
+			Object ck = null;
 			try {
 				if ( cachingEnabled ) {
 					EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
 					ck = cache.generateCacheKey( entry.getId(), persister, source.getFactory(), source.getTenantIdentifier() );
 					lock = cache.lockItem( ck, entry.getVersion() );
 				}
 
 				if ( persister.isVersioned() && requestedLockMode == LockMode.FORCE  ) {
 					// todo : should we check the current isolation mode explicitly?
 					Object nextVersion = persister.forceVersionIncrement(
 							entry.getId(), entry.getVersion(), source
 					);
 					entry.forceLocked( object, nextVersion );
 				}
 				else {
 					persister.lock( entry.getId(), entry.getVersion(), object, lockOptions, source );
 				}
 				entry.setLockMode(requestedLockMode);
 			}
 			finally {
 				// the database now holds a lock + the object is flushed from the cache,
 				// so release the soft lock
 				if ( cachingEnabled ) {
 					persister.getCacheAccessStrategy().unlockItem( ck, lock );
 				}
 			}
 
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java
index 55bc6e10ff..2118ecd20b 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java
@@ -1,150 +1,149 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
-import org.hibernate.cache.spi.CollectionCacheKey;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CollectionCacheEntry;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.CacheHelper;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.InitializeCollectionEvent;
 import org.hibernate.event.spi.InitializeCollectionEventListener;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * @author Gavin King
  */
 public class DefaultInitializeCollectionEventListener implements InitializeCollectionEventListener {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( DefaultInitializeCollectionEventListener.class );
 
 	/**
 	 * called by a collection that wants to initialize itself
 	 */
 	public void onInitializeCollection(InitializeCollectionEvent event) throws HibernateException {
 		PersistentCollection collection = event.getCollection();
 		SessionImplementor source = event.getSession();
 
 		CollectionEntry ce = source.getPersistenceContext().getCollectionEntry( collection );
 		if ( ce == null ) {
 			throw new HibernateException( "collection was evicted" );
 		}
 		if ( !collection.wasInitialized() ) {
 			final boolean traceEnabled = LOG.isTraceEnabled();
 			if ( traceEnabled ) {
 				LOG.tracev(
 						"Initializing collection {0}",
 						MessageHelper.collectionInfoString(
 								ce.getLoadedPersister(),
 								collection,
 								ce.getLoadedKey(),
 								source
 						)
 				);
 				LOG.trace( "Checking second-level cache" );
 			}
 
 			final boolean foundInCache = initializeCollectionFromCache(
 					ce.getLoadedKey(),
 					ce.getLoadedPersister(),
 					collection,
 					source
 			);
 
 			if ( foundInCache ) {
 				if ( traceEnabled ) {
 					LOG.trace( "Collection initialized from cache" );
 				}
 			}
 			else {
 				if ( traceEnabled ) {
 					LOG.trace( "Collection not cached" );
 				}
 				ce.getLoadedPersister().initialize( ce.getLoadedKey(), source );
 				if ( traceEnabled ) {
 					LOG.trace( "Collection initialized" );
 				}
 
 				if ( source.getFactory().getStatistics().isStatisticsEnabled() ) {
 					source.getFactory().getStatisticsImplementor().fetchCollection(
 							ce.getLoadedPersister().getRole()
 					);
 				}
 			}
 		}
 	}
 
 	/**
 	 * Try to initialize a collection from the cache
 	 *
 	 * @param id The id of the collection of initialize
 	 * @param persister The collection persister
 	 * @param collection The collection to initialize
 	 * @param source The originating session
 	 *
 	 * @return true if we were able to initialize the collection from the cache;
 	 *         false otherwise.
 	 */
 	private boolean initializeCollectionFromCache(
 			Serializable id,
 			CollectionPersister persister,
 			PersistentCollection collection,
 			SessionImplementor source) {
 
 		if ( !source.getLoadQueryInfluencers().getEnabledFilters().isEmpty()
 				&& persister.isAffectedByEnabledFilters( source ) ) {
 			LOG.trace( "Disregarding cached version (if any) of collection due to enabled filters" );
 			return false;
 		}
 
 		final boolean useCache = persister.hasCache() && source.getCacheMode().isGetEnabled();
 
 		if ( !useCache ) {
 			return false;
 		}
 
 		final SessionFactoryImplementor factory = source.getFactory();
 		final CollectionRegionAccessStrategy cacheAccessStrategy = persister.getCacheAccessStrategy();
-		final CollectionCacheKey ck = cacheAccessStrategy.generateCacheKey( id, persister, factory, source.getTenantIdentifier() );
+		final Object ck = cacheAccessStrategy.generateCacheKey( id, persister, factory, source.getTenantIdentifier() );
 		final Object ce = CacheHelper.fromSharedCache( source, ck, persister.getCacheAccessStrategy() );
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			if ( ce == null ) {
 				factory.getStatisticsImplementor()
 						.secondLevelCacheMiss( cacheAccessStrategy.getRegion().getName() );
 			}
 			else {
 				factory.getStatisticsImplementor()
 						.secondLevelCacheHit( cacheAccessStrategy.getRegion().getName() );
 			}
 		}
 
 		if ( ce == null ) {
 			return false;
 		}
 
 		CollectionCacheEntry cacheEntry = (CollectionCacheEntry) persister.getCacheEntryStructure().destructure(
 				ce,
 				factory
 		);
 
 		final PersistenceContext persistenceContext = source.getPersistenceContext();
 		cacheEntry.assemble( collection, persister, persistenceContext.getCollectionOwner( id, persister ) );
 		persistenceContext.getCollectionEntry( collection ).postInitialize( collection );
 		// addInitializedCollection(collection, persister, id);
 		return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
index 23d8dfdc08..135ea67787 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
@@ -1,855 +1,854 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.WrongClassException;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.engine.internal.CacheHelper;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.LoadEvent;
 import org.hibernate.event.spi.LoadEventListener;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PostLoadEventListener;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.type.EmbeddedComponentType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
  * Defines the default load event listeners used by hibernate for loading entities
  * in response to generated load events.
  *
  * @author Steve Ebersole
  */
 public class DefaultLoadEventListener extends AbstractLockUpgradeEventListener implements LoadEventListener {
 	public static final Object REMOVED_ENTITY_MARKER = new Object();
 	public static final Object INCONSISTENT_RTN_CLASS_MARKER = new Object();
 	public static final LockMode DEFAULT_LOCK_MODE = LockMode.NONE;
 
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( DefaultLoadEventListener.class );
 
 
 	/**
 	 * Handle the given load event.
 	 *
 	 * @param event The load event to be handled.
 	 *
 	 * @throws HibernateException
 	 */
 	public void onLoad(LoadEvent event, LoadEventListener.LoadType loadType) throws HibernateException {
 		final SessionImplementor source = event.getSession();
 
 		EntityPersister persister;
 		if ( event.getInstanceToLoad() != null ) {
 			persister = source.getEntityPersister(
 					null,
 					event.getInstanceToLoad()
 			);
 			//the load() which takes an entity does not pass an entityName
 			event.setEntityClassName( event.getInstanceToLoad().getClass().getName() );
 		}
 		else {
 			persister = source.getFactory().getEntityPersister( event.getEntityClassName() );
 		}
 
 		if ( persister == null ) {
 			throw new HibernateException( "Unable to locate persister: " + event.getEntityClassName() );
 		}
 
 		final Class idClass = persister.getIdentifierType().getReturnedClass();
 		if ( idClass != null && !idClass.isInstance( event.getEntityId() ) ) {
 			// we may have the kooky jpa requirement of allowing find-by-id where
 			// "id" is the "simple pk value" of a dependent objects parent.  This
 			// is part of its generally goofy "derived identity" "feature"
 			if ( persister.getEntityMetamodel().getIdentifierProperty().isEmbedded() ) {
 				final EmbeddedComponentType dependentIdType =
 						(EmbeddedComponentType) persister.getEntityMetamodel().getIdentifierProperty().getType();
 				if ( dependentIdType.getSubtypes().length == 1 ) {
 					final Type singleSubType = dependentIdType.getSubtypes()[0];
 					if ( singleSubType.isEntityType() ) {
 						final EntityType dependentParentType = (EntityType) singleSubType;
 						final Type dependentParentIdType = dependentParentType.getIdentifierOrUniqueKeyType( source.getFactory() );
 						if ( dependentParentIdType.getReturnedClass().isInstance( event.getEntityId() ) ) {
 							// yep that's what we have...
 							loadByDerivedIdentitySimplePkValue(
 									event,
 									loadType,
 									persister,
 									dependentIdType,
 									source.getFactory().getEntityPersister( dependentParentType.getAssociatedEntityName() )
 							);
 							return;
 						}
 					}
 				}
 			}
 			throw new TypeMismatchException(
 					"Provided id of the wrong type for class " + persister.getEntityName() + ". Expected: " + idClass
 							+ ", got " + event.getEntityId().getClass()
 			);
 		}
 
 		final EntityKey keyToLoad = source.generateEntityKey( event.getEntityId(), persister );
 
 		try {
 			if ( loadType.isNakedEntityReturned() ) {
 				//do not return a proxy!
 				//(this option indicates we are initializing a proxy)
 				event.setResult( load( event, persister, keyToLoad, loadType ) );
 			}
 			else {
 				//return a proxy if appropriate
 				if ( event.getLockMode() == LockMode.NONE ) {
 					event.setResult( proxyOrLoad( event, persister, keyToLoad, loadType ) );
 				}
 				else {
 					event.setResult( lockAndLoad( event, persister, keyToLoad, loadType, source ) );
 				}
 			}
 		}
 		catch (HibernateException e) {
 			LOG.unableToLoadCommand( e );
 			throw e;
 		}
 	}
 
 	private void loadByDerivedIdentitySimplePkValue(
 			LoadEvent event,
 			LoadEventListener.LoadType options,
 			EntityPersister dependentPersister,
 			EmbeddedComponentType dependentIdType,
 			EntityPersister parentPersister) {
 		final EntityKey parentEntityKey = event.getSession().generateEntityKey( event.getEntityId(), parentPersister );
 		final Object parent = doLoad( event, parentPersister, parentEntityKey, options );
 
 		final Serializable dependent = (Serializable) dependentIdType.instantiate( parent, event.getSession() );
 		dependentIdType.setPropertyValues( dependent, new Object[] {parent}, dependentPersister.getEntityMode() );
 		final EntityKey dependentEntityKey = event.getSession().generateEntityKey( dependent, dependentPersister );
 		event.setEntityId( dependent );
 
 		event.setResult( doLoad( event, dependentPersister, dependentEntityKey, options ) );
 	}
 
 	/**
 	 * Performs the load of an entity.
 	 *
 	 * @param event The initiating load request event
 	 * @param persister The persister corresponding to the entity to be loaded
 	 * @param keyToLoad The key of the entity to be loaded
 	 * @param options The defined load options
 	 *
 	 * @return The loaded entity.
 	 *
 	 * @throws HibernateException
 	 */
 	protected Object load(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options) {
 
 		if ( event.getInstanceToLoad() != null ) {
 			if ( event.getSession().getPersistenceContext().getEntry( event.getInstanceToLoad() ) != null ) {
 				throw new PersistentObjectException(
 						"attempted to load into an instance that was already associated with the session: " +
 								MessageHelper.infoString(
 										persister,
 										event.getEntityId(),
 										event.getSession().getFactory()
 								)
 				);
 			}
 			persister.setIdentifier( event.getInstanceToLoad(), event.getEntityId(), event.getSession() );
 		}
 
 		Object entity = doLoad( event, persister, keyToLoad, options );
 
 		boolean isOptionalInstance = event.getInstanceToLoad() != null;
 
 		if ( !options.isAllowNulls() || isOptionalInstance ) {
 			if ( entity == null ) {
 				event.getSession()
 						.getFactory()
 						.getEntityNotFoundDelegate()
 						.handleEntityNotFound( event.getEntityClassName(), event.getEntityId() );
 			}
 		}
 
 		if ( isOptionalInstance && entity != event.getInstanceToLoad() ) {
 			throw new NonUniqueObjectException( event.getEntityId(), event.getEntityClassName() );
 		}
 
 		return entity;
 	}
 
 	/**
 	 * Based on configured options, will either return a pre-existing proxy,
 	 * generate a new proxy, or perform an actual load.
 	 *
 	 * @param event The initiating load request event
 	 * @param persister The persister corresponding to the entity to be loaded
 	 * @param keyToLoad The key of the entity to be loaded
 	 * @param options The defined load options
 	 *
 	 * @return The result of the proxy/load operation.
 	 */
 	protected Object proxyOrLoad(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options) {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Loading entity: {0}",
 					MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() )
 			);
 		}
 
 		// this class has no proxies (so do a shortcut)
 		if ( !persister.hasProxy() ) {
 			return load( event, persister, keyToLoad, options );
 		}
 
 		final PersistenceContext persistenceContext = event.getSession().getPersistenceContext();
 
 		// look for a proxy
 		Object proxy = persistenceContext.getProxy( keyToLoad );
 		if ( proxy != null ) {
 			return returnNarrowedProxy( event, persister, keyToLoad, options, persistenceContext, proxy );
 		}
 
 		if ( options.isAllowProxyCreation() ) {
 			return createProxyIfNecessary( event, persister, keyToLoad, options, persistenceContext );
 		}
 
 		// return a newly loaded object
 		return load( event, persister, keyToLoad, options );
 	}
 
 	/**
 	 * Given a proxy, initialize it and/or narrow it provided either
 	 * is necessary.
 	 *
 	 * @param event The initiating load request event
 	 * @param persister The persister corresponding to the entity to be loaded
 	 * @param keyToLoad The key of the entity to be loaded
 	 * @param options The defined load options
 	 * @param persistenceContext The originating session
 	 * @param proxy The proxy to narrow
 	 *
 	 * @return The created/existing proxy
 	 */
 	private Object returnNarrowedProxy(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options,
 			final PersistenceContext persistenceContext,
 			final Object proxy) {
 		LOG.trace( "Entity proxy found in session cache" );
 		LazyInitializer li = ( (HibernateProxy) proxy ).getHibernateLazyInitializer();
 		if ( li.isUnwrap() ) {
 			return li.getImplementation();
 		}
 		Object impl = null;
 		if ( !options.isAllowProxyCreation() ) {
 			impl = load( event, persister, keyToLoad, options );
 			if ( impl == null ) {
 				event.getSession()
 						.getFactory()
 						.getEntityNotFoundDelegate()
 						.handleEntityNotFound( persister.getEntityName(), keyToLoad.getIdentifier() );
 			}
 		}
 		return persistenceContext.narrowProxy( proxy, persister, keyToLoad, impl );
 	}
 
 	/**
 	 * If there is already a corresponding proxy associated with the
 	 * persistence context, return it; otherwise create a proxy, associate it
 	 * with the persistence context, and return the just-created proxy.
 	 *
 	 * @param event The initiating load request event
 	 * @param persister The persister corresponding to the entity to be loaded
 	 * @param keyToLoad The key of the entity to be loaded
 	 * @param options The defined load options
 	 * @param persistenceContext The originating session
 	 *
 	 * @return The created/existing proxy
 	 */
 	private Object createProxyIfNecessary(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options,
 			final PersistenceContext persistenceContext) {
 		Object existing = persistenceContext.getEntity( keyToLoad );
 		if ( existing != null ) {
 			// return existing object or initialized proxy (unless deleted)
 			LOG.trace( "Entity found in session cache" );
 			if ( options.isCheckDeleted() ) {
 				EntityEntry entry = persistenceContext.getEntry( existing );
 				Status status = entry.getStatus();
 				if ( status == Status.DELETED || status == Status.GONE ) {
 					return null;
 				}
 			}
 			return existing;
 		}
 		LOG.trace( "Creating new proxy for entity" );
 		// return new uninitialized proxy
 		Object proxy = persister.createProxy( event.getEntityId(), event.getSession() );
 		persistenceContext.getBatchFetchQueue().addBatchLoadableEntityKey( keyToLoad );
 		persistenceContext.addProxy( keyToLoad, proxy );
 		return proxy;
 	}
 
 	/**
 	 * If the class to be loaded has been configured with a cache, then lock
 	 * given id in that cache and then perform the load.
 	 *
 	 * @param event The initiating load request event
 	 * @param persister The persister corresponding to the entity to be loaded
 	 * @param keyToLoad The key of the entity to be loaded
 	 * @param options The defined load options
 	 * @param source The originating session
 	 *
 	 * @return The loaded entity
 	 *
 	 * @throws HibernateException
 	 */
 	protected Object lockAndLoad(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options,
 			final SessionImplementor source) {
 		SoftLock lock = null;
-		final EntityCacheKey ck;
+		final Object ck;
 		final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
 		if ( persister.hasCache() ) {
 			ck = cache.generateCacheKey(
 					event.getEntityId(),
 					persister,
 					source.getFactory(),
 					source.getTenantIdentifier()
 			);
 			lock = persister.getCacheAccessStrategy().lockItem( ck, null );
 		}
 		else {
 			ck = null;
 		}
 
 		Object entity;
 		try {
 			entity = load( event, persister, keyToLoad, options );
 		}
 		finally {
 			if ( persister.hasCache() ) {
 				cache.unlockItem( ck, lock );
 			}
 		}
 
 		return event.getSession().getPersistenceContext().proxyFor( persister, keyToLoad, entity );
 	}
 
 
 	/**
 	 * Coordinates the efforts to load a given entity.  First, an attempt is
 	 * made to load the entity from the session-level cache.  If not found there,
 	 * an attempt is made to locate it in second-level cache.  Lastly, an
 	 * attempt is made to load it directly from the datasource.
 	 *
 	 * @param event The load event
 	 * @param persister The persister for the entity being requested for load
 	 * @param keyToLoad The EntityKey representing the entity to be loaded.
 	 * @param options The load options.
 	 *
 	 * @return The loaded entity, or null.
 	 */
 	protected Object doLoad(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options) {
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( traceEnabled ) {
 			LOG.tracev(
 					"Attempting to resolve: {0}",
 					MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() )
 			);
 		}
 
 		Object entity = loadFromSessionCache( event, keyToLoad, options );
 		if ( entity == REMOVED_ENTITY_MARKER ) {
 			LOG.debug( "Load request found matching entity in context, but it is scheduled for removal; returning null" );
 			return null;
 		}
 		if ( entity == INCONSISTENT_RTN_CLASS_MARKER ) {
 			LOG.debug(
 					"Load request found matching entity in context, but the matched entity was of an inconsistent return type; returning null"
 			);
 			return null;
 		}
 		if ( entity != null ) {
 			if ( traceEnabled ) {
 				LOG.tracev(
 						"Resolved object in session cache: {0}",
 						MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() )
 				);
 			}
 			return entity;
 		}
 
 		entity = loadFromSecondLevelCache( event, persister, options );
 		if ( entity != null ) {
 			if ( traceEnabled ) {
 				LOG.tracev(
 						"Resolved object in second-level cache: {0}",
 						MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() )
 				);
 			}
 		}
 		else {
 			if ( traceEnabled ) {
 				LOG.tracev(
 						"Object not resolved in any cache: {0}",
 						MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() )
 				);
 			}
 			entity = loadFromDatasource( event, persister, keyToLoad, options );
 		}
 
 		if ( entity != null && persister.hasNaturalIdentifier() ) {
 			event.getSession().getPersistenceContext().getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					event.getEntityId(),
 					event.getSession().getPersistenceContext().getNaturalIdHelper().extractNaturalIdValues(
 							entity,
 							persister
 					)
 			);
 		}
 
 
 		return entity;
 	}
 
 	/**
 	 * Performs the process of loading an entity from the configured
 	 * underlying datasource.
 	 *
 	 * @param event The load event
 	 * @param persister The persister for the entity being requested for load
 	 * @param keyToLoad The EntityKey representing the entity to be loaded.
 	 * @param options The load options.
 	 *
 	 * @return The object loaded from the datasource, or null if not found.
 	 */
 	protected Object loadFromDatasource(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options) {
 		final SessionImplementor source = event.getSession();
 		Object entity = persister.load(
 				event.getEntityId(),
 				event.getInstanceToLoad(),
 				event.getLockOptions(),
 				source
 		);
 
 		if ( event.isAssociationFetch() && source.getFactory().getStatistics().isStatisticsEnabled() ) {
 			source.getFactory().getStatisticsImplementor().fetchEntity( event.getEntityClassName() );
 		}
 
 		return entity;
 	}
 
 	/**
 	 * Attempts to locate the entity in the session-level cache.
 	 * <p/>
 	 * If allowed to return nulls, then if the entity happens to be found in
 	 * the session cache, we check the entity type for proper handling
 	 * of entity hierarchies.
 	 * <p/>
 	 * If checkDeleted was set to true, then if the entity is found in the
 	 * session-level cache, it's current status within the session cache
 	 * is checked to see if it has previously been scheduled for deletion.
 	 *
 	 * @param event The load event
 	 * @param keyToLoad The EntityKey representing the entity to be loaded.
 	 * @param options The load options.
 	 *
 	 * @return The entity from the session-level cache, or null.
 	 *
 	 * @throws HibernateException Generally indicates problems applying a lock-mode.
 	 */
 	protected Object loadFromSessionCache(
 			final LoadEvent event,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options) throws HibernateException {
 
 		SessionImplementor session = event.getSession();
 		Object old = session.getEntityUsingInterceptor( keyToLoad );
 
 		if ( old != null ) {
 			// this object was already loaded
 			EntityEntry oldEntry = session.getPersistenceContext().getEntry( old );
 			if ( options.isCheckDeleted() ) {
 				Status status = oldEntry.getStatus();
 				if ( status == Status.DELETED || status == Status.GONE ) {
 					return REMOVED_ENTITY_MARKER;
 				}
 			}
 			if ( options.isAllowNulls() ) {
 				final EntityPersister persister = event.getSession()
 						.getFactory()
 						.getEntityPersister( keyToLoad.getEntityName() );
 				if ( !persister.isInstance( old ) ) {
 					return INCONSISTENT_RTN_CLASS_MARKER;
 				}
 			}
 			upgradeLock( old, oldEntry, event.getLockOptions(), event.getSession() );
 		}
 
 		return old;
 	}
 
 	/**
 	 * Attempts to load the entity from the second-level cache.
 	 *
 	 * @param event The load event
 	 * @param persister The persister for the entity being requested for load
 	 * @param options The load options.
 	 *
 	 * @return The entity from the second-level cache, or null.
 	 */
 	protected Object loadFromSecondLevelCache(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final LoadEventListener.LoadType options) {
 
 		final SessionImplementor source = event.getSession();
 		final boolean useCache = persister.hasCache()
 				&& source.getCacheMode().isGetEnabled()
 				&& event.getLockMode().lessThan( LockMode.READ );
 
 		if ( !useCache ) {
 			// we can't use cache here
 			return null;
 		}
 
 		final SessionFactoryImplementor factory = source.getFactory();
 		final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-		final EntityCacheKey ck = cache.generateCacheKey(
+		final Object ck = cache.generateCacheKey(
 				event.getEntityId(),
 				persister,
 				factory,
 				source.getTenantIdentifier()
 		);
 
 		final Object ce = CacheHelper.fromSharedCache( source, ck, persister.getCacheAccessStrategy() );
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			if ( ce == null ) {
 				factory.getStatisticsImplementor().secondLevelCacheMiss(
 						cache.getRegion().getName()
 				);
 			}
 			else {
 				factory.getStatisticsImplementor().secondLevelCacheHit(
 						cache.getRegion().getName()
 				);
 			}
 		}
 
 		if ( ce == null ) {
 			// nothing was found in cache
 			return null;
 		}
 
 		CacheEntry entry = (CacheEntry) persister.getCacheEntryStructure().destructure( ce, factory );
 		Object entity = convertCacheEntryToEntity( entry, event.getEntityId(), persister, event );
 		
 		if ( !persister.isInstance( entity ) ) {
 			throw new WrongClassException(
 					"loaded object was of wrong class " + entity.getClass(),
 					event.getEntityId(),
 					persister.getEntityName()
 				);
 		}
 		
 		return entity;
 	}
 
 	private Object convertCacheEntryToEntity(
 			CacheEntry entry,
 			Serializable entityId,
 			EntityPersister persister,
 			LoadEvent event) {
 
 		final EventSource session = event.getSession();
 		final SessionFactoryImplementor factory = session.getFactory();
 		final EntityPersister subclassPersister;
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"Converting second-level cache entry [%s] into entity : %s",
 					entry,
 					MessageHelper.infoString( persister, entityId, factory )
 			);
 		}
 
 		final Object entity;
 		if ( entry.isReferenceEntry() ) {
 			final Object optionalObject = event.getInstanceToLoad();
 			if ( optionalObject != null ) {
 				throw new HibernateException(
 						String.format(
 								"Attempt to load entity [%s] from cache using provided object instance, but cache " +
 										"is storing references",
 								MessageHelper.infoString( persister, entityId, factory )
 						)
 				);
 			}
 
 			ReferenceCacheEntryImpl referenceCacheEntry = (ReferenceCacheEntryImpl) entry;
 			entity = referenceCacheEntry.getReference();
 			if ( entity == null ) {
 				throw new IllegalStateException(
 						"Reference cache entry contained null : " + MessageHelper.infoString(
 								persister,
 								entityId,
 								factory
 						)
 				);
 			}
 			subclassPersister = referenceCacheEntry.getSubclassPersister();
 		}
 		else {
 			subclassPersister = factory.getEntityPersister( entry.getSubclass() );
 			final Object optionalObject = event.getInstanceToLoad();
 			entity = optionalObject == null
 					? session.instantiate( subclassPersister, entityId )
 					: optionalObject;
 		}
 
 		// make it circular-reference safe
 		final EntityKey entityKey = session.generateEntityKey( entityId, subclassPersister );
 		TwoPhaseLoad.addUninitializedCachedEntity(
 				entityKey,
 				entity,
 				subclassPersister,
 				LockMode.NONE,
 				entry.areLazyPropertiesUnfetched(),
 				entry.getVersion(),
 				session
 		);
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final Object[] values;
 		final Object version;
 		final boolean isReadOnly;
 		if ( entry.isReferenceEntry() ) {
 			values = null;
 			version = null;
 			isReadOnly = true;
 		}
 		else {
 			final Type[] types = subclassPersister.getPropertyTypes();
 			// initializes the entity by (desired) side-effect
 			values = ( (StandardCacheEntryImpl) entry ).assemble(
 					entity, entityId, subclassPersister, session.getInterceptor(), session
 			);
 			if ( ( (StandardCacheEntryImpl) entry ).isDeepCopyNeeded() ) {
 				TypeHelper.deepCopy(
 						values,
 						types,
 						subclassPersister.getPropertyUpdateability(),
 						values,
 						session
 				);
 			}
 			version = Versioning.getVersion( values, subclassPersister );
 			LOG.tracef( "Cached Version : %s", version );
 
 			final Object proxy = persistenceContext.getProxy( entityKey );
 			if ( proxy != null ) {
 				// there is already a proxy for this impl
 				// only set the status to read-only if the proxy is read-only
 				isReadOnly = ( (HibernateProxy) proxy ).getHibernateLazyInitializer().isReadOnly();
 			}
 			else {
 				isReadOnly = session.isDefaultReadOnly();
 			}
 		}
 
 		persistenceContext.addEntry(
 				entity,
 				( isReadOnly ? Status.READ_ONLY : Status.MANAGED ),
 				values,
 				null,
 				entityId,
 				version,
 				LockMode.NONE,
 				true,
 				subclassPersister,
 				false,
 				entry.areLazyPropertiesUnfetched()
 		);
 		subclassPersister.afterInitialize( entity, entry.areLazyPropertiesUnfetched(), session );
 		persistenceContext.initializeNonLazyCollections();
 
 		//PostLoad is needed for EJB3
 		PostLoadEvent postLoadEvent = new PostLoadEvent( session )
 				.setEntity( entity )
 				.setId( entityId )
 				.setPersister( persister );
 
 		for ( PostLoadEventListener listener : postLoadEventListeners( session ) ) {
 			listener.onPostLoad( postLoadEvent );
 		}
 
 		return entity;
 	}
 
 	private Object assembleCacheEntry(
 			final StandardCacheEntryImpl entry,
 			final Serializable id,
 			final EntityPersister persister,
 			final LoadEvent event) throws HibernateException {
 
 		final Object optionalObject = event.getInstanceToLoad();
 		final EventSource session = event.getSession();
 		final SessionFactoryImplementor factory = session.getFactory();
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Assembling entity from second-level cache: {0}",
 					MessageHelper.infoString( persister, id, factory )
 			);
 		}
 
 		EntityPersister subclassPersister = factory.getEntityPersister( entry.getSubclass() );
 		Object result = optionalObject == null ?
 				session.instantiate( subclassPersister, id ) : optionalObject;
 
 		// make it circular-reference safe
 		final EntityKey entityKey = session.generateEntityKey( id, subclassPersister );
 		TwoPhaseLoad.addUninitializedCachedEntity(
 				entityKey,
 				result,
 				subclassPersister,
 				LockMode.NONE,
 				entry.areLazyPropertiesUnfetched(),
 				entry.getVersion(),
 				session
 		);
 
 		Type[] types = subclassPersister.getPropertyTypes();
 		Object[] values = entry.assemble(
 				result,
 				id,
 				subclassPersister,
 				session.getInterceptor(),
 				session
 		); // intializes result by side-effect
 		TypeHelper.deepCopy(
 				values,
 				types,
 				subclassPersister.getPropertyUpdateability(),
 				values,
 				session
 		);
 
 		Object version = Versioning.getVersion( values, subclassPersister );
 		LOG.tracev( "Cached Version: {0}", version );
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		boolean isReadOnly = session.isDefaultReadOnly();
 		if ( persister.isMutable() ) {
 			Object proxy = persistenceContext.getProxy( entityKey );
 			if ( proxy != null ) {
 				// there is already a proxy for this impl
 				// only set the status to read-only if the proxy is read-only
 				isReadOnly = ( (HibernateProxy) proxy ).getHibernateLazyInitializer().isReadOnly();
 			}
 		}
 		else {
 			isReadOnly = true;
 		}
 		persistenceContext.addEntry(
 				result,
 				( isReadOnly ? Status.READ_ONLY : Status.MANAGED ),
 				values,
 				null,
 				id,
 				version,
 				LockMode.NONE,
 				true,
 				subclassPersister,
 				false,
 				entry.areLazyPropertiesUnfetched()
 		);
 		subclassPersister.afterInitialize( result, entry.areLazyPropertiesUnfetched(), session );
 		persistenceContext.initializeNonLazyCollections();
 		// upgrade the lock if necessary:
 		//lock(result, lockMode);
 
 		//PostLoad is needed for EJB3
 		//TODO: reuse the PostLoadEvent...
 		PostLoadEvent postLoadEvent = new PostLoadEvent( session )
 				.setEntity( result )
 				.setId( id )
 				.setPersister( persister );
 
 		for ( PostLoadEventListener listener : postLoadEventListeners( session ) ) {
 			listener.onPostLoad( postLoadEvent );
 		}
 
 		return result;
 	}
 
 	private Iterable<PostLoadEventListener> postLoadEventListeners(EventSource session) {
 		return session
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( EventType.POST_LOAD )
 				.listeners();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultRefreshEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultRefreshEventListener.java
index 6ba772ef59..6ca71fe98d 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultRefreshEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultRefreshEventListener.java
@@ -1,188 +1,187 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 import java.util.IdentityHashMap;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.UnresolvableObjectException;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.internal.CascadePoint;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.RefreshEvent;
 import org.hibernate.event.spi.RefreshEventListener;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * Defines the default refresh event listener used by hibernate for refreshing entities
  * in response to generated refresh events.
  *
  * @author Steve Ebersole
  */
 public class DefaultRefreshEventListener implements RefreshEventListener {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( DefaultRefreshEventListener.class );
 
 	public void onRefresh(RefreshEvent event) throws HibernateException {
 		onRefresh( event, new IdentityHashMap( 10 ) );
 	}
 
 	/**
 	 * Handle the given refresh event.
 	 *
 	 * @param event The refresh event to be handled.
 	 */
 	public void onRefresh(RefreshEvent event, Map refreshedAlready) {
 
 		final EventSource source = event.getSession();
 
 		boolean isTransient = !source.contains( event.getObject() );
 		if ( source.getPersistenceContext().reassociateIfUninitializedProxy( event.getObject() ) ) {
 			if ( isTransient ) {
 				source.setReadOnly( event.getObject(), source.isDefaultReadOnly() );
 			}
 			return;
 		}
 
 		final Object object = source.getPersistenceContext().unproxyAndReassociate( event.getObject() );
 
 		if ( refreshedAlready.containsKey( object ) ) {
 			LOG.trace( "Already refreshed" );
 			return;
 		}
 
 		final EntityEntry e = source.getPersistenceContext().getEntry( object );
 		final EntityPersister persister;
 		final Serializable id;
 
 		if ( e == null ) {
 			persister = source.getEntityPersister(
 					event.getEntityName(),
 					object
 			); //refresh() does not pass an entityName
 			id = persister.getIdentifier( object, event.getSession() );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev(
 						"Refreshing transient {0}", MessageHelper.infoString(
 						persister,
 						id,
 						source.getFactory()
 				)
 				);
 			}
 			final EntityKey key = source.generateEntityKey( id, persister );
 			if ( source.getPersistenceContext().getEntry( key ) != null ) {
 				throw new PersistentObjectException(
 						"attempted to refresh transient instance when persistent instance was already associated with the Session: " +
 								MessageHelper.infoString( persister, id, source.getFactory() )
 				);
 			}
 		}
 		else {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev(
 						"Refreshing ", MessageHelper.infoString(
 						e.getPersister(),
 						e.getId(),
 						source.getFactory()
 				)
 				);
 			}
 			if ( !e.isExistsInDatabase() ) {
 				throw new UnresolvableObjectException(
 						e.getId(),
 						"this instance does not yet exist as a row in the database"
 				);
 			}
 
 			persister = e.getPersister();
 			id = e.getId();
 		}
 
 		// cascade the refresh prior to refreshing this entity
 		refreshedAlready.put( object, object );
 		Cascade.cascade(
 				CascadingActions.REFRESH,
 				CascadePoint.BEFORE_REFRESH,
 				source,
 				persister,
 				object,
 				refreshedAlready
 		);
 
 		if ( e != null ) {
 			final EntityKey key = source.generateEntityKey( id, persister );
 			source.getPersistenceContext().removeEntity( key );
 			if ( persister.hasCollections() ) {
 				new EvictVisitor( source ).process( object, persister );
 			}
 		}
 
 		if ( persister.hasCache() ) {
 			final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-			EntityCacheKey ck = cache.generateCacheKey(
+			Object ck = cache.generateCacheKey(
 					id,
 					persister,
 					source.getFactory(),
 					source.getTenantIdentifier()
 			);
 			cache.evict( ck );
 		}
 
 		evictCachedCollections( persister, id, source.getFactory() );
 
 		String previousFetchProfile = source.getLoadQueryInfluencers().getInternalFetchProfile();
 		source.getLoadQueryInfluencers().setInternalFetchProfile( "refresh" );
 		Object result = persister.load( id, object, event.getLockOptions(), source );
 		// Keep the same read-only/modifiable setting for the entity that it had before refreshing;
 		// If it was transient, then set it to the default for the source.
 		if ( result != null ) {
 			if ( !persister.isMutable() ) {
 				// this is probably redundant; it should already be read-only
 				source.setReadOnly( result, true );
 			}
 			else {
 				source.setReadOnly( result, ( e == null ? source.isDefaultReadOnly() : e.isReadOnly() ) );
 			}
 		}
 		source.getLoadQueryInfluencers().setInternalFetchProfile( previousFetchProfile );
 
 		UnresolvableObjectException.throwIfNull( result, id, persister.getEntityName() );
 
 	}
 
 	private void evictCachedCollections(EntityPersister persister, Serializable id, SessionFactoryImplementor factory) {
 		evictCachedCollections( persister.getPropertyTypes(), id, factory );
 	}
 
 	private void evictCachedCollections(Type[] types, Serializable id, SessionFactoryImplementor factory)
 			throws HibernateException {
 		for ( Type type : types ) {
 			if ( type.isCollectionType() ) {
 				factory.getCache().evictCollection( ( (CollectionType) type ).getRole(), id );
 			}
 			else if ( type.isComponentType() ) {
 				CompositeType actype = (CompositeType) type;
 				evictCachedCollections( actype.getSubtypes(), id, factory );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/CacheImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/CacheImpl.java
index 25fe300463..dfac9b16a3 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/CacheImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CacheImpl.java
@@ -1,350 +1,348 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 
 import org.hibernate.HibernateException;
 import org.hibernate.boot.spi.SessionFactoryOptions;
-import org.hibernate.cache.spi.CollectionCacheKey;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.engine.spi.CacheImplementor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * @author Strong Liu <stliu@hibernate.org>
  */
 public class CacheImpl implements CacheImplementor {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( CacheImpl.class );
 
 	private final SessionFactoryImplementor sessionFactory;
 	private final SessionFactoryOptions settings;
 	private final transient QueryCache queryCache;
 	private final transient RegionFactory regionFactory;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient ConcurrentMap<String, QueryCache> queryCaches;
 	private final transient ConcurrentMap<String, Region> allCacheRegions = new ConcurrentHashMap<String, Region>();
 
 	public CacheImpl(SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 		this.settings = sessionFactory.getSessionFactoryOptions();
 		//todo should get this from service registry
 		this.regionFactory = settings.getServiceRegistry().getService( RegionFactory.class );
 		regionFactory.start( settings, sessionFactory.getProperties() );
 		if ( settings.isQueryCacheEnabled() ) {
 			updateTimestampsCache = new UpdateTimestampsCache(
 					settings,
 					sessionFactory.getProperties(),
 					sessionFactory
 			);
 			queryCache = settings.getQueryCacheFactory()
 					.getQueryCache( null, updateTimestampsCache, settings, sessionFactory.getProperties() );
 			queryCaches = new ConcurrentHashMap<String, QueryCache>();
 			allCacheRegions.put( updateTimestampsCache.getRegion().getName(), updateTimestampsCache.getRegion() );
 			allCacheRegions.put( queryCache.getRegion().getName(), queryCache.getRegion() );
 		}
 		else {
 			updateTimestampsCache = null;
 			queryCache = null;
 			queryCaches = null;
 		}
 	}
 
 	@Override
 	public boolean containsEntity(Class entityClass, Serializable identifier) {
 		return containsEntity( entityClass.getName(), identifier );
 	}
 
 	@Override
 	public boolean containsEntity(String entityName, Serializable identifier) {
 		EntityPersister p = sessionFactory.getEntityPersister( entityName );
 		if ( p.hasCache() ) {
 			EntityRegionAccessStrategy cache = p.getCacheAccessStrategy();
-			EntityCacheKey key = cache.generateCacheKey( identifier, p, sessionFactory, null ); // have to assume non tenancy
+			Object key = cache.generateCacheKey( identifier, p, sessionFactory, null ); // have to assume non tenancy
 			return cache.getRegion().contains( key );
 		}
 		else {
 			return false;
 		}
 	}
 
 	@Override
 	public void evictEntity(Class entityClass, Serializable identifier) {
 		evictEntity( entityClass.getName(), identifier );
 	}
 
 	@Override
 	public void evictEntity(String entityName, Serializable identifier) {
 		EntityPersister p = sessionFactory.getEntityPersister( entityName );
 		if ( p.hasCache() ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf(
 						"Evicting second-level cache: %s",
 						MessageHelper.infoString( p, identifier, sessionFactory )
 				);
 			}
 			EntityRegionAccessStrategy cache = p.getCacheAccessStrategy();
-			EntityCacheKey key = cache.generateCacheKey( identifier, p, sessionFactory, null ); // have to assume non tenancy
+			Object key = cache.generateCacheKey( identifier, p, sessionFactory, null ); // have to assume non tenancy
 			cache.evict( key );
 		}
 	}
 
 	@Override
 	public void evictEntityRegion(Class entityClass) {
 		evictEntityRegion( entityClass.getName() );
 	}
 
 	@Override
 	public void evictEntityRegion(String entityName) {
 		EntityPersister p = sessionFactory.getEntityPersister( entityName );
 		if ( p.hasCache() ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Evicting second-level cache: %s", p.getEntityName() );
 			}
 			p.getCacheAccessStrategy().evictAll();
 		}
 	}
 
 	@Override
 	public void evictEntityRegions() {
 		for ( String s : sessionFactory.getEntityPersisters().keySet() ) {
 			evictEntityRegion( s );
 		}
 	}
 
 	@Override
 	public void evictNaturalIdRegion(Class entityClass) {
 		evictNaturalIdRegion( entityClass.getName() );
 	}
 
 	@Override
 	public void evictNaturalIdRegion(String entityName) {
 		EntityPersister p = sessionFactory.getEntityPersister( entityName );
 		if ( p.hasNaturalIdCache() ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Evicting natural-id cache: %s", p.getEntityName() );
 			}
 			p.getNaturalIdCacheAccessStrategy().evictAll();
 		}
 	}
 
 	@Override
 	public void evictNaturalIdRegions() {
 		for ( String s : sessionFactory.getEntityPersisters().keySet() ) {
 			evictNaturalIdRegion( s );
 		}
 	}
 
 	@Override
 	public boolean containsCollection(String role, Serializable ownerIdentifier) {
 		CollectionPersister p = sessionFactory.getCollectionPersister( role );
 		if ( p.hasCache() ) {
 			CollectionRegionAccessStrategy cache = p.getCacheAccessStrategy();
-			CollectionCacheKey key = cache.generateCacheKey( ownerIdentifier, p, sessionFactory, null ); // have to assume non tenancy
+			Object key = cache.generateCacheKey( ownerIdentifier, p, sessionFactory, null ); // have to assume non tenancy
 			return cache.getRegion().contains( key );
 		}
 		else {
 			return false;
 		}
 	}
 
 	@Override
 	public void evictCollection(String role, Serializable ownerIdentifier) {
 		CollectionPersister p = sessionFactory.getCollectionPersister( role );
 		if ( p.hasCache() ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf(
 						"Evicting second-level cache: %s",
 						MessageHelper.collectionInfoString( p, ownerIdentifier, sessionFactory )
 				);
 			}
 			CollectionRegionAccessStrategy cache = p.getCacheAccessStrategy();
-			CollectionCacheKey key = cache.generateCacheKey( ownerIdentifier, p, sessionFactory, null ); // have to assume non tenancy
+			Object key = cache.generateCacheKey( ownerIdentifier, p, sessionFactory, null ); // have to assume non tenancy
 			cache.evict( key );
 		}
 	}
 
 	@Override
 	public void evictCollectionRegion(String role) {
 		CollectionPersister p = sessionFactory.getCollectionPersister( role );
 		if ( p.hasCache() ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Evicting second-level cache: %s", p.getRole() );
 			}
 			p.getCacheAccessStrategy().evictAll();
 		}
 	}
 
 	@Override
 	public void evictCollectionRegions() {
 		for ( String s : sessionFactory.getCollectionPersisters().keySet() ) {
 			evictCollectionRegion( s );
 		}
 	}
 
 	@Override
 	public boolean containsQuery(String regionName) {
 		return queryCaches.containsKey( regionName );
 	}
 
 	@Override
 	public void evictDefaultQueryRegion() {
 		if ( sessionFactory.getSessionFactoryOptions().isQueryCacheEnabled() ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debug( "Evicting default query region cache." );
 			}
 			sessionFactory.getQueryCache().clear();
 		}
 	}
 
 	@Override
 	public void evictQueryRegion(String regionName) {
 		if ( regionName == null ) {
 			throw new NullPointerException(
 					"Region-name cannot be null (use Cache#evictDefaultQueryRegion to evict the default query cache)"
 			);
 		}
 		if ( sessionFactory.getSessionFactoryOptions().isQueryCacheEnabled() ) {
 			QueryCache namedQueryCache = queryCaches.get( regionName );
 			// TODO : cleanup entries in queryCaches + allCacheRegions ?
 			if ( namedQueryCache != null ) {
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf( "Evicting query cache, region: %s", regionName );
 				}
 				namedQueryCache.clear();
 			}
 		}
 	}
 
 	@Override
 	public void evictQueryRegions() {
 		evictDefaultQueryRegion();
 
 		if ( CollectionHelper.isEmpty( queryCaches ) ) {
 			return;
 		}
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debug( "Evicting cache of all query regions." );
 		}
 		for ( QueryCache queryCache : queryCaches.values() ) {
 			queryCache.clear();
 		}
 	}
 
 	@Override
 	public void close() {
 		if ( settings.isQueryCacheEnabled() ) {
 			queryCache.destroy();
 
 			for ( QueryCache cache : queryCaches.values() ) {
 				cache.destroy();
 			}
 			updateTimestampsCache.destroy();
 		}
 
 		regionFactory.stop();
 	}
 
 	@Override
 	public QueryCache getQueryCache() {
 		return queryCache;
 	}
 
 	@Override
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		if ( regionName == null ) {
 			return getQueryCache();
 		}
 
 		if ( !settings.isQueryCacheEnabled() ) {
 			return null;
 		}
 
 		QueryCache currentQueryCache = queryCaches.get( regionName );
 		if ( currentQueryCache == null ) {
 			synchronized (allCacheRegions) {
 				currentQueryCache = queryCaches.get( regionName );
 				if ( currentQueryCache == null ) {
 					currentQueryCache = settings.getQueryCacheFactory()
 							.getQueryCache(
 									regionName,
 									updateTimestampsCache,
 									settings,
 									sessionFactory.getProperties()
 							);
 					queryCaches.put( regionName, currentQueryCache );
 					allCacheRegions.put( currentQueryCache.getRegion().getName(), currentQueryCache.getRegion() );
 				}
 				else {
 					return currentQueryCache;
 				}
 			}
 		}
 		return currentQueryCache;
 	}
 
 	@Override
 	public void addCacheRegion(String name, Region region) {
 		allCacheRegions.put( name, region );
 	}
 
 	@Override
 	public UpdateTimestampsCache getUpdateTimestampsCache() {
 		return updateTimestampsCache;
 	}
 
 	@Override
 	public void evictQueries() throws HibernateException {
 		if ( settings.isQueryCacheEnabled() ) {
 			queryCache.clear();
 		}
 	}
 
 	@Override
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return allCacheRegions.get( regionName );
 	}
 
 	@Override
 	public Region getNaturalIdCacheRegion(String regionName) {
 		return allCacheRegions.get( regionName );
 	}
 
 	@SuppressWarnings({"unchecked"})
 	@Override
 	public Map<String, Region> getAllSecondLevelCacheRegions() {
 		return new HashMap<String, Region>( allCacheRegions );
 	}
 
 	@Override
 	public RegionFactory getRegionFactory() {
 		return regionFactory;
 	}
 
 	@Override
 	public void evictAllRegions() {
 		evictCollectionRegions();
 		evictDefaultQueryRegion();
 		evictEntityRegions();
 		evictQueryRegions();
 		evictNaturalIdRegions();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index d77fbb730f..2671565053 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1529 +1,1541 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal;
 
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 
+import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionEventListener;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.Transaction;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownEntityTypeException;
 import org.hibernate.boot.cfgxml.spi.CfgXmlAccessService;
 import org.hibernate.boot.cfgxml.spi.LoadedConfig;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jndi.spi.JndiService;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.query.spi.ReturnMetadata;
 import org.hibernate.engine.spi.ActionQueue;
 import org.hibernate.engine.spi.CacheImplementor;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionBuilderImplementor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.config.ConfigurationException;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.resource.jdbc.spi.StatementInspector;
 import org.hibernate.resource.transaction.TransactionCoordinator;
 import org.hibernate.secure.spi.GrantedPermission;
 import org.hibernate.secure.spi.JaccPermissionDeclarations;
 import org.hibernate.secure.spi.JaccService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
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
  * @see org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
  * @see org.hibernate.Session
  * @see org.hibernate.hql.spi.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl implements SessionFactoryImplementor {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( SessionFactoryImpl.class );
 
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private final String name;
 	private final String uuid;
 
 	private final transient Map<String,EntityPersister> entityPersisters;
 	private final transient Map<String,ClassMetadata> classMetadata;
 	private final transient Map<Class,String> entityProxyInterfaceMap;
 	private final transient Map<String,CollectionPersister> collectionPersisters;
 	private final transient Map<String,CollectionMetadata> collectionMetadata;
 	private final transient Map<String,Set<String>> collectionRolesByEntityParticipant;
 	private final transient Map<String,IdentifierGenerator> identifierGenerators;
 	private final transient NamedQueryRepository namedQueryRepository;
 	private final transient Map<String, FilterDefinition> filters;
 	private final transient Map<String, FetchProfile> fetchProfiles;
 	private final transient Map<String,String> imports;
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
 	private final transient JdbcServices jdbcServices;
 	private final transient Dialect dialect;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
 	private final transient ConcurrentMap<EntityNameResolver,Object> entityNameResolvers = new ConcurrentHashMap<EntityNameResolver, Object>();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient CacheImplementor cacheAccess;
 	private transient boolean isClosed;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 	private final transient SessionFactoryOptions sessionFactoryOptions;
+	private final transient Map<String, RegionAccessStrategy> cacheAccessStrategiesMap = new HashMap();
 
 	public SessionFactoryImpl(final MetadataImplementor metadata, SessionFactoryOptions options) {
 		LOG.debug( "Building session factory" );
 
 		this.sessionFactoryOptions = options;
 		this.settings = new Settings( options, metadata );
 
 		this.serviceRegistry = options.getServiceRegistry()
 				.getService( SessionFactoryServiceRegistryFactory.class )
 				.buildServiceRegistry( this, options );
 
 		final CfgXmlAccessService cfgXmlAccessService = serviceRegistry.getService( CfgXmlAccessService.class );
 
 		String sfName = settings.getSessionFactoryName();
 		if ( cfgXmlAccessService.getAggregatedConfig() != null ) {
 			if ( sfName == null ) {
 				sfName = cfgXmlAccessService.getAggregatedConfig().getSessionFactoryName();
 			}
 			applyCfgXmlValues( cfgXmlAccessService.getAggregatedConfig(), serviceRegistry );
 		}
 
 		this.name = sfName;
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 
 		this.properties = new Properties();
 		this.properties.putAll( serviceRegistry.getService( ConfigurationService.class ).getSettings() );
 
 		this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
 		this.dialect = this.jdbcServices.getDialect();
 		this.cacheAccess = this.serviceRegistry.getService( CacheImplementor.class );
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), options.getCustomSqlFunctionMap() );
 
 		for ( SessionFactoryObserver sessionFactoryObserver : options.getSessionFactoryObservers() ) {
 			this.observer.addObserver( sessionFactoryObserver );
 		}
 
 		this.typeResolver = metadata.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		this.filters.putAll( metadata.getFilterDefinitions() );
 
 		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 		LOG.debugf( "Instantiating session factory with properties: %s", properties );
 
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
 				integrators.clear();
 			}
 		}
 		final IntegratorObserver integratorObserver = new IntegratorObserver();
 		this.observer.addObserver( integratorObserver );
 		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
 			integrator.integrate( metadata, this, this.serviceRegistry );
 			integratorObserver.integrators.add( integrator );
 		}
 
 		//Generators:
 
 		this.identifierGenerators = new HashMap<String, IdentifierGenerator>();
 		for ( PersistentClass model : metadata.getEntityBindings() ) {
 			if ( !model.isInherited() ) {
 				IdentifierGenerator generator = model.getIdentifier().createIdentifierGenerator(
 						metadata.getIdentifierGeneratorFactory(),
 						getDialect(),
 						settings.getDefaultCatalogName(),
 						settings.getDefaultSchemaName(),
 						(RootClass) model
 				);
 				identifierGenerators.put( model.getEntityName(), generator );
 			}
 		}
 
 		this.imports = new HashMap<String,String>( metadata.getImports() );
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		final PersisterCreationContext persisterCreationContext = new PersisterCreationContext() {
 			@Override
 			public SessionFactoryImplementor getSessionFactory() {
 				return SessionFactoryImpl.this;
 			}
 
 			@Override
 			public MetadataImplementor getMetadata() {
 				return metadata;
 			}
 		};
 
 		final RegionFactory regionFactory = cacheAccess.getRegionFactory();
 		final String cacheRegionPrefix = settings.getCacheRegionPrefix() == null ? "" : settings.getCacheRegionPrefix() + ".";
 		final PersisterFactory persisterFactory = serviceRegistry.getService( PersisterFactory.class );
 
 		// todo : consider removing this silliness and just have EntityPersister directly implement ClassMetadata
 		//		EntityPersister.getClassMetadata() for the internal impls simply "return this";
 		//		collapsing those would allow us to remove this "extra" Map
 		//
 		// todo : similar for CollectionPersister/CollectionMetadata
 
 		this.entityPersisters = new HashMap<String,EntityPersister>();
-		Map cacheAccessStrategiesMap = new HashMap();
 		Map<String,ClassMetadata> inFlightClassMetadataMap = new HashMap<String,ClassMetadata>();
 		this.entityProxyInterfaceMap = CollectionHelper.concurrentMap( metadata.getEntityBindings().size() );
 		for ( final PersistentClass model : metadata.getEntityBindings() ) {
 			final String cacheRegionName = cacheRegionPrefix + model.getRootClass().getCacheRegionName();
 			// cache region is defined by the root-class in the hierarchy...
 			final EntityRegionAccessStrategy accessStrategy = determineEntityRegionAccessStrategy(
 					regionFactory,
 					cacheAccessStrategiesMap,
 					model,
 					cacheRegionName
 			);
 
 			final NaturalIdRegionAccessStrategy naturalIdAccessStrategy = determineNaturalIdRegionAccessStrategy(
 					regionFactory,
 					cacheRegionPrefix,
 					cacheAccessStrategiesMap,
 					model
 			);
 
 			final EntityPersister cp = persisterFactory.createEntityPersister(
 					model,
 					accessStrategy,
 					naturalIdAccessStrategy,
 					persisterCreationContext
 			);
 			entityPersisters.put( model.getEntityName(), cp );
 			inFlightClassMetadataMap.put( model.getEntityName(), cp.getClassMetadata() );
 
 			if ( cp.getConcreteProxyClass() != null
 					&& cp.getConcreteProxyClass().isInterface()
 					&& !Map.class.isAssignableFrom( cp.getConcreteProxyClass() )
 					&& cp.getMappedClass() != cp.getConcreteProxyClass() ) {
 				// IMPL NOTE : we exclude Map based proxy interfaces here because that should
 				//		indicate MAP entity mode.0
 
 				if ( cp.getMappedClass().equals( cp.getConcreteProxyClass() ) ) {
 					// this part handles an odd case in the Hibernate test suite where we map an interface
 					// as the class and the proxy.  I cannot think of a real life use case for that
 					// specific test, but..
 					LOG.debugf( "Entity [%s] mapped same interface [%s] as class and proxy", cp.getEntityName(), cp.getMappedClass() );
 				}
 				else {
 					final String old = entityProxyInterfaceMap.put( cp.getConcreteProxyClass(), cp.getEntityName() );
 					if ( old != null ) {
 						throw new HibernateException(
 								String.format(
 										Locale.ENGLISH,
 										"Multiple entities [%s, %s] named the same interface [%s] as their proxy which is not supported",
 										old,
 										cp.getEntityName(),
 										cp.getConcreteProxyClass().getName()
 								)
 						);
 					}
 				}
 			}
 		}
 		this.classMetadata = Collections.unmodifiableMap( inFlightClassMetadataMap );
 
 		this.collectionPersisters = new HashMap<String,CollectionPersister>();
 		Map<String,Set<String>> inFlightEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		Map<String,CollectionMetadata> tmpCollectionMetadata = new HashMap<String,CollectionMetadata>();
 		for ( final Collection model : metadata.getCollectionBindings() ) {
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			final CollectionRegionAccessStrategy accessStrategy;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				LOG.tracev( "Building shared cache region for collection data [{0}]", model.getRole() );
 				CollectionRegion collectionRegion = regionFactory.buildCollectionRegion(
 						cacheRegionName,
 						properties,
 						CacheDataDescriptionImpl.decode( model )
 				);
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				cacheAccessStrategiesMap.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion( cacheRegionName, collectionRegion );
 			}
 			else {
 				accessStrategy = null;
 			}
 
 			final CollectionPersister persister = persisterFactory.createCollectionPersister(
 					model,
 					accessStrategy,
 					persisterCreationContext
 			);
 			collectionPersisters.put( model.getRole(), persister );
 			tmpCollectionMetadata.put( model.getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set<String> roles = inFlightEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					inFlightEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set<String> roles = inFlightEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					inFlightEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
+
 		this.collectionMetadata = Collections.unmodifiableMap( tmpCollectionMetadata );
 
 		for ( Map.Entry<String,Set<String>> entityToCollectionRoleMapEntry : inFlightEntityToCollectionRoleMap.entrySet() ) {
 			entityToCollectionRoleMapEntry.setValue(
 					Collections.unmodifiableSet( entityToCollectionRoleMapEntry.getValue() )
 			);
 		}
 		this.collectionRolesByEntityParticipant = Collections.unmodifiableMap( inFlightEntityToCollectionRoleMap );
 
 		//Named Queries:
 		this.namedQueryRepository = metadata.buildNamedQueryRepository( this );
 
 		// after *all* persisters and named queries are registered
 		for ( EntityPersister persister : entityPersisters.values() ) {
 			persister.generateEntityDefinition();
 		}
 
 		for ( EntityPersister persister : entityPersisters.values() ) {
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister );
 		}
 		for ( CollectionPersister persister : collectionPersisters.values() ) {
 			persister.postInstantiate();
 		}
 
 		LOG.debug( "Instantiated session factory" );
 
 		settings.getMultiTableBulkIdStrategy().prepare(
 				jdbcServices,
 				buildLocalConnectionAccess(),
 				metadata,
 				sessionFactoryOptions
 		);
 
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( serviceRegistry, metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( serviceRegistry, metadata ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( serviceRegistry, metadata ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( serviceRegistry, metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			final Map<String,HibernateException> errors = checkNamedQueries();
 			if ( ! errors.isEmpty() ) {
 				StringBuilder failingQueries = new StringBuilder( "Errors in named queries: " );
 				String sep = "";
 				for ( Map.Entry<String,HibernateException> entry : errors.entrySet() ) {
 					LOG.namedQueryError( entry.getKey(), entry.getValue() );
 					failingQueries.append( sep ).append( entry.getKey() );
 					sep = ", ";
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap<String,FetchProfile>();
 		for ( org.hibernate.mapping.FetchProfile mappingProfile : metadata.getFetchProfiles() ) {
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			for ( org.hibernate.mapping.FetchProfile.Fetch mappingFetch : mappingProfile.getFetches() ) {
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = entityName == null
 						? null
 						: entityPersisters.get( entityName );
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
 				((Loadable) owner).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.observer.sessionFactoryCreated( this );
 
 		SessionFactoryRegistry.INSTANCE.addSessionFactory(
 				uuid,
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				this,
 				serviceRegistry.getService( JndiService.class )
 		);
 	}
 
 	private void applyCfgXmlValues(LoadedConfig aggregatedConfig, SessionFactoryServiceRegistry serviceRegistry) {
 		final JaccService jaccService = serviceRegistry.getService( JaccService.class );
 		if ( jaccService.getContextId() != null ) {
 			final JaccPermissionDeclarations permissions = aggregatedConfig.getJaccPermissions( jaccService.getContextId() );
 			if ( permissions != null ) {
 				for ( GrantedPermission grantedPermission : permissions.getPermissionDeclarations() ) {
 					jaccService.addPermission( grantedPermission );
 				}
 			}
 		}
 
 		if ( aggregatedConfig.getEventListenerMap() != null ) {
 			final ClassLoaderService cls = serviceRegistry.getService( ClassLoaderService.class );
 			final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 			for ( Map.Entry<EventType, Set<String>> entry : aggregatedConfig.getEventListenerMap().entrySet() ) {
 				final EventListenerGroup group = eventListenerRegistry.getEventListenerGroup( entry.getKey() );
 				for ( String listenerClassName : entry.getValue() ) {
 					try {
 						group.appendListener( cls.classForName( listenerClassName ).newInstance() );
 					}
 					catch (Exception e) {
 						throw new ConfigurationException( "Unable to instantiate event listener class : " + listenerClassName, e );
 					}
 				}
 			}
 		}
 	}
 
 	private NaturalIdRegionAccessStrategy determineNaturalIdRegionAccessStrategy(
 			RegionFactory regionFactory,
 			String cacheRegionPrefix,
 			Map cacheAccessStrategiesMap,
 			PersistentClass model) {
 		NaturalIdRegionAccessStrategy naturalIdAccessStrategy = null;
 		if ( model.hasNaturalId() && model.getNaturalIdCacheRegionName() != null ) {
 			final String naturalIdCacheRegionName = cacheRegionPrefix + model.getNaturalIdCacheRegionName();
 			naturalIdAccessStrategy = ( NaturalIdRegionAccessStrategy ) cacheAccessStrategiesMap.get( naturalIdCacheRegionName );
 
 			if ( naturalIdAccessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 				final CacheDataDescriptionImpl cacheDataDescription = CacheDataDescriptionImpl.decode( model );
 
 				NaturalIdRegion naturalIdRegion = null;
 				try {
 					naturalIdRegion = regionFactory.buildNaturalIdRegion(
 							naturalIdCacheRegionName,
 							properties,
 							cacheDataDescription
 					);
 				}
 				catch ( UnsupportedOperationException e ) {
 					LOG.warnf(
 							"Shared cache region factory [%s] does not support natural id caching; " +
 									"shared NaturalId caching will be disabled for not be enabled for %s",
 							regionFactory.getClass().getName(),
 							model.getEntityName()
 					);
 				}
 
 				if (naturalIdRegion != null) {
 					naturalIdAccessStrategy = naturalIdRegion.buildAccessStrategy( regionFactory.getDefaultAccessType() );
 					cacheAccessStrategiesMap.put( naturalIdCacheRegionName, naturalIdAccessStrategy );
 					cacheAccess.addCacheRegion(  naturalIdCacheRegionName, naturalIdRegion );
 				}
 			}
 		}
 		return naturalIdAccessStrategy;
 	}
 
 	private EntityRegionAccessStrategy determineEntityRegionAccessStrategy(
 			RegionFactory regionFactory,
 			Map cacheAccessStrategiesMap,
 			PersistentClass model,
 			String cacheRegionName) {
 		EntityRegionAccessStrategy accessStrategy = ( EntityRegionAccessStrategy ) cacheAccessStrategiesMap.get( cacheRegionName );
 		if ( accessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			if ( accessType != null ) {
 				LOG.tracef( "Building shared cache region for entity data [%s]", model.getEntityName() );
 				EntityRegion entityRegion = regionFactory.buildEntityRegion(
 						cacheRegionName,
 						properties,
 						CacheDataDescriptionImpl.decode( model )
 				);
 				accessStrategy = entityRegion.buildAccessStrategy( accessType );
 				cacheAccessStrategiesMap.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion( cacheRegionName, entityRegion );
 			}
 		}
 		return accessStrategy;
 	}
 
 	private JdbcConnectionAccess buildLocalConnectionAccess() {
 		return new JdbcConnectionAccess() {
 			@Override
 			public Connection obtainConnection() throws SQLException {
 				return settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE
 						? serviceRegistry.getService( ConnectionProvider.class ).getConnection()
 						: serviceRegistry.getService( MultiTenantConnectionProvider.class ).getAnyConnection();
 			}
 
 			@Override
 			public void releaseConnection(Connection connection) throws SQLException {
 				if ( settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE ) {
 					serviceRegistry.getService( ConnectionProvider.class ).closeConnection( connection );
 				}
 				else {
 					serviceRegistry.getService( MultiTenantConnectionProvider.class ).releaseAnyConnection( connection );
 				}
 			}
 
 			@Override
 			public boolean supportsAggressiveRelease() {
 				return false;
 			}
 		};
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
 	public SessionBuilderImplementor withOptions() {
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
 		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizer() == null ) {
 			return;
 		}
 		registerEntityNameResolvers( persister.getEntityMetamodel().getTuplizer() );
 	}
 
 	private void registerEntityNameResolvers(EntityTuplizer tuplizer) {
 		EntityNameResolver[] resolvers = tuplizer.getEntityNameResolvers();
 		if ( resolvers == null ) {
 			return;
 		}
 
 		for ( EntityNameResolver resolver : resolvers ) {
 			registerEntityNameResolver( resolver );
 		}
 	}
 
 	private static final Object ENTITY_NAME_RESOLVER_MAP_VALUE = new Object();
 
 	public void registerEntityNameResolver(EntityNameResolver resolver) {
 		entityNameResolvers.put( resolver, ENTITY_NAME_RESOLVER_MAP_VALUE );
 	}
 
 	@Override
 	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
 		return entityNameResolvers.keySet();
 	}
 
 	public QueryPlanCache getQueryPlanCache() {
 		return queryPlanCache;
 	}
 
 	private Map<String,HibernateException> checkNamedQueries() throws HibernateException {
 		return namedQueryRepository.checkNamedQueries( queryPlanCache );
 	}
 
 	@Override
 	public Map<String, EntityPersister> getEntityPersisters() {
 		return entityPersisters;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(String entityName) throws MappingException {
 		EntityPersister result = entityPersisters.get( entityName );
 		if ( result == null ) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 	@Override
 	public EntityPersister locateEntityPersister(Class byClass) {
 		EntityPersister entityPersister = entityPersisters.get( byClass.getName() );
 		if ( entityPersister == null ) {
 			String mappedEntityName = entityProxyInterfaceMap.get( byClass );
 			if ( mappedEntityName != null ) {
 				entityPersister = entityPersisters.get( mappedEntityName );
 			}
 		}
 
 		if ( entityPersister == null ) {
 			throw new UnknownEntityTypeException( "Unable to locate persister: " + byClass.getName() );
 		}
 
 		return entityPersister;
 	}
 
 	@Override
 	public EntityPersister locateEntityPersister(String byName) {
 		final EntityPersister entityPersister = entityPersisters.get( byName );
 		if ( entityPersister == null ) {
 			throw new UnknownEntityTypeException( "Unable to locate persister: " + byName );
 		}
 		return entityPersister;
 	}
 
 	@Override
 	public DeserializationResolver getDeserializationResolver() {
 		return new DeserializationResolver() {
 			@Override
 			public SessionFactoryImplementor resolve() {
 				return (SessionFactoryImplementor) SessionFactoryRegistry.INSTANCE.findSessionFactory( uuid, name );
 			}
 		};
 	}
 
 	@Override
 	public Map<String, CollectionPersister> getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
 		CollectionPersister result = collectionPersisters.get(role);
 		if ( result == null ) {
 			throw new MappingException( "Unknown collection role: " + role );
 		}
 		return result;
 	}
 
 	@SuppressWarnings("deprecation")
 	public Settings getSettings() {
 		return settings;
 	}
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return sessionFactoryOptions;
 	}
 
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
 	}
 
 	public Dialect getDialect() {
 		if ( serviceRegistry == null ) {
 			throw new IllegalStateException( "Cannot determine dialect because serviceRegistry is null." );
 		}
 		return dialect;
 	}
 
 	public Interceptor getInterceptor() {
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
 	public Reference getReference() {
 		// from javax.naming.Referenceable
 		LOG.debug( "Returning a Reference to the SessionFactory" );
 		return new Reference(
 				SessionFactoryImpl.class.getName(),
 				new StringRefAddr("uuid", uuid),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	@Override
 	public NamedQueryRepository getNamedQueryRepository() {
 		return namedQueryRepository;
 	}
 
 	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
 		namedQueryRepository.registerNamedQueryDefinition( name, definition );
 	}
 
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return namedQueryRepository.getNamedQueryDefinition( queryName );
 	}
 
 	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
 		namedQueryRepository.registerNamedSQLQueryDefinition( name, definition );
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return namedQueryRepository.getNamedSQLQueryDefinition( queryName );
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String mappingName) {
 		return namedQueryRepository.getResultSetMappingDefinition( mappingName );
 	}
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierPropertyName();
 	}
 
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		final ReturnMetadata metadata = queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata();
 		return metadata == null ? null : metadata.getReturnTypes();
 	}
 
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		final ReturnMetadata metadata = queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata();
 		return metadata == null ? null : metadata.getReturnAliases();
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getClassMetadata( persistentClass.getName() );
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return collectionMetadata.get( roleName );
 	}
 
 	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
 		return classMetadata.get( entityName );
 	}
 
 	/**
 	 * Given the name of an entity class, determine all the class and interface names by which it can be
 	 * referenced in an HQL query.
 	 *
      * @param className The name of the entity class
 	 *
 	 * @return the names of all persistent (mapped) classes that extend or implement the
 	 *     given class or interface, accounting for implicit/explicit polymorphism settings
 	 *     and excluding mapped subclasses/joined-subclasses of other classes in the result.
 	 * @throws MappingException
 	 */
 	public String[] getImplementors(String className) throws MappingException {
 
 		final Class clazz;
 		try {
 			clazz = serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
 		}
 		catch (ClassLoadingException cnfe) {
 			return new String[] { className }; //for a dynamic-class
 		}
 
 		ArrayList<String> results = new ArrayList<String>();
 		for ( EntityPersister checkPersister : entityPersisters.values() ) {
 			if ( ! Queryable.class.isInstance( checkPersister ) ) {
 				continue;
 			}
 			final Queryable checkQueryable = Queryable.class.cast( checkPersister );
 			final String checkQueryableEntityName = checkQueryable.getEntityName();
 			final boolean isMappedClass = className.equals( checkQueryableEntityName );
 			if ( checkQueryable.isExplicitPolymorphism() ) {
 				if ( isMappedClass ) {
 					return new String[] { className }; //NOTE EARLY EXIT
 				}
 			}
 			else {
 				if ( isMappedClass ) {
 					results.add( checkQueryableEntityName );
 				}
 				else {
 					final Class mappedClass = checkQueryable.getMappedClass();
 					if ( mappedClass != null && clazz.isAssignableFrom( mappedClass ) ) {
 						final boolean assignableSuperclass;
 						if ( checkQueryable.isInherited() ) {
 							Class mappedSuperclass = getEntityPersister( checkQueryable.getMappedSuperclass() ).getMappedClass();
 							assignableSuperclass = clazz.isAssignableFrom( mappedSuperclass );
 						}
 						else {
 							assignableSuperclass = false;
 						}
 						if ( !assignableSuperclass ) {
 							results.add( checkQueryableEntityName );
 						}
 					}
 				}
 			}
 		}
 		return results.toArray( new String[results.size()] );
 	}
 
 	@Override
 	public String getImportedClassName(String className) {
 		String result = imports.get( className );
 		if ( result == null ) {
 			try {
 				serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
 				imports.put( className, className );
 				return className;
 			}
 			catch ( ClassLoadingException cnfe ) {
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
 	 * @throws HibernateException
 	 */
 	public void close() throws HibernateException {
 
 		if ( isClosed ) {
 			LOG.trace( "Already closed" );
 			return;
 		}
 
 		LOG.closing();
 
 		isClosed = true;
 
 		settings.getMultiTableBulkIdStrategy().release( jdbcServices, buildLocalConnectionAccess() );
 
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
 
 		cacheAccess.close();
 
 		queryPlanCache.cleanup();
 
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport.drop( false, true );
 		}
 
 		SessionFactoryRegistry.INSTANCE.removeSessionFactory(
 				uuid,
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				serviceRegistry.getService( JndiService.class )
 		);
 
 		observer.sessionFactoryClosed( this );
 		serviceRegistry.destroy();
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
 		cacheAccess.evictQueries();
 	}
 
 	public void evictQueries(String regionName) throws HibernateException {
 		getCache().evictQueryRegion( regionName );
 	}
 
 	public UpdateTimestampsCache getUpdateTimestampsCache() {
 		return cacheAccess.getUpdateTimestampsCache();
 	}
 
 	public QueryCache getQueryCache() {
 		return cacheAccess.getQueryCache();
 	}
 
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		return cacheAccess.getQueryCache( regionName );
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return cacheAccess.getSecondLevelCacheRegion( regionName );
 	}
 
+	@Override
+	public RegionAccessStrategy getSecondLevelCacheRegionAccessStrategy(String regionName) {
+		return cacheAccessStrategiesMap.get(regionName);
+	}
+
 	public Region getNaturalIdCacheRegion(String regionName) {
 		return cacheAccess.getNaturalIdCacheRegion( regionName );
 	}
 
+	@Override
+	public RegionAccessStrategy getNaturalIdCacheRegionAccessStrategy(String regionName) {
+		return cacheAccessStrategiesMap.get(regionName);
+	}
+
 	@SuppressWarnings( {"unchecked"})
 	public Map getAllSecondLevelCacheRegions() {
 		return cacheAccess.getAllSecondLevelCacheRegions();
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
 		FilterDefinition def = filters.get( filterName );
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
 		return identifierGenerators.get(rootEntityName);
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
 //			if ( ! transactionFactory().compatibleWithJtaSynchronization() ) {
 //				LOG.autoFlushWillNotWork();
 //			}
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
 				Class implClass = serviceRegistry.getService( ClassLoaderService.class ).classForName( impl );
 				return ( CurrentSessionContext ) implClass
 						.getConstructor( new Class[] { SessionFactoryImplementor.class } )
 						.newInstance( this );
 			}
 			catch( Throwable t ) {
 				LOG.unableToConstructCurrentSessionContext( impl, t );
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
 		return fetchProfiles.get( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return typeHelper;
 	}
 
 	static class SessionBuilderImpl implements SessionBuilderImplementor {
 		private static final Logger log = CoreLogging.logger( SessionBuilderImpl.class );
 
 		private final SessionFactoryImpl sessionFactory;
 		private SessionOwner sessionOwner;
 		private Interceptor interceptor;
 		private StatementInspector statementInspector;
 		private Connection connection;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private boolean autoClose;
 		private boolean autoJoinTransactions = true;
 		private boolean flushBeforeCompletion;
 		private String tenantIdentifier;
 		private List<SessionEventListener> listeners;
 
 		SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 			this.sessionOwner = null;
 			final Settings settings = sessionFactory.settings;
 
 			// set up default builder values...
 			this.interceptor = sessionFactory.getInterceptor();
 			this.statementInspector = sessionFactory.getSessionFactoryOptions().getStatementInspector();
 			this.connectionReleaseMode = settings.getConnectionReleaseMode();
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
 
 			listeners = settings.getBaselineSessionEventsListenerBuilder().buildBaselineList();
 		}
 
 		protected TransactionCoordinator getTransactionCoordinator() {
 			return null;
 		}
 
 		protected JdbcCoordinatorImpl getJdbcCoordinator() {
 			return null;
 		}
 
 		protected Transaction getTransaction() {
 			return null;
 		}
 
 		protected ActionQueue.TransactionCompletionProcesses getTransactionCompletionProcesses() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			log.tracef( "Opening Hibernate Session.  tenant=%s, owner=%s", tenantIdentifier, sessionOwner );
 			final SessionImpl session = new SessionImpl(
 					connection,
 					sessionFactory,
 					sessionOwner,
 					getTransactionCoordinator(),
 					getJdbcCoordinator(),
 					getTransaction(),
 					getTransactionCompletionProcesses(),
 					autoJoinTransactions,
 					sessionFactory.settings.getRegionFactory().nextTimestamp(),
 					interceptor,
 					statementInspector,
 					flushBeforeCompletion,
 					autoClose,
 					connectionReleaseMode,
 					tenantIdentifier
 			);
 
 			for ( SessionEventListener listener : listeners ) {
 				session.getEventListenerManager().addListener( listener );
 			}
 
 			return session;
 		}
 
 		@Override
 		public SessionBuilder owner(SessionOwner sessionOwner) {
 			this.sessionOwner = sessionOwner;
 			return this;
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
 		public SessionBuilder statementInspector(StatementInspector statementInspector) {
 			this.statementInspector = statementInspector;
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
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 
 		@Override
 		public SessionBuilder eventListeners(SessionEventListener... listeners) {
 			Collections.addAll( this.listeners, listeners );
 			return this;
 		}
 
 		@Override
 		public SessionBuilder clearEventListeners() {
 			listeners.clear();
 			return this;
 		}
 	}
 
 	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Connection connection;
 		private String tenantIdentifier;
 
 		public StatelessSessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
 		}
 
 		@Override
 		public StatelessSession openStatelessSession() {
 			return new StatelessSessionImpl( connection, tenantIdentifier, sessionFactory,
 					sessionFactory.settings.getRegionFactory().nextTimestamp() );
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
 
 	@Override
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 		return getSessionFactoryOptions().getCustomEntityDirtinessStrategy();
 	}
 
 	@Override
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return getSessionFactoryOptions().getCurrentTenantIdentifierResolver();
 	}
 
 
 	// Serialization handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly serialized
 	 *
 	 * @param out The stream into which the object is being serialized.
 	 *
 	 * @throws IOException Can be thrown by the stream
 	 */
 	private void writeObject(ObjectOutputStream out) throws IOException {
 		LOG.debugf( "Serializing: %s", uuid );
 		out.defaultWriteObject();
 		LOG.trace( "Serialized" );
 	}
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized
 	 *
 	 * @param in The stream from which the object is being deserialized.
 	 *
 	 * @throws IOException Can be thrown by the stream
 	 * @throws ClassNotFoundException Again, can be thrown by the stream
 	 */
 	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
 		LOG.trace( "Deserializing" );
 		in.defaultReadObject();
 		LOG.debugf( "Deserialized: %s", uuid );
 	}
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized.
 	 * Here we resolve the uuid/name read from the stream previously to resolve the SessionFactory
 	 * instance to use based on the registrations with the {@link SessionFactoryRegistry}
 	 *
 	 * @return The resolved factory to use.
 	 *
 	 * @throws InvalidObjectException Thrown if we could not resolve the factory by uuid/name.
 	 */
 	private Object readResolve() throws InvalidObjectException {
 		LOG.trace( "Resolving serialized SessionFactory" );
 		return locateSessionFactoryOnDeserialization( uuid, name );
 	}
 
 	private static SessionFactory locateSessionFactoryOnDeserialization(String uuid, String name) throws InvalidObjectException{
 		final SessionFactory uuidResult = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 		if ( uuidResult != null ) {
 			LOG.debugf( "Resolved SessionFactory by UUID [%s]", uuid );
 			return uuidResult;
 		}
 
 		// in case we were deserialized in a different JVM, look for an instance with the same name
 		// (provided we were given a name)
 		if ( name != null ) {
 			final SessionFactory namedResult = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
 			if ( namedResult != null ) {
 				LOG.debugf( "Resolved SessionFactory by name [%s]", name );
 				return namedResult;
 			}
 		}
 
 		throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
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
 		LOG.trace( "Deserializing SessionFactory from Session" );
 		final String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		final String name = isNamed ? ois.readUTF() : null;
 		return (SessionFactoryImpl) locateSessionFactoryOnDeserialization( uuid, name );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
index 62f719e9ff..9c2c425072 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
@@ -1,780 +1,779 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 import javax.transaction.SystemException;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.StatelessSession;
 import org.hibernate.Transaction;
 import org.hibernate.UnresolvableObjectException;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.SessionEventListenerManagerImpl;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.loader.criteria.CriteriaLoader;
 import org.hibernate.loader.custom.CustomLoader;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
 import org.hibernate.resource.jdbc.spi.StatementInspector;
 import org.hibernate.resource.transaction.TransactionCoordinator;
 import org.hibernate.resource.transaction.spi.TransactionStatus;
 
 /**
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class StatelessSessionImpl extends AbstractSessionImpl implements StatelessSession {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( StatelessSessionImpl.class );
 
 	private TransactionCoordinator transactionCoordinator;
 
 	private transient JdbcCoordinator jdbcCoordinator;
 	private PersistenceContext temporaryPersistenceContext = new StatefulPersistenceContext( this );
 	private long timestamp;
 	private JdbcSessionContext jdbcSessionContext;
 
 	private LoadQueryInfluencers statelessLoadQueryInfluencers = new LoadQueryInfluencers( null ) {
 		@Override
 		public String getInternalFetchProfile() {
 			return null;
 		}
 
 		@Override
 		public void setInternalFetchProfile(String internalFetchProfile) {
 		}
 	};
 
 	StatelessSessionImpl(
 			Connection connection,
 			String tenantIdentifier,
 			SessionFactoryImpl factory) {
 		this( connection, tenantIdentifier, factory, factory.getSettings().getRegionFactory().nextTimestamp() );
 	}
 
 	StatelessSessionImpl(
 			Connection connection,
 			String tenantIdentifier,
 			SessionFactoryImpl factory,
 			long timestamp) {
 		super( factory, tenantIdentifier );
 		this.jdbcSessionContext = new JdbcSessionContextImpl(
 				factory,
 				new StatementInspector() {
 					@Override
 					public String inspect(String sql) {
 						return null;
 					}
 				}
 		);
 		this.jdbcCoordinator = new JdbcCoordinatorImpl( connection, this );
 
 		this.transactionCoordinator = getTransactionCoordinatorBuilder().buildTransactionCoordinator(
 				jdbcCoordinator,
 				this
 		);
 		this.currentHibernateTransaction = getTransaction();
 		this.timestamp = timestamp;
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		return transactionCoordinator;
 	}
 
 	@Override
 	public JdbcCoordinator getJdbcCoordinator() {
 		return this.jdbcCoordinator;
 	}
 
 	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return true;
 	}
 
 	// inserts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Serializable insert(Object entity) {
 		errorIfClosed();
 		return insert( null, entity );
 	}
 
 	@Override
 	public Serializable insert(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister( entityName, entity );
 		Serializable id = persister.getIdentifierGenerator().generate( this, entity );
 		Object[] state = persister.getPropertyValues( entity );
 		if ( persister.isVersioned() ) {
 			boolean substitute = Versioning.seedVersion(
 					state, persister.getVersionProperty(), persister.getVersionType(), this
 			);
 			if ( substitute ) {
 				persister.setPropertyValues( entity, state );
 			}
 		}
 		if ( id == IdentifierGeneratorHelper.POST_INSERT_INDICATOR ) {
 			id = persister.insert( state, entity, this );
 		}
 		else {
 			persister.insert( id, state, entity, this );
 		}
 		persister.setIdentifier( entity, id, this );
 		return id;
 	}
 
 
 	// deletes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void delete(Object entity) {
 		errorIfClosed();
 		delete( null, entity );
 	}
 
 	@Override
 	public void delete(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister( entityName, entity );
 		Serializable id = persister.getIdentifier( entity, this );
 		Object version = persister.getVersion( entity );
 		persister.delete( id, version, entity, this );
 	}
 
 
 	// updates ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void update(Object entity) {
 		errorIfClosed();
 		update( null, entity );
 	}
 
 	@Override
 	public void update(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister( entityName, entity );
 		Serializable id = persister.getIdentifier( entity, this );
 		Object[] state = persister.getPropertyValues( entity );
 		Object oldVersion;
 		if ( persister.isVersioned() ) {
 			oldVersion = persister.getVersion( entity );
 			Object newVersion = Versioning.increment( oldVersion, persister.getVersionType(), this );
 			Versioning.setVersion( state, newVersion, persister );
 			persister.setPropertyValues( entity, state );
 		}
 		else {
 			oldVersion = null;
 		}
 		persister.update( id, state, null, false, null, oldVersion, entity, null, this );
 	}
 
 
 	// loading ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Object get(Class entityClass, Serializable id) {
 		return get( entityClass.getName(), id );
 	}
 
 	@Override
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) {
 		return get( entityClass.getName(), id, lockMode );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id) {
 		return get( entityName, id, LockMode.NONE );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockMode lockMode) {
 		errorIfClosed();
 		Object result = getFactory().getEntityPersister( entityName )
 				.load( id, null, lockMode, this );
 		if ( temporaryPersistenceContext.isLoadFinished() ) {
 			temporaryPersistenceContext.clear();
 		}
 		return result;
 	}
 
 	@Override
 	public void refresh(Object entity) {
 		refresh( bestGuessEntityName( entity ), entity, LockMode.NONE );
 	}
 
 	@Override
 	public void refresh(String entityName, Object entity) {
 		refresh( entityName, entity, LockMode.NONE );
 	}
 
 	@Override
 	public void refresh(Object entity, LockMode lockMode) {
 		refresh( bestGuessEntityName( entity ), entity, lockMode );
 	}
 
 	@Override
 	public void refresh(String entityName, Object entity, LockMode lockMode) {
 		final EntityPersister persister = this.getEntityPersister( entityName, entity );
 		final Serializable id = persister.getIdentifier( entity, this );
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Refreshing transient {0}", MessageHelper.infoString( persister, id, this.getFactory() ) );
 		}
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
 			final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-			final EntityCacheKey ck = cache.generateCacheKey( id, persister, getFactory(), getTenantIdentifier() );
+			final Object ck = cache.generateCacheKey( id, persister, getFactory(), getTenantIdentifier() );
 			cache.evict( ck );
 		}
 		String previousFetchProfile = this.getLoadQueryInfluencers().getInternalFetchProfile();
 		Object result = null;
 		try {
 			this.getLoadQueryInfluencers().setInternalFetchProfile( "refresh" );
 			result = persister.load( id, entity, lockMode, this );
 		}
 		finally {
 			this.getLoadQueryInfluencers().setInternalFetchProfile( previousFetchProfile );
 		}
 		UnresolvableObjectException.throwIfNull( result, id, persister.getEntityName() );
 	}
 
 	@Override
 	public Object immediateLoad(String entityName, Serializable id)
 			throws HibernateException {
 		throw new SessionException( "proxies cannot be fetched by a stateless session" );
 	}
 
 	@Override
 	public void initializeCollection(
 			PersistentCollection collection,
 			boolean writing) throws HibernateException {
 		throw new SessionException( "collections cannot be fetched by a stateless session" );
 	}
 
 	@Override
 	public Object instantiate(
 			String entityName,
 			Serializable id) throws HibernateException {
 		errorIfClosed();
 		return getFactory().getEntityPersister( entityName ).instantiate( id, this );
 	}
 
 	@Override
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
 
 	@Override
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 			throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
 			throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean isOpen() {
 		return !isClosed();
 	}
 
 	@Override
 	public void close() {
 		managedClose();
 	}
 
 	@Override
 	public boolean isAutoCloseSessionEnabled() {
 		return factory.getSettings().isAutoCloseSessionEnabled();
 	}
 
 	@Override
 	public boolean shouldAutoClose() {
 		return isAutoCloseSessionEnabled() && !isClosed();
 	}
 
 
 	private boolean isFlushModeNever() {
 		return false;
 	}
 
 	private void managedClose() {
 		if ( isClosed() ) {
 			throw new SessionException( "Session was already closed!" );
 		}
 		jdbcCoordinator.close();
 		setClosed();
 	}
 
 	private void managedFlush() {
 		errorIfClosed();
 		jdbcCoordinator.executeBatch();
 	}
 
 	private SessionEventListenerManagerImpl sessionEventsManager;
 
 	@Override
 	public SessionEventListenerManager getEventListenerManager() {
 		if ( sessionEventsManager == null ) {
 			sessionEventsManager = new SessionEventListenerManagerImpl();
 		}
 		return sessionEventsManager;
 	}
 
 	@Override
 	public String bestGuessEntityName(Object object) {
 		if ( object instanceof HibernateProxy ) {
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation();
 		}
 		return guessEntityName( object );
 	}
 
 	@Override
 	public Connection connection() {
 		errorIfClosed();
 		return jdbcCoordinator.getLogicalConnection().getPhysicalConnection();
 	}
 
 	@Override
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
 			afterOperation( success );
 		}
 		temporaryPersistenceContext.clear();
 		return result;
 	}
 
 	@Override
 	public CacheMode getCacheMode() {
 		return CacheMode.IGNORE;
 	}
 
 	@Override
 	public int getDontFlushFromFind() {
 		return 0;
 	}
 
 	@Override
 	public Serializable getContextEntityIdentifier(Object object) {
 		errorIfClosed();
 		return null;
 	}
 
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(String entityName, Object object)
 			throws HibernateException {
 		errorIfClosed();
 		if ( entityName == null ) {
 			return factory.getEntityPersister( guessEntityName( object ) );
 		}
 		else {
 			return factory.getEntityPersister( entityName ).getSubclassEntityPersister( object, getFactory() );
 		}
 	}
 
 	@Override
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		errorIfClosed();
 		return null;
 	}
 
 	@Override
 	public FlushMode getFlushMode() {
 		return FlushMode.COMMIT;
 	}
 
 	@Override
 	public Interceptor getInterceptor() {
 		return EmptyInterceptor.INSTANCE;
 	}
 
 	@Override
 	public PersistenceContext getPersistenceContext() {
 		return temporaryPersistenceContext;
 	}
 
 	@Override
 	public long getTimestamp() {
 		return timestamp;
 	}
 
 	@Override
 	public String guessEntityName(Object entity) throws HibernateException {
 		errorIfClosed();
 		return entity.getClass().getName();
 	}
 
 	@Override
 	public boolean isConnected() {
 		return jdbcCoordinator.getLogicalConnection().isPhysicallyConnected();
 	}
 
 	@Override
 	public boolean isTransactionInProgress() {
 		return !isClosed() && transactionCoordinator.isJoined() && transactionCoordinator.getTransactionDriverControl()
 				.getStatus() == TransactionStatus.ACTIVE;
 	}
 
 	@Override
 	public void setAutoClear(boolean enabled) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public void disableTransactionAutoJoin() {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public void setCacheMode(CacheMode cm) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public void setFlushMode(FlushMode fm) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public Transaction beginTransaction() throws HibernateException {
 		errorIfClosed();
 		Transaction result = getTransaction();
 		result.begin();
 		return result;
 	}
 
 	@Override
 	public boolean isEventSource() {
 		return false;
 	}
 
 	public boolean isDefaultReadOnly() {
 		return false;
 	}
 
 	public void setDefaultReadOnly(boolean readOnly) throws HibernateException {
 		if ( readOnly ) {
 			throw new UnsupportedOperationException();
 		}
 	}
 
 /////////////////////////////////////////////////////////////////////////////////////////////////////
 
 	//TODO: COPY/PASTE FROM SessionImpl, pull up!
 
 	@Override
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		boolean success = false;
 		List results = Collections.EMPTY_LIST;
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation( success );
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	public void afterOperation(boolean success) {
 		if ( !isTransactionInProgress() ) {
 			jdbcCoordinator.afterTransaction();
 		}
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		errorIfClosed();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName, String alias) {
 		errorIfClosed();
 		return new CriteriaImpl( entityName, alias, this );
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass) {
 		errorIfClosed();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName) {
 		errorIfClosed();
 		return new CriteriaImpl( entityName, this );
 	}
 
 	@Override
 	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 
 		errorIfClosed();
 		String entityName = criteriaImpl.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable( entityName ),
 				factory,
 				criteriaImpl,
 				entityName,
 				getLoadQueryInfluencers()
 		);
 		return loader.scroll( this, scrollMode );
 	}
 
 	@Override
 	@SuppressWarnings({"unchecked"})
 	public List list(Criteria criteria) throws HibernateException {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 
 		errorIfClosed();
 		String[] implementors = factory.getImplementors( criteriaImpl.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		for ( int i = 0; i < size; i++ ) {
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 					factory,
 					criteriaImpl,
 					implementors[i],
 					getLoadQueryInfluencers()
 			);
 		}
 
 
 		List results = Collections.EMPTY_LIST;
 		boolean success = false;
 		try {
 			for ( int i = 0; i < size; i++ ) {
 				final List currentResults = loaders[i].list( this );
 				currentResults.addAll( results );
 				results = currentResults;
 			}
 			success = true;
 		}
 		finally {
 			afterOperation( success );
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
 		EntityPersister persister = factory.getEntityPersister( entityName );
 		if ( !( persister instanceof OuterJoinLoadable ) ) {
 			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
 		}
 		return (OuterJoinLoadable) persister;
 	}
 
 	@Override
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 			throws HibernateException {
 		errorIfClosed();
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		boolean success = false;
 		List results;
 		try {
 			results = loader.list( this, queryParameters );
 			success = true;
 		}
 		finally {
 			afterOperation( success );
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	@Override
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 			throws HibernateException {
 		errorIfClosed();
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 		return loader.scroll( queryParameters, this );
 	}
 
 	@Override
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		return plan.performScroll( queryParameters, this );
 	}
 
 	@Override
 	public void afterScrollOperation() {
 		temporaryPersistenceContext.clear();
 	}
 
 	@Override
 	public void flush() {
 	}
 
 	@Override
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return statelessLoadQueryInfluencers;
 	}
 
 	@Override
 	public int executeNativeUpdate(
 			NativeSQLQuerySpecification nativeSQLQuerySpecification,
 			QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		queryParameters.validateParameters();
 		NativeSQLQueryPlan plan = getNativeSQLQueryPlan( nativeSQLQuerySpecification );
 
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation( success );
 		}
 		temporaryPersistenceContext.clear();
 		return result;
 	}
 
 	@Override
 	public JdbcSessionContext getJdbcSessionContext() {
 		return this.jdbcSessionContext;
 	}
 
 	@Override
 	public void afterTransactionBegin() {
 
 	}
 
 	@Override
 	public void beforeTransactionCompletion() {
 		flushBeforeTransactionCompletion();
 	}
 
 	@Override
 	public void afterTransactionCompletion(boolean successful, boolean delayed) {
 		if ( shouldAutoClose() && !isClosed() ) {
 			managedClose();
 		}
 	}
 
 	@Override
 	public void flushBeforeTransactionCompletion() {
 		boolean flush = false;
 		try {
 			flush = (
 					!isClosed()
 							&& !isFlushModeNever()
 							&& !JtaStatusHelper.isRollback(
 							getJtaPlatform().getCurrentStatus()
 					) );
 		}
 		catch (SystemException se) {
 			throw new HibernateException( "could not determine transaction status in beforeCompletion()", se );
 		}
 		if ( flush ) {
 			managedFlush();
 		}
 	}
 
 	private JtaPlatform getJtaPlatform() {
 		return factory.getServiceRegistry().getService( JtaPlatform.class );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 8a89ab38c0..51bc9b9a2b 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,2626 +1,2625 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.loader;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.TimeUnit;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.FilterKey;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.dialect.pagination.NoopLimitHandler;
 import org.hibernate.engine.internal.CacheHelper;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FetchingScrollableResultsImpl;
 import org.hibernate.internal.ScrollableResultsImpl;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.loader.spi.AfterLoadAction;
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
 	protected static final CoreMessageLogger LOG = CoreLogging.messageLogger( Loader.class );
 	protected static final boolean DEBUG_ENABLED = LOG.isDebugEnabled();
 
 	private final SessionFactoryImplementor factory;
 	private volatile ColumnNameCache columnNameCache;
 
 	private final boolean referenceCachingEnabled;
 
 	public Loader(SessionFactoryImplementor factory) {
 		this.factory = factory;
 		this.referenceCachingEnabled = factory.getSessionFactoryOptions().isDirectReferenceCacheEntriesEnabled();
 	}
 
 	/**
 	 * The SQL query string to be called; implemented by all subclasses
 	 *
 	 * @return The sql command this loader should use to get its {@link ResultSet}.
 	 */
 	public abstract String getSQLString();
 
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
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
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
 	protected String preprocessSQL(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 		sql = applyLocks( sql, parameters, dialect, afterLoadActions );
 
 		// Keep this here, rather than moving to Select.  Some Dialects may need the hint to be appended to the very
 		// end or beginning of the finalized SQL statement, so wait until everything is processed.
 		if ( parameters.getQueryHints() != null && parameters.getQueryHints().size() > 0 ) {
 			sql = dialect.getQueryHintString( sql, parameters.getQueryHints() );
 		}
 
 		return getFactory().getSessionFactoryOptions().isCommentsEnabled()
 				? prependComment( sql, parameters )
 				: sql;
 	}
 
 	protected boolean shouldUseFollowOnLocking(
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) {
 		if ( dialect.useFollowOnLocking() ) {
 			// currently only one lock mode is allowed in follow-on locking
 			final LockMode lockMode = determineFollowOnLockMode( parameters.getLockOptions() );
 			final LockOptions lockOptions = new LockOptions( lockMode );
 			if ( lockOptions.getLockMode() != LockMode.UPGRADE_SKIPLOCKED ) {
 				LOG.usingFollowOnLocking();
 				lockOptions.setTimeOut( parameters.getLockOptions().getTimeOut() );
 				lockOptions.setScope( parameters.getLockOptions().getScope() );
 				afterLoadActions.add(
 						new AfterLoadAction() {
 							@Override
 							public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
 								( (Session) session ).buildLockRequest( lockOptions ).lock(
 										persister.getEntityName(),
 										entity
 								);
 							}
 						}
 				);
 				parameters.setLockOptions( new LockOptions() );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
 		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
 
 		if ( lockOptions.hasAliasSpecificLockModes() ) {
 			LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
 		}
 
 		return lockModeToUse;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return "/* " + comment + " */ " + sql;
 		}
 	}
 
 	/**
 	 * Execute an SQL query and attempt to instantiate instances of the class mapped by the given
 	 * persister from each row of the <tt>ResultSet</tt>. If an object is supplied, will attempt to
 	 * initialize that object. If a collection is supplied, attempt to initialize that collection.
 	 */
 	public List doQueryAndInitializeNonLazyCollections(
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
 
 	public List doQueryAndInitializeNonLazyCollections(
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
 	 *
 	 * @return The loaded "row".
 	 *
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
 		catch (SQLException sqle) {
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
 				if ( !keyToRead.equals( loadedKeys[0] ) ) {
 					throw new AssertionFailure(
 							String.format(
 									"Unexpected key read for row; expected [%s]; actual [%s]",
 									keyToRead,
 									loadedKeys[0]
 							)
 					);
 				}
 				if ( result == null ) {
 					result = loaded;
 				}
 			}
 			while ( resultSet.next() &&
 					isCurrentRowForSameEntity( keyToRead, 0, resultSet, session ) );
 		}
 		catch (SQLException sqle) {
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
 
 	private boolean isCurrentRowForSameEntity(
 			final EntityKey keyToRead,
 			final int persisterIndex,
 			final ResultSet resultSet,
 			final SessionImplementor session) throws SQLException {
 		EntityKey currentRowKey = getKeyFromResultSet(
 				persisterIndex, getEntityPersisters()[persisterIndex], null, resultSet, session
 		);
 		return keyToRead.equals( currentRowKey );
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
 	 *
 	 * @return The loaded "row".
 	 *
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
 			// key value upon which to perform the breaking logic.  However,
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
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not perform sequential read of results (forward)",
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
 	 *
 	 * @return The loaded "row".
 	 *
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
 		catch (SQLException sqle) {
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
 			return session.generateEntityKey(
 					optionalId, session.getEntityPersister(
 							optionalEntityName,
 							optionalObject
 					)
 			);
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
 		extractKeysFromResultSet(
 				persisters,
 				queryParameters,
 				resultSet,
 				session,
 				keys,
 				lockModesArray,
 				hydratedObjects
 		);
 
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
 					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setImplementation( entity );
 					row[i] = proxy;
 				}
 			}
 		}
 
 		applyPostLoadLocks( row, lockModesArray, session );
 
 		return forcedResultTransformer == null
 				? getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session )
 				: forcedResultTransformer.transformTuple(
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
 			keys[entitySpan - 1] = session.generateEntityKey( optionalId, persisters[entitySpan - 1] );
 			// skip the last persister below...
 			numberOfPersistersToProcess = entitySpan - 1;
 		}
 		else {
 			numberOfPersistersToProcess = entitySpan;
 		}
 
 		final Object[] hydratedKeyState = new Object[numberOfPersistersToProcess];
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			hydratedKeyState[i] = idType.hydrate(
 					resultSet,
 					getEntityAliases()[i].getSuffixedKeyAliases(),
 					session,
 					null
 			);
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
 
 			for ( int i = 0; i < collectionPersisters.length; i++ ) {
 
 				final boolean hasCollectionOwners = collectionOwners != null &&
 						collectionOwners[i] > -1;
 				//true if this is a query and we are loading multiple instances of the same collection role
 				//otherwise this is a CollectionInitializer and we are loading up a single collection or batch
 
 				final Object owner = hasCollectionOwners ?
 						row[collectionOwners[i]] :
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
 		final int maxRows = LimitHelper.hasMaxRows( selection ) ?
 				selection.getMaxRows() :
 				Integer.MAX_VALUE;
 
 		final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 
 		final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, false, afterLoadActions, session );
 		final ResultSet rs = wrapper.getResultSet();
 		final Statement st = wrapper.getStatement();
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		try {
 			return processResultSet(
 					rs,
 					queryParameters,
 					session,
 					returnProxies,
 					forcedResultTransformer,
 					maxRows,
 					afterLoadActions
 			);
 		}
 		finally {
 			session.getJdbcCoordinator().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 		}
 
 	}
 
 	protected List processResultSet(
 			ResultSet rs,
 			QueryParameters queryParameters,
 			SessionImplementor session,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer,
 			int maxRows,
 			List<AfterLoadAction> afterLoadActions) throws SQLException {
 		final int entitySpan = getEntityPersisters().length;
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
 		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final List results = new ArrayList();
 
 		handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
 		EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
 		LOG.trace( "Processing result set" );
 		int count;
 
 		for ( count = 0; count < maxRows && rs.next(); count++ ) {
 			if ( DEBUG_ENABLED ) {
 				LOG.debugf( "Result set row: %s", count );
 			}
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
 				subselectResultKeys.add( keys );
 				keys = new EntityKey[entitySpan]; //can't reuse in this case
 			}
 		}
 
 		LOG.tracev( "Done processing result set ({0} rows)", count );
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				rs,
 				session,
 				queryParameters.isReadOnly( session ),
 				afterLoadActions
 		);
 		if ( createSubselects ) {
 			createSubselects( subselectResultKeys, queryParameters, session );
 		}
 		return results;
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return false;
 	}
 
 	protected boolean hasSubselectLoadableCollections() {
 		final Loadable[] loadables = getEntityPersisters();
 		for ( Loadable loadable : loadables ) {
 			if ( loadable.hasSubselectLoadableCollections() ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private static Set[] transpose(List keys) {
 		Set[] result = new Set[( (EntityKey[]) keys.get( 0 ) ).length];
 		for ( int j = 0; j < result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( Object key : keys ) {
 				result[j].add( ( (EntityKey[]) key )[j] );
 			}
 		}
 		return result;
 	}
 
 	private void createSubselects(List keys, QueryParameters queryParameters, SessionImplementor session) {
 		if ( keys.size() > 1 ) { //if we only returned one entity, query by key is more efficient
 
 			Set[] keySets = transpose( keys );
 
 			Map namedParameterLocMap = buildNamedParameterLocMap( queryParameters );
 
 			final Loadable[] loadables = getEntityPersisters();
 			final String[] aliases = getAliases();
 			for ( Object key : keys ) {
 				final EntityKey[] rowKeys = (EntityKey[]) key;
 				for ( int i = 0; i < rowKeys.length; i++ ) {
 
 					if ( rowKeys[i] != null && loadables[i].hasSubselectLoadableCollections() ) {
 
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
 		if ( queryParameters.getNamedParameters() != null ) {
 			final Map namedParameterLocMap = new HashMap();
 			for ( String name : queryParameters.getNamedParameters().keySet() ) {
 				namedParameterLocMap.put(
 						name,
 						getNamedParameterLocs( name )
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
 			final boolean readOnly) throws HibernateException {
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSetId,
 				session,
 				readOnly,
 				Collections.<AfterLoadAction>emptyList()
 		);
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SessionImplementor session,
 			final boolean readOnly,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 			for ( CollectionPersister collectionPersister : collectionPersisters ) {
 				if ( collectionPersister.isArray() ) {
 					//for arrays, we should end the collection load before resolving
 					//the entities, since the actual array instances are not instantiated
 					//during loading
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersister );
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
 
 		if ( hydratedObjects != null ) {
 			int hydratedObjectsSize = hydratedObjects.size();
 			LOG.tracev( "Total objects hydrated: {0}", hydratedObjectsSize );
 			for ( Object hydratedObject : hydratedObjects ) {
 				TwoPhaseLoad.initializeEntity( hydratedObject, readOnly, session, pre );
 			}
 		}
 
 		if ( collectionPersisters != null ) {
 			for ( CollectionPersister collectionPersister : collectionPersisters ) {
 				if ( !collectionPersister.isArray() ) {
 					//for sets, we should end the collection load after resolving
 					//the entities, since we might call hashCode() on the elements
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersister );
 				}
 			}
 		}
 
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur after
 		// endCollectionLoad to ensure the collection is in the
 		// persistence context.
 		if ( hydratedObjects != null ) {
 			for ( Object hydratedObject : hydratedObjects ) {
 				TwoPhaseLoad.postLoad( hydratedObject, session, post );
 				if ( afterLoadActions != null ) {
 					for ( AfterLoadAction afterLoadAction : afterLoadActions ) {
 						final EntityEntry entityEntry = session.getPersistenceContext().getEntry( hydratedObject );
 						if ( entityEntry == null ) {
 							// big problem
 							throw new HibernateException(
 									"Could not locate EntityEntry immediately after two-phase load"
 							);
 						}
 						afterLoadAction.afterLoad( session, hydratedObject, (Loadable) entityEntry.getPersister() );
 					}
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
 				.getCollectionLoadContext( (ResultSet) resultSetId )
 				.endLoadingCollections( collectionPersister );
 	}
 
 	/**
 	 * Determine the actual ResultTransformer that will be used to
 	 * transform query results.
 	 *
 	 * @param resultTransformer the specified result transformer
 	 *
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
 	 *
 	 * @return true, if getResultColumnOrRow() transforms the results; false, otherwise
 	 */
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return false;
 	}
 
 	/**
 	 * Returns the aliases that corresponding to a result row.
 	 *
 	 * @return Returns the aliases that corresponding to a result row.
 	 */
 	protected String[] getResultRowAliases() {
 		return null;
 	}
 
 	/**
 	 * Get the actual object that is returned in the user-visible result list.
 	 * This empty implementation merely returns its first argument. This is
 	 * overridden by some subclasses.
 	 */
 	protected Object getResultColumnOrRow(
 			Object[] row,
 			ResultTransformer transformer,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(
 			Object[] row,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	/**
 	 * For missing objects associated by one-to-one with another object in the
 	 * result set, register the fact that the the object is missing with the
 	 * session.
 	 */
 	private void registerNonExists(
 			final EntityKey[] keys,
 			final Loadable[] persisters,
 			final SessionImplementor session) {
 
 		final int[] owners = getOwners();
 		if ( owners != null ) {
 
 			EntityType[] ownerAssociationTypes = getOwnerAssociationTypes();
 			for ( int i = 0; i < keys.length; i++ ) {
 
 				int owner = owners[i];
 				if ( owner > -1 ) {
 					EntityKey ownerKey = keys[owner];
 					if ( keys[i] == null && ownerKey != null ) {
 
 						final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 						/*final boolean isPrimaryKey;
 						final boolean isSpecialOneToOne;
 						if ( ownerAssociationTypes == null || ownerAssociationTypes[i] == null ) {
 							isPrimaryKey = true;
 							isSpecialOneToOne = false;
 						}
 						else {
 							isPrimaryKey = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName()==null;
 							isSpecialOneToOne = ownerAssociationTypes[i].getLHSPropertyName()!=null;
 						}*/
 
 						//TODO: can we *always* use the "null property" approach for everything?
 						/*if ( isPrimaryKey && !isSpecialOneToOne ) {
 							persistenceContext.addNonExistantEntityKey(
 									new EntityKey( ownerKey.getIdentifier(), persisters[i], session.getEntityMode() )
 							);
 						}
 						else if ( isSpecialOneToOne ) {*/
 						boolean isOneToOneAssociation = ownerAssociationTypes != null &&
 								ownerAssociationTypes[i] != null &&
 								ownerAssociationTypes[i].isOneToOne();
 						if ( isOneToOneAssociation ) {
 							persistenceContext.addNullProperty(
 									ownerKey,
 									ownerAssociationTypes[i].getPropertyName()
 							);
 						}
 						/*}
 						else {
 							persistenceContext.addNonExistantEntityUniqueKey( new EntityUniqueKey(
 									persisters[i].getEntityName(),
 									ownerAssociationTypes[i].getRHSUniqueKeyPropertyName(),
 									ownerKey.getIdentifier(),
 									persisters[owner].getIdentifierType(),
 									session.getEntityMode()
 							) );
 						}*/
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Read one collection element from the current row of the JDBC result set
 	 */
 	private void readCollectionElement(
 			final Object optionalOwner,
 			final Serializable optionalKey,
 			final CollectionPersister persister,
 			final CollectionAliases descriptor,
 			final ResultSet rs,
 			final SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 		final Serializable collectionRowKey = (Serializable) persister.readKey(
 				rs,
 				descriptor.getSuffixedKeyAliases(),
 				session
 		);
 
 		if ( collectionRowKey != null ) {
 			// we found a collection element in the result set
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf(
 						"Found row of collection: %s",
 						MessageHelper.collectionInfoString( persister, collectionRowKey, getFactory() )
 				);
 			}
 
 			Object owner = optionalOwner;
 			if ( owner == null ) {
 				owner = persistenceContext.getCollectionOwner( collectionRowKey, persister );
 				if ( owner == null ) {
 					//TODO: This is assertion is disabled because there is a bug that means the
 					//	  original owner of a transient, uninitialized collection is not known
 					//	  if the collection is re-referenced by a different object associated
 					//	  with the current Session
 					//throw new AssertionFailure("bug loading unowned collection");
 				}
 			}
 
 			PersistentCollection rowCollection = persistenceContext.getLoadContexts()
 					.getCollectionLoadContext( rs )
 					.getLoadingCollection( persister, collectionRowKey );
 
 			if ( rowCollection != null ) {
 				rowCollection.readFrom( rs, persister, descriptor, owner );
 			}
 
 		}
 		else if ( optionalKey != null ) {
 			// we did not find a collection element in the result set, so we
 			// ensure that a collection is created with the owner's identifier,
 			// since what we have is an empty collection
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf(
 						"Result set contains (possibly empty) collection: %s",
 						MessageHelper.collectionInfoString( persister, optionalKey, getFactory() )
 				);
 			}
 
 			persistenceContext.getLoadContexts()
 					.getCollectionLoadContext( rs )
 					.getLoadingCollection( persister, optionalKey ); // handle empty collection
 
 		}
 
 		// else no collection element, but also no owner
 
 	}
 
 	/**
 	 * If this is a collection initializer, we need to tell the session that a collection
 	 * is being initialized, to account for the possibility of the collection having
 	 * no elements (hence no rows in the result set).
 	 */
 	private void handleEmptyCollections(
 			final Serializable[] keys,
 			final Object resultSetId,
 			final SessionImplementor session) {
 
 		if ( keys != null ) {
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			// this is a collection initializer, so we must create a collection
 			// for each of the passed-in keys, to account for the possibility
 			// that the collection is empty and has no rows in the result set
 			CollectionPersister[] collectionPersisters = getCollectionPersisters();
 			for ( CollectionPersister collectionPersister : collectionPersisters ) {
 				for ( Serializable key : keys ) {
 					//handle empty collections
 					if ( debugEnabled ) {
 						LOG.debugf(
 								"Result set contains (possibly empty) collection: %s",
 								MessageHelper.collectionInfoString( collectionPersister, key, getFactory() )
 						);
 					}
 
 					session.getPersistenceContext()
 							.getLoadContexts()
 							.getCollectionLoadContext( (ResultSet) resultSetId )
 							.getLoadingCollection( collectionPersister, key );
 				}
 			}
 		}
 
 		// else this is not a collection initializer (and empty collections will
 		// be detected by looking for the owner's identifier in the result set)
 	}
 
 	/**
 	 * Read a row of <tt>Key</tt>s from the <tt>ResultSet</tt> into the given array.
 	 * Warning: this method is side-effecty.
 	 * <p/>
 	 * If an <tt>id</tt> is given, don't bother going to the <tt>ResultSet</tt>.
 	 */
 	private EntityKey getKeyFromResultSet(
 			final int i,
 			final Loadable persister,
 			final Serializable id,
 			final ResultSet rs,
 			final SessionImplementor session) throws HibernateException, SQLException {
 
 		Serializable resultId;
 
 		// if we know there is exactly 1 row, we can skip.
 		// it would be great if we could _always_ skip this;
 		// it is a problem for <key-many-to-one>
 
 		if ( isSingleRowLoader() && id != null ) {
 			resultId = id;
 		}
 		else {
 			final Type idType = persister.getIdentifierType();
 			resultId = (Serializable) idType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedKeyAliases(),
 					session,
 					null //problematic for <key-many-to-one>!
 			);
 
 			final boolean idIsResultId = id != null &&
 					resultId != null &&
 					idType.isEqual( id, resultId, factory );
 
 			if ( idIsResultId ) {
 				resultId = id; //use the id passed in
 			}
 		}
 
 		return resultId == null ? null : session.generateEntityKey( resultId, persister );
 	}
 
 	/**
 	 * Check the version of the object in the <tt>ResultSet</tt> against
 	 * the object version in the session cache, throwing an exception
 	 * if the version numbers are different
 	 */
 	private void checkVersion(
 			final int i,
 			final Loadable persister,
 			final Serializable id,
 			final Object entity,
 			final ResultSet rs,
 			final SessionImplementor session) throws HibernateException, SQLException {
 
 		Object version = session.getPersistenceContext().getEntry( entity ).getVersion();
 
 		if ( version != null ) { //null version means the object is in the process of being loaded somewhere else in the ResultSet
 			final VersionType versionType = persister.getVersionType();
 			final Object currentVersion = versionType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedVersionAliases(),
 					session,
 					null
 			);
 			if ( !versionType.isEqual( version, currentVersion ) ) {
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor()
 							.optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), id );
 			}
 		}
 
 	}
 
 	/**
 	 * Resolve any IDs for currently loaded objects, duplications within the
 	 * <tt>ResultSet</tt>, etc. Instantiate empty objects to be initialized from the
 	 * <tt>ResultSet</tt>. Return an array of objects (a row of results) and an
 	 * array of booleans (by side-effect) that determine whether the corresponding
 	 * object should be initialized.
 	 */
 	private Object[] getRow(
 			final ResultSet rs,
 			final Loadable[] persisters,
 			final EntityKey[] keys,
 			final Object optionalObject,
 			final EntityKey optionalObjectKey,
 			final LockMode[] lockModes,
 			final List hydratedObjects,
 			final SessionImplementor session) throws HibernateException, SQLException {
 		final int cols = persisters.length;
 		final EntityAliases[] descriptors = getEntityAliases();
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Result row: %s", StringHelper.toString( keys ) );
 		}
 
 		final Object[] rowResults = new Object[cols];
 
 		for ( int i = 0; i < cols; i++ ) {
 
 			Object object = null;
 			EntityKey key = keys[i];
 
 			if ( keys[i] == null ) {
 				//do nothing
 			}
 			else {
 				//If the object is already loaded, return the loaded one
 				object = session.getEntityUsingInterceptor( key );
 				if ( object != null ) {
 					//its already loaded so don't need to hydrate it
 					instanceAlreadyLoaded(
 							rs,
 							i,
 							persisters[i],
 							key,
 							object,
 							lockModes[i],
 							session
 					);
 				}
 				else {
 					object = instanceNotYetLoaded(
 							rs,
 							i,
 							persisters[i],
 							descriptors[i].getRowIdAlias(),
 							key,
 							lockModes[i],
 							optionalObjectKey,
 							optionalObject,
 							hydratedObjects,
 							session
 					);
 				}
 			}
 
 			rowResults[i] = object;
 
 		}
 
 		return rowResults;
 	}
 
 	/**
 	 * The entity instance is already in the session cache
 	 */
 	private void instanceAlreadyLoaded(
 			final ResultSet rs,
 			final int i,
 			final Loadable persister,
 			final EntityKey key,
 			final Object object,
 			final LockMode requestedLockMode,
 			final SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( !persister.isInstance( object ) ) {
 			throw new WrongClassException(
 					"loaded object was of wrong class " + object.getClass(),
 					key.getIdentifier(),
 					persister.getEntityName()
 			);
 		}
 
 		if ( LockMode.NONE != requestedLockMode && upgradeLocks() ) { //no point doing this if NONE was requested
 			final EntityEntry entry = session.getPersistenceContext().getEntry( object );
 			if ( entry.getLockMode().lessThan( requestedLockMode ) ) {
 				//we only check the version when _upgrading_ lock modes
 				if ( persister.isVersioned() ) {
 					checkVersion( i, persister, key.getIdentifier(), object, rs, session );
 				}
 				//we need to upgrade the lock mode to the mode requested
 				entry.setLockMode( requestedLockMode );
 			}
 		}
 	}
 
 
 	/**
 	 * The entity instance is not in the session cache
 	 */
 	private Object instanceNotYetLoaded(
 			final ResultSet rs,
 			final int i,
 			final Loadable persister,
 			final String rowIdAlias,
 			final EntityKey key,
 			final LockMode lockMode,
 			final EntityKey optionalObjectKey,
 			final Object optionalObject,
 			final List hydratedObjects,
 			final SessionImplementor session)
 			throws HibernateException, SQLException {
 		final String instanceClass = getInstanceClass(
 				rs,
 				i,
 				persister,
 				key.getIdentifier(),
 				session
 		);
 
 		// see if the entity defines reference caching, and if so use the cached reference (if one).
 		if ( session.getCacheMode().isGetEnabled() && persister.canUseReferenceCacheEntries() ) {
 			final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-			final EntityCacheKey ck = cache.generateCacheKey(
+			final Object ck = cache.generateCacheKey(
 					key.getIdentifier(),
 					persister,
 					session.getFactory(),
 					session.getTenantIdentifier()
 					);
 			final Object cachedEntry = CacheHelper.fromSharedCache( session, ck, cache );
 			if ( cachedEntry != null ) {
 				CacheEntry entry = (CacheEntry) persister.getCacheEntryStructure().destructure( cachedEntry, factory );
 				return ( (ReferenceCacheEntryImpl) entry ).getReference();
 			}
 		}
 
 		final Object object;
 		if ( optionalObjectKey != null && key.equals( optionalObjectKey ) ) {
 			//its the given optional object
 			object = optionalObject;
 		}
 		else {
 			// instantiate a new instance
 			object = session.instantiate( instanceClass, key.getIdentifier() );
 		}
 
 		//need to hydrate it.
 
 		// grab its state from the ResultSet and keep it in the Session
 		// (but don't yet initialize the object itself)
 		// note that we acquire LockMode.READ even if it was not requested
 		LockMode acquiredLockMode = lockMode == LockMode.NONE ? LockMode.READ : lockMode;
 		loadFromResultSet(
 				rs,
 				i,
 				object,
 				instanceClass,
 				key,
 				rowIdAlias,
 				acquiredLockMode,
 				persister,
 				session
 		);
 
 		//materialize associations (and initialize the object) later
 		hydratedObjects.add( object );
 
 		return object;
 	}
 
 	private boolean isEagerPropertyFetchEnabled(int i) {
 		boolean[] array = getEntityEagerPropertyFetches();
 		return array != null && array[i];
 	}
 
 
 	/**
 	 * Hydrate the state an object from the SQL <tt>ResultSet</tt>, into
 	 * an array or "hydrated" values (do not resolve associations yet),
 	 * and pass the hydrates state to the session.
 	 */
 	private void loadFromResultSet(
 			final ResultSet rs,
 			final int i,
 			final Object object,
 			final String instanceEntityName,
 			final EntityKey key,
 			final String rowIdAlias,
 			final LockMode lockMode,
 			final Loadable rootPersister,
 			final SessionImplementor session) throws SQLException, HibernateException {
 
 		final Serializable id = key.getIdentifier();
 
 		// Get the persister for the _subclass_
 		final Loadable persister = (Loadable) getFactory().getEntityPersister( instanceEntityName );
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Initializing object from ResultSet: {0}", MessageHelper.infoString(
 							persister,
 							id,
 							getFactory()
 					)
 			);
 		}
 
 		boolean eagerPropertyFetch = isEagerPropertyFetchEnabled( i );
 
 		// add temp entry so that the next step is circular-reference
 		// safe - only needed because some types don't take proper
 		// advantage of two-phase-load (esp. components)
 		TwoPhaseLoad.addUninitializedEntity(
 				key,
 				object,
 				persister,
 				lockMode,
 				!eagerPropertyFetch,
 				session
 		);
 
 		//This is not very nice (and quite slow):
 		final String[][] cols = persister == rootPersister ?
 				getEntityAliases()[i].getSuffixedPropertyAliases() :
 				getEntityAliases()[i].getSuffixedPropertyAliases( persister );
 
 		final Object[] values = persister.hydrate(
 				rs,
 				id,
 				object,
 				rootPersister,
 				cols,
 				eagerPropertyFetch,
 				session
 		);
 
 		final Object rowId = persister.hasRowId() ? rs.getObject( rowIdAlias ) : null;
 
 		final AssociationType[] ownerAssociationTypes = getOwnerAssociationTypes();
 		if ( ownerAssociationTypes != null && ownerAssociationTypes[i] != null ) {
 			String ukName = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName();
 			if ( ukName != null ) {
 				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex( ukName );
 				final Type type = persister.getPropertyTypes()[index];
 
 				// polymorphism not really handled completely correctly,
 				// perhaps...well, actually its ok, assuming that the
 				// entity name used in the lookup is the same as the
 				// the one used here, which it will be
 
 				EntityUniqueKey euk = new EntityUniqueKey(
 						rootPersister.getEntityName(), //polymorphism comment above
 						ukName,
 						type.semiResolve( values[index], session, object ),
 						type,
 						persister.getEntityMode(),
 						session.getFactory()
 				);
 				session.getPersistenceContext().addEntity( euk, object );
 			}
 		}
 
 		TwoPhaseLoad.postHydrate(
 				persister,
 				id,
 				values,
 				rowId,
 				object,
 				lockMode,
 				!eagerPropertyFetch,
 				session
 		);
 
 	}
 
 	/**
 	 * Determine the concrete class of an instance in the <tt>ResultSet</tt>
 	 */
 	private String getInstanceClass(
 			final ResultSet rs,
 			final int i,
 			final Loadable persister,
 			final Serializable id,
 			final SessionImplementor session) throws HibernateException, SQLException {
 
 		if ( persister.hasSubclasses() ) {
 
 			// Code to handle subclasses of topClass
 			final Object discriminatorValue = persister.getDiscriminatorType().nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedDiscriminatorAlias(),
 					session,
 					null
 			);
 
 			final String result = persister.getSubclassForDiscriminatorValue( discriminatorValue );
 
 			if ( result == null ) {
 				//woops we got an instance of another class hierarchy branch
 				throw new WrongClassException(
 						"Discriminator: " + discriminatorValue,
 						id,
 						persister.getEntityName()
 				);
 			}
 
 			return result;
 
 		}
 		else {
 			return persister.getEntityName();
 		}
 	}
 
 	/**
 	 * Advance the cursor to the first required row of the <tt>ResultSet</tt>
 	 */
 	private void advance(final ResultSet rs, final RowSelection selection) throws SQLException {
 
 		final int firstRow = LimitHelper.getFirstRow( selection );
 		if ( firstRow != 0 ) {
 			if ( getFactory().getSessionFactoryOptions().isScrollableResultSetsEnabled() ) {
 				// we can go straight to the first required row
 				rs.absolute( firstRow );
 			}
 			else {
 				// we need to step through the rows one row at a time (slow)
 				for ( int m = 0; m < firstRow; m++ ) {
 					rs.next();
 				}
 			}
 		}
 	}
 
 	/**
 	 * Build LIMIT clause handler applicable for given selection criteria. Returns {@link NoopLimitHandler} delegate
 	 * if dialect does not support LIMIT expression or processed query does not use pagination.
 	 *
 	 * @param selection Selection criteria.
 	 *
 	 * @return LIMIT clause delegate.
 	 */
 	protected LimitHandler getLimitHandler(RowSelection selection) {
 		final LimitHandler limitHandler = getFactory().getDialect().getLimitHandler();
 		return LimitHelper.useLimit( limitHandler, selection ) ? limitHandler : NoopLimitHandler.INSTANCE;
 	}
 
 	private ScrollMode getScrollMode(
 			boolean scroll,
 			boolean hasFirstRow,
 			boolean useLimitOffSet,
 			QueryParameters queryParameters) {
 		final boolean canScroll = getFactory().getSessionFactoryOptions().isScrollableResultSetsEnabled();
 		if ( canScroll ) {
 			if ( scroll ) {
 				return queryParameters.getScrollMode();
 			}
 			if ( hasFirstRow && !useLimitOffSet ) {
 				return ScrollMode.SCROLL_INSENSITIVE;
 			}
 		}
 		return null;
 	}
 
 	/**
 	 * Process query string by applying filters, LIMIT clause, locks and comments if necessary.
 	 * Finally execute SQL statement and advance to the first row.
 	 */
 	protected SqlStatementWrapper executeQueryStatement(
 			final QueryParameters queryParameters,
 			final boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			final SessionImplementor session) throws SQLException {
 		return executeQueryStatement( getSQLString(), queryParameters, scroll, afterLoadActions, session );
 	}
 
 	protected SqlStatementWrapper executeQueryStatement(
 			String sqlStatement,
 			QueryParameters queryParameters,
 			boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			SessionImplementor session) throws SQLException {
 
 		// Processing query filters.
 		queryParameters.processFilters( sqlStatement, session );
 
 		// Applying LIMIT clause.
 		final LimitHandler limitHandler = getLimitHandler(
 				queryParameters.getRowSelection()
 		);
 		String sql = limitHandler.processSql( queryParameters.getFilteredSQL(), queryParameters.getRowSelection() );
 
 		// Adding locks and comments.
 		sql = preprocessSQL( sql, queryParameters, getFactory().getDialect(), afterLoadActions );
 
 		final PreparedStatement st = prepareQueryStatement( sql, queryParameters, limitHandler, scroll, session );
 		return new SqlStatementWrapper(
 				st, getResultSet(
 				st,
 				queryParameters.getRowSelection(),
 				limitHandler,
 				queryParameters.hasAutoDiscoverScalarTypes(),
 				session
 		)
 		);
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
 			String sql,
 			final QueryParameters queryParameters,
 			final LimitHandler limitHandler,
 			final boolean scroll,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		final Dialect dialect = getFactory().getDialect();
 		final RowSelection selection = queryParameters.getRowSelection();
 		final boolean useLimit = LimitHelper.useLimit( limitHandler, selection );
 		final boolean hasFirstRow = LimitHelper.hasFirstRow( selection );
 		final boolean useLimitOffset = hasFirstRow && useLimit && limitHandler.supportsLimitOffset();
 		final boolean callable = queryParameters.isCallable();
 		final ScrollMode scrollMode = getScrollMode( scroll, hasFirstRow, useLimitOffset, queryParameters );
 
 		PreparedStatement st = session.getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(
 				sql,
 				callable,
 				scrollMode
 		);
 
 		try {
 
 			int col = 1;
 			//TODO: can we limit stored procedures ?!
 			col += limitHandler.bindLimitParametersAtStartOfQuery( selection, st, col );
 
 			if ( callable ) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement) st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
 			col += limitHandler.bindLimitParametersAtEndOfQuery( selection, st, col );
 
 			limitHandler.setMaxRows( selection, st );
 
 			if ( selection != null ) {
 				if ( selection.getTimeout() != null ) {
 					st.setQueryTimeout( selection.getTimeout() );
 				}
 				if ( selection.getFetchSize() != null ) {
 					st.setFetchSize( selection.getFetchSize() );
 				}
 			}
 
 			// handle lock timeout...
 			LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 					if ( !dialect.supportsLockTimeouts() ) {
 						if ( LOG.isDebugEnabled() ) {
 							LOG.debugf(
 									"Lock timeout [%s] requested but dialect reported to not support lock timeouts",
 									lockOptions.getTimeOut()
 							);
 						}
 					}
 					else if ( dialect.isLockTimeoutParameterized() ) {
 						st.setInt( col++, lockOptions.getTimeOut() );
 					}
 				}
 			}
 
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Bound [{0}] parameters total", col );
 			}
 		}
 		catch (SQLException sqle) {
 			session.getJdbcCoordinator().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 			throw sqle;
 		}
 		catch (HibernateException he) {
 			session.getJdbcCoordinator().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 			throw he;
 		}
 
 		return st;
 	}
 
 	/**
 	 * Bind all parameter values into the prepared statement in preparation
 	 * for execution.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 *
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 *
 	 * @throws SQLException Indicates problems performing the binding.
 	 */
 	protected int bindParameterValues(
 			PreparedStatement statement,
 			QueryParameters queryParameters,
 			int startIndex,
 			SessionImplementor session) throws SQLException {
 		int span = 0;
 		span += bindPositionalParameters( statement, queryParameters, startIndex, session );
 		span += bindNamedParameters( statement, queryParameters.getNamedParameters(), startIndex + span, session );
 		return span;
 	}
 
 	/**
 	 * Bind positional parameter values to the JDBC prepared statement.
 	 * <p/>
 	 * Positional parameters are those specified by JDBC-style ? parameters
 	 * in the source query.  It is (currently) expected that these come
 	 * before any named parameters in the source query.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 *
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 *
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindPositionalParameters(
 			final PreparedStatement statement,
 			final QueryParameters queryParameters,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		final Object[] values = queryParameters.getFilteredPositionalParameterValues();
 		final Type[] types = queryParameters.getFilteredPositionalParameterTypes();
 		int span = 0;
 		for ( int i = 0; i < values.length; i++ ) {
 			types[i].nullSafeSet( statement, values[i], startIndex + span, session );
 			span += types[i].getColumnSpan( getFactory() );
 		}
 		return span;
 	}
 
 	/**
 	 * Bind named parameters to the JDBC prepared statement.
 	 * <p/>
 	 * This is a generic implementation, the problem being that in the
 	 * general case we do not know enough information about the named
 	 * parameters to perform this in a complete manner here.  Thus this
 	 * is generally overridden on subclasses allowing named parameters to
 	 * apply the specific behavior.  The most usual limitation here is that
 	 * we need to assume the type span is always one...
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param namedParams A map of parameter names to values
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 *
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 *
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindNamedParameters(
 			final PreparedStatement statement,
 			final Map<String, TypedValue> namedParams,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		int result = 0;
 		if ( CollectionHelper.isEmpty( namedParams ) ) {
 			return result;
 		}
 
 		for ( String name : namedParams.keySet() ) {
 			TypedValue typedValue = namedParams.get( name );
 			int columnSpan = typedValue.getType().getColumnSpan( getFactory() );
 			int[] locs = getNamedParameterLocs( name );
 			for ( int loc : locs ) {
 				if ( DEBUG_ENABLED ) {
 					LOG.debugf(
 							"bindNamedParameters() %s -> %s [%s]",
 							typedValue.getValue(),
 							name,
 							loc + startIndex
 					);
 				}
 				int start = loc * columnSpan + startIndex;
 				typedValue.getType().nullSafeSet( statement, typedValue.getValue(), start, session );
 			}
 			result += locs.length;
 		}
 		return result;
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		throw new AssertionFailure( "no named parameters" );
 	}
 
 	/**
 	 * Execute given <tt>PreparedStatement</tt>, advance to the first result and return SQL <tt>ResultSet</tt>.
 	 */
 	protected final ResultSet getResultSet(
 			final PreparedStatement st,
 			final RowSelection selection,
 			final LimitHandler limitHandler,
 			final boolean autodiscovertypes,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		try {
 			ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
 			rs = wrapResultSetIfEnabled( rs, session );
 
 			if ( !limitHandler.supportsLimitOffset() || !LimitHelper.useLimit( limitHandler, selection ) ) {
 				advance( rs, selection );
 			}
 
 			if ( autodiscovertypes ) {
 				autoDiscoverTypes( rs );
 			}
 			return rs;
 		}
 		catch (SQLException sqle) {
 			session.getJdbcCoordinator().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 			throw sqle;
 		}
 	}
 
 	protected void autoDiscoverTypes(ResultSet rs) {
 		throw new AssertionFailure( "Auto discover types not supported in this loader" );
 
 	}
 
 	private ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
 		if ( session.getFactory().getSessionFactoryOptions().isWrapResultSetsEnabled() ) {
 			try {
 				LOG.debugf( "Wrapping result set [%s]", rs );
 				return session.getFactory()
 						.getServiceRegistry()
 						.getService( JdbcServices.class )
 						.getResultSetWrapper().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch (SQLException e) {
 				LOG.unableToWrapResultSet( e );
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(final ResultSet rs) throws SQLException {
 		final ColumnNameCache cache = columnNameCache;
 		if ( cache == null ) {
 			//there is no need for a synchronized second check, as in worst case
 			//we'll have allocated an unnecessary ColumnNameCache
 			LOG.trace( "Building columnName -> columnIndex cache" );
 			columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
 			return columnNameCache;
 		}
 		else {
 			return cache;
 		}
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 */
 	protected final List loadEntity(
 			final SessionImplementor session,
 			final Object id,
 			final Type identifierType,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalIdentifier,
 			final EntityPersister persister,
 			LockOptions lockOptions) throws HibernateException {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Loading entity: %s", MessageHelper.infoString( persister, id, identifierType, getFactory() ) );
 		}
 
 		List result;
 		try {
 			QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( new Type[] {identifierType} );
 			qp.setPositionalParameterValues( new Object[] {id} );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( optionalEntityName );
 			qp.setOptionalId( optionalIdentifier );
 			qp.setLockOptions( lockOptions );
 			result = doQueryAndInitializeNonLazyCollections( session, qp, false );
 		}
 		catch (SQLException sqle) {
 			final Loadable[] persisters = getEntityPersisters();
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not load an entity: " +
 							MessageHelper.infoString(
 									persisters[persisters.length - 1],
 									id,
 									identifierType,
 									getFactory()
 							),
 					getSQLString()
 			);
 		}
 
 		LOG.debug( "Done entity load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 *
 	 * @param persister only needed for logging
 	 */
 	protected final List loadEntity(
 			final SessionImplementor session,
 			final Object key,
 			final Object index,
 			final Type keyType,
 			final Type indexType,
 			final EntityPersister persister) throws HibernateException {
 		LOG.debug( "Loading collection element by index" );
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters(
 							new Type[] {keyType, indexType},
 							new Object[] {key, index}
 					),
 					false
 			);
 		}
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not load collection element by index",
 					getSQLString()
 			);
 		}
 
 		LOG.debug( "Done entity load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by wrappers that batch load entities
 	 */
 	public final List loadEntityBatch(
 			final SessionImplementor session,
 			final Serializable[] ids,
 			final Type idType,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalId,
 			final EntityPersister persister,
 			LockOptions lockOptions) throws HibernateException {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Batch loading entity: %s", MessageHelper.infoString( persister, ids, getFactory() ) );
 		}
 
 		Type[] types = new Type[ids.length];
 		Arrays.fill( types, idType );
 		List result;
 		try {
 			QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( types );
 			qp.setPositionalParameterValues( ids );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( optionalEntityName );
 			qp.setOptionalId( optionalId );
 			qp.setLockOptions( lockOptions );
 			result = doQueryAndInitializeNonLazyCollections( session, qp, false );
 		}
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not load an entity batch: " +
 							MessageHelper.infoString( getEntityPersisters()[0], ids, getFactory() ),
 					getSQLString()
 			);
 		}
 
 		LOG.debug( "Done entity batch load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that initialize collections
 	 */
 	public final void loadCollection(
 			final SessionImplementor session,
 			final Serializable id,
 			final Type type) throws HibernateException {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() )
 			);
 		}
 
 		Serializable[] ids = new Serializable[] {id};
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( new Type[] {type}, ids, ids ),
 					true
 			);
 		}
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize a collection: " +
 							MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ),
 					getSQLString()
 			);
 		}
 
 		LOG.debug( "Done loading collection" );
 	}
 
 	/**
 	 * Called by wrappers that batch initialize collections
 	 */
 	public final void loadCollectionBatch(
 			final SessionImplementor session,
 			final Serializable[] ids,
 			final Type type) throws HibernateException {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Batch loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() )
 			);
 		}
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( idTypes, ids, ids ),
 					true
 			);
 		}
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize a collection batch: " +
 							MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 					getSQLString()
 			);
 		}
 
 		LOG.debug( "Done batch load" );
 	}
 
 	/**
 	 * Called by subclasses that batch initialize collections
 	 */
 	protected final void loadCollectionSubselect(
 			final SessionImplementor session,
 			final Serializable[] ids,
 			final Object[] parameterValues,
 			final Type[] parameterTypes,
 			final Map<String, TypedValue> namedParameters,
 			final Type type) throws HibernateException {
 		final Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( parameterTypes, parameterValues, namedParameters, ids ),
 					true
 			);
 		}
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not load collection by subselect: " +
 							MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 					getSQLString()
 			);
 		}
 	}
 
 	/**
 	 * Return the query results, using the query cache, called
 	 * by subclasses that implement cacheable queries
 	 */
 	protected List list(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Set<Serializable> querySpaces,
 			final Type[] resultTypes) throws HibernateException {
 		final boolean cacheable = factory.getSessionFactoryOptions().isQueryCacheEnabled() &&
 				queryParameters.isCacheable();
 
 		if ( cacheable ) {
 			return listUsingQueryCache( session, queryParameters, querySpaces, resultTypes );
 		}
 		else {
 			return listIgnoreQueryCache( session, queryParameters );
 		}
 	}
 
 	private List listIgnoreQueryCache(SessionImplementor session, QueryParameters queryParameters) {
 		return getResultList( doList( session, queryParameters ), queryParameters.getResultTransformer() );
 	}
 
 	private List listUsingQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Set<Serializable> querySpaces,
 			final Type[] resultTypes) {
 
 		QueryCache queryCache = factory.getQueryCache( queryParameters.getCacheRegion() );
 
 		QueryKey key = generateQueryKey( session, queryParameters );
 
 		if ( querySpaces == null || querySpaces.size() == 0 ) {
 			LOG.tracev( "Unexpected querySpaces is {0}", ( querySpaces == null ? querySpaces : "empty" ) );
 		}
 		else {
 			LOG.tracev( "querySpaces is {0}", querySpaces );
 		}
 
 		List result = getResultFromQueryCache(
 				session,
 				queryParameters,
 				querySpaces,
 				resultTypes,
 				queryCache,
 				key
 		);
 
 		if ( result == null ) {
 			result = doList( session, queryParameters, key.getResultTransformer() );
 
 			putResultInQueryCache(
 					session,
 					queryParameters,
 					resultTypes,
 					queryCache,
 					key,
 					result
 			);
 		}
 
 		ResultTransformer resolvedTransformer = resolveResultTransformer( queryParameters.getResultTransformer() );
 		if ( resolvedTransformer != null ) {
 			result = (
 					areResultSetRowsTransformedImmediately() ?
 							key.getResultTransformer().retransformResults(
 									result,
 									getResultRowAliases(),
 									queryParameters.getResultTransformer(),
 									includeInResultRow()
 							) :
 							key.getResultTransformer().untransformToTuples(
 									result
 							)
 			);
 		}
 
 		return getResultList( result, queryParameters.getResultTransformer() );
 	}
 
 	private QueryKey generateQueryKey(
 			SessionImplementor session,
 			QueryParameters queryParameters) {
 		return QueryKey.generateQueryKey(
 				getSQLString(),
 				queryParameters,
 				FilterKey.createFilterKeys( session.getLoadQueryInfluencers().getEnabledFilters() ),
 				session,
 				createCacheableResultTransformer( queryParameters )
 		);
 	}
 
 	private CacheableResultTransformer createCacheableResultTransformer(QueryParameters queryParameters) {
 		return CacheableResultTransformer.create(
 				queryParameters.getResultTransformer(),
 				getResultRowAliases(),
 				includeInResultRow()
 		);
 	}
 
 	private List getResultFromQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Set<Serializable> querySpaces,
 			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key) {
 		List result = null;
 
 		if ( session.getCacheMode().isGetEnabled() ) {
 			boolean isImmutableNaturalKeyLookup =
 					queryParameters.isNaturalKeyLookup() &&
 							resultTypes.length == 1 &&
 							resultTypes[0].isEntityType() &&
 							getEntityPersister( EntityType.class.cast( resultTypes[0] ) )
 									.getEntityMetamodel()
 									.hasImmutableNaturalId();
 
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
 			try {
 				result = queryCache.get(
 						key,
 						key.getResultTransformer().getCachedResultTypes( resultTypes ),
 						isImmutableNaturalKeyLookup,
 						querySpaces,
 						session
 				);
 			}
 			finally {
 				persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 			}
 
 			if ( factory.getStatistics().isStatisticsEnabled() ) {
 				if ( result == null ) {
 					factory.getStatisticsImplementor()
 							.queryCacheMiss( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 				else {
 					factory.getStatisticsImplementor()
 							.queryCacheHit( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 			}
 		}
 
 		return result;
 	}
 
 	private EntityPersister getEntityPersister(EntityType entityType) {
 		return factory.getEntityPersister( entityType.getAssociatedEntityName() );
 	}
 
 	protected void putResultInQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key,
 			final List result) {
 		if ( session.getCacheMode().isPutEnabled() ) {
 			boolean put = queryCache.put(
 					key,
 					key.getResultTransformer().getCachedResultTypes( resultTypes ),
 					result,
 					queryParameters.isNaturalKeyLookup(),
 					session
 			);
 			if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor()
 						.queryCachePut( getQueryIdentifier(), queryCache.getRegion().getName() );
 			}
 		}
 	}
 
 	/**
 	 * Actually execute a query, ignoring the query cache
 	 */
 
 	protected List doList(final SessionImplementor session, final QueryParameters queryParameters)
 			throws HibernateException {
 		return doList( session, queryParameters, null );
 	}
 
 	private List doList(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final ResultTransformer forcedResultTransformer)
 			throws HibernateException {
 
 		final boolean stats = getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) {
 			startTime = System.nanoTime();
 		}
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections( session, queryParameters, true, forcedResultTransformer );
 		}
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not execute query",
 					getSQLString()
 			);
 		}
 
 		if ( stats ) {
 			final long endTime = System.nanoTime();
 			final long milliseconds = TimeUnit.MILLISECONDS.convert( endTime - startTime, TimeUnit.NANOSECONDS );
 			getFactory().getStatisticsImplementor().queryExecuted(
 					getQueryIdentifier(),
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 499ffbdfdc..3d4ea3adc7 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1910 +1,1909 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StructuredCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.CacheHelper;
 import org.hibernate.engine.internal.ImmutableEntityEntryFactory;
 import org.hibernate.engine.internal.MutableEntityEntryFactory;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityEntryFactory;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext.NaturalIdHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.jdbc.TooManyRowsAffectedException;
 import org.hibernate.loader.entity.BatchingEntityLoaderBuilder;
 import org.hibernate.loader.entity.CascadeEntityLoader;
 import org.hibernate.loader.entity.EntityLoader;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.spi.PersisterCreationContext;
 import org.hibernate.persister.walking.internal.EntityIdentifierDefinitionHelper;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.access.internal.PropertyAccessStrategyBackRefImpl;
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
 import org.hibernate.tuple.GenerationTiming;
 import org.hibernate.tuple.InDatabaseValueGenerationStrategy;
 import org.hibernate.tuple.InMemoryValueGenerationStrategy;
 import org.hibernate.tuple.NonIdentifierAttribute;
 import org.hibernate.tuple.ValueGeneration;
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
 
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( AbstractEntityPersister.class );
 
 	public static final String ENTITY_CLASS = "class";
 
 	// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final SessionFactoryImplementor factory;
 	private final EntityRegionAccessStrategy cacheAccessStrategy;
 	private final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy;
 	private final boolean isLazyPropertiesCacheable;
 	private final CacheEntryHelper cacheEntryHelper;
 	private final EntityMetamodel entityMetamodel;
 	private final EntityTuplizer entityTuplizer;
 	private final EntityEntryFactory entityEntryFactory;
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
 
 	private final List<Integer> lobProperties = new ArrayList<Integer>();
 
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
 
 	private final Set<String> affectingFetchProfileNames = new HashSet<String>();
 
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
 
 	private final Map subclassPropertyAliases = new HashMap();
 	private final Map subclassPropertyColumnNames = new HashMap();
 
 	protected final BasicEntityPropertyMapping propertyMapping;
 
 	private final boolean useReferenceCacheEntries;
 
 	protected void addDiscriminatorToInsert(Insert insert) {
 	}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 	}
 
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
 
 	protected abstract String filterFragment(String alias, Set<String> treatAsDeclarations);
 
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
 		return entityMetamodel.getSubclassEntityNames().contains( entityName );
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
 		System.arraycopy( sqlLazyUpdateStrings, 1, result, 1, getTableSpan() - 1 );
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
 			boolean[] tableUpdateNeeded = new boolean[getTableSpan()];
 			for ( int property : dirtyProperties ) {
 				int table = propertyTableNumbers[property];
 				tableUpdateNeeded[table] = tableUpdateNeeded[table] ||
 						( getPropertyColumnSpan( property ) > 0 && updateability[property] );
 			}
 			if ( isVersioned() ) {
 				tableUpdateNeeded[0] = tableUpdateNeeded[0] ||
 						Versioning.isVersionIncrementRequired(
 								dirtyProperties,
 								hasDirtyCollection,
 								getPropertyVersionability()
 						);
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
 
 	@SuppressWarnings("UnnecessaryBoxing")
 	public AbstractEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 			final PersisterCreationContext creationContext) throws HibernateException {
 
 		// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		this.factory = creationContext.getSessionFactory();
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		this.naturalIdRegionAccessStrategy = naturalIdRegionAccessStrategy;
 		isLazyPropertiesCacheable = persistentClass.isLazyPropertiesCacheable();
 
 		this.entityMetamodel = new EntityMetamodel( persistentClass, this, factory );
 		this.entityTuplizer = this.entityMetamodel.getTuplizer();
 
 		if ( entityMetamodel.isMutable() ) {
 			this.entityEntryFactory = MutableEntityEntryFactory.INSTANCE;
 		}
 		else {
 			this.entityEntryFactory = ImmutableEntityEntryFactory.INSTANCE;
 		}
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		int batch = persistentClass.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSessionFactoryOptions().getDefaultBatchFetchSize();
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
 			Column col = (Column) iter.next();
 			rootTableKeyColumnNames[i] = col.getQuotedName( factory.getDialect() );
 			rootTableKeyColumnReaders[i] = col.getReadExpr( factory.getDialect() );
 			rootTableKeyColumnReaderTemplates[i] = col.getTemplate(
 					factory.getDialect(),
 					factory.getSqlFunctionRegistry()
 			);
 			identifierAliases[i] = col.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( persistentClass.isVersioned() ) {
 			versionColumnName = ( (Column) persistentClass.getVersion().getColumnIterator().next() ).getQuotedName(
 					factory.getDialect()
 			);
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( persistentClass.getWhere() ) ?
 				"( " + persistentClass.getWhere() + ") " :
 				null;
 		sqlWhereStringTemplate = sqlWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate(
 						sqlWhereString,
 						factory.getDialect(),
 						factory.getSqlFunctionRegistry()
 				);
 
 		// PROPERTIES
 
 		final boolean lazyAvailable = isInstrumented();
 
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
 			Property prop = (Property) iter.next();
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
 				Selectable thing = (Selectable) colIter.next();
 				colAliases[k] = thing.getAlias( factory.getDialect(), prop.getValue().getTable() );
 				if ( thing.isFormula() ) {
 					foundFormula = true;
 					formulaTemplates[k] = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				}
 				else {
 					Column col = (Column) thing;
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
 
 			if ( prop.isLob() && getFactory().getDialect().forceLobAsLastValue() ) {
 				lobProperties.add( i );
 			}
 
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
 			Property prop = (Property) iter.next();
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
 				Selectable thing = (Selectable) colIter.next();
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
 					Column col = (Column) thing;
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
 			subclassPropertyCascadeStyleClosure[j++] = (CascadeStyle) iter.next();
 		}
 		subclassPropertyFetchModeClosure = new FetchMode[joinedFetchesList.size()];
 		iter = joinedFetchesList.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyFetchModeClosure[j++] = (FetchMode) iter.next();
 		}
 
 		propertyDefinedOnSubclass = new boolean[definedBySubclass.size()];
 		iter = definedBySubclass.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			propertyDefinedOnSubclass[j++] = (Boolean) iter.next();
 		}
 
 		// Handle any filters applied to the class level
 		filterHelper = new FilterHelper( persistentClass.getFilters(), factory );
 
 		// Check if we can use Reference Cached entities in 2lc
 		// todo : should really validate that the cache access type is read-only
 		boolean refCacheEntries = true;
 		if ( !factory.getSessionFactoryOptions().isDirectReferenceCacheEntriesEnabled() ) {
 			refCacheEntries = false;
 		}
 
 		// for now, limit this to just entities that:
 		// 		1) are immutable
 		if ( entityMetamodel.isMutable() ) {
 			refCacheEntries = false;
 		}
 
 		//		2)  have no associations.  Eventually we want to be a little more lenient with associations.
 		for ( Type type : getSubclassPropertyTypeClosure() ) {
 			if ( type.isAssociationType() ) {
 				refCacheEntries = false;
 			}
 		}
 
 		useReferenceCacheEntries = refCacheEntries;
 
 		this.cacheEntryHelper = buildCacheEntryHelper();
 
 	}
 
 	protected CacheEntryHelper buildCacheEntryHelper() {
 		if ( cacheAccessStrategy == null ) {
 			// the entity defined no caching...
 			return NoopCacheEntryHelper.INSTANCE;
 		}
 
 		if ( canUseReferenceCacheEntries() ) {
 			entityMetamodel.setLazy( false );
 			// todo : do we also need to unset proxy factory?
 			return new ReferenceCacheEntryHelper( this );
 		}
 
 		return factory.getSessionFactoryOptions().isStructuredCacheEntriesEnabled()
 				? new StructuredCacheEntryHelper( this )
 				: new StandardCacheEntryHelper( this );
 	}
 
 	public boolean canUseReferenceCacheEntries() {
 		return useReferenceCacheEntries;
 	}
 
 	protected static String getTemplateFromString(String string, SessionFactoryImplementor factory) {
 		return string == null ?
 				null :
 				Template.renderWhereStringTemplate( string, factory.getDialect(), factory.getSqlFunctionRegistry() );
 	}
 
 	protected String generateLazySelectString() {
 
 		if ( !entityMetamodel.hasLazyProperties() ) {
 			return null;
 		}
 
 		HashSet tableNumbers = new HashSet();
 		ArrayList columnNumbers = new ArrayList();
 		ArrayList formulaNumbers = new ArrayList();
 		for ( String lazyPropertyName : lazyPropertyNames ) {
 			// all this only really needs to consider properties
 			// of this class, not its subclasses, but since we
 			// are reusing code used for sequential selects, we
 			// use the subclass closure
 			int propertyNumber = getSubclassPropertyIndex( lazyPropertyName );
 
 			int tableNumber = getSubclassPropertyTableNumber( propertyNumber );
 			tableNumbers.add( tableNumber );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int colNumber : colNumbers ) {
 				if ( colNumber != -1 ) {
 					columnNumbers.add( colNumber );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int formNumber : formNumbers ) {
 				if ( formNumber != -1 ) {
 					formulaNumbers.add( formNumber );
 				}
 			}
 		}
 
 		if ( columnNumbers.size() == 0 && formulaNumbers.size() == 0 ) {
 			// only one-to-one is lazy fetched
 			return null;
 		}
 
 		return renderSelect(
 				ArrayHelper.toIntArray( tableNumbers ),
 				ArrayHelper.toIntArray( columnNumbers ),
 				ArrayHelper.toIntArray( formulaNumbers )
 		);
 
 	}
 
 	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session)
 			throws HibernateException {
 
 		final Serializable id = session.getContextEntityIdentifier( entity );
 
 		final EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 		if ( entry == null ) {
 			throw new HibernateException( "entity is not associated with the session: " + id );
 		}
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Initializing lazy properties of: {0}, field access: {1}", MessageHelper.infoString(
 							this,
 							id,
 							getFactory()
 					), fieldName
 			);
 		}
 
 		if ( session.getCacheMode().isGetEnabled() && hasCache() ) {
 			final EntityRegionAccessStrategy cache = getCacheAccessStrategy();
-			final EntityCacheKey cacheKey = cache.generateCacheKey(id, this, session.getFactory(), session.getTenantIdentifier() );
+			final Object cacheKey = cache.generateCacheKey(id, this, session.getFactory(), session.getTenantIdentifier() );
 			final Object ce = CacheHelper.fromSharedCache( session, cacheKey, cache );
 			if ( ce != null ) {
 				final CacheEntry cacheEntry = (CacheEntry) getCacheEntryStructure().destructure( ce, factory );
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
 
 		if ( !hasLazyProperties() ) {
 			throw new AssertionFailure( "no lazy properties" );
 		}
 
 		LOG.trace( "Initializing lazy properties from datastore" );
 
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
 						ps = session.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( lazySelect );
 						getIdentifierType().nullSafeSet( ps, id, 1, session );
 						rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 						rs.next();
 					}
 					final Object[] snapshot = entry.getLoadedState();
 					for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 						Object propValue = lazyPropertyTypes[j].nullSafeGet(
 								rs,
 								lazyPropertyColumnAliases[j],
 								session,
 								entity
 						);
 						if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 							result = propValue;
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					session.getJdbcCoordinator().getResourceRegistry().release( ps );
 					session.getJdbcCoordinator().afterStatementExecution();
 				}
 			}
 
 			LOG.trace( "Done initializing lazy properties" );
 
 			return result;
 
 		}
 		catch (SQLException sqle) {
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
 
 		LOG.trace( "Initializing lazy properties from second-level cache" );
 
 		Object result = null;
 		Serializable[] disassembledValues = cacheEntry.getDisassembledState();
 		final Object[] snapshot = entry.getLoadedState();
 		for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 			final Object propValue = lazyPropertyTypes[j].assemble(
 					disassembledValues[lazyPropertyNumbers[j]],
 					session,
 					entity
 			);
 			if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 				result = propValue;
 			}
 		}
 
 		LOG.trace( "Done initializing lazy properties" );
 
 		return result;
 	}
 
 	private boolean initializeLazyProperty(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Object[] snapshot,
 			final int j,
 			final Object propValue) {
 		setPropertyValue( entity, lazyPropertyNumbers[j], propValue );
 		if ( snapshot != null ) {
 			// object have been loaded with setReadOnly(true); HHH-2236
 			snapshot[lazyPropertyNumbers[j]] = lazyPropertyTypes[j].deepCopy( propValue, factory );
 		}
 		return fieldName.equals( lazyPropertyNames[j] );
 	}
 
 	public boolean isBatchable() {
 		return optimisticLockStyle() == OptimisticLockStyle.NONE
 				|| ( !isVersioned() && optimisticLockStyle() == OptimisticLockStyle.VERSION )
 				|| getFactory().getSessionFactoryOptions().isJdbcBatchVersionedData();
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Getting current persistent state for: {0}", MessageHelper.infoString(
 							this,
 							id,
 							getFactory()
 					)
 			);
 		}
 
 		try {
 			PreparedStatement ps = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getSQLSnapshotSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				//if ( isVersioned() ) getVersionType().nullSafeSet( ps, version, getIdentifierColumnSpan()+1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
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
 							values[i] = types[i].hydrate(
 									rs,
 									getPropertyAliases( "", i ),
 									session,
 									null
 							); //null owner ok??
 						}
 					}
 					return values;
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( ps );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 					getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	@Override
 	public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session)
 			throws HibernateException {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"resolving unique key [%s] to identifier for entity [%s]",
 					key,
 					getEntityName()
 			);
 		}
 
 		int propertyIndex = getSubclassPropertyIndex( uniquePropertyName );
 		if ( propertyIndex < 0 ) {
 			throw new HibernateException(
 					"Could not determine Type for property [" + uniquePropertyName + "] on entity [" + getEntityName() + "]"
 			);
 		}
 		Type propertyType = getSubclassPropertyType( propertyIndex );
 
 		try {
 			PreparedStatement ps = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( generateIdByUniqueKeySelectString( uniquePropertyName ) );
 			try {
 				propertyType.nullSafeSet( ps, key, 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					return (Serializable) getIdentifierType().nullSafeGet( rs, getIdentifierAliases(), session, null );
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( ps );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					String.format(
 							"could not resolve unique property [%s] to identifier for entity [%s]",
 							uniquePropertyName,
 							getEntityName()
 					),
 					getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	protected String generateIdByUniqueKeySelectString(String uniquePropertyName) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "resolve id by unique property [" + getEntityName() + "." + uniquePropertyName + "]" );
 		}
 
 		final String rooAlias = getRootAlias();
 
 		select.setFromClause( fromTableFragment( rooAlias ) + fromJoinFragment( rooAlias, true, false ) );
 
 		SelectFragment selectFragment = new SelectFragment();
 		selectFragment.addColumns( rooAlias, getIdentifierColumnNames(), getIdentifierAliases() );
 		select.setSelectClause( selectFragment );
 
 		StringBuilder whereClauseBuffer = new StringBuilder();
 		final int uniquePropertyIndex = getSubclassPropertyIndex( uniquePropertyName );
 		final String uniquePropertyTableAlias = generateTableAlias(
 				rooAlias,
 				getSubclassPropertyTableNumber( uniquePropertyIndex )
 		);
 		String sep = "";
 		for ( String columnTemplate : getSubclassPropertyColumnReaderTemplateClosure()[uniquePropertyIndex] ) {
 			if ( columnTemplate == null ) {
 				continue;
 			}
 			final String columnReference = StringHelper.replace(
 					columnTemplate,
 					Template.TEMPLATE,
 					uniquePropertyTableAlias
 			);
 			whereClauseBuffer.append( sep ).append( columnReference ).append( "=?" );
 			sep = " and ";
 		}
 		for ( String formulaTemplate : getSubclassPropertyFormulaTemplateClosure()[uniquePropertyIndex] ) {
 			if ( formulaTemplate == null ) {
 				continue;
 			}
 			final String formulaReference = StringHelper.replace(
 					formulaTemplate,
 					Template.TEMPLATE,
 					uniquePropertyTableAlias
 			);
 			whereClauseBuffer.append( sep ).append( formulaReference ).append( "=?" );
 			sep = " and ";
 		}
 		whereClauseBuffer.append( whereJoinFragment( rooAlias, true, false ) );
 
 		select.setWhereClause( whereClauseBuffer.toString() );
 
 		return select.setOuterJoins( "", "" ).toStatementString();
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
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "get version " + getEntityName() );
 		}
 		return select.addCondition( rootTableKeyColumnNames, "=?" ).toStatementString();
 	}
 
 	public boolean[] getPropertyUniqueness() {
 		return propertyUniqueness;
 	}
 
 	protected String generateInsertGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( GenerationTiming.INSERT );
 	}
 
 	protected String generateUpdateGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( GenerationTiming.ALWAYS );
 	}
 
 	private String generateGeneratedValuesSelectString(final GenerationTiming generationTimingToMatch) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "get generated state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 
 		// Here we render the select column list based on the properties defined as being generated.
 		// For partial component generation, we currently just re-select the whole component
 		// rather than trying to handle the individual generated portions.
 		String selectClause = concretePropertySelectFragment(
 				getRootAlias(),
 				new InclusionChecker() {
 					@Override
 					public boolean includeProperty(int propertyNumber) {
 						final InDatabaseValueGenerationStrategy generationStrategy
 								= entityMetamodel.getInDatabaseValueGenerationStrategies()[propertyNumber];
 						return generationStrategy != null
 								&& timingsMatch( generationStrategy.getGenerationTiming(), generationTimingToMatch );
 					}
 				}
 		);
 		selectClause = selectClause.substring( 2 );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuilder()
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
 
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "get current state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String selectClause = StringHelper.join( ", ", aliasedIdColumns ) +
 				concretePropertySelectFragment( getRootAlias(), getPropertyUpdateability() );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuilder()
 				.append(
 						StringHelper.join(
 								"=? and ",
 								aliasedIdColumns
 						)
 				)
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
 		if ( LOG.isTraceEnabled() ) {
 			LOG.trace(
 					"Forcing version increment [" + MessageHelper.infoString( this, id, getFactory() ) + "; "
 							+ getVersionType().toLoggableString( currentVersion, getFactory() ) + " -> "
 							+ getVersionType().toLoggableString( nextVersion, getFactory() ) + "]"
 			);
 		}
 
 		// todo : cache this sql...
 		String versionIncrementString = generateVersionIncrementUpdateString();
 		PreparedStatement st = null;
 		try {
 			st = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( versionIncrementString, false );
 			try {
 				getVersionType().nullSafeSet( st, nextVersion, 1, session );
 				getIdentifierType().nullSafeSet( st, id, 2, session );
 				getVersionType().nullSafeSet( st, currentVersion, 2 + getIdentifierColumnSpan(), session );
 				int rows = session.getJdbcCoordinator().getResultSetReturn().executeUpdate( st );
 				if ( rows != 1 ) {
 					throw new StaleObjectStateException( getEntityName(), id );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( st );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException sqle) {
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
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting version: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		try {
 			PreparedStatement st = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getVersionSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( st, id, 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
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
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, st );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( st );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
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
 		lockers.put( LockMode.UPGRADE_SKIPLOCKED, generateLocker( LockMode.UPGRADE_SKIPLOCKED ) );
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
 		return (LockingStrategy) lockers.get( lockMode );
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
 		String rootPropertyName = StringHelper.root( propertyPath );
 		Type type = propertyMapping.toType( rootPropertyName );
 		if ( type.isAssociationType() ) {
 			AssociationType assocType = (AssociationType) type;
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
 		int index = ArrayHelper.indexOf(
 				getSubclassPropertyNameClosure(),
 				rootPropertyName
 		); //TODO: optimize this better!
 		return index == -1 ? 0 : getSubclassPropertyTableNumber( index );
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
 
 	public static String generateTableAlias(String rootAlias, int tableNumber) {
 		if ( tableNumber == 0 ) {
 			return rootAlias;
 		}
 		StringBuilder buf = new StringBuilder().append( rootAlias );
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
 		return ArrayHelper.indexOf( subclassPropertyNameClosure, propertyName );
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
 
 	@Override
 	public String[][] getSubclassPropertyFormulaTemplateClosure() {
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
 
 	@Override
 	public int[] resolveAttributeIndexes(String[] attributeNames) {
 		if ( attributeNames == null || attributeNames.length == 0 ) {
 			return new int[0];
 		}
 		int[] fields = new int[attributeNames.length];
 		int counter = 0;
 
 		// We sort to get rid of duplicates
 		Arrays.sort( attributeNames );
 
 		Integer index0 = entityMetamodel.getPropertyIndexOrNull( attributeNames[0] );
 		if ( index0 != null ) {
 			fields[counter++] = index0;
 		}
 
 		for ( int i = 0, j = 1; j < attributeNames.length; ++i, ++j ) {
 			if ( !attributeNames[i].equals( attributeNames[j] ) ) {
 				Integer index = entityMetamodel.getPropertyIndexOrNull( attributeNames[j] );
 				if ( index != null ) {
 					fields[counter++] = index;
 				}
 			}
 		}
 
 		return Arrays.copyOf( fields, counter );
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
 
@@ -3274,2001 +3273,2001 @@ public abstract class AbstractEntityPersister
 						updateStrings[j],
 						session
 				);
 			}
 		}
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 		// apply any pre-insert in-memory value generation
 		preInsertInMemoryValueGeneration( fields, object, session );
 
 		final int span = getTableSpan();
 		final Serializable id;
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			id = insert( fields, notNull, generateInsertString( true, notNull ), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			id = insert( fields, getPropertyInsertability(), getSQLIdentityInsertString(), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 		return id;
 	}
 
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		// apply any pre-insert in-memory value generation
 		preInsertInMemoryValueGeneration( fields, object, session );
 
 		final int span = getTableSpan();
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 	}
 
 	private void preInsertInMemoryValueGeneration(Object[] fields, Object object, SessionImplementor session) {
 		if ( getEntityMetamodel().hasPreInsertGeneratedValues() ) {
 			final InMemoryValueGenerationStrategy[] strategies = getEntityMetamodel().getInMemoryValueGenerationStrategies();
 			for ( int i = 0; i < strategies.length; i++ ) {
 				if ( strategies[i] != null && strategies[i].getGenerationTiming().includesInsert() ) {
 					fields[i] = strategies[i].getValueGenerator().generateValue( (Session) session, object );
 					setPropertyValue( object, i, fields[i] );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Delete an object
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 			throws HibernateException {
 		final int span = getTableSpan();
 		boolean isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && isAllOrDirtyOptLocking();
 		Object[] loadedState = null;
 		if ( isImpliedOptimisticLocking ) {
 			// need to treat this as if it where optimistic-lock="all" (dirty does *not* make sense);
 			// first we need to locate the "loaded" state
 			//
 			// Note, it potentially could be a proxy, so doAfterTransactionCompletion the location the safe way...
 			final EntityKey key = session.generateEntityKey( id, this );
 			Object entity = session.getPersistenceContext().getEntity( key );
 			if ( entity != null ) {
 				EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 				loadedState = entry.getLoadedState();
 			}
 		}
 
 		final String[] deleteStrings;
 		if ( isImpliedOptimisticLocking && loadedState != null ) {
 			// we need to utilize dynamic delete statements
 			deleteStrings = generateSQLDeletStrings( loadedState );
 		}
 		else {
 			// otherwise, utilize the static delete statements
 			deleteStrings = getSQLDeleteStrings();
 		}
 
 		for ( int j = span - 1; j >= 0; j-- ) {
 			delete( id, version, j, object, deleteStrings[j], session, loadedState );
 		}
 
 	}
 
 	private boolean isAllOrDirtyOptLocking() {
 		return entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.DIRTY
 				|| entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL;
 	}
 
 	private String[] generateSQLDeletStrings(Object[] loadedState) {
 		int span = getTableSpan();
 		String[] deleteStrings = new String[span];
 		for ( int j = span - 1; j >= 0; j-- ) {
 			Delete delete = new Delete()
 					.setTableName( getTableName( j ) )
 					.addPrimaryKeyColumns( getKeyColumns( j ) );
 			if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 				delete.setComment( "delete " + getEntityName() + " [" + j + "]" );
 			}
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 					// this property belongs to the table and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( loadedState[i], getFactory() );
 					for ( int k = 0; k < propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							delete.addWhereFragment( propertyColumnNames[k] + " = ?" );
 						}
 						else {
 							delete.addWhereFragment( propertyColumnNames[k] + " is null" );
 						}
 					}
 				}
 			}
 			deleteStrings[j] = delete.toStatementString();
 		}
 		return deleteStrings;
 	}
 
 	protected void logStaticSQL() {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Static SQL for entity: %s", getEntityName() );
 			if ( sqlLazySelectString != null ) {
 				LOG.debugf( " Lazy select: %s", sqlLazySelectString );
 			}
 			if ( sqlVersionSelectString != null ) {
 				LOG.debugf( " Version select: %s", sqlVersionSelectString );
 			}
 			if ( sqlSnapshotSelectString != null ) {
 				LOG.debugf( " Snapshot select: %s", sqlSnapshotSelectString );
 			}
 			for ( int j = 0; j < getTableSpan(); j++ ) {
 				LOG.debugf( " Insert %s: %s", j, getSQLInsertStrings()[j] );
 				LOG.debugf( " Update %s: %s", j, getSQLUpdateStrings()[j] );
 				LOG.debugf( " Delete %s: %s", j, getSQLDeleteStrings()[j] );
 			}
 			if ( sqlIdentityInsertString != null ) {
 				LOG.debugf( " Identity insert: %s", sqlIdentityInsertString );
 			}
 			if ( sqlUpdateByRowIdString != null ) {
 				LOG.debugf( " Update by row id (all fields): %s", sqlUpdateByRowIdString );
 			}
 			if ( sqlLazyUpdateByRowIdString != null ) {
 				LOG.debugf( " Update by row id (non-lazy fields): %s", sqlLazyUpdateByRowIdString );
 			}
 			if ( sqlInsertGeneratedValuesSelectString != null ) {
 				LOG.debugf( " Insert-generated property select: %s", sqlInsertGeneratedValuesSelectString );
 			}
 			if ( sqlUpdateGeneratedValuesSelectString != null ) {
 				LOG.debugf( " Update-generated property select: %s", sqlUpdateGeneratedValuesSelectString );
 			}
 		}
 	}
 
 	@Override
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		final StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator( alias ), enabledFilters );
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	@Override
 	public String filterFragment(String alias, Map enabledFilters, Set<String> treatAsDeclarations) {
 		final StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator( alias ), enabledFilters );
 		return sessionFilterFragment.append( filterFragment( alias, treatAsDeclarations ) ).toString();
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return rootAlias;
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	@Override
 	public String oneToManyFilterFragment(String alias, Set<String> treatAsDeclarations) {
 		return oneToManyFilterFragment( alias );
 	}
 
 	@Override
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		// NOTE : Not calling createJoin here is just a performance optimization
 		return getSubclassTableSpan() == 1
 				? ""
 				: createJoin(
 				alias,
 				innerJoin,
 				includeSubclasses,
 				Collections.<String>emptySet()
 		).toFromFragmentString();
 	}
 
 	@Override
 	public String fromJoinFragment(
 			String alias,
 			boolean innerJoin,
 			boolean includeSubclasses,
 			Set<String> treatAsDeclarations) {
 		// NOTE : Not calling createJoin here is just a performance optimization
 		return getSubclassTableSpan() == 1
 				? ""
 				: createJoin( alias, innerJoin, includeSubclasses, treatAsDeclarations ).toFromFragmentString();
 	}
 
 	@Override
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		// NOTE : Not calling createJoin here is just a performance optimization
 		return getSubclassTableSpan() == 1
 				? ""
 				: createJoin(
 				alias,
 				innerJoin,
 				includeSubclasses,
 				Collections.<String>emptySet()
 		).toWhereFragmentString();
 	}
 
 	@Override
 	public String whereJoinFragment(
 			String alias,
 			boolean innerJoin,
 			boolean includeSubclasses,
 			Set<String> treatAsDeclarations) {
 		// NOTE : Not calling createJoin here is just a performance optimization
 		return getSubclassTableSpan() == 1
 				? ""
 				: createJoin( alias, innerJoin, includeSubclasses, treatAsDeclarations ).toWhereFragmentString();
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return false;
 	}
 
 	protected JoinFragment createJoin(
 			String name,
 			boolean innerJoin,
 			boolean includeSubclasses,
 			Set<String> treatAsDeclarations) {
 		// IMPL NOTE : all joins join to the pk of the driving table
 		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() );
 		final JoinFragment join = getFactory().getDialect().createOuterJoinFragment();
 		final int tableSpan = getSubclassTableSpan();
 		// IMPL NOTE : notice that we skip the first table; it is the driving table!
 		for ( int j = 1; j < tableSpan; j++ ) {
 			final JoinType joinType = determineSubclassTableJoinType(
 					j,
 					innerJoin,
 					includeSubclasses,
 					treatAsDeclarations
 			);
 
 			if ( joinType != null && joinType != JoinType.NONE ) {
 				join.addJoin(
 						getSubclassTableName( j ),
 						generateTableAlias( name, j ),
 						idCols,
 						getSubclassTableKeyColumns( j ),
 						joinType
 				);
 			}
 		}
 		return join;
 	}
 
 	protected JoinType determineSubclassTableJoinType(
 			int subclassTableNumber,
 			boolean canInnerJoin,
 			boolean includeSubclasses,
 			Set<String> treatAsDeclarations) {
 
 		if ( isClassOrSuperclassTable( subclassTableNumber ) ) {
 			final boolean shouldInnerJoin = canInnerJoin
 					&& !isInverseTable( subclassTableNumber )
 					&& !isNullableTable( subclassTableNumber );
 			// the table is either this persister's driving table or (one of) its super class persister's driving
 			// tables which can be inner joined as long as the `shouldInnerJoin` condition resolves to true
 			return shouldInnerJoin ? JoinType.INNER_JOIN : JoinType.LEFT_OUTER_JOIN;
 		}
 
 		// otherwise we have a subclass table and need to look a little deeper...
 
 		// IMPL NOTE : By default includeSubclasses indicates that all subclasses should be joined and that each
 		// subclass ought to be joined by outer-join.  However, TREAT-AS always requires that an inner-join be used
 		// so we give TREAT-AS higher precedence...
 
 		if ( isSubclassTableIndicatedByTreatAsDeclarations( subclassTableNumber, treatAsDeclarations ) ) {
 			return JoinType.INNER_JOIN;
 		}
 
 		if ( includeSubclasses
 				&& !isSubclassTableSequentialSelect( subclassTableNumber )
 				&& !isSubclassTableLazy( subclassTableNumber ) ) {
 			return JoinType.LEFT_OUTER_JOIN;
 		}
 
 		return JoinType.NONE;
 	}
 
 	protected boolean isSubclassTableIndicatedByTreatAsDeclarations(
 			int subclassTableNumber,
 			Set<String> treatAsDeclarations) {
 		return false;
 	}
 
 
 	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
 		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
 		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
 		// IMPL NOTE : notice that we skip the first table; it is the driving table!
 		for ( int i = 1; i < tableNumbers.length; i++ ) {
 			final int j = tableNumbers[i];
 			jf.addJoin(
 					getSubclassTableName( j ),
 					generateTableAlias( getRootAlias(), j ),
 					keyCols,
 					getSubclassTableKeyColumns( j ),
 					isInverseSubclassTable( j ) || isNullableSubclassTable( j )
 							? JoinType.LEFT_OUTER_JOIN
 							: JoinType.INNER_JOIN
 			);
 		}
 		return jf;
 	}
 
 	protected SelectFragment createSelect(
 			final int[] subclassColumnNumbers,
 			final int[] subclassFormulaNumbers) {
 
 		SelectFragment selectFragment = new SelectFragment();
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < subclassColumnNumbers.length; i++ ) {
 			int columnNumber = subclassColumnNumbers[i];
 			if ( subclassColumnSelectableClosure[columnNumber] ) {
 				final String subalias = generateTableAlias( getRootAlias(), columnTableNumbers[columnNumber] );
 				selectFragment.addColumnTemplate(
 						subalias,
 						columnReaderTemplates[columnNumber],
 						columnAliases[columnNumber]
 				);
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < subclassFormulaNumbers.length; i++ ) {
 			int formulaNumber = subclassFormulaNumbers[i];
 			final String subalias = generateTableAlias( getRootAlias(), formulaTableNumbers[formulaNumber] );
 			selectFragment.addFormula( subalias, formulaTemplates[formulaNumber], formulaAliases[formulaNumber] );
 		}
 
 		return selectFragment;
 	}
 
 	protected String createFrom(int tableNumber, String alias) {
 		return getSubclassTableName( tableNumber ) + ' ' + alias;
 	}
 
 	protected String createWhereByKey(int tableNumber, String alias) {
 		//TODO: move to .sql package, and refactor with similar things!
 		return StringHelper.join(
 				"=? and ",
 				StringHelper.qualify( alias, getSubclassTableKeyColumns( tableNumber ) )
 		) + "=?";
 	}
 
 	protected String renderSelect(
 			final int[] tableNumbers,
 			final int[] columnNumbers,
 			final int[] formulaNumbers) {
 
 		Arrays.sort( tableNumbers ); //get 'em in the right order (not that it really matters)
 
 		//render the where and from parts
 		int drivingTable = tableNumbers[0];
 		final String drivingAlias = generateTableAlias(
 				getRootAlias(),
 				drivingTable
 		); //we *could* regerate this inside each called method!
 		final String where = createWhereByKey( drivingTable, drivingAlias );
 		final String from = createFrom( drivingTable, drivingAlias );
 
 		//now render the joins
 		JoinFragment jf = createJoin( tableNumbers, drivingAlias );
 
 		//now render the select clause
 		SelectFragment selectFragment = createSelect( columnNumbers, formulaNumbers );
 
 		//now tie it all together
 		Select select = new Select( getFactory().getDialect() );
 		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 		select.setFromClause( from );
 		select.setWhereClause( where );
 		select.setOuterJoins( jf.toFromFragmentString(), jf.toWhereFragmentString() );
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "sequential select " + getEntityName() );
 		}
 		return select.toStatementString();
 	}
 
 	private String getRootAlias() {
 		return StringHelper.generateAlias( getEntityName() );
 	}
 
 	/**
 	 * Post-construct is a callback for AbstractEntityPersister subclasses to call after they are all done with their
 	 * constructor processing.  It allows AbstractEntityPersister to extend its construction after all subclass-specific
 	 * details have been handled.
 	 *
 	 * @param mapping The mapping
 	 *
 	 * @throws MappingException Indicates a problem accessing the Mapping
 	 */
 	protected void postConstruct(Mapping mapping) throws MappingException {
 		initPropertyPaths( mapping );
 
 		//doLateInit();
 		prepareEntityIdentifierDefinition();
 	}
 
 	private void doLateInit() {
 		//insert/update/delete SQL
 		final int joinSpan = getTableSpan();
 		sqlDeleteStrings = new String[joinSpan];
 		sqlInsertStrings = new String[joinSpan];
 		sqlUpdateStrings = new String[joinSpan];
 		sqlLazyUpdateStrings = new String[joinSpan];
 
 		sqlUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getPropertyUpdateability(), 0, true );
 		sqlLazyUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getNonLazyPropertyUpdateability(), 0, true );
 
 		for ( int j = 0; j < joinSpan; j++ ) {
 			sqlInsertStrings[j] = customSQLInsert[j] == null ?
 					generateInsertString( getPropertyInsertability(), j ) :
 					customSQLInsert[j];
 			sqlUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlLazyUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getNonLazyPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlDeleteStrings[j] = customSQLDelete[j] == null ?
 					generateDeleteString( j ) :
 					customSQLDelete[j];
 		}
 
 		tableHasColumns = new boolean[joinSpan];
 		for ( int j = 0; j < joinSpan; j++ ) {
 			tableHasColumns[j] = sqlUpdateStrings[j] != null;
 		}
 
 		//select SQL
 		sqlSnapshotSelectString = generateSnapshotSelectString();
 		sqlLazySelectString = generateLazySelectString();
 		sqlVersionSelectString = generateSelectVersionString();
 		if ( hasInsertGeneratedProperties() ) {
 			sqlInsertGeneratedValuesSelectString = generateInsertGeneratedValuesSelectString();
 		}
 		if ( hasUpdateGeneratedProperties() ) {
 			sqlUpdateGeneratedValuesSelectString = generateUpdateGeneratedValuesSelectString();
 		}
 		if ( isIdentifierAssignedByInsert() ) {
 			identityDelegate = ( (PostInsertIdentifierGenerator) getIdentifierGenerator() )
 					.getInsertGeneratedIdentifierDelegate( this, getFactory().getDialect(), useGetGeneratedKeys() );
 			sqlIdentityInsertString = customSQLInsert[0] == null
 					? generateIdentityInsertString( getPropertyInsertability() )
 					: customSQLInsert[0];
 		}
 		else {
 			sqlIdentityInsertString = null;
 		}
 
 		logStaticSQL();
 	}
 
 	public final void postInstantiate() throws MappingException {
 		doLateInit();
 
 		createLoaders();
 		createUniqueKeyLoaders();
 		createQueryLoader();
 
 		doPostInstantiate();
 	}
 
 	protected void doPostInstantiate() {
 	}
 
 	//needed by subclasses to override the createLoader strategy
 	protected Map getLoaders() {
 		return loaders;
 	}
 
 	//Relational based Persisters should be content with this implementation
 	protected void createLoaders() {
 		final Map loaders = getLoaders();
 		loaders.put( LockMode.NONE, createEntityLoader( LockMode.NONE ) );
 
 		UniqueEntityLoader readLoader = createEntityLoader( LockMode.READ );
 		loaders.put( LockMode.READ, readLoader );
 
 		//TODO: inexact, what we really need to know is: are any outer joins used?
 		boolean disableForUpdate = getSubclassTableSpan() > 1 &&
 				hasSubclasses() &&
 				!getFactory().getDialect().supportsOuterJoinForUpdate();
 
 		loaders.put(
 				LockMode.UPGRADE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE )
 		);
 		loaders.put(
 				LockMode.UPGRADE_NOWAIT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_NOWAIT )
 		);
 		loaders.put(
 				LockMode.UPGRADE_SKIPLOCKED,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_SKIPLOCKED )
 		);
 		loaders.put(
 				LockMode.FORCE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.FORCE )
 		);
 		loaders.put(
 				LockMode.PESSIMISTIC_READ,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_READ )
 		);
 		loaders.put(
 				LockMode.PESSIMISTIC_WRITE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_WRITE )
 		);
 		loaders.put(
 				LockMode.PESSIMISTIC_FORCE_INCREMENT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_FORCE_INCREMENT )
 		);
 		loaders.put( LockMode.OPTIMISTIC, createEntityLoader( LockMode.OPTIMISTIC ) );
 		loaders.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, createEntityLoader( LockMode.OPTIMISTIC_FORCE_INCREMENT ) );
 
 		loaders.put(
 				"merge",
 				new CascadeEntityLoader( this, CascadingActions.MERGE, getFactory() )
 		);
 		loaders.put(
 				"refresh",
 				new CascadeEntityLoader( this, CascadingActions.REFRESH, getFactory() )
 		);
 	}
 
 	protected void createQueryLoader() {
 		if ( loaderName != null ) {
 			queryLoader = new NamedQueryLoader( loaderName, this );
 		}
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 		return load( id, optionalObject, new LockOptions().setLockMode( lockMode ), session );
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 			throws HibernateException {
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Fetching entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
 		final UniqueEntityLoader loader = getAppropriateLoader( lockOptions, session );
 		return loader.load( id, optionalObject, session, lockOptions );
 	}
 
 	public void registerAffectingFetchProfile(String fetchProfileName) {
 		affectingFetchProfileNames.add( fetchProfileName );
 	}
 
 	private boolean isAffectedByEntityGraph(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().getFetchGraph() != null || session.getLoadQueryInfluencers()
 				.getLoadGraph() != null;
 	}
 
 	private boolean isAffectedByEnabledFetchProfiles(SessionImplementor session) {
 		for ( String s : session.getLoadQueryInfluencers().getEnabledFetchProfileNames() ) {
 			if ( affectingFetchProfileNames.contains( s ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() );
 	}
 
 	private UniqueEntityLoader getAppropriateLoader(LockOptions lockOptions, SessionImplementor session) {
 		if ( queryLoader != null ) {
 			// if the user specified a custom query loader we need to that
 			// regardless of any other consideration
 			return queryLoader;
 		}
 		else if ( isAffectedByEnabledFilters( session ) ) {
 			// because filters affect the rows returned (because they add
 			// restrictions) these need to be next in precedence
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( session.getLoadQueryInfluencers().getInternalFetchProfile() != null && LockMode.UPGRADE.greaterThan(
 				lockOptions.getLockMode()
 		) ) {
 			// Next, we consider whether an 'internal' fetch profile has been set.
 			// This indicates a special fetch profile Hibernate needs applied
 			// (for its merge loading process e.g.).
 			return (UniqueEntityLoader) getLoaders().get( session.getLoadQueryInfluencers().getInternalFetchProfile() );
 		}
 		else if ( isAffectedByEnabledFetchProfiles( session ) ) {
 			// If the session has associated influencers we need to adjust the
 			// SQL query used for loading based on those influencers
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( isAffectedByEntityGraph( session ) ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else {
 			return (UniqueEntityLoader) getLoaders().get( lockOptions.getLockMode() );
 		}
 	}
 
 	private boolean isAllNull(Object[] array, int tableNumber) {
 		for ( int i = 0; i < array.length; i++ ) {
 			if ( isPropertyOfTable( i, tableNumber ) && array[i] != null ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	public boolean isSubclassPropertyNullable(int i) {
 		return subclassPropertyNullabilityClosure[i];
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is dirty
 	 */
 	protected final boolean[] getPropertiesToUpdate(final int[] dirtyProperties, final boolean hasDirtyCollection) {
 		final boolean[] propsToUpdate = new boolean[entityMetamodel.getPropertySpan()];
 		final boolean[] updateability = getPropertyUpdateability(); //no need to check laziness, dirty checking handles that
 		for ( int j = 0; j < dirtyProperties.length; j++ ) {
 			int property = dirtyProperties[j];
 			if ( updateability[property] ) {
 				propsToUpdate[property] = true;
 			}
 		}
 		if ( isVersioned() && updateability[getVersionProperty()] ) {
 			propsToUpdate[getVersionProperty()] =
 					Versioning.isVersionIncrementRequired(
 							dirtyProperties,
 							hasDirtyCollection,
 							getPropertyVersionability()
 					);
 		}
 		return propsToUpdate;
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is insertable and non-null
 	 */
 	protected boolean[] getPropertiesToInsert(Object[] fields) {
 		boolean[] notNull = new boolean[fields.length];
 		boolean[] insertable = getPropertyInsertability();
 		for ( int i = 0; i < fields.length; i++ ) {
 			notNull[i] = insertable[i] && fields[i] != null;
 		}
 		return notNull;
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param currentState The current state of the entity (the state to be checked).
 	 * @param previousState The previous state of the entity (the state to be checked against).
 	 * @param entity The entity for which we are checking state dirtiness.
 	 * @param session The session in which the check is occurring.
 	 *
 	 * @return <tt>null</tt> or the indices of the dirty properties
 	 *
 	 * @throws HibernateException
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SessionImplementor session)
 			throws HibernateException {
 		int[] props = TypeHelper.findDirty(
 				entityMetamodel.getProperties(),
 				currentState,
 				previousState,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 		);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param old The old state of the entity.
 	 * @param current The current state of the entity.
 	 * @param entity The entity for which we are checking state modification.
 	 * @param session The session in which the check is occurring.
 	 *
 	 * @return <tt>null</tt> or the indices of the modified properties
 	 *
 	 * @throws HibernateException
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object entity, SessionImplementor session)
 			throws HibernateException {
 		int[] props = TypeHelper.findModified(
 				entityMetamodel.getProperties(),
 				current,
 				old,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 		);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Which properties appear in the SQL update?
 	 * (Initialized, updateable ones!)
 	 */
 	protected boolean[] getPropertyUpdateability(Object entity) {
 		return hasUninitializedLazyProperties( entity )
 				? getNonLazyPropertyUpdateability()
 				: getPropertyUpdateability();
 	}
 
 	private void logDirtyProperties(int[] props) {
 		if ( LOG.isTraceEnabled() ) {
 			for ( int i = 0; i < props.length; i++ ) {
 				String propertyName = entityMetamodel.getProperties()[props[i]].getName();
 				LOG.trace( StringHelper.qualify( getEntityName(), propertyName ) + " is dirty" );
 			}
 		}
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	public EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryHelper.getCacheEntryStructure();
 	}
 
 	@Override
 	public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 		return cacheEntryHelper.buildCacheEntry( entity, state, version, session );
 	}
 
 	public boolean hasNaturalIdCache() {
 		return naturalIdRegionAccessStrategy != null;
 	}
 
 	public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 		return naturalIdRegionAccessStrategy;
 	}
 
 	public Comparator getVersionComparator() {
 		return isVersioned() ? getVersionType().getComparator() : null;
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public final String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	public EntityType getEntityType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isPolymorphic() {
 		return entityMetamodel.isPolymorphic();
 	}
 
 	public boolean isInherited() {
 		return entityMetamodel.isInherited();
 	}
 
 	public boolean hasCascades() {
 		return entityMetamodel.hasCascades();
 	}
 
 	public boolean hasIdentifierProperty() {
 		return !entityMetamodel.getIdentifierProperty().isVirtual();
 	}
 
 	public VersionType getVersionType() {
 		return (VersionType) locateVersionType();
 	}
 
 	private Type locateVersionType() {
 		return entityMetamodel.getVersionProperty() == null ?
 				null :
 				entityMetamodel.getVersionProperty().getType();
 	}
 
 	public int getVersionProperty() {
 		return entityMetamodel.getVersionPropertyIndex();
 	}
 
 	public boolean isVersioned() {
 		return entityMetamodel.isVersioned();
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return entityMetamodel.getIdentifierProperty().isIdentifierAssignedByInsert();
 	}
 
 	public boolean hasLazyProperties() {
 		return entityMetamodel.hasLazyProperties();
 	}
 
 //	public boolean hasUninitializedLazyProperties(Object entity) {
 //		if ( hasLazyProperties() ) {
 //			InterceptFieldCallback callback = ( ( InterceptFieldEnabled ) entity ).getInterceptFieldCallback();
 //			return callback != null && !( ( FieldInterceptor ) callback ).isInitialized();
 //		}
 //		else {
 //			return false;
 //		}
 //	}
 
 	public void afterReassociate(Object entity, SessionImplementor session) {
 		if ( getEntityMetamodel().getInstrumentationMetadata().isInstrumented() ) {
 			FieldInterceptor interceptor = getEntityMetamodel().getInstrumentationMetadata()
 					.extractInterceptor( entity );
 			if ( interceptor != null ) {
 				interceptor.setSession( session );
 			}
 			else {
 				FieldInterceptor fieldInterceptor = getEntityMetamodel().getInstrumentationMetadata().injectInterceptor(
 						entity,
 						getEntityName(),
 						null,
 						session
 				);
 				fieldInterceptor.dirty();
 			}
 		}
 
 		handleNaturalIdReattachment( entity, session );
 	}
 
 	private void handleNaturalIdReattachment(Object entity, SessionImplementor session) {
 		if ( !hasNaturalIdentifier() ) {
 			return;
 		}
 
 		if ( getEntityMetamodel().hasImmutableNaturalId() ) {
 			// we assume there were no changes to natural id during detachment for now, that is validated later
 			// during flush.
 			return;
 		}
 
 		final NaturalIdHelper naturalIdHelper = session.getPersistenceContext().getNaturalIdHelper();
 		final Serializable id = getIdentifier( entity, session );
 
 		// for reattachment of mutable natural-ids, we absolutely positively have to grab the snapshot from the
 		// database, because we have no other way to know if the state changed while detached.
 		final Object[] naturalIdSnapshot;
 		final Object[] entitySnapshot = session.getPersistenceContext().getDatabaseSnapshot( id, this );
 		if ( entitySnapshot == StatefulPersistenceContext.NO_ROW ) {
 			naturalIdSnapshot = null;
 		}
 		else {
 			naturalIdSnapshot = naturalIdHelper.extractNaturalIdValues( entitySnapshot, this );
 		}
 
 		naturalIdHelper.removeSharedNaturalIdCrossReference( this, id, naturalIdSnapshot );
 		naturalIdHelper.manageLocalNaturalIdCrossReference(
 				this,
 				id,
 				naturalIdHelper.extractNaturalIdValues( entity, this ),
 				naturalIdSnapshot,
 				CachedNaturalIdValueSource.UPDATE
 		);
 	}
 
 	public Boolean isTransient(Object entity, SessionImplementor session) throws HibernateException {
 		final Serializable id;
 		if ( canExtractIdOutOfEntity() ) {
 			id = getIdentifier( entity, session );
 		}
 		else {
 			id = null;
 		}
 		// we *always* assume an instance with a null
 		// identifier or no identifier property is unsaved!
 		if ( id == null ) {
 			return Boolean.TRUE;
 		}
 
 		// check the version unsaved-value, if appropriate
 		final Object version = getVersion( entity );
 		if ( isVersioned() ) {
 			// let this take precedence if defined, since it works for
 			// assigned identifiers
 			Boolean result = entityMetamodel.getVersionProperty()
 					.getUnsavedValue().isUnsaved( version );
 			if ( result != null ) {
 				return result;
 			}
 		}
 
 		// check the id unsaved-value
 		Boolean result = entityMetamodel.getIdentifierProperty()
 				.getUnsavedValue().isUnsaved( id );
 		if ( result != null ) {
 			return result;
 		}
 
 		// check to see if it is in the second-level cache
 		if ( session.getCacheMode().isGetEnabled() && hasCache() ) {
 			final EntityRegionAccessStrategy cache = getCacheAccessStrategy();
-			final EntityCacheKey ck = cache.generateCacheKey( id, this, session.getFactory(), session.getTenantIdentifier() );
+			final Object ck = cache.generateCacheKey( id, this, session.getFactory(), session.getTenantIdentifier() );
 			final Object ce = CacheHelper.fromSharedCache( session, ck, getCacheAccessStrategy() );
 			if ( ce != null ) {
 				return Boolean.FALSE;
 			}
 		}
 
 		return null;
 	}
 
 	public boolean hasCollections() {
 		return entityMetamodel.hasCollections();
 	}
 
 	public boolean hasMutableProperties() {
 		return entityMetamodel.hasMutableProperties();
 	}
 
 	public boolean isMutable() {
 		return entityMetamodel.isMutable();
 	}
 
 	private boolean isModifiableEntity(EntityEntry entry) {
 		return ( entry == null ? isMutable() : entry.isModifiableEntity() );
 	}
 
 	public boolean isAbstract() {
 		return entityMetamodel.isAbstract();
 	}
 
 	public boolean hasSubclasses() {
 		return entityMetamodel.hasSubclasses();
 	}
 
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() throws HibernateException {
 		return entityMetamodel.getIdentifierProperty().getIdentifierGenerator();
 	}
 
 	public String getRootEntityName() {
 		return entityMetamodel.getRootName();
 	}
 
 	public ClassMetadata getClassMetadata() {
 		return this;
 	}
 
 	public String getMappedSuperclass() {
 		return entityMetamodel.getSuperclass();
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return entityMetamodel.isExplicitPolymorphism();
 	}
 
 	protected boolean useDynamicUpdate() {
 		return entityMetamodel.isDynamicUpdate();
 	}
 
 	protected boolean useDynamicInsert() {
 		return entityMetamodel.isDynamicInsert();
 	}
 
 	protected boolean hasEmbeddedCompositeIdentifier() {
 		return entityMetamodel.getIdentifierProperty().isEmbedded();
 	}
 
 	public boolean canExtractIdOutOfEntity() {
 		return hasIdentifierProperty() || hasEmbeddedCompositeIdentifier() || hasIdentifierMapper();
 	}
 
 	private boolean hasIdentifierMapper() {
 		return entityMetamodel.getIdentifierProperty().hasIdentifierMapper();
 	}
 
 	public String[] getKeyColumnNames() {
 		return getIdentifierColumnNames();
 	}
 
 	public String getName() {
 		return getEntityName();
 	}
 
 	public boolean isCollection() {
 		return false;
 	}
 
 	public boolean consumesEntityAlias() {
 		return true;
 	}
 
 	public boolean consumesCollectionAlias() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) throws MappingException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public Type getType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return entityMetamodel.isSelectBeforeUpdate();
 	}
 
 	protected final OptimisticLockStyle optimisticLockStyle() {
 		return entityMetamodel.getOptimisticLockStyle();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 		return entityMetamodel.getTuplizer().createProxy( id, session );
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) +
 				'(' + entityMetamodel.getName() + ')';
 	}
 
 	public final String selectFragment(
 			Joinable rhs,
 			String rhsAlias,
 			String lhsAlias,
 			String entitySuffix,
 			String collectionSuffix,
 			boolean includeCollectionColumns) {
 		return selectFragment( lhsAlias, entitySuffix );
 	}
 
 	public boolean isInstrumented() {
 		return entityMetamodel.isInstrumented();
 	}
 
 	public boolean hasInsertGeneratedProperties() {
 		return entityMetamodel.hasInsertGeneratedValues();
 	}
 
 	public boolean hasUpdateGeneratedProperties() {
 		return entityMetamodel.hasUpdateGeneratedValues();
 	}
 
 	public boolean isVersionPropertyGenerated() {
 		return isVersioned() && getEntityMetamodel().isVersionGenerated();
 	}
 
 	public boolean isVersionPropertyInsertable() {
 		return isVersioned() && getPropertyInsertability()[getVersionProperty()];
 	}
 
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		getEntityTuplizer().afterInitialize( entity, lazyPropertiesAreUnfetched, session );
 	}
 
 	public String[] getPropertyNames() {
 		return entityMetamodel.getPropertyNames();
 	}
 
 	public Type[] getPropertyTypes() {
 		return entityMetamodel.getPropertyTypes();
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return entityMetamodel.getPropertyLaziness();
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return entityMetamodel.getPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return entityMetamodel.getPropertyCheckability();
 	}
 
 	public boolean[] getNonLazyPropertyUpdateability() {
 		return entityMetamodel.getNonlazyPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return entityMetamodel.getPropertyInsertability();
 	}
 
 	/**
 	 * @deprecated no simple, direct replacement
 	 */
 	@Deprecated
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return null;
 	}
 
 	/**
 	 * @deprecated no simple, direct replacement
 	 */
 	@Deprecated
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return null;
 	}
 
 	public boolean[] getPropertyNullability() {
 		return entityMetamodel.getPropertyNullability();
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return entityMetamodel.getPropertyVersionability();
 	}
 
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return entityMetamodel.getCascadeStyles();
 	}
 
 	public final Class getMappedClass() {
 		return getEntityTuplizer().getMappedClass();
 	}
 
 	public boolean implementsLifecycle() {
 		return getEntityTuplizer().isLifecycleImplementor();
 	}
 
 	public Class getConcreteProxyClass() {
 		return getEntityTuplizer().getConcreteProxyClass();
 	}
 
 	public void setPropertyValues(Object object, Object[] values) {
 		getEntityTuplizer().setPropertyValues( object, values );
 	}
 
 	public void setPropertyValue(Object object, int i, Object value) {
 		getEntityTuplizer().setPropertyValue( object, i, value );
 	}
 
 	public Object[] getPropertyValues(Object object) {
 		return getEntityTuplizer().getPropertyValues( object );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) {
 		return getEntityTuplizer().getPropertyValue( object, i );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) {
 		return getEntityTuplizer().getPropertyValue( object, propertyName );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) {
 		return getEntityTuplizer().getIdentifier( object, null );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return getEntityTuplizer().getIdentifier( entity, session );
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		getEntityTuplizer().setIdentifier( entity, id, session );
 	}
 
 	@Override
 	public Object getVersion(Object object) {
 		return getEntityTuplizer().getVersion( object );
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		return getEntityTuplizer().instantiate( id, session );
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return getEntityTuplizer().isInstance( object );
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return getEntityTuplizer().hasUninitializedLazyProperties( object );
 	}
 
 	@Override
 	public void resetIdentifier(
 			Object entity,
 			Serializable currentId,
 			Object currentVersion,
 			SessionImplementor session) {
 		getEntityTuplizer().resetIdentifier( entity, currentId, currentVersion, session );
 	}
 
 	@Override
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		if ( !hasSubclasses() ) {
 			return this;
 		}
 		else {
 			final String concreteEntityName = getEntityTuplizer().determineConcreteSubclassEntityName(
 					instance,
 					factory
 			);
 			if ( concreteEntityName == null || getEntityName().equals( concreteEntityName ) ) {
 				// the contract of EntityTuplizer.determineConcreteSubclassEntityName says that returning null
 				// is an indication that the specified entity-name (this.getEntityName) should be used.
 				return this;
 			}
 			else {
 				return factory.getEntityPersister( concreteEntityName );
 			}
 		}
 	}
 
 	public boolean isMultiTable() {
 		return false;
 	}
 
 	protected int getPropertySpan() {
 		return entityMetamodel.getPropertySpan();
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
 			throws HibernateException {
 		return getEntityTuplizer().getPropertyValuesToInsert( object, mergeMap, session );
 	}
 
 	public void processInsertGeneratedProperties(
 			Serializable id,
 			Object entity,
 			Object[] state,
 			SessionImplementor session) {
 		if ( !hasInsertGeneratedProperties() ) {
 			throw new AssertionFailure( "no insert-generated properties" );
 		}
 		processGeneratedProperties(
 				id,
 				entity,
 				state,
 				session,
 				sqlInsertGeneratedValuesSelectString,
 				GenerationTiming.INSERT
 		);
 	}
 
 	public void processUpdateGeneratedProperties(
 			Serializable id,
 			Object entity,
 			Object[] state,
 			SessionImplementor session) {
 		if ( !hasUpdateGeneratedProperties() ) {
 			throw new AssertionFailure( "no update-generated properties" );
 		}
 		processGeneratedProperties(
 				id,
 				entity,
 				state,
 				session,
 				sqlUpdateGeneratedValuesSelectString,
 				GenerationTiming.ALWAYS
 		);
 	}
 
 	private void processGeneratedProperties(
 			Serializable id,
 			Object entity,
 			Object[] state,
 			SessionImplementor session,
 			String selectionSQL,
 			GenerationTiming matchTiming) {
 		// force immediate execution of the insert batch (if one)
 		session.getJdbcCoordinator().executeBatch();
 
 		try {
 			PreparedStatement ps = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( selectionSQL );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					if ( !rs.next() ) {
 						throw new HibernateException(
 								"Unable to locate row for retrieval of generated properties: " +
 										MessageHelper.infoString( this, id, getFactory() )
 						);
 					}
 					int propertyIndex = -1;
 					for ( NonIdentifierAttribute attribute : entityMetamodel.getProperties() ) {
 						propertyIndex++;
 						final ValueGeneration valueGeneration = attribute.getValueGenerationStrategy();
 						if ( isReadRequired( valueGeneration, matchTiming ) ) {
 							final Object hydratedState = attribute.getType().hydrate(
 									rs, getPropertyAliases(
 											"",
 											propertyIndex
 									), session, entity
 							);
 							state[propertyIndex] = attribute.getType().resolve( hydratedState, session, entity );
 							setPropertyValue( entity, propertyIndex, state[propertyIndex] );
 						}
 					}
 //					for ( int i = 0; i < getPropertySpan(); i++ ) {
 //						if ( includeds[i] != ValueInclusion.NONE ) {
 //							Object hydratedState = getPropertyTypes()[i].hydrate( rs, getPropertyAliases( "", i ), session, entity );
 //							state[i] = getPropertyTypes()[i].resolve( hydratedState, session, entity );
 //							setPropertyValue( entity, i, state[i] );
 //						}
 //					}
 				}
 				finally {
 					if ( rs != null ) {
 						session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 					}
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( ps );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"unable to select generated column values",
 					selectionSQL
 			);
 		}
 
 	}
 
 	/**
 	 * Whether the given value generation strategy requires to read the value from the database or not.
 	 */
 	private boolean isReadRequired(ValueGeneration valueGeneration, GenerationTiming matchTiming) {
 		return valueGeneration != null &&
 				valueGeneration.getValueGenerator() == null &&
 				timingsMatch( valueGeneration.getGenerationTiming(), matchTiming );
 	}
 
 	private boolean timingsMatch(GenerationTiming timing, GenerationTiming matchTiming) {
 		return
 				( matchTiming == GenerationTiming.INSERT && timing.includesInsert() ) ||
 						( matchTiming == GenerationTiming.ALWAYS && timing.includesUpdate() );
 	}
 
 	public String getIdentifierPropertyName() {
 		return entityMetamodel.getIdentifierProperty().getName();
 	}
 
 	public Type getIdentifierType() {
 		return entityMetamodel.getIdentifierProperty().getType();
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return entityMetamodel.getNaturalIdentifierProperties();
 	}
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session)
 			throws HibernateException {
 		if ( !hasNaturalIdentifier() ) {
 			throw new MappingException(
 					"persistent class did not define a natural-id : " + MessageHelper.infoString(
 							this
 					)
 			);
 		}
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev(
 					"Getting current natural-id snapshot state for: {0}",
 					MessageHelper.infoString( this, id, getFactory() )
 			);
 		}
 
 		int[] naturalIdPropertyIndexes = getNaturalIdentifierProperties();
 		int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
 		boolean[] naturalIdMarkers = new boolean[getPropertySpan()];
 		Type[] extractionTypes = new Type[naturalIdPropertyCount];
 		for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 			extractionTypes[i] = getPropertyTypes()[naturalIdPropertyIndexes[i]];
 			naturalIdMarkers[naturalIdPropertyIndexes[i]] = true;
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// TODO : look at perhaps caching this...
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id state " + getEntityName() );
 		}
 		select.setSelectClause( concretePropertySelectFragmentSansLeadingComma( getRootAlias(), naturalIdMarkers ) );
 		select.setFromClause( fromTableFragment( getRootAlias() ) + fromJoinFragment( getRootAlias(), true, false ) );
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String whereClause = new StringBuilder()
 				.append(
 						StringHelper.join(
 								"=? and ",
 								aliasedIdColumns
 						)
 				)
 				.append( "=?" )
 				.append( whereJoinFragment( getRootAlias(), true, false ) )
 				.toString();
 
 		String sql = select.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 		///////////////////////////////////////////////////////////////////////
 
 		Object[] snapshot = new Object[naturalIdPropertyCount];
 		try {
 			PreparedStatement ps = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					final EntityKey key = session.generateEntityKey( id, this );
 					Object owner = session.getPersistenceContext().getEntity( key );
 					for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 						snapshot[i] = extractionTypes[i].hydrate(
 								rs, getPropertyAliases(
 										"",
 										naturalIdPropertyIndexes[i]
 								), session, null
 						);
 						if ( extractionTypes[i].isEntityType() ) {
 							snapshot[i] = extractionTypes[i].resolve( snapshot[i], session, owner );
 						}
 					}
 					return snapshot;
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( ps );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 					sql
 			);
 		}
 	}
 
 	@Override
 	public Serializable loadEntityIdByNaturalId(
 			Object[] naturalIdValues,
 			LockOptions lockOptions,
 			SessionImplementor session) {
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"Resolving natural-id [%s] to id : %s ",
 					naturalIdValues,
 					MessageHelper.infoString( this )
 			);
 		}
 
 		final boolean[] valueNullness = determineValueNullness( naturalIdValues );
 		final String sqlEntityIdByNaturalIdString = determinePkByNaturalIdQuery( valueNullness );
 
 		try {
 			PreparedStatement ps = session
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlEntityIdByNaturalIdString );
 			try {
 				int positions = 1;
 				int loop = 0;
 				for ( int idPosition : getNaturalIdentifierProperties() ) {
 					final Object naturalIdValue = naturalIdValues[loop++];
 					if ( naturalIdValue != null ) {
 						final Type type = getPropertyTypes()[idPosition];
 						type.nullSafeSet( ps, naturalIdValue, positions, session );
 						positions += type.getColumnSpan( session.getFactory() );
 					}
 				}
 				ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( ps );
 				try {
 					// if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 
 					final Object hydratedId = getIdentifierType().hydrate( rs, getIdentifierAliases(), session, null );
 					return (Serializable) getIdentifierType().resolve( hydratedId, session, null );
 				}
 				finally {
 					session.getJdbcCoordinator().getResourceRegistry().release( rs, ps );
 				}
 			}
 			finally {
 				session.getJdbcCoordinator().getResourceRegistry().release( ps );
 				session.getJdbcCoordinator().afterStatementExecution();
 			}
 		}
 		catch (SQLException e) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					String.format(
 							"could not resolve natural-id [%s] to id : %s",
 							naturalIdValues,
 							MessageHelper.infoString( this )
 					),
 					sqlEntityIdByNaturalIdString
 			);
 		}
 	}
 
 	private boolean[] determineValueNullness(Object[] naturalIdValues) {
 		boolean[] nullness = new boolean[naturalIdValues.length];
 		for ( int i = 0; i < naturalIdValues.length; i++ ) {
 			nullness[i] = naturalIdValues[i] == null;
 		}
 		return nullness;
 	}
 
 	private Boolean naturalIdIsNonNullable;
 	private String cachedPkByNonNullableNaturalIdQuery;
 
 	private String determinePkByNaturalIdQuery(boolean[] valueNullness) {
 		if ( !hasNaturalIdentifier() ) {
 			throw new HibernateException(
 					"Attempt to build natural-id -> PK resolution query for entity that does not define natural id"
 			);
 		}
 
 		// performance shortcut for cases where the natural-id is defined as completely non-nullable
 		if ( isNaturalIdNonNullable() ) {
 			if ( valueNullness != null && !ArrayHelper.isAllFalse( valueNullness ) ) {
 				throw new HibernateException( "Null value(s) passed to lookup by non-nullable natural-id" );
 			}
 			if ( cachedPkByNonNullableNaturalIdQuery == null ) {
 				cachedPkByNonNullableNaturalIdQuery = generateEntityIdByNaturalIdSql( null );
 			}
 			return cachedPkByNonNullableNaturalIdQuery;
 		}
 
 		// Otherwise, regenerate it each time
 		return generateEntityIdByNaturalIdSql( valueNullness );
 	}
 
 	protected boolean isNaturalIdNonNullable() {
 		if ( naturalIdIsNonNullable == null ) {
 			naturalIdIsNonNullable = determineNaturalIdNullability();
 		}
 		return naturalIdIsNonNullable;
 	}
 
 	private boolean determineNaturalIdNullability() {
 		boolean[] nullability = getPropertyNullability();
 		for ( int position : getNaturalIdentifierProperties() ) {
 			// if any individual property is nullable, return false
 			if ( nullability[position] ) {
 				return false;
 			}
 		}
 		// return true if we found no individually nullable properties
 		return true;
 	}
 
 	private String generateEntityIdByNaturalIdSql(boolean[] valueNullness) {
 		EntityPersister rootPersister = getFactory().getEntityPersister( getRootEntityName() );
 		if ( rootPersister != this ) {
 			if ( rootPersister instanceof AbstractEntityPersister ) {
 				return ( (AbstractEntityPersister) rootPersister ).generateEntityIdByNaturalIdSql( valueNullness );
 			}
 		}
 
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSessionFactoryOptions().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id->entity-id state " + getEntityName() );
 		}
 
 		final String rootAlias = getRootAlias();
 
 		select.setSelectClause( identifierSelectFragment( rootAlias, "" ) );
 		select.setFromClause( fromTableFragment( rootAlias ) + fromJoinFragment( rootAlias, true, false ) );
 
 		final StringBuilder whereClause = new StringBuilder();
 		final int[] propertyTableNumbers = getPropertyTableNumbers();
 		final int[] naturalIdPropertyIndexes = this.getNaturalIdentifierProperties();
 		int valuesIndex = -1;
 		for ( int propIdx = 0; propIdx < naturalIdPropertyIndexes.length; propIdx++ ) {
 			valuesIndex++;
 			if ( propIdx > 0 ) {
 				whereClause.append( " and " );
 			}
 
 			final int naturalIdIdx = naturalIdPropertyIndexes[propIdx];
 			final String tableAlias = generateTableAlias( rootAlias, propertyTableNumbers[naturalIdIdx] );
 			final String[] propertyColumnNames = getPropertyColumnNames( naturalIdIdx );
 			final String[] aliasedPropertyColumns = StringHelper.qualify( tableAlias, propertyColumnNames );
 
 			if ( valueNullness != null && valueNullness[valuesIndex] ) {
 				whereClause.append( StringHelper.join( " is null and ", aliasedPropertyColumns ) ).append( " is null" );
 			}
 			else {
 				whereClause.append( StringHelper.join( "=? and ", aliasedPropertyColumns ) ).append( "=?" );
 			}
 		}
 
 		whereClause.append( whereJoinFragment( getRootAlias(), true, false ) );
 
 		return select.setOuterJoins( "", "" ).setWhereClause( whereClause.toString() ).toStatementString();
 	}
 
 	protected String concretePropertySelectFragmentSansLeadingComma(String alias, boolean[] include) {
 		String concretePropertySelectFragment = concretePropertySelectFragment( alias, include );
 		int firstComma = concretePropertySelectFragment.indexOf( ", " );
 		if ( firstComma == 0 ) {
 			concretePropertySelectFragment = concretePropertySelectFragment.substring( 2 );
 		}
 		return concretePropertySelectFragment;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return entityMetamodel.hasNaturalIdentifier();
 	}
 
 	public void setPropertyValue(Object object, String propertyName, Object value) {
 		getEntityTuplizer().setPropertyValue( object, propertyName, value );
 	}
 
 	public static int getTableId(String tableName, String[] tables) {
 		for ( int j = 0; j < tables.length; j++ ) {
 			if ( tableName.equalsIgnoreCase( tables[j] ) ) {
 				return j;
 			}
 		}
 		throw new AssertionFailure( "Table " + tableName + " not found" );
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return entityMetamodel.getEntityMode();
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return entityTuplizer;
 	}
 
 	@Override
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return entityMetamodel.getInstrumentationMetadata();
 	}
 
 	@Override
 	public String getTableAliasForColumn(String columnName, String rootAlias) {
 		return generateTableAlias( rootAlias, determineTableNumberForColumn( columnName ) );
 	}
 
 	public int determineTableNumberForColumn(String columnName) {
 		return 0;
 	}
 
 	@Override
 	public EntityEntryFactory getEntityEntryFactory() {
 		return this.entityEntryFactory;
 	}
 
 	/**
 	 * Consolidated these onto a single helper because the 2 pieces work in tandem.
 	 */
 	public interface CacheEntryHelper {
 		CacheEntryStructure getCacheEntryStructure();
 
 		CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session);
 	}
 
 	private static class StandardCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private StandardCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class ReferenceCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 
 		private ReferenceCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new ReferenceCacheEntryImpl( entity, persister );
 		}
 	}
 
 	private static class StructuredCacheEntryHelper implements CacheEntryHelper {
 		private final EntityPersister persister;
 		private final StructuredCacheEntry structure;
 
 		private StructuredCacheEntryHelper(EntityPersister persister) {
 			this.persister = persister;
 			this.structure = new StructuredCacheEntry( persister );
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return structure;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			return new StandardCacheEntryImpl(
 					state,
 					persister,
 					persister.hasUninitializedLazyProperties( entity ),
 					version,
 					session,
 					entity
 			);
 		}
 	}
 
 	private static class NoopCacheEntryHelper implements CacheEntryHelper {
 		public static final NoopCacheEntryHelper INSTANCE = new NoopCacheEntryHelper();
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return UnstructuredCacheEntry.INSTANCE;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(Object entity, Object[] state, Object version, SessionImplementor session) {
 			throw new HibernateException( "Illegal attempt to build cache entry for non-cached entity" );
 		}
 	}
 
 
 	// EntityDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private EntityIdentifierDefinition entityIdentifierDefinition;
 	private Iterable<AttributeDefinition> embeddedCompositeIdentifierAttributes;
 	private Iterable<AttributeDefinition> attributeDefinitions;
 
 	@Override
 	public void generateEntityDefinition() {
 		prepareEntityIdentifierDefinition();
 		collectAttributeDefinitions();
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return this;
 	}
 
 	@Override
 	public EntityIdentifierDefinition getEntityKeyDefinition() {
 		return entityIdentifierDefinition;
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		return attributeDefinitions;
 	}
 
 
 	private void prepareEntityIdentifierDefinition() {
 		if ( entityIdentifierDefinition != null ) {
 			return;
 		}
 		final Type idType = getIdentifierType();
 
 		if ( !idType.isComponentType() ) {
 			entityIdentifierDefinition =
 					EntityIdentifierDefinitionHelper.buildSimpleEncapsulatedIdentifierDefinition( this );
 			return;
 		}
 
 		final CompositeType cidType = (CompositeType) idType;
 		if ( !cidType.isEmbedded() ) {
 			entityIdentifierDefinition =
 					EntityIdentifierDefinitionHelper.buildEncapsulatedCompositeIdentifierDefinition( this );
 			return;
 		}
 
 		entityIdentifierDefinition =
 				EntityIdentifierDefinitionHelper.buildNonEncapsulatedCompositeIdentifierDefinition( this );
 	}
 
 	private void collectAttributeDefinitions(
 			Map<String, AttributeDefinition> attributeDefinitionsByName,
 			EntityMetamodel metamodel) {
 		for ( int i = 0; i < metamodel.getPropertySpan(); i++ ) {
 			final AttributeDefinition attributeDefinition = metamodel.getProperties()[i];
 			// Don't replace an attribute definition if it is already in attributeDefinitionsByName
 			// because the new value will be from a subclass.
 			final AttributeDefinition oldAttributeDefinition = attributeDefinitionsByName.get(
 					attributeDefinition.getName()
 			);
 			if ( oldAttributeDefinition != null ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.tracef(
 							"Ignoring subclass attribute definition [%s.%s] because it is defined in a superclass ",
 							entityMetamodel.getName(),
 							attributeDefinition.getName()
 					);
 				}
 			}
 			else {
 				attributeDefinitionsByName.put( attributeDefinition.getName(), attributeDefinition );
 			}
 		}
 
 		// see if there are any subclass persisters...
 		final Set<String> subClassEntityNames = metamodel.getSubclassEntityNames();
 		if ( subClassEntityNames == null ) {
 			return;
 		}
 
 		// see if we can find the persisters...
 		for ( String subClassEntityName : subClassEntityNames ) {
 			if ( metamodel.getName().equals( subClassEntityName ) ) {
 				// skip it
 				continue;
 			}
 			try {
 				final EntityPersister subClassEntityPersister = factory.getEntityPersister( subClassEntityName );
 				collectAttributeDefinitions( attributeDefinitionsByName, subClassEntityPersister.getEntityMetamodel() );
 			}
 			catch (MappingException e) {
 				throw new IllegalStateException(
 						String.format(
 								"Could not locate subclass EntityPersister [%s] while processing EntityPersister [%s]",
 								subClassEntityName,
 								metamodel.getName()
 						),
 						e
 				);
 			}
 		}
 	}
 
 	private void collectAttributeDefinitions() {
 		// todo : I think this works purely based on luck atm
 		// 		specifically in terms of the sub/super class entity persister(s) being available.  Bit of chicken-egg
 		// 		problem there:
 		//			* If I do this during postConstruct (as it is now), it works as long as the
 		//			super entity persister is already registered, but I don't think that is necessarily true.
 		//			* If I do this during postInstantiate then lots of stuff in postConstruct breaks if we want
 		//			to try and drive SQL generation on these (which we do ultimately).  A possible solution there
 		//			would be to delay all SQL generation until postInstantiate
 
 		Map<String, AttributeDefinition> attributeDefinitionsByName = new LinkedHashMap<String, AttributeDefinition>();
 		collectAttributeDefinitions( attributeDefinitionsByName, getEntityMetamodel() );
 
 
 //		EntityMetamodel currentEntityMetamodel = this.getEntityMetamodel();
 //		while ( currentEntityMetamodel != null ) {
 //			for ( int i = 0; i < currentEntityMetamodel.getPropertySpan(); i++ ) {
 //				attributeDefinitions.add( currentEntityMetamodel.getProperties()[i] );
 //			}
 //			// see if there is a super class EntityMetamodel
 //			final String superEntityName = currentEntityMetamodel.getSuperclass();
 //			if ( superEntityName != null ) {
 //				currentEntityMetamodel = factory.getEntityPersister( superEntityName ).getEntityMetamodel();
 //			}
 //			else {
 //				currentEntityMetamodel = null;
 //			}
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentNaturalIdCacheStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentNaturalIdCacheStatisticsImpl.java
index db14beb0c1..7eedbe4676 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentNaturalIdCacheStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentNaturalIdCacheStatisticsImpl.java
@@ -1,187 +1,190 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.stat.internal;
 
 import java.util.HashMap;
 import java.util.Map;
 import java.util.concurrent.atomic.AtomicLong;
 import java.util.concurrent.locks.Lock;
 import java.util.concurrent.locks.ReadWriteLock;
 import java.util.concurrent.locks.ReentrantReadWriteLock;
 
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.Region;
+import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.stat.NaturalIdCacheStatistics;
 
 /**
  * NaturalId cache statistics of a specific region
  * 
  * @author Eric Dalquist
  */
 public class ConcurrentNaturalIdCacheStatisticsImpl extends CategorizedStatistics implements NaturalIdCacheStatistics {
 	private static final long serialVersionUID = 1L;
 	private final transient Region region;
+	private final transient NaturalIdRegionAccessStrategy accessStrategy;
 	private final AtomicLong hitCount = new AtomicLong();
 	private final AtomicLong missCount = new AtomicLong();
 	private final AtomicLong putCount = new AtomicLong();
 	private final AtomicLong executionCount = new AtomicLong();
 	private final AtomicLong executionMaxTime = new AtomicLong();
 	private final AtomicLong executionMinTime = new AtomicLong( Long.MAX_VALUE );
 	private final AtomicLong totalExecutionTime = new AtomicLong();
 
 	private final Lock readLock;
 	private final Lock writeLock;
+
 	{
 		final ReadWriteLock lock = new ReentrantReadWriteLock();
 		this.readLock = lock.readLock();
 		this.writeLock = lock.writeLock();
 	}
 
-	ConcurrentNaturalIdCacheStatisticsImpl(Region region) {
+	ConcurrentNaturalIdCacheStatisticsImpl(Region region, NaturalIdRegionAccessStrategy accessStrategy) {
 		super( region.getName() );
 		this.region = region;
+		this.accessStrategy = accessStrategy;
 	}
 
 	@Override
 	public long getHitCount() {
 		return this.hitCount.get();
 	}
 
 	@Override
 	public long getMissCount() {
 		return this.missCount.get();
 	}
 
 	@Override
 	public long getPutCount() {
 		return this.putCount.get();
 	}
 
 	/**
 	 * queries executed to the DB
 	 */
 	@Override
 	public long getExecutionCount() {
 		return this.executionCount.get();
 	}
 
 	/**
 	 * average time in ms taken by the excution of this query onto the DB
 	 */
 	@Override
 	public long getExecutionAvgTime() {
 		// We write lock here to be sure that we always calculate the average time
 		// with all updates from the executed applied: executionCount and totalExecutionTime
 		// both used in the calculation
 		this.writeLock.lock();
 		try {
 			long avgExecutionTime = 0;
 			if ( this.executionCount.get() > 0 ) {
 				avgExecutionTime = this.totalExecutionTime.get() / this.executionCount.get();
 			}
 			return avgExecutionTime;
 		}
 		finally {
 			this.writeLock.unlock();
 		}
 	}
 
 	/**
 	 * max time in ms taken by the excution of this query onto the DB
 	 */
 	@Override
 	public long getExecutionMaxTime() {
 		return this.executionMaxTime.get();
 	}
 
 	/**
 	 * min time in ms taken by the excution of this query onto the DB
 	 */
 	@Override
 	public long getExecutionMinTime() {
 		return this.executionMinTime.get();
 	}
 
 	@Override
 	public long getElementCountInMemory() {
 		return this.region.getElementCountInMemory();
 	}
 
 	@Override
 	public long getElementCountOnDisk() {
 		return this.region.getElementCountOnDisk();
 	}
 
 	@Override
 	public long getSizeInMemory() {
 		return this.region.getSizeInMemory();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Map getEntries() {
 		final Map map = new HashMap();
 		for ( Object o : this.region.toMap().entrySet() ) {
-			final Map.Entry me = (Map.Entry) o;
-			map.put( ( (NaturalIdCacheKey) me.getKey() ).getNaturalIdValues(), me.getValue() );
+			Map.Entry me = (Map.Entry) o;
+			map.put( accessStrategy.getNaturalIdValues(me.getKey()), me.getValue() );
 		}
 		return map;
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder buf = new StringBuilder()
 			.append( "NaturalIdCacheStatistics" )
 			.append( "[hitCount=" ).append( this.hitCount )
 			.append( ",missCount=" ).append( this.missCount )
 			.append( ",putCount=" ).append( this.putCount )
 			.append( ",executionCount=" ).append( this.executionCount )
 			.append( ",executionAvgTime=" ).append( this.getExecutionAvgTime() )
 			.append( ",executionMinTime=" ).append( this.executionMinTime )
 			.append( ",executionMaxTime=" ).append( this.executionMaxTime );
 		// not sure if this would ever be null but wanted to be careful
 		if ( this.region != null ) {
 			buf.append( ",elementCountInMemory=" ).append( this.getElementCountInMemory() )
 				.append( ",elementCountOnDisk=" ).append( this.getElementCountOnDisk() )
 				.append( ",sizeInMemory=" ).append( this.getSizeInMemory() );
 		}
 		buf.append( ']' );
 		return buf.toString();
 	}
 
 	void incrementHitCount() {
 		this.hitCount.getAndIncrement();
 	}
 
 	void incrementMissCount() {
 		this.missCount.getAndIncrement();
 	}
 
 	void incrementPutCount() {
 		this.putCount.getAndIncrement();
 	}
 
 	void queryExecuted(long time) {
 		// read lock is enough, concurrent updates are supported by the underlying type AtomicLong
 		// this only guards executed(long, long) to be called, when another thread is executing getExecutionAvgTime()
 		this.readLock.lock();
 		try {
 			// Less chances for a context switch
 			//noinspection StatementWithEmptyBody
 			for ( long old = this.executionMinTime.get(); time < old && !this.executionMinTime.compareAndSet( old, time ); old = this.executionMinTime.get() ) {
 			}
 			//noinspection StatementWithEmptyBody
 			for ( long old = this.executionMaxTime.get(); time > old && !this.executionMaxTime.compareAndSet( old, time ); old = this.executionMaxTime.get() ) {
 			}
 			this.executionCount.getAndIncrement();
 			this.totalExecutionTime.addAndGet( time );
 		}
 		finally {
 			this.readLock.unlock();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentSecondLevelCacheStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentSecondLevelCacheStatisticsImpl.java
index 8c4518352b..a6bce5e32b 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentSecondLevelCacheStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentSecondLevelCacheStatisticsImpl.java
@@ -1,95 +1,108 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.stat.internal;
 
 import java.util.HashMap;
-import java.util.Iterator;
 import java.util.Map;
 import java.util.concurrent.atomic.AtomicLong;
 
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.Region;
+import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
+import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 
 /**
  * Second level cache statistics of a specific region
  *
  * @author Alex Snaps
  */
 public class ConcurrentSecondLevelCacheStatisticsImpl extends CategorizedStatistics implements SecondLevelCacheStatistics {
 	private final transient Region region;
+	private final transient EntityRegionAccessStrategy entityRegionAccessStrategy;
+	private final transient CollectionRegionAccessStrategy collectionRegionAccessStrategy;
 	private AtomicLong hitCount = new AtomicLong();
 	private AtomicLong missCount = new AtomicLong();
 	private AtomicLong putCount = new AtomicLong();
 
-	ConcurrentSecondLevelCacheStatisticsImpl(Region region) {
+	ConcurrentSecondLevelCacheStatisticsImpl(Region region,
+											 EntityRegionAccessStrategy entityRegionAccessStrategy,
+											 CollectionRegionAccessStrategy collectionRegionAccessStrategy) {
 		super( region.getName() );
 		this.region = region;
+		this.entityRegionAccessStrategy = entityRegionAccessStrategy;
+		this.collectionRegionAccessStrategy = collectionRegionAccessStrategy;
 	}
 
 	public long getHitCount() {
 		return hitCount.get();
 	}
 
 	public long getMissCount() {
 		return missCount.get();
 	}
 
 	public long getPutCount() {
 		return putCount.get();
 	}
 
 	public long getElementCountInMemory() {
 		return region.getElementCountInMemory();
 	}
 
 	public long getElementCountOnDisk() {
 		return region.getElementCountOnDisk();
 	}
 
 	public long getSizeInMemory() {
 		return region.getSizeInMemory();
 	}
 
 	public Map getEntries() {
 		Map map = new HashMap();
-		Iterator iter = region.toMap().entrySet().iterator();
-		while (iter.hasNext()) {
-			Map.Entry me = (Map.Entry) iter.next();
-			map.put(((EntityCacheKey) me.getKey()).getKey(), me.getValue());
+		for (Object o : region.toMap().entrySet()) {
+			Map.Entry me = (Map.Entry) o;
+			Object id;
+			if (entityRegionAccessStrategy != null) {
+				id = entityRegionAccessStrategy.getCacheKeyId(me.getKey());
+			} else if (collectionRegionAccessStrategy != null) {
+				id = collectionRegionAccessStrategy.getCacheKeyId(me.getKey());
+			} else {
+				id = me.getKey();
+			}
+			map.put(id, me.getValue());
 		}
 		return map;
 	}
 
 	public String toString() {
 		StringBuilder buf = new StringBuilder()
 				.append("SecondLevelCacheStatistics")
 				.append("[hitCount=").append(this.hitCount)
 				.append(",missCount=").append(this.missCount)
 				.append(",putCount=").append(this.putCount);
 		//not sure if this would ever be null but wanted to be careful
 		if (region != null) {
 			buf.append(",elementCountInMemory=").append(this.getElementCountInMemory())
 					.append(",elementCountOnDisk=").append(this.getElementCountOnDisk())
 					.append(",sizeInMemory=").append(this.getSizeInMemory());
 		}
 		buf.append(']');
 		return buf.toString();
 	}
 
 	void incrementHitCount() {
 		hitCount.getAndIncrement();
 	}
 
 	void incrementMissCount() {
 		missCount.getAndIncrement();
 	}
 
 	void incrementPutCount() {
 		putCount.getAndIncrement();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
index b9b6e87dee..16b5478fb6 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/ConcurrentStatisticsImpl.java
@@ -1,892 +1,908 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.stat.internal;
 
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 import java.util.concurrent.atomic.AtomicLong;
 
 import org.hibernate.cache.spi.Region;
+import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
+import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
+import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
+import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.service.Service;
 import org.hibernate.stat.CollectionStatistics;
 import org.hibernate.stat.EntityStatistics;
 import org.hibernate.stat.NaturalIdCacheStatistics;
 import org.hibernate.stat.QueryStatistics;
 import org.hibernate.stat.SecondLevelCacheStatistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 
 import static org.hibernate.internal.CoreLogging.messageLogger;
 
 /**
  * Implementation of {@link org.hibernate.stat.Statistics} based on the {@link java.util.concurrent} package.
  *
  * @author Alex Snaps
  */
 @SuppressWarnings({ "unchecked" })
 public class ConcurrentStatisticsImpl implements StatisticsImplementor, Service {
 	private static final CoreMessageLogger LOG = messageLogger( ConcurrentStatisticsImpl.class );
 
 	private SessionFactoryImplementor sessionFactory;
 
 	private volatile boolean isStatisticsEnabled;
 	private volatile long startTime;
 	private AtomicLong sessionOpenCount = new AtomicLong();
 	private AtomicLong sessionCloseCount = new AtomicLong();
 	private AtomicLong flushCount = new AtomicLong();
 	private AtomicLong connectCount = new AtomicLong();
 
 	private AtomicLong prepareStatementCount = new AtomicLong();
 	private AtomicLong closeStatementCount = new AtomicLong();
 
 	private AtomicLong entityLoadCount = new AtomicLong();
 	private AtomicLong entityUpdateCount = new AtomicLong();
 	private AtomicLong entityInsertCount = new AtomicLong();
 	private AtomicLong entityDeleteCount = new AtomicLong();
 	private AtomicLong entityFetchCount = new AtomicLong();
 	private AtomicLong collectionLoadCount = new AtomicLong();
 	private AtomicLong collectionUpdateCount = new AtomicLong();
 	private AtomicLong collectionRemoveCount = new AtomicLong();
 	private AtomicLong collectionRecreateCount = new AtomicLong();
 	private AtomicLong collectionFetchCount = new AtomicLong();
 
 	private AtomicLong secondLevelCacheHitCount = new AtomicLong();
 	private AtomicLong secondLevelCacheMissCount = new AtomicLong();
 	private AtomicLong secondLevelCachePutCount = new AtomicLong();
 	
 	private AtomicLong naturalIdCacheHitCount = new AtomicLong();
 	private AtomicLong naturalIdCacheMissCount = new AtomicLong();
 	private AtomicLong naturalIdCachePutCount = new AtomicLong();
 	private AtomicLong naturalIdQueryExecutionCount = new AtomicLong();
 	private AtomicLong naturalIdQueryExecutionMaxTime = new AtomicLong();
 	private volatile String naturalIdQueryExecutionMaxTimeRegion;
 	
 	private AtomicLong queryExecutionCount = new AtomicLong();
 	private AtomicLong queryExecutionMaxTime = new AtomicLong();
 	private volatile String queryExecutionMaxTimeQueryString;
 	private AtomicLong queryCacheHitCount = new AtomicLong();
 	private AtomicLong queryCacheMissCount = new AtomicLong();
 	private AtomicLong queryCachePutCount = new AtomicLong();
 
 	private AtomicLong updateTimestampsCacheHitCount = new AtomicLong();
 	private AtomicLong updateTimestampsCacheMissCount = new AtomicLong();
 	private AtomicLong updateTimestampsCachePutCount = new AtomicLong();
 
 	private AtomicLong committedTransactionCount = new AtomicLong();
 	private AtomicLong transactionCount = new AtomicLong();
 
 	private AtomicLong optimisticFailureCount = new AtomicLong();
 
 	/**
 	 * natural id cache statistics per region
 	 */
 	private final ConcurrentMap naturalIdCacheStatistics = new ConcurrentHashMap();
 	/**
 	 * second level cache statistics per region
 	 */
 	private final ConcurrentMap secondLevelCacheStatistics = new ConcurrentHashMap();
 	/**
 	 * entity statistics per name
 	 */
 	private final ConcurrentMap entityStatistics = new ConcurrentHashMap();
 	/**
 	 * collection statistics per name
 	 */
 	private final ConcurrentMap collectionStatistics = new ConcurrentHashMap();
 	/**
 	 * entity statistics per query string (HQL or SQL)
 	 */
 	private final ConcurrentMap queryStatistics = new ConcurrentHashMap();
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public ConcurrentStatisticsImpl() {
 		clear();
 	}
 
 	public ConcurrentStatisticsImpl(SessionFactoryImplementor sessionFactory) {
 		clear();
 		this.sessionFactory = sessionFactory;
 	}
 
 	/**
 	 * reset all statistics
 	 */
 	public void clear() {
 		secondLevelCacheHitCount.set( 0 );
 		secondLevelCacheMissCount.set( 0 );
 		secondLevelCachePutCount.set( 0 );
 		
 		naturalIdCacheHitCount.set( 0 );
 		naturalIdCacheMissCount.set( 0 );
 		naturalIdCachePutCount.set( 0 );
 		naturalIdQueryExecutionCount.set( 0 );
 		naturalIdQueryExecutionMaxTime.set( 0 );
 		naturalIdQueryExecutionMaxTimeRegion = null;
 
 		sessionCloseCount.set( 0 );
 		sessionOpenCount.set( 0 );
 		flushCount.set( 0 );
 		connectCount.set( 0 );
 
 		prepareStatementCount.set( 0 );
 		closeStatementCount.set( 0 );
 
 		entityDeleteCount.set( 0 );
 		entityInsertCount.set( 0 );
 		entityUpdateCount.set( 0 );
 		entityLoadCount.set( 0 );
 		entityFetchCount.set( 0 );
 
 		collectionRemoveCount.set( 0 );
 		collectionUpdateCount.set( 0 );
 		collectionRecreateCount.set( 0 );
 		collectionLoadCount.set( 0 );
 		collectionFetchCount.set( 0 );
 
 		queryExecutionCount.set( 0 );
 		queryCacheHitCount.set( 0 );
 		queryExecutionMaxTime.set( 0 );
 		queryExecutionMaxTimeQueryString = null;
 		queryCacheMissCount.set( 0 );
 		queryCachePutCount.set( 0 );
 
 		updateTimestampsCacheMissCount.set( 0 );
 		updateTimestampsCacheHitCount.set( 0 );
 		updateTimestampsCachePutCount.set( 0 );
 
 		transactionCount.set( 0 );
 		committedTransactionCount.set( 0 );
 
 		optimisticFailureCount.set( 0 );
 
 		secondLevelCacheStatistics.clear();
 		entityStatistics.clear();
 		collectionStatistics.clear();
 		queryStatistics.clear();
 		naturalIdCacheStatistics.clear();
 
 		startTime = System.currentTimeMillis();
 	}
 
 	public void openSession() {
 		sessionOpenCount.getAndIncrement();
 	}
 
 	public void closeSession() {
 		sessionCloseCount.getAndIncrement();
 	}
 
 	public void flush() {
 		flushCount.getAndIncrement();
 	}
 
 	public void connect() {
 		connectCount.getAndIncrement();
 	}
 
 	public void loadEntity(String entityName) {
 		entityLoadCount.getAndIncrement();
 		( (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName ) ).incrementLoadCount();
 	}
 
 	public void fetchEntity(String entityName) {
 		entityFetchCount.getAndIncrement();
 		( (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName ) ).incrementFetchCount();
 	}
 
 	/**
 	 * find entity statistics per name
 	 *
 	 * @param entityName entity name
 	 *
 	 * @return EntityStatistics object
 	 */
 	public EntityStatistics getEntityStatistics(String entityName) {
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) entityStatistics.get( entityName );
 		if ( es == null ) {
 			es = new ConcurrentEntityStatisticsImpl( entityName );
 			ConcurrentEntityStatisticsImpl previous;
 			if ( ( previous = (ConcurrentEntityStatisticsImpl) entityStatistics.putIfAbsent(
 					entityName, es
 			) ) != null ) {
 				es = previous;
 			}
 		}
 		return es;
 	}
 
 	public void updateEntity(String entityName) {
 		entityUpdateCount.getAndIncrement();
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName );
 		es.incrementUpdateCount();
 	}
 
 	public void insertEntity(String entityName) {
 		entityInsertCount.getAndIncrement();
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName );
 		es.incrementInsertCount();
 	}
 
 	public void deleteEntity(String entityName) {
 		entityDeleteCount.getAndIncrement();
 		ConcurrentEntityStatisticsImpl es = (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName );
 		es.incrementDeleteCount();
 	}
 
 	/**
 	 * Get collection statistics per role
 	 *
 	 * @param role collection role
 	 *
 	 * @return CollectionStatistics
 	 */
 	public CollectionStatistics getCollectionStatistics(String role) {
 		ConcurrentCollectionStatisticsImpl cs = (ConcurrentCollectionStatisticsImpl) collectionStatistics.get( role );
 		if ( cs == null ) {
 			cs = new ConcurrentCollectionStatisticsImpl( role );
 			ConcurrentCollectionStatisticsImpl previous;
 			if ( ( previous = (ConcurrentCollectionStatisticsImpl) collectionStatistics.putIfAbsent(
 					role, cs
 			) ) != null ) {
 				cs = previous;
 			}
 		}
 		return cs;
 	}
 
 	public void loadCollection(String role) {
 		collectionLoadCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementLoadCount();
 	}
 
 	public void fetchCollection(String role) {
 		collectionFetchCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementFetchCount();
 	}
 
 	public void updateCollection(String role) {
 		collectionUpdateCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementUpdateCount();
 	}
 
 	public void recreateCollection(String role) {
 		collectionRecreateCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementRecreateCount();
 	}
 
 	public void removeCollection(String role) {
 		collectionRemoveCount.getAndIncrement();
 		( (ConcurrentCollectionStatisticsImpl) getCollectionStatistics( role ) ).incrementRemoveCount();
 	}
 	
 
 	@Override
 	public NaturalIdCacheStatistics getNaturalIdCacheStatistics(String regionName) {
 		ConcurrentNaturalIdCacheStatisticsImpl nics =
 				(ConcurrentNaturalIdCacheStatisticsImpl) naturalIdCacheStatistics.get( regionName );
 		
 		if ( nics == null ) {
 			if ( sessionFactory == null ) {
 				return null;
 			}
 			Region region = sessionFactory.getNaturalIdCacheRegion( regionName );
 			if ( region == null ) {
 				return null;
 			}
-			nics = new ConcurrentNaturalIdCacheStatisticsImpl( region );
+			NaturalIdRegionAccessStrategy accessStrategy
+					= (NaturalIdRegionAccessStrategy) sessionFactory.getNaturalIdCacheRegionAccessStrategy(regionName);
+
+			nics = new ConcurrentNaturalIdCacheStatisticsImpl( region, accessStrategy );
 			ConcurrentNaturalIdCacheStatisticsImpl previous;
 			if ( ( previous = (ConcurrentNaturalIdCacheStatisticsImpl) naturalIdCacheStatistics.putIfAbsent(
 					regionName, nics
 			) ) != null ) {
 				nics = previous;
 			}
 		}
 		return nics;
 	}
 
 	/**
 	 * Second level cache statistics per region
 	 *
 	 * @param regionName region name
 	 *
 	 * @return SecondLevelCacheStatistics
 	 */
 	public SecondLevelCacheStatistics getSecondLevelCacheStatistics(String regionName) {
 		ConcurrentSecondLevelCacheStatisticsImpl slcs
 				= (ConcurrentSecondLevelCacheStatisticsImpl) secondLevelCacheStatistics.get( regionName );
 		if ( slcs == null ) {
 			if ( sessionFactory == null ) {
 				return null;
 			}
 			Region region = sessionFactory.getSecondLevelCacheRegion( regionName );
 			if ( region == null ) {
 				return null;
 			}
-			slcs = new ConcurrentSecondLevelCacheStatisticsImpl( region );
+			RegionAccessStrategy accessStrategy = sessionFactory.getSecondLevelCacheRegionAccessStrategy(regionName);
+
+			EntityRegionAccessStrategy entityRegionAccessStrategy
+					= accessStrategy instanceof EntityRegionAccessStrategy ?
+					(EntityRegionAccessStrategy) accessStrategy : null;
+			CollectionRegionAccessStrategy collectionRegionAccessStrategy
+					= accessStrategy instanceof CollectionRegionAccessStrategy ?
+					(CollectionRegionAccessStrategy) accessStrategy : null;
+
+			slcs = new ConcurrentSecondLevelCacheStatisticsImpl( region, entityRegionAccessStrategy, collectionRegionAccessStrategy );
 			ConcurrentSecondLevelCacheStatisticsImpl previous;
 			if ( ( previous = (ConcurrentSecondLevelCacheStatisticsImpl) secondLevelCacheStatistics.putIfAbsent(
 					regionName, slcs
 			) ) != null ) {
 				slcs = previous;
 			}
 		}
 		return slcs;
 	}
 
 	public void secondLevelCachePut(String regionName) {
 		secondLevelCachePutCount.getAndIncrement();
 		( (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics( regionName ) ).incrementPutCount();
 	}
 
 	public void secondLevelCacheHit(String regionName) {
 		secondLevelCacheHitCount.getAndIncrement();
 		( (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics( regionName ) ).incrementHitCount();
 	}
 
 	public void secondLevelCacheMiss(String regionName) {
 		secondLevelCacheMissCount.getAndIncrement();
 		( (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics( regionName ) ).incrementMissCount();
 	}
 	
 	@Override
 	public void naturalIdCachePut(String regionName) {
 		naturalIdCachePutCount.getAndIncrement();
 		( (ConcurrentNaturalIdCacheStatisticsImpl) getNaturalIdCacheStatistics( regionName ) ).incrementPutCount();
 	}
 
 	@Override
 	public void naturalIdCacheHit(String regionName) {
 		naturalIdCacheHitCount.getAndIncrement();
 		( (ConcurrentNaturalIdCacheStatisticsImpl) getNaturalIdCacheStatistics( regionName ) ).incrementHitCount();
 	}
 
 	@Override
 	public void naturalIdCacheMiss(String regionName) {
 		naturalIdCacheMissCount.getAndIncrement();
 		( (ConcurrentNaturalIdCacheStatisticsImpl) getNaturalIdCacheStatistics( regionName ) ).incrementMissCount();
 	}
 	
 	@Override
 	public void naturalIdQueryExecuted(String regionName, long time) {
 		naturalIdQueryExecutionCount.getAndIncrement();
 		boolean isLongestQuery;
 		//noinspection StatementWithEmptyBody
 		for ( long old = naturalIdQueryExecutionMaxTime.get();
 				( isLongestQuery = time > old ) && ( !naturalIdQueryExecutionMaxTime.compareAndSet( old, time ) );
 				old = naturalIdQueryExecutionMaxTime.get() ) {
 			// nothing to do here given the odd loop structure...
 		}
 		if ( isLongestQuery && regionName != null ) {
 			naturalIdQueryExecutionMaxTimeRegion = regionName;
 		}
 		if ( regionName != null ) {
 			( (ConcurrentNaturalIdCacheStatisticsImpl) getNaturalIdCacheStatistics( regionName ) ).queryExecuted( time );
 		}
 	}
 
 	@Override
 	public void queryExecuted(String hql, int rows, long time) {
 		LOG.hql(hql, time, (long) rows );
 		queryExecutionCount.getAndIncrement();
 		boolean isLongestQuery;
 		//noinspection StatementWithEmptyBody
 		for ( long old = queryExecutionMaxTime.get();
 				( isLongestQuery = time > old ) && ( !queryExecutionMaxTime.compareAndSet( old, time ) );
 				old = queryExecutionMaxTime.get() ) {
 			// nothing to do here given the odd loop structure...
 		}
 		if ( isLongestQuery ) {
 			queryExecutionMaxTimeQueryString = hql;
 		}
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.executed( rows, time );
 		}
 	}
 	@Override
 	public void queryCacheHit(String hql, String regionName) {
 		queryCacheHitCount.getAndIncrement();
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.incrementCacheHitCount();
 		}
 		ConcurrentSecondLevelCacheStatisticsImpl slcs = (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(
 				regionName
 		);
 		slcs.incrementHitCount();
 	}
 	@Override
 	public void queryCacheMiss(String hql, String regionName) {
 		queryCacheMissCount.getAndIncrement();
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.incrementCacheMissCount();
 		}
 		ConcurrentSecondLevelCacheStatisticsImpl slcs = (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(
 				regionName
 		);
 		slcs.incrementMissCount();
 	}
 	@Override
 	public void queryCachePut(String hql, String regionName) {
 		queryCachePutCount.getAndIncrement();
 		if ( hql != null ) {
 			ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) getQueryStatistics( hql );
 			qs.incrementCachePutCount();
 		}
 		ConcurrentSecondLevelCacheStatisticsImpl slcs = (ConcurrentSecondLevelCacheStatisticsImpl) getSecondLevelCacheStatistics(
 				regionName
 		);
 		slcs.incrementPutCount();
 	}
 
 	@Override
 	public void updateTimestampsCacheHit() {
 		updateTimestampsCacheHitCount.getAndIncrement();
 	}
 
 	@Override
 	public void updateTimestampsCacheMiss() {
 		updateTimestampsCacheMissCount.getAndIncrement();
 	}
 
 	@Override
 	public void updateTimestampsCachePut() {
 		updateTimestampsCachePutCount.getAndIncrement();
 	}
 
 	/**
 	 * Query statistics from query string (HQL or SQL)
 	 *
 	 * @param queryString query string
 	 *
 	 * @return QueryStatistics
 	 */
 	@Override
 	public QueryStatistics getQueryStatistics(String queryString) {
 		ConcurrentQueryStatisticsImpl qs = (ConcurrentQueryStatisticsImpl) queryStatistics.get( queryString );
 		if ( qs == null ) {
 			qs = new ConcurrentQueryStatisticsImpl( queryString );
 			ConcurrentQueryStatisticsImpl previous;
 			if ( ( previous = (ConcurrentQueryStatisticsImpl) queryStatistics.putIfAbsent(
 					queryString, qs
 			) ) != null ) {
 				qs = previous;
 			}
 		}
 		return qs;
 	}
 
 	/**
 	 * @return entity deletion count
 	 */
 	@Override
 	public long getEntityDeleteCount() {
 		return entityDeleteCount.get();
 	}
 
 	/**
 	 * @return entity insertion count
 	 */
 	@Override
 	public long getEntityInsertCount() {
 		return entityInsertCount.get();
 	}
 
 	/**
 	 * @return entity load (from DB)
 	 */
 	@Override
 	public long getEntityLoadCount() {
 		return entityLoadCount.get();
 	}
 
 	/**
 	 * @return entity fetch (from DB)
 	 */
 	@Override
 	public long getEntityFetchCount() {
 		return entityFetchCount.get();
 	}
 
 	/**
 	 * @return entity update
 	 */
 	@Override
 	public long getEntityUpdateCount() {
 		return entityUpdateCount.get();
 	}
 	@Override
 	public long getQueryExecutionCount() {
 		return queryExecutionCount.get();
 	}
 	@Override
 	public long getQueryCacheHitCount() {
 		return queryCacheHitCount.get();
 	}
 	@Override
 	public long getQueryCacheMissCount() {
 		return queryCacheMissCount.get();
 	}
 	@Override
 	public long getQueryCachePutCount() {
 		return queryCachePutCount.get();
 	}
 	@Override
 	public long getUpdateTimestampsCacheHitCount() {
 		return updateTimestampsCacheHitCount.get();
 	}
 	@Override
 	public long getUpdateTimestampsCacheMissCount() {
 		return updateTimestampsCacheMissCount.get();
 	}
 	@Override
 	public long getUpdateTimestampsCachePutCount() {
 		return updateTimestampsCachePutCount.get();
 	}
 
 	/**
 	 * @return flush
 	 */
 	@Override
 	public long getFlushCount() {
 		return flushCount.get();
 	}
 
 	/**
 	 * @return session connect
 	 */
 	@Override
 	public long getConnectCount() {
 		return connectCount.get();
 	}
 
 	/**
 	 * @return second level cache hit
 	 */
 	@Override
 	public long getSecondLevelCacheHitCount() {
 		return secondLevelCacheHitCount.get();
 	}
 
 	/**
 	 * @return second level cache miss
 	 */
 	@Override
 	public long getSecondLevelCacheMissCount() {
 		return secondLevelCacheMissCount.get();
 	}
 
 	/**
 	 * @return second level cache put
 	 */
 	@Override
 	public long getSecondLevelCachePutCount() {
 		return secondLevelCachePutCount.get();
 	}
 
 	@Override
 	public long getNaturalIdQueryExecutionCount() {
 		return naturalIdQueryExecutionCount.get();
 	}
 
 	@Override
 	public long getNaturalIdQueryExecutionMaxTime() {
 		return naturalIdQueryExecutionMaxTime.get();
 	}
 	
 	@Override
 	public String getNaturalIdQueryExecutionMaxTimeRegion() {
 		return naturalIdQueryExecutionMaxTimeRegion;
 	}
 	
 	@Override
 	public long getNaturalIdCacheHitCount() {
 		return naturalIdCacheHitCount.get();
 	}
 
 	@Override
 	public long getNaturalIdCacheMissCount() {
 		return naturalIdCacheMissCount.get();
 	}
 
 	@Override
 	public long getNaturalIdCachePutCount() {
 		return naturalIdCachePutCount.get();
 	}
 
 	/**
 	 * @return session closing
 	 */
 	@Override
 	public long getSessionCloseCount() {
 		return sessionCloseCount.get();
 	}
 
 	/**
 	 * @return session opening
 	 */
 	@Override
 	public long getSessionOpenCount() {
 		return sessionOpenCount.get();
 	}
 
 	/**
 	 * @return collection loading (from DB)
 	 */
 	@Override
 	public long getCollectionLoadCount() {
 		return collectionLoadCount.get();
 	}
 
 	/**
 	 * @return collection fetching (from DB)
 	 */
 	@Override
 	public long getCollectionFetchCount() {
 		return collectionFetchCount.get();
 	}
 
 	/**
 	 * @return collection update
 	 */
 	@Override
 	public long getCollectionUpdateCount() {
 		return collectionUpdateCount.get();
 	}
 
 	/**
 	 * @return collection removal
 	 *         FIXME: even if isInverse="true"?
 	 */
 	@Override
 	public long getCollectionRemoveCount() {
 		return collectionRemoveCount.get();
 	}
 
 	/**
 	 * @return collection recreation
 	 */
 	@Override
 	public long getCollectionRecreateCount() {
 		return collectionRecreateCount.get();
 	}
 
 	/**
 	 * @return start time in ms (JVM standards {@link System#currentTimeMillis()})
 	 */
 	@Override
 	public long getStartTime() {
 		return startTime;
 	}
 
 	/**
 	 * log in info level the main statistics
 	 */
 	@Override
 	public void logSummary() {
 		LOG.loggingStatistics();
 		LOG.startTime( startTime );
 		LOG.sessionsOpened( sessionOpenCount.get() );
 		LOG.sessionsClosed( sessionCloseCount.get() );
 		LOG.transactions( transactionCount.get() );
 		LOG.successfulTransactions( committedTransactionCount.get() );
 		LOG.optimisticLockFailures( optimisticFailureCount.get() );
 		LOG.flushes( flushCount.get() );
 		LOG.connectionsObtained( connectCount.get() );
 		LOG.statementsPrepared( prepareStatementCount.get() );
 		LOG.statementsClosed( closeStatementCount.get() );
 		LOG.secondLevelCachePuts( secondLevelCachePutCount.get() );
 		LOG.secondLevelCacheHits( secondLevelCacheHitCount.get() );
 		LOG.secondLevelCacheMisses( secondLevelCacheMissCount.get() );
 		LOG.entitiesLoaded( entityLoadCount.get() );
 		LOG.entitiesUpdated( entityUpdateCount.get() );
 		LOG.entitiesInserted( entityInsertCount.get() );
 		LOG.entitiesDeleted( entityDeleteCount.get() );
 		LOG.entitiesFetched( entityFetchCount.get() );
 		LOG.collectionsLoaded( collectionLoadCount.get() );
 		LOG.collectionsUpdated( collectionUpdateCount.get() );
 		LOG.collectionsRemoved( collectionRemoveCount.get() );
 		LOG.collectionsRecreated( collectionRecreateCount.get() );
 		LOG.collectionsFetched( collectionFetchCount.get() );
 		LOG.naturalIdCachePuts( naturalIdCachePutCount.get() );
 		LOG.naturalIdCacheHits( naturalIdCacheHitCount.get() );
 		LOG.naturalIdCacheMisses( naturalIdCacheMissCount.get() );
 		LOG.naturalIdMaxQueryTime( naturalIdQueryExecutionMaxTime.get() );
 		LOG.naturalIdQueriesExecuted( naturalIdQueryExecutionCount.get() );
 		LOG.queriesExecuted( queryExecutionCount.get() );
 		LOG.queryCachePuts( queryCachePutCount.get() );
 		LOG.timestampCachePuts( updateTimestampsCachePutCount.get() );
 		LOG.timestampCacheHits( updateTimestampsCacheHitCount.get() );
 		LOG.timestampCacheMisses( updateTimestampsCacheMissCount.get() );
 		LOG.queryCacheHits( queryCacheHitCount.get() );
 		LOG.queryCacheMisses( queryCacheMissCount.get() );
 		LOG.maxQueryTime( queryExecutionMaxTime.get() );
 	}
 
 	/**
 	 * Are statistics logged
 	 */
 	@Override
 	public boolean isStatisticsEnabled() {
 		return isStatisticsEnabled;
 	}
 
 	/**
 	 * Enable statistics logs (this is a dynamic parameter)
 	 */
 	@Override
 	public void setStatisticsEnabled(boolean b) {
 		isStatisticsEnabled = b;
 	}
 
 	/**
 	 * @return Returns the max query execution time,
 	 *         for all queries
 	 */
 	@Override
 	public long getQueryExecutionMaxTime() {
 		return queryExecutionMaxTime.get();
 	}
 
 	/**
 	 * Get all executed query strings
 	 */
 	@Override
 	public String[] getQueries() {
 		return ArrayHelper.toStringArray( queryStatistics.keySet() );
 	}
 
 	/**
 	 * Get the names of all entities
 	 */
 	@Override
 	public String[] getEntityNames() {
 		if ( sessionFactory == null ) {
 			return ArrayHelper.toStringArray( entityStatistics.keySet() );
 		}
 		else {
 			return ArrayHelper.toStringArray( sessionFactory.getAllClassMetadata().keySet() );
 		}
 	}
 
 	/**
 	 * Get the names of all collection roles
 	 */
 	@Override
 	public String[] getCollectionRoleNames() {
 		if ( sessionFactory == null ) {
 			return ArrayHelper.toStringArray( collectionStatistics.keySet() );
 		}
 		else {
 			return ArrayHelper.toStringArray( sessionFactory.getAllCollectionMetadata().keySet() );
 		}
 	}
 
 	/**
 	 * Get all second-level cache region names
 	 */
 	@Override
 	public String[] getSecondLevelCacheRegionNames() {
 		if ( sessionFactory == null ) {
 			return ArrayHelper.toStringArray( secondLevelCacheStatistics.keySet() );
 		}
 		else {
 			return ArrayHelper.toStringArray( sessionFactory.getAllSecondLevelCacheRegions().keySet() );
 		}
 	}
 	@Override
 	public void endTransaction(boolean success) {
 		transactionCount.getAndIncrement();
 		if ( success ) {
 			committedTransactionCount.getAndIncrement();
 		}
 	}
 	@Override
 	public long getSuccessfulTransactionCount() {
 		return committedTransactionCount.get();
 	}
 	@Override
 	public long getTransactionCount() {
 		return transactionCount.get();
 	}
 	@Override
 	public void closeStatement() {
 		closeStatementCount.getAndIncrement();
 	}
 	@Override
 	public void prepareStatement() {
 		prepareStatementCount.getAndIncrement();
 	}
 	@Override
 	public long getCloseStatementCount() {
 		return closeStatementCount.get();
 	}
 	@Override
 	public long getPrepareStatementCount() {
 		return prepareStatementCount.get();
 	}
 	@Override
 	public void optimisticFailure(String entityName) {
 		optimisticFailureCount.getAndIncrement();
 		( (ConcurrentEntityStatisticsImpl) getEntityStatistics( entityName ) ).incrementOptimisticFailureCount();
 	}
 	@Override
 	public long getOptimisticFailureCount() {
 		return optimisticFailureCount.get();
 	}
 
 	@Override
 	public String toString() {
 		return new StringBuilder()
 				.append( "Statistics[" )
 				.append( "start time=" ).append( startTime )
 				.append( ",sessions opened=" ).append( sessionOpenCount )
 				.append( ",sessions closed=" ).append( sessionCloseCount )
 				.append( ",transactions=" ).append( transactionCount )
 				.append( ",successful transactions=" ).append( committedTransactionCount )
 				.append( ",optimistic lock failures=" ).append( optimisticFailureCount )
 				.append( ",flushes=" ).append( flushCount )
 				.append( ",connections obtained=" ).append( connectCount )
 				.append( ",statements prepared=" ).append( prepareStatementCount )
 				.append( ",statements closed=" ).append( closeStatementCount )
 				.append( ",second level cache puts=" ).append( secondLevelCachePutCount )
 				.append( ",second level cache hits=" ).append( secondLevelCacheHitCount )
 				.append( ",second level cache misses=" ).append( secondLevelCacheMissCount )
 				.append( ",entities loaded=" ).append( entityLoadCount )
 				.append( ",entities updated=" ).append( entityUpdateCount )
 				.append( ",entities inserted=" ).append( entityInsertCount )
 				.append( ",entities deleted=" ).append( entityDeleteCount )
 				.append( ",entities fetched=" ).append( entityFetchCount )
 				.append( ",collections loaded=" ).append( collectionLoadCount )
 				.append( ",collections updated=" ).append( collectionUpdateCount )
 				.append( ",collections removed=" ).append( collectionRemoveCount )
 				.append( ",collections recreated=" ).append( collectionRecreateCount )
 				.append( ",collections fetched=" ).append( collectionFetchCount )
 				.append( ",naturalId queries executed to database=" ).append( naturalIdQueryExecutionCount )
 				.append( ",naturalId cache puts=" ).append( naturalIdCachePutCount )
 				.append( ",naturalId cache hits=" ).append( naturalIdCacheHitCount )
 				.append( ",naturalId cache misses=" ).append( naturalIdCacheMissCount )
 				.append( ",naturalId max query time=" ).append( naturalIdQueryExecutionMaxTime )
 				.append( ",queries executed to database=" ).append( queryExecutionCount )
 				.append( ",query cache puts=" ).append( queryCachePutCount )
 				.append( ",query cache hits=" ).append( queryCacheHitCount )
 				.append( ",query cache misses=" ).append( queryCacheMissCount )
 				.append(",update timestamps cache puts=").append(updateTimestampsCachePutCount)
 				.append(",update timestamps cache hits=").append(updateTimestampsCacheHitCount)
 				.append(",update timestamps cache misses=").append(updateTimestampsCacheMissCount)
 				.append( ",max query time=" ).append( queryExecutionMaxTime )
 				.append( ']' )
 				.toString();
 	}
 	@Override
 	public String getQueryExecutionMaxTimeQueryString() {
 		return queryExecutionMaxTimeQueryString;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/cache/spi/NaturalIdCacheKeyTest.java b/hibernate-core/src/test/java/org/hibernate/cache/spi/NaturalIdCacheKeyTest.java
index 771c92fcf6..9143c57f20 100644
--- a/hibernate-core/src/test/java/org/hibernate/cache/spi/NaturalIdCacheKeyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/cache/spi/NaturalIdCacheKeyTest.java
@@ -1,80 +1,81 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.spi;
 
-import static junit.framework.Assert.assertEquals;
-import static org.junit.Assert.assertArrayEquals;
-import static org.mockito.Matchers.anyObject;
-import static org.mockito.Matchers.eq;
-import static org.mockito.Mockito.mock;
-import static org.mockito.Mockito.when;
-
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
+import org.hibernate.cache.internal.OldNaturalIdCacheKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 import org.junit.Test;
 import org.mockito.invocation.InvocationOnMock;
 import org.mockito.stubbing.Answer;
 
+import static junit.framework.Assert.assertEquals;
+import static org.junit.Assert.assertArrayEquals;
+import static org.mockito.Matchers.anyObject;
+import static org.mockito.Matchers.eq;
+import static org.mockito.Mockito.mock;
+import static org.mockito.Mockito.when;
+
 public class NaturalIdCacheKeyTest {
     @Test
     public void testSerializationRoundTrip() throws Exception {
         final EntityPersister entityPersister = mock(EntityPersister.class);
         final SessionImplementor sessionImplementor = mock(SessionImplementor.class);
         final SessionFactoryImplementor sessionFactoryImplementor = mock(SessionFactoryImplementor.class);
         final Type mockType = mock(Type.class);
         
         when (entityPersister.getRootEntityName()).thenReturn("EntityName");
         
         when(sessionImplementor.getFactory()).thenReturn(sessionFactoryImplementor);
         
         when(entityPersister.getNaturalIdentifierProperties()).thenReturn(new int[] {0, 1, 2});
         when(entityPersister.getPropertyTypes()).thenReturn(new Type[] {
                 mockType,
                 mockType,
                 mockType
         });
         
         when(mockType.getHashCode(anyObject(), eq(sessionFactoryImplementor))).thenAnswer(new Answer<Object>() {
             @Override
             public Object answer(InvocationOnMock invocation) throws Throwable {
                 return invocation.getArguments()[0].hashCode();
             }
         });
         
         when(mockType.disassemble(anyObject(), eq(sessionImplementor), eq(null))).thenAnswer(new Answer<Object>() {
             @Override
             public Object answer(InvocationOnMock invocation) throws Throwable {
                 return invocation.getArguments()[0];
             }
         });
 
-        final NaturalIdCacheKey key = DefaultCacheKeysFactory.createNaturalIdKey( new Object[] {"a", "b", "c"}, entityPersister, sessionImplementor );
+        final OldNaturalIdCacheKey key = (OldNaturalIdCacheKey) DefaultCacheKeysFactory.createNaturalIdKey( new Object[] {"a", "b", "c"}, entityPersister, sessionImplementor );
         
         final ByteArrayOutputStream baos = new ByteArrayOutputStream();
         final ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(key);
         
         final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
-        final NaturalIdCacheKey keyClone = (NaturalIdCacheKey)ois.readObject();
+        final OldNaturalIdCacheKey keyClone = (OldNaturalIdCacheKey) ois.readObject();
         
         assertEquals(key, keyClone);
         assertEquals(key.hashCode(), keyClone.hashCode());
         assertEquals(key.toString(), keyClone.toString());
         assertEquals(key.getEntityName(), keyClone.getEntityName());
         assertArrayEquals(key.getNaturalIdValues(), keyClone.getNaturalIdValues());
         assertEquals(key.getTenantId(), keyClone.getTenantId());
         
     }
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/filter/DynamicFilterTest.java b/hibernate-core/src/test/java/org/hibernate/test/filter/DynamicFilterTest.java
index f28cf9c336..2357c70716 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/filter/DynamicFilterTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/filter/DynamicFilterTest.java
@@ -1,970 +1,969 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.filter;
 
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
-import org.hibernate.cache.spi.CollectionCacheKey;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CollectionCacheEntry;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.criterion.DetachedCriteria;
 import org.hibernate.criterion.Property;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.criterion.Subqueries;
 import org.hibernate.dialect.IngresDialect;
 import org.hibernate.dialect.SybaseASE15Dialect;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.transform.DistinctRootEntityResultTransformer;
 
 import org.hibernate.testing.SkipForDialect;
 import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
 import org.junit.Test;
 
 import org.jboss.logging.Logger;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Implementation of DynamicFilterTest.
  *
  * @author Steve Ebersole
  */
 @SkipForDialect( value = SybaseASE15Dialect.class, jiraKey = "HHH-3637")
 public class DynamicFilterTest extends BaseNonConfigCoreFunctionalTestCase {
 	private static final Logger log = Logger.getLogger( DynamicFilterTest.class );
 
 	@Override
 	public String[] getMappings() {
 		return new String[]{
 			"filter/defs.hbm.xml",
 			"filter/LineItem.hbm.xml",
 			"filter/Order.hbm.xml",
 			"filter/Product.hbm.xml",
 			"filter/Salesperson.hbm.xml",
 			"filter/Department.hbm.xml",
 			"filter/Category.hbm.xml"
 		};
 	}
 
 	@Override
 	protected String getCacheConcurrencyStrategy() {
 		return "nonstrict-read-write";
 	}
 
 	@Override
 	public void addSettings(Map settings) {
 		settings.put( AvailableSettings.MAX_FETCH_DEPTH, "1" );
 		settings.put( AvailableSettings.GENERATE_STATISTICS, "true" );
 		settings.put( AvailableSettings.USE_QUERY_CACHE, "true" );
 	}
 
 	@Test
 	@SkipForDialect( value = {SybaseASE15Dialect.class, IngresDialect.class})
 	public void testSqlSyntaxOfFiltersWithUnions() {
 		Session session = openSession();
 		session.enableFilter( "unioned" );
 		session.createQuery( "from Category" ).list();
 		session.close();
 	}
 
 	@Test
 	public void testSecondLevelCachedCollectionsFiltering() {
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		long ts = ( ( SessionImplementor ) session ).getTimestamp();
 
 		// Force a collection into the second level cache, with its non-filtered elements
 		Salesperson sp = ( Salesperson ) session.load( Salesperson.class, testData.steveId );
 		Hibernate.initialize( sp.getOrders() );
 		CollectionPersister persister = sessionFactory().getCollectionPersister( Salesperson.class.getName() + ".orders" );
 		assertTrue( "No cache for collection", persister.hasCache() );
 		CollectionRegionAccessStrategy cache = persister.getCacheAccessStrategy();
-		CollectionCacheKey cacheKey = cache.generateCacheKey(
+		Object cacheKey = cache.generateCacheKey(
 				testData.steveId,
 				persister,
 				sessionFactory(),
 				session.getTenantIdentifier()
 		);
 		CollectionCacheEntry cachedData = ( CollectionCacheEntry ) cache.get( cacheKey, ts );
 		assertNotNull( "collection was not in cache", cachedData );
 
 		session.close();
 
 		session = openSession();
 		ts = ( ( SessionImplementor ) session ).getTimestamp();
 		session.enableFilter( "fulfilledOrders" ).setParameter( "asOfDate", testData.lastMonth.getTime() );
 		sp = ( Salesperson ) session.createQuery( "from Salesperson as s where s.id = :id" )
 				.setLong( "id", testData.steveId )
 				.uniqueResult();
 		assertEquals( "Filtered-collection not bypassing 2L-cache", 1, sp.getOrders().size() );
 
-		CollectionCacheKey cacheKey2 = cache.generateCacheKey(
+		Object cacheKey2 = cache.generateCacheKey(
 				testData.steveId,
 				persister,
 				sessionFactory(),
 				session.getTenantIdentifier()
 		);
 		CollectionCacheEntry cachedData2 = ( CollectionCacheEntry ) persister.getCacheAccessStrategy().get( cacheKey2, ts );
 		assertNotNull( "collection no longer in cache!", cachedData2 );
 		assertSame( "Different cache values!", cachedData, cachedData2 );
 
 		session.close();
 
 		session = openSession();
 		session.enableFilter( "fulfilledOrders" ).setParameter( "asOfDate", testData.lastMonth.getTime() );
 		sp = ( Salesperson ) session.load( Salesperson.class, testData.steveId );
 		assertEquals( "Filtered-collection not bypassing 2L-cache", 1, sp.getOrders().size() );
 
 		session.close();
 
 		// Finally, make sure that the original cached version did not get over-written
 		session = openSession();
 		sp = ( Salesperson ) session.load( Salesperson.class, testData.steveId );
 		assertEquals( "Actual cached version got over-written", 2, sp.getOrders().size() );
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testCombinedClassAndCollectionFiltersEnabled() {
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.enableFilter( "regionlist" ).setParameterList( "regions", new String[]{"LA", "APAC"} );
 		session.enableFilter( "fulfilledOrders" ).setParameter( "asOfDate", testData.lastMonth.getTime() );
 
 		// test retreival through hql with the collection as non-eager
 		List salespersons = session.createQuery( "select s from Salesperson as s" ).list();
 		assertEquals( "Incorrect salesperson count", 1, salespersons.size() );
 		Salesperson sp = ( Salesperson ) salespersons.get( 0 );
 		assertEquals( "Incorrect order count", 1, sp.getOrders().size() );
 
 		session.clear();
 
 		session.disableFilter( "regionlist" );
 		session.enableFilter( "regionlist" ).setParameterList( "regions", new String[]{"LA", "APAC", "APAC"} );
 		// Second test retreival through hql with the collection as non-eager with different region list
 		salespersons = session.createQuery( "select s from Salesperson as s" ).list();
 		assertEquals( "Incorrect salesperson count", 1, salespersons.size() );
 		sp = ( Salesperson ) salespersons.get( 0 );
 		assertEquals( "Incorrect order count", 1, sp.getOrders().size() );
 
 		session.clear();
 
 
 		// test retreival through hql with the collection join fetched
 		salespersons = session.createQuery( "select s from Salesperson as s left join fetch s.orders" ).list();
 		assertEquals( "Incorrect salesperson count", 1, salespersons.size() );
 		sp = ( Salesperson ) salespersons.get( 0 );
 		assertEquals( "Incorrect order count", 1, sp.getOrders().size() );
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testHqlFilters() {
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// HQL test
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         log.info( "Starting HQL filter tests" );
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.enableFilter( "region" ).setParameter( "region", "APAC" );
 
 		session.enableFilter( "effectiveDate" )
 		        .setParameter( "asOfDate", testData.lastMonth.getTime() );
 
         log.info( "HQL against Salesperson..." );
 		List results = session.createQuery( "select s from Salesperson as s left join fetch s.orders" ).list();
 		assertTrue( "Incorrect filtered HQL result count [" + results.size() + "]", results.size() == 1 );
 		Salesperson result = ( Salesperson ) results.get( 0 );
 		assertTrue( "Incorrect collectionfilter count", result.getOrders().size() == 1 );
 
         log.info( "HQL against Product..." );
 		results = session.createQuery( "from Product as p where p.stockNumber = ?" ).setInteger( 0, 124 ).list();
 		assertTrue( results.size() == 1 );
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testFiltersWithCustomerReadAndWrite() {
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Custom SQL read/write with filter
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         log.info("Starting HQL filter with custom SQL get/set tests");
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.enableFilter( "heavyProducts" ).setParameter("weightKilograms", 4d);
         log.info( "HQL against Product..." );
 		List results = session.createQuery( "from Product").list();
 		assertEquals( 1, results.size() );
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testCriteriaQueryFilters() {
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Criteria-query test
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         log.info("Starting Criteria-query filter tests");
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.enableFilter( "region" ).setParameter( "region", "APAC" );
 
 		session.enableFilter( "fulfilledOrders" )
 		        .setParameter( "asOfDate", testData.lastMonth.getTime() );
 
 		session.enableFilter( "effectiveDate" )
 		        .setParameter( "asOfDate", testData.lastMonth.getTime() );
 
         log.info("Criteria query against Salesperson...");
 		List salespersons = session.createCriteria( Salesperson.class )
 		        .setFetchMode( "orders", FetchMode.JOIN )
 		        .list();
 		assertEquals( "Incorrect salesperson count", 1, salespersons.size() );
 		assertEquals( "Incorrect order count", 1, ( ( Salesperson ) salespersons.get( 0 ) ).getOrders().size() );
 
         log.info("Criteria query against Product...");
 		List products = session.createCriteria( Product.class )
 		        .add( Restrictions.eq( "stockNumber", 124 ) )
 		        .list();
 		assertEquals( "Incorrect product count", 1, products.size() );
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testCriteriaControl() {
 		TestData testData = new TestData();
 		testData.prepare();
 
 		// the subquery...
 		DetachedCriteria subquery = DetachedCriteria.forClass( Salesperson.class )
 				.setProjection( Property.forName( "name" ) );
 
 		Session session = openSession();
 		session.beginTransaction();
 		session.enableFilter( "fulfilledOrders" ).setParameter( "asOfDate", testData.lastMonth.getTime() );
 		session.enableFilter( "regionlist" ).setParameterList( "regions", new String[] {"APAC"} );
 
 		List result = session.createCriteria( Order.class )
 				.add( Subqueries.in( "steve", subquery ) )
 				.list();
 		assertEquals( 1, result.size() );
 
 		session.getTransaction().commit();
 		session.close();
 
 		testData.release();
 	}
 
 	@Test
 	public void testCriteriaSubqueryWithFilters() {
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Criteria-subquery test
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         log.info("Starting Criteria-subquery filter tests");
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.enableFilter("region").setParameter("region", "APAC");
 
         log.info("Criteria query against Department with a subquery on Salesperson in the APAC reqion...");
 		DetachedCriteria salespersonSubquery = DetachedCriteria.forClass(Salesperson.class)
 				.add(Restrictions.eq("name", "steve"))
 				.setProjection(Property.forName("department"));
 
 		Criteria departmentsQuery = session.createCriteria(Department.class).add(Subqueries.propertyIn("id", salespersonSubquery));
 		List departments = departmentsQuery.list();
 
 		assertEquals("Incorrect department count", 1, departments.size());
 
         log.info("Criteria query against Department with a subquery on Salesperson in the FooBar reqion...");
 
 		session.enableFilter("region").setParameter("region", "Foobar");
 		departments = departmentsQuery.list();
 
 		assertEquals("Incorrect department count", 0, departments.size());
 
         log.info("Criteria query against Order with a subquery for line items with a subquery on product and sold by a given sales person...");
 		session.enableFilter("region").setParameter("region", "APAC");
 
 		DetachedCriteria lineItemSubquery = DetachedCriteria.forClass(LineItem.class)
 				.add( Restrictions.ge( "quantity", 1L ) )
 				.createCriteria( "product" )
 				.add( Restrictions.eq( "name", "Acme Hair Gel" ) )
 				.setProjection( Property.forName( "id" ) );
 
 		List orders = session.createCriteria(Order.class)
 				.add(Subqueries.exists(lineItemSubquery))
 				.add(Restrictions.eq("buyer", "gavin"))
 				.list();
 
 		assertEquals("Incorrect orders count", 1, orders.size());
 
         log.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of last month");
 		session.enableFilter("region").setParameter("region", "APAC");
 		session.enableFilter("effectiveDate").setParameter("asOfDate", testData.lastMonth.getTime());
 
 		DetachedCriteria productSubquery = DetachedCriteria.forClass(Product.class)
 				.add(Restrictions.eq("name", "Acme Hair Gel"))
 				.setProjection(Property.forName("id"));
 
 		lineItemSubquery = DetachedCriteria.forClass(LineItem.class)
 				.add(Restrictions.ge("quantity", 1L ))
 				.createCriteria("product")
 				.add(Subqueries.propertyIn("id", productSubquery))
 				.setProjection(Property.forName("id"));
 
 		orders = session.createCriteria(Order.class)
 				.add(Subqueries.exists(lineItemSubquery))
 				.add(Restrictions.eq("buyer", "gavin"))
 				.list();
 
 		assertEquals("Incorrect orders count", 1, orders.size());
 
 
         log.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of 4 months ago");
 		session.enableFilter("region").setParameter("region", "APAC");
 		session.enableFilter("effectiveDate").setParameter("asOfDate", testData.fourMonthsAgo.getTime());
 
 		orders = session.createCriteria(Order.class)
 				.add(Subqueries.exists(lineItemSubquery))
 				.add(Restrictions.eq("buyer", "gavin"))
 				.list();
 
 		assertEquals("Incorrect orders count", 0, orders.size());
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testHQLSubqueryWithFilters() {
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// HQL subquery with filters test
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         log.info("Starting HQL subquery with filters tests");
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.enableFilter("region").setParameter("region", "APAC");
 
         log.info("query against Department with a subquery on Salesperson in the APAC reqion...");
 
 		List departments = session.createQuery(
 				"select d from Department as d where d.id in (select s.department from Salesperson s where s.name = ?)"
 		).setString( 0, "steve" ).list();
 
 		assertEquals("Incorrect department count", 1, departments.size());
 
         log.info("query against Department with a subquery on Salesperson in the FooBar reqion...");
 
 		session.enableFilter("region").setParameter( "region", "Foobar" );
 		departments = session.createQuery("select d from Department as d where d.id in (select s.department from Salesperson s where s.name = ?)").setString(0, "steve").list();
 
 		assertEquals( "Incorrect department count", 0, departments.size() );
 
         log.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region for a given buyer");
 		session.enableFilter("region").setParameter( "region", "APAC" );
 
 		List orders = session.createQuery("select o from Order as o where exists (select li.id from LineItem li, Product as p where p.id = li.product and li.quantity >= ? and p.name = ?) and o.buyer = ?")
 				.setLong(0, 1L).setString(1, "Acme Hair Gel").setString(2, "gavin").list();
 
 		assertEquals( "Incorrect orders count", 1, orders.size() );
 
         log.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of last month");
 
 		session.enableFilter("region").setParameter("region", "APAC");
 		session.enableFilter("effectiveDate").setParameter( "asOfDate", testData.lastMonth.getTime() );
 
 		orders = session.createQuery("select o from Order as o where exists (select li.id from LineItem li where li.quantity >= ? and li.product in (select p.id from Product p where p.name = ?)) and o.buyer = ?")
 				.setLong(0, 1L).setString(1, "Acme Hair Gel").setString(2, "gavin").list();
 
 		assertEquals( "Incorrect orders count", 1, orders.size() );
 
 
         log.info(
 				"query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of 4 months ago"
 		);
 
 		session.enableFilter("region").setParameter("region", "APAC");
 		session.enableFilter("effectiveDate").setParameter("asOfDate", testData.fourMonthsAgo.getTime());
 
 		orders = session.createQuery("select o from Order as o where exists (select li.id from LineItem li where li.quantity >= ? and li.product in (select p.id from Product p where p.name = ?)) and o.buyer = ?")
 				.setLong( 0, 1L ).setString( 1, "Acme Hair Gel" ).setString( 2, "gavin" ).list();
 
 		assertEquals("Incorrect orders count", 0, orders.size());
 
         log.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of last month with named types");
 
 		session.enableFilter("region").setParameter("region", "APAC");
 		session.enableFilter("effectiveDate").setParameter("asOfDate", testData.lastMonth.getTime());
 
 		orders = session.createQuery("select o from Order as o where exists (select li.id from LineItem li where li.quantity >= :quantity and li.product in (select p.id from Product p where p.name = :name)) and o.buyer = :buyer")
 				.setLong("quantity", 1L).setString("name", "Acme Hair Gel").setString("buyer", "gavin").list();
 
 		assertEquals("Incorrect orders count", 1, orders.size());
 
         log.info("query against Order with a subquery for line items with a subquery line items where the product name is Acme Hair Gel and the quantity is greater than 1 in a given region and the product is effective as of last month with mixed types");
 
 		session.enableFilter("region").setParameter("region", "APAC");
 		session.enableFilter("effectiveDate").setParameter("asOfDate", testData.lastMonth.getTime());
 
 		orders = session.createQuery("select o from Order as o where exists (select li.id from LineItem li where li.quantity >= ? and li.product in (select p.id from Product p where p.name = ?)) and o.buyer = :buyer")
 				.setLong( 0, 1L ).setString( 1, "Acme Hair Gel" ).setString( "buyer", "gavin" ).list();
 
 		assertEquals("Incorrect orders count", 1, orders.size());
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testFilterApplicationOnHqlQueryWithImplicitSubqueryContainingPositionalParameter() {
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.beginTransaction();
 
 		final String queryString = "from Order o where ? in ( select sp.name from Salesperson sp )";
 
 		// first a control-group query
 		List result = session.createQuery( queryString ).setParameter( 0, "steve" ).list();
 		assertEquals( 2, result.size() );
 
 		// now lets enable filters on Order...
 		session.enableFilter( "fulfilledOrders" ).setParameter( "asOfDate", testData.lastMonth.getTime() );
 		result = session.createQuery( queryString ).setParameter( 0, "steve" ).list();
 		assertEquals( 1, result.size() );
 
 		// now, lets additionally enable filter on Salesperson.  First a valid one...
 		session.enableFilter( "regionlist" ).setParameterList( "regions", new String[] { "APAC" } );
 		result = session.createQuery( queryString ).setParameter( 0, "steve" ).list();
 		assertEquals( 1, result.size() );
 
 		// ... then a silly one...
 		session.enableFilter( "regionlist" ).setParameterList( "regions", new String[] { "gamma quadrant" } );
 		result = session.createQuery( queryString ).setParameter( 0, "steve" ).list();
 		assertEquals( 0, result.size() );
 
 		session.getTransaction().commit();
 		session.close();
 
 		testData.release();
 	}
 
 	@Test
 	public void testFilterApplicationOnHqlQueryWithImplicitSubqueryContainingNamedParameter() {
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.beginTransaction();
 
 		final String queryString = "from Order o where :salesPersonName in ( select sp.name from Salesperson sp )";
 
 		// first a control-group query
 		List result = session.createQuery( queryString ).setParameter( "salesPersonName", "steve" ).list();
 		assertEquals( 2, result.size() );
 
 		// now lets enable filters on Order...
 		session.enableFilter( "fulfilledOrders" ).setParameter( "asOfDate", testData.lastMonth.getTime() );
 		result = session.createQuery( queryString ).setParameter( "salesPersonName", "steve" ).list();
 		assertEquals( 1, result.size() );
 
 		// now, lets additionally enable filter on Salesperson.  First a valid one...
 		session.enableFilter( "regionlist" ).setParameterList( "regions", new String[] { "APAC" } );
 		result = session.createQuery( queryString ).setParameter( "salesPersonName", "steve" ).list();
 		assertEquals( 1, result.size() );
 
 		// ... then a silly one...
 		session.enableFilter( "regionlist" ).setParameterList( "regions", new String[] { "gamma quadrant" } );
 		result = session.createQuery( queryString ).setParameter( "salesPersonName", "steve" ).list();
 		assertEquals( 0, result.size() );
 
 		session.getTransaction().commit();
 		session.close();
 
 		testData.release();
 	}
 
 	@Test
 	public void testFiltersOnSimpleHqlDelete() {
 		Session session = openSession();
 		session.beginTransaction();
 		Salesperson sp = new Salesperson();
 		sp.setName( "steve" );
 		sp.setRegion( "NA" );
 		session.persist( sp );
 		Salesperson sp2 = new Salesperson();
 		sp2.setName( "john" );
 		sp2.setRegion( "APAC" );
 		session.persist( sp2 );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		session.enableFilter( "region" ).setParameter( "region", "NA" );
 		int count = session.createQuery( "delete from Salesperson" ).executeUpdate();
 		assertEquals( 1, count );
 		session.delete( sp2 );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testFiltersOnMultiTableHqlDelete() {
 		Session session = openSession();
 		session.beginTransaction();
 		Salesperson sp = new Salesperson();
 		sp.setName( "steve" );
 		sp.setRegion( "NA" );
 		session.persist( sp );
 		Salesperson sp2 = new Salesperson();
 		sp2.setName( "john" );
 		sp2.setRegion( "APAC" );
 		session.persist( sp2 );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		session.enableFilter( "region" ).setParameter( "region", "NA" );
 		int count = session.createQuery( "delete from Salesperson" ).executeUpdate();
 		assertEquals( 1, count );
 		session.delete( sp2 );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testGetFilters() {
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Get() test
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         log.info("Starting get() filter tests (eager assoc. fetching).");
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.enableFilter( "region" ).setParameter( "region", "APAC" );
 
         log.info("Performing get()...");
 		Salesperson salesperson = ( Salesperson ) session.get( Salesperson.class, testData.steveId );
 		assertNotNull( salesperson );
 		assertEquals( "Incorrect order count", 1, salesperson.getOrders().size() );
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testOneToManyFilters() {
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// one-to-many loading tests
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         log.info("Starting one-to-many collection loader filter tests.");
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.enableFilter( "seniorSalespersons" )
 		        .setParameter( "asOfDate", testData.lastMonth.getTime() );
 
         log.info("Performing load of Department...");
 		Department department = ( Department ) session.load( Department.class, testData.deptId );
 		Set salespersons = department.getSalespersons();
 		assertEquals( "Incorrect salesperson count", 1, salespersons.size() );
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testInStyleFilterParameter() {
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// one-to-many loading tests
 		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
         log.info("Starting one-to-many collection loader filter tests.");
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.enableFilter( "regionlist" )
 		        .setParameterList( "regions", new String[]{"LA", "APAC"} );
 
         log.debug("Performing query of Salespersons");
 		List salespersons = session.createQuery( "from Salesperson" ).list();
 		assertEquals( "Incorrect salesperson count", 1, salespersons.size() );
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testManyToManyFilterOnCriteria() {
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.enableFilter( "effectiveDate" ).setParameter( "asOfDate", new Date() );
 
 		Product prod = ( Product ) session.createCriteria( Product.class )
 		        .setResultTransformer( DistinctRootEntityResultTransformer.INSTANCE )
 		        .add( Restrictions.eq( "id", testData.prod1Id ) )
 		        .uniqueResult();
 
 		assertNotNull( prod );
 		assertEquals( "Incorrect Product.categories count for filter", 1, prod.getCategories().size() );
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testManyToManyFilterOnLoad() {
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.enableFilter( "effectiveDate" ).setParameter( "asOfDate", new Date() );
 
 		Product prod = ( Product ) session.get( Product.class, testData.prod1Id );
 
 		long initLoadCount = sessionFactory().getStatistics().getCollectionLoadCount();
 		long initFetchCount = sessionFactory().getStatistics().getCollectionFetchCount();
 
 		// should already have been initialized...
 		int size = prod.getCategories().size();
 		assertEquals( "Incorrect filtered collection count", 1, size );
 
 		long currLoadCount = sessionFactory().getStatistics().getCollectionLoadCount();
 		long currFetchCount = sessionFactory().getStatistics().getCollectionFetchCount();
 
 		assertTrue(
 		        "load with join fetch of many-to-many did not trigger join fetch",
 		        ( initLoadCount == currLoadCount ) && ( initFetchCount == currFetchCount )
 		);
 
 		// make sure we did not get back a collection of proxies
 		long initEntityLoadCount = sessionFactory().getStatistics().getEntityLoadCount();
 		Iterator itr = prod.getCategories().iterator();
 		while ( itr.hasNext() ) {
 			Category cat = ( Category ) itr.next();
 			System.out.println( " ===> " + cat.getName() );
 		}
 		long currEntityLoadCount = sessionFactory().getStatistics().getEntityLoadCount();
 
 		assertTrue(
 		        "load with join fetch of many-to-many did not trigger *complete* join fetch",
 		        ( initEntityLoadCount == currEntityLoadCount )
 		);
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testManyToManyOnCollectionLoadAfterHQL() {
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.enableFilter( "effectiveDate" ).setParameter( "asOfDate", new Date() );
 
 		// Force the categories to not get initialized here
 		List result = session.createQuery( "from Product as p where p.id = :id" )
 		        .setLong( "id", testData.prod1Id )
 		        .list();
 		assertTrue( "No products returned from HQL", !result.isEmpty() );
 
 		Product prod = ( Product ) result.get( 0 );
 		assertNotNull( prod );
 		assertEquals( "Incorrect Product.categories count for filter on collection load", 1, prod.getCategories().size() );
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testManyToManyFilterOnQuery() {
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 		session.enableFilter( "effectiveDate" ).setParameter( "asOfDate", new Date() );
 
 		List result = session.createQuery( "from Product p inner join fetch p.categories" ).list();
 		assertTrue( "No products returned from HQL many-to-many filter case", !result.isEmpty() );
 
 		Product prod = ( Product ) result.get( 0 );
 
 		assertNotNull( prod );
 		assertEquals( "Incorrect Product.categories count for filter with HQL", 1, prod.getCategories().size() );
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testManyToManyBase() {
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 
 		Product prod = ( Product ) session.get( Product.class, testData.prod1Id );
 
 		long initLoadCount = sessionFactory().getStatistics().getCollectionLoadCount();
 		long initFetchCount = sessionFactory().getStatistics().getCollectionFetchCount();
 
 		// should already have been initialized...
 		int size = prod.getCategories().size();
 		assertEquals( "Incorrect non-filtered collection count", 2, size );
 
 		long currLoadCount = sessionFactory().getStatistics().getCollectionLoadCount();
 		long currFetchCount = sessionFactory().getStatistics().getCollectionFetchCount();
 
 		assertTrue(
 		        "load with join fetch of many-to-many did not trigger join fetch",
 		        ( initLoadCount == currLoadCount ) && ( initFetchCount == currFetchCount )
 		);
 
 		// make sure we did not get back a collection of proxies
 		long initEntityLoadCount = sessionFactory().getStatistics().getEntityLoadCount();
 		Iterator itr = prod.getCategories().iterator();
 		while ( itr.hasNext() ) {
 			Category cat = ( Category ) itr.next();
 			System.out.println( " ===> " + cat.getName() );
 		}
 		long currEntityLoadCount = sessionFactory().getStatistics().getEntityLoadCount();
 
 		assertTrue(
 		        "load with join fetch of many-to-many did not trigger *complete* join fetch",
 		        ( initEntityLoadCount == currEntityLoadCount )
 		);
 
 		session.close();
 		testData.release();
 	}
 
 	@Test
 	public void testManyToManyBaseThruCriteria() {
 		TestData testData = new TestData();
 		testData.prepare();
 
 		Session session = openSession();
 
 		List result = session.createCriteria( Product.class )
 		        .add( Restrictions.eq( "id", testData.prod1Id ) )
 		        .list();
 
 		Product prod = ( Product ) result.get( 0 );
 
 		long initLoadCount = sessionFactory().getStatistics().getCollectionLoadCount();
 		long initFetchCount = sessionFactory().getStatistics().getCollectionFetchCount();
 
 		// should already have been initialized...
 		int size = prod.getCategories().size();
 		assertEquals( "Incorrect non-filtered collection count", 2, size );
 
 		long currLoadCount = sessionFactory().getStatistics().getCollectionLoadCount();
 		long currFetchCount = sessionFactory().getStatistics().getCollectionFetchCount();
 
 		assertTrue(
 		        "load with join fetch of many-to-many did not trigger join fetch",
 		        ( initLoadCount == currLoadCount ) && ( initFetchCount == currFetchCount )
 		);
 
 		// make sure we did not get back a collection of proxies
 		long initEntityLoadCount = sessionFactory().getStatistics().getEntityLoadCount();
 		Iterator itr = prod.getCategories().iterator();
 		while ( itr.hasNext() ) {
 			Category cat = ( Category ) itr.next();
 			System.out.println( " ===> " + cat.getName() );
 		}
 		long currEntityLoadCount = sessionFactory().getStatistics().getEntityLoadCount();
 
 		assertTrue(
 		        "load with join fetch of many-to-many did not trigger *complete* join fetch",
 		        ( initEntityLoadCount == currEntityLoadCount )
 		);
 
 		session.close();
 		testData.release();
 	}
 
 	private class TestData {
 		private Long steveId;
 		private Long deptId;
 		private Long prod1Id;
 		private Calendar lastMonth;
 		private Calendar nextMonth;
 		private Calendar sixMonthsAgo;
 		private Calendar fourMonthsAgo;
 
 		private List entitiesToCleanUp = new ArrayList();
 
 		private void prepare() {
 			Session session = openSession();
 			Transaction transaction = session.beginTransaction();
 
 			lastMonth = new GregorianCalendar();
 			lastMonth.add( Calendar.MONTH, -1 );
 
 			nextMonth = new GregorianCalendar();
 			nextMonth.add( Calendar.MONTH, 1 );
 
 			sixMonthsAgo = new GregorianCalendar();
 			sixMonthsAgo.add( Calendar.MONTH, -6 );
 
 			fourMonthsAgo = new GregorianCalendar();
 			fourMonthsAgo.add( Calendar.MONTH, -4 );
 
 			Department dept = new Department();
 			dept.setName( "Sales" );
 
 			session.save( dept );
 			deptId = dept.getId();
 			entitiesToCleanUp.add( dept );
 
 			Salesperson steve = new Salesperson();
 			steve.setName( "steve" );
 			steve.setRegion( "APAC" );
 			steve.setHireDate( sixMonthsAgo.getTime() );
 
 			steve.setDepartment( dept );
 			dept.getSalespersons().add( steve );
 
 			Salesperson max = new Salesperson();
 			max.setName( "max" );
 			max.setRegion( "EMEA" );
 			max.setHireDate( nextMonth.getTime() );
 
 			max.setDepartment( dept );
 			dept.getSalespersons().add( max );
 
 			session.save( steve );
 			session.save( max );
 			entitiesToCleanUp.add( steve );
 			entitiesToCleanUp.add( max );
 
 			steveId = steve.getId();
 
 			Category cat1 = new Category( "test cat 1", lastMonth.getTime(), nextMonth.getTime() );
 			Category cat2 = new Category( "test cat 2", sixMonthsAgo.getTime(), fourMonthsAgo.getTime() );
 
 			Product product1 = new Product();
 			product1.setName( "Acme Hair Gel" );
 			product1.setStockNumber( 123 );
 			product1.setWeightPounds( 0.25 );
 			product1.setEffectiveStartDate( lastMonth.getTime() );
 			product1.setEffectiveEndDate( nextMonth.getTime() );
 
 			product1.addCategory( cat1 );
 			product1.addCategory( cat2 );
 
 			session.save( product1 );
 			entitiesToCleanUp.add( product1 );
 			prod1Id = product1.getId();
 
 			Order order1 = new Order();
 			order1.setBuyer( "gavin" );
 			order1.setRegion( "APAC" );
 			order1.setPlacementDate( sixMonthsAgo.getTime() );
 			order1.setFulfillmentDate( fourMonthsAgo.getTime() );
 			order1.setSalesperson( steve );
 			order1.addLineItem( product1, 500 );
 
 			session.save( order1 );
 			entitiesToCleanUp.add( order1 );
 
 			Product product2 = new Product();
 			product2.setName( "Acme Super-Duper DTO Factory" );
 			product2.setStockNumber( 124 );
 			product1.setWeightPounds( 10.0 );
 			product2.setEffectiveStartDate( sixMonthsAgo.getTime() );
 			product2.setEffectiveEndDate( new Date() );
 
 			Category cat3 = new Category( "test cat 2", sixMonthsAgo.getTime(), new Date() );
 			product2.addCategory( cat3 );
 
 			session.save( product2 );
 			entitiesToCleanUp.add( product2 );
 
 			// An uncategorized product
 			Product product3 = new Product();
 			product3.setName( "Uncategorized product" );
 			session.save( product3 );
 			entitiesToCleanUp.add( product3 );
 
 			Order order2 = new Order();
 			order2.setBuyer( "christian" );
 			order2.setRegion( "EMEA" );
 			order2.setPlacementDate( lastMonth.getTime() );
 			order2.setSalesperson( steve );
 			order2.addLineItem( product2, -1 );
 
 			session.save( order2 );
 			entitiesToCleanUp.add( order2 );
 
 			transaction.commit();
 			session.close();
 		}
 
 		private void release() {
 			Session session = openSession();
 			Transaction transaction = session.beginTransaction();
 
 			Iterator itr = entitiesToCleanUp.iterator();
 			while ( itr.hasNext() ) {
 				session.delete( itr.next() );
 			}
 
 			transaction.commit();
 			session.close();
 		}
 	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareCollectionRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareCollectionRegionAccessStrategy.java
index 94b6457767..ffa8c52fef 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareCollectionRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareCollectionRegionAccessStrategy.java
@@ -1,172 +1,172 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.nonstop;
 
-import java.io.Serializable;
-
 import net.sf.ehcache.constructs.nonstop.NonStopCacheException;
-
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
-import org.hibernate.cache.spi.CollectionCacheKey;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * Implementation of {@link CollectionRegionAccessStrategy} that handles {@link NonStopCacheException} using
  * {@link HibernateNonstopCacheExceptionHandler}
  *
  * @author Abhishek Sanoujam
  * @author Alex Snaps
  */
 public class NonstopAwareCollectionRegionAccessStrategy implements CollectionRegionAccessStrategy {
 	private final CollectionRegionAccessStrategy actualStrategy;
 	private final HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler;
 
 	/**
 	 * Constructor accepting the actual {@link CollectionRegionAccessStrategy} and the {@link HibernateNonstopCacheExceptionHandler}
 	 *
 	 * @param actualStrategy The wrapped strategy
 	 * @param hibernateNonstopExceptionHandler The exception handler
 	 */
 	public NonstopAwareCollectionRegionAccessStrategy(
 			CollectionRegionAccessStrategy actualStrategy,
 			HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler) {
 		this.actualStrategy = actualStrategy;
 		this.hibernateNonstopExceptionHandler = hibernateNonstopExceptionHandler;
 	}
 
 	@Override
 	public CollectionRegion getRegion() {
 		return actualStrategy.getRegion();
 	}
 
 	@Override
-	public void evict(CollectionCacheKey key) throws CacheException {
+	public void evict(Object key) throws CacheException {
 		try {
 			actualStrategy.evict( key );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
 	public void evictAll() throws CacheException {
 		try {
 			actualStrategy.evictAll();
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
-	public Object get(CollectionCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		try {
 			return actualStrategy.get( key, txTimestamp );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return null;
 		}
 	}
 
 	@Override
-	public SoftLock lockItem(CollectionCacheKey key, Object version) throws CacheException {
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		try {
 			return actualStrategy.lockItem( key, version );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return null;
 		}
 	}
 
 	@Override
 	public SoftLock lockRegion() throws CacheException {
 		try {
 			return actualStrategy.lockRegion();
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return null;
 		}
 	}
 
 	@Override
-	public boolean putFromLoad(CollectionCacheKey key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		try {
 			return actualStrategy.putFromLoad( key, value, txTimestamp, version, minimalPutOverride );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
-	public boolean putFromLoad(CollectionCacheKey key, Object value, long txTimestamp, Object version) throws CacheException {
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
 		try {
 			return actualStrategy.putFromLoad( key, value, txTimestamp, version );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
-	public void remove(CollectionCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		try {
 			actualStrategy.remove( key );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
 	public void removeAll() throws CacheException {
 		try {
 			actualStrategy.removeAll();
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
-	public void unlockItem(CollectionCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		try {
 			actualStrategy.unlockItem( key, lock );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
 	public void unlockRegion(SoftLock lock) throws CacheException {
 		try {
 			actualStrategy.unlockRegion( lock );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
-	public CollectionCacheKey generateCacheKey(Serializable id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+	public Object generateCacheKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
 		return DefaultCacheKeysFactory.createCollectionKey( id, persister, factory, tenantIdentifier );
 	}
 
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getCollectionId(cacheKey);
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareEntityRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareEntityRegionAccessStrategy.java
index ca3795e1f9..7371faaf89 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareEntityRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareEntityRegionAccessStrategy.java
@@ -1,217 +1,218 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.nonstop;
 
-import java.io.Serializable;
-
 import net.sf.ehcache.constructs.nonstop.NonStopCacheException;
-
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Implementation of {@link EntityRegionAccessStrategy} that handles {@link net.sf.ehcache.constructs.nonstop.NonStopCacheException} using
  * {@link HibernateNonstopCacheExceptionHandler}
  *
  * @author Abhishek Sanoujam
  * @author Alex Snaps
  */
 public class NonstopAwareEntityRegionAccessStrategy implements EntityRegionAccessStrategy {
 	private final EntityRegionAccessStrategy actualStrategy;
 	private final HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler;
 
 	/**
 	 * Constructor accepting the actual {@link EntityRegionAccessStrategy} and the {@link HibernateNonstopCacheExceptionHandler}
 	 *
 	 * @param actualStrategy The wrapped EntityRegionAccessStrategy
 	 * @param hibernateNonstopExceptionHandler The exception handler
 	 */
 	public NonstopAwareEntityRegionAccessStrategy(
 			EntityRegionAccessStrategy actualStrategy,
 			HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler) {
 		this.actualStrategy = actualStrategy;
 		this.hibernateNonstopExceptionHandler = hibernateNonstopExceptionHandler;
 	}
 
 	@Override
 	public EntityRegion getRegion() {
 		return actualStrategy.getRegion();
 	}
 
 	@Override
-	public boolean afterInsert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
 		try {
 			return actualStrategy.afterInsert( key, value, version );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
-	public boolean afterUpdate(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
 			throws CacheException {
 		try {
 			return actualStrategy.afterUpdate( key, value, currentVersion, previousVersion, lock );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
-	public void evict(EntityCacheKey key) throws CacheException {
+	public void evict(Object key) throws CacheException {
 		try {
 			actualStrategy.evict( key );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
 	public void evictAll() throws CacheException {
 		try {
 			actualStrategy.evictAll();
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
-	public Object get(EntityCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		try {
 			return actualStrategy.get( key, txTimestamp );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return null;
 		}
 	}
 
 	@Override
-	public boolean insert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean insert(Object key, Object value, Object version) throws CacheException {
 		try {
 			return actualStrategy.insert( key, value, version );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
-	public SoftLock lockItem(EntityCacheKey key, Object version) throws CacheException {
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		try {
 			return actualStrategy.lockItem( key, version );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return null;
 		}
 	}
 
 	@Override
 	public SoftLock lockRegion() throws CacheException {
 		try {
 			return actualStrategy.lockRegion();
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return null;
 		}
 	}
 
 	@Override
-	public boolean putFromLoad(EntityCacheKey key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		try {
 			return actualStrategy.putFromLoad( key, value, txTimestamp, version, minimalPutOverride );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
-	public boolean putFromLoad(EntityCacheKey key, Object value, long txTimestamp, Object version) throws CacheException {
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
 		try {
 			return actualStrategy.putFromLoad( key, value, txTimestamp, version );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
-	public void remove(EntityCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		try {
 			actualStrategy.remove( key );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
 	public void removeAll() throws CacheException {
 		try {
 			actualStrategy.removeAll();
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
-	public void unlockItem(EntityCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		try {
 			actualStrategy.unlockItem( key, lock );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
 	public void unlockRegion(SoftLock lock) throws CacheException {
 		try {
 			actualStrategy.unlockRegion( lock );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
-	public boolean update(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion)
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
 			throws CacheException {
 		try {
 			return actualStrategy.update( key, value, currentVersion, previousVersion );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
-	public EntityCacheKey generateCacheKey(Serializable id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+	public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
 		return DefaultCacheKeysFactory.createEntityKey( id, persister, factory, tenantIdentifier );
 	}
+
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getEntityId(cacheKey);
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareNaturalIdRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareNaturalIdRegionAccessStrategy.java
index 22202fe7e3..67d3283830 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareNaturalIdRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/nonstop/NonstopAwareNaturalIdRegionAccessStrategy.java
@@ -1,214 +1,216 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.nonstop;
 
 import net.sf.ehcache.constructs.nonstop.NonStopCacheException;
-
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Implementation of {@link NaturalIdRegionAccessStrategy} that handles {@link NonStopCacheException} using
  * {@link HibernateNonstopCacheExceptionHandler}
  *
  * @author Abhishek Sanoujam
  * @author Alex Snaps
  */
 public class NonstopAwareNaturalIdRegionAccessStrategy implements NaturalIdRegionAccessStrategy {
 	private final NaturalIdRegionAccessStrategy actualStrategy;
 	private final HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler;
 
 	/**
 	 * Constructor accepting the actual {@link NaturalIdRegionAccessStrategy} and the {@link HibernateNonstopCacheExceptionHandler}
 	 *
 	 * @param actualStrategy The wrapped NaturalIdRegionAccessStrategy
 	 * @param hibernateNonstopExceptionHandler The exception handler
 	 */
 	public NonstopAwareNaturalIdRegionAccessStrategy(
 			NaturalIdRegionAccessStrategy actualStrategy,
 			HibernateNonstopCacheExceptionHandler hibernateNonstopExceptionHandler) {
 		this.actualStrategy = actualStrategy;
 		this.hibernateNonstopExceptionHandler = hibernateNonstopExceptionHandler;
 	}
 
 	@Override
-	public boolean insert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean insert(Object key, Object value) throws CacheException {
 		try {
 			return actualStrategy.insert( key, value );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
-	public boolean afterInsert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean afterInsert(Object key, Object value) throws CacheException {
 		try {
 			return actualStrategy.afterInsert( key, value );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
-	public boolean update(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean update(Object key, Object value) throws CacheException {
 		try {
 			return actualStrategy.update( key, value );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
-	public boolean afterUpdate(NaturalIdCacheKey key, Object value, SoftLock lock) throws CacheException {
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
 		try {
 			return actualStrategy.afterUpdate( key, value, lock );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
 	public NaturalIdRegion getRegion() {
 		return actualStrategy.getRegion();
 	}
 
 	@Override
-	public void evict(NaturalIdCacheKey key) throws CacheException {
+	public void evict(Object key) throws CacheException {
 		try {
 			actualStrategy.evict( key );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
 	public void evictAll() throws CacheException {
 		try {
 			actualStrategy.evictAll();
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
-	public Object get(NaturalIdCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		try {
 			return actualStrategy.get( key, txTimestamp );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return null;
 		}
 	}
 
 	@Override
-	public SoftLock lockItem(NaturalIdCacheKey key, Object version) throws CacheException {
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		try {
 			return actualStrategy.lockItem( key, version );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return null;
 		}
 	}
 
 	@Override
 	public SoftLock lockRegion() throws CacheException {
 		try {
 			return actualStrategy.lockRegion();
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return null;
 		}
 	}
 
 	@Override
-	public boolean putFromLoad(NaturalIdCacheKey key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		try {
 			return actualStrategy.putFromLoad( key, value, txTimestamp, version, minimalPutOverride );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
-	public boolean putFromLoad(NaturalIdCacheKey key, Object value, long txTimestamp, Object version) throws CacheException {
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
 		try {
 			return actualStrategy.putFromLoad( key, value, txTimestamp, version );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 			return false;
 		}
 	}
 
 	@Override
-	public void remove(NaturalIdCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		try {
 			actualStrategy.remove( key );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
 	public void removeAll() throws CacheException {
 		try {
 			actualStrategy.removeAll();
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
-	public void unlockItem(NaturalIdCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		try {
 			actualStrategy.unlockItem( key, lock );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
 	public void unlockRegion(SoftLock lock) throws CacheException {
 		try {
 			actualStrategy.unlockRegion( lock );
 		}
 		catch (NonStopCacheException nonStopCacheException) {
 			hibernateNonstopExceptionHandler.handleNonstopCacheException( nonStopCacheException );
 		}
 	}
 
 	@Override
-	public NaturalIdCacheKey generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
+	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
 		return DefaultCacheKeysFactory.createNaturalIdKey( naturalIdValues, persister, session );
 	}
 
+	@Override
+	public Object[] getNaturalIdValues(Object cacheKey) {
+		return DefaultCacheKeysFactory.getNaturalIdValues(cacheKey);
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/AbstractEhcacheAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/AbstractEhcacheAccessStrategy.java
index 8ec4bda1a2..63ed4faf24 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/AbstractEhcacheAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/AbstractEhcacheAccessStrategy.java
@@ -1,160 +1,137 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
-import java.io.Serializable;
-
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheTransactionalDataRegion;
-import org.hibernate.cache.internal.DefaultCacheKeysFactory;
-import org.hibernate.cache.spi.CacheKey;
-import org.hibernate.cache.spi.CollectionCacheKey;
-import org.hibernate.cache.spi.EntityCacheKey;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.persister.collection.CollectionPersister;
-import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Ultimate superclass for all Ehcache specific Hibernate AccessStrategy implementations.
  *
  * @param <T> type of the enclosed region
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
-abstract class AbstractEhcacheAccessStrategy<T extends EhcacheTransactionalDataRegion, K extends CacheKey> {
+abstract class AbstractEhcacheAccessStrategy<T extends EhcacheTransactionalDataRegion> {
 	private final T region;
 	private final SessionFactoryOptions settings;
 
 	/**
 	 * Create an access strategy wrapping the given region.
 	 *
 	 * @param region The wrapped region.  Accessible to subclasses via {@link #region()}
 	 * @param settings The Hibernate settings.  Accessible to subclasses via {@link #settings()}
 	 */
 	AbstractEhcacheAccessStrategy(T region, SessionFactoryOptions settings) {
 		this.region = region;
 		this.settings = settings;
 	}
 
 	/**
 	 * The wrapped Hibernate cache region.
 	 */
 	protected T region() {
 		return region;
 	}
 
 	/**
 	 * The settings for this persistence unit.
 	 */
 	protected SessionFactoryOptions settings() {
 		return settings;
 	}
 
 	/**
 	 * This method is a placeholder for method signatures supplied by interfaces pulled in further down the class
 	 * hierarchy.
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object)
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object)
 	 */
-	public final boolean putFromLoad(K key, Object value, long txTimestamp, Object version) throws CacheException {
+	public final boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
 		return putFromLoad( key, value, txTimestamp, version, settings.isMinimalPutsEnabled() );
 	}
 
 	/**
 	 * This method is a placeholder for method signatures supplied by interfaces pulled in further down the class
 	 * hierarchy.
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object, boolean)
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object, boolean)
 	 */
-	public abstract boolean putFromLoad(K key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public abstract boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException;
 
 	/**
 	 * Region locks are not supported.
 	 *
 	 * @return <code>null</code>
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#lockRegion()
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#lockRegion()
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public final SoftLock lockRegion() {
 		return null;
 	}
 
 	/**
 	 * Region locks are not supported - perform a cache clear as a precaution.
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#unlockRegion(org.hibernate.cache.spi.access.SoftLock)
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#unlockRegion(org.hibernate.cache.spi.access.SoftLock)
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public final void unlockRegion(SoftLock lock) throws CacheException {
 		region.clear();
 	}
 
 	/**
 	 * A no-op since this is an asynchronous cache access strategy.
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#remove(java.lang.Object)
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#remove(java.lang.Object)
 	 */
-	public void remove(K key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 	}
 
 	/**
 	 * Called to evict data from the entire region
 	 *
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#removeAll()
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#removeAll()
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public final void removeAll() throws CacheException {
 		region.clear();
 	}
 
 	/**
 	 * Remove the given mapping without regard to transactional safety
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#evict(java.lang.Object)
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#evict(java.lang.Object)
 	 */
-	public final void evict(K key) throws CacheException {
+	public final void evict(Object key) throws CacheException {
 		region.remove( key );
 	}
 
 	/**
 	 * Remove all mappings without regard to transactional safety
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#evictAll()
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#evictAll()
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public final void evictAll() throws CacheException {
 		region.clear();
 	}
-
-	public CollectionCacheKey generateCacheKey(Serializable id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
-		return DefaultCacheKeysFactory.createCollectionKey( id, persister, factory, tenantIdentifier );
-	}
-
-	public EntityCacheKey generateCacheKey(Serializable id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
-		return DefaultCacheKeysFactory.createEntityKey( id, persister, factory, tenantIdentifier );
-	}
-
-	public NaturalIdCacheKey generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
-		return DefaultCacheKeysFactory.createNaturalIdKey( naturalIdValues, persister, session );
-	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/AbstractReadWriteEhcacheAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/AbstractReadWriteEhcacheAccessStrategy.java
index 81b6324676..192647e3ef 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/AbstractReadWriteEhcacheAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/AbstractReadWriteEhcacheAccessStrategy.java
@@ -1,386 +1,385 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.UUID;
 import java.util.concurrent.atomic.AtomicLong;
 
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.EhCacheMessageLogger;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheTransactionalDataRegion;
-import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.jboss.logging.Logger;
 
 /**
  * Superclass for all Ehcache specific read/write AccessStrategy implementations.
  *
  * @param <T> the type of the enclosed cache region
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
-abstract class AbstractReadWriteEhcacheAccessStrategy<T extends EhcacheTransactionalDataRegion, K extends CacheKey>
-		extends AbstractEhcacheAccessStrategy<T, K> {
+abstract class AbstractReadWriteEhcacheAccessStrategy<T extends EhcacheTransactionalDataRegion>
+		extends AbstractEhcacheAccessStrategy<T> {
 
 	private static final EhCacheMessageLogger LOG = Logger.getMessageLogger(
 			EhCacheMessageLogger.class,
 			AbstractReadWriteEhcacheAccessStrategy.class.getName()
 	);
 
 	private final UUID uuid = UUID.randomUUID();
 	private final AtomicLong nextLockId = new AtomicLong();
 
 	private final Comparator versionComparator;
 
 	/**
 	 * Creates a read/write cache access strategy around the given cache region.
 	 */
 	public AbstractReadWriteEhcacheAccessStrategy(T region, SessionFactoryOptions settings) {
 		super( region, settings );
 		this.versionComparator = region.getCacheDataDescription().getVersionComparator();
 	}
 
 	/**
 	 * Returns <code>null</code> if the item is not readable.  Locked items are not readable, nor are items created
 	 * after the start of this transaction.
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#get(java.lang.Object, long)
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#get(java.lang.Object, long)
 	 */
-	public final Object get(K key, long txTimestamp) throws CacheException {
+	public final Object get(Object key, long txTimestamp) throws CacheException {
 		readLockIfNeeded( key );
 		try {
 			final Lockable item = (Lockable) region().get( key );
 
 			final boolean readable = item != null && item.isReadable( txTimestamp );
 			if ( readable ) {
 				return item.getValue();
 			}
 			else {
 				return null;
 			}
 		}
 		finally {
 			readUnlockIfNeeded( key );
 		}
 	}
 
 	/**
 	 * Returns <code>false</code> and fails to put the value if there is an existing un-writeable item mapped to this
 	 * key.
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object, boolean)
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#putFromLoad(java.lang.Object, java.lang.Object, long, java.lang.Object, boolean)
 	 */
 	@Override
 	public final boolean putFromLoad(
-			K key,
+			Object key,
 			Object value,
 			long txTimestamp,
 			Object version,
 			boolean minimalPutOverride)
 			throws CacheException {
 		region().writeLock( key );
 		try {
 			final Lockable item = (Lockable) region().get( key );
 			final boolean writeable = item == null || item.isWriteable( txTimestamp, version, versionComparator );
 			if ( writeable ) {
 				region().put( key, new Item( value, version, region().nextTimestamp() ) );
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		finally {
 			region().writeUnlock( key );
 		}
 	}
 
 	/**
 	 * Soft-lock a cache item.
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#lockItem(java.lang.Object, java.lang.Object)
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#lockItem(java.lang.Object, java.lang.Object)
 	 */
-	public final SoftLock lockItem(K key, Object version) throws CacheException {
+	public final SoftLock lockItem(Object key, Object version) throws CacheException {
 		region().writeLock( key );
 		try {
 			final Lockable item = (Lockable) region().get( key );
 			final long timeout = region().nextTimestamp() + region().getTimeout();
 			final Lock lock = (item == null) ? new Lock( timeout, uuid, nextLockId(), version ) : item.lock(
 					timeout,
 					uuid,
 					nextLockId()
 			);
 			region().put( key, lock );
 			return lock;
 		}
 		finally {
 			region().writeUnlock( key );
 		}
 	}
 
 	/**
 	 * Soft-unlock a cache item.
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#unlockItem(java.lang.Object, org.hibernate.cache.spi.access.SoftLock)
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#unlockItem(java.lang.Object, org.hibernate.cache.spi.access.SoftLock)
 	 */
-	public final void unlockItem(K key, SoftLock lock) throws CacheException {
+	public final void unlockItem(Object key, SoftLock lock) throws CacheException {
 		region().writeLock( key );
 		try {
 			final Lockable item = (Lockable) region().get( key );
 
 			if ( (item != null) && item.isUnlockable( lock ) ) {
 				decrementLock( key, (Lock) item );
 			}
 			else {
 				handleLockExpiry( key, item );
 			}
 		}
 		finally {
 			region().writeUnlock( key );
 		}
 	}
 
 	private long nextLockId() {
 		return nextLockId.getAndIncrement();
 	}
 
 	/**
 	 * Unlock and re-put the given key, lock combination.
 	 */
 	protected void decrementLock(Object key, Lock lock) {
 		lock.unlock( region().nextTimestamp() );
 		region().put( key, lock );
 	}
 
 	/**
 	 * Handle the timeout of a previous lock mapped to this key
 	 */
 	protected void handleLockExpiry(Object key, Lockable lock) {
 		LOG.softLockedCacheExpired( region().getName(), key, lock == null ? "(null)" : lock.toString() );
 
 		final long ts = region().nextTimestamp() + region().getTimeout();
 		// create new lock that times out immediately
 		final Lock newLock = new Lock( ts, uuid, nextLockId.getAndIncrement(), null );
 		newLock.unlock( ts );
 		region().put( key, newLock );
 	}
 
 	/**
 	 * Read lock the entry for the given key if internal cache locks will not provide correct exclusion.
 	 */
 	private void readLockIfNeeded(Object key) {
 		if ( region().locksAreIndependentOfCache() ) {
 			region().readLock( key );
 		}
 	}
 
 	/**
 	 * Read unlock the entry for the given key if internal cache locks will not provide correct exclusion.
 	 */
 	private void readUnlockIfNeeded(Object key) {
 		if ( region().locksAreIndependentOfCache() ) {
 			region().readUnlock( key );
 		}
 	}
 
 	/**
 	 * Interface type implemented by all wrapper objects in the cache.
 	 */
 	protected static interface Lockable {
 
 		/**
 		 * Returns <code>true</code> if the enclosed value can be read by a transaction started at the given time.
 		 */
 		public boolean isReadable(long txTimestamp);
 
 		/**
 		 * Returns <code>true</code> if the enclosed value can be replaced with one of the given version by a
 		 * transaction started at the given time.
 		 */
 		public boolean isWriteable(long txTimestamp, Object version, Comparator versionComparator);
 
 		/**
 		 * Returns the enclosed value.
 		 */
 		public Object getValue();
 
 		/**
 		 * Returns <code>true</code> if the given lock can be unlocked using the given SoftLock instance as a handle.
 		 */
 		public boolean isUnlockable(SoftLock lock);
 
 		/**
 		 * Locks this entry, stamping it with the UUID and lockId given, with the lock timeout occuring at the specified
 		 * time.  The returned Lock object can be used to unlock the entry in the future.
 		 */
 		public Lock lock(long timeout, UUID uuid, long lockId);
 	}
 
 	/**
 	 * Wrapper type representing unlocked items.
 	 */
 	protected static final class Item implements Serializable, Lockable {
 		private static final long serialVersionUID = 1L;
 		private final Object value;
 		private final Object version;
 		private final long timestamp;
 
 		/**
 		 * Creates an unlocked item wrapping the given value with a version and creation timestamp.
 		 */
 		Item(Object value, Object version, long timestamp) {
 			this.value = value;
 			this.version = version;
 			this.timestamp = timestamp;
 		}
 
 		@Override
 		public boolean isReadable(long txTimestamp) {
 			return txTimestamp > timestamp;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
 			return version != null && versionComparator.compare( version, newVersion ) < 0;
 		}
 
 		@Override
 		public Object getValue() {
 			return value;
 		}
 
 		@Override
 		public boolean isUnlockable(SoftLock lock) {
 			return false;
 		}
 
 		@Override
 		public Lock lock(long timeout, UUID uuid, long lockId) {
 			return new Lock( timeout, uuid, lockId, version );
 		}
 	}
 
 	/**
 	 * Wrapper type representing locked items.
 	 */
 	protected static final class Lock implements Serializable, Lockable, SoftLock {
 		private static final long serialVersionUID = 2L;
 
 		private final UUID sourceUuid;
 		private final long lockId;
 		private final Object version;
 
 		private long timeout;
 		private boolean concurrent;
 		private int multiplicity = 1;
 		private long unlockTimestamp;
 
 		/**
 		 * Creates a locked item with the given identifiers and object version.
 		 */
 		Lock(long timeout, UUID sourceUuid, long lockId, Object version) {
 			this.timeout = timeout;
 			this.lockId = lockId;
 			this.version = version;
 			this.sourceUuid = sourceUuid;
 		}
 
 		@Override
 		public boolean isReadable(long txTimestamp) {
 			return false;
 		}
 
 		@Override
 		@SuppressWarnings({"SimplifiableIfStatement", "unchecked"})
 		public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
 			if ( txTimestamp > timeout ) {
 				// if timedout then allow write
 				return true;
 			}
 			if ( multiplicity > 0 ) {
 				// if still locked then disallow write
 				return false;
 			}
 			return version == null
 					? txTimestamp > unlockTimestamp
 					: versionComparator.compare( version, newVersion ) < 0;
 		}
 
 		@Override
 		public Object getValue() {
 			return null;
 		}
 
 		@Override
 		public boolean isUnlockable(SoftLock lock) {
 			return equals( lock );
 		}
 
 		@Override
 		@SuppressWarnings("SimplifiableIfStatement")
 		public boolean equals(Object o) {
 			if ( o == this ) {
 				return true;
 			}
 			else if ( o instanceof Lock ) {
 				return (lockId == ((Lock) o).lockId) && sourceUuid.equals( ((Lock) o).sourceUuid );
 			}
 			else {
 				return false;
 			}
 		}
 
 		@Override
 		public int hashCode() {
 			final int hash = (sourceUuid != null ? sourceUuid.hashCode() : 0);
 			int temp = (int) lockId;
 			for ( int i = 1; i < Long.SIZE / Integer.SIZE; i++ ) {
 				temp ^= (lockId >>> (i * Integer.SIZE));
 			}
 			return hash + temp;
 		}
 
 		/**
 		 * Returns true if this Lock has been concurrently locked by more than one transaction.
 		 */
 		public boolean wasLockedConcurrently() {
 			return concurrent;
 		}
 
 		@Override
 		public Lock lock(long timeout, UUID uuid, long lockId) {
 			concurrent = true;
 			multiplicity++;
 			this.timeout = timeout;
 			return this;
 		}
 
 		/**
 		 * Unlocks this Lock, and timestamps the unlock event.
 		 */
 		public void unlock(long timestamp) {
 			if ( --multiplicity == 0 ) {
 				unlockTimestamp = timestamp;
 			}
 		}
 
 		@Override
 		public String toString() {
 			return "Lock Source-UUID:" + sourceUuid + " Lock-ID:" + lockId;
 		}
 	}
 }
 
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheCollectionRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheCollectionRegionAccessStrategy.java
index be0854bbf1..b7278bad9d 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheCollectionRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheCollectionRegionAccessStrategy.java
@@ -1,83 +1,95 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheCollectionRegion;
-import org.hibernate.cache.spi.CollectionCacheKey;
+import org.hibernate.cache.internal.DefaultCacheKeysFactory;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * Ehcache specific non-strict read/write collection region access strategy
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
 public class NonStrictReadWriteEhcacheCollectionRegionAccessStrategy
-		extends AbstractEhcacheAccessStrategy<EhcacheCollectionRegion,CollectionCacheKey>
+		extends AbstractEhcacheAccessStrategy<EhcacheCollectionRegion>
 		implements CollectionRegionAccessStrategy {
 
 	/**
 	 * Create a non-strict read/write access strategy accessing the given collection region.
 	 *
 	 * @param region The wrapped region
 	 * @param settings The Hibernate settings
 	 */
 	public NonStrictReadWriteEhcacheCollectionRegionAccessStrategy(EhcacheCollectionRegion region, SessionFactoryOptions settings) {
 		super( region, settings );
 	}
 
 	@Override
 	public CollectionRegion getRegion() {
 		return region();
 	}
 
 	@Override
-	public Object get(CollectionCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		return region().get( key );
 	}
 
 	@Override
-	public boolean putFromLoad(CollectionCacheKey key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		if ( minimalPutOverride && region().contains( key ) ) {
 			return false;
 		}
 		else {
 			region().put( key, value );
 			return true;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Since this is a non-strict read/write strategy item locking is not used.
 	 */
 	@Override
-	public SoftLock lockItem(CollectionCacheKey key, Object version) throws CacheException {
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		return null;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Since this is a non-strict read/write strategy item locking is not used.
 	 */
 	@Override
-	public void unlockItem(CollectionCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		region().remove( key );
 	}
 
 	@Override
-	public void remove(CollectionCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		region().remove( key );
 	}
+
+	@Override
+	public Object generateCacheKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+		return DefaultCacheKeysFactory.createCollectionKey( id, persister, factory, tenantIdentifier );
+	}
+
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getCollectionId(cacheKey);
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheEntityRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheEntityRegionAccessStrategy.java
index e68abd03c3..54e5af723a 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheEntityRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheEntityRegionAccessStrategy.java
@@ -1,127 +1,134 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
-import java.io.Serializable;
-
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheEntityRegion;
-import org.hibernate.cache.spi.EntityCacheKey;
+import org.hibernate.cache.internal.DefaultCacheKeysFactory;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Ehcache specific non-strict read/write entity region access strategy
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
 public class NonStrictReadWriteEhcacheEntityRegionAccessStrategy
-		extends AbstractEhcacheAccessStrategy<EhcacheEntityRegion,EntityCacheKey>
+		extends AbstractEhcacheAccessStrategy<EhcacheEntityRegion>
 		implements EntityRegionAccessStrategy {
 
 	/**
 	 * Create a non-strict read/write access strategy accessing the given collection region.
 	 *
 	 * @param region The wrapped region
 	 * @param settings The Hibernate settings
 	 */
 	public NonStrictReadWriteEhcacheEntityRegionAccessStrategy(EhcacheEntityRegion region, SessionFactoryOptions settings) {
 		super( region, settings );
 	}
 
 	@Override
 	public EntityRegion getRegion() {
 		return super.region();
 	}
 
 	@Override
-	public Object get(EntityCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		return region().get( key );
 	}
 
 	@Override
-	public boolean putFromLoad(EntityCacheKey key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		if ( minimalPutOverride && region().contains( key ) ) {
 			return false;
 		}
 		else {
 			region().put( key, value );
 			return true;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Since this is a non-strict read/write strategy item locking is not used.
 	 */
 	@Override
-	public SoftLock lockItem(EntityCacheKey key, Object version) throws CacheException {
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		return null;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Since this is a non-strict read/write strategy item locking is not used.
 	 */
 	@Override
-	public void unlockItem(EntityCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		region().remove( key );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Returns <code>false</code> since this is an asynchronous cache access strategy.
 	 */
 	@Override
-	public boolean insert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean insert(Object key, Object value, Object version) throws CacheException {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Returns <code>false</code> since this is a non-strict read/write cache access strategy
 	 */
 	@Override
-	public boolean afterInsert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Removes the entry since this is a non-strict read/write cache strategy.
 	 */
 	@Override
-	public boolean update(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion)
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
 			throws CacheException {
 		remove( key );
 		return false;
 	}
 
 	@Override
-	public boolean afterUpdate(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
 			throws CacheException {
 		unlockItem( key, lock );
 		return false;
 	}
 
 	@Override
-	public void remove(EntityCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		region().remove( key );
 	}
 
+	@Override
+	public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+		return DefaultCacheKeysFactory.createEntityKey( id, persister, factory, tenantIdentifier );
+	}
+
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getEntityId(cacheKey);
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheNaturalIdRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheNaturalIdRegionAccessStrategy.java
index 5537087253..2999c3d3a3 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheNaturalIdRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/NonStrictReadWriteEhcacheNaturalIdRegionAccessStrategy.java
@@ -1,123 +1,132 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheNaturalIdRegion;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
+import org.hibernate.cache.internal.DefaultCacheKeysFactory;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Ehcache specific non-strict read/write NaturalId region access strategy
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
 public class NonStrictReadWriteEhcacheNaturalIdRegionAccessStrategy
-		extends AbstractEhcacheAccessStrategy<EhcacheNaturalIdRegion,NaturalIdCacheKey>
+		extends AbstractEhcacheAccessStrategy<EhcacheNaturalIdRegion>
 		implements NaturalIdRegionAccessStrategy {
 
 	/**
 	 * Create a non-strict read/write access strategy accessing the given NaturalId region.
 	 *
 	 * @param region The wrapped region
 	 * @param settings The Hibernate settings
 	 */
 	public NonStrictReadWriteEhcacheNaturalIdRegionAccessStrategy(EhcacheNaturalIdRegion region, SessionFactoryOptions settings) {
 		super( region, settings );
 	}
 
 	@Override
 	public NaturalIdRegion getRegion() {
 		return region();
 	}
 
 	@Override
-	public Object get(NaturalIdCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		return region().get( key );
 	}
 
 	@Override
-	public boolean putFromLoad(NaturalIdCacheKey key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		if ( minimalPutOverride && region().contains( key ) ) {
 			return false;
 		}
 		else {
 			region().put( key, value );
 			return true;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Since this is a non-strict read/write strategy item locking is not used.
 	 */
 	@Override
-	public SoftLock lockItem(NaturalIdCacheKey key, Object version) throws CacheException {
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		return null;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Since this is a non-strict read/write strategy item locking is not used.
 	 */
 	@Override
-	public void unlockItem(NaturalIdCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		region().remove( key );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Returns <code>false</code> since this is an asynchronous cache access strategy.
 	 */
 	@Override
-	public boolean insert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean insert(Object key, Object value) throws CacheException {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Returns <code>false</code> since this is a non-strict read/write cache access strategy
 	 */
 	@Override
-	public boolean afterInsert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean afterInsert(Object key, Object value) throws CacheException {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Removes the entry since this is a non-strict read/write cache strategy.
 	 */
 	@Override
-	public boolean update(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean update(Object key, Object value) throws CacheException {
 		remove( key );
 		return false;
 	}
 
 	@Override
-	public boolean afterUpdate(NaturalIdCacheKey key, Object value, SoftLock lock) throws CacheException {
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
 		unlockItem( key, lock );
 		return false;
 	}
 
 	@Override
-	public void remove(NaturalIdCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		region().remove( key );
 	}
 
+ 	@Override
+	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
+		return DefaultCacheKeysFactory.createNaturalIdKey(naturalIdValues, persister, session);
+	}
+
+	@Override
+	public Object[] getNaturalIdValues(Object cacheKey) {
+		return DefaultCacheKeysFactory.getNaturalIdValues(cacheKey);
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheCollectionRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheCollectionRegionAccessStrategy.java
index 0b048b24fd..7de3b272ed 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheCollectionRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheCollectionRegionAccessStrategy.java
@@ -1,72 +1,84 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheCollectionRegion;
-import org.hibernate.cache.spi.CollectionCacheKey;
+import org.hibernate.cache.internal.DefaultCacheKeysFactory;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * Ehcache specific read-only collection region access strategy
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
 public class ReadOnlyEhcacheCollectionRegionAccessStrategy
-		extends AbstractEhcacheAccessStrategy<EhcacheCollectionRegion,CollectionCacheKey>
+		extends AbstractEhcacheAccessStrategy<EhcacheCollectionRegion>
 		implements CollectionRegionAccessStrategy {
 
 	/**
 	 * Create a read-only access strategy accessing the given collection region.
 	 *
 	 * @param region The wrapped region
 	 * @param settings The Hibernate settings
 	 */
 	public ReadOnlyEhcacheCollectionRegionAccessStrategy(EhcacheCollectionRegion region, SessionFactoryOptions settings) {
 		super( region, settings );
 	}
 
 	@Override
 	public CollectionRegion getRegion() {
 		return region();
 	}
 
 	@Override
-	public Object get(CollectionCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		return region().get( key );
 	}
 
 	@Override
-	public boolean putFromLoad(CollectionCacheKey key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		if ( minimalPutOverride && region().contains( key ) ) {
 			return false;
 		}
 		else {
 			region().put( key, value );
 			return true;
 		}
 	}
 
 	@Override
-	public SoftLock lockItem(CollectionCacheKey key, Object version) throws UnsupportedOperationException {
+	public SoftLock lockItem(Object key, Object version) throws UnsupportedOperationException {
 		return null;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * A no-op since this cache is read-only
 	 */
 	@Override
-	public void unlockItem(CollectionCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
+	}
+
+	@Override
+	public Object generateCacheKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+		return DefaultCacheKeysFactory.createCollectionKey( id, persister, factory, tenantIdentifier );
+	}
+
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getCollectionId(cacheKey);
 	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheEntityRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheEntityRegionAccessStrategy.java
index a09bbafb30..43d16d3f70 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheEntityRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheEntityRegionAccessStrategy.java
@@ -1,114 +1,126 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheEntityRegion;
-import org.hibernate.cache.spi.EntityCacheKey;
+import org.hibernate.cache.internal.DefaultCacheKeysFactory;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Ehcache specific read-only entity region access strategy
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
-public class ReadOnlyEhcacheEntityRegionAccessStrategy extends AbstractEhcacheAccessStrategy<EhcacheEntityRegion,EntityCacheKey>
+public class ReadOnlyEhcacheEntityRegionAccessStrategy extends AbstractEhcacheAccessStrategy<EhcacheEntityRegion>
 		implements EntityRegionAccessStrategy {
 
 	/**
 	 * Create a read-only access strategy accessing the given entity region.
 	 *
 	 * @param region The wrapped region
 	 * @param settings The Hibernate settings
 	 */
 	public ReadOnlyEhcacheEntityRegionAccessStrategy(EhcacheEntityRegion region, SessionFactoryOptions settings) {
 		super( region, settings );
 	}
 
 	@Override
 	public EntityRegion getRegion() {
 		return region();
 	}
 
 	@Override
-	public Object get(EntityCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		return region().get( key );
 	}
 
 	@Override
-	public boolean putFromLoad(EntityCacheKey key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		if ( minimalPutOverride && region().contains( key ) ) {
 			return false;
 		}
 		else {
 			region().put( key, value );
 			return true;
 		}
 	}
 
 	@Override
-	public SoftLock lockItem(EntityCacheKey key, Object version) throws UnsupportedOperationException {
+	public SoftLock lockItem(Object key, Object version) throws UnsupportedOperationException {
 		return null;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * A no-op since this cache is read-only
 	 */
 	@Override
-	public void unlockItem(EntityCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		evict( key );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * This cache is asynchronous hence a no-op
 	 */
 	@Override
-	public boolean insert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean insert(Object key, Object value, Object version) throws CacheException {
 		return false;
 	}
 
 	@Override
-	public boolean afterInsert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
 		region().put( key, value );
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Throws UnsupportedOperationException since this cache is read-only
 	 *
 	 * @throws UnsupportedOperationException always
 	 */
 	@Override
-	public boolean update(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion)
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
 			throws UnsupportedOperationException {
 		throw new UnsupportedOperationException( "Can't write to a readonly object" );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Throws UnsupportedOperationException since this cache is read-only
 	 *
 	 * @throws UnsupportedOperationException always
 	 */
 	@Override
-	public boolean afterUpdate(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
 			throws UnsupportedOperationException {
 		throw new UnsupportedOperationException( "Can't write to a readonly object" );
 	}
+
+	@Override
+	public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+		return DefaultCacheKeysFactory.createEntityKey( id, persister, factory, tenantIdentifier );
+	}
+
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getEntityId(cacheKey);
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheNaturalIdRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheNaturalIdRegionAccessStrategy.java
index 0a0e26cfff..35926666b9 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheNaturalIdRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadOnlyEhcacheNaturalIdRegionAccessStrategy.java
@@ -1,113 +1,125 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheNaturalIdRegion;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
+import org.hibernate.cache.internal.DefaultCacheKeysFactory;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Ehcache specific read-only NaturalId region access strategy
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
 public class ReadOnlyEhcacheNaturalIdRegionAccessStrategy
-		extends AbstractEhcacheAccessStrategy<EhcacheNaturalIdRegion,NaturalIdCacheKey>
+		extends AbstractEhcacheAccessStrategy<EhcacheNaturalIdRegion>
 		implements NaturalIdRegionAccessStrategy {
 
 	/**
 	 * Create a read-only access strategy accessing the given NaturalId region.
 	 *
 	 * @param region THe wrapped region
 	 * @param settings The Hibermate settings
 	 */
 	public ReadOnlyEhcacheNaturalIdRegionAccessStrategy(EhcacheNaturalIdRegion region, SessionFactoryOptions settings) {
 		super( region, settings );
 	}
 
 	@Override
 	public NaturalIdRegion getRegion() {
 		return region();
 	}
 
 	@Override
-	public Object get(NaturalIdCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		return region().get( key );
 	}
 
 	@Override
-	public boolean putFromLoad(NaturalIdCacheKey key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		if ( minimalPutOverride && region().contains( key ) ) {
 			return false;
 		}
 		else {
 			region().put( key, value );
 			return true;
 		}
 	}
 
 	@Override
-	public SoftLock lockItem(NaturalIdCacheKey key, Object version) throws UnsupportedOperationException {
+	public SoftLock lockItem(Object key, Object version) throws UnsupportedOperationException {
 		return null;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * A no-op since this cache is read-only
 	 */
 	@Override
-	public void unlockItem(NaturalIdCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		region().remove( key );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * This cache is asynchronous hence a no-op
 	 */
 	@Override
-	public boolean insert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean insert(Object key, Object value) throws CacheException {
 		return false;
 	}
 
 	@Override
-	public boolean afterInsert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean afterInsert(Object key, Object value) throws CacheException {
 		region().put( key, value );
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Throws UnsupportedOperationException since this cache is read-only
 	 *
 	 * @throws UnsupportedOperationException always
 	 */
 	@Override
-	public boolean update(NaturalIdCacheKey key, Object value) throws UnsupportedOperationException {
+	public boolean update(Object key, Object value) throws UnsupportedOperationException {
 		throw new UnsupportedOperationException( "Can't write to a readonly object" );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Throws UnsupportedOperationException since this cache is read-only
 	 *
 	 * @throws UnsupportedOperationException always
 	 */
 	@Override
-	public boolean afterUpdate(NaturalIdCacheKey key, Object value, SoftLock lock) throws UnsupportedOperationException {
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws UnsupportedOperationException {
 		throw new UnsupportedOperationException( "Can't write to a readonly object" );
 	}
+
+	@Override
+	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
+		return DefaultCacheKeysFactory.createNaturalIdKey(naturalIdValues, persister, session);
+	}
+
+	@Override
+	public Object[] getNaturalIdValues(Object cacheKey) {
+		return DefaultCacheKeysFactory.getNaturalIdValues(cacheKey);
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheCollectionRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheCollectionRegionAccessStrategy.java
index acd70b5018..2402e372f3 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheCollectionRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheCollectionRegionAccessStrategy.java
@@ -1,39 +1,52 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheCollectionRegion;
-import org.hibernate.cache.spi.CollectionCacheKey;
+import org.hibernate.cache.internal.DefaultCacheKeysFactory;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * Ehcache specific read/write collection region access strategy
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
 public class ReadWriteEhcacheCollectionRegionAccessStrategy
-		extends AbstractReadWriteEhcacheAccessStrategy<EhcacheCollectionRegion,CollectionCacheKey>
+		extends AbstractReadWriteEhcacheAccessStrategy<EhcacheCollectionRegion>
 		implements CollectionRegionAccessStrategy {
 
 	/**
 	 * Create a read/write access strategy accessing the given collection region.
 	 *
 	 * @param region The wrapped region
 	 * @param settings The Hibernate settings
 	 */
 	public ReadWriteEhcacheCollectionRegionAccessStrategy(EhcacheCollectionRegion region, SessionFactoryOptions settings) {
 		super( region, settings );
 	}
 
 	@Override
 	public CollectionRegion getRegion() {
 		return region();
 	}
+
+
+	@Override
+	public Object generateCacheKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+		return DefaultCacheKeysFactory.createCollectionKey( id, persister, factory, tenantIdentifier );
+	}
+
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getCollectionId(cacheKey);
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheEntityRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheEntityRegionAccessStrategy.java
index 7b0a088ba8..a691e2bc9a 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheEntityRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheEntityRegionAccessStrategy.java
@@ -1,121 +1,133 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheEntityRegion;
-import org.hibernate.cache.spi.EntityCacheKey;
+import org.hibernate.cache.internal.DefaultCacheKeysFactory;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Ehcache specific read/write entity region access strategy
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
 public class ReadWriteEhcacheEntityRegionAccessStrategy
-		extends AbstractReadWriteEhcacheAccessStrategy<EhcacheEntityRegion,EntityCacheKey>
+		extends AbstractReadWriteEhcacheAccessStrategy<EhcacheEntityRegion>
 		implements EntityRegionAccessStrategy {
 
 	/**
 	 * Create a read/write access strategy accessing the given entity region.
 	 *
 	 * @param region The wrapped region
 	 * @param settings The Hibernate settings
 	 */
 	public ReadWriteEhcacheEntityRegionAccessStrategy(EhcacheEntityRegion region, SessionFactoryOptions settings) {
 		super( region, settings );
 	}
 
 	@Override
 	public EntityRegion getRegion() {
 		return region();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * A no-op since this is an asynchronous cache access strategy.
 	 */
 	@Override
-	public boolean insert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean insert(Object key, Object value, Object version) throws CacheException {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Inserts will only succeed if there is no existing value mapped to this key.
 	 */
 	@Override
-	public boolean afterInsert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
 		region().writeLock( key );
 		try {
 			final Lockable item = (Lockable) region().get( key );
 			if ( item == null ) {
 				region().put( key, new Item( value, version, region().nextTimestamp() ) );
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		finally {
 			region().writeUnlock( key );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * A no-op since this is an asynchronous cache access strategy.
 	 */
 	@Override
-	public boolean update(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion)
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
 			throws CacheException {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Updates will only succeed if this entry was locked by this transaction and exclusively this transaction for the
 	 * duration of this transaction.  It is important to also note that updates will fail if the soft-lock expired during
 	 * the course of this transaction.
 	 */
 	@Override
-	public boolean afterUpdate(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
 			throws CacheException {
 		//what should we do with previousVersion here?
 		region().writeLock( key );
 		try {
 			final Lockable item = (Lockable) region().get( key );
 
 			if ( item != null && item.isUnlockable( lock ) ) {
 				final Lock lockItem = (Lock) item;
 				if ( lockItem.wasLockedConcurrently() ) {
 					decrementLock( key, lockItem );
 					return false;
 				}
 				else {
 					region().put( key, new Item( value, currentVersion, region().nextTimestamp() ) );
 					return true;
 				}
 			}
 			else {
 				handleLockExpiry( key, item );
 				return false;
 			}
 		}
 		finally {
 			region().writeUnlock( key );
 		}
 	}
+
+	@Override
+	public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+		return DefaultCacheKeysFactory.createEntityKey(id, persister, factory, tenantIdentifier);
+	}
+
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getEntityId(cacheKey);
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheNaturalIdRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheNaturalIdRegionAccessStrategy.java
index 86629de1da..61fcb06a7d 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheNaturalIdRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/ReadWriteEhcacheNaturalIdRegionAccessStrategy.java
@@ -1,119 +1,131 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheNaturalIdRegion;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
+import org.hibernate.cache.internal.DefaultCacheKeysFactory;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Ehcache specific read/write NaturalId region access strategy
  *
  * @author Chris Dennis
  * @author Alex Snaps
  */
 public class ReadWriteEhcacheNaturalIdRegionAccessStrategy
-		extends AbstractReadWriteEhcacheAccessStrategy<EhcacheNaturalIdRegion,NaturalIdCacheKey>
+		extends AbstractReadWriteEhcacheAccessStrategy<EhcacheNaturalIdRegion>
 		implements NaturalIdRegionAccessStrategy {
 
 	/**
 	 * Create a read/write access strategy accessing the given NaturalId region.
 	 *
 	 * @param region The wrapped region
 	 * @param settings The Hibernate settings
 	 */
 	public ReadWriteEhcacheNaturalIdRegionAccessStrategy(EhcacheNaturalIdRegion region, SessionFactoryOptions settings) {
 		super( region, settings );
 	}
 
 	@Override
 	public NaturalIdRegion getRegion() {
 		return region();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * A no-op since this is an asynchronous cache access strategy.
 	 */
 	@Override
-	public boolean insert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean insert(Object key, Object value) throws CacheException {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Inserts will only succeed if there is no existing value mapped to this key.
 	 */
 	@Override
-	public boolean afterInsert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean afterInsert(Object key, Object value) throws CacheException {
 		region().writeLock( key );
 		try {
 			final Lockable item = (Lockable) region().get( key );
 			if ( item == null ) {
 				region().put( key, new Item( value, null, region().nextTimestamp() ) );
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		finally {
 			region().writeUnlock( key );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * A no-op since this is an asynchronous cache access strategy.
 	 */
 	@Override
-	public boolean update(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean update(Object key, Object value) throws CacheException {
 		return false;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * Updates will only succeed if this entry was locked by this transaction and exclusively this transaction for the
 	 * duration of this transaction.  It is important to also note that updates will fail if the soft-lock expired during
 	 * the course of this transaction.
 	 */
 	@Override
-	public boolean afterUpdate(NaturalIdCacheKey key, Object value, SoftLock lock) throws CacheException {
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
 		//what should we do with previousVersion here?
 		region().writeLock( key );
 		try {
 			final Lockable item = (Lockable) region().get( key );
 
 			if ( item != null && item.isUnlockable( lock ) ) {
 				final Lock lockItem = (Lock) item;
 				if ( lockItem.wasLockedConcurrently() ) {
 					decrementLock( key, lockItem );
 					return false;
 				}
 				else {
 					region().put( key, new Item( value, null, region().nextTimestamp() ) );
 					return true;
 				}
 			}
 			else {
 				handleLockExpiry( key, item );
 				return false;
 			}
 		}
 		finally {
 			region().writeUnlock( key );
 		}
 	}
+
+	@Override
+	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
+		return DefaultCacheKeysFactory.createNaturalIdKey(naturalIdValues, persister, session);
+	}
+
+	@Override
+	public Object[] getNaturalIdValues(Object cacheKey) {
+		return DefaultCacheKeysFactory.getNaturalIdValues(cacheKey);
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheCollectionRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheCollectionRegionAccessStrategy.java
index fa474b770d..c23efe9a94 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheCollectionRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheCollectionRegionAccessStrategy.java
@@ -1,108 +1,114 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
-import java.io.Serializable;
-
 import net.sf.ehcache.Ehcache;
 import net.sf.ehcache.Element;
-
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheCollectionRegion;
-import org.hibernate.cache.spi.CollectionCacheKey;
+import org.hibernate.cache.internal.DefaultCacheKeysFactory;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * JTA CollectionRegionAccessStrategy.
  *
  * @author Chris Dennis
  * @author Ludovic Orban
  * @author Alex Snaps
  */
 public class TransactionalEhcacheCollectionRegionAccessStrategy
-		extends AbstractEhcacheAccessStrategy<EhcacheCollectionRegion,CollectionCacheKey>
+		extends AbstractEhcacheAccessStrategy<EhcacheCollectionRegion>
 		implements CollectionRegionAccessStrategy {
 
 	private final Ehcache ehcache;
 
 	/**
 	 * Construct a new collection region access strategy.
 	 *
 	 * @param region the Hibernate region.
 	 * @param ehcache the cache.
 	 * @param settings the Hibernate settings.
 	 */
 	public TransactionalEhcacheCollectionRegionAccessStrategy(
 			EhcacheCollectionRegion region,
 			Ehcache ehcache,
 			SessionFactoryOptions settings) {
 		super( region, settings );
 		this.ehcache = ehcache;
 	}
 
 	@Override
-	public Object get(CollectionCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		try {
 			final Element element = ehcache.get( key );
 			return element == null ? null : element.getObjectValue();
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
 
 	@Override
 	public CollectionRegion getRegion() {
 		return region();
 	}
 
 	@Override
-	public SoftLock lockItem(CollectionCacheKey key, Object version) throws CacheException {
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		return null;
 	}
 
 	@Override
 	public boolean putFromLoad(
-			CollectionCacheKey key,
+			Object key,
 			Object value,
 			long txTimestamp,
 			Object version,
 			boolean minimalPutOverride) throws CacheException {
 		try {
 			if ( minimalPutOverride && ehcache.get( key ) != null ) {
 				return false;
 			}
 			//OptimisticCache? versioning?
 			ehcache.put( new Element( key, value ) );
 			return true;
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
 
 	@Override
-	public void remove(CollectionCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		try {
 			ehcache.remove( key );
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
 
 	@Override
-	public void unlockItem(CollectionCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		// no-op
 	}
 
+	@Override
+	public Object generateCacheKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+		return DefaultCacheKeysFactory.createCollectionKey( id, persister, factory, tenantIdentifier );
+	}
+
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getCollectionId(cacheKey);
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheEntityRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheEntityRegionAccessStrategy.java
index 9f38d620c8..1cc4a2e504 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheEntityRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheEntityRegionAccessStrategy.java
@@ -1,140 +1,151 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import net.sf.ehcache.Ehcache;
 import net.sf.ehcache.Element;
-
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheEntityRegion;
-import org.hibernate.cache.spi.EntityCacheKey;
+import org.hibernate.cache.internal.DefaultCacheKeysFactory;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * JTA EntityRegionAccessStrategy.
  *
  * @author Chris Dennis
  * @author Ludovic Orban
  * @author Alex Snaps
  */
-public class TransactionalEhcacheEntityRegionAccessStrategy extends AbstractEhcacheAccessStrategy<EhcacheEntityRegion,EntityCacheKey>
+public class TransactionalEhcacheEntityRegionAccessStrategy extends AbstractEhcacheAccessStrategy<EhcacheEntityRegion>
 		implements EntityRegionAccessStrategy {
 
 	private final Ehcache ehcache;
 
 	/**
 	 * Construct a new entity region access strategy.
 	 *
 	 * @param region the Hibernate region.
 	 * @param ehcache the cache.
 	 * @param settings the Hibernate settings.
 	 */
 	public TransactionalEhcacheEntityRegionAccessStrategy(
 			EhcacheEntityRegion region,
 			Ehcache ehcache,
 			SessionFactoryOptions settings) {
 		super( region, settings );
 		this.ehcache = ehcache;
 	}
 
 	@Override
-	public boolean afterInsert(EntityCacheKey key, Object value, Object version) {
+	public boolean afterInsert(Object key, Object value, Object version) {
 		return false;
 	}
 
 	@Override
-	public boolean afterUpdate(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
 		return false;
 	}
 
 	@Override
-	public Object get(EntityCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		try {
 			final Element element = ehcache.get( key );
 			return element == null ? null : element.getObjectValue();
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
 
 	@Override
 	public EntityRegion getRegion() {
 		return region();
 	}
 
 	@Override
-	public boolean insert(EntityCacheKey key, Object value, Object version)
+	public boolean insert(Object key, Object value, Object version)
 			throws CacheException {
 		//OptimisticCache? versioning?
 		try {
 			ehcache.put( new Element( key, value ) );
 			return true;
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
 
 	@Override
-	public SoftLock lockItem(EntityCacheKey key, Object version) throws CacheException {
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		return null;
 	}
 
 	@Override
 	public boolean putFromLoad(
-			EntityCacheKey key,
+			Object key,
 			Object value,
 			long txTimestamp,
 			Object version,
 			boolean minimalPutOverride) throws CacheException {
 		try {
 			if ( minimalPutOverride && ehcache.get( key ) != null ) {
 				return false;
 			}
 			//OptimisticCache? versioning?
 			ehcache.put( new Element( key, value ) );
 			return true;
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
 
 	@Override
-	public void remove(EntityCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		try {
 			ehcache.remove( key );
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
 
 	@Override
-	public void unlockItem(EntityCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		// no-op
 	}
 
 	@Override
 	public boolean update(
-			EntityCacheKey key,
+			Object key,
 			Object value,
 			Object currentVersion,
 			Object previousVersion) throws CacheException {
 		try {
 			ehcache.put( new Element( key, value ) );
 			return true;
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
+
+	@Override
+	public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+		return DefaultCacheKeysFactory.createEntityKey(id, persister, factory, tenantIdentifier);
+	}
+
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getEntityId(cacheKey);
+	}
 }
diff --git a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheNaturalIdRegionAccessStrategy.java b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheNaturalIdRegionAccessStrategy.java
index 64b1606fed..a840c23250 100644
--- a/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheNaturalIdRegionAccessStrategy.java
+++ b/hibernate-ehcache/src/main/java/org/hibernate/cache/ehcache/internal/strategy/TransactionalEhcacheNaturalIdRegionAccessStrategy.java
@@ -1,137 +1,147 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.ehcache.internal.strategy;
 
 import net.sf.ehcache.Ehcache;
 import net.sf.ehcache.Element;
-
 import org.hibernate.boot.spi.SessionFactoryOptions;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.ehcache.internal.regions.EhcacheNaturalIdRegion;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
+import org.hibernate.cache.internal.DefaultCacheKeysFactory;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * JTA NaturalIdRegionAccessStrategy.
  *
  * @author Chris Dennis
  * @author Ludovic Orban
  * @author Alex Snaps
  */
 public class TransactionalEhcacheNaturalIdRegionAccessStrategy
-		extends AbstractEhcacheAccessStrategy<EhcacheNaturalIdRegion,NaturalIdCacheKey>
+		extends AbstractEhcacheAccessStrategy<EhcacheNaturalIdRegion>
 		implements NaturalIdRegionAccessStrategy {
 
 	private final Ehcache ehcache;
 
 	/**
 	 * Construct a new collection region access strategy.
 	 *
 	 * @param region the Hibernate region.
 	 * @param ehcache the cache.
 	 * @param settings the Hibernate settings.
 	 */
 	public TransactionalEhcacheNaturalIdRegionAccessStrategy(
 			EhcacheNaturalIdRegion region,
 			Ehcache ehcache,
 			SessionFactoryOptions settings) {
 		super( region, settings );
 		this.ehcache = ehcache;
 	}
 
 	@Override
-	public boolean afterInsert(NaturalIdCacheKey key, Object value) {
+	public boolean afterInsert(Object key, Object value) {
 		return false;
 	}
 
 	@Override
-	public boolean afterUpdate(NaturalIdCacheKey key, Object value, SoftLock lock) {
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) {
 		return false;
 	}
 
 	@Override
-	public Object get(NaturalIdCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		try {
 			final Element element = ehcache.get( key );
 			return element == null ? null : element.getObjectValue();
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
 
 	@Override
 	public NaturalIdRegion getRegion() {
 		return region();
 	}
 
 	@Override
-	public boolean insert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean insert(Object key, Object value) throws CacheException {
 		//OptimisticCache? versioning?
 		try {
 			ehcache.put( new Element( key, value ) );
 			return true;
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
 
 	@Override
-	public SoftLock lockItem(NaturalIdCacheKey key, Object version) throws CacheException {
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		return null;
 	}
 
 	@Override
 	public boolean putFromLoad(
-			NaturalIdCacheKey key,
+			Object key,
 			Object value,
 			long txTimestamp,
 			Object version,
 			boolean minimalPutOverride) throws CacheException {
 		try {
 			if ( minimalPutOverride && ehcache.get( key ) != null ) {
 				return false;
 			}
 			//OptimisticCache? versioning?
 			ehcache.put( new Element( key, value ) );
 			return true;
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
 
 	@Override
-	public void remove(NaturalIdCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		try {
 			ehcache.remove( key );
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
 
 	@Override
-	public void unlockItem(NaturalIdCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		// no-op
 	}
 
 	@Override
-	public boolean update(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean update(Object key, Object value) throws CacheException {
 		try {
 			ehcache.put( new Element( key, value ) );
 			return true;
 		}
 		catch (net.sf.ehcache.CacheException e) {
 			throw new CacheException( e );
 		}
 	}
 
+	@Override
+	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
+		return DefaultCacheKeysFactory.createNaturalIdKey(naturalIdValues, persister, session);
+	}
+
+	@Override
+	public Object[] getNaturalIdValues(Object cacheKey) {
+		return DefaultCacheKeysFactory.getNaturalIdValues(cacheKey);
+	}
 }
diff --git a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java
index 7f928e859e..a72a093c01 100644
--- a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java
+++ b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/collection/TransactionalAccess.java
@@ -1,91 +1,93 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.infinispan.collection;
 
-import java.io.Serializable;
-
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.infinispan.access.TransactionalAccessDelegate;
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
-import org.hibernate.cache.spi.CollectionCacheKey;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
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
 		this.delegate = new TransactionalAccessDelegate( region, region.getPutFromLoadValidator() );
 	}
 
-	public void evict(CollectionCacheKey key) throws CacheException {
+	public void evict(Object key) throws CacheException {
 		delegate.evict( key );
 	}
 
 	public void evictAll() throws CacheException {
 		delegate.evictAll();
 	}
 
-	public Object get(CollectionCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		return delegate.get( key, txTimestamp );
 	}
 
-	public boolean putFromLoad(CollectionCacheKey key, Object value, long txTimestamp, Object version) throws CacheException {
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
 		return delegate.putFromLoad( key, value, txTimestamp, version );
 	}
 
-	public boolean putFromLoad(CollectionCacheKey key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		return delegate.putFromLoad( key, value, txTimestamp, version, minimalPutOverride );
 	}
 
-	public void remove(CollectionCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		delegate.remove( key );
 	}
 
 	public void removeAll() throws CacheException {
 		delegate.removeAll();
 	}
 
 	public CollectionRegion getRegion() {
 		return region;
 	}
 
-	public SoftLock lockItem(CollectionCacheKey key, Object version) throws CacheException {
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		return null;
 	}
 
 	public SoftLock lockRegion() throws CacheException {
 		return null;
 	}
 
-	public void unlockItem(CollectionCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 	}
 
 	public void unlockRegion(SoftLock lock) throws CacheException {
 	}
 
 	@Override
-	public CollectionCacheKey generateCacheKey(Serializable id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
-		return DefaultCacheKeysFactory.createCollectionKey( id, persister, factory, tenantIdentifier );
+	public Object generateCacheKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+		return DefaultCacheKeysFactory.createCollectionKey(id, persister, factory, tenantIdentifier);
+	}
+
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getCollectionId(cacheKey);
 	}
 
 }
diff --git a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/ReadOnlyAccess.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/ReadOnlyAccess.java
index a034899fcb..38da0a84b5 100644
--- a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/ReadOnlyAccess.java
+++ b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/ReadOnlyAccess.java
@@ -1,41 +1,40 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.infinispan.entity;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 
 /**
  * A specialization of {@link TransactionalAccess} that ensures we never update data. Infinispan
  * access is always transactional.
  *
  * @author Chris Bredesen
  * @author Galder Zamarreño
  * @since 3.5
  */
 class ReadOnlyAccess extends TransactionalAccess {
 
 	ReadOnlyAccess(EntityRegionImpl region) {
 		super( region );
 	}
 
 	@Override
 	public boolean update(
-			EntityCacheKey key, Object value, Object currentVersion,
+			Object key, Object value, Object currentVersion,
 			Object previousVersion) throws CacheException {
 		throw new UnsupportedOperationException( "Illegal attempt to edit read only item" );
 	}
 
 	@Override
 	public boolean afterUpdate(
-			EntityCacheKey key, Object value, Object currentVersion,
+			Object key, Object value, Object currentVersion,
 			Object previousVersion, SoftLock lock) throws CacheException {
 		throw new UnsupportedOperationException( "Illegal attempt to edit read only item" );
 	}
 
 }
diff --git a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java
index 2d5bf83193..15f408144d 100644
--- a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java
+++ b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/entity/TransactionalAccess.java
@@ -1,109 +1,110 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.infinispan.entity;
 
-import java.io.Serializable;
-
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.infinispan.access.TransactionalAccessDelegate;
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
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
 		this.delegate = new TransactionalAccessDelegate( region, region.getPutFromLoadValidator() );
 	}
 
-	public void evict(EntityCacheKey key) throws CacheException {
+	public void evict(Object key) throws CacheException {
 		delegate.evict( key );
 	}
 
 	public void evictAll() throws CacheException {
 		delegate.evictAll();
 	}
 
-	public Object get(EntityCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		return delegate.get( key, txTimestamp );
 	}
 
 	public EntityRegion getRegion() {
 		return this.region;
 	}
 
-	public boolean insert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean insert(Object key, Object value, Object version) throws CacheException {
 		return delegate.insert( key, value, version );
 	}
 
-	public boolean putFromLoad(EntityCacheKey key, Object value, long txTimestamp, Object version) throws CacheException {
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
 		return delegate.putFromLoad( key, value, txTimestamp, version );
 	}
 
-	public boolean putFromLoad(EntityCacheKey key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		return delegate.putFromLoad( key, value, txTimestamp, version, minimalPutOverride );
 	}
 
-	public void remove(EntityCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		delegate.remove( key );
 	}
 
 	public void removeAll() throws CacheException {
 		delegate.removeAll();
 	}
 
-	public boolean update(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion)
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
 			throws CacheException {
 		return delegate.update( key, value, currentVersion, previousVersion );
 	}
 
-	public SoftLock lockItem(EntityCacheKey key, Object version) throws CacheException {
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		return null;
 	}
 
 	public SoftLock lockRegion() throws CacheException {
 		return null;
 	}
 
-	public void unlockItem(EntityCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 	}
 
 	public void unlockRegion(SoftLock lock) throws CacheException {
 	}
 
-	public boolean afterInsert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
 		return false;
 	}
 
-	public boolean afterUpdate(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
 			throws CacheException {
 		return false;
 	}
 
 	@Override
-	public EntityCacheKey generateCacheKey(Serializable id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
-		return DefaultCacheKeysFactory.createEntityKey( id, persister, factory, tenantIdentifier );
+	public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+		return DefaultCacheKeysFactory.createEntityKey(id, persister, factory, tenantIdentifier);
 	}
 
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getEntityId(cacheKey);
+	}
 }
diff --git a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/naturalid/ReadOnlyAccess.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/naturalid/ReadOnlyAccess.java
index bef04363d1..adccce120b 100644
--- a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/naturalid/ReadOnlyAccess.java
+++ b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/naturalid/ReadOnlyAccess.java
@@ -1,32 +1,31 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.infinispan.naturalid;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 
 /**
  * @author Strong Liu <stliu@hibernate.org>
  */
 class ReadOnlyAccess extends TransactionalAccess {
 
 	ReadOnlyAccess(NaturalIdRegionImpl naturalIdRegion) {
 		super( naturalIdRegion );
 	}
 
 	@Override
-	public boolean update(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean update(Object key, Object value) throws CacheException {
 		throw new UnsupportedOperationException( "Illegal attempt to edit read only item" );
 	}
 
 	@Override
-	public boolean afterUpdate(NaturalIdCacheKey key, Object value, SoftLock lock) throws CacheException {
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
 		throw new UnsupportedOperationException( "Illegal attempt to edit read only item" );
 	}
 
 }
diff --git a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/naturalid/TransactionalAccess.java b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/naturalid/TransactionalAccess.java
index bd0ba14fbf..8817b39e34 100644
--- a/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/naturalid/TransactionalAccess.java
+++ b/hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/naturalid/TransactionalAccess.java
@@ -1,115 +1,118 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cache.infinispan.naturalid;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.infinispan.access.TransactionalAccessDelegate;
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * @author Strong Liu <stliu@hibernate.org>
  */
 class TransactionalAccess implements NaturalIdRegionAccessStrategy {
 	private final NaturalIdRegionImpl region;
 	private final TransactionalAccessDelegate delegate;
 
 	TransactionalAccess(NaturalIdRegionImpl region) {
 		this.region = region;
 		this.delegate = new TransactionalAccessDelegate( region, region.getPutFromLoadValidator() );
 	}
 
 	@Override
-	public boolean insert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean insert(Object key, Object value) throws CacheException {
 		return delegate.insert( key, value, null );
 	}
 
 	@Override
-	public boolean update(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean update(Object key, Object value) throws CacheException {
 		return delegate.update( key, value, null, null );
 	}
 
 	@Override
 	public NaturalIdRegion getRegion() {
 		return region;
 	}
 
 	@Override
-	public void evict(NaturalIdCacheKey key) throws CacheException {
+	public void evict(Object key) throws CacheException {
 		delegate.evict( key );
 	}
 
 	@Override
 	public void evictAll() throws CacheException {
 		delegate.evictAll();
 	}
 
 	@Override
-	public Object get(NaturalIdCacheKey key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		return delegate.get( key, txTimestamp );
 	}
 
 	@Override
-	public boolean putFromLoad(NaturalIdCacheKey key, Object value, long txTimestamp, Object version) throws CacheException {
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
 		return delegate.putFromLoad( key, value, txTimestamp, version );
 	}
 
 	@Override
-	public boolean putFromLoad(NaturalIdCacheKey key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 		return delegate.putFromLoad( key, value, txTimestamp, version, minimalPutOverride );
 	}
 
 	@Override
-	public void remove(NaturalIdCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		delegate.remove( key );
 	}
 
 	@Override
 	public void removeAll() throws CacheException {
 		delegate.removeAll();
 	}
 
 	@Override
-	public SoftLock lockItem(NaturalIdCacheKey key, Object version) throws CacheException {
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		return null;
 	}
 
 	@Override
 	public SoftLock lockRegion() throws CacheException {
 		return null;
 	}
 
 	@Override
-	public void unlockItem(NaturalIdCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 	}
 
 	@Override
 	public void unlockRegion(SoftLock lock) throws CacheException {
 	}
 
 	@Override
-	public boolean afterInsert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean afterInsert(Object key, Object value) throws CacheException {
 		return false;
 	}
 
 	@Override
-	public boolean afterUpdate(NaturalIdCacheKey key, Object value, SoftLock lock) throws CacheException {
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
 		return false;
 	}
 
 	@Override
-	public NaturalIdCacheKey generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
+	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
 		return DefaultCacheKeysFactory.createNaturalIdKey( naturalIdValues, persister, session );
 	}
 
+	@Override
+	public Object[] getNaturalIdValues(Object cacheKey) {
+		return DefaultCacheKeysFactory.getNaturalIdValues(cacheKey);
+	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
index fd48bbb4e1..b08b0718e8 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractEntityCollectionRegionTestCase.java
@@ -1,127 +1,126 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan;
 
 import java.util.Properties;
 
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.spi.CacheDataDescription;
-import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.TransactionalDataRegion;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Base class for tests of EntityRegion and CollectionRegion implementations.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
-public abstract class AbstractEntityCollectionRegionTestCase<T extends CacheKey> extends AbstractRegionImplTestCase<T> {
+public abstract class AbstractEntityCollectionRegionTestCase extends AbstractRegionImplTestCase {
 	@Test
 	public void testSupportedAccessTypes() throws Exception {
 		supportedAccessTypeTest();
 	}
 
 	private void supportedAccessTypeTest() throws Exception {
 		StandardServiceRegistryBuilder ssrb = CacheTestUtil.buildBaselineStandardServiceRegistryBuilder(
 				"test",
 				InfinispanRegionFactory.class,
 				true,
 				false
 		);
 		ssrb.applySetting( InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, "entity" );
 		final StandardServiceRegistry registry = ssrb.build();
 		try {
 			InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
 					registry,
 					getCacheTestSupport()
 			);
 			supportedAccessTypeTest( regionFactory, CacheTestUtil.toProperties( ssrb.getSettings() ) );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( registry );
 		}
 	}
 
 	/**
 	 * Creates a Region using the given factory, and then ensure that it handles calls to
 	 * buildAccessStrategy as expected when all the various {@link AccessType}s are passed as
 	 * arguments.
 	 */
 	protected abstract void supportedAccessTypeTest(RegionFactory regionFactory, Properties properties);
 
 	@Test
 	public void testIsTransactionAware() throws Exception {
 		StandardServiceRegistryBuilder ssrb = CacheTestUtil.buildBaselineStandardServiceRegistryBuilder(
 				"test",
 				InfinispanRegionFactory.class,
 				true,
 				false
 		);
 		final StandardServiceRegistry registry = ssrb.build();
 		try {
 			Properties properties = CacheTestUtil.toProperties( ssrb.getSettings() );
 			InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
 					registry,
 					getCacheTestSupport()
 			);
 			TransactionalDataRegion region = (TransactionalDataRegion) createRegion(
 					regionFactory,
 					"test/test",
 					properties,
 					getCacheDataDescription()
 			);
 			assertTrue( "Region is transaction-aware", region.isTransactionAware() );
 			CacheTestUtil.stopRegionFactory( regionFactory, getCacheTestSupport() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( registry );
 		}
 	}
 
 	@Test
 	public void testGetCacheDataDescription() throws Exception {
 		StandardServiceRegistryBuilder ssrb = CacheTestUtil.buildBaselineStandardServiceRegistryBuilder(
 				"test",
 				InfinispanRegionFactory.class,
 				true,
 				false
 		);
 		final StandardServiceRegistry registry = ssrb.build();
 		try {
 			Properties properties = CacheTestUtil.toProperties( ssrb.getSettings() );
 			InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
 					registry,
 					getCacheTestSupport()
 			);
 			TransactionalDataRegion region = (TransactionalDataRegion) createRegion(
 					regionFactory,
 					"test/test",
 					properties,
 					getCacheDataDescription()
 			);
 			CacheDataDescription cdd = region.getCacheDataDescription();
 			assertNotNull( cdd );
 			CacheDataDescription expected = getCacheDataDescription();
 			assertEquals( expected.isMutable(), cdd.isMutable() );
 			assertEquals( expected.isVersioned(), cdd.isVersioned() );
 			assertEquals( expected.getVersionComparator(), cdd.getVersionComparator() );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( registry );
 		}
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
index e41e243e93..2de5a570c9 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
@@ -1,240 +1,239 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan;
 
 import java.util.Properties;
 import java.util.Set;
 
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
-import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.GeneralDataRegion;
 import org.hibernate.cache.spi.QueryResultsRegion;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
-import org.junit.Ignore;
-import org.junit.Test;
 import org.infinispan.AdvancedCache;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 import org.jboss.logging.Logger;
+import org.junit.Ignore;
+import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 
 /**
  * Base class for tests of QueryResultsRegion and TimestampsRegion.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
-public abstract class AbstractGeneralDataRegionTestCase<T extends CacheKey> extends AbstractRegionImplTestCase<T> {
+public abstract class AbstractGeneralDataRegionTestCase extends AbstractRegionImplTestCase {
 	private static final Logger log = Logger.getLogger( AbstractGeneralDataRegionTestCase.class );
 
 	protected static final String KEY = "Key";
 
 	protected static final String VALUE1 = "value1";
 	protected static final String VALUE2 = "value2";
 
 	protected StandardServiceRegistryBuilder createStandardServiceRegistryBuilder() {
 		return CacheTestUtil.buildBaselineStandardServiceRegistryBuilder(
 				"test",
 				InfinispanRegionFactory.class,
 				false,
 				true
 		);
 	}
 
 	@Override
-	protected void putInRegion(Region region, T key, Object value) {
+	protected void putInRegion(Region region, Object key, Object value) {
 		((GeneralDataRegion) region).put( key, value );
 	}
 
 	@Override
-	protected void removeFromRegion(Region region, T key) {
+	protected void removeFromRegion(Region region, Object key) {
 		((GeneralDataRegion) region).evict( key );
 	}
 
 	@Test
 	@Ignore // currently ignored because of HHH-9800
 	public void testEvict() throws Exception {
 		evictOrRemoveTest();
 	}
 
 	private void evictOrRemoveTest() throws Exception {
 		final StandardServiceRegistryBuilder ssrb = createStandardServiceRegistryBuilder();
 		StandardServiceRegistry registry1 = ssrb.build();
 		StandardServiceRegistry registry2 = ssrb.build();
 		try {
 			InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
 					registry1,
 					getCacheTestSupport()
 			);
 
 			final Properties properties = CacheTestUtil.toProperties( ssrb.getSettings() );
 
 			boolean invalidation = false;
 
 			// Sleep a bit to avoid concurrent FLUSH problem
 			avoidConcurrentFlush();
 
 			GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(
 					regionFactory,
 					getStandardRegionName( REGION_PREFIX ),
 					properties,
 					null
 			);
 
 			regionFactory = CacheTestUtil.startRegionFactory(
 					registry2,
 					getCacheTestSupport()
 			);
 
 			GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(
 					regionFactory,
 					getStandardRegionName( REGION_PREFIX ),
 					properties,
 					null
 			);
 			assertNull( "local is clean", localRegion.get( KEY ) );
 			assertNull( "remote is clean", remoteRegion.get( KEY ) );
 
 			regionPut( localRegion );
 			sleep( 250 );
 			assertEquals( VALUE1, localRegion.get( KEY ) );
 
 			// allow async propagation
 			sleep( 250 );
 			Object expected = invalidation ? null : VALUE1;
 			assertEquals( expected, remoteRegion.get( KEY ) );
 
 			regionEvict( localRegion );
 
 			// allow async propagation
 			sleep( 250 );
 			assertEquals( null, localRegion.get( KEY ) );
 			assertEquals( null, remoteRegion.get( KEY ) );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( registry1 );
 			StandardServiceRegistryBuilder.destroy( registry2 );
 		}
 	}
 
    protected void regionEvict(GeneralDataRegion region) throws Exception {
       region.evict(KEY);
    }
 
    protected void regionPut(GeneralDataRegion region) throws Exception {
       region.put(KEY, VALUE1);
    }
 
    protected abstract String getStandardRegionName(String regionPrefix);
 
 	/**
 	 * Test method for {@link QueryResultsRegion#evictAll()}.
 	 * <p/>
 	 * FIXME add testing of the "immediately without regard for transaction isolation" bit in the
 	 * CollectionRegionAccessStrategy API.
 	 */
 	public void testEvictAll() throws Exception {
 		evictOrRemoveAllTest( "entity" );
 	}
 
 	private void evictOrRemoveAllTest(String configName) throws Exception {
 		final StandardServiceRegistryBuilder ssrb = createStandardServiceRegistryBuilder();
 		StandardServiceRegistry registry1 = ssrb.build();
 		StandardServiceRegistry registry2 = ssrb.build();
 
 		try {
 			final Properties properties = CacheTestUtil.toProperties( ssrb.getSettings() );
 
 			InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
 					registry1,
 					getCacheTestSupport()
 			);
 			AdvancedCache localCache = getInfinispanCache( regionFactory );
 
 			// Sleep a bit to avoid concurrent FLUSH problem
 			avoidConcurrentFlush();
 
 			GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(
 					regionFactory,
 					getStandardRegionName( REGION_PREFIX ),
 					properties,
 					null
 			);
 
 			regionFactory = CacheTestUtil.startRegionFactory(
 					registry2,
 					getCacheTestSupport()
 			);
 			AdvancedCache remoteCache = getInfinispanCache( regionFactory );
 
 			// Sleep a bit to avoid concurrent FLUSH problem
 			avoidConcurrentFlush();
 
 			GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(
 					regionFactory,
 					getStandardRegionName( REGION_PREFIX ),
 					properties,
 					null
 			);
 
 			Set keys = localCache.keySet();
 			assertEquals( "No valid children in " + keys, 0, getValidKeyCount( keys ) );
 
 			keys = remoteCache.keySet();
 			assertEquals( "No valid children in " + keys, 0, getValidKeyCount( keys ) );
 
 			assertNull( "local is clean", localRegion.get( KEY ) );
 			assertNull( "remote is clean", remoteRegion.get( KEY ) );
 
 			regionPut(localRegion);
 			assertEquals( VALUE1, localRegion.get( KEY ) );
 
 			// Allow async propagation
 			sleep( 250 );
 
 			regionPut(remoteRegion);
 			assertEquals( VALUE1, remoteRegion.get( KEY ) );
 
 			// Allow async propagation
 			sleep( 250 );
 
 			localRegion.evictAll();
 
 			// allow async propagation
 			sleep( 250 );
 			// This should re-establish the region root node in the optimistic case
 			assertNull( localRegion.get( KEY ) );
 			assertEquals( "No valid children in " + keys, 0, getValidKeyCount( localCache.keySet() ) );
 
 			// Re-establishing the region root on the local node doesn't
 			// propagate it to other nodes. Do a get on the remote node to re-establish
 			// This only adds a node in the case of optimistic locking
 			assertEquals( null, remoteRegion.get( KEY ) );
 			assertEquals( "No valid children in " + keys, 0, getValidKeyCount( remoteCache.keySet() ) );
 
 			assertEquals( "local is clean", null, localRegion.get( KEY ) );
 			assertEquals( "remote is clean", null, remoteRegion.get( KEY ) );
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( registry1 );
 			StandardServiceRegistryBuilder.destroy( registry2 );
 		}
 	}
 
 	protected void rollback() {
 		try {
 			BatchModeTransactionManager.getInstance().rollback();
 		}
 		catch (Exception e) {
 			log.error( e.getMessage(), e );
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java
index 23c59bf62e..2daf5ad2e0 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractRegionImplTestCase.java
@@ -1,39 +1,38 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan;
 
 import java.util.Properties;
 
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CacheDataDescription;
-import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.internal.util.compare.ComparableComparator;
 import org.infinispan.AdvancedCache;
 
 /**
  * Base class for tests of Region implementations.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
-public abstract class AbstractRegionImplTestCase<T extends CacheKey> extends AbstractNonFunctionalTestCase {
+public abstract class AbstractRegionImplTestCase extends AbstractNonFunctionalTestCase {
 
    protected abstract AdvancedCache getInfinispanCache(InfinispanRegionFactory regionFactory);
 
    protected abstract Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd);
 
-   protected abstract void putInRegion(Region region, T key, Object value);
+   protected abstract void putInRegion(Region region, Object key, Object value);
 
-   protected abstract void removeFromRegion(Region region, T key);
+   protected abstract void removeFromRegion(Region region, Object key);
 
    protected CacheDataDescription getCacheDataDescription() {
       return new CacheDataDescriptionImpl(true, true, ComparableComparator.INSTANCE);
    }
 
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
index efa2eb66d0..1d85372e99 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/AbstractCollectionRegionAccessStrategyTestCase.java
@@ -1,485 +1,480 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan.collection;
 
+import javax.transaction.TransactionManager;
 import java.util.concurrent.Callable;
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.ExecutorService;
 import java.util.concurrent.Executors;
 import java.util.concurrent.Future;
 import java.util.concurrent.TimeUnit;
 
-import javax.transaction.TransactionManager;
-
+import junit.framework.AssertionFailedError;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.access.PutFromLoadValidator;
 import org.hibernate.cache.infinispan.access.TransactionalAccessDelegate;
 import org.hibernate.cache.infinispan.collection.CollectionRegionImpl;
 import org.hibernate.cache.infinispan.util.Caches;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CacheDataDescription;
-import org.hibernate.cache.spi.CollectionCacheKey;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.internal.util.compare.ComparableComparator;
 import org.hibernate.test.cache.infinispan.AbstractNonFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.NodeEnvironment;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.hibernate.test.cache.infinispan.util.TestingKeyFactory;
-import org.junit.After;
-import org.junit.Before;
-import org.junit.Test;
-
-import junit.framework.AssertionFailedError;
-
 import org.infinispan.test.CacheManagerCallable;
 import org.infinispan.test.fwk.TestCacheManagerFactory;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 import org.jboss.logging.Logger;
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
 
 import static org.infinispan.test.TestingUtil.withCacheManager;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Base class for tests of CollectionRegionAccessStrategy impls.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractCollectionRegionAccessStrategyTestCase extends AbstractNonFunctionalTestCase {
 	private static final Logger log = Logger.getLogger( AbstractCollectionRegionAccessStrategyTestCase.class );
-
 	public static final String REGION_NAME = "test/com.foo.test";
 	public static final String KEY_BASE = "KEY";
 	public static final String VALUE1 = "VALUE1";
 	public static final String VALUE2 = "VALUE2";
 
 	protected static int testCount;
 
 	protected NodeEnvironment localEnvironment;
 	protected CollectionRegionImpl localCollectionRegion;
 	protected CollectionRegionAccessStrategy localAccessStrategy;
 
 	protected NodeEnvironment remoteEnvironment;
 	protected CollectionRegionImpl remoteCollectionRegion;
 	protected CollectionRegionAccessStrategy remoteAccessStrategy;
 
 	protected boolean invalidation;
 	protected boolean synchronous;
 
 	protected Exception node1Exception;
 	protected Exception node2Exception;
 
 	protected AssertionFailedError node1Failure;
 	protected AssertionFailedError node2Failure;
 
 	protected abstract AccessType getAccessType();
 
 	@Before
 	public void prepareResources() throws Exception {
 		// to mimic exactly the old code results, both environments here are exactly the same...
 		StandardServiceRegistryBuilder ssrb = createStandardServiceRegistryBuilder( getConfigurationName() );
 		localEnvironment = new NodeEnvironment( ssrb );
 		localEnvironment.prepare();
 
 		localCollectionRegion = localEnvironment.getCollectionRegion( REGION_NAME, getCacheDataDescription() );
 		localAccessStrategy = localCollectionRegion.buildAccessStrategy( getAccessType() );
 
 		invalidation = Caches.isInvalidationCache(localCollectionRegion.getCache());
 		synchronous = Caches.isSynchronousCache(localCollectionRegion.getCache());
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		remoteEnvironment = new NodeEnvironment( ssrb );
 		remoteEnvironment.prepare();
 
 		remoteCollectionRegion = remoteEnvironment.getCollectionRegion( REGION_NAME, getCacheDataDescription() );
 		remoteAccessStrategy = remoteCollectionRegion.buildAccessStrategy( getAccessType() );
 	}
 
 	protected abstract String getConfigurationName();
 
 	protected static StandardServiceRegistryBuilder createStandardServiceRegistryBuilder(String configName) {
 		final StandardServiceRegistryBuilder ssrb = CacheTestUtil.buildBaselineStandardServiceRegistryBuilder(
 				REGION_PREFIX,
 				InfinispanRegionFactory.class,
 				true,
 				false
 		);
 		ssrb.applySetting( InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, configName );
 		return ssrb;
 	}
 
 	protected CacheDataDescription getCacheDataDescription() {
 		return new CacheDataDescriptionImpl( true, true, ComparableComparator.INSTANCE );
 	}
 
 	@After
 	public void releaseResources() throws Exception {
 		if ( localEnvironment != null ) {
 			localEnvironment.release();
 		}
 		if ( remoteEnvironment != null ) {
 			remoteEnvironment.release();
 		}
 	}
 
 	protected boolean isUsingInvalidation() {
 		return invalidation;
 	}
 
 	protected boolean isSynchronous() {
 		return synchronous;
 	}
 
 	@Test
 	public abstract void testCacheConfiguration();
 
 	@Test
 	public void testGetRegion() {
 		assertEquals( "Correct region", localCollectionRegion, localAccessStrategy.getRegion() );
 	}
 
 	@Test
 	public void testPutFromLoadRemoveDoesNotProduceStaleData() throws Exception {
 		final CountDownLatch pferLatch = new CountDownLatch( 1 );
 		final CountDownLatch removeLatch = new CountDownLatch( 1 );
       final TransactionManager remoteTm = remoteCollectionRegion.getTransactionManager();
       withCacheManager(new CacheManagerCallable(TestCacheManagerFactory.createCacheManager(false)) {
          @Override
          public void call() {
             PutFromLoadValidator validator = new PutFromLoadValidator(cm,
                   remoteTm, 20000) {
                @Override
                public boolean acquirePutFromLoadLock(Object key) {
                   boolean acquired = super.acquirePutFromLoadLock( key );
                   try {
                      removeLatch.countDown();
                      pferLatch.await( 2, TimeUnit.SECONDS );
                   }
                   catch (InterruptedException e) {
                      log.debug( "Interrupted" );
                      Thread.currentThread().interrupt();
                   }
                   catch (Exception e) {
                      log.error( "Error", e );
                      throw new RuntimeException( "Error", e );
                   }
                   return acquired;
                }
             };
 
             final TransactionalAccessDelegate delegate =
                   new TransactionalAccessDelegate(localCollectionRegion, validator);
             final TransactionManager localTm = localCollectionRegion.getTransactionManager();
 
             Callable<Void> pferCallable = new Callable<Void>() {
                public Void call() throws Exception {
                   delegate.putFromLoad( "k1", "v1", 0, null );
                   return null;
                }
             };
 
             Callable<Void> removeCallable = new Callable<Void>() {
                public Void call() throws Exception {
                   removeLatch.await();
                   Caches.withinTx(localTm, new Callable<Void>() {
                      @Override
                      public Void call() throws Exception {
                         delegate.remove("k1");
                         return null;
                      }
                   });
                   pferLatch.countDown();
                   return null;
                }
             };
 
             ExecutorService executorService = Executors.newCachedThreadPool();
             Future<Void> pferFuture = executorService.submit( pferCallable );
             Future<Void> removeFuture = executorService.submit( removeCallable );
 
             try {
                pferFuture.get();
                removeFuture.get();
             } catch (Exception e) {
                throw new RuntimeException(e);
             }
 
             assertFalse(localCollectionRegion.getCache().containsKey("k1"));
          }
       });
 	}
 
 	@Test
 	public void testPutFromLoad() throws Exception {
 		putFromLoadTest( false );
 	}
 
 	@Test
 	public void testPutFromLoadMinimal() throws Exception {
 		putFromLoadTest( true );
 	}
 
 	private void putFromLoadTest(final boolean useMinimalAPI) throws Exception {
 
-		final CollectionCacheKey KEY = TestingKeyFactory.generateCollectionCacheKey( KEY_BASE + testCount++ );
+		final Object KEY = TestingKeyFactory.generateCollectionCacheKey( KEY_BASE + testCount++ );
 
 		final CountDownLatch writeLatch1 = new CountDownLatch( 1 );
 		final CountDownLatch writeLatch2 = new CountDownLatch( 1 );
 		final CountDownLatch completionLatch = new CountDownLatch( 2 );
 
 		Thread node1 = new Thread() {
 
 			public void run() {
 
 				try {
 					long txTimestamp = System.currentTimeMillis();
 					BatchModeTransactionManager.getInstance().begin();
 
 					assertEquals( "node1 starts clean", null, localAccessStrategy.get( KEY, txTimestamp ) );
 
 					writeLatch1.await();
 
 					if ( useMinimalAPI ) {
 						localAccessStrategy.putFromLoad( KEY, VALUE2, txTimestamp, new Integer( 2 ), true );
 					}
 					else {
 						localAccessStrategy.putFromLoad( KEY, VALUE2, txTimestamp, new Integer( 2 ) );
 					}
 
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (Exception e) {
 					log.error( "node1 caught exception", e );
 					node1Exception = e;
 					rollback();
 				}
 				catch (AssertionFailedError e) {
 					node1Failure = e;
 					rollback();
 				}
 				finally {
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
 
 					assertNull( "node2 starts clean", remoteAccessStrategy.get( KEY, txTimestamp ) );
 
 					// Let node1 write
 					writeLatch1.countDown();
 					// Wait for node1 to finish
 					writeLatch2.await();
 
 					// Let the first PFER propagate
 					sleep( 200 );
 
 					if ( useMinimalAPI ) {
 						remoteAccessStrategy.putFromLoad( KEY, VALUE1, txTimestamp, new Integer( 1 ), true );
 					}
 					else {
 						remoteAccessStrategy.putFromLoad( KEY, VALUE1, txTimestamp, new Integer( 1 ) );
 					}
 
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (Exception e) {
 					log.error( "node2 caught exception", e );
 					node2Exception = e;
 					rollback();
 				}
 				catch (AssertionFailedError e) {
 					node2Failure = e;
 					rollback();
 				}
 				finally {
 					completionLatch.countDown();
 				}
 			}
 		};
 
 		node1.setDaemon( true );
 		node2.setDaemon( true );
 
 		node1.start();
 		node2.start();
 
 		assertTrue( "Threads completed", completionLatch.await( 2, TimeUnit.SECONDS ) );
 
 		if ( node1Failure != null ) {
 			throw node1Failure;
 		}
 		if ( node2Failure != null ) {
 			throw node2Failure;
 		}
 
 		assertEquals( "node1 saw no exceptions", null, node1Exception );
 		assertEquals( "node2 saw no exceptions", null, node2Exception );
 
 		// let the final PFER propagate
 		sleep( 100 );
 
 		long txTimestamp = System.currentTimeMillis();
 		String msg1 = "Correct node1 value";
 		String msg2 = "Correct node2 value";
 		Object expected1 = null;
 		Object expected2 = null;
 		if ( isUsingInvalidation() ) {
 			// PFER does not generate any invalidation, so each node should
 			// succeed. We count on database locking and Hibernate removing
 			// the collection on any update to prevent the situation we have
 			// here where the caches have inconsistent data
 			expected1 = VALUE2;
 			expected2 = VALUE1;
 		}
 		else {
 			// the initial VALUE2 should prevent the node2 put
 			expected1 = VALUE2;
 			expected2 = VALUE2;
 		}
 
 		assertEquals( msg1, expected1, localAccessStrategy.get( KEY, txTimestamp ) );
 		assertEquals( msg2, expected2, remoteAccessStrategy.get( KEY, txTimestamp ) );
 	}
 
 	@Test
 	public void testRemove() throws Exception {
 		evictOrRemoveTest( false );
 	}
 
 	@Test
 	public void testRemoveAll() throws Exception {
 		evictOrRemoveAllTest( false );
 	}
 
 	@Test
 	public void testEvict() throws Exception {
 		evictOrRemoveTest( true );
 	}
 
 	@Test
 	public void testEvictAll() throws Exception {
 		evictOrRemoveAllTest( true );
 	}
 
 	private void evictOrRemoveTest(final boolean evict) throws Exception {
 
-		final CollectionCacheKey KEY = TestingKeyFactory.generateCollectionCacheKey( KEY_BASE + testCount++ );
+		final Object KEY = TestingKeyFactory.generateCollectionCacheKey( KEY_BASE + testCount++ );
 
 		assertNull( "local is clean", localAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 		assertNull( "remote is clean", remoteAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 
 		localAccessStrategy.putFromLoad( KEY, VALUE1, System.currentTimeMillis(), new Integer( 1 ) );
 		assertEquals( VALUE1, localAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 		remoteAccessStrategy.putFromLoad( KEY, VALUE1, System.currentTimeMillis(), new Integer( 1 ) );
 		assertEquals( VALUE1, remoteAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 
 		// Wait for async propagation
 		sleep( 250 );
 
       Caches.withinTx(localCollectionRegion.getTransactionManager(), new Callable<Void>() {
          @Override
          public Void call() throws Exception {
             if (evict)
                localAccessStrategy.evict(KEY);
             else
                localAccessStrategy.remove(KEY);
             return null;
          }
       });
 
 		assertEquals( null, localAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 
 		assertEquals( null, remoteAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 	}
 
 	private void evictOrRemoveAllTest(final boolean evict) throws Exception {
 
-		final CollectionCacheKey KEY = TestingKeyFactory.generateCollectionCacheKey( KEY_BASE + testCount++ );
+		final Object KEY = TestingKeyFactory.generateCollectionCacheKey( KEY_BASE + testCount++ );
 
 		assertEquals( 0, getValidKeyCount( localCollectionRegion.getCache().keySet() ) );
 
 		assertEquals( 0, getValidKeyCount( remoteCollectionRegion.getCache().keySet() ) );
 
 		assertNull( "local is clean", localAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 		assertNull( "remote is clean", remoteAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 
 		localAccessStrategy.putFromLoad( KEY, VALUE1, System.currentTimeMillis(), new Integer( 1 ) );
 		assertEquals( VALUE1, localAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 		remoteAccessStrategy.putFromLoad( KEY, VALUE1, System.currentTimeMillis(), new Integer( 1 ) );
 		assertEquals( VALUE1, remoteAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 
 		// Wait for async propagation
 		sleep( 250 );
 
       Caches.withinTx(localCollectionRegion.getTransactionManager(), new Callable<Void>() {
          @Override
          public Void call() throws Exception {
             if (evict)
                localAccessStrategy.evictAll();
             else
                localAccessStrategy.removeAll();
             return null;
          }
       });
 
 		// This should re-establish the region root node
 		assertNull( localAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 
 		assertEquals( 0, getValidKeyCount( localCollectionRegion.getCache().keySet() ) );
 
 		// Re-establishing the region root on the local node doesn't
 		// propagate it to other nodes. Do a get on the remote node to re-establish
 		assertEquals( null, remoteAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 
 		assertEquals( 0, getValidKeyCount( remoteCollectionRegion.getCache().keySet() ) );
 
 		// Test whether the get above messes up the optimistic version
 		remoteAccessStrategy.putFromLoad( KEY, VALUE1, System.currentTimeMillis(), new Integer( 1 ) );
 		assertEquals( VALUE1, remoteAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 
 		assertEquals( 1, getValidKeyCount( remoteCollectionRegion.getCache().keySet() ) );
 
 		// Wait for async propagation of the putFromLoad
 		sleep( 250 );
 
 		assertEquals(
 				"local is correct", (isUsingInvalidation() ? null : VALUE1), localAccessStrategy.get(
 				KEY, System
 				.currentTimeMillis()
 		)
 		);
 		assertEquals( "remote is correct", VALUE1, remoteAccessStrategy.get( KEY, System.currentTimeMillis() ) );
 	}
 
 	private void rollback() {
 		try {
 			BatchModeTransactionManager.getInstance().rollback();
 		}
 		catch (Exception e) {
 			log.error( e.getMessage(), e );
 		}
 
 	}
 
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/CollectionRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/CollectionRegionImplTestCase.java
index 88373f3f65..a26b2581cd 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/CollectionRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/CollectionRegionImplTestCase.java
@@ -1,75 +1,74 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan.collection;
 
+import java.util.Properties;
+
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CacheDataDescription;
-import org.hibernate.cache.spi.CollectionCacheKey;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.test.cache.infinispan.AbstractEntityCollectionRegionTestCase;
 import org.infinispan.AdvancedCache;
 
-import java.util.Properties;
-
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.fail;
 
 /**
  * Tests of CollectionRegionImpl.
  * 
  * @author Galder Zamarreño
  */
-public class CollectionRegionImplTestCase extends AbstractEntityCollectionRegionTestCase<CollectionCacheKey> {
+public class CollectionRegionImplTestCase extends AbstractEntityCollectionRegionTestCase {
 
    private static CacheDataDescription MUTABLE_NON_VERSIONED = new CacheDataDescriptionImpl(true, false, null);
 
    @Override
    protected void supportedAccessTypeTest(RegionFactory regionFactory, Properties properties) {
       CollectionRegion region = regionFactory.buildCollectionRegion("test", properties, MUTABLE_NON_VERSIONED);
       assertNull("Got TRANSACTIONAL", region.buildAccessStrategy(AccessType.TRANSACTIONAL)
                .lockRegion());
       try {
          region.buildAccessStrategy(AccessType.NONSTRICT_READ_WRITE);
          fail("Incorrectly got NONSTRICT_READ_WRITE");
       } catch (CacheException good) {
       }
 
       try {
          region.buildAccessStrategy(AccessType.READ_WRITE);
          fail("Incorrectly got READ_WRITE");
       } catch (CacheException good) {
       }
    }
 
    @Override
    protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd) {
       return regionFactory.buildCollectionRegion(regionName, properties, cdd);
    }
 
    @Override
    protected AdvancedCache getInfinispanCache(InfinispanRegionFactory regionFactory) {
       return regionFactory.getCacheManager().getCache(InfinispanRegionFactory.DEF_ENTITY_RESOURCE).getAdvancedCache();
    }
 
    @Override
-   protected void putInRegion(Region region, CollectionCacheKey key, Object value) {
+   protected void putInRegion(Region region, Object key, Object value) {
       CollectionRegionAccessStrategy strategy = ((CollectionRegion) region).buildAccessStrategy(AccessType.TRANSACTIONAL);
       strategy.putFromLoad(key, value, System.currentTimeMillis(), new Integer(1));
    }
 
    @Override
-   protected void removeFromRegion(Region region, CollectionCacheKey key) {
+   protected void removeFromRegion(Region region, Object key) {
       ((CollectionRegion) region).buildAccessStrategy(AccessType.TRANSACTIONAL).remove(key);
    }
 
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/TransactionalExtraAPITestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/TransactionalExtraAPITestCase.java
index b8b63c473f..61503cb1ff 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/TransactionalExtraAPITestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/collection/TransactionalExtraAPITestCase.java
@@ -1,101 +1,100 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan.collection;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
-import org.hibernate.cache.spi.CollectionCacheKey;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.test.cache.infinispan.AbstractNonFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.NodeEnvironment;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.hibernate.test.cache.infinispan.util.TestingKeyFactory;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import static org.junit.Assert.assertNull;
 
 /**
  * TransactionalExtraAPITestCase.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class TransactionalExtraAPITestCase extends AbstractNonFunctionalTestCase {
 
 	public static final String REGION_NAME = "test/com.foo.test";
-	public static final CollectionCacheKey KEY = TestingKeyFactory.generateCollectionCacheKey( "KEY" );
+	public static final Object KEY = TestingKeyFactory.generateCollectionCacheKey( "KEY" );
 	public static final String VALUE1 = "VALUE1";
 	public static final String VALUE2 = "VALUE2";
 
 	private NodeEnvironment environment;
 	private static CollectionRegionAccessStrategy accessStrategy;
 
 	@Before
 	public final void prepareLocalAccessStrategy() throws Exception {
 		environment = new NodeEnvironment( createStandardServiceRegistryBuilder() );
 		environment.prepare();
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		accessStrategy = environment.getCollectionRegion( REGION_NAME, null ).buildAccessStrategy( getAccessType() );
 	}
 
 	protected StandardServiceRegistryBuilder createStandardServiceRegistryBuilder() {
 		StandardServiceRegistryBuilder ssrb = CacheTestUtil.buildBaselineStandardServiceRegistryBuilder(
 				REGION_PREFIX, InfinispanRegionFactory.class, true, false
 		);
 		ssrb.applySetting( InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, getCacheConfigName() );
 		return ssrb;
 	}
 
 	protected String getCacheConfigName() {
 		return "entity";
 	}
 
 	protected AccessType getAccessType() {
 		return AccessType.TRANSACTIONAL;
 	}
 
 	@After
 	public final void releaseLocalAccessStrategy() throws Exception {
 		if ( environment != null ) {
 			environment.release();
 		}
 	}
 
 	protected CollectionRegionAccessStrategy getCollectionAccessStrategy() {
 		return accessStrategy;
 	}
 
 	@Test
 	public void testLockItem() {
 		assertNull( getCollectionAccessStrategy().lockItem( KEY, new Integer( 1 ) ) );
 	}
 
 	@Test
 	public void testLockRegion() {
 		assertNull( getCollectionAccessStrategy().lockRegion() );
 	}
 
 	@Test
 	public void testUnlockItem() {
 		getCollectionAccessStrategy().unlockItem( KEY, new MockSoftLock() );
 	}
 
 	@Test
 	public void testUnlockRegion() {
 		getCollectionAccessStrategy().unlockItem( KEY, new MockSoftLock() );
 	}
 
 	public static class MockSoftLock implements SoftLock {
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
index 90e0a81782..da336ff0d9 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractEntityRegionAccessStrategyTestCase.java
@@ -1,602 +1,599 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan.entity;
 
 import java.util.Arrays;
 import java.util.concurrent.Callable;
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.TimeUnit;
 
+import junit.framework.AssertionFailedError;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.entity.EntityRegionImpl;
 import org.hibernate.cache.infinispan.util.Caches;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CacheDataDescription;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.internal.util.compare.ComparableComparator;
 import org.hibernate.test.cache.infinispan.AbstractNonFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.NodeEnvironment;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.hibernate.test.cache.infinispan.util.TestingKeyFactory;
-import org.junit.After;
-import org.junit.Before;
-import org.junit.Test;
-
-import junit.framework.AssertionFailedError;
-
 import org.infinispan.Cache;
 import org.infinispan.test.TestingUtil;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 import org.jboss.logging.Logger;
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Base class for tests of EntityRegionAccessStrategy impls.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractEntityRegionAccessStrategyTestCase extends AbstractNonFunctionalTestCase {
 
    private static final Logger log = Logger.getLogger(AbstractEntityRegionAccessStrategyTestCase.class);
 
    public static final String REGION_NAME = "test/com.foo.test";
    public static final String KEY_BASE = "KEY";
    public static final String VALUE1 = "VALUE1";
    public static final String VALUE2 = "VALUE2";
 
    protected static int testCount;
 
    protected NodeEnvironment localEnvironment;
    protected EntityRegionImpl localEntityRegion;
    protected EntityRegionAccessStrategy localAccessStrategy;
 
    protected NodeEnvironment remoteEnvironment;
    protected EntityRegionImpl remoteEntityRegion;
    protected EntityRegionAccessStrategy remoteAccessStrategy;
 
    protected boolean invalidation;
    protected boolean synchronous;
 
    protected Exception node1Exception;
    protected Exception node2Exception;
 
    protected AssertionFailedError node1Failure;
    protected AssertionFailedError node2Failure;
 
    @Before
    public void prepareResources() throws Exception {
       // to mimic exactly the old code results, both environments here are exactly the same...
       StandardServiceRegistryBuilder ssrb = createStandardServiceRegistryBuilder( getConfigurationName() );
       localEnvironment = new NodeEnvironment( ssrb );
       localEnvironment.prepare();
 
       localEntityRegion = localEnvironment.getEntityRegion(REGION_NAME, getCacheDataDescription());
       localAccessStrategy = localEntityRegion.buildAccessStrategy(getAccessType());
 
       invalidation = Caches.isInvalidationCache(localEntityRegion.getCache());
       synchronous = Caches.isSynchronousCache(localEntityRegion.getCache());
 
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       remoteEnvironment = new NodeEnvironment( ssrb );
       remoteEnvironment.prepare();
 
       remoteEntityRegion = remoteEnvironment.getEntityRegion(REGION_NAME, getCacheDataDescription());
       remoteAccessStrategy = remoteEntityRegion.buildAccessStrategy(getAccessType());
 
       waitForClusterToForm(localEntityRegion.getCache(),
             remoteEntityRegion.getCache());
    }
 
    protected void waitForClusterToForm(Cache... caches) {
       TestingUtil.blockUntilViewsReceived(10000, Arrays.asList(caches));
    }
 
    protected abstract String getConfigurationName();
 
    protected static StandardServiceRegistryBuilder createStandardServiceRegistryBuilder(String configName) {
       StandardServiceRegistryBuilder ssrb = CacheTestUtil.buildBaselineStandardServiceRegistryBuilder(
             REGION_PREFIX,
             InfinispanRegionFactory.class,
             true,
             false
       );
       ssrb.applySetting( InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, configName );
       return ssrb;
    }
 
    protected CacheDataDescription getCacheDataDescription() {
       return new CacheDataDescriptionImpl(true, true, ComparableComparator.INSTANCE);
    }
 
    @After
    public void releaseResources() throws Exception {
       try {
          if (localEnvironment != null) {
             localEnvironment.release();
          }
       } finally {
          if (remoteEnvironment != null) {
             remoteEnvironment.release();
          }
       }
    }
 
    protected abstract AccessType getAccessType();
 
    protected boolean isUsingInvalidation() {
       return invalidation;
    }
 
    protected boolean isSynchronous() {
       return synchronous;
    }
 
    protected void assertThreadsRanCleanly() {
       if (node1Failure != null) {
          throw node1Failure;
       }
       if (node2Failure != null) {
          throw node2Failure;
       }
 
       if (node1Exception != null) {
          log.error("node1 saw an exception", node1Exception);
          assertEquals("node1 saw no exceptions", null, node1Exception);
       }
 
       if (node2Exception != null) {
          log.error("node2 saw an exception", node2Exception);
          assertEquals("node2 saw no exceptions", null, node2Exception);
       }
    }
 
    @Test
    public abstract void testCacheConfiguration();
 
    @Test
    public void testGetRegion() {
       assertEquals("Correct region", localEntityRegion, localAccessStrategy.getRegion());
    }
 
    @Test
    public void testPutFromLoad() throws Exception {
       putFromLoadTest(false);
    }
 
    @Test
    public void testPutFromLoadMinimal() throws Exception {
       putFromLoadTest(true);
    }
 
    /**
     * Simulate 2 nodes, both start, tx do a get, experience a cache miss, then
     * 'read from db.' First does a putFromLoad, then an update. Second tries to
     * do a putFromLoad with stale data (i.e. it took longer to read from the db).
     * Both commit their tx. Then both start a new tx and get. First should see
     * the updated data; second should either see the updated data
     * (isInvalidation() == false) or null (isInvalidation() == true).
     *
     * @param useMinimalAPI
     * @throws Exception
     */
    private void putFromLoadTest(final boolean useMinimalAPI) throws Exception {
 
-      final EntityCacheKey KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
+      final Object KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
 
       final CountDownLatch writeLatch1 = new CountDownLatch(1);
       final CountDownLatch writeLatch2 = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(2);
 
       Thread node1 = new Thread() {
 
          @Override
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
 
          @Override
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
 
    @Test
    public void testInsert() throws Exception {
 
-      final EntityCacheKey KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
+      final Object KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
 
       final CountDownLatch readLatch = new CountDownLatch(1);
       final CountDownLatch commitLatch = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(2);
 
       Thread inserter = new Thread() {
 
          @Override
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
 
          @Override
          public void run() {
 
             try {
                long txTimestamp = System.currentTimeMillis();
                BatchModeTransactionManager.getInstance().begin();
 
                readLatch.await();
 //               Object expected = !isBlockingReads() ? null : VALUE1;
                Object expected = null;
 
                assertEquals(
                      "Correct initial value", expected, localAccessStrategy.get(
                      KEY,
                      txTimestamp
                )
                );
 
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
 
    @Test
    public void testUpdate() throws Exception {
 
-      final EntityCacheKey KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
+      final Object KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
 
       // Set up initial state
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
 
       // Let the async put propagate
       sleep(250);
 
       final CountDownLatch readLatch = new CountDownLatch(1);
       final CountDownLatch commitLatch = new CountDownLatch(1);
       final CountDownLatch completionLatch = new CountDownLatch(2);
 
       Thread updater = new Thread("testUpdate-updater") {
 
          @Override
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
                if (!readerUnlocked) {
                   readLatch.countDown();
                }
                log.debug("Completion latch countdown");
                completionLatch.countDown();
             }
          }
       };
 
       Thread reader = new Thread("testUpdate-reader") {
 
          @Override
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
 
    @Test
    public void testRemove() throws Exception {
       evictOrRemoveTest(false);
    }
 
    @Test
    public void testRemoveAll() throws Exception {
       evictOrRemoveAllTest(false);
    }
 
    @Test
    public void testEvict() throws Exception {
       evictOrRemoveTest(true);
    }
 
    @Test
    public void testEvictAll() throws Exception {
       evictOrRemoveAllTest(true);
    }
 
    private void evictOrRemoveTest(final boolean evict) throws Exception {
-      final EntityCacheKey KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
+      final Object KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
       assertEquals(0, getValidKeyCount(localEntityRegion.getCache().keySet()));
       assertEquals(0, getValidKeyCount(remoteEntityRegion.getCache().keySet()));
 
       assertNull("local is clean", localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertNull("remote is clean", remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, localAccessStrategy.get(KEY, System.currentTimeMillis()));
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       Caches.withinTx(localEntityRegion.getTransactionManager(), new Callable<Void>() {
          @Override
          public Void call() throws Exception {
             if (evict)
                localAccessStrategy.evict(KEY);
             else
                localAccessStrategy.remove(KEY);
             return null;
          }
       });
       assertEquals(null, localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(0, getValidKeyCount(localEntityRegion.getCache().keySet()));
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(0, getValidKeyCount(remoteEntityRegion.getCache().keySet()));
    }
 
    private void evictOrRemoveAllTest(final boolean evict) throws Exception {
-      final EntityCacheKey KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
+      final Object KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
       assertEquals(0, getValidKeyCount(localEntityRegion.getCache().keySet()));
       assertEquals(0, getValidKeyCount(remoteEntityRegion.getCache().keySet()));
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
 
       Caches.withinTx(localEntityRegion.getTransactionManager(), new Callable<Void>() {
          @Override
          public Void call() throws Exception {
             if (evict) {
                log.debug("Call evict all locally");
                localAccessStrategy.evictAll();
             } else {
                localAccessStrategy.removeAll();
             }
             return null;
          }
       });
 
       // This should re-establish the region root node in the optimistic case
       assertNull(localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(0, getValidKeyCount(localEntityRegion.getCache().keySet()));
 
       // Re-establishing the region root on the local node doesn't
       // propagate it to other nodes. Do a get on the remote node to re-establish
       assertEquals(null, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(0, getValidKeyCount(remoteEntityRegion.getCache().keySet()));
 
       // Test whether the get above messes up the optimistic version
       remoteAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
       assertEquals(VALUE1, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(1, getValidKeyCount(remoteEntityRegion.getCache().keySet()));
 
       // Wait for async propagation
       sleep(250);
 
       assertEquals(
             "local is correct", (isUsingInvalidation() ? null : VALUE1), localAccessStrategy
             .get(KEY, System.currentTimeMillis())
       );
       assertEquals(
             "remote is correct", VALUE1, remoteAccessStrategy.get(
             KEY, System
             .currentTimeMillis()
       )
       );
    }
 
    protected void rollback() {
       try {
          BatchModeTransactionManager.getInstance().rollback();
       } catch (Exception e) {
          log.error(e.getMessage(), e);
       }
    }
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractReadOnlyAccessTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractReadOnlyAccessTestCase.java
index d6dd77ff13..d3a4dbce37 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractReadOnlyAccessTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractReadOnlyAccessTestCase.java
@@ -1,71 +1,70 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan.entity;
 
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.test.cache.infinispan.util.TestingKeyFactory;
-import org.junit.Test;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
+import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 
 /**
  * Base class for tests of TRANSACTIONAL access.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractReadOnlyAccessTestCase extends AbstractEntityRegionAccessStrategyTestCase {
 
    @Override
    protected AccessType getAccessType() {
       return AccessType.READ_ONLY;
    }
 
    @Test
    @Override
    public void testPutFromLoad() throws Exception {
       putFromLoadTest(false);
    }
 
    @Test
    @Override
    public void testPutFromLoadMinimal() throws Exception {
       putFromLoadTest(true);
    }
 
    private void putFromLoadTest(boolean minimal) throws Exception {
 
-      final EntityCacheKey KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
+      final Object KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
 
       long txTimestamp = System.currentTimeMillis();
       BatchModeTransactionManager.getInstance().begin();
       assertNull(localAccessStrategy.get(KEY, System.currentTimeMillis()));
       if (minimal)
          localAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, 1, true);
       else
          localAccessStrategy.putFromLoad(KEY, VALUE1, txTimestamp, 1);
 
       sleep(250);
       Object expected = isUsingInvalidation() ? null : VALUE1;
       assertEquals(expected, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
 
       BatchModeTransactionManager.getInstance().commit();
       assertEquals(VALUE1, localAccessStrategy.get(KEY, System.currentTimeMillis()));
       assertEquals(expected, remoteAccessStrategy.get(KEY, System.currentTimeMillis()));
    }
 
    @Test(expected = UnsupportedOperationException.class)
    @Override
    public void testUpdate() throws Exception {
-      final EntityCacheKey KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
+      final Object KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
       localAccessStrategy.update( KEY, VALUE2, 2, 1);
    }
 
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java
index 87e549a18d..fd3dce0257 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/AbstractTransactionalAccessTestCase.java
@@ -1,118 +1,116 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan.entity;
 
 import java.util.concurrent.CountDownLatch;
 import java.util.concurrent.TimeUnit;
 
 import junit.framework.AssertionFailedError;
-
-import org.infinispan.transaction.tm.BatchModeTransactionManager;
-import org.jboss.logging.Logger;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.test.cache.infinispan.util.TestingKeyFactory;
+import org.infinispan.transaction.tm.BatchModeTransactionManager;
+import org.jboss.logging.Logger;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Base class for tests of TRANSACTIONAL access.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractTransactionalAccessTestCase extends AbstractEntityRegionAccessStrategyTestCase {
 	private static final Logger log = Logger.getLogger( AbstractTransactionalAccessTestCase.class );
 
 	@Override
    protected AccessType getAccessType() {
       return AccessType.TRANSACTIONAL;
    }
 
     public void testContestedPutFromLoad() throws Exception {
 
-        final EntityCacheKey KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
+        final Object KEY = TestingKeyFactory.generateEntityCacheKey( KEY_BASE + testCount++ );
 
         localAccessStrategy.putFromLoad(KEY, VALUE1, System.currentTimeMillis(), new Integer(1));
 
         final CountDownLatch pferLatch = new CountDownLatch(1);
         final CountDownLatch pferCompletionLatch = new CountDownLatch(1);
         final CountDownLatch commitLatch = new CountDownLatch(1);
         final CountDownLatch completionLatch = new CountDownLatch(1);
 
         Thread blocker = new Thread("Blocker") {
 
             @Override
             public void run() {
 
                 try {
                     long txTimestamp = System.currentTimeMillis();
                     BatchModeTransactionManager.getInstance().begin();
 
                     assertEquals("Correct initial value", VALUE1, localAccessStrategy.get(KEY, txTimestamp));
 
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
 
             @Override
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
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java
index 0ba2b2310e..2752a41507 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/EntityRegionImplTestCase.java
@@ -1,75 +1,74 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan.entity;
 
+import java.util.Properties;
+
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CacheDataDescription;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.test.cache.infinispan.AbstractEntityCollectionRegionTestCase;
 import org.infinispan.AdvancedCache;
 
-import java.util.Properties;
-
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.fail;
 
 /**
  * Tests of EntityRegionImpl.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
-public class EntityRegionImplTestCase extends AbstractEntityCollectionRegionTestCase<EntityCacheKey> {
+public class EntityRegionImplTestCase extends AbstractEntityCollectionRegionTestCase {
 
    private static CacheDataDescription MUTABLE_NON_VERSIONED = new CacheDataDescriptionImpl(true, false, null);
 
    @Override
    protected void supportedAccessTypeTest(RegionFactory regionFactory, Properties properties) {
       EntityRegion region = regionFactory.buildEntityRegion("test", properties, MUTABLE_NON_VERSIONED);
       assertNull("Got TRANSACTIONAL",
             region.buildAccessStrategy(AccessType.TRANSACTIONAL).lockRegion());
       try {
          region.buildAccessStrategy(AccessType.NONSTRICT_READ_WRITE);
          fail("Incorrectly got NONSTRICT_READ_WRITE");
       } catch (CacheException good) {
       }
 
       try {
          region.buildAccessStrategy(AccessType.READ_WRITE);
          fail("Incorrectly got READ_WRITE");
       } catch (CacheException good) {
       }
    }
 
    @Override
-   protected void putInRegion(Region region, EntityCacheKey key, Object value) {
+   protected void putInRegion(Region region, Object key, Object value) {
       ((EntityRegion) region).buildAccessStrategy(AccessType.TRANSACTIONAL).insert(key, value, 1);
    }
 
    @Override
-   protected void removeFromRegion(Region region, EntityCacheKey key) {
+   protected void removeFromRegion(Region region, Object key) {
       ((EntityRegion) region).buildAccessStrategy(AccessType.TRANSACTIONAL).remove(key);
    }
 
    @Override
    protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd) {
       return regionFactory.buildEntityRegion(regionName, properties, cdd);
    }
 
    @Override
    protected AdvancedCache getInfinispanCache(InfinispanRegionFactory regionFactory) {
       return regionFactory.getCacheManager().getCache(
             InfinispanRegionFactory.DEF_ENTITY_RESOURCE).getAdvancedCache();
    }
 
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/TransactionalExtraAPITestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/TransactionalExtraAPITestCase.java
index cb86be8a48..61fb89aa3e 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/TransactionalExtraAPITestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/entity/TransactionalExtraAPITestCase.java
@@ -1,138 +1,137 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan.entity;
 
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
-import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
-import org.hibernate.cache.spi.EntityCacheKey;
+import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.test.cache.infinispan.AbstractNonFunctionalTestCase;
 import org.hibernate.test.cache.infinispan.NodeEnvironment;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 import org.hibernate.test.cache.infinispan.util.TestingKeyFactory;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNull;
 
 /**
  * Tests for the "extra API" in EntityRegionAccessStrategy;.
  * <p>
  * By "extra API" we mean those methods that are superfluous to the 
  * function of the JBC integration, where the impl is a no-op or a static
  * false return value, UnsupportedOperationException, etc.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class TransactionalExtraAPITestCase extends AbstractNonFunctionalTestCase {
 	public static final String REGION_NAME = "test/com.foo.test";
-	public static final EntityCacheKey KEY = TestingKeyFactory.generateEntityCacheKey( "KEY" );
+	public static final Object KEY = TestingKeyFactory.generateEntityCacheKey( "KEY" );
 	public static final String VALUE1 = "VALUE1";
 	public static final String VALUE2 = "VALUE2";
 
 	private NodeEnvironment environment;
 	private EntityRegionAccessStrategy accessStrategy;
 
 	@Before
 	public final void prepareLocalAccessStrategy() throws Exception {
 		environment = new NodeEnvironment( createStandardServiceRegistryBuilder() );
 		environment.prepare();
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		accessStrategy = environment.getEntityRegion( REGION_NAME, new CacheDataDescriptionImpl(true, false, null)).buildAccessStrategy( getAccessType() );
    }
 
 	protected StandardServiceRegistryBuilder createStandardServiceRegistryBuilder() {
 		StandardServiceRegistryBuilder ssrb = CacheTestUtil.buildBaselineStandardServiceRegistryBuilder(
 				REGION_PREFIX,
 				InfinispanRegionFactory.class,
 				true,
 				false
 		);
 		ssrb.applySetting( InfinispanRegionFactory.ENTITY_CACHE_RESOURCE_PROP, getCacheConfigName() );
 		return ssrb;
 	}
 
 	@After
 	public final void releaseLocalAccessStrategy() throws Exception {
 		if ( environment != null ) {
 			environment.release();
 		}
 	}
 
 	protected final EntityRegionAccessStrategy getEntityAccessStrategy() {
 		return accessStrategy;
 	}
 
 	protected String getCacheConfigName() {
 		return "entity";
 	}
 
 	protected AccessType getAccessType() {
 		return AccessType.TRANSACTIONAL;
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testLockItem() {
 		assertNull( getEntityAccessStrategy().lockItem( KEY, Integer.valueOf( 1 ) ) );
 	}
 
 	@Test
 	public void testLockRegion() {
 		assertNull( getEntityAccessStrategy().lockRegion() );
 	}
 
 	@Test
 	public void testUnlockItem() {
 		getEntityAccessStrategy().unlockItem( KEY, new MockSoftLock() );
 	}
 
 	@Test
 	public void testUnlockRegion() {
 		getEntityAccessStrategy().unlockItem( KEY, new MockSoftLock() );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testAfterInsert() {
 		assertFalse(
 				"afterInsert always returns false",
 				getEntityAccessStrategy().afterInsert(
 						KEY,
 						VALUE1,
 						Integer.valueOf( 1 )
 				)
 		);
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing"})
 	public void testAfterUpdate() {
 		assertFalse(
 				"afterInsert always returns false",
 				getEntityAccessStrategy().afterUpdate(
 						KEY,
 						VALUE2,
 						Integer.valueOf( 1 ),
 						Integer.valueOf( 2 ),
 						new MockSoftLock()
 				)
 		);
 	}
 
 	public static class MockSoftLock implements SoftLock {
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
index f6e6ef39da..2a8ba6baf0 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/EntityCollectionInvalidationTestCase.java
@@ -1,370 +1,366 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan.functional.cluster;
 
+import javax.transaction.TransactionManager;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Set;
 
-import javax.transaction.TransactionManager;
-
+import org.hibernate.Session;
+import org.hibernate.SessionFactory;
+import org.hibernate.test.cache.infinispan.functional.Contact;
+import org.hibernate.test.cache.infinispan.functional.Customer;
 import org.infinispan.Cache;
 import org.infinispan.manager.CacheContainer;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent;
 import org.infinispan.util.logging.Log;
 import org.infinispan.util.logging.LogFactory;
 import org.jboss.util.collection.ConcurrentSet;
 import org.junit.Test;
-import org.hibernate.Session;
-import org.hibernate.SessionFactory;
-import org.hibernate.cache.spi.EntityCacheKey;
-import org.hibernate.test.cache.infinispan.functional.Contact;
-import org.hibernate.test.cache.infinispan.functional.Customer;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 
 /**
  * EntityCollectionInvalidationTestCase.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class EntityCollectionInvalidationTestCase extends DualNodeTestCase {
 	private static final Log log = LogFactory.getLog( EntityCollectionInvalidationTestCase.class );
 
 	private static final long SLEEP_TIME = 50l;
 	private static final Integer CUSTOMER_ID = new Integer( 1 );
 
 	static int test = 0;
 
 	@Test
 	public void testAll() throws Exception {
 		log.info( "*** testAll()" );
 
 		// Bind a listener to the "local" cache
 		// Our region factory makes its CacheManager available to us
 		CacheContainer localManager = ClusterAwareRegionFactory.getCacheManager( DualNodeTestCase.LOCAL );
 		// Cache localCache = localManager.getCache("entity");
 		Cache localCustomerCache = localManager.getCache( Customer.class.getName() );
 		Cache localContactCache = localManager.getCache( Contact.class.getName() );
 		Cache localCollectionCache = localManager.getCache( Customer.class.getName() + ".contacts" );
 		MyListener localListener = new MyListener( "local" );
 		localCustomerCache.addListener( localListener );
 		localContactCache.addListener( localListener );
 		localCollectionCache.addListener( localListener );
 		TransactionManager localTM = DualNodeJtaTransactionManagerImpl.getInstance( DualNodeTestCase.LOCAL );
 
 		// Bind a listener to the "remote" cache
 		CacheContainer remoteManager = ClusterAwareRegionFactory.getCacheManager( DualNodeTestCase.REMOTE );
 		Cache remoteCustomerCache = remoteManager.getCache( Customer.class.getName() );
 		Cache remoteContactCache = remoteManager.getCache( Contact.class.getName() );
 		Cache remoteCollectionCache = remoteManager.getCache( Customer.class.getName() + ".contacts" );
 		MyListener remoteListener = new MyListener( "remote" );
 		remoteCustomerCache.addListener( remoteListener );
 		remoteContactCache.addListener( remoteListener );
 		remoteCollectionCache.addListener( remoteListener );
 		TransactionManager remoteTM = DualNodeJtaTransactionManagerImpl.getInstance( DualNodeTestCase.REMOTE );
 
 		SessionFactory localFactory = sessionFactory();
 		SessionFactory remoteFactory = secondNodeEnvironment().getSessionFactory();
 
 		try {
 			assertTrue( remoteListener.isEmpty() );
 			assertTrue( localListener.isEmpty() );
 
 			log.debug( "Create node 0" );
 			IdContainer ids = createCustomer( localFactory, localTM );
 
 			assertTrue( remoteListener.isEmpty() );
 			assertTrue( localListener.isEmpty() );
 
 			// Sleep a bit to let async commit propagate. Really just to
 			// help keep the logs organized for debugging any issues
 			sleep( SLEEP_TIME );
 
 			log.debug( "Find node 0" );
 			// This actually brings the collection into the cache
 			getCustomer( ids.customerId, localFactory, localTM );
 
 			sleep( SLEEP_TIME );
 
 			// Now the collection is in the cache so, the 2nd "get"
 			// should read everything from the cache
 			log.debug( "Find(2) node 0" );
 			localListener.clear();
 			getCustomer( ids.customerId, localFactory, localTM );
 
 			// Check the read came from the cache
 			log.debug( "Check cache 0" );
 			assertLoadedFromCache( localListener, ids.customerId, ids.contactIds );
 
 			log.debug( "Find node 1" );
 			// This actually brings the collection into the cache since invalidation is in use
 			getCustomer( ids.customerId, remoteFactory, remoteTM );
 
 			// Now the collection is in the cache so, the 2nd "get"
 			// should read everything from the cache
 			log.debug( "Find(2) node 1" );
 			remoteListener.clear();
 			getCustomer( ids.customerId, remoteFactory, remoteTM );
 
 			// Check the read came from the cache
 			log.debug( "Check cache 1" );
 			assertLoadedFromCache( remoteListener, ids.customerId, ids.contactIds );
 
 			// Modify customer in remote
 			remoteListener.clear();
 			ids = modifyCustomer( ids.customerId, remoteFactory, remoteTM );
 			sleep( 250 );
 			assertLoadedFromCache( remoteListener, ids.customerId, ids.contactIds );
 
 			// After modification, local cache should have been invalidated and hence should be empty
 			assertEquals( 0, getValidKeyCount( localCollectionCache.keySet() ) );
 			assertEquals( 0, getValidKeyCount( localCustomerCache.keySet() ) );
 		}
 		catch (Exception e) {
 			log.error( "Error", e );
 			throw e;
 		}
 		finally {
 			// cleanup the db
 			log.debug( "Cleaning up" );
 			cleanup( localFactory, localTM );
 		}
 	}
 
 	private IdContainer createCustomer(SessionFactory sessionFactory, TransactionManager tm)
 			throws Exception {
 		log.debug( "CREATE CUSTOMER" );
 
 		tm.begin();
 
 		try {
 			Session session = sessionFactory.getCurrentSession();
 			Customer customer = new Customer();
 			customer.setName( "JBoss" );
 			Set<Contact> contacts = new HashSet<Contact>();
 
 			Contact kabir = new Contact();
 			kabir.setCustomer( customer );
 			kabir.setName( "Kabir" );
 			kabir.setTlf( "1111" );
 			contacts.add( kabir );
 
 			Contact bill = new Contact();
 			bill.setCustomer( customer );
 			bill.setName( "Bill" );
 			bill.setTlf( "2222" );
 			contacts.add( bill );
 
 			customer.setContacts( contacts );
 
 			session.save( customer );
 			tm.commit();
 
 			IdContainer ids = new IdContainer();
 			ids.customerId = customer.getId();
 			Set contactIds = new HashSet();
 			contactIds.add( kabir.getId() );
 			contactIds.add( bill.getId() );
 			ids.contactIds = contactIds;
 
 			return ids;
 		}
 		catch (Exception e) {
 			log.error( "Caught exception creating customer", e );
 			try {
 				tm.rollback();
 			}
 			catch (Exception e1) {
 				log.error( "Exception rolling back txn", e1 );
 			}
 			throw e;
 		}
 		finally {
 			log.debug( "CREATE CUSTOMER -  END" );
 		}
 	}
 
 	private Customer getCustomer(Integer id, SessionFactory sessionFactory, TransactionManager tm) throws Exception {
 		log.debug( "Find customer with id=" + id );
 		tm.begin();
 		try {
 			Session session = sessionFactory.getCurrentSession();
 			Customer customer = doGetCustomer( id, session, tm );
 			tm.commit();
 			return customer;
 		}
 		catch (Exception e) {
 			try {
 				tm.rollback();
 			}
 			catch (Exception e1) {
 				log.error( "Exception rolling back txn", e1 );
 			}
 			throw e;
 		}
 		finally {
 			log.debug( "Find customer ended." );
 		}
 	}
 
 	private Customer doGetCustomer(Integer id, Session session, TransactionManager tm) throws Exception {
 		Customer customer = (Customer) session.get( Customer.class, id );
 		// Access all the contacts
 		for ( Iterator it = customer.getContacts().iterator(); it.hasNext(); ) {
 			((Contact) it.next()).getName();
 		}
 		return customer;
 	}
 
 	private IdContainer modifyCustomer(Integer id, SessionFactory sessionFactory, TransactionManager tm)
 			throws Exception {
 		log.debug( "Modify customer with id=" + id );
 		tm.begin();
 		try {
 			Session session = sessionFactory.getCurrentSession();
 			IdContainer ids = new IdContainer();
 			Set contactIds = new HashSet();
 			Customer customer = doGetCustomer( id, session, tm );
 			customer.setName( "NewJBoss" );
 			ids.customerId = customer.getId();
 			Set<Contact> contacts = customer.getContacts();
 			for ( Contact c : contacts ) {
 				contactIds.add( c.getId() );
 			}
 			Contact contact = contacts.iterator().next();
 			contacts.remove( contact );
 			contactIds.remove( contact.getId() );
 			ids.contactIds = contactIds;
 			contact.setCustomer( null );
 
 			session.save( customer );
 			tm.commit();
 			return ids;
 		}
 		catch (Exception e) {
 			try {
 				tm.rollback();
 			}
 			catch (Exception e1) {
 				log.error( "Exception rolling back txn", e1 );
 			}
 			throw e;
 		}
 		finally {
 			log.debug( "Find customer ended." );
 		}
 	}
 
 	private void cleanup(SessionFactory sessionFactory, TransactionManager tm) throws Exception {
 		tm.begin();
 		try {
 			Session session = sessionFactory.getCurrentSession();
 			Customer c = (Customer) session.get( Customer.class, CUSTOMER_ID );
 			if ( c != null ) {
 				Set contacts = c.getContacts();
 				for ( Iterator it = contacts.iterator(); it.hasNext(); ) {
 					session.delete( it.next() );
 				}
 				c.setContacts( null );
 				session.delete( c );
 			}
 
 			tm.commit();
 		}
 		catch (Exception e) {
 			try {
 				tm.rollback();
 			}
 			catch (Exception e1) {
 				log.error( "Exception rolling back txn", e1 );
 			}
 			log.error( "Caught exception in cleanup", e );
 		}
 	}
 
 	private void assertLoadedFromCache(MyListener listener, Integer custId, Set contactIds) {
 		assertTrue(
 				"Customer#" + custId + " was in cache", listener.visited.contains(
 				"Customer#"
 						+ custId
 		)
 		);
 		for ( Iterator it = contactIds.iterator(); it.hasNext(); ) {
 			Integer contactId = (Integer) it.next();
 			assertTrue(
 					"Contact#" + contactId + " was in cache", listener.visited.contains(
 					"Contact#"
 							+ contactId
 			)
 			);
 			assertTrue(
 					"Contact#" + contactId + " was in cache", listener.visited.contains(
 					"Contact#"
 							+ contactId
 			)
 			);
 		}
 		assertTrue(
 				"Customer.contacts" + custId + " was in cache", listener.visited
 				.contains( "Customer.contacts#" + custId )
 		);
 	}
 
 	protected int getValidKeyCount(Set keys) {
       return keys.size();
 	}
 
 	@Listener
 	public static class MyListener {
 		private static final Log log = LogFactory.getLog( MyListener.class );
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
 			log.debug( event.toString() );
 			if ( !event.isPre() ) {
-				EntityCacheKey cacheKey = (EntityCacheKey) event.getKey();
-				Integer primKey = (Integer) cacheKey.getKey();
-				String key = cacheKey.getEntityName() + '#' + primKey;
+				String key = String.valueOf(event.getKey());
 				log.debug( "MyListener[" + name + "] - Visiting key " + key );
 				// String name = fqn.toString();
 				String token = ".functional.";
 				int index = key.indexOf( token );
 				if ( index > -1 ) {
 					index += token.length();
 					key = key.substring( index );
 					log.debug( "MyListener[" + name + "] - recording visit to " + key );
 					visited.add( key );
 				}
 			}
 		}
 	}
 
 	private class IdContainer {
 		Integer customerId;
 		Set<Integer> contactIds;
 	}
 
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/NaturalIdInvalidationTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/NaturalIdInvalidationTestCase.java
index 2b1891de0b..dd6099b010 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/NaturalIdInvalidationTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/functional/cluster/NaturalIdInvalidationTestCase.java
@@ -1,284 +1,279 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan.functional.cluster;
 
+import javax.transaction.TransactionManager;
+import java.util.Set;
+import java.util.concurrent.Callable;
+
 import org.hibernate.Criteria;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.Transaction;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.test.cache.infinispan.functional.Citizen;
 import org.hibernate.test.cache.infinispan.functional.NaturalIdOnManyToOne;
 import org.hibernate.test.cache.infinispan.functional.State;
 import org.infinispan.Cache;
 import org.infinispan.manager.CacheContainer;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent;
 import org.infinispan.util.logging.Log;
 import org.infinispan.util.logging.LogFactory;
 import org.jboss.util.collection.ConcurrentSet;
-import org.junit.After;
 import org.junit.Test;
 
-import javax.transaction.TransactionManager;
-import java.util.List;
-import java.util.Set;
-import java.util.concurrent.Callable;
-
-import static org.infinispan.test.TestingUtil.tmpDirectory;
 import static org.infinispan.test.TestingUtil.withTx;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * // TODO: Document this
  *
  * @author Galder Zamarreño
  * @since // TODO
  */
 public class NaturalIdInvalidationTestCase extends DualNodeTestCase {
 
    private static final Log log = LogFactory.getLog(NaturalIdInvalidationTestCase.class);
 
    private static final long SLEEP_TIME = 50l;
    private static final Integer CUSTOMER_ID = new Integer( 1 );
    private static int test = 0;
 
    @Override
    protected Class<?>[] getAnnotatedClasses() {
       return new Class[] {
             Citizen.class, State.class,
             NaturalIdOnManyToOne.class
       };
    }
 
    @Test
    public void testAll() throws Exception {
       log.info( "*** testAll()" );
 
       // Bind a listener to the "local" cache
       // Our region factory makes its CacheManager available to us
       CacheContainer localManager = ClusterAwareRegionFactory.getCacheManager(DualNodeTestCase.LOCAL);
       Cache localNaturalIdCache = localManager.getCache(Citizen.class.getName() + "##NaturalId");
       MyListener localListener = new MyListener( "local" );
       localNaturalIdCache.addListener(localListener);
       TransactionManager localTM = DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestCase.LOCAL);
 
       // Bind a listener to the "remote" cache
       CacheContainer remoteManager = ClusterAwareRegionFactory.getCacheManager(DualNodeTestCase.REMOTE);
       Cache remoteNaturalIdCache = remoteManager.getCache(Citizen.class.getName() + "##NaturalId");
       MyListener remoteListener = new MyListener( "remote" );
       remoteNaturalIdCache.addListener(remoteListener);
       TransactionManager remoteTM = DualNodeJtaTransactionManagerImpl.getInstance(DualNodeTestCase.REMOTE);
       
       SessionFactory localFactory = sessionFactory();
       SessionFactory remoteFactory = secondNodeEnvironment().getSessionFactory();
 
       try {
          assertTrue(remoteListener.isEmpty());
          assertTrue(localListener.isEmpty());
 
          saveSomeCitizens(localTM, localFactory);
 
          assertTrue(remoteListener.isEmpty());
          assertTrue(localListener.isEmpty());
 
          // Sleep a bit to let async commit propagate. Really just to
          // help keep the logs organized for debugging any issues
          sleep( SLEEP_TIME );
 
          log.debug("Find node 0");
          // This actually brings the collection into the cache
          getCitizenWithCriteria(localTM, localFactory);
 
          sleep( SLEEP_TIME );
          // Now the collection is in the cache so, the 2nd "get"
          // should read everything from the cache
          log.debug( "Find(2) node 0" );
          localListener.clear();
          getCitizenWithCriteria(localTM, localFactory);
 
          // Check the read came from the cache
          log.debug( "Check cache 0" );
          assertLoadedFromCache(localListener, "1234");
 
          log.debug( "Find node 1" );
          // This actually brings the collection into the cache since invalidation is in use
          getCitizenWithCriteria(remoteTM, remoteFactory);
 
          // Now the collection is in the cache so, the 2nd "get"
          // should read everything from the cache
          log.debug( "Find(2) node 1" );
          remoteListener.clear();
          getCitizenWithCriteria(remoteTM, remoteFactory);
 
          // Check the read came from the cache
          log.debug( "Check cache 1" );
          assertLoadedFromCache(remoteListener, "1234");
 
          // Modify customer in remote
          remoteListener.clear();
          deleteCitizenWithCriteria(remoteTM, remoteFactory);
          sleep(250);
 
          Set localKeys = localNaturalIdCache.keySet();
          assertEquals(1, localKeys.size());
          // Only key left is the one for the citizen *not* in France
          localKeys.toString().contains("000");
       }
       catch (Exception e) {
          log.error("Error", e);
          throw e;
       } finally {
          withTx(localTM, new Callable<Void>() {
             @Override
             public Void call() throws Exception {
                Session s = sessionFactory().openSession();
                s.beginTransaction();
                s.createQuery( "delete NaturalIdOnManyToOne" ).executeUpdate();
                s.createQuery( "delete Citizen" ).executeUpdate();
                s.createQuery( "delete State" ).executeUpdate();
                s.getTransaction().commit();
                s.close();
                return null;
             }
          });
       }
    }
 
    private void assertLoadedFromCache(MyListener localListener, String id) {
       for (String visited : localListener.visited){
          if (visited.contains(id))
             return;
       }
       fail("Citizen (" + id + ") should have present in the cache");
    }
 
    private void saveSomeCitizens(TransactionManager tm, final SessionFactory sf) throws Exception {
       final Citizen c1 = new Citizen();
       c1.setFirstname( "Emmanuel" );
       c1.setLastname( "Bernard" );
       c1.setSsn( "1234" );
 
       final State france = new State();
       france.setName( "Ile de France" );
       c1.setState( france );
 
       final Citizen c2 = new Citizen();
       c2.setFirstname( "Gavin" );
       c2.setLastname( "King" );
       c2.setSsn( "000" );
       final State australia = new State();
       australia.setName( "Australia" );
       c2.setState( australia );
 
       withTx(tm, new Callable<Void>() {
          @Override
          public Void call() throws Exception {
             Session s = sf.openSession();
             Transaction tx = s.beginTransaction();
             s.persist( australia );
             s.persist( france );
             s.persist( c1 );
             s.persist( c2 );
             tx.commit();
             s.close();
             return null;
          }
       });
    }
 
    private void getCitizenWithCriteria(TransactionManager tm, final SessionFactory sf) throws Exception {
       withTx(tm, new Callable<Void >() {
          @Override
          public Void call() throws Exception {
             Session s = sf.openSession();
             Transaction tx = s.beginTransaction();
             State france = getState(s, "Ile de France");
             Criteria criteria = s.createCriteria( Citizen.class );
             criteria.add( Restrictions.naturalId().set( "ssn", "1234" ).set( "state", france ) );
             criteria.setCacheable( true );
             criteria.list();
             // cleanup
             tx.commit();
             s.close();
             return null;
          }
       });
    }
 
    private void deleteCitizenWithCriteria(TransactionManager tm, final SessionFactory sf) throws Exception {
       withTx(tm, new Callable<Void >() {
          @Override
          public Void call() throws Exception {
             Session s = sf.openSession();
             Transaction tx = s.beginTransaction();
             State france = getState(s, "Ile de France");
             Criteria criteria = s.createCriteria( Citizen.class );
             criteria.add( Restrictions.naturalId().set( "ssn", "1234" ).set( "state", france ) );
             criteria.setCacheable( true );
             Citizen c = (Citizen) criteria.uniqueResult();
             s.delete(c);
             // cleanup
             tx.commit();
             s.close();
             return null;
          }
       });
    }
 
    private State getState(Session s, String name) {
       Criteria criteria = s.createCriteria( State.class );
       criteria.add( Restrictions.eq("name", name) );
       criteria.setCacheable(true);
       return (State) criteria.list().get( 0 );
    }
 
    @Listener
    public static class MyListener {
       private static final Log log = LogFactory.getLog( MyListener.class );
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
          log.debug( event.toString() );
          if ( !event.isPre() ) {
-            NaturalIdCacheKey cacheKey = (NaturalIdCacheKey) event.getKey();
-            visited.add(cacheKey.toString());
+            visited.add(event.getKey().toString());
 //            Integer primKey = (Integer) cacheKey.getKey();
 //            String key = (String) cacheKey.getEntityOrRoleName() + '#' + primKey;
 //            log.debug( "MyListener[" + name + "] - Visiting key " + key );
 //            // String name = fqn.toString();
 //            String token = ".functional.";
 //            int index = key.indexOf( token );
 //            if ( index > -1 ) {
 //               index += token.length();
 //               key = key.substring( index );
 //               log.debug( "MyListener[" + name + "] - recording visit to " + key );
 //               visited.add( key );
 //            }
          }
       }
    }
 
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/TestingKeyFactory.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/TestingKeyFactory.java
index 61b3dbe14f..26c695d8b1 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/TestingKeyFactory.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/util/TestingKeyFactory.java
@@ -1,84 +1,61 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.cache.infinispan.util;
 
 import java.io.Serializable;
 
-import org.hibernate.cache.spi.CollectionCacheKey;
-import org.hibernate.cache.spi.EntityCacheKey;
-
 public class TestingKeyFactory {
 
 	private TestingKeyFactory() {
 		//Not to be constructed
 	}
 
-	public static EntityCacheKey generateEntityCacheKey(String id) {
+	public static Object generateEntityCacheKey(String id) {
 		return new TestingEntityCacheKey( id );
 	}
 
-	public static CollectionCacheKey generateCollectionCacheKey(String id) {
+	public static Object generateCollectionCacheKey(String id) {
 		return new TestingEntityCacheKey( id );
 	}
 
 	//For convenience implement both interfaces.
-	private static class TestingEntityCacheKey implements EntityCacheKey, CollectionCacheKey, Serializable {
+	private static class TestingEntityCacheKey implements Serializable {
 
 		private final String id;
 
 		public TestingEntityCacheKey(String id) {
 			this.id = id;
 		}
 
 		@Override
-		public Serializable getKey() {
-			return null;
-		}
-
-		@Override
-		public String getEntityName() {
-			return null;
-		}
-
-		@Override
-		public String getCollectionRole() {
-			return null;
-		}
-
-		@Override
-		public String getTenantId() {
-			return null;
-		}
-
-		@Override
 		public int hashCode() {
 			final int prime = 31;
 			int result = 1;
 			result = prime * result + ((id == null) ? 0 : id.hashCode());
 			return result;
 		}
 
 		@Override
 		public boolean equals(Object obj) {
 			if (this == obj)
 				return true;
 			if (obj == null)
 				return false;
 			if (getClass() != obj.getClass())
 				return false;
 			TestingEntityCacheKey other = (TestingEntityCacheKey) obj;
 			if (id == null) {
 				if (other.id != null)
 					return false;
 			} else if (!id.equals(other.id))
 				return false;
 			return true;
 		}
 
 	}
 
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java
index e62c826ad4..ec2c2713cf 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/AbstractReadWriteAccessStrategy.java
@@ -1,367 +1,366 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.UUID;
 import java.util.concurrent.atomic.AtomicLong;
 import java.util.concurrent.locks.ReentrantReadWriteLock;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Strong Liu
  */
-abstract class AbstractReadWriteAccessStrategy<T extends CacheKey> extends BaseRegionAccessStrategy<T> {
+abstract class AbstractReadWriteAccessStrategy extends BaseRegionAccessStrategy {
 	private static final Logger LOG = Logger.getLogger( AbstractReadWriteAccessStrategy.class.getName() );
 
 	private final UUID uuid = UUID.randomUUID();
 	private final AtomicLong nextLockId = new AtomicLong();
 	private ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
 	protected java.util.concurrent.locks.Lock readLock = reentrantReadWriteLock.readLock();
 	protected java.util.concurrent.locks.Lock writeLock = reentrantReadWriteLock.writeLock();
 
 	/**
 	 * Returns <code>null</code> if the item is not readable.  Locked items are not readable, nor are items created
 	 * after the start of this transaction.
 	 */
 	@Override
-	public final Object get(T key, long txTimestamp) throws CacheException {
+	public final Object get(Object key, long txTimestamp) throws CacheException {
 		LOG.debugf( "getting key[%s] from region[%s]", key, getInternalRegion().getName() );
 		try {
 			readLock.lock();
 			Lockable item = (Lockable) getInternalRegion().get( key );
 
 			boolean readable = item != null && item.isReadable( txTimestamp );
 			if ( readable ) {
 				LOG.debugf( "hit key[%s] in region[%s]", key, getInternalRegion().getName() );
 				return item.getValue();
 			}
 			else {
 				if ( item == null ) {
 					LOG.debugf( "miss key[%s] in region[%s]", key, getInternalRegion().getName() );
 				}
 				else {
 					LOG.debugf( "hit key[%s] in region[%s], but it is unreadable", key, getInternalRegion().getName() );
 				}
 				return null;
 			}
 		}
 		finally {
 			readLock.unlock();
 		}
 	}
 
 	abstract Comparator getVersionComparator();
 
 	/**
 	 * Returns <code>false</code> and fails to put the value if there is an existing un-writeable item mapped to this
 	 * key.
 	 */
 	@Override
 	public final boolean putFromLoad(
-			T key,
+			Object key,
 			Object value,
 			long txTimestamp,
 			Object version,
 			boolean minimalPutOverride)
 			throws CacheException {
 		try {
 			LOG.debugf( "putting key[%s] -> value[%s] into region[%s]", key, value, getInternalRegion().getName() );
 			writeLock.lock();
 			Lockable item = (Lockable) getInternalRegion().get( key );
 			boolean writeable = item == null || item.isWriteable( txTimestamp, version, getVersionComparator() );
 			if ( writeable ) {
 				LOG.debugf(
 						"putting key[%s] -> value[%s] into region[%s] success",
 						key,
 						value,
 						getInternalRegion().getName()
 				);
 				getInternalRegion().put( key, new Item( value, version, getInternalRegion().nextTimestamp() ) );
 				return true;
 			}
 			else {
 				LOG.debugf(
 						"putting key[%s] -> value[%s] into region[%s] fail due to it is unwriteable",
 						key,
 						value,
 						getInternalRegion().getName()
 				);
 				return false;
 			}
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 	/**
 	 * Soft-lock a cache item.
 	 */
 	@Override
-	public final SoftLock lockItem(T key, Object version) throws CacheException {
+	public final SoftLock lockItem(Object key, Object version) throws CacheException {
 
 		try {
 			LOG.debugf( "locking key[%s] in region[%s]", key, getInternalRegion().getName() );
 			writeLock.lock();
 			Lockable item = (Lockable) getInternalRegion().get( key );
 			long timeout = getInternalRegion().nextTimestamp() + getInternalRegion().getTimeout();
 			final Lock lock = ( item == null ) ? new Lock( timeout, uuid, nextLockId(), version ) : item.lock(
 					timeout,
 					uuid,
 					nextLockId()
 			);
 			getInternalRegion().put( key, lock );
 			return lock;
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 	/**
 	 * Soft-unlock a cache item.
 	 */
 	@Override
-	public final void unlockItem(T key, SoftLock lock) throws CacheException {
+	public final void unlockItem(Object key, SoftLock lock) throws CacheException {
 
 		try {
 			LOG.debugf( "unlocking key[%s] in region[%s]", key, getInternalRegion().getName() );
 			writeLock.lock();
 			Lockable item = (Lockable) getInternalRegion().get( key );
 
 			if ( ( item != null ) && item.isUnlockable( lock ) ) {
 				decrementLock( key, (Lock) item );
 			}
 			else {
 				handleLockExpiry( key, item );
 			}
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 	private long nextLockId() {
 		return nextLockId.getAndIncrement();
 	}
 
 	/**
 	 * Unlock and re-put the given key, lock combination.
 	 */
 	protected void decrementLock(Object key, Lock lock) {
 		lock.unlock( getInternalRegion().nextTimestamp() );
 		getInternalRegion().put( key, lock );
 	}
 
 	/**
 	 * Handle the timeout of a previous lock mapped to this key
 	 */
 	protected void handleLockExpiry(Object key, Lockable lock) {
 		LOG.info( "Cached entry expired : " + key );
 
 		long ts = getInternalRegion().nextTimestamp() + getInternalRegion().getTimeout();
 		// create new lock that times out immediately
 		Lock newLock = new Lock( ts, uuid, nextLockId.getAndIncrement(), null );
 		newLock.unlock( ts );
 		getInternalRegion().put( key, newLock );
 	}
 
 	/**
 	 * Interface type implemented by all wrapper objects in the cache.
 	 */
 	protected interface Lockable {
 
 		/**
 		 * Returns <code>true</code> if the enclosed value can be read by a transaction started at the given time.
 		 */
 		boolean isReadable(long txTimestamp);
 
 		/**
 		 * Returns <code>true</code> if the enclosed value can be replaced with one of the given version by a
 		 * transaction started at the given time.
 		 */
 		boolean isWriteable(long txTimestamp, Object version, Comparator versionComparator);
 
 		/**
 		 * Returns the enclosed value.
 		 */
 		Object getValue();
 
 		/**
 		 * Returns <code>true</code> if the given lock can be unlocked using the given SoftLock instance as a handle.
 		 */
 		boolean isUnlockable(SoftLock lock);
 
 		/**
 		 * Locks this entry, stamping it with the UUID and lockId given, with the lock timeout occuring at the specified
 		 * time.  The returned Lock object can be used to unlock the entry in the future.
 		 */
 		Lock lock(long timeout, UUID uuid, long lockId);
 	}
 
 	/**
 	 * Wrapper type representing unlocked items.
 	 */
 	protected final static class Item implements Serializable, Lockable {
 
 		private static final long serialVersionUID = 1L;
 		private final Object value;
 		private final Object version;
 		private final long timestamp;
 
 		/**
 		 * Creates an unlocked item wrapping the given value with a version and creation timestamp.
 		 */
 		Item(Object value, Object version, long timestamp) {
 			this.value = value;
 			this.version = version;
 			this.timestamp = timestamp;
 		}
 
 		@Override
 		public boolean isReadable(long txTimestamp) {
 			return txTimestamp > timestamp;
 		}
 
 		@Override
 		public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
 			return version != null && versionComparator.compare( version, newVersion ) < 0;
 		}
 
 		@Override
 		public Object getValue() {
 			return value;
 		}
 
 		@Override
 		public boolean isUnlockable(SoftLock lock) {
 			return false;
 		}
 
 		@Override
 		public Lock lock(long timeout, UUID uuid, long lockId) {
 			return new Lock( timeout, uuid, lockId, version );
 		}
 	}
 
 	/**
 	 * Wrapper type representing locked items.
 	 */
 	protected final static class Lock implements Serializable, Lockable, SoftLock {
 
 		private static final long serialVersionUID = 2L;
 
 		private final UUID sourceUuid;
 		private final long lockId;
 		private final Object version;
 
 		private long timeout;
 		private boolean concurrent;
 		private int multiplicity = 1;
 		private long unlockTimestamp;
 
 		/**
 		 * Creates a locked item with the given identifiers and object version.
 		 */
 		Lock(long timeout, UUID sourceUuid, long lockId, Object version) {
 			this.timeout = timeout;
 			this.lockId = lockId;
 			this.version = version;
 			this.sourceUuid = sourceUuid;
 		}
 
 		@Override
 		public boolean isReadable(long txTimestamp) {
 			return false;
 		}
 
 		@Override
 		public boolean isWriteable(long txTimestamp, Object newVersion, Comparator versionComparator) {
 			if ( txTimestamp > timeout ) {
 				// if timedout then allow write
 				return true;
 			}
 			if ( multiplicity > 0 ) {
 				// if still locked then disallow write
 				return false;
 			}
 			return version == null ? txTimestamp > unlockTimestamp : versionComparator.compare(
 					version,
 					newVersion
 			) < 0;
 		}
 
 		@Override
 		public Object getValue() {
 			return null;
 		}
 
 		@Override
 		public boolean isUnlockable(SoftLock lock) {
 			return equals( lock );
 		}
 
 		@Override
 		public boolean equals(Object o) {
 			if ( o == this ) {
 				return true;
 			}
 			else if ( o instanceof Lock ) {
 				return ( lockId == ( (Lock) o ).lockId ) && sourceUuid.equals( ( (Lock) o ).sourceUuid );
 			}
 			else {
 				return false;
 			}
 		}
 
 		@Override
 		public int hashCode() {
 			int hash = ( sourceUuid != null ? sourceUuid.hashCode() : 0 );
 			int temp = (int) lockId;
 			for ( int i = 1; i < Long.SIZE / Integer.SIZE; i++ ) {
 				temp ^= ( lockId >>> ( i * Integer.SIZE ) );
 			}
 			return hash + temp;
 		}
 
 		/**
 		 * Returns true if this Lock has been concurrently locked by more than one transaction.
 		 */
 		public boolean wasLockedConcurrently() {
 			return concurrent;
 		}
 
 		@Override
 		public Lock lock(long timeout, UUID uuid, long lockId) {
 			concurrent = true;
 			multiplicity++;
 			this.timeout = timeout;
 			return this;
 		}
 
 		/**
 		 * Unlocks this Lock, and timestamps the unlock event.
 		 */
 		public void unlock(long timestamp) {
 			if ( --multiplicity == 0 ) {
 				unlockTimestamp = timestamp;
 			}
 		}
 
 		@Override
 		public String toString() {
 			return "Lock Source-UUID:" + sourceUuid + " Lock-ID:" + lockId;
 		}
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseCollectionRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseCollectionRegionAccessStrategy.java
index 20884b0703..973b7dd501 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseCollectionRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseCollectionRegionAccessStrategy.java
@@ -1,49 +1,51 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
-import java.io.Serializable;
-
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
-import org.hibernate.cache.spi.CollectionCacheKey;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * @author Strong Liu
  */
-class BaseCollectionRegionAccessStrategy extends BaseRegionAccessStrategy<CollectionCacheKey> implements CollectionRegionAccessStrategy {
+class BaseCollectionRegionAccessStrategy extends BaseRegionAccessStrategy implements CollectionRegionAccessStrategy {
 
 	private final CollectionRegionImpl region;
 
 	BaseCollectionRegionAccessStrategy(CollectionRegionImpl region) {
 		this.region = region;
 	}
 
 	@Override
 	protected BaseGeneralDataRegion getInternalRegion() {
 		return region;
 	}
 
 	@Override
 	protected boolean isDefaultMinimalPutOverride() {
 		return region.getSettings().isMinimalPutsEnabled();
 	}
 
 	@Override
 	public CollectionRegion getRegion() {
 		return region;
 	}
 
 	@Override
-	public CollectionCacheKey generateCacheKey(Serializable id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+	public Object generateCacheKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
 		return DefaultCacheKeysFactory.createCollectionKey( id, persister, factory, tenantIdentifier );
 	}
 
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getCollectionId(cacheKey);
+	}
+
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseEntityRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseEntityRegionAccessStrategy.java
index 75849b7dea..57023e59ba 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseEntityRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseEntityRegionAccessStrategy.java
@@ -1,73 +1,74 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
-import java.io.Serializable;
-
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * @author Strong Liu
  */
-class BaseEntityRegionAccessStrategy extends BaseRegionAccessStrategy<EntityCacheKey> implements EntityRegionAccessStrategy {
+class BaseEntityRegionAccessStrategy extends BaseRegionAccessStrategy implements EntityRegionAccessStrategy {
 
 	private final EntityRegionImpl region;
 
 	BaseEntityRegionAccessStrategy(EntityRegionImpl region) {
 		this.region = region;
 	}
 
 	@Override
 	public EntityRegion getRegion() {
 		return region;
 	}
 
 	@Override
-	public boolean insert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean insert(Object key, Object value, Object version) throws CacheException {
 		return putFromLoad( key, value, 0, version );
 	}
 
 	@Override
-	public boolean afterInsert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
 		return true;
 	}
 
 	@Override
-	public boolean update(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion)
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
 			throws CacheException {
 		return false;
 	}
 
 	@Override
-	public boolean afterUpdate(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
 			throws CacheException {
 		return false;
 	}
 
 	@Override
 	protected BaseGeneralDataRegion getInternalRegion() {
 		return region;
 	}
 
 	@Override
 	protected boolean isDefaultMinimalPutOverride() {
 		return region.getSettings().isMinimalPutsEnabled();
 	}
 
 	@Override
-	public EntityCacheKey generateCacheKey(Serializable id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+	public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
 		return DefaultCacheKeysFactory.createEntityKey( id, persister, factory, tenantIdentifier );
 	}
 
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getEntityId(cacheKey);
+	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseNaturalIdRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseNaturalIdRegionAccessStrategy.java
index e360085b8f..560b46a31f 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseNaturalIdRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseNaturalIdRegionAccessStrategy.java
@@ -1,71 +1,71 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
-import java.io.Serializable;
-
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.type.Type;
 
 /**
  * @author Eric Dalquist
  */
-class BaseNaturalIdRegionAccessStrategy extends BaseRegionAccessStrategy<NaturalIdCacheKey> implements NaturalIdRegionAccessStrategy {
+class BaseNaturalIdRegionAccessStrategy extends BaseRegionAccessStrategy implements NaturalIdRegionAccessStrategy {
 	private final NaturalIdRegionImpl region;
 
 	@Override
 	protected BaseGeneralDataRegion getInternalRegion() {
 		return region;
 	}
 
 	@Override
 	protected boolean isDefaultMinimalPutOverride() {
 		return region.getSettings().isMinimalPutsEnabled();
 	}
 
 	@Override
 	public NaturalIdRegion getRegion() {
 		return region;
 	}
 
 	@Override
-	public boolean insert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean insert(Object key, Object value) throws CacheException {
 		return putFromLoad( key, value, 0, null );
 	}
 
 	@Override
-	public boolean afterInsert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean afterInsert(Object key, Object value) throws CacheException {
 		return false;
 	}
 
 	@Override
-	public boolean update(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean update(Object key, Object value) throws CacheException {
 		return putFromLoad( key, value, 0, null );
 	}
 
 	@Override
-	public boolean afterUpdate(NaturalIdCacheKey key, Object value, SoftLock lock) throws CacheException {
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
 		return false;
 	}
 
 	BaseNaturalIdRegionAccessStrategy(NaturalIdRegionImpl region) {
 		this.region = region;
 	}
 
 	@Override
-	public NaturalIdCacheKey generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
+	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
 		return DefaultCacheKeysFactory.createNaturalIdKey( naturalIdValues, persister, session );
 	}
+
+	@Override
+	public Object[] getNaturalIdValues(Object cacheKey) {
+		return DefaultCacheKeysFactory.getNaturalIdValues(cacheKey);
+	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegionAccessStrategy.java
index a8b6ab707b..7b277e026f 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/BaseRegionAccessStrategy.java
@@ -1,118 +1,117 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.jboss.logging.Logger;
 
 /**
  * @author Strong Liu
  */
-abstract class BaseRegionAccessStrategy<T extends CacheKey> implements RegionAccessStrategy<T> {
+abstract class BaseRegionAccessStrategy implements RegionAccessStrategy {
 
 	private static final Logger LOG = Logger.getLogger( BaseRegionAccessStrategy.class );
 
 	protected abstract BaseGeneralDataRegion getInternalRegion();
 
 	protected abstract boolean isDefaultMinimalPutOverride();
 
 	@Override
-	public Object get(T key, long txTimestamp) throws CacheException {
+	public Object get(Object key, long txTimestamp) throws CacheException {
 		return getInternalRegion().get( key );
 	}
 
 	@Override
-	public boolean putFromLoad(T key, Object value, long txTimestamp, Object version) throws CacheException {
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version) throws CacheException {
 		return putFromLoad( key, value, txTimestamp, version, isDefaultMinimalPutOverride() );
 	}
 
 	@Override
-	public boolean putFromLoad(T key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
+	public boolean putFromLoad(Object key, Object value, long txTimestamp, Object version, boolean minimalPutOverride)
 			throws CacheException {
 
 		if ( key == null || value == null ) {
 			return false;
 		}
 		if ( minimalPutOverride && getInternalRegion().contains( key ) ) {
 			LOG.debugf( "Item already cached: %s", key );
 			return false;
 		}
 		LOG.debugf( "Caching: %s", key );
 		getInternalRegion().put( key, value );
 		return true;
 
 	}
 
 	/**
 	 * Region locks are not supported.
 	 *
 	 * @return <code>null</code>
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#lockRegion()
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#lockRegion()
 	 */
 	@Override
 	public SoftLock lockRegion() throws CacheException {
 		return null;
 	}
 
 	/**
 	 * Region locks are not supported - perform a cache clear as a precaution.
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#unlockRegion(org.hibernate.cache.spi.access.SoftLock)
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#unlockRegion(org.hibernate.cache.spi.access.SoftLock)
 	 */
 	@Override
 	public void unlockRegion(SoftLock lock) throws CacheException {
 		evictAll();
 	}
 
 	@Override
-	public SoftLock lockItem(T key, Object version) throws CacheException {
+	public SoftLock lockItem(Object key, Object version) throws CacheException {
 		return null;
 	}
 
 	@Override
-	public void unlockItem(T key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 	}
 
 
 	/**
 	 * A no-op since this is an asynchronous cache access strategy.
 	 *
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#remove(java.lang.Object)
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#remove(java.lang.Object)
 	 */
 	@Override
-	public void remove(T key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 	}
 
 	/**
 	 * Called to evict data from the entire region
 	 *
 	 * @throws CacheException Propogated from underlying {@link org.hibernate.cache.spi.Region}
 	 * @see org.hibernate.cache.spi.access.EntityRegionAccessStrategy#removeAll()
 	 * @see org.hibernate.cache.spi.access.CollectionRegionAccessStrategy#removeAll()
 	 */
 	@Override
 	public void removeAll() throws CacheException {
 		evictAll();
 	}
 
 	@Override
-	public void evict(T key) throws CacheException {
+	public void evict(Object key) throws CacheException {
 		getInternalRegion().evict( key );
 	}
 
 	@Override
 	public void evictAll() throws CacheException {
 		getInternalRegion().evictAll();
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteCollectionRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteCollectionRegionAccessStrategy.java
index 58a288ca29..4b261979a1 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteCollectionRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteCollectionRegionAccessStrategy.java
@@ -1,30 +1,29 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.CollectionCacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 
 /**
  * @author Strong Liu
  */
 class NonstrictReadWriteCollectionRegionAccessStrategy extends BaseCollectionRegionAccessStrategy {
 	NonstrictReadWriteCollectionRegionAccessStrategy(CollectionRegionImpl region) {
 		super( region );
 	}
 
 	@Override
-	public void unlockItem(CollectionCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		evict( key );
 	}
 
 	@Override
-	public void remove(CollectionCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		evict( key );
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteEntityRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteEntityRegionAccessStrategy.java
index 6435c9356f..70c9c8623d 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteEntityRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteEntityRegionAccessStrategy.java
@@ -1,66 +1,65 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 
 /**
  * @author Strong Liu
  */
 class NonstrictReadWriteEntityRegionAccessStrategy extends BaseEntityRegionAccessStrategy {
 	NonstrictReadWriteEntityRegionAccessStrategy(EntityRegionImpl region) {
 		super( region );
 	}
 
 	/**
 	 * Since this is a non-strict read/write strategy item locking is not used.
 	 */
 	@Override
-	public void unlockItem(EntityCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		evict( key );
 	}
 
 	/**
 	 * Returns <code>false</code> since this is an asynchronous cache access strategy.
 	 */
 	@Override
-	public boolean insert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean insert(Object key, Object value, Object version) throws CacheException {
 		return false;
 	}
 
 	/**
 	 * Returns <code>false</code> since this is a non-strict read/write cache access strategy
 	 */
 	@Override
-	public boolean afterInsert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
 		return false;
 	}
 
 	/**
 	 * Removes the entry since this is a non-strict read/write cache strategy.
 	 */
 	@Override
-	public boolean update(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion)
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
 			throws CacheException {
 		evict( key );
 		return false;
 	}
 
 	@Override
-	public boolean afterUpdate(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
 			throws CacheException {
 		unlockItem( key, lock );
 		return false;
 	}
 
 	@Override
-	public void remove(EntityCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		evict( key );
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteNaturalIdRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteNaturalIdRegionAccessStrategy.java
index 830d37269e..3f23270588 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteNaturalIdRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/NonstrictReadWriteNaturalIdRegionAccessStrategy.java
@@ -1,46 +1,45 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 
 /**
  * @author Eric Dalquist
  */
 class NonstrictReadWriteNaturalIdRegionAccessStrategy extends BaseNaturalIdRegionAccessStrategy {
 	NonstrictReadWriteNaturalIdRegionAccessStrategy(NaturalIdRegionImpl region) {
 		super( region );
 	}
 
 	@Override
-	public void unlockItem(NaturalIdCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		evict( key );
 	}
 
 	@Override
-	public void remove(NaturalIdCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		evict( key );
 	}
 
 	@Override
-	public boolean insert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean insert(Object key, Object value) throws CacheException {
 		return false;
 	}
 
 	@Override
-	public boolean afterInsert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean afterInsert(Object key, Object value) throws CacheException {
 		return false;
 	}
 
 	@Override
-	public boolean update(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean update(Object key, Object value) throws CacheException {
 		remove( key );
 		return false;
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyEntityRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyEntityRegionAccessStrategy.java
index a3aa01ba1f..18bfb5d5bf 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyEntityRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyEntityRegionAccessStrategy.java
@@ -1,69 +1,68 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.jboss.logging.Logger;
 
 /**
  * @author Strong Liu
  */
 class ReadOnlyEntityRegionAccessStrategy extends BaseEntityRegionAccessStrategy {
 	private static final Logger LOG = Logger.getLogger( ReadOnlyEntityRegionAccessStrategy.class );
 
 
 	ReadOnlyEntityRegionAccessStrategy(EntityRegionImpl region) {
 		super( region );
 	}
 
 	/**
 	 * This cache is asynchronous hence a no-op
 	 */
 	@Override
-	public boolean insert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean insert(Object key, Object value, Object version) throws CacheException {
 		return false; //wait until tx complete, see afterInsert().
 	}
 
 	@Override
-	public boolean afterInsert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
 		getInternalRegion().put( key, value ); //save into cache since the tx is completed
 		return true;
 	}
 
 	@Override
-	public void unlockItem(EntityCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		evict( key );
 	}
 
 	/**
 	 * Throws UnsupportedOperationException since this cache is read-only
 	 *
 	 * @throws UnsupportedOperationException always
 	 */
 	@Override
-	public boolean update(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion)
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
 			throws CacheException {
 		LOG.info( "Illegal attempt to update item cached as read-only : " + key );
 		throw new UnsupportedOperationException( "Can't write to a readonly object" );
 	}
 
 	/**
 	 * Throws UnsupportedOperationException since this cache is read-only
 	 *
 	 * @throws UnsupportedOperationException always
 	 */
 	@Override
-	public boolean afterUpdate(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
 			throws CacheException {
 		LOG.info( "Illegal attempt to update item cached as read-only : " + key );
 		throw new UnsupportedOperationException( "Can't write to a readonly object" );
 	}
 
 
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyNaturalIdRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyNaturalIdRegionAccessStrategy.java
index 3b3a064c17..93f3b9b7ec 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyNaturalIdRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadOnlyNaturalIdRegionAccessStrategy.java
@@ -1,25 +1,24 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 
 /**
  * @author Eric Dalquist
  */
 class ReadOnlyNaturalIdRegionAccessStrategy extends BaseNaturalIdRegionAccessStrategy {
 	ReadOnlyNaturalIdRegionAccessStrategy(NaturalIdRegionImpl region) {
 		super( region );
 	}
 
 	@Override
-	public void unlockItem(NaturalIdCacheKey key, SoftLock lock) throws CacheException {
+	public void unlockItem(Object key, SoftLock lock) throws CacheException {
 		evict( key );
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteCollectionRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteCollectionRegionAccessStrategy.java
index ed4389f84d..d73eeb81d7 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteCollectionRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteCollectionRegionAccessStrategy.java
@@ -1,56 +1,58 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
-import java.io.Serializable;
 import java.util.Comparator;
 
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
-import org.hibernate.cache.spi.CollectionCacheKey;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * @author Strong Liu
  */
-class ReadWriteCollectionRegionAccessStrategy extends AbstractReadWriteAccessStrategy<CollectionCacheKey>
+class ReadWriteCollectionRegionAccessStrategy extends AbstractReadWriteAccessStrategy
 		implements CollectionRegionAccessStrategy {
 
 	private final CollectionRegionImpl region;
 
 	ReadWriteCollectionRegionAccessStrategy(CollectionRegionImpl region) {
 		this.region = region;
 	}
 
 	@Override
 	Comparator getVersionComparator() {
 		return region.getCacheDataDescription().getVersionComparator();
 	}
 
 	@Override
 	protected BaseGeneralDataRegion getInternalRegion() {
 		return region;
 	}
 
 	@Override
 	protected boolean isDefaultMinimalPutOverride() {
 		return region.getSettings().isMinimalPutsEnabled();
 	}
 
 	@Override
 	public CollectionRegion getRegion() {
 		return region;
 	}
 
 	@Override
-	public CollectionCacheKey generateCacheKey(Serializable id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+	public Object generateCacheKey(Object id, CollectionPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
 		return DefaultCacheKeysFactory.createCollectionKey( id, persister, factory, tenantIdentifier );
 	}
 
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getCollectionId(cacheKey);
+	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteEntityRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteEntityRegionAccessStrategy.java
index 382f98dbb9..42283092b7 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteEntityRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteEntityRegionAccessStrategy.java
@@ -1,117 +1,119 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
-import java.io.Serializable;
 import java.util.Comparator;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * @author Strong Liu
  */
-class ReadWriteEntityRegionAccessStrategy extends AbstractReadWriteAccessStrategy<EntityCacheKey>
+class ReadWriteEntityRegionAccessStrategy extends AbstractReadWriteAccessStrategy
 		implements EntityRegionAccessStrategy {
 	private final EntityRegionImpl region;
 
 	ReadWriteEntityRegionAccessStrategy(EntityRegionImpl region) {
 		this.region = region;
 	}
 
 	@Override
-	public boolean insert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean insert(Object key, Object value, Object version) throws CacheException {
 		return false;
 	}
 
 	@Override
-	public boolean update(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion)
+	public boolean update(Object key, Object value, Object currentVersion, Object previousVersion)
 			throws CacheException {
 		return false;
 	}
 
 	@Override
-	public boolean afterInsert(EntityCacheKey key, Object value, Object version) throws CacheException {
+	public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
 
 		try {
 			writeLock.lock();
 			Lockable item = (Lockable) region.get( key );
 			if ( item == null ) {
 				region.put( key, new Item( value, version, region.nextTimestamp() ) );
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 
 	@Override
-	public boolean afterUpdate(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock)
 			throws CacheException {
 		try {
 			writeLock.lock();
 			Lockable item = (Lockable) region.get( key );
 
 			if ( item != null && item.isUnlockable( lock ) ) {
 				Lock lockItem = (Lock) item;
 				if ( lockItem.wasLockedConcurrently() ) {
 					decrementLock( key, lockItem );
 					return false;
 				}
 				else {
 					region.put( key, new Item( value, currentVersion, region.nextTimestamp() ) );
 					return true;
 				}
 			}
 			else {
 				handleLockExpiry( key, item );
 				return false;
 			}
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 
 	@Override
 	protected BaseGeneralDataRegion getInternalRegion() {
 		return region;
 	}
 
 	@Override
 	protected boolean isDefaultMinimalPutOverride() {
 		return region.getSettings().isMinimalPutsEnabled();
 	}
 
 	@Override
 	Comparator getVersionComparator() {
 		return region.getCacheDataDescription().getVersionComparator();
 	}
 
 	@Override
 	public EntityRegion getRegion() {
 		return region;
 	}
 
 	@Override
-	public EntityCacheKey generateCacheKey(Serializable id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
+	public Object generateCacheKey(Object id, EntityPersister persister, SessionFactoryImplementor factory, String tenantIdentifier) {
 		return DefaultCacheKeysFactory.createEntityKey( id, persister, factory, tenantIdentifier );
 	}
 
+	@Override
+	public Object getCacheKeyId(Object cacheKey) {
+		return DefaultCacheKeysFactory.getEntityId(cacheKey);
+	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteNaturalIdRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteNaturalIdRegionAccessStrategy.java
index 2cb429c6d6..caefe030e7 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteNaturalIdRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/ReadWriteNaturalIdRegionAccessStrategy.java
@@ -1,113 +1,117 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import java.util.Comparator;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.internal.DefaultCacheKeysFactory;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * @author Eric Dalquist
  */
-class ReadWriteNaturalIdRegionAccessStrategy extends AbstractReadWriteAccessStrategy<NaturalIdCacheKey>
+class ReadWriteNaturalIdRegionAccessStrategy extends AbstractReadWriteAccessStrategy
 		implements NaturalIdRegionAccessStrategy {
 
 	private final NaturalIdRegionImpl region;
 
 	ReadWriteNaturalIdRegionAccessStrategy(NaturalIdRegionImpl region) {
 		this.region = region;
 	}
 
 	@Override
-	public boolean insert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean insert(Object key, Object value) throws CacheException {
 		return false;
 	}
 
 	@Override
-	public boolean update(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean update(Object key, Object value) throws CacheException {
 		return false;
 	}
 
 	@Override
-	public boolean afterInsert(NaturalIdCacheKey key, Object value) throws CacheException {
+	public boolean afterInsert(Object key, Object value) throws CacheException {
 
 		try {
 			writeLock.lock();
 			Lockable item = (Lockable) region.get( key );
 			if ( item == null ) {
 				region.put( key, new Item( value, null, region.nextTimestamp() ) );
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 
 	@Override
-	public boolean afterUpdate(NaturalIdCacheKey key, Object value, SoftLock lock) throws CacheException {
+	public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
 		try {
 			writeLock.lock();
 			Lockable item = (Lockable) region.get( key );
 
 			if ( item != null && item.isUnlockable( lock ) ) {
 				Lock lockItem = (Lock) item;
 				if ( lockItem.wasLockedConcurrently() ) {
 					decrementLock( key, lockItem );
 					return false;
 				}
 				else {
 					region.put( key, new Item( value, null, region.nextTimestamp() ) );
 					return true;
 				}
 			}
 			else {
 				handleLockExpiry( key, item );
 				return false;
 			}
 		}
 		finally {
 			writeLock.unlock();
 		}
 	}
 
 	@Override
 	Comparator getVersionComparator() {
 		return region.getCacheDataDescription().getVersionComparator();
 	}
 
 	@Override
 	protected BaseGeneralDataRegion getInternalRegion() {
 		return region;
 	}
 
 	@Override
 	protected boolean isDefaultMinimalPutOverride() {
 		return region.getSettings().isMinimalPutsEnabled();
 	}
 
 	@Override
 	public NaturalIdRegion getRegion() {
 		return region;
 	}
 
 	@Override
-	public NaturalIdCacheKey generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
+	public Object generateCacheKey(Object[] naturalIdValues, EntityPersister persister, SessionImplementor session) {
 		return DefaultCacheKeysFactory.createNaturalIdKey( naturalIdValues, persister, session );
 	}
+
+	@Override
+	public Object[] getNaturalIdValues(Object cacheKey) {
+		return DefaultCacheKeysFactory.getNaturalIdValues(cacheKey);
+	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalCollectionRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalCollectionRegionAccessStrategy.java
index 35c510cb81..f789f70fea 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalCollectionRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalCollectionRegionAccessStrategy.java
@@ -1,25 +1,24 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.CollectionCacheKey;
 
 /**
  * @author Strong Liu <stliu@hibernate.org>
  */
 class TransactionalCollectionRegionAccessStrategy extends BaseCollectionRegionAccessStrategy {
 	TransactionalCollectionRegionAccessStrategy(CollectionRegionImpl region) {
 		super( region );
 	}
 
 	@Override
-	public void remove(CollectionCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		evict( key );
 	}
 
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalEntityRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalEntityRegionAccessStrategy.java
index 502b2745de..56507eaf00 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalEntityRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalEntityRegionAccessStrategy.java
@@ -1,42 +1,41 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.EntityCacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 
 /**
  * @author Strong Liu <stliu@hibernate.org>
  */
 class TransactionalEntityRegionAccessStrategy extends BaseEntityRegionAccessStrategy {
 	TransactionalEntityRegionAccessStrategy(EntityRegionImpl region) {
 		super( region );
 	}
 
 	@Override
-	public boolean afterInsert(EntityCacheKey key, Object value, Object version) {
+	public boolean afterInsert(Object key, Object value, Object version) {
 		return false;
 	}
 
 	@Override
-	public boolean afterUpdate(EntityCacheKey key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
+	public boolean afterUpdate(Object key, Object value, Object currentVersion, Object previousVersion, SoftLock lock) {
 		return false;
 	}
 
 	@Override
-	public void remove(EntityCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		evict( key );
 	}
 
 	@Override
 	public boolean update(
-			EntityCacheKey key, Object value, Object currentVersion,
+			Object key, Object value, Object currentVersion,
 			Object previousVersion) throws CacheException {
 		return insert( key, value, currentVersion );
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalNaturalIdRegionAccessStrategy.java b/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalNaturalIdRegionAccessStrategy.java
index 3ddfa1fa66..23e6444578 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalNaturalIdRegionAccessStrategy.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/cache/TransactionalNaturalIdRegionAccessStrategy.java
@@ -1,25 +1,24 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.testing.cache;
 
 import org.hibernate.cache.CacheException;
-import org.hibernate.cache.spi.NaturalIdCacheKey;
 
 /**
  * @author Eric Dalquist
  */
 class TransactionalNaturalIdRegionAccessStrategy extends BaseNaturalIdRegionAccessStrategy {
 	TransactionalNaturalIdRegionAccessStrategy(NaturalIdRegionImpl region) {
 		super( region );
 	}
 
 	@Override
-	public void remove(NaturalIdCacheKey key) throws CacheException {
+	public void remove(Object key) throws CacheException {
 		evict( key );
 	}
 
 }
