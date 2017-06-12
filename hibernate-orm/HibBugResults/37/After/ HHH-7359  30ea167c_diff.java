diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
index 52c4795d92..31c565ce15 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
@@ -1,138 +1,139 @@
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
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostCollectionUpdateEvent;
 import org.hibernate.event.spi.PostCollectionUpdateEventListener;
 import org.hibernate.event.spi.PreCollectionUpdateEvent;
 import org.hibernate.event.spi.PreCollectionUpdateEventListener;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 
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
-					MessageHelper.collectionInfoString( persister, id, persister.getFactory() )
+					MessageHelper.collectionInfoString(persister, collection,
+							id, session )
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
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
index 9558b14159..75323d23d6 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
@@ -1,1172 +1,1171 @@
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
 package org.hibernate.collection.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 
 import javax.naming.NamingException;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LazyInitializationException;
 import org.hibernate.Session;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.SessionFactoryRegistry;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
 /**
  * Base class implementing {@link org.hibernate.collection.spi.PersistentCollection}
  *
  * @author Gavin King
  */
 public abstract class AbstractPersistentCollection implements Serializable, PersistentCollection {
 	private static final Logger log = Logger.getLogger( AbstractPersistentCollection.class );
 
 	private transient SessionImplementor session;
 	private boolean initialized;
 	private transient List<DelayedOperation> operationQueue;
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
 
 	private String sessionFactoryUuid;
 	private boolean specjLazyLoad = false;
 
 	public final String getRole() {
 		return role;
 	}
 
 	public final Serializable getKey() {
 		return key;
 	}
 
 	public final boolean isUnreferenced() {
 		return role == null;
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
 		initialize( false );
 	}
 
 	/**
 	 * Called by the {@link Collection#size} method
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	protected boolean readSize() {
 		if ( !initialized ) {
 			if ( cachedSize != -1 && !hasQueuedOperations() ) {
 				return true;
 			}
 			else {
 				boolean isExtraLazy = withTemporarySessionIfNeeded(
 						new LazyInitializationWork<Boolean>() {
 							@Override
 							public Boolean doWork() {
 								CollectionEntry entry = session.getPersistenceContext().getCollectionEntry( AbstractPersistentCollection.this );
 
 								if ( entry != null ) {
 									CollectionPersister persister = entry.getLoadedPersister();
 									if ( persister.isExtraLazy() ) {
 										if ( hasQueuedOperations() ) {
 											session.flush();
 										}
 										cachedSize = persister.getSize( entry.getLoadedKey(), session );
 										return true;
 									}
 									else {
 										read();
 									}
 								}
 								else{
 									throwLazyInitializationExceptionIfNotConnected();
 								}
 								return false;
 							}
 						}
 				);
 				if ( isExtraLazy ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	public static interface LazyInitializationWork<T> {
 		public T doWork();
 	}
 
 	private <T> T withTemporarySessionIfNeeded(LazyInitializationWork<T> lazyInitializationWork) {
 		SessionImplementor originalSession = null;
 		boolean isTempSession = false;
 		boolean isJTA = false;
 
 		if ( session == null ) {
 			if ( specjLazyLoad ) {
 				session = openTemporarySessionForLoading();
 				isTempSession = true;
 			}
 			else {
 				throw new LazyInitializationException( "could not initialize proxy - no Session" );
 			}
 		}
 		else if ( !session.isOpen() ) {
 			if ( specjLazyLoad ) {
 				originalSession = session;
 				session = openTemporarySessionForLoading();
 				isTempSession = true;
 			}
 			else {
 				throw new LazyInitializationException( "could not initialize proxy - the owning Session was closed" );
 			}
 		}
 		else if ( !session.isConnected() ) {
 			if ( specjLazyLoad ) {
 				originalSession = session;
 				session = openTemporarySessionForLoading();
 				isTempSession = true;
 			}
 			else {
 				throw new LazyInitializationException( "could not initialize proxy - the owning Session is disconnected" );
 			}
 		}
 
 		if ( isTempSession ) {
 			// TODO: On the next major release, add an
 			// 'isJTA' or 'getTransactionFactory' method to Session.
 			isJTA = session.getTransactionCoordinator()
 					.getTransactionContext().getTransactionEnvironment()
 					.getTransactionFactory()
 					.compatibleWithJtaSynchronization();
 			
 			if ( !isJTA ) {
 				// Explicitly handle the transactions only if we're not in
 				// a JTA environment.  A lazy loading temporary session can
 				// be created even if a current session and transaction are
 				// open (ex: session.clear() was used).  We must prevent
 				// multiple transactions.
 				( ( Session) session ).beginTransaction();
 			}
 			
 			session.getPersistenceContext().addUninitializedDetachedCollection(
 					session.getFactory().getCollectionPersister( getRole() ),
 					this
 			);
 		}
 
 		try {
 			return lazyInitializationWork.doWork();
 		}
 		finally {
 			if ( isTempSession ) {
 				// make sure the just opened temp session gets closed!
 				try {
 					if ( !isJTA ) {
 						( ( Session) session ).getTransaction().commit();
 					}
 					( (Session) session ).close();
 				}
 				catch (Exception e) {
 					log.warn( "Unable to close temporary session used to load lazy collection associated to no session" );
 				}
 				session = originalSession;
 			}
 		}
 	}
 
 	private SessionImplementor openTemporarySessionForLoading() {
 		if ( sessionFactoryUuid == null ) {
 			throwLazyInitializationException( "SessionFactory UUID not known to create temporary Session for loading" );
 		}
 
 		SessionFactoryImplementor sf = (SessionFactoryImplementor)
 				SessionFactoryRegistry.INSTANCE.getSessionFactory( sessionFactoryUuid );
 		return (SessionImplementor) sf.openSession();
 	}
 
 	protected Boolean readIndexExistence(final Object index) {
 		if ( !initialized ) {
 			Boolean extraLazyExistenceCheck = withTemporarySessionIfNeeded(
 					new LazyInitializationWork<Boolean>() {
 						@Override
 						public Boolean doWork() {
 							CollectionEntry entry = session.getPersistenceContext().getCollectionEntry( AbstractPersistentCollection.this );
 							CollectionPersister persister = entry.getLoadedPersister();
 							if ( persister.isExtraLazy() ) {
 								if ( hasQueuedOperations() ) {
 									session.flush();
 								}
 								return persister.indexExists( entry.getLoadedKey(), index, session );
 							}
 							else {
 								read();
 							}
 							return null;
 						}
 					}
 			);
 			if ( extraLazyExistenceCheck != null ) {
 				return extraLazyExistenceCheck;
 			}
 		}
 		return null;
 	}
 
 	protected Boolean readElementExistence(final Object element) {
 		if ( !initialized ) {
 			Boolean extraLazyExistenceCheck = withTemporarySessionIfNeeded(
 					new LazyInitializationWork<Boolean>() {
 						@Override
 						public Boolean doWork() {
 							CollectionEntry entry = session.getPersistenceContext().getCollectionEntry( AbstractPersistentCollection.this );
 							CollectionPersister persister = entry.getLoadedPersister();
 							if ( persister.isExtraLazy() ) {
 								if ( hasQueuedOperations() ) {
 									session.flush();
 								}
 								return persister.elementExists( entry.getLoadedKey(), element, session );
 							}
 							else {
 								read();
 							}
 							return null;
 						}
 					}
 			);
 			if ( extraLazyExistenceCheck != null ) {
 				return extraLazyExistenceCheck;
 			}
 		}
 		return null;
 	}
 
 	protected static final Object UNKNOWN = new MarkerObject( "UNKNOWN" );
 
 	protected Object readElementByIndex(final Object index) {
 		if ( !initialized ) {
 			class ExtraLazyElementByIndexReader implements LazyInitializationWork {
 				private boolean isExtraLazy;
 				private Object element;
 
 				@Override
 				public Object doWork() {
 					CollectionEntry entry = session.getPersistenceContext().getCollectionEntry( AbstractPersistentCollection.this );
 					CollectionPersister persister = entry.getLoadedPersister();
 					isExtraLazy = persister.isExtraLazy();
 					if ( isExtraLazy ) {
 						if ( hasQueuedOperations() ) {
 							session.flush();
 						}
 						element = persister.getElementByIndex( entry.getLoadedKey(), index, session, owner );
 					}
 					else {
 						read();
 					}
 					return null;
 				}
 			}
 
 			ExtraLazyElementByIndexReader reader = new ExtraLazyElementByIndexReader();
 			//noinspection unchecked
 			withTemporarySessionIfNeeded( reader );
 			if ( reader.isExtraLazy ) {
 				return reader.element;
 			}
 		}
 		return UNKNOWN;
 
 	}
 
 	protected int getCachedSize() {
 		return cachedSize;
 	}
 
 	private boolean isConnectedToSession() {
 		return session != null &&
 				session.isOpen() &&
 				session.getPersistenceContext().containsCollection( this );
 	}
 
 	/**
 	 * Called by any writer method of the collection interface
 	 */
 	protected final void write() {
 		initialize( true );
 		dirty();
 	}
 
 	/**
 	 * Is this collection in a state that would allow us to
 	 * "queue" operations?
 	 */
 	@SuppressWarnings({"JavaDoc"})
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
 	@SuppressWarnings({"JavaDoc"})
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
 	@SuppressWarnings({"JavaDoc"})
 	protected boolean isClearQueueEnabled() {
 		return !initialized &&
 				isConnectedToSession() &&
 				isInverseCollectionNoOrphanDelete();
 	}
 
 	/**
 	 * Is this the "inverse" end of a bidirectional association?
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	private boolean isInverseCollection() {
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( this );
 		return ce != null && ce.getLoadedPersister().isInverse();
 	}
 
 	/**
 	 * Is this the "inverse" end of a bidirectional association with
 	 * no orphan delete enabled?
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	private boolean isInverseCollectionNoOrphanDelete() {
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( this );
 		return ce != null &&
 				ce.getLoadedPersister().isInverse() &&
 				!ce.getLoadedPersister().hasOrphanDelete();
 	}
 
 	/**
 	 * Is this the "inverse" end of a bidirectional one-to-many, or
 	 * of a collection with no orphan delete?
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	private boolean isInverseOneToManyOrNoOrphanDelete() {
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( this );
 		return ce != null && ce.getLoadedPersister().isInverse() && (
 				ce.getLoadedPersister().isOneToMany() ||
 						!ce.getLoadedPersister().hasOrphanDelete()
 		);
 	}
 
 	/**
 	 * Queue an addition
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	protected final void queueOperation(DelayedOperation operation) {
 		if ( operationQueue == null ) {
 			operationQueue = new ArrayList<DelayedOperation>( 10 );
 		}
 		operationQueue.add( operation );
 		dirty = true; //needed so that we remove this collection from the second-level cache
 	}
 
 	/**
 	 * After reading all existing elements from the database,
 	 * add the queued elements to the underlying collection.
 	 */
 	protected final void performQueuedOperations() {
 		for ( DelayedOperation operation : operationQueue ) {
 			operation.operate();
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
 		operationQueue = null;
 		cachedSize = -1;
 		clearDirty();
 	}
 
 	/**
 	 * Not called by Hibernate, but used by non-JDK serialization,
 	 * eg. SOAP libraries.
 	 */
 	public AbstractPersistentCollection() {
 	}
 
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
 		if ( operationQueue != null ) {
 			performQueuedOperations();
 			operationQueue = null;
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
 	 *
 	 * @param writing currently obsolete
 	 *
 	 * @throws LazyInitializationException if we cannot initialize
 	 */
 	protected final void initialize(final boolean writing) {
 		if ( initialized ) {
 			return;
 		}
 
 		withTemporarySessionIfNeeded(
 				new LazyInitializationWork<Object>() {
 					@Override
 					public Object doWork() {
 						session.initializeCollection( AbstractPersistentCollection.this, writing );
 						return null;
 					}
 				}
 		);
 	}
 
 	private void throwLazyInitializationExceptionIfNotConnected() {
 		if ( !isConnectedToSession() ) {
 			throwLazyInitializationException( "no session or session was closed" );
 		}
 		if ( !session.isConnected() ) {
 			throwLazyInitializationException( "session is disconnected" );
 		}
 	}
 
 	private void throwLazyInitializationException(String message) {
 		throw new LazyInitializationException(
 				"failed to lazily initialize a collection" +
 						(role == null ? "" : " of role: " + role) +
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
 	 *
 	 * @return true if this was currently associated with the given session
 	 */
 	public final boolean unsetSession(SessionImplementor currentSession) {
 		prepareForPossibleSpecialSpecjInitialization();
 		if ( currentSession == this.session ) {
 			this.session = null;
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	protected void prepareForPossibleSpecialSpecjInitialization() {
 		if ( session != null ) {
 			specjLazyLoad = session.getFactory().getSettings().isInitializeLazyStateOutsideTransactionsEnabled();
 
 			if ( specjLazyLoad && sessionFactoryUuid == null ) {
 				try {
 					sessionFactoryUuid = (String) session.getFactory().getReference().get( "uuid" ).getContent();
 				}
 				catch (NamingException e) {
 					//not much we can do if this fails...
 				}
 			}
 		}
 	}
 
 
 	/**
 	 * Associate the collection with the given session.
 	 *
 	 * @return false if the collection was already associated with the session
 	 *
 	 * @throws HibernateException if the collection was already associated
 	 * with another open session
 	 */
 	public final boolean setCurrentSession(SessionImplementor session) throws HibernateException {
 		if ( session == this.session ) {
 			return false;
 		}
 		else {
 			if ( isConnectedToSession() ) {
 				CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( this );
 				if ( ce == null ) {
 					throw new HibernateException(
 							"Illegal attempt to associate a collection with two open sessions"
 					);
 				}
 				else {
 					throw new HibernateException(
 							"Illegal attempt to associate a collection with two open sessions: " +
 									MessageHelper.collectionInfoString(
-											ce.getLoadedPersister(),
-											ce.getLoadedKey(),
-											session.getFactory()
+											ce.getLoadedPersister(), this,
+											ce.getLoadedKey(), session
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
 		if ( !initialized ) {
 			if ( initializing ) {
 				throw new AssertionFailure( "force initialize loading collection" );
 			}
 			if ( session == null ) {
 				throw new HibernateException( "collection is not associated with any session" );
 			}
 			if ( !session.isConnected() ) {
 				throw new HibernateException( "disconnected session" );
 			}
 			session.initializeCollection( this, false );
 		}
 	}
 
 
 	/**
 	 * Get the current snapshot from the session
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	protected final Serializable getSnapshot() {
 		return session.getPersistenceContext().getSnapshot( this );
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
 		return operationQueue != null;
 	}
 
 	/**
 	 * Iterate the "queued" additions
 	 */
 	public final Iterator queuedAdditionIterator() {
 		if ( hasQueuedOperations() ) {
 			return new Iterator() {
 				int i = 0;
 
 				public Object next() {
 					return operationQueue.get( i++ ).getAddedInstance();
 				}
 
 				public boolean hasNext() {
 					return i < operationQueue.size();
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
 	@SuppressWarnings({"unchecked"})
 	public final Collection getQueuedOrphans(String entityName) {
 		if ( hasQueuedOperations() ) {
 			Collection additions = new ArrayList( operationQueue.size() );
 			Collection removals = new ArrayList( operationQueue.size() );
 			for ( DelayedOperation operation : operationQueue ) {
 				additions.add( operation.getAddedInstance() );
 				removals.add( operation.getOrphan() );
 			}
 			return getOrphans( removals, additions, entityName, session );
 		}
 		else {
 			return Collections.EMPTY_LIST;
 		}
 	}
 
 	/**
 	 * Called before inserting rows, to ensure that any surrogate keys
 	 * are fully generated
 	 */
 	public void preInsert(CollectionPersister persister) throws HibernateException {
 	}
 
 	/**
 	 * Called after inserting a row, to fetch the natively generated id
 	 */
 	public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {
 	}
 
 	/**
 	 * get all "orphaned" elements
 	 */
 	public abstract Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException;
 
 	/**
 	 * Get the current session
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	public final SessionImplementor getSession() {
 		return session;
 	}
 
 	protected final class IteratorProxy implements Iterator {
 		protected final Iterator itr;
 
 		public IteratorProxy(Iterator itr) {
 			this.itr = itr;
 		}
 
 		public boolean hasNext() {
 			return itr.hasNext();
 		}
 
 		public Object next() {
 			return itr.next();
 		}
 
 		public void remove() {
 			write();
 			itr.remove();
 		}
 
 	}
 
 	protected final class ListIteratorProxy implements ListIterator {
 		protected final ListIterator itr;
 
 		public ListIteratorProxy(ListIterator itr) {
 			this.itr = itr;
 		}
 
 		@SuppressWarnings({"unchecked"})
 		public void add(Object o) {
 			write();
 			itr.add( o );
 		}
 
 		public boolean hasNext() {
 			return itr.hasNext();
 		}
 
 		public boolean hasPrevious() {
 			return itr.hasPrevious();
 		}
 
 		public Object next() {
 			return itr.next();
 		}
 
 		public int nextIndex() {
 			return itr.nextIndex();
 		}
 
 		public Object previous() {
 			return itr.previous();
 		}
 
 		public int previousIndex() {
 			return itr.previousIndex();
 		}
 
 		public void remove() {
 			write();
 			itr.remove();
 		}
 
 		@SuppressWarnings({"unchecked"})
 		public void set(Object o) {
 			write();
 			itr.set( o );
 		}
 
 	}
 
 	protected class SetProxy implements java.util.Set {
 		protected final Collection set;
 
 		public SetProxy(Collection set) {
 			this.set = set;
 		}
 
 		@SuppressWarnings({"unchecked"})
 		public boolean add(Object o) {
 			write();
 			return set.add( o );
 		}
 
 		@SuppressWarnings({"unchecked"})
 		public boolean addAll(Collection c) {
 			write();
 			return set.addAll( c );
 		}
 
 		public void clear() {
 			write();
 			set.clear();
 		}
 
 		public boolean contains(Object o) {
 			return set.contains( o );
 		}
 
 		public boolean containsAll(Collection c) {
 			return set.containsAll( c );
 		}
 
 		public boolean isEmpty() {
 			return set.isEmpty();
 		}
 
 		public Iterator iterator() {
 			return new IteratorProxy( set.iterator() );
 		}
 
 		public boolean remove(Object o) {
 			write();
 			return set.remove( o );
 		}
 
 		public boolean removeAll(Collection c) {
 			write();
 			return set.removeAll( c );
 		}
 
 		public boolean retainAll(Collection c) {
 			write();
 			return set.retainAll( c );
 		}
 
 		public int size() {
 			return set.size();
 		}
 
 		public Object[] toArray() {
 			return set.toArray();
 		}
 
 		@SuppressWarnings({"unchecked"})
 		public Object[] toArray(Object[] array) {
 			return set.toArray( array );
 		}
 
 	}
 
 	protected final class ListProxy implements java.util.List {
 		protected final List list;
 
 		public ListProxy(List list) {
 			this.list = list;
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public void add(int index, Object value) {
 			write();
 			list.add( index, value );
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public boolean add(Object o) {
 			write();
 			return list.add( o );
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public boolean addAll(Collection c) {
 			write();
 			return list.addAll( c );
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public boolean addAll(int i, Collection c) {
 			write();
 			return list.addAll( i, c );
 		}
 
 		@Override
 		public void clear() {
 			write();
 			list.clear();
 		}
 
 		@Override
 		public boolean contains(Object o) {
 			return list.contains( o );
 		}
 
 		@Override
 		public boolean containsAll(Collection c) {
 			return list.containsAll( c );
 		}
 
 		@Override
 		public Object get(int i) {
 			return list.get( i );
 		}
 
 		@Override
 		public int indexOf(Object o) {
 			return list.indexOf( o );
 		}
 
 		@Override
 		public boolean isEmpty() {
 			return list.isEmpty();
 		}
 
 		@Override
 		public Iterator iterator() {
 			return new IteratorProxy( list.iterator() );
 		}
 
 		@Override
 		public int lastIndexOf(Object o) {
 			return list.lastIndexOf( o );
 		}
 
 		@Override
 		public ListIterator listIterator() {
 			return new ListIteratorProxy( list.listIterator() );
 		}
 
 		@Override
 		public ListIterator listIterator(int i) {
 			return new ListIteratorProxy( list.listIterator( i ) );
 		}
 
 		@Override
 		public Object remove(int i) {
 			write();
 			return list.remove( i );
 		}
 
 		@Override
 		public boolean remove(Object o) {
 			write();
 			return list.remove( o );
 		}
 
 		@Override
 		public boolean removeAll(Collection c) {
 			write();
 			return list.removeAll( c );
 		}
 
 		@Override
 		public boolean retainAll(Collection c) {
 			write();
 			return list.retainAll( c );
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public Object set(int i, Object o) {
 			write();
 			return list.set( i, o );
 		}
 
 		@Override
 		public int size() {
 			return list.size();
 		}
 
 		@Override
 		public List subList(int i, int j) {
 			return list.subList( i, j );
 		}
 
 		@Override
 		public Object[] toArray() {
 			return list.toArray();
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public Object[] toArray(Object[] array) {
 			return list.toArray( array );
 		}
 
 	}
 
 	/**
 	 * Contract for operations which are part of a collection's operation queue.
 	 */
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
 	@SuppressWarnings({"JavaDoc", "unchecked"})
 	protected static Collection getOrphans(
 			Collection oldElements,
 			Collection currentElements,
 			String entityName,
 			SessionImplementor session) throws HibernateException {
 
 		// short-circuit(s)
 		if ( currentElements.size() == 0 ) {
 			return oldElements; // no new elements, the old list contains only Orphans
 		}
 		if ( oldElements.size() == 0 ) {
 			return oldElements; // no old elements, so no Orphans neither
 		}
 
 		final EntityPersister entityPersister = session.getFactory().getEntityPersister( entityName );
 		final Type idType = entityPersister.getIdentifierType();
 
 		// create the collection holding the Orphans
 		Collection res = new ArrayList();
 
 		// collect EntityIdentifier(s) of the *current* elements - add them into a HashSet for fast access
 		java.util.Set currentIds = new HashSet();
 		java.util.Set currentSaving = new IdentitySet();
 		for ( Object current : currentElements ) {
 			if ( current != null && ForeignKeys.isNotTransient( entityName, current, null, session ) ) {
 				EntityEntry ee = session.getPersistenceContext().getEntry( current );
 				if ( ee != null && ee.getStatus() == Status.SAVING ) {
 					currentSaving.add( current );
 				}
 				else {
 					Serializable currentId = ForeignKeys.getEntityIdentifierIfNotUnsaved(
 							entityName,
 							current,
 							session
 					);
 					currentIds.add( new TypedValue( idType, currentId, entityPersister.getEntityMode() ) );
 				}
 			}
 		}
 
 		// iterate over the *old* list
 		for ( Object old : oldElements ) {
 			if ( !currentSaving.contains( old ) ) {
 				Serializable oldId = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, old, session );
 				if ( !currentIds.contains( new TypedValue( idType, oldId, entityPersister.getEntityMode() ) ) ) {
 					res.add( old );
 				}
 			}
 		}
 
 		return res;
 	}
 
 	public static void identityRemove(
 			Collection list,
 			Object object,
 			String entityName,
 			SessionImplementor session) throws HibernateException {
 
 		if ( object != null && ForeignKeys.isNotTransient( entityName, object, null, session ) ) {
 			final EntityPersister entityPersister = session.getFactory().getEntityPersister( entityName );
 			Type idType = entityPersister.getIdentifierType();
 
 			Serializable idOfCurrent = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, object, session );
 			Iterator itr = list.iterator();
 			while ( itr.hasNext() ) {
 				Serializable idOfOld = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, itr.next(), session );
 				if ( idType.isEqual( idOfCurrent, idOfOld, session.getFactory() ) ) {
 					itr.remove();
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/Collections.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/Collections.java
index 16311723da..2c6a840ba3 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/Collections.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Collections.java
@@ -1,268 +1,267 @@
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
 
 import java.io.Serializable;
 
-import org.jboss.logging.Logger;
-
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.CollectionType;
+import org.jboss.logging.Logger;
 
 /**
  * Implements book-keeping for the collection persistence by reachability algorithm
  *
  * @author Gavin King
  */
 public final class Collections {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Collections.class.getName());
 
 	private Collections() {
 	}
 
 	/**
 	 * record the fact that this collection was dereferenced
 	 *
 	 * @param coll The collection to be updated by un-reachability.
 	 */
 	@SuppressWarnings( {"JavaDoc"})
 	public static void processUnreachableCollection(PersistentCollection coll, SessionImplementor session) {
 		if ( coll.getOwner()==null ) {
 			processNeverReferencedCollection(coll, session);
 		}
 		else {
 			processDereferencedCollection(coll, session);
 		}
 	}
 
 	private static void processDereferencedCollection(PersistentCollection coll, SessionImplementor session) {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		CollectionEntry entry = persistenceContext.getCollectionEntry(coll);
 		final CollectionPersister loadedPersister = entry.getLoadedPersister();
 
 		if ( LOG.isDebugEnabled() && loadedPersister != null ) {
 			LOG.debugf(
 					"Collection dereferenced: %s",
-					MessageHelper.collectionInfoString(
-							loadedPersister,
-							entry.getLoadedKey(),
-							session.getFactory()
+					MessageHelper.collectionInfoString( loadedPersister, 
+							coll, entry.getLoadedKey(), session
 					)
 			);
 		}
 
 		// do a check
 		boolean hasOrphanDelete = loadedPersister != null && loadedPersister.hasOrphanDelete();
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
 		prepareCollectionForUpdate( coll, entry, session.getFactory() );
 
 	}
 
 	private static void processNeverReferencedCollection(PersistentCollection coll, SessionImplementor session)
 	throws HibernateException {
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		CollectionEntry entry = persistenceContext.getCollectionEntry(coll);
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Found collection with unloaded owner: %s",
-					MessageHelper.collectionInfoString( entry.getLoadedPersister(), entry.getLoadedKey(), session.getFactory() ) );
+					MessageHelper.collectionInfoString( 
+							entry.getLoadedPersister(), coll,
+							entry.getLoadedKey(), session ) );
 		}
 
 		entry.setCurrentPersister( entry.getLoadedPersister() );
 		entry.setCurrentKey( entry.getLoadedKey() );
 
 		prepareCollectionForUpdate( coll, entry, session.getFactory() );
 
 	}
 
     /**
      * Initialize the role of the collection.
      *
      * @param collection The collection to be updated by reachability.
      * @param type The type of the collection.
      * @param entity The owner of the collection.
 	 * @param session The session from which this request originates
      */
 	public static void processReachableCollection(
 			PersistentCollection collection,
 	        CollectionType type,
 	        Object entity,
 	        SessionImplementor session) {
 
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
-                                                        MessageHelper.collectionInfoString(persister, ce.getCurrentKey(), factory),
-                                                        MessageHelper.collectionInfoString(ce.getLoadedPersister(),
+                                                        MessageHelper.collectionInfoString(persister, collection, ce.getCurrentKey(), session),
+                                                        MessageHelper.collectionInfoString(ce.getLoadedPersister(), collection, 
                                                                                            ce.getLoadedKey(),
-                                                                                           factory));
+                                                                                           session));
             else LOG.debugf("Collection found: %s, was: %s (uninitialized)",
-                            MessageHelper.collectionInfoString(persister, ce.getCurrentKey(), factory),
-                            MessageHelper.collectionInfoString(ce.getLoadedPersister(), ce.getLoadedKey(), factory));
+                            MessageHelper.collectionInfoString(persister, collection, ce.getCurrentKey(), session),
+                            MessageHelper.collectionInfoString(ce.getLoadedPersister(), collection, ce.getLoadedKey(), session));
         }
 
 		prepareCollectionForUpdate( collection, ce, factory );
 
 	}
 
 	/**
 	 * 1. record the collection role that this collection is referenced by
 	 * 2. decide if the collection needs deleting/creating/updating (but
 	 *	don't actually schedule the action yet)
 	 */
 	@SuppressWarnings( {"JavaDoc"})
 	private static void prepareCollectionForUpdate(
 			PersistentCollection collection,
 	        CollectionEntry entry,
 	        SessionFactoryImplementor factory) {
 
 		if ( entry.isProcessed() ) {
 			throw new AssertionFailure( "collection was processed twice by flush()" );
 		}
 		entry.setProcessed( true );
 
 		final CollectionPersister loadedPersister = entry.getLoadedPersister();
 		final CollectionPersister currentPersister = entry.getCurrentPersister();
 		if ( loadedPersister != null || currentPersister != null ) {					// it is or was referenced _somewhere_
 
 			boolean ownerChanged = loadedPersister != currentPersister ||				// if either its role changed,
 			                       !currentPersister
 					                       .getKeyType().isEqual(                       // or its key changed
 													entry.getLoadedKey(),
 			                                        entry.getCurrentKey(),
 			                                        factory
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
 					entry.setDorecreate( true );	// we will need to create new entries
 				}
 
 				if ( loadedPersister != null ) {
 					entry.setDoremove( true );		// we will need to remove ye olde entries
 					if ( entry.isDorecreate() ) {
 						LOG.trace( "Forcing collection initialization" );
 						collection.forceInitialization();
 					}
 				}
 			}
 			else if ( collection.isDirty() ) {
 				// the collection's elements have changed
 				entry.setDoupdate( true );
 			}
 
 		}
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
index 72a5e68898..23f9755feb 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
@@ -1,359 +1,358 @@
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
 package org.hibernate.engine.loading.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Set;
 
-import org.jboss.logging.Logger;
-
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.entry.CollectionCacheEntry;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
+import org.jboss.logging.Logger;
 
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
 					return null; // ignore this row of results! Note the early exit
 				}
 				LOG.trace( "Collection not yet initialized; initializing" );
 			}
 			else {
 				Object owner = loadContexts.getPersistenceContext().getCollectionOwner( key, persister );
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
 			if ( lce == null ) {
 				LOG.loadingCollectionKeyNotFound( collectionKey );
 			}
 			else if ( lce.getResultSet() == resultSet && lce.getPersister() == persister ) {
 				if ( matches == null ) {
 					matches = new ArrayList();
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
 			if ( LOG.isDebugEnabled()) LOG.debugf( "No collections were found in result set for role: %s", persister.getRole() );
 			return;
 		}
 
 		final int count = matchedCollectionEntries.size();
 		if ( LOG.isDebugEnabled()) LOG.debugf("%s collections were found in result set for role: %s", count, persister.getRole());
 
 		for ( int i = 0; i < count; i++ ) {
 			LoadingCollectionEntry lce = ( LoadingCollectionEntry ) matchedCollectionEntries.get( i );
 			endLoadingCollection( lce, persister );
 		}
 
 		if ( LOG.isDebugEnabled() ) LOG.debugf( "%s collections initialized for role: %s", count, persister.getRole() );
 	}
 
 	private void endLoadingCollection(LoadingCollectionEntry lce, CollectionPersister persister) {
 		LOG.tracev( "Ending loading collection [{0}]", lce );
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 
 		boolean hasNoQueuedAdds = lce.getCollection().endRead(); // warning: can cause a recursive calls! (proxy initialization)
 
 		if ( persister.getCollectionType().hasHolder() ) {
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
 		if ( addToCache ) {
 			addCollectionToCache( lce, persister );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Collection fully initialized: %s",
-					MessageHelper.collectionInfoString(persister, lce.getKey(), session.getFactory())
+					MessageHelper.collectionInfoString(persister, lce.getCollection(), lce.getKey(), session)
 			);
 		}
 		if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 			session.getFactory().getStatisticsImplementor().loadCollection(persister.getRole());
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
 
 		if ( LOG.isDebugEnabled() ) {
-			LOG.debugf( "Caching collection: %s", MessageHelper.collectionInfoString( persister, lce.getKey(), factory ) );
+			LOG.debugf( "Caching collection: %s", MessageHelper.collectionInfoString( persister, lce.getCollection(), lce.getKey(), session ) );
 		}
 
 		if ( !session.getEnabledFilters().isEmpty() && persister.isAffectedByEnabledFilters( session ) ) {
 			// some filters affecting the collection are enabled on the session, so do not do the put into the cache.
 			LOG.debug( "Refusing to add to cache due to enabled filters" );
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
-									MessageHelper.collectionInfoString( persister, lce.getKey(), factory ) +
+									MessageHelper.collectionInfoString( persister, lce.getCollection(), lce.getKey(), session ) +
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java
index ba56e22870..5a6e4a6cf2 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java
@@ -1,314 +1,309 @@
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
 package org.hibernate.engine.loading.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.util.HashMap;
 import java.util.IdentityHashMap;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * Maps {@link ResultSet result-sets} to specific contextual data related to processing that result set
  * <p/>
  * Implementation note: internally an {@link IdentityMap} is used to maintain the mappings mainly because I'd
  * rather not be dependent upon potentially bad {@link Object#equals} and {@link Object#hashCode} implementations on
  * the JDBC result sets
  * <p/>
  * Considering the JDBC-redesign work, would further like this contextual info not mapped separately, but available
  * based on the result set being processed.  This would also allow maintaining a single mapping as we could reliably
  * get notification of the result-set closing...
  *
  * @author Steve Ebersole
  */
 public class LoadContexts {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, LoadContexts.class.getName());
 
 	private final PersistenceContext persistenceContext;
 	private Map<ResultSet,CollectionLoadContext> collectionLoadContexts;
 	private Map<ResultSet,EntityLoadContext> entityLoadContexts;
 
 	private Map<CollectionKey,LoadingCollectionEntry> xrefLoadingCollectionEntries;
 
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
 			CollectionLoadContext collectionLoadContext = collectionLoadContexts.remove( resultSet );
 			collectionLoadContext.cleanup();
 		}
 		if ( entityLoadContexts != null ) {
 			EntityLoadContext entityLoadContext = entityLoadContexts.remove( resultSet );
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
 			for ( CollectionLoadContext collectionLoadContext : collectionLoadContexts.values() ) {
 				LOG.failSafeCollectionsCleanup( collectionLoadContext );
 				collectionLoadContext.cleanup();
 			}
 			collectionLoadContexts.clear();
 		}
 		if ( entityLoadContexts != null ) {
 			for ( EntityLoadContext entityLoadContext : entityLoadContexts.values() ) {
 				LOG.failSafeEntitiesCleanup( entityLoadContext );
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
 		if ( collectionLoadContexts == null ) {
 			collectionLoadContexts = new IdentityHashMap<ResultSet, CollectionLoadContext>( 8 );
 		}
 		else {
 			context = collectionLoadContexts.get(resultSet);
 		}
 		if ( context == null ) {
 			LOG.tracev( "Constructing collection load context for result set [{0}]", resultSet );
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
 		LoadingCollectionEntry lce = locateLoadingCollectionEntry( new CollectionKey( persister, ownerKey ) );
 		if ( lce != null ) {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracef(
 						"Returning loading collection: %s",
 						MessageHelper.collectionInfoString( persister, ownerKey, getSession().getFactory() )
 				);
 			}
 			return lce.getCollection();
 		}
-		// TODO : should really move this log statement to CollectionType, where this is used from...
-		if ( LOG.isTraceEnabled() ) {
-			LOG.tracef( "Creating collection wrapper: %s",
-					MessageHelper.collectionInfoString( persister, ownerKey, getSession().getFactory() ) );
-		}
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
 			xrefLoadingCollectionEntries = new HashMap<CollectionKey,LoadingCollectionEntry>();
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
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	Map getLoadingCollectionXRefs() {
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
 		if ( xrefLoadingCollectionEntries == null ) {
 			return null;
 		}
 		LOG.tracev( "Attempting to locate loading collection entry [{0}] in any result-set context", key );
 		LoadingCollectionEntry rtn = xrefLoadingCollectionEntries.get( key );
 		if ( rtn == null ) {
 			LOG.tracev( "Collection [{0}] not located in load context", key );
 		}
 		else {
 			LOG.tracev( "Collection [{0}] located in load context", key );
 		}
 		return rtn;
 	}
 
 	/*package*/void cleanupCollectionXRefs(Set<CollectionKey> entryKeys) {
 		for ( CollectionKey entryKey : entryKeys ) {
 			xrefLoadingCollectionEntries.remove( entryKey );
 		}
 	}
 
 
 	// Entity load contexts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// 	* currently, not yet used...
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public EntityLoadContext getEntityLoadContext(ResultSet resultSet) {
 		EntityLoadContext context = null;
 		if ( entityLoadContexts == null ) {
 			entityLoadContexts = new IdentityHashMap<ResultSet, EntityLoadContext>( 8 );
 		}
 		else {
 			context = entityLoadContexts.get( resultSet );
 		}
 		if ( context == null ) {
 			context = new EntityLoadContext( this, resultSet );
 			entityLoadContexts.put( resultSet, context );
 		}
 		return context;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java
index 69ce7ce476..033682d5df 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java
@@ -1,148 +1,146 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 
-import org.jboss.logging.Logger;
-
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.entry.CollectionCacheEntry;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.spi.InitializeCollectionEvent;
 import org.hibernate.event.spi.InitializeCollectionEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
+import org.jboss.logging.Logger;
 
 /**
  * @author Gavin King
  */
 public class DefaultInitializeCollectionEventListener implements InitializeCollectionEventListener {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, DefaultInitializeCollectionEventListener.class.getName() );
 
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
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Initializing collection {0}",
-						MessageHelper.collectionInfoString( ce.getLoadedPersister(), ce.getLoadedKey(),
-						source.getFactory() ) );
+						MessageHelper.collectionInfoString( ce.getLoadedPersister(), collection, ce.getLoadedKey(), source ) );
 			}
 
 			LOG.trace( "Checking second-level cache" );
 			final boolean foundInCache = initializeCollectionFromCache(
 					ce.getLoadedKey(),
 					ce.getLoadedPersister(),
 					collection,
 					source
 				);
 
 			if ( foundInCache ) {
 				LOG.trace( "Collection initialized from cache" );
 			}
 			else {
 				LOG.trace( "Collection not cached" );
 				ce.getLoadedPersister().initialize( ce.getLoadedKey(), source );
 				LOG.trace( "Collection initialized" );
 
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
 
 		if ( !source.getLoadQueryInfluencers().getEnabledFilters().isEmpty() && persister.isAffectedByEnabledFilters( source ) ) {
 			LOG.trace( "Disregarding cached version (if any) of collection due to enabled filters" );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/EvictVisitor.java b/hibernate-core/src/main/java/org/hibernate/event/internal/EvictVisitor.java
index dcc18fa019..14a0f6ffae 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/EvictVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/EvictVisitor.java
@@ -1,92 +1,93 @@
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
 package org.hibernate.event.internal;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.CollectionType;
 
 /**
  * Evict any collections referenced by the object from the session cache.
  * This will NOT pick up any collections that were dereferenced, so they
  * will be deleted (suboptimal but not exactly incorrect).
  *
  * @author Gavin King
  */
 public class EvictVisitor extends AbstractVisitor {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, EvictVisitor.class.getName() );
 
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
 		if ( type.hasHolder() ) {
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
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Evicting collection: %s",
 					MessageHelper.collectionInfoString( ce.getLoadedPersister(),
+							collection,
 							ce.getLoadedKey(),
-							getSession().getFactory() ) );
+							getSession() ) );
 		}
 		if ( ce.getLoadedPersister() != null && ce.getLoadedKey() != null ) {
 			//TODO: is this 100% correct?
 			getSession().getPersistenceContext().getCollectionsByKey().remove(
 					new CollectionKey( ce.getLoadedPersister(), ce.getLoadedKey() )
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 4bddd04438..fc77d3ff05 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -206,1731 +206,1731 @@ public abstract class AbstractCollectionPersister
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
 					(CacheEntryStructure) new StructuredMapCacheEntry() :
 					(CacheEntryStructure) new StructuredCollectionCacheEntry();
 		}
 		else {
 			cacheEntryStructure = new UnstructuredCacheEntry();
 		}
 
 		dialect = factory.getDialect();
 		sqlExceptionHelper = factory.getSQLExceptionHelper();
 		collectionType = collection.getCollectionType();
 		role = collection.getRole();
 		entityName = collection.getOwnerEntityName();
 		ownerPersister = factory.getEntityPersister( entityName );
 		queryLoaderName = collection.getLoaderName();
 		nodeName = collection.getNodeName();
 		isMutable = collection.isMutable();
 
 		Table table = collection.getCollectionTable();
 		fetchMode = collection.getElement().getFetchMode();
 		elementType = collection.getElement().getType();
 		// isSet = collection.isSet();
 		// isSorted = collection.isSorted();
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
 				Template.renderWhereStringTemplate( sqlWhereString, dialect, factory.getSqlFunctionRegistry() ) :
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
 			keyColumnNames[k] = col.getQuotedName( dialect );
 			keyColumnAliases[k] = col.getAlias( dialect, collection.getOwner().getRootTable() );
 			k++;
 		}
 
 		// unquotedKeyColumnNames = StringHelper.unQuote(keyColumnAliases);
 
 		// ELEMENT
 
 		String elemNode = collection.getElementNodeName();
 		if ( elementType.isEntityType() ) {
 			String entityName = ( (EntityType) elementType ).getAssociatedEntityName();
 			elementPersister = factory.getEntityPersister( entityName );
 			if ( elemNode == null ) {
 				elemNode = cfg.getClassMapping( entityName ).getNodeName();
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
 			elementColumnAliases[j] = selectable.getAlias( dialect, table );
 			if ( selectable.isFormula() ) {
 				Formula form = (Formula) selectable;
 				elementFormulaTemplates[j] = form.getTemplate( dialect, factory.getSqlFunctionRegistry() );
 				elementFormulas[j] = form.getFormula();
 			}
 			else {
 				Column col = (Column) selectable;
 				elementColumnNames[j] = col.getQuotedName( dialect );
 				elementColumnWriters[j] = col.getWriteExpr();
 				elementColumnReaders[j] = col.getReadExpr( dialect );
 				elementColumnReaderTemplates[j] = col.getTemplate( dialect, factory.getSqlFunctionRegistry() );
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
 
 		// workaround, for backward compatibility of sets with no
 		// not-null columns, assume all columns are used in the
 		// row locator SQL
 		if ( !hasNotNullableColumns ) {
 			Arrays.fill( elementColumnIsInPrimaryKey, true );
 		}
 
 		// INDEX AND ROW SELECT
 
 		hasIndex = collection.isIndexed();
 		if ( hasIndex ) {
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
 				indexColumnAliases[i] = s.getAlias( dialect );
 				if ( s.isFormula() ) {
 					Formula indexForm = (Formula) s;
 					indexFormulaTemplates[i] = indexForm.getTemplate( dialect, factory.getSqlFunctionRegistry() );
 					indexFormulas[i] = indexForm.getFormula();
 					hasFormula = true;
 				}
 				else {
 					Column indexCol = (Column) s;
 					indexColumnNames[i] = indexCol.getQuotedName( dialect );
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
 		if ( hasIdentifier ) {
 			if ( collection.isOneToMany() ) {
 				throw new MappingException( "one-to-many collections with identifiers are not supported" );
 			}
 			IdentifierCollection idColl = (IdentifierCollection) collection;
 			identifierType = idColl.getIdentifier().getType();
 			iter = idColl.getIdentifier().getColumnIterator();
 			Column col = (Column) iter.next();
 			identifierColumnName = col.getQuotedName( dialect );
 			identifierColumnAlias = col.getAlias( dialect );
 			// unquotedIdentifierColumnName = identifierColumnAlias;
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
 			// unquotedIdentifierColumnName = null;
 			identifierGenerator = null;
 		}
 
 		// GENERATE THE SQL:
 
 		// sqlSelectString = sqlSelectString();
 		// sqlSelectRowString = sqlSelectRowString();
 
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
 
 		sqlSelectSizeString = generateSelectSizeString( collection.isIndexed() && !collection.isMap() );
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
 			elementClass = null; // elementType.returnedClass();
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
 			if ( elementPersister instanceof PropertyMapping ) { // not all classpersisters implement PropertyMapping!
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
 			orderByTranslation = Template.translateOrderBy(
 					collection.getOrderBy(),
 					new ColumnMapperImpl(),
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			orderByTranslation = null;
 		}
 
 		// Handle any filters applied to this collection
 		filterHelper = new FilterHelper( collection.getFilters(), factory);
 
 		// Handle any filters applied to this collection for many-to-many
 		manyToManyFilterHelper = new FilterHelper( collection.getManyToManyFilters(), factory);
 		manyToManyWhereString = StringHelper.isNotEmpty( collection.getManyToManyWhere() ) ?
 				"( " + collection.getManyToManyWhere() + ")" :
 				null;
 		manyToManyWhereTemplate = manyToManyWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( manyToManyWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		hasManyToManyOrder = collection.getManyToManyOrdering() != null;
 		if ( hasManyToManyOrder ) {
 			manyToManyOrderByTranslation = Template.translateOrderBy(
 					collection.getManyToManyOrdering(),
 					new ColumnMapperImpl(),
 					factory,
 					dialect,
 					factory.getSqlFunctionRegistry()
 			);
 		}
 		else {
 			manyToManyOrderByTranslation = null;
 		}
 
 		initCollectionPropertyMap();
 	}
 
 	private class ColumnMapperImpl implements ColumnMapper {
 		@Override
 		public SqlValueReference[] map(String reference) {
 			final String[] columnNames;
 			final String[] formulaTemplates;
 
 			// handle the special "$element$" property name...
 			if ( "$element$".equals( reference ) ) {
 				columnNames = elementColumnNames;
 				formulaTemplates = elementFormulaTemplates;
 			}
 			else {
 				columnNames = elementPropertyMapping.toColumns( reference );
 				formulaTemplates = formulaTemplates( reference, columnNames.length );
 			}
 
 			final SqlValueReference[] result = new SqlValueReference[ columnNames.length ];
 			int i = 0;
 			for ( final String columnName : columnNames ) {
 				if ( columnName == null ) {
 					// if the column name is null, it indicates that this index in the property value mapping is
 					// actually represented by a formula.
 					final int propertyIndex = elementPersister.getEntityMetamodel().getPropertyIndex( reference );
 					final String formulaTemplate = formulaTemplates[i];
 					result[i] = new FormulaReference() {
 						@Override
 						public String getFormulaFragment() {
 							return formulaTemplate;
 						}
 					};
 				}
 				else {
 					result[i] = new ColumnReference() {
 						@Override
 						public String getColumnName() {
 							return columnName;
 						}
 					};
 				}
 				i++;
 			}
 			return result;
 		}
 	}
 
 	private String[] formulaTemplates(String reference, int expectedSize) {
 		try {
 			final int propertyIndex = elementPersister.getEntityMetamodel().getPropertyIndex( reference );
 			return  ( (Queryable) elementPersister ).getSubclassPropertyFormulaTemplateClosure()[propertyIndex];
 		}
 		catch (Exception e) {
 			return new String[expectedSize];
 		}
 	}
 
 	public void postInstantiate() throws MappingException {
 		initializer = queryLoaderName == null ?
 				createCollectionInitializer( LoadQueryInfluencers.NONE ) :
 				new NamedQueryCollectionInitializer( queryLoaderName, this );
 	}
 
 	protected void logStaticSQL() {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Static SQL for collection: %s", getRole() );
 			if ( getSQLInsertRowString() != null ) LOG.debugf( " Row insert: %s", getSQLInsertRowString() );
 			if ( getSQLUpdateRowString() != null ) LOG.debugf( " Row update: %s", getSQLUpdateRowString() );
 			if ( getSQLDeleteRowString() != null ) LOG.debugf( " Row delete: %s", getSQLDeleteRowString() );
 			if ( getSQLDeleteString() != null ) LOG.debugf( " One-shot delete: %s", getSQLDeleteString() );
 		}
 	}
 
 	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
 		getAppropriateInitializer( key, session ).initialize( key, session );
 	}
 
 	protected CollectionInitializer getAppropriateInitializer(Serializable key, SessionImplementor session) {
 		if ( queryLoaderName != null ) {
 			// if there is a user-specified loader, return that
 			// TODO: filters!?
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
 
 		if ( subselect == null ) {
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
 				? orderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
 				: "";
 	}
 
 	public String getManyToManyOrderByString(String alias) {
 		return hasManyToManyOrdering()
 				? manyToManyOrderByTranslation.injectAliases( new StandardOrderByAliasResolver( alias ) )
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
 	public Class getElementClass() { // needed by arrays
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
 		if ( baseIndex != 0 ) {
             index = (Integer)index - baseIndex;
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
 			throw new NullPointerException( "null key for collection: " + role ); // an assertion
 		}
 		getKeyType().nullSafeSet( st, key, i, session );
 		return i + keyColumnAliases.length;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElement(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getElementType().nullSafeSet( st, elt, i, elementColumnIsSettable, session );
 		return i + ArrayHelper.countTrue( elementColumnIsSettable );
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndex(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getIndexType().nullSafeSet( st, incrementIndexByBase( index ), i, indexColumnIsSettable, session );
 		return i + ArrayHelper.countTrue( indexColumnIsSettable );
 	}
 
 	protected Object incrementIndexByBase(Object index) {
 		if ( baseIndex != 0 ) {
             index = (Integer)index + baseIndex;
 		}
 		return index;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElementToWhere(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( elementIsPureFormula ) {
 			throw new AssertionFailure( "cannot use a formula-based element in the where condition" );
 		}
 		getElementType().nullSafeSet( st, elt, i, elementColumnIsInPrimaryKey, session );
 		return i + elementColumnAliases.length;
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndexToWhere(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( indexContainsFormula ) {
 			throw new AssertionFailure( "cannot use a formula-based index in the where condition" );
 		}
 		getIndexType().nullSafeSet( st, incrementIndexByBase( index ), i, session );
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
 		}
 		else {
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
 				.substring( 2 ); // strip leading ','
 	}
 
 	protected String generateSelectSizeString(boolean isIntegerIndexed) {
 		String selectValue = isIntegerIndexed ?
 				"max(" + getIndexColumnNames()[0] + ") + 1" : // lists, arrays
 				"count(" + getElementColumnNames()[0] + ")"; // sets, maps, bags
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addColumn( selectValue )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumn( "1" )
 				.toStatementString();
 	}
 
 	protected String generateSelectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumns( getElementColumnNames(), elementColumnAliases )
 				.addColumns( indexFormulas, indexColumnAliases )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByElementString() {
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getElementColumnNames(), "=?" )
 				.addCondition( elementFormulas, "=?" )
 				.addColumn( "1" )
 				.toStatementString();
 	}
 
 	protected SelectFragment generateSelectFragment(String alias, String columnSuffix) {
 		return new SelectFragment()
 				.setSuffix( columnSuffix )
 				.addColumns( alias, keyColumnNames, keyColumnAliases );
 	}
 
 	protected void appendElementColumns(SelectFragment frag, String elemAlias) {
 		for ( int i = 0; i < elementColumnIsSettable.length; i++ ) {
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
 			for ( int i = 0; i < indexColumnIsSettable.length; i++ ) {
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
 		return qualify( alias, indexColumnNames, indexFormulaTemplates );
 
 	}
 
 	public String[] getElementColumnNames(String alias) {
 		return qualify( alias, elementColumnNames, elementFormulaTemplates );
 	}
 
 	private static String[] qualify(String alias, String[] columnNames, String[] formulaTemplates) {
 		int span = columnNames.length;
 		String[] result = new String[span];
 		for ( int i = 0; i < span; i++ ) {
 			if ( columnNames[i] == null ) {
 				result[i] = StringHelper.replace( formulaTemplates[i], Template.TEMPLATE, alias );
 			}
 			else {
 				result[i] = StringHelper.qualify( alias, columnNames[i] );
 			}
 		}
 		return result;
 	}
 
 	public String[] getElementColumnNames() {
 		return elementColumnNames; // TODO: something with formulas...
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
 
 	public boolean isInverse() {
 		return isInverse;
 	}
 
 	public String getTableName() {
 		return qualifiedTableName;
 	}
 
 	private BasicBatchKey removeBatchKey;
 
 	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Deleting collection: %s",
 						MessageHelper.collectionInfoString( this, id, getFactory() ) );
 			}
 
 			// Remove all the old entries
 
 			try {
 				int offset = 1;
 				PreparedStatement st = null;
 				Expectation expectation = Expectations.appropriateExpectation( getDeleteAllCheckStyle() );
 				boolean callable = isDeleteAllCallable();
 				boolean useBatch = expectation.canBeBatched();
 				String sql = getSQLDeleteString();
 				if ( useBatch ) {
 					if ( removeBatchKey == null ) {
 						removeBatchKey = new BasicBatchKey(
 								getRole() + "#REMOVE",
 								expectation
 								);
 					}
 					st = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getBatch( removeBatchKey )
 							.getBatchStatement( sql, callable );
 				}
 				else {
 					st = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql, callable );
 				}
 
 				try {
 					offset += expectation.prepare( st );
 
 					writeKey( st, id, offset, session );
 					if ( useBatch ) {
 						session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getBatch( removeBatchKey )
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
 
 				LOG.debug( "Done deleting collection" );
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not delete collection: " +
 								MessageHelper.collectionInfoString( this, id, getFactory() ),
 						getSQLDeleteString()
 						);
 			}
 
 		}
 
 	}
 
 	private BasicBatchKey recreateBatchKey;
 
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Inserting collection: %s",
-						MessageHelper.collectionInfoString( this, id, getFactory() ) );
+						MessageHelper.collectionInfoString( this, collection, id, session ) );
 			}
 
 			try {
 				// create all the new entries
 				Iterator entries = collection.entries( this );
 				if ( entries.hasNext() ) {
 					Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 					collection.preInsert( this );
 					int i = 0;
 					int count = 0;
 					while ( entries.hasNext() ) {
 
 						final Object entry = entries.next();
 						if ( collection.entryExists( entry, i ) ) {
 							int offset = 1;
 							PreparedStatement st = null;
 							boolean callable = isInsertCallable();
 							boolean useBatch = expectation.canBeBatched();
 							String sql = getSQLInsertRowString();
 
 							if ( useBatch ) {
 								if ( recreateBatchKey == null ) {
 									recreateBatchKey = new BasicBatchKey(
 											getRole() + "#RECREATE",
 											expectation
 											);
 								}
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( recreateBatchKey )
 										.getBatchStatement( sql, callable );
 							}
 							else {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getStatementPreparer()
 										.prepareStatement( sql, callable );
 							}
 
 							try {
 								offset += expectation.prepare( st );
 
 								// TODO: copy/paste from insertRows()
 								int loc = writeKey( st, id, offset, session );
 								if ( hasIdentifier ) {
 									loc = writeIdentifier( st, collection.getIdentifier( entry, i ), loc, session );
 								}
 								if ( hasIndex /* && !indexIsFormula */) {
 									loc = writeIndex( st, collection.getIndex( entry, i, this ), loc, session );
 								}
 								loc = writeElement( st, collection.getElement( entry ), loc, session );
 
 								if ( useBatch ) {
 									session.getTransactionCoordinator()
 											.getJdbcCoordinator()
 											.getBatch( recreateBatchKey )
 											.addToBatch();
 								}
 								else {
 									expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 								}
 
 								collection.afterRowInsert( this, entry, i );
 								count++;
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
 						i++;
 					}
 
 					LOG.debugf( "Done inserting collection: %s rows inserted", count );
 
 				}
 				else {
 					LOG.debug( "Collection was empty" );
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not insert collection: " +
-								MessageHelper.collectionInfoString( this, id, getFactory() ),
+								MessageHelper.collectionInfoString( this, collection, id, session ),
 						getSQLInsertRowString()
 						);
 			}
 		}
 	}
 
 	protected boolean isRowDeleteEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	public void deleteRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Deleting rows of collection: %s",
-						MessageHelper.collectionInfoString( this, id, getFactory() ) );
+						MessageHelper.collectionInfoString( this, collection, id, session ) );
 			}
 
 			boolean deleteByIndex = !isOneToMany() && hasIndex && !indexContainsFormula;
 			final Expectation expectation = Expectations.appropriateExpectation( getDeleteCheckStyle() );
 			try {
 				// delete all the deleted entries
 				Iterator deletes = collection.getDeletes( this, !deleteByIndex );
 				if ( deletes.hasNext() ) {
 					int offset = 1;
 					int count = 0;
 					while ( deletes.hasNext() ) {
 						PreparedStatement st = null;
 						boolean callable = isDeleteCallable();
 						boolean useBatch = expectation.canBeBatched();
 						String sql = getSQLDeleteRowString();
 
 						if ( useBatch ) {
 							if ( deleteBatchKey == null ) {
 								deleteBatchKey = new BasicBatchKey(
 										getRole() + "#DELETE",
 										expectation
 										);
 							}
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getBatch( deleteBatchKey )
 									.getBatchStatement( sql, callable );
 						}
 						else {
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getStatementPreparer()
 									.prepareStatement( sql, callable );
 						}
 
 						try {
 							expectation.prepare( st );
 
 							Object entry = deletes.next();
 							int loc = offset;
 							if ( hasIdentifier ) {
 								writeIdentifier( st, entry, loc, session );
 							}
 							else {
 								loc = writeKey( st, id, loc, session );
 								if ( deleteByIndex ) {
 									writeIndexToWhere( st, entry, loc, session );
 								}
 								else {
 									writeElementToWhere( st, entry, loc, session );
 								}
 							}
 
 							if ( useBatch ) {
 								session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( deleteBatchKey )
 										.addToBatch();
 							}
 							else {
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							count++;
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
 
 						LOG.debugf( "Done deleting collection rows: %s deleted", count );
 					}
 				}
 				else {
 					LOG.debug( "No rows to delete" );
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not delete collection rows: " +
-								MessageHelper.collectionInfoString( this, id, getFactory() ),
+								MessageHelper.collectionInfoString( this, collection, id, session ),
 						getSQLDeleteRowString()
 						);
 			}
 		}
 	}
 
 	protected boolean isRowInsertEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey insertBatchKey;
 
 	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) LOG.debugf( "Inserting rows of collection: %s",
-					MessageHelper.collectionInfoString( this, id, getFactory() ) );
+					MessageHelper.collectionInfoString( this, collection, id, session ) );
 
 			try {
 				// insert all the new entries
 				collection.preInsert( this );
 				Iterator entries = collection.entries( this );
 				Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 				boolean callable = isInsertCallable();
 				boolean useBatch = expectation.canBeBatched();
 				String sql = getSQLInsertRowString();
 				int i = 0;
 				int count = 0;
 				while ( entries.hasNext() ) {
 					int offset = 1;
 					Object entry = entries.next();
 					PreparedStatement st = null;
 					if ( collection.needsInserting( entry, i, elementType ) ) {
 
 						if ( useBatch ) {
 							if ( insertBatchKey == null ) {
 								insertBatchKey = new BasicBatchKey(
 										getRole() + "#INSERT",
 										expectation
 										);
 							}
 							if ( st == null ) {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( insertBatchKey )
 										.getBatchStatement( sql, callable );
 							}
 						}
 						else {
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getStatementPreparer()
 									.prepareStatement( sql, callable );
 						}
 
 						try {
 							offset += expectation.prepare( st );
 							// TODO: copy/paste from recreate()
 							offset = writeKey( st, id, offset, session );
 							if ( hasIdentifier ) {
 								offset = writeIdentifier( st, collection.getIdentifier( entry, i ), offset, session );
 							}
 							if ( hasIndex /* && !indexIsFormula */) {
 								offset = writeIndex( st, collection.getIndex( entry, i, this ), offset, session );
 							}
 							writeElement( st, collection.getElement( entry ), offset, session );
 
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().getBatch( insertBatchKey ).addToBatch();
 							}
 							else {
 								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
 							}
 							collection.afterRowInsert( this, entry, i );
 							count++;
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
 					i++;
 				}
 				LOG.debugf( "Done inserting rows: %s inserted", count );
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not insert collection rows: " +
-								MessageHelper.collectionInfoString( this, id, getFactory() ),
+								MessageHelper.collectionInfoString( this, collection, id, session ),
 						getSQLInsertRowString()
 						);
 			}
 
 		}
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public String getOwnerEntityName() {
 		return entityName;
 	}
 
 	public EntityPersister getOwnerEntityPersister() {
 		return ownerPersister;
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 	public boolean hasOrphanDelete() {
 		return hasOrphanDelete;
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return indexType;
 		}
 		return elementPropertyMapping.toType( propertyName );
 	}
 
 	public abstract boolean isManyToMany();
 
 	public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 		StringBuilder buffer = new StringBuilder();
 		manyToManyFilterHelper.render( buffer, elementPersister.getFilterAliasGenerator(alias), enabledFilters );
 
 		if ( manyToManyWhereString != null ) {
 			buffer.append( " and " )
 					.append( StringHelper.replace( manyToManyWhereTemplate, Template.TEMPLATE, alias ) );
 		}
 
 		return buffer.toString();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return qualify( alias, indexColumnNames, indexFormulaTemplates );
 		}
 		return elementPropertyMapping.toColumns( alias, propertyName );
 	}
 
 	private String[] indexFragments;
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String[] toColumns(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			if ( indexFragments == null ) {
 				String[] tmp = new String[indexColumnNames.length];
 				for ( int i = 0; i < indexColumnNames.length; i++ ) {
 					tmp[i] = indexColumnNames[i] == null
 							? indexFormulas[i]
 							: indexColumnNames[i];
 					indexFragments = tmp;
 				}
 			}
 			return indexFragments;
 		}
 
 		return elementPropertyMapping.toColumns( propertyName );
 	}
 
 	public Type getType() {
 		return elementPropertyMapping.getType(); // ==elementType ??
 	}
 
 	public String getName() {
 		return getRole();
 	}
 
 	public EntityPersister getElementPersister() {
 		if ( elementPersister == null ) {
 			throw new AssertionFailure( "not an association" );
 		}
 		return elementPersister;
 	}
 
 	public boolean isCollection() {
 		return true;
 	}
 
 	public Serializable[] getCollectionSpaces() {
 		return spaces;
 	}
 
 	protected abstract String generateDeleteString();
 
 	protected abstract String generateDeleteRowString();
 
 	protected abstract String generateUpdateRowString();
 
 	protected abstract String generateInsertRowString();
 
 	public void updateRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && collection.isRowUpdatePossible() ) {
 
 			LOG.debugf( "Updating rows of collection: %s#%s", role, id );
 
 			// update all the modified entries
 			int count = doUpdateRows( id, collection, session );
 
 			LOG.debugf( "Done updating rows: %s updated", count );
 		}
 	}
 
 	protected abstract int doUpdateRows(Serializable key, PersistentCollection collection, SessionImplementor session)
 			throws HibernateException;
 
 	public CollectionMetadata getCollectionMetadata() {
 		return this;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected String filterFragment(String alias) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	protected boolean isInsertCallable() {
 		return insertCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getInsertCheckStyle() {
 		return insertCheckStyle;
 	}
 
 	protected boolean isUpdateCallable() {
 		return updateCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getUpdateCheckStyle() {
 		return updateCheckStyle;
 	}
 
 	protected boolean isDeleteCallable() {
 		return deleteCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteCheckStyle() {
 		return deleteCheckStyle;
 	}
 
 	protected boolean isDeleteAllCallable() {
 		return deleteAllCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteAllCheckStyle() {
 		return deleteAllCheckStyle;
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + role + ')';
 	}
 
 	public boolean isVersioned() {
 		return isVersioned && getOwnerEntityPersister().isVersioned();
 	}
 
 	public String getNodeName() {
 		return nodeName;
 	}
 
 	public String getElementNodeName() {
 		return elementNodeName;
 	}
 
 	public String getIndexNodeName() {
 		return indexNodeName;
 	}
 
 	// TODO: deprecate???
 	protected SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	// TODO: needed???
 	protected SqlExceptionHelper getSQLExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
 	}
 
 	public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return filterHelper.isAffectedBy( session.getEnabledFilters() ) ||
 				( isManyToMany() && manyToManyFilterHelper.isAffectedBy( session.getEnabledFilters() ) );
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 
 	public boolean isMutable() {
 		return isMutable;
 	}
 
 	public String[] getCollectionPropertyColumnAliases(String propertyName, String suffix) {
 		String rawAliases[] = (String[]) collectionPropertyColumnAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String result[] = new String[rawAliases.length];
 		for ( int i = 0; i < rawAliases.length; i++ ) {
 			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 
 	// TODO: formulas ?
 	public void initCollectionPropertyMap() {
 
 		initCollectionPropertyMap( "key", keyType, keyColumnAliases, keyColumnNames );
 		initCollectionPropertyMap( "element", elementType, elementColumnAliases, elementColumnNames );
 		if ( hasIndex ) {
 			initCollectionPropertyMap( "index", indexType, indexColumnAliases, indexColumnNames );
 		}
 		if ( hasIdentifier ) {
 			initCollectionPropertyMap(
 					"id",
 					identifierType,
 					new String[] { identifierColumnAlias },
 					new String[] { identifierColumnName } );
 		}
 	}
 
 	private void initCollectionPropertyMap(String aliasName, Type type, String[] columnAliases, String[] columnNames) {
 
 		collectionPropertyColumnAliases.put( aliasName, columnAliases );
 		collectionPropertyColumnNames.put( aliasName, columnNames );
 
 		if ( type.isComponentType() ) {
 			CompositeType ct = (CompositeType) type;
 			String[] propertyNames = ct.getPropertyNames();
 			for ( int i = 0; i < propertyNames.length; i++ ) {
 				String name = propertyNames[i];
 				collectionPropertyColumnAliases.put( aliasName + "." + name, columnAliases[i] );
 				collectionPropertyColumnNames.put( aliasName + "." + name, columnNames[i] );
 			}
 		}
 
 	}
 
 	public int getSize(Serializable key, SessionImplementor session) {
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectSizeString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				ResultSet rs = st.executeQuery();
 				try {
 					return rs.next() ? rs.getInt( 1 ) - baseIndex : 0;
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve collection size: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 					);
 		}
 	}
 
 	public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 		return exists( key, incrementIndexByBase( index ), getIndexType(), sqlDetectRowByIndexString, session );
 	}
 
 	public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 		return exists( key, element, getElementType(), sqlDetectRowByElementString, session );
 	}
 
 	private boolean exists(Serializable key, Object indexOrElement, Type indexOrElementType, String sql, SessionImplementor session) {
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				indexOrElementType.nullSafeSet( st, indexOrElement, keyColumnNames.length + 1, session );
 				ResultSet rs = st.executeQuery();
 				try {
 					return rs.next();
 				}
 				finally {
 					rs.close();
 				}
 			}
 			catch ( TransientObjectException e ) {
 				return false;
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not check row existence: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 					);
 		}
 	}
 
 	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectRowByIndexString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				getIndexType().nullSafeSet( st, incrementIndexByBase( index ), keyColumnNames.length + 1, session );
 				ResultSet rs = st.executeQuery();
 				try {
 					if ( rs.next() ) {
 						return getElementType().nullSafeGet( rs, elementColumnAliases, session, owner );
 					}
 					else {
 						return null;
 					}
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not read row: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 					);
 		}
 	}
 
 	public boolean isExtraLazy() {
 		return isExtraLazy;
 	}
 
 	protected Dialect getDialect() {
 		return dialect;
 	}
 
 	/**
 	 * Intended for internal use only. In fact really only currently used from
 	 * test suite for assertion purposes.
 	 * 
 	 * @return The default collection initializer for this persister/collection.
 	 */
 	public CollectionInitializer getInitializer() {
 		return initializer;
 	}
 
 	private class StandardOrderByAliasResolver implements OrderByAliasResolver {
 		private final String rootAlias;
 
 		private StandardOrderByAliasResolver(String rootAlias) {
 			this.rootAlias = rootAlias;
 		}
 
 		@Override
 		public String resolveTableAlias(String columnReference) {
 			if ( elementPersister == null ) {
 				// we have collection of non-entity elements...
 				return rootAlias;
 			}
 			else {
 				return ( (Loadable) elementPersister ).getTableAliasForColumn( columnReference, rootAlias );
 			}
 		}
 	}
 	
 	public abstract FilterAliasGenerator getFilterAliasGenerator(final String rootAlias);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
index 4170a8b3cd..58647cdc9b 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
@@ -1,364 +1,364 @@
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
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.internal.StaticFilterAliasGenerator;
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
-					"could not update collection rows: " + MessageHelper.collectionInfoString( this, id, getFactory() ),
+					"could not update collection rows: " + MessageHelper.collectionInfoString( this, collection, id, session ),
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
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return new StaticFilterAliasGenerator(rootAlias);
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
index 01f58b9653..488e631e5d 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
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
 package org.hibernate.persister.collection;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
 import java.util.Iterator;
 
 import org.hibernate.MappingException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.internal.FilterAliasGenerator;
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
-					MessageHelper.collectionInfoString( this, id, getFactory() ),
+					MessageHelper.collectionInfoString( this, collection, id, session ),
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
 		StringBuilder buf = new StringBuilder();
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
 
 	@Override
 	public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 		return getElementPersister().getFilterAliasGenerator(rootAlias);
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/pretty/MessageHelper.java b/hibernate-core/src/main/java/org/hibernate/pretty/MessageHelper.java
index 401317a9d5..f519ecdc41 100644
--- a/hibernate-core/src/main/java/org/hibernate/pretty/MessageHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/pretty/MessageHelper.java
@@ -1,356 +1,406 @@
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
 package org.hibernate.pretty;
 import java.io.Serializable;
 
+import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * MessageHelper methods for rendering log messages relating to managed
  * entities and collections typically used in log statements and exception
  * messages.
  *
  * @author Max Andersen, Gavin King
  */
 public final class MessageHelper {
 
 	private MessageHelper() {
 	}
 
 
 	// entities ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Generate an info message string relating to a particular entity,
 	 * based on the given entityName and id.
 	 *
 	 * @param entityName The defined entity name.
 	 * @param id The entity id value.
 	 * @return An info string, in the form [FooBar#1].
 	 */
 	public static String infoString(String entityName, Serializable id) {
 		StringBuilder s = new StringBuilder();
 		s.append( '[' );
 		if( entityName == null ) {
 			s.append( "<null entity name>" );
 		}
 		else {
 			s.append( entityName );
 		}
 		s.append( '#' );
 
 		if ( id == null ) {
 			s.append( "<null>" );
 		}
 		else {
 			s.append( id );
 		}
 		s.append( ']' );
 
 		return s.toString();
 	}
 
 	/**
 	 * Generate an info message string relating to a particular entity.
 	 *
 	 * @param persister The persister for the entity
 	 * @param id The entity id value
 	 * @param factory The session factory
 	 * @return An info string, in the form [FooBar#1]
 	 */
 	public static String infoString(
 			EntityPersister persister, 
 			Object id, 
 			SessionFactoryImplementor factory) {
 		StringBuilder s = new StringBuilder();
 		s.append( '[' );
 		Type idType;
 		if( persister == null ) {
 			s.append( "<null EntityPersister>" );
 			idType = null;
 		}
 		else {
 			s.append( persister.getEntityName() );
 			idType = persister.getIdentifierType();
 		}
 		s.append( '#' );
 
 		if ( id == null ) {
 			s.append( "<null>" );
 		}
 		else {
 			if ( idType == null ) {
 				s.append( id );
 			}
 			else {
 				s.append( idType.toLoggableString( id, factory ) );
 			}
 		}
 		s.append( ']' );
 
 		return s.toString();
 
 	}
 
 	/**
 	 * Generate an info message string relating to a particular entity,.
 	 *
 	 * @param persister The persister for the entity
 	 * @param id The entity id value
 	 * @param identifierType The entity identifier type mapping
 	 * @param factory The session factory
 	 * @return An info string, in the form [FooBar#1]
 	 */
 	public static String infoString(
 			EntityPersister persister, 
 			Object id, 
 			Type identifierType,
 			SessionFactoryImplementor factory) {
 		StringBuilder s = new StringBuilder();
 		s.append( '[' );
 		if( persister == null ) {
 			s.append( "<null EntityPersister>" );
 		}
 		else {
 			s.append( persister.getEntityName() );
 		}
 		s.append( '#' );
 
 		if ( id == null ) {
 			s.append( "<null>" );
 		}
 		else {
 			s.append( identifierType.toLoggableString( id, factory ) );
 		}
 		s.append( ']' );
 
 		return s.toString();
 	}
 
 	/**
 	 * Generate an info message string relating to a series of entities.
 	 *
 	 * @param persister The persister for the entities
 	 * @param ids The entity id values
 	 * @param factory The session factory
 	 * @return An info string, in the form [FooBar#<1,2,3>]
 	 */
 	public static String infoString(
 			EntityPersister persister, 
 			Serializable[] ids, 
 			SessionFactoryImplementor factory) {
 		StringBuilder s = new StringBuilder();
 		s.append( '[' );
 		if( persister == null ) {
 			s.append( "<null EntityPersister>" );
 		}
 		else {
 			s.append( persister.getEntityName() );
 			s.append( "#<" );
 			for ( int i=0; i<ids.length; i++ ) {
 				s.append( persister.getIdentifierType().toLoggableString( ids[i], factory ) );
 				if ( i < ids.length-1 ) {
 					s.append( ", " );
 				}
 			}
 			s.append( '>' );
 		}
 		s.append( ']' );
 
 		return s.toString();
 
 	}
 
 	/**
 	 * Generate an info message string relating to given entity persister.
 	 *
 	 * @param persister The persister.
 	 * @return An info string, in the form [FooBar]
 	 */
 	public static String infoString(EntityPersister persister) {
 		StringBuilder s = new StringBuilder();
 		s.append( '[' );
 		if ( persister == null ) {
 			s.append( "<null EntityPersister>" );
 		}
 		else {
 			s.append( persister.getEntityName() );
 		}
 		s.append( ']' );
 		return s.toString();
 	}
 
 	/**
 	 * Generate an info message string relating to a given property value
 	 * for an entity.
 	 *
 	 * @param entityName The entity name
 	 * @param propertyName The name of the property
 	 * @param key The property value.
 	 * @return An info string, in the form [Foo.bars#1]
 	 */
 	public static String infoString(String entityName, String propertyName, Object key) {
 		StringBuilder s = new StringBuilder()
 				.append( '[' )
 				.append( entityName )
 				.append( '.' )
 				.append( propertyName )
 				.append( '#' );
 
 		if ( key == null ) {
 			s.append( "<null>" );
 		}
 		else {
 			s.append( key );
 		}
 		s.append( ']' );
 		return s.toString();
 	}
 
 
 	// collections ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+	
+	/**
+	 * Generate an info message string relating to a particular managed
+	 * collection.  Attempts to intelligently handle property-refs issues
+	 * where the collection key is not the same as the owner key.
+	 *
+	 * @param persister The persister for the collection
+	 * @param collection The collection itself
+	 * @param collectionKey The collection key
+	 * @param session The session
+	 * @return An info string, in the form [Foo.bars#1]
+	 */
+	public static String collectionInfoString( 
+			CollectionPersister persister,
+			PersistentCollection collection,
+			Serializable collectionKey,
+			SessionImplementor session ) {
+		
+		StringBuilder s = new StringBuilder();
+		s.append( '[' );
+		if ( persister == null ) {
+			s.append( "<unreferenced>" );
+		}
+		else {
+			s.append( persister.getRole() );
+			s.append( '#' );
+			
+			Type ownerIdentifierType = persister.getOwnerEntityPersister()
+					.getIdentifierType();
+			Serializable ownerKey;
+			// TODO: Is it redundant to attempt to use the collectionKey,
+			// or is always using the owner id sufficient?
+			if ( collectionKey.getClass().isAssignableFrom( 
+					ownerIdentifierType.getReturnedClass() ) ) {
+				ownerKey = collectionKey;
+			} else {
+				ownerKey = session.getPersistenceContext()
+						.getEntry( collection.getOwner() ).getId();
+			}
+			s.append( ownerIdentifierType.toLoggableString( 
+					ownerKey, session.getFactory() ) );
+		}
+		s.append( ']' );
 
+		return s.toString();
+	}
 
 	/**
 	 * Generate an info message string relating to a series of managed
 	 * collections.
 	 *
 	 * @param persister The persister for the collections
 	 * @param ids The id values of the owners
 	 * @param factory The session factory
 	 * @return An info string, in the form [Foo.bars#<1,2,3>]
 	 */
 	public static String collectionInfoString(
 			CollectionPersister persister, 
 			Serializable[] ids, 
 			SessionFactoryImplementor factory) {
 		StringBuilder s = new StringBuilder();
 		s.append( '[' );
 		if ( persister == null ) {
 			s.append( "<unreferenced>" );
 		}
 		else {
 			s.append( persister.getRole() );
 			s.append( "#<" );
 			for ( int i = 0; i < ids.length; i++ ) {
 				addIdToCollectionInfoString( persister, ids[i], factory, s );
 				if ( i < ids.length-1 ) {
 					s.append( ", " );
 				}
 			}
 			s.append( '>' );
 		}
 		s.append( ']' );
 		return s.toString();
 	}
 
 	/**
 	 * Generate an info message string relating to a particular managed
 	 * collection.
 	 *
 	 * @param persister The persister for the collection
 	 * @param id The id value of the owner
 	 * @param factory The session factory
 	 * @return An info string, in the form [Foo.bars#1]
 	 */
 	public static String collectionInfoString(
 			CollectionPersister persister, 
 			Serializable id, 
 			SessionFactoryImplementor factory) {
 		StringBuilder s = new StringBuilder();
 		s.append( '[' );
 		if ( persister == null ) {
 			s.append( "<unreferenced>" );
 		}
 		else {
 			s.append( persister.getRole() );
 			s.append( '#' );
 
 			if ( id == null ) {
 				s.append( "<null>" );
 			}
 			else {
 				addIdToCollectionInfoString( persister, id, factory, s );
 			}
 		}
 		s.append( ']' );
 
 		return s.toString();
 	}
 	
 	private static void addIdToCollectionInfoString(
 			CollectionPersister persister,
 			Serializable id,
 			SessionFactoryImplementor factory,
 			StringBuilder s ) {
 		// Need to use the identifier type of the collection owner
 		// since the incoming is value is actually the owner's id.
 		// Using the collection's key type causes problems with
 		// property-ref keys.
 		// Also need to check that the expected identifier type matches
 		// the given ID.  Due to property-ref keys, the collection key
 		// may not be the owner key.
-		Type identifierType = persister.getOwnerEntityPersister()
+		Type ownerIdentifierType = persister.getOwnerEntityPersister()
 				.getIdentifierType();
 		if ( id.getClass().isAssignableFrom( 
-				identifierType.getReturnedClass() ) ) {
-			s.append( identifierType.toLoggableString( id, factory ) );
+				ownerIdentifierType.getReturnedClass() ) ) {
+			s.append( ownerIdentifierType.toLoggableString( id, factory ) );
+		} else {
+			// TODO: This is a crappy backup if a property-ref is used.
+			// If the reference is an object w/o toString(), this isn't going to work.
+			s.append( id.toString() );
 		}
-		// TODO: If the collection key was not the owner key, 
 	}
 
 	/**
 	 * Generate an info message string relating to a particular managed
 	 * collection.
 	 *
 	 * @param role The role-name of the collection
 	 * @param id The id value of the owner
 	 * @return An info string, in the form [Foo.bars#1]
 	 */
 	public static String collectionInfoString(String role, Serializable id) {
 		StringBuilder s = new StringBuilder();
 		s.append( '[' );
 		if( role == null ) {
 			s.append( "<unreferenced>" );
 		}
 		else {
 			s.append( role );
 			s.append( '#' );
 
 			if ( id == null ) {
 				s.append( "<null>" );
 			}
 			else {
 				s.append( id );
 			}
 		}
 		s.append( ']' );
 		return s.toString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
index c410181186..f7683161de 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
@@ -1,692 +1,703 @@
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
-
 import org.hibernate.EntityMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
+import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
+import org.jboss.logging.Logger;
 
 /**
  * A type that handles Hibernate <tt>PersistentCollection</tt>s (including arrays).
  * 
  * @author Gavin King
  */
 public abstract class CollectionType extends AbstractType implements AssociationType {
 
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionType.class.getName());
+
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
 
 	public final boolean isEqual(Object x, Object y) {
 		return x == y
 			|| ( x instanceof PersistentCollection && ( (PersistentCollection) x ).isWrapper( y ) )
 			|| ( y instanceof PersistentCollection && ( (PersistentCollection) y ).isWrapper( x ) );
 	}
 
 	public int compare(Object x, Object y) {
 		return 0; // collections cannot be compared
 	}
 
 	public int getHashCode(Object x) {
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
 
 	protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		final List<String> list = new ArrayList<String>();
 		Type elemType = getElementType( factory );
 		Iterator itr = getElementsIterator( value );
 		while ( itr.hasNext() ) {
 			list.add( elemType.toLoggableString( itr.next(), factory ) );
 		}
 		return list.toString();
 	}
 
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
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
 		return getElementsIterator(collection);
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
 				id = entityEntry.getPersister().getPropertyValue( owner, foreignKeyPropertyName );
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
 			Class ownerMappedClass = ownerPersister.getMappedClass();
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
 		final EntityMode entityMode = persister.getOwnerEntityPersister().getEntityMode();
 
 		// check if collection is currently being loaded
 		PersistentCollection collection = persistenceContext.getLoadContexts().locateLoadingCollection( persister, key );
 		
 		if ( collection == null ) {
 			
 			// check if it is already completely loaded, but unowned
 			collection = persistenceContext.useUnownedCollection( new CollectionKey(persister, key, entityMode) );
 			
 			if ( collection == null ) {
 				// create a new collection wrapper, to be initialized later
 				collection = instantiate( session, persister, key );
+				
 				collection.setOwner(owner);
 	
 				persistenceContext.addUninitializedCollection( persister, collection, key );
 	
 				// some collections are not lazy:
 				if ( initializeImmediately() ) {
 					session.initializeCollection( collection, false );
 				}
 				else if ( !persister.isLazy() ) {
 					persistenceContext.addNonLazyCollection( collection );
 				}
 	
 				if ( hasHolder() ) {
 					session.getPersistenceContext().addCollectionHolder( collection );
 				}
 				
 			}
 			
+			if ( LOG.isTraceEnabled() ) {
+				LOG.tracef( "Created collection wrapper: %s",
+						MessageHelper.collectionInfoString( persister, collection,
+								key, session ) );
+			}
+			
 		}
 		
 		collection.setOwner(owner);
 
 		return collection.getValue();
 	}
 
 	public boolean hasHolder() {
 		return false;
 	}
 
 	protected boolean initializeImmediately() {
 		return false;
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
diff --git a/hibernate-core/src/test/resources/log4j.properties b/hibernate-core/src/test/resources/log4j.properties
index 686aae8fc2..f933444118 100644
--- a/hibernate-core/src/test/resources/log4j.properties
+++ b/hibernate-core/src/test/resources/log4j.properties
@@ -1,17 +1,23 @@
+log4j.appender.null=org.apache.log4j.varia.NullAppender
+
+# Only records messages at INFO below, used to mimim root level info logging
+log4j.appender.stdout_info=org.apache.log4j.ConsoleAppender
+log4j.appender.stdout_info.Target=System.out
+log4j.appender.stdout_info.Threshold=INFO
+log4j.appender.stdout_info.layout=org.apache.log4j.PatternLayout
+log4j.appender.stdout_info.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
+
+# Used by loggers other than root to log at any level
 log4j.appender.stdout=org.apache.log4j.ConsoleAppender
 log4j.appender.stdout.Target=System.out
 log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
 log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
 
 
-log4j.rootLogger=info, stdout
+log4j.rootLogger=trace, null, stdout_info
 
 log4j.logger.org.hibernate.tool.hbm2ddl=trace
-log4j.logger.org.hibernate.testing.cache=debug
+log4j.logger.org.hibernate.testing.cache=debug, stdout
 
 # SQL Logging - HHH-6833
-log4j.logger.org.hibernate.SQL=debug
-
-log4j.logger.org.hibernate.hql.internal.ast=debug
-
-log4j.logger.org.hibernate.sql.ordering.antlr=debug
\ No newline at end of file
+log4j.logger.org.hibernate.SQL=debug, stdout
\ No newline at end of file
