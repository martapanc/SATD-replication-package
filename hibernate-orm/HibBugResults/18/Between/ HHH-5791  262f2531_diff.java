diff --git a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
index 63767ac45c..7b64492889 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
@@ -1,661 +1,661 @@
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
 import java.util.Map;
 import java.util.Set;
 import java.util.HashSet;
 
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.WrongClassException;
 import org.hibernate.PropertyValueException;
 import org.hibernate.engine.Cascade;
 import org.hibernate.engine.CascadingAction;
 import org.hibernate.engine.EntityEntry;
 import org.hibernate.engine.EntityKey;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.engine.Status;
 import org.hibernate.event.EventSource;
 import org.hibernate.event.MergeEvent;
 import org.hibernate.event.MergeEventListener;
 import org.hibernate.intercept.FieldInterceptionHelper;
 import org.hibernate.intercept.FieldInterceptor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
  * Defines the default copy event listener used by hibernate for copying entities
  * in response to generated copy events.
  *
  * @author Gavin King
  */
 public class DefaultMergeEventListener extends AbstractSaveEventListener 
 	implements MergeEventListener {
 
 	private static final Logger log = LoggerFactory.getLogger(DefaultMergeEventListener.class);
 	
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
 					log.trace( "transient instance could not be processed by merge when checking nullability: " +
 							transientEntityName + "[" + transientEntity + "]" );
 				}
 				if ( isNullabilityCheckedGlobal( event.getSession() ) ) {
 					throw new TransientObjectException(
 						"one or more objects is an unsaved transient instance - save transient instance(s) before merging: " +
 						transientEntityNames );
 				}
 				else {
 					log.trace( "retry saving transient instances without checking nullability" );
 					// failures will be detected later...
 					retryMergeTransientEntities( event, transientCopyCache, copyCache, false );
 				}
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
 				log.trace( "transient instance could not be processed by merge: " +
 						event.getSession().guessEntityName( copy ) + "[" + entity + "]" );
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
 					log.trace("ignoring uninitialized proxy");
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
 				log.trace("already in merge process");
 				event.setResult( entity );				
 			}
 			else {
 				if ( copyCache.containsKey( entity ) ) {
 					log.trace("already in copyCache; setting in merge process");					
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
 						EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
 						Object managedEntity = source.getPersistenceContext().getEntity( key );
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
 		log.trace("ignoring persistent instance");
 		
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
 		
 		log.trace("merging transient instance");
 		
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
 
 		log.trace("merging transient instance");
 
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
-			if ( propertyFromCopy == null || ! propertyType.isEntityType() ) {
-				log.trace( "property '" + copyEntry.getEntityName() + "." + propertyName +
-						"' is null or not an entity; " + propertyName + " =["+propertyFromCopy+"]");
+			if ( propertyFromCopy == null ||
+					propertyFromEntity == null ||
+					! propertyType.isEntityType() ||
+					! copyCache.containsKey( propertyFromEntity ) ) {
+				if ( log.isTraceEnabled() ) {
+					String fullPropertyName = "property '" + copyEntry.getEntityName() + "." + propertyName;
+					log.trace( fullPropertyName + " in copy is " + ( propertyFromCopy == null ? "null" : propertyFromCopy ) );
+					log.trace( fullPropertyName + " in original is " + ( propertyFromCopy == null ? "null" : propertyFromCopy ) );
+					log.trace( fullPropertyName + ( propertyType.isEntityType() ? " is" : " is not" ) + " an entity type" );
+					if ( propertyFromEntity != null && ! copyCache.containsKey( propertyFromEntity ) ) {
+						log.trace( fullPropertyName + " is not in copy cache" );
+					}
+				}
 				if ( isNullabilityCheckedGlobal( source ) ) {
 					throw ex;
 				}
 				else {
 					// retry save w/o checking for non-nullable properties
 					// (the failure will be detected later)
 					saveTransientEntity( copy, entityName, requestedId, source, copyCache, false );
 				}
 			}
-			if ( ! copyCache.containsKey( propertyFromEntity ) ) {
-				log.trace( "property '" + copyEntry.getEntityName() + "." + propertyName +
-						"' from original entity is not in copyCache; " + propertyName + " =["+propertyFromEntity+"]");
-				if ( isNullabilityCheckedGlobal( source ) ) {
-					throw ex;
+			if ( log.isTraceEnabled() && propertyFromEntity != null ) {
+				if ( ( ( EventCache ) copyCache ).isOperatedOn( propertyFromEntity ) ) {
+					log.trace( "property '" + copyEntry.getEntityName() + "." + propertyName +
+							"' from original entity is in copyCache and is in the process of being merged; " +
+							propertyName + " =["+propertyFromEntity+"]");
 				}
 				else {
-					// retry save w/o checking non-nullable properties
-					// (the failure will be detected later)
-					saveTransientEntity( copy, entityName, requestedId, source, copyCache, false );					
+					log.trace( "property '" + copyEntry.getEntityName() + "." + propertyName +
+							"' from original entity is in copyCache and is not in the process of being merged; " +
+							propertyName + " =["+propertyFromEntity+"]");
 				}
 			}
-			if ( ( ( EventCache ) copyCache ).isOperatedOn( propertyFromEntity ) ) {
-				log.trace( "property '" + copyEntry.getEntityName() + "." + propertyName +
-						"' from original entity is in copyCache and is in the process of being merged; " +
-						propertyName + " =["+propertyFromEntity+"]");
-			}
-			else {
-				log.trace( "property '" + copyEntry.getEntityName() + "." + propertyName +
-						"' from original entity is in copyCache and is not in the process of being merged; " +
-						propertyName + " =["+propertyFromEntity+"]");
-			}
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
 		
 		log.trace("merging detached instance");
 		
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
 				EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
 				Object managedEntity = source.getPersistenceContext().getEntity( key );
 				entry = source.getPersistenceContext().getEntry( managedEntity );
 			}
 		}
 
 		if ( entry == null ) {
 			// perhaps this should be an exception since it is only ever used
 			// in the above method?
 			return false;
 		}
 		else {
 			return entry.isExistsInDatabase();
 		}
 	}
 
 	protected void copyValues(
 		final EntityPersister persister, 
 		final Object entity, 
 		final Object target, 
 		final SessionImplementor source,
 		final Map copyCache
 	) {
 		final Object[] copiedValues = TypeHelper.replace(
 				persister.getPropertyValues( entity, source.getEntityMode() ),
 				persister.getPropertyValues( target, source.getEntityMode() ),
 				persister.getPropertyTypes(),
 				source,
 				target, 
 				copyCache
 			);
 
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
 
 
 	protected CascadingAction getCascadeAction() {
 		return CascadingAction.MERGE;
 	}
 
 	protected Boolean getAssumedUnsaved() {
 		return Boolean.FALSE;
 	}
 	
 	/**
 	 * Cascade behavior is redefined by this subclass, disable superclass behavior
 	 */
 	protected void cascadeAfterSave(EventSource source, EntityPersister persister, Object entity, Object anything) 
 	throws HibernateException {
 	}
 
 	/**
 	 * Cascade behavior is redefined by this subclass, disable superclass behavior
 	 */
 	protected void cascadeBeforeSave(EventSource source, EntityPersister persister, Object entity, Object anything) 
 	throws HibernateException {
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeCheckNullFalseDelayedInsertTest.java b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeCheckNullFalseDelayedInsertTest.java
new file mode 100644
index 0000000000..b9353399bf
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeCheckNullFalseDelayedInsertTest.java
@@ -0,0 +1,52 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Middleware LLC or third-party contributors as
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
+package org.hibernate.test.cascade.circle;
+
+import junit.framework.Test;
+
+import org.hibernate.TransientObjectException;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.cfg.Environment;
+import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
+
+/**
+ * @author Gail Badner
+ */
+public class MultiPathCircleCascadeCheckNullFalseDelayedInsertTest extends MultiPathCircleCascadeDelayedInsertTest {
+
+	public MultiPathCircleCascadeCheckNullFalseDelayedInsertTest(String str) {
+		super( str );
+	}
+
+	@Override
+	 public void configure(Configuration cfg) {
+		super.configure( cfg );
+		cfg.setProperty( Environment.CHECK_NULLABILITY, "false" );
+	}
+
+	public static Test suite() {
+		return new FunctionalTestClassTestSuite( MultiPathCircleCascadeCheckNullFalseDelayedInsertTest.class );
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeCheckNullTrueDelayedInsertTest.java b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeCheckNullTrueDelayedInsertTest.java
new file mode 100644
index 0000000000..750b7c6ccb
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeCheckNullTrueDelayedInsertTest.java
@@ -0,0 +1,51 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Middleware LLC or third-party contributors as
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
+package org.hibernate.test.cascade.circle;
+
+import junit.framework.Test;
+
+import org.hibernate.cfg.Configuration;
+import org.hibernate.cfg.Environment;
+import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
+
+/**
+ * @author Gail Badner
+ */
+public class MultiPathCircleCascadeCheckNullTrueDelayedInsertTest extends MultiPathCircleCascadeDelayedInsertTest {
+
+	public MultiPathCircleCascadeCheckNullTrueDelayedInsertTest(String str) {
+		super( str );
+	}
+
+	@Override
+	 public void configure(Configuration cfg) {
+		super.configure( cfg );
+		cfg.setProperty( Environment.CHECK_NULLABILITY, "true" );
+	}
+
+	public static Test suite() {
+		return new FunctionalTestClassTestSuite( MultiPathCircleCascadeCheckNullTrueDelayedInsertTest.class );
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeDelayedInsert.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeDelayedInsert.hbm.xml
new file mode 100644
index 0000000000..985fd92891
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeDelayedInsert.hbm.xml
@@ -0,0 +1,82 @@
+<?xml version="1.0"?>
+<!DOCTYPE hibernate-mapping SYSTEM "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd" >
+
+<hibernate-mapping package="org.hibernate.test.cascade.circle">
+
+    <class name="Route" table="HB_Route">
+
+        <id name="routeID" type="long"><generator class="increment"/></id>
+
+        <property name="name" type="string" not-null="true"/>
+
+        <set name="nodes" inverse="true" cascade="persist,merge,refresh">
+            <key column="routeID"/>
+            <one-to-many class="Node"/>
+        </set>
+    </class>
+
+   <class name="Tour" table="HB_Tour">
+
+        <id name="tourID" type="long"><generator class="increment"/></id>
+
+        <property name="name" type="string" not-null="true"/>
+
+        <set name="nodes" inverse="true" lazy="true" cascade="merge,refresh">
+            <key column="tourID"/>
+            <one-to-many class="Node"/>
+        </set>
+    </class>
+    
+    <class name="Transport" table="HB_Transport">
+
+        <id name="transportID" type="long"><generator class="increment"/></id>
+
+        <property name="name" type="string" not-null="true"/>
+
+        <many-to-one name="pickupNode"
+            column="pickupNodeID"
+            unique="true"
+            not-null="true"
+            cascade="merge,refresh"
+            lazy="false"/> 
+
+        <many-to-one name="deliveryNode"
+            column="deliveryNodeID"
+            unique="true"
+            not-null="true"
+            cascade="merge,refresh"
+            lazy="false"/> 
+    </class>
+
+    <class name="Node" table="HB_Node">
+
+        <id name="nodeID" type="long"><generator class="increment"/></id>
+
+        <property name="name" type="string" not-null="true"/>
+
+         <set name="deliveryTransports" inverse="true" lazy="true" cascade="merge,refresh">
+            <key column="deliveryNodeID"/>
+            <one-to-many class="Transport"/>
+        </set>
+
+        <set name="pickupTransports" inverse="true" lazy="true" cascade="merge,refresh">
+            <key column="pickupNodeID"/>
+            <one-to-many class="Transport"/>
+        </set>
+
+        <many-to-one name="route"
+            column="routeID"
+            unique="false"
+            not-null="true"
+            cascade="none"
+            lazy="false"/> 
+
+        <many-to-one name="tour"
+            column="tourID"
+            unique="false"
+            not-null="false"
+            cascade="merge,refresh"
+            lazy="false"/>                 
+    </class>
+
+</hibernate-mapping>
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeDelayedInsertTest.java b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeDelayedInsertTest.java
new file mode 100644
index 0000000000..d6531a25c0
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeDelayedInsertTest.java
@@ -0,0 +1,65 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2010, Red Hat Middleware LLC or third-party contributors as
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
+package org.hibernate.test.cascade.circle;
+
+import junit.framework.Test;
+
+import org.hibernate.JDBCException;
+import org.hibernate.PropertyValueException;
+import org.hibernate.TransientObjectException;
+import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
+
+/**
+ * @author Gail Badner
+ */
+public class MultiPathCircleCascadeDelayedInsertTest extends MultiPathCircleCascadeTest {
+	public MultiPathCircleCascadeDelayedInsertTest(String string) {
+		super(string);
+	}
+
+	public String[] getMappings() {
+		return new String[] {
+				"cascade/circle/MultiPathCircleCascadeDelayedInsert.hbm.xml"
+		};
+	}
+
+	public static Test suite() {
+		return new FunctionalTestClassTestSuite( MultiPathCircleCascadeDelayedInsertTest.class );
+	}
+
+	protected void checkExceptionFromNullValueForNonNullable(Exception ex, boolean checkNullability, boolean isNullValue ) {
+		if ( checkNullability ) {
+			if ( isNullValue ) {
+				assertTrue( ex instanceof PropertyValueException );
+			}
+			else {
+				assertTrue( ex instanceof TransientObjectException );
+			}
+		}
+		else {
+			assertTrue( ex instanceof JDBCException || ex instanceof TransientObjectException );
+		}
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeTest.java b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeTest.java
index c4aabe0285..4ffbe5e740 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cascade/circle/MultiPathCircleCascadeTest.java
@@ -1,552 +1,566 @@
 //$Id: $
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
 
 package org.hibernate.test.cascade.circle;
 
 import java.util.Iterator;
 
 import junit.framework.Test;
 
 import org.hibernate.JDBCException;
 import org.hibernate.PropertyValueException;
 import org.hibernate.Session;
 import org.hibernate.TransientObjectException;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.SessionImplementor;
 import org.hibernate.testing.junit.functional.FunctionalTestCase;
 import org.hibernate.testing.junit.functional.FunctionalTestClassTestSuite;
 
 /**
  * The test case uses the following model:
  *
  *                          <-    ->
  *                      -- (N : 0,1) -- Tour
  *                      |    <-   ->
  *                      | -- (1 : N) -- (pickup) ----
  *               ->     | |                          |
  * Route -- (1 : N) -- Node                      Transport
  *                      |  <-   ->                |
  *                      -- (1 : N) -- (delivery) --
  *
  *  Arrows indicate the direction of cascade-merge.
  *
  * It reproduced the following issues:
  *    http://opensource.atlassian.com/projects/hibernate/browse/HHH-3046
  *    http://opensource.atlassian.com/projects/hibernate/browse/HHH-3810
  *
  * This tests that merge is cascaded properly from each entity.
  * 
  * @author Pavol Zibrita, Gail Badner
  */
 public class MultiPathCircleCascadeTest extends FunctionalTestCase {
 
 	public MultiPathCircleCascadeTest(String string) {
 		super(string);
 	}
 
 	public void configure(Configuration cfg) {
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true");
 		cfg.setProperty( Environment.STATEMENT_BATCH_SIZE, "0" );
 	}
 
 	public String[] getMappings() {
 		return new String[] {
 				"cascade/circle/MultiPathCircleCascade.hbm.xml"
 		};
 	}
 
 	public static Test suite() {
 		return new FunctionalTestClassTestSuite( MultiPathCircleCascadeTest.class );
 	}
 	
 	protected void cleanupTest() {
 		Session s = openSession();
 		s.beginTransaction();
 		s.createQuery( "delete from Transport" );
 		s.createQuery( "delete from Tour" );
 		s.createQuery( "delete from Node" );
 		s.createQuery( "delete from Route" );
 	}
 	
 	public void testMergeEntityWithNonNullableTransientEntity()
 	{
 		Route route = getUpdatedDetachedEntity();
 
 		Node node = ( Node ) route.getNodes().iterator().next();
 		route.getNodes().remove( node );
 
 		Route routeNew = new Route();
 		routeNew.setName( "new route" );
 		routeNew.getNodes().add( node );
 		node.setRoute( routeNew );
 
 		Session s = openSession();
 		s.beginTransaction();
 
 		try {
 			s.merge( node );
+			s.getTransaction().commit();
 			fail( "should have thrown an exception" );
 		}
 		catch ( Exception ex ) {
-			if ( ( ( SessionImplementor ) s ).getFactory().getSettings().isCheckNullability() ) {
-				assertTrue( ex instanceof TransientObjectException );
-			}
-			else {
-				assertTrue( ex instanceof JDBCException );
-			}
+			checkExceptionFromNullValueForNonNullable(
+					ex,
+					( ( SessionImplementor ) s ).getFactory().getSettings().isCheckNullability(),
+					false
+			);
 		}
 		finally {
 			s.getTransaction().rollback();
 			s.close();
 		}
 	}
 
 	public void testMergeEntityWithNonNullableEntityNull()
 	{
 		Route route = getUpdatedDetachedEntity();
 
 		Node node = ( Node ) route.getNodes().iterator().next();
 		route.getNodes().remove( node );
 		node.setRoute( null );
 
 		Session s = openSession();
 		s.beginTransaction();
 
 		try {
 			s.merge( node );
+			s.getTransaction().commit();
 			fail( "should have thrown an exception" );
 		}
 		catch ( Exception ex ) {
-			if ( ( ( SessionImplementor ) s ).getFactory().getSettings().isCheckNullability() ) {
-				assertTrue( ex instanceof PropertyValueException );
-			}
-			else {
-				assertTrue( ex instanceof JDBCException );
-			}
+			checkExceptionFromNullValueForNonNullable(
+					ex,
+					( ( SessionImplementor ) s ).getFactory().getSettings().isCheckNullability(),
+					true
+			);
 		}
 		finally {
 			s.getTransaction().rollback();
 			s.close();
 		}
 	}
 
 	public void testMergeEntityWithNonNullablePropSetToNull()
 	{
 		Route route = getUpdatedDetachedEntity();
 		Node node = ( Node ) route.getNodes().iterator().next();
 		node.setName( null );
 
 		Session s = openSession();
 		s.beginTransaction();
 
 		try {
 			s.merge( route );
+			s.getTransaction().commit();
 			fail( "should have thrown an exception" );
 		}
 		catch ( Exception ex ) {
-			if ( ( ( SessionImplementor ) s ).getFactory().getSettings().isCheckNullability() ) {
-				assertTrue( ex instanceof PropertyValueException );
-			}
-			else {
-				assertTrue( ex instanceof JDBCException );
-			}
+			checkExceptionFromNullValueForNonNullable(
+					ex,
+					( ( SessionImplementor ) s ).getFactory().getSettings().isCheckNullability(),
+					true
+			);
 		}
 		finally {
 			s.getTransaction().rollback();
 			s.close();
 		}
 	}
 
 	public void testMergeRoute()
 	{
 
 		Route route = getUpdatedDetachedEntity();
 
 		clearCounts();
 
 		Session s = openSession();
 		s.beginTransaction();
 
 		s.merge(route);
 
 		s.getTransaction().commit();
 		s.close();
 
 		assertInsertCount( 4 );
 		assertUpdateCount( 1 );
 
 		s = openSession();
 		s.beginTransaction();
 		route = ( Route ) s.get( Route.class, route.getRouteID() );
 		checkResults( route, true );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testMergePickupNode()
 	{
 
 		Route route = getUpdatedDetachedEntity();
 
 		clearCounts();
 
 		Session s = openSession();
 		s.beginTransaction();
 
 		Iterator it=route.getNodes().iterator();
 		Node node = ( Node ) it.next();
 		Node pickupNode;
 		if ( node.getName().equals( "pickupNodeB") ) {
 			pickupNode = node;
 		}
 		else {
 			node = ( Node ) it.next();
 			assertEquals( "pickupNodeB", node.getName() );
 			pickupNode = node;
 		}
 
 		pickupNode = ( Node ) s.merge( pickupNode );
 
 		s.getTransaction().commit();
 		s.close();
 
 		assertInsertCount( 4 );
 		assertUpdateCount( 0 );
 
 		s = openSession();
 		s.beginTransaction();
 		route = ( Route ) s.get( Route.class, route.getRouteID() );
 		checkResults( route, false );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testMergeDeliveryNode()
 	{
 
 		Route route = getUpdatedDetachedEntity();
 
 		clearCounts();
 
 		Session s = openSession();
 		s.beginTransaction();
 
 		Iterator it=route.getNodes().iterator();
 		Node node = ( Node ) it.next();
 		Node deliveryNode;
 		if ( node.getName().equals( "deliveryNodeB") ) {
 			deliveryNode = node;
 		}
 		else {
 			node = ( Node ) it.next();
 			assertEquals( "deliveryNodeB", node.getName() );
 			deliveryNode = node;
 		}
 
 		deliveryNode = ( Node ) s.merge( deliveryNode );
 
 		s.getTransaction().commit();
 		s.close();
 
 		assertInsertCount( 4 );
 		assertUpdateCount( 0 );
 
 		s = openSession();
 		s.beginTransaction();
 		route = ( Route ) s.get( Route.class, route.getRouteID() );
 		checkResults( route, false );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testMergeTour()
 	{
 
 		Route route = getUpdatedDetachedEntity();
 
 		clearCounts();
 
 		Session s = openSession();
 		s.beginTransaction();
 
 		Tour tour = ( Tour ) s.merge( ( ( Node ) route.getNodes().toArray()[0]).getTour() );
 
 		s.getTransaction().commit();
 		s.close();
 
 		assertInsertCount( 4 );
 		assertUpdateCount( 0 );
 
 		s = openSession();
 		s.beginTransaction();
 		route = ( Route ) s.get( Route.class, route.getRouteID() );
 		checkResults( route, false );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void testMergeTransport()
 	{
 
 		Route route = getUpdatedDetachedEntity();
 
 		clearCounts();
 
 		Session s = openSession();
 		s.beginTransaction();
 
 		Node node = ( ( Node ) route.getNodes().toArray()[0]);
 		Transport transport;
 		if ( node.getPickupTransports().size() == 1 ) {
 			transport = ( Transport ) node.getPickupTransports().toArray()[0];
 		}
 		else {
 			transport = ( Transport ) node.getDeliveryTransports().toArray()[0];
 		}
 
 		transport = ( Transport ) s.merge( transport  );
 
 		s.getTransaction().commit();
 		s.close();
 
 		assertInsertCount( 4 );
 		assertUpdateCount( 0 );
 
 		s = openSession();
 		s.beginTransaction();
 		route = ( Route ) s.get( Route.class, route.getRouteID() );
 		checkResults( route, false );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	private Route getUpdatedDetachedEntity() {
 
 		Session s = openSession();
 		s.beginTransaction();
 
 		Route route = new Route();
 		route.setName("routeA");
 
 		s.save( route );
 		s.getTransaction().commit();
 		s.close();
 
 		route.setName( "new routeA" );
 		route.setTransientField(new String("sfnaouisrbn"));
 
 		Tour tour = new Tour();
 		tour.setName("tourB");
 
 		Transport transport = new Transport();
 		transport.setName("transportB");
 
 		Node pickupNode = new Node();
 		pickupNode.setName("pickupNodeB");
 
 		Node deliveryNode = new Node();
 		deliveryNode.setName("deliveryNodeB");
 
 		pickupNode.setRoute(route);
 		pickupNode.setTour(tour);
 		pickupNode.getPickupTransports().add(transport);
 		pickupNode.setTransientField("pickup node aaaaaaaaaaa");
 
 		deliveryNode.setRoute(route);
 		deliveryNode.setTour(tour);
 		deliveryNode.getDeliveryTransports().add(transport);
 		deliveryNode.setTransientField("delivery node aaaaaaaaa");
 
 		tour.getNodes().add(pickupNode);
 		tour.getNodes().add(deliveryNode);
 
 		route.getNodes().add(pickupNode);
 		route.getNodes().add(deliveryNode);
 
 		transport.setPickupNode(pickupNode);
 		transport.setDeliveryNode(deliveryNode);
 		transport.setTransientField("aaaaaaaaaaaaaa");
 
 		return route;
 	}
 
 	private void checkResults(Route route, boolean isRouteUpdated) {
 		// since merge is not cascaded to route, this method needs to
 		// know whether route is expected to be updated
 		if ( isRouteUpdated ) {
 			assertEquals( "new routeA", route.getName() );
 		}
 		assertEquals( 2, route.getNodes().size() );
 		Node deliveryNode = null;
 		Node pickupNode = null;
 		for( Iterator it=route.getNodes().iterator(); it.hasNext(); ) {
 			Node node = ( Node ) it.next();
 			if( "deliveryNodeB".equals( node.getName(  )  ) ) {
 				deliveryNode = node;
 			}
 			else if( "pickupNodeB".equals( node.getName() ) ) {
 				pickupNode = node;
 			}
 			else {
 				fail( "unknown node");
 			}
 		}
 		assertNotNull( deliveryNode );
 		assertSame( route, deliveryNode.getRoute() );
 		assertEquals( 1, deliveryNode.getDeliveryTransports().size() );
 		assertEquals( 0, deliveryNode.getPickupTransports().size() );
 		assertNotNull( deliveryNode.getTour() );
 		assertEquals( "node original value", deliveryNode.getTransientField() );
 
 		assertNotNull( pickupNode );
 		assertSame( route, pickupNode.getRoute() );
 		assertEquals( 0, pickupNode.getDeliveryTransports().size() );
 		assertEquals( 1, pickupNode.getPickupTransports().size() );
 		assertNotNull( pickupNode.getTour() );
 		assertEquals( "node original value", pickupNode.getTransientField() );
 
 		assertTrue( ! deliveryNode.getNodeID().equals( pickupNode.getNodeID() ) );
 		assertSame( deliveryNode.getTour(), pickupNode.getTour() );
 		assertSame( deliveryNode.getDeliveryTransports().iterator().next(),
 				pickupNode.getPickupTransports().iterator().next() );
 
 		Tour tour = deliveryNode.getTour();
 		Transport transport = ( Transport ) deliveryNode.getDeliveryTransports().iterator().next();
 
 		assertEquals( "tourB", tour.getName() );
 		assertEquals( 2, tour.getNodes().size() );
 		assertTrue( tour.getNodes().contains( deliveryNode ) );
 		assertTrue( tour.getNodes().contains( pickupNode ) );
 
 		assertEquals( "transportB", transport.getName() );
 		assertSame( deliveryNode, transport.getDeliveryNode() );
 		assertSame( pickupNode, transport.getPickupNode() );
 		assertEquals( "transport original value", transport.getTransientField() );
 	}
 
 	public void testMergeData3Nodes()
 	{
 
 		Session s = openSession();
 		s.beginTransaction();
 
 		Route route = new Route();
 		route.setName("routeA");
 
 		s.save( route );
 		s.getTransaction().commit();
 		s.close();
 
 		clearCounts();
 
 		s = openSession();
 		s.beginTransaction();
 
 		route = (Route) s.get(Route.class, new Long(1));
 		//System.out.println(route);
 		route.setName( "new routA" );
 
 		route.setTransientField(new String("sfnaouisrbn"));
 
 		Tour tour = new Tour();
 		tour.setName("tourB");
 
 		Transport transport1 = new Transport();
 		transport1.setName("TRANSPORT1");
 
 		Transport transport2 = new Transport();
 		transport2.setName("TRANSPORT2");
 
 		Node node1 = new Node();
 		node1.setName("NODE1");
 
 		Node node2 = new Node();
 		node2.setName("NODE2");
 
 		Node node3 = new Node();
 		node3.setName("NODE3");
 
 		node1.setRoute(route);
 		node1.setTour(tour);
 		node1.getPickupTransports().add(transport1);
 		node1.setTransientField("node 1");
 
 		node2.setRoute(route);
 		node2.setTour(tour);
 		node2.getDeliveryTransports().add(transport1);
 		node2.getPickupTransports().add(transport2);
 		node2.setTransientField("node 2");
 
 		node3.setRoute(route);
 		node3.setTour(tour);
 		node3.getDeliveryTransports().add(transport2);
 		node3.setTransientField("node 3");
 
 		tour.getNodes().add(node1);
 		tour.getNodes().add(node2);
 		tour.getNodes().add(node3);
 
 		route.getNodes().add(node1);
 		route.getNodes().add(node2);
 		route.getNodes().add(node3);
 
 		transport1.setPickupNode(node1);
 		transport1.setDeliveryNode(node2);
 		transport1.setTransientField("aaaaaaaaaaaaaa");
 
 		transport2.setPickupNode(node2);
 		transport2.setDeliveryNode(node3);
 		transport2.setTransientField("bbbbbbbbbbbbb");
 
 		Route mergedRoute = (Route) s.merge(route);
 
 		s.getTransaction().commit();
 		s.close();
 
 		assertInsertCount( 6 );
 		assertUpdateCount( 1 );
 	}
 
+	protected void checkExceptionFromNullValueForNonNullable(Exception ex, boolean checkNullability, boolean isNullValue ) {
+		if ( checkNullability ) {
+			if ( isNullValue ) {
+				assertTrue( ex instanceof PropertyValueException );
+			}
+			else {
+				assertTrue( ex instanceof TransientObjectException );
+			}
+		}
+		else {
+			assertTrue( ex instanceof JDBCException );
+		}
+	}
+
 	protected void clearCounts() {
 		getSessions().getStatistics().clear();
 	}
 
 	protected void assertInsertCount(int expected) {
 		int inserts = ( int ) getSessions().getStatistics().getEntityInsertCount();
 		assertEquals( "unexpected insert count", expected, inserts );
 	}
 
 	protected void assertUpdateCount(int expected) {
 		int updates = ( int ) getSessions().getStatistics().getEntityUpdateCount();
 		assertEquals( "unexpected update counts", expected, updates );
 	}
 
 	protected void assertDeleteCount(int expected) {
 		int deletes = ( int ) getSessions().getStatistics().getEntityDeleteCount();
 		assertEquals( "unexpected delete counts", expected, deletes );
 	}	
 }
