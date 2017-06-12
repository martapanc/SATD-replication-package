diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
index 5a18231060..7ef2512008 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
@@ -1,1245 +1,1294 @@
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
+import java.util.Map;
 
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
 import org.hibernate.type.CompositeType;
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
 
 	protected boolean isConnectedToSession() {
 		return session != null
 				&& session.isOpen()
 				&& session.getPersistenceContext().containsCollection( this );
 	}
 
 	protected boolean isInitialized() {
 		return initialized;
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
 	protected boolean isInverseCollection() {
 		final CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( this );
 		return ce != null && ce.getLoadedPersister().isInverse();
 	}
 
 	/**
 	 * Is this the "inverse" end of a bidirectional association with
 	 * no orphan delete enabled?
 	 */
 	@SuppressWarnings({"JavaDoc"})
 	protected boolean isInverseCollectionNoOrphanDelete() {
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
 	protected boolean isInverseOneToManyOrNoOrphanDelete() {
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
+	 * Replace entity instances with copy in {@code copyCache}/.
+	 *
+	 * @param copyCache - mapping from entity in the process of being
+	 *                    merged to managed copy.
+	 */
+	public final void replaceQueuedOperationValues(CollectionPersister persister, Map copyCache) {
+		for ( DelayedOperation operation : operationQueue ) {
+			if ( ValueDelayedOperation.class.isInstance( operation ) ) {
+				( (ValueDelayedOperation) operation ).replace( persister, copyCache );
+			}
+		}
+	}
+
+	/**
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
 			allowLoadOutsideTransaction = session.getFactory().getSessionFactoryOptions().isInitializeLazyStateOutsideTransactionsEnabled();
 
 			if ( allowLoadOutsideTransaction && sessionFactoryUuid == null ) {
 				sessionFactoryUuid = session.getFactory().getUuid();
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
 		// Selecting a type used in where part of update statement
 		// (must match condidion in org.hibernate.persister.collection.BasicCollectionPersister.doUpdateRows).
 		// See HHH-9474
 		Type whereType;
 		if ( persister.hasIndex() ) {
 			whereType = persister.getIndexType();
 		}
 		else {
 			whereType = persister.getElementType();
 		}
 		if ( whereType instanceof CompositeType ) {
 			CompositeType componentIndexType = (CompositeType) whereType;
 			return !componentIndexType.hasNotNullProperty();
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
 
+	protected interface ValueDelayedOperation extends DelayedOperation {
+		void replace(CollectionPersister collectionPersister, Map copyCache);
+	}
+
+	protected abstract class AbstractValueDelayedOperation implements ValueDelayedOperation {
+		private Object addedValue;
+		private Object orphan;
+
+		protected AbstractValueDelayedOperation(Object addedValue, Object orphan) {
+			this.addedValue = addedValue;
+			this.orphan = orphan;
+		}
+
+		public void replace(CollectionPersister persister, Map copyCache) {
+			if ( addedValue != null ) {
+				addedValue = getReplacement( persister.getElementType(), addedValue, copyCache );
+			}
+		}
+
+		protected final Object getReplacement(Type type, Object current, Map copyCache) {
+			return type.replace( current, null, session, owner, copyCache );
+		}
+
+		@Override
+		public final Object getAddedInstance() {
+			return addedValue;
+		}
+
+		@Override
+		public final Object getOrphan() {
+			return orphan;
+		}
+	}
+
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
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentBag.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentBag.java
index 1409be8d79..b131fa85e8 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentBag.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentBag.java
@@ -1,573 +1,561 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.collection.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
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
 
 	/**
 	 * Constructs a PersistentBag.  Needed for SOAP libraries, etc
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public PersistentBag() {
 	}
 
 	/**
 	 * Constructs a PersistentBag
 	 *
 	 * @param session The session
 	 */
 	public PersistentBag(SessionImplementor session) {
 		super( session );
 	}
 
 	/**
 	 * Constructs a PersistentBag
 	 *
 	 * @param session The session
 	 * @param coll The base elements.
 	 */
 	@SuppressWarnings("unchecked")
 	public PersistentBag(SessionImplementor session, Collection coll) {
 		super( session );
 		if ( coll instanceof List ) {
 			bag = (List) coll;
 		}
 		else {
 			bag = new ArrayList();
 			for ( Object element : coll ) {
 				bag.add( element );
 			}
 		}
 		setInitialized();
 		setDirectlyAccessible( true );
 	}
 
 	@Override
 	public boolean isWrapper(Object collection) {
 		return bag==collection;
 	}
 
 	@Override
 	public boolean empty() {
 		return bag.isEmpty();
 	}
 
 	@Override
 	public Iterator entries(CollectionPersister persister) {
 		return bag.iterator();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner)
 			throws HibernateException, SQLException {
 		// note that if we load this collection from a cartesian product
 		// the multiplicity would be broken ... so use an idbag instead
 		final Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() ) ;
 		if ( element != null ) {
 			bag.add( element );
 		}
 		return element;
 	}
 
 	@Override
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
 		this.bag = (List) persister.getCollectionType().instantiate( anticipatedSize );
 	}
 
 	@Override
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		final Type elementType = persister.getElementType();
 		final List sn = (List) getSnapshot();
 		if ( sn.size() != bag.size() ) {
 			return false;
 		}
 		for ( Object elt : bag ) {
 			final boolean unequal = countOccurrences( elt, bag, elementType ) != countOccurrences( elt, sn, elementType );
 			if ( unequal ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return ( (Collection) snapshot ).isEmpty();
 	}
 
 	private int countOccurrences(Object element, List list, Type elementType)
 			throws HibernateException {
 		final Iterator iter = list.iterator();
 		int result = 0;
 		while ( iter.hasNext() ) {
 			if ( elementType.isSame( element, iter.next() ) ) {
 				result++;
 			}
 		}
 		return result;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Serializable getSnapshot(CollectionPersister persister)
 			throws HibernateException {
 		final ArrayList clonedList = new ArrayList( bag.size() );
 		for ( Object item : bag ) {
 			clonedList.add( persister.getElementType().deepCopy( item, persister.getFactory() ) );
 		}
 		return clonedList;
 	}
 
 	@Override
 	public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
 		final List sn = (List) snapshot;
 		return getOrphans( sn, bag, entityName, getSession() );
 	}
 
 	@Override
 	public Serializable disassemble(CollectionPersister persister)
 			throws HibernateException {
 		final int length = bag.size();
 		final Serializable[] result = new Serializable[length];
 		for ( int i=0; i<length; i++ ) {
 			result[i] = persister.getElementType().disassemble( bag.get( i ), getSession(), null );
 		}
 		return result;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 			throws HibernateException {
 		final Serializable[] array = (Serializable[]) disassembled;
 		final int size = array.length;
 		beforeInitialize( persister, size );
 		for ( Serializable item : array ) {
 			final Object element = persister.getElementType().assemble( item, getSession(), owner );
 			if ( element != null ) {
 				bag.add( element );
 			}
 		}
 	}
 
 	@Override
 	public boolean needsRecreate(CollectionPersister persister) {
 		return !persister.isOneToMany();
 	}
 
 
 	// For a one-to-many, a <bag> is not really a bag;
 	// it is *really* a set, since it can't contain the
 	// same element twice. It could be considered a bug
 	// in the mapping dtd that <bag> allows <one-to-many>.
 
 	// Anyway, here we implement <set> semantics for a
 	// <one-to-many> <bag>!
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
 		final Type elementType = persister.getElementType();
 		final ArrayList deletes = new ArrayList();
 		final List sn = (List) getSnapshot();
 		final Iterator olditer = sn.iterator();
 		int i=0;
 		while ( olditer.hasNext() ) {
 			final Object old = olditer.next();
 			final Iterator newiter = bag.iterator();
 			boolean found = false;
 			if ( bag.size()>i && elementType.isSame( old, bag.get( i++ ) ) ) {
 			//a shortcut if its location didn't change!
 				found = true;
 			}
 			else {
 				//search for it
 				//note that this code is incorrect for other than one-to-many
 				while ( newiter.hasNext() ) {
 					if ( elementType.isSame( old, newiter.next() ) ) {
 						found = true;
 						break;
 					}
 				}
 			}
 			if ( !found ) {
 				deletes.add( old );
 			}
 		}
 		return deletes.iterator();
 	}
 
 	@Override
 	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
 		final List sn = (List) getSnapshot();
 		if ( sn.size() > i && elemType.isSame( sn.get( i ), entry ) ) {
 			//a shortcut if its location didn't change!
 			return false;
 		}
 		else {
 			//search for it
 			//note that this code is incorrect for other than one-to-many
 			for ( Object old : sn ) {
 				if ( elemType.isSame( old, entry ) ) {
 					return false;
 				}
 			}
 			return true;
 		}
 	}
 
 	@Override
 	public boolean isRowUpdatePossible() {
 		return false;
 	}
 
 	@Override
 	public boolean needsUpdating(Object entry, int i, Type elemType) {
 		return false;
 	}
 
 	@Override
 	public int size() {
 		return readSize() ? getCachedSize() : bag.size();
 	}
 
 	@Override
 	public boolean isEmpty() {
 		return readSize() ? getCachedSize()==0 : bag.isEmpty();
 	}
 
 	@Override
 	public boolean contains(Object object) {
 		final Boolean exists = readElementExistence( object );
 		return exists == null ? bag.contains( object ) : exists;
 	}
 
 	@Override
 	public Iterator iterator() {
 		read();
 		return new IteratorProxy( bag.iterator() );
 	}
 
 	@Override
 	public Object[] toArray() {
 		read();
 		return bag.toArray();
 	}
 
 	@Override
 	public Object[] toArray(Object[] a) {
 		read();
 		return bag.toArray( a );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean add(Object object) {
 		if ( !isOperationQueueEnabled() ) {
 			write();
 			return bag.add( object );
 		}
 		else {
 			queueOperation( new SimpleAdd( object ) );
 			return true;
 		}
 	}
 
 	@Override
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
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean containsAll(Collection c) {
 		read();
 		return bag.containsAll( c );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean addAll(Collection values) {
 		if ( values.size()==0 ) {
 			return false;
 		}
 		if ( !isOperationQueueEnabled() ) {
 			write();
 			return bag.addAll( values );
 		}
 		else {
 			for ( Object value : values ) {
 				queueOperation( new SimpleAdd( value ) );
 			}
 			return values.size()>0;
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
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
 
 	@Override
 	@SuppressWarnings("unchecked")
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
 
 	@Override
 	@SuppressWarnings("unchecked")
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
 
 	@Override
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
 		throw new UnsupportedOperationException("Bags don't have indexes");
 	}
 
 	@Override
 	public Object getElement(Object entry) {
 		return entry;
 	}
 
 	@Override
 	public Object getSnapshotElement(Object entry, int i) {
 		final List sn = (List) getSnapshot();
 		return sn.get( i );
 	}
 
 	/**
 	 * Count how many times the given object occurs in the elements
 	 *
 	 * @param o The object to check
 	 *
 	 * @return The number of occurences.
 	 */
 	@SuppressWarnings("UnusedDeclaration")
 	public int occurrences(Object o) {
 		read();
 		final Iterator itr = bag.iterator();
 		int result = 0;
 		while ( itr.hasNext() ) {
 			if ( o.equals( itr.next() ) ) {
 				result++;
 			}
 		}
 		return result;
 	}
 
 	// List OPERATIONS:
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public void add(int i, Object o) {
 		write();
 		bag.add( i, o );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean addAll(int i, Collection c) {
 		if ( c.size() > 0 ) {
 			write();
 			return bag.addAll( i, c );
 		}
 		else {
 			return false;
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object get(int i) {
 		read();
 		return bag.get( i );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public int indexOf(Object o) {
 		read();
 		return bag.indexOf( o );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public int lastIndexOf(Object o) {
 		read();
 		return bag.lastIndexOf( o );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public ListIterator listIterator() {
 		read();
 		return new ListIteratorProxy( bag.listIterator() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public ListIterator listIterator(int i) {
 		read();
 		return new ListIteratorProxy( bag.listIterator( i ) );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object remove(int i) {
 		write();
 		return bag.remove( i );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object set(int i, Object o) {
 		write();
 		return bag.set( i, o );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public List subList(int start, int end) {
 		read();
 		return new ListProxy( bag.subList( start, end ) );
 	}
 
 	@Override
 	public boolean entryExists(Object entry, int i) {
 		return entry!=null;
 	}
 
 	@Override
 	public String toString() {
 		read();
 		return bag.toString();
 	}
 
 	/**
 	 * Bag does not respect the collection API and do an
 	 * JVM instance comparison to do the equals.
 	 * The semantic is broken not to have to initialize a
 	 * collection for a simple equals() operation.
 	 * @see java.lang.Object#equals(java.lang.Object)
 	 *
 	 * {@inheritDoc}
 	 */
 	@Override
 	public boolean equals(Object obj) {
 		return super.equals( obj );
 	}
 
 	@Override
 	public int hashCode() {
 		return super.hashCode();
 	}
 
 	final class Clear implements DelayedOperation {
 		@Override
 		public void operate() {
 			bag.clear();
 		}
 
 		@Override
 		public Object getAddedInstance() {
 			return null;
 		}
 
 		@Override
 		public Object getOrphan() {
 			throw new UnsupportedOperationException("queued clear cannot be used with orphan delete");
 		}
 	}
 
-	final class SimpleAdd implements DelayedOperation {
-		private Object value;
+	final class SimpleAdd extends AbstractValueDelayedOperation {
 
-		public SimpleAdd(Object value) {
-			this.value = value;
+		public SimpleAdd(Object addedValue) {
+			super( addedValue, null );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public void operate() {
-			bag.add( value );
-		}
-
-		@Override
-		public Object getAddedInstance() {
-			return value;
-		}
-
-		@Override
-		public Object getOrphan() {
-			return null;
+			bag.add( getAddedInstance() );
 		}
 	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java
index 66cabbc604..599b312610 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentList.java
@@ -1,641 +1,590 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.collection.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.ListIterator;
-import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
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
 
 	/**
 	 * Constructs a PersistentList.  This form needed for SOAP libraries, etc
 	 */
 	public PersistentList() {
 	}
 
 	/**
 	 * Constructs a PersistentList.
 	 *
 	 * @param session The session
 	 */
 	public PersistentList(SessionImplementor session) {
 		super( session );
 	}
 
 	/**
 	 * Constructs a PersistentList.
 	 *
 	 * @param session The session
 	 * @param list The raw list
 	 */
 	public PersistentList(SessionImplementor session, List list) {
 		super( session );
 		this.list = list;
 		setInitialized();
 		setDirectlyAccessible( true );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
 		final ArrayList clonedList = new ArrayList( list.size() );
 		for ( Object element : list ) {
 			final Object deepCopy = persister.getElementType().deepCopy( element, persister.getFactory() );
 			clonedList.add( deepCopy );
 		}
 		return clonedList;
 	}
 
 	@Override
 	public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
 		final List sn = (List) snapshot;
 		return getOrphans( sn, list, entityName, getSession() );
 	}
 
 	@Override
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		final Type elementType = persister.getElementType();
 		final List sn = (List) getSnapshot();
 		if ( sn.size()!=this.list.size() ) {
 			return false;
 		}
 		final Iterator itr = list.iterator();
 		final Iterator snapshotItr = sn.iterator();
 		while ( itr.hasNext() ) {
 			if ( elementType.isDirty( itr.next(), snapshotItr.next(), getSession() ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return ( (Collection) snapshot ).isEmpty();
 	}
 
 	@Override
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
 		this.list = (List) persister.getCollectionType().instantiate( anticipatedSize );
 	}
 
 	@Override
 	public boolean isWrapper(Object collection) {
 		return list==collection;
 	}
 
 	@Override
 	public int size() {
 		return readSize() ? getCachedSize() : list.size();
 	}
 
 	@Override
 	public boolean isEmpty() {
 		return readSize() ? getCachedSize()==0 : list.isEmpty();
 	}
 
 	@Override
 	public boolean contains(Object object) {
 		final Boolean exists = readElementExistence( object );
 		return exists == null
 				? list.contains( object )
 				: exists;
 	}
 
 	@Override
 	public Iterator iterator() {
 		read();
 		return new IteratorProxy( list.iterator() );
 	}
 
 	@Override
 	public Object[] toArray() {
 		read();
 		return list.toArray();
 	}
 
 	@Override
 	public Object[] toArray(Object[] array) {
 		read();
 		return list.toArray( array );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean add(Object object) {
 		if ( !isOperationQueueEnabled() ) {
 			write();
 			return list.add( object );
 		}
 		else {
 			queueOperation( new SimpleAdd( object ) );
 			return true;
 		}
 	}
 
 	@Override
 	public boolean remove(Object value) {
 		final Boolean exists = isPutQueueEnabled() ? readElementExistence( value ) : null;
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
 		else if ( exists ) {
 			queueOperation( new SimpleRemove( value ) );
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean containsAll(Collection coll) {
 		read();
 		return list.containsAll( coll );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean addAll(Collection values) {
 		if ( values.size()==0 ) {
 			return false;
 		}
 		if ( !isOperationQueueEnabled() ) {
 			write();
 			return list.addAll( values );
 		}
 		else {
 			for ( Object value : values ) {
 				queueOperation( new SimpleAdd( value ) );
 			}
 			return values.size()>0;
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean addAll(int index, Collection coll) {
 		if ( coll.size()>0 ) {
 			write();
 			return list.addAll( index,  coll );
 		}
 		else {
 			return false;
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
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
 
 	@Override
 	@SuppressWarnings("unchecked")
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
 
 	@Override
 	@SuppressWarnings("unchecked")
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
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object get(int index) {
 		if ( index < 0 ) {
 			throw new ArrayIndexOutOfBoundsException( "negative index" );
 		}
 		final Object result = readElementByIndex( index );
 		return result == UNKNOWN ? list.get( index ) : result;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object set(int index, Object value) {
 		if (index<0) {
 			throw new ArrayIndexOutOfBoundsException("negative index");
 		}
 
 		final Object old = isPutQueueEnabled() ? readElementByIndex( index ) : UNKNOWN;
 
 		if ( old==UNKNOWN ) {
 			write();
 			return list.set( index, value );
 		}
 		else {
 			queueOperation( new Set( index, value, old ) );
 			return old;
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object remove(int index) {
 		if ( index < 0 ) {
 			throw new ArrayIndexOutOfBoundsException( "negative index" );
 		}
 		final Object old = isPutQueueEnabled() ? readElementByIndex( index ) : UNKNOWN;
 		if ( old == UNKNOWN ) {
 			write();
 			return list.remove( index );
 		}
 		else {
 			queueOperation( new Remove( index, old ) );
 			return old;
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public void add(int index, Object value) {
 		if ( index < 0 ) {
 			throw new ArrayIndexOutOfBoundsException( "negative index" );
 		}
 		if ( !isInitialized() || isConnectedToSession() ) {
 			// NOTE : we don't care about the inverse part here because
 			// even if the collection is inverse, this side is driving the
 			// writing of the indexes.  And because this is a positioned-add
 			// we need to load the underlying elements to know how that
 			// affects overall re-ordering
 			write();
 			list.add( index, value );
 		}
 		else {
 			queueOperation( new Add( index, value ) );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public int indexOf(Object value) {
 		read();
 		return list.indexOf( value );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public int lastIndexOf(Object value) {
 		read();
 		return list.lastIndexOf( value );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public ListIterator listIterator() {
 		read();
 		return new ListIteratorProxy( list.listIterator() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public ListIterator listIterator(int index) {
 		read();
 		return new ListIteratorProxy( list.listIterator( index ) );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public java.util.List subList(int from, int to) {
 		read();
 		return new ListProxy( list.subList( from, to ) );
 	}
 
 	@Override
 	public boolean empty() {
 		return list.isEmpty();
 	}
 
 	@Override
 	public String toString() {
 		read();
 		return list.toString();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object readFrom(ResultSet rs, CollectionPersister persister, CollectionAliases descriptor, Object owner)
 			throws HibernateException, SQLException {
 		final Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() ) ;
 		final int index = (Integer) persister.readIndex( rs, descriptor.getSuffixedIndexAliases(), getSession() );
 
 		//pad with nulls from the current last element up to the new index
 		for ( int i = list.size(); i<=index; i++) {
 			list.add( i, null );
 		}
 
 		list.set( index, element );
 		return element;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Iterator entries(CollectionPersister persister) {
 		return list.iterator();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 			throws HibernateException {
 		final Serializable[] array = (Serializable[]) disassembled;
 		final int size = array.length;
 		beforeInitialize( persister, size );
 		for ( Serializable arrayElement : array ) {
 			list.add( persister.getElementType().assemble( arrayElement, getSession(), owner ) );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Serializable disassemble(CollectionPersister persister) throws HibernateException {
 		final int length = list.size();
 		final Serializable[] result = new Serializable[length];
 		for ( int i=0; i<length; i++ ) {
 			result[i] = persister.getElementType().disassemble( list.get( i ), getSession(), null );
 		}
 		return result;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
 		final List deletes = new ArrayList();
 		final List sn = (List) getSnapshot();
 		int end;
 		if ( sn.size() > list.size() ) {
 			for ( int i=list.size(); i<sn.size(); i++ ) {
 				deletes.add( indexIsFormula ? sn.get( i ) : i );
 			}
 			end = list.size();
 		}
 		else {
 			end = sn.size();
 		}
 		for ( int i=0; i<end; i++ ) {
 			final Object item = list.get( i );
 			final Object snapshotItem = sn.get( i );
 			if ( item == null && snapshotItem != null ) {
 				deletes.add( indexIsFormula ? snapshotItem : i );
 			}
 		}
 		return deletes.iterator();
 	}
 
 	@Override
 	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
 		final List sn = (List) getSnapshot();
 		return list.get( i ) != null && ( i >= sn.size() || sn.get( i ) == null );
 	}
 
 	@Override
 	public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
 		final List sn = (List) getSnapshot();
 		return i < sn.size()
 				&& sn.get( i ) != null
 				&& list.get( i ) != null
 				&& elemType.isDirty( list.get( i ), sn.get( i ), getSession() );
 	}
 
 	@Override
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
 		return i;
 	}
 
 	@Override
 	public Object getElement(Object entry) {
 		return entry;
 	}
 
 	@Override
 	public Object getSnapshotElement(Object entry, int i) {
 		final List sn = (List) getSnapshot();
 		return sn.get( i );
 	}
 
 	@Override
 	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
 	public boolean equals(Object other) {
 		read();
 		return list.equals( other );
 	}
 
 	@Override
 	public int hashCode() {
 		read();
 		return list.hashCode();
 	}
 
 	@Override
 	public boolean entryExists(Object entry, int i) {
 		return entry!=null;
 	}
 
 	final class Clear implements DelayedOperation {
 		@Override
 		public void operate() {
 			list.clear();
 		}
 
 		@Override
 		public Object getAddedInstance() {
 			return null;
 		}
 
 		@Override
 		public Object getOrphan() {
 			throw new UnsupportedOperationException( "queued clear cannot be used with orphan delete" );
 		}
 	}
 
-	final class SimpleAdd implements DelayedOperation {
-		private Object value;
+	final class SimpleAdd extends AbstractValueDelayedOperation {
 
-		public SimpleAdd(Object value) {
-			this.value = value;
+		public SimpleAdd(Object addedValue) {
+			super( addedValue, null );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public void operate() {
-			list.add( value );
-		}
-
-		@Override
-		public Object getAddedInstance() {
-			return value;
-		}
-
-		@Override
-		public Object getOrphan() {
-			return null;
+			list.add( getAddedInstance() );
 		}
 	}
 
-	final class Add implements DelayedOperation {
+	abstract class AbstractListValueDelayedOperation extends AbstractValueDelayedOperation {
 		private int index;
-		private Object value;
 
-		public Add(int index, Object value) {
+		AbstractListValueDelayedOperation(Integer index, Object addedValue, Object orphan) {
+			super( addedValue, orphan );
 			this.index = index;
-			this.value = value;
 		}
 
-		@Override
-		@SuppressWarnings("unchecked")
-		public void operate() {
-			list.add( index, value );
-		}
-
-		@Override
-		public Object getAddedInstance() {
-			return value;
-		}
-
-		@Override
-		public Object getOrphan() {
-			return null;
+		protected final int getIndex() {
+			return index;
 		}
 	}
 
-	final class Set implements DelayedOperation {
-		private int index;
-		private Object value;
-		private Object old;
+	final class Add extends AbstractListValueDelayedOperation {
 
-		public Set(int index, Object value, Object old) {
-			this.index = index;
-			this.value = value;
-			this.old = old;
+		public Add(int index, Object addedValue) {
+			super( index, addedValue, null );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public void operate() {
-			list.set( index, value );
-		}
-
-		@Override
-		public Object getAddedInstance() {
-			return value;
-		}
-
-		@Override
-		public Object getOrphan() {
-			return old;
+			list.add( getIndex(), getAddedInstance() );
 		}
 	}
 
-	final class Remove implements DelayedOperation {
-		private int index;
-		private Object old;
+	final class Set extends AbstractListValueDelayedOperation {
 
-		public Remove(int index, Object old) {
-			this.index = index;
-			this.old = old;
+		public Set(int index, Object addedValue, Object orphan) {
+			super( index, addedValue, orphan );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public void operate() {
-			list.remove( index );
-		}
-
-		@Override
-		public Object getAddedInstance() {
-			return null;
-		}
-
-		@Override
-		public Object getOrphan() {
-			return old;
+			list.set( getIndex(), getAddedInstance() );
 		}
 	}
 
-	final class SimpleRemove implements DelayedOperation {
-		private Object value;
+	final class Remove extends AbstractListValueDelayedOperation {
 
-		public SimpleRemove(Object value) {
-			this.value = value;
+		public Remove(int index, Object orphan) {
+			super( index, null, orphan );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public void operate() {
-			list.remove( value );
+			 list.remove( getIndex() );
 		}
+	}
 
-		@Override
-		public Object getAddedInstance() {
-			return null;
+	final class SimpleRemove extends AbstractValueDelayedOperation {
+
+		public SimpleRemove(Object orphan) {
+			super( null, orphan );
 		}
 
 		@Override
-		public Object getOrphan() {
-			return value;
+		@SuppressWarnings("unchecked")
+		public void operate() {
+			list.remove( getOrphan() );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMap.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMap.java
index ab85032c5c..8496697fb7 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMap.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentMap.java
@@ -1,628 +1,611 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.collection.internal;
 
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
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
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
 		super( session );
 	}
 
 	/**
 	 * Instantiates a non-lazy map (the underlying map is constructed
 	 * from the incoming map reference).
 	 *
 	 * @param session The session to which this map will belong.
 	 * @param map The underlying map data.
 	 */
 	public PersistentMap(SessionImplementor session, Map map) {
 		super( session );
 		this.map = map;
 		setInitialized();
 		setDirectlyAccessible( true );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
 		final HashMap clonedMap = new HashMap( map.size() );
 		for ( Object o : map.entrySet() ) {
 			final Entry e = (Entry) o;
 			final Object copy = persister.getElementType().deepCopy( e.getValue(), persister.getFactory() );
 			clonedMap.put( e.getKey(), copy );
 		}
 		return clonedMap;
 	}
 
 	@Override
 	public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
 		final Map sn = (Map) snapshot;
 		return getOrphans( sn.values(), map.values(), entityName, getSession() );
 	}
 
 	@Override
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		final Type elementType = persister.getElementType();
 		final Map snapshotMap = (Map) getSnapshot();
 		if ( snapshotMap.size() != this.map.size() ) {
 			return false;
 		}
 
 		for ( Object o : map.entrySet() ) {
 			final Entry entry = (Entry) o;
 			if ( elementType.isDirty( entry.getValue(), snapshotMap.get( entry.getKey() ), getSession() ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return ( (Map) snapshot ).isEmpty();
 	}
 
 	@Override
 	public boolean isWrapper(Object collection) {
 		return map==collection;
 	}
 
 	@Override
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
 		this.map = (Map) persister.getCollectionType().instantiate( anticipatedSize );
 	}
 
 	@Override
 	public int size() {
 		return readSize() ? getCachedSize() : map.size();
 	}
 
 	@Override
 	public boolean isEmpty() {
 		return readSize() ? getCachedSize()==0 : map.isEmpty();
 	}
 
 	@Override
 	public boolean containsKey(Object key) {
 		final Boolean exists = readIndexExistence( key );
 		return exists == null ? map.containsKey( key ) : exists;
 	}
 
 	@Override
 	public boolean containsValue(Object value) {
 		final Boolean exists = readElementExistence( value );
 		return exists == null
 				? map.containsValue( value )
 				: exists;
 	}
 
 	@Override
 	public Object get(Object key) {
 		final Object result = readElementByIndex( key );
 		return result == UNKNOWN
 				? map.get( key )
 				: result;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object put(Object key, Object value) {
 		if ( isPutQueueEnabled() ) {
 			final Object old = readElementByIndex( key );
 			if ( old != UNKNOWN ) {
 				queueOperation( new Put( key, value, old ) );
 				return old;
 			}
 		}
 		initialize( true );
 		final Object old = map.put( key, value );
 		// would be better to use the element-type to determine
 		// whether the old and the new are equal here; the problem being
 		// we do not necessarily have access to the element type in all
 		// cases
 		if ( value != old ) {
 			dirty();
 		}
 		return old;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object remove(Object key) {
 		if ( isPutQueueEnabled() ) {
 			final Object old = readElementByIndex( key );
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
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public void putAll(Map puts) {
 		if ( puts.size() > 0 ) {
 			initialize( true );
 			for ( Object o : puts.entrySet() ) {
 				final Entry entry = (Entry) o;
 				put( entry.getKey(), entry.getValue() );
 			}
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
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
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Set keySet() {
 		read();
 		return new SetProxy( map.keySet() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Collection values() {
 		read();
 		return new SetProxy( map.values() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Set entrySet() {
 		read();
 		return new EntrySetProxy( map.entrySet() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean empty() {
 		return map.isEmpty();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public String toString() {
 		read();
 		return map.toString();
 	}
 
 	private transient List<Object[]> loadingEntries;
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object readFrom(
 			ResultSet rs,
 			CollectionPersister persister,
 			CollectionAliases descriptor,
 			Object owner) throws HibernateException, SQLException {
 		final Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() );
 		if ( element != null ) {
 			final Object index = persister.readIndex( rs, descriptor.getSuffixedIndexAliases(), getSession() );
 			if ( loadingEntries == null ) {
 				loadingEntries = new ArrayList<Object[]>();
 			}
 			loadingEntries.add( new Object[] { index, element } );
 		}
 		return element;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean endRead() {
 		if ( loadingEntries != null ) {
 			for ( Object[] entry : loadingEntries ) {
 				map.put( entry[0], entry[1] );
 			}
 			loadingEntries = null;
 		}
 		return super.endRead();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Iterator entries(CollectionPersister persister) {
 		return map.entrySet().iterator();
 	}
 
 	/**
 	 * a wrapper for Map.Entry sets
 	 */
 	class EntrySetProxy implements Set {
 		private final Set set;
 		EntrySetProxy(Set set) {
 			this.set=set;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public boolean add(Object entry) {
 			//write(); -- doesn't
 			return set.add( entry );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public boolean addAll(Collection entries) {
 			//write(); -- doesn't
 			return set.addAll( entries );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public void clear() {
 			write();
 			set.clear();
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public boolean contains(Object entry) {
 			return set.contains( entry );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public boolean containsAll(Collection entries) {
 			return set.containsAll( entries );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public boolean isEmpty() {
 			return set.isEmpty();
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public Iterator iterator() {
 			return new EntryIteratorProxy( set.iterator() );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public boolean remove(Object entry) {
 			write();
 			return set.remove( entry );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public boolean removeAll(Collection entries) {
 			write();
 			return set.removeAll( entries );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public boolean retainAll(Collection entries) {
 			write();
 			return set.retainAll( entries );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public int size() {
 			return set.size();
 		}
 
 		// amazingly, these two will work because AbstractCollection
 		// uses iterator() to fill the array
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public Object[] toArray() {
 			return set.toArray();
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public Object[] toArray(Object[] array) {
 			return set.toArray( array );
 		}
 	}
 
 	final class EntryIteratorProxy implements Iterator {
 		private final Iterator iter;
 		EntryIteratorProxy(Iterator iter) {
 			this.iter=iter;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public boolean hasNext() {
 			return iter.hasNext();
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public Object next() {
 			return new MapEntryProxy( (Map.Entry) iter.next() );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
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
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public Object getKey() {
 			return me.getKey();
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public Object getValue() {
 			return me.getValue();
 		}
 
 		@Override
 		@SuppressWarnings({"unchecked", "EqualsWhichDoesntCheckParameterClass"})
 		public boolean equals(Object o) {
 			return me.equals( o );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public int hashCode() {
 			return me.hashCode();
 		}
 
 		// finally, what it's all about...
 		@Override
 		@SuppressWarnings("unchecked")
 		public Object setValue(Object value) {
 			write();
 			return me.setValue( value );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 			throws HibernateException {
 		final Serializable[] array = (Serializable[]) disassembled;
 		final int size = array.length;
 		beforeInitialize( persister, size );
 		for ( int i = 0; i < size; i+=2 ) {
 			map.put(
 					persister.getIndexType().assemble( array[i], getSession(), owner ),
 					persister.getElementType().assemble( array[i+1], getSession(), owner )
 			);
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Serializable disassemble(CollectionPersister persister) throws HibernateException {
 		final Serializable[] result = new Serializable[ map.size() * 2 ];
 		final Iterator itr = map.entrySet().iterator();
 		int i=0;
 		while ( itr.hasNext() ) {
 			final Map.Entry e = (Map.Entry) itr.next();
 			result[i++] = persister.getIndexType().disassemble( e.getKey(), getSession(), null );
 			result[i++] = persister.getElementType().disassemble( e.getValue(), getSession(), null );
 		}
 		return result;
 
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
 		final List deletes = new ArrayList();
 		for ( Object o : ((Map) getSnapshot()).entrySet() ) {
 			final Entry e = (Entry) o;
 			final Object key = e.getKey();
 			if ( e.getValue() != null && map.get( key ) == null ) {
 				deletes.add( indexIsFormula ? e.getValue() : key );
 			}
 		}
 		return deletes.iterator();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
 		final Map sn = (Map) getSnapshot();
 		final Map.Entry e = (Map.Entry) entry;
 		return e.getValue() != null && sn.get( e.getKey() ) == null;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
 		final Map sn = (Map) getSnapshot();
 		final Map.Entry e = (Map.Entry) entry;
 		final Object snValue = sn.get( e.getKey() );
 		return e.getValue() != null
 				&& snValue != null
 				&& elemType.isDirty( snValue, e.getValue(), getSession() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
 		return ( (Map.Entry) entry ).getKey();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object getElement(Object entry) {
 		return ( (Map.Entry) entry ).getValue();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object getSnapshotElement(Object entry, int i) {
 		final Map sn = (Map) getSnapshot();
 		return sn.get( ( (Map.Entry) entry ).getKey() );
 	}
 
 	@Override
 	@SuppressWarnings({"unchecked", "EqualsWhichDoesntCheckParameterClass"})
 	public boolean equals(Object other) {
 		read();
 		return map.equals( other );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public int hashCode() {
 		read();
 		return map.hashCode();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean entryExists(Object entry, int i) {
 		return ( (Map.Entry) entry ).getValue() != null;
 	}
 
 	final class Clear implements DelayedOperation {
 		@Override
 		public void operate() {
 			map.clear();
 		}
 
 		@Override
 		public Object getAddedInstance() {
 			return null;
 		}
 
 		@Override
 		public Object getOrphan() {
 			throw new UnsupportedOperationException( "queued clear cannot be used with orphan delete" );
 		}
 	}
 
-	final class Put implements DelayedOperation {
+	abstract class AbstractMapValueDelayedOperation extends AbstractValueDelayedOperation {
 		private Object index;
-		private Object value;
-		private Object old;
-		
-		public Put(Object index, Object value, Object old) {
+
+		protected AbstractMapValueDelayedOperation(Object index, Object addedValue, Object orphan) {
+			super( addedValue, orphan );
 			this.index = index;
-			this.value = value;
-			this.old = old;
 		}
 
-		@Override
-		@SuppressWarnings("unchecked")
-		public void operate() {
-			map.put( index, value );
+		protected final Object getIndex() {
+			return index;
 		}
+	}
 
-		@Override
-		@SuppressWarnings("unchecked")
-		public Object getAddedInstance() {
-			return value;
+	final class Put extends AbstractMapValueDelayedOperation {
+
+		public Put(Object index, Object addedValue, Object orphan) {
+			super( index, addedValue, orphan );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
-		public Object getOrphan() {
-			return old;
+		public void operate() {
+			map.put( getIndex(), getAddedInstance() );
 		}
 	}
 
-	final class Remove implements DelayedOperation {
-		private Object index;
-		private Object old;
-		
-		public Remove(Object index, Object old) {
-			this.index = index;
-			this.old = old;
+	final class Remove extends AbstractMapValueDelayedOperation {
+
+		public Remove(Object index, Object orphan) {
+			super( index, null, orphan );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public void operate() {
-			map.remove( index );
-		}
-
-		@Override
-		public Object getAddedInstance() {
-			return null;
-		}
-
-		@Override
-		public Object getOrphan() {
-			return old;
+			map.remove( getIndex() );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSet.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSet.java
index daf903825f..cc56ae6e80 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSet.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/PersistentSet.java
@@ -1,510 +1,489 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.collection.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
+import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
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
 		super( session );
 		// Sets can be just a view of a part of another collection.
 		// do we need to copy it to be sure it won't be changing
 		// underneath us?
 		// ie. this.set.addAll(set);
 		this.set = set;
 		setInitialized();
 		setDirectlyAccessible( true );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
 		final HashMap clonedSet = new HashMap( set.size() );
 		for ( Object aSet : set ) {
 			final Object copied = persister.getElementType().deepCopy( aSet, persister.getFactory() );
 			clonedSet.put( copied, copied );
 		}
 		return clonedSet;
 	}
 
 	@Override
 	public Collection getOrphans(Serializable snapshot, String entityName) throws HibernateException {
 		final java.util.Map sn = (java.util.Map) snapshot;
 		return getOrphans( sn.keySet(), set, entityName, getSession() );
 	}
 
 	@Override
 	public boolean equalsSnapshot(CollectionPersister persister) throws HibernateException {
 		final Type elementType = persister.getElementType();
 		final java.util.Map sn = (java.util.Map) getSnapshot();
 		if ( sn.size()!=set.size() ) {
 			return false;
 		}
 		else {
 			for ( Object test : set ) {
 				final Object oldValue = sn.get( test );
 				if ( oldValue == null || elementType.isDirty( oldValue, test, getSession() ) ) {
 					return false;
 				}
 			}
 			return true;
 		}
 	}
 
 	@Override
 	public boolean isSnapshotEmpty(Serializable snapshot) {
 		return ( (java.util.Map) snapshot ).isEmpty();
 	}
 
 	@Override
 	public void beforeInitialize(CollectionPersister persister, int anticipatedSize) {
 		this.set = (Set) persister.getCollectionType().instantiate( anticipatedSize );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner)
 			throws HibernateException {
 		final Serializable[] array = (Serializable[]) disassembled;
 		final int size = array.length;
 		beforeInitialize( persister, size );
 		for ( Serializable arrayElement : array ) {
 			final Object assembledArrayElement = persister.getElementType().assemble( arrayElement, getSession(), owner );
 			if ( assembledArrayElement != null ) {
 				set.add( assembledArrayElement );
 			}
 		}
 	}
 
 	@Override
 	public boolean empty() {
 		return set.isEmpty();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public int size() {
 		return readSize() ? getCachedSize() : set.size();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean isEmpty() {
 		return readSize() ? getCachedSize()==0 : set.isEmpty();
 	}
 
 	@Override
 	public boolean contains(Object object) {
 		final Boolean exists = readElementExistence( object );
 		return exists == null
 				? set.contains( object )
 				: exists;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Iterator iterator() {
 		read();
 		return new IteratorProxy( set.iterator() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object[] toArray() {
 		read();
 		return set.toArray();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object[] toArray(Object[] array) {
 		read();
 		return set.toArray( array );
 	}
 
 	@Override
 	public boolean add(Object value) {
 		final Boolean exists = isOperationQueueEnabled() ? readElementExistence( value ) : null;
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
 		else if ( exists ) {
 			return false;
 		}
 		else {
 			queueOperation( new SimpleAdd( value ) );
 			return true;
 		}
 	}
 
 	@Override
 	public boolean remove(Object value) {
 		final Boolean exists = isPutQueueEnabled() ? readElementExistence( value ) : null;
 		if ( exists == null ) {
 			initialize( true );
 			if ( set.remove( value ) ) {
 				dirty();
 				return true;
 			}
 			else {
 				return false;
 			}
 		}
 		else if ( exists ) {
 			queueOperation( new SimpleRemove( value ) );
 			return true;
 		}
 		else {
 			return false;
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean containsAll(Collection coll) {
 		read();
 		return set.containsAll( coll );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
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
 
 	@Override
 	@SuppressWarnings("unchecked")
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
 
 	@Override
 	@SuppressWarnings("unchecked")
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
 
 	@Override
 	@SuppressWarnings("unchecked")
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
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public String toString() {
 		read();
 		return set.toString();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object readFrom(
 			ResultSet rs,
 			CollectionPersister persister,
 			CollectionAliases descriptor,
 			Object owner) throws HibernateException, SQLException {
 		final Object element = persister.readElement( rs, owner, descriptor.getSuffixedElementAliases(), getSession() );
 		if ( element != null ) {
 			tempList.add( element );
 		}
 		return element;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public void beginRead() {
 		super.beginRead();
 		tempList = new ArrayList();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean endRead() {
 		set.addAll( tempList );
 		tempList = null;
 		setInitialized();
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Iterator entries(CollectionPersister persister) {
 		return set.iterator();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Serializable disassemble(CollectionPersister persister) throws HibernateException {
 		final Serializable[] result = new Serializable[ set.size() ];
 		final Iterator itr = set.iterator();
 		int i=0;
 		while ( itr.hasNext() ) {
 			result[i++] = persister.getElementType().disassemble( itr.next(), getSession(), null );
 		}
 		return result;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Iterator getDeletes(CollectionPersister persister, boolean indexIsFormula) throws HibernateException {
 		final Type elementType = persister.getElementType();
 		final java.util.Map sn = (java.util.Map) getSnapshot();
 		final ArrayList deletes = new ArrayList( sn.size() );
 
 		Iterator itr = sn.keySet().iterator();
 		while ( itr.hasNext() ) {
 			final Object test = itr.next();
 			if ( !set.contains( test ) ) {
 				// the element has been removed from the set
 				deletes.add( test );
 			}
 		}
 
 		itr = set.iterator();
 		while ( itr.hasNext() ) {
 			final Object test = itr.next();
 			final Object oldValue = sn.get( test );
 			if ( oldValue!=null && elementType.isDirty( test, oldValue, getSession() ) ) {
 				// the element has changed
 				deletes.add( oldValue );
 			}
 		}
 
 		return deletes.iterator();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
 		final Object oldValue = ( (java.util.Map) getSnapshot() ).get( entry );
 		// note that it might be better to iterate the snapshot but this is safe,
 		// assuming the user implements equals() properly, as required by the Set
 		// contract!
 		return oldValue == null || elemType.isDirty( oldValue, entry, getSession() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean needsUpdating(Object entry, int i, Type elemType) {
 		return false;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean isRowUpdatePossible() {
 		return false;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object getIndex(Object entry, int i, CollectionPersister persister) {
 		throw new UnsupportedOperationException("Sets don't have indexes");
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object getElement(Object entry) {
 		return entry;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Object getSnapshotElement(Object entry, int i) {
 		throw new UnsupportedOperationException("Sets don't support updating by element");
 	}
 
 	@Override
 	@SuppressWarnings({"unchecked", "EqualsWhichDoesntCheckParameterClass"})
 	public boolean equals(Object other) {
 		read();
 		return set.equals( other );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public int hashCode() {
 		read();
 		return set.hashCode();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean entryExists(Object key, int i) {
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public boolean isWrapper(Object collection) {
 		return set==collection;
 	}
 
 	final class Clear implements DelayedOperation {
 		@Override
 		public void operate() {
 			set.clear();
 		}
 
 		@Override
 		public Object getAddedInstance() {
 			return null;
 		}
 
 		@Override
 		public Object getOrphan() {
 			throw new UnsupportedOperationException("queued clear cannot be used with orphan delete");
 		}
 	}
 
-	final class SimpleAdd implements DelayedOperation {
-		private Object value;
-		
-		public SimpleAdd(Object value) {
-			this.value = value;
+	final class SimpleAdd extends AbstractValueDelayedOperation {
+
+		public SimpleAdd(Object addedValue) {
+			super( addedValue, null );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public void operate() {
-			set.add( value );
-		}
-
-		@Override
-		public Object getAddedInstance() {
-			return value;
-		}
-
-		@Override
-		public Object getOrphan() {
-			return null;
+			set.add( getAddedInstance() );
 		}
 	}
 
-	final class SimpleRemove implements DelayedOperation {
-		private Object value;
-		
-		public SimpleRemove(Object value) {
-			this.value = value;
+	final class SimpleRemove extends AbstractValueDelayedOperation {
+
+		public SimpleRemove(Object orphan) {
+			super( null, orphan );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public void operate() {
-			set.remove( value );
-		}
-
-		@Override
-		public Object getAddedInstance() {
-			return null;
-		}
-
-		@Override
-		public Object getOrphan() {
-			return value;
+			set.remove( getOrphan() );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
index 818e321ca5..079fd9cca2 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
@@ -1,793 +1,795 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.SortedMap;
 import java.util.TreeMap;
 
 import org.hibernate.EntityMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
+import org.hibernate.collection.internal.AbstractPersistentCollection;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 import org.jboss.logging.Logger;
 
-import org.dom4j.Element;
-import org.dom4j.Node;
-
 /**
  * A type that handles Hibernate <tt>PersistentCollection</tt>s (including arrays).
  * 
  * @author Gavin King
  */
 public abstract class CollectionType extends AbstractType implements AssociationType {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionType.class.getName());
 
 	private static final Object NOT_NULL_COLLECTION = new MarkerObject( "NOT NULL COLLECTION" );
 	public static final Object UNFETCHED_COLLECTION = new MarkerObject( "UNFETCHED COLLECTION" );
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String role;
 	private final String foreignKeyPropertyName;
 
 	public CollectionType(TypeFactory.TypeScope typeScope, String role, String foreignKeyPropertyName) {
 		this.typeScope = typeScope;
 		this.role = role;
 		this.foreignKeyPropertyName = foreignKeyPropertyName;
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
 				if ( !li.isUninitialized() ) {
 					element = li.getImplementation();
 				}
 			}
 			if ( element == childObject ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	@Override
 	public boolean isCollectionType() {
 		return true;
 	}
 
 	@Override
 	public final boolean isEqual(Object x, Object y) {
 		return x == y
 			|| ( x instanceof PersistentCollection && ( (PersistentCollection) x ).wasInitialized() && ( (PersistentCollection) x ).isWrapper( y ) )
 			|| ( y instanceof PersistentCollection && ( (PersistentCollection) y ).wasInitialized() && ( (PersistentCollection) y ).isWrapper( x ) );
 	}
 
 	@Override
 	public int compare(Object x, Object y) {
 		return 0; // collections cannot be compared
 	}
 
 	@Override
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
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner) throws SQLException {
-		return nullSafeGet( rs, new String[] {name}, session, owner );
+		return nullSafeGet( rs, new String[] { name }, session, owner );
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String[] name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( null, session, owner );
 	}
 
 	@Override
 	public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		//NOOP
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value, int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 	}
 
 	@Override
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
 
 	@Override
 	public int getColumnSpan(Mapping session) throws MappingException {
 		return 0;
 	}
 
 	@Override
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
 
 	@Override
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return value;
 	}
 
 	@Override
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
 		return getElementsIterator( collection );
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
 
 	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	@Override
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
 
 	@Override
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
 
 	@Override
 	public boolean isDirty(Object old, Object current, SessionImplementor session)
 			throws HibernateException {
 
 		// collections don't dirty an unversioned parent entity
 
 		// TODO: I don't really like this implementation; it would be better if
 		// this was handled by searchForDirtyCollections()
 		return super.isDirty( old, current, session );
 		// return false;
 
 	}
 
 	@Override
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
 	@Override
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	@Override
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.TO_PARENT;
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
 		if ( entityEntry == null ) {
 			// This just handles a particular case of component
 			// projection, perhaps get rid of it and throw an exception
 			return null;
 		}
 		
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
 				id = keyType.semiResolve(
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
 
 	@Override
 	public Object hydrate(ResultSet rs, String[] name, SessionImplementor session, Object owner) {
 		// can't just return null here, since that would
 		// cause an owning component to become null
 		return NOT_NULL_COLLECTION;
 	}
 
 	@Override
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
 
 	@Override
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		throw new UnsupportedOperationException(
 			"collection mappings may not form part of a property-ref" );
 	}
 
 	public boolean isArrayType() {
 		return false;
 	}
 
 	@Override
 	public boolean useLHSPrimaryKey() {
 		return foreignKeyPropertyName == null;
 	}
 
 	@Override
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	@Override
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory)
 			throws MappingException {
 		return (Joinable) factory.getCollectionPersister( role );
 	}
 
 	@Override
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
 		return false;
 	}
 
 	@Override
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
 				final PersistentCollection originalPersistentCollection = (PersistentCollection) original;
 				final PersistentCollection resultPersistentCollection = (PersistentCollection) result;
 
 				preserveSnapshot( originalPersistentCollection, resultPersistentCollection, elemType, owner, copyCache, session );
 
 				if ( ! originalPersistentCollection.isDirty() ) {
 					resultPersistentCollection.clearDirty();
 				}
 			}
 		}
 
 		return result;
 	}
 
 	private void preserveSnapshot(
 			PersistentCollection original,
 			PersistentCollection result,
 			Type elemType,
 			Object owner,
 			Map copyCache,
 			SessionImplementor session) {
 		Serializable originalSnapshot = original.getStoredSnapshot();
 		Serializable resultSnapshot = result.getStoredSnapshot();
 		Serializable targetSnapshot;
 
 		if ( originalSnapshot instanceof List ) {
 			targetSnapshot = new ArrayList(
 					( (List) originalSnapshot ).size() );
 			for ( Object obj : (List) originalSnapshot ) {
 				( (List) targetSnapshot ).add( elemType.replace( obj, null, session, owner, copyCache ) );
 			}
 
 		}
 		else if ( originalSnapshot instanceof Map ) {
 			if ( originalSnapshot instanceof SortedMap ) {
 				targetSnapshot = new TreeMap( ( (SortedMap) originalSnapshot ).comparator() );
 			}
 			else {
 				targetSnapshot = new HashMap(
 						CollectionHelper.determineProperSizing( ( (Map) originalSnapshot ).size() ),
 						CollectionHelper.LOAD_FACTOR
 				);
 			}
 
 			for ( Map.Entry<Object, Object> entry : ( (Map<Object, Object>) originalSnapshot ).entrySet() ) {
 				Object key = entry.getKey();
 				Object value = entry.getValue();
 				Object resultSnapshotValue = ( resultSnapshot == null )
 						? null
 						: ( (Map<Object, Object>) resultSnapshot ).get( key );
 
 				Object newValue = elemType.replace( value, resultSnapshotValue, session, owner, copyCache );
 
 				if ( key == value ) {
 					( (Map) targetSnapshot ).put( newValue, newValue );
 
 				}
 				else {
 					( (Map) targetSnapshot ).put( key, newValue );
 				}
 
 			}
 
 		}
 		else if ( originalSnapshot instanceof Object[] ) {
 			Object[] arr = (Object[]) originalSnapshot;
 			for ( int i = 0; i < arr.length; i++ ) {
 				arr[i] = elemType.replace( arr[i], null, session, owner, copyCache );
 			}
 			targetSnapshot = originalSnapshot;
 
 		}
 		else {
 			// retain the same snapshot
 			targetSnapshot = resultSnapshot;
 
 		}
 
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( result );
 		if ( ce != null ) {
 			ce.resetStoredSnapshot( result, targetSnapshot );
 		}
 
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
 
 	@Override
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
+			if ( ( (PersistentCollection) original ).hasQueuedOperations() ) {
+				final AbstractPersistentCollection pc = (AbstractPersistentCollection) original;
+				pc.replaceQueuedOperationValues( getPersister( session ), copyCache );
+			}
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
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
 	@Override
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
 			throws MappingException {
 		return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
 	}
 
 	@Override
 	public String getOnCondition(
 			String alias,
 			SessionFactoryImplementor factory,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters, treatAsDeclarations );
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
 			
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracef( "Created collection wrapper: %s",
 						MessageHelper.collectionInfoString( persister, collection,
 								key, session ) );
 			}
 			
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
 
 	@Override
 	public String getLHSPropertyName() {
 		return foreignKeyPropertyName;
 	}
 
 	/**
 	 * We always need to dirty check the collection because we sometimes 
 	 * need to incremement version number of owner and also because of 
 	 * how assemble/disassemble is implemented for uks
 	 */
 	@Override
 	public boolean isAlwaysDirtyChecked() {
 		return true; 
 	}
 
 	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/OrderByTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/OrderByTest.java
index 0c99517b3a..8b22d32c70 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/OrderByTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/OrderByTest.java
@@ -1,524 +1,529 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.annotations.onetomany;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.Criteria;
+import org.hibernate.Hibernate;
 import org.hibernate.NullPrecedence;
 import org.hibernate.Session;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle8iDialect;
 import org.hibernate.dialect.SQLServer2008Dialect;
 import org.hibernate.dialect.SQLServer2012Dialect;
 import org.hibernate.dialect.SQLServerDialect;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.testing.RequiresDialect;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Assert;
 import org.junit.Test;
 
 /**
  * @author Emmanuel Bernard
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  * @author Brett Meyer
  */
 public class OrderByTest extends BaseCoreFunctionalTestCase {
 	@Test
 	public void testOrderByOnIdClassProperties() throws Exception {
 		Session s = openSession( );
 		s.getTransaction().begin();
 		Order o = new Order();
 		o.setAcademicYear( 2000 );
 		o.setSchoolId( "Supelec" );
 		o.setSchoolIdSort( 1 );
 		s.persist( o );
 		OrderItem oi1 = new OrderItem();
 		oi1.setAcademicYear( 2000 );
 		oi1.setDayName( "Monday" );
 		oi1.setSchoolId( "Supelec" );
 		oi1.setOrder( o );
 		oi1.setDayNo( 23 );
 		s.persist( oi1 );
 		OrderItem oi2 = new OrderItem();
 		oi2.setAcademicYear( 2000 );
 		oi2.setDayName( "Tuesday" );
 		oi2.setSchoolId( "Supelec" );
 		oi2.setOrder( o );
 		oi2.setDayNo( 30 );
 		s.persist( oi2 );
 		s.flush();
 		s.clear();
 
 		OrderID oid = new OrderID();
 		oid.setAcademicYear( 2000 );
 		oid.setSchoolId( "Supelec" );
 		o = (Order) s.get( Order.class, oid );
 		assertEquals( 30, o.getItemList().get( 0 ).getDayNo().intValue() );
 
 		s.getTransaction().rollback();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-465")
 	@RequiresDialect(value = { H2Dialect.class, MySQLDialect.class, SQLServer2008Dialect.class },
 			comment = "By default H2 places NULL values first, so testing 'NULLS LAST' expression. " +
 					"For MySQL and SQL Server 2008 testing overridden Dialect#renderOrderByElement(String, String, String, NullPrecedence) method. " +
 					"MySQL and SQLServer 2008 does not support NULLS FIRST / LAST syntax at the moment, so transforming the expression to 'CASE WHEN ...'.")
 	public void testAnnotationNullsFirstLast() {
 		Session session = openSession();
 
 		// Populating database with test data.
 		session.getTransaction().begin();
 		Tiger tiger1 = new Tiger();
 		tiger1.setName( null ); // Explicitly setting null value.
 		Tiger tiger2 = new Tiger();
 		tiger2.setName( "Max" );
 		Monkey monkey1 = new Monkey();
 		monkey1.setName( "Michael" );
 		Monkey monkey2 = new Monkey();
 		monkey2.setName( null );  // Explicitly setting null value.
 		Zoo zoo = new Zoo( "Warsaw ZOO" );
 		zoo.getTigers().add( tiger1 );
 		zoo.getTigers().add( tiger2 );
 		zoo.getMonkeys().add( monkey1 );
 		zoo.getMonkeys().add( monkey2 );
 		session.persist( zoo );
 		session.persist( tiger1 );
 		session.persist( tiger2 );
 		session.persist( monkey1 );
 		session.persist( monkey2 );
 		session.getTransaction().commit();
 
 		session.clear();
 
 		session.getTransaction().begin();
 		zoo = (Zoo) session.get( Zoo.class, zoo.getId() );
 		// Testing @org.hibernate.annotations.OrderBy.
 		Iterator<Tiger> iterator1 = zoo.getTigers().iterator();
 		Assert.assertEquals( tiger2.getName(), iterator1.next().getName() );
 		Assert.assertNull( iterator1.next().getName() );
 		// Testing @javax.persistence.OrderBy.
 		Iterator<Monkey> iterator2 = zoo.getMonkeys().iterator();
 		Assert.assertEquals( monkey1.getName(), iterator2.next().getName() );
 		Assert.assertNull( iterator2.next().getName() );
 		session.getTransaction().commit();
 
 		session.clear();
 
 		// Cleanup data.
 		session.getTransaction().begin();
 		session.delete( tiger1 );
 		session.delete( tiger2 );
 		session.delete( monkey1 );
 		session.delete( monkey2 );
 		session.delete( zoo );
 		session.getTransaction().commit();
 
 		session.close();
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-465")
 	@RequiresDialect(value = { H2Dialect.class, MySQLDialect.class, SQLServer2008Dialect.class },
 			comment = "By default H2 places NULL values first, so testing 'NULLS LAST' expression. " +
 					"For MySQL and SQL Server 2008 testing overridden Dialect#renderOrderByElement(String, String, String, NullPrecedence) method. " +
 					"MySQL and SQL Server 2008 does not support NULLS FIRST / LAST syntax at the moment, so transforming the expression to 'CASE WHEN ...'.")
 	public void testCriteriaNullsFirstLast() {
 		Session session = openSession();
 
 		// Populating database with test data.
 		session.getTransaction().begin();
 		Zoo zoo1 = new Zoo( null );
 		Zoo zoo2 = new Zoo( "Warsaw ZOO" );
 		session.persist( zoo1 );
 		session.persist( zoo2 );
 		session.getTransaction().commit();
 
 		session.clear();
 
 		session.getTransaction().begin();
 		Criteria criteria = session.createCriteria( Zoo.class );
 		criteria.addOrder( org.hibernate.criterion.Order.asc( "name" ).nulls( NullPrecedence.LAST ) );
 		Iterator<Zoo> iterator = (Iterator<Zoo>) criteria.list().iterator();
 		Assert.assertEquals( zoo2.getName(), iterator.next().getName() );
 		Assert.assertNull( iterator.next().getName() );
 		session.getTransaction().commit();
 
 		session.clear();
 
 		// Cleanup data.
 		session.getTransaction().begin();
 		session.delete( zoo1 );
 		session.delete( zoo2 );
 		session.getTransaction().commit();
 
 		session.close();
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-465")
 	@RequiresDialect(value = { H2Dialect.class, MySQLDialect.class, SQLServer2008Dialect.class },
 			comment = "By default H2 places NULL values first, so testing 'NULLS LAST' expression. " +
 					"For MySQL and SQL Server 2008 testing overridden Dialect#renderOrderByElement(String, String, String, NullPrecedence) method. " +
 					"MySQL and SQL Server 2008 does not support NULLS FIRST / LAST syntax at the moment, so transforming the expression to 'CASE WHEN ...'.")
 	public void testNullsFirstLastSpawnMultipleColumns() {
 		Session session = openSession();
 
 		// Populating database with test data.
 		session.getTransaction().begin();
 		Zoo zoo = new Zoo();
 		zoo.setName( "Berlin ZOO" );
 		Visitor visitor1 = new Visitor( null, null );
 		Visitor visitor2 = new Visitor( null, "Antoniak" );
 		Visitor visitor3 = new Visitor( "Lukasz", "Antoniak" );
 		zoo.getVisitors().add( visitor1 );
 		zoo.getVisitors().add( visitor2 );
 		zoo.getVisitors().add( visitor3 );
 		session.save( zoo );
 		session.save( visitor1 );
 		session.save( visitor2 );
 		session.save( visitor3 );
 		session.getTransaction().commit();
 
 		session.clear();
 
 		session.getTransaction().begin();
 		zoo = (Zoo) session.get( Zoo.class, zoo.getId() );
 		Iterator<Visitor> iterator = zoo.getVisitors().iterator();
 		Assert.assertEquals( 3, zoo.getVisitors().size() );
 		Assert.assertEquals( visitor3, iterator.next() );
 		Assert.assertEquals( visitor2, iterator.next() );
 		Assert.assertEquals( visitor1, iterator.next() );
 		session.getTransaction().commit();
 
 		session.clear();
 
 		// Cleanup data.
 		session.getTransaction().begin();
 		session.delete( visitor1 );
 		session.delete( visitor2 );
 		session.delete( visitor3 );
 		session.delete( zoo );
 		session.getTransaction().commit();
 
 		session.close();
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-465")
 	@RequiresDialect(value = { H2Dialect.class, MySQLDialect.class, SQLServer2008Dialect.class },
 			comment = "By default H2 places NULL values first, so testing 'NULLS LAST' expression. " +
 					"For MySQL and SQL Server 2008 testing overridden Dialect#renderOrderByElement(String, String, String, NullPrecedence) method. " +
 					"MySQL and SQL Server 2008 does not support NULLS FIRST / LAST syntax at the moment, so transforming the expression to 'CASE WHEN ...'.")
 	public void testHqlNullsFirstLast() {
 		Session session = openSession();
 
 		// Populating database with test data.
 		session.getTransaction().begin();
 		Zoo zoo1 = new Zoo();
 		zoo1.setName( null );
 		Zoo zoo2 = new Zoo();
 		zoo2.setName( "Warsaw ZOO" );
 		session.persist( zoo1 );
 		session.persist( zoo2 );
 		session.getTransaction().commit();
 
 		session.getTransaction().begin();
 		List<Zoo> orderedResults = (List<Zoo>) session.createQuery( "from Zoo z order by z.name nulls lAsT" ).list();
 		Assert.assertEquals( Arrays.asList( zoo2, zoo1 ), orderedResults );
 		session.getTransaction().commit();
 
 		session.clear();
 
 		// Cleanup data.
 		session.getTransaction().begin();
 		session.delete( zoo1 );
 		session.delete( zoo2 );
 		session.getTransaction().commit();
 
 		session.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-7608" )
 	@RequiresDialect({ H2Dialect.class, Oracle8iDialect.class })
 	public void testOrderByReferencingFormulaColumn() {
 		Session session = openSession();
 
 		// Populating database with test data.
 		session.getTransaction().begin();
 		Box box1 = new Box( 1 );
 		Item item1 = new Item( 1, "1", box1 );
 		Item item2 = new Item( 2, "22", box1 );
 		Item item3 = new Item( 3, "2", box1 );
 		session.persist( box1 );
 		session.persist( item1 );
 		session.persist( item2 );
 		session.persist( item3 );
 		session.flush();
 		session.refresh( item1 );
 		session.refresh( item2 );
 		session.refresh( item3 );
 		session.getTransaction().commit();
 
 		session.clear();
 
 		session.getTransaction().begin();
 		box1 = (Box) session.get( Box.class, box1.getId() );
 		Assert.assertEquals( Arrays.asList( item2, item1, item3 ), box1.getItems() );
 		session.getTransaction().commit();
 
 		session.clear();
 
 		// Cleanup data.
 		session.getTransaction().begin();
 		session.delete( item1 );
 		session.delete( item2 );
 		session.delete( item3 );
 		session.delete( box1 );
 		session.getTransaction().commit();
 
 		session.close();
 	}
 	
 	@Test
 	@TestForIssue(jiraKey = "HHH-5732")
 	public void testInverseIndex() {
 		final CollectionPersister transactionsPersister = sessionFactory().getCollectionPersister(
 				BankAccount.class.getName() + ".transactions" );
 		assertTrue( transactionsPersister.isInverse() );
 
 		Session s = openSession();
 		s.getTransaction().begin();
 
 		BankAccount account = new BankAccount();
 		account.addTransaction( "zzzzz" );
 		account.addTransaction( "aaaaa" );
 		account.addTransaction( "mmmmm" );
 		s.save( account );
 		s.getTransaction().commit();
 
 		s.close();
 
 		s = openSession();
 		s.getTransaction().begin();
 		
 		try {
 			final QueryableCollection queryableCollection = (QueryableCollection) transactionsPersister;
 			SimpleSelect select = new SimpleSelect( getDialect() )
 					.setTableName( queryableCollection.getTableName() )
 					.addColumn( "code" )
 					.addColumn( "transactions_index" );
 			PreparedStatement preparedStatement = ((SessionImplementor)s).getJdbcCoordinator().getStatementPreparer().prepareStatement( select.toStatementString() );
 			ResultSet resultSet = ((SessionImplementor)s).getJdbcCoordinator().getResultSetReturn().extract( preparedStatement );
 			Map<Integer, String> valueMap = new HashMap<Integer, String>();
 			while ( resultSet.next() ) {
 				final String code = resultSet.getString( 1 );
 				assertFalse( "code column was null", resultSet.wasNull() );
 				final int indx = resultSet.getInt( 2 );
 				assertFalse( "List index column was null", resultSet.wasNull() );
 				valueMap.put( indx, code );
 			}
 			assertEquals( 3, valueMap.size() );
 			assertEquals( "zzzzz", valueMap.get( 0 ) );
 			assertEquals( "aaaaa", valueMap.get( 1 ) );
 			assertEquals( "mmmmm", valueMap.get( 2 ) );
 		}
 		catch ( SQLException e ) {
 			fail(e.getMessage());
 		}
 		finally {
 			s.getTransaction().rollback();
 			s.close();
 		}
 	}
 	
 	@Test
 	@TestForIssue( jiraKey = "HHH-8083" )
 	public void testInverseIndexCascaded() {
 		final Session s = openSession();
 		s.getTransaction().begin();
 
 		Forum forum = new Forum();
 		forum.setName( "forum1" );
 		forum = (Forum) s.merge( forum );
 
 		s.flush();
 		s.clear();
 		sessionFactory().getCache().evictEntityRegions();
 
 		forum = (Forum) s.get( Forum.class, forum.getId() );
 
 		final Post post = new Post();
 		post.setName( "post1" );
 		post.setForum( forum );
 		forum.getPosts().add( post );
 
 		final User user = new User();
 		user.setName( "john" );
 		user.setForum( forum );
 		forum.getUsers().add( user );
 
 		forum = (Forum) s.merge( forum );
 
 		s.flush();
 		s.clear();
 		sessionFactory().getCache().evictEntityRegions();
 
 		forum = (Forum) s.get( Forum.class, forum.getId() );
 
 		final Post post2 = new Post();
 		post2.setName( "post2" );
 		post2.setForum( forum );
 		forum.getPosts().add( post2 );
 
 		forum = (Forum) s.merge( forum );
 
 		s.flush();
 		s.clear();
 		sessionFactory().getCache().evictEntityRegions();
 
 		forum = (Forum) s.get( Forum.class, forum.getId() );
 
 		assertEquals( 2, forum.getPosts().size() );
 		assertEquals( "post1", forum.getPosts().get( 0 ).getName() );
 		assertEquals( "post2", forum.getPosts().get( 1 ).getName() );
+		Hibernate.initialize( forum.getPosts() );
+		assertEquals( 2, forum.getPosts().size() );
 		assertEquals( 1, forum.getUsers().size() );
 		assertEquals( "john", forum.getUsers().get( 0 ).getName() );
+		Hibernate.initialize( forum.getUsers() );
+		assertEquals( 1, forum.getUsers().size() );
 	}
   
 	@Test
 	@TestForIssue(jiraKey = "HHH-8794")
 	public void testOrderByNoElement() {
 
 		final Session s = openSession();
 		s.getTransaction().begin();
 
 		Employee employee = new Employee( 1 );
 
 		Computer computer = new Computer( 1 );
 		computer.setComputerName( "Bob's computer" );
 		computer.setEmployee( employee );
 
 		Computer computer2 = new Computer( 2 );
 		computer2.setComputerName( "Alice's computer" );
 		computer2.setEmployee( employee );
 
 		s.save( employee );
 		s.save( computer2 );
 		s.save( computer );
 
 		s.flush();
 		s.clear();
 		sessionFactory().getCache().evictEntityRegions();
 
 		employee = (Employee) s.get( Employee.class, employee.getId() );
 
 		assertEquals( 2, employee.getAssets().size() );
 		assertEquals( 1, employee.getAssets().get( 0 ).getIdAsset().intValue() );
 		assertEquals( 2, employee.getAssets().get( 1 ).getIdAsset().intValue() );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-9002" )
 	public void testOrderByOneToManyWithJoinTable() {
 		A a = new A();
 		a.setName( "a" );
 		B b1 = new B();
 		b1.setName( "b1" );
 		B b2 = new B();
 		b2.setName( "b2" );
 		C c11 = new C();
 		c11.setName( "c11" );
 		C c12 = new C();
 		c12.setName( "c12" );
 		C c21 = new C();
 		c21.setName( "c21" );
 		C c22 = new C();
 		c22.setName( "c22" );
 
 		a.getBs().add( b1 );
 		a.getBs().add( b2 );
 		b1.getCs().add( c11 );
 		b1.getCs().add( c12 );
 		b2.getCs().add( c21 );
 		b2.getCs().add( c22 );
 
 		Session s = openSession();
 		s.getTransaction().begin();
 		s.persist( a );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.getTransaction().begin();
 
 		b1 =  (B) s.get( B.class, b1.getId() );
 		assertEquals( "b1", b1.getName() );
 		List<C> cs = b1.getCs();
 		assertEquals( 2, cs.size() );
 		assertEquals( "c11", cs.get( 0 ).getName() );
 		assertEquals( "c12", cs.get( 1 ).getName() );
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.getTransaction().begin();
 
 		a = (A) s.get( A.class, a.getId() );
 		assertEquals( "a", a.getName() );
 		assertEquals( 2, a.getBs().size() );
 		List<B> bs = a.getBs();
 		assertEquals( "b1", bs.get( 0 ).getName() );
 		assertEquals( "b2", bs.get( 1 ).getName() );
 		List<C> b1cs = bs.get( 0 ).getCs();
 		assertEquals( 2, b1cs.size() );
 		assertEquals( "c11", b1cs.get( 0 ).getName() );
 		assertEquals( "c12", b1cs.get( 1 ).getName() );
 		List<C> b2cs = bs.get( 1 ).getCs();
 		assertEquals( 2, b2cs.size() );
 		assertEquals( "c21", b2cs.get( 0 ).getName() );
 		assertEquals( "c22", b2cs.get( 1 ).getName() );
 
 		s.delete( a );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Order.class, OrderItem.class, Zoo.class, Tiger.class,
 				Monkey.class, Visitor.class, Box.class, Item.class,
 				BankAccount.class, Transaction.class,
 				Comment.class, Forum.class, Post.class, User.class,
 				Asset.class, Computer.class, Employee.class,
 				A.class, B.class, C.class
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cascade/MergeTest.java b/hibernate-core/src/test/java/org/hibernate/test/cascade/MergeTest.java
new file mode 100644
index 0000000000..f17aabd3ba
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/cascade/MergeTest.java
@@ -0,0 +1,134 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.cascade;
+
+import java.util.ArrayList;
+import java.util.List;
+import javax.persistence.CascadeType;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.ManyToOne;
+import javax.persistence.OneToMany;
+
+import org.junit.Test;
+
+import org.hibernate.Hibernate;
+import org.hibernate.Session;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+
+public class MergeTest extends BaseCoreFunctionalTestCase {
+
+	@Test
+	public void testMergeDetachedEntityWithNewOneToManyElements() {
+		Order order = new Order();
+
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.persist( order );
+		s.getTransaction().commit();
+		s.close();
+
+		Item item1 = new Item();
+		item1.name = "i1";
+
+		Item item2 = new Item();
+		item2.name = "i2";
+
+		order.addItem( item1 );
+		order.addItem( item2 );
+
+		s = openSession();
+		s.getTransaction().begin();
+		order = (Order) s.merge( order );
+		s.flush();
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		order = s.get( Order.class, order.id );
+		assertEquals( 2, order.items.size() );
+		s.delete( order );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	public void testMergeEntityWithNewOneToManyElements() {
+		Order order = new Order();
+
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.persist( order );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		order = s.get( Order.class, order.id );
+		Item item1 = new Item();
+		item1.name = "i1";
+		Item item2 = new Item();
+		item2.name = "i2";
+		order.addItem( item1 );
+		order.addItem( item2 );
+		assertFalse( Hibernate.isInitialized( order.items ) );
+		order = (Order) s.merge( order );
+		//s.flush();
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		order = s.get( Order.class, order.id );
+		assertEquals( 2, order.items.size() );
+		s.delete( order );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	protected Class[] getAnnotatedClasses() {
+		return new Class[] {
+				Order.class,
+				Item.class
+		};
+	}
+
+	@Entity
+	private static class Order {
+		@Id
+		@GeneratedValue
+		private Long id;
+
+		@OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
+		private List<Item> items = new ArrayList<Item>();
+
+		public Order() {
+		}
+
+		public void addItem(Item item) {
+			items.add( item );
+			item.order = this;
+		}
+	}
+
+	@Entity
+	private static class Item {
+		@Id
+		@GeneratedValue
+		private Long id;
+
+		private String name;
+
+		@ManyToOne
+		private Order order;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/bag/BagDuplicatesTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/bag/BagDuplicatesTest.java
new file mode 100644
index 0000000000..d3dcb34ec0
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/bag/BagDuplicatesTest.java
@@ -0,0 +1,230 @@
+/*
+ * Copyright 2014 JBoss Inc
+ *
+ * Licensed under the Apache License, Version 2.0 (the "License");
+ * you may not use this file except in compliance with the License.
+ * You may obtain a copy of the License at
+ *
+ *      http://www.apache.org/licenses/LICENSE-2.0
+ *
+ * Unless required by applicable law or agreed to in writing, software
+ * distributed under the License is distributed on an "AS IS" BASIS,
+ * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+ * See the License for the specific language governing permissions and
+ * limitations under the License.
+ */
+package org.hibernate.test.collection.bag;
+
+import org.hibernate.HibernateException;
+import org.hibernate.Session;
+import org.hibernate.Transaction;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.junit.Test;
+
+import javax.persistence.*;
+import java.util.ArrayList;
+import java.util.List;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.fail;
+
+/**
+ * This template demonstrates how to develop a test case for Hibernate ORM, using its built-in unit test framework.
+ * Although ORMStandaloneTestCase is perfectly acceptable as a reproducer, usage of this class is much preferred.
+ * Since we nearly always include a regression test with bug fixes, providing your reproducer using this method
+ * simplifies the process.
+ *
+ * What's even better?  Fork hibernate-orm itself, add your test case directly to a module's unit tests, then
+ * submit it as a PR!
+ */
+public class BagDuplicatesTest extends BaseCoreFunctionalTestCase {
+
+	// Add your entities here.
+	@Override
+	protected Class[] getAnnotatedClasses() {
+		return new Class[] {
+			Parent.class,
+			Child.class
+		};
+	}
+
+	// If you use *.hbm.xml mappings, instead of annotations, add the mappings here.
+	@Override
+	protected String[] getMappings() {
+		return new String[] {
+//				"Foo.hbm.xml",
+//				"Bar.hbm.xml"
+		};
+	}
+	// If those mappings reside somewhere other than resources/org/hibernate/test, change this.
+	@Override
+	protected String getBaseForMappings() {
+		return "org/hibernate/test/";
+	}
+
+	// Add in any settings that are specific to your test.  See resources/hibernate.properties for the defaults.
+	@Override
+	protected void configure(Configuration configuration) {
+		super.configure( configuration );
+
+		configuration.setProperty( AvailableSettings.SHOW_SQL, "true" );
+	}
+
+	// Add your tests, using standard JUnit.
+	@Test
+	public void HHH10385Test() throws Exception {
+		// BaseCoreFunctionalTestCase automatically creates the SessionFactory and provides the Session.
+		Session session = null;
+		Transaction transaction = null;
+
+        Long parentId = null;
+
+		try {
+            session = openSession();
+            transaction = session.beginTransaction();
+
+            Parent parent = new Parent();
+            session.persist(parent);
+            session.flush();
+            parentId = parent.getId();
+
+			transaction.commit();
+		} catch (HibernateException e) {
+            if (transaction != null) {
+                transaction.rollback();
+            }
+            fail(e.getMessage());
+		} finally {
+            if (session != null) {
+                session.close();
+            }
+        }
+
+        try {
+            session = openSession();
+            transaction = session.beginTransaction();
+
+            Parent parent = session.get(Parent.class, parentId);
+            Child child1 = new Child();
+            child1.setName("child1");
+            child1.setParent(parent);
+            parent.addChild(child1);
+            parent = (Parent) session.merge(parent);
+            session.flush();
+            //assertEquals(1, parent.getChildren().size());
+
+            transaction.commit();
+        } catch (HibernateException e) {
+            if (transaction != null) {
+                transaction.rollback();
+            }
+            fail(e.getMessage());
+        } finally {
+            if (session != null) {
+                session.close();
+            }
+        }
+
+        try {
+            session = openSession();
+            transaction = session.beginTransaction();
+
+            Parent parent = session.get(Parent.class, parentId);
+            assertEquals(1, parent.getChildren().size());
+
+            transaction.commit();
+        } catch (HibernateException e) {
+            if (transaction != null) {
+                transaction.rollback();
+            }
+            fail(e.getMessage());
+        } finally {
+            if (session != null) {
+                session.close();
+            }
+        }
+	}
+
+	@Entity(name = "Parent")
+	public static class Parent {
+
+		@Id
+		@GeneratedValue(strategy = GenerationType.AUTO)
+		private Long id;
+
+		@OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
+		private List<Child> children = new ArrayList<Child>();
+
+		public Parent() {
+		}
+
+		public Long getId() {
+			return id;
+		}
+
+		public void setId(Long id) {
+			this.id = id;
+		}
+
+		public List<Child> getChildren() {
+			return children;
+		}
+
+		public void addChild(Child child) {
+			children.add(child);
+			child.setParent(this);
+		}
+
+		public void removeChild(Child child) {
+			children.remove(child);
+			child.setParent(null);
+		}
+	}
+
+	@Entity(name = "Child")
+	public static class Child {
+
+		@Id
+		@GeneratedValue(strategy = GenerationType.AUTO)
+		private Long id;
+
+		private String name;
+
+		@ManyToOne
+		private Parent parent;
+
+		public Child() {
+		}
+
+		public Long getId() {
+			return id;
+		}
+
+		public String getName() {
+			return name;
+		}
+
+		public void setName(String name) {
+			this.name = name;
+		}
+
+		public Parent getParent() {
+			return parent;
+		}
+
+		public void setParent(Parent parent) {
+			this.parent = parent;
+		}
+
+		@Override
+		public String toString() {
+			return "Child{" +
+					"id=" + id +
+					", name='" + name + '\'' +
+					'}';
+		}
+	}
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/bag/Item.java b/hibernate-core/src/test/java/org/hibernate/test/collection/bag/Item.java
new file mode 100644
index 0000000000..3429c588aa
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/bag/Item.java
@@ -0,0 +1,37 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.collection.bag;
+
+/**
+ * @author Gail Badner
+ */
+public class Item {
+	private Long id;
+	private String name;
+	private Order order;
+
+	public Long getId() {
+		return id;
+	}
+	public void setId(Long id) {
+		this.id = id;
+	}
+
+	public String getName() {
+		return name;
+	}
+	public void setName(String name) {
+		this.name = name;
+	}
+
+	public Order getOrder() {
+		return order;
+	}
+	public void setOrder(Order order) {
+		this.order = order;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/bag/Mappings.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/collection/bag/Mappings.hbm.xml
index a412e36f77..134f3018fe 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/collection/bag/Mappings.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/bag/Mappings.hbm.xml
@@ -1,26 +1,44 @@
 <?xml version="1.0"?>
 <!--
   ~ Hibernate, Relational Persistence for Idiomatic Java
   ~
   ~ License: GNU Lesser General Public License (LGPL), version 2.1 or later.
   ~ See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
   -->
 <!DOCTYPE hibernate-mapping PUBLIC
 	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
 	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 
 
 <hibernate-mapping package="org.hibernate.test.collection.bag">
 
     <class name="BagOwner">
 		<id name="name" column="NAME" type="string" />
 
         <many-to-one name="parent" class="BagOwner" cascade="none" />
 
         <bag name="children" inverse="true" cascade="all">
             <key column="PARENT" />
             <one-to-many class="BagOwner" />
         </bag>
 	</class>
 
+    <class name="Order" table="TAuction">
+        <id name="id">
+            <generator class="native"/>
+        </id>
+        <bag name="items" inverse="true"
+             cascade="all-delete-orphan">
+            <key column="orderId"/>
+            <one-to-many class="Item"/>
+        </bag>
+    </class>
+
+    <class name="Item" table="TBid">
+        <id name="id">
+            <generator class="native"/>
+        </id>
+        <property name="name"/>
+        <many-to-one name="order" column="orderId"/>
+    </class>
 </hibernate-mapping>
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/bag/Order.java b/hibernate-core/src/test/java/org/hibernate/test/collection/bag/Order.java
new file mode 100644
index 0000000000..116390a927
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/bag/Order.java
@@ -0,0 +1,41 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.collection.bag;
+
+import java.util.ArrayList;
+import java.util.List;
+
+/**
+ * @author Gail Badner
+ */
+public class Order {
+	private Long id;
+
+	private List<Item> items = new ArrayList<Item>();
+
+	public Order() {
+	}
+
+	public Long getId() {
+		return id;
+	}
+	public void setId(Long id) {
+		this.id = id;
+	}
+
+	public List<Item> getItems() {
+		return items;
+	}
+	public void setItems(List<Item> items) {
+		this.items = items;
+	}
+
+	public void addItem(Item item) {
+		items.add( item );
+		item.setOrder( this );
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/bag/PersistentBagTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/bag/PersistentBagTest.java
index 31c015dbdc..f41a241399 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/collection/bag/PersistentBagTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/bag/PersistentBagTest.java
@@ -1,72 +1,106 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.test.collection.bag;
 
 import java.util.ArrayList;
 
 import org.hibernate.Session;
 import org.hibernate.collection.internal.PersistentBag;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
+import static org.junit.Assert.assertEquals;
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
+
+	@Test
+	public void testMergePersistentEntityWithNewOneToManyElements() {
+		Order order = new Order();
+
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.persist( order );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		order = s.get( Order.class, order.getId() );
+		Item item1 = new Item();
+		item1.setName( "i1" );
+		Item item2 = new Item();
+		item2.setName( "i2" );
+		order.addItem( item1 );
+		order.addItem( item2 );
+		order = (Order) s.merge( order );
+		//s.flush();
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		order = s.get( Order.class, order.getId() );
+		assertEquals( 2, order.getItems().size() );
+		s.delete( order );
+		s.getTransaction().commit();
+		s.close();
+	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/delayedOperation/BagDelayedOperationTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/delayedOperation/BagDelayedOperationTest.java
new file mode 100644
index 0000000000..0a18eac435
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/delayedOperation/BagDelayedOperationTest.java
@@ -0,0 +1,340 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+
+package org.hibernate.test.collection.delayedOperation;
+
+import java.util.ArrayList;
+import java.util.List;
+import javax.persistence.CascadeType;
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.GenerationType;
+import javax.persistence.Id;
+import javax.persistence.ManyToOne;
+import javax.persistence.OneToMany;
+
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
+
+import org.hibernate.Hibernate;
+import org.hibernate.Session;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+
+/**
+ * Tests delayed operations that are queued for a PersistentBag. The Bag does not have
+ * to be extra-lazy to queue the operations.
+ * @author Gail Badner
+ */
+public class BagDelayedOperationTest extends BaseCoreFunctionalTestCase {
+	private Long parentId;
+
+	@Override
+	protected Class[] getAnnotatedClasses() {
+		return new Class[] {
+				Parent.class,
+				Child.class
+		};
+	}
+
+	@Before
+	public void setup() {
+		// start by cleaning up in case a test fails
+		if ( parentId != null ) {
+			cleanup();
+		}
+
+		Parent parent = new Parent();
+		Child child1 = new Child( "Sherman" );
+		Child child2 = new Child( "Yogi" );
+		parent.addChild( child1 );
+		parent.addChild( child2 );
+
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.persist( parent );
+		s.getTransaction().commit();
+		s.close();
+
+		parentId = parent.getId();
+	}
+
+	@After
+	public void cleanup() {
+		Session s = openSession();
+		s.getTransaction().begin();
+		Parent parent = s.get( Parent.class, parentId );
+		parent.getChildren().clear();
+		s.delete( parent );
+		s.getTransaction().commit();
+		s.close();
+
+		parentId = null;
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-5855")
+	public void testSimpleAddDetached() {
+		// Create 2 detached Child objects.
+		Session s = openSession();
+		s.getTransaction().begin();
+		Child c1 = new Child( "Darwin" );
+		s.persist( c1 );
+		Child c2 = new Child( "Comet" );
+		s.persist( c2 );
+		s.getTransaction().commit();
+		s.close();
+
+		// Now Child c is detached.
+
+		s = openSession();
+		s.getTransaction().begin();
+		Parent p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// add detached Child c
+		p.addChild( c1 );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add a detached Child and commit
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 3, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add another detached Child, merge, and commit
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		p.addChild( c2 );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.merge( p );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 4, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-5855")
+	public void testSimpleAddTransient() {
+		// Add a transient Child and commit.
+		Session s = openSession();
+		s.getTransaction().begin();
+		Parent p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// add transient Child
+		p.addChild( new Child( "Darwin" ) );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 3, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add another transient Child and commit again.
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// add transient Child
+		p.addChild( new Child( "Comet" ) );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.merge( p );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 4, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-5855")
+	public void testSimpleAddManaged() {
+		// Add 2 Child entities
+		Session s = openSession();
+		s.getTransaction().begin();
+		Child c1 = new Child( "Darwin" );
+		s.persist( c1 );
+		Child c2 = new Child( "Comet" );
+		s.persist( c2 );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add a managed Child and commit
+		s = openSession();
+		s.getTransaction().begin();
+		Parent p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// get the first Child so it is managed; add to collection
+		p.addChild( s.get( Child.class, c1.getId() ) );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 3, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add the other managed Child, merge and commit.
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// get the second Child so it is managed; add to collection
+		p.addChild( s.get( Child.class, c2.getId() ) );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.merge( p );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 4, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Entity(name = "Parent")
+	public static class Parent {
+
+		@Id
+		@GeneratedValue(strategy = GenerationType.AUTO)
+		private Long id;
+
+		// Don't need extra-lazy to delay add operations to a bag.
+		@OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
+		private List<Child> children = new ArrayList<Child>();
+
+		public Parent() {
+		}
+
+		public Long getId() {
+			return id;
+		}
+
+		public void setId(Long id) {
+			this.id = id;
+		}
+
+		public List<Child> getChildren() {
+			return children;
+		}
+
+		public void addChild(Child child) {
+			children.add(child);
+			child.setParent(this);
+		}
+	}
+
+	@Entity(name = "Child")
+	public static class Child {
+
+		@Id
+		@GeneratedValue(strategy = GenerationType.AUTO)
+		private Long id;
+
+		@Column(nullable = false)
+		private String name;
+
+		@ManyToOne
+		private Parent parent;
+
+		public Child() {
+		}
+
+		public Child(String name) {
+			this.name = name;
+		}
+
+		public Long getId() {
+			return id;
+		}
+
+		public String getName() {
+			return name;
+		}
+
+		public void setName(String name) {
+			this.name = name;
+		}
+
+		public Parent getParent() {
+			return parent;
+		}
+
+		public void setParent(Parent parent) {
+			this.parent = parent;
+		}
+
+		@Override
+		public String toString() {
+			return "Child{" +
+					"id=" + id +
+					", name='" + name + '\'' +
+					'}';
+		}
+
+		@Override
+		public boolean equals(Object o) {
+			if ( this == o ) {
+				return true;
+			}
+			if ( o == null || getClass() != o.getClass() ) {
+				return false;
+			}
+
+			Child child = (Child) o;
+
+			return name.equals( child.name );
+
+		}
+
+		@Override
+		public int hashCode() {
+			return name.hashCode();
+		}
+	}
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/delayedOperation/ListDelayedOperationTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/delayedOperation/ListDelayedOperationTest.java
new file mode 100644
index 0000000000..ff768dd5da
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/delayedOperation/ListDelayedOperationTest.java
@@ -0,0 +1,465 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+
+package org.hibernate.test.collection.delayedOperation;
+
+import java.util.ArrayList;
+import java.util.List;
+import javax.persistence.CascadeType;
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.GenerationType;
+import javax.persistence.Id;
+import javax.persistence.ManyToOne;
+import javax.persistence.OneToMany;
+import javax.persistence.OrderColumn;
+
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
+
+import org.hibernate.Hibernate;
+import org.hibernate.Session;
+import org.hibernate.annotations.LazyCollection;
+import org.hibernate.annotations.LazyCollectionOption;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertTrue;
+
+/**
+ * Tests delayed operations that are queued for a PersistentSet. remove( Object )
+ * requires extra lazy to queue the operations.
+ * @author Gail Badner
+ */
+public class ListDelayedOperationTest extends BaseCoreFunctionalTestCase {
+	private Long parentId;
+	private Long childId1;
+	private Long childId2;
+
+	@Override
+	protected Class[] getAnnotatedClasses() {
+		return new Class[] {
+				Parent.class,
+				Child.class
+		};
+	}
+
+	@Before
+	public void setup() {
+		// start by cleaning up in case a test fails
+		if ( parentId != null ) {
+			cleanup();
+		}
+
+		Parent parent = new Parent();
+		Child child1 = new Child( "Sherman" );
+		Child child2 = new Child( "Yogi" );
+		parent.addChild( child1 );
+		parent.addChild( child2 );
+
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.persist( parent );
+		s.getTransaction().commit();
+		s.close();
+
+		parentId = parent.getId();
+		childId1 = child1.getId();
+		childId2 = child2.getId();
+	}
+
+	@After
+	public void cleanup() {
+		Session s = openSession();
+		s.getTransaction().begin();
+		Parent parent = s.get( Parent.class, parentId );
+		parent.getChildren().clear();
+		s.delete( parent );
+		s.getTransaction().commit();
+		s.close();
+
+		parentId = null;
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-5855")
+	public void testSimpleAddDetached() {
+		// Create 2 detached Child objects.
+		Session s = openSession();
+		s.getTransaction().begin();
+		Child c1 = new Child( "Darwin" );
+		s.persist( c1 );
+		Child c2 = new Child( "Comet" );
+		s.persist( c2 );
+		s.getTransaction().commit();
+		s.close();
+
+		// Now Child c is detached.
+
+		s = openSession();
+		s.getTransaction().begin();
+		Parent p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// add detached Child c
+		p.addChild( c1 );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add a detached Child and commit
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 3, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add another detached Child, merge, and commit
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		p.addChild( c2 );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.merge( p );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 4, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-5855")
+	public void testSimpleAddTransient() {
+		// Add a transient Child and commit.
+		Session s = openSession();
+		s.getTransaction().begin();
+		Parent p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// add transient Child
+		p.addChild( new Child( "Darwin" ) );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 3, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add another transient Child and commit again.
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// add transient Child
+		p.addChild( new Child( "Comet" ) );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.merge( p );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 4, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-5855")
+	public void testSimpleAddManaged() {
+		// Add 2 Child entities
+		Session s = openSession();
+		s.getTransaction().begin();
+		Child c1 = new Child( "Darwin" );
+		s.persist( c1 );
+		Child c2 = new Child( "Comet" );
+		s.persist( c2 );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add a managed Child and commit
+		s = openSession();
+		s.getTransaction().begin();
+		Parent p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// get the first Child so it is managed; add to collection
+		p.addChild( s.get( Child.class, c1.getId() ) );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 3, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add the other managed Child, merge and commit.
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// get the second Child so it is managed; add to collection
+		p.addChild( s.get( Child.class, c2.getId() ) );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.merge( p );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 4, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-5855")
+	public void testSimpleRemoveDetached() {
+		// Get the 2 Child entities and detach.
+		Session s = openSession();
+		s.getTransaction().begin();
+		Child c1 = s.get( Child.class, childId1 );
+		Child c2 = s.get( Child.class, childId2 );
+		s.getTransaction().commit();
+		s.close();
+
+		// Remove a detached entity element and commit
+		s = openSession();
+		s.getTransaction().begin();
+		Parent p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// remove a detached element and commit
+		Hibernate.initialize( p.getChildren() );
+		p.removeChild( c1 );
+		for ( Child c : p.getChildren() ) {
+			if ( c.equals( c1 ) ) {
+				s.evict( c );
+			}
+		}
+		assertTrue( Hibernate.isInitialized( p.getChildren() ) );
+		//s.merge( p );
+		s.getTransaction().commit();
+		s.close();
+
+		// Remove a detached entity element, merge, and commit
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		Hibernate.initialize( p.getChildren() );
+		assertEquals( 1, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+
+		// Remove a detached entity element, merge, and commit
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// remove a detached element and commit
+		p.removeChild( c2 );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		p = (Parent) s.merge( p );
+		Hibernate.initialize( p );
+		s.getTransaction().commit();
+		s.close();
+
+		// Remove a detached entity element, merge, and commit
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 0, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+/* STILL WORKING ON THIS ONE...
+	@Test
+	@TestForIssue( jiraKey = "HHH-5855")
+	public void testSimpleRemoveManaged() {
+		// Remove a managed entity element and commit
+		Session s = openSession();
+		s.getTransaction().begin();
+		Parent p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// get c1 so it is managed, then remove and commit
+		p.removeChild( s.get( Child.class, childId1 ) );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 1, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// get c1 so it is managed, then remove, merge and commit
+		p.removeChild( s.get( Child.class, childId2 ) );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.merge( p );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 0, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+	}
+*/
+
+	@Entity(name = "Parent")
+	public static class Parent {
+
+		@Id
+		@GeneratedValue(strategy = GenerationType.AUTO)
+		private Long id;
+
+		@OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
+		@LazyCollection(LazyCollectionOption.EXTRA )
+		@OrderColumn
+		private List<Child> children = new ArrayList<Child>();
+
+		public Parent() {
+		}
+
+		public Long getId() {
+			return id;
+		}
+
+		public void setId(Long id) {
+			this.id = id;
+		}
+
+		public List<Child> getChildren() {
+			return children;
+		}
+
+		public void addChild(Child child) {
+			children.add(child);
+			child.setParent(this);
+		}
+
+		public void addChild(Child child, int i) {
+			children.add(i, child );
+			child.setParent(this);
+		}
+
+		public void removeChild(Child child) {
+			children.remove(child);
+			child.setParent(null);
+		}
+	}
+
+	@Entity(name = "Child")
+	public static class Child {
+
+		@Id
+		@GeneratedValue(strategy = GenerationType.AUTO)
+		private Long id;
+
+		@Column(nullable = false)
+		private String name;
+
+		@ManyToOne
+		private Parent parent;
+
+		public Child() {
+		}
+
+		public Child(String name) {
+			this.name = name;
+		}
+
+		public Long getId() {
+			return id;
+		}
+
+		public String getName() {
+			return name;
+		}
+
+		public void setName(String name) {
+			this.name = name;
+		}
+
+		public Parent getParent() {
+			return parent;
+		}
+
+		public void setParent(Parent parent) {
+			this.parent = parent;
+		}
+
+		@Override
+		public String toString() {
+			return "Child{" +
+					"id=" + id +
+					", name='" + name + '\'' +
+					'}';
+		}
+
+		@Override
+		public boolean equals(Object o) {
+			if ( this == o ) {
+				return true;
+			}
+			if ( o == null || getClass() != o.getClass() ) {
+				return false;
+			}
+
+			Child child = (Child) o;
+
+			return name.equals( child.name );
+
+		}
+
+		@Override
+		public int hashCode() {
+			return name.hashCode();
+		}
+	}
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/collection/delayedOperation/SetDelayedOperationTest.java b/hibernate-core/src/test/java/org/hibernate/test/collection/delayedOperation/SetDelayedOperationTest.java
new file mode 100644
index 0000000000..4c7dbe5644
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/collection/delayedOperation/SetDelayedOperationTest.java
@@ -0,0 +1,448 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+
+package org.hibernate.test.collection.delayedOperation;
+
+import java.util.HashSet;
+import java.util.Set;
+import javax.persistence.CascadeType;
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.GenerationType;
+import javax.persistence.Id;
+import javax.persistence.ManyToOne;
+import javax.persistence.OneToMany;
+
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
+
+import org.hibernate.Hibernate;
+import org.hibernate.Session;
+import org.hibernate.annotations.LazyCollection;
+import org.hibernate.annotations.LazyCollectionOption;
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+
+/**
+ * Tests delayed operations that are queued for a PersistentSet. The Set must be
+ * extra lazy to queue the operations.
+ * @author Gail Badner
+ */
+public class SetDelayedOperationTest extends BaseCoreFunctionalTestCase {
+	private Long parentId;
+	private Long childId1;
+	private Long childId2;
+
+	@Override
+	protected Class[] getAnnotatedClasses() {
+		return new Class[] {
+				Parent.class,
+				Child.class
+		};
+	}
+
+	@Before
+	public void setup() {
+		// start by cleaning up in case a test fails
+		if ( parentId != null ) {
+			cleanup();
+		}
+
+		Parent parent = new Parent();
+		Child child1 = new Child( "Sherman" );
+		Child child2 = new Child( "Yogi" );
+		parent.addChild( child1 );
+		parent.addChild( child2 );
+
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.persist( parent );
+		s.getTransaction().commit();
+		s.close();
+
+		parentId = parent.getId();
+		childId1 = child1.getId();
+		childId2 = child2.getId();
+	}
+
+	@After
+	public void cleanup() {
+		Session s = openSession();
+		s.getTransaction().begin();
+		Parent parent = s.get( Parent.class, parentId );
+		parent.getChildren().clear();
+		s.delete( parent );
+		s.getTransaction().commit();
+		s.close();
+
+		parentId = null;
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-5855")
+	public void testSimpleAddDetached() {
+		// Create 2 detached Child objects.
+		Session s = openSession();
+		s.getTransaction().begin();
+		Child c1 = new Child( "Darwin" );
+		s.persist( c1 );
+		Child c2 = new Child( "Comet" );
+		s.persist( c2 );
+		s.getTransaction().commit();
+		s.close();
+
+		// Now Child c is detached.
+
+		s = openSession();
+		s.getTransaction().begin();
+		Parent p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// add detached Child c
+		p.addChild( c1 );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add a detached Child and commit
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 3, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add another detached Child, merge, and commit
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		p.addChild( c2 );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.merge( p );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 4, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-5855")
+	public void testSimpleAddTransient() {
+		// Add a transient Child and commit.
+		Session s = openSession();
+		s.getTransaction().begin();
+		Parent p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// add transient Child
+		p.addChild( new Child( "Darwin" ) );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 3, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add another transient Child and commit again.
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// add transient Child
+		p.addChild( new Child( "Comet" ) );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.merge( p );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 4, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-5855")
+	public void testSimpleAddManaged() {
+		// Add 2 Child entities
+		Session s = openSession();
+		s.getTransaction().begin();
+		Child c1 = new Child( "Darwin" );
+		s.persist( c1 );
+		Child c2 = new Child( "Comet" );
+		s.persist( c2 );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add a managed Child and commit
+		s = openSession();
+		s.getTransaction().begin();
+		Parent p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// get the first Child so it is managed; add to collection
+		p.addChild( s.get( Child.class, c1.getId() ) );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 3, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+
+		// Add the other managed Child, merge and commit.
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// get the second Child so it is managed; add to collection
+		p.addChild( s.get( Child.class, c2.getId() ) );
+		// collection should still be uninitialized
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.merge( p );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 4, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-5855")
+	public void testSimpleRemoveDetached() {
+		// Get the 2 Child entities and detach.
+		Session s = openSession();
+		s.getTransaction().begin();
+		Child c1 = s.get( Child.class, childId1 );
+		Child c2 = s.get( Child.class, childId2 );
+		s.getTransaction().commit();
+		s.close();
+
+		// Remove a detached entity element and commit
+		s = openSession();
+		s.getTransaction().begin();
+		Parent p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// remove a detached element and commit
+		p.removeChild( c1 );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.merge( p );
+		s.getTransaction().commit();
+		s.close();
+
+		// Remove a detached entity element, merge, and commit
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 1, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+
+		// Remove a detached entity element, merge, and commit
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// remove a detached element and commit
+		p.removeChild( c2 );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		p = (Parent) s.merge( p );
+		Hibernate.initialize( p );
+		s.getTransaction().commit();
+		s.close();
+
+		// Remove a detached entity element, merge, and commit
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 0, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-5855")
+	public void testSimpleRemoveManaged() {
+		// Remove a managed entity element and commit
+		Session s = openSession();
+		s.getTransaction().begin();
+		Parent p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// get c1 so it is managed, then remove and commit
+		p.removeChild( s.get( Child.class, childId1 ) );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 1, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		// get c1 so it is managed, then remove, merge and commit
+		p.removeChild( s.get( Child.class, childId2 ) );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		s.merge( p );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.getTransaction().begin();
+		p = s.get( Parent.class, parentId );
+		assertFalse( Hibernate.isInitialized( p.getChildren() ) );
+		assertEquals( 0, p.getChildren().size() );
+		s.getTransaction().commit();
+		s.close();
+	}
+
+	@Entity(name = "Parent")
+	public static class Parent {
+
+		@Id
+		@GeneratedValue(strategy = GenerationType.AUTO)
+		private Long id;
+
+		@OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
+		@LazyCollection( value = LazyCollectionOption.EXTRA)
+		private Set<Child> children = new HashSet<Child>();
+
+		public Parent() {
+		}
+
+		public Long getId() {
+			return id;
+		}
+
+		public void setId(Long id) {
+			this.id = id;
+		}
+
+		public Set<Child> getChildren() {
+			return children;
+		}
+
+		public void addChild(Child child) {
+			children.add(child);
+			child.setParent(this);
+		}
+
+		public void removeChild(Child child) {
+			children.remove(child);
+			child.setParent(null);
+		}
+	}
+
+	@Entity(name = "Child")
+	public static class Child {
+
+		@Id
+		@GeneratedValue(strategy = GenerationType.AUTO)
+		private Long id;
+
+		@Column(nullable = false)
+		private String name;
+
+		@ManyToOne
+		private Parent parent;
+
+		public Child() {
+		}
+
+		public Child(String name) {
+			this.name = name;
+		}
+
+		public Long getId() {
+			return id;
+		}
+
+		public String getName() {
+			return name;
+		}
+
+		public void setName(String name) {
+			this.name = name;
+		}
+
+		public Parent getParent() {
+			return parent;
+		}
+
+		public void setParent(Parent parent) {
+			this.parent = parent;
+		}
+
+		@Override
+		public String toString() {
+			return "Child{" +
+					"id=" + id +
+					", name='" + name + '\'' +
+					'}';
+		}
+
+		@Override
+		public boolean equals(Object o) {
+			if ( this == o ) {
+				return true;
+			}
+			if ( o == null || getClass() != o.getClass() ) {
+				return false;
+			}
+
+			Child child = (Child) o;
+
+			return name.equals( child.name );
+
+		}
+
+		@Override
+		public int hashCode() {
+			return name.hashCode();
+		}
+	}
+
+}
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/cascade/MergeTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/cascade/MergeTest.java
new file mode 100644
index 0000000000..de99bbd1e0
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/cascade/MergeTest.java
@@ -0,0 +1,131 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.jpa.test.cascade;
+
+import java.util.ArrayList;
+import java.util.List;
+import javax.persistence.CascadeType;
+import javax.persistence.Entity;
+import javax.persistence.EntityManager;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.ManyToOne;
+import javax.persistence.OneToMany;
+
+import org.junit.Test;
+
+import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
+
+import static org.junit.Assert.assertEquals;
+
+public class MergeTest extends BaseEntityManagerFunctionalTestCase {
+
+	@Test
+	public void testMergeDetachedEntityWithNewOneToManyElements() {
+		Order order = new Order();
+
+		EntityManager em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		em.persist( order );
+		em.getTransaction().commit();
+		em.close();
+
+		Item item1 = new Item();
+		item1.name = "i1";
+
+		Item item2 = new Item();
+		item2.name = "i2";
+
+		order.addItem( item1 );
+		order.addItem( item2 );
+
+		em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		order = em.merge( order );
+		em.flush();
+		em.getTransaction().commit();
+		em.close();
+
+		em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		order = em.find( Order.class, order.id );
+		assertEquals( 2, order.items.size() );
+		em.remove( order );
+		em.getTransaction().commit();
+		em.close();
+	}
+
+	@Test
+	public void testMergeLoadedEntityWithNewOneToManyElements() {
+		Order order = new Order();
+
+		EntityManager em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		em.persist( order );
+		em.getTransaction().commit();
+		em.close();
+
+		em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		order = em.find( Order.class, order.id );
+		Item item1 = new Item();
+		item1.name = "i1";
+		Item item2 = new Item();
+		item2.name = "i2";
+		order.addItem( item1 );
+		order.addItem( item2 );
+		order = em.merge( order );
+		em.flush();
+		em.getTransaction().commit();
+		em.close();
+
+		em = getOrCreateEntityManager();
+		em.getTransaction().begin();
+		order = em.find( Order.class, order.id );
+		assertEquals( 2, order.items.size() );
+		em.remove( order );
+		em.getTransaction().commit();
+		em.close();
+	}
+
+	protected Class[] getAnnotatedClasses() {
+		return new Class[] {
+				Order.class,
+				Item.class
+		};
+	}
+
+	@Entity
+	private static class Order {
+		@Id
+		@GeneratedValue
+		private Long id;
+
+		@OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
+		private List<Item> items = new ArrayList<Item>();
+
+		public Order() {
+		}
+
+		public void addItem(Item item) {
+			items.add( item );
+			item.order = this;
+		}
+	}
+
+	@Entity
+	private static class Item {
+		@Id
+		@GeneratedValue
+		private Long id;
+
+		private String name;
+
+		@ManyToOne
+		private Order order;
+	}
+}
