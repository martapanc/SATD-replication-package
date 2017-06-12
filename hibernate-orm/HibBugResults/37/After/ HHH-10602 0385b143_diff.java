diff --git a/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java b/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
index 7ef2512008..dc87b3b9df 100644
--- a/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
+++ b/hibernate-core/src/main/java/org/hibernate/collection/internal/AbstractPersistentCollection.java
@@ -1,1294 +1,1301 @@
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
 import java.util.Map;
 
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
+	private boolean isTempSession = false;
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
-		SessionImplementor originalSession = null;
-		boolean isTempSession = false;
-		boolean isJTA = false;
+		SessionImplementor tempSession = null;
 
 		if ( session == null ) {
 			if ( allowLoadOutsideTransaction ) {
-				session = openTemporarySessionForLoading();
-				isTempSession = true;
+				tempSession = openTemporarySessionForLoading();
 			}
 			else {
 				throwLazyInitializationException( "could not initialize proxy - no Session" );
 			}
 		}
 		else if ( !session.isOpen() ) {
 			if ( allowLoadOutsideTransaction ) {
-				originalSession = session;
-				session = openTemporarySessionForLoading();
-				isTempSession = true;
+				tempSession = openTemporarySessionForLoading();
 			}
 			else {
 				throwLazyInitializationException( "could not initialize proxy - the owning Session was closed" );
 			}
 		}
 		else if ( !session.isConnected() ) {
 			if ( allowLoadOutsideTransaction ) {
-				originalSession = session;
-				session = openTemporarySessionForLoading();
-				isTempSession = true;
+				tempSession = openTemporarySessionForLoading();
 			}
 			else {
 				throwLazyInitializationException( "could not initialize proxy - the owning Session is disconnected" );
 			}
 		}
 
-		if ( isTempSession ) {
+
+		SessionImplementor originalSession = null;
+		boolean isJTA = false;
+
+		if ( tempSession != null ) {
+			isTempSession = true;
+			originalSession = session;
+			session = tempSession;
+
+
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
-			if ( isTempSession ) {
+			if ( tempSession != null ) {
 				// make sure the just opened temp session gets closed!
+				isTempSession = false;
+				session = originalSession;
+
 				try {
 					if ( !isJTA ) {
-						( (Session) session ).getTransaction().commit();
+						( (Session) tempSession ).getTransaction().commit();
 					}
-					( (Session) session ).close();
+					( (Session) tempSession ).close();
 				}
 				catch (Exception e) {
 					LOG.warn( "Unable to close temporary session used to load lazy collection associated to no session" );
 				}
-				session = originalSession;
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
 	 * Replace entity instances with copy in {@code copyCache}/.
 	 *
 	 * @param copyCache - mapping from entity in the process of being
 	 *                    merged to managed copy.
 	 */
 	public final void replaceQueuedOperationValues(CollectionPersister persister, Map copyCache) {
 		for ( DelayedOperation operation : operationQueue ) {
 			if ( ValueDelayedOperation.class.isInstance( operation ) ) {
 				( (ValueDelayedOperation) operation ).replace( persister, copyCache );
 			}
 		}
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
-			this.session = null;
+			if ( !isTempSession ) {
+				this.session = null;
+			}
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
 
 	protected interface ValueDelayedOperation extends DelayedOperation {
 		void replace(CollectionPersister collectionPersister, Map copyCache);
 	}
 
 	protected abstract class AbstractValueDelayedOperation implements ValueDelayedOperation {
 		private Object addedValue;
 		private Object orphan;
 
 		protected AbstractValueDelayedOperation(Object addedValue, Object orphan) {
 			this.addedValue = addedValue;
 			this.orphan = orphan;
 		}
 
 		public void replace(CollectionPersister persister, Map copyCache) {
 			if ( addedValue != null ) {
 				addedValue = getReplacement( persister.getElementType(), addedValue, copyCache );
 			}
 		}
 
 		protected final Object getReplacement(Type type, Object current, Map copyCache) {
 			return type.replace( current, null, session, owner, copyCache );
 		}
 
 		@Override
 		public final Object getAddedInstance() {
 			return addedValue;
 		}
 
 		@Override
 		public final Object getOrphan() {
 			return orphan;
 		}
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
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
index 079fd9cca2..cdfa27c3b9 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
@@ -1,795 +1,800 @@
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
 import org.hibernate.collection.internal.AbstractPersistentCollection;
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
 		return nullSafeGet( rs, new String[] { name }, session, owner );
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
 			if ( ( (PersistentCollection) original ).hasQueuedOperations() ) {
 				final AbstractPersistentCollection pc = (AbstractPersistentCollection) original;
 				pc.replaceQueuedOperationValues( getPersister( session ), copyCache );
 			}
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
-				// create a new collection wrapper, to be initialized later
-				collection = instantiate( session, persister, key );
-				
-				collection.setOwner(owner);
-	
-				persistenceContext.addUninitializedCollection( persister, collection, key );
-	
-				// some collections are not lazy:
-				if ( initializeImmediately() ) {
-					session.initializeCollection( collection, false );
-				}
-				else if ( !persister.isLazy() ) {
-					persistenceContext.addNonLazyCollection( collection );
-				}
-	
-				if ( hasHolder() ) {
-					session.getPersistenceContext().addCollectionHolder( collection );
+
+				collection = persistenceContext.getCollection( new CollectionKey(persister, key, entityMode) );
+
+				if ( collection == null ) {
+					// create a new collection wrapper, to be initialized later
+					collection = instantiate( session, persister, key );
+
+					collection.setOwner( owner );
+
+					persistenceContext.addUninitializedCollection( persister, collection, key );
+
+					// some collections are not lazy:
+					if ( initializeImmediately() ) {
+						session.initializeCollection( collection, false );
+					}
+					else if ( !persister.isLazy() ) {
+						persistenceContext.addNonLazyCollection( collection );
+					}
+
+					if ( hasHolder() ) {
+						session.getPersistenceContext().addCollectionHolder( collection );
+					}
 				}
-				
+
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/ondemandload/cache/CacheLazyLoadNoTransTest.java b/hibernate-core/src/test/java/org/hibernate/test/ondemandload/cache/CacheLazyLoadNoTransTest.java
new file mode 100644
index 0000000000..1434c5aec2
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/ondemandload/cache/CacheLazyLoadNoTransTest.java
@@ -0,0 +1,189 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.ondemandload.cache;
+
+import java.io.Serializable;
+import java.util.ArrayList;
+import java.util.List;
+import java.util.Map;
+import javax.persistence.Cacheable;
+import javax.persistence.CascadeType;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToMany;
+import javax.persistence.ManyToOne;
+import javax.persistence.OneToMany;
+import javax.persistence.Table;
+
+import org.hibernate.Session;
+import org.hibernate.annotations.Cache;
+import org.hibernate.annotations.CacheConcurrencyStrategy;
+import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.cfg.Environment;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.persister.collection.CollectionPersister;
+
+import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
+import org.junit.Test;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertTrue;
+
+/**
+ * @author Janario Oliveira
+ */
+public class CacheLazyLoadNoTransTest extends BaseNonConfigCoreFunctionalTestCase {
+
+	@SuppressWarnings("unchecked")
+	@Override
+	protected void addSettings(Map settings) {
+		super.addSettings( settings );
+
+		settings.put( AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS, "true" );
+		settings.put( Environment.USE_SECOND_LEVEL_CACHE, "true" );
+		settings.put( Environment.USE_QUERY_CACHE, "true" );
+		settings.put( Environment.CACHE_PROVIDER_CONFIG, "true" );
+	}
+
+	@Test
+	public void testOneToMany() {
+		Customer customer = new Customer();
+		Item item1 = new Item( customer );
+		Item item2 = new Item( customer );
+		customer.boughtItems.add( item1 );
+		customer.boughtItems.add( item2 );
+		persist( customer );
+
+		//init cache
+		assertFalse( isCached( customer.id, Customer.class, "boughtItems" ) );
+		customer = find( Customer.class, customer.id );
+		assertEquals( 2, customer.boughtItems.size() );
+
+		//read from cache
+		assertTrue( isCached( customer.id, Customer.class, "boughtItems" ) );
+		customer = find( Customer.class, customer.id );
+		assertEquals( 2, customer.boughtItems.size() );
+	}
+
+	@Test
+	public void testManyToMany() {
+		Application application = new Application();
+		persist( application );
+		Customer customer = new Customer();
+		customer.applications.add( application );
+		application.customers.add( customer );
+		persist( customer );
+
+		//init cache
+		assertFalse( isCached( customer.id, Customer.class, "applications" ) );
+		assertFalse( isCached( application.id, Application.class, "customers" ) );
+
+		customer = find( Customer.class, customer.id );
+		assertEquals( 1, customer.applications.size() );
+		application = find( Application.class, application.id );
+		assertEquals( 1, application.customers.size() );
+
+		assertTrue( isCached( customer.id, Customer.class, "applications" ) );
+		assertTrue( isCached( application.id, Application.class, "customers" ) );
+
+		//read from cache
+		customer = find( Customer.class, customer.id );
+		assertEquals( 1, customer.applications.size() );
+		application = find( Application.class, application.id );
+		assertEquals( 1, application.customers.size() );
+	}
+
+	private void persist(Object entity) {
+		Session session = openSession();
+		session.beginTransaction();
+		session.persist( entity );
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	private <E> E find(Class<E> entityClass, int id) {
+		Session session;
+		session = openSession();
+		E customer = session.get( entityClass, id );
+		session.close();
+		return customer;
+	}
+
+	private boolean isCached(Serializable id, Class<?> entityClass, String attr) {
+		Session session = openSession();
+		CollectionPersister persister = sessionFactory().getCollectionPersister( entityClass.getName() + "." + attr );
+		CollectionRegionAccessStrategy cache = persister.getCacheAccessStrategy();
+		Object key = cache.generateCacheKey( id, persister, sessionFactory(), session.getTenantIdentifier() );
+		Object cachedValue = cache.get(
+				( (SessionImplementor) session ),
+				key,
+				( (SessionImplementor) session ).getTimestamp()
+		);
+		session.close();
+		return cachedValue != null;
+	}
+
+	@Override
+	protected Class[] getAnnotatedClasses() {
+		return new Class[] {Application.class, Customer.class, Item.class};
+	}
+
+	@Entity
+	@Table(name = "application")
+	@Cacheable
+	public static class Application {
+		@Id
+		@GeneratedValue
+		private Integer id;
+
+		@ManyToMany(mappedBy = "applications")
+		@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
+		private List<Customer> customers = new ArrayList<>();
+	}
+
+	@Entity
+	@Table(name = "customer")
+	@Cacheable
+	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
+	public static class Customer {
+		@Id
+		@GeneratedValue
+		private Integer id;
+
+		@ManyToMany
+		@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
+		private List<Application> applications = new ArrayList<>();
+
+		@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
+		@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
+		private List<Item> boughtItems = new ArrayList<>();
+	}
+
+	@Entity
+	@Table(name = "item")
+	@Cacheable
+	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
+	public static class Item {
+		@Id
+		@GeneratedValue
+		private Integer id;
+		@ManyToOne
+		@JoinColumn(name = "customer_id")
+		private Customer customer;
+
+		protected Item() {
+		}
+
+		public Item(Customer customer) {
+			this.customer = customer;
+		}
+	}
+}
