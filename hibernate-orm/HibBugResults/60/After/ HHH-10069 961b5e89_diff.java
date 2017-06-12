diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
index df0a0c8d2c..8b1c9cd5bc 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
@@ -1,1239 +1,1240 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
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
 import org.hibernate.FlushMode;
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
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.SessionFactoryRegistry;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.ComponentType;
+import org.hibernate.type.CompositeType;
 import org.hibernate.type.IntegerType;
 import org.hibernate.type.LongType;
 import org.hibernate.type.PostgresUUIDType;
 import org.hibernate.type.StringType;
 import org.hibernate.type.Type;
 import org.hibernate.type.UUIDBinaryType;
 import org.hibernate.type.UUIDCharType;
 
 /**
  * Base class implementing {@link org.hibernate.collection.spi.PersistentCollection}
  *
  * @author Gavin King
  */
 public abstract class AbstractPersistentCollection implements Serializable, PersistentCollection {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( AbstractPersistentCollection.class );
 
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
 	private boolean allowLoadOutsideTransaction;
 
 	/**
 	 * Not called by Hibernate, but used by non-JDK serialization,
 	 * eg. SOAP libraries.
 	 */
 	public AbstractPersistentCollection() {
 	}
 
 	protected AbstractPersistentCollection(SessionImplementor session) {
 		this.session = session;
 	}
 
 	@Override
 	public final String getRole() {
 		return role;
 	}
 
 	@Override
 	public final Serializable getKey() {
 		return key;
 	}
 
 	@Override
 	public final boolean isUnreferenced() {
 		return role == null;
 	}
 
 	@Override
 	public final boolean isDirty() {
 		return dirty;
 	}
 
 	@Override
 	public final void clearDirty() {
 		dirty = false;
 	}
 
 	@Override
 	public final void dirty() {
 		dirty = true;
 	}
 
 	@Override
 	public final Serializable getStoredSnapshot() {
 		return storedSnapshot;
 	}
 
 	//Careful: these methods do not initialize the collection.
 
 	@Override
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
 				final boolean isExtraLazy = withTemporarySessionIfNeeded(
 						new LazyInitializationWork<Boolean>() {
 							@Override
 							public Boolean doWork() {
 								final CollectionEntry entry = session.getPersistenceContext().getCollectionEntry( AbstractPersistentCollection.this );
 
 								if ( entry != null ) {
 									final CollectionPersister persister = entry.getLoadedPersister();
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
 
 	/**
 	 * TBH not sure why this is public
 	 *
 	 * @param <T> The java type of the return for this LazyInitializationWork
 	 */
 	public static interface LazyInitializationWork<T> {
 		/**
 		 * Do the represented work and return the result.
 		 *
 		 * @return The result
 		 */
 		public T doWork();
 	}
 
 	private <T> T withTemporarySessionIfNeeded(LazyInitializationWork<T> lazyInitializationWork) {
 		SessionImplementor originalSession = null;
 		boolean isTempSession = false;
 		boolean isJTA = false;
 
 		if ( session == null ) {
 			if ( allowLoadOutsideTransaction ) {
 				session = openTemporarySessionForLoading();
 				isTempSession = true;
 			}
 			else {
 				throwLazyInitializationException( "could not initialize proxy - no Session" );
 			}
 		}
 		else if ( !session.isOpen() ) {
 			if ( allowLoadOutsideTransaction ) {
 				originalSession = session;
 				session = openTemporarySessionForLoading();
 				isTempSession = true;
 			}
 			else {
 				throwLazyInitializationException( "could not initialize proxy - the owning Session was closed" );
 			}
 		}
 		else if ( !session.isConnected() ) {
 			if ( allowLoadOutsideTransaction ) {
 				originalSession = session;
 				session = openTemporarySessionForLoading();
 				isTempSession = true;
 			}
 			else {
 				throwLazyInitializationException( "could not initialize proxy - the owning Session is disconnected" );
 			}
 		}
 
 		if ( isTempSession ) {
 			isJTA = session.getTransactionCoordinator().getTransactionCoordinatorBuilder().isJta();
 			
 			if ( !isJTA ) {
 				// Explicitly handle the transactions only if we're not in
 				// a JTA environment.  A lazy loading temporary session can
 				// be created even if a current session and transaction are
 				// open (ex: session.clear() was used).  We must prevent
 				// multiple transactions.
 				( (Session) session ).beginTransaction();
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
 						( (Session) session ).getTransaction().commit();
 					}
 					( (Session) session ).close();
 				}
 				catch (Exception e) {
 					LOG.warn( "Unable to close temporary session used to load lazy collection associated to no session" );
 				}
 				session = originalSession;
 			}
 		}
 	}
 
 	private SessionImplementor openTemporarySessionForLoading() {
 		if ( sessionFactoryUuid == null ) {
 			throwLazyInitializationException( "SessionFactory UUID not known to create temporary Session for loading" );
 		}
 
 		final SessionFactoryImplementor sf = (SessionFactoryImplementor)
 				SessionFactoryRegistry.INSTANCE.getSessionFactory( sessionFactoryUuid );
 		final SessionImplementor session = (SessionImplementor) sf.openSession();
 		session.getPersistenceContext().setDefaultReadOnly( true );
 		session.setFlushMode( FlushMode.MANUAL );
 		return session;
 	}
 
 	protected Boolean readIndexExistence(final Object index) {
 		if ( !initialized ) {
 			final Boolean extraLazyExistenceCheck = withTemporarySessionIfNeeded(
 					new LazyInitializationWork<Boolean>() {
 						@Override
 						public Boolean doWork() {
 							final CollectionEntry entry = session.getPersistenceContext().getCollectionEntry( AbstractPersistentCollection.this );
 							final CollectionPersister persister = entry.getLoadedPersister();
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
 			final Boolean extraLazyExistenceCheck = withTemporarySessionIfNeeded(
 					new LazyInitializationWork<Boolean>() {
 						@Override
 						public Boolean doWork() {
 							final CollectionEntry entry = session.getPersistenceContext().getCollectionEntry( AbstractPersistentCollection.this );
 							final CollectionPersister persister = entry.getLoadedPersister();
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
 					final CollectionEntry entry = session.getPersistenceContext().getCollectionEntry( AbstractPersistentCollection.this );
 					final CollectionPersister persister = entry.getLoadedPersister();
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
 
 			final ExtraLazyElementByIndexReader reader = new ExtraLazyElementByIndexReader();
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
 		return session != null
 				&& session.isOpen()
 				&& session.getPersistenceContext().containsCollection( this );
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
 		return !initialized
 				&& isConnectedToSession()
 				&& isInverseCollection();
 	}
 
 	/**
 	 * Is this collection in a state that would allow us to
 	 * "queue" puts? This is a special case, because of orphan
 	 * delete.
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	protected boolean isPutQueueEnabled() {
 		return !initialized
 				&& isConnectedToSession()
 				&& isInverseOneToManyOrNoOrphanDelete();
 	}
 
 	/**
 	 * Is this collection in a state that would allow us to
 	 * "queue" clear? This is a special case, because of orphan
 	 * delete.
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	protected boolean isClearQueueEnabled() {
 		return !initialized
 				&& isConnectedToSession()
 				&& isInverseCollectionNoOrphanDelete();
 	}
 
 	/**
 	 * Is this the "inverse" end of a bidirectional association?
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	private boolean isInverseCollection() {
 		final CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( this );
 		return ce != null && ce.getLoadedPersister().isInverse();
 	}
 
 	/**
 	 * Is this the "inverse" end of a bidirectional association with
 	 * no orphan delete enabled?
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	private boolean isInverseCollectionNoOrphanDelete() {
 		final CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( this );
 		return ce != null
 				&&
 				ce.getLoadedPersister().isInverse() &&
 				!ce.getLoadedPersister().hasOrphanDelete();
 	}
 
 	/**
 	 * Is this the "inverse" end of a bidirectional one-to-many, or
 	 * of a collection with no orphan delete?
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	private boolean isInverseOneToManyOrNoOrphanDelete() {
 		final CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( this );
 		return ce != null
 				&& ce.getLoadedPersister().isInverse()
 				&& ( ce.getLoadedPersister().isOneToMany() || !ce.getLoadedPersister().hasOrphanDelete() );
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
 		//needed so that we remove this collection from the second-level cache
 		dirty = true;
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
 
 	@Override
 	public void setSnapshot(Serializable key, String role, Serializable snapshot) {
 		this.key = key;
 		this.role = role;
 		this.storedSnapshot = snapshot;
 	}
 
 	@Override
 	public void postAction() {
 		operationQueue = null;
 		cachedSize = -1;
 		clearDirty();
 	}
 
 	@Override
 	public Object getValue() {
 		return this;
 	}
 
 	@Override
 	public void beginRead() {
 		// override on some subclasses
 		initializing = true;
 	}
 
 	@Override
 	public boolean endRead() {
 		//override on some subclasses
 		return afterInitialize();
 	}
 
 	@Override
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
 
 	@Override
 	public boolean isDirectlyAccessible() {
 		return directlyAccessible;
 	}
 
 	@Override
 	public final boolean unsetSession(SessionImplementor currentSession) {
 		prepareForPossibleLoadingOutsideTransaction();
 		if ( currentSession == this.session ) {
 			this.session = null;
 			return true;
 		}
 		else {
 			if ( this.session != null ) {
 				LOG.logCannotUnsetUnexpectedSessionInCollection( generateUnexpectedSessionStateMessage( currentSession ) );
 			}
 			return false;
 		}
 	}
 
 	protected void prepareForPossibleLoadingOutsideTransaction() {
 		if ( session != null ) {
 			allowLoadOutsideTransaction = session.getFactory().getSettings().isInitializeLazyStateOutsideTransactionsEnabled();
 
 			if ( allowLoadOutsideTransaction && sessionFactoryUuid == null ) {
 				try {
 					sessionFactoryUuid = (String) session.getFactory().getReference().get( "uuid" ).getContent();
 				}
 				catch (NamingException e) {
 					//not much we can do if this fails...
 				}
 			}
 		}
 	}
 
 	@Override
 	public final boolean setCurrentSession(SessionImplementor session) throws HibernateException {
 		if ( session == this.session ) {
 			return false;
 		}
 		else {
 			if ( this.session != null ) {
 				final String msg = generateUnexpectedSessionStateMessage( session );
 				if ( isConnectedToSession() ) {
 					throw new HibernateException(
 							"Illegal attempt to associate a collection with two open sessions. " + msg
 					);
 				}
 				else {
 					LOG.logUnexpectedSessionInCollectionNotConnected( msg );
 					this.session = session;
 					return true;
 				}
 			}
 			else {
 				this.session = session;
 				return true;
 			}
 		}
 	}
 
 	private String generateUnexpectedSessionStateMessage(SessionImplementor session) {
 		// NOTE: If this.session != null, this.session may be operating on this collection
 		// (e.g., by changing this.role, this.key, or even this.session) in a different thread.
 
 		// Grab the current role and key (it can still get changed by this.session...)
 		// If this collection is connected to this.session, then this.role and this.key should
 		// be consistent with the CollectionEntry in this.session (as long as this.session doesn't
 		// change it). Don't access the CollectionEntry in this.session because that could result
 		// in multi-threaded access to this.session.
 		final String roleCurrent = role;
 		final Serializable keyCurrent = key;
 
 		final StringBuilder sb = new StringBuilder( "Collection : " );
 		if ( roleCurrent != null ) {
 			sb.append( MessageHelper.collectionInfoString( roleCurrent, keyCurrent ) );
 		}
 		else {
 			final CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( this );
 			if ( ce != null ) {
 				sb.append(
 						MessageHelper.collectionInfoString(
 								ce.getLoadedPersister(),
 								this,
 								ce.getLoadedKey(),
 								session
 						)
 				);
 			}
 			else {
 				sb.append( "<unknown>" );
 			}
 		}
 		// only include the collection contents if debug logging
 		if ( LOG.isDebugEnabled() ) {
 			final String collectionContents = wasInitialized() ? toString() : "<uninitialized>";
 			sb.append( "\nCollection contents: [" ).append( collectionContents ).append( "]" );
 		}
 		return sb.toString();
 	}
 
 	@Override
 	public boolean needsRecreate(CollectionPersister persister) {
 		// Workaround for situations like HHH-7072.  If the collection element is a component that consists entirely
 		// of nullable properties, we currently have to forcefully recreate the entire collection.  See the use
 		// of hasNotNullableColumns in the AbstractCollectionPersister constructor for more info.  In order to delete
 		// row-by-row, that would require SQL like "WHERE ( COL = ? OR ( COL is null AND ? is null ) )", rather than
 		// the current "WHERE COL = ?" (fails for null for most DBs).  Note that
 		// the param would have to be bound twice.  Until we eventually add "parameter bind points" concepts to the
 		// AST in ORM 5+, handling this type of condition is either extremely difficult or impossible.  Forcing
 		// recreation isn't ideal, but not really any other option in ORM 4.
-		if (persister.getElementType() instanceof ComponentType) {
-			ComponentType componentType = (ComponentType) persister.getElementType();
+		if ( persister.getElementType() instanceof CompositeType ) {
+			CompositeType componentType = (CompositeType) persister.getElementType();
 			return !componentType.hasNotNullProperty();
 		}
 		return false;
 	}
 
 	@Override
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
 
 	@Override
 	public final boolean wasInitialized() {
 		return initialized;
 	}
 
 	@Override
 	public boolean isRowUpdatePossible() {
 		return true;
 	}
 
 	@Override
 	public final boolean hasQueuedOperations() {
 		return operationQueue != null;
 	}
 
 	@Override
 	public final Iterator queuedAdditionIterator() {
 		if ( hasQueuedOperations() ) {
 			return new Iterator() {
 				private int index;
 
 				@Override
 				public Object next() {
 					return operationQueue.get( index++ ).getAddedInstance();
 				}
 
 				@Override
 				public boolean hasNext() {
 					return index < operationQueue.size();
 				}
 
 				@Override
 				public void remove() {
 					throw new UnsupportedOperationException();
 				}
 			};
 		}
 		else {
 			return EmptyIterator.INSTANCE;
 		}
 	}
 
 	@Override
 	@SuppressWarnings({"unchecked"})
 	public final Collection getQueuedOrphans(String entityName) {
 		if ( hasQueuedOperations() ) {
 			final Collection additions = new ArrayList( operationQueue.size() );
 			final Collection removals = new ArrayList( operationQueue.size() );
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
 
 	@Override
 	public void preInsert(CollectionPersister persister) throws HibernateException {
 	}
 
 	@Override
 	public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {
 	}
 
 	@Override
 	public abstract Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException;
 
 	/**
 	 * Get the session currently associated with this collection.
 	 *
 	 * @return The session
 	 */
 	public final SessionImplementor getSession() {
 		return session;
 	}
 
 	protected final class IteratorProxy implements Iterator {
 		protected final Iterator itr;
 
 		public IteratorProxy(Iterator itr) {
 			this.itr = itr;
 		}
 
 		@Override
 		public boolean hasNext() {
 			return itr.hasNext();
 		}
 
 		@Override
 		public Object next() {
 			return itr.next();
 		}
 
 		@Override
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
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public void add(Object o) {
 			write();
 			itr.add( o );
 		}
 
 		@Override
 		public boolean hasNext() {
 			return itr.hasNext();
 		}
 
 		@Override
 		public boolean hasPrevious() {
 			return itr.hasPrevious();
 		}
 
 		@Override
 		public Object next() {
 			return itr.next();
 		}
 
 		@Override
 		public int nextIndex() {
 			return itr.nextIndex();
 		}
 
 		@Override
 		public Object previous() {
 			return itr.previous();
 		}
 
 		@Override
 		public int previousIndex() {
 			return itr.previousIndex();
 		}
 
 		@Override
 		public void remove() {
 			write();
 			itr.remove();
 		}
 
 		@Override
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
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public boolean add(Object o) {
 			write();
 			return set.add( o );
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked"})
 		public boolean addAll(Collection c) {
 			write();
 			return set.addAll( c );
 		}
 
 		@Override
 		public void clear() {
 			write();
 			set.clear();
 		}
 
 		@Override
 		public boolean contains(Object o) {
 			return set.contains( o );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public boolean containsAll(Collection c) {
 			return set.containsAll( c );
 		}
 
 		@Override
 		public boolean isEmpty() {
 			return set.isEmpty();
 		}
 
 		@Override
 		public Iterator iterator() {
 			return new IteratorProxy( set.iterator() );
 		}
 
 		@Override
 		public boolean remove(Object o) {
 			write();
 			return set.remove( o );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public boolean removeAll(Collection c) {
 			write();
 			return set.removeAll( c );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public boolean retainAll(Collection c) {
 			write();
 			return set.retainAll( c );
 		}
 
 		@Override
 		public int size() {
 			return set.size();
 		}
 
 		@Override
 		public Object[] toArray() {
 			return set.toArray();
 		}
 
 		@Override
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
 		@SuppressWarnings("unchecked")
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
 		@SuppressWarnings("unchecked")
 		public boolean removeAll(Collection c) {
 			write();
 			return list.removeAll( c );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
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
 			// no new elements, the old list contains only Orphans
 			return oldElements;
 		}
 		if ( oldElements.size() == 0 ) {
 			// no old elements, so no Orphans neither
 			return oldElements;
 		}
 
 		final EntityPersister entityPersister = session.getFactory().getEntityPersister( entityName );
 		final Type idType = entityPersister.getIdentifierType();
 		final boolean useIdDirect = mayUseIdDirect( idType );
 
 		// create the collection holding the Orphans
 		final Collection res = new ArrayList();
 
 		// collect EntityIdentifier(s) of the *current* elements - add them into a HashSet for fast access
 		final java.util.Set currentIds = new HashSet();
 		final java.util.Set currentSaving = new IdentitySet();
 		for ( Object current : currentElements ) {
 			if ( current != null && ForeignKeys.isNotTransient( entityName, current, null, session ) ) {
 				final EntityEntry ee = session.getPersistenceContext().getEntry( current );
 				if ( ee != null && ee.getStatus() == Status.SAVING ) {
 					currentSaving.add( current );
 				}
 				else {
 					final Serializable currentId = ForeignKeys.getEntityIdentifierIfNotUnsaved(
 							entityName,
 							current,
 							session
 					);
 					currentIds.add( useIdDirect ? currentId : new TypedValue( idType, currentId ) );
 				}
 			}
 		}
 
 		// iterate over the *old* list
 		for ( Object old : oldElements ) {
 			if ( !currentSaving.contains( old ) ) {
 				final Serializable oldId = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, old, session );
 				if ( !currentIds.contains( useIdDirect ? oldId : new TypedValue( idType, oldId ) ) ) {
 					res.add( old );
 				}
 			}
 		}
 
 		return res;
 	}
 
 	private static boolean mayUseIdDirect(Type idType) {
 		return idType == StringType.INSTANCE
 			|| idType == IntegerType.INSTANCE
 			|| idType == LongType.INSTANCE
 			|| idType == UUIDBinaryType.INSTANCE
 			|| idType == UUIDCharType.INSTANCE
 			|| idType == PostgresUUIDType.INSTANCE;
 	}
 
 	/**
 	 * Removes entity entries that have an equal identifier with the incoming entity instance
 	 *
 	 * @param list The list containing the entity instances
 	 * @param entityInstance The entity instance to match elements.
 	 * @param entityName The entity name
 	 * @param session The session
 	 */
 	public static void identityRemove(
 			Collection list,
 			Object entityInstance,
 			String entityName,
 			SessionImplementor session) {
 
 		if ( entityInstance != null && ForeignKeys.isNotTransient( entityName, entityInstance, null, session ) ) {
 			final EntityPersister entityPersister = session.getFactory().getEntityPersister( entityName );
 			final Type idType = entityPersister.getIdentifierType();
 
 			final Serializable idOfCurrent = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, entityInstance, session );
 			final Iterator itr = list.iterator();
 			while ( itr.hasNext() ) {
 				final Serializable idOfOld = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, itr.next(), session );
 				if ( idType.isEqual( idOfCurrent, idOfOld, session.getFactory() ) ) {
 					itr.remove();
 					break;
 				}
 			}
 
 		}
 	}
 
 	@Override
 	public Object getIdentifier(Object entry, int i) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public Object getOwner() {
 		return owner;
 	}
 
 	@Override
 	public void setOwner(Object owner) {
 		this.owner = owner;
 	}
 
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java
index a1145696c0..abe0efa44e 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/HqlSqlWalker.java
@@ -1,1329 +1,1330 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.ast;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.QueryException;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.engine.internal.ParameterBinder;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.hql.internal.antlr.HqlSqlBaseWalker;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.antlr.HqlTokenTypes;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.tree.AggregateNode;
 import org.hibernate.hql.internal.ast.tree.AssignmentSpecification;
 import org.hibernate.hql.internal.ast.tree.CastFunctionNode;
 import org.hibernate.hql.internal.ast.tree.CollectionFunction;
 import org.hibernate.hql.internal.ast.tree.ConstructorNode;
 import org.hibernate.hql.internal.ast.tree.DeleteStatement;
 import org.hibernate.hql.internal.ast.tree.DotNode;
 import org.hibernate.hql.internal.ast.tree.FromClause;
 import org.hibernate.hql.internal.ast.tree.FromElement;
 import org.hibernate.hql.internal.ast.tree.FromElementFactory;
 import org.hibernate.hql.internal.ast.tree.FromReferenceNode;
 import org.hibernate.hql.internal.ast.tree.IdentNode;
 import org.hibernate.hql.internal.ast.tree.IndexNode;
 import org.hibernate.hql.internal.ast.tree.InsertStatement;
 import org.hibernate.hql.internal.ast.tree.IntoClause;
 import org.hibernate.hql.internal.ast.tree.MethodNode;
 import org.hibernate.hql.internal.ast.tree.OperatorNode;
 import org.hibernate.hql.internal.ast.tree.ParameterContainer;
 import org.hibernate.hql.internal.ast.tree.ParameterNode;
 import org.hibernate.hql.internal.ast.tree.QueryNode;
 import org.hibernate.hql.internal.ast.tree.ResolvableNode;
 import org.hibernate.hql.internal.ast.tree.RestrictableStatement;
 import org.hibernate.hql.internal.ast.tree.ResultVariableRefNode;
 import org.hibernate.hql.internal.ast.tree.SelectClause;
 import org.hibernate.hql.internal.ast.tree.SelectExpression;
 import org.hibernate.hql.internal.ast.tree.UpdateStatement;
 import org.hibernate.hql.internal.ast.util.ASTPrinter;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.internal.ast.util.AliasGenerator;
 import org.hibernate.hql.internal.ast.util.JoinProcessor;
 import org.hibernate.hql.internal.ast.util.LiteralProcessor;
 import org.hibernate.hql.internal.ast.util.NodeTraverser;
 import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
 import org.hibernate.hql.internal.ast.util.SyntheticAndFactory;
 import org.hibernate.hql.spi.QueryTranslator;
 import org.hibernate.id.BulkInsertionCapableIdentifierGenerator;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.param.CollectionFilterKeyParameterSpecification;
 import org.hibernate.param.NamedParameterSpecification;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.param.PositionalParameterSpecification;
 import org.hibernate.param.VersionTypeSeedParameterSpecification;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.ComponentType;
+import org.hibernate.type.CompositeType;
 import org.hibernate.type.DbTimestampType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 import org.hibernate.usertype.UserVersionType;
 
 import antlr.ASTFactory;
 import antlr.RecognitionException;
 import antlr.SemanticException;
 import antlr.collections.AST;
 
 /**
  * Implements methods used by the HQL->SQL tree transform grammar (a.k.a. the second phase).
  * <ul>
  * <li>Isolates the Hibernate API-specific code from the ANTLR generated code.</li>
  * <li>Handles the SQL fragments generated by the persisters in order to create the SELECT and FROM clauses,
  * taking into account the joins and projections that are implied by the mappings (persister/queryable).</li>
  * <li>Uses SqlASTFactory to create customized AST nodes.</li>
  * </ul>
  *
  * @see SqlASTFactory
  */
 public class HqlSqlWalker extends HqlSqlBaseWalker implements ErrorReporter, ParameterBinder.NamedParameterSource {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( HqlSqlWalker.class );
 
 	private final QueryTranslatorImpl queryTranslatorImpl;
 	private final HqlParser hqlParser;
 	private final SessionFactoryHelper sessionFactoryHelper;
 	private final Map tokenReplacements;
 	private final AliasGenerator aliasGenerator = new AliasGenerator();
 	private final LiteralProcessor literalProcessor;
 	private final ParseErrorHandler parseErrorHandler;
 	private final ASTPrinter printer;
 	private final String collectionFilterRole;
 
 	private FromClause currentFromClause;
 	private SelectClause selectClause;
 
 	/**
 	 * Maps each top-level result variable to its SelectExpression;
 	 * (excludes result variables defined in subqueries)
 	 */
 	private Map<String, SelectExpression> selectExpressionsByResultVariable = new HashMap<String, SelectExpression>();
 
 	private Set<Serializable> querySpaces = new HashSet<Serializable>();
 
 	private int parameterCount;
 	private Map namedParameters = new HashMap();
 	private ArrayList<ParameterSpecification> parameters = new ArrayList<ParameterSpecification>();
 	private int numberOfParametersInSetClause;
 	private int positionalParameterCount;
 
 	private ArrayList assignmentSpecifications = new ArrayList();
 
 	private JoinType impliedJoinType = JoinType.INNER_JOIN;
 
 	private boolean inEntityGraph;
 
 	/**
 	 * Create a new tree transformer.
 	 *
 	 * @param qti Back pointer to the query translator implementation that is using this tree transform.
 	 * @param sfi The session factory implementor where the Hibernate mappings can be found.
 	 * @param parser A reference to the phase-1 parser
 	 * @param tokenReplacements Registers the token replacement map with the walker.  This map will
 	 * be used to substitute function names and constants.
 	 * @param collectionRole The collection role name of the collection used as the basis for the
 	 * filter, NULL if this is not a collection filter compilation.
 	 */
 	public HqlSqlWalker(
 			QueryTranslatorImpl qti,
 			SessionFactoryImplementor sfi,
 			HqlParser parser,
 			Map tokenReplacements,
 			String collectionRole) {
 		setASTFactory( new SqlASTFactory( this ) );
 		// Initialize the error handling delegate.
 		this.parseErrorHandler = new ErrorCounter( qti.getQueryString() );
 		this.queryTranslatorImpl = qti;
 		this.sessionFactoryHelper = new SessionFactoryHelper( sfi );
 		this.literalProcessor = new LiteralProcessor( this );
 		this.tokenReplacements = tokenReplacements;
 		this.collectionFilterRole = collectionRole;
 		this.hqlParser = parser;
 		this.printer = new ASTPrinter( SqlTokenTypes.class );
 	}
 
 	// handle trace logging ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private int traceDepth;
 
 	@Override
 	public void traceIn(String ruleName, AST tree) {
 		if ( !LOG.isTraceEnabled() ) {
 			return;
 		}
 		if ( inputState.guessing > 0 ) {
 			return;
 		}
 		String prefix = StringHelper.repeat( '-', ( traceDepth++ * 2 ) ) + "-> ";
 		String traceText = ruleName + " (" + buildTraceNodeName( tree ) + ")";
 		LOG.trace( prefix + traceText );
 	}
 
 	private String buildTraceNodeName(AST tree) {
 		return tree == null
 				? "???"
 				: tree.getText() + " [" + printer.getTokenTypeName( tree.getType() ) + "]";
 	}
 
 	@Override
 	public void traceOut(String ruleName, AST tree) {
 		if ( !LOG.isTraceEnabled() ) {
 			return;
 		}
 		if ( inputState.guessing > 0 ) {
 			return;
 		}
 		String prefix = "<-" + StringHelper.repeat( '-', ( --traceDepth * 2 ) ) + " ";
 		LOG.trace( prefix + ruleName );
 	}
 
 	@Override
 	protected void prepareFromClauseInputTree(AST fromClauseInput) {
 		if ( !isSubQuery() ) {
 //			// inject param specifications to account for dynamic filter param values
 //			if ( ! getEnabledFilters().isEmpty() ) {
 //				Iterator filterItr = getEnabledFilters().values().iterator();
 //				while ( filterItr.hasNext() ) {
 //					FilterImpl filter = ( FilterImpl ) filterItr.next();
 //					if ( ! filter.getFilterDefinition().getParameterNames().isEmpty() ) {
 //						Iterator paramItr = filter.getFilterDefinition().getParameterNames().iterator();
 //						while ( paramItr.hasNext() ) {
 //							String parameterName = ( String ) paramItr.next();
 //							// currently param filters *only* work with single-column parameter types;
 //							// if that limitation is ever lifted, this logic will need to change to account for that
 //							ParameterNode collectionFilterKeyParameter = ( ParameterNode ) astFactory.create( PARAM, "?" );
 //							DynamicFilterParameterSpecification paramSpec = new DynamicFilterParameterSpecification(
 //									filter.getName(),
 //									parameterName,
 //									filter.getFilterDefinition().getParameterType( parameterName ),
 //									 positionalParameterCount++
 //							);
 //							collectionFilterKeyParameter.setHqlParameterSpecification( paramSpec );
 //							parameters.add( paramSpec );
 //						}
 //					}
 //				}
 //			}
 
 			if ( isFilter() ) {
 				// Handle collection-filter compilation.
 				// IMPORTANT NOTE: This is modifying the INPUT (HQL) tree, not the output tree!
 				QueryableCollection persister = sessionFactoryHelper.getCollectionPersister( collectionFilterRole );
 				Type collectionElementType = persister.getElementType();
 				if ( !collectionElementType.isEntityType() ) {
 					throw new QueryException( "collection of values in filter: this" );
 				}
 
 				String collectionElementEntityName = persister.getElementPersister().getEntityName();
 				ASTFactory inputAstFactory = hqlParser.getASTFactory();
 				AST fromElement = inputAstFactory.create( HqlTokenTypes.FILTER_ENTITY, collectionElementEntityName );
 				ASTUtil.createSibling( inputAstFactory, HqlTokenTypes.ALIAS, "this", fromElement );
 				fromClauseInput.addChild( fromElement );
 				// Show the modified AST.
 				LOG.debug( "prepareFromClauseInputTree() : Filter - Added 'this' as a from element..." );
 				queryTranslatorImpl.showHqlAst( hqlParser.getAST() );
 
 				// Create a parameter specification for the collection filter...
 				Type collectionFilterKeyType = sessionFactoryHelper.requireQueryableCollection( collectionFilterRole )
 						.getKeyType();
 				ParameterNode collectionFilterKeyParameter = (ParameterNode) astFactory.create( PARAM, "?" );
 				CollectionFilterKeyParameterSpecification collectionFilterKeyParameterSpec = new CollectionFilterKeyParameterSpecification(
 						collectionFilterRole, collectionFilterKeyType, positionalParameterCount++
 				);
 				collectionFilterKeyParameter.setHqlParameterSpecification( collectionFilterKeyParameterSpec );
 				parameters.add( collectionFilterKeyParameterSpec );
 			}
 		}
 	}
 
 	public boolean isFilter() {
 		return collectionFilterRole != null;
 	}
 
 	public String getCollectionFilterRole() {
 		return collectionFilterRole;
 	}
 
 	public boolean isInEntityGraph() {
 		return inEntityGraph;
 	}
 
 	public SessionFactoryHelper getSessionFactoryHelper() {
 		return sessionFactoryHelper;
 	}
 
 	public Map getTokenReplacements() {
 		return tokenReplacements;
 	}
 
 	public AliasGenerator getAliasGenerator() {
 		return aliasGenerator;
 	}
 
 	public FromClause getCurrentFromClause() {
 		return currentFromClause;
 	}
 
 	public ParseErrorHandler getParseErrorHandler() {
 		return parseErrorHandler;
 	}
 
 	@Override
 	public void reportError(RecognitionException e) {
 		parseErrorHandler.reportError( e ); // Use the delegate.
 	}
 
 	@Override
 	public void reportError(String s) {
 		parseErrorHandler.reportError( s ); // Use the delegate.
 	}
 
 	@Override
 	public void reportWarning(String s) {
 		parseErrorHandler.reportWarning( s );
 	}
 
 	/**
 	 * Returns the set of unique query spaces (a.k.a.
 	 * table names) that occurred in the query.
 	 *
 	 * @return A set of table names (Strings).
 	 */
 	public Set<Serializable> getQuerySpaces() {
 		return querySpaces;
 	}
 
 	@Override
 	protected AST createFromElement(String path, AST alias, AST propertyFetch) throws SemanticException {
 		FromElement fromElement = currentFromClause.addFromElement( path, alias );
 		fromElement.setAllPropertyFetch( propertyFetch != null );
 		return fromElement;
 	}
 
 	@Override
 	protected AST createFromFilterElement(AST filterEntity, AST alias) throws SemanticException {
 		FromElement fromElement = currentFromClause.addFromElement( filterEntity.getText(), alias );
 		FromClause fromClause = fromElement.getFromClause();
 		QueryableCollection persister = sessionFactoryHelper.getCollectionPersister( collectionFilterRole );
 		// Get the names of the columns used to link between the collection
 		// owner and the collection elements.
 		String[] keyColumnNames = persister.getKeyColumnNames();
 		String fkTableAlias = persister.isOneToMany()
 				? fromElement.getTableAlias()
 				: fromClause.getAliasGenerator().createName( collectionFilterRole );
 		JoinSequence join = sessionFactoryHelper.createJoinSequence();
 		join.setRoot( persister, fkTableAlias );
 		if ( !persister.isOneToMany() ) {
 			join.addJoin(
 					(AssociationType) persister.getElementType(),
 					fromElement.getTableAlias(),
 					JoinType.INNER_JOIN,
 					persister.getElementColumnNames( fkTableAlias )
 			);
 		}
 		join.addCondition( fkTableAlias, keyColumnNames, " = ?" );
 		fromElement.setJoinSequence( join );
 		fromElement.setFilter( true );
 		LOG.debug( "createFromFilterElement() : processed filter FROM element." );
 		return fromElement;
 	}
 
 	@Override
 	protected void createFromJoinElement(
 			AST path,
 			AST alias,
 			int joinType,
 			AST fetchNode,
 			AST propertyFetch,
 			AST with) throws SemanticException {
 		boolean fetch = fetchNode != null;
 		if ( fetch && isSubQuery() ) {
 			throw new QueryException( "fetch not allowed in subquery from-elements" );
 		}
 		// The path AST should be a DotNode, and it should have been evaluated already.
 		if ( path.getType() != SqlTokenTypes.DOT ) {
 			throw new SemanticException( "Path expected for join!" );
 		}
 		DotNode dot = (DotNode) path;
 		JoinType hibernateJoinType = JoinProcessor.toHibernateJoinType( joinType );
 		dot.setJoinType( hibernateJoinType );    // Tell the dot node about the join type.
 		dot.setFetch( fetch );
 		// Generate an explicit join for the root dot node.   The implied joins will be collected and passed up
 		// to the root dot node.
 		dot.resolve( true, false, alias == null ? null : alias.getText() );
 
 		final FromElement fromElement;
 		if ( dot.getDataType() != null && dot.getDataType().isComponentType() ) {
 			if ( dot.getDataType().isAnyType() ) {
 				throw new SemanticException( "An AnyType attribute cannot be join fetched" );
 				// ^^ because the discriminator (aka, the "meta columns") must be known to the SQL in
 				// 		a non-parameterized way.
 			}
 			FromElementFactory factory = new FromElementFactory(
 					getCurrentFromClause(),
 					dot.getLhs().getFromElement(),
 					dot.getPropertyPath(),
 					alias == null ? null : alias.getText(),
 					null,
 					false
 			);
-			fromElement = factory.createComponentJoin( (ComponentType) dot.getDataType() );
+			fromElement = factory.createComponentJoin( (CompositeType) dot.getDataType() );
 		}
 		else {
 			fromElement = dot.getImpliedJoin();
 			fromElement.setAllPropertyFetch( propertyFetch != null );
 
 			if ( with != null ) {
 				if ( fetch ) {
 					throw new SemanticException( "with-clause not allowed on fetched associations; use filters" );
 				}
 				handleWithFragment( fromElement, with );
 			}
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debug( "createFromJoinElement() : " + getASTPrinter().showAsString( fromElement, "-- join tree --" ) );
 		}
 	}
 
 	private void handleWithFragment(FromElement fromElement, AST hqlWithNode) throws SemanticException {
 		try {
 			withClause( hqlWithNode );
 			AST hqlSqlWithNode = returnAST;
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debug(
 						"handleWithFragment() : " + getASTPrinter().showAsString(
 								hqlSqlWithNode,
 								"-- with clause --"
 						)
 				);
 			}
 			WithClauseVisitor visitor = new WithClauseVisitor( fromElement, queryTranslatorImpl );
 			NodeTraverser traverser = new NodeTraverser( visitor );
 			traverser.traverseDepthFirst( hqlSqlWithNode );
 
 			String withClauseJoinAlias = visitor.getJoinAlias();
 			if ( withClauseJoinAlias == null ) {
 				withClauseJoinAlias = fromElement.getCollectionTableAlias();
 			}
 			else {
 				FromElement referencedFromElement = visitor.getReferencedFromElement();
 				if ( referencedFromElement != fromElement ) {
 					LOG.warnf(
 							"with-clause expressions do not reference the from-clause element to which the " +
 									"with-clause was associated.  The query may not work as expected [%s]",
 							queryTranslatorImpl.getQueryString()
 					);
 				}
 			}
 
 			SqlGenerator sql = new SqlGenerator( getSessionFactoryHelper().getFactory() );
 			sql.whereExpr( hqlSqlWithNode.getFirstChild() );
 
 			fromElement.setWithClauseFragment( withClauseJoinAlias, "(" + sql.getSQL() + ")" );
 		}
 		catch (SemanticException e) {
 			throw e;
 		}
 		catch (InvalidWithClauseException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new SemanticException( e.getMessage() );
 		}
 	}
 
 	private static class WithClauseVisitor implements NodeTraverser.VisitationStrategy {
 		private final FromElement joinFragment;
 		private final QueryTranslatorImpl queryTranslatorImpl;
 
 		private FromElement referencedFromElement;
 		private String joinAlias;
 
 		public WithClauseVisitor(FromElement fromElement, QueryTranslatorImpl queryTranslatorImpl) {
 			this.joinFragment = fromElement;
 			this.queryTranslatorImpl = queryTranslatorImpl;
 		}
 
 		public void visit(AST node) {
 			// TODO : currently expects that the individual with expressions apply to the same sql table join.
 			//      This may not be the case for joined-subclass where the property values
 			//      might be coming from different tables in the joined hierarchy.  At some
 			//      point we should expand this to support that capability.  However, that has
 			//      some difficulties:
 			//          1) the biggest is how to handle ORs when the individual comparisons are
 			//              linked to different sql joins.
 			//          2) here we would need to track each comparison individually, along with
 			//              the join alias to which it applies and then pass that information
 			//              back to the FromElement so it can pass it along to the JoinSequence
 			if ( node instanceof DotNode ) {
 				DotNode dotNode = (DotNode) node;
 				FromElement fromElement = dotNode.getFromElement();
 				if ( referencedFromElement != null ) {
 					if ( fromElement != referencedFromElement ) {
 						throw new HibernateException( "with-clause referenced two different from-clause elements" );
 					}
 				}
 				else {
 					referencedFromElement = fromElement;
 					joinAlias = extractAppliedAlias( dotNode );
 					// TODO : temporary
 					//      needed because currently persister is the one that
 					// creates and renders the join fragments for inheritance
 					//      hierarchies...
 					if ( !joinAlias.equals( referencedFromElement.getTableAlias() ) ) {
 						throw new InvalidWithClauseException(
 								"with clause can only reference columns in the driving table",
 								queryTranslatorImpl.getQueryString()
 						);
 					}
 				}
 			}
 			else if ( node instanceof ParameterNode ) {
 				applyParameterSpecification( ( (ParameterNode) node ).getHqlParameterSpecification() );
 			}
 			else if ( node instanceof ParameterContainer ) {
 				applyParameterSpecifications( (ParameterContainer) node );
 			}
 		}
 
 		private void applyParameterSpecifications(ParameterContainer parameterContainer) {
 			if ( parameterContainer.hasEmbeddedParameters() ) {
 				ParameterSpecification[] specs = parameterContainer.getEmbeddedParameters();
 				for ( ParameterSpecification spec : specs ) {
 					applyParameterSpecification( spec );
 				}
 			}
 		}
 
 		private void applyParameterSpecification(ParameterSpecification paramSpec) {
 			joinFragment.addEmbeddedParameter( paramSpec );
 		}
 
 		private String extractAppliedAlias(DotNode dotNode) {
 			return dotNode.getText().substring( 0, dotNode.getText().indexOf( '.' ) );
 		}
 
 		public FromElement getReferencedFromElement() {
 			return referencedFromElement;
 		}
 
 		public String getJoinAlias() {
 			return joinAlias;
 		}
 	}
 
 	/**
 	 * Sets the current 'FROM' context.
 	 *
 	 * @param fromNode The new 'FROM' context.
 	 * @param inputFromNode The from node from the input AST.
 	 */
 	@Override
 	protected void pushFromClause(AST fromNode, AST inputFromNode) {
 		FromClause newFromClause = (FromClause) fromNode;
 		newFromClause.setParentFromClause( currentFromClause );
 		currentFromClause = newFromClause;
 	}
 
 	/**
 	 * Returns to the previous 'FROM' context.
 	 */
 	private void popFromClause() {
 		currentFromClause = currentFromClause.getParentFromClause();
 	}
 
 	@Override
 	protected void lookupAlias(AST aliasRef)
 			throws SemanticException {
 		FromElement alias = currentFromClause.getFromElement( aliasRef.getText() );
 		FromReferenceNode aliasRefNode = (FromReferenceNode) aliasRef;
 		aliasRefNode.setFromElement( alias );
 	}
 
 	@Override
 	protected void setImpliedJoinType(int joinType) {
 		impliedJoinType = JoinProcessor.toHibernateJoinType( joinType );
 	}
 
 	public JoinType getImpliedJoinType() {
 		return impliedJoinType;
 	}
 
 	@Override
 	protected AST lookupProperty(AST dot, boolean root, boolean inSelect) throws SemanticException {
 		DotNode dotNode = (DotNode) dot;
 		FromReferenceNode lhs = dotNode.getLhs();
 		AST rhs = lhs.getNextSibling();
 		switch ( rhs.getType() ) {
 			case SqlTokenTypes.ELEMENTS:
 			case SqlTokenTypes.INDICES:
 				if ( LOG.isDebugEnabled() ) {
 					LOG.debugf(
 							"lookupProperty() %s => %s(%s)",
 							dotNode.getPath(),
 							rhs.getText(),
 							lhs.getPath()
 					);
 				}
 				CollectionFunction f = (CollectionFunction) rhs;
 				// Re-arrange the tree so that the collection function is the root and the lhs is the path.
 				f.setFirstChild( lhs );
 				lhs.setNextSibling( null );
 				dotNode.setFirstChild( f );
 				resolve( lhs );            // Don't forget to resolve the argument!
 				f.resolve( inSelect );    // Resolve the collection function now.
 				return f;
 			default:
 				// Resolve everything up to this dot, but don't resolve the placeholders yet.
 				dotNode.resolveFirstChild();
 				return dotNode;
 		}
 	}
 
 	@Override
 	protected boolean isNonQualifiedPropertyRef(AST ident) {
 		final String identText = ident.getText();
 		if ( currentFromClause.isFromElementAlias( identText ) ) {
 			return false;
 		}
 
 		List fromElements = currentFromClause.getExplicitFromElements();
 		if ( fromElements.size() == 1 ) {
 			final FromElement fromElement = (FromElement) fromElements.get( 0 );
 			try {
 				LOG.tracev( "Attempting to resolve property [{0}] as a non-qualified ref", identText );
 				return fromElement.getPropertyMapping( identText ).toType( identText ) != null;
 			}
 			catch (QueryException e) {
 				// Should mean that no such property was found
 			}
 		}
 
 		return false;
 	}
 
 	@Override
 	protected AST lookupNonQualifiedProperty(AST property) throws SemanticException {
 		final FromElement fromElement = (FromElement) currentFromClause.getExplicitFromElements().get( 0 );
 		AST syntheticDotNode = generateSyntheticDotNodeForNonQualifiedPropertyRef( property, fromElement );
 		return lookupProperty( syntheticDotNode, false, getCurrentClauseType() == HqlSqlTokenTypes.SELECT );
 	}
 
 	private AST generateSyntheticDotNodeForNonQualifiedPropertyRef(AST property, FromElement fromElement) {
 		AST dot = getASTFactory().create( DOT, "{non-qualified-property-ref}" );
 		// TODO : better way?!?
 		( (DotNode) dot ).setPropertyPath( ( (FromReferenceNode) property ).getPath() );
 
 		IdentNode syntheticAlias = (IdentNode) getASTFactory().create( IDENT, "{synthetic-alias}" );
 		syntheticAlias.setFromElement( fromElement );
 		syntheticAlias.setResolved();
 
 		dot.setFirstChild( syntheticAlias );
 		dot.addChild( property );
 
 		return dot;
 	}
 
 	@Override
 	protected void processQuery(AST select, AST query) throws SemanticException {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "processQuery() : %s", query.toStringTree() );
 		}
 
 		try {
 			QueryNode qn = (QueryNode) query;
 
 			// Was there an explicit select expression?
 			boolean explicitSelect = select != null && select.getNumberOfChildren() > 0;
 
 			// Add in the EntityGraph attribute nodes.
 			if ( queryTranslatorImpl.getEntityGraphQueryHint() != null ) {
 				final boolean oldInEntityGraph = inEntityGraph;
 				try {
 					inEntityGraph = true;
 					qn.getFromClause().getFromElements().addAll(
 							queryTranslatorImpl.getEntityGraphQueryHint().toFromElements( qn.getFromClause(), this )
 					);
 				}
 				finally {
 					inEntityGraph = oldInEntityGraph;
 				}
 			}
 
 			if ( !explicitSelect ) {
 				// No explicit select expression; render the id and properties
 				// projection lists for every persister in the from clause into
 				// a single 'token node'.
 				//TODO: the only reason we need this stuff now is collection filters,
 				//      we should get rid of derived select clause completely!
 				createSelectClauseFromFromClause( qn );
 			}
 			else {
 				// Use the explicitly declared select expression; determine the
 				// return types indicated by each select token
 				useSelectClause( select );
 			}
 
 			// After that, process the JOINs.
 			// Invoke a delegate to do the work, as this is farily complex.
 			JoinProcessor joinProcessor = new JoinProcessor( this );
 			joinProcessor.processJoins( qn );
 
 			// Attach any mapping-defined "ORDER BY" fragments
 			Iterator itr = qn.getFromClause().getProjectionList().iterator();
 			while ( itr.hasNext() ) {
 				final FromElement fromElement = (FromElement) itr.next();
 //			if ( fromElement.isFetch() && fromElement.isCollectionJoin() ) {
 				if ( fromElement.isFetch() && fromElement.getQueryableCollection() != null ) {
 					// Does the collection referenced by this FromElement
 					// specify an order-by attribute?  If so, attach it to
 					// the query's order-by
 					if ( fromElement.getQueryableCollection().hasOrdering() ) {
 						String orderByFragment = fromElement
 								.getQueryableCollection()
 								.getSQLOrderByString( fromElement.getCollectionTableAlias() );
 						qn.getOrderByClause().addOrderFragment( orderByFragment );
 					}
 					if ( fromElement.getQueryableCollection().hasManyToManyOrdering() ) {
 						String orderByFragment = fromElement.getQueryableCollection()
 								.getManyToManyOrderByString( fromElement.getTableAlias() );
 						qn.getOrderByClause().addOrderFragment( orderByFragment );
 					}
 				}
 			}
 		}
 		finally {
 			popFromClause();
 		}
 	}
 
 	protected void postProcessDML(RestrictableStatement statement) throws SemanticException {
 		statement.getFromClause().resolve();
 
 		FromElement fromElement = (FromElement) statement.getFromClause().getFromElements().get( 0 );
 		Queryable persister = fromElement.getQueryable();
 		// Make #@%$^#^&# sure no alias is applied to the table name
 		fromElement.setText( persister.getTableName() );
 
 //		// append any filter fragments; the EMPTY_MAP is used under the assumption that
 //		// currently enabled filters should not affect this process
 //		if ( persister.getDiscriminatorType() != null ) {
 //			new SyntheticAndFactory( getASTFactory() ).addDiscriminatorWhereFragment(
 //			        statement,
 //			        persister,
 //			        java.util.Collections.EMPTY_MAP,
 //			        fromElement.getTableAlias()
 //			);
 //		}
 		if ( persister.getDiscriminatorType() != null || !queryTranslatorImpl.getEnabledFilters().isEmpty() ) {
 			new SyntheticAndFactory( this ).addDiscriminatorWhereFragment(
 					statement,
 					persister,
 					queryTranslatorImpl.getEnabledFilters(),
 					fromElement.getTableAlias()
 			);
 		}
 
 	}
 
 	@Override
 	protected void postProcessUpdate(AST update) throws SemanticException {
 		UpdateStatement updateStatement = (UpdateStatement) update;
 
 		postProcessDML( updateStatement );
 	}
 
 	@Override
 	protected void postProcessDelete(AST delete) throws SemanticException {
 		postProcessDML( (DeleteStatement) delete );
 	}
 
 	@Override
 	protected void postProcessInsert(AST insert) throws SemanticException, QueryException {
 		InsertStatement insertStatement = (InsertStatement) insert;
 		insertStatement.validate();
 
 		SelectClause selectClause = insertStatement.getSelectClause();
 		Queryable persister = insertStatement.getIntoClause().getQueryable();
 
 		if ( !insertStatement.getIntoClause().isExplicitIdInsertion() ) {
 			// the insert did not explicitly reference the id.  See if
 			//		1) that is allowed
 			//		2) whether we need to alter the SQL tree to account for id
 			final IdentifierGenerator generator = persister.getIdentifierGenerator();
 			if ( !BulkInsertionCapableIdentifierGenerator.class.isInstance( generator ) ) {
 				throw new QueryException(
 						"Invalid identifier generator encountered for implicit id handling as part of bulk insertions"
 				);
 			}
 			final BulkInsertionCapableIdentifierGenerator capableGenerator =
 					BulkInsertionCapableIdentifierGenerator.class.cast( generator );
 			if ( !capableGenerator.supportsBulkInsertionIdentifierGeneration() ) {
 				throw new QueryException(
 						"Identifier generator reported it does not support implicit id handling as part of bulk insertions"
 				);
 			}
 
 			final String fragment = capableGenerator.determineBulkInsertionIdentifierGenerationSelectFragment(
 					sessionFactoryHelper.getFactory().getDialect()
 			);
 			if ( fragment != null ) {
 				// we got a fragment from the generator, so alter the sql tree...
 				//
 				// first, wrap the fragment as a node
 				AST fragmentNode = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, fragment );
 				// next, rearrange the SQL tree to add the fragment node as the first select expression
 				AST originalFirstSelectExprNode = selectClause.getFirstChild();
 				selectClause.setFirstChild( fragmentNode );
 				fragmentNode.setNextSibling( originalFirstSelectExprNode );
 				// finally, prepend the id column name(s) to the insert-spec
 				insertStatement.getIntoClause().prependIdColumnSpec();
 			}
 		}
 
 		if ( sessionFactoryHelper.getFactory().getDialect().supportsParametersInInsertSelect() ) {
 			AST child = selectClause.getFirstChild();
 			int i = 0;
 			while ( child != null ) {
 				if ( child instanceof ParameterNode ) {
 					// infer the parameter type from the type listed in the INSERT INTO clause
 					( (ParameterNode) child ).setExpectedType(
 							insertStatement.getIntoClause()
 									.getInsertionTypes()[selectClause.getParameterPositions().get( i )]
 					);
 					i++;
 				}
 				child = child.getNextSibling();
 			}
 		}
 
 		final boolean includeVersionProperty = persister.isVersioned() &&
 				!insertStatement.getIntoClause().isExplicitVersionInsertion() &&
 				persister.isVersionPropertyInsertable();
 		if ( includeVersionProperty ) {
 			// We need to seed the version value as part of this bulk insert
 			VersionType versionType = persister.getVersionType();
 			AST versionValueNode = null;
 
 			if ( sessionFactoryHelper.getFactory().getDialect().supportsParametersInInsertSelect() ) {
 				int[] sqlTypes = versionType.sqlTypes( sessionFactoryHelper.getFactory() );
 				if ( sqlTypes == null || sqlTypes.length == 0 ) {
 					throw new IllegalStateException( versionType.getClass() + ".sqlTypes() returns null or empty array" );
 				}
 				if ( sqlTypes.length > 1 ) {
 					throw new IllegalStateException(
 							versionType.getClass() +
 									".sqlTypes() returns > 1 element; only single-valued versions are allowed."
 					);
 				}
 				versionValueNode = getASTFactory().create( HqlSqlTokenTypes.PARAM, "?" );
 				ParameterSpecification paramSpec = new VersionTypeSeedParameterSpecification( versionType );
 				( (ParameterNode) versionValueNode ).setHqlParameterSpecification( paramSpec );
 				parameters.add( 0, paramSpec );
 
 				if ( sessionFactoryHelper.getFactory().getDialect().requiresCastingOfParametersInSelectClause() ) {
 					// we need to wrtap the param in a cast()
 					MethodNode versionMethodNode = (MethodNode) getASTFactory().create(
 							HqlSqlTokenTypes.METHOD_CALL,
 							"("
 					);
 					AST methodIdentNode = getASTFactory().create( HqlSqlTokenTypes.IDENT, "cast" );
 					versionMethodNode.addChild( methodIdentNode );
 					versionMethodNode.initializeMethodNode( methodIdentNode, true );
 					AST castExprListNode = getASTFactory().create( HqlSqlTokenTypes.EXPR_LIST, "exprList" );
 					methodIdentNode.setNextSibling( castExprListNode );
 					castExprListNode.addChild( versionValueNode );
 					versionValueNode.setNextSibling(
 							getASTFactory().create(
 									HqlSqlTokenTypes.IDENT,
 									sessionFactoryHelper.getFactory().getDialect().getTypeName( sqlTypes[0] )
 							)
 					);
 					processFunction( versionMethodNode, true );
 					versionValueNode = versionMethodNode;
 				}
 			}
 			else {
 				if ( isIntegral( versionType ) ) {
 					try {
 						Object seedValue = versionType.seed( null );
 						versionValueNode = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, seedValue.toString() );
 					}
 					catch (Throwable t) {
 						throw new QueryException( "could not determine seed value for version on bulk insert [" + versionType + "]" );
 					}
 				}
 				else if ( isDatabaseGeneratedTimestamp( versionType ) ) {
 					String functionName = sessionFactoryHelper.getFactory()
 							.getDialect()
 							.getCurrentTimestampSQLFunctionName();
 					versionValueNode = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, functionName );
 				}
 				else {
 					throw new QueryException( "cannot handle version type [" + versionType + "] on bulk inserts with dialects not supporting parameters in insert-select statements" );
 				}
 			}
 
 			AST currentFirstSelectExprNode = selectClause.getFirstChild();
 			selectClause.setFirstChild( versionValueNode );
 			versionValueNode.setNextSibling( currentFirstSelectExprNode );
 
 			insertStatement.getIntoClause().prependVersionColumnSpec();
 		}
 
 		if ( insertStatement.getIntoClause().isDiscriminated() ) {
 			String sqlValue = insertStatement.getIntoClause().getQueryable().getDiscriminatorSQLValue();
 			AST discrimValue = getASTFactory().create( HqlSqlTokenTypes.SQL_TOKEN, sqlValue );
 			insertStatement.getSelectClause().addChild( discrimValue );
 		}
 
 	}
 
 	private boolean isDatabaseGeneratedTimestamp(Type type) {
 		// currently only the Hibernate-supplied DbTimestampType is supported here
 		return DbTimestampType.class.isAssignableFrom( type.getClass() );
 	}
 
 	private boolean isIntegral(Type type) {
 		return Long.class.isAssignableFrom( type.getReturnedClass() )
 				|| Integer.class.isAssignableFrom( type.getReturnedClass() )
 				|| long.class.isAssignableFrom( type.getReturnedClass() )
 				|| int.class.isAssignableFrom( type.getReturnedClass() );
 	}
 
 	private void useSelectClause(AST select) throws SemanticException {
 		selectClause = (SelectClause) select;
 		selectClause.initializeExplicitSelectClause( currentFromClause );
 	}
 
 	private void createSelectClauseFromFromClause(QueryNode qn) throws SemanticException {
 		AST select = astFactory.create( SELECT_CLAUSE, "{derived select clause}" );
 		AST sibling = qn.getFromClause();
 		qn.setFirstChild( select );
 		select.setNextSibling( sibling );
 		selectClause = (SelectClause) select;
 		selectClause.initializeDerivedSelectClause( currentFromClause );
 		LOG.debug( "Derived SELECT clause created." );
 	}
 
 	@Override
 	protected void resolve(AST node) throws SemanticException {
 		if ( node != null ) {
 			// This is called when it's time to fully resolve a path expression.
 			ResolvableNode r = (ResolvableNode) node;
 			if ( isInFunctionCall() ) {
 				r.resolveInFunctionCall( false, true );
 			}
 			else {
 				r.resolve( false, true );    // Generate implicit joins, only if necessary.
 			}
 		}
 	}
 
 	@Override
 	protected void resolveSelectExpression(AST node) throws SemanticException {
 		// This is called when it's time to fully resolve a path expression.
 		int type = node.getType();
 		switch ( type ) {
 			case DOT: {
 				DotNode dot = (DotNode) node;
 				dot.resolveSelectExpression();
 				break;
 			}
 			case ALIAS_REF: {
 				// Notify the FROM element that it is being referenced by the select.
 				FromReferenceNode aliasRefNode = (FromReferenceNode) node;
 				//aliasRefNode.resolve( false, false, aliasRefNode.getText() ); //TODO: is it kosher to do it here?
 				aliasRefNode.resolve( false, false ); //TODO: is it kosher to do it here?
 				FromElement fromElement = aliasRefNode.getFromElement();
 				if ( fromElement != null ) {
 					fromElement.setIncludeSubclasses( true );
 				}
 				break;
 			}
 			default: {
 				break;
 			}
 		}
 	}
 
 	@Override
 	protected void beforeSelectClause() throws SemanticException {
 		// Turn off includeSubclasses on all FromElements.
 		FromClause from = getCurrentFromClause();
 		List fromElements = from.getFromElements();
 		for ( Iterator iterator = fromElements.iterator(); iterator.hasNext(); ) {
 			FromElement fromElement = (FromElement) iterator.next();
 			fromElement.setIncludeSubclasses( false );
 		}
 	}
 
 	@Override
 	protected AST generatePositionalParameter(AST inputNode) throws SemanticException {
 		if ( namedParameters.size() > 0 ) {
 			throw new SemanticException(
 					"cannot define positional parameter after any named parameters have been defined"
 			);
 		}
 		LOG.warnf(
 				"[DEPRECATION] Encountered positional parameter near line %s, column %s in HQL: [%s].  Positional parameter " +
 						"are considered deprecated; use named parameters or JPA-style positional parameters instead.",
 				inputNode.getLine(),
 				inputNode.getColumn(),
 				queryTranslatorImpl.getQueryString()
 		);
 		ParameterNode parameter = (ParameterNode) astFactory.create( PARAM, "?" );
 		PositionalParameterSpecification paramSpec = new PositionalParameterSpecification(
 				inputNode.getLine(),
 				inputNode.getColumn(),
 				positionalParameterCount++
 		);
 		parameter.setHqlParameterSpecification( paramSpec );
 		parameters.add( paramSpec );
 		return parameter;
 	}
 
 	@Override
 	protected AST generateNamedParameter(AST delimiterNode, AST nameNode) throws SemanticException {
 		String name = nameNode.getText();
 		trackNamedParameterPositions( name );
 
 		// create the node initially with the param name so that it shows
 		// appropriately in the "original text" attribute
 		ParameterNode parameter = (ParameterNode) astFactory.create( NAMED_PARAM, name );
 		parameter.setText( "?" );
 
 		NamedParameterSpecification paramSpec = new NamedParameterSpecification(
 				delimiterNode.getLine(),
 				delimiterNode.getColumn(),
 				name
 		);
 		parameter.setHqlParameterSpecification( paramSpec );
 		parameters.add( paramSpec );
 		return parameter;
 	}
 
 	private void trackNamedParameterPositions(String name) {
 		Integer loc = parameterCount++;
 		Object o = namedParameters.get( name );
 		if ( o == null ) {
 			namedParameters.put( name, loc );
 		}
 		else if ( o instanceof Integer ) {
 			ArrayList list = new ArrayList( 4 );
 			list.add( o );
 			list.add( loc );
 			namedParameters.put( name, list );
 		}
 		else {
 			( (ArrayList) o ).add( loc );
 		}
 	}
 
 	@Override
 	protected void processConstant(AST constant) throws SemanticException {
 		literalProcessor.processConstant(
 				constant,
 				true
 		);  // Use the delegate, resolve identifiers as FROM element aliases.
 	}
 
 	@Override
 	protected void processBoolean(AST constant) throws SemanticException {
 		literalProcessor.processBoolean( constant );  // Use the delegate.
 	}
 
 	@Override
 	protected void processNumericLiteral(AST literal) {
 		literalProcessor.processNumeric( literal );
 	}
 
 	@Override
 	protected void processIndex(AST indexOp) throws SemanticException {
 		IndexNode indexNode = (IndexNode) indexOp;
 		indexNode.resolve( true, true );
 	}
 
 	@Override
 	protected void processFunction(AST functionCall, boolean inSelect) throws SemanticException {
 		MethodNode methodNode = (MethodNode) functionCall;
 		methodNode.resolve( inSelect );
 	}
 
 	@Override
 	protected void processCastFunction(AST castFunctionCall, boolean inSelect) throws SemanticException {
 		CastFunctionNode castFunctionNode = (CastFunctionNode) castFunctionCall;
 		castFunctionNode.resolve( inSelect );
 	}
 
 	@Override
 	protected void processAggregation(AST node, boolean inSelect) throws SemanticException {
 		AggregateNode aggregateNode = (AggregateNode) node;
 		aggregateNode.resolve();
 	}
 
 	@Override
 	protected void processConstructor(AST constructor) throws SemanticException {
 		ConstructorNode constructorNode = (ConstructorNode) constructor;
 		constructorNode.prepare();
 	}
 
 	@Override
 	protected void setAlias(AST selectExpr, AST ident) {
 		( (SelectExpression) selectExpr ).setAlias( ident.getText() );
 		// only put the alias (i.e., result variable) in selectExpressionsByResultVariable
 		// if is not defined in a subquery.
 		if ( !isSubQuery() ) {
 			selectExpressionsByResultVariable.put( ident.getText(), (SelectExpression) selectExpr );
 		}
 	}
 
 	@Override
 	protected boolean isOrderExpressionResultVariableRef(AST orderExpressionNode) throws SemanticException {
 		// ORDER BY is not supported in a subquery
 		// TODO: should an exception be thrown if an ORDER BY is in a subquery?
 		if ( !isSubQuery() &&
 				orderExpressionNode.getType() == IDENT &&
 				selectExpressionsByResultVariable.containsKey( orderExpressionNode.getText() ) ) {
 			return true;
 		}
 		return false;
 	}
 
 	@Override
 	protected void handleResultVariableRef(AST resultVariableRef) throws SemanticException {
 		if ( isSubQuery() ) {
 			throw new SemanticException(
 					"References to result variables in subqueries are not supported."
 			);
 		}
 		( (ResultVariableRefNode) resultVariableRef ).setSelectExpression(
 				selectExpressionsByResultVariable.get( resultVariableRef.getText() )
 		);
 	}
 
 	/**
 	 * Returns the locations of all occurrences of the named parameter.
 	 */
 	public int[] getNamedParameterLocations(String name) throws QueryException {
 		Object o = namedParameters.get( name );
 		if ( o == null ) {
 			throw new QueryException(
 					QueryTranslator.ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR + name,
 					queryTranslatorImpl.getQueryString()
 			);
 		}
 		if ( o instanceof Integer ) {
 			return new int[] {(Integer) o};
 		}
 		else {
 			return ArrayHelper.toIntArray( (ArrayList) o );
 		}
 	}
 
 	public void addQuerySpaces(Serializable[] spaces) {
 		querySpaces.addAll( Arrays.asList( spaces ) );
 	}
 
 	public Type[] getReturnTypes() {
 		return selectClause.getQueryReturnTypes();
 	}
 
 	public String[] getReturnAliases() {
 		return selectClause.getQueryReturnAliases();
 	}
 
 	public SelectClause getSelectClause() {
 		return selectClause;
 	}
 
 	public FromClause getFinalFromClause() {
 		FromClause top = currentFromClause;
 		while ( top.getParentFromClause() != null ) {
 			top = top.getParentFromClause();
 		}
 		return top;
 	}
 
 	public boolean isShallowQuery() {
 		// select clauses for insert statements should alwasy be treated as shallow
 		return getStatementType() == INSERT || queryTranslatorImpl.isShallowQuery();
 	}
 
 	public Map getEnabledFilters() {
 		return queryTranslatorImpl.getEnabledFilters();
 	}
 
 	public LiteralProcessor getLiteralProcessor() {
 		return literalProcessor;
 	}
 
 	public ASTPrinter getASTPrinter() {
 		return printer;
 	}
 
 	public ArrayList<ParameterSpecification> getParameters() {
 		return parameters;
 	}
 
 	public int getNumberOfParametersInSetClause() {
 		return numberOfParametersInSetClause;
 	}
 
 	@Override
 	protected void evaluateAssignment(AST eq) throws SemanticException {
 		prepareLogicOperator( eq );
 		Queryable persister = getCurrentFromClause().getFromElement().getQueryable();
 		evaluateAssignment( eq, persister, -1 );
 	}
 
 	private void evaluateAssignment(AST eq, Queryable persister, int targetIndex) {
 		if ( persister.isMultiTable() ) {
 			// no need to even collect this information if the persister is considered multi-table
 			AssignmentSpecification specification = new AssignmentSpecification( eq, persister );
 			if ( targetIndex >= 0 ) {
 				assignmentSpecifications.add( targetIndex, specification );
 			}
 			else {
 				assignmentSpecifications.add( specification );
 			}
 			numberOfParametersInSetClause += specification.getParameters().length;
 		}
 	}
 
 	public ArrayList getAssignmentSpecifications() {
 		return assignmentSpecifications;
 	}
 
 	@Override
 	protected AST createIntoClause(String path, AST propertySpec) throws SemanticException {
 		Queryable persister = (Queryable) getSessionFactoryHelper().requireClassPersister( path );
 
 		IntoClause intoClause = (IntoClause) getASTFactory().create( INTO, persister.getEntityName() );
 		intoClause.setFirstChild( propertySpec );
 		intoClause.initialize( persister );
 
 		addQuerySpaces( persister.getQuerySpaces() );
 
 		return intoClause;
 	}
 
 	@Override
 	protected void prepareVersioned(AST updateNode, AST versioned) throws SemanticException {
 		UpdateStatement updateStatement = (UpdateStatement) updateNode;
 		FromClause fromClause = updateStatement.getFromClause();
 		if ( versioned != null ) {
 			// Make sure that the persister is versioned
 			Queryable persister = fromClause.getFromElement().getQueryable();
 			if ( !persister.isVersioned() ) {
 				throw new SemanticException( "increment option specified for update of non-versioned entity" );
 			}
 
 			VersionType versionType = persister.getVersionType();
 			if ( versionType instanceof UserVersionType ) {
 				throw new SemanticException( "user-defined version types not supported for increment option" );
 			}
 
 			AST eq = getASTFactory().create( HqlSqlTokenTypes.EQ, "=" );
 			AST versionPropertyNode = generateVersionPropertyNode( persister );
 
 			eq.setFirstChild( versionPropertyNode );
 
 			AST versionIncrementNode = null;
 			if ( isTimestampBasedVersion( versionType ) ) {
 				versionIncrementNode = getASTFactory().create( HqlSqlTokenTypes.PARAM, "?" );
 				ParameterSpecification paramSpec = new VersionTypeSeedParameterSpecification( versionType );
 				( (ParameterNode) versionIncrementNode ).setHqlParameterSpecification( paramSpec );
 				parameters.add( 0, paramSpec );
 			}
 			else {
 				// Not possible to simply re-use the versionPropertyNode here as it causes
 				// OOM errors due to circularity :(
 				versionIncrementNode = getASTFactory().create( HqlSqlTokenTypes.PLUS, "+" );
 				versionIncrementNode.setFirstChild( generateVersionPropertyNode( persister ) );
 				versionIncrementNode.addChild( getASTFactory().create( HqlSqlTokenTypes.IDENT, "1" ) );
 			}
 
 			eq.addChild( versionIncrementNode );
 
 			evaluateAssignment( eq, persister, 0 );
 
 			AST setClause = updateStatement.getSetClause();
 			AST currentFirstSetElement = setClause.getFirstChild();
 			setClause.setFirstChild( eq );
 			eq.setNextSibling( currentFirstSetElement );
 		}
 	}
 
 	private boolean isTimestampBasedVersion(VersionType versionType) {
 		final Class javaType = versionType.getReturnedClass();
 		return Date.class.isAssignableFrom( javaType )
 				|| Calendar.class.isAssignableFrom( javaType );
 	}
 
 	private AST generateVersionPropertyNode(Queryable persister) throws SemanticException {
 		String versionPropertyName = persister.getPropertyNames()[persister.getVersionProperty()];
 		AST versionPropertyRef = getASTFactory().create( HqlSqlTokenTypes.IDENT, versionPropertyName );
 		AST versionPropertyNode = lookupNonQualifiedProperty( versionPropertyRef );
 		resolve( versionPropertyNode );
 		return versionPropertyNode;
 	}
 
 	@Override
 	protected void prepareLogicOperator(AST operator) throws SemanticException {
 		( (OperatorNode) operator ).initialize();
 	}
 
 	@Override
 	protected void prepareArithmeticOperator(AST operator) throws SemanticException {
 		( (OperatorNode) operator ).initialize();
 	}
 
 	@Override
 	protected void validateMapPropertyExpression(AST node) throws SemanticException {
 		try {
 			FromReferenceNode fromReferenceNode = (FromReferenceNode) node;
 			QueryableCollection collectionPersister = fromReferenceNode.getFromElement().getQueryableCollection();
 			if ( !Map.class.isAssignableFrom( collectionPersister.getCollectionType().getReturnedClass() ) ) {
 				throw new SemanticException( "node did not reference a map" );
 			}
 		}
 		catch (SemanticException se) {
 			throw se;
 		}
 		catch (Throwable t) {
 			throw new SemanticException( "node did not reference a map" );
 		}
 	}
 
 	public Set<String> getTreatAsDeclarationsByPath(String path) {
 		return hqlParser.getTreatMap().get( path );
 	}
 
 	public static void panic() {
 		throw new QueryException( "TreeWalker: panic" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/ComponentJoin.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/ComponentJoin.java
index 35f49313a0..2ea9e55d42 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/ComponentJoin.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/ComponentJoin.java
@@ -1,165 +1,165 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.ast.tree;
 
 import org.hibernate.QueryException;
 import org.hibernate.hql.internal.NameGenerator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.PropertyMapping;
-import org.hibernate.type.ComponentType;
+import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * Models an explicit join terminating at a component value (e.g. <tt>... from Person p join p.name as n ...</tt>)
  *
  * @author Steve Ebersole
  */
 public class ComponentJoin extends FromElement {
 	private final String componentPath;
-	private final ComponentType componentType;
+	private final CompositeType componentType;
 
 	private final String componentProperty;
 	private final String[] columns;
 	private final String columnsFragment;
 
 	public ComponentJoin(
 			FromClause fromClause,
 			FromElement origin,
 			String alias,
 			String componentPath,
-			ComponentType componentType) {
+			CompositeType componentType) {
 		super( fromClause, origin, alias );
 		this.componentPath = componentPath;
 		this.componentType = componentType;
 		this.componentProperty = StringHelper.unqualify( componentPath );
 		fromClause.addJoinByPathMap( componentPath, this );
 		initializeComponentJoin( new ComponentFromElementType( this ) );
 
 		this.columns = origin.getPropertyMapping( "" ).toColumns( getTableAlias(), componentProperty );
 		StringBuilder buf = new StringBuilder();
 		for ( int j = 0; j < columns.length; j++ ) {
 			final String column = columns[j];
 			if ( j > 0 ) {
 				buf.append( ", " );
 			}
 			buf.append( column );
 		}
 		this.columnsFragment = buf.toString();
 	}
 
 	public String getComponentPath() {
 		return componentPath;
 	}
 
 	public String getComponentProperty() {
 		return componentProperty;
 	}
 
-	public ComponentType getComponentType() {
+	public CompositeType getComponentType() {
 		return componentType;
 	}
 
 	@Override
 	public Type getDataType() {
 		return getComponentType();
 	}
 
 	@Override
 	public String getIdentityColumn() {
 		// used to "resolve" the IdentNode when our alias is encountered *by itself* in the query; so
 		//		here we use the component
 		// NOTE : ^^ is true *except for* when encountered by itself in the SELECT clause.  That gets
 		// 		routed through org.hibernate.hql.internal.ast.tree.ComponentJoin.ComponentFromElementType.renderScalarIdentifierSelect()
 		//		which we also override to account for
 		return columnsFragment;
 	}
 
 	@Override
 	public String[] getIdentityColumns() {
 		return columns;
 	}
 
 	@Override
 	public String getDisplayText() {
 		return "ComponentJoin{path=" + getComponentPath() + ", type=" + componentType.getReturnedClass() + "}";
 	}
 
 	public class ComponentFromElementType extends FromElementType {
 		private final PropertyMapping propertyMapping = new ComponentPropertyMapping();
 
 		public ComponentFromElementType(FromElement fromElement) {
 			super( fromElement );
 		}
 
 		@Override
 		public Type getDataType() {
 			return getComponentType();
 		}
 
 		@Override
 		public QueryableCollection getQueryableCollection() {
 			return null;
 		}
 
 		@Override
 		public PropertyMapping getPropertyMapping(String propertyName) {
 			return propertyMapping;
 		}
 
 		@Override
 		public Type getPropertyType(String propertyName, String propertyPath) {
 			int index = getComponentType().getPropertyIndex( propertyName );
 			return getComponentType().getSubtypes()[index];
 		}
 
 		@Override
 		public String renderScalarIdentifierSelect(int i) {
 			String[] cols = getBasePropertyMapping().toColumns( getTableAlias(), getComponentProperty() );
 			StringBuilder buf = new StringBuilder();
 			// For property references generate <tablealias>.<columnname> as <projectionalias>
 			for ( int j = 0; j < cols.length; j++ ) {
 				final String column = cols[j];
 				if ( j > 0 ) {
 					buf.append( ", " );
 				}
 				buf.append( column ).append( " as " ).append( NameGenerator.scalarName( i, j ) );
 			}
 			return buf.toString();
 		}
 	}
 
 	protected PropertyMapping getBasePropertyMapping() {
 		return getOrigin().getPropertyMapping( "" );
 	}
 
 	private final class ComponentPropertyMapping implements PropertyMapping {
 		@Override
 		public Type getType() {
 			return getComponentType();
 		}
 
 		@Override
 		public Type toType(String propertyName) throws QueryException {
 			return getBasePropertyMapping().toType( getPropertyPath( propertyName ) );
 		}
 
 		protected String getPropertyPath(String propertyName) {
 			return getComponentPath() + '.' + propertyName;
 		}
 
 		@Override
 		public String[] toColumns(String alias, String propertyName) throws QueryException {
 			return getBasePropertyMapping().toColumns( alias, getPropertyPath( propertyName ) );
 		}
 
 		@Override
 		public String[] toColumns(String propertyName) throws QueryException, UnsupportedOperationException {
 			return getBasePropertyMapping().toColumns( getPropertyPath( propertyName ) );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementFactory.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementFactory.java
index 139ea1c9eb..7c8d886b44 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/FromElementFactory.java
@@ -1,551 +1,551 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.ast.tree;
 
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.internal.ast.util.AliasGenerator;
 import org.hibernate.hql.internal.ast.util.PathHelper;
 import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
-import org.hibernate.type.ComponentType;
+import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 import antlr.ASTFactory;
 import antlr.SemanticException;
 import antlr.collections.AST;
 
 /**
  * Encapsulates the creation of FromElements and JoinSequences.
  *
  * @author josh
  */
 public class FromElementFactory implements SqlTokenTypes {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( FromElementFactory.class );
 
 	private FromClause fromClause;
 	private FromElement origin;
 	private String path;
 
 	private String classAlias;
 	private String[] columns;
 	private boolean implied;
 	private boolean inElementsFunction;
 	private boolean collection;
 	private QueryableCollection queryableCollection;
 	private CollectionType collectionType;
 
 	/**
 	 * Creates entity from elements.
 	 */
 	public FromElementFactory(FromClause fromClause, FromElement origin, String path) {
 		this.fromClause = fromClause;
 		this.origin = origin;
 		this.path = path;
 		collection = false;
 	}
 
 	/**
 	 * Creates collection from elements.
 	 */
 	public FromElementFactory(
 			FromClause fromClause,
 			FromElement origin,
 			String path,
 			String classAlias,
 			String[] columns,
 			boolean implied) {
 		this( fromClause, origin, path );
 		this.classAlias = classAlias;
 		this.columns = columns;
 		this.implied = implied;
 		collection = true;
 	}
 
 	FromElement addFromElement() throws SemanticException {
 		final FromClause parentFromClause = fromClause.getParentFromClause();
 		if ( parentFromClause != null ) {
 			// Look up class name using the first identifier in the path.
 			final String pathAlias = PathHelper.getAlias( path );
 			final FromElement parentFromElement = parentFromClause.getFromElement( pathAlias );
 			if ( parentFromElement != null ) {
 				return createFromElementInSubselect( path, pathAlias, parentFromElement, classAlias );
 			}
 		}
 
 		final EntityPersister entityPersister = fromClause.getSessionFactoryHelper().requireClassPersister( path );
 
 		final FromElement elem = createAndAddFromElement(
 				path,
 				classAlias,
 				entityPersister,
 				(EntityType) ( (Queryable) entityPersister ).getType(),
 				null
 		);
 
 		// Add to the query spaces.
 		fromClause.getWalker().addQuerySpaces( entityPersister.getQuerySpaces() );
 
 		return elem;
 	}
 
 	private FromElement createFromElementInSubselect(
 			String path,
 			String pathAlias,
 			FromElement parentFromElement,
 			String classAlias) throws SemanticException {
 		LOG.debugf( "createFromElementInSubselect() : path = %s", path );
 
 		// Create an DotNode AST for the path and resolve it.
 		FromElement fromElement = evaluateFromElementPath( path, classAlias );
 		EntityPersister entityPersister = fromElement.getEntityPersister();
 
 		// If the first identifier in the path refers to the class alias (not the class name), then this
 		// is a correlated subselect.  If it's a correlated sub-select, use the existing table alias.  Otherwise
 		// generate a new one.
 		String tableAlias = null;
 		boolean correlatedSubselect = pathAlias.equals( parentFromElement.getClassAlias() );
 		if ( correlatedSubselect ) {
 			tableAlias = fromElement.getTableAlias();
 		}
 		else {
 			tableAlias = null;
 		}
 
 		// If the from element isn't in the same clause, create a new from element.
 		if ( fromElement.getFromClause() != fromClause ) {
 			LOG.debug( "createFromElementInSubselect() : creating a new FROM element..." );
 			fromElement = createFromElement( entityPersister );
 			initializeAndAddFromElement(
 					fromElement,
 					path,
 					classAlias,
 					entityPersister,
 					(EntityType) ( (Queryable) entityPersister ).getType(),
 					tableAlias
 			);
 		}
 		LOG.debugf( "createFromElementInSubselect() : %s -> %s", path, fromElement );
 		return fromElement;
 	}
 
 	private FromElement evaluateFromElementPath(String path, String classAlias) throws SemanticException {
 		ASTFactory factory = fromClause.getASTFactory();
 		FromReferenceNode pathNode = (FromReferenceNode) PathHelper.parsePath( path, factory );
 		pathNode.recursiveResolve(
 				// This is the root level node.
 				FromReferenceNode.ROOT_LEVEL,
 				// Generate an explicit from clause at the root.
 				false,
 				classAlias,
 				null
 		);
 		if ( pathNode.getImpliedJoin() != null ) {
 			return pathNode.getImpliedJoin();
 		}
 		return pathNode.getFromElement();
 	}
 
 	FromElement createCollectionElementsJoin(
 			QueryableCollection queryableCollection,
 			String collectionName) throws SemanticException {
 		JoinSequence collectionJoinSequence = fromClause.getSessionFactoryHelper()
 				.createCollectionJoinSequence( queryableCollection, collectionName );
 		this.queryableCollection = queryableCollection;
 		return createCollectionJoin( collectionJoinSequence, null );
 	}
 
 	public FromElement createCollection(
 			QueryableCollection queryableCollection,
 			String role,
 			JoinType joinType,
 			boolean fetchFlag,
 			boolean indexed)
 			throws SemanticException {
 		if ( !collection ) {
 			throw new IllegalStateException( "FromElementFactory not initialized for collections!" );
 		}
 		this.inElementsFunction = indexed;
 		FromElement elem;
 		this.queryableCollection = queryableCollection;
 		collectionType = queryableCollection.getCollectionType();
 		String roleAlias = fromClause.getAliasGenerator().createName( role );
 
 		// Correlated subqueries create 'special' implied from nodes
 		// because correlated subselects can't use an ANSI-style join
 		boolean explicitSubqueryFromElement = fromClause.isSubQuery() && !implied;
 		if ( explicitSubqueryFromElement ) {
 			String pathRoot = StringHelper.root( path );
 			FromElement origin = fromClause.getFromElement( pathRoot );
 			if ( origin == null || origin.getFromClause() != fromClause ) {
 				implied = true;
 			}
 		}
 
 		// super-duper-classic-parser-regression-testing-mojo-magic...
 		if ( explicitSubqueryFromElement && DotNode.useThetaStyleImplicitJoins ) {
 			implied = true;
 		}
 
 		Type elementType = queryableCollection.getElementType();
 		if ( elementType.isEntityType() ) {
 			// A collection of entities...
 			elem = createEntityAssociation( role, roleAlias, joinType );
 		}
 		else if ( elementType.isComponentType() ) {
 			// A collection of components...
 			JoinSequence joinSequence = createJoinSequence( roleAlias, joinType );
 			elem = createCollectionJoin( joinSequence, roleAlias );
 		}
 		else {
 			// A collection of scalar elements...
 			JoinSequence joinSequence = createJoinSequence( roleAlias, joinType );
 			elem = createCollectionJoin( joinSequence, roleAlias );
 		}
 
 		elem.setRole( role );
 		elem.setQueryableCollection( queryableCollection );
 		// Don't include sub-classes for implied collection joins or subquery joins.
 		if ( implied ) {
 			elem.setIncludeSubclasses( false );
 		}
 
 		if ( explicitSubqueryFromElement ) {
 			// Treat explict from elements in sub-queries properly.
 			elem.setInProjectionList( true );
 		}
 
 		if ( fetchFlag ) {
 			elem.setFetch( true );
 		}
 		return elem;
 	}
 
 	public FromElement createEntityJoin(
 			String entityClass,
 			String tableAlias,
 			JoinSequence joinSequence,
 			boolean fetchFlag,
 			boolean inFrom,
 			EntityType type,
 			String role,
 			String joinPath) throws SemanticException {
 		FromElement elem = createJoin( entityClass, tableAlias, joinSequence, type, false );
 		elem.setFetch( fetchFlag );
 
 		if ( joinPath != null ) {
 			elem.applyTreatAsDeclarations( fromClause.getWalker().getTreatAsDeclarationsByPath( joinPath ) );
 		}
 
 		EntityPersister entityPersister = elem.getEntityPersister();
 		int numberOfTables = entityPersister.getQuerySpaces().length;
 		if ( numberOfTables > 1 && implied && !elem.useFromFragment() ) {
 			LOG.debug( "createEntityJoin() : Implied multi-table entity join" );
 			elem.setUseFromFragment( true );
 		}
 
 		// If this is an implied join in a FROM clause, then use ANSI-style joining, and set the
 		// flag on the FromElement that indicates that it was implied in the FROM clause itself.
 		if ( implied && inFrom ) {
 			joinSequence.setUseThetaStyle( false );
 			elem.setUseFromFragment( true );
 			elem.setImpliedInFromClause( true );
 		}
 		if ( elem.getWalker().isSubQuery() ) {
 			// two conditions where we need to transform this to a theta-join syntax:
 			//      1) 'elem' is the "root from-element" in correlated subqueries
 			//      2) The DotNode.useThetaStyleImplicitJoins has been set to true
 			//          and 'elem' represents an implicit join
 			if ( elem.getFromClause() != elem.getOrigin().getFromClause() ||
 //			        ( implied && DotNode.useThetaStyleImplicitJoins ) ) {
 					DotNode.useThetaStyleImplicitJoins ) {
 				// the "root from-element" in correlated subqueries do need this piece
 				elem.setType( FROM_FRAGMENT );
 				joinSequence.setUseThetaStyle( true );
 				elem.setUseFromFragment( false );
 			}
 		}
 
 		elem.setRole( role );
 
 		return elem;
 	}
 
-	public FromElement createComponentJoin(ComponentType type) {
+	public FromElement createComponentJoin(CompositeType type) {
 
 		// need to create a "place holder" from-element that can store the component/alias for this
 		// 		component join
 		return new ComponentJoin( fromClause, origin, classAlias, path, type );
 	}
 
 	FromElement createElementJoin(QueryableCollection queryableCollection) throws SemanticException {
 		FromElement elem;
 
 		implied = true; //TODO: always true for now, but not if we later decide to support elements() in the from clause
 		inElementsFunction = true;
 		Type elementType = queryableCollection.getElementType();
 		if ( !elementType.isEntityType() ) {
 			throw new IllegalArgumentException( "Cannot create element join for a collection of non-entities!" );
 		}
 		this.queryableCollection = queryableCollection;
 		SessionFactoryHelper sfh = fromClause.getSessionFactoryHelper();
 		FromElement destination = null;
 		String tableAlias = null;
 		EntityPersister entityPersister = queryableCollection.getElementPersister();
 		tableAlias = fromClause.getAliasGenerator().createName( entityPersister.getEntityName() );
 		String associatedEntityName = entityPersister.getEntityName();
 		EntityPersister targetEntityPersister = sfh.requireClassPersister( associatedEntityName );
 		// Create the FROM element for the target (the elements of the collection).
 		destination = createAndAddFromElement(
 				associatedEntityName,
 				classAlias,
 				targetEntityPersister,
 				(EntityType) queryableCollection.getElementType(),
 				tableAlias
 		);
 		// If the join is implied, then don't include sub-classes on the element.
 		if ( implied ) {
 			destination.setIncludeSubclasses( false );
 		}
 		fromClause.addCollectionJoinFromElementByPath( path, destination );
 //		origin.addDestination(destination);
 		// Add the query spaces.
 		fromClause.getWalker().addQuerySpaces( entityPersister.getQuerySpaces() );
 
 		CollectionType type = queryableCollection.getCollectionType();
 		String role = type.getRole();
 		String roleAlias = origin.getTableAlias();
 
 		String[] targetColumns = sfh.getCollectionElementColumns( role, roleAlias );
 		AssociationType elementAssociationType = sfh.getElementAssociationType( type );
 
 		// Create the join element under the from element.
 		JoinType joinType = JoinType.INNER_JOIN;
 		JoinSequence joinSequence = sfh.createJoinSequence(
 				implied,
 				elementAssociationType,
 				tableAlias,
 				joinType,
 				targetColumns
 		);
 		elem = initializeJoin( path, destination, joinSequence, targetColumns, origin, false );
 		elem.setUseFromFragment( true );    // The associated entity is implied, but it must be included in the FROM.
 		elem.setCollectionTableAlias( roleAlias );    // The collection alias is the role.
 		return elem;
 	}
 
 	private FromElement createCollectionJoin(JoinSequence collectionJoinSequence, String tableAlias)
 			throws SemanticException {
 		String text = queryableCollection.getTableName();
 		AST ast = createFromElement( text );
 		FromElement destination = (FromElement) ast;
 		Type elementType = queryableCollection.getElementType();
 		if ( elementType.isCollectionType() ) {
 			throw new SemanticException( "Collections of collections are not supported!" );
 		}
 		destination.initializeCollection( fromClause, classAlias, tableAlias );
 		destination.setType( JOIN_FRAGMENT );        // Tag this node as a JOIN.
 		destination.setIncludeSubclasses( false );    // Don't include subclasses in the join.
 		destination.setCollectionJoin( true );        // This is a clollection join.
 		destination.setJoinSequence( collectionJoinSequence );
 		destination.setOrigin( origin, false );
 		destination.setCollectionTableAlias( tableAlias );
 //		origin.addDestination( destination );
 // This was the cause of HHH-242
 //		origin.setType( FROM_FRAGMENT );			// Set the parent node type so that the AST is properly formed.
 		origin.setText( "" );                        // The destination node will have all the FROM text.
 		origin.setCollectionJoin( true );            // The parent node is a collection join too (voodoo - see JoinProcessor)
 		fromClause.addCollectionJoinFromElementByPath( path, destination );
 		fromClause.getWalker().addQuerySpaces( queryableCollection.getCollectionSpaces() );
 		return destination;
 	}
 
 	private FromElement createEntityAssociation(
 			String role,
 			String roleAlias,
 			JoinType joinType) throws SemanticException {
 		FromElement elem;
 		Queryable entityPersister = (Queryable) queryableCollection.getElementPersister();
 		String associatedEntityName = entityPersister.getEntityName();
 		// Get the class name of the associated entity.
 		if ( queryableCollection.isOneToMany() ) {
 			LOG.debugf(
 					"createEntityAssociation() : One to many - path = %s role = %s associatedEntityName = %s",
 					path,
 					role,
 					associatedEntityName
 			);
 			JoinSequence joinSequence = createJoinSequence( roleAlias, joinType );
 
 			elem = createJoin(
 					associatedEntityName,
 					roleAlias,
 					joinSequence,
 					(EntityType) queryableCollection.getElementType(),
 					false
 			);
 		}
 		else {
 			LOG.debugf(
 					"createManyToMany() : path = %s role = %s associatedEntityName = %s",
 					path,
 					role,
 					associatedEntityName
 			);
 			elem = createManyToMany(
 					role, associatedEntityName,
 					roleAlias, entityPersister, (EntityType) queryableCollection.getElementType(), joinType
 			);
 			fromClause.getWalker().addQuerySpaces( queryableCollection.getCollectionSpaces() );
 		}
 		elem.setCollectionTableAlias( roleAlias );
 		return elem;
 	}
 
 	private FromElement createJoin(
 			String entityClass,
 			String tableAlias,
 			JoinSequence joinSequence,
 			EntityType type,
 			boolean manyToMany) throws SemanticException {
 		//  origin, path, implied, columns, classAlias,
 		EntityPersister entityPersister = fromClause.getSessionFactoryHelper().requireClassPersister( entityClass );
 		FromElement destination = createAndAddFromElement(
 				entityClass,
 				classAlias,
 				entityPersister,
 				type,
 				tableAlias
 		);
 		return initializeJoin( path, destination, joinSequence, getColumns(), origin, manyToMany );
 	}
 
 	private FromElement createManyToMany(
 			String role,
 			String associatedEntityName,
 			String roleAlias,
 			Queryable entityPersister,
 			EntityType type,
 			JoinType joinType) throws SemanticException {
 		FromElement elem;
 		SessionFactoryHelper sfh = fromClause.getSessionFactoryHelper();
 		if ( inElementsFunction /*implied*/ ) {
 			// For implied many-to-many, just add the end join.
 			JoinSequence joinSequence = createJoinSequence( roleAlias, joinType );
 			elem = createJoin( associatedEntityName, roleAlias, joinSequence, type, true );
 		}
 		else {
 			// For an explicit many-to-many relationship, add a second join from the intermediate
 			// (many-to-many) table to the destination table.  Also, make sure that the from element's
 			// idea of the destination is the destination table.
 			String tableAlias = fromClause.getAliasGenerator().createName( entityPersister.getEntityName() );
 			String[] secondJoinColumns = sfh.getCollectionElementColumns( role, roleAlias );
 			// Add the second join, the one that ends in the destination table.
 			JoinSequence joinSequence = createJoinSequence( roleAlias, joinType );
 			joinSequence.addJoin(
 					sfh.getElementAssociationType( collectionType ),
 					tableAlias,
 					joinType,
 					secondJoinColumns
 			);
 			elem = createJoin( associatedEntityName, tableAlias, joinSequence, type, false );
 			elem.setUseFromFragment( true );
 		}
 		return elem;
 	}
 
 	private JoinSequence createJoinSequence(String roleAlias, JoinType joinType) {
 		SessionFactoryHelper sessionFactoryHelper = fromClause.getSessionFactoryHelper();
 		String[] joinColumns = getColumns();
 		if ( collectionType == null ) {
 			throw new IllegalStateException( "collectionType is null!" );
 		}
 		return sessionFactoryHelper.createJoinSequence( implied, collectionType, roleAlias, joinType, joinColumns );
 	}
 
 	private FromElement createAndAddFromElement(
 			String className,
 			String classAlias,
 			EntityPersister entityPersister,
 			EntityType type,
 			String tableAlias) {
 		if ( !( entityPersister instanceof Joinable ) ) {
 			throw new IllegalArgumentException( "EntityPersister " + entityPersister + " does not implement Joinable!" );
 		}
 		FromElement element = createFromElement( entityPersister );
 		initializeAndAddFromElement( element, className, classAlias, entityPersister, type, tableAlias );
 		return element;
 	}
 
 	private void initializeAndAddFromElement(
 			FromElement element,
 			String className,
 			String classAlias,
 			EntityPersister entityPersister,
 			EntityType type,
 			String tableAlias) {
 		if ( tableAlias == null ) {
 			AliasGenerator aliasGenerator = fromClause.getAliasGenerator();
 			tableAlias = aliasGenerator.createName( entityPersister.getEntityName() );
 		}
 		element.initializeEntity( fromClause, className, entityPersister, type, classAlias, tableAlias );
 	}
 
 	private FromElement createFromElement(EntityPersister entityPersister) {
 		Joinable joinable = (Joinable) entityPersister;
 		String text = joinable.getTableName();
 		AST ast = createFromElement( text );
 		FromElement element = (FromElement) ast;
 		return element;
 	}
 
 	private AST createFromElement(String text) {
 		AST ast = ASTUtil.create(
 				fromClause.getASTFactory(),
 				implied ? IMPLIED_FROM : FROM_FRAGMENT, // This causes the factory to instantiate the desired class.
 				text
 		);
 		// Reset the node type, because the rest of the system is expecting FROM_FRAGMENT, all we wanted was
 		// for the factory to create the right sub-class.  This might get reset again later on anyway to make the
 		// SQL generation simpler.
 		ast.setType( FROM_FRAGMENT );
 		return ast;
 	}
 
 	private FromElement initializeJoin(
 			String path,
 			FromElement destination,
 			JoinSequence joinSequence,
 			String[] columns,
 			FromElement origin,
 			boolean manyToMany) {
 		destination.setType( JOIN_FRAGMENT );
 		destination.setJoinSequence( joinSequence );
 		destination.setColumns( columns );
 		destination.setOrigin( origin, manyToMany );
 		fromClause.addJoinByPathMap( path, destination );
 		return destination;
 	}
 
 	private String[] getColumns() {
 		if ( columns == null ) {
 			throw new IllegalStateException( "No foriegn key columns were supplied!" );
 		}
 		return columns;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IntoClause.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IntoClause.java
index abc4c5fb79..65a82d1724 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IntoClause.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IntoClause.java
@@ -1,265 +1,265 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.ast.tree;
 
 import java.sql.Types;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.QueryException;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.Queryable;
-import org.hibernate.type.ComponentType;
+import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 import antlr.collections.AST;
 
 /**
  * Represents an entity referenced in the INTO clause of an HQL
  * INSERT statement.
  *
  * @author Steve Ebersole
  */
 public class IntoClause extends HqlSqlWalkerNode implements DisplayableNode {
 
 	private Queryable persister;
 	private String columnSpec = "";
 	private Type[] types;
 
 	private boolean discriminated;
 	private boolean explicitIdInsertion;
 	private boolean explicitVersionInsertion;
 
 	private Set componentIds;
 	private List explicitComponentIds;
 
 	public void initialize(Queryable persister) {
 		if ( persister.isAbstract() ) {
 			throw new QueryException( "cannot insert into abstract class (no table)" );
 		}
 		this.persister = persister;
 		initializeColumns();
 
 		if ( getWalker().getSessionFactoryHelper().hasPhysicalDiscriminatorColumn( persister ) ) {
 			discriminated = true;
 			columnSpec += ", " + persister.getDiscriminatorColumnName();
 		}
 
 		resetText();
 	}
 
 	private void resetText() {
 		setText( "into " + getTableName() + " ( " + columnSpec + " )" );
 	}
 
 	public String getTableName() {
 		return persister.getSubclassTableName( 0 );
 	}
 
 	public Queryable getQueryable() {
 		return persister;
 	}
 
 	public String getEntityName() {
 		return persister.getEntityName();
 	}
 
 	public Type[] getInsertionTypes() {
 		return types;
 	}
 
 	public boolean isDiscriminated() {
 		return discriminated;
 	}
 
 	public boolean isExplicitIdInsertion() {
 		return explicitIdInsertion;
 	}
 
 	public boolean isExplicitVersionInsertion() {
 		return explicitVersionInsertion;
 	}
 
 	public void prependIdColumnSpec() {
 		columnSpec = persister.getIdentifierColumnNames()[0] + ", " + columnSpec;
 		resetText();
 	}
 
 	public void prependVersionColumnSpec() {
 		columnSpec = persister.getPropertyColumnNames( persister.getVersionProperty() )[0] + ", " + columnSpec;
 		resetText();
 	}
 
 	public void validateTypes(SelectClause selectClause) throws QueryException {
 		Type[] selectTypes = selectClause.getQueryReturnTypes();
 		if ( selectTypes.length + selectClause.getTotalParameterCount() != types.length ) {
 			throw new QueryException( "number of select types did not match those for insert" );
 		}
 
 		int parameterCount = 0;
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( selectClause.getParameterPositions().contains( i ) ) {
 				parameterCount++;
 			}
 			else if ( !areCompatible( types[i], selectTypes[i - parameterCount] ) ) {
 				throw new QueryException(
 						"insertion type [" + types[i] + "] and selection type [" +
 								selectTypes[i - parameterCount] + "] at position " + i + " are not compatible"
 				);
 			}
 		}
 
 		// otherwise, everything ok.
 	}
 
 	/**
 	 * Returns additional display text for the AST node.
 	 *
 	 * @return String - The additional display text.
 	 */
 	public String getDisplayText() {
 		return "IntoClause{"
 				+ "entityName=" + getEntityName()
 				+ ",tableName=" + getTableName()
 				+ ",columns={" + columnSpec + "}"
 				+ "}";
 	}
 
 	private void initializeColumns() {
 		AST propertySpec = getFirstChild();
 		List types = new ArrayList();
 		visitPropertySpecNodes( propertySpec.getFirstChild(), types );
 		this.types = ArrayHelper.toTypeArray( types );
 		columnSpec = columnSpec.substring( 0, columnSpec.length() - 2 );
 	}
 
 	private void visitPropertySpecNodes(AST propertyNode, List types) {
 		if ( propertyNode == null ) {
 			return;
 		}
 		// TODO : we really need to be able to deal with component paths here also;
 		// this is difficult because the hql-sql grammar expects all those node types
 		// to be FromReferenceNodes.  One potential fix here would be to convert the
 		// IntoClause to just use a FromClause/FromElement combo (as a child of the
 		// InsertStatement) and move all this logic into the InsertStatement.  That's
 		// probably the easiest approach (read: least amount of changes to the grammar
 		// and code), but just doesn't feel right as then an insert would contain
 		// 2 from-clauses
 		String name = propertyNode.getText();
 		if ( isSuperclassProperty( name ) ) {
 			throw new QueryException( "INSERT statements cannot refer to superclass/joined properties [" + name + "]" );
 		}
 
 		if ( !explicitIdInsertion ) {
-			if ( persister.getIdentifierType() instanceof ComponentType ) {
+			if ( persister.getIdentifierType() instanceof CompositeType ) {
 				if ( componentIds == null ) {
-					String[] propertyNames = ( (ComponentType) persister.getIdentifierType() ).getPropertyNames();
+					String[] propertyNames = ( (CompositeType) persister.getIdentifierType() ).getPropertyNames();
 					componentIds = new HashSet();
 					for ( int i = 0; i < propertyNames.length; i++ ) {
 						componentIds.add( propertyNames[i] );
 					}
 				}
 				if ( componentIds.contains( name ) ) {
 					if ( explicitComponentIds == null ) {
 						explicitComponentIds = new ArrayList( componentIds.size() );
 					}
 					explicitComponentIds.add( name );
 					explicitIdInsertion = explicitComponentIds.size() == componentIds.size();
 				}
 			}
 			else if ( name.equals( persister.getIdentifierPropertyName() ) ) {
 				explicitIdInsertion = true;
 			}
 		}
 
 		if ( persister.isVersioned() ) {
 			if ( name.equals( persister.getPropertyNames()[persister.getVersionProperty()] ) ) {
 				explicitVersionInsertion = true;
 			}
 		}
 
 		String[] columnNames = persister.toColumns( name );
 		renderColumns( columnNames );
 		types.add( persister.toType( name ) );
 
 		// visit width-first, then depth
 		visitPropertySpecNodes( propertyNode.getNextSibling(), types );
 		visitPropertySpecNodes( propertyNode.getFirstChild(), types );
 	}
 
 	private void renderColumns(String[] columnNames) {
 		for ( int i = 0; i < columnNames.length; i++ ) {
 			columnSpec += columnNames[i] + ", ";
 		}
 	}
 
 	private boolean isSuperclassProperty(String propertyName) {
 		// really there are two situations where it should be ok to allow the insertion
 		// into properties defined on a superclass:
 		//      1) union-subclass with an abstract root entity
 		//      2) discrim-subclass
 		//
 		// #1 is handled already because of the fact that
 		// UnionSubclassPersister alreay always returns 0
 		// for this call...
 		//
 		// we may want to disallow it for discrim-subclass just for
 		// consistency-sake (currently does not work anyway)...
 		return persister.getSubclassPropertyTableNumber( propertyName ) != 0;
 	}
 
 	/**
 	 * Determine whether the two types are "assignment compatible".
 	 *
 	 * @param target The type defined in the into-clause.
 	 * @param source The type defined in the select clause.
 	 *
 	 * @return True if they are assignment compatible.
 	 */
 	private boolean areCompatible(Type target, Type source) {
 		if ( target.equals( source ) ) {
 			// if the types report logical equivalence, return true...
 			return true;
 		}
 
 		// otherwise, doAfterTransactionCompletion a "deep equivalence" check...
 
 		if ( !target.getReturnedClass().isAssignableFrom( source.getReturnedClass() ) ) {
 			return false;
 		}
 
 		int[] targetDatatypes = target.sqlTypes( getSessionFactoryHelper().getFactory() );
 		int[] sourceDatatypes = source.sqlTypes( getSessionFactoryHelper().getFactory() );
 
 		if ( targetDatatypes.length != sourceDatatypes.length ) {
 			return false;
 		}
 
 		for ( int i = 0; i < targetDatatypes.length; i++ ) {
 			if ( !areSqlTypesCompatible( targetDatatypes[i], sourceDatatypes[i] ) ) {
 				return false;
 			}
 		}
 
 		return true;
 	}
 
 	private boolean areSqlTypesCompatible(int target, int source) {
 		switch ( target ) {
 			case Types.TIMESTAMP:
 				return source == Types.DATE || source == Types.TIME || source == Types.TIMESTAMP;
 			case Types.DATE:
 				return source == Types.DATE || source == Types.TIMESTAMP;
 			case Types.TIME:
 				return source == Types.TIME || source == Types.TIMESTAMP;
 			default:
 				return target == source;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/criteria/ComponentCollectionCriteriaInfoProvider.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/ComponentCollectionCriteriaInfoProvider.java
index daa2d8dc01..4175e3b3a2 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/ComponentCollectionCriteriaInfoProvider.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/ComponentCollectionCriteriaInfoProvider.java
@@ -1,69 +1,69 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.loader.criteria;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.PropertyMapping;
-import org.hibernate.type.ComponentType;
+import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * @author David Mansfield
  */
 
 class ComponentCollectionCriteriaInfoProvider implements CriteriaInfoProvider {
 	private final QueryableCollection persister;
 	private final Map<String, Type> subTypes = new HashMap<String, Type>();
 
 	ComponentCollectionCriteriaInfoProvider(QueryableCollection persister) {
 		this.persister = persister;
 		if ( !persister.getElementType().isComponentType() ) {
 			throw new IllegalArgumentException( "persister for role " + persister.getRole() + " is not a collection-of-component" );
 		}
 
-		ComponentType componentType = (ComponentType) persister.getElementType();
+		CompositeType componentType = (CompositeType) persister.getElementType();
 		String[] names = componentType.getPropertyNames();
 		Type[] types = componentType.getSubtypes();
 
 		for ( int i = 0; i < names.length; i++ ) {
 			subTypes.put( names[i], types[i] );
 		}
 
 	}
 
 	@Override
 	public String getName() {
 		return persister.getRole();
 	}
 
 	@Override
 	public Serializable[] getSpaces() {
 		return persister.getCollectionSpaces();
 	}
 
 	@Override
 	public PropertyMapping getPropertyMapping() {
 		return persister;
 	}
 
 	@Override
 	public Type getType(String relativePath) {
 		// TODO: can a component have a nested component? then we may need to do something more here...
 		if ( relativePath.indexOf( '.' ) >= 0 ) {
 			throw new IllegalArgumentException( "dotted paths not handled (yet?!) for collection-of-component" );
 		}
 		Type type = subTypes.get( relativePath );
 		if ( type == null ) {
 			throw new IllegalArgumentException( "property " + relativePath + " not found in component of collection " + getName() );
 		}
 		return type;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/EntityLoadQueryDetails.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/EntityLoadQueryDetails.java
index 6bda38cfeb..d86c166df8 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/EntityLoadQueryDetails.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/EntityLoadQueryDetails.java
@@ -1,260 +1,259 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.loader.plan.exec.internal;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Collections;
 
 import org.hibernate.LockOptions;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.loader.plan.exec.process.internal.AbstractRowReader;
 import org.hibernate.loader.plan.exec.process.internal.EntityReferenceInitializerImpl;
 import org.hibernate.loader.plan.exec.process.internal.EntityReturnReader;
 import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
 import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorHelper;
 import org.hibernate.loader.plan.exec.process.spi.EntityReferenceInitializer;
 import org.hibernate.loader.plan.exec.process.spi.ReaderCollector;
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
 import org.hibernate.loader.plan.exec.process.spi.RowReader;
 import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
 import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
 import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.QuerySpace;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.entity.Queryable;
-import org.hibernate.type.ComponentType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 /**
  * Handles interpreting a LoadPlan (for loading of an entity) by:<ul>
  *     <li>generating the SQL query to perform</li>
  *     <li>creating the readers needed to read the results from the SQL's ResultSet</li>
  * </ul>
  *
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class EntityLoadQueryDetails extends AbstractLoadQueryDetails {
 	private static final Logger log = CoreLogging.logger( EntityLoadQueryDetails.class );
 
 	private final EntityReferenceAliases entityReferenceAliases;
 	private final ReaderCollector readerCollector;
 
 	/**
 	 * Constructs a EntityLoadQueryDetails object from the given inputs.
 	 *
 	 * @param loadPlan The load plan
 	 * @param keyColumnNames The columns to load the entity by (the PK columns or some other unique set of columns)
 	 * @param buildingParameters And influencers that would affect the generated SQL (mostly we are concerned with those
 	 * that add additional joins here)
 	 * @param factory The SessionFactory
 	 */
 	protected EntityLoadQueryDetails(
 			LoadPlan loadPlan,
 			String[] keyColumnNames,
 			AliasResolutionContextImpl aliasResolutionContext,
 			EntityReturn rootReturn,
 			QueryBuildingParameters buildingParameters,
 			SessionFactoryImplementor factory) {
 		super(
 				loadPlan,
 				aliasResolutionContext,
 				buildingParameters,
 				keyColumnNames,
 				rootReturn,
 				factory
 		);
 		this.entityReferenceAliases = aliasResolutionContext.generateEntityReferenceAliases(
 				rootReturn.getQuerySpaceUid(),
 				rootReturn.getEntityPersister()
 		);
 		this.readerCollector = new EntityLoaderReaderCollectorImpl(
 				new EntityReturnReader( rootReturn ),
 				new EntityReferenceInitializerImpl( rootReturn, entityReferenceAliases, true )
 		);
 		generate();
 	}
 
 	private EntityReturn getRootEntityReturn() {
 		return (EntityReturn) getRootReturn();
 	}
 
 	/**
 	 * Applies "table fragments" to the FROM-CLAUSE of the given SelectStatementBuilder for the given Loadable
 	 *
 	 * @param select The SELECT statement builder
 	 *
 	 * @see org.hibernate.persister.entity.OuterJoinLoadable#fromTableFragment(java.lang.String)
 	 * @see org.hibernate.persister.entity.Joinable#fromJoinFragment(java.lang.String, boolean, boolean)
 	 */
 	protected void applyRootReturnTableFragments(SelectStatementBuilder select) {
 		final String fromTableFragment;
 		final String rootAlias = entityReferenceAliases.getTableAlias();
 		final OuterJoinLoadable outerJoinLoadable = (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
 		if ( getQueryBuildingParameters().getLockOptions() != null ) {
 			fromTableFragment = getSessionFactory().getDialect().appendLockHint(
 					getQueryBuildingParameters().getLockOptions(),
 					outerJoinLoadable.fromTableFragment( rootAlias )
 			);
 			select.setLockOptions( getQueryBuildingParameters().getLockOptions() );
 		}
 		else if ( getQueryBuildingParameters().getLockMode() != null ) {
 			fromTableFragment = getSessionFactory().getDialect().appendLockHint(
 					getQueryBuildingParameters().getLockMode(),
 					outerJoinLoadable.fromTableFragment( rootAlias )
 			);
 			select.setLockMode( getQueryBuildingParameters().getLockMode() );
 		}
 		else {
 			fromTableFragment = outerJoinLoadable.fromTableFragment( rootAlias );
 		}
 		select.appendFromClauseFragment( fromTableFragment + outerJoinLoadable.fromJoinFragment( rootAlias, true, true ) );
 	}
 
 	protected void applyRootReturnFilterRestrictions(SelectStatementBuilder selectStatementBuilder) {
 		final Queryable rootQueryable = (Queryable) getRootEntityReturn().getEntityPersister();
 		selectStatementBuilder.appendRestrictions(
 				rootQueryable.filterFragment(
 						entityReferenceAliases.getTableAlias(),
 						Collections.emptyMap()
 				)
 		);
 	}
 
 	protected void applyRootReturnWhereJoinRestrictions(SelectStatementBuilder selectStatementBuilder) {
 		final Joinable joinable = (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
 		selectStatementBuilder.appendRestrictions(
 				joinable.whereJoinFragment(
 						entityReferenceAliases.getTableAlias(),
 						true,
 						true
 				)
 		);
 	}
 
 	@Override
 	protected void applyRootReturnOrderByFragments(SelectStatementBuilder selectStatementBuilder) {
 	}
 
 	@Override
 	protected ReaderCollector getReaderCollector() {
 		return readerCollector;
 	}
 
 	@Override
 	protected QuerySpace getRootQuerySpace() {
 		return getQuerySpace( getRootEntityReturn().getQuerySpaceUid() );
 	}
 
 	@Override
 	protected String getRootTableAlias() {
 		return entityReferenceAliases.getTableAlias();
 	}
 
 	@Override
 	protected boolean shouldApplyRootReturnFilterBeforeKeyRestriction() {
 		return false;
 	}
 
 	protected void applyRootReturnSelectFragments(SelectStatementBuilder selectStatementBuilder) {
 		final OuterJoinLoadable outerJoinLoadable = (OuterJoinLoadable) getRootEntityReturn().getEntityPersister();
 		selectStatementBuilder.appendSelectClauseFragment(
 				outerJoinLoadable.selectFragment(
 						entityReferenceAliases.getTableAlias(),
 						entityReferenceAliases.getColumnAliases().getSuffix()
 
 				)
 		);
 	}
 
 	private static class EntityLoaderReaderCollectorImpl extends ReaderCollectorImpl {
 		private final EntityReturnReader entityReturnReader;
 
 		public EntityLoaderReaderCollectorImpl(
 				EntityReturnReader entityReturnReader,
 				EntityReferenceInitializer entityReferenceInitializer) {
 			this.entityReturnReader = entityReturnReader;
 			add( entityReferenceInitializer );
 		}
 
 		@Override
 		public RowReader buildRowReader() {
 			return new EntityLoaderRowReader( this );
 		}
 
 		@Override
 		public EntityReturnReader getReturnReader() {
 			return entityReturnReader;
 		}
 	}
 
 	private static class EntityLoaderRowReader extends AbstractRowReader {
 		private final EntityReturnReader rootReturnReader;
 
 		public EntityLoaderRowReader(EntityLoaderReaderCollectorImpl entityLoaderReaderCollector) {
 			super( entityLoaderReaderCollector );
 			this.rootReturnReader = entityLoaderReaderCollector.getReturnReader();
 		}
 
 		@Override
 		public Object readRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
 			final ResultSetProcessingContext.EntityReferenceProcessingState processingState =
 					rootReturnReader.getIdentifierResolutionContext( context );
 			// if the entity reference we are hydrating is a Return, it is possible that its EntityKey is
 			// supplied by the QueryParameter optional entity information
 			if ( context.shouldUseOptionalEntityInformation() && context.getQueryParameters().getOptionalId() != null ) {
 				EntityKey entityKey = ResultSetProcessorHelper.getOptionalObjectKey(
 						context.getQueryParameters(),
 						context.getSession()
 				);
 				processingState.registerIdentifierHydratedForm( entityKey.getIdentifier() );
 				processingState.registerEntityKey( entityKey );
 				final EntityPersister entityPersister = processingState.getEntityReference().getEntityPersister();
 				if ( entityPersister.getIdentifierType().isComponentType()  ) {
 					final CompositeType identifierType = (CompositeType) entityPersister.getIdentifierType();
 					if ( !identifierType.isEmbedded() ) {
 						addKeyManyToOnesToSession(
 								context,
 								identifierType,
 								entityKey.getIdentifier()
 						);
 					}
 				}
 			}
 			return super.readRow( resultSet, context );
 		}
 
 		private void addKeyManyToOnesToSession(ResultSetProcessingContextImpl context, CompositeType componentType, Object component ) {
 			for ( int i = 0 ; i < componentType.getSubtypes().length ; i++ ) {
 				final Type subType = componentType.getSubtypes()[ i ];
 				final Object subValue = componentType.getPropertyValue( component, i, context.getSession() );
 				if ( subType.isEntityType() ) {
 					( (Session) context.getSession() ).buildLockRequest( LockOptions.NONE ).lock( subValue );
 				}
 				else if ( subType.isComponentType() ) {
-					addKeyManyToOnesToSession( context, (ComponentType) subType, subValue  );
+					addKeyManyToOnesToSession( context, (CompositeType) subType, subValue  );
 				}
 			}
 		}
 
 		@Override
 		protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
 			return rootReturnReader.read( resultSet, context );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/ProxyFactory.java b/hibernate-core/src/main/java/org/hibernate/proxy/ProxyFactory.java
index 608e3c5ed3..4cc39a5d92 100644
--- a/hibernate-core/src/main/java/org/hibernate/proxy/ProxyFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/ProxyFactory.java
@@ -1,67 +1,67 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.proxy;
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.CompositeType;
 
 /**
  * Contract for run-time, proxy-based lazy initialization proxies.
  *
  * @author Gavin King
  */
 public interface ProxyFactory {
 
 	/**
 	 * Called immediately after instantiation of this factory.
 	 * <p/>
 	 * Essentially equivalent to constructor injection, but contracted
 	 * here via interface.
 	 *
 	 * @param entityName The name of the entity for which this factory should
 	 * generate proxies.
 	 * @param persistentClass The entity class for which to generate proxies;
 	 * not always the same as the entityName.
 	 * @param interfaces The interfaces to expose in the generated proxy;
 	 * {@link HibernateProxy} is already included in this collection.
 	 * @param getIdentifierMethod Reference to the identifier getter method;
 	 * invocation on this method should not force initialization
 	 * @param setIdentifierMethod Reference to the identifier setter method;
 	 * invocation on this method should not force initialization
 	 * @param componentIdType For composite identifier types, a reference to
-	 * the {@link org.hibernate.type.ComponentType type} of the identifier
+	 * the {@link org.hibernate.type.CompositeType type} of the identifier
 	 * property; again accessing the id should generally not cause
 	 * initialization - but need to bear in mind <key-many-to-one/>
 	 * mappings.
 	 * @throws HibernateException Indicates a problem completing post
 	 * instantiation initialization.
 	 */
 	public void postInstantiate(
 			String entityName,
 			Class persistentClass,
 			Set<Class> interfaces,
 			Method getIdentifierMethod,
 			Method setIdentifierMethod,
 			CompositeType componentIdType) throws HibernateException;
 
 	/**
 	 * Create a new proxy instance
 	 *
 	 * @param id The id value for the proxy to be generated.
 	 * @param session The session to which the generated proxy will be
 	 * associated.
 	 * @return The generated proxy.
 	 * @throws HibernateException Indicates problems generating the requested
 	 * proxy.
 	 */
 	public HibernateProxy getProxy(Serializable id,SessionImplementor session) throws HibernateException;
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
index 67b951c50f..dcf22d13e2 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
@@ -1,502 +1,516 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
+import org.hibernate.PropertyNotFoundException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.HibernateProxyHelper;
 import org.hibernate.proxy.LazyInitializer;
 
-import org.dom4j.Node;
-
 /**
  * Handles "any" mappings
  * 
  * @author Gavin King
  */
 public class AnyType extends AbstractType implements CompositeType, AssociationType {
 	private final TypeFactory.TypeScope scope;
 	private final Type identifierType;
 	private final Type discriminatorType;
 
 	/**
 	 * Intended for use only from legacy {@link ObjectType} type definition
-	 *
-	 * @param discriminatorType
-	 * @param identifierType
 	 */
 	protected AnyType(Type discriminatorType, Type identifierType) {
 		this( null, discriminatorType, identifierType );
 	}
 
 	public AnyType(TypeFactory.TypeScope scope, Type discriminatorType, Type identifierType) {
 		this.scope = scope;
 		this.discriminatorType = discriminatorType;
 		this.identifierType = identifierType;
 	}
 
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 	public Type getDiscriminatorType() {
 		return discriminatorType;
 	}
 
 
 	// general Type metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public String getName() {
 		return "object";
 	}
 
 	@Override
 	public Class getReturnedClass() {
 		return Object.class;
 	}
 
 	@Override
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.sqlTypes( mapping ), identifierType.sqlTypes( mapping ) );
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.dictatedSizes( mapping ), identifierType.dictatedSizes( mapping ) );
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.defaultSizes( mapping ), identifierType.defaultSizes( mapping ) );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, EntityMode entityMode) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean isAnyType() {
 		return true;
 	}
 
 	@Override
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	@Override
 	public boolean isComponentType() {
 		return true;
 	}
 
 	@Override
 	public boolean isEmbedded() {
 		return false;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	@Override
 	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return value;
 	}
 
 
 	// general Type functionality ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public int compare(Object x, Object y) {
 		if ( x == null ) {
 			// if y is also null, return that they are the same (no option for "UNKNOWN")
 			// if y is not null, return that y is "greater" (-1 because the result is from the perspective of
 			// 		the first arg: x)
 			return y == null ? 0 : -1;
 		}
 		else if ( y == null ) {
 			// x is not null, but y is.  return that x is "greater"
 			return 1;
 		}
 
 		// At this point we know both are non-null.
 		final Object xId = extractIdentifier( x );
 		final Object yId = extractIdentifier( y );
 
 		return getIdentifierType().compare( xId, yId );
 	}
 
 	private Object extractIdentifier(Object entity) {
 		final EntityPersister concretePersister = guessEntityPersister( entity );
 		return concretePersister == null
 				? null
 				: concretePersister.getEntityTuplizer().getIdentifier( entity, null );
 	}
 
 	private EntityPersister guessEntityPersister(Object object) {
 		if ( scope == null ) {
 			return null;
 		}
 
 		String entityName = null;
 
 		// this code is largely copied from Session's bestGuessEntityName
 		Object entity = object;
 		if ( entity instanceof HibernateProxy ) {
 			final LazyInitializer initializer = ( (HibernateProxy) entity ).getHibernateLazyInitializer();
 			if ( initializer.isUninitialized() ) {
 				entityName = initializer.getEntityName();
 			}
 			entity = initializer.getImplementation();
 		}
 
 		if ( entityName == null ) {
 			for ( EntityNameResolver resolver : scope.resolveFactory().iterateEntityNameResolvers() ) {
 				entityName = resolver.resolveEntityName( entity );
 				if ( entityName != null ) {
 					break;
 				}
 			}
 		}
 
 		if ( entityName == null ) {
 			// the old-time stand-by...
 			entityName = object.getClass().getName();
 		}
 
 		return scope.resolveFactory().getEntityPersister( entityName );
 	}
 
 	@Override
 	public boolean isSame(Object x, Object y) throws HibernateException {
 		return x == y;
 	}
 
 	@Override
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		if ( current == null ) {
 			return old != null;
 		}
 		else if ( old == null ) {
 			return true;
 		}
 
 		final ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) old;
 		final boolean[] idCheckable = new boolean[checkable.length-1];
 		System.arraycopy( checkable, 1, idCheckable, 0, idCheckable.length );
 		return ( checkable[0] && !holder.entityName.equals( session.bestGuessEntityName( current ) ) )
 				|| identifierType.isModified( holder.id, getIdentifier( current, session ), idCheckable, session );
 	}
 
 	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		final boolean[] result = new boolean[ getColumnSpan( mapping ) ];
 		if ( value != null ) {
 			Arrays.fill( result, true );
 		}
 		return result;
 	}
 
 	@Override
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return isDirty( old, current, session );
 	}
 
 	@Override
 	public int getColumnSpan(Mapping session) {
 		return 2;
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
 			throws HibernateException, SQLException {
 		return resolveAny(
 				(String) discriminatorType.nullSafeGet( rs, names[0], session, owner ),
 				(Serializable) identifierType.nullSafeGet( rs, names[1], session, owner ),
 				session
 		);
 	}
 
 	@Override
 	public Object hydrate(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
 			throws HibernateException, SQLException {
 		final String entityName = (String) discriminatorType.nullSafeGet( rs, names[0], session, owner );
 		final Serializable id = (Serializable) identifierType.nullSafeGet( rs, names[1], session, owner );
 		return new ObjectTypeCacheEntry( entityName, id );
 	}
 
 	@Override
 	public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		final ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) value;
 		return resolveAny( holder.entityName, holder.id, session );
 	}
 
 	private Object resolveAny(String entityName, Serializable id, SessionImplementor session)
 			throws HibernateException {
 		return entityName==null || id==null
 				? null
 				: session.internalLoad( entityName, id, false, false );
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, SessionImplementor session)
 			throws HibernateException, SQLException {
 		nullSafeSet( st, value, index, null, session );
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, boolean[] settable, SessionImplementor session)
 			throws HibernateException, SQLException {
 		Serializable id;
 		String entityName;
 		if ( value == null ) {
 			id = null;
 			entityName = null;
 		}
 		else {
 			entityName = session.bestGuessEntityName( value );
 			id = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, value, session );
 		}
 
 		// discriminatorType is assumed to be single-column type
 		if ( settable == null || settable[0] ) {
 			discriminatorType.nullSafeSet( st, entityName, index, session );
 		}
 		if ( settable == null ) {
 			identifierType.nullSafeSet( st, id, index+1, session );
 		}
 		else {
 			final boolean[] idSettable = new boolean[ settable.length-1 ];
 			System.arraycopy( settable, 1, idSettable, 0, idSettable.length );
 			identifierType.nullSafeSet( st, id, index+1, idSettable, session );
 		}
 	}
 
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		//TODO: terrible implementation!
 		return value == null
 				? "null"
 				: factory.getTypeHelper()
 				.entity( HibernateProxyHelper.getClassWithoutInitializingProxy( value ) )
 				.toLoggableString( value, factory );
 	}
 
 	@Override
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
 		final ObjectTypeCacheEntry e = (ObjectTypeCacheEntry) cached;
 		return e == null ? null : session.internalLoad( e.entityName, e.id, false, false );
 	}
 
 	@Override
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			return new ObjectTypeCacheEntry(
 					session.bestGuessEntityName( value ),
 					ForeignKeys.getEntityIdentifierIfNotUnsaved(
 							session.bestGuessEntityName( value ),
 							value,
 							session
 					)
 			);
 		}
 	}
 
 	@Override
 	public Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache)
 			throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		else {
 			final String entityName = session.bestGuessEntityName( original );
 			final Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, original, session );
 			return session.internalLoad( entityName, id, false, false );
 		}
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs,	String name, SessionImplementor session, Object owner) {
 		throw new UnsupportedOperationException( "object is a multicolumn type" );
 	}
 
 	@Override
 	public Object semiResolve(Object value, SessionImplementor session, Object owner) {
 		throw new UnsupportedOperationException( "any mappings may not form part of a property-ref" );
 	}
 
 	// CompositeType implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	private static final String[] PROPERTY_NAMES = new String[] { "class", "id" };
 
 	@Override
 	public String[] getPropertyNames() {
 		return PROPERTY_NAMES;
 	}
 
 	@Override
+	public int getPropertyIndex(String name) {
+		if ( PROPERTY_NAMES[0].equals( name ) ) {
+			return 0;
+		}
+		else if ( PROPERTY_NAMES[1].equals( name ) ) {
+			return 1;
+		}
+
+		throw new PropertyNotFoundException( "Unable to locate property named " + name + " on AnyType" );
+	}
+
+	@Override
 	public Object getPropertyValue(Object component, int i, SessionImplementor session) throws HibernateException {
 		return i==0
 				? session.bestGuessEntityName( component )
 				: getIdentifier( component, session );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, SessionImplementor session) throws HibernateException {
 		return new Object[] {
 				session.bestGuessEntityName( component ),
 				getIdentifier( component, session )
 		};
 	}
 
 	private Serializable getIdentifier(Object value, SessionImplementor session) throws HibernateException {
 		try {
 			return ForeignKeys.getEntityIdentifierIfNotUnsaved(
 					session.bestGuessEntityName( value ),
 					value,
 					session
 			);
 		}
 		catch (TransientObjectException toe) {
 			return null;
 		}
 	}
 
 	@Override
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) {
 		throw new UnsupportedOperationException();
 	}
 
 	private static final boolean[] NULLABILITY = new boolean[] { false, false };
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return NULLABILITY;
 	}
 
 	@Override
+	public boolean hasNotNullProperty() {
+		// both are non-nullable
+		return true;
+	}
+
+	@Override
 	public Type[] getSubtypes() {
 		return new Type[] {discriminatorType, identifierType };
 	}
 
 	@Override
 	public CascadeStyle getCascadeStyle(int i) {
 		return CascadeStyles.NONE;
 	}
 
 	@Override
 	public FetchMode getFetchMode(int i) {
 		return FetchMode.SELECT;
 	}
 
 
 	// AssociationType implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FROM_PARENT;
 	}
 
 	@Override
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 
 	@Override
 	public String getLHSPropertyName() {
 		return null;
 	}
 
 	public boolean isReferenceToPrimaryKey() {
 		return true;
 	}
 
 	@Override
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	@Override
 	public boolean isAlwaysDirtyChecked() {
 		return false;
 	}
 
 	@Override
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
 	@Override
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
 	@Override
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public String getOnCondition(
 			String alias,
 			SessionFactoryImplementor factory,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * Used to externalize discrimination per a given identifier.  For example, when writing to
 	 * second level cache we write the discrimination resolved concrete type for each entity written.
 	 */
 	public static final class ObjectTypeCacheEntry implements Serializable {
 		final String entityName;
 		final Serializable id;
 
 		ObjectTypeCacheEntry(String entityName, Serializable id) {
 			this.entityName = entityName;
 			this.id = id;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
index 7e05804258..714abb0cee 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
@@ -1,823 +1,822 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.tuple.component.ComponentTuplizer;
 
-import org.dom4j.Element;
-import org.dom4j.Node;
-
 /**
  * Handles "component" mappings
  *
  * @author Gavin King
  */
 public class ComponentType extends AbstractType implements CompositeType, ProcedureParameterExtractionAware {
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyNullability;
 	protected final int propertySpan;
 	private final CascadeStyle[] cascade;
 	private final FetchMode[] joinedFetch;
 	private final boolean isKey;
 	private boolean hasNotNullProperty;
 
 	protected final EntityMode entityMode;
 	protected final ComponentTuplizer componentTuplizer;
 
 	public ComponentType(TypeFactory.TypeScope typeScope, ComponentMetamodel metamodel) {
 		this.typeScope = typeScope;
 		// for now, just "re-flatten" the metamodel since this is temporary stuff anyway (HHH-1907)
 		this.isKey = metamodel.isKey();
 		this.propertySpan = metamodel.getPropertySpan();
 		this.propertyNames = new String[propertySpan];
 		this.propertyTypes = new Type[propertySpan];
 		this.propertyNullability = new boolean[propertySpan];
 		this.cascade = new CascadeStyle[propertySpan];
 		this.joinedFetch = new FetchMode[propertySpan];
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			StandardProperty prop = metamodel.getProperty( i );
 			this.propertyNames[i] = prop.getName();
 			this.propertyTypes[i] = prop.getType();
 			this.propertyNullability[i] = prop.isNullable();
 			this.cascade[i] = prop.getCascadeStyle();
 			this.joinedFetch[i] = prop.getFetchMode();
 			if ( !prop.isNullable() ) {
 				hasNotNullProperty = true;
 			}
 		}
 
 		this.entityMode = metamodel.getEntityMode();
 		this.componentTuplizer = metamodel.getComponentTuplizer();
 	}
 
 	public boolean isKey() {
 		return isKey;
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	public ComponentTuplizer getComponentTuplizer() {
 		return componentTuplizer;
 	}
 
 	@Override
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		int span = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			span += propertyTypes[i].getColumnSpan( mapping );
 		}
 		return span;
 	}
 
 	@Override
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		int[] sqlTypes = new int[getColumnSpan( mapping )];
 		int n = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int[] subtypes = propertyTypes[i].sqlTypes( mapping );
 			for ( int subtype : subtypes ) {
 				sqlTypes[n++] = subtype;
 			}
 		}
 		return sqlTypes;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[getColumnSpan( mapping )];
 		int soFar = 0;
 		for ( Type propertyType : propertyTypes ) {
 			final Size[] propertySizes = propertyType.dictatedSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[getColumnSpan( mapping )];
 		int soFar = 0;
 		for ( Type propertyType : propertyTypes ) {
 			final Size[] propertySizes = propertyType.defaultSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 
 	@Override
 	public final boolean isComponentType() {
 		return true;
 	}
 
 	public Class getReturnedClass() {
 		return componentTuplizer.getMappedClass();
 	}
 
 	@Override
 	public boolean isSame(Object x, Object y) throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isSame( xvalues[i], yvalues[i] ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public boolean isEqual(final Object x, final Object y) throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( getPropertyValue( x, i ), getPropertyValue( y, i ) ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public boolean isEqual(final Object x, final Object y, final SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( getPropertyValue( x, i ), getPropertyValue( y, i ), factory ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public int compare(final Object x, final Object y) {
 		if ( x == y ) {
 			return 0;
 		}
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int propertyCompare = propertyTypes[i].compare( getPropertyValue( x, i ), getPropertyValue( y, i ) );
 			if ( propertyCompare != 0 ) {
 				return propertyCompare;
 			}
 		}
 		return 0;
 	}
 
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	@Override
 	public int getHashCode(final Object x) {
 		int result = 17;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = getPropertyValue( x, i );
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y );
 			}
 		}
 		return result;
 	}
 
 	@Override
 	public int getHashCode(final Object x, final SessionFactoryImplementor factory) {
 		int result = 17;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = getPropertyValue( x, i );
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y, factory );
 			}
 		}
 		return result;
 	}
 
 	@Override
 	public boolean isDirty(final Object x, final Object y, final SessionImplementor session) throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( propertyTypes[i].isDirty( getPropertyValue( x, i ), getPropertyValue( y, i ), session ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public boolean isDirty(final Object x, final Object y, final boolean[] checkable, final SessionImplementor session)
 			throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
 		int loc = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			if ( len <= 1 ) {
 				final boolean dirty = ( len == 0 || checkable[loc] ) &&
 						propertyTypes[i].isDirty( getPropertyValue( x, i ), getPropertyValue( y, i ), session );
 				if ( dirty ) {
 					return true;
 				}
 			}
 			else {
 				boolean[] subcheckable = new boolean[len];
 				System.arraycopy( checkable, loc, subcheckable, 0, len );
 				final boolean dirty = propertyTypes[i].isDirty(
 						getPropertyValue( x, i ),
 						getPropertyValue( y, i ),
 						subcheckable,
 						session
 				);
 				if ( dirty ) {
 					return true;
 				}
 			}
 			loc += len;
 		}
 		return false;
 	}
 
 	@Override
 	public boolean isModified(
 			final Object old,
 			final Object current,
 			final boolean[] checkable,
 			final SessionImplementor session) throws HibernateException {
 		if ( current == null ) {
 			return old != null;
 		}
 		if ( old == null ) {
 			return true;
 		}
 		Object[] oldValues = (Object[]) old;
 		int loc = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			boolean[] subcheckable = new boolean[len];
 			System.arraycopy( checkable, loc, subcheckable, 0, len );
 			if ( propertyTypes[i].isModified( oldValues[i], getPropertyValue( current, i ), subcheckable, session ) ) {
 				return true;
 			}
 			loc += len;
 		}
 		return false;
 
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( hydrate( rs, names, session, owner ), session, owner );
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value, int begin, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, entityMode );
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			propertyTypes[i].nullSafeSet( st, subvalues[i], begin, session );
 			begin += propertyTypes[i].getColumnSpan( session.getFactory() );
 		}
 	}
 
 	@Override
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int begin,
 			boolean[] settable,
 			SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, entityMode );
 
 		int loc = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			//noinspection StatementWithEmptyBody
 			if ( len == 0 ) {
 				//noop
 			}
 			else if ( len == 1 ) {
 				if ( settable[loc] ) {
 					propertyTypes[i].nullSafeSet( st, subvalues[i], begin, session );
 					begin++;
 				}
 			}
 			else {
 				boolean[] subsettable = new boolean[len];
 				System.arraycopy( settable, loc, subsettable, 0, len );
 				propertyTypes[i].nullSafeSet( st, subvalues[i], begin, subsettable, session );
 				begin += ArrayHelper.countTrue( subsettable );
 			}
 			loc += len;
 		}
 	}
 
 	private Object[] nullSafeGetValues(Object value, EntityMode entityMode) throws HibernateException {
 		if ( value == null ) {
 			return new Object[propertySpan];
 		}
 		else {
 			return getPropertyValues( value, entityMode );
 		}
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	@Override
 	public Object getPropertyValue(Object component, int i, SessionImplementor session)
 			throws HibernateException {
 		return getPropertyValue( component, i );
 	}
 
 	public Object getPropertyValue(Object component, int i, EntityMode entityMode)
 			throws HibernateException {
 		return getPropertyValue( component, i );
 	}
 
 	public Object getPropertyValue(Object component, int i)
 			throws HibernateException {
 		if ( component instanceof Object[] ) {
 			// A few calls to hashCode pass the property values already in an
 			// Object[] (ex: QueryKey hash codes for cached queries).
 			// It's easiest to just check for the condition here prior to
 			// trying reflection.
 			return ( (Object[]) component )[i];
 		}
 		else {
 			return componentTuplizer.getPropertyValue( component, i );
 		}
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, SessionImplementor session)
 			throws HibernateException {
 		return getPropertyValues( component, entityMode );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, EntityMode entityMode)
 			throws HibernateException {
 		if ( component instanceof Object[] ) {
 			// A few calls to hashCode pass the property values already in an 
 			// Object[] (ex: QueryKey hash codes for cached queries).
 			// It's easiest to just check for the condition here prior to
 			// trying reflection.
 			return (Object[]) component;
 		}
 		else {
 			return componentTuplizer.getPropertyValues( component );
 		}
 	}
 
 	@Override
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
 			throws HibernateException {
 		componentTuplizer.setPropertyValues( component, values );
 	}
 
 	@Override
 	public Type[] getSubtypes() {
 		return propertyTypes;
 	}
 
 	@Override
 	public String getName() {
 		return "component" + ArrayHelper.toString( propertyNames );
 	}
 
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 
 		if ( entityMode == null ) {
 			throw new ClassCastException( value.getClass().getName() );
 		}
 		Map<String, String> result = new HashMap<String, String>();
 		Object[] values = getPropertyValues( value, entityMode );
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			result.put( propertyNames[i], propertyTypes[i].toLoggableString( values[i], factory ) );
 		}
 		return StringHelper.unqualify( getName() ) + result.toString();
 	}
 
 	@Override
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
 
 	@Override
 	public Object deepCopy(Object component, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( component == null ) {
 			return null;
 		}
 
 		Object[] values = getPropertyValues( component, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			values[i] = propertyTypes[i].deepCopy( values[i], factory );
 		}
 
 		Object result = instantiate( entityMode );
 		setPropertyValues( result, values, entityMode );
 
 		//not absolutely necessary, but helps for some
 		//equals()/hashCode() implementations
 		if ( componentTuplizer.hasParentProperty() ) {
 			componentTuplizer.setParent( result, componentTuplizer.getParent( component ), factory );
 		}
 
 		return result;
 	}
 
 	@Override
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache)
 			throws HibernateException {
 
 		if ( original == null ) {
 			return null;
 		}
 		//if ( original == target ) return target;
 
 		final Object result = target == null
 				? instantiate( owner, session )
 				: target;
 
 		Object[] values = TypeHelper.replace(
 				getPropertyValues( original, entityMode ),
 				getPropertyValues( result, entityMode ),
 				propertyTypes,
 				session,
 				owner,
 				copyCache
 		);
 
 		setPropertyValues( result, values, entityMode );
 		return result;
 	}
 
 	@Override
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache,
 			ForeignKeyDirection foreignKeyDirection)
 			throws HibernateException {
 
 		if ( original == null ) {
 			return null;
 		}
 		//if ( original == target ) return target;
 
 		final Object result = target == null ?
 				instantiate( owner, session ) :
 				target;
 
 		Object[] values = TypeHelper.replace(
 				getPropertyValues( original, entityMode ),
 				getPropertyValues( result, entityMode ),
 				propertyTypes,
 				session,
 				owner,
 				copyCache,
 				foreignKeyDirection
 		);
 
 		setPropertyValues( result, values, entityMode );
 		return result;
 	}
 
 	/**
 	 * This method does not populate the component parent
 	 */
 	public Object instantiate(EntityMode entityMode) throws HibernateException {
 		return componentTuplizer.instantiate();
 	}
 
 	public Object instantiate(Object parent, SessionImplementor session)
 			throws HibernateException {
 
 		Object result = instantiate( entityMode );
 
 		if ( componentTuplizer.hasParentProperty() && parent != null ) {
 			componentTuplizer.setParent(
 					result,
 					session.getPersistenceContext().proxyFor( parent ),
 					session.getFactory()
 			);
 		}
 
 		return result;
 	}
 
 	@Override
 	public CascadeStyle getCascadeStyle(int i) {
 		return cascade[i];
 	}
 
 	@Override
 	public boolean isMutable() {
 		return true;
 	}
 
 	@Override
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			Object[] values = getPropertyValues( value, entityMode );
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				values[i] = propertyTypes[i].disassemble( values[i], session, owner );
 			}
 			return values;
 		}
 	}
 
 	@Override
 	public Object assemble(Serializable object, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( object == null ) {
 			return null;
 		}
 		else {
 			Object[] values = (Object[]) object;
 			Object[] assembled = new Object[values.length];
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				assembled[i] = propertyTypes[i].assemble( (Serializable) values[i], session, owner );
 			}
 			Object result = instantiate( owner, session );
 			setPropertyValues( result, assembled, entityMode );
 			return result;
 		}
 	}
 
 	@Override
 	public FetchMode getFetchMode(int i) {
 		return joinedFetch[i];
 	}
 
 	@Override
 	public Object hydrate(
 			final ResultSet rs,
 			final String[] names,
 			final SessionImplementor session,
 			final Object owner)
 			throws HibernateException, SQLException {
 
 		int begin = 0;
 		boolean notNull = false;
 		Object[] values = new Object[propertySpan];
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int length = propertyTypes[i].getColumnSpan( session.getFactory() );
 			String[] range = ArrayHelper.slice( names, begin, length ); //cache this
 			Object val = propertyTypes[i].hydrate( rs, range, session, owner );
 			if ( val == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[i] = val;
 			begin += length;
 		}
 
 		return notNull ? values : null;
 	}
 
 	@Override
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( value != null ) {
 			Object result = instantiate( owner, session );
 			Object[] values = (Object[]) value;
 			Object[] resolvedValues = new Object[values.length]; //only really need new array during semiresolve!
 			for ( int i = 0; i < values.length; i++ ) {
 				resolvedValues[i] = propertyTypes[i].resolve( values[i], session, owner );
 			}
 			setPropertyValues( result, resolvedValues, entityMode );
 			return result;
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//note that this implementation is kinda broken
 		//for components with many-to-one associations
 		return resolve( value, session, owner );
 	}
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[getColumnSpan( mapping )];
 		if ( value == null ) {
 			return result;
 		}
 		Object[] values = getPropertyValues( value, EntityMode.POJO ); //TODO!!!!!!!
 		int loc = 0;
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			boolean[] propertyNullness = propertyTypes[i].toColumnNullness( values[i], mapping );
 			System.arraycopy( propertyNullness, 0, result, loc, propertyNullness.length );
 			loc += propertyNullness.length;
 		}
 		return result;
 	}
 
 	@Override
 	public boolean isEmbedded() {
 		return false;
 	}
 
+	@Override
 	public int getPropertyIndex(String name) {
 		String[] names = getPropertyNames();
 		for ( int i = 0, max = names.length; i < max; i++ ) {
 			if ( names[i].equals( name ) ) {
 				return i;
 			}
 		}
 		throw new PropertyNotFoundException(
 				"Unable to locate property named " + name + " on " + getReturnedClass().getName()
 		);
 	}
 
 	private Boolean canDoExtraction;
 
 	@Override
 	public boolean canDoExtraction() {
 		if ( canDoExtraction == null ) {
 			canDoExtraction = determineIfProcedureParamExtractionCanBePerformed();
 		}
 		return canDoExtraction;
 	}
 
 	private boolean determineIfProcedureParamExtractionCanBePerformed() {
 		for ( Type propertyType : propertyTypes ) {
 			if ( !ProcedureParameterExtractionAware.class.isInstance( propertyType ) ) {
 				return false;
 			}
 			if ( !( (ProcedureParameterExtractionAware) propertyType ).canDoExtraction() ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public Object extract(CallableStatement statement, int startIndex, SessionImplementor session) throws SQLException {
 		Object[] values = new Object[propertySpan];
 
 		int currentIndex = startIndex;
 		boolean notNull = false;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			// we know this cast is safe from canDoExtraction
 			final ProcedureParameterExtractionAware propertyType = (ProcedureParameterExtractionAware) propertyTypes[i];
 			final Object value = propertyType.extract( statement, currentIndex, session );
 			if ( value == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[i] = value;
 			currentIndex += propertyType.getColumnSpan( session.getFactory() );
 		}
 
 		if ( !notNull ) {
 			values = null;
 		}
 
 		return resolve( values, session, null );
 	}
 
 	@Override
 	public Object extract(CallableStatement statement, String[] paramNames, SessionImplementor session)
 			throws SQLException {
 		// for this form to work all sub-property spans must be one (1)...
 
 		Object[] values = new Object[propertySpan];
 
 		int indx = 0;
 		boolean notNull = false;
 		for ( String paramName : paramNames ) {
 			// we know this cast is safe from canDoExtraction
 			final ProcedureParameterExtractionAware propertyType = (ProcedureParameterExtractionAware) propertyTypes[indx];
 			final Object value = propertyType.extract( statement, new String[] {paramName}, session );
 			if ( value == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[indx] = value;
 		}
 
 		if ( !notNull ) {
 			values = null;
 		}
 
 		return resolve( values, session, null );
 	}
 
+	@Override
 	public boolean hasNotNullProperty() {
 		return hasNotNullProperty;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java b/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
index c77d3446e1..d3f56f2a9c 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
@@ -1,281 +1,298 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
+import org.hibernate.PropertyNotFoundException;
+import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
-import org.hibernate.engine.jdbc.Size;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.LoggableUserType;
 
-import org.dom4j.Element;
-import org.dom4j.Node;
-
 /**
  * Adapts {@link CompositeUserType} to the {@link Type} interface
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class CompositeCustomType extends AbstractType implements CompositeType, BasicType {
 	private final CompositeUserType userType;
 	private final String[] registrationKeys;
 	private final String name;
 	private final boolean customLogging;
 
 	public CompositeCustomType(CompositeUserType userType) {
 		this( userType, ArrayHelper.EMPTY_STRING_ARRAY );
 	}
 
 	public CompositeCustomType(CompositeUserType userType, String[] registrationKeys) {
 		this.userType = userType;
 		this.name = userType.getClass().getName();
 		this.customLogging = LoggableUserType.class.isInstance( userType );
 		this.registrationKeys = registrationKeys;
 	}
 
 	public String[] getRegistrationKeys() {
 		return registrationKeys;
 	}
 
 	public CompositeUserType getUserType() {
 		return userType;
 	}
 
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	public Type[] getSubtypes() {
 		return userType.getPropertyTypes();
 	}
 
 	public String[] getPropertyNames() {
 		return userType.getPropertyNames();
 	}
 
+	@Override
+	public int getPropertyIndex(String name) {
+		String[] names = getPropertyNames();
+		for ( int i = 0, max = names.length; i < max; i++ ) {
+			if ( names[i].equals( name ) ) {
+				return i;
+			}
+		}
+		throw new PropertyNotFoundException(
+				"Unable to locate property named " + name + " on " + getReturnedClass().getName()
+		);
+	}
+
 	public Object[] getPropertyValues(Object component, SessionImplementor session) throws HibernateException {
 		return getPropertyValues( component, EntityMode.POJO );
 	}
 
 	public Object[] getPropertyValues(Object component, EntityMode entityMode) throws HibernateException {
 		int len = getSubtypes().length;
 		Object[] result = new Object[len];
 		for ( int i = 0; i < len; i++ ) {
 			result[i] = getPropertyValue( component, i );
 		}
 		return result;
 	}
 
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) throws HibernateException {
 		for ( int i = 0; i < values.length; i++ ) {
 			userType.setPropertyValue( component, i, values[i] );
 		}
 	}
 
 	public Object getPropertyValue(Object component, int i, SessionImplementor session) throws HibernateException {
 		return getPropertyValue( component, i );
 	}
 
 	public Object getPropertyValue(Object component, int i) throws HibernateException {
 		return userType.getPropertyValue( component, i );
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return CascadeStyles.NONE;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return FetchMode.DEFAULT;
 	}
 
 	public boolean isComponentType() {
 		return true;
 	}
 
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return userType.deepCopy( value );
 	}
 
 	public Object assemble(
 			Serializable cached,
 			SessionImplementor session,
 			Object owner) throws HibernateException {
 		return userType.assemble( cached, session, owner );
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return userType.disassemble( value, session );
 	}
 
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache)
 			throws HibernateException {
 		return userType.replace( original, target, session, owner );
 	}
 
 	public boolean isEqual(Object x, Object y)
 			throws HibernateException {
 		return userType.equals( x, y );
 	}
 
 	public int getHashCode(Object x) {
 		return userType.hashCode( x );
 	}
 
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		Type[] types = userType.getPropertyTypes();
 		int n = 0;
 		for ( Type type : types ) {
 			n += type.getColumnSpan( mapping );
 		}
 		return n;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public Class getReturnedClass() {
 		return userType.returnedClass();
 	}
 
 	public boolean isMutable() {
 		return userType.isMutable();
 	}
 
 	public Object nullSafeGet(
 			ResultSet rs,
 			String columnName,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		return userType.nullSafeGet( rs, new String[] {columnName}, session, owner );
 	}
 
 	public Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		return userType.nullSafeGet( rs, names, session, owner );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 		userType.nullSafeSet( st, value, index, session );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		userType.nullSafeSet( st, value, index, session );
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		int[] result = new int[getColumnSpan( mapping )];
 		int n = 0;
 		for ( Type type : userType.getPropertyTypes() ) {
 			for ( int sqlType : type.sqlTypes( mapping ) ) {
 				result[n++] = sqlType;
 			}
 		}
 		return result;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[getColumnSpan( mapping )];
 		int soFar = 0;
 		for ( Type propertyType : userType.getPropertyTypes() ) {
 			final Size[] propertySizes = propertyType.dictatedSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[getColumnSpan( mapping )];
 		int soFar = 0;
 		for ( Type propertyType : userType.getPropertyTypes() ) {
 			final Size[] propertySizes = propertyType.defaultSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		else if ( customLogging ) {
 			return ( (LoggableUserType) userType ).toLoggableString( value, factory );
 		}
 		else {
 			return value.toString();
 		}
 	}
 
 	public boolean[] getPropertyNullability() {
 		return null;
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[getColumnSpan( mapping )];
 		if ( value == null ) {
 			return result;
 		}
 		Object[] values = getPropertyValues( value, EntityMode.POJO ); //TODO!!!!!!!
 		int loc = 0;
 		Type[] propertyTypes = getSubtypes();
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			boolean[] propertyNullness = propertyTypes[i].toColumnNullness( values[i], mapping );
 			System.arraycopy( propertyNullness, 0, result, loc, propertyNullness.length );
 			loc += propertyNullness.length;
 		}
 		return result;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return isDirty( old, current, session );
 	}
 
 	public boolean isEmbedded() {
 		return false;
 	}
+
+	@Override
+	public boolean hasNotNullProperty() {
+		// We just don't know.  So assume nullable
+		return false;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CompositeType.java b/hibernate-core/src/main/java/org/hibernate/type/CompositeType.java
index 5854ad4532..f5978cb31b 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CompositeType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CompositeType.java
@@ -1,134 +1,151 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.lang.reflect.Method;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * Contract for value types to hold collections and have cascades, etc.  The notion is that of composition.  JPA terms
  * this an embeddable.
  *
  * @author Steve Ebersole
  */
 public interface CompositeType extends Type {
 	/**
 	 * Get the types of the component properties
 	 *
 	 * @return The component property types.
 	 */
-	public Type[] getSubtypes();
+	Type[] getSubtypes();
 
 	/**
 	 * Get the names of the component properties
 	 *
 	 * @return The component property names
 	 */
-	public String[] getPropertyNames();
+	String[] getPropertyNames();
 
 	/**
 	 * Retrieve the indicators regarding which component properties are nullable.
 	 * <p/>
 	 * An optional operation
 	 *
 	 * @return nullability of component properties
 	 */
-	public boolean[] getPropertyNullability();
+	boolean[] getPropertyNullability();
 
 	/**
 	 * Extract the values of the component properties from the given component instance
 	 *
 	 * @param component The component instance
 	 * @param session The session from which the request originates
 	 *
 	 * @return The property values
 	 *
 	 * @throws HibernateException Indicates a problem access the property values.
 	 */
-	public Object[] getPropertyValues(Object component, SessionImplementor session) throws HibernateException;
+	Object[] getPropertyValues(Object component, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Extract the values of the component properties from the given component instance without access to the
 	 * session.
 	 * <p/>
 	 * An optional operation
 	 *
 	 * @param component The component instance
 	 * @param entityMode The entity mode
 	 *
 	 * @return The property values
 	 *
 	 * @throws HibernateException Indicates a problem access the property values.
 	 */
-	public Object[] getPropertyValues(Object component, EntityMode entityMode) throws HibernateException;
+	Object[] getPropertyValues(Object component, EntityMode entityMode) throws HibernateException;
 
 	/**
 	 * Extract a particular component property value indicated by index.
 	 *
 	 * @param component The component instance
 	 * @param index The index of the property whose value is to be extracted
 	 * @param session The session from which the request originates.
 	 *
 	 * @return The extracted component property value
 	 *
 	 * @throws HibernateException Indicates a problem access the property value.
 	 */
-	public Object getPropertyValue(Object component, int index, SessionImplementor session) throws HibernateException;
+	Object getPropertyValue(Object component, int index, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Inject property values onto the given component instance
 	 * <p/>
 	 * An optional operation
 	 *
 	 * @param component The component instance
 	 * @param values The values to inject
 	 * @param entityMode The entity mode
 	 *
 	 * @throws HibernateException Indicates an issue performing the injection
 	 */
-	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) throws HibernateException;
+	void setPropertyValues(Object component, Object[] values, EntityMode entityMode) throws HibernateException;
 
 	/**
 	 * Retrieve the cascade style of the indicated component property.
 	 *
 	 * @param index The property index,
 	 *
 	 * @return The cascade style.
 	 */
-	public CascadeStyle getCascadeStyle(int index);
+	CascadeStyle getCascadeStyle(int index);
 
 	/**
 	 * Retrieve the fetch mode of the indicated component property.
 	 *
 	 * @param index The property index,
 	 *
 	 * @return The fetch mode
 	 */
-	public FetchMode getFetchMode(int index);
+	FetchMode getFetchMode(int index);
 
 	/**
 	 * Is the given method a member of this component's class?
 	 *
 	 * @param method The method to check
 	 *
 	 * @return True if the method is a member; false otherwise.
 	 */
-	public boolean isMethodOf(Method method);
+	boolean isMethodOf(Method method);
 
 	/**
 	 * Is this component embedded?  "embedded" indicates that the component is "virtual", that its properties are
 	 * "flattened" onto its owner
 	 *
 	 * @return True if this component is embedded; false otherwise.
 	 */
-	public boolean isEmbedded();
+	boolean isEmbedded();
+
+	/**
+	 * Convenience method to quickly check {@link #getPropertyNullability} for any non-nullable sub-properties.
+	 *
+	 * @return {@code true} if any of the properties are not-nullable as indicated by {@link #getPropertyNullability},
+	 * {@code false} otherwise.
+	 */
+	boolean hasNotNullProperty();
+
+	/**
+	 * Convenience method for locating the property index for a given property name.
+	 *
+	 * @param propertyName The (sub-)property name to find.
+	 *
+	 * @return The (sub-)property index, relative to all the array-valued method returns defined on this contract.
+	 */
+	int getPropertyIndex(String propertyName);
 }
