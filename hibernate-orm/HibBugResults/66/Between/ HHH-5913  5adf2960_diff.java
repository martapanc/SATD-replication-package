diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
index 5eea860266..eade926163 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
@@ -1,200 +1,216 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.action.spi.Executable;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.access.SoftLock;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
+import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.service.event.spi.EventListenerRegistry;
 
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
 
 	public CollectionAction(
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
 			final CacheKey ck = session.generateCacheKey(
 					key,
 					persister.getKeyType(),
 					persister.getRole()
 			);
 			final SoftLock lock = persister.getCacheAccessStrategy().lockItem( ck, null );
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
 			CacheKey ck = session.generateCacheKey(
 					key, 
 					persister.getKeyType(), 
 					persister.getRole()
 			);
 			persister.getCacheAccessStrategy().remove( ck );
 		}
 	}
 
 	@Override
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + 
 				MessageHelper.infoString( collectionRole, key );
 	}
 
 	@Override
 	public int compareTo(Object other) {
 		CollectionAction action = ( CollectionAction ) other;
 		//sort first by role name
 		int roleComparison = collectionRole.compareTo( action.collectionRole );
 		if ( roleComparison != 0 ) {
 			return roleComparison;
 		}
 		else {
 			//then by fk
 			return persister.getKeyType()
 					.compare( key, action.key, session.getEntityMode() );
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
 			final CacheKey ck = session.generateCacheKey(
 					key,
 					persister.getKeyType(),
 					persister.getRole()
 			);
 			persister.getCacheAccessStrategy().unlockItem( ck, lock );
 		}
 	}
+
+	protected <T> EventListenerGroup<T> listenerGroup(EventType<T> eventType) {
+		return getSession()
+				.getFactory()
+				.getServiceRegistry()
+				.getService( EventListenerRegistry.class )
+				.getEventListenerGroup( eventType );
+	}
+
+	protected EventSource eventSource() {
+		return (EventSource) getSession();
+	}
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java
index f0245293f0..c0a70e56c1 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java
@@ -1,103 +1,102 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
 import org.hibernate.event.PostCollectionRecreateEvent;
 import org.hibernate.event.PostCollectionRecreateEventListener;
 import org.hibernate.event.PreCollectionRecreateEvent;
 import org.hibernate.event.PreCollectionRecreateEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.service.event.spi.EventListenerGroup;
 
 public final class CollectionRecreateAction extends CollectionAction {
 
 	public CollectionRecreateAction(
-				final PersistentCollection collection, 
-				final CollectionPersister persister, 
-				final Serializable id, 
-				final SessionImplementor session) throws CacheException {
+			final PersistentCollection collection,
+			final CollectionPersister persister,
+			final Serializable id,
+			final SessionImplementor session) throws CacheException {
 		super( persister, collection, id, session );
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		// this method is called when a new non-null collection is persisted
 		// or when an existing (non-null) collection is moved to a new owner
 		final PersistentCollection collection = getCollection();
 		
 		preRecreate();
 
 		getPersister().recreate( collection, getKey(), getSession() );
 		
 		getSession().getPersistenceContext()
 				.getCollectionEntry(collection)
 				.afterAction(collection);
 		
 		evict();
 
 		postRecreate();
 
 		if ( getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
 			getSession().getFactory().getStatisticsImplementor()
 					.recreateCollection( getPersister().getRole() );
 		}
 	}
 
 	private void preRecreate() {
-		PreCollectionRecreateEventListener[] preListeners = getSession().getListeners()
-				.getPreCollectionRecreateEventListeners();
-		if (preListeners.length > 0) {
-			PreCollectionRecreateEvent preEvent = new PreCollectionRecreateEvent(
-					getPersister(), getCollection(), ( EventSource ) getSession() );
-			for ( PreCollectionRecreateEventListener preListener : preListeners ) {
-				preListener.onPreRecreateCollection( preEvent );
-			}
+		EventListenerGroup<PreCollectionRecreateEventListener> listenerGroup = listenerGroup( EventType.PRE_COLLECTION_RECREATE );
+		if ( listenerGroup.isEmpty() ) {
+			return;
+		}
+		final PreCollectionRecreateEvent event = new PreCollectionRecreateEvent( getPersister(), getCollection(), eventSource() );
+		for ( PreCollectionRecreateEventListener listener : listenerGroup.listeners() ) {
+			listener.onPreRecreateCollection( event );
 		}
 	}
 
 	private void postRecreate() {
-		PostCollectionRecreateEventListener[] postListeners = getSession().getListeners()
-				.getPostCollectionRecreateEventListeners();
-		if (postListeners.length > 0) {
-			PostCollectionRecreateEvent postEvent = new PostCollectionRecreateEvent(
-					getPersister(), getCollection(), ( EventSource ) getSession() );
-			for ( PostCollectionRecreateEventListener postListener : postListeners ) {
-				postListener.onPostRecreateCollection( postEvent );
-			}
+		EventListenerGroup<PostCollectionRecreateEventListener> listenerGroup = listenerGroup( EventType.POST_COLLECTION_RECREATE );
+		if ( listenerGroup.isEmpty() ) {
+			return;
+		}
+		final PostCollectionRecreateEvent event = new PostCollectionRecreateEvent( getPersister(), getCollection(), eventSource() );
+		for ( PostCollectionRecreateEventListener listener : listenerGroup.listeners() ) {
+			listener.onPostRecreateCollection( event );
 		}
 	}
 }
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java
index a2716ed8b2..77bac41205 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java
@@ -1,153 +1,162 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
 import org.hibernate.event.PostCollectionRemoveEvent;
 import org.hibernate.event.PostCollectionRemoveEventListener;
 import org.hibernate.event.PreCollectionRemoveEvent;
 import org.hibernate.event.PreCollectionRemoveEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.service.event.spi.EventListenerGroup;
 
 public final class CollectionRemoveAction extends CollectionAction {
 
 	private boolean emptySnapshot;
 	private final Object affectedOwner;
 	
 	/**
 	 * Removes a persistent collection from its loaded owner.
 	 *
 	 * Use this constructor when the collection is non-null.
 	 *
 	 * @param collection The collection to to remove; must be non-null
 	 * @param persister  The collection's persister
 	 * @param id The collection key
 	 * @param emptySnapshot Indicates if the snapshot is empty
 	 * @param session The session
 	 *
 	 * @throws AssertionFailure if collection is null.
 	 */
 	public CollectionRemoveAction(
 				final PersistentCollection collection,
 				final CollectionPersister persister,
 				final Serializable id,
 				final boolean emptySnapshot,
 				final SessionImplementor session) {
 		super( persister, collection, id, session );
 		if (collection == null) {
 			throw new AssertionFailure("collection == null");
 		}
 		this.emptySnapshot = emptySnapshot;
 		// the loaded owner will be set to null after the collection is removed,
 		// so capture its value as the affected owner so it is accessible to
 		// both pre- and post- events
 		this.affectedOwner = session.getPersistenceContext().getLoadedCollectionOwnerOrNull( collection );
 	}
 
 	/**
 	 * Removes a persistent collection from a specified owner.
 	 *
 	 * Use this constructor when the collection to be removed has not been loaded.
 	 *
 	 * @param affectedOwner The collection's owner; must be non-null
 	 * @param persister  The collection's persister
 	 * @param id The collection key
 	 * @param emptySnapshot Indicates if the snapshot is empty
 	 * @param session The session
 	 *
 	 * @throws AssertionFailure if affectedOwner is null.
 	 */
 	public CollectionRemoveAction(
 				final Object affectedOwner,
 				final CollectionPersister persister,
 				final Serializable id,
 				final boolean emptySnapshot,
 				final SessionImplementor session) {
 		super( persister, null, id, session );
 		if (affectedOwner == null) {
 			throw new AssertionFailure("affectedOwner == null");
 		}
 		this.emptySnapshot = emptySnapshot;
 		this.affectedOwner = affectedOwner;
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		preRemove();
 
 		if ( !emptySnapshot ) {
 			// an existing collection that was either non-empty or uninitialized
 			// is replaced by null or a different collection
 			// (if the collection is uninitialized, hibernate has no way of
 			// knowing if the collection is actually empty without querying the db)
 			getPersister().remove( getKey(), getSession() );
 		}
 		
 		final PersistentCollection collection = getCollection();
 		if (collection!=null) {
 			getSession().getPersistenceContext()
 				.getCollectionEntry(collection)
 				.afterAction(collection);
 		}
 		
 		evict();
 
 		postRemove();		
 
 		if ( getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
 			getSession().getFactory().getStatisticsImplementor()
 					.removeCollection( getPersister().getRole() );
 		}
 	}
 
 	private void preRemove() {
-		PreCollectionRemoveEventListener[] preListeners = getSession().getListeners()
-				.getPreCollectionRemoveEventListeners();
-		if (preListeners.length>0) {
-			PreCollectionRemoveEvent preEvent = new PreCollectionRemoveEvent(
-					getPersister(), getCollection(), ( EventSource ) getSession(), affectedOwner );
-			for ( PreCollectionRemoveEventListener preListener : preListeners ) {
-				preListener.onPreRemoveCollection( preEvent );
-			}
+		EventListenerGroup<PreCollectionRemoveEventListener> listenerGroup = listenerGroup( EventType.PRE_COLLECTION_REMOVE );
+		if ( listenerGroup.isEmpty() ) {
+			return;
+		}
+		final PreCollectionRemoveEvent event = new PreCollectionRemoveEvent(
+				getPersister(),
+				getCollection(),
+				eventSource(),
+				affectedOwner
+		);
+		for ( PreCollectionRemoveEventListener listener : listenerGroup.listeners() ) {
+			listener.onPreRemoveCollection( event );
 		}
 	}
 
 	private void postRemove() {
-		PostCollectionRemoveEventListener[] postListeners = getSession().getListeners()
-				.getPostCollectionRemoveEventListeners();
-		if (postListeners.length>0) {
-			PostCollectionRemoveEvent postEvent = new PostCollectionRemoveEvent(
-					getPersister(), getCollection(), ( EventSource ) getSession(), affectedOwner );
-			for ( PostCollectionRemoveEventListener postListener : postListeners ) {
-				postListener.onPostRemoveCollection( postEvent );
-			}
+		EventListenerGroup<PostCollectionRemoveEventListener> listenerGroup = listenerGroup( EventType.POST_COLLECTION_REMOVE );
+		if ( listenerGroup.isEmpty() ) {
+			return;
+		}
+		final PostCollectionRemoveEvent event = new PostCollectionRemoveEvent(
+				getPersister(),
+				getCollection(),
+				eventSource(),
+				affectedOwner
+		);
+		for ( PostCollectionRemoveEventListener listener : listenerGroup.listeners() ) {
+			listener.onPostRemoveCollection( event );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
index 6e3031dd46..4b5b08986b 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
@@ -1,134 +1,141 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
 import org.hibernate.event.PostCollectionUpdateEvent;
 import org.hibernate.event.PostCollectionUpdateEventListener;
+import org.hibernate.event.PreCollectionRemoveEvent;
+import org.hibernate.event.PreCollectionRemoveEventListener;
 import org.hibernate.event.PreCollectionUpdateEvent;
 import org.hibernate.event.PreCollectionUpdateEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
+import org.hibernate.service.event.spi.EventListenerGroup;
 
 public final class CollectionUpdateAction extends CollectionAction {
 
 	private final boolean emptySnapshot;
 
 	public CollectionUpdateAction(
 				final PersistentCollection collection,
 				final CollectionPersister persister,
 				final Serializable id,
 				final boolean emptySnapshot,
 				final SessionImplementor session) {
 		super( persister, collection, id, session );
 		this.emptySnapshot = emptySnapshot;
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		final Serializable id = getKey();
 		final SessionImplementor session = getSession();
 		final CollectionPersister persister = getPersister();
 		final PersistentCollection collection = getCollection();
 		boolean affectedByFilters = persister.isAffectedByEnabledFilters(session);
 
 		preUpdate();
 
 		if ( !collection.wasInitialized() ) {
 			if ( !collection.hasQueuedOperations() ) throw new AssertionFailure( "no queued adds" );
 			//do nothing - we only need to notify the cache...
 		}
 		else if ( !affectedByFilters && collection.empty() ) {
 			if ( !emptySnapshot ) persister.remove( id, session );
 		}
 		else if ( collection.needsRecreate(persister) ) {
 			if (affectedByFilters) {
 				throw new HibernateException(
 					"cannot recreate collection while filter is enabled: " + 
 					MessageHelper.collectionInfoString( persister, id, persister.getFactory() )
 				);
 			}
 			if ( !emptySnapshot ) persister.remove( id, session );
 			persister.recreate( collection, id, session );
 		}
 		else {
 			persister.deleteRows( collection, id, session );
 			persister.updateRows( collection, id, session );
 			persister.insertRows( collection, id, session );
 		}
 
 		getSession().getPersistenceContext()
 			.getCollectionEntry(collection)
 			.afterAction(collection);
 
 		evict();
 
 		postUpdate();
 
 		if ( getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
 			getSession().getFactory().getStatisticsImplementor().
 					updateCollection( getPersister().getRole() );
 		}
 	}
 	
 	private void preUpdate() {
-		PreCollectionUpdateEventListener[] preListeners = getSession().getListeners()
-				.getPreCollectionUpdateEventListeners();
-		if (preListeners.length > 0) {
-			PreCollectionUpdateEvent preEvent = new PreCollectionUpdateEvent(
-					getPersister(), getCollection(), ( EventSource ) getSession() );
-			for ( PreCollectionUpdateEventListener preListener : preListeners ) {
-				preListener.onPreUpdateCollection( preEvent );
-			}
+		EventListenerGroup<PreCollectionUpdateEventListener> listenerGroup = listenerGroup( EventType.PRE_COLLECTION_UPDATE );
+		if ( listenerGroup.isEmpty() ) {
+			return;
+		}
+		final PreCollectionUpdateEvent event = new PreCollectionUpdateEvent(
+				getPersister(),
+				getCollection(),
+				eventSource()
+		);
+		for ( PreCollectionUpdateEventListener listener : listenerGroup.listeners() ) {
+			listener.onPreUpdateCollection( event );
 		}
 	}
 
 	private void postUpdate() {
-		PostCollectionUpdateEventListener[] postListeners = getSession().getListeners()
-				.getPostCollectionUpdateEventListeners();
-		if (postListeners.length > 0) {
-			PostCollectionUpdateEvent postEvent = new PostCollectionUpdateEvent(
-					getPersister(),
-					getCollection(),
-					( EventSource ) getSession()
-			);
-			for ( PostCollectionUpdateEventListener postListener : postListeners ) {
-				postListener.onPostUpdateCollection( postEvent );
-			}
+		EventListenerGroup<PostCollectionUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_COLLECTION_UPDATE );
+		if ( listenerGroup.isEmpty() ) {
+			return;
+		}
+		final PostCollectionUpdateEvent event = new PostCollectionUpdateEvent(
+				getPersister(),
+				getCollection(),
+				eventSource()
+		);
+		for ( PostCollectionUpdateEventListener listener : listenerGroup.listeners() ) {
+			listener.onPostUpdateCollection( event );
 		}
 	}
 }
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
index 82424ba235..606a97d67c 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
@@ -1,189 +1,205 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.action.spi.Executable;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
+import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.service.event.spi.EventListenerRegistry;
 
 /**
  * Base class for actions relating to insert/update/delete of an entity
  * instance.
  *
  * @author Gavin King
  */
 public abstract class EntityAction
 		implements Executable, Serializable, Comparable, AfterTransactionCompletionProcess {
 
 	private final String entityName;
 	private final Serializable id;
 
 	private transient Object instance;
 	private transient SessionImplementor session;
 	private transient EntityPersister persister;
 
 	/**
 	 * Instantiate an action.
 	 *
 	 * @param session The session from which this action is coming.
 	 * @param id The id of the entity
 	 * @param instance The entity instance
 	 * @param persister The entity persister
 	 */
 	protected EntityAction(SessionImplementor session, Serializable id, Object instance, EntityPersister persister) {
 		this.entityName = persister.getEntityName();
 		this.id = id;
 		this.instance = instance;
 		this.session = session;
 		this.persister = persister;
 	}
 
 	@Override
 	public BeforeTransactionCompletionProcess getBeforeTransactionCompletionProcess() {
 		return null;
 	}
 
 	@Override
 	public AfterTransactionCompletionProcess getAfterTransactionCompletionProcess() {
 		return needsAfterTransactionCompletion()
 				? this
 				: null;
 	}
 
 	protected abstract boolean hasPostCommitEventListeners();
 
 	public boolean needsAfterTransactionCompletion() {
 		return persister.hasCache() || hasPostCommitEventListeners();
 	}
 
 	/**
 	 * entity name accessor
 	 *
 	 * @return The entity name
 	 */
 	public String getEntityName() {
 		return entityName;
 	}
 
 	/**
 	 * entity id accessor
 	 *
 	 * @return The entity id
 	 */
 	public final Serializable getId() {
 		if ( id instanceof DelayedPostInsertIdentifier ) {
 			Serializable eeId = session.getPersistenceContext().getEntry( instance ).getId();
 			return eeId instanceof DelayedPostInsertIdentifier ? null : eeId;
 		}
 		return id;
 	}
 
 	public final DelayedPostInsertIdentifier getDelayedId() {
 		return DelayedPostInsertIdentifier.class.isInstance( id ) ?
 				DelayedPostInsertIdentifier.class.cast( id ) :
 				null;
 	}
 
 	/**
 	 * entity instance accessor
 	 *
 	 * @return The entity instance
 	 */
 	public final Object getInstance() {
 		return instance;
 	}
 
 	/**
 	 * originating session accessor
 	 *
 	 * @return The session from which this action originated.
 	 */
 	public final SessionImplementor getSession() {
 		return session;
 	}
 
 	/**
 	 * entity persister accessor
 	 *
 	 * @return The entity persister
 	 */
 	public final EntityPersister getPersister() {
 		return persister;
 	}
 
 	@Override
 	public final Serializable[] getPropertySpaces() {
 		return persister.getPropertySpaces();
 	}
 
 	@Override
 	public void beforeExecutions() {
 		throw new AssertionFailure( "beforeExecutions() called for non-collection action" );
 	}
 
 	@Override
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + MessageHelper.infoString( entityName, id );
 	}
 
 	@Override
 	public int compareTo(Object other) {
 		EntityAction action = ( EntityAction ) other;
 		//sort first by entity name
 		int roleComparison = entityName.compareTo( action.entityName );
 		if ( roleComparison != 0 ) {
 			return roleComparison;
 		}
 		else {
 			//then by id
 			return persister.getIdentifierType().compare( id, action.id, session.getEntityMode() );
 		}
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
 			this.persister = session.getFactory().getEntityPersister( entityName );
 			this.instance = session.getPersistenceContext().getEntity( session.generateEntityKey( id, persister ) );
 		}
 	}
+
+	protected <T> EventListenerGroup<T> listenerGroup(EventType<T> eventType) {
+		return getSession()
+				.getFactory()
+				.getServiceRegistry()
+				.getService( EventListenerRegistry.class )
+				.getEventListenerGroup( eventType );
+	}
+
+	protected EventSource eventSource() {
+		return (EventSource) getSession();
+	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
index dfcb0891ab..833ff2123d 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
@@ -1,181 +1,181 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.access.SoftLock;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
 import org.hibernate.event.PostDeleteEvent;
 import org.hibernate.event.PostDeleteEventListener;
 import org.hibernate.event.PreDeleteEvent;
 import org.hibernate.event.PreDeleteEventListener;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.service.event.spi.EventListenerGroup;
 
 public final class EntityDeleteAction extends EntityAction {
 	private final Object version;
 	private final boolean isCascadeDeleteEnabled;
 	private final Object[] state;
 
 	private SoftLock lock;
 
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
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		Serializable id = getId();
 		EntityPersister persister = getPersister();
 		SessionImplementor session = getSession();
 		Object instance = getInstance();
 
 		boolean veto = preDelete();
 
 		Object version = this.version;
 		if ( persister.isVersionPropertyGenerated() ) {
 			// we need to grab the version value from the entity, otherwise
 			// we have issues with generated-version entities that may have
 			// multiple actions queued during the same flush
 			version = persister.getVersion( instance, session.getEntityMode() );
 		}
 
 		final CacheKey ck;
 		if ( persister.hasCache() ) {
 			ck = session.generateCacheKey( id, persister.getIdentifierType(), persister.getRootEntityName() );
 			lock = persister.getCacheAccessStrategy().lockItem( ck, version );
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
 		EntityEntry entry = persistenceContext.removeEntry( instance );
 		if ( entry == null ) {
 			throw new AssertionFailure( "possible nonthreadsafe access to session" );
 		}
 		entry.postDelete();
 
 		persistenceContext.removeEntity( entry.getEntityKey() );
 		persistenceContext.removeProxy( entry.getEntityKey() );
 		
 		if ( persister.hasCache() ) {
 			persister.getCacheAccessStrategy().remove( ck );
 		}
 
 		postDelete();
 
 		if ( getSession().getFactory().getStatistics().isStatisticsEnabled() && !veto ) {
 			getSession().getFactory().getStatisticsImplementor().deleteEntity( getPersister().getEntityName() );
 		}
 	}
 
 	private boolean preDelete() {
-		PreDeleteEventListener[] preListeners = getSession().getListeners().getPreDeleteEventListeners();
 		boolean veto = false;
-		if (preListeners.length>0) {
-			PreDeleteEvent preEvent = new PreDeleteEvent( getInstance(), getId(), state, getPersister() ,(EventSource) getSession() );
-			for ( PreDeleteEventListener preListener : preListeners ) {
-				veto = preListener.onPreDelete( preEvent ) || veto;
-			}
+		EventListenerGroup<PreDeleteEventListener> listenerGroup = listenerGroup( EventType.PRE_DELETE );
+		if ( listenerGroup.isEmpty() ) {
+			return veto;
+		}
+		final PreDeleteEvent event = new PreDeleteEvent( getInstance(), getId(), state, getPersister(), eventSource() );
+		for ( PreDeleteEventListener listener : listenerGroup.listeners() ) {
+			veto |= listener.onPreDelete( event );
 		}
 		return veto;
 	}
 
 	private void postDelete() {
-		PostDeleteEventListener[] postListeners = getSession().getListeners()
-				.getPostDeleteEventListeners();
-		if (postListeners.length>0) {
-			PostDeleteEvent postEvent = new PostDeleteEvent(
-					getInstance(),
-					getId(),
-					state,
-					getPersister(),
-					(EventSource) getSession() 
-			);
-			for ( PostDeleteEventListener postListener : postListeners ) {
-				postListener.onPostDelete( postEvent );
-			}
+		EventListenerGroup<PostDeleteEventListener> listenerGroup = listenerGroup( EventType.POST_DELETE );
+		if ( listenerGroup.isEmpty() ) {
+			return;
+		}
+		final PostDeleteEvent event = new PostDeleteEvent(
+				getInstance(),
+				getId(),
+				state,
+				getPersister(),
+				eventSource()
+		);
+		for ( PostDeleteEventListener listener : listenerGroup.listeners() ) {
+			listener.onPostDelete( event );
 		}
 	}
 
 	private void postCommitDelete() {
-		PostDeleteEventListener[] postListeners = getSession().getListeners()
-				.getPostCommitDeleteEventListeners();
-		if (postListeners.length>0) {
-			PostDeleteEvent postEvent = new PostDeleteEvent(
-					getInstance(),
-					getId(),
-					state,
-					getPersister(),
-					(EventSource) getSession()
-			);
-			for ( PostDeleteEventListener postListener : postListeners ) {
-				postListener.onPostDelete( postEvent );
-			}
+		EventListenerGroup<PostDeleteEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_DELETE );
+		if ( listenerGroup.isEmpty() ) {
+			return;
 		}
+		final PostDeleteEvent event = new PostDeleteEvent(
+				getInstance(),
+				getId(),
+				state,
+				getPersister(),
+				eventSource()
+		);
 	}
 
 	@Override
 	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) throws HibernateException {
 		if ( getPersister().hasCache() ) {
 			final CacheKey ck = getSession().generateCacheKey(
 					getId(),
 					getPersister().getIdentifierType(),
 					getPersister().getRootEntityName()
 			);
 			getPersister().getCacheAccessStrategy().unlockItem( ck, lock );
 		}
 		postCommitDelete();
 	}
 
 	@Override
 	protected boolean hasPostCommitEventListeners() {
-		return getSession().getListeners().getPostCommitDeleteEventListeners().length > 0;
+		return ! listenerGroup( EventType.POST_COMMIT_DELETE ).isEmpty();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
index c954a78d7d..c36731f3f0 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
@@ -1,204 +1,206 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
 import org.hibernate.event.PostInsertEvent;
 import org.hibernate.event.PostInsertEventListener;
 import org.hibernate.event.PreInsertEvent;
 import org.hibernate.event.PreInsertEventListener;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.service.event.spi.EventListenerGroup;
 
 public final class EntityIdentityInsertAction extends EntityAction  {
 
 	private transient Object[] state;
 	private final boolean isDelayed;
 	private final EntityKey delayedEntityKey;
 	//private CacheEntry cacheEntry;
 	private Serializable generatedId;
 
 	public EntityIdentityInsertAction(
 			Object[] state,
 	        Object instance,
 	        EntityPersister persister,
 	        SessionImplementor session,
 	        boolean isDelayed) throws HibernateException {
 		super(
 				session,
 				( isDelayed ? generateDelayedPostInsertIdentifier() : null ),
 				instance,
 				persister
 		);
 		this.state = state;
 		this.isDelayed = isDelayed;
 		this.delayedEntityKey = isDelayed ? generateDelayedEntityKey() : null;
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		final EntityPersister persister = getPersister();
 		final SessionImplementor session = getSession();
 		final Object instance = getInstance();
 
 		boolean veto = preInsert();
 
 		// Don't need to lock the cache here, since if someone
 		// else inserted the same pk first, the insert would fail
 
 		if ( !veto ) {
 			generatedId = persister.insert( state, instance, session );
 			if ( persister.hasInsertGeneratedProperties() ) {
 				persister.processInsertGeneratedProperties( generatedId, instance, state, session );
 			}
 			//need to do that here rather than in the save event listener to let
 			//the post insert events to have a id-filled entity when IDENTITY is used (EJB3)
 			persister.setIdentifier( instance, generatedId, session );
 			getSession().getPersistenceContext().registerInsertedKey( getPersister(), generatedId );
 		}
 
 
 		//TODO: this bit actually has to be called after all cascades!
 		//      but since identity insert is called *synchronously*,
 		//      instead of asynchronously as other actions, it isn't
 		/*if ( persister.hasCache() && !persister.isCacheInvalidationRequired() ) {
 			cacheEntry = new CacheEntry(object, persister, session);
 			persister.getCache().insert(generatedId, cacheEntry);
 		}*/
 
 		postInsert();
 
 		if ( session.getFactory().getStatistics().isStatisticsEnabled() && !veto ) {
 			session.getFactory().getStatisticsImplementor().insertEntity( getPersister().getEntityName() );
 		}
 
 	}
 
 	@Override
     public boolean needsAfterTransactionCompletion() {
 		//TODO: simply remove this override if we fix the above todos
 		return hasPostCommitEventListeners();
 	}
 
 	@Override
     protected boolean hasPostCommitEventListeners() {
-		return getSession().getListeners().getPostCommitInsertEventListeners().length>0;
+		return ! listenerGroup( EventType.POST_COMMIT_INSERT ).isEmpty();
 	}
 
 	@Override
 	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
 		//TODO: reenable if we also fix the above todo
 		/*EntityPersister persister = getEntityPersister();
 		if ( success && persister.hasCache() && !persister.isCacheInvalidationRequired() ) {
 			persister.getCache().afterInsert( getGeneratedId(), cacheEntry );
 		}*/
 		postCommitInsert();
 	}
 
 	private void postInsert() {
 		if ( isDelayed ) {
 			getSession().getPersistenceContext().replaceDelayedEntityIdentityInsertKeys( delayedEntityKey, generatedId );
 		}
-		PostInsertEventListener[] postListeners = getSession().getListeners()
-				.getPostInsertEventListeners();
-		if (postListeners.length>0) {
-			PostInsertEvent postEvent = new PostInsertEvent(
-					getInstance(),
-					generatedId,
-					state,
-					getPersister(),
-					(EventSource) getSession()
-			);
-			for ( PostInsertEventListener postListener : postListeners ) {
-				postListener.onPostInsert( postEvent );
-			}
+
+		EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_INSERT );
+		if ( listenerGroup.isEmpty() ) {
+			return;
+		}
+		final PostInsertEvent event = new PostInsertEvent(
+				getInstance(),
+				generatedId,
+				state,
+				getPersister(),
+				eventSource()
+		);
+		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
+			listener.onPostInsert( event );
 		}
 	}
 
 	private void postCommitInsert() {
-		PostInsertEventListener[] postListeners = getSession().getListeners()
-				.getPostCommitInsertEventListeners();
-		if (postListeners.length>0) {
-			PostInsertEvent postEvent = new PostInsertEvent(
-					getInstance(),
-					generatedId,
-					state,
-					getPersister(),
-					(EventSource) getSession()
-			);
-			for ( PostInsertEventListener postListener : postListeners ) {
-				postListener.onPostInsert( postEvent );
-			}
+		EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_INSERT );
+		if ( listenerGroup.isEmpty() ) {
+			return;
+		}
+		final PostInsertEvent event = new PostInsertEvent(
+				getInstance(),
+				generatedId,
+				state,
+				getPersister(),
+				eventSource()
+		);
+		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
+			listener.onPostInsert( event );
 		}
 	}
 
 	private boolean preInsert() {
-		PreInsertEventListener[] preListeners = getSession().getListeners()
-				.getPreInsertEventListeners();
+		EventListenerGroup<PreInsertEventListener> listenerGroup = listenerGroup( EventType.PRE_INSERT );
+		if ( listenerGroup.isEmpty() ) {
+			return false; // NO_VETO
+		}
 		boolean veto = false;
-		if (preListeners.length>0) {
-			PreInsertEvent preEvent = new PreInsertEvent( getInstance(), null, state, getPersister(), (EventSource)getSession() );
-			for ( PreInsertEventListener preListener : preListeners ) {
-				veto = preListener.onPreInsert( preEvent ) || veto;
-			}
+		final PreInsertEvent event = new PreInsertEvent( getInstance(), null, state, getPersister(), eventSource() );
+		for ( PreInsertEventListener listener : listenerGroup.listeners() ) {
+			veto |= listener.onPreInsert( event );
 		}
 		return veto;
 	}
 
 	public final Serializable getGeneratedId() {
 		return generatedId;
 	}
 
 	public EntityKey getDelayedEntityKey() {
 		return delayedEntityKey;
 	}
 
 	private synchronized static DelayedPostInsertIdentifier generateDelayedPostInsertIdentifier() {
 		return new DelayedPostInsertIdentifier();
 	}
 
 	private EntityKey generateDelayedEntityKey() {
 		if ( !isDelayed ) {
 			throw new AssertionFailure( "cannot request delayed entity-key for non-delayed post-insert-id generation" );
 		}
 		return getSession().generateEntityKey( getDelayedId(), getPersister() );
 	}
 
 	@Override
     public void afterDeserialize(SessionImplementor session) {
 		super.afterDeserialize( session );
 		// IMPL NOTE: non-flushed changes code calls this method with session == null...
 		// guard against NullPointerException
 		if ( session != null ) {
 			EntityEntry entityEntry = session.getPersistenceContext().getEntry( getInstance() );
 			this.state = entityEntry.getLoadedState();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
index 1a0d615686..1d29eb47d6 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
@@ -1,204 +1,208 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.entry.CacheEntry;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Versioning;
 import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
 import org.hibernate.event.PostInsertEvent;
 import org.hibernate.event.PostInsertEventListener;
 import org.hibernate.event.PreInsertEvent;
 import org.hibernate.event.PreInsertEventListener;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.service.event.spi.EventListenerRegistry;
 
 public final class EntityInsertAction extends EntityAction {
 
 	private Object[] state;
 	private Object version;
 	private Object cacheEntry;
 
 	public EntityInsertAction(
 	        Serializable id,
 	        Object[] state,
 	        Object instance,
 	        Object version,
 	        EntityPersister persister,
 	        SessionImplementor session) throws HibernateException {
 		super( session, id, instance, persister );
 		this.state = state;
 		this.version = version;
 	}
 
 	public Object[] getState() {
 		return state;
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		EntityPersister persister = getPersister();
 		SessionImplementor session = getSession();
 		Object instance = getInstance();
 		Serializable id = getId();
 
 		boolean veto = preInsert();
 
 		// Don't need to lock the cache here, since if someone
 		// else inserted the same pk first, the insert would fail
 
 		if ( !veto ) {
 			
 			persister.insert( id, state, instance, session );
 		
 			EntityEntry entry = session.getPersistenceContext().getEntry( instance );
 			if ( entry == null ) {
 				throw new AssertionFailure( "possible nonthreadsafe access to session" );
 			}
 			
 			entry.postInsert();
 	
 			if ( persister.hasInsertGeneratedProperties() ) {
 				persister.processInsertGeneratedProperties( id, instance, state, session );
 				if ( persister.isVersionPropertyGenerated() ) {
 					version = Versioning.getVersion(state, persister);
 				}
 				entry.postUpdate(instance, state, version);
 			}
 
 			getSession().getPersistenceContext().registerInsertedKey( getPersister(), getId() );
 		}
 
 		final SessionFactoryImplementor factory = getSession().getFactory();
 
 		if ( isCachePutEnabled( persister, session ) ) {
 			
 			CacheEntry ce = new CacheEntry(
 					state,
 					persister, 
 					persister.hasUninitializedLazyProperties( instance, session.getEntityMode() ),
 					version,
 					session,
 					instance
 				);
 			
 			cacheEntry = persister.getCacheEntryStructure().structure(ce);
 			final CacheKey ck = session.generateCacheKey( id, persister.getIdentifierType(), persister.getRootEntityName() );
 			boolean put = persister.getCacheAccessStrategy().insert( ck, cacheEntry, version );
 			
 			if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor().secondLevelCachePut( getPersister().getCacheAccessStrategy().getRegion().getName() );
 			}
 			
 		}
 
 		postInsert();
 
 		if ( factory.getStatistics().isStatisticsEnabled() && !veto ) {
 			factory.getStatisticsImplementor()
 					.insertEntity( getPersister().getEntityName() );
 		}
 
 	}
 
 	private void postInsert() {
-		PostInsertEventListener[] postListeners = getSession().getListeners()
-				.getPostInsertEventListeners();
-		if ( postListeners.length > 0 ) {
-			PostInsertEvent postEvent = new PostInsertEvent(
-					getInstance(),
-					getId(),
-					state,
-					getPersister(),
-					(EventSource) getSession()
-			);
-			for ( int i = 0; i < postListeners.length; i++ ) {
-				postListeners[i].onPostInsert(postEvent);
-			}
+		EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_INSERT );
+		if ( listenerGroup.isEmpty() ) {
+			return;
+		}
+		final PostInsertEvent event = new PostInsertEvent(
+				getInstance(),
+				getId(),
+				state,
+				getPersister(),
+				eventSource()
+		);
+		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
+			listener.onPostInsert( event );
 		}
 	}
 
 	private void postCommitInsert() {
-		PostInsertEventListener[] postListeners = getSession().getListeners()
-				.getPostCommitInsertEventListeners();
-		if ( postListeners.length > 0 ) {
-			PostInsertEvent postEvent = new PostInsertEvent(
-					getInstance(),
-					getId(),
-					state,
-					getPersister(),
-					(EventSource) getSession() 
-			);
-			for ( PostInsertEventListener postListener : postListeners ) {
-				postListener.onPostInsert( postEvent );
-			}
+		EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_INSERT );
+		if ( listenerGroup.isEmpty() ) {
+			return;
+		}
+		final PostInsertEvent event = new PostInsertEvent(
+				getInstance(),
+				getId(),
+				state,
+				getPersister(),
+				eventSource()
+		);
+		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
+			listener.onPostInsert( event );
 		}
 	}
 
 	private boolean preInsert() {
-		PreInsertEventListener[] preListeners = getSession().getListeners()
-				.getPreInsertEventListeners();
 		boolean veto = false;
-		if (preListeners.length>0) {
-			PreInsertEvent preEvent = new PreInsertEvent( getInstance(), getId(), state, getPersister(), (EventSource)getSession() );
-			for ( PreInsertEventListener preListener : preListeners ) {
-				veto = preListener.onPreInsert( preEvent ) || veto;
-			}
+
+		EventListenerGroup<PreInsertEventListener> listenerGroup = listenerGroup( EventType.PRE_INSERT );
+		if ( listenerGroup.isEmpty() ) {
+			return veto;
+		}
+		final PreInsertEvent event = new PreInsertEvent( getInstance(), getId(), state, getPersister(), eventSource() );
+		for ( PreInsertEventListener listener : listenerGroup.listeners() ) {
+			veto |= listener.onPreInsert( event );
 		}
 		return veto;
 	}
 
 	@Override
 	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) throws HibernateException {
 		EntityPersister persister = getPersister();
 		if ( success && isCachePutEnabled( persister, getSession() ) ) {
 			final CacheKey ck = getSession().generateCacheKey( getId(), persister.getIdentifierType(), persister.getRootEntityName() );
 			boolean put = persister.getCacheAccessStrategy().afterInsert( ck, cacheEntry, version );
 			
 			if ( put && getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
 				getSession().getFactory().getStatisticsImplementor()
 						.secondLevelCachePut( getPersister().getCacheAccessStrategy().getRegion().getName() );
 			}
 		}
 		postCommitInsert();
 	}
 
 	@Override
 	protected boolean hasPostCommitEventListeners() {
-		return getSession().getListeners().getPostCommitInsertEventListeners().length>0;
+		return ! listenerGroup( EventType.POST_COMMIT_INSERT ).isEmpty();
 	}
 	
 	private boolean isCachePutEnabled(EntityPersister persister, SessionImplementor session) {
 		return persister.hasCache()
 				&& !persister.isCacheInvalidationRequired()
 				&& session.getCacheMode().isPutEnabled();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
index 9e31f4fada..6c2275e39c 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
@@ -1,273 +1,274 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.access.SoftLock;
 import org.hibernate.cache.entry.CacheEntry;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.engine.Versioning;
-import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
 import org.hibernate.event.PostUpdateEvent;
 import org.hibernate.event.PostUpdateEventListener;
 import org.hibernate.event.PreUpdateEvent;
 import org.hibernate.event.PreUpdateEventListener;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.service.event.spi.EventListenerGroup;
 import org.hibernate.type.TypeHelper;
 
 public final class EntityUpdateAction extends EntityAction {
 	private final Object[] state;
 	private final Object[] previousState;
 	private final Object previousVersion;
 	private final int[] dirtyFields;
 	private final boolean hasDirtyCollection;
 	private final Object rowId;
 	private Object nextVersion;
 	private Object cacheEntry;
 	private SoftLock lock;
 
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
 	        final SessionImplementor session) throws HibernateException {
 		super( session, id, instance, persister );
 		this.state = state;
 		this.previousState = previousState;
 		this.previousVersion = previousVersion;
 		this.nextVersion = nextVersion;
 		this.dirtyFields = dirtyProperties;
 		this.hasDirtyCollection = hasDirtyCollection;
 		this.rowId = rowId;
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		Serializable id = getId();
 		EntityPersister persister = getPersister();
 		SessionImplementor session = getSession();
 		Object instance = getInstance();
 
 		boolean veto = preUpdate();
 
 		final SessionFactoryImplementor factory = getSession().getFactory();
 		Object previousVersion = this.previousVersion;
 		if ( persister.isVersionPropertyGenerated() ) {
 			// we need to grab the version value from the entity, otherwise
 			// we have issues with generated-version entities that may have
 			// multiple actions queued during the same flush
 			previousVersion = persister.getVersion( instance, session.getEntityMode() );
 		}
 		
 		final CacheKey ck;
 		if ( persister.hasCache() ) {
 			ck = session.generateCacheKey(
 					id, 
 					persister.getIdentifierType(), 
 					persister.getRootEntityName()
 			);
 			lock = persister.getCacheAccessStrategy().lockItem( ck, previousVersion );
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
 
 		EntityEntry entry = getSession().getPersistenceContext().getEntry( instance );
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
 			if ( persister.isCacheInvalidationRequired() || entry.getStatus()!=Status.MANAGED ) {
 				persister.getCacheAccessStrategy().remove( ck );
 			}
 			else {
 				//TODO: inefficient if that cache is just going to ignore the updated state!
 				CacheEntry ce = new CacheEntry(
 						state, 
 						persister, 
 						persister.hasUninitializedLazyProperties( instance, session.getEntityMode() ), 
 						nextVersion,
 						getSession(),
 						instance
 				);
 				cacheEntry = persister.getCacheEntryStructure().structure( ce );
 				boolean put = persister.getCacheAccessStrategy().update( ck, cacheEntry, nextVersion, previousVersion );
 				if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 					factory.getStatisticsImplementor().secondLevelCachePut( getPersister().getCacheAccessStrategy().getRegion().getName() );
 				}
 			}
 		}
 
 		postUpdate();
 
 		if ( factory.getStatistics().isStatisticsEnabled() && !veto ) {
 			factory.getStatisticsImplementor()
 					.updateEntity( getPersister().getEntityName() );
 		}
 	}
 
-	private void postUpdate() {
-		PostUpdateEventListener[] postListeners = getSession().getListeners()
-				.getPostUpdateEventListeners();
-		if (postListeners.length>0) {
-			PostUpdateEvent postEvent = new PostUpdateEvent( 
-					getInstance(), 
-					getId(), 
-					state, 
-					previousState,
-					dirtyFields,
-					getPersister(),
-					(EventSource) getSession() 
-				);
-			for ( int i = 0; i < postListeners.length; i++ ) {
-				postListeners[i].onPostUpdate(postEvent);
-			}
+	private boolean preUpdate() {
+		boolean veto = false;
+		EventListenerGroup<PreUpdateEventListener> listenerGroup = listenerGroup( EventType.PRE_UPDATE );
+		if ( listenerGroup.isEmpty() ) {
+			return veto;
 		}
+		final PreUpdateEvent event = new PreUpdateEvent(
+				getInstance(),
+				getId(),
+				state,
+				previousState,
+				getPersister(),
+				eventSource()
+		);
+		for ( PreUpdateEventListener listener : listenerGroup.listeners() ) {
+			veto |= listener.onPreUpdate( event );
+		}
+		return veto;
 	}
 
-	private void postCommitUpdate() {
-		PostUpdateEventListener[] postListeners = getSession().getListeners()
-				.getPostCommitUpdateEventListeners();
-		if (postListeners.length>0) {
-			PostUpdateEvent postEvent = new PostUpdateEvent( 
-					getInstance(), 
-					getId(), 
-					state, 
-					previousState,
-					dirtyFields,
-					getPersister(),
-					(EventSource) getSession()
-				);
-			for ( int i = 0; i < postListeners.length; i++ ) {
-				postListeners[i].onPostUpdate(postEvent);
-			}
+	private void postUpdate() {
+		EventListenerGroup<PostUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_UPDATE );
+		if ( listenerGroup.isEmpty() ) {
+			return;
+		}
+		final PostUpdateEvent event = new PostUpdateEvent(
+				getInstance(),
+				getId(),
+				state,
+				previousState,
+				dirtyFields,
+				getPersister(),
+				eventSource()
+		);
+		for ( PostUpdateEventListener listener : listenerGroup.listeners() ) {
+			listener.onPostUpdate( event );
 		}
 	}
 
-	private boolean preUpdate() {
-		PreUpdateEventListener[] preListeners = getSession().getListeners()
-				.getPreUpdateEventListeners();
-		boolean veto = false;
-		if (preListeners.length>0) {
-			PreUpdateEvent preEvent = new PreUpdateEvent( 
-					getInstance(), 
-					getId(), 
-					state, 
-					previousState, 
-					getPersister(),
-					(EventSource)getSession()
-				);
-			for ( int i = 0; i < preListeners.length; i++ ) {
-				veto = preListeners[i].onPreUpdate(preEvent) || veto;
-			}
+	private void postCommitUpdate() {
+		EventListenerGroup<PostUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_UPDATE );
+		if ( listenerGroup.isEmpty() ) {
+			return;
+		}
+		final PostUpdateEvent event = new PostUpdateEvent(
+				getInstance(),
+				getId(),
+				state,
+				previousState,
+				dirtyFields,
+				getPersister(),
+				eventSource()
+		);
+		for ( PostUpdateEventListener listener : listenerGroup.listeners() ) {
+			listener.onPostUpdate( event );
 		}
-		return veto;
 	}
 
 	@Override
 	protected boolean hasPostCommitEventListeners() {
-		return getSession().getListeners().getPostCommitUpdateEventListeners().length>0;
+		return ! listenerGroup( EventType.POST_COMMIT_UPDATE ).isEmpty();
 	}
 
 	@Override
 	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) throws CacheException {
 		EntityPersister persister = getPersister();
 		if ( persister.hasCache() ) {
 			
 			final CacheKey ck = getSession().generateCacheKey(
 					getId(), 
 					persister.getIdentifierType(), 
 					persister.getRootEntityName()
 				);
 			
 			if ( success && cacheEntry!=null /*!persister.isCacheInvalidationRequired()*/ ) {
 				boolean put = persister.getCacheAccessStrategy().afterUpdate( ck, cacheEntry, nextVersion, previousVersion, lock );
 				
 				if ( put && getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
 					getSession().getFactory().getStatisticsImplementor().secondLevelCachePut( getPersister().getCacheAccessStrategy().getRegion().getName() );
 				}
 			}
 			else {
 				persister.getCacheAccessStrategy().unlockItem( ck, lock );
 			}
 		}
 		postCommitUpdate();
 	}
 
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/entry/CacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/entry/CacheEntry.java
index 3378fab964..51ad7ab088 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/entry/CacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/entry/CacheEntry.java
@@ -1,162 +1,171 @@
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
 package org.hibernate.cache.entry;
+
 import java.io.Serializable;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
 import org.hibernate.event.PreLoadEvent;
 import org.hibernate.event.PreLoadEventListener;
+import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.type.TypeHelper;
-import org.hibernate.internal.util.collections.ArrayHelper;
 
 /**
  * A cached instance of a persistent class
  *
  * @author Gavin King
  */
 public final class CacheEntry implements Serializable {
 
 	private final Serializable[] disassembledState;
 	private final String subclass;
 	private final boolean lazyPropertiesAreUnfetched;
 	private final Object version;
 	
 	public String getSubclass() {
 		return subclass;
 	}
 	
 	public boolean areLazyPropertiesUnfetched() {
 		return lazyPropertiesAreUnfetched;
 	}
 	
 	public CacheEntry(
 			final Object[] state, 
 			final EntityPersister persister, 
 			final boolean unfetched, 
 			final Object version,
 			final SessionImplementor session, 
 			final Object owner) 
 	throws HibernateException {
 		//disassembled state gets put in a new array (we write to cache by value!)
 		this.disassembledState = TypeHelper.disassemble(
 				state, 
 				persister.getPropertyTypes(), 
 				persister.isLazyPropertiesCacheable() ? 
 					null : persister.getPropertyLaziness(),
 				session, 
 				owner 
 			);
 		subclass = persister.getEntityName();
 		lazyPropertiesAreUnfetched = unfetched || !persister.isLazyPropertiesCacheable();
 		this.version = version;
 	}
 	
 	public Object getVersion() {
 		return version;
 	}
 
 	CacheEntry(Serializable[] state, String subclass, boolean unfetched, Object version) {
 		this.disassembledState = state;
 		this.subclass = subclass;
 		this.lazyPropertiesAreUnfetched = unfetched;
 		this.version = version;
 	}
 
 	public Object[] assemble(
 			final Object instance, 
 			final Serializable id, 
 			final EntityPersister persister, 
 			final Interceptor interceptor, 
 			final EventSource session) 
 	throws HibernateException {
 
 		if ( !persister.getEntityName().equals(subclass) ) {
 			throw new AssertionFailure("Tried to assemble a different subclass instance");
 		}
 
 		return assemble(disassembledState, instance, id, persister, interceptor, session);
 
 	}
 
 	private static Object[] assemble(
 			final Serializable[] values, 
 			final Object result, 
 			final Serializable id, 
 			final EntityPersister persister, 
 			final Interceptor interceptor, 
 			final EventSource session) throws HibernateException {
 			
 		//assembled state gets put in a new array (we read from cache by value!)
 		Object[] assembledProps = TypeHelper.assemble(
 				values, 
 				persister.getPropertyTypes(), 
 				session, result 
 			);
 
 		//persister.setIdentifier(result, id); //before calling interceptor, for consistency with normal load
 
 		//TODO: reuse the PreLoadEvent
-		PreLoadEvent preLoadEvent = new PreLoadEvent( session )
-				.setEntity(result)
-				.setState(assembledProps)
-				.setId(id)
-				.setPersister(persister);
-		
-		PreLoadEventListener[] listeners = session.getListeners().getPreLoadEventListeners();
-		for ( PreLoadEventListener listener : listeners ) {
+		final PreLoadEvent preLoadEvent = new PreLoadEvent( session )
+				.setEntity( result )
+				.setState( assembledProps )
+				.setId( id )
+				.setPersister( persister );
+
+		final EventListenerGroup<PreLoadEventListener> listenerGroup = session
+				.getFactory()
+				.getServiceRegistry()
+				.getService( EventListenerRegistry.class )
+				.getEventListenerGroup( EventType.PRE_LOAD );
+		for ( PreLoadEventListener listener : listenerGroup.listeners() ) {
 			listener.onPreLoad( preLoadEvent );
 		}
-		
+
 		persister.setPropertyValues( 
 				result, 
 				assembledProps, 
 				session.getEntityMode() 
 			);
 
 		return assembledProps;
 	}
 
     public Serializable[] getDisassembledState() {
 	    // todo: this was added to support initializing an entity's EntityEntry snapshot during reattach;
 	    // this should be refactored to instead expose a method to assemble a EntityEntry based on this
 	    // state for return.
 	    return disassembledState;
     }
 
 	public String toString() {
 		return "CacheEntry(" + subclass + ')' + 
 				ArrayHelper.toString(disassembledState);
 	}
 
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index d89accc9e3..29fb070066 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,3733 +1,3336 @@
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
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
 import java.io.StringReader;
 import java.lang.reflect.Array;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
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
 import java.util.ResourceBundle;
 import java.util.Set;
 import java.util.StringTokenizer;
 import java.util.TreeMap;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.DocumentException;
 import org.dom4j.Element;
 import org.hibernate.AnnotationException;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
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
 import org.hibernate.cfg.beanvalidation.BeanValidationActivator;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.event.AutoFlushEventListener;
 import org.hibernate.event.DeleteEventListener;
 import org.hibernate.event.DirtyCheckEventListener;
 import org.hibernate.event.EventListeners;
+import org.hibernate.event.EventType;
 import org.hibernate.event.EvictEventListener;
 import org.hibernate.event.FlushEntityEventListener;
 import org.hibernate.event.FlushEventListener;
 import org.hibernate.event.InitializeCollectionEventListener;
 import org.hibernate.event.LoadEventListener;
 import org.hibernate.event.LockEventListener;
 import org.hibernate.event.MergeEventListener;
 import org.hibernate.event.PersistEventListener;
 import org.hibernate.event.PostCollectionRecreateEventListener;
 import org.hibernate.event.PostCollectionRemoveEventListener;
 import org.hibernate.event.PostCollectionUpdateEventListener;
 import org.hibernate.event.PostDeleteEventListener;
 import org.hibernate.event.PostInsertEventListener;
 import org.hibernate.event.PostLoadEventListener;
 import org.hibernate.event.PostUpdateEventListener;
 import org.hibernate.event.PreCollectionRecreateEventListener;
 import org.hibernate.event.PreCollectionRemoveEventListener;
 import org.hibernate.event.PreCollectionUpdateEventListener;
 import org.hibernate.event.PreDeleteEventListener;
 import org.hibernate.event.PreInsertEventListener;
 import org.hibernate.event.PreLoadEventListener;
 import org.hibernate.event.PreUpdateEventListener;
 import org.hibernate.event.RefreshEventListener;
 import org.hibernate.event.ReplicateEventListener;
 import org.hibernate.event.SaveOrUpdateEventListener;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentifierGeneratorAggregator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.impl.SessionFactoryImpl;
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
+<<<<<<< HEAD
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
+=======
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.service.internal.ServiceRegistryImpl;
+import org.hibernate.service.spi.ServiceRegistry;
+>>>>>>> HHH-5913 - Implement set of event listeners as a service
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
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
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
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, Configuration.class.getName());
 
 	/**
 	 * Setting used to give the name of the default {@link org.hibernate.annotations.CacheConcurrencyStrategy}
 	 * to use when either {@link javax.persistence.Cacheable @Cacheable} or
 	 * {@link org.hibernate.annotations.Cache @Cache} is used.  {@link org.hibernate.annotations.Cache @Cache(strategy="..")} is used to override.
 	 */
 	public static final String DEFAULT_CACHE_CONCURRENCY_STRATEGY = "hibernate.cache.default_cache_concurrency_strategy";
 
 	/**
 	 * Setting which indicates whether or not the new {@link org.hibernate.id.IdentifierGenerator} are used
 	 * for AUTO, TABLE and SEQUENCE.
 	 * Default to false to keep backward compatibility.
 	 */
 	public static final String USE_NEW_ID_GENERATOR_MAPPINGS = "hibernate.id.new_generator_mappings";
 
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
 
-	private EventListeners eventListeners;
-
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
-		eventListeners = new EventListeners();
 
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
             LOG.cachedFileNotFound(cachedFile.getPath(), e);
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
         LOG.mappingPackage(packageName);
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
 	 * @return The sequence of DDL commands to apply the schema objects
 	 *
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 	 *
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata)
 			throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						( table.getSchema() == null ) ? defaultSchema : table.getSchema(),
 						( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog(),
 								table.isQuoted()
 
 					);
 				if ( tableInfo == null ) {
 					script.add(
 							table.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 								)
 						);
 				}
 				else {
 					Iterator<String> subiter = table.sqlAlterStrings(
 							dialect,
 							mapping,
 							tableInfo,
 							defaultCatalog,
 							defaultSchema
 						);
 					while ( subiter.hasNext() ) {
 						script.add( subiter.next() );
 					}
 				}
 
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
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						table.getSchema(),
 						table.getCatalog(),
 						table.isQuoted()
 					);
 
 				if ( dialect.hasAlterTable() ) {
 					Iterator subIter = table.getForeignKeyIterator();
 					while ( subIter.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subIter.next();
 						if ( fk.isPhysicalConstraint() ) {
 							boolean create = tableInfo == null || (
 									tableInfo.getForeignKeyMetadata( fk ) == null && (
 											//Icky workaround for MySQL bug:
 											!( dialect instanceof MySQLDialect ) ||
 													tableInfo.getIndexMetadata( fk.getName() ) == null
 										)
 								);
 							if ( create ) {
 								script.add(
 										fk.sqlCreateString(
 												dialect,
 												mapping,
 												defaultCatalog,
 												defaultSchema
 											)
 									);
 							}
 						}
 					}
 				}
 
 				Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					final Index index = (Index) subIter.next();
 					// Skip if index already exists
 					if ( tableInfo != null && StringHelper.isNotEmpty( index.getName() ) ) {
 						final IndexMetadata meta = tableInfo.getIndexMetadata( index.getName() );
 						if ( meta != null ) {
 							continue;
 						}
 					}
 					script.add(
 							index.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 							)
 					);
 				}
 
 //broken, 'cos we don't generate these with names in SchemaExport
 //				subIter = table.getUniqueKeyIterator();
 //				while ( subIter.hasNext() ) {
 //					UniqueKey uk = (UniqueKey) subIter.next();
 //					if ( tableInfo==null || tableInfo.getIndexMetadata( uk.getFilterName() ) == null ) {
 //						script.add( uk.sqlCreateString(dialect, mapping) );
 //					}
 //				}
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				String[] lines = generator.sqlCreateStrings( dialect );
 				script.addAll( Arrays.asList( lines ) );
 			}
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	public void validateSchema(Dialect dialect, DatabaseMetadata databaseMetadata)throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						( table.getSchema() == null ) ? defaultSchema : table.getSchema(),
 						( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog(),
 								table.isQuoted());
 				if ( tableInfo == null ) {
 					throw new HibernateException( "Missing table: " + table.getName() );
 				}
 				else {
 					table.validateColumns( dialect, mapping, tableInfo );
 				}
 
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				throw new HibernateException( "Missing sequence or table: " + key );
 			}
 		}
 	}
 
 	private void validate() throws MappingException {
 		Iterator iter = classes.values().iterator();
 		while ( iter.hasNext() ) {
 			( (PersistentClass) iter.next() ).validate( mapping );
 		}
 		iter = collections.values().iterator();
 		while ( iter.hasNext() ) {
 			( (Collection) iter.next() ).validate( mapping );
 		}
 	}
 
 	/**
 	 * Call this to ensure the mappings are fully compiled/built. Usefull to ensure getting
 	 * access to all information in the metamodel when calling e.g. getClassMappings().
 	 */
 	public void buildMappings() {
 		secondPassCompile();
 	}
 
 	protected void secondPassCompile() throws MappingException {
         LOG.trace("Starting secondPassCompile() processing");
 
 		//process default values first
 		{
 			if ( !isDefaultProcessed ) {
 				//use global delimiters if orm.xml declare it
 				final Object isDelimited = reflectionManager.getDefaults().get( "delimited-identifier" );
 				if ( isDelimited != null && isDelimited == Boolean.TRUE ) {
 					getProperties().put( Environment.GLOBALLY_QUOTED_IDENTIFIERS, "true" );
 				}
 
 				AnnotationBinder.bindDefaults( createMappings() );
 				isDefaultProcessed = true;
 			}
 		}
 
 		// process metadata queue
 		{
 			metadataSourceQueue.syncAnnotatedClasses();
 			metadataSourceQueue.processMetadata( determineMetadataSourcePrecedence() );
 		}
 
 		// process cache queue
 		{
 			for ( CacheHolder holder : caches ) {
 				if ( holder.isClass ) {
 					applyCacheConcurrencyStrategy( holder );
 				}
 				else {
 					applyCollectionCacheConcurrencyStrategy( holder );
 				}
 			}
 			caches.clear();
 		}
 
 		try {
 			inSecondPass = true;
 			processSecondPassesOfType( PkDrivenByDefaultMapsIdSecondPass.class );
 			processSecondPassesOfType( SetSimpleValueTypeSecondPass.class );
 			processSecondPassesOfType( CopyIdentifierComponentSecondPass.class );
 			processFkSecondPassInOrder();
 			processSecondPassesOfType( CreateKeySecondPass.class );
 			processSecondPassesOfType( SecondaryTableSecondPass.class );
 
 			originalSecondPassCompile();
 
 			inSecondPass = false;
 		}
 		catch ( RecoverableException e ) {
 			//the exception was not recoverable after all
 			throw ( RuntimeException ) e.getCause();
 		}
 
 		for ( Map.Entry<Table, List<UniqueConstraintHolder>> tableListEntry : uniqueConstraintHoldersByTable.entrySet() ) {
 			final Table table = tableListEntry.getKey();
 			final List<UniqueConstraintHolder> uniqueConstraints = tableListEntry.getValue();
 			int uniqueIndexPerTable = 0;
 			for ( UniqueConstraintHolder holder : uniqueConstraints ) {
 				uniqueIndexPerTable++;
 				final String keyName = StringHelper.isEmpty( holder.getName() )
 						? "key" + uniqueIndexPerTable
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns() );
 			}
 		}
 
 		applyConstraintsToDDL();
 	}
 
 	private void processSecondPassesOfType(Class<? extends SecondPass> type) {
 		Iterator iter = secondPasses.iterator();
 		while ( iter.hasNext() ) {
 			SecondPass sp = ( SecondPass ) iter.next();
 			//do the second pass of simple value types first and remove them
 			if ( type.isInstance( sp ) ) {
 				sp.doSecondPass( classes );
 				iter.remove();
 			}
 		}
 	}
 
 	/**
 	 * Processes FKSecondPass instances trying to resolve any
 	 * graph circularity (ie PK made of a many to one linking to
 	 * an entity having a PK made of a ManyToOne ...).
 	 */
 	private void processFkSecondPassInOrder() {
         LOG.debugf("Processing fk mappings (*ToOne and JoinedSubclass)");
 		List<FkSecondPass> fkSecondPasses = getFKSecondPassesOnly();
 
 		if ( fkSecondPasses.size() == 0 ) {
 			return; // nothing to do here
 		}
 
 		// split FkSecondPass instances into primary key and non primary key FKs.
 		// While doing so build a map of class names to FkSecondPass instances depending on this class.
 		Map<String, Set<FkSecondPass>> isADependencyOf = new HashMap<String, Set<FkSecondPass>>();
 		List<FkSecondPass> endOfQueueFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( FkSecondPass sp : fkSecondPasses ) {
 			if ( sp.isInPrimaryKey() ) {
 				String referenceEntityName = sp.getReferencedEntityName();
 				PersistentClass classMapping = getClassMapping( referenceEntityName );
 				String dependentTable = classMapping.getTable().getQuotedName();
 				if ( !isADependencyOf.containsKey( dependentTable ) ) {
 					isADependencyOf.put( dependentTable, new HashSet<FkSecondPass>() );
 				}
 				isADependencyOf.get( dependentTable ).add( sp );
 			}
 			else {
 				endOfQueueFkSecondPasses.add( sp );
 			}
 		}
 
 		// using the isADependencyOf map we order the FkSecondPass recursively instances into the right order for processing
 		List<FkSecondPass> orderedFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( String tableName : isADependencyOf.keySet() ) {
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, tableName, tableName );
 		}
 
 		// process the ordered FkSecondPasses
 		for ( FkSecondPass sp : orderedFkSecondPasses ) {
 			sp.doSecondPass( classes );
 		}
 
 		processEndOfQueue( endOfQueueFkSecondPasses );
 	}
 
 	/**
 	 * @return Returns a list of all <code>secondPasses</code> instances which are a instance of
 	 *         <code>FkSecondPass</code>.
 	 */
 	private List<FkSecondPass> getFKSecondPassesOnly() {
 		Iterator iter = secondPasses.iterator();
 		List<FkSecondPass> fkSecondPasses = new ArrayList<FkSecondPass>( secondPasses.size() );
 		while ( iter.hasNext() ) {
 			SecondPass sp = ( SecondPass ) iter.next();
 			//do the second pass of fk before the others and remove them
 			if ( sp instanceof FkSecondPass ) {
 				fkSecondPasses.add( ( FkSecondPass ) sp );
 				iter.remove();
 			}
 		}
 		return fkSecondPasses;
 	}
 
 	/**
 	 * Recursively builds a list of FkSecondPass instances ready to be processed in this order.
 	 * Checking all dependencies recursively seems quite expensive, but the original code just relied
 	 * on some sort of table name sorting which failed in certain circumstances.
 	 * <p/>
 	 * See <tt>ANN-722</tt> and <tt>ANN-730</tt>
 	 *
 	 * @param orderedFkSecondPasses The list containing the <code>FkSecondPass<code> instances ready
 	 * for processing.
 	 * @param isADependencyOf Our lookup data structure to determine dependencies between tables
 	 * @param startTable Table name to start recursive algorithm.
 	 * @param currentTable The current table name used to check for 'new' dependencies.
 	 */
 	private void buildRecursiveOrderedFkSecondPasses(
 			List<FkSecondPass> orderedFkSecondPasses,
 			Map<String, Set<FkSecondPass>> isADependencyOf,
 			String startTable,
 			String currentTable) {
 
 		Set<FkSecondPass> dependencies = isADependencyOf.get( currentTable );
 
 		// bottom out
 		if ( dependencies == null || dependencies.size() == 0 ) {
 			return;
 		}
 
 		for ( FkSecondPass sp : dependencies ) {
 			String dependentTable = sp.getValue().getTable().getQuotedName();
 			if ( dependentTable.compareTo( startTable ) == 0 ) {
 				StringBuilder sb = new StringBuilder(
 						"Foreign key circularity dependency involving the following tables: "
 				);
 				throw new AnnotationException( sb.toString() );
 			}
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, startTable, dependentTable );
 			if ( !orderedFkSecondPasses.contains( sp ) ) {
 				orderedFkSecondPasses.add( 0, sp );
 			}
 		}
 	}
 
 	private void processEndOfQueue(List<FkSecondPass> endOfQueueFkSecondPasses) {
 		/*
 		 * If a second pass raises a recoverableException, queue it for next round
 		 * stop of no pass has to be processed or if the number of pass to processes
 		 * does not diminish between two rounds.
 		 * If some failing pass remain, raise the original exception
 		 */
 		boolean stopProcess = false;
 		RuntimeException originalException = null;
 		while ( !stopProcess ) {
 			List<FkSecondPass> failingSecondPasses = new ArrayList<FkSecondPass>();
 			Iterator<FkSecondPass> it = endOfQueueFkSecondPasses.listIterator();
 			while ( it.hasNext() ) {
 				final FkSecondPass pass = it.next();
 				try {
 					pass.doSecondPass( classes );
 				}
 				catch ( RecoverableException e ) {
 					failingSecondPasses.add( pass );
 					if ( originalException == null ) {
 						originalException = ( RuntimeException ) e.getCause();
 					}
 				}
 			}
 			stopProcess = failingSecondPasses.size() == 0 || failingSecondPasses.size() == endOfQueueFkSecondPasses.size();
 			endOfQueueFkSecondPasses = failingSecondPasses;
 		}
 		if ( endOfQueueFkSecondPasses.size() > 0 ) {
 			throw originalException;
 		}
 	}
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames) {
 		keyName = normalizer.normalizeIdentifierQuoting( keyName );
 
 		UniqueKey uc;
 		int size = columnNames.length;
 		Column[] columns = new Column[size];
 		Set<Column> unbound = new HashSet<Column>();
 		Set<Column> unboundNoLogical = new HashSet<Column>();
 		for ( int index = 0; index < size; index++ ) {
 			final String logicalColumnName = normalizer.normalizeIdentifierQuoting( columnNames[index] );
 			try {
 				final String columnName = createMappings().getPhysicalColumnName( logicalColumnName, table );
 				columns[index] = new Column( columnName );
 				unbound.add( columns[index] );
 				//column equals and hashcode is based on column name
 			}
 			catch ( MappingException e ) {
 				unboundNoLogical.add( new Column( logicalColumnName ) );
 			}
 		}
 		for ( Column column : columns ) {
 			if ( table.containsColumn( column ) ) {
 				uc = table.getOrCreateUniqueKey( keyName );
 				uc.addColumn( table.getColumn( column ) );
 				unbound.remove( column );
 			}
 		}
 		if ( unbound.size() > 0 || unboundNoLogical.size() > 0 ) {
 			StringBuilder sb = new StringBuilder( "Unable to create unique key constraint (" );
 			for ( String columnName : columnNames ) {
 				sb.append( columnName ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( ") on table " ).append( table.getName() ).append( ": " );
 			for ( Column column : unbound ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( " not found" );
 			throw new AnnotationException( sb.toString() );
 		}
 	}
 
 	private void applyConstraintsToDDL() {
 		boolean applyOnDdl = getProperties().getProperty(
 				"hibernate.validator.apply_to_ddl",
 				"true"
 		)
 				.equalsIgnoreCase( "true" );
 
 		if ( !applyOnDdl ) {
 			return; // nothing to do in this case
 		}
 		applyHibernateValidatorLegacyConstraintsOnDDL();
 		applyBeanValidationConstraintsOnDDL();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void applyHibernateValidatorLegacyConstraintsOnDDL() {
 		//TODO search for the method only once and cache it?
 		Constructor validatorCtr = null;
 		Method applyMethod = null;
 		try {
 			Class classValidator = ReflectHelper.classForName(
 					"org.hibernate.validator.ClassValidator", this.getClass()
 			);
 			Class messageInterpolator = ReflectHelper.classForName(
 					"org.hibernate.validator.MessageInterpolator", this.getClass()
 			);
 			validatorCtr = classValidator.getDeclaredConstructor(
 					Class.class, ResourceBundle.class, messageInterpolator, Map.class, ReflectionManager.class
 			);
 			applyMethod = classValidator.getMethod( "apply", PersistentClass.class );
 		}
 		catch ( ClassNotFoundException e ) {
             if (!isValidatorNotPresentLogged) LOG.validatorNotFound();
 			isValidatorNotPresentLogged = true;
 		}
 		catch ( NoSuchMethodException e ) {
 			throw new AnnotationException( e );
 		}
 		if ( applyMethod != null ) {
 			for ( PersistentClass persistentClazz : classes.values() ) {
 				//integrate the validate framework
 				String className = persistentClazz.getClassName();
 				if ( StringHelper.isNotEmpty( className ) ) {
 					try {
 						Object validator = validatorCtr.newInstance(
 								ReflectHelper.classForName( className ), null, null, null, reflectionManager
 						);
 						applyMethod.invoke( validator, persistentClazz );
 					}
 					catch ( Exception e ) {
                         LOG.unableToApplyConstraints(className, e);
 					}
 				}
 			}
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void applyBeanValidationConstraintsOnDDL() {
 		BeanValidationActivator.applyDDL( classes.values(), getProperties() );
 	}
 
 	private void originalSecondPassCompile() throws MappingException {
         LOG.debugf("Processing extends queue");
 		processExtendsQueue();
 
         LOG.debugf("Processing collection mappings");
 		Iterator itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			if ( ! (sp instanceof QuerySecondPass) ) {
 				sp.doSecondPass( classes );
 				itr.remove();
 			}
 		}
 
         LOG.debugf("Processing native query and ResultSetMapping mappings");
 		itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			sp.doSecondPass( classes );
 			itr.remove();
 		}
 
         LOG.debugf("Processing association property references");
 
 		itr = propertyReferences.iterator();
 		while ( itr.hasNext() ) {
 			Mappings.PropertyReference upr = (Mappings.PropertyReference) itr.next();
 
 			PersistentClass clazz = getClassMapping( upr.referencedClass );
 			if ( clazz == null ) {
 				throw new MappingException(
 						"property-ref to unmapped class: " +
 						upr.referencedClass
 					);
 			}
 
 			Property prop = clazz.getReferencedProperty( upr.propertyName );
 			if ( upr.unique ) {
 				( (SimpleValue) prop.getValue() ).setAlternateUniqueKey( true );
 			}
 		}
 
 		//TODO: Somehow add the newly created foreign keys to the internal collection
 
         LOG.debugf("Processing foreign key constraints");
 
 		itr = getTableMappings();
 		Set done = new HashSet();
 		while ( itr.hasNext() ) {
 			secondPassCompileForeignKeys( (Table) itr.next(), done );
 		}
 
 	}
 
 	private int processExtendsQueue() {
         LOG.debugf("Processing extends queue");
 		int added = 0;
 		ExtendsQueueEntry extendsQueueEntry = findPossibleExtends();
 		while ( extendsQueueEntry != null ) {
 			metadataSourceQueue.processHbmXml( extendsQueueEntry.getMetadataXml(), extendsQueueEntry.getEntityNames() );
 			extendsQueueEntry = findPossibleExtends();
 		}
 
 		if ( extendsQueue.size() > 0 ) {
 			Iterator iterator = extendsQueue.keySet().iterator();
 			StringBuffer buf = new StringBuffer( "Following super classes referenced in extends not found: " );
 			while ( iterator.hasNext() ) {
 				final ExtendsQueueEntry entry = ( ExtendsQueueEntry ) iterator.next();
 				buf.append( entry.getExplicitName() );
 				if ( entry.getMappingPackage() != null ) {
 					buf.append( "[" ).append( entry.getMappingPackage() ).append( "]" );
 				}
 				if ( iterator.hasNext() ) {
 					buf.append( "," );
 				}
 			}
 			throw new MappingException( buf.toString() );
 		}
 
 		return added;
 	}
 
 	protected ExtendsQueueEntry findPossibleExtends() {
 		Iterator<ExtendsQueueEntry> itr = extendsQueue.keySet().iterator();
 		while ( itr.hasNext() ) {
 			final ExtendsQueueEntry entry = itr.next();
 			boolean found = getClassMapping( entry.getExplicitName() ) != null
 					|| getClassMapping( HbmBinder.getClassName( entry.getExplicitName(), entry.getMappingPackage() ) ) != null;
 			if ( found ) {
 				itr.remove();
 				return entry;
 			}
 		}
 		return null;
 	}
 
 	protected void secondPassCompileForeignKeys(Table table, Set done) throws MappingException {
 		table.createForeignKeys();
 		Iterator iter = table.getForeignKeyIterator();
 		while ( iter.hasNext() ) {
 
 			ForeignKey fk = (ForeignKey) iter.next();
 			if ( !done.contains( fk ) ) {
 				done.add( fk );
 				final String referencedEntityName = fk.getReferencedEntityName();
 				if ( referencedEntityName == null ) {
 					throw new MappingException(
 							"An association from the table " +
 							fk.getTable().getName() +
 							" does not specify the referenced entity"
 						);
 				}
                 LOG.debugf("Resolving reference to class: %s", referencedEntityName);
 				PersistentClass referencedClass = classes.get( referencedEntityName );
 				if ( referencedClass == null ) {
 					throw new MappingException(
 							"An association from the table " +
 							fk.getTable().getName() +
 							" refers to an unmapped class: " +
 							referencedEntityName
 						);
 				}
 				if ( referencedClass.isJoinedSubclass() ) {
 					secondPassCompileForeignKeys( referencedClass.getSuperclass().getTable(), done );
 				}
 				fk.setReferencedTable( referencedClass.getTable() );
 				fk.alignColumns();
 			}
 		}
 	}
 
 	public Map<String, NamedQueryDefinition> getNamedQueries() {
 		return namedQueries;
 	}
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
 	 * @return The build {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 */
 	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
         LOG.debugf("Preparing to build session factory with filters : %s", filterDefinitions);
 
 		secondPassCompile();
         if (!metadataSourceQueue.isEmpty()) LOG.incompleteMappingMetadataCacheProcessing();
 
-		enableLegacyHibernateValidator();
-		enableBeanValidation();
-		enableHibernateSearch();
+// todo : processing listeners for validator and search (and envers) requires HHH-5562
+		enableLegacyHibernateValidator( serviceRegistry );
+		enableBeanValidation( serviceRegistry );
+//		enableHibernateSearch();
 
 		validate();
 		Environment.verifyProperties( properties );
 		Properties copy = new Properties();
 		copy.putAll( properties );
 		ConfigurationHelper.resolvePlaceHolders( copy );
 		Settings settings = buildSettings( copy, serviceRegistry );
 
 		return new SessionFactoryImpl(
 				this,
 				mapping,
 				serviceRegistry,
 				settings,
-				getInitializedEventListeners(),
 				sessionFactoryObserver
 			);
 	}
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
 	 * @return The build {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 *
 	 * @deprecated Use {@link #buildSessionFactory(ServiceRegistry)} instead
 	 */
 	public SessionFactory buildSessionFactory() throws HibernateException {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		final ServiceRegistry serviceRegistry =  new BasicServiceRegistryImpl( properties );
 		setSessionFactoryObserver(
 				new SessionFactoryObserver() {
 					@Override
 					public void sessionFactoryCreated(SessionFactory factory) {
 					}
 
 					@Override
 					public void sessionFactoryClosed(SessionFactory factory) {
 						( (BasicServiceRegistryImpl) serviceRegistry ).destroy();
 					}
 				}
 		);
 		return buildSessionFactory( serviceRegistry );
 	}
 
 	private static final String LEGACY_VALIDATOR_EVENT_LISTENER = "org.hibernate.validator.event.ValidateEventListener";
 
-	private void enableLegacyHibernateValidator() {
-		//add validator events if the jar is available
-		boolean enableValidatorListeners = !"false".equalsIgnoreCase(
-				getProperty(
-						"hibernate.validator.autoregister_listeners"
-				)
-		);
+	private void enableLegacyHibernateValidator(ServiceRegistry serviceRegistry) {
+		boolean loadLegacyValidator = ConfigurationHelper.getBoolean( "hibernate.validator.autoregister_listeners", properties, false );
+
 		Class validateEventListenerClass = null;
 		try {
-			validateEventListenerClass = ReflectHelper.classForName( LEGACY_VALIDATOR_EVENT_LISTENER, Configuration.class );
+			validateEventListenerClass = serviceRegistry.getService( ClassLoaderService.class ).classForName( LEGACY_VALIDATOR_EVENT_LISTENER );
 		}
-		catch ( ClassNotFoundException e ) {
-			//validator is not present
-            LOG.debugf("Legacy Validator not present in classpath, ignoring event listener registration");
+		catch ( Exception ignored) {
 		}
-		if ( enableValidatorListeners && validateEventListenerClass != null ) {
-			//TODO so much duplication
-			Object validateEventListener;
-			try {
-				validateEventListener = validateEventListenerClass.newInstance();
-			}
-			catch ( Exception e ) {
-				throw new AnnotationException( "Unable to load Validator event listener", e );
-			}
-			{
-				boolean present = false;
-				PreInsertEventListener[] listeners = getEventListeners().getPreInsertEventListeners();
-				if ( listeners != null ) {
-					for ( Object eventListener : listeners ) {
-						//not isAssignableFrom since the user could subclass
-						present = present || validateEventListenerClass == eventListener.getClass();
-					}
-					if ( !present ) {
-						int length = listeners.length + 1;
-						PreInsertEventListener[] newListeners = new PreInsertEventListener[length];
-						System.arraycopy( listeners, 0, newListeners, 0, length - 1 );
-						newListeners[length - 1] = ( PreInsertEventListener ) validateEventListener;
-						getEventListeners().setPreInsertEventListeners( newListeners );
-					}
-				}
-				else {
-					getEventListeners().setPreInsertEventListeners(
-							new PreInsertEventListener[] { ( PreInsertEventListener ) validateEventListener }
-					);
-				}
-			}
 
-			//update event listener
-			{
-				boolean present = false;
-				PreUpdateEventListener[] listeners = getEventListeners().getPreUpdateEventListeners();
-				if ( listeners != null ) {
-					for ( Object eventListener : listeners ) {
-						//not isAssignableFrom since the user could subclass
-						present = present || validateEventListenerClass == eventListener.getClass();
-					}
-					if ( !present ) {
-						int length = listeners.length + 1;
-						PreUpdateEventListener[] newListeners = new PreUpdateEventListener[length];
-						System.arraycopy( listeners, 0, newListeners, 0, length - 1 );
-						newListeners[length - 1] = ( PreUpdateEventListener ) validateEventListener;
-						getEventListeners().setPreUpdateEventListeners( newListeners );
-					}
-				}
-				else {
-					getEventListeners().setPreUpdateEventListeners(
-							new PreUpdateEventListener[] { ( PreUpdateEventListener ) validateEventListener }
-					);
-				}
-			}
+		if ( ! loadLegacyValidator || validateEventListenerClass == null) {
+			LOG.debugf( "Skipping legacy validator loading" );
+			return;
+		}
+
+		final Object validateEventListener;
+		try {
+			validateEventListener = validateEventListenerClass.newInstance();
 		}
+		catch ( Exception e ) {
+			throw new AnnotationException( "Unable to load Validator event listener", e );
+		}
+
+		EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
+		// todo : duplication strategy
+
+		listenerRegistry.appendListeners( EventType.PRE_INSERT, (PreInsertEventListener) validateEventListener );
+		listenerRegistry.appendListeners( EventType.PRE_UPDATE, (PreUpdateEventListener) validateEventListener );
 	}
 
-	private void enableBeanValidation() {
-		BeanValidationActivator.activateBeanValidation( getEventListeners(), getProperties() );
+	private void enableBeanValidation(ServiceRegistry serviceRegistry) {
+		BeanValidationActivator.activateBeanValidation(
+				serviceRegistry.getService( EventListenerRegistry.class ),
+				getProperties()
+		);
 	}
 
 	private static final String SEARCH_EVENT_LISTENER_REGISTERER_CLASS = "org.hibernate.cfg.search.HibernateSearchEventListenerRegister";
 
 	/**
 	 * Tries to automatically register Hibernate Search event listeners by locating the
 	 * appropriate bootstrap class and calling the <code>enableHibernateSearch</code> method.
 	 */
-	private void enableHibernateSearch() {
-		// load the bootstrap class
-		Class searchStartupClass;
-		try {
-			searchStartupClass = ReflectHelper.classForName( SEARCH_STARTUP_CLASS, getClass() );
-		}
-		catch ( ClassNotFoundException e ) {
-			// TODO remove this together with SearchConfiguration after 3.1.0 release of Search
-			// try loading deprecated HibernateSearchEventListenerRegister
-			try {
-				searchStartupClass = ReflectHelper.classForName( SEARCH_EVENT_LISTENER_REGISTERER_CLASS, getClass() );
-			}
-			catch ( ClassNotFoundException cnfe ) {
-                LOG.debugf("Search not present in classpath, ignoring event listener registration.");
-				return;
-			}
-		}
-
-		// call the method for registering the listeners
-		try {
-			Object searchStartupInstance = searchStartupClass.newInstance();
-			Method enableSearchMethod = searchStartupClass.getDeclaredMethod(
-					SEARCH_STARTUP_METHOD,
-					EventListeners.class,
-					Properties.class
-			);
-			enableSearchMethod.invoke( searchStartupInstance, getEventListeners(), getProperties() );
-		}
-		catch ( InstantiationException e ) {
-            LOG.debugf("Unable to instantiate %s, ignoring event listener registration.", SEARCH_STARTUP_CLASS);
-		}
-		catch ( IllegalAccessException e ) {
-            LOG.debugf("Unable to instantiate %s, ignoring event listener registration.", SEARCH_STARTUP_CLASS);
-		}
-		catch ( NoSuchMethodException e ) {
-            LOG.debugf("Method %s() not found in %s", SEARCH_STARTUP_METHOD, SEARCH_STARTUP_CLASS);
-		}
-		catch ( InvocationTargetException e ) {
-            LOG.debugf("Unable to execute %s, ignoring event listener registration.", SEARCH_STARTUP_METHOD);
-		}
-	}
-
-	private EventListeners getInitializedEventListeners() {
-		EventListeners result = (EventListeners) eventListeners.shallowCopy();
-		result.initializeListeners( this );
-		return result;
-	}
+//	private void enableHibernateSearch() {
+//		// load the bootstrap class
+//		Class searchStartupClass;
+//		try {
+//			searchStartupClass = ReflectHelper.classForName( SEARCH_STARTUP_CLASS, getClass() );
+//		}
+//		catch ( ClassNotFoundException e ) {
+//			// TODO remove this together with SearchConfiguration after 3.1.0 release of Search
+//			// try loading deprecated HibernateSearchEventListenerRegister
+//			try {
+//				searchStartupClass = ReflectHelper.classForName( SEARCH_EVENT_LISTENER_REGISTERER_CLASS, getClass() );
+//			}
+//			catch ( ClassNotFoundException cnfe ) {
+//                LOG.debugf("Search not present in classpath, ignoring event listener registration.");
+//				return;
+//			}
+//		}
+//
+//		// call the method for registering the listeners
+//		try {
+//			Object searchStartupInstance = searchStartupClass.newInstance();
+//			Method enableSearchMethod = searchStartupClass.getDeclaredMethod(
+//					SEARCH_STARTUP_METHOD,
+//					EventListeners.class,
+//					Properties.class
+//			);
+//			enableSearchMethod.invoke( searchStartupInstance, getEventListeners(), getProperties() );
+//		}
+//		catch ( InstantiationException e ) {
+//            LOG.debugf("Unable to instantiate %s, ignoring event listener registration.", SEARCH_STARTUP_CLASS);
+//		}
+//		catch ( IllegalAccessException e ) {
+//            LOG.debugf("Unable to instantiate %s, ignoring event listener registration.", SEARCH_STARTUP_CLASS);
+//		}
+//		catch ( NoSuchMethodException e ) {
+//            LOG.debugf("Method %s() not found in %s", SEARCH_STARTUP_METHOD, SEARCH_STARTUP_CLASS);
+//		}
+//		catch ( InvocationTargetException e ) {
+//            LOG.debugf("Unable to execute %s, ignoring event listener registration.", SEARCH_STARTUP_METHOD);
+//		}
+//	}
 
 	/**
 	 * Rterieve the configured {@link Interceptor}.
 	 *
 	 * @return The current {@link Interceptor}
 	 */
 	public Interceptor getInterceptor() {
 		return interceptor;
 	}
 
 	/**
 	 * Set the current {@link Interceptor}
 	 *
 	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory) built}
 	 * {@link SessionFactory}.
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setInterceptor(Interceptor interceptor) {
 		this.interceptor = interceptor;
 		return this;
 	}
 
 	/**
 	 * Get all properties
 	 *
 	 * @return all properties
 	 */
 	public Properties getProperties() {
 		return properties;
 	}
 
 	/**
 	 * Get a property value by name
 	 *
 	 * @param propertyName The name of the property
 	 *
 	 * @return The value curently associated with that property name; may be null.
 	 */
 	public String getProperty(String propertyName) {
 		return properties.getProperty( propertyName );
 	}
 
 	/**
 	 * Specify a completely new set of properties
 	 *
 	 * @param properties The new set of properties
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setProperties(Properties properties) {
 		this.properties = properties;
 		return this;
 	}
 
 	/**
 	 * Add the given properties to ours.
 	 *
 	 * @param extraProperties The properties to add.
 	 *
 	 * @return this for method chaining
 	 *
 	 */
 	public Configuration addProperties(Properties extraProperties) {
 		this.properties.putAll( extraProperties );
 		return this;
 	}
 
 	/**
 	 * Adds the incoming properties to the internal properties structure, as long as the internal structure does not
 	 * already contain an entry for the given key.
 	 *
 	 * @param properties The properties to merge
 	 *
 	 * @return this for ethod chaining
 	 */
 	public Configuration mergeProperties(Properties properties) {
 		for ( Map.Entry entry : properties.entrySet() ) {
 			if ( this.properties.containsKey( entry.getKey() ) ) {
 				continue;
 			}
 			this.properties.setProperty( (String) entry.getKey(), (String) entry.getValue() );
 		}
 		return this;
 	}
 
 	/**
 	 * Set a property value by name
 	 *
 	 * @param propertyName The name of the property to set
 	 * @param value The new property value
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setProperty(String propertyName, String value) {
 		properties.setProperty( propertyName, value );
 		return this;
 	}
 
 	private void addProperties(Element parent) {
 		Iterator itr = parent.elementIterator( "property" );
 		while ( itr.hasNext() ) {
 			Element node = (Element) itr.next();
 			String name = node.attributeValue( "name" );
 			String value = node.getText().trim();
             LOG.debugf("%s=%s", name, value);
 			properties.setProperty( name, value );
 			if ( !name.startsWith( "hibernate" ) ) {
 				properties.setProperty( "hibernate." + name, value );
 			}
 		}
 		Environment.verifyProperties( properties );
 	}
 
 	/**
 	 * Use the mappings and properties specified in an application resource named <tt>hibernate.cfg.xml</tt>.
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find <tt>hibernate.cfg.xml</tt>
 	 *
 	 * @see #configure(String)
 	 */
 	public Configuration configure() throws HibernateException {
 		configure( "/hibernate.cfg.xml" );
 		return this;
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application resource. The format of the resource is
 	 * defined in <tt>hibernate-configuration-3.0.dtd</tt>.
 	 * <p/>
 	 * The resource is found via {@link #getConfigurationInputStream}
 	 *
 	 * @param resource The resource to use
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(String resource) throws HibernateException {
         LOG.configuringFromResource(resource);
 		InputStream stream = getConfigurationInputStream( resource );
 		return doConfigure( stream, resource );
 	}
 
 	/**
 	 * Get the configuration file as an <tt>InputStream</tt>. Might be overridden
 	 * by subclasses to allow the configuration to be located by some arbitrary
 	 * mechanism.
 	 * <p/>
 	 * By default here we use classpath resource resolution
 	 *
 	 * @param resource The resource to locate
 	 *
 	 * @return The stream
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 */
 	protected InputStream getConfigurationInputStream(String resource) throws HibernateException {
         LOG.configurationResource(resource);
 		return ConfigHelper.getResourceAsStream( resource );
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given document. The format of the document is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param url URL from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the url
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(URL url) throws HibernateException {
         LOG.configuringFromUrl(url);
 		try {
 			return doConfigure( url.openStream(), url.toString() );
 		}
 		catch (IOException ioe) {
 			throw new HibernateException( "could not configure from URL: " + url, ioe );
 		}
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application file. The format of the file is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param configFile File from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the file
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(File configFile) throws HibernateException {
         LOG.configuringFromFile(configFile.getName());
 		try {
 			return doConfigure( new FileInputStream( configFile ), configFile.toString() );
 		}
 		catch (FileNotFoundException fnfe) {
 			throw new HibernateException( "could not find file: " + configFile, fnfe );
 		}
 	}
 
 	/**
 	 * Configure this configuration's state from the contents of the given input stream.  The expectation is that
 	 * the stream contents represent an XML document conforming to the Hibernate Configuration DTD.  See
 	 * {@link #doConfigure(Document)} for further details.
 	 *
 	 * @param stream The input stream from which to read
 	 * @param resourceName The name to use in warning/error messages
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Indicates a problem reading the stream contents.
 	 */
 	protected Configuration doConfigure(InputStream stream, String resourceName) throws HibernateException {
 		try {
 			List errors = new ArrayList();
 			Document document = xmlHelper.createSAXReader( resourceName, errors, entityResolver )
 					.read( new InputSource( stream ) );
 			if ( errors.size() != 0 ) {
 				throw new MappingException( "invalid configuration", (Throwable) errors.get( 0 ) );
 			}
 			doConfigure( document );
 		}
 		catch (DocumentException e) {
 			throw new HibernateException( "Could not parse configuration: " + resourceName, e );
 		}
 		finally {
 			try {
 				stream.close();
 			}
 			catch (IOException ioe) {
                 LOG.unableToCloseInputStreamForResource(resourceName, ioe);
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given XML document.
 	 * The format of the file is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param document an XML document from which you wish to load the configuration
 	 * @return A configuration configured via the <tt>Document</tt>
 	 * @throws HibernateException if there is problem in accessing the file.
 	 */
 	public Configuration configure(org.w3c.dom.Document document) throws HibernateException {
         LOG.configuringFromXmlDocument();
 		return doConfigure( xmlHelper.createDOMReader().read( document ) );
 	}
 
 	/**
 	 * Parse a dom4j document conforming to the Hibernate Configuration DTD (<tt>hibernate-configuration-3.0.dtd</tt>)
 	 * and use its information to configure this {@link Configuration}'s state
 	 *
 	 * @param doc The dom4j document
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Indicates a problem performing the configuration task
 	 */
 	protected Configuration doConfigure(Document doc) throws HibernateException {
 		Element sfNode = doc.getRootElement().element( "session-factory" );
 		String name = sfNode.attributeValue( "name" );
 		if ( name != null ) {
 			properties.setProperty( Environment.SESSION_FACTORY_NAME, name );
 		}
 		addProperties( sfNode );
 		parseSessionFactory( sfNode, name );
 
 		Element secNode = doc.getRootElement().element( "security" );
 		if ( secNode != null ) {
 			parseSecurity( secNode );
 		}
 
         LOG.configuredSessionFactory(name);
         LOG.debugf("Properties: %s", properties);
 
 		return this;
 	}
 
 
 	private void parseSessionFactory(Element sfNode, String name) {
 		Iterator elements = sfNode.elementIterator();
 		while ( elements.hasNext() ) {
 			Element subelement = (Element) elements.next();
 			String subelementName = subelement.getName();
 			if ( "mapping".equals( subelementName ) ) {
 				parseMappingElement( subelement, name );
 			}
 			else if ( "class-cache".equals( subelementName ) ) {
 				String className = subelement.attributeValue( "class" );
 				Attribute regionNode = subelement.attribute( "region" );
 				final String region = ( regionNode == null ) ? className : regionNode.getValue();
 				boolean includeLazy = !"non-lazy".equals( subelement.attributeValue( "include" ) );
 				setCacheConcurrencyStrategy( className, subelement.attributeValue( "usage" ), region, includeLazy );
 			}
 			else if ( "collection-cache".equals( subelementName ) ) {
 				String role = subelement.attributeValue( "collection" );
 				Attribute regionNode = subelement.attribute( "region" );
 				final String region = ( regionNode == null ) ? role : regionNode.getValue();
 				setCollectionCacheConcurrencyStrategy( role, subelement.attributeValue( "usage" ), region );
 			}
-			else if ( "listener".equals( subelementName ) ) {
-				parseListener( subelement );
-			}
-			else if ( "event".equals( subelementName ) ) {
-				parseEvent( subelement );
-			}
 		}
 	}
 
 	private void parseMappingElement(Element mappingElement, String name) {
 		final Attribute resourceAttribute = mappingElement.attribute( "resource" );
 		final Attribute fileAttribute = mappingElement.attribute( "file" );
 		final Attribute jarAttribute = mappingElement.attribute( "jar" );
 		final Attribute packageAttribute = mappingElement.attribute( "package" );
 		final Attribute classAttribute = mappingElement.attribute( "class" );
 
 		if ( resourceAttribute != null ) {
 			final String resourceName = resourceAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named resource [%s] for mapping", name, resourceName);
 			addResource( resourceName );
 		}
 		else if ( fileAttribute != null ) {
 			final String fileName = fileAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named file [%s] for mapping", name, fileName);
 			addFile( fileName );
 		}
 		else if ( jarAttribute != null ) {
 			final String jarFileName = jarAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named jar file [%s] for mapping", name, jarFileName);
 			addJar( new File( jarFileName ) );
 		}
 		else if ( packageAttribute != null ) {
 			final String packageName = packageAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named package [%s] for mapping", name, packageName);
 			addPackage( packageName );
 		}
 		else if ( classAttribute != null ) {
 			final String className = classAttribute.getValue();
             LOG.debugf("Session-factory config [%s] named class [%s] for mapping", name, className);
 			try {
 				addAnnotatedClass( ReflectHelper.classForName( className ) );
 			}
 			catch ( Exception e ) {
 				throw new MappingException(
 						"Unable to load class [ " + className + "] declared in Hibernate configuration <mapping/> entry",
 						e
 				);
 			}
 		}
 		else {
 			throw new MappingException( "<mapping> element in configuration specifies no known attributes" );
 		}
 	}
 
 	private void parseSecurity(Element secNode) {
 		String contextId = secNode.attributeValue( "context" );
         setProperty(Environment.JACC_CONTEXTID, contextId);
         LOG.jaccContextId(contextId);
 		JACCConfiguration jcfg = new JACCConfiguration( contextId );
 		Iterator grantElements = secNode.elementIterator();
 		while ( grantElements.hasNext() ) {
 			Element grantElement = (Element) grantElements.next();
 			String elementName = grantElement.getName();
 			if ( "grant".equals( elementName ) ) {
 				jcfg.addPermission(
 						grantElement.attributeValue( "role" ),
 						grantElement.attributeValue( "entity-name" ),
 						grantElement.attributeValue( "actions" )
 					);
 			}
 		}
 	}
 
-	private void parseEvent(Element element) {
-		String type = element.attributeValue( "type" );
-		List listeners = element.elements();
-		String[] listenerClasses = new String[ listeners.size() ];
-		for ( int i = 0; i < listeners.size() ; i++ ) {
-			listenerClasses[i] = ( (Element) listeners.get( i ) ).attributeValue( "class" );
-		}
-        LOG.debugf("Event listeners: %s=%s", type, StringHelper.toString(listenerClasses));
-		setListeners( type, listenerClasses );
-	}
-
-	private void parseListener(Element element) {
-		String type = element.attributeValue( "type" );
-		if ( type == null ) {
-			throw new MappingException( "No type specified for listener" );
-		}
-		String impl = element.attributeValue( "class" );
-        LOG.debugf("Event listener: %s=%s", type, impl);
-		setListeners( type, new String[]{impl} );
-	}
-
-	public void setListener(String type, String listener) {
-		String[] listeners = null;
-		if ( listener != null ) {
-			listeners = (String[]) Array.newInstance( String.class, 1 );
-			listeners[0] = listener;
-		}
-		setListeners( type, listeners );
-	}
-
-	public void setListeners(String type, String[] listenerClasses) {
-		Object[] listeners = null;
-		if ( listenerClasses != null ) {
-			listeners = (Object[]) Array.newInstance( eventListeners.getListenerClassFor(type), listenerClasses.length );
-			for ( int i = 0; i < listeners.length ; i++ ) {
-				try {
-					listeners[i] = ReflectHelper.classForName( listenerClasses[i] ).newInstance();
-				}
-				catch (Exception e) {
-					throw new MappingException(
-							"Unable to instantiate specified event (" + type + ") listener class: " + listenerClasses[i],
-							e
-						);
-				}
-			}
-		}
-		setListeners( type, listeners );
-	}
-
-	public void setListener(String type, Object listener) {
-		Object[] listeners = null;
-		if ( listener != null ) {
-			listeners = (Object[]) Array.newInstance( eventListeners.getListenerClassFor(type), 1 );
-			listeners[0] = listener;
-		}
-		setListeners( type, listeners );
-	}
-
-	public void setListeners(String type, Object[] listeners) {
-		if ( "auto-flush".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setAutoFlushEventListeners( new AutoFlushEventListener[]{} );
-			}
-			else {
-				eventListeners.setAutoFlushEventListeners( (AutoFlushEventListener[]) listeners );
-			}
-		}
-		else if ( "merge".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setMergeEventListeners( new MergeEventListener[]{} );
-			}
-			else {
-				eventListeners.setMergeEventListeners( (MergeEventListener[]) listeners );
-			}
-		}
-		else if ( "create".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPersistEventListeners( new PersistEventListener[]{} );
-			}
-			else {
-				eventListeners.setPersistEventListeners( (PersistEventListener[]) listeners );
-			}
-		}
-		else if ( "create-onflush".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPersistOnFlushEventListeners( new PersistEventListener[]{} );
-			}
-			else {
-				eventListeners.setPersistOnFlushEventListeners( (PersistEventListener[]) listeners );
-			}
-		}
-		else if ( "delete".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setDeleteEventListeners( new DeleteEventListener[]{} );
-			}
-			else {
-				eventListeners.setDeleteEventListeners( (DeleteEventListener[]) listeners );
-			}
-		}
-		else if ( "dirty-check".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setDirtyCheckEventListeners( new DirtyCheckEventListener[]{} );
-			}
-			else {
-				eventListeners.setDirtyCheckEventListeners( (DirtyCheckEventListener[]) listeners );
-			}
-		}
-		else if ( "evict".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setEvictEventListeners( new EvictEventListener[]{} );
-			}
-			else {
-				eventListeners.setEvictEventListeners( (EvictEventListener[]) listeners );
-			}
-		}
-		else if ( "flush".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setFlushEventListeners( new FlushEventListener[]{} );
-			}
-			else {
-				eventListeners.setFlushEventListeners( (FlushEventListener[]) listeners );
-			}
-		}
-		else if ( "flush-entity".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setFlushEntityEventListeners( new FlushEntityEventListener[]{} );
-			}
-			else {
-				eventListeners.setFlushEntityEventListeners( (FlushEntityEventListener[]) listeners );
-			}
-		}
-		else if ( "load".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setLoadEventListeners( new LoadEventListener[]{} );
-			}
-			else {
-				eventListeners.setLoadEventListeners( (LoadEventListener[]) listeners );
-			}
-		}
-		else if ( "load-collection".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setInitializeCollectionEventListeners(
-						new InitializeCollectionEventListener[]{}
-					);
-			}
-			else {
-				eventListeners.setInitializeCollectionEventListeners(
-						(InitializeCollectionEventListener[]) listeners
-					);
-			}
-		}
-		else if ( "lock".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setLockEventListeners( new LockEventListener[]{} );
-			}
-			else {
-				eventListeners.setLockEventListeners( (LockEventListener[]) listeners );
-			}
-		}
-		else if ( "refresh".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setRefreshEventListeners( new RefreshEventListener[]{} );
-			}
-			else {
-				eventListeners.setRefreshEventListeners( (RefreshEventListener[]) listeners );
-			}
-		}
-		else if ( "replicate".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setReplicateEventListeners( new ReplicateEventListener[]{} );
-			}
-			else {
-				eventListeners.setReplicateEventListeners( (ReplicateEventListener[]) listeners );
-			}
-		}
-		else if ( "save-update".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setSaveOrUpdateEventListeners( new SaveOrUpdateEventListener[]{} );
-			}
-			else {
-				eventListeners.setSaveOrUpdateEventListeners( (SaveOrUpdateEventListener[]) listeners );
-			}
-		}
-		else if ( "save".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setSaveEventListeners( new SaveOrUpdateEventListener[]{} );
-			}
-			else {
-				eventListeners.setSaveEventListeners( (SaveOrUpdateEventListener[]) listeners );
-			}
-		}
-		else if ( "update".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setUpdateEventListeners( new SaveOrUpdateEventListener[]{} );
-			}
-			else {
-				eventListeners.setUpdateEventListeners( (SaveOrUpdateEventListener[]) listeners );
-			}
-		}
-		else if ( "pre-load".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPreLoadEventListeners( new PreLoadEventListener[]{} );
-			}
-			else {
-				eventListeners.setPreLoadEventListeners( (PreLoadEventListener[]) listeners );
-			}
-		}
-		else if ( "pre-update".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPreUpdateEventListeners( new PreUpdateEventListener[]{} );
-			}
-			else {
-				eventListeners.setPreUpdateEventListeners( (PreUpdateEventListener[]) listeners );
-			}
-		}
-		else if ( "pre-delete".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPreDeleteEventListeners( new PreDeleteEventListener[]{} );
-			}
-			else {
-				eventListeners.setPreDeleteEventListeners( (PreDeleteEventListener[]) listeners );
-			}
-		}
-		else if ( "pre-insert".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPreInsertEventListeners( new PreInsertEventListener[]{} );
-			}
-			else {
-				eventListeners.setPreInsertEventListeners( (PreInsertEventListener[]) listeners );
-			}
-		}
-		else if ( "pre-collection-recreate".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPreCollectionRecreateEventListeners( new PreCollectionRecreateEventListener[]{} );
-			}
-			else {
-				eventListeners.setPreCollectionRecreateEventListeners( (PreCollectionRecreateEventListener[]) listeners );
-			}
-		}
-		else if ( "pre-collection-remove".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPreCollectionRemoveEventListeners( new PreCollectionRemoveEventListener[]{} );
-			}
-			else {
-				eventListeners.setPreCollectionRemoveEventListeners( ( PreCollectionRemoveEventListener[]) listeners );
-			}
-		}
-		else if ( "pre-collection-update".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPreCollectionUpdateEventListeners( new PreCollectionUpdateEventListener[]{} );
-			}
-			else {
-				eventListeners.setPreCollectionUpdateEventListeners( ( PreCollectionUpdateEventListener[]) listeners );
-			}
-		}
-		else if ( "post-load".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPostLoadEventListeners( new PostLoadEventListener[]{} );
-			}
-			else {
-				eventListeners.setPostLoadEventListeners( (PostLoadEventListener[]) listeners );
-			}
-		}
-		else if ( "post-update".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPostUpdateEventListeners( new PostUpdateEventListener[]{} );
-			}
-			else {
-				eventListeners.setPostUpdateEventListeners( (PostUpdateEventListener[]) listeners );
-			}
-		}
-		else if ( "post-delete".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPostDeleteEventListeners( new PostDeleteEventListener[]{} );
-			}
-			else {
-				eventListeners.setPostDeleteEventListeners( (PostDeleteEventListener[]) listeners );
-			}
-		}
-		else if ( "post-insert".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPostInsertEventListeners( new PostInsertEventListener[]{} );
-			}
-			else {
-				eventListeners.setPostInsertEventListeners( (PostInsertEventListener[]) listeners );
-			}
-		}
-		else if ( "post-commit-update".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPostCommitUpdateEventListeners(
-						new PostUpdateEventListener[]{}
-					);
-			}
-			else {
-				eventListeners.setPostCommitUpdateEventListeners( (PostUpdateEventListener[]) listeners );
-			}
-		}
-		else if ( "post-commit-delete".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPostCommitDeleteEventListeners(
-						new PostDeleteEventListener[]{}
-					);
-			}
-			else {
-				eventListeners.setPostCommitDeleteEventListeners( (PostDeleteEventListener[]) listeners );
-			}
-		}
-		else if ( "post-commit-insert".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPostCommitInsertEventListeners(
-						new PostInsertEventListener[]{}
-				);
-			}
-			else {
-				eventListeners.setPostCommitInsertEventListeners( (PostInsertEventListener[]) listeners );
-			}
-		}
-		else if ( "post-collection-recreate".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPostCollectionRecreateEventListeners( new PostCollectionRecreateEventListener[]{} );
-			}
-			else {
-				eventListeners.setPostCollectionRecreateEventListeners( (PostCollectionRecreateEventListener[]) listeners );
-			}
-		}
-		else if ( "post-collection-remove".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPostCollectionRemoveEventListeners( new PostCollectionRemoveEventListener[]{} );
-			}
-			else {
-				eventListeners.setPostCollectionRemoveEventListeners( ( PostCollectionRemoveEventListener[]) listeners );
-			}
-		}
-		else if ( "post-collection-update".equals( type ) ) {
-			if ( listeners == null ) {
-				eventListeners.setPostCollectionUpdateEventListeners( new PostCollectionUpdateEventListener[]{} );
-			}
-			else {
-				eventListeners.setPostCollectionUpdateEventListeners( ( PostCollectionUpdateEventListener[]) listeners );
-			}
-		}
-		else {
-			throw new MappingException("Unrecognized listener type [" + type + "]");
-		}
-	}
-
-	public EventListeners getEventListeners() {
-		return eventListeners;
-	}
-
 	RootClass getRootClassMapping(String clazz) throws MappingException {
 		try {
 			return (RootClass) getClassMapping( clazz );
 		}
 		catch (ClassCastException cce) {
 			throw new MappingException( "You may only specify a cache for root <class> mappings" );
 		}
 	}
 
 	/**
 	 * Set up a cache for an entity class
 	 *
 	 * @param entityName The name of the entity to which we shoudl associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy) {
 		setCacheConcurrencyStrategy( entityName, concurrencyStrategy, entityName );
 		return this;
 	}
 
 	/**
 	 * Set up a cache for an entity class, giving an explicit region name
 	 *
 	 * @param entityName The name of the entity to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 * @param region The name of the cache region to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy, String region) {
 		setCacheConcurrencyStrategy( entityName, concurrencyStrategy, region, true );
 		return this;
 	}
 
 	public void setCacheConcurrencyStrategy(
 			String entityName,
 			String concurrencyStrategy,
 			String region,
 			boolean cacheLazyProperty) throws MappingException {
 		caches.add( new CacheHolder( entityName, concurrencyStrategy, region, true, cacheLazyProperty ) );
 	}
 
 	private void applyCacheConcurrencyStrategy(CacheHolder holder) {
 		RootClass rootClass = getRootClassMapping( holder.role );
 		if ( rootClass == null ) {
 			throw new MappingException( "Cannot cache an unknown entity: " + holder.role );
 		}
 		rootClass.setCacheConcurrencyStrategy( holder.usage );
 		rootClass.setCacheRegionName( holder.region );
 		rootClass.setLazyPropertiesCacheable( holder.cacheLazy );
 	}
 
 	/**
 	 * Set up a cache for a collection role
 	 *
 	 * @param collectionRole The name of the collection to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy) {
 		setCollectionCacheConcurrencyStrategy( collectionRole, concurrencyStrategy, collectionRole );
 		return this;
 	}
 
 	/**
 	 * Set up a cache for a collection role, giving an explicit region name
 	 *
 	 * @param collectionRole The name of the collection to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 * @param region The name of the cache region to use
 	 *
 	 * @return this for method chaining
 	 */
 	public void setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy, String region) {
 		caches.add( new CacheHolder( collectionRole, concurrencyStrategy, region, false, false ) );
 	}
 
 	private void applyCollectionCacheConcurrencyStrategy(CacheHolder holder) {
 		Collection collection = getCollectionMapping( holder.role );
 		if ( collection == null ) {
 			throw new MappingException( "Cannot cache an unknown collection: " + holder.role );
 		}
 		collection.setCacheConcurrencyStrategy( holder.usage );
 		collection.setCacheRegionName( holder.region );
 	}
 
 	/**
 	 * Get the query language imports
 	 *
 	 * @return a mapping from "import" names to fully qualified class names
 	 */
 	public Map<String,String> getImports() {
 		return imports;
 	}
 
 	/**
 	 * Create an object-oriented view of the configuration properties
 	 *
 	 * @return The build settings
 	 */
 	public Settings buildSettings(ServiceRegistry serviceRegistry) {
 		Properties clone = ( Properties ) properties.clone();
 		ConfigurationHelper.resolvePlaceHolders( clone );
 		return buildSettingsInternal( clone, serviceRegistry );
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) throws HibernateException {
 		return buildSettingsInternal( props, serviceRegistry );
 	}
 
 	private Settings buildSettingsInternal(Properties props, ServiceRegistry serviceRegistry) {
 		final Settings settings = settingsFactory.buildSettings( props, serviceRegistry );
 		settings.setEntityTuplizerFactory( this.getEntityTuplizerFactory() );
 //		settings.setComponentTuplizerFactory( this.getComponentTuplizerFactory() );
 		return settings;
 	}
 
 	public Map getNamedSQLQueries() {
 		return namedSqlQueries;
 	}
 
 	public Map getSqlResultSetMappings() {
 		return sqlResultSetMappings;
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
 	/**
 	 * Set a custom naming strategy
 	 *
 	 * @param namingStrategy the NamingStrategy to set
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setNamingStrategy(NamingStrategy namingStrategy) {
 		this.namingStrategy = namingStrategy;
 		return this;
 	}
 
 	/**
 	 * Retrieve the IdentifierGeneratorFactory in effect for this configuration.
 	 *
 	 * @return This configuration's IdentifierGeneratorFactory.
 	 */
 	public DefaultIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	public Mapping buildMapping() {
 		return new Mapping() {
 			public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 				return identifierGeneratorFactory;
 			}
 
 			/**
 			 * Returns the identifier type of a mapped class
 			 */
 			public Type getIdentifierType(String entityName) throws MappingException {
 				PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				return pc.getIdentifier().getType();
 			}
 
 			public String getIdentifierPropertyName(String entityName) throws MappingException {
 				final PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				if ( !pc.hasIdentifierProperty() ) {
 					return null;
 				}
 				return pc.getIdentifierProperty().getName();
 			}
 
 			public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 				final PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				Property prop = pc.getReferencedProperty( propertyName );
 				if ( prop == null ) {
 					throw new MappingException(
 							"property not known: " +
 							entityName + '.' + propertyName
 						);
 				}
 				return prop.getType();
 			}
 		};
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		//we need  reflectionManager before reading the other components (MetadataSourceQueue in particular)
 		final MetadataProvider metadataProvider = (MetadataProvider) ois.readObject();
 		this.mapping = buildMapping();
 		xmlHelper = new XMLHelper();
 		createReflectionManager(metadataProvider);
 		ois.defaultReadObject();
 	}
 
 	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
 		//We write MetadataProvider first as we need  reflectionManager before reading the other components
 		final MetadataProvider metadataProvider = ( ( MetadataProviderInjector ) reflectionManager ).getMetadataProvider();
 		out.writeObject( metadataProvider );
 		out.defaultWriteObject();
 	}
 
 	private void createReflectionManager() {
 		createReflectionManager( new JPAMetadataProvider() );
 	}
 
 	private void createReflectionManager(MetadataProvider metadataProvider) {
 		reflectionManager = new JavaReflectionManager();
 		( ( MetadataProviderInjector ) reflectionManager ).setMetadataProvider( metadataProvider );
 	}
 
 	public Map getFilterDefinitions() {
 		return filterDefinitions;
 	}
 
 	public void addFilterDefinition(FilterDefinition definition) {
 		filterDefinitions.put( definition.getFilterName(), definition );
 	}
 
 	public Iterator iterateFetchProfiles() {
 		return fetchProfiles.values().iterator();
 	}
 
 	public void addFetchProfile(FetchProfile fetchProfile) {
 		fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 	}
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
 		auxiliaryDatabaseObjects.add( object );
 	}
 
 	public Map getSqlFunctions() {
 		return sqlFunctions;
 	}
 
 	public void addSqlFunction(String functionName, SQLFunction function) {
 		sqlFunctions.put( functionName, function );
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	/**
 	 * Allows registration of a type into the type registry.  The phrase 'override' in the method name simply
 	 * reminds that registration *potentially* replaces a previously registered type .
 	 *
 	 * @param type The type to register.
 	 */
 	public void registerTypeOverride(BasicType type) {
 		getTypeResolver().registerTypeOverride( type );
 	}
 
 
 	public void registerTypeOverride(UserType type, String[] keys) {
 		getTypeResolver().registerTypeOverride( type, keys );
 	}
 
 	public void registerTypeOverride(CompositeUserType type, String[] keys) {
 		getTypeResolver().registerTypeOverride( type, keys );
 	}
 
 	public SessionFactoryObserver getSessionFactoryObserver() {
 		return sessionFactoryObserver;
 	}
 
 	public void setSessionFactoryObserver(SessionFactoryObserver sessionFactoryObserver) {
 		this.sessionFactoryObserver = sessionFactoryObserver;
 	}
 
 
 	// Mappings impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Internal implementation of the Mappings interface giving access to the Configuration's internal
 	 * <tt>metadata repository</tt> state ({@link Configuration#classes}, {@link Configuration#tables}, etc).
 	 */
 	protected class MappingsImpl implements ExtendedMappings, Serializable {
 
 		private String schemaName;
 
 		public String getSchemaName() {
 			return schemaName;
 		}
 
 		public void setSchemaName(String schemaName) {
 			this.schemaName = schemaName;
 		}
 
 
 		private String catalogName;
 
 		public String getCatalogName() {
 			return catalogName;
 		}
 
 		public void setCatalogName(String catalogName) {
 			this.catalogName = catalogName;
 		}
 
 
 		private String defaultPackage;
 
 		public String getDefaultPackage() {
 			return defaultPackage;
 		}
 
 		public void setDefaultPackage(String defaultPackage) {
 			this.defaultPackage = defaultPackage;
 		}
 
 
 		private boolean autoImport;
 
 		public boolean isAutoImport() {
 			return autoImport;
 		}
 
 		public void setAutoImport(boolean autoImport) {
 			this.autoImport = autoImport;
 		}
 
 
 		private boolean defaultLazy;
 
 		public boolean isDefaultLazy() {
 			return defaultLazy;
 		}
 
 		public void setDefaultLazy(boolean defaultLazy) {
 			this.defaultLazy = defaultLazy;
 		}
 
 
 		private String defaultCascade;
 
 		public String getDefaultCascade() {
 			return defaultCascade;
 		}
 
 		public void setDefaultCascade(String defaultCascade) {
 			this.defaultCascade = defaultCascade;
 		}
 
 
 		private String defaultAccess;
 
 		public String getDefaultAccess() {
 			return defaultAccess;
 		}
 
 		public void setDefaultAccess(String defaultAccess) {
 			this.defaultAccess = defaultAccess;
 		}
 
 
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 
 		public void setNamingStrategy(NamingStrategy namingStrategy) {
 			Configuration.this.namingStrategy = namingStrategy;
 		}
 
 		public TypeResolver getTypeResolver() {
 			return typeResolver;
 		}
 
 		public Iterator<PersistentClass> iterateClasses() {
 			return classes.values().iterator();
 		}
 
 		public PersistentClass getClass(String entityName) {
 			return classes.get( entityName );
 		}
 
 		public PersistentClass locatePersistentClassByEntityName(String entityName) {
 			PersistentClass persistentClass = classes.get( entityName );
 			if ( persistentClass == null ) {
 				String actualEntityName = imports.get( entityName );
 				if ( StringHelper.isNotEmpty( actualEntityName ) ) {
 					persistentClass = classes.get( actualEntityName );
 				}
 			}
 			return persistentClass;
 		}
 
 		public void addClass(PersistentClass persistentClass) throws DuplicateMappingException {
 			Object old = classes.put( persistentClass.getEntityName(), persistentClass );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "class/entity", persistentClass.getEntityName() );
 			}
 		}
 
 		public void addImport(String entityName, String rename) throws DuplicateMappingException {
 			String existing = imports.put( rename, entityName );
 			if ( existing != null ) {
                 if (existing.equals(entityName)) LOG.duplicateImport(entityName, rename);
                 else throw new DuplicateMappingException("duplicate import: " + rename + " refers to both " + entityName + " and "
                                                          + existing + " (try using auto-import=\"false\")", "import", rename);
 			}
 		}
 
 		public Collection getCollection(String role) {
 			return collections.get( role );
 		}
 
 		public Iterator<Collection> iterateCollections() {
 			return collections.values().iterator();
 		}
 
 		public void addCollection(Collection collection) throws DuplicateMappingException {
 			Object old = collections.put( collection.getRole(), collection );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "collection role", collection.getRole() );
 			}
 		}
 
 		public Table getTable(String schema, String catalog, String name) {
 			String key = Table.qualify(catalog, schema, name);
 			return tables.get(key);
 		}
 
 		public Iterator<Table> iterateTables() {
 			return tables.values().iterator();
 		}
 
 		public Table addTable(
 				String schema,
 				String catalog,
 				String name,
 				String subselect,
 				boolean isAbstract) {
 			name = getObjectNameNormalizer().normalizeIdentifierQuoting( name );
 			schema = getObjectNameNormalizer().normalizeIdentifierQuoting( schema );
 			catalog = getObjectNameNormalizer().normalizeIdentifierQuoting( catalog );
 
 			String key = subselect == null ? Table.qualify( catalog, schema, name ) : subselect;
 			Table table = tables.get( key );
 
 			if ( table == null ) {
 				table = new Table();
 				table.setAbstract( isAbstract );
 				table.setName( name );
 				table.setSchema( schema );
 				table.setCatalog( catalog );
 				table.setSubselect( subselect );
 				tables.put( key, table );
 			}
 			else {
 				if ( !isAbstract ) {
 					table.setAbstract( false );
 				}
 			}
 
 			return table;
 		}
 
 		public Table addDenormalizedTable(
 				String schema,
 				String catalog,
 				String name,
 				boolean isAbstract,
 				String subselect,
 				Table includedTable) throws DuplicateMappingException {
 			name = getObjectNameNormalizer().normalizeIdentifierQuoting( name );
 			schema = getObjectNameNormalizer().normalizeIdentifierQuoting( schema );
 			catalog = getObjectNameNormalizer().normalizeIdentifierQuoting( catalog );
 
 			String key = subselect == null ? Table.qualify(catalog, schema, name) : subselect;
 			if ( tables.containsKey( key ) ) {
 				throw new DuplicateMappingException( "table", name );
 			}
 
 			Table table = new DenormalizedTable( includedTable );
 			table.setAbstract( isAbstract );
 			table.setName( name );
 			table.setSchema( schema );
 			table.setCatalog( catalog );
 			table.setSubselect( subselect );
 
 			tables.put( key, table );
 			return table;
 		}
 
 		public NamedQueryDefinition getQuery(String name) {
 			return namedQueries.get( name );
 		}
 
 		public void addQuery(String name, NamedQueryDefinition query) throws DuplicateMappingException {
 			if ( !defaultNamedQueryNames.contains( name ) ) {
 				applyQuery( name, query );
 			}
 		}
 
 		private void applyQuery(String name, NamedQueryDefinition query) {
 			checkQueryName( name );
 			namedQueries.put( name.intern(), query );
 		}
 
 		private void checkQueryName(String name) throws DuplicateMappingException {
 			if ( namedQueries.containsKey( name ) || namedSqlQueries.containsKey( name ) ) {
 				throw new DuplicateMappingException( "query", name );
 			}
 		}
 
 		public void addDefaultQuery(String name, NamedQueryDefinition query) {
 			applyQuery( name, query );
 			defaultNamedQueryNames.add( name );
 		}
 
 		public NamedSQLQueryDefinition getSQLQuery(String name) {
 			return namedSqlQueries.get( name );
 		}
 
 		public void addSQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException {
 			if ( !defaultNamedNativeQueryNames.contains( name ) ) {
 				applySQLQuery( name, query );
 			}
 		}
 
 		private void applySQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException {
 			checkQueryName( name );
 			namedSqlQueries.put( name.intern(), query );
 		}
 
 		public void addDefaultSQLQuery(String name, NamedSQLQueryDefinition query) {
 			applySQLQuery( name, query );
 			defaultNamedNativeQueryNames.add( name );
 		}
 
 		public ResultSetMappingDefinition getResultSetMapping(String name) {
 			return sqlResultSetMappings.get(name);
 		}
 
 		public void addResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
 			if ( !defaultSqlResultSetMappingNames.contains( sqlResultSetMapping.getName() ) ) {
 				applyResultSetMapping( sqlResultSetMapping );
 			}
 		}
 
 		public void applyResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
 			Object old = sqlResultSetMappings.put( sqlResultSetMapping.getName(), sqlResultSetMapping );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "resultSet",  sqlResultSetMapping.getName() );
 			}
 		}
 
 		public void addDefaultResultSetMapping(ResultSetMappingDefinition definition) {
 			final String name = definition.getName();
 			if ( !defaultSqlResultSetMappingNames.contains( name ) && getResultSetMapping( name ) != null ) {
 				removeResultSetMapping( name );
 			}
 			applyResultSetMapping( definition );
 			defaultSqlResultSetMappingNames.add( name );
 		}
 
 		protected void removeResultSetMapping(String name) {
 			sqlResultSetMappings.remove( name );
 		}
 
 		public TypeDef getTypeDef(String typeName) {
 			return typeDefs.get( typeName );
 		}
 
 		public void addTypeDef(String typeName, String typeClass, Properties paramMap) {
 			TypeDef def = new TypeDef( typeClass, paramMap );
 			typeDefs.put( typeName, def );
             LOG.debugf("Added %s with class %s", typeName, typeClass);
 		}
 
 		public Map getFilterDefinitions() {
 			return filterDefinitions;
 		}
 
 		public FilterDefinition getFilterDefinition(String name) {
 			return filterDefinitions.get( name );
 		}
 
 		public void addFilterDefinition(FilterDefinition definition) {
 			filterDefinitions.put( definition.getFilterName(), definition );
 		}
 
 		public FetchProfile findOrCreateFetchProfile(String name, MetadataSource source) {
 			FetchProfile profile = fetchProfiles.get( name );
 			if ( profile == null ) {
 				profile = new FetchProfile( name, source );
 				fetchProfiles.put( name, profile );
 			}
 			return profile;
 		}
 
 		public Iterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjects() {
 			return iterateAuxiliaryDatabaseObjects();
 		}
 
 		public Iterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjects() {
 			return auxiliaryDatabaseObjects.iterator();
 		}
 
 		public ListIterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjectsInReverse() {
 			return iterateAuxiliaryDatabaseObjectsInReverse();
 		}
 
 		public ListIterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjectsInReverse() {
 			return auxiliaryDatabaseObjects.listIterator( auxiliaryDatabaseObjects.size() );
 		}
 
 		public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
 			auxiliaryDatabaseObjects.add( auxiliaryDatabaseObject );
 		}
 
 		/**
 		 * Internal struct used to help track physical table names to logical table names.
 		 */
 		private class TableDescription implements Serializable {
 			final String logicalName;
 			final Table denormalizedSupertable;
 
 			TableDescription(String logicalName, Table denormalizedSupertable) {
 				this.logicalName = logicalName;
 				this.denormalizedSupertable = denormalizedSupertable;
 			}
 		}
 
 		public String getLogicalTableName(Table table) throws MappingException {
 			return getLogicalTableName( table.getQuotedSchema(), table.getCatalog(), table.getQuotedName() );
 		}
 
 		private String getLogicalTableName(String schema, String catalog, String physicalName) throws MappingException {
 			String key = buildTableNameKey( schema, catalog, physicalName );
 			TableDescription descriptor = (TableDescription) tableNameBinding.get( key );
 			if (descriptor == null) {
 				throw new MappingException( "Unable to find physical table: " + physicalName);
 			}
 			return descriptor.logicalName;
 		}
 
 		public void addTableBinding(
 				String schema,
 				String catalog,
 				String logicalName,
 				String physicalName,
 				Table denormalizedSuperTable) throws DuplicateMappingException {
 			String key = buildTableNameKey( schema, catalog, physicalName );
 			TableDescription tableDescription = new TableDescription( logicalName, denormalizedSuperTable );
 			TableDescription oldDescriptor = ( TableDescription ) tableNameBinding.put( key, tableDescription );
 			if ( oldDescriptor != null && ! oldDescriptor.logicalName.equals( logicalName ) ) {
 				//TODO possibly relax that
 				throw new DuplicateMappingException(
 						"Same physical table name [" + physicalName + "] references several logical table names: [" +
 								oldDescriptor.logicalName + "], [" + logicalName + ']',
 						"table",
 						physicalName
 				);
 			}
 		}
 
 		private String buildTableNameKey(String schema, String catalog, String finalName) {
 			StringBuffer keyBuilder = new StringBuffer();
 			if (schema != null) keyBuilder.append( schema );
 			keyBuilder.append( ".");
 			if (catalog != null) keyBuilder.append( catalog );
 			keyBuilder.append( ".");
 			keyBuilder.append( finalName );
 			return keyBuilder.toString();
 		}
 
 		/**
 		 * Internal struct used to maintain xref between physical and logical column
 		 * names for a table.  Mainly this is used to ensure that the defined
 		 * {@link NamingStrategy} is not creating duplicate column names.
 		 */
 		private class TableColumnNameBinding implements Serializable {
 			private final String tableName;
 			private Map/*<String, String>*/ logicalToPhysical = new HashMap();
 			private Map/*<String, String>*/ physicalToLogical = new HashMap();
 
 			private TableColumnNameBinding(String tableName) {
 				this.tableName = tableName;
 			}
 
 			public void addBinding(String logicalName, Column physicalColumn) {
 				bindLogicalToPhysical( logicalName, physicalColumn );
 				bindPhysicalToLogical( logicalName, physicalColumn );
 			}
 
 			private void bindLogicalToPhysical(String logicalName, Column physicalColumn) throws DuplicateMappingException {
 				final String logicalKey = logicalName.toLowerCase();
 				final String physicalName = physicalColumn.getQuotedName();
 				final String existingPhysicalName = ( String ) logicalToPhysical.put( logicalKey, physicalName );
 				if ( existingPhysicalName != null ) {
 					boolean areSamePhysicalColumn = physicalColumn.isQuoted()
 							? existingPhysicalName.equals( physicalName )
 							: existingPhysicalName.equalsIgnoreCase( physicalName );
 					if ( ! areSamePhysicalColumn ) {
 						throw new DuplicateMappingException(
 								" Table [" + tableName + "] contains logical column name [" + logicalName
 										+ "] referenced by multiple physical column names: [" + existingPhysicalName
 										+ "], [" + physicalName + "]",
 								"column-binding",
 								tableName + "." + logicalName
 						);
 					}
 				}
 			}
 
 			private void bindPhysicalToLogical(String logicalName, Column physicalColumn) throws DuplicateMappingException {
 				final String physicalName = physicalColumn.getQuotedName();
 				final String existingLogicalName = ( String ) physicalToLogical.put( physicalName, logicalName );
 				if ( existingLogicalName != null && ! existingLogicalName.equals( logicalName ) ) {
 					throw new DuplicateMappingException(
 							" Table [" + tableName + "] contains phyical column name [" + physicalName
 									+ "] represented by different logical column names: [" + existingLogicalName
 									+ "], [" + logicalName + "]",
 							"column-binding",
 							tableName + "." + physicalName
 					);
 				}
 			}
 		}
 
 		public void addColumnBinding(String logicalName, Column physicalColumn, Table table) throws DuplicateMappingException {
 			TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( table );
 			if ( binding == null ) {
 				binding = new TableColumnNameBinding( table.getName() );
 				columnNameBindingPerTable.put( table, binding );
 			}
 			binding.addBinding( logicalName, physicalColumn );
 		}
 
 		public String getPhysicalColumnName(String logicalName, Table table) throws MappingException {
 			logicalName = logicalName.toLowerCase();
 			String finalName = null;
 			Table currentTable = table;
 			do {
 				TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( currentTable );
 				if ( binding != null ) {
 					finalName = ( String ) binding.logicalToPhysical.get( logicalName );
 				}
 				String key = buildTableNameKey(
 						currentTable.getQuotedSchema(), currentTable.getCatalog(), currentTable.getQuotedName()
 				);
 				TableDescription description = ( TableDescription ) tableNameBinding.get( key );
 				if ( description != null ) {
 					currentTable = description.denormalizedSupertable;
 				}
 				else {
 					currentTable = null;
 				}
 			} while ( finalName == null && currentTable != null );
 
 			if ( finalName == null ) {
 				throw new MappingException(
 						"Unable to find column with logical name " + logicalName + " in table " + table.getName()
 				);
 			}
 			return finalName;
 		}
 
 		public String getLogicalColumnName(String physicalName, Table table) throws MappingException {
 			String logical = null;
 			Table currentTable = table;
 			TableDescription description = null;
 			do {
 				TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( currentTable );
 				if ( binding != null ) {
 					logical = ( String ) binding.physicalToLogical.get( physicalName );
 				}
 				String key = buildTableNameKey(
 						currentTable.getQuotedSchema(), currentTable.getCatalog(), currentTable.getQuotedName()
 				);
 				description = ( TableDescription ) tableNameBinding.get( key );
 				if ( description != null ) {
 					currentTable = description.denormalizedSupertable;
 				}
 				else {
 					currentTable = null;
 				}
 			}
 			while ( logical == null && currentTable != null && description != null );
 			if ( logical == null ) {
 				throw new MappingException(
 						"Unable to find logical column name from physical name "
 								+ physicalName + " in table " + table.getName()
 				);
 			}
 			return logical;
 		}
 
 		public void addSecondPass(SecondPass sp) {
 			addSecondPass( sp, false );
 		}
 
 		public void addSecondPass(SecondPass sp, boolean onTopOfTheQueue) {
 			if ( onTopOfTheQueue ) {
 				secondPasses.add( 0, sp );
 			}
 			else {
 				secondPasses.add( sp );
 			}
 		}
 
 		public void addPropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, false ) );
 		}
 
 		public void addUniquePropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, true ) );
 		}
 
 		public void addToExtendsQueue(ExtendsQueueEntry entry) {
 			extendsQueue.put( entry, null );
 		}
 
 		public DefaultIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 			return identifierGeneratorFactory;
 		}
 
 		public void addMappedSuperclass(Class type, MappedSuperclass mappedSuperclass) {
 			mappedSuperClasses.put( type, mappedSuperclass );
 		}
 
 		public MappedSuperclass getMappedSuperclass(Class type) {
 			return mappedSuperClasses.get( type );
 		}
 
 		public ObjectNameNormalizer getObjectNameNormalizer() {
 			return normalizer;
 		}
 
 		public Properties getConfigurationProperties() {
 			return properties;
 		}
 
 
 		private Boolean useNewGeneratorMappings;
 
 		public void addDefaultGenerator(IdGenerator generator) {
 			this.addGenerator( generator );
 			defaultNamedGenerators.add( generator.getName() );
 		}
 
 		public boolean isInSecondPass() {
 			return inSecondPass;
 		}
 
 		public PropertyData getPropertyAnnotatedWithMapsId(XClass entityType, String propertyName) {
 			final Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			return map == null ? null : map.get( propertyName );
 		}
 
 		public void addPropertyAnnotatedWithMapsId(XClass entityType, PropertyData property) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithMapsId.put( entityType, map );
 			}
 			map.put( property.getProperty().getAnnotation( MapsId.class ).value(), property );
 		}
 
 		public boolean isSpecjProprietarySyntaxEnabled() {
 			return specjProprietarySyntaxEnabled;
 		}
 
 		public void addPropertyAnnotatedWithMapsIdSpecj(XClass entityType, PropertyData property, String mapsIdValue) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithMapsId.put( entityType, map );
 			}
 			map.put( mapsIdValue, property );
 		}
 
 		public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName) {
 			final Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 			return map == null ? null : map.get( propertyName );
 		}
 
 		public void addToOneAndIdProperty(XClass entityType, PropertyData property) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithIdAndToOne.put( entityType, map );
 			}
 			map.put( property.getPropertyName(), property );
 		}
 
 		@SuppressWarnings({ "UnnecessaryUnboxing" })
 		public boolean useNewGeneratorMappings() {
 			if ( useNewGeneratorMappings == null ) {
 				final String booleanName = getConfigurationProperties().getProperty( USE_NEW_ID_GENERATOR_MAPPINGS );
 				useNewGeneratorMappings = Boolean.valueOf( booleanName );
 			}
 			return useNewGeneratorMappings.booleanValue();
 		}
 
 		public IdGenerator getGenerator(String name) {
 			return getGenerator( name, null );
 		}
 
 		public IdGenerator getGenerator(String name, Map<String, IdGenerator> localGenerators) {
 			if ( localGenerators != null ) {
 				IdGenerator result = localGenerators.get( name );
 				if ( result != null ) {
 					return result;
 				}
 			}
 			return namedGenerators.get( name );
 		}
 
 		public void addGenerator(IdGenerator generator) {
 			if ( !defaultNamedGenerators.contains( generator.getName() ) ) {
 				IdGenerator old = namedGenerators.put( generator.getName(), generator );
                 if (old != null) LOG.duplicateGeneratorName(old.getName());
 			}
 		}
 
 		public void addGeneratorTable(String name, Properties params) {
 			Object old = generatorTables.put( name, params );
             if (old != null) LOG.duplicateGeneratorTable(name);
 		}
 
 		public Properties getGeneratorTableProperties(String name, Map<String, Properties> localGeneratorTables) {
 			if ( localGeneratorTables != null ) {
 				Properties result = localGeneratorTables.get( name );
 				if ( result != null ) {
 					return result;
 				}
 			}
 			return generatorTables.get( name );
 		}
 
 		public Map<String, Join> getJoins(String entityName) {
 			return joins.get( entityName );
 		}
 
 		public void addJoins(PersistentClass persistentClass, Map<String, Join> joins) {
 			Object old = Configuration.this.joins.put( persistentClass.getEntityName(), joins );
             if (old != null) LOG.duplicateJoins(persistentClass.getEntityName());
 		}
 
 		public AnnotatedClassType getClassType(XClass clazz) {
 			AnnotatedClassType type = classTypes.get( clazz.getName() );
 			if ( type == null ) {
 				return addClassType( clazz );
 			}
 			else {
 				return type;
 			}
 		}
 
 		//FIXME should be private but is part of the ExtendedMapping contract
 
 		public AnnotatedClassType addClassType(XClass clazz) {
 			AnnotatedClassType type;
 			if ( clazz.isAnnotationPresent( Entity.class ) ) {
 				type = AnnotatedClassType.ENTITY;
 			}
 			else if ( clazz.isAnnotationPresent( Embeddable.class ) ) {
 				type = AnnotatedClassType.EMBEDDABLE;
 			}
 			else if ( clazz.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 				type = AnnotatedClassType.EMBEDDABLE_SUPERCLASS;
 			}
 			else {
 				type = AnnotatedClassType.NONE;
 			}
 			classTypes.put( clazz.getName(), type );
 			return type;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Map<Table, List<String[]>> getTableUniqueConstraints() {
 			final Map<Table, List<String[]>> deprecatedStructure = new HashMap<Table, List<String[]>>(
 					CollectionHelper.determineProperSizing( getUniqueConstraintHoldersByTable() ),
 					CollectionHelper.LOAD_FACTOR
 			);
 			for ( Map.Entry<Table, List<UniqueConstraintHolder>> entry : getUniqueConstraintHoldersByTable().entrySet() ) {
 				List<String[]> columnsPerConstraint = new ArrayList<String[]>(
 						CollectionHelper.determineProperSizing( entry.getValue().size() )
 				);
 				deprecatedStructure.put( entry.getKey(), columnsPerConstraint );
 				for ( UniqueConstraintHolder holder : entry.getValue() ) {
 					columnsPerConstraint.add( holder.getColumns() );
 				}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/ExternalSessionFactoryConfig.java b/hibernate-core/src/main/java/org/hibernate/cfg/ExternalSessionFactoryConfig.java
index 8fb72177af..327e9eb91b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ExternalSessionFactoryConfig.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ExternalSessionFactoryConfig.java
@@ -1,396 +1,342 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
- *
  */
 package org.hibernate.cfg;
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
-import org.hibernate.internal.util.StringHelper;
+
 import org.hibernate.internal.util.config.ConfigurationHelper;
 
 /**
  * Defines support for various externally configurable SessionFactory(s), for
  * example, {@link org.hibernate.jmx.HibernateService JMX} or the JCA
  * adapter.
  *
  * @author Steve Ebersole
  */
 public abstract class ExternalSessionFactoryConfig {
-
 	private String mapResources;
 	private String dialect;
 	private String defaultSchema;
 	private String defaultCatalog;
 	private String maximumFetchDepth;
 	private String jdbcFetchSize;
 	private String jdbcBatchSize;
 	private String batchVersionedDataEnabled;
 	private String jdbcScrollableResultSetEnabled;
 	private String getGeneratedKeysEnabled;
 	private String streamsForBinaryEnabled;
 	private String reflectionOptimizationEnabled;
 	private String querySubstitutions;
 	private String showSqlEnabled;
 	private String commentsEnabled;
 	private String cacheProviderClass;
 	private String cacheProviderConfig;
 	private String cacheRegionPrefix;
 	private String secondLevelCacheEnabled;
 	private String minimalPutsEnabled;
 	private String queryCacheEnabled;
 
 	private Map additionalProperties;
 	private Set excludedPropertyNames = new HashSet();
-	private Map customListeners;
-
 
 	protected Set getExcludedPropertyNames() {
 		return excludedPropertyNames;
 	}
 
 	public final String getMapResources() {
 		return mapResources;
 	}
 
 	public final void setMapResources(String mapResources) {
 		this.mapResources = mapResources;
 	}
 
 	public void addMapResource(String mapResource) {
 		if ( mapResources==null || mapResources.length()==0 ) {
 			mapResources = mapResource.trim();
 		}
 		else {
 			mapResources += ", " + mapResource.trim();
 		}
 	}
 
 	public final String getDialect() {
 		return dialect;
 	}
 
 	public final void setDialect(String dialect) {
 		this.dialect = dialect;
 	}
 
 	public final String getDefaultSchema() {
 		return defaultSchema;
 	}
 
 	public final void setDefaultSchema(String defaultSchema) {
 		this.defaultSchema = defaultSchema;
 	}
 
 	public final String getDefaultCatalog() {
 		return defaultCatalog;
 	}
 
 	public final void setDefaultCatalog(String defaultCatalog) {
 		this.defaultCatalog = defaultCatalog;
 	}
 
 	public final String getMaximumFetchDepth() {
 		return maximumFetchDepth;
 	}
 
 	public final void setMaximumFetchDepth(String maximumFetchDepth) {
 		verifyInt( maximumFetchDepth );
 		this.maximumFetchDepth = maximumFetchDepth;
 	}
 
 	public final String getJdbcFetchSize() {
 		return jdbcFetchSize;
 	}
 
 	public final void setJdbcFetchSize(String jdbcFetchSize) {
 		verifyInt( jdbcFetchSize );
 		this.jdbcFetchSize = jdbcFetchSize;
 	}
 
 	public final String getJdbcBatchSize() {
 		return jdbcBatchSize;
 	}
 
 	public final void setJdbcBatchSize(String jdbcBatchSize) {
 		verifyInt( jdbcBatchSize );
 		this.jdbcBatchSize = jdbcBatchSize;
 	}
 
 	public final String getBatchVersionedDataEnabled() {
 		return batchVersionedDataEnabled;
 	}
 
 	public final void setBatchVersionedDataEnabled(String batchVersionedDataEnabled) {
 		this.batchVersionedDataEnabled = batchVersionedDataEnabled;
 	}
 
 	public final String getJdbcScrollableResultSetEnabled() {
 		return jdbcScrollableResultSetEnabled;
 	}
 
 	public final void setJdbcScrollableResultSetEnabled(String jdbcScrollableResultSetEnabled) {
 		this.jdbcScrollableResultSetEnabled = jdbcScrollableResultSetEnabled;
 	}
 
 	public final String getGetGeneratedKeysEnabled() {
 		return getGeneratedKeysEnabled;
 	}
 
 	public final void setGetGeneratedKeysEnabled(String getGeneratedKeysEnabled) {
 		this.getGeneratedKeysEnabled = getGeneratedKeysEnabled;
 	}
 
 	public final String getStreamsForBinaryEnabled() {
 		return streamsForBinaryEnabled;
 	}
 
 	public final void setStreamsForBinaryEnabled(String streamsForBinaryEnabled) {
 		this.streamsForBinaryEnabled = streamsForBinaryEnabled;
 	}
 
 	public final String getReflectionOptimizationEnabled() {
 		return reflectionOptimizationEnabled;
 	}
 
 	public final void setReflectionOptimizationEnabled(String reflectionOptimizationEnabled) {
 		this.reflectionOptimizationEnabled = reflectionOptimizationEnabled;
 	}
 
 	public final String getQuerySubstitutions() {
 		return querySubstitutions;
 	}
 
 	public final void setQuerySubstitutions(String querySubstitutions) {
 		this.querySubstitutions = querySubstitutions;
 	}
 
 	public final String getShowSqlEnabled() {
 		return showSqlEnabled;
 	}
 
 	public final void setShowSqlEnabled(String showSqlEnabled) {
 		this.showSqlEnabled = showSqlEnabled;
 	}
 
 	public final String getCommentsEnabled() {
 		return commentsEnabled;
 	}
 
 	public final void setCommentsEnabled(String commentsEnabled) {
 		this.commentsEnabled = commentsEnabled;
 	}
 
 	public final String getSecondLevelCacheEnabled() {
 		return secondLevelCacheEnabled;
 	}
 
 	public final void setSecondLevelCacheEnabled(String secondLevelCacheEnabled) {
 		this.secondLevelCacheEnabled = secondLevelCacheEnabled;
 	}
 
 	public final String getCacheProviderClass() {
 		return cacheProviderClass;
 	}
 
 	public final void setCacheProviderClass(String cacheProviderClass) {
 		this.cacheProviderClass = cacheProviderClass;
 	}
 
 	public String getCacheProviderConfig() {
 		return cacheProviderConfig;
 	}
 
 	public void setCacheProviderConfig(String cacheProviderConfig) {
 		this.cacheProviderConfig = cacheProviderConfig;
 	}
 
 	public final String getCacheRegionPrefix() {
 		return cacheRegionPrefix;
 	}
 
 	public final void setCacheRegionPrefix(String cacheRegionPrefix) {
 		this.cacheRegionPrefix = cacheRegionPrefix;
 	}
 
 	public final String getMinimalPutsEnabled() {
 		return minimalPutsEnabled;
 	}
 
 	public final void setMinimalPutsEnabled(String minimalPutsEnabled) {
 		this.minimalPutsEnabled = minimalPutsEnabled;
 	}
 
 	public final String getQueryCacheEnabled() {
 		return queryCacheEnabled;
 	}
 
 	public final void setQueryCacheEnabled(String queryCacheEnabled) {
 		this.queryCacheEnabled = queryCacheEnabled;
 	}
 
-	public final Map getCustomListeners() {
-		return customListeners;
-	}
-
-	public void setCustomListeners(Map customListeners) {
-		this.customListeners = customListeners;
-	}
-
-	public void setCustomListenersAsString(String customListenersString) {
-		// Note : expected in the syntax:
-		//      type=listenerClass
-		//          ({sep}type=listenerClass)*
-		// where {sep} is any whitespace or comma
-		if ( StringHelper.isNotEmpty( customListenersString ) ) {
-			String[] listenerEntries = ConfigurationHelper.toStringArray( customListenersString, " ,\n\t\r\f" );
-			for ( int i = 0; i < listenerEntries.length; i++ ) {
-				final int keyValueSepPosition = listenerEntries[i].indexOf( '=' );
-				final String type = listenerEntries[i].substring( 0, keyValueSepPosition );
-				final String listenerClass = listenerEntries[i].substring( keyValueSepPosition + 1 );
-				setCustomListener( type, listenerClass );
-			}
-		}
-	}
-
-	public void setCustomListener(String type, String listenerClass) {
-		if ( customListeners == null ) {
-			customListeners = new HashMap();
-		}
-		customListeners.put( type, listenerClass );
-	}
-
 	public final void addAdditionalProperty(String name, String value) {
 		if ( !getExcludedPropertyNames().contains( name ) ) {
 			if ( additionalProperties == null ) {
 				additionalProperties = new HashMap();
 			}
 			additionalProperties.put( name, value );
 		}
 	}
 
 	protected final Configuration buildConfiguration() {
 
 		Configuration cfg = new Configuration().setProperties( buildProperties() );
 
 
 		String[] mappingFiles = ConfigurationHelper.toStringArray( mapResources, " ,\n\t\r\f" );
 		for ( int i = 0; i < mappingFiles.length; i++ ) {
 			cfg.addResource( mappingFiles[i] );
 		}
 
-		if ( customListeners != null && !customListeners.isEmpty() ) {
-			Iterator entries = customListeners.entrySet().iterator();
-			while ( entries.hasNext() ) {
-				final Map.Entry entry = ( Map.Entry ) entries.next();
-				final String type = ( String ) entry.getKey();
-				final Object value = entry.getValue();
-				if ( value != null ) {
-					if ( String.class.isAssignableFrom( value.getClass() ) ) {
-						// Its the listener class name
-						cfg.setListener( type, ( ( String ) value ) );
-					}
-					else {
-						// Its the listener instance (or better be)
-						cfg.setListener( type, value );
-					}
-				}
-			}
-		}
-
 		return cfg;
 	}
 
 	protected final Properties buildProperties() {
 		Properties props = new Properties();
 		setUnlessNull( props, Environment.DIALECT, dialect );
 		setUnlessNull( props, Environment.DEFAULT_SCHEMA, defaultSchema );
 		setUnlessNull( props, Environment.DEFAULT_CATALOG, defaultCatalog );
 		setUnlessNull( props, Environment.MAX_FETCH_DEPTH, maximumFetchDepth );
 		setUnlessNull( props, Environment.STATEMENT_FETCH_SIZE, jdbcFetchSize );
 		setUnlessNull( props, Environment.STATEMENT_BATCH_SIZE, jdbcBatchSize );
 		setUnlessNull( props, Environment.BATCH_VERSIONED_DATA, batchVersionedDataEnabled );
 		setUnlessNull( props, Environment.USE_SCROLLABLE_RESULTSET, jdbcScrollableResultSetEnabled );
 		setUnlessNull( props, Environment.USE_GET_GENERATED_KEYS, getGeneratedKeysEnabled );
 		setUnlessNull( props, Environment.USE_STREAMS_FOR_BINARY, streamsForBinaryEnabled );
 		setUnlessNull( props, Environment.USE_REFLECTION_OPTIMIZER, reflectionOptimizationEnabled );
 		setUnlessNull( props, Environment.QUERY_SUBSTITUTIONS, querySubstitutions );
 		setUnlessNull( props, Environment.SHOW_SQL, showSqlEnabled );
 		setUnlessNull( props, Environment.USE_SQL_COMMENTS, commentsEnabled );
 		setUnlessNull( props, Environment.CACHE_PROVIDER, cacheProviderClass );
 		setUnlessNull( props, Environment.CACHE_PROVIDER_CONFIG, cacheProviderConfig );
 		setUnlessNull( props, Environment.CACHE_REGION_PREFIX, cacheRegionPrefix );
 		setUnlessNull( props, Environment.USE_MINIMAL_PUTS, minimalPutsEnabled );
 		setUnlessNull( props, Environment.USE_SECOND_LEVEL_CACHE, secondLevelCacheEnabled );
 		setUnlessNull( props, Environment.USE_QUERY_CACHE, queryCacheEnabled );
 
 		Map extraProperties = getExtraProperties();
 		if ( extraProperties != null ) {
 			addAll( props, extraProperties );
 		}
 
 		if ( additionalProperties != null ) {
 			addAll( props, additionalProperties );
 		}
 
 		return props;
 	}
 
 	protected void addAll( Properties target, Map source ) {
 		Iterator itr = source.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String propertyName = ( String ) entry.getKey();
 			final String propertyValue = ( String ) entry.getValue();
 			if ( propertyName != null && propertyValue != null ) {
 				// Make sure we don't override previous set values
 				if ( !target.keySet().contains( propertyName ) ) {
 					if ( !getExcludedPropertyNames().contains( propertyName) ) {
 						target.put( propertyName, propertyValue );
 					}
 				}
 			}
 		}
 	}
 
 	protected Map getExtraProperties() {
 		return null;
 	}
 
 	private void setUnlessNull(Properties props, String key, String value) {
 		if ( value != null ) {
 			props.setProperty( key, value );
 		}
 	}
 
 	private void verifyInt(String value)
 	{
 		if ( value != null ) {
 			Integer.parseInt( value );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java
index ecffcc72cb..4078637e3d 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationActivator.java
@@ -1,173 +1,175 @@
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
 package org.hibernate.cfg.beanvalidation;
+
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Properties;
 import java.util.Set;
+
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.event.EventListeners;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.PersistentClass;
+import org.hibernate.service.event.spi.EventListenerRegistry;
 
 /**
  * This class has no hard dependency on Bean Validation APIs
  * It must use reflection every time BV is required.
  * @author Emmanuel Bernard
  */
 public class BeanValidationActivator {
-
 	private static final String BV_DISCOVERY_CLASS = "javax.validation.Validation";
 	private static final String TYPE_SAFE_ACTIVATOR_CLASS = "org.hibernate.cfg.beanvalidation.TypeSafeActivator";
 	private static final String TYPE_SAFE_DDL_METHOD = "applyDDL";
 	private static final String TYPE_SAFE_ACTIVATOR_METHOD = "activateBeanValidation";
 	private static final String MODE_PROPERTY = "javax.persistence.validation.mode";
 
-	public static void activateBeanValidation(EventListeners eventListeners, Properties properties) {
+	public static void activateBeanValidation(EventListenerRegistry listenerRegistry, Properties properties) {
 		Set<ValidationMode> modes = ValidationMode.getModes( properties.get( MODE_PROPERTY ) );
 
 		try {
 			//load Validation
 			ReflectHelper.classForName( BV_DISCOVERY_CLASS, BeanValidationActivator.class );
 		}
 		catch ( ClassNotFoundException e ) {
 			if ( modes.contains( ValidationMode.CALLBACK ) ) {
 				throw new HibernateException( "Bean Validation not available in the class path but required in " + MODE_PROPERTY );
 			}
 			else if (modes.contains( ValidationMode.AUTO ) ) {
 				//nothing to activate
 				return;
 			}
 		}
 
 		//de-activate not-null tracking at the core level when Bean Validation
 		// is present unless the user really asks for it
 		//Note that if BV is not present, the behavior is backward compatible
 		if ( properties.getProperty( Environment.CHECK_NULLABILITY ) == null ) {
 			properties.setProperty( Environment.CHECK_NULLABILITY, "false" );
 		}
 
 		if ( ! ( modes.contains( ValidationMode.CALLBACK ) || modes.contains( ValidationMode.AUTO ) ) ) return;
 
 		try {
 			Class<?> activator = ReflectHelper.classForName( TYPE_SAFE_ACTIVATOR_CLASS, BeanValidationActivator.class );
 			Method activateBeanValidation =
-					activator.getMethod( TYPE_SAFE_ACTIVATOR_METHOD, EventListeners.class, Properties.class );
-			activateBeanValidation.invoke( null, eventListeners, properties );
+					activator.getMethod( TYPE_SAFE_ACTIVATOR_METHOD, EventListenerRegistry.class, Properties.class );
+			activateBeanValidation.invoke( null, listenerRegistry, properties );
 		}
 		catch ( NoSuchMethodException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 		catch ( IllegalAccessException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 		catch ( InvocationTargetException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 		catch ( ClassNotFoundException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 	}
 
 	public static void applyDDL(Collection<PersistentClass> persistentClasses, Properties properties) {
 		Set<ValidationMode> modes = ValidationMode.getModes( properties.get( MODE_PROPERTY ) );
 		if ( ! ( modes.contains( ValidationMode.DDL ) || modes.contains( ValidationMode.AUTO ) ) ) return;
 		try {
 			//load Validation
 			ReflectHelper.classForName( BV_DISCOVERY_CLASS, BeanValidationActivator.class );
 		}
 		catch ( ClassNotFoundException e ) {
 			if ( modes.contains( ValidationMode.DDL ) ) {
 				throw new HibernateException( "Bean Validation not available in the class path but required in " + MODE_PROPERTY );
 			}
 			else if (modes.contains( ValidationMode.AUTO ) ) {
 				//nothing to activate
 				return;
 			}
 		}
 		try {
 			Class<?> activator = ReflectHelper.classForName( TYPE_SAFE_ACTIVATOR_CLASS, BeanValidationActivator.class );
 			Method applyDDL =
 					activator.getMethod( TYPE_SAFE_DDL_METHOD, Collection.class, Properties.class );
 			applyDDL.invoke( null, persistentClasses, properties );
 		}
 		catch ( NoSuchMethodException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 		catch ( IllegalAccessException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 		catch ( InvocationTargetException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 		catch ( ClassNotFoundException e ) {
 			throw new HibernateException( "Unable to get the default Bean Validation factory", e);
 		}
 	}
 
 	private static enum ValidationMode {
 		AUTO,
 		CALLBACK,
 		NONE,
 		DDL;
 
 		public static Set<ValidationMode> getModes(Object modeProperty) {
 			Set<ValidationMode> modes = new HashSet<ValidationMode>(3);
 			if (modeProperty == null) {
 				modes.add(ValidationMode.AUTO);
 			}
 			else {
 				final String[] modesInString = modeProperty.toString().split( "," );
 				for ( String modeInString : modesInString ) {
 					modes.add( getMode(modeInString) );
 				}
 			}
 			if ( modes.size() > 1 && ( modes.contains( ValidationMode.AUTO ) || modes.contains( ValidationMode.NONE ) ) ) {
 				StringBuilder message = new StringBuilder( "Incompatible validation modes mixed: " );
 				for (ValidationMode mode : modes) {
 					message.append( mode ).append( ", " );
 				}
 				throw new HibernateException( message.substring( 0, message.length() - 2 ) );
 			}
 			return modes;
 		}
 
 		private static ValidationMode getMode(String modeProperty) {
 			if (modeProperty == null || modeProperty.length() == 0) {
 				return AUTO;
 			}
 			else {
 				try {
 					return valueOf( modeProperty.trim().toUpperCase() );
 				}
 				catch ( IllegalArgumentException e ) {
 					throw new HibernateException( "Unknown validation mode in " + MODE_PROPERTY + ": " + modeProperty.toString() );
 				}
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/DuplicationStrategyImpl.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/DuplicationStrategyImpl.java
new file mode 100644
index 0000000000..68d747b6a5
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/DuplicationStrategyImpl.java
@@ -0,0 +1,44 @@
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
+package org.hibernate.cfg.beanvalidation;
+
+import org.hibernate.service.event.spi.DuplicationStrategy;
+
+/**
+ * @author Steve Ebersole
+ */
+public class DuplicationStrategyImpl implements DuplicationStrategy {
+	public static final DuplicationStrategyImpl INSTANCE = new DuplicationStrategyImpl();
+
+	@Override
+	public boolean areMatch(Object listener, Object original) {
+		return listener.getClass().equals( original.getClass() ) &&
+				BeanValidationEventListener.class.equals( listener.getClass() );
+	}
+
+	@Override
+	public Action getAction() {
+		return Action.KEEP_ORIGINAL;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
index 0597067435..966daeafa8 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
@@ -1,386 +1,367 @@
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
 package org.hibernate.cfg.beanvalidation;
 
-import java.util.Arrays;
-import java.util.Collection;
-import java.util.Collections;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.Map;
-import java.util.Properties;
-import java.util.Set;
-import java.util.StringTokenizer;
 import javax.validation.Validation;
 import javax.validation.ValidatorFactory;
 import javax.validation.constraints.Digits;
 import javax.validation.constraints.Max;
 import javax.validation.constraints.Min;
 import javax.validation.constraints.NotNull;
 import javax.validation.constraints.Size;
 import javax.validation.metadata.BeanDescriptor;
 import javax.validation.metadata.ConstraintDescriptor;
 import javax.validation.metadata.PropertyDescriptor;
+import java.util.Arrays;
+import java.util.Collection;
+import java.util.Collections;
+import java.util.HashSet;
+import java.util.Iterator;
+import java.util.Map;
+import java.util.Properties;
+import java.util.Set;
+import java.util.StringTokenizer;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.MappingException;
-import org.hibernate.event.EventListeners;
-import org.hibernate.event.PreDeleteEventListener;
-import org.hibernate.event.PreInsertEventListener;
-import org.hibernate.event.PreUpdateEventListener;
+import org.hibernate.event.EventType;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SingleTableSubclass;
-import org.jboss.logging.Logger;
+import org.hibernate.service.event.spi.EventListenerRegistry;
 
 /**
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 class TypeSafeActivator {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, TypeSafeActivator.class.getName());
 
 	private static final String FACTORY_PROPERTY = "javax.persistence.validation.factory";
 
-	public static void activateBeanValidation(EventListeners eventListeners, Properties properties) {
+	@SuppressWarnings( {"UnusedDeclaration"})
+	public static void activateBeanValidation(EventListenerRegistry listenerRegistry, Properties properties) {
 		ValidatorFactory factory = getValidatorFactory( properties );
-		BeanValidationEventListener beanValidationEventListener = new BeanValidationEventListener(
+		BeanValidationEventListener listener = new BeanValidationEventListener(
 				factory, properties
 		);
 
-		{
-			PreInsertEventListener[] listeners = eventListeners.getPreInsertEventListeners();
-			int length = listeners.length + 1;
-			PreInsertEventListener[] newListeners = new PreInsertEventListener[length];
-			System.arraycopy( listeners, 0, newListeners, 0, length - 1 );
-			newListeners[length - 1] = beanValidationEventListener;
-			eventListeners.setPreInsertEventListeners( newListeners );
-		}
-
-		{
-			PreUpdateEventListener[] listeners = eventListeners.getPreUpdateEventListeners();
-			int length = listeners.length + 1;
-			PreUpdateEventListener[] newListeners = new PreUpdateEventListener[length];
-			System.arraycopy( listeners, 0, newListeners, 0, length - 1 );
-			newListeners[length - 1] = beanValidationEventListener;
-			eventListeners.setPreUpdateEventListeners( newListeners );
-		}
+		listenerRegistry.addDuplicationStrategy( DuplicationStrategyImpl.INSTANCE );
 
-		{
-			PreDeleteEventListener[] listeners = eventListeners.getPreDeleteEventListeners();
-			int length = listeners.length + 1;
-			PreDeleteEventListener[] newListeners = new PreDeleteEventListener[length];
-			System.arraycopy( listeners, 0, newListeners, 0, length - 1 );
-			newListeners[length - 1] = beanValidationEventListener;
-			eventListeners.setPreDeleteEventListeners( newListeners );
-		}
+		listenerRegistry.appendListeners( EventType.PRE_INSERT, listener );
+		listenerRegistry.appendListeners( EventType.PRE_UPDATE, listener );
+		listenerRegistry.appendListeners( EventType.PRE_DELETE, listener );
 	}
 
+	@SuppressWarnings( {"UnusedDeclaration"})
 	public static void applyDDL(Collection<PersistentClass> persistentClasses, Properties properties) {
 		ValidatorFactory factory = getValidatorFactory( properties );
 		Class<?>[] groupsArray = new GroupsPerOperation( properties ).get( GroupsPerOperation.Operation.DDL );
 		Set<Class<?>> groups = new HashSet<Class<?>>( Arrays.asList( groupsArray ) );
 
 		for ( PersistentClass persistentClass : persistentClasses ) {
 			final String className = persistentClass.getClassName();
 
 			if ( className == null || className.length() == 0 ) {
 				continue;
 			}
 			Class<?> clazz;
 			try {
 				clazz = ReflectHelper.classForName( className, TypeSafeActivator.class );
 			}
 			catch ( ClassNotFoundException e ) {
 				throw new AssertionFailure( "Entity class not found", e );
 			}
 
 			try {
 				applyDDL( "", persistentClass, clazz, factory, groups, true );
 			}
 			catch (Exception e) {
                 LOG.unableToApplyConstraints(className, e);
 			}
 		}
 	}
 
 	private static void applyDDL(String prefix,
 								 PersistentClass persistentClass,
 								 Class<?> clazz,
 								 ValidatorFactory factory,
 								 Set<Class<?>> groups,
 								 boolean activateNotNull) {
 		final BeanDescriptor descriptor = factory.getValidator().getConstraintsForClass( clazz );
 		//no bean level constraints can be applied, go to the properties
 
 		for ( PropertyDescriptor propertyDesc : descriptor.getConstrainedProperties() ) {
 			Property property = findPropertyByName( persistentClass, prefix + propertyDesc.getPropertyName() );
 			boolean hasNotNull;
 			if ( property != null ) {
 				hasNotNull = applyConstraints(
 						propertyDesc.getConstraintDescriptors(), property, propertyDesc, groups, activateNotNull
 				);
 				if ( property.isComposite() && propertyDesc.isCascaded() ) {
 					Class<?> componentClass = ( (Component) property.getValue() ).getComponentClass();
 
 					/*
 					 * we can apply not null if the upper component let's us activate not null
 					 * and if the property is not null.
 					 * Otherwise, all sub columns should be left nullable
 					 */
 					final boolean canSetNotNullOnColumns = activateNotNull && hasNotNull;
 					applyDDL(
 							prefix + propertyDesc.getPropertyName() + ".",
 							persistentClass, componentClass, factory, groups,
 							canSetNotNullOnColumns
 					);
 				}
 				//FIXME add collection of components
 			}
 		}
 	}
 
 	private static boolean applyConstraints(Set<ConstraintDescriptor<?>> constraintDescriptors,
 											Property property,
 											PropertyDescriptor propertyDesc,
 											Set<Class<?>> groups,
 											boolean canApplyNotNull
 	) {
 		boolean hasNotNull = false;
 		for ( ConstraintDescriptor<?> descriptor : constraintDescriptors ) {
 			if ( groups != null && Collections.disjoint( descriptor.getGroups(), groups ) ) {
 				continue;
 			}
 
 			if ( canApplyNotNull ) {
 				hasNotNull = hasNotNull || applyNotNull( property, descriptor );
 			}
 
 			// apply bean validation specific constraints
 			applyDigits( property, descriptor );
 			applySize( property, descriptor, propertyDesc );
 			applyMin( property, descriptor );
 			applyMax( property, descriptor );
 
 			// apply hibernate validator specific constraints - we cannot import any HV specific classes though!
 			// no need to check explicitly for @Range. @Range is a composed constraint using @Min and @Max which
 			// will be taken care later
 			applyLength( property, descriptor, propertyDesc );
 
 			// pass an empty set as composing constraints inherit the main constraint and thus are matching already
 			hasNotNull = hasNotNull || applyConstraints(
 					descriptor.getComposingConstraints(),
 					property, propertyDesc, null,
 					canApplyNotNull
 			);
 		}
 		return hasNotNull;
 	}
 
 	private static void applyMin(Property property, ConstraintDescriptor<?> descriptor) {
 		if ( Min.class.equals( descriptor.getAnnotation().annotationType() ) ) {
 			@SuppressWarnings("unchecked")
 			ConstraintDescriptor<Min> minConstraint = (ConstraintDescriptor<Min>) descriptor;
 			long min = minConstraint.getAnnotation().value();
 
 			Column col = (Column) property.getColumnIterator().next();
 			String checkConstraint = col.getName() + ">=" + min;
 			applySQLCheck( col, checkConstraint );
 		}
 	}
 
 	private static void applyMax(Property property, ConstraintDescriptor<?> descriptor) {
 		if ( Max.class.equals( descriptor.getAnnotation().annotationType() ) ) {
 			@SuppressWarnings("unchecked")
 			ConstraintDescriptor<Max> maxConstraint = (ConstraintDescriptor<Max>) descriptor;
 			long max = maxConstraint.getAnnotation().value();
 			Column col = (Column) property.getColumnIterator().next();
 			String checkConstraint = col.getName() + "<=" + max;
 			applySQLCheck( col, checkConstraint );
 		}
 	}
 
 	private static void applySQLCheck(Column col, String checkConstraint) {
 		String existingCheck = col.getCheckConstraint();
 		// need to check whether the new check is already part of the existing check, because applyDDL can be called
 		// multiple times
 		if ( StringHelper.isNotEmpty( existingCheck ) && !existingCheck.contains( checkConstraint ) ) {
 			checkConstraint = col.getCheckConstraint() + " AND " + checkConstraint;
 		}
 		col.setCheckConstraint( checkConstraint );
 	}
 
 	private static boolean applyNotNull(Property property, ConstraintDescriptor<?> descriptor) {
 		boolean hasNotNull = false;
 		if ( NotNull.class.equals( descriptor.getAnnotation().annotationType() ) ) {
 			if ( !( property.getPersistentClass() instanceof SingleTableSubclass ) ) {
 				//single table should not be forced to null
 				if ( !property.isComposite() ) { //composite should not add not-null on all columns
 					@SuppressWarnings( "unchecked" )
 					Iterator<Column> iter = property.getColumnIterator();
 					while ( iter.hasNext() ) {
 						iter.next().setNullable( false );
 						hasNotNull = true;
 					}
 				}
 			}
 			hasNotNull = true;
 		}
 		return hasNotNull;
 	}
 
 	private static void applyDigits(Property property, ConstraintDescriptor<?> descriptor) {
 		if ( Digits.class.equals( descriptor.getAnnotation().annotationType() ) ) {
 			@SuppressWarnings("unchecked")
 			ConstraintDescriptor<Digits> digitsConstraint = (ConstraintDescriptor<Digits>) descriptor;
 			int integerDigits = digitsConstraint.getAnnotation().integer();
 			int fractionalDigits = digitsConstraint.getAnnotation().fraction();
 			Column col = (Column) property.getColumnIterator().next();
 			col.setPrecision( integerDigits + fractionalDigits );
 			col.setScale( fractionalDigits );
 		}
 	}
 
 	private static void applySize(Property property, ConstraintDescriptor<?> descriptor, PropertyDescriptor propertyDescriptor) {
 		if ( Size.class.equals( descriptor.getAnnotation().annotationType() )
 				&& String.class.equals( propertyDescriptor.getElementClass() ) ) {
 			@SuppressWarnings("unchecked")
 			ConstraintDescriptor<Size> sizeConstraint = (ConstraintDescriptor<Size>) descriptor;
 			int max = sizeConstraint.getAnnotation().max();
 			Column col = (Column) property.getColumnIterator().next();
 			if ( max < Integer.MAX_VALUE ) {
 				col.setLength( max );
 			}
 		}
 	}
 
 	private static void applyLength(Property property, ConstraintDescriptor<?> descriptor, PropertyDescriptor propertyDescriptor) {
 		if ( "org.hibernate.validator.constraints.Length".equals(
 				descriptor.getAnnotation().annotationType().getName()
 		)
 				&& String.class.equals( propertyDescriptor.getElementClass() ) ) {
 			@SuppressWarnings("unchecked")
 			int max = (Integer) descriptor.getAttributes().get( "max" );
 			Column col = (Column) property.getColumnIterator().next();
 			if ( max < Integer.MAX_VALUE ) {
 				col.setLength( max );
 			}
 		}
 	}
 
 	/**
 	 * Retrieve the property by path in a recursive way, including IndentifierProperty in the loop
 	 * If propertyName is null or empty, the IdentifierProperty is returned
 	 */
 	private static Property findPropertyByName(PersistentClass associatedClass, String propertyName) {
 		Property property = null;
 		Property idProperty = associatedClass.getIdentifierProperty();
 		String idName = idProperty != null ? idProperty.getName() : null;
 		try {
 			if ( propertyName == null
 					|| propertyName.length() == 0
 					|| propertyName.equals( idName ) ) {
 				//default to id
 				property = idProperty;
 			}
 			else {
 				if ( propertyName.indexOf( idName + "." ) == 0 ) {
 					property = idProperty;
 					propertyName = propertyName.substring( idName.length() + 1 );
 				}
 				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
 				while ( st.hasMoreElements() ) {
 					String element = (String) st.nextElement();
 					if ( property == null ) {
 						property = associatedClass.getProperty( element );
 					}
 					else {
 						if ( !property.isComposite() ) {
 							return null;
 						}
 						property = ( (Component) property.getValue() ).getProperty( element );
 					}
 				}
 			}
 		}
 		catch ( MappingException e ) {
 			try {
 				//if we do not find it try to check the identifier mapper
 				if ( associatedClass.getIdentifierMapper() == null ) {
 					return null;
 				}
 				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
 				while ( st.hasMoreElements() ) {
 					String element = (String) st.nextElement();
 					if ( property == null ) {
 						property = associatedClass.getIdentifierMapper().getProperty( element );
 					}
 					else {
 						if ( !property.isComposite() ) {
 							return null;
 						}
 						property = ( (Component) property.getValue() ).getProperty( element );
 					}
 				}
 			}
 			catch ( MappingException ee ) {
 				return null;
 			}
 		}
 		return property;
 	}
 
 	private static ValidatorFactory getValidatorFactory(Map<Object, Object> properties) {
 		ValidatorFactory factory = null;
 		if ( properties != null ) {
 			Object unsafeProperty = properties.get( FACTORY_PROPERTY );
 			if ( unsafeProperty != null ) {
 				try {
 					factory = ValidatorFactory.class.cast( unsafeProperty );
 				}
 				catch ( ClassCastException e ) {
 					throw new HibernateException(
 							"Property " + FACTORY_PROPERTY
 									+ " should contain an object of type " + ValidatorFactory.class.getName()
 					);
 				}
 			}
 		}
 		if ( factory == null ) {
 			try {
 				factory = Validation.buildDefaultValidatorFactory();
 			}
 			catch ( Exception e ) {
 				throw new HibernateException( "Unable to build the default ValidatorFactory", e );
 			}
 		}
 		return factory;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
index 0d1b3dcfcd..843237ef8c 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
@@ -1,400 +1,393 @@
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
 import org.hibernate.event.EventListeners;
 import org.hibernate.impl.CriteriaImpl;
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
 	 * name, with values corresponding to the {@link org.hibernate.impl.FilterImpl}
 	 * instance.
 	 * @return The currently enabled filters.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	@Deprecated
     public Map getEnabledFilters();
 
 	public int getDontFlushFromFind();
 
-	/**
-	 * Retrieves the configured event listeners from this event source.
-	 *
-	 * @return The configured event listeners.
-	 */
-	public EventListeners getListeners();
-
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java b/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
index d1e37b2923..14a4059695 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
@@ -1,311 +1,334 @@
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
 package org.hibernate.engine;
 
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockMode;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.entry.CacheEntry;
+import org.hibernate.event.EventType;
 import org.hibernate.event.PostLoadEvent;
 import org.hibernate.event.PostLoadEventListener;
 import org.hibernate.event.PreLoadEvent;
 import org.hibernate.event.PreLoadEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.proxy.HibernateProxy;
+import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
  * Functionality relating to Hibernate's two-phase loading process,
  * that may be reused by persisters that do not use the Loader
  * framework
  *
  * @author Gavin King
  */
 public final class TwoPhaseLoad {
 
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, TwoPhaseLoad.class.getName());
+    private static final HibernateLogger LOG = Logger.getMessageLogger(
+			HibernateLogger.class, TwoPhaseLoad.class.getName()
+	);
 
 	private TwoPhaseLoad() {}
 
 	/**
 	 * Register the "hydrated" state of an entity instance, after the first step of 2-phase loading.
 	 *
 	 * Add the "hydrated state" (an array) of an uninitialized entity to the session. We don't try
 	 * to resolve any associations yet, because there might be other entities waiting to be
 	 * read from the JDBC result set we are currently processing
 	 */
 	public static void postHydrate(
 		final EntityPersister persister,
 		final Serializable id,
 		final Object[] values,
 		final Object rowId,
 		final Object object,
 		final LockMode lockMode,
 		final boolean lazyPropertiesAreUnfetched,
 		final SessionImplementor session)
 	throws HibernateException {
 
 		Object version = Versioning.getVersion(values, persister);
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
 				lazyPropertiesAreUnfetched
 			);
 
         if (LOG.isTraceEnabled() && version != null) {
 			String versionStr = persister.isVersioned()
 					? persister.getVersionType().toLoggableString( version, session.getFactory() )
 			        : "null";
             LOG.trace("Version: " + versionStr);
 		}
 
 	}
 
 	/**
 	 * Perform the second step of 2-phase load. Fully initialize the entity
 	 * instance.
 	 *
 	 * After processing a JDBC result set, we "resolve" all the associations
 	 * between the entities which were instantiated and had their state
 	 * "hydrated" into an array
 	 */
 	public static void initializeEntity(
 			final Object entity,
 			final boolean readOnly,
 			final SessionImplementor session,
 			final PreLoadEvent preLoadEvent,
 			final PostLoadEvent postLoadEvent) throws HibernateException {
 
 		//TODO: Should this be an InitializeEntityEventListener??? (watch out for performance!)
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		EntityEntry entityEntry = persistenceContext.getEntry(entity);
 		if ( entityEntry == null ) {
 			throw new AssertionFailure( "possible non-threadsafe access to the session" );
 		}
 		EntityPersister persister = entityEntry.getPersister();
 		Serializable id = entityEntry.getId();
 		Object[] hydratedState = entityEntry.getLoadedState();
 
-        if (LOG.isDebugEnabled()) LOG.debugf("Resolving associations for %s",
-                                             MessageHelper.infoString(persister, id, session.getFactory()));
+        if (LOG.isDebugEnabled()) LOG.debugf(
+				"Resolving associations for %s",
+				MessageHelper.infoString( persister, id, session.getFactory() )
+		);
 
 		Type[] types = persister.getPropertyTypes();
 		for ( int i = 0; i < hydratedState.length; i++ ) {
 			final Object value = hydratedState[i];
 			if ( value!=LazyPropertyInitializer.UNFETCHED_PROPERTY && value!=BackrefPropertyAccessor.UNKNOWN ) {
 				hydratedState[i] = types[i].resolve( value, session, entity );
 			}
 		}
 
 		//Must occur after resolving identifiers!
 		if ( session.isEventSource() ) {
-			preLoadEvent.setEntity(entity).setState(hydratedState).setId(id).setPersister(persister);
-			PreLoadEventListener[] listeners = session.getListeners().getPreLoadEventListeners();
-			for ( int i = 0; i < listeners.length; i++ ) {
-				listeners[i].onPreLoad(preLoadEvent);
+			preLoadEvent.setEntity( entity ).setState( hydratedState ).setId( id ).setPersister( persister );
+
+			final EventListenerGroup<PreLoadEventListener> listenerGroup = session
+					.getFactory()
+					.getServiceRegistry()
+					.getService( EventListenerRegistry.class )
+					.getEventListenerGroup( EventType.PRE_LOAD );
+			for ( PreLoadEventListener listener : listenerGroup.listeners() ) {
+				listener.onPreLoad( preLoadEvent );
 			}
 		}
 
 		persister.setPropertyValues( entity, hydratedState, session.getEntityMode() );
 
 		final SessionFactoryImplementor factory = session.getFactory();
 		if ( persister.hasCache() && session.getCacheMode().isPutEnabled() ) {
 
-            if (LOG.isDebugEnabled()) LOG.debugf("Adding entity to second-level cache: %s",
-                                                 MessageHelper.infoString(persister, id, session.getFactory()));
+            if (LOG.isDebugEnabled()) LOG.debugf(
+					"Adding entity to second-level cache: %s",
+					MessageHelper.infoString( persister, id, session.getFactory() )
+			);
 
 			Object version = Versioning.getVersion(hydratedState, persister);
 			CacheEntry entry = new CacheEntry(
 					hydratedState,
 					persister,
 					entityEntry.isLoadedWithLazyPropertiesUnfetched(),
 					version,
 					session,
 					entity
 			);
 			CacheKey cacheKey = session.generateCacheKey( id, persister.getIdentifierType(), persister.getRootEntityName() );
 
 			// explicit handling of caching for rows just inserted and then somehow forced to be read
 			// from the database *within the same transaction*.  usually this is done by
 			// 		1) Session#refresh, or
 			// 		2) Session#clear + some form of load
 			//
 			// we need to be careful not to clobber the lock here in the cache so that it can be rolled back if need be
 			if ( session.getPersistenceContext().wasInsertedDuringTransaction( persister, id ) ) {
 				persister.getCacheAccessStrategy().update(
 						cacheKey,
 						persister.getCacheEntryStructure().structure( entry ),
 						version,
 						version
 				);
 			}
 			else {
 				boolean put = persister.getCacheAccessStrategy().putFromLoad(
 						cacheKey,
 						persister.getCacheEntryStructure().structure( entry ),
 						session.getTimestamp(),
 						version,
 						useMinimalPuts( session, entityEntry )
 				);
 
 				if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 					factory.getStatisticsImplementor().secondLevelCachePut( persister.getCacheAccessStrategy().getRegion().getName() );
 				}
 			}
 		}
 
 		boolean isReallyReadOnly = readOnly;
 		if ( !persister.isMutable() ) {
 			isReallyReadOnly = true;
 		}
 		else {
 			Object proxy = persistenceContext.getProxy( entityEntry.getEntityKey() );
 			if ( proxy != null ) {
 				// there is already a proxy for this impl
 				// only set the status to read-only if the proxy is read-only
 				isReallyReadOnly = ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().isReadOnly();
 			}
 		}
 		if ( isReallyReadOnly ) {
 			//no need to take a snapshot - this is a
 			//performance optimization, but not really
 			//important, except for entities with huge
 			//mutable property values
 			persistenceContext.setEntryStatus(entityEntry, Status.READ_ONLY);
 		}
 		else {
 			//take a snapshot
 			TypeHelper.deepCopy(
 					hydratedState,
 					persister.getPropertyTypes(),
 					persister.getPropertyUpdateability(),
 					hydratedState,  //after setting values to object, entityMode
 					session
 			);
 			persistenceContext.setEntryStatus(entityEntry, Status.MANAGED);
 		}
 
 		persister.afterInitialize(
 				entity,
 				entityEntry.isLoadedWithLazyPropertiesUnfetched(),
 				session
 			);
 
 		if ( session.isEventSource() ) {
-			postLoadEvent.setEntity(entity).setId(id).setPersister(persister);
-			PostLoadEventListener[] listeners = session.getListeners().getPostLoadEventListeners();
-			for ( int i = 0; i < listeners.length; i++ ) {
-				listeners[i].onPostLoad(postLoadEvent);
+			postLoadEvent.setEntity( entity ).setId( id ).setPersister( persister );
+
+			final EventListenerGroup<PostLoadEventListener> listenerGroup = session
+					.getFactory()
+					.getServiceRegistry()
+					.getService( EventListenerRegistry.class )
+					.getEventListenerGroup( EventType.POST_LOAD );
+			for ( PostLoadEventListener listener : listenerGroup.listeners() ) {
+				listener.onPostLoad( postLoadEvent );
 			}
 		}
 
-        if (LOG.isDebugEnabled()) LOG.debugf("Done materializing entity %s",
-                                             MessageHelper.infoString(persister, id, session.getFactory()));
+        if ( LOG.isDebugEnabled() ) {
+			LOG.debugf(
+					"Done materializing entity %s",
+					MessageHelper.infoString( persister, id, session.getFactory() )
+			);
+		}
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().loadEntity( persister.getEntityName() );
 		}
 
 	}
 
 	private static boolean useMinimalPuts(SessionImplementor session, EntityEntry entityEntry) {
 		return ( session.getFactory().getSettings().isMinimalPutsEnabled() &&
 						session.getCacheMode()!=CacheMode.REFRESH ) ||
 				( entityEntry.getPersister().hasLazyProperties() &&
 						entityEntry.isLoadedWithLazyPropertiesUnfetched() &&
 						entityEntry.getPersister().isLazyPropertiesCacheable() );
 	}
 
 	/**
 	 * Add an uninitialized instance of an entity class, as a placeholder to ensure object
 	 * identity. Must be called before <tt>postHydrate()</tt>.
 	 *
 	 * Create a "temporary" entry for a newly instantiated entity. The entity is uninitialized,
 	 * but we need the mapping from id to instance in order to guarantee uniqueness.
 	 */
 	public static void addUninitializedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnfetched,
 			final SessionImplementor session
 	) {
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
 				lazyPropertiesAreUnfetched
 			);
 	}
 
 	public static void addUninitializedCachedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnfetched,
 			final Object version,
 			final SessionImplementor session
 	) {
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
 				lazyPropertiesAreUnfetched
 			);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/EventListenerRegistration.java b/hibernate-core/src/main/java/org/hibernate/event/EventListenerRegistration.java
new file mode 100644
index 0000000000..bcbc40afa7
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/event/EventListenerRegistration.java
@@ -0,0 +1,39 @@
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
+package org.hibernate.event;
+
+import java.util.Map;
+
+import org.hibernate.cfg.Mappings;
+import org.hibernate.service.spi.ServiceRegistry;
+
+/**
+ * Contract for performing event listener registration.  This is completely a work in progress for now.  The
+ * expectation is that this gets tied in with the "service locator" pattern defined by HHH-5562
+ *
+ * @author Steve Ebersole
+ */
+public interface EventListenerRegistration {
+	public void apply(ServiceRegistry serviceRegistry, Mappings mappings, Map<?,?> configValues);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/event/EventType.java b/hibernate-core/src/main/java/org/hibernate/event/EventType.java
new file mode 100644
index 0000000000..f399dc8a1d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/event/EventType.java
@@ -0,0 +1,195 @@
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
+package org.hibernate.event;
+
+import java.lang.reflect.Field;
+import java.security.AccessController;
+import java.security.PrivilegedAction;
+import java.util.Collection;
+import java.util.HashMap;
+import java.util.Map;
+
+import org.hibernate.HibernateException;
+
+/**
+ * Enumeration of the recognized types of events, including meta-information about each.
+ *
+ * @author Steve Ebersole
+ */
+public class EventType<T> {
+	public static final EventType<LoadEventListener> LOAD
+			= new EventType<LoadEventListener>( "load", LoadEventListener.class );
+	public static final EventType<InitializeCollectionEventListener> INIT_COLLECTION
+			= new EventType<InitializeCollectionEventListener>( "load-collection", InitializeCollectionEventListener.class );
+
+	public static final EventType<SaveOrUpdateEventListener> SAVE_UPDATE
+			= new EventType<SaveOrUpdateEventListener>( "save-update", SaveOrUpdateEventListener.class );
+	public static final EventType<SaveOrUpdateEventListener> UPDATE
+			= new EventType<SaveOrUpdateEventListener>( "update", SaveOrUpdateEventListener.class );
+	public static final EventType<SaveOrUpdateEventListener> SAVE
+			= new EventType<SaveOrUpdateEventListener>( "save", SaveOrUpdateEventListener.class );
+	public static final EventType<PersistEventListener> PERSIST
+			= new EventType<PersistEventListener>( "create", PersistEventListener.class );
+	public static final EventType<PersistEventListener> PERSIST_ONFLUSH
+			= new EventType<PersistEventListener>( "create-onflush", PersistEventListener.class );
+
+	public static final EventType<MergeEventListener> MERGE
+			= new EventType<MergeEventListener>( "merge", MergeEventListener.class );
+
+	public static final EventType<DeleteEventListener> DELETE
+			= new EventType<DeleteEventListener>( "delete", DeleteEventListener.class );
+
+	public static final EventType<ReplicateEventListener> REPLICATE
+			= new EventType<ReplicateEventListener>( "replicate", ReplicateEventListener.class );
+
+	public static final EventType<FlushEventListener> FLUSH
+			= new EventType<FlushEventListener>( "flush", FlushEventListener.class );
+	public static final EventType<AutoFlushEventListener> AUTO_FLUSH
+			= new EventType<AutoFlushEventListener>( "auto-flush", AutoFlushEventListener.class );
+	public static final EventType<DirtyCheckEventListener> DIRTY_CHECK
+			= new EventType<DirtyCheckEventListener>( "dirty-check", DirtyCheckEventListener.class );
+	public static final EventType<FlushEntityEventListener> FLUSH_ENTITY
+			= new EventType<FlushEntityEventListener>( "flush-entity", FlushEntityEventListener.class );
+
+	public static final EventType<EvictEventListener> EVICT
+			= new EventType<EvictEventListener>( "evict", EvictEventListener.class );
+
+	public static final EventType<LockEventListener> LOCK
+			= new EventType<LockEventListener>( "lock", LockEventListener.class );
+
+	public static final EventType<RefreshEventListener> REFRESH
+			= new EventType<RefreshEventListener>( "refresh", RefreshEventListener.class );
+
+	public static final EventType<PreLoadEventListener> PRE_LOAD
+			= new EventType<PreLoadEventListener>( "pre-load", PreLoadEventListener.class );
+	public static final EventType<PreDeleteEventListener> PRE_DELETE
+			= new EventType<PreDeleteEventListener>( "pre-delete", PreDeleteEventListener.class );
+	public static final EventType<PreUpdateEventListener> PRE_UPDATE
+			= new EventType<PreUpdateEventListener>( "pre-update", PreUpdateEventListener.class );
+	public static final EventType<PreInsertEventListener> PRE_INSERT
+			= new EventType<PreInsertEventListener>( "pre-insert", PreInsertEventListener.class );
+
+	public static final EventType<PostLoadEventListener> POST_LOAD
+			= new EventType<PostLoadEventListener>( "post-load", PostLoadEventListener.class );
+	public static final EventType<PostDeleteEventListener> POST_DELETE
+			= new EventType<PostDeleteEventListener>( "post-delete", PostDeleteEventListener.class );
+	public static final EventType<PostUpdateEventListener> POST_UPDATE
+			= new EventType<PostUpdateEventListener>( "post-update", PostUpdateEventListener.class );
+	public static final EventType<PostInsertEventListener> POST_INSERT
+			= new EventType<PostInsertEventListener>( "post-insert", PostInsertEventListener.class );
+
+	public static final EventType<PostDeleteEventListener> POST_COMMIT_DELETE
+			= new EventType<PostDeleteEventListener>( "post-commit-delete", PostDeleteEventListener.class );
+	public static final EventType<PostUpdateEventListener> POST_COMMIT_UPDATE
+			= new EventType<PostUpdateEventListener>( "post-commit-update", PostUpdateEventListener.class );
+	public static final EventType<PostInsertEventListener> POST_COMMIT_INSERT
+			= new EventType<PostInsertEventListener>( "post-commit-insert", PostInsertEventListener.class );
+
+	public static final EventType<PreCollectionRecreateEventListener> PRE_COLLECTION_RECREATE
+			= new EventType<PreCollectionRecreateEventListener>( "pre-collection-recreate", PreCollectionRecreateEventListener.class );
+	public static final EventType<PreCollectionRemoveEventListener> PRE_COLLECTION_REMOVE
+			= new EventType<PreCollectionRemoveEventListener>( "pre-collection-remove", PreCollectionRemoveEventListener.class );
+	public static final EventType<PreCollectionUpdateEventListener> PRE_COLLECTION_UPDATE
+			= new EventType<PreCollectionUpdateEventListener>( "pre-collection-update", PreCollectionUpdateEventListener.class );
+
+	public static final EventType<PostCollectionRecreateEventListener> POST_COLLECTION_RECREATE
+			= new EventType<PostCollectionRecreateEventListener>( "post-collection-recreate", PostCollectionRecreateEventListener.class );
+	public static final EventType<PostCollectionRemoveEventListener> POST_COLLECTION_REMOVE
+			= new EventType<PostCollectionRemoveEventListener>( "post-collection-remove", PostCollectionRemoveEventListener.class );
+	public static final EventType<PostCollectionUpdateEventListener> POST_COLLECTION_UPDATE
+			= new EventType<PostCollectionUpdateEventListener>( "post-collection-update", PostCollectionUpdateEventListener.class );
+
+
+	/**
+	 * Maintain a map of {@link EventType} instances keyed by name for lookup by name as well as {@link #values()}
+	 * resolution.
+	 */
+	public static final Map<String,EventType> eventTypeByNameMap = AccessController.doPrivileged(
+			new PrivilegedAction<Map<String, EventType>>() {
+				@Override
+				public Map<String, EventType> run() {
+					final Map<String, EventType> typeByNameMap = new HashMap<String, EventType>();
+					final Field[] fields = EventType.class.getDeclaredFields();
+					for ( int i = 0, max = fields.length; i < max; i++ ) {
+						if ( EventType.class.isAssignableFrom( fields[i].getType() ) ) {
+							try {
+								final EventType typeField = ( EventType ) fields[i].get( null );
+								typeByNameMap.put( typeField.eventName(), typeField );
+							}
+							catch( Exception t ) {
+								throw new HibernateException( "Unable to initialize EventType map", t );
+							}
+						}
+					}
+					return typeByNameMap;
+				}
+			}
+	);
+
+	/**
+	 * Find an {@link EventType} by its name
+	 *
+	 * @param eventName The name
+	 *
+	 * @return The {@link EventType} instance.
+	 *
+	 * @throws HibernateException If eventName is null, or if eventName does not correlate to any known event type.
+	 */
+	public static EventType resolveEventTypeByName(final String eventName) {
+		if ( eventName == null ) {
+			throw new HibernateException( "event name to resolve cannot be null" );
+		}
+		final EventType eventType = eventTypeByNameMap.get( eventName );
+		if ( eventType == null ) {
+			throw new HibernateException( "Unable to locate proper event type for event name [" + eventName + "]" );
+		}
+		return eventType;
+	}
+
+	/**
+	 * Get a collection of all {@link EventType} instances.
+	 *
+	 * @return All {@link EventType} instances
+	 */
+	public static Collection<EventType> values() {
+		return eventTypeByNameMap.values();
+	}
+
+
+	private final String eventName;
+	private final Class<? extends T> baseListenerInterface;
+
+	private EventType(String eventName, Class<? extends T> baseListenerInterface) {
+		this.eventName = eventName;
+		this.baseListenerInterface = baseListenerInterface;
+	}
+
+	public String eventName() {
+		return eventName;
+	}
+
+	public Class baseListenerInterface() {
+		return baseListenerInterface;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
index e091af4ebe..291705cfe9 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
@@ -1,372 +1,387 @@
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
 package org.hibernate.event.def;
 
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.action.internal.CollectionRecreateAction;
 import org.hibernate.action.internal.CollectionRemoveAction;
 import org.hibernate.action.internal.CollectionUpdateAction;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.ActionQueue;
 import org.hibernate.engine.Cascade;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.CollectionEntry;
 import org.hibernate.engine.CollectionKey;
 import org.hibernate.engine.Collections;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
 import org.hibernate.event.FlushEntityEvent;
 import org.hibernate.event.FlushEntityEventListener;
 import org.hibernate.event.FlushEvent;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.internal.util.collections.LazyIterator;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.Printer;
-import org.jboss.logging.Logger;
+import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.service.event.spi.EventListenerRegistry;
 
 /**
  * A convenience base class for listeners whose functionality results in flushing.
  *
  * @author Steve Eberole
  */
 public abstract class AbstractFlushingEventListener implements Serializable {
 
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
-                                                                       AbstractFlushingEventListener.class.getName());
+    private static final HibernateLogger LOG = Logger.getMessageLogger(
+			HibernateLogger.class,
+			AbstractFlushingEventListener.class.getName()
+	);
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Pre-flushing section
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Coordinates the processing necessary to get things ready for executions
 	 * as db calls by preping the session caches and moving the appropriate
 	 * entities and collections to their respective execution queues.
 	 *
 	 * @param event The flush event.
 	 * @throws HibernateException Error flushing caches to execution queues.
 	 */
 	protected void flushEverythingToExecutions(FlushEvent event) throws HibernateException {
 
         LOG.trace("Flushing session");
 
 		EventSource session = event.getSession();
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		session.getInterceptor().preFlush( new LazyIterator( persistenceContext.getEntitiesByKey() ) );
 
 		prepareEntityFlushes(session);
 		// we could move this inside if we wanted to
 		// tolerate collection initializations during
 		// collection dirty checking:
 		prepareCollectionFlushes(session);
 		// now, any collections that are initialized
 		// inside this block do not get updated - they
 		// are ignored until the next flush
 
 		persistenceContext.setFlushing(true);
 		try {
 			flushEntities(event);
 			flushCollections(session);
 		}
 		finally {
 			persistenceContext.setFlushing(false);
 		}
 
 		//some statistics
         if (LOG.isDebugEnabled()) {
-            LOG.debugf("Flushed: %s insertions, %s updates, %s deletions to %s objects",
-                       session.getActionQueue().numberOfInsertions(),
-                       session.getActionQueue().numberOfUpdates(),
-                       session.getActionQueue().numberOfDeletions(),
-                       persistenceContext.getEntityEntries().size());
-            LOG.debugf("Flushed: %s (re)creations, %s updates, %s removals to %s collections",
-                       session.getActionQueue().numberOfCollectionCreations(),
-                       session.getActionQueue().numberOfCollectionUpdates(),
-                       session.getActionQueue().numberOfCollectionRemovals(),
-                       persistenceContext.getCollectionEntries().size());
+            LOG.debugf(
+					"Flushed: %s insertions, %s updates, %s deletions to %s objects",
+					session.getActionQueue().numberOfInsertions(),
+					session.getActionQueue().numberOfUpdates(),
+					session.getActionQueue().numberOfDeletions(),
+					persistenceContext.getEntityEntries().size()
+			);
+            LOG.debugf(
+					"Flushed: %s (re)creations, %s updates, %s removals to %s collections",
+					session.getActionQueue().numberOfCollectionCreations(),
+					session.getActionQueue().numberOfCollectionUpdates(),
+					session.getActionQueue().numberOfCollectionRemovals(),
+					persistenceContext.getCollectionEntries().size()
+			);
 			new Printer( session.getFactory() ).toString(
 					persistenceContext.getEntitiesByKey().values().iterator(),
 					session.getEntityMode()
 				);
 		}
 	}
 
 	/**
 	 * process cascade save/update at the start of a flush to discover
 	 * any newly referenced entity that must be passed to saveOrUpdate(),
 	 * and also apply orphan delete
 	 */
 	private void prepareEntityFlushes(EventSource session) throws HibernateException {
 
-        LOG.debugf("Processing flush-time cascades");
+        LOG.debugf( "Processing flush-time cascades" );
 
 		final Map.Entry[] list = IdentityMap.concurrentEntries( session.getPersistenceContext().getEntityEntries() );
 		//safe from concurrent modification because of how entryList() is implemented on IdentityMap
 		final int size = list.length;
 		final Object anything = getAnything();
 		for ( int i=0; i<size; i++ ) {
 			Map.Entry me = list[i];
 			EntityEntry entry = (EntityEntry) me.getValue();
 			Status status = entry.getStatus();
 			if ( status == Status.MANAGED || status == Status.SAVING || status == Status.READ_ONLY ) {
 				cascadeOnFlush( session, entry.getPersister(), me.getKey(), anything );
 			}
 		}
 	}
 
 	private void cascadeOnFlush(EventSource session, EntityPersister persister, Object object, Object anything)
 	throws HibernateException {
 		session.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadingAction(), Cascade.BEFORE_FLUSH, session )
 			.cascade( persister, object, anything );
 		}
 		finally {
 			session.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	protected Object getAnything() { return null; }
 
 	protected CascadingAction getCascadingAction() {
 		return CascadingAction.SAVE_UPDATE;
 	}
 
 	/**
 	 * Initialize the flags of the CollectionEntry, including the
 	 * dirty check.
 	 */
 	private void prepareCollectionFlushes(SessionImplementor session) throws HibernateException {
 
 		// Initialize dirty flags for arrays + collections with composite elements
 		// and reset reached, doupdate, etc.
 
-        LOG.debugf("Dirty checking collections");
+        LOG.debugf( "Dirty checking collections" );
 
 		final List list = IdentityMap.entries( session.getPersistenceContext().getCollectionEntries() );
 		final int size = list.size();
 		for ( int i = 0; i < size; i++ ) {
 			Map.Entry e = ( Map.Entry ) list.get( i );
 			( (CollectionEntry) e.getValue() ).preFlush( (PersistentCollection) e.getKey() );
 		}
 	}
 
 	/**
 	 * 1. detect any dirty entities
 	 * 2. schedule any entity updates
 	 * 3. search out any reachable collections
 	 */
 	private void flushEntities(FlushEvent event) throws HibernateException {
 
         LOG.trace("Flushing entities and processing referenced collections");
 
 		// Among other things, updateReachables() will recursively load all
 		// collections that are moving roles. This might cause entities to
 		// be loaded.
 
 		// So this needs to be safe from concurrent modification problems.
 		// It is safe because of how IdentityMap implements entrySet()
 
 		final EventSource source = event.getSession();
 
 		final Map.Entry[] list = IdentityMap.concurrentEntries( source.getPersistenceContext().getEntityEntries() );
 		final int size = list.length;
 		for ( int i = 0; i < size; i++ ) {
 
 			// Update the status of the object and if necessary, schedule an update
 
 			Map.Entry me = list[i];
 			EntityEntry entry = (EntityEntry) me.getValue();
 			Status status = entry.getStatus();
 
 			if ( status != Status.LOADING && status != Status.GONE ) {
-				FlushEntityEvent entityEvent = new FlushEntityEvent( source, me.getKey(), entry );
-				FlushEntityEventListener[] listeners = source.getListeners().getFlushEntityEventListeners();
-				for ( int j = 0; j < listeners.length; j++ ) {
-					listeners[j].onFlushEntity(entityEvent);
+				final FlushEntityEvent entityEvent = new FlushEntityEvent( source, me.getKey(), entry );
+				final EventListenerGroup<FlushEntityEventListener> listenerGroup = source
+						.getFactory()
+						.getServiceRegistry()
+						.getService( EventListenerRegistry.class )
+						.getEventListenerGroup( EventType.FLUSH_ENTITY );
+				for ( FlushEntityEventListener listener : listenerGroup.listeners() ) {
+					listener.onFlushEntity( entityEvent );
 				}
 			}
 		}
 
 		source.getActionQueue().sortActions();
 	}
 
 	/**
 	 * process any unreferenced collections and then inspect all known collections,
 	 * scheduling creates/removes/updates
 	 */
 	private void flushCollections(EventSource session) throws HibernateException {
 
         LOG.trace("Processing unreferenced collections");
 
 		List list = IdentityMap.entries( session.getPersistenceContext().getCollectionEntries() );
 		int size = list.size();
 		for ( int i = 0; i < size; i++ ) {
 			Map.Entry me = ( Map.Entry ) list.get( i );
 			CollectionEntry ce = (CollectionEntry) me.getValue();
 			if ( !ce.isReached() && !ce.isIgnore() ) {
 				Collections.processUnreachableCollection( (PersistentCollection) me.getKey(), session );
 			}
 		}
 
 		// Schedule updates to collections:
 
         LOG.trace("Scheduling collection removes/(re)creates/updates");
 
 		list = IdentityMap.entries( session.getPersistenceContext().getCollectionEntries() );
 		size = list.size();
 		ActionQueue actionQueue = session.getActionQueue();
 		for ( int i = 0; i < size; i++ ) {
 			Map.Entry me = (Map.Entry) list.get(i);
 			PersistentCollection coll = (PersistentCollection) me.getKey();
 			CollectionEntry ce = (CollectionEntry) me.getValue();
 
 			if ( ce.isDorecreate() ) {
 				session.getInterceptor().onCollectionRecreate( coll, ce.getCurrentKey() );
 				actionQueue.addAction(
 						new CollectionRecreateAction(
 								coll,
 								ce.getCurrentPersister(),
 								ce.getCurrentKey(),
 								session
 							)
 					);
 			}
 			if ( ce.isDoremove() ) {
 				session.getInterceptor().onCollectionRemove( coll, ce.getLoadedKey() );
 				actionQueue.addAction(
 						new CollectionRemoveAction(
 								coll,
 								ce.getLoadedPersister(),
 								ce.getLoadedKey(),
 								ce.isSnapshotEmpty(coll),
 								session
 							)
 					);
 			}
 			if ( ce.isDoupdate() ) {
 				session.getInterceptor().onCollectionUpdate( coll, ce.getLoadedKey() );
 				actionQueue.addAction(
 						new CollectionUpdateAction(
 								coll,
 								ce.getLoadedPersister(),
 								ce.getLoadedKey(),
 								ce.isSnapshotEmpty(coll),
 								session
 							)
 					);
 			}
 
 		}
 
 		actionQueue.sortCollectionActions();
 
 	}
 
 	/**
 	 * Execute all SQL and second-level cache updates, in a
 	 * special order so that foreign-key constraints cannot
 	 * be violated:
 	 * <ol>
 	 * <li> Inserts, in the order they were performed
 	 * <li> Updates
 	 * <li> Deletion of collection elements
 	 * <li> Insertion of collection elements
 	 * <li> Deletes, in the order they were performed
 	 * </ol>
 	 */
 	protected void performExecutions(EventSource session) throws HibernateException {
 
         LOG.trace("Executing flush");
 
 		try {
 			session.getTransactionCoordinator().getJdbcCoordinator().flushBeginning();
 			// we need to lock the collection caches before
 			// executing entity inserts/updates in order to
 			// account for bidi associations
 			session.getActionQueue().prepareActions();
 			session.getActionQueue().executeActions();
 		}
 		finally {
 			session.getTransactionCoordinator().getJdbcCoordinator().flushEnding();
 		}
 	}
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// Post-flushing section
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * 1. Recreate the collection key -> collection map
 	 * 2. rebuild the collection entries
 	 * 3. call Interceptor.postFlush()
 	 */
 	protected void postFlush(SessionImplementor session) throws HibernateException {
 
         LOG.trace("Post flush");
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		persistenceContext.getCollectionsByKey().clear();
 		persistenceContext.getBatchFetchQueue()
 				.clearSubselects(); //the database has changed now, so the subselect results need to be invalidated
 
 		Iterator iter = persistenceContext.getCollectionEntries().entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			CollectionEntry collectionEntry = (CollectionEntry) me.getValue();
 			PersistentCollection persistentCollection = (PersistentCollection) me.getKey();
 			collectionEntry.postFlush(persistentCollection);
 			if ( collectionEntry.getLoadedPersister() == null ) {
 				//if the collection is dereferenced, remove from the session cache
 				//iter.remove(); //does not work, since the entrySet is not backed by the set
 				persistenceContext.getCollectionEntries()
 						.remove(persistentCollection);
 			}
 			else {
 				//otherwise recreate the mapping between the collection and its key
 				CollectionKey collectionKey = new CollectionKey(
 						collectionEntry.getLoadedPersister(),
 						collectionEntry.getLoadedKey(),
 						session.getEntityMode()
 					);
 				persistenceContext.getCollectionsByKey()
 						.put(collectionKey, persistentCollection);
 			}
 		}
 
 		session.getInterceptor().postFlush( new LazyIterator( persistenceContext.getEntitiesByKey() ) );
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
index 9f78b85ce3..9aedb3c283 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
@@ -1,655 +1,668 @@
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
 package org.hibernate.event.def;
 
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockMode;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.access.SoftLock;
 import org.hibernate.cache.entry.CacheEntry;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.engine.TwoPhaseLoad;
 import org.hibernate.engine.Versioning;
 import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
 import org.hibernate.event.LoadEvent;
 import org.hibernate.event.LoadEventListener;
 import org.hibernate.event.PostLoadEvent;
 import org.hibernate.event.PostLoadEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
+import org.hibernate.service.event.spi.EventListenerRegistry;
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
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        DefaultLoadEventListener.class.getName());
 
 
 	/**
 	 * Handle the given load event.
 	 *
 	 * @param event The load event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onLoad(LoadEvent event, LoadEventListener.LoadType loadType) throws HibernateException {
 
 		final SessionImplementor source = event.getSession();
 
 		EntityPersister persister;
 		if ( event.getInstanceToLoad() != null ) {
 			persister = source.getEntityPersister( null, event.getInstanceToLoad() ); //the load() which takes an entity does not pass an entityName
 			event.setEntityClassName( event.getInstanceToLoad().getClass().getName() );
 		}
 		else {
 			persister = source.getFactory().getEntityPersister( event.getEntityClassName() );
 		}
 
 		if ( persister == null ) {
 			throw new HibernateException(
 					"Unable to locate persister: " +
 					event.getEntityClassName()
 				);
 		}
 
 		final Class idClass = persister.getIdentifierType().getReturnedClass();
 		if ( persister.getIdentifierType().isComponentType() && EntityMode.DOM4J == event.getSession().getEntityMode() ) {
 			// skip this check for composite-ids relating to dom4j entity-mode;
 			// alternatively, we could add a check to make sure the incoming id value is
 			// an instance of Element...
 		}
 		else {
 			if ( idClass != null && ! idClass.isInstance( event.getEntityId() ) ) {
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
 						"Provided id of the wrong type for class " + persister.getEntityName() + ". Expected: " + idClass + ", got " + event.getEntityId().getClass()
 				);
 			}
 		}
 
 		final  EntityKey keyToLoad = source.generateEntityKey( event.getEntityId(), persister );
 
 		try {
 			if ( loadType.isNakedEntityReturned() ) {
 				//do not return a proxy!
 				//(this option indicates we are initializing a proxy)
 				event.setResult( load(event, persister, keyToLoad, loadType) );
 			}
 			else {
 				//return a proxy if appropriate
 				if ( event.getLockMode() == LockMode.NONE ) {
 					event.setResult( proxyOrLoad(event, persister, keyToLoad, loadType) );
 				}
 				else {
 					event.setResult( lockAndLoad(event, persister, keyToLoad, loadType, source) );
 				}
 			}
 		}
 		catch(HibernateException e) {
             LOG.unableToLoadCommand(e);
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
 		dependentIdType.setPropertyValues( dependent, new Object[] {parent}, event.getSession().getEntityMode() );
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
 	 * @return The loaded entity.
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
 						MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() )
 					);
 			}
 			persister.setIdentifier( event.getInstanceToLoad(), event.getEntityId(), event.getSession() );
 		}
 
 		Object entity = doLoad(event, persister, keyToLoad, options);
 
 		boolean isOptionalInstance = event.getInstanceToLoad() != null;
 
 		if ( !options.isAllowNulls() || isOptionalInstance ) {
 			if ( entity == null ) {
 				event.getSession().getFactory().getEntityNotFoundDelegate().handleEntityNotFound( event.getEntityClassName(), event.getEntityId() );
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
 	 * @return The result of the proxy/load operation.
 	 */
 	protected Object proxyOrLoad(
 		final LoadEvent event,
 		final EntityPersister persister,
 		final EntityKey keyToLoad,
 		final LoadEventListener.LoadType options) {
 
         if (LOG.isTraceEnabled()) LOG.trace("Loading entity: "
                                             + MessageHelper.infoString(persister,
                                                                              event.getEntityId(),
                                                                              event.getSession().getFactory()));
 
         // this class has no proxies (so do a shortcut)
         if (!persister.hasProxy()) return load(event, persister, keyToLoad, options);
         final PersistenceContext persistenceContext = event.getSession().getPersistenceContext();
 
 		// look for a proxy
         Object proxy = persistenceContext.getProxy(keyToLoad);
         if (proxy != null) return returnNarrowedProxy(event, persister, keyToLoad, options, persistenceContext, proxy);
         if (options.isAllowProxyCreation()) return createProxyIfNecessary(event, persister, keyToLoad, options, persistenceContext);
         // return a newly loaded object
         return load(event, persister, keyToLoad, options);
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
 	 * @return The created/existing proxy
 	 */
 	private Object returnNarrowedProxy(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options,
 			final PersistenceContext persistenceContext,
 			final Object proxy) {
         LOG.trace("Entity proxy found in session cache");
 		LazyInitializer li = ( (HibernateProxy) proxy ).getHibernateLazyInitializer();
 		if ( li.isUnwrap() ) {
 			return li.getImplementation();
 		}
 		Object impl = null;
 		if ( !options.isAllowProxyCreation() ) {
 			impl = load( event, persister, keyToLoad, options );
 			if ( impl == null ) {
 				event.getSession().getFactory().getEntityNotFoundDelegate().handleEntityNotFound( persister.getEntityName(), keyToLoad.getIdentifier());
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
             LOG.trace("Entity found in session cache");
 			if ( options.isCheckDeleted() ) {
 				EntityEntry entry = persistenceContext.getEntry( existing );
 				Status status = entry.getStatus();
 				if ( status == Status.DELETED || status == Status.GONE ) {
 					return null;
 				}
 			}
 			return existing;
 		}
         LOG.trace("Creating new proxy for entity");
         // return new uninitialized proxy
         Object proxy = persister.createProxy(event.getEntityId(), event.getSession());
         persistenceContext.getBatchFetchQueue().addBatchLoadableEntityKey(keyToLoad);
         persistenceContext.addProxy(keyToLoad, proxy);
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
 	 * @return The loaded entity
 	 * @throws HibernateException
 	 */
 	protected Object lockAndLoad(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options,
 			final SessionImplementor source) {
 		SoftLock lock = null;
 		final CacheKey ck;
 		if ( persister.hasCache() ) {
 			ck = source.generateCacheKey(
 					event.getEntityId(),
 					persister.getIdentifierType(),
 					persister.getRootEntityName()
 			);
 			lock = persister.getCacheAccessStrategy().lockItem( ck, null );
 		}
 		else {
 			ck = null;
 		}
 
 		Object entity;
 		try {
 			entity = load(event, persister, keyToLoad, options);
 		}
 		finally {
 			if ( persister.hasCache() ) {
 				persister.getCacheAccessStrategy().unlockItem( ck, lock );
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
 	 * @return The loaded entity, or null.
 	 */
 	protected Object doLoad(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final EntityKey keyToLoad,
 			final LoadEventListener.LoadType options) {
 
         if (LOG.isTraceEnabled()) LOG.trace("Attempting to resolve: "
                                             + MessageHelper.infoString(persister,
                                                                        event.getEntityId(),
                                                                        event.getSession().getFactory()));
 
 		Object entity = loadFromSessionCache( event, keyToLoad, options );
 		if ( entity == REMOVED_ENTITY_MARKER ) {
             LOG.debugf("Load request found matching entity in context, but it is scheduled for removal; returning null");
 			return null;
 		}
 		if ( entity == INCONSISTENT_RTN_CLASS_MARKER ) {
             LOG.debugf("Load request found matching entity in context, but the matched entity was of an inconsistent return type; returning null");
 			return null;
 		}
 		if ( entity != null ) {
             if (LOG.isTraceEnabled()) LOG.trace("Resolved object in session cache: "
                                                 + MessageHelper.infoString(persister,
                                                                            event.getEntityId(),
                                                                            event.getSession().getFactory()));
 			return entity;
 		}
 
 		entity = loadFromSecondLevelCache(event, persister, options);
 		if ( entity != null ) {
             if (LOG.isTraceEnabled()) LOG.trace("Resolved object in second-level cache: "
                                                 + MessageHelper.infoString(persister,
                                                                            event.getEntityId(),
                                                                            event.getSession().getFactory()));
 			return entity;
 		}
 
         if (LOG.isTraceEnabled()) LOG.trace("Object not resolved in any cache: "
                                             + MessageHelper.infoString(persister,
                                                                        event.getEntityId(),
                                                                        event.getSession().getFactory()));
 
 		return loadFromDatasource(event, persister, keyToLoad, options);
 	}
 
 	/**
 	 * Performs the process of loading an entity from the configured
 	 * underlying datasource.
 	 *
 	 * @param event The load event
 	 * @param persister The persister for the entity being requested for load
 	 * @param keyToLoad The EntityKey representing the entity to be loaded.
 	 * @param options The load options.
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
 	 * @return The entity from the session-level cache, or null.
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
 //				EntityPersister persister = event.getSession().getFactory().getEntityPersister( event.getEntityClassName() );
 				EntityPersister persister = event.getSession().getFactory().getEntityPersister( keyToLoad.getEntityName() );
 				if ( ! persister.isInstance( old, event.getSession().getEntityMode() ) ) {
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
 	 * @return The entity from the second-level cache, or null.
 	 */
 	protected Object loadFromSecondLevelCache(
 			final LoadEvent event,
 			final EntityPersister persister,
 			final LoadEventListener.LoadType options) {
 
 		final SessionImplementor source = event.getSession();
 
 		final boolean useCache = persister.hasCache()
 				&& source.getCacheMode().isGetEnabled()
 				&& event.getLockMode().lessThan(LockMode.READ);
 
 		if ( useCache ) {
 
 			final SessionFactoryImplementor factory = source.getFactory();
 
 			final CacheKey ck = source.generateCacheKey(
 					event.getEntityId(),
 					persister.getIdentifierType(),
 					persister.getRootEntityName()
 			);
 			Object ce = persister.getCacheAccessStrategy().get( ck, source.getTimestamp() );
 			if ( factory.getStatistics().isStatisticsEnabled() ) {
 				if ( ce == null ) {
 					factory.getStatisticsImplementor().secondLevelCacheMiss(
 							persister.getCacheAccessStrategy().getRegion().getName()
 					);
 				}
 				else {
 					factory.getStatisticsImplementor().secondLevelCacheHit(
 							persister.getCacheAccessStrategy().getRegion().getName()
 					);
 				}
 			}
 
 			if ( ce != null ) {
 				CacheEntry entry = (CacheEntry) persister.getCacheEntryStructure().destructure( ce, factory );
 
 				// Entity was found in second-level cache...
 				return assembleCacheEntry(
 						entry,
 						event.getEntityId(),
 						persister,
 						event
 				);
 			}
 		}
 
 		return null;
 	}
 
 	private Object assembleCacheEntry(
 			final CacheEntry entry,
 			final Serializable id,
 			final EntityPersister persister,
 			final LoadEvent event) throws HibernateException {
 
 		final Object optionalObject = event.getInstanceToLoad();
 		final EventSource session = event.getSession();
 		final SessionFactoryImplementor factory = session.getFactory();
 
         if (LOG.isTraceEnabled()) LOG.trace("Assembling entity from second-level cache: "
                                             + MessageHelper.infoString(persister, id, factory));
 
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
 		Object[] values = entry.assemble( result, id, subclassPersister, session.getInterceptor(), session ); // intializes result by side-effect
 		TypeHelper.deepCopy(
 				values,
 				types,
 				subclassPersister.getPropertyUpdateability(),
 				values,
 				session
 		);
 
 		Object version = Versioning.getVersion( values, subclassPersister );
         if (LOG.isTraceEnabled()) LOG.trace("Cached Version: " + version);
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		boolean isReadOnly = session.isDefaultReadOnly();
 		if ( persister.isMutable() ) {
 			Object proxy = persistenceContext.getProxy( entityKey );
 			if ( proxy != null ) {
 				// there is already a proxy for this impl
 				// only set the status to read-only if the proxy is read-only
 				isReadOnly = ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().isReadOnly();
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
-		PostLoadEvent postLoadEvent = new PostLoadEvent(session).setEntity(result)
-				.setId(id).setPersister(persister);
-		PostLoadEventListener[] listeners = session.getListeners().getPostLoadEventListeners();
-		for ( int i = 0; i < listeners.length; i++ ) {
-			listeners[i].onPostLoad(postLoadEvent);
+		PostLoadEvent postLoadEvent = new PostLoadEvent( session )
+				.setEntity( result )
+				.setId( id )
+				.setPersister( persister );
+
+		for ( PostLoadEventListener listener : postLoadEventListeners( session ) ) {
+			listener.onPostLoad( postLoadEvent );
 		}
 
 		return result;
 	}
+
+	private Iterable<PostLoadEventListener> postLoadEventListeners(EventSource session) {
+		return session
+				.getFactory()
+				.getServiceRegistry()
+				.getService( EventListenerRegistry.class )
+				.getEventListenerGroup( EventType.POST_LOAD )
+				.listeners();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
index 8b8c33d7bb..fedc863b18 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
@@ -1,1382 +1,1374 @@
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
 package org.hibernate.impl;
 
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.net.URL;
 import java.sql.Connection;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
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
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.event.EventListeners;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
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
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.internal.ConcurrentStatisticsImpl;
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
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SessionFactoryImpl.class.getName());
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
 	private final transient ServiceRegistryImplementor serviceRegistry;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient QueryCache queryCache;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient Map<String,QueryCache> queryCaches;
 	private final transient ConcurrentMap<String,Region> allCacheRegions = new ConcurrentHashMap<String, Region>();
 	private final transient Statistics statistics;
-	private final transient EventListeners eventListeners;
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
-	        EventListeners listeners,
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.buildingSessionFactory();
 
 		// todo : move stats building to SF service reg building
 		this.statistics = buildStatistics( settings, serviceRegistry );
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 		this.interceptor = cfg.getInterceptor();
 
 		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
 				this,
 				cfg
 		);
 		this.settings = settings;
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
-        this.eventListeners = listeners;
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
 
 		for ( Integrator integrator : locateIntegrators( this.serviceRegistry ) ) {
 			integrator.integrate( cfg, this, (SessionFactoryServiceRegistry) this.serviceRegistry );
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
 			new SchemaExport( getJdbcServices(), cfg ).create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( getJdbcServices(), cfg ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( getJdbcServices(), cfg ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( getJdbcServices(), cfg );
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
 
 	private Iterable<Integrator> locateIntegrators(ServiceRegistryImplementor serviceRegistry) {
 		List<Integrator> integrators = new ArrayList<Integrator>();
 
 		// todo : add "known" integrators -> BV, hibernate validation, search, envers
 
 		final Properties properties = new Properties();
 
 		ClassLoaderService classLoader = serviceRegistry.getService( ClassLoaderService.class );
 		List<URL> urls = classLoader.locateResources( "META-INF/hibernate/org.hibernate.impl.Integrator" );
 		for ( URL url : urls ) {
 			try {
 				final InputStream propertyStream = url.openStream();
 				try {
 					properties.clear();
 					properties.load( propertyStream );
 					// for now we only understand 'implClass' as key
 					final String implClass = properties.getProperty( "implClass" );
 					Class integratorClass = classLoader.classForName( implClass );
 					try {
 						integrators.add( (Integrator) integratorClass.newInstance() );
 					}
 					catch (Exception e) {
 						throw new HibernateException( "Unable to instantiate specified Integrator class [" + implClass + "]", e );
 					}
 				}
 				finally {
 					try {
 						propertyStream.close();
 					}
 					catch (IOException ignore) {
 					}
 				}
 			}
 			catch ( IOException ioe ) {
 				LOG.debugf( ioe, "Unable to process Integrator service file [%s], skipping" , url.toExternalForm() );
 			}
 		}
 		return integrators;
 	}
 
 	@Override
 	public void addObserver(SessionFactoryObserver observer) {
 		this.observer.addObserver( observer );
 	}
 
 	private Statistics buildStatistics(Settings settings, ServiceRegistry serviceRegistry) {
 		Statistics statistics = new ConcurrentStatisticsImpl( this );
 		statistics.setStatisticsEnabled( settings.isStatisticsEnabled() );
 		LOG.debugf("Statistics initialized [enabled=%s]", settings.isStatisticsEnabled());
 		return statistics;
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
 
 	public StatelessSession openStatelessSession() {
 		return new StatelessSessionImpl( null, this );
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return new StatelessSessionImpl( connection, this );
 	}
 
 	private SessionImpl openSession(
 		Connection connection,
 	    boolean autoClose,
 	    long timestamp,
 	    Interceptor sessionLocalInterceptor
 	) {
 		return new SessionImpl(
 		        connection,
 		        this,
 		        autoClose,
 		        timestamp,
 		        sessionLocalInterceptor == null ? interceptor : sessionLocalInterceptor,
 		        settings.getDefaultEntityMode(),
 		        settings.isFlushBeforeCompletionEnabled(),
 		        settings.isAutoCloseSessionEnabled(),
 		        settings.getConnectionReleaseMode()
 			);
 	}
 
 	public Session openSession(Connection connection, Interceptor sessionLocalInterceptor) {
 		return openSession(connection, false, Long.MIN_VALUE, sessionLocalInterceptor);
 	}
 
 	public Session openSession(Interceptor sessionLocalInterceptor) throws HibernateException {
 		// note that this timestamp is not correct if the connection provider
 		// returns an older JDBC connection that was associated with a
 		// transaction that was already begun before openSession() was called
 		// (don't know any possible solution to this!)
 		long timestamp = settings.getRegionFactory().nextTimestamp();
 		return openSession( null, true, timestamp, sessionLocalInterceptor );
 	}
 
 	public Session openSession(Connection connection) {
 		return openSession(connection, interceptor); //prevents this session from adding things to cache
 	}
 
 	public Session openSession() throws HibernateException {
 		return openSession(interceptor);
 	}
 
 	public Session openTemporarySession() throws HibernateException {
 		return new SessionImpl(
 				null,
 		        this,
 		        true,
 		        settings.getRegionFactory().nextTimestamp(),
 		        interceptor,
 		        settings.getDefaultEntityMode(),
 		        false,
 		        false,
 		        ConnectionReleaseMode.AFTER_STATEMENT
 			);
 	}
 
 	public Session openSession(
 			final Connection connection,
 	        final boolean flushBeforeCompletionEnabled,
 	        final boolean autoCloseSessionEnabled,
 	        final ConnectionReleaseMode connectionReleaseMode) throws HibernateException {
 		return new SessionImpl(
 				connection,
 		        this,
 		        true,
 		        settings.getRegionFactory().nextTimestamp(),
 		        interceptor,
 		        settings.getDefaultEntityMode(),
 		        flushBeforeCompletionEnabled,
 		        autoCloseSessionEnabled,
 		        connectionReleaseMode
 			);
 	}
 
 	public Session getCurrentSession() throws HibernateException {
 		if ( currentSessionContext == null ) {
 			throw new HibernateException( "No CurrentSessionContext configured!" );
 		}
 		return currentSessionContext.currentSession();
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
-		eventListeners.destroyListeners();
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
 		return statistics;
 	}
 
 	public StatisticsImplementor getStatisticsImplementor() {
 		return (StatisticsImplementor) statistics;
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
 
-	public EventListeners getEventListeners() {
-		return eventListeners;
-	}
-
 	@Override
 	public ServiceRegistryImplementor getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return entityNotFoundDelegate;
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
 		String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		String name = null;
 		if ( isNamed ) {
 			name = ois.readUTF();
 		}
 		Object result = SessionFactoryObjectFactory.getInstance( uuid );
 		if ( result == null ) {
             LOG.trace("Could not locate session factory by uuid [" + uuid + "] during session deserialization; trying name");
 			if ( isNamed ) {
 				result = SessionFactoryObjectFactory.getNamedInstance( name );
 			}
 			if ( result == null ) {
 				throw new InvalidObjectException( "could not resolve session factory during session deserialization [uuid=" + uuid + ", name=" + name + "]" );
 			}
 		}
 		return ( SessionFactoryImpl ) result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
index 6eba65c09f..758b00d2eb 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
@@ -1,2191 +1,2173 @@
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
 package org.hibernate.impl;
 
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
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
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
-import org.hibernate.event.EventListeners;
 import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
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
+import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.service.event.spi.EventListenerRegistry;
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
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, SessionImpl.class.getName());
 
 	private transient long timestamp;
 
 	private transient ActionQueue actionQueue;
 	private transient StatefulPersistenceContext persistenceContext;
 	private transient TransactionCoordinatorImpl transactionCoordinator;
-	private transient EventListeners listeners;
 	private transient Interceptor interceptor;
 	private transient EntityNameResolver entityNameResolver = new CoordinatingEntityNameResolver();
 
 	private transient ConnectionReleaseMode connectionReleaseMode;
 	private transient FlushMode flushMode = FlushMode.AUTO;
 	private transient CacheMode cacheMode = CacheMode.NORMAL;
 	private transient EntityMode entityMode = EntityMode.POJO;
 	private transient boolean autoClear; //for EJB3
 	private transient boolean autoJoinTransactions = true;
 
 	private transient int dontFlushFromFind = 0;
 	private transient boolean flushBeforeCompletionEnabled;
 	private transient boolean autoCloseSessionEnabled;
 
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
 		super( parent.factory );
 		this.rootSession = parent;
 		this.timestamp = parent.timestamp;
 		this.transactionCoordinator = parent.transactionCoordinator;
 		this.interceptor = parent.interceptor;
-		this.listeners = parent.listeners;
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
 	 * @param autoclose NOT USED
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
 			final boolean autoclose,
 			final long timestamp,
 			final Interceptor interceptor,
 			final EntityMode entityMode,
 			final boolean flushBeforeCompletionEnabled,
 			final boolean autoCloseSessionEnabled,
 			final ConnectionReleaseMode connectionReleaseMode) {
 		super( factory );
 		this.rootSession = null;
 		this.timestamp = timestamp;
 		this.entityMode = entityMode;
 		this.interceptor = interceptor;
-		this.listeners = factory.getEventListeners();
 		this.actionQueue = new ActionQueue( this );
 		this.persistenceContext = new StatefulPersistenceContext( this );
 		this.flushBeforeCompletionEnabled = flushBeforeCompletionEnabled;
 		this.autoCloseSessionEnabled = autoCloseSessionEnabled;
 		this.connectionReleaseMode = connectionReleaseMode;
 
 		this.transactionCoordinator = new TransactionCoordinatorImpl( connection, this );
 		this.transactionCoordinator.getJdbcCoordinator().getLogicalConnection().addObserver(
 				new ConnectionObserverStatsBridge( factory )
 		);
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
         if (factory.getStatistics().isStatisticsEnabled()) factory.getStatisticsImplementor().openSession();
 
         LOG.debugf("Opened session at timestamp: %s", timestamp);
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
-		SaveOrUpdateEventListener[] saveOrUpdateEventListener = listeners.getSaveOrUpdateEventListeners();
-		for ( int i = 0; i < saveOrUpdateEventListener.length; i++ ) {
-			saveOrUpdateEventListener[i].onSaveOrUpdate(event);
+		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE_UPDATE ) ) {
+			listener.onSaveOrUpdate( event );
 		}
 	}
 
+	private <T> Iterable<T> listeners(EventType<T> type) {
+		return eventListenerGroup( type ).listeners();
+	}
+
+	private <T> EventListenerGroup<T> eventListenerGroup(EventType<T> type) {
+		return factory.getServiceRegistry().getService( EventListenerRegistry.class ).getEventListenerGroup( type );
+	}
+
 
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
-		SaveOrUpdateEventListener[] saveEventListener = listeners.getSaveEventListeners();
-		for ( int i = 0; i < saveEventListener.length; i++ ) {
-			saveEventListener[i].onSaveOrUpdate(event);
+		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE ) ) {
+			listener.onSaveOrUpdate( event );
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
-		SaveOrUpdateEventListener[] updateEventListener = listeners.getUpdateEventListeners();
-		for ( int i = 0; i < updateEventListener.length; i++ ) {
-			updateEventListener[i].onSaveOrUpdate(event);
+		for ( SaveOrUpdateEventListener listener : listeners( EventType.UPDATE ) ) {
+			listener.onSaveOrUpdate( event );
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
 
-	private void fireLock(LockEvent lockEvent) {
+	private void fireLock(LockEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
-		LockEventListener[] lockEventListener = listeners.getLockEventListeners();
-		for ( int i = 0; i < lockEventListener.length; i++ ) {
-			lockEventListener[i].onLock( lockEvent );
+		for ( LockEventListener listener : listeners( EventType.LOCK ) ) {
+			listener.onLock( event );
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
-		PersistEventListener[] persistEventListener = listeners.getPersistEventListeners();
-		for ( int i = 0; i < persistEventListener.length; i++ ) {
-			persistEventListener[i].onPersist(event, copiedAlready);
+		for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
+			listener.onPersist( event, copiedAlready );
 		}
 	}
 
 	private void firePersist(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
-		PersistEventListener[] createEventListener = listeners.getPersistEventListeners();
-		for ( int i = 0; i < createEventListener.length; i++ ) {
-			createEventListener[i].onPersist(event);
+		for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
+			listener.onPersist( event );
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
-		PersistEventListener[] persistEventListener = listeners.getPersistOnFlushEventListeners();
-		for ( int i = 0; i < persistEventListener.length; i++ ) {
-			persistEventListener[i].onPersist(event, copiedAlready);
+		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
+			listener.onPersist( event, copiedAlready );
 		}
 	}
 
 	private void firePersistOnFlush(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
-		PersistEventListener[] createEventListener = listeners.getPersistOnFlushEventListeners();
-		for ( int i = 0; i < createEventListener.length; i++ ) {
-			createEventListener[i].onPersist(event);
+		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
+			listener.onPersist( event );
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
-		MergeEventListener[] mergeEventListener = listeners.getMergeEventListeners();
-		for ( int i = 0; i < mergeEventListener.length; i++ ) {
-			mergeEventListener[i].onMerge(event);
+		for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
+			listener.onMerge( event );
 		}
 		return event.getResult();
 	}
 
 	private void fireMerge(Map copiedAlready, MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
-		MergeEventListener[] mergeEventListener = listeners.getMergeEventListeners();
-		for ( int i = 0; i < mergeEventListener.length; i++ ) {
-			mergeEventListener[i].onMerge(event, copiedAlready);
+		for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
+			listener.onMerge( event, copiedAlready );
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
-		DeleteEventListener[] deleteEventListener = listeners.getDeleteEventListeners();
-		for ( int i = 0; i < deleteEventListener.length; i++ ) {
-			deleteEventListener[i].onDelete( event );
+		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
+			listener.onDelete( event );
 		}
 	}
 
 	private void fireDelete(DeleteEvent event, Set transientEntities) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
-		DeleteEventListener[] deleteEventListener = listeners.getDeleteEventListeners();
-		for ( int i = 0; i < deleteEventListener.length; i++ ) {
-			deleteEventListener[i].onDelete( event, transientEntities );
+		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
+			listener.onDelete( event, transientEntities );
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
-		LoadEventListener[] loadEventListener = listeners.getLoadEventListeners();
-		for ( int i = 0; i < loadEventListener.length; i++ ) {
-			loadEventListener[i].onLoad(event, loadType);
+		for ( LoadEventListener listener : listeners( EventType.LOAD ) ) {
+			listener.onLoad( event, loadType );
 		}
 	}
 
 
 	// refresh() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void refresh(Object object) throws HibernateException {
 		fireRefresh( new RefreshEvent(object, this) );
 	}
 
 	public void refresh(Object object, LockMode lockMode) throws HibernateException {
 		fireRefresh( new RefreshEvent(object, lockMode, this) );
 	}
 
 	public void refresh(Object object, LockOptions lockOptions) throws HibernateException {
 		fireRefresh( new RefreshEvent(object, lockOptions, this) );
 	}
 
 	public void refresh(Object object, Map refreshedAlready) throws HibernateException {
 		fireRefresh( refreshedAlready, new RefreshEvent( object, this ) );
 	}
 
-	private void fireRefresh(RefreshEvent refreshEvent) {
+	private void fireRefresh(RefreshEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
-		RefreshEventListener[] refreshEventListener = listeners.getRefreshEventListeners();
-		for ( int i = 0; i < refreshEventListener.length; i++ ) {
-			refreshEventListener[i].onRefresh( refreshEvent );
+		for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
+			listener.onRefresh( event );
 		}
 	}
 
-	private void fireRefresh(Map refreshedAlready, RefreshEvent refreshEvent) {
+	private void fireRefresh(Map refreshedAlready, RefreshEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
-		RefreshEventListener[] refreshEventListener = listeners.getRefreshEventListeners();
-		for ( int i = 0; i < refreshEventListener.length; i++ ) {
-			refreshEventListener[i].onRefresh( refreshEvent, refreshedAlready );
+		for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
+			listener.onRefresh( event, refreshedAlready );
 		}
 	}
 
 
 	// replicate() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void replicate(Object obj, ReplicationMode replicationMode) throws HibernateException {
 		fireReplicate( new ReplicateEvent(obj, replicationMode, this) );
 	}
 
 	public void replicate(String entityName, Object obj, ReplicationMode replicationMode)
 	throws HibernateException {
 		fireReplicate( new ReplicateEvent( entityName, obj, replicationMode, this ) );
 	}
 
 	private void fireReplicate(ReplicateEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
-		ReplicateEventListener[] replicateEventListener = listeners.getReplicateEventListeners();
-		for ( int i = 0; i < replicateEventListener.length; i++ ) {
-			replicateEventListener[i].onReplicate(event);
+		for ( ReplicateEventListener listener : listeners( EventType.REPLICATE ) ) {
+			listener.onReplicate( event );
 		}
 	}
 
 
 	// evict() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * remove any hard references to the entity that are held by the infrastructure
 	 * (references held by application or other persistant instances are okay)
 	 */
 	public void evict(Object object) throws HibernateException {
 		fireEvict( new EvictEvent( object, this ) );
 	}
 
-	private void fireEvict(EvictEvent evictEvent) {
+	private void fireEvict(EvictEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
-		EvictEventListener[] evictEventListener = listeners.getEvictEventListeners();
-		for ( int i = 0; i < evictEventListener.length; i++ ) {
-			evictEventListener[i].onEvict( evictEvent );
+		for ( EvictEventListener listener : listeners( EventType.EVICT ) ) {
+			listener.onEvict( event );
 		}
 	}
 
 	/**
 	 * detect in-memory changes, determine if the changes are to tables
 	 * named in the query and, if so, complete execution the flush
 	 */
 	protected boolean autoFlushIfRequired(Set querySpaces) throws HibernateException {
 		errorIfClosed();
 		if ( ! isTransactionInProgress() ) {
 			// do not auto-flush while outside a transaction
 			return false;
 		}
-		AutoFlushEvent event = new AutoFlushEvent(querySpaces, this);
-		AutoFlushEventListener[] autoFlushEventListener = listeners.getAutoFlushEventListeners();
-		for ( int i = 0; i < autoFlushEventListener.length; i++ ) {
-			autoFlushEventListener[i].onAutoFlush(event);
+		AutoFlushEvent event = new AutoFlushEvent( querySpaces, this );
+		for ( AutoFlushEventListener listener : listeners( EventType.AUTO_FLUSH ) ) {
+			listener.onAutoFlush( event );
 		}
 		return event.isFlushRequired();
 	}
 
 	public boolean isDirty() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
         LOG.debugf("Checking session dirtiness");
 		if ( actionQueue.areInsertionsOrDeletionsQueued() ) {
             LOG.debugf("Session dirty (scheduled updates and insertions)");
 			return true;
 		}
-        DirtyCheckEvent event = new DirtyCheckEvent(this);
-        DirtyCheckEventListener[] dirtyCheckEventListener = listeners.getDirtyCheckEventListeners();
-        for (int i = 0; i < dirtyCheckEventListener.length; i++) {
-            dirtyCheckEventListener[i].onDirtyCheck(event);
+        DirtyCheckEvent event = new DirtyCheckEvent( this );
+		for ( DirtyCheckEventListener listener : listeners( EventType.DIRTY_CHECK ) ) {
+			listener.onDirtyCheck( event );
 		}
         return event.isDirty();
 	}
 
 	public void flush() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( persistenceContext.getCascadeLevel() > 0 ) {
 			throw new HibernateException("Flush during cascade is dangerous");
 		}
-		FlushEventListener[] flushEventListener = listeners.getFlushEventListeners();
-		for ( int i = 0; i < flushEventListener.length; i++ ) {
-			flushEventListener[i].onFlush( new FlushEvent(this) );
+		for ( FlushEventListener listener : listeners( EventType.FLUSH ) ) {
+			listener.onFlush( new FlushEvent( this ) );
 		}
 	}
 
 	public void forceFlush(EntityEntry entityEntry) throws HibernateException {
 		errorIfClosed();
         if (LOG.isDebugEnabled()) LOG.debugf("Flushing to force deletion of re-saved object: %s",
                                              MessageHelper.infoString(entityEntry.getPersister(), entityEntry.getId(), getFactory()));
 
 		if ( persistenceContext.getCascadeLevel() > 0 ) {
 			throw new ObjectDeletedException(
 				"deleted object would be re-saved by cascade (remove deleted object from associations)",
 				entityEntry.getId(),
 				entityEntry.getPersister().getEntityName()
 			);
 		}
 
 		flush();
 	}
 
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		List results = CollectionHelper.EMPTY_LIST;
 		boolean success = false;
 
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 		}
 		return results;
 	}
 
 	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 		}
 		return result;
 	}
 
     public int executeNativeUpdate(NativeSQLQuerySpecification nativeQuerySpecification,
             QueryParameters queryParameters) throws HibernateException {
         errorIfClosed();
         checkTransactionSynchStatus();
         queryParameters.validateParameters();
         NativeSQLQueryPlan plan = getNativeSQLQueryPlan( nativeQuerySpecification );
 
 
         autoFlushIfRequired( plan.getCustomQuery().getQuerySpaces() );
 
         boolean success = false;
         int result = 0;
         try {
             result = plan.performExecuteUpdate(queryParameters, this);
             success = true;
         } finally {
             afterOperation(success);
         }
         return result;
     }
 
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, true );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
 		try {
 			return plan.performIterate( queryParameters, this );
 		}
 		finally {
 			dontFlushFromFind--;
 		}
 	}
 
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return plan.performScroll( queryParameters, this );
 		}
 		finally {
 			dontFlushFromFind--;
 		}
 	}
 
 	public Query createFilter(Object collection, String queryString) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		CollectionFilterImpl filter = new CollectionFilterImpl(
 				queryString,
 		        collection,
 		        this,
 		        getFilterQueryPlan( collection, queryString, null, false ).getParameterMetadata()
 		);
 		filter.setComment( queryString );
 		return filter;
 	}
 
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.getNamedQuery( queryName );
 	}
 
 	public Object instantiate(String entityName, Serializable id) throws HibernateException {
 		return instantiate( factory.getEntityPersister( entityName ), id );
 	}
 
 	/**
 	 * give the interceptor an opportunity to override the default instantiation
 	 */
 	public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		Object result = interceptor.instantiate( persister.getEntityName(), entityMode, id );
 		if ( result == null ) {
 			result = persister.instantiate( id, this );
 		}
 		return result;
 	}
 
 	public EntityMode getEntityMode() {
 		checkTransactionSynchStatus();
 		return entityMode;
 	}
 
 	public void setFlushMode(FlushMode flushMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
         LOG.trace("Setting flush mode to: " + flushMode);
 		this.flushMode = flushMode;
 	}
 
 	public FlushMode getFlushMode() {
 		checkTransactionSynchStatus();
 		return flushMode;
 	}
 
 	public CacheMode getCacheMode() {
 		checkTransactionSynchStatus();
 		return cacheMode;
 	}
 
 	public void setCacheMode(CacheMode cacheMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
         LOG.trace("Setting cache mode to: " + cacheMode);
 		this.cacheMode= cacheMode;
 	}
 
 	public Transaction getTransaction() throws HibernateException {
 		errorIfClosed();
 		return transactionCoordinator.getTransaction();
 	}
 
 	public Transaction beginTransaction() throws HibernateException {
 		errorIfClosed();
         // todo : should seriously consider not allowing a txn to begin from a child session
         // can always route the request to the root session...
         if (rootSession != null) LOG.transactionStartedOnNonRootSession();
 
 		Transaction result = getTransaction();
 		result.begin();
 		return result;
 	}
 
 	public EntityPersister getEntityPersister(final String entityName, final Object object) {
 		errorIfClosed();
 		if (entityName==null) {
 			return factory.getEntityPersister( guessEntityName( object ) );
 		}
 		else {
 			// try block is a hack around fact that currently tuplizers are not
 			// given the opportunity to resolve a subclass entity name.  this
 			// allows the (we assume custom) interceptor the ability to
 			// influence this decision if we were not able to based on the
 			// given entityName
 			try {
 				return factory.getEntityPersister( entityName )
 						.getSubclassEntityPersister( object, getFactory(), entityMode );
 			}
 			catch( HibernateException e ) {
 				try {
 					return getEntityPersister( null, object );
 				}
 				catch( HibernateException e2 ) {
 					throw e;
 				}
 			}
 		}
 	}
 
 	// not for internal use:
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object instanceof HibernateProxy ) {
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			if ( li.getSession() != this ) {
 				throw new TransientObjectException( "The proxy was not associated with this session" );
 			}
 			return li.getIdentifier();
 		}
 		else {
 			EntityEntry entry = persistenceContext.getEntry(object);
 			if ( entry == null ) {
 				throw new TransientObjectException( "The instance was not associated with this session" );
 			}
 			return entry.getId();
 		}
 	}
 
 	/**
 	 * Get the id value for an object that is actually associated with the session. This
 	 * is a bit stricter than getEntityIdentifierIfNotUnsaved().
 	 */
 	public Serializable getContextEntityIdentifier(Object object) {
 		errorIfClosed();
 		if ( object instanceof HibernateProxy ) {
 			return getProxyIdentifier( object );
 		}
 		else {
 			EntityEntry entry = persistenceContext.getEntry(object);
 			return entry != null ? entry.getId() : null;
 		}
 	}
 
 	private Serializable getProxyIdentifier(Object proxy) {
 		return ( (HibernateProxy) proxy ).getHibernateLazyInitializer().getIdentifier();
 	}
 
 	private FilterQueryPlan getFilterQueryPlan(
 			Object collection,
 			String filter,
 			QueryParameters parameters,
 			boolean shallow) throws HibernateException {
 		if ( collection == null ) {
 			throw new NullPointerException( "null collection passed to filter" );
 		}
 
 		CollectionEntry entry = persistenceContext.getCollectionEntryOrNull( collection );
 		final CollectionPersister roleBeforeFlush = (entry == null) ? null : entry.getLoadedPersister();
 
 		FilterQueryPlan plan = null;
 		if ( roleBeforeFlush == null ) {
 			// if it was previously unreferenced, we need to flush in order to
 			// get its state into the database in order to execute query
 			flush();
 			entry = persistenceContext.getCollectionEntryOrNull( collection );
 			CollectionPersister roleAfterFlush = (entry == null) ? null : entry.getLoadedPersister();
 			if ( roleAfterFlush == null ) {
 				throw new QueryException( "The collection was unreferenced" );
 			}
 			plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleAfterFlush.getRole(), shallow, getEnabledFilters() );
 		}
 		else {
 			// otherwise, we only need to flush if there are in-memory changes
 			// to the queried tables
 			plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleBeforeFlush.getRole(), shallow, getEnabledFilters() );
 			if ( autoFlushIfRequired( plan.getQuerySpaces() ) ) {
 				// might need to run a different filter entirely after the flush
 				// because the collection role may have changed
 				entry = persistenceContext.getCollectionEntryOrNull( collection );
 				CollectionPersister roleAfterFlush = (entry == null) ? null : entry.getLoadedPersister();
 				if ( roleBeforeFlush != roleAfterFlush ) {
 					if ( roleAfterFlush == null ) {
 						throw new QueryException( "The collection was dereferenced" );
 					}
 					plan = factory.getQueryPlanCache().getFilterQueryPlan( filter, roleAfterFlush.getRole(), shallow, getEnabledFilters() );
 				}
 			}
 		}
 
 		if ( parameters != null ) {
 			parameters.getPositionalParameterValues()[0] = entry.getLoadedKey();
 			parameters.getPositionalParameterTypes()[0] = entry.getLoadedPersister().getKeyType();
 		}
 
 		return plan;
 	}
 
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, false );
 		List results = CollectionHelper.EMPTY_LIST;
 
 		boolean success = false;
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 		}
 		return results;
 	}
 
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, true );
 		return plan.performIterate( queryParameters, this );
 	}
 
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
 	public Criteria createCriteria(String entityName, String alias) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, alias, this);
 	}
 
 	public Criteria createCriteria(Class persistentClass) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
 	public Criteria createCriteria(String entityName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl(entityName, this);
 	}
 
 	public ScrollableResults scroll(CriteriaImpl criteria, ScrollMode scrollMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String entityName = criteria.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable(entityName),
 				factory,
 				criteria,
 				entityName,
 				getLoadQueryInfluencers()
 		);
 		autoFlushIfRequired( loader.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return loader.scroll(this, scrollMode);
 		}
 		finally {
 			dontFlushFromFind--;
 		}
 	}
 
 	public List list(CriteriaImpl criteria) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String[] implementors = factory.getImplementors( criteria.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		Set spaces = new HashSet();
 		for( int i=0; i <size; i++ ) {
 
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 					factory,
 					criteria,
 					implementors[i],
 					getLoadQueryInfluencers()
 				);
 
 			spaces.addAll( loaders[i].getQuerySpaces() );
 
 		}
 
 		autoFlushIfRequired(spaces);
 
 		List results = Collections.EMPTY_LIST;
 		dontFlushFromFind++;
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
 			dontFlushFromFind--;
 			afterOperation(success);
 		}
 
 		return results;
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
 		EntityPersister persister = factory.getEntityPersister(entityName);
 		if ( !(persister instanceof OuterJoinLoadable) ) {
 			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
 		}
 		return ( OuterJoinLoadable ) persister;
 	}
 
 	public boolean contains(Object object) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object instanceof HibernateProxy ) {
 			//do not use proxiesByKey, since not all
 			//proxies that point to this session's
 			//instances are in that collection!
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				//if it is an uninitialized proxy, pointing
 				//with this session, then when it is accessed,
 				//the underlying instance will be "contained"
 				return li.getSession()==this;
 			}
 			else {
 				//if it is initialized, see if the underlying
 				//instance is contained, since we need to
 				//account for the fact that it might have been
 				//evicted
 				object = li.getImplementation();
 			}
 		}
 		// A session is considered to contain an entity only if the entity has
 		// an entry in the session's persistence context and the entry reports
 		// that the entity has not been removed
 		EntityEntry entry = persistenceContext.getEntry( object );
 		return entry != null && entry.getStatus() != Status.DELETED && entry.getStatus() != Status.GONE;
 	}
 
 	public Query createQuery(String queryString) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createQuery( queryString );
 	}
 
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createSQLQuery( sql );
 	}
 
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
         LOG.trace("Scroll SQL query: " + customQuery.getSQL());
 
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		autoFlushIfRequired( loader.getQuerySpaces() );
 
 		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
 		try {
 			return loader.scroll(queryParameters, this);
 		}
 		finally {
 			dontFlushFromFind--;
 		}
 	}
 
 	// basically just an adapted copy of find(CriteriaImpl)
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 
         LOG.trace("SQL query: " + customQuery.getSQL());
 
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		autoFlushIfRequired( loader.getQuerySpaces() );
 
 		dontFlushFromFind++;
 		boolean success = false;
 		try {
 			List results = loader.list(this, queryParameters);
 			success = true;
 			return results;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 		}
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		checkTransactionSynchStatus();
 		return factory;
 	}
 
 	public void initializeCollection(PersistentCollection collection, boolean writing)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
-		InitializeCollectionEventListener[] listener = listeners.getInitializeCollectionEventListeners();
-		for ( int i = 0; i < listener.length; i++ ) {
-			listener[i].onInitializeCollection( new InitializeCollectionEvent(collection, this) );
+		InitializeCollectionEvent event = new InitializeCollectionEvent( collection, this );
+		for ( InitializeCollectionEventListener listener : listeners( EventType.INIT_COLLECTION ) ) {
+			listener.onInitializeCollection( event );
 		}
 	}
 
 	public String bestGuessEntityName(Object object) {
 		if (object instanceof HibernateProxy) {
 			LazyInitializer initializer = ( ( HibernateProxy ) object ).getHibernateLazyInitializer();
 			// it is possible for this method to be called during flush processing,
 			// so make certain that we do not accidentally initialize an uninitialized proxy
 			if ( initializer.isUninitialized() ) {
 				return initializer.getEntityName();
 			}
 			object = initializer.getImplementation();
 		}
 		EntityEntry entry = persistenceContext.getEntry(object);
 		if (entry==null) {
 			return guessEntityName(object);
 		}
 		else {
 			return entry.getPersister().getEntityName();
 		}
 	}
 
 	public String getEntityName(Object object) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if (object instanceof HibernateProxy) {
 			if ( !persistenceContext.containsProxy( object ) ) {
 				throw new TransientObjectException("proxy was not associated with the session");
 			}
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation();
 		}
 
 		EntityEntry entry = persistenceContext.getEntry(object);
 		if ( entry == null ) {
 			throwTransientObjectException( object );
 		}
 		return entry.getPersister().getEntityName();
 	}
 
 	private void throwTransientObjectException(Object object) throws HibernateException {
 		throw new TransientObjectException(
 				"object references an unsaved transient instance - save the transient instance before flushing: " +
 				guessEntityName(object)
 			);
 	}
 
 	public String guessEntityName(Object object) throws HibernateException {
 		errorIfClosed();
 		return entityNameResolver.resolveEntityName( object );
 	}
 
 	public void cancelQuery() throws HibernateException {
 		errorIfClosed();
 		getTransactionCoordinator().getJdbcCoordinator().cancelLastQuery();
 	}
 
 	public Interceptor getInterceptor() {
 		checkTransactionSynchStatus();
 		return interceptor;
 	}
 
 	public int getDontFlushFromFind() {
 		return dontFlushFromFind;
 	}
 
 	public String toString() {
 		StringBuffer buf = new StringBuffer(500)
 			.append( "SessionImpl(" );
 		if ( !isClosed() ) {
 			buf.append(persistenceContext)
 				.append(";")
 				.append(actionQueue);
 		}
 		else {
 			buf.append("<closed>");
 		}
 		return buf.append(')').toString();
 	}
 
-	public EventListeners getListeners() {
-		return listeners;
-	}
-
 	public ActionQueue getActionQueue() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return actionQueue;
 	}
 
 	public PersistenceContext getPersistenceContext() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext;
 	}
 
 	public SessionStatistics getStatistics() {
 		checkTransactionSynchStatus();
 		return new SessionStatisticsImpl(this);
 	}
 
 	public boolean isEventSource() {
 		checkTransactionSynchStatus();
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isDefaultReadOnly() {
 		return persistenceContext.isDefaultReadOnly();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		persistenceContext.setDefaultReadOnly( defaultReadOnly );
 	}
 
 	public boolean isReadOnly(Object entityOrProxy) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return persistenceContext.isReadOnly( entityOrProxy );
 	}
 
 	public void setReadOnly(Object entity, boolean readOnly) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		persistenceContext.setReadOnly(entity, readOnly);
 	}
 
 	public void doWork(final Work work) throws HibernateException {
 		WorkExecutorVisitable<Void> realWork = new WorkExecutorVisitable<Void>() {
 			@Override
 			public Void accept(WorkExecutor<Void> workExecutor, Connection connection) throws SQLException {
 				workExecutor.executeWork( work, connection );
 				return null;
 			}
 		};
 		doWork( realWork );
 	}
 
 	public <T> T doReturningWork(final ReturningWork<T> work) throws HibernateException {
 		WorkExecutorVisitable<T> realWork = new WorkExecutorVisitable<T>() {
 			@Override
 			public T accept(WorkExecutor<T> workExecutor, Connection connection) throws SQLException {
 				return workExecutor.executeReturningWork( work, connection );
 			}
 		};
 		return doWork( realWork );
 	}
 
 	private <T> T doWork(WorkExecutorVisitable<T> work) throws HibernateException {
 		return transactionCoordinator.getJdbcCoordinator().coordinateWork( work );
 	}
 
 	public void afterScrollOperation() {
 		// nothing to do in a stateful session
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		errorIfClosed();
 		return transactionCoordinator;
 	}
 
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return loadQueryInfluencers;
 	}
 
 	// filter support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Filter getEnabledFilter(String filterName) {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Filter enableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.enableFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void disableFilter(String filterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.disableFilter( filterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object getFilterParameterValue(String filterParameterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getFilterParameterValue( filterParameterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Type getFilterParameterType(String filterParameterName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getFilterParameterType( filterParameterName );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Map getEnabledFilters() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilters();
 	}
 
 
 	// internal fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String getFetchProfile() {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getInternalFetchProfile();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setFetchProfile(String fetchProfile) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.setInternalFetchProfile( fetchProfile );
 	}
 
 
 	// fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		return loadQueryInfluencers.isFetchProfileEnabled( name );
 	}
 
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.enableFetchProfile( name );
 	}
 
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.disableFetchProfile( name );
 	}
 
 
 	private void checkTransactionSynchStatus() {
 		if ( !isClosed() ) {
 			transactionCoordinator.pulse();
 		}
 	}
 
 	/**
 	 * Used by JDK serialization...
 	 *
 	 * @param ois The input stream from which we are being read...
 	 * @throws IOException Indicates a general IO stream exception
 	 * @throws ClassNotFoundException Indicates a class resolution issue
 	 */
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
         LOG.trace("Deserializing session");
 
 		ois.defaultReadObject();
 
 		entityNameResolver = new CoordinatingEntityNameResolver();
 
 		boolean isRootSession = ois.readBoolean();
 		connectionReleaseMode = ConnectionReleaseMode.parse( ( String ) ois.readObject() );
 		entityMode = EntityMode.parse( ( String ) ois.readObject() );
 		autoClear = ois.readBoolean();
 		autoJoinTransactions = ois.readBoolean();
 		flushMode = FlushMode.parse( ( String ) ois.readObject() );
 		cacheMode = CacheMode.parse( ( String ) ois.readObject() );
 		flushBeforeCompletionEnabled = ois.readBoolean();
 		autoCloseSessionEnabled = ois.readBoolean();
 		interceptor = ( Interceptor ) ois.readObject();
 
 		factory = SessionFactoryImpl.deserialize( ois );
-		listeners = factory.getEventListeners();
 
 		if ( isRootSession ) {
 			transactionCoordinator = TransactionCoordinatorImpl.deserialize( ois, this );
 		}
 
 		persistenceContext = StatefulPersistenceContext.deserialize( ois, this );
 		actionQueue = ActionQueue.deserialize( ois, this );
 
 		loadQueryInfluencers = ( LoadQueryInfluencers ) ois.readObject();
 
 		childSessionsByEntityMode = ( Map ) ois.readObject();
 
 		// LoadQueryInfluencers.getEnabledFilters() tries to validate each enabled
 		// filter, which will fail when called before FilterImpl.afterDeserialize( factory );
 		// Instead lookup the filter by name and then call FilterImpl.afterDeserialize( factory ).
 		Iterator iter = loadQueryInfluencers.getEnabledFilterNames().iterator();
 		while ( iter.hasNext() ) {
 			String filterName = ( String ) iter.next();
 			 ( ( FilterImpl ) loadQueryInfluencers.getEnabledFilter( filterName )  )
 					.afterDeserialize( factory );
 		}
 
 		if ( isRootSession && childSessionsByEntityMode != null ) {
 			iter = childSessionsByEntityMode.values().iterator();
 			while ( iter.hasNext() ) {
 				final SessionImpl child = ( ( SessionImpl ) iter.next() );
 				child.rootSession = this;
 				child.transactionCoordinator = this.transactionCoordinator;
 			}
 		}
 	}
 
 	/**
 	 * Used by JDK serialization...
 	 *
 	 * @param oos The output stream to which we are being written...
 	 * @throws IOException Indicates a general IO stream exception
 	 */
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		if ( ! transactionCoordinator.getJdbcCoordinator().getLogicalConnection().isReadyForSerialization() ) {
 			throw new IllegalStateException( "Cannot serialize a session while connected" );
 		}
 
         LOG.trace("Serializing session");
 
 		oos.defaultWriteObject();
 
 		oos.writeBoolean( rootSession == null );
 		oos.writeObject( connectionReleaseMode.toString() );
 		oos.writeObject( entityMode.toString() );
 		oos.writeBoolean( autoClear );
 		oos.writeBoolean( autoJoinTransactions );
 		oos.writeObject( flushMode.toString() );
 		oos.writeObject( cacheMode.toString() );
 		oos.writeBoolean( flushBeforeCompletionEnabled );
 		oos.writeBoolean( autoCloseSessionEnabled );
 		// we need to writeObject() on this since interceptor is user defined
 		oos.writeObject( interceptor );
 
 		factory.serialize( oos );
 
 		if ( rootSession == null ) {
 			transactionCoordinator.serialize( oos );
 		}
 
 		persistenceContext.serialize( oos );
 		actionQueue.serialize( oos );
 
 		// todo : look at optimizing these...
 		oos.writeObject( loadQueryInfluencers );
 		oos.writeObject( childSessionsByEntityMode );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public TypeHelper getTypeHelper() {
 		return getSessionFactory().getTypeHelper();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public LobHelper getLobHelper() {
 		if ( lobHelper == null ) {
 			lobHelper = new LobHelperImpl( this );
 		}
 		return lobHelper;
 	}
 
 	private transient LobHelperImpl lobHelper;
 
 	private static class LobHelperImpl implements LobHelper {
 		private final SessionImpl session;
 
 		private LobHelperImpl(SessionImpl session) {
 			this.session = session;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Blob createBlob(byte[] bytes) {
 			return lobCreator().createBlob( bytes );
 		}
 
 		private LobCreator lobCreator() {
 			return session.getFactory().getJdbcServices().getLobCreator( session );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Blob createBlob(InputStream stream, long length) {
 			return lobCreator().createBlob( stream, length );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Clob createClob(String string) {
 			return lobCreator().createClob( string );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Clob createClob(Reader reader, long length) {
 			return lobCreator().createClob( reader, length );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Clob createNClob(String string) {
 			return lobCreator().createNClob( string );
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Clob createNClob(Reader reader, long length) {
 			return lobCreator().createNClob( reader, length );
 		}
 	}
 
 	private class CoordinatingEntityNameResolver implements EntityNameResolver {
 		public String resolveEntityName(Object entity) {
 			String entityName = interceptor.getEntityName( entity );
 			if ( entityName != null ) {
 				return entityName;
 			}
 
 			Iterator itr = factory.iterateEntityNameResolvers( entityMode );
 			while ( itr.hasNext() ) {
 				final EntityNameResolver resolver = ( EntityNameResolver ) itr.next();
 				entityName = resolver.resolveEntityName( entity );
 				if ( entityName != null ) {
 					break;
 				}
 			}
 			if ( entityName != null ) {
 				return entityName;
 			}
 
 			// the old-time stand-by...
 			return entity.getClass().getName();
 		}
 	}
 
 	private class LockRequestImpl implements LockRequest {
 		private final LockOptions lockOptions;
 		private LockRequestImpl(LockOptions lo) {
 			lockOptions = new LockOptions();
 			LockOptions.copy(lo, lockOptions);
 		}
 
 		public LockMode getLockMode() {
 			return lockOptions.getLockMode();
 		}
 
 		public LockRequest setLockMode(LockMode lockMode) {
 			lockOptions.setLockMode(lockMode);
 			return this;
 		}
 
 		public int getTimeOut() {
 			return lockOptions.getTimeOut();
 		}
 
 		public LockRequest setTimeOut(int timeout) {
 			lockOptions.setTimeOut(timeout);
 			return this;
 		}
 
 		public boolean getScope() {
 			return lockOptions.getScope();
 		}
 
 		public LockRequest setScope(boolean scope) {
 			lockOptions.setScope(scope);
 			return this;
 		}
 
 		public void lock(String entityName, Object object) throws HibernateException {
 			fireLock( entityName, object, lockOptions );
 		}
 		public void lock(Object object) throws HibernateException {
 			fireLock( object, lockOptions );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
index c005ca008a..e8cddb7633 100755
--- a/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
@@ -1,713 +1,709 @@
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
 package org.hibernate.impl;
 
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
 import org.hibernate.HibernateLogger;
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
 import org.hibernate.event.EventListeners;
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
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, StatelessSessionImpl.class.getName());
 
 	private TransactionCoordinator transactionCoordinator;
 	private PersistenceContext temporaryPersistenceContext = new StatefulPersistenceContext( this );
 
 	StatelessSessionImpl(Connection connection, SessionFactoryImpl factory) {
 		super( factory );
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
 
-	public EventListeners getListeners() {
-		throw new UnsupportedOperationException();
-	}
-
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
index 44b2f91f3b..10d38b7d46 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
@@ -1,75 +1,76 @@
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
 
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderInitiator;
 import org.hibernate.engine.jdbc.internal.JdbcServicesInitiator;
 import org.hibernate.engine.transaction.internal.TransactionFactoryInitiator;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.internal.PersisterFactoryInitiator;
 import org.hibernate.service.classloading.internal.ClassLoaderServiceInitiator;
 import org.hibernate.service.internal.SessionFactoryServiceRegistryFactoryInitiator;
+import org.hibernate.service.event.internal.EventListenerServiceInitiator;
 import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
 import org.hibernate.service.jdbc.connections.internal.MultiTenantConnectionProviderInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectFactoryInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectResolverInitiator;
 import org.hibernate.service.jmx.internal.JmxServiceInitiator;
 import org.hibernate.service.jndi.internal.JndiServiceInitiator;
 import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 import org.hibernate.service.spi.BasicServiceInitiator;
 
 import java.util.ArrayList;
 import java.util.List;
 
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
 
 		return serviceInitiators;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerGroupImpl.java b/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerGroupImpl.java
new file mode 100644
index 0000000000..0d49c518d9
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerGroupImpl.java
@@ -0,0 +1,179 @@
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
+package org.hibernate.service.event.internal;
+
+import java.lang.reflect.Array;
+import java.util.ArrayList;
+import java.util.Collections;
+import java.util.LinkedHashSet;
+import java.util.List;
+import java.util.ListIterator;
+import java.util.Set;
+
+import org.hibernate.event.EventType;
+import org.hibernate.service.event.spi.DuplicationStrategy;
+import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.service.event.spi.EventListenerRegistrationException;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EventListenerGroupImpl<T> implements EventListenerGroup<T> {
+	private EventType<T> eventType;
+
+	private final Set<DuplicationStrategy> duplicationStrategies = new LinkedHashSet<DuplicationStrategy>();
+	private List<T> listeners;
+
+	public EventListenerGroupImpl(EventType<T> eventType) {
+		this.eventType = eventType;
+		duplicationStrategies.add(
+				// At minimum make sure we do not register the same exact listener class multiple times.
+				new DuplicationStrategy() {
+					@Override
+					public boolean areMatch(Object listener, Object original) {
+						return listener.getClass().equals( original.getClass() );
+					}
+
+					@Override
+					public Action getAction() {
+						return Action.ERROR;
+					}
+				}
+		);
+	}
+
+	@Override
+	public EventType<T> getEventType() {
+		return eventType;
+	}
+
+	@Override
+	public boolean isEmpty() {
+		return count() <= 0;
+	}
+
+	@Override
+	public int count() {
+		return listeners == null ? 0 : listeners.size();
+	}
+
+	@Override
+	public void clear() {
+		if ( duplicationStrategies != null ) {
+			duplicationStrategies.clear();
+		}
+		if ( listeners != null ) {
+			listeners.clear();
+		}
+	}
+
+	@Override
+	public void addDuplicationStrategy(DuplicationStrategy strategy) {
+		duplicationStrategies.add( strategy );
+	}
+
+	public Iterable<T> listeners() {
+		return listeners == null ? Collections.<T>emptyList() : listeners;
+	}
+
+	@Override
+	public void appendListeners(T... listeners) {
+		for ( T listener : listeners ) {
+			appendListener( listener );
+		}
+	}
+
+	@Override
+	public void appendListener(T listener) {
+		if ( listenerShouldGetAdded( listener ) ) {
+			internalAppend( listener );
+		}
+	}
+
+	@Override
+	public void prependListeners(T... listeners) {
+		for ( T listener : listeners ) {
+			prependListener( listener );
+		}
+	}
+
+	@Override
+	public void prependListener(T listener) {
+		if ( listenerShouldGetAdded( listener ) ) {
+			internalPrepend( listener );
+		}
+	}
+
+	private boolean listenerShouldGetAdded(T listener) {
+		if ( listeners == null ) {
+			listeners = new ArrayList<T>();
+			return true;
+			// no need to do de-dup checks
+		}
+
+		boolean doAdd = true;
+		strategy_loop: for ( DuplicationStrategy strategy : duplicationStrategies ) {
+			final ListIterator<T> itr = listeners.listIterator();
+			while ( itr.hasNext() ) {
+				final T existingListener = itr.next();
+				if ( strategy.areMatch( listener,  existingListener ) ) {
+					switch ( strategy.getAction() ) {
+						// todo : add debug logging of what happens here...
+						case ERROR: {
+							throw new EventListenerRegistrationException( "Duplicate event listener found" );
+						}
+						case KEEP_ORIGINAL: {
+							doAdd = false;
+							break strategy_loop;
+						}
+						case REPLACE_ORIGINAL: {
+							itr.set( listener );
+							doAdd = false;
+							break strategy_loop;
+						}
+					}
+				}
+			}
+		}
+		return doAdd;
+	}
+
+	private void internalPrepend(T listener) {
+		checkAgainstBaseInterface( listener );
+		listeners.add( 0, listener );
+	}
+
+	private void checkAgainstBaseInterface(T listener) {
+		if ( !eventType.baseListenerInterface().isInstance( listener ) ) {
+			throw new EventListenerRegistrationException(
+					"Listener did not implement expected interface [" + eventType.baseListenerInterface().getName() + "]"
+			);
+		}
+	}
+
+	private void internalAppend(T listener) {
+		checkAgainstBaseInterface( listener );
+		listeners.add( listener );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerRegistryImpl.java
new file mode 100644
index 0000000000..d33cc531bc
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerRegistryImpl.java
@@ -0,0 +1,422 @@
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
+package org.hibernate.service.event.internal;
+
+import java.lang.reflect.Array;
+import java.util.Collections;
+import java.util.HashMap;
+import java.util.Map;
+
+import org.hibernate.HibernateException;
+import org.hibernate.event.EventType;
+import org.hibernate.event.def.DefaultAutoFlushEventListener;
+import org.hibernate.event.def.DefaultDeleteEventListener;
+import org.hibernate.event.def.DefaultDirtyCheckEventListener;
+import org.hibernate.event.def.DefaultEvictEventListener;
+import org.hibernate.event.def.DefaultFlushEntityEventListener;
+import org.hibernate.event.def.DefaultFlushEventListener;
+import org.hibernate.event.def.DefaultInitializeCollectionEventListener;
+import org.hibernate.event.def.DefaultLoadEventListener;
+import org.hibernate.event.def.DefaultLockEventListener;
+import org.hibernate.event.def.DefaultMergeEventListener;
+import org.hibernate.event.def.DefaultPersistEventListener;
+import org.hibernate.event.def.DefaultPersistOnFlushEventListener;
+import org.hibernate.event.def.DefaultPostLoadEventListener;
+import org.hibernate.event.def.DefaultPreLoadEventListener;
+import org.hibernate.event.def.DefaultRefreshEventListener;
+import org.hibernate.event.def.DefaultReplicateEventListener;
+import org.hibernate.event.def.DefaultSaveEventListener;
+import org.hibernate.event.def.DefaultSaveOrUpdateEventListener;
+import org.hibernate.event.def.DefaultUpdateEventListener;
+import org.hibernate.service.event.spi.DuplicationStrategy;
+import org.hibernate.service.event.spi.EventListenerRegistrationException;
+import org.hibernate.service.event.spi.EventListenerRegistry;
+
+import static org.hibernate.event.EventType.AUTO_FLUSH;
+import static org.hibernate.event.EventType.DELETE;
+import static org.hibernate.event.EventType.DIRTY_CHECK;
+import static org.hibernate.event.EventType.EVICT;
+import static org.hibernate.event.EventType.FLUSH;
+import static org.hibernate.event.EventType.FLUSH_ENTITY;
+import static org.hibernate.event.EventType.INIT_COLLECTION;
+import static org.hibernate.event.EventType.LOAD;
+import static org.hibernate.event.EventType.LOCK;
+import static org.hibernate.event.EventType.MERGE;
+import static org.hibernate.event.EventType.PERSIST;
+import static org.hibernate.event.EventType.PERSIST_ONFLUSH;
+import static org.hibernate.event.EventType.POST_COLLECTION_RECREATE;
+import static org.hibernate.event.EventType.POST_COLLECTION_REMOVE;
+import static org.hibernate.event.EventType.POST_COLLECTION_UPDATE;
+import static org.hibernate.event.EventType.POST_COMMIT_DELETE;
+import static org.hibernate.event.EventType.POST_COMMIT_INSERT;
+import static org.hibernate.event.EventType.POST_COMMIT_UPDATE;
+import static org.hibernate.event.EventType.POST_DELETE;
+import static org.hibernate.event.EventType.POST_INSERT;
+import static org.hibernate.event.EventType.POST_LOAD;
+import static org.hibernate.event.EventType.POST_UPDATE;
+import static org.hibernate.event.EventType.PRE_COLLECTION_RECREATE;
+import static org.hibernate.event.EventType.PRE_COLLECTION_REMOVE;
+import static org.hibernate.event.EventType.PRE_COLLECTION_UPDATE;
+import static org.hibernate.event.EventType.PRE_DELETE;
+import static org.hibernate.event.EventType.PRE_INSERT;
+import static org.hibernate.event.EventType.PRE_LOAD;
+import static org.hibernate.event.EventType.PRE_UPDATE;
+import static org.hibernate.event.EventType.REFRESH;
+import static org.hibernate.event.EventType.REPLICATE;
+import static org.hibernate.event.EventType.SAVE;
+import static org.hibernate.event.EventType.SAVE_UPDATE;
+import static org.hibernate.event.EventType.UPDATE;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EventListenerRegistryImpl implements EventListenerRegistry {
+	private Map<Class,Object> listenerClassToInstanceMap = new HashMap<Class, Object>();
+
+	private Map<EventType,EventListenerGroupImpl> registeredEventListenersMap = prepareListenerMap();
+
+	@SuppressWarnings({ "unchecked" })
+	public <T> EventListenerGroupImpl<T> getEventListenerGroup(EventType<T> eventType) {
+		EventListenerGroupImpl<T> listeners = registeredEventListenersMap.get( eventType );
+		if ( listeners == null ) {
+			throw new HibernateException( "Unable to find listeners for type [" + eventType.eventName() + "]" );
+		}
+		return listeners;
+	}
+
+	@Override
+	public void addDuplicationStrategy(DuplicationStrategy strategy) {
+		for ( EventListenerGroupImpl group : registeredEventListenersMap.values() ) {
+			group.addDuplicationStrategy( strategy );
+		}
+	}
+
+	@Override
+	public <T> void setListeners(EventType<T> type, Class<T>... listenerClasses) {
+		setListeners( type, resolveListenerInstances( type, listenerClasses ) );
+	}
+
+	@SuppressWarnings( {"unchecked"})
+	private <T> T[] resolveListenerInstances(EventType<T> type, Class<T>... listenerClasses) {
+		T[] listeners = (T[]) Array.newInstance( type.baseListenerInterface(), listenerClasses.length );
+		for ( int i = 0; i < listenerClasses.length; i++ ) {
+			listeners[i] = resolveListenerInstance( listenerClasses[i] );
+		}
+		return listeners;
+	}
+
+	@SuppressWarnings( {"unchecked"})
+	private <T> T resolveListenerInstance(Class<T> listenerClass) {
+		T listenerInstance = (T) listenerClassToInstanceMap.get( listenerClass );
+		if ( listenerInstance == null ) {
+			listenerInstance = instantiateListener( listenerClass );
+			listenerClassToInstanceMap.put( listenerClass, listenerInstance );
+		}
+		return listenerInstance;
+	}
+
+	private <T> T instantiateListener(Class<T> listenerClass) {
+		try {
+			return listenerClass.newInstance();
+		}
+		catch ( Exception e ) {
+			throw new EventListenerRegistrationException(
+					"Unable to instantiate specified event listener class: " + listenerClass.getName(),
+					e
+			);
+		}
+	}
+
+	@Override
+	public <T> void setListeners(EventType<T> type, T... listeners) {
+		EventListenerGroupImpl<T> registeredListeners = getEventListenerGroup( type );
+		registeredListeners.clear();
+		if ( listeners != null ) {
+			for ( int i = 0, max = listeners.length; i < max; i++ ) {
+				registeredListeners.appendListener( listeners[i] );
+			}
+		}
+	}
+
+	@Override
+	public <T> void appendListeners(EventType<T> type, Class<T>... listenerClasses) {
+		appendListeners( type, resolveListenerInstances( type, listenerClasses ) );
+	}
+
+	@Override
+	public <T> void appendListeners(EventType<T> type, T... listeners) {
+		getEventListenerGroup( type ).appendListeners( listeners );
+	}
+
+	@Override
+	public <T> void prependListeners(EventType<T> type, Class<T>... listenerClasses) {
+		prependListeners( type, resolveListenerInstances( type, listenerClasses ) );
+	}
+
+	@Override
+	public <T> void prependListeners(EventType<T> type, T... listeners) {
+		getEventListenerGroup( type ).prependListeners( listeners );
+	}
+
+	private static Map<EventType,EventListenerGroupImpl> prepareListenerMap() {
+		final Map<EventType,EventListenerGroupImpl> workMap = new HashMap<EventType, EventListenerGroupImpl>();
+
+		// auto-flush listeners
+		prepareListeners(
+				AUTO_FLUSH,
+				new DefaultAutoFlushEventListener(),
+				workMap
+		);
+
+		// create listeners
+		prepareListeners(
+				PERSIST,
+				new DefaultPersistEventListener(),
+				workMap
+		);
+
+		// create-onflush listeners
+		prepareListeners(
+				PERSIST_ONFLUSH,
+				new DefaultPersistOnFlushEventListener(),
+				workMap
+		);
+
+		// delete listeners
+		prepareListeners(
+				DELETE,
+				new DefaultDeleteEventListener(),
+				workMap
+		);
+
+		// dirty-check listeners
+		prepareListeners(
+				DIRTY_CHECK,
+				new DefaultDirtyCheckEventListener(),
+				workMap
+		);
+
+		// evict listeners
+		prepareListeners(
+				EVICT,
+				new DefaultEvictEventListener(),
+				workMap
+		);
+
+		// flush listeners
+		prepareListeners(
+				FLUSH,
+				new DefaultFlushEventListener(),
+				workMap
+		);
+
+		// flush-entity listeners
+		prepareListeners(
+				FLUSH_ENTITY,
+				new DefaultFlushEntityEventListener(),
+				workMap
+		);
+
+		// load listeners
+		prepareListeners(
+				LOAD,
+				new DefaultLoadEventListener(),
+				workMap
+		);
+
+		// load-collection listeners
+		prepareListeners(
+				INIT_COLLECTION,
+				new DefaultInitializeCollectionEventListener(),
+				workMap
+		);
+
+		// lock listeners
+		prepareListeners(
+				LOCK,
+				new DefaultLockEventListener(),
+				workMap
+		);
+
+		// merge listeners
+		prepareListeners(
+				MERGE,
+				new DefaultMergeEventListener(),
+				workMap
+		);
+
+		// pre-collection-recreate listeners
+		prepareListeners(
+				PRE_COLLECTION_RECREATE,
+				workMap
+		);
+
+		// pre-collection-remove listeners
+		prepareListeners(
+				PRE_COLLECTION_REMOVE,
+				workMap
+		);
+
+		// pre-collection-update listeners
+		prepareListeners(
+				PRE_COLLECTION_UPDATE,
+				workMap
+		);
+
+		// pre-delete listeners
+		prepareListeners(
+				PRE_DELETE,
+				workMap
+		);
+
+		// pre-insert listeners
+		prepareListeners(
+				PRE_INSERT,
+				workMap
+		);
+
+		// pre-load listeners
+		prepareListeners(
+				PRE_LOAD,
+				new DefaultPreLoadEventListener(),
+				workMap
+		);
+
+		// pre-update listeners
+		prepareListeners(
+				PRE_UPDATE,
+				workMap
+		);
+
+		// post-collection-recreate listeners
+		prepareListeners(
+				POST_COLLECTION_RECREATE,
+				workMap
+		);
+
+		// post-collection-remove listeners
+		prepareListeners(
+				POST_COLLECTION_REMOVE,
+				workMap
+		);
+
+		// post-collection-update listeners
+		prepareListeners(
+				POST_COLLECTION_UPDATE,
+				workMap
+		);
+
+		// post-commit-delete listeners
+		prepareListeners(
+				POST_COMMIT_DELETE,
+				workMap
+		);
+
+		// post-commit-insert listeners
+		prepareListeners(
+				POST_COMMIT_INSERT,
+				workMap
+		);
+
+		// post-commit-update listeners
+		prepareListeners(
+				POST_COMMIT_UPDATE,
+				workMap
+		);
+
+		// post-delete listeners
+		prepareListeners(
+				POST_DELETE,
+				workMap
+		);
+
+		// post-insert listeners
+		prepareListeners(
+				POST_INSERT,
+				workMap
+		);
+
+		// post-load listeners
+		prepareListeners(
+				POST_LOAD,
+				new DefaultPostLoadEventListener(),
+				workMap
+		);
+
+		// post-update listeners
+		prepareListeners(
+				POST_UPDATE,
+				workMap
+		);
+
+		// update listeners
+		prepareListeners(
+				UPDATE,
+				new DefaultUpdateEventListener(),
+				workMap
+		);
+
+		// refresh listeners
+		prepareListeners(
+				REFRESH,
+				new DefaultRefreshEventListener(),
+				workMap
+		);
+
+		// replicate listeners
+		prepareListeners(
+				REPLICATE,
+				new DefaultReplicateEventListener(),
+				workMap
+		);
+
+		// save listeners
+		prepareListeners(
+				SAVE,
+				new DefaultSaveEventListener(),
+				workMap
+		);
+
+		// save-update listeners
+		prepareListeners(
+				SAVE_UPDATE,
+				new DefaultSaveOrUpdateEventListener(),
+				workMap
+		);
+
+		return Collections.unmodifiableMap( workMap );
+	}
+
+	private static <T> void prepareListeners(EventType<T> type, Map<EventType,EventListenerGroupImpl> map) {
+		prepareListeners( type, null, map  );
+	}
+
+	private static <T> void prepareListeners(EventType<T> type, T defaultListener, Map<EventType,EventListenerGroupImpl> map) {
+		final EventListenerGroupImpl<T> listeners = new EventListenerGroupImpl<T>( type );
+		if ( defaultListener != null ) {
+			listeners.appendListener( defaultListener );
+		}
+		map.put( type, listeners  );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerServiceInitiator.java
new file mode 100644
index 0000000000..8c8881d351
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerServiceInitiator.java
@@ -0,0 +1,49 @@
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
+package org.hibernate.service.event.internal;
+
+import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.service.spi.ServiceInitiator;
+import org.hibernate.service.spi.ServiceRegistry;
+
+import java.util.Map;
+
+/**
+ * Service initiator for {@link EventListenerRegistry}
+ *
+ * @author Steve Ebersole
+ */
+public class EventListenerServiceInitiator implements ServiceInitiator<EventListenerRegistry> {
+	public static final EventListenerServiceInitiator INSTANCE = new EventListenerServiceInitiator();
+
+	@Override
+	public Class<EventListenerRegistry> getServiceInitiated() {
+		return EventListenerRegistry.class;
+	}
+
+	@Override
+	public EventListenerRegistry initiateService(Map configurationValues, ServiceRegistry registry) {
+		return new EventListenerRegistryImpl();
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/internal/TemporaryListenerHelper.java b/hibernate-core/src/main/java/org/hibernate/service/event/internal/TemporaryListenerHelper.java
new file mode 100644
index 0000000000..bed2e92d84
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/event/internal/TemporaryListenerHelper.java
@@ -0,0 +1,47 @@
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
+package org.hibernate.service.event.internal;
+
+import org.hibernate.event.EventType;
+import org.hibernate.service.event.spi.EventListenerRegistry;
+
+/**
+ * Helper for handling various aspects of event listeners.  Temporary because generally speaking this is legacy stuff
+ * that should change as we move forward
+ *
+ * @author Steve Ebersole
+ */
+public class TemporaryListenerHelper {
+	public static interface ListenerProcessor {
+		public void processListener(Object listener);
+	}
+
+	public static void processListeners(EventListenerRegistry registryRegistry, ListenerProcessor processer) {
+		for ( EventType eventType : EventType.values() ) {
+			for ( Object listener : registryRegistry.getEventListenerGroup( eventType ).listeners() ) {
+				processer.processListener( listener );
+			}
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/spi/DuplicationStrategy.java b/hibernate-core/src/main/java/org/hibernate/service/event/spi/DuplicationStrategy.java
new file mode 100644
index 0000000000..18da5d3f35
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/event/spi/DuplicationStrategy.java
@@ -0,0 +1,58 @@
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
+package org.hibernate.service.event.spi;
+
+/**
+ * Defines listener duplication checking strategy, both in terms of when a duplication is detected (see
+ * {@link #areMatch}) as well as how to handle a duplication (see {@link #getAction}).
+ *
+ * @author Steve Ebersole
+ */
+public interface DuplicationStrategy {
+	/**
+	 * The enumerated list of actions available on duplication match
+	 */
+	public static enum Action {
+		ERROR,
+		KEEP_ORIGINAL,
+		REPLACE_ORIGINAL
+	}
+
+	/**
+	 * Are the two listener instances considered a duplication?
+	 *
+	 * @param listener The listener we are currently trying to register
+	 * @param original An already registered listener
+	 *
+	 * @return {@literal true} if the two instances are considered a duplication; {@literal false} otherwise
+	 */
+	public boolean areMatch(Object listener, Object original);
+
+	/**
+	 * How should a duplication be handled?
+	 *
+	 * @return The strategy for handling duplication
+	 */
+	public Action getAction();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerGroup.java b/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerGroup.java
new file mode 100644
index 0000000000..f379bc2324
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerGroup.java
@@ -0,0 +1,74 @@
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
+package org.hibernate.service.event.spi;
+
+import java.io.Serializable;
+
+import org.hibernate.event.EventType;
+
+/**
+ * Contract for a groups of events listeners for a particular event type.
+ *
+ * @author Steve Ebersole
+ */
+public interface EventListenerGroup<T> extends Serializable {
+
+	/**
+	 * Retrieve the event type associated with this groups of listeners.
+	 *
+	 * @return The event type.
+	 */
+	public EventType<T> getEventType();
+
+	/**
+	 * Are there no listeners registered?
+	 *
+	 * @return {@literal true} if no listeners are registered; {@literal false} otherwise.
+	 */
+	public boolean isEmpty();
+
+	public int count();
+
+	public Iterable<T> listeners();
+
+	/**
+	 * Mechanism to more finely control the notion of duplicates.
+	 * <p/>
+	 * For example, say you are registering listeners for an extension library.  This extension library
+	 * could define a "marker interface" which indicates listeners related to it and register a strategy
+	 * that checks against that marker interface.
+	 *
+	 * @param strategy The duplication strategy
+	 */
+	public void addDuplicationStrategy(DuplicationStrategy strategy);
+
+	public void appendListener(T listener);
+	public void appendListeners(T... listeners);
+
+	public void prependListener(T listener);
+	public void prependListeners(T... listeners);
+
+	public void clear();
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerRegistrationException.java b/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerRegistrationException.java
new file mode 100644
index 0000000000..922dff24d0
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerRegistrationException.java
@@ -0,0 +1,41 @@
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
+package org.hibernate.service.event.spi;
+
+import org.hibernate.HibernateException;
+
+/**
+ * Indicates a problem registering an event listener.
+ * 
+ * @author Steve Ebersole
+ */
+public class EventListenerRegistrationException extends HibernateException {
+	public EventListenerRegistrationException(String s) {
+		super( s );
+	}
+
+	public EventListenerRegistrationException(String string, Throwable root) {
+		super( string, root );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerRegistry.java b/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerRegistry.java
new file mode 100644
index 0000000000..3e506f4787
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerRegistry.java
@@ -0,0 +1,51 @@
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
+package org.hibernate.service.event.spi;
+
+import java.io.Serializable;
+
+import org.hibernate.event.EventType;
+import org.hibernate.service.spi.Service;
+
+/**
+ * Service for accessing each {@link EventListenerGroup} by {@link EventType}, as well as convenience
+ * methods for managing the listeners registered in each {@link EventListenerGroup}.
+ *
+ * @author Steve Ebersole
+ */
+public interface EventListenerRegistry extends Service, Serializable {
+	// todo : rename this to getEventListenerGroup
+	public <T> EventListenerGroup<T> getEventListenerGroup(EventType<T> eventType);
+
+	public void addDuplicationStrategy(DuplicationStrategy strategy);
+
+	public <T> void setListeners(EventType<T> type, Class<T>... listeners);
+	public <T> void setListeners(EventType<T> type, T... listeners);
+
+	public <T> void appendListeners(EventType<T> type, Class<T>... listeners);
+	public <T> void appendListeners(EventType<T> type, T... listeners);
+
+	public <T> void prependListeners(EventType<T> type, Class<T>... listeners);
+	public <T> void prependListeners(EventType<T> type, T... listeners);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
index ae9b1ef769..d316ae944f 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
@@ -1,673 +1,686 @@
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
 package org.hibernate.tuple.entity;
 
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
+import org.hibernate.event.EventType;
 import org.hibernate.event.PersistEvent;
+import org.hibernate.event.PersistEventListener;
 import org.hibernate.id.Assigned;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.property.Getter;
 import org.hibernate.property.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
+import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.tuple.Instantiator;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.VersionProperty;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 
 /**
  * Support for tuplizers relating to entities.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public abstract class AbstractEntityTuplizer implements EntityTuplizer {
 
-    private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
-                                                                       AbstractEntityTuplizer.class.getName());
+    private static final HibernateLogger LOG = Logger.getMessageLogger(
+			HibernateLogger.class,
+			AbstractEntityTuplizer.class.getName()
+	);
 
 	//TODO: currently keeps Getters and Setters (instead of PropertyAccessors) because of the way getGetter() and getSetter() are implemented currently; yuck!
 
 	private final EntityMetamodel entityMetamodel;
 
 	private final Getter idGetter;
 	private final Setter idSetter;
 
 	protected final Getter[] getters;
 	protected final Setter[] setters;
 	protected final int propertySpan;
 	protected final boolean hasCustomAccessors;
 	private final Instantiator instantiator;
 	private final ProxyFactory proxyFactory;
 	private final CompositeType identifierMapperType;
 
 	public Type getIdentifierMapperType() {
 		return identifierMapperType;
 	}
 
 	/**
 	 * Build an appropriate Getter for the given property.
 	 *
 	 * @param mappedProperty The property to be accessed via the built Getter.
 	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
 	 * @return An appropriate Getter instance.
 	 */
 	protected abstract Getter buildPropertyGetter(Property mappedProperty, PersistentClass mappedEntity);
 
 	/**
 	 * Build an appropriate Setter for the given property.
 	 *
 	 * @param mappedProperty The property to be accessed via the built Setter.
 	 * @param mappedEntity The entity information regarding the mapped entity owning this property.
 	 * @return An appropriate Setter instance.
 	 */
 	protected abstract Setter buildPropertySetter(Property mappedProperty, PersistentClass mappedEntity);
 
 	/**
 	 * Build an appropriate Instantiator for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @return An appropriate Instantiator instance.
 	 */
 	protected abstract Instantiator buildInstantiator(PersistentClass mappingInfo);
 
 	/**
 	 * Build an appropriate ProxyFactory for the given mapped entity.
 	 *
 	 * @param mappingInfo The mapping information regarding the mapped entity.
 	 * @param idGetter The constructed Getter relating to the entity's id property.
 	 * @param idSetter The constructed Setter relating to the entity's id property.
 	 * @return An appropriate ProxyFactory instance.
 	 */
 	protected abstract ProxyFactory buildProxyFactory(PersistentClass mappingInfo, Getter idGetter, Setter idSetter);
 
 	/**
 	 * Constructs a new AbstractEntityTuplizer instance.
 	 *
 	 * @param entityMetamodel The "interpreted" information relating to the mapped entity.
 	 * @param mappingInfo The parsed "raw" mapping data relating to the given entity.
 	 */
 	public AbstractEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappingInfo) {
 		this.entityMetamodel = entityMetamodel;
 
 		if ( !entityMetamodel.getIdentifierProperty().isVirtual() ) {
 			idGetter = buildPropertyGetter( mappingInfo.getIdentifierProperty(), mappingInfo );
 			idSetter = buildPropertySetter( mappingInfo.getIdentifierProperty(), mappingInfo );
 		}
 		else {
 			idGetter = null;
 			idSetter = null;
 		}
 
 		propertySpan = entityMetamodel.getPropertySpan();
 
         getters = new Getter[propertySpan];
 		setters = new Setter[propertySpan];
 
 		Iterator itr = mappingInfo.getPropertyClosureIterator();
 		boolean foundCustomAccessor=false;
 		int i=0;
 		while ( itr.hasNext() ) {
 			//TODO: redesign how PropertyAccessors are acquired...
 			Property property = (Property) itr.next();
 			getters[i] = buildPropertyGetter(property, mappingInfo);
 			setters[i] = buildPropertySetter(property, mappingInfo);
 			if ( !property.isBasicPropertyAccessor() ) {
 				foundCustomAccessor = true;
 			}
 			i++;
 		}
 		hasCustomAccessors = foundCustomAccessor;
 
         instantiator = buildInstantiator( mappingInfo );
 
 		if ( entityMetamodel.isLazy() ) {
 			proxyFactory = buildProxyFactory( mappingInfo, idGetter, idSetter );
 			if (proxyFactory == null) {
 				entityMetamodel.setLazy( false );
 			}
 		}
 		else {
 			proxyFactory = null;
 		}
 
 		Component mapper = mappingInfo.getIdentifierMapper();
 		if ( mapper == null ) {
 			identifierMapperType = null;
 			mappedIdentifierValueMarshaller = null;
 		}
 		else {
 			identifierMapperType = (CompositeType) mapper.getType();
 			mappedIdentifierValueMarshaller = buildMappedIdentifierValueMarshaller(
 					(ComponentType) entityMetamodel.getIdentifierProperty().getType(),
 					(ComponentType) identifierMapperType
 			);
 		}
 	}
 
 	/** Retreives the defined entity-name for the tuplized entity.
 	 *
 	 * @return The entity-name.
 	 */
 	protected String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	/**
 	 * Retrieves the defined entity-names for any subclasses defined for this
 	 * entity.
 	 *
 	 * @return Any subclass entity-names.
 	 */
 	protected Set getSubclassEntityNames() {
 		return entityMetamodel.getSubclassEntityNames();
 	}
 
 	public Serializable getIdentifier(Object entity) throws HibernateException {
 		return getIdentifier( entity, null );
 	}
 
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		final Object id;
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			id = entity;
 		}
 		else {
 			if ( idGetter == null ) {
 				if (identifierMapperType==null) {
 					throw new HibernateException( "The class has no identifier property: " + getEntityName() );
 				}
 				else {
 					id = mappedIdentifierValueMarshaller.getIdentifier( entity, getEntityMode(), session );
 				}
 			}
 			else {
                 id = idGetter.get( entity );
             }
         }
 
 		try {
 			return (Serializable) id;
 		}
 		catch ( ClassCastException cce ) {
 			StringBuffer msg = new StringBuffer( "Identifier classes must be serializable. " );
 			if ( id != null ) {
 				msg.append( id.getClass().getName() ).append( " is not serializable. " );
 			}
 			if ( cce.getMessage() != null ) {
 				msg.append( cce.getMessage() );
 			}
 			throw new ClassCastException( msg.toString() );
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setIdentifier(Object entity, Serializable id) throws HibernateException {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		setIdentifier( entity, id, null );
 	}
 
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			if ( entity != id ) {
 				CompositeType copier = (CompositeType) entityMetamodel.getIdentifierProperty().getType();
 				copier.setPropertyValues( entity, copier.getPropertyValues( id, getEntityMode() ), getEntityMode() );
 			}
 		}
 		else if ( idSetter != null ) {
 			idSetter.set( entity, id, getFactory() );
 		}
 		else if ( identifierMapperType != null ) {
 			mappedIdentifierValueMarshaller.setIdentifier( entity, id, getEntityMode(), session );
 		}
 	}
 
 	private static interface MappedIdentifierValueMarshaller {
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session);
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session);
 	}
 
 	private final MappedIdentifierValueMarshaller mappedIdentifierValueMarshaller;
 
 	private static MappedIdentifierValueMarshaller buildMappedIdentifierValueMarshaller(
 			ComponentType mappedIdClassComponentType,
 			ComponentType virtualIdComponent) {
 		// so basically at this point we know we have a "mapped" composite identifier
 		// which is an awful way to say that the identifier is represented differently
 		// in the entity and in the identifier value.  The incoming value should
 		// be an instance of the mapped identifier class (@IdClass) while the incoming entity
 		// should be an instance of the entity class as defined by metamodel.
 		//
 		// However, even within that we have 2 potential scenarios:
 		//		1) @IdClass types and entity @Id property types match
 		//			- return a NormalMappedIdentifierValueMarshaller
 		//		2) They do not match
 		//			- return a IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller
 		boolean wereAllEquivalent = true;
 		// the sizes being off is a much bigger problem that should have been caught already...
 		for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
 			if ( virtualIdComponent.getSubtypes()[i].isEntityType()
 					&& ! mappedIdClassComponentType.getSubtypes()[i].isEntityType() ) {
 				wereAllEquivalent = false;
 				break;
 			}
 		}
 
 		return wereAllEquivalent
 				? (MappedIdentifierValueMarshaller) new NormalMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType )
 				: (MappedIdentifierValueMarshaller) new IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller( virtualIdComponent, mappedIdClassComponentType );
 	}
 
 	private static class NormalMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
 		private final ComponentType virtualIdComponent;
 		private final ComponentType mappedIdentifierType;
 
 		private NormalMappedIdentifierValueMarshaller(ComponentType virtualIdComponent, ComponentType mappedIdentifierType) {
 			this.virtualIdComponent = virtualIdComponent;
 			this.mappedIdentifierType = mappedIdentifierType;
 		}
 
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
 			Object id = mappedIdentifierType.instantiate( entityMode );
 			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
 			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
 			return id;
 		}
 
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
 			virtualIdComponent.setPropertyValues(
 					entity,
 					mappedIdentifierType.getPropertyValues( id, session ),
 					entityMode
 			);
 		}
 	}
 
 	private static class IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller implements MappedIdentifierValueMarshaller {
 		private final ComponentType virtualIdComponent;
 		private final ComponentType mappedIdentifierType;
 
 		private IncrediblySillyJpaMapsIdMappedIdentifierValueMarshaller(ComponentType virtualIdComponent, ComponentType mappedIdentifierType) {
 			this.virtualIdComponent = virtualIdComponent;
 			this.mappedIdentifierType = mappedIdentifierType;
 		}
 
 		public Object getIdentifier(Object entity, EntityMode entityMode, SessionImplementor session) {
 			final Object id = mappedIdentifierType.instantiate( entityMode );
 			final Object[] propertyValues = virtualIdComponent.getPropertyValues( entity, entityMode );
 			final Type[] subTypes = virtualIdComponent.getSubtypes();
 			final Type[] copierSubTypes = mappedIdentifierType.getSubtypes();
 			final int length = subTypes.length;
 			for ( int i = 0 ; i < length; i++ ) {
 				if ( propertyValues[i] == null ) {
 					throw new HibernateException( "No part of a composite identifier may be null" );
 				}
 				//JPA 2 @MapsId + @IdClass points to the pk of the entity
 				if ( subTypes[i].isAssociationType() && ! copierSubTypes[i].isAssociationType() ) {
 					// we need a session to handle this use case
 					if ( session == null ) {
 						throw new AssertionError(
 								"Deprecated version of getIdentifier (no session) was used but session was required"
 						);
 					}
 					final Object subId;
 					if ( HibernateProxy.class.isInstance( propertyValues[i] ) ) {
 						subId = ( (HibernateProxy) propertyValues[i] ).getHibernateLazyInitializer().getIdentifier();
 					}
 					else {
 						EntityEntry pcEntry = session.getPersistenceContext().getEntry( propertyValues[i] );
 						if ( pcEntry != null ) {
 							subId = pcEntry.getId();
 						}
 						else {
-                            LOG.debugf("Performing implicit derived identity cascade");
+                            LOG.debugf( "Performing implicit derived identity cascade" );
 							final PersistEvent event = new PersistEvent( null, propertyValues[i], (EventSource) session );
-							for ( int x = 0; x < session.getListeners().getPersistEventListeners().length; x++ ) {
-								session.getListeners().getPersistEventListeners()[x].onPersist( event );
-
+							for ( PersistEventListener listener : persistEventListeners( session ) ) {
+								listener.onPersist( event );
 							}
 							pcEntry = session.getPersistenceContext().getEntry( propertyValues[i] );
 							if ( pcEntry == null || pcEntry.getId() == null ) {
 								throw new HibernateException( "Unable to process implicit derived identity cascade" );
 							}
 							else {
 								subId = pcEntry.getId();
 							}
 						}
 					}
 					propertyValues[i] = subId;
 				}
 			}
 			mappedIdentifierType.setPropertyValues( id, propertyValues, entityMode );
 			return id;
 		}
 
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode, SessionImplementor session) {
 			final Object[] extractedValues = mappedIdentifierType.getPropertyValues( id, entityMode );
 			final Object[] injectionValues = new Object[ extractedValues.length ];
 			for ( int i = 0; i < virtualIdComponent.getSubtypes().length; i++ ) {
 				final Type virtualPropertyType = virtualIdComponent.getSubtypes()[i];
 				final Type idClassPropertyType = mappedIdentifierType.getSubtypes()[i];
 				if ( virtualPropertyType.isEntityType() && ! idClassPropertyType.isEntityType() ) {
 					if ( session == null ) {
 						throw new AssertionError(
 								"Deprecated version of getIdentifier (no session) was used but session was required"
 						);
 					}
 					final String associatedEntityName = ( (EntityType) virtualPropertyType ).getAssociatedEntityName();
 					final EntityKey entityKey = session.generateEntityKey(
 							(Serializable) extractedValues[i],
 							session.getFactory().getEntityPersister( associatedEntityName )
 					);
 					// it is conceivable there is a proxy, so check that first
 					Object association = session.getPersistenceContext().getProxy( entityKey );
 					if ( association == null ) {
 						// otherwise look for an initialized version
 						association = session.getPersistenceContext().getEntity( entityKey );
 					}
 					injectionValues[i] = association;
 				}
 				else {
 					injectionValues[i] = extractedValues[i];
 				}
 			}
 			virtualIdComponent.setPropertyValues( entity, injectionValues, session.getEntityMode() );
 		}
 	}
 
+	private static Iterable<PersistEventListener> persistEventListeners(SessionImplementor session) {
+		return session
+				.getFactory()
+				.getServiceRegistry()
+				.getService( EventListenerRegistry.class )
+				.getEventListenerGroup( EventType.PERSIST )
+				.listeners();
+	}
+
 	/**
 	 * {@inheritDoc}
 	 */
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion) {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		resetIdentifier( entity, currentId, currentVersion, null );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void resetIdentifier(
 			Object entity,
 			Serializable currentId,
 			Object currentVersion,
 			SessionImplementor session) {
 		if ( entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned ) {
 		}
 		else {
 			//reset the id
 			Serializable result = entityMetamodel.getIdentifierProperty()
 					.getUnsavedValue()
 					.getDefaultValue( currentId );
 			setIdentifier( entity, result, session );
 			//reset the version
 			VersionProperty versionProperty = entityMetamodel.getVersionProperty();
 			if ( entityMetamodel.isVersioned() ) {
 				setPropertyValue(
 				        entity,
 				        entityMetamodel.getVersionPropertyIndex(),
 						versionProperty.getUnsavedValue().getDefaultValue( currentVersion )
 				);
 			}
 		}
 	}
 
 	public Object getVersion(Object entity) throws HibernateException {
 		if ( !entityMetamodel.isVersioned() ) return null;
 		return getters[ entityMetamodel.getVersionPropertyIndex() ].get( entity );
 	}
 
 	protected boolean shouldGetAllProperties(Object entity) {
 		return !hasUninitializedLazyProperties( entity );
 	}
 
 	public Object[] getPropertyValues(Object entity) throws HibernateException {
 		boolean getAll = shouldGetAllProperties( entity );
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
 			StandardProperty property = entityMetamodel.getProperties()[j];
 			if ( getAll || !property.isLazy() ) {
 				result[j] = getters[j].get( entity );
 			}
 			else {
 				result[j] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 			}
 		}
 		return result;
 	}
 
 	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
 	throws HibernateException {
 		final int span = entityMetamodel.getPropertySpan();
 		final Object[] result = new Object[span];
 
 		for ( int j = 0; j < span; j++ ) {
 			result[j] = getters[j].getForInsert( entity, mergeMap, session );
 		}
 		return result;
 	}
 
 	public Object getPropertyValue(Object entity, int i) throws HibernateException {
 		return getters[i].get( entity );
 	}
 
 	public Object getPropertyValue(Object entity, String propertyPath) throws HibernateException {
 		int loc = propertyPath.indexOf('.');
 		String basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		//final int index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		Integer index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		if (index == null) {
 			propertyPath = "_identifierMapper." + propertyPath;
 			loc = propertyPath.indexOf('.');
 			basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		}
 		index = entityMetamodel.getPropertyIndexOrNull( basePropertyName );
 		final Object baseValue = getPropertyValue( entity, index.intValue() );
 		if ( loc > 0 ) {
 			if ( baseValue == null ) {
 				return null;
 			}
 			return getComponentValue(
 					(ComponentType) entityMetamodel.getPropertyTypes()[index.intValue()],
 					baseValue,
 					propertyPath.substring(loc+1)
 			);
 		}
 		else {
 			return baseValue;
 		}
 	}
 
 	/**
 	 * Extract a component property value.
 	 *
 	 * @param type The component property types.
 	 * @param component The component instance itself.
 	 * @param propertyPath The property path for the property to be extracted.
 	 * @return The property value extracted.
 	 */
 	protected Object getComponentValue(ComponentType type, Object component, String propertyPath) {
 		final int loc = propertyPath.indexOf( '.' );
 		final String basePropertyName = loc > 0
 				? propertyPath.substring( 0, loc )
 				: propertyPath;
 		final int index = findSubPropertyIndex( type, basePropertyName );
 		final Object baseValue = type.getPropertyValue( component, index, getEntityMode() );
 		if ( loc > 0 ) {
 			if ( baseValue == null ) {
 				return null;
 			}
 			return getComponentValue(
 					(ComponentType) type.getSubtypes()[index],
 					baseValue,
 					propertyPath.substring(loc+1)
 			);
 		}
 		else {
 			return baseValue;
 		}
 
 	}
 
 	private int findSubPropertyIndex(ComponentType type, String subPropertyName) {
 		final String[] propertyNames = type.getPropertyNames();
 		for ( int index = 0; index<propertyNames.length; index++ ) {
 			if ( subPropertyName.equals( propertyNames[index] ) ) {
 				return index;
 			}
 		}
 		throw new MappingException( "component property not found: " + subPropertyName );
 	}
 
 	public void setPropertyValues(Object entity, Object[] values) throws HibernateException {
 		boolean setAll = !entityMetamodel.hasLazyProperties();
 
 		for ( int j = 0; j < entityMetamodel.getPropertySpan(); j++ ) {
 			if ( setAll || values[j] != LazyPropertyInitializer.UNFETCHED_PROPERTY ) {
 				setters[j].set( entity, values[j], getFactory() );
 			}
 		}
 	}
 
 	public void setPropertyValue(Object entity, int i, Object value) throws HibernateException {
 		setters[i].set( entity, value, getFactory() );
 	}
 
 	public void setPropertyValue(Object entity, String propertyName, Object value) throws HibernateException {
 		setters[ entityMetamodel.getPropertyIndex( propertyName ) ].set( entity, value, getFactory() );
 	}
 
 	public final Object instantiate(Serializable id) throws HibernateException {
 		// 99% of the time the session is not needed.  Its only needed for certain brain-dead
 		// interpretations of JPA 2 "derived identity" support
 		return instantiate( id, null );
 	}
 
 	public final Object instantiate(Serializable id, SessionImplementor session) {
 		Object result = getInstantiator().instantiate( id );
 		if ( id != null ) {
 			setIdentifier( result, id, session );
 		}
 		return result;
 	}
 
 	public final Object instantiate() throws HibernateException {
 		return instantiate( null, null );
 	}
 
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {}
 
 	public boolean hasUninitializedLazyProperties(Object entity) {
 		// the default is to simply not lazy fetch properties for now...
 		return false;
 	}
 
 	public final boolean isInstance(Object object) {
         return getInstantiator().isInstance( object );
 	}
 
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy();
 	}
 
 	public final Object createProxy(Serializable id, SessionImplementor session)
 	throws HibernateException {
 		return getProxyFactory().getProxy( id, session );
 	}
 
 	public boolean isLifecycleImplementor() {
 		return false;
 	}
 
 	protected final EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	protected final SessionFactoryImplementor getFactory() {
 		return entityMetamodel.getSessionFactory();
 	}
 
 	protected final Instantiator getInstantiator() {
 		return instantiator;
 	}
 
 	protected final ProxyFactory getProxyFactory() {
 		return proxyFactory;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + getEntityMetamodel().getName() + ')';
 	}
 
 	public Getter getIdentifierGetter() {
 		return idGetter;
 	}
 
 	public Getter getVersionGetter() {
 		if ( getEntityMetamodel().isVersioned() ) {
 			return getGetter( getEntityMetamodel().getVersionPropertyIndex() );
 		}
 		return null;
 	}
 
 	public Getter getGetter(int i) {
 		return getters[i];
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/access/xml/XmlAccessTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/access/xml/XmlAccessTest.java
index 4e072098ef..99ddc9d4b7 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/access/xml/XmlAccessTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/access/xml/XmlAccessTest.java
@@ -1,210 +1,204 @@
-//$Id$
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.test.annotations.access.xml;
 
+import javax.persistence.AccessType;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
-import javax.persistence.AccessType;
-import junit.framework.TestCase;
+
 import org.hibernate.EntityMode;
-import org.hibernate.cfg.AnnotationConfiguration;
-import org.hibernate.cfg.Environment;
+import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.property.BasicPropertyAccessor;
 import org.hibernate.property.DirectPropertyAccessor;
-import org.hibernate.service.ServiceRegistry;
-import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.PojoEntityTuplizer;
 
+import org.junit.Assert;
+import org.junit.Test;
+
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+
 
 /**
  * Test verifying that it is possible to configure the access type via xml configuration.
  *
  * @author Hardy Ferentschik
  */
-public class XmlAccessTest extends TestCase {
-
-	private ServiceRegistry serviceRegistry;
-
-	@Override
-    protected void setUp() {
-		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( Environment.getProperties() );
-	}
-
-	@Override
-    protected void tearDown() {
-		if ( serviceRegistry != null ) {
-			ServiceRegistryBuilder.destroy( serviceRegistry );
-		}
-	}
-
+public class XmlAccessTest extends BaseUnitTestCase {
+	@Test
 	public void testAccessOnBasicXmlElement() throws Exception {
 		Class<?> classUnderTest = Tourist.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		List<String> configFiles = Collections.emptyList();
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 
 		// without any xml configuration we have field access
 		assertAccessType( factory, classUnderTest, AccessType.FIELD );
 
 		// now with an additional xml configuration file changing the default access type for Tourist using basic
 		configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Tourist.xml" );
 		factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
+	@Test
 	public void testAccessOnPersistenceUnitDefaultsXmlElement() throws Exception {
 		Class<?> classUnderTest = Tourist.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		List<String> configFiles = Collections.emptyList();
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 
 		// without any xml configuration we have field access
 		assertAccessType( factory, classUnderTest, AccessType.FIELD );
 
 		// now with an additional xml configuration file changing the default access type for Tourist using persitence unit defaults
 		configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Tourist2.xml" );
 		factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
+	@Test
 	public void testAccessOnEntityMappingsXmlElement() throws Exception {
 		Class<?> classUnderTest = Tourist.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		List<String> configFiles = Collections.emptyList();
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 
 		// without any xml configuration we have field access
 		assertAccessType( factory, classUnderTest, AccessType.FIELD );
 
 		// now with an additional xml configuration file changing the default access type for Tourist using default in entity-mappings
 		configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Tourist3.xml" );
 		factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
+	@Test
 	public void testAccessOnEntityXmlElement() throws Exception {
 		Class<?> classUnderTest = Tourist.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		List<String> configFiles = Collections.emptyList();
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 
 		// without any xml configuration we have field access
 		assertAccessType( factory, classUnderTest, AccessType.FIELD );
 
 		// now with an additional xml configuration file changing the default access type for Tourist using entity level config
 		configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Tourist4.xml" );
 		factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
+	@Test
 	public void testAccessOnMappedSuperClassXmlElement() throws Exception {
 		Class<?> classUnderTest = Waiter.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		classes.add( Crew.class );
 		List<String> configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Crew.xml" );
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.FIELD );
 	}
 
+	@Test
 	public void testAccessOnAssociationXmlElement() throws Exception {
 		Class<?> classUnderTest = RentalCar.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		classes.add( Driver.class );
 		List<String> configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/RentalCar.xml" );
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
+	@Test
 	public void testAccessOnEmbeddedXmlElement() throws Exception {
 		Class<?> classUnderTest = Cook.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		classes.add( Knive.class );
 		List<String> configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Cook.xml" );
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
+	@Test
 	public void testAccessOnElementCollectionXmlElement() throws Exception {
 		Class<?> classUnderTest = Boy.class;
 		List<Class<?>> classes = new ArrayList<Class<?>>();
 		classes.add( classUnderTest );
 		List<String> configFiles = new ArrayList<String>();
 		configFiles.add( "org/hibernate/test/annotations/access/xml/Boy.xml" );
 		SessionFactoryImplementor factory = buildSessionFactory( classes, configFiles );
 		assertAccessType( factory, classUnderTest, AccessType.PROPERTY );
 	}
 
 	private SessionFactoryImplementor buildSessionFactory(List<Class<?>> classesUnderTest, List<String> configFiles) {
 		assert classesUnderTest != null;
 		assert configFiles != null;
-		AnnotationConfiguration cfg = new AnnotationConfiguration();
+		Configuration cfg = new Configuration();
 		for ( Class<?> clazz : classesUnderTest ) {
 			cfg.addAnnotatedClass( clazz );
 		}
 		for ( String configFile : configFiles ) {
 			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( configFile );
 			cfg.addInputStream( is );
 		}
-		return ( SessionFactoryImplementor ) cfg.buildSessionFactory( serviceRegistry );
+		return ( SessionFactoryImplementor ) cfg.buildSessionFactory();
 	}
 
 	// uses the first getter of the tupelizer for the assertions
 
 	private void assertAccessType(SessionFactoryImplementor factory, Class<?> classUnderTest, AccessType accessType) {
 		EntityMetamodel metaModel = factory.getEntityPersister( classUnderTest.getName() )
 				.getEntityMetamodel();
 		PojoEntityTuplizer tuplizer = ( PojoEntityTuplizer ) metaModel.getTuplizer( EntityMode.POJO );
 		if ( AccessType.FIELD.equals( accessType ) ) {
-			assertTrue(
+			Assert.assertTrue(
 					"Field access was expected.",
 					tuplizer.getGetter( 0 ) instanceof DirectPropertyAccessor.DirectGetter
 			);
 		}
 		else {
-			assertTrue(
+			Assert.assertTrue(
 					"Property access was expected.",
 					tuplizer.getGetter( 0 ) instanceof BasicPropertyAccessor.BasicGetter
 			);
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/ListenerTest.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/ListenerTest.java
deleted file mode 100644
index a1563ec160..0000000000
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/ListenerTest.java
+++ /dev/null
@@ -1,353 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
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
- */
-package org.hibernate.test.cfg;
-import java.util.Set;
-import org.hibernate.HibernateException;
-import org.hibernate.MappingException;
-import org.hibernate.cfg.Configuration;
-import org.hibernate.event.DeleteEvent;
-import org.hibernate.event.DeleteEventListener;
-import org.hibernate.event.def.DefaultDeleteEventListener;
-
-import org.junit.Test;
-
-import org.hibernate.testing.junit4.BaseUnitTestCase;
-
-import static org.junit.Assert.assertEquals;
-import static org.junit.Assert.assertNotNull;
-import static org.junit.Assert.assertTrue;
-import static org.junit.Assert.fail;
-
-/**
- * @author Gail Badner
- */
-public class ListenerTest extends BaseUnitTestCase {
-
-	public static class InvalidListenerForTest {
-	}
-
-	public static class DeleteListenerForTest implements DeleteEventListener {
-		public void onDelete(DeleteEvent event) throws HibernateException {
-		}
-
-		public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
-		}
-	}
-
-	public static class AnotherDeleteListenerForTest implements DeleteEventListener {
-		public void onDelete(DeleteEvent event) throws HibernateException {
-		}
-
-		public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
-		}
-	}
-
-	@Test
-	public void testSetListenerNullClass() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		cfg.setListener( "delete", null );
-		assertEquals( 0, cfg.getEventListeners().getDeleteEventListeners().length );
-	}
-
-	@Test
-	public void testSetListenersNullClass() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		cfg.setListeners( "delete", null );
-		assertEquals( 0, cfg.getEventListeners().getDeleteEventListeners().length );
-	}
-
-	@Test
-	public void testSetListenerEmptyClassNameArray() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListener( "delete", new String[] { } );
-			fail( "should have thrown java.lang.ArrayStoreException" );
-		}
-		catch ( ArrayStoreException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenersEmptyClassNsmeArray() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		cfg.setListeners( "delete", new String[] { } );
-		assertEquals( 0, cfg.getEventListeners().getDeleteEventListeners().length );
-	}
-
-	@Test
-	public void testSetListenerEmptyClassObjectArray() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListener( "delete", new Object[] { } );
-			fail( "should have thrown java.lang.ArrayStoreException" );
-		}
-		catch ( ArrayStoreException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenersEmptyClassObjectArray() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListeners( "delete", new Object[] { } );
-			fail( "should have thrown ClassCastException" );
-		}
-		catch ( ClassCastException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenerEmptyClassArray() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListener( "delete", new DefaultDeleteEventListener[] { } );
-			fail( "should have thrown java.lang.ArrayStoreException" );
-		}
-		catch ( ArrayStoreException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenersEmptyClassArray() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		cfg.setListeners( "delete", new DefaultDeleteEventListener[] { } );
-		assertEquals( 0, cfg.getEventListeners().getDeleteEventListeners().length );
-	}
-
-	@Test
-	public void testSetListenerUnknownClassName() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListener( "delete", "UnknownClassName" );
-			fail( "should have thrown MappingException" );
-		}
-		catch ( MappingException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenersUnknownClassName() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListeners( "delete", new String[] { "UnknownClassName" } );
-			fail( "should have thrown MappingException" );
-		}
-		catch ( MappingException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenerInvalidClassName() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListener( "delete", InvalidListenerForTest.class.getName() );
-			fail( "should have thrown MappingException" );
-		}
-		catch ( MappingException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenersInvalidClassName() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListeners( "delete", new String[] { InvalidListenerForTest.class.getName() } );
-			fail( "should have thrown MappingException" );
-		}
-		catch ( MappingException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenerClassName() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		cfg.setListener( "delete", DeleteListenerForTest.class.getName() );
-		assertEquals( 1, cfg.getEventListeners().getDeleteEventListeners().length );
-		assertTrue( cfg.getEventListeners().getDeleteEventListeners()[0] instanceof DeleteListenerForTest );
-	}
-
-	@Test
-	public void testSetListenersClassName() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		cfg.setListeners( "delete", new String[] { DeleteListenerForTest.class.getName() } );
-		assertEquals( 1, cfg.getEventListeners().getDeleteEventListeners().length );
-		assertTrue( cfg.getEventListeners().getDeleteEventListeners()[0] instanceof DeleteListenerForTest );
-	}
-
-	@Test
-	public void testSetListenerClassNames() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListener(
-					"delete", new String[] {
-					DeleteListenerForTest.class.getName(),
-					AnotherDeleteListenerForTest.class.getName()
-			}
-			);
-			fail( "should have thrown java.lang.ArrayStoreException" );
-		}
-		catch ( ArrayStoreException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenersClassNames() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		cfg.setListeners(
-				"delete", new String[] {
-				DeleteListenerForTest.class.getName(),
-				AnotherDeleteListenerForTest.class.getName()
-		}
-		);
-		assertEquals( 2, cfg.getEventListeners().getDeleteEventListeners().length );
-		assertTrue( cfg.getEventListeners().getDeleteEventListeners()[0] instanceof DeleteListenerForTest );
-		assertTrue( cfg.getEventListeners().getDeleteEventListeners()[1] instanceof AnotherDeleteListenerForTest );
-	}
-
-	@Test
-	public void testSetListenerClassInstance() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		cfg.setListener( "delete", new DeleteListenerForTest() );
-		assertEquals( 1, cfg.getEventListeners().getDeleteEventListeners().length );
-	}
-
-	@Test
-	public void testSetListenersClassInstances() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		cfg.setListeners(
-				"delete", new DeleteEventListener[] {
-				new DeleteListenerForTest(),
-				new AnotherDeleteListenerForTest()
-		}
-		);
-		assertEquals( 2, cfg.getEventListeners().getDeleteEventListeners().length );
-		assertTrue( cfg.getEventListeners().getDeleteEventListeners()[0] instanceof DeleteListenerForTest );
-		assertTrue( cfg.getEventListeners().getDeleteEventListeners()[1] instanceof AnotherDeleteListenerForTest );
-	}
-
-	@Test
-	public void testSetListenerInvalidClassInstance() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListener( "delete", new InvalidListenerForTest() );
-			fail( "should have thrown java.lang.ArrayStoreException" );
-		}
-		catch ( ArrayStoreException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenersInvalidClassInstances() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListeners( "delete", new InvalidListenerForTest[] { new InvalidListenerForTest() } );
-			fail( "should have thrown java.lang.ClassCastException" );
-		}
-		catch ( ClassCastException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenerNullType() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListener( null, new DeleteListenerForTest() );
-			fail( "should have thrown MappingException" );
-		}
-		catch ( MappingException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenersNullType() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListeners( null, new DeleteEventListener[] { new DeleteListenerForTest() } );
-			fail( "should have thrown MappingException" );
-		}
-		catch ( MappingException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenerUnknownType() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListener( "unknown-type", new DeleteListenerForTest() );
-			fail( "should have thrown MappingException" );
-		}
-		catch ( MappingException ex ) {
-			// expected
-		}
-	}
-
-	@Test
-	public void testSetListenersUnknownType() {
-		Configuration cfg = new Configuration();
-		assertNotNull( cfg.getEventListeners().getDeleteEventListeners() );
-		try {
-			cfg.setListeners( "unknown-type", new DeleteEventListener[] { new DeleteListenerForTest() } );
-			fail( "should have thrown MappingException" );
-		}
-		catch ( MappingException ex ) {
-			// expected
-		}
-	}
-}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
index 8597d932c8..49c94bf074 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/PersisterClassProviderTest.java
@@ -1,83 +1,89 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * JBoss, Home of Professional Open Source
  * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
  * as indicated by the @authors tag. All rights reserved.
  * See the copyright.txt in the distribution for a
  * full listing of individual contributors.
  *
  * This copyrighted material is made available to anyone wishing to use,
  * modify, copy, or redistribute it subject to the terms and conditions
  * of the GNU Lesser General Public License, v. 2.1.
  * This program is distributed in the hope that it will be useful, but WITHOUT A
  * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
  * You should have received a copy of the GNU Lesser General Public License,
  * v.2.1 along with this distribution; if not, write to the Free Software
  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
  * MA  02110-1301, USA.
  */
 package org.hibernate.test.cfg.persister;
 
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.ServiceRegistry;
 
 import org.junit.Test;
 
 import org.hibernate.testing.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class PersisterClassProviderTest extends BaseUnitTestCase {
 	@Test
 	public void testPersisterClassProvider() throws Exception {
 
 		Configuration cfg = new Configuration();
 		cfg.addAnnotatedClass( Gate.class );
 		ServiceRegistry serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 		//no exception as the GoofyPersisterClassProvider is not set
 		SessionFactory sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 		sessionFactory.close();
+		ServiceRegistryBuilder.destroy( serviceRegistry );
 
+		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
 		serviceRegistry.registerService( PersisterClassResolver.class, new GoofyPersisterClassProvider() );
 		cfg = new Configuration();
 		cfg.addAnnotatedClass( Gate.class );
 		try {
 			sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 			sessionFactory.close();
 		}
 		catch ( MappingException e ) {
 			assertEquals(
 					"The entity persister should be overridden",
 					GoofyPersisterClassProvider.NoopEntityPersister.class,
 					( (GoofyException) e.getCause() ).getValue()
 			);
 		}
+		finally {
+			ServiceRegistryBuilder.destroy( serviceRegistry );
+		}
 
 		cfg = new Configuration();
 		cfg.addAnnotatedClass( Portal.class );
 		cfg.addAnnotatedClass( Window.class );
+		serviceRegistry = ServiceRegistryBuilder.buildServiceRegistry( cfg.getProperties() );
+		serviceRegistry.registerService( PersisterClassResolver.class, new GoofyPersisterClassProvider() );
 		try {
 			sessionFactory = cfg.buildSessionFactory( serviceRegistry );
 			sessionFactory.close();
 		}
 		catch ( MappingException e ) {
 			assertEquals(
 					"The collection persister should be overridden but not the entity persister",
 					GoofyPersisterClassProvider.NoopCollectionPersister.class,
 					( (GoofyException) e.getCause() ).getValue() );
 		}
-
-		if ( serviceRegistry != null ) {
+		finally {
 			ServiceRegistryBuilder.destroy( serviceRegistry );
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/event/collection/CollectionListeners.java b/hibernate-core/src/test/java/org/hibernate/test/event/collection/CollectionListeners.java
index 7c8b8895a6..be17efb04b 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/event/collection/CollectionListeners.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/event/collection/CollectionListeners.java
@@ -1,212 +1,207 @@
-//$Id: $
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2007, Red Hat Middleware LLC or third-party contributors as
- * indicated by the @author tags or express copyright attribution statements
- * applied by the authors.
+ * Copyright (c) 2007-2011, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
  *
- * All third-party contributions are distributed under license by Red Hat
- * Middleware LLC.  This copyrighted material is made available to anyone
- * wishing to use, modify, copy, or redistribute it subject to the terms
- * and conditions of the GNU Lesser General Public License, as published by
- * the Free Software Foundation.  This program is distributed in the hope
- * that it will be useful, but WITHOUT ANY WARRANTY; without even the
- * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
  *
- * See the GNU Lesser General Public License for more details.  You should
- * have received a copy of the GNU Lesser General Public License along with
- * this distribution; if not, write to: Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor Boston, MA  02110-1301  USA
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
  */
 package org.hibernate.test.event.collection;
+
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
+
 import org.hibernate.SessionFactory;
+import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.event.AbstractCollectionEvent;
+import org.hibernate.event.EventType;
 import org.hibernate.event.InitializeCollectionEvent;
-import org.hibernate.event.InitializeCollectionEventListener;
 import org.hibernate.event.PostCollectionRecreateEvent;
 import org.hibernate.event.PostCollectionRecreateEventListener;
 import org.hibernate.event.PostCollectionRemoveEvent;
 import org.hibernate.event.PostCollectionRemoveEventListener;
 import org.hibernate.event.PostCollectionUpdateEvent;
 import org.hibernate.event.PostCollectionUpdateEventListener;
 import org.hibernate.event.PreCollectionRecreateEvent;
 import org.hibernate.event.PreCollectionRecreateEventListener;
 import org.hibernate.event.PreCollectionRemoveEvent;
 import org.hibernate.event.PreCollectionRemoveEventListener;
 import org.hibernate.event.PreCollectionUpdateEvent;
 import org.hibernate.event.PreCollectionUpdateEventListener;
 import org.hibernate.event.def.DefaultInitializeCollectionEventListener;
-import org.hibernate.impl.SessionFactoryImpl;
+import org.hibernate.service.event.spi.EventListenerRegistry;
 
 /**
- * Author: Gail Badner
+ * @author Gail Badner
+ * @author Steve Ebersole
  */
 public class CollectionListeners {
 
-
 	public interface Listener extends Serializable {
 		void addEvent(AbstractCollectionEvent event, Listener listener);
 	}
 
 	public static abstract class AbstractListener implements Listener {
 
 		private final CollectionListeners listeners;
 
 		protected AbstractListener( CollectionListeners listeners ) {
 			this.listeners = listeners;
 		}
 
 		public void addEvent(AbstractCollectionEvent event, Listener listener) {
 			listeners.addEvent( event, listener );
 		}
 	}
 
 	public static class InitializeCollectionListener
 			extends DefaultInitializeCollectionEventListener
 			implements Listener {
 		private final CollectionListeners listeners;
 		private InitializeCollectionListener(CollectionListeners listeners) {
 			this.listeners = listeners;
 		}
 		public void onInitializeCollection(InitializeCollectionEvent event) {
 			super.onInitializeCollection( event );
 			addEvent( event, this );
 		}
 		public void addEvent(AbstractCollectionEvent event, Listener listener) {
 			listeners.addEvent( event, listener );
 		}
 	}
 
 	public static class PreCollectionRecreateListener extends AbstractListener
 			implements PreCollectionRecreateEventListener {
 		private PreCollectionRecreateListener(CollectionListeners listeners) {
 			super( listeners );
 		}
 		public void onPreRecreateCollection(PreCollectionRecreateEvent event) {
 			addEvent( event, this );
 		}
 	}
 
 	public static class PostCollectionRecreateListener extends AbstractListener
 			implements PostCollectionRecreateEventListener {
 		private PostCollectionRecreateListener(CollectionListeners listeners) {
 			super( listeners );
 		}
 		public void onPostRecreateCollection(PostCollectionRecreateEvent event) {
 			addEvent( event, this );
 		}
 	}
 
 	public static class PreCollectionRemoveListener extends AbstractListener
 			implements PreCollectionRemoveEventListener {
 		private PreCollectionRemoveListener(CollectionListeners listeners) {
 			super( listeners );
 		}
 		public void onPreRemoveCollection(PreCollectionRemoveEvent event) {
 			addEvent( event, this );
 		}
 	}
 
 	public static class PostCollectionRemoveListener extends AbstractListener
 			implements PostCollectionRemoveEventListener {
 		private PostCollectionRemoveListener(CollectionListeners listeners) {
 			super( listeners );
 		}
 		public void onPostRemoveCollection(PostCollectionRemoveEvent event) {
 			addEvent( event, this );
 		}
 	}
 
 	public static class PreCollectionUpdateListener extends AbstractListener
 			implements PreCollectionUpdateEventListener {
 		private PreCollectionUpdateListener(CollectionListeners listeners) {
 			super( listeners );
 		}
 		public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
 			addEvent( event, this );
 		}
 	}
 
 	public static class PostCollectionUpdateListener extends AbstractListener
 			implements PostCollectionUpdateEventListener {
 		private PostCollectionUpdateListener(CollectionListeners listeners) {
 			super( listeners );
 		}
 		public void onPostUpdateCollection(PostCollectionUpdateEvent event) {
 			addEvent( event, this );
 		}
 	}
 
 	private final PreCollectionRecreateListener preCollectionRecreateListener;
 	private final InitializeCollectionListener initializeCollectionListener;
 	private final PreCollectionRemoveListener preCollectionRemoveListener;
 	private final PreCollectionUpdateListener preCollectionUpdateListener;
 	private final PostCollectionRecreateListener postCollectionRecreateListener;
 	private final PostCollectionRemoveListener postCollectionRemoveListener;
 	private final PostCollectionUpdateListener postCollectionUpdateListener;
 
 	private List listenersCalled = new ArrayList();
 	private List events = new ArrayList();
 
 	public CollectionListeners( SessionFactory sf) {
 		preCollectionRecreateListener = new PreCollectionRecreateListener( this );
 		initializeCollectionListener = new InitializeCollectionListener( this );
 		preCollectionRemoveListener = new PreCollectionRemoveListener( this );
 		preCollectionUpdateListener = new PreCollectionUpdateListener( this );
 		postCollectionRecreateListener = new PostCollectionRecreateListener( this );
 		postCollectionRemoveListener = new PostCollectionRemoveListener( this );
 		postCollectionUpdateListener = new PostCollectionUpdateListener( this );
-		SessionFactoryImpl impl = ( SessionFactoryImpl ) sf;
-		impl.getEventListeners().setInitializeCollectionEventListeners(
-				new InitializeCollectionEventListener[] { initializeCollectionListener }
-		);
-		impl.getEventListeners().setPreCollectionRecreateEventListeners(
-				new PreCollectionRecreateEventListener[] { preCollectionRecreateListener }
-		);
-		impl.getEventListeners().setPostCollectionRecreateEventListeners(
-				new PostCollectionRecreateEventListener[] { postCollectionRecreateListener }
-		);
-		impl.getEventListeners().setPreCollectionRemoveEventListeners(
-				new PreCollectionRemoveEventListener[] { preCollectionRemoveListener }
-		);
-		impl.getEventListeners().setPostCollectionRemoveEventListeners(
-				new PostCollectionRemoveEventListener[] { postCollectionRemoveListener }
-		);
-		impl.getEventListeners().setPreCollectionUpdateEventListeners(
-				new PreCollectionUpdateEventListener[] { preCollectionUpdateListener }
-		);
-		impl.getEventListeners().setPostCollectionUpdateEventListeners(
-				new PostCollectionUpdateEventListener[] { postCollectionUpdateListener }
-		);
+
+		EventListenerRegistry registry = ( (SessionFactoryImplementor) sf ).getServiceRegistry().getService( EventListenerRegistry.class );
+		registry.setListeners( EventType.INIT_COLLECTION, initializeCollectionListener );
+
+		registry.setListeners( EventType.PRE_COLLECTION_RECREATE, preCollectionRecreateListener );
+		registry.setListeners( EventType.POST_COLLECTION_RECREATE, postCollectionRecreateListener );
+
+		registry.setListeners( EventType.PRE_COLLECTION_REMOVE, preCollectionRemoveListener );
+		registry.setListeners( EventType.POST_COLLECTION_REMOVE, postCollectionRemoveListener );
+
+		registry.setListeners( EventType.PRE_COLLECTION_UPDATE, preCollectionUpdateListener );
+		registry.setListeners( EventType.POST_COLLECTION_UPDATE, postCollectionUpdateListener );
 	}
 
 	public void addEvent(AbstractCollectionEvent event, Listener listener) {
 		listenersCalled.add( listener );
 		events.add( event );
 	}
 
 	public List getListenersCalled() {
 		return listenersCalled;
 	}
 
 	public List getEvents() {
 		return events;
 	}
 
 	public void clear() {
 		listenersCalled.clear();
 		events.clear();
 	}
 
 	public PreCollectionRecreateListener getPreCollectionRecreateListener() { return preCollectionRecreateListener; }
 	public InitializeCollectionListener getInitializeCollectionListener() { return initializeCollectionListener; }
 	public PreCollectionRemoveListener getPreCollectionRemoveListener() { return preCollectionRemoveListener; }
 	public PreCollectionUpdateListener getPreCollectionUpdateListener() { return preCollectionUpdateListener; }
 	public PostCollectionRecreateListener getPostCollectionRecreateListener() { return postCollectionRecreateListener; }
 	public PostCollectionRemoveListener getPostCollectionRemoveListener() { return postCollectionRemoveListener; }
 	public PostCollectionUpdateListener getPostCollectionUpdateListener() { return postCollectionUpdateListener; }
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java b/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java
index 5d9f25e94c..1dd7735913 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java
@@ -1,103 +1,113 @@
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
 package org.hibernate.test.events;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.event.DeleteEvent;
 import org.hibernate.event.DeleteEventListener;
 import org.hibernate.event.Destructible;
+import org.hibernate.event.EventType;
 import org.hibernate.event.Initializable;
+import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.service.internal.ServiceRegistryImpl;
 
 import org.junit.Test;
 
+import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 
 
 /**
  * CallbackTest implementation
  *
  * @author Steve Ebersole
  */
 public class CallbackTest extends BaseCoreFunctionalTestCase {
 	private TestingObserver observer = new TestingObserver();
 	private TestingListener listener = new TestingListener();
 
 	public String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	public void configure(Configuration cfg) {
 		cfg.setSessionFactoryObserver( observer );
-		cfg.getEventListeners().setDeleteEventListeners( new DeleteEventListener[] { listener } );
+	}
+
+	@Override
+	protected void applyServices(ServiceRegistryImpl serviceRegistry) {
+		super.applyServices( serviceRegistry );
+		serviceRegistry.getService( EventListenerRegistry.class ).setListeners( EventType.DELETE, listener );
 	}
 
 	@Test
+	@FailureExpected( jiraKey = "HHH-5913", message = "Need to figure out how to initialize/destroy event listeners now")
 	public void testCallbacks() {
 		assertEquals( "observer not notified of creation", 1, observer.creationCount );
 		assertEquals( "listener not notified of creation", 1, listener.initCount );
 
 		sessionFactory().close();
 
 		assertEquals( "observer not notified of close", 1, observer.closedCount );
 		assertEquals( "listener not notified of close", 1, listener.destoryCount );
 	}
 
 	private static class TestingObserver implements SessionFactoryObserver {
 		private int creationCount = 0;
 		private int closedCount = 0;
 
 		public void sessionFactoryCreated(SessionFactory factory) {
 			creationCount++;
 		}
 
 		public void sessionFactoryClosed(SessionFactory factory) {
 			closedCount++;
 		}
 	}
 
 	private static class TestingListener implements DeleteEventListener, Initializable, Destructible {
 		private int initCount = 0;
 		private int destoryCount = 0;
 
 		public void initialize(Configuration cfg) {
 			initCount++;
 		}
 
 		public void cleanup() {
 			destoryCount++;
 		}
 
 		public void onDelete(DeleteEvent event) throws HibernateException {
 		}
 
 		public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java b/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
index 3148452972..c30616a85a 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
@@ -1,150 +1,160 @@
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
 package org.hibernate.test.jpa;
 
 import javax.persistence.EntityNotFoundException;
 import java.io.Serializable;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.event.AutoFlushEventListener;
+import org.hibernate.event.EventType;
 import org.hibernate.event.FlushEntityEventListener;
 import org.hibernate.event.FlushEventListener;
 import org.hibernate.event.PersistEventListener;
 import org.hibernate.event.def.DefaultAutoFlushEventListener;
 import org.hibernate.event.def.DefaultFlushEntityEventListener;
 import org.hibernate.event.def.DefaultFlushEventListener;
 import org.hibernate.event.def.DefaultPersistEventListener;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.proxy.EntityNotFoundDelegate;
+import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.service.internal.ServiceRegistryImpl;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * An abstract test for all JPA spec related tests.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractJPATest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "jpa/Part.hbm.xml", "jpa/Item.hbm.xml", "jpa/MyEntity.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.JPAQL_STRICT_COMPLIANCE, "true" );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "false" );
 		cfg.setEntityNotFoundDelegate( new JPAEntityNotFoundDelegate() );
-		cfg.getEventListeners().setPersistEventListeners( buildPersistEventListeners() );
-		cfg.getEventListeners().setPersistOnFlushEventListeners( buildPersisOnFlushEventListeners() );
-		cfg.getEventListeners().setAutoFlushEventListeners( buildAutoFlushEventListeners() );
-		cfg.getEventListeners().setFlushEventListeners( buildFlushEventListeners() );
-		cfg.getEventListeners().setFlushEntityEventListeners( buildFlushEntityEventListeners() );
+	}
+
+	@Override
+	protected void applyServices(ServiceRegistryImpl serviceRegistry) {
+		super.applyServices( serviceRegistry );
+
+		EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
+		eventListenerRegistry.setListeners( EventType.PERSIST, buildPersistEventListeners() );
+		eventListenerRegistry.setListeners( EventType.PERSIST_ONFLUSH, buildPersisOnFlushEventListeners() );
+		eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, buildAutoFlushEventListeners() );
+		eventListenerRegistry.setListeners( EventType.FLUSH, buildFlushEventListeners() );
+		eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, buildFlushEntityEventListeners() );
 	}
 
 	@Override
 	public String getCacheConcurrencyStrategy() {
 		// no second level caching
 		return null;
 	}
 
 
 	// mimic specific exception aspects of the JPA environment ~~~~~~~~~~~~~~~~
 
 	private static class JPAEntityNotFoundDelegate implements EntityNotFoundDelegate {
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException("Unable to find " + entityName  + " with id " + id);			
 		}
 	}
 
 	// mimic specific event aspects of the JPA environment ~~~~~~~~~~~~~~~~~~~~
 
 	protected PersistEventListener[] buildPersistEventListeners() {
 		return new PersistEventListener[] { new JPAPersistEventListener() };
 	}
 
 	protected PersistEventListener[] buildPersisOnFlushEventListeners() {
 		return new PersistEventListener[] { new JPAPersistOnFlushEventListener() };
 	}
 
 	protected AutoFlushEventListener[] buildAutoFlushEventListeners() {
 		return new AutoFlushEventListener[] { JPAAutoFlushEventListener.INSTANCE };
 	}
 
 	protected FlushEventListener[] buildFlushEventListeners() {
 		return new FlushEventListener[] { JPAFlushEventListener.INSTANCE };
 	}
 
 	protected FlushEntityEventListener[] buildFlushEntityEventListeners() {
 		return new FlushEntityEventListener[] { new JPAFlushEntityEventListener() };
 	}
 
 	public static class JPAPersistEventListener extends DefaultPersistEventListener {
 		// overridden in JPA impl for entity callbacks...
 	}
 
 	public static class JPAPersistOnFlushEventListener extends JPAPersistEventListener {
 		@Override
         protected CascadingAction getCascadeAction() {
 			return CascadingAction.PERSIST_ON_FLUSH;
 		}
 	}
 
 	public static class JPAAutoFlushEventListener extends DefaultAutoFlushEventListener {
 		// not sure why EM code has this ...
 		public static final AutoFlushEventListener INSTANCE = new JPAAutoFlushEventListener();
 
 		@Override
         protected CascadingAction getCascadingAction() {
 			return CascadingAction.PERSIST_ON_FLUSH;
 		}
 
 		@Override
         protected Object getAnything() {
 			return IdentityMap.instantiate( 10 );
 		}
 	}
 
 	public static class JPAFlushEventListener extends DefaultFlushEventListener {
 		// not sure why EM code has this ...
 		public static final FlushEventListener INSTANCE = new JPAFlushEventListener();
 
 		@Override
         protected CascadingAction getCascadingAction() {
 			return CascadingAction.PERSIST_ON_FLUSH;
 		}
 
 		@Override
         protected Object getAnything() {
 			return IdentityMap.instantiate( 10 );
 		}
 	}
 
 	public static class JPAFlushEntityEventListener extends DefaultFlushEntityEventListener {
 		// in JPA, used mainly for preUpdate callbacks...
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java b/hibernate-core/src/test/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
index e6f12b30b5..74857b9b62 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
@@ -1,173 +1,178 @@
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
 package org.hibernate.test.keymanytoone.bidir.component;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
+import org.hibernate.event.EventType;
 import org.hibernate.event.LoadEvent;
 import org.hibernate.event.LoadEventListener;
 import org.hibernate.event.def.DefaultLoadEventListener;
+import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.service.internal.ServiceRegistryImpl;
 
 import org.junit.Test;
 
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.fail;
 
 /**
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"unchecked"})
 public class EagerKeyManyToOneTest extends BaseCoreFunctionalTestCase {
 	public String[] getMappings() {
 		return new String[] { "keymanytoone/bidir/component/EagerMapping.hbm.xml" };
 	}
 
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
-		LoadEventListener[] baseListeners = cfg.getEventListeners().getLoadEventListeners();
-		int baseLength = baseListeners.length;
-		LoadEventListener[] expandedListeners = new LoadEventListener[ baseLength + 1 ];
-		expandedListeners[ 0 ] = new CustomLoadListener();
-		System.arraycopy( baseListeners, 0, expandedListeners, 1, baseLength );
-		cfg.getEventListeners().setLoadEventListeners( expandedListeners );
+	}
+
+	@Override
+	protected void applyServices(ServiceRegistryImpl serviceRegistry) {
+		super.applyServices( serviceRegistry );
+
+		EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
+		eventListenerRegistry.prependListeners( EventType.LOAD, new CustomLoadListener() );
 	}
 
 	@Test
 	public void testSaveCascadedToKeyManyToOne() {
 		sessionFactory().getStatistics().clear();
 
 		// test cascading a save to an association with a key-many-to-one which refers to a
 		// just saved entity
 		Session s = openSession();
 		s.beginTransaction();
 		Customer cust = new Customer( "Acme, Inc." );
 		Order order = new Order( new Order.Id( cust, 1 ) );
 		cust.getOrders().add( order );
 		s.save( cust );
 		s.flush();
 		assertEquals( 2, sessionFactory().getStatistics().getEntityInsertCount() );
 		s.delete( cust );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLoadingStrategies() {
 		sessionFactory().getStatistics().clear();
 
 		Session s = openSession();
 		s.beginTransaction();
 		Customer cust = new Customer( "Acme, Inc." );
 		Order order = new Order( new Order.Id( cust, 1 ) );
 		cust.getOrders().add( order );
 		s.save( cust );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 
 		cust = ( Customer ) s.createQuery( "from Customer" ).uniqueResult();
 		assertEquals( 1, cust.getOrders().size() );
 		s.clear();
 
 		cust = ( Customer ) s.createQuery( "from Customer c join fetch c.orders" ).uniqueResult();
 		assertEquals( 1, cust.getOrders().size() );
 		s.clear();
 
 		cust = ( Customer ) s.createQuery( "from Customer c join fetch c.orders as o join fetch o.id.customer" ).uniqueResult();
 		assertEquals( 1, cust.getOrders().size() );
 		s.clear();
 
 		cust = ( Customer ) s.createCriteria( Customer.class ).uniqueResult();
 		assertEquals( 1, cust.getOrders().size() );
 		s.clear();
 
 		s.delete( cust );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-2277")
 	public void testLoadEntityWithEagerFetchingToKeyManyToOneReferenceBackToSelf() {
 		sessionFactory().getStatistics().clear();
 
 		// long winded method name to say that this is a test specifically for HHH-2277 ;)
 		// essentially we have a bidirectional association where one side of the
 		// association is actually part of a composite PK.
 		//
 		// The way these are mapped causes the problem because both sides
 		// are defined as eager which leads to the infinite loop; if only
 		// one side is marked as eager, then all is ok.  In other words the
 		// problem arises when both pieces of instance data are coming from
 		// the same result set.  This is because no "entry" can be placed
 		// into the persistence context for the association with the
 		// composite key because we are in the process of trying to build
 		// the composite-id instance
 		Session s = openSession();
 		s.beginTransaction();
 		Customer cust = new Customer( "Acme, Inc." );
 		Order order = new Order( new Order.Id( cust, 1 ) );
 		cust.getOrders().add( order );
 		s.save( cust );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		try {
 			cust = ( Customer ) s.get( Customer.class, cust.getId() );
 		}
 		catch( OverflowCondition overflow ) {
 			fail( "get()/load() caused overflow condition" );
 		}
 		s.delete( cust );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	private static class OverflowCondition extends RuntimeException {
 	}
 
 	private static class CustomLoadListener extends DefaultLoadEventListener {
 		private int internalLoadCount = 0;
 		public void onLoad(LoadEvent event, LoadType loadType) throws HibernateException {
 			if ( LoadEventListener.INTERNAL_LOAD_EAGER.getName().equals( loadType.getName() ) ) {
 				internalLoadCount++;
 				if ( internalLoadCount > 10 ) {
 					throw new OverflowCondition();
 				}
 			}
 			super.onLoad( event, loadType );
 			internalLoadCount--;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/stats/StatsTest.java b/hibernate-core/src/test/java/org/hibernate/test/stats/StatsTest.java
index 9153ea1427..59fc68fb63 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/stats/StatsTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/stats/StatsTest.java
@@ -1,255 +1,257 @@
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
 package org.hibernate.test.stats;
 import java.util.HashSet;
 import java.util.Iterator;
 
 import org.hibernate.FetchMode;
 import org.hibernate.Hibernate;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.mapping.Collection;
 import org.hibernate.stat.QueryStatistics;
 import org.hibernate.stat.Statistics;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * Show the difference between fetch and load
  *
  * @author Emmanuel Bernard
  */
 public class StatsTest extends BaseCoreFunctionalTestCase {
 	public String[] getMappings() {
 		return new String[] { "stats/Continent.hbm.xml" };
 	}
 
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 	}
 
 	@Test
 	@SuppressWarnings( {"UnusedAssignment"})
 	public void testCollectionFetchVsLoad() throws Exception {
 		Statistics stats = sessionFactory().getStatistics();
 		stats.clear();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Continent europe = fillDb(s);
 		tx.commit();
 		s.clear();
 
 		tx = s.beginTransaction();
 		assertEquals(0, stats.getCollectionLoadCount() );
 		assertEquals(0,  stats.getCollectionFetchCount() );
 		Continent europe2 = (Continent) s.get( Continent.class, europe.getId() );
 		assertEquals("Lazy true: no collection should be loaded", 0, stats.getCollectionLoadCount() );
 		assertEquals( 0, stats.getCollectionFetchCount() );
 		europe2.getCountries().size();
 		assertEquals( 1, stats.getCollectionLoadCount() );
 		assertEquals("Explicit fetch of the collection state", 1, stats.getCollectionFetchCount() );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		stats.clear();
 		europe = fillDb(s);
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		assertEquals( 0, stats.getCollectionLoadCount() );
 		assertEquals( 0, stats.getCollectionFetchCount() );
 		europe2 = (Continent) s.createQuery(
 				"from " + Continent.class.getName() + " a join fetch a.countries where a.id = " + europe.getId()
 			).uniqueResult();
 		assertEquals( 1, stats.getCollectionLoadCount() );
 		assertEquals( "collection should be loaded in the same query as its parent", 0, stats.getCollectionFetchCount() );
 		tx.commit();
 		s.close();
 
+		// open second SessionFactory
 		Collection coll = configuration().getCollectionMapping(Continent.class.getName() + ".countries");
 		coll.setFetchMode(FetchMode.JOIN);
 		coll.setLazy(false);
-		SessionFactory sf = configuration().buildSessionFactory( serviceRegistry() );
+		SessionFactory sf = configuration().buildSessionFactory();
 		stats = sf.getStatistics();
 		stats.clear();
 		stats.setStatisticsEnabled(true);
 		s = sf.openSession();
 		tx = s.beginTransaction();
 		europe = fillDb(s);
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		assertEquals( 0, stats.getCollectionLoadCount() );
 		assertEquals( 0, stats.getCollectionFetchCount() );
 		europe2 = (Continent) s.get( Continent.class, europe.getId() );
 		assertEquals( 1, stats.getCollectionLoadCount() );
 		assertEquals( "Should do direct load, not indirect second load when lazy false and JOIN", 0, stats.getCollectionFetchCount() );
 		tx.commit();
 		s.close();
 		sf.close();
 
+		// open third SessionFactory
 		coll = configuration().getCollectionMapping(Continent.class.getName() + ".countries");
 		coll.setFetchMode(FetchMode.SELECT);
 		coll.setLazy(false);
-		sf = configuration().buildSessionFactory( serviceRegistry() );
+		sf = configuration().buildSessionFactory();
 		stats = sf.getStatistics();
 		stats.clear();
 		stats.setStatisticsEnabled(true);
 		s = sf.openSession();
 		tx = s.beginTransaction();
 		europe = fillDb(s);
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		assertEquals( 0, stats.getCollectionLoadCount() );
 		assertEquals( 0, stats.getCollectionFetchCount() );
 		europe2 = (Continent) s.get( Continent.class, europe.getId() );
 		assertEquals( 1, stats.getCollectionLoadCount() );
 		assertEquals( "Should do explicit collection load, not part of the first one", 1, stats.getCollectionFetchCount() );
 		for ( Object o : europe2.getCountries() ) {
 			s.delete( o );
 		}
 		cleanDb( s );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testQueryStatGathering() {
 		Statistics stats = sessionFactory().getStatistics();
 		stats.clear();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		fillDb(s);
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		final String continents = "from Continent";
 		int results = s.createQuery( continents ).list().size();
 		QueryStatistics continentStats = stats.getQueryStatistics( continents );
 		assertNotNull( "stats were null",  continentStats );
 		assertEquals( "unexpected execution count", 1, continentStats.getExecutionCount() );
 		assertEquals( "unexpected row count", results, continentStats.getExecutionRowCount() );
 		long maxTime = continentStats.getExecutionMaxTime();
 		assertEquals( maxTime, stats.getQueryExecutionMaxTime() );
 //		assertEquals( continents, stats.getQueryExecutionMaxTimeQueryString() );
 
 		Iterator itr = s.createQuery( continents ).iterate();
 		// iterate() should increment the execution count
 		assertEquals( "unexpected execution count", 2, continentStats.getExecutionCount() );
 		// but should not effect the cumulative row count
 		assertEquals( "unexpected row count", results, continentStats.getExecutionRowCount() );
 		Hibernate.close( itr );
 
 		ScrollableResults scrollableResults = s.createQuery( continents ).scroll();
 		// same deal with scroll()...
 		assertEquals( "unexpected execution count", 3, continentStats.getExecutionCount() );
 		assertEquals( "unexpected row count", results, continentStats.getExecutionRowCount() );
 		// scroll through data because SybaseASE15Dialect throws NullPointerException
 		// if data is not read before closing the ResultSet
 		while ( scrollableResults.next() ) {
 			// do nothing
 		}
 		scrollableResults.close();
 		tx.commit();
 		s.close();
 
 		// explicitly check that statistics for "split queries" get collected
 		// under the original query
 		stats.clear();
 		s = openSession();
 		tx = s.beginTransaction();
 		final String localities = "from Locality";
 		results = s.createQuery( localities ).list().size();
 		QueryStatistics localityStats = stats.getQueryStatistics( localities );
 		assertNotNull( "stats were null",  localityStats );
 		// ...one for each split query
 		assertEquals( "unexpected execution count", 2, localityStats.getExecutionCount() );
 		assertEquals( "unexpected row count", results, localityStats.getExecutionRowCount() );
 		maxTime = localityStats.getExecutionMaxTime();
 		assertEquals( maxTime, stats.getQueryExecutionMaxTime() );
 //		assertEquals( localities, stats.getQueryExecutionMaxTimeQueryString() );
 		tx.commit();
 		s.close();
 		assertFalse( s.isOpen() );
 
 		// native sql queries
 		stats.clear();
 		s = openSession();
 		tx = s.beginTransaction();
 		final String sql = "select id, name from Country";
 		results = s.createSQLQuery( sql ).addEntity( Country.class ).list().size();
 		QueryStatistics sqlStats = stats.getQueryStatistics( sql );
 		assertNotNull( "sql stats were null", sqlStats );
 		assertEquals( "unexpected execution count", 1, sqlStats.getExecutionCount() );
 		assertEquals( "unexpected row count", results, sqlStats.getExecutionRowCount() );
 		maxTime = sqlStats.getExecutionMaxTime();
 		assertEquals( maxTime, stats.getQueryExecutionMaxTime() );
 //		assertEquals( sql, stats.getQueryExecutionMaxTimeQueryString() );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		cleanDb( s );
 		tx.commit();
 		s.close();
 	}
 
 	private Continent fillDb(Session s) {
 		Continent europe = new Continent();
 		europe.setName("Europe");
 		Country france = new Country();
 		france.setName("France");
 		europe.setCountries( new HashSet() );
 		europe.getCountries().add(france);
 		s.persist(france);
 		s.persist(europe);
 		return europe;
 	}
 
 	private void cleanDb(Session s) {
 		s.createQuery( "delete Locality" ).executeUpdate();
 		s.createQuery( "delete Country" ).executeUpdate();
 		s.createQuery( "delete Continent" ).executeUpdate();
 	}
 
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
index 80f4182b08..3136a3f606 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
@@ -1,1636 +1,1598 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.ejb;
 
 import javax.naming.BinaryRefAddr;
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.Referenceable;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.EntityNotFoundException;
 import javax.persistence.MappedSuperclass;
 import javax.persistence.PersistenceException;
 import javax.persistence.spi.PersistenceUnitInfo;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.sql.DataSource;
 import java.io.BufferedInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectOutput;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.lang.annotation.Annotation;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.MappingNotFoundException;
-import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.annotations.reflection.XMLContext;
 import org.hibernate.ejb.connection.InjectedDataSourceConnectionProvider;
+import org.hibernate.ejb.event.JpaEventListenerRegistration;
 import org.hibernate.ejb.instrument.InterceptFieldClassFileTransformer;
 import org.hibernate.ejb.packaging.JarVisitorFactory;
 import org.hibernate.ejb.packaging.NamedInputStream;
 import org.hibernate.ejb.packaging.NativeScanner;
 import org.hibernate.ejb.packaging.PersistenceMetadata;
 import org.hibernate.ejb.packaging.PersistenceXmlLoader;
 import org.hibernate.ejb.packaging.Scanner;
 import org.hibernate.ejb.util.ConfigurationHelper;
 import org.hibernate.ejb.util.LogHelper;
 import org.hibernate.ejb.util.NamingHelper;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
-import org.hibernate.event.EventListeners;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.xml.MappingReader;
 import org.hibernate.internal.util.xml.OriginImpl;
 import org.hibernate.internal.util.xml.XmlDocument;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.JACCConfiguration;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;
 
 /**
  * Allow a fine tuned configuration of an EJB 3.0 EntityManagerFactory
  *
  * A Ejb3Configuration object is only guaranteed to create one EntityManagerFactory.
- * Multiple usage of #buildEntityManagerFactory() is not guaranteed.
+ * Multiple usage of {@link #buildEntityManagerFactory()} is not guaranteed.
  *
  * After #buildEntityManagerFactory() has been called, you no longer can change the configuration
  * state (no class adding, no property change etc)
  *
  * When serialized / deserialized or retrieved from the JNDI, you no longer can change the
  * configuration state (no class adding, no property change etc)
  *
  * Putting the configuration in the JNDI is an expensive operation that requires a partial
  * serialization
  *
  * @author Emmanuel Bernard
  */
 public class Ejb3Configuration implements Serializable, Referenceable {
 
-    private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
-                                                                           Ejb3Configuration.class.getName());
+    private static final EntityManagerLogger LOG = Logger.getMessageLogger(
+			EntityManagerLogger.class,
+			Ejb3Configuration.class.getName()
+	);
 	private static final String IMPLEMENTATION_NAME = HibernatePersistence.class.getName();
 	private static final String META_INF_ORM_XML = "META-INF/orm.xml";
 	private static final String PARSED_MAPPING_DOMS = "hibernate.internal.mapping_doms";
 
 	private static EntityNotFoundDelegate ejb3EntityNotFoundDelegate = new Ejb3EntityNotFoundDelegate();
 	private static Configuration DEFAULT_CONFIGURATION = new Configuration();
 
 	private static class Ejb3EntityNotFoundDelegate implements EntityNotFoundDelegate, Serializable {
 		public void handleEntityNotFound(String entityName, Serializable id) {
 			throw new EntityNotFoundException("Unable to find " + entityName  + " with id " + id);
 		}
 	}
 
 	private String persistenceUnitName;
 	private String cfgXmlResource;
 
 	private Configuration cfg;
 	//made transient and not restored in deserialization on purpose, should no longer be called after restoration
-	private transient EventListenerConfigurator listenerConfigurator;
 	private PersistenceUnitTransactionType transactionType;
 	private boolean discardOnClose;
 	//made transient and not restored in deserialization on purpose, should no longer be called after restoration
 	private transient ClassLoader overridenClassLoader;
 	private boolean isConfigurationProcessed = false;
 
 
 	public Ejb3Configuration() {
 		cfg = new Configuration();
 		cfg.setEntityNotFoundDelegate( ejb3EntityNotFoundDelegate );
-		listenerConfigurator = new EventListenerConfigurator( this );
 	}
 
 	/**
 	 * Used to inject a datasource object as the connection provider.
 	 * If used, be sure to <b>not override</b> the hibernate.connection.provider_class
 	 * property
 	 */
 	@SuppressWarnings({ "JavaDoc", "unchecked" })
 	public void setDataSource(DataSource ds) {
 		if ( ds != null ) {
 			cfg.getProperties().put( Environment.DATASOURCE, ds );
 			this.setProperty( Environment.CONNECTION_PROVIDER, DatasourceConnectionProviderImpl.class.getName() );
 		}
 	}
 
 	/**
 	 * create a factory from a parsed persistence.xml
 	 * Especially the scanning of classes and additional jars is done already at this point.
 	 * <p/>
 	 * NOTE: public only for unit testing purposes; not a public API!
 	 *
 	 * @param metadata The information parsed from the persistence.xml
 	 * @param overridesIn Any explicitly passed config settings
 	 *
 	 * @return this
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Ejb3Configuration configure(PersistenceMetadata metadata, Map overridesIn) {
         LOG.debugf("Creating Factory: %s", metadata.getName());
 
 		Map overrides = new HashMap();
 		if ( overridesIn != null ) {
 			overrides.putAll( overridesIn );
 		}
 
 		Map workingVars = new HashMap();
 		workingVars.put( AvailableSettings.PERSISTENCE_UNIT_NAME, metadata.getName() );
 		this.persistenceUnitName = metadata.getName();
 
 		if ( StringHelper.isNotEmpty( metadata.getJtaDatasource() ) ) {
 			this.setProperty( Environment.DATASOURCE, metadata.getJtaDatasource() );
 		}
 		else if ( StringHelper.isNotEmpty( metadata.getNonJtaDatasource() ) ) {
 			this.setProperty( Environment.DATASOURCE, metadata.getNonJtaDatasource() );
 		}
 		else {
 			final String driver = (String) metadata.getProps().get( AvailableSettings.JDBC_DRIVER );
 			if ( StringHelper.isNotEmpty( driver ) ) {
 				this.setProperty( Environment.DRIVER, driver );
 			}
 			final String url = (String) metadata.getProps().get( AvailableSettings.JDBC_URL );
 			if ( StringHelper.isNotEmpty( url ) ) {
 				this.setProperty( Environment.URL, url );
 			}
 			final String user = (String) metadata.getProps().get( AvailableSettings.JDBC_USER );
 			if ( StringHelper.isNotEmpty( user ) ) {
 				this.setProperty( Environment.USER, user );
 			}
 			final String pass = (String) metadata.getProps().get( AvailableSettings.JDBC_PASSWORD );
 			if ( StringHelper.isNotEmpty( pass ) ) {
 				this.setProperty( Environment.PASS, pass );
 			}
 		}
 		defineTransactionType( metadata.getTransactionType(), workingVars );
 		if ( metadata.getClasses().size() > 0 ) {
 			workingVars.put( AvailableSettings.CLASS_NAMES, metadata.getClasses() );
 		}
 		if ( metadata.getPackages().size() > 0 ) {
 			workingVars.put( AvailableSettings.PACKAGE_NAMES, metadata.getPackages() );
 		}
 		if ( metadata.getMappingFiles().size() > 0 ) {
 			workingVars.put( AvailableSettings.XML_FILE_NAMES, metadata.getMappingFiles() );
 		}
 		if ( metadata.getHbmfiles().size() > 0 ) {
 			workingVars.put( AvailableSettings.HBXML_FILES, metadata.getHbmfiles() );
 		}
 
 		Properties props = new Properties();
 		props.putAll( metadata.getProps() );
 
 		// validation factory
 		final Object validationFactory = overrides.get( AvailableSettings.VALIDATION_FACTORY );
 		if ( validationFactory != null ) {
 			props.put( AvailableSettings.VALIDATION_FACTORY, validationFactory );
 		}
 		overrides.remove( AvailableSettings.VALIDATION_FACTORY );
 
 		// validation-mode (overrides has precedence)
 		{
 			final Object integrationValue = overrides.get( AvailableSettings.VALIDATION_MODE );
 			if ( integrationValue != null ) {
 				props.put( AvailableSettings.VALIDATION_MODE, integrationValue.toString() );
 			}
 			else if ( metadata.getValidationMode() != null ) {
 				props.put( AvailableSettings.VALIDATION_MODE, metadata.getValidationMode() );
 			}
 			overrides.remove( AvailableSettings.VALIDATION_MODE );
 		}
 
 		// shared-cache-mode (overrides has precedence)
 		{
 			final Object integrationValue = overrides.get( AvailableSettings.SHARED_CACHE_MODE );
 			if ( integrationValue != null ) {
 				props.put( AvailableSettings.SHARED_CACHE_MODE, integrationValue.toString() );
 			}
 			else if ( metadata.getSharedCacheMode() != null ) {
 				props.put( AvailableSettings.SHARED_CACHE_MODE, metadata.getSharedCacheMode() );
 			}
 			overrides.remove( AvailableSettings.SHARED_CACHE_MODE );
 		}
 
 		for ( Map.Entry entry : (Set<Map.Entry>) overrides.entrySet() ) {
 			Object value = entry.getValue();
 			props.put( entry.getKey(), value == null ? "" :  value ); //alter null, not allowed in properties
 		}
 
 		configure( props, workingVars );
 		return this;
 	}
 
 	/**
 	 * Build the configuration from an entity manager name and given the
 	 * appropriate extra properties. Those properties override the one get through
 	 * the persistence.xml file.
 	 * If the persistence unit name is not found or does not match the Persistence Provider, null is returned
 	 *
 	 * This method is used in a non managed environment
 	 *
 	 * @param persistenceUnitName persistence unit name
 	 * @param integration properties passed to the persistence provider
 	 *
 	 * @return configured Ejb3Configuration or null if no persistence unit match
 	 *
 	 * @see HibernatePersistence#createEntityManagerFactory(String, java.util.Map)
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Ejb3Configuration configure(String persistenceUnitName, Map integration) {
 		try {
             LOG.debugf("Look up for persistence unit: %s", persistenceUnitName);
 			integration = integration == null ?
 					CollectionHelper.EMPTY_MAP :
 					Collections.unmodifiableMap( integration );
 			Enumeration<URL> xmls = Thread.currentThread()
 					.getContextClassLoader()
 					.getResources( "META-INF/persistence.xml" );
             if (!xmls.hasMoreElements()) LOG.unableToFindPersistenceXmlInClasspath();
 			while ( xmls.hasMoreElements() ) {
 				URL url = xmls.nextElement();
                 LOG.trace("Analyzing persistence.xml: " + url);
 				List<PersistenceMetadata> metadataFiles = PersistenceXmlLoader.deploy(
 						url,
 						integration,
 						cfg.getEntityResolver(),
 						PersistenceUnitTransactionType.RESOURCE_LOCAL );
 				for ( PersistenceMetadata metadata : metadataFiles ) {
                     LOG.trace(metadata);
 
 					if ( metadata.getProvider() == null || IMPLEMENTATION_NAME.equalsIgnoreCase(
 							metadata.getProvider()
 					) ) {
 						//correct provider
 
 						//lazy load the scanner to avoid unnecessary IOExceptions
 						Scanner scanner = null;
 						URL jarURL = null;
 						if ( metadata.getName() == null ) {
 							scanner = buildScanner( metadata.getProps(), integration );
 							jarURL = JarVisitorFactory.getJarURLFromURLEntry( url, "/META-INF/persistence.xml" );
 							metadata.setName( scanner.getUnqualifiedJarName(jarURL) );
 						}
 						if ( persistenceUnitName == null && xmls.hasMoreElements() ) {
 							throw new PersistenceException( "No name provided and several persistence units found" );
 						}
 						else if ( persistenceUnitName == null || metadata.getName().equals( persistenceUnitName ) ) {
 							if (scanner == null) {
 								scanner = buildScanner( metadata.getProps(), integration );
 								jarURL = JarVisitorFactory.getJarURLFromURLEntry( url, "/META-INF/persistence.xml" );
 							}
 							//scan main JAR
 							ScanningContext mainJarScanCtx = new ScanningContext()
 									.scanner( scanner )
 									.url( jarURL )
 									.explicitMappingFiles( metadata.getMappingFiles() )
 									.searchOrm( true );
 							setDetectedArtifactsOnScanningContext( mainJarScanCtx, metadata.getProps(), integration,
 																				metadata.getExcludeUnlistedClasses() );
 							addMetadataFromScan( mainJarScanCtx, metadata );
 
 							ScanningContext otherJarScanCtx = new ScanningContext()
 									.scanner( scanner )
 									.explicitMappingFiles( metadata.getMappingFiles() )
 									.searchOrm( true );
 							setDetectedArtifactsOnScanningContext( otherJarScanCtx, metadata.getProps(), integration,
 																				false );
 							for ( String jarFile : metadata.getJarFiles() ) {
 								otherJarScanCtx.url( JarVisitorFactory.getURLFromPath( jarFile ) );
 								addMetadataFromScan( otherJarScanCtx, metadata );
 							}
 							return configure( metadata, integration );
 						}
 					}
 				}
 			}
 			return null;
 		}
 		catch (Exception e) {
 			if ( e instanceof PersistenceException) {
 				throw (PersistenceException) e;
 			}
 			else {
 				throw new PersistenceException( getExceptionHeader() + "Unable to configure EntityManagerFactory", e );
 			}
 		}
 	}
 
 	private Scanner buildScanner(Properties properties, Map<?,?> integration) {
 		//read the String or Instance from the integration map first and use the properties as a backup.
 		Object scanner = integration.get( AvailableSettings.SCANNER );
 		if (scanner == null) {
 			scanner = properties.getProperty( AvailableSettings.SCANNER );
 		}
 		if (scanner != null) {
 			Class<?> scannerClass;
 			if ( scanner instanceof String ) {
 				try {
 					scannerClass = ReflectHelper.classForName( (String) scanner, this.getClass() );
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new PersistenceException(  "Cannot find scanner class. " + AvailableSettings.SCANNER + "=" + scanner, e );
 				}
 			}
 			else if (scanner instanceof Class) {
 				scannerClass = (Class<? extends Scanner>) scanner;
 			}
 			else if (scanner instanceof Scanner) {
 				return (Scanner) scanner;
 			}
 			else {
 				throw new PersistenceException(  "Scanner class configuration error: unknown type on the property. " + AvailableSettings.SCANNER );
 			}
 			try {
 				return (Scanner) scannerClass.newInstance();
 			}
 			catch ( InstantiationException e ) {
 				throw new PersistenceException(  "Unable to load Scanner class: " + scannerClass, e );
 			}
 			catch ( IllegalAccessException e ) {
 				throw new PersistenceException(  "Unable to load Scanner class: " + scannerClass, e );
 			}
 		}
 		else {
 			return new NativeScanner();
 		}
 	}
 
 	private static class ScanningContext {
 		//boolean excludeUnlistedClasses;
 		private Scanner scanner;
 		private URL url;
 		private List<String> explicitMappingFiles;
 		private boolean detectClasses;
 		private boolean detectHbmFiles;
 		private boolean searchOrm;
 
 		public ScanningContext scanner(Scanner scanner) {
 			this.scanner = scanner;
 			return this;
 		}
 
 		public ScanningContext url(URL url) {
 			this.url = url;
 			return this;
 		}
 
 		public ScanningContext explicitMappingFiles(List<String> explicitMappingFiles) {
 			this.explicitMappingFiles = explicitMappingFiles;
 			return this;
 		}
 
 		public ScanningContext detectClasses(boolean detectClasses) {
 			this.detectClasses = detectClasses;
 			return this;
 		}
 
 		public ScanningContext detectHbmFiles(boolean detectHbmFiles) {
 			this.detectHbmFiles = detectHbmFiles;
 			return this;
 		}
 
 		public ScanningContext searchOrm(boolean searchOrm) {
 			this.searchOrm = searchOrm;
 			return this;
 		}
 	}
 
 	private static void addMetadataFromScan(ScanningContext scanningContext, PersistenceMetadata metadata) throws IOException {
 		List<String> classes = metadata.getClasses();
 		List<String> packages = metadata.getPackages();
 		List<NamedInputStream> hbmFiles = metadata.getHbmfiles();
 		List<String> mappingFiles = metadata.getMappingFiles();
 		addScannedEntries( scanningContext, classes, packages, hbmFiles, mappingFiles );
 	}
 
 	private static void addScannedEntries(ScanningContext scanningContext, List<String> classes, List<String> packages, List<NamedInputStream> hbmFiles, List<String> mappingFiles) throws IOException {
 		Scanner scanner = scanningContext.scanner;
 		if (scanningContext.detectClasses) {
 			Set<Class<? extends Annotation>> annotationsToExclude = new HashSet<Class<? extends Annotation>>(3);
 			annotationsToExclude.add( Entity.class );
 			annotationsToExclude.add( MappedSuperclass.class );
 			annotationsToExclude.add( Embeddable.class );
 			Set<Class<?>> matchingClasses = scanner.getClassesInJar( scanningContext.url, annotationsToExclude );
 			for (Class<?> clazz : matchingClasses) {
 				classes.add( clazz.getName() );
 			}
 
 			Set<Package> matchingPackages = scanner.getPackagesInJar( scanningContext.url, new HashSet<Class<? extends Annotation>>(0) );
 			for (Package pkg : matchingPackages) {
 				packages.add( pkg.getName() );
 			}
 		}
 		Set<String> patterns = new HashSet<String>();
 		if (scanningContext.searchOrm) {
 			patterns.add( META_INF_ORM_XML );
 		}
 		if (scanningContext.detectHbmFiles) {
 			patterns.add( "**/*.hbm.xml" );
 		}
 		if ( mappingFiles != null) patterns.addAll( mappingFiles );
 		if (patterns.size() !=0) {
 			Set<NamedInputStream> files = scanner.getFilesInJar( scanningContext.url, patterns );
 			for (NamedInputStream file : files) {
 				hbmFiles.add( file );
 				if (mappingFiles != null) mappingFiles.remove( file.getName() );
 			}
 		}
 	}
 
 	/**
 	 * Process configuration from a PersistenceUnitInfo object; typically called by the container
 	 * via {@link javax.persistence.spi.PersistenceProvider#createContainerEntityManagerFactory}.
 	 * In Hibernate EM, this correlates to {@link HibernatePersistence#createContainerEntityManagerFactory}
 	 *
 	 * @param info The persistence unit info passed in by the container (usually from processing a persistence.xml).
 	 * @param integration The map of integration properties from the container to configure the provider.
 	 *
 	 * @return this
 	 *
 	 * @see HibernatePersistence#createContainerEntityManagerFactory
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Ejb3Configuration configure(PersistenceUnitInfo info, Map integration) {
         if (LOG.isDebugEnabled()) LOG.debugf("Processing %s", LogHelper.logPersistenceUnitInfo(info));
         else LOG.processingPersistenceUnitInfoName(info.getPersistenceUnitName());
 
 		// Spec says the passed map may be null, so handle that to make further processing easier...
 		integration = integration != null ? Collections.unmodifiableMap( integration ) : CollectionHelper.EMPTY_MAP;
 
 		// See if we (Hibernate) are the persistence provider
 		String provider = (String) integration.get( AvailableSettings.PROVIDER );
 		if ( provider == null ) {
 			provider = info.getPersistenceProviderClassName();
 		}
 		if ( provider != null && ! provider.trim().startsWith( IMPLEMENTATION_NAME ) ) {
             LOG.requiredDifferentProvider(provider);
 			return null;
 		}
 
 		// set the classloader, passed in by the container in info, to set as the TCCL so that
 		// Hibernate uses it to properly resolve class references.
 		if ( info.getClassLoader() == null ) {
 			throw new IllegalStateException(
 					"[PersistenceUnit: " + info.getPersistenceUnitName() == null ? "" : info.getPersistenceUnitName()
 							+ "] " + "PersistenceUnitInfo.getClassLoader() id null" );
 		}
 		Thread thread = Thread.currentThread();
 		ClassLoader contextClassLoader = thread.getContextClassLoader();
 		boolean sameClassLoader = info.getClassLoader().equals( contextClassLoader );
 		if ( ! sameClassLoader ) {
 			overridenClassLoader = info.getClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		else {
 			overridenClassLoader = null;
 		}
 
 		// Best I can tell, 'workingVars' is some form of additional configuration contract.
 		// But it does not correlate 1-1 to EMF/SF settings.  It really is like a set of de-typed
 		// additional configuration info.  I think it makes better sense to define this as an actual
 		// contract if that was in fact the intent; the code here is pretty confusing.
 		try {
 			Map workingVars = new HashMap();
 			workingVars.put( AvailableSettings.PERSISTENCE_UNIT_NAME, info.getPersistenceUnitName() );
 			this.persistenceUnitName = info.getPersistenceUnitName();
 			List<String> entities = new ArrayList<String>( 50 );
 			if ( info.getManagedClassNames() != null ) entities.addAll( info.getManagedClassNames() );
 			List<NamedInputStream> hbmFiles = new ArrayList<NamedInputStream>();
 			List<String> packages = new ArrayList<String>();
 			List<String> xmlFiles = new ArrayList<String>( 50 );
 			List<XmlDocument> xmlDocuments = new ArrayList<XmlDocument>( 50 );
 			if ( info.getMappingFileNames() != null ) {
 				xmlFiles.addAll( info.getMappingFileNames() );
 			}
 			//Should always be true if the container is not dump
 			boolean searchForORMFiles = ! xmlFiles.contains( META_INF_ORM_XML );
 
 			ScanningContext context = new ScanningContext();
 			final Properties copyOfProperties = (Properties) info.getProperties().clone();
 			ConfigurationHelper.overrideProperties( copyOfProperties, integration );
 			context.scanner( buildScanner( copyOfProperties, integration ) )
 					.searchOrm( searchForORMFiles )
 					.explicitMappingFiles( null ); //URLs provided by the container already
 
 			//context for other JARs
 			setDetectedArtifactsOnScanningContext(context, info.getProperties(), null, false );
 			for ( URL jar : info.getJarFileUrls() ) {
 				context.url(jar);
 				scanForClasses( context, packages, entities, hbmFiles );
 			}
 
 			//main jar
 			context.url( info.getPersistenceUnitRootUrl() );
 			setDetectedArtifactsOnScanningContext( context, info.getProperties(), null, info.excludeUnlistedClasses() );
 			scanForClasses( context, packages, entities, hbmFiles );
 
 			Properties properties = info.getProperties() != null ? info.getProperties() : new Properties();
 			ConfigurationHelper.overrideProperties( properties, integration );
 
 			//FIXME entities is used to enhance classes and to collect annotated entities this should not be mixed
 			//fill up entities with the on found in xml files
 			addXMLEntities( xmlFiles, info, entities, xmlDocuments );
 
 			//FIXME send the appropriate entites.
 			if ( "true".equalsIgnoreCase( properties.getProperty( AvailableSettings.USE_CLASS_ENHANCER ) ) ) {
 				info.addTransformer( new InterceptFieldClassFileTransformer( entities ) );
 			}
 
 			workingVars.put( AvailableSettings.CLASS_NAMES, entities );
 			workingVars.put( AvailableSettings.PACKAGE_NAMES, packages );
 			workingVars.put( AvailableSettings.XML_FILE_NAMES, xmlFiles );
 			workingVars.put( PARSED_MAPPING_DOMS, xmlDocuments );
 
 			if ( hbmFiles.size() > 0 ) {
 				workingVars.put( AvailableSettings.HBXML_FILES, hbmFiles );
 			}
 
 			// validation factory
 			final Object validationFactory = integration.get( AvailableSettings.VALIDATION_FACTORY );
 			if ( validationFactory != null ) {
 				properties.put( AvailableSettings.VALIDATION_FACTORY, validationFactory );
 			}
 
 			// validation-mode (integration has precedence)
 			{
 				final Object integrationValue = integration.get( AvailableSettings.VALIDATION_MODE );
 				if ( integrationValue != null ) {
 					properties.put( AvailableSettings.VALIDATION_MODE, integrationValue.toString() );
 				}
 				else if ( info.getValidationMode() != null ) {
 					properties.put( AvailableSettings.VALIDATION_MODE, info.getValidationMode().name() );
 				}
 			}
 
 			// shared-cache-mode (integration has precedence)
 			{
 				final Object integrationValue = integration.get( AvailableSettings.SHARED_CACHE_MODE );
 				if ( integrationValue != null ) {
 					properties.put( AvailableSettings.SHARED_CACHE_MODE, integrationValue.toString() );
 				}
 				else if ( info.getSharedCacheMode() != null ) {
 					properties.put( AvailableSettings.SHARED_CACHE_MODE, info.getSharedCacheMode().name() );
 				}
 			}
 
 			//datasources
 			Boolean isJTA = null;
 			boolean overridenDatasource = false;
 			if ( integration.containsKey( AvailableSettings.JTA_DATASOURCE ) ) {
 				String dataSource = (String) integration.get( AvailableSettings.JTA_DATASOURCE );
 				overridenDatasource = true;
 				properties.setProperty( Environment.DATASOURCE, dataSource );
 				isJTA = Boolean.TRUE;
 			}
 			if ( integration.containsKey( AvailableSettings.NON_JTA_DATASOURCE ) ) {
 				String dataSource = (String) integration.get( AvailableSettings.NON_JTA_DATASOURCE );
 				overridenDatasource = true;
 				properties.setProperty( Environment.DATASOURCE, dataSource );
 				if (isJTA == null) isJTA = Boolean.FALSE;
 			}
 
 			if ( ! overridenDatasource && ( info.getJtaDataSource() != null || info.getNonJtaDataSource() != null ) ) {
 				isJTA = info.getJtaDataSource() != null ? Boolean.TRUE : Boolean.FALSE;
 				this.setDataSource(
 						isJTA ? info.getJtaDataSource() : info.getNonJtaDataSource()
 				);
 				this.setProperty(
 						Environment.CONNECTION_PROVIDER, InjectedDataSourceConnectionProvider.class.getName()
 				);
 			}
 			/*
 			 * If explicit type => use it
 			 * If a JTA DS is used => JTA transaction,
 			 * if a non JTA DS is used => RESOURCe_LOCAL
 			 * if none, set to JavaEE default => JTA transaction
 			 */
 			PersistenceUnitTransactionType transactionType = info.getTransactionType();
 			if (transactionType == null) {
 				if (isJTA == Boolean.TRUE) {
 					transactionType = PersistenceUnitTransactionType.JTA;
 				}
 				else if ( isJTA == Boolean.FALSE ) {
 					transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
 				}
 				else {
 					transactionType = PersistenceUnitTransactionType.JTA;
 				}
 			}
 			defineTransactionType( transactionType, workingVars );
 			configure( properties, workingVars );
 		}
 		finally {
 			//After EMF, set the CCL back
 			if ( ! sameClassLoader ) {
 				thread.setContextClassLoader( contextClassLoader );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Processes {@code xmlFiles} argument and populates:<ul>
 	 * <li>the {@code entities} list with encountered classnames</li>
 	 * <li>the {@code xmlDocuments} list with parsed/validated {@link XmlDocument} corrolary to each xml file</li>
 	 * </ul>
 	 *
 	 * @param xmlFiles The XML resource names; these will be resolved by classpath lookup and parsed/validated.
 	 * @param info The PUI
 	 * @param entities (output) The names of all encountered "mapped" classes
 	 * @param xmlDocuments (output) The list of {@link XmlDocument} instances of each entry in {@code xmlFiles}
 	 */
 	@SuppressWarnings({ "unchecked" })
 	private void addXMLEntities(
 			List<String> xmlFiles,
 			PersistenceUnitInfo info,
 			List<String> entities,
 			List<XmlDocument> xmlDocuments) {
 		//TODO handle inputstream related hbm files
 		ClassLoader classLoaderToUse = info.getNewTempClassLoader();
 		if ( classLoaderToUse == null ) {
             LOG.persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly();
 			return;
 		}
 		for ( final String xmlFile : xmlFiles ) {
 			final InputStream fileInputStream = classLoaderToUse.getResourceAsStream( xmlFile );
 			if ( fileInputStream == null ) {
                 LOG.unableToResolveMappingFile(xmlFile);
 				continue;
 			}
 			final InputSource inputSource = new InputSource( fileInputStream );
 
 			XmlDocument metadataXml = MappingReader.INSTANCE.readMappingDocument(
 					cfg.getEntityResolver(),
 					inputSource,
 					new OriginImpl( "persistence-unit-info", xmlFile )
 			);
 			xmlDocuments.add( metadataXml );
 			try {
 				final Element rootElement = metadataXml.getDocumentTree().getRootElement();
 				if ( rootElement != null && "entity-mappings".equals( rootElement.getName() ) ) {
 					Element element = rootElement.element( "package" );
 					String defaultPackage = element != null ? element.getTextTrim() : null;
 					List<Element> elements = rootElement.elements( "entity" );
 					for (Element subelement : elements ) {
 						String classname = XMLContext.buildSafeClassName( subelement.attributeValue( "class" ), defaultPackage );
 						if ( ! entities.contains( classname ) ) {
 							entities.add( classname );
 						}
 					}
 					elements = rootElement.elements( "mapped-superclass" );
 					for (Element subelement : elements ) {
 						String classname = XMLContext.buildSafeClassName( subelement.attributeValue( "class" ), defaultPackage );
 						if ( ! entities.contains( classname ) ) {
 							entities.add( classname );
 						}
 					}
 					elements = rootElement.elements( "embeddable" );
 					for (Element subelement : elements ) {
 						String classname = XMLContext.buildSafeClassName( subelement.attributeValue( "class" ), defaultPackage );
 						if ( ! entities.contains( classname ) ) {
 							entities.add( classname );
 						}
 					}
 				}
 				else if ( rootElement != null && "hibernate-mappings".equals( rootElement.getName() ) ) {
 					//FIXME include hbm xml entities to enhance them but entities is also used to collect annotated entities
 				}
 			}
 			finally {
 				try {
 					fileInputStream.close();
 				}
 				catch (IOException ioe) {
                     LOG.unableToCloseInputStream(ioe);
 				}
 			}
 		}
 		xmlFiles.clear();
 	}
 
 	private void defineTransactionType(Object overridenTxType, Map workingVars) {
 		if ( overridenTxType == null ) {
 //			if ( transactionType == null ) {
 //				transactionType = PersistenceUnitTransactionType.JTA; //this is the default value
 //			}
 			//nothing to override
 		}
 		else if ( overridenTxType instanceof String ) {
 			transactionType = PersistenceXmlLoader.getTransactionType( (String) overridenTxType );
 		}
 		else if ( overridenTxType instanceof PersistenceUnitTransactionType ) {
 			transactionType = (PersistenceUnitTransactionType) overridenTxType;
 		}
 		else {
 			throw new PersistenceException( getExceptionHeader() +
 					AvailableSettings.TRANSACTION_TYPE + " of the wrong class type"
 							+ ": " + overridenTxType.getClass()
 			);
 		}
 
 	}
 
 	public Ejb3Configuration setProperty(String key, String value) {
 		cfg.setProperty( key, value );
 		return this;
 	}
 
 	/**
 	 * Set ScanningContext detectClasses and detectHbmFiles according to context
 	 */
 	private void setDetectedArtifactsOnScanningContext(ScanningContext context,
 													   Properties properties,
 													   Map overridenProperties,
 													   boolean excludeIfNotOverriden) {
 
 		boolean detectClasses = false;
 		boolean detectHbm = false;
 		String detectSetting = overridenProperties != null ?
 				(String) overridenProperties.get( AvailableSettings.AUTODETECTION ) :
 				null;
 		detectSetting = detectSetting == null ?
 				properties.getProperty( AvailableSettings.AUTODETECTION) :
 				detectSetting;
 		if ( detectSetting == null && excludeIfNotOverriden) {
 			//not overriden through HibernatePersistence.AUTODETECTION so we comply with the spec excludeUnlistedClasses
 			context.detectClasses( false ).detectHbmFiles( false );
 			return;
 		}
 
 		if ( detectSetting == null){
 			detectSetting = "class,hbm";
 		}
 		StringTokenizer st = new StringTokenizer( detectSetting, ", ", false );
 		while ( st.hasMoreElements() ) {
 			String element = (String) st.nextElement();
 			if ( "class".equalsIgnoreCase( element ) ) detectClasses = true;
 			if ( "hbm".equalsIgnoreCase( element ) ) detectHbm = true;
 		}
         LOG.debugf("Detect class: %s; detect hbm: %s", detectClasses, detectHbm);
 		context.detectClasses( detectClasses ).detectHbmFiles( detectHbm );
 	}
 
 	private void scanForClasses(ScanningContext scanningContext, List<String> packages, List<String> entities, List<NamedInputStream> hbmFiles) {
 		if (scanningContext.url == null) {
             LOG.containerProvidingNullPersistenceUnitRootUrl();
 			return;
 		}
 		try {
 			addScannedEntries( scanningContext, entities, packages, hbmFiles, null );
 		}
 		catch (RuntimeException e) {
 			throw new RuntimeException( "error trying to scan <jar-file>: " + scanningContext.url.toString(), e );
 		}
 		catch( IOException e ) {
 			throw new RuntimeException( "Error while reading " + scanningContext.url.toString(), e );
 		}
 	}
 
 	/**
 	 * create a factory from a list of properties and
 	 * HibernatePersistence.CLASS_NAMES -> Collection<String> (use to list the classes from config files
 	 * HibernatePersistence.PACKAGE_NAMES -> Collection<String> (use to list the mappings from config files
 	 * HibernatePersistence.HBXML_FILES -> Collection<InputStream> (input streams of hbm files)
 	 * HibernatePersistence.LOADED_CLASSES -> Collection<Class> (list of loaded classes)
 	 * <p/>
 	 * <b>Used by JBoss AS only</b>
 	 * @deprecated use the Java Persistence API
 	 */
 	// This is used directly by JBoss so don't remove until further notice.  bill@jboss.org
 	@Deprecated
     public EntityManagerFactory createEntityManagerFactory(Map workingVars) {
 		configure( workingVars );
 		return buildEntityManagerFactory();
 	}
 
 	/**
 	 * Process configuration and build an EntityManagerFactory <b>when</b> the configuration is ready
 	 * @deprecated
 	 */
 	@Deprecated
     public EntityManagerFactory createEntityManagerFactory() {
 		configure( cfg.getProperties(), new HashMap() );
 		return buildEntityManagerFactory();
 	}
 
 	public EntityManagerFactory buildEntityManagerFactory() {
 		return buildEntityManagerFactory( new BasicServiceRegistryImpl( cfg.getProperties() ) );
 	}
 
 	public EntityManagerFactory buildEntityManagerFactory(ServiceRegistry serviceRegistry) {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			configure( (Properties)null, null );
 			NamingHelper.bind(this);
+			// todo : temporary -> HHH-5562
+			new JpaEventListenerRegistration().apply( serviceRegistry, cfg.createMappings(), cfg.getProperties() );
 			return new EntityManagerFactoryImpl(
 					transactionType,
 					discardOnClose,
 					getSessionInterceptorClass( cfg.getProperties() ),
 					cfg,
 					serviceRegistry
 			);
 		}
 		catch (HibernateException e) {
 			throw new PersistenceException( getExceptionHeader() + "Unable to build EntityManagerFactory", e );
 		}
 		finally {
 			if (thread != null) {
 				thread.setContextClassLoader( contextClassLoader );
 			}
 		}
 	}
 
 	private Class getSessionInterceptorClass(Properties properties) {
 		String sessionInterceptorClassname = (String) properties.get( AvailableSettings.SESSION_INTERCEPTOR );
 		if ( StringHelper.isNotEmpty( sessionInterceptorClassname ) ) {
 			try {
 				Class interceptorClass = ReflectHelper.classForName(
 						sessionInterceptorClassname, Ejb3Configuration.class
 				);
 				interceptorClass.newInstance();
 				return interceptorClass;
 			}
 			catch (ClassNotFoundException e) {
 				throw new PersistenceException( getExceptionHeader() + "Unable to load "
 						+ AvailableSettings.SESSION_INTERCEPTOR + ": " + sessionInterceptorClassname, e);
 			}
 			catch (IllegalAccessException e) {
 				throw new PersistenceException( getExceptionHeader() + "Unable to instanciate "
 						+ AvailableSettings.SESSION_INTERCEPTOR + ": " + sessionInterceptorClassname, e);
 			}
 			catch (InstantiationException e) {
 				throw new PersistenceException( getExceptionHeader() + "Unable to instanciate "
 						+ AvailableSettings.SESSION_INTERCEPTOR + ": " + sessionInterceptorClassname, e);
 			}
         }
         return null;
 	}
 
 	public Reference getReference() throws NamingException {
-        LOG.debugf("Returning a Reference to the Ejb3Configuration");
+        LOG.debugf( "Returning a Reference to the Ejb3Configuration" );
 		ByteArrayOutputStream stream = new ByteArrayOutputStream();
 		ObjectOutput out = null;
 		byte[] serialized;
 		try {
 			out = new ObjectOutputStream( stream );
 			out.writeObject( this );
 			out.close();
 			serialized = stream.toByteArray();
 			stream.close();
 		}
 		catch (IOException e) {
 			NamingException namingException = new NamingException( "Unable to serialize Ejb3Configuration" );
 			namingException.setRootCause( e );
 			throw namingException;
 		}
 
 		return new Reference(
 				Ejb3Configuration.class.getName(),
 				new BinaryRefAddr("object", serialized ),
 				Ejb3ConfigurationObjectFactory.class.getName(),
 				null
 		);
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public Ejb3Configuration configure(Map configValues) {
 		Properties props = new Properties();
 		if ( configValues != null ) {
 			props.putAll( configValues );
 			//remove huge non String elements for a clean props
 			props.remove( AvailableSettings.CLASS_NAMES );
 			props.remove( AvailableSettings.PACKAGE_NAMES );
 			props.remove( AvailableSettings.HBXML_FILES );
 			props.remove( AvailableSettings.LOADED_CLASSES );
 		}
 		return configure( props, configValues );
 	}
 
 	/**
 	 * Configures this configuration object from 2 distinctly different sources.
 	 *
 	 * @param properties These are the properties that came from the user, either via
 	 * a persistence.xml or explicitly passed in to one of our
 	 * {@link javax.persistence.spi.PersistenceProvider}/{@link HibernatePersistence} contracts.
 	 * @param workingVars Is collection of settings which need to be handled similarly
 	 * between the 2 main bootstrap methods, but where the values are determine very differently
 	 * by each bootstrap method.  todo eventually make this a contract (class/interface)
 	 *
 	 * @return The configured configuration
 	 *
 	 * @see HibernatePersistence
 	 */
 	private Ejb3Configuration configure(Properties properties, Map workingVars) {
 		//TODO check for people calling more than once this method (except buildEMF)
 		if (isConfigurationProcessed) return this;
 		isConfigurationProcessed = true;
 		Properties preparedProperties = prepareProperties( properties, workingVars );
 		if ( workingVars == null ) workingVars = CollectionHelper.EMPTY_MAP;
 
 		if ( preparedProperties.containsKey( AvailableSettings.CFG_FILE ) ) {
 			String cfgFileName = preparedProperties.getProperty( AvailableSettings.CFG_FILE );
 			cfg.configure( cfgFileName );
 		}
 
 		cfg.addProperties( preparedProperties ); //persistence.xml has priority over hibernate.cfg.xml
 
 		addClassesToSessionFactory( workingVars );
 
 		//processes specific properties
 		List<String> jaccKeys = new ArrayList<String>();
 
 
 		Interceptor defaultInterceptor = DEFAULT_CONFIGURATION.getInterceptor();
 		NamingStrategy defaultNamingStrategy = DEFAULT_CONFIGURATION.getNamingStrategy();
 
 		Iterator propertyIt = preparedProperties.keySet().iterator();
 		while ( propertyIt.hasNext() ) {
 			Object uncastObject = propertyIt.next();
 			//had to be safe
 			if ( uncastObject != null && uncastObject instanceof String ) {
 				String propertyKey = (String) uncastObject;
 				if ( propertyKey.startsWith( AvailableSettings.CLASS_CACHE_PREFIX ) ) {
 					setCacheStrategy( propertyKey, preparedProperties, true, workingVars );
 				}
 				else if ( propertyKey.startsWith( AvailableSettings.COLLECTION_CACHE_PREFIX ) ) {
 					setCacheStrategy( propertyKey, preparedProperties, false, workingVars );
 				}
 				else if ( propertyKey.startsWith( AvailableSettings.JACC_PREFIX )
 						&& ! ( propertyKey.equals( AvailableSettings.JACC_CONTEXT_ID )
 						|| propertyKey.equals( AvailableSettings.JACC_ENABLED ) ) ) {
 					jaccKeys.add( propertyKey );
 				}
 			}
 		}
 		final Interceptor interceptor = instantiateCustomClassFromConfiguration(
 				preparedProperties,
 				defaultInterceptor,
 				cfg.getInterceptor(),
 				AvailableSettings.INTERCEPTOR,
 				"interceptor",
 				Interceptor.class
 		);
 		if ( interceptor != null ) {
 			cfg.setInterceptor( interceptor );
 		}
 		final NamingStrategy namingStrategy = instantiateCustomClassFromConfiguration(
 				preparedProperties,
 				defaultNamingStrategy,
 				cfg.getNamingStrategy(),
 				AvailableSettings.NAMING_STRATEGY,
 				"naming strategy",
 				NamingStrategy.class
 		);
 		if ( namingStrategy != null ) {
 			cfg.setNamingStrategy( namingStrategy );
 		}
 
 		final SessionFactoryObserver observer = instantiateCustomClassFromConfiguration(
 				preparedProperties,
 				null,
 				cfg.getSessionFactoryObserver(),
 				AvailableSettings.SESSION_FACTORY_OBSERVER,
 				"SessionFactory observer",
 				SessionFactoryObserver.class
 		);
 		if ( observer != null ) {
 			cfg.setSessionFactoryObserver( observer );
 		}
 
 		if ( jaccKeys.size() > 0 ) {
 			addSecurity( jaccKeys, preparedProperties, workingVars );
 		}
 
-		//initialize listeners
-		listenerConfigurator.setProperties( preparedProperties );
-		listenerConfigurator.configure();
-
 		//some spec compliance checking
 		//TODO centralize that?
         if (!"true".equalsIgnoreCase(cfg.getProperty(Environment.AUTOCOMMIT))) LOG.jdbcAutoCommitFalseBreaksEjb3Spec(Environment.AUTOCOMMIT);
         discardOnClose = preparedProperties.getProperty(AvailableSettings.DISCARD_PC_ON_CLOSE).equals("true");
 		return this;
 	}
 
 	private <T> T instantiateCustomClassFromConfiguration(
 			Properties preparedProperties,
 			T defaultObject,
 			T cfgObject,
 			String propertyName,
 			String classDescription,
 			Class<T> objectClass) {
 		if ( preparedProperties.containsKey( propertyName )
 				&& ( cfgObject == null || cfgObject.equals( defaultObject ) ) ) {
 			//cfg.setXxx has precedence over configuration file
 			String className = preparedProperties.getProperty( propertyName );
 			try {
 				Class<T> clazz = classForName( className );
 				return clazz.newInstance();
 				//cfg.setInterceptor( (Interceptor) instance.newInstance() );
 			}
 			catch (ClassNotFoundException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to find " + classDescription + " class: " + className, e
 				);
 			}
 			catch (IllegalAccessException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to access " + classDescription + " class: " + className, e
 				);
 			}
 			catch (InstantiationException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + "Unable to instantiate " + classDescription + " class: " + className, e
 				);
 			}
 			catch (ClassCastException e) {
 				throw new PersistenceException(
 						getExceptionHeader() + classDescription + " class does not implement " + objectClass + " interface: "
 								+ className, e
 				);
 			}
 		}
 		return null;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void addClassesToSessionFactory(Map workingVars) {
 		if ( workingVars.containsKey( AvailableSettings.CLASS_NAMES ) ) {
 			Collection<String> classNames = (Collection<String>) workingVars.get(
 					AvailableSettings.CLASS_NAMES
 			);
 			addNamedAnnotatedClasses( this, classNames, workingVars );
 		}
 
 		if ( workingVars.containsKey( PARSED_MAPPING_DOMS ) ) {
 			Collection<XmlDocument> xmlDocuments = (Collection<XmlDocument>) workingVars.get( PARSED_MAPPING_DOMS );
 			for ( XmlDocument xmlDocument : xmlDocuments ) {
 				cfg.add( xmlDocument );
 			}
 		}
 
 		//TODO apparently only used for Tests, get rid of it?
 		if ( workingVars.containsKey( AvailableSettings.LOADED_CLASSES ) ) {
 			Collection<Class> classes = (Collection<Class>) workingVars.get( AvailableSettings.LOADED_CLASSES );
 			for ( Class clazz : classes ) {
 				cfg.addAnnotatedClass( clazz );
 			}
 		}
 		if ( workingVars.containsKey( AvailableSettings.PACKAGE_NAMES ) ) {
 			Collection<String> packages = (Collection<String>) workingVars.get(
 					AvailableSettings.PACKAGE_NAMES
 			);
 			for ( String pkg : packages ) {
 				cfg.addPackage( pkg );
 			}
 		}
 		if ( workingVars.containsKey( AvailableSettings.XML_FILE_NAMES ) ) {
 			Collection<String> xmlFiles = (Collection<String>) workingVars.get( AvailableSettings.XML_FILE_NAMES );
 			for ( String xmlFile : xmlFiles ) {
 				Boolean useMetaInf = null;
 				try {
 					if ( xmlFile.endsWith( META_INF_ORM_XML ) ) {
 						useMetaInf = true;
 					}
 					cfg.addResource( xmlFile );
 				}
 				catch( MappingNotFoundException e ) {
 					if ( ! xmlFile.endsWith( META_INF_ORM_XML ) ) {
 						throw new PersistenceException( getExceptionHeader()
 								+ "Unable to find XML mapping file in classpath: " + xmlFile);
 					}
 					else {
 						useMetaInf = false;
 						//swallow it, the META-INF/orm.xml is optional
 					}
 				}
 				catch( MappingException me ) {
 					throw new PersistenceException( getExceptionHeader()
 								+ "Error while reading JPA XML file: " + xmlFile, me);
 				}
                 if (Boolean.TRUE.equals(useMetaInf)) {
 					LOG.exceptionHeaderFound(getExceptionHeader(), META_INF_ORM_XML);
 				}
                 else if (Boolean.FALSE.equals(useMetaInf)) {
 					LOG.exceptionHeaderNotFound(getExceptionHeader(), META_INF_ORM_XML);
 				}
 			}
 		}
 		if ( workingVars.containsKey( AvailableSettings.HBXML_FILES ) ) {
 			Collection<NamedInputStream> hbmXmlFiles = (Collection<NamedInputStream>) workingVars.get(
 					AvailableSettings.HBXML_FILES
 			);
 			for ( NamedInputStream is : hbmXmlFiles ) {
 				try {
 					//addInputStream has the responsibility to close the stream
 					cfg.addInputStream( new BufferedInputStream( is.getStream() ) );
 				}
 				catch (MappingException me) {
 					//try our best to give the file name
 					if ( StringHelper.isEmpty( is.getName() ) ) {
 						throw me;
 					}
 					else {
 						throw new MappingException("Error while parsing file: " + is.getName(), me );
 					}
 				}
 			}
 		}
 	}
 
 	private String getExceptionHeader() {
         return (StringHelper.isNotEmpty(persistenceUnitName)) ? "[PersistenceUnit: " + persistenceUnitName + "] " : "";
 	}
 
 	private Properties prepareProperties(Properties properties, Map workingVars) {
 		Properties preparedProperties = new Properties();
 
 		//defaults different from Hibernate
 		preparedProperties.setProperty( Environment.RELEASE_CONNECTIONS, "auto" );
 		preparedProperties.setProperty( Environment.JPAQL_STRICT_COMPLIANCE, "true" );
 		//settings that always apply to a compliant EJB3
 		preparedProperties.setProperty( Environment.AUTOCOMMIT, "true" );
 		preparedProperties.setProperty( Environment.USE_IDENTIFIER_ROLLBACK, "false" );
 		preparedProperties.setProperty( Environment.FLUSH_BEFORE_COMPLETION, "false" );
 		preparedProperties.setProperty( AvailableSettings.DISCARD_PC_ON_CLOSE, "false" );
 		if (cfgXmlResource != null) {
 			preparedProperties.setProperty( AvailableSettings.CFG_FILE, cfgXmlResource );
 			cfgXmlResource = null;
 		}
 
 		//override the new defaults with the user defined ones
 		//copy programmatically defined properties
 		if ( cfg.getProperties() != null ) preparedProperties.putAll( cfg.getProperties() );
 		//copy them coping from configuration
 		if ( properties != null ) preparedProperties.putAll( properties );
 		//note we don't copy cfg.xml properties, since they have to be overriden
 
 		if (transactionType == null) {
 			//if it has not been set, the user use a programmatic way
 			transactionType = PersistenceUnitTransactionType.RESOURCE_LOCAL;
 		}
 		defineTransactionType(
 				preparedProperties.getProperty( AvailableSettings.TRANSACTION_TYPE ),
 				workingVars
 		);
 		boolean hasTxStrategy = StringHelper.isNotEmpty(
 				preparedProperties.getProperty( Environment.TRANSACTION_STRATEGY )
 		);
 		if ( ! hasTxStrategy && transactionType == PersistenceUnitTransactionType.JTA ) {
 			preparedProperties.setProperty(
 					Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class.getName()
 			);
 		}
 		else if ( ! hasTxStrategy && transactionType == PersistenceUnitTransactionType.RESOURCE_LOCAL ) {
 			preparedProperties.setProperty( Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class.getName() );
 		}
         if (hasTxStrategy) LOG.overridingTransactionStrategyDangerous(Environment.TRANSACTION_STRATEGY);
 		if ( preparedProperties.getProperty( Environment.FLUSH_BEFORE_COMPLETION ).equals( "true" ) ) {
 			preparedProperties.setProperty( Environment.FLUSH_BEFORE_COMPLETION, "false" );
             LOG.definingFlushBeforeCompletionIgnoredInHem(Environment.FLUSH_BEFORE_COMPLETION);
 		}
 		return preparedProperties;
 	}
 
 	private Class classForName(String className) throws ClassNotFoundException {
 		return ReflectHelper.classForName( className, this.getClass() );
 	}
 
 	private void setCacheStrategy(String propertyKey, Map properties, boolean isClass, Map workingVars) {
 		String role = propertyKey.substring(
 				( isClass ? AvailableSettings.CLASS_CACHE_PREFIX
 						.length() : AvailableSettings.COLLECTION_CACHE_PREFIX.length() )
 						+ 1
 		);
 		//dot size added
 		String value = (String) properties.get( propertyKey );
 		StringTokenizer params = new StringTokenizer( value, ";, " );
 		if ( !params.hasMoreTokens() ) {
 			StringBuilder error = new StringBuilder( "Illegal usage of " );
 			error.append(
 					isClass ? AvailableSettings.CLASS_CACHE_PREFIX : AvailableSettings.COLLECTION_CACHE_PREFIX
 			);
 			error.append( ": " ).append( propertyKey ).append( " " ).append( value );
 			throw new PersistenceException( getExceptionHeader() + error.toString() );
 		}
 		String usage = params.nextToken();
 		String region = null;
 		if ( params.hasMoreTokens() ) {
 			region = params.nextToken();
 		}
 		if ( isClass ) {
 			boolean lazyProperty = true;
 			if ( params.hasMoreTokens() ) {
 				lazyProperty = "all".equalsIgnoreCase( params.nextToken() );
 			}
 			cfg.setCacheConcurrencyStrategy( role, usage, region, lazyProperty );
 		}
 		else {
 			cfg.setCollectionCacheConcurrencyStrategy( role, usage, region );
 		}
 	}
 
 	private void addSecurity(List<String> keys, Map properties, Map workingVars) {
         LOG.debugf("Adding security");
 		if ( !properties.containsKey( AvailableSettings.JACC_CONTEXT_ID ) ) {
 			throw new PersistenceException( getExceptionHeader() +
 					"Entities have been configured for JACC, but "
 							+ AvailableSettings.JACC_CONTEXT_ID
 							+ " has not been set"
 			);
 		}
 		String contextId = (String) properties.get( AvailableSettings.JACC_CONTEXT_ID );
 		setProperty( Environment.JACC_CONTEXTID, contextId );
 
 		int roleStart = AvailableSettings.JACC_PREFIX.length() + 1;
 
 		for ( String key : keys ) {
 			JACCConfiguration jaccCfg = new JACCConfiguration( contextId );
 			try {
 				String role = key.substring( roleStart, key.indexOf( '.', roleStart ) );
 				int classStart = roleStart + role.length() + 1;
 				String clazz = key.substring( classStart, key.length() );
 				String actions = (String) properties.get( key );
 				jaccCfg.addPermission( role, clazz, actions );
 			}
 			catch (IndexOutOfBoundsException e) {
 				throw new PersistenceException( getExceptionHeader() +
 						"Illegal usage of " + AvailableSettings.JACC_PREFIX + ": " + key );
 			}
 		}
 	}
 
 	private void addNamedAnnotatedClasses(
 			Ejb3Configuration cfg, Collection<String> classNames, Map workingVars
 	) {
 		for ( String name : classNames ) {
 			try {
 				Class clazz = classForName( name );
 				cfg.addAnnotatedClass( clazz );
 			}
 			catch (ClassNotFoundException cnfe) {
 				Package pkg;
 				try {
 					pkg = classForName( name + ".package-info" ).getPackage();
 				}
 				catch (ClassNotFoundException e) {
 					pkg = null;
 				}
                 if (pkg == null) throw new PersistenceException(getExceptionHeader() + "class or package not found", cnfe);
                 else cfg.addPackage(name);
 			}
 		}
 	}
 
-	/*
-		TODO: not needed any more?
-	public Settings buildSettings(ConnectionProvider connectionProvider) throws HibernateException {
-		Thread thread = null;
-		ClassLoader contextClassLoader = null;
-		if (overridenClassLoader != null) {
-			thread = Thread.currentThread();
-			contextClassLoader = thread.getContextClassLoader();
-			thread.setContextClassLoader( overridenClassLoader );
-		}
-		try {
-			return settingsFactory.buildSettings( cfg.getProperties(), connectionProvider );
-		}
-		finally {
-			if (thread != null) thread.setContextClassLoader( contextClassLoader );
-		}
-	}
-	*/
-
 	public Ejb3Configuration addProperties(Properties props) {
 		cfg.addProperties( props );
 		return this;
 	}
 
 	public Ejb3Configuration addAnnotatedClass(Class persistentClass) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addAnnotatedClass( persistentClass );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration configure(String resource) throws HibernateException {
 		//delay the call to configure to allow proper addition of all annotated classes (EJB-330)
 		if (cfgXmlResource != null)
 			throw new PersistenceException("configure(String) method already called for " + cfgXmlResource);
 		this.cfgXmlResource = resource;
 		return this;
 	}
 
 	public Ejb3Configuration addPackage(String packageName) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addPackage( packageName );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addFile(String xmlFile) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addFile( xmlFile );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addClass(Class persistentClass) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addClass( persistentClass );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addFile(File xmlFile) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addFile( xmlFile );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public void buildMappings() {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.buildMappings();
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Iterator getClassMappings() {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			return cfg.getClassMappings();
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
-	public EventListeners getEventListeners() {
-		return cfg.getEventListeners();
-	}
-
-	SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
-		return cfg.buildSessionFactory( serviceRegistry );
-	}
-
 	public Iterator getTableMappings() {
 		return cfg.getTableMappings();
 	}
 
 	public PersistentClass getClassMapping(String persistentClass) {
 		return cfg.getClassMapping( persistentClass );
 	}
 
 	public org.hibernate.mapping.Collection getCollectionMapping(String role) {
 		return cfg.getCollectionMapping( role );
 	}
 
 	public void setEntityResolver(EntityResolver entityResolver) {
 		cfg.setEntityResolver( entityResolver );
 	}
 
 	public Map getNamedQueries() {
 		return cfg.getNamedQueries();
 	}
 
 	public Interceptor getInterceptor() {
 		return cfg.getInterceptor();
 	}
 
 	public Properties getProperties() {
 		return cfg.getProperties();
 	}
 
 	public Ejb3Configuration setInterceptor(Interceptor interceptor) {
 		cfg.setInterceptor( interceptor );
 		return this;
 	}
 
 	public Ejb3Configuration setProperties(Properties properties) {
 		cfg.setProperties( properties );
 		return this;
 	}
 
 	public Map getFilterDefinitions() {
 		return cfg.getFilterDefinitions();
 	}
 
 	public void addFilterDefinition(FilterDefinition definition) {
 		cfg.addFilterDefinition( definition );
 	}
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
 		cfg.addAuxiliaryDatabaseObject( object );
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return cfg.getNamingStrategy();
 	}
 
 	public Ejb3Configuration setNamingStrategy(NamingStrategy namingStrategy) {
 		cfg.setNamingStrategy( namingStrategy );
 		return this;
 	}
 
 	public Ejb3Configuration setSessionFactoryObserver(SessionFactoryObserver observer) {
 		cfg.setSessionFactoryObserver( observer );
 		return this;
 	}
 
-	public void setListeners(String type, String[] listenerClasses) {
-		cfg.setListeners( type, listenerClasses );
-	}
-
-	public void setListeners(String type, Object[] listeners) {
-		cfg.setListeners( type, listeners );
-	}
-
 	/**
 	 * This API is intended to give a read-only configuration.
 	 * It is sueful when working with SchemaExport or any Configuration based
 	 * tool.
 	 * DO NOT update configuration through it.
 	 */
 	public Configuration getHibernateConfiguration() {
 		//TODO make it really read only (maybe through proxying)
 		return cfg;
 	}
 
 	public Ejb3Configuration addInputStream(InputStream xmlInputStream) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addInputStream( xmlInputStream );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addResource(String path) throws MappingException {
 		Thread thread = null;
 		ClassLoader contextClassLoader = null;
 		if (overridenClassLoader != null) {
 			thread = Thread.currentThread();
 			contextClassLoader = thread.getContextClassLoader();
 			thread.setContextClassLoader( overridenClassLoader );
 		}
 		try {
 			cfg.addResource( path );
 			return this;
 		}
 		finally {
 			if (thread != null) thread.setContextClassLoader( contextClassLoader );
 		}
 	}
 
 	public Ejb3Configuration addResource(String path, ClassLoader classLoader) throws MappingException {
 		cfg.addResource( path, classLoader );
 		return this;
 	}
 
 	private enum XML_SEARCH {
 		HBM,
 		ORM_XML,
 		BOTH,
 		NONE;
 
 		public static XML_SEARCH getType(boolean searchHbm, boolean searchOrm) {
 			return searchHbm ?
 					searchOrm ? XML_SEARCH.BOTH : XML_SEARCH.HBM :
 					searchOrm ? XML_SEARCH.ORM_XML : XML_SEARCH.NONE;
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EventListenerConfigurator.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EventListenerConfigurator.java
deleted file mode 100644
index 2cfc958e9a..0000000000
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EventListenerConfigurator.java
+++ /dev/null
@@ -1,264 +0,0 @@
-/*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
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
- */
-package org.hibernate.ejb;
-import java.beans.BeanInfo;
-import java.beans.Introspector;
-import java.beans.PropertyDescriptor;
-import java.lang.reflect.Array;
-import java.util.ArrayList;
-import java.util.Enumeration;
-import java.util.Iterator;
-import java.util.List;
-import java.util.Properties;
-import java.util.StringTokenizer;
-import org.hibernate.HibernateException;
-import org.hibernate.MappingException;
-import org.hibernate.annotations.common.reflection.ReflectionManager;
-import org.hibernate.ejb.event.CallbackHandlerConsumer;
-import org.hibernate.ejb.event.EJB3AutoFlushEventListener;
-import org.hibernate.ejb.event.EJB3DeleteEventListener;
-import org.hibernate.ejb.event.EJB3FlushEntityEventListener;
-import org.hibernate.ejb.event.EJB3FlushEventListener;
-import org.hibernate.ejb.event.EJB3MergeEventListener;
-import org.hibernate.ejb.event.EJB3PersistEventListener;
-import org.hibernate.ejb.event.EJB3PersistOnFlushEventListener;
-import org.hibernate.ejb.event.EJB3PostDeleteEventListener;
-import org.hibernate.ejb.event.EJB3PostInsertEventListener;
-import org.hibernate.ejb.event.EJB3PostLoadEventListener;
-import org.hibernate.ejb.event.EJB3PostUpdateEventListener;
-import org.hibernate.ejb.event.EJB3SaveEventListener;
-import org.hibernate.ejb.event.EJB3SaveOrUpdateEventListener;
-import org.hibernate.ejb.event.EntityCallbackHandler;
-import org.hibernate.event.AutoFlushEventListener;
-import org.hibernate.event.DeleteEventListener;
-import org.hibernate.event.EventListeners;
-import org.hibernate.event.FlushEntityEventListener;
-import org.hibernate.event.FlushEventListener;
-import org.hibernate.event.MergeEventListener;
-import org.hibernate.event.PersistEventListener;
-import org.hibernate.event.PostDeleteEventListener;
-import org.hibernate.event.PostInsertEventListener;
-import org.hibernate.event.PostLoadEventListener;
-import org.hibernate.event.PostUpdateEventListener;
-import org.hibernate.event.PreDeleteEventListener;
-import org.hibernate.event.PreInsertEventListener;
-import org.hibernate.event.PreLoadEventListener;
-import org.hibernate.event.PreUpdateEventListener;
-import org.hibernate.event.SaveOrUpdateEventListener;
-import org.hibernate.event.def.DefaultPostLoadEventListener;
-import org.hibernate.event.def.DefaultPreLoadEventListener;
-import org.hibernate.mapping.PersistentClass;
-import org.hibernate.secure.JACCPreDeleteEventListener;
-import org.hibernate.secure.JACCPreInsertEventListener;
-import org.hibernate.secure.JACCPreLoadEventListener;
-import org.hibernate.secure.JACCPreUpdateEventListener;
-import org.hibernate.secure.JACCSecurityListener;
-
-/**
- * @author Emmanuel Bernard
- */
-public class EventListenerConfigurator {
-	private static final Object[] READER_METHOD_ARGS = new Object[0];
-
-	private Ejb3Configuration configuration;
-	private boolean isSecurity;
-
-	public EventListenerConfigurator(Ejb3Configuration configuration) {
-		this.configuration = configuration;
-		EventListeners listenerConfig = configuration.getEventListeners();
-
-		//Action event
-		//EJB3-specific ops listeners
-		listenerConfig.setFlushEventListeners( new FlushEventListener[] { EJB3FlushEventListener.INSTANCE } );
-		//EJB3-specific ops listeners
-		listenerConfig.setAutoFlushEventListeners( new AutoFlushEventListener[] { EJB3AutoFlushEventListener.INSTANCE } );
-		listenerConfig.setDeleteEventListeners( new DeleteEventListener[] { new EJB3DeleteEventListener() } );
-		listenerConfig.setFlushEntityEventListeners(
-				new FlushEntityEventListener[] { new EJB3FlushEntityEventListener() }
-		);
-		listenerConfig.setMergeEventListeners( new MergeEventListener[] { new EJB3MergeEventListener() } );
-		listenerConfig.setPersistEventListeners( new PersistEventListener[] { new EJB3PersistEventListener() } );
-		listenerConfig.setPersistOnFlushEventListeners(
-				new PersistEventListener[] { new EJB3PersistOnFlushEventListener() }
-		);
-		listenerConfig.setSaveEventListeners( new SaveOrUpdateEventListener[] { new EJB3SaveEventListener() } );
-		listenerConfig.setSaveOrUpdateEventListeners(
-				new SaveOrUpdateEventListener[] { new EJB3SaveOrUpdateEventListener() }
-		);
-
-		//Pre events
-		listenerConfig.setPreInsertEventListeners(
-				new PreInsertEventListener[] {
-						new JACCPreInsertEventListener(),
-				}
-		);
-		listenerConfig.setPreUpdateEventListeners(
-				new PreUpdateEventListener[] {
-						new JACCPreUpdateEventListener(),
-				}
-		);
-		listenerConfig.setPreDeleteEventListeners(
-				new PreDeleteEventListener[] {
-						new JACCPreDeleteEventListener()
-				}
-		);
-
-		//Add the default Hibernate Core PreLoadEventListener
-		//TODO shouldn't we read the value from getPreLoadEventListeners and add JACC?
-		//probably a better thing to do as it allows cfg.xml config but this is a big change and need more thoughts
-		listenerConfig.setPreLoadEventListeners(
-				new PreLoadEventListener[] {
-						new DefaultPreLoadEventListener(),
-						new JACCPreLoadEventListener()
-				}
-		);
-
-		//post events
-		listenerConfig.setPostDeleteEventListeners(
-				new PostDeleteEventListener[] { new EJB3PostDeleteEventListener() }
-		);
-		listenerConfig.setPostInsertEventListeners(
-				new PostInsertEventListener[] { new EJB3PostInsertEventListener() }
-		);
-		//Add the default Hibernate Core PostLoadEventListener
-		//TODO shouldn't we read the value from getPostLoadEventListeners
-		//probably a better thing to do as it allows cfg.xml config but this is a big change and need more thoughts
-		listenerConfig.setPostLoadEventListeners(
-				new PostLoadEventListener[] { new EJB3PostLoadEventListener(), new DefaultPostLoadEventListener() }
-		);
-		
-		EJB3PostUpdateEventListener postUpdateEventListener = new EJB3PostUpdateEventListener();
-		listenerConfig.setPostUpdateEventListeners(
-				new PostUpdateEventListener[] { postUpdateEventListener }
-		);
-		
-// EJB-288 - registration of post collection listeners. Commented out due to problem
-// of duplicated callbacks. See Jira.
-//		listenerConfig.setPostCollectionRecreateEventListeners(
-//				new PostCollectionRecreateEventListener[] { postUpdateEventListener });
-//		
-//		listenerConfig.setPostCollectionRemoveEventListeners(
-//				new PostCollectionRemoveEventListener[] { postUpdateEventListener });
-//		
-//		listenerConfig.setPostCollectionUpdateEventListeners(
-//				new PostCollectionUpdateEventListener[] { postUpdateEventListener });		
-	}
-
-	public void setProperties(Properties properties) {
-		if ( properties.containsKey( AvailableSettings.JACC_ENABLED ) ) {
-			isSecurity = true;
-		}
-		//override events if needed
-		Enumeration<?> enumeration = properties.propertyNames();
-		while ( enumeration.hasMoreElements() ) {
-			String name = (String) enumeration.nextElement();
-			if ( name.startsWith( AvailableSettings.EVENT_LISTENER_PREFIX ) ) {
-				String type = name.substring( AvailableSettings.EVENT_LISTENER_PREFIX.length() + 1 );
-				StringTokenizer st = new StringTokenizer( properties.getProperty( name ), " ,", false );
-				List<String> listeners = new ArrayList<String>();
-				while ( st.hasMoreElements() ) {
-					listeners.add( (String) st.nextElement() );
-				}
-				configuration.setListeners( type, listeners.toArray( new String[listeners.size()] ) );
-			}
-		}
-	}
-
-	@SuppressWarnings("unchecked")
-	public void configure() {
-		//TODO exclude pure hbm file classes?
-		//TODO move it to each event listener initialize()?
-		EntityCallbackHandler callbackHandler = new EntityCallbackHandler();
-		configuration.buildMappings(); //needed to get all the classes
-		Iterator classes = configuration.getClassMappings();
-		ReflectionManager reflectionManager = configuration.getHibernateConfiguration().getReflectionManager();
-		while ( classes.hasNext() ) {
-			PersistentClass clazz = (PersistentClass) classes.next();
-			if ( clazz.getClassName() != null ) {
-				//we can have non java class persisted by hibernate
-				try {
-					callbackHandler.add( reflectionManager.classForName( clazz.getClassName(), this.getClass() ), reflectionManager );
-				}
-				catch (ClassNotFoundException e) {
-					throw new MappingException( "entity class not found: " + clazz.getNodeName(), e );
-				}
-			}
-		}
-
-		EventListeners listenerConfig = configuration.getEventListeners();
-
-		BeanInfo beanInfo = null;
-		try {
-			beanInfo = Introspector.getBeanInfo( listenerConfig.getClass(), Object.class );
-			PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
-			try {
-				for (int i = 0, max = pds.length; i < max; i++) {
-					final Object listeners = pds[i].getReadMethod().invoke( listenerConfig, READER_METHOD_ARGS );
-					if ( listeners == null ) {
-						throw new HibernateException( "Listener [" + pds[i].getName() + "] was null" );
-					}
-					if ( listeners instanceof Object[] ) {
-						int securityListenersNbr = 0;
-						Object[] listenersArray = (Object[]) listeners;
-						for (Object listener : listenersArray) {
-							if ( listener != null && listener instanceof CallbackHandlerConsumer ) {
-								( (CallbackHandlerConsumer) listener ).setCallbackHandler( callbackHandler );
-							}
-							if ( listener != null && listener instanceof JACCSecurityListener ) {
-								if ( !isSecurity ) {
-									securityListenersNbr++;
-								}
-							}
-						}
-						if ( !isSecurity ) {
-							Class clazz = pds[i].getReadMethod().getReturnType().getComponentType();
-							Object newArray = Array.newInstance( clazz, listenersArray.length - securityListenersNbr );
-							int index = 0;
-							for (Object listener : listenersArray) {
-								if ( !( listener != null && listener instanceof JACCSecurityListener ) ) {
-									Array.set( newArray, index++, listener );
-								}
-							}
-							pds[i].getWriteMethod().invoke( listenerConfig, newArray );
-						}
-					}
-				}
-			}
-			catch (HibernateException e) {
-				throw e;
-			}
-			catch (Throwable t) {
-				throw new HibernateException( "Unable to validate listener config", t );
-			}
-		}
-		catch (Exception t) {
-			throw new HibernateException( "Unable to copy listeners", t );
-		}
-		finally {
-			if ( beanInfo != null ) {
-				// release the jdk internal caches everytime to ensure this
-				// plays nicely with destroyable class-loaders
-				Introspector.flushFromCaches( getClass() );
-			}
-		}
-	}
-}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/BeanCallback.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/BeanCallback.java
index 9a501aad78..7922c43db5 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/BeanCallback.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/BeanCallback.java
@@ -1,53 +1,56 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 
 /**
  * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
  */
 public class BeanCallback extends Callback {
 	public BeanCallback(Method callbackMethod) {
 		super( callbackMethod );
 	}
 
 	public void invoke(Object bean) {
 		try {
 			callbackMethod.invoke( bean, new Object[0] );
 		}
 		catch (InvocationTargetException e) {
 			//keep runtime exceptions as is
 			if ( e.getTargetException() instanceof RuntimeException ) {
 				throw (RuntimeException) e.getTargetException();
 			}
 			else {
 				throw new RuntimeException( e.getTargetException() );
 			}
 		}
 		catch (Exception e) {
 			throw new RuntimeException( e );
 		}
 	}
 
 
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/Callback.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/Callback.java
index 0d2e4d2806..f279e82109 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/Callback.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/Callback.java
@@ -1,87 +1,90 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.StringTokenizer;
+
 import org.hibernate.internal.util.ReflectHelper;
 
 /**
  * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
  */
 public abstract class Callback implements Serializable {
 	transient protected Method callbackMethod;
 
 	public Callback(Method callbackMethod) {
 		this.callbackMethod = callbackMethod;
 	}
 
 	public Method getCallbackMethod() {
 		return callbackMethod;
 	}
 
 	public abstract void invoke(Object bean);
 
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		oos.defaultWriteObject();
 		oos.writeObject( callbackMethod.toGenericString() );
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		ois.defaultReadObject();
 		String signature = (String) ois.readObject();
 		StringTokenizer st = new StringTokenizer( signature, " ", false );
 		String usefulSignature = null;
 		while ( st.hasMoreElements() ) usefulSignature = (String) st.nextElement();
 		int parenthesis = usefulSignature.indexOf( "(" );
 		String methodAndClass = usefulSignature.substring( 0, parenthesis );
 		int lastDot = methodAndClass.lastIndexOf( "." );
 		String clazzName = methodAndClass.substring( 0, lastDot );
 		Class callbackClass = ReflectHelper.classForName( clazzName, this.getClass() );
 		String parametersString = usefulSignature.substring( parenthesis + 1, usefulSignature.length() - 1 );
 		st = new StringTokenizer( parametersString, ", ", false );
 		List<Class> parameters = new ArrayList<Class>();
 		while ( st.hasMoreElements() ) {
 			String parameter = (String) st.nextElement();
 			parameters.add( ReflectHelper.classForName( parameter, this.getClass() ) );
 		}
 		String methodName = methodAndClass.substring( lastDot + 1, methodAndClass.length() );
 		try {
 			callbackMethod = callbackClass.getDeclaredMethod(
 					methodName,
 					parameters.toArray( new Class[ parameters.size() ] )
 			);
 			if ( ! callbackMethod.isAccessible() ) {
 				callbackMethod.setAccessible( true );
 			}
 		}
 		catch (NoSuchMethodException e) {
 			throw new IOException( "Unable to get EJB3 callback method: " + signature + ", cause: " + e );
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/CallbackHandlerConsumer.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/CallbackHandlerConsumer.java
index 37e31d5d99..2b83a0dada 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/CallbackHandlerConsumer.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/CallbackHandlerConsumer.java
@@ -1,30 +1,31 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
 
-
 /**
  * @author Emmanuel Bernard
  */
-public interface CallbackHandlerConsumer {
+public interface CallbackHandlerConsumer extends HibernateEntityManagerEventListener {
 	void setCallbackHandler(EntityCallbackHandler callbackHandler);
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/CallbackResolver.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/CallbackResolver.java
index 876354af25..bce46012be 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/CallbackResolver.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/CallbackResolver.java
@@ -1,225 +1,230 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
-import java.lang.annotation.Annotation;
-import java.lang.annotation.ElementType;
-import java.lang.annotation.Target;
-import java.lang.reflect.Method;
-import java.util.ArrayList;
-import java.util.List;
+
 import javax.persistence.Entity;
 import javax.persistence.EntityListeners;
 import javax.persistence.ExcludeDefaultListeners;
 import javax.persistence.ExcludeSuperclassListeners;
 import javax.persistence.MappedSuperclass;
 import javax.persistence.PersistenceException;
+import java.lang.annotation.Annotation;
+import java.lang.annotation.ElementType;
+import java.lang.annotation.Target;
+import java.lang.reflect.Method;
+import java.util.ArrayList;
+import java.util.List;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XMethod;
 import org.hibernate.ejb.EntityManagerLogger;
-import org.jboss.logging.Logger;
 
 /**
  * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
  */
 public final class CallbackResolver {
 
     private static final EntityManagerLogger LOG = Logger.getMessageLogger(EntityManagerLogger.class,
                                                                            CallbackResolver.class.getName());
 
 	private static boolean useAnnotationAnnotatedByListener;
 
 	static {
 		//check whether reading annotations of annotations is useful or not
 		useAnnotationAnnotatedByListener = false;
 		Target target = EntityListeners.class.getAnnotation( Target.class );
 		if ( target != null ) {
 			for ( ElementType type : target.value() ) {
 				if ( type.equals( ElementType.ANNOTATION_TYPE ) ) useAnnotationAnnotatedByListener = true;
 			}
 		}
 	}
 
 	private CallbackResolver() {
 	}
 
 	public static Callback[] resolveCallback(XClass beanClass, Class annotation, ReflectionManager reflectionManager) {
 		List<Callback> callbacks = new ArrayList<Callback>();
 		List<String> callbacksMethodNames = new ArrayList<String>(); //used to track overriden methods
 		List<Class> orderedListeners = new ArrayList<Class>();
 		XClass currentClazz = beanClass;
 		boolean stopListeners = false;
 		boolean stopDefaultListeners = false;
 		do {
 			Callback callback = null;
 			List<XMethod> methods = currentClazz.getDeclaredMethods();
 			final int size = methods.size();
 			for ( int i = 0; i < size ; i++ ) {
 				final XMethod xMethod = methods.get( i );
 				if ( xMethod.isAnnotationPresent( annotation ) ) {
 					Method method = reflectionManager.toMethod( xMethod );
 					final String methodName = method.getName();
 					if ( ! callbacksMethodNames.contains( methodName ) ) {
 						//overriden method, remove the superclass overriden method
 						if ( callback == null ) {
 							callback = new BeanCallback( method );
 							Class returnType = method.getReturnType();
 							Class[] args = method.getParameterTypes();
 							if ( returnType != Void.TYPE || args.length != 0 ) {
 								throw new RuntimeException(
 										"Callback methods annotated on the bean class must return void and take no arguments: " + annotation
 												.getName() + " - " + xMethod
 								);
 							}
                             if (!method.isAccessible()) method.setAccessible(true);
                             LOG.debugf("Adding %s as %s callback for entity %s",
                                        methodName,
                                        annotation.getSimpleName(),
                                        beanClass.getName());
 							callbacks.add( 0, callback ); //superclass first
 							callbacksMethodNames.add( 0, methodName );
 						}
 						else {
 							throw new PersistenceException(
 									"You can only annotate one callback method with "
 											+ annotation.getName() + " in bean class: " + beanClass.getName()
 							);
 						}
 					}
 				}
 			}
 			if ( !stopListeners ) {
 				getListeners( currentClazz, orderedListeners );
 				stopListeners = currentClazz.isAnnotationPresent( ExcludeSuperclassListeners.class );
 				stopDefaultListeners = currentClazz.isAnnotationPresent( ExcludeDefaultListeners.class );
 			}
 
 			do {
 				currentClazz = currentClazz.getSuperclass();
 			}
 			while ( currentClazz != null
 					&& ! ( currentClazz.isAnnotationPresent( Entity.class )
 					|| currentClazz.isAnnotationPresent( MappedSuperclass.class ) )
 					);
 		}
 		while ( currentClazz != null );
 
 		//handle default listeners
 		if ( ! stopDefaultListeners ) {
 			List<Class> defaultListeners = (List<Class>) reflectionManager.getDefaults().get( EntityListeners.class );
 
 			if ( defaultListeners != null ) {
 				int defaultListenerSize = defaultListeners.size();
 				for ( int i = defaultListenerSize - 1; i >= 0 ; i-- ) {
 					orderedListeners.add( defaultListeners.get( i ) );
 				}
 			}
 		}
 
 		for ( Class listener : orderedListeners ) {
 			Callback callback = null;
 			if ( listener != null ) {
 				XClass xListener = reflectionManager.toXClass( listener );
 				callbacksMethodNames = new ArrayList<String>();
 				do {
 					List<XMethod> methods = xListener.getDeclaredMethods();
 					final int size = methods.size();
 					for ( int i = 0; i < size ; i++ ) {
 						final XMethod xMethod = methods.get( i );
 						if ( xMethod.isAnnotationPresent( annotation ) ) {
 							final Method method = reflectionManager.toMethod( xMethod );
 							final String methodName = method.getName();
 							if ( ! callbacksMethodNames.contains( methodName ) ) {
 								//overriden method, remove the superclass overriden method
 								if ( callback == null ) {
 									try {
 										callback = new ListenerCallback( method, listener.newInstance() );
 									}
 									catch (IllegalAccessException e) {
 										throw new PersistenceException(
 												"Unable to create instance of " + listener.getName()
 														+ " as a listener of beanClass", e
 										);
 									}
 									catch (InstantiationException e) {
 										throw new PersistenceException(
 												"Unable to create instance of " + listener.getName()
 														+ " as a listener of beanClass", e
 										);
 									}
 									Class returnType = method.getReturnType();
 									Class[] args = method.getParameterTypes();
 									if ( returnType != Void.TYPE || args.length != 1 ) {
 										throw new PersistenceException(
 												"Callback methods annotated in a listener bean class must return void and take one argument: " + annotation
 														.getName() + " - " + method
 										);
 									}
                                     if (!method.isAccessible()) method.setAccessible(true);
                                     LOG.debugf("Adding %s as %s callback for entity %s",
                                                methodName,
                                                annotation.getSimpleName(),
                                                beanClass.getName());
 									callbacks.add( 0, callback ); // listeners first
 								}
 								else {
 									throw new PersistenceException(
 											"You can only annotate one callback method with "
 													+ annotation.getName() + " in bean class: " + beanClass.getName() + " and callback listener: "
 													+ listener.getName()
 									);
 								}
 							}
 						}
 					}
 					xListener = null;  //xListener.getSuperclass();
 				}
 				while ( xListener != null );
 			}
 		}
 		return callbacks.toArray( new Callback[ callbacks.size() ] );
 	}
 
 	private static void getListeners(XClass currentClazz, List<Class> orderedListeners) {
 		EntityListeners entityListeners = currentClazz.getAnnotation( EntityListeners.class );
 		if ( entityListeners != null ) {
 			Class[] classes = entityListeners.value();
 			int size = classes.length;
 			for ( int index = size - 1; index >= 0 ; index-- ) {
 				orderedListeners.add( classes[index] );
 			}
 		}
 		if ( useAnnotationAnnotatedByListener ) {
 			Annotation[] annotations = currentClazz.getAnnotations();
 			for ( Annotation annot : annotations ) {
 				entityListeners = annot.getClass().getAnnotation( EntityListeners.class );
 				if ( entityListeners != null ) {
 					Class[] classes = entityListeners.value();
 					int size = classes.length;
 					for ( int index = size - 1; index >= 0 ; index-- ) {
 						orderedListeners.add( classes[index] );
 					}
 				}
 			}
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3AutoFlushEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3AutoFlushEventListener.java
index fcd0f88a9e..7aa880eab4 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3AutoFlushEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3AutoFlushEventListener.java
@@ -1,47 +1,51 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.event.AutoFlushEventListener;
 import org.hibernate.event.def.DefaultAutoFlushEventListener;
 import org.hibernate.internal.util.collections.IdentityMap;
 
 /**
- * In EJB3, it is the create operation that is cascaded to unmanaged
- * entities at flush time (instead of the save-update operation in
- * Hibernate).
+ * In JPA, it is the create operation that is cascaded to unmanaged entities at flush time (instead of the save-update
+ * operation in Hibernate).
  *
  * @author Gavin King
  */
-public class EJB3AutoFlushEventListener extends DefaultAutoFlushEventListener {
+public class EJB3AutoFlushEventListener
+		extends DefaultAutoFlushEventListener
+		implements HibernateEntityManagerEventListener {
 
 	public static final AutoFlushEventListener INSTANCE = new EJB3AutoFlushEventListener();
 
 	protected CascadingAction getCascadingAction() {
 		return CascadingAction.PERSIST_ON_FLUSH;
 	}
 
 	protected Object getAnything() {
 		return IdentityMap.instantiate( 10 );
 	}
 
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3DeleteEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3DeleteEventListener.java
index ae5675f30f..41ebf052c1 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3DeleteEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3DeleteEventListener.java
@@ -1,65 +1,69 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import java.io.Serializable;
+
 import org.hibernate.event.DeleteEvent;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.def.DefaultDeleteEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Overrides the LifeCycle OnSave call to call the PreRemove operation
  *
  * @author Emmanuel Bernard
  */
 public class EJB3DeleteEventListener extends DefaultDeleteEventListener implements CallbackHandlerConsumer {
 	private EntityCallbackHandler callbackHandler;
 
 	public void setCallbackHandler(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public EJB3DeleteEventListener() {
 		super();
 	}
 
 	public EJB3DeleteEventListener(EntityCallbackHandler callbackHandler) {
 		this();
 		this.callbackHandler = callbackHandler;
 	}
 
 	@Override
 	protected boolean invokeDeleteLifecycle(EventSource session, Object entity, EntityPersister persister) {
 		callbackHandler.preRemove( entity );
 		return super.invokeDeleteLifecycle( session, entity, persister );
 	}
 
 	@Override
 	protected void performDetachedEntityDeletionCheck(DeleteEvent event) {
 		EventSource source = event.getSession();
 		String entityName = event.getEntityName();
 		EntityPersister persister = source.getEntityPersister( entityName, event.getObject() );
 		Serializable id =  persister.getIdentifier( event.getObject(), source );
 		entityName = entityName == null ? source.guessEntityName( event.getObject() ) : entityName; 
 		throw new IllegalArgumentException("Removing a detached instance "+ entityName + "#" + id);
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEntityEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEntityEventListener.java
index 219a1583ad..060aa6dc18 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEntityEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEntityEventListener.java
@@ -1,83 +1,87 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import org.hibernate.EntityMode;
 import org.hibernate.SessionFactory;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.event.def.DefaultFlushEntityEventListener;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * Overrides the LifeCycle OnSave call to call the PreUpdate operation
  *
  * @author Emmanuel Bernard
  */
 public class EJB3FlushEntityEventListener extends DefaultFlushEntityEventListener implements CallbackHandlerConsumer {
 	private EntityCallbackHandler callbackHandler;
 
 	public void setCallbackHandler(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public EJB3FlushEntityEventListener() {
 		super();
 	}
 
 	public EJB3FlushEntityEventListener(EntityCallbackHandler callbackHandler) {
 		super();
 		this.callbackHandler = callbackHandler;
 	}
 
 	@Override
 	protected boolean invokeInterceptor(
-			SessionImplementor session, Object entity, EntityEntry entry, Object[] values, EntityPersister persister
-	) {
+			SessionImplementor session,
+			Object entity,
+			EntityEntry entry,
+			Object[] values,
+			EntityPersister persister) {
 		boolean isDirty = false;
 		if ( entry.getStatus() != Status.DELETED ) {
 			if ( callbackHandler.preUpdate( entity ) ) {
 				isDirty = copyState( entity, persister.getPropertyTypes(), values, session.getFactory() );
 			}
 		}
 		return super.invokeInterceptor( session, entity, entry, values, persister ) || isDirty;
 	}
 
-	/**
-	 * copy the entity state into the state array and return true if the state has changed
-	 */
 	private boolean copyState(Object entity, Type[] types, Object[] state, SessionFactory sf) {
+		// copy the entity state into the state array and return true if the state has changed
 		ClassMetadata metadata = sf.getClassMetadata( entity.getClass() );
 		Object[] newState = metadata.getPropertyValues( entity, EntityMode.POJO );
 		int size = newState.length;
 		boolean isDirty = false;
 		for ( int index = 0; index < size ; index++ ) {
 			if ( !types[index].isEqual( state[index], newState[index], EntityMode.POJO ) ) {
 				isDirty = true;
 				state[index] = newState[index];
 			}
 		}
 		return isDirty;
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEventListener.java
index f9674ebc8c..1bec969185 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3FlushEventListener.java
@@ -1,47 +1,49 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.event.FlushEventListener;
 import org.hibernate.event.def.DefaultFlushEventListener;
 import org.hibernate.internal.util.collections.IdentityMap;
 
 /**
- * In EJB3, it is the create operation that is cascaded to unmanaged
- * ebtities at flush time (instead of the save-update operation in
- * Hibernate).
+ * In EJB3, it is the create operation that is cascaded to unmanaged entities at flush time (instead of the
+ * save-update operation in Hibernate).
  *
  * @author Gavin King
  */
-public class EJB3FlushEventListener extends DefaultFlushEventListener {
+public class EJB3FlushEventListener extends DefaultFlushEventListener implements HibernateEntityManagerEventListener {
 
 	public static final FlushEventListener INSTANCE = new EJB3FlushEventListener();
 
 	protected CascadingAction getCascadingAction() {
 		return CascadingAction.PERSIST_ON_FLUSH;
 	}
 
 	protected Object getAnything() {
 		return IdentityMap.instantiate( 10 );
 	}
 
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3MergeEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3MergeEventListener.java
index 08f16c30b0..4511657515 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3MergeEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3MergeEventListener.java
@@ -1,63 +1,73 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import java.io.Serializable;
+
 import org.hibernate.event.EventSource;
 import org.hibernate.event.def.DefaultMergeEventListener;
 
 /**
  * Overrides the LifeCycle OnSave call to call the PrePersist operation
  *
  * @author Emmanuel Bernard
  */
 public class EJB3MergeEventListener extends DefaultMergeEventListener implements CallbackHandlerConsumer {
 	private EntityCallbackHandler callbackHandler;
 
 	public void setCallbackHandler(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public EJB3MergeEventListener() {
 		super();
 	}
 
 	public EJB3MergeEventListener(EntityCallbackHandler callbackHandler) {
 		super();
 		this.callbackHandler = callbackHandler;
 	}
 
 	@Override
-	protected Serializable saveWithRequestedId(Object entity, Serializable requestedId, String entityName,
-											   Object anything, EventSource source) {
+	protected Serializable saveWithRequestedId(
+			Object entity,
+			Serializable requestedId,
+			String entityName,
+			Object anything,
+			EventSource source) {
 		callbackHandler.preCreate( entity );
-		return super.saveWithRequestedId( entity, requestedId, entityName, anything,
-				source );
+		return super.saveWithRequestedId( entity, requestedId, entityName, anything, source );
 	}
 
 	@Override
-	protected Serializable saveWithGeneratedId(Object entity, String entityName, Object anything, EventSource source,
-											   boolean requiresImmediateIdAccess) {
+	protected Serializable saveWithGeneratedId(
+			Object entity,
+			String entityName,
+			Object anything,
+			EventSource source,
+			boolean requiresImmediateIdAccess) {
 		callbackHandler.preCreate( entity );
-		return super.saveWithGeneratedId( entity, entityName, anything, source,
-				requiresImmediateIdAccess );
+		return super.saveWithGeneratedId( entity, entityName, anything, source, requiresImmediateIdAccess );
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PersistEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PersistEventListener.java
index 2315b229d3..101bb76695 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PersistEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PersistEventListener.java
@@ -1,75 +1,85 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import java.io.Serializable;
+
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EJB3CascadeStyle;
 import org.hibernate.engine.EJB3CascadingAction;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.def.DefaultPersistEventListener;
 
 /**
  * Overrides the LifeCycle OnSave call to call the PrePersist operation
  *
  * @author Emmanuel Bernard
  */
 public class EJB3PersistEventListener extends DefaultPersistEventListener implements CallbackHandlerConsumer {
 	static {
 		EJB3CascadeStyle.PERSIST_EJB3.hasOrphanDelete(); //triggers class loading to override persist with PERSIST_EJB3
 	}
 
 	private EntityCallbackHandler callbackHandler;
 
 	public void setCallbackHandler(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public EJB3PersistEventListener() {
 		super();
 	}
 
 	public EJB3PersistEventListener(EntityCallbackHandler callbackHandler) {
 		super();
 		this.callbackHandler = callbackHandler;
 	}
 
 	@Override
-	protected Serializable saveWithRequestedId(Object entity, Serializable requestedId, String entityName,
-											   Object anything, EventSource source) {
+	protected Serializable saveWithRequestedId(
+			Object entity,
+			Serializable requestedId,
+			String entityName,
+			Object anything,
+			EventSource source) {
 		callbackHandler.preCreate( entity );
-		return super.saveWithRequestedId( entity, requestedId, entityName, anything,
-				source );
+		return super.saveWithRequestedId( entity, requestedId, entityName, anything, source );
 	}
 
 	@Override
-	protected Serializable saveWithGeneratedId(Object entity, String entityName, Object anything, EventSource source,
-											   boolean requiresImmediateIdAccess) {
+	protected Serializable saveWithGeneratedId(
+			Object entity,
+			String entityName,
+			Object anything,
+			EventSource source,
+			boolean requiresImmediateIdAccess) {
 		callbackHandler.preCreate( entity );
-		return super.saveWithGeneratedId( entity, entityName, anything, source,
-				requiresImmediateIdAccess );
+		return super.saveWithGeneratedId( entity, entityName, anything, source, requiresImmediateIdAccess );
 	}
 
 	@Override
 	protected CascadingAction getCascadeAction() {
 		return EJB3CascadingAction.PERSIST_SKIPLAZY;
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PersistOnFlushEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PersistOnFlushEventListener.java
index 8829c927e6..feac85ca73 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PersistOnFlushEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PersistOnFlushEventListener.java
@@ -1,33 +1,36 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import org.hibernate.engine.CascadingAction;
 
 /**
  * @author Emmanuel Bernard
  */
 public class EJB3PersistOnFlushEventListener extends EJB3PersistEventListener {
 	@Override
 	protected CascadingAction getCascadeAction() {
 		return CascadingAction.PERSIST_ON_FLUSH;
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostDeleteEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostDeleteEventListener.java
index 2304468076..868d754dd8 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostDeleteEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostDeleteEventListener.java
@@ -1,49 +1,52 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import org.hibernate.event.PostDeleteEvent;
 import org.hibernate.event.PostDeleteEventListener;
 
 /**
  * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
  */
 public class EJB3PostDeleteEventListener implements PostDeleteEventListener, CallbackHandlerConsumer {
 	EntityCallbackHandler callbackHandler;
 
 	public void setCallbackHandler(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public EJB3PostDeleteEventListener() {
 		super();
 	}
 
 	public EJB3PostDeleteEventListener(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public void onPostDelete(PostDeleteEvent event) {
 		Object entity = event.getEntity();
 		callbackHandler.postRemove( entity );
 	}
 
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostInsertEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostInsertEventListener.java
index 0bb0b6f125..091c5a6ae5 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostInsertEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostInsertEventListener.java
@@ -1,48 +1,51 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import org.hibernate.event.PostInsertEvent;
 import org.hibernate.event.PostInsertEventListener;
 
 /**
  * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
  */
 public class EJB3PostInsertEventListener implements PostInsertEventListener, CallbackHandlerConsumer {
 	EntityCallbackHandler callbackHandler;
 
 	public void setCallbackHandler(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public EJB3PostInsertEventListener() {
 		super();
 	}
 
 	public EJB3PostInsertEventListener(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public void onPostInsert(PostInsertEvent event) {
 		Object entity = event.getEntity();
 		callbackHandler.postCreate( entity );
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostLoadEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostLoadEventListener.java
index 03efa681b0..f1a599829c 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostLoadEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostLoadEventListener.java
@@ -1,49 +1,52 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import org.hibernate.event.PostLoadEvent;
 import org.hibernate.event.PostLoadEventListener;
 
 /**
  * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
  */
 public class EJB3PostLoadEventListener implements PostLoadEventListener, CallbackHandlerConsumer {
 	EntityCallbackHandler callbackHandler;
 
 	public void setCallbackHandler(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public EJB3PostLoadEventListener() {
 		super();
 	}
 
 	public EJB3PostLoadEventListener(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public void onPostLoad(PostLoadEvent event) {
 		Object entity = event.getEntity();
 		callbackHandler.postLoad( entity );
 	}
 
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostUpdateEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostUpdateEventListener.java
index 47efd86d08..dd339098d1 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostUpdateEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3PostUpdateEventListener.java
@@ -1,90 +1,96 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.Status;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.PostCollectionRecreateEvent;
 import org.hibernate.event.PostCollectionRecreateEventListener;
 import org.hibernate.event.PostCollectionRemoveEvent;
 import org.hibernate.event.PostCollectionRemoveEventListener;
 import org.hibernate.event.PostCollectionUpdateEvent;
 import org.hibernate.event.PostCollectionUpdateEventListener;
 import org.hibernate.event.PostUpdateEvent;
 import org.hibernate.event.PostUpdateEventListener;
 
 /**
  * Implementation of the post update listeners.
  * 
  * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
  */
 @SuppressWarnings("serial")
-public class EJB3PostUpdateEventListener implements PostUpdateEventListener,
-		CallbackHandlerConsumer, PostCollectionRecreateEventListener,
-		PostCollectionRemoveEventListener, PostCollectionUpdateEventListener {
+public class EJB3PostUpdateEventListener
+		implements PostUpdateEventListener,
+				   CallbackHandlerConsumer,
+				   PostCollectionRecreateEventListener,
+				   PostCollectionRemoveEventListener,
+				   PostCollectionUpdateEventListener {
 	EntityCallbackHandler callbackHandler;
 
 	public void setCallbackHandler(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public EJB3PostUpdateEventListener() {
 		super();
 	}
 
 	public EJB3PostUpdateEventListener(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public void onPostUpdate(PostUpdateEvent event) {
 		Object entity = event.getEntity();
 		EventSource eventSource = event.getSession();
 		handlePostUpdate(entity, eventSource);
 	}
 
 	private void handlePostUpdate(Object entity, EventSource source) {
 		EntityEntry entry = (EntityEntry) source.getPersistenceContext()
 				.getEntityEntries().get(entity);
 		// mimic the preUpdate filter
 		if (Status.DELETED != entry.getStatus()) {
 			callbackHandler.postUpdate(entity);
 		}
 	}
 
 	public void onPostRecreateCollection(PostCollectionRecreateEvent event) {
 		Object entity = event.getCollection().getOwner();
 		EventSource eventSource = event.getSession();
 		handlePostUpdate(entity, eventSource);
 	}
 
 	public void onPostRemoveCollection(PostCollectionRemoveEvent event) {
 		Object entity = event.getCollection().getOwner();
 		EventSource eventSource = event.getSession();
 		handlePostUpdate(entity, eventSource);		
 	}
 
 	public void onPostUpdateCollection(PostCollectionUpdateEvent event) {
 		Object entity = event.getCollection().getOwner();
 		EventSource eventSource = event.getSession();
 		handlePostUpdate(entity, eventSource);		
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3SaveEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3SaveEventListener.java
index d088dc7a53..07a4ac899f 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3SaveEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3SaveEventListener.java
@@ -1,63 +1,73 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import java.io.Serializable;
+
 import org.hibernate.event.EventSource;
 import org.hibernate.event.def.DefaultSaveEventListener;
 
 /**
  * Overrides the LifeCycle OnSave call to call the PrePersist operation
  *
  * @author Emmanuel Bernard
  */
 public class EJB3SaveEventListener extends DefaultSaveEventListener implements CallbackHandlerConsumer {
 	private EntityCallbackHandler callbackHandler;
 
 	public void setCallbackHandler(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public EJB3SaveEventListener() {
 		super();
 	}
 
 	public EJB3SaveEventListener(EntityCallbackHandler callbackHandler) {
 		super();
 		this.callbackHandler = callbackHandler;
 	}
 
 	@Override
-	protected Serializable saveWithRequestedId(Object entity, Serializable requestedId, String entityName,
-											   Object anything, EventSource source) {
+	protected Serializable saveWithRequestedId(
+			Object entity,
+			Serializable requestedId,
+			String entityName,
+			Object anything,
+			EventSource source) {
 		callbackHandler.preCreate( entity );
-		return super.saveWithRequestedId( entity, requestedId, entityName, anything,
-				source );
+		return super.saveWithRequestedId( entity, requestedId, entityName, anything, source );
 	}
 
 	@Override
-	protected Serializable saveWithGeneratedId(Object entity, String entityName, Object anything, EventSource source,
-											   boolean requiresImmediateIdAccess) {
+	protected Serializable saveWithGeneratedId(
+			Object entity,
+			String entityName,
+			Object anything,
+			EventSource source,
+			boolean requiresImmediateIdAccess) {
 		callbackHandler.preCreate( entity );
-		return super.saveWithGeneratedId( entity, entityName, anything, source,
-				requiresImmediateIdAccess );
+		return super.saveWithGeneratedId( entity, entityName, anything, source, requiresImmediateIdAccess );
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3SaveOrUpdateEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3SaveOrUpdateEventListener.java
index 38cbf51bdc..36305f8fa3 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3SaveOrUpdateEventListener.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EJB3SaveOrUpdateEventListener.java
@@ -1,63 +1,73 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
+
 import java.io.Serializable;
+
 import org.hibernate.event.EventSource;
 import org.hibernate.event.def.DefaultSaveOrUpdateEventListener;
 
 /**
  * Overrides the LifeCycle OnSave call to call the PrePersist operation
  *
  * @author Emmanuel Bernard
  */
 public class EJB3SaveOrUpdateEventListener extends DefaultSaveOrUpdateEventListener implements CallbackHandlerConsumer {
 	private EntityCallbackHandler callbackHandler;
 
 	public void setCallbackHandler(EntityCallbackHandler callbackHandler) {
 		this.callbackHandler = callbackHandler;
 	}
 
 	public EJB3SaveOrUpdateEventListener() {
 		super();
 	}
 
 	public EJB3SaveOrUpdateEventListener(EntityCallbackHandler callbackHandler) {
 		super();
 		this.callbackHandler = callbackHandler;
 	}
 
 	@Override
-	protected Serializable saveWithRequestedId(Object entity, Serializable requestedId, String entityName,
-											   Object anything, EventSource source) {
+	protected Serializable saveWithRequestedId(
+			Object entity,
+			Serializable requestedId,
+			String entityName,
+			Object anything,
+			EventSource source) {
 		callbackHandler.preCreate( entity );
-		return super.saveWithRequestedId( entity, requestedId, entityName, anything,
-				source );
+		return super.saveWithRequestedId( entity, requestedId, entityName, anything, source );
 	}
 
 	@Override
-	protected Serializable saveWithGeneratedId(Object entity, String entityName, Object anything, EventSource source,
-											   boolean requiresImmediateIdAccess) {
+	protected Serializable saveWithGeneratedId(
+			Object entity,
+			String entityName,
+			Object anything,
+			EventSource source,
+			boolean requiresImmediateIdAccess) {
 		callbackHandler.preCreate( entity );
-		return super.saveWithGeneratedId( entity, entityName, anything, source,
-				requiresImmediateIdAccess );
+		return super.saveWithGeneratedId( entity, entityName, anything, source, requiresImmediateIdAccess );
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EntityCallbackHandler.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EntityCallbackHandler.java
index 76c27f2d5a..d09e71818b 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EntityCallbackHandler.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/EntityCallbackHandler.java
@@ -1,109 +1,113 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
-import java.io.Serializable;
-import java.util.HashMap;
+
 import javax.persistence.PostLoad;
 import javax.persistence.PostPersist;
 import javax.persistence.PostRemove;
 import javax.persistence.PostUpdate;
 import javax.persistence.PrePersist;
 import javax.persistence.PreRemove;
 import javax.persistence.PreUpdate;
+import java.io.Serializable;
+import java.util.HashMap;
+
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XClass;
 
 /**
  * Keep track of all lifecycle callbacks and listeners for a given persistence unit
  *
  * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
  */
 @SuppressWarnings({"unchecked", "serial"})
 public class EntityCallbackHandler implements Serializable {
 	private HashMap<Class, Callback[]> preCreates = new HashMap<Class, Callback[]>();
 	private HashMap<Class, Callback[]> postCreates = new HashMap<Class, Callback[]>();
 	private HashMap<Class, Callback[]> preRemoves = new HashMap<Class, Callback[]>();
 	private HashMap<Class, Callback[]> postRemoves = new HashMap<Class, Callback[]>();
 	private HashMap<Class, Callback[]> preUpdates = new HashMap<Class, Callback[]>();
 	private HashMap<Class, Callback[]> postUpdates = new HashMap<Class, Callback[]>();
 	private HashMap<Class, Callback[]> postLoads = new HashMap<Class, Callback[]>();
 
 	public void add(XClass entity, ReflectionManager reflectionManager) {
 		addCallback( entity, preCreates, PrePersist.class, reflectionManager );
 		addCallback( entity, postCreates, PostPersist.class, reflectionManager );
 		addCallback( entity, preRemoves, PreRemove.class, reflectionManager );
 		addCallback( entity, postRemoves, PostRemove.class, reflectionManager );
 		addCallback( entity, preUpdates, PreUpdate.class, reflectionManager );
 		addCallback( entity, postUpdates, PostUpdate.class, reflectionManager );
 		addCallback( entity, postLoads, PostLoad.class, reflectionManager );
 	}
 
 	public boolean preCreate(Object bean) {
 		return callback( preCreates.get( bean.getClass() ), bean );
 	}
 
 	public boolean postCreate(Object bean) {
 		return callback( postCreates.get( bean.getClass() ), bean );
 	}
 
 	public boolean preRemove(Object bean) {
 		return callback( preRemoves.get( bean.getClass() ), bean );
 	}
 
 	public boolean postRemove(Object bean) {
 		return callback( postRemoves.get( bean.getClass() ), bean );
 	}
 
 	public boolean preUpdate(Object bean) {
 		return callback( preUpdates.get( bean.getClass() ), bean );
 	}
 
 	public boolean postUpdate(Object bean) {
 		return callback( postUpdates.get( bean.getClass() ), bean );
 	}
 
 	public boolean postLoad(Object bean) {
 		return callback( postLoads.get( bean.getClass() ), bean );
 	}
 
 
 	private boolean callback(Callback[] callbacks, Object bean) {
 		if ( callbacks != null && callbacks.length != 0 ) {
 			for ( Callback callback : callbacks ) {
 				callback.invoke( bean );
 			}
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 
 	private void addCallback(
 			XClass entity, HashMap<Class, Callback[]> map, Class annotation, ReflectionManager reflectionManager
 	) {
 		Callback[] callbacks = null;
 		callbacks = CallbackResolver.resolveCallback( entity, annotation, reflectionManager );
 		map.put( reflectionManager.toClass( entity ), callbacks );
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/HibernateEntityManagerEventListener.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/HibernateEntityManagerEventListener.java
new file mode 100644
index 0000000000..71a004eedb
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/HibernateEntityManagerEventListener.java
@@ -0,0 +1,32 @@
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
+package org.hibernate.ejb.event;
+
+/**
+ * Marker interface for handling listener duplication checking (to avoid multiple registrations).
+ *
+ * @author Steve Ebersole
+ */
+public interface HibernateEntityManagerEventListener {
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaEventListenerRegistration.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaEventListenerRegistration.java
new file mode 100644
index 0000000000..c4324ca888
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaEventListenerRegistration.java
@@ -0,0 +1,170 @@
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
+package org.hibernate.ejb.event;
+
+import java.util.Iterator;
+import java.util.Map;
+
+import org.hibernate.HibernateException;
+import org.hibernate.MappingException;
+import org.hibernate.annotations.common.reflection.ReflectionManager;
+import org.hibernate.cfg.Mappings;
+import org.hibernate.ejb.AvailableSettings;
+import org.hibernate.event.EventListenerRegistration;
+import org.hibernate.event.EventType;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.secure.JACCPreDeleteEventListener;
+import org.hibernate.secure.JACCPreInsertEventListener;
+import org.hibernate.secure.JACCPreLoadEventListener;
+import org.hibernate.secure.JACCPreUpdateEventListener;
+import org.hibernate.secure.JACCSecurityListener;
+import org.hibernate.service.classloading.spi.ClassLoaderService;
+import org.hibernate.service.event.spi.DuplicationStrategy;
+import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
+
+/**
+ * Prepare the HEM-specific event listeners.
+ * 
+ * @todo : tie this in with the "service locator" pattern defined by HHH-5562
+ * 
+ * @author Steve Ebersole
+ */
+public class JpaEventListenerRegistration implements EventListenerRegistration {
+	private static final DuplicationStrategy JPA_DUPLICATION_STRATEGY = new DuplicationStrategy() {
+		@Override
+		public boolean areMatch(Object listener, Object original) {
+			return listener.getClass().equals( original.getClass() ) &&
+					HibernateEntityManagerEventListener.class.isInstance( original );
+		}
+
+		@Override
+		public Action getAction() {
+			return Action.KEEP_ORIGINAL;
+		}
+	};
+
+	private static final DuplicationStrategy JACC_DUPLICATION_STRATEGY = new DuplicationStrategy() {
+		@Override
+		public boolean areMatch(Object listener, Object original) {
+			return listener.getClass().equals( original.getClass() ) &&
+					JACCSecurityListener.class.isInstance( original );
+		}
+
+		@Override
+		public Action getAction() {
+			return Action.KEEP_ORIGINAL;
+		}
+	};
+
+	@Override
+	@SuppressWarnings( {"unchecked"})
+	public void apply(ServiceRegistry serviceRegistry, Mappings mappings, Map<?,?> configValues) {
+		boolean isSecurityEnabled = configValues.containsKey( AvailableSettings.JACC_ENABLED );
+
+		EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
+		eventListenerRegistry.addDuplicationStrategy( JPA_DUPLICATION_STRATEGY );
+		eventListenerRegistry.addDuplicationStrategy( JACC_DUPLICATION_STRATEGY );
+
+		// op listeners
+		eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, EJB3AutoFlushEventListener.INSTANCE );
+		eventListenerRegistry.setListeners( EventType.DELETE, new EJB3DeleteEventListener() );
+		eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, new EJB3FlushEntityEventListener() );
+		eventListenerRegistry.setListeners( EventType.FLUSH, EJB3FlushEventListener.INSTANCE );
+		eventListenerRegistry.setListeners( EventType.MERGE, new EJB3MergeEventListener() );
+		eventListenerRegistry.setListeners( EventType.PERSIST, new EJB3PersistEventListener() );
+		eventListenerRegistry.setListeners( EventType.PERSIST_ONFLUSH, new EJB3PersistOnFlushEventListener() );
+		eventListenerRegistry.setListeners( EventType.SAVE, new EJB3SaveEventListener() );
+		eventListenerRegistry.setListeners( EventType.SAVE_UPDATE, new EJB3SaveOrUpdateEventListener() );
+
+		// pre op listeners
+		if ( isSecurityEnabled ) {
+			eventListenerRegistry.prependListeners( EventType.PRE_DELETE, new JACCPreDeleteEventListener() );
+			eventListenerRegistry.prependListeners( EventType.PRE_INSERT, new JACCPreInsertEventListener() );
+			eventListenerRegistry.prependListeners( EventType.PRE_UPDATE, new JACCPreUpdateEventListener() );
+			eventListenerRegistry.prependListeners( EventType.PRE_LOAD, new JACCPreLoadEventListener() );
+		}
+
+		// post op listeners
+		eventListenerRegistry.prependListeners( EventType.POST_DELETE, new EJB3PostDeleteEventListener() );
+		eventListenerRegistry.prependListeners( EventType.POST_INSERT, new EJB3PostInsertEventListener() );
+		eventListenerRegistry.prependListeners( EventType.POST_LOAD, new EJB3PostLoadEventListener() );
+		eventListenerRegistry.prependListeners( EventType.POST_UPDATE, new EJB3PostUpdateEventListener() );
+
+		for ( Map.Entry<?,?> entry : configValues.entrySet() ) {
+			if ( ! String.class.isInstance( entry.getKey() ) ) {
+				continue;
+			}
+			final String propertyName = (String) entry.getKey();
+			if ( ! propertyName.startsWith( AvailableSettings.EVENT_LISTENER_PREFIX ) ) {
+				continue;
+			}
+			final String eventTypeName = propertyName.substring( AvailableSettings.EVENT_LISTENER_PREFIX.length() + 1 );
+			final EventType eventType = EventType.resolveEventTypeByName( eventTypeName );
+			final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
+			eventListenerGroup.clear();
+			for ( String listenerImpl : ( (String) entry.getValue() ).split( " ," ) ) {
+				eventListenerGroup.appendListener( instantiate( listenerImpl, serviceRegistry ) );
+			}
+		}
+
+		// todo : we may need to account for callback handlers previously set (shared across EMFs)
+
+		final EntityCallbackHandler callbackHandler = new EntityCallbackHandler();
+		Iterator classes = mappings.iterateClasses();
+		ReflectionManager reflectionManager = mappings.getReflectionManager();
+		while ( classes.hasNext() ) {
+			PersistentClass clazz = (PersistentClass) classes.next();
+			if ( clazz.getClassName() == null ) {
+				//we can have non java class persisted by hibernate
+				continue;
+			}
+			try {
+				callbackHandler.add( reflectionManager.classForName( clazz.getClassName(), this.getClass() ), reflectionManager );
+			}
+			catch (ClassNotFoundException e) {
+				throw new MappingException( "entity class not found: " + clazz.getNodeName(), e );
+			}
+		}
+
+		for ( EventType eventType : EventType.values() ) {
+			final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
+			for ( Object listener : eventListenerGroup.listeners() ) {
+				if ( CallbackHandlerConsumer.class.isInstance( listener ) ) {
+					( (CallbackHandlerConsumer) listener ).setCallbackHandler( callbackHandler );
+				}
+			}
+		}
+	}
+
+	private Object instantiate(String listenerImpl, ServiceRegistry serviceRegistry) {
+		try {
+			return serviceRegistry.getService( ClassLoaderService.class ).classForName( listenerImpl ).newInstance();
+		}
+		catch (Exception e) {
+			throw new HibernateException( "Could not instantiate requested listener [" + listenerImpl + "]", e );
+		}
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/ListenerCallback.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/ListenerCallback.java
index 13a8c64ca4..041fae1a97 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/ListenerCallback.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/ListenerCallback.java
@@ -1,79 +1,82 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Middleware LLC.
+ * distributed under license by Red Hat Inc.
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
 package org.hibernate.ejb.event;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
+
 import org.hibernate.internal.util.ReflectHelper;
 
 /**
  * @author <a href="mailto:kabir.khan@jboss.org">Kabir Khan</a>
  */
 public class ListenerCallback extends Callback {
 	protected transient Object listener;
 
 	public ListenerCallback(Method callbackMethod, Object listener) {
 		super( callbackMethod );
 		this.listener = listener;
 	}
 
 	@Override
     public void invoke(Object bean) {
 		try {
 			callbackMethod.invoke( listener, new Object[]{bean} );
 		}
 		catch (InvocationTargetException e) {
 			//keep runtime exceptions as is
 			if ( e.getTargetException() instanceof RuntimeException ) {
 				throw (RuntimeException) e.getTargetException();
 			}
 			else {
 				throw new RuntimeException( e.getTargetException() );
 			}
 		}
 		catch (Exception e) {
 			throw new RuntimeException( e );
 		}
 	}
 
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		oos.defaultWriteObject();
 		oos.writeObject( listener.getClass().getName() );
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		ois.defaultReadObject();
 		String listenerClass = (String) ois.readObject();
 		try {
 			listener = ReflectHelper.classForName( listenerClass, this.getClass() ).newInstance();
 		}
 		catch (InstantiationException e) {
 			throw new ClassNotFoundException( "Unable to load class:" + listenerClass, e );
 		}
 		catch (IllegalAccessException e) {
 			throw new ClassNotFoundException( "Unable to load class:" + listenerClass, e );
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/PackagedEntityManagerTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/PackagedEntityManagerTest.java
index 9219063039..34788c7ac2 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/PackagedEntityManagerTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/PackagedEntityManagerTest.java
@@ -1,484 +1,487 @@
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
 package org.hibernate.ejb.test.packaging;
 
 import javax.persistence.EntityManager;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.Persistence;
 import javax.persistence.PersistenceException;
 import java.io.File;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Properties;
 
 import org.hibernate.ejb.AvailableSettings;
 import org.hibernate.ejb.HibernateEntityManagerFactory;
 import org.hibernate.ejb.test.Distributor;
 import org.hibernate.ejb.test.Item;
 import org.hibernate.ejb.test.pack.cfgxmlpar.Morito;
 import org.hibernate.ejb.test.pack.defaultpar.ApplicationServer;
 import org.hibernate.ejb.test.pack.defaultpar.IncrementListener;
 import org.hibernate.ejb.test.pack.defaultpar.Lighter;
 import org.hibernate.ejb.test.pack.defaultpar.Money;
 import org.hibernate.ejb.test.pack.defaultpar.Mouse;
 import org.hibernate.ejb.test.pack.defaultpar.OtherIncrementListener;
 import org.hibernate.ejb.test.pack.defaultpar.Version;
 import org.hibernate.ejb.test.pack.defaultpar_1_0.ApplicationServer1;
 import org.hibernate.ejb.test.pack.defaultpar_1_0.Lighter1;
 import org.hibernate.ejb.test.pack.defaultpar_1_0.Mouse1;
 import org.hibernate.ejb.test.pack.defaultpar_1_0.Version1;
 import org.hibernate.ejb.test.pack.excludehbmpar.Caipirinha;
 import org.hibernate.ejb.test.pack.explodedpar.Carpet;
 import org.hibernate.ejb.test.pack.explodedpar.Elephant;
 import org.hibernate.ejb.test.pack.externaljar.Scooter;
 import org.hibernate.ejb.test.pack.spacepar.Bug;
 import org.hibernate.ejb.test.pack.various.Airplane;
 import org.hibernate.ejb.test.pack.various.Seat;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.event.EventListeners;
+import org.hibernate.event.EventType;
 import org.hibernate.internal.util.ConfigHelper;
+import org.hibernate.service.event.spi.EventListenerRegistry;
 import org.hibernate.stat.Statistics;
 
 import org.junit.Test;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * In this test we verify that  it is possible to bootstrap Hibernate/JPA from
  * various bundles (war, par, ...) using {@code Persistence.createEntityManagerFactory()}
  * <p/>
  * Each test will before its run build the required bundle and place them into the classpath.
  *
  * @author Gavin King
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public class PackagedEntityManagerTest extends PackagingTestCase {
 	@Test
 	public void testDefaultPar() throws Exception {
 		File testPackage = buildDefaultPar();
 		addPackageToClasspath( testPackage );
 
 		// run the test
 		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "defaultpar", new HashMap() );
 		EntityManager em = emf.createEntityManager();
 		ApplicationServer as = new ApplicationServer();
 		as.setName( "JBoss AS" );
 		Version v = new Version();
 		v.setMajor( 4 );
 		v.setMinor( 0 );
 		v.setMicro( 3 );
 		as.setVersion( v );
 		Mouse mouse = new Mouse();
 		mouse.setName( "mickey" );
 		em.getTransaction().begin();
 		em.persist( as );
 		em.persist( mouse );
 		assertEquals( 1, em.createNamedQuery( "allMouse" ).getResultList().size() );
 		Lighter lighter = new Lighter();
 		lighter.name = "main";
 		lighter.power = " 250 W";
 		em.persist( lighter );
 		em.flush();
 		em.remove( lighter );
 		em.remove( mouse );
 		assertNotNull( as.getId() );
 		em.remove( as );
 		em.getTransaction().commit();
 		em.close();
 		emf.close();
 	}
 
 	@Test
 	public void testDefaultParForPersistence_1_0() throws Exception {
 		File testPackage = buildDefaultPar_1_0();
 		addPackageToClasspath( testPackage );
 
 		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "defaultpar_1_0", new HashMap() );
 		EntityManager em = emf.createEntityManager();
 		ApplicationServer1 as = new ApplicationServer1();
 		as.setName( "JBoss AS" );
 		Version1 v = new Version1();
 		v.setMajor( 4 );
 		v.setMinor( 0 );
 		v.setMicro( 3 );
 		as.setVersion( v );
 		Mouse1 mouse = new Mouse1();
 		mouse.setName( "mickey" );
 		em.getTransaction().begin();
 		em.persist( as );
 		em.persist( mouse );
 		assertEquals( 1, em.createNamedQuery( "allMouse_1_0" ).getResultList().size() );
 		Lighter1 lighter = new Lighter1();
 		lighter.name = "main";
 		lighter.power = " 250 W";
 		em.persist( lighter );
 		em.flush();
 		em.remove( lighter );
 		em.remove( mouse );
 		assertNotNull( as.getId() );
 		em.remove( as );
 		em.getTransaction().commit();
 		em.close();
 		emf.close();
 	}
 
 	@Test
 	public void testListenersDefaultPar() throws Exception {
 		File testPackage = buildDefaultPar();
 		addPackageToClasspath( testPackage );
 
 		IncrementListener.reset();
 		OtherIncrementListener.reset();
 		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "defaultpar", new HashMap() );
 		EntityManager em = emf.createEntityManager();
 		em.getTransaction().begin();
 		ApplicationServer as = new ApplicationServer();
 		as.setName( "JBoss AS" );
 		Version v = new Version();
 		v.setMajor( 4 );
 		v.setMinor( 0 );
 		v.setMicro( 3 );
 		as.setVersion( v );
 		em.persist( as );
 		em.flush();
 		assertEquals( "Failure in default listeners", 1, IncrementListener.getIncrement() );
 		assertEquals( "Failure in XML overriden listeners", 1, OtherIncrementListener.getIncrement() );
 
 		Mouse mouse = new Mouse();
 		mouse.setName( "mickey" );
 		em.persist( mouse );
 		em.flush();
 		assertEquals( "Failure in @ExcludeDefaultListeners", 1, IncrementListener.getIncrement() );
 		assertEquals( 1, OtherIncrementListener.getIncrement() );
 
 		Money money = new Money();
 		em.persist( money );
 		em.flush();
 		assertEquals( "Failure in @ExcludeDefaultListeners", 2, IncrementListener.getIncrement() );
 		assertEquals( 1, OtherIncrementListener.getIncrement() );
 
 		em.getTransaction().rollback();
 		em.close();
 		emf.close();
 	}
 
 	@Test
 	public void testExplodedPar() throws Exception {
 		File testPackage = buildExplodedPar();
 		addPackageToClasspath( testPackage );
 
 		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "explodedpar", new HashMap() );
 		EntityManager em = emf.createEntityManager();
 		org.hibernate.ejb.test.pack.explodedpar.Carpet carpet = new Carpet();
 		Elephant el = new Elephant();
 		el.setName( "Dumbo" );
 		carpet.setCountry( "Turkey" );
 		em.getTransaction().begin();
 		em.persist( carpet );
 		em.persist( el );
 		assertEquals( 1, em.createNamedQuery( "allCarpet" ).getResultList().size() );
 		assertNotNull( carpet.getId() );
 		em.remove( carpet );
 		em.getTransaction().commit();
 		em.close();
 		emf.close();
 	}
 
 	@Test
 	public void testExcludeHbmPar() throws Exception {
 		File testPackage = buildExcludeHbmPar();
 		addPackageToClasspath( testPackage );
 
 		EntityManagerFactory emf = null;
 		try {
 			emf = Persistence.createEntityManagerFactory( "excludehbmpar", new HashMap() );
 		}
 		catch ( PersistenceException e ) {
 			Throwable nested = e.getCause();
 			if ( nested == null ) {
 				throw e;
 			}
 			nested = nested.getCause();
 			if ( nested == null ) {
 				throw e;
 			}
 			if ( !( nested instanceof ClassNotFoundException ) ) {
 				throw e;
 			}
 			fail( "Try to process hbm file: " + e.getMessage() );
 		}
 		EntityManager em = emf.createEntityManager();
 		Caipirinha s = new Caipirinha( "Strong" );
 		em.getTransaction().begin();
 		em.persist( s );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		s = em.find( Caipirinha.class, s.getId() );
 		em.remove( s );
 		em.getTransaction().commit();
 		em.close();
 		emf.close();
 	}
 
 	@Test
 	public void testCfgXmlPar() throws Exception {
 		File testPackage = buildCfgXmlPar();
 		addPackageToClasspath( testPackage );
 
 		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "cfgxmlpar", new HashMap() );
 		EntityManager em = emf.createEntityManager();
 		Item i = new Item();
 		i.setDescr( "Blah" );
 		i.setName( "factory" );
 		Morito m = new Morito();
 		m.setPower( "SuperStrong" );
 		em.getTransaction().begin();
 		em.persist( i );
 		em.persist( m );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		i = em.find( Item.class, i.getName() );
 		em.remove( i );
 		em.remove( em.find( Morito.class, m.getId() ) );
 		em.getTransaction().commit();
 		em.close();
 		emf.close();
 	}
 
 	@Test
 	public void testSpacePar() throws Exception {
 		File testPackage = buildSpacePar();
 		addPackageToClasspath( testPackage );
 
 		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "space par", new HashMap() );
 		EntityManager em = emf.createEntityManager();
 		Bug bug = new Bug();
 		bug.setSubject( "Spaces in directory name don't play well on Windows" );
 		em.getTransaction().begin();
 		em.persist( bug );
 		em.flush();
 		em.remove( bug );
 		assertNotNull( bug.getId() );
 		em.getTransaction().rollback();
 		em.close();
 		emf.close();
 	}
 
 	@Test
 	public void testOverriddenPar() throws Exception {
 		File testPackage = buildOverridenPar();
 		addPackageToClasspath( testPackage );
 
 		HashMap properties = new HashMap();
 		properties.put( AvailableSettings.JTA_DATASOURCE, null );
 		Properties p = new Properties();
 		p.load( ConfigHelper.getResourceAsStream( "/overridenpar.properties" ) );
 		properties.putAll( p );
 		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "overridenpar", properties );
 		EntityManager em = emf.createEntityManager();
 		org.hibernate.ejb.test.pack.overridenpar.Bug bug = new org.hibernate.ejb.test.pack.overridenpar.Bug();
 		bug.setSubject( "Allow DS overriding" );
 		em.getTransaction().begin();
 		em.persist( bug );
 		em.flush();
 		em.remove( bug );
 		assertNotNull( bug.getId() );
 		em.getTransaction().rollback();
 		em.close();
 		emf.close();
 	}
 
 	@Test
 	public void testListeners() throws Exception {
 		File testPackage = buildExplicitPar();
 		addPackageToClasspath( testPackage );
 
 		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "manager1", new HashMap() );
 		EntityManager em = emf.createEntityManager();
-		EventListeners eventListeners = em.unwrap( SessionImplementor.class ).getListeners();
+		EventListenerRegistry listenerRegistry = em.unwrap( SessionImplementor.class ).getFactory()
+				.getServiceRegistry()
+				.getService( EventListenerRegistry.class );
 		assertEquals(
 				"Explicit pre-insert event through hibernate.ejb.event.pre-insert does not work",
-				eventListeners.getPreInsertEventListeners().length,
-				eventListeners.getPreUpdateEventListeners().length + 1
+				listenerRegistry.getEventListenerGroup( EventType.PRE_INSERT ).count(),
+				listenerRegistry.getEventListenerGroup( EventType.PRE_UPDATE ).count() + 1
 		);
 
 		em.close();
 		emf.close();
 	}
 
 	@Test
 	public void testExtendedEntityManager() throws Exception {
 		File testPackage = buildExplicitPar();
 		addPackageToClasspath( testPackage );
 
 		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "manager1", new HashMap() );
 		EntityManager em = emf.createEntityManager();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.getTransaction().begin();
 		em.persist( item );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		assertTrue( em.contains( item ) );
 
 		em.getTransaction().begin();
 		Item item1 = (Item) em.createQuery( "select i from Item i where descr like 'M%'" ).getSingleResult();
 		assertNotNull( item1 );
 		assertSame( item, item1 );
 		item.setDescr( "Micro$oft wireless mouse" );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		assertTrue( em.contains( item ) );
 
 		em.getTransaction().begin();
 		item1 = em.find( Item.class, "Mouse" );
 		assertSame( item, item1 );
 		em.getTransaction().commit();
 		assertTrue( em.contains( item ) );
 
 		item1 = em.find( Item.class, "Mouse" );
 		assertSame( item, item1 );
 		assertTrue( em.contains( item ) );
 
 		item1 = (Item) em.createQuery( "select i from Item i where descr like 'M%'" ).getSingleResult();
 		assertNotNull( item1 );
 		assertSame( item, item1 );
 		assertTrue( em.contains( item ) );
 
 		em.getTransaction().begin();
 		assertTrue( em.contains( item ) );
 		em.remove( item );
 		em.remove( item ); //second remove should be a no-op
 		em.getTransaction().commit();
 
 		em.close();
 		emf.close();
 	}
 
 	@Test
 	public void testConfiguration() throws Exception {
 		File testPackage = buildExplicitPar();
 		addPackageToClasspath( testPackage );
 
 		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "manager1", new HashMap() );
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		Distributor res = new Distributor();
 		res.setName( "Bruce" );
 		item.setDistributors( new HashSet<Distributor>() );
 		item.getDistributors().add( res );
 		Statistics stats = ( (HibernateEntityManagerFactory) emf ).getSessionFactory().getStatistics();
 		stats.clear();
 		stats.setStatisticsEnabled( true );
 
 		EntityManager em = emf.createEntityManager();
 		em.getTransaction().begin();
 
 		em.persist( res );
 		em.persist( item );
 		assertTrue( em.contains( item ) );
 
 		em.getTransaction().commit();
 		em.close();
 
 		assertEquals( 1, stats.getSecondLevelCachePutCount() );
 		assertEquals( 0, stats.getSecondLevelCacheHitCount() );
 
 		em = emf.createEntityManager();
 		em.getTransaction().begin();
 		Item second = em.find( Item.class, item.getName() );
 		assertEquals( 1, second.getDistributors().size() );
 		assertEquals( 1, stats.getSecondLevelCacheHitCount() );
 		em.getTransaction().commit();
 		em.close();
 
 		em = emf.createEntityManager();
 		em.getTransaction().begin();
 		second = em.find( Item.class, item.getName() );
 		assertEquals( 1, second.getDistributors().size() );
 		assertEquals( 3, stats.getSecondLevelCacheHitCount() );
 		for ( Distributor distro : second.getDistributors() ) {
 			em.remove( distro );
 		}
 		em.remove( second );
 		em.getTransaction().commit();
 		em.close();
 
 		stats.clear();
 		stats.setStatisticsEnabled( false );
 		emf.close();
 	}
 
 	@Test
 	public void testExternalJar() throws Exception {
 		File externalJar = buildExternalJar();
 		File testPackage = buildExplicitPar();
 		addPackageToClasspath( testPackage, externalJar );
 
 		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "manager1", new HashMap() );
 		EntityManager em = emf.createEntityManager();
 		Scooter s = new Scooter();
 		s.setModel( "Abadah" );
 		s.setSpeed( 85l );
 		em.getTransaction().begin();
 		em.persist( s );
 		em.getTransaction().commit();
 		em.close();
 		em = emf.createEntityManager();
 		em.getTransaction().begin();
 		s = em.find( Scooter.class, s.getModel() );
 		assertEquals( new Long( 85 ), s.getSpeed() );
 		em.remove( s );
 		em.getTransaction().commit();
 		em.close();
 		emf.close();
 	}
 
 	@Test
 	public void testORMFileOnMainAndExplicitJars() throws Exception {
 		File testPackage = buildExplicitPar();
 		addPackageToClasspath( testPackage );
 
 		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "manager1", new HashMap() );
 		EntityManager em = emf.createEntityManager();
 		Seat seat = new Seat();
 		seat.setNumber( "3B" );
 		Airplane plane = new Airplane();
 		plane.setSerialNumber( "75924418409052355" );
 		em.getTransaction().begin();
 		em.persist( seat );
 		em.persist( plane );
 		em.flush();
 		em.getTransaction().rollback();
 		em.close();
 		emf.close();
 	}
 }
\ No newline at end of file
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
index 64e462b28f..68e4ecf7f3 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
@@ -1,315 +1,311 @@
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
 import org.hibernate.event.EventListeners;
 import org.hibernate.impl.CriteriaImpl;
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
 
-    public EventListeners getListeners() {
-        return delegate.getListeners();
-    }
-
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
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/BaseEnversCollectionEventListener.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/BaseEnversCollectionEventListener.java
new file mode 100644
index 0000000000..b0e8742b99
--- /dev/null
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/BaseEnversCollectionEventListener.java
@@ -0,0 +1,243 @@
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
+package org.hibernate.envers.event;
+
+import java.io.Serializable;
+import java.util.List;
+
+import org.hibernate.collection.PersistentCollection;
+import org.hibernate.engine.CollectionEntry;
+import org.hibernate.envers.RevisionType;
+import org.hibernate.envers.configuration.AuditConfiguration;
+import org.hibernate.envers.entities.EntityConfiguration;
+import org.hibernate.envers.entities.RelationDescription;
+import org.hibernate.envers.entities.mapper.PersistentCollectionChangeData;
+import org.hibernate.envers.entities.mapper.id.IdMapper;
+import org.hibernate.envers.synchronization.AuditProcess;
+import org.hibernate.envers.synchronization.work.AuditWorkUnit;
+import org.hibernate.envers.synchronization.work.CollectionChangeWorkUnit;
+import org.hibernate.envers.synchronization.work.FakeBidirectionalRelationWorkUnit;
+import org.hibernate.envers.synchronization.work.PersistentCollectionChangeWorkUnit;
+import org.hibernate.event.AbstractCollectionEvent;
+import org.hibernate.persister.collection.AbstractCollectionPersister;
+
+/**
+ * Base class for Envers' collection event related listeners
+ *
+ * @author Adam Warski (adam at warski dot org)
+ * @author Hern�n Chanfreau
+ * @author Steve Ebersole
+ */
+public abstract class BaseEnversCollectionEventListener extends BaseEnversEventListener {
+	protected BaseEnversCollectionEventListener(AuditConfiguration enversConfiguration) {
+		super( enversConfiguration );
+	}
+
+    protected final CollectionEntry getCollectionEntry(AbstractCollectionEvent event) {
+        return event.getSession().getPersistenceContext().getCollectionEntry(event.getCollection());
+    }
+
+    protected final void onCollectionAction(
+			AbstractCollectionEvent event,
+			PersistentCollection newColl,
+			Serializable oldColl,
+			CollectionEntry collectionEntry) {
+        String entityName = event.getAffectedOwnerEntityName();
+        if ( ! enversConfiguration().getGlobalCfg().isGenerateRevisionsForCollections() ) {
+            return;
+        }
+        if ( enversConfiguration().getEntCfg().isVersioned( entityName ) ) {
+            AuditProcess auditProcess = enversConfiguration().getSyncManager().get(event.getSession());
+
+            String ownerEntityName = ((AbstractCollectionPersister) collectionEntry.getLoadedPersister()).getOwnerEntityName();
+            String referencingPropertyName = collectionEntry.getRole().substring(ownerEntityName.length() + 1);
+
+            // Checking if this is not a "fake" many-to-one bidirectional relation. The relation description may be
+            // null in case of collections of non-entities.
+            RelationDescription rd = searchForRelationDescription( entityName, referencingPropertyName );
+            if ( rd != null && rd.getMappedByPropertyName() != null ) {
+                generateFakeBidirecationalRelationWorkUnits(
+						auditProcess,
+						newColl,
+						oldColl,
+						entityName,
+                        referencingPropertyName,
+						event,
+						rd
+				);
+            }
+			else {
+                PersistentCollectionChangeWorkUnit workUnit = new PersistentCollectionChangeWorkUnit(
+						event.getSession(),
+						entityName,
+						enversConfiguration(),
+						newColl,
+						collectionEntry,
+						oldColl,
+						event.getAffectedOwnerIdOrNull(),
+						referencingPropertyName
+				);
+				auditProcess.addWorkUnit( workUnit );
+
+                if (workUnit.containsWork()) {
+                    // There are some changes: a revision needs also be generated for the collection owner
+                    auditProcess.addWorkUnit(
+							new CollectionChangeWorkUnit(
+									event.getSession(),
+									event.getAffectedOwnerEntityName(),
+									enversConfiguration(),
+									event.getAffectedOwnerIdOrNull(),
+									event.getAffectedOwnerOrNull()
+							)
+					);
+
+                    generateBidirectionalCollectionChangeWorkUnits( auditProcess, event, workUnit, rd );
+                }
+            }
+        }
+    }
+
+    /**
+     * Looks up a relation description corresponding to the given property in the given entity. If no description is
+     * found in the given entity, the parent entity is checked (so that inherited relations work).
+	 *
+     * @param entityName Name of the entity, in which to start looking.
+     * @param referencingPropertyName The name of the property.
+	 * 
+     * @return A found relation description corresponding to the given entity or {@code null}, if no description can
+     * be found.
+     */
+    private RelationDescription searchForRelationDescription(String entityName, String referencingPropertyName) {
+        EntityConfiguration configuration = enversConfiguration().getEntCfg().get( entityName );
+        RelationDescription rd = configuration.getRelationDescription(referencingPropertyName);
+        if ( rd == null && configuration.getParentEntityName() != null ) {
+            return searchForRelationDescription( configuration.getParentEntityName(), referencingPropertyName );
+        }
+
+        return rd;
+    }
+
+    private void generateFakeBidirecationalRelationWorkUnits(
+			AuditProcess auditProcess,
+			PersistentCollection newColl,
+			Serializable oldColl,
+			String collectionEntityName,
+			String referencingPropertyName,
+			AbstractCollectionEvent event,
+			RelationDescription rd) {
+        // First computing the relation changes
+        List<PersistentCollectionChangeData> collectionChanges = enversConfiguration()
+				.getEntCfg()
+				.get( collectionEntityName )
+				.getPropertyMapper()
+                .mapCollectionChanges( referencingPropertyName, newColl, oldColl, event.getAffectedOwnerIdOrNull() );
+
+        // Getting the id mapper for the related entity, as the work units generated will corrspond to the related
+        // entities.
+        String relatedEntityName = rd.getToEntityName();
+        IdMapper relatedIdMapper = enversConfiguration().getEntCfg().get(relatedEntityName).getIdMapper();
+
+        // For each collection change, generating the bidirectional work unit.
+        for ( PersistentCollectionChangeData changeData : collectionChanges ) {
+            Object relatedObj = changeData.getChangedElement();
+            Serializable relatedId = (Serializable) relatedIdMapper.mapToIdFromEntity(relatedObj);
+            RevisionType revType = (RevisionType) changeData.getData().get(
+					enversConfiguration().getAuditEntCfg().getRevisionTypePropName()
+			);
+
+            // This can be different from relatedEntityName, in case of inheritance (the real entity may be a subclass
+            // of relatedEntityName).
+            String realRelatedEntityName = event.getSession().bestGuessEntityName(relatedObj);
+
+            // By default, the nested work unit is a collection change work unit.
+            AuditWorkUnit nestedWorkUnit = new CollectionChangeWorkUnit(
+					event.getSession(),
+					realRelatedEntityName,
+					enversConfiguration(),
+                    relatedId,
+					relatedObj
+			);
+
+            auditProcess.addWorkUnit(
+					new FakeBidirectionalRelationWorkUnit(
+							event.getSession(),
+							realRelatedEntityName,
+							enversConfiguration(),
+							relatedId,
+							referencingPropertyName,
+							event.getAffectedOwnerOrNull(),
+							rd,
+							revType,
+							changeData.getChangedElementIndex(),
+							nestedWorkUnit
+					)
+			);
+        }
+
+        // We also have to generate a collection change work unit for the owning entity.
+        auditProcess.addWorkUnit(
+				new CollectionChangeWorkUnit(
+						event.getSession(),
+						collectionEntityName,
+						enversConfiguration(),
+						event.getAffectedOwnerIdOrNull(),
+						event.getAffectedOwnerOrNull()
+				)
+		);
+    }
+
+    private void generateBidirectionalCollectionChangeWorkUnits(
+			AuditProcess auditProcess,
+			AbstractCollectionEvent event,
+			PersistentCollectionChangeWorkUnit workUnit,
+			RelationDescription rd) {
+        // Checking if this is enabled in configuration ...
+        if ( ! enversConfiguration().getGlobalCfg().isGenerateRevisionsForCollections() ) {
+            return;
+        }
+
+        // Checking if this is not a bidirectional relation - then, a revision needs also be generated for
+        // the other side of the relation.
+        // relDesc can be null if this is a collection of simple values (not a relation).
+        if ( rd != null && rd.isBidirectional() ) {
+            String relatedEntityName = rd.getToEntityName();
+            IdMapper relatedIdMapper = enversConfiguration().getEntCfg().get( relatedEntityName ).getIdMapper();
+
+            for ( PersistentCollectionChangeData changeData : workUnit.getCollectionChanges() ) {
+                Object relatedObj = changeData.getChangedElement();
+                Serializable relatedId = (Serializable) relatedIdMapper.mapToIdFromEntity( relatedObj );
+
+                auditProcess.addWorkUnit(
+						new CollectionChangeWorkUnit(
+								event.getSession(),
+								relatedEntityName,
+								enversConfiguration(),
+								relatedId,
+								relatedObj
+						)
+				);
+            }
+        }
+    }
+}
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/BaseEnversEventListener.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/BaseEnversEventListener.java
new file mode 100644
index 0000000000..2c5de73be6
--- /dev/null
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/BaseEnversEventListener.java
@@ -0,0 +1,132 @@
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
+package org.hibernate.envers.event;
+
+import java.io.Serializable;
+
+import org.hibernate.engine.SessionImplementor;
+import org.hibernate.envers.configuration.AuditConfiguration;
+import org.hibernate.envers.entities.RelationDescription;
+import org.hibernate.envers.entities.RelationType;
+import org.hibernate.envers.entities.mapper.id.IdMapper;
+import org.hibernate.envers.synchronization.AuditProcess;
+import org.hibernate.envers.synchronization.work.CollectionChangeWorkUnit;
+import org.hibernate.envers.tools.Tools;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.proxy.HibernateProxy;
+
+/**
+ * Base class for all Envers event listeners
+ *
+ * @author Adam Warski (adam at warski dot org)
+ * @author Hern�n Chanfreau
+ * @author Steve Ebersole
+ */
+public abstract class BaseEnversEventListener implements EnversListener {
+	private AuditConfiguration enversConfiguration;
+
+	protected BaseEnversEventListener(AuditConfiguration enversConfiguration) {
+		this.enversConfiguration = enversConfiguration;
+	}
+
+	protected AuditConfiguration enversConfiguration() {
+		return enversConfiguration;
+	}
+
+	protected final void generateBidirectionalCollectionChangeWorkUnits(
+			AuditProcess auditProcess,
+			EntityPersister entityPersister,
+			String entityName,
+			Object[] newState,
+			Object[] oldState,
+			SessionImplementor session) {
+		// Checking if this is enabled in configuration ...
+		if ( ! enversConfiguration.getGlobalCfg().isGenerateRevisionsForCollections() ) {
+			return;
+		}
+
+		// Checks every property of the entity, if it is an "owned" to-one relation to another entity.
+		// If the value of that property changed, and the relation is bi-directional, a new revision
+		// for the related entity is generated.
+		String[] propertyNames = entityPersister.getPropertyNames();
+
+		for ( int i=0; i<propertyNames.length; i++ ) {
+			String propertyName = propertyNames[i];
+			RelationDescription relDesc = enversConfiguration.getEntCfg().getRelationDescription(entityName, propertyName);
+			if (relDesc != null && relDesc.isBidirectional() && relDesc.getRelationType() == RelationType.TO_ONE &&
+					relDesc.isInsertable()) {
+				// Checking for changes
+				Object oldValue = oldState == null ? null : oldState[i];
+				Object newValue = newState == null ? null : newState[i];
+
+				if (!Tools.entitiesEqual( session, relDesc.getToEntityName(), oldValue, newValue )) {
+					// We have to generate changes both in the old collection (size decreses) and new collection
+					// (size increases).
+					if (newValue != null) {
+						// relDesc.getToEntityName() doesn't always return the entity name of the value - in case
+						// of subclasses, this will be root class, no the actual class. So it can't be used here.
+						String toEntityName;
+						Serializable id;
+
+						if (newValue instanceof HibernateProxy ) {
+							HibernateProxy hibernateProxy = (HibernateProxy) newValue;
+							toEntityName = session.bestGuessEntityName(newValue);
+							id = hibernateProxy.getHibernateLazyInitializer().getIdentifier();
+							// We've got to initialize the object from the proxy to later read its state.
+							newValue = Tools.getTargetFromProxy(session.getFactory(), hibernateProxy);
+						} else {
+							toEntityName =  session.guessEntityName(newValue);
+
+							IdMapper idMapper = enversConfiguration.getEntCfg().get(toEntityName).getIdMapper();
+							 id = (Serializable) idMapper.mapToIdFromEntity(newValue);
+						}
+
+						auditProcess.addWorkUnit(new CollectionChangeWorkUnit(session, toEntityName, enversConfiguration, id, newValue));
+					}
+
+					if (oldValue != null) {
+						String toEntityName;
+						Serializable id;
+
+						if(oldValue instanceof HibernateProxy) {
+							HibernateProxy hibernateProxy = (HibernateProxy) oldValue;
+							toEntityName = session.bestGuessEntityName(oldValue);
+							id = hibernateProxy.getHibernateLazyInitializer().getIdentifier();
+							// We've got to initialize the object as we'll read it's state anyway.
+							oldValue = Tools.getTargetFromProxy(session.getFactory(), hibernateProxy);
+						} else {
+							toEntityName =  session.guessEntityName(oldValue);
+
+							IdMapper idMapper = enversConfiguration.getEntCfg().get(toEntityName).getIdMapper();
+							id = (Serializable) idMapper.mapToIdFromEntity(oldValue);
+						}
+
+						auditProcess.addWorkUnit(new CollectionChangeWorkUnit(session, toEntityName, enversConfiguration, id, oldValue));
+					}
+				}
+			}
+		}
+	}
+
+}
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversEventListenerRegistration.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversEventListenerRegistration.java
new file mode 100644
index 0000000000..610f2c7691
--- /dev/null
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversEventListenerRegistration.java
@@ -0,0 +1,58 @@
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
+package org.hibernate.envers.event;
+
+import java.util.Map;
+
+import org.hibernate.cfg.Mappings;
+import org.hibernate.envers.configuration.AuditConfiguration;
+import org.hibernate.event.EventListenerRegistration;
+import org.hibernate.event.EventType;
+import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.service.spi.ServiceRegistry;
+
+/**
+ * See "transitory" notes on {@link EventListenerRegistration}
+ * 
+ * @author Steve Ebersole
+ */
+public class EnversEventListenerRegistration implements EventListenerRegistration {
+	@Override
+	public void apply(ServiceRegistry serviceRegistry, Mappings mappings, Map<?, ?> configValues) {
+		EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
+		listenerRegistry.addDuplicationStrategy( EnversListenerDuplicationStrategy.INSTANCE );
+
+		// todo : build AuditConfiguration; requires massive changes...
+//		AuditConfiguration auditConfiguration = AuditConfiguration.getFor( ??? )
+		AuditConfiguration auditConfiguration = null;
+
+		// create/add listeners
+		listenerRegistry.appendListeners( EventType.POST_DELETE, new EnversPostDeleteEventListenerImpl( auditConfiguration ) );
+		listenerRegistry.appendListeners( EventType.POST_INSERT, new EnversPostInsertEventListenerImpl( auditConfiguration ) );
+		listenerRegistry.appendListeners( EventType.POST_UPDATE, new EnversPostUpdateEventListenerImpl( auditConfiguration ) );
+		listenerRegistry.appendListeners( EventType.POST_COLLECTION_RECREATE, new EnversPostCollectionRecreateEventListenerImpl( auditConfiguration ) );
+		listenerRegistry.appendListeners( EventType.PRE_COLLECTION_REMOVE, new EnversPreCollectionRemoveEventListenerImpl( auditConfiguration ) );
+		listenerRegistry.appendListeners( EventType.PRE_COLLECTION_UPDATE, new EnversPreCollectionUpdateEventListenerImpl( auditConfiguration ) );
+	}
+}
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversListener.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversListener.java
new file mode 100644
index 0000000000..5f5ae019f7
--- /dev/null
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversListener.java
@@ -0,0 +1,32 @@
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
+package org.hibernate.envers.event;
+
+/**
+ * Marker interface for Envers listeners for duplication handling.
+ *
+ * @author Steve Ebersole
+ */
+public interface EnversListener {
+}
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversListenerDuplicationStrategy.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversListenerDuplicationStrategy.java
new file mode 100644
index 0000000000..6791760298
--- /dev/null
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversListenerDuplicationStrategy.java
@@ -0,0 +1,45 @@
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
+package org.hibernate.envers.event;
+
+import org.hibernate.service.event.spi.DuplicationStrategy;
+
+/**
+ * Event listener duplication strategy for envers
+ *
+ * @author Steve Ebersole
+ */
+public class EnversListenerDuplicationStrategy implements DuplicationStrategy {
+	public static final EnversListenerDuplicationStrategy INSTANCE = new EnversListenerDuplicationStrategy();
+
+	@Override
+	public boolean areMatch(Object listener, Object original) {
+		return listener.getClass().equals( original ) && EnversListener.class.isInstance( listener );
+	}
+
+	@Override
+	public Action getAction() {
+		return Action.KEEP_ORIGINAL;
+	}
+}
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostCollectionRecreateEventListenerImpl.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostCollectionRecreateEventListenerImpl.java
new file mode 100644
index 0000000000..2910cc16ef
--- /dev/null
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostCollectionRecreateEventListenerImpl.java
@@ -0,0 +1,51 @@
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
+package org.hibernate.envers.event;
+
+import org.hibernate.engine.CollectionEntry;
+import org.hibernate.envers.configuration.AuditConfiguration;
+import org.hibernate.event.PostCollectionRecreateEvent;
+import org.hibernate.event.PostCollectionRecreateEventListener;
+
+/**
+ * @author Adam Warski (adam at warski dot org)
+ * @author Hern�n Chanfreau
+ * @author Steve Ebersole
+ */
+public class EnversPostCollectionRecreateEventListenerImpl
+		extends  BaseEnversCollectionEventListener
+		implements PostCollectionRecreateEventListener {
+
+	protected EnversPostCollectionRecreateEventListenerImpl(AuditConfiguration enversConfiguration) {
+		super( enversConfiguration );
+	}
+
+	@Override
+	public void onPostRecreateCollection(PostCollectionRecreateEvent event) {
+        CollectionEntry collectionEntry = getCollectionEntry( event );
+        if ( ! collectionEntry.getLoadedPersister().isInverse() ) {
+            onCollectionAction( event, event.getCollection(), null, collectionEntry );
+        }
+	}
+}
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostDeleteEventListenerImpl.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostDeleteEventListenerImpl.java
new file mode 100644
index 0000000000..68b20095d0
--- /dev/null
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostDeleteEventListenerImpl.java
@@ -0,0 +1,72 @@
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
+package org.hibernate.envers.event;
+
+import org.hibernate.envers.configuration.AuditConfiguration;
+import org.hibernate.envers.synchronization.AuditProcess;
+import org.hibernate.envers.synchronization.work.AuditWorkUnit;
+import org.hibernate.envers.synchronization.work.DelWorkUnit;
+import org.hibernate.event.PostDeleteEvent;
+import org.hibernate.event.PostDeleteEventListener;
+
+/**
+ * @author Adam Warski (adam at warski dot org)
+ * @author Hern�n Chanfreau
+ * @author Steve Ebersole
+ */
+public class EnversPostDeleteEventListenerImpl extends BaseEnversEventListener implements PostDeleteEventListener {
+	protected EnversPostDeleteEventListenerImpl(AuditConfiguration enversConfiguration) {
+		super( enversConfiguration );
+	}
+
+	@Override
+	public void onPostDelete(PostDeleteEvent event) {
+        String entityName = event.getPersister().getEntityName();
+
+        if ( enversConfiguration().getEntCfg().isVersioned( entityName ) ) {
+            AuditProcess auditProcess = enversConfiguration().getSyncManager().get( event.getSession() );
+
+            AuditWorkUnit workUnit = new DelWorkUnit(
+					event.getSession(),
+					event.getPersister().getEntityName(),
+					enversConfiguration(),
+                    event.getId(),
+					event.getPersister(),
+					event.getDeletedState()
+			);
+            auditProcess.addWorkUnit( workUnit );
+
+            if ( workUnit.containsWork() ) {
+                generateBidirectionalCollectionChangeWorkUnits(
+						auditProcess,
+						event.getPersister(),
+						entityName,
+						null,
+                        event.getDeletedState(),
+						event.getSession()
+				);
+			}
+		}
+	}
+}
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostInsertEventListenerImpl.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostInsertEventListenerImpl.java
new file mode 100644
index 0000000000..74b51439d3
--- /dev/null
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostInsertEventListenerImpl.java
@@ -0,0 +1,71 @@
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
+package org.hibernate.envers.event;
+
+import org.hibernate.envers.configuration.AuditConfiguration;
+import org.hibernate.envers.synchronization.AuditProcess;
+import org.hibernate.envers.synchronization.work.AddWorkUnit;
+import org.hibernate.envers.synchronization.work.AuditWorkUnit;
+import org.hibernate.event.PostInsertEvent;
+import org.hibernate.event.PostInsertEventListener;
+
+/**
+ * @author Adam Warski (adam at warski dot org)
+ * @author Hern�n Chanfreau
+ * @author Steve Ebersole
+ */
+public class EnversPostInsertEventListenerImpl extends BaseEnversEventListener implements PostInsertEventListener {
+	public EnversPostInsertEventListenerImpl(AuditConfiguration enversConfiguration) {
+		super( enversConfiguration );
+	}
+
+    public void onPostInsert(PostInsertEvent event) {
+        String entityName = event.getPersister().getEntityName();
+
+        if ( enversConfiguration().getEntCfg().isVersioned( entityName ) ) {
+            AuditProcess auditProcess = enversConfiguration().getSyncManager().get(event.getSession());
+
+            AuditWorkUnit workUnit = new AddWorkUnit(
+					event.getSession(),
+					event.getPersister().getEntityName(),
+					enversConfiguration(),
+                    event.getId(),
+					event.getPersister(),
+					event.getState()
+			);
+            auditProcess.addWorkUnit( workUnit );
+
+            if ( workUnit.containsWork() ) {
+                generateBidirectionalCollectionChangeWorkUnits(
+						auditProcess,
+						event.getPersister(),
+						entityName,
+						event.getState(),
+                        null,
+						event.getSession()
+				);
+            }
+        }
+	}
+}
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostUpdateEventListenerImpl.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostUpdateEventListenerImpl.java
new file mode 100644
index 0000000000..18a9245fb8
--- /dev/null
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPostUpdateEventListenerImpl.java
@@ -0,0 +1,73 @@
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
+package org.hibernate.envers.event;
+
+import org.hibernate.envers.configuration.AuditConfiguration;
+import org.hibernate.envers.synchronization.AuditProcess;
+import org.hibernate.envers.synchronization.work.AuditWorkUnit;
+import org.hibernate.envers.synchronization.work.ModWorkUnit;
+import org.hibernate.event.PostUpdateEvent;
+import org.hibernate.event.PostUpdateEventListener;
+
+/**
+ * @author Adam Warski (adam at warski dot org)
+ * @author Hern�n Chanfreau
+ * @author Steve Ebersole
+ */
+public class EnversPostUpdateEventListenerImpl extends BaseEnversEventListener implements PostUpdateEventListener {
+	protected EnversPostUpdateEventListenerImpl(AuditConfiguration enversConfiguration) {
+		super( enversConfiguration );
+	}
+
+	@Override
+	public void onPostUpdate(PostUpdateEvent event) {
+        String entityName = event.getPersister().getEntityName();
+
+        if ( enversConfiguration().getEntCfg().isVersioned(entityName) ) {
+            AuditProcess auditProcess = enversConfiguration().getSyncManager().get(event.getSession());
+
+            AuditWorkUnit workUnit = new ModWorkUnit(
+					event.getSession(),
+					event.getPersister().getEntityName(),
+					enversConfiguration(),
+                    event.getId(),
+					event.getPersister(),
+					event.getState(),
+					event.getOldState()
+			);
+            auditProcess.addWorkUnit( workUnit );
+
+            if ( workUnit.containsWork() ) {
+                generateBidirectionalCollectionChangeWorkUnits(
+						auditProcess,
+						event.getPersister(),
+						entityName,
+						event.getState(),
+                        event.getOldState(),
+						event.getSession()
+				);
+            }
+        }
+	}
+}
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPreCollectionRemoveEventListenerImpl.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPreCollectionRemoveEventListenerImpl.java
new file mode 100644
index 0000000000..1d13f61b10
--- /dev/null
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPreCollectionRemoveEventListenerImpl.java
@@ -0,0 +1,51 @@
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
+package org.hibernate.envers.event;
+
+import org.hibernate.engine.CollectionEntry;
+import org.hibernate.envers.configuration.AuditConfiguration;
+import org.hibernate.event.PreCollectionRemoveEvent;
+import org.hibernate.event.PreCollectionRemoveEventListener;
+
+/**
+ * @author Adam Warski (adam at warski dot org)
+ * @author Hern�n Chanfreau
+ * @author Steve Ebersole
+ */
+public class EnversPreCollectionRemoveEventListenerImpl
+		extends BaseEnversCollectionEventListener
+		implements PreCollectionRemoveEventListener {
+
+	protected EnversPreCollectionRemoveEventListenerImpl(AuditConfiguration enversConfiguration) {
+		super( enversConfiguration );
+	}
+
+	@Override
+	public void onPreRemoveCollection(PreCollectionRemoveEvent event) {
+        CollectionEntry collectionEntry = getCollectionEntry( event );
+        if ( collectionEntry != null && !collectionEntry.getLoadedPersister().isInverse() ) {
+            onCollectionAction( event, null, collectionEntry.getSnapshot(), collectionEntry );
+        }
+	}
+}
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPreCollectionUpdateEventListenerImpl.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPreCollectionUpdateEventListenerImpl.java
new file mode 100644
index 0000000000..e65baf60ca
--- /dev/null
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversPreCollectionUpdateEventListenerImpl.java
@@ -0,0 +1,51 @@
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
+package org.hibernate.envers.event;
+
+import org.hibernate.engine.CollectionEntry;
+import org.hibernate.envers.configuration.AuditConfiguration;
+import org.hibernate.event.PreCollectionUpdateEvent;
+import org.hibernate.event.PreCollectionUpdateEventListener;
+
+/**
+ * @author Adam Warski (adam at warski dot org)
+ * @author Hern�n Chanfreau
+ * @author Steve Ebersole
+ */
+public class EnversPreCollectionUpdateEventListenerImpl
+		extends BaseEnversCollectionEventListener
+		implements PreCollectionUpdateEventListener {
+
+	protected EnversPreCollectionUpdateEventListenerImpl(AuditConfiguration enversConfiguration) {
+		super( enversConfiguration );
+	}
+
+	@Override
+	public void onPreUpdateCollection(PreCollectionUpdateEvent event) {
+        CollectionEntry collectionEntry = getCollectionEntry( event );
+        if ( ! collectionEntry.getLoadedPersister().isInverse() ) {
+            onCollectionAction( event, event.getCollection(), collectionEntry.getSnapshot(), collectionEntry );
+        }
+	}
+}
