diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
index 9bafc76724..82424ba235 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityAction.java
@@ -1,190 +1,189 @@
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
-import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 
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
-			this.instance = session.getPersistenceContext().getEntity( new EntityKey( id, persister, session.getEntityMode() ) );
+			this.instance = session.getPersistenceContext().getEntity( session.generateEntityKey( id, persister ) );
 		}
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
index b14dfb7798..c954a78d7d 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
@@ -1,204 +1,204 @@
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
 import org.hibernate.event.EventSource;
 import org.hibernate.event.PostInsertEvent;
 import org.hibernate.event.PostInsertEventListener;
 import org.hibernate.event.PreInsertEvent;
 import org.hibernate.event.PreInsertEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 
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
 		return getSession().getListeners().getPostCommitInsertEventListeners().length>0;
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
 		PostInsertEventListener[] postListeners = getSession().getListeners()
 				.getPostInsertEventListeners();
 		if (postListeners.length>0) {
 			PostInsertEvent postEvent = new PostInsertEvent(
 					getInstance(),
 					generatedId,
 					state,
 					getPersister(),
 					(EventSource) getSession()
 			);
 			for ( PostInsertEventListener postListener : postListeners ) {
 				postListener.onPostInsert( postEvent );
 			}
 		}
 	}
 
 	private void postCommitInsert() {
 		PostInsertEventListener[] postListeners = getSession().getListeners()
 				.getPostCommitInsertEventListeners();
 		if (postListeners.length>0) {
 			PostInsertEvent postEvent = new PostInsertEvent(
 					getInstance(),
 					generatedId,
 					state,
 					getPersister(),
 					(EventSource) getSession()
 			);
 			for ( PostInsertEventListener postListener : postListeners ) {
 				postListener.onPostInsert( postEvent );
 			}
 		}
 	}
 
 	private boolean preInsert() {
 		PreInsertEventListener[] preListeners = getSession().getListeners()
 				.getPreInsertEventListeners();
 		boolean veto = false;
 		if (preListeners.length>0) {
 			PreInsertEvent preEvent = new PreInsertEvent( getInstance(), null, state, getPersister(), (EventSource)getSession() );
 			for ( PreInsertEventListener preListener : preListeners ) {
 				veto = preListener.onPreInsert( preEvent ) || veto;
 			}
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
-		return new EntityKey( getDelayedId(), getPersister(), getSession().getEntityMode() );
+		return getSession().generateEntityKey( getDelayedId(), getPersister() );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/Collections.java b/hibernate-core/src/main/java/org/hibernate/engine/Collections.java
index 100b33512d..9747412240 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/Collections.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/Collections.java
@@ -1,263 +1,261 @@
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
 package org.hibernate.engine;
+
 import java.io.Serializable;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.CollectionType;
-import org.jboss.logging.Logger;
 
 /**
  * Implements book-keeping for the collection persistence by reachability algorithm
  * @author Gavin King
  */
 public final class Collections {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, Collections.class.getName());
 
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
-			EntityKey key = new EntityKey(
-					ownerId,
-			        loadedPersister.getOwnerEntityPersister(),
-			        session.getEntityMode()
-			);
+			EntityKey key = session.generateEntityKey( ownerId, loadedPersister.getOwnerEntityPersister() );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/EntityEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/EntityEntry.java
index 8d50a52c1e..a0d5121288 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/EntityEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/EntityEntry.java
@@ -1,389 +1,396 @@
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
+
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
+
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 
 /**
- * We need an entry to tell us all about the current state
- * of an object with respect to its persistent state
+ * We need an entry to tell us all about the current state of an object with respect to its persistent state
  * 
  * @author Gavin King
  */
 public final class EntityEntry implements Serializable {
-
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
+	private final String tenantId;
 	private final String entityName;
 	private transient EntityKey cachedEntityKey; // cached EntityKey (lazy-initialized)
 	private boolean isBeingReplicated;
 	private boolean loadedWithLazyPropertiesUnfetched; //NOTE: this is not updated when properties are fetched lazily!
 	private final transient Object rowId;
 
 	EntityEntry(
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final EntityMode entityMode,
+			final String tenantId,
 			final boolean disableVersionIncrement,
 			final boolean lazyPropertiesAreUnfetched) {
 		this.status=status;
 		this.previousStatus = null;
 		// only retain loaded state if the status is not Status.READ_ONLY
 		if ( status != Status.READ_ONLY ) { this.loadedState = loadedState; }
 		this.id=id;
 		this.rowId=rowId;
 		this.existsInDatabase=existsInDatabase;
 		this.version=version;
 		this.lockMode=lockMode;
 		this.isBeingReplicated=disableVersionIncrement;
 		this.loadedWithLazyPropertiesUnfetched = lazyPropertiesAreUnfetched;
 		this.persister=persister;
 		this.entityMode = entityMode;
+		this.tenantId = tenantId;
 		this.entityName = persister == null ? null : persister.getEntityName();
 	}
 
 	private EntityEntry(
 			final SessionFactoryImplementor factory,
 			final String entityName,
 			final Serializable id,
 			final EntityMode entityMode,
+			final String tenantId,
 			final Status status,
 			final Status previousStatus,
 			final Object[] loadedState,
 	        final Object[] deletedState,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final boolean isBeingReplicated,
 			final boolean loadedWithLazyPropertiesUnfetched) {
 		// Used during custom deserialization
 		this.entityName = entityName;
 		this.persister = ( factory == null ? null : factory.getEntityPersister( entityName ) );
 		this.id = id;
 		this.entityMode = entityMode;
+		this.tenantId = tenantId;
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
-			cachedEntityKey = new EntityKey( getId(), getPersister(), entityMode );
+			cachedEntityKey = new EntityKey( getId(), getPersister(), entityMode, tenantId );
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
 		setLockMode(LockMode.WRITE);
 
 		if ( getPersister().isVersioned() ) {
 			this.version = nextVersion;
 			getPersister().setPropertyValue(
 					entity,
 					getPersister().getVersionProperty(), 
 					nextVersion, 
 					entityMode 
 			);
 		}
 
 		FieldInterceptionHelper.clearDirty( entity );
 	}
 
 	/**
 	 * After actually deleting a row, record the fact that the instance no longer
 	 * exists in the database
 	 */
 	public void postDelete() {
 		previousStatus = status;
 		status = Status.GONE;
 		existsInDatabase = false;
 	}
 	
 	/**
 	 * After actually inserting a row, record the fact that the instance exists on the 
 	 * database (needed for identity-column key generation)
 	 */
 	public void postInsert() {
 		existsInDatabase = true;
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
 
 	public boolean requiresDirtyCheck(Object entity) {		
 		return isModifiableEntity() && (
 				getPersister().hasMutableProperties() ||
 				!FieldInterceptionHelper.isInstrumented( entity ) ||
 				FieldInterceptionHelper.extractFieldInterceptor( entity).isDirty()
 			);
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
 		return ( status != Status.READ_ONLY ) &&
 				! ( status == Status.DELETED && previousStatus == Status.READ_ONLY ) &&
 				getPersister().isMutable();
 	}
 
 	public void forceLocked(Object entity, Object nextVersion) {
 		version = nextVersion;
 		loadedState[ persister.getVersionProperty() ] = version;
 		//noinspection deprecation
 		setLockMode( LockMode.FORCE );  // TODO:  use LockMode.PESSIMISTIC_FORCE_INCREMENT
 		persister.setPropertyValue(
 				entity,
 		        getPersister().getVersionProperty(),
 		        nextVersion,
 		        entityMode
 		);
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
 			loadedState = getPersister().getPropertyValues( entity, entityMode );
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
 
 	/**
 	 * Custom serialization routine used during serialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param oos The stream to which we should write the serial data.
 	 *
 	 * @throws IOException If a stream error occurs
 	 */
 	void serialize(ObjectOutputStream oos) throws IOException {
-		oos.writeObject( entityName );
+		oos.writeUTF( entityName );
 		oos.writeObject( id );
-		oos.writeObject( entityMode.toString() );
-		oos.writeObject( status.toString() );
-		oos.writeObject( ( previousStatus == null ? "" : previousStatus.toString() ) );
+		oos.writeUTF( entityMode.toString() );
+		oos.writeUTF( tenantId );
+		oos.writeUTF( status.toString() );
+		oos.writeUTF( ( previousStatus == null ? "" : previousStatus.toString() ) );
 		// todo : potentially look at optimizing these two arrays
 		oos.writeObject( loadedState );
 		oos.writeObject( deletedState );
 		oos.writeObject( version );
-		oos.writeObject( lockMode.toString() );
+		oos.writeUTF( lockMode.toString() );
 		oos.writeBoolean( existsInDatabase );
 		oos.writeBoolean( isBeingReplicated );
 		oos.writeBoolean( loadedWithLazyPropertiesUnfetched );
 	}
 
 	/**
 	 * Custom deserialization routine used during deserialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param ois The stream from which to read the entry.
 	 * @param session The session being deserialized.
 	 *
 	 * @return The deserialized EntityEntry
 	 *
 	 * @throws IOException If a stream error occurs
 	 * @throws ClassNotFoundException If any of the classes declared in the stream
 	 * cannot be found
 	 */
 	static EntityEntry deserialize(
 			ObjectInputStream ois,
 	        SessionImplementor session) throws IOException, ClassNotFoundException {
 		String previousStatusString = null;
 		return new EntityEntry(
 				( session == null ? null : session.getFactory() ),
-		        ( String ) ois.readObject(),
+		        ois.readUTF(),
 				( Serializable ) ois.readObject(),
-	            EntityMode.parse( ( String ) ois.readObject() ),
-				Status.parse( ( String ) ois.readObject() ),
+	            EntityMode.parse( ois.readUTF() ),
+				ois.readUTF(),
+				Status.parse( ois.readUTF() ),
 				( ( previousStatusString = ( String ) ois.readObject() ).length() == 0 ?
 							null :
 							Status.parse( previousStatusString ) 
 				),
 	            ( Object[] ) ois.readObject(),
 	            ( Object[] ) ois.readObject(),
 	            ois.readObject(),
-	            LockMode.parse( ( String ) ois.readObject() ),
+	            LockMode.parse( ois.readUTF() ),
 	            ois.readBoolean(),
 	            ois.readBoolean(),
 	            ois.readBoolean()
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/EntityKey.java b/hibernate-core/src/main/java/org/hibernate/engine/EntityKey.java
index a450284714..d84635abd3 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/EntityKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/EntityKey.java
@@ -1,176 +1,198 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 20082011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.engine;
+
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 
 /**
  * Uniquely identifies of an entity instance in a particular session by identifier.
  * <p/>
- * Uniqueing information consists of the entity-name and the identifier value.
+ * Information used to determine uniqueness consists of the entity-name and the identifier value (see {@link #equals}).
  *
- * @see EntityUniqueKey
  * @author Gavin King
  */
 public final class EntityKey implements Serializable {
 	private final Serializable identifier;
-	private final String rootEntityName;
 	private final String entityName;
+	private final String rootEntityName;
+	private final EntityMode entityMode;
+	private final String tenantId;
+
+	private final int hashCode;
+
 	private final Type identifierType;
 	private final boolean isBatchLoadable;
 	private final SessionFactoryImplementor factory;
-	private final int hashCode;
-	private final EntityMode entityMode;
 
 	/**
-	 * Construct a unique identifier for an entity class instance
+	 * Construct a unique identifier for an entity class instance.
+	 * <p>
+	 * NOTE : This signature has changed to accommodate both entity mode and multi-tenancy, both of which relate to
+	 * the Session to which this key belongs.  To help minimize the impact of these changes in the future, the
+	 * {@link SessionImplementor#generateEntityKey} method was added to hide the session-specific changes.
+	 *
+	 * @param id The entity id
+	 * @param persister The entity persister
+	 * @param entityMode The entity mode of the session to which this key belongs
+	 * @param tenantId The tenant identifier of the session to which this key belongs
 	 */
-	public EntityKey(Serializable id, EntityPersister persister, EntityMode entityMode) {
+	public EntityKey(Serializable id, EntityPersister persister, EntityMode entityMode, String tenantId) {
 		if ( id == null ) {
 			throw new AssertionFailure( "null identifier" );
 		}
 		this.identifier = id; 
-		this.entityMode = entityMode;
 		this.rootEntityName = persister.getRootEntityName();
 		this.entityName = persister.getEntityName();
+		this.entityMode = entityMode;
+		this.tenantId = tenantId;
+
 		this.identifierType = persister.getIdentifierType();
 		this.isBatchLoadable = persister.isBatchLoadable();
 		this.factory = persister.getFactory();
-		hashCode = generateHashCode(); //cache the hashcode
+		this.hashCode = generateHashCode();
 	}
 
 	/**
 	 * Used to reconstruct an EntityKey during deserialization.
 	 *
 	 * @param identifier The identifier value
 	 * @param rootEntityName The root entity name
 	 * @param entityName The specific entity name
 	 * @param identifierType The type of the identifier value
 	 * @param batchLoadable Whether represented entity is eligible for batch loading
 	 * @param factory The session factory
 	 * @param entityMode The entity's entity mode
+	 * @param tenantId The entity's tenant id (from the session that loaded it).
 	 */
 	private EntityKey(
 			Serializable identifier,
 	        String rootEntityName,
 	        String entityName,
 	        Type identifierType,
 	        boolean batchLoadable,
 	        SessionFactoryImplementor factory,
-	        EntityMode entityMode) {
+	        EntityMode entityMode,
+			String tenantId) {
 		this.identifier = identifier;
 		this.rootEntityName = rootEntityName;
 		this.entityName = entityName;
 		this.identifierType = identifierType;
 		this.isBatchLoadable = batchLoadable;
 		this.factory = factory;
 		this.entityMode = entityMode;
+		this.tenantId = tenantId;
 		this.hashCode = generateHashCode();
 	}
 
+	private int generateHashCode() {
+		int result = 17;
+		result = 37 * result + rootEntityName.hashCode();
+		result = 37 * result + identifierType.getHashCode( identifier, entityMode, factory );
+		return result;
+	}
+
 	public boolean isBatchLoadable() {
 		return isBatchLoadable;
 	}
 
-	/**
-	 * Get the user-visible identifier
-	 */
 	public Serializable getIdentifier() {
 		return identifier;
 	}
 
 	public String getEntityName() {
 		return entityName;
 	}
 
+	@Override
 	public boolean equals(Object other) {
 		EntityKey otherKey = (EntityKey) other;
 		return otherKey.rootEntityName.equals(this.rootEntityName) && 
 			identifierType.isEqual(otherKey.identifier, this.identifier, entityMode, factory);
 	}
-	
-	private int generateHashCode() {
-		int result = 17;
-		result = 37 * result + rootEntityName.hashCode();
-		result = 37 * result + identifierType.getHashCode( identifier, entityMode, factory );
-		return result;
-	}
 
+	@Override
 	public int hashCode() {
 		return hashCode;
 	}
 
+	@Override
 	public String toString() {
 		return "EntityKey" + 
 			MessageHelper.infoString( factory.getEntityPersister( entityName ), identifier, factory );
 	}
 
 	/**
 	 * Custom serialization routine used during serialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param oos The stream to which we should write the serial data.
-	 * @throws IOException
+	 *
+	 * @throws IOException Thrown by Java I/O
 	 */
 	void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeObject( identifier );
-		oos.writeObject( rootEntityName );
-		oos.writeObject( entityName );
+		oos.writeUTF( rootEntityName );
+		oos.writeUTF( entityName );
 		oos.writeObject( identifierType );
 		oos.writeBoolean( isBatchLoadable );
-		oos.writeObject( entityMode.toString() );
+		oos.writeUTF( entityMode.toString() );
+		oos.writeUTF( tenantId );
 	}
 
 	/**
 	 * Custom deserialization routine used during deserialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param ois The stream from which to read the entry.
 	 * @param session The session being deserialized.
+	 *
 	 * @return The deserialized EntityEntry
-	 * @throws IOException
-	 * @throws ClassNotFoundException
+	 *
+	 * @throws IOException Thrown by Java I/O
+	 * @throws ClassNotFoundException Thrown by Java I/O
 	 */
 	static EntityKey deserialize(
 			ObjectInputStream ois,
 	        SessionImplementor session) throws IOException, ClassNotFoundException {
 		return new EntityKey(
 				( Serializable ) ois.readObject(),
-		        ( String ) ois.readObject(),
-		        ( String ) ois.readObject(),
+		        ois.readUTF(),
+		        ois.readUTF(),
 		        ( Type ) ois.readObject(),
 		        ois.readBoolean(),
 		        ( session == null ? null : session.getFactory() ),
-		        EntityMode.parse( ( String ) ois.readObject() )
+		        EntityMode.parse( ois.readUTF() ),
+				ois.readUTF()
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
index 19758ee96d..35474769e9 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/SessionImplementor.java
@@ -1,371 +1,388 @@
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
+	 * Match te method on {@link org.hibernate.Session} and {@link org.hibernate.StatelessSession}
+	 *
+	 * @return The tenant identifier of this session
+	 */
+	public String getTenantIdentifier();
+
+	/**
 	 * Provides access to JDBC connections
 	 *
 	 * @return The contract for accessing JDBC connections.
 	 */
 	public JdbcConnectionAccess getJdbcConnectionAccess();
 
 	/**
+	 * Hide the changing requirements of entity key creation
+	 *
+	 * @param id The entity id
+	 * @param persister The entity persister
+	 *
+	 * @return The entity key
+	 */
+	public EntityKey generateEntityKey(Serializable id, EntityPersister persister);
+
+	/**
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
 
 	/**
 	 * Retrieves the configured event listeners from this event source.
 	 *
 	 * @return The configured event listeners.
 	 */
 	public EventListeners getListeners();
 
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
index ef57f1ebcc..8a3419e73d 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/StatefulPersistenceContext.java
@@ -1,1675 +1,1678 @@
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
 package org.hibernate.engine;
 
-import static org.jboss.logging.Logger.Level.WARN;
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
+
 import org.apache.commons.collections.map.AbstractReferenceMap;
 import org.apache.commons.collections.map.ReferenceMap;
+import org.jboss.logging.Logger;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.loading.LoadContexts;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.tuple.ElementWrapper;
-import org.jboss.logging.Logger;
+
+import static org.jboss.logging.Logger.Level.WARN;
 
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
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
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
-		EntityKey key = new EntityKey( id, persister, session.getEntityMode() );
+		final EntityKey key = session.generateEntityKey( id, persister );
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
+				session.getTenantIdentifier(),
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched
-			);
+		);
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
-			EntityPersister persister = session.getFactory().getEntityPersister( li.getEntityName() );
-			EntityKey key = new EntityKey( li.getIdentifier(), persister, session.getEntityMode() );
+			final EntityPersister persister = session.getFactory().getEntityPersister( li.getEntityName() );
+			final EntityKey key = session.generateEntityKey( li.getIdentifier(), persister );
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
-		return getEntity( new EntityKey( key, collectionPersister.getOwnerEntityPersister(), session.getEntityMode() ) );
+		return getEntity( session.generateEntityKey( key, collectionPersister.getOwnerEntityPersister() ) );
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
 		return nonExistantEntityKeys.contains(key);
 	}*/
 
 	/**
 	 * Do we already know that the entity does not exist in the
 	 * database?
 	 */
 	/*public boolean isNonExistant(EntityUniqueKey key) {
 		return nonExistentEntityUniqueKeys.contains(key);
 	}*/
 
 	public int getCascadeLevel() {
 		return cascading;
 	}
 
 	public int incrementCascadeLevel() {
 		return ++cascading;
 	}
 
 	public int decrementCascadeLevel() {
 		return --cascading;
 	}
 
 	public boolean isFlushing() {
 		return flushing;
 	}
 
 	public void setFlushing(boolean flushing) {
 		this.flushing = flushing;
 	}
 
 	/**
 	 * Call this before begining a two-phase load
 	 */
 	public void beforeLoad() {
 		loadCounter++;
 	}
 
 	/**
 	 * Call this after finishing a two-phase load
 	 */
 	public void afterLoad() {
 		loadCounter--;
 	}
 
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
 		return new StringBuffer()
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
 	public Serializable getOwnerId(String entityName, String propertyName, Object childEntity, Map mergeMap) {
 		final String collectionRole = entityName + '.' + propertyName;
 		final EntityPersister persister = session.getFactory().getEntityPersister( entityName );
 		final CollectionPersister collectionPersister = session.getFactory().getCollectionPersister( collectionRole );
 
 	    // try cache lookup first
 		Object parent = parentsByChild.get( childEntity );
 		if ( parent != null ) {
 			final EntityEntry entityEntry = ( EntityEntry ) entityEntries.get( parent );
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
 		Iterator entities = IdentityMap.entries(entityEntries).iterator();
 		while ( entities.hasNext() ) {
 			final Map.Entry me = ( Map.Entry ) entities.next();
 			final EntityEntry entityEntry = ( EntityEntry ) me.getValue();
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
 			Iterator mergeMapItr = mergeMap.entrySet().iterator();
 			while ( mergeMapItr.hasNext() ) {
 				final Map.Entry mergeMapEntry = ( Map.Entry ) mergeMapItr.next();
 				if ( mergeMapEntry.getKey() instanceof HibernateProxy ) {
 					final HibernateProxy proxy = ( HibernateProxy ) mergeMapEntry.getKey();
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
 		Object collection = persister.getPropertyValue(
 				potentialParent,
 				property,
 				session.getEntityMode()
 		);
 		return collection != null
 				&& Hibernate.isInitialized( collection )
 				&& collectionPersister.getCollectionType().contains( collection, childEntity, session );
 	}
 
 	/**
 	 * Search the persistence context for an index of the child object,
 	 * given a collection role
 	 */
 	public Object getIndexInOwner(String entity, String property, Object childEntity, Map mergeMap) {
 
 		EntityPersister persister = session.getFactory()
 				.getEntityPersister(entity);
 		CollectionPersister cp = session.getFactory()
 				.getCollectionPersister(entity + '.' + property);
 
 	    // try cache lookup first
 	    Object parent = parentsByChild.get(childEntity);
 		if (parent != null) {
 			final EntityEntry entityEntry = (EntityEntry) entityEntries.get(parent);
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
 		Iterator entities = IdentityMap.entries(entityEntries).iterator();
 		while ( entities.hasNext() ) {
 			Map.Entry me = (Map.Entry) entities.next();
 			EntityEntry ee = (EntityEntry) me.getValue();
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
 			Object potentialParent
 	){
 		Object collection = persister.getPropertyValue( potentialParent, property, session.getEntityMode() );
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
 	public void addNullProperty(EntityKey ownerKey, String propertyName) {
 		nullAssociations.add( new AssociationKey(ownerKey, propertyName) );
 	}
 
 	/**
 	 * Is the association property belonging to the keyed entity null?
 	 */
 	public boolean isPropertyNull(EntityKey ownerKey, String propertyName) {
 		return nullAssociations.contains( new AssociationKey(ownerKey, propertyName) );
 	}
 
 	private void clearNullProperties() {
 		nullAssociations.clear();
 	}
 
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
 
 	public void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId) {
 		Object entity = entitiesByKey.remove( oldKey );
 		EntityEntry oldEntry = ( EntityEntry ) entityEntries.remove( entity );
 		parentsByChild.clear();
 
-		EntityKey newKey = new EntityKey( generatedId, oldEntry.getPersister(), getSession().getEntityMode() );
+		final EntityKey newKey = session.generateEntityKey( generatedId, oldEntry.getPersister() );
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
         LOG.trace("Serializing persistent-context");
 
 		oos.writeBoolean( defaultReadOnly );
 		oos.writeBoolean( hasNonReadOnlyEntities );
 
 		oos.writeInt( entitiesByKey.size() );
         LOG.trace("Starting serialization of [" + entitiesByKey.size() + "] entitiesByKey entries");
 		Iterator itr = entitiesByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitiesByUniqueKey.size() );
         LOG.trace("Starting serialization of [" + entitiesByUniqueKey.size() + "] entitiesByUniqueKey entries");
 		itr = entitiesByUniqueKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityUniqueKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( proxiesByKey.size() );
         LOG.trace("Starting serialization of [" + proxiesByKey.size() + "] proxiesByKey entries");
 		itr = proxiesByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitySnapshotsByKey.size() );
         LOG.trace("Starting serialization of [" + entitySnapshotsByKey.size() + "] entitySnapshotsByKey entries");
 		itr = entitySnapshotsByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entityEntries.size() );
         LOG.trace("Starting serialization of [" + entityEntries.size() + "] entityEntries entries");
 		itr = entityEntries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			( ( EntityEntry ) entry.getValue() ).serialize( oos );
 		}
 
 		oos.writeInt( collectionsByKey.size() );
         LOG.trace("Starting serialization of [" + collectionsByKey.size() + "] collectionsByKey entries");
 		itr = collectionsByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( CollectionKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( collectionEntries.size() );
         LOG.trace("Starting serialization of [" + collectionEntries.size() + "] collectionEntries entries");
 		itr = collectionEntries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			( ( CollectionEntry ) entry.getValue() ).serialize( oos );
 		}
 
 		oos.writeInt( arrayHolders.size() );
         LOG.trace("Starting serialization of [" + arrayHolders.size() + "] arrayHolders entries");
 		itr = arrayHolders.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( nullifiableEntityKeys.size() );
         LOG.trace("Starting serialization of [" + nullifiableEntityKeys.size() + "] nullifiableEntityKey entries");
 		itr = nullifiableEntityKeys.iterator();
 		while ( itr.hasNext() ) {
 			EntityKey entry = ( EntityKey ) itr.next();
 			entry.serialize( oos );
 		}
 	}
 
 	public static StatefulPersistenceContext deserialize(
 			ObjectInputStream ois,
 	        SessionImplementor session) throws IOException, ClassNotFoundException {
         LOG.trace("Serializing persistent-context");
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
             LOG.trace("Starting deserialization of [" + count + "] entitiesByKey entries");
 			rtn.entitiesByKey = new HashMap( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
             LOG.trace("Starting deserialization of [" + count + "] entitiesByUniqueKey entries");
 			rtn.entitiesByUniqueKey = new HashMap( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByUniqueKey.put( EntityUniqueKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
             LOG.trace("Starting deserialization of [" + count + "] proxiesByKey entries");
 			rtn.proxiesByKey = new ReferenceMap( AbstractReferenceMap.HARD, AbstractReferenceMap.WEAK, count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count, .75f );
 			for ( int i = 0; i < count; i++ ) {
 				EntityKey ek = EntityKey.deserialize( ois, session );
 				Object proxy = ois.readObject();
 				if ( proxy instanceof HibernateProxy ) {
 					( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().setSession( session );
 					rtn.proxiesByKey.put( ek, proxy );
                 } else LOG.trace("Encountered prunded proxy");
 				// otherwise, the proxy was pruned during the serialization process
 			}
 
 			count = ois.readInt();
             LOG.trace("Starting deserialization of [" + count + "] entitySnapshotsByKey entries");
 			rtn.entitySnapshotsByKey = new HashMap( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitySnapshotsByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
             LOG.trace("Starting deserialization of [" + count + "] entityEntries entries");
 			rtn.entityEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				Object entity = ois.readObject();
 				EntityEntry entry = EntityEntry.deserialize( ois, session );
 				rtn.entityEntries.put( entity, entry );
 			}
 
 			count = ois.readInt();
             LOG.trace("Starting deserialization of [" + count + "] collectionsByKey entries");
 			rtn.collectionsByKey = new HashMap( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.collectionsByKey.put( CollectionKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
             LOG.trace("Starting deserialization of [" + count + "] collectionEntries entries");
 			rtn.collectionEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				final PersistentCollection pc = ( PersistentCollection ) ois.readObject();
 				final CollectionEntry ce = CollectionEntry.deserialize( ois, session );
 				pc.setCurrentSession( session );
 				rtn.collectionEntries.put( pc, ce );
 			}
 
 			count = ois.readInt();
             LOG.trace("Starting deserialization of [" + count + "] arrayHolders entries");
 			rtn.arrayHolders = IdentityMap.instantiate( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.arrayHolders.put( ois.readObject(), ois.readObject() );
 			}
 
 			count = ois.readInt();
             LOG.trace("Starting deserialization of [" + count + "] nullifiableEntityKey entries");
 			rtn.nullifiableEntityKeys = new HashSet();
 			for ( int i = 0; i < count; i++ ) {
 				rtn.nullifiableEntityKeys.add( EntityKey.deserialize( ois, session ) );
 			}
 
 		}
 		catch ( HibernateException he ) {
 			throw new InvalidObjectException( he.getMessage() );
 		}
 
 		return rtn;
 	}
 
 	/**
 	 * @see org.hibernate.engine.PersistenceContext#addChildParent(java.lang.Object, java.lang.Object)
 	 */
 	public void addChildParent(Object child, Object parent) {
 		parentsByChild.put(child, parent);
 	}
 
 	/**
 	 * @see org.hibernate.engine.PersistenceContext#removeChildParent(java.lang.Object)
 	 */
 	public void removeChildParent(Object child) {
 	   parentsByChild.remove(child);
 	}
 
 
 	private HashMap<String,List<Serializable>> insertedKeysMap;
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void registerInsertedKey(EntityPersister persister, Serializable id) {
 		// we only are about regsitering these if the persister defines caching
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
 
 	/**
 	 * {@inheritDoc}
 	 */
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractReassociateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractReassociateEventListener.java
index c257cb8e06..c166c62b25 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractReassociateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractReassociateEventListener.java
@@ -1,102 +1,105 @@
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
+
 import java.io.Serializable;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockMode;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.Status;
 import org.hibernate.engine.Versioning;
 import org.hibernate.event.AbstractEvent;
 import org.hibernate.event.EventSource;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.TypeHelper;
-import org.jboss.logging.Logger;
 
 /**
  * A convenience base class for listeners that respond to requests to reassociate an entity
  * to a session ( such as through lock() or update() ).
  *
  * @author Gavin King
  */
 public class AbstractReassociateEventListener implements Serializable {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        AbstractReassociateEventListener.class.getName());
 
 	/**
 	 * Associates a given entity (either transient or associated with another session) to
 	 * the given session.
 	 *
 	 * @param event The event triggering the re-association
 	 * @param object The entity to be associated
 	 * @param id The id of the entity.
 	 * @param persister The entity's persister instance.
 	 *
 	 * @return An EntityEntry representing the entity within this session.
 	 */
 	protected final EntityEntry reassociate(AbstractEvent event, Object object, Serializable id, EntityPersister persister) {
 
         if (LOG.isTraceEnabled()) LOG.trace("Reassociating transient instance: "
                                             + MessageHelper.infoString(persister, id, event.getSession().getFactory()));
 
-		EventSource source = event.getSession();
-		EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
+		final EventSource source = event.getSession();
+		final EntityKey key = source.generateEntityKey( id, persister );
 
 		source.getPersistenceContext().checkUniqueness( key, object );
 
 		//get a snapshot
 		Object[] values = persister.getPropertyValues( object, source.getEntityMode() );
 		TypeHelper.deepCopy(
 				values,
 				persister.getPropertyTypes(),
 				persister.getPropertyUpdateability(),
 				values,
 				source
 		);
 		Object version = Versioning.getVersion( values, persister );
 
 		EntityEntry newEntry = source.getPersistenceContext().addEntity(
 				object,
 				( persister.isMutable() ? Status.MANAGED : Status.READ_ONLY ),
 				values,
 				key,
 				version,
 				LockMode.NONE,
 				true,
 				persister,
 				false,
 				true //will be ignored, using the existing Entry instead
 		);
 
 		new OnLockVisitor( source, id, object ).process( object, persister );
 
 		persister.afterReassociate( object, source );
 
 		return newEntry;
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java
index 3b06992e53..6d715fc3bd 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/AbstractSaveEventListener.java
@@ -1,515 +1,517 @@
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
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockMode;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.action.internal.EntityIdentityInsertAction;
 import org.hibernate.action.internal.EntityInsertAction;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.Cascade;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.ForeignKeys;
 import org.hibernate.engine.Nullability;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.engine.Versioning;
 import org.hibernate.event.EventSource;
 import org.hibernate.id.IdentifierGenerationException;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
  * A convenience bas class for listeners responding to save events.
  *
  * @author Steve Ebersole.
  */
 public abstract class AbstractSaveEventListener extends AbstractReassociateEventListener {
 
 	protected static final int PERSISTENT = 0;
 	protected static final int TRANSIENT = 1;
 	protected static final int DETACHED = 2;
 	protected static final int DELETED = 3;
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        AbstractSaveEventListener.class.getName());
 
 	/**
 	 * Prepares the save call using the given requested id.
 	 *
 	 * @param entity The entity to be saved.
 	 * @param requestedId The id to which to associate the entity.
 	 * @param entityName The name of the entity being saved.
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of this save event.
 	 *
 	 * @return The id used to save the entity.
 	 */
 	protected Serializable saveWithRequestedId(
 			Object entity,
 			Serializable requestedId,
 			String entityName,
 			Object anything,
 			EventSource source) {
 		return performSave(
 				entity,
 				requestedId,
 				source.getEntityPersister( entityName, entity ),
 				false,
 				anything,
 				source,
 				true
 		);
 	}
 
 	/**
 	 * Prepares the save call using a newly generated id.
 	 *
 	 * @param entity The entity to be saved
 	 * @param entityName The entity-name for the entity to be saved
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of this save event.
 	 * @param requiresImmediateIdAccess does the event context require
 	 * access to the identifier immediately after execution of this method (if
 	 * not, post-insert style id generators may be postponed if we are outside
 	 * a transaction).
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable saveWithGeneratedId(
 			Object entity,
 			String entityName,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 		EntityPersister persister = source.getEntityPersister( entityName, entity );
 		Serializable generatedId = persister.getIdentifierGenerator().generate( source, entity );
 		if ( generatedId == null ) {
 			throw new IdentifierGenerationException( "null id generated for:" + entity.getClass() );
 		}
 		else if ( generatedId == IdentifierGeneratorHelper.SHORT_CIRCUIT_INDICATOR ) {
 			return source.getIdentifier( entity );
 		}
 		else if ( generatedId == IdentifierGeneratorHelper.POST_INSERT_INDICATOR ) {
 			return performSave( entity, null, persister, true, anything, source, requiresImmediateIdAccess );
 		}
 		else {
             // TODO: define toString()s for generators
             if (LOG.isDebugEnabled()) LOG.debugf("Generated identifier: %s, using strategy: %s",
                                                  persister.getIdentifierType().toLoggableString(generatedId, source.getFactory()),
                                                  persister.getIdentifierGenerator().getClass().getName());
 
 			return performSave( entity, generatedId, persister, false, anything, source, true );
 		}
 	}
 
 	/**
 	 * Ppepares the save call by checking the session caches for a pre-existing
 	 * entity and performing any lifecycle callbacks.
 	 *
 	 * @param entity The entity to be saved.
 	 * @param id The id by which to save the entity.
 	 * @param persister The entity's persister instance.
 	 * @param useIdentityColumn Is an identity column being used?
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session from which the event originated.
 	 * @param requiresImmediateIdAccess does the event context require
 	 * access to the identifier immediately after execution of this method (if
 	 * not, post-insert style id generators may be postponed if we are outside
 	 * a transaction).
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable performSave(
 			Object entity,
 			Serializable id,
 			EntityPersister persister,
 			boolean useIdentityColumn,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 
-        if (LOG.isTraceEnabled()) LOG.trace("Saving " + MessageHelper.infoString(persister, id, source.getFactory()));
+        if ( LOG.isTraceEnabled() ) {
+			LOG.trace("Saving " + MessageHelper.infoString(persister, id, source.getFactory()));
+		}
 
-		EntityKey key;
+		final EntityKey key;
 		if ( !useIdentityColumn ) {
-			key = new EntityKey( id, persister, source.getEntityMode() );
+			key = source.generateEntityKey( id, persister );
 			Object old = source.getPersistenceContext().getEntity( key );
 			if ( old != null ) {
 				if ( source.getPersistenceContext().getEntry( old ).getStatus() == Status.DELETED ) {
 					source.forceFlush( source.getPersistenceContext().getEntry( old ) );
 				}
 				else {
 					throw new NonUniqueObjectException( id, persister.getEntityName() );
 				}
 			}
 			persister.setIdentifier( entity, id, source );
 		}
 		else {
 			key = null;
 		}
 
 		if ( invokeSaveLifecycle( entity, persister, source ) ) {
 			return id; //EARLY EXIT
 		}
 
 		return performSaveOrReplicate(
 				entity,
 				key,
 				persister,
 				useIdentityColumn,
 				anything,
 				source,
 				requiresImmediateIdAccess
 		);
 	}
 
 	protected boolean invokeSaveLifecycle(Object entity, EntityPersister persister, EventSource source) {
 		// Sub-insertions should occur before containing insertion so
 		// Try to do the callback now
 		if ( persister.implementsLifecycle( source.getEntityMode() ) ) {
             LOG.debugf("Calling onSave()");
 			if ( ( ( Lifecycle ) entity ).onSave( source ) ) {
                 LOG.debugf("Insertion vetoed by onSave()");
 				return true;
 			}
 		}
 		return false;
 	}
 
 	/**
 	 * Performs all the actual work needed to save an entity (well to get the save moved to
 	 * the execution queue).
 	 *
 	 * @param entity The entity to be saved
 	 * @param key The id to be used for saving the entity (or null, in the case of identity columns)
 	 * @param persister The entity's persister instance.
 	 * @param useIdentityColumn Should an identity column be used for id generation?
 	 * @param anything Generally cascade-specific information.
 	 * @param source The session which is the source of the current event.
 	 * @param requiresImmediateIdAccess Is access to the identifier required immediately
 	 * after the completion of the save?  persist(), for example, does not require this...
 	 *
 	 * @return The id used to save the entity; may be null depending on the
 	 *         type of id generator used and the requiresImmediateIdAccess value
 	 */
 	protected Serializable performSaveOrReplicate(
 			Object entity,
 			EntityKey key,
 			EntityPersister persister,
 			boolean useIdentityColumn,
 			Object anything,
 			EventSource source,
 			boolean requiresImmediateIdAccess) {
 
 		Serializable id = key == null ? null : key.getIdentifier();
 
 		boolean inTxn = source.getTransactionCoordinator().isTransactionInProgress();
 		boolean shouldDelayIdentityInserts = !inTxn && !requiresImmediateIdAccess;
 
 		// Put a placeholder in entries, so we don't recurse back and try to save() the
 		// same object again. QUESTION: should this be done before onSave() is called?
 		// likewise, should it be done before onUpdate()?
 		source.getPersistenceContext().addEntry(
 				entity,
 				Status.SAVING,
 				null,
 				null,
 				id,
 				null,
 				LockMode.WRITE,
 				useIdentityColumn,
 				persister,
 				false,
 				false
 		);
 
 		cascadeBeforeSave( source, persister, entity, anything );
 
 		if ( useIdentityColumn && !shouldDelayIdentityInserts ) {
             LOG.trace("Executing insertions");
 			source.getActionQueue().executeInserts();
 		}
 
 		Object[] values = persister.getPropertyValuesToInsert( entity, getMergeMap( anything ), source );
 		Type[] types = persister.getPropertyTypes();
 
 		boolean substitute = substituteValuesIfNecessary( entity, id, values, persister, source );
 
 		if ( persister.hasCollections() ) {
 			substitute = substitute || visitCollectionsBeforeSave( entity, id, values, types, source );
 		}
 
 		if ( substitute ) {
 			persister.setPropertyValues( entity, values, source.getEntityMode() );
 		}
 
 		TypeHelper.deepCopy(
 				values,
 				types,
 				persister.getPropertyUpdateability(),
 				values,
 				source
 		);
 
 		new ForeignKeys.Nullifier( entity, false, useIdentityColumn, source )
 				.nullifyTransientReferences( values, types );
 		new Nullability( source ).checkNullability( values, persister, false );
 
 		if ( useIdentityColumn ) {
 			EntityIdentityInsertAction insert = new EntityIdentityInsertAction(
 					values, entity, persister, source, shouldDelayIdentityInserts
 			);
 			if ( !shouldDelayIdentityInserts ) {
                 LOG.debugf("Executing identity-insert immediately");
 				source.getActionQueue().execute( insert );
 				id = insert.getGeneratedId();
-				key = new EntityKey( id, persister, source.getEntityMode() );
+				key = source.generateEntityKey( id, persister );
 				source.getPersistenceContext().checkUniqueness( key, entity );
 			}
 			else {
                 LOG.debugf("Delaying identity-insert due to no transaction in progress");
 				source.getActionQueue().addAction( insert );
 				key = insert.getDelayedEntityKey();
 			}
 		}
 
 		Object version = Versioning.getVersion( values, persister );
 		source.getPersistenceContext().addEntity(
 				entity,
 				( persister.isMutable() ? Status.MANAGED : Status.READ_ONLY ),
 				values,
 				key,
 				version,
 				LockMode.WRITE,
 				useIdentityColumn,
 				persister,
 				isVersionIncrementDisabled(),
 				false
 		);
 		//source.getPersistenceContext().removeNonExist( new EntityKey( id, persister, source.getEntityMode() ) );
 
 		if ( !useIdentityColumn ) {
 			source.getActionQueue().addAction(
 					new EntityInsertAction( id, values, entity, version, persister, source )
 			);
 		}
 
 		cascadeAfterSave( source, persister, entity, anything );
 
 		markInterceptorDirty( entity, persister, source );
 
 		return id;
 	}
 
 	private void markInterceptorDirty(Object entity, EntityPersister persister, EventSource source) {
 		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
 			FieldInterceptor interceptor = FieldInterceptionHelper.injectFieldInterceptor(
 					entity,
 					persister.getEntityName(),
 					null,
 					source
 			);
 			interceptor.dirty();
 		}
 	}
 
 	protected Map getMergeMap(Object anything) {
 		return null;
 	}
 
 	/**
 	 * After the save, will te version number be incremented
 	 * if the instance is modified?
 	 *
 	 * @return True if the version will be incremented on an entity change after save;
 	 *         false otherwise.
 	 */
 	protected boolean isVersionIncrementDisabled() {
 		return false;
 	}
 
 	protected boolean visitCollectionsBeforeSave(Object entity, Serializable id, Object[] values, Type[] types, EventSource source) {
 		WrapVisitor visitor = new WrapVisitor( source );
 		// substitutes into values by side-effect
 		visitor.processEntityPropertyValues( values, types );
 		return visitor.isSubstitutionRequired();
 	}
 
 	/**
 	 * Perform any property value substitution that is necessary
 	 * (interceptor callback, version initialization...)
 	 *
 	 * @param entity The entity
 	 * @param id The entity identifier
 	 * @param values The snapshot entity state
 	 * @param persister The entity persister
 	 * @param source The originating session
 	 *
 	 * @return True if the snapshot state changed such that
 	 * reinjection of the values into the entity is required.
 	 */
 	protected boolean substituteValuesIfNecessary(
 			Object entity,
 			Serializable id,
 			Object[] values,
 			EntityPersister persister,
 			SessionImplementor source) {
 		boolean substitute = source.getInterceptor().onSave(
 				entity,
 				id,
 				values,
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 		);
 
 		//keep the existing version number in the case of replicate!
 		if ( persister.isVersioned() ) {
 			substitute = Versioning.seedVersion(
 					values,
 					persister.getVersionProperty(),
 					persister.getVersionType(),
 					source
 			) || substitute;
 		}
 		return substitute;
 	}
 
 	/**
 	 * Handles the calls needed to perform pre-save cascades for the given entity.
 	 *
 	 * @param source The session from whcih the save event originated.
 	 * @param persister The entity's persister instance.
 	 * @param entity The entity to be saved.
 	 * @param anything Generally cascade-specific data
 	 */
 	protected void cascadeBeforeSave(
 			EventSource source,
 			EntityPersister persister,
 			Object entity,
 			Object anything) {
 
 		// cascade-save to many-to-one BEFORE the parent is saved
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadeAction(), Cascade.BEFORE_INSERT_AFTER_DELETE, source )
 					.cascade( persister, entity, anything );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	/**
 	 * Handles to calls needed to perform post-save cascades.
 	 *
 	 * @param source The session from which the event originated.
 	 * @param persister The entity's persister instance.
 	 * @param entity The entity beng saved.
 	 * @param anything Generally cascade-specific data
 	 */
 	protected void cascadeAfterSave(
 			EventSource source,
 			EntityPersister persister,
 			Object entity,
 			Object anything) {
 
 		// cascade-save to collections AFTER the collection owner was saved
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadeAction(), Cascade.AFTER_INSERT_BEFORE_DELETE, source )
 					.cascade( persister, entity, anything );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	protected abstract CascadingAction getCascadeAction();
 
 	/**
 	 * Determine whether the entity is persistent, detached, or transient
 	 *
 	 * @param entity The entity to check
 	 * @param entityName The name of the entity
 	 * @param entry The entity's entry in the persistence context
 	 * @param source The originating session.
 	 *
 	 * @return The state.
 	 */
 	protected int getEntityState(
 			Object entity,
 			String entityName,
 			EntityEntry entry, //pass this as an argument only to avoid double looking
 			SessionImplementor source) {
 
 		if ( entry != null ) { // the object is persistent
 
 			//the entity is associated with the session, so check its status
 			if ( entry.getStatus() != Status.DELETED ) {
 				// do nothing for persistent instances
                 if (LOG.isTraceEnabled()) LOG.trace("Persistent instance of: " + getLoggableName(entityName, entity));
 				return PERSISTENT;
 			}
             // ie. e.status==DELETED
             if (LOG.isTraceEnabled()) LOG.trace("Deleted instance of: " + getLoggableName(entityName, entity));
             return DELETED;
 
 		}
         // the object is transient or detached
 
 		// the entity is not associated with the session, so
         // try interceptor and unsaved-value
 
 		if (ForeignKeys.isTransient(entityName, entity, getAssumedUnsaved(), source)) {
             if (LOG.isTraceEnabled()) LOG.trace("Transient instance of: " + getLoggableName(entityName, entity));
             return TRANSIENT;
 		}
         if (LOG.isTraceEnabled()) LOG.trace("Detached instance of: " + getLoggableName(entityName, entity));
         return DETACHED;
 	}
 
 	protected String getLoggableName(String entityName, Object entity) {
 		return entityName == null ? entity.getClass().getName() : entityName;
 	}
 
 	protected Boolean getAssumedUnsaved() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultDeleteEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultDeleteEventListener.java
index 301a0e4273..e324207bad 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultDeleteEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultDeleteEventListener.java
@@ -1,349 +1,351 @@
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
 import java.util.Set;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.CacheMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockMode;
 import org.hibernate.TransientObjectException;
 import org.hibernate.action.internal.EntityDeleteAction;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.Cascade;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.ForeignKeys;
 import org.hibernate.engine.Nullability;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.Status;
 import org.hibernate.event.DeleteEvent;
 import org.hibernate.event.DeleteEventListener;
 import org.hibernate.event.EventSource;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
-import org.jboss.logging.Logger;
 
 /**
  * Defines the default delete event listener used by hibernate for deleting entities
  * from the datastore in response to generated delete events.
  *
  * @author Steve Ebersole
  */
 public class DefaultDeleteEventListener implements DeleteEventListener {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        DefaultDeleteEventListener.class.getName());
 
 	/**
 	 * Handle the given delete event.
 	 *
 	 * @param event The delete event to be handled.
 	 *
 	 * @throws HibernateException
 	 */
 	public void onDelete(DeleteEvent event) throws HibernateException {
 		onDelete( event, new IdentitySet() );
 	}
 
 	/**
 	 * Handle the given delete event.  This is the cascaded form.
 	 *
 	 * @param event The delete event.
 	 * @param transientEntities The cache of entities already deleted
 	 *
 	 * @throws HibernateException
 	 */
 	public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
 
 		final EventSource source = event.getSession();
 
 		final PersistenceContext persistenceContext = source.getPersistenceContext();
 		Object entity = persistenceContext.unproxyAndReassociate( event.getObject() );
 
 		EntityEntry entityEntry = persistenceContext.getEntry( entity );
 		final EntityPersister persister;
 		final Serializable id;
 		final Object version;
 
 		if ( entityEntry == null ) {
             LOG.trace("Entity was not persistent in delete processing");
 
 			persister = source.getEntityPersister( event.getEntityName(), entity );
 
 			if ( ForeignKeys.isTransient( persister.getEntityName(), entity, null, source ) ) {
 				deleteTransientEntity( source, entity, event.isCascadeDeleteEnabled(), persister, transientEntities );
 				// EARLY EXIT!!!
 				return;
 			}
             performDetachedEntityDeletionCheck(event);
 
 			id = persister.getIdentifier( entity, source );
 
 			if ( id == null ) {
 				throw new TransientObjectException(
 						"the detached instance passed to delete() had a null identifier"
 				);
 			}
 
-			EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
+			final EntityKey key = source.generateEntityKey( id, persister );
 
 			persistenceContext.checkUniqueness( key, entity );
 
 			new OnUpdateVisitor( source, id, entity ).process( entity, persister );
 
 			version = persister.getVersion( entity, source.getEntityMode() );
 
 			entityEntry = persistenceContext.addEntity(
 					entity,
 					( persister.isMutable() ? Status.MANAGED : Status.READ_ONLY ),
 					persister.getPropertyValues( entity, source.getEntityMode() ),
 					key,
 					version,
 					LockMode.NONE,
 					true,
 					persister,
 					false,
 					false
 			);
 		}
 		else {
             LOG.trace("Deleting a persistent instance");
 
 			if ( entityEntry.getStatus() == Status.DELETED || entityEntry.getStatus() == Status.GONE ) {
                 LOG.trace("Object was already deleted");
 				return;
 			}
 			persister = entityEntry.getPersister();
 			id = entityEntry.getId();
 			version = entityEntry.getVersion();
 		}
 
 		/*if ( !persister.isMutable() ) {
 			throw new HibernateException(
 					"attempted to delete an object of immutable class: " +
 					MessageHelper.infoString(persister)
 				);
 		}*/
 
 		if ( invokeDeleteLifecycle( source, entity, persister ) ) {
 			return;
 		}
 
 		deleteEntity( source, entity, entityEntry, event.isCascadeDeleteEnabled(), persister, transientEntities );
 
 		if ( source.getFactory().getSettings().isIdentifierRollbackEnabled() ) {
 			persister.resetIdentifier( entity, id, version, source );
 		}
 	}
 
 	/**
 	 * Called when we have recognized an attempt to delete a detached entity.
 	 * <p/>
 	 * This is perfectly valid in Hibernate usage; JPA, however, forbids this.
 	 * Thus, this is a hook for HEM to affect this behavior.
 	 *
 	 * @param event The event.
 	 */
 	protected void performDetachedEntityDeletionCheck(DeleteEvent event) {
 		// ok in normal Hibernate usage to delete a detached entity; JPA however
 		// forbids it, thus this is a hook for HEM to affect this behavior
 	}
 
 	/**
 	 * We encountered a delete request on a transient instance.
 	 * <p/>
 	 * This is a deviation from historical Hibernate (pre-3.2) behavior to
 	 * align with the JPA spec, which states that transient entities can be
 	 * passed to remove operation in which case cascades still need to be
 	 * performed.
 	 *
 	 * @param session The session which is the source of the event
 	 * @param entity The entity being delete processed
 	 * @param cascadeDeleteEnabled Is cascading of deletes enabled
 	 * @param persister The entity persister
 	 * @param transientEntities A cache of already visited transient entities
 	 * (to avoid infinite recursion).
 	 */
 	protected void deleteTransientEntity(
 			EventSource session,
 			Object entity,
 			boolean cascadeDeleteEnabled,
 			EntityPersister persister,
 			Set transientEntities) {
         LOG.handlingTransientEntity();
 		if ( transientEntities.contains( entity ) ) {
             LOG.trace("Already handled transient entity; skipping");
 			return;
 		}
 		transientEntities.add( entity );
 		cascadeBeforeDelete( session, persister, entity, null, transientEntities );
 		cascadeAfterDelete( session, persister, entity, transientEntities );
 	}
 
 	/**
 	 * Perform the entity deletion.  Well, as with most operations, does not
 	 * really perform it; just schedules an action/execution with the
 	 * {@link org.hibernate.engine.ActionQueue} for execution during flush.
 	 *
 	 * @param session The originating session
 	 * @param entity The entity to delete
 	 * @param entityEntry The entity's entry in the {@link PersistenceContext}
 	 * @param isCascadeDeleteEnabled Is delete cascading enabled?
 	 * @param persister The entity persister.
 	 * @param transientEntities A cache of already deleted entities.
 	 */
 	protected final void deleteEntity(
 			final EventSource session,
 			final Object entity,
 			final EntityEntry entityEntry,
 			final boolean isCascadeDeleteEnabled,
 			final EntityPersister persister,
 			final Set transientEntities) {
 
         if (LOG.isTraceEnabled()) LOG.trace("Deleting "
                                             + MessageHelper.infoString(persister, entityEntry.getId(), session.getFactory()));
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final Type[] propTypes = persister.getPropertyTypes();
 		final Object version = entityEntry.getVersion();
 
 		final Object[] currentState;
 		if ( entityEntry.getLoadedState() == null ) { //ie. the entity came in from update()
 			currentState = persister.getPropertyValues( entity, session.getEntityMode() );
 		}
 		else {
 			currentState = entityEntry.getLoadedState();
 		}
 
 		final Object[] deletedState = createDeletedState( persister, currentState, session );
 		entityEntry.setDeletedState( deletedState );
 
 		session.getInterceptor().onDelete(
 				entity,
 				entityEntry.getId(),
 				deletedState,
 				persister.getPropertyNames(),
 				propTypes
 		);
 
 		// before any callbacks, etc, so subdeletions see that this deletion happened first
 		persistenceContext.setEntryStatus( entityEntry, Status.DELETED );
-		EntityKey key = new EntityKey( entityEntry.getId(), persister, session.getEntityMode() );
+		final EntityKey key = session.generateEntityKey( entityEntry.getId(), persister );
 
 		cascadeBeforeDelete( session, persister, entity, entityEntry, transientEntities );
 
 		new ForeignKeys.Nullifier( entity, true, false, session )
 				.nullifyTransientReferences( entityEntry.getDeletedState(), propTypes );
 		new Nullability( session ).checkNullability( entityEntry.getDeletedState(), persister, true );
 		persistenceContext.getNullifiableEntityKeys().add( key );
 
 		// Ensures that containing deletions happen before sub-deletions
 		session.getActionQueue().addAction(
 				new EntityDeleteAction(
 						entityEntry.getId(),
 						deletedState,
 						version,
 						entity,
 						persister,
 						isCascadeDeleteEnabled,
 						session
 				)
 		);
 
 		cascadeAfterDelete( session, persister, entity, transientEntities );
 
 		// the entry will be removed after the flush, and will no longer
 		// override the stale snapshot
 		// This is now handled by removeEntity() in EntityDeleteAction
 		//persistenceContext.removeDatabaseSnapshot(key);
 	}
 
 	private Object[] createDeletedState(EntityPersister persister, Object[] currentState, EventSource session) {
 		Type[] propTypes = persister.getPropertyTypes();
 		final Object[] deletedState = new Object[propTypes.length];
 //		TypeFactory.deepCopy( currentState, propTypes, persister.getPropertyUpdateability(), deletedState, session );
 		boolean[] copyability = new boolean[propTypes.length];
 		java.util.Arrays.fill( copyability, true );
 		TypeHelper.deepCopy( currentState, propTypes, copyability, deletedState, session );
 		return deletedState;
 	}
 
 	protected boolean invokeDeleteLifecycle(EventSource session, Object entity, EntityPersister persister) {
 		if ( persister.implementsLifecycle( session.getEntityMode() ) ) {
             LOG.debugf("Calling onDelete()");
 			if ( ( ( Lifecycle ) entity ).onDelete( session ) ) {
                 LOG.debugf("Deletion vetoed by onDelete()");
 				return true;
 			}
 		}
 		return false;
 	}
 
 	protected void cascadeBeforeDelete(
 			EventSource session,
 			EntityPersister persister,
 			Object entity,
 			EntityEntry entityEntry,
 			Set transientEntities) throws HibernateException {
 
 		CacheMode cacheMode = session.getCacheMode();
 		session.setCacheMode( CacheMode.GET );
 		session.getPersistenceContext().incrementCascadeLevel();
 		try {
 			// cascade-delete to collections BEFORE the collection owner is deleted
 			new Cascade( CascadingAction.DELETE, Cascade.AFTER_INSERT_BEFORE_DELETE, session )
 					.cascade( persister, entity, transientEntities );
 		}
 		finally {
 			session.getPersistenceContext().decrementCascadeLevel();
 			session.setCacheMode( cacheMode );
 		}
 	}
 
 	protected void cascadeAfterDelete(
 			EventSource session,
 			EntityPersister persister,
 			Object entity,
 			Set transientEntities) throws HibernateException {
 
 		CacheMode cacheMode = session.getCacheMode();
 		session.setCacheMode( CacheMode.GET );
 		session.getPersistenceContext().incrementCascadeLevel();
 		try {
 			// cascade-delete to many-to-one AFTER the parent was deleted
 			new Cascade( CascadingAction.DELETE, Cascade.BEFORE_INSERT_AFTER_DELETE, session )
 					.cascade( persister, entity, transientEntities );
 		}
 		finally {
 			session.getPersistenceContext().decrementCascadeLevel();
 			session.setCacheMode( cacheMode );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultEvictEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultEvictEventListener.java
index 97646fb5ca..db61e294ce 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultEvictEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultEvictEventListener.java
@@ -1,119 +1,119 @@
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
 import org.hibernate.HibernateLogger;
 import org.hibernate.engine.Cascade;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.EvictEvent;
 import org.hibernate.event.EvictEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.jboss.logging.Logger;
 
 /**
  * Defines the default evict event listener used by hibernate for evicting entities
  * in response to generated flush events.  In particular, this implementation will
  * remove any hard references to the entity that are held by the infrastructure
  * (references held by application or other persistent instances are okay)
  *
  * @author Steve Ebersole
  */
 public class DefaultEvictEventListener implements EvictEventListener {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        DefaultEvictEventListener.class.getName());
 
 	/**
 	 * Handle the given evict event.
 	 *
 	 * @param event The evict event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onEvict(EvictEvent event) throws HibernateException {
 		EventSource source = event.getSession();
 		final Object object = event.getObject();
 		final PersistenceContext persistenceContext = source.getPersistenceContext();
 
 		if ( object instanceof HibernateProxy ) {
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			Serializable id = li.getIdentifier();
 			EntityPersister persister = source.getFactory().getEntityPersister( li.getEntityName() );
 			if ( id == null ) {
 				throw new IllegalArgumentException("null identifier");
 			}
 
-			EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
+			final EntityKey key = source.generateEntityKey( id, persister );
 			persistenceContext.removeProxy( key );
 
 			if ( !li.isUninitialized() ) {
 				final Object entity = persistenceContext.removeEntity( key );
 				if ( entity != null ) {
 					EntityEntry e = event.getSession().getPersistenceContext().removeEntry( entity );
 					doEvict( entity, key, e.getPersister(), event.getSession() );
 				}
 			}
 			li.unsetSession();
 		}
 		else {
 			EntityEntry e = persistenceContext.removeEntry( object );
 			if ( e != null ) {
 				persistenceContext.removeEntity( e.getEntityKey() );
 				doEvict( object, e.getEntityKey(), e.getPersister(), source );
 			}
 		}
 	}
 
 	protected void doEvict(
 		final Object object,
 		final EntityKey key,
 		final EntityPersister persister,
 		final EventSource session)
 	throws HibernateException {
 
         if (LOG.isTraceEnabled()) LOG.trace("Evicting " + MessageHelper.infoString(persister));
 
 		// remove all collections for the entity from the session-level cache
 		if ( persister.hasCollections() ) {
 			new EvictVisitor( session ).process( object, persister );
 		}
 
 		// remove any snapshot, not really for memory management purposes, but
 		// rather because it might now be stale, and there is no longer any
 		// EntityEntry to take precedence
 		// This is now handled by removeEntity()
 		//session.getPersistenceContext().removeDatabaseSnapshot(key);
 
 		new Cascade( CascadingAction.EVICT, Cascade.AFTER_EVICT, session )
 				.cascade( persister, object );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java
index 2d96162a21..3c78156aeb 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultFlushEntityEventListener.java
@@ -1,564 +1,564 @@
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
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.action.internal.DelayedPostInsertIdentifier;
 import org.hibernate.action.internal.EntityUpdateAction;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.Nullability;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.engine.Versioning;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.FlushEntityEvent;
 import org.hibernate.event.FlushEntityEventListener;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
 
 /**
  * An event that occurs for each entity instance at flush time
  *
  * @author Gavin King
  */
 public class DefaultFlushEntityEventListener implements FlushEntityEventListener {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        DefaultFlushEntityEventListener.class.getName());
 
 	/**
 	 * make sure user didn't mangle the id
 	 */
 	public void checkId(
 			Object object,
 			EntityPersister persister,
 			Serializable id,
 			EntityMode entityMode,
 			SessionImplementor session) throws HibernateException {
 
 		if ( id != null && id instanceof DelayedPostInsertIdentifier ) {
 			// this is a situation where the entity id is assigned by a post-insert generator
 			// and was saved outside the transaction forcing it to be delayed
 			return;
 		}
 
 		if ( persister.canExtractIdOutOfEntity() ) {
 
 			Serializable oid = persister.getIdentifier( object, session );
 			if (id==null) {
 				throw new AssertionFailure("null id in " + persister.getEntityName() + " entry (don't flush the Session after an exception occurs)");
 			}
 			if ( !persister.getIdentifierType().isEqual( id, oid, entityMode, session.getFactory() ) ) {
 				throw new HibernateException(
 						"identifier of an instance of " +
 						persister.getEntityName() +
 						" was altered from " + id +
 						" to " + oid
 					);
 			}
 		}
 
 	}
 
 	private void checkNaturalId(
 			EntityPersister persister,
 	        EntityEntry entry,
 	        Object[] current,
 	        Object[] loaded,
 	        EntityMode entityMode,
 	        SessionImplementor session) {
 		if ( persister.hasNaturalIdentifier() && entry.getStatus() != Status.READ_ONLY ) {
  			Object[] snapshot = null;
 			Type[] types = persister.getPropertyTypes();
 			int[] props = persister.getNaturalIdentifierProperties();
 			boolean[] updateable = persister.getPropertyUpdateability();
 			for ( int i=0; i<props.length; i++ ) {
 				int prop = props[i];
 				if ( !updateable[prop] ) {
  					Object loadedVal;
  					if ( loaded == null ) {
  						if ( snapshot == null) {
  							snapshot = session.getPersistenceContext().getNaturalIdSnapshot( entry.getId(), persister );
  						}
  						loadedVal = snapshot[i];
  					} else {
  						loadedVal = loaded[prop];
  					}
  					if ( !types[prop].isEqual( current[prop], loadedVal, entityMode ) ) {
 						throw new HibernateException(
 								"immutable natural identifier of an instance of " +
 								persister.getEntityName() +
 								" was altered"
 							);
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Flushes a single entity's state to the database, by scheduling
 	 * an update action, if necessary
 	 */
 	public void onFlushEntity(FlushEntityEvent event) throws HibernateException {
 		final Object entity = event.getEntity();
 		final EntityEntry entry = event.getEntityEntry();
 		final EventSource session = event.getSession();
 		final EntityPersister persister = entry.getPersister();
 		final Status status = entry.getStatus();
 		final EntityMode entityMode = session.getEntityMode();
 		final Type[] types = persister.getPropertyTypes();
 
 		final boolean mightBeDirty = entry.requiresDirtyCheck(entity);
 
 		final Object[] values = getValues( entity, entry, entityMode, mightBeDirty, session );
 
 		event.setPropertyValues(values);
 
 		//TODO: avoid this for non-new instances where mightBeDirty==false
 		boolean substitute = wrapCollections( session, persister, types, values);
 
 		if ( isUpdateNecessary( event, mightBeDirty ) ) {
 			substitute = scheduleUpdate( event ) || substitute;
 		}
 
 		if ( status != Status.DELETED ) {
 			// now update the object .. has to be outside the main if block above (because of collections)
 			if (substitute) persister.setPropertyValues( entity, values, entityMode );
 
 			// Search for collections by reachability, updating their role.
 			// We don't want to touch collections reachable from a deleted object
 			if ( persister.hasCollections() ) {
 				new FlushVisitor(session, entity).processEntityPropertyValues(values, types);
 			}
 		}
 
 	}
 
 	private Object[] getValues(
 			Object entity,
 			EntityEntry entry,
 			EntityMode entityMode,
 			boolean mightBeDirty,
 	        SessionImplementor session) {
 		final Object[] loadedState = entry.getLoadedState();
 		final Status status = entry.getStatus();
 		final EntityPersister persister = entry.getPersister();
 
 		final Object[] values;
 		if ( status == Status.DELETED ) {
 			//grab its state saved at deletion
 			values = entry.getDeletedState();
 		}
 		else if ( !mightBeDirty && loadedState!=null ) {
 			values = loadedState;
 		}
 		else {
 			checkId( entity, persister, entry.getId(), entityMode, session );
 
 			// grab its current state
 			values = persister.getPropertyValues( entity, entityMode );
 
 			checkNaturalId( persister, entry, values, loadedState, entityMode, session );
 		}
 		return values;
 	}
 
 	private boolean wrapCollections(
 			EventSource session,
 			EntityPersister persister,
 			Type[] types,
 			Object[] values
 	) {
 		if ( persister.hasCollections() ) {
 
 			// wrap up any new collections directly referenced by the object
 			// or its components
 
 			// NOTE: we need to do the wrap here even if its not "dirty",
 			// because collections need wrapping but changes to _them_
 			// don't dirty the container. Also, for versioned data, we
 			// need to wrap before calling searchForDirtyCollections
 
 			WrapVisitor visitor = new WrapVisitor(session);
 			// substitutes into values by side-effect
 			visitor.processEntityPropertyValues(values, types);
 			return visitor.isSubstitutionRequired();
 		}
 		else {
 			return false;
 		}
 	}
 
 	private boolean isUpdateNecessary(final FlushEntityEvent event, final boolean mightBeDirty) {
 		final Status status = event.getEntityEntry().getStatus();
 		if ( mightBeDirty || status==Status.DELETED ) {
 			// compare to cached state (ignoring collections unless versioned)
 			dirtyCheck(event);
 			if ( isUpdateNecessary(event) ) {
 				return true;
 			}
 			else {
 				FieldInterceptionHelper.clearDirty( event.getEntity() );
 				return false;
 			}
 		}
 		else {
 			return hasDirtyCollections( event, event.getEntityEntry().getPersister(), status );
 		}
 	}
 
 	private boolean scheduleUpdate(final FlushEntityEvent event) {
 
 		final EntityEntry entry = event.getEntityEntry();
 		final EventSource session = event.getSession();
 		final Object entity = event.getEntity();
 		final Status status = entry.getStatus();
 		final EntityMode entityMode = session.getEntityMode();
 		final EntityPersister persister = entry.getPersister();
 		final Object[] values = event.getPropertyValues();
 
         if (LOG.isTraceEnabled()) {
 			if ( status == Status.DELETED ) {
                 if (!persister.isMutable()) LOG.trace("Updating immutable, deleted entity: "
                                                       + MessageHelper.infoString(persister, entry.getId(), session.getFactory()));
                 else if (!entry.isModifiableEntity()) LOG.trace("Updating non-modifiable, deleted entity: "
                                                                 + MessageHelper.infoString(persister,
                                                                                            entry.getId(),
                                                                                            session.getFactory()));
                 else LOG.trace("Updating deleted entity: "
                                + MessageHelper.infoString(persister, entry.getId(), session.getFactory()));
             } else LOG.trace("Updating entity: " + MessageHelper.infoString(persister, entry.getId(), session.getFactory()));
 		}
 
 		final boolean intercepted = !entry.isBeingReplicated() && handleInterception( event );
 
 		// increment the version number (if necessary)
 		final Object nextVersion = getNextVersion(event);
 
 		// if it was dirtied by a collection only
 		int[] dirtyProperties = event.getDirtyProperties();
 		if ( event.isDirtyCheckPossible() && dirtyProperties == null ) {
 			if ( ! intercepted && !event.hasDirtyCollection() ) {
 				throw new AssertionFailure( "dirty, but no dirty properties" );
 			}
 			dirtyProperties = ArrayHelper.EMPTY_INT_ARRAY;
 		}
 
 		// check nullability but do not doAfterTransactionCompletion command execute
 		// we'll use scheduled updates for that.
 		new Nullability(session).checkNullability( values, persister, true );
 
 		// schedule the update
 		// note that we intentionally do _not_ pass in currentPersistentState!
 		session.getActionQueue().addAction(
 				new EntityUpdateAction(
 						entry.getId(),
 						values,
 						dirtyProperties,
 						event.hasDirtyCollection(),
 						( status == Status.DELETED && ! entry.isModifiableEntity() ?
 								persister.getPropertyValues( entity, entityMode ) :
 								entry.getLoadedState() ),
 						entry.getVersion(),
 						nextVersion,
 						entity,
 						entry.getRowId(),
 						persister,
 						session
 					)
 			);
 
 		return intercepted;
 	}
 
 	protected boolean handleInterception(FlushEntityEvent event) {
 		SessionImplementor session = event.getSession();
 		EntityEntry entry = event.getEntityEntry();
 		EntityPersister persister = entry.getPersister();
 		Object entity = event.getEntity();
 
 		//give the Interceptor a chance to modify property values
 		final Object[] values = event.getPropertyValues();
 		final boolean intercepted = invokeInterceptor( session, entity, entry, values, persister );
 
 		//now we might need to recalculate the dirtyProperties array
 		if ( intercepted && event.isDirtyCheckPossible() && !event.isDirtyCheckHandledByInterceptor() ) {
 			int[] dirtyProperties;
 			if ( event.hasDatabaseSnapshot() ) {
 				dirtyProperties = persister.findModified( event.getDatabaseSnapshot(), values, entity, session );
 			}
 			else {
 				dirtyProperties = persister.findDirty( values, entry.getLoadedState(), entity, session );
 			}
 			event.setDirtyProperties(dirtyProperties);
 		}
 
 		return intercepted;
 	}
 
 	protected boolean invokeInterceptor(
 			SessionImplementor session,
 			Object entity,
 			EntityEntry entry,
 			final Object[] values,
 			EntityPersister persister) {
 		return session.getInterceptor().onFlushDirty(
 				entity,
 				entry.getId(),
 				values,
 				entry.getLoadedState(),
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 		);
 	}
 
 	/**
 	 * Convience method to retreive an entities next version value
 	 */
 	private Object getNextVersion(FlushEntityEvent event) throws HibernateException {
 
 		EntityEntry entry = event.getEntityEntry();
 		EntityPersister persister = entry.getPersister();
 		if ( persister.isVersioned() ) {
 
 			Object[] values = event.getPropertyValues();
 
 			if ( entry.isBeingReplicated() ) {
 				return Versioning.getVersion(values, persister);
 			}
 			else {
 				int[] dirtyProperties = event.getDirtyProperties();
 
 				final boolean isVersionIncrementRequired = isVersionIncrementRequired(
 						event,
 						entry,
 						persister,
 						dirtyProperties
 					);
 
 				final Object nextVersion = isVersionIncrementRequired ?
 						Versioning.increment( entry.getVersion(), persister.getVersionType(), event.getSession() ) :
 						entry.getVersion(); //use the current version
 
 				Versioning.setVersion(values, nextVersion, persister);
 
 				return nextVersion;
 			}
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private boolean isVersionIncrementRequired(
 			FlushEntityEvent event,
 			EntityEntry entry,
 			EntityPersister persister,
 			int[] dirtyProperties
 	) {
 		final boolean isVersionIncrementRequired = entry.getStatus()!=Status.DELETED && (
 				dirtyProperties==null ||
 				Versioning.isVersionIncrementRequired(
 						dirtyProperties,
 						event.hasDirtyCollection(),
 						persister.getPropertyVersionability()
 					)
 			);
 		return isVersionIncrementRequired;
 	}
 
 	/**
 	 * Performs all necessary checking to determine if an entity needs an SQL update
 	 * to synchronize its state to the database. Modifies the event by side-effect!
 	 * Note: this method is quite slow, avoid calling if possible!
 	 */
 	protected final boolean isUpdateNecessary(FlushEntityEvent event) throws HibernateException {
 
 		EntityPersister persister = event.getEntityEntry().getPersister();
 		Status status = event.getEntityEntry().getStatus();
 
 		if ( !event.isDirtyCheckPossible() ) {
 			return true;
 		}
 		else {
 
 			int[] dirtyProperties = event.getDirtyProperties();
 			if ( dirtyProperties!=null && dirtyProperties.length!=0 ) {
 				return true; //TODO: suck into event class
 			}
 			else {
 				return hasDirtyCollections( event, persister, status );
 			}
 
 		}
 	}
 
 	private boolean hasDirtyCollections(FlushEntityEvent event, EntityPersister persister, Status status) {
 		if ( isCollectionDirtyCheckNecessary(persister, status ) ) {
 			DirtyCollectionSearchVisitor visitor = new DirtyCollectionSearchVisitor(
 					event.getSession(),
 					persister.getPropertyVersionability()
 				);
 			visitor.processEntityPropertyValues( event.getPropertyValues(), persister.getPropertyTypes() );
 			boolean hasDirtyCollections = visitor.wasDirtyCollectionFound();
 			event.setHasDirtyCollection(hasDirtyCollections);
 			return hasDirtyCollections;
 		}
 		else {
 			return false;
 		}
 	}
 
 	private boolean isCollectionDirtyCheckNecessary(EntityPersister persister, Status status) {
 		return ( status == Status.MANAGED || status == Status.READ_ONLY ) &&
 				persister.isVersioned() &&
 				persister.hasCollections();
 	}
 
 	/**
 	 * Perform a dirty check, and attach the results to the event
 	 */
 	protected void dirtyCheck(FlushEntityEvent event) throws HibernateException {
 
 		final Object entity = event.getEntity();
 		final Object[] values = event.getPropertyValues();
 		final SessionImplementor session = event.getSession();
 		final EntityEntry entry = event.getEntityEntry();
 		final EntityPersister persister = entry.getPersister();
 		final Serializable id = entry.getId();
 		final Object[] loadedState = entry.getLoadedState();
 
 		int[] dirtyProperties = session.getInterceptor().findDirty(
 				entity,
 				id,
 				values,
 				loadedState,
 				persister.getPropertyNames(),
 				persister.getPropertyTypes()
 			);
 
 		event.setDatabaseSnapshot(null);
 
 		final boolean interceptorHandledDirtyCheck;
 		boolean cannotDirtyCheck;
 
 		if ( dirtyProperties==null ) {
 			// Interceptor returned null, so do the dirtycheck ourself, if possible
 			interceptorHandledDirtyCheck = false;
 
 			cannotDirtyCheck = loadedState==null; // object loaded by update()
 			if ( !cannotDirtyCheck ) {
 				// dirty check against the usual snapshot of the entity
 				dirtyProperties = persister.findDirty( values, loadedState, entity, session );
 			}
 			else if ( entry.getStatus() == Status.DELETED && ! event.getEntityEntry().isModifiableEntity() ) {
 				// A non-modifiable (e.g., read-only or immutable) entity needs to be have
 				// references to transient entities set to null before being deleted. No other
 				// fields should be updated.
 				if ( values != entry.getDeletedState() ) {
 					throw new IllegalStateException(
 							"Entity has status Status.DELETED but values != entry.getDeletedState"
 					);
 				}
 				// Even if loadedState == null, we can dirty-check by comparing currentState and
 				// entry.getDeletedState() because the only fields to be updated are those that
 				// refer to transient entities that are being set to null.
 				// - currentState contains the entity's current property values.
 				// - entry.getDeletedState() contains the entity's current property values with
 				//   references to transient entities set to null.
 				// - dirtyProperties will only contain properties that refer to transient entities
 				final Object[] currentState =
 						persister.getPropertyValues( event.getEntity(), event.getSession().getEntityMode() );
 				dirtyProperties = persister.findDirty( entry.getDeletedState(), currentState, entity, session );
 				cannotDirtyCheck = false;
 			}
 			else {
 				// dirty check against the database snapshot, if possible/necessary
 				final Object[] databaseSnapshot = getDatabaseSnapshot(session, persister, id);
 				if ( databaseSnapshot != null ) {
 					dirtyProperties = persister.findModified(databaseSnapshot, values, entity, session);
 					cannotDirtyCheck = false;
 					event.setDatabaseSnapshot(databaseSnapshot);
 				}
 			}
 		}
 		else {
 			// the Interceptor handled the dirty checking
 			cannotDirtyCheck = false;
 			interceptorHandledDirtyCheck = true;
 		}
 
 		logDirtyProperties( id, dirtyProperties, persister );
 
 		event.setDirtyProperties(dirtyProperties);
 		event.setDirtyCheckHandledByInterceptor(interceptorHandledDirtyCheck);
 		event.setDirtyCheckPossible(!cannotDirtyCheck);
 
 	}
 
 	private void logDirtyProperties(Serializable id, int[] dirtyProperties, EntityPersister persister) {
         if (LOG.isTraceEnabled() && dirtyProperties != null && dirtyProperties.length > 0) {
 			final String[] allPropertyNames = persister.getPropertyNames();
 			final String[] dirtyPropertyNames = new String[ dirtyProperties.length ];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				dirtyPropertyNames[i] = allPropertyNames[ dirtyProperties[i]];
 			}
             LOG.trace("Found dirty properties [" + MessageHelper.infoString(persister.getEntityName(), id) + "] : "
                       + dirtyPropertyNames);
 		}
 	}
 
 	private Object[] getDatabaseSnapshot(SessionImplementor session, EntityPersister persister, Serializable id) {
 		if ( persister.isSelectBeforeUpdateRequired() ) {
 			Object[] snapshot = session.getPersistenceContext()
 					.getDatabaseSnapshot(id, persister);
 			if (snapshot==null) {
 				//do we even really need this? the update will fail anyway....
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor()
 							.optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), id );
 			}
             return snapshot;
 		}
         // TODO: optimize away this lookup for entities w/o unsaved-value="undefined"
-        EntityKey entityKey = new EntityKey(id, persister, session.getEntityMode());
+        final EntityKey entityKey = session.generateEntityKey( id, persister );
         return session.getPersistenceContext().getCachedDatabaseSnapshot(entityKey);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
index 6bf9599746..124d796036 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
@@ -1,675 +1,659 @@
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
+
 import java.io.Serializable;
+
+import org.jboss.logging.Logger;
+
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
 import org.hibernate.event.LoadEvent;
 import org.hibernate.event.LoadEventListener;
 import org.hibernate.event.PostLoadEvent;
 import org.hibernate.event.PostLoadEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.type.EmbeddedComponentType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
-import org.jboss.logging.Logger;
 
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
 
-		EntityKey keyToLoad = new EntityKey( event.getEntityId(), persister, source.getEntityMode()  );
+		final  EntityKey keyToLoad = source.generateEntityKey( event.getEntityId(), persister );
 
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
-		final EntityKey parentEntityKey = new EntityKey(
-				event.getEntityId(),
-				parentPersister,
-				event.getSession().getEntityMode()
-		);
-		final Object parent = doLoad(
-				event,
-				parentPersister,
-				parentEntityKey,
-				options
-		);
+		final EntityKey parentEntityKey = event.getSession().generateEntityKey( event.getEntityId(), parentPersister );
+		final Object parent = doLoad( event, parentPersister, parentEntityKey, options );
 
-		Serializable dependent = (Serializable) dependentIdType.instantiate( parent, event.getSession() );
+		final Serializable dependent = (Serializable) dependentIdType.instantiate( parent, event.getSession() );
 		dependentIdType.setPropertyValues( dependent, new Object[] {parent}, event.getSession().getEntityMode() );
-		final EntityKey dependentEntityKey = new EntityKey(
-				dependent,
-				dependentPersister,
-				event.getSession().getEntityMode()
-		);
+		final EntityKey dependentEntityKey = event.getSession().generateEntityKey( dependent, dependentPersister );
 		event.setEntityId( dependent );
-		dependent = (Serializable) doLoad(
-				event,
-				dependentPersister,
-				dependentEntityKey,
-				options
-		);
 
-		event.setResult( dependent );
+		event.setResult( doLoad( event, dependentPersister, dependentEntityKey, options ) );
 	}
 
 	/**
-	 * Perfoms the load of an entity.
+	 * Performs the load of an entity.
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
 			ck = new CacheKey(
 					event.getEntityId(),
 					persister.getIdentifierType(),
 					persister.getRootEntityName(),
 					source.getEntityMode(),
 					source.getFactory()
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
 
 			final CacheKey ck = new CacheKey(
 					event.getEntityId(),
 					persister.getIdentifierType(),
 					persister.getRootEntityName(),
 					source.getEntityMode(),
 					source.getFactory()
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
-		EntityKey entityKey = new EntityKey( id, subclassPersister, session.getEntityMode() );
+		final EntityKey entityKey = session.generateEntityKey( id, subclassPersister );
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
 		PostLoadEvent postLoadEvent = new PostLoadEvent(session).setEntity(result)
 				.setId(id).setPersister(persister);
 		PostLoadEventListener[] listeners = session.getListeners().getPostLoadEventListeners();
 		for ( int i = 0; i < listeners.length; i++ ) {
 			listeners[i].onPostLoad(postLoadEvent);
 		}
 
 		return result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
index f4d49f6170..3f0391dd7a 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
@@ -1,663 +1,657 @@
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
 package org.hibernate.event.def;
+
 import java.io.Serializable;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.PropertyValueException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.WrongClassException;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
+import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.engine.Cascade;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.MergeEvent;
 import org.hibernate.event.MergeEventListener;
-import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
-import org.jboss.logging.Logger;
 
 /**
  * Defines the default copy event listener used by hibernate for copying entities
  * in response to generated copy events.
  *
  * @author Gavin King
  */
 public class DefaultMergeEventListener extends AbstractSaveEventListener
 	implements MergeEventListener {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        DefaultMergeEventListener.class.getName());
 
 	@Override
     protected Map getMergeMap(Object anything) {
 		return ( ( EventCache ) anything ).invertMap();
 	}
 
 	/**
 	 * Handle the given merge event.
 	 *
 	 * @param event The merge event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onMerge(MergeEvent event) throws HibernateException {
 		EventCache copyCache = new EventCache();
 		onMerge( event, copyCache );
 		// TODO: iteratively get transient entities and retry merge until one of the following conditions:
 		//       1) transientCopyCache.size() == 0
 		//       2) transientCopyCache.size() is not decreasing and copyCache.size() is not increasing
 		// TODO: find out if retrying can add entities to copyCache (don't think it can...)
 		// For now, just retry once; throw TransientObjectException if there are still any transient entities
 		Map transientCopyCache = getTransientCopyCache(event, copyCache );
 		if ( transientCopyCache.size() > 0 ) {
 			retryMergeTransientEntities( event, transientCopyCache, copyCache, true );
 			// find any entities that are still transient after retry
 			transientCopyCache = getTransientCopyCache(event, copyCache );
 			if ( transientCopyCache.size() > 0 ) {
 				Set transientEntityNames = new HashSet();
 				for( Iterator it=transientCopyCache.entrySet().iterator(); it.hasNext(); ) {
 					Object transientEntity = ( ( Map.Entry ) it.next() ).getKey();
 					String transientEntityName = event.getSession().guessEntityName( transientEntity );
 					transientEntityNames.add( transientEntityName );
                     LOG.trace("Transient instance could not be processed by merge when checking nullability: "
                               + transientEntityName + "[" + transientEntity + "]");
 				}
                 if (isNullabilityCheckedGlobal(event.getSession())) throw new TransientObjectException(
 						"one or more objects is an unsaved transient instance - save transient instance(s) before merging: " +
 						transientEntityNames );
                 LOG.trace("Retry saving transient instances without checking nullability");
                 // failures will be detected later...
                 retryMergeTransientEntities(event, transientCopyCache, copyCache, false);
 			}
 		}
 		copyCache.clear();
 		copyCache = null;
 	}
 
 	protected EventCache getTransientCopyCache(MergeEvent event, EventCache copyCache) {
 		EventCache transientCopyCache = new EventCache();
 		for ( Iterator it=copyCache.entrySet().iterator(); it.hasNext(); ) {
 			Map.Entry mapEntry = ( Map.Entry ) it.next();
 			Object entity = mapEntry.getKey();
 			Object copy = mapEntry.getValue();
 			if ( copy instanceof HibernateProxy ) {
 				copy = ( (HibernateProxy) copy ).getHibernateLazyInitializer().getImplementation();
 			}
 			EntityEntry copyEntry = event.getSession().getPersistenceContext().getEntry( copy );
 			if ( copyEntry == null ) {
 				// entity name will not be available for non-POJO entities
 				// TODO: cache the entity name somewhere so that it is available to this exception
                 LOG.trace("Transient instance could not be processed by merge: " + event.getSession().guessEntityName(copy) + "["
                           + entity + "]");
 				// merge did not cascade to this entity; it's in copyCache because a
 				// different entity has a non-nullable reference to this entity;
 				// this entity should not be put in transientCopyCache, because it was
 				// not included in the merge;
 				// if the global setting for checking nullability is false, the non-nullable
 				// reference to this entity will be detected later
 				if ( isNullabilityCheckedGlobal( event.getSession() ) ) {
 					throw new TransientObjectException(
 						"object is an unsaved transient instance - save the transient instance before merging: " +
 							event.getSession().guessEntityName( copy )
 					);
 				}
 			}
 			else if ( copyEntry.getStatus() == Status.SAVING ) {
 				transientCopyCache.put( entity, copy, copyCache.isOperatedOn( entity ) );
 			}
 			else if ( copyEntry.getStatus() != Status.MANAGED && copyEntry.getStatus() != Status.READ_ONLY ) {
 				throw new AssertionFailure( "Merged entity does not have status set to MANAGED or READ_ONLY; "+copy+" status="+copyEntry.getStatus() );
 			}
 		}
 		return transientCopyCache;
 	}
 
 	protected void retryMergeTransientEntities(
 			MergeEvent event,
 			Map transientCopyCache,
 			EventCache copyCache,
 			boolean isNullabilityChecked) {
 		// TODO: The order in which entities are saved may matter (e.g., a particular transient entity
 		//       may need to be saved before other transient entities can be saved;
 		//       Keep retrying the batch of transient entities until either:
 		//       1) there are no transient entities left in transientCopyCache
 		//       or 2) no transient entities were saved in the last batch
 		// For now, just run through the transient entities and retry the merge
 		for ( Iterator it=transientCopyCache.entrySet().iterator(); it.hasNext(); ) {
 			Map.Entry mapEntry = ( Map.Entry ) it.next();
 			Object entity = mapEntry.getKey();
 			Object copy = transientCopyCache.get( entity );
 			EntityEntry copyEntry = event.getSession().getPersistenceContext().getEntry( copy );
 			mergeTransientEntity(
 					entity,
 					copyEntry.getEntityName(),
 					( entity == event.getEntity() ? event.getRequestedId() : copyEntry.getId() ),
 					event.getSession(),
 					copyCache,
 					isNullabilityChecked
 			);
 		}
 	}
 
 	/**
 	 * Handle the given merge event.
 	 *
 	 * @param event The merge event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onMerge(MergeEvent event, Map copiedAlready) throws HibernateException {
 
 		final EventCache copyCache = ( EventCache ) copiedAlready;
 		final EventSource source = event.getSession();
 		final Object original = event.getOriginal();
 
 		if ( original != null ) {
 
 			final Object entity;
 			if ( original instanceof HibernateProxy ) {
 				LazyInitializer li = ( (HibernateProxy) original ).getHibernateLazyInitializer();
 				if ( li.isUninitialized() ) {
                     LOG.trace("Ignoring uninitialized proxy");
 					event.setResult( source.load( li.getEntityName(), li.getIdentifier() ) );
 					return; //EARLY EXIT!
 				}
 				else {
 					entity = li.getImplementation();
 				}
 			}
 			else {
 				entity = original;
 			}
 
 			if ( copyCache.containsKey( entity ) &&
 					( copyCache.isOperatedOn( entity ) ) ) {
                 LOG.trace("Already in merge process");
 				event.setResult( entity );
 			}
 			else {
 				if ( copyCache.containsKey( entity ) ) {
                     LOG.trace("Already in copyCache; setting in merge process");
 					copyCache.setOperatedOn( entity, true );
 				}
 				event.setEntity( entity );
 				int entityState = -1;
 
 				// Check the persistence context for an entry relating to this
 				// entity to be merged...
 				EntityEntry entry = source.getPersistenceContext().getEntry( entity );
 				if ( entry == null ) {
 					EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 					Serializable id = persister.getIdentifier( entity, source );
 					if ( id != null ) {
-						EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
-						Object managedEntity = source.getPersistenceContext().getEntity( key );
+						final EntityKey key = source.generateEntityKey( id, persister );
+						final Object managedEntity = source.getPersistenceContext().getEntity( key );
 						entry = source.getPersistenceContext().getEntry( managedEntity );
 						if ( entry != null ) {
 							// we have specialized case of a detached entity from the
 							// perspective of the merge operation.  Specifically, we
 							// have an incoming entity instance which has a corresponding
 							// entry in the current persistence context, but registered
 							// under a different entity instance
 							entityState = DETACHED;
 						}
 					}
 				}
 
 				if ( entityState == -1 ) {
 					entityState = getEntityState( entity, event.getEntityName(), entry, source );
 				}
 
 				switch (entityState) {
 					case DETACHED:
 						entityIsDetached(event, copyCache);
 						break;
 					case TRANSIENT:
 						entityIsTransient(event, copyCache);
 						break;
 					case PERSISTENT:
 						entityIsPersistent(event, copyCache);
 						break;
 					default: //DELETED
 						throw new ObjectDeletedException(
 								"deleted instance passed to merge",
 								null,
 								getLoggableName( event.getEntityName(), entity )
 							);
 				}
 			}
 
 		}
 
 	}
 
 	protected void entityIsPersistent(MergeEvent event, Map copyCache) {
         LOG.trace("Ignoring persistent instance");
 
 		//TODO: check that entry.getIdentifier().equals(requestedId)
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 
 		( ( EventCache ) copyCache ).put( entity, entity, true  );  //before cascade!
 
 		cascadeOnMerge(source, persister, entity, copyCache);
 		copyValues(persister, entity, entity, source, copyCache);
 
 		event.setResult(entity);
 	}
 
 	protected void entityIsTransient(MergeEvent event, Map copyCache) {
 
         LOG.trace("Merging transient instance");
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 		final String entityName = persister.getEntityName();
 
 		event.setResult( mergeTransientEntity( entity, entityName, event.getRequestedId(), source, copyCache, true ) );
 	}
 
 	protected Object mergeTransientEntity(Object entity, String entityName, Serializable requestedId, EventSource source, Map copyCache) {
 		return mergeTransientEntity( entity, entityName, requestedId, source, copyCache, true );
 	}
 
 	private Object mergeTransientEntity(
 			Object entity,
 			String entityName,
 			Serializable requestedId,
 			EventSource source,
 			Map copyCache,
 			boolean isNullabilityChecked) {
 
         LOG.trace("Merging transient instance");
 
 		final EntityPersister persister = source.getEntityPersister( entityName, entity );
 
 		final Serializable id = persister.hasIdentifierProperty() ?
 				persister.getIdentifier( entity, source ) :
 		        null;
 		if ( copyCache.containsKey( entity ) ) {
 			persister.setIdentifier( copyCache.get( entity ), id, source );
 		}
 		else {
 			( ( EventCache ) copyCache ).put( entity, source.instantiate( persister, id ), true ); //before cascade!
 		}
 		final Object copy = copyCache.get( entity );
 
 		// cascade first, so that all unsaved objects get their
 		// copy created before we actually copy
 		//cascadeOnMerge(event, persister, entity, copyCache, Cascades.CASCADE_BEFORE_MERGE);
 		super.cascadeBeforeSave(source, persister, entity, copyCache);
 		copyValues(persister, entity, copy, source, copyCache, ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT);
 
 		try {
 			// try saving; check for non-nullable properties that are null or transient entities before saving
 			saveTransientEntity( copy, entityName, requestedId, source, copyCache, isNullabilityChecked );
 		}
 		catch (PropertyValueException ex) {
 			String propertyName = ex.getPropertyName();
 			Object propertyFromCopy = persister.getPropertyValue( copy, propertyName, source.getEntityMode() );
 			Object propertyFromEntity = persister.getPropertyValue( entity, propertyName, source.getEntityMode() );
 			Type propertyType = persister.getPropertyType( propertyName );
 			EntityEntry copyEntry = source.getPersistenceContext().getEntry( copy );
 			if ( propertyFromCopy == null ||
 					propertyFromEntity == null ||
 					! propertyType.isEntityType() ||
 					! copyCache.containsKey( propertyFromEntity ) ) {
 				if ( LOG.isTraceEnabled() ) {
                     LOG.trace("Property '" + copyEntry.getEntityName() + "." + propertyName + "' in copy is "
                               + (propertyFromCopy == null ? "null" : propertyFromCopy));
                     LOG.trace("Property '" + copyEntry.getEntityName() + "." + propertyName + "' in original is "
                               + (propertyFromCopy == null ? "null" : propertyFromCopy));
                     LOG.trace("Property '" + copyEntry.getEntityName() + "." + propertyName + "' is"
                               + (propertyType.isEntityType() ? "" : " not") + " an entity type");
                     if (propertyFromEntity != null && !copyCache.containsKey(propertyFromEntity)) LOG.trace("Property '"
                                                                                                             + copyEntry.getEntityName()
                                                                                                             + "."
                                                                                                             + propertyName
                                                                                                             + "' is not in copy cache");
 	            }
                 if ( isNullabilityCheckedGlobal( source ) ) {
                     throw ex;
                 }
                 else {
                     // retry save w/o checking for non-nullable properties
                     // (the failure will be detected later)
                     saveTransientEntity( copy, entityName, requestedId, source, copyCache, false );
 				}
 			}
 			if ( LOG.isTraceEnabled() && propertyFromEntity != null ) {
                 if (((EventCache)copyCache).isOperatedOn(propertyFromEntity)) LOG.trace("Property '"
                                                                                         + copyEntry.getEntityName()
                                                                                         + "."
                                                                                         + propertyName
                                                                                         + "' from original entity is in copyCache and is in the process of being merged; "
                                                                                         + propertyName + " =[" + propertyFromEntity
                                                                                         + "]");
                 else LOG.trace("Property '" + copyEntry.getEntityName() + "." + propertyName
                                + "' from original entity is in copyCache and is not in the process of being merged; "
                                + propertyName + " =[" + propertyFromEntity + "]");
 			}
 			// continue...; we'll find out if it ends up not getting saved later
 		}
 
 		// cascade first, so that all unsaved objects get their
 		// copy created before we actually copy
 		super.cascadeAfterSave(source, persister, entity, copyCache);
 		copyValues(persister, entity, copy, source, copyCache, ForeignKeyDirection.FOREIGN_KEY_TO_PARENT);
 
 		return copy;
 
 	}
 
 	private boolean isNullabilityCheckedGlobal(EventSource source) {
 		return source.getFactory().getSettings().isCheckNullability();
 	}
 
 	private void saveTransientEntity(
 			Object entity,
 			String entityName,
 			Serializable requestedId,
 			EventSource source,
 			Map copyCache,
 			boolean isNullabilityChecked) {
 
 		boolean isNullabilityCheckedOrig =
 			source.getFactory().getSettings().isCheckNullability();
 		try {
 			source.getFactory().getSettings().setCheckNullability( isNullabilityChecked );
 			//this bit is only *really* absolutely necessary for handling
 			//requestedId, but is also good if we merge multiple object
 			//graphs, since it helps ensure uniqueness
 			if (requestedId==null) {
 				saveWithGeneratedId( entity, entityName, copyCache, source, false );
 			}
 			else {
 				saveWithRequestedId( entity, requestedId, entityName, copyCache, source );
 			}
 		}
 		finally {
 			source.getFactory().getSettings().setCheckNullability( isNullabilityCheckedOrig );
 		}
 	}
 	protected void entityIsDetached(MergeEvent event, Map copyCache) {
 
         LOG.trace("Merging detached instance");
 
 		final Object entity = event.getEntity();
 		final EventSource source = event.getSession();
 
 		final EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 		final String entityName = persister.getEntityName();
 
 		Serializable id = event.getRequestedId();
 		if ( id == null ) {
 			id = persister.getIdentifier( entity, source );
 		}
 		else {
 			// check that entity id = requestedId
 			Serializable entityId = persister.getIdentifier( entity, source );
 			if ( !persister.getIdentifierType().isEqual( id, entityId, source.getEntityMode(), source.getFactory() ) ) {
 				throw new HibernateException( "merge requested with id not matching id of passed entity" );
 			}
 		}
 
 		String previousFetchProfile = source.getFetchProfile();
 		source.setFetchProfile("merge");
 		//we must clone embedded composite identifiers, or
 		//we will get back the same instance that we pass in
 		final Serializable clonedIdentifier = (Serializable) persister.getIdentifierType()
 				.deepCopy( id, source.getEntityMode(), source.getFactory() );
 		final Object result = source.get(entityName, clonedIdentifier);
 		source.setFetchProfile(previousFetchProfile);
 
 		if ( result == null ) {
 			//TODO: we should throw an exception if we really *know* for sure
 			//      that this is a detached instance, rather than just assuming
 			//throw new StaleObjectStateException(entityName, id);
 
 			// we got here because we assumed that an instance
 			// with an assigned id was detached, when it was
 			// really persistent
 			entityIsTransient(event, copyCache);
 		}
 		else {
 			( ( EventCache ) copyCache ).put( entity, result, true ); //before cascade!
 
 			final Object target = source.getPersistenceContext().unproxy(result);
 			if ( target == entity ) {
 				throw new AssertionFailure("entity was not detached");
 			}
 			else if ( !source.getEntityName(target).equals(entityName) ) {
 				throw new WrongClassException(
 						"class of the given object did not match class of persistent copy",
 						event.getRequestedId(),
 						entityName
 					);
 			}
 			else if ( isVersionChanged( entity, source, persister, target ) ) {
 				if ( source.getFactory().getStatistics().isStatisticsEnabled() ) {
 					source.getFactory().getStatisticsImplementor()
 							.optimisticFailure( entityName );
 				}
 				throw new StaleObjectStateException( entityName, id );
 			}
 
 			// cascade first, so that all unsaved objects get their
 			// copy created before we actually copy
 			cascadeOnMerge(source, persister, entity, copyCache);
 			copyValues(persister, entity, target, source, copyCache);
 
 			//copyValues works by reflection, so explicitly mark the entity instance dirty
 			markInterceptorDirty( entity, target );
 
 			event.setResult(result);
 		}
 
 	}
 
 	private void markInterceptorDirty(final Object entity, final Object target) {
 		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
 			FieldInterceptor interceptor = FieldInterceptionHelper.extractFieldInterceptor( target );
 			if ( interceptor != null ) {
 				interceptor.dirty();
 			}
 		}
 	}
 
 	private boolean isVersionChanged(Object entity, EventSource source, EntityPersister persister, Object target) {
 		if ( ! persister.isVersioned() ) {
 			return false;
 		}
 		// for merging of versioned entities, we consider the version having
 		// been changed only when:
 		// 1) the two version values are different;
 		//      *AND*
 		// 2) The target actually represents database state!
 		//
 		// This second condition is a special case which allows
 		// an entity to be merged during the same transaction
 		// (though during a seperate operation) in which it was
 		// originally persisted/saved
 		boolean changed = ! persister.getVersionType().isSame(
 				persister.getVersion( target, source.getEntityMode() ),
 				persister.getVersion( entity, source.getEntityMode() ),
 				source.getEntityMode()
 		);
 
 		// TODO : perhaps we should additionally require that the incoming entity
 		// version be equivalent to the defined unsaved-value?
 		return changed && existsInDatabase( target, source, persister );
 	}
 
 	private boolean existsInDatabase(Object entity, EventSource source, EntityPersister persister) {
 		EntityEntry entry = source.getPersistenceContext().getEntry( entity );
 		if ( entry == null ) {
 			Serializable id = persister.getIdentifier( entity, source );
 			if ( id != null ) {
-				EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
-				Object managedEntity = source.getPersistenceContext().getEntity( key );
+				final EntityKey key = source.generateEntityKey( id, persister );
+				final Object managedEntity = source.getPersistenceContext().getEntity( key );
 				entry = source.getPersistenceContext().getEntry( managedEntity );
 			}
 		}
 
-		if ( entry == null ) {
-			// perhaps this should be an exception since it is only ever used
-			// in the above method?
-			return false;
-		}
-		else {
-			return entry.isExistsInDatabase();
-		}
+		return entry != null && entry.isExistsInDatabase();
 	}
 
 	protected void copyValues(
-		final EntityPersister persister,
-		final Object entity,
-		final Object target,
-		final SessionImplementor source,
-		final Map copyCache
-	) {
+			final EntityPersister persister,
+			final Object entity,
+			final Object target,
+			final SessionImplementor source,
+			final Map copyCache) {
 		final Object[] copiedValues = TypeHelper.replace(
 				persister.getPropertyValues( entity, source.getEntityMode() ),
 				persister.getPropertyValues( target, source.getEntityMode() ),
 				persister.getPropertyTypes(),
 				source,
 				target,
 				copyCache
-			);
+		);
 
 		persister.setPropertyValues( target, copiedValues, source.getEntityMode() );
 	}
 
 	protected void copyValues(
 			final EntityPersister persister,
 			final Object entity,
 			final Object target,
 			final SessionImplementor source,
 			final Map copyCache,
 			final ForeignKeyDirection foreignKeyDirection) {
 
 		final Object[] copiedValues;
 
 		if ( foreignKeyDirection == ForeignKeyDirection.FOREIGN_KEY_TO_PARENT ) {
 			// this is the second pass through on a merge op, so here we limit the
 			// replacement to associations types (value types were already replaced
 			// during the first pass)
 			copiedValues = TypeHelper.replaceAssociations(
 					persister.getPropertyValues( entity, source.getEntityMode() ),
 					persister.getPropertyValues( target, source.getEntityMode() ),
 					persister.getPropertyTypes(),
 					source,
 					target,
 					copyCache,
 					foreignKeyDirection
 			);
 		}
 		else {
 			copiedValues = TypeHelper.replace(
 					persister.getPropertyValues( entity, source.getEntityMode() ),
 					persister.getPropertyValues( target, source.getEntityMode() ),
 					persister.getPropertyTypes(),
 					source,
 					target,
 					copyCache,
 					foreignKeyDirection
 			);
 		}
 
 		persister.setPropertyValues( target, copiedValues, source.getEntityMode() );
 	}
 
 	/**
 	 * Perform any cascades needed as part of this copy event.
 	 *
 	 * @param source The merge event being processed.
 	 * @param persister The persister of the entity being copied.
 	 * @param entity The entity being copied.
 	 * @param copyCache A cache of already copied instance.
 	 */
 	protected void cascadeOnMerge(
 		final EventSource source,
 		final EntityPersister persister,
 		final Object entity,
 		final Map copyCache
 	) {
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( getCascadeAction(), Cascade.BEFORE_MERGE, source )
 					.cascade(persister, entity, copyCache);
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 
 	@Override
     protected CascadingAction getCascadeAction() {
 		return CascadingAction.MERGE;
 	}
 
 	@Override
     protected Boolean getAssumedUnsaved() {
 		return Boolean.FALSE;
 	}
 
 	/**
 	 * Cascade behavior is redefined by this subclass, disable superclass behavior
 	 */
 	@Override
     protected void cascadeAfterSave(EventSource source, EntityPersister persister, Object entity, Object anything)
 	throws HibernateException {
 	}
 
 	/**
 	 * Cascade behavior is redefined by this subclass, disable superclass behavior
 	 */
 	@Override
     protected void cascadeBeforeSave(EventSource source, EntityPersister persister, Object entity, Object anything)
 	throws HibernateException {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultRefreshEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultRefreshEventListener.java
index 42f3976854..16eadc5e2a 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultRefreshEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultRefreshEventListener.java
@@ -1,176 +1,178 @@
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
 import java.util.Map;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.engine.Cascade;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.RefreshEvent;
 import org.hibernate.event.RefreshEventListener;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
-import org.jboss.logging.Logger;
 
 /**
  * Defines the default refresh event listener used by hibernate for refreshing entities
  * in response to generated refresh events.
  *
  * @author Steve Ebersole
  */
 public class DefaultRefreshEventListener implements RefreshEventListener {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        DefaultRefreshEventListener.class.getName());
 
 	public void onRefresh(RefreshEvent event) throws HibernateException {
 		onRefresh( event, IdentityMap.instantiate(10) );
 	}
 
 	/**
 	 * Handle the given refresh event.
 	 *
 	 * @param event The refresh event to be handled.
 	 */
 	public void onRefresh(RefreshEvent event, Map refreshedAlready) {
 
 		final EventSource source = event.getSession();
 
 		boolean isTransient = ! source.contains( event.getObject() );
 		if ( source.getPersistenceContext().reassociateIfUninitializedProxy( event.getObject() ) ) {
 			if ( isTransient ) {
 				source.setReadOnly( event.getObject(), source.isDefaultReadOnly() );
 			}
 			return;
 		}
 
 		final Object object = source.getPersistenceContext().unproxyAndReassociate( event.getObject() );
 
 		if ( refreshedAlready.containsKey(object) ) {
             LOG.trace("Already refreshed");
 			return;
 		}
 
 		final EntityEntry e = source.getPersistenceContext().getEntry( object );
 		final EntityPersister persister;
 		final Serializable id;
 
 		if ( e == null ) {
 			persister = source.getEntityPersister(null, object); //refresh() does not pass an entityName
 			id = persister.getIdentifier( object, event.getSession() );
             if (LOG.isTraceEnabled()) LOG.trace("Refreshing transient "
                                                 + MessageHelper.infoString(persister, id, source.getFactory()));
-			EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
+			final EntityKey key = source.generateEntityKey( id, persister );
 			if ( source.getPersistenceContext().getEntry(key) != null ) {
 				throw new PersistentObjectException(
 						"attempted to refresh transient instance when persistent instance was already associated with the Session: " +
 						MessageHelper.infoString(persister, id, source.getFactory() )
 					);
 			}
 		}
 		else {
             if (LOG.isTraceEnabled()) LOG.trace("Refreshing "
                                                 + MessageHelper.infoString(e.getPersister(), e.getId(), source.getFactory()));
 			if ( !e.isExistsInDatabase() ) {
 				throw new HibernateException( "this instance does not yet exist as a row in the database" );
 			}
 
 			persister = e.getPersister();
 			id = e.getId();
 		}
 
 		// cascade the refresh prior to refreshing this entity
 		refreshedAlready.put(object, object);
 		new Cascade(CascadingAction.REFRESH, Cascade.BEFORE_REFRESH, source)
 				.cascade( persister, object, refreshedAlready );
 
 		if ( e != null ) {
-			EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
+			final EntityKey key = source.generateEntityKey( id, persister );
 			source.getPersistenceContext().removeEntity(key);
 			if ( persister.hasCollections() ) new EvictVisitor( source ).process(object, persister);
 		}
 
 		if ( persister.hasCache() ) {
 			final CacheKey ck = new CacheKey(
 					id,
 					persister.getIdentifierType(),
 					persister.getRootEntityName(),
 					source.getEntityMode(),
 					source.getFactory()
 			);
 			persister.getCacheAccessStrategy().evict( ck );
 		}
 
 		evictCachedCollections( persister, id, source.getFactory() );
 
 		String previousFetchProfile = source.getFetchProfile();
 		source.setFetchProfile("refresh");
 		Object result = persister.load( id, object, event.getLockOptions(), source );
 		// Keep the same read-only/modifiable setting for the entity that it had before refreshing;
 		// If it was transient, then set it to the default for the source.
 		if ( result != null ) {
 			if ( ! persister.isMutable() ) {
 				// this is probably redundant; it should already be read-only
 				source.setReadOnly( result, true );
 			}
 			else {
 				source.setReadOnly( result, ( e == null ? source.isDefaultReadOnly() : e.isReadOnly() ) );
 			}
 		}
 		source.setFetchProfile(previousFetchProfile);
 
 		UnresolvableObjectException.throwIfNull( result, id, persister.getEntityName() );
 
 	}
 
 	private void evictCachedCollections(EntityPersister persister, Serializable id, SessionFactoryImplementor factory) {
 		evictCachedCollections( persister.getPropertyTypes(), id, factory );
 	}
 
 	private void evictCachedCollections(Type[] types, Serializable id, SessionFactoryImplementor factory)
 	throws HibernateException {
 		for ( int i = 0; i < types.length; i++ ) {
 			if ( types[i].isCollectionType() ) {
 				factory.evictCollection( ( (CollectionType) types[i] ).getRole(), id );
 			}
 			else if ( types[i].isComponentType() ) {
 				CompositeType actype = (CompositeType) types[i];
 				evictCachedCollections( actype.getSubtypes(), id, factory );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultReplicateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultReplicateEventListener.java
index 29f8a1d0ad..871972b45a 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultReplicateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultReplicateEventListener.java
@@ -1,215 +1,216 @@
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
 package org.hibernate.event.def;
+
 import java.io.Serializable;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockMode;
 import org.hibernate.ReplicationMode;
 import org.hibernate.TransientObjectException;
 import org.hibernate.engine.Cascade;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.ReplicateEvent;
 import org.hibernate.event.ReplicateEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.Type;
-import org.jboss.logging.Logger;
 
 /**
  * Defines the default replicate event listener used by Hibernate to replicate
  * entities in response to generated replicate events.
  *
  * @author Steve Ebersole
  */
 public class DefaultReplicateEventListener extends AbstractSaveEventListener implements ReplicateEventListener {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        DefaultReplicateEventListener.class.getName());
 
 	/**
 	 * Handle the given replicate event.
 	 *
 	 * @param event The replicate event to be handled.
 	 *
 	 * @throws TransientObjectException An invalid attempt to replicate a transient entity.
 	 */
 	public void onReplicate(ReplicateEvent event) {
 		final EventSource source = event.getSession();
 		if ( source.getPersistenceContext().reassociateIfUninitializedProxy( event.getObject() ) ) {
             LOG.trace("Uninitialized proxy passed to replicate()");
 			return;
 		}
 
 		Object entity = source.getPersistenceContext().unproxyAndReassociate( event.getObject() );
 
 		if ( source.getPersistenceContext().isEntryFor( entity ) ) {
             LOG.trace("Ignoring persistent instance passed to replicate()");
 			//hum ... should we cascade anyway? throw an exception? fine like it is?
 			return;
 		}
 
 		EntityPersister persister = source.getEntityPersister( event.getEntityName(), entity );
 
 		// get the id from the object
 		/*if ( persister.isUnsaved(entity, source) ) {
 			throw new TransientObjectException("transient instance passed to replicate()");
 		}*/
 		Serializable id = persister.getIdentifier( entity, source );
 		if ( id == null ) {
 			throw new TransientObjectException( "instance with null id passed to replicate()" );
 		}
 
 		final ReplicationMode replicationMode = event.getReplicationMode();
 
 		final Object oldVersion;
 		if ( replicationMode == ReplicationMode.EXCEPTION ) {
 			//always do an INSERT, and let it fail by constraint violation
 			oldVersion = null;
 		}
 		else {
 			//what is the version on the database?
 			oldVersion = persister.getCurrentVersion( id, source );
 		}
 
 		if ( oldVersion != null ) {
             if (LOG.isTraceEnabled()) LOG.trace("Found existing row for "
                                                 + MessageHelper.infoString(persister, id, source.getFactory()));
 
 			/// HHH-2378
 			final Object realOldVersion = persister.isVersioned() ? oldVersion : null;
 
 			boolean canReplicate = replicationMode.shouldOverwriteCurrentVersion(
 					entity,
 					realOldVersion,
 					persister.getVersion( entity, source.getEntityMode() ),
 					persister.getVersionType()
 			);
 
             // if can replicate, will result in a SQL UPDATE
             // else do nothing (don't even reassociate object!)
             if (canReplicate) performReplication(entity, id, realOldVersion, persister, replicationMode, source);
             else LOG.trace("No need to replicate");
 
 			//TODO: would it be better to do a refresh from db?
 		}
 		else {
 			// no existing row - do an insert
             if (LOG.isTraceEnabled()) LOG.trace("No existing row, replicating new instance "
                                                 + MessageHelper.infoString(persister, id, source.getFactory()));
 
 			final boolean regenerate = persister.isIdentifierAssignedByInsert(); // prefer re-generation of identity!
-			final EntityKey key = regenerate ?
-					null : new EntityKey( id, persister, source.getEntityMode() );
+			final EntityKey key = regenerate ? null : source.generateEntityKey( id, persister );
 
 			performSaveOrReplicate(
 					entity,
 					key,
 					persister,
 					regenerate,
 					replicationMode,
 					source,
 					true
 			);
 
 		}
 	}
 
 	@Override
     protected boolean visitCollectionsBeforeSave(Object entity, Serializable id, Object[] values, Type[] types, EventSource source) {
 		//TODO: we use two visitors here, inefficient!
 		OnReplicateVisitor visitor = new OnReplicateVisitor( source, id, entity, false );
 		visitor.processEntityPropertyValues( values, types );
 		return super.visitCollectionsBeforeSave( entity, id, values, types, source );
 	}
 
 	@Override
     protected boolean substituteValuesIfNecessary(
 			Object entity,
 			Serializable id,
 			Object[] values,
 			EntityPersister persister,
 			SessionImplementor source) {
 		return false;
 	}
 
 	@Override
     protected boolean isVersionIncrementDisabled() {
 		return true;
 	}
 
 	private void performReplication(
 			Object entity,
 			Serializable id,
 			Object version,
 			EntityPersister persister,
 			ReplicationMode replicationMode,
 			EventSource source) throws HibernateException {
 
         if (LOG.isTraceEnabled()) LOG.trace("Replicating changes to "
                                             + MessageHelper.infoString(persister, id, source.getFactory()));
 
 		new OnReplicateVisitor( source, id, entity, true ).process( entity, persister );
 
 		source.getPersistenceContext().addEntity(
 				entity,
 				( persister.isMutable() ? Status.MANAGED : Status.READ_ONLY ),
 				null,
-				new EntityKey( id, persister, source.getEntityMode() ),
+				source.generateEntityKey( id, persister ),
 				version,
 				LockMode.NONE,
 				true,
 				persister,
 				true,
 				false
 		);
 
 		cascadeAfterReplicate( entity, persister, replicationMode, source );
 	}
 
 	private void cascadeAfterReplicate(
 			Object entity,
 			EntityPersister persister,
 			ReplicationMode replicationMode,
 			EventSource source) {
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( CascadingAction.REPLICATE, Cascade.AFTER_UPDATE, source )
 					.cascade( persister, entity, replicationMode );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	@Override
     protected CascadingAction getCascadeAction() {
 		return CascadingAction.REPLICATE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java
index 38709dc98e..e8d9d7b565 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java
@@ -1,364 +1,365 @@
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
 package org.hibernate.event.def;
+
 import java.io.Serializable;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockMode;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.Cascade;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.SaveOrUpdateEvent;
 import org.hibernate.event.SaveOrUpdateEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
-import org.jboss.logging.Logger;
 
 /**
  * Defines the default listener used by Hibernate for handling save-update
  * events.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public class DefaultSaveOrUpdateEventListener extends AbstractSaveEventListener implements SaveOrUpdateEventListener {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        DefaultSaveOrUpdateEventListener.class.getName());
 
 	/**
 	 * Handle the given update event.
 	 *
 	 * @param event The update event to be handled.
 	 */
 	public void onSaveOrUpdate(SaveOrUpdateEvent event) {
 		final SessionImplementor source = event.getSession();
 		final Object object = event.getObject();
 		final Serializable requestedId = event.getRequestedId();
 
 		if ( requestedId != null ) {
 			//assign the requested id to the proxy, *before*
 			//reassociating the proxy
 			if ( object instanceof HibernateProxy ) {
 				( ( HibernateProxy ) object ).getHibernateLazyInitializer().setIdentifier( requestedId );
 			}
 		}
 
         // For an uninitialized proxy, noop, don't even need to return an id, since it is never a save()
         if (reassociateIfUninitializedProxy(object, source)) LOG.trace("Reassociated uninitialized proxy");
 		else {
 			//initialize properties of the event:
 			final Object entity = source.getPersistenceContext().unproxyAndReassociate( object );
 			event.setEntity( entity );
 			event.setEntry( source.getPersistenceContext().getEntry( entity ) );
 			//return the id in the event object
 			event.setResultId( performSaveOrUpdate( event ) );
 		}
 
 	}
 
 	protected boolean reassociateIfUninitializedProxy(Object object, SessionImplementor source) {
 		return source.getPersistenceContext().reassociateIfUninitializedProxy( object );
 	}
 
 	protected Serializable performSaveOrUpdate(SaveOrUpdateEvent event) {
 		int entityState = getEntityState(
 				event.getEntity(),
 				event.getEntityName(),
 				event.getEntry(),
 				event.getSession()
 		);
 
 		switch ( entityState ) {
 			case DETACHED:
 				entityIsDetached( event );
 				return null;
 			case PERSISTENT:
 				return entityIsPersistent( event );
 			default: //TRANSIENT or DELETED
 				return entityIsTransient( event );
 		}
 	}
 
 	protected Serializable entityIsPersistent(SaveOrUpdateEvent event) throws HibernateException {
         LOG.trace("Ignoring persistent instance");
 
 		EntityEntry entityEntry = event.getEntry();
 		if ( entityEntry == null ) {
 			throw new AssertionFailure( "entity was transient or detached" );
 		}
 		else {
 
 			if ( entityEntry.getStatus() == Status.DELETED ) {
 				throw new AssertionFailure( "entity was deleted" );
 			}
 
 			final SessionFactoryImplementor factory = event.getSession().getFactory();
 
 			Serializable requestedId = event.getRequestedId();
 
 			Serializable savedId;
 			if ( requestedId == null ) {
 				savedId = entityEntry.getId();
 			}
 			else {
 
 				final boolean isEqual = !entityEntry.getPersister().getIdentifierType()
 						.isEqual( requestedId, entityEntry.getId(), event.getSession().getEntityMode(), factory );
 
 				if ( isEqual ) {
 					throw new PersistentObjectException(
 							"object passed to save() was already persistent: " +
 									MessageHelper.infoString( entityEntry.getPersister(), requestedId, factory )
 					);
 				}
 
 				savedId = requestedId;
 
 			}
 
             if (LOG.isTraceEnabled()) LOG.trace("Object already associated with session: "
                                                 + MessageHelper.infoString(entityEntry.getPersister(), savedId, factory));
 
 			return savedId;
 
 		}
 	}
 
 	/**
 	 * The given save-update event named a transient entity.
 	 * <p/>
 	 * Here, we will perform the save processing.
 	 *
 	 * @param event The save event to be handled.
 	 *
 	 * @return The entity's identifier after saving.
 	 */
 	protected Serializable entityIsTransient(SaveOrUpdateEvent event) {
 
         LOG.trace("Saving transient instance");
 
 		final EventSource source = event.getSession();
 
 		EntityEntry entityEntry = event.getEntry();
 		if ( entityEntry != null ) {
 			if ( entityEntry.getStatus() == Status.DELETED ) {
 				source.forceFlush( entityEntry );
 			}
 			else {
 				throw new AssertionFailure( "entity was persistent" );
 			}
 		}
 
 		Serializable id = saveWithGeneratedOrRequestedId( event );
 
 		source.getPersistenceContext().reassociateProxy( event.getObject(), id );
 
 		return id;
 	}
 
 	/**
 	 * Save the transient instance, assigning the right identifier
 	 *
 	 * @param event The initiating event.
 	 *
 	 * @return The entity's identifier value after saving.
 	 */
 	protected Serializable saveWithGeneratedOrRequestedId(SaveOrUpdateEvent event) {
 		return saveWithGeneratedId(
 				event.getEntity(),
 				event.getEntityName(),
 				null,
 				event.getSession(),
 				true
 		);
 	}
 
 	/**
 	 * The given save-update event named a detached entity.
 	 * <p/>
 	 * Here, we will perform the update processing.
 	 *
 	 * @param event The update event to be handled.
 	 */
 	protected void entityIsDetached(SaveOrUpdateEvent event) {
 
         LOG.trace("Updating detached instance");
 
 		if ( event.getSession().getPersistenceContext().isEntryFor( event.getEntity() ) ) {
 			//TODO: assertion only, could be optimized away
 			throw new AssertionFailure( "entity was persistent" );
 		}
 
 		Object entity = event.getEntity();
 
 		EntityPersister persister = event.getSession().getEntityPersister( event.getEntityName(), entity );
 
 		event.setRequestedId(
 				getUpdateId(
 						entity, persister, event.getRequestedId(), event.getSession()
 				)
 		);
 
 		performUpdate( event, entity, persister );
 
 	}
 
 	/**
 	 * Determine the id to use for updating.
 	 *
 	 * @param entity The entity.
 	 * @param persister The entity persister
 	 * @param requestedId The requested identifier
 	 * @param session The session
 	 *
 	 * @return The id.
 	 *
 	 * @throws TransientObjectException If the entity is considered transient.
 	 */
 	protected Serializable getUpdateId(
 			Object entity,
 			EntityPersister persister,
 			Serializable requestedId,
 			SessionImplementor session) {
 		// use the id assigned to the instance
 		Serializable id = persister.getIdentifier( entity, session );
 		if ( id == null ) {
 			// assume this is a newly instantiated transient object
 			// which should be saved rather than updated
 			throw new TransientObjectException(
 					"The given object has a null identifier: " +
 							persister.getEntityName()
 			);
 		}
 		else {
 			return id;
 		}
 
 	}
 
 	protected void performUpdate(
 			SaveOrUpdateEvent event,
 			Object entity,
 			EntityPersister persister) throws HibernateException {
 
         if (!persister.isMutable()) LOG.trace("Immutable instance passed to performUpdate()");
 
         if (LOG.isTraceEnabled()) LOG.trace("Updating "
                                             + MessageHelper.infoString(persister,
                                                                        event.getRequestedId(),
                                                                        event.getSession().getFactory()));
 
         final EventSource source = event.getSession();
-
-		EntityKey key = new EntityKey(event.getRequestedId(), persister, source.getEntityMode());
+		final EntityKey key = source.generateEntityKey( event.getRequestedId(), persister );
 
 		source.getPersistenceContext().checkUniqueness(key, entity);
 
 		if (invokeUpdateLifecycle(entity, persister, source)) {
             reassociate(event, event.getObject(), event.getRequestedId(), persister);
             return;
         }
 
 		// this is a transient object with existing persistent state not loaded by the session
 
 		new OnUpdateVisitor(source, event.getRequestedId(), entity).process(entity, persister);
 
 		// TODO: put this stuff back in to read snapshot from
         // the second-level cache (needs some extra work)
         /*Object[] cachedState = null;
 
         if ( persister.hasCache() ) {
         	CacheEntry entry = (CacheEntry) persister.getCache()
         			.get( event.getRequestedId(), source.getTimestamp() );
             cachedState = entry==null ?
             		null :
             		entry.getState(); //TODO: half-assemble this stuff
         }*/
 
 		source.getPersistenceContext().addEntity(entity, (persister.isMutable() ? Status.MANAGED : Status.READ_ONLY), null, // cachedState,
                                                  key,
                                                  persister.getVersion(entity, source.getEntityMode()),
                                                  LockMode.NONE,
                                                  true,
                                                  persister,
                                                  false,
                                                  true // assume true, since we don't really know, and it doesn't matter
         );
 
 		persister.afterReassociate(entity, source);
 
         if (LOG.isTraceEnabled()) LOG.trace("Updating "
                                             + MessageHelper.infoString(persister, event.getRequestedId(), source.getFactory()));
 
         cascadeOnUpdate(event, persister, entity);
 	}
 
 	protected boolean invokeUpdateLifecycle(Object entity, EntityPersister persister, EventSource source) {
 		if ( persister.implementsLifecycle( source.getEntityMode() ) ) {
             LOG.debugf("Calling onUpdate()");
             if (((Lifecycle)entity).onUpdate(source)) {
                 LOG.debugf("Update vetoed by onUpdate()");
 				return true;
 			}
 		}
 		return false;
 	}
 
 	/**
 	 * Handles the calls needed to perform cascades as part of an update request
 	 * for the given entity.
 	 *
 	 * @param event The event currently being processed.
 	 * @param persister The defined persister for the entity being updated.
 	 * @param entity The entity being updated.
 	 */
 	private void cascadeOnUpdate(SaveOrUpdateEvent event, EntityPersister persister, Object entity) {
 		EventSource source = event.getSession();
 		source.getPersistenceContext().incrementCascadeLevel();
 		try {
 			new Cascade( CascadingAction.SAVE_UPDATE, Cascade.AFTER_UPDATE, source )
 					.cascade( persister, entity );
 		}
 		finally {
 			source.getPersistenceContext().decrementCascadeLevel();
 		}
 	}
 
 	@Override
     protected CascadingAction getCascadeAction() {
 		return CascadingAction.SAVE_UPDATE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java
index 6375181110..d6d413c46e 100755
--- a/hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/AbstractSessionImpl.java
@@ -1,284 +1,297 @@
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
 package org.hibernate.impl;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.List;
+
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Query;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionContract;
-import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
+import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.NamedQueryDefinition;
 import org.hibernate.engine.NamedSQLQueryDefinition;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.jdbc.LobCreationContext;
+import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.query.HQLQueryPlan;
 import org.hibernate.engine.query.NativeSQLQueryPlan;
 import org.hibernate.engine.query.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
+import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
 
 /**
  * Functionality common to stateless and stateful sessions
  *
  * @author Gavin King
  */
 public abstract class AbstractSessionImpl implements Serializable, SharedSessionContract, SessionImplementor, TransactionContext {
-
 	protected transient SessionFactoryImpl factory;
 	private String tenantIdentifier;
 	private boolean closed = false;
 
 	protected AbstractSessionImpl(SessionFactoryImpl factory) {
 		this.factory = factory;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public TransactionEnvironment getTransactionEnvironment() {
 		return factory.getTransactionEnvironment();
 	}
 
 	@Override
 	public <T> T execute(final LobCreationContext.Callback<T> callback) {
 		return getTransactionCoordinator().getJdbcCoordinator().coordinateWork(
 				new WorkExecutorVisitable<T>() {
 					@Override
 					public T accept(WorkExecutor<T> workExecutor, Connection connection) throws SQLException {
 						try {
 							return callback.executeOnConnection( connection );
 						}
 						catch (SQLException e) {
 							throw getFactory().getSQLExceptionHelper().convert(
 									e,
 									"Error creating contextual LOB : " + e.getMessage()
 							);
 						}
 					}
 				}
 		);
 	}
 
+	@Override
 	public boolean isClosed() {
 		return closed;
 	}
 
 	protected void setClosed() {
 		closed = true;
 	}
 
 	protected void errorIfClosed() {
 		if ( closed ) {
 			throw new SessionException( "Session is closed!" );
 		}
 	}
 
+	@Override
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		NamedQueryDefinition nqd = factory.getNamedQuery( queryName );
 		final Query query;
 		if ( nqd != null ) {
 			String queryString = nqd.getQueryString();
 			query = new QueryImpl(
 					queryString,
 			        nqd.getFlushMode(),
 			        this,
 			        getHQLQueryPlan( queryString, false ).getParameterMetadata()
 			);
 			query.setComment( "named HQL query " + queryName );
 		}
 		else {
 			NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 			if ( nsqlqd==null ) {
 				throw new MappingException( "Named query not known: " + queryName );
 			}
 			query = new SQLQueryImpl(
 					nsqlqd,
 			        this,
 			        factory.getQueryPlanCache().getSQLParameterMetadata( nsqlqd.getQueryString() )
 			);
 			query.setComment( "named native SQL query " + queryName );
 			nqd = nsqlqd;
 		}
 		initQuery( query, nqd );
 		return query;
 	}
 
+	@Override
 	public Query getNamedSQLQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 		if ( nsqlqd==null ) {
 			throw new MappingException( "Named SQL query not known: " + queryName );
 		}
 		Query query = new SQLQueryImpl(
 				nsqlqd,
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( nsqlqd.getQueryString() )
 		);
 		query.setComment( "named native SQL query " + queryName );
 		initQuery( query, nsqlqd );
 		return query;
 	}
 
 	private void initQuery(Query query, NamedQueryDefinition nqd) {
 		query.setCacheable( nqd.isCacheable() );
 		query.setCacheRegion( nqd.getCacheRegion() );
 		if ( nqd.getTimeout()!=null ) query.setTimeout( nqd.getTimeout().intValue() );
 		if ( nqd.getFetchSize()!=null ) query.setFetchSize( nqd.getFetchSize().intValue() );
 		if ( nqd.getCacheMode() != null ) query.setCacheMode( nqd.getCacheMode() );
 		query.setReadOnly( nqd.isReadOnly() );
 		if ( nqd.getComment() != null ) query.setComment( nqd.getComment() );
 	}
 
+	@Override
 	public Query createQuery(String queryString) {
 		errorIfClosed();
 		QueryImpl query = new QueryImpl(
 				queryString,
 		        this,
 		        getHQLQueryPlan( queryString, false ).getParameterMetadata()
 		);
 		query.setComment( queryString );
 		return query;
 	}
 
+	@Override
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
 		SQLQueryImpl query = new SQLQueryImpl(
 				sql,
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( sql )
 		);
 		query.setComment( "dynamic native SQL query" );
 		return query;
 	}
 
 	protected HQLQueryPlan getHQLQueryPlan(String query, boolean shallow) throws HibernateException {
 		return factory.getQueryPlanCache().getHQLQueryPlan( query, shallow, getEnabledFilters() );
 	}
 
 	protected NativeSQLQueryPlan getNativeSQLQueryPlan(NativeSQLQuerySpecification spec) throws HibernateException {
 		return factory.getQueryPlanCache().getNativeSQLQueryPlan( spec );
 	}
 
+	@Override
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return listCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
+	@Override
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return scrollCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public String getTenantIdentifier() {
 		return tenantIdentifier;
 	}
 
 	@Override
 	public void setTenantIdentifier(String identifier) {
 		if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 			throw new HibernateException( "SessionFactory was not configured for multi-tenancy" );
 		}
 		this.tenantIdentifier = identifier;
 	}
 
+	@Override
+	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
+		return new EntityKey( id, persister, getEntityMode(), getTenantIdentifier() );
+	}
+
 	private transient JdbcConnectionAccess jdbcConnectionAccess;
 
 	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		if ( jdbcConnectionAccess == null ) {
 			if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 				jdbcConnectionAccess = new NonContextualJdbcConnectionAccess(
 						factory.getServiceRegistry().getService( ConnectionProvider.class )
 				);
 			}
 			else {
 				jdbcConnectionAccess = new ContextualJdbcConnectionAccess(
 						factory.getServiceRegistry().getService( MultiTenantConnectionProvider.class )
 				);
 			}
 		}
 		return jdbcConnectionAccess;
 	}
 
 	private static class NonContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
 		private final ConnectionProvider connectionProvider;
 
 		private NonContextualJdbcConnectionAccess(ConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			return connectionProvider.getConnection();
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			connectionProvider.closeConnection( connection );
 		}
 	}
 
 	private class ContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
 		private final MultiTenantConnectionProvider connectionProvider;
 
 		private ContextualJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 			return connectionProvider.getConnection( tenantIdentifier );
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 			connectionProvider.releaseConnection( tenantIdentifier, connection );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
index c582845464..471e63bd8b 100755
--- a/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/impl/StatelessSessionImpl.java
@@ -1,715 +1,719 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
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
 package org.hibernate.impl;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
+
+import org.jboss.logging.Logger;
+
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
-import org.jboss.logging.Logger;
 
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
 			final CacheKey ck = new CacheKey(
 					id,
 			        persister.getIdentifierType(),
 			        persister.getRootEntityName(),
 			        this.getEntityMode(),
 			        this.getFactory()
 			);
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
-		Object loaded = temporaryPersistenceContext.getEntity( new EntityKey( id, persister, getEntityMode() ) );
+		Object loaded = temporaryPersistenceContext.getEntity( generateEntityKey( id, persister ) );
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
 
 	public EventListeners getListeners() {
 		throw new UnsupportedOperationException();
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
index acc0df1f46..3f44629eee 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,2304 +1,2300 @@
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
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.cache.FilterKey;
 import org.hibernate.cache.QueryCache;
 import org.hibernate.cache.QueryKey;
 import org.hibernate.collection.PersistentCollection;
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
 import org.hibernate.impl.FetchingScrollableResultsImpl;
 import org.hibernate.impl.ScrollableResultsImpl;
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
-import org.jboss.logging.Logger;
 
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
 
     protected static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, Loader.class.getName());
 
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
-			return new EntityKey(
-					optionalId,
-					session.getEntityPersister( optionalEntityName, optionalObject ),
-					session.getEntityMode()
-				);
+			return session.generateEntityKey( optionalId, session.getEntityPersister( optionalEntityName, optionalObject ) );
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
-			keys[ entitySpan - 1 ] = new EntityKey( optionalId, persisters[ entitySpan - 1 ], session.getEntityMode() );
+			keys[ entitySpan - 1 ] = session.generateEntityKey( optionalId, persisters[ entitySpan - 1 ] );
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
-							keys[targetIndex] = new EntityKey( targetId, persisters[targetIndex], session.getEntityMode() );
+							keys[targetIndex] = session.generateEntityKey( targetId, persisters[targetIndex] );
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
-							object = instanceNotYetLoaded(
+							instanceNotYetLoaded(
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
-			keys[i] = resolvedId == null ? null : new EntityKey( resolvedId, persisters[i], session.getEntityMode() );
+			keys[i] = resolvedId == null ? null : session.generateEntityKey( resolvedId, persisters[i] );
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
 	}
 
 	/**
 	 * Are rows transformed immediately after being read from the ResultSet?
 	 * @return true, if getResultColumnOrRow() transforms the results; false, otherwise
 	 */
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return false;
 	}
 
 	/**
 	 * Returns the aliases that corresponding to a result row.
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
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(Object[] row,
 														 ResultSet rs,
 														 SessionImplementor session)
 			throws SQLException, HibernateException {
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
 						boolean isOneToOneAssociation = ownerAssociationTypes!=null &&
 								ownerAssociationTypes[i]!=null &&
 								ownerAssociationTypes[i].isOneToOne();
 						if ( isOneToOneAssociation ) {
 							persistenceContext.addNullProperty( ownerKey,
 									ownerAssociationTypes[i].getPropertyName() );
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
 
             if (LOG.isDebugEnabled()) LOG.debugf("Found row of collection: %s",
                                                  MessageHelper.collectionInfoString(persister, collectionRowKey, getFactory()));
 
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
 
             if (LOG.isDebugEnabled()) LOG.debugf("Result set contains (possibly empty) collection: %s",
                                                  MessageHelper.collectionInfoString(persister, optionalKey, getFactory()));
 
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
 			// this is a collection initializer, so we must create a collection
 			// for each of the passed-in keys, to account for the possibility
 			// that the collection is empty and has no rows in the result set
 
 			CollectionPersister[] collectionPersisters = getCollectionPersisters();
 			for ( int j=0; j<collectionPersisters.length; j++ ) {
 				for ( int i = 0; i < keys.length; i++ ) {
 					//handle empty collections
 
                     if (LOG.isDebugEnabled()) LOG.debugf("Result set contains (possibly empty) collection: %s",
                                                          MessageHelper.collectionInfoString(collectionPersisters[j],
                                                                                             keys[i],
                                                                                             getFactory()));
 
 					session.getPersistenceContext()
 							.getLoadContexts()
 							.getCollectionLoadContext( ( ResultSet ) resultSetId )
 							.getLoadingCollection( collectionPersisters[j], keys[i] );
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
 
 			Type idType = persister.getIdentifierType();
 			resultId = (Serializable) idType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedKeyAliases(),
 					session,
 					null //problematic for <key-many-to-one>!
 				);
 
 			final boolean idIsResultId = id != null &&
 					resultId != null &&
 					idType.isEqual( id, resultId, session.getEntityMode(), factory );
 
 			if ( idIsResultId ) resultId = id; //use the id passed in
 		}
 
-		return resultId == null ?
-				null :
-				new EntityKey( resultId, persister, session.getEntityMode() );
+		return resultId == null ? null : session.generateEntityKey( resultId, persister );
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
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		Object version = session.getPersistenceContext().getEntry( entity ).getVersion();
 
 		if ( version != null ) { //null version means the object is in the process of being loaded somewhere else in the ResultSet
 			VersionType versionType = persister.getVersionType();
 			Object currentVersion = versionType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedVersionAliases(),
 					session,
 					null
 				);
 			if ( !versionType.isEqual(version, currentVersion) ) {
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
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		final int cols = persisters.length;
 		final EntityAliases[] descriptors = getEntityAliases();
 
         if (LOG.isDebugEnabled()) LOG.debugf("Result row: %s", StringHelper.toString(keys));
 
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
 	        final LockMode lockMode,
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 		if ( !persister.isInstance( object, session.getEntityMode() ) ) {
 			throw new WrongClassException(
 					"loaded object was of wrong class " + object.getClass(),
 					key.getIdentifier(),
 					persister.getEntityName()
 				);
 		}
 
 		if ( LockMode.NONE != lockMode && upgradeLocks() ) { //no point doing this if NONE was requested
 
 			final boolean isVersionCheckNeeded = persister.isVersioned() &&
 					session.getPersistenceContext().getEntry(object)
 							.getLockMode().lessThan( lockMode );
 			// we don't need to worry about existing version being uninitialized
 			// because this block isn't called by a re-entrant load (re-entrant
 			// loads _always_ have lock mode NONE)
 			if (isVersionCheckNeeded) {
 				//we only check the version when _upgrading_ lock modes
 				checkVersion( i, persister, key.getIdentifier(), object, rs, session );
 				//we need to upgrade the lock mode to the mode requested
 				session.getPersistenceContext().getEntry(object)
 						.setLockMode(lockMode);
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
 		return array!=null && array[i];
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
 	        final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		final Serializable id = key.getIdentifier();
 
 		// Get the persister for the _subclass_
 		final Loadable persister = (Loadable) getFactory().getEntityPersister( instanceEntityName );
 
         if (LOG.isTraceEnabled()) LOG.trace("Initializing object from ResultSet: "
                                             + MessageHelper.infoString(persister, id, getFactory()));
 
 		boolean eagerPropertyFetch = isEagerPropertyFetchEnabled(i);
 
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
 				getEntityAliases()[i].getSuffixedPropertyAliases(persister);
 
 		final Object[] values = persister.hydrate(
 				rs,
 				id,
 				object,
 				rootPersister,
 				cols,
 				eagerPropertyFetch,
 				session
 			);
 
 		final Object rowId = persister.hasRowId() ? rs.getObject(rowIdAlias) : null;
 
 		final AssociationType[] ownerAssociationTypes = getOwnerAssociationTypes();
 		if ( ownerAssociationTypes != null && ownerAssociationTypes[i] != null ) {
 			String ukName = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName();
 			if (ukName!=null) {
 				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex(ukName);
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
 						session.getEntityMode(), session.getFactory()
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
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		if ( persister.hasSubclasses() ) {
 
 			// Code to handle subclasses of topClass
 			Object discriminatorValue = persister.getDiscriminatorType().nullSafeGet(
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
 	private void advance(final ResultSet rs, final RowSelection selection)
 			throws SQLException {
 
 		final int firstRow = getFirstRow( selection );
 		if ( firstRow != 0 ) {
 			if ( getFactory().getSettings().isScrollableResultSetsEnabled() ) {
 				// we can go straight to the first required row
 				rs.absolute( firstRow );
 			}
 			else {
 				// we need to step through the rows one row at a time (slow)
 				for ( int m = 0; m < firstRow; m++ ) rs.next();
 			}
 		}
 	}
 
 	private static boolean hasMaxRows(RowSelection selection) {
 		return selection != null && selection.getMaxRows() != null;
 	}
 
 	private static int getFirstRow(RowSelection selection) {
 		if ( selection == null || selection.getFirstRow() == null ) {
 			return 0;
 		}
 		else {
 			return selection.getFirstRow().intValue();
 		}
 	}
 
 	private int interpretFirstRow(int zeroBasedFirstResult) {
 		return getFactory().getDialect().convertToFirstRowValue( zeroBasedFirstResult );
 	}
 
 	/**
 	 * Should we pre-process the SQL string, adding a dialect-specific
 	 * LIMIT clause.
 	 */
 	private static boolean useLimit(final RowSelection selection, final Dialect dialect) {
 		return dialect.supportsLimit() && hasMaxRows( selection );
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
 	        final QueryParameters queryParameters,
 	        final boolean scroll,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 
 		queryParameters.processFilters( getSQLString(), session );
 		String sql = queryParameters.getFilteredSQL();
 		final Dialect dialect = getFactory().getDialect();
 		final RowSelection selection = queryParameters.getRowSelection();
 		boolean useLimit = useLimit( selection, dialect );
 		boolean hasFirstRow = getFirstRow( selection ) > 0;
 		boolean useOffset = hasFirstRow && useLimit && dialect.supportsLimitOffset();
 		boolean callable = queryParameters.isCallable();
 
 		final boolean canScroll = getFactory().getSettings().isScrollableResultSetsEnabled();
 		final boolean useScrollableResultSetToSkip = hasFirstRow &&
 				!useOffset &&
 				getFactory().getSettings().isScrollableResultSetsEnabled();
 		final ScrollMode scrollMode =
 				canScroll
 						? scroll || useScrollableResultSetToSkip
 								? queryParameters.getScrollMode()
 								: ScrollMode.SCROLL_INSENSITIVE
 						: null;
 
 		if ( useLimit ) {
 			sql = dialect.getLimitString(
 					sql.trim(), //use of trim() here is ugly?
 					useOffset ? getFirstRow(selection) : 0,
 					getMaxOrLimit(selection, dialect)
 				);
 		}
 
 		sql = preprocessSQL( sql, queryParameters, dialect );
 
 		PreparedStatement st = null;
 
 
 		st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(
 				sql,
 				callable,
 				scrollMode
 		);
 
 		try {
 
 			int col = 1;
 			//TODO: can we limit stored procedures ?!
 			if ( useLimit && dialect.bindLimitParametersFirst() ) {
 				col += bindLimitParameters( st, col, selection );
 			}
 			if (callable) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement)st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
 			if ( useLimit && !dialect.bindLimitParametersFirst() ) {
 				col += bindLimitParameters( st, col, selection );
 			}
 
 			if ( !useLimit ) {
 				setMaxRows( st, selection );
 			}
 
 			if ( selection != null ) {
 				if ( selection.getTimeout() != null ) {
 					st.setQueryTimeout( selection.getTimeout().intValue() );
 				}
 				if ( selection.getFetchSize() != null ) {
 					st.setFetchSize( selection.getFetchSize().intValue() );
 				}
 			}
 
 			// handle lock timeout...
 			LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
                     if (!dialect.supportsLockTimeouts()) LOG.debugf("Lock timeout [%s] requested but dialect reported to not support lock timeouts",
                                                                     lockOptions.getTimeOut());
                     else if (dialect.isLockTimeoutParameterized()) st.setInt(col++, lockOptions.getTimeOut());
 				}
 			}
 
             LOG.trace("Bound [" + col + "] parameters total");
 		}
 		catch ( SQLException sqle ) {
 			st.close();
 			throw sqle;
 		}
 		catch ( HibernateException he ) {
 			st.close();
 			throw he;
 		}
 
 		return st;
 	}
 
 	/**
 	 * Some dialect-specific LIMIT clauses require the maximum last row number
 	 * (aka, first_row_number + total_row_count), while others require the maximum
 	 * returned row count (the total maximum number of rows to return).
 	 *
 	 * @param selection The selection criteria
 	 * @param dialect The dialect
 	 * @return The appropriate value to bind into the limit clause.
 	 */
 	private static int getMaxOrLimit(final RowSelection selection, final Dialect dialect) {
 		final int firstRow = dialect.convertToFirstRowValue( getFirstRow( selection ) );
 		final int lastRow = selection.getMaxRows().intValue();
 		if ( dialect.useMaxForLimit() ) {
 			return lastRow + firstRow;
 		}
 		else {
 			return lastRow;
 		}
 	}
 
 	/**
 	 * Bind parameter values needed by the dialect-specific LIMIT clause.
 	 *
 	 * @param statement The statement to which to bind limit param values.
 	 * @param index The bind position from which to start binding
 	 * @param selection The selection object containing the limit information.
 	 * @return The number of parameter values bound.
 	 * @throws java.sql.SQLException Indicates problems binding parameter values.
 	 */
 	private int bindLimitParameters(
 			final PreparedStatement statement,
 			final int index,
 			final RowSelection selection) throws SQLException {
 		Dialect dialect = getFactory().getDialect();
 		if ( !dialect.supportsVariableLimit() ) {
 			return 0;
 		}
 		if ( !hasMaxRows( selection ) ) {
 			throw new AssertionFailure( "no max results set" );
 		}
 		int firstRow = interpretFirstRow( getFirstRow( selection ) );
 		int lastRow = getMaxOrLimit( selection, dialect );
 		boolean hasFirstRow = dialect.supportsLimitOffset() && ( firstRow > 0 || dialect.forceLimitUsage() );
 		boolean reverse = dialect.bindLimitParametersInReverseOrder();
 		if ( hasFirstRow ) {
 			statement.setInt( index + ( reverse ? 1 : 0 ), firstRow );
 		}
 		statement.setInt( index + ( reverse || !hasFirstRow ? 0 : 1 ), lastRow );
 		return hasFirstRow ? 2 : 1;
 	}
 
 	/**
 	 * Use JDBC API to limit the number of rows returned by the SQL query if necessary
 	 */
 	private void setMaxRows(
 			final PreparedStatement st,
 			final RowSelection selection) throws SQLException {
 		if ( hasMaxRows( selection ) ) {
 			st.setMaxRows( selection.getMaxRows().intValue() + interpretFirstRow( getFirstRow( selection ) ) );
 		}
 	}
 
 	/**
 	 * Bind all parameter values into the prepared statement in preparation
 	 * for execution.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 * @return The number of JDBC bind positions actually bound during this method execution.
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
 	 * @return The number of JDBC bind positions actually bound during this method execution.
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
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindNamedParameters(
 			final PreparedStatement statement,
 			final Map namedParams,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
 		if ( namedParams != null ) {
 			// assumes that types are all of span 1
 			Iterator iter = namedParams.entrySet().iterator();
 			int result = 0;
 			while ( iter.hasNext() ) {
 				Map.Entry e = ( Map.Entry ) iter.next();
 				String name = ( String ) e.getKey();
 				TypedValue typedval = ( TypedValue ) e.getValue();
 				int[] locs = getNamedParameterLocs( name );
 				for ( int i = 0; i < locs.length; i++ ) {
                     LOG.debugf("bindNamedParameters() %s -> %s [%s]", typedval.getValue(), name, locs[i] + startIndex);
 					typedval.getType().nullSafeSet( statement, typedval.getValue(), locs[i] + startIndex, session );
 				}
 				result += locs.length;
 			}
 			return result;
 		}
 		else {
 			return 0;
 		}
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		throw new AssertionFailure("no named parameters");
 	}
 
 	/**
 	 * Fetch a <tt>PreparedStatement</tt>, call <tt>setMaxRows</tt> and then execute it,
 	 * advance to the first result and return an SQL <tt>ResultSet</tt>
 	 */
 	protected final ResultSet getResultSet(
 	        final PreparedStatement st,
 	        final boolean autodiscovertypes,
 	        final boolean callable,
 	        final RowSelection selection,
 	        final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		ResultSet rs = null;
 		try {
 			Dialect dialect = getFactory().getDialect();
 			rs = st.executeQuery();
 			rs = wrapResultSetIfEnabled( rs , session );
 
 			if ( !dialect.supportsLimitOffset() || !useLimit( selection, dialect ) ) {
 				advance( rs, selection );
 			}
 
 			if ( autodiscovertypes ) {
 				autoDiscoverTypes( rs );
 			}
 			return rs;
 		}
 		catch ( SQLException sqle ) {
 			st.close();
 			throw sqle;
 		}
 	}
 
 	protected void autoDiscoverTypes(ResultSet rs) {
 		throw new AssertionFailure("Auto discover types not supported in this loader");
 
 	}
 
 	private synchronized ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
 		// synchronized to avoid multi-thread access issues; defined as method synch to avoid
 		// potential deadlock issues due to nature of code.
 		if ( session.getFactory().getSettings().isWrapResultSetsEnabled() ) {
 			try {
                 LOG.debugf("Wrapping result set [%s]", rs);
 				return session.getFactory()
 						.getJdbcServices()
 						.getResultSetWrapper().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch(SQLException e) {
                 LOG.unableToWrapResultSet(e);
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
             LOG.trace("Building columnName->columnIndex cache");
 			columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
 		}
 
 		return columnNameCache;
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 * @param persister only needed for logging
 	 * @param lockOptions
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
 
         if (LOG.isDebugEnabled()) LOG.debugf("Loading entity: %s",
                                              MessageHelper.infoString(persister, id, identifierType, getFactory()));
 
 		List result;
 		try {
 			QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( new Type[] { identifierType } );
 			qp.setPositionalParameterValues( new Object[] { id } );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( optionalEntityName );
 			qp.setOptionalId( optionalIdentifier );
 			qp.setLockOptions( lockOptions );
 			result = doQueryAndInitializeNonLazyCollections( session, qp, false );
 		}
 		catch ( SQLException sqle ) {
 			final Loadable[] persisters = getEntityPersisters();
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load an entity: " +
 			        MessageHelper.infoString( persisters[persisters.length-1], id, identifierType, getFactory() ),
 			        getSQLString()
 				);
 		}
 
         LOG.debugf("Done entity load");
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 * @param persister only needed for logging
 	 */
 	protected final List loadEntity(
 	        final SessionImplementor session,
 	        final Object key,
 	        final Object index,
 	        final Type keyType,
 	        final Type indexType,
 	        final EntityPersister persister) throws HibernateException {
 
         LOG.debugf("Loading collection element by index");
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters(
 							new Type[] { keyType, indexType },
 							new Object[] { key, index }
 					),
 					false
 			);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not collection element by index",
 			        getSQLString()
 				);
 		}
 
         LOG.debugf("Done entity load");
 
 		return result;
 
 	}
 
 	/**
 	 * Called by wrappers that batch load entities
 	 * @param persister only needed for logging
 	 * @param lockOptions
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
 
         if (LOG.isDebugEnabled()) LOG.debugf("Batch loading entity: %s", MessageHelper.infoString(persister, ids, getFactory()));
 
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
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load an entity batch: " +
 			        MessageHelper.infoString( getEntityPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 
         LOG.debugf("Done entity batch load");
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that initialize collections
 	 */
 	public final void loadCollection(
 	        final SessionImplementor session,
 	        final Serializable id,
 	        final Type type) throws HibernateException {
 
         if (LOG.isDebugEnabled()) LOG.debugf("Loading collection: %s",
                                              MessageHelper.collectionInfoString(getCollectionPersisters()[0], id, getFactory()));
 
 		Serializable[] ids = new Serializable[]{id};
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( new Type[]{type}, ids, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize a collection: " +
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ),
 					getSQLString()
 				);
 		}
 
         LOG.debugf("Done loading collection");
 
 	}
 
 	/**
 	 * Called by wrappers that batch initialize collections
 	 */
 	public final void loadCollectionBatch(
 	        final SessionImplementor session,
 	        final Serializable[] ids,
 	        final Type type) throws HibernateException {
 
         if (LOG.isDebugEnabled()) LOG.debugf("Batch loading collection: %s",
                                              MessageHelper.collectionInfoString(getCollectionPersisters()[0], ids, getFactory()));
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( idTypes, ids, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not initialize a collection batch: " +
 			        MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 
         LOG.debugf("Done batch load");
 
 	}
 
 	/**
 	 * Called by subclasses that batch initialize collections
 	 */
 	protected final void loadCollectionSubselect(
 	        final SessionImplementor session,
 	        final Serializable[] ids,
 	        final Object[] parameterValues,
 	        final Type[] parameterTypes,
 	        final Map namedParameters,
 	        final Type type) throws HibernateException {
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections( session,
 					new QueryParameters( parameterTypes, parameterValues, namedParameters, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
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
 	        final Set querySpaces,
 	        final Type[] resultTypes) throws HibernateException {
 
 		final boolean cacheable = factory.getSettings().isQueryCacheEnabled() &&
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
 			final Set querySpaces,
 			final Type[] resultTypes) {
 
 		QueryCache queryCache = factory.getQueryCache( queryParameters.getCacheRegion() );
 
 		QueryKey key = generateQueryKey( session, queryParameters );
 
         if (querySpaces == null || querySpaces.size() == 0) LOG.trace("Unexpected querySpaces is "
                                                                       + (querySpaces == null ? querySpaces : "empty"));
         else LOG.trace("querySpaces is " + querySpaces.toString());
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index bddfb33587..441d462a0d 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1,1650 +1,1652 @@
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
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.entry.CacheEntryStructure;
 import org.hibernate.cache.entry.StructuredCollectionCacheEntry;
 import org.hibernate.cache.entry.StructuredMapCacheEntry;
 import org.hibernate.cache.entry.UnstructuredCacheEntry;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.collection.PersistentCollection;
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
 import org.hibernate.impl.FilterHelper;
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
-import org.jboss.logging.Logger;
 
 
 /**
  * Base implementation of the <tt>QueryableCollection</tt> interface.
  *
  * @author Gavin King
  * @see BasicCollectionPersister
  * @see OneToManyPersister
  */
 public abstract class AbstractCollectionPersister
 		implements CollectionMetadata, SQLLoadableCollection {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
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
-			.getSubselect( new EntityKey( key, getOwnerEntityPersister(), session.getEntityMode() ) );
+				.getSubselect( session.generateEntityKey( key, getOwnerEntityPersister() ) );
 
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
 
 	public boolean isInverse() {
 		return isInverse;
 	}
 
 	public String getTableName() {
 		return qualifiedTableName;
 	}
 
 	private BasicBatchKey removeBatchKey;
 
 	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
             if (LOG.isDebugEnabled()) LOG.debugf("Deleting collection: %s",
                                                  MessageHelper.collectionInfoString(this, id, getFactory()));
 
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
 					offset+= expectation.prepare( st );
 
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
 
                 LOG.debugf("Done deleting collection");
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
 
             if (LOG.isDebugEnabled()) LOG.debugf("Inserting collection: %s",
                                                  MessageHelper.collectionInfoString(this, id, getFactory()));
 
 			try {
 				//create all the new entries
 				Iterator entries = collection.entries(this);
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
 								offset+= expectation.prepare( st );
 
 								//TODO: copy/paste from insertRows()
 								int loc = writeKey( st, id, offset, session );
 								if ( hasIdentifier ) {
 									loc = writeIdentifier( st, collection.getIdentifier(entry, i), loc, session );
 								}
 								if ( hasIndex /*&& !indexIsFormula*/ ) {
 									loc = writeIndex( st, collection.getIndex(entry, i, this), loc, session );
 								}
 								loc = writeElement(st, collection.getElement(entry), loc, session );
 
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
 
                     LOG.debugf("Done inserting collection: %s rows inserted", count);
 
                 } else LOG.debugf("Collection was empty");
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 				        sqle,
 				        "could not insert collection: " +
 				        MessageHelper.collectionInfoString( this, id, getFactory() ),
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
 
             if (LOG.isDebugEnabled()) LOG.debugf("Deleting rows of collection: %s",
                                                  MessageHelper.collectionInfoString(this, id, getFactory()));
 
 			boolean deleteByIndex = !isOneToMany() && hasIndex && !indexContainsFormula;
 			final Expectation expectation = Expectations.appropriateExpectation( getDeleteCheckStyle() );
 			try {
 				//delete all the deleted entries
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
 
                         LOG.debugf("Done deleting collection rows: %s deleted", count);
 					}
                 } else LOG.debugf("No rows to delete");
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 				        sqle,
 				        "could not delete collection rows: " +
 				        MessageHelper.collectionInfoString( this, id, getFactory() ),
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
 
             if (LOG.isDebugEnabled()) LOG.debugf("Inserting rows of collection: %s",
                                                  MessageHelper.collectionInfoString(this, id, getFactory()));
 
 			try {
 				//insert all the new entries
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
 							//TODO: copy/paste from recreate()
 							offset = writeKey( st, id, offset, session );
 							if ( hasIdentifier ) {
 								offset = writeIdentifier( st, collection.getIdentifier(entry, i), offset, session );
 							}
 							if ( hasIndex /*&& !indexIsFormula*/ ) {
 								offset = writeIndex( st, collection.getIndex(entry, i, this), offset, session );
 							}
 							writeElement(st, collection.getElement(entry), offset, session );
 
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
                 LOG.debugf("Done inserting rows: %s inserted", count);
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 				        sqle,
 				        "could not insert collection rows: " +
 				        MessageHelper.collectionInfoString( this, id, getFactory() ),
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
 		StringBuffer buffer = new StringBuffer();
 		manyToManyFilterHelper.render( buffer, alias, enabledFilters );
 
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
 		return elementPropertyMapping.getType(); //==elementType ??
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
 
             LOG.debugf("Updating rows of collection: %s#%s", role, id);
 
 			//update all the modified entries
 			int count = doUpdateRows( id, collection, session );
 
             LOG.debugf("Done updating rows: %s updated", count);
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
 
 		StringBuffer sessionFilterFragment = new StringBuffer();
 		filterHelper.render( sessionFilterFragment, alias, enabledFilters );
 
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 3a160a3472..1280da5c3c 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,1122 +1,1124 @@
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
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.cache.CacheKey;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.entry.CacheEntry;
 import org.hibernate.cache.entry.CacheEntryStructure;
 import org.hibernate.cache.entry.StructuredCacheEntry;
 import org.hibernate.cache.entry.UnstructuredCacheEntry;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.ValueInclusion;
 import org.hibernate.engine.Versioning;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.impl.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.jdbc.TooManyRowsAffectedException;
 import org.hibernate.loader.entity.BatchingEntityLoader;
 import org.hibernate.loader.entity.CascadeEntityLoader;
 import org.hibernate.loader.entity.EntityLoader;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.Update;
 import org.hibernate.tuple.Tuplizer;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.hibernate.type.VersionType;
-import org.jboss.logging.Logger;
 
 /**
  * Basic functionality for persisting an entity via JDBC
  * through either generated or custom SQL
  *
  * @author Gavin King
  */
 public abstract class AbstractEntityPersister
 		implements OuterJoinLoadable, Queryable, ClassMetadata, UniqueKeyLoadable,
-		SQLLoadable, LazyPropertyInitializer, PostInsertIdentityPersister, Lockable {
+				   SQLLoadable, LazyPropertyInitializer, PostInsertIdentityPersister, Lockable {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        AbstractEntityPersister.class.getName());
 
 	public static final String ENTITY_CLASS = "class";
 
 	// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final SessionFactoryImplementor factory;
 	private final EntityRegionAccessStrategy cacheAccessStrategy;
 	private final boolean isLazyPropertiesCacheable;
 	private final CacheEntryStructure cacheEntryStructure;
 	private final EntityMetamodel entityMetamodel;
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
 
 	private final Set affectingFetchProfileNames = new HashSet();
 
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
 
 	private final String temporaryIdTableName;
 	private final String temporaryIdTableDDL;
 
 	private final Map subclassPropertyAliases = new HashMap();
 	private final Map subclassPropertyColumnNames = new HashMap();
 
 	protected final BasicEntityPropertyMapping propertyMapping;
 
 	protected void addDiscriminatorToInsert(Insert insert) {}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {}
 
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
 		return entityMetamodel.getSubclassEntityNames().contains(entityName);
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
 		for ( int i = 1; i < getTableSpan(); i++ ) {
 			result[i] = sqlLazyUpdateStrings[i];
 		}
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
 			boolean[] tableUpdateNeeded = new boolean[ getTableSpan() ];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				int property = dirtyProperties[i];
 				int table = propertyTableNumbers[property];
 				tableUpdateNeeded[table] = tableUpdateNeeded[table] ||
 						( getPropertyColumnSpan(property) > 0 && updateability[property] );
 			}
 			if ( isVersioned() ) {
 				tableUpdateNeeded[0] = tableUpdateNeeded[0] ||
 					Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
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
 
 	public AbstractEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory) throws HibernateException {
 
 		// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		isLazyPropertiesCacheable = persistentClass.isLazyPropertiesCacheable();
 		this.cacheEntryStructure = factory.getSettings().isStructuredCacheEntriesEnabled() ?
 				(CacheEntryStructure) new StructuredCacheEntry(this) :
 				(CacheEntryStructure) new UnstructuredCacheEntry();
 
 		this.entityMetamodel = new EntityMetamodel( persistentClass, factory );
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		int batch = persistentClass.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
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
 			Column col = ( Column ) iter.next();
 			rootTableKeyColumnNames[i] = col.getQuotedName( factory.getDialect() );
 			rootTableKeyColumnReaders[i] = col.getReadExpr( factory.getDialect() );
 			rootTableKeyColumnReaderTemplates[i] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			identifierAliases[i] = col.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( persistentClass.isVersioned() ) {
 			versionColumnName = ( ( Column ) persistentClass.getVersion().getColumnIterator().next() ).getQuotedName( factory.getDialect() );
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( persistentClass.getWhere() ) ? "( " + persistentClass.getWhere() + ") " : null;
 		sqlWhereStringTemplate = sqlWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( sqlWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		// PROPERTIES
 
 		final boolean lazyAvailable = isInstrumented(EntityMode.POJO);
 
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
 			Property prop = ( Property ) iter.next();
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
 				Selectable thing = ( Selectable ) colIter.next();
 				colAliases[k] = thing.getAlias( factory.getDialect() , prop.getValue().getTable() );
 				if ( thing.isFormula() ) {
 					foundFormula = true;
 					formulaTemplates[k] = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				}
 				else {
 					Column col = (Column)thing;
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
 				lazyNumbers.add( new Integer( i ) );
 				lazyTypes.add( prop.getValue().getType() );
 				lazyColAliases.add( colAliases );
 			}
 
 			propertyColumnUpdateable[i] = prop.getValue().getColumnUpdateability();
 			propertyColumnInsertable[i] = prop.getValue().getColumnInsertability();
 
 			propertySelectable[i] = prop.isSelectable();
 
 			propertyUniqueness[i] = prop.getValue().isAlternateUniqueKey();
 
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
 			Property prop = ( Property ) iter.next();
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
 				Selectable thing = ( Selectable ) colIter.next();
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
 					Column col = (Column)thing;
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
 			subclassPropertyCascadeStyleClosure[j++] = ( CascadeStyle ) iter.next();
 		}
 		subclassPropertyFetchModeClosure = new FetchMode[joinedFetchesList.size()];
 		iter = joinedFetchesList.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyFetchModeClosure[j++] = ( FetchMode ) iter.next();
 		}
 
 		propertyDefinedOnSubclass = new boolean[definedBySubclass.size()];
 		iter = definedBySubclass.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			propertyDefinedOnSubclass[j++] = ( ( Boolean ) iter.next() ).booleanValue();
 		}
 
 		// Handle any filters applied to the class level
 		filterHelper = new FilterHelper( persistentClass.getFilterMap(), factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		temporaryIdTableName = persistentClass.getTemporaryIdTableName();
 		temporaryIdTableDDL = persistentClass.getTemporaryIdTableDDL();
 	}
 
 	protected String generateLazySelectString() {
 
 		if ( !entityMetamodel.hasLazyProperties() ) {
 			return null;
 		}
 
 		HashSet tableNumbers = new HashSet();
 		ArrayList columnNumbers = new ArrayList();
 		ArrayList formulaNumbers = new ArrayList();
 		for ( int i = 0; i < lazyPropertyNames.length; i++ ) {
 			// all this only really needs to consider properties
 			// of this class, not its subclasses, but since we
 			// are reusing code used for sequential selects, we
 			// use the subclass closure
 			int propertyNumber = getSubclassPropertyIndex( lazyPropertyNames[i] );
 
 			int tableNumber = getSubclassPropertyTableNumber( propertyNumber );
 			tableNumbers.add( new Integer( tableNumber ) );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int j = 0; j < colNumbers.length; j++ ) {
 				if ( colNumbers[j]!=-1 ) {
 					columnNumbers.add( new Integer( colNumbers[j] ) );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int j = 0; j < formNumbers.length; j++ ) {
 				if ( formNumbers[j]!=-1 ) {
 					formulaNumbers.add( new Integer( formNumbers[j] ) );
 				}
 			}
 		}
 
 		if ( columnNumbers.size()==0 && formulaNumbers.size()==0 ) {
 			// only one-to-one is lazy fetched
 			return null;
 		}
 
 		return renderSelect( ArrayHelper.toIntArray( tableNumbers ),
 				ArrayHelper.toIntArray( columnNumbers ),
 				ArrayHelper.toIntArray( formulaNumbers ) );
 
 	}
 
 	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session)
 			throws HibernateException {
 
 		final Serializable id = session.getContextEntityIdentifier( entity );
 
 		final EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 		if ( entry == null ) {
 			throw new HibernateException( "entity is not associated with the session: " + id );
 		}
 
         if (LOG.isTraceEnabled()) LOG.trace("Initializing lazy properties of: " + MessageHelper.infoString(this, id, getFactory())
                                             + ", field access: " + fieldName);
 
 		if ( hasCache() ) {
 			CacheKey cacheKey = new CacheKey(id, getIdentifierType(), getEntityName(), session.getEntityMode(), getFactory() );
 			Object ce = getCacheAccessStrategy().get( cacheKey, session.getTimestamp() );
 			if (ce!=null) {
 				CacheEntry cacheEntry = (CacheEntry) getCacheEntryStructure().destructure(ce, factory);
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
 
         if (!hasLazyProperties()) throw new AssertionFailure("no lazy properties");
 
         LOG.trace("Initializing lazy properties from datastore");
 
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
 						ps = session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( lazySelect );
 						getIdentifierType().nullSafeSet( ps, id, 1, session );
 						rs = ps.executeQuery();
 						rs.next();
 					}
 					final Object[] snapshot = entry.getLoadedState();
 					for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 						Object propValue = lazyPropertyTypes[j].nullSafeGet( rs, lazyPropertyColumnAliases[j], session, entity );
 						if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 							result = propValue;
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						rs.close();
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					ps.close();
 				}
 			}
 
             LOG.trace("Done initializing lazy properties");
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
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
 
         LOG.trace("Initializing lazy properties from second-level cache");
 
 		Object result = null;
 		Serializable[] disassembledValues = cacheEntry.getDisassembledState();
 		final Object[] snapshot = entry.getLoadedState();
 		for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 			final Object propValue = lazyPropertyTypes[j].assemble(
 					disassembledValues[ lazyPropertyNumbers[j] ],
 					session,
 					entity
 				);
 			if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 				result = propValue;
 			}
 		}
 
         LOG.trace("Done initializing lazy properties");
 
 		return result;
 	}
 
 	private boolean initializeLazyProperty(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Object[] snapshot,
 			final int j,
 			final Object propValue) {
 		setPropertyValue( entity, lazyPropertyNumbers[j], propValue, session.getEntityMode() );
 		if (snapshot != null) {
 			// object have been loaded with setReadOnly(true); HHH-2236
 			snapshot[ lazyPropertyNumbers[j] ] = lazyPropertyTypes[j].deepCopy( propValue, session.getEntityMode(), factory );
 		}
 		return fieldName.equals( lazyPropertyNames[j] );
 	}
 
 	public boolean isBatchable() {
 		return optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_NONE ||
 			( !isVersioned() && optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_VERSION ) ||
 			getFactory().getSettings().isJdbcBatchVersionedData();
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
 
         if (LOG.isTraceEnabled()) LOG.trace("Getting current persistent state for: "
                                             + MessageHelper.infoString(this, id, getFactory()));
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getSQLSnapshotSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				//if ( isVersioned() ) getVersionType().nullSafeSet( ps, version, getIdentifierColumnSpan()+1, session );
 				ResultSet rs = ps.executeQuery();
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
 							values[i] = types[i].hydrate( rs, getPropertyAliases( "", i ), session, null ); //null owner ok??
 						}
 					}
 					return values;
 				}
@@ -1871,2242 +1873,2242 @@ public abstract class AbstractEntityPersister
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 	}
 
 	private void initDiscriminatorPropertyPath(Mapping mapping) throws MappingException {
 		propertyMapping.initPropertyPaths( ENTITY_CLASS,
 				getDiscriminatorType(),
 				new String[]{getDiscriminatorColumnName()},
 				new String[]{getDiscriminatorColumnReaders()},
 				new String[]{getDiscriminatorColumnReaderTemplate()},
 				new String[]{getDiscriminatorFormulaTemplate()},
 				getFactory() );
 	}
 
 	protected void initPropertyPaths(Mapping mapping) throws MappingException {
 		initOrdinaryPropertyPaths(mapping);
 		initOrdinaryPropertyPaths(mapping); //do two passes, for collection property-ref!
 		initIdentifierPropertyPaths(mapping);
 		if ( entityMetamodel.isPolymorphic() ) {
 			initDiscriminatorPropertyPath( mapping );
 		}
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockMode lockMode,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoader.createBatchingEntityLoader(
 				this,
 				batchSize,
 				lockMode,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockOptions lockOptions,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoader.createBatchingEntityLoader(
 				this,
 				batchSize,
 			lockOptions,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected UniqueEntityLoader createEntityLoader(LockMode lockMode) throws MappingException {
 		return createEntityLoader( lockMode, LoadQueryInfluencers.NONE );
 	}
 
 	protected boolean check(int rows, Serializable id, int tableNumber, Expectation expectation, PreparedStatement statement) throws HibernateException {
 		try {
 			expectation.verifyOutcome( rows, statement, -1 );
 		}
 		catch( StaleStateException e ) {
 			if ( !isNullableTable( tableNumber ) ) {
 				if ( getFactory().getStatistics().isStatisticsEnabled() ) {
 					getFactory().getStatisticsImplementor()
 							.optimisticFailure( getEntityName() );
 				}
 				throw new StaleObjectStateException( getEntityName(), id );
 			}
 			return false;
 		}
 		catch( TooManyRowsAffectedException e ) {
 			throw new HibernateException(
 					"Duplicate identifier in table for: " +
 					MessageHelper.infoString( this, id, getFactory() )
 			);
 		}
 		catch ( Throwable t ) {
 			return false;
 		}
 		return true;
 	}
 
 	protected String generateUpdateString(boolean[] includeProperty, int j, boolean useRowId) {
 		return generateUpdateString( includeProperty, j, null, useRowId );
 	}
 
 	/**
 	 * Generate the SQL that updates a row by id (and version)
 	 */
 	protected String generateUpdateString(final boolean[] includeProperty,
 										  final int j,
 										  final Object[] oldFields,
 										  final boolean useRowId) {
 
 		Update update = new Update( getFactory().getDialect() ).setTableName( getTableName( j ) );
 
 		// select the correct row by either pk or rowid
 		if ( useRowId ) {
 			update.addPrimaryKeyColumns( new String[]{rowIdName} ); //TODO: eventually, rowIdName[j]
 		}
 		else {
 			update.addPrimaryKeyColumns( getKeyColumns( j ) );
 		}
 
 		boolean hasColumns = false;
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this is a property of the table, which we are updating
 				update.addColumns( getPropertyColumnNames(i), propertyColumnUpdateable[i], propertyColumnWriters[i] );
 				hasColumns = hasColumns || getPropertyColumnSpan( i ) > 0;
 			}
 		}
 
 		if ( j == 0 && isVersioned() && entityMetamodel.getOptimisticLockMode() == Versioning.OPTIMISTIC_LOCK_VERSION ) {
 			// this is the root (versioned) table, and we are using version-based
 			// optimistic locking;  if we are not updating the version, also don't
 			// check it (unless this is a "generated" version column)!
 			if ( checkVersion( includeProperty ) ) {
 				update.setVersionColumnName( getVersionColumnName() );
 				hasColumns = true;
 			}
 		}
 		else if ( entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION && oldFields != null ) {
 			// we are using "all" or "dirty" property-based optimistic locking
 
 			boolean[] includeInWhere = entityMetamodel.getOptimisticLockMode() == Versioning.OPTIMISTIC_LOCK_ALL ?
 					getPropertyUpdateability() : //optimistic-lock="all", include all updatable properties
 					includeProperty; //optimistic-lock="dirty", include all properties we are updating this time
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				boolean include = includeInWhere[i] &&
 						isPropertyOfTable( i, j ) &&
 						versionability[i];
 				if ( include ) {
 					// this property belongs to the table, and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					String[] propertyColumnWriters = getPropertyColumnWriters( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( oldFields[i], getFactory() );
 					for ( int k=0; k<propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							update.addWhereColumn( propertyColumnNames[k], "=" + propertyColumnWriters[k] );
 						}
 						else {
 							update.addWhereColumn( propertyColumnNames[k], " is null" );
 						}
 					}
 				}
 			}
 
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "update " + getEntityName() );
 		}
 
 		return hasColumns ? update.toStatementString() : null;
 	}
 
 	private boolean checkVersion(final boolean[] includeProperty) {
         return includeProperty[ getVersionProperty() ] ||
 				entityMetamodel.getPropertyUpdateGenerationInclusions()[ getVersionProperty() ] != ValueInclusion.NONE;
 	}
 
 	protected String generateInsertString(boolean[] includeProperty, int j) {
 		return generateInsertString( false, includeProperty, j );
 	}
 
 	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty) {
 		return generateInsertString( identityInsert, includeProperty, 0 );
 	}
 
 	/**
 	 * Generate the SQL that inserts a row
 	 */
 	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty, int j) {
 
 		// todo : remove the identityInsert param and variations;
 		//   identity-insert strings are now generated from generateIdentityInsertString()
 
 		Insert insert = new Insert( getFactory().getDialect() )
 				.setTableName( getTableName( j ) );
 
 		// add normal properties
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		if ( j == 0 ) {
 			addDiscriminatorToInsert( insert );
 		}
 
 		// add the primary key
 		if ( j == 0 && identityInsert ) {
 			insert.addIdentityColumn( getKeyColumns( 0 )[0] );
 		}
 		else {
 			insert.addColumns( getKeyColumns( j ) );
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 
 		String result = insert.toStatementString();
 
 		// append the SQL to return the generated identifier
 		if ( j == 0 && identityInsert && useInsertSelectIdentity() ) { //TODO: suck into Insert
 			result = getFactory().getDialect().appendIdentitySelectToInsert( result );
 		}
 
 		return result;
 	}
 
 	/**
 	 * Used to generate an insery statement against the root table in the
 	 * case of identifier generation strategies where the insert statement
 	 * executions actually generates the identifier value.
 	 *
 	 * @param includeProperty indices of the properties to include in the
 	 * insert statement.
 	 * @return The insert SQL statement string
 	 */
 	protected String generateIdentityInsertString(boolean[] includeProperty) {
 		Insert insert = identityDelegate.prepareIdentifierGeneratingInsert();
 		insert.setTableName( getTableName( 0 ) );
 
 		// add normal properties
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		addDiscriminatorToInsert( insert );
 
 		// delegate already handles PK columns
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 
 		return insert.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL that deletes a row by id (and version)
 	 */
 	protected String generateDeleteString(int j) {
 		Delete delete = new Delete()
 				.setTableName( getTableName( j ) )
 				.addPrimaryKeyColumns( getKeyColumns( j ) );
 		if ( j == 0 ) {
 			delete.setVersionColumnName( getVersionColumnName() );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( "delete " + getEntityName() );
 		}
 		return delete.toStatementString();
 	}
 
 	protected int dehydrate(
 			Serializable id,
 			Object[] fields,
 			boolean[] includeProperty,
 			boolean[][] includeColumns,
 			int j,
 			PreparedStatement st,
 			SessionImplementor session) throws HibernateException, SQLException {
 		return dehydrate( id, fields, null, includeProperty, includeColumns, j, st, session, 1 );
 	}
 
 	/**
 	 * Marshall the fields of a persistent instance to a prepared statement
 	 */
 	protected int dehydrate(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final boolean[][] includeColumns,
 	        final int j,
 	        final PreparedStatement ps,
 	        final SessionImplementor session,
 	        int index) throws SQLException, HibernateException {
 
         if (LOG.isTraceEnabled()) LOG.trace("Dehydrating entity: " + MessageHelper.infoString(this, id, getFactory()));
 
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				//index += getPropertyColumnSpan( i );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 
 		if ( rowId != null ) {
 			ps.setObject( index, rowId );
 			index += 1;
 		}
 		else if ( id != null ) {
 			getIdentifierType().nullSafeSet( ps, id, index, session );
 			index += getIdentifierColumnSpan();
 		}
 
 		return index;
 
 	}
 
 	/**
 	 * Unmarshall the fields of a persistent instance from a result set,
 	 * without resolving associations or collections. Question: should
 	 * this really be here, or should it be sent back to Loader?
 	 */
 	public Object[] hydrate(
 			final ResultSet rs,
 	        final Serializable id,
 	        final Object object,
 	        final Loadable rootLoadable,
 	        final String[][] suffixedPropertyColumns,
 	        final boolean allProperties,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 
         if (LOG.isTraceEnabled()) LOG.trace("Hydrating entity: " + MessageHelper.infoString(this, id, getFactory()));
 
 		final AbstractEntityPersister rootPersister = (AbstractEntityPersister) rootLoadable;
 
 		final boolean hasDeferred = rootPersister.hasSequentialSelect();
 		PreparedStatement sequentialSelect = null;
 		ResultSet sequentialResultSet = null;
 		boolean sequentialSelectEmpty = false;
 		try {
 
 			if ( hasDeferred ) {
 				final String sql = rootPersister.getSequentialSelect( getEntityName() );
 				if ( sql != null ) {
 					//TODO: I am not so sure about the exception handling in this bit!
 					sequentialSelect = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql );
 					rootPersister.getIdentifierType().nullSafeSet( sequentialSelect, id, 1, session );
 					sequentialResultSet = sequentialSelect.executeQuery();
 					if ( !sequentialResultSet.next() ) {
 						// TODO: Deal with the "optional" attribute in the <join> mapping;
 						// this code assumes that optional defaults to "true" because it
 						// doesn't actually seem to work in the fetch="join" code
 						//
 						// Note that actual proper handling of optional-ality here is actually
 						// more involved than this patch assumes.  Remember that we might have
 						// multiple <join/> mappings associated with a single entity.  Really
 						// a couple of things need to happen to properly handle optional here:
 						//  1) First and foremost, when handling multiple <join/>s, we really
 						//      should be using the entity root table as the driving table;
 						//      another option here would be to choose some non-optional joined
 						//      table to use as the driving table.  In all likelihood, just using
 						//      the root table is much simplier
 						//  2) Need to add the FK columns corresponding to each joined table
 						//      to the generated select list; these would then be used when
 						//      iterating the result set to determine whether all non-optional
 						//      data is present
 						// My initial thoughts on the best way to deal with this would be
 						// to introduce a new SequentialSelect abstraction that actually gets
 						// generated in the persisters (ok, SingleTable...) and utilized here.
 						// It would encapsulated all this required optional-ality checking...
 						sequentialSelectEmpty = true;
 					}
 				}
 			}
 
 			final String[] propNames = getPropertyNames();
 			final Type[] types = getPropertyTypes();
 			final Object[] values = new Object[types.length];
 			final boolean[] laziness = getPropertyLaziness();
 			final String[] propSubclassNames = getSubclassPropertySubclassNameClosure();
 
 			for ( int i = 0; i < types.length; i++ ) {
 				if ( !propertySelectable[i] ) {
 					values[i] = BackrefPropertyAccessor.UNKNOWN;
 				}
 				else if ( allProperties || !laziness[i] ) {
 					//decide which ResultSet to get the property value from:
 					final boolean propertyIsDeferred = hasDeferred &&
 							rootPersister.isSubclassPropertyDeferred( propNames[i], propSubclassNames[i] );
 					if ( propertyIsDeferred && sequentialSelectEmpty ) {
 						values[i] = null;
 					}
 					else {
 						final ResultSet propertyResultSet = propertyIsDeferred ? sequentialResultSet : rs;
 						final String[] cols = propertyIsDeferred ? propertyColumnAliases[i] : suffixedPropertyColumns[i];
 						values[i] = types[i].hydrate( propertyResultSet, cols, session, object );
 					}
 				}
 				else {
 					values[i] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 				}
 			}
 
 			if ( sequentialResultSet != null ) {
 				sequentialResultSet.close();
 			}
 
 			return values;
 
 		}
 		finally {
 			if ( sequentialSelect != null ) {
 				sequentialSelect.close();
 			}
 		}
 	}
 
 	protected boolean useInsertSelectIdentity() {
 		return !useGetGeneratedKeys() && getFactory().getDialect().supportsInsertSelectIdentity();
 	}
 
 	protected boolean useGetGeneratedKeys() {
 		return getFactory().getSettings().isGetGeneratedKeysEnabled();
 	}
 
 	protected String getSequentialSelect(String entityName) {
 		throw new UnsupportedOperationException("no sequential selects");
 	}
 
 	/**
 	 * Perform an SQL INSERT, and then retrieve a generated identifier.
 	 * <p/>
 	 * This form is used for PostInsertIdentifierGenerator-style ids (IDENTITY,
 	 * select, etc).
 	 */
 	protected Serializable insert(
 			final Object[] fields,
 	        final boolean[] notNull,
 	        String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
         if (LOG.isTraceEnabled()) {
             LOG.trace("Inserting entity: " + getEntityName() + " (native id)");
             if (isVersioned()) LOG.trace("Version: " + Versioning.getVersion(fields, this));
 		}
 
 		Binder binder = new Binder() {
 			public void bindValues(PreparedStatement ps) throws SQLException {
 				dehydrate( null, fields, notNull, propertyColumnInsertable, 0, ps, session );
 			}
 			public Object getEntity() {
 				return object;
 			}
 		};
 		return identityDelegate.performInsert( sql, session, binder );
 	}
 
 	public String getIdentitySelectString() {
 		//TODO: cache this in an instvar
 		return getFactory().getDialect().getIdentitySelectString(
 				getTableName(0),
 				getKeyColumns(0)[0],
 				getIdentifierType().sqlTypes( getFactory() )[0]
 		);
 	}
 
 	public String getSelectByUniqueKeyString(String propertyName) {
 		return new SimpleSelect( getFactory().getDialect() )
 			.setTableName( getTableName(0) )
 			.addColumns( getKeyColumns(0) )
 			.addCondition( getPropertyColumnNames(propertyName), "=?" )
 			.toStatementString();
 	}
 
 	private BasicBatchKey inserBatchKey;
 
 	/**
 	 * Perform an SQL INSERT.
 	 * <p/>
 	 * This for is used for all non-root tables as well as the root table
 	 * in cases where the identifier value is known before the insert occurs.
 	 */
 	protected void insert(
 			final Serializable id,
 	        final Object[] fields,
 	        final boolean[] notNull,
 	        final int j,
 	        final String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		//note: it is conceptually possible that a UserType could map null to
 		//	  a non-null value, so the following is arguable:
 		if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 			return;
 		}
 
         if (LOG.isTraceEnabled()) {
             LOG.trace("Inserting entity: " + MessageHelper.infoString(this, id, getFactory()));
             if (j == 0 && isVersioned()) LOG.trace("Version: " + Versioning.getVersion(fields, this));
 		}
 
 		// TODO : shouldn't inserts be Expectations.NONE?
 		final Expectation expectation = Expectations.appropriateExpectation( insertResultCheckStyles[j] );
 		// we can't batch joined inserts, *especially* not if it is an identity insert;
 		// nor can we batch statements where the expectation is based on an output param
 		final boolean useBatch = j == 0 && expectation.canBeBatched();
 		if ( useBatch && inserBatchKey == null ) {
 			inserBatchKey = new BasicBatchKey(
 					getEntityName() + "#INSERT",
 					expectation
 			);
 		}
 		final boolean callable = isInsertCallable( j );
 
 		try {
 			// Render the SQL query
 			final PreparedStatement insert;
 			if ( useBatch ) {
 				insert = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( inserBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				insert = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				int index = 1;
 				index += expectation.prepare( insert );
 
 				// Write the values of fields onto the prepared statement - we MUST use the state at the time the
 				// insert was issued (cos of foreign key constraints). Not necessarily the object's current state
 
 				dehydrate( id, fields, null, notNull, propertyColumnInsertable, j, insert, session, index );
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( inserBatchKey ).addToBatch();
 				}
 				else {
 					expectation.verifyOutcome( insert.executeUpdate(), insert, -1 );
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
 					insert.close();
 				}
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not insert: " + MessageHelper.infoString( this ),
 					sql
 			);
 		}
 
 	}
 
 	/**
 	 * Perform an SQL UPDATE or SQL INSERT
 	 */
 	protected void updateOrInsert(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( !isInverseTable( j ) ) {
 
 			final boolean isRowToUpdate;
 			if ( isNullableTable( j ) && oldFields != null && isAllNull( oldFields, j ) ) {
 				//don't bother trying to update, we know there is no row there yet
 				isRowToUpdate = false;
 			}
 			else if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 				//if all fields are null, we might need to delete existing row
 				isRowToUpdate = true;
 				delete( id, oldVersion, j, object, getSQLDeleteStrings()[j], session, null );
 			}
 			else {
 				//there is probably a row there, so try to update
 				//if no rows were updated, we will find out
 				isRowToUpdate = update( id, fields, oldFields, rowId, includeProperty, j, oldVersion, object, sql, session );
 			}
 
 			if ( !isRowToUpdate && !isAllNull( fields, j ) ) {
 				// assume that the row was not there since it previously had only null
 				// values, so do an INSERT instead
 				//TODO: does not respect dynamic-insert
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 
 		}
 
 	}
 
 	private BasicBatchKey updateBatchKey;
 
 	protected boolean update(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		final Expectation expectation = Expectations.appropriateExpectation( updateResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && expectation.canBeBatched() && isBatchable(); //note: updates to joined tables can't be batched...
 		if ( useBatch && updateBatchKey == null ) {
 			updateBatchKey = new BasicBatchKey(
 					getEntityName() + "#UPDATE",
 					expectation
 			);
 		}
 		final boolean callable = isUpdateCallable( j );
 		final boolean useVersion = j == 0 && isVersioned();
 
         if (LOG.isTraceEnabled()) {
             LOG.trace("Updating entity: " + MessageHelper.infoString(this, id, getFactory()));
             if (useVersion) LOG.trace("Existing version: " + oldVersion + " -> New version:" + fields[getVersionProperty()]);
 		}
 
 		try {
 			int index = 1; // starting index
 			final PreparedStatement update;
 			if ( useBatch ) {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( updateBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				index+= expectation.prepare( update );
 
 				//Now write the values of fields onto the prepared statement
 				index = dehydrate( id, fields, rowId, includeProperty, propertyColumnUpdateable, j, update, session, index );
 
 				// Write any appropriate versioning conditional parameters
 				if ( useVersion && Versioning.OPTIMISTIC_LOCK_VERSION == entityMetamodel.getOptimisticLockMode() ) {
 					if ( checkVersion( includeProperty ) ) {
 						getVersionType().nullSafeSet( update, oldVersion, index, session );
 					}
 				}
 				else if ( entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION && oldFields != null ) {
 					boolean[] versionability = getPropertyVersionability(); //TODO: is this really necessary????
 					boolean[] includeOldField = entityMetamodel.getOptimisticLockMode() == Versioning.OPTIMISTIC_LOCK_ALL ?
 							getPropertyUpdateability() : includeProperty;
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						boolean include = includeOldField[i] &&
 								isPropertyOfTable( i, j ) &&
 								versionability[i]; //TODO: is this really necessary????
 						if ( include ) {
 							boolean[] settable = types[i].toColumnNullness( oldFields[i], getFactory() );
 							types[i].nullSafeSet(
 									update,
 									oldFields[i],
 									index,
 									settable,
 									session
 								);
 							index += ArrayHelper.countTrue(settable);
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( updateBatchKey ).addToBatch();
 					return true;
 				}
 				else {
 					return check( update.executeUpdate(), id, j, expectation, update );
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
 					update.close();
 				}
 			}
 
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not update: " + MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 		}
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	/**
 	 * Perform an SQL DELETE
 	 */
 	protected void delete(
 			final Serializable id,
 			final Object version,
 			final int j,
 			final Object object,
 			final String sql,
 			final SessionImplementor session,
 			final Object[] loadedState) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		final boolean useVersion = j == 0 && isVersioned();
 		final boolean callable = isDeleteCallable( j );
 		final Expectation expectation = Expectations.appropriateExpectation( deleteResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && isBatchable() && expectation.canBeBatched();
 		if ( useBatch && deleteBatchKey == null ) {
 			deleteBatchKey = new BasicBatchKey(
 					getEntityName() + "#DELETE",
 					expectation
 			);
 		}
 
         if (LOG.isTraceEnabled()) {
             LOG.trace("Deleting entity: " + MessageHelper.infoString(this, id, getFactory()));
             if (useVersion) LOG.trace("Version: " + version);
 		}
 
 		if ( isTableCascadeDeleteEnabled( j ) ) {
             LOG.trace("Delete handled by foreign key constraint: " + getTableName(j));
 			return; //EARLY EXIT!
 		}
 
 		try {
 			//Render the SQL query
 			PreparedStatement delete;
 			int index = 1;
 			if ( useBatch ) {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( deleteBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 
 				index += expectation.prepare( delete );
 
 				// Do the key. The key is immutable so we can use the _current_ object state - not necessarily
 				// the state at the time the delete was issued
 				getIdentifierType().nullSafeSet( delete, id, index, session );
 				index += getIdentifierColumnSpan();
 
 				// We should use the _current_ object state (ie. after any updates that occurred during flush)
 
 				if ( useVersion ) {
 					getVersionType().nullSafeSet( delete, version, index, session );
 				}
 				else if ( entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION && loadedState != null ) {
 					boolean[] versionability = getPropertyVersionability();
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 							// this property belongs to the table and it is not specifically
 							// excluded from optimistic locking by optimistic-lock="false"
 							boolean[] settable = types[i].toColumnNullness( loadedState[i], getFactory() );
 							types[i].nullSafeSet( delete, loadedState[i], index, settable, session );
 							index += ArrayHelper.countTrue( settable );
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( deleteBatchKey ).addToBatch();
 				}
 				else {
 					check( delete.executeUpdate(), id, j, expectation, delete );
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
 					delete.close();
 				}
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not delete: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 
 		}
 
 	}
 
 	private String[] getUpdateStrings(boolean byRowId, boolean lazy) {
 		if ( byRowId ) {
 			return lazy ? getSQLLazyUpdateByRowIdStrings() : getSQLUpdateByRowIdStrings();
 		}
 		else {
 			return lazy ? getSQLLazyUpdateStrings() : getSQLUpdateStrings();
 		}
 	}
 
 	/**
 	 * Update an object
 	 */
 	public void update(
 			final Serializable id,
 	        final Object[] fields,
 	        final int[] dirtyFields,
 	        final boolean hasDirtyCollection,
 	        final Object[] oldFields,
 	        final Object oldVersion,
 	        final Object object,
 	        final Object rowId,
 	        final SessionImplementor session) throws HibernateException {
 
 		//note: dirtyFields==null means we had no snapshot, and we couldn't get one using select-before-update
 		//	  oldFields==null just means we had no snapshot to begin with (we might have used select-before-update to get the dirtyFields)
 
 		final boolean[] tableUpdateNeeded = getTableUpdateNeeded( dirtyFields, hasDirtyCollection );
 		final int span = getTableSpan();
 
 		final boolean[] propsToUpdate;
 		final String[] updateStrings;
 		EntityEntry entry = session.getPersistenceContext().getEntry( object );
 
 		// Ensure that an immutable or non-modifiable entity is not being updated unless it is
 		// in the process of being deleted.
 		if ( entry == null && ! isMutable() ) {
 			throw new IllegalStateException( "Updating immutable entity that is not in session yet!" );
 		}
 		if ( ( entityMetamodel.isDynamicUpdate() && dirtyFields != null ) ) {
 			// We need to generate the UPDATE SQL when dynamic-update="true"
 			propsToUpdate = getPropertiesToUpdate( dirtyFields, hasDirtyCollection );
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else if ( ! isModifiableEntity( entry ) ) {
 			// We need to generate UPDATE SQL when a non-modifiable entity (e.g., read-only or immutable)
 			// needs:
 			// - to have references to transient entities set to null before being deleted
 			// - to have version incremented do to a "dirty" association
 			// If dirtyFields == null, then that means that there are no dirty properties to
 			// to be updated; an empty array for the dirty fields needs to be passed to
 			// getPropertiesToUpdate() instead of null.
 			propsToUpdate = getPropertiesToUpdate(
 					( dirtyFields == null ? ArrayHelper.EMPTY_INT_ARRAY : dirtyFields ),
 					hasDirtyCollection
 			);
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else {
 			// For the case of dynamic-update="false", or no snapshot, we use the static SQL
 			updateStrings = getUpdateStrings(
 					rowId != null,
 					hasUninitializedLazyProperties( object, session.getEntityMode() )
 				);
 			propsToUpdate = getPropertyUpdateability( object, session.getEntityMode() );
 		}
 
 		for ( int j = 0; j < span; j++ ) {
 			// Now update only the tables with dirty properties (and the table with the version number)
 			if ( tableUpdateNeeded[j] ) {
 				updateOrInsert(
 						id,
 						fields,
 						oldFields,
 						j == 0 ? rowId : null,
 						propsToUpdate,
 						j,
 						oldVersion,
 						object,
 						updateStrings[j],
 						session
 					);
 			}
 		}
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
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
 
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
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
 
 	/**
 	 * Delete an object
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 			throws HibernateException {
 		final int span = getTableSpan();
 		boolean isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION;
 		Object[] loadedState = null;
 		if ( isImpliedOptimisticLocking ) {
 			// need to treat this as if it where optimistic-lock="all" (dirty does *not* make sense);
 			// first we need to locate the "loaded" state
 			//
 			// Note, it potentially could be a proxy, so doAfterTransactionCompletion the location the safe way...
-			EntityKey key = new EntityKey( id, this, session.getEntityMode() );
+			final EntityKey key = session.generateEntityKey( id, this );
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
 
 	private String[] generateSQLDeletStrings(Object[] loadedState) {
 		int span = getTableSpan();
 		String[] deleteStrings = new String[span];
 		for ( int j = span - 1; j >= 0; j-- ) {
 			Delete delete = new Delete()
 					.setTableName( getTableName( j ) )
 					.addPrimaryKeyColumns( getKeyColumns( j ) );
 			if ( getFactory().getSettings().isCommentsEnabled() ) {
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
         if (LOG.isDebugEnabled()) {
             LOG.debugf("Static SQL for entity: %s", getEntityName());
             if (sqlLazySelectString != null) LOG.debugf(" Lazy select: %s", sqlLazySelectString);
             if (sqlVersionSelectString != null) LOG.debugf(" Version select: %s", sqlVersionSelectString);
             if (sqlSnapshotSelectString != null) LOG.debugf(" Snapshot select: %s", sqlSnapshotSelectString);
 			for ( int j = 0; j < getTableSpan(); j++ ) {
                 LOG.debugf(" Insert %s: %s", j, getSQLInsertStrings()[j]);
                 LOG.debugf(" Update %s: %s", j, getSQLUpdateStrings()[j]);
                 LOG.debugf(" Delete %s: %s", j, getSQLDeleteStrings()[j]);
 			}
             if (sqlIdentityInsertString != null) LOG.debugf(" Identity insert: %s", sqlIdentityInsertString);
             if (sqlUpdateByRowIdString != null) LOG.debugf(" Update by row id (all fields): %s", sqlUpdateByRowIdString);
             if (sqlLazyUpdateByRowIdString != null) LOG.debugf(" Update by row id (non-lazy fields): %s",
                                                                sqlLazyUpdateByRowIdString);
             if (sqlInsertGeneratedValuesSelectString != null) LOG.debugf("Insert-generated property select: %s",
                                                                          sqlInsertGeneratedValuesSelectString);
             if (sqlUpdateGeneratedValuesSelectString != null) LOG.debugf("Update-generated property select: %s",
                                                                          sqlUpdateGeneratedValuesSelectString);
 		}
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		final StringBuffer sessionFilterFragment = new StringBuffer();
 		filterHelper.render( sessionFilterFragment, generateFilterConditionAlias( alias ), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return rootAlias;
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toFromFragmentString();
 	}
 
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toWhereFragmentString();
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return false;
 	}
 
 	protected JoinFragment createJoin(String name, boolean innerJoin, boolean includeSubclasses) {
 		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() ); //all joins join to the pk of the driving table
 		final JoinFragment join = getFactory().getDialect().createOuterJoinFragment();
 		final int tableSpan = getSubclassTableSpan();
 		for ( int j = 1; j < tableSpan; j++ ) { //notice that we skip the first table; it is the driving table!
 			final boolean joinIsIncluded = isClassOrSuperclassTable( j ) ||
 					( includeSubclasses && !isSubclassTableSequentialSelect( j ) && !isSubclassTableLazy( j ) );
 			if ( joinIsIncluded ) {
 				join.addJoin( getSubclassTableName( j ),
 						generateTableAlias( name, j ),
 						idCols,
 						getSubclassTableKeyColumns( j ),
 						innerJoin && isClassOrSuperclassTable( j ) && !isInverseTable( j ) && !isNullableTable( j ) ?
 						JoinFragment.INNER_JOIN : //we can inner join to superclass tables (the row MUST be there)
 						JoinFragment.LEFT_OUTER_JOIN //we can never inner join to subclass tables
 					);
 			}
 		}
 		return join;
 	}
 
 	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
 		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
 		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
 		for ( int i = 1; i < tableNumbers.length; i++ ) { //skip the driving table
 			final int j = tableNumbers[i];
 			jf.addJoin( getSubclassTableName( j ),
 					generateTableAlias( getRootAlias(), j ),
 					keyCols,
 					getSubclassTableKeyColumns( j ),
 					isInverseSubclassTable( j ) || isNullableSubclassTable( j ) ?
 					JoinFragment.LEFT_OUTER_JOIN :
 					JoinFragment.INNER_JOIN );
 		}
 		return jf;
 	}
 
 	protected SelectFragment createSelect(final int[] subclassColumnNumbers,
 										  final int[] subclassFormulaNumbers) {
 
 		SelectFragment selectFragment = new SelectFragment();
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < subclassColumnNumbers.length; i++ ) {
 			int columnNumber = subclassColumnNumbers[i];
 			if ( subclassColumnSelectableClosure[columnNumber] ) {
 				final String subalias = generateTableAlias( getRootAlias(), columnTableNumbers[columnNumber] );
 				selectFragment.addColumnTemplate( subalias, columnReaderTemplates[columnNumber], columnAliases[columnNumber] );
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
 		return StringHelper.join( "=? and ",
 				StringHelper.qualify( alias, getSubclassTableKeyColumns( tableNumber ) ) ) + "=?";
 	}
 
 	protected String renderSelect(
 			final int[] tableNumbers,
 	        final int[] columnNumbers,
 	        final int[] formulaNumbers) {
 
 		Arrays.sort( tableNumbers ); //get 'em in the right order (not that it really matters)
 
 		//render the where and from parts
 		int drivingTable = tableNumbers[0];
 		final String drivingAlias = generateTableAlias( getRootAlias(), drivingTable ); //we *could* regerate this inside each called method!
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
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "sequential select " + getEntityName() );
 		}
 		return select.toStatementString();
 	}
 
 	private String getRootAlias() {
 		return StringHelper.generateAlias( getEntityName() );
 	}
 
 	protected void postConstruct(Mapping mapping) throws MappingException {
 		initPropertyPaths(mapping);
 
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
 			identityDelegate = ( ( PostInsertIdentifierGenerator ) getIdentifierGenerator() )
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
 
 	public void postInstantiate() throws MappingException {
 
 		createLoaders();
 		createUniqueKeyLoaders();
 		createQueryLoader();
 
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
 		loaders.put( LockMode.OPTIMISTIC, createEntityLoader( LockMode.OPTIMISTIC) );
 		loaders.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, createEntityLoader(LockMode.OPTIMISTIC_FORCE_INCREMENT) );
 
 		loaders.put(
 				"merge",
 				new CascadeEntityLoader( this, CascadingAction.MERGE, getFactory() )
 			);
 		loaders.put(
 				"refresh",
 				new CascadeEntityLoader( this, CascadingAction.REFRESH, getFactory() )
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
 		return load( id, optionalObject, new LockOptions().setLockMode(lockMode), session );
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 			throws HibernateException {
 
         if (LOG.isTraceEnabled()) LOG.trace("Fetching entity: " + MessageHelper.infoString(this, id, getFactory()));
 
 		final UniqueEntityLoader loader = getAppropriateLoader(lockOptions, session );
 		return loader.load( id, optionalObject, session, lockOptions );
 	}
 
 	public void registerAffectingFetchProfile(String fetchProfileName) {
 		affectingFetchProfileNames.add( fetchProfileName );
 	}
 
 	private boolean isAffectedByEnabledFetchProfiles(SessionImplementor session) {
 		Iterator itr = session.getLoadQueryInfluencers().getEnabledFetchProfileNames().iterator();
 		while ( itr.hasNext() ) {
 			if ( affectingFetchProfileNames.contains( itr.next() ) ) {
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
 			// restirctions) these need to be next in precendence
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( session.getLoadQueryInfluencers().getInternalFetchProfile() != null && LockMode.UPGRADE.greaterThan( lockOptions.getLockMode() ) ) {
 			// Next, we consider whether an 'internal' fetch profile has been set.
 			// This indicates a special fetch profile Hibernate needs applied
 			// (for its merge loading process e.g.).
 			return ( UniqueEntityLoader ) getLoaders().get( session.getLoadQueryInfluencers().getInternalFetchProfile() );
 		}
 		else if ( isAffectedByEnabledFetchProfiles( session ) ) {
 			// If the session has associated influencers we need to adjust the
 			// SQL query used for loading based on those influencers
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else {
 			return ( UniqueEntityLoader ) getLoaders().get( lockOptions.getLockMode() );
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
 		final boolean[] propsToUpdate = new boolean[ entityMetamodel.getPropertySpan() ];
 		final boolean[] updateability = getPropertyUpdateability(); //no need to check laziness, dirty checking handles that
 		for ( int j = 0; j < dirtyProperties.length; j++ ) {
 			int property = dirtyProperties[j];
 			if ( updateability[property] ) {
 				propsToUpdate[property] = true;
 			}
 		}
 		if ( isVersioned() && updateability[getVersionProperty() ]) {
 			propsToUpdate[ getVersionProperty() ] =
 				Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
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
 	 * @param session The session in which the check is ccurring.
 	 * @return <tt>null</tt> or the indices of the dirty properties
 	 * @throws HibernateException
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findDirty(
 				entityMetamodel.getProperties(),
 				currentState,
 				previousState,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity, session.getEntityMode() ),
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
 	 * @param session The session in which the check is ccurring.
 	 * @return <tt>null</tt> or the indices of the modified properties
 	 * @throws HibernateException
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findModified(
 				entityMetamodel.getProperties(),
 				current,
 				old,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity, session.getEntityMode() ),
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
 	protected boolean[] getPropertyUpdateability(Object entity, EntityMode entityMode) {
 		return hasUninitializedLazyProperties( entity, entityMode ) ?
 				getNonLazyPropertyUpdateability() :
 				getPropertyUpdateability();
 	}
 
 	private void logDirtyProperties(int[] props) {
         if (LOG.isTraceEnabled()) {
 			for ( int i = 0; i < props.length; i++ ) {
 				String propertyName = entityMetamodel.getProperties()[ props[i] ].getName();
                 LOG.trace(StringHelper.qualify(getEntityName(), propertyName) + " is dirty");
 			}
 		}
 	}
 
 	protected EntityTuplizer getTuplizer(SessionImplementor session) {
 		return getTuplizer( session.getEntityMode() );
 	}
 
 	protected EntityTuplizer getTuplizer(EntityMode entityMode) {
 		return entityMetamodel.getTuplizer( entityMode );
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
 
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
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
 		return ( VersionType ) locateVersionType();
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
 		//if ( hasLazyProperties() ) {
 		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
 			FieldInterceptor interceptor = FieldInterceptionHelper.extractFieldInterceptor( entity );
 			if ( interceptor != null ) {
 				interceptor.setSession( session );
 			}
 			else {
 				FieldInterceptor fieldInterceptor = FieldInterceptionHelper.injectFieldInterceptor(
 						entity,
 						getEntityName(),
 						null,
 						session
 				);
 				fieldInterceptor.dirty();
 			}
 		}
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
 		final Object version = getVersion( entity, session.getEntityMode() );
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
 		if ( hasCache() ) {
 			CacheKey ck = new CacheKey(
 					id,
 					getIdentifierType(),
 					getRootEntityName(),
 					session.getEntityMode(),
 					session.getFactory()
 				);
 			if ( getCacheAccessStrategy().get( ck, session.getTimestamp() ) != null ) {
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
 		return propertyMapping.toType(propertyName);
 	}
 
 	public Type getType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return entityMetamodel.isSelectBeforeUpdate();
 	}
 
 	protected final int optimisticLockMode() {
 		return entityMetamodel.getOptimisticLockMode();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 		return entityMetamodel.getTuplizer( session.getEntityMode() )
 				.createProxy( id, session );
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
 
 	public boolean isInstrumented(EntityMode entityMode) {
 		EntityTuplizer tuplizer = entityMetamodel.getTuplizerOrNull(entityMode);
 		return tuplizer!=null && tuplizer.isInstrumented();
 	}
 
 	public boolean hasInsertGeneratedProperties() {
 		return entityMetamodel.hasInsertGeneratedValues();
 	}
 
 	public boolean hasUpdateGeneratedProperties() {
 		return entityMetamodel.hasUpdateGeneratedValues();
 	}
 
 	public boolean isVersionPropertyGenerated() {
 		return isVersioned() && ( getPropertyUpdateGenerationInclusions() [ getVersionProperty() ] != ValueInclusion.NONE );
 	}
 
 	public boolean isVersionPropertyInsertable() {
 		return isVersioned() && getPropertyInsertability() [ getVersionProperty() ];
 	}
 
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		getTuplizer( session ).afterInitialize( entity, lazyPropertiesAreUnfetched, session );
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
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return entityMetamodel.getPropertyInsertGenerationInclusions();
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return entityMetamodel.getPropertyUpdateGenerationInclusions();
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
 
 	public final Class getMappedClass(EntityMode entityMode) {
 		Tuplizer tup = entityMetamodel.getTuplizerOrNull(entityMode);
 		return tup==null ? null : tup.getMappedClass();
 	}
 
 	public boolean implementsLifecycle(EntityMode entityMode) {
 		return getTuplizer( entityMode ).isLifecycleImplementor();
 	}
 
 	public Class getConcreteProxyClass(EntityMode entityMode) {
 		return getTuplizer( entityMode ).getConcreteProxyClass();
 	}
 
 	public void setPropertyValues(Object object, Object[] values, EntityMode entityMode)
 			throws HibernateException {
 		getTuplizer( entityMode ).setPropertyValues( object, values );
 	}
 
 	public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode)
 			throws HibernateException {
 		getTuplizer( entityMode ).setPropertyValue( object, i, value );
 	}
 
 	public Object[] getPropertyValues(Object object, EntityMode entityMode)
 			throws HibernateException {
 		return getTuplizer( entityMode ).getPropertyValues( object );
 	}
 
 	public Object getPropertyValue(Object object, int i, EntityMode entityMode)
 			throws HibernateException {
 		return getTuplizer( entityMode ).getPropertyValue( object , i );
 	}
 
 	public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode)
 			throws HibernateException {
 		return getTuplizer( entityMode ).getPropertyValue( object, propertyName );
 	}
 
 	public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
 		return getTuplizer( entityMode ).getIdentifier( object, null );
 	}
 
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return getTuplizer( session.getEntityMode() ).getIdentifier( entity, session );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setIdentifier(Object entity, Serializable id, EntityMode entityMode)
 			throws HibernateException {
 		getTuplizer( entityMode ).setIdentifier( entity, id, null );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		getTuplizer( session ).setIdentifier( entity, id, session );
 	}
 
 	public Object getVersion(Object object, EntityMode entityMode)
 			throws HibernateException {
 		return getTuplizer( entityMode ).getVersion( object );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object instantiate(Serializable id, EntityMode entityMode)
 			throws HibernateException {
 		return getTuplizer( entityMode ).instantiate( id, null );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object instantiate(Serializable id, SessionImplementor session)
 			throws HibernateException {
 		return getTuplizer( session ).instantiate( id, session );
 	}
 
 	public boolean isInstance(Object object, EntityMode entityMode) {
 		return getTuplizer( entityMode ).isInstance( object );
 	}
 
 	public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
 		return getTuplizer( entityMode ).hasUninitializedLazyProperties( object );
 	}
 
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
 		getTuplizer( entityMode ).resetIdentifier( entity, currentId, currentVersion, null );
 	}
 
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		getTuplizer( session ).resetIdentifier( entity, currentId, currentVersion, session );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public EntityPersister getSubclassEntityPersister(
 			Object instance,
 			SessionFactoryImplementor factory,
 			EntityMode entityMode) {
 		if ( !hasSubclasses() ) {
 			return this;
 		}
 		else {
 			final String concreteEntityName = getTuplizer( entityMode )
 					.determineConcreteSubclassEntityName( instance, factory );
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
 
 	public EntityMode guessEntityMode(Object object) {
 		return entityMetamodel.guessEntityMode(object);
 	}
 
 	public boolean isMultiTable() {
 		return false;
 	}
 
 	public String getTemporaryIdTableName() {
 		return temporaryIdTableName;
 	}
 
 	public String getTemporaryIdTableDDL() {
 		return temporaryIdTableDDL;
 	}
 
 	protected int getPropertySpan() {
 		return entityMetamodel.getPropertySpan();
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException {
 		return getTuplizer( session.getEntityMode() ).getPropertyValuesToInsert( object, mergeMap, session );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasInsertGeneratedProperties() ) {
 			throw new AssertionFailure("no insert-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlInsertGeneratedValuesSelectString, getPropertyInsertGenerationInclusions() );
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasUpdateGeneratedProperties() ) {
 			throw new AssertionFailure("no update-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlUpdateGeneratedValuesSelectString, getPropertyUpdateGenerationInclusions() );
 	}
 
 	private void processGeneratedProperties(
 			Serializable id,
 	        Object entity,
 	        Object[] state,
 	        SessionImplementor session,
 	        String selectionSQL,
 	        ValueInclusion[] includeds) {
 		// force immediate execution of the insert batch (if one)
 		session.getTransactionCoordinator().getJdbcCoordinator().executeBatch();
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( selectionSQL );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = ps.executeQuery();
 				try {
 					if ( !rs.next() ) {
 						throw new HibernateException(
 								"Unable to locate row for retrieval of generated properties: " +
 								MessageHelper.infoString( this, id, getFactory() )
 							);
 					}
 					for ( int i = 0; i < getPropertySpan(); i++ ) {
 						if ( includeds[i] != ValueInclusion.NONE ) {
 							Object hydratedState = getPropertyTypes()[i].hydrate( rs, getPropertyAliases( "", i ), session, entity );
 							state[i] = getPropertyTypes()[i].resolve( hydratedState, session, entity );
 							setPropertyValue( entity, i, state[i], session.getEntityMode() );
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						rs.close();
 					}
 				}
 			}
 			finally {
 				ps.close();
 			}
 		}
 		catch( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"unable to select generated column values",
 					selectionSQL
 			);
 		}
 
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
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !hasNaturalIdentifier() ) {
 			throw new MappingException( "persistent class did not define a natural-id : " + MessageHelper.infoString( this ) );
 		}
         if (LOG.isTraceEnabled()) LOG.trace("Getting current natural-id snapshot state for: "
                                             + MessageHelper.infoString(this, id, getFactory()));
 
 		int[] naturalIdPropertyIndexes = getNaturalIdentifierProperties();
 		int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
 		boolean[] naturalIdMarkers = new boolean[ getPropertySpan() ];
 		Type[] extractionTypes = new Type[ naturalIdPropertyCount ];
 		for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 			extractionTypes[i] = getPropertyTypes()[ naturalIdPropertyIndexes[i] ];
 			naturalIdMarkers[ naturalIdPropertyIndexes[i] ] = true;
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// TODO : look at perhaps caching this...
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id state " + getEntityName() );
 		}
 		select.setSelectClause( concretePropertySelectFragmentSansLeadingComma( getRootAlias(), naturalIdMarkers ) );
 		select.setFromClause( fromTableFragment( getRootAlias() ) + fromJoinFragment( getRootAlias(), true, false ) );
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String whereClause = new StringBuffer()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		String sql = select.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 		///////////////////////////////////////////////////////////////////////
 
 		Object[] snapshot = new Object[ naturalIdPropertyCount ];
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = ps.executeQuery();
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
-					final EntityKey key = new EntityKey( id, this, session.getEntityMode() );
+					final EntityKey key = session.generateEntityKey( id, this );
 					Object owner = session.getPersistenceContext().getEntity( key );
 					for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 						snapshot[i] = extractionTypes[i].hydrate( rs, getPropertyAliases( "", naturalIdPropertyIndexes[i] ), session, null );
 						if (extractionTypes[i].isEntityType()) {
 							snapshot[i] = extractionTypes[i].resolve(snapshot[i], session, owner);
 						}
 					}
 					return snapshot;
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				ps.close();
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 			        sql
 			);
 		}
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
 
 	public void setPropertyValue(Object object, String propertyName, Object value, EntityMode entityMode)
 			throws HibernateException {
 		getTuplizer( entityMode ).setPropertyValue( object, propertyName, value );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
index 74244c4231..a9cb2f407e 100755
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/NamedQueryLoader.java
@@ -1,87 +1,86 @@
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
 package org.hibernate.persister.entity;
+
 import java.io.Serializable;
+
+import org.jboss.logging.Logger;
+
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateLogger;
 import org.hibernate.LockOptions;
-import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.impl.AbstractQueryImpl;
 import org.hibernate.loader.entity.UniqueEntityLoader;
-import org.jboss.logging.Logger;
 
 /**
  * Not really a <tt>Loader</tt>, just a wrapper around a
  * named query.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class NamedQueryLoader implements UniqueEntityLoader {
 	private final String queryName;
 	private final EntityPersister persister;
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class, NamedQueryLoader.class.getName());
 
 	public NamedQueryLoader(String queryName, EntityPersister persister) {
 		super();
 		this.queryName = queryName;
 		this.persister = persister;
 	}
 
 	public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {
         if (lockOptions != null) LOG.debugf("Ignoring lock-options passed to named query loader");
 		return load( id, optionalObject, session );
 	}
 
 	public Object load(Serializable id, Object optionalObject, SessionImplementor session) {
         LOG.debugf("Loading entity: %s using named query: %s", persister.getEntityName(), queryName);
 
 		AbstractQueryImpl query = (AbstractQueryImpl) session.getNamedQuery(queryName);
 		if ( query.hasNamedParameters() ) {
 			query.setParameter(
 					query.getNamedParameters()[0],
 					id,
 					persister.getIdentifierType()
 				);
 		}
 		else {
 			query.setParameter( 0, id, persister.getIdentifierType() );
 		}
 		query.setOptionalId(id);
 		query.setOptionalEntityName( persister.getEntityName() );
 		query.setOptionalObject(optionalObject);
 		query.setFlushMode( FlushMode.MANUAL );
 		query.list();
 
 		// now look up the object we are really interested in!
-		// (this lets us correctly handle proxies and multi-row
-		// or multi-column queries)
-		return session.getPersistenceContext()
-				.getEntity( new EntityKey( id, persister, session.getEntityMode() ) );
+		// (this lets us correctly handle proxies and multi-row or multi-column queries)
+		return session.getPersistenceContext().getEntity( session.generateEntityKey( id, persister ) );
 
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java
index 9f4815f8a4..1a32d6a30f 100755
--- a/hibernate-core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/AbstractLazyInitializer.java
@@ -1,346 +1,314 @@
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
 package org.hibernate.proxy;
+
 import java.io.Serializable;
+
 import org.hibernate.HibernateException;
 import org.hibernate.LazyInitializationException;
 import org.hibernate.SessionException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Convenience base class for lazy initialization handlers.  Centralizes the basic plumbing of doing lazy
  * initialization freeing subclasses to acts as essentially adapters to their intended entity mode and/or
  * proxy generation strategy.
  *
  * @author Gavin King
  */
 public abstract class AbstractLazyInitializer implements LazyInitializer {
 	private String entityName;
 	private Serializable id;
 	private Object target;
 	private boolean initialized;
 	private boolean readOnly;
 	private boolean unwrap;
 	private transient SessionImplementor session;
 	private Boolean readOnlyBeforeAttachedToSession;
 
 	/**
 	 * For serialization from the non-pojo initializers (HHH-3309)
 	 */
 	protected AbstractLazyInitializer() {
 	}
 
 	/**
 	 * Main constructor.
 	 *
 	 * @param entityName The name of the entity being proxied.
 	 * @param id The identifier of the entity being proxied.
 	 * @param session The session owning the proxy.
 	 */
 	protected AbstractLazyInitializer(String entityName, Serializable id, SessionImplementor session) {
 		this.entityName = entityName;
 		this.id = id;
 		// initialize other fields depending on session state
 		if ( session == null ) {
 			unsetSession();
 		}
 		else {
 			setSession( session );
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final String getEntityName() {
 		return entityName;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final Serializable getIdentifier() {
 		return id;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final void setIdentifier(Serializable id) {
 		this.id = id;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final boolean isUninitialized() {
 		return !initialized;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final SessionImplementor getSession() {
 		return session;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final void setSession(SessionImplementor s) throws HibernateException {
 		if ( s != session ) {
 			// check for s == null first, since it is least expensive
 			if ( s == null ){
 				unsetSession();
 			}
 			else if ( isConnectedToSession() ) {
 				//TODO: perhaps this should be some other RuntimeException...
 				throw new HibernateException("illegally attempted to associate a proxy with two open Sessions");
 			}
 			else {
 				// s != null
 				session = s;
 				if ( readOnlyBeforeAttachedToSession == null ) {
 					// use the default read-only/modifiable setting
 					final EntityPersister persister = s.getFactory().getEntityPersister( entityName );
 					setReadOnly( s.getPersistenceContext().isDefaultReadOnly() || ! persister.isMutable() );
 				}
 				else {
 					// use the read-only/modifiable setting indicated during deserialization
 					setReadOnly( readOnlyBeforeAttachedToSession.booleanValue() );
 					readOnlyBeforeAttachedToSession = null;
 				}
 			}
 		}
 	}
 
 	private static EntityKey generateEntityKeyOrNull(Serializable id, SessionImplementor s, String entityName) {
 		if ( id == null || s == null || entityName == null ) {
 			return null;
 		}
-		return new EntityKey( id, s.getFactory().getEntityPersister( entityName ), s.getEntityMode() );
+		return s.generateEntityKey( id, s.getFactory().getEntityPersister( entityName ) );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final void unsetSession() {
 		session = null;
 		readOnly = false;
 		readOnlyBeforeAttachedToSession = null;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final void initialize() throws HibernateException {
 		if (!initialized) {
 			if ( session==null ) {
 				throw new LazyInitializationException("could not initialize proxy - no Session");
 			}
 			else if ( !session.isOpen() ) {
 				throw new LazyInitializationException("could not initialize proxy - the owning Session was closed");
 			}
 			else if ( !session.isConnected() ) {
 				throw new LazyInitializationException("could not initialize proxy - the owning Session is disconnected");
 			}
 			else {
 				target = session.immediateLoad(entityName, id);
 				initialized = true;
 				checkTargetState();
 			}
 		}
 		else {
 			checkTargetState();
 		}
 	}
 
 	private void checkTargetState() {
 		if ( !unwrap ) {
 			if ( target == null ) {
 				getSession().getFactory().getEntityNotFoundDelegate().handleEntityNotFound( entityName, id );
 			}
 		}
 	}
 
 	/**
 	 * Getter for property 'connectedToSession'.
 	 *
 	 * @return Value for property 'connectedToSession'.
 	 */
 	protected final boolean isConnectedToSession() {
 		return getProxyOrNull() != null;
 	}
 
 	private Object getProxyOrNull() {
 		final EntityKey entityKey = generateEntityKeyOrNull( getIdentifier(), session, getEntityName() );
 		if ( entityKey != null && session != null && session.isOpen() ) {
 			return session.getPersistenceContext().getProxy( entityKey );
 		}
 		return null;
 	}
 
-	/**
-	 * Return the underlying persistent object, initializing if necessary
-	 */
+	@Override
 	public final Object getImplementation() {
 		initialize();
 		return target;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final void setImplementation(Object target) {
 		this.target = target;
 		initialized = true;
 	}
 
-	/**
-	 * Return the underlying persistent object in the given <tt>Session</tt>, or null,
-	 * do not initialize the proxy
-	 */
+	@Override
 	public final Object getImplementation(SessionImplementor s) throws HibernateException {
 		final EntityKey entityKey = generateEntityKeyOrNull( getIdentifier(), s, getEntityName() );
 		return ( entityKey == null ? null : s.getPersistenceContext().getEntity( entityKey ) );
 	}
 
 	/**
 	 * Getter for property 'target'.
 	 * <p/>
 	 * Same as {@link #getImplementation()} except that this method will not force initialization.
 	 *
 	 * @return Value for property 'target'.
 	 */
 	protected final Object getTarget() {
 		return target;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final boolean isReadOnlySettingAvailable() {
 		return ( session != null && ! session.isClosed() );
 	}
 
 	private void errorIfReadOnlySettingNotAvailable() {
 		if ( session == null ) {
 			throw new TransientObjectException(
 					"Proxy is detached (i.e, session is null). The read-only/modifiable setting is only accessible when the proxy is associated with an open session." );
 		}
 		if ( session.isClosed() ) {
 			throw new SessionException(
 					"Session is closed. The read-only/modifiable setting is only accessible when the proxy is associated with an open session." );
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final boolean isReadOnly() {
 		errorIfReadOnlySettingNotAvailable();
 		return readOnly;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public final void setReadOnly(boolean readOnly) {
 		errorIfReadOnlySettingNotAvailable();
 		// only update if readOnly is different from current setting
 		if ( this.readOnly != readOnly ) {
 			final EntityPersister persister = session.getFactory().getEntityPersister( entityName );
 			if ( ! persister.isMutable() && ! readOnly ) {
 				throw new IllegalStateException( "cannot make proxies for immutable entities modifiable");
 			}
 			this.readOnly = readOnly;
 			if ( initialized ) {
 				EntityKey key = generateEntityKeyOrNull( getIdentifier(), session, getEntityName() );
 				if ( key != null && session.getPersistenceContext().containsEntity( key ) ) {
 					session.getPersistenceContext().setReadOnly( target, readOnly );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Get the read-only/modifiable setting that should be put in affect when it is
 	 * attached to a session.
 	 *
 	 * This method should only be called during serialization when read-only/modifiable setting
 	 * is not available (i.e., isReadOnlySettingAvailable() == false)
 	 *
 	 * @returns, null, if the default setting should be used;
 	 *           true, for read-only;
 	 *           false, for modifiable
 	 * @throws IllegalStateException if isReadOnlySettingAvailable() == true
 	 */
 	protected final Boolean isReadOnlyBeforeAttachedToSession() {
 		if ( isReadOnlySettingAvailable() ) {
 			throw new IllegalStateException(
 					"Cannot call isReadOnlyBeforeAttachedToSession when isReadOnlySettingAvailable == true"
 			);
 		}
 		return readOnlyBeforeAttachedToSession;
 	}
 
 	/**
 	 * Set the read-only/modifiable setting that should be put in affect when it is
 	 * attached to a session.
 	 *
 	 * This method should only be called during deserialization, before associating
 	 * the proxy with a session.
 	 *
 	 * @param readOnlyBeforeAttachedToSession, the read-only/modifiable setting to use when
 	 * associated with a session; null indicates that the default should be used.
 	 * @throws IllegalStateException if isReadOnlySettingAvailable() == true
 	 */
 	/* package-private */
 	final void setReadOnlyBeforeAttachedToSession(Boolean readOnlyBeforeAttachedToSession) {
 		if ( isReadOnlySettingAvailable() ) {
 			throw new IllegalStateException(
 					"Cannot call setReadOnlyBeforeAttachedToSession when isReadOnlySettingAvailable == true"
 			);
 		}
 		this.readOnlyBeforeAttachedToSession = readOnlyBeforeAttachedToSession;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isUnwrap() {
 		return unwrap;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void setUnwrap(boolean unwrap) {
 		this.unwrap = unwrap;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java
index 27ba8678f4..13bb859807 100644
--- a/hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/pojo/BasicLazyInitializer.java
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
 package org.hibernate.proxy.pojo;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
+
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.proxy.AbstractLazyInitializer;
 import org.hibernate.type.CompositeType;
 
 /**
  * Lazy initializer for POJOs
  *
  * @author Gavin King
  */
 public abstract class BasicLazyInitializer extends AbstractLazyInitializer {
 
 	protected static final Object INVOKE_IMPLEMENTATION = new MarkerObject("INVOKE_IMPLEMENTATION");
 
 	protected Class persistentClass;
 	protected Method getIdentifierMethod;
 	protected Method setIdentifierMethod;
 	protected boolean overridesEquals;
 	private Object replacement;
 	protected CompositeType componentIdType;
 
 	protected BasicLazyInitializer(
 			String entityName,
 	        Class persistentClass,
 	        Serializable id,
 	        Method getIdentifierMethod,
 	        Method setIdentifierMethod,
 	        CompositeType componentIdType,
 	        SessionImplementor session) {
 		super(entityName, id, session);
 		this.persistentClass = persistentClass;
 		this.getIdentifierMethod = getIdentifierMethod;
 		this.setIdentifierMethod = setIdentifierMethod;
 		this.componentIdType = componentIdType;
 		overridesEquals = ReflectHelper.overridesEquals(persistentClass);
 	}
 
 	protected abstract Object serializableProxy();
 
 	@SuppressWarnings({ "UnnecessaryBoxing" })
 	protected final Object invoke(Method method, Object[] args, Object proxy) throws Throwable {
 		String methodName = method.getName();
 		int params = args.length;
 
 		if ( params==0 ) {
 			if ( "writeReplace".equals(methodName) ) {
 				return getReplacement();
 			}
 			else if ( !overridesEquals && "hashCode".equals(methodName) ) {
 				return Integer.valueOf( System.identityHashCode(proxy) );
 			}
 			else if ( isUninitialized() && method.equals(getIdentifierMethod) ) {
 				return getIdentifier();
 			}
 			else if ( "getHibernateLazyInitializer".equals(methodName) ) {
 				return this;
 			}
 		}
 		else if ( params==1 ) {
 			if ( !overridesEquals && "equals".equals(methodName) ) {
 				return args[0]==proxy ? Boolean.TRUE : Boolean.FALSE;
 			}
 			else if ( method.equals(setIdentifierMethod) ) {
 				initialize();
 				setIdentifier( (Serializable) args[0] );
 				return INVOKE_IMPLEMENTATION;
 			}
 		}
 
 		//if it is a property of an embedded component, invoke on the "identifier"
 		if ( componentIdType!=null && componentIdType.isMethodOf(method) ) {
 			return method.invoke( getIdentifier(), args );
 		}
 
 		// otherwise:
 		return INVOKE_IMPLEMENTATION;
 
 	}
 
 	private Object getReplacement() {
-
 		final SessionImplementor session = getSession();
 		if ( isUninitialized() && session != null && session.isOpen()) {
-			final EntityKey key = new EntityKey(
+			final EntityKey key = session.generateEntityKey(
 					getIdentifier(),
-			        session.getFactory().getEntityPersister( getEntityName() ),
-			        session.getEntityMode()
-				);
+					session.getFactory().getEntityPersister( getEntityName() )
+			);
 			final Object entity = session.getPersistenceContext().getEntity(key);
 			if (entity!=null) setImplementation( entity );
 		}
 
 		if ( isUninitialized() ) {
 			if (replacement==null) {
 				replacement = serializableProxy();
 			}
 			return replacement;
 		}
 		else {
 			return getTarget();
 		}
 
 	}
 
 	public final Class getPersistentClass() {
 		return persistentClass;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
index d0ee858115..ae9b1ef769 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
@@ -1,671 +1,673 @@
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
+
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
+
+import org.jboss.logging.Logger;
+
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
 import org.hibernate.event.PersistEvent;
 import org.hibernate.id.Assigned;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.property.Getter;
 import org.hibernate.property.Setter;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.ProxyFactory;
 import org.hibernate.tuple.Instantiator;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.VersionProperty;
 import org.hibernate.type.ComponentType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
-import org.jboss.logging.Logger;
 
 
 /**
  * Support for tuplizers relating to entities.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 public abstract class AbstractEntityTuplizer implements EntityTuplizer {
 
     private static final HibernateLogger LOG = Logger.getMessageLogger(HibernateLogger.class,
                                                                        AbstractEntityTuplizer.class.getName());
 
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
                             LOG.debugf("Performing implicit derived identity cascade");
 							final PersistEvent event = new PersistEvent( null, propertyValues[i], (EventSource) session );
 							for ( int x = 0; x < session.getListeners().getPersistEventListeners().length; x++ ) {
 								session.getListeners().getPersistEventListeners()[x].onPersist( event );
 
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
-					final EntityKey entityKey = new EntityKey(
+					final EntityKey entityKey = session.generateEntityKey(
 							(Serializable) extractedValues[i],
-							session.getFactory().getEntityPersister( associatedEntityName ),
-							entityMode
+							session.getFactory().getEntityPersister( associatedEntityName )
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
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
index 4e61f3fc17..04d83ce218 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
@@ -1,297 +1,299 @@
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
+
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
+
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.ForeignKeys;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * A many-to-one association to an entity.
  *
  * @author Gavin King
  */
 public class ManyToOneType extends EntityType {
 	private final boolean ignoreNotFound;
 	private boolean isLogicalOneToOne;
 
 	/**
 	 * Creates a many-to-one association type with the given referenced entity.
 	 *
 	 * @param scope The scope for this instance.
 	 * @param referencedEntityName The name iof the referenced entity
 	 */
 	public ManyToOneType(TypeFactory.TypeScope scope, String referencedEntityName) {
 		this( scope, referencedEntityName, false );
 	}
 
 	/**
 	 * Creates a many-to-one association type with the given referenced entity and the
 	 * given laziness characteristic
 	 *
 	 * @param scope The scope for this instance.
 	 * @param referencedEntityName The name iof the referenced entity
 	 * @param lazy Should the association be handled lazily
 	 */
 	public ManyToOneType(TypeFactory.TypeScope scope, String referencedEntityName, boolean lazy) {
 		this( scope, referencedEntityName, null, lazy, true, false, false, false );
 	}
 
 	public ManyToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			boolean ignoreNotFound,
 			boolean isLogicalOneToOne) {
 		super( scope, referencedEntityName, uniqueKeyPropertyName, !lazy, isEmbeddedInXML, unwrapProxy );
 		this.ignoreNotFound = ignoreNotFound;
 		this.isLogicalOneToOne = isLogicalOneToOne;
 	}
 
 	protected boolean isNullable() {
 		return ignoreNotFound;
 	}
 
 	public boolean isAlwaysDirtyChecked() {
 		// always need to dirty-check, even when non-updateable;
 		// this ensures that when the association is updated,
 		// the entity containing this association will be updated
 		// in the cache
 		return true;
 	}
 
 	public boolean isOneToOne() {
 		return false;
 	}
 
 	public boolean isLogicalOneToOne() {
 		return isLogicalOneToOne;
 	}
 
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		// our column span is the number of columns in the PK
 		return getIdentifierOrUniqueKeyType( mapping ).getColumnSpan( mapping );
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return getIdentifierOrUniqueKeyType( mapping ).sqlTypes( mapping );
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return getIdentifierOrUniqueKeyType( mapping ).dictatedSizes( mapping );
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return getIdentifierOrUniqueKeyType( mapping ).defaultSizes( mapping );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		getIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeSet( st, getIdentifier( value, session ), index, settable, session );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 		getIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeSet( st, getIdentifier( value, session ), index, session );
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT;
 	}
 
 	public Object hydrate(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		// return the (fully resolved) identifier value, but do not resolve
 		// to the actual referenced entity instance
 		// NOTE: the owner of the association is not really the owner of the id!
 		Serializable id = (Serializable) getIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeGet( rs, names, session, null );
 		scheduleBatchLoadIfNeeded( id, session );
 		return id;
 	}
 
 	/**
 	 * Register the entity as batch loadable, if enabled
 	 */
 	@SuppressWarnings({ "JavaDoc" })
 	private void scheduleBatchLoadIfNeeded(Serializable id, SessionImplementor session) throws MappingException {
 		//cannot batch fetch by unique key (property-ref associations)
 		if ( uniqueKeyPropertyName == null && id != null ) {
-			EntityPersister persister = session.getFactory().getEntityPersister( getAssociatedEntityName() );
-			EntityKey entityKey = new EntityKey( id, persister, session.getEntityMode() );
+			final EntityPersister persister = session.getFactory().getEntityPersister( getAssociatedEntityName() );
+			final EntityKey entityKey = session.generateEntityKey( id, persister );
 			if ( !session.getPersistenceContext().containsEntity( entityKey ) ) {
 				session.getPersistenceContext().getBatchFetchQueue().addBatchLoadableEntityKey( entityKey );
 			}
 		}
 	}
 	
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 
 	public boolean isModified(
 			Object old,
 			Object current,
 			boolean[] checkable,
 			SessionImplementor session) throws HibernateException {
 		if ( current == null ) {
 			return old!=null;
 		}
 		if ( old == null ) {
 			// we already know current is not null...
 			return true;
 		}
 		// the ids are fully resolved, so compare them with isDirty(), not isModified()
 		return getIdentifierOrUniqueKeyType( session.getFactory() )
 				.isDirty( old, getIdentifier( current, session ), session );
 	}
 
 	public Serializable disassemble(
 			Object value,
 			SessionImplementor session,
 			Object owner) throws HibernateException {
 
 		if ( isNotEmbedded( session ) ) {
 			return getIdentifierType( session ).disassemble( value, session, owner );
 		}
 		
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			// cache the actual id of the object, not the value of the
 			// property-ref, which might not be initialized
 			Object id = ForeignKeys.getEntityIdentifierIfNotUnsaved( 
 					getAssociatedEntityName(), 
 					value, 
 					session
 			);
 			if ( id == null ) {
 				throw new AssertionFailure(
 						"cannot cache a reference to an object with a null id: " + 
 						getAssociatedEntityName()
 				);
 			}
 			return getIdentifierType( session ).disassemble( id, session, owner );
 		}
 	}
 
 	public Object assemble(
 			Serializable oid,
 			SessionImplementor session,
 			Object owner) throws HibernateException {
 		
 		//TODO: currently broken for unique-key references (does not detect
 		//      change to unique key property of the associated object)
 		
 		Serializable id = assembleId( oid, session );
 
 		if ( isNotEmbedded( session ) ) {
 			return id;
 		}
 		
 		if ( id == null ) {
 			return null;
 		}
 		else {
 			return resolveIdentifier( id, session );
 		}
 	}
 
 	private Serializable assembleId(Serializable oid, SessionImplementor session) {
 		//the owner of the association is not the owner of the id
 		return ( Serializable ) getIdentifierType( session ).assemble( oid, session, null );
 	}
 
 	public void beforeAssemble(Serializable oid, SessionImplementor session) {
 		scheduleBatchLoadIfNeeded( assembleId( oid, session ), session );
 	}
 	
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan( mapping ) ];
 		if ( value != null ) {
 			Arrays.fill( result, true );
 		}
 		return result;
 	}
 	
 	public boolean isDirty(
 			Object old,
 			Object current,
 			SessionImplementor session) throws HibernateException {
 		if ( isSame( old, current, session.getEntityMode() ) ) {
 			return false;
 		}
 		Object oldid = getIdentifier( old, session );
 		Object newid = getIdentifier( current, session );
 		return getIdentifierType( session ).isDirty( oldid, newid, session );
 	}
 
 	public boolean isDirty(
 			Object old,
 			Object current,
 			boolean[] checkable,
 			SessionImplementor session) throws HibernateException {
 		if ( isAlwaysDirtyChecked() ) {
 			return isDirty( old, current, session );
 		}
 		else {
 			if ( isSame( old, current, session.getEntityMode() ) ) {
 				return false;
 			}
 			Object oldid = getIdentifier( old, session );
 			Object newid = getIdentifier( current, session );
 			return getIdentifierType( session ).isDirty( oldid, newid, checkable, session );
 		}
 		
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
index 4edb7a97a1..731a9e76cf 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
@@ -1,182 +1,176 @@
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
+
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
+
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * A one-to-one association to an entity
  * @author Gavin King
  */
 public class OneToOneType extends EntityType {
 
 	private final ForeignKeyDirection foreignKeyType;
 	private final String propertyName;
 	private final String entityName;
 
 	public OneToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			ForeignKeyDirection foreignKeyType,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			String entityName,
 			String propertyName) {
 		super( scope, referencedEntityName, uniqueKeyPropertyName, !lazy, isEmbeddedInXML, unwrapProxy );
 		this.foreignKeyType = foreignKeyType;
 		this.propertyName = propertyName;
 		this.entityName = entityName;
 	}
 	
 	public String getPropertyName() {
 		return propertyName;
 	}
 	
 	public boolean isNull(Object owner, SessionImplementor session) {
-		
 		if ( propertyName != null ) {
-			
-			EntityPersister ownerPersister = session.getFactory()
-					.getEntityPersister(entityName); 
-			Serializable id = session.getContextEntityIdentifier(owner);
-
-			EntityKey entityKey = new EntityKey( id, ownerPersister, session.getEntityMode() );
-			
-			return session.getPersistenceContext()
-					.isPropertyNull( entityKey, getPropertyName() );
-			
+			final EntityPersister ownerPersister = session.getFactory().getEntityPersister( entityName );
+			final Serializable id = session.getContextEntityIdentifier( owner );
+			final EntityKey entityKey = session.generateEntityKey( id, ownerPersister );
+			return session.getPersistenceContext().isPropertyNull( entityKey, getPropertyName() );
 		}
 		else {
 			return false;
 		}
-
 	}
 
 	public int getColumnSpan(Mapping session) throws MappingException {
 		return 0;
 	}
 
 	public int[] sqlTypes(Mapping session) throws MappingException {
 		return ArrayHelper.EMPTY_INT_ARRAY;
 	}
 
 	private static final Size[] SIZES = new Size[0];
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return SIZES;
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return SIZES;
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session) {
 		//nothing to do
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) {
 		//nothing to do
 	}
 
 	public boolean isOneToOne() {
 		return true;
 	}
 
 	public boolean isDirty(Object old, Object current, SessionImplementor session) {
 		return false;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) {
 		return false;
 	}
 
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) {
 		return false;
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return foreignKeyType;
 	}
 
 	public Object hydrate(
 		ResultSet rs,
 		String[] names,
 		SessionImplementor session,
 		Object owner)
 	throws HibernateException, SQLException {
 
 		return session.getContextEntityIdentifier(owner);
 	}
 
 	protected boolean isNullable() {
 		return foreignKeyType==ForeignKeyDirection.FOREIGN_KEY_TO_PARENT;
 	}
 
 	public boolean useLHSPrimaryKey() {
 		return true;
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 		return null;
 	}
 
 	public Object assemble(Serializable oid, SessionImplementor session, Object owner)
 	throws HibernateException {
 		//this should be a call to resolve(), not resolveIdentifier(), 
 		//'cos it might be a property-ref, and we did not cache the
 		//referenced value
 		return resolve( session.getContextEntityIdentifier(owner), session, owner );
 	}
 	
 	/**
 	 * We don't need to dirty check one-to-one because of how 
 	 * assemble/disassemble is implemented and because a one-to-one 
 	 * association is never dirty
 	 */
 	public boolean isAlwaysDirtyChecked() {
 		//TODO: this is kinda inconsistent with CollectionType
 		return false; 
 	}
 	
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
index 8f868c9209..861d28230f 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
@@ -1,675 +1,674 @@
-//$Id: CustomPersister.java 11398 2007-04-10 14:54:07Z steve.ebersole@jboss.com $
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Hashtable;
 import java.util.Map;
 import org.hibernate.EntityMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.cache.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.entry.CacheEntryStructure;
 import org.hibernate.cache.entry.UnstructuredCacheEntry;
 import org.hibernate.engine.CascadeStyle;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.Mapping;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.TwoPhaseLoad;
 import org.hibernate.engine.ValueInclusion;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.PostLoadEvent;
 import org.hibernate.event.PreLoadEvent;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDHexGenerator;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.sql.QuerySelect;
 import org.hibernate.sql.Select;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 public class CustomPersister implements EntityPersister {
 
 	private static final Hashtable INSTANCES = new Hashtable();
 	private static final IdentifierGenerator GENERATOR = new UUIDHexGenerator();
 
 	private SessionFactoryImplementor factory;
 
 	public CustomPersister(
 			PersistentClass model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping mapping) {
 		this.factory = factory;
 	}
 
 	public boolean hasLazyProperties() {
 		return false;
 	}
 
 	private void checkEntityMode(EntityMode entityMode) {
 		if ( EntityMode.POJO != entityMode ) {
 			throw new IllegalArgumentException( "Unhandled EntityMode : " + entityMode );
 		}
 	}
 
 	private void checkEntityMode(SessionImplementor session) {
 		checkEntityMode( session.getEntityMode() );
 	}
 
 	public boolean isInherited() {
 		return false;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	public Class getMappedClass() {
 		return Custom.class;
 	}
 
 	public void postInstantiate() throws MappingException {}
 
 	public String getEntityName() {
 		return Custom.class.getName();
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return Custom.class.getName().equals(entityName);
 	}
 
 	public boolean hasProxy() {
 		return false;
 	}
 
 	public boolean hasCollections() {
 		return false;
 	}
 
 	public boolean hasCascades() {
 		return false;
 	}
 
 	public boolean isMutable() {
 		return true;
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return false;
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return false;
 	}
 
 	public Boolean isTransient(Object object, SessionImplementor session) {
 		return new Boolean( ( (Custom) object ).id==null );
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session)
 	throws HibernateException {
 		return getPropertyValues( object, session.getEntityMode() );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void retrieveGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Class getMappedClass(EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return Custom.class;
 	}
 
 	public boolean implementsLifecycle(EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return false;
 	}
 
 	public boolean implementsValidatable(EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return false;
 	}
 
 	public Class getConcreteProxyClass(EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return Custom.class;
 	}
 
 	public void setPropertyValues(Object object, Object[] values, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		setPropertyValue( object, 0, values[0], entityMode );
 	}
 
 	public void setPropertyValue(Object object, int i, Object value, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		( (Custom) object ).setName( (String) value );
 	}
 
 	public Object[] getPropertyValues(Object object, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		Custom c = (Custom) object;
 		return new Object[] { c.getName() };
 	}
 
 	public Object getPropertyValue(Object object, int i, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		return ( (Custom) object ).getName();
 	}
 
 	public Object getPropertyValue(Object object, String propertyName, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		return ( (Custom) object ).getName();
 	}
 
 	public Serializable getIdentifier(Object object, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		return ( (Custom) object ).id;
 	}
 
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		checkEntityMode( session );
 		return ( (Custom) entity ).id;
 	}
 
 	public void setIdentifier(Object object, Serializable id, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		( (Custom) object ).id = (String) id;
 	}
 
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		checkEntityMode( session );
 		( (Custom) entity ).id = (String) id;
 	}
 
 	public Object getVersion(Object object, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		return null;
 	}
 
 	public Object instantiate(Serializable id, EntityMode entityMode) throws HibernateException {
 		checkEntityMode( entityMode );
 		return instantiate( id );
 	}
 
 	private Object instantiate(Serializable id) {
 		Custom c = new Custom();
 		c.id = (String) id;
 		return c;
 	}
 
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		checkEntityMode( session );
 		return instantiate( id );
 	}
 
 	public boolean isInstance(Object object, EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return object instanceof Custom;
 	}
 
 	public boolean hasUninitializedLazyProperties(Object object, EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return false;
 	}
 
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		( ( Custom ) entity ).id = ( String ) currentId;
 	}
 
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		checkEntityMode( session );
 		( ( Custom ) entity ).id = ( String ) currentId;
 	}
 
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory, EntityMode entityMode) {
 		checkEntityMode( entityMode );
 		return this;
 	}
 
 	public int[] findDirty(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session
 	) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	public int[] findModified(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session
 	) throws HibernateException {
 		if ( !EqualsHelper.equals( x[0], y[0] ) ) {
 			return new int[] { 0 };
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * @see EntityPersister#hasIdentifierProperty()
 	 */
 	public boolean hasIdentifierProperty() {
 		return true;
 	}
 
 	/**
 	 * @see EntityPersister#isVersioned()
 	 */
 	public boolean isVersioned() {
 		return false;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionType()
 	 */
 	public VersionType getVersionType() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getVersionProperty()
 	 */
 	public int getVersionProperty() {
 		return 0;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierGenerator()
 	 */
 	public IdentifierGenerator getIdentifierGenerator()
 	throws HibernateException {
 		return GENERATOR;
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, org.hibernate.LockOptions , SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 		return load(id, optionalObject, lockOptions.getLockMode(), session);
 	}
 
 	/**
 	 * @see EntityPersister#load(Serializable, Object, LockMode, SessionImplementor)
 	 */
 	public Object load(
 		Serializable id,
 		Object optionalObject,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		// fails when optional object is supplied
 
 		Custom clone = null;
 		Custom obj = (Custom) INSTANCES.get(id);
 		if (obj!=null) {
 			clone = (Custom) obj.clone();
 			TwoPhaseLoad.addUninitializedEntity(
-					new EntityKey( id, this, session.getEntityMode() ),
+					session.generateEntityKey( id, this ),
 					clone,
 					this,
 					LockMode.NONE,
 					false,
 					session
 				);
 			TwoPhaseLoad.postHydrate(
 					this, id,
 					new String[] { obj.getName() },
 					null,
 					clone,
 					LockMode.NONE,
 					false,
 					session
 				);
 			TwoPhaseLoad.initializeEntity(
 					clone,
 					false,
 					session,
 					new PreLoadEvent( (EventSource) session ),
 					new PostLoadEvent( (EventSource) session )
 				);
 		}
 		return clone;
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockOptions lockOptions,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * @see EntityPersister#lock(Serializable, Object, Object, LockMode, SessionImplementor)
 	 */
 	public void lock(
 		Serializable id,
 		Object version,
 		Object object,
 		LockMode lockMode,
 		SessionImplementor session
 	) throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void insert(
 		Serializable id,
 		Object[] fields,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put(id, ( (Custom) object ).clone() );
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException {
 
 		throw new UnsupportedOperationException();
 	}
 
 	public void delete(
 		Serializable id,
 		Object version,
 		Object object,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.remove(id);
 	}
 
 	/**
 	 * @see EntityPersister
 	 */
 	public void update(
 		Serializable id,
 		Object[] fields,
 		int[] dirtyFields,
 		boolean hasDirtyCollection,
 		Object[] oldFields,
 		Object oldVersion,
 		Object object,
 		Object rowId,
 		SessionImplementor session
 	) throws HibernateException {
 
 		INSTANCES.put( id, ( (Custom) object ).clone() );
 
 	}
 
 	private static final Type[] TYPES = new Type[] { Hibernate.STRING };
 	private static final String[] NAMES = new String[] { "name" };
 	private static final boolean[] MUTABILITY = new boolean[] { true };
 	private static final boolean[] GENERATION = new boolean[] { false };
 
 	/**
 	 * @see EntityPersister#getPropertyTypes()
 	 */
 	public Type[] getPropertyTypes() {
 		return TYPES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyNames()
 	 */
 	public String[] getPropertyNames() {
 		return NAMES;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyCascadeStyles()
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return null;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierType()
 	 */
 	public Type getIdentifierType() {
 		return Hibernate.STRING;
 	}
 
 	/**
 	 * @see EntityPersister#getIdentifierPropertyName()
 	 */
 	public String getIdentifierPropertyName() {
 		return "id";
 	}
 
 	public boolean hasCache() {
 		return false;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return null;
 	}
 
 	public String getRootEntityName() {
 		return "CUSTOMS";
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return new String[] { "CUSTOMS" };
 	}
 
 	/**
 	 * @see EntityPersister#getClassMetadata()
 	 */
 	public ClassMetadata getClassMetadata() {
 		return null;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return MUTABILITY;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return MUTABILITY;
 	}
 
 	/**
 	 * @see EntityPersister#getPropertyInsertability()
 	 */
 	public boolean[] getPropertyInsertability() {
 		return MUTABILITY;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return new ValueInclusion[0];
 	}
 
 
 	public boolean canExtractIdOutOfEntity() {
 		return true;
 	}
 
 	public boolean isBatchLoadable() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object getPropertyValue(Object object, String propertyName)
 		throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session)
 		throws HibernateException {
 		throw new UnsupportedOperationException("no proxy for this class");
 	}
 
 	public Object getCurrentVersion(
 		Serializable id,
 		SessionImplementor session)
 		throws HibernateException {
 
 		return INSTANCES.get(id);
 	}
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 			throws HibernateException {
 		return null;
 	}
 
 	public EntityMode guessEntityMode(Object object) {
 		if ( !isInstance(object, EntityMode.POJO) ) {
 			return null;
 		}
 		else {
 			return EntityMode.POJO;
 		}
 	}
 
 	public boolean[] getPropertyNullability() {
 		return MUTABILITY;
 	}
 
 	public boolean isDynamic() {
 		return false;
 	}
 
 	public boolean isCacheInvalidationRequired() {
 		return false;
 	}
 
 	public void applyFilters(QuerySelect select, String alias, Map filters) {
 	}
 
 	public void applyFilters(Select select, String alias, Map filters) {
 	}
 
 
 	public void afterInitialize(Object entity, boolean fetched, SessionImplementor session) {
 	}
 
 	public void afterReassociate(Object entity, SessionImplementor session) {
 	}
 
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 	throws HibernateException {
 		return null;
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return MUTABILITY;
 	}
 
 	public CacheEntryStructure getCacheEntryStructure() {
 		return new UnstructuredCacheEntry();
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return false;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return null;
 	}
 
 	public Type[] getNaturalIdentifierTypes() {
 		return null;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return false;
 	}
 
 	public boolean hasMutableProperties() {
 		return false;
 	}
 
 	public boolean isInstrumented(EntityMode entityMode) {
 		return false;
 	}
 
 	public boolean hasInsertGeneratedProperties() {
 		return false;
 	}
 
 	public boolean hasUpdateGeneratedProperties() {
 		return false;
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return null;
 	}
 
 	public boolean isLazyPropertiesCacheable() {
 		return true;
 	}
 
 	public boolean hasGeneratedProperties() {
 		return false;
 	}
 
 	public boolean isVersionPropertyGenerated() {
 		return false;
 	}
 
 	public String[] getOrphanRemovalOneToOnePaths() {
 		return null;
 	}
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	public Comparator getVersionComparator() {
 		return null;
 	}
 
 	public EntityMetamodel getEntityMetamodel() {
 		return null;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
index 05b8e38da3..66b29edc3a 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/multitenancy/schema/SchemaBasedMultiTenancyTest.java
@@ -1,206 +1,203 @@
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
-import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
-import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
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
-import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public class SchemaBasedMultiTenancyTest extends BaseUnitTestCase {
 	private DriverManagerConnectionProviderImpl acmeProvider;
 	private DriverManagerConnectionProviderImpl jbossProvider;
 
 	private ServiceRegistryImplementor serviceRegistry;
 
 	private SessionFactory sessionFactory;
 
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
 		cfg.addAnnotatedClass( Customer.class );
 
 		cfg.buildMappings();
 
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
-		).execute( 		// so stupid...
-				false, 	// do not script the export (write it to file)
-				true, 	// do run it against the database
-				false, 	// do not *just* perform the drop
-				false	// do not *just* perform the create
+		).execute(		 // so stupid...
+						   false,	 // do not script the export (write it to file)
+						   true,	 // do run it against the database
+						   false,	 // do not *just* perform the drop
+						   false	// do not *just* perform the create
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
 
 		serviceRegistry = new BasicServiceRegistryImpl( cfg.getProperties() );
 		serviceRegistry.registerService( MultiTenantConnectionProvider.class, multiTenantConnectionProvider );
 
 		sessionFactory = cfg.buildSessionFactory( serviceRegistry );
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
 		Customer steve = new Customer( "steve" );
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
 
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
index 1cf35046a9..199b36d445 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/entities/mapper/relation/lazy/AbstractDelegateSessionImplementor.java
@@ -1,299 +1,309 @@
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
 package org.hibernate.envers.entities.mapper.relation.lazy;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
+
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Query;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.collection.PersistentCollection;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.LoadQueryInfluencers;
 import org.hibernate.engine.NonFlushedChanges;
 import org.hibernate.engine.PersistenceContext;
 import org.hibernate.engine.QueryParameters;
 import org.hibernate.engine.SessionFactoryImplementor;
 import org.hibernate.engine.SessionImplementor;
-import org.hibernate.engine.jdbc.LobCreationContext;
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
+	public String getTenantIdentifier() {
+		return delegate.getTenantIdentifier();
+	}
+
+	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		return delegate.getJdbcConnectionAccess();
 	}
 
 	@Override
+	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
+		return delegate.generateEntityKey( id, persister );
+	}
+
+	@Override
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
 
     public EventListeners getListeners() {
         return delegate.getListeners();
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
