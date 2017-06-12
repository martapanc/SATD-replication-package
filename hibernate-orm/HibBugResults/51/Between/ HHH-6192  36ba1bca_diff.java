diff --git a/hibernate-core/src/main/java/org/hibernate/Hibernate.java b/hibernate-core/src/main/java/org/hibernate/Hibernate.java
index 4f737fda93..21517ad253 100644
--- a/hibernate-core/src/main/java/org/hibernate/Hibernate.java
+++ b/hibernate-core/src/main/java/org/hibernate/Hibernate.java
@@ -1,181 +1,181 @@
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
 package org.hibernate;
 
 import java.util.Iterator;
 
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.HibernateIterator;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * <ul>
  * <li>Provides access to the full range of Hibernate built-in types. <tt>Type</tt>
  * instances may be used to bind values to query parameters.
  * <li>A factory for new <tt>Blob</tt>s and <tt>Clob</tt>s.
  * <li>Defines static methods for manipulation of proxies.
  * </ul>
  *
  * @author Gavin King
  * @see java.sql.Clob
  * @see java.sql.Blob
  * @see org.hibernate.type.Type
  */
 
 public final class Hibernate {
 	/**
 	 * Cannot be instantiated.
 	 */
 	private Hibernate() {
 		throw new UnsupportedOperationException();
 	}
 
 
 	/**
 	 * Force initialization of a proxy or persistent collection.
 	 * <p/>
 	 * Note: This only ensures intialization of a proxy object or collection;
 	 * it is not guaranteed that the elements INSIDE the collection will be initialized/materialized.
 	 *
 	 * @param proxy a persistable object, proxy, persistent collection or <tt>null</tt>
 	 * @throws HibernateException if we can't initialize the proxy at this time, eg. the <tt>Session</tt> was closed
 	 */
 	public static void initialize(Object proxy) throws HibernateException {
 		if ( proxy == null ) {
 			return;
 		}
 		else if ( proxy instanceof HibernateProxy ) {
 			( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().initialize();
 		}
 		else if ( proxy instanceof PersistentCollection ) {
-			( ( PersistentCollection ) proxy ).forceInitialization();
+			( (PersistentCollection) proxy ).forceInitialization();
 		}
 	}
 
 	/**
 	 * Check if the proxy or persistent collection is initialized.
 	 *
 	 * @param proxy a persistable object, proxy, persistent collection or <tt>null</tt>
 	 * @return true if the argument is already initialized, or is not a proxy or collection
 	 */
 	public static boolean isInitialized(Object proxy) {
 		if ( proxy instanceof HibernateProxy ) {
 			return !( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().isUninitialized();
 		}
 		else if ( proxy instanceof PersistentCollection ) {
 			return ( ( PersistentCollection ) proxy ).wasInitialized();
 		}
 		else {
 			return true;
 		}
 	}
 
 	/**
 	 * Get the true, underlying class of a proxied persistent class. This operation
 	 * will initialize a proxy by side-effect.
 	 *
 	 * @param proxy a persistable object or proxy
 	 * @return the true class of the instance
 	 * @throws HibernateException
 	 */
 	public static Class getClass(Object proxy) {
 		if ( proxy instanceof HibernateProxy ) {
 			return ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer()
 					.getImplementation()
 					.getClass();
 		}
 		else {
 			return proxy.getClass();
 		}
 	}
 
 	public static LobCreator getLobCreator(Session session) {
 		return getLobCreator( ( SessionImplementor ) session );
 	}
 
 	public static LobCreator getLobCreator(SessionImplementor session) {
 		return session.getFactory()
 				.getJdbcServices()
 				.getLobCreator( ( LobCreationContext ) session );
 	}
 
 	/**
 	 * Close an <tt>Iterator</tt> created by <tt>iterate()</tt> immediately,
 	 * instead of waiting until the session is closed or disconnected.
 	 *
 	 * @param iterator an <tt>Iterator</tt> created by <tt>iterate()</tt>
 	 * @throws HibernateException
 	 * @see org.hibernate.Query#iterate
 	 * @see Query#iterate()
 	 */
 	public static void close(Iterator iterator) throws HibernateException {
 		if ( iterator instanceof HibernateIterator ) {
 			( ( HibernateIterator ) iterator ).close();
 		}
 		else {
 			throw new IllegalArgumentException( "not a Hibernate iterator" );
 		}
 	}
 
 	/**
 	 * Check if the property is initialized. If the named property does not exist
 	 * or is not persistent, this method always returns <tt>true</tt>.
 	 *
 	 * @param proxy The potential proxy
 	 * @param propertyName the name of a persistent attribute of the object
 	 * @return true if the named property of the object is not listed as uninitialized; false otherwise
 	 */
 	public static boolean isPropertyInitialized(Object proxy, String propertyName) {
 		
 		Object entity;
 		if ( proxy instanceof HibernateProxy ) {
 			LazyInitializer li = ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				return false;
 			}
 			else {
 				entity = li.getImplementation();
 			}
 		}
 		else {
 			entity = proxy;
 		}
 
 		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
 			FieldInterceptor interceptor = FieldInterceptionHelper.extractFieldInterceptor( entity );
 			return interceptor == null || interceptor.isInitialized( propertyName );
 		}
 		else {
 			return true;
 		}
 		
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionAction.java
index aa9598964d..31c0c66fb0 100644
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
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.EventType;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 
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
index 773077bcce..848d89b408 100644
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
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventType;
 import org.hibernate.event.PostCollectionRecreateEvent;
 import org.hibernate.event.PostCollectionRecreateEventListener;
 import org.hibernate.event.PreCollectionRecreateEvent;
 import org.hibernate.event.PreCollectionRecreateEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.event.service.spi.EventListenerGroup;
 
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
index b56607faf6..51a0eac9fc 100644
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
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventType;
 import org.hibernate.event.PostCollectionRemoveEvent;
 import org.hibernate.event.PostCollectionRemoveEventListener;
 import org.hibernate.event.PreCollectionRemoveEvent;
 import org.hibernate.event.PreCollectionRemoveEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.event.service.spi.EventListenerGroup;
 
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
index 93907573b7..d8e09e4b44 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
@@ -1,138 +1,138 @@
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
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventType;
 import org.hibernate.event.PostCollectionUpdateEvent;
 import org.hibernate.event.PostCollectionUpdateEventListener;
 import org.hibernate.event.PreCollectionUpdateEvent;
 import org.hibernate.event.PreCollectionUpdateEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.event.service.spi.EventListenerGroup;
 
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
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CollectionCacheEntry.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CollectionCacheEntry.java
index cf266bdb46..2b8bb3661c 100755
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CollectionCacheEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CollectionCacheEntry.java
@@ -1,65 +1,65 @@
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
 package org.hibernate.cache.spi.entry;
 
 import java.io.Serializable;
 
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * @author Gavin King
  */
 public class CollectionCacheEntry implements Serializable {
 
 	private final Serializable state;
 	
 	public Serializable[] getState() {
 		//TODO: assumes all collections disassemble to an array!
 		return (Serializable[]) state;
 	}
 
 	public CollectionCacheEntry(PersistentCollection collection, CollectionPersister persister) {
 		this.state = collection.disassemble(persister);
 	}
 	
 	CollectionCacheEntry(Serializable state) {
 		this.state = state;
 	}
 	
 	public void assemble(
 		final PersistentCollection collection, 
 		final CollectionPersister persister,
 		final Object owner
 	) {
 		collection.initializeFromCache(persister, state, owner);
 		collection.afterInitialize();
 	}
 	
 	public String toString() {
 		return "CollectionCacheEntry" + ArrayHelper.toString( getState() );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/PersistentArrayHolder.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentArrayHolder.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/collection/PersistentArrayHolder.java
rename to hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentArrayHolder.java
index 9086e57b12..eda61e0634 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentArrayHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentArrayHolder.java
@@ -1,255 +1,258 @@
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
- *
  */
-package org.hibernate.collection;
+package org.hibernate.collection.internal;
+
 import java.io.Serializable;
 import java.lang.reflect.Array;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
-import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.collection.spi.AbstractPersistentCollection;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
-import org.jboss.logging.Logger;
 
 /**
  * A persistent wrapper for an array. Lazy initialization
  * is NOT supported. Use of Hibernate arrays is not really
  * recommended.
  *
  * @author Gavin King
  */
 public class PersistentArrayHolder extends AbstractPersistentCollection {
 	protected Object array;
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, PersistentArrayHolder.class.getName());
 
 	//just to help out during the load (ugly, i know)
 	private transient Class elementClass;
 	private transient java.util.List tempList;
 
 	public PersistentArrayHolder(SessionImplementor session, Object array) {
 		super(session);
 		this.array = array;
 		setInitialized();
 	}
 
 	public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
 		EntityMode entityMode = getSession().getEntityMode();
 		int length = /*(array==null) ? tempList.size() :*/ Array.getLength(array);
 		Serializable result = (Serializable) Array.newInstance( persister.getElementClass(), length );
 		for ( int i=0; i<length; i++ ) {
 			Object elt = /*(array==null) ? tempList.get(i) :*/ Array.get(array, i);
 			try {
 				Array.set( result, i, persister.getElementType().deepCopy(elt, entityMode, persister.getFactory()) );
 			}
 			catch (IllegalArgumentException iae) {
                 LOG.invalidArrayElementType(iae.getMessage());
 				throw new HibernateException( "Array element type error", iae );
 			}
 		}
 		return result;
 	}
 
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return Array.getLength( snapshot ) == 0;
 	}
 
 	@Override
     public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
 		Object[] sn = (Object[]) snapshot;
 		Object[] arr = (Object[]) array;
 		ArrayList result = new ArrayList();
 		for (int i=0; i<sn.length; i++) result.add( sn[i] );
 		for (int i=0; i<sn.length; i++) identityRemove( result, arr[i], entityName, getSession() );
 		return result;
 	}
 
 	public PersistentArrayHolder(SessionImplementor session, CollectionPersister persister) throws HibernateException {
 		super(session);
 		elementClass = persister.getElementClass();
 	}
 
 	public Object getArray() {
 		return array;
 	}
 
 	public boolean isWrapper(Object collection) {
 		return array==collection;
 	}
 
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		Type elementType = persister.getElementType();
 		Serializable snapshot = getSnapshot();
 		int xlen = Array.getLength(snapshot);
 		if ( xlen!= Array.getLength(array) ) return false;
 		for ( int i=0; i<xlen; i++) {
 			if ( elementType.isDirty( Array.get(snapshot, i), Array.get(array, i), getSession() ) ) return false;
 		}
 		return true;
 	}
 
 	public Iterator elements() {
 		//if (array==null) return tempList.iterator();
 		int length = Array.getLength(array);
 		java.util.List list = new ArrayList(length);
 		for (int i=0; i<length; i++) {
 			list.add( Array.get(array, i) );
 		}
 		return list.iterator();
 	}
 	@Override
     public boolean empty() {
 		return false;
 	}
 
 	public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner)
 	throws HibernateException, SQLException {
 
 		Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() );
 		int index = ( (Integer) persister.readIndex( rs, descriptor.getSuffixedIndexAliases(), getSession() ) ).intValue();
 		for ( int i = tempList.size(); i<=index; i++) {
 			tempList.add(i, null);
 		}
 		tempList.set(index, element);
 		return element;
 	}
 
 	public Iterator entries(CollectionPersister persister) {
 		return elements();
 	}
 
 	@Override
     public void beginRead() {
 		super.beginRead();
 		tempList = new ArrayList();
 	}
 	@Override
     public boolean endRead() {
 		setInitialized();
 		array = Array.newInstance( elementClass, tempList.size() );
 		for ( int i=0; i<tempList.size(); i++) {
 			Array.set(array, i, tempList.get(i) );
 		}
 		tempList=null;
 		return true;
 	}
 
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
 		//if (tempList==null) throw new UnsupportedOperationException("Can't lazily initialize arrays");
 	}
 
 	@Override
     public boolean isDirectlyAccessible() {
 		return true;
 	}
 
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 	throws HibernateException {
 		Serializable[] cached = (Serializable[]) disassembled;
 
 		array = Array.newInstance( persister.getElementClass(), cached.length );
 
 		for ( int i=0; i<cached.length; i++ ) {
 			Array.set( array, i, persister.getElementType().assemble( cached[i], getSession(), owner ) );
 		}
 	}
 
 	public Serializable disassemble(CollectionPersister persister) throws HibernateException {
 		int length = Array.getLength(array);
 		Serializable[] result = new Serializable[length];
 		for ( int i=0; i<length; i++ ) {
 			result[i] = persister.getElementType().disassemble( Array.get(array,i), getSession(), null );
 		}
 
 		/*int length = tempList.size();
 		Serializable[] result = new Serializable[length];
 		for ( int i=0; i<length; i++ ) {
 			result[i] = persister.getElementType().disassemble( tempList.get(i), session );
 		}*/
 
 		return result;
 
 	}
 
 	@Override
     public Object getValue() {
 		return array;
 	}
 
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
 		java.util.List deletes = new ArrayList();
 		Serializable sn = getSnapshot();
 		int snSize = Array.getLength(sn);
 		int arraySize = Array.getLength(array);
 		int end;
 		if ( snSize > arraySize ) {
 			for ( int i=arraySize; i<snSize; i++ ) deletes.add( new Integer(i) );
 			end = arraySize;
 		}
 		else {
 			end = snSize;
 		}
 		for ( int i=0; i<end; i++ ) {
 			if ( Array.get(array, i)==null && Array.get(sn, i)!=null ) deletes.add( new Integer(i) );
 		}
 		return deletes.iterator();
 	}
 
 	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
 		Serializable sn = getSnapshot();
 		return Array.get(array, i)!=null && ( i >= Array.getLength(sn) || Array.get(sn, i)==null );
 	}
 
 	public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
 		Serializable sn = getSnapshot();
 		return i<Array.getLength(sn) &&
 				Array.get(sn, i)!=null &&
 				Array.get(array, i)!=null &&
 				elemType.isDirty( Array.get(array, i), Array.get(sn, i), getSession() );
 	}
 
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
 		return new Integer(i);
 	}
 
 	public Object getElement(Object entry) {
 		return entry;
 	}
 
 	public Object getSnapshotElement(Object entry, int i) {
 		Serializable sn = getSnapshot();
 		return Array.get(sn, i);
 	}
 
 	public boolean entryExists(Object entry, int i) {
 		return entry!=null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/PersistentBag.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentBag.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/collection/PersistentBag.java
rename to hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentBag.java
index 908bc1b797..1ceca5ad9a 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentBag.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentBag.java
@@ -1,569 +1,571 @@
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
- *
  */
-package org.hibernate.collection;
+package org.hibernate.collection.internal;
+
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
+
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.AbstractPersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 
 /**
  * An unordered, unkeyed collection that can contain the same element
  * multiple times. The Java collections API, curiously, has no <tt>Bag</tt>.
  * Most developers seem to use <tt>List</tt>s to represent bag semantics,
  * so Hibernate follows this practice.
  *
  * @author Gavin King
  */
 public class PersistentBag extends AbstractPersistentCollection implements List {
 
 	protected List bag;
 
 	public PersistentBag(SessionImplementor session) {
 		super(session);
 	}
 
 	public PersistentBag(SessionImplementor session, Collection coll) {
 		super(session);
 		if (coll instanceof List) {
 			bag = (List) coll;
 		}
 		else {
 			bag = new ArrayList();
 			Iterator iter = coll.iterator();
 			while ( iter.hasNext() ) {
 				bag.add( iter.next() );
 			}
 		}
 		setInitialized();
 		setDirectlyAccessible(true);
 	}
 
 	public PersistentBag() {} //needed for SOAP libraries, etc
 
 	public boolean isWrapper(Object collection) {
 		return bag==collection;
 	}
 	public boolean empty() {
 		return bag.isEmpty();
 	}
 
 	public Iterator entries(CollectionPersister persister) {
 		return bag.iterator();
 	}
 
 	public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner)
 	throws HibernateException, SQLException {
 		// note that if we load this collection from a cartesian product
 		// the multiplicity would be broken ... so use an idbag instead
 		Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() ) ;
 		if (element!=null) bag.add(element);
 		return element;
 	}
 
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
 		this.bag = ( List ) persister.getCollectionType().instantiate( anticipatedSize );
 	}
 
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		Type elementType = persister.getElementType();
 		EntityMode entityMode = getSession().getEntityMode();
 		List sn = (List) getSnapshot();
 		if ( sn.size()!=bag.size() ) return false;
 		Iterator iter = bag.iterator();
 		while ( iter.hasNext() ) {
 			Object elt = iter.next();
 			final boolean unequal = countOccurrences(elt, bag, elementType, entityMode) !=
 				countOccurrences(elt, sn, elementType, entityMode);
 			if ( unequal ) return false;
 		}
 		return true;
 	}
 
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return ( (Collection) snapshot ).isEmpty();
 	}
 
 	private int countOccurrences(Object element, List list, Type elementType, EntityMode entityMode)
 	throws HibernateException {
 		Iterator iter = list.iterator();
 		int result=0;
 		while ( iter.hasNext() ) {
 			if ( elementType.isSame( element, iter.next(), entityMode ) ) result++;
 		}
 		return result;
 	}
 
 	public Serializable getSnapshot(CollectionPersister persister)
 	throws HibernateException {
 		EntityMode entityMode = getSession().getEntityMode();
 		ArrayList clonedList = new ArrayList( bag.size() );
 		Iterator iter = bag.iterator();
 		while ( iter.hasNext() ) {
 			clonedList.add( persister.getElementType().deepCopy( iter.next(), entityMode, persister.getFactory() ) );
 		}
 		return clonedList;
 	}
 
 	public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
 	    List sn = (List) snapshot;
 	    return getOrphans( sn, bag, entityName, getSession() );
 	}
 
 
 	public Serializable disassemble(CollectionPersister persister)
 	throws HibernateException {
 
 		int length = bag.size();
 		Serializable[] result = new Serializable[length];
 		for ( int i=0; i<length; i++ ) {
 			result[i] = persister.getElementType().disassemble( bag.get(i), getSession(), null );
 		}
 		return result;
 	}
 
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 	throws HibernateException {
 		Serializable[] array = (Serializable[]) disassembled;
 		int size = array.length;
 		beforeInitialize( persister, size );
 		for ( int i = 0; i < size; i++ ) {
 			Object element = persister.getElementType().assemble( array[i], getSession(), owner );
 			if ( element!=null ) {
 				bag.add( element );
 			}
 		}
 	}
 
 	public boolean needsRecreate(CollectionPersister persister) {
 		return !persister.isOneToMany();
 	}
 
 
 	// For a one-to-many, a <bag> is not really a bag;
 	// it is *really* a set, since it can't contain the
 	// same element twice. It could be considered a bug
 	// in the mapping dtd that <bag> allows <one-to-many>.
 
 	// Anyway, here we implement <set> semantics for a
 	// <one-to-many> <bag>!
 
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
 		//if ( !persister.isOneToMany() ) throw new AssertionFailure("Not implemented for Bags");
 		Type elementType = persister.getElementType();
 		EntityMode entityMode = getSession().getEntityMode();
 		ArrayList deletes = new ArrayList();
 		List sn = (List) getSnapshot();
 		Iterator olditer = sn.iterator();
 		int i=0;
 		while ( olditer.hasNext() ) {
 			Object old = olditer.next();
 			Iterator newiter = bag.iterator();
 			boolean found = false;
 			if ( bag.size()>i && elementType.isSame( old, bag.get(i++), entityMode ) ) {
 			//a shortcut if its location didn't change!
 				found = true;
 			}
 			else {
 				//search for it
 				//note that this code is incorrect for other than one-to-many
 				while ( newiter.hasNext() ) {
 					if ( elementType.isSame( old, newiter.next(), entityMode ) ) {
 						found = true;
 						break;
 					}
 				}
 			}
 			if (!found) deletes.add(old);
 		}
 		return deletes.iterator();
 	}
 
 	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
 		//if ( !persister.isOneToMany() ) throw new AssertionFailure("Not implemented for Bags");
 		List sn = (List) getSnapshot();
 		final EntityMode entityMode = getSession().getEntityMode();
 		if ( sn.size()>i && elemType.isSame( sn.get(i), entry, entityMode ) ) {
 		//a shortcut if its location didn't change!
 			return false;
 		}
 		else {
 			//search for it
 			//note that this code is incorrect for other than one-to-many
 			Iterator olditer = sn.iterator();
 			while ( olditer.hasNext() ) {
 				Object old = olditer.next();
 				if ( elemType.isSame( old, entry, entityMode ) ) return false;
 			}
 			return true;
 		}
 	}
 
 	public boolean isRowUpdatePossible() {
 		return false;
 	}
 
 	public boolean needsUpdating(Object entry, int i, Type elemType) {
 		//if ( !persister.isOneToMany() ) throw new AssertionFailure("Not implemented for Bags");
 		return false;
 	}
 
 	/**
 	 * @see java.util.Collection#size()
 	 */
 	public int size() {
 		return readSize() ? getCachedSize() : bag.size();
 	}
 
 	/**
 	 * @see java.util.Collection#isEmpty()
 	 */
 	public boolean isEmpty() {
 		return readSize() ? getCachedSize()==0 : bag.isEmpty();
 	}
 
 	/**
 	 * @see java.util.Collection#contains(Object)
 	 */
 	public boolean contains(Object object) {
 		Boolean exists = readElementExistence(object);
 		return exists==null ?
 				bag.contains(object) :
 				exists.booleanValue();
 	}
 
 	/**
 	 * @see java.util.Collection#iterator()
 	 */
 	public Iterator iterator() {
 		read();
 		return new IteratorProxy( bag.iterator() );
 	}
 
 	/**
 	 * @see java.util.Collection#toArray()
 	 */
 	public Object[] toArray() {
 		read();
 		return bag.toArray();
 	}
 
 	/**
 	 * @see java.util.Collection#toArray(Object[])
 	 */
 	public Object[] toArray(Object[] a) {
 		read();
 		return bag.toArray(a);
 	}
 
 	/**
 	 * @see java.util.Collection#add(Object)
 	 */
 	public boolean add(Object object) {
 		if ( !isOperationQueueEnabled() ) {
 			write();
 			return bag.add(object);
 		}
 		else {
 			queueOperation( new SimpleAdd(object) );
 			return true;
 		}
 	}
 
 	/**
 	 * @see java.util.Collection#remove(Object)
 	 */
 	public boolean remove(Object o) {
 		initialize( true );
 		if ( bag.remove( o ) ) {
 			dirty();
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.Collection#containsAll(Collection)
 	 */
 	public boolean containsAll(Collection c) {
 		read();
 		return bag.containsAll(c);
 	}
 
 	/**
 	 * @see java.util.Collection#addAll(Collection)
 	 */
 	public boolean addAll(Collection values) {
 		if ( values.size()==0 ) return false;
 		if ( !isOperationQueueEnabled() ) {
 			write();
 			return bag.addAll(values);
 		}
 		else {
 			Iterator iter = values.iterator();
 			while ( iter.hasNext() ) {
 				queueOperation( new SimpleAdd( iter.next() ) );
 			}
 			return values.size()>0;
 		}
 	}
 
 	/**
 	 * @see java.util.Collection#removeAll(Collection)
 	 */
 	public boolean removeAll(Collection c) {
 		if ( c.size()>0 ) {
 			initialize( true );
 			if ( bag.removeAll( c ) ) {
 				dirty();
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.Collection#retainAll(Collection)
 	 */
 	public boolean retainAll(Collection c) {
 		initialize( true );
 		if ( bag.retainAll( c ) ) {
 			dirty();
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.Collection#clear()
 	 */
 	public void clear() {
 		if ( isClearQueueEnabled() ) {
 			queueOperation( new Clear() );
 		}
 		else {
 			initialize( true );
 			if ( ! bag.isEmpty() ) {
 				bag.clear();
 				dirty();
 			}
 		}
 	}
 
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
 		throw new UnsupportedOperationException("Bags don't have indexes");
 	}
 
 	public Object getElement(Object entry) {
 		return entry;
 	}
 
 	public Object getSnapshotElement(Object entry, int i) {
 		List sn = (List) getSnapshot();
 		return sn.get(i);
 	}
 
 	public int occurrences(Object o) {
 		read();
 		Iterator iter = bag.iterator();
 		int result=0;
 		while ( iter.hasNext() ) {
 			if ( o.equals( iter.next() ) ) result++;
 		}
 		return result;
 	}
 
 	// List OPERATIONS:
 
 	/**
 	 * @see java.util.List#add(int, Object)
 	 */
 	public void add(int i, Object o) {
 		write();
 		bag.add(i, o);
 	}
 
 	/**
 	 * @see java.util.List#addAll(int, Collection)
 	 */
 	public boolean addAll(int i, Collection c) {
 		if ( c.size()>0 ) {
 			write();
 			return bag.addAll(i, c);
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.List#get(int)
 	 */
 	public Object get(int i) {
 		read();
 		return bag.get(i);
 	}
 
 	/**
 	 * @see java.util.List#indexOf(Object)
 	 */
 	public int indexOf(Object o) {
 		read();
 		return bag.indexOf(o);
 	}
 
 	/**
 	 * @see java.util.List#lastIndexOf(Object)
 	 */
 	public int lastIndexOf(Object o) {
 		read();
 		return bag.lastIndexOf(o);
 	}
 
 	/**
 	 * @see java.util.List#listIterator()
 	 */
 	public ListIterator listIterator() {
 		read();
 		return new ListIteratorProxy( bag.listIterator() );
 	}
 
 	/**
 	 * @see java.util.List#listIterator(int)
 	 */
 	public ListIterator listIterator(int i) {
 		read();
 		return new ListIteratorProxy( bag.listIterator(i) );
 	}
 
 	/**
 	 * @see java.util.List#remove(int)
 	 */
 	public Object remove(int i) {
 		write();
 		return bag.remove(i);
 	}
 
 	/**
 	 * @see java.util.List#set(int, Object)
 	 */
 	public Object set(int i, Object o) {
 		write();
 		return bag.set(i, o);
 	}
 
 	/**
 	 * @see java.util.List#subList(int, int)
 	 */
 	public List subList(int start, int end) {
 		read();
 		return new ListProxy( bag.subList(start, end) );
 	}
 
 	public String toString() {
 		read();
 		return bag.toString();
 	}
 
 	/*public boolean equals(Object other) {
 		read();
 		return bag.equals(other);
 	}
 
 	public int hashCode(Object other) {
 		read();
 		return bag.hashCode();
 	}*/
 
 	public boolean entryExists(Object entry, int i) {
 		return entry!=null;
 	}
 
 	/**
 	 * Bag does not respect the collection API and do an
 	 * JVM instance comparison to do the equals.
 	 * The semantic is broken not to have to initialize a
 	 * collection for a simple equals() operation.
 	 * @see java.lang.Object#equals(java.lang.Object)
 	 */
 	public boolean equals(Object obj) {
 		return super.equals(obj);
 	}
 
 	/**
 	 * @see java.lang.Object#hashCode()
 	 */
 	public int hashCode() {
 		return super.hashCode();
 	}
 
 	final class Clear implements DelayedOperation {
 		public void operate() {
 			bag.clear();
 		}
 		public Object getAddedInstance() {
 			return null;
 		}
 		public Object getOrphan() {
 			throw new UnsupportedOperationException("queued clear cannot be used with orphan delete");
 		}
 	}
 
 	final class SimpleAdd implements DelayedOperation {
 		private Object value;
 
 		public SimpleAdd(Object value) {
 			this.value = value;
 		}
 		public void operate() {
 			bag.add(value);
 		}
 		public Object getAddedInstance() {
 			return value;
 		}
 		public Object getOrphan() {
 			return null;
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/PersistentElementHolder.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentElementHolder.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/collection/PersistentElementHolder.java
rename to hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentElementHolder.java
index ee2ae87b49..3c51565846 100755
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentElementHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentElementHolder.java
@@ -1,235 +1,237 @@
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
- *
  */
-package org.hibernate.collection;
+package org.hibernate.collection.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
+
 import org.dom4j.Element;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.AbstractPersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 
 /**
  * A persistent wrapper for an XML element
  *
  * @author Gavin King
  */
 public class PersistentElementHolder extends AbstractPersistentCollection {
 	protected Element element;
 
 	public PersistentElementHolder(SessionImplementor session, Element element) {
 		super(session);
 		this.element = element;
 		setInitialized();
 	}
 
 	public Serializable getSnapshot(CollectionPersister persister)
 	throws HibernateException {
 
 		final Type elementType = persister.getElementType();
 		List elements = element.elements( persister.getElementNodeName() );
 		ArrayList snapshot = new ArrayList( elements.size() );
 		for ( int i=0; i<elements.size(); i++ ) {
 			Element elem = (Element) elements.get(i);
 			Object value = elementType.fromXMLNode( elem, persister.getFactory() );
 			Object copy = elementType.deepCopy(value , getSession().getEntityMode(), persister.getFactory() );
 			snapshot.add(copy);
 		}
 		return snapshot;
 
 	}
 
 	@Override
     public Collection getOrphans(Serializable snapshot, String entityName)
 	throws HibernateException {
 		//orphan delete not supported for EntityMode.DOM4J
 		return CollectionHelper.EMPTY_COLLECTION;
 	}
 
 	public PersistentElementHolder(SessionImplementor session, CollectionPersister persister, Serializable key)
 	throws HibernateException {
 		super(session);
 		Element owner = (Element) session.getPersistenceContext().getCollectionOwner(key, persister);
 		if (owner==null) throw new AssertionFailure("null owner");
 		//element = XMLHelper.generateDom4jElement( persister.getNodeName() );
 		final String nodeName = persister.getNodeName();
 		if ( ".".equals(nodeName) ) {
 			element = owner;
 		}
 		else {
 			element = owner.element( nodeName );
 			if (element==null) element = owner.addElement( nodeName );
 		}
 	}
 
 	public boolean isWrapper(Object collection) {
 		return element==collection;
 	}
 
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		Type elementType = persister.getElementType();
 
 		ArrayList snapshot = (ArrayList) getSnapshot();
 		List elements = element.elements( persister.getElementNodeName() );
 		if ( snapshot.size()!= elements.size() ) return false;
 		for ( int i=0; i<snapshot.size(); i++ ) {
 			Object old = snapshot.get(i);
 			Element elem = (Element) elements.get(i);
 			Object current = elementType.fromXMLNode( elem, persister.getFactory() );
 			if ( elementType.isDirty( old, current, getSession() ) ) return false;
 		}
 		return true;
 	}
 
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return ( (Collection) snapshot ).isEmpty();
 	}
 
 	@Override
     public boolean empty() {
 		return !element.elementIterator().hasNext();
 	}
 
 	public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner)
 	throws HibernateException, SQLException {
 		Object object = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() );
 		final Type elementType = persister.getElementType();
 		Element subelement = element.addElement( persister.getElementNodeName() );
 		elementType.setToXMLNode( subelement, object, persister.getFactory() );
 		return object;
 	}
 
 	public Iterator entries(CollectionPersister persister) {
 
 		final Type elementType = persister.getElementType();
 		List elements =  element.elements( persister.getElementNodeName() );
 		int length = elements.size();
 		List result = new ArrayList(length);
 		for ( int i=0; i<length; i++ ) {
 			Element elem = (Element) elements.get(i);
 			Object object = elementType.fromXMLNode( elem, persister.getFactory() );
 			result.add(object);
 		}
 		return result.iterator();
 	}
 
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {}
 
 	@Override
     public boolean isDirectlyAccessible() {
 		return true;
 	}
 
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 	throws HibernateException {
 
 		Type elementType = persister.getElementType();
 		Serializable[] cached = (Serializable[]) disassembled;
 		for ( int i=0; i<cached.length; i++ ) {
 			Object object = elementType.assemble( cached[i], getSession(), owner );
 			Element subelement = element.addElement( persister.getElementNodeName() );
 			elementType.setToXMLNode( subelement, object, persister.getFactory() );
 		}
 
 	}
 
 	public Serializable disassemble(CollectionPersister persister) throws HibernateException {
 
 		Type elementType = persister.getElementType();
 		List elements =  element.elements( persister.getElementNodeName() );
 		int length = elements.size();
 		Serializable[] result = new Serializable[length];
 		for ( int i=0; i<length; i++ ) {
 			Element elem = (Element) elements.get(i);
 			Object object = elementType.fromXMLNode( elem, persister.getFactory() );
 			result[i] = elementType.disassemble( object, getSession(), null );
 		}
 		return result;
 	}
 
 	@Override
     public Object getValue() {
 		return element;
 	}
 
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula)
 	throws HibernateException {
 
 		Type elementType = persister.getElementType();
 		ArrayList snapshot = (ArrayList) getSnapshot();
 		List elements = element.elements( persister.getElementNodeName() );
 		ArrayList result = new ArrayList();
 		for ( int i=0; i<snapshot.size(); i++ ) {
 			Object old = snapshot.get(i);
 			if ( i >= elements.size() ) {
 				result.add(old);
 			}
 			else {
 				Element elem = (Element) elements.get(i);
 				Object object = elementType.fromXMLNode( elem, persister.getFactory() );
 				if ( elementType.isDirty( old, object, getSession() ) ) result.add(old);
 			}
 		}
 		return result.iterator();
 
 	}
 
 	public boolean needsInserting(Object entry, int i, Type elementType)
 	throws HibernateException {
 		ArrayList snapshot = (ArrayList) getSnapshot();
 		return i>=snapshot.size() || elementType.isDirty( snapshot.get(i), entry, getSession() );
 	}
 
 	public boolean needsUpdating(Object entry, int i, Type elementType)
 	throws HibernateException {
 		return false;
 	}
 
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object getElement(Object entry) {
 		return entry;
 	}
 
 	public Object getSnapshotElement(Object entry, int i) {
 		throw new UnsupportedOperationException();
 	}
 
 	public boolean entryExists(Object entry, int i) {
 		return entry!=null;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/PersistentIdentifierBag.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIdentifierBag.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/collection/PersistentIdentifierBag.java
rename to hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIdentifierBag.java
index 3d0f3333c6..926a36b2e0 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentIdentifierBag.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIdentifierBag.java
@@ -1,433 +1,435 @@
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
- *
  */
-package org.hibernate.collection;
+package org.hibernate.collection.internal;
+
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Map;
+
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.AbstractPersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 
 /**
  * An <tt>IdentifierBag</tt> implements "bag" semantics more efficiently than
  * a regular <tt>Bag</tt> by adding a synthetic identifier column to the
  * table. This identifier is unique for all rows in the table, allowing very
  * efficient updates and deletes. The value of the identifier is never exposed
  * to the application.<br>
  * <br>
  * <tt>IdentifierBag</tt>s may not be used for a many-to-one association.
  * Furthermore, there is no reason to use <tt>inverse="true"</tt>.
  *
  * @author Gavin King
  */
 public class PersistentIdentifierBag extends AbstractPersistentCollection implements List {
 
 	protected List values; //element
 	protected Map identifiers; //index -> id
 
 	public PersistentIdentifierBag(SessionImplementor session) {
 		super(session);
 	}
 
 	public PersistentIdentifierBag() {} //needed for SOAP libraries, etc
 
 	public PersistentIdentifierBag(SessionImplementor session, Collection coll) {
 		super(session);
 		if (coll instanceof List) {
 			values = (List) coll;
 		}
 		else {
 			values = new ArrayList();
 			Iterator iter = coll.iterator();
 			while ( iter.hasNext() ) {
 				values.add( iter.next() );
 			}
 		}
 		setInitialized();
 		setDirectlyAccessible(true);
 		identifiers = new HashMap();
 	}
 
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 	throws HibernateException {
 		Serializable[] array = (Serializable[]) disassembled;
 		int size = array.length;
 		beforeInitialize( persister, size );
 		for ( int i = 0; i < size; i+=2 ) {
 			identifiers.put(
 				new Integer(i/2),
 				persister.getIdentifierType().assemble( array[i], getSession(), owner )
 			);
 			values.add( persister.getElementType().assemble( array[i+1], getSession(), owner ) );
 		}
 	}
 
 	public Object getIdentifier(Object entry, int i) {
 		return identifiers.get( new Integer(i) );
 	}
 
 	public boolean isWrapper(Object collection) {
 		return values==collection;
 	}
 
 	public boolean add(Object o) {
 		write();
 		values.add(o);
 		return true;
 	}
 
 	public void clear() {
 		initialize( true );
 		if ( ! values.isEmpty() || ! identifiers.isEmpty() ) {
 			values.clear();
 			identifiers.clear();
 			dirty();
 		}
 	}
 
 	public boolean contains(Object o) {
 		read();
 		return values.contains(o);
 	}
 
 	public boolean containsAll(Collection c) {
 		read();
 		return values.containsAll(c);
 	}
 
 	public boolean isEmpty() {
 		return readSize() ? getCachedSize()==0 : values.isEmpty();
 	}
 
 	public Iterator iterator() {
 		read();
 		return new IteratorProxy( values.iterator() );
 	}
 
 	public boolean remove(Object o) {
 		initialize( true );
 		int index = values.indexOf(o);
 		if (index>=0) {
 			beforeRemove(index);
 			values.remove(index);
 			dirty();
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	public boolean removeAll(Collection c) {
 		if ( c.size() > 0 ) {
 			boolean result = false;
 			Iterator iter = c.iterator();
 			while ( iter.hasNext() ) {
 				if ( remove( iter.next() ) ) result=true;
 			}
 			return result;
 		}
 		else {
 			return false;
 		}
 	}
 
 	public boolean retainAll(Collection c) {
 		initialize( true );
 		if ( values.retainAll( c ) ) {
 			dirty();
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	public int size() {
 		return readSize() ? getCachedSize() : values.size();
 	}
 
 	public Object[] toArray() {
 		read();
 		return values.toArray();
 	}
 
 	public Object[] toArray(Object[] a) {
 		read();
 		return values.toArray(a);
 	}
 
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
 		identifiers = anticipatedSize <= 0 ? new HashMap() : new HashMap( anticipatedSize + 1 + (int)( anticipatedSize * .75f ), .75f );
 		values = anticipatedSize <= 0 ? new ArrayList() : new ArrayList( anticipatedSize );
 	}
 
 	public Serializable disassemble(CollectionPersister persister)
 			throws HibernateException {
 		Serializable[] result = new Serializable[ values.size() * 2 ];
 		int i=0;
 		for (int j=0; j< values.size(); j++) {
 			Object value = values.get(j);
 			result[i++] = persister.getIdentifierType().disassemble( identifiers.get( new Integer(j) ), getSession(), null );
 			result[i++] = persister.getElementType().disassemble( value, getSession(), null );
 		}
 		return result;
 	}
 
 	public boolean empty() {
 		return values.isEmpty();
 	}
 
 	public Iterator entries(CollectionPersister persister) {
 		return values.iterator();
 	}
 
 	public boolean entryExists(Object entry, int i) {
 		return entry!=null;
 	}
 
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		Type elementType = persister.getElementType();
 		Map snap = (Map) getSnapshot();
 		if ( snap.size()!= values.size() ) return false;
 		for ( int i=0; i<values.size(); i++ ) {
 			Object value = values.get(i);
 			Object id = identifiers.get( new Integer(i) );
 			if (id==null) return false;
 			Object old = snap.get(id);
 			if ( elementType.isDirty( old, value, getSession() ) ) return false;
 		}
 		return true;
 	}
 
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return ( (Map) snapshot ).isEmpty();
 	}
 
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
 		Map snap = (Map) getSnapshot();
 		List deletes = new ArrayList( snap.keySet() );
 		for ( int i=0; i<values.size(); i++ ) {
 			if ( values.get(i)!=null ) deletes.remove( identifiers.get( new Integer(i) ) );
 		}
 		return deletes.iterator();
 	}
 
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
 		throw new UnsupportedOperationException("Bags don't have indexes");
 	}
 
 	public Object getElement(Object entry) {
 		return entry;
 	}
 
 	public Object getSnapshotElement(Object entry, int i) {
 		Map snap = (Map) getSnapshot();
 		Object id = identifiers.get( new Integer(i) );
 		return snap.get(id);
 	}
 
 	public boolean needsInserting(Object entry, int i, Type elemType)
 		throws HibernateException {
 
 		Map snap = (Map) getSnapshot();
 		Object id = identifiers.get( new Integer(i) );
 		return entry!=null && ( id==null || snap.get(id)==null );
 	}
 
 	public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
 
 		if (entry==null) return false;
 		Map snap = (Map) getSnapshot();
 		Object id = identifiers.get( new Integer(i) );
 		if (id==null) return false;
 		Object old = snap.get(id);
 		return old!=null && elemType.isDirty( old, entry, getSession() );
 	}
 
 
 	public Object readFrom(
 		ResultSet rs,
 		CollectionPersister persister,
 		CollectionAliases descriptor,
 		Object owner)
 		throws HibernateException, SQLException {
 
 		Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() );
 		Object old = identifiers.put(
 			new Integer( values.size() ),
 			persister.readIdentifier( rs, descriptor.getSuffixedIdentifierAlias(), getSession() )
 		);
 		if ( old==null ) values.add(element); //maintain correct duplication if loaded in a cartesian product
 		return element;
 	}
 
 	public Serializable getSnapshot(CollectionPersister persister)
 		throws HibernateException {
 
 		EntityMode entityMode = getSession().getEntityMode();
 
 		HashMap map = new HashMap( values.size() );
 		Iterator iter = values.iterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			Object value = iter.next();
 			map.put(
 				identifiers.get( new Integer(i++) ),
 				persister.getElementType().deepCopy(value, entityMode, persister.getFactory())
 			);
 		}
 		return map;
 	}
 
 	public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
 		Map sn = (Map) snapshot;
 		return getOrphans( sn.values(), values, entityName, getSession() );
 	}
 
 	public void preInsert(CollectionPersister persister) throws HibernateException {
 		Iterator iter = values.iterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			Object entry = iter.next();
 			Integer loc = new Integer(i++);
 			if ( !identifiers.containsKey(loc) ) { //TODO: native ids
 				Serializable id = persister.getIdentifierGenerator().generate( getSession(), entry );
 				identifiers.put(loc, id);
 			}
 		}
 	}
 
 	public void add(int index, Object element) {
 		write();
 		beforeAdd(index);
 		values.add(index, element);
 	}
 
 	public boolean addAll(int index, Collection c) {
 		if ( c.size() > 0 ) {
 			Iterator iter = c.iterator();
 			while ( iter.hasNext() ) {
 				add( index++, iter.next() );
 			}
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	public Object get(int index) {
 		read();
 		return values.get(index);
 	}
 
 	public int indexOf(Object o) {
 		read();
 		return values.indexOf(o);
 	}
 
 	public int lastIndexOf(Object o) {
 		read();
 		return values.lastIndexOf(o);
 	}
 
 	public ListIterator listIterator() {
 		read();
 		return new ListIteratorProxy( values.listIterator() );
 	}
 
 	public ListIterator listIterator(int index) {
 		read();
 		return new ListIteratorProxy( values.listIterator(index) );
 	}
 
 	private void beforeRemove(int index) {
 		Object removedId = identifiers.get( new Integer(index) );
 		int last = values.size()-1;
 		for ( int i=index; i<last; i++ ) {
 			Object id = identifiers.get( new Integer(i+1) );
 	        if ( id==null ) {
 				identifiers.remove( new Integer(i) );
 	        }
 	        else {
 				identifiers.put( new Integer(i), id );
 	        }
 		}
 		identifiers.put( new Integer(last), removedId );
 	}
 
 	private void beforeAdd(int index) {
 		for ( int i=index; i<values.size(); i++ ) {
 			identifiers.put( new Integer(i+1), identifiers.get( new Integer(i) ) );
 		}
 		identifiers.remove( new Integer(index) );
 	}
 
 	public Object remove(int index) {
 		write();
 		beforeRemove(index);
 		return values.remove(index);
 	}
 
 	public Object set(int index, Object element) {
 		write();
 		return values.set(index, element);
 	}
 
 	public List subList(int fromIndex, int toIndex) {
 		read();
 		return new ListProxy( values.subList(fromIndex, toIndex) );
 	}
 
 	public boolean addAll(Collection c) {
 		if ( c.size()> 0 ) {
 			write();
 			return values.addAll(c);
 		}
 		else {
 			return false;
 		}
 	}
 
 	public void afterRowInsert(
 		CollectionPersister persister,
 		Object entry,
 		int i)
 		throws HibernateException {
 		//TODO: if we are using identity columns, fetch the identifier
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/PersistentIndexedElementHolder.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIndexedElementHolder.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/collection/PersistentIndexedElementHolder.java
rename to hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIndexedElementHolder.java
index 83905317e8..a5e8ffec0c 100755
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentIndexedElementHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentIndexedElementHolder.java
@@ -1,250 +1,253 @@
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
- *
  */
-package org.hibernate.collection;
+package org.hibernate.collection.internal;
+
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
+
 import org.dom4j.Element;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.AbstractPersistentCollection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
+import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 import org.hibernate.type.XmlRepresentableType;
-import org.hibernate.internal.util.collections.CollectionHelper;
 
 /**
  * A persistent wrapper for an XML element
  *
  * @author Gavin King
  */
 public abstract class PersistentIndexedElementHolder extends AbstractPersistentCollection {
 	protected Element element;
 	
 	public PersistentIndexedElementHolder(SessionImplementor session, Element element) {
 		super(session);
 		this.element = element;
 		setInitialized();
 	}
 	
 	public static final class IndexedValue {
 		String index;
 		Object value;
 		IndexedValue(String index, Object value) {
 			this.index = index;
 			this.value = value;
 		}
 	}
 	
 	protected static String getIndex(Element element, String indexNodeName, int i) {
 		if (indexNodeName!=null) {
 			return element.attributeValue(indexNodeName);
 		}
 		else {
 			return Integer.toString(i);
 		}
 	}
 	
 	protected static void setIndex(Element element, String indexNodeName, String index) {
 		if (indexNodeName!=null) element.addAttribute(indexNodeName, index);
 	}
 	
 	protected static String getIndexAttributeName(CollectionPersister persister) {
 		String node = persister.getIndexNodeName();
 		return node==null ? null : node.substring(1);
 	}
 	
 	public Serializable getSnapshot(CollectionPersister persister) 
 	throws HibernateException {
 		
 		final Type elementType = persister.getElementType();
 		String indexNode = getIndexAttributeName(persister);		
 		List elements = element.elements( persister.getElementNodeName() );
 		HashMap snapshot = new HashMap( elements.size() );
 		for ( int i=0; i<elements.size(); i++ ) {
 			Element elem = (Element) elements.get(i);
 			Object value = elementType.fromXMLNode( elem, persister.getFactory() );
 			Object copy = elementType.deepCopy( value, getSession().getEntityMode(), persister.getFactory() );
 			snapshot.put( getIndex(elem, indexNode, i), copy );
 		}
 		return snapshot;
 		
 	}
 
 	public Collection getOrphans(Serializable snapshot, String entityName) 
 	throws HibernateException {
 		//orphan delete not supported for EntityMode.DOM4J
 		return CollectionHelper.EMPTY_COLLECTION; 
 	}
 
 	public PersistentIndexedElementHolder(SessionImplementor session, CollectionPersister persister, Serializable key) 
 	throws HibernateException {
 		super(session);
 		Element owner = (Element) session.getPersistenceContext().getCollectionOwner(key, persister);
 		if (owner==null) throw new AssertionFailure("null owner");
 		//element = XMLHelper.generateDom4jElement( persister.getNodeName() );
 		final String nodeName = persister.getNodeName();
 		if ( ".".equals(nodeName) ) {
 			element = owner;
 		}
 		else {
 			element = owner.element( nodeName );
 			if (element==null) element = owner.addElement( nodeName );
 		}
 	}
 
 	public boolean isWrapper(Object collection) {
 		return element==collection;
 	}
 
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		Type elementType = persister.getElementType();
 		String indexNode = getIndexAttributeName(persister);		
 		HashMap snapshot = (HashMap) getSnapshot();
 		List elements = element.elements( persister.getElementNodeName() );
 		if ( snapshot.size()!= elements.size() ) return false;
 		for ( int i=0; i<snapshot.size(); i++ ) {
 			Element elem = (Element) elements.get(i);
 			Object old = snapshot.get( getIndex(elem, indexNode, i) );
 			Object current = elementType.fromXMLNode( elem, persister.getFactory() );
 			if ( elementType.isDirty( old, current, getSession() ) ) return false;
 		}
 		return true;
 	}
 
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return ( (HashMap) snapshot ).isEmpty();
 	}
 	
 	public boolean empty() {
 		return !element.elementIterator().hasNext();
 	}
 
 	public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner)
 	throws HibernateException, SQLException {
 		Object object = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() );
 		final Type elementType = persister.getElementType();
 		final SessionFactoryImplementor factory = persister.getFactory();
 		String indexNode = getIndexAttributeName(persister);
 
 		Element elem = element.addElement( persister.getElementNodeName() );
 		elementType.setToXMLNode( elem, object, factory ); 
 		
 		final Type indexType = persister.getIndexType();
 		final Object indexValue = persister.readIndex( rs, descriptor.getSuffixedIndexAliases(), getSession() );
 		final String index = ( (XmlRepresentableType) indexType ).toXMLString( indexValue, factory );
 		setIndex(elem, indexNode, index);
 		return object;
 	}
 
 	public Iterator entries(CollectionPersister persister) {
 		
 		final Type elementType = persister.getElementType();
 		String indexNode = getIndexAttributeName(persister);
 		List elements =  element.elements( persister.getElementNodeName() );
 		int length = elements.size();
 		List result = new ArrayList(length);
 		for ( int i=0; i<length; i++ ) {
 			Element elem = (Element) elements.get(i);
 			Object object = elementType.fromXMLNode( elem, persister.getFactory() );
 			result.add( new IndexedValue( getIndex(elem, indexNode, i), object ) );
 		}
 		return result.iterator();
 	}
 
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {}
 
 	public boolean isDirectlyAccessible() {
 		return true;
 	}
 
 	public Object getValue() {
 		return element;
 	}
 
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) 
 	throws HibernateException {
 		
 		final Type indexType = persister.getIndexType();
 		HashMap snapshot = (HashMap) getSnapshot();
 		HashMap deletes = (HashMap) snapshot.clone();
 		deletes.keySet().removeAll( ( (HashMap) getSnapshot(persister) ).keySet() );
 		ArrayList deleteList = new ArrayList( deletes.size() );
 		for ( Object o : deletes.entrySet() ) {
 			Map.Entry me = (Map.Entry) o;
 			final Object object = indexIsFormula ?
 					me.getValue() :
 					( (XmlRepresentableType) indexType ).fromXMLString( (String) me.getKey(), persister.getFactory() );
 			if ( object != null ) {
 				deleteList.add( object );
 			}
 		}
 		
 		return deleteList.iterator();
 		
 	}
 
 	public boolean needsInserting(Object entry, int i, Type elementType) 
 	throws HibernateException {
 		HashMap snapshot = (HashMap) getSnapshot();
 		IndexedValue iv = (IndexedValue) entry;
 		return iv.value!=null && snapshot.get( iv.index )==null;
 	}
 
 	public boolean needsUpdating(Object entry, int i, Type elementType) 
 	throws HibernateException {
 		HashMap snapshot = (HashMap) getSnapshot();
 		IndexedValue iv = (IndexedValue) entry;
 		Object old = snapshot.get( iv.index );
 		return old!=null && elementType.isDirty( old, iv.value, getSession() );
 	}
 
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
 		String index = ( (IndexedValue) entry ).index;
 		final Type indexType = persister.getIndexType();
 		return ( (XmlRepresentableType) indexType ).fromXMLString( index, persister.getFactory() );
 	}
 
 	public Object getElement(Object entry) {
 		return ( (IndexedValue) entry ).value;
 	}
 
 	public Object getSnapshotElement(Object entry, int i) {
 		return ( (HashMap) getSnapshot() ).get( ( (IndexedValue) entry ).index );
 	}
 
 	public boolean entryExists(Object entry, int i) {
 		return entry!=null;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/PersistentList.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/collection/PersistentList.java
rename to hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java
index 8a2ff683dc..a907597993 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentList.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java
@@ -1,600 +1,602 @@
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
- *
  */
-package org.hibernate.collection;
+package org.hibernate.collection.internal;
+
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
+
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.AbstractPersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 
 /**
  * A persistent wrapper for a <tt>java.util.List</tt>. Underlying
  * collection is an <tt>ArrayList</tt>.
  *
  * @see java.util.ArrayList
  * @author Gavin King
  */
 public class PersistentList extends AbstractPersistentCollection implements List {
 
 	protected List list;
 
 	public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
 
 		EntityMode entityMode = getSession().getEntityMode();
 
 		ArrayList clonedList = new ArrayList( list.size() );
 		Iterator iter = list.iterator();
 		while ( iter.hasNext() ) {
 			Object deepCopy = persister.getElementType()
 					.deepCopy( iter.next(), entityMode, persister.getFactory() );
 			clonedList.add( deepCopy );
 		}
 		return clonedList;
 	}
 
 	public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
 		List sn = (List) snapshot;
 	    return getOrphans( sn, list, entityName, getSession() );
 	}
 
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		Type elementType = persister.getElementType();
 		List sn = (List) getSnapshot();
 		if ( sn.size()!=this.list.size() ) return false;
 		Iterator iter = list.iterator();
 		Iterator sniter = sn.iterator();
 		while ( iter.hasNext() ) {
 			if ( elementType.isDirty( iter.next(), sniter.next(), getSession() ) ) return false;
 		}
 		return true;
 	}
 
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return ( (Collection) snapshot ).isEmpty();
 	}
 
 	public PersistentList(SessionImplementor session) {
 		super(session);
 	}
 
 	public PersistentList(SessionImplementor session, List list) {
 		super(session);
 		this.list = list;
 		setInitialized();
 		setDirectlyAccessible(true);
 	}
 
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
 		this.list = ( List ) persister.getCollectionType().instantiate( anticipatedSize );
 	}
 
 	public boolean isWrapper(Object collection) {
 		return list==collection;
 	}
 
 	public PersistentList() {} //needed for SOAP libraries, etc
 
 	/**
 	 * @see java.util.List#size()
 	 */
 	public int size() {
 		return readSize() ? getCachedSize() : list.size();
 	}
 
 	/**
 	 * @see java.util.List#isEmpty()
 	 */
 	public boolean isEmpty() {
 		return readSize() ? getCachedSize()==0 : list.isEmpty();
 	}
 
 	/**
 	 * @see java.util.List#contains(Object)
 	 */
 	public boolean contains(Object object) {
 		Boolean exists = readElementExistence(object);
 		return exists==null ?
 				list.contains(object) :
 				exists.booleanValue();
 	}
 
 	/**
 	 * @see java.util.List#iterator()
 	 */
 	public Iterator iterator() {
 		read();
 		return new IteratorProxy( list.iterator() );
 	}
 
 	/**
 	 * @see java.util.List#toArray()
 	 */
 	public Object[] toArray() {
 		read();
 		return list.toArray();
 	}
 
 	/**
 	 * @see java.util.List#toArray(Object[])
 	 */
 	public Object[] toArray(Object[] array) {
 		read();
 		return list.toArray(array);
 	}
 
 	/**
 	 * @see java.util.List#add(Object)
 	 */
 	public boolean add(Object object) {
 		if ( !isOperationQueueEnabled() ) {
 			write();
 			return list.add(object);
 		}
 		else {
 			queueOperation( new SimpleAdd(object) );
 			return true;
 		}
 	}
 
 	/**
 	 * @see java.util.List#remove(Object)
 	 */
 	public boolean remove(Object value) {
 		Boolean exists = isPutQueueEnabled() ? readElementExistence(value) : null;
 		if ( exists == null ) {
 			initialize( true );
 			if ( list.remove( value ) ) {
 				dirty();
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		else if ( exists.booleanValue() ) {
 			queueOperation( new SimpleRemove(value) );
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.List#containsAll(Collection)
 	 */
 	public boolean containsAll(Collection coll) {
 		read();
 		return list.containsAll(coll);
 	}
 
 	/**
 	 * @see java.util.List#addAll(Collection)
 	 */
 	public boolean addAll(Collection values) {
 		if ( values.size()==0 ) {
 			return false;
 		}
 		if ( !isOperationQueueEnabled() ) {
 			write();
 			return list.addAll(values);
 		}
 		else {
 			Iterator iter = values.iterator();
 			while ( iter.hasNext() ) {
 				queueOperation( new SimpleAdd( iter.next() ) );
 			}
 			return values.size()>0;
 		}
 	}
 
 	/**
 	 * @see java.util.List#addAll(int, Collection)
 	 */
 	public boolean addAll(int index, Collection coll) {
 		if ( coll.size()>0 ) {
 			write();
 			return list.addAll(index,  coll);
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.List#removeAll(Collection)
 	 */
 	public boolean removeAll(Collection coll) {
 		if ( coll.size()>0 ) {
 			initialize( true );
 			if ( list.removeAll( coll ) ) {
 				dirty();
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.List#retainAll(Collection)
 	 */
 	public boolean retainAll(Collection coll) {
 		initialize( true );
 		if ( list.retainAll( coll ) ) {
 			dirty();
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.List#clear()
 	 */
 	public void clear() {
 		if ( isClearQueueEnabled() ) {
 			queueOperation( new Clear() );
 		}
 		else {
 			initialize( true );
 			if ( ! list.isEmpty() ) {
 				list.clear();
 				dirty();
 			}
 		}
 	}
 
 	/**
 	 * @see java.util.List#get(int)
 	 */
 	public Object get(int index) {
 		if (index<0) {
 			throw new ArrayIndexOutOfBoundsException("negative index");
 		}
 		Object result = readElementByIndex( new Integer(index) );
 		return result==UNKNOWN ? list.get(index) : result;
 	}
 
 	/**
 	 * @see java.util.List#set(int, Object)
 	 */
 	public Object set(int index, Object value) {
 		if (index<0) {
 			throw new ArrayIndexOutOfBoundsException("negative index");
 		}
 		Object old = isPutQueueEnabled() ? readElementByIndex( new Integer(index) ) : UNKNOWN;
 		if ( old==UNKNOWN ) {
 			write();
 			return list.set(index, value);
 		}
 		else {
 			queueOperation( new Set(index, value, old) );
 			return old;
 		}
 	}
 
 	/**
 	 * @see java.util.List#add(int, Object)
 	 */
 	public void add(int index, Object value) {
 		if (index<0) {
 			throw new ArrayIndexOutOfBoundsException("negative index");
 		}
 		if ( !isOperationQueueEnabled() ) {
 			write();
 			list.add(index, value);
 		}
 		else {
 			queueOperation( new Add(index, value) );
 		}
 	}
 
 	/**
 	 * @see java.util.List#remove(int)
 	 */
 	public Object remove(int index) {
 		if (index<0) {
 			throw new ArrayIndexOutOfBoundsException("negative index");
 		}
 		Object old = isPutQueueEnabled() ?
 				readElementByIndex( new Integer(index) ) : UNKNOWN;
 		if ( old==UNKNOWN ) {
 			write();
 			return list.remove(index);
 		}
 		else {
 			queueOperation( new Remove(index, old) );
 			return old;
 		}
 	}
 
 	/**
 	 * @see java.util.List#indexOf(Object)
 	 */
 	public int indexOf(Object value) {
 		read();
 		return list.indexOf(value);
 	}
 
 	/**
 	 * @see java.util.List#lastIndexOf(Object)
 	 */
 	public int lastIndexOf(Object value) {
 		read();
 		return list.lastIndexOf(value);
 	}
 
 	/**
 	 * @see java.util.List#listIterator()
 	 */
 	public ListIterator listIterator() {
 		read();
 		return new ListIteratorProxy( list.listIterator() );
 	}
 
 	/**
 	 * @see java.util.List#listIterator(int)
 	 */
 	public ListIterator listIterator(int index) {
 		read();
 		return new ListIteratorProxy( list.listIterator(index) );
 	}
 
 	/**
 	 * @see java.util.List#subList(int, int)
 	 */
 	public java.util.List subList(int from, int to) {
 		read();
 		return new ListProxy( list.subList(from, to) );
 	}
 
 	public boolean empty() {
 		return list.isEmpty();
 	}
 
 	public String toString() {
 		read();
 		return list.toString();
 	}
 
 	public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner)
 	throws HibernateException, SQLException {
 		Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() ) ;
 		int index = ( (Integer) persister.readIndex( rs, descriptor.getSuffixedIndexAliases(), getSession() ) ).intValue();
 
 		//pad with nulls from the current last element up to the new index
 		for ( int i = list.size(); i<=index; i++) {
 			list.add(i, null);
 		}
 
 		list.set(index, element);
 		return element;
 	}
 
 	public Iterator entries(CollectionPersister persister) {
 		return list.iterator();
 	}
 
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 	throws HibernateException {
 		Serializable[] array = ( Serializable[] ) disassembled;
 		int size = array.length;
 		beforeInitialize( persister, size );
 		for ( int i = 0; i < size; i++ ) {
 			list.add( persister.getElementType().assemble( array[i], getSession(), owner ) );
 		}
 	}
 
 	public Serializable disassemble(CollectionPersister persister)
 	throws HibernateException {
 
 		int length = list.size();
 		Serializable[] result = new Serializable[length];
 		for ( int i=0; i<length; i++ ) {
 			result[i] = persister.getElementType().disassemble( list.get(i), getSession(), null );
 		}
 		return result;
 	}
 
 
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
 		List deletes = new ArrayList();
 		List sn = (List) getSnapshot();
 		int end;
 		if ( sn.size() > list.size() ) {
 			for ( int i=list.size(); i<sn.size(); i++ ) {
 				deletes.add( indexIsFormula ? sn.get(i) : new Integer(i) );
 			}
 			end = list.size();
 		}
 		else {
 			end = sn.size();
 		}
 		for ( int i=0; i<end; i++ ) {
 			if ( list.get(i)==null && sn.get(i)!=null ) {
 				deletes.add( indexIsFormula ? sn.get(i) : new Integer(i) );
 			}
 		}
 		return deletes.iterator();
 	}
 
 	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
 		final List sn = (List) getSnapshot();
 		return list.get(i)!=null && ( i >= sn.size() || sn.get(i)==null );
 	}
 
 	public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
 		final List sn = (List) getSnapshot();
 		return i<sn.size() && sn.get(i)!=null && list.get(i)!=null &&
 			elemType.isDirty( list.get(i), sn.get(i), getSession() );
 	}
 
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
 		return new Integer(i);
 	}
 
 	public Object getElement(Object entry) {
 		return entry;
 	}
 
 	public Object getSnapshotElement(Object entry, int i) {
 		final List sn = (List) getSnapshot();
 		return sn.get(i);
 	}
 
 	public boolean equals(Object other) {
 		read();
 		return list.equals(other);
 	}
 
 	public int hashCode() {
 		read();
 		return list.hashCode();
 	}
 
 	public boolean entryExists(Object entry, int i) {
 		return entry!=null;
 	}
 
 	final class Clear implements DelayedOperation {
 		public void operate() {
 			list.clear();
 		}
 		public Object getAddedInstance() {
 			return null;
 		}
 		public Object getOrphan() {
 			throw new UnsupportedOperationException("queued clear cannot be used with orphan delete");
 		}
 	}
 
 	final class SimpleAdd implements DelayedOperation {
 		private Object value;
 
 		public SimpleAdd(Object value) {
 			this.value = value;
 		}
 		public void operate() {
 			list.add(value);
 		}
 		public Object getAddedInstance() {
 			return value;
 		}
 		public Object getOrphan() {
 			return null;
 		}
 	}
 
 	final class Add implements DelayedOperation {
 		private int index;
 		private Object value;
 
 		public Add(int index, Object value) {
 			this.index = index;
 			this.value = value;
 		}
 		public void operate() {
 			list.add(index, value);
 		}
 		public Object getAddedInstance() {
 			return value;
 		}
 		public Object getOrphan() {
 			return null;
 		}
 	}
 
 	final class Set implements DelayedOperation {
 		private int index;
 		private Object value;
 		private Object old;
 
 		public Set(int index, Object value, Object old) {
 			this.index = index;
 			this.value = value;
 			this.old = old;
 		}
 		public void operate() {
 			list.set(index, value);
 		}
 		public Object getAddedInstance() {
 			return value;
 		}
 		public Object getOrphan() {
 			return old;
 		}
 	}
 
 	final class Remove implements DelayedOperation {
 		private int index;
 		private Object old;
 
 		public Remove(int index, Object old) {
 			this.index = index;
 			this.old = old;
 		}
 		public void operate() {
 			list.remove(index);
 		}
 		public Object getAddedInstance() {
 			return null;
 		}
 		public Object getOrphan() {
 			return old;
 		}
 	}
 
 	final class SimpleRemove implements DelayedOperation {
 		private Object value;
 
 		public SimpleRemove(Object value) {
 			this.value = value;
 		}
 		public void operate() {
 			list.remove(value);
 		}
 		public Object getAddedInstance() {
 			return null;
 		}
 		public Object getOrphan() {
 			return value;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/PersistentListElementHolder.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentListElementHolder.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/collection/PersistentListElementHolder.java
rename to hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentListElementHolder.java
index 440f6726f1..45911d8d8e 100755
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentListElementHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentListElementHolder.java
@@ -1,80 +1,83 @@
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
-package org.hibernate.collection;
+package org.hibernate.collection.internal;
+
 import java.io.Serializable;
 import java.util.List;
+
 import org.dom4j.Element;
+
 import org.hibernate.HibernateException;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.IntegerType;
 import org.hibernate.type.Type;
 
 /**
  * @author Gavin King
  */
 public class PersistentListElementHolder extends PersistentIndexedElementHolder {
 
 	public PersistentListElementHolder(SessionImplementor session, Element element) {
 		super( session, element );
 	}
 
 	public PersistentListElementHolder(SessionImplementor session, CollectionPersister persister,
 			Serializable key) throws HibernateException {
 		super( session, persister, key );
 	}
 
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 	throws HibernateException {
 		
 		Type elementType = persister.getElementType();
 		final String indexNodeName = getIndexAttributeName(persister);
 		Serializable[] cached = (Serializable[]) disassembled;
 		for ( int i=0; i<cached.length; i++ ) {
 			Object object = elementType.assemble( cached[i], getSession(), owner );
 			Element subelement = element.addElement( persister.getElementNodeName() );
 			elementType.setToXMLNode( subelement, object, persister.getFactory() );
 			setIndex( subelement, indexNodeName, Integer.toString(i) );
 		}
 		
 	}
 
 	public Serializable disassemble(CollectionPersister persister) throws HibernateException {
 				
 		Type elementType = persister.getElementType();
 		final String indexNodeName = getIndexAttributeName(persister);
 		List elements =  element.elements( persister.getElementNodeName() );
 		int length = elements.size();
 		Serializable[] result = new Serializable[length];
 		for ( int i=0; i<length; i++ ) {
 			Element elem = (Element) elements.get(i);
 			Object object = elementType.fromXMLNode( elem, persister.getFactory() );
 			Integer index = IntegerType.INSTANCE.fromString( getIndex(elem, indexNodeName, i) );
 			result[ index.intValue() ] = elementType.disassemble( object, getSession(), null );
 		}
 		return result;
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/PersistentMap.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMap.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/collection/PersistentMap.java
rename to hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMap.java
index d1770d195b..cc6637c6b0 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentMap.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMap.java
@@ -1,512 +1,514 @@
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
- *
  */
-package org.hibernate.collection;
+package org.hibernate.collection.internal;
+
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
+
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.AbstractPersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 
 
 /**
  * A persistent wrapper for a <tt>java.util.Map</tt>. Underlying collection
  * is a <tt>HashMap</tt>.
  *
  * @see java.util.HashMap
  * @author Gavin King
  */
 public class PersistentMap extends AbstractPersistentCollection implements Map {
 
 	protected Map map;
 
 	/**
 	 * Empty constructor.
 	 * <p/>
 	 * Note: this form is not ever ever ever used by Hibernate; it is, however,
 	 * needed for SOAP libraries and other such marshalling code.
 	 */
 	public PersistentMap() {
 		// intentionally empty
 	}
 
 	/**
 	 * Instantiates a lazy map (the underlying map is un-initialized).
 	 *
 	 * @param session The session to which this map will belong.
 	 */
 	public PersistentMap(SessionImplementor session) {
 		super(session);
 	}
 
 	/**
 	 * Instantiates a non-lazy map (the underlying map is constructed
 	 * from the incoming map reference).
 	 *
 	 * @param session The session to which this map will belong.
 	 * @param map The underlying map data.
 	 */
 	public PersistentMap(SessionImplementor session, Map map) {
 		super(session);
 		this.map = map;
 		setInitialized();
 		setDirectlyAccessible(true);
 	}
 
 	public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
 		EntityMode entityMode = getSession().getEntityMode();
 		HashMap clonedMap = new HashMap( map.size() );
 		Iterator iter = map.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry e = (Map.Entry) iter.next();
 			final Object copy = persister.getElementType()
 				.deepCopy( e.getValue(), entityMode, persister.getFactory() );
 			clonedMap.put( e.getKey(), copy );
 		}
 		return clonedMap;
 	}
 
 	public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
 		Map sn = (Map) snapshot;
 		return getOrphans( sn.values(), map.values(), entityName, getSession() );
 	}
 
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		Type elementType = persister.getElementType();
 		Map xmap = (Map) getSnapshot();
 		if ( xmap.size()!=this.map.size() ) return false;
 		Iterator iter = map.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry entry = (Map.Entry) iter.next();
 			if ( elementType.isDirty( entry.getValue(), xmap.get( entry.getKey() ), getSession() ) ) return false;
 		}
 		return true;
 	}
 
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return ( (Map) snapshot ).isEmpty();
 	}
 
 	public boolean isWrapper(Object collection) {
 		return map==collection;
 	}
 
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
 		this.map = ( Map ) persister.getCollectionType().instantiate( anticipatedSize );
 	}
 
 
 	/**
 	 * @see java.util.Map#size()
 	 */
 	public int size() {
 		return readSize() ? getCachedSize() : map.size();
 	}
 
 	/**
 	 * @see java.util.Map#isEmpty()
 	 */
 	public boolean isEmpty() {
 		return readSize() ? getCachedSize()==0 : map.isEmpty();
 	}
 
 	/**
 	 * @see java.util.Map#containsKey(Object)
 	 */
 	public boolean containsKey(Object key) {
 		Boolean exists = readIndexExistence(key);
 		return exists==null ? map.containsKey(key) : exists.booleanValue();
 	}
 
 	/**
 	 * @see java.util.Map#containsValue(Object)
 	 */
 	public boolean containsValue(Object value) {
 		Boolean exists = readElementExistence(value);
 		return exists==null ? 
 				map.containsValue(value) : 
 				exists.booleanValue();
 	}
 
 	/**
 	 * @see java.util.Map#get(Object)
 	 */
 	public Object get(Object key) {
 		Object result = readElementByIndex(key);
 		return result==UNKNOWN ? map.get(key) : result;
 	}
 
 	/**
 	 * @see java.util.Map#put(Object, Object)
 	 */
 	public Object put(Object key, Object value) {
 		if ( isPutQueueEnabled() ) {
 			Object old = readElementByIndex( key );
 			if ( old != UNKNOWN ) {
 				queueOperation( new Put( key, value, old ) );
 				return old;
 			}
 		}
 		initialize( true );
 		Object old = map.put( key, value );
 		// would be better to use the element-type to determine
 		// whether the old and the new are equal here; the problem being
 		// we do not necessarily have access to the element type in all
 		// cases
 		if ( value != old ) {
 			dirty();
 		}
 		return old;
 	}
 
 	/**
 	 * @see java.util.Map#remove(Object)
 	 */
 	public Object remove(Object key) {
 		if ( isPutQueueEnabled() ) {
 			Object old = readElementByIndex( key );
 			if ( old != UNKNOWN ) {
 				queueOperation( new Remove( key, old ) );
 				return old;
 			}
 		}
 		// TODO : safe to interpret "map.remove(key) == null" as non-dirty?
 		initialize( true );
 		if ( map.containsKey( key ) ) {
 			dirty();
 		}
 		return map.remove( key );
 	}
 
 	/**
 	 * @see java.util.Map#putAll(java.util.Map puts)
 	 */
 	public void putAll(Map puts) {
 		if ( puts.size()>0 ) {
 			initialize( true );
 			Iterator itr = puts.entrySet().iterator();
 			while ( itr.hasNext() ) {
 				Map.Entry entry = ( Entry ) itr.next();
 				put( entry.getKey(), entry.getValue() );
 			}
 		}
 	}
 
 	/**
 	 * @see java.util.Map#clear()
 	 */
 	public void clear() {
 		if ( isClearQueueEnabled() ) {
 			queueOperation( new Clear() );
 		}
 		else {
 			initialize( true );
 			if ( ! map.isEmpty() ) {
 				dirty();
 				map.clear();
 			}
 		}
 	}
 
 	/**
 	 * @see java.util.Map#keySet()
 	 */
 	public Set keySet() {
 		read();
 		return new SetProxy( map.keySet() );
 	}
 
 	/**
 	 * @see java.util.Map#values()
 	 */
 	public Collection values() {
 		read();
 		return new SetProxy( map.values() );
 	}
 
 	/**
 	 * @see java.util.Map#entrySet()
 	 */
 	public Set entrySet() {
 		read();
 		return new EntrySetProxy( map.entrySet() );
 	}
 
 	public boolean empty() {
 		return map.isEmpty();
 	}
 
 	public String toString() {
 		read();
 		return map.toString();
 	}
 
 	public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner)
 	throws HibernateException, SQLException {
 		Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() );
 		Object index = persister.readIndex( rs, descriptor.getSuffixedIndexAliases(), getSession() );
 		if ( element!=null ) map.put(index, element);
 		return element;
 	}
 
 	public Iterator entries(CollectionPersister persister) {
 		return map.entrySet().iterator();
 	}
 
 	/** a wrapper for Map.Entry sets */
 	class EntrySetProxy implements Set {
 		private final Set set;
 		EntrySetProxy(Set set) {
 			this.set=set;
 		}
 		public boolean add(Object entry) {
 			//write(); -- doesn't
 			return set.add(entry);
 		}
 		public boolean addAll(Collection entries) {
 			//write(); -- doesn't
 			return set.addAll(entries);
 		}
 		public void clear() {
 			write();
 			set.clear();
 		}
 		public boolean contains(Object entry) {
 			return set.contains(entry);
 		}
 		public boolean containsAll(Collection entries) {
 			return set.containsAll(entries);
 		}
 		public boolean isEmpty() {
 			return set.isEmpty();
 		}
 		public Iterator iterator() {
 			return new EntryIteratorProxy( set.iterator() );
 		}
 		public boolean remove(Object entry) {
 			write();
 			return set.remove(entry);
 		}
 		public boolean removeAll(Collection entries) {
 			write();
 			return set.removeAll(entries);
 		}
 		public boolean retainAll(Collection entries) {
 			write();
 			return set.retainAll(entries);
 		}
 		public int size() {
 			return set.size();
 		}
 		// amazingly, these two will work because AbstractCollection
 		// uses iterator() to fill the array
 		public Object[] toArray() {
 			return set.toArray();
 		}
 		public Object[] toArray(Object[] array) {
 			return set.toArray(array);
 		}
 	}
 	final class EntryIteratorProxy implements Iterator {
 		private final Iterator iter;
 		EntryIteratorProxy(Iterator iter) {
 			this.iter=iter;
 		}
 		public boolean hasNext() {
 			return iter.hasNext();
 		}
 		public Object next() {
 			return new MapEntryProxy( (Map.Entry) iter.next() );
 		}
 		public void remove() {
 			write();
 			iter.remove();
 		}
 	}
 
 	final class MapEntryProxy implements Map.Entry {
 		private final Map.Entry me;
 		MapEntryProxy( Map.Entry me ) {
 			this.me = me;
 		}
 		public Object getKey() { return me.getKey(); }
 		public Object getValue() { return me.getValue(); }
 		public boolean equals(Object o) { return me.equals(o); }
 		public int hashCode() { return me.hashCode(); }
 		// finally, what it's all about...
 		public Object setValue(Object value) {
 			write();
 			return me.setValue(value);
 		}
 	}
 
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 	throws HibernateException {
 		Serializable[] array = ( Serializable[] ) disassembled;
 		int size = array.length;
 		beforeInitialize( persister, size );
 		for ( int i = 0; i < size; i+=2 ) {
 			map.put(
 					persister.getIndexType().assemble( array[i], getSession(), owner ),
 					persister.getElementType().assemble( array[i+1], getSession(), owner )
 				);
 		}
 	}
 
 	public Serializable disassemble(CollectionPersister persister) throws HibernateException {
 
 		Serializable[] result = new Serializable[ map.size() * 2 ];
 		Iterator iter = map.entrySet().iterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			Map.Entry e = (Map.Entry) iter.next();
 			result[i++] = persister.getIndexType().disassemble( e.getKey(), getSession(), null );
 			result[i++] = persister.getElementType().disassemble( e.getValue(), getSession(), null );
 		}
 		return result;
 
 	}
 
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) 
 	throws HibernateException {
 		List deletes = new ArrayList();
 		Iterator iter = ( (Map) getSnapshot() ).entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry e = (Map.Entry) iter.next();
 			Object key = e.getKey();
 			if ( e.getValue()!=null && map.get(key)==null ) {
 				deletes.add( indexIsFormula ? e.getValue() : key );
 			}
 		}
 		return deletes.iterator();
 	}
 
 	public boolean needsInserting(Object entry, int i, Type elemType) 
 	throws HibernateException {
 		final Map sn = (Map) getSnapshot();
 		Map.Entry e = (Map.Entry) entry;
 		return e.getValue()!=null && sn.get( e.getKey() )==null;
 	}
 
 	public boolean needsUpdating(Object entry, int i, Type elemType) 
 	throws HibernateException {
 		final Map sn = (Map) getSnapshot();
 		Map.Entry e = (Map.Entry) entry;
 		Object snValue = sn.get( e.getKey() );
 		return e.getValue()!=null &&
 			snValue!=null &&
 			elemType.isDirty( snValue, e.getValue(), getSession() );
 	}
 
 
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
 		return ( (Map.Entry) entry ).getKey();
 	}
 
 	public Object getElement(Object entry) {
 		return ( (Map.Entry) entry ).getValue();
 	}
 
 	public Object getSnapshotElement(Object entry, int i) {
 		final Map sn = (Map) getSnapshot();
 		return sn.get( ( (Map.Entry) entry ).getKey() );
 	}
 
 	public boolean equals(Object other) {
 		read();
 		return map.equals(other);
 	}
 
 	public int hashCode() {
 		read();
 		return map.hashCode();
 	}
 
 	public boolean entryExists(Object entry, int i) {
 		return ( (Map.Entry) entry ).getValue()!=null;
 	}
 
 	final class Clear implements DelayedOperation {
 		public void operate() {
 			map.clear();
 		}
 		public Object getAddedInstance() {
 			return null;
 		}
 		public Object getOrphan() {
 			throw new UnsupportedOperationException("queued clear cannot be used with orphan delete");
 		}
 	}
 
 	final class Put implements DelayedOperation {
 		private Object index;
 		private Object value;
 		private Object old;
 		
 		public Put(Object index, Object value, Object old) {
 			this.index = index;
 			this.value = value;
 			this.old = old;
 		}
 		public void operate() {
 			map.put(index, value);
 		}
 		public Object getAddedInstance() {
 			return value;
 		}
 		public Object getOrphan() {
 			return old;
 		}
 	}
 
 	final class Remove implements DelayedOperation {
 		private Object index;
 		private Object old;
 		
 		public Remove(Object index, Object old) {
 			this.index = index;
 			this.old = old;
 		}
 		public void operate() {
 			map.remove(index);
 		}
 		public Object getAddedInstance() {
 			return null;
 		}
 		public Object getOrphan() {
 			return old;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/PersistentMapElementHolder.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMapElementHolder.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/collection/PersistentMapElementHolder.java
rename to hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMapElementHolder.java
index 1ba9f8ec53..749e0b8716 100755
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentMapElementHolder.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMapElementHolder.java
@@ -1,90 +1,92 @@
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
- *
  */
-package org.hibernate.collection;
+package org.hibernate.collection.internal;
+
 import java.io.Serializable;
 import java.util.List;
+
 import org.dom4j.Element;
+
 import org.hibernate.HibernateException;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 import org.hibernate.type.XmlRepresentableType;
 
 /**
  * @author Gavin King
  */
 public class PersistentMapElementHolder extends PersistentIndexedElementHolder {
 
 	public PersistentMapElementHolder(SessionImplementor session, Element element) {
 		super( session, element );
 	}
 
 	public PersistentMapElementHolder(SessionImplementor session, CollectionPersister persister,
 			Serializable key) throws HibernateException {
 		super( session, persister, key );
 	}
 
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 	throws HibernateException {
 		
 		Type elementType = persister.getElementType();
 		Type indexType = persister.getIndexType();
 		final String indexNodeName = getIndexAttributeName(persister);
 
 		Serializable[] cached = (Serializable[]) disassembled;
 
 		for ( int i=0; i<cached.length; ) {
 			Object index = indexType.assemble( cached[i++], getSession(), owner );
 			Object object = elementType.assemble( cached[i++], getSession(), owner );
 			
 			Element subelement = element.addElement( persister.getElementNodeName() );
 			elementType.setToXMLNode( subelement, object, persister.getFactory() );
 			
 			String indexString = ( (XmlRepresentableType) indexType ).toXMLString( index, persister.getFactory() );
 			setIndex( subelement, indexNodeName, indexString );
 		}
 		
 	}
 
 	public Serializable disassemble(CollectionPersister persister) throws HibernateException {
 		
 		Type elementType = persister.getElementType();
 		Type indexType = persister.getIndexType();
 		final String indexNodeName = getIndexAttributeName(persister);
 
 		List elements =  element.elements( persister.getElementNodeName() );
 		int length = elements.size();
 		Serializable[] result = new Serializable[length*2];
 		for ( int i=0; i<length*2; ) {
 			Element elem = (Element) elements.get(i/2);
 			Object object = elementType.fromXMLNode( elem, persister.getFactory() );
 			final String indexString = getIndex(elem, indexNodeName, i);
 			Object index = ( (XmlRepresentableType) indexType ).fromXMLString( indexString, persister.getFactory() );
 			result[i++] = indexType.disassemble( index, getSession(), null );
 			result[i++] = elementType.disassemble( object, getSession(), null );
 		}
 		return result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/PersistentSet.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSet.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/collection/PersistentSet.java
rename to hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSet.java
index 7aefc62df1..c3a41db239 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentSet.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSet.java
@@ -1,489 +1,491 @@
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
- *
  */
-package org.hibernate.collection;
+package org.hibernate.collection.internal;
+
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
+
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.AbstractPersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 
 
 /**
  * A persistent wrapper for a <tt>java.util.Set</tt>. The underlying
  * collection is a <tt>HashSet</tt>.
  *
  * @see java.util.HashSet
  * @author Gavin King
  */
 public class PersistentSet extends AbstractPersistentCollection implements java.util.Set {
 
 	protected Set set;
 	protected transient List tempList;
 
 	/**
 	 * Empty constructor.
 	 * <p/>
 	 * Note: this form is not ever ever ever used by Hibernate; it is, however,
 	 * needed for SOAP libraries and other such marshalling code.
 	 */
 	public PersistentSet() {
 		// intentionally empty
 	}
 
 	/**
 	 * Constructor matching super.  Instantiates a lazy set (the underlying
 	 * set is un-initialized).
 	 *
 	 * @param session The session to which this set will belong.
 	 */
 	public PersistentSet(SessionImplementor session) {
 		super( session );
 	}
 
 	/**
 	 * Instantiates a non-lazy set (the underlying set is constructed
 	 * from the incoming set reference).
 	 *
 	 * @param session The session to which this set will belong.
 	 * @param set The underlying set data.
 	 */
 	public PersistentSet(SessionImplementor session, java.util.Set set) {
 		super(session);
 		// Sets can be just a view of a part of another collection.
 		// do we need to copy it to be sure it won't be changing
 		// underneath us?
 		// ie. this.set.addAll(set);
 		this.set = set;
 		setInitialized();
 		setDirectlyAccessible(true);
 	}
 
 
 	public Serializable getSnapshot(CollectionPersister persister) 
 	throws HibernateException {
 		EntityMode entityMode = getSession().getEntityMode();
 		
 		//if (set==null) return new Set(session);
 		HashMap clonedSet = new HashMap( set.size() );
 		Iterator iter = set.iterator();
 		while ( iter.hasNext() ) {
 			Object copied = persister.getElementType()
 					.deepCopy( iter.next(), entityMode, persister.getFactory() );
 			clonedSet.put(copied, copied);
 		}
 		return clonedSet;
 	}
 
 	public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
 		java.util.Map sn = (java.util.Map) snapshot;
 		return getOrphans( sn.keySet(), set, entityName, getSession() );
 	}
 
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		Type elementType = persister.getElementType();
 		java.util.Map sn = (java.util.Map) getSnapshot();
 		if ( sn.size()!=set.size() ) {
 			return false;
 		}
 		else {
 			Iterator iter = set.iterator();
 			while ( iter.hasNext() ) {
 				Object test = iter.next();
 				Object oldValue = sn.get(test);
 				if ( oldValue==null || elementType.isDirty( oldValue, test, getSession() ) ) return false;
 			}
 			return true;
 		}
 	}
 
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return ( (java.util.Map) snapshot ).isEmpty();
 	}
 
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
 		this.set = ( Set ) persister.getCollectionType().instantiate( anticipatedSize );
 	}
 
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 	throws HibernateException {
 		Serializable[] array = ( Serializable[] ) disassembled;
 		int size = array.length;
 		beforeInitialize( persister, size );
 		for (int i = 0; i < size; i++ ) {
 			Object element = persister.getElementType().assemble( array[i], getSession(), owner );
 			if ( element != null ) {
 				set.add( element );
 			}
 		}
 	}
 
 	public boolean empty() {
 		return set.isEmpty();
 	}
 
 	/**
 	 * @see java.util.Set#size()
 	 */
 	public int size() {
 		return readSize() ? getCachedSize() : set.size();
 	}
 
 	/**
 	 * @see java.util.Set#isEmpty()
 	 */
 	public boolean isEmpty() {
 		return readSize() ? getCachedSize()==0 : set.isEmpty();
 	}
 
 	/**
 	 * @see java.util.Set#contains(Object)
 	 */
 	public boolean contains(Object object) {
 		Boolean exists = readElementExistence(object);
 		return exists==null ? 
 				set.contains(object) : 
 				exists.booleanValue();
 	}
 
 	/**
 	 * @see java.util.Set#iterator()
 	 */
 	public Iterator iterator() {
 		read();
 		return new IteratorProxy( set.iterator() );
 	}
 
 	/**
 	 * @see java.util.Set#toArray()
 	 */
 	public Object[] toArray() {
 		read();
 		return set.toArray();
 	}
 
 	/**
 	 * @see java.util.Set#toArray(Object[])
 	 */
 	public Object[] toArray(Object[] array) {
 		read();
 		return set.toArray(array);
 	}
 
 	/**
 	 * @see java.util.Set#add(Object)
 	 */
 	public boolean add(Object value) {
 		Boolean exists = isOperationQueueEnabled() ? readElementExistence( value ) : null;
 		if ( exists == null ) {
 			initialize( true );
 			if ( set.add( value ) ) {
 				dirty();
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		else if ( exists.booleanValue() ) {
 			return false;
 		}
 		else {
 			queueOperation( new SimpleAdd(value) );
 			return true;
 		}
 	}
 
 	/**
 	 * @see java.util.Set#remove(Object)
 	 */
 	public boolean remove(Object value) {
 		Boolean exists = isPutQueueEnabled() ? readElementExistence( value ) : null;
 		if ( exists==null ) {
 			initialize( true );
 			if ( set.remove( value ) ) {
 				dirty();
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		else if ( exists.booleanValue() ) {
 			queueOperation( new SimpleRemove(value) );
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.Set#containsAll(Collection)
 	 */
 	public boolean containsAll(Collection coll) {
 		read();
 		return set.containsAll(coll);
 	}
 
 	/**
 	 * @see java.util.Set#addAll(Collection)
 	 */
 	public boolean addAll(Collection coll) {
 		if ( coll.size() > 0 ) {
 			initialize( true );
 			if ( set.addAll( coll ) ) {
 				dirty();
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.Set#retainAll(Collection)
 	 */
 	public boolean retainAll(Collection coll) {
 		initialize( true );
 		if ( set.retainAll( coll ) ) {
 			dirty();
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.Set#removeAll(Collection)
 	 */
 	public boolean removeAll(Collection coll) {
 		if ( coll.size() > 0 ) {
 			initialize( true );
 			if ( set.removeAll( coll ) ) {
 				dirty();
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * @see java.util.Set#clear()
 	 */
 	public void clear() {
 		if ( isClearQueueEnabled() ) {
 			queueOperation( new Clear() );
 		}
 		else {
 			initialize( true );
 			if ( !set.isEmpty() ) {
 				set.clear();
 				dirty();
 			}
 		}
 	}
 
 	public String toString() {
 		//if (needLoading) return "asleep";
 		read();
 		return set.toString();
 	}
 
 	public Object readFrom(
 	        ResultSet rs,
 	        CollectionPersister persister,
 	        CollectionAliases descriptor,
 	        Object owner) throws HibernateException, SQLException {
 		Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() );
 		if (element!=null) tempList.add(element);
 		return element;
 	}
 
 	public void beginRead() {
 		super.beginRead();
 		tempList = new ArrayList();
 	}
 
 	public boolean endRead() {
 		set.addAll(tempList);
 		tempList = null;
 		setInitialized();
 		return true;
 	}
 
 	public Iterator entries(CollectionPersister persister) {
 		return set.iterator();
 	}
 
 	public Serializable disassemble(CollectionPersister persister)
 	throws HibernateException {
 
 		Serializable[] result = new Serializable[ set.size() ];
 		Iterator iter = set.iterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			result[i++] = persister.getElementType().disassemble( iter.next(), getSession(), null );
 		}
 		return result;
 
 	}
 
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
 		Type elementType = persister.getElementType();
 		final java.util.Map sn = (java.util.Map) getSnapshot();
 		ArrayList deletes = new ArrayList( sn.size() );
 		Iterator iter = sn.keySet().iterator();
 		while ( iter.hasNext() ) {
 			Object test = iter.next();
 			if ( !set.contains(test) ) {
 				// the element has been removed from the set
 				deletes.add(test);
 			}
 		}
 		iter = set.iterator();
 		while ( iter.hasNext() ) {
 			Object test = iter.next();
 			Object oldValue = sn.get(test);
 			if ( oldValue!=null && elementType.isDirty( test, oldValue, getSession() ) ) {
 				// the element has changed
 				deletes.add(oldValue);
 			}
 		}
 		return deletes.iterator();
 	}
 
 	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
 		final java.util.Map sn = (java.util.Map) getSnapshot();
 		Object oldValue = sn.get(entry);
 		// note that it might be better to iterate the snapshot but this is safe,
 		// assuming the user implements equals() properly, as required by the Set
 		// contract!
 		return oldValue==null || elemType.isDirty( oldValue, entry, getSession() );
 	}
 
 	public boolean needsUpdating(Object entry, int i, Type elemType) {
 		return false;
 	}
 
 	public boolean isRowUpdatePossible() {
 		return false;
 	}
 
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
 		throw new UnsupportedOperationException("Sets don't have indexes");
 	}
 
 	public Object getElement(Object entry) {
 		return entry;
 	}
 
 	public Object getSnapshotElement(Object entry, int i) {
 		throw new UnsupportedOperationException("Sets don't support updating by element");
 	}
 
 	public boolean equals(Object other) {
 		read();
 		return set.equals(other);
 	}
 
 	public int hashCode() {
 		read();
 		return set.hashCode();
 	}
 
 	public boolean entryExists(Object key, int i) {
 		return true;
 	}
 
 	public boolean isWrapper(Object collection) {
 		return set==collection;
 	}
 
 	final class Clear implements DelayedOperation {
 		public void operate() {
 			set.clear();
 		}
 		public Object getAddedInstance() {
 			return null;
 		}
 		public Object getOrphan() {
 			throw new UnsupportedOperationException("queued clear cannot be used with orphan delete");
 		}
 	}
 
 	final class SimpleAdd implements DelayedOperation {
 		private Object value;
 		
 		public SimpleAdd(Object value) {
 			this.value = value;
 		}
 		public void operate() {
 			set.add(value);
 		}
 		public Object getAddedInstance() {
 			return value;
 		}
 		public Object getOrphan() {
 			return null;
 		}
 	}
 
 	final class SimpleRemove implements DelayedOperation {
 		private Object value;
 		
 		public SimpleRemove(Object value) {
 			this.value = value;
 		}
 		public void operate() {
 			set.remove(value);
 		}
 		public Object getAddedInstance() {
 			return null;
 		}
 		public Object getOrphan() {
 			return value;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/PersistentSortedMap.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedMap.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/collection/PersistentSortedMap.java
rename to hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedMap.java
index 649c048bc2..26f86287f9 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentSortedMap.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedMap.java
@@ -1,211 +1,211 @@
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
- *
  */
-package org.hibernate.collection;
+package org.hibernate.collection.internal;
+
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.Comparator;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import java.util.SortedMap;
 import java.util.TreeMap;
+
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.BasicCollectionPersister;
 
-
 /**
  * A persistent wrapper for a <tt>java.util.SortedMap</tt>. Underlying
  * collection is a <tt>TreeMap</tt>.
  *
  * @see java.util.TreeMap
  * @author <a href="mailto:doug.currie@alum.mit.edu">e</a>
  */
 public class PersistentSortedMap extends PersistentMap implements SortedMap {
 
 	protected Comparator comparator;
 
 	protected Serializable snapshot(BasicCollectionPersister persister, EntityMode entityMode) throws HibernateException {
 		TreeMap clonedMap = new TreeMap(comparator);
 		Iterator iter = map.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry e = (Map.Entry) iter.next();
 			clonedMap.put( e.getKey(), persister.getElementType().deepCopy( e.getValue(), entityMode, persister.getFactory() ) );
 		}
 		return clonedMap;
 	}
 
 	public PersistentSortedMap(SessionImplementor session) {
 		super(session);
 	}
 
 	public void setComparator(Comparator comparator) {
 		this.comparator = comparator;
 	}
 
 	public PersistentSortedMap(SessionImplementor session, SortedMap map) {
 		super(session, map);
 		comparator = map.comparator();
 	}
 
 	public PersistentSortedMap() {} //needed for SOAP libraries, etc
 
 	/**
 	 * @see PersistentSortedMap#comparator()
 	 */
 	public Comparator comparator() {
 		return comparator;
 	}
 
 	/**
 	 * @see PersistentSortedMap#subMap(Object, Object)
 	 */
 	public SortedMap subMap(Object fromKey, Object toKey) {
 		read();
 		SortedMap m = ( (SortedMap) map ).subMap(fromKey, toKey);
 		return new SortedSubMap(m);
 	}
 
 	/**
 	 * @see PersistentSortedMap#headMap(Object)
 	 */
 	public SortedMap headMap(Object toKey) {
 		read();
 		SortedMap m;
 		m = ( (SortedMap) map ).headMap(toKey);
 		return new SortedSubMap(m);
 	}
 
 	/**
 	 * @see PersistentSortedMap#tailMap(Object)
 	 */
 	public SortedMap tailMap(Object fromKey) {
 		read();
 		SortedMap m;
 		m = ( (SortedMap) map ).tailMap(fromKey);
 		return new SortedSubMap(m);
 	}
 
 	/**
 	 * @see PersistentSortedMap#firstKey()
 	 */
 	public Object firstKey() {
 		read();
 		return ( (SortedMap) map ).firstKey();
 	}
 
 	/**
 	 * @see PersistentSortedMap#lastKey()
 	 */
 	public Object lastKey() {
 		read();
 		return ( (SortedMap) map ).lastKey();
 	}
 
 	class SortedSubMap implements SortedMap {
 
 		SortedMap submap;
 
 		SortedSubMap(SortedMap m) {
 			this.submap = m;
 		}
 		// from Map
 		public int size() {
 			return submap.size();
 		}
 		public boolean isEmpty() {
 			return submap.isEmpty();
 		}
 		public boolean containsKey(Object key) {
 			return submap.containsKey(key);
 		}
 		public boolean containsValue(Object key) {
 			return submap.containsValue(key) ;
 		}
 		public Object get(Object key) {
 			return submap.get(key);
 		}
 		public Object put(Object key, Object value) {
 			write();
 			return submap.put(key,  value);
 		}
 		public Object remove(Object key) {
 			write();
 			return submap.remove(key);
 		}
 		public void putAll(Map other) {
 			write();
 			submap.putAll(other);
 		}
 		public void clear() {
 			write();
 			submap.clear();
 		}
 		public Set keySet() {
 			return new SetProxy( submap.keySet() );
 		}
 		public Collection values() {
 			return new SetProxy( submap.values() );
 		}
 		public Set entrySet() {
 			return new EntrySetProxy( submap.entrySet() );
 		}
 		// from SortedMap
 		public Comparator comparator() {
 			return submap.comparator();
 		}
 		public SortedMap subMap(Object fromKey, Object toKey) {
 			SortedMap m;
 			m = submap.subMap(fromKey, toKey);
 			return new SortedSubMap( m );
 		}
 		public SortedMap headMap(Object toKey) {
 			SortedMap m;
 			m = submap.headMap(toKey);
 			return new SortedSubMap(m);
 		}
 		public SortedMap tailMap(Object fromKey) {
 			SortedMap m;
 			m = submap.tailMap(fromKey);
 			return new SortedSubMap(m);
 		}
 		public Object firstKey() {
 			return  submap.firstKey();
 		}
 		public Object lastKey() {
 			return submap.lastKey();
 		}
 
 	}
 
 }
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/PersistentSortedSet.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedSet.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/collection/PersistentSortedSet.java
rename to hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedSet.java
index 958153c2b6..20ee35c106 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentSortedSet.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSortedSet.java
@@ -1,166 +1,166 @@
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
- *
  */
-package org.hibernate.collection;
+package org.hibernate.collection.internal;
+
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Iterator;
 import java.util.SortedSet;
 import java.util.TreeMap;
+
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.BasicCollectionPersister;
 
-
 /**
  * A persistent wrapper for a <tt>java.util.SortedSet</tt>. Underlying
  * collection is a <tt>TreeSet</tt>.
  *
  * @see java.util.TreeSet
  * @author <a href="mailto:doug.currie@alum.mit.edu">e</a>
  */
 public class PersistentSortedSet extends PersistentSet implements SortedSet {
 
 	protected Comparator comparator;
 
 	protected Serializable snapshot(BasicCollectionPersister persister, EntityMode entityMode) 
 	throws HibernateException {
 		//if (set==null) return new Set(session);
 		TreeMap clonedSet = new TreeMap(comparator);
 		Iterator iter = set.iterator();
 		while ( iter.hasNext() ) {
 			Object copy = persister.getElementType().deepCopy( iter.next(), entityMode, persister.getFactory() );
 			clonedSet.put(copy, copy);
 		}
 		return clonedSet;
 	}
 
 	public void setComparator(Comparator comparator) {
 		this.comparator = comparator;
 	}
 
 	public PersistentSortedSet(SessionImplementor session) {
 		super(session);
 	}
 
 	public PersistentSortedSet(SessionImplementor session, SortedSet set) {
 		super(session, set);
 		comparator = set.comparator();
 	}
 
 	public PersistentSortedSet() {} //needed for SOAP libraries, etc
 
 	/**
 	 * @see PersistentSortedSet#comparator()
 	 */
 	public Comparator comparator() {
 		return comparator;
 	}
 
 	/**
 	 * @see PersistentSortedSet#subSet(Object,Object)
 	 */
 	public SortedSet subSet(Object fromElement, Object toElement) {
 		read();
 		SortedSet s;
 		s = ( (SortedSet) set ).subSet(fromElement, toElement);
 		return new SubSetProxy(s);
 	}
 
 	/**
 	 * @see PersistentSortedSet#headSet(Object)
 	 */
 	public SortedSet headSet(Object toElement) {
 		read();
 		SortedSet s = ( (SortedSet) set ).headSet(toElement);
 		return new SubSetProxy(s);
 	}
 
 	/**
 	 * @see PersistentSortedSet#tailSet(Object)
 	 */
 	public SortedSet tailSet(Object fromElement) {
 		read();
 		SortedSet s = ( (SortedSet) set ).tailSet(fromElement);
 		return new SubSetProxy(s);
 	}
 
 	/**
 	 * @see PersistentSortedSet#first()
 	 */
 	public Object first() {
 		read();
 		return ( (SortedSet) set ).first();
 	}
 
 	/**
 	 * @see PersistentSortedSet#last()
 	 */
 	public Object last() {
 		read();
 		return ( (SortedSet) set ).last();
 	}
 
 	/** wrapper for subSets to propagate write to its backing set */
 	class SubSetProxy extends SetProxy implements SortedSet {
 
 		SubSetProxy(SortedSet s) {
 			super(s);
 		}
 
 		public Comparator comparator() {
 			return ( (SortedSet) this.set ).comparator();
 		}
 
 		public Object first() {
 			return ( (SortedSet) this.set ).first();
 		}
 
 		public SortedSet headSet(Object toValue) {
 			return new SubSetProxy( ( (SortedSet) this.set ).headSet(toValue) );
 		}
 
 		public Object last() {
 			return ( (SortedSet) this.set ).last();
 		}
 
 		public SortedSet subSet(Object fromValue, Object toValue) {
 			return new SubSetProxy( ( (SortedSet) this.set ).subSet(fromValue, toValue) );
 		}
 
 		public SortedSet tailSet(Object fromValue) {
 			return new SubSetProxy( ( (SortedSet) this.set ).tailSet(fromValue) );
 		}
 
 	}
 
 }
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/AbstractPersistentCollection.java b/hibernate-core/src/main/java/org/hibernate/collection/spi/AbstractPersistentCollection.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/collection/AbstractPersistentCollection.java
rename to hibernate-core/src/main/java/org/hibernate/collection/spi/AbstractPersistentCollection.java
index 520d7efab6..6192591d4b 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/AbstractPersistentCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/spi/AbstractPersistentCollection.java
@@ -1,975 +1,975 @@
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
- *
  */
-package org.hibernate.collection;
+package org.hibernate.collection.spi;
+
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LazyInitializationException;
 import org.hibernate.engine.CollectionEntry;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.ForeignKeys;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.engine.TypedValue;
+import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
-import org.hibernate.internal.util.MarkerObject;
 
 /**
- * Base class implementing {@link PersistentCollection}
+ * Base class implementing {@link org.hibernate.collection.spi.PersistentCollection}
  *
  * @author Gavin King
  */
 public abstract class AbstractPersistentCollection implements Serializable, PersistentCollection {
 
 	private transient SessionImplementor session;
 	private boolean initialized;
 	private transient List operationQueue;
 	private transient boolean directlyAccessible;
 	private transient boolean initializing;
 	private Object owner;
 	private int cachedSize = -1;
 	
 	private String role;
 	private Serializable key;
 	// collections detect changes made via their public interface and mark
 	// themselves as dirty as a performance optimization
 	private boolean dirty;
 	private Serializable storedSnapshot;
 
 	public final String getRole() {
 		return role;
 	}
 	
 	public final Serializable getKey() {
 		return key;
 	}
 	
 	public final boolean isUnreferenced() {
 		return role==null;
 	}
 	
 	public final boolean isDirty() {
 		return dirty;
 	}
 	
 	public final void clearDirty() {
 		dirty = false;
 	}
 	
 	public final void dirty() {
 		dirty = true;
 	}
 	
 	public final Serializable getStoredSnapshot() {
 		return storedSnapshot;
 	}
 	
 	//Careful: these methods do not initialize the collection.
 	/**
 	 * Is the initialized collection empty?
 	 */
 	public abstract boolean empty();
 	/**
 	 * Called by any read-only method of the collection interface
 	 */
 	protected final void read() {
 		initialize(false);
 	}
 	/**
 	 * Called by the <tt>size()</tt> method
 	 */
 	protected boolean readSize() {
 		if (!initialized) {
 			if ( cachedSize!=-1 && !hasQueuedOperations() ) {
 				return true;
 			}
 			else {
 				throwLazyInitializationExceptionIfNotConnected();
 				CollectionEntry entry = session.getPersistenceContext().getCollectionEntry(this);
 				CollectionPersister persister = entry.getLoadedPersister();
 				if ( persister.isExtraLazy() ) {
 					if ( hasQueuedOperations() ) {
 						session.flush();
 					}
 					cachedSize = persister.getSize( entry.getLoadedKey(), session );
 					return true;
 				}
 			}
 		}
 		read();
 		return false;
 	}
 	
 	protected Boolean readIndexExistence(Object index) {
 		if (!initialized) {
 			throwLazyInitializationExceptionIfNotConnected();
 			CollectionEntry entry = session.getPersistenceContext().getCollectionEntry(this);
 			CollectionPersister persister = entry.getLoadedPersister();
 			if ( persister.isExtraLazy() ) {
 				if ( hasQueuedOperations() ) {
 					session.flush();
 				}
 				return new Boolean( persister.indexExists( entry.getLoadedKey(), index, session ) );
 			}
 		}
 		read();
 		return null;
 		
 	}
 	
 	protected Boolean readElementExistence(Object element) {
 		if (!initialized) {
 			throwLazyInitializationExceptionIfNotConnected();
 			CollectionEntry entry = session.getPersistenceContext().getCollectionEntry(this);
 			CollectionPersister persister = entry.getLoadedPersister();
 			if ( persister.isExtraLazy() ) {
 				if ( hasQueuedOperations() ) {
 					session.flush();
 				}
 				return new Boolean( persister.elementExists( entry.getLoadedKey(), element, session ) );
 			}
 		}
 		read();
 		return null;
 		
 	}
 	
 	protected static final Object UNKNOWN = new MarkerObject("UNKNOWN");
 	
 	protected Object readElementByIndex(Object index) {
 		if (!initialized) {
 			throwLazyInitializationExceptionIfNotConnected();
 			CollectionEntry entry = session.getPersistenceContext().getCollectionEntry(this);
 			CollectionPersister persister = entry.getLoadedPersister();
 			if ( persister.isExtraLazy() ) {
 				if ( hasQueuedOperations() ) {
 					session.flush();
 				}
 				return persister.getElementByIndex( entry.getLoadedKey(), index, session, owner );
 			}
 		}
 		read();
 		return UNKNOWN;
 		
 	}
 	
 	protected int getCachedSize() {
 		return cachedSize;
 	}
 	
 	/**
 	 * Is the collection currently connected to an open session?
 	 */
 	private final boolean isConnectedToSession() {
 		return session!=null && 
 				session.isOpen() &&
 				session.getPersistenceContext().containsCollection(this);
 	}
 
 	/**
 	 * Called by any writer method of the collection interface
 	 */
 	protected final void write() {
 		initialize(true);
 		dirty();
 	}
 	/**
 	 * Is this collection in a state that would allow us to
 	 * "queue" operations?
 	 */
 	protected boolean isOperationQueueEnabled() {
 		return !initialized &&
 				isConnectedToSession() &&
 				isInverseCollection();
 	}
 	/**
 	 * Is this collection in a state that would allow us to
 	 * "queue" puts? This is a special case, because of orphan
 	 * delete.
 	 */
 	protected boolean isPutQueueEnabled() {
 		return !initialized &&
 				isConnectedToSession() &&
 				isInverseOneToManyOrNoOrphanDelete();
 	}
 	/**
 	 * Is this collection in a state that would allow us to
 	 * "queue" clear? This is a special case, because of orphan
 	 * delete.
 	 */
 	protected boolean isClearQueueEnabled() {
 		return !initialized &&
 				isConnectedToSession() &&
 				isInverseCollectionNoOrphanDelete();
 	}
 
 	/**
 	 * Is this the "inverse" end of a bidirectional association?
 	 */
 	private boolean isInverseCollection() {
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry(this);
 		return ce != null && ce.getLoadedPersister().isInverse();
 	}
 
 	/**
 	 * Is this the "inverse" end of a bidirectional association with
 	 * no orphan delete enabled?
 	 */
 	private boolean isInverseCollectionNoOrphanDelete() {
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry(this);
 		return ce != null && 
 				ce.getLoadedPersister().isInverse() &&
 				!ce.getLoadedPersister().hasOrphanDelete();
 	}
 
 	/**
 	 * Is this the "inverse" end of a bidirectional one-to-many, or 
 	 * of a collection with no orphan delete?
 	 */
 	private boolean isInverseOneToManyOrNoOrphanDelete() {
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry(this);
 		return ce != null && ce.getLoadedPersister().isInverse() && (
 				ce.getLoadedPersister().isOneToMany() || 
 				!ce.getLoadedPersister().hasOrphanDelete() 
 			);
 	}
 
 	/**
 	 * Queue an addition
 	 */
 	protected final void queueOperation(Object element) {
 		if (operationQueue==null) operationQueue = new ArrayList(10);
 		operationQueue.add(element);
 		dirty = true; //needed so that we remove this collection from the second-level cache
 	}
 
 	/**
 	 * After reading all existing elements from the database,
 	 * add the queued elements to the underlying collection.
 	 */
 	protected final void performQueuedOperations() {
 		for ( int i=0; i<operationQueue.size(); i++ ) {
 			( (DelayedOperation) operationQueue.get(i) ).operate();
 		}
 	}
 
 	/**
 	 * After flushing, re-init snapshot state.
 	 */
 	public void setSnapshot(Serializable key, String role, Serializable snapshot) {
 		this.key = key;
 		this.role = role;
 		this.storedSnapshot = snapshot;
 	}
 
 	/**
 	 * After flushing, clear any "queued" additions, since the
 	 * database state is now synchronized with the memory state.
 	 */
 	public void postAction() {
 		operationQueue=null;
 		cachedSize = -1;
 		clearDirty();
 	}
 	
 	/**
 	 * Not called by Hibernate, but used by non-JDK serialization,
 	 * eg. SOAP libraries.
 	 */
 	public AbstractPersistentCollection() {}
 
 	protected AbstractPersistentCollection(SessionImplementor session) {
 		this.session = session;
 	}
 
 	/**
 	 * return the user-visible collection (or array) instance
 	 */
 	public Object getValue() {
 		return this;
 	}
 
 	/**
 	 * Called just before reading any rows from the JDBC result set
 	 */
 	public void beginRead() {
 		// override on some subclasses
 		initializing = true;
 	}
 
 	/**
 	 * Called after reading all rows from the JDBC result set
 	 */
 	public boolean endRead() {
 		//override on some subclasses
 		return afterInitialize();
 	}
 	
 	public boolean afterInitialize() {
 		setInitialized();
 		//do this bit after setting initialized to true or it will recurse
 		if (operationQueue!=null) {
 			performQueuedOperations();
 			operationQueue=null;
 			cachedSize = -1;
 			return false;
 		}
 		else {
 			return true;
 		}
 	}
 
 	/**
 	 * Initialize the collection, if possible, wrapping any exceptions
 	 * in a runtime exception
 	 * @param writing currently obsolete
 	 * @throws LazyInitializationException if we cannot initialize
 	 */
 	protected final void initialize(boolean writing) {
 		if (!initialized) {
 			if (initializing) {
 				throw new LazyInitializationException("illegal access to loading collection");
 			}
 			throwLazyInitializationExceptionIfNotConnected();
 			session.initializeCollection(this, writing);
 		}
 	}
 	
 	private void throwLazyInitializationExceptionIfNotConnected() {
 		if ( !isConnectedToSession() )  {
 			throwLazyInitializationException("no session or session was closed");
 		}
 		if ( !session.isConnected() ) {
             throwLazyInitializationException("session is disconnected");
 		}		
 	}
 	
 	private void throwLazyInitializationException(String message) {
 		throw new LazyInitializationException(
 				"failed to lazily initialize a collection" + 
 				( role==null ?  "" : " of role: " + role ) + 
 				", " + message
 			);
 	}
 
 	protected final void setInitialized() {
 		this.initializing = false;
 		this.initialized = true;
 	}
 
 	protected final void setDirectlyAccessible(boolean directlyAccessible) {
 		this.directlyAccessible = directlyAccessible;
 	}
 
 	/**
 	 * Could the application possibly have a direct reference to
 	 * the underlying collection implementation?
 	 */
 	public boolean isDirectlyAccessible() {
 		return directlyAccessible;
 	}
 
 	/**
 	 * Disassociate this collection from the given session.
 	 * @return true if this was currently associated with the given session
 	 */
 	public final boolean unsetSession(SessionImplementor currentSession) {
 		if (currentSession==this.session) {
 			this.session=null;
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	/**
 	 * Associate the collection with the given session.
 	 * @return false if the collection was already associated with the session
 	 * @throws HibernateException if the collection was already associated
 	 * with another open session
 	 */
 	public final boolean setCurrentSession(SessionImplementor session) throws HibernateException {
 		if (session==this.session) {
 			return false;
 		}
 		else {
 			if ( isConnectedToSession() ) {
 				CollectionEntry ce = session.getPersistenceContext().getCollectionEntry(this);
 				if (ce==null) {
 					throw new HibernateException(
 							"Illegal attempt to associate a collection with two open sessions"
 						);
 				}
 				else {
 					throw new HibernateException(
 							"Illegal attempt to associate a collection with two open sessions: " +
 							MessageHelper.collectionInfoString( 
 									ce.getLoadedPersister(), 
 									ce.getLoadedKey(), 
 									session.getFactory() 
 								)
 						);
 				}
 			}
 			else {
 				this.session = session;
 				return true;
 			}
 		}
 	}
 
 	/**
 	 * Do we need to completely recreate this collection when it changes?
 	 */
 	public boolean needsRecreate(CollectionPersister persister) {
 		return false;
 	}
 	
 	/**
 	 * To be called internally by the session, forcing
 	 * immediate initialization.
 	 */
 	public final void forceInitialization() throws HibernateException {
 		if (!initialized) {
 			if (initializing) {
 				throw new AssertionFailure("force initialize loading collection");
 			}
 			if (session==null) {
 				throw new HibernateException("collection is not associated with any session");
 			}
 			if ( !session.isConnected() ) {
 				throw new HibernateException("disconnected session");
 			}
 			session.initializeCollection(this, false);
 		}
 	}
 
 
 	/**
 	 * Get the current snapshot from the session
 	 */
 	protected final Serializable getSnapshot() {
 		return session.getPersistenceContext().getSnapshot(this);
 	}
 
 	/**
 	 * Is this instance initialized?
 	 */
 	public final boolean wasInitialized() {
 		return initialized;
 	}
 	
 	public boolean isRowUpdatePossible() {
 		return true;
 	}
 
 	/**
 	 * Does this instance have any "queued" additions?
 	 */
 	public final boolean hasQueuedOperations() {
 		return operationQueue!=null;
 	}
 	/**
 	 * Iterate the "queued" additions
 	 */
 	public final Iterator queuedAdditionIterator() {
 		if ( hasQueuedOperations() ) {
 			return new Iterator() {
 				int i = 0;
 				public Object next() {
 					return ( (DelayedOperation) operationQueue.get(i++) ).getAddedInstance();
 				}
 				public boolean hasNext() {
 					return i<operationQueue.size();
 				}
 				public void remove() {
 					throw new UnsupportedOperationException();
 				}
 			};
 		}
 		else {
 			return EmptyIterator.INSTANCE;
 		}
 	}
 	/**
 	 * Iterate the "queued" additions
 	 */
 	public final Collection getQueuedOrphans(String entityName) {
 		if ( hasQueuedOperations() ) {
 			Collection additions = new ArrayList( operationQueue.size() );
 			Collection removals = new ArrayList( operationQueue.size() );
 			for ( int i = 0; i < operationQueue.size(); i++ ) {
 				DelayedOperation op = (DelayedOperation) operationQueue.get(i);
 				additions.add( op.getAddedInstance() );
 				removals.add( op.getOrphan() );
 			}
 			return getOrphans(removals, additions, entityName, session);
 		}
 		else {
 			return CollectionHelper.EMPTY_COLLECTION;
 		}
 	}
 
 	/**
 	 * Called before inserting rows, to ensure that any surrogate keys
 	 * are fully generated
 	 */
 	public void preInsert(CollectionPersister persister) throws HibernateException {}
 	/**
 	 * Called after inserting a row, to fetch the natively generated id
 	 */
 	public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {}
 	/**
 	 * get all "orphaned" elements
 	 */
 	public abstract Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException;
 
 	/**
 	 * Get the current session
 	 */
 	public final SessionImplementor getSession() {
 		return session;
 	}
 
-	final class IteratorProxy implements Iterator {
-		private final Iterator iter;
-		IteratorProxy(Iterator iter) {
+	protected final class IteratorProxy implements Iterator {
+		protected final Iterator iter;
+
+		public IteratorProxy(Iterator iter) {
 			this.iter=iter;
 		}
 		public boolean hasNext() {
 			return iter.hasNext();
 		}
 
 		public Object next() {
 			return iter.next();
 		}
 
 		public void remove() {
 			write();
 			iter.remove();
 		}
 
 	}
 
-	final class ListIteratorProxy implements ListIterator {
-		private final ListIterator iter;
-		ListIteratorProxy(ListIterator iter) {
+	protected final class ListIteratorProxy implements ListIterator {
+		protected final ListIterator iter;
+
+		public ListIteratorProxy(ListIterator iter) {
 			this.iter = iter;
 		}
 		public void add(Object o) {
 			write();
 			iter.add(o);
 		}
 
 		public boolean hasNext() {
 			return iter.hasNext();
 		}
 
 		public boolean hasPrevious() {
 			return iter.hasPrevious();
 		}
 
 		public Object next() {
 			return iter.next();
 		}
 
 		public int nextIndex() {
 			return iter.nextIndex();
 		}
 
 		public Object previous() {
 			return iter.previous();
 		}
 
 		public int previousIndex() {
 			return iter.previousIndex();
 		}
 
 		public void remove() {
 			write();
 			iter.remove();
 		}
 
 		public void set(Object o) {
 			write();
 			iter.set(o);
 		}
 
 	}
 
-	class SetProxy implements java.util.Set {
-
-		final Collection set;
+	protected class SetProxy implements java.util.Set {
+		protected final Collection set;
 
-		SetProxy(Collection set) {
+		public SetProxy(Collection set) {
 			this.set=set;
 		}
 		public boolean add(Object o) {
 			write();
 			return set.add(o);
 		}
 
 		public boolean addAll(Collection c) {
 			write();
 			return set.addAll(c);
 		}
 
 		public void clear() {
 			write();
 			set.clear();
 		}
 
 		public boolean contains(Object o) {
 			return set.contains(o);
 		}
 
 		public boolean containsAll(Collection c) {
 			return set.containsAll(c);
 		}
 
 		public boolean isEmpty() {
 			return set.isEmpty();
 		}
 
 		public Iterator iterator() {
 			return new IteratorProxy( set.iterator() );
 		}
 
 		public boolean remove(Object o) {
 			write();
 			return set.remove(o);
 		}
 
 		public boolean removeAll(Collection c) {
 			write();
 			return set.removeAll(c);
 		}
 
 		public boolean retainAll(Collection c) {
 			write();
 			return set.retainAll(c);
 		}
 
 		public int size() {
 			return set.size();
 		}
 
 		public Object[] toArray() {
 			return set.toArray();
 		}
 
 		public Object[] toArray(Object[] array) {
 			return set.toArray(array);
 		}
 
 	}
 
-	final class ListProxy implements java.util.List {
-
-		private final java.util.List list;
+	protected final class ListProxy implements java.util.List {
+		protected final java.util.List list;
 
-		ListProxy(java.util.List list) {
+		public ListProxy(java.util.List list) {
 			this.list = list;
 		}
 
 		public void add(int index, Object value) {
 			write();
 			list.add(index, value);
 		}
 
 		/**
 		 * @see java.util.Collection#add(Object)
 		 */
 		public boolean add(Object o) {
 			write();
 			return list.add(o);
 		}
 
 		/**
 		 * @see java.util.Collection#addAll(Collection)
 		 */
 		public boolean addAll(Collection c) {
 			write();
 			return list.addAll(c);
 		}
 
 		/**
 		 * @see java.util.List#addAll(int, Collection)
 		 */
 		public boolean addAll(int i, Collection c) {
 			write();
 			return list.addAll(i, c);
 		}
 
 		/**
 		 * @see java.util.Collection#clear()
 		 */
 		public void clear() {
 			write();
 			list.clear();
 		}
 
 		/**
 		 * @see java.util.Collection#contains(Object)
 		 */
 		public boolean contains(Object o) {
 			return list.contains(o);
 		}
 
 		/**
 		 * @see java.util.Collection#containsAll(Collection)
 		 */
 		public boolean containsAll(Collection c) {
 			return list.containsAll(c);
 		}
 
 		/**
 		 * @see java.util.List#get(int)
 		 */
 		public Object get(int i) {
 			return list.get(i);
 		}
 
 		/**
 		 * @see java.util.List#indexOf(Object)
 		 */
 		public int indexOf(Object o) {
 			return list.indexOf(o);
 		}
 
 		/**
 		 * @see java.util.Collection#isEmpty()
 		 */
 		public boolean isEmpty() {
 			return list.isEmpty();
 		}
 
 		/**
 		 * @see java.util.Collection#iterator()
 		 */
 		public Iterator iterator() {
 			return new IteratorProxy( list.iterator() );
 		}
 
 		/**
 		 * @see java.util.List#lastIndexOf(Object)
 		 */
 		public int lastIndexOf(Object o) {
 			return list.lastIndexOf(o);
 		}
 
 		/**
 		 * @see java.util.List#listIterator()
 		 */
 		public ListIterator listIterator() {
 			return new ListIteratorProxy( list.listIterator() );
 		}
 
 		/**
 		 * @see java.util.List#listIterator(int)
 		 */
 		public ListIterator listIterator(int i) {
 			return new ListIteratorProxy( list.listIterator(i) );
 		}
 
 		/**
 		 * @see java.util.List#remove(int)
 		 */
 		public Object remove(int i) {
 			write();
 			return list.remove(i);
 		}
 
 		/**
 		 * @see java.util.Collection#remove(Object)
 		 */
 		public boolean remove(Object o) {
 			write();
 			return list.remove(o);
 		}
 
 		/**
 		 * @see java.util.Collection#removeAll(Collection)
 		 */
 		public boolean removeAll(Collection c) {
 			write();
 			return list.removeAll(c);
 		}
 
 		/**
 		 * @see java.util.Collection#retainAll(Collection)
 		 */
 		public boolean retainAll(Collection c) {
 			write();
 			return list.retainAll(c);
 		}
 
 		/**
 		 * @see java.util.List#set(int, Object)
 		 */
 		public Object set(int i, Object o) {
 			write();
 			return list.set(i, o);
 		}
 
 		/**
 		 * @see java.util.Collection#size()
 		 */
 		public int size() {
 			return list.size();
 		}
 
 		/**
 		 * @see java.util.List#subList(int, int)
 		 */
 		public List subList(int i, int j) {
 			return list.subList(i, j);
 		}
 
 		/**
 		 * @see java.util.Collection#toArray()
 		 */
 		public Object[] toArray() {
 			return list.toArray();
 		}
 
 		/**
 		 * @see java.util.Collection#toArray(Object[])
 		 */
 		public Object[] toArray(Object[] array) {
 			return list.toArray(array);
 		}
 
 	}
 
 
 	protected interface DelayedOperation {
 		public void operate();
 		public Object getAddedInstance();
 		public Object getOrphan();
 	}
 	
 	/**
 	 * Given a collection of entity instances that used to
 	 * belong to the collection, and a collection of instances
 	 * that currently belong, return a collection of orphans
 	 */
 	protected static Collection getOrphans(
 			Collection oldElements, 
 			Collection currentElements, 
 			String entityName, 
 			SessionImplementor session)
 	throws HibernateException {
 
 		// short-circuit(s)
 		if ( currentElements.size()==0 ) return oldElements; // no new elements, the old list contains only Orphans
 		if ( oldElements.size()==0) return oldElements; // no old elements, so no Orphans neither
 		
 		Type idType = session.getFactory().getEntityPersister(entityName).getIdentifierType();
 
 		// create the collection holding the Orphans
 		Collection res = new ArrayList();
 
 		// collect EntityIdentifier(s) of the *current* elements - add them into a HashSet for fast access
 		java.util.Set currentIds = new HashSet();
 		java.util.Set currentSaving = new IdentitySet();
 		for ( Iterator it=currentElements.iterator(); it.hasNext(); ) {
 			Object current = it.next();
 			if ( current!=null && ForeignKeys.isNotTransient(entityName, current, null, session) ) {
 				EntityEntry ee = session.getPersistenceContext().getEntry( current );
 				if ( ee != null && ee.getStatus() == Status.SAVING ) {
 					currentSaving.add( current );
 				}
 				else {
 					Serializable currentId = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, current, session);
 					currentIds.add( new TypedValue( idType, currentId, session.getEntityMode() ) );
 				}
 			}
 		}
 
 		// iterate over the *old* list
 		for ( Iterator it=oldElements.iterator(); it.hasNext(); ) {
 			Object old = it.next();
 			if ( ! currentSaving.contains( old ) ) {
 				Serializable oldId = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, old, session);
 				if ( !currentIds.contains( new TypedValue( idType, oldId, session.getEntityMode() ) ) ) {
 					res.add(old);
 				}
 			}
 		}
 
 		return res;
 	}
 
-	static void identityRemove(
+	public static void identityRemove(
 			Collection list, 
 			Object object, 
 			String entityName, 
-			SessionImplementor session)
-	throws HibernateException {
+			SessionImplementor session) throws HibernateException {
 
 		if ( object!=null && ForeignKeys.isNotTransient(entityName, object, null, session) ) {
 			
 			Type idType = session.getFactory().getEntityPersister(entityName).getIdentifierType();
 
 			Serializable idOfCurrent = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, object, session);
 			Iterator iter = list.iterator();
 			while ( iter.hasNext() ) {
 				Serializable idOfOld = ForeignKeys.getEntityIdentifierIfNotUnsaved(entityName, iter.next(), session);
 				if ( idType.isEqual( idOfCurrent, idOfOld, session.getEntityMode(), session.getFactory() ) ) {
 					iter.remove();
 					break;
 				}
 			}
 
 		}
 	}
 	
 	public Object getIdentifier(Object entry, int i) {
 		throw new UnsupportedOperationException();
 	}
 	
 	public Object getOwner() {
 		return owner;
 	}
 	
 	public void setOwner(Object owner) {
 		this.owner = owner;
 	}
 	
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/PersistentCollection.java b/hibernate-core/src/main/java/org/hibernate/collection/spi/PersistentCollection.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/collection/PersistentCollection.java
rename to hibernate-core/src/main/java/org/hibernate/collection/spi/PersistentCollection.java
index 44cdc8c948..0768a171f3 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/PersistentCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/spi/PersistentCollection.java
@@ -1,324 +1,325 @@
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
- *
  */
-package org.hibernate.collection;
+package org.hibernate.collection.spi;
+
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Collection;
 import java.util.Iterator;
+
 import org.hibernate.HibernateException;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.Type;
 
 /**
  * Persistent collections are treated as value objects by Hibernate.
  * ie. they have no independent existence beyond the object holding
  * a reference to them. Unlike instances of entity classes, they are
  * automatically deleted when unreferenced and automatically become
  * persistent when held by a persistent object. Collections can be
  * passed between different objects (change "roles") and this might
  * cause their elements to move from one database table to another.<br>
  * <br>
  * Hibernate "wraps" a java collection in an instance of
  * PersistentCollection. This mechanism is designed to support
  * tracking of changes to the collection's persistent state and
  * lazy instantiation of collection elements. The downside is that
  * only certain abstract collection types are supported and any
  * extra semantics are lost<br>
  * <br>
  * Applications should <em>never</em> use classes in this package
  * directly, unless extending the "framework" here.<br>
  * <br>
  * Changes to <em>structure</em> of the collection are recorded by the
  * collection calling back to the session. Changes to mutable
  * elements (ie. composite elements) are discovered by cloning their
  * state when the collection is initialized and comparing at flush
  * time.
  *
  * @author Gavin King
  */
 public interface PersistentCollection {
 	
 	/**
 	 * Get the owning entity. Note that the owner is only
 	 * set during the flush cycle, and when a new collection
 	 * wrapper is created while loading an entity.
 	 */
 	public Object getOwner();
 	/**
 	 * Set the reference to the owning entity
 	 */
 	public void setOwner(Object entity);
 	
 	/**
 	 * Is the collection empty? (don't try to initialize the collection)
 	 */
 	public boolean empty();
 
 	/**
 	 * After flushing, re-init snapshot state.
 	 */
 	public void setSnapshot(Serializable key, String role, Serializable snapshot);
 	
 	/**
 	 * After flushing, clear any "queued" additions, since the
 	 * database state is now synchronized with the memory state.
 	 */
 	public void postAction();
 	
 	/**
 	 * return the user-visible collection (or array) instance
 	 */
 	public Object getValue();
 
 	/**
 	 * Called just before reading any rows from the JDBC result set
 	 */
 	public void beginRead();
 
 	/**
 	 * Called after reading all rows from the JDBC result set
 	 */
 	public boolean endRead();
 	
 	/**
 	 * Called after initializing from cache
 	 */
 	public boolean afterInitialize();
 
 	/**
 	 * Could the application possibly have a direct reference to
 	 * the underlying collection implementation?
 	 */
 	public boolean isDirectlyAccessible();
 
 	/**
 	 * Disassociate this collection from the given session.
 	 * @return true if this was currently associated with the given session
 	 */
 	public boolean unsetSession(SessionImplementor currentSession);
 
 	/**
 	 * Associate the collection with the given session.
 	 * @return false if the collection was already associated with the session
 	 * @throws HibernateException if the collection was already associated
 	 * with another open session
 	 */
 	public boolean setCurrentSession(SessionImplementor session)
 			throws HibernateException;
 
 	/**
 	 * Read the state of the collection from a disassembled cached value
 	 */
 	public void initializeFromCache(CollectionPersister persister,
 			Serializable disassembled, Object owner) throws HibernateException;
 
 	/**
 	 * Iterate all collection entries, during update of the database
 	 */
 	public Iterator entries(CollectionPersister persister);
 
 	/**
 	 * Read a row from the JDBC result set
 	 */
 	public Object readFrom(ResultSet rs, CollectionPersister role, CollectionAliases descriptor, Object owner)
 			throws HibernateException, SQLException;
 
 	/**
 	 * Get the index of the given collection entry
 	 */
 	public Object getIdentifier(Object entry, int i);
 	
 	/**
 	 * Get the index of the given collection entry
 	 * @param persister it was more elegant before we added this...
 	 */
 	public Object getIndex(Object entry, int i, CollectionPersister persister);
 	
 	/**
 	 * Get the value of the given collection entry
 	 */
 	public Object getElement(Object entry);
 	
 	/**
 	 * Get the snapshot value of the given collection entry
 	 */
 	public Object getSnapshotElement(Object entry, int i);
 
 	/**
 	 * Called before any elements are read into the collection,
 	 * allowing appropriate initializations to occur.
 	 *
 	 * @param persister The underlying collection persister.
 	 * @param anticipatedSize The anticipated size of the collection after initilization is complete.
 	 */
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize);
 
 	/**
 	 * Does the current state exactly match the snapshot?
 	 */
 	public boolean equalsSnapshot(CollectionPersister persister) 
 		throws HibernateException;
 
 	/**
 	 * Is the snapshot empty?
 	 */
 	public boolean isSnapshotEmpty(Serializable snapshot);
 	
 	/**
 	 * Disassemble the collection, ready for the cache
 	 */
 	public Serializable disassemble(CollectionPersister persister)
 	throws HibernateException;
 
 	/**
 	 * Do we need to completely recreate this collection when it changes?
 	 */
 	public boolean needsRecreate(CollectionPersister persister);
 
 	/**
 	 * Return a new snapshot of the current state of the collection
 	 */
 	public Serializable getSnapshot(CollectionPersister persister)
 			throws HibernateException;
 
 	/**
 	 * To be called internally by the session, forcing
 	 * immediate initialization.
 	 */
 	public void forceInitialization() throws HibernateException;
 
 	/**
 	 * Does an element exist at this entry in the collection?
 	 */
 	public boolean entryExists(Object entry, int i); //note that i parameter is now unused (delete it?)
 
 	/**
 	 * Do we need to insert this element?
 	 */
 	public boolean needsInserting(Object entry, int i, Type elemType)
 			throws HibernateException;
 
 	/**
 	 * Do we need to update this element?
 	 */
 	public boolean needsUpdating(Object entry, int i, Type elemType)
 			throws HibernateException;
 	
 	public boolean isRowUpdatePossible();
 
 	/**
 	 * Get all the elements that need deleting
 	 */
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) 
 			throws HibernateException;
 
 	/**
 	 * Is this the wrapper for the given underlying collection instance?
 	 */
 	public boolean isWrapper(Object collection);
 
 	/**
 	 * Is this instance initialized?
 	 */
 	public boolean wasInitialized();
 
 	/**
 	 * Does this instance have any "queued" additions?
 	 */
 	public boolean hasQueuedOperations();
 
 	/**
 	 * Iterate the "queued" additions
 	 */
 	public Iterator queuedAdditionIterator();
 	
 	/**
 	 * Get the "queued" orphans
 	 */
 	public Collection getQueuedOrphans(String entityName);
 	
 	/**
 	 * Get the current collection key value
 	 */
 	public Serializable getKey();
 	
 	/**
 	 * Get the current role name
 	 */
 	public String getRole();
 	
 	/**
 	 * Is the collection unreferenced?
 	 */
 	public boolean isUnreferenced();
 	
 	/**
 	 * Is the collection dirty? Note that this is only
 	 * reliable during the flush cycle, after the 
 	 * collection elements are dirty checked against
 	 * the snapshot.
 	 */
 	public boolean isDirty();
 	
 	/**
 	 * Clear the dirty flag, after flushing changes
 	 * to the database.
 	 */
 	public void clearDirty();
 	
 	/**
 	 * Get the snapshot cached by the collection
 	 * instance
 	 */
 	public Serializable getStoredSnapshot();
 	
 	/**
 	 * Mark the collection as dirty
 	 */
 	public void dirty();
 	
 	/**
 	 * Called before inserting rows, to ensure that any surrogate keys
 	 * are fully generated
 	 */
 	public void preInsert(CollectionPersister persister)
 	throws HibernateException;
 
 	/**
 	 * Called after inserting a row, to fetch the natively generated id
 	 */
 	public void afterRowInsert(CollectionPersister persister, Object entry, int i) 
 	throws HibernateException;
 
 	/**
 	 * get all "orphaned" elements
 	 */
 	public Collection getOrphans(Serializable snapshot, String entityName)
 	throws HibernateException;
 	
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/BatchFetchQueue.java b/hibernate-core/src/main/java/org/hibernate/engine/BatchFetchQueue.java
index 8af52712d1..62b0523d77 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/BatchFetchQueue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/BatchFetchQueue.java
@@ -1,288 +1,288 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2--8-2011, Red Hat Inc. or third-party contributors as
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
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.cache.spi.CacheKey;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Tracks entity and collection keys that are available for batch
  * fetching, and the queries which were used to load entities, which
  * can be re-used as a subquery for loading owned collections.
  *
  * @author Gavin King
  */
 public class BatchFetchQueue {
 
 	public static final Object MARKER = new MarkerObject( "MARKER" );
 
 	/**
 	 * Defines a sequence of {@link EntityKey} elements that are currently
 	 * elegible for batch-fetching.
 	 * <p/>
 	 * Even though this is a map, we only use the keys.  A map was chosen in
 	 * order to utilize a {@link LinkedHashMap} to maintain sequencing
 	 * as well as uniqueness.
 	 * <p/>
 	 * TODO : this would be better as a SequencedReferenceSet, but no such beast exists!
 	 */
 	private final Map batchLoadableEntityKeys = new LinkedHashMap(8);
 
 	/**
 	 * A map of {@link SubselectFetch subselect-fetch descriptors} keyed by the
 	 * {@link EntityKey) against which the descriptor is registered.
 	 */
 	private final Map subselectsByEntityKey = new HashMap(8);
 
 	/**
 	 * The owning persistence context.
 	 */
 	private final PersistenceContext context;
 
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
 	 */
 	public void clear() {
 		batchLoadableEntityKeys.clear();
 		subselectsByEntityKey.clear();
 	}
 
 	/**
 	 * Retrieve the fetch descriptor associated with the given entity key.
 	 *
 	 * @param key The entity key for which to locate any defined subselect fetch.
 	 * @return The fetch descriptor; may return null if no subselect fetch queued for
 	 * this entity key.
 	 */
 	public SubselectFetch getSubselect(EntityKey key) {
 		return (SubselectFetch) subselectsByEntityKey.get(key);
 	}
 
 	/**
 	 * Adds a subselect fetch decriptor for the given entity key.
 	 *
 	 * @param key The entity for which to register the subselect fetch.
 	 * @param subquery The fetch descriptor.
 	 */
 	public void addSubselect(EntityKey key, SubselectFetch subquery) {
 		subselectsByEntityKey.put(key, subquery);
 	}
 
 	/**
 	 * After evicting or deleting an entity, we don't need to
 	 * know the query that was used to load it anymore (don't
 	 * call this after loading the entity, since we might still
 	 * need to load its collections)
 	 */
 	public void removeSubselect(EntityKey key) {
 		subselectsByEntityKey.remove(key);
 	}
 
 	/**
 	 * Clears all pending subselect fetches from the queue.
 	 * <p/>
 	 * Called after flushing.
 	 */
 	public void clearSubselects() {
 		subselectsByEntityKey.clear();
 	}
 
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
 			batchLoadableEntityKeys.put( key, MARKER );
 		}
 	}
 
 	/**
 	 * After evicting or deleting or loading an entity, we don't
 	 * need to batch fetch it anymore, remove it from the queue
 	 * if necessary
 	 */
 	public void removeBatchLoadableEntityKey(EntityKey key) {
 		if ( key.isBatchLoadable() ) batchLoadableEntityKeys.remove(key);
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
 			final int batchSize,
 			final EntityMode entityMode) {
 		Serializable[] keys = new Serializable[batchSize];
 		keys[0] = id;
 		int i = 1;
 		//int count = 0;
 		int end = -1;
 		boolean checkForEnd = false;
 		// this only works because collection entries are kept in a sequenced
 		// map by persistence context (maybe we should do like entities and
 		// keep a separate sequences set...)
 		Iterator iter = context.getCollectionEntries().entrySet().iterator(); //TODO: calling entrySet on an IdentityMap is SLOW!!
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 
 			CollectionEntry ce = (CollectionEntry) me.getValue();
 			PersistentCollection collection = (PersistentCollection) me.getKey();
 			if ( !collection.wasInitialized() && ce.getLoadedPersister() == collectionPersister ) {
 
 				if ( checkForEnd && i == end ) {
 					return keys; //the first key found after the given key
 				}
 
 				//if ( end == -1 && count > batchSize*10 ) return keys; //try out ten batches, max
 
 				final boolean isEqual = collectionPersister.getKeyType().isEqual(
 						id,
 						ce.getLoadedKey(),
 						entityMode,
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
 
 		Iterator iter = batchLoadableEntityKeys.keySet().iterator();
 		while ( iter.hasNext() ) {
 			EntityKey key = (EntityKey) iter.next();
 			if ( key.getEntityName().equals( persister.getEntityName() ) ) { //TODO: this needn't exclude subclasses...
 				if ( checkForEnd && i == end ) {
 					//the first id found after the given id
 					return ids;
 				}
 				if ( persister.getIdentifierType().isEqual( id, key.getIdentifier(), entityMode ) ) {
 					end = i;
 				}
 				else {
 					if ( !isCached( key, persister ) ) {
 						ids[i++] = key.getIdentifier();
 					}
 				}
 				if ( i == batchSize ) {
 					i = 1; //end of array, start filling again from start
 					if (end!=-1) checkForEnd = true;
 				}
 			}
 		}
 		return ids; //we ran out of ids to try
 	}
 
 	private boolean isCached(EntityKey entityKey, EntityPersister persister) {
 		if ( persister.hasCache() ) {
 			CacheKey key = context.getSession().generateCacheKey(
 					entityKey.getIdentifier(),
 					persister.getIdentifierType(),
 					entityKey.getEntityName()
 			);
 			return persister.getCacheAccessStrategy().get( key, context.getSession().getTimestamp() ) != null;
 		}
 		return false;
 	}
 
 	private boolean isCached(Serializable collectionKey, CollectionPersister persister) {
 		if ( persister.hasCache() ) {
 			CacheKey cacheKey = context.getSession().generateCacheKey(
 					collectionKey,
 			        persister.getKeyType(),
 			        persister.getRole()
 			);
 			return persister.getCacheAccessStrategy().get( cacheKey, context.getSession().getTimestamp() ) != null;
 		}
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/Cascade.java b/hibernate-core/src/main/java/org/hibernate/engine/Cascade.java
index 53fc0541d2..a28e37a773 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/Cascade.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/Cascade.java
@@ -1,474 +1,474 @@
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
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Stack;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.event.EventSource;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
 /**
  * Delegate responsible for, in conjunction with the various
  * {@link CascadingAction actions}, implementing cascade processing.
  *
  * @author Gavin King
  * @see CascadingAction
  */
 public final class Cascade {
 
 	/**
 	 * A cascade point that occurs just after the insertion of the parent entity and
 	 * just before deletion
 	 */
 	public static final int AFTER_INSERT_BEFORE_DELETE = 1;
 	/**
 	 * A cascade point that occurs just before the insertion of the parent entity and
 	 * just after deletion
 	 */
 	public static final int BEFORE_INSERT_AFTER_DELETE = 2;
 	/**
 	 * A cascade point that occurs just after the insertion of the parent entity and
 	 * just before deletion, inside a collection
 	 */
 	public static final int AFTER_INSERT_BEFORE_DELETE_VIA_COLLECTION = 3;
 	/**
 	 * A cascade point that occurs just after update of the parent entity
 	 */
 	public static final int AFTER_UPDATE = 0;
 	/**
 	 * A cascade point that occurs just before the session is flushed
 	 */
 	public static final int BEFORE_FLUSH = 0;
 	/**
 	 * A cascade point that occurs just after eviction of the parent entity from the
 	 * session cache
 	 */
 	public static final int AFTER_EVICT = 0;
 	/**
 	 * A cascade point that occurs just after locking a transient parent entity into the
 	 * session cache
 	 */
 	public static final int BEFORE_REFRESH = 0;
 	/**
 	 * A cascade point that occurs just after refreshing a parent entity
 	 */
 	public static final int AFTER_LOCK = 0;
 	/**
 	 * A cascade point that occurs just before merging from a transient parent entity into
 	 * the object in the session cache
 	 */
 	public static final int BEFORE_MERGE = 0;
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Cascade.class.getName());
 
 
 	private int cascadeTo;
 	private EventSource eventSource;
 	private CascadingAction action;
 
 	public Cascade(final CascadingAction action, final int cascadeTo, final EventSource eventSource) {
 		this.cascadeTo = cascadeTo;
 		this.eventSource = eventSource;
 		this.action = action;
 	}
 
 	private SessionFactoryImplementor getFactory() {
 		return eventSource.getFactory();
 	}
 
 	/**
 	 * Cascade an action from the parent entity instance to all its children.
 	 *
 	 * @param persister The parent's entity persister
 	 * @param parent The parent reference.
 	 * @throws HibernateException
 	 */
 	public void cascade(final EntityPersister persister, final Object parent)
 	throws HibernateException {
 		cascade( persister, parent, null );
 	}
 
 	/**
 	 * Cascade an action from the parent entity instance to all its children.  This
 	 * form is typicaly called from within cascade actions.
 	 *
 	 * @param persister The parent's entity persister
 	 * @param parent The parent reference.
 	 * @param anything Anything ;)   Typically some form of cascade-local cache
 	 * which is specific to each CascadingAction type
 	 * @throws HibernateException
 	 */
 	public void cascade(final EntityPersister persister, final Object parent, final Object anything)
 			throws HibernateException {
 
 		if ( persister.hasCascades() || action.requiresNoCascadeChecking() ) { // performance opt
             LOG.trace("Processing cascade " + action + " for: " + persister.getEntityName());
 
 			Type[] types = persister.getPropertyTypes();
 			CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();
 			EntityMode entityMode = eventSource.getEntityMode();
 			boolean hasUninitializedLazyProperties = persister.hasUninitializedLazyProperties( parent, entityMode );
 			for ( int i=0; i<types.length; i++) {
 				final CascadeStyle style = cascadeStyles[i];
 				final String propertyName = persister.getPropertyNames()[i];
 				if ( hasUninitializedLazyProperties && persister.getPropertyLaziness()[i] && ! action.performOnLazyProperty() ) {
 					//do nothing to avoid a lazy property initialization
 					continue;
 				}
 
 				if ( style.doCascade( action ) ) {
 					cascadeProperty(
 						    parent,
 					        persister.getPropertyValue( parent, i, entityMode ),
 					        types[i],
 					        style,
 							propertyName,
 					        anything,
 					        false
 					);
 				}
 				else if ( action.requiresNoCascadeChecking() ) {
 					action.noCascade(
 							eventSource,
 							persister.getPropertyValue( parent, i, entityMode ),
 							parent,
 							persister,
 							i
 					);
 				}
 			}
 
             LOG.trace("Done processing cascade " + action + " for: " + persister.getEntityName());
 		}
 	}
 
 	/**
 	 * Cascade an action to the child or children
 	 */
 	private void cascadeProperty(
 			final Object parent,
 			final Object child,
 			final Type type,
 			final CascadeStyle style,
 			final String propertyName,
 			final Object anything,
 			final boolean isCascadeDeleteEnabled) throws HibernateException {
 
 		if (child!=null) {
 			if ( type.isAssociationType() ) {
 				AssociationType associationType = (AssociationType) type;
 				if ( cascadeAssociationNow( associationType ) ) {
 					cascadeAssociation(
 							parent,
 							child,
 							type,
 							style,
 							anything,
 							isCascadeDeleteEnabled
 						);
 				}
 			}
 			else if ( type.isComponentType() ) {
 				cascadeComponent( parent, child, (CompositeType) type, propertyName, anything );
 			}
 		}
 		else {
 			// potentially we need to handle orphan deletes for one-to-ones here...
 			if ( isLogicalOneToOne( type ) ) {
 				// We have a physical or logical one-to-one and from previous checks we know we
 				// have a null value.  See if the attribute cascade settings and action-type require
 				// orphan checking
 				if ( style.hasOrphanDelete() && action.deleteOrphans() ) {
 					// value is orphaned if loaded state for this property shows not null
 					// because it is currently null.
 					final EntityEntry entry = eventSource.getPersistenceContext().getEntry( parent );
 					if ( entry != null && entry.getStatus() != Status.SAVING ) {
 						final Object loadedValue;
 						if ( componentPathStack.isEmpty() ) {
 							// association defined on entity
 							loadedValue = entry.getLoadedValue( propertyName );
 						}
 						else {
 							// association defined on component
 							// 		todo : this is currently unsupported because of the fact that
 							//		we do not know the loaded state of this value properly
 							//		and doing so would be very difficult given how components and
 							//		entities are loaded (and how 'loaded state' is put into the
 							//		EntityEntry).  Solutions here are to either:
 							//			1) properly account for components as a 2-phase load construct
 							//			2) just assume the association was just now orphaned and
 							// 				issue the orphan delete.  This would require a special
 							//				set of SQL statements though since we do not know the
 							//				orphaned value, something a delete with a subquery to
 							// 				match the owner.
 //							final EntityType entityType = (EntityType) type;
 //							final String propertyPath = composePropertyPath( entityType.getPropertyName() );
 							loadedValue = null;
 						}
 						if ( loadedValue != null ) {
 							final String entityName = entry.getPersister().getEntityName();
                             if (LOG.isTraceEnabled()) {
 								final Serializable id = entry.getPersister().getIdentifier( loadedValue, eventSource );
 								final String description = MessageHelper.infoString( entityName, id );
                                 LOG.trace("Deleting orphaned entity instance: " + description);
 							}
 							eventSource.delete( entityName, loadedValue, false, new HashSet() );
 						}
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Check if the association is a one to one in the logical model (either a shared-pk
 	 * or unique fk).
 	 *
 	 * @param type The type representing the attribute metadata
 	 *
 	 * @return True if the attribute represents a logical one to one association
 	 */
 	private boolean isLogicalOneToOne(Type type) {
 		return type.isEntityType() && ( (EntityType) type ).isLogicalOneToOne();
 	}
 
 	private String composePropertyPath(String propertyName) {
 		if ( componentPathStack.isEmpty() ) {
 			return propertyName;
 		}
 		else {
 			StringBuffer buffer = new StringBuffer();
 			Iterator itr = componentPathStack.iterator();
 			while ( itr.hasNext() ) {
 				buffer.append( itr.next() ).append( '.' );
 			}
 			buffer.append( propertyName );
 			return buffer.toString();
 		}
 	}
 
 	private Stack componentPathStack = new Stack();
 
 	private boolean cascadeAssociationNow(AssociationType associationType) {
 		return associationType.getForeignKeyDirection().cascadeNow(cascadeTo) &&
 			( eventSource.getEntityMode()!=EntityMode.DOM4J || associationType.isEmbeddedInXML() );
 	}
 
 	private void cascadeComponent(
 			final Object parent,
 			final Object child,
 			final CompositeType componentType,
 			final String componentPropertyName,
 			final Object anything) {
 		componentPathStack.push( componentPropertyName );
 		Object[] children = componentType.getPropertyValues( child, eventSource );
 		Type[] types = componentType.getSubtypes();
 		for ( int i=0; i<types.length; i++ ) {
 			final CascadeStyle componentPropertyStyle = componentType.getCascadeStyle(i);
 			final String subPropertyName = componentType.getPropertyNames()[i];
 			if ( componentPropertyStyle.doCascade(action) ) {
 				cascadeProperty(
 						parent,
 						children[i],
 						types[i],
 						componentPropertyStyle,
 						subPropertyName,
 						anything,
 						false
 					);
 			}
 		}
 		componentPathStack.pop();
 	}
 
 	private void cascadeAssociation(
 			final Object parent,
 			final Object child,
 			final Type type,
 			final CascadeStyle style,
 			final Object anything,
 			final boolean isCascadeDeleteEnabled) {
 		if ( type.isEntityType() || type.isAnyType() ) {
 			cascadeToOne( parent, child, type, style, anything, isCascadeDeleteEnabled );
 		}
 		else if ( type.isCollectionType() ) {
 			cascadeCollection( parent, child, style, anything, (CollectionType) type );
 		}
 	}
 
 	/**
 	 * Cascade an action to a collection
 	 */
 	private void cascadeCollection(
 			final Object parent,
 			final Object child,
 			final CascadeStyle style,
 			final Object anything,
 			final CollectionType type) {
 		CollectionPersister persister = eventSource.getFactory()
 				.getCollectionPersister( type.getRole() );
 		Type elemType = persister.getElementType();
 
 		final int oldCascadeTo = cascadeTo;
 		if ( cascadeTo==AFTER_INSERT_BEFORE_DELETE) {
 			cascadeTo = AFTER_INSERT_BEFORE_DELETE_VIA_COLLECTION;
 		}
 
 		//cascade to current collection elements
 		if ( elemType.isEntityType() || elemType.isAnyType() || elemType.isComponentType() ) {
 			cascadeCollectionElements(
 				parent,
 				child,
 				type,
 				style,
 				elemType,
 				anything,
 				persister.isCascadeDeleteEnabled()
 			);
 		}
 
 		cascadeTo = oldCascadeTo;
 	}
 
 	/**
 	 * Cascade an action to a to-one association or any type
 	 */
 	private void cascadeToOne(
 			final Object parent,
 			final Object child,
 			final Type type,
 			final CascadeStyle style,
 			final Object anything,
 			final boolean isCascadeDeleteEnabled) {
 		final String entityName = type.isEntityType()
 				? ( (EntityType) type ).getAssociatedEntityName()
 				: null;
 		if ( style.reallyDoCascade(action) ) { //not really necessary, but good for consistency...
 			eventSource.getPersistenceContext().addChildParent(child, parent);
 			try {
 				action.cascade(eventSource, child, entityName, anything, isCascadeDeleteEnabled);
 			}
 			finally {
 				eventSource.getPersistenceContext().removeChildParent(child);
 			}
 		}
 	}
 
 	/**
 	 * Cascade to the collection elements
 	 */
 	private void cascadeCollectionElements(
 			final Object parent,
 			final Object child,
 			final CollectionType collectionType,
 			final CascadeStyle style,
 			final Type elemType,
 			final Object anything,
 			final boolean isCascadeDeleteEnabled) throws HibernateException {
 		// we can't cascade to non-embedded elements
 		boolean embeddedElements = eventSource.getEntityMode()!=EntityMode.DOM4J ||
 				( (EntityType) collectionType.getElementType( eventSource.getFactory() ) ).isEmbeddedInXML();
 
 		boolean reallyDoCascade = style.reallyDoCascade(action) &&
 			embeddedElements && child!=CollectionType.UNFETCHED_COLLECTION;
 
 		if ( reallyDoCascade ) {
             LOG.trace("Cascade " + action + " for collection: " + collectionType.getRole());
 
 			Iterator iter = action.getCascadableChildrenIterator(eventSource, collectionType, child);
 			while ( iter.hasNext() ) {
 				cascadeProperty(
 						parent,
 						iter.next(),
 						elemType,
 						style,
 						null,
 						anything,
 						isCascadeDeleteEnabled
 					);
 			}
 
             LOG.trace("Done cascade " + action + " for collection: " + collectionType.getRole());
 		}
 
 		final boolean deleteOrphans = style.hasOrphanDelete() &&
 				action.deleteOrphans() &&
 				elemType.isEntityType() &&
 				child instanceof PersistentCollection; //a newly instantiated collection can't have orphans
 
 		if ( deleteOrphans ) { // handle orphaned entities!!
             LOG.trace("Deleting orphans for collection: " + collectionType.getRole());
 
 			// we can do the cast since orphan-delete does not apply to:
 			// 1. newly instantiated collections
 			// 2. arrays (we can't track orphans for detached arrays)
 			final String entityName = collectionType.getAssociatedEntityName( eventSource.getFactory() );
 			deleteOrphans( entityName, (PersistentCollection) child );
 
             LOG.trace("Done deleting orphans for collection: " + collectionType.getRole());
 		}
 	}
 
 	/**
 	 * Delete any entities that were removed from the collection
 	 */
 	private void deleteOrphans(String entityName, PersistentCollection pc) throws HibernateException {
 		//TODO: suck this logic into the collection!
 		final Collection orphans;
 		if ( pc.wasInitialized() ) {
 			CollectionEntry ce = eventSource.getPersistenceContext().getCollectionEntry(pc);
 			orphans = ce==null ?
 					CollectionHelper.EMPTY_COLLECTION :
 					ce.getOrphans(entityName, pc);
 		}
 		else {
 			orphans = pc.getQueuedOrphans(entityName);
 		}
 
 		final Iterator orphanIter = orphans.iterator();
 		while ( orphanIter.hasNext() ) {
 			Object orphan = orphanIter.next();
 			if (orphan!=null) {
                 LOG.trace("Deleting orphaned entity instance: " + entityName);
 				eventSource.delete( entityName, orphan, false, new HashSet() );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/CascadingAction.java b/hibernate-core/src/main/java/org/hibernate/engine/CascadingAction.java
index 91d83f95ad..487d6c2842 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/CascadingAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/CascadingAction.java
@@ -1,468 +1,468 @@
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
 package org.hibernate.engine;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.ReplicationMode;
 import org.hibernate.TransientObjectException;
-import org.hibernate.collection.PersistentCollection;
 import org.hibernate.event.EventSource;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
 /**
  * A session action that may be cascaded from parent entity to its children
  *
  * @author Gavin King
  */
 public abstract class CascadingAction {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CascadingAction.class.getName());
 
 
 	// the CascadingAction contract ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * protected constructor
 	 */
 	CascadingAction() {
 	}
 
 	/**
 	 * Cascade the action to the child object.
 	 *
 	 * @param session The session within which the cascade is occuring.
 	 * @param child The child to which cascading should be performed.
 	 * @param entityName The child's entity name
 	 * @param anything Anything ;)  Typically some form of cascade-local cache
 	 * which is specific to each CascadingAction type
 	 * @param isCascadeDeleteEnabled Are cascading deletes enabled.
 	 * @throws HibernateException
 	 */
 	public abstract void cascade(
 			EventSource session,
 			Object child,
 			String entityName,
 			Object anything,
 			boolean isCascadeDeleteEnabled) throws HibernateException;
 
 	/**
 	 * Given a collection, get an iterator of the children upon which the
 	 * current cascading action should be visited.
 	 *
 	 * @param session The session within which the cascade is occuring.
 	 * @param collectionType The mapping type of the collection.
 	 * @param collection The collection instance.
 	 * @return The children iterator.
 	 */
 	public abstract Iterator getCascadableChildrenIterator(
 			EventSource session,
 			CollectionType collectionType,
 			Object collection);
 
 	/**
 	 * Does this action potentially extrapolate to orphan deletes?
 	 *
 	 * @return True if this action can lead to deletions of orphans.
 	 */
 	public abstract boolean deleteOrphans();
 
 
 	/**
 	 * Does the specified cascading action require verification of no cascade validity?
 	 *
 	 * @return True if this action requires no-cascade verification; false otherwise.
 	 */
 	public boolean requiresNoCascadeChecking() {
 		return false;
 	}
 
 	/**
 	 * Called (in the case of {@link #requiresNoCascadeChecking} returning true) to validate
 	 * that no cascade on the given property is considered a valid semantic.
 	 *
 	 * @param session The session witin which the cascade is occurring.
 	 * @param child The property value
 	 * @param parent The property value owner
 	 * @param persister The entity persister for the owner
 	 * @param propertyIndex The index of the property within the owner.
 	 */
 	public void noCascade(EventSource session, Object child, Object parent, EntityPersister persister, int propertyIndex) {
 	}
 
 	/**
 	 * Should this action be performed (or noCascade consulted) in the case of lazy properties.
 	 */
 	public boolean performOnLazyProperty() {
 		return true;
 	}
 
 
 	// the CascadingAction implementations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * @see org.hibernate.Session#delete(Object)
 	 */
 	public static final CascadingAction DELETE = new CascadingAction() {
 		@Override
         public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
 		throws HibernateException {
             LOG.trace("Cascading to delete: " + entityName);
 			session.delete( entityName, child, isCascadeDeleteEnabled, ( Set ) anything );
 		}
 		@Override
         public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
 			// delete does cascade to uninitialized collections
 			return CascadingAction.getAllElementsIterator(session, collectionType, collection);
 		}
 		@Override
         public boolean deleteOrphans() {
 			// orphans should be deleted during delete
 			return true;
 		}
 		@Override
         public String toString() {
 			return "ACTION_DELETE";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#lock(Object, LockMode)
 	 */
 	public static final CascadingAction LOCK = new CascadingAction() {
 		@Override
         public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
 		throws HibernateException {
             LOG.trace("Cascading to lock: " + entityName);
 			LockMode lockMode = LockMode.NONE;
 			LockOptions lr = new LockOptions();
 			if ( anything instanceof LockOptions) {
 				LockOptions lockOptions = (LockOptions)anything;
 				lr.setTimeOut(lockOptions.getTimeOut());
 				lr.setScope( lockOptions.getScope());
 				if ( lockOptions.getScope() == true )	// cascade specified lockMode
 					lockMode = lockOptions.getLockMode();
 			}
 			lr.setLockMode(lockMode);
 			session.buildLockRequest(lr).lock(entityName, child);
 		}
 		@Override
         public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
 			// lock doesn't cascade to uninitialized collections
 			return getLoadedElementsIterator(session, collectionType, collection);
 		}
 		@Override
         public boolean deleteOrphans() {
 			//TODO: should orphans really be deleted during lock???
 			return false;
 		}
 		@Override
         public String toString() {
 			return "ACTION_LOCK";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#refresh(Object)
 	 */
 	public static final CascadingAction REFRESH = new CascadingAction() {
 		@Override
         public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
 		throws HibernateException {
             LOG.trace("Cascading to refresh: " + entityName);
 			session.refresh( child, (Map) anything );
 		}
 		@Override
         public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
 			// refresh doesn't cascade to uninitialized collections
 			return getLoadedElementsIterator(session, collectionType, collection);
 		}
 		@Override
         public boolean deleteOrphans() {
 			return false;
 		}
 		@Override
         public String toString() {
 			return "ACTION_REFRESH";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#evict(Object)
 	 */
 	public static final CascadingAction EVICT = new CascadingAction() {
 		@Override
         public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
 		throws HibernateException {
             LOG.trace("Cascading to evict: " + entityName);
 			session.evict(child);
 		}
 		@Override
         public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
 			// evicts don't cascade to uninitialized collections
 			return getLoadedElementsIterator(session, collectionType, collection);
 		}
 		@Override
         public boolean deleteOrphans() {
 			return false;
 		}
 		@Override
         public boolean performOnLazyProperty() {
 			return false;
 		}
 		@Override
         public String toString() {
 			return "ACTION_EVICT";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#saveOrUpdate(Object)
 	 */
 	public static final CascadingAction SAVE_UPDATE = new CascadingAction() {
 		@Override
         public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
 		throws HibernateException {
             LOG.trace("Cascading to save or update: " + entityName);
 			session.saveOrUpdate(entityName, child);
 		}
 		@Override
         public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
 			// saves / updates don't cascade to uninitialized collections
 			return getLoadedElementsIterator(session, collectionType, collection);
 		}
 		@Override
         public boolean deleteOrphans() {
 			// orphans should be deleted during save/update
 			return true;
 		}
 		@Override
         public boolean performOnLazyProperty() {
 			return false;
 		}
 		@Override
         public String toString() {
 			return "ACTION_SAVE_UPDATE";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#merge(Object)
 	 */
 	public static final CascadingAction MERGE = new CascadingAction() {
 		@Override
         public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
 		throws HibernateException {
             LOG.trace("Cascading to merge: " + entityName);
 			session.merge( entityName, child, (Map) anything );
 		}
 		@Override
         public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
 			// merges don't cascade to uninitialized collections
 //			//TODO: perhaps this does need to cascade after all....
 			return getLoadedElementsIterator(session, collectionType, collection);
 		}
 		@Override
         public boolean deleteOrphans() {
 			// orphans should not be deleted during merge??
 			return false;
 		}
 		@Override
         public String toString() {
 			return "ACTION_MERGE";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#persist(Object)
 	 */
 	public static final CascadingAction PERSIST = new CascadingAction() {
 		@Override
         public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
 		throws HibernateException {
             LOG.trace("Cascading to persist: " + entityName);
 			session.persist( entityName, child, (Map) anything );
 		}
 		@Override
         public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
 			// persists don't cascade to uninitialized collections
 			return CascadingAction.getAllElementsIterator(session, collectionType, collection);
 		}
 		@Override
         public boolean deleteOrphans() {
 			return false;
 		}
 		@Override
         public boolean performOnLazyProperty() {
 			return false;
 		}
 		@Override
         public String toString() {
 			return "ACTION_PERSIST";
 		}
 	};
 
 	/**
 	 * Execute persist during flush time
 	 *
 	 * @see org.hibernate.Session#persist(Object)
 	 */
 	public static final CascadingAction PERSIST_ON_FLUSH = new CascadingAction() {
 		@Override
         public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
 		throws HibernateException {
             LOG.trace("Cascading to persist on flush: " + entityName);
 			session.persistOnFlush( entityName, child, (Map) anything );
 		}
 		@Override
         public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
 			// persists don't cascade to uninitialized collections
 			return CascadingAction.getLoadedElementsIterator(session, collectionType, collection);
 		}
 		@Override
         public boolean deleteOrphans() {
 			return true;
 		}
 		@Override
         public boolean requiresNoCascadeChecking() {
 			return true;
 		}
 		@Override
         public void noCascade(
 				EventSource session,
 				Object child,
 				Object parent,
 				EntityPersister persister,
 				int propertyIndex) {
 			if ( child == null ) {
 				return;
 			}
 			Type type = persister.getPropertyTypes()[propertyIndex];
 			if ( type.isEntityType() ) {
 				String childEntityName = ( ( EntityType ) type ).getAssociatedEntityName( session.getFactory() );
 
 				if ( ! isInManagedState( child, session )
 						&& ! ( child instanceof HibernateProxy ) //a proxy cannot be transient and it breaks ForeignKeys.isTransient
 						&& ForeignKeys.isTransient( childEntityName, child, null, session ) ) {
 					String parentEntiytName = persister.getEntityName();
 					String propertyName = persister.getPropertyNames()[propertyIndex];
 					throw new TransientObjectException(
 							"object references an unsaved transient instance - " +
 							"save the transient instance before flushing: " +
 							parentEntiytName + "." + propertyName + " -> " + childEntityName
 					);
 
 				}
 			}
 		}
 		@Override
         public boolean performOnLazyProperty() {
 			return false;
 		}
 
 		private boolean isInManagedState(Object child, EventSource session) {
 			EntityEntry entry = session.getPersistenceContext().getEntry( child );
 			return entry != null && (entry.getStatus() == Status.MANAGED || entry.getStatus() == Status.READ_ONLY);
 		}
 
 		@Override
         public String toString() {
 			return "ACTION_PERSIST_ON_FLUSH";
 		}
 	};
 
 	/**
 	 * @see org.hibernate.Session#replicate(Object, org.hibernate.ReplicationMode)
 	 */
 	public static final CascadingAction REPLICATE = new CascadingAction() {
 		@Override
         public void cascade(EventSource session, Object child, String entityName, Object anything, boolean isCascadeDeleteEnabled)
 		throws HibernateException {
             LOG.trace("Cascading to replicate: " + entityName);
 			session.replicate( entityName, child, (ReplicationMode) anything );
 		}
 		@Override
         public Iterator getCascadableChildrenIterator(EventSource session, CollectionType collectionType, Object collection) {
 			// replicate does cascade to uninitialized collections
 			return getLoadedElementsIterator(session, collectionType, collection);
 		}
 		@Override
         public boolean deleteOrphans() {
 			return false; //I suppose?
 		}
 		@Override
         public String toString() {
 			return "ACTION_REPLICATE";
 		}
 	};
 
 
 	// static helper methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Given a collection, get an iterator of all its children, loading them
 	 * from the database if necessary.
 	 *
 	 * @param session The session within which the cascade is occuring.
 	 * @param collectionType The mapping type of the collection.
 	 * @param collection The collection instance.
 	 * @return The children iterator.
 	 */
 	private static Iterator getAllElementsIterator(
 			EventSource session,
 			CollectionType collectionType,
 			Object collection) {
 		return collectionType.getElementsIterator( collection, session );
 	}
 
 	/**
 	 * Iterate just the elements of the collection that are already there. Don't load
 	 * any new elements from the database.
 	 */
 	public static Iterator getLoadedElementsIterator(SessionImplementor session, CollectionType collectionType, Object collection) {
 		if ( collectionIsInitialized(collection) ) {
 			// handles arrays and newly instantiated collections
 			return collectionType.getElementsIterator(collection, session);
 		}
 		else {
 			// does not handle arrays (thats ok, cos they can't be lazy)
 			// or newly instantiated collections, so we can do the cast
 			return ( (PersistentCollection) collection ).queuedAdditionIterator();
 		}
 	}
 
 	private static boolean collectionIsInitialized(Object collection) {
 		return !(collection instanceof PersistentCollection) || ( (PersistentCollection) collection ).wasInitialized();
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/CollectionEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/CollectionEntry.java
index deea980558..fc556ed9d9 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/CollectionEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/CollectionEntry.java
@@ -1,416 +1,416 @@
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
 package org.hibernate.engine;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.Collection;
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
-import org.hibernate.collection.PersistentCollection;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.jboss.logging.Logger;
 
 /**
  * We need an entry to tell us all about the current state
  * of a collection with respect to its persistent state
  *
  * @author Gavin King
  */
 public final class CollectionEntry implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionEntry.class.getName());
 
 	//ATTRIBUTES MAINTAINED BETWEEN FLUSH CYCLES
 
 	// session-start/post-flush persistent state
 	private Serializable snapshot;
 	// allow the CollectionSnapshot to be serialized
 	private String role;
 
 	// "loaded" means the reference that is consistent
 	// with the current database state
 	private transient CollectionPersister loadedPersister;
 	private Serializable loadedKey;
 
 	// ATTRIBUTES USED ONLY DURING FLUSH CYCLE
 
 	// during flush, we navigate the object graph to
 	// collections and decide what to do with them
 	private transient boolean reached;
 	private transient boolean processed;
 	private transient boolean doupdate;
 	private transient boolean doremove;
 	private transient boolean dorecreate;
 	// if we instantiate a collection during the flush() process,
 	// we must ignore it for the rest of the flush()
 	private transient boolean ignore;
 
 	// "current" means the reference that was found during flush()
 	private transient CollectionPersister currentPersister;
 	private transient Serializable currentKey;
 
 	/**
 	 * For newly wrapped collections, or dereferenced collection wrappers
 	 */
 	public CollectionEntry(CollectionPersister persister, PersistentCollection collection) {
 		// new collections that get found + wrapped
 		// during flush shouldn't be ignored
 		ignore = false;
 
 		collection.clearDirty(); //a newly wrapped collection is NOT dirty (or we get unnecessary version updates)
 
 		snapshot = persister.isMutable() ?
 				collection.getSnapshot(persister) :
 				null;
 		collection.setSnapshot(loadedKey, role, snapshot);
 	}
 
 	/**
 	 * For collections just loaded from the database
 	 */
 	public CollectionEntry(
 			final PersistentCollection collection,
 			final CollectionPersister loadedPersister,
 			final Serializable loadedKey,
 			final boolean ignore
 	) {
 		this.ignore=ignore;
 
 		//collection.clearDirty()
 
 		this.loadedKey = loadedKey;
 		setLoadedPersister(loadedPersister);
 
 		collection.setSnapshot(loadedKey, role, null);
 
 		//postInitialize() will be called after initialization
 	}
 
 	/**
 	 * For uninitialized detached collections
 	 */
 	public CollectionEntry(CollectionPersister loadedPersister, Serializable loadedKey) {
 		// detached collection wrappers that get found + reattached
 		// during flush shouldn't be ignored
 		ignore = false;
 
 		//collection.clearDirty()
 
 		this.loadedKey = loadedKey;
 		setLoadedPersister(loadedPersister);
 	}
 
 	/**
 	 * For initialized detached collections
 	 */
 	CollectionEntry(PersistentCollection collection, SessionFactoryImplementor factory)
 	throws MappingException {
 		// detached collections that get found + reattached
 		// during flush shouldn't be ignored
 		ignore = false;
 
 		loadedKey = collection.getKey();
 		setLoadedPersister( factory.getCollectionPersister( collection.getRole() ) );
 
 		snapshot = collection.getStoredSnapshot();
 	}
 
 	/**
 	 * Used from custom serialization.
 	 *
 	 * @see #serialize
 	 * @see #deserialize
 	 */
 	private CollectionEntry(
 			String role,
 	        Serializable snapshot,
 	        Serializable loadedKey,
 	        SessionFactoryImplementor factory) {
 		this.role = role;
 		this.snapshot = snapshot;
 		this.loadedKey = loadedKey;
 		if ( role != null ) {
 			afterDeserialize( factory );
 		}
 	}
 
 	/**
 	 * Determine if the collection is "really" dirty, by checking dirtiness
 	 * of the collection elements, if necessary
 	 */
 	private void dirty(PersistentCollection collection) throws HibernateException {
 
 		boolean forceDirty = collection.wasInitialized() &&
 				!collection.isDirty() && //optimization
 				getLoadedPersister() != null &&
 				getLoadedPersister().isMutable() && //optimization
 				( collection.isDirectlyAccessible() || getLoadedPersister().getElementType().isMutable() ) && //optimization
 				!collection.equalsSnapshot( getLoadedPersister() );
 
 		if ( forceDirty ) {
 			collection.dirty();
 		}
 
 	}
 
 	public void preFlush(PersistentCollection collection) throws HibernateException {
 
 		boolean nonMutableChange = collection.isDirty() &&
 				getLoadedPersister()!=null &&
 				!getLoadedPersister().isMutable();
 		if (nonMutableChange) {
 			throw new HibernateException(
 					"changed an immutable collection instance: " +
 					MessageHelper.collectionInfoString( getLoadedPersister().getRole(), getLoadedKey() )
 				);
 		}
 
 		dirty(collection);
 
         if (LOG.isDebugEnabled() && collection.isDirty() && getLoadedPersister() != null) LOG.debugf("Collection dirty: %s",
                                                                                                      MessageHelper.collectionInfoString(getLoadedPersister().getRole(),
                                                                                                                                         getLoadedKey()));
 
 		setDoupdate(false);
 		setDoremove(false);
 		setDorecreate(false);
 		setReached(false);
 		setProcessed(false);
 	}
 
 	public void postInitialize(PersistentCollection collection) throws HibernateException {
 		snapshot = getLoadedPersister().isMutable() ?
 				collection.getSnapshot( getLoadedPersister() ) :
 				null;
 		collection.setSnapshot(loadedKey, role, snapshot);
 	}
 
 	/**
 	 * Called after a successful flush
 	 */
 	public void postFlush(PersistentCollection collection) throws HibernateException {
 		if ( isIgnore() ) {
 			ignore = false;
 		}
 		else if ( !isProcessed() ) {
 			throw new AssertionFailure( "collection [" + collection.getRole() + "] was not processed by flush()" );
 		}
 		collection.setSnapshot(loadedKey, role, snapshot);
 	}
 
 	/**
 	 * Called after execution of an action
 	 */
 	public void afterAction(PersistentCollection collection) {
 		loadedKey = getCurrentKey();
 		setLoadedPersister( getCurrentPersister() );
 
 		boolean resnapshot = collection.wasInitialized() &&
 				( isDoremove() || isDorecreate() || isDoupdate() );
 		if ( resnapshot ) {
 			snapshot = loadedPersister==null || !loadedPersister.isMutable() ?
 					null :
 					collection.getSnapshot(loadedPersister); //re-snapshot
 		}
 
 		collection.postAction();
 	}
 
 	public Serializable getKey() {
 		return getLoadedKey();
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public Serializable getSnapshot() {
 		return snapshot;
 	}
 
 	private void setLoadedPersister(CollectionPersister persister) {
 		loadedPersister = persister;
 		setRole( persister == null ? null : persister.getRole() );
 	}
 
 	void afterDeserialize(SessionFactoryImplementor factory) {
 		loadedPersister = ( factory == null ? null : factory.getCollectionPersister(role) );
 	}
 
 	public boolean wasDereferenced() {
 		return getLoadedKey() == null;
 	}
 
 	public boolean isReached() {
 		return reached;
 	}
 
 	public void setReached(boolean reached) {
 		this.reached = reached;
 	}
 
 	public boolean isProcessed() {
 		return processed;
 	}
 
 	public void setProcessed(boolean processed) {
 		this.processed = processed;
 	}
 
 	public boolean isDoupdate() {
 		return doupdate;
 	}
 
 	public void setDoupdate(boolean doupdate) {
 		this.doupdate = doupdate;
 	}
 
 	public boolean isDoremove() {
 		return doremove;
 	}
 
 	public void setDoremove(boolean doremove) {
 		this.doremove = doremove;
 	}
 
 	public boolean isDorecreate() {
 		return dorecreate;
 	}
 
 	public void setDorecreate(boolean dorecreate) {
 		this.dorecreate = dorecreate;
 	}
 
 	public boolean isIgnore() {
 		return ignore;
 	}
 
 	public CollectionPersister getCurrentPersister() {
 		return currentPersister;
 	}
 
 	public void setCurrentPersister(CollectionPersister currentPersister) {
 		this.currentPersister = currentPersister;
 	}
 
 	/**
 	 * This is only available late during the flush
 	 * cycle
 	 */
 	public Serializable getCurrentKey() {
 		return currentKey;
 	}
 
 	public void setCurrentKey(Serializable currentKey) {
 		this.currentKey = currentKey;
 	}
 
 	/**
 	 * This is only available late during the flush cycle
 	 */
 	public CollectionPersister getLoadedPersister() {
 		return loadedPersister;
 	}
 
 	public Serializable getLoadedKey() {
 		return loadedKey;
 	}
 
 	public void setRole(String role) {
 		this.role = role;
 	}
 
 	@Override
     public String toString() {
 		String result = "CollectionEntry" +
 				MessageHelper.collectionInfoString( loadedPersister.getRole(), loadedKey );
 		if (currentPersister!=null) {
 			result += "->" +
 					MessageHelper.collectionInfoString( currentPersister.getRole(), currentKey );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the collection orphans (entities which were removed from the collection)
 	 */
 	public Collection getOrphans(String entityName, PersistentCollection collection)
 	throws HibernateException {
 		if (snapshot==null) {
 			throw new AssertionFailure("no collection snapshot for orphan delete");
 		}
 		return collection.getOrphans( snapshot, entityName );
 	}
 
 	public boolean isSnapshotEmpty(PersistentCollection collection) {
 		//TODO: does this really need to be here?
 		//      does the collection already have
 		//      it's own up-to-date snapshot?
 		return collection.wasInitialized() &&
 			( getLoadedPersister()==null || getLoadedPersister().isMutable() ) &&
 			collection.isSnapshotEmpty( getSnapshot() );
 	}
 
 
 
 	/**
 	 * Custom serialization routine used during serialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param oos The stream to which we should write the serial data.
 	 * @throws java.io.IOException
 	 */
 	void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeObject( role );
 		oos.writeObject( snapshot );
 		oos.writeObject( loadedKey );
 	}
 
 	/**
 	 * Custom deserialization routine used during deserialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param ois The stream from which to read the entry.
 	 * @param session The session being deserialized.
 	 * @return The deserialized CollectionEntry
 	 * @throws IOException
 	 * @throws ClassNotFoundException
 	 */
 	static CollectionEntry deserialize(
 			ObjectInputStream ois,
 	        SessionImplementor session) throws IOException, ClassNotFoundException {
 		return new CollectionEntry(
 				( String ) ois.readObject(),
 		        ( Serializable ) ois.readObject(),
 		        ( Serializable ) ois.readObject(),
 		        ( session == null ? null : session.getFactory() )
 		);
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/Collections.java b/hibernate-core/src/main/java/org/hibernate/engine/Collections.java
index f8193b74ba..cd954dd71a 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/Collections.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/Collections.java
@@ -1,261 +1,261 @@
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.collection.PersistentCollection;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.CollectionType;
 
 /**
  * Implements book-keeping for the collection persistence by reachability algorithm
  * @author Gavin King
  */
 public final class Collections {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Collections.class.getName());
 
 	private Collections() {}
 
 	/**
 	 * record the fact that this collection was dereferenced
 	 *
 	 * @param coll The collection to be updated by unreachability.
 	 * @throws HibernateException
 	 */
 	public static void processUnreachableCollection(PersistentCollection coll, SessionImplementor session)
 	throws HibernateException {
 
 		if ( coll.getOwner()==null ) {
 			processNeverReferencedCollection(coll, session);
 		}
 		else {
 			processDereferencedCollection(coll, session);
 		}
 
 	}
 
 	private static void processDereferencedCollection(PersistentCollection coll, SessionImplementor session)
 	throws HibernateException {
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		CollectionEntry entry = persistenceContext.getCollectionEntry(coll);
 		final CollectionPersister loadedPersister = entry.getLoadedPersister();
 
         if (LOG.isDebugEnabled() && loadedPersister != null) LOG.debugf("Collection dereferenced: %s",
                                                                         MessageHelper.collectionInfoString(loadedPersister,
                                                                                                            entry.getLoadedKey(),
                                                                                                            session.getFactory()));
 
 		// do a check
 		boolean hasOrphanDelete = loadedPersister != null &&
 		                          loadedPersister.hasOrphanDelete();
 		if (hasOrphanDelete) {
 			Serializable ownerId = loadedPersister.getOwnerEntityPersister().getIdentifier( coll.getOwner(), session );
 			if ( ownerId == null ) {
 				// the owning entity may have been deleted and its identifier unset due to
 				// identifier-rollback; in which case, try to look up its identifier from
 				// the persistence context
 				if ( session.getFactory().getSettings().isIdentifierRollbackEnabled() ) {
 					EntityEntry ownerEntry = persistenceContext.getEntry( coll.getOwner() );
 					if ( ownerEntry != null ) {
 						ownerId = ownerEntry.getId();
 					}
 				}
 				if ( ownerId == null ) {
 					throw new AssertionFailure( "Unable to determine collection owner identifier for orphan-delete processing" );
 				}
 			}
 			EntityKey key = session.generateEntityKey( ownerId, loadedPersister.getOwnerEntityPersister() );
 			Object owner = persistenceContext.getEntity(key);
 			if ( owner == null ) {
 				throw new AssertionFailure(
 						"collection owner not associated with session: " +
 						loadedPersister.getRole()
 				);
 			}
 			EntityEntry e = persistenceContext.getEntry(owner);
 			//only collections belonging to deleted entities are allowed to be dereferenced in the case of orphan delete
 			if ( e != null && e.getStatus() != Status.DELETED && e.getStatus() != Status.GONE ) {
 				throw new HibernateException(
 						"A collection with cascade=\"all-delete-orphan\" was no longer referenced by the owning entity instance: " +
 						loadedPersister.getRole()
 				);
 			}
 		}
 
 		// do the work
 		entry.setCurrentPersister(null);
 		entry.setCurrentKey(null);
 		prepareCollectionForUpdate( coll, entry, session.getEntityMode(), session.getFactory() );
 
 	}
 
 	private static void processNeverReferencedCollection(PersistentCollection coll, SessionImplementor session)
 	throws HibernateException {
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		CollectionEntry entry = persistenceContext.getCollectionEntry(coll);
 
         LOG.debugf("Found collection with unloaded owner: %s",
                    MessageHelper.collectionInfoString(entry.getLoadedPersister(), entry.getLoadedKey(), session.getFactory()));
 
 		entry.setCurrentPersister( entry.getLoadedPersister() );
 		entry.setCurrentKey( entry.getLoadedKey() );
 
 		prepareCollectionForUpdate( coll, entry, session.getEntityMode(), session.getFactory() );
 
 	}
 
     /**
      * Initialize the role of the collection.
      *
      * @param collection The collection to be updated by reachability.
      * @param type The type of the collection.
      * @param entity The owner of the collection.
      * @throws HibernateException
      */
 	public static void processReachableCollection(
 			PersistentCollection collection,
 	        CollectionType type,
 	        Object entity,
 	        SessionImplementor session)
 	throws HibernateException {
 
 		collection.setOwner(entity);
 
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry(collection);
 
 		if ( ce == null ) {
 			// refer to comment in StatefulPersistenceContext.addCollection()
 			throw new HibernateException(
 					"Found two representations of same collection: " +
 					type.getRole()
 			);
 		}
 
 		// The CollectionEntry.isReached() stuff is just to detect any silly users
 		// who set up circular or shared references between/to collections.
 		if ( ce.isReached() ) {
 			// We've been here before
 			throw new HibernateException(
 					"Found shared references to a collection: " +
 					type.getRole()
 			);
 		}
 		ce.setReached(true);
 
 		SessionFactoryImplementor factory = session.getFactory();
 		CollectionPersister persister = factory.getCollectionPersister( type.getRole() );
 		ce.setCurrentPersister(persister);
 		ce.setCurrentKey( type.getKeyOfOwner(entity, session) ); //TODO: better to pass the id in as an argument?
 
         if (LOG.isDebugEnabled()) {
             if (collection.wasInitialized()) LOG.debugf("Collection found: %s, was: %s (initialized)",
                                                         MessageHelper.collectionInfoString(persister, ce.getCurrentKey(), factory),
                                                         MessageHelper.collectionInfoString(ce.getLoadedPersister(),
                                                                                            ce.getLoadedKey(),
                                                                                            factory));
             else LOG.debugf("Collection found: %s, was: %s (uninitialized)",
                             MessageHelper.collectionInfoString(persister, ce.getCurrentKey(), factory),
                             MessageHelper.collectionInfoString(ce.getLoadedPersister(), ce.getLoadedKey(), factory));
         }
 
 		prepareCollectionForUpdate( collection, ce, session.getEntityMode(), factory );
 
 	}
 
 	/**
 	 * 1. record the collection role that this collection is referenced by
 	 * 2. decide if the collection needs deleting/creating/updating (but
 	 *	don't actually schedule the action yet)
 	 */
 	private static void prepareCollectionForUpdate(
 			PersistentCollection collection,
 	        CollectionEntry entry,
 	        EntityMode entityMode,
 	        SessionFactoryImplementor factory)
 	throws HibernateException {
 
 		if ( entry.isProcessed() ) {
 			throw new AssertionFailure( "collection was processed twice by flush()" );
 		}
 		entry.setProcessed(true);
 
 		final CollectionPersister loadedPersister = entry.getLoadedPersister();
 		final CollectionPersister currentPersister = entry.getCurrentPersister();
 		if ( loadedPersister != null || currentPersister != null ) {					// it is or was referenced _somewhere_
 
 			boolean ownerChanged = loadedPersister != currentPersister ||				// if either its role changed,
 			                       !currentPersister
 					                       .getKeyType().isEqual(                       // or its key changed
 													entry.getLoadedKey(),
 			                                        entry.getCurrentKey(),
 			                                        entityMode, factory
 			                       );
 
 			if (ownerChanged) {
 
 				// do a check
 				final boolean orphanDeleteAndRoleChanged = loadedPersister != null &&
 				                                           currentPersister != null &&
 				                                           loadedPersister.hasOrphanDelete();
 
 				if (orphanDeleteAndRoleChanged) {
 					throw new HibernateException(
 							"Don't change the reference to a collection with cascade=\"all-delete-orphan\": " +
 							loadedPersister.getRole()
 						);
 				}
 
 				// do the work
 				if ( currentPersister != null ) {
 					entry.setDorecreate(true);											// we will need to create new entries
 				}
 
 				if ( loadedPersister != null ) {
 					entry.setDoremove(true);											// we will need to remove ye olde entries
 					if ( entry.isDorecreate() ) {
                         LOG.trace("Forcing collection initialization");
 						collection.forceInitialization();								// force initialize!
 					}
 				}
 
 			}
 			else if ( collection.isDirty() ) {											// else if it's elements changed
 				entry.setDoupdate(true);
 			}
 
 		}
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/PersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/PersistenceContext.java
index c7fe989e1d..cf7af1336d 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/PersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/PersistenceContext.java
@@ -1,602 +1,602 @@
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
 package org.hibernate.engine;
 import java.io.Serializable;
 import java.util.HashSet;
 import java.util.Map;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.loading.LoadContexts;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Holds the state of the persistence context, including the 
  * first-level cache, entries, snapshots, proxies, etc.
  * 
  * @author Gavin King
  */
 public interface PersistenceContext {
 	
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
 	 */
 	public void addUnownedCollection(CollectionKey key, PersistentCollection collection);
 
 	/**
 	 * Get and remove a collection whose owner is not yet loaded,
 	 * when its owner is being loaded
 	 */
 	public PersistentCollection useUnownedCollection(CollectionKey key);
 
 	/**
 	 * Get the <tt>BatchFetchQueue</tt>, instantiating one if
 	 * necessary.
 	 */
 	public BatchFetchQueue getBatchFetchQueue();
 	
 	/**
 	 * Clear the state of the persistence context
 	 */
 	public void clear();
 
 	/**
 	 * @return false if we know for certain that all the entities are read-only
 	 */
 	public boolean hasNonReadOnlyEntities();
 
 	/**
 	 * Set the status of an entry
 	 */
 	public void setEntryStatus(EntityEntry entry, Status status);
 
 	/**
 	 * Called after transactions end
 	 */
 	public void afterTransactionCompletion();
 
 	/**
 	 * Get the current state of the entity as known to the underlying
 	 * database, or null if there is no corresponding row 
 	 */
 	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister)
 			throws HibernateException;
 
 	public Object[] getCachedDatabaseSnapshot(EntityKey key);
 
 	/**
 	 * Get the values of the natural id fields as known to the underlying 
 	 * database, or null if the entity has no natural id or there is no 
 	 * corresponding row.
 	 */
 	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister)
 	throws HibernateException;
 
 	/**
 	 * Add a canonical mapping from entity key to entity instance
 	 */
 	public void addEntity(EntityKey key, Object entity);
 
 	/**
 	 * Get the entity instance associated with the given 
 	 * <tt>EntityKey</tt>
 	 */
 	public Object getEntity(EntityKey key);
 
 	/**
 	 * Is there an entity with the given key in the persistence context
 	 */
 	public boolean containsEntity(EntityKey key);
 
 	/**
 	 * Remove an entity from the session cache, also clear
 	 * up other state associated with the entity, all except
 	 * for the <tt>EntityEntry</tt>
 	 */
 	public Object removeEntity(EntityKey key);
 
 	/**
 	 * Get an entity cached by unique key
 	 */
 	public Object getEntity(EntityUniqueKey euk);
 
 	/**
 	 * Add an entity to the cache by unique key
 	 */
 	public void addEntity(EntityUniqueKey euk, Object entity);
 
 	/**
 	 * Retreive the EntityEntry representation of the given entity.
 	 *
 	 * @param entity The entity for which to locate the EntityEntry.
 	 * @return The EntityEntry for the given entity.
 	 */
 	public EntityEntry getEntry(Object entity);
 
 	/**
 	 * Remove an entity entry from the session cache
 	 */
 	public EntityEntry removeEntry(Object entity);
 
 	/**
 	 * Is there an EntityEntry for this instance?
 	 */
 	public boolean isEntryFor(Object entity);
 
 	/**
 	 * Get the collection entry for a persistent collection
 	 */
 	public CollectionEntry getCollectionEntry(PersistentCollection coll);
 
 	/**
 	 * Adds an entity to the internal caches.
 	 */
 	public EntityEntry addEntity(final Object entity, final Status status,
 			final Object[] loadedState, final EntityKey entityKey, final Object version,
 			final LockMode lockMode, final boolean existsInDatabase,
 			final EntityPersister persister, final boolean disableVersionIncrement, boolean lazyPropertiesAreUnfetched);
 
 	/**
 	 * Generates an appropriate EntityEntry instance and adds it 
 	 * to the event source's internal caches.
 	 */
 	public EntityEntry addEntry(final Object entity, final Status status,
 			final Object[] loadedState, final Object rowId, final Serializable id,
 			final Object version, final LockMode lockMode, final boolean existsInDatabase,
 			final EntityPersister persister, final boolean disableVersionIncrement, boolean lazyPropertiesAreUnfetched);
 
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
 	 *
 	 * To get the default read-only/modifiable setting used for
 	 * entities and proxies that are loaded into the session:
 	 * @see org.hibernate.Session#isDefaultReadOnly()
 	 *
 	 * @param entityOrProxy
 	 * @return true, the object is read-only; false, the object is modifiable.
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
 	 * To set the default read-only/modifiable setting used for
 	 * entities and proxies that are loaded into this persistence context:
 	 * @see PersistenceContext#setDefaultReadOnly(boolean)
 	 * @see org.hibernate.Session#setDefaultReadOnly(boolean)
 	 *
 	 * To override this persistence context's read-only/modifiable setting
 	 * for entities and proxies loaded by a Query:
 	 * @see org.hibernate.Query#setReadOnly(boolean)
 	 *
 	 * @param entityOrProxy, an entity or HibernateProxy
 	 * @param readOnly, if true, the entity or proxy is made read-only;
 	 *                  if false, the entity or proxy is made modifiable.
 	 *
 	 * @see org.hibernate.Session#setReadOnly(Object, boolean)
 	 */
 	public void setReadOnly(Object entityOrProxy, boolean readOnly);
 
 	void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId);
 
 	/**
 	 * Put child/parent relation to cache for cascading op
 	 * @param parent
 	 * @param child
 	 */
 	public void addChildParent(Object parent, Object child);
 
 	/**
 	 * Remove child/parent relation from cache 
 	 * @param parent
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
index c4743e1703..3f7a2e0f7d 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
@@ -1,392 +1,392 @@
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
 import org.hibernate.cache.spi.CacheKey;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.internal.CriteriaImpl;
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
 	 * name, with values corresponding to the {@link org.hibernate.internal.FilterImpl}
 	 * instance.
 	 * @return The currently enabled filters.
 	 * @deprecated use #getLoadQueryInfluencers instead
 	 */
 	@Deprecated
     public Map getEnabledFilters();
 
 	public int getDontFlushFromFind();
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/StatefulPersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/StatefulPersistenceContext.java
index 617e7d5ae6..5e2d7101cb 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/StatefulPersistenceContext.java
@@ -1,1051 +1,1051 @@
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
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.apache.commons.collections.map.AbstractReferenceMap;
 import org.apache.commons.collections.map.ReferenceMap;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
-import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.loading.LoadContexts;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.tuple.ElementWrapper;
 
 import static org.jboss.logging.Logger.Level.WARN;
 
 /**
  * A <tt>PersistenceContext</tt> represents the state of persistent "stuff" which
  * Hibernate is tracking.  This includes persistent entities, collections,
  * as well as proxies generated.
  * </p>
  * There is meant to be a one-to-one correspondence between a SessionImpl and
  * a PersistentContext.  The SessionImpl uses the PersistentContext to track
  * the current state of its context.  Event-listeners then use the
  * PersistentContext to drive their processing.
  *
  * @author Steve Ebersole
  */
 public class StatefulPersistenceContext implements PersistenceContext {
 
 	public static final Object NO_ROW = new MarkerObject( "NO_ROW" );
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        StatefulPersistenceContext.class.getName());
 	private static final int INIT_COLL_SIZE = 8;
 
 	private SessionImplementor session;
 
 	// Loaded entity instances, by EntityKey
 	private Map entitiesByKey;
 
 	// Loaded entity instances, by EntityUniqueKey
 	private Map entitiesByUniqueKey;
 
 	// Identity map of EntityEntry instances, by the entity instance
 	private Map entityEntries;
 
 	// Entity proxies, by EntityKey
 	private Map proxiesByKey;
 
 	// Snapshots of current database state for entities
 	// that have *not* been loaded
 	private Map entitySnapshotsByKey;
 
 	// Identity map of array holder ArrayHolder instances, by the array instance
 	private Map arrayHolders;
 
 	// Identity map of CollectionEntry instances, by the collection wrapper
 	private Map collectionEntries;
 
 	// Collection wrappers, by the CollectionKey
 	private Map collectionsByKey; //key=CollectionKey, value=PersistentCollection
 
 	// Set of EntityKeys of deleted objects
 	private HashSet nullifiableEntityKeys;
 
 	// properties that we have tried to load, and not found in the database
 	private HashSet nullAssociations;
 
 	// A list of collection wrappers that were instantiating during result set
 	// processing, that we will need to initialize at the end of the query
 	private List nonlazyCollections;
 
 	// A container for collections we load up when the owning entity is not
 	// yet loaded ... for now, this is purely transient!
 	private Map unownedCollections;
 
 	// Parent entities cache by their child for cascading
 	// May be empty or not contains all relation
 	private Map parentsByChild;
 
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
 
 		entitiesByKey = new HashMap( INIT_COLL_SIZE );
 		entitiesByUniqueKey = new HashMap( INIT_COLL_SIZE );
 		proxiesByKey = new ReferenceMap( AbstractReferenceMap.HARD, AbstractReferenceMap.WEAK );
 		entitySnapshotsByKey = new HashMap( INIT_COLL_SIZE );
 
 		entityEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		collectionEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		collectionsByKey = new HashMap( INIT_COLL_SIZE );
 		arrayHolders = IdentityMap.instantiate( INIT_COLL_SIZE );
 		parentsByChild = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 
 		nullifiableEntityKeys = new HashSet();
 
 		initTransientState();
 	}
 
 	private void initTransientState() {
 		nullAssociations = new HashSet( INIT_COLL_SIZE );
 		nonlazyCollections = new ArrayList( INIT_COLL_SIZE );
 	}
 
 	public boolean isStateless() {
 		return false;
 	}
 
 	public SessionImplementor getSession() {
 		return session;
 	}
 
 	public LoadContexts getLoadContexts() {
 		if ( loadContexts == null ) {
 			loadContexts = new LoadContexts( this );
 		}
 		return loadContexts;
 	}
 
 	public void addUnownedCollection(CollectionKey key, PersistentCollection collection) {
 		if (unownedCollections==null) {
 			unownedCollections = new HashMap(8);
 		}
 		unownedCollections.put(key, collection);
 	}
 
 	public PersistentCollection useUnownedCollection(CollectionKey key) {
 		if (unownedCollections==null) {
 			return null;
 		}
 		else {
 			return (PersistentCollection) unownedCollections.remove(key);
 		}
 	}
 
 	/**
 	 * Get the <tt>BatchFetchQueue</tt>, instantiating one if
 	 * necessary.
 	 */
 	public BatchFetchQueue getBatchFetchQueue() {
 		if (batchFetchQueue==null) {
 			batchFetchQueue = new BatchFetchQueue(this);
 		}
 		return batchFetchQueue;
 	}
 
 	public void clear() {
 		Iterator itr = proxiesByKey.values().iterator();
 		while ( itr.hasNext() ) {
 			final LazyInitializer li = ( ( HibernateProxy ) itr.next() ).getHibernateLazyInitializer();
 			li.unsetSession();
 		}
 		Map.Entry[] collectionEntryArray = IdentityMap.concurrentEntries( collectionEntries );
 		for ( int i = 0; i < collectionEntryArray.length; i++ ) {
 			( ( PersistentCollection ) collectionEntryArray[i].getKey() ).unsetSession( getSession() );
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
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isDefaultReadOnly() {
 		return defaultReadOnly;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		this.defaultReadOnly = defaultReadOnly;
 	}
 
 	public boolean hasNonReadOnlyEntities() {
 		return hasNonReadOnlyEntities;
 	}
 
 	public void setEntryStatus(EntityEntry entry, Status status) {
 		entry.setStatus(status);
 		setHasNonReadOnlyEnties(status);
 	}
 
 	private void setHasNonReadOnlyEnties(Status status) {
 		if ( status==Status.DELETED || status==Status.MANAGED || status==Status.SAVING ) {
 			hasNonReadOnlyEntities = true;
 		}
 	}
 
 	public void afterTransactionCompletion() {
 		cleanUpInsertedKeysAfterTransaction();
 		// Downgrade locks
 		Iterator iter = entityEntries.values().iterator();
 		while ( iter.hasNext() ) {
 			( (EntityEntry) iter.next() ).setLockMode(LockMode.NONE);
 		}
 	}
 
 	/**
 	 * Get the current state of the entity as known to the underlying
 	 * database, or null if there is no corresponding row
 	 */
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
 
 	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister)
 	throws HibernateException {
 		if ( !persister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		// if the natural-id is marked as non-mutable, it is not retrieved during a
 		// normal database-snapshot operation...
 		int[] props = persister.getNaturalIdentifierProperties();
 		boolean[] updateable = persister.getPropertyUpdateability();
 		boolean allNatualIdPropsAreUpdateable = true;
 		for ( int i = 0; i < props.length; i++ ) {
 			if ( !updateable[ props[i] ] ) {
 				allNatualIdPropsAreUpdateable = false;
 				break;
 			}
 		}
 
 		if ( allNatualIdPropsAreUpdateable ) {
 			// do this when all the properties are updateable since there is
 			// a certain likelihood that the information will already be
 			// snapshot-cached.
 			Object[] entitySnapshot = getDatabaseSnapshot( id, persister );
 			if ( entitySnapshot == NO_ROW ) {
 				return null;
 			}
 			Object[] naturalIdSnapshot = new Object[ props.length ];
 			for ( int i = 0; i < props.length; i++ ) {
 				naturalIdSnapshot[i] = entitySnapshot[ props[i] ];
 			}
 			return naturalIdSnapshot;
 		}
 		else {
 			return persister.getNaturalIdentifierSnapshot( id, session );
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
 	public Object[] getCachedDatabaseSnapshot(EntityKey key) {
 		Object snapshot = entitySnapshotsByKey.get( key );
 		if ( snapshot == NO_ROW ) {
 			throw new IllegalStateException( "persistence context reported no row snapshot for " + MessageHelper.infoString( key.getEntityName(), key.getIdentifier() ) );
 		}
 		return ( Object[] ) snapshot;
 	}
 
 	/*public void removeDatabaseSnapshot(EntityKey key) {
 		entitySnapshotsByKey.remove(key);
 	}*/
 
 	public void addEntity(EntityKey key, Object entity) {
 		entitiesByKey.put(key, entity);
 		getBatchFetchQueue().removeBatchLoadableEntityKey(key);
 	}
 
 	/**
 	 * Get the entity instance associated with the given
 	 * <tt>EntityKey</tt>
 	 */
 	public Object getEntity(EntityKey key) {
 		return entitiesByKey.get(key);
 	}
 
 	public boolean containsEntity(EntityKey key) {
 		return entitiesByKey.containsKey(key);
 	}
 
 	/**
 	 * Remove an entity from the session cache, also clear
 	 * up other state associated with the entity, all except
 	 * for the <tt>EntityEntry</tt>
 	 */
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
 	public Object getEntity(EntityUniqueKey euk) {
 		return entitiesByUniqueKey.get(euk);
 	}
 
 	/**
 	 * Add an entity to the cache by unique key
 	 */
 	public void addEntity(EntityUniqueKey euk, Object entity) {
 		entitiesByUniqueKey.put(euk, entity);
 	}
 
 	/**
 	 * Retreive the EntityEntry representation of the given entity.
 	 *
 	 * @param entity The entity for which to locate the EntityEntry.
 	 * @return The EntityEntry for the given entity.
 	 */
 	public EntityEntry getEntry(Object entity) {
 		return (EntityEntry) entityEntries.get(entity);
 	}
 
 	/**
 	 * Remove an entity entry from the session cache
 	 */
 	public EntityEntry removeEntry(Object entity) {
 		return (EntityEntry) entityEntries.remove(entity);
 	}
 
 	/**
 	 * Is there an EntityEntry for this instance?
 	 */
 	public boolean isEntryFor(Object entity) {
 		return entityEntries.containsKey(entity);
 	}
 
 	/**
 	 * Get the collection entry for a persistent collection
 	 */
 	public CollectionEntry getCollectionEntry(PersistentCollection coll) {
 		return (CollectionEntry) collectionEntries.get(coll);
 	}
 
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
 			boolean lazyPropertiesAreUnfetched
 	) {
 
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
 				session.getEntityMode(),
 				session.getTenantIdentifier(),
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched
 		);
 		entityEntries.put(entity, e);
 
 		setHasNonReadOnlyEnties(status);
 		return e;
 	}
 
 	public boolean containsCollection(PersistentCollection collection) {
 		return collectionEntries.containsKey(collection);
 	}
 
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
 	public void reassociateProxy(Object value, Serializable id) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
 		if ( value instanceof HibernateProxy ) {
             LOG.debugf("Setting proxy identifier: %s", id);
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
 	public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object)
 	throws HibernateException {
 
 		boolean alreadyNarrow = persister.getConcreteProxyClass( session.getEntityMode() )
 				.isAssignableFrom( proxy.getClass() );
 
 		if ( !alreadyNarrow ) {
             if (LOG.isEnabled(WARN)) LOG.narrowingProxy(persister.getConcreteProxyClass(session.getEntityMode()));
 
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
 	public Object proxyFor(Object impl) throws HibernateException {
 		EntityEntry e = getEntry(impl);
 		return proxyFor( e.getPersister(), e.getEntityKey(), impl );
 	}
 
 	/**
 	 * Get the entity that owns this persistent collection
 	 */
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
 	public void addUninitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) {
 		CollectionEntry ce = new CollectionEntry(collection, persister, id, flushing);
 		addCollection(collection, ce, id);
 	}
 
 	/**
 	 * add a detached uninitialized collection
 	 */
 	public void addUninitializedDetachedCollection(CollectionPersister persister, PersistentCollection collection) {
 		CollectionEntry ce = new CollectionEntry( persister, collection.getKey() );
 		addCollection( collection, ce, collection.getKey() );
 	}
 
 	/**
 	 * Add a new collection (ie. a newly created one, just instantiated by the
 	 * application, with no database state or snapshot)
 	 * @param collection The collection to be associated with the persistence context
 	 */
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
 		CollectionKey collectionKey = new CollectionKey( entry.getLoadedPersister(), key, session.getEntityMode() );
 		PersistentCollection old = ( PersistentCollection ) collectionsByKey.put( collectionKey, coll );
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
 	public PersistentCollection getCollection(CollectionKey collectionKey) {
 		return (PersistentCollection) collectionsByKey.get(collectionKey);
 	}
 
 	/**
 	 * Register a collection for non-lazy loading at the end of the
 	 * two-phase load
 	 */
 	public void addNonLazyCollection(PersistentCollection collection) {
 		nonlazyCollections.add(collection);
 	}
 
 	/**
 	 * Force initialization of all non-lazy collections encountered during
 	 * the current two-phase load (actually, this is a no-op, unless this
 	 * is the "outermost" load)
 	 */
 	public void initializeNonLazyCollections() throws HibernateException {
 		if ( loadCounter == 0 ) {
             LOG.debugf("Initializing non-lazy collections");
 			//do this work only at the very highest level of the load
 			loadCounter++; //don't let this method be called recursively
 			try {
 				int size;
 				while ( ( size = nonlazyCollections.size() ) > 0 ) {
 					//note that each iteration of the loop may add new elements
 					( (PersistentCollection) nonlazyCollections.remove( size - 1 ) ).forceInitialization();
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
 	public PersistentCollection getCollectionHolder(Object array) {
 		return (PersistentCollection) arrayHolders.get(array);
 	}
 
 	/**
 	 * Register a <tt>PersistentCollection</tt> object for an array.
 	 * Associates a holder with an array - MUST be called after loading
 	 * array, since the array instance is not created until endLoad().
 	 */
 	public void addCollectionHolder(PersistentCollection holder) {
 		//TODO:refactor + make this method private
 		arrayHolders.put( holder.getValue(), holder );
 	}
 
 	public PersistentCollection removeCollectionHolder(Object array) {
 		return (PersistentCollection) arrayHolders.remove(array);
 	}
 
 	/**
 	 * Get the snapshot of the pre-flush collection state
 	 */
 	public Serializable getSnapshot(PersistentCollection coll) {
 		return getCollectionEntry(coll).getSnapshot();
 	}
 
 	/**
 	 * Get the collection entry for a collection passed to filter,
 	 * which might be a collection wrapper, an array, or an unwrapped
 	 * collection. Return null if there is no entry.
 	 */
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
 				Iterator wrappers = IdentityMap.keyIterator(collectionEntries);
 				while ( wrappers.hasNext() ) {
 					PersistentCollection pc = (PersistentCollection) wrappers.next();
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
 	public Object getProxy(EntityKey key) {
 		return proxiesByKey.get(key);
 	}
 
 	/**
 	 * Add a proxy to the session cache
 	 */
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
 	public Object removeProxy(EntityKey key) {
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.removeBatchLoadableEntityKey( key );
 			batchFetchQueue.removeSubselect( key );
 		}
 		return proxiesByKey.remove( key );
 	}
 
 	/**
 	 * Record the fact that an entity does not exist in the database
 	 *
 	 * @param key the primary key of the entity
 	 */
 	/*public void addNonExistantEntityKey(EntityKey key) {
 		nonExistantEntityKeys.add(key);
 	}*/
 
 	/**
 	 * Record the fact that an entity does not exist in the database
 	 *
 	 * @param key a unique key of the entity
 	 */
 	/*public void addNonExistantEntityUniqueKey(EntityUniqueKey key) {
 		nonExistentEntityUniqueKeys.add(key);
 	}*/
 
 	/*public void removeNonExist(EntityKey key) {
 		nonExistantEntityKeys.remove(key);
 	}*/
 
 	/**
 	 * Retrieve the set of EntityKeys representing nullifiable references
 	 */
 	public HashSet getNullifiableEntityKeys() {
 		return nullifiableEntityKeys;
 	}
 
 	public Map getEntitiesByKey() {
 		return entitiesByKey;
 	}
 
 	public Map getProxiesByKey() {
 		return proxiesByKey;
 	}
 
 	public Map getEntityEntries() {
 		return entityEntries;
 	}
 
 	public Map getCollectionEntries() {
 		return collectionEntries;
 	}
 
 	public Map getCollectionsByKey() {
 		return collectionsByKey;
 	}
 
 	/**
 	 * Do we already know that the entity does not exist in the
 	 * database?
 	 */
 	/*public boolean isNonExistant(EntityKey key) {
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/loading/CollectionLoadContext.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/CollectionLoadContext.java
index 5fd523e908..7b9beabf77 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/CollectionLoadContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/CollectionLoadContext.java
@@ -1,343 +1,343 @@
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
 package org.hibernate.engine.loading;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.CacheKey;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.cache.spi.entry.CollectionCacheEntry;
-import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.CollectionEntry;
 import org.hibernate.engine.CollectionKey;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.persister.collection.CollectionPersister;
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionLoadContext.class.getName());
 
 	private final LoadContexts loadContexts;
 	private final ResultSet resultSet;
 	private Set localLoadingCollectionKeys = new HashSet();
 
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
-	 * <li>an instance of {@link PersistentCollection} which indicates to
+	 * <li>an instance of {@link org.hibernate.collection.spi.PersistentCollection} which indicates to
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
 		final EntityMode em = loadContexts.getPersistenceContext().getSession().getEntityMode();
 		final CollectionKey collectionKey = new CollectionKey( persister, key, em );
         if (LOG.isTraceEnabled()) LOG.trace("Starting attempt to find loading collection ["
                                             + MessageHelper.collectionInfoString(persister.getRole(), key) + "]");
 		final LoadingCollectionEntry loadingCollectionEntry = loadContexts.locateLoadingCollectionEntry( collectionKey );
 		if ( loadingCollectionEntry == null ) {
 			// look for existing collection as part of the persistence context
 			PersistentCollection collection = loadContexts.getPersistenceContext().getCollection( collectionKey );
 			if ( collection != null ) {
 				if ( collection.wasInitialized() ) {
                     LOG.trace("Collection already initialized; ignoring");
 					return null; // ignore this row of results! Note the early exit
                 }
                 LOG.trace("Collection not yet initialized; initializing");
 			}
 			else {
 				Object owner = loadContexts.getPersistenceContext().getCollectionOwner( key, persister );
 				final boolean newlySavedEntity = owner != null
 						&& loadContexts.getPersistenceContext().getEntry( owner ).getStatus() != Status.LOADING
 						&& em != EntityMode.DOM4J;
 				if ( newlySavedEntity ) {
 					// important, to account for newly saved entities in query
 					// todo : some kind of check for new status...
                     LOG.trace("Owning entity already loaded; ignoring");
 					return null;
 				}
                 // create one
                 LOG.trace("Instantiating new collection [key=" + key + ", rs=" + resultSet + "]");
                 collection = persister.getCollectionType().instantiate(loadContexts.getPersistenceContext().getSession(),
                                                                        persister,
                                                                        key);
 			}
 			collection.beforeInitialize( persister, -1 );
 			collection.beginRead();
 			localLoadingCollectionKeys.add( collectionKey );
 			loadContexts.registerLoadingCollectionXRef( collectionKey, new LoadingCollectionEntry( resultSet, persister, key, collection ) );
 			return collection;
 		}
         if (loadingCollectionEntry.getResultSet() == resultSet) {
             LOG.trace("Found loading collection bound to current result set processing; reading row");
             return loadingCollectionEntry.getCollection();
 		}
         // ignore this row, the collection is in process of
         // being loaded somewhere further "up" the stack
         LOG.trace("Collection is already being initialized; ignoring row");
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
 		SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
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
 		List matches = null;
 		Iterator iter = localLoadingCollectionKeys.iterator();
 		while ( iter.hasNext() ) {
 			final CollectionKey collectionKey = (CollectionKey) iter.next();
 			final LoadingCollectionEntry lce = loadContexts.locateLoadingCollectionEntry( collectionKey );
             if (lce == null) LOG.loadingCollectionKeyNotFound(collectionKey);
 			else if ( lce.getResultSet() == resultSet && lce.getPersister() == persister ) {
 				if ( matches == null ) {
 					matches = new ArrayList();
 				}
 				matches.add( lce );
 				if ( lce.getCollection().getOwner() == null ) {
 					session.getPersistenceContext().addUnownedCollection(
 							new CollectionKey( persister, lce.getKey(), session.getEntityMode() ),
 							lce.getCollection()
 					);
 				}
                 LOG.trace("Removing collection load entry [" + lce + "]");
 
 				// todo : i'd much rather have this done from #endLoadingCollection(CollectionPersister,LoadingCollectionEntry)...
 				loadContexts.unregisterLoadingCollectionXRef( collectionKey );
 				iter.remove();
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
 
 	private void endLoadingCollections(CollectionPersister persister, List matchedCollectionEntries) {
 		if ( matchedCollectionEntries == null ) {
             LOG.debugf("No collections were found in result set for role: %s", persister.getRole());
 			return;
 		}
 
 		final int count = matchedCollectionEntries.size();
         LOG.debugf("%s collections were found in result set for role: %s", count, persister.getRole());
 
 		for ( int i = 0; i < count; i++ ) {
 			LoadingCollectionEntry lce = ( LoadingCollectionEntry ) matchedCollectionEntries.get( i );
 			endLoadingCollection( lce, persister );
 		}
 
         LOG.debugf("%s collections initialized for role: %s", count, persister.getRole());
 	}
 
 	private void endLoadingCollection(LoadingCollectionEntry lce, CollectionPersister persister) {
         LOG.trace("Ending loading collection [" + lce + "]");
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 		final EntityMode em = session.getEntityMode();
 
 		boolean hasNoQueuedAdds = lce.getCollection().endRead(); // warning: can cause a recursive calls! (proxy initialization)
 
 		if ( persister.getCollectionType().hasHolder( em ) ) {
 			getLoadContext().getPersistenceContext().addCollectionHolder( lce.getCollection() );
 		}
 
 		CollectionEntry ce = getLoadContext().getPersistenceContext().getCollectionEntry( lce.getCollection() );
 		if ( ce == null ) {
 			ce = getLoadContext().getPersistenceContext().addInitializedCollection( persister, lce.getCollection(), lce.getKey() );
 		}
 		else {
 			ce.postInitialize( lce.getCollection() );
 		}
 
 		boolean addToCache = hasNoQueuedAdds && // there were no queued additions
 				persister.hasCache() &&             // and the role has a cache
 				session.getCacheMode().isPutEnabled() &&
 				!ce.isDoremove();                   // and this is not a forced initialization during flush
         if (addToCache) addCollectionToCache(lce, persister);
 
         if (LOG.isDebugEnabled()) LOG.debugf("Collection fully initialized: %s",
                                              MessageHelper.collectionInfoString(persister, lce.getKey(), session.getFactory()));
         if (session.getFactory().getStatistics().isStatisticsEnabled()) session.getFactory().getStatisticsImplementor().loadCollection(persister.getRole());
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
 
         if (LOG.isDebugEnabled()) LOG.debugf("Caching collection: %s",
                                              MessageHelper.collectionInfoString(persister, lce.getKey(), factory));
 
 		if ( !session.getEnabledFilters().isEmpty() && persister.isAffectedByEnabledFilters( session ) ) {
 			// some filters affecting the collection are enabled on the session, so do not do the put into the cache.
             LOG.debugf("Refusing to add to cache due to enabled filters");
 			// todo : add the notion of enabled filters to the CacheKey to differentiate filtered collections from non-filtered;
 			//      but CacheKey is currently used for both collections and entities; would ideally need to define two seperate ones;
 			//      currently this works in conjuction with the check on
 			//      DefaultInitializeCollectionEventHandler.initializeCollectionFromCache() (which makes sure to not read from
 			//      cache with enabled filters).
 			return; // EARLY EXIT!!!!!
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
 					Object linkedOwner = lce.getCollection().getOwner();
 					if ( linkedOwner != null ) {
 						final Serializable ownerKey = persister.getOwnerEntityPersister().getIdentifier( linkedOwner, session );
 						collectionOwner = getLoadContext().getPersistenceContext().getCollectionOwner( ownerKey, persister );
 					}
 				}
 				if ( collectionOwner == null ) {
 					throw new HibernateException(
 							"Unable to resolve owner of loading collection [" +
 									MessageHelper.collectionInfoString( persister, lce.getKey(), factory ) +
 									"] for second level caching"
 					);
 				}
 			}
 			version = getLoadContext().getPersistenceContext().getEntry( collectionOwner ).getVersion();
 		}
 		else {
 			version = null;
 		}
 
 		CollectionCacheEntry entry = new CollectionCacheEntry( lce.getCollection(), persister );
 		CacheKey cacheKey = session.generateCacheKey( lce.getKey(), persister.getKeyType(), persister.getRole() );
 		boolean put = persister.getCacheAccessStrategy().putFromLoad(
 				cacheKey,
 				persister.getCacheEntryStructure().structure(entry),
 				session.getTimestamp(),
 				version,
 				factory.getSettings().isMinimalPutsEnabled() && session.getCacheMode()!= CacheMode.REFRESH
 		);
 
 		if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().secondLevelCachePut( persister.getCacheAccessStrategy().getRegion().getName() );
 		}
 	}
 
 	void cleanup() {
         if (!localLoadingCollectionKeys.isEmpty()) LOG.localLoadingCollectionKeysCount(localLoadingCollectionKeys.size());
 		loadContexts.cleanupCollectionXRefs( localLoadingCollectionKeys );
 		localLoadingCollectionKeys.clear();
 	}
 
 
 	@Override
     public String toString() {
 		return super.toString() + "<rs=" + resultSet + ">";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadContexts.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadContexts.java
index d7e29f7cd5..89449d6ca4 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadContexts.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadContexts.java
@@ -1,310 +1,310 @@
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
 package org.hibernate.engine.loading;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import org.hibernate.EntityMode;
 import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.CollectionKey;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 
 import org.jboss.logging.Logger;
 
 /**
  * Maps {@link ResultSet result-sets} to specific contextual data
  * related to processing that {@link ResultSet result-sets}.
  * <p/>
  * Implementation note: internally an {@link IdentityMap} is used to maintain
  * the mappings; {@link IdentityMap} was chosen because I'd rather not be
  * dependent upon potentially bad {@link ResultSet#equals} and {ResultSet#hashCode}
  * implementations.
  * <p/>
  * Considering the JDBC-redesign work, would further like this contextual info
  * not mapped seperately, but available based on the result set being processed.
  * This would also allow maintaining a single mapping as we could reliably get
  * notification of the result-set closing...
  *
  * @author Steve Ebersole
  */
 public class LoadContexts {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, LoadContexts.class.getName());
 
 	private final PersistenceContext persistenceContext;
 	private Map collectionLoadContexts;
 	private Map entityLoadContexts;
 
 	private Map xrefLoadingCollectionEntries;
 
 	/**
 	 * Creates and binds this to the given persistence context.
 	 *
 	 * @param persistenceContext The persistence context to which this
 	 * will be bound.
 	 */
 	public LoadContexts(PersistenceContext persistenceContext) {
 		this.persistenceContext = persistenceContext;
 	}
 
 	/**
 	 * Retrieves the persistence context to which this is bound.
 	 *
 	 * @return The persistence context to which this is bound.
 	 */
 	public PersistenceContext getPersistenceContext() {
 		return persistenceContext;
 	}
 
 	private SessionImplementor getSession() {
 		return getPersistenceContext().getSession();
 	}
 
 	private EntityMode getEntityMode() {
 		return getSession().getEntityMode();
 	}
 
 
 	// cleanup code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
  	/**
 	 * Release internal state associated with the given result set.
 	 * <p/>
 	 * This should be called when we are done with processing said result set,
 	 * ideally as the result set is being closed.
 	 *
 	 * @param resultSet The result set for which it is ok to release
 	 * associated resources.
 	 */
 	public void cleanup(ResultSet resultSet) {
 		if ( collectionLoadContexts != null ) {
 			CollectionLoadContext collectionLoadContext = ( CollectionLoadContext ) collectionLoadContexts.remove( resultSet );
 			collectionLoadContext.cleanup();
 		}
 		if ( entityLoadContexts != null ) {
 			EntityLoadContext entityLoadContext = ( EntityLoadContext ) entityLoadContexts.remove( resultSet );
 			entityLoadContext.cleanup();
 		}
 	}
 
 	/**
 	 * Release internal state associated with *all* result sets.
 	 * <p/>
 	 * This is intended as a "failsafe" process to make sure we get everything
 	 * cleaned up and released.
 	 */
 	public void cleanup() {
 		if ( collectionLoadContexts != null ) {
 			Iterator itr = collectionLoadContexts.values().iterator();
 			while ( itr.hasNext() ) {
 				CollectionLoadContext collectionLoadContext = ( CollectionLoadContext ) itr.next();
                 LOG.failSafeCollectionsCleanup(collectionLoadContext);
 				collectionLoadContext.cleanup();
 			}
 			collectionLoadContexts.clear();
 		}
 		if ( entityLoadContexts != null ) {
 			Iterator itr = entityLoadContexts.values().iterator();
 			while ( itr.hasNext() ) {
 				EntityLoadContext entityLoadContext = ( EntityLoadContext ) itr.next();
                 LOG.failSafeEntitiesCleanup(entityLoadContext);
 				entityLoadContext.cleanup();
 			}
 			entityLoadContexts.clear();
 		}
 	}
 
 
 	// Collection load contexts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Do we currently have any internal entries corresponding to loading
 	 * collections?
 	 *
 	 * @return True if we currently hold state pertaining to loading collections;
 	 * false otherwise.
 	 */
 	public boolean hasLoadingCollectionEntries() {
 		return ( collectionLoadContexts != null && !collectionLoadContexts.isEmpty() );
 	}
 
 	/**
 	 * Do we currently have any registered internal entries corresponding to loading
 	 * collections?
 	 *
 	 * @return True if we currently hold state pertaining to a registered loading collections;
 	 * false otherwise.
 	 */
 	public boolean hasRegisteredLoadingCollectionEntries() {
 		return ( xrefLoadingCollectionEntries != null && !xrefLoadingCollectionEntries.isEmpty() );
 	}
 
 
 	/**
 	 * Get the {@link CollectionLoadContext} associated with the given
 	 * {@link ResultSet}, creating one if needed.
 	 *
 	 * @param resultSet The result set for which to retrieve the context.
 	 * @return The processing context.
 	 */
 	public CollectionLoadContext getCollectionLoadContext(ResultSet resultSet) {
 		CollectionLoadContext context = null;
         if (collectionLoadContexts == null) collectionLoadContexts = IdentityMap.instantiate(8);
         else context = (CollectionLoadContext)collectionLoadContexts.get(resultSet);
 		if ( context == null ) {
             LOG.trace("Constructing collection load context for result set [" + resultSet + "]");
 			context = new CollectionLoadContext( this, resultSet );
 			collectionLoadContexts.put( resultSet, context );
 		}
 		return context;
 	}
 
 	/**
 	 * Attempt to locate the loading collection given the owner's key.  The lookup here
 	 * occurs against all result-set contexts...
 	 *
 	 * @param persister The collection persister
 	 * @param ownerKey The owner key
 	 * @return The loading collection, or null if not found.
 	 */
 	public PersistentCollection locateLoadingCollection(CollectionPersister persister, Serializable ownerKey) {
 		LoadingCollectionEntry lce = locateLoadingCollectionEntry( new CollectionKey( persister, ownerKey, getEntityMode() ) );
 		if ( lce != null ) {
             if (LOG.isTraceEnabled()) LOG.trace("Returning loading collection: "
                                                 + MessageHelper.collectionInfoString(persister, ownerKey, getSession().getFactory()));
 			return lce.getCollection();
 		}
         // TODO : should really move this log statement to CollectionType, where this is used from...
         if (LOG.isTraceEnabled()) LOG.trace("Creating collection wrapper: "
                                             + MessageHelper.collectionInfoString(persister, ownerKey, getSession().getFactory()));
         return null;
 	}
 
 	// loading collection xrefs ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Register a loading collection xref.
 	 * <p/>
 	 * This xref map is used because sometimes a collection is in process of
 	 * being loaded from one result set, but needs to be accessed from the
 	 * context of another "nested" result set processing.
 	 * <p/>
 	 * Implementation note: package protected, as this is meant solely for use
 	 * by {@link CollectionLoadContext} to be able to locate collections
 	 * being loaded by other {@link CollectionLoadContext}s/{@link ResultSet}s.
 	 *
 	 * @param entryKey The xref collection key
 	 * @param entry The corresponding loading collection entry
 	 */
 	void registerLoadingCollectionXRef(CollectionKey entryKey, LoadingCollectionEntry entry) {
 		if ( xrefLoadingCollectionEntries == null ) {
 			xrefLoadingCollectionEntries = new HashMap();
 		}
 		xrefLoadingCollectionEntries.put( entryKey, entry );
 	}
 
 	/**
 	 * The inverse of {@link #registerLoadingCollectionXRef}.  Here, we are done
 	 * processing the said collection entry, so we remove it from the
 	 * load context.
 	 * <p/>
 	 * The idea here is that other loading collections can now reference said
 	 * collection directly from the {@link PersistenceContext} because it
 	 * has completed its load cycle.
 	 * <p/>
 	 * Implementation note: package protected, as this is meant solely for use
 	 * by {@link CollectionLoadContext} to be able to locate collections
 	 * being loaded by other {@link CollectionLoadContext}s/{@link ResultSet}s.
 	 *
 	 * @param key The key of the collection we are done processing.
 	 */
 	void unregisterLoadingCollectionXRef(CollectionKey key) {
 		if ( !hasRegisteredLoadingCollectionEntries() ) {
 			return;
 		}
 		xrefLoadingCollectionEntries.remove(key);
 	 }
 
 	/*package*/Map getLoadingCollectionXRefs() {
  		return xrefLoadingCollectionEntries;
  	}
 
 
 	/**
 	 * Locate the LoadingCollectionEntry within *any* of the tracked
 	 * {@link CollectionLoadContext}s.
 	 * <p/>
 	 * Implementation note: package protected, as this is meant solely for use
 	 * by {@link CollectionLoadContext} to be able to locate collections
 	 * being loaded by other {@link CollectionLoadContext}s/{@link ResultSet}s.
 	 *
 	 * @param key The collection key.
 	 * @return The located entry; or null.
 	 */
 	LoadingCollectionEntry locateLoadingCollectionEntry(CollectionKey key) {
         if (xrefLoadingCollectionEntries == null) return null;
         LOG.trace("Attempting to locate loading collection entry [" + key + "] in any result-set context");
 		LoadingCollectionEntry rtn = ( LoadingCollectionEntry ) xrefLoadingCollectionEntries.get( key );
         if (rtn == null) LOG.trace("Collection [" + key + "] not located in load context");
         else LOG.trace("Collection [" + key + "] located in load context");
 		return rtn;
 	}
 
 	/*package*/void cleanupCollectionXRefs(Set entryKeys) {
 		Iterator itr = entryKeys.iterator();
 		while ( itr.hasNext() ) {
 			final CollectionKey entryKey = ( CollectionKey ) itr.next();
 			xrefLoadingCollectionEntries.remove( entryKey );
 		}
 	}
 
 
 	// Entity load contexts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// 	* currently, not yet used...
 
 	public EntityLoadContext getEntityLoadContext(ResultSet resultSet) {
 		EntityLoadContext context = null;
 		if ( entityLoadContexts == null ) {
 			entityLoadContexts = IdentityMap.instantiate( 8 );
 		}
 		else {
 			context = ( EntityLoadContext ) entityLoadContexts.get( resultSet );
 		}
 		if ( context == null ) {
 			context = new EntityLoadContext( this, resultSet );
 			entityLoadContexts.put( resultSet, context );
 		}
 		return context;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadingCollectionEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadingCollectionEntry.java
index 99fba4ee9c..e16fc78999 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadingCollectionEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/LoadingCollectionEntry.java
@@ -1,73 +1,73 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
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
- *
- */
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
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
+ *
+ */
 package org.hibernate.engine.loading;
-import java.io.Serializable;
-import java.sql.ResultSet;
-import org.hibernate.collection.PersistentCollection;
-import org.hibernate.persister.collection.CollectionPersister;
-import org.hibernate.pretty.MessageHelper;
-
-/**
- * Represents a collection currently being loaded.
- *
- * @author Steve Ebersole
- */
-public class LoadingCollectionEntry {
-	private final ResultSet resultSet;
-	private final CollectionPersister persister;
-	private final Serializable key;
-	private final PersistentCollection collection;
-
-	public LoadingCollectionEntry(
-			ResultSet resultSet,
-			CollectionPersister persister,
-			Serializable key,
-			PersistentCollection collection) {
-		this.resultSet = resultSet;
-		this.persister = persister;
-		this.key = key;
-		this.collection = collection;
-	}
-
-	public ResultSet getResultSet() {
-		return resultSet;
-	}
-
-	public CollectionPersister getPersister() {
-		return persister;
-	}
-
-	public Serializable getKey() {
-		return key;
-	}
-
-	public PersistentCollection getCollection() {
-		return collection;
-	}
-
-	public String toString() {
-		return getClass().getName() + "<rs=" + resultSet + ", coll=" + MessageHelper.collectionInfoString( persister.getRole(), key ) + ">@" + Integer.toHexString( hashCode() );
-	}
-}
+import java.io.Serializable;
+import java.sql.ResultSet;
+import org.hibernate.collection.spi.PersistentCollection;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.pretty.MessageHelper;
+
+/**
+ * Represents a collection currently being loaded.
+ *
+ * @author Steve Ebersole
+ */
+public class LoadingCollectionEntry {
+	private final ResultSet resultSet;
+	private final CollectionPersister persister;
+	private final Serializable key;
+	private final PersistentCollection collection;
+
+	public LoadingCollectionEntry(
+			ResultSet resultSet,
+			CollectionPersister persister,
+			Serializable key,
+			PersistentCollection collection) {
+		this.resultSet = resultSet;
+		this.persister = persister;
+		this.key = key;
+		this.collection = collection;
+	}
+
+	public ResultSet getResultSet() {
+		return resultSet;
+	}
+
+	public CollectionPersister getPersister() {
+		return persister;
+	}
+
+	public Serializable getKey() {
+		return key;
+	}
+
+	public PersistentCollection getCollection() {
+		return collection;
+	}
+
+	public String toString() {
+		return getClass().getName() + "<rs=" + resultSet + ", coll=" + MessageHelper.collectionInfoString( persister.getRole(), key ) + ">@" + Integer.toHexString( hashCode() );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/event/AbstractCollectionEvent.java b/hibernate-core/src/main/java/org/hibernate/event/AbstractCollectionEvent.java
index becb484e3a..0c815c9a7e 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/AbstractCollectionEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/AbstractCollectionEvent.java
@@ -1,136 +1,136 @@
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
 package org.hibernate.event;
 import java.io.Serializable;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.CollectionEntry;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * Defines a base class for events involving collections.
  *
  * @author Gail Badner
  */
 public abstract class AbstractCollectionEvent extends AbstractEvent {
 
 	private final PersistentCollection collection;
 	private final Object affectedOwner;
 	private final Serializable affectedOwnerId;
 	private final String affectedOwnerEntityName;
 
 	/**
 	 * Constructs an AbstractCollectionEvent object.
 	 *
 	 * @param collection - the collection
 	 * @param source - the Session source
 	 * @param affectedOwner - the owner that is affected by this event;
 	 * can be null if unavailable
 	 * @param affectedOwnerId - the ID for the owner that is affected
 	 * by this event; can be null if unavailable
 	 * that is affected by this event; can be null if unavailable
 	 */
 	public AbstractCollectionEvent( CollectionPersister collectionPersister,
 					PersistentCollection collection,
 					EventSource source,
 					Object affectedOwner,
 					Serializable affectedOwnerId) {
 		super(source);
 		this.collection = collection;
 		this.affectedOwner = affectedOwner;
 		this.affectedOwnerId = affectedOwnerId;
 		this.affectedOwnerEntityName =
 				getAffectedOwnerEntityName( collectionPersister, affectedOwner, source );
 	}
 
 	protected static CollectionPersister getLoadedCollectionPersister( PersistentCollection collection, EventSource source ) {
 		CollectionEntry ce = source.getPersistenceContext().getCollectionEntry( collection );
 		return ( ce == null ? null : ce.getLoadedPersister() );		
 	}
 
 	protected static Object getLoadedOwnerOrNull( PersistentCollection collection, EventSource source ) {
 		return source.getPersistenceContext().getLoadedCollectionOwnerOrNull( collection );
 	}
 
 	protected static Serializable getLoadedOwnerIdOrNull( PersistentCollection collection, EventSource source ) {
 		return source.getPersistenceContext().getLoadedCollectionOwnerIdOrNull( collection );
 	}
 
 	protected static Serializable getOwnerIdOrNull( Object owner, EventSource source ) {
 		EntityEntry ownerEntry = source.getPersistenceContext().getEntry( owner );
 		return ( ownerEntry == null ? null : ownerEntry.getId() );
 	}
 
 	protected static String getAffectedOwnerEntityName(CollectionPersister collectionPersister, Object affectedOwner, EventSource source ) {
 
 		// collectionPersister should not be null, but we don't want to throw
 		// an exception if it is null
 		String entityName =
 				( collectionPersister == null ? null : collectionPersister.getOwnerEntityPersister().getEntityName() );
 		if ( affectedOwner != null ) {
 			EntityEntry ee = source.getPersistenceContext().getEntry( affectedOwner );
 			if ( ee != null && ee.getEntityName() != null) {
 				entityName = ee.getEntityName();
 			}
 		}	
 		return entityName;
 	}
 
 	public PersistentCollection getCollection() {
 		return collection;
 	}
 
 	/**
 	 * Get the collection owner entity that is affected by this event.
 	 *
 	 * @return the affected owner; returns null if the entity is not in the persistence context
 	 * (e.g., because the collection from a detached entity was moved to a new owner)
 	 */
 	public Object getAffectedOwnerOrNull() {
 		return affectedOwner;
 	}
 
 	/**
 	 * Get the ID for the collection owner entity that is affected by this event.
 	 *
 	 * @return the affected owner ID; returns null if the ID cannot be obtained
 	 * from the collection's loaded key (e.g., a property-ref is used for the
 	 * collection and does not include the entity's ID)
 	 */
 	public Serializable getAffectedOwnerIdOrNull() {
 		return affectedOwnerId;
 	}
 
 	/**
 	 * Get the entity name for the collection owner entity that is affected by this event.
 	 *
 	 * @return the entity name; if the owner is not in the PersistenceContext, the
 	 * returned value may be a superclass name, instead of the actual class name
 	 */
 	public String getAffectedOwnerEntityName() {
 		return affectedOwnerEntityName;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/InitializeCollectionEvent.java b/hibernate-core/src/main/java/org/hibernate/event/InitializeCollectionEvent.java
index b95f52476c..243b9cc31d 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/InitializeCollectionEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/InitializeCollectionEvent.java
@@ -1,43 +1,43 @@
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
 package org.hibernate.event;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 
 /**
  * An event that occurs when a collection wants to be
  * initialized
  * 
  * @author Gavin King
  */
 public class InitializeCollectionEvent extends AbstractCollectionEvent {
 
 	public InitializeCollectionEvent(PersistentCollection collection, EventSource source ) {
 		super( getLoadedCollectionPersister( collection, source ),
 				collection,
 				source,
 				getLoadedOwnerOrNull( collection, source ),
 				getLoadedOwnerIdOrNull( collection, source ) );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/PostCollectionRecreateEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PostCollectionRecreateEvent.java
index ea478886f0..b330e7bb58 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/PostCollectionRecreateEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/PostCollectionRecreateEvent.java
@@ -1,43 +1,43 @@
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
 package org.hibernate.event;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * An event that occurs after a collection is recreated
  *
  * @author Gail Badner
  */
 public class PostCollectionRecreateEvent extends AbstractCollectionEvent {
 
 	public PostCollectionRecreateEvent( CollectionPersister collectionPersister,
 										PersistentCollection collection,
 										EventSource source ) {
 		super( collectionPersister, collection, source,
 				collection.getOwner(),
 				getOwnerIdOrNull( collection.getOwner(), source ) );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/PostCollectionRemoveEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PostCollectionRemoveEvent.java
index c5de3a86c4..0523d57461 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/PostCollectionRemoveEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/PostCollectionRemoveEvent.java
@@ -1,44 +1,44 @@
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
 package org.hibernate.event;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * An event that occurs after a collection is removed
  *
  * @author Gail Badner
  */
 public class PostCollectionRemoveEvent extends AbstractCollectionEvent {
 
 	public PostCollectionRemoveEvent(CollectionPersister collectionPersister,
 									 PersistentCollection collection,
 									 EventSource source,
 									 Object loadedOwner ) {
 		super( collectionPersister, collection, source,
 				loadedOwner,
 				getOwnerIdOrNull( loadedOwner, source ) );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/PostCollectionUpdateEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PostCollectionUpdateEvent.java
index 0c1a974b84..44255aaac6 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/PostCollectionUpdateEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/PostCollectionUpdateEvent.java
@@ -1,43 +1,43 @@
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
 package org.hibernate.event;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * An event that occurs after a collection is updated
  *
  * @author Gail Badner
  */
 public class PostCollectionUpdateEvent extends AbstractCollectionEvent {
 
 	public PostCollectionUpdateEvent(CollectionPersister collectionPersister,
 									 PersistentCollection collection,
 									 EventSource source) {
 		super( collectionPersister, collection, source,
 				getLoadedOwnerOrNull( collection, source ),
 				getLoadedOwnerIdOrNull( collection, source ) );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/PreCollectionRecreateEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PreCollectionRecreateEvent.java
index 92e2bbfb7c..ea7cc9f42b 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/PreCollectionRecreateEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/PreCollectionRecreateEvent.java
@@ -1,43 +1,43 @@
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
 package org.hibernate.event;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * An event that occurs before a collection is recreated
  *
  * @author Gail Badner
  */
 public class PreCollectionRecreateEvent extends AbstractCollectionEvent {
 
 	public PreCollectionRecreateEvent(CollectionPersister collectionPersister,
 									  PersistentCollection collection,
 									  EventSource source) {
 		super( collectionPersister, collection, source,
 				collection.getOwner(),
 				getOwnerIdOrNull( collection.getOwner(), source ) );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/PreCollectionRemoveEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PreCollectionRemoveEvent.java
index de0ec6115a..bbed9fe141 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/PreCollectionRemoveEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/PreCollectionRemoveEvent.java
@@ -1,44 +1,44 @@
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
 package org.hibernate.event;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * An event that occurs before a collection is removed
  *
  * @author Gail Badner
  */
 public class PreCollectionRemoveEvent extends AbstractCollectionEvent {
 
 	public PreCollectionRemoveEvent(CollectionPersister collectionPersister,
 									PersistentCollection collection,
 									EventSource source,
 									Object loadedOwner) {
 		super( collectionPersister, collection, source,
 				loadedOwner,
 				getOwnerIdOrNull( loadedOwner, source ) );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/PreCollectionUpdateEvent.java b/hibernate-core/src/main/java/org/hibernate/event/PreCollectionUpdateEvent.java
index fc3f964b72..31d3007ece 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/PreCollectionUpdateEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/PreCollectionUpdateEvent.java
@@ -1,43 +1,43 @@
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
 package org.hibernate.event;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * An event that occurs before a collection is updated
  *
  * @author Gail Badner
  */
 public class PreCollectionUpdateEvent extends AbstractCollectionEvent {
 
 	public PreCollectionUpdateEvent(CollectionPersister collectionPersister,
 									PersistentCollection collection,
 									EventSource source) {
 		super( collectionPersister, collection, source,
 				getLoadedOwnerOrNull( collection, source ),
 				getLoadedOwnerIdOrNull( collection, source ) );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractFlushingEventListener.java
index c0b0c14dd3..8d632aecae 100644
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
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.action.internal.CollectionRecreateAction;
 import org.hibernate.action.internal.CollectionRemoveAction;
 import org.hibernate.action.internal.CollectionUpdateAction;
-import org.hibernate.collection.PersistentCollection;
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
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultInitializeCollectionEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultInitializeCollectionEventListener.java
index 24163fbf99..0a67d94bef 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultInitializeCollectionEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultInitializeCollectionEventListener.java
@@ -1,146 +1,146 @@
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
 package org.hibernate.event.def;
 
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.CacheKey;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.cache.spi.entry.CollectionCacheEntry;
-import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.CollectionEntry;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.InitializeCollectionEvent;
 import org.hibernate.event.InitializeCollectionEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * @author Gavin King
  */
 public class DefaultInitializeCollectionEventListener implements InitializeCollectionEventListener {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultInitializeCollectionEventListener.class.getName());
 
 	/**
 	 * called by a collection that wants to initialize itself
 	 */
 	public void onInitializeCollection(InitializeCollectionEvent event)
 	throws HibernateException {
 
 		PersistentCollection collection = event.getCollection();
 		SessionImplementor source = event.getSession();
 
 		CollectionEntry ce = source.getPersistenceContext().getCollectionEntry(collection);
 		if (ce==null) throw new HibernateException("collection was evicted");
 		if ( !collection.wasInitialized() ) {
             if (LOG.isTraceEnabled()) LOG.trace("Initializing collection "
                                                 + MessageHelper.collectionInfoString(ce.getLoadedPersister(),
                                                                                      ce.getLoadedKey(),
                                                                                      source.getFactory()));
 
             LOG.trace("Checking second-level cache");
 			final boolean foundInCache = initializeCollectionFromCache(
 					ce.getLoadedKey(),
 					ce.getLoadedPersister(),
 					collection,
 					source
 				);
 
             if (foundInCache) LOG.trace("Collection initialized from cache");
 			else {
                 LOG.trace("Collection not cached");
 				ce.getLoadedPersister().initialize( ce.getLoadedKey(), source );
                 LOG.trace("Collection initialized");
 
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
 	 * @return true if we were able to initialize the collection from the cache;
 	 * false otherwise.
 	 */
 	private boolean initializeCollectionFromCache(
 			Serializable id,
 			CollectionPersister persister,
 			PersistentCollection collection,
 			SessionImplementor source) {
 
 		if ( !source.getEnabledFilters().isEmpty() && persister.isAffectedByEnabledFilters( source ) ) {
             LOG.trace("Disregarding cached version (if any) of collection due to enabled filters");
 			return false;
 		}
 
 		final boolean useCache = persister.hasCache() &&
 				source.getCacheMode().isGetEnabled();
 
         if (!useCache) return false;
 
         final SessionFactoryImplementor factory = source.getFactory();
 
         final CacheKey ck = source.generateCacheKey( id, persister.getKeyType(), persister.getRole() );
         Object ce = persister.getCacheAccessStrategy().get(ck, source.getTimestamp());
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
             if (ce == null) {
                 factory.getStatisticsImplementor()
 						.secondLevelCacheMiss( persister.getCacheAccessStrategy().getRegion().getName() );
             }
 			else {
                 factory.getStatisticsImplementor()
 						.secondLevelCacheHit( persister.getCacheAccessStrategy().getRegion().getName() );
             }
 		}
 
         if ( ce == null ) {
 			return false;
 		}
 
 		CollectionCacheEntry cacheEntry = (CollectionCacheEntry)persister.getCacheEntryStructure().destructure(ce, factory);
 
 		final PersistenceContext persistenceContext = source.getPersistenceContext();
         cacheEntry.assemble(collection, persister, persistenceContext.getCollectionOwner(id, persister));
         persistenceContext.getCollectionEntry(collection).postInitialize(collection);
         // addInitializedCollection(collection, persister, id);
         return true;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DirtyCollectionSearchVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/DirtyCollectionSearchVisitor.java
index b5f74b85b6..2f70381d2d 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DirtyCollectionSearchVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DirtyCollectionSearchVisitor.java
@@ -1,88 +1,88 @@
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
 import org.hibernate.HibernateException;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.type.CollectionType;
 
 /**
  * Do we have a dirty collection here?
  * 1. if it is a new application-instantiated collection, return true (does not occur anymore!)
  * 2. if it is a component, recurse
  * 3. if it is a wrappered collection, ask the collection entry
  *
  * @author Gavin King
  */
 public class DirtyCollectionSearchVisitor extends AbstractVisitor {
 
 	private boolean dirty = false;
 	private boolean[] propertyVersionability;
 
 	DirtyCollectionSearchVisitor(EventSource session, boolean[] propertyVersionability) {
 		super(session);
 		this.propertyVersionability = propertyVersionability;
 	}
 
 	boolean wasDirtyCollectionFound() {
 		return dirty;
 	}
 
 	Object processCollection(Object collection, CollectionType type)
 		throws HibernateException {
 
 		if (collection!=null) {
 
 			SessionImplementor session = getSession();
 
 			final PersistentCollection persistentCollection;
 			if ( type.isArrayType() ) {
 				 persistentCollection = session.getPersistenceContext().getCollectionHolder(collection);
 				// if no array holder we found an unwrappered array (this can't occur,
 				// because we now always call wrap() before getting to here)
 				// return (ah==null) ? true : searchForDirtyCollections(ah, type);
 			}
 			else {
 				// if not wrappered yet, its dirty (this can't occur, because
 				// we now always call wrap() before getting to here)
 				// return ( ! (obj instanceof PersistentCollection) ) ?
 				//true : searchForDirtyCollections( (PersistentCollection) obj, type );
 				persistentCollection = (PersistentCollection) collection;
 			}
 
 			if ( persistentCollection.isDirty() ) { //we need to check even if it was not initialized, because of delayed adds!
 				dirty=true;
 				return null; //NOTE: EARLY EXIT!
 			}
 		}
 
 		return null;
 	}
 
 	boolean includeEntityProperty(Object[] values, int i) {
 		return propertyVersionability[i] && super.includeEntityProperty(values, i);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/EvictVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/EvictVisitor.java
index aa9b89f453..8c205d8a11 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/EvictVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/EvictVisitor.java
@@ -1,89 +1,89 @@
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
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.CollectionEntry;
 import org.hibernate.engine.CollectionKey;
 import org.hibernate.event.EventSource;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.CollectionType;
 import org.jboss.logging.Logger;
 
 /**
  * Evict any collections referenced by the object from the session cache.
  * This will NOT pick up any collections that were dereferenced, so they
  * will be deleted (suboptimal but not exactly incorrect).
  *
  * @author Gavin King
  */
 public class EvictVisitor extends AbstractVisitor {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EvictVisitor.class.getName());
 
 	EvictVisitor(EventSource session) {
 		super(session);
 	}
 
 	@Override
     Object processCollection(Object collection, CollectionType type)
 		throws HibernateException {
 
 		if (collection!=null) evictCollection(collection, type);
 
 		return null;
 	}
 	public void evictCollection(Object value, CollectionType type) {
 
 		final Object pc;
 		if ( type.hasHolder( getSession().getEntityMode() ) ) {
 			pc = getSession().getPersistenceContext().removeCollectionHolder(value);
 		}
 		else if ( value instanceof PersistentCollection ) {
 			pc = value;
 		}
 		else {
 			return; //EARLY EXIT!
 		}
 
 		PersistentCollection collection = (PersistentCollection) pc;
 		if ( collection.unsetSession( getSession() ) ) evictCollection(collection);
 	}
 
 	private void evictCollection(PersistentCollection collection) {
 		CollectionEntry ce = (CollectionEntry) getSession().getPersistenceContext().getCollectionEntries().remove(collection);
         if (LOG.isDebugEnabled()) LOG.debugf("Evicting collection: %s",
                                              MessageHelper.collectionInfoString(ce.getLoadedPersister(),
                                                                                 ce.getLoadedKey(),
                                                                                 getSession().getFactory()));
 		if ( ce.getLoadedPersister() != null && ce.getLoadedKey() != null ) {
 			//TODO: is this 100% correct?
 			getSession().getPersistenceContext().getCollectionsByKey().remove(
 					new CollectionKey( ce.getLoadedPersister(), ce.getLoadedKey(), getSession().getEntityMode() )
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/FlushVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/FlushVisitor.java
index 0e189b31ec..4fb5974237 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/FlushVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/FlushVisitor.java
@@ -1,71 +1,71 @@
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
 import org.hibernate.HibernateException;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.Collections;
 import org.hibernate.event.EventSource;
 import org.hibernate.type.CollectionType;
 
 /**
  * Process collections reachable from an entity. This
  * visitor assumes that wrap was already performed for
  * the entity.
  *
  * @author Gavin King
  */
 public class FlushVisitor extends AbstractVisitor {
 	
 	private Object owner;
 
 	Object processCollection(Object collection, CollectionType type)
 	throws HibernateException {
 		
 		if (collection==CollectionType.UNFETCHED_COLLECTION) {
 			return null;
 		}
 
 		if (collection!=null) {
 			final PersistentCollection coll;
 			if ( type.hasHolder( getSession().getEntityMode() ) ) {
 				coll = getSession().getPersistenceContext().getCollectionHolder(collection);
 			}
 			else {
 				coll = (PersistentCollection) collection;
 			}
 
 			Collections.processReachableCollection( coll, type, owner, getSession() );
 		}
 
 		return null;
 
 	}
 
 	FlushVisitor(EventSource session, Object owner) {
 		super(session);
 		this.owner = owner;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/OnLockVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/OnLockVisitor.java
index ecff853c8b..e7003f0b8f 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/OnLockVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/OnLockVisitor.java
@@ -1,89 +1,89 @@
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
 import org.hibernate.HibernateException;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.CollectionType;
 
 /**
  * When a transient entity is passed to lock(), we must inspect all its collections and
  * 1. associate any uninitialized PersistentCollections with this session
  * 2. associate any initialized PersistentCollections with this session, using the
  * existing snapshot
  * 3. throw an exception for each "new" collection
  *
  * @author Gavin King
  */
 public class OnLockVisitor extends ReattachVisitor {
 
 	public OnLockVisitor(EventSource session, Serializable key, Object owner) {
 		super( session, key, owner );
 	}
 
 	Object processCollection(Object collection, CollectionType type) throws HibernateException {
 
 		SessionImplementor session = getSession();
 		CollectionPersister persister = session.getFactory().getCollectionPersister( type.getRole() );
 
 		if ( collection == null ) {
 			//do nothing
 		}
 		else if ( collection instanceof PersistentCollection ) {
 			PersistentCollection persistentCollection = ( PersistentCollection ) collection;
 			if ( persistentCollection.setCurrentSession( session ) ) {
 				if ( isOwnerUnchanged( persistentCollection, persister, extractCollectionKeyFromOwner( persister ) ) ) {
 					// a "detached" collection that originally belonged to the same entity
 					if ( persistentCollection.isDirty() ) {
 						throw new HibernateException( "reassociated object has dirty collection" );
 					}
 					reattachCollection( persistentCollection, type );
 				}
 				else {
 					// a "detached" collection that belonged to a different entity
 					throw new HibernateException( "reassociated object has dirty collection reference" );
 				}
 			}
 			else {
 				// a collection loaded in the current session
 				// can not possibly be the collection belonging
 				// to the entity passed to update()
 				throw new HibernateException( "reassociated object has dirty collection reference" );
 			}
 		}
 		else {
 			// brand new collection
 			//TODO: or an array!! we can't lock objects with arrays now??
 			throw new HibernateException( "reassociated object has dirty collection reference (or an array)" );
 		}
 
 		return null;
 
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/OnReplicateVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/OnReplicateVisitor.java
index 170ae7dc4a..b71bb33325 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/OnReplicateVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/OnReplicateVisitor.java
@@ -1,87 +1,87 @@
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
 import org.hibernate.HibernateException;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.event.EventSource;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.CollectionType;
 
 /**
  * When an entity is passed to replicate(), and there is an existing row, we must
  * inspect all its collections and
  * 1. associate any uninitialized PersistentCollections with this session
  * 2. associate any initialized PersistentCollections with this session, using the
  * existing snapshot
  * 3. execute a collection removal (SQL DELETE) for each null collection property
  * or "new" collection
  *
  * @author Gavin King
  */
 public class OnReplicateVisitor extends ReattachVisitor {
 
 	private boolean isUpdate;
 
 	OnReplicateVisitor(EventSource session, Serializable key, Object owner, boolean isUpdate) {
 		super( session, key, owner );
 		this.isUpdate = isUpdate;
 	}
 
 	Object processCollection(Object collection, CollectionType type)
 			throws HibernateException {
 
 		if ( collection == CollectionType.UNFETCHED_COLLECTION ) {
 			return null;
 		}
 
 		EventSource session = getSession();
 		CollectionPersister persister = session.getFactory().getCollectionPersister( type.getRole() );
 
 		if ( isUpdate ) {
 			removeCollection( persister, extractCollectionKeyFromOwner( persister ), session );
 		}
 		if ( collection != null && ( collection instanceof PersistentCollection ) ) {
 			PersistentCollection wrapper = ( PersistentCollection ) collection;
 			wrapper.setCurrentSession( session );
 			if ( wrapper.wasInitialized() ) {
 				session.getPersistenceContext().addNewCollection( persister, wrapper );
 			}
 			else {
 				reattachCollection( wrapper, type );
 			}
 		}
 		else {
 			// otherwise a null or brand new collection
 			// this will also (inefficiently) handle arrays, which
 			// have no snapshot, so we can't do any better
 			//processArrayOrNewCollection(collection, type);
 		}
 
 		return null;
 
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/OnUpdateVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/OnUpdateVisitor.java
index 3edba0ede9..cc4b58d78d 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/OnUpdateVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/OnUpdateVisitor.java
@@ -1,90 +1,90 @@
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
 import org.hibernate.HibernateException;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.event.EventSource;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.CollectionType;
 
 /**
  * When an entity is passed to update(), we must inspect all its collections and
  * 1. associate any uninitialized PersistentCollections with this session
  * 2. associate any initialized PersistentCollections with this session, using the
  *    existing snapshot
  * 3. execute a collection removal (SQL DELETE) for each null collection property
  *    or "new" collection
  *
  * @author Gavin King
  */
 public class OnUpdateVisitor extends ReattachVisitor {
 
 	OnUpdateVisitor(EventSource session, Serializable key, Object owner) {
 		super( session, key, owner );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	Object processCollection(Object collection, CollectionType type) throws HibernateException {
 
 		if ( collection == CollectionType.UNFETCHED_COLLECTION ) {
 			return null;
 		}
 
 		EventSource session = getSession();
 		CollectionPersister persister = session.getFactory().getCollectionPersister( type.getRole() );
 
 		final Serializable collectionKey = extractCollectionKeyFromOwner( persister );
 		if ( collection!=null && (collection instanceof PersistentCollection) ) {
 			PersistentCollection wrapper = (PersistentCollection) collection;
 			if ( wrapper.setCurrentSession(session) ) {
 				//a "detached" collection!
 				if ( !isOwnerUnchanged( wrapper, persister, collectionKey ) ) {
 					// if the collection belonged to a different entity,
 					// clean up the existing state of the collection
 					removeCollection( persister, collectionKey, session );
 				}
 				reattachCollection(wrapper, type);
 			}
 			else {
 				// a collection loaded in the current session
 				// can not possibly be the collection belonging
 				// to the entity passed to update()
 				removeCollection(persister, collectionKey, session);
 			}
 		}
 		else {
 			// null or brand new collection
 			// this will also (inefficiently) handle arrays, which have
 			// no snapshot, so we can't do any better
 			removeCollection(persister, collectionKey, session);
 		}
 
 		return null;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/ProxyVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/ProxyVisitor.java
index cb81130c1a..4c6eef49af 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/ProxyVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/ProxyVisitor.java
@@ -1,100 +1,100 @@
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
 import org.hibernate.HibernateException;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.event.EventSource;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.EntityType;
 
 /**
  * Reassociates uninitialized proxies with the session
  * @author Gavin King
  */
 public abstract class ProxyVisitor extends AbstractVisitor {
 
 
 	public ProxyVisitor(EventSource session) {
 		super(session);
 	}
 
 	Object processEntity(Object value, EntityType entityType) throws HibernateException {
 
 		if (value!=null) {
 			getSession().getPersistenceContext().reassociateIfUninitializedProxy(value);
 			// if it is an initialized proxy, let cascade
 			// handle it later on
 		}
 
 		return null;
 	}
 
 	/**
 	 * Has the owner of the collection changed since the collection
 	 * was snapshotted and detached?
 	 */
 	protected static boolean isOwnerUnchanged(
 			final PersistentCollection snapshot, 
 			final CollectionPersister persister, 
 			final Serializable id
 	) {
 		return isCollectionSnapshotValid(snapshot) &&
 				persister.getRole().equals( snapshot.getRole() ) &&
 				id.equals( snapshot.getKey() );
 	}
 
 	private static boolean isCollectionSnapshotValid(PersistentCollection snapshot) {
 		return snapshot != null &&
 				snapshot.getRole() != null &&
 				snapshot.getKey() != null;
 	}
 	
 	/**
 	 * Reattach a detached (disassociated) initialized or uninitialized
 	 * collection wrapper, using a snapshot carried with the collection
 	 * wrapper
 	 */
 	protected void reattachCollection(PersistentCollection collection, CollectionType type)
 	throws HibernateException {
 		if ( collection.wasInitialized() ) {
 			CollectionPersister collectionPersister = getSession().getFactory()
 			.getCollectionPersister( type.getRole() );
 			getSession().getPersistenceContext()
 				.addInitializedDetachedCollection( collectionPersister, collection );
 		}
 		else {
 			if ( !isCollectionSnapshotValid(collection) ) {
 				throw new HibernateException( "could not reassociate uninitialized transient collection" );
 			}
 			CollectionPersister collectionPersister = getSession().getFactory()
 					.getCollectionPersister( collection.getRole() );
 			getSession().getPersistenceContext()
 				.addUninitializedDetachedCollection( collectionPersister, collection );
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/WrapVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/def/WrapVisitor.java
index 88ba876c57..f0d538af6f 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/WrapVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/WrapVisitor.java
@@ -1,161 +1,161 @@
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
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
 /**
  * Wrap collections in a Hibernate collection
  * wrapper.
  * @author Gavin King
  */
 public class WrapVisitor extends ProxyVisitor {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, WrapVisitor.class.getName());
 
 	boolean substitute = false;
 
 	boolean isSubstitutionRequired() {
 		return substitute;
 	}
 
 	WrapVisitor(EventSource session) {
 		super(session);
 	}
 
 	@Override
     Object processCollection(Object collection, CollectionType collectionType)
 	throws HibernateException {
 
 		if ( collection!=null && (collection instanceof PersistentCollection) ) {
 
 			final SessionImplementor session = getSession();
 			PersistentCollection coll = (PersistentCollection) collection;
 			if ( coll.setCurrentSession(session) ) {
 				reattachCollection( coll, collectionType );
 			}
 			return null;
 
 		}
 		else {
 			return processArrayOrNewCollection(collection, collectionType);
 		}
 
 	}
 
 	final Object processArrayOrNewCollection(Object collection, CollectionType collectionType)
 	throws HibernateException {
 
 		final SessionImplementor session = getSession();
 
 		if (collection==null) {
 			//do nothing
 			return null;
 		}
 		else {
 			CollectionPersister persister = session.getFactory().getCollectionPersister( collectionType.getRole() );
 
 			final PersistenceContext persistenceContext = session.getPersistenceContext();
 			//TODO: move into collection type, so we can use polymorphism!
 			if ( collectionType.hasHolder( session.getEntityMode() ) ) {
 
 				if (collection==CollectionType.UNFETCHED_COLLECTION) return null;
 
 				PersistentCollection ah = persistenceContext.getCollectionHolder(collection);
 				if (ah==null) {
 					ah = collectionType.wrap(session, collection);
 					persistenceContext.addNewCollection( persister, ah );
 					persistenceContext.addCollectionHolder(ah);
 				}
 				return null;
 			}
 			else {
 
 				PersistentCollection persistentCollection = collectionType.wrap(session, collection);
 				persistenceContext.addNewCollection( persister, persistentCollection );
 
                 if (LOG.isTraceEnabled()) LOG.trace("Wrapped collection in role: " + collectionType.getRole());
 
 				return persistentCollection; //Force a substitution!
 
 			}
 
 		}
 
 	}
 
 	@Override
     void processValue(int i, Object[] values, Type[] types) {
 		Object result = processValue( values[i], types[i] );
 		if (result!=null) {
 			substitute = true;
 			values[i] = result;
 		}
 	}
 
 	@Override
     Object processComponent(Object component, CompositeType componentType)
 	throws HibernateException {
 
 		if (component!=null) {
 			Object[] values = componentType.getPropertyValues( component, getSession() );
 			Type[] types = componentType.getSubtypes();
 			boolean substituteComponent = false;
 			for ( int i=0; i<types.length; i++ ) {
 				Object result = processValue( values[i], types[i] );
 				if (result!=null) {
 					values[i] = result;
 					substituteComponent = true;
 				}
 			}
 			if (substituteComponent) {
 				componentType.setPropertyValues( component, values, getSession().getEntityMode() );
 			}
 		}
 
 		return null;
 	}
 
 	@Override
     void process(Object object, EntityPersister persister) throws HibernateException {
 		EntityMode entityMode = getSession().getEntityMode();
 		Object[] values = persister.getPropertyValues( object, entityMode );
 		Type[] types = persister.getPropertyTypes();
 		processEntityPropertyValues(values, types);
 		if ( isSubstitutionRequired() ) {
 			persister.setPropertyValues( object, values, entityMode );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
index ae7519e2a7..d7af907ec7 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
@@ -1,1079 +1,1079 @@
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
 package org.hibernate.internal;
 
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
 import org.hibernate.SessionBuilder;
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
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
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
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
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
 		super( parent.factory, parent.getTenantIdentifier() );
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
 			final ConnectionReleaseMode connectionReleaseMode,
 			final String tenantIdentifier) {
 		super( factory, tenantIdentifier );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
index af3d1f8d4a..35232eb5af 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
@@ -1,707 +1,707 @@
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
 package org.hibernate.internal;
 
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
 import org.hibernate.Interceptor;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.StatelessSession;
 import org.hibernate.Transaction;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cache.spi.CacheKey;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, StatelessSessionImpl.class.getName());
 
 	private TransactionCoordinator transactionCoordinator;
 	private PersistenceContext temporaryPersistenceContext = new StatefulPersistenceContext( this );
 
 	StatelessSessionImpl(Connection connection, String tenantIdentifier, SessionFactoryImpl factory) {
 		super( factory, tenantIdentifier );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 1cb3b488eb..c45383a8a1 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,1055 +1,1055 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009 by Red Hat Inc and/or its affiliates or by
  * third-party contributors as indicated by either @author tags or express
  * copyright attribution statements applied by the authors.  All
  * third-party contributions are distributed under license by Red Hat Inc.
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
 package org.hibernate.loader;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.cache.spi.FilterKey;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.EntityUniqueKey;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.RowSelection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SubselectFetch;
 import org.hibernate.engine.TwoPhaseLoad;
 import org.hibernate.engine.TypedValue;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.PostLoadEvent;
 import org.hibernate.event.PreLoadEvent;
 import org.hibernate.hql.HolderInstantiator;
 import org.hibernate.internal.FetchingScrollableResultsImpl;
 import org.hibernate.internal.ScrollableResultsImpl;
 import org.hibernate.internal.util.StringHelper;
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
 
     protected static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Loader.class.getName());
 
 	private final SessionFactoryImplementor factory;
 	private ColumnNameCache columnNameCache;
 
 	public Loader(SessionFactoryImplementor factory) {
 		this.factory = factory;
 	}
 
 	/**
 	 * The SQL query string to be called; implemented by all subclasses
 	 *
 	 * @return The sql command this loader should use to get its {@link ResultSet}.
 	 */
 	protected abstract String getSQLString();
 
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
 	protected String applyLocks(String sql, LockOptions lockOptions, Dialect dialect) throws HibernateException {
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
 	protected String preprocessSQL(String sql, QueryParameters parameters, Dialect dialect)
 			throws HibernateException {
 
 		sql = applyLocks( sql, parameters.getLockOptions(), dialect );
 
 		return getFactory().getSettings().isCommentsEnabled() ?
 				prependComment( sql, parameters ) : sql;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return new StringBuffer( comment.length() + sql.length() + 5 )
 					.append( "/* " )
 					.append( comment )
 					.append( " */ " )
 					.append( sql )
 					.toString();
 		}
 	}
 
 	/**
 	 * Execute an SQL query and attempt to instantiate instances of the class mapped by the given
 	 * persister from each row of the <tt>ResultSet</tt>. If an object is supplied, will attempt to
 	 * initialize that object. If a collection is supplied, attempt to initialize that collection.
 	 */
 	private List doQueryAndInitializeNonLazyCollections(
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
 
 	private List doQueryAndInitializeNonLazyCollections(
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
 	 * @return The loaded "row".
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
 		catch ( SQLException sqle ) {
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
 				if ( result == null ) {
 					result = loaded;
 				}
 			}
 			while ( keyToRead.equals( loadedKeys[0] ) && resultSet.next() );
 		}
 		catch ( SQLException sqle ) {
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
 	 * @return The loaded "row".
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
 			// key value upon which to doAfterTransactionCompletion the breaking logic.  However,
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
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not doAfterTransactionCompletion sequential read of results (forward)",
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
 	 * @return The loaded "row".
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
 		catch ( SQLException sqle ) {
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
 			return session.generateEntityKey( optionalId, session.getEntityPersister( optionalEntityName, optionalObject ) );
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
 		extractKeysFromResultSet( persisters, queryParameters, resultSet, session, keys, lockModesArray, hydratedObjects );
 
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
 					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setImplementation(entity);
 					row[i] = proxy;
 				}
 			}
 		}
 
 		applyPostLoadLocks( row, lockModesArray, session );
 
 		return forcedResultTransformer == null ?
 				getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session ) :
 				forcedResultTransformer.transformTuple(
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
 			keys[ entitySpan - 1 ] = session.generateEntityKey( optionalId, persisters[ entitySpan - 1 ] );
 			// skip the last persister below...
 			numberOfPersistersToProcess = entitySpan - 1;
 		}
 		else {
 			numberOfPersistersToProcess = entitySpan;
 		}
 
 		final Object[] hydratedKeyState = new Object[numberOfPersistersToProcess];
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			hydratedKeyState[i] = idType.hydrate( resultSet, getEntityAliases()[i].getSuffixedKeyAliases(), session, null );
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
 
 	private Serializable determineResultId(SessionImplementor session, Serializable optionalId, Type idType, Serializable resolvedId) {
 		final boolean idIsResultId = optionalId != null
 				&& resolvedId != null
 				&& idType.isEqual( optionalId, resolvedId, session.getEntityMode(), factory );
 		final Serializable resultId = idIsResultId ? optionalId : resolvedId;
 		return resultId;
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
 
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 
 				final boolean hasCollectionOwners = collectionOwners !=null &&
 						collectionOwners[i] > -1;
 				//true if this is a query and we are loading multiple instances of the same collection role
 				//otherwise this is a CollectionInitializer and we are loading up a single collection or batch
 
 				final Object owner = hasCollectionOwners ?
 						row[ collectionOwners[i] ] :
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
 		final int maxRows = hasMaxRows( selection ) ?
 				selection.getMaxRows().intValue() :
 				Integer.MAX_VALUE;
 
 		final int entitySpan = getEntityPersisters().length;
 
 		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final PreparedStatement st = prepareQueryStatement( queryParameters, false, session );
 		final ResultSet rs = getResultSet( st, queryParameters.hasAutoDiscoverScalarTypes(), queryParameters.isCallable(), selection, session );
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
 		final List results = new ArrayList();
 
 		try {
 
 			handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
 
 			EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
 
             LOG.trace("Processing result set");
 
 			int count;
 			for ( count = 0; count < maxRows && rs.next(); count++ ) {
 
                 LOG.debugf("Result set row: %s", count);
 
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
 					subselectResultKeys.add(keys);
 					keys = new EntityKey[entitySpan]; //can't reuse in this case
 				}
 
 			}
 
             LOG.trace("Done processing result set (" + count + " rows)");
 
 		}
 		finally {
 			st.close();
 		}
 
 		initializeEntitiesAndCollections( hydratedObjects, rs, session, queryParameters.isReadOnly( session ) );
 
 		if ( createSubselects ) createSubselects( subselectResultKeys, queryParameters, session );
 
 		return results; //getResultList(results);
 
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return false;
 	}
 
 	protected boolean hasSubselectLoadableCollections() {
 		final Loadable[] loadables = getEntityPersisters();
 		for (int i=0; i<loadables.length; i++ ) {
 			if ( loadables[i].hasSubselectLoadableCollections() ) return true;
 		}
 		return false;
 	}
 
 	private static Set[] transpose( List keys ) {
 		Set[] result = new Set[ ( ( EntityKey[] ) keys.get(0) ).length ];
 		for ( int j=0; j<result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( int i=0; i<keys.size(); i++ ) {
 				result[j].add( ( ( EntityKey[] ) keys.get(i) ) [j] );
 			}
 		}
 		return result;
 	}
 
 	private void createSubselects(List keys, QueryParameters queryParameters, SessionImplementor session) {
 		if ( keys.size() > 1 ) { //if we only returned one entity, query by key is more efficient
 
 			Set[] keySets = transpose(keys);
 
 			Map namedParameterLocMap = buildNamedParameterLocMap( queryParameters );
 
 			final Loadable[] loadables = getEntityPersisters();
 			final String[] aliases = getAliases();
 			final Iterator iter = keys.iterator();
 			while ( iter.hasNext() ) {
 
 				final EntityKey[] rowKeys = (EntityKey[]) iter.next();
 				for ( int i=0; i<rowKeys.length; i++ ) {
 
 					if ( rowKeys[i]!=null && loadables[i].hasSubselectLoadableCollections() ) {
 
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
 		if ( queryParameters.getNamedParameters()!=null ) {
 			final Map namedParameterLocMap = new HashMap();
 			Iterator piter = queryParameters.getNamedParameters().keySet().iterator();
 			while ( piter.hasNext() ) {
 				String name = (String) piter.next();
 				namedParameterLocMap.put(
 						name,
 						getNamedParameterLocs(name)
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
 			final boolean readOnly)
 	throws HibernateException {
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( collectionPersisters[i].isArray() ) {
 					//for arrays, we should end the collection load before resolving
 					//the entities, since the actual array instances are not instantiated
 					//during loading
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
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
 
 		if ( hydratedObjects!=null ) {
 			int hydratedObjectsSize = hydratedObjects.size();
             LOG.trace("Total objects hydrated: " + hydratedObjectsSize);
 			for ( int i = 0; i < hydratedObjectsSize; i++ ) {
 				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre, post );
 			}
 		}
 
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( !collectionPersisters[i].isArray() ) {
 					//for sets, we should end the collection load after resolving
 					//the entities, since we might call hashCode() on the elements
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
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
 				.getCollectionLoadContext( ( ResultSet ) resultSetId )
 				.endLoadingCollections( collectionPersister );
 	}
 
 	/**
 	 * Determine the actual ResultTransformer that will be used to
 	 * transform query results.
 	 *
 	 * @param resultTransformer the specified result transformer
 	 * @return the actual result transformer
 	 */
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return resultTransformer;
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		return results;
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index f8256e5610..90203044f7 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,1051 +1,1051 @@
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
 package org.hibernate.persister.collection;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StructuredCollectionCacheEntry;
 import org.hibernate.cache.spi.entry.StructuredMapCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.collection.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SubselectFetch;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.exception.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.List;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Table;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.ordering.antlr.ColumnMapper;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 
 /**
  * Base implementation of the <tt>QueryableCollection</tt> interface.
  *
  * @author Gavin King
  * @see BasicCollectionPersister
  * @see OneToManyPersister
  */
 public abstract class AbstractCollectionPersister
 		implements CollectionMetadata, SQLLoadableCollection {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractCollectionPersister.class.getName());
 
     // TODO: encapsulate the protected instance variables!
 
 	private final String role;
 
 	//SQL statements
 	private final String sqlDeleteString;
 	private final String sqlInsertRowString;
 	private final String sqlUpdateRowString;
 	private final String sqlDeleteRowString;
 	private final String sqlSelectSizeString;
 	private final String sqlSelectRowByIndexString;
 	private final String sqlDetectRowByIndexString;
 	private final String sqlDetectRowByElementString;
 
 	protected final String sqlWhereString;
 	private final String sqlOrderByStringTemplate;
 	private final String sqlWhereStringTemplate;
 	private final boolean hasOrder;
 	protected final boolean hasWhere;
 	private final int baseIndex;
 
 	private final String nodeName;
 	private final String elementNodeName;
 	private final String indexNodeName;
 
 	protected final boolean indexContainsFormula;
 	protected final boolean elementIsPureFormula;
 
 	//types
 	private final Type keyType;
 	private final Type indexType;
 	protected final Type elementType;
 	private final Type identifierType;
 
 	//columns
 	protected final String[] keyColumnNames;
 	protected final String[] indexColumnNames;
 	protected final String[] indexFormulaTemplates;
 	protected final String[] indexFormulas;
 	protected final boolean[] indexColumnIsSettable;
 	protected final String[] elementColumnNames;
 	protected final String[] elementColumnWriters;
 	protected final String[] elementColumnReaders;
 	protected final String[] elementColumnReaderTemplates;
 	protected final String[] elementFormulaTemplates;
 	protected final String[] elementFormulas;
 	protected final boolean[] elementColumnIsSettable;
 	protected final boolean[] elementColumnIsInPrimaryKey;
 	protected final String[] indexColumnAliases;
 	protected final String[] elementColumnAliases;
 	protected final String[] keyColumnAliases;
 
 	protected final String identifierColumnName;
 	private final String identifierColumnAlias;
 	//private final String unquotedIdentifierColumnName;
 
 	protected final String qualifiedTableName;
 
 	private final String queryLoaderName;
 
 	private final boolean isPrimitiveArray;
 	private final boolean isArray;
 	protected final boolean hasIndex;
 	protected final boolean hasIdentifier;
 	private final boolean isLazy;
 	private final boolean isExtraLazy;
 	private final boolean isInverse;
 	private final boolean isMutable;
 	private final boolean isVersioned;
 	protected final int batchSize;
 	private final FetchMode fetchMode;
 	private final boolean hasOrphanDelete;
 	private final boolean subselectLoadable;
 
 	//extra information about the element type
 	private final Class elementClass;
 	private final String entityName;
 
 	private final Dialect dialect;
 	private final SqlExceptionHelper sqlExceptionHelper;
 	private final SessionFactoryImplementor factory;
 	private final EntityPersister ownerPersister;
 	private final IdentifierGenerator identifierGenerator;
 	private final PropertyMapping elementPropertyMapping;
 	private final EntityPersister elementPersister;
 	private final CollectionRegionAccessStrategy cacheAccessStrategy;
 	private final CollectionType collectionType;
 	private CollectionInitializer initializer;
 
 	private final CacheEntryStructure cacheEntryStructure;
 
 	// dynamic filters for the collection
 	private final FilterHelper filterHelper;
 
 	// dynamic filters specifically for many-to-many inside the collection
 	private final FilterHelper manyToManyFilterHelper;
 
 	private final String manyToManyWhereString;
 	private final String manyToManyWhereTemplate;
 
 	private final boolean hasManyToManyOrder;
 	private final String manyToManyOrderByTemplate;
 
 	// custom sql
 	private final boolean insertCallable;
 	private final boolean updateCallable;
 	private final boolean deleteCallable;
 	private final boolean deleteAllCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 	private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
 
 	private final Serializable[] spaces;
 
 	private Map collectionPropertyColumnAliases = new HashMap();
 	private Map collectionPropertyColumnNames = new HashMap();
 
 	public AbstractCollectionPersister(
 			final Collection collection,
 			final CollectionRegionAccessStrategy cacheAccessStrategy,
 			final Configuration cfg,
 			final SessionFactoryImplementor factory) throws MappingException, CacheException {
 
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		if ( factory.getSettings().isStructuredCacheEntriesEnabled() ) {
 			cacheEntryStructure = collection.isMap() ?
 					( CacheEntryStructure ) new StructuredMapCacheEntry() :
 					( CacheEntryStructure ) new StructuredCollectionCacheEntry();
 		}
 		else {
 			cacheEntryStructure = new UnstructuredCacheEntry();
 		}
 
 		dialect = factory.getDialect();
 		sqlExceptionHelper = factory.getSQLExceptionHelper();
 		collectionType = collection.getCollectionType();
 		role = collection.getRole();
 		entityName = collection.getOwnerEntityName();
 		ownerPersister = factory.getEntityPersister(entityName);
 		queryLoaderName = collection.getLoaderName();
 		nodeName = collection.getNodeName();
 		isMutable = collection.isMutable();
 
 		Table table = collection.getCollectionTable();
 		fetchMode = collection.getElement().getFetchMode();
 		elementType = collection.getElement().getType();
 		//isSet = collection.isSet();
 		//isSorted = collection.isSorted();
 		isPrimitiveArray = collection.isPrimitiveArray();
 		isArray = collection.isArray();
 		subselectLoadable = collection.isSubselectLoadable();
 
 		qualifiedTableName = table.getQualifiedName(
 				dialect,
 				factory.getSettings().getDefaultCatalogName(),
 				factory.getSettings().getDefaultSchemaName()
 			);
 
 		int spacesSize = 1 + collection.getSynchronizedTables().size();
 		spaces = new String[spacesSize];
 		spaces[0] = qualifiedTableName;
 		Iterator iter = collection.getSynchronizedTables().iterator();
 		for ( int i = 1; i < spacesSize; i++ ) {
 			spaces[i] = (String) iter.next();
 		}
 
 		sqlWhereString = StringHelper.isNotEmpty( collection.getWhere() ) ? "( " + collection.getWhere() + ") " : null;
 		hasWhere = sqlWhereString != null;
 		sqlWhereStringTemplate = hasWhere ?
 				Template.renderWhereStringTemplate(sqlWhereString, dialect, factory.getSqlFunctionRegistry()) :
 				null;
 
 		hasOrphanDelete = collection.hasOrphanDelete();
 
 		int batch = collection.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 
 		isVersioned = collection.isOptimisticLocked();
 
 		// KEY
 
 		keyType = collection.getKey().getType();
 		iter = collection.getKey().getColumnIterator();
 		int keySpan = collection.getKey().getColumnSpan();
 		keyColumnNames = new String[keySpan];
 		keyColumnAliases = new String[keySpan];
 		int k = 0;
 		while ( iter.hasNext() ) {
 			// NativeSQL: collect key column and auto-aliases
 			Column col = ( (Column) iter.next() );
 			keyColumnNames[k] = col.getQuotedName(dialect);
 			keyColumnAliases[k] = col.getAlias(dialect,collection.getOwner().getRootTable());
 			k++;
 		}
 
 		//unquotedKeyColumnNames = StringHelper.unQuote(keyColumnAliases);
 
 		//ELEMENT
 
 		String elemNode = collection.getElementNodeName();
 		if ( elementType.isEntityType() ) {
 			String entityName = ( (EntityType) elementType ).getAssociatedEntityName();
 			elementPersister = factory.getEntityPersister(entityName);
 			if ( elemNode==null ) {
 				elemNode = cfg.getClassMapping(entityName).getNodeName();
 			}
 			// NativeSQL: collect element column and auto-aliases
 
 		}
 		else {
 			elementPersister = null;
 		}
 		elementNodeName = elemNode;
 
 		int elementSpan = collection.getElement().getColumnSpan();
 		elementColumnAliases = new String[elementSpan];
 		elementColumnNames = new String[elementSpan];
 		elementColumnWriters = new String[elementSpan];
 		elementColumnReaders = new String[elementSpan];
 		elementColumnReaderTemplates = new String[elementSpan];
 		elementFormulaTemplates = new String[elementSpan];
 		elementFormulas = new String[elementSpan];
 		elementColumnIsSettable = new boolean[elementSpan];
 		elementColumnIsInPrimaryKey = new boolean[elementSpan];
 		boolean isPureFormula = true;
 		boolean hasNotNullableColumns = false;
 		int j = 0;
 		iter = collection.getElement().getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable selectable = (Selectable) iter.next();
 			elementColumnAliases[j] = selectable.getAlias(dialect);
 			if ( selectable.isFormula() ) {
 				Formula form = (Formula) selectable;
 				elementFormulaTemplates[j] = form.getTemplate(dialect, factory.getSqlFunctionRegistry());
 				elementFormulas[j] = form.getFormula();
 			}
 			else {
 				Column col = (Column) selectable;
 				elementColumnNames[j] = col.getQuotedName(dialect);
 				elementColumnWriters[j] = col.getWriteExpr();
 				elementColumnReaders[j] = col.getReadExpr(dialect);
 				elementColumnReaderTemplates[j] = col.getTemplate(dialect, factory.getSqlFunctionRegistry());
 				elementColumnIsSettable[j] = true;
 				elementColumnIsInPrimaryKey[j] = !col.isNullable();
 				if ( !col.isNullable() ) {
 					hasNotNullableColumns = true;
 				}
 				isPureFormula = false;
 			}
 			j++;
 		}
 		elementIsPureFormula = isPureFormula;
 
 		//workaround, for backward compatibility of sets with no
 		//not-null columns, assume all columns are used in the
 		//row locator SQL
 		if ( !hasNotNullableColumns ) {
 			Arrays.fill( elementColumnIsInPrimaryKey, true );
 		}
 
 
 		// INDEX AND ROW SELECT
 
 		hasIndex = collection.isIndexed();
 		if (hasIndex) {
 			// NativeSQL: collect index column and auto-aliases
 			IndexedCollection indexedCollection = (IndexedCollection) collection;
 			indexType = indexedCollection.getIndex().getType();
 			int indexSpan = indexedCollection.getIndex().getColumnSpan();
 			iter = indexedCollection.getIndex().getColumnIterator();
 			indexColumnNames = new String[indexSpan];
 			indexFormulaTemplates = new String[indexSpan];
 			indexFormulas = new String[indexSpan];
 			indexColumnIsSettable = new boolean[indexSpan];
 			indexColumnAliases = new String[indexSpan];
 			int i = 0;
 			boolean hasFormula = false;
 			while ( iter.hasNext() ) {
 				Selectable s = (Selectable) iter.next();
 				indexColumnAliases[i] = s.getAlias(dialect);
 				if ( s.isFormula() ) {
 					Formula indexForm = (Formula) s;
 					indexFormulaTemplates[i] = indexForm.getTemplate(dialect, factory.getSqlFunctionRegistry());
 					indexFormulas[i] = indexForm.getFormula();
 					hasFormula = true;
 				}
 				else {
 					Column indexCol = (Column) s;
 					indexColumnNames[i] = indexCol.getQuotedName(dialect);
 					indexColumnIsSettable[i] = true;
 				}
 				i++;
 			}
 			indexContainsFormula = hasFormula;
 			baseIndex = indexedCollection.isList() ?
 					( (List) indexedCollection ).getBaseIndex() : 0;
 
 			indexNodeName = indexedCollection.getIndexNodeName();
 
 		}
 		else {
 			indexContainsFormula = false;
 			indexColumnIsSettable = null;
 			indexFormulaTemplates = null;
 			indexFormulas = null;
 			indexType = null;
 			indexColumnNames = null;
 			indexColumnAliases = null;
 			baseIndex = 0;
 			indexNodeName = null;
 		}
 
 		hasIdentifier = collection.isIdentified();
 		if (hasIdentifier) {
 			if ( collection.isOneToMany() ) {
 				throw new MappingException( "one-to-many collections with identifiers are not supported" );
 			}
 			IdentifierCollection idColl = (IdentifierCollection) collection;
 			identifierType = idColl.getIdentifier().getType();
 			iter = idColl.getIdentifier().getColumnIterator();
 			Column col = ( Column ) iter.next();
 			identifierColumnName = col.getQuotedName(dialect);
 			identifierColumnAlias = col.getAlias(dialect);
 			//unquotedIdentifierColumnName = identifierColumnAlias;
 			identifierGenerator = idColl.getIdentifier().createIdentifierGenerator(
 					cfg.getIdentifierGeneratorFactory(),
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName(),
 					null
 			);
 		}
 		else {
 			identifierType = null;
 			identifierColumnName = null;
 			identifierColumnAlias = null;
 			//unquotedIdentifierColumnName = null;
 			identifierGenerator = null;
 		}
 
 		//GENERATE THE SQL:
 
 		//sqlSelectString = sqlSelectString();
 		//sqlSelectRowString = sqlSelectRowString();
 
 		if ( collection.getCustomSQLInsert() == null ) {
 			sqlInsertRowString = generateInsertRowString();
 			insertCallable = false;
 			insertCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlInsertRowString = collection.getCustomSQLInsert();
 			insertCallable = collection.isCustomInsertCallable();
 			insertCheckStyle = collection.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collection.getCustomSQLInsert(), insertCallable )
 		            : collection.getCustomSQLInsertCheckStyle();
 		}
 
 		if ( collection.getCustomSQLUpdate() == null ) {
 			sqlUpdateRowString = generateUpdateRowString();
 			updateCallable = false;
 			updateCheckStyle = ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		else {
 			sqlUpdateRowString = collection.getCustomSQLUpdate();
 			updateCallable = collection.isCustomUpdateCallable();
 			updateCheckStyle = collection.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( collection.getCustomSQLUpdate(), insertCallable )
 		            : collection.getCustomSQLUpdateCheckStyle();
 		}
 
 		if ( collection.getCustomSQLDelete() == null ) {
 			sqlDeleteRowString = generateDeleteRowString();
 			deleteCallable = false;
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteRowString = collection.getCustomSQLDelete();
 			deleteCallable = collection.isCustomDeleteCallable();
 			deleteCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		if ( collection.getCustomSQLDeleteAll() == null ) {
 			sqlDeleteString = generateDeleteString();
 			deleteAllCallable = false;
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 		else {
 			sqlDeleteString = collection.getCustomSQLDeleteAll();
 			deleteAllCallable = collection.isCustomDeleteAllCallable();
 			deleteAllCheckStyle = ExecuteUpdateResultCheckStyle.NONE;
 		}
 
 		sqlSelectSizeString = generateSelectSizeString(  collection.isIndexed() && !collection.isMap() );
 		sqlDetectRowByIndexString = generateDetectRowByIndexString();
 		sqlDetectRowByElementString = generateDetectRowByElementString();
 		sqlSelectRowByIndexString = generateSelectRowByIndexString();
 
 		logStaticSQL();
 
 		isLazy = collection.isLazy();
 		isExtraLazy = collection.isExtraLazy();
 
 		isInverse = collection.isInverse();
 
 		if ( collection.isArray() ) {
 			elementClass = ( (org.hibernate.mapping.Array) collection ).getElementClass();
 		}
 		else {
 			// for non-arrays, we don't need to know the element class
 			elementClass = null; //elementType.returnedClass();
 		}
 
 		if ( elementType.isComponentType() ) {
 			elementPropertyMapping = new CompositeElementPropertyMapping(
 					elementColumnNames,
 					elementColumnReaders,
 					elementColumnReaderTemplates,
 					elementFormulaTemplates,
 					(CompositeType) elementType,
 					factory
 				);
 		}
 		else if ( !elementType.isEntityType() ) {
 			elementPropertyMapping = new ElementPropertyMapping(
 					elementColumnNames,
 					elementType
 				);
 		}
 		else {
 			if ( elementPersister instanceof PropertyMapping ) { //not all classpersisters implement PropertyMapping!
 				elementPropertyMapping = (PropertyMapping) elementPersister;
 			}
 			else {
 				elementPropertyMapping = new ElementPropertyMapping(
 						elementColumnNames,
 						elementType
 					);
 			}
 		}
 
 		hasOrder = collection.getOrderBy() != null;
 		if ( hasOrder ) {
 			ColumnMapper mapper = new ColumnMapper() {
 				public String[] map(String reference) {
 					return elementPropertyMapping.toColumns( reference );
 				}
 			};
 			sqlOrderByStringTemplate = Template.renderOrderByStringTemplate(
 					collection.getOrderBy(),
 					mapper,
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			sqlOrderByStringTemplate = null;
 		}
 
 		// Handle any filters applied to this collection
 		filterHelper = new FilterHelper( collection.getFilterMap(), dialect, factory.getSqlFunctionRegistry() );
 
 		// Handle any filters applied to this collection for many-to-many
 		manyToManyFilterHelper = new FilterHelper( collection.getManyToManyFilterMap(), dialect, factory.getSqlFunctionRegistry() );
 		manyToManyWhereString = StringHelper.isNotEmpty( collection.getManyToManyWhere() ) ?
 				"( " + collection.getManyToManyWhere() + ")" :
 				null;
 		manyToManyWhereTemplate = manyToManyWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( manyToManyWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		hasManyToManyOrder = collection.getManyToManyOrdering() != null;
 		if ( hasManyToManyOrder ) {
 			ColumnMapper mapper = new ColumnMapper() {
 				public String[] map(String reference) {
 					return elementPropertyMapping.toColumns( reference );
 				}
 			};
 			manyToManyOrderByTemplate = Template.renderOrderByStringTemplate(
 					collection.getManyToManyOrdering(),
 					mapper,
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			manyToManyOrderByTemplate = null;
 		}
 
 		initCollectionPropertyMap();
 	}
 
 	public void postInstantiate() throws MappingException {
 		initializer = queryLoaderName == null ?
 				createCollectionInitializer( LoadQueryInfluencers.NONE ) :
 				new NamedQueryCollectionInitializer( queryLoaderName, this );
 	}
 
 	protected void logStaticSQL() {
         if (LOG.isDebugEnabled()) {
             LOG.debugf("Static SQL for collection: %s", getRole());
             if (getSQLInsertRowString() != null) LOG.debugf(" Row insert: %s", getSQLInsertRowString());
             if (getSQLUpdateRowString() != null) LOG.debugf(" Row update: %s", getSQLUpdateRowString());
             if (getSQLDeleteRowString() != null) LOG.debugf(" Row delete: %s", getSQLDeleteRowString());
             if (getSQLDeleteString() != null) LOG.debugf(" One-shot delete: %s", getSQLDeleteString());
 		}
 	}
 
 	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 		getAppropriateInitializer( key, session ).initialize( key, session );
 	}
 
 	protected CollectionInitializer getAppropriateInitializer(Serializable key, SessionImplementor session) {
 		if ( queryLoaderName != null ) {
 			//if there is a user-specified loader, return that
 			//TODO: filters!?
 			return initializer;
 		}
 		CollectionInitializer subselectInitializer = getSubselectInitializer( key, session );
 		if ( subselectInitializer != null ) {
 			return subselectInitializer;
 		}
 		else if ( session.getEnabledFilters().isEmpty() ) {
 			return initializer;
 		}
 		else {
 			return createCollectionInitializer( session.getLoadQueryInfluencers() );
 		}
 	}
 
 	private CollectionInitializer getSubselectInitializer(Serializable key, SessionImplementor session) {
 
 		if ( !isSubselectLoadable() ) {
 			return null;
 		}
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 
 		SubselectFetch subselect = persistenceContext.getBatchFetchQueue()
 				.getSubselect( session.generateEntityKey( key, getOwnerEntityPersister() ) );
 
 		if (subselect == null) {
 			return null;
 		}
 		else {
 
 			// Take care of any entities that might have
 			// been evicted!
 			Iterator iter = subselect.getResult().iterator();
 			while ( iter.hasNext() ) {
 				if ( !persistenceContext.containsEntity( (EntityKey) iter.next() ) ) {
 					iter.remove();
 				}
 			}
 
 			// Run a subquery loader
 			return createSubselectInitializer( subselect, session );
 		}
 	}
 
 	protected abstract CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session);
 
 	protected abstract CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
 			throws MappingException;
 
 	public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public CollectionType getCollectionType() {
 		return collectionType;
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	public String getSQLOrderByString(String alias) {
 		return hasOrdering()
 				? StringHelper.replace( sqlOrderByStringTemplate, Template.TEMPLATE, alias )
 				: "";
 	}
 
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
 				? StringHelper.replace( manyToManyOrderByTemplate, Template.TEMPLATE, alias )
 				: "";
 	}
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public boolean hasOrdering() {
 		return hasOrder;
 	}
 
 	public boolean hasManyToManyOrdering() {
 		return isManyToMany() && hasManyToManyOrder;
 	}
 
 	public boolean hasWhere() {
 		return hasWhere;
 	}
 
 	protected String getSQLDeleteString() {
 		return sqlDeleteString;
 	}
 
 	protected String getSQLInsertRowString() {
 		return sqlInsertRowString;
 	}
 
 	protected String getSQLUpdateRowString() {
 		return sqlUpdateRowString;
 	}
 
 	protected String getSQLDeleteRowString() {
 		return sqlDeleteRowString;
 	}
 
 	public Type getKeyType() {
 		return keyType;
 	}
 
 	public Type getIndexType() {
 		return indexType;
 	}
 
 	public Type getElementType() {
 		return elementType;
 	}
 
 	/**
 	 * Return the element class of an array, or null otherwise
 	 */
 	public Class getElementClass() { //needed by arrays
 		return elementClass;
 	}
 
 	public Object readElement(ResultSet rs, Object owner, String[] aliases, SessionImplementor session)
 	throws HibernateException, SQLException {
 		return getElementType().nullSafeGet( rs, aliases, session, owner );
 	}
 
 	public Object readIndex(ResultSet rs, String[] aliases, SessionImplementor session)
 	throws HibernateException, SQLException {
 		Object index = getIndexType().nullSafeGet( rs, aliases, session, null );
 		if ( index == null ) {
 			throw new HibernateException( "null index column for collection: " + role );
 		}
 		index = decrementIndexByBase( index );
 		return index;
 	}
 
 	protected Object decrementIndexByBase(Object index) {
 		if (baseIndex!=0) {
 			index = new Integer( ( (Integer) index ).intValue() - baseIndex );
 		}
 		return index;
 	}
 
 	public Object readIdentifier(ResultSet rs, String alias, SessionImplementor session)
 	throws HibernateException, SQLException {
 		Object id = getIdentifierType().nullSafeGet( rs, alias, session, null );
 		if ( id == null ) {
 			throw new HibernateException( "null identifier column for collection: " + role );
 		}
 		return id;
 	}
 
 	public Object readKey(ResultSet rs, String[] aliases, SessionImplementor session)
 	throws HibernateException, SQLException {
 		return getKeyType().nullSafeGet( rs, aliases, session, null );
 	}
 
 	/**
 	 * Write the key to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeKey(PreparedStatement st, Serializable key, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		if ( key == null ) {
 			throw new NullPointerException( "null key for collection: " + role );  //an assertion
 		}
 		getKeyType().nullSafeSet( st, key, i, session );
 		return i + keyColumnAliases.length;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElement(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getElementType().nullSafeSet(st, elt, i, elementColumnIsSettable, session);
 		return i + ArrayHelper.countTrue( elementColumnIsSettable );
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndex(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getIndexType().nullSafeSet( st, incrementIndexByBase(index), i, indexColumnIsSettable, session );
 		return i + ArrayHelper.countTrue(indexColumnIsSettable);
 	}
 
 	protected Object incrementIndexByBase(Object index) {
 		if (baseIndex!=0) {
 			index = new Integer( ( (Integer) index ).intValue() + baseIndex );
 		}
 		return index;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElementToWhere(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if (elementIsPureFormula) {
 			throw new AssertionFailure("cannot use a formula-based element in the where condition");
 		}
 		getElementType().nullSafeSet(st, elt, i, elementColumnIsInPrimaryKey, session);
 		return i + elementColumnAliases.length;
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndexToWhere(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if (indexContainsFormula) {
 			throw new AssertionFailure("cannot use a formula-based index in the where condition");
 		}
 		getIndexType().nullSafeSet( st, incrementIndexByBase(index), i, session );
 		return i + indexColumnAliases.length;
 	}
 
 	/**
 	 * Write the identifier to a JDBC <tt>PreparedStatement</tt>
 	 */
 	public int writeIdentifier(PreparedStatement st, Object id, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		getIdentifierType().nullSafeSet( st, id, i, session );
 		return i + 1;
 	}
 
 	public boolean isPrimitiveArray() {
 		return isPrimitiveArray;
 	}
 
 	public boolean isArray() {
 		return isArray;
 	}
 
 	public String[] getKeyColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( keyColumnAliases );
 	}
 
 	public String[] getElementColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( elementColumnAliases );
 	}
 
 	public String[] getIndexColumnAliases(String suffix) {
 		if ( hasIndex ) {
 			return new Alias( suffix ).toAliasStrings( indexColumnAliases );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String getIdentifierColumnAlias(String suffix) {
 		if ( hasIdentifier ) {
 			return new Alias( suffix ).toAliasString( identifierColumnAlias );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String getIdentifierColumnName() {
 		if ( hasIdentifier ) {
 			return identifierColumnName;
 		} else {
 			return null;
 		}
 	}
 
 	/**
 	 * Generate a list of collection index, key and element columns
 	 */
 	public String selectFragment(String alias, String columnSuffix) {
 		SelectFragment frag = generateSelectFragment( alias, columnSuffix );
 		appendElementColumns( frag, alias );
 		appendIndexColumns( frag, alias );
 		appendIdentifierColumns( frag, alias );
 
 		return frag.toFragmentString()
 				.substring( 2 ); //strip leading ','
 	}
 
 	protected String generateSelectSizeString(boolean isIntegerIndexed) {
 		String selectValue = isIntegerIndexed ?
 			"max(" + getIndexColumnNames()[0] + ") + 1": //lists, arrays
 			"count(" + getElementColumnNames()[0] + ")"; //sets, maps, bags
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addColumn(selectValue)
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumn("1")
 				.toStatementString();
 	}
 
 	protected String generateSelectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumns( getElementColumnNames(), elementColumnAliases )
 				.addColumns( indexFormulas, indexColumnAliases )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByElementString() {
 		return new SimpleSelect(dialect)
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getElementColumnNames(), "=?" )
 				.addCondition( elementFormulas, "=?" )
 				.addColumn("1")
 				.toStatementString();
 	}
 
 	protected SelectFragment generateSelectFragment(String alias, String columnSuffix) {
 		return new SelectFragment()
 				.setSuffix( columnSuffix )
 				.addColumns( alias, keyColumnNames, keyColumnAliases );
 	}
 
 	protected void appendElementColumns(SelectFragment frag, String elemAlias) {
 		for ( int i=0; i<elementColumnIsSettable.length; i++ ) {
 			if ( elementColumnIsSettable[i] ) {
 				frag.addColumnTemplate( elemAlias, elementColumnReaderTemplates[i], elementColumnAliases[i] );
 			}
 			else {
 				frag.addFormula( elemAlias, elementFormulaTemplates[i], elementColumnAliases[i] );
 			}
 		}
 	}
 
 	protected void appendIndexColumns(SelectFragment frag, String alias) {
 		if ( hasIndex ) {
 			for ( int i=0; i<indexColumnIsSettable.length; i++ ) {
 				if ( indexColumnIsSettable[i] ) {
 					frag.addColumn( alias, indexColumnNames[i], indexColumnAliases[i] );
 				}
 				else {
 					frag.addFormula( alias, indexFormulaTemplates[i], indexColumnAliases[i] );
 				}
 			}
 		}
 	}
 
 	protected void appendIdentifierColumns(SelectFragment frag, String alias) {
 		if ( hasIdentifier ) {
 			frag.addColumn( alias, identifierColumnName, identifierColumnAlias );
 		}
 	}
 
 	public String[] getIndexColumnNames() {
 		return indexColumnNames;
 	}
 
 	public String[] getIndexFormulas() {
 		return indexFormulas;
 	}
 
 	public String[] getIndexColumnNames(String alias) {
 		return qualify(alias, indexColumnNames, indexFormulaTemplates);
 
 	}
 
 	public String[] getElementColumnNames(String alias) {
 		return qualify(alias, elementColumnNames, elementFormulaTemplates);
 	}
 
 	private static String[] qualify(String alias, String[] columnNames, String[] formulaTemplates) {
 		int span = columnNames.length;
 		String[] result = new String[span];
 		for (int i=0; i<span; i++) {
 			if ( columnNames[i]==null ) {
 				result[i] = StringHelper.replace( formulaTemplates[i], Template.TEMPLATE, alias );
 			}
 			else {
 				result[i] = StringHelper.qualify( alias, columnNames[i] );
 			}
 		}
 		return result;
 	}
 
 	public String[] getElementColumnNames() {
 		return elementColumnNames; //TODO: something with formulas...
 	}
 
 	public String[] getKeyColumnNames() {
 		return keyColumnNames;
 	}
 
 	public boolean hasIndex() {
 		return hasIndex;
 	}
 
 	public boolean isLazy() {
 		return isLazy;
 	}
 
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
index 13ba09db05..4ebcd62b11 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
@@ -1,356 +1,356 @@
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
 package org.hibernate.persister.collection;
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SubselectFetch;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.BatchingCollectionInitializer;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.loader.collection.SubselectCollectionLoader;
 import org.hibernate.mapping.Collection;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.Update;
 import org.hibernate.type.AssociationType;
 
 /**
  * Collection persister for collections of values and many-to-many associations.
  *
  * @author Gavin King
  */
 public class BasicCollectionPersister extends AbstractCollectionPersister {
 
 	public boolean isCascadeDeleteEnabled() {
 		return false;
 	}
 
 	public BasicCollectionPersister(
 			Collection collection,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			Configuration cfg,
 			SessionFactoryImplementor factory) throws MappingException, CacheException {
 		super( collection, cacheAccessStrategy, cfg, factory );
 	}
 
 	/**
 	 * Generate the SQL DELETE that deletes all rows
 	 */
 	@Override
     protected String generateDeleteString() {
 		
 		Delete delete = new Delete()
 				.setTableName( qualifiedTableName )
 				.addPrimaryKeyColumns( keyColumnNames );
 		
 		if ( hasWhere ) delete.setWhere( sqlWhereString );
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( "delete collection " + getRole() );
 		}
 		
 		return delete.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL INSERT that creates a new row
 	 */
 	@Override
     protected String generateInsertRowString() {
 		
 		Insert insert = new Insert( getDialect() )
 				.setTableName( qualifiedTableName )
 				.addColumns( keyColumnNames );
 		
 		if ( hasIdentifier) insert.addColumn( identifierColumnName );
 		
 		if ( hasIndex /*&& !indexIsFormula*/ ) {
 			insert.addColumns( indexColumnNames, indexColumnIsSettable );
 		}
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert collection row " + getRole() );
 		}
 		
 		//if ( !elementIsFormula ) {
 			insert.addColumns( elementColumnNames, elementColumnIsSettable, elementColumnWriters );
 		//}
 		
 		return insert.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL UPDATE that updates a row
 	 */
 	@Override
     protected String generateUpdateRowString() {
 		
 		Update update = new Update( getDialect() )
 			.setTableName( qualifiedTableName );
 		
 		//if ( !elementIsFormula ) {
 			update.addColumns( elementColumnNames, elementColumnIsSettable, elementColumnWriters );
 		//}
 		
 		if ( hasIdentifier ) {
 			update.addPrimaryKeyColumns( new String[]{ identifierColumnName } );
 		}
 		else if ( hasIndex && !indexContainsFormula ) {
 			update.addPrimaryKeyColumns( ArrayHelper.join( keyColumnNames, indexColumnNames ) );
 		}
 		else {
 			update.addPrimaryKeyColumns( keyColumnNames );
 			update.addPrimaryKeyColumns( elementColumnNames, elementColumnIsInPrimaryKey, elementColumnWriters );
 		}
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "update collection row " + getRole() );
 		}
 		
 		return update.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL DELETE that deletes a particular row
 	 */
 	@Override
     protected String generateDeleteRowString() {
 		
 		Delete delete = new Delete()
 			.setTableName( qualifiedTableName );
 		
 		if ( hasIdentifier ) {
 			delete.addPrimaryKeyColumns( new String[]{ identifierColumnName } );
 		}
 		else if ( hasIndex && !indexContainsFormula ) {
 			delete.addPrimaryKeyColumns( ArrayHelper.join( keyColumnNames, indexColumnNames ) );
 		}
 		else {
 			delete.addPrimaryKeyColumns( keyColumnNames );
 			delete.addPrimaryKeyColumns( elementColumnNames, elementColumnIsInPrimaryKey, elementColumnWriters );
 		}
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( "delete collection row " + getRole() );
 		}
 		
 		return delete.toStatementString();
 	}
 
 	public boolean consumesEntityAlias() {
 		return false;
 	}
 
 	public boolean consumesCollectionAlias() {
 //		return !isOneToMany();
 		return true;
 	}
 
 	public boolean isOneToMany() {
 		return false;
 	}
 
 	@Override
     public boolean isManyToMany() {
 		return elementType.isEntityType(); //instanceof AssociationType;
 	}
 
 	private BasicBatchKey updateBatchKey;
 
 	@Override
     protected int doUpdateRows(Serializable id, PersistentCollection collection, SessionImplementor session)
 			throws HibernateException {
 		
 		if ( ArrayHelper.isAllFalse(elementColumnIsSettable) ) return 0;
 
 		try {
 			PreparedStatement st = null;
 			Expectation expectation = Expectations.appropriateExpectation( getUpdateCheckStyle() );
 			boolean callable = isUpdateCallable();
 			boolean useBatch = expectation.canBeBatched();
 			Iterator entries = collection.entries( this );
 			String sql = getSQLUpdateRowString();
 			int i = 0;
 			int count = 0;
 			while ( entries.hasNext() ) {
 				Object entry = entries.next();
 				if ( collection.needsUpdating( entry, i, elementType ) ) {
 					int offset = 1;
 
 					if ( useBatch ) {
 						if ( updateBatchKey == null ) {
 							updateBatchKey = new BasicBatchKey(
 									getRole() + "#UPDATE",
 									expectation
 							);
 						}
 						st = session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getBatch( updateBatchKey )
 								.getBatchStatement( sql, callable );
 					}
 					else {
 						st = session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( sql, callable );
 					}
 
 					try {
 						offset+= expectation.prepare( st );
 						int loc = writeElement( st, collection.getElement( entry ), offset, session );
 						if ( hasIdentifier ) {
 							writeIdentifier( st, collection.getIdentifier( entry, i ), loc, session );
 						}
 						else {
 							loc = writeKey( st, id, loc, session );
 							if ( hasIndex && !indexContainsFormula ) {
 								writeIndexToWhere( st, collection.getIndex( entry, i, this ), loc, session );
 							}
 							else {
 								writeElementToWhere( st, collection.getSnapshotElement( entry, i ), loc, session );
 							}
 						}
 
 						if ( useBatch ) {
 							session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getBatch( updateBatchKey )
 									.addToBatch();
 						}
 						else {
 							expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 						}
 					}
 					catch ( SQLException sqle ) {
 						if ( useBatch ) {
 							session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 						}
 						throw sqle;
 					}
 					finally {
 						if ( !useBatch ) {
 							st.close();
 						}
 					}
 					count++;
 				}
 				i++;
 			}
 			return count;
 		}
 		catch ( SQLException sqle ) {
 			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not update collection rows: " + MessageHelper.collectionInfoString( this, id, getFactory() ),
 					getSQLUpdateRowString()
 				);
 		}
 	}
 
 	public String selectFragment(
 	        Joinable rhs,
 	        String rhsAlias,
 	        String lhsAlias,
 	        String entitySuffix,
 	        String collectionSuffix,
 	        boolean includeCollectionColumns) {
 		// we need to determine the best way to know that two joinables
 		// represent a single many-to-many...
 		if ( rhs != null && isManyToMany() && !rhs.isCollection() ) {
 			AssociationType elementType = ( ( AssociationType ) getElementType() );
 			if ( rhs.equals( elementType.getAssociatedJoinable( getFactory() ) ) ) {
 				return manyToManySelectFragment( rhs, rhsAlias, lhsAlias, collectionSuffix );
 			}
 		}
 		return includeCollectionColumns ? selectFragment( lhsAlias, collectionSuffix ) : "";
 	}
 
 	private String manyToManySelectFragment(
 	        Joinable rhs,
 	        String rhsAlias,
 	        String lhsAlias,
 	        String collectionSuffix) {
 		SelectFragment frag = generateSelectFragment( lhsAlias, collectionSuffix );
 
 		String[] elementColumnNames = rhs.getKeyColumnNames();
 		frag.addColumns( rhsAlias, elementColumnNames, elementColumnAliases );
 		appendIndexColumns( frag, lhsAlias );
 		appendIdentifierColumns( frag, lhsAlias );
 
 		return frag.toFragmentString()
 				.substring( 2 ); //strip leading ','
 	}
 
 	/**
 	 * Create the <tt>CollectionLoader</tt>
 	 *
 	 * @see org.hibernate.loader.collection.BasicCollectionLoader
 	 */
 	@Override
     protected CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers)
 			throws MappingException {
 		return BatchingCollectionInitializer.createBatchingCollectionInitializer( this, batchSize, getFactory(), loadQueryInfluencers );
 	}
 
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return "";
 	}
 
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return "";
 	}
 
 	@Override
     protected CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session) {
 		return new SubselectCollectionLoader( 
 				this,
 				subselect.toSubselectString( getCollectionType().getLHSPropertyName() ),
 				subselect.getResult(),
 				subselect.getQueryParameters(),
 				subselect.getNamedParameterLocMap(),
 				session.getFactory(),
 				session.getLoadQueryInfluencers() 
 		);
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
index 0dc1be8611..b8ceeb2c15 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
@@ -1,309 +1,309 @@
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
 package org.hibernate.persister.collection;
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 /**
  * A strategy for persisting a collection role. Defines a contract between
  * the persistence strategy and the actual persistent collection framework
  * and session. Does not define operations that are required for querying
  * collections, or loading by outer join.<br>
  * <br>
  * Implements persistence of a collection instance while the instance is
  * referenced in a particular role.<br>
  * <br>
  * This class is highly coupled to the <tt>PersistentCollection</tt>
  * hierarchy, since double dispatch is used to load and update collection
  * elements.<br>
  * <br>
  * May be considered an immutable view of the mapping object
  *
  * @see QueryableCollection
- * @see PersistentCollection
+ * @see org.hibernate.collection.spi.PersistentCollection
  * @author Gavin King
  */
 public interface CollectionPersister {
 	/**
 	 * Initialize the given collection with the given key
 	 */
 	public void initialize(Serializable key, SessionImplementor session) //TODO: add owner argument!!
 	throws HibernateException;
 	/**
 	 * Is this collection role cacheable
 	 */
 	public boolean hasCache();
 	/**
 	 * Get the cache
 	 */
 	public CollectionRegionAccessStrategy getCacheAccessStrategy();
 	/**
 	 * Get the cache structure
 	 */
 	public CacheEntryStructure getCacheEntryStructure();
 	/**
 	 * Get the associated <tt>Type</tt>
 	 */
 	public CollectionType getCollectionType();
 	/**
 	 * Get the "key" type (the type of the foreign key)
 	 */
 	public Type getKeyType();
 	/**
 	 * Get the "index" type for a list or map (optional operation)
 	 */
 	public Type getIndexType();
 	/**
 	 * Get the "element" type
 	 */
 	public Type getElementType();
 	/**
 	 * Return the element class of an array, or null otherwise
 	 */
 	public Class getElementClass();
 	/**
 	 * Read the key from a row of the JDBC <tt>ResultSet</tt>
 	 */
 	public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
 		throws HibernateException, SQLException;
 	/**
 	 * Read the element from a row of the JDBC <tt>ResultSet</tt>
 	 */
 	public Object readElement(
 		ResultSet rs,
 		Object owner,
 		String[] columnAliases,
 		SessionImplementor session)
 		throws HibernateException, SQLException;
 	/**
 	 * Read the index from a row of the JDBC <tt>ResultSet</tt>
 	 */
 	public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
 		throws HibernateException, SQLException;
 	/**
 	 * Read the identifier from a row of the JDBC <tt>ResultSet</tt>
 	 */
 	public Object readIdentifier(
 		ResultSet rs,
 		String columnAlias,
 		SessionImplementor session)
 		throws HibernateException, SQLException;
 	/**
 	 * Is this an array or primitive values?
 	 */
 	public boolean isPrimitiveArray();
 	/**
 	 * Is this an array?
 	 */
 	public boolean isArray();
 	/**
 	 * Is this a one-to-many association?
 	 */
 	public boolean isOneToMany();
 	/**
 	 * Is this a many-to-many association?  Note that this is mainly
 	 * a convenience feature as the single persister does not
 	 * conatin all the information needed to handle a many-to-many
 	 * itself, as internally it is looked at as two many-to-ones.
 	 */
 	public boolean isManyToMany();
 
 	public String getManyToManyFilterFragment(String alias, Map enabledFilters);
 
 	/**
 	 * Is this an "indexed" collection? (list or map)
 	 */
 	public boolean hasIndex();
 	/**
 	 * Is this collection lazyily initialized?
 	 */
 	public boolean isLazy();
 	/**
 	 * Is this collection "inverse", so state changes are not
 	 * propogated to the database.
 	 */
 	public boolean isInverse();
 	/**
 	 * Completely remove the persistent state of the collection
 	 */
 	public void remove(Serializable id, SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * (Re)create the collection's persistent state
 	 */
 	public void recreate(
 		PersistentCollection collection,
 		Serializable key,
 		SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * Delete the persistent state of any elements that were removed from
 	 * the collection
 	 */
 	public void deleteRows(
 		PersistentCollection collection,
 		Serializable key,
 		SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * Update the persistent state of any elements that were modified
 	 */
 	public void updateRows(
 		PersistentCollection collection,
 		Serializable key,
 		SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * Insert the persistent state of any new collection elements
 	 */
 	public void insertRows(
 		PersistentCollection collection,
 		Serializable key,
 		SessionImplementor session)
 		throws HibernateException;
 	/**
 	 * Get the name of this collection role (the fully qualified class name,
 	 * extended by a "property path")
 	 */
 	public String getRole();
 	/**
 	 * Get the persister of the entity that "owns" this collection
 	 */
 	public EntityPersister getOwnerEntityPersister();
 	/**
 	 * Get the surrogate key generation strategy (optional operation)
 	 */
 	public IdentifierGenerator getIdentifierGenerator();
 	/**
 	 * Get the type of the surrogate key
 	 */
 	public Type getIdentifierType();
 	/**
 	 * Does this collection implement "orphan delete"?
 	 */
 	public boolean hasOrphanDelete();
 	/**
 	 * Is this an ordered collection? (An ordered collection is
 	 * ordered by the initialization operation, not by sorting
 	 * that happens in memory, as in the case of a sorted collection.)
 	 */
 	public boolean hasOrdering();
 
 	public boolean hasManyToManyOrdering();
 
 	/**
 	 * Get the "space" that holds the persistent state
 	 */
 	public Serializable[] getCollectionSpaces();
 
 	public CollectionMetadata getCollectionMetadata();
 
 	/**
 	 * Is cascade delete handled by the database-level
 	 * foreign key constraint definition?
 	 */
 	public abstract boolean isCascadeDeleteEnabled();
 	
 	/**
 	 * Does this collection cause version increment of the 
 	 * owning entity?
 	 */
 	public boolean isVersioned();
 	
 	/**
 	 * Can the elements of this collection change?
 	 */
 	public boolean isMutable();
 	
 	//public boolean isSubselectLoadable();
 	
 	public String getNodeName();
 	
 	public String getElementNodeName();
 	
 	public String getIndexNodeName();
 
 	public void postInstantiate() throws MappingException;
 	
 	public SessionFactoryImplementor getFactory();
 
 	public boolean isAffectedByEnabledFilters(SessionImplementor session);
 
 	/**
 	 * Generates the collection's key column aliases, based on the given
 	 * suffix.
 	 *
 	 * @param suffix The suffix to use in the key column alias generation.
 	 * @return The key column aliases.
 	 */
 	public String[] getKeyColumnAliases(String suffix);
 
 	/**
 	 * Generates the collection's index column aliases, based on the given
 	 * suffix.
 	 *
 	 * @param suffix The suffix to use in the index column alias generation.
 	 * @return The key column aliases, or null if not indexed.
 	 */
 	public String[] getIndexColumnAliases(String suffix);
 
 	/**
 	 * Generates the collection's element column aliases, based on the given
 	 * suffix.
 	 *
 	 * @param suffix The suffix to use in the element column alias generation.
 	 * @return The key column aliases.
 	 */
 	public String[] getElementColumnAliases(String suffix);
 
 	/**
 	 * Generates the collection's identifier column aliases, based on the given
 	 * suffix.
 	 *
 	 * @param suffix The suffix to use in the key column alias generation.
 	 * @return The key column aliases.
 	 */
 	public String getIdentifierColumnAlias(String suffix);
 	
 	public boolean isExtraLazy();
 	public int getSize(Serializable key, SessionImplementor session);
 	public boolean indexExists(Serializable key, Object index, SessionImplementor session);
 	public boolean elementExists(Serializable key, Object element, SessionImplementor session);
 	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
index bec0064b41..1ff0769102 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
@@ -1,409 +1,409 @@
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
 package org.hibernate.persister.collection;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
 import org.hibernate.MappingException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.SubselectFetch;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.BatchingCollectionInitializer;
 import org.hibernate.loader.collection.CollectionInitializer;
 import org.hibernate.loader.collection.SubselectOneToManyLoader;
 import org.hibernate.loader.entity.CollectionElementLoader;
 import org.hibernate.mapping.Collection;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.sql.Update;
 
 /**
  * Collection persister for one-to-many associations.
  *
  * @author Gavin King
  */
 public class OneToManyPersister extends AbstractCollectionPersister {
 
 	private final boolean cascadeDeleteEnabled;
 	private final boolean keyIsNullable;
 	private final boolean keyIsUpdateable;
 
 	@Override
     protected boolean isRowDeleteEnabled() {
 		return keyIsUpdateable && keyIsNullable;
 	}
 
 	@Override
     protected boolean isRowInsertEnabled() {
 		return keyIsUpdateable;
 	}
 
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public OneToManyPersister(
 			Collection collection,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			Configuration cfg,
 			SessionFactoryImplementor factory) throws MappingException, CacheException {
 		super( collection, cacheAccessStrategy, cfg, factory );
 		cascadeDeleteEnabled = collection.getKey().isCascadeDeleteEnabled() &&
 				factory.getDialect().supportsCascadeDelete();
 		keyIsNullable = collection.getKey().isNullable();
 		keyIsUpdateable = collection.getKey().isUpdateable();
 	}
 
 	/**
 	 * Generate the SQL UPDATE that updates all the foreign keys to null
 	 */
 	@Override
     protected String generateDeleteString() {
 		
 		Update update = new Update( getDialect() )
 				.setTableName( qualifiedTableName )
 				.addColumns( keyColumnNames, "null" )
 				.addPrimaryKeyColumns( keyColumnNames );
 		
 		if ( hasIndex && !indexContainsFormula ) update.addColumns( indexColumnNames, "null" );
 		
 		if ( hasWhere ) update.setWhere( sqlWhereString );
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "delete one-to-many " + getRole() );
 		}
 		
 		return update.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL UPDATE that updates a foreign key to a value
 	 */
 	@Override
     protected String generateInsertRowString() {
 		
 		Update update = new Update( getDialect() )
 				.setTableName( qualifiedTableName )
 				.addColumns( keyColumnNames );
 		
 		if ( hasIndex && !indexContainsFormula ) update.addColumns( indexColumnNames );
 		
 		//identifier collections not supported for 1-to-many
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "create one-to-many row " + getRole() );
 		}
 		
 		return update.addPrimaryKeyColumns( elementColumnNames, elementColumnWriters )
 				.toStatementString();
 	}
 
 	/**
 	 * Not needed for one-to-many association
 	 */
 	@Override
     protected String generateUpdateRowString() {
 		return null;
 	}
 
 	/**
 	 * Generate the SQL UPDATE that updates a particular row's foreign
 	 * key to null
 	 */
 	@Override
     protected String generateDeleteRowString() {
 		
 		Update update = new Update( getDialect() )
 				.setTableName( qualifiedTableName )
 				.addColumns( keyColumnNames, "null" );
 		
 		if ( hasIndex && !indexContainsFormula ) update.addColumns( indexColumnNames, "null" );
 		
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "delete one-to-many row " + getRole() );
 		}
 		
 		//use a combination of foreign key columns and pk columns, since
 		//the ordering of removal and addition is not guaranteed when
 		//a child moves from one parent to another
 		String[] rowSelectColumnNames = ArrayHelper.join( keyColumnNames, elementColumnNames );
 		return update.addPrimaryKeyColumns( rowSelectColumnNames )
 				.toStatementString();
 	}
 
 	public boolean consumesEntityAlias() {
 		return true;
 	}
 	public boolean consumesCollectionAlias() {
 		return true;
 	}
 
 	public boolean isOneToMany() {
 		return true;
 	}
 
 	@Override
     public boolean isManyToMany() {
 		return false;
 	}
 
 	private BasicBatchKey deleteRowBatchKey;
 	private BasicBatchKey insertRowBatchKey;
 
 	@Override
     protected int doUpdateRows(Serializable id, PersistentCollection collection, SessionImplementor session) {
 
 		// we finish all the "removes" first to take care of possible unique
 		// constraints and so that we can take better advantage of batching
 		
 		try {
 			int count = 0;
 			if ( isRowDeleteEnabled() ) {
 				final Expectation deleteExpectation = Expectations.appropriateExpectation( getDeleteCheckStyle() );
 				final boolean useBatch = deleteExpectation.canBeBatched();
 				if ( useBatch && deleteRowBatchKey == null ) {
 					deleteRowBatchKey = new BasicBatchKey(
 							getRole() + "#DELETEROW",
 							deleteExpectation
 					);
 				}
 				final String sql = getSQLDeleteRowString();
 
 				PreparedStatement st = null;
 				// update removed rows fks to null
 				try {
 					int i = 0;
 					Iterator entries = collection.entries( this );
 					int offset = 1;
 					while ( entries.hasNext() ) {
 						Object entry = entries.next();
 						if ( collection.needsUpdating( entry, i, elementType ) ) {  // will still be issued when it used to be null
 							if ( useBatch ) {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( deleteRowBatchKey )
 										.getBatchStatement( sql, isDeleteCallable() );
 							}
 							else {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getStatementPreparer()
 										.prepareStatement( sql, isDeleteCallable() );
 							}
 							int loc = writeKey( st, id, offset, session );
 							writeElementToWhere( st, collection.getSnapshotElement(entry, i), loc, session );
 							if ( useBatch ) {
 								session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( deleteRowBatchKey )
 										.addToBatch();
 							}
 							else {
 								deleteExpectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							count++;
 						}
 						i++;
 					}
 				}
 				catch ( SQLException e ) {
 					if ( useBatch ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 					}
 					throw e;
 				}
 				finally {
 					if ( !useBatch ) {
 						st.close();
 					}
 				}
 			}
 			
 			if ( isRowInsertEnabled() ) {
 				final Expectation insertExpectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 				boolean useBatch = insertExpectation.canBeBatched();
 				boolean callable = isInsertCallable();
 				if ( useBatch && insertRowBatchKey == null ) {
 					insertRowBatchKey = new BasicBatchKey(
 							getRole() + "#INSERTROW",
 							insertExpectation
 					);
 				}
 				final String sql = getSQLInsertRowString();
 
 				PreparedStatement st = null;
 				// now update all changed or added rows fks
 				try {
 					int i = 0;
 					Iterator entries = collection.entries( this );
 					while ( entries.hasNext() ) {
 						Object entry = entries.next();
 						int offset = 1;
 						if ( collection.needsUpdating( entry, i, elementType ) ) {
 							if ( useBatch ) {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( insertRowBatchKey )
 										.getBatchStatement( sql, callable );
 							}
 							else {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getStatementPreparer()
 										.prepareStatement( sql, callable );
 							}
 
 							offset += insertExpectation.prepare( st );
 
 							int loc = writeKey( st, id, offset, session );
 							if ( hasIndex && !indexContainsFormula ) {
 								loc = writeIndexToWhere( st, collection.getIndex( entry, i, this ), loc, session );
 							}
 
 							writeElementToWhere( st, collection.getElement( entry ), loc, session );
 
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().getBatch( insertRowBatchKey ).addToBatch();
 							}
 							else {
 								insertExpectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							count++;
 						}
 						i++;
 					}
 				}
 				catch ( SQLException sqle ) {
 					if ( useBatch ) {
 						session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 					}
 					throw sqle;
 				}
 				finally {
 					if ( !useBatch ) {
 						st.close();
 					}
 				}
 			}
 
 			return count;
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not update collection rows: " + 
 					MessageHelper.collectionInfoString( this, id, getFactory() ),
 					getSQLInsertRowString()
 			);
 		}
 	}
 
 	public String selectFragment(
 	        Joinable rhs,
 	        String rhsAlias,
 	        String lhsAlias,
 	        String entitySuffix,
 	        String collectionSuffix,
 	        boolean includeCollectionColumns) {
 		StringBuffer buf = new StringBuffer();
 		if ( includeCollectionColumns ) {
 //			buf.append( selectFragment( lhsAlias, "" ) )//ignore suffix for collection columns!
 			buf.append( selectFragment( lhsAlias, collectionSuffix ) )
 					.append( ", " );
 		}
 		OuterJoinLoadable ojl = ( OuterJoinLoadable ) getElementPersister();
 		return buf.append( ojl.selectFragment( lhsAlias, entitySuffix ) )//use suffix for the entity columns
 				.toString();
 	}
 
 	/**
 	 * Create the <tt>OneToManyLoader</tt>
 	 *
 	 * @see org.hibernate.loader.collection.OneToManyLoader
 	 */
 	@Override
     protected CollectionInitializer createCollectionInitializer(LoadQueryInfluencers loadQueryInfluencers) 
 			throws MappingException {
 		return BatchingCollectionInitializer.createBatchingOneToManyInitializer( this, batchSize, getFactory(), loadQueryInfluencers );
 	}
 
 	public String fromJoinFragment(String alias,
 								   boolean innerJoin,
 								   boolean includeSubclasses) {
 		return ( ( Joinable ) getElementPersister() ).fromJoinFragment( alias, innerJoin, includeSubclasses );
 	}
 
 	public String whereJoinFragment(String alias,
 									boolean innerJoin,
 									boolean includeSubclasses) {
 		return ( ( Joinable ) getElementPersister() ).whereJoinFragment( alias, innerJoin, includeSubclasses );
 	}
 
 	@Override
     public String getTableName() {
 		return ( ( Joinable ) getElementPersister() ).getTableName();
 	}
 
 	@Override
     public String filterFragment(String alias) throws MappingException {
 		String result = super.filterFragment( alias );
 		if ( getElementPersister() instanceof Joinable ) {
 			result += ( ( Joinable ) getElementPersister() ).oneToManyFilterFragment( alias );
 		}
 		return result;
 
 	}
 
 	@Override
     protected CollectionInitializer createSubselectInitializer(SubselectFetch subselect, SessionImplementor session) {
 		return new SubselectOneToManyLoader( 
 				this,
 				subselect.toSubselectString( getCollectionType().getLHSPropertyName() ),
 				subselect.getResult(),
 				subselect.getQueryParameters(),
 				subselect.getNamedParameterLocMap(),
 				session.getFactory(),
 				session.getLoadQueryInfluencers()
 			);
 	}
 
 	@Override
     public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 		return new CollectionElementLoader( this, getFactory(), session.getLoadQueryInfluencers() )
 				.loadElement( session, key, incrementIndexByBase(index) );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java b/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
index b72b5ef3f7..b9adcfa216 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
@@ -1,141 +1,141 @@
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
 package org.hibernate.type;
 import java.io.Serializable;
 import java.lang.reflect.Array;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
-import org.hibernate.collection.PersistentArrayHolder;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.internal.PersistentArrayHolder;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * A type for persistent arrays.
  * @author Gavin King
  */
 public class ArrayType extends CollectionType {
 
 	private final Class elementClass;
 	private final Class arrayClass;
 
 	public ArrayType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Class elementClass, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 		this.elementClass = elementClass;
 		arrayClass = Array.newInstance(elementClass, 0).getClass();
 	}
 
 	public Class getReturnedClass() {
 		return arrayClass;
 	}
 
-	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) 
+	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key)
 	throws HibernateException {
 		return new PersistentArrayHolder(session, persister);
 	}
 
 	/**
 	 * Not defined for collections of primitive type
 	 */
 	public Iterator getElementsIterator(Object collection) {
 		return Arrays.asList( (Object[]) collection ).iterator();
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object array) {
 		return new PersistentArrayHolder(session, array);
 	}
 
 	public boolean isArrayType() {
 		return true;
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		int length = Array.getLength(value);
 		List list = new ArrayList(length);
 		Type elemType = getElementType(factory);
 		for ( int i=0; i<length; i++ ) {
 			list.add( elemType.toLoggableString( Array.get(value, i), factory ) );
 		}
 		return list.toString();
 	}
 	
 	public Object instantiateResult(Object original) {
 		return Array.newInstance( elementClass, Array.getLength(original) );
 	}
 
 	public Object replaceElements(
 		Object original,
 		Object target,
 		Object owner, 
 		Map copyCache, 
 		SessionImplementor session)
 	throws HibernateException {
 		
 		int length = Array.getLength(original);
 		if ( length!=Array.getLength(target) ) {
 			//note: this affects the return value!
 			target=instantiateResult(original);
 		}
 		
 		Type elemType = getElementType( session.getFactory() );
 		for ( int i=0; i<length; i++ ) {
 			Array.set( target, i, elemType.replace( Array.get(original, i), null, session, owner, copyCache ) );
 		}
 		
 		return target;
 	
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object indexOf(Object array, Object element) {
 		int length = Array.getLength(array);
 		for ( int i=0; i<length; i++ ) {
 			//TODO: proxies!
 			if ( Array.get(array, i)==element ) return new Integer(i);
 		}
 		return null;
 	}
 
 	protected boolean initializeImmediately(EntityMode entityMode) {
 		return true;
 	}
 
 	public boolean hasHolder(EntityMode entityMode) {
 		return true;
 	}
 	
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/BagType.java b/hibernate-core/src/main/java/org/hibernate/type/BagType.java
index db26f4fdd4..0e28ae58f2 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/BagType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BagType.java
@@ -1,70 +1,70 @@
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
 package org.hibernate.type;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collection;
 import org.dom4j.Element;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
-import org.hibernate.collection.PersistentBag;
-import org.hibernate.collection.PersistentCollection;
-import org.hibernate.collection.PersistentElementHolder;
+import org.hibernate.collection.internal.PersistentBag;
+import org.hibernate.collection.internal.PersistentElementHolder;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class BagType extends CollectionType {
 
 	public BagType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key)
 	throws HibernateException {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			return new PersistentElementHolder(session, persister, key);
 		}
 		else {
 			return new PersistentBag(session);
 		}
 	}
 
 	public Class getReturnedClass() {
 		return java.util.Collection.class;
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			return new PersistentElementHolder( session, (Element) collection );
 		}
 		else {
 			return new PersistentBag( session, (Collection) collection );
 		}
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0 ? new ArrayList() : new ArrayList( anticipatedSize + 1 );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
index f0ec268a4a..11129de1eb 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
@@ -1,716 +1,716 @@
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
 package org.hibernate.type;
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import org.dom4j.Element;
 import org.dom4j.Node;
 import org.hibernate.EntityMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.CollectionKey;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * A type that handles Hibernate <tt>PersistentCollection</tt>s (including arrays).
  * 
  * @author Gavin King
  */
 public abstract class CollectionType extends AbstractType implements AssociationType {
 
 	private static final Object NOT_NULL_COLLECTION = new MarkerObject( "NOT NULL COLLECTION" );
 	public static final Object UNFETCHED_COLLECTION = new MarkerObject( "UNFETCHED COLLECTION" );
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String role;
 	private final String foreignKeyPropertyName;
 	private final boolean isEmbeddedInXML;
 
 	public CollectionType(TypeFactory.TypeScope typeScope, String role, String foreignKeyPropertyName, boolean isEmbeddedInXML) {
 		this.typeScope = typeScope;
 		this.role = role;
 		this.foreignKeyPropertyName = foreignKeyPropertyName;
 		this.isEmbeddedInXML = isEmbeddedInXML;
 	}
 
 	public boolean isEmbeddedInXML() {
 		return isEmbeddedInXML;
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public Object indexOf(Object collection, Object element) {
 		throw new UnsupportedOperationException( "generic collections don't have indexes" );
 	}
 
 	public boolean contains(Object collection, Object childObject, SessionImplementor session) {
 		// we do not have to worry about queued additions to uninitialized
 		// collections, since they can only occur for inverse collections!
 		Iterator elems = getElementsIterator( collection, session );
 		while ( elems.hasNext() ) {
 			Object element = elems.next();
 			// worrying about proxies is perhaps a little bit of overkill here...
 			if ( element instanceof HibernateProxy ) {
 				LazyInitializer li = ( (HibernateProxy) element ).getHibernateLazyInitializer();
 				if ( !li.isUninitialized() ) element = li.getImplementation();
 			}
 			if ( element == childObject ) return true;
 		}
 		return false;
 	}
 
 	public boolean isCollectionType() {
 		return true;
 	}
 
 	public final boolean isEqual(Object x, Object y, EntityMode entityMode) {
 		return x == y
 			|| ( x instanceof PersistentCollection && ( (PersistentCollection) x ).isWrapper( y ) )
 			|| ( y instanceof PersistentCollection && ( (PersistentCollection) y ).isWrapper( x ) );
 	}
 
 	public int compare(Object x, Object y, EntityMode entityMode) {
 		return 0; // collections cannot be compared
 	}
 
 	public int getHashCode(Object x, EntityMode entityMode) {
 		throw new UnsupportedOperationException( "cannot doAfterTransactionCompletion lookups on collections" );
 	}
 
 	/**
 	 * Instantiate an uninitialized collection wrapper or holder. Callers MUST add the holder to the
 	 * persistence context!
 	 *
 	 * @param session The session from which the request is originating.
 	 * @param persister The underlying collection persister (metadata)
 	 * @param key The owner key.
 	 * @return The instantiated collection.
 	 */
 	public abstract PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key);
 
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner) throws SQLException {
 		return nullSafeGet( rs, new String[] { name }, session, owner );
 	}
 
 	public Object nullSafeGet(ResultSet rs, String[] name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( null, session, owner );
 	}
 
 	public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		//NOOP
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 	}
 
 	public int[] sqlTypes(Mapping session) throws MappingException {
 		return ArrayHelper.EMPTY_INT_ARRAY;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return new Size[] { LEGACY_DICTATED_SIZE };
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return new Size[] { LEGACY_DEFAULT_SIZE };
 	}
 
 	public int getColumnSpan(Mapping session) throws MappingException {
 		return 0;
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		else if ( !Hibernate.isInitialized( value ) ) {
 			return "<uninitialized>";
 		}
 		else {
 			return renderLoggableString( value, factory );
 		}
 	}
 
 	protected String renderLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( Element.class.isInstance( value ) ) {
 			// for DOM4J "collections" only
 			// TODO: it would be better if this was done at the higher level by Printer
 			return ( ( Element ) value ).asXML();
 		}
 		else {
 			List list = new ArrayList();
 			Type elemType = getElementType( factory );
 			Iterator iter = getElementsIterator( value );
 			while ( iter.hasNext() ) {
 				list.add( elemType.toLoggableString( iter.next(), factory ) );
 			}
 			return list.toString();
 		}
 	}
 
 	public Object deepCopy(Object value, EntityMode entityMode, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return value;
 	}
 
 	public String getName() {
 		return getReturnedClass().getName() + '(' + getRole() + ')';
 	}
 
 	/**
 	 * Get an iterator over the element set of the collection, which may not yet be wrapped
 	 *
 	 * @param collection The collection to be iterated
 	 * @param session The session from which the request is originating.
 	 * @return The iterator.
 	 */
 	public Iterator getElementsIterator(Object collection, SessionImplementor session) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			final SessionFactoryImplementor factory = session.getFactory();
 			final CollectionPersister persister = factory.getCollectionPersister( getRole() );
 			final Type elementType = persister.getElementType();
 			
 			List elements = ( (Element) collection ).elements( persister.getElementNodeName() );
 			ArrayList results = new ArrayList();
 			for ( int i=0; i<elements.size(); i++ ) {
 				Element value = (Element) elements.get(i);
 				results.add( elementType.fromXMLNode( value, factory ) );
 			}
 			return results.iterator();
 		}
 		else {
 			return getElementsIterator(collection);
 		}
 	}
 
 	/**
 	 * Get an iterator over the element set of the collection in POJO mode
 	 *
 	 * @param collection The collection to be iterated
 	 * @return The iterator.
 	 */
 	protected Iterator getElementsIterator(Object collection) {
 		return ( (Collection) collection ).iterator();
 	}
 
 	public boolean isMutable() {
 		return false;
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//remember the uk value
 		
 		//This solution would allow us to eliminate the owner arg to disassemble(), but
 		//what if the collection was null, and then later had elements added? seems unsafe
 		//session.getPersistenceContext().getCollectionEntry( (PersistentCollection) value ).getKey();
 		
 		final Serializable key = getKeyOfOwner(owner, session);
 		if (key==null) {
 			return null;
 		}
 		else {
 			return getPersister(session)
 					.getKeyType()
 					.disassemble( key, session, owner );
 		}
 	}
 
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//we must use the "remembered" uk value, since it is 
 		//not available from the EntityEntry during assembly
 		if (cached==null) {
 			return null;
 		}
 		else {
 			final Serializable key = (Serializable) getPersister(session)
 					.getKeyType()
 					.assemble( cached, session, owner);
 			return resolveKey( key, session, owner );
 		}
 	}
 
 	/**
 	 * Is the owning entity versioned?
 	 *
 	 * @param session The session from which the request is originating.
 	 * @return True if the collection owner is versioned; false otherwise.
 	 * @throws org.hibernate.MappingException Indicates our persister could not be located.
 	 */
 	private boolean isOwnerVersioned(SessionImplementor session) throws MappingException {
 		return getPersister( session ).getOwnerEntityPersister().isVersioned();
 	}
 
 	/**
 	 * Get our underlying collection persister (using the session to access the
 	 * factory).
 	 *
 	 * @param session The session from which the request is originating.
 	 * @return The underlying collection persister
 	 */
 	private CollectionPersister getPersister(SessionImplementor session) {
 		return session.getFactory().getCollectionPersister( role );
 	}
 
 	public boolean isDirty(Object old, Object current, SessionImplementor session)
 			throws HibernateException {
 
 		// collections don't dirty an unversioned parent entity
 
 		// TODO: I don't really like this implementation; it would be better if
 		// this was handled by searchForDirtyCollections()
 		return isOwnerVersioned( session ) && super.isDirty( old, current, session );
 		// return false;
 
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return isDirty(old, current, session);
 	}
 
 	/**
 	 * Wrap the naked collection instance in a wrapper, or instantiate a
 	 * holder. Callers <b>MUST</b> add the holder to the persistence context!
 	 *
 	 * @param session The session from which the request is originating.
 	 * @param collection The bare collection to be wrapped.
 	 * @return The wrapped collection.
 	 */
 	public abstract PersistentCollection wrap(SessionImplementor session, Object collection);
 
 	/**
 	 * Note: return true because this type is castable to <tt>AssociationType</tt>. Not because
 	 * all collections are associations.
 	 */
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FOREIGN_KEY_TO_PARENT;
 	}
 
 	/**
 	 * Get the key value from the owning entity instance, usually the identifier, but might be some
 	 * other unique key, in the case of property-ref
 	 *
 	 * @param owner The collection owner
 	 * @param session The session from which the request is originating.
 	 * @return The collection owner's key
 	 */
 	public Serializable getKeyOfOwner(Object owner, SessionImplementor session) {
 		
 		EntityEntry entityEntry = session.getPersistenceContext().getEntry( owner );
 		if ( entityEntry == null ) return null; // This just handles a particular case of component
 									  // projection, perhaps get rid of it and throw an exception
 		
 		if ( foreignKeyPropertyName == null ) {
 			return entityEntry.getId();
 		}
 		else {
 			// TODO: at the point where we are resolving collection references, we don't
 			// know if the uk value has been resolved (depends if it was earlier or
 			// later in the mapping document) - now, we could try and use e.getStatus()
 			// to decide to semiResolve(), trouble is that initializeEntity() reuses
 			// the same array for resolved and hydrated values
 			Object id;
 			if ( entityEntry.getLoadedState() != null ) {
 				id = entityEntry.getLoadedValue( foreignKeyPropertyName );
 			}
 			else {
 				id = entityEntry.getPersister().getPropertyValue( owner, foreignKeyPropertyName, session.getEntityMode() );
 			}
 
 			// NOTE VERY HACKISH WORKAROUND!!
 			// TODO: Fix this so it will work for non-POJO entity mode
 			Type keyType = getPersister( session ).getKeyType();
 			if ( !keyType.getReturnedClass().isInstance( id ) ) {
 				id = (Serializable) keyType.semiResolve(
 						entityEntry.getLoadedValue( foreignKeyPropertyName ),
 						session,
 						owner 
 					);
 			}
 
 			return (Serializable) id;
 		}
 	}
 
 	/**
 	 * Get the id value from the owning entity key, usually the same as the key, but might be some
 	 * other property, in the case of property-ref
 	 *
 	 * @param key The collection owner key
 	 * @param session The session from which the request is originating.
 	 * @return The collection owner's id, if it can be obtained from the key;
 	 * otherwise, null is returned
 	 */
 	public Serializable getIdOfOwnerOrNull(Serializable key, SessionImplementor session) {
 		Serializable ownerId = null;
 		if ( foreignKeyPropertyName == null ) {
 			ownerId = key;
 		}
 		else {
 			Type keyType = getPersister( session ).getKeyType();
 			EntityPersister ownerPersister = getPersister( session ).getOwnerEntityPersister();
 			// TODO: Fix this so it will work for non-POJO entity mode
 			Class ownerMappedClass = ownerPersister.getMappedClass( session.getEntityMode() );
 			if ( ownerMappedClass.isAssignableFrom( keyType.getReturnedClass() ) &&
 					keyType.getReturnedClass().isInstance( key ) ) {
 				// the key is the owning entity itself, so get the ID from the key
 				ownerId = ownerPersister.getIdentifier( key, session );
 			}
 			else {
 				// TODO: check if key contains the owner ID
 			}
 		}
 		return ownerId;
 	}
 
 	public Object hydrate(ResultSet rs, String[] name, SessionImplementor session, Object owner) {
 		// can't just return null here, since that would
 		// cause an owning component to become null
 		return NOT_NULL_COLLECTION;
 	}
 
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		
 		return resolveKey( getKeyOfOwner( owner, session ), session, owner );
 	}
 	
 	private Object resolveKey(Serializable key, SessionImplementor session, Object owner) {
 		// if (key==null) throw new AssertionFailure("owner identifier unknown when re-assembling
 		// collection reference");
 		return key == null ? null : // TODO: can this case really occur??
 			getCollection( key, session, owner );
 	}
 
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		throw new UnsupportedOperationException(
 			"collection mappings may not form part of a property-ref" );
 	}
 
 	public boolean isArrayType() {
 		return false;
 	}
 
 	public boolean useLHSPrimaryKey() {
 		return foreignKeyPropertyName == null;
 	}
 
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory)
 			throws MappingException {
 		return (Joinable) factory.getCollectionPersister( role );
 	}
 
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
 		return false;
 	}
 
 	public String getAssociatedEntityName(SessionFactoryImplementor factory)
 			throws MappingException {
 		try {
 			
 			QueryableCollection collectionPersister = (QueryableCollection) factory
 					.getCollectionPersister( role );
 			
 			if ( !collectionPersister.getElementType().isEntityType() ) {
 				throw new MappingException( 
 						"collection was not an association: " + 
 						collectionPersister.getRole() 
 					);
 			}
 			
 			return collectionPersister.getElementPersister().getEntityName();
 			
 		}
 		catch (ClassCastException cce) {
 			throw new MappingException( "collection role is not queryable " + role );
 		}
 	}
 
 	/**
 	 * Replace the elements of a collection with the elements of another collection.
 	 *
 	 * @param original The 'source' of the replacement elements (where we copy from)
 	 * @param target The target of the replacement elements (where we copy to)
 	 * @param owner The owner of the collection being merged
 	 * @param copyCache The map of elements already replaced.
 	 * @param session The session from which the merge event originated.
 	 * @return The merged collection.
 	 */
 	public Object replaceElements(
 			Object original,
 			Object target,
 			Object owner,
 			Map copyCache,
 			SessionImplementor session) {
 		// TODO: does not work for EntityMode.DOM4J yet!
 		java.util.Collection result = ( java.util.Collection ) target;
 		result.clear();
 
 		// copy elements into newly empty target collection
 		Type elemType = getElementType( session.getFactory() );
 		Iterator iter = ( (java.util.Collection) original ).iterator();
 		while ( iter.hasNext() ) {
 			result.add( elemType.replace( iter.next(), null, session, owner, copyCache ) );
 		}
 
 		// if the original is a PersistentCollection, and that original
 		// was not flagged as dirty, then reset the target's dirty flag
 		// here after the copy operation.
 		// </p>
 		// One thing to be careful of here is a "bare" original collection
 		// in which case we should never ever ever reset the dirty flag
 		// on the target because we simply do not know...
 		if ( original instanceof PersistentCollection ) {
 			if ( result instanceof PersistentCollection ) {
 				if ( ! ( ( PersistentCollection ) original ).isDirty() ) {
 					( ( PersistentCollection ) result ).clearDirty();
 				}
 			}
 		}
 
 		return result;
 	}
 
 	/**
 	 * Instantiate a new "underlying" collection exhibiting the same capacity
 	 * charactersitcs and the passed "original".
 	 *
 	 * @param original The original collection.
 	 * @return The newly instantiated collection.
 	 */
 	protected Object instantiateResult(Object original) {
 		// by default just use an unanticipated capacity since we don't
 		// know how to extract the capacity to use from original here...
 		return instantiate( -1 );
 	}
 
 	/**
 	 * Instantiate an empty instance of the "underlying" collection (not a wrapper),
 	 * but with the given anticipated size (i.e. accounting for initial capacity
 	 * and perhaps load factor).
 	 *
 	 * @param anticipatedSize The anticipated size of the instaniated collection
 	 * after we are done populating it.
 	 * @return A newly instantiated collection to be wrapped.
 	 */
 	public abstract Object instantiate(int anticipatedSize);
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object replace(
 			final Object original,
 			final Object target,
 			final SessionImplementor session,
 			final Object owner,
 			final Map copyCache) throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		if ( !Hibernate.isInitialized( original ) ) {
 			return target;
 		}
 
 		// for a null target, or a target which is the same as the original, we
 		// need to put the merged elements in a new collection
 		Object result = target == null || target == original ? instantiateResult( original ) : target;
 		
 		//for arrays, replaceElements() may return a different reference, since
 		//the array length might not match
 		result = replaceElements( original, result, owner, copyCache, session );
 
 		if ( original == target ) {
 			// get the elements back into the target making sure to handle dirty flag
 			boolean wasClean = PersistentCollection.class.isInstance( target ) && !( ( PersistentCollection ) target ).isDirty();
 			//TODO: this is a little inefficient, don't need to do a whole
 			//      deep replaceElements() call
 			replaceElements( result, target, owner, copyCache, session );
 			if ( wasClean ) {
 				( ( PersistentCollection ) target ).clearDirty();
 			}
 			result = target;
 		}
 
 		return result;
 	}
 
 	/**
 	 * Get the Hibernate type of the collection elements
 	 *
 	 * @param factory The session factory.
 	 * @return The type of the collection elements
 	 * @throws MappingException Indicates the underlying persister could not be located.
 	 */
 	public final Type getElementType(SessionFactoryImplementor factory) throws MappingException {
 		return factory.getCollectionPersister( getRole() ).getElementType();
 	}
 
 	public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
 			throws MappingException {
 		return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
 	}
 
 	/**
 	 * instantiate a collection wrapper (called when loading an object)
 	 *
 	 * @param key The collection owner key
 	 * @param session The session from which the request is originating.
 	 * @param owner The collection owner
 	 * @return The collection
 	 */
 	public Object getCollection(Serializable key, SessionImplementor session, Object owner) {
 
 		CollectionPersister persister = getPersister( session );
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityMode entityMode = session.getEntityMode();
 
 		if (entityMode==EntityMode.DOM4J && !isEmbeddedInXML) {
 			return UNFETCHED_COLLECTION;
 		}
 		
 		// check if collection is currently being loaded
 		PersistentCollection collection = persistenceContext.getLoadContexts().locateLoadingCollection( persister, key );
 		
 		if ( collection == null ) {
 			
 			// check if it is already completely loaded, but unowned
 			collection = persistenceContext.useUnownedCollection( new CollectionKey(persister, key, entityMode) );
 			
 			if ( collection == null ) {
 				// create a new collection wrapper, to be initialized later
 				collection = instantiate( session, persister, key );
 				collection.setOwner(owner);
 	
 				persistenceContext.addUninitializedCollection( persister, collection, key );
 	
 				// some collections are not lazy:
 				if ( initializeImmediately( entityMode ) ) {
 					session.initializeCollection( collection, false );
 				}
 				else if ( !persister.isLazy() ) {
 					persistenceContext.addNonLazyCollection( collection );
 				}
 	
 				if ( hasHolder( entityMode ) ) {
 					session.getPersistenceContext().addCollectionHolder( collection );
 				}
 				
 			}
 			
 		}
 		
 		collection.setOwner(owner);
 
 		return collection.getValue();
 	}
 
 	public boolean hasHolder(EntityMode entityMode) {
 		return entityMode == EntityMode.DOM4J;
 	}
 
 	protected boolean initializeImmediately(EntityMode entityMode) {
 		return entityMode == EntityMode.DOM4J;
 	}
 
 	public String getLHSPropertyName() {
 		return foreignKeyPropertyName;
 	}
 
 	public boolean isXMLElement() {
 		return true;
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return xml;
 	}
 
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) 
 	throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			node.detach();
 		}
 		else {
 			replaceNode( node, (Element) value );
 		}
 	}
 	
 	/**
 	 * We always need to dirty check the collection because we sometimes 
 	 * need to incremement version number of owner and also because of 
 	 * how assemble/disassemble is implemented for uks
 	 */
 	public boolean isAlwaysDirtyChecked() {
 		return true; 
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
index a5936af3d9..a3b38ba7e7 100755
--- a/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
@@ -1,119 +1,119 @@
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
 package org.hibernate.type;
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.Map;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.usertype.LoggableUserType;
 import org.hibernate.usertype.UserCollectionType;
 
 /**
  * A custom type for mapping user-written classes that implement <tt>PersistentCollection</tt>
  * 
- * @see org.hibernate.collection.PersistentCollection
+ * @see org.hibernate.collection.spi.PersistentCollection
  * @see org.hibernate.usertype.UserCollectionType
  * @author Gavin King
  */
 public class CustomCollectionType extends CollectionType {
 
 	private final UserCollectionType userType;
 	private final boolean customLogging;
 
 	public CustomCollectionType(
 			TypeFactory.TypeScope typeScope,
 			Class userTypeClass,
 			String role,
 			String foreignKeyPropertyName,
 			boolean isEmbeddedInXML) {
 		super( typeScope, role, foreignKeyPropertyName, isEmbeddedInXML );
 
 		if ( !UserCollectionType.class.isAssignableFrom( userTypeClass ) ) {
 			throw new MappingException( "Custom type does not implement UserCollectionType: " + userTypeClass.getName() );
 		}
 
 		try {
 			userType = ( UserCollectionType ) userTypeClass.newInstance();
 		}
 		catch ( InstantiationException ie ) {
 			throw new MappingException( "Cannot instantiate custom type: " + userTypeClass.getName() );
 		}
 		catch ( IllegalAccessException iae ) {
 			throw new MappingException( "IllegalAccessException trying to instantiate custom type: " + userTypeClass.getName() );
 		}
 
 		customLogging = LoggableUserType.class.isAssignableFrom( userTypeClass );
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key)
 	throws HibernateException {
 		return userType.instantiate(session, persister);
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return userType.wrap(session, collection);
 	}
 
 	public Class getReturnedClass() {
 		return userType.instantiate( -1 ).getClass();
 	}
 
 	public Object instantiate(int anticipatedType) {
 		return userType.instantiate( anticipatedType );
 	}
 
 	public Iterator getElementsIterator(Object collection) {
 		return userType.getElementsIterator(collection);
 	}
 	public boolean contains(Object collection, Object entity, SessionImplementor session) {
 		return userType.contains(collection, entity);
 	}
 	public Object indexOf(Object collection, Object entity) {
 		return userType.indexOf(collection, entity);
 	}
 
 	public Object replaceElements(Object original, Object target, Object owner, Map copyCache, SessionImplementor session)
 	throws HibernateException {
 		CollectionPersister cp = session.getFactory().getCollectionPersister( getRole() );
 		return userType.replaceElements(original, target, cp, owner, copyCache, session);
 	}
 
 	protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( customLogging ) {
 			return ( ( LoggableUserType ) userType ).toLoggableString( value, factory );
 		}
 		else {
 			return super.renderLoggableString( value, factory );
 		}
 	}
 
 	public UserCollectionType getUserType() {
 		return userType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java b/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
index 696a8c2de8..6835c35104 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
@@ -1,65 +1,65 @@
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
 package org.hibernate.type;
 import java.io.Serializable;
 import java.util.ArrayList;
 import org.hibernate.HibernateException;
-import org.hibernate.collection.PersistentCollection;
-import org.hibernate.collection.PersistentIdentifierBag;
+import org.hibernate.collection.internal.PersistentIdentifierBag;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class IdentifierBagType extends CollectionType {
 
 	public IdentifierBagType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
 	public PersistentCollection instantiate(
 		SessionImplementor session,
 		CollectionPersister persister, Serializable key)
 		throws HibernateException {
 
 		return new PersistentIdentifierBag(session);
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0 ? new ArrayList() : new ArrayList( anticipatedSize + 1 );
 	}
 	
 	public Class getReturnedClass() {
 		return java.util.Collection.class;
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentIdentifierBag( session, (java.util.Collection) collection );
 	}
 
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ListType.java b/hibernate-core/src/main/java/org/hibernate/type/ListType.java
index 21b1966a14..d5d88f6985 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ListType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ListType.java
@@ -1,82 +1,82 @@
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
 package org.hibernate.type;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 import org.dom4j.Element;
 import org.hibernate.EntityMode;
-import org.hibernate.collection.PersistentCollection;
-import org.hibernate.collection.PersistentList;
-import org.hibernate.collection.PersistentListElementHolder;
+import org.hibernate.collection.internal.PersistentListElementHolder;
+import org.hibernate.collection.spi.PersistentCollection;
+import org.hibernate.collection.internal.PersistentList;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class ListType extends CollectionType {
 
 	public ListType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			return new PersistentListElementHolder(session, persister, key);
 		}
 		else {
 			return new PersistentList(session);
 		}
 	}
 
 	public Class getReturnedClass() {
 		return List.class;
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			return new PersistentListElementHolder( session, (Element) collection );
 		}
 		else {
 			return new PersistentList( session, (List) collection );
 		}
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0 ? new ArrayList() : new ArrayList( anticipatedSize + 1 );
 	}
 	
 	public Object indexOf(Object collection, Object element) {
 		List list = (List) collection;
 		for ( int i=0; i<list.size(); i++ ) {
 			//TODO: proxies!
 			if ( list.get(i)==element ) return new Integer(i);
 		}
 		return null;
 	}
 	
 }
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/MapType.java b/hibernate-core/src/main/java/org/hibernate/type/MapType.java
index 9e3f65db4a..c367268918 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/MapType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/MapType.java
@@ -1,112 +1,112 @@
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
 package org.hibernate.type;
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import org.dom4j.Element;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
-import org.hibernate.collection.PersistentCollection;
-import org.hibernate.collection.PersistentMap;
-import org.hibernate.collection.PersistentMapElementHolder;
+import org.hibernate.collection.internal.PersistentMap;
+import org.hibernate.collection.spi.PersistentCollection;
+import org.hibernate.collection.internal.PersistentMapElementHolder;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 
 public class MapType extends CollectionType {
 
 	public MapType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			return new PersistentMapElementHolder(session, persister, key);
 		}
 		else {
 			return new PersistentMap(session);
 		}
 	}
 
 	public Class getReturnedClass() {
 		return Map.class;
 	}
 
 	public Iterator getElementsIterator(Object collection) {
 		return ( (java.util.Map) collection ).values().iterator();
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			return new PersistentMapElementHolder( session, (Element) collection );
 		}
 		else {
 			return new PersistentMap( session, (java.util.Map) collection );
 		}
 	}
 	
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0 
 		       ? new HashMap()
 		       : new HashMap( anticipatedSize + (int)( anticipatedSize * .75f ), .75f );
 	}
 
 	public Object replaceElements(
 		final Object original,
 		final Object target,
 		final Object owner, 
 		final java.util.Map copyCache, 
 		final SessionImplementor session)
 		throws HibernateException {
 
 		CollectionPersister cp = session.getFactory().getCollectionPersister( getRole() );
 		
 		java.util.Map result = (java.util.Map) target;
 		result.clear();
 		
 		Iterator iter = ( (java.util.Map) original ).entrySet().iterator();
 		while ( iter.hasNext() ) {
 			java.util.Map.Entry me = (java.util.Map.Entry) iter.next();
 			Object key = cp.getIndexType().replace( me.getKey(), null, session, owner, copyCache );
 			Object value = cp.getElementType().replace( me.getValue(), null, session, owner, copyCache );
 			result.put(key, value);
 		}
 		
 		return result;
 		
 	}
 	
 	public Object indexOf(Object collection, Object element) {
 		Iterator iter = ( (Map) collection ).entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			//TODO: proxies!
 			if ( me.getValue()==element ) return me.getKey();
 		}
 		return null;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/SetType.java b/hibernate-core/src/main/java/org/hibernate/type/SetType.java
index f02c57ba0e..0d9bdf06a2 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/SetType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SetType.java
@@ -1,69 +1,69 @@
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
 package org.hibernate.type;
 import java.io.Serializable;
 import java.util.HashSet;
 import org.dom4j.Element;
 import org.hibernate.EntityMode;
-import org.hibernate.collection.PersistentCollection;
-import org.hibernate.collection.PersistentElementHolder;
-import org.hibernate.collection.PersistentSet;
+import org.hibernate.collection.internal.PersistentElementHolder;
+import org.hibernate.collection.spi.PersistentCollection;
+import org.hibernate.collection.internal.PersistentSet;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class SetType extends CollectionType {
 
 	public SetType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			return new PersistentElementHolder(session, persister, key);
 		}
 		else {
 			return new PersistentSet(session);
 		}
 	}
 
 	public Class getReturnedClass() {
 		return java.util.Set.class;
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			return new PersistentElementHolder( session, (Element) collection );
 		}
 		else {
 			return new PersistentSet( session, (java.util.Set) collection );
 		}
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0
 		       ? new HashSet()
 		       : new HashSet( anticipatedSize + (int)( anticipatedSize * .75f ), .75f );
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java b/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
index 01f4808bf7..b092cff903 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
@@ -1,81 +1,81 @@
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
 package org.hibernate.type;
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.TreeMap;
 import org.dom4j.Element;
 import org.hibernate.EntityMode;
-import org.hibernate.collection.PersistentCollection;
-import org.hibernate.collection.PersistentElementHolder;
-import org.hibernate.collection.PersistentMapElementHolder;
-import org.hibernate.collection.PersistentSortedMap;
+import org.hibernate.collection.internal.PersistentElementHolder;
+import org.hibernate.collection.internal.PersistentSortedMap;
+import org.hibernate.collection.spi.PersistentCollection;
+import org.hibernate.collection.internal.PersistentMapElementHolder;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 
 public class SortedMapType extends MapType {
 
 	private final Comparator comparator;
 
 	public SortedMapType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Comparator comparator, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 		this.comparator = comparator;
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			return new PersistentMapElementHolder(session, persister, key);
 		}
 		else {
 			PersistentSortedMap map = new PersistentSortedMap(session);
 			map.setComparator(comparator);
 			return map;
 		}
 	}
 
 	public Class getReturnedClass() {
 		return java.util.SortedMap.class;
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return new TreeMap(comparator);
 	}
 	
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			return new PersistentElementHolder( session, (Element) collection );
 		}
 		else {
 			return new PersistentSortedMap( session, (java.util.SortedMap) collection );
 		}
 	}
 
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java b/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
index e9561e7f39..9ae668a551 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
@@ -1,78 +1,78 @@
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
 package org.hibernate.type;
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.TreeSet;
 import org.dom4j.Element;
 import org.hibernate.EntityMode;
-import org.hibernate.collection.PersistentCollection;
-import org.hibernate.collection.PersistentElementHolder;
-import org.hibernate.collection.PersistentSortedSet;
+import org.hibernate.collection.internal.PersistentElementHolder;
+import org.hibernate.collection.internal.PersistentSortedSet;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class SortedSetType extends SetType {
 
 	private final Comparator comparator;
 
 	public SortedSetType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Comparator comparator, boolean isEmbeddedInXML) {
 		super( typeScope, role, propertyRef, isEmbeddedInXML );
 		this.comparator = comparator;
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			return new PersistentElementHolder(session, persister, key);
 		}
 		else {
 			PersistentSortedSet set = new PersistentSortedSet(session);
 			set.setComparator(comparator);
 			return set;
 		}
 	}
 
 	public Class getReturnedClass() {
 		return java.util.SortedSet.class;
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return new TreeSet(comparator);
 	}
 	
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			return new PersistentElementHolder( session, (Element) collection );
 		}
 		else {
 			return new PersistentSortedSet( session, (java.util.SortedSet) collection );
 		}
 	}
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/usertype/UserCollectionType.java b/hibernate-core/src/main/java/org/hibernate/usertype/UserCollectionType.java
index e9633b648c..87e75c4ca4 100755
--- a/hibernate-core/src/main/java/org/hibernate/usertype/UserCollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/usertype/UserCollectionType.java
@@ -1,91 +1,91 @@
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
 package org.hibernate.usertype;
 import java.util.Iterator;
 import java.util.Map;
 import org.hibernate.HibernateException;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * A custom type for mapping user-written classes that implement <tt>PersistentCollection</tt>
  * 
- * @see org.hibernate.collection.PersistentCollection
+ * @see org.hibernate.collection.spi.PersistentCollection
  * @author Gavin King
  */
 public interface UserCollectionType {
 	
 	/**
 	 * Instantiate an uninitialized instance of the collection wrapper
 	 */
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister) 
 	throws HibernateException;
 	
 	/**
 	 * Wrap an instance of a collection
 	 */
 	public PersistentCollection wrap(SessionImplementor session, Object collection);
 	
 	/**
 	 * Return an iterator over the elements of this collection - the passed collection
 	 * instance may or may not be a wrapper
 	 */
 	public Iterator getElementsIterator(Object collection);
 
 	/**
 	 * Optional operation. Does the collection contain the entity instance?
 	 */
 	public boolean contains(Object collection, Object entity);
 	/**
 	 * Optional operation. Return the index of the entity in the collection.
 	 */
 	public Object indexOf(Object collection, Object entity);
 	
 	/**
 	 * Replace the elements of a collection with the elements of another collection
 	 */
 	public Object replaceElements(
 			Object original, 
 			Object target, 
 			CollectionPersister persister, 
 			Object owner, 
 			Map copyCache, 
 			SessionImplementor session)
 			throws HibernateException;
 
 	/**
 	 * Instantiate an empty instance of the "underlying" collection (not a wrapper),
 	 * but with the given anticipated size (i.e. accounting for initial size
 	 * and perhaps load factor).
 	 *
 	 * @param anticipatedSize The anticipated size of the instaniated collection
 	 * after we are done populating it.  Note, may be negative to indicate that
 	 * we not yet know anything about the anticipated size (i.e., when initializing
 	 * from a result set row by row).
 	 */
 	public Object instantiate(int anticipatedSize);
 	
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index c0285a8d2c..947014aead 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
@@ -1,700 +1,700 @@
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
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Comparator;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class GoofyPersisterClassProvider implements PersisterClassResolver {
 	@Override
 	public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	public static class NoopEntityPersister implements EntityPersister {
 
 		public NoopEntityPersister(org.hibernate.mapping.PersistentClass persistentClass,
 								   org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
 								   org.hibernate.engine.SessionFactoryImplementor sf,
 								   org.hibernate.engine.Mapping mapping) {
 			throw new GoofyException(NoopEntityPersister.class);
 		}
 
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRootEntityName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getEntityName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityMetamodel getEntityMetamodel() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isSubclassEntityName(String entityName) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasProxy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCollections() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasMutableProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasSubselectLoadableCollections() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCascades() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInherited() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isIdentifierAssignedByInsert() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIdentifierProperty() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean canExtractIdOutOfEntity() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Comparator getVersionComparator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public VersionType getVersionType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getVersionProperty() {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasNaturalIdentifier() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasLazyProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type[] getPropertyTypes() {
 			return new Type[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getPropertyNames() {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierPropertyName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCacheInvalidationRequired() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazyPropertiesCacheable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public ClassMetadata getClassMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isBatchLoadable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityMode guessEntityMode(Object object) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInstrumented(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasInsertGeneratedProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasUpdateGeneratedProperties() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersionPropertyGenerated() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void afterReassociate(Object entity, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
 				throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getMappedClass(EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean implementsLifecycle(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean implementsValidatable(EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getConcreteProxyClass(EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object[] getPropertyValues(Object object, EntityMode entityMode) throws HibernateException {
 			return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getPropertyValue(Object object, int i, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode)
 				throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setIdentifier(Object entity, Serializable id, EntityMode entityMode) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getVersion(Object object, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object instantiate(Serializable id, EntityMode entityMode) throws HibernateException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInstance(Object object, EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory, EntityMode entityMode) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 	}
 
 	public static class NoopCollectionPersister implements CollectionPersister {
 
 		public NoopCollectionPersister(org.hibernate.mapping.Collection collection,
 									   org.hibernate.cache.spi.access.CollectionRegionAccessStrategy strategy,
 									   org.hibernate.cfg.Configuration configuration,
 									   org.hibernate.engine.SessionFactoryImplementor sf) {
 			throw new GoofyException(NoopCollectionPersister.class);
 		}
 
 		public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasCache() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionRegionAccessStrategy getCacheAccessStrategy() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionType getCollectionType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getKeyType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIndexType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getElementType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Class getElementClass() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readKey(ResultSet rs, String[] keyAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readElement(ResultSet rs, Object owner, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIndex(ResultSet rs, String[] columnAliases, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object readIdentifier(ResultSet rs, String columnAlias, SessionImplementor session)
 				throws HibernateException, SQLException {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isPrimitiveArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isArray() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isOneToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isManyToMany() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasIndex() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isInverse() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void recreate(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void deleteRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void updateRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void insertRows(PersistentCollection collection, Serializable key, SessionImplementor session)
 				throws HibernateException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getRole() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public EntityPersister getOwnerEntityPersister() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Type getIdentifierType() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrphanDelete() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean hasManyToManyOrdering() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Serializable[] getCollectionSpaces() {
 			return new Serializable[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public CollectionMetadata getCollectionMetadata() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isCascadeDeleteEnabled() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isVersioned() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isMutable() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getElementNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIndexNodeName() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public void postInstantiate() throws MappingException {
 			//To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public SessionFactoryImplementor getFactory() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getKeyColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getIndexColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String[] getElementColumnAliases(String suffix) {
 			return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public String getIdentifierColumnAlias(String suffix) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean isExtraLazy() {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public int getSize(Serializable key, SessionImplementor session) {
 			return 0;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 			return false;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/bag/PersistentBagTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/bag/PersistentBagTest.java
index 76de41cc9a..9091ff79cc 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/collection/bag/PersistentBagTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/bag/PersistentBagTest.java
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
 package org.hibernate.test.collection.bag;
 import java.util.ArrayList;
 
 import org.hibernate.Session;
-import org.hibernate.collection.PersistentBag;
+import org.hibernate.collection.internal.PersistentBag;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests related to operations on a PersistentBag.
  *
  * @author Steve Ebersole
  */
 public class PersistentBagTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "collection/bag/Mappings.hbm.xml" };
 	}
 
 	@Test
 	public void testWriteMethodDirtying() {
 		BagOwner parent = new BagOwner( "root" );
 		BagOwner child = new BagOwner( "c1" );
 		parent.getChildren().add( child );
 		child.setParent( parent );
 		BagOwner otherChild = new BagOwner( "c2" );
 
 		Session session = openSession();
 		session.beginTransaction();
 		session.save( parent );
 		session.flush();
 		// at this point, the list on parent has now been replaced with a PersistentBag...
 		PersistentBag children = ( PersistentBag ) parent.getChildren();
 
 		assertFalse( children.remove( otherChild ) );
 		assertFalse( children.isDirty() );
 
 		ArrayList otherCollection = new ArrayList();
 		otherCollection.add( child );
 		assertFalse( children.retainAll( otherCollection ) );
 		assertFalse( children.isDirty() );
 
 		otherCollection = new ArrayList();
 		otherCollection.add( otherChild );
 		assertFalse( children.removeAll( otherCollection ) );
 		assertFalse( children.isDirty() );
 
 		children.clear();
 		session.delete( child );
 		assertTrue( children.isDirty() );
 
 		session.flush();
 
 		children.clear();
 		assertFalse( children.isDirty() );
 
 		session.delete( parent );
 		session.getTransaction().commit();
 		session.close();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/idbag/PersistentIdBagTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/idbag/PersistentIdBagTest.java
index 2494797704..87f0727a65 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/collection/idbag/PersistentIdBagTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/idbag/PersistentIdBagTest.java
@@ -1,88 +1,88 @@
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
 package org.hibernate.test.collection.idbag;
 import java.util.ArrayList;
 
 import org.hibernate.Session;
-import org.hibernate.collection.PersistentIdentifierBag;
+import org.hibernate.collection.internal.PersistentIdentifierBag;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests related to operations on a PersistentIdentifierBag
  *
  * @author Steve Ebersole
  */
 public class PersistentIdBagTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "collection/idbag/Mappings.hbm.xml" };
 	}
 
 	@Test
 	public void testWriteMethodDirtying() {
 		IdbagOwner parent = new IdbagOwner( "root" );
 		IdbagOwner child = new IdbagOwner( "c1" );
 		parent.getChildren().add( child );
 		IdbagOwner otherChild = new IdbagOwner( "c2" );
 
 		Session session = openSession();
 		session.beginTransaction();
 		session.save( parent );
 		session.flush();
 		// at this point, the list on parent has now been replaced with a PersistentBag...
 		PersistentIdentifierBag children = ( PersistentIdentifierBag ) parent.getChildren();
 
 		assertFalse( children.remove( otherChild ) );
 		assertFalse( children.isDirty() );
 
 		ArrayList otherCollection = new ArrayList();
 		otherCollection.add( child );
 		assertFalse( children.retainAll( otherCollection ) );
 		assertFalse( children.isDirty() );
 
 		otherCollection = new ArrayList();
 		otherCollection.add( otherChild );
 		assertFalse( children.removeAll( otherCollection ) );
 		assertFalse( children.isDirty() );
 
 		children.clear();
 		session.delete( child );
 		assertTrue( children.isDirty() );
 
 		session.flush();
 
 		children.clear();
 		assertFalse( children.isDirty() );
 
 		session.delete( parent );
 		session.getTransaction().commit();
 		session.close();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/list/PersistentListTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/list/PersistentListTest.java
index 83717023a5..8b2c458bec 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/collection/list/PersistentListTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/list/PersistentListTest.java
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
 package org.hibernate.test.collection.list;
 import java.util.ArrayList;
 
 import org.hibernate.Session;
-import org.hibernate.collection.PersistentList;
+import org.hibernate.collection.internal.PersistentList;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Tests related to operations on a PersistentList
  *
  * @author Steve Ebersole
  */
 public class PersistentListTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "collection/list/Mappings.hbm.xml" };
 	}
 
 	@Test
 	public void testWriteMethodDirtying() {
 		ListOwner parent = new ListOwner( "root" );
 		ListOwner child = new ListOwner( "c1" );
 		parent.getChildren().add( child );
 		child.setParent( parent );
 		ListOwner otherChild = new ListOwner( "c2" );
 
 		Session session = openSession();
 		session.beginTransaction();
 		session.save( parent );
 		session.flush();
 		// at this point, the list on parent has now been replaced with a PersistentList...
-		PersistentList children = ( PersistentList ) parent.getChildren();
+		PersistentList children = (PersistentList) parent.getChildren();
 
 		assertFalse( children.remove( otherChild ) );
 		assertFalse( children.isDirty() );
 
 		ArrayList otherCollection = new ArrayList();
 		otherCollection.add( child );
 		assertFalse( children.retainAll( otherCollection ) );
 		assertFalse( children.isDirty() );
 
 		otherCollection = new ArrayList();
 		otherCollection.add( otherChild );
 		assertFalse( children.removeAll( otherCollection ) );
 		assertFalse( children.isDirty() );
 
 		children.clear();
 		session.delete( child );
 		assertTrue( children.isDirty() );
 
 		session.flush();
 
 		children.clear();
 		assertFalse( children.isDirty() );
 
 		session.delete( parent );
 		session.getTransaction().commit();
 		session.close();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/map/PersistentMapTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/map/PersistentMapTest.java
index df9f4659cf..a0ea17bba7 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/collection/map/PersistentMapTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/map/PersistentMapTest.java
@@ -1,161 +1,161 @@
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
 package org.hibernate.test.collection.map;
 import java.util.HashMap;
 
 import org.hibernate.Session;
-import org.hibernate.collection.PersistentMap;
+import org.hibernate.collection.internal.PersistentMap;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Test various situations using a {@link PersistentMap}.
  *
  * @author Steve Ebersole
  */
 public class PersistentMapTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "collection/map/Mappings.hbm.xml" };
 	}
 
 	@Test
 	@SuppressWarnings({ "unchecked" })
 	public void testWriteMethodDirtying() {
 		Parent parent = new Parent( "p1" );
 		Child child = new Child( "c1" );
 		parent.getChildren().put( child.getName(), child );
 		child.setParent( parent );
 		Child otherChild = new Child( "c2" );
 
 		Session session = openSession();
 		session.beginTransaction();
 		session.save( parent );
 		session.flush();
 		// at this point, the map on parent has now been replaced with a PersistentMap...
 		PersistentMap children = ( PersistentMap ) parent.getChildren();
 
 		Object old = children.put( child.getName(), child );
 		assertTrue( old == child );
 		assertFalse( children.isDirty() );
 
 		old = children.remove( otherChild.getName() );
 		assertNull( old );
 		assertFalse( children.isDirty() );
 
 		HashMap otherMap = new HashMap();
 		otherMap.put( child.getName(), child );
 		children.putAll( otherMap );
 		assertFalse( children.isDirty() );
 
 		otherMap = new HashMap();
 		otherMap.put( otherChild.getName(), otherChild );
 		children.putAll( otherMap );
 		assertTrue( children.isDirty() );
 
 		children.clearDirty();
 		session.delete( child );
 		children.clear();
 		assertTrue( children.isDirty() );
 		session.flush();
 
 		children.clear();
 		assertFalse( children.isDirty() );
 
 		session.delete( parent );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testPutAgainstUninitializedMap() {
 		// prepare map owner...
 		Session session = openSession();
 		session.beginTransaction();
 		Parent parent = new Parent( "p1" );
 		session.save( parent );
 		session.getTransaction().commit();
 		session.close();
 
 		// Now, reload the parent and test adding children
 		session = openSession();
 		session.beginTransaction();
 		parent = ( Parent ) session.get( Parent.class, parent.getName() );
 		parent.addChild( "c1" );
 		parent.addChild( "c2" );
 		session.getTransaction().commit();
 		session.close();
 
 		assertEquals( 2, parent.getChildren().size() );
 
 		session = openSession();
 		session.beginTransaction();
 		session.delete( parent );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
     public void testRemoveAgainstUninitializedMap() {
         Parent parent = new Parent( "p1" );
         Child child = new Child( "c1" );
         parent.addChild( child );
 
         Session session = openSession();
         session.beginTransaction();
         session.save( parent );
         session.getTransaction().commit();
         session.close();
 
         // Now reload the parent and test removing the child
         session = openSession();
         session.beginTransaction();
         parent = ( Parent ) session.get( Parent.class, parent.getName() );
         Child child2 = ( Child ) parent.getChildren().remove( child.getName() );
 		child2.setParent( null );
 		assertNotNull( child2 );
 		assertTrue( parent.getChildren().isEmpty() );
         session.getTransaction().commit();
         session.close();
 
 		// Load the parent once again and make sure child is still gone
 		//		then cleanup
         session = openSession();
         session.beginTransaction();
 		parent = ( Parent ) session.get( Parent.class, parent.getName() );
 		assertTrue( parent.getChildren().isEmpty() );
 		session.delete( child2 );
 		session.delete( parent );
         session.getTransaction().commit();
         session.close();
     }
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/set/PersistentSetTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/set/PersistentSetTest.java
index 8081d8860a..f6dd8c91f7 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/collection/set/PersistentSetTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/set/PersistentSetTest.java
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
 package org.hibernate.test.collection.set;
 
 import java.util.HashSet;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Session;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.collection.PersistentSet;
+import org.hibernate.collection.internal.PersistentSet;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.stat.CollectionStatistics;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 import org.junit.Test;
 
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public class PersistentSetTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "collection/set/Mappings.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 	}
 
 	@Test
 	public void testWriteMethodDirtying() {
 		Parent parent = new Parent( "p1" );
 		Child child = new Child( "c1" );
 		parent.getChildren().add( child );
 		child.setParent( parent );
 		Child otherChild = new Child( "c2" );
 
 		Session session = openSession();
 		session.beginTransaction();
 		session.save( parent );
 		session.flush();
 		// at this point, the set on parent has now been replaced with a PersistentSet...
 		PersistentSet children = ( PersistentSet ) parent.getChildren();
 
 		assertFalse( children.add( child ) );
 		assertFalse( children.isDirty() );
 
 		assertFalse( children.remove( otherChild ) );
 		assertFalse( children.isDirty() );
 
 		HashSet otherSet = new HashSet();
 		otherSet.add( child );
 		assertFalse( children.addAll( otherSet ) );
 		assertFalse( children.isDirty() );
 
 		assertFalse( children.retainAll( otherSet ) );
 		assertFalse( children.isDirty() );
 
 		otherSet = new HashSet();
 		otherSet.add( otherChild );
 		assertFalse( children.removeAll( otherSet ) );
 		assertFalse( children.isDirty() );
 
 		assertTrue( children.retainAll( otherSet ));
 		assertTrue( children.isDirty() );
 		assertTrue( children.isEmpty() );
 
 		children.clear();
 		session.delete( child );
 		assertTrue( children.isDirty() );
 
 		session.flush();
 
 		children.clear();
 		assertFalse( children.isDirty() );
 
 		session.delete( parent );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testCollectionMerging() {
 		Session session = openSession();
 		session.beginTransaction();
 		Parent parent = new Parent( "p1" );
 		Child child = new Child( "c1" );
 		parent.getChildren().add( child );
 		child.setParent( parent );
 		session.save( parent );
 		session.getTransaction().commit();
 		session.close();
 
 		CollectionStatistics stats =  sessionFactory().getStatistics().getCollectionStatistics( Parent.class.getName() + ".children" );
 		long recreateCount = stats.getRecreateCount();
 		long updateCount = stats.getUpdateCount();
 
 		session = openSession();
 		session.beginTransaction();
 		parent = ( Parent ) session.merge( parent );
 		session.getTransaction().commit();
 		session.close();
 
 		assertEquals( 1, parent.getChildren().size() );
 		assertEquals( recreateCount, stats.getRecreateCount() );
 		assertEquals( updateCount, stats.getUpdateCount() );
 
 		session = openSession();
 		session.beginTransaction();
 		parent = ( Parent ) session.get( Parent.class, "p1" );
 		assertEquals( 1, parent.getChildren().size() );
 		session.delete( parent );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testCollectiondirtyChecking() {
 		Session session = openSession();
 		session.beginTransaction();
 		Parent parent = new Parent( "p1" );
 		Child child = new Child( "c1" );
 		parent.getChildren().add( child );
 		child.setParent( parent );
 		session.save( parent );
 		session.getTransaction().commit();
 		session.close();
 
 		CollectionStatistics stats =  sessionFactory().getStatistics().getCollectionStatistics( Parent.class.getName() + ".children" );
 		long recreateCount = stats.getRecreateCount();
 		long updateCount = stats.getUpdateCount();
 
 		session = openSession();
 		session.beginTransaction();
 		parent = ( Parent ) session.get( Parent.class, "p1" );
 		assertEquals( 1, parent.getChildren().size() );
 		session.getTransaction().commit();
 		session.close();
 
 		assertEquals( 1, parent.getChildren().size() );
 		assertEquals( recreateCount, stats.getRecreateCount() );
 		assertEquals( updateCount, stats.getUpdateCount() );
 
 		session = openSession();
 		session.beginTransaction();
 		assertEquals( 1, parent.getChildren().size() );
 		session.delete( parent );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testCompositeElementWriteMethodDirtying() {
 		Container container = new Container( "p1" );
 		Container.Content c1 = new Container.Content( "c1" );
 		container.getContents().add( c1 );
 		Container.Content c2 = new Container.Content( "c2" );
 
 		Session session = openSession();
 		session.beginTransaction();
 		session.save( container );
 		session.flush();
 		// at this point, the set on container has now been replaced with a PersistentSet...
-		PersistentSet children = ( PersistentSet ) container.getContents();
+		PersistentSet children = (PersistentSet) container.getContents();
 
 		assertFalse( children.add( c1 ) );
 		assertFalse( children.isDirty() );
 
 		assertFalse( children.remove( c2 ) );
 		assertFalse( children.isDirty() );
 
 		HashSet otherSet = new HashSet();
 		otherSet.add( c1 );
 		assertFalse( children.addAll( otherSet ) );
 		assertFalse( children.isDirty() );
 
 		assertFalse( children.retainAll( otherSet ) );
 		assertFalse( children.isDirty() );
 
 		otherSet = new HashSet();
 		otherSet.add( c2 );
 		assertFalse( children.removeAll( otherSet ) );
 		assertFalse( children.isDirty() );
 
 		assertTrue( children.retainAll( otherSet ));
 		assertTrue( children.isDirty() );
 		assertTrue( children.isEmpty() );
 
 		children.clear();
 		assertTrue( children.isDirty() );
 
 		session.flush();
 
 		children.clear();
 		assertFalse( children.isDirty() );
 
 		session.delete( container );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "HHH-2485" )
 	public void testCompositeElementMerging() {
 		Session session = openSession();
 		session.beginTransaction();
 		Container container = new Container( "p1" );
 		Container.Content c1 = new Container.Content( "c1" );
 		container.getContents().add( c1 );
 		session.save( container );
 		session.getTransaction().commit();
 		session.close();
 
 		CollectionStatistics stats =  sessionFactory().getStatistics().getCollectionStatistics( Container.class.getName() + ".contents" );
 		long recreateCount = stats.getRecreateCount();
 		long updateCount = stats.getUpdateCount();
 
 		container.setName( "another name" );
 
 		session = openSession();
 		session.beginTransaction();
 		container = ( Container ) session.merge( container );
 		session.getTransaction().commit();
 		session.close();
 
 		assertEquals( 1, container.getContents().size() );
 		assertEquals( recreateCount, stats.getRecreateCount() );
 		assertEquals( updateCount, stats.getUpdateCount() );
 
 		session = openSession();
 		session.beginTransaction();
 		container = ( Container ) session.get( Container.class, container.getId() );
 		assertEquals( 1, container.getContents().size() );
 		session.delete( container );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "HHH-2485" )
 	public void testCompositeElementCollectionDirtyChecking() {
 		Session session = openSession();
 		session.beginTransaction();
 		Container container = new Container( "p1" );
 		Container.Content c1 = new Container.Content( "c1" );
 		container.getContents().add( c1 );
 		session.save( container );
 		session.getTransaction().commit();
 		session.close();
 
 		CollectionStatistics stats =  sessionFactory().getStatistics().getCollectionStatistics( Container.class.getName() + ".contents" );
 		long recreateCount = stats.getRecreateCount();
 		long updateCount = stats.getUpdateCount();
 
 		session = openSession();
 		session.beginTransaction();
 		container = ( Container ) session.get( Container.class, container.getId() );
 		assertEquals( 1, container.getContents().size() );
 		session.getTransaction().commit();
 		session.close();
 
 		assertEquals( 1, container.getContents().size() );
 		assertEquals( recreateCount, stats.getRecreateCount() );
 		assertEquals( updateCount, stats.getUpdateCount() );
 
 		session = openSession();
 		session.beginTransaction();
 		container = ( Container ) session.get( Container.class, container.getId() );
 		assertEquals( 1, container.getContents().size() );
 		session.delete( container );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testLoadChildCheckParentContainsChildCache() {
 		Parent parent = new Parent( "p1" );
 		Child child = new Child( "c1" );
 		child.setDescription( "desc1" );
 		parent.getChildren().add( child );
 		child.setParent( parent );
 		Child otherChild = new Child( "c2" );
 		otherChild.setDescription( "desc2" );
 		parent.getChildren().add( otherChild );
 		otherChild.setParent( parent );
 
 		Session session = openSession();
 		session.beginTransaction();
 		session.save( parent );
 		session.getTransaction().commit();
 
 		session = openSession();
 		session.beginTransaction();
 		parent = ( Parent ) session.get( Parent.class, parent.getName() );
 		assertTrue( parent.getChildren().contains( child ) );
 		assertTrue( parent.getChildren().contains( otherChild ) );
 		session.getTransaction().commit();
 
 		session = openSession();
 		session.beginTransaction();
 
 		child = ( Child ) session.get( Child.class, child.getName() );
 		assertTrue( child.getParent().getChildren().contains( child ) );
 		session.clear();
 
 		child = ( Child ) session.createCriteria( Child.class, child.getName() )
 				.setCacheable( true )
 				.add( Restrictions.idEq( "c1" ) )
 				.uniqueResult();
 		assertTrue( child.getParent().getChildren().contains( child ) );
 		assertTrue( child.getParent().getChildren().contains( otherChild ) );
 		session.clear();
 
 		child = ( Child ) session.createCriteria( Child.class, child.getName() )
 				.setCacheable( true )
 				.add( Restrictions.idEq( "c1" ) )
 				.uniqueResult();
 		assertTrue( child.getParent().getChildren().contains( child ) );
 		assertTrue( child.getParent().getChildren().contains( otherChild ) );
 		session.clear();
 
 		child = ( Child ) session.createQuery( "from Child where name = 'c1'" )
 				.setCacheable( true )
 				.uniqueResult();
 		assertTrue( child.getParent().getChildren().contains( child ) );
 
 		child = ( Child ) session.createQuery( "from Child where name = 'c1'" )
 				.setCacheable( true )
 				.uniqueResult();
 		assertTrue( child.getParent().getChildren().contains( child ) );
 
 		session.delete( child.getParent() );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testLoadChildCheckParentContainsChildNoCache() {
 		Parent parent = new Parent( "p1" );
 		Child child = new Child( "c1" );
 		parent.getChildren().add( child );
 		child.setParent( parent );
 		Child otherChild = new Child( "c2" );
 		parent.getChildren().add( otherChild );
 		otherChild.setParent( parent );
 
 		Session session = openSession();
 		session.beginTransaction();
 		session.save( parent );
 		session.getTransaction().commit();
 
 		session = openSession();
 		session.beginTransaction();
 		session.setCacheMode( CacheMode.IGNORE );
 		parent = ( Parent ) session.get( Parent.class, parent.getName() );
 		assertTrue( parent.getChildren().contains( child ) );
 		assertTrue( parent.getChildren().contains( otherChild ) );
 		session.getTransaction().commit();
 
 		session = openSession();
 		session.beginTransaction();
 		session.setCacheMode( CacheMode.IGNORE );
 
 		child = ( Child ) session.get( Child.class, child.getName() );
 		assertTrue( child.getParent().getChildren().contains( child ) );
 		session.clear();
 
 		child = ( Child ) session.createCriteria( Child.class, child.getName() )
 				.add( Restrictions.idEq( "c1" ) )
 				.uniqueResult();
 		assertTrue( child.getParent().getChildren().contains( child ) );
 		assertTrue( child.getParent().getChildren().contains( otherChild ) );
 		session.clear();
 
 		child = ( Child ) session.createQuery( "from Child where name = 'c1'" ).uniqueResult();
 		assertTrue( child.getParent().getChildren().contains( child ) );
 
 		session.delete( child.getParent() );
 		session.getTransaction().commit();
 		session.close();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/event/collection/AbstractCollectionEventTest.java b/hibernate-core/src/test/java/org/hibernate/test/event/collection/AbstractCollectionEventTest.java
index 611e24c7ba..df7dba41c3 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/event/collection/AbstractCollectionEventTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/event/collection/AbstractCollectionEventTest.java
@@ -1,806 +1,806 @@
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
 
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.Session;
 import org.hibernate.Transaction;
-import org.hibernate.collection.PersistentCollection;
-import org.hibernate.collection.PersistentSet;
+import org.hibernate.collection.spi.PersistentCollection;
+import org.hibernate.collection.internal.PersistentSet;
 import org.hibernate.event.AbstractCollectionEvent;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.test.event.collection.association.bidirectional.manytomany.ChildWithBidirectionalManyToMany;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 
 /**
  * @author Gail Badner
  */
 public abstract class AbstractCollectionEventTest extends BaseCoreFunctionalTestCase {
 	@Override
 	protected void cleanupTest() {
 		ParentWithCollection dummyParent = createParent( "dummyParent" );
 		dummyParent.newChildren( createCollection() );
 		Child dummyChild = dummyParent.addChild( "dummyChild" );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		List children = s.createCriteria( dummyChild.getClass() ).list();
 		List parents = s.createCriteria( dummyParent.getClass() ).list();
 		for ( Iterator it = parents.iterator(); it.hasNext(); ) {
 			ParentWithCollection parent = ( ParentWithCollection ) it.next();
 			parent.clearChildren();
 			s.delete( parent );
 		}
 		for ( Iterator it = children.iterator(); it.hasNext(); ) {
 			s.delete( it.next() );
 		}
 		tx.commit();
 		s.close();
 	}
 
 	public abstract ParentWithCollection createParent(String name);
 
 	public abstract Collection createCollection();
 
 	@Test
 	public void testSaveParentEmptyChildren() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithNoChildren( "parent" );
 		assertEquals( 0, parent.getChildren().size() );
 		int index = 0;
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
 		checkNumberOfResults( listeners, index );
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		tx.commit();
 		s.close();
 		assertNotNull( parent.getChildren() );
 		checkNumberOfResults( listeners, 0 );
 	}
 
 	@Test
 	public void testSaveParentOneChild() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		int index = 0;
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
 		Child child = ( Child ) parent.getChildren().iterator().next();
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 		}
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testUpdateParentNullToOneChild() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithNullChildren( "parent" );
 		listeners.clear();
 		assertNull( parent.getChildren() );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		assertNotNull( parent.getChildren() );
 		Child newChild = parent.addChild( "new" );
 		tx.commit();
 		s.close();
 		int index = 0;
 		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
 		if ( newChild instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
 			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
 		}
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testUpdateParentNoneToOneChild() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithNoChildren( "parent" );
 		listeners.clear();
 		assertEquals( 0, parent.getChildren().size() );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		Child newChild = parent.addChild( "new" );
 		tx.commit();
 		s.close();
 		int index = 0;
 		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
 		if ( newChild instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
 			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
 		}
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testUpdateParentOneToTwoChildren() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		assertEquals( 1, parent.getChildren().size() );
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		Child newChild = parent.addChild( "new2" );
 		tx.commit();
 		s.close();
 		int index = 0;
 		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
 		if ( newChild instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
 			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
 		}
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testUpdateParentOneToTwoSameChildren() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		Child child = ( Child ) parent.getChildren().iterator().next();
 		assertEquals( 1, parent.getChildren().size() );
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		if ( child instanceof Entity ) {
 			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child ).getId() );
 		}
 		parent.addChild( child );
 		tx.commit();
 		s.close();
 		int index = 0;
 		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
 		}
 		ChildWithBidirectionalManyToMany childWithManyToMany = null;
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			childWithManyToMany = ( ChildWithBidirectionalManyToMany ) child;
 			if ( ( ( PersistentCollection ) childWithManyToMany.getParents() ).wasInitialized() ) {
 				checkResult( listeners, listeners.getInitializeCollectionListener(), childWithManyToMany, index++ );
 			}
 		}
 		if ( !( parent.getChildren() instanceof PersistentSet ) ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
 		}
 		if ( childWithManyToMany != null && !( childWithManyToMany.getParents() instanceof PersistentSet ) ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), childWithManyToMany, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), childWithManyToMany, index++ );
 		}
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testUpdateParentNullToOneChildDiffCollection() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithNullChildren( "parent" );
 		listeners.clear();
 		assertNull( parent.getChildren() );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		Collection collectionOrig = parent.getChildren();
 		parent.newChildren( createCollection() );
 		Child newChild = parent.addChild( "new" );
 		tx.commit();
 		s.close();
 		int index = 0;
 		if ( ( ( PersistentCollection ) collectionOrig ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, collectionOrig, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, collectionOrig, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, collectionOrig, index++ );
 		if ( newChild instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
 			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testUpdateParentNoneToOneChildDiffCollection() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithNoChildren( "parent" );
 		listeners.clear();
 		assertEquals( 0, parent.getChildren().size() );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		Collection oldCollection = parent.getChildren();
 		parent.newChildren( createCollection() );
 		Child newChild = parent.addChild( "new" );
 		tx.commit();
 		s.close();
 		int index = 0;
 		if ( ( ( PersistentCollection ) oldCollection ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, oldCollection, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, index++ );
 		if ( newChild instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
 			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testUpdateParentOneChildDiffCollectionSameChild() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		Child child = ( Child ) parent.getChildren().iterator().next();
 		listeners.clear();
 		assertEquals( 1, parent.getChildren().size() );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		if ( child instanceof Entity ) {
 			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child).getId() );
 		}
 		Collection oldCollection = parent.getChildren();
 		parent.newChildren( createCollection() );
 		parent.addChild( child );
 		tx.commit();
 		s.close();
 		int index = 0;
 		if ( ( ( PersistentCollection ) oldCollection ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, oldCollection, index++ );
 		}
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			ChildWithBidirectionalManyToMany childWithManyToMany = ( ChildWithBidirectionalManyToMany ) child;
 			if ( ( ( PersistentCollection ) childWithManyToMany.getParents() ).wasInitialized() ) {
 				checkResult( listeners, listeners.getInitializeCollectionListener(), childWithManyToMany, index++ );
 			}
 		}
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, index++ );
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			// hmmm, the same parent was removed and re-added to the child's collection;
 			// should this be considered an update?
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testUpdateParentOneChildDiffCollectionDiffChild() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		Child oldChild = ( Child ) parent.getChildren().iterator().next();
 		listeners.clear();
 		assertEquals( 1, parent.getChildren().size() );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		if ( oldChild instanceof Entity ) {
 			oldChild = ( Child ) s.get( oldChild.getClass(), ( ( Entity ) oldChild).getId() );
 		}
 		Collection oldCollection = parent.getChildren();
 		parent.newChildren( createCollection() );
 		Child newChild = parent.addChild( "new1" );
 		tx.commit();
 		s.close();
 		int index = 0;
-		if ( ( ( PersistentCollection ) oldCollection ).wasInitialized() ) {
+		if ( ( (PersistentCollection) oldCollection ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, oldCollection, index++ );
 		}
 		if ( oldChild instanceof ChildWithBidirectionalManyToMany ) {
 			ChildWithBidirectionalManyToMany oldChildWithManyToMany = ( ChildWithBidirectionalManyToMany ) oldChild;
 			if ( ( ( PersistentCollection ) oldChildWithManyToMany.getParents() ).wasInitialized() ) {
 				checkResult( listeners, listeners.getInitializeCollectionListener(), oldChildWithManyToMany, index++ );
 			}
 		}
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, index++ );
 		if ( oldChild instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) oldChild, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) oldChild, index++ );
 			checkResult( listeners, listeners.getPreCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
 			checkResult( listeners, listeners.getPostCollectionRecreateListener(), ( ChildWithBidirectionalManyToMany ) newChild, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testUpdateParentOneChildToNoneByRemove() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		assertEquals( 1, parent.getChildren().size() );
 		Child child = ( Child ) parent.getChildren().iterator().next();
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		if ( child instanceof Entity ) {
 			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child ).getId() );
 		}
 		parent.removeChild( child );
 		tx.commit();
 		s.close();
 		int index = 0;
 		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
 		}
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			ChildWithBidirectionalManyToMany childWithManyToMany = ( ChildWithBidirectionalManyToMany ) child;
 			if ( ( ( PersistentCollection ) childWithManyToMany.getParents( ) ).wasInitialized() ) {
 				checkResult( listeners, listeners.getInitializeCollectionListener(), childWithManyToMany, index++ );
 			}
 		}
 		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 		}
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testUpdateParentOneChildToNoneByClear() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		assertEquals( 1, parent.getChildren().size() );
 		Child child = ( Child ) parent.getChildren().iterator().next();
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		if ( child instanceof Entity ) {
 			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child ).getId() );
 		}
 		parent.clearChildren();
 		tx.commit();
 		s.close();
 		int index = 0;
 		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
 		}
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			ChildWithBidirectionalManyToMany childWithManyToMany = ( ChildWithBidirectionalManyToMany ) child;
 			if ( ( ( PersistentCollection ) childWithManyToMany.getParents() ).wasInitialized() ) {
 				checkResult( listeners, listeners.getInitializeCollectionListener(), childWithManyToMany, index++ );
 			}
 		}
 		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 		}
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testUpdateParentTwoChildrenToOne() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		assertEquals( 1, parent.getChildren().size() );
 		Child oldChild = ( Child ) parent.getChildren().iterator().next();
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		parent.addChild( "new" );
 		tx.commit();
 		s.close();
 		listeners.clear();
 		s = openSession();
 		tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		if ( oldChild instanceof Entity ) {
 			oldChild = ( Child ) s.get( oldChild.getClass(), ( ( Entity ) oldChild ).getId() );
 		}
 		parent.removeChild( oldChild );
 		tx.commit();
 		s.close();
 		int index = 0;
 		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
 		}
 		if ( oldChild instanceof ChildWithBidirectionalManyToMany ) {
 			ChildWithBidirectionalManyToMany oldChildWithManyToMany = ( ChildWithBidirectionalManyToMany ) oldChild;
 			if ( ( ( PersistentCollection ) oldChildWithManyToMany.getParents() ).wasInitialized() ) {
 				checkResult( listeners, listeners.getInitializeCollectionListener(), oldChildWithManyToMany, index++ );
 			}
 		}
 		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
 		if ( oldChild instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) oldChild, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) oldChild, index++ );
 		}
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testDeleteParentWithNullChildren() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithNullChildren( "parent" );
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		s.delete( parent );
 		tx.commit();
 		s.close();
 		int index = 0;
 		checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, index++ );
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testDeleteParentWithNoChildren() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithNoChildren( "parent" );
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		s.delete( parent );
 		tx.commit();
 		s.close();
 		int index = 0;
 		checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, index++ );
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testDeleteParentAndChild() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		Child child = ( Child ) parent.getChildren().iterator().next();
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		if ( child instanceof Entity ) {
 			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child ).getId() );
 		}
 		parent.removeChild( child );
 		if ( child instanceof Entity ) {
 			s.delete( child );
 		}
 		s.delete( parent );
 		tx.commit();
 		s.close();
 		int index = 0;
 		checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, index++ );
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionRemoveListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 			checkResult( listeners, listeners.getPostCollectionRemoveListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 		}
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testMoveChildToDifferentParent() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		ParentWithCollection otherParent = createParentWithOneChild( "otherParent", "otherChild" );
 		Child child = ( Child ) parent.getChildren().iterator().next();
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		otherParent = ( ParentWithCollection ) s.get( otherParent.getClass(), otherParent.getId() );
 		if ( child instanceof Entity ) {
 			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child ).getId() );
 		}
 		parent.removeChild( child );
 		otherParent.addChild( child );
 		tx.commit();
 		s.close();
 		int index = 0;
 		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
 		}
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 		}
 		if ( ( ( PersistentCollection ) otherParent.getChildren() ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), otherParent, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPreCollectionUpdateListener(), otherParent, index++ );
 		checkResult( listeners, listeners.getPostCollectionUpdateListener(), otherParent, index++ );
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 		}
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testMoveAllChildrenToDifferentParent() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		ParentWithCollection otherParent = createParentWithOneChild( "otherParent", "otherChild" );
 		Child child = ( Child ) parent.getChildren().iterator().next();
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		otherParent = ( ParentWithCollection ) s.get( otherParent.getClass(), otherParent.getId() );
 		if ( child instanceof Entity ) {
 			child = ( Child ) s.get( child.getClass(), ( ( Entity ) child ).getId() );
 		}
 		otherParent.addAllChildren( parent.getChildren() );
 		parent.clearChildren();
 		tx.commit();
 		s.close();
 		int index = 0;
 		if ( ( ( PersistentCollection ) parent.getChildren() ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, index++ );
 		}
 		if ( ( ( PersistentCollection ) otherParent.getChildren() ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), otherParent, index++ );
 		}
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionUpdateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionUpdateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPreCollectionUpdateListener(), otherParent, index++ );
 		checkResult( listeners, listeners.getPostCollectionUpdateListener(), otherParent, index++ );
 		if ( child instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) child, index++ );
 		}
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testMoveCollectionToDifferentParent() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		ParentWithCollection otherParent = createParentWithOneChild( "otherParent", "otherChild" );
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		otherParent = ( ParentWithCollection ) s.get( otherParent.getClass(), otherParent.getId() );
 		Collection otherCollectionOrig = otherParent.getChildren();
 		otherParent.newChildren( parent.getChildren() );
 		parent.newChildren( null );
 		tx.commit();
 		s.close();
 		int index = 0;
 		Child otherChildOrig = null;
 		if ( ( ( PersistentCollection ) otherCollectionOrig ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), otherParent, otherCollectionOrig, index++ );
 			otherChildOrig = ( Child ) otherCollectionOrig.iterator().next();
 			if ( otherChildOrig instanceof ChildWithBidirectionalManyToMany ) {
 				checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) otherChildOrig, index++ );
 			}
 		}
 		checkResult( listeners, listeners.getInitializeCollectionListener(), parent, otherParent.getChildren(), index++ );
 		Child otherChild = ( Child ) otherParent.getChildren().iterator().next();
 		if ( otherChild instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) otherChild, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, otherParent.getChildren(), index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, otherParent.getChildren(), index++ );
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), otherParent, otherCollectionOrig, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), otherParent, otherCollectionOrig, index++ );
 		if ( otherChild instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherChildOrig, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherChildOrig, index++ );
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherChild, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherChild, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), otherParent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), otherParent, index++ );
 		// there should also be pre- and post-recreate collection events for parent, but thats broken now;
 		// this is covered in BrokenCollectionEventTest
 		checkNumberOfResults( listeners, index );
 	}
 
 	@Test
 	public void testMoveCollectionToDifferentParentFlushMoveToDifferentParent() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		ParentWithCollection otherParent = createParentWithOneChild( "otherParent", "otherChild" );
 		ParentWithCollection otherOtherParent = createParentWithNoChildren( "otherParent" );
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		otherParent = ( ParentWithCollection ) s.get( otherParent.getClass(), otherParent.getId() );
 		otherOtherParent = ( ParentWithCollection ) s.get( otherOtherParent.getClass(), otherOtherParent.getId() );
 		Collection otherCollectionOrig = otherParent.getChildren();
 		Collection otherOtherCollectionOrig = otherOtherParent.getChildren();
 		otherParent.newChildren( parent.getChildren() );
 		parent.newChildren( null );
 		s.flush();
 		otherOtherParent.newChildren( otherParent.getChildren() );
 		otherParent.newChildren( null );
 		tx.commit();
 		s.close();
 		int index = 0;
 		Child otherChildOrig = null;
 		if ( ( ( PersistentCollection ) otherCollectionOrig ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), otherParent, otherCollectionOrig, index++ );
 			otherChildOrig = ( Child ) otherCollectionOrig.iterator().next();
 			if ( otherChildOrig instanceof ChildWithBidirectionalManyToMany ) {
 				checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) otherChildOrig, index++ );
 			}
 		}
 		checkResult( listeners, listeners.getInitializeCollectionListener(), parent, otherOtherParent.getChildren(), index++ );
 		Child otherOtherChild = ( Child ) otherOtherParent.getChildren().iterator().next();
 		if ( otherOtherChild instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), ( ChildWithBidirectionalManyToMany ) otherOtherChild, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, otherOtherParent.getChildren(), index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, otherOtherParent.getChildren(), index++ );
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), otherParent, otherCollectionOrig, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), otherParent, otherCollectionOrig, index++ );
 		if ( otherOtherChild instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherChildOrig, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherChildOrig, index++ );
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherOtherChild, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherOtherChild, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), otherParent, otherOtherParent.getChildren(), index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), otherParent, otherOtherParent.getChildren(), index++ );
 		if ( ( ( PersistentCollection ) otherOtherCollectionOrig ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), otherOtherParent, otherOtherCollectionOrig, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), otherParent, otherOtherParent.getChildren(), index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), otherParent, otherOtherParent.getChildren(), index++ );
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), otherOtherParent, otherOtherCollectionOrig, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), otherOtherParent, otherOtherCollectionOrig, index++ );
 		if ( otherOtherChild instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherOtherChild, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) otherOtherChild, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), otherOtherParent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), otherOtherParent, index++ );
 		// there should also be pre- and post-recreate collection events for parent, and otherParent
 		// but thats broken now; this is covered in BrokenCollectionEventTest
 		checkNumberOfResults( listeners, index );
 	}
 
 	protected ParentWithCollection createParentWithNullChildren(String parentName) {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		ParentWithCollection parent = createParent( parentName );
 		s.save( parent );
 		tx.commit();
 		s.close();
 		return parent;
 	}
 
 	protected ParentWithCollection createParentWithNoChildren(String parentName) {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		ParentWithCollection parent = createParent( parentName );
 		parent.newChildren( createCollection() );
 		s.save( parent );
 		tx.commit();
 		s.close();
 		return parent;
 	}
 
 	protected ParentWithCollection createParentWithOneChild(String parentName, String ChildName) {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		ParentWithCollection parent = createParent( parentName );
 		parent.newChildren( createCollection() );
 		parent.addChild( ChildName );
 		s.save( parent );
 		tx.commit();
 		s.close();
 		return parent;
 	}
 
 	protected void checkResult(CollectionListeners listeners,
 							 CollectionListeners.Listener listenerExpected,
 							 ParentWithCollection parent,
 							 int index) {
 		checkResult( listeners, listenerExpected, parent, parent.getChildren(), index );
 	}
 	protected void checkResult(CollectionListeners listeners,
 							 CollectionListeners.Listener listenerExpected,
 							 ChildWithBidirectionalManyToMany child,
 							 int index) {
 		checkResult( listeners, listenerExpected, child, child.getParents(), index );
 	}
 
 	protected void checkResult(CollectionListeners listeners,
 							 CollectionListeners.Listener listenerExpected,
 							 Entity ownerExpected,
 							 Collection collExpected,
 							 int index) {
 		assertSame( listenerExpected, listeners.getListenersCalled().get( index ) );
 		assertSame(
 				ownerExpected,
 				( ( AbstractCollectionEvent ) listeners.getEvents().get( index ) ).getAffectedOwnerOrNull()
 		);
 		assertEquals(
 				ownerExpected.getId(),
 				( ( AbstractCollectionEvent ) listeners.getEvents().get( index ) ).getAffectedOwnerIdOrNull()
 		);
 		assertEquals(
 				ownerExpected.getClass().getName(),
 				( ( AbstractCollectionEvent ) listeners.getEvents().get( index ) ).getAffectedOwnerEntityName()
 		);
 		assertSame(
 				collExpected, ( ( AbstractCollectionEvent ) listeners.getEvents().get( index ) ).getCollection()
 		);
 	}
 
 	protected void checkNumberOfResults(CollectionListeners listeners, int nEventsExpected) {
 		assertEquals( nEventsExpected, listeners.getListenersCalled().size() );
 		assertEquals( nEventsExpected, listeners.getEvents().size() );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/event/collection/BrokenCollectionEventTest.java b/hibernate-core/src/test/java/org/hibernate/test/event/collection/BrokenCollectionEventTest.java
index f9d886a952..2aa7df5dcc 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/event/collection/BrokenCollectionEventTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/event/collection/BrokenCollectionEventTest.java
@@ -1,338 +1,338 @@
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
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.Session;
 import org.hibernate.Transaction;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.event.AbstractCollectionEvent;
 
 import org.junit.Test;
 
 import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.test.event.collection.association.bidirectional.manytomany.ChildWithBidirectionalManyToMany;
 import org.hibernate.test.event.collection.association.unidirectional.ParentWithCollectionOfEntities;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 
 /**
  * These tests are known to fail. When the functionality is corrected, the
  * corresponding method will be moved into AbstractCollectionEventTest.
  *
  * @author Gail Badner
  */
 public class BrokenCollectionEventTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "event/collection/association/unidirectional/onetomany/UnidirectionalOneToManySetMapping.hbm.xml" };
 	}
 
 	@Override
 	protected void cleanupTest() {
 		ParentWithCollection dummyParent = createParent( "dummyParent" );
 		dummyParent.setChildren( createCollection() );
 		Child dummyChild = dummyParent.addChild( "dummyChild" );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		List children = s.createCriteria( dummyChild.getClass() ).list();
 		List parents = s.createCriteria( dummyParent.getClass() ).list();
 		for ( Iterator it = parents.iterator(); it.hasNext(); ) {
 			ParentWithCollection parent = ( ParentWithCollection ) it.next();
 			parent.clearChildren();
 			s.delete( parent );
 		}
 		for ( Iterator it = children.iterator(); it.hasNext(); ) {
 			s.delete( it.next() );
 		}
 		tx.commit();
 		s.close();
 	}
 
 	public ParentWithCollection createParent(String name) {
 		return new ParentWithCollectionOfEntities( name );
 	}
 
 	public Collection createCollection() {
 		return new HashSet();
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "unknown" )
 	public void testUpdateDetachedParentNoChildrenToNull() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithNoChildren( "parent" );
 		listeners.clear();
 		assertEquals( 0, parent.getChildren().size() );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Collection oldCollection = parent.getChildren();
 		parent.newChildren( null );
 		s.update( parent );
 		tx.commit();
 		s.close();
 		int index = 0;
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, index++ );
 		// pre- and post- collection recreate events should be created when updating an entity with a "null" collection
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
 		checkNumberOfResults( listeners, index );
 	}
 
 	// The following fails for the same reason as testUpdateDetachedParentNoChildrenToNullFailureExpected
 	// When that issue is fixed, this one should also be fixed and moved into AbstractCollectionEventTest.
 	/*
 	public void testUpdateDetachedParentOneChildToNullFailureExpected() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		Child oldChild = ( Child ) parent.getChildren().iterator().next();
 		assertEquals( 1, parent.getChildren().size() );
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Collection oldCollection = parent.getChildren();
 		parent.newChildren( null );
 		s.update( parent );
 		tx.commit();
 		s.close();
 		int index = 0;
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, index++ );
 		if ( oldChild instanceof ChildWithBidirectionalManyToMany ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) oldChild, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), ( ChildWithBidirectionalManyToMany ) oldChild, index++ );
 		}
 		// pre- and post- collection recreate events should be created when updating an entity with a "null" collection
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
 		checkNumberOfResults( listeners, index );
 	}
 	*/
 
 	@Test
 	@FailureExpected( jiraKey = "unknown" )
 	public void testSaveParentNullChildren() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithNullChildren( "parent" );
 		assertNull( parent.getChildren() );
 		int index = 0;
 		// pre- and post- collection recreate events should be created when creating an entity with a "null" collection
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
 		checkNumberOfResults( listeners, index );
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		tx.commit();
 		s.close();
 		assertNotNull( parent.getChildren() );
 		checkNumberOfResults( listeners, 0 );
 	}
 
 	@Test
 	@FailureExpected( jiraKey = "unknown" )
 	public void testUpdateParentNoChildrenToNull() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithNoChildren( "parent" );
 		listeners.clear();
 		assertEquals( 0, parent.getChildren().size() );
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( ParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		Collection oldCollection = parent.getChildren();
 		parent.newChildren( null );
 		tx.commit();
 		s.close();
 		int index = 0;
-		if ( ( ( PersistentCollection ) oldCollection ).wasInitialized() ) {
+		if ( ( (PersistentCollection) oldCollection ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, oldCollection, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, index++ );
 		// pre- and post- collection recreate events should be created when updating an entity with a "null" collection
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
 		checkNumberOfResults( listeners, index );
 	}
 
 
 	// The following two tests fail for the same reason as testUpdateParentNoChildrenToNullFailureExpected
 	// When that issue is fixed, this one should also be fixed and moved into AbstractCollectionEventTest.
 	/*
 	public void testUpdateParentOneChildToNullFailureExpected() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		Child oldChild = ( Child ) parent.getChildren().iterator().next();
 		assertEquals( 1, parent.getChildren().size() );
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( AbstractParentWithCollection ) s.get( parent.getClass(), parent.getId() );
 		if ( oldChild instanceof ChildEntity ) {
 			oldChild = ( Child ) s.get( oldChild.getClass(), ( ( ChildEntity ) oldChild ).getId() );
 		}
 		Collection oldCollection = parent.getChildren();
 		parent.newChildren( null );
 		tx.commit();
 		s.close();
 		int index = 0;
 		if ( ( ( PersistentCollection ) oldCollection ).wasInitialized() ) {
 			checkResult( listeners, listeners.getInitializeCollectionListener(), parent, oldCollection, index++ );
 		}
 		ChildWithBidirectionalManyToMany oldChildWithManyToMany = null;
 		if ( oldChild instanceof ChildWithBidirectionalManyToMany ) {
 			oldChildWithManyToMany = ( ChildWithBidirectionalManyToMany ) oldChild;
 			if ( ( ( PersistentCollection ) oldChildWithManyToMany.getParents() ).wasInitialized() ) {
 				checkResult( listeners, listeners.getInitializeCollectionListener(), oldChildWithManyToMany, index++ );
 			}
 		}
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, index++ );
 		if ( oldChildWithManyToMany != null ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), oldChildWithManyToMany, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), oldChildWithManyToMany, index++ );
 		}
 		// pre- and post- collection recreate events should be created when updating an entity with a "null" collection
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
 		checkNumberOfResults( listeners, index );
 	}
 
 	public void testUpdateMergedParentOneChildToNullFailureExpected() {
 		CollectionListeners listeners = new CollectionListeners( sessionFactory() );
 		ParentWithCollection parent = createParentWithOneChild( "parent", "child" );
 		assertEquals( 1, parent.getChildren().size() );
 		listeners.clear();
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		parent = ( AbstractParentWithCollection ) s.merge( parent );
 		Collection oldCollection = parent.getChildren();
 		parent.newChildren( null );
 		tx.commit();
 		s.close();
 		int index = 0;
 		Child oldChild = ( Child ) oldCollection.iterator().next();
 		ChildWithBidirectionalManyToMany oldChildWithManyToMany = null;
 		if ( oldChild instanceof ChildWithBidirectionalManyToMany ) {
 			oldChildWithManyToMany = ( ChildWithBidirectionalManyToMany ) oldChild;
 			if ( ( ( PersistentCollection ) oldChildWithManyToMany.getParents() ).wasInitialized() ) {
 		}
 			checkResult( listeners, listeners.getInitializeCollectionListener(), oldChildWithManyToMany, index++ );
 		}
 		checkResult( listeners, listeners.getPreCollectionRemoveListener(), parent, oldCollection, index++ );
 		checkResult( listeners, listeners.getPostCollectionRemoveListener(), parent, oldCollection, index++ );
 		if ( oldChildWithManyToMany != null ) {
 			checkResult( listeners, listeners.getPreCollectionUpdateListener(), oldChildWithManyToMany, index++ );
 			checkResult( listeners, listeners.getPostCollectionUpdateListener(), oldChildWithManyToMany, index++ );
 		}
 		// pre- and post- collection recreate events should be created when updating an entity with a "null" collection
 		checkResult( listeners, listeners.getPreCollectionRecreateListener(), parent, index++ );
 		checkResult( listeners, listeners.getPostCollectionRecreateListener(), parent, index++ );
 		checkNumberOfResults( listeners, index );
 	}	
 	*/
 
 	private ParentWithCollection createParentWithNullChildren(String parentName) {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		ParentWithCollection parent = createParent( parentName );
 		s.save( parent );
 		tx.commit();
 		s.close();
 		return parent;
 	}
 
 	private ParentWithCollection createParentWithNoChildren(String parentName) {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		ParentWithCollection parent = createParent( parentName );
 		parent.setChildren( createCollection() );
 		s.save( parent );
 		tx.commit();
 		s.close();
 		return parent;
 	}
 
 	private ParentWithCollection createParentWithOneChild(String parentName, String ChildName) {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		ParentWithCollection parent = createParent( parentName );
 		parent.setChildren( createCollection() );
 		parent.addChild( ChildName );
 		s.save( parent );
 		tx.commit();
 		s.close();
 		return parent;
 	}
 
 	protected void checkResult(CollectionListeners listeners,
 							 CollectionListeners.Listener listenerExpected,
 							 ParentWithCollection parent,
 							 int index) {
 		checkResult( listeners, listenerExpected, parent, parent.getChildren(), index );
 	}
 	protected void checkResult(CollectionListeners listeners,
 							 CollectionListeners.Listener listenerExpected,
 							 ChildWithBidirectionalManyToMany child,
 							 int index) {
 		checkResult( listeners, listenerExpected, child, child.getParents(), index );
 	}
 
 	protected void checkResult(CollectionListeners listeners,
 							 CollectionListeners.Listener listenerExpected,
 							 Entity ownerExpected,
 							 Collection collExpected,
 							 int index) {
 		assertSame( listenerExpected, listeners.getListenersCalled().get( index ) );
 		assertSame(
 				ownerExpected,
 				( ( AbstractCollectionEvent ) listeners.getEvents().get( index ) ).getAffectedOwnerOrNull()
 		);
 		assertEquals(
 				ownerExpected.getId(),
 				( ( AbstractCollectionEvent ) listeners.getEvents().get( index ) ).getAffectedOwnerIdOrNull()
 		);
 		assertEquals(
 				ownerExpected.getClass().getName(),
 				( ( AbstractCollectionEvent ) listeners.getEvents().get( index ) ).getAffectedOwnerEntityName()
 		);
 		assertSame(
 				collExpected, ( ( AbstractCollectionEvent ) listeners.getEvents().get( index ) ).getCollection()
 		);
 	}
 
 	private void checkNumberOfResults(CollectionListeners listeners, int nEventsExpected) {
 		assertEquals( nEventsExpected, listeners.getListenersCalled().size() );
 		assertEquals( nEventsExpected, listeners.getEvents().size() );
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/MyListType.java b/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/MyListType.java
index 383ace141b..4bb1087775 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/MyListType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/MyListType.java
@@ -1,58 +1,58 @@
 package org.hibernate.test.usercollection.basic;
 import java.util.Iterator;
 import java.util.Map;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.usertype.UserCollectionType;
 
 public class MyListType implements UserCollectionType {
 
 	static int lastInstantiationRequest = -2;
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister) throws HibernateException {
 		return new PersistentMyList(session);
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		if ( session.getEntityMode()==EntityMode.DOM4J ) {
 			throw new IllegalStateException("dom4j not supported");
 		}
 		else {
 			return new PersistentMyList( session, (IMyList) collection );
 		}
 	}
 
 	public Iterator getElementsIterator(Object collection) {
 		return ( (IMyList) collection ).iterator();
 	}
 
 	public boolean contains(Object collection, Object entity) {
 		return ( (IMyList) collection ).contains(entity);
 	}
 
 	public Object indexOf(Object collection, Object entity) {
 		int l = ( (IMyList) collection ).indexOf(entity);
 		if(l<0) {
 			return null;
 		} else {
 			return new Integer(l);
 		}
 	}
 
 	public Object replaceElements(Object original, Object target, CollectionPersister persister, Object owner, Map copyCache, SessionImplementor session) throws HibernateException {
 		IMyList result = (IMyList) target;
 		result.clear();
 		result.addAll((MyList)original);
 		return result;
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		lastInstantiationRequest = anticipatedSize;
 		return new MyList();
 	}
 
 	
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/PersistentMyList.java b/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/PersistentMyList.java
index c671099c24..c8afed7d91 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/PersistentMyList.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/usercollection/basic/PersistentMyList.java
@@ -1,17 +1,17 @@
 package org.hibernate.test.usercollection.basic;
-import org.hibernate.collection.PersistentList;
+import org.hibernate.collection.internal.PersistentList;
 import org.hibernate.engine.SessionImplementor;
 
 public class PersistentMyList extends PersistentList implements IMyList {
 
 	public PersistentMyList(SessionImplementor session) {
 		super(session);
 	}
 
 	public PersistentMyList(SessionImplementor session, IMyList list) {
 		super(session, list);
 	}
 
 	
 	
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/DefaultableListType.java b/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/DefaultableListType.java
index 394728499b..1950c2ddb2 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/DefaultableListType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/DefaultableListType.java
@@ -1,73 +1,73 @@
 package org.hibernate.test.usercollection.parameterized;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import org.hibernate.EntityMode;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.usertype.ParameterizedType;
 import org.hibernate.usertype.UserCollectionType;
-
-/**
- * Our Hibernate type-system extension for defining our specialized collection
- * contract.
- *
- * @author Holger Brands
- * @author Steve Ebersole
- */
-public class DefaultableListType implements UserCollectionType, ParameterizedType {
-    private String defaultValue;
-
-	public Object instantiate(int anticipatedSize) {
-		DefaultableListImpl list = anticipatedSize < 0 ? new DefaultableListImpl() : new DefaultableListImpl( anticipatedSize );
-		list.setDefaultValue( defaultValue );
-		return list;
-	}
-
-	public PersistentCollection instantiate(
-			SessionImplementor session,
-			CollectionPersister persister) {
-		return new PersistentDefaultableList( session );
-	}
-
-	public PersistentCollection wrap(SessionImplementor session, Object collection) {
-		if ( session.getEntityMode() == EntityMode.DOM4J ) {
-			throw new IllegalStateException( "dom4j not supported" );
-		}
-		else {
-			return new PersistentDefaultableList( session, ( List ) collection );
-		}
-	}
-
-	public Iterator getElementsIterator(Object collection) {
-		return ( ( DefaultableList ) collection ).iterator();
-	}
-
-	public boolean contains(Object collection, Object entity) {
-		return ( ( DefaultableList ) collection ).contains( entity );
-	}
-
-	public Object indexOf(Object collection, Object entity) {
-		int index = ( ( DefaultableList ) collection ).indexOf( entity );
-		return index >= 0 ? new Integer( index ) : null;
-	}
-
-	public Object replaceElements(
-			Object original,
-			Object target,
-			CollectionPersister persister,
-			Object owner,
-			Map copyCache,
-			SessionImplementor session) {
-		DefaultableList result = ( DefaultableList ) target;
-		result.clear();
-		result.addAll( ( DefaultableList ) original );
-		return result;
-	}
-
-	public void setParameterValues(Properties parameters) {
-        defaultValue = parameters.getProperty( "default" );
-	}
-}
+
+/**
+ * Our Hibernate type-system extension for defining our specialized collection
+ * contract.
+ *
+ * @author Holger Brands
+ * @author Steve Ebersole
+ */
+public class DefaultableListType implements UserCollectionType, ParameterizedType {
+    private String defaultValue;
+
+	public Object instantiate(int anticipatedSize) {
+		DefaultableListImpl list = anticipatedSize < 0 ? new DefaultableListImpl() : new DefaultableListImpl( anticipatedSize );
+		list.setDefaultValue( defaultValue );
+		return list;
+	}
+
+	public PersistentCollection instantiate(
+			SessionImplementor session,
+			CollectionPersister persister) {
+		return new PersistentDefaultableList( session );
+	}
+
+	public PersistentCollection wrap(SessionImplementor session, Object collection) {
+		if ( session.getEntityMode() == EntityMode.DOM4J ) {
+			throw new IllegalStateException( "dom4j not supported" );
+		}
+		else {
+			return new PersistentDefaultableList( session, ( List ) collection );
+		}
+	}
+
+	public Iterator getElementsIterator(Object collection) {
+		return ( ( DefaultableList ) collection ).iterator();
+	}
+
+	public boolean contains(Object collection, Object entity) {
+		return ( ( DefaultableList ) collection ).contains( entity );
+	}
+
+	public Object indexOf(Object collection, Object entity) {
+		int index = ( ( DefaultableList ) collection ).indexOf( entity );
+		return index >= 0 ? new Integer( index ) : null;
+	}
+
+	public Object replaceElements(
+			Object original,
+			Object target,
+			CollectionPersister persister,
+			Object owner,
+			Map copyCache,
+			SessionImplementor session) {
+		DefaultableList result = ( DefaultableList ) target;
+		result.clear();
+		result.addAll( ( DefaultableList ) original );
+		return result;
+	}
+
+	public void setParameterValues(Properties parameters) {
+        defaultValue = parameters.getProperty( "default" );
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/PersistentDefaultableList.java b/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/PersistentDefaultableList.java
index bddabd4268..8141458685 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/PersistentDefaultableList.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/usercollection/parameterized/PersistentDefaultableList.java
@@ -1,27 +1,27 @@
 package org.hibernate.test.usercollection.parameterized;
 import java.util.List;
-import org.hibernate.collection.PersistentList;
+import org.hibernate.collection.internal.PersistentList;
 import org.hibernate.engine.SessionImplementor;
-
-/**
- * The "persistent wrapper" around our specialized collection contract
- *
- * @author Holger Brands
- * @author Steve Ebersole
- */
-public class PersistentDefaultableList extends PersistentList implements DefaultableList {
-	public PersistentDefaultableList(SessionImplementor session) {
-		super( session );
-	}
-
-	public PersistentDefaultableList(SessionImplementor session, List list) {
-		super( session, list );
-	}
-
-	public PersistentDefaultableList() {
-	}
-
-	public String getDefaultValue() {
-		return ( ( DefaultableList ) this.list ).getDefaultValue();
-	}
-}
+
+/**
+ * The "persistent wrapper" around our specialized collection contract
+ *
+ * @author Holger Brands
+ * @author Steve Ebersole
+ */
+public class PersistentDefaultableList extends PersistentList implements DefaultableList {
+	public PersistentDefaultableList(SessionImplementor session) {
+		super( session );
+	}
+
+	public PersistentDefaultableList(SessionImplementor session, List list) {
+		super( session, list );
+	}
+
+	public PersistentDefaultableList() {
+	}
+
+	public String getDefaultValue() {
+		return ( ( DefaultableList ) this.list ).getDefaultValue();
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/PersistenceUtilHelper.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/PersistenceUtilHelper.java
index 2c7fa86d0d..26f0175355 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/PersistenceUtilHelper.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/PersistenceUtilHelper.java
@@ -1,236 +1,236 @@
 package org.hibernate.ejb.util;
 import java.io.Serializable;
 import java.lang.reflect.AccessibleObject;
 import java.lang.reflect.Field;
 import java.lang.reflect.InvocationTargetException;
 import java.lang.reflect.Member;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.WeakHashMap;
 import javax.persistence.PersistenceException;
 import javax.persistence.spi.LoadState;
 import org.hibernate.AssertionFailure;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 /**
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 public class PersistenceUtilHelper {
 	public static LoadState isLoadedWithoutReference(Object proxy, String property, MetadataCache cache) {
 		Object entity;
 		boolean sureFromUs = false;
 		if ( proxy instanceof HibernateProxy ) {
 			LazyInitializer li = ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer();
 			if ( li.isUninitialized() ) {
 				return LoadState.NOT_LOADED;
 			}
 			else {
 				entity = li.getImplementation();
 			}
 			sureFromUs = true;
 		}
 		else {
 			entity = proxy;
 		}
 
 		//we are instrumenting but we can't assume we are the only ones
 		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
 			FieldInterceptor interceptor = FieldInterceptionHelper.extractFieldInterceptor( entity );
 			final boolean isInitialized = interceptor == null || interceptor.isInitialized( property );
 			LoadState state;
 			if (isInitialized && interceptor != null) {
 				//property is loaded according to bytecode enhancement, but is it loaded as far as association?
 				//it's ours, we can read
 				state = isLoaded( get( entity, property, cache ) );
 				//it's ours so we know it's loaded
 				if (state == LoadState.UNKNOWN) state = LoadState.LOADED;
 			}
 			else if ( interceptor != null && (! isInitialized)) {
 				state = LoadState.NOT_LOADED;
 			}
 			else if ( sureFromUs ) { //interceptor == null
 				//property is loaded according to bytecode enhancement, but is it loaded as far as association?
 				//it's ours, we can read
 				state = isLoaded( get( entity, property, cache ) );
 				//it's ours so we know it's loaded
 				if (state == LoadState.UNKNOWN) state = LoadState.LOADED;
 			}
 			else {
 				state = LoadState.UNKNOWN;
 			}
 
 			return state;
 		}
 		else {
 			//can't do sureFromUs ? LoadState.LOADED : LoadState.UNKNOWN;
 			//is that an association?
 			return LoadState.UNKNOWN;
 		}
 	}
 
 	public static LoadState isLoadedWithReference(Object proxy, String property, MetadataCache cache) {
 		//for sure we don't instrument and for sure it's not a lazy proxy
 		Object object = get(proxy, property, cache);
 		return isLoaded( object );
 	}
 
 	private static Object get(Object proxy, String property, MetadataCache cache) {
 		final Class<?> clazz = proxy.getClass();
 
 		try {
 			Member member = cache.getMember( clazz, property );
 			if (member instanceof Field) {
 				return ( (Field) member ).get( proxy );
 			}
 			else if (member instanceof Method) {
 				return ( (Method) member ).invoke( proxy );
 			}
 			else {
 				throw new AssertionFailure( "Member object neither Field nor Method: " + member);
 			}
 		}
 		catch ( IllegalAccessException e ) {
 			throw new PersistenceException( "Unable to access field or method: "
 							+ clazz + "#"
 							+ property, e);
 		}
 		catch ( InvocationTargetException e ) {
 			throw new PersistenceException( "Unable to access field or method: "
 							+ clazz + "#"
 							+ property, e);
 		}
 	}
 
 	private static void setAccessibility(Member member) {
 		//Sun's ease of use, sigh...
 		( ( AccessibleObject ) member ).setAccessible( true );
 	}
 
 	public static LoadState isLoaded(Object o) {
 		if ( o instanceof HibernateProxy ) {
 			final boolean isInitialized = !( ( HibernateProxy ) o ).getHibernateLazyInitializer().isUninitialized();
 			return isInitialized ? LoadState.LOADED : LoadState.NOT_LOADED;
 		}
 		else if ( o instanceof PersistentCollection ) {
-			final boolean isInitialized = ( ( PersistentCollection ) o ).wasInitialized();
+			final boolean isInitialized = ( (PersistentCollection) o ).wasInitialized();
 			return isInitialized ? LoadState.LOADED : LoadState.NOT_LOADED;
 		}
 		else {
 			return LoadState.UNKNOWN;
 		}
 	}
 
 	/**
 	 * Returns the method with the specified name or <code>null</code> if it does not exist.
 	 *
 	 * @param clazz The class to check.
 	 * @param methodName The method name.
 	 *
 	 * @return Returns the method with the specified name or <code>null</code> if it does not exist.
 	 */
 	private static Method getMethod(Class<?> clazz, String methodName) {
 		try {
 			char string[] = methodName.toCharArray();
 			string[0] = Character.toUpperCase( string[0] );
 			methodName = new String( string );
 			try {
 				return clazz.getDeclaredMethod( "get" + methodName );
 			}
 			catch ( NoSuchMethodException e ) {
 				return clazz.getDeclaredMethod( "is" + methodName );
 			}
 		}
 		catch ( NoSuchMethodException e ) {
 			return null;
 		}
 	}
 
 	/**
 	 * Cache hierarchy and member resolution in a weak hash map
 	 */
 	//TODO not really thread-safe
 	public static class MetadataCache implements Serializable {
 		private transient Map<Class<?>, ClassCache> classCache = new WeakHashMap<Class<?>, ClassCache>();
 
 
 		private void readObject(java.io.ObjectInputStream stream) {
 			classCache = new WeakHashMap<Class<?>, ClassCache>();
 		}
 
 		Member getMember(Class<?> clazz, String property) {
 			ClassCache cache = classCache.get( clazz );
 			if (cache == null) {
 				cache = new ClassCache(clazz);
 				classCache.put( clazz, cache );
 			}
 			Member member = cache.members.get( property );
 			if ( member == null ) {
 				member = findMember( clazz, property );
 				cache.members.put( property, member );
 			}
 			return member;
 		}
 
 		private Member findMember(Class<?> clazz, String property) {
 			final List<Class<?>> classes = getClassHierarchy( clazz );
 
 			for (Class current : classes) {
 				final Field field;
 				try {
 					field = current.getDeclaredField( property );
 					setAccessibility( field );
 					return field;
 				}
 				catch ( NoSuchFieldException e ) {
 					final Method method = getMethod( current, property );
 					if (method != null) {
 						setAccessibility( method );
 						return method;
 					}
 				}
 			}
 			//we could not find any match
 			throw new PersistenceException( "Unable to find field or method: "
 							+ clazz + "#"
 							+ property);
 		}
 
 		private List<Class<?>> getClassHierarchy(Class<?> clazz) {
 			ClassCache cache = classCache.get( clazz );
 			if (cache == null) {
 				cache = new ClassCache(clazz);
 				classCache.put( clazz, cache );
 			}
 			return cache.classHierarchy;
 		}
 
 		private static List<Class<?>> findClassHierarchy(Class<?> clazz) {
 			List<Class<?>> classes = new ArrayList<Class<?>>();
 			Class<?> current = clazz;
 			do {
 				classes.add( current );
 				current = current.getSuperclass();
 			}
 			while ( current != null );
 			return classes;
 		}
 
 		private static class ClassCache {
 			List<Class<?>> classHierarchy;
 			Map<String, Member> members = new HashMap<String, Member>();
 
 			public ClassCache(Class<?> clazz) {
 				classHierarchy = findClassHierarchy( clazz );
 			}
 		}
 	}
 
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/ComponentPropertyMapper.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/ComponentPropertyMapper.java
index 09b0363c00..526779df41 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/ComponentPropertyMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/ComponentPropertyMapper.java
@@ -1,107 +1,107 @@
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
  */
 package org.hibernate.envers.entities.mapper;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.envers.entities.PropertyData;
 import org.hibernate.envers.exception.AuditException;
 import org.hibernate.envers.reader.AuditReaderImplementor;
 import org.hibernate.envers.tools.reflection.ReflectionTools;
 import org.hibernate.property.Setter;
 import org.hibernate.internal.util.ReflectHelper;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public class ComponentPropertyMapper implements PropertyMapper, CompositeMapperBuilder {
     private final PropertyData propertyData;
     private final MultiPropertyMapper delegate;
 	private final String componentClassName;
 
     public ComponentPropertyMapper(PropertyData propertyData, String componentClassName) {
         this.propertyData = propertyData;
         this.delegate = new MultiPropertyMapper();
 		this.componentClassName = componentClassName;
     }
 
 	public void add(PropertyData propertyData) {
         delegate.add(propertyData);
     }
 
     public CompositeMapperBuilder addComponent(PropertyData propertyData, String componentClassName) {
         return delegate.addComponent(propertyData, componentClassName);
     }
 
     public void addComposite(PropertyData propertyData, PropertyMapper propertyMapper) {
         delegate.addComposite(propertyData, propertyMapper);
     }
 
     public boolean mapToMapFromEntity(SessionImplementor session, Map<String, Object> data, Object newObj, Object oldObj) {
         return delegate.mapToMapFromEntity(session, data, newObj, oldObj);
     }
 
     public void mapToEntityFromMap(AuditConfiguration verCfg, Object obj, Map data, Object primaryKey, AuditReaderImplementor versionsReader, Number revision) {
         if (data == null || obj == null) {
             return;
         }
 
         Setter setter = ReflectionTools.getSetter(obj.getClass(), propertyData);
 
 		// If all properties are null and single, then the component has to be null also.
 		boolean allNullAndSingle = true;
 		for (Map.Entry<PropertyData, PropertyMapper> property : delegate.getProperties().entrySet()) {
 			if (data.get(property.getKey().getName()) != null || !(property.getValue() instanceof SinglePropertyMapper)) {
 				allNullAndSingle = false;
 				break;
 			}
 		}
 
 		if (allNullAndSingle) {
 			// single property, but default value need not be null, so we'll set it to null anyway 
 			setter.set(obj, null, null);			
 		} else {
 			// set the component
 			try {
 				Object subObj = ReflectHelper.getDefaultConstructor(
 						Thread.currentThread().getContextClassLoader().loadClass(componentClassName)).newInstance();
 				setter.set(obj, subObj, null);
 				delegate.mapToEntityFromMap(verCfg, subObj, data, primaryKey, versionsReader, revision);
 			} catch (Exception e) {
 				throw new AuditException(e);
 			}
 		}
     }
 
  	public List<PersistentCollectionChangeData> mapCollectionChanges(String referencingPropertyName,
                                                                                     PersistentCollection newColl,
                                                                                     Serializable oldColl,
                                                                                     Serializable id) {
         return delegate.mapCollectionChanges(referencingPropertyName, newColl, oldColl, id);
     }
 
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/MultiPropertyMapper.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/MultiPropertyMapper.java
index 5cd8fdeaaf..5a9b0d0445 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/MultiPropertyMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/MultiPropertyMapper.java
@@ -1,154 +1,154 @@
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
  */
 package org.hibernate.envers.entities.mapper;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.envers.entities.PropertyData;
 import org.hibernate.envers.reader.AuditReaderImplementor;
 import org.hibernate.envers.tools.MappingTools;
 import org.hibernate.envers.tools.Tools;
 import org.hibernate.envers.tools.reflection.ReflectionTools;
 import org.hibernate.property.Getter;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public class MultiPropertyMapper implements ExtendedPropertyMapper {
     protected final Map<PropertyData, PropertyMapper> properties;
     private final Map<String, PropertyData> propertyDatas;
 
     public MultiPropertyMapper() {
         properties = Tools.newHashMap();
         propertyDatas = Tools.newHashMap();
     }
 
     public void add(PropertyData propertyData) {
         SinglePropertyMapper single = new SinglePropertyMapper();
         single.add(propertyData);
         properties.put(propertyData, single);
         propertyDatas.put(propertyData.getName(), propertyData);
     }
 
     public CompositeMapperBuilder addComponent(PropertyData propertyData, String componentClassName) {
         if (properties.get(propertyData) != null) {
 			// This is needed for second pass to work properly in the components mapper
             return (CompositeMapperBuilder) properties.get(propertyData);
         }
 
         ComponentPropertyMapper componentMapperBuilder = new ComponentPropertyMapper(propertyData, componentClassName);
 		addComposite(propertyData, componentMapperBuilder);
 
         return componentMapperBuilder;
     }
 
     public void addComposite(PropertyData propertyData, PropertyMapper propertyMapper) {
         properties.put(propertyData, propertyMapper);
         propertyDatas.put(propertyData.getName(), propertyData);
     }
 
     private Object getAtIndexOrNull(Object[] array, int index) { return array == null ? null : array[index]; }
 
     public boolean map(SessionImplementor session, Map<String, Object> data, String[] propertyNames, Object[] newState, Object[] oldState) {
         boolean ret = false;
         for (int i=0; i<propertyNames.length; i++) {
             String propertyName = propertyNames[i];
 
             if (propertyDatas.containsKey(propertyName)) {
                 ret |= properties.get(propertyDatas.get(propertyName)).mapToMapFromEntity(session, data,
                         getAtIndexOrNull(newState, i),
                         getAtIndexOrNull(oldState, i));
             }
         }
 
         return ret;
     }
 
     public boolean mapToMapFromEntity(SessionImplementor session, Map<String, Object> data, Object newObj, Object oldObj) {
         boolean ret = false;
         for (PropertyData propertyData : properties.keySet()) {
             Getter getter;
             if (newObj != null) {
                 getter = ReflectionTools.getGetter(newObj.getClass(), propertyData);
             } else if (oldObj != null) {
                 getter = ReflectionTools.getGetter(oldObj.getClass(), propertyData);
             } else {
                 return false;
             }
 
             ret |= properties.get(propertyData).mapToMapFromEntity(session, data,
                     newObj == null ? null : getter.get(newObj),
                     oldObj == null ? null : getter.get(oldObj));
         }
 
         return ret;
     }
 
     public void mapToEntityFromMap(AuditConfiguration verCfg, Object obj, Map data, Object primaryKey,
                                    AuditReaderImplementor versionsReader, Number revision) {
         for (PropertyMapper mapper : properties.values()) {
             mapper.mapToEntityFromMap(verCfg, obj, data, primaryKey, versionsReader, revision);
         }
     }
 
     public List<PersistentCollectionChangeData> mapCollectionChanges(String referencingPropertyName,
                                                                                     PersistentCollection newColl,
                                                                                     Serializable oldColl,
                                                                                     Serializable id) {
 		// Name of the properyt, to which we will delegate the mapping.
 		String delegatePropertyName;
 
 		// Checking if the property name doesn't reference a collection in a component - then the name will containa a .
 		int dotIndex = referencingPropertyName.indexOf('.');
 		if (dotIndex != -1) {
 			// Computing the name of the component
 			String componentName = referencingPropertyName.substring(0, dotIndex);
 			// And the name of the property in the component
 			String propertyInComponentName = MappingTools.createComponentPrefix(componentName)
 					+ referencingPropertyName.substring(dotIndex+1);
 
 			// We need to get the mapper for the component.
 			referencingPropertyName = componentName;
 			// As this is a component, we delegate to the property in the component.
 			delegatePropertyName = propertyInComponentName;
 		} else {
 			// If this is not a component, we delegate to the same property.
 			delegatePropertyName = referencingPropertyName;
 		}
 
         PropertyMapper mapper = properties.get(propertyDatas.get(referencingPropertyName));
         if (mapper != null) {
             return mapper.mapCollectionChanges(delegatePropertyName, newColl, oldColl, id);
         } else {
             return null;
         }
     }
 
 	public Map<PropertyData, PropertyMapper> getProperties() {
 		return properties;
 	}
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/PropertyMapper.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/PropertyMapper.java
index 2ec824f9d8..5aa13e8153 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/PropertyMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/PropertyMapper.java
@@ -1,70 +1,70 @@
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
  */
 package org.hibernate.envers.entities.mapper;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.envers.reader.AuditReaderImplementor;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public interface PropertyMapper {
     /**
      * Maps properties to the given map, basing on differences between properties of new and old objects.
      * @param session The current session.
 	 * @param data Data to map to.
 	 * @param newObj New state of the entity.
 	 * @param oldObj Old state of the entity.
 	 * @return True if there are any differences between the states represented by newObj and oldObj.
      */
     boolean mapToMapFromEntity(SessionImplementor session, Map<String, Object> data, Object newObj, Object oldObj);
 
     /**
      * Maps properties from the given map to the given object.
      * @param verCfg Versions configuration.
      * @param obj Object to map to.
      * @param data Data to map from.
      * @param primaryKey Primary key of the object to which we map (for relations)
      * @param versionsReader VersionsReader for reading relations
      * @param revision Revision at which the object is read, for reading relations
      */
     void mapToEntityFromMap(AuditConfiguration verCfg, Object obj, Map data, Object primaryKey,
                             AuditReaderImplementor versionsReader, Number revision);
 
     /**
      * Maps collection changes
      * @param referencingPropertyName Name of the field, which holds the collection in the entity.
      * @param newColl New collection, after updates.
      * @param oldColl Old collection, before updates.
      * @param id Id of the object owning the collection.
      * @return List of changes that need to be performed on the persistent store.
      */
     List<PersistentCollectionChangeData> mapCollectionChanges(String referencingPropertyName,
                                                               PersistentCollection newColl,
                                                               Serializable oldColl, Serializable id);
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/SinglePropertyMapper.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/SinglePropertyMapper.java
index 634d99246e..3ad2bfb981 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/SinglePropertyMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/SinglePropertyMapper.java
@@ -1,106 +1,106 @@
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
  */
 package org.hibernate.envers.entities.mapper;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import org.hibernate.HibernateException;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.envers.entities.PropertyData;
 import org.hibernate.envers.exception.AuditException;
 import org.hibernate.envers.reader.AuditReaderImplementor;
 import org.hibernate.envers.tools.Tools;
 import org.hibernate.envers.tools.reflection.ReflectionTools;
 import org.hibernate.property.DirectPropertyAccessor;
 import org.hibernate.property.Setter;
 
 /**
  * TODO: diff
  * @author Adam Warski (adam at warski dot org)
  */
 public class SinglePropertyMapper implements PropertyMapper, SimpleMapperBuilder {
     private PropertyData propertyData;
 
     public SinglePropertyMapper(PropertyData propertyData) {
         this.propertyData = propertyData;
     }
 
     public SinglePropertyMapper() { }
 
     public void add(PropertyData propertyData) {
         if (this.propertyData != null) {
             throw new AuditException("Only one property can be added!");
         }
 
         this.propertyData = propertyData;
     }
 
     public boolean mapToMapFromEntity(SessionImplementor session, Map<String, Object> data, Object newObj, Object oldObj) {
         data.put(propertyData.getName(), newObj);
 
         return !Tools.objectsEqual(newObj, oldObj);
     }
 
     public void mapToEntityFromMap(AuditConfiguration verCfg, Object obj, Map data, Object primaryKey,
                                    AuditReaderImplementor versionsReader, Number revision) {
         if (data == null || obj == null) {
             return;
         }
 
         Setter setter = ReflectionTools.getSetter(obj.getClass(), propertyData);
 		Object value = data.get(propertyData.getName());
 		// We only set a null value if the field is not primite. Otherwise, we leave it intact.
 		if (value != null || !isPrimitive(setter, propertyData, obj.getClass())) {
         	setter.set(obj, value, null);
 		}
     }
 
 	private boolean isPrimitive(Setter setter, PropertyData propertyData, Class<?> cls) {
 		if (cls == null) {
 			throw new HibernateException("No field found for property: " + propertyData.getName());
 		}
 
 		if (setter instanceof DirectPropertyAccessor.DirectSetter) {
 			// In a direct setter, getMethod() returns null
 			// Trying to look up the field
 			try {
 				return cls.getDeclaredField(propertyData.getBeanName()).getType().isPrimitive();
 			} catch (NoSuchFieldException e) {
 				return isPrimitive(setter, propertyData, cls.getSuperclass());
 			}
 		} else {
 			return setter.getMethod().getParameterTypes()[0].isPrimitive();
 		}
 	}
 
     public List<PersistentCollectionChangeData> mapCollectionChanges(String referencingPropertyName,
                                                                      PersistentCollection newColl,
                                                                      Serializable oldColl,
                                                                      Serializable id) {
         return null;
     }
 
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/SubclassPropertyMapper.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/SubclassPropertyMapper.java
index bdfc87eb33..bdfaefc110 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/SubclassPropertyMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/SubclassPropertyMapper.java
@@ -1,98 +1,98 @@
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
  */
 package org.hibernate.envers.entities.mapper;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.envers.entities.PropertyData;
 import org.hibernate.envers.reader.AuditReaderImplementor;
 
 /**
  * A mapper which maps from a parent mapper and a "main" one, but adds only to the "main". The "main" mapper
  * should be the mapper of the subclass.
  * @author Adam Warski (adam at warski dot org)
  */
 public class SubclassPropertyMapper implements ExtendedPropertyMapper {
     private ExtendedPropertyMapper main;
     private ExtendedPropertyMapper parentMapper;
 
     public SubclassPropertyMapper(ExtendedPropertyMapper main, ExtendedPropertyMapper parentMapper) {
         this.main = main;
         this.parentMapper = parentMapper;
     }
 
     public boolean map(SessionImplementor session, Map<String, Object> data, String[] propertyNames, Object[] newState, Object[] oldState) {
         boolean parentDiffs = parentMapper.map(session, data, propertyNames, newState, oldState);
         boolean mainDiffs = main.map(session, data, propertyNames, newState, oldState);
 
         return parentDiffs || mainDiffs;
     }
 
     public boolean mapToMapFromEntity(SessionImplementor session, Map<String, Object> data, Object newObj, Object oldObj) {
         boolean parentDiffs = parentMapper.mapToMapFromEntity(session, data, newObj, oldObj);
         boolean mainDiffs = main.mapToMapFromEntity(session, data, newObj, oldObj);
 
         return parentDiffs || mainDiffs;
     }
 
     public void mapToEntityFromMap(AuditConfiguration verCfg, Object obj, Map data, Object primaryKey, AuditReaderImplementor versionsReader, Number revision) {
         parentMapper.mapToEntityFromMap(verCfg, obj, data, primaryKey, versionsReader, revision);
         main.mapToEntityFromMap(verCfg, obj, data, primaryKey, versionsReader, revision);
     }
 
     public List<PersistentCollectionChangeData> mapCollectionChanges(String referencingPropertyName,
-                                                                                    PersistentCollection newColl, 
+                                                                                    PersistentCollection newColl,
                                                                                     Serializable oldColl,
                                                                                     Serializable id) {
         List<PersistentCollectionChangeData> parentCollectionChanges = parentMapper.mapCollectionChanges(
                 referencingPropertyName, newColl, oldColl, id);
 
 		List<PersistentCollectionChangeData> mainCollectionChanges = main.mapCollectionChanges(
 				referencingPropertyName, newColl, oldColl, id);
 
         if (parentCollectionChanges == null) {
             return mainCollectionChanges;
         } else {
         	if(mainCollectionChanges != null) {
                 parentCollectionChanges.addAll(mainCollectionChanges);
         	}
 			return parentCollectionChanges;
         }
     }
 
     public CompositeMapperBuilder addComponent(PropertyData propertyData, String componentClassName) {
         return main.addComponent(propertyData, componentClassName);
     }
 
     public void addComposite(PropertyData propertyData, PropertyMapper propertyMapper) {
         main.addComposite(propertyData, propertyMapper);
     }
 
     public void add(PropertyData propertyData) {
         main.add(propertyData);
     }
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/AbstractCollectionMapper.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/AbstractCollectionMapper.java
index 2c59e40208..8c54595b82 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/AbstractCollectionMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/AbstractCollectionMapper.java
@@ -1,153 +1,153 @@
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
  */
 package org.hibernate.envers.entities.mapper.relation;
 import java.io.Serializable;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.envers.RevisionType;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.envers.entities.mapper.PersistentCollectionChangeData;
 import org.hibernate.envers.entities.mapper.PropertyMapper;
 import org.hibernate.envers.entities.mapper.relation.lazy.initializor.Initializor;
 import org.hibernate.envers.exception.AuditException;
 import org.hibernate.envers.reader.AuditReaderImplementor;
 import org.hibernate.envers.tools.reflection.ReflectionTools;
 import org.hibernate.property.Setter;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public abstract class AbstractCollectionMapper<T> implements PropertyMapper {
     protected final CommonCollectionMapperData commonCollectionMapperData;    
     protected final Class<? extends T> collectionClass;
 
     private final Constructor<? extends T> proxyConstructor;
 
     protected AbstractCollectionMapper(CommonCollectionMapperData commonCollectionMapperData,
                                        Class<? extends T> collectionClass, Class<? extends T> proxyClass) {
         this.commonCollectionMapperData = commonCollectionMapperData;
         this.collectionClass = collectionClass;
 
         try {
             proxyConstructor = proxyClass.getConstructor(Initializor.class);
         } catch (NoSuchMethodException e) {
             throw new AuditException(e);
         }
     }
 
     protected abstract Collection getNewCollectionContent(PersistentCollection newCollection);
     protected abstract Collection getOldCollectionContent(Serializable oldCollection);
 
     /**
      * Maps the changed collection element to the given map.
      * @param data Where to map the data.
      * @param changed The changed collection element to map.
      */
     protected abstract void mapToMapFromObject(Map<String, Object> data, Object changed);
 
     private void addCollectionChanges(List<PersistentCollectionChangeData> collectionChanges, Set<Object> changed,
                                       RevisionType revisionType, Serializable id) {
         for (Object changedObj : changed) {
             Map<String, Object> entityData = new HashMap<String, Object>();
             Map<String, Object> originalId = new HashMap<String, Object>();
             entityData.put(commonCollectionMapperData.getVerEntCfg().getOriginalIdPropName(), originalId);
 
             collectionChanges.add(new PersistentCollectionChangeData(
                     commonCollectionMapperData.getVersionsMiddleEntityName(), entityData, changedObj));
             // Mapping the collection owner's id.
             commonCollectionMapperData.getReferencingIdData().getPrefixedMapper().mapToMapFromId(originalId, id);
 
             // Mapping collection element and index (if present).
             mapToMapFromObject(originalId, changedObj);
 
             entityData.put(commonCollectionMapperData.getVerEntCfg().getRevisionTypePropName(), revisionType);
         }
     }
 
     @SuppressWarnings({"unchecked"})
     public List<PersistentCollectionChangeData> mapCollectionChanges(String referencingPropertyName,
                                                                      PersistentCollection newColl,
                                                                      Serializable oldColl, Serializable id) {
         if (!commonCollectionMapperData.getCollectionReferencingPropertyData().getName()
                 .equals(referencingPropertyName)) {
             return null;
         }
 
         List<PersistentCollectionChangeData> collectionChanges = new ArrayList<PersistentCollectionChangeData>();
 
         // Comparing new and old collection content.
         Collection newCollection = getNewCollectionContent(newColl);
         Collection oldCollection = getOldCollectionContent(oldColl);
 
         Set<Object> added = new HashSet<Object>();
         if (newColl != null) { added.addAll(newCollection); }
 		// Re-hashing the old collection as the hash codes of the elements there may have changed, and the
 		// removeAll in AbstractSet has an implementation that is hashcode-change sensitive (as opposed to addAll).
         if (oldColl != null) { added.removeAll(new HashSet(oldCollection)); }
 
         addCollectionChanges(collectionChanges, added, RevisionType.ADD, id);
 
         Set<Object> deleted = new HashSet<Object>();
         if (oldColl != null) { deleted.addAll(oldCollection); }
 		// The same as above - re-hashing new collection.
         if (newColl != null) { deleted.removeAll(new HashSet(newCollection)); }
 
         addCollectionChanges(collectionChanges, deleted, RevisionType.DEL, id);
 
         return collectionChanges;
     }
 
     public boolean mapToMapFromEntity(SessionImplementor session, Map<String, Object> data, Object newObj, Object oldObj) {
         // Changes are mapped in the "mapCollectionChanges" method.
         return false;
     }
 
     protected abstract Initializor<T> getInitializor(AuditConfiguration verCfg,
                                                      AuditReaderImplementor versionsReader, Object primaryKey,
                                                      Number revision);
 
     public void mapToEntityFromMap(AuditConfiguration verCfg, Object obj, Map data, Object primaryKey,
                                    AuditReaderImplementor versionsReader, Number revision) {
         Setter setter = ReflectionTools.getSetter(obj.getClass(),
                 commonCollectionMapperData.getCollectionReferencingPropertyData());
         try {
             setter.set(obj, proxyConstructor.newInstance(getInitializor(verCfg, versionsReader, primaryKey, revision)), null);
         } catch (InstantiationException e) {
             throw new AuditException(e);
         } catch (IllegalAccessException e) {
             throw new AuditException(e);
         } catch (InvocationTargetException e) {
             throw new AuditException(e);
         }
     }
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/BasicCollectionMapper.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/BasicCollectionMapper.java
index a54d5904fb..8145c87495 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/BasicCollectionMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/BasicCollectionMapper.java
@@ -1,71 +1,71 @@
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
  */
 package org.hibernate.envers.entities.mapper.relation;
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.Map;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.envers.entities.mapper.PropertyMapper;
 import org.hibernate.envers.entities.mapper.relation.lazy.initializor.BasicCollectionInitializor;
 import org.hibernate.envers.entities.mapper.relation.lazy.initializor.Initializor;
 import org.hibernate.envers.reader.AuditReaderImplementor;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public final class BasicCollectionMapper<T extends Collection> extends AbstractCollectionMapper<T> implements PropertyMapper {
     private final MiddleComponentData elementComponentData;
 
     public BasicCollectionMapper(CommonCollectionMapperData commonCollectionMapperData,
                                  Class<? extends T> collectionClass, Class<? extends T> proxyClass,
                                  MiddleComponentData elementComponentData) {
         super(commonCollectionMapperData, collectionClass, proxyClass);
         this.elementComponentData = elementComponentData;
     }
 
     protected Initializor<T> getInitializor(AuditConfiguration verCfg, AuditReaderImplementor versionsReader,
                                             Object primaryKey, Number revision) {
         return new BasicCollectionInitializor<T>(verCfg, versionsReader, commonCollectionMapperData.getQueryGenerator(),
                 primaryKey, revision, collectionClass, elementComponentData);
     }
 
     protected Collection getNewCollectionContent(PersistentCollection newCollection) {
         return (Collection) newCollection;
     }
 
     protected Collection getOldCollectionContent(Serializable oldCollection) {
         if (oldCollection == null) {
             return null;
         } else if (oldCollection instanceof Map) {
             return ((Map) oldCollection).keySet();
         } else {
             return (Collection) oldCollection;
         }
     }
 
     protected void mapToMapFromObject(Map<String, Object> data, Object changed) {
         elementComponentData.getComponentMapper().mapToMapFromObject(data, changed);
     }
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/ListCollectionMapper.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/ListCollectionMapper.java
index 73f992f5be..1ef579e0aa 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/ListCollectionMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/ListCollectionMapper.java
@@ -1,83 +1,83 @@
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
  */
 package org.hibernate.envers.entities.mapper.relation;
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.List;
 import java.util.Map;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.envers.entities.mapper.PropertyMapper;
 import org.hibernate.envers.entities.mapper.relation.lazy.initializor.Initializor;
 import org.hibernate.envers.entities.mapper.relation.lazy.initializor.ListCollectionInitializor;
 import org.hibernate.envers.entities.mapper.relation.lazy.proxy.ListProxy;
 import org.hibernate.envers.reader.AuditReaderImplementor;
 import org.hibernate.envers.tools.Pair;
 import org.hibernate.envers.tools.Tools;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public final class ListCollectionMapper extends AbstractCollectionMapper<List> implements PropertyMapper {
     private final MiddleComponentData elementComponentData;
     private final MiddleComponentData indexComponentData;
 
     public ListCollectionMapper(CommonCollectionMapperData commonCollectionMapperData,
                                 MiddleComponentData elementComponentData, MiddleComponentData indexComponentData) {
         super(commonCollectionMapperData, List.class, ListProxy.class);
         this.elementComponentData = elementComponentData;
         this.indexComponentData = indexComponentData;
     }
 
     protected Initializor<List> getInitializor(AuditConfiguration verCfg, AuditReaderImplementor versionsReader,
                                                Object primaryKey, Number revision) {
         return new ListCollectionInitializor(verCfg, versionsReader, commonCollectionMapperData.getQueryGenerator(),
                 primaryKey, revision, elementComponentData, indexComponentData);
     }
 
     @SuppressWarnings({"unchecked"})
     protected Collection getNewCollectionContent(PersistentCollection newCollection) {
         if (newCollection == null) {
             return null;
         } else {
             return Tools.listToIndexElementPairList((List<Object>) newCollection);
         }
     }
 
     @SuppressWarnings({"unchecked"})
     protected Collection getOldCollectionContent(Serializable oldCollection) {
         if (oldCollection == null) {
             return null;
         } else {
             return Tools.listToIndexElementPairList((List<Object>) oldCollection);
         }
     }
 
     @SuppressWarnings({"unchecked"})
     protected void mapToMapFromObject(Map<String, Object> data, Object changed) {
         Pair<Integer, Object> indexValuePair = (Pair<Integer, Object>) changed;
         elementComponentData.getComponentMapper().mapToMapFromObject(data, indexValuePair.getSecond());
         indexComponentData.getComponentMapper().mapToMapFromObject(data, indexValuePair.getFirst());
     }
 }
\ No newline at end of file
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/MapCollectionMapper.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/MapCollectionMapper.java
index 2754e975b0..6d2f29bb40 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/MapCollectionMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/MapCollectionMapper.java
@@ -1,76 +1,76 @@
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
  */
 package org.hibernate.envers.entities.mapper.relation;
 import java.io.Serializable;
 import java.util.Collection;
 import java.util.Map;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.envers.entities.mapper.PropertyMapper;
 import org.hibernate.envers.entities.mapper.relation.lazy.initializor.Initializor;
 import org.hibernate.envers.entities.mapper.relation.lazy.initializor.MapCollectionInitializor;
 import org.hibernate.envers.reader.AuditReaderImplementor;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public final class MapCollectionMapper<T extends Map> extends AbstractCollectionMapper<T> implements PropertyMapper {
     private final MiddleComponentData elementComponentData;
     private final MiddleComponentData indexComponentData;
 
     public MapCollectionMapper(CommonCollectionMapperData commonCollectionMapperData,
                                Class<? extends T> collectionClass, Class<? extends T> proxyClass,
                                MiddleComponentData elementComponentData, MiddleComponentData indexComponentData) {
         super(commonCollectionMapperData, collectionClass, proxyClass);
         this.elementComponentData = elementComponentData;
         this.indexComponentData = indexComponentData;
     }
 
     protected Initializor<T> getInitializor(AuditConfiguration verCfg, AuditReaderImplementor versionsReader,
                                             Object primaryKey, Number revision) {
         return new MapCollectionInitializor<T>(verCfg, versionsReader, commonCollectionMapperData.getQueryGenerator(),
                 primaryKey, revision, collectionClass, elementComponentData, indexComponentData);
     }
 
     protected Collection getNewCollectionContent(PersistentCollection newCollection) {
         if (newCollection == null) {
             return null;
         } else {
             return ((Map) newCollection).entrySet();
         }
     }
 
     protected Collection getOldCollectionContent(Serializable oldCollection) {
         if (oldCollection == null) {
             return null;
         } else {
             return ((Map) oldCollection).entrySet();
         }
     }
 
     protected void mapToMapFromObject(Map<String, Object> data, Object changed) {
         elementComponentData.getComponentMapper().mapToMapFromObject(data, ((Map.Entry) changed).getValue());
         indexComponentData.getComponentMapper().mapToMapFromObject(data, ((Map.Entry) changed).getKey());
     }
 }
\ No newline at end of file
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/OneToOneNotOwningMapper.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/OneToOneNotOwningMapper.java
index 9ebc95ef46..84d05fe9c0 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/OneToOneNotOwningMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/OneToOneNotOwningMapper.java
@@ -1,98 +1,98 @@
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
  */
 package org.hibernate.envers.entities.mapper.relation;
 import java.io.Serializable;
 import java.util.List;
 import java.util.Map;
 import javax.persistence.NoResultException;
 import org.hibernate.NonUniqueResultException;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.envers.entities.EntityConfiguration;
 import org.hibernate.envers.entities.PropertyData;
 import org.hibernate.envers.entities.mapper.PersistentCollectionChangeData;
 import org.hibernate.envers.entities.mapper.PropertyMapper;
 import org.hibernate.envers.exception.AuditException;
 import org.hibernate.envers.query.AuditEntity;
 import org.hibernate.envers.reader.AuditReaderImplementor;
 import org.hibernate.envers.tools.reflection.ReflectionTools;
 import org.hibernate.property.Setter;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  * @author Hern�n Chanfreau
  */
 public class OneToOneNotOwningMapper implements PropertyMapper {
     private String owningReferencePropertyName;
     private String owningEntityName;
     private PropertyData propertyData;
 
     public OneToOneNotOwningMapper(String owningReferencePropertyName, String owningEntityName,
                                    PropertyData propertyData) {
         this.owningReferencePropertyName = owningReferencePropertyName;
         this.owningEntityName = owningEntityName;
         this.propertyData = propertyData;
     }
 
     public boolean mapToMapFromEntity(SessionImplementor session, Map<String, Object> data, Object newObj, Object oldObj) {
         return false;
     }
 
     public void mapToEntityFromMap(AuditConfiguration verCfg, Object obj, Map data, Object primaryKey, AuditReaderImplementor versionsReader, Number revision) {
         if (obj == null) {
             return;
         }
 
     	EntityConfiguration entCfg = verCfg.getEntCfg().get(owningEntityName);
     	if(entCfg == null) {
     		// a relation marked as RelationTargetAuditMode.NOT_AUDITED 
     		entCfg = verCfg.getEntCfg().getNotVersionEntityConfiguration(owningEntityName);
     	}
 
         Class<?> entityClass = ReflectionTools.loadClass(entCfg.getEntityClassName());
 
         Object value;
 
         try {
             value = versionsReader.createQuery().forEntitiesAtRevision(entityClass, owningEntityName, revision)
                     .add(AuditEntity.relatedId(owningReferencePropertyName).eq(primaryKey)).getSingleResult();
         } catch (NoResultException e) {
             value = null;
         } catch (NonUniqueResultException e) {
             throw new AuditException("Many versions results for one-to-one relationship: (" + owningEntityName +
                     ", " + owningReferencePropertyName + ")");
         }
 
         Setter setter = ReflectionTools.getSetter(obj.getClass(), propertyData);
         setter.set(obj, value, null);
     }
 
     public List<PersistentCollectionChangeData> mapCollectionChanges(String referencingPropertyName,
                                                                                     PersistentCollection newColl,
                                                                                     Serializable oldColl,
                                                                                     Serializable id) {
         return null;
     }
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/ToOneIdMapper.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/ToOneIdMapper.java
index a59f0b4684..07aa7e38ba 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/ToOneIdMapper.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/ToOneIdMapper.java
@@ -1,110 +1,110 @@
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
  */
 package org.hibernate.envers.entities.mapper.relation;
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.envers.entities.EntityConfiguration;
 import org.hibernate.envers.entities.PropertyData;
 import org.hibernate.envers.entities.mapper.PersistentCollectionChangeData;
 import org.hibernate.envers.entities.mapper.PropertyMapper;
 import org.hibernate.envers.entities.mapper.id.IdMapper;
 import org.hibernate.envers.entities.mapper.relation.lazy.ToOneDelegateSessionImplementor;
 import org.hibernate.envers.reader.AuditReaderImplementor;
 import org.hibernate.envers.tools.Tools;
 import org.hibernate.envers.tools.reflection.ReflectionTools;
 import org.hibernate.property.Setter;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  * @author Hern�n Chanfreau
  */
 public class ToOneIdMapper implements PropertyMapper {
     private final IdMapper delegate;
     private final PropertyData propertyData;
     private final String referencedEntityName;
     private final boolean nonInsertableFake;
 
     public ToOneIdMapper(IdMapper delegate, PropertyData propertyData, String referencedEntityName, boolean nonInsertableFake) {
         this.delegate = delegate;
         this.propertyData = propertyData;
         this.referencedEntityName = referencedEntityName;
         this.nonInsertableFake = nonInsertableFake;
     }
 
     public boolean mapToMapFromEntity(SessionImplementor session, Map<String, Object> data, Object newObj, Object oldObj) {
         HashMap<String, Object> newData = new HashMap<String, Object>();
         data.put(propertyData.getName(), newData);
 
         // If this property is originally non-insertable, but made insertable because it is in a many-to-one "fake"
         // bi-directional relation, we always store the "old", unchaged data, to prevent storing changes made
         // to this field. It is the responsibility of the collection to properly update it if it really changed.
         delegate.mapToMapFromEntity(newData, nonInsertableFake ? oldObj : newObj);
 
         //noinspection SimplifiableConditionalExpression
         return nonInsertableFake ? false : !Tools.entitiesEqual(session, referencedEntityName, newObj, oldObj);
     }
 
     public void mapToEntityFromMap(AuditConfiguration verCfg, Object obj, Map data, Object primaryKey,
                                    AuditReaderImplementor versionsReader, Number revision) {
         if (obj == null) {
             return;
         }
 
         Object entityId = delegate.mapToIdFromMap((Map) data.get(propertyData.getName()));
         Object value;
         if (entityId == null) {
             value = null;
         } else {
             if (versionsReader.getFirstLevelCache().contains(referencedEntityName, revision, entityId)) {
                 value = versionsReader.getFirstLevelCache().get(referencedEntityName, revision, entityId);
             } else {
             	EntityConfiguration entCfg = verCfg.getEntCfg().get(referencedEntityName);
             	if(entCfg == null) {
             		// a relation marked as RelationTargetAuditMode.NOT_AUDITED 
             		entCfg = verCfg.getEntCfg().getNotVersionEntityConfiguration(referencedEntityName);
             	}
             	
                 Class<?> entityClass = ReflectionTools.loadClass(entCfg.getEntityClassName());
 
                 value = versionsReader.getSessionImplementor().getFactory().getEntityPersister(referencedEntityName).
                         createProxy((Serializable)entityId, new ToOneDelegateSessionImplementor(versionsReader, entityClass, entityId, revision, verCfg));
             }
         }
 
         Setter setter = ReflectionTools.getSetter(obj.getClass(), propertyData);
         setter.set(obj, value, null);
     }
 
     public List<PersistentCollectionChangeData> mapCollectionChanges(String referencingPropertyName,
                                                                      PersistentCollection newColl,
                                                                      Serializable oldColl,
                                                                      Serializable id) {
         return null;
     }
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
index 95e6af147e..1ed2597677 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
@@ -1,310 +1,310 @@
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
 import org.hibernate.cache.spi.CacheKey;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
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
 import org.hibernate.internal.CriteriaImpl;
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
index 6b8b4e90d3..9b8270768b 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/event/BaseEnversCollectionEventListener.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/event/BaseEnversCollectionEventListener.java
@@ -1,243 +1,243 @@
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
 
 import java.io.Serializable;
 import java.util.List;
 
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.CollectionEntry;
 import org.hibernate.envers.RevisionType;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.envers.entities.EntityConfiguration;
 import org.hibernate.envers.entities.RelationDescription;
 import org.hibernate.envers.entities.mapper.PersistentCollectionChangeData;
 import org.hibernate.envers.entities.mapper.id.IdMapper;
 import org.hibernate.envers.synchronization.AuditProcess;
 import org.hibernate.envers.synchronization.work.AuditWorkUnit;
 import org.hibernate.envers.synchronization.work.CollectionChangeWorkUnit;
 import org.hibernate.envers.synchronization.work.FakeBidirectionalRelationWorkUnit;
 import org.hibernate.envers.synchronization.work.PersistentCollectionChangeWorkUnit;
 import org.hibernate.event.AbstractCollectionEvent;
 import org.hibernate.persister.collection.AbstractCollectionPersister;
 
 /**
  * Base class for Envers' collection event related listeners
  *
  * @author Adam Warski (adam at warski dot org)
  * @author Hern�n Chanfreau
  * @author Steve Ebersole
  */
 public abstract class BaseEnversCollectionEventListener extends BaseEnversEventListener {
 	protected BaseEnversCollectionEventListener(AuditConfiguration enversConfiguration) {
 		super( enversConfiguration );
 	}
 
     protected final CollectionEntry getCollectionEntry(AbstractCollectionEvent event) {
         return event.getSession().getPersistenceContext().getCollectionEntry(event.getCollection());
     }
 
     protected final void onCollectionAction(
 			AbstractCollectionEvent event,
 			PersistentCollection newColl,
 			Serializable oldColl,
 			CollectionEntry collectionEntry) {
         String entityName = event.getAffectedOwnerEntityName();
         if ( ! getAuditConfiguration().getGlobalCfg().isGenerateRevisionsForCollections() ) {
             return;
         }
         if ( getAuditConfiguration().getEntCfg().isVersioned( entityName ) ) {
             AuditProcess auditProcess = getAuditConfiguration().getSyncManager().get(event.getSession());
 
             String ownerEntityName = ((AbstractCollectionPersister) collectionEntry.getLoadedPersister()).getOwnerEntityName();
             String referencingPropertyName = collectionEntry.getRole().substring(ownerEntityName.length() + 1);
 
             // Checking if this is not a "fake" many-to-one bidirectional relation. The relation description may be
             // null in case of collections of non-entities.
             RelationDescription rd = searchForRelationDescription( entityName, referencingPropertyName );
             if ( rd != null && rd.getMappedByPropertyName() != null ) {
                 generateFakeBidirecationalRelationWorkUnits(
 						auditProcess,
 						newColl,
 						oldColl,
 						entityName,
                         referencingPropertyName,
 						event,
 						rd
 				);
             }
 			else {
                 PersistentCollectionChangeWorkUnit workUnit = new PersistentCollectionChangeWorkUnit(
 						event.getSession(),
 						entityName,
 						getAuditConfiguration(),
 						newColl,
 						collectionEntry,
 						oldColl,
 						event.getAffectedOwnerIdOrNull(),
 						referencingPropertyName
 				);
 				auditProcess.addWorkUnit( workUnit );
 
                 if (workUnit.containsWork()) {
                     // There are some changes: a revision needs also be generated for the collection owner
                     auditProcess.addWorkUnit(
 							new CollectionChangeWorkUnit(
 									event.getSession(),
 									event.getAffectedOwnerEntityName(),
 									getAuditConfiguration(),
 									event.getAffectedOwnerIdOrNull(),
 									event.getAffectedOwnerOrNull()
 							)
 					);
 
                     generateBidirectionalCollectionChangeWorkUnits( auditProcess, event, workUnit, rd );
                 }
             }
         }
     }
 
     /**
      * Looks up a relation description corresponding to the given property in the given entity. If no description is
      * found in the given entity, the parent entity is checked (so that inherited relations work).
 	 *
      * @param entityName Name of the entity, in which to start looking.
      * @param referencingPropertyName The name of the property.
 	 * 
      * @return A found relation description corresponding to the given entity or {@code null}, if no description can
      * be found.
      */
     private RelationDescription searchForRelationDescription(String entityName, String referencingPropertyName) {
         EntityConfiguration configuration = getAuditConfiguration().getEntCfg().get( entityName );
         RelationDescription rd = configuration.getRelationDescription(referencingPropertyName);
         if ( rd == null && configuration.getParentEntityName() != null ) {
             return searchForRelationDescription( configuration.getParentEntityName(), referencingPropertyName );
         }
 
         return rd;
     }
 
     private void generateFakeBidirecationalRelationWorkUnits(
 			AuditProcess auditProcess,
 			PersistentCollection newColl,
 			Serializable oldColl,
 			String collectionEntityName,
 			String referencingPropertyName,
 			AbstractCollectionEvent event,
 			RelationDescription rd) {
         // First computing the relation changes
         List<PersistentCollectionChangeData> collectionChanges = getAuditConfiguration()
 				.getEntCfg()
 				.get( collectionEntityName )
 				.getPropertyMapper()
                 .mapCollectionChanges( referencingPropertyName, newColl, oldColl, event.getAffectedOwnerIdOrNull() );
 
         // Getting the id mapper for the related entity, as the work units generated will corrspond to the related
         // entities.
         String relatedEntityName = rd.getToEntityName();
         IdMapper relatedIdMapper = getAuditConfiguration().getEntCfg().get(relatedEntityName).getIdMapper();
 
         // For each collection change, generating the bidirectional work unit.
         for ( PersistentCollectionChangeData changeData : collectionChanges ) {
             Object relatedObj = changeData.getChangedElement();
             Serializable relatedId = (Serializable) relatedIdMapper.mapToIdFromEntity(relatedObj);
             RevisionType revType = (RevisionType) changeData.getData().get(
 					getAuditConfiguration().getAuditEntCfg().getRevisionTypePropName()
 			);
 
             // This can be different from relatedEntityName, in case of inheritance (the real entity may be a subclass
             // of relatedEntityName).
             String realRelatedEntityName = event.getSession().bestGuessEntityName(relatedObj);
 
             // By default, the nested work unit is a collection change work unit.
             AuditWorkUnit nestedWorkUnit = new CollectionChangeWorkUnit(
 					event.getSession(),
 					realRelatedEntityName,
 					getAuditConfiguration(),
                     relatedId,
 					relatedObj
 			);
 
             auditProcess.addWorkUnit(
 					new FakeBidirectionalRelationWorkUnit(
 							event.getSession(),
 							realRelatedEntityName,
 							getAuditConfiguration(),
 							relatedId,
 							referencingPropertyName,
 							event.getAffectedOwnerOrNull(),
 							rd,
 							revType,
 							changeData.getChangedElementIndex(),
 							nestedWorkUnit
 					)
 			);
         }
 
         // We also have to generate a collection change work unit for the owning entity.
         auditProcess.addWorkUnit(
 				new CollectionChangeWorkUnit(
 						event.getSession(),
 						collectionEntityName,
 						getAuditConfiguration(),
 						event.getAffectedOwnerIdOrNull(),
 						event.getAffectedOwnerOrNull()
 				)
 		);
     }
 
     private void generateBidirectionalCollectionChangeWorkUnits(
 			AuditProcess auditProcess,
 			AbstractCollectionEvent event,
 			PersistentCollectionChangeWorkUnit workUnit,
 			RelationDescription rd) {
         // Checking if this is enabled in configuration ...
         if ( ! getAuditConfiguration().getGlobalCfg().isGenerateRevisionsForCollections() ) {
             return;
         }
 
         // Checking if this is not a bidirectional relation - then, a revision needs also be generated for
         // the other side of the relation.
         // relDesc can be null if this is a collection of simple values (not a relation).
         if ( rd != null && rd.isBidirectional() ) {
             String relatedEntityName = rd.getToEntityName();
             IdMapper relatedIdMapper = getAuditConfiguration().getEntCfg().get( relatedEntityName ).getIdMapper();
 
             for ( PersistentCollectionChangeData changeData : workUnit.getCollectionChanges() ) {
                 Object relatedObj = changeData.getChangedElement();
                 Serializable relatedId = (Serializable) relatedIdMapper.mapToIdFromEntity( relatedObj );
 
                 auditProcess.addWorkUnit(
 						new CollectionChangeWorkUnit(
 								event.getSession(),
 								relatedEntityName,
 								getAuditConfiguration(),
 								relatedId,
 								relatedObj
 						)
 				);
             }
         }
     }
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/PersistentCollectionChangeWorkUnit.java b/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/PersistentCollectionChangeWorkUnit.java
index b85896ad29..8a814eb0e8 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/PersistentCollectionChangeWorkUnit.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/synchronization/work/PersistentCollectionChangeWorkUnit.java
@@ -1,206 +1,206 @@
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
  */
 package org.hibernate.envers.synchronization.work;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import org.hibernate.Session;
-import org.hibernate.collection.PersistentCollection;
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.CollectionEntry;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.envers.RevisionType;
 import org.hibernate.envers.configuration.AuditConfiguration;
 import org.hibernate.envers.configuration.AuditEntitiesConfiguration;
 import org.hibernate.envers.entities.mapper.PersistentCollectionChangeData;
 
 /**
  * @author Adam Warski (adam at warski dot org)
  */
 public class PersistentCollectionChangeWorkUnit extends AbstractAuditWorkUnit implements AuditWorkUnit {
     private final List<PersistentCollectionChangeData> collectionChanges;
     private final String referencingPropertyName;
 
     public PersistentCollectionChangeWorkUnit(SessionImplementor sessionImplementor, String entityName,
 											  AuditConfiguration auditCfg, PersistentCollection collection,
 											  CollectionEntry collectionEntry, Serializable snapshot, Serializable id,
                                               String referencingPropertyName) {
         super(sessionImplementor, entityName, auditCfg, new PersistentCollectionChangeWorkUnitId(id, collectionEntry.getRole()));
 
 		this.referencingPropertyName = referencingPropertyName;
 
         collectionChanges = auditCfg.getEntCfg().get(getEntityName()).getPropertyMapper()
                 .mapCollectionChanges(referencingPropertyName, collection, snapshot, id);
     }
 
     public PersistentCollectionChangeWorkUnit(SessionImplementor sessionImplementor, String entityName,
                                               AuditConfiguration verCfg, Serializable id,
                                               List<PersistentCollectionChangeData> collectionChanges,
                                               String referencingPropertyName) {
         super(sessionImplementor, entityName, verCfg, id);
 
         this.collectionChanges = collectionChanges;
         this.referencingPropertyName = referencingPropertyName;
     }
 
     public boolean containsWork() {
         return collectionChanges != null && collectionChanges.size() != 0;
     }
 
     public Map<String, Object> generateData(Object revisionData) {
         throw new UnsupportedOperationException("Cannot generate data for a collection change work unit!");
     }
 
     @SuppressWarnings({"unchecked"})
     public void perform(Session session, Object revisionData) {
         AuditEntitiesConfiguration entitiesCfg = verCfg.getAuditEntCfg();
 
         for (PersistentCollectionChangeData persistentCollectionChangeData : collectionChanges) {
             // Setting the revision number
             ((Map<String, Object>) persistentCollectionChangeData.getData().get(entitiesCfg.getOriginalIdPropName()))
                     .put(entitiesCfg.getRevisionFieldName(), revisionData);
 
             auditStrategy.performCollectionChange(session, verCfg, persistentCollectionChangeData, revisionData);
         }
     }
 
     public String getReferencingPropertyName() {
         return referencingPropertyName;
     }
 
     public List<PersistentCollectionChangeData> getCollectionChanges() {
         return collectionChanges;
     }
 
     public AuditWorkUnit merge(AddWorkUnit second) {
         return null;
     }
 
     public AuditWorkUnit merge(ModWorkUnit second) {
         return null;
     }
 
     public AuditWorkUnit merge(DelWorkUnit second) {
         return null;
     }
 
     public AuditWorkUnit merge(CollectionChangeWorkUnit second) {
         return null;
     }
 
     public AuditWorkUnit merge(FakeBidirectionalRelationWorkUnit second) {
         return null;
     }
 
     public AuditWorkUnit dispatch(WorkUnitMergeVisitor first) {
         if (first instanceof PersistentCollectionChangeWorkUnit) {
             PersistentCollectionChangeWorkUnit original = (PersistentCollectionChangeWorkUnit) first;
 
             // Merging the collection changes in both work units.
 
             // First building a map from the ids of the collection-entry-entities from the "second" collection changes,
             // to the PCCD objects. That way, we will be later able to check if an "original" collection change
             // should be added, or if it is overshadowed by a new one.
             Map<Object, PersistentCollectionChangeData> newChangesIdMap = new HashMap<Object, PersistentCollectionChangeData>();
             for (PersistentCollectionChangeData persistentCollectionChangeData : getCollectionChanges()) {
                 newChangesIdMap.put(
                         getOriginalId(persistentCollectionChangeData),
                         persistentCollectionChangeData);
             }
 
             // This will be the list with the resulting (merged) changes.
             List<PersistentCollectionChangeData> mergedChanges = new ArrayList<PersistentCollectionChangeData>();
 
             // Including only those original changes, which are not overshadowed by new ones.
             for (PersistentCollectionChangeData originalCollectionChangeData : original.getCollectionChanges()) {
                 Object originalOriginalId = getOriginalId(originalCollectionChangeData);
                 if (!newChangesIdMap.containsKey(originalOriginalId)) {
                     mergedChanges.add(originalCollectionChangeData);
                 } else {
                     // If the changes collide, checking if the first one isn't a DEL, and the second a subsequent ADD
                     // If so, removing the change alltogether.
                     String revTypePropName = verCfg.getAuditEntCfg().getRevisionTypePropName();
                     if (RevisionType.ADD.equals(newChangesIdMap.get(originalOriginalId).getData().get(
                             revTypePropName)) && RevisionType.DEL.equals(originalCollectionChangeData.getData().get(
                             revTypePropName))) {
                         newChangesIdMap.remove(originalOriginalId);
                     }
                 }
             }
 
             // Finally adding all of the new changes to the end of the list (the map values may differ from
             // getCollectionChanges() because of the last operation above).
             mergedChanges.addAll(newChangesIdMap.values());
 
             return new PersistentCollectionChangeWorkUnit(sessionImplementor, entityName, verCfg, id, mergedChanges, 
                     referencingPropertyName);
         } else {
             throw new RuntimeException("Trying to merge a " + first + " with a PersitentCollectionChangeWorkUnit. " +
                     "This is not really possible.");
         }
     }
 
     private Object getOriginalId(PersistentCollectionChangeData persistentCollectionChangeData) {
         return persistentCollectionChangeData.getData().get(verCfg.getAuditEntCfg().getOriginalIdPropName());
     }
 
     /**
      * A unique identifier for a collection work unit. Consists of an id of the owning entity and the name of
      * the entity plus the name of the field (the role). This is needed because such collections aren't entities
      * in the "normal" mapping, but they are entities for Envers.
      */
     private static class PersistentCollectionChangeWorkUnitId implements Serializable {
         private static final long serialVersionUID = -8007831518629167537L;
         
         private final Serializable ownerId;
         private final String role;
 
         public PersistentCollectionChangeWorkUnitId(Serializable ownerId, String role) {
             this.ownerId = ownerId;
             this.role = role;
         }
 
         @Override
         public boolean equals(Object o) {
             if (this == o) return true;
             if (o == null || getClass() != o.getClass()) return false;
 
             PersistentCollectionChangeWorkUnitId that = (PersistentCollectionChangeWorkUnitId) o;
 
             if (ownerId != null ? !ownerId.equals(that.ownerId) : that.ownerId != null) return false;
             //noinspection RedundantIfStatement
             if (role != null ? !role.equals(that.role) : that.role != null) return false;
 
             return true;
         }
 
         @Override
         public int hashCode() {
             int result = ownerId != null ? ownerId.hashCode() : 0;
             result = 31 * result + (role != null ? role.hashCode() : 0);
             return result;
         }
     }
 }
