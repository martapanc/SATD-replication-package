diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
index eade926163..ec3ec06f46 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
@@ -1,216 +1,216 @@
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
 import org.hibernate.event.EventSource;
 import org.hibernate.event.EventType;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
-import org.hibernate.service.event.spi.EventListenerGroup;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 
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
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java
index c0a70e56c1..773077bcce 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRecreateAction.java
@@ -1,102 +1,102 @@
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
 import org.hibernate.event.EventType;
 import org.hibernate.event.PostCollectionRecreateEvent;
 import org.hibernate.event.PostCollectionRecreateEventListener;
 import org.hibernate.event.PreCollectionRecreateEvent;
 import org.hibernate.event.PreCollectionRecreateEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
-import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerGroup;
 
 public final class CollectionRecreateAction extends CollectionAction {
 
 	public CollectionRecreateAction(
 			final PersistentCollection collection,
 			final CollectionPersister persister,
 			final Serializable id,
 			final SessionImplementor session) throws CacheException {
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
 		EventListenerGroup<PreCollectionRecreateEventListener> listenerGroup = listenerGroup( EventType.PRE_COLLECTION_RECREATE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PreCollectionRecreateEvent event = new PreCollectionRecreateEvent( getPersister(), getCollection(), eventSource() );
 		for ( PreCollectionRecreateEventListener listener : listenerGroup.listeners() ) {
 			listener.onPreRecreateCollection( event );
 		}
 	}
 
 	private void postRecreate() {
 		EventListenerGroup<PostCollectionRecreateEventListener> listenerGroup = listenerGroup( EventType.POST_COLLECTION_RECREATE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostCollectionRecreateEvent event = new PostCollectionRecreateEvent( getPersister(), getCollection(), eventSource() );
 		for ( PostCollectionRecreateEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostRecreateCollection( event );
 		}
 	}
 }
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java
index 77bac41205..b56607faf6 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionRemoveAction.java
@@ -1,162 +1,162 @@
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
 import org.hibernate.event.EventType;
 import org.hibernate.event.PostCollectionRemoveEvent;
 import org.hibernate.event.PostCollectionRemoveEventListener;
 import org.hibernate.event.PreCollectionRemoveEvent;
 import org.hibernate.event.PreCollectionRemoveEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
-import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerGroup;
 
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
 		EventListenerGroup<PreCollectionRemoveEventListener> listenerGroup = listenerGroup( EventType.PRE_COLLECTION_REMOVE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PreCollectionRemoveEvent event = new PreCollectionRemoveEvent(
 				getPersister(),
 				getCollection(),
 				eventSource(),
 				affectedOwner
 		);
 		for ( PreCollectionRemoveEventListener listener : listenerGroup.listeners() ) {
 			listener.onPreRemoveCollection( event );
 		}
 	}
 
 	private void postRemove() {
 		EventListenerGroup<PostCollectionRemoveEventListener> listenerGroup = listenerGroup( EventType.POST_COLLECTION_REMOVE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostCollectionRemoveEvent event = new PostCollectionRemoveEvent(
 				getPersister(),
 				getCollection(),
 				eventSource(),
 				affectedOwner
 		);
 		for ( PostCollectionRemoveEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostRemoveCollection( event );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
index 4b5b08986b..93907573b7 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
@@ -1,141 +1,138 @@
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
 import org.hibernate.event.EventType;
 import org.hibernate.event.PostCollectionUpdateEvent;
 import org.hibernate.event.PostCollectionUpdateEventListener;
-import org.hibernate.event.PreCollectionRemoveEvent;
-import org.hibernate.event.PreCollectionRemoveEventListener;
 import org.hibernate.event.PreCollectionUpdateEvent;
 import org.hibernate.event.PreCollectionUpdateEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
-import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerGroup;
 
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
 		EventListenerGroup<PreCollectionUpdateEventListener> listenerGroup = listenerGroup( EventType.PRE_COLLECTION_UPDATE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PreCollectionUpdateEvent event = new PreCollectionUpdateEvent(
 				getPersister(),
 				getCollection(),
 				eventSource()
 		);
 		for ( PreCollectionUpdateEventListener listener : listenerGroup.listeners() ) {
 			listener.onPreUpdateCollection( event );
 		}
 	}
 
 	private void postUpdate() {
 		EventListenerGroup<PostCollectionUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_COLLECTION_UPDATE );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostCollectionUpdateEvent event = new PostCollectionUpdateEvent(
 				getPersister(),
 				getCollection(),
 				eventSource()
 		);
 		for ( PostCollectionUpdateEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostUpdateCollection( event );
 		}
 	}
 }
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
index 606a97d67c..0791105a9b 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
@@ -1,205 +1,205 @@
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
 import org.hibernate.event.EventSource;
 import org.hibernate.event.EventType;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
-import org.hibernate.service.event.spi.EventListenerGroup;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 
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
index 833ff2123d..7adba234f2 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
@@ -1,181 +1,180 @@
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
-import org.hibernate.event.EventSource;
 import org.hibernate.event.EventType;
 import org.hibernate.event.PostDeleteEvent;
 import org.hibernate.event.PostDeleteEventListener;
 import org.hibernate.event.PreDeleteEvent;
 import org.hibernate.event.PreDeleteEventListener;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerGroup;
 
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
 		boolean veto = false;
 		EventListenerGroup<PreDeleteEventListener> listenerGroup = listenerGroup( EventType.PRE_DELETE );
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
 		EventListenerGroup<PostDeleteEventListener> listenerGroup = listenerGroup( EventType.POST_DELETE );
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
 
 	private void postCommitDelete() {
 		EventListenerGroup<PostDeleteEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_DELETE );
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
 		return ! listenerGroup( EventType.POST_COMMIT_DELETE ).isEmpty();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
index c36731f3f0..9b70a7c34a 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
@@ -1,206 +1,206 @@
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
 import org.hibernate.event.EventType;
 import org.hibernate.event.PostInsertEvent;
 import org.hibernate.event.PostInsertEventListener;
 import org.hibernate.event.PreInsertEvent;
 import org.hibernate.event.PreInsertEventListener;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerGroup;
 
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
 		return ! listenerGroup( EventType.POST_COMMIT_INSERT ).isEmpty();
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
 
 		EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostInsertEvent event = new PostInsertEvent(
 				getInstance(),
 				generatedId,
 				state,
 				getPersister(),
 				eventSource()
 		);
 		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostInsert( event );
 		}
 	}
 
 	private void postCommitInsert() {
 		EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostInsertEvent event = new PostInsertEvent(
 				getInstance(),
 				generatedId,
 				state,
 				getPersister(),
 				eventSource()
 		);
 		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostInsert( event );
 		}
 	}
 
 	private boolean preInsert() {
 		EventListenerGroup<PreInsertEventListener> listenerGroup = listenerGroup( EventType.PRE_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return false; // NO_VETO
 		}
 		boolean veto = false;
 		final PreInsertEvent event = new PreInsertEvent( getInstance(), null, state, getPersister(), eventSource() );
 		for ( PreInsertEventListener listener : listenerGroup.listeners() ) {
 			veto |= listener.onPreInsert( event );
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
index 1d29eb47d6..c9fccab70a 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
@@ -1,208 +1,206 @@
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
-import org.hibernate.event.EventSource;
 import org.hibernate.event.EventType;
 import org.hibernate.event.PostInsertEvent;
 import org.hibernate.event.PostInsertEventListener;
 import org.hibernate.event.PreInsertEvent;
 import org.hibernate.event.PreInsertEventListener;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.service.event.spi.EventListenerGroup;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerGroup;
 
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
 		EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostInsertEvent event = new PostInsertEvent(
 				getInstance(),
 				getId(),
 				state,
 				getPersister(),
 				eventSource()
 		);
 		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostInsert( event );
 		}
 	}
 
 	private void postCommitInsert() {
 		EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostInsertEvent event = new PostInsertEvent(
 				getInstance(),
 				getId(),
 				state,
 				getPersister(),
 				eventSource()
 		);
 		for ( PostInsertEventListener listener : listenerGroup.listeners() ) {
 			listener.onPostInsert( event );
 		}
 	}
 
 	private boolean preInsert() {
 		boolean veto = false;
 
 		EventListenerGroup<PreInsertEventListener> listenerGroup = listenerGroup( EventType.PRE_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return veto;
 		}
 		final PreInsertEvent event = new PreInsertEvent( getInstance(), getId(), state, getPersister(), eventSource() );
 		for ( PreInsertEventListener listener : listenerGroup.listeners() ) {
 			veto |= listener.onPreInsert( event );
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
 		return ! listenerGroup( EventType.POST_COMMIT_INSERT ).isEmpty();
 	}
 	
 	private boolean isCachePutEnabled(EntityPersister persister, SessionImplementor session) {
 		return persister.hasCache()
 				&& !persister.isCacheInvalidationRequired()
 				&& session.getCacheMode().isPutEnabled();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
index 6c2275e39c..3c5036cfa0 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
@@ -1,274 +1,274 @@
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
 import org.hibernate.event.EventType;
 import org.hibernate.event.PostUpdateEvent;
 import org.hibernate.event.PostUpdateEventListener;
 import org.hibernate.event.PreUpdateEvent;
 import org.hibernate.event.PreUpdateEventListener;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.service.event.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerGroup;
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
 
 	private boolean preUpdate() {
 		boolean veto = false;
 		EventListenerGroup<PreUpdateEventListener> listenerGroup = listenerGroup( EventType.PRE_UPDATE );
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
 		EventListenerGroup<PostUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_UPDATE );
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
 
 	private void postCommitUpdate() {
 		EventListenerGroup<PostUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_UPDATE );
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
 
 	@Override
 	protected boolean hasPostCommitEventListeners() {
 		return ! listenerGroup( EventType.POST_COMMIT_UPDATE ).isEmpty();
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
index 51ad7ab088..bb7ec2c92b 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/entry/CacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/entry/CacheEntry.java
@@ -1,171 +1,171 @@
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
 
 import java.io.Serializable;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.EventType;
 import org.hibernate.event.PreLoadEvent;
 import org.hibernate.event.PreLoadEventListener;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.service.event.spi.EventListenerGroup;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.type.TypeHelper;
 
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
 		final PreLoadEvent preLoadEvent = new PreLoadEvent( session )
 				.setEntity( result )
 				.setState( assembledProps )
 				.setId( id )
 				.setPersister( persister );
 
 		final EventListenerGroup<PreLoadEventListener> listenerGroup = session
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( EventType.PRE_LOAD );
 		for ( PreLoadEventListener listener : listenerGroup.listeners() ) {
 			listener.onPreLoad( preLoadEvent );
 		}
 
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
index 31feee59b5..e388e200a8 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,2747 +1,2748 @@
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
 
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
 import java.io.StringReader;
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
 import java.util.Set;
 import java.util.StringTokenizer;
 import java.util.TreeMap;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.DocumentException;
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
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
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.FilterDefinition;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.ResultSetMappingDefinition;
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
 import org.hibernate.service.ServiceRegistry;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Configuration.class.getName());
 
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
             LOG.cachedFileNotFound( cachedFile.getPath(), e );
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
         LOG.debugf( "Mapping Package %s", packageName );
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
 			sb.append( ") on table " ).append( table.getName() ).append( ": database column " );
 			for ( Column column : unbound ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( " not found. Make sure that you use the correct column name which depends on the naming strategy in use (it may not be the same as the property name in the entity, especially for relational types)" );
 			throw new AnnotationException( sb.toString() );
 		}
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
-		final ServiceRegistry serviceRegistry =  new BasicServiceRegistryImpl( properties );
+		final ServiceRegistry serviceRegistry =  new ServiceRegistryBuilder( properties ).buildServiceRegistry();
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationIntegrator.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationIntegrator.java
index aaca83f799..8865fa6bb0 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationIntegrator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/BeanValidationIntegrator.java
@@ -1,296 +1,296 @@
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
 package org.hibernate.cfg.beanvalidation;
 
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Method;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Properties;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
+import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.spi.Integrator;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 /**
  * @author Steve Ebersole
  */
 public class BeanValidationIntegrator implements Integrator {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BeanValidationIntegrator.class.getName());
 
 	public static final String APPLY_CONSTRAINTS = "hibernate.validator.apply_to_ddl";
 
 	public static final String BV_CHECK_CLASS = "javax.validation.Validation";
 
 	public static final String MODE_PROPERTY = "javax.persistence.validation.mode";
 
 	private static final String ACTIVATOR_CLASS = "org.hibernate.cfg.beanvalidation.TypeSafeActivator";
 	private static final String DDL_METHOD = "applyDDL";
 	private static final String ACTIVATE_METHOD = "activateBeanValidation";
 	private static final String VALIDATE_METHOD = "validateFactory";
 
 	public static void validateFactory(Object object) {
 		try {
 			final Class activatorClass = BeanValidationIntegrator.class.getClassLoader().loadClass( ACTIVATOR_CLASS );
 			try {
 				final Method validateMethod = activatorClass.getMethod( VALIDATE_METHOD, Object.class );
 				if ( ! validateMethod.isAccessible() ) {
 					validateMethod.setAccessible( true );
 				}
 				try {
 					validateMethod.invoke( null, object );
 				}
 				catch (InvocationTargetException e) {
 					if ( e.getTargetException() instanceof HibernateException ) {
 						throw (HibernateException) e.getTargetException();
 					}
 					throw new HibernateException( "Unable to check validity of passed ValidatorFactory", e );
 				}
 				catch (IllegalAccessException e) {
 					throw new HibernateException( "Unable to check validity of passed ValidatorFactory", e );
 				}
 			}
 			catch (HibernateException e) {
 				throw e;
 			}
 			catch (Exception e) {
 				throw new HibernateException( "Could not locate method needed for ValidatorFactory validation", e );
 			}
 		}
 		catch (HibernateException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new HibernateException( "Could not locate TypeSafeActivator class", e );
 		}
 	}
 
 	@Override
 	public void integrate(
 			Configuration configuration,
 			SessionFactoryImplementor sessionFactory,
 			SessionFactoryServiceRegistry serviceRegistry) {
 		// determine requested validation modes.
 		final Set<ValidationMode> modes = ValidationMode.getModes( configuration.getProperties().get( MODE_PROPERTY ) );
 
 		final ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 
 		// try to locate a BV class to see if it is available on the classpath
 		boolean isBeanValidationAvailable;
 		try {
 			classLoaderService.classForName( BV_CHECK_CLASS );
 			isBeanValidationAvailable = true;
 		}
 		catch ( Exception e ) {
 			isBeanValidationAvailable = false;
 		}
 
 		// locate the type safe activator class
 		final Class typeSafeActivatorClass = loadTypeSafeActivatorClass( serviceRegistry );
 
 		// todo : if this works out, probably better to simply alter TypeSafeActivator into a single method...
 
 		applyRelationalConstraints(
 				modes,
 				isBeanValidationAvailable,
 				typeSafeActivatorClass,
 				configuration
 		);
 		applyHibernateListeners(
 				modes,
 				isBeanValidationAvailable,
 				typeSafeActivatorClass,
 				configuration,
 				sessionFactory,
 				serviceRegistry
 		);
 	}
 
 	private Class loadTypeSafeActivatorClass(SessionFactoryServiceRegistry serviceRegistry) {
 		try {
 			return serviceRegistry.getService( ClassLoaderService.class ).classForName( ACTIVATOR_CLASS );
 		}
 		catch (Exception e) {
 			return null;
 		}
 	}
 
 	private void applyRelationalConstraints(
 			Set<ValidationMode> modes,
 			boolean beanValidationAvailable,
 			Class typeSafeActivatorClass,
 			Configuration configuration) {
 		if ( ! ConfigurationHelper.getBoolean( APPLY_CONSTRAINTS, configuration.getProperties(), true ) ){
 			LOG.debug( "Skipping application of relational constraints from legacy Hibernate Validator" );
 			return;
 		}
 
 		if ( ! ( modes.contains( ValidationMode.DDL ) || modes.contains( ValidationMode.AUTO ) ) ) {
 			return;
 		}
 
 		if ( ! beanValidationAvailable ) {
 			if ( modes.contains( ValidationMode.DDL ) ) {
 				throw new HibernateException( "Bean Validation not available in the class path but required in " + MODE_PROPERTY );
 			}
 			else if (modes.contains( ValidationMode.AUTO ) ) {
 				//nothing to activate
 				return;
 			}
 		}
 
 		try {
 			Method applyDDLMethod = typeSafeActivatorClass.getMethod( DDL_METHOD, Collection.class, Properties.class );
 			try {
 				applyDDLMethod.invoke(
 						null,
 						configuration.createMappings().getClasses().values(),
 						configuration.getProperties()
 				);
 			}
 			catch (HibernateException e) {
 				throw e;
 			}
 			catch (Exception e) {
 				throw new HibernateException( "Error applying BeanValidation relational constraints", e );
 			}
 		}
 		catch (HibernateException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new HibernateException( "Unable to locate TypeSafeActivator#applyDDL method", e );
 		}
 	}
 
 	private void applyHibernateListeners(
 			Set<ValidationMode> modes,
 			boolean beanValidationAvailable,
 			Class typeSafeActivatorClass,
 			Configuration configuration,
 			SessionFactoryImplementor sessionFactory,
 			SessionFactoryServiceRegistry serviceRegistry) {
 		// de-activate not-null tracking at the core level when Bean Validation is present unless the user explicitly
 		// asks for it
 		if ( configuration.getProperty( Environment.CHECK_NULLABILITY ) == null ) {
 			sessionFactory.getSettings().setCheckNullability( false );
 		}
 
 		if ( ! ( modes.contains( ValidationMode.CALLBACK ) || modes.contains( ValidationMode.AUTO ) ) ) {
 			return;
 		}
 
 		if ( ! beanValidationAvailable ) {
 			if ( modes.contains( ValidationMode.CALLBACK ) ) {
 				throw new HibernateException( "Bean Validation not available in the class path but required in " + MODE_PROPERTY );
 			}
 			else if (modes.contains( ValidationMode.AUTO ) ) {
 				//nothing to activate
 				return;
 			}
 		}
 
 		try {
 			Method activateMethod = typeSafeActivatorClass.getMethod( ACTIVATE_METHOD, EventListenerRegistry.class, Properties.class );
 			try {
 				activateMethod.invoke(
 						null,
 						serviceRegistry.getService( EventListenerRegistry.class ),
 						configuration.getProperties()
 				);
 			}
 			catch (HibernateException e) {
 				throw e;
 			}
 			catch (Exception e) {
 				throw new HibernateException( "Error applying BeanValidation relational constraints", e );
 			}
 		}
 		catch (HibernateException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new HibernateException( "Unable to locate TypeSafeActivator#applyDDL method", e );
 		}
 	}
 
 
 	// Because the javax validation classes might not be on the runtime classpath
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
 					throw new HibernateException( "Unknown validation mode in " + MODE_PROPERTY + ": " + modeProperty );
 				}
 			}
 		}
 	}
 
 	@Override
 	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 		// nothing to do here afaik
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/DuplicationStrategyImpl.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/DuplicationStrategyImpl.java
index 68d747b6a5..c86e79018c 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/DuplicationStrategyImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/DuplicationStrategyImpl.java
@@ -1,44 +1,44 @@
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
 package org.hibernate.cfg.beanvalidation;
 
-import org.hibernate.service.event.spi.DuplicationStrategy;
+import org.hibernate.event.service.spi.DuplicationStrategy;
 
 /**
  * @author Steve Ebersole
  */
 public class DuplicationStrategyImpl implements DuplicationStrategy {
 	public static final DuplicationStrategyImpl INSTANCE = new DuplicationStrategyImpl();
 
 	@Override
 	public boolean areMatch(Object listener, Object original) {
 		return listener.getClass().equals( original.getClass() ) &&
 				BeanValidationEventListener.class.equals( listener.getClass() );
 	}
 
 	@Override
 	public Action getAction() {
 		return Action.KEEP_ORIGINAL;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
index ae49adadde..2dd6ce5c75 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/beanvalidation/TypeSafeActivator.java
@@ -1,377 +1,377 @@
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
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.event.EventType;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SingleTableSubclass;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 
 /**
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 class TypeSafeActivator {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TypeSafeActivator.class.getName());
 
 	private static final String FACTORY_PROPERTY = "javax.persistence.validation.factory";
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public static void validateFactory(Object object) {
 		if ( ! ValidatorFactory.class.isInstance( object ) ) {
 			throw new HibernateException(
 					"Given object was not an instance of " + ValidatorFactory.class.getName()
 							+ "[" + object.getClass().getName() + "]"
 			);
 		}
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public static void activateBeanValidation(EventListenerRegistry listenerRegistry, Properties properties) {
 		ValidatorFactory factory = getValidatorFactory( properties );
 		BeanValidationEventListener listener = new BeanValidationEventListener(
 				factory, properties
 		);
 
 		listenerRegistry.addDuplicationStrategy( DuplicationStrategyImpl.INSTANCE );
 
 		listenerRegistry.appendListeners( EventType.PRE_INSERT, listener );
 		listenerRegistry.appendListeners( EventType.PRE_UPDATE, listener );
 		listenerRegistry.appendListeners( EventType.PRE_DELETE, listener );
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchIntegrator.java b/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchIntegrator.java
index 0116060bad..126f4017d3 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchIntegrator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/search/HibernateSearchIntegrator.java
@@ -1,134 +1,134 @@
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
 package org.hibernate.cfg.search;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.event.EventType;
 import org.hibernate.event.PostCollectionRecreateEventListener;
 import org.hibernate.event.PostCollectionRemoveEventListener;
 import org.hibernate.event.PostCollectionUpdateEventListener;
 import org.hibernate.event.PostDeleteEventListener;
 import org.hibernate.event.PostInsertEventListener;
 import org.hibernate.event.PostUpdateEventListener;
-import org.hibernate.spi.Integrator;
+import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.event.spi.DuplicationStrategy;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.DuplicationStrategy;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 /**
  * Integrates Hibernate Search into Hibernate Core by registering its needed listeners
  * <p/>
  * The note on the original (now removed) org.hibernate.cfg.search.HibernateSearchEventListenerRegister class indicated
  * that Search now uses a new means for this.  However that signature is relying on removed classes...
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  * @author Steve Ebersole
  */
 public class HibernateSearchIntegrator implements Integrator {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, HibernateSearchIntegrator.class.getName() );
 
 	public static final String AUTO_REGISTER = "hibernate.search.autoregister_listeners";
 	public static final String LISTENER_CLASS = "org.hibernate.search.event.FullTextIndexEventListener";
 
 	@Override
 	public void integrate(
 			Configuration configuration,
 			SessionFactoryImplementor sessionFactory,
 			SessionFactoryServiceRegistry serviceRegistry) {
 		final boolean registerListeners = ConfigurationHelper.getBoolean( AUTO_REGISTER, configuration.getProperties(), false );
 		if ( !registerListeners ) {
 			LOG.debug( "Skipping search event listener auto registration" );
 			return;
 		}
 
 		final Class listenerClass = loadSearchEventListener( serviceRegistry );
 		if ( listenerClass == null ) {
 			LOG.debug( "Skipping search event listener auto registration - could not fid listener class" );
 			return;
 		}
 
 		final Object listener = instantiateListener( listenerClass );
 
 		EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 
 		listenerRegistry.addDuplicationStrategy( new DuplicationStrategyImpl( listenerClass ) );
 
 		listenerRegistry.getEventListenerGroup( EventType.POST_INSERT ).appendListener( (PostInsertEventListener) listener );
 		listenerRegistry.getEventListenerGroup( EventType.POST_UPDATE ).appendListener( (PostUpdateEventListener) listener );
 		listenerRegistry.getEventListenerGroup( EventType.POST_DELETE ).appendListener( (PostDeleteEventListener) listener );
 		listenerRegistry.getEventListenerGroup( EventType.POST_COLLECTION_RECREATE ).appendListener( (PostCollectionRecreateEventListener) listener );
 		listenerRegistry.getEventListenerGroup( EventType.POST_COLLECTION_REMOVE ).appendListener( (PostCollectionRemoveEventListener) listener );
 		listenerRegistry.getEventListenerGroup( EventType.POST_COLLECTION_UPDATE ).appendListener( (PostCollectionUpdateEventListener) listener );
 	}
 
 	private Class loadSearchEventListener(SessionFactoryServiceRegistry serviceRegistry) {
 		try {
 			return serviceRegistry.getService( ClassLoaderService.class ).classForName( LISTENER_CLASS );
 		}
 		catch (Exception e) {
 			return null;
 		}
 	}
 
 	private Object instantiateListener(Class listenerClass) {
 		try {
 			return listenerClass.newInstance();
 		}
 		catch (Exception e) {
 			throw new AnnotationException( "Unable to instantiate Search event listener", e );
 		}
 	}
 
 	public static class DuplicationStrategyImpl implements DuplicationStrategy {
 		private final Class checkClass;
 
 		public DuplicationStrategyImpl(Class checkClass) {
 			this.checkClass = checkClass;
 		}
 
 		@Override
 		public boolean areMatch(Object listener, Object original) {
 			// not isAssignableFrom since the user could subclass
 			return checkClass == original.getClass() && checkClass == listener.getClass();
 		}
 
 		@Override
 		public Action getAction() {
 			return Action.KEEP_ORIGINAL;
 		}
 	}
 
 	@Override
 	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 		// nothing to do here afaik
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java b/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
index 89ee2e4a21..eea9204636 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
@@ -1,334 +1,334 @@
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
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockMode;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.entry.CacheEntry;
 import org.hibernate.event.EventType;
 import org.hibernate.event.PostLoadEvent;
 import org.hibernate.event.PostLoadEventListener;
 import org.hibernate.event.PreLoadEvent;
 import org.hibernate.event.PreLoadEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.proxy.HibernateProxy;
-import org.hibernate.service.event.spi.EventListenerGroup;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerRegistry;
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class, TwoPhaseLoad.class.getName()
 	);
 
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
 
         if (LOG.isDebugEnabled()) LOG.debugf(
 				"Resolving associations for %s",
 				MessageHelper.infoString( persister, id, session.getFactory() )
 		);
 
 		Type[] types = persister.getPropertyTypes();
 		for ( int i = 0; i < hydratedState.length; i++ ) {
 			final Object value = hydratedState[i];
 			if ( value!=LazyPropertyInitializer.UNFETCHED_PROPERTY && value!=BackrefPropertyAccessor.UNKNOWN ) {
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
 
 		persister.setPropertyValues( entity, hydratedState, session.getEntityMode() );
 
 		final SessionFactoryImplementor factory = session.getFactory();
 		if ( persister.hasCache() && session.getCacheMode().isPutEnabled() ) {
 
             if (LOG.isDebugEnabled()) LOG.debugf(
 					"Adding entity to second-level cache: %s",
 					MessageHelper.infoString( persister, id, session.getFactory() )
 			);
 
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
 			postLoadEvent.setEntity( entity ).setId( id ).setPersister( persister );
 
 			final EventListenerGroup<PostLoadEventListener> listenerGroup = session
 					.getFactory()
 					.getServiceRegistry()
 					.getService( EventListenerRegistry.class )
 					.getEventListenerGroup( EventType.POST_LOAD );
 			for ( PostLoadEventListener listener : listenerGroup.listeners() ) {
 				listener.onPostLoad( postLoadEvent );
 			}
 		}
 
         if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Done materializing entity %s",
 					MessageHelper.infoString( persister, id, session.getFactory() )
 			);
 		}
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
index 272aba4a38..c0b0c14dd3 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
@@ -1,387 +1,387 @@
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
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
 import org.hibernate.event.EventType;
 import org.hibernate.event.FlushEntityEvent;
 import org.hibernate.event.FlushEntityEventListener;
 import org.hibernate.event.FlushEvent;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.internal.util.collections.LazyIterator;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.Printer;
-import org.hibernate.service.event.spi.EventListenerGroup;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 
 /**
  * A convenience base class for listeners whose functionality results in flushing.
  *
  * @author Steve Eberole
  */
 public abstract class AbstractFlushingEventListener implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			AbstractFlushingEventListener.class.getName()
 	);
 
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
             LOG.debugf(
 					"Flushed: %s insertions, %s updates, %s deletions to %s objects",
 					session.getActionQueue().numberOfInsertions(),
 					session.getActionQueue().numberOfUpdates(),
 					session.getActionQueue().numberOfDeletions(),
 					persistenceContext.getEntityEntries().size()
 			);
             LOG.debugf(
 					"Flushed: %s (re)creations, %s updates, %s removals to %s collections",
 					session.getActionQueue().numberOfCollectionCreations(),
 					session.getActionQueue().numberOfCollectionUpdates(),
 					session.getActionQueue().numberOfCollectionRemovals(),
 					persistenceContext.getCollectionEntries().size()
 			);
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
 
         LOG.debugf( "Processing flush-time cascades" );
 
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
 
         LOG.debugf( "Dirty checking collections" );
 
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
 				final FlushEntityEvent entityEvent = new FlushEntityEvent( source, me.getKey(), entry );
 				final EventListenerGroup<FlushEntityEventListener> listenerGroup = source
 						.getFactory()
 						.getServiceRegistry()
 						.getService( EventListenerRegistry.class )
 						.getEventListenerGroup( EventType.FLUSH_ENTITY );
 				for ( FlushEntityEventListener listener : listenerGroup.listeners() ) {
 					listener.onFlushEntity( entityEvent );
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
index b4e0000801..9ceed6463f 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
@@ -1,668 +1,668 @@
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
 import org.hibernate.internal.CoreMessageLogger;
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
 import org.hibernate.event.EventType;
 import org.hibernate.event.LoadEvent;
 import org.hibernate.event.LoadEventListener;
 import org.hibernate.event.PostLoadEvent;
 import org.hibernate.event.PostLoadEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerRegistry;
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
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
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerGroupImpl.java b/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerGroupImpl.java
similarity index 94%
rename from hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerGroupImpl.java
rename to hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerGroupImpl.java
index 0d49c518d9..c099edcd90 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerGroupImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerGroupImpl.java
@@ -1,179 +1,178 @@
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
-package org.hibernate.service.event.internal;
+package org.hibernate.event.service.internal;
 
-import java.lang.reflect.Array;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Set;
 
 import org.hibernate.event.EventType;
-import org.hibernate.service.event.spi.DuplicationStrategy;
-import org.hibernate.service.event.spi.EventListenerGroup;
-import org.hibernate.service.event.spi.EventListenerRegistrationException;
+import org.hibernate.event.service.spi.DuplicationStrategy;
+import org.hibernate.event.service.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerRegistrationException;
 
 /**
  * @author Steve Ebersole
  */
 public class EventListenerGroupImpl<T> implements EventListenerGroup<T> {
 	private EventType<T> eventType;
 
 	private final Set<DuplicationStrategy> duplicationStrategies = new LinkedHashSet<DuplicationStrategy>();
 	private List<T> listeners;
 
 	public EventListenerGroupImpl(EventType<T> eventType) {
 		this.eventType = eventType;
 		duplicationStrategies.add(
 				// At minimum make sure we do not register the same exact listener class multiple times.
 				new DuplicationStrategy() {
 					@Override
 					public boolean areMatch(Object listener, Object original) {
 						return listener.getClass().equals( original.getClass() );
 					}
 
 					@Override
 					public Action getAction() {
 						return Action.ERROR;
 					}
 				}
 		);
 	}
 
 	@Override
 	public EventType<T> getEventType() {
 		return eventType;
 	}
 
 	@Override
 	public boolean isEmpty() {
 		return count() <= 0;
 	}
 
 	@Override
 	public int count() {
 		return listeners == null ? 0 : listeners.size();
 	}
 
 	@Override
 	public void clear() {
 		if ( duplicationStrategies != null ) {
 			duplicationStrategies.clear();
 		}
 		if ( listeners != null ) {
 			listeners.clear();
 		}
 	}
 
 	@Override
 	public void addDuplicationStrategy(DuplicationStrategy strategy) {
 		duplicationStrategies.add( strategy );
 	}
 
 	public Iterable<T> listeners() {
 		return listeners == null ? Collections.<T>emptyList() : listeners;
 	}
 
 	@Override
 	public void appendListeners(T... listeners) {
 		for ( T listener : listeners ) {
 			appendListener( listener );
 		}
 	}
 
 	@Override
 	public void appendListener(T listener) {
 		if ( listenerShouldGetAdded( listener ) ) {
 			internalAppend( listener );
 		}
 	}
 
 	@Override
 	public void prependListeners(T... listeners) {
 		for ( T listener : listeners ) {
 			prependListener( listener );
 		}
 	}
 
 	@Override
 	public void prependListener(T listener) {
 		if ( listenerShouldGetAdded( listener ) ) {
 			internalPrepend( listener );
 		}
 	}
 
 	private boolean listenerShouldGetAdded(T listener) {
 		if ( listeners == null ) {
 			listeners = new ArrayList<T>();
 			return true;
 			// no need to do de-dup checks
 		}
 
 		boolean doAdd = true;
 		strategy_loop: for ( DuplicationStrategy strategy : duplicationStrategies ) {
 			final ListIterator<T> itr = listeners.listIterator();
 			while ( itr.hasNext() ) {
 				final T existingListener = itr.next();
 				if ( strategy.areMatch( listener,  existingListener ) ) {
 					switch ( strategy.getAction() ) {
 						// todo : add debug logging of what happens here...
 						case ERROR: {
 							throw new EventListenerRegistrationException( "Duplicate event listener found" );
 						}
 						case KEEP_ORIGINAL: {
 							doAdd = false;
 							break strategy_loop;
 						}
 						case REPLACE_ORIGINAL: {
 							itr.set( listener );
 							doAdd = false;
 							break strategy_loop;
 						}
 					}
 				}
 			}
 		}
 		return doAdd;
 	}
 
 	private void internalPrepend(T listener) {
 		checkAgainstBaseInterface( listener );
 		listeners.add( 0, listener );
 	}
 
 	private void checkAgainstBaseInterface(T listener) {
 		if ( !eventType.baseListenerInterface().isInstance( listener ) ) {
 			throw new EventListenerRegistrationException(
 					"Listener did not implement expected interface [" + eventType.baseListenerInterface().getName() + "]"
 			);
 		}
 	}
 
 	private void internalAppend(T listener) {
 		checkAgainstBaseInterface( listener );
 		listeners.add( listener );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerRegistryImpl.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerRegistryImpl.java
rename to hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerRegistryImpl.java
index 0b88b9bfd5..b96bb46b9f 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerRegistryImpl.java
@@ -1,444 +1,423 @@
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
-package org.hibernate.service.event.internal;
+package org.hibernate.event.service.internal;
 
 import java.lang.reflect.Array;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
-import org.hibernate.cfg.Configuration;
-import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.event.EventListenerRegistration;
 import org.hibernate.event.EventType;
 import org.hibernate.event.def.DefaultAutoFlushEventListener;
 import org.hibernate.event.def.DefaultDeleteEventListener;
 import org.hibernate.event.def.DefaultDirtyCheckEventListener;
 import org.hibernate.event.def.DefaultEvictEventListener;
 import org.hibernate.event.def.DefaultFlushEntityEventListener;
 import org.hibernate.event.def.DefaultFlushEventListener;
 import org.hibernate.event.def.DefaultInitializeCollectionEventListener;
 import org.hibernate.event.def.DefaultLoadEventListener;
 import org.hibernate.event.def.DefaultLockEventListener;
 import org.hibernate.event.def.DefaultMergeEventListener;
 import org.hibernate.event.def.DefaultPersistEventListener;
 import org.hibernate.event.def.DefaultPersistOnFlushEventListener;
 import org.hibernate.event.def.DefaultPostLoadEventListener;
 import org.hibernate.event.def.DefaultPreLoadEventListener;
 import org.hibernate.event.def.DefaultRefreshEventListener;
 import org.hibernate.event.def.DefaultReplicateEventListener;
 import org.hibernate.event.def.DefaultSaveEventListener;
 import org.hibernate.event.def.DefaultSaveOrUpdateEventListener;
 import org.hibernate.event.def.DefaultUpdateEventListener;
-import org.hibernate.service.event.spi.DuplicationStrategy;
-import org.hibernate.service.event.spi.EventListenerRegistrationException;
-import org.hibernate.service.event.spi.EventListenerRegistry;
-import org.hibernate.service.spi.ServiceRegistryImplementor;
-import org.hibernate.service.StandardServiceInitiators.EventListenerRegistrationService;
+import org.hibernate.event.service.spi.DuplicationStrategy;
+import org.hibernate.event.service.spi.EventListenerRegistrationException;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 
 import static org.hibernate.event.EventType.AUTO_FLUSH;
 import static org.hibernate.event.EventType.DELETE;
 import static org.hibernate.event.EventType.DIRTY_CHECK;
 import static org.hibernate.event.EventType.EVICT;
 import static org.hibernate.event.EventType.FLUSH;
 import static org.hibernate.event.EventType.FLUSH_ENTITY;
 import static org.hibernate.event.EventType.INIT_COLLECTION;
 import static org.hibernate.event.EventType.LOAD;
 import static org.hibernate.event.EventType.LOCK;
 import static org.hibernate.event.EventType.MERGE;
 import static org.hibernate.event.EventType.PERSIST;
 import static org.hibernate.event.EventType.PERSIST_ONFLUSH;
 import static org.hibernate.event.EventType.POST_COLLECTION_RECREATE;
 import static org.hibernate.event.EventType.POST_COLLECTION_REMOVE;
 import static org.hibernate.event.EventType.POST_COLLECTION_UPDATE;
 import static org.hibernate.event.EventType.POST_COMMIT_DELETE;
 import static org.hibernate.event.EventType.POST_COMMIT_INSERT;
 import static org.hibernate.event.EventType.POST_COMMIT_UPDATE;
 import static org.hibernate.event.EventType.POST_DELETE;
 import static org.hibernate.event.EventType.POST_INSERT;
 import static org.hibernate.event.EventType.POST_LOAD;
 import static org.hibernate.event.EventType.POST_UPDATE;
 import static org.hibernate.event.EventType.PRE_COLLECTION_RECREATE;
 import static org.hibernate.event.EventType.PRE_COLLECTION_REMOVE;
 import static org.hibernate.event.EventType.PRE_COLLECTION_UPDATE;
 import static org.hibernate.event.EventType.PRE_DELETE;
 import static org.hibernate.event.EventType.PRE_INSERT;
 import static org.hibernate.event.EventType.PRE_LOAD;
 import static org.hibernate.event.EventType.PRE_UPDATE;
 import static org.hibernate.event.EventType.REFRESH;
 import static org.hibernate.event.EventType.REPLICATE;
 import static org.hibernate.event.EventType.SAVE;
 import static org.hibernate.event.EventType.SAVE_UPDATE;
 import static org.hibernate.event.EventType.UPDATE;
 
 /**
  * @author Steve Ebersole
  */
 public class EventListenerRegistryImpl implements EventListenerRegistry {
 	private Map<Class,Object> listenerClassToInstanceMap = new HashMap<Class, Object>();
 
 	private Map<EventType,EventListenerGroupImpl> registeredEventListenersMap = prepareListenerMap();
 
 	@SuppressWarnings({ "unchecked" })
 	public <T> EventListenerGroupImpl<T> getEventListenerGroup(EventType<T> eventType) {
 		EventListenerGroupImpl<T> listeners = registeredEventListenersMap.get( eventType );
 		if ( listeners == null ) {
 			throw new HibernateException( "Unable to find listeners for type [" + eventType.eventName() + "]" );
 		}
 		return listeners;
 	}
 
 	@Override
 	public void addDuplicationStrategy(DuplicationStrategy strategy) {
 		for ( EventListenerGroupImpl group : registeredEventListenersMap.values() ) {
 			group.addDuplicationStrategy( strategy );
 		}
 	}
 
 	@Override
 	public <T> void setListeners(EventType<T> type, Class<T>... listenerClasses) {
 		setListeners( type, resolveListenerInstances( type, listenerClasses ) );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	private <T> T[] resolveListenerInstances(EventType<T> type, Class<T>... listenerClasses) {
 		T[] listeners = (T[]) Array.newInstance( type.baseListenerInterface(), listenerClasses.length );
 		for ( int i = 0; i < listenerClasses.length; i++ ) {
 			listeners[i] = resolveListenerInstance( listenerClasses[i] );
 		}
 		return listeners;
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	private <T> T resolveListenerInstance(Class<T> listenerClass) {
 		T listenerInstance = (T) listenerClassToInstanceMap.get( listenerClass );
 		if ( listenerInstance == null ) {
 			listenerInstance = instantiateListener( listenerClass );
 			listenerClassToInstanceMap.put( listenerClass, listenerInstance );
 		}
 		return listenerInstance;
 	}
 
 	private <T> T instantiateListener(Class<T> listenerClass) {
 		try {
 			return listenerClass.newInstance();
 		}
 		catch ( Exception e ) {
 			throw new EventListenerRegistrationException(
 					"Unable to instantiate specified event listener class: " + listenerClass.getName(),
 					e
 			);
 		}
 	}
 
 	@Override
 	public <T> void setListeners(EventType<T> type, T... listeners) {
 		EventListenerGroupImpl<T> registeredListeners = getEventListenerGroup( type );
 		registeredListeners.clear();
 		if ( listeners != null ) {
 			for ( int i = 0, max = listeners.length; i < max; i++ ) {
 				registeredListeners.appendListener( listeners[i] );
 			}
 		}
 	}
 
 	@Override
 	public <T> void appendListeners(EventType<T> type, Class<T>... listenerClasses) {
 		appendListeners( type, resolveListenerInstances( type, listenerClasses ) );
 	}
 
 	@Override
 	public <T> void appendListeners(EventType<T> type, T... listeners) {
 		getEventListenerGroup( type ).appendListeners( listeners );
 	}
 
 	@Override
 	public <T> void prependListeners(EventType<T> type, Class<T>... listenerClasses) {
 		prependListeners( type, resolveListenerInstances( type, listenerClasses ) );
 	}
 
 	@Override
 	public <T> void prependListeners(EventType<T> type, T... listeners) {
 		getEventListenerGroup( type ).prependListeners( listeners );
 	}
 
 	private static Map<EventType,EventListenerGroupImpl> prepareListenerMap() {
 		final Map<EventType,EventListenerGroupImpl> workMap = new HashMap<EventType, EventListenerGroupImpl>();
 
 		// auto-flush listeners
 		prepareListeners(
 				AUTO_FLUSH,
 				new DefaultAutoFlushEventListener(),
 				workMap
 		);
 
 		// create listeners
 		prepareListeners(
 				PERSIST,
 				new DefaultPersistEventListener(),
 				workMap
 		);
 
 		// create-onflush listeners
 		prepareListeners(
 				PERSIST_ONFLUSH,
 				new DefaultPersistOnFlushEventListener(),
 				workMap
 		);
 
 		// delete listeners
 		prepareListeners(
 				DELETE,
 				new DefaultDeleteEventListener(),
 				workMap
 		);
 
 		// dirty-check listeners
 		prepareListeners(
 				DIRTY_CHECK,
 				new DefaultDirtyCheckEventListener(),
 				workMap
 		);
 
 		// evict listeners
 		prepareListeners(
 				EVICT,
 				new DefaultEvictEventListener(),
 				workMap
 		);
 
 		// flush listeners
 		prepareListeners(
 				FLUSH,
 				new DefaultFlushEventListener(),
 				workMap
 		);
 
 		// flush-entity listeners
 		prepareListeners(
 				FLUSH_ENTITY,
 				new DefaultFlushEntityEventListener(),
 				workMap
 		);
 
 		// load listeners
 		prepareListeners(
 				LOAD,
 				new DefaultLoadEventListener(),
 				workMap
 		);
 
 		// load-collection listeners
 		prepareListeners(
 				INIT_COLLECTION,
 				new DefaultInitializeCollectionEventListener(),
 				workMap
 		);
 
 		// lock listeners
 		prepareListeners(
 				LOCK,
 				new DefaultLockEventListener(),
 				workMap
 		);
 
 		// merge listeners
 		prepareListeners(
 				MERGE,
 				new DefaultMergeEventListener(),
 				workMap
 		);
 
 		// pre-collection-recreate listeners
 		prepareListeners(
 				PRE_COLLECTION_RECREATE,
 				workMap
 		);
 
 		// pre-collection-remove listeners
 		prepareListeners(
 				PRE_COLLECTION_REMOVE,
 				workMap
 		);
 
 		// pre-collection-update listeners
 		prepareListeners(
 				PRE_COLLECTION_UPDATE,
 				workMap
 		);
 
 		// pre-delete listeners
 		prepareListeners(
 				PRE_DELETE,
 				workMap
 		);
 
 		// pre-insert listeners
 		prepareListeners(
 				PRE_INSERT,
 				workMap
 		);
 
 		// pre-load listeners
 		prepareListeners(
 				PRE_LOAD,
 				new DefaultPreLoadEventListener(),
 				workMap
 		);
 
 		// pre-update listeners
 		prepareListeners(
 				PRE_UPDATE,
 				workMap
 		);
 
 		// post-collection-recreate listeners
 		prepareListeners(
 				POST_COLLECTION_RECREATE,
 				workMap
 		);
 
 		// post-collection-remove listeners
 		prepareListeners(
 				POST_COLLECTION_REMOVE,
 				workMap
 		);
 
 		// post-collection-update listeners
 		prepareListeners(
 				POST_COLLECTION_UPDATE,
 				workMap
 		);
 
 		// post-commit-delete listeners
 		prepareListeners(
 				POST_COMMIT_DELETE,
 				workMap
 		);
 
 		// post-commit-insert listeners
 		prepareListeners(
 				POST_COMMIT_INSERT,
 				workMap
 		);
 
 		// post-commit-update listeners
 		prepareListeners(
 				POST_COMMIT_UPDATE,
 				workMap
 		);
 
 		// post-delete listeners
 		prepareListeners(
 				POST_DELETE,
 				workMap
 		);
 
 		// post-insert listeners
 		prepareListeners(
 				POST_INSERT,
 				workMap
 		);
 
 		// post-load listeners
 		prepareListeners(
 				POST_LOAD,
 				new DefaultPostLoadEventListener(),
 				workMap
 		);
 
 		// post-update listeners
 		prepareListeners(
 				POST_UPDATE,
 				workMap
 		);
 
 		// update listeners
 		prepareListeners(
 				UPDATE,
 				new DefaultUpdateEventListener(),
 				workMap
 		);
 
 		// refresh listeners
 		prepareListeners(
 				REFRESH,
 				new DefaultRefreshEventListener(),
 				workMap
 		);
 
 		// replicate listeners
 		prepareListeners(
 				REPLICATE,
 				new DefaultReplicateEventListener(),
 				workMap
 		);
 
 		// save listeners
 		prepareListeners(
 				SAVE,
 				new DefaultSaveEventListener(),
 				workMap
 		);
 
 		// save-update listeners
 		prepareListeners(
 				SAVE_UPDATE,
 				new DefaultSaveOrUpdateEventListener(),
 				workMap
 		);
 
 		return Collections.unmodifiableMap( workMap );
 	}
 
 	private static <T> void prepareListeners(EventType<T> type, Map<EventType,EventListenerGroupImpl> map) {
 		prepareListeners( type, null, map );
 	}
 
 	private static <T> void prepareListeners(EventType<T> type, T defaultListener, Map<EventType,EventListenerGroupImpl> map) {
 		final EventListenerGroupImpl<T> listeners = new EventListenerGroupImpl<T>( type );
 		if ( defaultListener != null ) {
 			listeners.appendListener( defaultListener );
 		}
 		map.put( type, listeners  );
 	}
 
-	public static EventListenerRegistryImpl buildEventListenerRegistry(
-			SessionFactoryImplementor sessionFactory,
-			Configuration configuration,
-			ServiceRegistryImplementor serviceRegistry) {
-		final EventListenerRegistryImpl registry = new EventListenerRegistryImpl();
-
-		final EventListenerRegistrationService registrationService =  serviceRegistry.getService(
-				EventListenerRegistrationService.class
-		);
-		for ( EventListenerRegistration registration : registrationService.getEventListenerRegistrations() ) {
-			registration.apply( registry, configuration, configuration.getProperties(), serviceRegistry );
-		}
-
-		return registry;
-	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerServiceInitiator.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerServiceInitiator.java
rename to hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerServiceInitiator.java
index 0479485c34..6bf781a768 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/event/internal/EventListenerServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerServiceInitiator.java
@@ -1,53 +1,53 @@
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
-package org.hibernate.service.event.internal;
+package org.hibernate.event.service.internal;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 
 /**
  * Service initiator for {@link EventListenerRegistry}
  *
  * @author Steve Ebersole
  */
 public class EventListenerServiceInitiator implements SessionFactoryServiceInitiator<EventListenerRegistry> {
 	public static final EventListenerServiceInitiator INSTANCE = new EventListenerServiceInitiator();
 
 	@Override
 	public Class<EventListenerRegistry> getServiceInitiated() {
 		return EventListenerRegistry.class;
 	}
 
 	@Override
 	public EventListenerRegistry initiateService(
 			SessionFactoryImplementor sessionFactory,
 			Configuration configuration,
 			ServiceRegistryImplementor registry) {
-		return EventListenerRegistryImpl.buildEventListenerRegistry( sessionFactory, configuration, registry );
+		return new EventListenerRegistryImpl();
 	}
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/spi/DuplicationStrategy.java b/hibernate-core/src/main/java/org/hibernate/event/service/spi/DuplicationStrategy.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/service/event/spi/DuplicationStrategy.java
rename to hibernate-core/src/main/java/org/hibernate/event/service/spi/DuplicationStrategy.java
index 18da5d3f35..8f8ed93db4 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/event/spi/DuplicationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/service/spi/DuplicationStrategy.java
@@ -1,58 +1,58 @@
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
-package org.hibernate.service.event.spi;
+package org.hibernate.event.service.spi;
 
 /**
  * Defines listener duplication checking strategy, both in terms of when a duplication is detected (see
  * {@link #areMatch}) as well as how to handle a duplication (see {@link #getAction}).
  *
  * @author Steve Ebersole
  */
 public interface DuplicationStrategy {
 	/**
 	 * The enumerated list of actions available on duplication match
 	 */
 	public static enum Action {
 		ERROR,
 		KEEP_ORIGINAL,
 		REPLACE_ORIGINAL
 	}
 
 	/**
 	 * Are the two listener instances considered a duplication?
 	 *
 	 * @param listener The listener we are currently trying to register
 	 * @param original An already registered listener
 	 *
 	 * @return {@literal true} if the two instances are considered a duplication; {@literal false} otherwise
 	 */
 	public boolean areMatch(Object listener, Object original);
 
 	/**
 	 * How should a duplication be handled?
 	 *
 	 * @return The strategy for handling duplication
 	 */
 	public Action getAction();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerGroup.java b/hibernate-core/src/main/java/org/hibernate/event/service/spi/EventListenerGroup.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerGroup.java
rename to hibernate-core/src/main/java/org/hibernate/event/service/spi/EventListenerGroup.java
index f379bc2324..4336ca0cae 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerGroup.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/service/spi/EventListenerGroup.java
@@ -1,74 +1,74 @@
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
-package org.hibernate.service.event.spi;
+package org.hibernate.event.service.spi;
 
 import java.io.Serializable;
 
 import org.hibernate.event.EventType;
 
 /**
  * Contract for a groups of events listeners for a particular event type.
  *
  * @author Steve Ebersole
  */
 public interface EventListenerGroup<T> extends Serializable {
 
 	/**
 	 * Retrieve the event type associated with this groups of listeners.
 	 *
 	 * @return The event type.
 	 */
 	public EventType<T> getEventType();
 
 	/**
 	 * Are there no listeners registered?
 	 *
 	 * @return {@literal true} if no listeners are registered; {@literal false} otherwise.
 	 */
 	public boolean isEmpty();
 
 	public int count();
 
 	public Iterable<T> listeners();
 
 	/**
 	 * Mechanism to more finely control the notion of duplicates.
 	 * <p/>
 	 * For example, say you are registering listeners for an extension library.  This extension library
 	 * could define a "marker interface" which indicates listeners related to it and register a strategy
 	 * that checks against that marker interface.
 	 *
 	 * @param strategy The duplication strategy
 	 */
 	public void addDuplicationStrategy(DuplicationStrategy strategy);
 
 	public void appendListener(T listener);
 	public void appendListeners(T... listeners);
 
 	public void prependListener(T listener);
 	public void prependListeners(T... listeners);
 
 	public void clear();
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerRegistrationException.java b/hibernate-core/src/main/java/org/hibernate/event/service/spi/EventListenerRegistrationException.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerRegistrationException.java
rename to hibernate-core/src/main/java/org/hibernate/event/service/spi/EventListenerRegistrationException.java
index 922dff24d0..9fdbac34c0 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerRegistrationException.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/service/spi/EventListenerRegistrationException.java
@@ -1,41 +1,41 @@
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
-package org.hibernate.service.event.spi;
+package org.hibernate.event.service.spi;
 
 import org.hibernate.HibernateException;
 
 /**
  * Indicates a problem registering an event listener.
  * 
  * @author Steve Ebersole
  */
 public class EventListenerRegistrationException extends HibernateException {
 	public EventListenerRegistrationException(String s) {
 		super( s );
 	}
 
 	public EventListenerRegistrationException(String string, Throwable root) {
 		super( string, root );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerRegistry.java b/hibernate-core/src/main/java/org/hibernate/event/service/spi/EventListenerRegistry.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerRegistry.java
rename to hibernate-core/src/main/java/org/hibernate/event/service/spi/EventListenerRegistry.java
index 6ae929ae09..a80ca5793c 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/event/spi/EventListenerRegistry.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/service/spi/EventListenerRegistry.java
@@ -1,50 +1,50 @@
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
-package org.hibernate.service.event.spi;
+package org.hibernate.event.service.spi;
 
 import java.io.Serializable;
 
 import org.hibernate.event.EventType;
 import org.hibernate.service.Service;
 
 /**
  * Service for accessing each {@link EventListenerGroup} by {@link EventType}, as well as convenience
  * methods for managing the listeners registered in each {@link EventListenerGroup}.
  *
  * @author Steve Ebersole
  */
 public interface EventListenerRegistry extends Service, Serializable {
 	public <T> EventListenerGroup<T> getEventListenerGroup(EventType<T> eventType);
 
 	public void addDuplicationStrategy(DuplicationStrategy strategy);
 
 	public <T> void setListeners(EventType<T> type, Class<T>... listeners);
 	public <T> void setListeners(EventType<T> type, T... listeners);
 
 	public <T> void appendListeners(EventType<T> type, Class<T>... listeners);
 	public <T> void appendListeners(EventType<T> type, T... listeners);
 
 	public <T> void prependListeners(EventType<T> type, Class<T>... listeners);
 	public <T> void prependListeners(EventType<T> type, T... listeners);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
index f3bbbcad7f..bf8698ed19 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
@@ -1,1394 +1,1377 @@
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
-import java.util.List;
 import java.util.Map;
 import java.util.Properties;
-import java.util.ServiceLoader;
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
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
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
-import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
-import org.hibernate.cfg.search.HibernateSearchIntegrator;
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
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
+import org.hibernate.integrator.spi.Integrator;
+import org.hibernate.internal.CoreMessageLogger;
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
+import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
-import org.hibernate.spi.Integrator;
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
  * @see org.hibernate.hql.QueryTranslator
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
 	private final transient Map namedQueries;
 	private final transient Map namedSqlQueries;
 	private final transient Map sqlResultSetMappings;
 	private final transient Map filters;
 	private final transient Map fetchProfiles;
 	private final transient Map imports;
 	private final transient Interceptor interceptor;
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient QueryCache queryCache;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient Map<String,QueryCache> queryCaches;
 	private final transient ConcurrentMap<String,Region> allCacheRegions = new ConcurrentHashMap<String, Region>();
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
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.debug( "Building session factory" );
 
 		this.settings = settings;
 		this.interceptor = cfg.getInterceptor();
 
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
 
 		this.filters = new HashMap();
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
-		for ( Integrator integrator : locateIntegrators( this.serviceRegistry ) ) {
+		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
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
 
-	private Iterable<Integrator> locateIntegrators(ServiceRegistryImplementor serviceRegistry) {
-		List<Integrator> integrators = new ArrayList<Integrator>();
-
-		// todo : Envers needs to be handled by discovery to be because it is in a separate project
-		integrators.add( new BeanValidationIntegrator() );
-		integrators.add( new HibernateSearchIntegrator() );
-
-		for ( Integrator integrator : ServiceLoader.load( Integrator.class ) ) {
-			integrators.add( integrator );
-		}
-
-		return integrators;
-	}
-
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
 
 	public StatelessSession openStatelessSession() {
 		return new StatelessSessionImpl( null, this );
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return new StatelessSessionImpl( connection, this );
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
 
 	static class SessionBuilderImpl implements SessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Interceptor interceptor;
 		private Connection connection;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private EntityMode entityMode;
 		private boolean autoClose;
 		private boolean autoJoinTransactions = true;
 		private boolean flushBeforeCompletion;
 
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
 					connectionReleaseMode
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
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
index 7742d5c3f1..fd2d54282a 100644
--- a/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
@@ -1,1145 +1,1145 @@
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
 import java.sql.NClob;
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
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
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
 import org.hibernate.SharedSessionBuilder;
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
 import org.hibernate.event.EventSource;
 import org.hibernate.event.EventType;
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
-import org.hibernate.service.event.spi.EventListenerGroup;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerRegistry;
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionImpl.class.getName());
 
 	private transient long timestamp;
 
 	private transient ActionQueue actionQueue;
 	private transient StatefulPersistenceContext persistenceContext;
 	private transient TransactionCoordinatorImpl transactionCoordinator;
 	private transient Interceptor interceptor;
 	private transient EntityNameResolver entityNameResolver = new CoordinatingEntityNameResolver();
 
 	private transient ConnectionReleaseMode connectionReleaseMode;
 	private transient FlushMode flushMode = FlushMode.AUTO;
 	private transient CacheMode cacheMode = CacheMode.NORMAL;
 	private transient EntityMode entityMode = EntityMode.POJO;
 
 	private transient boolean autoClear; //for EJB3
 	private transient boolean autoJoinTransactions = true;
 	private transient boolean flushBeforeCompletionEnabled;
 	private transient boolean autoCloseSessionEnabled;
 
 	private transient int dontFlushFromFind = 0;
 
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
 	 * @param transactionCoordinator The transaction coordinator to use, may be null to indicate that a new transaction
 	 * coordinator should get created.
 	 * @param autoJoinTransactions Should the session automatically join JTA transactions?
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
 			final TransactionCoordinatorImpl transactionCoordinator,
 			final boolean autoJoinTransactions,
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
 		this.interceptor = interceptor == null ? EmptyInterceptor.INSTANCE : interceptor;
 		this.actionQueue = new ActionQueue( this );
 		this.persistenceContext = new StatefulPersistenceContext( this );
 		this.flushBeforeCompletionEnabled = flushBeforeCompletionEnabled;
 		this.autoCloseSessionEnabled = autoCloseSessionEnabled;
 		this.connectionReleaseMode = connectionReleaseMode;
 		this.autoJoinTransactions = autoJoinTransactions;
 
 		if ( transactionCoordinator == null ) {
 			this.transactionCoordinator = new TransactionCoordinatorImpl( connection, this );
 			this.transactionCoordinator.getJdbcCoordinator().getLogicalConnection().addObserver(
 					new ConnectionObserverStatsBridge( factory )
 			);
 		}
 		else {
 			if ( connection != null ) {
 				throw new SessionException( "Cannot simultaneously share transaction context and specify connection" );
 			}
 			this.transactionCoordinator = transactionCoordinator;
 		}
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
         if (factory.getStatistics().isStatisticsEnabled()) factory.getStatisticsImplementor().openSession();
 
         LOG.debugf("Opened session at timestamp: %s", timestamp);
 	}
 
 	@Override
 	public SharedSessionBuilder sessionWithOptions() {
 		return new SharedSessionBuilderImpl( this );
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
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE_UPDATE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 	}
 
 	private <T> Iterable<T> listeners(EventType<T> type) {
 		return eventListenerGroup( type ).listeners();
 	}
 
 	private <T> EventListenerGroup<T> eventListenerGroup(EventType<T> type) {
 		return factory.getServiceRegistry().getService( EventListenerRegistry.class ).getEventListenerGroup( type );
 	}
 
 
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
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE ) ) {
 			listener.onSaveOrUpdate( event );
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
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.UPDATE ) ) {
 			listener.onSaveOrUpdate( event );
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
 
 	private void fireLock(LockEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( LockEventListener listener : listeners( EventType.LOCK ) ) {
 			listener.onLock( event );
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
 		for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
 			listener.onPersist( event, copiedAlready );
 		}
 	}
 
 	private void firePersist(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
 			listener.onPersist( event );
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
 		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
 			listener.onPersist( event, copiedAlready );
 		}
 	}
 
 	private void firePersistOnFlush(PersistEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
 			listener.onPersist( event );
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
 		for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
 			listener.onMerge( event );
 		}
 		return event.getResult();
 	}
 
 	private void fireMerge(Map copiedAlready, MergeEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
 			listener.onMerge( event, copiedAlready );
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
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event );
 		}
 	}
 
 	private void fireDelete(DeleteEvent event, Set transientEntities) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event, transientEntities );
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
 		for ( LoadEventListener listener : listeners( EventType.LOAD ) ) {
 			listener.onLoad( event, loadType );
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
 
 	private void fireRefresh(RefreshEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
 			listener.onRefresh( event );
 		}
 	}
 
 	private void fireRefresh(Map refreshedAlready, RefreshEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
 			listener.onRefresh( event, refreshedAlready );
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
 		for ( ReplicateEventListener listener : listeners( EventType.REPLICATE ) ) {
 			listener.onReplicate( event );
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
 
 	private void fireEvict(EvictEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( EvictEventListener listener : listeners( EventType.EVICT ) ) {
 			listener.onEvict( event );
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
 		AutoFlushEvent event = new AutoFlushEvent( querySpaces, this );
 		for ( AutoFlushEventListener listener : listeners( EventType.AUTO_FLUSH ) ) {
 			listener.onAutoFlush( event );
 		}
 		return event.isFlushRequired();
 	}
 
 	public boolean isDirty() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
         LOG.debugf("Checking session dirtiness");
diff --git a/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceImpl.java b/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceImpl.java
new file mode 100644
index 0000000000..871eea8b83
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceImpl.java
@@ -0,0 +1,66 @@
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
+package org.hibernate.integrator.internal;
+
+import java.util.LinkedHashSet;
+import java.util.ServiceLoader;
+
+import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
+import org.hibernate.cfg.search.HibernateSearchIntegrator;
+import org.hibernate.integrator.spi.Integrator;
+import org.hibernate.integrator.spi.IntegratorService;
+import org.hibernate.service.spi.ServiceRegistryImplementor;
+
+/**
+ * @author Steve Ebersole
+ */
+public class IntegratorServiceImpl implements IntegratorService {
+	private final ServiceRegistryImplementor serviceRegistry;
+	private LinkedHashSet<Integrator> integrators = new LinkedHashSet<Integrator>();
+
+	public IntegratorServiceImpl(ServiceRegistryImplementor serviceRegistry) {
+		this.serviceRegistry = serviceRegistry;
+		// Standard integrators nameable from here.  Envers and JPA, for example, need to be handled by discovery
+		// because in separate project/jars
+		integrators.add( new BeanValidationIntegrator() );
+		integrators.add( new HibernateSearchIntegrator() );
+	}
+
+	@Override
+	public void addIntegrator(Integrator integrator) {
+		integrators.add( integrator );
+	}
+
+	@Override
+	public Iterable<Integrator> getIntegrators() {
+		LinkedHashSet<Integrator> integrators = new LinkedHashSet<Integrator>();
+		integrators.addAll( this.integrators );
+
+		for ( Integrator integrator : ServiceLoader.load( Integrator.class ) ) {
+			integrators.add( integrator );
+		}
+
+		return integrators;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/event/EventListenerRegistration.java b/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceInitiator.java
similarity index 65%
rename from hibernate-core/src/main/java/org/hibernate/event/EventListenerRegistration.java
rename to hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceInitiator.java
index 6cc9a875d9..30654b0a9b 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/EventListenerRegistration.java
+++ b/hibernate-core/src/main/java/org/hibernate/integrator/internal/IntegratorServiceInitiator.java
@@ -1,45 +1,47 @@
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
-package org.hibernate.event;
+package org.hibernate.integrator.internal;
 
 import java.util.Map;
 
-import org.hibernate.cfg.Configuration;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.integrator.spi.IntegratorService;
+import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
- * Contract for performing event listener registration.  This is completely a work in progress for now.  The
- * expectation is that this gets tied in with the "service locator" pattern defined by HHH-5562
- *
  * @author Steve Ebersole
  */
-public interface EventListenerRegistration {
-	public void apply(
-			EventListenerRegistry eventListenerRegistry,
-			Configuration configuration,
-			Map<?, ?> configValues,
-			ServiceRegistryImplementor serviceRegistry
-	);
+public class IntegratorServiceInitiator implements BasicServiceInitiator<IntegratorService> {
+	public static final IntegratorServiceInitiator INSTANCE = new IntegratorServiceInitiator();
+
+	@Override
+	public Class<IntegratorService> getServiceInitiated() {
+		return IntegratorService.class;
+	}
+
+	@Override
+	public IntegratorService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
+		return new IntegratorServiceImpl( registry );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/spi/Integrator.java b/hibernate-core/src/main/java/org/hibernate/integrator/spi/Integrator.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/spi/Integrator.java
rename to hibernate-core/src/main/java/org/hibernate/integrator/spi/Integrator.java
index b01d1dc513..c61e69bcbc 100644
--- a/hibernate-core/src/main/java/org/hibernate/spi/Integrator.java
+++ b/hibernate-core/src/main/java/org/hibernate/integrator/spi/Integrator.java
@@ -1,65 +1,65 @@
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
-package org.hibernate.spi;
+package org.hibernate.integrator.spi;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 /**
  * Contract for stuff that integrates with Hibernate.
  * <p/>
  * IMPL NOTE: called during session factory initialization (constructor), so not all parts of the passed session factory
  * will be available.
  *
  * @todo : the signature here *will* change, guaranteed
  *
  * @todo : better name ?
  *
  * @author Steve Ebersole
  * @since 4.0
  * @jira HHH-5562
  * @jira HHH-6081
  */
 public interface Integrator {
 	/**
 	 * Perform integration.
 	 *
 	 * @param configuration The configuration used to create the session factory
 	 * @param sessionFactory The session factory being created
 	 * @param serviceRegistry The session factory's service registry
 	 */
 	public void integrate(
 			Configuration configuration,
 			SessionFactoryImplementor sessionFactory,
 			SessionFactoryServiceRegistry serviceRegistry);
 
 	/**
 	 * Tongue-in-cheek name for a shutdown callback.
 	 *
 	 * @param sessionFactory The session factory being closed.
 	 * @param serviceRegistry That session factory's service registry
 	 */
 	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/integrator/spi/IntegratorService.java b/hibernate-core/src/main/java/org/hibernate/integrator/spi/IntegratorService.java
new file mode 100644
index 0000000000..662d44558e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/integrator/spi/IntegratorService.java
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
+package org.hibernate.integrator.spi;
+
+import org.hibernate.service.Service;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface IntegratorService extends Service {
+	/**
+	 * Manually add an integrator.  Added integrators supplement the set of discovered ones.
+	 * <p/>
+	 * This is mostly an internal contract used between modules.
+	 *
+	 * @param integrator The integrator
+	 */
+	public void addIntegrator(Integrator integrator);
+
+	/**
+	 * Retrieve all integrators.
+	 *
+	 * @return All integrators.
+	 */
+	public Iterable<Integrator> getIntegrators();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
index 9d4a46b4b5..5bc8e0285a 100644
--- a/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
+++ b/hibernate-core/src/main/java/org/hibernate/jmx/HibernateService.java
@@ -1,194 +1,198 @@
 //$Id: HibernateService.java 6100 2005-03-17 10:48:03Z turin42 $
 package org.hibernate.jmx;
 
+import javax.naming.InitialContext;
 import java.util.Map;
 import java.util.Properties;
-import javax.naming.InitialContext;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
-import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.ExternalSessionFactoryConfig;
+import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.jndi.JndiHelper;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
-import org.jboss.logging.Logger;
 
 
 /**
  * Implementation of <tt>HibernateServiceMBean</tt>. Creates a
  * <tt>SessionFactory</tt> and binds it to the specified JNDI name.<br>
  * <br>
  * All mapping documents are loaded as resources by the MBean.
  * @see HibernateServiceMBean
  * @see org.hibernate.SessionFactory
  * @author John Urberg, Gavin King
  */
 public class HibernateService extends ExternalSessionFactoryConfig implements HibernateServiceMBean {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, HibernateService.class.getName());
 
 	private String boundName;
 	private Properties properties = new Properties();
 
 	@Override
 	public void start() throws HibernateException {
 		boundName = getJndiName();
 		try {
 			buildSessionFactory();
 		}
 		catch (HibernateException he) {
             LOG.unableToBuildSessionFactoryUsingMBeanClasspath(he.getMessage());
             LOG.debug("Error was", he);
 			new SessionFactoryStub(this);
 		}
 	}
 
 	@Override
 	public void stop() {
         LOG.stoppingService();
 		try {
 			InitialContext context = JndiHelper.getInitialContext( buildProperties() );
 			( (SessionFactory) context.lookup(boundName) ).close();
 			//context.unbind(boundName);
 		}
 		catch (Exception e) {
             LOG.unableToStopHibernateService(e);
 		}
 	}
 
 	SessionFactory buildSessionFactory() throws HibernateException {
-        LOG.startingServiceAtJndiName(boundName);
-        LOG.serviceProperties(properties);
-        return buildConfiguration().buildSessionFactory(new BasicServiceRegistryImpl(properties));
+        LOG.startingServiceAtJndiName( boundName );
+        LOG.serviceProperties( properties );
+        return buildConfiguration().buildSessionFactory(
+				new ServiceRegistryBuilder( properties ).buildServiceRegistry()
+		);
 	}
 
 	@Override
 	protected Map getExtraProperties() {
 		return properties;
 	}
 
 	@Override
 	public String getTransactionStrategy() {
 		return getProperty(Environment.TRANSACTION_STRATEGY);
 	}
 
 	@Override
 	public void setTransactionStrategy(String txnStrategy) {
 		setProperty(Environment.TRANSACTION_STRATEGY, txnStrategy);
 	}
 
 	@Override
 	public String getUserTransactionName() {
 		return getProperty(Environment.USER_TRANSACTION);
 	}
 
 	@Override
 	public void setUserTransactionName(String utName) {
 		setProperty(Environment.USER_TRANSACTION, utName);
 	}
 
 	@Override
 	public String getJtaPlatformName() {
 		return getProperty( JtaPlatformInitiator.JTA_PLATFORM );
 	}
 
 	@Override
 	public void setJtaPlatformName(String name) {
 		setProperty( JtaPlatformInitiator.JTA_PLATFORM, name );
 	}
 
 	@Override
 	public String getPropertyList() {
 		return buildProperties().toString();
 	}
 
 	@Override
 	public String getProperty(String property) {
 		return properties.getProperty(property);
 	}
 
 	@Override
 	public void setProperty(String property, String value) {
 		properties.setProperty(property, value);
 	}
 
 	@Override
 	public void dropSchema() {
 		new SchemaExport( buildConfiguration() ).drop(false, true);
 	}
 
 	@Override
 	public void createSchema() {
 		new SchemaExport( buildConfiguration() ).create(false, true);
 	}
 
 	public String getName() {
 		return getProperty(Environment.SESSION_FACTORY_NAME);
 	}
 
 	@Override
 	public String getDatasource() {
 		return getProperty(Environment.DATASOURCE);
 	}
 
 	@Override
 	public void setDatasource(String datasource) {
 		setProperty(Environment.DATASOURCE, datasource);
 	}
 
 	@Override
 	public String getJndiName() {
 		return getProperty(Environment.SESSION_FACTORY_NAME);
 	}
 
 	@Override
 	public void setJndiName(String jndiName) {
 		setProperty(Environment.SESSION_FACTORY_NAME, jndiName);
 	}
 
 	@Override
 	public String getUserName() {
 		return getProperty(Environment.USER);
 	}
 
 	@Override
 	public void setUserName(String userName) {
 		setProperty(Environment.USER, userName);
 	}
 
 	@Override
 	public String getPassword() {
 		return getProperty(Environment.PASS);
 	}
 
 	@Override
 	public void setPassword(String password) {
 		setProperty(Environment.PASS, password);
 	}
 
 	@Override
 	public void setFlushBeforeCompletionEnabled(String enabled) {
 		setProperty(Environment.FLUSH_BEFORE_COMPLETION, enabled);
 	}
 
 	@Override
 	public String getFlushBeforeCompletionEnabled() {
 		return getProperty(Environment.FLUSH_BEFORE_COMPLETION);
 	}
 
 	@Override
 	public void setAutoCloseSessionEnabled(String enabled) {
 		setProperty(Environment.AUTO_CLOSE_SESSION, enabled);
 	}
 
 	@Override
 	public String getAutoCloseSessionEnabled() {
 		return getProperty(Environment.AUTO_CLOSE_SESSION);
 	}
 
 	public Properties getProperties() {
 		return buildProperties();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
new file mode 100644
index 0000000000..e18028371a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/service/ServiceRegistryBuilder.java
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
+package org.hibernate.service;
+
+import java.util.ArrayList;
+import java.util.HashMap;
+import java.util.List;
+import java.util.Map;
+
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.internal.ProvidedService;
+import org.hibernate.service.spi.BasicServiceInitiator;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ServiceRegistryBuilder {
+	private final Map configurationValues;
+	private final List<BasicServiceInitiator> initiators = standardInitiatorList();
+	private final List<ProvidedService> services = new ArrayList<ProvidedService>();
+
+	public ServiceRegistryBuilder() {
+		this( new HashMap() );
+	}
+
+	public ServiceRegistryBuilder(Map configurationValues) {
+		this.configurationValues = configurationValues;
+	}
+
+	private static List<BasicServiceInitiator> standardInitiatorList() {
+		final List<BasicServiceInitiator> initiators = new ArrayList<BasicServiceInitiator>();
+		initiators.addAll( StandardServiceInitiators.LIST );
+		return initiators;
+	}
+
+	public ServiceRegistryBuilder addInitiator(BasicServiceInitiator initiator) {
+		initiators.add( initiator );
+		return this;
+	}
+
+	@SuppressWarnings( {"unchecked"})
+	public ServiceRegistryBuilder addService(final Class serviceRole, final Service service) {
+		services.add( new ProvidedService( serviceRole, service ) );
+		return this;
+	}
+
+	public BasicServiceRegistry buildServiceRegistry() {
+		return new BasicServiceRegistryImpl( initiators, services, configurationValues );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
index 5bc7f1c9c8..24b3c21297 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/StandardServiceInitiators.java
@@ -1,122 +1,78 @@
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
-import org.hibernate.event.EventListenerRegistration;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.internal.PersisterFactoryInitiator;
 import org.hibernate.service.classloading.internal.ClassLoaderServiceInitiator;
+import org.hibernate.integrator.internal.IntegratorServiceInitiator;
 import org.hibernate.service.internal.SessionFactoryServiceRegistryFactoryInitiator;
-import org.hibernate.service.event.internal.EventListenerServiceInitiator;
 import org.hibernate.service.jdbc.connections.internal.ConnectionProviderInitiator;
 import org.hibernate.service.jdbc.connections.internal.MultiTenantConnectionProviderInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectFactoryInitiator;
 import org.hibernate.service.jdbc.dialect.internal.DialectResolverInitiator;
 import org.hibernate.service.jmx.internal.JmxServiceInitiator;
 import org.hibernate.service.jndi.internal.JndiServiceInitiator;
 import org.hibernate.service.jta.platform.internal.JtaPlatformInitiator;
 import org.hibernate.service.spi.BasicServiceInitiator;
-import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 import java.util.ArrayList;
 import java.util.List;
-import java.util.Map;
 
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
-
-		serviceInitiators.add( EventListenerRegistrationServiceInitiator.INSTANCE );
+		serviceInitiators.add( IntegratorServiceInitiator.INSTANCE );
 
 		return serviceInitiators;
 	}
 
-
-	// todo : completely temporary.  See HHH-5562 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-
-	/**
-	 * Acts as a service in the basic registry to which users/integrators can attach things that perform event listener
-	 * registration.  The event listeners live in the SessionFactory registry, but it has access to the basic registry.
-	 * So when it starts up, it looks in the basic registry for this service and does the requested  registrations.
-	 */
-	public static interface EventListenerRegistrationService extends Service {
-		public void attachEventListenerRegistration(EventListenerRegistration registration);
-		public Iterable<EventListenerRegistration> getEventListenerRegistrations();
-	}
-
-	public static class EventListenerRegistrationServiceImpl implements EventListenerRegistrationService {
-		private List<EventListenerRegistration> registrations = new ArrayList<EventListenerRegistration>();
-
-		@Override
-		public void attachEventListenerRegistration(EventListenerRegistration registration) {
-			registrations.add( registration );
-		}
-
-		@Override
-		public Iterable<EventListenerRegistration> getEventListenerRegistrations() {
-			return registrations;
-		}
-	}
-
-	public static class EventListenerRegistrationServiceInitiator implements BasicServiceInitiator<EventListenerRegistrationService> {
-		public static final EventListenerRegistrationServiceInitiator INSTANCE = new EventListenerRegistrationServiceInitiator();
-
-		@Override
-		public Class<EventListenerRegistrationService> getServiceInitiated() {
-			return EventListenerRegistrationService.class;
-		}
-
-		@Override
-		public EventListenerRegistrationService initiateService(Map configurationValues, ServiceRegistryImplementor registry) {
-			return new EventListenerRegistrationServiceImpl();
-		}
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/StandardSessionFactoryServiceInitiators.java b/hibernate-core/src/main/java/org/hibernate/service/StandardSessionFactoryServiceInitiators.java
index 6a10d48b12..00b06c2108 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/StandardSessionFactoryServiceInitiators.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/StandardSessionFactoryServiceInitiators.java
@@ -1,47 +1,47 @@
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
 
 import java.util.ArrayList;
 import java.util.List;
 
-import org.hibernate.service.event.internal.EventListenerServiceInitiator;
+import org.hibernate.event.service.internal.EventListenerServiceInitiator;
 import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 import org.hibernate.stat.internal.StatisticsInitiator;
 
 /**
  * @author Steve Ebersole
  */
 public class StandardSessionFactoryServiceInitiators {
 	public static List<SessionFactoryServiceInitiator> LIST = buildStandardServiceInitiatorList();
 
 	private static List<SessionFactoryServiceInitiator> buildStandardServiceInitiatorList() {
 		final List<SessionFactoryServiceInitiator> serviceInitiators = new ArrayList<SessionFactoryServiceInitiator>();
 
 		serviceInitiators.add( EventListenerServiceInitiator.INSTANCE );
 		serviceInitiators.add( StatisticsInitiator.INSTANCE );
 
 		return serviceInitiators;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
index 3ae90cf755..58c22ba0da 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/BasicServiceRegistryImpl.java
@@ -1,136 +1,142 @@
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
 package org.hibernate.service.internal;
 
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.Service;
-import org.hibernate.service.StandardServiceInitiators;
 import org.hibernate.service.UnknownServiceException;
 import org.hibernate.service.spi.BasicServiceInitiator;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.ServiceException;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 
 /**
  * Standard Hibernate implementation of the service registry.
  *
  * @author Steve Ebersole
  */
 public class BasicServiceRegistryImpl extends AbstractServiceRegistryImpl implements BasicServiceRegistry {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BasicServiceRegistryImpl.class.getName());
 
 	private final Map<Class,BasicServiceInitiator> serviceInitiatorMap;
 	private final Map configurationValues;
 
-	public BasicServiceRegistryImpl(Map configurationValues) {
-		this( StandardServiceInitiators.LIST, configurationValues );
-	}
-
 	@SuppressWarnings( {"unchecked"})
-	public BasicServiceRegistryImpl(List<BasicServiceInitiator> serviceInitiators, Map configurationValues) {
+	public BasicServiceRegistryImpl(
+			List<BasicServiceInitiator> serviceInitiators,
+			List<ProvidedService> providedServices,
+			Map configurationValues) {
 		super();
-		this.serviceInitiatorMap = toMap( serviceInitiators );
+
 		this.configurationValues = configurationValues;
+
+		this.serviceInitiatorMap = toMap( serviceInitiators );
 		for ( BasicServiceInitiator initiator : serviceInitiatorMap.values() ) {
 			// create the bindings up front to help identify to which registry services belong
 			createServiceBinding( initiator.getServiceInitiated() );
 		}
+
+		for ( ProvidedService providedService : providedServices ) {
+			ServiceBinding binding = locateOrCreateServiceBinding( providedService.getServiceRole(), false );
+			binding.setTarget( providedService.getService() );
+		}
 	}
 
 	/**
 	 * We convert the incoming list of initiators to a map for 2 reasons:<ul>
 	 * <li>to make it easier to look up the initiator we need for a given service role</li>
 	 * <li>to make sure there is only one initiator for a given service role (last wins)</li>
 	 * </ul>
 	 *
 	 * @param serviceInitiators The list of individual initiators
 	 *
 	 * @return The map of initiators keyed by the service rle they initiate.
 	 */
 	private static Map<Class, BasicServiceInitiator> toMap(List<BasicServiceInitiator> serviceInitiators) {
 		final Map<Class, BasicServiceInitiator> result = new HashMap<Class, BasicServiceInitiator>();
 		for ( BasicServiceInitiator initiator : serviceInitiators ) {
 			result.put( initiator.getServiceInitiated(), initiator );
 		}
 		return result;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public void registerServiceInitiator(BasicServiceInitiator initiator) {
 		ServiceBinding serviceBinding = locateServiceBinding( initiator.getServiceInitiated(), false );
 		if ( serviceBinding != null ) {
 			serviceBinding.setTarget( null );
 		}
 		else {
 			createServiceBinding( initiator.getServiceInitiated() );
 		}
 		final Object previous = serviceInitiatorMap.put( initiator.getServiceInitiated(), initiator );
 		if ( previous != null ) {
 			LOG.debugf( "Over-wrote existing service initiator [role=%s]", initiator.getServiceInitiated().getName() );
 		}
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	protected <T extends Service> T createService(Class<T> serviceRole) {
 		BasicServiceInitiator<T> initiator = serviceInitiatorMap.get( serviceRole );
 		if ( initiator == null ) {
 			throw new UnknownServiceException( serviceRole );
 		}
 		try {
 			T service = initiator.initiateService( configurationValues, this );
 			// IMPL NOTE : the register call here is important to avoid potential stack overflow issues
 			//		from recursive calls through #configureService
 			registerService( serviceRole, service );
 			return service;
 		}
 		catch ( ServiceException e ) {
 			throw e;
 		}
 		catch ( Exception e ) {
 			throw new ServiceException( "Unable to create requested service [" + serviceRole.getName() + "]", e );
 		}
 	}
 
 	@Override
 	protected <T extends Service> void configureService(T service) {
 		applyInjections( service );
 
 		if ( ServiceRegistryAwareService.class.isInstance( service ) ) {
 			( (ServiceRegistryAwareService) service ).injectServices( this );
 		}
 
 		if ( Configurable.class.isInstance( service ) ) {
 			( (Configurable) service ).configure( configurationValues );
 		}
 	}
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/event/internal/TemporaryListenerHelper.java b/hibernate-core/src/main/java/org/hibernate/service/internal/ProvidedService.java
similarity index 59%
rename from hibernate-core/src/main/java/org/hibernate/service/event/internal/TemporaryListenerHelper.java
rename to hibernate-core/src/main/java/org/hibernate/service/internal/ProvidedService.java
index bed2e92d84..74a2fd92dc 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/event/internal/TemporaryListenerHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/ProvidedService.java
@@ -1,47 +1,47 @@
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
-package org.hibernate.service.event.internal;
-
-import org.hibernate.event.EventType;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+package org.hibernate.service.internal;
 
 /**
- * Helper for handling various aspects of event listeners.  Temporary because generally speaking this is legacy stuff
- * that should change as we move forward
+ * A service provided as-is.
  *
  * @author Steve Ebersole
  */
-public class TemporaryListenerHelper {
-	public static interface ListenerProcessor {
-		public void processListener(Object listener);
+public class ProvidedService<T> {
+	private final Class<T> serviceRole;
+	private final T service;
+
+	public ProvidedService(Class<T> serviceRole, T service) {
+		this.serviceRole = serviceRole;
+		this.service = service;
+	}
+
+	public Class<T> getServiceRole() {
+		return serviceRole;
 	}
 
-	public static void processListeners(EventListenerRegistry registryRegistry, ListenerProcessor processer) {
-		for ( EventType eventType : EventType.values() ) {
-			for ( Object listener : registryRegistry.getEventListenerGroup( eventType ).listeners() ) {
-				processer.processListener( listener );
-			}
-		}
+	public T getService() {
+		return service;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
index b3aec976e4..446a2a71ae 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
@@ -1,54 +1,51 @@
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
 package org.hibernate.service.internal;
 
-import java.util.List;
-
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.service.Service;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
-import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 
 /**
  * Acts as a {@link Service} in the {@link BasicServiceRegistryImpl} whose function is as a factory for
  * {@link SessionFactoryServiceRegistryImpl} implementations.
  *
  * @author Steve Ebersole
  */
 public class SessionFactoryServiceRegistryFactoryImpl implements SessionFactoryServiceRegistryFactory {
 	private final ServiceRegistryImplementor theBasicServiceRegistry;
 
 	public SessionFactoryServiceRegistryFactoryImpl(ServiceRegistryImplementor theBasicServiceRegistry) {
 		this.theBasicServiceRegistry = theBasicServiceRegistry;
 	}
 
 	@Override
 	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
 			SessionFactoryImplementor sessionFactory,
 			Configuration configuration) {
 		return new SessionFactoryServiceRegistryImpl( theBasicServiceRegistry, sessionFactory, configuration );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
index 121bd487cd..8d5106ee5d 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
@@ -1,55 +1,53 @@
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
 package org.hibernate.service.spi;
 
-import java.util.List;
-
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.service.Service;
 import org.hibernate.service.internal.SessionFactoryServiceRegistryImpl;
 
 /**
  * Contract for builder of {@link SessionFactoryServiceRegistry} instances.  Defined as a service to
  * "sit inside" the {@link org.hibernate.service.BasicServiceRegistry}.
  *
  * @author Steve Ebersole
  */
 public interface SessionFactoryServiceRegistryFactory extends Service {
 	/**
 	 * Create the registry.
 	 *
 	 * @todo : fully expect this signature to change!
 	 *
 	 * @param sessionFactory The (in flux) session factory.  Generally this is useful for grabbing a reference for later
 	 * 		use.  However, care should be taken when invoking on the session factory until after it has been fully
 	 * 		initialized.
 	 * @param configuration The configuration object.
 	 *
 	 * @return The registry
 	 */
 	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
 			SessionFactoryImplementor sessionFactory,
 			Configuration configuration);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
index 20ac7dbac3..51f93a824f 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaExport.java
@@ -1,576 +1,577 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.BufferedReader;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.Reader;
 import java.io.Writer;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.internal.Formatter;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 /**
  * Commandline tool to export table schema to the database. This class may also be called from inside an application.
  *
  * @author Daniel Bradby
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class SchemaExport {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaExport.class.getName());
 	private static final String DEFAULT_IMPORT_FILE = "/import.sql";
 
 	public static enum Type {
 		CREATE,
 		DROP,
 		NONE,
 		BOTH;
 
 		public boolean doCreate() {
 			return this == BOTH || this == CREATE;
 		}
 
 		public boolean doDrop() {
 			return this == BOTH || this == DROP;
 		}
 	}
 
 	private final ConnectionHelper connectionHelper;
 	private final SqlStatementLogger sqlStatementLogger;
 	private final SqlExceptionHelper sqlExceptionHelper;
 	private final String[] dropSQL;
 	private final String[] createSQL;
 	private final String importFiles;
 
 	private final List<Exception> exceptions = new ArrayList<Exception>();
 
 	private Formatter formatter;
 
 	private String outputFile = null;
 	private String delimiter;
 	private boolean haltOnError = false;
 
 	public SchemaExport(ServiceRegistry serviceRegistry, Configuration configuration) {
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper(
 				serviceRegistry.getService( ConnectionProvider.class )
 		);
 		this.sqlStatementLogger = serviceRegistry.getService( JdbcServices.class ).getSqlStatementLogger();
 		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		this.sqlExceptionHelper = serviceRegistry.getService( JdbcServices.class ).getSqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				Environment.HBM2DDL_IMPORT_FILES,
 				configuration.getProperties(),
 				DEFAULT_IMPORT_FILE
 		);
 
 		final Dialect dialect = serviceRegistry.getService( JdbcServices.class ).getDialect();
 		this.dropSQL = configuration.generateDropSchemaScript( dialect );
 		this.createSQL = configuration.generateSchemaCreationScript( dialect );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration
 	 *
 	 * @param configuration The configuration from which to build a schema export.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(Configuration configuration) {
 		this( configuration, configuration.getProperties() );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration, with the given
 	 * database connection properties.
 	 *
 	 * @param configuration The configuration from which to build a schema export.
 	 * @param properties The properties from which to configure connectivity etc.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 *
 	 * @deprecated properties may be specified via the Configuration object
 	 */
 	@Deprecated
     public SchemaExport(Configuration configuration, Properties properties) throws HibernateException {
 		final Dialect dialect = Dialect.getDialect( properties );
 
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( properties );
 		this.connectionHelper = new ManagedProviderConnectionHelper( props );
 
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.formatter = FormatStyle.DDL.getFormatter();
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				Environment.HBM2DDL_IMPORT_FILES,
 				properties,
 				DEFAULT_IMPORT_FILE
 		);
 
 		this.dropSQL = configuration.generateDropSchemaScript( dialect );
 		this.createSQL = configuration.generateSchemaCreationScript( dialect );
 	}
 
 	/**
 	 * Create a schema exporter for the given Configuration, using the supplied connection for connectivity.
 	 *
 	 * @param configuration The configuration to use.
 	 * @param connection The JDBC connection to use.
 	 * @throws HibernateException Indicates problem preparing for schema export.
 	 */
 	public SchemaExport(Configuration configuration, Connection connection) throws HibernateException {
 		this.connectionHelper = new SuppliedConnectionHelper( connection );
 
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.formatter = FormatStyle.DDL.getFormatter();
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 
 		this.importFiles = ConfigurationHelper.getString(
 				Environment.HBM2DDL_IMPORT_FILES,
 				configuration.getProperties(),
 				DEFAULT_IMPORT_FILE
 		);
 
 		final Dialect dialect = Dialect.getDialect( configuration.getProperties() );
 		this.dropSQL = configuration.generateDropSchemaScript( dialect );
 		this.createSQL = configuration.generateSchemaCreationScript( dialect );
 	}
 
 	public SchemaExport(
 			ConnectionHelper connectionHelper,
 			String[] dropSql,
 			String[] createSql) {
 		this.connectionHelper = connectionHelper;
 		this.dropSQL = dropSql;
 		this.createSQL = createSql;
 		this.importFiles = "";
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 		this.formatter = FormatStyle.DDL.getFormatter();
 	}
 
 	/**
 	 * For generating a export script file, this is the file which will be written.
 	 *
 	 * @param filename The name of the file to which to write the export script.
 	 * @return this
 	 */
 	public SchemaExport setOutputFile(String filename) {
 		outputFile = filename;
 		return this;
 	}
 
 	/**
 	 * Set the end of statement delimiter
 	 *
 	 * @param delimiter The delimiter
 	 * @return this
 	 */
 	public SchemaExport setDelimiter(String delimiter) {
 		this.delimiter = delimiter;
 		return this;
 	}
 
 	/**
 	 * Should we format the sql strings?
 	 *
 	 * @param format Should we format SQL strings
 	 * @return this
 	 */
 	public SchemaExport setFormat(boolean format) {
 		this.formatter = ( format ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 		return this;
 	}
 
 	/**
 	 * Should we stop once an error occurs?
 	 *
 	 * @param haltOnError True if export should stop after error.
 	 * @return this
 	 */
 	public SchemaExport setHaltOnError(boolean haltOnError) {
 		this.haltOnError = haltOnError;
 		return this;
 	}
 
 	/**
 	 * Run the schema creation script.
 	 *
 	 * @param script print the DDL to the console
 	 * @param export export the script to the database
 	 */
 	public void create(boolean script, boolean export) {
 		create( Target.interpret( script, export ) );
 	}
 
 	public void create(Target output) {
 		execute( output, Type.CREATE );
 	}
 
 	/**
 	 * Run the drop schema script.
 	 *
 	 * @param script print the DDL to the console
 	 * @param export export the script to the database
 	 */
 	public void drop(boolean script, boolean export) {
 		drop( Target.interpret( script, export ) );
 	}
 
 	public void drop(Target output) {
 		execute( output, Type.DROP );
 	}
 
 	public void execute(boolean script, boolean export, boolean justDrop, boolean justCreate) {
 		execute( Target.interpret( script, export ), interpretType( justDrop, justCreate ) );
 	}
 
 	private Type interpretType(boolean justDrop, boolean justCreate) {
 		if ( justDrop ) {
 			return Type.DROP;
 		}
 		else if ( justCreate ) {
 			return Type.CREATE;
 		}
 		else {
 			return Type.BOTH;
 		}
 	}
 
 	public void execute(Target output, Type type) {
 		if ( output == Target.NONE || type == SchemaExport.Type.NONE ) {
 			return;
 		}
 		exceptions.clear();
 
 		LOG.runningHbm2ddlSchemaExport();
 
 		final List<NamedReader> importFileReaders = new ArrayList<NamedReader>();
 		for ( String currentFile : importFiles.split(",") ) {
 			try {
 				final String resourceName = currentFile.trim();
 				InputStream stream = ConfigHelper.getResourceAsStream( resourceName );
 				importFileReaders.add( new NamedReader( resourceName, stream ) );
 			}
 			catch ( HibernateException e ) {
 				LOG.debugf("Import file not found: %s", currentFile);
 			}
 		}
 
 		final List<Exporter> exporters = new ArrayList<Exporter>();
 		try {
 			// prepare exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			if ( output.doScript() ) {
 				exporters.add( new ScriptExporter() );
 			}
 			if ( outputFile != null ) {
 				exporters.add( new FileExporter( outputFile ) );
 			}
 			if ( output.doExport() ) {
 				exporters.add( new DatabaseExporter( connectionHelper, sqlExceptionHelper ) );
 			}
 
 			// perform exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			if ( type.doDrop() ) {
 				perform( dropSQL, exporters );
 			}
 			if ( type.doCreate() ) {
 				perform( createSQL, exporters );
 				if ( ! importFileReaders.isEmpty() ) {
 					for ( NamedReader namedReader : importFileReaders ) {
 						importScript( namedReader, exporters );
 					}
 				}
 			}
 		}
 		catch (Exception e) {
 			exceptions.add( e );
 			LOG.schemaExportUnsuccessful( e );
 		}
 		finally {
 			// release exporters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			for ( Exporter exporter : exporters ) {
 				try {
 					exporter.release();
 				}
 				catch (Exception ignore) {
 				}
 			}
 
 			// release the named readers from import scripts
 			for ( NamedReader namedReader : importFileReaders ) {
 				try {
 					namedReader.getReader().close();
 				}
 				catch (Exception ignore) {
 				}
 			}
             LOG.schemaExportComplete();
 		}
 	}
 
 	private void perform(String[] sqlCommands, List<Exporter> exporters) {
 		for ( String sqlCommand : sqlCommands ) {
 			String formatted = formatter.format( sqlCommand );
 	        if ( delimiter != null ) {
 				formatted += delimiter;
 			}
 			sqlStatementLogger.logStatement( formatted );
 			for ( Exporter exporter : exporters ) {
 				try {
 					exporter.export( formatted );
 				}
 				catch (Exception e) {
 					if ( haltOnError ) {
 						throw new HibernateException( "Error during DDL export", e );
 					}
 					exceptions.add( e );
 					LOG.unsuccessfulCreate( sqlCommand );
 					LOG.error( e.getMessage() );
 				}
 			}
 		}
 	}
 
 	private void importScript(NamedReader namedReader, List<Exporter> exporters) throws Exception {
 		BufferedReader reader = new BufferedReader( namedReader.getReader() );
 		long lineNo = 0;
 		for ( String sql = reader.readLine(); sql != null; sql = reader.readLine() ) {
 			try {
 				lineNo++;
 				String trimmedSql = sql.trim();
 				if ( trimmedSql.length() == 0 ||
 						trimmedSql.startsWith( "--" ) ||
 						trimmedSql.startsWith( "//" ) ||
 						trimmedSql.startsWith( "/*" ) ) {
 					continue;
 				}
                 if ( trimmedSql.endsWith(";") ) {
 					trimmedSql = trimmedSql.substring(0, trimmedSql.length() - 1);
 				}
                 LOG.debugf( trimmedSql );
 				for ( Exporter exporter: exporters ) {
 					if ( exporter.acceptsImportScripts() ) {
 						exporter.export( trimmedSql );
 					}
 				}
 			}
 			catch ( Exception e ) {
 				throw new ImportScriptException( "Error during import script execution at line " + lineNo, e );
 			}
 		}
 	}
 
 	private static class NamedReader {
 		private final Reader reader;
 		private final String name;
 
 		public NamedReader(String name, InputStream stream) {
 			this.name = name;
 			this.reader = new InputStreamReader( stream );
 		}
 
 		public Reader getReader() {
 			return reader;
 		}
 
 		public String getName() {
 			return name;
 		}
 	}
 
 	private void execute(boolean script, boolean export, Writer fileOutput, Statement statement, final String sql)
 			throws IOException, SQLException {
 		final SqlExceptionHelper sqlExceptionHelper = new SqlExceptionHelper();
 
 		String formatted = formatter.format( sql );
         if (delimiter != null) formatted += delimiter;
         if (script) System.out.println(formatted);
         LOG.debugf(formatted);
 		if ( outputFile != null ) {
 			fileOutput.write( formatted + "\n" );
 		}
 		if ( export ) {
 
 			statement.executeUpdate( sql );
 			try {
 				SQLWarning warnings = statement.getWarnings();
 				if ( warnings != null) {
 					sqlExceptionHelper.logAndClearWarnings( connectionHelper.getConnection() );
 				}
 			}
 			catch( SQLException sqle ) {
                 LOG.unableToLogSqlWarnings(sqle);
 			}
 		}
 
 	}
 
 	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return new BasicServiceRegistryImpl( properties );
+		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder( properties ).buildServiceRegistry();
 	}
 
 	public static void main(String[] args) {
 		try {
 			Configuration cfg = new Configuration();
 
 			boolean script = true;
 			boolean drop = false;
 			boolean create = false;
 			boolean halt = false;
 			boolean export = true;
 			String outFile = null;
 			String importFile = DEFAULT_IMPORT_FILE;
 			String propFile = null;
 			boolean format = false;
 			String delim = null;
 
 			for ( int i = 0; i < args.length; i++ ) {
 				if ( args[i].startsWith( "--" ) ) {
 					if ( args[i].equals( "--quiet" ) ) {
 						script = false;
 					}
 					else if ( args[i].equals( "--drop" ) ) {
 						drop = true;
 					}
 					else if ( args[i].equals( "--create" ) ) {
 						create = true;
 					}
 					else if ( args[i].equals( "--haltonerror" ) ) {
 						halt = true;
 					}
 					else if ( args[i].equals( "--text" ) ) {
 						export = false;
 					}
 					else if ( args[i].startsWith( "--output=" ) ) {
 						outFile = args[i].substring( 9 );
 					}
 					else if ( args[i].startsWith( "--import=" ) ) {
 						importFile = args[i].substring( 9 );
 					}
 					else if ( args[i].startsWith( "--properties=" ) ) {
 						propFile = args[i].substring( 13 );
 					}
 					else if ( args[i].equals( "--format" ) ) {
 						format = true;
 					}
 					else if ( args[i].startsWith( "--delimiter=" ) ) {
 						delim = args[i].substring( 12 );
 					}
 					else if ( args[i].startsWith( "--config=" ) ) {
 						cfg.configure( args[i].substring( 9 ) );
 					}
 					else if ( args[i].startsWith( "--naming=" ) ) {
 						cfg.setNamingStrategy(
 								( NamingStrategy ) ReflectHelper.classForName( args[i].substring( 9 ) )
 										.newInstance()
 						);
 					}
 				}
 				else {
 					String filename = args[i];
 					if ( filename.endsWith( ".jar" ) ) {
 						cfg.addJar( new File( filename ) );
 					}
 					else {
 						cfg.addFile( filename );
 					}
 				}
 
 			}
 
 			if ( propFile != null ) {
 				Properties props = new Properties();
 				props.putAll( cfg.getProperties() );
 				props.load( new FileInputStream( propFile ) );
 				cfg.setProperties( props );
 			}
 
 			if (importFile != null) {
 				cfg.setProperty( Environment.HBM2DDL_IMPORT_FILES, importFile );
 			}
 
 			BasicServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				SchemaExport se = new SchemaExport( serviceRegistry, cfg )
 						.setHaltOnError( halt )
 						.setOutputFile( outFile )
 						.setDelimiter( delim );
 				if ( format ) {
 					se.setFormat( true );
 				}
 				se.execute( script, export, drop, create );
 			}
 			finally {
 				serviceRegistry.destroy();
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToCreateSchema(e);
 			e.printStackTrace();
 		}
 	}
 
 	/**
 	 * Returns a List of all Exceptions which occured during the export.
 	 *
 	 * @return A List containig the Exceptions occured during the export
 	 */
 	public List getExceptions() {
 		return exceptions;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
index 3c2840e6c9..44de7b0060 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaUpdate.java
@@ -1,294 +1,295 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.FileInputStream;
 import java.io.FileWriter;
 import java.io.Writer;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.JDBCException;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.internal.Formatter;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 /**
  * A commandline tool to update a database schema. May also be called from inside an application.
  *
  * @author Christoph Sturm
  * @author Steve Ebersole
  */
 public class SchemaUpdate {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaUpdate.class.getName());
 
 	private final Configuration configuration;
 	private final ConnectionHelper connectionHelper;
 	private final SqlStatementLogger sqlStatementLogger;
 	private final SqlExceptionHelper sqlExceptionHelper;
 	private final Dialect dialect;
 
 	private final List<Exception> exceptions = new ArrayList<Exception>();
 
 	private Formatter formatter;
 
 	private boolean haltOnError = false;
 	private boolean format = true;
 	private String outputFile = null;
 	private String delimiter;
 
 	public SchemaUpdate(Configuration cfg) throws HibernateException {
 		this( cfg, cfg.getProperties() );
 	}
 
 	public SchemaUpdate(Configuration configuration, Properties properties) throws HibernateException {
 		this.configuration = configuration;
 		this.dialect = Dialect.getDialect( properties );
 
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( properties );
 		this.connectionHelper = new ManagedProviderConnectionHelper( props );
 
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 		this.sqlStatementLogger = new SqlStatementLogger( false, true );
 		this.formatter = FormatStyle.DDL.getFormatter();
 	}
 
 	public SchemaUpdate(ServiceRegistry serviceRegistry, Configuration cfg) throws HibernateException {
 		this.configuration = cfg;
 
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		this.dialect = jdbcServices.getDialect();
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper( jdbcServices.getConnectionProvider() );
 
 		this.sqlExceptionHelper = new SqlExceptionHelper();
 		this.sqlStatementLogger = jdbcServices.getSqlStatementLogger();
 		this.formatter = ( sqlStatementLogger.isFormat() ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 	}
 
 	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return new BasicServiceRegistryImpl( properties );
+		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder( properties ).buildServiceRegistry();
 	}
 
 	public static void main(String[] args) {
 		try {
 			Configuration cfg = new Configuration();
 
 			boolean script = true;
 			// If true then execute db updates, otherwise just generate and display updates
 			boolean doUpdate = true;
 			String propFile = null;
 
 			for ( int i = 0; i < args.length; i++ ) {
 				if ( args[i].startsWith( "--" ) ) {
 					if ( args[i].equals( "--quiet" ) ) {
 						script = false;
 					}
 					else if ( args[i].startsWith( "--properties=" ) ) {
 						propFile = args[i].substring( 13 );
 					}
 					else if ( args[i].startsWith( "--config=" ) ) {
 						cfg.configure( args[i].substring( 9 ) );
 					}
 					else if ( args[i].startsWith( "--text" ) ) {
 						doUpdate = false;
 					}
 					else if ( args[i].startsWith( "--naming=" ) ) {
 						cfg.setNamingStrategy(
 								( NamingStrategy ) ReflectHelper.classForName( args[i].substring( 9 ) ).newInstance()
 						);
 					}
 				}
 				else {
 					cfg.addFile( args[i] );
 				}
 
 			}
 
 			if ( propFile != null ) {
 				Properties props = new Properties();
 				props.putAll( cfg.getProperties() );
 				props.load( new FileInputStream( propFile ) );
 				cfg.setProperties( props );
 			}
 
 			BasicServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				new SchemaUpdate( serviceRegistry, cfg ).execute( script, doUpdate );
 			}
 			finally {
 				serviceRegistry.destroy();
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToRunSchemaUpdate(e);
 			e.printStackTrace();
 		}
 	}
 
 	/**
 	 * Execute the schema updates
 	 *
 	 * @param script print all DDL to the console
 	 */
 	public void execute(boolean script, boolean doUpdate) {
 		execute( Target.interpret( script, doUpdate ) );
 	}
 	
 	public void execute(Target target) {
         LOG.runningHbm2ddlSchemaUpdate();
 
 		Connection connection = null;
 		Statement stmt = null;
 		Writer outputFileWriter = null;
 
 		exceptions.clear();
 
 		try {
 			DatabaseMetadata meta;
 			try {
                 LOG.fetchingDatabaseMetadata();
 				connectionHelper.prepare( true );
 				connection = connectionHelper.getConnection();
 				meta = new DatabaseMetadata( connection, dialect );
 				stmt = connection.createStatement();
 			}
 			catch ( SQLException sqle ) {
 				exceptions.add( sqle );
                 LOG.unableToGetDatabaseMetadata(sqle);
 				throw sqle;
 			}
 
             LOG.updatingSchema();
 
 			if ( outputFile != null ) {
                 LOG.writingGeneratedSchemaToFile( outputFile );
 				outputFileWriter = new FileWriter( outputFile );
 			}
 
 			String[] sqlStrings = configuration.generateSchemaUpdateScript( dialect, meta );
 			for ( String sql : sqlStrings ) {
 				String formatted = formatter.format( sql );
 				try {
 					if ( delimiter != null ) {
 						formatted += delimiter;
 					}
 					if ( target.doScript() ) {
 						System.out.println( formatted );
 					}
 					if ( outputFile != null ) {
 						outputFileWriter.write( formatted + "\n" );
 					}
 					if ( target.doExport() ) {
                         LOG.debugf( sql );
 						stmt.executeUpdate( formatted );
 					}
 				}
 				catch ( SQLException e ) {
 					if ( haltOnError ) {
 						throw new JDBCException( "Error during DDL export", e );
 					}
 					exceptions.add( e );
                     LOG.unsuccessful(sql);
                     LOG.error(e.getMessage());
 				}
 			}
 
             LOG.schemaUpdateComplete();
 
 		}
 		catch ( Exception e ) {
 			exceptions.add( e );
             LOG.unableToCompleteSchemaUpdate(e);
 		}
 		finally {
 
 			try {
 				if ( stmt != null ) {
 					stmt.close();
 				}
 				connectionHelper.release();
 			}
 			catch ( Exception e ) {
 				exceptions.add( e );
                 LOG.unableToCloseConnection(e);
 			}
 			try {
 				if( outputFileWriter != null ) {
 					outputFileWriter.close();
 				}
 			}
 			catch(Exception e) {
 				exceptions.add(e);
                 LOG.unableToCloseConnection(e);
 			}
 		}
 	}
 
 	/**
 	 * Returns a List of all Exceptions which occured during the export.
 	 *
 	 * @return A List containig the Exceptions occured during the export
 	 */
 	public List getExceptions() {
 		return exceptions;
 	}
 
 	public void setHaltOnError(boolean haltOnError) {
 		this.haltOnError = haltOnError;
 	}
 
 	public void setFormat(boolean format) {
 		this.formatter = ( format ? FormatStyle.DDL : FormatStyle.NONE ).getFormatter();
 	}
 
 	public void setOutputFile(String outputFile) {
 		this.outputFile = outputFile;
 	}
 
 	public void setDelimiter(String delimiter) {
 		this.delimiter = delimiter;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
index 9d6169d270..980bfff2ef 100755
--- a/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/hbm2ddl/SchemaValidator.java
@@ -1,171 +1,172 @@
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
 package org.hibernate.tool.hbm2ddl;
 
 import java.io.FileInputStream;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 /**
  * A commandline tool to update a database schema. May also be called from
  * inside an application.
  *
  * @author Christoph Sturm
  */
 public class SchemaValidator {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SchemaValidator.class.getName());
 
 	private ConnectionHelper connectionHelper;
 	private Configuration configuration;
 	private Dialect dialect;
 
 	public SchemaValidator(Configuration cfg) throws HibernateException {
 		this( cfg, cfg.getProperties() );
 	}
 
 	public SchemaValidator(Configuration cfg, Properties connectionProperties) throws HibernateException {
 		this.configuration = cfg;
 		dialect = Dialect.getDialect( connectionProperties );
 		Properties props = new Properties();
 		props.putAll( dialect.getDefaultProperties() );
 		props.putAll( connectionProperties );
 		connectionHelper = new ManagedProviderConnectionHelper( props );
 	}
 
 	public SchemaValidator(ServiceRegistry serviceRegistry, Configuration cfg ) throws HibernateException {
 		this.configuration = cfg;
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		this.dialect = jdbcServices.getDialect();
 		this.connectionHelper = new SuppliedConnectionProviderConnectionHelper( jdbcServices.getConnectionProvider() );
 	}
 
 	private static BasicServiceRegistryImpl createServiceRegistry(Properties properties) {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return new BasicServiceRegistryImpl( properties );
+		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder( properties ).buildServiceRegistry();
 	}
 
 	public static void main(String[] args) {
 		try {
 			Configuration cfg = new Configuration();
 
 			String propFile = null;
 
 			for ( int i = 0; i < args.length; i++ ) {
 				if ( args[i].startsWith( "--" ) ) {
 					if ( args[i].startsWith( "--properties=" ) ) {
 						propFile = args[i].substring( 13 );
 					}
 					else if ( args[i].startsWith( "--config=" ) ) {
 						cfg.configure( args[i].substring( 9 ) );
 					}
 					else if ( args[i].startsWith( "--naming=" ) ) {
 						cfg.setNamingStrategy(
 								( NamingStrategy ) ReflectHelper.classForName( args[i].substring( 9 ) ).newInstance()
 						);
 					}
 				}
 				else {
 					cfg.addFile( args[i] );
 				}
 
 			}
 
 			if ( propFile != null ) {
 				Properties props = new Properties();
 				props.putAll( cfg.getProperties() );
 				props.load( new FileInputStream( propFile ) );
 				cfg.setProperties( props );
 			}
 
 			BasicServiceRegistryImpl serviceRegistry = createServiceRegistry( cfg.getProperties() );
 			try {
 				new SchemaValidator( serviceRegistry, cfg ).validate();
 			}
 			finally {
 				serviceRegistry.destroy();
 			}
 		}
 		catch ( Exception e ) {
             LOG.unableToRunSchemaUpdate(e);
 			e.printStackTrace();
 		}
 	}
 
 	/**
 	 * Perform the validations.
 	 */
 	public void validate() {
 
         LOG.runningSchemaValidator();
 
 		Connection connection = null;
 
 		try {
 
 			DatabaseMetadata meta;
 			try {
                 LOG.fetchingDatabaseMetadata();
 				connectionHelper.prepare( false );
 				connection = connectionHelper.getConnection();
 				meta = new DatabaseMetadata( connection, dialect, false );
 			}
 			catch ( SQLException sqle ) {
                 LOG.unableToGetDatabaseMetadata(sqle);
 				throw sqle;
 			}
 
 			configuration.validateSchema( dialect, meta );
 
 		}
 		catch ( SQLException e ) {
             LOG.unableToCompleteSchemaValidation(e);
 		}
 		finally {
 
 			try {
 				connectionHelper.release();
 			}
 			catch ( Exception e ) {
                 LOG.unableToCloseConnection(e);
 			}
 
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
index 88c1a6bd17..4d0fdfbe1a 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
@@ -1,686 +1,686 @@
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
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.EventType;
 import org.hibernate.event.PersistEvent;
 import org.hibernate.event.PersistEventListener;
 import org.hibernate.id.Assigned;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.property.Getter;
 import org.hibernate.property.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerRegistry;
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			AbstractEntityTuplizer.class.getName()
 	);
 
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
                             LOG.debugf( "Performing implicit derived identity cascade" );
 							final PersistEvent event = new PersistEvent( null, propertyValues[i], (EventSource) session );
 							for ( PersistEventListener listener : persistEventListeners( session ) ) {
 								listener.onPersist( event );
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
 
 	private static Iterable<PersistEventListener> persistEventListeners(SessionImplementor session) {
 		return session
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( EventType.PERSIST )
 				.listeners();
 	}
 
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
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
index ab1a2b001e..32628dcbb9 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
@@ -1,138 +1,137 @@
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
 package org.hibernate.metamodel.binding;
 
-import java.util.Collections;
+import org.hibernate.metamodel.relational.Column;
+import org.hibernate.service.BasicServiceRegistry;
+import org.hibernate.service.ServiceRegistryBuilder;
+import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
-import org.hibernate.metamodel.relational.Column;
-import org.hibernate.service.BasicServiceRegistry;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
-
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Basic tests of {@code hbm.xml} and annotation binding code
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractBasicBindingTests extends BaseUnitTestCase {
 
 	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() {
-		serviceRegistry = new BasicServiceRegistryImpl( Collections.emptyMap() );
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	protected BasicServiceRegistry basicServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Test
 	public void testSimpleEntityMapping() {
 		EntityBinding entityBinding = buildSimpleEntityBinding();
 		assertNotNull( entityBinding );
 		assertNotNull( entityBinding.getEntityIdentifier() );
 		assertNotNull( entityBinding.getEntityIdentifier().getValueBinding() );
 		assertNull( entityBinding.getVersioningValueBinding() );
 
 		AttributeBinding idAttributeBinding = entityBinding.getAttributeBinding( "id" );
 		assertNotNull( idAttributeBinding );
 		assertSame( idAttributeBinding, entityBinding.getEntityIdentifier().getValueBinding() );
 		assertNotNull( idAttributeBinding.getAttribute() );
 		assertNotNull( idAttributeBinding.getValue() );
 		assertTrue( idAttributeBinding.getValue() instanceof Column );
 
 		AttributeBinding nameBinding = entityBinding.getAttributeBinding( "name" );
 		assertNotNull( nameBinding );
 		assertNotNull( nameBinding.getAttribute() );
 		assertNotNull( nameBinding.getValue() );
 	}
 
 	@Test
 	public void testSimpleVersionedEntityMapping() {
 		EntityBinding entityBinding = buildSimpleVersionedEntityBinding();
 		assertNotNull( entityBinding );
 		assertNotNull( entityBinding.getEntityIdentifier() );
 		assertNotNull( entityBinding.getEntityIdentifier().getValueBinding() );
 		assertNotNull( entityBinding.getVersioningValueBinding() );
 		assertNotNull( entityBinding.getVersioningValueBinding().getAttribute() );
 
 		AttributeBinding idAttributeBinding = entityBinding.getAttributeBinding( "id" );
 		assertNotNull( idAttributeBinding );
 		assertSame( idAttributeBinding, entityBinding.getEntityIdentifier().getValueBinding() );
 		assertNotNull( idAttributeBinding.getAttribute() );
 		assertNotNull( idAttributeBinding.getValue() );
 		assertTrue( idAttributeBinding.getValue() instanceof Column );
 
 		AttributeBinding nameBinding = entityBinding.getAttributeBinding( "name" );
 		assertNotNull( nameBinding );
 		assertNotNull( nameBinding.getAttribute() );
 		assertNotNull( nameBinding.getValue() );
 	}
 
 	/*
 	@Test
 	public void testEntityWithElementCollection() {
 		EntityBinding entityBinding = buildEntityWithElementCollectionBinding();
 
 		assertNotNull( entityBinding );
 		assertNotNull( entityBinding.getEntityIdentifier() );
 		assertNotNull( entityBinding.getEntityIdentifier().getValueBinding() );
 		assertNull( entityBinding.getVersioningValueBinding() );
 
 		AttributeBinding idAttributeBinding = entityBinding.getAttributeBinding( "id" );
 		assertNotNull( idAttributeBinding );
 		assertSame( idAttributeBinding, entityBinding.getEntityIdentifier().getValueBinding() );
 		assertNotNull( idAttributeBinding.getAttribute() );
 		assertNotNull( idAttributeBinding.getValue() );
 		assertTrue( idAttributeBinding.getValue() instanceof Column );
 
 		AttributeBinding nameBinding = entityBinding.getAttributeBinding( "name" );
 		assertNotNull( nameBinding );
 		assertNotNull( nameBinding.getAttribute() );
 		assertNotNull( nameBinding.getValue() );
 	}
 	*/
 
 	public abstract EntityBinding buildSimpleVersionedEntityBinding();
 
 	public abstract EntityBinding buildSimpleEntityBinding();
 
 	//public abstract EntityBinding buildEntityWithElementCollectionBinding();
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicAnnotationBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicAnnotationBindingTests.java
index 90c09d0590..60dbbb46d9 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicAnnotationBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicAnnotationBindingTests.java
@@ -1,93 +1,92 @@
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
 package org.hibernate.metamodel.binding;
 
 import java.io.IOException;
 import java.io.InputStream;
-import java.util.Collections;
 
 import org.jboss.jandex.Index;
 import org.jboss.jandex.Indexer;
 
 import org.hibernate.metamodel.source.MetadataSources;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.ServiceRegistryBuilder;
 
 import org.junit.Test;
 
 import org.hibernate.testing.FailureExpected;
 
 import static org.junit.Assert.fail;
 
 /**
  * Basic tests of annotation based binding code
  *
  * @author Hardy Ferentschik
  */
 public class BasicAnnotationBindingTests extends AbstractBasicBindingTests {
 
 	@FailureExpected(jiraKey = "HHH-5672", message = "Work in progress")
 	@Test
 	public void testSimpleEntityMapping() {
 		super.testSimpleEntityMapping();
 	}
 
 	@FailureExpected(jiraKey = "HHH-5672", message = "Work in progress")
 	@Test
 	public void testSimpleVersionedEntityMapping() {
 		super.testSimpleVersionedEntityMapping();
 	}
 
 	public EntityBinding buildSimpleEntityBinding() {
 		Index index = indexForClass( SimpleEntity.class );
-		MetadataImpl metadata = (MetadataImpl) new MetadataSources(  new BasicServiceRegistryImpl( Collections.emptyMap() ) ).buildMetadata();
+		MetadataImpl metadata = (MetadataImpl) new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() ).buildMetadata();
 		metadata.getAnnotationBinder().bindMappedClasses( index );
 
 		return metadata.getEntityBinding( SimpleEntity.class.getSimpleName() );
 	}
 
 	public EntityBinding buildSimpleVersionedEntityBinding() {
 		Index index = indexForClass( SimpleEntity.class );
-		MetadataImpl metadata = (MetadataImpl) new MetadataSources(  new BasicServiceRegistryImpl( Collections.emptyMap() ) ).buildMetadata();
+		MetadataImpl metadata = (MetadataImpl) new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() ).buildMetadata();
 		metadata.getAnnotationBinder().bindMappedClasses( index );
 
 		return metadata.getEntityBinding( SimpleVersionedEntity.class.getSimpleName() );
 	}
 
 	private Index indexForClass(Class<?>... classes) {
 		Indexer indexer = new Indexer();
 		for ( Class<?> clazz : classes ) {
 			InputStream stream = getClass().getClassLoader().getResourceAsStream(
 					clazz.getName().replace( '.', '/' ) + ".class"
 			);
 			try {
 				indexer.index( stream );
 			}
 			catch ( IOException e ) {
 				fail( "Unable to index" );
 			}
 		}
 		return indexer.complete();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
index 8bbc038e9c..fc32734ccc 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
@@ -1,122 +1,124 @@
 package org.hibernate.metamodel.source.annotations.global;
 
-import java.util.Collections;
 import java.util.Iterator;
 
 import org.jboss.jandex.Index;
-import org.junit.After;
-import org.junit.Before;
-import org.junit.Test;
 
 import org.hibernate.MappingException;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.FetchProfile;
 import org.hibernate.annotations.FetchProfiles;
 import org.hibernate.metamodel.source.MetadataSources;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
+
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
+
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.fail;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Hardy Ferentschik
  */
 public class FetchProfileBinderTest extends BaseUnitTestCase {
 
 	private BasicServiceRegistryImpl serviceRegistry;
 	private ClassLoaderService service;
 	private MetadataImpl meta;
 
 	@Before
 	public void setUp() {
-		serviceRegistry = new BasicServiceRegistryImpl( Collections.emptyMap() );
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 		service = serviceRegistry.getService( ClassLoaderService.class );
 		meta = (MetadataImpl) new MetadataSources( serviceRegistry ).buildMetadata();
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testSingleFetchProfile() {
 		@FetchProfile(name = "foo", fetchOverrides = {
 				@FetchProfile.FetchOverride(entity = Foo.class, association = "bar", mode = FetchMode.JOIN)
 		})
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 
 		FetchProfileBinder.bindFetchProfiles( meta, index );
 
 		Iterator<org.hibernate.metamodel.binding.FetchProfile> mappedFetchProfiles = meta.getFetchProfiles().iterator();
 		assertTrue( mappedFetchProfiles.hasNext() );
 		org.hibernate.metamodel.binding.FetchProfile profile = mappedFetchProfiles.next();
 		assertEquals( "Wrong fetch profile name", "foo", profile.getName() );
 		org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 		assertEquals( "Wrong association name", "bar", fetch.getAssociation() );
 		assertEquals( "Wrong association type", Foo.class.getName(), fetch.getEntity() );
 	}
 
 	@Test
 	public void testFetchProfiles() {
 		Index index = JandexHelper.indexForClass( service, FooBar.class );
 		FetchProfileBinder.bindFetchProfiles( meta, index );
 
 		Iterator<org.hibernate.metamodel.binding.FetchProfile> mappedFetchProfiles = meta.getFetchProfiles().iterator();
 		assertTrue( mappedFetchProfiles.hasNext() );
 		org.hibernate.metamodel.binding.FetchProfile profile = mappedFetchProfiles.next();
 		assertProfiles( profile );
 
 		assertTrue( mappedFetchProfiles.hasNext() );
 		profile = mappedFetchProfiles.next();
 		assertProfiles( profile );
 	}
 
 	private void assertProfiles(org.hibernate.metamodel.binding.FetchProfile profile) {
 		if ( profile.getName().equals( "foobar" ) ) {
 			org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 			assertEquals( "Wrong association name", "foobar", fetch.getAssociation() );
 			assertEquals( "Wrong association type", FooBar.class.getName(), fetch.getEntity() );
 		}
 		else if ( profile.getName().equals( "fubar" ) ) {
 			org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 			assertEquals( "Wrong association name", "fubar", fetch.getAssociation() );
 			assertEquals( "Wrong association type", FooBar.class.getName(), fetch.getEntity() );
 		}
 		else {
 			fail( "Wrong fetch name:" + profile.getName() );
 		}
 	}
 
 	@Test(expected = MappingException.class)
 	public void testNonJoinFetchThrowsException() {
 		@FetchProfile(name = "foo", fetchOverrides = {
 				@FetchProfile.FetchOverride(entity = Foo.class, association = "bar", mode = FetchMode.SELECT)
 		})
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 
 		FetchProfileBinder.bindFetchProfiles( meta, index );
 	}
 
 	@FetchProfiles( {
 			@FetchProfile(name = "foobar", fetchOverrides = {
 					@FetchProfile.FetchOverride(entity = FooBar.class, association = "foobar", mode = FetchMode.JOIN)
 			}),
 			@FetchProfile(name = "fubar", fetchOverrides = {
 					@FetchProfile.FetchOverride(entity = FooBar.class, association = "fubar", mode = FetchMode.JOIN)
 			})
 	})
 	class FooBar {
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/ConfiguredClassHierarchyBuilderTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/ConfiguredClassHierarchyBuilderTest.java
index 8d0763b212..4be69d7d0a 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/ConfiguredClassHierarchyBuilderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/util/ConfiguredClassHierarchyBuilderTest.java
@@ -1,290 +1,291 @@
 package org.hibernate.metamodel.source.annotations.util;
 
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.Set;
 import javax.persistence.AccessType;
 import javax.persistence.Entity;
 import javax.persistence.GeneratedValue;
 import javax.persistence.Id;
 import javax.persistence.Inheritance;
 import javax.persistence.InheritanceType;
 import javax.persistence.MappedSuperclass;
 
 import org.jboss.jandex.ClassInfo;
 import org.jboss.jandex.DotName;
 import org.jboss.jandex.Index;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.metamodel.source.annotations.ConfiguredClass;
 import org.hibernate.metamodel.source.annotations.ConfiguredClassHierarchy;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertTrue;
 import static org.junit.Assert.assertFalse;
 
 /**
  * @author Hardy Ferentschik
  */
 public class ConfiguredClassHierarchyBuilderTest extends BaseUnitTestCase {
 
 	private BasicServiceRegistryImpl serviceRegistry;
 	private ClassLoaderService service;
 
 	@Before
 	public void setUp() {
-		serviceRegistry = new BasicServiceRegistryImpl( Collections.emptyMap() );
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 		service = serviceRegistry.getService( ClassLoaderService.class );
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testSingleEntity() {
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 		Set<ConfiguredClassHierarchy> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				index, serviceRegistry
 		);
 		assertEquals( "There should be only one hierarchy", 1, hierarchies.size() );
 
 		Iterator<ConfiguredClass> iter = hierarchies.iterator().next().iterator();
 		ClassInfo info = iter.next().getClassInfo();
 		assertEquals( "wrong class", DotName.createSimple( Foo.class.getName() ), info.name() );
 		assertFalse( iter.hasNext() );
 	}
 
 	@Test
 	public void testSimpleInheritance() {
 		Index index = JandexHelper.indexForClass( service, B.class, A.class );
 		Set<ConfiguredClassHierarchy> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				index, serviceRegistry
 		);
 		assertEquals( "There should be only one hierarchy", 1, hierarchies.size() );
 
 		Iterator<ConfiguredClass> iter = hierarchies.iterator().next().iterator();
 		ClassInfo info = iter.next().getClassInfo();
 		assertEquals( "wrong class", DotName.createSimple( A.class.getName() ), info.name() );
 		info = iter.next().getClassInfo();
 		assertEquals( "wrong class", DotName.createSimple( B.class.getName() ), info.name() );
 		assertFalse( iter.hasNext() );
 	}
 
 	@Test
 	public void testMultipleHierarchies() {
 		Index index = JandexHelper.indexForClass( service, B.class, A.class, Foo.class );
 		Set<ConfiguredClassHierarchy> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				index, serviceRegistry
 		);
 		assertEquals( "There should be only one hierarchy", 2, hierarchies.size() );
 	}
 
 	@Test
 	public void testMappedSuperClass() {
 		@MappedSuperclass
 		class MappedSuperClass {
 			@Id
 			@GeneratedValue
 			private int id;
 		}
 
 		class UnmappedSubClass extends MappedSuperClass {
 			private String unmappedProperty;
 		}
 
 		@Entity
 		class MappedSubClass extends UnmappedSubClass {
 			private String mappedProperty;
 		}
 
 		Index index = JandexHelper.indexForClass(
 				service, MappedSubClass.class, MappedSuperClass.class, UnmappedSubClass.class
 		);
 		Set<ConfiguredClassHierarchy> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				index, serviceRegistry
 		);
 		assertEquals( "There should be only one hierarchy", 1, hierarchies.size() );
 
 		Iterator<ConfiguredClass> iter = hierarchies.iterator().next().iterator();
 		ClassInfo info = iter.next().getClassInfo();
 		assertEquals( "wrong class", DotName.createSimple( MappedSuperClass.class.getName() ), info.name() );
 		info = iter.next().getClassInfo();
 		assertEquals( "wrong class", DotName.createSimple( UnmappedSubClass.class.getName() ), info.name() );
 		info = iter.next().getClassInfo();
 		assertEquals( "wrong class", DotName.createSimple( MappedSubClass.class.getName() ), info.name() );
 		assertFalse( iter.hasNext() );
 	}
 
 	@Test(expected = AnnotationException.class)
 	public void testEntityAndMappedSuperClassAnnotations() {
 		@Entity
 		@MappedSuperclass
 		class EntityAndMappedSuperClass {
 		}
 
 		Index index = JandexHelper.indexForClass( service, EntityAndMappedSuperClass.class );
 		ConfiguredClassHierarchyBuilder.createEntityHierarchies( index, serviceRegistry );
 	}
 
 	@Test(expected = AnnotationException.class)
 	public void testNoIdAnnotation() {
 
 		@Entity
 		class A {
 			String id;
 		}
 
 		@Entity
 		class B extends A {
 		}
 
 		Index index = JandexHelper.indexForClass( service, B.class, A.class );
 		ConfiguredClassHierarchyBuilder.createEntityHierarchies( index, serviceRegistry );
 	}
 
 	@Test
 	public void testDefaultFieldAccess() {
 		@Entity
 		class A {
 			@Id
 			String id;
 		}
 
 		@Entity
 		class B extends A {
 		}
 
 		Index index = JandexHelper.indexForClass( service, B.class, A.class );
 		Set<ConfiguredClassHierarchy> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				index, serviceRegistry
 		);
 		assertTrue( hierarchies.size() == 1 );
 		ConfiguredClassHierarchy hierarchy = hierarchies.iterator().next();
 		assertEquals( "Wrong default access type", AccessType.FIELD, hierarchy.getDefaultAccessType() );
 	}
 
 	@Test
 	public void testDefaultPropertyAccess() {
 		@Entity
 		class A {
 			String id;
 
 			@Id
 			public String getId() {
 				return id;
 			}
 
 			public void setId(String id) {
 				this.id = id;
 			}
 		}
 
 		@Entity
 		class B extends A {
 		}
 
 		Index index = JandexHelper.indexForClass( service, B.class, A.class );
 		Set<ConfiguredClassHierarchy> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				index, serviceRegistry
 		);
 		assertTrue( hierarchies.size() == 1 );
 		ConfiguredClassHierarchy hierarchy = hierarchies.iterator().next();
 		assertEquals( "Wrong default access type", AccessType.PROPERTY, hierarchy.getDefaultAccessType() );
 	}
 
 	@Test
 	public void testDefaultInheritanceStrategy() {
 		@Entity
 		class A {
 			@Id
 			String id;
 		}
 
 		@Entity
 		class B extends A {
 		}
 
 		Index index = JandexHelper.indexForClass( service, B.class, A.class );
 		Set<ConfiguredClassHierarchy> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				index, serviceRegistry
 		);
 		assertTrue( hierarchies.size() == 1 );
 		ConfiguredClassHierarchy hierarchy = hierarchies.iterator().next();
 		assertEquals( "Wrong inheritance type", InheritanceType.SINGLE_TABLE, hierarchy.getInheritanceType() );
 	}
 
 
 	@Test
 	public void testExplicitInheritanceStrategy() {
 		@MappedSuperclass
 		class MappedSuperClass {
 
 		}
 
 		@Entity
 		@Inheritance(strategy = InheritanceType.JOINED)
 		class A extends MappedSuperClass {
 			@Id
 			String id;
 		}
 
 		@Entity
 		class B extends A {
 		}
 
 		Index index = JandexHelper.indexForClass( service, B.class, MappedSuperClass.class, A.class );
 		Set<ConfiguredClassHierarchy> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				index, serviceRegistry
 		);
 		assertTrue( hierarchies.size() == 1 );
 		ConfiguredClassHierarchy hierarchy = hierarchies.iterator().next();
 		assertEquals( "Wrong inheritance type", InheritanceType.JOINED, hierarchy.getInheritanceType() );
 	}
 
 	@Test(expected = AnnotationException.class)
 	public void testMultipleConflictingInheritanceDefinitions() {
 
 		@Entity
 		@Inheritance(strategy = InheritanceType.JOINED)
 		class A {
 			String id;
 		}
 
 		@Entity
 		@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
 		class B extends A {
 		}
 
 		Index index = JandexHelper.indexForClass( service, B.class, A.class );
 		ConfiguredClassHierarchyBuilder.createEntityHierarchies( index, serviceRegistry );
 	}
 
 	@Entity
 	public class Foo {
 		@Id
 		@GeneratedValue
 		private int id;
 	}
 
 	@Entity
 	public class A {
 		@Id
 		@GeneratedValue
 		private int id;
 	}
 
 	@Entity
 	public class B extends A {
 		private String name;
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParserTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParserTests.java
index 158831e4bc..7a606d58db 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParserTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParserTests.java
@@ -1,37 +1,38 @@
 package org.hibernate.metamodel.source.annotations.xml;
 
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.junit.Test;
 
 import org.hibernate.metamodel.source.MetadataSources;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Hardy Ferentschik
  */
 public class OrmXmlParserTests extends BaseUnitTestCase {
 	@Test
 	public void testSingleOrmXml() {
-		MetadataImpl metadata = (MetadataImpl) new MetadataSources(  new BasicServiceRegistryImpl( Collections.emptyMap() ) ).buildMetadata();
+		MetadataImpl metadata = (MetadataImpl) new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() ).buildMetadata();
 		OrmXmlParser parser = new OrmXmlParser( metadata );
 		Set<String> xmlFiles = new HashSet<String>();
 		xmlFiles.add( "org/hibernate/metamodel/source/annotations/xml/orm.xml" );
 		parser.parseAndUpdateIndex( xmlFiles, null );
 	}
 
 	@Test
 	public void testOrmXmlWithOldSchema() {
-		MetadataImpl metadata = (MetadataImpl) new MetadataSources(  new BasicServiceRegistryImpl( Collections.emptyMap() ) ).buildMetadata();
+		MetadataImpl metadata = (MetadataImpl) new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() ).buildMetadata();
 		OrmXmlParser parser = new OrmXmlParser( metadata );
 		Set<String> xmlFiles = new HashSet<String>();
 		xmlFiles.add( "org/hibernate/metamodel/source/annotations/xml/orm-star.xml" );
 		parser.parseAndUpdateIndex( xmlFiles, null );
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/event/collection/CollectionListeners.java b/hibernate-core/src/test/java/org/hibernate/test/event/collection/CollectionListeners.java
index be17efb04b..f76692ac33 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/event/collection/CollectionListeners.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/event/collection/CollectionListeners.java
@@ -1,207 +1,207 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.event.collection;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.event.AbstractCollectionEvent;
 import org.hibernate.event.EventType;
 import org.hibernate.event.InitializeCollectionEvent;
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
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 
 /**
  * @author Gail Badner
  * @author Steve Ebersole
  */
 public class CollectionListeners {
 
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
 
 		EventListenerRegistry registry = ( (SessionFactoryImplementor) sf ).getServiceRegistry().getService( EventListenerRegistry.class );
 		registry.setListeners( EventType.INIT_COLLECTION, initializeCollectionListener );
 
 		registry.setListeners( EventType.PRE_COLLECTION_RECREATE, preCollectionRecreateListener );
 		registry.setListeners( EventType.POST_COLLECTION_RECREATE, postCollectionRecreateListener );
 
 		registry.setListeners( EventType.PRE_COLLECTION_REMOVE, preCollectionRemoveListener );
 		registry.setListeners( EventType.POST_COLLECTION_REMOVE, postCollectionRemoveListener );
 
 		registry.setListeners( EventType.PRE_COLLECTION_UPDATE, preCollectionUpdateListener );
 		registry.setListeners( EventType.POST_COLLECTION_UPDATE, postCollectionUpdateListener );
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
index 454515193b..9dbe9c7d86 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/events/CallbackTest.java
@@ -1,128 +1,135 @@
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
-import java.util.Map;
+
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cfg.Configuration;
+import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.event.DeleteEvent;
 import org.hibernate.event.DeleteEventListener;
 import org.hibernate.event.Destructible;
-import org.hibernate.event.EventListenerRegistration;
 import org.hibernate.event.EventType;
 import org.hibernate.event.Initializable;
-import org.hibernate.service.StandardServiceInitiators;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerRegistry;
+import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.hibernate.service.spi.SessionFactoryServiceRegistry;
+import org.hibernate.integrator.spi.Integrator;
 
 import org.junit.Test;
 
 import org.hibernate.testing.FailureExpected;
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
 	}
 
 	@Override
 	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 		super.applyServices( serviceRegistry );
-		serviceRegistry.getService( StandardServiceInitiators.EventListenerRegistrationService.class ).attachEventListenerRegistration(
-				new EventListenerRegistration() {
+		serviceRegistry.getService( IntegratorService.class ).addIntegrator(
+				new Integrator() {
 					@Override
-					public void apply(
-							EventListenerRegistry eventListenerRegistry,
+					public void integrate(
 							Configuration configuration,
-							Map<?, ?> configValues,
-							ServiceRegistryImplementor serviceRegistry) {
-						eventListenerRegistry.setListeners( EventType.DELETE, listener );
+							SessionFactoryImplementor sessionFactory,
+							SessionFactoryServiceRegistry serviceRegistry) {
+						serviceRegistry.getService( EventListenerRegistry.class ).setListeners(
+								EventType.DELETE, listener
+						);
+					}
+
+					@Override
+					public void disintegrate(
+							SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 					}
 				}
 		);
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "HHH-5913", message = "Need to figure out how to initialize/destroy event listeners now")
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java b/hibernate-core/src/test/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java
index 47ba305144..64d4acb02a 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/flush/TestCollectionInitializingDuringFlush.java
@@ -1,98 +1,103 @@
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
 package org.hibernate.test.flush;
 
-import java.util.Map;
-
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.event.EventListenerRegistration;
+import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.event.EventType;
-import org.hibernate.service.StandardServiceInitiators;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerRegistry;
+import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.hibernate.service.spi.SessionFactoryServiceRegistry;
+import org.hibernate.integrator.spi.Integrator;
 
 import org.junit.Test;
 
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * @author Steve Ebersole
  */
 @TestForIssue( jiraKey = "HHH-2763" )
 public class TestCollectionInitializingDuringFlush extends BaseCoreFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] { Author.class, Book.class, Publisher.class };
 	}
 
 	@Override
 	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 		super.applyServices( serviceRegistry );
-		serviceRegistry.getService( StandardServiceInitiators.EventListenerRegistrationService.class ).attachEventListenerRegistration(
-				new EventListenerRegistration() {
+		serviceRegistry.getService( IntegratorService.class ).addIntegrator(
+				new Integrator() {
 					@Override
-					public void apply(
-							EventListenerRegistry eventListenerRegistry,
+					public void integrate(
 							Configuration configuration,
-							Map<?, ?> configValues,
-							ServiceRegistryImplementor serviceRegistry) {
-						eventListenerRegistry.getEventListenerGroup( EventType.PRE_UPDATE ).appendListener( new InitializingPreUpdateEventListener() );
+							SessionFactoryImplementor sessionFactory,
+							SessionFactoryServiceRegistry serviceRegistry) {
+						serviceRegistry.getService( EventListenerRegistry.class )
+								.getEventListenerGroup( EventType.PRE_UPDATE )
+								.appendListener( new InitializingPreUpdateEventListener() );
+					}
+
+					@Override
+					public void disintegrate(
+							SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 					}
 				}
 		);
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "HHH-2763" )
 	public void testInitializationDuringFlush() {
 		Session s = openSession();
 		s.beginTransaction();
 		Publisher publisher = new Publisher( "acme" );
 		Author author = new Author( "john" );
 		author.setPublisher( publisher );
 		publisher.getAuthors().add( author );
 		author.getBooks().add( new Book( "Reflections on a Wimpy Kid", author ) );
 		s.save( author );
 		s.getTransaction().commit();
 		s.clear();
 
 		s = openSession();
 		s.beginTransaction();
 		publisher = (Publisher) s.get( Publisher.class, publisher.getId() );
 		publisher.setName( "random nally" );
 		s.flush();
 		s.getTransaction().commit();
 		s.clear();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( author );
 		s.getTransaction().commit();
 		s.clear();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
index 2dad3fb5f1..00a09106c8 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jdbc/proxies/BatchingTest.java
@@ -1,222 +1,220 @@
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
 package org.hibernate.test.jdbc.proxies;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.Statement;
 
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.jdbc.batch.internal.BatchBuilderImpl;
 import org.hibernate.engine.jdbc.batch.internal.BatchingBatch;
 import org.hibernate.engine.jdbc.batch.internal.NonBatchingBatch;
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
 import org.hibernate.engine.jdbc.batch.spi.BatchKey;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
-import org.hibernate.service.StandardServiceInitiators;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.common.JournalingBatchObserver;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Steve Ebersole
  */
 public class BatchingTest extends BaseUnitTestCase implements BatchKey {
 	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() throws Exception {
-		serviceRegistry = new BasicServiceRegistryImpl(
-				StandardServiceInitiators.LIST,
-				ConnectionProviderBuilder.getConnectionProviderProperties()
-		);
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( ConnectionProviderBuilder.getConnectionProviderProperties() )
+				.buildServiceRegistry();
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
 	@Override
 	public int getBatchedStatementCount() {
 		return 1;
 	}
 
 	@Override
 	public Expectation getExpectation() {
 		return Expectations.BASIC;
 	}
 
 	@Test
 	public void testNonBatchingUsage() throws Exception {
 		final TransactionContext transactionContext = new TransactionContextImpl(
 				new TransactionEnvironmentImpl( serviceRegistry )
 		);
 
 		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		JournalingTransactionObserver observer = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( observer );
 
 		final JdbcCoordinator jdbcCoordinator = transactionCoordinator.getJdbcCoordinator();
 		LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		Statement statement = connection.createStatement();
 		statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 		statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 		assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() );
 		statement.close();
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() ); // after_transaction specified
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, observer.getBegins() );
 
 		final String insertSql = "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )";
 
 		final BatchBuilder batchBuilder = new BatchBuilderImpl( -1 );
 		final BatchKey batchKey = new BasicBatchKey( "this", Expectations.BASIC );
 		final Batch insertBatch = batchBuilder.buildBatch( batchKey, jdbcCoordinator );
 
 		final JournalingBatchObserver batchObserver = new JournalingBatchObserver();
 		insertBatch.addObserver( batchObserver );
 
 		assertTrue( "unexpected Batch impl", NonBatchingBatch.class.isInstance( insertBatch ) );
 		PreparedStatement insert = insertBatch.getBatchStatement( insertSql, false );
 		insert.setLong( 1, 1 );
 		insert.setString( 2, "name" );
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 0, batchObserver.getImplicitExecutionCount() );
 		insertBatch.addToBatch();
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		insertBatch.execute();
 		assertEquals( 1, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		insertBatch.release();
 
 		txn.commit();
 		logicalConnection.close();
 	}
 
 	@Test
 	public void testBatchingUsage() throws Exception {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) );
 
 		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		JournalingTransactionObserver transactionObserver = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( transactionObserver );
 
 		final JdbcCoordinator jdbcCoordinator = transactionCoordinator.getJdbcCoordinator();
 		LogicalConnectionImplementor logicalConnection = jdbcCoordinator.getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		Statement statement = connection.createStatement();
 		statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 		statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 		assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() );
 		statement.close();
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 		assertTrue( logicalConnection.isPhysicallyConnected() ); // after_transaction specified
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, transactionObserver.getBegins() );
 
 		final BatchBuilder batchBuilder = new BatchBuilderImpl( 2 );
 		final BatchKey batchKey = new BasicBatchKey( "this", Expectations.BASIC );
 		final Batch insertBatch = batchBuilder.buildBatch( batchKey, jdbcCoordinator );
 		assertTrue( "unexpected Batch impl", BatchingBatch.class.isInstance( insertBatch ) );
 
 		final JournalingBatchObserver batchObserver = new JournalingBatchObserver();
 		insertBatch.addObserver( batchObserver );
 
 		final String insertSql = "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )";
 
 		PreparedStatement insert = insertBatch.getBatchStatement( insertSql, false );
 		insert.setLong( 1, 1 );
 		insert.setString( 2, "name" );
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 0, batchObserver.getImplicitExecutionCount() );
 		insertBatch.addToBatch();
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 0, batchObserver.getImplicitExecutionCount() );
 		assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		PreparedStatement insert2 = insertBatch.getBatchStatement( insertSql, false );
 		assertSame( insert, insert2 );
 		insert = insert2;
 		insert.setLong( 1, 2 );
 		insert.setString( 2, "another name" );
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 0, batchObserver.getImplicitExecutionCount() );
 		insertBatch.addToBatch();
 		assertEquals( 0, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		insertBatch.execute();
 		assertEquals( 1, batchObserver.getExplicitExecutionCount() );
 		assertEquals( 1, batchObserver.getImplicitExecutionCount() );
 		assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 		insertBatch.release();
 
 		txn.commit();
 		logicalConnection.close();
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java b/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
index 0680a7e167..7ac2c51fed 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/jpa/AbstractJPATest.java
@@ -1,173 +1,180 @@
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
-import java.util.Map;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.CascadingAction;
+import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.event.AutoFlushEventListener;
-import org.hibernate.event.EventListenerRegistration;
 import org.hibernate.event.EventType;
 import org.hibernate.event.FlushEntityEventListener;
 import org.hibernate.event.FlushEventListener;
 import org.hibernate.event.PersistEventListener;
 import org.hibernate.event.def.DefaultAutoFlushEventListener;
 import org.hibernate.event.def.DefaultFlushEntityEventListener;
 import org.hibernate.event.def.DefaultFlushEventListener;
 import org.hibernate.event.def.DefaultPersistEventListener;
+import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.proxy.EntityNotFoundDelegate;
-import org.hibernate.service.StandardServiceInitiators;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerRegistry;
+import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
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
 	}
 
 	@Override
 	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 		super.applyServices( serviceRegistry );
-		serviceRegistry.getService( StandardServiceInitiators.EventListenerRegistrationService.class ).attachEventListenerRegistration(
-				new EventListenerRegistration() {
+		serviceRegistry.getService( IntegratorService.class ).addIntegrator(
+				new Integrator() {
 					@Override
-					public void apply(
-							EventListenerRegistry eventListenerRegistry,
+					public void integrate(
 							Configuration configuration,
-							Map<?, ?> configValues,
-							ServiceRegistryImplementor serviceRegistry) {
+							SessionFactoryImplementor sessionFactory,
+							SessionFactoryServiceRegistry serviceRegistry) {
+						EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 						eventListenerRegistry.setListeners( EventType.PERSIST, buildPersistEventListeners() );
-						eventListenerRegistry.setListeners( EventType.PERSIST_ONFLUSH, buildPersisOnFlushEventListeners() );
+						eventListenerRegistry.setListeners(
+								EventType.PERSIST_ONFLUSH, buildPersisOnFlushEventListeners()
+						);
 						eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, buildAutoFlushEventListeners() );
 						eventListenerRegistry.setListeners( EventType.FLUSH, buildFlushEventListeners() );
 						eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, buildFlushEntityEventListeners() );
 					}
+
+					@Override
+					public void disintegrate(
+							SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
+					}
 				}
 		);
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
index e42fa71373..e22d4d8ffc 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/keymanytoone/bidir/component/EagerKeyManyToOneTest.java
@@ -1,194 +1,198 @@
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
 
-import java.util.Map;
-
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.event.EventListenerRegistration;
+import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.event.EventType;
 import org.hibernate.event.LoadEvent;
 import org.hibernate.event.LoadEventListener;
 import org.hibernate.event.def.DefaultLoadEventListener;
-import org.hibernate.service.StandardServiceInitiators;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerRegistry;
+import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
-import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.hibernate.service.spi.SessionFactoryServiceRegistry;
+import org.hibernate.integrator.spi.Integrator;
 
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
 	}
 
 	@Override
 	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 		super.applyServices( serviceRegistry );
 
-		// (they are different service registries!)
-		serviceRegistry.getService( StandardServiceInitiators.EventListenerRegistrationService.class ).attachEventListenerRegistration(
-				new EventListenerRegistration() {
+		serviceRegistry.getService( IntegratorService.class ).addIntegrator(
+				new Integrator() {
 					@Override
-					public void apply(
-							EventListenerRegistry eventListenerRegistry,
+					public void integrate(
 							Configuration configuration,
-							Map<?, ?> configValues,
-							ServiceRegistryImplementor serviceRegistry) {
-						eventListenerRegistry.prependListeners( EventType.LOAD, new CustomLoadListener() );
+							SessionFactoryImplementor sessionFactory,
+							SessionFactoryServiceRegistry serviceRegistry) {
+						serviceRegistry.getService( EventListenerRegistry.class ).prependListeners(
+								EventType.LOAD, new CustomLoadListener()
+						);
+					}
+
+					@Override
+					public void disintegrate(
+							SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 					}
 				}
 		);
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
index 87f589cadb..74d78f51cb 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
@@ -1,304 +1,303 @@
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
 package org.hibernate.test.multitenancy.schema;
 
 import java.sql.Connection;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Session;
-import org.hibernate.SessionFactory;
 import org.hibernate.cache.HashtableCacheProvider;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.SessionFactoryImplementor;
-import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
 import org.hibernate.service.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.tool.hbm2ddl.ConnectionHelper;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 
 import org.junit.After;
 import org.junit.Assert;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public class SchemaBasedMultiTenancyTest extends BaseUnitTestCase {
 	private DriverManagerConnectionProviderImpl acmeProvider;
 	private DriverManagerConnectionProviderImpl jbossProvider;
 
 	private ServiceRegistryImplementor serviceRegistry;
 
 	private SessionFactoryImplementor sessionFactory;
 
 	@Before
 	public void setUp() {
 		acmeProvider = ConnectionProviderBuilder.buildConnectionProvider( "acme" );
 		jbossProvider = ConnectionProviderBuilder.buildConnectionProvider( "jboss" );
 		AbstractMultiTenantConnectionProvider multiTenantConnectionProvider = new AbstractMultiTenantConnectionProvider() {
 			@Override
 			protected ConnectionProvider getAnyConnectionProvider() {
 				return acmeProvider;
 			}
 
 			@Override
 			protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
 				if ( "acme".equals( tenantIdentifier ) ) {
 					return acmeProvider;
 				}
 				else if ( "jboss".equals( tenantIdentifier ) ) {
 					return jbossProvider;
 				}
 				throw new HibernateException( "Unknown tenant identifier" );
 			}
 		};
 
 		Configuration cfg = new Configuration();
 		cfg.getProperties().put( Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE );
 		cfg.setProperty( Environment.CACHE_PROVIDER, HashtableCacheProvider.class.getName() );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 		cfg.addAnnotatedClass( Customer.class );
 
 		cfg.buildMappings();
 		RootClass meta = (RootClass) cfg.getClassMapping( Customer.class.getName() );
 		meta.setCacheConcurrencyStrategy( "read-write" );
 
 		// do the acme export
 		new SchemaExport(
 				new ConnectionHelper() {
 					private Connection connection;
 					@Override
 					public void prepare(boolean needsAutoCommit) throws SQLException {
 						connection = acmeProvider.getConnection();
 					}
 
 					@Override
 					public Connection getConnection() throws SQLException {
 						return connection;
 					}
 
 					@Override
 					public void release() throws SQLException {
 						acmeProvider.closeConnection( connection );
 					}
 				},
 				cfg.generateDropSchemaScript( ConnectionProviderBuilder.getCorrespondingDialect() ),
 				cfg.generateSchemaCreationScript( ConnectionProviderBuilder.getCorrespondingDialect() )
 		).execute(		 // so stupid...
 						   false,	 // do not script the export (write it to file)
 						   true,	 // do run it against the database
 						   false,	 // do not *just* perform the drop
 						   false	// do not *just* perform the create
 		);
 
 		// do the jboss export
 		new SchemaExport(
 				new ConnectionHelper() {
 					private Connection connection;
 					@Override
 					public void prepare(boolean needsAutoCommit) throws SQLException {
 						connection = jbossProvider.getConnection();
 					}
 
 					@Override
 					public Connection getConnection() throws SQLException {
 						return connection;
 					}
 
 					@Override
 					public void release() throws SQLException {
 						jbossProvider.closeConnection( connection );
 					}
 				},
 				cfg.generateDropSchemaScript( ConnectionProviderBuilder.getCorrespondingDialect() ),
 				cfg.generateSchemaCreationScript( ConnectionProviderBuilder.getCorrespondingDialect() )
 		).execute( 		// so stupid...
 				false, 	// do not script the export (write it to file)
 				true, 	// do run it against the database
 				false, 	// do not *just* perform the drop
 				false	// do not *just* perform the create
 		);
 
-		serviceRegistry = new BasicServiceRegistryImpl( cfg.getProperties() );
-		serviceRegistry.registerService( MultiTenantConnectionProvider.class, multiTenantConnectionProvider );
+		serviceRegistry = (ServiceRegistryImplementor) new ServiceRegistryBuilder( cfg.getProperties() )
+				.addService( MultiTenantConnectionProvider.class, multiTenantConnectionProvider )
+				.buildServiceRegistry();
 
 		sessionFactory = (SessionFactoryImplementor) cfg.buildSessionFactory( serviceRegistry );
 	}
 
 	@After
 	public void tearDown() {
 		if ( sessionFactory != null ) {
 			sessionFactory.close();
 		}
 		if ( serviceRegistry != null ) {
 			serviceRegistry.destroy();
 		}
 		if ( jbossProvider != null ) {
 			jbossProvider.stop();
 		}
 		if ( acmeProvider != null ) {
 			acmeProvider.stop();
 		}
 	}
 
 	private Session openSession() {
 		return sessionFactory.openSession();
 	}
 
 	@Test
 	public void testBasicExpectedBehavior() {
 		Session session = openSession();
 		session.setTenantIdentifier( "jboss" );
 		session.beginTransaction();
 		Customer steve = new Customer( 1L, "steve" );
 		session.save( steve );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		try {
 			session.setTenantIdentifier( "acme" );
 			session.beginTransaction();
 			Customer check = (Customer) session.get( Customer.class, steve.getId() );
 			Assert.assertNull( "tenancy not properly isolated", check );
 		}
 		finally {
 			session.getTransaction().commit();
 			session.close();
 		}
 
 		session = openSession();
 		session.setTenantIdentifier( "jboss" );
 		session.beginTransaction();
 		session.delete( steve );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testSameIdentifiers() {
 		// create a customer 'steve' in jboss
 		Session session = openSession();
 		session.setTenantIdentifier( "jboss" );
 		session.beginTransaction();
 		Customer steve = new Customer( 1L, "steve" );
 		session.save( steve );
 		session.getTransaction().commit();
 		session.close();
 
 		// now, create a customer 'john' in acme
 		session = openSession();
 		session.setTenantIdentifier( "acme" );
 		session.beginTransaction();
 		Customer john = new Customer( 1L, "john" );
 		session.save( john );
 		session.getTransaction().commit();
 		session.close();
 
 		sessionFactory.getStatisticsImplementor().clear();
 
 		// make sure we get the correct people back, from cache
 		// first, jboss
 		{
 			session = openSession();
 			session.setTenantIdentifier( "jboss" );
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "steve", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 1, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 		sessionFactory.getStatisticsImplementor().clear();
 		// then, acme
 		{
 			session = openSession();
 			session.setTenantIdentifier( "acme" );
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "john", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 1, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 
 		// make sure the same works from datastore too
 		sessionFactory.getStatisticsImplementor().clear();
 		sessionFactory.getCache().evictEntityRegions();
 		// first jboss
 		{
 			session = openSession();
 			session.setTenantIdentifier( "jboss" );
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "steve", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 0, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 		sessionFactory.getStatisticsImplementor().clear();
 		// then, acme
 		{
 			session = openSession();
 			session.setTenantIdentifier( "acme" );
 			session.beginTransaction();
 			Customer customer = (Customer) session.load( Customer.class, 1L );
 			Assert.assertEquals( "john", customer.getName() );
 			// also, make sure this came from second level
 			Assert.assertEquals( 0, sessionFactory.getStatisticsImplementor().getSecondLevelCacheHitCount() );
 			session.getTransaction().commit();
 			session.close();
 		}
 
 		session = openSession();
 		session.setTenantIdentifier( "jboss" );
 		session.beginTransaction();
 		session.delete( steve );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.setTenantIdentifier( "acme" );
 		session.beginTransaction();
 		session.delete( john );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java b/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
index 7c596512f3..cbe1f046b9 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/service/ServiceBootstrappingTest.java
@@ -1,91 +1,95 @@
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
 package org.hibernate.test.service;
 
 import java.util.Properties;
 
 import org.junit.Test;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
 import org.hibernate.service.jdbc.connections.internal.UserSuppliedConnectionProviderImpl;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public class ServiceBootstrappingTest extends BaseUnitTestCase {
 	@Test
 	public void testBasicBuild() {
-		BasicServiceRegistryImpl serviceRegistry = new BasicServiceRegistryImpl( ConnectionProviderBuilder.getConnectionProviderProperties() );
+		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder(
+				ConnectionProviderBuilder.getConnectionProviderProperties()
+		).buildServiceRegistry();
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 		assertFalse( jdbcServices.getSqlStatementLogger().isLogToStdout() );
 
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBuildWithLogging() {
 		Properties props = ConnectionProviderBuilder.getConnectionProviderProperties();
 		props.put( Environment.SHOW_SQL, "true" );
 
-		BasicServiceRegistryImpl serviceRegistry = new BasicServiceRegistryImpl( props );
+		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( props ).buildServiceRegistry();
+
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 		assertTrue( jdbcServices.getSqlStatementLogger().isLogToStdout() );
 
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBuildWithServiceOverride() {
 		Properties props = ConnectionProviderBuilder.getConnectionProviderProperties();
 
-		BasicServiceRegistryImpl serviceRegistry = new BasicServiceRegistryImpl( props );
+		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( props ).buildServiceRegistry();
 		JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( DriverManagerConnectionProviderImpl.class ) );
 
 		serviceRegistry.registerService( ConnectionProvider.class, new UserSuppliedConnectionProviderImpl() );
 
 		assertTrue( jdbcServices.getDialect() instanceof H2Dialect );
 		assertTrue( jdbcServices.getConnectionProvider().isUnwrappableAs( UserSuppliedConnectionProviderImpl.class ) );
 
 		serviceRegistry.destroy();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
index 7b47132d88..c0f77af919 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jdbc/TestExpectedUsage.java
@@ -1,142 +1,141 @@
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
 package org.hibernate.test.transaction.jdbc;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
-import org.hibernate.service.StandardServiceInitiators;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
-import static org.junit.Assert.assertEquals;
-import static org.junit.Assert.assertFalse;
-import static org.junit.Assert.assertTrue;
-import static org.junit.Assert.fail;
 
-import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.testing.env.ConnectionProviderBuilder;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertTrue;
+import static org.junit.Assert.fail;
+
 /**
  * @author Steve Ebersole
  */
 public class TestExpectedUsage extends BaseUnitTestCase {
 	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	public void setUp() throws Exception {
-		serviceRegistry = new BasicServiceRegistryImpl(
-				StandardServiceInitiators.LIST,
-				ConnectionProviderBuilder.getConnectionProviderProperties()
-		);
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( ConnectionProviderBuilder.getConnectionProviderProperties() )
+				.buildServiceRegistry();
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBasicUsage() {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) ) {
 			@Override
 			public ConnectionReleaseMode getConnectionReleaseMode() {
 				return ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		};
 
 		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		JournalingTransactionObserver observer = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( observer );
 
 		LogicalConnectionImplementor logicalConnection = transactionCoordinator.getJdbcCoordinator().getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		try {
 			Statement statement = connection.createStatement();
 			statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 			statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			statement.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() ); // after_transaction specified
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : SQLException" );
 		}
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, observer.getBegins() );
 		try {
 			PreparedStatement ps = connection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 			ps = connection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 			connection.prepareStatement( "delete from SANDBOX_JDBC_TST" ).execute();
 			// lets forget to close these...
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 			// and commit the transaction...
 			txn.commit();
 
 			// we should now have:
 			//		1) no resources because of after_transaction release mode
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			//		2) non-physically connected logical connection, again because of after_transaction release mode
 			assertFalse( logicalConnection.isPhysicallyConnected() );
 			//		3) transaction observer callbacks
 			assertEquals( 1, observer.getBeforeCompletions() );
 			assertEquals( 1, observer.getAfterCompletions() );
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : SQLException" );
 		}
 		finally {
 			logicalConnection.close();
 		}
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
index b1c5d57766..26d44c02c4 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/BasicDrivingTest.java
@@ -1,164 +1,165 @@
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
 package org.hibernate.test.transaction.jta;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.internal.jta.JtaTransactionFactory;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.ServiceProxy;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.env.ConnectionProviderBuilder;
 import org.hibernate.testing.jta.TestingJtaBootstrap;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * Testing transaction handling when the JTA transaction facade is the driver.
  *
  * @author Steve Ebersole
  */
 public class BasicDrivingTest extends BaseUnitTestCase {
 	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	@SuppressWarnings( {"unchecked"})
 	public void setUp() throws Exception {
 		Map configValues = new HashMap();
 		configValues.putAll( ConnectionProviderBuilder.getConnectionProviderProperties() );
 		configValues.put( Environment.TRANSACTION_STRATEGY, JtaTransactionFactory.class.getName() );
 		TestingJtaBootstrap.prepare( configValues );
-		serviceRegistry = new BasicServiceRegistryImpl( configValues );
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( configValues ).buildServiceRegistry();
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBasicUsage() throws Throwable {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) );
 
 		TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		JournalingTransactionObserver observer = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( observer );
 
 		LogicalConnectionImplementor logicalConnection = transactionCoordinator.getJdbcCoordinator().getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		try {
 			Statement statement = connection.createStatement();
 			statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 			statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			statement.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertFalse( logicalConnection.isPhysicallyConnected() ); // after_statement specified
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : SQLException" );
 		}
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, observer.getBegins() );
 		assertTrue( txn.isInitiator() );
 		try {
 			PreparedStatement ps = connection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 			ps = connection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 			connection.prepareStatement( "delete from SANDBOX_JDBC_TST" ).execute();
 			// lets forget to close these...
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 
 			// and commit the transaction...
 			txn.commit();
 
 			// we should now have:
 			//		1) no resources because of after_transaction release mode
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			//		2) non-physically connected logical connection, again because of after_transaction release mode
 			assertFalse( logicalConnection.isPhysicallyConnected() );
 			//		3) transaction observer callbacks
 			assertEquals( 1, observer.getBeforeCompletions() );
 			assertEquals( 1, observer.getAfterCompletions() );
 		}
 		catch ( SQLException sqle ) {
 			try {
 				JtaPlatform instance = ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();
 				instance.retrieveTransactionManager().rollback();
 			}
 			catch (Exception ignore) {
 			}
 			fail( "incorrect exception type : SQLException" );
 		}
 		catch (Throwable reThrowable) {
 			try {
 				JtaPlatform instance = ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();
 				instance.retrieveTransactionManager().rollback();
 			}
 			catch (Exception ignore) {
 			}
 			throw reThrowable;
 		}
 		finally {
 			logicalConnection.close();
 		}
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
index 0f0413535d..4756608460 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/transaction/jta/ManagedDrivingTest.java
@@ -1,179 +1,178 @@
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
 package org.hibernate.test.transaction.jta;
 
 import javax.transaction.TransactionManager;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.spi.LogicalConnectionImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.internal.jta.CMTTransactionFactory;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
-import org.hibernate.service.StandardServiceInitiators;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.service.internal.ServiceProxy;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.jta.TestingJtaBootstrap;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.test.common.JournalingTransactionObserver;
 import org.hibernate.test.common.TransactionContextImpl;
 import org.hibernate.test.common.TransactionEnvironmentImpl;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * Testing transaction facade handling when the transaction is being driven by something other than the facade.
  *
  * @author Steve Ebersole
  */
 public class ManagedDrivingTest extends BaseUnitTestCase {
 	private BasicServiceRegistryImpl serviceRegistry;
 
 	@Before
 	@SuppressWarnings( {"unchecked"})
 	public void setUp() throws Exception {
 		Map configValues = new HashMap();
 		TestingJtaBootstrap.prepare( configValues );
-//		configValues.putAll( ConnectionProviderBuilder.getConnectionProviderProperties() );
 		configValues.put( Environment.TRANSACTION_STRATEGY, CMTTransactionFactory.class.getName() );
 
-		serviceRegistry = new BasicServiceRegistryImpl( StandardServiceInitiators.LIST, configValues );
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( configValues ).buildServiceRegistry();
 	}
 
 	@After
 	public void tearDown() throws Exception {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testBasicUsage() throws Throwable {
 		final TransactionContext transactionContext = new TransactionContextImpl( new TransactionEnvironmentImpl( serviceRegistry ) ) {
 			@Override
 			public ConnectionReleaseMode getConnectionReleaseMode() {
 				return ConnectionReleaseMode.AFTER_STATEMENT;
 			}
 		};
 
 		final TransactionCoordinatorImpl transactionCoordinator = new TransactionCoordinatorImpl( null, transactionContext );
 		final JournalingTransactionObserver transactionObserver = new JournalingTransactionObserver();
 		transactionCoordinator.addObserver( transactionObserver );
 
 		final LogicalConnectionImplementor logicalConnection = transactionCoordinator.getJdbcCoordinator().getLogicalConnection();
 		Connection connection = logicalConnection.getShareableConnectionProxy();
 
 		// set up some tables to use
 		try {
 			Statement statement = connection.createStatement();
 			statement.execute( "drop table SANDBOX_JDBC_TST if exists" );
 			statement.execute( "create table SANDBOX_JDBC_TST ( ID integer, NAME varchar(100) )" );
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			statement.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertFalse( logicalConnection.isPhysicallyConnected() ); // after_statement specified
 		}
 		catch ( SQLException sqle ) {
 			fail( "incorrect exception type : SQLException" );
 		}
 
 		JtaPlatform instance = ( (ServiceProxy) serviceRegistry.getService( JtaPlatform.class ) ).getTargetInstance();
 		TransactionManager transactionManager = instance.retrieveTransactionManager();
 
 		// start the cmt
 		transactionManager.begin();
 
 		// ok, now we can get down to it...
 		TransactionImplementor txn = transactionCoordinator.getTransaction();  // same as Session#getTransaction
 		txn.begin();
 		assertEquals( 1, transactionObserver.getBegins() );
 		assertFalse( txn.isInitiator() );
 		connection = logicalConnection.getShareableConnectionProxy();
 		try {
 			PreparedStatement ps = connection.prepareStatement( "insert into SANDBOX_JDBC_TST( ID, NAME ) values ( ?, ? )" );
 			ps.setLong( 1, 1 );
 			ps.setString( 2, "name" );
 			ps.execute();
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			ps.close();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 
 			ps = connection.prepareStatement( "select * from SANDBOX_JDBC_TST" );
 			ps.executeQuery();
 			connection.prepareStatement( "delete from SANDBOX_JDBC_TST" ).execute();
 			// lets forget to close these...
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 
 			// and commit the transaction...
 			txn.commit();
 
 			// since txn is not a driver, nothing should have changed...
 			assertTrue( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertTrue( logicalConnection.isPhysicallyConnected() );
 			assertEquals( 0, transactionObserver.getBeforeCompletions() );
 			assertEquals( 0, transactionObserver.getAfterCompletions() );
 
 			transactionManager.commit();
 			assertFalse( logicalConnection.getResourceRegistry().hasRegisteredResources() );
 			assertFalse( logicalConnection.isPhysicallyConnected() );
 			assertEquals( 1, transactionObserver.getBeforeCompletions() );
 			assertEquals( 1, transactionObserver.getAfterCompletions() );
 		}
 		catch ( SQLException sqle ) {
 			try {
 				transactionManager.rollback();
 			}
 			catch (Exception ignore) {
 			}
 			fail( "incorrect exception type : SQLException" );
 		}
 		catch (Throwable reThrowable) {
 			try {
 				transactionManager.rollback();
 			}
 			catch (Exception ignore) {
 			}
 			throw reThrowable;
 		}
 		finally {
 			logicalConnection.close();
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
index e3446f5a5c..23583b4dac 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/Ejb3Configuration.java
@@ -1,1604 +1,1602 @@
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
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.annotations.reflection.XMLContext;
 import org.hibernate.cfg.beanvalidation.BeanValidationIntegrator;
 import org.hibernate.ejb.connection.InjectedDataSourceConnectionProvider;
-import org.hibernate.ejb.event.JpaEventListenerRegistration;
+import org.hibernate.ejb.event.JpaIntegrator;
 import org.hibernate.ejb.instrument.InterceptFieldClassFileTransformer;
 import org.hibernate.ejb.internal.EntityManagerMessageLogger;
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
+import org.hibernate.integrator.spi.IntegratorService;
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
-import org.hibernate.service.StandardServiceInitiators;
-import org.hibernate.service.internal.BasicServiceRegistryImpl;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.jdbc.connections.internal.DatasourceConnectionProviderImpl;
 
 /**
  * Allow a fine tuned configuration of an EJB 3.0 EntityManagerFactory
  *
  * A Ejb3Configuration object is only guaranteed to create one EntityManagerFactory.
  * Multiple usage of {@link #buildEntityManagerFactory()} is not guaranteed.
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
 
     private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(
 			EntityManagerMessageLogger.class,
 			Ejb3Configuration.class.getName()
 	);
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
 	private PersistenceUnitTransactionType transactionType;
 	private boolean discardOnClose;
 	//made transient and not restored in deserialization on purpose, should no longer be called after restoration
 	private transient ClassLoader overridenClassLoader;
 	private boolean isConfigurationProcessed = false;
 
 
 	public Ejb3Configuration() {
 		cfg = new Configuration();
 		cfg.setEntityNotFoundDelegate( ejb3EntityNotFoundDelegate );
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
 			BeanValidationIntegrator.validateFactory( validationFactory );
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
 				BeanValidationIntegrator.validateFactory( validationFactory );
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
-		return buildEntityManagerFactory( new BasicServiceRegistryImpl( cfg.getProperties() ) );
+		return buildEntityManagerFactory( new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry() );
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
-			// todo : temporary -> HHH-5562
-			serviceRegistry.getService( StandardServiceInitiators.EventListenerRegistrationService.class )
-					.attachEventListenerRegistration( new JpaEventListenerRegistration() );
+			serviceRegistry.getService( IntegratorService.class ).addIntegrator( new JpaIntegrator() );
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
         LOG.debugf( "Returning a Reference to the Ejb3Configuration" );
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
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaEventListenerRegistration.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaIntegrator.java
similarity index 87%
rename from hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaEventListenerRegistration.java
rename to hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaIntegrator.java
index f43b693782..abe9ce611e 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaEventListenerRegistration.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/event/JpaIntegrator.java
@@ -1,173 +1,175 @@
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
 package org.hibernate.ejb.event;
 
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.ejb.AvailableSettings;
-import org.hibernate.event.EventListenerRegistration;
+import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.event.EventType;
+import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.secure.JACCPreDeleteEventListener;
 import org.hibernate.secure.JACCPreInsertEventListener;
 import org.hibernate.secure.JACCPreLoadEventListener;
 import org.hibernate.secure.JACCPreUpdateEventListener;
 import org.hibernate.secure.JACCSecurityListener;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
-import org.hibernate.service.event.spi.DuplicationStrategy;
-import org.hibernate.service.event.spi.EventListenerGroup;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.DuplicationStrategy;
+import org.hibernate.event.service.spi.EventListenerGroup;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
+import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 /**
  * Prepare the HEM-specific event listeners.
- *
- * @todo : make this into Integrator per HHH-5562 ?? 
  * 
  * @author Steve Ebersole
  */
-public class JpaEventListenerRegistration implements EventListenerRegistration {
+public class JpaIntegrator implements Integrator {
 	private static final DuplicationStrategy JPA_DUPLICATION_STRATEGY = new DuplicationStrategy() {
 		@Override
 		public boolean areMatch(Object listener, Object original) {
 			return listener.getClass().equals( original.getClass() ) &&
 					HibernateEntityManagerEventListener.class.isInstance( original );
 		}
 
 		@Override
 		public Action getAction() {
 			return Action.KEEP_ORIGINAL;
 		}
 	};
 
 	private static final DuplicationStrategy JACC_DUPLICATION_STRATEGY = new DuplicationStrategy() {
 		@Override
 		public boolean areMatch(Object listener, Object original) {
 			return listener.getClass().equals( original.getClass() ) &&
 					JACCSecurityListener.class.isInstance( original );
 		}
 
 		@Override
 		public Action getAction() {
 			return Action.KEEP_ORIGINAL;
 		}
 	};
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
-	public void apply(
-			EventListenerRegistry eventListenerRegistry,
+	public void integrate(
 			Configuration configuration,
-			Map<?, ?> configValues,
-			ServiceRegistryImplementor serviceRegistry) {
-		boolean isSecurityEnabled = configValues.containsKey( AvailableSettings.JACC_ENABLED );
+			SessionFactoryImplementor sessionFactory,
+			SessionFactoryServiceRegistry serviceRegistry) {
+		final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
+
+		boolean isSecurityEnabled = configuration.getProperties().containsKey( AvailableSettings.JACC_ENABLED );
 
 		eventListenerRegistry.addDuplicationStrategy( JPA_DUPLICATION_STRATEGY );
 		eventListenerRegistry.addDuplicationStrategy( JACC_DUPLICATION_STRATEGY );
 
 		// op listeners
 		eventListenerRegistry.setListeners( EventType.AUTO_FLUSH, EJB3AutoFlushEventListener.INSTANCE );
 		eventListenerRegistry.setListeners( EventType.DELETE, new EJB3DeleteEventListener() );
 		eventListenerRegistry.setListeners( EventType.FLUSH_ENTITY, new EJB3FlushEntityEventListener() );
 		eventListenerRegistry.setListeners( EventType.FLUSH, EJB3FlushEventListener.INSTANCE );
 		eventListenerRegistry.setListeners( EventType.MERGE, new EJB3MergeEventListener() );
 		eventListenerRegistry.setListeners( EventType.PERSIST, new EJB3PersistEventListener() );
 		eventListenerRegistry.setListeners( EventType.PERSIST_ONFLUSH, new EJB3PersistOnFlushEventListener() );
 		eventListenerRegistry.setListeners( EventType.SAVE, new EJB3SaveEventListener() );
 		eventListenerRegistry.setListeners( EventType.SAVE_UPDATE, new EJB3SaveOrUpdateEventListener() );
 
 		// pre op listeners
 		if ( isSecurityEnabled ) {
 			eventListenerRegistry.prependListeners( EventType.PRE_DELETE, new JACCPreDeleteEventListener() );
 			eventListenerRegistry.prependListeners( EventType.PRE_INSERT, new JACCPreInsertEventListener() );
 			eventListenerRegistry.prependListeners( EventType.PRE_UPDATE, new JACCPreUpdateEventListener() );
 			eventListenerRegistry.prependListeners( EventType.PRE_LOAD, new JACCPreLoadEventListener() );
 		}
 
 		// post op listeners
 		eventListenerRegistry.prependListeners( EventType.POST_DELETE, new EJB3PostDeleteEventListener() );
 		eventListenerRegistry.prependListeners( EventType.POST_INSERT, new EJB3PostInsertEventListener() );
 		eventListenerRegistry.prependListeners( EventType.POST_LOAD, new EJB3PostLoadEventListener() );
 		eventListenerRegistry.prependListeners( EventType.POST_UPDATE, new EJB3PostUpdateEventListener() );
 
-		for ( Map.Entry<?,?> entry : configValues.entrySet() ) {
+		for ( Map.Entry<?,?> entry : configuration.getProperties().entrySet() ) {
 			if ( ! String.class.isInstance( entry.getKey() ) ) {
 				continue;
 			}
 			final String propertyName = (String) entry.getKey();
 			if ( ! propertyName.startsWith( AvailableSettings.EVENT_LISTENER_PREFIX ) ) {
 				continue;
 			}
 			final String eventTypeName = propertyName.substring( AvailableSettings.EVENT_LISTENER_PREFIX.length() + 1 );
 			final EventType eventType = EventType.resolveEventTypeByName( eventTypeName );
 			final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
-			eventListenerGroup.clear();
 			for ( String listenerImpl : ( (String) entry.getValue() ).split( " ," ) ) {
 				eventListenerGroup.appendListener( instantiate( listenerImpl, serviceRegistry ) );
 			}
 		}
 
-		// todo : we may need to account for callback handlers previously set (shared across EMFs)
-
 		final EntityCallbackHandler callbackHandler = new EntityCallbackHandler();
 		Iterator classes = configuration.getClassMappings();
 		ReflectionManager reflectionManager = configuration.getReflectionManager();
 		while ( classes.hasNext() ) {
 			PersistentClass clazz = (PersistentClass) classes.next();
 			if ( clazz.getClassName() == null ) {
 				//we can have non java class persisted by hibernate
 				continue;
 			}
 			try {
 				callbackHandler.add( reflectionManager.classForName( clazz.getClassName(), this.getClass() ), reflectionManager );
 			}
 			catch (ClassNotFoundException e) {
 				throw new MappingException( "entity class not found: " + clazz.getNodeName(), e );
 			}
 		}
 
 		for ( EventType eventType : EventType.values() ) {
 			final EventListenerGroup eventListenerGroup = eventListenerRegistry.getEventListenerGroup( eventType );
 			for ( Object listener : eventListenerGroup.listeners() ) {
 				if ( CallbackHandlerConsumer.class.isInstance( listener ) ) {
 					( (CallbackHandlerConsumer) listener ).setCallbackHandler( callbackHandler );
 				}
 			}
 		}
 	}
 
+	@Override
+	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
+	}
+
 	private Object instantiate(String listenerImpl, ServiceRegistryImplementor serviceRegistry) {
 		try {
 			return serviceRegistry.getService( ClassLoaderService.class ).classForName( listenerImpl ).newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "Could not instantiate requested listener [" + listenerImpl + "]", e );
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
index fcaed74ce6..b29b63a850 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/BaseEntityManagerFunctionalTestCase.java
@@ -1,286 +1,287 @@
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
 package org.hibernate.ejb.test;
 
 import javax.persistence.EntityManager;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.Persistence;
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.ejb.AvailableSettings;
 import org.hibernate.ejb.Ejb3Configuration;
 import org.hibernate.internal.util.config.ConfigurationHelper;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * A base class for all ejb tests.
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 public abstract class BaseEntityManagerFunctionalTestCase extends BaseUnitTestCase {
 	private static final Logger log = Logger.getLogger( BaseEntityManagerFunctionalTestCase.class );
 
 	// IMPL NOTE : Here we use @Before and @After (instead of @BeforeClassOnce and @AfterClassOnce like we do in
 	// BaseCoreFunctionalTestCase) because the old HEM test methodology was to create an EMF for each test method.
 
 	private static final Dialect dialect = Dialect.getDialect();
 
 	private Ejb3Configuration ejb3Configuration;
 	private BasicServiceRegistryImpl serviceRegistry;
 	private EntityManagerFactory entityManagerFactory;
 
 	private EntityManager em;
 	private ArrayList<EntityManager> isolatedEms = new ArrayList<EntityManager>();
 
 	protected Dialect getDialect() {
 		return dialect;
 	}
 
 	protected EntityManagerFactory entityManagerFactory() {
 		return entityManagerFactory;
 	}
 
 	protected BasicServiceRegistryImpl serviceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Before
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void buildEntityManagerFactory() throws Exception {
 		log.trace( "Building session factory" );
 		ejb3Configuration = buildConfiguration();
 		ejb3Configuration.configure( getConfig() );
 		afterConfigurationBuilt( ejb3Configuration );
 		serviceRegistry = buildServiceRegistry( ejb3Configuration.getHibernateConfiguration() );
 		applyServices( serviceRegistry );
 		entityManagerFactory = ejb3Configuration.buildEntityManagerFactory( serviceRegistry );
 		afterEntityManagerFactoryBuilt();
 	}
 
 	protected Ejb3Configuration buildConfiguration() {
 		Ejb3Configuration ejb3Cfg = constructConfiguration();
 		addMappings( ejb3Cfg.getHibernateConfiguration() );
 		return ejb3Cfg;
 	}
 
 	protected Ejb3Configuration constructConfiguration() {
 		Ejb3Configuration ejb3Configuration = new Ejb3Configuration();
 		if ( createSchema() ) {
 			ejb3Configuration.getHibernateConfiguration().setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		}
 		ejb3Configuration
 				.getHibernateConfiguration()
 				.setProperty( Configuration.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 		ejb3Configuration
 				.getHibernateConfiguration()
 				.setProperty( Environment.DIALECT, getDialect().getClass().getName() );
 		return ejb3Configuration;
 	}
 
 	protected void addMappings(Configuration configuration) {
 		String[] mappings = getMappings();
 		if ( mappings != null ) {
 			for ( String mapping : mappings ) {
 				configuration.addResource( mapping, getClass().getClassLoader() );
 			}
 		}
 	}
 
 	protected static final String[] NO_MAPPINGS = new String[0];
 
 	protected String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	protected Map getConfig() {
 		Map<Object, Object> config = loadProperties();
 		ArrayList<Class> classes = new ArrayList<Class>();
 
 		classes.addAll( Arrays.asList( getAnnotatedClasses() ) );
 		config.put( AvailableSettings.LOADED_CLASSES, classes );
 		for ( Map.Entry<Class, String> entry : getCachedClasses().entrySet() ) {
 			config.put( AvailableSettings.CLASS_CACHE_PREFIX + "." + entry.getKey().getName(), entry.getValue() );
 		}
 		for ( Map.Entry<String, String> entry : getCachedCollections().entrySet() ) {
 			config.put( AvailableSettings.COLLECTION_CACHE_PREFIX + "." + entry.getKey(), entry.getValue() );
 		}
 		if ( getEjb3DD().length > 0 ) {
 			ArrayList<String> dds = new ArrayList<String>();
 			dds.addAll( Arrays.asList( getEjb3DD() ) );
 			config.put( AvailableSettings.XML_FILE_NAMES, dds );
 		}
 
 		addConfigOptions( config );
 		return config;
 	}
 
 	protected void addConfigOptions(Map options) {
 	}
 
 	private Properties loadProperties() {
 		Properties props = new Properties();
 		InputStream stream = Persistence.class.getResourceAsStream( "/hibernate.properties" );
 		if ( stream != null ) {
 			try {
 				props.load( stream );
 			}
 			catch ( Exception e ) {
 				throw new RuntimeException( "could not load hibernate.properties" );
 			}
 			finally {
 				try {
 					stream.close();
 				}
 				catch ( IOException ignored ) {
 				}
 			}
 		}
 		return props;
 	}
 
 	protected static final Class<?>[] NO_CLASSES = new Class[0];
 
 	protected Class<?>[] getAnnotatedClasses() {
 		return NO_CLASSES;
 	}
 
 	public Map<Class, String> getCachedClasses() {
 		return new HashMap<Class, String>();
 	}
 
 	public Map<String, String> getCachedCollections() {
 		return new HashMap<String, String>();
 	}
 
 	public String[] getEjb3DD() {
 		return new String[] { };
 	}
 
 	@SuppressWarnings( {"UnusedParameters"})
 	protected void afterConfigurationBuilt(Ejb3Configuration ejb3Configuration) {
 	}
 
 	protected BasicServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
 		Properties properties = new Properties();
 		properties.putAll( configuration.getProperties() );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return new BasicServiceRegistryImpl( properties );
+		return (BasicServiceRegistryImpl) new ServiceRegistryBuilder( properties ).buildServiceRegistry();
 	}
 
 	@SuppressWarnings( {"UnusedParameters"})
 	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 	}
 
 	protected void afterEntityManagerFactoryBuilt() {
 	}
 
 	protected boolean createSchema() {
 		return true;
 	}
 
 
 	@After
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void releaseResources() {
 		releaseUnclosedEntityManagers();
 
 		if ( entityManagerFactory != null ) {
 			entityManagerFactory.close();
 		}
 
 		if ( serviceRegistry != null ) {
 			serviceRegistry.destroy();
 		}
 	}
 
 	private void releaseUnclosedEntityManagers() {
 		releaseUnclosedEntityManager( this.em );
 
 		for ( EntityManager isolatedEm : isolatedEms ) {
 			releaseUnclosedEntityManager( isolatedEm );
 		}
 	}
 
 	private void releaseUnclosedEntityManager(EntityManager em) {
 		if ( em == null ) {
 			return;
 		}
 		if ( em.getTransaction().isActive() ) {
 			em.getTransaction().rollback();
             log.warn("You left an open transaction! Fix your test case. For now, we are closing it for you.");
 		}
 		if ( em.isOpen() ) {
 			// as we open an EM before the test runs, it will still be open if the test uses a custom EM.
 			// or, the person may have forgotten to close. So, do not raise a "fail", but log the fact.
 			em.close();
             log.warn("The EntityManager is not closed. Closing it.");
 		}
 	}
 
 	protected EntityManager getOrCreateEntityManager() {
 		if ( em == null || !em.isOpen() ) {
 			em = entityManagerFactory.createEntityManager();
 		}
 		return em;
 	}
 
 	protected EntityManager createIsolatedEntityManager() {
 		EntityManager isolatedEm = entityManagerFactory.createEntityManager();
 		isolatedEms.add( isolatedEm );
 		return isolatedEm;
 	}
 
 	protected EntityManager createIsolatedEntityManager(Map props) {
 		EntityManager isolatedEm = entityManagerFactory.createEntityManager(props);
 		isolatedEms.add( isolatedEm );
 		return isolatedEm;
 	}
 
 	protected EntityManager createEntityManager(Map properties) {
 		// always reopen a new EM and close the existing one
 		if ( em != null && em.isOpen() ) {
 			em.close();
 		}
 		em = entityManagerFactory.createEntityManager( properties );
 		return em;
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/PackagedEntityManagerTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/PackagedEntityManagerTest.java
index 34788c7ac2..4d440aacfb 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/PackagedEntityManagerTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/packaging/PackagedEntityManagerTest.java
@@ -1,487 +1,487 @@
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
 import org.hibernate.event.EventType;
 import org.hibernate.internal.util.ConfigHelper;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerRegistry;
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
 		EventListenerRegistry listenerRegistry = em.unwrap( SessionImplementor.class ).getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class );
 		assertEquals(
 				"Explicit pre-insert event through hibernate.ejb.event.pre-insert does not work",
 				listenerRegistry.getEventListenerGroup( EventType.PRE_INSERT ).count(),
 				listenerRegistry.getEventListenerGroup( EventType.PRE_UPDATE ).count() + 1
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
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/AuditReaderFactory.java b/hibernate-envers/src/main/java/org/hibernate/envers/AuditReaderFactory.java
index 8faa0a7ae0..21a9190620 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/AuditReaderFactory.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/AuditReaderFactory.java
@@ -1,97 +1,97 @@
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
 package org.hibernate.envers;
 
 import javax.persistence.EntityManager;
 
 import org.hibernate.Session;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.envers.event.EnversListener;
 import org.hibernate.envers.exception.AuditException;
 import org.hibernate.envers.reader.AuditReaderImpl;
 import org.hibernate.event.EventType;
 import org.hibernate.event.PostInsertEventListener;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public class AuditReaderFactory {
     private AuditReaderFactory() { }
 
     /**
      * Create an audit reader associated with an open session.
      * @param session An open session.
      * @return An audit reader associated with the given sesison. It shouldn't be used
      * after the session is closed.
      * @throws AuditException When the given required listeners aren't installed.
      */
     public static AuditReader get(Session session) throws AuditException {
         SessionImplementor sessionImpl;
 		if (!(session instanceof SessionImplementor)) {
 			sessionImpl = (SessionImplementor) session.getSessionFactory().getCurrentSession();
 		} else {
 			sessionImpl = (SessionImplementor) session;
 		}
 
 		// todo : I wonder if there is a better means to do this via "named lookup" based on the session factory name/uuid
 		final EventListenerRegistry listenerRegistry = sessionImpl
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class );
 
 		for ( PostInsertEventListener listener : listenerRegistry.getEventListenerGroup( EventType.POST_INSERT ).listeners() ) {
 			if ( listener instanceof EnversListener ) {
 				// todo : slightly different from original code in that I am not checking the other listener groups...
 				return new AuditReaderImpl(
 						( (EnversListener) listener ).getAuditConfiguration(),
 						session,
 						sessionImpl
 				);
 			}
 		}
 
         throw new AuditException( "Envers listeners were not properly registered" );
     }
 
     /**
      * Create an audit reader associated with an open entity manager.
      * @param entityManager An open entity manager.
      * @return An audit reader associated with the given entity manager. It shouldn't be used
      * after the entity manager is closed.
      * @throws AuditException When the given entity manager is not based on Hibernate, or if the required
      * listeners aren't installed.
      */
     public static AuditReader get(EntityManager entityManager) throws AuditException {
         if (entityManager.getDelegate() instanceof Session) {
             return get((Session) entityManager.getDelegate());
         }
 
         if (entityManager.getDelegate() instanceof EntityManager) {
             return get((EntityManager) entityManager.getDelegate());
         }
 
         throw new AuditException("Hibernate EntityManager not present!");
     }
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversIntegrator.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversIntegrator.java
index f83f57e40c..58fcc4714a 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversIntegrator.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversIntegrator.java
@@ -1,78 +1,78 @@
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
 package org.hibernate.envers.event;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.event.EventType;
-import org.hibernate.spi.Integrator;
+import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.service.event.spi.EventListenerRegistry;
+import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 /**
  * Provides integration for Envers into Hibernate, which mainly means registering the proper event listeners.
  * 
  * @author Steve Ebersole
  */
 public class EnversIntegrator implements Integrator {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, EnversIntegrator.class.getName() );
 
 	public static final String AUTO_REGISTER = "hibernate.listeners.envers.autoRegister";
 
 	@Override
 	public void integrate(
 			Configuration configuration,
 			SessionFactoryImplementor sessionFactory,
 			SessionFactoryServiceRegistry serviceRegistry) {
 		final boolean autoRegister = ConfigurationHelper.getBoolean( AUTO_REGISTER, configuration.getProperties(), true );
 		if ( !autoRegister ) {
 			LOG.debug( "Skipping Envers listener auto registration" );
 			return;
 		}
 
 		EventListenerRegistry listenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 		listenerRegistry.addDuplicationStrategy( EnversListenerDuplicationStrategy.INSTANCE );
 
 		final AuditConfiguration enversConfiguration = AuditConfiguration.getFor( configuration );
 
         if (enversConfiguration.getEntCfg().hasAuditedEntities()) {
 		    listenerRegistry.appendListeners( EventType.POST_DELETE, new EnversPostDeleteEventListenerImpl( enversConfiguration ) );
 		    listenerRegistry.appendListeners( EventType.POST_INSERT, new EnversPostInsertEventListenerImpl( enversConfiguration ) );
 		    listenerRegistry.appendListeners( EventType.POST_UPDATE, new EnversPostUpdateEventListenerImpl( enversConfiguration ) );
 		    listenerRegistry.appendListeners( EventType.POST_COLLECTION_RECREATE, new EnversPostCollectionRecreateEventListenerImpl( enversConfiguration ) );
 		    listenerRegistry.appendListeners( EventType.PRE_COLLECTION_REMOVE, new EnversPreCollectionRemoveEventListenerImpl( enversConfiguration ) );
 		    listenerRegistry.appendListeners( EventType.PRE_COLLECTION_UPDATE, new EnversPreCollectionUpdateEventListenerImpl( enversConfiguration ) );
         }
 	}
 
 	@Override
 	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 		// nothing to do afaik
 	}
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversListenerDuplicationStrategy.java b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversListenerDuplicationStrategy.java
index 6791760298..ae46616d89 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversListenerDuplicationStrategy.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/EnversListenerDuplicationStrategy.java
@@ -1,45 +1,45 @@
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
 package org.hibernate.envers.event;
 
-import org.hibernate.service.event.spi.DuplicationStrategy;
+import org.hibernate.event.service.spi.DuplicationStrategy;
 
 /**
  * Event listener duplication strategy for envers
  *
  * @author Steve Ebersole
  */
 public class EnversListenerDuplicationStrategy implements DuplicationStrategy {
 	public static final EnversListenerDuplicationStrategy INSTANCE = new EnversListenerDuplicationStrategy();
 
 	@Override
 	public boolean areMatch(Object listener, Object original) {
 		return listener.getClass().equals( original ) && EnversListener.class.isInstance( listener );
 	}
 
 	@Override
 	public Action getAction() {
 		return Action.KEEP_ORIGINAL;
 	}
 }
diff --git a/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java b/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java
index b6765777ef..4bde4e66a4 100644
--- a/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java
+++ b/hibernate-envers/src/test/java/org/hibernate/envers/test/AbstractEntityTest.java
@@ -1,125 +1,126 @@
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
 package org.hibernate.envers.test;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.ejb.Ejb3Configuration;
 import org.hibernate.envers.AuditReader;
 import org.hibernate.envers.AuditReaderFactory;
 import org.hibernate.envers.event.EnversIntegrator;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.testing.AfterClassOnce;
 import org.hibernate.testing.BeforeClassOnce;
 import org.junit.Before;
 
 import javax.persistence.EntityManager;
 import javax.persistence.EntityManagerFactory;
 import java.io.IOException;
 import java.util.Properties;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public abstract class AbstractEntityTest extends AbstractEnversTest {
     private EntityManagerFactory emf;
     private EntityManager entityManager;
     private AuditReader auditReader;
     private Ejb3Configuration cfg;
 	private BasicServiceRegistryImpl serviceRegistry;
     private boolean audited;
 
     public abstract void configure(Ejb3Configuration cfg);
 
     private void closeEntityManager() {
         if (entityManager != null) {
             entityManager.close();
             entityManager = null;
         }
     }
 
     @Before
     public void newEntityManager() {
         closeEntityManager();
         
         entityManager = emf.createEntityManager();
 
         if (audited) {
             auditReader = AuditReaderFactory.get(entityManager);
         }
     }
 
     @BeforeClassOnce
     public void init() throws IOException {
         init(true, getAuditStrategy());
     }
 
     protected void init(boolean audited, String auditStrategy) throws IOException {
         this.audited = audited;
 
         cfg = new Ejb3Configuration();
         Properties configValues = cfg.getProperties();
         if (!audited) {
 			configValues.setProperty(EnversIntegrator.AUTO_REGISTER, "false");
         }
 
         configValues.setProperty(Environment.HBM2DDL_AUTO, "create-drop");
         configValues.setProperty(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
         configValues.setProperty(Environment.DRIVER, "org.h2.Driver");
         configValues.setProperty(Environment.USER, "sa");
 
         // Separate database for each test class
         configValues.setProperty(Environment.URL, "jdbc:h2:mem:" + this.getClass().getName() + ";DB_CLOSE_DELAY=-1");
 
         if (auditStrategy != null && !"".equals(auditStrategy)) {
             cfg.setProperty("org.hibernate.envers.audit_strategy", auditStrategy);
         }
 
         configure( cfg );
 
-		serviceRegistry = new BasicServiceRegistryImpl(configValues);
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( configValues ).buildServiceRegistry();
 
         emf = cfg.buildEntityManagerFactory( serviceRegistry );
 
         newEntityManager();
     }
 
     @AfterClassOnce
     public void close() {
         closeEntityManager();
         emf.close();
 		serviceRegistry.destroy();
     }
 
     public EntityManager getEntityManager() {
         return entityManager;
     }
 
     public AuditReader getAuditReader() {
         return auditReader;
     }
 
     public Ejb3Configuration getCfg() {
         return cfg;
     }
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
index d18d556b2c..8ee94d5db0 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/AbstractGeneralDataRegionTestCase.java
@@ -1,226 +1,228 @@
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
 
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 import org.jboss.logging.Logger;
 
 import org.hibernate.cache.GeneralDataRegion;
 import org.hibernate.cache.QueryResultsRegion;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cfg.Configuration;
+import org.hibernate.service.ServiceRegistry;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.Test;
 
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNull;
 
 /**
  * Base class for tests of QueryResultsRegion and TimestampsRegion.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public abstract class AbstractGeneralDataRegionTestCase extends AbstractRegionImplTestCase {
 	private static final Logger log = Logger.getLogger( AbstractGeneralDataRegionTestCase.class );
 
 	protected static final String KEY = "Key";
 
 	protected static final String VALUE1 = "value1";
 	protected static final String VALUE2 = "value2";
 
 	protected Configuration createConfiguration() {
 		return CacheTestUtil.buildConfiguration( "test", InfinispanRegionFactory.class, false, true );
 	}
 
 	@Override
 	protected void putInRegion(Region region, Object key, Object value) {
 		((GeneralDataRegion) region).put( key, value );
 	}
 
 	@Override
 	protected void removeFromRegion(Region region, Object key) {
 		((GeneralDataRegion) region).evict( key );
 	}
 
 	@Test
 	public void testEvict() throws Exception {
 		evictOrRemoveTest();
 	}
 
 	private void evictOrRemoveTest() throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new BasicServiceRegistryImpl( cfg.getProperties() ),
+				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
 				cfg,
 				getCacheTestSupport()
 		);
 		CacheAdapter localCache = getInfinispanCache( regionFactory );
 		boolean invalidation = localCache.isClusteredInvalidation();
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ), cfg.getProperties(), null
 		);
 
 		cfg = createConfiguration();
 		regionFactory = CacheTestUtil.startRegionFactory(
-				new BasicServiceRegistryImpl( cfg.getProperties() ),
+				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
 				cfg,
 				getCacheTestSupport()
 		);
 
 		GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties(),
 				null
 		);
 
 		assertNull( "local is clean", localRegion.get( KEY ) );
 		assertNull( "remote is clean", remoteRegion.get( KEY ) );
 
 		localRegion.put( KEY, VALUE1 );
 		assertEquals( VALUE1, localRegion.get( KEY ) );
 
 		// allow async propagation
 		sleep( 250 );
 		Object expected = invalidation ? null : VALUE1;
 		assertEquals( expected, remoteRegion.get( KEY ) );
 
 		localRegion.evict( KEY );
 
 		// allow async propagation
 		sleep( 250 );
 		assertEquals( null, localRegion.get( KEY ) );
 		assertEquals( null, remoteRegion.get( KEY ) );
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
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new BasicServiceRegistryImpl( cfg.getProperties() ),
+				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
 				cfg,
 				getCacheTestSupport()
 		);
 		CacheAdapter localCache = getInfinispanCache( regionFactory );
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		GeneralDataRegion localRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties(),
 				null
 		);
 
 		cfg = createConfiguration();
 		regionFactory = CacheTestUtil.startRegionFactory(
-				new BasicServiceRegistryImpl( cfg.getProperties() ),
+				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
 				cfg,
 				getCacheTestSupport()
 		);
 		CacheAdapter remoteCache = getInfinispanCache( regionFactory );
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		GeneralDataRegion remoteRegion = (GeneralDataRegion) createRegion(
 				regionFactory,
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties(),
 				null
 		);
 
 		Set keys = localCache.keySet();
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( keys ) );
 
 		keys = remoteCache.keySet();
 		assertEquals( "No valid children in " + keys, 0, getValidKeyCount( keys ) );
 
 		assertNull( "local is clean", localRegion.get( KEY ) );
 		assertNull( "remote is clean", remoteRegion.get( KEY ) );
 
 		localRegion.put( KEY, VALUE1 );
 		assertEquals( VALUE1, localRegion.get( KEY ) );
 
 		// Allow async propagation
 		sleep( 250 );
 
 		remoteRegion.put( KEY, VALUE1 );
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
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
index cf8e57de0b..c8ad070713 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/NodeEnvironment.java
@@ -1,138 +1,139 @@
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
 package org.hibernate.test.cache.infinispan;
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.collection.CollectionRegionImpl;
 import org.hibernate.cache.infinispan.entity.EntityRegionImpl;
 import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.hibernate.cfg.Configuration;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 /**
  * Defines the environment for a node.
  *
  * @author Steve Ebersole
  */
 public class NodeEnvironment {
 	private final Configuration configuration;
 
 	private BasicServiceRegistryImpl serviceRegistry;
 	private InfinispanRegionFactory regionFactory;
 
 	private Map<String,EntityRegionImpl> entityRegionMap;
 	private Map<String,CollectionRegionImpl> collectionRegionMap;
 
 	public NodeEnvironment(Configuration configuration) {
 		this.configuration = configuration;
 	}
 
 	public Configuration getConfiguration() {
 		return configuration;
 	}
 
 	public BasicServiceRegistryImpl getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	public EntityRegionImpl getEntityRegion(String name, CacheDataDescription cacheDataDescription) {
 		if ( entityRegionMap == null ) {
 			entityRegionMap = new HashMap<String, EntityRegionImpl>();
 			return buildAndStoreEntityRegion( name, cacheDataDescription );
 		}
 		EntityRegionImpl region = entityRegionMap.get( name );
 		if ( region == null ) {
 			region = buildAndStoreEntityRegion( name, cacheDataDescription );
 		}
 		return region;
 	}
 
 	private EntityRegionImpl buildAndStoreEntityRegion(String name, CacheDataDescription cacheDataDescription) {
 		EntityRegionImpl region = (EntityRegionImpl) regionFactory.buildEntityRegion(
 				name,
 				configuration.getProperties(),
 				cacheDataDescription
 		);
 		entityRegionMap.put( name, region );
 		return region;
 	}
 
 	public CollectionRegionImpl getCollectionRegion(String name, CacheDataDescription cacheDataDescription) {
 		if ( collectionRegionMap == null ) {
 			collectionRegionMap = new HashMap<String, CollectionRegionImpl>();
 			return buildAndStoreCollectionRegion( name, cacheDataDescription );
 		}
 		CollectionRegionImpl region = collectionRegionMap.get( name );
 		if ( region == null ) {
 			region = buildAndStoreCollectionRegion( name, cacheDataDescription );
 			collectionRegionMap.put( name, region );
 		}
 		return region;
 	}
 
 	private CollectionRegionImpl buildAndStoreCollectionRegion(String name, CacheDataDescription cacheDataDescription) {
 		CollectionRegionImpl region;
 		region = (CollectionRegionImpl) regionFactory.buildCollectionRegion(
 				name,
 				configuration.getProperties(),
 				cacheDataDescription
 		);
 		return region;
 	}
 
 	public void prepare() throws Exception {
-		serviceRegistry = new BasicServiceRegistryImpl( configuration.getProperties() );
+		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder( configuration.getProperties() ).buildServiceRegistry();
 		regionFactory = CacheTestUtil.startRegionFactory( serviceRegistry, configuration );
 	}
 
 	public void release() throws Exception {
 		if ( entityRegionMap != null ) {
 			for ( EntityRegionImpl region : entityRegionMap.values() ) {
 				region.getCacheAdapter().withFlags( FlagAdapter.CACHE_MODE_LOCAL ).clear();
 				region.getCacheAdapter().stop();
 			}
 			entityRegionMap.clear();
 		}
 		if ( collectionRegionMap != null ) {
 			for ( CollectionRegionImpl collectionRegion : collectionRegionMap.values() ) {
 				collectionRegion.getCacheAdapter().withFlags( FlagAdapter.CACHE_MODE_LOCAL ).clear();
 				collectionRegion.getCacheAdapter().stop();
 			}
 			collectionRegionMap.clear();
 		}
 		if ( regionFactory != null ) {
 // Currently the RegionFactory is shutdown by its registration with the CacheTestSetup from CacheTestUtil when built
 			regionFactory.stop();
 		}
 		if ( serviceRegistry != null ) {
 			serviceRegistry.destroy();
 		}
 	}
 }
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
index dc8a56ef8f..9defaf1502 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/query/QueryRegionImplTestCase.java
@@ -1,334 +1,335 @@
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
 
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.CacheEntryVisitedEvent;
 import org.infinispan.transaction.tm.BatchModeTransactionManager;
 import org.infinispan.util.concurrent.IsolationLevel;
 import org.jboss.logging.Logger;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.QueryResultsRegion;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.StandardQueryCache;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.cfg.Configuration;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import junit.framework.AssertionFailedError;
 
 import org.hibernate.test.cache.infinispan.AbstractGeneralDataRegionTestCase;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests of QueryResultRegionImpl.
  *
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class QueryRegionImplTestCase extends AbstractGeneralDataRegionTestCase {
 	private static final Logger log = Logger.getLogger( QueryRegionImplTestCase.class );
 
 	@Override
 	protected Region createRegion(
 			InfinispanRegionFactory regionFactory,
 			String regionName,
 			Properties properties,
 			CacheDataDescription cdd) {
 		return regionFactory.buildQueryResultsRegion( regionName, properties );
 	}
 
 	@Override
 	protected String getStandardRegionName(String regionPrefix) {
 		return regionPrefix + "/" + StandardQueryCache.class.getName();
 	}
 
 	@Override
 	protected CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory) {
 		return CacheAdapterImpl.newInstance( regionFactory.getCacheManager().getCache( "local-query" ) );
 	}
 
 	@Override
 	protected Configuration createConfiguration() {
 		return CacheTestUtil.buildCustomQueryCacheConfiguration( "test", "replicated-query" );
 	}
 
 	private void putDoesNotBlockGetTest() throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new BasicServiceRegistryImpl( cfg.getProperties() ),
+				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
 				cfg,
 				getCacheTestSupport()
 		);
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		final QueryResultsRegion region = regionFactory.buildQueryResultsRegion(
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties()
 		);
 
 		region.put( KEY, VALUE1 );
 		assertEquals( VALUE1, region.get( KEY ) );
 
 		final CountDownLatch readerLatch = new CountDownLatch( 1 );
 		final CountDownLatch writerLatch = new CountDownLatch( 1 );
 		final CountDownLatch completionLatch = new CountDownLatch( 1 );
 		final ExceptionHolder holder = new ExceptionHolder();
 
 		Thread reader = new Thread() {
 			@Override
 			public void run() {
 				try {
 					BatchModeTransactionManager.getInstance().begin();
 					log.debug( "Transaction began, get value for key" );
 					assertTrue( VALUE2.equals( region.get( KEY ) ) == false );
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (AssertionFailedError e) {
 					holder.a1 = e;
 					rollback();
 				}
 				catch (Exception e) {
 					holder.e1 = e;
 					rollback();
 				}
 				finally {
 					readerLatch.countDown();
 				}
 			}
 		};
 
 		Thread writer = new Thread() {
 			@Override
 			public void run() {
 				try {
 					BatchModeTransactionManager.getInstance().begin();
 					log.debug( "Put value2" );
 					region.put( KEY, VALUE2 );
 					log.debug( "Put finished for value2, await writer latch" );
 					writerLatch.await();
 					log.debug( "Writer latch finished" );
 					BatchModeTransactionManager.getInstance().commit();
 					log.debug( "Transaction committed" );
 				}
 				catch (Exception e) {
 					holder.e2 = e;
 					rollback();
 				}
 				finally {
 					completionLatch.countDown();
 				}
 			}
 		};
 
 		reader.setDaemon( true );
 		writer.setDaemon( true );
 
 		writer.start();
 		assertFalse( "Writer is blocking", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 
 		// Start the reader
 		reader.start();
 		assertTrue( "Reader finished promptly", readerLatch.await( 1000000000, TimeUnit.MILLISECONDS ) );
 
 		writerLatch.countDown();
 		assertTrue( "Reader finished promptly", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 
 		assertEquals( VALUE2, region.get( KEY ) );
 
 		if ( holder.a1 != null ) {
 			throw holder.a1;
 		}
 		else if ( holder.a2 != null ) {
 			throw holder.a2;
 		}
 
 		assertEquals( "writer saw no exceptions", null, holder.e1 );
 		assertEquals( "reader saw no exceptions", null, holder.e2 );
 	}
 
 	public void testGetDoesNotBlockPut() throws Exception {
 		getDoesNotBlockPutTest();
 	}
 
 	private void getDoesNotBlockPutTest() throws Exception {
 		Configuration cfg = createConfiguration();
 		InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-				new BasicServiceRegistryImpl( cfg.getProperties() ),
+				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
 				cfg,
 				getCacheTestSupport()
 		);
 
 		// Sleep a bit to avoid concurrent FLUSH problem
 		avoidConcurrentFlush();
 
 		final QueryResultsRegion region = regionFactory.buildQueryResultsRegion(
 				getStandardRegionName( REGION_PREFIX ),
 				cfg.getProperties()
 		);
 
 		region.put( KEY, VALUE1 );
 		assertEquals( VALUE1, region.get( KEY ) );
 
 		// final Fqn rootFqn = getRegionFqn(getStandardRegionName(REGION_PREFIX), REGION_PREFIX);
 		final CacheAdapter jbc = getInfinispanCache( regionFactory );
 
 		final CountDownLatch blockerLatch = new CountDownLatch( 1 );
 		final CountDownLatch writerLatch = new CountDownLatch( 1 );
 		final CountDownLatch completionLatch = new CountDownLatch( 1 );
 		final ExceptionHolder holder = new ExceptionHolder();
 
 		Thread blocker = new Thread() {
 
 			@Override
 			public void run() {
 				// Fqn toBlock = new Fqn(rootFqn, KEY);
 				GetBlocker blocker = new GetBlocker( blockerLatch, KEY );
 				try {
 					jbc.addListener( blocker );
 
 					BatchModeTransactionManager.getInstance().begin();
 					region.get( KEY );
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (Exception e) {
 					holder.e1 = e;
 					rollback();
 				}
 				finally {
 					jbc.removeListener( blocker );
 				}
 			}
 		};
 
 		Thread writer = new Thread() {
 
 			@Override
 			public void run() {
 				try {
 					writerLatch.await();
 
 					BatchModeTransactionManager.getInstance().begin();
 					region.put( KEY, VALUE2 );
 					BatchModeTransactionManager.getInstance().commit();
 				}
 				catch (Exception e) {
 					holder.e2 = e;
 					rollback();
 				}
 				finally {
 					completionLatch.countDown();
 				}
 			}
 		};
 
 		blocker.setDaemon( true );
 		writer.setDaemon( true );
 
 		boolean unblocked = false;
 		try {
 			blocker.start();
 			writer.start();
 
 			assertFalse( "Blocker is blocking", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 			// Start the writer
 			writerLatch.countDown();
 			assertTrue( "Writer finished promptly", completionLatch.await( 100, TimeUnit.MILLISECONDS ) );
 
 			blockerLatch.countDown();
 			unblocked = true;
 
 			if ( IsolationLevel.REPEATABLE_READ.equals( jbc.getConfiguration().getIsolationLevel() ) ) {
 				assertEquals( VALUE1, region.get( KEY ) );
 			}
 			else {
 				assertEquals( VALUE2, region.get( KEY ) );
 			}
 
 			if ( holder.a1 != null ) {
 				throw holder.a1;
 			}
 			else if ( holder.a2 != null ) {
 				throw holder.a2;
 			}
 
 			assertEquals( "blocker saw no exceptions", null, holder.e1 );
 			assertEquals( "writer saw no exceptions", null, holder.e2 );
 		}
 		finally {
 			if ( !unblocked ) {
 				blockerLatch.countDown();
 			}
 		}
 	}
 
 	@Listener
 	public class GetBlocker {
 
 		private CountDownLatch latch;
 		// private Fqn fqn;
 		private Object key;
 
 		GetBlocker(
 				CountDownLatch latch,
 				Object key
 		) {
 			this.latch = latch;
 			this.key = key;
 		}
 
 		@CacheEntryVisited
 		public void nodeVisisted(CacheEntryVisitedEvent event) {
 			if ( event.isPre() && event.getKey().equals( key ) ) {
 				try {
 					latch.await();
 				}
 				catch (InterruptedException e) {
 					log.error( "Interrupted waiting for latch", e );
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
diff --git a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
index 729925d7d4..9a412ffa16 100644
--- a/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
+++ b/hibernate-infinispan/src/test/java/org/hibernate/test/cache/infinispan/timestamp/TimestampsRegionImplTestCase.java
@@ -1,212 +1,213 @@
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
 
 import org.infinispan.AdvancedCache;
 import org.infinispan.notifications.Listener;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryActivated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryCreated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryEvicted;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryInvalidated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryLoaded;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryPassivated;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
 import org.infinispan.notifications.cachelistener.annotation.CacheEntryVisited;
 import org.infinispan.notifications.cachelistener.event.Event;
 
 import org.hibernate.cache.CacheDataDescription;
 import org.hibernate.cache.Region;
 import org.hibernate.cache.UpdateTimestampsCache;
 import org.hibernate.cache.infinispan.InfinispanRegionFactory;
 import org.hibernate.cache.infinispan.impl.ClassLoaderAwareCache;
 import org.hibernate.cache.infinispan.timestamp.TimestampsRegionImpl;
 import org.hibernate.cache.infinispan.util.CacheAdapter;
 import org.hibernate.cache.infinispan.util.CacheAdapterImpl;
 import org.hibernate.cache.infinispan.util.FlagAdapter;
 import org.hibernate.cfg.Configuration;
+import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.hibernate.test.cache.infinispan.AbstractGeneralDataRegionTestCase;
 import org.hibernate.test.cache.infinispan.functional.classloader.Account;
 import org.hibernate.test.cache.infinispan.functional.classloader.AccountHolder;
 import org.hibernate.test.cache.infinispan.functional.classloader.SelectedClassnameClassLoader;
 import org.hibernate.test.cache.infinispan.util.CacheTestUtil;
 
 /**
  * Tests of TimestampsRegionImpl.
  * 
  * @author Galder Zamarreño
  * @since 3.5
  */
 public class TimestampsRegionImplTestCase extends AbstractGeneralDataRegionTestCase {
 
     @Override
    protected String getStandardRegionName(String regionPrefix) {
       return regionPrefix + "/" + UpdateTimestampsCache.class.getName();
    }
 
    @Override
    protected Region createRegion(InfinispanRegionFactory regionFactory, String regionName, Properties properties, CacheDataDescription cdd) {
       return regionFactory.buildTimestampsRegion(regionName, properties);
    }
 
    @Override
    protected CacheAdapter getInfinispanCache(InfinispanRegionFactory regionFactory) {
       return CacheAdapterImpl.newInstance(regionFactory.getCacheManager().getCache("timestamps"));
    }
 
    public void testClearTimestampsRegionInIsolated() throws Exception {
       Configuration cfg = createConfiguration();
       InfinispanRegionFactory regionFactory = CacheTestUtil.startRegionFactory(
-			  new BasicServiceRegistryImpl( cfg.getProperties() ),
+				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
 			  cfg,
 			  getCacheTestSupport()
 	  );
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       Configuration cfg2 = createConfiguration();
       InfinispanRegionFactory regionFactory2 = CacheTestUtil.startRegionFactory(
-			  new BasicServiceRegistryImpl( cfg.getProperties() ),
+				new ServiceRegistryBuilder( cfg.getProperties() ).buildServiceRegistry(),
 			  cfg2,
 			  getCacheTestSupport()
 	  );
       // Sleep a bit to avoid concurrent FLUSH problem
       avoidConcurrentFlush();
 
       TimestampsRegionImpl region = (TimestampsRegionImpl) regionFactory.buildTimestampsRegion(getStandardRegionName(REGION_PREFIX), cfg.getProperties());
       TimestampsRegionImpl region2 = (TimestampsRegionImpl) regionFactory2.buildTimestampsRegion(getStandardRegionName(REGION_PREFIX), cfg2.getProperties());
 //      QueryResultsRegion region2 = regionFactory2.buildQueryResultsRegion(getStandardRegionName(REGION_PREFIX), cfg2.getProperties());
 
 //      ClassLoader cl = Thread.currentThread().getContextClassLoader();
 //      Thread.currentThread().setContextClassLoader(cl.getParent());
 //      log.info("TCCL is " + cl.getParent());
 
       Account acct = new Account();
       acct.setAccountHolder(new AccountHolder());
       region.getCacheAdapter().withFlags(FlagAdapter.FORCE_SYNCHRONOUS).put(acct, "boo");
 
 //      region.put(acct, "boo");
 //
 //      region.evictAll();
 
 //      Account acct = new Account();
 //      acct.setAccountHolder(new AccountHolder());
 
 
 
    }
 
    @Override
    protected Configuration createConfiguration() {
       return CacheTestUtil.buildConfiguration("test", MockInfinispanRegionFactory.class, false, true);
    }
 
    public static class MockInfinispanRegionFactory extends InfinispanRegionFactory {
 
       public MockInfinispanRegionFactory() {
       }
 
       public MockInfinispanRegionFactory(Properties props) {
          super(props);
       }
 
 //      @Override
 //      protected TimestampsRegionImpl createTimestampsRegion(CacheAdapter cacheAdapter, String regionName) {
 //         return new MockTimestampsRegionImpl(cacheAdapter, regionName, getTransactionManager(), this);
 //      }
 
       @Override
       protected ClassLoaderAwareCache createCacheWrapper(AdvancedCache cache) {
          return new ClassLoaderAwareCache(cache, Thread.currentThread().getContextClassLoader()) {
             @Override
             public void addListener(Object listener) {
                super.addListener(new MockClassLoaderAwareListener(listener, this));
             }
          };
       }
 
       //      @Override
 //      protected EmbeddedCacheManager createCacheManager(Properties properties) throws CacheException {
 //         try {
 //            EmbeddedCacheManager manager = new DefaultCacheManager(InfinispanRegionFactory.DEF_INFINISPAN_CONFIG_RESOURCE);
 //            org.infinispan.config.Configuration ispnCfg = new org.infinispan.config.Configuration();
 //            ispnCfg.setCacheMode(org.infinispan.config.Configuration.CacheMode.REPL_SYNC);
 //            manager.defineConfiguration("timestamps", ispnCfg);
 //            return manager;
 //         } catch (IOException e) {
 //            throw new CacheException("Unable to create default cache manager", e);
 //         }
 //      }
 
       @Listener      
       public static class MockClassLoaderAwareListener extends ClassLoaderAwareCache.ClassLoaderAwareListener {
          MockClassLoaderAwareListener(Object listener, ClassLoaderAwareCache cache) {
             super(listener, cache);
          }
 
          @CacheEntryActivated
          @CacheEntryCreated
          @CacheEntryEvicted
          @CacheEntryInvalidated
          @CacheEntryLoaded
          @CacheEntryModified
          @CacheEntryPassivated
          @CacheEntryRemoved
          @CacheEntryVisited
          public void event(Event event) throws Throwable {
             ClassLoader cl = Thread.currentThread().getContextClassLoader();
             String notFoundPackage = "org.hibernate.test.cache.infinispan.functional.classloader";
             String[] notFoundClasses = { notFoundPackage + ".Account", notFoundPackage + ".AccountHolder" };
             SelectedClassnameClassLoader visible = new SelectedClassnameClassLoader(null, null, notFoundClasses, cl);
             Thread.currentThread().setContextClassLoader(visible);
             super.event(event);
             Thread.currentThread().setContextClassLoader(cl);            
          }
       }
    }
 
 //   @Listener
 //   public static class MockTimestampsRegionImpl extends TimestampsRegionImpl {
 //
 //      public MockTimestampsRegionImpl(CacheAdapter cacheAdapter, String name, TransactionManager transactionManager, RegionFactory factory) {
 //         super(cacheAdapter, name, transactionManager, factory);
 //      }
 //
 //      @CacheEntryModified
 //      public void nodeModified(CacheEntryModifiedEvent event) {
 ////         ClassLoader cl = Thread.currentThread().getContextClassLoader();
 ////         String notFoundPackage = "org.hibernate.test.cache.infinispan.functional.classloader";
 ////         String[] notFoundClasses = { notFoundPackage + ".Account", notFoundPackage + ".AccountHolder" };
 ////         SelectedClassnameClassLoader visible = new SelectedClassnameClassLoader(null, null, notFoundClasses, cl);
 ////         Thread.currentThread().setContextClassLoader(visible);
 //         super.nodeModified(event);
 ////         Thread.currentThread().setContextClassLoader(cl);
 //      }
 //   }
 
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java b/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
index f110dcbfad..d51f8389e8 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/ServiceRegistryBuilder.java
@@ -1,53 +1,53 @@
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
 package org.hibernate.testing;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import java.util.Map;
 import java.util.Properties;
 
 /**
  * @author Steve Ebersole
  */
 public class ServiceRegistryBuilder {
 	public static BasicServiceRegistryImpl buildServiceRegistry() {
 		return buildServiceRegistry( Environment.getProperties() );
 	}
 
 	public static BasicServiceRegistryImpl buildServiceRegistry(Map serviceRegistryConfig) {
 		Properties properties = new Properties();
 		properties.putAll( serviceRegistryConfig );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		return new BasicServiceRegistryImpl( properties );
+		return (BasicServiceRegistryImpl) new org.hibernate.service.ServiceRegistryBuilder( properties ).buildServiceRegistry();
 	}
 
 	public static void destroy(ServiceRegistry serviceRegistry) {
 		( (BasicServiceRegistryImpl) serviceRegistry ).destroy();
 	}
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
index 9df8ec1445..4730870b1a 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
@@ -1,412 +1,412 @@
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
 package org.hibernate.testing.junit4;
 
 import java.io.InputStream;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Session;
 import org.hibernate.cache.HashtableCacheProvider;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.AbstractReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 
 import org.hibernate.testing.AfterClassOnce;
 import org.hibernate.testing.BeforeClassOnce;
 import org.hibernate.testing.OnExpectedFailure;
 import org.hibernate.testing.OnFailure;
 import org.hibernate.testing.SkipLog;
 
 import static org.junit.Assert.fail;
 
 /**
  * Applies functional testing logic for core Hibernate testing on top of {@link BaseUnitTestCase}
  *
  * @author Steve Ebersole
  */
 public abstract class BaseCoreFunctionalTestCase extends BaseUnitTestCase {
 	public static final String VALIDATE_DATA_CLEANUP = "hibernate.test.validateDataCleanup";
 	public static final Dialect DIALECT = Dialect.getDialect();
 
 	private Configuration configuration;
 	private BasicServiceRegistryImpl serviceRegistry;
 	private SessionFactoryImplementor sessionFactory;
 
 	private Session session;
 
 	protected static Dialect getDialect() {
 		return DIALECT;
 	}
 
 	protected Configuration configuration() {
 		return configuration;
 	}
 
 	protected BasicServiceRegistryImpl serviceRegistry() {
 		return serviceRegistry;
 	}
 
 	protected SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	protected Session openSession() throws HibernateException {
 		session = sessionFactory().openSession();
 		return session;
 	}
 
 	protected Session openSession(Interceptor interceptor) throws HibernateException {
 		session = sessionFactory().withOptions().interceptor( interceptor ).openSession();
 		return session;
 	}
 
 
 	// before/after test class ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@BeforeClassOnce
 	@SuppressWarnings( {"UnusedDeclaration"})
 	private void buildSessionFactory() {
 		configuration = buildConfiguration();
 		serviceRegistry = buildServiceRegistry( configuration );
 		sessionFactory = (SessionFactoryImplementor) configuration.buildSessionFactory( serviceRegistry );
 		afterSessionFactoryBuilt();
 	}
 
 	protected Configuration buildConfiguration() {
 		Configuration cfg = constructConfiguration();
 		configure( cfg );
 		addMappings( cfg );
 		cfg.buildMappings();
 		applyCacheSettings( cfg );
 		afterConfigurationBuilt( cfg );
 		return cfg;
 	}
 
 	protected Configuration constructConfiguration() {
 		Configuration configuration = new Configuration()
 				.setProperty( Environment.CACHE_PROVIDER, HashtableCacheProvider.class.getName() );
 		configuration.setProperty( Configuration.USE_NEW_ID_GENERATOR_MAPPINGS, "true" );
 		if ( createSchema() ) {
 			configuration.setProperty( Environment.HBM2DDL_AUTO, "create-drop" );
 		}
 		configuration.setProperty( Environment.DIALECT, getDialect().getClass().getName() );
 		return configuration;
 	}
 
 	protected void configure(Configuration configuration) {
 	}
 
 	protected void addMappings(Configuration configuration) {
 		String[] mappings = getMappings();
 		if ( mappings != null ) {
 			for ( String mapping : mappings ) {
 				configuration.addResource(
 						getBaseForMappings() + mapping,
 						getClass().getClassLoader()
 				);
 			}
 		}
 		Class<?>[] annotatedClasses = getAnnotatedClasses();
 		if ( annotatedClasses != null ) {
 			for ( Class<?> annotatedClass : annotatedClasses ) {
 				configuration.addAnnotatedClass( annotatedClass );
 			}
 		}
 		String[] annotatedPackages = getAnnotatedPackages();
 		if ( annotatedPackages != null ) {
 			for ( String annotatedPackage : annotatedPackages ) {
 				configuration.addPackage( annotatedPackage );
 			}
 		}
 		String[] xmlFiles = getXmlFiles();
 		if ( xmlFiles != null ) {
 			for ( String xmlFile : xmlFiles ) {
 				InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream( xmlFile );
 				configuration.addInputStream( is );
 			}
 		}
 	}
 
 	protected static final String[] NO_MAPPINGS = new String[0];
 
 	protected String[] getMappings() {
 		return NO_MAPPINGS;
 	}
 
 	protected String getBaseForMappings() {
 		return "org/hibernate/test/";
 	}
 
 	protected static final Class<?>[] NO_CLASSES = new Class[0];
 
 	protected Class<?>[] getAnnotatedClasses() {
 		return NO_CLASSES;
 	}
 
 	protected String[] getAnnotatedPackages() {
 		return NO_MAPPINGS;
 	}
 
 	protected String[] getXmlFiles() {
 		// todo : rename to getOrmXmlFiles()
 		return NO_MAPPINGS;
 	}
 
 	protected void applyCacheSettings(Configuration configuration) {
 		if ( getCacheConcurrencyStrategy() != null ) {
 			Iterator itr = configuration.getClassMappings();
 			while ( itr.hasNext() ) {
 				PersistentClass clazz = (PersistentClass) itr.next();
 				Iterator props = clazz.getPropertyClosureIterator();
 				boolean hasLob = false;
 				while ( props.hasNext() ) {
 					Property prop = (Property) props.next();
 					if ( prop.getValue().isSimpleValue() ) {
 						String type = ( (SimpleValue) prop.getValue() ).getTypeName();
 						if ( "blob".equals(type) || "clob".equals(type) ) {
 							hasLob = true;
 						}
 						if ( Blob.class.getName().equals(type) || Clob.class.getName().equals(type) ) {
 							hasLob = true;
 						}
 					}
 				}
 				if ( !hasLob && !clazz.isInherited() && overrideCacheStrategy() ) {
 					configuration.setCacheConcurrencyStrategy( clazz.getEntityName(), getCacheConcurrencyStrategy() );
 				}
 			}
 			itr = configuration.getCollectionMappings();
 			while ( itr.hasNext() ) {
 				Collection coll = (Collection) itr.next();
 				configuration.setCollectionCacheConcurrencyStrategy( coll.getRole(), getCacheConcurrencyStrategy() );
 			}
 		}
 	}
 
 	protected boolean overrideCacheStrategy() {
 		return true;
 	}
 
 	protected String getCacheConcurrencyStrategy() {
 		return null;
 	}
 
 	protected void afterConfigurationBuilt(Configuration configuration) {
 		afterConfigurationBuilt( configuration.createMappings(), getDialect() );
 	}
 
 	protected void afterConfigurationBuilt(Mappings mappings, Dialect dialect) {
 	}
 
 	protected BasicServiceRegistryImpl buildServiceRegistry(Configuration configuration) {
 		Properties properties = new Properties();
 		properties.putAll( configuration.getProperties() );
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
-		BasicServiceRegistryImpl serviceRegistry = new BasicServiceRegistryImpl( properties );
+		BasicServiceRegistryImpl serviceRegistry = (BasicServiceRegistryImpl) new org.hibernate.service.ServiceRegistryBuilder( properties ).buildServiceRegistry();
 		applyServices( serviceRegistry );
 		return serviceRegistry;
 	}
 
 	protected void applyServices(BasicServiceRegistryImpl serviceRegistry) {
 	}
 
 	protected void afterSessionFactoryBuilt() {
 	}
 
 	protected boolean createSchema() {
 		return true;
 	}
 
 	protected boolean rebuildSessionFactoryOnError() {
 		return true;
 	}
 
 	@AfterClassOnce
 	@SuppressWarnings( {"UnusedDeclaration"})
 	private void releaseSessionFactory() {
 		if ( sessionFactory == null ) {
 			return;
 		}
 		sessionFactory.close();
 		sessionFactory = null;
 		configuration = null;
 	}
 
 	@OnFailure
 	@OnExpectedFailure
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void onFailure() {
 		if ( rebuildSessionFactoryOnError() ) {
 			rebuildSessionFactory();
 		}
 	}
 
 	protected void rebuildSessionFactory() {
 		if ( sessionFactory == null ) {
 			return;
 		}
 		sessionFactory.close();
 		serviceRegistry.destroy();
 
 		serviceRegistry = buildServiceRegistry( configuration );
 		sessionFactory = (SessionFactoryImplementor) configuration.buildSessionFactory( serviceRegistry );
 		afterSessionFactoryBuilt();
 	}
 
 
 	// before/after each test ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Before
 	public final void beforeTest() throws Exception {
 		prepareTest();
 	}
 
 	protected void prepareTest() throws Exception {
 	}
 
 	@After
 	public final void afterTest() throws Exception {
 		cleanupTest();
 
 		cleanupSession();
 
 		assertAllDataRemoved();
 	}
 
 	private void cleanupSession() {
 		if ( session != null && ! ( (SessionImplementor) session ).isClosed() ) {
 			if ( session.isConnected() ) {
 				session.doWork( new RollbackWork() );
 			}
 			session.close();
 		}
 		session = null;
 	}
 
 	public class RollbackWork implements Work {
 		public void execute(Connection connection) throws SQLException {
 			connection.rollback();
 		}
 	}
 
 	protected void cleanupTest() throws Exception {
 	}
 
 	@SuppressWarnings( {"UnnecessaryBoxing", "UnnecessaryUnboxing"})
 	protected void assertAllDataRemoved() {
 		if ( !createSchema() ) {
 			return; // no tables were created...
 		}
 		if ( !Boolean.getBoolean( VALIDATE_DATA_CLEANUP ) ) {
 			return;
 		}
 
 		Session tmpSession = sessionFactory.openSession();
 		try {
 			List list = tmpSession.createQuery( "select o from java.lang.Object o" ).list();
 
 			Map<String,Integer> items = new HashMap<String,Integer>();
 			if ( !list.isEmpty() ) {
 				for ( Object element : list ) {
 					Integer l = items.get( tmpSession.getEntityName( element ) );
 					if ( l == null ) {
 						l = Integer.valueOf( 0 );
 					}
 					l = Integer.valueOf( l.intValue() + 1 ) ;
 					items.put( tmpSession.getEntityName( element ), l );
 					System.out.println( "Data left: " + element );
 				}
 				fail( "Data is left in the database: " + items.toString() );
 			}
 		}
 		finally {
 			try {
 				tmpSession.close();
 			}
 			catch( Throwable t ) {
 				// intentionally empty
 			}
 		}
 	}
 
 	protected boolean readCommittedIsolationMaintained(String scenario) {
 		int isolation = java.sql.Connection.TRANSACTION_READ_UNCOMMITTED;
 		Session testSession = null;
 		try {
 			testSession = openSession();
 			isolation = testSession.doReturningWork(
 					new AbstractReturningWork<Integer>() {
 						@Override
 						public Integer execute(Connection connection) throws SQLException {
 							return connection.getTransactionIsolation();
 						}
 					}
 			);
 		}
 		catch( Throwable ignore ) {
 		}
 		finally {
 			if ( testSession != null ) {
 				try {
 					testSession.close();
 				}
 				catch( Throwable ignore ) {
 				}
 			}
 		}
 		if ( isolation < java.sql.Connection.TRANSACTION_READ_COMMITTED ) {
 			SkipLog.reportSkip( "environment does not support at least read committed isolation", scenario );
 			return false;
 		}
 		else {
 			return true;
 		}
 	}
 
 }
