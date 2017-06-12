diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
index 655bed0eff..083021154c 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/CollectionUpdateAction.java
@@ -1,150 +1,150 @@
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
 
 /**
  * The action for updating a collection
  */
 public final class CollectionUpdateAction extends CollectionAction {
 	private final boolean emptySnapshot;
 
 	/**
 	 * Constructs a CollectionUpdateAction
 	 *
 	 * @param collection The collection to update
 	 * @param persister The collection persister
 	 * @param id The collection key
 	 * @param emptySnapshot Indicates if the snapshot is empty
 	 * @param session The session
 	 */
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
 		final boolean affectedByFilters = persister.isAffectedByEnabledFilters( session );
 
 		preUpdate();
 
 		if ( !collection.wasInitialized() ) {
 			if ( !collection.hasQueuedOperations() ) {
 				throw new AssertionFailure( "no queued adds" );
 			}
-			//do nothing - we only need to notify the cache...
+			//do nothing - we only need to notify the cache... 
 		}
 		else if ( !affectedByFilters && collection.empty() ) {
 			if ( !emptySnapshot ) {
 				persister.remove( id, session );
 			}
 		}
 		else if ( collection.needsRecreate( persister ) ) {
 			if ( affectedByFilters ) {
 				throw new HibernateException(
 						"cannot recreate collection while filter is enabled: " +
 								MessageHelper.collectionInfoString( persister, collection, id, session )
 				);
 			}
 			if ( !emptySnapshot ) {
 				persister.remove( id, session );
 			}
 			persister.recreate( collection, id, session );
 		}
 		else {
 			persister.deleteRows( collection, id, session );
 			persister.updateRows( collection, id, session );
 			persister.insertRows( collection, id, session );
 		}
 
 		getSession().getPersistenceContext().getCollectionEntry( collection ).afterAction( collection );
 		evict();
 		postUpdate();
 
 		if ( getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
 			getSession().getFactory().getStatisticsImplementor().updateCollection( getPersister().getRole() );
 		}
 	}
 	
 	private void preUpdate() {
 		final EventListenerGroup<PreCollectionUpdateEventListener> listenerGroup = listenerGroup( EventType.PRE_COLLECTION_UPDATE );
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
 		final EventListenerGroup<PostCollectionUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_COLLECTION_UPDATE );
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
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/QueuedOperationCollectionAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/QueuedOperationCollectionAction.java
new file mode 100644
index 0000000000..f45562b6e7
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/QueuedOperationCollectionAction.java
@@ -0,0 +1,74 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.action.internal;
+
+import java.io.Serializable;
+
+import org.hibernate.HibernateException;
+import org.hibernate.collection.spi.PersistentCollection;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.persister.collection.CollectionPersister;
+
+/**
+ * If a collection is extra lazy and has queued ops, we still need to
+ * process them.  Ex: OneToManyPersister needs to insert indexes for List
+ * collections.  See HHH-8083.
+ * 
+ * @author Brett Meyer
+ */
+public final class QueuedOperationCollectionAction extends CollectionAction {
+	
+	/**
+	 * Constructs a CollectionUpdateAction
+	 *
+	 * @param collection The collection to update
+	 * @param persister The collection persister
+	 * @param id The collection key
+	 * @param session The session
+	 */
+	public QueuedOperationCollectionAction(
+				final PersistentCollection collection,
+				final CollectionPersister persister,
+				final Serializable id,
+				final SessionImplementor session) {
+		super( persister, collection, id, session );
+	}
+
+	@Override
+	public void execute() throws HibernateException {
+		final Serializable id = getKey();
+		final SessionImplementor session = getSession();
+		final CollectionPersister persister = getPersister();
+		final PersistentCollection collection = getCollection();
+
+		persister.processQueuedOps( collection, id, session );
+	}
+}
+
+
+
+
+
+
+
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/ActionQueue.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/ActionQueue.java
index a70c2bf3d5..99f8dc6933 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/ActionQueue.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/ActionQueue.java
@@ -1,850 +1,883 @@
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
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.LinkedList;
 import java.util.List;
 import java.util.Queue;
 import java.util.Set;
 import java.util.concurrent.ConcurrentLinkedQueue;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.PropertyValueException;
 import org.hibernate.action.internal.AbstractEntityInsertAction;
 import org.hibernate.action.internal.BulkOperationCleanupAction;
 import org.hibernate.action.internal.CollectionAction;
 import org.hibernate.action.internal.CollectionRecreateAction;
 import org.hibernate.action.internal.CollectionRemoveAction;
 import org.hibernate.action.internal.CollectionUpdateAction;
 import org.hibernate.action.internal.EntityAction;
 import org.hibernate.action.internal.EntityDeleteAction;
 import org.hibernate.action.internal.EntityIdentityInsertAction;
 import org.hibernate.action.internal.EntityInsertAction;
 import org.hibernate.action.internal.EntityUpdateAction;
+import org.hibernate.action.internal.QueuedOperationCollectionAction;
 import org.hibernate.action.internal.UnresolvedEntityInsertActions;
 import org.hibernate.action.spi.AfterTransactionCompletionProcess;
 import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
 import org.hibernate.action.spi.Executable;
 import org.hibernate.cache.CacheException;
 import org.hibernate.engine.internal.NonNullableTransientDependencies;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.Type;
 
 /**
  * Responsible for maintaining the queue of actions related to events.
- * </p>
+ * 
  * The ActionQueue holds the DML operations queued as part of a session's
  * transactional-write-behind semantics.  DML operations are queued here
  * until a flush forces them to be executed against the database.
  *
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class ActionQueue {
 
 	static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ActionQueue.class.getName());
 	private static final int INIT_QUEUE_LIST_SIZE = 5;
 
 	private SessionImplementor session;
 
 	// Object insertions, updates, and deletions have list semantics because
 	// they must happen in the right order so as to respect referential
 	// integrity
 	private UnresolvedEntityInsertActions unresolvedInsertions;
 	private ArrayList insertions;
 	private ArrayList<EntityDeleteAction> deletions;
 	private ArrayList updates;
 	// Actually the semantics of the next three are really "Bag"
 	// Note that, unlike objects, collection insertions, updates,
 	// deletions are not really remembered between flushes. We
 	// just re-use the same Lists for convenience.
 	private ArrayList collectionCreations;
 	private ArrayList collectionUpdates;
+	private ArrayList collectionQueuedOps;
 	private ArrayList collectionRemovals;
 
 	private AfterTransactionCompletionProcessQueue afterTransactionProcesses;
 	private BeforeTransactionCompletionProcessQueue beforeTransactionProcesses;
 
 	/**
 	 * Constructs an action queue bound to the given session.
 	 *
 	 * @param session The session "owning" this queue.
 	 */
 	public ActionQueue(SessionImplementor session) {
 		this.session = session;
 		init();
 	}
 
 	private void init() {
 		unresolvedInsertions = new UnresolvedEntityInsertActions();
 		insertions = new ArrayList<AbstractEntityInsertAction>( INIT_QUEUE_LIST_SIZE );
 		deletions = new ArrayList<EntityDeleteAction>( INIT_QUEUE_LIST_SIZE );
 		updates = new ArrayList( INIT_QUEUE_LIST_SIZE );
 
 		collectionCreations = new ArrayList( INIT_QUEUE_LIST_SIZE );
 		collectionRemovals = new ArrayList( INIT_QUEUE_LIST_SIZE );
 		collectionUpdates = new ArrayList( INIT_QUEUE_LIST_SIZE );
+		collectionQueuedOps = new ArrayList( INIT_QUEUE_LIST_SIZE );
 
 		afterTransactionProcesses = new AfterTransactionCompletionProcessQueue( session );
 		beforeTransactionProcesses = new BeforeTransactionCompletionProcessQueue( session );
 	}
 
 	public void clear() {
 		updates.clear();
 		insertions.clear();
 		deletions.clear();
 
 		collectionCreations.clear();
 		collectionRemovals.clear();
 		collectionUpdates.clear();
+		collectionQueuedOps.clear();
 
 		unresolvedInsertions.clear();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(EntityInsertAction action) {
 		LOG.tracev( "Adding an EntityInsertAction for [{0}] object", action.getEntityName() );
 		addInsertAction( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(EntityDeleteAction action) {
 		deletions.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(EntityUpdateAction action) {
 		updates.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(CollectionRecreateAction action) {
 		collectionCreations.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(CollectionRemoveAction action) {
 		collectionRemovals.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void addAction(CollectionUpdateAction action) {
 		collectionUpdates.add( action );
 	}
 
 	@SuppressWarnings({ "unchecked" })
+	public void addAction(QueuedOperationCollectionAction action) {
+		collectionQueuedOps.add( action );
+	}
+
+	@SuppressWarnings({ "unchecked" })
 	public void addAction(EntityIdentityInsertAction insert) {
 		LOG.tracev( "Adding an EntityIdentityInsertAction for [{0}] object", insert.getEntityName() );
 		addInsertAction( insert );
 	}
 
 	private void addInsertAction(AbstractEntityInsertAction insert) {
 		if ( insert.isEarlyInsert() ) {
 			// For early inserts, must execute inserts before finding non-nullable transient entities.
 			// TODO: find out why this is necessary
 			LOG.tracev(
 					"Executing inserts before finding non-nullable transient entities for early insert: [{0}]",
 					insert
 			);
 			executeInserts();
 		}
 		NonNullableTransientDependencies nonNullableTransientDependencies = insert.findNonNullableTransientEntities();
 		if ( nonNullableTransientDependencies == null ) {
 			LOG.tracev( "Adding insert with no non-nullable, transient entities: [{0}]", insert);
 			addResolvedEntityInsertAction( insert );
 		}
 		else {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev(
 						"Adding insert with non-nullable, transient entities; insert=[{0}], dependencies=[{1}]",
 						insert,
 						nonNullableTransientDependencies.toLoggableString( insert.getSession() )
 				);
 			}
 			unresolvedInsertions.addUnresolvedEntityInsertAction( insert, nonNullableTransientDependencies );
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void addResolvedEntityInsertAction(AbstractEntityInsertAction insert) {
 		if ( insert.isEarlyInsert() ) {
 			LOG.trace( "Executing insertions before resolved early-insert" );
 			executeInserts();
 			LOG.debug( "Executing identity-insert immediately" );
 			execute( insert );
 		}
 		else {
 			LOG.trace( "Adding resolved non-early insert action." );
 			insertions.add( insert );
 		}
 		insert.makeEntityManaged();
 		for ( AbstractEntityInsertAction resolvedAction :
 				unresolvedInsertions.resolveDependentActions( insert.getInstance(), session ) ) {
 			addResolvedEntityInsertAction( resolvedAction );
 		}
 	}
 
 	/**
 	 * Are there unresolved entity insert actions that depend on non-nullable
 	 * associations with a transient entity?
 	 * @return true, if there are unresolved entity insert actions that depend on
 	 *               non-nullable associations with a transient entity;
 	 *         false, otherwise
 	 */
 	public boolean hasUnresolvedEntityInsertActions() {
 		return ! unresolvedInsertions.isEmpty();
 	}
 
 	/**
 	 * Throws {@link org.hibernate.PropertyValueException} if there are any unresolved
 	 * entity insert actions that depend on non-nullable associations with
 	 * a transient entity. This method should be called on completion of
 	 * an operation (after all cascades are completed) that saves an entity.
 	 *
 	 * @throws org.hibernate.PropertyValueException if there are any unresolved entity
 	 * insert actions; {@link org.hibernate.PropertyValueException#getEntityName()}
 	 * and {@link org.hibernate.PropertyValueException#getPropertyName()} will
 	 * return the entity name and property value for the first unresolved
 	 * entity insert action.
 	 */
 	public void checkNoUnresolvedActionsAfterOperation() throws PropertyValueException {
 		unresolvedInsertions.checkNoUnresolvedActionsAfterOperation();
 	}
 
 	public void addAction(BulkOperationCleanupAction cleanupAction) {
 		registerCleanupActions( cleanupAction );
 	}
 
 	public void registerProcess(AfterTransactionCompletionProcess process) {
 		afterTransactionProcesses.register( process );
 	}
 
 	public void registerProcess(BeforeTransactionCompletionProcess process) {
 		beforeTransactionProcesses.register( process );
 	}
 
 	/**
 	 * Perform all currently queued entity-insertion actions.
 	 *
 	 * @throws HibernateException error executing queued insertion actions.
 	 */
 	public void executeInserts() throws HibernateException {
 		executeActions( insertions );
 	}
 
 	/**
 	 * Perform all currently queued actions.
 	 *
 	 * @throws HibernateException error executing queued actions.
 	 */
 	public void executeActions() throws HibernateException {
 		if ( ! unresolvedInsertions.isEmpty() ) {
 			throw new IllegalStateException(
 					"About to execute actions, but there are unresolved entity insert actions."
 			);
 		}
 		executeActions( insertions );
 		executeActions( updates );
+		// do before actions are handled in the other collection queues
+		executeActions( collectionQueuedOps );
 		executeActions( collectionRemovals );
 		executeActions( collectionUpdates );
 		executeActions( collectionCreations );
 		executeActions( deletions );
 	}
 
 	/**
 	 * Prepares the internal action queues for execution.
 	 *
 	 * @throws HibernateException error preparing actions.
 	 */
 	public void prepareActions() throws HibernateException {
 		prepareActions( collectionRemovals );
 		prepareActions( collectionUpdates );
 		prepareActions( collectionCreations );
+		prepareActions( collectionQueuedOps );
 	}
 
 	/**
 	 * Performs cleanup of any held cache softlocks.
 	 *
 	 * @param success Was the transaction successful.
 	 */
 	public void afterTransactionCompletion(boolean success) {
 		afterTransactionProcesses.afterTransactionCompletion( success );
 	}
 
 	/**
 	 * Execute any registered {@link org.hibernate.action.spi.BeforeTransactionCompletionProcess}
 	 */
 	public void beforeTransactionCompletion() {
 		beforeTransactionProcesses.beforeTransactionCompletion();
 	}
 
 	/**
 	 * Check whether the given tables/query-spaces are to be executed against
 	 * given the currently queued actions.
 	 *
 	 * @param tables The table/query-spaces to check.
 	 *
 	 * @return True if we contain pending actions against any of the given
 	 *         tables; false otherwise.
 	 */
 	public boolean areTablesToBeUpdated(Set tables) {
 		return areTablesToUpdated( updates, tables ) ||
 				areTablesToUpdated( insertions, tables ) ||
 				areTablesToUpdated( unresolvedInsertions.getDependentEntityInsertActions(), tables ) ||
 				areTablesToUpdated( deletions, tables ) ||
 				areTablesToUpdated( collectionUpdates, tables ) ||
 				areTablesToUpdated( collectionCreations, tables ) ||
+				areTablesToUpdated( collectionQueuedOps, tables ) ||
 				areTablesToUpdated( collectionRemovals, tables );
 	}
 
 	/**
 	 * Check whether any insertion or deletion actions are currently queued.
 	 *
 	 * @return True if insertions or deletions are currently queued; false otherwise.
 	 */
 	public boolean areInsertionsOrDeletionsQueued() {
 		return ( insertions.size() > 0 || ! unresolvedInsertions.isEmpty() || deletions.size() > 0 );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private static boolean areTablesToUpdated(Iterable actions, Set tableSpaces) {
 		for ( Executable action : (Iterable<Executable>) actions ) {
 			final Serializable[] spaces = action.getPropertySpaces();
 			for ( Serializable space : spaces ) {
 				if ( tableSpaces.contains( space ) ) {
 					LOG.debugf( "Changes must be flushed to space: %s", space );
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private void executeActions(List list) throws HibernateException {
 		for ( Object aList : list ) {
 			execute( (Executable) aList );
 		}
 		list.clear();
 		session.getTransactionCoordinator().getJdbcCoordinator().executeBatch();
 	}
 
 	public void execute(Executable executable) {
 		try {
 			executable.execute();
 		}
 		finally {
 			registerCleanupActions( executable );
 		}
 	}
 
 	private void registerCleanupActions(Executable executable) {
 		beforeTransactionProcesses.register( executable.getBeforeTransactionCompletionProcess() );
 		if ( session.getFactory().getSettings().isQueryCacheEnabled() ) {
 			final String[] spaces = (String[]) executable.getPropertySpaces();
 			if ( spaces != null && spaces.length > 0 ) { //HHH-6286
 				afterTransactionProcesses.addSpacesToInvalidate( spaces );
 				session.getFactory().getUpdateTimestampsCache().preinvalidate( spaces );
 			}
 		}
 		afterTransactionProcesses.register( executable.getAfterTransactionCompletionProcess() );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private void prepareActions(List queue) throws HibernateException {
 		for ( Executable executable : (List<Executable>) queue ) {
 			executable.beforeExecutions();
 		}
 	}
 
 	/**
 	 * Returns a string representation of the object.
 	 *
 	 * @return a string representation of the object.
 	 */
 	@Override
     public String toString() {
 		return new StringBuilder()
 				.append( "ActionQueue[insertions=" ).append( insertions )
 				.append( " updates=" ).append( updates )
 				.append( " deletions=" ).append( deletions )
 				.append( " collectionCreations=" ).append( collectionCreations )
 				.append( " collectionRemovals=" ).append( collectionRemovals )
 				.append( " collectionUpdates=" ).append( collectionUpdates )
+				.append( " collectionQueuedOps=" ).append( collectionQueuedOps )
 				.append( " unresolvedInsertDependencies=" ).append( unresolvedInsertions )
 				.append( "]" )
 				.toString();
 	}
 
 	public int numberOfCollectionRemovals() {
 		return collectionRemovals.size();
 	}
 
 	public int numberOfCollectionUpdates() {
 		return collectionUpdates.size();
 	}
 
 	public int numberOfCollectionCreations() {
 		return collectionCreations.size();
 	}
 
 	public int numberOfDeletions() {
 		return deletions.size();
 	}
 
 	public int numberOfUpdates() {
 		return updates.size();
 	}
 
 	public int numberOfInsertions() {
 		return insertions.size();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void sortCollectionActions() {
 		if ( session.getFactory().getSettings().isOrderUpdatesEnabled() ) {
 			//sort the updates by fk
 			java.util.Collections.sort( collectionCreations );
 			java.util.Collections.sort( collectionUpdates );
+			java.util.Collections.sort( collectionQueuedOps );
 			java.util.Collections.sort( collectionRemovals );
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void sortActions() {
 		if ( session.getFactory().getSettings().isOrderUpdatesEnabled() ) {
 			//sort the updates by pk
 			java.util.Collections.sort( updates );
 		}
 		if ( session.getFactory().getSettings().isOrderInsertsEnabled() ) {
 			sortInsertActions();
 		}
 	}
 
 	/**
 	 * Order the {@link #insertions} queue such that we group inserts
 	 * against the same entity together (without violating constraints).  The
 	 * original order is generated by cascade order, which in turn is based on
 	 * the directionality of foreign-keys.  So even though we will be changing
 	 * the ordering here, we need to make absolutely certain that we do not
 	 * circumvent this FK ordering to the extent of causing constraint
 	 * violations
 	 */
 	private void sortInsertActions() {
 		new InsertActionSorter().sort();
 	}
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public ArrayList cloneDeletions() {
 		return ( ArrayList ) deletions.clone();
 	}
 
 	public void clearFromFlushNeededCheck(int previousCollectionRemovalSize) {
 		collectionCreations.clear();
 		collectionUpdates.clear();
+		collectionQueuedOps.clear();
 		updates.clear();
 		// collection deletions are a special case since update() can add
 		// deletions of collections not loaded by the session.
 		for ( int i = collectionRemovals.size() - 1; i >= previousCollectionRemovalSize; i-- ) {
 			collectionRemovals.remove( i );
 		}
 	}
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public boolean hasAfterTransactionActions() {
 		return afterTransactionProcesses.processes.size() > 0;
 	}
 
 	public boolean hasBeforeTransactionActions() {
 		return beforeTransactionProcesses.processes.size() > 0;
 	}
 
 	public boolean hasAnyQueuedActions() {
 		return updates.size() > 0 ||
 				insertions.size() > 0 ||
 				! unresolvedInsertions.isEmpty() ||
 				deletions.size() > 0 ||
 				collectionUpdates.size() > 0 ||
+				collectionQueuedOps.size() > 0 ||
 				collectionRemovals.size() > 0 ||
 				collectionCreations.size() > 0;
 	}
 
 	public void unScheduleDeletion(EntityEntry entry, Object rescuedEntity) {
 		for ( int i = 0; i < deletions.size(); i++ ) {
 			EntityDeleteAction action = deletions.get( i );
 			if ( action.getInstance() == rescuedEntity ) {
 				deletions.remove( i );
 				return;
 			}
 		}
 		throw new AssertionFailure( "Unable to perform un-delete for instance " + entry.getEntityName() );
 	}
 
 	/**
 	 * Used by the owning session to explicitly control serialization of the
 	 * action queue
 	 *
 	 * @param oos The stream to which the action queue should get written
 	 *
 	 * @throws IOException Indicates an error writing to the stream
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		LOG.trace( "Serializing action-queue" );
 
 		unresolvedInsertions.serialize( oos );
 
 		int queueSize = insertions.size();
 		LOG.tracev( "Starting serialization of [{0}] insertions entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( insertions.get( i ) );
 		}
 
 		queueSize = deletions.size();
 		LOG.tracev( "Starting serialization of [{0}] deletions entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( deletions.get( i ) );
 		}
 
 		queueSize = updates.size();
 		LOG.tracev( "Starting serialization of [{0}] updates entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( updates.get( i ) );
 		}
 
 		queueSize = collectionUpdates.size();
 		LOG.tracev( "Starting serialization of [{0}] collectionUpdates entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( collectionUpdates.get( i ) );
 		}
 
 		queueSize = collectionRemovals.size();
 		LOG.tracev( "Starting serialization of [{0}] collectionRemovals entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( collectionRemovals.get( i ) );
 		}
 
 		queueSize = collectionCreations.size();
 		LOG.tracev( "Starting serialization of [{0}] collectionCreations entries", queueSize );
 		oos.writeInt( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			oos.writeObject( collectionCreations.get( i ) );
 		}
+
+		queueSize = collectionQueuedOps.size();
+		LOG.tracev( "Starting serialization of [{0}] collectionQueuedOps entries", queueSize );
+		oos.writeInt( queueSize );
+		for ( int i = 0; i < queueSize; i++ ) {
+			oos.writeObject( collectionQueuedOps.get( i ) );
+		}
 	}
 
 	/**
 	 * Used by the owning session to explicitly control deserialization of the
 	 * action queue
 	 *
 	 * @param ois The stream from which to read the action queue
 	 * @param session The session to which the action queue belongs
 	 *
 	 * @return The deserialized action queue
 	 *
 	 * @throws IOException indicates a problem reading from the stream
 	 * @throws ClassNotFoundException Generally means we were unable to locate user classes.
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public static ActionQueue deserialize(
 			ObjectInputStream ois,
 			SessionImplementor session) throws IOException, ClassNotFoundException {
 		LOG.trace( "Dedeserializing action-queue" );
 		ActionQueue rtn = new ActionQueue( session );
 
 		rtn.unresolvedInsertions = UnresolvedEntityInsertActions.deserialize( ois, session );
 
 		int queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] insertions entries", queueSize );
 		rtn.insertions = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			EntityAction action = ( EntityAction ) ois.readObject();
 			action.afterDeserialize( session );
 			rtn.insertions.add( action );
 		}
 
 		queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] deletions entries", queueSize );
 		rtn.deletions = new ArrayList<EntityDeleteAction>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			EntityDeleteAction action = ( EntityDeleteAction ) ois.readObject();
 			action.afterDeserialize( session );
 			rtn.deletions.add( action );
 		}
 
 		queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] updates entries", queueSize );
 		rtn.updates = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			EntityAction action = ( EntityAction ) ois.readObject();
 			action.afterDeserialize( session );
 			rtn.updates.add( action );
 		}
 
 		queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] collectionUpdates entries", queueSize );
 		rtn.collectionUpdates = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			CollectionAction action = (CollectionAction) ois.readObject();
 			action.afterDeserialize( session );
 			rtn.collectionUpdates.add( action );
 		}
 
 		queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] collectionRemovals entries", queueSize );
 		rtn.collectionRemovals = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			CollectionAction action = ( CollectionAction ) ois.readObject();
 			action.afterDeserialize( session );
 			rtn.collectionRemovals.add( action );
 		}
 
 		queueSize = ois.readInt();
 		LOG.tracev( "Starting deserialization of [{0}] collectionCreations entries", queueSize );
 		rtn.collectionCreations = new ArrayList<Executable>( queueSize );
 		for ( int i = 0; i < queueSize; i++ ) {
 			CollectionAction action = ( CollectionAction ) ois.readObject();
 			action.afterDeserialize( session );
 			rtn.collectionCreations.add( action );
 		}
+
+		queueSize = ois.readInt();
+		LOG.tracev( "Starting deserialization of [{0}] collectionQueuedOps entries", queueSize );
+		rtn.collectionQueuedOps = new ArrayList<Executable>( queueSize );
+		for ( int i = 0; i < queueSize; i++ ) {
+			CollectionAction action = ( CollectionAction ) ois.readObject();
+			action.afterDeserialize( session );
+			rtn.collectionQueuedOps.add( action );
+		}
 		return rtn;
 	}
 
 	private static class BeforeTransactionCompletionProcessQueue {
 		private SessionImplementor session;
 		// Concurrency handling required when transaction completion process is dynamically registered
 		// inside event listener (HHH-7478).
 		private Queue<BeforeTransactionCompletionProcess> processes = new ConcurrentLinkedQueue<BeforeTransactionCompletionProcess>();
 
 		private BeforeTransactionCompletionProcessQueue(SessionImplementor session) {
 			this.session = session;
 		}
 
 		public void register(BeforeTransactionCompletionProcess process) {
 			if ( process == null ) {
 				return;
 			}
 			processes.add( process );
 		}
 
 		public void beforeTransactionCompletion() {
 			for ( BeforeTransactionCompletionProcess process : processes ) {
 				try {
 					process.doBeforeTransactionCompletion( session );
 				}
 				catch (HibernateException he) {
 					throw he;
 				}
 				catch (Exception e) {
 					throw new AssertionFailure( "Unable to perform beforeTransactionCompletion callback", e );
 				}
 			}
 			processes.clear();
 		}
 	}
 
 	private static class AfterTransactionCompletionProcessQueue {
 		private SessionImplementor session;
 		private Set<String> querySpacesToInvalidate = new HashSet<String>();
 		// Concurrency handling required when transaction completion process is dynamically registered
 		// inside event listener (HHH-7478).
 		private Queue<AfterTransactionCompletionProcess> processes = new ConcurrentLinkedQueue<AfterTransactionCompletionProcess>();
 
 		private AfterTransactionCompletionProcessQueue(SessionImplementor session) {
 			this.session = session;
 		}
 
 		public void addSpacesToInvalidate(String[] spaces) {
 			for ( String space : spaces ) {
 				addSpaceToInvalidate( space );
 			}
 		}
 
 		public void addSpaceToInvalidate(String space) {
 			querySpacesToInvalidate.add( space );
 		}
 
 		public void register(AfterTransactionCompletionProcess process) {
 			if ( process == null ) {
 				return;
 			}
 			processes.add( process );
 		}
 
 		public void afterTransactionCompletion(boolean success) {
 			for ( AfterTransactionCompletionProcess process : processes ) {
 				try {
 					process.doAfterTransactionCompletion( success, session );
 				}
 				catch ( CacheException ce ) {
 					LOG.unableToReleaseCacheLock( ce );
 					// continue loop
 				}
 				catch ( Exception e ) {
 					throw new AssertionFailure( "Exception releasing cache locks", e );
 				}
 			}
 			processes.clear();
 
 			if ( session.getFactory().getSettings().isQueryCacheEnabled() ) {
 				session.getFactory().getUpdateTimestampsCache().invalidate(
 						querySpacesToInvalidate.toArray( new String[ querySpacesToInvalidate.size()] )
 				);
 			}
 			querySpacesToInvalidate.clear();
 		}
 	}
 
 	/**
 	 * Sorts the insert actions using more hashes.
 	 *
 	 * @author Jay Erb
 	 */
 	private class InsertActionSorter {
 		// the mapping of entity names to their latest batch numbers.
 		private HashMap<String,Integer> latestBatches = new HashMap<String,Integer>();
 		private HashMap<Object,Integer> entityBatchNumber;
 
 		// the map of batch numbers to EntityInsertAction lists
 		private HashMap<Integer,List<EntityInsertAction>> actionBatches = new HashMap<Integer,List<EntityInsertAction>>();
 
 		public InsertActionSorter() {
 			//optimize the hash size to eliminate a rehash.
 			entityBatchNumber = new HashMap<Object,Integer>( insertions.size() + 1, 1.0f );
 		}
 
 		/**
 		 * Sort the insert actions.
 		 */
 		@SuppressWarnings({ "unchecked", "UnnecessaryBoxing" })
 		public void sort() {
 			// the list of entity names that indicate the batch number
 			for ( EntityInsertAction action : (List<EntityInsertAction>) insertions ) {
 				// remove the current element from insertions. It will be added back later.
 				String entityName = action.getEntityName();
 
 				// the entity associated with the current action.
 				Object currentEntity = action.getInstance();
 
 				Integer batchNumber;
 				if ( latestBatches.containsKey( entityName ) ) {
 					// There is already an existing batch for this type of entity.
 					// Check to see if the latest batch is acceptable.
 					batchNumber = findBatchNumber( action, entityName );
 				}
 				else {
 					// add an entry for this type of entity.
 					// we can be assured that all referenced entities have already
 					// been processed,
 					// so specify that this entity is with the latest batch.
 					// doing the batch number before adding the name to the list is
 					// a faster way to get an accurate number.
 
 					batchNumber = actionBatches.size();
 					latestBatches.put( entityName, batchNumber );
 				}
 				entityBatchNumber.put( currentEntity, batchNumber );
 				addToBatch( batchNumber, action );
 			}
 			insertions.clear();
 
 			// now rebuild the insertions list. There is a batch for each entry in the name list.
 			for ( int i = 0; i < actionBatches.size(); i++ ) {
 				List<EntityInsertAction> batch = actionBatches.get( i );
 				for ( EntityInsertAction action : batch ) {
 					insertions.add( action );
 				}
 			}
 		}
 
 		/**
 		 * Finds an acceptable batch for this entity to be a member as part of the {@link InsertActionSorter}
 		 *
 		 * @param action The action being sorted
 		 * @param entityName The name of the entity affected by the action
 		 *
 		 * @return An appropriate batch number; todo document this process better
 		 */
 		@SuppressWarnings({ "UnnecessaryBoxing", "unchecked" })
 		private Integer findBatchNumber(
 				EntityInsertAction action,
 				String entityName) {
 			// loop through all the associated entities and make sure they have been
 			// processed before the latest
 			// batch associated with this entity type.
 
 			// the current batch number is the latest batch for this entity type.
 			Integer latestBatchNumberForType = latestBatches.get( entityName );
 
 			// loop through all the associations of the current entity and make sure that they are processed
 			// before the current batch number
 			Object[] propertyValues = action.getState();
 			Type[] propertyTypes = action.getPersister().getClassMetadata()
 					.getPropertyTypes();
 
 			for ( int i = 0; i < propertyValues.length; i++ ) {
 				Object value = propertyValues[i];
 				Type type = propertyTypes[i];
 				if ( type.isEntityType() && value != null ) {
 					// find the batch number associated with the current association, if any.
 					Integer associationBatchNumber = entityBatchNumber.get( value );
 					if ( associationBatchNumber != null && associationBatchNumber.compareTo( latestBatchNumberForType ) > 0 ) {
 						// create a new batch for this type. The batch number is the number of current batches.
 						latestBatchNumberForType = actionBatches.size();
 						latestBatches.put( entityName, latestBatchNumberForType );
 						// since this entity will now be processed in the latest possible batch,
 						// we can be assured that it will come after all other associations,
 						// there's not need to continue checking.
 						break;
 					}
 				}
 			}
 			return latestBatchNumberForType;
 		}
 
 		@SuppressWarnings({ "unchecked" })
 		private void addToBatch(Integer batchNumber, EntityInsertAction action) {
 			List<EntityInsertAction> actions = actionBatches.get( batchNumber );
 
 			if ( actions == null ) {
 				actions = new LinkedList<EntityInsertAction>();
 				actionBatches.put( batchNumber, actions );
 			}
 			actions.add( action );
 		}
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
index 81f889b50e..1bbe6ba427 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/AbstractFlushingEventListener.java
@@ -1,381 +1,392 @@
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
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.action.internal.CollectionRecreateAction;
 import org.hibernate.action.internal.CollectionRemoveAction;
 import org.hibernate.action.internal.CollectionUpdateAction;
+import org.hibernate.action.internal.QueuedOperationCollectionAction;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.Cascade;
 import org.hibernate.engine.internal.CascadePoint;
 import org.hibernate.engine.internal.Collections;
 import org.hibernate.engine.spi.ActionQueue;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.FlushEntityEvent;
 import org.hibernate.event.spi.FlushEntityEventListener;
 import org.hibernate.event.spi.FlushEvent;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.EntityPrinter;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.internal.util.collections.LazyIterator;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * A convenience base class for listeners whose functionality results in flushing.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractFlushingEventListener implements Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, AbstractFlushingEventListener.class.getName() );
 
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
 
 		LOG.trace( "Flushing session" );
 
 		EventSource session = event.getSession();
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		session.getInterceptor().preFlush( new LazyIterator( persistenceContext.getEntitiesByKey() ) );
 
 		prepareEntityFlushes( session, persistenceContext );
 		// we could move this inside if we wanted to
 		// tolerate collection initializations during
 		// collection dirty checking:
 		prepareCollectionFlushes( persistenceContext );
 		// now, any collections that are initialized
 		// inside this block do not get updated - they
 		// are ignored until the next flush
 
 		persistenceContext.setFlushing(true);
 		try {
 			flushEntities( event, persistenceContext );
 			flushCollections( session, persistenceContext );
 		}
 		finally {
 			persistenceContext.setFlushing(false);
 		}
 
 		//some statistics
 		logFlushResults( event );
 	}
 
 	@SuppressWarnings( value = {"unchecked"} )
 	private void logFlushResults(FlushEvent event) {
 		if ( !LOG.isDebugEnabled() ) {
 			return;
 		}
 		final EventSource session = event.getSession();
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		LOG.debugf(
 				"Flushed: %s insertions, %s updates, %s deletions to %s objects",
 				session.getActionQueue().numberOfInsertions(),
 				session.getActionQueue().numberOfUpdates(),
 				session.getActionQueue().numberOfDeletions(),
 				persistenceContext.getNumberOfManagedEntities()
 		);
 		LOG.debugf(
 				"Flushed: %s (re)creations, %s updates, %s removals to %s collections",
 				session.getActionQueue().numberOfCollectionCreations(),
 				session.getActionQueue().numberOfCollectionUpdates(),
 				session.getActionQueue().numberOfCollectionRemovals(),
 				persistenceContext.getCollectionEntries().size()
 		);
 		new EntityPrinter( session.getFactory() ).toString(
 				persistenceContext.getEntitiesByKey().entrySet()
 		);
 	}
 
 	/**
 	 * process cascade save/update at the start of a flush to discover
 	 * any newly referenced entity that must be passed to saveOrUpdate(),
 	 * and also apply orphan delete
 	 */
 	private void prepareEntityFlushes(EventSource session, PersistenceContext persistenceContext) throws HibernateException {
 
 		LOG.debug( "Processing flush-time cascades" );
 
 		final Object anything = getAnything();
 		//safe from concurrent modification because of how concurrentEntries() is implemented on IdentityMap
 		for ( Map.Entry<Object,EntityEntry> me : persistenceContext.reentrantSafeEntityEntries() ) {
 //		for ( Map.Entry me : IdentityMap.concurrentEntries( persistenceContext.getEntityEntries() ) ) {
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
 			new Cascade( getCascadingAction(), CascadePoint.BEFORE_FLUSH, session ).cascade( persister, object, anything );
 		}
 		finally {
 			session.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	protected Object getAnything() { return null; }
 
 	protected CascadingAction getCascadingAction() {
 		return CascadingActions.SAVE_UPDATE;
 	}
 
 	/**
 	 * Initialize the flags of the CollectionEntry, including the
 	 * dirty check.
 	 */
 	private void prepareCollectionFlushes(PersistenceContext persistenceContext) throws HibernateException {
 
 		// Initialize dirty flags for arrays + collections with composite elements
 		// and reset reached, doupdate, etc.
 
 		LOG.debug( "Dirty checking collections" );
 
 		for ( Map.Entry<PersistentCollection,CollectionEntry> entry :
 				IdentityMap.concurrentEntries( (Map<PersistentCollection,CollectionEntry>) persistenceContext.getCollectionEntries() )) {
 			entry.getValue().preFlush( entry.getKey() );
 		}
 	}
 
 	/**
 	 * 1. detect any dirty entities
 	 * 2. schedule any entity updates
 	 * 3. search out any reachable collections
 	 */
 	private void flushEntities(final FlushEvent event, final PersistenceContext persistenceContext) throws HibernateException {
 
 		LOG.trace( "Flushing entities and processing referenced collections" );
 
 		final EventSource source = event.getSession();
 		final Iterable<FlushEntityEventListener> flushListeners = source
 				.getFactory()
 				.getServiceRegistry()
 				.getService( EventListenerRegistry.class )
 				.getEventListenerGroup( EventType.FLUSH_ENTITY )
 				.listeners();
 
 		// Among other things, updateReachables() will recursively load all
 		// collections that are moving roles. This might cause entities to
 		// be loaded.
 
 		// So this needs to be safe from concurrent modification problems.
 		// It is safe because of how IdentityMap implements entrySet()
 
 		for ( Map.Entry<Object,EntityEntry> me : persistenceContext.reentrantSafeEntityEntries() ) {
 //		for ( Map.Entry me : IdentityMap.concurrentEntries( persistenceContext.getEntityEntries() ) ) {
 
 			// Update the status of the object and if necessary, schedule an update
 
 			EntityEntry entry = (EntityEntry) me.getValue();
 			Status status = entry.getStatus();
 
 			if ( status != Status.LOADING && status != Status.GONE ) {
 				final FlushEntityEvent entityEvent = new FlushEntityEvent( source, me.getKey(), entry );
 				for ( FlushEntityEventListener listener : flushListeners ) {
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
 	private void flushCollections(final EventSource session, final PersistenceContext persistenceContext) throws HibernateException {
 
 		LOG.trace( "Processing unreferenced collections" );
 
 		for ( Map.Entry<PersistentCollection,CollectionEntry> me :
 				IdentityMap.concurrentEntries( (Map<PersistentCollection,CollectionEntry>) persistenceContext.getCollectionEntries() )) {
 			CollectionEntry ce = me.getValue();
 			if ( !ce.isReached() && !ce.isIgnore() ) {
 				Collections.processUnreachableCollection( me.getKey(), session );
 			}
 		}
 
 		// Schedule updates to collections:
 
 		LOG.trace( "Scheduling collection removes/(re)creates/updates" );
 
 		ActionQueue actionQueue = session.getActionQueue();
 		for ( Map.Entry<PersistentCollection,CollectionEntry> me :
 			IdentityMap.concurrentEntries( (Map<PersistentCollection,CollectionEntry>) persistenceContext.getCollectionEntries() )) {
 			PersistentCollection coll = me.getKey();
 			CollectionEntry ce = me.getValue();
 
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
+			if ( !coll.wasInitialized() && coll.hasQueuedOperations() ) {
+				actionQueue.addAction(
+						new QueuedOperationCollectionAction(
+								coll,
+								ce.getLoadedPersister(),
+								ce.getLoadedKey(),
+								session
+							)
+					);
+			}
 
 		}
 
 		actionQueue.sortCollectionActions();
 
 	}
 
 	/**
 	 * Execute all SQL (and second-level cache updates) in a special order so that foreign-key constraints cannot
 	 * be violated: <ol>
 	 * <li> Inserts, in the order they were performed
 	 * <li> Updates
 	 * <li> Deletion of collection elements
 	 * <li> Insertion of collection elements
 	 * <li> Deletes, in the order they were performed
 	 * </ol>
 	 *
 	 * @param session The session being flushed
 	 */
 	protected void performExecutions(EventSource session) {
 		LOG.trace( "Executing flush" );
 
 		// IMPL NOTE : here we alter the flushing flag of the persistence context to allow
 		//		during-flush callbacks more leniency in regards to initializing proxies and
 		//		lazy collections during their processing.
 		// For more information, see HHH-2763
 		try {
 			session.getTransactionCoordinator().getJdbcCoordinator().flushBeginning();
 			session.getPersistenceContext().setFlushing( true );
 			// we need to lock the collection caches before executing entity inserts/updates in order to
 			// account for bi-directional associations
 			session.getActionQueue().prepareActions();
 			session.getActionQueue().executeActions();
 		}
 		finally {
 			session.getPersistenceContext().setFlushing( false );
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
 
 		LOG.trace( "Post flush" );
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		persistenceContext.getCollectionsByKey().clear();
 		
 		// the database has changed now, so the subselect results need to be invalidated
 		// the batch fetching queues should also be cleared - especially the collection batch fetching one
 		persistenceContext.getBatchFetchQueue().clear();
 
 		for ( Map.Entry<PersistentCollection, CollectionEntry> me : IdentityMap.concurrentEntries( persistenceContext.getCollectionEntries() ) ) {
 			CollectionEntry collectionEntry = me.getValue();
 			PersistentCollection persistentCollection = me.getKey();
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
 						collectionEntry.getLoadedKey()
 				);
 				persistenceContext.getCollectionsByKey().put(collectionKey, persistentCollection);
 			}
 		}
 
 		session.getInterceptor().postFlush( new LazyIterator( persistenceContext.getEntitiesByKey() ) );
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 160057e76c..40b4eac66c 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -645,1406 +645,1416 @@ public abstract class AbstractCollectionPersister
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
 						expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
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
 						session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 
 	protected BasicBatchKey recreateBatchKey;
 
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Inserting collection: %s",
 						MessageHelper.collectionInfoString( this, collection, id, session ) );
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
 									expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
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
 									session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 								MessageHelper.collectionInfoString( this, collection, id, session ),
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
 						MessageHelper.collectionInfoString( this, collection, id, session ) );
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
 								expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
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
 								session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 								MessageHelper.collectionInfoString( this, collection, id, session ),
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
 					MessageHelper.collectionInfoString( this, collection, id, session ) );
 
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
 								expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
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
 								session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 								MessageHelper.collectionInfoString( this, collection, id, session ),
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
+	
+	public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
+			throws HibernateException {
+		if ( collection.hasQueuedOperations() ) {
+			doProcessQueuedOps( collection, key, session );
+		}
+	}
+	
+	protected abstract void doProcessQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
+			throws HibernateException;
 
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
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next() ? rs.getInt( 1 ) - baseIndex : 0;
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next();
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			catch ( TransientObjectException e ) {
 				return false;
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					if ( rs.next() ) {
 						return getElementType().nullSafeGet( rs, elementColumnAliases, session, owner );
 					}
 					else {
 						return null;
 					}
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 
 	public int getBatchSize() {
 		return batchSize;
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
 
 	// ColectionDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public CollectionPersister getCollectionPersister() {
 		return this;
 	}
 
 	@Override
 	public CollectionIndexDefinition getIndexDefinition() {
 		if ( ! hasIndex() ) {
 			return null;
 		}
 
 		return new CollectionIndexDefinition() {
 			@Override
 			public CollectionDefinition getCollectionDefinition() {
 				return AbstractCollectionPersister.this;
 			}
 
 			@Override
 			public Type getType() {
 				return getIndexType();
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
 				if ( getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat composite collection index type as entity" );
 				}
 				return (EntityPersister) ( (AssociationType) getIndexType() ).getAssociatedJoinable( getFactory() );
 			}
 
 			@Override
 			public CompositionDefinition toCompositeDefinition() {
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat entity collection index type as composite" );
 				}
 				// todo : implement
 				throw new NotYetImplementedException();
 			}
 		};
 	}
 
 	@Override
 	public CollectionElementDefinition getElementDefinition() {
 		return new CollectionElementDefinition() {
 			@Override
 			public CollectionDefinition getCollectionDefinition() {
 				return AbstractCollectionPersister.this;
 			}
 
 			@Override
 			public Type getType() {
 				return getElementType();
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
 				if ( getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat composite collection element type as entity" );
 				}
 				return getElementPersister();
 			}
 
 			@Override
 			public CompositeCollectionElementDefinition toCompositeElementDefinition() {
 
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat entity collection element type as composite" );
 				}
 
 				return new CompositeCollectionElementDefinition() {
 					@Override
 					public String getName() {
 						return "";
 					}
 
 					@Override
 					public Type getType() {
 						return getElementType();
 					}
 
 					@Override
 					public AttributeSource getSource() {
 						// TODO: what if this is a collection w/in an encapsulated composition attribute?
 						// should return the encapsulated composition attribute instead???
 						return getOwnerEntityPersister();
 					}
 
 					@Override
 					public Iterable<AttributeDefinition> getAttributes() {
 						return CompositionSingularSubAttributesHelper.getCompositeCollectionElementSubAttributes( this );
 					}
 
 					@Override
 					public CollectionDefinition getCollectionDefinition() {
 						return AbstractCollectionPersister.this;
 					}
 				};
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
index b9250a0b08..4778bb733e 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/BasicCollectionPersister.java
@@ -1,366 +1,372 @@
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
 import org.hibernate.loader.collection.BatchingCollectionInitializerBuilder;
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
+	
+	@Override
+	protected void doProcessQueuedOps(PersistentCollection collection, Serializable id, SessionImplementor session)
+			throws HibernateException {
+		// nothing to do
+	}
 
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
 							expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
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
 							session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 					"could not update collection rows: " + MessageHelper.collectionInfoString( this, collection, id, session ),
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
 		return BatchingCollectionInitializerBuilder.getBuilder( getFactory() )
 				.createBatchingCollectionInitializer( this, batchSize, getFactory(), loadQueryInfluencers );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
index ecfe83c7d8..0bbd9aa78c 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/CollectionPersister.java
@@ -1,312 +1,322 @@
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
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
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
  * @see org.hibernate.collection.spi.PersistentCollection
  * @author Gavin King
  */
 public interface CollectionPersister extends CollectionDefinition {
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
+	
+	/**
+	 * Process queued operations within the PersistentCollection.
+	 */
+	public void processQueuedOps(
+			PersistentCollection collection,
+			Serializable key,
+			SessionImplementor session)
+			throws HibernateException;
+	
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
 	public int getBatchSize();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
index 6cec2e9259..ed4046f38b 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/OneToManyPersister.java
@@ -1,527 +1,532 @@
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
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.loader.collection.BatchingCollectionInitializerBuilder;
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
  * @author Brett Meyer
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
 	 * Generate the SQL UPDATE that inserts a collection index
 	 */
 	@Override
     protected String generateUpdateRowString() {
 		Update update = new Update( getDialect() ).setTableName( qualifiedTableName );
 		update.addPrimaryKeyColumns( elementColumnNames, elementColumnIsSettable, elementColumnWriters );
 		if ( hasIdentifier ) {
 			update.addPrimaryKeyColumns( new String[]{ identifierColumnName } );
 		}
 		if ( hasIndex && !indexContainsFormula ) {
 			update.addColumns( indexColumnNames );
 		}
 		
 		return update.toStatementString();
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
 	
 	@Override
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 		super.recreate( collection, id, session );
-		writeIndex( collection, id, session );
+		writeIndex( collection, collection.entries( this ), id, session );
 	}
 	
 	@Override
 	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 		super.insertRows( collection, id, session );
-		writeIndex( collection, id, session );
+		writeIndex( collection, collection.entries( this ), id, session );
 	}
 	
-	private void writeIndex(PersistentCollection collection, Serializable id, SessionImplementor session) {
+	@Override
+	protected void doProcessQueuedOps(PersistentCollection collection, Serializable id, SessionImplementor session)
+			throws HibernateException {
+		writeIndex( collection, collection.queuedAdditionIterator(), id, session );
+	}
+	
+	private void writeIndex(PersistentCollection collection, Iterator entries, Serializable id, SessionImplementor session) {
 		// If one-to-many and inverse, still need to create the index.  See HHH-5732.
 		if ( isInverse && hasIndex && !indexContainsFormula ) {
 			try {
-				Iterator entries = collection.entries( this );
 				if ( entries.hasNext() ) {
 					Expectation expectation = Expectations.appropriateExpectation( getUpdateCheckStyle() );
 					int i = 0;
 					int count = 0;
 					while ( entries.hasNext() ) {
 
 						final Object entry = entries.next();
 						if ( collection.entryExists( entry, i ) ) {
 							int offset = 1;
 							PreparedStatement st = null;
 							boolean callable = isUpdateCallable();
 							boolean useBatch = expectation.canBeBatched();
 							String sql = getSQLUpdateRowString();
 
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
 								if ( hasIdentifier ) {
 									offset = writeIdentifier( st, collection.getIdentifier( entry, i ), offset, session );
 								}
 								offset = writeIndex( st, collection.getIndex( entry, i, this ), offset, session );
 								offset = writeElement( st, collection.getElement( entry ), offset, session );
 
 								if ( useBatch ) {
 									session.getTransactionCoordinator()
 											.getJdbcCoordinator()
 											.getBatch( recreateBatchKey )
 											.addToBatch();
 								}
 								else {
 									expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
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
 									session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 								}
 							}
 
 						}
 						i++;
 					}
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not update collection: " +
 								MessageHelper.collectionInfoString( this, collection, id, session ),
 						getSQLUpdateRowString()
 						);
 			}
 		}
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
 								deleteExpectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
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
 						session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 								insertExpectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
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
 						session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 					}
 				}
 			}
 
 			return count;
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not update collection rows: " + 
 					MessageHelper.collectionInfoString( this, collection, id, session ),
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
 		return BatchingCollectionInitializerBuilder.getBuilder( getFactory() )
 				.createBatchingOneToManyInitializer( this, batchSize, getFactory(), loadQueryInfluencers );
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/Comment.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/Comment.java
new file mode 100644
index 0000000000..ee9e1ef1fb
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/Comment.java
@@ -0,0 +1,67 @@
+package org.hibernate.test.annotations.onetomany;
+
+import javax.persistence.Column;
+import javax.persistence.DiscriminatorColumn;
+import javax.persistence.DiscriminatorType;
+import javax.persistence.DiscriminatorValue;
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.GeneratedValue;
+import javax.persistence.GenerationType;
+import javax.persistence.Id;
+import javax.persistence.Inheritance;
+import javax.persistence.InheritanceType;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+
+@Entity(name="Comment")
+@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
+@DiscriminatorColumn(name = "DTYPE", discriminatorType= DiscriminatorType.STRING, length = 3)
+@DiscriminatorValue(value = "WPT")
+public class Comment {
+	
+	private Long id;
+	private Post post;
+	private String name;
+	private Forum forum;
+	
+	@Id
+	@GeneratedValue(strategy = GenerationType.AUTO)
+	@Column(name = "id", updatable = false, insertable = false)
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+	
+	@ManyToOne(optional=true,fetch=FetchType.LAZY)
+	@JoinColumn(name="FK_PostId", nullable=true, insertable=true,updatable=false)
+	public Post getPost() {
+		return post;
+	}
+
+	public void setPost(Post family) {
+		this.post = family;
+	}
+
+	@ManyToOne(optional=true,fetch=FetchType.LAZY)
+	@JoinColumn(name="FK_ForumId", nullable=true, insertable=true,updatable=false)
+	public Forum getForum() {
+		return forum;
+	}
+
+	public void setForum(Forum forum) {
+		this.forum = forum;
+	}
+
+	@Column
+	public String getName() {
+		return name;
+	}
+
+	public void setName(String name) {
+		this.name = name;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/Forum.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/Forum.java
new file mode 100644
index 0000000000..b0aefbe57f
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/Forum.java
@@ -0,0 +1,68 @@
+package org.hibernate.test.annotations.onetomany;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import javax.persistence.CascadeType;
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.GeneratedValue;
+import javax.persistence.GenerationType;
+import javax.persistence.Id;
+import javax.persistence.OneToMany;
+import javax.persistence.OrderColumn;
+
+import org.hibernate.annotations.LazyCollection;
+import org.hibernate.annotations.LazyCollectionOption;
+
+@Entity(name="Forum")
+public class Forum{
+
+	private Long id;
+	private String name;
+	protected List<Comment> posts = new ArrayList<Comment>();
+	protected List<User> users = new ArrayList<User>();
+	
+	@Id
+	@GeneratedValue(strategy = GenerationType.AUTO)
+	@Column(name = "id", updatable = false, insertable = false)
+	public Long getId() {
+		return id;
+	}
+
+	public void setId(Long id) {
+		this.id = id;
+	}
+	
+	@OneToMany(mappedBy = "forum", cascade = CascadeType.ALL , orphanRemoval = false, fetch = FetchType.LAZY)
+	@LazyCollection(LazyCollectionOption.EXTRA)
+	@OrderColumn(name = "idx2")
+	public List<Comment> getPosts() {
+		return posts;
+	}
+
+	public void setPosts(List<Comment> children) {
+		this.posts = children;
+	}
+	
+	@OneToMany(mappedBy = "forum", cascade = CascadeType.ALL , orphanRemoval = true, fetch = FetchType.LAZY)
+	@LazyCollection(LazyCollectionOption.EXTRA)
+	@OrderColumn(name = "idx3")
+	public List<User> getUsers() {
+		return users;
+	}
+
+	public void setUsers(List<User> users) {
+		this.users = users;
+	}
+
+	@Column
+	public String getName() {
+		return name;
+	}
+
+	public void setName(String name) {
+		this.name = name;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/OrderByTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/OrderByTest.java
index 4f6366a307..15aa425284 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/OrderByTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/OrderByTest.java
@@ -1,377 +1,418 @@
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
 import org.hibernate.NullPrecedence;
 import org.hibernate.Session;
 import org.hibernate.dialect.H2Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.Oracle8iDialect;
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
 	@RequiresDialect(value = { H2Dialect.class, MySQLDialect.class },
 			comment = "By default H2 places NULL values first, so testing 'NULLS LAST' expression. " +
 					"For MySQL testing overridden Dialect#renderOrderByElement(String, String, String, NullPrecedence) method. " +
 					"MySQL does not support NULLS FIRST / LAST syntax at the moment, so transforming the expression to 'CASE WHEN ...'.")
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
 	@RequiresDialect(value = { H2Dialect.class, MySQLDialect.class },
 			comment = "By default H2 places NULL values first, so testing 'NULLS LAST' expression. " +
 					"For MySQL testing overridden Dialect#renderOrderByElement(String, String, String, NullPrecedence) method. " +
 					"MySQL does not support NULLS FIRST / LAST syntax at the moment, so transforming the expression to 'CASE WHEN ...'.")
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
 	@RequiresDialect(value = { H2Dialect.class, MySQLDialect.class },
 			comment = "By default H2 places NULL values first, so testing 'NULLS LAST' expression. " +
 					"For MySQL testing overridden Dialect#renderOrderByElement(String, String, String, NullPrecedence) method. " +
 					"MySQL does not support NULLS FIRST / LAST syntax at the moment, so transforming the expression to 'CASE WHEN ...'.")
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
 	@RequiresDialect(value = { H2Dialect.class, MySQLDialect.class },
 			comment = "By default H2 places NULL values first, so testing 'NULLS LAST' expression. " +
 					"For MySQL testing overridden Dialect#renderOrderByElement(String, String, String, NullPrecedence) method. " +
 					"MySQL does not support NULLS FIRST / LAST syntax at the moment, so transforming the expression to 'CASE WHEN ...'.")
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
 			PreparedStatement preparedStatement = ((SessionImplementor)s).getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareStatement( select.toStatementString() );
 			ResultSet resultSet = ((SessionImplementor)s).getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( preparedStatement );
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
 	}
+	
+	@Test
+	@TestForIssue( jiraKey = "HHH-8083" )
+	public void testInverseIndexCascaded() {
+		final Session s = openSession();
+		s.getTransaction().begin();
+
+		Forum forum = new Forum();
+		forum.setName( "forum1" );
+		forum = (Forum) s.merge( forum );
+
+		s.flush();
+		s.clear();
+		sessionFactory().getCache().evictEntityRegions();
+
+		forum = (Forum) s.get( Forum.class, forum.getId() );
+
+		final Post post = new Post();
+		post.setName( "post1" );
+		post.setForum( forum );
+		forum.getPosts().add( post );
+
+		final User user = new User();
+		user.setName( "john" );
+		user.setForum( forum );
+		forum.getUsers().add( user );
+
+		forum = (Forum) s.merge( forum );
+
+		s.flush();
+		s.clear();
+		sessionFactory().getCache().evictEntityRegions();
+
+		forum = (Forum) s.get( Forum.class, forum.getId() );
+
+		assertEquals( 1, forum.getPosts().size() );
+		assertEquals( "post1", forum.getPosts().get( 0 ).getName() );
+		assertEquals( 1, forum.getUsers().size() );
+		assertEquals( "john", forum.getUsers().get( 0 ).getName() );
+	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Order.class, OrderItem.class, Zoo.class, Tiger.class,
 				Monkey.class, Visitor.class, Box.class, Item.class,
-				BankAccount.class, Transaction.class
+				BankAccount.class, Transaction.class,
+				Comment.class, Forum.class, Post.class, User.class
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/Post.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/Post.java
new file mode 100644
index 0000000000..1a22d8ae4c
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/Post.java
@@ -0,0 +1,32 @@
+package org.hibernate.test.annotations.onetomany;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import javax.persistence.CascadeType;
+import javax.persistence.DiscriminatorValue;
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.OneToMany;
+import javax.persistence.OrderColumn;
+
+import org.hibernate.annotations.LazyCollection;
+import org.hibernate.annotations.LazyCollectionOption;
+
+@Entity(name = "Post")
+@DiscriminatorValue(value = "WCT")
+public class Post extends Comment{
+
+	protected List<Comment> comments = new ArrayList<Comment>();
+
+	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL , orphanRemoval = false, fetch = FetchType.LAZY)
+	@LazyCollection(LazyCollectionOption.EXTRA)
+	@OrderColumn(name = "idx")
+	public List<Comment> getComments() {
+		return comments;
+	}
+
+	public void setComments(List<Comment> comments) {
+		this.comments = comments;
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/User.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/User.java
new file mode 100644
index 0000000000..e7a15be980
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/onetomany/User.java
@@ -0,0 +1,44 @@
+package org.hibernate.test.annotations.onetomany;
+
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.FetchType;
+import javax.persistence.GeneratedValue;
+import javax.persistence.GenerationType;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+
+@Entity(name="Userx")
+public class User {
+	private Long id;
+	private String name;
+	private Forum forum;
+	
+	@Id
+	@GeneratedValue(strategy = GenerationType.AUTO)
+	@Column(name = "id", updatable = false, insertable = false)
+	public Long getId() {
+		return id;
+	}
+	public void setId(Long id) {
+		this.id = id;
+	}
+	
+	@Column
+	public String getName() {
+		return name;
+	}
+	public void setName(String name) {
+		this.name = name;
+	}
+	
+	@ManyToOne(optional=false,fetch=FetchType.LAZY)
+	@JoinColumn(name="FK_ForumId", nullable=false, insertable=true,updatable=false)
+	public Forum getForum() {
+		return forum;
+	}
+	public void setForum(Forum forum) {
+		this.forum = forum;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index cd85045871..911b004260 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
@@ -1,853 +1,858 @@
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
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.FilterAliasGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.tuple.entity.NonPojoInstrumentationMetadata;
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
 	public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
 		return NoopEntityPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	@Override
 	public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
 		return NoopCollectionPersister.class;
 	}
 
 	public static class NoopEntityPersister implements EntityPersister {
 
 		public NoopEntityPersister(org.hibernate.mapping.PersistentClass persistentClass,
 								   org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
 								   NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
 								   SessionFactoryImplementor sf,
 								   Mapping mapping) {
 			throw new GoofyException(NoopEntityPersister.class);
 		}
 
 		@Override
 		public EntityMode getEntityMode() {
 			return null;
 		}
 
 		@Override
 		public EntityTuplizer getEntityTuplizer() {
 			return null;
 		}
 
 		@Override
 		public EntityInstrumentationMetadata getInstrumentationMetadata() {
 			return new NonPojoInstrumentationMetadata( null );
 		}
 
 		@Override
 		public void postInstantiate() throws MappingException {
 
 		}
 
 		@Override
 		public SessionFactoryImplementor getFactory() {
 			return null;
 		}
 
 		@Override
 		public String getRootEntityName() {
 			return null;
 		}
 
 		@Override
 		public String getEntityName() {
 			return null;
 		}
 
 		@Override
 		public EntityMetamodel getEntityMetamodel() {
 			return null;
 		}
 
 		@Override
 		public boolean isSubclassEntityName(String entityName) {
 			return false;
 		}
 
 		@Override
 		public Serializable[] getPropertySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public Serializable[] getQuerySpaces() {
 			return new Serializable[0];
 		}
 
 		@Override
 		public boolean hasProxy() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasMutableProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasSubselectLoadableCollections() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCascades() {
 			return false;
 		}
 
 		@Override
 		public boolean isMutable() {
 			return false;
 		}
 
 		@Override
 		public boolean isInherited() {
 			return false;
 		}
 
 		@Override
 		public boolean isIdentifierAssignedByInsert() {
 			return false;
 		}
 
 		@Override
 		public Type getPropertyType(String propertyName) throws MappingException {
 			return null;
 		}
 
 		@Override
 		public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) {
 			return new int[0];
 		}
 
 		@Override
 		public boolean hasIdentifierProperty() {
 			return false;
 		}
 
 		@Override
 		public boolean canExtractIdOutOfEntity() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersioned() {
 			return false;
 		}
 
 		@Override
 		public Comparator getVersionComparator() {
 			return null;
 		}
 
 		@Override
 		public VersionType getVersionType() {
 			return null;
 		}
 
 		@Override
 		public int getVersionProperty() {
 			return 0;
 		}
 
 		@Override
 		public boolean hasNaturalIdentifier() {
 			return false;
 		}
 
 		@Override
 		public int[] getNaturalIdentifierProperties() {
 			return new int[0];
 		}
 
 		@Override
 		public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues, LockOptions lockOptions,
 				SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public IdentifierGenerator getIdentifierGenerator() {
 			return null;
 		}
 
 		@Override
 		public boolean hasLazyProperties() {
 			return false;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) {
 		}
 
 		@Override
 		public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session) {
 		}
 
 		@Override
 		public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public Serializable insert(Object[] fields, Object object, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void delete(Serializable id, Object version, Object object, SessionImplementor session) {
 		}
 
 		@Override
 		public void update(Serializable id, Object[] fields, int[] dirtyFields, boolean hasDirtyCollection, Object[] oldFields, Object oldVersion, Object object, Object rowId, SessionImplementor session) {
 		}
 
 		@Override
 		public Type[] getPropertyTypes() {
 			return new Type[0];
 		}
 
 		@Override
 		public String[] getPropertyNames() {
 			return new String[0];
 		}
 
 		@Override
 		public boolean[] getPropertyInsertability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 			return new ValueInclusion[0];
 		}
 
 		@Override
 		public boolean[] getPropertyUpdateability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyCheckability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyNullability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyVersionability() {
 			return new boolean[0];
 		}
 
 		@Override
 		public boolean[] getPropertyLaziness() {
 			return new boolean[0];
 		}
 
 		@Override
 		public CascadeStyle[] getPropertyCascadeStyles() {
 			return new CascadeStyle[0];
 		}
 
 		@Override
 		public Type getIdentifierType() {
 			return null;
 		}
 
 		@Override
 		public String getIdentifierPropertyName() {
 			return null;
 		}
 
 		@Override
 		public boolean isCacheInvalidationRequired() {
 			return false;
 		}
 
 		@Override
 		public boolean isLazyPropertiesCacheable() {
 			return false;
 		}
 
 		@Override
 		public boolean hasCache() {
 			return false;
 		}
 
 		@Override
 		public EntityRegionAccessStrategy getCacheAccessStrategy() {
 			return null;
 		}
 		
 		@Override
 		public boolean hasNaturalIdCache() {
 			return false;
 		}
 
 		@Override
 		public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
 			return null;
 		}
 
 		@Override
 		public CacheEntryStructure getCacheEntryStructure() {
 			return null;
 		}
 
 		@Override
 		public CacheEntry buildCacheEntry(
 				Object entity, Object[] state, Object version, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public ClassMetadata getClassMetadata() {
 			return null;
 		}
 
 		@Override
 		public boolean isBatchLoadable() {
 			return false;
 		}
 
 		@Override
 		public boolean isSelectBeforeUpdateRequired() {
 			return false;
 		}
 
 		@Override
 		public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 			return new Object[0];
 		}
 
 		@Override
 		public Serializable getIdByUniqueKey(Serializable key, String uniquePropertyName, SessionImplementor session) {
 			throw new UnsupportedOperationException( "not supported" );
 		}
 
 		@Override
 		public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstrumented() {
 			return false;
 		}
 
 		@Override
 		public boolean hasInsertGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean hasUpdateGeneratedProperties() {
 			return false;
 		}
 
 		@Override
 		public boolean isVersionPropertyGenerated() {
 			return false;
 		}
 
 		@Override
 		public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		}
 
 		@Override
 		public void afterReassociate(Object entity, SessionImplementor session) {
 		}
 
 		@Override
 		public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException {
 			return null;
 		}
 
 		@Override
 		public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 			return new Object[0];
 		}
 
 		@Override
 		public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		}
 
 		@Override
 		public Class getMappedClass() {
 			return null;
 		}
 
 		@Override
 		public boolean implementsLifecycle() {
 			return false;
 		}
 
 		@Override
 		public Class getConcreteProxyClass() {
 			return null;
 		}
 
 		@Override
 		public void setPropertyValues(Object object, Object[] values) {
 		}
 
 		@Override
 		public void setPropertyValue(Object object, int i, Object value) {
 		}
 
 		@Override
 		public Object[] getPropertyValues(Object object) {
 			return new Object[0];
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, int i) {
 			return null;
 		}
 
 		@Override
 		public Object getPropertyValue(Object object, String propertyName) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object object) {
 			return null;
 		}
 
 		@Override
 		public Serializable getIdentifier(Object entity, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		}
 
 		@Override
 		public Object getVersion(Object object) {
 			return null;
 		}
 
 		@Override
 		public Object instantiate(Serializable id, SessionImplementor session) {
 			return null;
 		}
 
 		@Override
 		public boolean isInstance(Object object) {
 			return false;
 		}
 
 		@Override
 		public boolean hasUninitializedLazyProperties(Object object) {
 			return false;
 		}
 
 		@Override
 		public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		}
 
 		@Override
 		public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 			return null;
 		}
 
 		@Override
 		public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
 			// TODO Auto-generated method stub
 			return null;
 		}
 
 		@Override
 		public EntityPersister getEntityPersister() {
 			return this;
 		}
 
 		@Override
 		public EntityIdentifierDefinition getEntityKeyDefinition() {
 			return null;  //To change body of implemented methods use File | Settings | File Templates.
 		}
 
 		@Override
 		public Iterable<AttributeDefinition> getAttributes() {
 			throw new NotYetImplementedException();
 		}
 	}
 
 	public static class NoopCollectionPersister implements CollectionPersister {
 
 		public NoopCollectionPersister(org.hibernate.mapping.Collection collection,
 									   org.hibernate.cache.spi.access.CollectionRegionAccessStrategy strategy,
 									   org.hibernate.cfg.Configuration configuration,
 									   SessionFactoryImplementor sf) {
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
 
 		@Override
 		public CollectionPersister getCollectionPersister() {
 			return this;
 		}
 
 		public CollectionType getCollectionType() {
 			throw new NotYetImplementedException();
 		}
 
 		@Override
 		public CollectionIndexDefinition getIndexDefinition() {
 			throw new NotYetImplementedException();
 		}
 
 		@Override
 		public CollectionElementDefinition getElementDefinition() {
 			throw new NotYetImplementedException();
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
 
 		@Override
 		public int getBatchSize() {
 			return 0;
 		}
+
+		@Override
+		public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
+				throws HibernateException {
+		}
 	}
 }
