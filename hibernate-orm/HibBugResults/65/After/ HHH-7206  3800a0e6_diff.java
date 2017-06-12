diff --git a/hibernate-core/src/main/java/org/hibernate/NaturalIdLoadAccess.java b/hibernate-core/src/main/java/org/hibernate/NaturalIdLoadAccess.java
index a4ff80ff53..89effd90b6 100644
--- a/hibernate-core/src/main/java/org/hibernate/NaturalIdLoadAccess.java
+++ b/hibernate-core/src/main/java/org/hibernate/NaturalIdLoadAccess.java
@@ -1,75 +1,93 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 
 /**
  * Loads an entity by its natural identifier
  * 
  * @author Eric Dalquist
  * @author Steve Ebersole
  *
  * @see org.hibernate.annotations.NaturalId
  */
 public interface NaturalIdLoadAccess {
 	/**
 	 * Specify the {@link LockOptions} to use when retrieving the entity.
 	 *
 	 * @param lockOptions The lock options to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public NaturalIdLoadAccess with(LockOptions lockOptions);
 
 	/**
 	 * Add a NaturalId attribute value.
 	 * 
 	 * @param attributeName The entity attribute name that is marked as a NaturalId
 	 * @param value The value of the attribute
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public NaturalIdLoadAccess using(String attributeName, Object value);
 
 	/**
+	 * For entities with mutable natural ids, should Hibernate perform "synchronization" prior to performing
+	 * lookups?  The default is to perform "synchronization" (for correctness).
+	 * <p/>
+	 * "synchronization" here indicates updating the natural-id -> pk cross reference maintained as part of the
+	 * session.  When enabled, prior to performing the lookup, Hibernate will check all entities of the given
+	 * type associated with the session to see if its natural-id values have changed and, if so, update the
+	 * cross reference.  There is a performance impact associated with this, so if application developers are
+	 * certain the natural-ids in play have not changed, this setting can be disabled to circumvent that impact.
+	 * However, disabling this setting when natural-ids values have changed can result in incorrect results!
+	 *
+	 * @param enabled Should synchronization be performed?  {@code true} indicates synchronization will be performed;
+	 * {@code false} indicates it will be circumvented.
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	public NaturalIdLoadAccess setSynchronizationEnabled(boolean enabled);
+
+	/**
 	 * Return the persistent instance with the natural id value(s) defined by the call(s) to {@link #using}.  This
 	 * method might return a proxied instance that is initialized on-demand, when a non-identifier method is accessed.
 	 *
 	 * You should not use this method to determine if an instance exists; to check for existence, use {@link #load}
 	 * instead.  Use this only to retrieve an instance that you assume exists, where non-existence would be an
 	 * actual error.
 	 *
 	 * @return the persistent instance or proxy
 	 */
 	public Object getReference();
 
 	/**
 	 * Return the persistent instance with the natural id value(s) defined by the call(s) to {@link #using}, or
 	 * {@code null} if there is no such persistent instance.  If the instance is already associated with the session,
 	 * return that instance, initializing it if needed.  This method never returns an uninitialized instance.
 	 *
 	 * @return The persistent instance or {@code null} 
 	 */
 	public Object load();
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/SimpleNaturalIdLoadAccess.java b/hibernate-core/src/main/java/org/hibernate/SimpleNaturalIdLoadAccess.java
index 64d9602c3a..0d4da07705 100644
--- a/hibernate-core/src/main/java/org/hibernate/SimpleNaturalIdLoadAccess.java
+++ b/hibernate-core/src/main/java/org/hibernate/SimpleNaturalIdLoadAccess.java
@@ -1,70 +1,83 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 
 /**
  * Loads an entity by its natural identifier
  * 
  * @author Eric Dalquist
  * @author Steve Ebersole
  *
  * @see org.hibernate.annotations.NaturalId
  * @see NaturalIdLoadAccess
  */
 public interface SimpleNaturalIdLoadAccess {
 	/**
 	 * Specify the {@link org.hibernate.LockOptions} to use when retrieving the entity.
 	 *
 	 * @param lockOptions The lock options to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SimpleNaturalIdLoadAccess with(LockOptions lockOptions);
 
 	/**
+	 * For entities with mutable natural ids, should Hibernate perform "synchronization" prior to performing 
+	 * lookups?  The default is to perform "synchronization" (for correctness).
+	 * <p/>
+	 * See {@link NaturalIdLoadAccess#setSynchronizationEnabled} for detailed discussion.
+	 *
+	 * @param enabled Should synchronization be performed?  {@code true} indicates synchronization will be performed;
+	 * {@code false} indicates it will be circumvented.
+	 *
+	 * @return {@code this}, for method chaining
+	 */
+	public SimpleNaturalIdLoadAccess setSynchronizationEnabled(boolean enabled);
+
+	/**
 	 * Return the persistent instance with the given natural id value, assuming that the instance exists. This method
 	 * might return a proxied instance that is initialized on-demand, when a non-identifier method is accessed.
 	 *
 	 * You should not use this method to determine if an instance exists; to check for existence, use {@link #load}
 	 * instead.  Use this only to retrieve an instance that you assume exists, where non-existence would be an
 	 * actual error.
 	 *
 	 * @param naturalIdValue The value of the natural-id for the entity to retrieve
 	 *
 	 * @return the persistent instance or proxy
 	 */
 	public Object getReference(Object naturalIdValue);
 
 	/**
 	 * Return the persistent instance with the given natural id value, or {@code null} if there is no such persistent
 	 * instance.  If the instance is already associated with the session, return that instance, initializing it if
 	 * needed.  This method never returns an uninitialized instance.
 	 *
 	 * @param naturalIdValue The value of the natural-id for the entity to retrieve
 	 * 
 	 * @return The persistent instance or {@code null}
 	 */
 	public Object load(Object naturalIdValue);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/AbstractEntityInsertAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/AbstractEntityInsertAction.java
index 830e13cbf3..36b1a710a3 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/AbstractEntityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/AbstractEntityInsertAction.java
@@ -1,175 +1,206 @@
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
 package org.hibernate.action.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.internal.NonNullableTransientDependencies;
 import org.hibernate.engine.internal.Nullability;
 import org.hibernate.engine.internal.Versioning;
+import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * A base class for entity insert actions.
  *
  * @author Gail Badner
  */
 public abstract class AbstractEntityInsertAction extends EntityAction {
 	private transient Object[] state;
 	private final boolean isVersionIncrementDisabled;
 	private boolean isExecuted;
 	private boolean areTransientReferencesNullified;
 
 	/**
 	 * Constructs an AbstractEntityInsertAction object.
 	 *
 	 * @param id - the entity ID
 	 * @param state - the entity state
 	 * @param instance - the entity
 	 * @param isVersionIncrementDisabled - true, if version increment should
 	 *                                     be disabled; false, otherwise
 	 * @param persister - the entity persister
 	 * @param session - the session
 	 */
 	protected AbstractEntityInsertAction(
 			Serializable id,
 			Object[] state,
 			Object instance,
 			boolean isVersionIncrementDisabled,
 			EntityPersister persister,
 			SessionImplementor session) {
 		super( session, id, instance, persister );
 		this.state = state;
 		this.isVersionIncrementDisabled = isVersionIncrementDisabled;
 		this.isExecuted = false;
 		this.areTransientReferencesNullified = false;
+
+		handleNaturalIdPreSaveNotifications();
 	}
 
 	/**
 	 * Returns the entity state.
 	 *
 	 * NOTE: calling {@link #nullifyTransientReferencesIfNotAlready} can modify the
 	 *       entity state.
 	 * @return the entity state.
 	 *
 	 * @see {@link #nullifyTransientReferencesIfNotAlready}
 	 */
 	public Object[] getState() {
 		return state;
 	}
 
 	/**
 	 * Does this insert action need to be executed as soon as possible
 	 * (e.g., to generate an ID)?
 	 * @return true, if it needs to be executed as soon as possible;
 	 *         false, otherwise.
 	 */
 	public abstract boolean isEarlyInsert();
 
 	/**
 	 * Find the transient unsaved entity dependencies that are non-nullable.
 	 * @return the transient unsaved entity dependencies that are non-nullable,
 	 *         or null if there are none.
 	 */
 	public NonNullableTransientDependencies findNonNullableTransientEntities() {
 		return ForeignKeys.findNonNullableTransientEntities(
 				getPersister().getEntityName(),
 				getInstance(),
 				getState(),
 				isEarlyInsert(),
 				getSession()
 		);
 	}
 
 	/**
 	 * Nullifies any references to transient entities in the entity state
 	 * maintained by this action. References to transient entities
 	 * should be nullified when an entity is made "managed" or when this
 	 * action is executed, whichever is first.
 	 * <p/>
 	 * References will only be nullified the first time this method is
 	 * called for a this object, so it can safely be called both when
 	 * the entity is made "managed" and when this action is executed.
 	 *
 	 * @see {@link #makeEntityManaged() }
 	 */
 	protected final void nullifyTransientReferencesIfNotAlready() {
 		if ( ! areTransientReferencesNullified ) {
 			new ForeignKeys.Nullifier( getInstance(), false, isEarlyInsert(), getSession() )
 					.nullifyTransientReferences( getState(), getPersister().getPropertyTypes() );
 			new Nullability( getSession() ).checkNullability( getState(), getPersister(), false );
 			areTransientReferencesNullified = true;
 		}
 	}
 
 	/**
 	 * Make the entity "managed" by the persistence context.
 	 */
 	public final void makeEntityManaged() {
 		nullifyTransientReferencesIfNotAlready();
 		Object version = Versioning.getVersion( getState(), getPersister() );
 		getSession().getPersistenceContext().addEntity(
 				getInstance(),
 				( getPersister().isMutable() ? Status.MANAGED : Status.READ_ONLY ),
 				getState(),
 				getEntityKey(),
 				version,
 				LockMode.WRITE,
 				isExecuted,
 				getPersister(),
 				isVersionIncrementDisabled,
 				false
 		);
 	}
 
 	/**
 	 * Indicate that the action has executed.
 	 */
 	protected void markExecuted() {
 		this.isExecuted = true;
 	}
 
 	/**
 	 * Returns the {@link EntityKey}.
 	 * @return the {@link EntityKey}.
 	 */
 	protected abstract EntityKey getEntityKey();
 
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
+
+	/**
+	 * Handle sending notifications needed for natural-id before saving
+	 */
+	protected void handleNaturalIdPreSaveNotifications() {
+		// before save, we need to add a local (transactional) natural id cross-reference
+		getSession().getPersistenceContext().getNaturalIdHelper().manageLocalNaturalIdCrossReference(
+				getPersister(),
+				getId(),
+				state,
+				null,
+				CachedNaturalIdValueSource.INSERT
+		);
+	}
+
+	/**
+	 * Handle sending notifications needed for natural-id after saving
+	 */
+	protected void handleNaturalIdPostSaveNotifications() {
+		// after save, we need to manage the shared cache entries
+		getSession().getPersistenceContext().getNaturalIdHelper().manageSharedNaturalIdCrossReference(
+				getPersister(),
+				getId(),
+				state,
+				null,
+				CachedNaturalIdValueSource.INSERT
+		);
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
index 34c1f9badb..be914bad83 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityDeleteAction.java
@@ -1,183 +1,194 @@
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
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
+import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostDeleteEvent;
 import org.hibernate.event.spi.PostDeleteEventListener;
 import org.hibernate.event.spi.PreDeleteEvent;
 import org.hibernate.event.spi.PreDeleteEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 
 public final class EntityDeleteAction extends EntityAction {
 	private final Object version;
 	private final boolean isCascadeDeleteEnabled;
 	private final Object[] state;
 
 	private SoftLock lock;
+	private Object[] naturalIdValues;
 
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
+
+		// before remove we need to remove the local (transactional) natural id cross-reference
+		naturalIdValues = session.getPersistenceContext().getNaturalIdHelper().removeLocalNaturalIdCrossReference(
+				getPersister(),
+				getId(),
+				state
+		);
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
 			version = persister.getVersion( instance );
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
 
+		persistenceContext.getNaturalIdHelper().removeSharedNaturalIdCrossReference( persister, id, naturalIdValues );
+
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
 		for( PostDeleteEventListener listener : listenerGroup.listeners() ){
 			listener.onPostDelete( event );
 		}
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
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
index e9a91fd757..ba4a43e284 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
@@ -1,215 +1,216 @@
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
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostInsertEvent;
 import org.hibernate.event.spi.PostInsertEventListener;
 import org.hibernate.event.spi.PreInsertEvent;
 import org.hibernate.event.spi.PreInsertEventListener;
 import org.hibernate.persister.entity.EntityPersister;
 
 public final class EntityInsertAction extends AbstractEntityInsertAction {
 
 	private Object version;
 	private Object cacheEntry;
 
 	public EntityInsertAction(
 			Serializable id,
 			Object[] state,
 			Object instance,
 			Object version,
 			EntityPersister persister,
 			boolean isVersionIncrementDisabled,
 			SessionImplementor session) throws HibernateException {
 		super( id, state, instance, isVersionIncrementDisabled, persister, session );
 		this.version = version;
 	}
 
 	@Override
 	public boolean isEarlyInsert() {
 		return false;
 	}
 
 	@Override
 	protected EntityKey getEntityKey() {
 		return getSession().generateEntityKey( getId(), getPersister() );
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		nullifyTransientReferencesIfNotAlready();
 
 		EntityPersister persister = getPersister();
 		SessionImplementor session = getSession();
 		Object instance = getInstance();
 		Serializable id = getId();
 
 		boolean veto = preInsert();
 
 		// Don't need to lock the cache here, since if someone
 		// else inserted the same pk first, the insert would fail
 
 		if ( !veto ) {
 			
 			persister.insert( id, getState(), instance, session );
 		
 			EntityEntry entry = session.getPersistenceContext().getEntry( instance );
 			if ( entry == null ) {
-				throw new AssertionFailure( "possible nonthreadsafe access to session" );
+				throw new AssertionFailure( "possible non-threadsafe access to session" );
 			}
 			
 			entry.postInsert( getState() );
 	
 			if ( persister.hasInsertGeneratedProperties() ) {
 				persister.processInsertGeneratedProperties( id, instance, getState(), session );
 				if ( persister.isVersionPropertyGenerated() ) {
 					version = Versioning.getVersion( getState(), persister );
 				}
 				entry.postUpdate(instance, getState(), version);
 			}
 
 			getSession().getPersistenceContext().registerInsertedKey( getPersister(), getId() );
 		}
 
 		final SessionFactoryImplementor factory = getSession().getFactory();
 
 		if ( isCachePutEnabled( persister, session ) ) {
 			
 			CacheEntry ce = new CacheEntry(
 					getState(),
 					persister, 
 					persister.hasUninitializedLazyProperties( instance ),
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
-			
 		}
 
+		handleNaturalIdPostSaveNotifications();
+
 		postInsert();
 
 		if ( factory.getStatistics().isStatisticsEnabled() && !veto ) {
 			factory.getStatisticsImplementor()
 					.insertEntity( getPersister().getEntityName() );
 		}
 
 		markExecuted();
 	}
 
 	private void postInsert() {
 		EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_INSERT );
 		if ( listenerGroup.isEmpty() ) {
 			return;
 		}
 		final PostInsertEvent event = new PostInsertEvent(
 				getInstance(),
 				getId(),
 				getState(),
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
 				getState(),
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
 		final PreInsertEvent event = new PreInsertEvent( getInstance(), getId(), getState(), getPersister(), eventSource() );
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
index 3a855a0d32..0cf7832e96 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
@@ -1,274 +1,291 @@
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
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.engine.internal.Versioning;
+import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostUpdateEvent;
 import org.hibernate.event.spi.PostUpdateEventListener;
 import org.hibernate.event.spi.PreUpdateEvent;
 import org.hibernate.event.spi.PreUpdateEventListener;
 import org.hibernate.persister.entity.EntityPersister;
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
+
+		session.getPersistenceContext().getNaturalIdHelper().manageLocalNaturalIdCrossReference(
+				persister,
+				id,
+				state,
+				previousState,
+				CachedNaturalIdValueSource.UPDATE
+		);
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
 			previousVersion = persister.getVersion( instance );
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
 			if ( persister.isCacheInvalidationRequired() || entry.getStatus()!= Status.MANAGED ) {
 				persister.getCacheAccessStrategy().remove( ck );
 			}
 			else {
 				//TODO: inefficient if that cache is just going to ignore the updated state!
 				CacheEntry ce = new CacheEntry(
 						state, 
 						persister, 
 						persister.hasUninitializedLazyProperties( instance ),
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
 
+		session.getPersistenceContext().getNaturalIdHelper().manageSharedNaturalIdCrossReference(
+				persister,
+				id,
+				state,
+				previousState,
+				CachedNaturalIdValueSource.UPDATE
+		);
+
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java
index 8221e56900..e290e9a331 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/NaturalIdXrefDelegate.java
@@ -1,412 +1,487 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+import java.util.ArrayList;
 import java.util.Arrays;
+import java.util.Collection;
+import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 
-import org.hibernate.pretty.MessageHelper;
 import org.jboss.logging.Logger;
 
-import org.hibernate.action.spi.AfterTransactionCompletionProcess;
+import org.hibernate.AssertionFailure;
 import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
-import org.hibernate.cache.spi.access.SoftLock;
-import org.hibernate.engine.spi.CachedNaturalIdValueSource;
+import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.event.spi.EventSource;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * Maintains a {@link org.hibernate.engine.spi.PersistenceContext}-level 2-way cross-reference (xref) between the 
- * identifiers and natural ids of entities associated with the PersistenceContext.  Additionally coordinates
- * actions related to the shared caching of the entity's natural id. 
+ * identifiers and natural ids of entities associated with the PersistenceContext.
+ * <p/>
+ * Most operations resolve the proper {@link NaturalIdResolutionCache} to use based on the persister and 
+ * simply delegate calls there.
  * 
  * @author Steve Ebersole
  */
 public class NaturalIdXrefDelegate {
 	private static final Logger LOG = Logger.getLogger( NaturalIdXrefDelegate.class );
 
 	private final StatefulPersistenceContext persistenceContext;
 	private final Map<EntityPersister, NaturalIdResolutionCache> naturalIdResolutionCacheMap = new ConcurrentHashMap<EntityPersister, NaturalIdResolutionCache>();
 
 	public NaturalIdXrefDelegate(StatefulPersistenceContext persistenceContext) {
 		this.persistenceContext = persistenceContext;
 	}
 
+	/**
+	 * Access to the session (via the PersistenceContext) to which this delegate ultimately belongs.
+	 *
+	 * @return The session
+	 */
 	protected SessionImplementor session() {
 		return persistenceContext.getSession();
 	}
 
-	public void cacheNaturalIdResolution(
-			EntityPersister persister, 
-			final Serializable pk,
-			Object[] naturalIdValues,
-			CachedNaturalIdValueSource valueSource) {
-		persister = locatePersisterForKey( persister );
+	/**
+	 * Creates needed cross-reference entries between the given primary (pk) and natural (naturalIdValues) key values
+	 * for the given persister.  Returns an indication of whether entries were actually made.  If those values already
+	 * existed as an entry, {@code false} would be returned here.
+	 *
+	 * @param persister The persister representing the entity type.
+	 * @param pk The primary key value
+	 * @param naturalIdValues The natural id value(s)
+	 *
+	 * @return {@code true} if a new entry was actually added; {@code false} otherwise.
+	 */
+	public boolean cacheNaturalIdCrossReference(EntityPersister persister, Serializable pk, Object[] naturalIdValues) {
 		validateNaturalId( persister, naturalIdValues );
 
-		Object[] previousNaturalIdValues = valueSource == CachedNaturalIdValueSource.UPDATE
-				? persistenceContext.getNaturalIdSnapshot( pk, persister )
-				: null;
-
 		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		if ( entityNaturalIdResolutionCache == null ) {
 			entityNaturalIdResolutionCache = new NaturalIdResolutionCache( persister );
 			naturalIdResolutionCacheMap.put( persister, entityNaturalIdResolutionCache );
 		}
-
-		final boolean justAddedToLocalCache = entityNaturalIdResolutionCache.cache( pk, naturalIdValues );
-
-		// If second-level caching is enabled cache the resolution there as well
-		//		NOTE : the checks using 'justAddedToLocalCache' below protect only the stat journaling, not actually
-		//		putting into the shared cache.  we still put into the shared cache because that might have locking
-		//		semantics that we need to honor.  we protect the stat journaling in this manner because there
-		//		are cases where we have this method called multiple times and we want to avoid the multiple 'put'
-		// 		stats incrementing.
-		if ( persister.hasNaturalIdCache() ) {
-			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
-			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session() );
-
-			final SessionFactoryImplementor factory = session().getFactory();
-
-			switch ( valueSource ) {
-				case LOAD: {
-					final boolean put = naturalIdCacheAccessStrategy.putFromLoad(
-							naturalIdCacheKey,
-							pk,
-							session().getTimestamp(),
-							null
-					);
-
-					if ( put && justAddedToLocalCache && factory.getStatistics().isStatisticsEnabled() ) {
-						factory.getStatisticsImplementor()
-								.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
-					}
-
-					break;
-				}
-				case INSERT: {
-					final boolean put = naturalIdCacheAccessStrategy.insert( naturalIdCacheKey, pk );
-					if ( put && justAddedToLocalCache && factory.getStatistics().isStatisticsEnabled() ) {
-						factory.getStatisticsImplementor()
-								.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
-					}
-
-					( (EventSource) session() ).getActionQueue().registerProcess(
-							new AfterTransactionCompletionProcess() {
-								@Override
-								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
-									final boolean put = naturalIdCacheAccessStrategy.afterInsert( naturalIdCacheKey, pk );
-
-									if ( put && justAddedToLocalCache && factory.getStatistics().isStatisticsEnabled() ) {
-										factory.getStatisticsImplementor()
-												.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
-									}
-								}
-							}
-					);
-
-					break;
-				}
-				case UPDATE: {
-					final NaturalIdCacheKey previousCacheKey = new NaturalIdCacheKey( previousNaturalIdValues, persister, session() );
-					final SoftLock removalLock = naturalIdCacheAccessStrategy.lockItem( previousCacheKey, null );
-					naturalIdCacheAccessStrategy.remove( previousCacheKey );
-
-					final SoftLock lock = naturalIdCacheAccessStrategy.lockItem( naturalIdCacheKey, null );
-					final boolean put = naturalIdCacheAccessStrategy.update( naturalIdCacheKey, pk );
-					if ( put && justAddedToLocalCache && factory.getStatistics().isStatisticsEnabled() ) {
-						factory.getStatisticsImplementor()
-								.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
-					}
-
-					( (EventSource) session() ).getActionQueue().registerProcess(
-							new AfterTransactionCompletionProcess() {
-								@Override
-								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
-									naturalIdCacheAccessStrategy.unlockRegion( removalLock );
-									final boolean put = naturalIdCacheAccessStrategy.afterUpdate( naturalIdCacheKey, pk, lock );
-
-									if ( put && justAddedToLocalCache && factory.getStatistics().isStatisticsEnabled() ) {
-										factory.getStatisticsImplementor()
-												.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
-									}
-
-									naturalIdCacheAccessStrategy.unlockItem( naturalIdCacheKey, lock );
-								}
-							}
-					);
-
-					break;
-				}
-			}
-		}
-	}
-
-	protected EntityPersister locatePersisterForKey(EntityPersister persister) {
-		return persistenceContext.getSession().getFactory().getEntityPersister( persister.getRootEntityName() );
-	}
-
-	protected void validateNaturalId(EntityPersister persister, Object[] naturalIdValues) {
-		if ( !persister.hasNaturalIdentifier() ) {
-			throw new IllegalArgumentException( "Entity did not define a natrual-id" );
-		}
-		if ( persister.getNaturalIdentifierProperties().length != naturalIdValues.length ) {
-			throw new IllegalArgumentException( "Mismatch between expected number of natural-id values and found." );
-		}
+		return entityNaturalIdResolutionCache.cache( pk, naturalIdValues );
 	}
 
-	public void evictNaturalIdResolution(EntityPersister persister, final Serializable pk, Object[] deletedNaturalIdValues) {
+	/**
+	 * Handle removing cross reference entries for the given natural-id/pk combo
+	 *
+	 * @param persister The persister representing the entity type.
+	 * @param pk The primary key value
+	 * @param naturalIdValues The natural id value(s)
+	 * 
+	 * @return The cached values, if any.  May be different from incoming values.
+	 */
+	public Object[] removeNaturalIdCrossReference(EntityPersister persister, Serializable pk, Object[] naturalIdValues) {
 		persister = locatePersisterForKey( persister );
-		validateNaturalId( persister, deletedNaturalIdValues );
+		validateNaturalId( persister, naturalIdValues );
 
 		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		Object[] sessionCachedNaturalIdValues = null;
 		if ( entityNaturalIdResolutionCache != null ) {
 			final CachedNaturalId cachedNaturalId = entityNaturalIdResolutionCache.pkToNaturalIdMap
 					.remove( pk );
 			if ( cachedNaturalId != null ) {
 				entityNaturalIdResolutionCache.naturalIdToPkMap.remove( cachedNaturalId );
 				sessionCachedNaturalIdValues = cachedNaturalId.getValues();
 			}
 		}
 
 		if ( persister.hasNaturalIdCache() ) {
 			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister
 					.getNaturalIdCacheAccessStrategy();
-			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( deletedNaturalIdValues, persister, session() );
+			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session() );
 			naturalIdCacheAccessStrategy.evict( naturalIdCacheKey );
 
 			if ( sessionCachedNaturalIdValues != null
-					&& !Arrays.equals( sessionCachedNaturalIdValues, deletedNaturalIdValues ) ) {
+					&& !Arrays.equals( sessionCachedNaturalIdValues, naturalIdValues ) ) {
 				final NaturalIdCacheKey sessionNaturalIdCacheKey = new NaturalIdCacheKey( sessionCachedNaturalIdValues, persister, session() );
 				naturalIdCacheAccessStrategy.evict( sessionNaturalIdCacheKey );
 			}
 		}
+
+		return sessionCachedNaturalIdValues;
+	}
+
+	/**
+	 * Are the naturals id values cached here (if any) for the given persister+pk combo the same as the given values?
+	 *
+	 * @param persister The persister representing the entity type.
+	 * @param pk The primary key value
+	 * @param naturalIdValues The natural id value(s) to check
+	 * 
+	 * @return {@code true} if the given naturalIdValues match the current cached values; {@code false} otherwise.
+	 */
+	public boolean sameAsCached(EntityPersister persister, Serializable pk, Object[] naturalIdValues) {
+		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
+		return entityNaturalIdResolutionCache != null
+				&& entityNaturalIdResolutionCache.sameAsCached( pk, naturalIdValues );
+	}
+
+	/**
+	 * It is only valid to define natural ids at the root of an entity hierarchy.  This method makes sure we are 
+	 * using the root persister.
+	 *
+	 * @param persister The persister representing the entity type.
+	 * 
+	 * @return The root persister.
+	 */
+	protected EntityPersister locatePersisterForKey(EntityPersister persister) {
+		return persistenceContext.getSession().getFactory().getEntityPersister( persister.getRootEntityName() );
+	}
+
+	/**
+	 * Invariant validate of the natural id.  Checks include<ul>
+	 *     <li>that the entity defines a natural id</li>
+	 *     <li>the number of natural id values matches the expected number</li>
+	 * </ul>
+	 *
+	 * @param persister The persister representing the entity type.
+	 * @param naturalIdValues The natural id values
+	 */
+	protected void validateNaturalId(EntityPersister persister, Object[] naturalIdValues) {
+		if ( !persister.hasNaturalIdentifier() ) {
+			throw new IllegalArgumentException( "Entity did not define a natrual-id" );
+		}
+		if ( persister.getNaturalIdentifierProperties().length != naturalIdValues.length ) {
+			throw new IllegalArgumentException( "Mismatch between expected number of natural-id values and found." );
+		}
 	}
 
+	/**
+	 * Given a persister and primary key, find the locally cross-referenced natural id.
+	 *
+	 * @param persister The persister representing the entity type.
+	 * @param pk The entity primary key
+	 * 
+	 * @return The corresponding cross-referenced natural id values, or {@code null} if none 
+	 */
 	public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk) {
 		persister = locatePersisterForKey( persister );
 		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 		if ( entityNaturalIdResolutionCache == null ) {
 			return null;
 		}
 
 		final CachedNaturalId cachedNaturalId = entityNaturalIdResolutionCache.pkToNaturalIdMap.get( pk );
 		if ( cachedNaturalId == null ) {
 			return null;
 		}
 
 		return cachedNaturalId.getValues();
 	}
 
+	/**
+	 * Given a persister and natural-id value(s), find the locally cross-referenced primary key.  Will return
+	 * {@link PersistenceContext.NaturalIdHelper#INVALID_NATURAL_ID_REFERENCE} if the given natural ids are known to
+	 * be invalid (see {@link #stashInvalidNaturalIdReference}).
+	 *
+	 * @param persister The persister representing the entity type.
+	 * @param naturalIdValues The natural id value(s)
+	 * 
+	 * @return The corresponding cross-referenced primary key, 
+	 * 		{@link PersistenceContext.NaturalIdHelper#INVALID_NATURAL_ID_REFERENCE},
+	 * 		or {@code null} if none 
+	 */
 	public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues) {
 		persister = locatePersisterForKey( persister );
 		validateNaturalId( persister, naturalIdValues );
 
 		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
 
 		Serializable pk;
 		final CachedNaturalId cachedNaturalId = new CachedNaturalId( persister, naturalIdValues );
 		if ( entityNaturalIdResolutionCache != null ) {
 			pk = entityNaturalIdResolutionCache.naturalIdToPkMap.get( cachedNaturalId );
 
 			// Found in session cache
 			if ( pk != null ) {
 				if ( LOG.isTraceEnabled() ) {
 					LOG.trace(
 							"Resolved natural key -> primary key resolution in session cache: " +
 									persister.getRootEntityName() + "#[" +
 									Arrays.toString( naturalIdValues ) + "]"
 					);
 				}
 
 				return pk;
 			}
+
+			// if we did not find a hit, see if we know about these natural ids as invalid...
+			if ( entityNaturalIdResolutionCache.containsInvalidNaturalIdReference( naturalIdValues ) ) {
+				return PersistenceContext.NaturalIdHelper.INVALID_NATURAL_ID_REFERENCE;
+			}
 		}
 
 		// Session cache miss, see if second-level caching is enabled
 		if ( !persister.hasNaturalIdCache() ) {
 			return null;
 		}
 
 		// Try resolution from second-level cache
 		final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session() );
 
 		final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
 		pk = (Serializable) naturalIdCacheAccessStrategy.get( naturalIdCacheKey, session().getTimestamp() );
 
 		// Found in second-level cache, store in session cache
 		final SessionFactoryImplementor factory = session().getFactory();
 		if ( pk != null ) {
 			if ( factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor().naturalIdCacheHit(
 						naturalIdCacheAccessStrategy.getRegion().getName()
 				);
 			}
 
 			if ( LOG.isTraceEnabled() ) {
 				// protected to avoid Arrays.toString call unless needed
 				LOG.tracef(
 						"Found natural key [%s] -> primary key [%s] xref in second-level cache for %s",
 						Arrays.toString( naturalIdValues ),
 						pk,
 						persister.getRootEntityName()
 				);
 			}
 
 			if ( entityNaturalIdResolutionCache == null ) {
 				entityNaturalIdResolutionCache = new NaturalIdResolutionCache( persister );
 				naturalIdResolutionCacheMap.put( persister, entityNaturalIdResolutionCache );
 			}
 
 			entityNaturalIdResolutionCache.pkToNaturalIdMap.put( pk, cachedNaturalId );
 			entityNaturalIdResolutionCache.naturalIdToPkMap.put( cachedNaturalId, pk );
 		}
 		else if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().naturalIdCacheMiss( naturalIdCacheAccessStrategy.getRegion().getName() );
 		}
 
 		return pk;
 	}
 
+	/**
+	 * Return all locally cross-referenced primary keys for the given persister.  Used as part of load
+	 * synchronization process.
+	 *
+	 * @param persister The persister representing the entity type.
+	 * 
+	 * @return The primary keys
+	 * 
+	 * @see org.hibernate.NaturalIdLoadAccess#setSynchronizationEnabled
+	 */
+	public Collection<Serializable> getCachedPkResolutions(EntityPersister persister) {
+		persister = locatePersisterForKey( persister );
+
+		Collection<Serializable> pks = null;
+
+		NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
+		if ( entityNaturalIdResolutionCache != null ) {
+			pks = entityNaturalIdResolutionCache.pkToNaturalIdMap.keySet();
+		}
+
+		if ( pks == null || pks.isEmpty() ) {
+			return java.util.Collections.emptyList();
+		}
+		else {
+			return java.util.Collections.unmodifiableCollection( pks );
+		}
+	}
+
+	/**
+	 * As part of "load synchronization process", if a particular natural id is found to have changed we need to track
+	 * its invalidity until after the next flush.  This method lets the "load synchronization process" indicate
+	 * when it has encountered such changes.
+	 *
+	 * @param persister The persister representing the entity type.
+	 * @param invalidNaturalIdValues The "old" natural id values.
+	 *
+	 * @see org.hibernate.NaturalIdLoadAccess#setSynchronizationEnabled
+	 */
+	public void stashInvalidNaturalIdReference(EntityPersister persister, Object[] invalidNaturalIdValues) {
+		persister = locatePersisterForKey( persister );
+
+		final NaturalIdResolutionCache entityNaturalIdResolutionCache = naturalIdResolutionCacheMap.get( persister );
+		if ( entityNaturalIdResolutionCache == null ) {
+			throw new AssertionFailure( "Expecting NaturalIdResolutionCache to exist already for entity " + persister.getEntityName() );
+		}
+
+		entityNaturalIdResolutionCache.stashInvalidNaturalIdReference( invalidNaturalIdValues );
+	}
+
+	/**
+	 * Again, as part of "load synchronization process" we need to also be able to clear references to these
+	 * known-invalid natural-ids after flush.  This method exposes that capability.
+	 */
+	public void unStashInvalidNaturalIdReferences() {
+		for ( NaturalIdResolutionCache naturalIdResolutionCache : naturalIdResolutionCacheMap.values() ) {
+			naturalIdResolutionCache.unStashInvalidNaturalIdReferences();
+		}
+	}
 
+	/**
+	 * Used to put natural id values into collections.  Useful mainly to apply equals/hashCode implementations.
+	 */
 	private static class CachedNaturalId {
 		private final EntityPersister persister;
 		private final Object[] values;
 		private final Type[] naturalIdTypes;
 		private int hashCode;
 
 		public CachedNaturalId(EntityPersister persister, Object[] values) {
 			this.persister = persister;
 			this.values = values;
 
 			final int prime = 31;
 			int hashCodeCalculation = 1;
 			hashCodeCalculation = prime * hashCodeCalculation + persister.hashCode();
 
 			final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 			naturalIdTypes = new Type[ naturalIdPropertyIndexes.length ];
 			int i = 0;
 			for ( int naturalIdPropertyIndex : naturalIdPropertyIndexes ) {
 				final Type type = persister.getPropertyType( persister.getPropertyNames()[ naturalIdPropertyIndex ] );
 				naturalIdTypes[i] = type;
 				int elementHashCode = values[i] == null ? 0 :type.getHashCode( values[i], persister.getFactory() );
 				hashCodeCalculation = prime * hashCodeCalculation + elementHashCode;
 				i++;
 			}
 
 			this.hashCode = hashCodeCalculation;
 		}
 
 		public Object[] getValues() {
 			return values;
 		}
 
 		@Override
 		public int hashCode() {
 			return this.hashCode;
 		}
 
 		@Override
 		public boolean equals(Object obj) {
 			if ( this == obj ) {
 				return true;
 			}
 			if ( obj == null ) {
 				return false;
 			}
 			if ( getClass() != obj.getClass() ) {
 				return false;
 			}
 
 			final CachedNaturalId other = (CachedNaturalId) obj;
-			return persister.equals( other.persister ) && areSame( values, other.values );
+			return persister.equals( other.persister ) && isSame(other.values );
 		}
 
-		private boolean areSame(Object[] values, Object[] otherValues) {
+		private boolean isSame(Object[] otherValues) {
 			// lengths have already been verified at this point
 			for ( int i = 0; i < naturalIdTypes.length; i++ ) {
 				if ( ! naturalIdTypes[i].isEqual( values[i], otherValues[i], persister.getFactory() ) ) {
 					return false;
 				}
 			}
 			return true;
 		}
 	}
 
+	/**
+	 * Represents the persister-specific cross-reference cache.
+	 */
 	private static class NaturalIdResolutionCache implements Serializable {
 		private final EntityPersister persister;
 		private final Type[] naturalIdTypes;
 
 		private Map<Serializable, CachedNaturalId> pkToNaturalIdMap = new ConcurrentHashMap<Serializable, CachedNaturalId>();
 		private Map<CachedNaturalId, Serializable> naturalIdToPkMap = new ConcurrentHashMap<CachedNaturalId, Serializable>();
 
+		private List<CachedNaturalId> invalidNaturalIdList;
+
 		private NaturalIdResolutionCache(EntityPersister persister) {
 			this.persister = persister;
 
 			final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 			naturalIdTypes = new Type[ naturalIdPropertyIndexes.length ];
 			int i = 0;
 			for ( int naturalIdPropertyIndex : naturalIdPropertyIndexes ) {
 				naturalIdTypes[i++] = persister.getPropertyType( persister.getPropertyNames()[ naturalIdPropertyIndex ] );
 			}
 		}
 
 		public EntityPersister getPersister() {
 			return persister;
 		}
 
+		public boolean sameAsCached(Serializable pk, Object[] naturalIdValues) {
+			final CachedNaturalId initial = pkToNaturalIdMap.get( pk );
+			if ( initial != null ) {
+				if ( initial.isSame( naturalIdValues ) ) {
+					return true;
+				}
+			}
+			return false;
+		}
+
 		public boolean cache(Serializable pk, Object[] naturalIdValues) {
 			final CachedNaturalId initial = pkToNaturalIdMap.get( pk );
 			if ( initial != null ) {
-				if ( areSame( naturalIdValues, initial.getValues() ) ) {
+				if ( initial.isSame( naturalIdValues ) ) {
 					return false;
 				}
 				naturalIdToPkMap.remove( initial );
 			}
 
 			final CachedNaturalId cachedNaturalId = new CachedNaturalId( persister, naturalIdValues );
 			pkToNaturalIdMap.put( pk, cachedNaturalId );
 			naturalIdToPkMap.put( cachedNaturalId, pk );
 			
 			return true;
 		}
 
-		private boolean areSame(Object[] naturalIdValues, Object[] values) {
-			// lengths have already been verified at this point
-			for ( int i = 0; i < naturalIdTypes.length; i++ ) {
-				if ( ! naturalIdTypes[i].isEqual( naturalIdValues[i], values[i] ) ) {
-					return false;
-				}
+		public void stashInvalidNaturalIdReference(Object[] invalidNaturalIdValues) {
+			if ( invalidNaturalIdList == null ) {
+				invalidNaturalIdList = new ArrayList<CachedNaturalId>();
+			}
+			invalidNaturalIdList.add( new CachedNaturalId( persister, invalidNaturalIdValues ) );
+		}
+
+		public boolean containsInvalidNaturalIdReference(Object[] naturalIdValues) {
+			return invalidNaturalIdList != null
+					&& invalidNaturalIdList.contains( new CachedNaturalId( persister, naturalIdValues ) );
+		}
+
+		public void unStashInvalidNaturalIdReferences() {
+			if ( invalidNaturalIdList != null ) {
+				invalidNaturalIdList.clear();
 			}
-			return true;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
index 56546f5ffc..866aefc2ad 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
@@ -1,1786 +1,2026 @@
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
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
+import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Map.Entry;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
+import org.hibernate.action.spi.AfterTransactionCompletionProcess;
+import org.hibernate.cache.spi.NaturalIdCacheKey;
+import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
+import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.loading.internal.LoadContexts;
 import org.hibernate.engine.spi.AssociationKey;
 import org.hibernate.engine.spi.BatchFetchQueue;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
+import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ConcurrentReferenceHashMap;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.tuple.ElementWrapper;
 
 /**
  * A <strong>stateful</strong> implementation of the {@link PersistenceContext} contract meaning that we maintain this
  * state throughout the life of the persistence context.
  * </p>
  * IMPL NOTE: There is meant to be a one-to-one correspondence between a {@link org.hibernate.internal.SessionImpl}
  * and a PersistentContext.  Event listeners and other Session collaborators then use the PersistentContext to drive
  * their processing.
  *
  * @author Steve Ebersole
  */
 public class StatefulPersistenceContext implements PersistenceContext {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, StatefulPersistenceContext.class.getName() );
 
 	public static final Object NO_ROW = new MarkerObject( "NO_ROW" );
 
 	private static final int INIT_COLL_SIZE = 8;
 
 	private SessionImplementor session;
 
 	// Loaded entity instances, by EntityKey
 	private Map<EntityKey, Object> entitiesByKey;
 
 	// Loaded entity instances, by EntityUniqueKey
 	private Map<EntityUniqueKey, Object> entitiesByUniqueKey;
 
 	// Identity map of EntityEntry instances, by the entity instance
 	private Map<Object,EntityEntry> entityEntries;
 
 	// Entity proxies, by EntityKey
 	private Map<EntityKey, Object> proxiesByKey;
 
 	// Snapshots of current database state for entities
 	// that have *not* been loaded
 	private Map<EntityKey, Object> entitySnapshotsByKey;
 
 	// Identity map of array holder ArrayHolder instances, by the array instance
 	private Map<Object, PersistentCollection> arrayHolders;
 
 	// Identity map of CollectionEntry instances, by the collection wrapper
 	private IdentityMap<PersistentCollection, CollectionEntry> collectionEntries;
 
 	// Collection wrappers, by the CollectionKey
 	private Map<CollectionKey, PersistentCollection> collectionsByKey;
 
 	// Set of EntityKeys of deleted objects
 	private HashSet<EntityKey> nullifiableEntityKeys;
 
 	// properties that we have tried to load, and not found in the database
 	private HashSet<AssociationKey> nullAssociations;
 
 	// A list of collection wrappers that were instantiating during result set
 	// processing, that we will need to initialize at the end of the query
 	private List<PersistentCollection> nonlazyCollections;
 
 	// A container for collections we load up when the owning entity is not
 	// yet loaded ... for now, this is purely transient!
 	private Map<CollectionKey,PersistentCollection> unownedCollections;
 
 	// Parent entities cache by their child for cascading
 	// May be empty or not contains all relation
 	private Map<Object,Object> parentsByChild;
 
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
 
 		entitiesByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
 		entitiesByUniqueKey = new HashMap<EntityUniqueKey, Object>( INIT_COLL_SIZE );
 		//noinspection unchecked
 		proxiesByKey = new ConcurrentReferenceHashMap<EntityKey, Object>( INIT_COLL_SIZE, .75f, 1, ConcurrentReferenceHashMap.ReferenceType.STRONG, ConcurrentReferenceHashMap.ReferenceType.WEAK, null );
 		entitySnapshotsByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
 
 		entityEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		collectionEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		parentsByChild = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 
 		collectionsByKey = new HashMap<CollectionKey, PersistentCollection>( INIT_COLL_SIZE );
 		arrayHolders = new IdentityHashMap<Object, PersistentCollection>( INIT_COLL_SIZE );
 
 		nullifiableEntityKeys = new HashSet<EntityKey>();
 
 		initTransientState();
 	}
 
 	private void initTransientState() {
 		nullAssociations = new HashSet<AssociationKey>( INIT_COLL_SIZE );
 		nonlazyCollections = new ArrayList<PersistentCollection>( INIT_COLL_SIZE );
 	}
 
 	@Override
 	public boolean isStateless() {
 		return false;
 	}
 
 	@Override
 	public SessionImplementor getSession() {
 		return session;
 	}
 
 	@Override
 	public LoadContexts getLoadContexts() {
 		if ( loadContexts == null ) {
 			loadContexts = new LoadContexts( this );
 		}
 		return loadContexts;
 	}
 
 	@Override
 	public void addUnownedCollection(CollectionKey key, PersistentCollection collection) {
 		if (unownedCollections==null) {
 			unownedCollections = new HashMap<CollectionKey,PersistentCollection>(INIT_COLL_SIZE);
 		}
 		unownedCollections.put( key, collection );
 	}
 
 	@Override
 	public PersistentCollection useUnownedCollection(CollectionKey key) {
 		return ( unownedCollections == null ) ? null : unownedCollections.remove( key );
 	}
 
 	@Override
 	public BatchFetchQueue getBatchFetchQueue() {
 		if (batchFetchQueue==null) {
 			batchFetchQueue = new BatchFetchQueue(this);
 		}
 		return batchFetchQueue;
 	}
 
 	@Override
 	public void clear() {
 		for ( Object o : proxiesByKey.values() ) {
 			if ( o == null ) {
 				//entry may be GCd
 				continue;
 			}
 			((HibernateProxy) o).getHibernateLazyInitializer().unsetSession();
 		}
 		for ( Map.Entry<PersistentCollection, CollectionEntry> aCollectionEntryArray : IdentityMap.concurrentEntries( collectionEntries ) ) {
 			aCollectionEntryArray.getKey().unsetSession( getSession() );
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
 
 	@Override
 	public boolean isDefaultReadOnly() {
 		return defaultReadOnly;
 	}
 
 	@Override
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		this.defaultReadOnly = defaultReadOnly;
 	}
 
 	@Override
 	public boolean hasNonReadOnlyEntities() {
 		return hasNonReadOnlyEntities;
 	}
 
 	@Override
 	public void setEntryStatus(EntityEntry entry, Status status) {
 		entry.setStatus(status);
 		setHasNonReadOnlyEnties(status);
 	}
 
 	private void setHasNonReadOnlyEnties(Status status) {
 		if ( status==Status.DELETED || status==Status.MANAGED || status==Status.SAVING ) {
 			hasNonReadOnlyEntities = true;
 		}
 	}
 
 	@Override
 	public void afterTransactionCompletion() {
 		cleanUpInsertedKeysAfterTransaction();
 		// Downgrade locks
 		for ( EntityEntry o : entityEntries.values() ) {
 			o.setLockMode( LockMode.NONE );
 		}
 	}
 
 	/**
 	 * Get the current state of the entity as known to the underlying
 	 * database, or null if there is no corresponding row
 	 */
 	@Override
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
 
 	@Override
 	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister)
 	throws HibernateException {
 		if ( !persister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
+		persister = locateProperPersister( persister );
+
 		// let's first see if it is part of the natural id cache...
-		final Object[] cachedValue = findCachedNaturalId( persister, id );
+		final Object[] cachedValue = naturalIdHelper.findCachedNaturalId( persister, id );
 		if ( cachedValue != null ) {
 			return cachedValue;
 		}
 
 		// check to see if the natural id is mutable/immutable
 		if ( persister.getEntityMetamodel().hasImmutableNaturalId() ) {
 			// an immutable natural-id is not retrieved during a normal database-snapshot operation...
 			final Object[] dbValue = persister.getNaturalIdentifierSnapshot( id, session );
-			cacheNaturalIdResolution( persister, id, dbValue, CachedNaturalIdValueSource.LOAD );
+			naturalIdHelper.cacheNaturalIdCrossReferenceFromLoad(
+					persister,
+					id,
+					dbValue
+			);
 			return dbValue;
 		}
 		else {
 			// for a mutable natural there is a likelihood that the the information will already be
 			// snapshot-cached.
 			final int[] props = persister.getNaturalIdentifierProperties();
 			final Object[] entitySnapshot = getDatabaseSnapshot( id, persister );
 			if ( entitySnapshot == NO_ROW ) {
 				return null;
 			}
 
 			final Object[] naturalIdSnapshotSubSet = new Object[ props.length ];
 			for ( int i = 0; i < props.length; i++ ) {
 				naturalIdSnapshotSubSet[i] = entitySnapshot[ props[i] ];
 			}
-			cacheNaturalIdResolution( persister, id, naturalIdSnapshotSubSet, CachedNaturalIdValueSource.LOAD );
+			naturalIdHelper.cacheNaturalIdCrossReferenceFromLoad(
+					persister,
+					id,
+					naturalIdSnapshotSubSet
+			);
 			return naturalIdSnapshotSubSet;
 		}
 	}
 
+	private EntityPersister locateProperPersister(EntityPersister persister) {
+		return session.getFactory().getEntityPersister( persister.getRootEntityName() );
+	}
+
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
 	@Override
 	public Object[] getCachedDatabaseSnapshot(EntityKey key) {
 		Object snapshot = entitySnapshotsByKey.get( key );
 		if ( snapshot == NO_ROW ) {
 			throw new IllegalStateException( "persistence context reported no row snapshot for " + MessageHelper.infoString( key.getEntityName(), key.getIdentifier() ) );
 		}
 		return ( Object[] ) snapshot;
 	}
 
 	@Override
 	public void addEntity(EntityKey key, Object entity) {
 		entitiesByKey.put(key, entity);
 		getBatchFetchQueue().removeBatchLoadableEntityKey(key);
 	}
 
 	/**
 	 * Get the entity instance associated with the given
 	 * <tt>EntityKey</tt>
 	 */
 	@Override
 	public Object getEntity(EntityKey key) {
 		return entitiesByKey.get(key);
 	}
 
 	@Override
 	public boolean containsEntity(EntityKey key) {
 		return entitiesByKey.containsKey(key);
 	}
 
 	/**
 	 * Remove an entity from the session cache, also clear
 	 * up other state associated with the entity, all except
 	 * for the <tt>EntityEntry</tt>
 	 */
 	@Override
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
 	@Override
 	public Object getEntity(EntityUniqueKey euk) {
 		return entitiesByUniqueKey.get(euk);
 	}
 
 	/**
 	 * Add an entity to the cache by unique key
 	 */
 	@Override
 	public void addEntity(EntityUniqueKey euk, Object entity) {
 		entitiesByUniqueKey.put(euk, entity);
 	}
 
 	/**
 	 * Retrieve the EntityEntry representation of the given entity.
 	 *
 	 * @param entity The entity for which to locate the EntityEntry.
 	 * @return The EntityEntry for the given entity.
 	 */
 	@Override
 	public EntityEntry getEntry(Object entity) {
 		return entityEntries.get(entity);
 	}
 
 	/**
 	 * Remove an entity entry from the session cache
 	 */
 	@Override
 	public EntityEntry removeEntry(Object entity) {
 		return entityEntries.remove(entity);
 	}
 
 	/**
 	 * Is there an EntityEntry for this instance?
 	 */
 	@Override
 	public boolean isEntryFor(Object entity) {
 		return entityEntries.containsKey(entity);
 	}
 
 	/**
 	 * Get the collection entry for a persistent collection
 	 */
 	@Override
 	public CollectionEntry getCollectionEntry(PersistentCollection coll) {
 		return collectionEntries.get(coll);
 	}
 
 	/**
 	 * Adds an entity to the internal caches.
 	 */
 	@Override
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
 			boolean lazyPropertiesAreUnfetched) {
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
 	@Override
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
 				persister.getEntityMode(),
 				session.getTenantIdentifier(),
 				disableVersionIncrement,
 				lazyPropertiesAreUnfetched,
 				this
 		);
 		entityEntries.put(entity, e);
 
 		setHasNonReadOnlyEnties(status);
 		return e;
 	}
 
 	@Override
 	public boolean containsCollection(PersistentCollection collection) {
 		return collectionEntries.containsKey(collection);
 	}
 
 	@Override
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
 	@Override
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
 	@Override
 	public void reassociateProxy(Object value, Serializable id) throws MappingException {
 		if ( value instanceof ElementWrapper ) {
 			value = ( (ElementWrapper) value ).getElement();
 		}
 
 		if ( value instanceof HibernateProxy ) {
 			LOG.debugf( "Setting proxy identifier: %s", id );
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
 	@Override
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
 	@Override
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
 	@Override
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
 	@Override
 	public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object)
 			throws HibernateException {
 
 		final Class concreteProxyClass = persister.getConcreteProxyClass();
 		boolean alreadyNarrow = concreteProxyClass.isAssignableFrom( proxy.getClass() );
 
 		if ( !alreadyNarrow ) {
 			LOG.narrowingProxy( concreteProxyClass );
 
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
 	@Override
 	public Object proxyFor(EntityPersister persister, EntityKey key, Object impl)
 	throws HibernateException {
 		if ( !persister.hasProxy() ) {
 			return impl;
 		}
 		Object proxy = proxiesByKey.get( key );
 		return ( proxy != null ) ? narrowProxy( proxy, persister, key, impl ) : impl;
 	}
 
 	/**
 	 * Return the existing proxy associated with the given <tt>EntityKey</tt>, or the
 	 * argument (the entity associated with the key) if no proxy exists.
 	 * (slower than the form above)
 	 */
 	@Override
 	public Object proxyFor(Object impl) throws HibernateException {
 		EntityEntry e = getEntry(impl);
 		return proxyFor( e.getPersister(), e.getEntityKey(), impl );
 	}
 
 	/**
 	 * Get the entity that owns this persistent collection
 	 */
 	@Override
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
 	@Override
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
 	@Override
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
 	@Override
 	public void addUninitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) {
 		CollectionEntry ce = new CollectionEntry(collection, persister, id, flushing);
 		addCollection(collection, ce, id);
 	}
 
 	/**
 	 * add a detached uninitialized collection
 	 */
 	@Override
 	public void addUninitializedDetachedCollection(CollectionPersister persister, PersistentCollection collection) {
 		CollectionEntry ce = new CollectionEntry( persister, collection.getKey() );
 		addCollection( collection, ce, collection.getKey() );
 	}
 
 	/**
 	 * Add a new collection (ie. a newly created one, just instantiated by the
 	 * application, with no database state or snapshot)
 	 * @param collection The collection to be associated with the persistence context
 	 */
 	@Override
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
 		CollectionKey collectionKey = new CollectionKey( entry.getLoadedPersister(), key );
 		PersistentCollection old = collectionsByKey.put( collectionKey, coll );
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
 	@Override
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
 	@Override
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
 	@Override
 	public PersistentCollection getCollection(CollectionKey collectionKey) {
 		return collectionsByKey.get( collectionKey );
 	}
 
 	/**
 	 * Register a collection for non-lazy loading at the end of the
 	 * two-phase load
 	 */
 	@Override
 	public void addNonLazyCollection(PersistentCollection collection) {
 		nonlazyCollections.add(collection);
 	}
 
 	/**
 	 * Force initialization of all non-lazy collections encountered during
 	 * the current two-phase load (actually, this is a no-op, unless this
 	 * is the "outermost" load)
 	 */
 	@Override
 	public void initializeNonLazyCollections() throws HibernateException {
 		if ( loadCounter == 0 ) {
 			LOG.debug( "Initializing non-lazy collections" );
 			//do this work only at the very highest level of the load
 			loadCounter++; //don't let this method be called recursively
 			try {
 				int size;
 				while ( ( size = nonlazyCollections.size() ) > 0 ) {
 					//note that each iteration of the loop may add new elements
 					nonlazyCollections.remove( size - 1 ).forceInitialization();
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
 	@Override
 	public PersistentCollection getCollectionHolder(Object array) {
 		return arrayHolders.get(array);
 	}
 
 	/**
 	 * Register a <tt>PersistentCollection</tt> object for an array.
 	 * Associates a holder with an array - MUST be called after loading
 	 * array, since the array instance is not created until endLoad().
 	 */
 	@Override
 	public void addCollectionHolder(PersistentCollection holder) {
 		//TODO:refactor + make this method private
 		arrayHolders.put( holder.getValue(), holder );
 	}
 
 	@Override
 	public PersistentCollection removeCollectionHolder(Object array) {
 		return arrayHolders.remove(array);
 	}
 
 	/**
 	 * Get the snapshot of the pre-flush collection state
 	 */
 	@Override
 	public Serializable getSnapshot(PersistentCollection coll) {
 		return getCollectionEntry(coll).getSnapshot();
 	}
 
 	/**
 	 * Get the collection entry for a collection passed to filter,
 	 * which might be a collection wrapper, an array, or an unwrapped
 	 * collection. Return null if there is no entry.
 	 */
 	@Override
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
 				Iterator<PersistentCollection> wrappers = collectionEntries.keyIterator();
 				while ( wrappers.hasNext() ) {
 					PersistentCollection pc = wrappers.next();
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
 	@Override
 	public Object getProxy(EntityKey key) {
 		return proxiesByKey.get(key);
 	}
 
 	/**
 	 * Add a proxy to the session cache
 	 */
 	@Override
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
 	@Override
 	public Object removeProxy(EntityKey key) {
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.removeBatchLoadableEntityKey( key );
 			batchFetchQueue.removeSubselect( key );
 		}
 		return proxiesByKey.remove( key );
 	}
 
 	/**
 	 * Retrieve the set of EntityKeys representing nullifiable references
 	 */
 	@Override
 	public HashSet getNullifiableEntityKeys() {
 		return nullifiableEntityKeys;
 	}
 
 	@Override
 	public Map getEntitiesByKey() {
 		return entitiesByKey;
 	}
 
 	public Map getProxiesByKey() {
 		return proxiesByKey;
 	}
 
 	@Override
 	public Map getEntityEntries() {
 		return entityEntries;
 	}
 
 	@Override
 	public Map getCollectionEntries() {
 		return collectionEntries;
 	}
 
 	@Override
 	public Map getCollectionsByKey() {
 		return collectionsByKey;
 	}
 
 	@Override
 	public int getCascadeLevel() {
 		return cascading;
 	}
 
 	@Override
 	public int incrementCascadeLevel() {
 		return ++cascading;
 	}
 
 	@Override
 	public int decrementCascadeLevel() {
 		return --cascading;
 	}
 
 	@Override
 	public boolean isFlushing() {
 		return flushing;
 	}
 
 	@Override
 	public void setFlushing(boolean flushing) {
+		final boolean afterFlush = this.flushing && ! flushing;
 		this.flushing = flushing;
+		if ( afterFlush ) {
+			getNaturalIdHelper().cleanupFromSynchronizations();
+		}
 	}
 
 	/**
 	 * Call this before beginning a two-phase load
 	 */
 	@Override
 	public void beforeLoad() {
 		loadCounter++;
 	}
 
 	/**
 	 * Call this after finishing a two-phase load
 	 */
 	@Override
 	public void afterLoad() {
 		loadCounter--;
 	}
 
 	@Override
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
 		return new StringBuilder()
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
 	@Override
 	public Serializable getOwnerId(String entityName, String propertyName, Object childEntity, Map mergeMap) {
 		final String collectionRole = entityName + '.' + propertyName;
 		final EntityPersister persister = session.getFactory().getEntityPersister( entityName );
 		final CollectionPersister collectionPersister = session.getFactory().getCollectionPersister( collectionRole );
 
 	    // try cache lookup first
 		Object parent = parentsByChild.get( childEntity );
 		if ( parent != null ) {
 			final EntityEntry entityEntry = entityEntries.get( parent );
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
 		for ( Entry<Object,EntityEntry> me : IdentityMap.concurrentEntries( entityEntries ) ) {
 			final EntityEntry entityEntry = me.getValue();
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
 			for ( Object o : mergeMap.entrySet() ) {
 				final Entry mergeMapEntry = (Entry) o;
 				if ( mergeMapEntry.getKey() instanceof HibernateProxy ) {
 					final HibernateProxy proxy = (HibernateProxy) mergeMapEntry.getKey();
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
 		Object collection = persister.getPropertyValue( potentialParent, property );
 		return collection != null
 				&& Hibernate.isInitialized( collection )
 				&& collectionPersister.getCollectionType().contains( collection, childEntity, session );
 	}
 
 	/**
 	 * Search the persistence context for an index of the child object,
 	 * given a collection role
 	 */
 	@Override
 	public Object getIndexInOwner(String entity, String property, Object childEntity, Map mergeMap) {
 
 		EntityPersister persister = session.getFactory()
 				.getEntityPersister(entity);
 		CollectionPersister cp = session.getFactory()
 				.getCollectionPersister(entity + '.' + property);
 
 	    // try cache lookup first
 	    Object parent = parentsByChild.get(childEntity);
 		if (parent != null) {
 			final EntityEntry entityEntry = entityEntries.get(parent);
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
 		for ( Entry<Object, EntityEntry> me : IdentityMap.concurrentEntries( entityEntries ) ) {
 			EntityEntry ee = me.getValue();
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
 			Object potentialParent){
 		Object collection = persister.getPropertyValue( potentialParent, property );
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
 	@Override
 	public void addNullProperty(EntityKey ownerKey, String propertyName) {
 		nullAssociations.add( new AssociationKey(ownerKey, propertyName) );
 	}
 
 	/**
 	 * Is the association property belonging to the keyed entity null?
 	 */
 	@Override
 	public boolean isPropertyNull(EntityKey ownerKey, String propertyName) {
 		return nullAssociations.contains( new AssociationKey(ownerKey, propertyName) );
 	}
 
 	private void clearNullProperties() {
 		nullAssociations.clear();
 	}
 
 	@Override
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
 
 	@Override
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
 
 	@Override
 	public void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId) {
 		Object entity = entitiesByKey.remove( oldKey );
 		EntityEntry oldEntry = entityEntries.remove( entity );
 		parentsByChild.clear();
 
 		final EntityKey newKey = session.generateEntityKey( generatedId, oldEntry.getPersister() );
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
 		final boolean tracing = LOG.isTraceEnabled();
 		if ( tracing ) LOG.trace( "Serializing persistent-context" );
 
 		oos.writeBoolean( defaultReadOnly );
 		oos.writeBoolean( hasNonReadOnlyEntities );
 
 		oos.writeInt( entitiesByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entitiesByKey.size() + "] entitiesByKey entries");
 		Iterator itr = entitiesByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitiesByUniqueKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entitiesByUniqueKey.size() + "] entitiesByUniqueKey entries");
 		itr = entitiesByUniqueKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityUniqueKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( proxiesByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + proxiesByKey.size() + "] proxiesByKey entries");
 		itr = proxiesByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( (EntityKey) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entitySnapshotsByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entitySnapshotsByKey.size() + "] entitySnapshotsByKey entries");
 		itr = entitySnapshotsByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( EntityKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( entityEntries.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + entityEntries.size() + "] entityEntries entries");
 		itr = entityEntries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			( ( EntityEntry ) entry.getValue() ).serialize( oos );
 		}
 
 		oos.writeInt( collectionsByKey.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + collectionsByKey.size() + "] collectionsByKey entries");
 		itr = collectionsByKey.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			( ( CollectionKey ) entry.getKey() ).serialize( oos );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( collectionEntries.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + collectionEntries.size() + "] collectionEntries entries");
 		itr = collectionEntries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			( ( CollectionEntry ) entry.getValue() ).serialize( oos );
 		}
 
 		oos.writeInt( arrayHolders.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + arrayHolders.size() + "] arrayHolders entries");
 		itr = arrayHolders.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			Map.Entry entry = ( Map.Entry ) itr.next();
 			oos.writeObject( entry.getKey() );
 			oos.writeObject( entry.getValue() );
 		}
 
 		oos.writeInt( nullifiableEntityKeys.size() );
 		if ( tracing ) LOG.trace("Starting serialization of [" + nullifiableEntityKeys.size() + "] nullifiableEntityKey entries");
 		for ( EntityKey entry : nullifiableEntityKeys ) {
 			entry.serialize( oos );
 		}
 	}
 
 	public static StatefulPersistenceContext deserialize(
 			ObjectInputStream ois,
 			SessionImplementor session) throws IOException, ClassNotFoundException {
 		final boolean tracing = LOG.isTraceEnabled();
 		if ( tracing ) LOG.trace("Serializing persistent-context");
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
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitiesByKey entries");
 			rtn.entitiesByKey = new HashMap<EntityKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitiesByUniqueKey entries");
 			rtn.entitiesByUniqueKey = new HashMap<EntityUniqueKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByUniqueKey.put( EntityUniqueKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] proxiesByKey entries");
 			//noinspection unchecked
 			rtn.proxiesByKey = new ConcurrentReferenceHashMap<EntityKey, Object>(
 					count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count,
 					.75f,
 					1,
 					ConcurrentReferenceHashMap.ReferenceType.STRONG,
 					ConcurrentReferenceHashMap.ReferenceType.WEAK,
 					null
 			);
 			for ( int i = 0; i < count; i++ ) {
 				EntityKey ek = EntityKey.deserialize( ois, session );
 				Object proxy = ois.readObject();
 				if ( proxy instanceof HibernateProxy ) {
 					( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().setSession( session );
 					rtn.proxiesByKey.put( ek, proxy );
 				} else {
 					if ( tracing ) LOG.trace("Encountered prunded proxy");
 				}
 				// otherwise, the proxy was pruned during the serialization process
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitySnapshotsByKey entries");
 			rtn.entitySnapshotsByKey = new HashMap<EntityKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitySnapshotsByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entityEntries entries");
 			rtn.entityEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				Object entity = ois.readObject();
 				EntityEntry entry = EntityEntry.deserialize( ois, rtn );
 				rtn.entityEntries.put( entity, entry );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] collectionsByKey entries");
 			rtn.collectionsByKey = new HashMap<CollectionKey,PersistentCollection>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.collectionsByKey.put( CollectionKey.deserialize( ois, session ), (PersistentCollection) ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] collectionEntries entries");
 			rtn.collectionEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				final PersistentCollection pc = ( PersistentCollection ) ois.readObject();
 				final CollectionEntry ce = CollectionEntry.deserialize( ois, session );
 				pc.setCurrentSession( session );
 				rtn.collectionEntries.put( pc, ce );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] arrayHolders entries");
 			rtn.arrayHolders = new IdentityHashMap<Object, PersistentCollection>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.arrayHolders.put( ois.readObject(), (PersistentCollection) ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] nullifiableEntityKey entries");
 			rtn.nullifiableEntityKeys = new HashSet<EntityKey>();
 			for ( int i = 0; i < count; i++ ) {
 				rtn.nullifiableEntityKeys.add( EntityKey.deserialize( ois, session ) );
 			}
 
 		}
 		catch ( HibernateException he ) {
 			throw new InvalidObjectException( he.getMessage() );
 		}
 
 		return rtn;
 	}
 
 	@Override
 	public void addChildParent(Object child, Object parent) {
 		parentsByChild.put(child, parent);
 	}
 
 	@Override
 	public void removeChildParent(Object child) {
 	   parentsByChild.remove(child);
 	}
 
 
 	// INSERTED KEYS HANDLING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private HashMap<String,List<Serializable>> insertedKeysMap;
 
 	@Override
 	public void registerInsertedKey(EntityPersister persister, Serializable id) {
 		// we only are worried about registering these if the persister defines caching
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
 
 	@Override
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
 
 
 
 	// NATURAL ID RESOLUTION HANDLING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	private NaturalIdXrefDelegate naturalIdXrefDelegate = new NaturalIdXrefDelegate( this );
+	private final NaturalIdXrefDelegate naturalIdXrefDelegate = new NaturalIdXrefDelegate( this );
+
+	private final NaturalIdHelper naturalIdHelper = new NaturalIdHelper() {
+		@Override
+		public void cacheNaturalIdCrossReferenceFromLoad(
+				EntityPersister persister,
+				Serializable id,
+				Object[] naturalIdValues) {
+			if ( !persister.hasNaturalIdentifier() ) {
+				// nothing to do
+				return;
+			}
+
+			persister = locateProperPersister( persister );
 
-	@Override
-	public void entityStateInsertedNotification(EntityEntry entityEntry, Object[] state) {
-		final EntityPersister persister = entityEntry.getPersister();
-		if ( !persister.hasNaturalIdentifier() ) {
-			// nothing to do
-			return;
+			// 'justAddedLocally' is meant to handle the case where we would get double stats jounaling
+			//	from a single load event.  The first put journal would come from the natural id resolution;
+			// the second comes from the entity loading.  In this condition, we want to avoid the multiple
+			// 'put' stats incrementing.
+			boolean justAddedLocally = naturalIdXrefDelegate.cacheNaturalIdCrossReference( persister, id, naturalIdValues );
+
+			if ( justAddedLocally && persister.hasNaturalIdCache() ) {
+				managedSharedCacheEntries( persister, id, naturalIdValues, CachedNaturalIdValueSource.LOAD );
+			}
 		}
 
-		final Object[] naturalIdValues = getNaturalIdValues( state, persister );
+		@Override
+		public void manageLocalNaturalIdCrossReference(
+				EntityPersister persister,
+				Serializable id,
+				Object[] state,
+				Object[] previousState,
+				CachedNaturalIdValueSource source) {
+			if ( !persister.hasNaturalIdentifier() ) {
+				// nothing to do
+				return;
+			}
 
-		// cache
-		naturalIdXrefDelegate.cacheNaturalIdResolution(
-				persister,
-				entityEntry.getId(),
-				naturalIdValues,
-				CachedNaturalIdValueSource.INSERT
-		);
-	}
+			persister = locateProperPersister( persister );
+			final Object[] naturalIdValues = extractNaturalIdValues( state, persister );
 
-	@Override
-	public void entityStateUpdatedNotification(EntityEntry entityEntry, Object[] state) {
-		final EntityPersister persister = entityEntry.getPersister();
-		if ( !persister.hasNaturalIdentifier() ) {
-			// nothing to do
-			return;
+			// cache
+			naturalIdXrefDelegate.cacheNaturalIdCrossReference( persister, id, naturalIdValues );
 		}
 
-		final Object[] naturalIdValues = getNaturalIdValues( state, persister );
+		@Override
+		public void manageSharedNaturalIdCrossReference(
+				EntityPersister persister,
+				final Serializable id,
+				Object[] state,
+				Object[] previousState,
+				CachedNaturalIdValueSource source) {
+			if ( !persister.hasNaturalIdentifier() ) {
+				// nothing to do
+				return;
+			}
 
-		// re-cache
-		naturalIdXrefDelegate.cacheNaturalIdResolution(
-				persister,
-				entityEntry.getId(),
-				naturalIdValues,
-				CachedNaturalIdValueSource.UPDATE
-		);
-	}
+			if ( !persister.hasNaturalIdCache() ) {
+				// nothing to do
+				return;
+			}
 
-	@Override
-	public void entityStateDeletedNotification(EntityEntry entityEntry, Object[] deletedState) {
-		final EntityPersister persister = entityEntry.getPersister();
-		if ( !persister.hasNaturalIdentifier() ) {
-			// nothing to do
-			return;
+			persister = locateProperPersister( persister );
+			final Object[] naturalIdValues = extractNaturalIdValues( state, persister );
+
+			managedSharedCacheEntries( persister, id, naturalIdValues, source );
 		}
 
-		final Object[] naturalIdValues = getNaturalIdValues( deletedState, persister );
+		private void managedSharedCacheEntries(
+				EntityPersister persister,
+				final Serializable id,
+				Object[] naturalIdValues,
+				CachedNaturalIdValueSource source) {
+			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
+			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session );
+
+			final SessionFactoryImplementor factory = session.getFactory();
+
+			switch ( source ) {
+				case LOAD: {
+					final boolean put = naturalIdCacheAccessStrategy.putFromLoad(
+							naturalIdCacheKey,
+							id,
+							session.getTimestamp(),
+							null
+					);
+
+					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
+						factory.getStatisticsImplementor().naturalIdCachePut(
+								naturalIdCacheAccessStrategy.getRegion()
+										.getName()
+						);
+					}
+
+					break;
+				}
+				case INSERT: {
+					final boolean put = naturalIdCacheAccessStrategy.insert( naturalIdCacheKey, id );
+					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
+						factory.getStatisticsImplementor()
+								.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
+					}
+
+					( (EventSource) session ).getActionQueue().registerProcess(
+							new AfterTransactionCompletionProcess() {
+								@Override
+								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
+									final boolean put = naturalIdCacheAccessStrategy.afterInsert( naturalIdCacheKey, id );
+
+									if ( put && factory.getStatistics().isStatisticsEnabled() ) {
+										factory.getStatisticsImplementor()
+												.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
+									}
+								}
+							}
+					);
+
+					break;
+				}
+				case UPDATE: {
+					final Object[] previousNaturalIdValues = getNaturalIdSnapshot( id, persister );
+					final NaturalIdCacheKey previousCacheKey = new NaturalIdCacheKey( previousNaturalIdValues, persister, session );
+					final SoftLock removalLock = naturalIdCacheAccessStrategy.lockItem( previousCacheKey, null );
+					naturalIdCacheAccessStrategy.remove( previousCacheKey );
+
+					final SoftLock lock = naturalIdCacheAccessStrategy.lockItem( naturalIdCacheKey, null );
+					final boolean put = naturalIdCacheAccessStrategy.update( naturalIdCacheKey, id );
+					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
+						factory.getStatisticsImplementor()
+								.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
+					}
+
+					( (EventSource) session ).getActionQueue().registerProcess(
+							new AfterTransactionCompletionProcess() {
+								@Override
+								public void doAfterTransactionCompletion(boolean success, SessionImplementor session) {
+									naturalIdCacheAccessStrategy.unlockRegion( removalLock );
+									final boolean put = naturalIdCacheAccessStrategy.afterUpdate( naturalIdCacheKey, id, lock );
 
-		// evict from cache
-		naturalIdXrefDelegate.evictNaturalIdResolution( persister, entityEntry.getId(), naturalIdValues );
+									if ( put && factory.getStatistics().isStatisticsEnabled() ) {
+										factory.getStatisticsImplementor()
+												.naturalIdCachePut( naturalIdCacheAccessStrategy.getRegion().getName() );
+									}
+
+									naturalIdCacheAccessStrategy.unlockItem( naturalIdCacheKey, lock );
+								}
+							}
+					);
+
+					break;
+				}
+			}
+		}
+
+		@Override
+		public Object[] removeLocalNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] state) {
+			if ( !persister.hasNaturalIdentifier() ) {
+				// nothing to do
+				return null;
+			}
+
+			persister = locateProperPersister( persister );
+			final Object[] naturalIdValues = getNaturalIdValues( state, persister );
+
+			final Object[] localNaturalIdValues = naturalIdXrefDelegate.removeNaturalIdCrossReference( 
+					persister, 
+					id, 
+					naturalIdValues 
+			);
+
+			return localNaturalIdValues != null ? localNaturalIdValues : naturalIdValues;
+		}
+
+		@Override
+		public void removeSharedNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] naturalIdValues) {
+			if ( !persister.hasNaturalIdentifier() ) {
+				// nothing to do
+				return;
+			}
+
+			if ( ! persister.hasNaturalIdCache() ) {
+				// nothing to do
+				return;
+			}
+
+			// todo : couple of things wrong here:
+			//		1) should be using access strategy, not plain evict..
+			//		2) should prefer session-cached values if any (requires interaction from removeLocalNaturalIdCrossReference
+
+			persister = locateProperPersister( persister );
+			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
+			final NaturalIdCacheKey naturalIdCacheKey = new NaturalIdCacheKey( naturalIdValues, persister, session );
+			naturalIdCacheAccessStrategy.evict( naturalIdCacheKey );
+
+//			if ( sessionCachedNaturalIdValues != null
+//					&& !Arrays.equals( sessionCachedNaturalIdValues, deletedNaturalIdValues ) ) {
+//				final NaturalIdCacheKey sessionNaturalIdCacheKey = new NaturalIdCacheKey( sessionCachedNaturalIdValues, persister, session );
+//				naturalIdCacheAccessStrategy.evict( sessionNaturalIdCacheKey );
+//			}
+		}
+
+		@Override
+		public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk) {
+			return naturalIdXrefDelegate.findCachedNaturalId( locateProperPersister( persister ), pk );
+		}
+
+		@Override
+		public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues) {
+			return naturalIdXrefDelegate.findCachedNaturalIdResolution( locateProperPersister( persister ), naturalIdValues );
+		}
+
+		@Override
+		public Object[] extractNaturalIdValues(Object[] state, EntityPersister persister) {
+			final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
+			if ( state.length == naturalIdPropertyIndexes.length ) {
+				return state;
+			}
+
+			final Object[] naturalIdValues = new Object[naturalIdPropertyIndexes.length];
+			for ( int i = 0; i < naturalIdPropertyIndexes.length; i++ ) {
+				naturalIdValues[i] = state[naturalIdPropertyIndexes[i]];
+			}
+			return naturalIdValues;
+		}
+
+		@Override
+		public Object[] extractNaturalIdValues(Object entity, EntityPersister persister) {
+			if ( entity == null ) {
+				throw new AssertionFailure( "Entity from which to extract natural id value(s) cannot be null" );
+			}
+			if ( persister == null ) {
+				throw new AssertionFailure( "Persister to use in extracting natural id value(s) cannot be null" );
+			}
+
+			final int[] naturalIdentifierProperties = persister.getNaturalIdentifierProperties();
+			final Object[] naturalIdValues = new Object[naturalIdentifierProperties.length];
+
+			for ( int i = 0; i < naturalIdentifierProperties.length; i++ ) {
+				naturalIdValues[i] = persister.getPropertyValue( entity, naturalIdentifierProperties[i] );
+			}
+
+			return naturalIdValues;
+		}
+
+		@Override
+		public Collection<Serializable> getCachedPkResolutions(EntityPersister entityPersister) {
+			return naturalIdXrefDelegate.getCachedPkResolutions( entityPersister );
+		}
+
+		@Override
+		public void handleSynchronization(EntityPersister persister, Serializable pk, Object entity) {
+			if ( !persister.hasNaturalIdentifier() ) {
+				// nothing to do
+				return;
+			}
+
+			persister = locateProperPersister( persister );
+
+			final Object[] naturalIdValuesFromCurrentObjectState = extractNaturalIdValues( entity, persister );
+			final boolean changed = ! naturalIdXrefDelegate.sameAsCached(
+					persister,
+					pk,
+					naturalIdValuesFromCurrentObjectState
+			);
+
+			if ( changed ) {
+				final Object[] cachedNaturalIdValues = naturalIdXrefDelegate.findCachedNaturalId( persister, pk );
+				naturalIdXrefDelegate.cacheNaturalIdCrossReference( persister, pk, naturalIdValuesFromCurrentObjectState );
+				naturalIdXrefDelegate.stashInvalidNaturalIdReference( persister, cachedNaturalIdValues );
+
+				removeSharedNaturalIdCrossReference(
+						persister,
+						pk,
+						cachedNaturalIdValues
+				);
+			}
+		}
+
+		@Override
+		public void cleanupFromSynchronizations() {
+			naturalIdXrefDelegate.unStashInvalidNaturalIdReferences();
+		}
+	};
+
+	@Override
+	public NaturalIdHelper getNaturalIdHelper() {
+		return naturalIdHelper;
 	}
 
 	private Object[] getNaturalIdValues(Object[] state, EntityPersister persister) {
 		final int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
 		final Object[] naturalIdValues = new Object[naturalIdPropertyIndexes.length];
 
 		for ( int i = 0; i < naturalIdPropertyIndexes.length; i++ ) {
 			naturalIdValues[i] = state[naturalIdPropertyIndexes[i]];
 		}
 
 		return naturalIdValues;
 	}
-
-
-	@Override
-	public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk) {
-		return naturalIdXrefDelegate.findCachedNaturalId( persister, pk );
-	}
-
-	@Override
-	public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues) {
-		return naturalIdXrefDelegate.findCachedNaturalIdResolution( persister, naturalIdValues );
-	}
-
-	@Override
-	public void cacheNaturalIdResolution(
-			EntityPersister persister,
-			final Serializable pk,
-			Object[] naturalIdValues,
-			CachedNaturalIdValueSource valueSource) {
-		naturalIdXrefDelegate.cacheNaturalIdResolution( persister, pk, naturalIdValues, valueSource );
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
index 064c1944be..f4e3d6ce59 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
@@ -1,342 +1,367 @@
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
 package org.hibernate.engine.internal;
 
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PostLoadEventListener;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.event.spi.PreLoadEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.proxy.HibernateProxy;
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
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, TwoPhaseLoad.class.getName() );
 
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
 
 		Object version = Versioning.getVersion( values, persister );
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
 
 		if ( LOG.isTraceEnabled() && version != null ) {
 			String versionStr = persister.isVersioned()
 					? persister.getVersionType().toLoggableString( version, session.getFactory() )
 					: "null";
 			LOG.tracev( "Version: {0}", versionStr );
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
-
-		//TODO: Should this be an InitializeEntityEventListener??? (watch out for performance!)
-
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
-		EntityEntry entityEntry = persistenceContext.getEntry(entity);
+		final EntityEntry entityEntry = persistenceContext.getEntry(entity);
+		final EntityPersister persister = entityEntry.getPersister();
+		final Serializable id = entityEntry.getId();
+
+//		persistenceContext.getNaturalIdHelper().startingLoad( persister, id );
+//		try {
+			doInitializeEntity( entity, entityEntry, readOnly, session, preLoadEvent, postLoadEvent );
+//		}
+//		finally {
+//			persistenceContext.getNaturalIdHelper().endingLoad( persister, id );
+//		}
+	}
+
+	private static void doInitializeEntity(
+			final Object entity,
+			final EntityEntry entityEntry,
+			final boolean readOnly,
+			final SessionImplementor session,
+			final PreLoadEvent preLoadEvent,
+			final PostLoadEvent postLoadEvent) throws HibernateException {
 		if ( entityEntry == null ) {
 			throw new AssertionFailure( "possible non-threadsafe access to the session" );
 		}
+
+		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		EntityPersister persister = entityEntry.getPersister();
 		Serializable id = entityEntry.getId();
 		Object[] hydratedState = entityEntry.getLoadedState();
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
-				"Resolving associations for %s",
-				MessageHelper.infoString( persister, id, session.getFactory() )
-					);
+					"Resolving associations for %s",
+					MessageHelper.infoString( persister, id, session.getFactory() )
+			);
 		}
 
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
 
 		persister.setPropertyValues( entity, hydratedState );
 
 		final SessionFactoryImplementor factory = session.getFactory();
 		if ( persister.hasCache() && session.getCacheMode().isPutEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf(
-					"Adding entity to second-level cache: %s",
-					MessageHelper.infoString( persister, id, session.getFactory() )
-						);
+						"Adding entity to second-level cache: %s",
+						MessageHelper.infoString( persister, id, session.getFactory() )
+				);
 			}
 
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
 
+		if ( persister.hasNaturalIdentifier() ) {
+			persistenceContext.getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(
+					persister,
+					id,
+					persistenceContext.getNaturalIdHelper().extractNaturalIdValues( hydratedState, persister )
+			);
+		}
+
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
-			);
+		);
 
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
-
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java
index ccdd884910..f0b202a58d 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java
@@ -1,465 +1,441 @@
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
 
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.Session;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * We need an entry to tell us all about the current state of an object with respect to its persistent state
  * 
  * @author Gavin King
  */
 public final class EntityEntry implements Serializable {
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
 	private final String tenantId;
 	private final String entityName;
 	private transient EntityKey cachedEntityKey; // cached EntityKey (lazy-initialized)
 	private boolean isBeingReplicated;
 	private boolean loadedWithLazyPropertiesUnfetched; //NOTE: this is not updated when properties are fetched lazily!
 	private final transient Object rowId;
 	private final transient PersistenceContext persistenceContext;
 
 	public EntityEntry(
 			final Status status,
 			final Object[] loadedState,
 			final Object rowId,
 			final Serializable id,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final EntityPersister persister,
 			final EntityMode entityMode,
 			final String tenantId,
 			final boolean disableVersionIncrement,
 			final boolean lazyPropertiesAreUnfetched,
 			final PersistenceContext persistenceContext) {
 		this.status = status;
 		this.previousStatus = null;
 		// only retain loaded state if the status is not Status.READ_ONLY
 		if ( status != Status.READ_ONLY ) {
 			this.loadedState = loadedState;
 		}
 		this.id=id;
 		this.rowId=rowId;
 		this.existsInDatabase=existsInDatabase;
 		this.version=version;
 		this.lockMode=lockMode;
 		this.isBeingReplicated=disableVersionIncrement;
 		this.loadedWithLazyPropertiesUnfetched = lazyPropertiesAreUnfetched;
 		this.persister=persister;
 		this.entityMode = entityMode;
 		this.tenantId = tenantId;
 		this.entityName = persister == null ? null : persister.getEntityName();
 		this.persistenceContext = persistenceContext;
 	}
 
 	/**
 	 * This for is used during custom deserialization handling
 	 */
 	@SuppressWarnings( {"JavaDoc"})
 	private EntityEntry(
 			final SessionFactoryImplementor factory,
 			final String entityName,
 			final Serializable id,
 			final EntityMode entityMode,
 			final String tenantId,
 			final Status status,
 			final Status previousStatus,
 			final Object[] loadedState,
 	        final Object[] deletedState,
 			final Object version,
 			final LockMode lockMode,
 			final boolean existsInDatabase,
 			final boolean isBeingReplicated,
 			final boolean loadedWithLazyPropertiesUnfetched,
 			final PersistenceContext persistenceContext) {
 		this.entityName = entityName;
 		this.persister = ( factory == null ? null : factory.getEntityPersister( entityName ) );
 		this.id = id;
 		this.entityMode = entityMode;
 		this.tenantId = tenantId;
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
 		this.persistenceContext = persistenceContext;
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
 			cachedEntityKey = new EntityKey( getId(), getPersister(), tenantId );
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
 		setLockMode( LockMode.WRITE );
 
 		if ( getPersister().isVersioned() ) {
 			this.version = nextVersion;
 			getPersister().setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
 		}
 
 		if ( getPersister().getInstrumentationMetadata().isInstrumented() ) {
 			final FieldInterceptor interceptor = getPersister().getInstrumentationMetadata().extractInterceptor( entity );
 			if ( interceptor != null ) {
 				interceptor.clearDirty();
 			}
 		}
 		persistenceContext.getSession()
 				.getFactory()
 				.getCustomEntityDirtinessStrategy()
 				.resetDirty( entity, getPersister(), (Session) persistenceContext.getSession() );
-
-		notifyLoadedStateUpdated( updatedState );
 	}
 
 	/**
 	 * After actually deleting a row, record the fact that the instance no longer
 	 * exists in the database
 	 */
 	public void postDelete() {
 		previousStatus = status;
 		status = Status.GONE;
 		existsInDatabase = false;
-		
-		notifyLoadedStateDeleted( deletedState );
 	}
 	
 	/**
 	 * After actually inserting a row, record the fact that the instance exists on the 
 	 * database (needed for identity-column key generation)
 	 */
 	public void postInsert(Object[] insertedState) {
 		existsInDatabase = true;
-		
-		notifyLoadedStateInserted( insertedState );
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
 
 	/**
 	 * Not sure this is the best method name, but the general idea here is to return {@code true} if the entity can
 	 * possibly be dirty.  This can only be the case if it is in a modifiable state (not read-only/deleted) and it
 	 * either has mutable properties or field-interception is not telling us it is dirty.  Clear as mud? :/
 	 *
 	 * A name like canPossiblyBeDirty might be better
 	 *
 	 * @param entity The entity to test
 	 *
 	 * @return {@code true} indicates that the entity could possibly be dirty and that dirty check
 	 * should happen; {@code false} indicates there is no way the entity can be dirty
 	 */
 	public boolean requiresDirtyCheck(Object entity) {
 		return isModifiableEntity()
 				&& ( getPersister().hasMutableProperties() || ! isUnequivocallyNonDirty( entity ) );
 	}
 
 	@SuppressWarnings( {"SimplifiableIfStatement"})
 	private boolean isUnequivocallyNonDirty(Object entity) {
 		if ( getPersister().getInstrumentationMetadata().isInstrumented() ) {
 			// the entity must be instrumented (otherwise we cant check dirty flag) and the dirty flag is false
 			return ! getPersister().getInstrumentationMetadata().extractInterceptor( entity ).isDirty();
 		}
 
 		final CustomEntityDirtinessStrategy customEntityDirtinessStrategy =
 				persistenceContext.getSession().getFactory().getCustomEntityDirtinessStrategy();
 		if ( customEntityDirtinessStrategy.canDirtyCheck( entity, getPersister(), (Session) persistenceContext.getSession() ) ) {
 			return ! customEntityDirtinessStrategy.isDirty( entity, getPersister(), (Session) persistenceContext.getSession() );
 		}
 
 		return false;
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
 		return getPersister().isMutable()
 				&& status != Status.READ_ONLY
 				&& ! ( status == Status.DELETED && previousStatus == Status.READ_ONLY );
 	}
 
 	public void forceLocked(Object entity, Object nextVersion) {
 		version = nextVersion;
 		loadedState[ persister.getVersionProperty() ] = version;
 		//noinspection deprecation
 		setLockMode( LockMode.FORCE );  // TODO:  use LockMode.PESSIMISTIC_FORCE_INCREMENT
 		persister.setPropertyValue( entity, getPersister().getVersionProperty(), nextVersion );
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
 			loadedState = getPersister().getPropertyValues( entity );
-			notifyLoadedStateUpdated( loadedState );
+			persistenceContext.getNaturalIdHelper().manageLocalNaturalIdCrossReference(
+					persister,
+					id,
+					loadedState,
+					null,
+					CachedNaturalIdValueSource.LOAD
+			);
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
 
-	private void notifyLoadedStateUpdated(Object[] state) {
-		if ( persistenceContext == null ) {
-			throw new HibernateException( "PersistenceContext was null on attempt to update loaded state" );
-		}
-
-		persistenceContext.entityStateUpdatedNotification( this, state );
-	}
-	
-	private void notifyLoadedStateInserted(Object[] state) {
-		if ( persistenceContext == null ) {
-			throw new HibernateException( "PersistenceContext was null on attempt to insert loaded state" );
-		}
-
-		persistenceContext.entityStateInsertedNotification( this, state );
-	}
-
-	private void notifyLoadedStateDeleted(Object[] deletedState) {
-		if ( persistenceContext == null ) {
-			throw new HibernateException( "PersistenceContext was null on attempt to delete loaded state" );
-		}
-
-		persistenceContext.entityStateDeletedNotification( this, deletedState );
-	}
-
 	/**
 	 * Custom serialization routine used during serialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param oos The stream to which we should write the serial data.
 	 *
 	 * @throws IOException If a stream error occurs
 	 */
 	public void serialize(ObjectOutputStream oos) throws IOException {
 		oos.writeObject( entityName );
 		oos.writeObject( id );
 		oos.writeObject( entityMode.toString() );
 		oos.writeObject( tenantId );
 		oos.writeObject( status.name() );
 		oos.writeObject( (previousStatus == null ? "" : previousStatus.name()) );
 		// todo : potentially look at optimizing these two arrays
 		oos.writeObject( loadedState );
 		oos.writeObject( deletedState );
 		oos.writeObject( version );
 		oos.writeObject( lockMode.toString() );
 		oos.writeBoolean( existsInDatabase );
 		oos.writeBoolean( isBeingReplicated );
 		oos.writeBoolean( loadedWithLazyPropertiesUnfetched );
 	}
 
 	/**
 	 * Custom deserialization routine used during deserialization of a
 	 * Session/PersistenceContext for increased performance.
 	 *
 	 * @param ois The stream from which to read the entry.
 	 * @param persistenceContext The context being deserialized.
 	 *
 	 * @return The deserialized EntityEntry
 	 *
 	 * @throws IOException If a stream error occurs
 	 * @throws ClassNotFoundException If any of the classes declared in the stream
 	 * cannot be found
 	 */
 	public static EntityEntry deserialize(
 			ObjectInputStream ois,
 	        PersistenceContext persistenceContext) throws IOException, ClassNotFoundException {
 		String previousStatusString;
 		return new EntityEntry(
 				// this complexity comes from non-flushed changes, should really look at how that reattaches entries
 				( persistenceContext.getSession() == null ? null : persistenceContext.getSession().getFactory() ),
 		        (String) ois.readObject(),
 				( Serializable ) ois.readObject(),
 	            EntityMode.parse( (String) ois.readObject() ),
 				(String) ois.readObject(),
 				Status.valueOf( (String) ois.readObject() ),
 				( ( previousStatusString = ( String ) ois.readObject() ).length() == 0 ?
 							null :
 							Status.valueOf( previousStatusString )
 				),
 	            ( Object[] ) ois.readObject(),
 	            ( Object[] ) ois.readObject(),
 	            ois.readObject(),
 	            LockMode.valueOf( (String) ois.readObject() ),
 	            ois.readBoolean(),
 	            ois.readBoolean(),
 	            ois.readBoolean(),
 				persistenceContext
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java
index f59a4540a3..fdec5216ed 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java
@@ -1,716 +1,841 @@
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
 package org.hibernate.engine.spi;
 
 import java.io.Serializable;
+import java.util.Collection;
 import java.util.HashSet;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.loading.internal.LoadContexts;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Represents the state of "stuff" Hibernate is tracking, including (not exhaustive):
  * <ul>
  *     <li>entities</li>
  *     <li>collections</li>
  *     <li>snapshots</li>
  *     <li>proxies</li>
  * </ul>
  * <p/>
  * Often referred to as the "first level cache".
  * 
  * @author Gavin King
  * @author Steve Ebersole
  */
 @SuppressWarnings( {"JavaDoc"})
 public interface PersistenceContext {
 	
 	@SuppressWarnings( {"UnusedDeclaration"})
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
 	 *
 	 * @param key The collection key under which to add the collection
 	 * @param collection The collection to add
 	 */
 	public void addUnownedCollection(CollectionKey key, PersistentCollection collection);
 
 	/**
 	 * Take ownership of a previously unowned collection, if one.  This method returns {@code null} if no such
 	 * collection was previous added () or was previously removed.
 	 * <p/>
 	 * This should indicate the owner is being loaded and we are ready to "link" them.
 	 *
 	 * @param key The collection key for which to locate a collection collection
 	 *
 	 * @return The unowned collection, or {@code null}
 	 */
 	public PersistentCollection useUnownedCollection(CollectionKey key);
 
 	/**
 	 * Get the {@link BatchFetchQueue}, instantiating one if necessary.
 	 *
 	 * @return The batch fetch queue in effect for this persistence context
 	 */
 	public BatchFetchQueue getBatchFetchQueue();
 	
 	/**
 	 * Clear the state of the persistence context
 	 */
 	public void clear();
 
 	/**
 	 * @return false if we know for certain that all the entities are read-only
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean hasNonReadOnlyEntities();
 
 	/**
 	 * Set the status of an entry
 	 *
 	 * @param entry The entry for which to set the status
 	 * @param status The new status
 	 */
 	public void setEntryStatus(EntityEntry entry, Status status);
 
 	/**
 	 * Called after transactions end
 	 */
 	public void afterTransactionCompletion();
 
 	/**
 	 * Get the current state of the entity as known to the underlying database, or null if there is no
 	 * corresponding row
 	 *
 	 * @param id The identifier of the entity for which to grab a snapshot
 	 * @param persister The persister of the entity.
 	 *
 	 * @return The entity's (non-cached) snapshot
 	 *
 	 * @see #getCachedDatabaseSnapshot
 	 */
 	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister);
 
 	/**
 	 * Get the current database state of the entity, using the cached state snapshot if one is available.
 	 *
 	 * @param key The entity key
 	 *
 	 * @return The entity's (non-cached) snapshot
 	 */
 	public Object[] getCachedDatabaseSnapshot(EntityKey key);
 
 	/**
 	 * Get the values of the natural id fields as known to the underlying database, or null if the entity has no
 	 * natural id or there is no corresponding row.
 	 *
 	 * @param id The identifier of the entity for which to grab a snapshot
 	 * @param persister The persister of the entity.
 	 *
 	 * @return The current (non-cached) snapshot of the entity's natural id state.
 	 */
 	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister);
 
 	/**
 	 * Add a canonical mapping from entity key to entity instance
 	 *
 	 * @param key The key under which to add an entity
 	 * @param entity The entity instance to add
 	 */
 	public void addEntity(EntityKey key, Object entity);
 
 	/**
 	 * Get the entity instance associated with the given key
 	 *
 	 * @param key The key under which to look for an entity
 	 *
 	 * @return The matching entity, or {@code null}
 	 */
 	public Object getEntity(EntityKey key);
 
 	/**
 	 * Is there an entity with the given key in the persistence context
 	 *
 	 * @param key The key under which to look for an entity
 	 *
 	 * @return {@code true} indicates an entity was found; otherwise {@code false}
 	 */
 	public boolean containsEntity(EntityKey key);
 
 	/**
 	 * Remove an entity.  Also clears up all other state associated with the entity aside from the {@link EntityEntry}
 	 *
 	 * @param key The key whose matching entity should be removed
 	 *
 	 * @return The matching entity
 	 */
 	public Object removeEntity(EntityKey key);
 
 	/**
 	 * Add an entity to the cache by unique key
 	 *
 	 * @param euk The unique (non-primary) key under which to add an entity
 	 * @param entity The entity instance
 	 */
 	public void addEntity(EntityUniqueKey euk, Object entity);
 
 	/**
 	 * Get an entity cached by unique key
 	 *
 	 * @param euk The unique (non-primary) key under which to look for an entity
 	 *
 	 * @return The located entity
 	 */
 	public Object getEntity(EntityUniqueKey euk);
 
 	/**
 	 * Retrieve the {@link EntityEntry} representation of the given entity.
 	 *
 	 * @param entity The entity instance for which to locate the corresponding entry
 	 * @return The entry
 	 */
 	public EntityEntry getEntry(Object entity);
 
 	/**
 	 * Remove an entity entry from the session cache
 	 *
 	 * @param entity The entity instance for which to remove the corresponding entry
 	 * @return The matching entry
 	 */
 	public EntityEntry removeEntry(Object entity);
 
 	/**
 	 * Is there an {@link EntityEntry} registration for this entity instance?
 	 *
 	 * @param entity The entity instance for which to check for an entry
 	 *
 	 * @return {@code true} indicates a matching entry was found.
 	 */
 	public boolean isEntryFor(Object entity);
 
 	/**
 	 * Get the collection entry for a persistent collection
 	 *
 	 * @param coll The persistent collection instance for which to locate the collection entry
 	 *
 	 * @return The matching collection entry
 	 */
 	public CollectionEntry getCollectionEntry(PersistentCollection coll);
 
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
 			boolean lazyPropertiesAreUnfetched);
 
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
 			boolean lazyPropertiesAreUnfetched);
 
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
 	@SuppressWarnings( {"UnusedDeclaration"})
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
 	 * <p/>
 	 * To determine the default read-only/modifiable setting used for entities and proxies that are loaded into the
 	 * session use {@link org.hibernate.Session#isDefaultReadOnly}
 	 *
 	 * @param entityOrProxy an entity or proxy
 	 *
 	 * @return {@code true} if the object is read-only; otherwise {@code false} to indicate that the object is
 	 * modifiable.
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
 	 * @param entityOrProxy an entity or proxy
 	 * @param readOnly if {@code true}, the entity or proxy is made read-only; otherwise, the entity or proxy is made
 	 * modifiable.
 	 *
 	 * @see org.hibernate.Session#setDefaultReadOnly
 	 * @see org.hibernate.Session#setReadOnly
 	 * @see org.hibernate.Query#setReadOnly
 	 */
 	public void setReadOnly(Object entityOrProxy, boolean readOnly);
 
 	void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId);
 
 	/**
 	 * Add a child/parent relation to cache for cascading op
 	 *
 	 * @param child The child of the relationship
 	 * @param parent The parent of the relationship
 	 */
 	public void addChildParent(Object child, Object parent);
 
 	/**
 	 * Remove child/parent relation from cache
 	 *
 	 * @param child The child to be removed.
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
 
 	/**
-	 * Callback used to signal that loaded entity state has changed.
-	 *
-	 * @param entityEntry The entry of the entity that has changed.
-	 * @param state The new state.
-	 */
-	public void entityStateUpdatedNotification(EntityEntry entityEntry, Object[] state);
-
-	/**
-	 * Callback used to signal that entity state has been inserted.
-	 *
-	 * @param entityEntry The entry of the inserted entity
-	 * @param state The new state
-	 */
-	public void entityStateInsertedNotification(EntityEntry entityEntry, Object[] state);
-
-	/**
-	 * Callback used to signal that entity state has been deleted.
-	 *
-	 * @param entityEntry The entry of the inserted entity
-	 * @param deletedState The state of the entity at the time of deletion
-	 */
-	public void entityStateDeletedNotification(EntityEntry entityEntry, Object[] deletedState);
-
-	public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk);
-
-	public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalId);
-
-	public void cacheNaturalIdResolution(EntityPersister persister, Serializable pk, Object[] naturalId, CachedNaturalIdValueSource valueSource);
-	
+	 * Provides centralized access to natural-id-related functionality.
+	 */
+	public static interface NaturalIdHelper {
+		public static final Serializable INVALID_NATURAL_ID_REFERENCE = new Serializable() {};
+
+		/**
+		 * Given an array of "full entity state", extract the portions that represent the natural id
+		 * 
+		 * @param state The attribute state array
+		 * @param persister The persister representing the entity type.
+		 * 
+		 * @return The extracted natural id values
+		 */
+		public Object[] extractNaturalIdValues(Object[] state, EntityPersister persister);
+
+		/**
+		 * Given an entity instance, extract the values that represent the natural id
+		 *
+		 * @param entity The entity instance
+		 * @param persister The persister representing the entity type.
+		 *
+		 * @return The extracted natural id values
+		 */
+		public Object[] extractNaturalIdValues(Object entity, EntityPersister persister);
+
+		/**
+		 * Performs processing related to creating natural-id cross-reference entries on load.
+		 * Handles both the local (transactional) and shared (second-level) caches.
+		 *
+		 * @param persister The persister representing the entity type.
+		 * @param id The primary key value
+		 * @param naturalIdValues The natural id values
+		 */
+		public void cacheNaturalIdCrossReferenceFromLoad(
+				EntityPersister persister, 
+				Serializable id, 
+				Object[] naturalIdValues);
+
+		/**
+		 * Creates necessary local cross-reference entries.
+		 *
+		 * @param persister The persister representing the entity type.
+		 * @param id The primary key value
+		 * @param state Generally the "full entity state array", though could also be the natural id values array
+		 * @param previousState Generally the "full entity state array", though could also be the natural id values array.  
+		 * 		Specifically represents the previous values on update, and so is only used with {@link CachedNaturalIdValueSource#UPDATE}
+		 * @param source Enumeration representing how these values are coming into cache.
+		 */
+		public void manageLocalNaturalIdCrossReference(
+				EntityPersister persister,
+				Serializable id,
+				Object[] state,
+				Object[] previousState,
+				CachedNaturalIdValueSource source);
+
+		/**
+		 * Cleans up local cross-reference entries.
+		 * 
+		 * @param persister The persister representing the entity type.
+		 * @param id The primary key value
+		 * @param state Generally the "full entity state array", though could also be the natural id values array
+		 * 
+		 * @return The local cached natural id values (could be different from given values).
+		 */
+		public Object[] removeLocalNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] state);
+
+		/**
+		 * Creates necessary shared (second level cache) cross-reference entries.
+		 *
+		 * @param persister The persister representing the entity type.
+		 * @param id The primary key value
+		 * @param state Generally the "full entity state array", though could also be the natural id values array
+		 * @param previousState Generally the "full entity state array", though could also be the natural id values array.  
+		 * 		Specifically represents the previous values on update, and so is only used with {@link CachedNaturalIdValueSource#UPDATE}
+		 * @param source Enumeration representing how these values are coming into cache.
+		 */
+		public void manageSharedNaturalIdCrossReference(
+				EntityPersister persister,
+				Serializable id,
+				Object[] state,
+				Object[] previousState,
+				CachedNaturalIdValueSource source);
+
+		/**
+		 * Cleans up local cross-reference entries.
+		 *
+		 * @param persister The persister representing the entity type.
+		 * @param id The primary key value
+		 * @param naturalIdValues The natural id values array
+		 */
+		public void removeSharedNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] naturalIdValues);
+
+		/**
+		 * Given a persister and primary key, find the corresponding cross-referenced natural id values.
+		 *
+		 * @param persister The persister representing the entity type.
+		 * @param pk The primary key value
+		 * 
+		 * @return The cross-referenced natural-id values, or {@code null}
+		 */
+		public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk);
+
+		/**
+		 * Given a persister and natural-id values, find the corresponding cross-referenced primary key. Will return
+		 * {@link PersistenceContext.NaturalIdHelper#INVALID_NATURAL_ID_REFERENCE} if the given natural ids are known to
+		 * be invalid.
+		 *
+		 * @param persister The persister representing the entity type.
+		 * @param naturalIdValues The natural id value(s)
+		 *
+		 * @return The corresponding cross-referenced primary key, 
+		 * 		{@link PersistenceContext.NaturalIdHelper#INVALID_NATURAL_ID_REFERENCE},
+		 * 		or {@code null}. 
+		 */
+		public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues);
+
+		/**
+		 * Find all the locally cached primary key cross-reference entries for the given persister.
+		 *
+		 * @param persister The persister representing the entity type.
+		 * 
+		 * @return The primary keys
+		 */
+		public Collection<Serializable> getCachedPkResolutions(EntityPersister persister);
+
+		/**
+		 * Part of the "load synchronization process".  Responsible for maintaining cross-reference entries
+		 * when natural-id values were found to have changed.  Also responsible for tracking the old values 
+		 * as no longer valid until the next flush because otherwise going to the database would just re-pull
+		 * the old values as valid.  In this last responsibility, {@link #cleanupFromSynchronizations} is
+		 * the inverse process called after flush to clean up those entries.
+		 *
+		 * @param persister The persister representing the entity type.
+		 * @param pk The primary key
+		 * @param entity The entity instance
+		 * 
+		 * @see #cleanupFromSynchronizations
+		 */
+		public void handleSynchronization(EntityPersister persister, Serializable pk, Object entity);
+
+		/**
+		 * The clean up process of {@link #handleSynchronization}.  Responsible for cleaning up the tracking
+		 * of old values as no longer valid.
+		 */
+		public void cleanupFromSynchronizations();
+	}
+
+	/**
+	 * Access to the natural-id helper for this persistence context
+	 * 
+	 * @return This persistence context's natural-id helper
+	 */
+	public NaturalIdHelper getNaturalIdHelper();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
index ae2445d85c..c40942ea64 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
@@ -1,675 +1,666 @@
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
 import java.util.Arrays;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.action.internal.DelayedPostInsertIdentifier;
 import org.hibernate.action.internal.EntityUpdateAction;
 import org.hibernate.engine.internal.Nullability;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.FlushEntityEvent;
 import org.hibernate.event.spi.FlushEntityEventListener;
 import org.hibernate.internal.CoreMessageLogger;
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
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        DefaultFlushEntityEventListener.class.getName());
 
 	/**
 	 * make sure user didn't mangle the id
 	 */
 	public void checkId(Object object, EntityPersister persister, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
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
 			if ( !persister.getIdentifierType().isEqual( id, oid, session.getFactory() ) ) {
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
 	        SessionImplementor session) {
 		if ( persister.hasNaturalIdentifier() && entry.getStatus() != Status.READ_ONLY ) {
 			if ( ! persister.getEntityMetamodel().hasImmutableNaturalId() ) {
 				// SHORT-CUT: if the natural id is mutable (!immutable), no need to do the below checks
 				// EARLY EXIT!!!
 				return;
 			}
 
 			final int[] naturalIdentifierPropertiesIndexes = persister.getNaturalIdentifierProperties();
 			final Type[] propertyTypes = persister.getPropertyTypes();
 			final boolean[] propertyUpdateability = persister.getPropertyUpdateability();
 
 			final Object[] snapshot = loaded == null
 					? session.getPersistenceContext().getNaturalIdSnapshot( entry.getId(), persister )
-					: extractNaturalIdValues( loaded, naturalIdentifierPropertiesIndexes );
+					: session.getPersistenceContext().getNaturalIdHelper().extractNaturalIdValues( loaded, persister );
 
 			for ( int i=0; i<naturalIdentifierPropertiesIndexes.length; i++ ) {
 				final int naturalIdentifierPropertyIndex = naturalIdentifierPropertiesIndexes[i];
 				if ( propertyUpdateability[ naturalIdentifierPropertyIndex ] ) {
 					// if the given natural id property is updatable (mutable), there is nothing to check
 					continue;
 				}
 
 				final Type propertyType = propertyTypes[naturalIdentifierPropertyIndex];
 				if ( ! propertyType.isEqual( current[naturalIdentifierPropertyIndex], snapshot[i] ) ) {
 					throw new HibernateException(
 							String.format(
 									"An immutable natural identifier of entity %s was altered from %s to %s",
 									persister.getEntityName(),
 									propertyTypes[naturalIdentifierPropertyIndex].toLoggableString(
 											snapshot[i],
 											session.getFactory()
 									),
 									propertyTypes[naturalIdentifierPropertyIndex].toLoggableString(
 											current[naturalIdentifierPropertyIndex],
 											session.getFactory()
 									)
 							)
 					);
 			   }
 			}
 		}
 	}
 
-	public Object[] extractNaturalIdValues(Object[] entitySnapshot, int[] naturalIdPropertyIndexes) {
-		final Object[] naturalIdSnapshotSubSet = new Object[ naturalIdPropertyIndexes.length ];
-		for ( int i = 0; i < naturalIdPropertyIndexes.length; i++ ) {
-			naturalIdSnapshotSubSet[i] = entitySnapshot[ naturalIdPropertyIndexes[i] ];
-		}
-		return naturalIdSnapshotSubSet;
-	}
-
-
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
 		final Type[] types = persister.getPropertyTypes();
 
 		final boolean mightBeDirty = entry.requiresDirtyCheck(entity);
 
 		final Object[] values = getValues( entity, entry, mightBeDirty, session );
 
 		event.setPropertyValues(values);
 
 		//TODO: avoid this for non-new instances where mightBeDirty==false
 		boolean substitute = wrapCollections( session, persister, types, values);
 
 		if ( isUpdateNecessary( event, mightBeDirty ) ) {
 			substitute = scheduleUpdate( event ) || substitute;
 		}
 
 		if ( status != Status.DELETED ) {
 			// now update the object .. has to be outside the main if block above (because of collections)
 			if (substitute) persister.setPropertyValues( entity, values );
 
 			// Search for collections by reachability, updating their role.
 			// We don't want to touch collections reachable from a deleted object
 			if ( persister.hasCollections() ) {
 				new FlushVisitor(session, entity).processEntityPropertyValues(values, types);
 			}
 		}
 
 	}
 
 	private Object[] getValues(Object entity, EntityEntry entry, boolean mightBeDirty, SessionImplementor session) {
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
 			checkId( entity, persister, entry.getId(), session );
 
 			// grab its current state
 			values = persister.getPropertyValues( entity );
 
 			checkNaturalId( persister, entry, values, loadedState, session );
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
 				if ( event.getEntityEntry().getPersister().getInstrumentationMetadata().isInstrumented() ) {
 					event.getEntityEntry()
 							.getPersister()
 							.getInstrumentationMetadata()
 							.extractInterceptor( event.getEntity() )
 							.clearDirty();
 				}
 				event.getSession()
 						.getFactory()
 						.getCustomEntityDirtinessStrategy()
 						.resetDirty( event.getEntity(), event.getEntityEntry().getPersister(), event.getSession() );
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
 		final EntityPersister persister = entry.getPersister();
 		final Object[] values = event.getPropertyValues();
 
 		if ( LOG.isTraceEnabled() ) {
 			if ( status == Status.DELETED ) {
 				if ( !persister.isMutable() ) {
 					LOG.tracev( "Updating immutable, deleted entity: {0}",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() ) );
 				}
 				else if ( !entry.isModifiableEntity() )
 					LOG.tracev( "Updating non-modifiable, deleted entity: {0}",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() ) );
 				else
 					LOG.tracev( "Updating deleted entity: ",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() ) );
 			}
 			else
 				LOG.tracev( "Updating entity: {0}",
 						MessageHelper.infoString( persister, entry.getId(), session.getFactory() ) );
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
 								persister.getPropertyValues( entity ) :
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
 	protected void dirtyCheck(final FlushEntityEvent event) throws HibernateException {
 
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
 
 		if ( dirtyProperties == null ) {
 			// see if the custom dirtiness strategy can tell us...
 			class DirtyCheckContextImpl implements CustomEntityDirtinessStrategy.DirtyCheckContext {
 				int[] found = null;
 				@Override
 				public void doDirtyChecking(CustomEntityDirtinessStrategy.AttributeChecker attributeChecker) {
 					found = new DirtyCheckAttributeInfoImpl( event ).visitAttributes( attributeChecker );
 					if ( found != null && found.length == 0 ) {
 						found = null;
 					}
 				}
 			}
 			DirtyCheckContextImpl context = new DirtyCheckContextImpl();
 			session.getFactory().getCustomEntityDirtinessStrategy().findDirty(
 					entity,
 					persister,
 					(Session) session,
 					context
 			);
 			dirtyProperties = context.found;
 		}
 
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
 				final Object[] currentState = persister.getPropertyValues( event.getEntity() );
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
 
 	private class DirtyCheckAttributeInfoImpl implements CustomEntityDirtinessStrategy.AttributeInformation {
 		private final FlushEntityEvent event;
 		private final EntityPersister persister;
 		private final int numberOfAttributes;
 		private int index = 0;
 
 		private DirtyCheckAttributeInfoImpl(FlushEntityEvent event) {
 			this.event = event;
 			this.persister = event.getEntityEntry().getPersister();
 			this.numberOfAttributes = persister.getPropertyNames().length;
 		}
 
 		@Override
 		public EntityPersister getContainingPersister() {
 			return persister;
 		}
 
 		@Override
 		public int getAttributeIndex() {
 			return index;
 		}
 
 		@Override
 		public String getName() {
 			return persister.getPropertyNames()[ index ];
 		}
 
 		@Override
 		public Type getType() {
 			return persister.getPropertyTypes()[ index ];
 		}
 
 		@Override
 		public Object getCurrentValue() {
 			return event.getPropertyValues()[ index ];
 		}
 
 		Object[] databaseSnapshot;
 
 		@Override
 		public Object getLoadedValue() {
 			if ( databaseSnapshot == null ) {
 				databaseSnapshot = getDatabaseSnapshot( event.getSession(), persister, event.getEntityEntry().getId() );
 			}
 			return databaseSnapshot[ index ];
 		}
 
 		public int[] visitAttributes(CustomEntityDirtinessStrategy.AttributeChecker attributeChecker) {
 			databaseSnapshot = null;
 			index = 0;
 
 			final int[] indexes = new int[ numberOfAttributes ];
 			int count = 0;
 			for ( ; index < numberOfAttributes; index++ ) {
 				if ( attributeChecker.isDirty( this ) ) {
 					indexes[ count++ ] = index;
 				}
 			}
 			return Arrays.copyOf( indexes, count );
 		}
 	}
 
 	private void logDirtyProperties(Serializable id, int[] dirtyProperties, EntityPersister persister) {
 		if ( LOG.isTraceEnabled() && dirtyProperties != null && dirtyProperties.length > 0 ) {
 			final String[] allPropertyNames = persister.getPropertyNames();
 			final String[] dirtyPropertyNames = new String[ dirtyProperties.length ];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				dirtyPropertyNames[i] = allPropertyNames[ dirtyProperties[i]];
 			}
 			LOG.tracev( "Found dirty properties [{0}] : {1}",
 					MessageHelper.infoString( persister.getEntityName(), id ),
 					dirtyPropertyNames );
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
 		final EntityKey entityKey = session.generateEntityKey( id, persister );
 		return session.getPersistenceContext().getCachedDatabaseSnapshot( entityKey );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
index e6432fdf7a..6fdaa32ee3 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
@@ -1,671 +1,662 @@
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
 package org.hibernate.event.internal;
 
 import java.io.Serializable;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.SoftLock;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.internal.Versioning;
-import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.LoadEvent;
 import org.hibernate.event.spi.LoadEventListener;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PostLoadEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
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
 			LOG.unableToLoadCommand( e );
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
 		dependentIdType.setPropertyValues( dependent, new Object[] {parent}, dependentPersister.getEntityMode() );
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Loading entity: {0}",
 					MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() ) );
 		}
 
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
 		LOG.trace( "Entity proxy found in session cache" );
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
 			LOG.trace( "Entity found in session cache" );
 			if ( options.isCheckDeleted() ) {
 				EntityEntry entry = persistenceContext.getEntry( existing );
 				Status status = entry.getStatus();
 				if ( status == Status.DELETED || status == Status.GONE ) {
 					return null;
 				}
 			}
 			return existing;
 		}
 		LOG.trace( "Creating new proxy for entity" );
 		// return new uninitialized proxy
 		Object proxy = persister.createProxy( event.getEntityId(), event.getSession() );
 		persistenceContext.getBatchFetchQueue().addBatchLoadableEntityKey( keyToLoad );
 		persistenceContext.addProxy( keyToLoad, proxy );
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
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( traceEnabled ) LOG.tracev( "Attempting to resolve: {0}",
 					MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() ) );
 
 		Object entity = loadFromSessionCache( event, keyToLoad, options );
 		if ( entity == REMOVED_ENTITY_MARKER ) {
 			LOG.debug( "Load request found matching entity in context, but it is scheduled for removal; returning null" );
 			return null;
 		}
 		if ( entity == INCONSISTENT_RTN_CLASS_MARKER ) {
 			LOG.debug( "Load request found matching entity in context, but the matched entity was of an inconsistent return type; returning null" );
 			return null;
 		}
 		if ( entity != null ) {
 			if (traceEnabled) LOG.tracev("Resolved object in session cache: {0}",
 						MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() ) );
 			return entity;
 		}
 
 		entity = loadFromSecondLevelCache(event, persister, options);
 		if ( entity != null ) {
 			if ( traceEnabled ) LOG.tracev( "Resolved object in second-level cache: {0}",
 						MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() ) );
 			return entity;
 		}
 
 		if ( traceEnabled ) LOG.tracev( "Object not resolved in any cache: {0}",
 					MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() ) );
 
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
 		
 		if (entity != null && persister.hasNaturalIdentifier()) {
-			final int[] naturalIdentifierProperties = persister.getNaturalIdentifierProperties();
-			final Object[] naturalId = new Object[naturalIdentifierProperties.length];
-			
-			for ( int i = 0; i < naturalIdentifierProperties.length; i++ ) {
-				naturalId[i] = persister.getPropertyValue( entity, naturalIdentifierProperties[i] );
-			}
-			
-			event.getSession().getPersistenceContext().cacheNaturalIdResolution(
+			event.getSession().getPersistenceContext().getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					event.getEntityId(),
-					naturalId,
-					CachedNaturalIdValueSource.LOAD
+					source.getPersistenceContext().getNaturalIdHelper().extractNaturalIdValues( entity, persister )
 			);
 		}
 
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
 				final EntityPersister persister = event.getSession().getFactory().getEntityPersister( keyToLoad.getEntityName() );
 				if ( ! persister.isInstance( old ) ) {
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Assembling entity from second-level cache: {0}",
 					MessageHelper.infoString( persister, id, factory ) );
 		}
 
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
 		LOG.tracev( "Cached Version: {0}", version );
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
index b1a2bfbe58..5c59ea3977 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
@@ -1,155 +1,154 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.spi.ResolveNaturalIdEvent;
 import org.hibernate.event.spi.ResolveNaturalIdEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * Defines the default load event listeners used by hibernate for loading entities
  * in response to generated load events.
  * 
  * @author Eric Dalquist
  * @author Steve Ebersole
  */
 public class DefaultResolveNaturalIdEventListener
 		extends AbstractLockUpgradeEventListener
 		implements ResolveNaturalIdEventListener {
 
 	public static final Object REMOVED_ENTITY_MARKER = new Object();
 	public static final Object INCONSISTENT_RTN_CLASS_MARKER = new Object();
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			DefaultResolveNaturalIdEventListener.class.getName()
 	);
 
 	@Override
 	public void onResolveNaturalId(ResolveNaturalIdEvent event) throws HibernateException {
 		final Serializable entityId = resolveNaturalId( event );
 		event.setEntityId( entityId );
 	}
 
 	/**
 	 * Coordinates the efforts to load a given entity. First, an attempt is
 	 * made to load the entity from the session-level cache. If not found there,
 	 * an attempt is made to locate it in second-level cache. Lastly, an
 	 * attempt is made to load it directly from the datasource.
 	 * 
 	 * @param event The load event
 	 *
 	 * @return The loaded entity, or null.
 	 */
 	protected Serializable resolveNaturalId(final ResolveNaturalIdEvent event) {
 		final EntityPersister persister = event.getEntityPersister();
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		if ( traceEnabled )
 			LOG.tracev( "Attempting to resolve: {0}",
 					MessageHelper.infoString( persister, event.getNaturalIdValues(), event.getSession().getFactory() ) );
 
 		Serializable entityId = resolveFromCache( event );
 		if ( entityId != null ) {
 			if ( traceEnabled )
 				LOG.tracev( "Resolved object in cache: {0}",
 						MessageHelper.infoString( persister, event.getNaturalIdValues(), event.getSession().getFactory() ) );
 			return entityId;
 		}
 
 		if ( traceEnabled )
 			LOG.tracev( "Object not resolved in any cache: {0}",
 					MessageHelper.infoString( persister, event.getNaturalIdValues(), event.getSession().getFactory() ) );
 
 		return loadFromDatasource( event );
 	}
 
 	/**
 	 * Attempts to resolve the entity id corresponding to the event's natural id values from the session
 	 * 
 	 * @param event The load event
 	 *
 	 * @return The entity from the cache, or null.
 	 */
 	protected Serializable resolveFromCache(final ResolveNaturalIdEvent event) {
-		return event.getSession().getPersistenceContext().findCachedNaturalIdResolution(
+		return event.getSession().getPersistenceContext().getNaturalIdHelper().findCachedNaturalIdResolution(
 				event.getEntityPersister(),
 				event.getOrderedNaturalIdValues()
 		);
 	}
 
 	/**
 	 * Performs the process of loading an entity from the configured
 	 * underlying datasource.
 	 * 
 	 * @param event The load event
 	 *
 	 * @return The object loaded from the datasource, or null if not found.
 	 */
 	protected Serializable loadFromDatasource(final ResolveNaturalIdEvent event) {
 		final SessionFactoryImplementor factory = event.getSession().getFactory();
 		final boolean stats = factory.getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) {
 			startTime = System.currentTimeMillis();
 		}
 		
 		final Serializable pk = event.getEntityPersister().loadEntityIdByNaturalId(
 				event.getOrderedNaturalIdValues(),
 				event.getLockOptions(),
 				event.getSession()
 		);
 		
 		if ( stats ) {
 			final NaturalIdRegionAccessStrategy naturalIdCacheAccessStrategy = event.getEntityPersister().getNaturalIdCacheAccessStrategy();
 			final String regionName = naturalIdCacheAccessStrategy == null ? null : naturalIdCacheAccessStrategy.getRegion().getName();
 			
 			factory.getStatisticsImplementor().naturalIdQueryExecuted(
 					regionName,
 					System.currentTimeMillis() - startTime );
 		}
 		
 		//PK can be null if the entity doesn't exist
 		if (pk != null) {
-			event.getSession().getPersistenceContext().cacheNaturalIdResolution(
+			event.getSession().getPersistenceContext().getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(
 					event.getEntityPersister(),
 					pk,
-					event.getOrderedNaturalIdValues(),
-					CachedNaturalIdValueSource.LOAD
+					event.getOrderedNaturalIdValues()
 			);
 		}
 		
 		return pk;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
index b6372d426c..25890c4661 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
@@ -1,1092 +1,1093 @@
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
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
-import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.Criteria;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.IdentifierLoadAccess;
 import org.hibernate.Interceptor;
 import org.hibernate.LobHelper;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NaturalIdLoadAccess;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.ReplicationMode;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionBuilder;
 import org.hibernate.SimpleNaturalIdLoadAccess;
 import org.hibernate.Transaction;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.UnresolvableObjectException;
+import org.hibernate.cache.spi.NaturalIdCacheKey;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.criterion.NaturalIdentifier;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.query.spi.FilterQueryPlan;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.ActionQueue;
+import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.NonFlushedChanges;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.engine.transaction.spi.TransactionObserver;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.AutoFlushEvent;
 import org.hibernate.event.spi.AutoFlushEventListener;
 import org.hibernate.event.spi.DeleteEvent;
 import org.hibernate.event.spi.DeleteEventListener;
 import org.hibernate.event.spi.DirtyCheckEvent;
 import org.hibernate.event.spi.DirtyCheckEventListener;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.EvictEvent;
 import org.hibernate.event.spi.EvictEventListener;
 import org.hibernate.event.spi.FlushEvent;
 import org.hibernate.event.spi.FlushEventListener;
 import org.hibernate.event.spi.InitializeCollectionEvent;
 import org.hibernate.event.spi.InitializeCollectionEventListener;
 import org.hibernate.event.spi.LoadEvent;
 import org.hibernate.event.spi.LoadEventListener;
 import org.hibernate.event.spi.LoadEventListener.LoadType;
 import org.hibernate.event.spi.LockEvent;
 import org.hibernate.event.spi.LockEventListener;
 import org.hibernate.event.spi.MergeEvent;
 import org.hibernate.event.spi.MergeEventListener;
 import org.hibernate.event.spi.PersistEvent;
 import org.hibernate.event.spi.PersistEventListener;
 import org.hibernate.event.spi.RefreshEvent;
 import org.hibernate.event.spi.RefreshEventListener;
 import org.hibernate.event.spi.ReplicateEvent;
 import org.hibernate.event.spi.ReplicateEventListener;
 import org.hibernate.event.spi.ResolveNaturalIdEvent;
 import org.hibernate.event.spi.ResolveNaturalIdEventListener;
 import org.hibernate.event.spi.SaveOrUpdateEvent;
 import org.hibernate.event.spi.SaveOrUpdateEventListener;
 import org.hibernate.internal.CriteriaImpl.CriterionEntry;
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
 import org.hibernate.stat.SessionStatistics;
 import org.hibernate.stat.internal.SessionStatisticsImpl;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
 
 /**
  * Concrete implementation of a Session.
  *
  * Exposes two interfaces:<ul>
  *     <li>{@link Session} to the application</li>
  *     <li>{@link org.hibernate.engine.spi.SessionImplementor} to other Hibernate components (SPI)</li>
  * </ul>
  *
  * This class is not thread-safe.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class SessionImpl extends AbstractSessionImpl implements EventSource {
 
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
 
 	private transient boolean autoClear; //for EJB3
 	private transient boolean autoJoinTransactions = true;
 	private transient boolean flushBeforeCompletionEnabled;
 	private transient boolean autoCloseSessionEnabled;
 
 	private transient int dontFlushFromFind = 0;
 
 	private transient LoadQueryInfluencers loadQueryInfluencers;
 
 	private final transient boolean isTransactionCoordinatorShared;
 
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
 	 * @param flushBeforeCompletionEnabled Should we auto flush before completion of transaction
 	 * @param autoCloseSessionEnabled Should we auto close after completion of transaction
 	 * @param connectionReleaseMode The mode by which we should release JDBC connections.
 	 * @param tenantIdentifier The tenant identifier to use.  May be null
 	 */
 	SessionImpl(
 			final Connection connection,
 			final SessionFactoryImpl factory,
 			final TransactionCoordinatorImpl transactionCoordinator,
 			final boolean autoJoinTransactions,
 			final long timestamp,
 			final Interceptor interceptor,
 			final boolean flushBeforeCompletionEnabled,
 			final boolean autoCloseSessionEnabled,
 			final ConnectionReleaseMode connectionReleaseMode,
 			final String tenantIdentifier) {
 		super( factory, tenantIdentifier );
 		this.timestamp = timestamp;
 		this.interceptor = interceptor == null ? EmptyInterceptor.INSTANCE : interceptor;
 		this.actionQueue = new ActionQueue( this );
 		this.persistenceContext = new StatefulPersistenceContext( this );
 
 		this.autoCloseSessionEnabled = autoCloseSessionEnabled;
 		this.flushBeforeCompletionEnabled = flushBeforeCompletionEnabled;
 
 		if ( transactionCoordinator == null ) {
 			this.isTransactionCoordinatorShared = false;
 			this.connectionReleaseMode = connectionReleaseMode;
 			this.autoJoinTransactions = autoJoinTransactions;
 
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
 			this.isTransactionCoordinatorShared = true;
 			this.autoJoinTransactions = false;
 			if ( autoJoinTransactions ) {
 				LOG.debug(
 						"Session creation specified 'autoJoinTransactions', which is invalid in conjunction " +
 								"with sharing JDBC connection between sessions; ignoring"
 				);
 			}
 			if ( connectionReleaseMode != transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getConnectionReleaseMode() ) {
 				LOG.debug(
 						"Session creation specified 'connectionReleaseMode', which is invalid in conjunction " +
 								"with sharing JDBC connection between sessions; ignoring"
 				);
 			}
 			this.connectionReleaseMode = transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getConnectionReleaseMode();
 
 			// add a transaction observer so that we can handle delegating managed actions back to THIS session
 			// versus the session that created (and therefore "owns") the transaction coordinator
 			transactionCoordinator.addObserver(
 					new TransactionObserver() {
 						@Override
 						public void afterBegin(TransactionImplementor transaction) {
 						}
 
 						@Override
 						public void beforeCompletion(TransactionImplementor transaction) {
 							if ( SessionImpl.this.flushBeforeCompletionEnabled ) {
 								SessionImpl.this.managedFlush();
 							}
 						}
 
 						@Override
 						public void afterCompletion(boolean successful, TransactionImplementor transaction) {
 							if ( SessionImpl.this.autoCloseSessionEnabled ) {
 								SessionImpl.this.managedClose();
 							}
 						}
 					}
 			);
 		}
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
 		if (factory.getStatistics().isStatisticsEnabled()) {
 			factory.getStatisticsImplementor().openSession();
 		}
 
 		LOG.debugf( "Opened session at timestamp: %s", timestamp );
 	}
 
 	@Override
 	public SharedSessionBuilder sessionWithOptions() {
 		return new SharedSessionBuilderImpl( this );
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
 		LOG.trace( "Closing session" );
 		if ( isClosed() ) {
 			throw new SessionException( "Session was already closed" );
 		}
 
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().closeSession();
 		}
 
 		try {
 			if ( !isTransactionCoordinatorShared ) {
 				return transactionCoordinator.close();
 			}
 			else {
 				return null; // ???
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
 			LOG.trace( "Skipping auto-flush due to session closed" );
 			return;
 		}
 		LOG.trace( "Automatically flushing session" );
 		flush();
 	}
 
 	/**
 	 * Return changes to this session and its child sessions that have not been flushed yet.
 	 * <p/>
 	 * @return The non-flushed changes.
 	 */
 	public NonFlushedChanges getNonFlushedChanges() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return new NonFlushedChangesImpl( this );
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
 		// todo : why aren't these just part of the NonFlushedChanges API ?
 		replacePersistenceContext( ((NonFlushedChangesImpl) nonFlushedChanges).getPersistenceContext() );
 		replaceActionQueue( ((NonFlushedChangesImpl) nonFlushedChanges).getActionQueue() );
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
 			catch (IOException ignore) {
 			}
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
 				catch( IOException ignore ) {
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
 			catch (IOException ignore) {
 			}
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
 		LOG.debug( "Disconnecting session" );
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().manualDisconnect();
 	}
 
 	@Override
 	public void reconnect(Connection conn) throws HibernateException {
 		errorIfClosed();
 		LOG.debug( "Reconnecting session" );
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
 	 *
 	 * @param success Was the operation a success
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
 		try {
 			interceptor.beforeTransactionCompletion( hibernateTransaction );
 		}
 		catch (Throwable t) {
 			LOG.exceptionInBeforeTransactionCompletionInterceptor( t );
 		}
 	}
 
 	@Override
 	public void afterTransactionCompletion(TransactionImplementor hibernateTransaction, boolean successful) {
 		LOG.trace( "after transaction completion" );
 		persistenceContext.afterTransactionCompletion();
 		actionQueue.afterTransactionCompletion( successful );
 		if ( hibernateTransaction != null ) {
 			try {
 				interceptor.afterTransactionCompletion( hibernateTransaction );
 			}
 			catch (Throwable t) {
 				LOG.exceptionInAfterTransactionCompletionInterceptor( t );
 			}
 		}
 		if ( autoClear ) {
 			clear();
 		}
 	}
 
 	@Override
 	public String onPrepareStatement(String sql) {
 		errorIfClosed();
 		sql = interceptor.onPrepareStatement( sql );
 		if ( sql == null || sql.length() == 0 ) {
 			throw new AssertionFailure( "Interceptor.onPrepareStatement() returned null or empty string." );
 		}
 		return sql;
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
 
 	private void checkNoUnresolvedActionsBeforeOperation() {
 		if ( persistenceContext.getCascadeLevel() == 0 && actionQueue.hasUnresolvedEntityInsertActions() ) {
 			throw new IllegalStateException( "There are delayed insert actions before operation as cascade level 0." );
 		}
 	}
 
 	private void checkNoUnresolvedActionsAfterOperation() {
 		if ( persistenceContext.getCascadeLevel() == 0 ) {
 			actionQueue.checkNoUnresolvedActionsAfterOperation();
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
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE_UPDATE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
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
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
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
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.UPDATE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 	}
 
 
 	// lock() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException {
 		fireLock( new LockEvent( entityName, object, lockMode, this ) );
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
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
 			listener.onPersist( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
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
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
 			listener.onPersist( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
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
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
 			listener.onMerge( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
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
 		fireDelete( new DeleteEvent( object, this ) );
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
 		return this.byId( entityClass ).getReference( id );
 	}
 
 	public Object load(String entityName, Serializable id) throws HibernateException {
 		return this.byId( entityName ).getReference( id );
 	}
 
 	public Object get(Class entityClass, Serializable id) throws HibernateException {
 		return this.byId( entityClass ).load( id );
 	}
 
 	public Object get(String entityName, Serializable id) throws HibernateException {
 		return this.byId( entityName ).load( id );
 	}
 
 	/**	
 	 * Load the data for the object with the specified id into a newly created object.
 	 * This is only called when lazily initializing a proxy.
 	 * Do NOT return a proxy.
 	 */
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
 		if ( LOG.isDebugEnabled() ) {
 			EntityPersister persister = getFactory().getEntityPersister(entityName);
 			LOG.debugf( "Initializing proxy: %s", MessageHelper.infoString( persister, id, getFactory() ) );
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
 		return this.byId( entityClass ).with( new LockOptions( lockMode ) ).getReference( id );
 	}
 
 	public Object load(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityClass ).with( lockOptions ).getReference( id );
 	}
 
 	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityName ).with( new LockOptions( lockMode ) ).getReference( id );
 	}
 
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityName ).with( lockOptions ).getReference( id );
 	}
 
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityClass ).with( new LockOptions( lockMode ) ).load( id );
 	}
 
 	public Object get(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityClass ).with( lockOptions ).load( id );
 	}
 
 	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityName ).with( new LockOptions( lockMode ) ).load( id );
 	}
 
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityName ).with( lockOptions ).load( id );
 	}
 	
 	@Override
 	public IdentifierLoadAccessImpl byId(String entityName) {
 		return new IdentifierLoadAccessImpl( entityName );
 	}
 
 	@Override
 	public IdentifierLoadAccessImpl byId(Class entityClass) {
 		return new IdentifierLoadAccessImpl( entityClass );
 	}
 
 	@Override
 	public NaturalIdLoadAccess byNaturalId(String entityName) {
 		return new NaturalIdLoadAccessImpl( entityName );
 	}
 
 	@Override
 	public NaturalIdLoadAccess byNaturalId(Class entityClass) {
 		return new NaturalIdLoadAccessImpl( entityClass );
 	}
 
 	@Override
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName) {
 		return new SimpleNaturalIdLoadAccessImpl( entityName );
 	}
 
 	@Override
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(Class entityClass) {
 		return new SimpleNaturalIdLoadAccessImpl( entityClass );
 	}
 
 	private void fireLoad(LoadEvent event, LoadType loadType) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( LoadEventListener listener : listeners( EventType.LOAD ) ) {
 			listener.onLoad( event, loadType );
 		}
 	}
 
 	private void fireResolveNaturalId(ResolveNaturalIdEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( ResolveNaturalIdEventListener listener : listeners( EventType.RESOLVE_NATURAL_ID ) ) {
 			listener.onResolveNaturalId( event );
 		}
 	}
 
 
 	// refresh() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void refresh(Object object) throws HibernateException {
 		refresh( null, object );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object) throws HibernateException {
 		fireRefresh( new RefreshEvent( entityName, object, this ) );
 	}
 
 	public void refresh(Object object, LockMode lockMode) throws HibernateException {
 		fireRefresh( new RefreshEvent( object, lockMode, this ) );
 	}
 
 	public void refresh(Object object, LockOptions lockOptions) throws HibernateException {
 		refresh( null, object, lockOptions );
 	}
 	@Override
 	public void refresh(String entityName, Object object, LockOptions lockOptions) throws HibernateException {
 		fireRefresh( new RefreshEvent( entityName, object, lockOptions, this ) );
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
 
@@ -1409,1144 +1410,1202 @@ public final class SessionImpl extends AbstractSessionImpl implements EventSourc
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
 		final NaturalIdLoadAccess naturalIdLoadAccess = this.tryNaturalIdLoadAccess( criteria );
 		if ( naturalIdLoadAccess != null ) {
 			// EARLY EXIT!
 			return Arrays.asList( naturalIdLoadAccess.load() );
 		}
 
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
 
 	/**
 	 * Checks to see if the CriteriaImpl is a naturalId lookup that can be done via
 	 * NaturalIdLoadAccess
 	 *
 	 * @param criteria The criteria to check as a complete natural identifier lookup.
 	 *
 	 * @return A fully configured NaturalIdLoadAccess or null, if null is returned the standard CriteriaImpl execution
 	 *         should be performed
 	 */
 	private NaturalIdLoadAccess tryNaturalIdLoadAccess(CriteriaImpl criteria) {
 		// See if the criteria lookup is by naturalId
 		if ( !criteria.isLookupByNaturalKey() ) {
 			return null;
 		}
 
 		final String entityName = criteria.getEntityOrClassName();
 		final EntityPersister entityPersister = factory.getEntityPersister( entityName );
 
 		// Verify the entity actually has a natural id, needed for legacy support as NaturalIdentifier criteria
 		// queries did no natural id validation
 		if ( !entityPersister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		// Since isLookupByNaturalKey is true there can be only one CriterionEntry and getCriterion() will
 		// return an instanceof NaturalIdentifier
 		final CriterionEntry criterionEntry = (CriterionEntry) criteria.iterateExpressionEntries().next();
 		final NaturalIdentifier naturalIdentifier = (NaturalIdentifier) criterionEntry.getCriterion();
 
 		final Map<String, Object> naturalIdValues = naturalIdentifier.getNaturalIdValues();
 		final int[] naturalIdentifierProperties = entityPersister.getNaturalIdentifierProperties();
 
 		// Verify the NaturalIdentifier criterion includes all naturalId properties, first check that the property counts match
 		if ( naturalIdentifierProperties.length != naturalIdValues.size() ) {
 			return null;
 		}
 
 		final String[] propertyNames = entityPersister.getPropertyNames();
 		final NaturalIdLoadAccess naturalIdLoader = this.byNaturalId( entityName );
 
 		// Build NaturalIdLoadAccess and in the process verify all naturalId properties were specified
 		for ( int i = 0; i < naturalIdentifierProperties.length; i++ ) {
 			final String naturalIdProperty = propertyNames[naturalIdentifierProperties[i]];
 			final Object naturalIdValue = naturalIdValues.get( naturalIdProperty );
 
 			if ( naturalIdValue == null ) {
 				// A NaturalId property is missing from the critera query, can't use NaturalIdLoadAccess
 				return null;
 			}
 
 			naturalIdLoader.using( naturalIdProperty, naturalIdValue );
 		}
 
 		// Critera query contains a valid naturalId, use the new API
 		LOG.warn( "Session.byNaturalId(" + entityName
 				+ ") should be used for naturalId queries instead of Restrictions.naturalId() from a Criteria" );
 
 		return naturalIdLoader;
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Scroll SQL query: {0}", customQuery.getSQL() );
 		}
 
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "SQL query: {0}", customQuery.getSQL() );
 		}
 
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
 		InitializeCollectionEvent event = new InitializeCollectionEvent( collection, this );
 		for ( InitializeCollectionEventListener listener : listeners( EventType.INIT_COLLECTION ) ) {
 			listener.onInitializeCollection( event );
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
 		StringBuilder buf = new StringBuilder(500)
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
 		persistenceContext.setReadOnly( entity, readOnly );
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
 		LOG.trace( "Deserializing session" );
 
 		ois.defaultReadObject();
 
 		entityNameResolver = new CoordinatingEntityNameResolver();
 
 		connectionReleaseMode = ConnectionReleaseMode.parse( ( String ) ois.readObject() );
 		autoClear = ois.readBoolean();
 		autoJoinTransactions = ois.readBoolean();
 		flushMode = FlushMode.valueOf( ( String ) ois.readObject() );
 		cacheMode = CacheMode.valueOf( ( String ) ois.readObject() );
 		flushBeforeCompletionEnabled = ois.readBoolean();
 		autoCloseSessionEnabled = ois.readBoolean();
 		interceptor = ( Interceptor ) ois.readObject();
 
 		factory = SessionFactoryImpl.deserialize( ois );
 
 		transactionCoordinator = TransactionCoordinatorImpl.deserialize( ois, this );
 
 		persistenceContext = StatefulPersistenceContext.deserialize( ois, this );
 		actionQueue = ActionQueue.deserialize( ois, this );
 
 		loadQueryInfluencers = (LoadQueryInfluencers) ois.readObject();
 
 		// LoadQueryInfluencers.getEnabledFilters() tries to validate each enabled
 		// filter, which will fail when called before FilterImpl.afterDeserialize( factory );
 		// Instead lookup the filter by name and then call FilterImpl.afterDeserialize( factory ).
 		for ( String filterName : loadQueryInfluencers.getEnabledFilterNames() ) {
 			((FilterImpl) loadQueryInfluencers.getEnabledFilter( filterName )).afterDeserialize( factory );
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
 
 		LOG.trace( "Serializing session" );
 
 		oos.defaultWriteObject();
 
 		oos.writeObject( connectionReleaseMode.toString() );
 		oos.writeBoolean( autoClear );
 		oos.writeBoolean( autoJoinTransactions );
 		oos.writeObject( flushMode.toString() );
 		oos.writeObject( cacheMode.name() );
 		oos.writeBoolean( flushBeforeCompletionEnabled );
 		oos.writeBoolean( autoCloseSessionEnabled );
 		// we need to writeObject() on this since interceptor is user defined
 		oos.writeObject( interceptor );
 
 		factory.serialize( oos );
 
 		transactionCoordinator.serialize( oos );
 
 		persistenceContext.serialize( oos );
 		actionQueue.serialize( oos );
 
 		// todo : look at optimizing these...
 		oos.writeObject( loadQueryInfluencers );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public TypeHelper getTypeHelper() {
 		return getSessionFactory().getTypeHelper();
 	}
 
 	@Override
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
 
 		@Override
 		public Blob createBlob(byte[] bytes) {
 			return lobCreator().createBlob( bytes );
 		}
 
 		private LobCreator lobCreator() {
 			return session.getFactory().getJdbcServices().getLobCreator( session );
 		}
 
 		@Override
 		public Blob createBlob(InputStream stream, long length) {
 			return lobCreator().createBlob( stream, length );
 		}
 
 		@Override
 		public Clob createClob(String string) {
 			return lobCreator().createClob( string );
 		}
 
 		@Override
 		public Clob createClob(Reader reader, long length) {
 			return lobCreator().createClob( reader, length );
 		}
 
 		@Override
 		public NClob createNClob(String string) {
 			return lobCreator().createNClob( string );
 		}
 
 		@Override
 		public NClob createNClob(Reader reader, long length) {
 			return lobCreator().createNClob( reader, length );
 		}
 	}
 
 	private static class SharedSessionBuilderImpl extends SessionFactoryImpl.SessionBuilderImpl implements SharedSessionBuilder {
 		private final SessionImpl session;
 		private boolean shareTransactionContext;
 
 		private SharedSessionBuilderImpl(SessionImpl session) {
 			super( session.factory );
 			this.session = session;
 			super.tenantIdentifier( session.getTenantIdentifier() );
 		}
 
 		@Override
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			// todo : is this always true?  Or just in the case of sharing JDBC resources?
 			throw new SessionException( "Cannot redefine tenant identifier on child session" );
 		}
 
 		@Override
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return shareTransactionContext ? session.transactionCoordinator : super.getTransactionCoordinator();
 		}
 
 		@Override
 		public SharedSessionBuilder interceptor() {
 			return interceptor( session.interceptor );
 		}
 
 		@Override
 		public SharedSessionBuilder connection() {
 			this.shareTransactionContext = true;
 			return this;
 		}
 
 		@Override
 		public SharedSessionBuilder connectionReleaseMode() {
 			return connectionReleaseMode( session.connectionReleaseMode );
 		}
 
 		@Override
 		public SharedSessionBuilder autoJoinTransactions() {
 			return autoJoinTransactions( session.autoJoinTransactions );
 		}
 
 		@Override
 		public SharedSessionBuilder autoClose() {
 			return autoClose( session.autoCloseSessionEnabled );
 		}
 
 		@Override
 		public SharedSessionBuilder flushBeforeCompletion() {
 			return flushBeforeCompletion( session.flushBeforeCompletionEnabled );
 		}
 
 		/**
 		 * @deprecated Use {@link #connection()} instead
 		 */
 		@Override
 		@Deprecated
 		public SharedSessionBuilder transactionContext() {
 			return connection();
 		}
 
 		@Override
 		public SharedSessionBuilder interceptor(Interceptor interceptor) {
 			return (SharedSessionBuilder) super.interceptor( interceptor );
 		}
 
 		@Override
 		public SharedSessionBuilder noInterceptor() {
 			return (SharedSessionBuilder) super.noInterceptor();
 		}
 
 		@Override
 		public SharedSessionBuilder connection(Connection connection) {
 			return (SharedSessionBuilder) super.connection( connection );
 		}
 
 		@Override
 		public SharedSessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
 			return (SharedSessionBuilder) super.connectionReleaseMode( connectionReleaseMode );
 		}
 
 		@Override
 		public SharedSessionBuilder autoJoinTransactions(boolean autoJoinTransactions) {
 			return (SharedSessionBuilder) super.autoJoinTransactions( autoJoinTransactions );
 		}
 
 		@Override
 		public SharedSessionBuilder autoClose(boolean autoClose) {
 			return (SharedSessionBuilder) super.autoClose( autoClose );
 		}
 
 		@Override
 		public SharedSessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion) {
 			return (SharedSessionBuilder) super.flushBeforeCompletion( flushBeforeCompletion );
 		}
 	}
 
 	private class CoordinatingEntityNameResolver implements EntityNameResolver {
 		public String resolveEntityName(Object entity) {
 			String entityName = interceptor.getEntityName( entity );
 			if ( entityName != null ) {
 				return entityName;
 			}
 
 			for ( EntityNameResolver resolver : factory.iterateEntityNameResolvers() ) {
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
 
 	private class IdentifierLoadAccessImpl implements IdentifierLoadAccess {
 		private final EntityPersister entityPersister;
 		private LockOptions lockOptions;
 
 		private IdentifierLoadAccessImpl(EntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 		}
 
 		private IdentifierLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private IdentifierLoadAccessImpl(Class entityClass) {
 			this( entityClass.getName() );
 		}
 
 		@Override
 		public final IdentifierLoadAccessImpl with(LockOptions lockOptions) {
 			this.lockOptions = lockOptions;
 			return this;
 		}
 
 		@Override
 		public final Object getReference(Serializable id) {
 			if ( this.lockOptions != null ) {
 				LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), lockOptions, SessionImpl.this );
 				fireLoad( event, LoadEventListener.LOAD );
 				return event.getResult();
 			}
 
 			LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), false, SessionImpl.this );
 			boolean success = false;
 			try {
 				fireLoad( event, LoadEventListener.LOAD );
 				if ( event.getResult() == null ) {
 					getFactory().getEntityNotFoundDelegate().handleEntityNotFound( entityPersister.getEntityName(), id );
 				}
 				success = true;
 				return event.getResult();
 			}
 			finally {
 				afterOperation( success );
 			}
 		}
 
 		@Override
 		public final Object load(Serializable id) {
 			if ( this.lockOptions != null ) {
 				LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), lockOptions, SessionImpl.this );
 				fireLoad( event, LoadEventListener.GET );
 				return event.getResult();
 			}
 
 			LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), false, SessionImpl.this );
 			boolean success = false;
 			try {
 				fireLoad( event, LoadEventListener.GET );
 				success = true;
 				return event.getResult();
 			}
 			finally {
 				afterOperation( success );
 			}
 		}
 	}
 
 	private EntityPersister locateEntityPersister(String entityName) {
 		final EntityPersister entityPersister = factory.getEntityPersister( entityName );
 		if ( entityPersister == null ) {
 			throw new HibernateException( "Unable to locate persister: " + entityName );
 		}
 		return entityPersister;
 	}
 
 	private abstract class BaseNaturalIdLoadAccessImpl  {
 		private final EntityPersister entityPersister;
 		private LockOptions lockOptions;
+		private boolean synchronizationEnabled = true;
 
 		private BaseNaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 
 			if ( ! entityPersister.hasNaturalIdentifier() ) {
 				throw new HibernateException(
 						String.format( "Entity [%s] did not define a natural id", entityPersister.getEntityName() )
 				);
 			}
 		}
 
 		private BaseNaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private BaseNaturalIdLoadAccessImpl(Class entityClass) {
 			this( entityClass.getName() );
 		}
 
 		public BaseNaturalIdLoadAccessImpl with(LockOptions lockOptions) {
 			this.lockOptions = lockOptions;
 			return this;
 		}
 
+		protected void synchronizationEnabled(boolean synchronizationEnabled) {
+			this.synchronizationEnabled = synchronizationEnabled;
+		}
+
 		protected final Serializable resolveNaturalId(Map<String, Object> naturalIdParameters) {
-			final Set<Serializable> querySpaces = new LinkedHashSet<Serializable>();
-			for ( final Serializable querySpace : entityPersister.getQuerySpaces() ) {
-				querySpaces.add( querySpace );
-			}
-			
-			autoFlushIfRequired( querySpaces );
-			
+			performAnyNeededCrossReferenceSynchronizations();
+
 			final ResolveNaturalIdEvent event =
 					new ResolveNaturalIdEvent( naturalIdParameters, entityPersister, SessionImpl.this );
 			fireResolveNaturalId( event );
-			return event.getEntityId();
+
+			if ( event.getEntityId() == PersistenceContext.NaturalIdHelper.INVALID_NATURAL_ID_REFERENCE ) {
+				return null;
+			}
+			else {
+				return event.getEntityId();
+			}
+		}
+
+		protected void performAnyNeededCrossReferenceSynchronizations() {
+			if ( ! synchronizationEnabled ) {
+				// synchronization (this process) was disabled
+				return;
+			}
+			if ( ! isTransactionInProgress() ) {
+				// not in a transaction so skip synchronization
+				return;
+			}
+			if ( entityPersister.getEntityMetamodel().hasImmutableNaturalId() ) {
+				// only mutable natural-ids need this processing 
+				return;
+			}
+
+			for ( Serializable pk : getPersistenceContext().getNaturalIdHelper().getCachedPkResolutions( entityPersister ) ) {
+				final EntityKey entityKey = generateEntityKey( pk, entityPersister );
+				final Object entity = getPersistenceContext().getEntity( entityKey );
+				final EntityEntry entry = getPersistenceContext().getEntry( entity );
+
+				if ( !entry.requiresDirtyCheck( entity ) ) {
+					continue;
+				}
+
+				// MANAGED is the only status we care about here...
+				if ( entry.getStatus() != Status.MANAGED ) {
+					continue;
+				}
+
+				getPersistenceContext().getNaturalIdHelper().handleSynchronization(
+						entityPersister,
+						pk,
+						entity
+				);
+			}
 		}
 
 		protected final IdentifierLoadAccess getIdentifierLoadAccess() {
 			final IdentifierLoadAccessImpl identifierLoadAccess = new IdentifierLoadAccessImpl( entityPersister );
 			if ( this.lockOptions != null ) {
 				identifierLoadAccess.with( lockOptions );
 			}
 			return identifierLoadAccess;
 		}
+
+		protected EntityPersister entityPersister() {
+			return entityPersister;
+		}
 	}
 
 	private class NaturalIdLoadAccessImpl extends BaseNaturalIdLoadAccessImpl implements NaturalIdLoadAccess {
 		private final Map<String, Object> naturalIdParameters = new LinkedHashMap<String, Object>();
 
 		private NaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			super(entityPersister);
 		}
 
 		private NaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private NaturalIdLoadAccessImpl(Class entityClass) {
 			this( entityClass.getName() );
 		}
 		
 		@Override
 		public NaturalIdLoadAccessImpl with(LockOptions lockOptions) {
 			return (NaturalIdLoadAccessImpl) super.with( lockOptions );
 		}
 
 		@Override
 		public NaturalIdLoadAccess using(String attributeName, Object value) {
 			naturalIdParameters.put( attributeName, value );
 			return this;
 		}
 
 		@Override
+		public NaturalIdLoadAccessImpl setSynchronizationEnabled(boolean synchronizationEnabled) {
+			super.synchronizationEnabled( synchronizationEnabled );
+			return this;
+		}
+
+		@Override
 		public final Object getReference() {
 			final Serializable entityId = resolveNaturalId( this.naturalIdParameters );
 			if ( entityId == null ) {
 				return null;
 			}
 			return this.getIdentifierLoadAccess().getReference( entityId );
 		}
 
 		@Override
 		public final Object load() {
 			final Serializable entityId = resolveNaturalId( this.naturalIdParameters );
 			if ( entityId == null ) {
 				return null;
 			}
 			return this.getIdentifierLoadAccess().load( entityId );
 		}
 	}
 
 	private class SimpleNaturalIdLoadAccessImpl extends BaseNaturalIdLoadAccessImpl implements SimpleNaturalIdLoadAccess {
 		private final String naturalIdAttributeName;
 
 		private SimpleNaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			super(entityPersister);
 
 			if ( entityPersister.getNaturalIdentifierProperties().length != 1 ) {
 				throw new HibernateException(
 						String.format( "Entity [%s] did not define a simple natural id", entityPersister.getEntityName() )
 				);
 			}
 
 			final int naturalIdAttributePosition = entityPersister.getNaturalIdentifierProperties()[0];
 			this.naturalIdAttributeName = entityPersister.getPropertyNames()[ naturalIdAttributePosition ];
 		}
 
 		private SimpleNaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private SimpleNaturalIdLoadAccessImpl(Class entityClass) {
 			this( entityClass.getName() );
 		}
 
 		@Override
 		public final SimpleNaturalIdLoadAccessImpl with(LockOptions lockOptions) {
 			return (SimpleNaturalIdLoadAccessImpl) super.with( lockOptions );
 		}
 		
 		private Map<String, Object> getNaturalIdParameters(Object naturalIdValue) {
 			return Collections.singletonMap( naturalIdAttributeName, naturalIdValue );
 		}
 
 		@Override
+		public SimpleNaturalIdLoadAccessImpl setSynchronizationEnabled(boolean synchronizationEnabled) {
+			super.synchronizationEnabled( synchronizationEnabled );
+			return this;
+		}
+
+		@Override
 		public Object getReference(Object naturalIdValue) {
 			final Serializable entityId = resolveNaturalId( getNaturalIdParameters( naturalIdValue ) );
 			if ( entityId == null ) {
 				return null;
 			}
 			return this.getIdentifierLoadAccess().getReference( entityId );
 		}
 
 		@Override
 		public Object load(Object naturalIdValue) {
 			final Serializable entityId = resolveNaturalId( getNaturalIdParameters( naturalIdValue ) );
 			if ( entityId == null ) {
 				return null;
 			}
 			return this.getIdentifierLoadAccess().load( entityId );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
index d8a4d3f77b..82742db83d 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
@@ -1,962 +1,962 @@
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
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.spi.EntityInstrumentationMetadata;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.BasicAttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.tuple.IdentifierProperty;
 import org.hibernate.tuple.PropertyFactory;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.VersionProperty;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Centralizes metamodel information about an entity.
  *
  * @author Steve Ebersole
  */
 public class EntityMetamodel implements Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityMetamodel.class.getName());
 
 	private static final int NO_VERSION_INDX = -66;
 
 	private final SessionFactoryImplementor sessionFactory;
 
 	private final String name;
 	private final String rootName;
 	private final EntityType entityType;
 
 	private final IdentifierProperty identifierProperty;
 	private final boolean versioned;
 
 	private final int propertySpan;
 	private final int versionPropertyIndex;
 	private final StandardProperty[] properties;
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyLaziness;
 	private final boolean[] propertyUpdateability;
 	private final boolean[] nonlazyPropertyUpdateability;
 	private final boolean[] propertyCheckability;
 	private final boolean[] propertyInsertability;
 	private final ValueInclusion[] insertInclusions;
 	private final ValueInclusion[] updateInclusions;
 	private final boolean[] propertyNullability;
 	private final boolean[] propertyVersionability;
 	private final CascadeStyle[] cascadeStyles;
 	private final boolean hasInsertGeneratedValues;
 	private final boolean hasUpdateGeneratedValues;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final Map<String, Integer> propertyIndexes = new HashMap<String, Integer>();
 	private final boolean hasCollections;
 	private final boolean hasMutableProperties;
 	private final boolean hasLazyProperties;
 	private final boolean hasNonIdentifierPropertyNamedId;
 
 	private final int[] naturalIdPropertyNumbers;
 	private final boolean hasImmutableNaturalId;
 	private final boolean hasCacheableNaturalId;
 
 	private boolean lazy; //not final because proxy factory creation can fail
 	private final boolean hasCascades;
 	private final boolean mutable;
 	private final boolean isAbstract;
 	private final boolean selectBeforeUpdate;
 	private final boolean dynamicUpdate;
 	private final boolean dynamicInsert;
 	private final OptimisticLockStyle optimisticLockStyle;
 
 	private final boolean polymorphic;
 	private final String superclass;  // superclass entity-name
 	private final boolean explicitPolymorphism;
 	private final boolean inherited;
 	private final boolean hasSubclasses;
 	private final Set subclassEntityNames = new HashSet();
 	private final Map entityNameByInheritenceClassMap = new HashMap();
 
 	private final EntityMode entityMode;
 	private final EntityTuplizer entityTuplizer;
 	private final EntityInstrumentationMetadata instrumentationMetadata;
 
 	public EntityMetamodel(PersistentClass persistentClass, SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 
 		name = persistentClass.getEntityName();
 		rootName = persistentClass.getRootClass().getEntityName();
 		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
 
 		identifierProperty = PropertyFactory.buildIdentifierProperty(
 		        persistentClass,
 		        sessionFactory.getIdentifierGenerator( rootName )
 			);
 
 		versioned = persistentClass.isVersioned();
 
 		instrumentationMetadata = persistentClass.hasPojoRepresentation()
 				? Environment.getBytecodeProvider().getEntityInstrumentationMetadata( persistentClass.getMappedClass() )
 				: new NonPojoInstrumentationMetadata( persistentClass.getEntityName() );
 
 		boolean hasLazy = false;
 
 		propertySpan = persistentClass.getPropertyClosureSpan();
 		properties = new StandardProperty[propertySpan];
 		List<Integer> naturalIdNumbers = new ArrayList<Integer>();
 		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		propertyNames = new String[propertySpan];
 		propertyTypes = new Type[propertySpan];
 		propertyUpdateability = new boolean[propertySpan];
 		propertyInsertability = new boolean[propertySpan];
 		insertInclusions = new ValueInclusion[propertySpan];
 		updateInclusions = new ValueInclusion[propertySpan];
 		nonlazyPropertyUpdateability = new boolean[propertySpan];
 		propertyCheckability = new boolean[propertySpan];
 		propertyNullability = new boolean[propertySpan];
 		propertyVersionability = new boolean[propertySpan];
 		propertyLaziness = new boolean[propertySpan];
 		cascadeStyles = new CascadeStyle[propertySpan];
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i = 0;
 		int tempVersionProperty = NO_VERSION_INDX;
 		boolean foundCascade = false;
 		boolean foundCollection = false;
 		boolean foundMutable = false;
 		boolean foundNonIdentifierPropertyNamedId = false;
 		boolean foundInsertGeneratedValue = false;
 		boolean foundUpdateGeneratedValue = false;
 		boolean foundUpdateableNaturalIdProperty = false;
 
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 
 			if ( prop == persistentClass.getVersion() ) {
 				tempVersionProperty = i;
 				properties[i] = PropertyFactory.buildVersionProperty( prop, instrumentationMetadata.isInstrumented() );
 			}
 			else {
 				properties[i] = PropertyFactory.buildStandardProperty( prop, instrumentationMetadata.isInstrumented() );
 			}
 
 			if ( prop.isNaturalIdentifier() ) {
 				naturalIdNumbers.add( i );
 				if ( prop.isUpdateable() ) {
 					foundUpdateableNaturalIdProperty = true;
 				}
 			}
 
 			if ( "id".equals( prop.getName() ) ) {
 				foundNonIdentifierPropertyNamedId = true;
 			}
 
 			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			boolean lazy = prop.isLazy() && instrumentationMetadata.isInstrumented();
 			if ( lazy ) hasLazy = true;
 			propertyLaziness[i] = lazy;
 
 			propertyNames[i] = properties[i].getName();
 			propertyTypes[i] = properties[i].getType();
 			propertyNullability[i] = properties[i].isNullable();
 			propertyUpdateability[i] = properties[i].isUpdateable();
 			propertyInsertability[i] = properties[i].isInsertable();
 			insertInclusions[i] = determineInsertValueGenerationType( prop, properties[i] );
 			updateInclusions[i] = determineUpdateValueGenerationType( prop, properties[i] );
 			propertyVersionability[i] = properties[i].isVersionable();
 			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
 			propertyCheckability[i] = propertyUpdateability[i] ||
 					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
 
 			cascadeStyles[i] = properties[i].getCascadeStyle();
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			if ( properties[i].isLazy() ) {
 				hasLazy = true;
 			}
 
 			if ( properties[i].getCascadeStyle() != CascadeStyle.NONE ) {
 				foundCascade = true;
 			}
 
 			if ( indicatesCollection( properties[i].getType() ) ) {
 				foundCollection = true;
 			}
 
 			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
 				foundMutable = true;
 			}
 
 			if ( insertInclusions[i] != ValueInclusion.NONE ) {
 				foundInsertGeneratedValue = true;
 			}
 
 			if ( updateInclusions[i] != ValueInclusion.NONE ) {
 				foundUpdateGeneratedValue = true;
 			}
 
 			mapPropertyToIndex(prop, i);
 			i++;
 		}
 
 		if (naturalIdNumbers.size()==0) {
 			naturalIdPropertyNumbers = null;
 			hasImmutableNaturalId = false;
 			hasCacheableNaturalId = false;
 		}
 		else {
 			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
 			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
 			hasCacheableNaturalId = persistentClass.getNaturalIdCacheRegionName() != null;
 		}
 
 		hasInsertGeneratedValues = foundInsertGeneratedValue;
 		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
 
 		hasCascades = foundCascade;
 		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
 		versionPropertyIndex = tempVersionProperty;
 		hasLazyProperties = hasLazy;
         if (hasLazyProperties) LOG.lazyPropertyFetchingAvailable(name);
 
 		lazy = persistentClass.isLazy() && (
 				// TODO: this disables laziness even in non-pojo entity modes:
 				!persistentClass.hasPojoRepresentation() ||
 				!ReflectHelper.isFinalClass( persistentClass.getProxyInterface() )
 		);
 		mutable = persistentClass.isMutable();
 		if ( persistentClass.isAbstract() == null ) {
 			// legacy behavior (with no abstract attribute specified)
 			isAbstract = persistentClass.hasPojoRepresentation() &&
 			             ReflectHelper.isAbstractClass( persistentClass.getMappedClass() );
 		}
 		else {
 			isAbstract = persistentClass.isAbstract().booleanValue();
 			if ( !isAbstract && persistentClass.hasPojoRepresentation() &&
 			     ReflectHelper.isAbstractClass( persistentClass.getMappedClass() ) ) {
                 LOG.entityMappedAsNonAbstract(name);
 			}
 		}
 		selectBeforeUpdate = persistentClass.hasSelectBeforeUpdate();
 		dynamicUpdate = persistentClass.useDynamicUpdate();
 		dynamicInsert = persistentClass.useDynamicInsert();
 
 		polymorphic = persistentClass.isPolymorphic();
 		explicitPolymorphism = persistentClass.isExplicitPolymorphism();
 		inherited = persistentClass.isInherited();
 		superclass = inherited ?
 				persistentClass.getSuperclass().getEntityName() :
 				null;
 		hasSubclasses = persistentClass.hasSubclasses();
 
 		optimisticLockStyle = interpretOptLockMode( persistentClass.getOptimisticLockMode() );
 		final boolean isAllOrDirty =
 				optimisticLockStyle == OptimisticLockStyle.ALL
 						|| optimisticLockStyle == OptimisticLockStyle.DIRTY;
 		if ( isAllOrDirty && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && isAllOrDirty ) {
 			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
 		}
 
 		hasCollections = foundCollection;
 		hasMutableProperties = foundMutable;
 
 		iter = persistentClass.getSubclassIterator();
 		while ( iter.hasNext() ) {
 			subclassEntityNames.add( ( (PersistentClass) iter.next() ).getEntityName() );
 		}
 		subclassEntityNames.add( name );
 
 		if ( persistentClass.hasPojoRepresentation() ) {
 			entityNameByInheritenceClassMap.put( persistentClass.getMappedClass(), persistentClass.getEntityName() );
 			iter = persistentClass.getSubclassIterator();
 			while ( iter.hasNext() ) {
 				final PersistentClass pc = ( PersistentClass ) iter.next();
 				entityNameByInheritenceClassMap.put( pc.getMappedClass(), pc.getEntityName() );
 			}
 		}
 
 		entityMode = persistentClass.hasPojoRepresentation() ? EntityMode.POJO : EntityMode.MAP;
 		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
 		final String tuplizerClassName = persistentClass.getTuplizerImplClassName( entityMode );
 		if ( tuplizerClassName == null ) {
 			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, persistentClass );
 		}
 		else {
 			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClassName, this, persistentClass );
 		}
 	}
 
 	private OptimisticLockStyle interpretOptLockMode(int optimisticLockMode) {
 		switch ( optimisticLockMode ) {
 			case Versioning.OPTIMISTIC_LOCK_NONE: {
 				return OptimisticLockStyle.NONE;
 			}
 			case Versioning.OPTIMISTIC_LOCK_DIRTY: {
 				return OptimisticLockStyle.DIRTY;
 			}
 			case Versioning.OPTIMISTIC_LOCK_ALL: {
 				return OptimisticLockStyle.ALL;
 			}
 			default: {
 				return OptimisticLockStyle.VERSION;
 			}
 		}
 	}
 
 	public EntityMetamodel(EntityBinding entityBinding, SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 
 		name = entityBinding.getEntity().getName();
 
 		rootName = entityBinding.getHierarchyDetails().getRootEntityBinding().getEntity().getName();
 		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
 
 		identifierProperty = PropertyFactory.buildIdentifierProperty(
 		        entityBinding,
 		        sessionFactory.getIdentifierGenerator( rootName )
 		);
 
 		versioned = entityBinding.isVersioned();
 
 		boolean hasPojoRepresentation = false;
 		Class<?> mappedClass = null;
 		Class<?> proxyInterfaceClass = null;
 		if ( entityBinding.getEntity().getClassReferenceUnresolved() != null ) {
 			hasPojoRepresentation = true;
 			mappedClass = entityBinding.getEntity().getClassReference();
 			proxyInterfaceClass = entityBinding.getProxyInterfaceType().getValue();
 		}
 		instrumentationMetadata = Environment.getBytecodeProvider().getEntityInstrumentationMetadata( mappedClass );
 
 		boolean hasLazy = false;
 
 		// TODO: Fix after HHH-6337 is fixed; for now assume entityBinding is the root binding
 		BasicAttributeBinding rootEntityIdentifier = entityBinding.getHierarchyDetails().getEntityIdentifier().getValueBinding();
 		// entityBinding.getAttributeClosureSpan() includes the identifier binding;
 		// "properties" here excludes the ID, so subtract 1 if the identifier binding is non-null
 		propertySpan = rootEntityIdentifier == null ?
 				entityBinding.getAttributeBindingClosureSpan() :
 				entityBinding.getAttributeBindingClosureSpan() - 1;
 
 		properties = new StandardProperty[propertySpan];
 		List naturalIdNumbers = new ArrayList();
 		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		propertyNames = new String[propertySpan];
 		propertyTypes = new Type[propertySpan];
 		propertyUpdateability = new boolean[propertySpan];
 		propertyInsertability = new boolean[propertySpan];
 		insertInclusions = new ValueInclusion[propertySpan];
 		updateInclusions = new ValueInclusion[propertySpan];
 		nonlazyPropertyUpdateability = new boolean[propertySpan];
 		propertyCheckability = new boolean[propertySpan];
 		propertyNullability = new boolean[propertySpan];
 		propertyVersionability = new boolean[propertySpan];
 		propertyLaziness = new boolean[propertySpan];
 		cascadeStyles = new CascadeStyle[propertySpan];
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 		int i = 0;
 		int tempVersionProperty = NO_VERSION_INDX;
 		boolean foundCascade = false;
 		boolean foundCollection = false;
 		boolean foundMutable = false;
 		boolean foundNonIdentifierPropertyNamedId = false;
 		boolean foundInsertGeneratedValue = false;
 		boolean foundUpdateGeneratedValue = false;
 		boolean foundUpdateableNaturalIdProperty = false;
 
 		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			if ( attributeBinding == rootEntityIdentifier ) {
 				// skip the identifier attribute binding
 				continue;
 			}
 
 			if ( attributeBinding == entityBinding.getHierarchyDetails().getVersioningAttributeBinding() ) {
 				tempVersionProperty = i;
 				properties[i] = PropertyFactory.buildVersionProperty(
 						entityBinding.getHierarchyDetails().getVersioningAttributeBinding(),
 						instrumentationMetadata.isInstrumented()
 				);
 			}
 			else {
 				properties[i] = PropertyFactory.buildStandardProperty( attributeBinding, instrumentationMetadata.isInstrumented() );
 			}
 
 			// TODO: fix when natural IDs are added (HHH-6354)
 			//if ( attributeBinding.isNaturalIdentifier() ) {
 			//	naturalIdNumbers.add( i );
 			//	if ( attributeBinding.isUpdateable() ) {
 			//		foundUpdateableNaturalIdProperty = true;
 			//	}
 			//}
 
 			if ( "id".equals( attributeBinding.getAttribute().getName() ) ) {
 				foundNonIdentifierPropertyNamedId = true;
 			}
 
 			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			boolean lazy = attributeBinding.isLazy() && instrumentationMetadata.isInstrumented();
 			if ( lazy ) hasLazy = true;
 			propertyLaziness[i] = lazy;
 
 			propertyNames[i] = properties[i].getName();
 			propertyTypes[i] = properties[i].getType();
 			propertyNullability[i] = properties[i].isNullable();
 			propertyUpdateability[i] = properties[i].isUpdateable();
 			propertyInsertability[i] = properties[i].isInsertable();
 			insertInclusions[i] = determineInsertValueGenerationType( attributeBinding, properties[i] );
 			updateInclusions[i] = determineUpdateValueGenerationType( attributeBinding, properties[i] );
 			propertyVersionability[i] = properties[i].isVersionable();
 			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
 			propertyCheckability[i] = propertyUpdateability[i] ||
 					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
 
 			cascadeStyles[i] = properties[i].getCascadeStyle();
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			if ( properties[i].isLazy() ) {
 				hasLazy = true;
 			}
 
 			if ( properties[i].getCascadeStyle() != CascadeStyle.NONE ) {
 				foundCascade = true;
 			}
 
 			if ( indicatesCollection( properties[i].getType() ) ) {
 				foundCollection = true;
 			}
 
 			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
 				foundMutable = true;
 			}
 
 			if ( insertInclusions[i] != ValueInclusion.NONE ) {
 				foundInsertGeneratedValue = true;
 			}
 
 			if ( updateInclusions[i] != ValueInclusion.NONE ) {
 				foundUpdateGeneratedValue = true;
 			}
 
 			mapPropertyToIndex(attributeBinding.getAttribute(), i);
 			i++;
 		}
 
 		if (naturalIdNumbers.size()==0) {
 			naturalIdPropertyNumbers = null;
 			hasImmutableNaturalId = false;
 			hasCacheableNaturalId = false;
 		}
 		else {
 			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
 			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
 			hasCacheableNaturalId = false; //See previous TODO and HHH-6354
 		}
 
 		hasInsertGeneratedValues = foundInsertGeneratedValue;
 		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
 
 		hasCascades = foundCascade;
 		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
 		versionPropertyIndex = tempVersionProperty;
 		hasLazyProperties = hasLazy;
 		if (hasLazyProperties) {
 			LOG.lazyPropertyFetchingAvailable( name );
 		}
 
 		lazy = entityBinding.isLazy() && (
 				// TODO: this disables laziness even in non-pojo entity modes:
 				! hasPojoRepresentation ||
 				! ReflectHelper.isFinalClass( proxyInterfaceClass )
 		);
 		mutable = entityBinding.isMutable();
 		if ( entityBinding.isAbstract() == null ) {
 			// legacy behavior (with no abstract attribute specified)
 			isAbstract = hasPojoRepresentation &&
 			             ReflectHelper.isAbstractClass( mappedClass );
 		}
 		else {
 			isAbstract = entityBinding.isAbstract().booleanValue();
 			if ( !isAbstract && hasPojoRepresentation &&
 					ReflectHelper.isAbstractClass( mappedClass ) ) {
 				LOG.entityMappedAsNonAbstract(name);
 			}
 		}
 		selectBeforeUpdate = entityBinding.isSelectBeforeUpdate();
 		dynamicUpdate = entityBinding.isDynamicUpdate();
 		dynamicInsert = entityBinding.isDynamicInsert();
 
 		hasSubclasses = entityBinding.hasSubEntityBindings();
 		polymorphic = entityBinding.isPolymorphic();
 
 		explicitPolymorphism = entityBinding.getHierarchyDetails().isExplicitPolymorphism();
 		inherited = ! entityBinding.isRoot();
 		superclass = inherited ?
 				entityBinding.getEntity().getSuperType().getName() :
 				null;
 
 		optimisticLockStyle = entityBinding.getHierarchyDetails().getOptimisticLockStyle();
 		final boolean isAllOrDirty =
 				optimisticLockStyle == OptimisticLockStyle.ALL
 						|| optimisticLockStyle == OptimisticLockStyle.DIRTY;
 		if ( isAllOrDirty && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && isAllOrDirty ) {
 			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
 		}
 
 		hasCollections = foundCollection;
 		hasMutableProperties = foundMutable;
 
 		for ( EntityBinding subEntityBinding : entityBinding.getPostOrderSubEntityBindingClosure() ) {
 			subclassEntityNames.add( subEntityBinding.getEntity().getName() );
 			if ( subEntityBinding.getEntity().getClassReference() != null ) {
 				entityNameByInheritenceClassMap.put(
 						subEntityBinding.getEntity().getClassReference(),
 						subEntityBinding.getEntity().getName() );
 			}
 		}
 		subclassEntityNames.add( name );
 		if ( mappedClass != null ) {
 			entityNameByInheritenceClassMap.put( mappedClass, name );
 		}
 
 		entityMode = hasPojoRepresentation ? EntityMode.POJO : EntityMode.MAP;
 		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
 		Class<? extends EntityTuplizer> tuplizerClass = entityBinding.getCustomEntityTuplizerClass();
 
 		if ( tuplizerClass == null ) {
 			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, entityBinding );
 		}
 		else {
 			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClass, this, entityBinding );
 		}
 	}
 
 	private ValueInclusion determineInsertValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isInsertGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialInsertComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
 	private ValueInclusion determineInsertValueGenerationType(AttributeBinding mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isInsertGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		// TODO: fix the following when components are working (HHH-6173)
 		//else if ( mappingProperty.getValue() instanceof ComponentAttributeBinding ) {
 		//	if ( hasPartialInsertComponentGeneration( ( ComponentAttributeBinding ) mappingProperty.getValue() ) ) {
 		//		return ValueInclusion.PARTIAL;
 		//	}
 		//}
 		return ValueInclusion.NONE;
 	}
 
 	private boolean hasPartialInsertComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			Property prop = ( Property ) subProperties.next();
 			if ( prop.getGeneration() == PropertyGeneration.ALWAYS || prop.getGeneration() == PropertyGeneration.INSERT ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialInsertComponentGeneration( ( Component ) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private ValueInclusion determineUpdateValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isUpdateGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialUpdateComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
 	private ValueInclusion determineUpdateValueGenerationType(AttributeBinding mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isUpdateGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		// TODO: fix the following when components are working (HHH-6173)
 		//else if ( mappingProperty.getValue() instanceof ComponentAttributeBinding ) {
 		//	if ( hasPartialUpdateComponentGeneration( ( ComponentAttributeBinding ) mappingProperty.getValue() ) ) {
 		//		return ValueInclusion.PARTIAL;
 		//	}
 		//}
 		return ValueInclusion.NONE;
 	}
 
 	private boolean hasPartialUpdateComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			Property prop = ( Property ) subProperties.next();
 			if ( prop.getGeneration() == PropertyGeneration.ALWAYS ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialUpdateComponentGeneration( ( Component ) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private void mapPropertyToIndex(Property prop, int i) {
 		propertyIndexes.put( prop.getName(), i );
 		if ( prop.getValue() instanceof Component ) {
 			Iterator iter = ( (Component) prop.getValue() ).getPropertyIterator();
 			while ( iter.hasNext() ) {
 				Property subprop = (Property) iter.next();
 				propertyIndexes.put(
 						prop.getName() + '.' + subprop.getName(),
 						i
 					);
 			}
 		}
 	}
 
 	private void mapPropertyToIndex(Attribute attribute, int i) {
 		propertyIndexes.put( attribute.getName(), i );
 		if ( attribute.isSingular() &&
 				( ( SingularAttribute ) attribute ).getSingularAttributeType().isComponent() ) {
 			org.hibernate.metamodel.domain.Component component =
 					( org.hibernate.metamodel.domain.Component ) ( ( SingularAttribute ) attribute ).getSingularAttributeType();
 			for ( Attribute subAttribute : component.attributes() ) {
 				propertyIndexes.put(
 						attribute.getName() + '.' + subAttribute.getName(),
 						i
 					);
 			}
 		}
 	}
 
 	public EntityTuplizer getTuplizer() {
 		return entityTuplizer;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return naturalIdPropertyNumbers;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return naturalIdPropertyNumbers!=null;
 	}
 	
-	public boolean isNatrualIdentifierCached() {
+	public boolean isNaturalIdentifierCached() {
 		return hasNaturalIdentifier() && hasCacheableNaturalId;
 	}
 
 	public boolean hasImmutableNaturalId() {
 		return hasImmutableNaturalId;
 	}
 
 	public Set getSubclassEntityNames() {
 		return subclassEntityNames;
 	}
 
 	private boolean indicatesCollection(Type type) {
 		if ( type.isCollectionType() ) {
 			return true;
 		}
 		else if ( type.isComponentType() ) {
 			Type[] subtypes = ( (CompositeType) type ).getSubtypes();
 			for ( int i = 0; i < subtypes.length; i++ ) {
 				if ( indicatesCollection( subtypes[i] ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public String getRootName() {
 		return rootName;
 	}
 
 	public EntityType getEntityType() {
 		return entityType;
 	}
 
 	public IdentifierProperty getIdentifierProperty() {
 		return identifierProperty;
 	}
 
 	public int getPropertySpan() {
 		return propertySpan;
 	}
 
 	public int getVersionPropertyIndex() {
 		return versionPropertyIndex;
 	}
 
 	public VersionProperty getVersionProperty() {
 		if ( NO_VERSION_INDX == versionPropertyIndex ) {
 			return null;
 		}
 		else {
 			return ( VersionProperty ) properties[ versionPropertyIndex ];
 		}
 	}
 
 	public StandardProperty[] getProperties() {
 		return properties;
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		Integer index = getPropertyIndexOrNull(propertyName);
 		if ( index == null ) {
 			throw new HibernateException("Unable to resolve property: " + propertyName);
 		}
 		return index.intValue();
 	}
 
 	public Integer getPropertyIndexOrNull(String propertyName) {
 		return (Integer) propertyIndexes.get( propertyName );
 	}
 
 	public boolean hasCollections() {
 		return hasCollections;
 	}
 
 	public boolean hasMutableProperties() {
 		return hasMutableProperties;
 	}
 
 	public boolean hasNonIdentifierPropertyNamedId() {
 		return hasNonIdentifierPropertyNamedId;
 	}
 
 	public boolean hasLazyProperties() {
 		return hasLazyProperties;
 	}
 
 	public boolean hasCascades() {
 		return hasCascades;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	public OptimisticLockStyle getOptimisticLockStyle() {
 		return optimisticLockStyle;
 	}
 
 	public boolean isPolymorphic() {
 		return polymorphic;
 	}
 
 	public String getSuperclass() {
 		return superclass;
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	public boolean isInherited() {
 		return inherited;
 	}
 
 	public boolean hasSubclasses() {
 		return hasSubclasses;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public boolean isVersioned() {
 		return versioned;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	/**
 	 * Return the entity-name mapped to the given class within our inheritance hierarchy, if any.
 	 *
 	 * @param inheritenceClass The class for which to resolve the entity-name.
 	 * @return The mapped entity-name, or null if no such mapping was found.
 	 */
 	public String findEntityNameByEntityClass(Class inheritenceClass) {
 		return ( String ) entityNameByInheritenceClassMap.get( inheritenceClass );
 	}
 
 	@Override
     public String toString() {
 		return "EntityMetamodel(" + name + ':' + ArrayHelper.toString(properties) + ')';
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
 
 	public Type[] getPropertyTypes() {
 		return propertyTypes;
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return propertyLaziness;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return propertyUpdateability;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return propertyCheckability;
 	}
 
 	public boolean[] getNonlazyPropertyUpdateability() {
 		return nonlazyPropertyUpdateability;
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return propertyInsertability;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return insertInclusions;
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return updateInclusions;
 	}
 
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return propertyVersionability;
 	}
 
 	public CascadeStyle[] getCascadeStyles() {
 		return cascadeStyles;
 	}
 
 	public boolean hasInsertGeneratedValues() {
 		return hasInsertGeneratedValues;
 	}
 
 	public boolean hasUpdateGeneratedValues() {
 		return hasUpdateGeneratedValues;
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	/**
 	 * Whether or not this class can be lazy (ie intercepted)
 	 */
 	public boolean isInstrumented() {
 		return instrumentationMetadata.isInstrumented();
 	}
 
 	public EntityInstrumentationMetadata getInstrumentationMetadata() {
 		return instrumentationMetadata;
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdOnSingleManyToOneTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdOnSingleManyToOneTest.java
index 5e7361b32f..9427802c4c 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdOnSingleManyToOneTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdOnSingleManyToOneTest.java
@@ -1,154 +1,154 @@
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
 package org.hibernate.test.annotations.naturalid;
 
 import java.util.List;
 
 import org.jboss.logging.Logger;
 import org.junit.After;
 import org.junit.Test;
 
 import org.hibernate.Criteria;
+import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.stat.Statistics;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Test case for NaturalId annotation. See ANN-750.
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 @TestForIssue( jiraKey = "ANN-750" )
 public class NaturalIdOnSingleManyToOneTest extends BaseCoreFunctionalTestCase {
 	private static final Logger log = Logger.getLogger( NaturalIdOnSingleManyToOneTest.class );
 
 	@After
 	public void cleanupData() {
 		super.cleanupCache();
 		Session s = sessionFactory().openSession();
 		s.beginTransaction();
 		s.createQuery( "delete NaturalIdOnManyToOne" ).executeUpdate();
 		s.createQuery( "delete Citizen" ).executeUpdate();
 		s.createQuery( "delete State" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testMappingProperties() {
         log.warn("Commented out test");
 
 		ClassMetadata metaData = sessionFactory().getClassMetadata(
 				NaturalIdOnManyToOne.class
 		);
 		assertTrue(
 				"Class should have a natural key", metaData
 						.hasNaturalIdentifier()
 		);
 		int[] propertiesIndex = metaData.getNaturalIdentifierProperties();
 		assertTrue( "Wrong number of elements", propertiesIndex.length == 1 );
 	}
 
 	@Test
 	public void testManyToOneNaturalIdCached() {
 		NaturalIdOnManyToOne singleManyToOne = new NaturalIdOnManyToOne();
 		Citizen c1 = new Citizen();
 		c1.setFirstname( "Emmanuel" );
 		c1.setLastname( "Bernard" );
 		c1.setSsn( "1234" );
 
 		State france = new State();
 		france.setName( "Ile de France" );
 		c1.setState( france );
 
 		singleManyToOne.setCitizen( c1 );
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.persist( france );
 		s.persist( c1 );
 		s.persist( singleManyToOne );
 		tx.commit();
 		s.close();
 		
-		//Clear naturalId cache that was populated when putting the data
 		s.getSessionFactory().getCache().evictNaturalIdRegions();
-		
-		s = openSession();
-		tx = s.beginTransaction();
-		Criteria criteria = s.createCriteria( NaturalIdOnManyToOne.class );
-		criteria.add( Restrictions.naturalId().set( "citizen", c1 ) );
-		criteria.setCacheable( true );
-
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 		assertEquals( "NaturalId cache puts should be zero", 0, stats.getNaturalIdCachePutCount() );
 		assertEquals( "NaturalId cache hits should be zero", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "NaturalId Cache Puts", 0, stats.getNaturalIdCachePutCount() );
 		assertEquals( "NaturalId cache misses should be zero", 0, stats.getNaturalIdCacheMissCount() );
 
+		s = openSession();
+		tx = s.beginTransaction();
+		Criteria criteria = s.createCriteria( NaturalIdOnManyToOne.class );
+		criteria.add( Restrictions.naturalId().set( "citizen", c1 ) );
+		criteria.setCacheable( true );
+
 		// first query
 		List results = criteria.list();
 		assertEquals( 1, results.size() );
 		assertEquals( "NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "NaturalId Cache Misses", 1, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "NaturalId Cache Puts", 2, stats.getNaturalIdCachePutCount() ); // one for Citizen, one for NaturalIdOnManyToOne
 		assertEquals( "NaturalId Cache Queries", 1, stats.getNaturalIdQueryExecutionCount() );
 
 		// query a second time - result should be in session cache
 		criteria.list();
 		assertEquals( "NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "NaturalId Cache Misses", 1, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "NaturalId Cache Puts", 2, stats.getNaturalIdCachePutCount() );
 		assertEquals( "NaturalId Cache Queries", 1, stats.getNaturalIdQueryExecutionCount() );
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Override
     protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Citizen.class, State.class,
 				NaturalIdOnManyToOne.class
 		};
 	}
 
 	@Override
     protected void configure(Configuration cfg) {
 		cfg.setProperty( "hibernate.cache.use_query_cache", "true" );
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdTest.java
index bf8137c5d7..c3009a3a5f 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/annotations/naturalid/NaturalIdTest.java
@@ -1,347 +1,345 @@
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
 package org.hibernate.test.annotations.naturalid;
 
 import java.util.List;
 
 import org.junit.After;
 import org.junit.Test;
 
 import org.hibernate.Criteria;
 import org.hibernate.NaturalIdLoadAccess;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.stat.Statistics;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Test case for NaturalId annotation
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public class NaturalIdTest extends BaseCoreFunctionalTestCase {
 	@After
 	public void cleanupData() {
 		super.cleanupCache();
 		Session s = sessionFactory().openSession();
 		s.beginTransaction();
 		s.createQuery( "delete NaturalIdOnManyToOne" ).executeUpdate();
 		s.createQuery( "delete Citizen" ).executeUpdate();
 		s.createQuery( "delete State" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testMappingProperties() {
 		ClassMetadata metaData = sessionFactory().getClassMetadata(
 				Citizen.class
 		);
 		assertTrue(
 				"Class should have a natural key", metaData
 						.hasNaturalIdentifier()
 		);
 		int[] propertiesIndex = metaData.getNaturalIdentifierProperties();
 		assertTrue( "Wrong number of elements", propertiesIndex.length == 2 );
 	}
 
 	@Test
 	public void testNaturalIdCached() {
 		saveSomeCitizens();
 		
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		State france = this.getState( s, "Ile de France" );
 		Criteria criteria = s.createCriteria( Citizen.class );
 		criteria.add( Restrictions.naturalId().set( "ssn", "1234" ).set( "state", france ) );
 		criteria.setCacheable( true );
 		
 		this.cleanupCache();
 
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
-		assertEquals(
-				"Cache hits should be empty", 0, stats
-						.getNaturalIdCacheHitCount()
-		);
+		assertEquals( "Cache hits should be empty", 0, stats.getNaturalIdCacheHitCount() );
+		assertEquals( "Cache puts should be empty", 0, stats.getNaturalIdCachePutCount() );
 
 		// first query
 		List results = criteria.list();
 		assertEquals( 1, results.size() );
 		assertEquals( "NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "NaturalId Cache Misses", 1, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "NaturalId Cache Puts", 1, stats.getNaturalIdCachePutCount() );
 		assertEquals( "NaturalId Cache Queries", 1, stats.getNaturalIdQueryExecutionCount() );
 
 		// query a second time - result should be cached in session
 		criteria.list();
 		assertEquals( "NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "NaturalId Cache Misses", 1, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "NaturalId Cache Puts", 1, stats.getNaturalIdCachePutCount() );
 		assertEquals( "NaturalId Cache Queries", 1, stats.getNaturalIdQueryExecutionCount() );
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdLoaderNotCached() {
 		saveSomeCitizens();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		State france = this.getState( s, "Ile de France" );
 		final NaturalIdLoadAccess naturalIdLoader = s.byNaturalId( Citizen.class );
 		naturalIdLoader.using( "ssn", "1234" ).using( "state", france );
 
 		//NaturalId cache gets populated during entity loading, need to clear it out
 		this.cleanupCache();
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 		assertEquals( "NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "NaturalId Cache Misses", 0, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "NaturalId Cache Puts", 0, stats.getNaturalIdCachePutCount() );
 		assertEquals( "NaturalId Cache Queries", 0, stats.getNaturalIdQueryExecutionCount() );
 
 		// first query
 		Citizen citizen = (Citizen)naturalIdLoader.load();
 		assertNotNull( citizen );
 		assertEquals( "NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "NaturalId Cache Misses", 1, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "NaturalId Cache Puts", 1, stats.getNaturalIdCachePutCount() );
 		assertEquals( "NaturalId Cache Queries", 1, stats.getNaturalIdQueryExecutionCount() );
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdLoaderCached() {
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 		
 		assertEquals( "NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "NaturalId Cache Misses", 0, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "NaturalId Cache Puts", 0, stats.getNaturalIdCachePutCount() );
 		assertEquals( "NaturalId Cache Queries", 0, stats.getNaturalIdQueryExecutionCount() );
 
 		saveSomeCitizens();
 		
 		assertEquals( "NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "NaturalId Cache Misses", 0, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "NaturalId Cache Puts", 2, stats.getNaturalIdCachePutCount() );
 		assertEquals( "NaturalId Cache Queries", 0, stats.getNaturalIdQueryExecutionCount() );
 
 		
 		//Try NaturalIdLoadAccess after insert
 		
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		State france = this.getState( s, "Ile de France" );
 		NaturalIdLoadAccess naturalIdLoader = s.byNaturalId( Citizen.class );
 		naturalIdLoader.using( "ssn", "1234" ).using( "state", france );
 
 		//Not clearing naturalId caches, should be warm from entity loading
 		stats.clear();
 
 		// first query
 		Citizen citizen = (Citizen)naturalIdLoader.load();
 		assertNotNull( citizen );
 		assertEquals( "NaturalId Cache Hits", 1, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "NaturalId Cache Misses", 0, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "NaturalId Cache Puts", 0, stats.getNaturalIdCachePutCount() );
 		assertEquals( "NaturalId Cache Queries", 0, stats.getNaturalIdQueryExecutionCount() );
 
 		// cleanup
 		tx.rollback();
 		s.close();
 		
 		
 		//Try NaturalIdLoadAccess
 
 		s = openSession();
 		tx = s.beginTransaction();
 
 		this.cleanupCache();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 
 		// first query
 		citizen = (Citizen) s.get( Citizen.class, citizen.getId() );
 		assertNotNull( citizen );
 		assertEquals( "NaturalId Cache Hits", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "NaturalId Cache Misses", 0, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "NaturalId Cache Puts", 1, stats.getNaturalIdCachePutCount() );
 		assertEquals( "NaturalId Cache Queries", 0, stats.getNaturalIdQueryExecutionCount() );
 
 		// cleanup
 		tx.rollback();
 		s.close();
 
 		
 		//Try NaturalIdLoadAccess after load
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		france = this.getState( s, "Ile de France" );
 		naturalIdLoader = s.byNaturalId( Citizen.class );
 		naturalIdLoader.using( "ssn", "1234" ).using( "state", france );
 
 		//Not clearing naturalId caches, should be warm from entity loading
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 
 		// first query
 		citizen = (Citizen)naturalIdLoader.load();
 		assertNotNull( citizen );
 		assertEquals( "NaturalId Cache Hits", 1, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "NaturalId Cache Misses", 0, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "NaturalId Cache Puts", 0, stats.getNaturalIdCachePutCount() );
 		assertEquals( "NaturalId Cache Queries", 0, stats.getNaturalIdQueryExecutionCount() );
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdUncached() {
 		saveSomeCitizens();
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		State france = this.getState( s, "Ile de France" );
 		Criteria criteria = s.createCriteria( Citizen.class );
 		criteria.add(
 				Restrictions.naturalId().set( "ssn", "1234" ).set(
 						"state",
 						france
 				)
 		);
 		criteria.setCacheable( false );
 		
 		this.cleanupCache();
 
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 		assertEquals(
 				"Cache hits should be empty", 0, stats
 						.getNaturalIdCacheHitCount()
 		);
 
 		// first query
 		List results = criteria.list();
 		assertEquals( 1, results.size() );
 		assertEquals(
 				"Cache hits should be empty", 0, stats
 						.getNaturalIdCacheHitCount()
 		);
 		assertEquals(
 				"Query execution count should be one", 1, stats
 						.getNaturalIdQueryExecutionCount()
 		);
 
 		// query a second time - result should be cached in session
 		criteria.list();
 		assertEquals(
 				"Cache hits should be empty", 0, stats
 						.getNaturalIdCacheHitCount()
 		);
 		assertEquals(
 				"Second query should not be a miss", 1, stats
 						.getNaturalIdCacheMissCount()
 		);
 		assertEquals(
 				"Query execution count should be one", 1, stats
 						.getNaturalIdQueryExecutionCount()
 		);
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Citizen.class, State.class,
 				NaturalIdOnManyToOne.class
 		};
 	}
 
 	private void saveSomeCitizens() {
 		Citizen c1 = new Citizen();
 		c1.setFirstname( "Emmanuel" );
 		c1.setLastname( "Bernard" );
 		c1.setSsn( "1234" );
 
 		State france = new State();
 		france.setName( "Ile de France" );
 		c1.setState( france );
 
 		Citizen c2 = new Citizen();
 		c2.setFirstname( "Gavin" );
 		c2.setLastname( "King" );
 		c2.setSsn( "000" );
 		State australia = new State();
 		australia.setName( "Australia" );
 		c2.setState( australia );
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.persist( australia );
 		s.persist( france );
 		s.persist( c1 );
 		s.persist( c2 );
 		tx.commit();
 		s.close();
 	}
 
 	private State getState(Session s, String name) {
 		Criteria criteria = s.createCriteria( State.class );
 		criteria.add( Restrictions.eq( "name", name ) );
 		criteria.setCacheable( true );
 		return (State) criteria.list().get( 0 );
 	}
 
 	@Override
 	protected void configure(Configuration cfg) {
 		cfg.setProperty( "hibernate.cache.use_query_cache", "true" );
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutableentity/ImmutableEntityNaturalIdTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutableentity/ImmutableEntityNaturalIdTest.java
index d0b9633ae9..cc79c28a3b 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutableentity/ImmutableEntityNaturalIdTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/immutableentity/ImmutableEntityNaturalIdTest.java
@@ -1,175 +1,175 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.naturalid.immutableentity;
 
 import org.junit.Test;
 
 import org.hibernate.NaturalIdLoadAccess;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.stat.Statistics;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Test case for NaturalId annotation on an {@link Immutable} entity
  *
  * @author Eric Dalquist
  */
 @SuppressWarnings("unchecked")
 @TestForIssue( jiraKey = "HHH-7085" )
 public class ImmutableEntityNaturalIdTest extends BaseCoreFunctionalTestCase {
 	@Test
 	public void testMappingProperties() {
 		ClassMetadata metaData = sessionFactory().getClassMetadata(
 				Building.class
 		);
 		assertTrue(
 				"Class should have a natural key", metaData
 						.hasNaturalIdentifier()
 		);
 		int[] propertiesIndex = metaData.getNaturalIdentifierProperties();
 		assertEquals( "Wrong number of elements", 3, propertiesIndex.length );
 	}
 
 	@Test
 	public void testImmutableNaturalIdLifecycle() {
 		Statistics stats = sessionFactory().getStatistics();
 		stats.setStatisticsEnabled( true );
 		stats.clear();
 
 		assertEquals( "Cache hits should be empty", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "Cache misses should be empty", 0, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "Cache put should be empty", 0, stats.getNaturalIdCachePutCount() );
 		assertEquals( "Query count should be empty", 0, stats.getNaturalIdQueryExecutionCount() );
 		
 		Building b1 = new Building();
 		b1.setName( "Computer Science" );
 		b1.setAddress( "1210 W. Dayton St." );
 		b1.setCity( "Madison" );
 		b1.setState( "WI" );
 
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		s.persist( b1 );
 		tx.commit();
 		s.close();
 
 		assertEquals( "Cache hits should be empty", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "Cache misses should be empty", 0, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "Cache put should be one after insert", 1, stats.getNaturalIdCachePutCount() );
 		assertEquals( "Query count should be empty", 0, stats.getNaturalIdQueryExecutionCount() );
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		
 		//Clear caches and reset cache stats
 		s.getSessionFactory().getCache().evictNaturalIdRegions();
 		stats.clear();
 		
 		NaturalIdLoadAccess naturalIdLoader = s.byNaturalId( Building.class );
 		naturalIdLoader.using( "address", "1210 W. Dayton St." ).using( "city", "Madison" ).using( "state", "WI" );
 
 		// first query
 		Building building = (Building) naturalIdLoader.load();
 		assertNotNull( building );
 		assertEquals( "Cache hits should be empty", 0, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "Cache misses should be one after first query", 1, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "Cache put should be one after first query", 1, stats.getNaturalIdCachePutCount() );
 		assertEquals( "Query count should be one after first query", 1, stats.getNaturalIdQueryExecutionCount() );
 
 		// cleanup
 		tx.rollback();
 		s.close();
 		
 		//Try two, should be a cache hit
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		naturalIdLoader = s.byNaturalId( Building.class );
 		naturalIdLoader.using( "address", "1210 W. Dayton St." ).using( "city", "Madison" ).using( "state", "WI" );
 
 		// second query
 		building = (Building) naturalIdLoader.load();
 		assertNotNull( building );
 		assertEquals( "Cache hits should be one after second query", 1, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "Cache misses should be one after second query", 1, stats.getNaturalIdCacheMissCount() );
 		assertEquals( "Cache put should be one after second query", 1, stats.getNaturalIdCachePutCount() );
 		assertEquals( "Query count should be one after second query", 1, stats.getNaturalIdQueryExecutionCount() );
 		
 		// Try Deleting
 		s.delete( building );
 		
 		// third query
 		building = (Building) naturalIdLoader.load();
 		assertNull( building );
 		assertEquals( "Cache hits should be one after second query", 1, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "Cache misses should be two after second query", 2, stats.getNaturalIdCacheMissCount() );
-		assertEquals( "Cache put should be one after second query", 1, stats.getNaturalIdCachePutCount() );
+		assertEquals( "Cache put should be one after second query", 2, stats.getNaturalIdCachePutCount() );
 		assertEquals( "Query count should be two after second query", 2, stats.getNaturalIdQueryExecutionCount() );
 
 		// cleanup
 		tx.commit();
 		s.close();
 		
 		//Try three, should be db lookup and miss
 		
 		s = openSession();
 		tx = s.beginTransaction();
 		naturalIdLoader = s.byNaturalId( Building.class );
 		naturalIdLoader.using( "address", "1210 W. Dayton St." ).using( "city", "Madison" ).using( "state", "WI" );
 
 		// second query
 		building = (Building) naturalIdLoader.load();
 		assertNull( building );
 		assertEquals( "Cache hits should be one after third query", 1, stats.getNaturalIdCacheHitCount() );
 		assertEquals( "Cache misses should be one after third query", 3, stats.getNaturalIdCacheMissCount() );
-		assertEquals( "Cache put should be one after third query", 1, stats.getNaturalIdCachePutCount() );
+		assertEquals( "Cache put should be one after third query", 2, stats.getNaturalIdCachePutCount() );
 		assertEquals( "Query count should be one after third query", 3, stats.getNaturalIdQueryExecutionCount() );
 
 		// cleanup
 		tx.rollback();
 		s.close();
 	}
 
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[] {
 				Building.class
 		};
 	}
 
 	@Override
 	protected void configure(Configuration cfg) {
 		cfg.setProperty( "hibernate.cache.use_query_cache", "true" );
 	}
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/mutable/MutableNaturalIdTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/mutable/MutableNaturalIdTest.java
index ae3394df33..bb8783c830 100755
--- a/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/mutable/MutableNaturalIdTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/naturalid/mutable/MutableNaturalIdTest.java
@@ -1,430 +1,430 @@
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
 package org.hibernate.test.naturalid.mutable;
 
 import java.lang.reflect.Field;
 
 import org.junit.Test;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNotSame;
 import static org.junit.Assert.assertNull;
 
 /**
  * @author Gavin King
  */
 public class MutableNaturalIdTest extends BaseCoreFunctionalTestCase {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "naturalid/mutable/User.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		cfg.setProperty(Environment.USE_SECOND_LEVEL_CACHE, "true");
 		cfg.setProperty(Environment.USE_QUERY_CACHE, "true");
 		cfg.setProperty(Environment.GENERATE_STATISTICS, "true");
 	}
 
 	@Test
 	public void testCacheSynchronizationOnMutation() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User u = new User( "gavin", "hb", "secret" );
 		s.persist( u );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		u = (User) s.byId( User.class ).getReference( u.getId() );
 		u.setOrg( "ceylon" );
-		s.flush();
 		User oldNaturalId = (User) s.byNaturalId( User.class ).using( "name", "gavin" ).using( "org", "hb" ).load();
+		assertNull( oldNaturalId );
 		assertNotSame( u, oldNaturalId );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( u );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testReattachmentNaturalIdCheck() throws Throwable {
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "gavin", "hb", "secret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		Field name = u.getClass().getDeclaredField("name");
 		name.setAccessible(true);
 		name.set(u, "Gavin");
 		s = openSession();
 		s.beginTransaction();
 		try {
 			s.update( u );
 			s.getTransaction().commit();
 		}
 		catch( HibernateException expected ) {
 			s.getTransaction().rollback();
 		}
 		catch( Throwable t ) {
 			try {
 				s.getTransaction().rollback();
 			}
 			catch ( Throwable ignore ) {
 			}
 			throw t;
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( u );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNonexistentNaturalIdCache() {
 		sessionFactory().getStatistics().clear();
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		Object nullUser = s.createCriteria(User.class)
 			.add( Restrictions.naturalId()
 				.set("name", "gavin")
 				.set("org", "hb")
 			)
 			.setCacheable(true)
 			.uniqueResult();
 
 		assertNull(nullUser);
 
 		t.commit();
 		s.close();
 
 		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount(), 1 );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount(), 0 );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount(), 0 );
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		User u = new User("gavin", "hb", "secret");
 		s.persist(u);
 
 		t.commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		u = (User) s.createCriteria(User.class)
 			.add( Restrictions.naturalId()
 				.set("name", "gavin")
 				.set("org", "hb")
 			)
 			.setCacheable(true)
 			.uniqueResult();
 
 		assertNotNull(u);
 
 		t.commit();
 		s.close();
 
 		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		u = (User) s.createCriteria(User.class)
 			.add( Restrictions.naturalId()
 				.set("name", "gavin")
 				.set("org", "hb")
 			).setCacheable(true)
 			.uniqueResult();
 
 		s.delete(u);
 
 		t.commit();
 		s.close();
 
 		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() ); //0: incorrect stats since hbm.xml can't enable NaturalId caching
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		nullUser = s.createCriteria(User.class)
 			.add( Restrictions.naturalId()
 				.set("name", "gavin")
 				.set("org", "hb")
 			)
 			.setCacheable(true)
 			.uniqueResult();
 
 		assertNull(nullUser);
 
 		t.commit();
 		s.close();
 
 		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() );
 	}
 
 	@Test
 	public void testNaturalIdCache() {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		User u = new User( "gavin", "hb", "secret" );
 		s.persist( u );
 		t.commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		t = s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set( "name", "gavin" )
 						.set( "org", "hb" )
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		t.commit();
 		s.close();
 
 		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		s = openSession();
 		t = s.beginTransaction();
 		User v = new User("xam", "hb", "foobar");
 		s.persist(v);
 		t.commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		t = s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "gavin")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull(u);
 		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
 
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "gavin")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull(u);
 		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		s.createQuery("delete User").executeUpdate();
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdDeleteUsingCache() {
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "hb", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "steve")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		s.getTransaction().commit();
 		s.close();
 
 		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "steve")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() ); //0: incorrect stats since hbm.xml can't enable NaturalId caching
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		s.delete( u );
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "steve")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNull( u );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdRecreateUsingCache() {
 		testNaturalIdDeleteUsingCache();
 
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "hb", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "steve")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 
 		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() );
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCachePutCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		sessionFactory().getStatistics().clear();
 		s.getTransaction().commit();
 		s.close();
 		
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId()
 						.set("name", "steve")
 						.set("org", "hb")
 				)
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		assertEquals( 1, sessionFactory().getStatistics().getNaturalIdQueryExecutionCount() ); //0: incorrect stats since hbm.xml can't enable NaturalId caching
 		assertEquals( 0, sessionFactory().getStatistics().getNaturalIdCacheHitCount() ); //1: no stats since hbm.xml can't enable NaturalId caching
 
 		s.delete( u );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQuerying() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		User u = new User("emmanuel", "hb", "bh");
 		s.persist(u);
 
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 
 		u = (User) s.createQuery( "from User u where u.name = :name" )
 			.setParameter( "name", "emmanuel" ).uniqueResult();
 		assertEquals( "emmanuel", u.getName() );
 		s.delete( u );
 
 		t.commit();
 		s.close();
 	}
 }
 
