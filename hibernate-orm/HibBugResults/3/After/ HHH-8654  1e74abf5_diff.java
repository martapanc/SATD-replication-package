diff --git a/hibernate-core/src/main/java/org/hibernate/EmptySessionEventsListener.java b/hibernate-core/src/main/java/org/hibernate/BaseSessionEventListener.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/EmptySessionEventsListener.java
rename to hibernate-core/src/main/java/org/hibernate/BaseSessionEventListener.java
index 1ba1a51666..9ef723dc72 100644
--- a/hibernate-core/src/main/java/org/hibernate/EmptySessionEventsListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/BaseSessionEventListener.java
@@ -1,121 +1,121 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
- * A no-op implementation of SessionEventsListener.  Intended as a convenient base class for developing
- * SessionEventsListener implementations.
+ * A no-op implementation of SessionEventListener.  Intended as a convenient base class for developing
+ * SessionEventListener implementations.
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings("UnusedDeclaration")
-public class EmptySessionEventsListener implements SessionEventsListener {
+public class BaseSessionEventListener implements SessionEventListener {
 	@Override
 	public void transactionCompletion(boolean successful) {
 	}
 
 	@Override
 	public void jdbcConnectionAcquisitionStart() {
 	}
 
 	@Override
 	public void jdbcConnectionAcquisitionEnd() {
 	}
 
 	@Override
 	public void jdbcConnectionReleaseStart() {
 	}
 
 	@Override
 	public void jdbcConnectionReleaseEnd() {
 	}
 
 	@Override
 	public void jdbcPrepareStatementStart() {
 	}
 
 	@Override
 	public void jdbcPrepareStatementEnd() {
 	}
 
 	@Override
 	public void jdbcExecuteStatementStart() {
 	}
 
 	@Override
 	public void jdbcExecuteStatementEnd() {
 	}
 
 	@Override
 	public void jdbcExecuteBatchStart() {
 	}
 
 	@Override
 	public void jdbcExecuteBatchEnd() {
 	}
 
 	@Override
 	public void cachePutStart() {
 	}
 
 	@Override
 	public void cachePutEnd() {
 	}
 
 	@Override
 	public void cacheGetStart() {
 	}
 
 	@Override
 	public void cacheGetEnd(boolean hit) {
 	}
 
 	@Override
 	public void flushStart() {
 	}
 
 	@Override
 	public void flushEnd(int numberOfEntities, int numberOfCollections) {
 	}
 
 	@Override
 	public void partialFlushStart() {
 	}
 
 	@Override
 	public void partialFlushEnd(int numberOfEntities, int numberOfCollections) {
 	}
 
 	@Override
 	public void dirtyCalculationStart() {
 	}
 
 	@Override
 	public void dirtyCalculationEnd(boolean dirty) {
 	}
 
 	@Override
 	public void end() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/Session.java b/hibernate-core/src/main/java/org/hibernate/Session.java
index 1832bbf45c..383cf855f4 100644
--- a/hibernate-core/src/main/java/org/hibernate/Session.java
+++ b/hibernate-core/src/main/java/org/hibernate/Session.java
@@ -107,1002 +107,1002 @@ public interface Session extends SharedSessionContract {
 	 * store with persistable state held in memory.
 	 *
 	 * @throws HibernateException Indicates problems flushing the session or
 	 * talking to the database.
 	 */
 	public void flush() throws HibernateException;
 
 	/**
 	 * Set the flush mode for this session.
 	 * <p/>
 	 * The flush mode determines the points at which the session is flushed.
 	 * <i>Flushing</i> is the process of synchronizing the underlying persistent
 	 * store with persistable state held in memory.
 	 * <p/>
 	 * For a logically "read only" session, it is reasonable to set the session's
 	 * flush mode to {@link FlushMode#MANUAL} at the start of the session (in
 	 * order to achieve some extra performance).
 	 *
 	 * @param flushMode the new flush mode
 	 * @see FlushMode
 	 */
 	public void setFlushMode(FlushMode flushMode);
 
 	/**
 	 * Get the current flush mode for this session.
 	 *
 	 * @return The flush mode
 	 */
 	public FlushMode getFlushMode();
 
 	/**
 	 * Set the cache mode.
 	 * <p/>
 	 * Cache mode determines the manner in which this session can interact with
 	 * the second level cache.
 	 *
 	 * @param cacheMode The new cache mode.
 	 */
 	public void setCacheMode(CacheMode cacheMode);
 
 	/**
 	 * Get the current cache mode.
 	 *
 	 * @return The current cache mode.
 	 */
 	public CacheMode getCacheMode();
 
 	/**
 	 * Get the session factory which created this session.
 	 *
 	 * @return The session factory.
 	 * @see SessionFactory
 	 */
 	public SessionFactory getSessionFactory();
 
 	/**
 	 * End the session by releasing the JDBC connection and cleaning up.  It is
 	 * not strictly necessary to close the session but you must at least
 	 * {@link #disconnect()} it.
 	 *
 	 * @return the connection provided by the application or null.
 	 * @throws HibernateException Indicates problems cleaning up.
 	 */
 	public Connection close() throws HibernateException;
 
 	/**
 	 * Cancel the execution of the current query.
 	 * <p/>
 	 * This is the sole method on session which may be safely called from
 	 * another thread.
 	 *
 	 * @throws HibernateException There was a problem canceling the query
 	 */
 	public void cancelQuery() throws HibernateException;
 
 	/**
 	 * Check if the session is still open.
 	 *
 	 * @return boolean
 	 */
 	public boolean isOpen();
 
 	/**
 	 * Check if the session is currently connected.
 	 *
 	 * @return boolean
 	 */
 	public boolean isConnected();
 
 	/**
 	 * Does this session contain any changes which must be synchronized with
 	 * the database?  In other words, would any DML operations be executed if
 	 * we flushed this session?
 	 *
 	 * @return True if the session contains pending changes; false otherwise.
 	 * @throws HibernateException could not perform dirtying checking
 	 */
 	public boolean isDirty() throws HibernateException;
 
 	/**
 	 * Will entities and proxies that are loaded into this session be made 
 	 * read-only by default?
 	 *
 	 * To determine the read-only/modifiable setting for a particular entity 
 	 * or proxy:
 	 * @see Session#isReadOnly(Object)
 	 *
 	 * @return true, loaded entities/proxies will be made read-only by default; 
 	 *         false, loaded entities/proxies will be made modifiable by default. 
 	 */
 	public boolean isDefaultReadOnly();
 
 	/**
 	 * Change the default for entities and proxies loaded into this session
 	 * from modifiable to read-only mode, or from modifiable to read-only mode.
 	 *
 	 * Read-only entities are not dirty-checked and snapshots of persistent
 	 * state are not maintained. Read-only entities can be modified, but
 	 * changes are not persisted.
 	 *
 	 * When a proxy is initialized, the loaded entity will have the same
 	 * read-only/modifiable setting as the uninitialized
 	 * proxy has, regardless of the session's current setting.
 	 *
 	 * To change the read-only/modifiable setting for a particular entity
 	 * or proxy that is already in this session:
 	 * @see Session#setReadOnly(Object,boolean)
 	 *
 	 * To override this session's read-only/modifiable setting for entities
 	 * and proxies loaded by a Query:
 	 * @see Query#setReadOnly(boolean)
 	 *
 	 * @param readOnly true, the default for loaded entities/proxies is read-only;
 	 *                 false, the default for loaded entities/proxies is modifiable
 	 */
 	public void setDefaultReadOnly(boolean readOnly);
 
 	/**
 	 * Return the identifier value of the given entity as associated with this
 	 * session.  An exception is thrown if the given entity instance is transient
 	 * or detached in relation to this session.
 	 *
 	 * @param object a persistent instance
 	 * @return the identifier
 	 * @throws TransientObjectException if the instance is transient or associated with
 	 * a different session
 	 */
 	public Serializable getIdentifier(Object object);
 
 	/**
 	 * Check if this instance is associated with this <tt>Session</tt>.
 	 *
 	 * @param object an instance of a persistent class
 	 * @return true if the given instance is associated with this <tt>Session</tt>
 	 */
 	public boolean contains(Object object);
 
 	/**
 	 * Remove this instance from the session cache. Changes to the instance will
 	 * not be synchronized with the database. This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="evict"</tt>.
 	 *
 	 * @param object The entity to evict
 	 *
 	 * @throws NullPointerException if the passed object is {@code null}
 	 * @throws IllegalArgumentException if the passed object is not defined as an entity
 	 */
 	public void evict(Object object);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param theClass a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockMode the lock level
 	 *
 	 * @return the persistent instance or proxy
 	 *
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
 	public Object load(Class theClass, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param theClass a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockOptions contains the lock level
 	 * @return the persistent instance or proxy
 	 */
 	public Object load(Class theClass, Serializable id, LockOptions lockOptions);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param entityName a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockMode the lock level
 	 *
 	 * @return the persistent instance or proxy
 	 *
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
 	public Object load(String entityName, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param entityName a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockOptions contains the lock level
 	 *
 	 * @return the persistent instance or proxy
 	 */
 	public Object load(String entityName, Serializable id, LockOptions lockOptions);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * assuming that the instance exists. This method might return a proxied instance that
 	 * is initialized on-demand, when a non-identifier method is accessed.
 	 * <br><br>
 	 * You should not use this method to determine if an instance exists (use <tt>get()</tt>
 	 * instead). Use this only to retrieve an instance that you assume exists, where non-existence
 	 * would be an actual error.
 	 *
 	 * @param theClass a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 *
 	 * @return the persistent instance or proxy
 	 */
 	public Object load(Class theClass, Serializable id);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * assuming that the instance exists. This method might return a proxied instance that
 	 * is initialized on-demand, when a non-identifier method is accessed.
 	 * <br><br>
 	 * You should not use this method to determine if an instance exists (use <tt>get()</tt>
 	 * instead). Use this only to retrieve an instance that you assume exists, where non-existence
 	 * would be an actual error.
 	 *
 	 * @param entityName a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 *
 	 * @return the persistent instance or proxy
 	 */
 	public Object load(String entityName, Serializable id);
 
 	/**
 	 * Read the persistent state associated with the given identifier into the given transient
 	 * instance.
 	 *
 	 * @param object an "empty" instance of the persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 */
 	public void load(Object object, Serializable id);
 
 	/**
 	 * Persist the state of the given detached instance, reusing the current
 	 * identifier value.  This operation cascades to associated instances if
 	 * the association is mapped with {@code cascade="replicate"}
 	 *
 	 * @param object a detached instance of a persistent class
 	 * @param replicationMode The replication mode to use
 	 */
 	public void replicate(Object object, ReplicationMode replicationMode);
 
 	/**
 	 * Persist the state of the given detached instance, reusing the current
 	 * identifier value.  This operation cascades to associated instances if
 	 * the association is mapped with {@code cascade="replicate"}
 	 *
 	 * @param entityName The entity name
 	 * @param object a detached instance of a persistent class
 	 * @param replicationMode The replication mode to use
 	 */
 	public void replicate(String entityName, Object object, ReplicationMode replicationMode) ;
 
 	/**
 	 * Persist the given transient instance, first assigning a generated identifier. (Or
 	 * using the current value of the identifier property if the <tt>assigned</tt>
 	 * generator is used.) This operation cascades to associated instances if the
 	 * association is mapped with {@code cascade="save-update"}
 	 *
 	 * @param object a transient instance of a persistent class
 	 *
 	 * @return the generated identifier
 	 */
 	public Serializable save(Object object);
 
 	/**
 	 * Persist the given transient instance, first assigning a generated identifier. (Or
 	 * using the current value of the identifier property if the <tt>assigned</tt>
 	 * generator is used.)  This operation cascades to associated instances if the
 	 * association is mapped with {@code cascade="save-update"}
 	 *
 	 * @param entityName The entity name
 	 * @param object a transient instance of a persistent class
 	 *
 	 * @return the generated identifier
 	 */
 	public Serializable save(String entityName, Object object);
 
 	/**
 	 * Either {@link #save(Object)} or {@link #update(Object)} the given
 	 * instance, depending upon resolution of the unsaved-value checks (see the
 	 * manual for discussion of unsaved-value checking).
 	 * <p/>
 	 * This operation cascades to associated instances if the association is mapped
 	 * with {@code cascade="save-update"}
 	 *
 	 * @param object a transient or detached instance containing new or updated state
 	 *
 	 * @see Session#save(java.lang.Object)
 	 * @see Session#update(Object object)
 	 */
 	public void saveOrUpdate(Object object);
 
 	/**
 	 * Either {@link #save(String, Object)} or {@link #update(String, Object)}
 	 * the given instance, depending upon resolution of the unsaved-value checks
 	 * (see the manual for discussion of unsaved-value checking).
 	 * <p/>
 	 * This operation cascades to associated instances if the association is mapped
 	 * with {@code cascade="save-update"}
 	 *
 	 * @param entityName The entity name
 	 * @param object a transient or detached instance containing new or updated state
 	 *
 	 * @see Session#save(String,Object)
 	 * @see Session#update(String,Object)
 	 */
 	public void saveOrUpdate(String entityName, Object object);
 
 	/**
 	 * Update the persistent instance with the identifier of the given detached
 	 * instance. If there is a persistent instance with the same identifier,
 	 * an exception is thrown. This operation cascades to associated instances
 	 * if the association is mapped with {@code cascade="save-update"}
 	 *
 	 * @param object a detached instance containing updated state
 	 */
 	public void update(Object object);
 
 	/**
 	 * Update the persistent instance with the identifier of the given detached
 	 * instance. If there is a persistent instance with the same identifier,
 	 * an exception is thrown. This operation cascades to associated instances
 	 * if the association is mapped with {@code cascade="save-update"}
 	 *
 	 * @param entityName The entity name
 	 * @param object a detached instance containing updated state
 	 */
 	public void update(String entityName, Object object);
 
 	/**
 	 * Copy the state of the given object onto the persistent object with the same
 	 * identifier. If there is no persistent instance currently associated with
 	 * the session, it will be loaded. Return the persistent instance. If the
 	 * given instance is unsaved, save a copy of and return it as a newly persistent
 	 * instance. The given instance does not become associated with the session.
 	 * This operation cascades to associated instances if the association is mapped
 	 * with {@code cascade="merge"}
 	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param object a detached instance with state to be copied
 	 *
 	 * @return an updated persistent instance
 	 */
 	public Object merge(Object object);
 
 	/**
 	 * Copy the state of the given object onto the persistent object with the same
 	 * identifier. If there is no persistent instance currently associated with
 	 * the session, it will be loaded. Return the persistent instance. If the
 	 * given instance is unsaved, save a copy of and return it as a newly persistent
 	 * instance. The given instance does not become associated with the session.
 	 * This operation cascades to associated instances if the association is mapped
 	 * with {@code cascade="merge"}
 	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param entityName The entity name
 	 * @param object a detached instance with state to be copied
 	 *
 	 * @return an updated persistent instance
 	 */
 	public Object merge(String entityName, Object object);
 
 	/**
 	 * Make a transient instance persistent. This operation cascades to associated
 	 * instances if the association is mapped with {@code cascade="persist"}
 	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param object a transient instance to be made persistent
 	 */
 	public void persist(Object object);
 	/**
 	 * Make a transient instance persistent. This operation cascades to associated
 	 * instances if the association is mapped with {@code cascade="persist"}
 	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param entityName The entity name
 	 * @param object a transient instance to be made persistent
 	 */
 	public void persist(String entityName, Object object);
 
 	/**
 	 * Remove a persistent instance from the datastore. The argument may be
 	 * an instance associated with the receiving <tt>Session</tt> or a transient
 	 * instance with an identifier associated with existing persistent state.
 	 * This operation cascades to associated instances if the association is mapped
 	 * with {@code cascade="delete"}
 	 *
 	 * @param object the instance to be removed
 	 */
 	public void delete(Object object);
 
 	/**
 	 * Remove a persistent instance from the datastore. The <b>object</b> argument may be
 	 * an instance associated with the receiving <tt>Session</tt> or a transient
 	 * instance with an identifier associated with existing persistent state.
 	 * This operation cascades to associated instances if the association is mapped
 	 * with {@code cascade="delete"}
 	 *
 	 * @param entityName The entity name for the instance to be removed.
 	 * @param object the instance to be removed
 	 */
 	public void delete(String entityName, Object object);
 
 	/**
 	 * Obtain the specified lock level upon the given object. This may be used to
 	 * perform a version check (<tt>LockMode.READ</tt>), to upgrade to a pessimistic
 	 * lock (<tt>LockMode.PESSIMISTIC_WRITE</tt>), or to simply reassociate a transient instance
 	 * with a session (<tt>LockMode.NONE</tt>). This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="lock"</tt>.
 	 *
 	 * @param object a persistent or transient instance
 	 * @param lockMode the lock level
 	 *
 	 * @deprecated instead call buildLockRequest(LockMode).lock(object)
 	 */
 	@Deprecated
 	public void lock(Object object, LockMode lockMode);
 
 	/**
 	 * Obtain the specified lock level upon the given object. This may be used to
 	 * perform a version check (<tt>LockMode.OPTIMISTIC</tt>), to upgrade to a pessimistic
 	 * lock (<tt>LockMode.PESSIMISTIC_WRITE</tt>), or to simply reassociate a transient instance
 	 * with a session (<tt>LockMode.NONE</tt>). This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="lock"</tt>.
 	 *
 	 * @param entityName The name of the entity
 	 * @param object a persistent or transient instance
 	 * @param lockMode the lock level
 	 *
 	 * @deprecated instead call buildLockRequest(LockMode).lock(entityName, object)
 	 */
 	@SuppressWarnings( {"JavaDoc"})
 	@Deprecated
 	public void lock(String entityName, Object object, LockMode lockMode);
 
 	/**
 	 * Build a LockRequest that specifies the LockMode, pessimistic lock timeout and lock scope.
 	 * timeout and scope is ignored for optimistic locking.  After building the LockRequest,
 	 * call LockRequest.lock to perform the requested locking. 
 	 * <p/>
 	 * Example usage:
 	 * {@code session.buildLockRequest().setLockMode(LockMode.PESSIMISTIC_WRITE).setTimeOut(60000).lock(entity);}
 	 *
 	 * @param lockOptions contains the lock level
 	 *
 	 * @return a lockRequest that can be used to lock the passed object.
 	 */
 	public LockRequest buildLockRequest(LockOptions lockOptions);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database. It is
 	 * inadvisable to use this to implement long-running sessions that span many
 	 * business tasks. This method is, however, useful in certain special circumstances.
 	 * For example
 	 * <ul>
 	 * <li>where a database trigger alters the object state upon insert or update
 	 * <li>after executing direct SQL (eg. a mass update) in the same session
 	 * <li>after inserting a <tt>Blob</tt> or <tt>Clob</tt>
 	 * </ul>
 	 *
 	 * @param object a persistent or detached instance
 	 */
 	public void refresh(Object object);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database. It is
 	 * inadvisable to use this to implement long-running sessions that span many
 	 * business tasks. This method is, however, useful in certain special circumstances.
 	 * For example
 	 * <ul>
 	 * <li>where a database trigger alters the object state upon insert or update
 	 * <li>after executing direct SQL (eg. a mass update) in the same session
 	 * <li>after inserting a <tt>Blob</tt> or <tt>Clob</tt>
 	 * </ul>
 	 *
 	 * @param entityName a persistent class
 	 * @param object a persistent or detached instance
 	 */
 	public void refresh(String entityName, Object object);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
 	 *
 	 * @param object a persistent or detached instance
 	 * @param lockMode the lock mode to use
 	 *
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
 	public void refresh(Object object, LockMode lockMode);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
 	 *
 	 * @param object a persistent or detached instance
 	 * @param lockOptions contains the lock mode to use
 	 */
 	public void refresh(Object object, LockOptions lockOptions);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
 	 *
 	 * @param entityName a persistent class
 	 * @param object a persistent or detached instance
 	 * @param lockOptions contains the lock mode to use
 	 */
 	public void refresh(String entityName, Object object, LockOptions lockOptions);
 
 	/**
 	 * Determine the current lock mode of the given object.
 	 *
 	 * @param object a persistent instance
 	 *
 	 * @return the current lock mode
 	 */
 	public LockMode getCurrentLockMode(Object object);
 
 	/**
 	 * Create a {@link Query} instance for the given collection and filter string.  Contains an implicit {@code FROM}
 	 * element named {@code this} which refers to the defined table for the collection elements, as well as an implicit
 	 * {@code WHERE} restriction for this particular collection instance's key value.
 	 *
 	 * @param collection a persistent collection
 	 * @param queryString a Hibernate query fragment.
 	 *
 	 * @return The query instance for manipulation and execution
 	 */
 	public Query createFilter(Object collection, String queryString);
 
 	/**
 	 * Completely clear the session. Evict all loaded instances and cancel all pending
 	 * saves, updates and deletions. Do not close open iterators or instances of
 	 * <tt>ScrollableResults</tt>.
 	 */
 	public void clear();
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 *
 	 * @param clazz a persistent class
 	 * @param id an identifier
 	 *
 	 * @return a persistent instance or null
 	 */
 	public Object get(Class clazz, Serializable id);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param clazz a persistent class
 	 * @param id an identifier
 	 * @param lockMode the lock mode
 	 *
 	 * @return a persistent instance or null
 	 *
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
 	public Object get(Class clazz, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param clazz a persistent class
 	 * @param id an identifier
 	 * @param lockOptions the lock mode
 	 *
 	 * @return a persistent instance or null
 	 */
 	public Object get(Class clazz, Serializable id, LockOptions lockOptions);
 
 	/**
 	 * Return the persistent instance of the given named entity with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 *
 	 * @param entityName the entity name
 	 * @param id an identifier
 	 *
 	 * @return a persistent instance or null
 	 */
 	public Object get(String entityName, Serializable id);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param entityName the entity name
 	 * @param id an identifier
 	 * @param lockMode the lock mode
 	 *
 	 * @return a persistent instance or null
 	 *
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
 	public Object get(String entityName, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param entityName the entity name
 	 * @param id an identifier
 	 * @param lockOptions contains the lock mode
 	 *
 	 * @return a persistent instance or null
 	 */
 	public Object get(String entityName, Serializable id, LockOptions lockOptions);
 
 	/**
 	 * Return the entity name for a persistent entity.
 	 *   
 	 * @param object a persistent entity
 	 *
 	 * @return the entity name
 	 */
 	public String getEntityName(Object object);
 	
 	/**
 	 * Create an {@link IdentifierLoadAccess} instance to retrieve the specified entity type by
 	 * primary key.
 	 * 
 	 * @param entityName The entity name of the entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by primary key
 	 *
 	 * @throws HibernateException If the specified entity name cannot be resolved as an entity name
 	 */
 	public IdentifierLoadAccess byId(String entityName);
 
 	/**
 	 * Create an {@link IdentifierLoadAccess} instance to retrieve the specified entity by
 	 * primary key.
 	 *
 	 * @param entityClass The entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by primary key
 	 *
 	 * @throws HibernateException If the specified Class cannot be resolved as a mapped entity
 	 */
 	public IdentifierLoadAccess byId(Class entityClass);
 
 	/**
 	 * Create an {@link NaturalIdLoadAccess} instance to retrieve the specified entity by
 	 * its natural id.
 	 * 
 	 * @param entityName The entity name of the entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by natural id
 	 *
 	 * @throws HibernateException If the specified entity name cannot be resolved as an entity name
 	 */
 	public NaturalIdLoadAccess byNaturalId(String entityName);
 
 	/**
 	 * Create an {@link NaturalIdLoadAccess} instance to retrieve the specified entity by
 	 * its natural id.
 	 * 
 	 * @param entityClass The entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by natural id
 	 *
 	 * @throws HibernateException If the specified Class cannot be resolved as a mapped entity
 	 */
 	public NaturalIdLoadAccess byNaturalId(Class entityClass);
 
 	/**
 	 * Create an {@link SimpleNaturalIdLoadAccess} instance to retrieve the specified entity by
 	 * its natural id.
 	 *
 	 * @param entityName The entity name of the entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by natural id
 	 *
 	 * @throws HibernateException If the specified entityClass cannot be resolved as a mapped entity, or if the
 	 * entity does not define a natural-id or if its natural-id is made up of multiple attributes.
 	 */
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName);
 
 	/**
 	 * Create an {@link SimpleNaturalIdLoadAccess} instance to retrieve the specified entity by
 	 * its simple (single attribute) natural id.
 	 *
 	 * @param entityClass The entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by natural id
 	 *
 	 * @throws HibernateException If the specified entityClass cannot be resolved as a mapped entity, or if the
 	 * entity does not define a natural-id or if its natural-id is made up of multiple attributes.
 	 */
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(Class entityClass);
 
 	/**
 	 * Enable the named filter for this current session.
 	 *
 	 * @param filterName The name of the filter to be enabled.
 	 *
 	 * @return The Filter instance representing the enabled filter.
 	 */
 	public Filter enableFilter(String filterName);
 
 	/**
 	 * Retrieve a currently enabled filter by name.
 	 *
 	 * @param filterName The name of the filter to be retrieved.
 	 *
 	 * @return The Filter instance representing the enabled filter.
 	 */
 	public Filter getEnabledFilter(String filterName);
 
 	/**
 	 * Disable the named filter for the current session.
 	 *
 	 * @param filterName The name of the filter to be disabled.
 	 */
 	public void disableFilter(String filterName);
 	
 	/**
 	 * Get the statistics for this session.
 	 *
 	 * @return The session statistics being collected for this session
 	 */
 	public SessionStatistics getStatistics();
 
 	/**
 	 * Is the specified entity or proxy read-only?
 	 *
 	 * To get the default read-only/modifiable setting used for
 	 * entities and proxies that are loaded into the session:
 	 * @see org.hibernate.Session#isDefaultReadOnly()
 	 *
 	 * @param entityOrProxy an entity or HibernateProxy
 	 * @return {@code true} if the entity or proxy is read-only, {@code false} if the entity or proxy is modifiable.
 	 */
 	public boolean isReadOnly(Object entityOrProxy);
 
 	/**
 	 * Set an unmodified persistent object to read-only mode, or a read-only
 	 * object to modifiable mode. In read-only mode, no snapshot is maintained,
 	 * the instance is never dirty checked, and changes are not persisted.
 	 *
 	 * If the entity or proxy already has the specified read-only/modifiable
 	 * setting, then this method does nothing.
 	 * 
 	 * To set the default read-only/modifiable setting used for
 	 * entities and proxies that are loaded into the session:
 	 * @see org.hibernate.Session#setDefaultReadOnly(boolean)
 	 *
 	 * To override this session's read-only/modifiable setting for entities
 	 * and proxies loaded by a Query:
 	 * @see Query#setReadOnly(boolean)
 	 * 
 	 * @param entityOrProxy an entity or HibernateProxy
 	 * @param readOnly {@code true} if the entity or proxy should be made read-only; {@code false} if the entity or
 	 * proxy should be made modifiable
 	 */
 	public void setReadOnly(Object entityOrProxy, boolean readOnly);
 
 	/**
 	 * Controller for allowing users to perform JDBC related work using the Connection managed by this Session.
 	 *
 	 * @param work The work to be performed.
 	 * @throws HibernateException Generally indicates wrapped {@link java.sql.SQLException}
 	 */
 	public void doWork(Work work) throws HibernateException;
 
 	/**
 	 * Controller for allowing users to perform JDBC related work using the Connection managed by this Session.  After
 	 * execution returns the result of the {@link ReturningWork#execute} call.
 	 *
 	 * @param work The work to be performed.
 	 * @param <T> The type of the result returned from the work
 	 *
 	 * @return the result from calling {@link ReturningWork#execute}.
 	 *
 	 * @throws HibernateException Generally indicates wrapped {@link java.sql.SQLException}
 	 */
 	public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException;
 
 	/**
 	 * Disconnect the session from its underlying JDBC connection.  This is intended for use in cases where the
 	 * application has supplied the JDBC connection to the session and which require long-sessions (aka, conversations).
 	 * <p/>
 	 * It is considered an error to call this method on a session which was not opened by supplying the JDBC connection
 	 * and an exception will be thrown.
 	 * <p/>
 	 * For non-user-supplied scenarios, normal transaction management already handles disconnection and reconnection
 	 * automatically.
 	 *
 	 * @return the application-supplied connection or {@code null}
 	 *
 	 * @see #reconnect(Connection)
 	 */
 	Connection disconnect();
 
 	/**
 	 * Reconnect to the given JDBC connection.
 	 *
 	 * @param connection a JDBC connection
 	 * 
 	 * @see #disconnect()
 	 */
 	void reconnect(Connection connection);
 
 	/**
 	 * Is a particular fetch profile enabled on this session?
 	 *
 	 * @param name The name of the profile to be checked.
 	 * @return True if fetch profile is enabled; false if not.
 	 * @throws UnknownProfileException Indicates that the given name does not
 	 * match any known profile names
 	 *
 	 * @see org.hibernate.engine.profile.FetchProfile for discussion of this feature
 	 */
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException;
 
 	/**
 	 * Enable a particular fetch profile on this session.  No-op if requested
 	 * profile is already enabled.
 	 *
 	 * @param name The name of the fetch profile to be enabled.
 	 * @throws UnknownProfileException Indicates that the given name does not
 	 * match any known profile names
 	 *
 	 * @see org.hibernate.engine.profile.FetchProfile for discussion of this feature
 	 */
 	public void enableFetchProfile(String name) throws UnknownProfileException;
 
 	/**
 	 * Disable a particular fetch profile on this session.  No-op if requested
 	 * profile is already disabled.
 	 *
 	 * @param name The name of the fetch profile to be disabled.
 	 * @throws UnknownProfileException Indicates that the given name does not
 	 * match any known profile names
 	 *
 	 * @see org.hibernate.engine.profile.FetchProfile for discussion of this feature
 	 */
 	public void disableFetchProfile(String name) throws UnknownProfileException;
 
 	/**
 	 * Convenience access to the {@link TypeHelper} associated with this session's {@link SessionFactory}.
 	 * <p/>
 	 * Equivalent to calling {@link #getSessionFactory()}.{@link SessionFactory#getTypeHelper getTypeHelper()}
 	 *
 	 * @return The {@link TypeHelper} associated with this session's {@link SessionFactory}
 	 */
 	public TypeHelper getTypeHelper();
 
 	/**
 	 * Retrieve this session's helper/delegate for creating LOB instances.
 	 *
 	 * @return This session's LOB helper
 	 */
 	public LobHelper getLobHelper();
 
 	/**
 	 * Contains locking details (LockMode, Timeout and Scope).
 	 */
 	public interface LockRequest {
 		/**
 		 * Constant usable as a time out value that indicates no wait semantics should be used in
 		 * attempting to acquire locks.
 		 */
 		static final int PESSIMISTIC_NO_WAIT = 0;
 		/**
 		 * Constant usable as a time out value that indicates that attempting to acquire locks should be allowed to
 		 * wait forever (apply no timeout).
 		 */
 		static final int PESSIMISTIC_WAIT_FOREVER = -1;
 
 		/**
 		 * Get the lock mode.
 		 *
 		 * @return the lock mode.
 		 */
 		LockMode getLockMode();
 
 		/**
 		 * Specify the LockMode to be used.  The default is LockMode.none.
 		 *
 		 * @param lockMode The lock mode to use for this request
 		 *
 		 * @return this LockRequest instance for operation chaining.
 		 */
 		LockRequest setLockMode(LockMode lockMode);
 
 		/**
 		 * Get the timeout setting.
 		 *
 		 * @return timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 		 */
 		int getTimeOut();
 
 		/**
 		 * Specify the pessimistic lock timeout (check if your dialect supports this option).
 		 * The default pessimistic lock behavior is to wait forever for the lock.
 		 *
 		 * @param timeout is time in milliseconds to wait for lock.  -1 means wait forever and 0 means no wait.
 		 *
 		 * @return this LockRequest instance for operation chaining.
 		 */
 		LockRequest setTimeOut(int timeout);
 
 		/**
 		 * Check if locking is cascaded to owned collections and relationships.
 		 *
 		 * @return true if locking will be extended to owned collections and relationships.
 		 */
 		boolean getScope();
 
 		/**
 		 * Specify if LockMode should be cascaded to owned collections and relationships.
 		 * The association must be mapped with {@code cascade="lock"} for scope=true to work.
 		 *
 		 * @param scope {@code true} to cascade locks; {@code false} to not.
 		 *
 		 * @return {@code this}, for method chaining
 		 */
 		LockRequest setScope(boolean scope);
 
 		/**
 		 * Perform the requested locking.
 		 *
 		 * @param entityName The name of the entity to lock
 		 * @param object The instance of the entity to lock
 		 */
 		void lock(String entityName, Object object);
 
 		/**
 		 * Perform the requested locking.
 		 *
 		 * @param object The instance of the entity to lock
 		 */
 		void lock(Object object);
 	}
 
 	/**
 	 * Add one or more listeners to the Session
 	 *
 	 * @param listeners The listener(s) to add
 	 */
-	public void addEventsListeners(SessionEventsListener... listeners);
+	public void addEventListeners(SessionEventListener... listeners);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/SessionBuilder.java b/hibernate-core/src/main/java/org/hibernate/SessionBuilder.java
index ea7b09fc07..7cb90fa0c6 100644
--- a/hibernate-core/src/main/java/org/hibernate/SessionBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/SessionBuilder.java
@@ -1,137 +1,137 @@
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
 package org.hibernate;
 
 import java.sql.Connection;
 
 /**
  * Represents a consolidation of all session creation options into a builder style delegate.
  * 
  * @author Steve Ebersole
  */
 public interface SessionBuilder {
 	/**
 	 * Opens a session with the specified options.
 	 *
 	 * @return The session
 	 */
 	public Session openSession();
 
 	/**
 	 * Adds a specific interceptor to the session options.
 	 *
 	 * @param interceptor The interceptor to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionBuilder interceptor(Interceptor interceptor);
 
 	/**
 	 * Signifies that no {@link Interceptor} should be used.
 	 * <p/>
 	 * By default the {@link Interceptor} associated with the {@link SessionFactory} is passed to the
 	 * {@link Session} whenever we open one without the user having specified a specific interceptor to
 	 * use.
 	 * <p/>
 	 * Calling {@link #interceptor(Interceptor)} with null has the same net effect.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionBuilder noInterceptor();
 
 	/**
 	 * Adds a specific connection to the session options.
 	 *
 	 * @param connection The connection to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionBuilder connection(Connection connection);
 
 	/**
 	 * Use a specific connection release mode for these session options.
 	 *
 	 * @param connectionReleaseMode The connection release mode to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionBuilder connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode);
 
 	/**
 	 * Should the session built automatically join in any ongoing JTA transactions.
 	 *
 	 * @param autoJoinTransactions Should JTA transactions be automatically joined
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionBuilder autoJoinTransactions(boolean autoJoinTransactions);
 
 	/**
 	 * Should the session be automatically closed after transaction completion.
 	 *
 	 * @param autoClose Should the session be automatically closed
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @deprecated Only integrations can specify autoClosing behavior of individual sessions.  See
 	 * {@link org.hibernate.engine.spi.SessionOwner}
 	 */
 	@Deprecated
 	public SessionBuilder autoClose(boolean autoClose);
 
 	/**
 	 * Should the session be automatically flushed during the "before completion" phase of transaction handling.
 	 *
 	 * @param flushBeforeCompletion Should the session be automatically flushed
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionBuilder flushBeforeCompletion(boolean flushBeforeCompletion);
 
 	/**
 	 * Define the tenant identifier to be associated with the opened session.
 	 *
 	 * @param tenantIdentifier The tenant identifier.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SessionBuilder tenantIdentifier(String tenantIdentifier);
 
 	/**
-	 * Apply one or more SessionEventsListener instances to the listeners for the Session to be built.
+	 * Apply one or more SessionEventListener instances to the listeners for the Session to be built.
 	 *
 	 * @param listeners The listeners to incorporate into the built Session
 	 *
 	 * @return {@code this}, for method chaining
 	 */
-	public SessionBuilder eventListeners(SessionEventsListener... listeners);
+	public SessionBuilder eventListeners(SessionEventListener... listeners);
 
 	/**
 	 * Remove all listeners intended for the built Session currently held here, including any auto-apply ones; in other
 	 * words, start with a clean slate.
 	 *
 	 * {@code this}, for method chaining
 	 */
 	public SessionBuilder clearEventListeners();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/SessionEventsListener.java b/hibernate-core/src/main/java/org/hibernate/SessionEventListener.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/SessionEventsListener.java
rename to hibernate-core/src/main/java/org/hibernate/SessionEventListener.java
index ce18b70176..e8b298402f 100644
--- a/hibernate-core/src/main/java/org/hibernate/SessionEventsListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/SessionEventListener.java
@@ -1,67 +1,67 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 
 import java.io.Serializable;
 
 /**
  * NOTE : Consider this an incubating API, likely to change as wider usage indicates changes that need to be made
  *
  * @author Steve Ebersole
  */
-public interface SessionEventsListener extends Serializable {
+public interface SessionEventListener extends Serializable {
 	public void transactionCompletion(boolean successful);
 
 	public void jdbcConnectionAcquisitionStart();
 	public void jdbcConnectionAcquisitionEnd();
 
 	public void jdbcConnectionReleaseStart();
 	public void jdbcConnectionReleaseEnd();
 
 	public void jdbcPrepareStatementStart();
 	public void jdbcPrepareStatementEnd();
 
 	public void jdbcExecuteStatementStart();
 	public void jdbcExecuteStatementEnd();
 
 	public void jdbcExecuteBatchStart();
 	public void jdbcExecuteBatchEnd();
 
 	public void cachePutStart();
 	public void cachePutEnd();
 
 	public void cacheGetStart();
 	public void cacheGetEnd(boolean hit);
 
 	public void flushStart();
 	public void flushEnd(int numberOfEntities, int numberOfCollections);
 
 	public void partialFlushStart();
 	public void partialFlushEnd(int numberOfEntities, int numberOfCollections);
 
 	public void dirtyCalculationStart();
 	public void dirtyCalculationEnd(boolean dirty);
 
 	public void end();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
index 316ec56296..d84fb31c30 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityInsertAction.java
@@ -1,250 +1,250 @@
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
 
 /**
  * The action for performing an entity insertion, for entities not defined to use IDENTITY generation.
  *
  * @see EntityIdentityInsertAction
  */
 public final class EntityInsertAction extends AbstractEntityInsertAction {
 	private Object version;
 	private Object cacheEntry;
 
 	/**
 	 * Constructs an EntityInsertAction.
 	 *
 	 * @param id The entity identifier
 	 * @param state The current (extracted) entity state
 	 * @param instance The entity instance
 	 * @param version The current entity version value
 	 * @param persister The entity's persister
 	 * @param isVersionIncrementDisabled Whether version incrementing is disabled.
 	 * @param session The session
 	 */
 	public EntityInsertAction(
 			Serializable id,
 			Object[] state,
 			Object instance,
 			Object version,
 			EntityPersister persister,
 			boolean isVersionIncrementDisabled,
 			SessionImplementor session) {
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
 
 		final EntityPersister persister = getPersister();
 		final SessionImplementor session = getSession();
 		final Object instance = getInstance();
 		final Serializable id = getId();
 
 		final boolean veto = preInsert();
 
 		// Don't need to lock the cache here, since if someone
 		// else inserted the same pk first, the insert would fail
 
 		if ( !veto ) {
 			
 			persister.insert( id, getState(), instance, session );
 
 			final EntityEntry entry = session.getPersistenceContext().getEntry( instance );
 			if ( entry == null ) {
 				throw new AssertionFailure( "possible non-threadsafe access to session" );
 			}
 			
 			entry.postInsert( getState() );
 	
 			if ( persister.hasInsertGeneratedProperties() ) {
 				persister.processInsertGeneratedProperties( id, instance, getState(), session );
 				if ( persister.isVersionPropertyGenerated() ) {
 					version = Versioning.getVersion( getState(), persister );
 				}
 				entry.postUpdate( instance, getState(), version );
 			}
 
 			getSession().getPersistenceContext().registerInsertedKey( getPersister(), getId() );
 		}
 
 		final SessionFactoryImplementor factory = getSession().getFactory();
 
 		if ( isCachePutEnabled( persister, session ) ) {
 			final CacheEntry ce = persister.buildCacheEntry(
 					instance,
 					getState(),
 					version,
 					session
 			);
 			cacheEntry = persister.getCacheEntryStructure().structure( ce );
 			final CacheKey ck = session.generateCacheKey( id, persister.getIdentifierType(), persister.getRootEntityName() );
 
 			final boolean put = cacheInsert( persister, ck );
 
 			if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor().secondLevelCachePut( getPersister().getCacheAccessStrategy().getRegion().getName() );
 			}
 		}
 
 		handleNaturalIdPostSaveNotifications( id );
 
 		postInsert();
 
 		if ( factory.getStatistics().isStatisticsEnabled() && !veto ) {
 			factory.getStatisticsImplementor().insertEntity( getPersister().getEntityName() );
 		}
 
 		markExecuted();
 	}
 
 	private boolean cacheInsert(EntityPersister persister, CacheKey ck) {
 		try {
-			getSession().getSessionEventsManager().cachePutStart();
+			getSession().getEventListenerManager().cachePutStart();
 			return persister.getCacheAccessStrategy().insert( ck, cacheEntry, version );
 		}
 		finally {
-			getSession().getSessionEventsManager().cachePutEnd();
+			getSession().getEventListenerManager().cachePutEnd();
 		}
 	}
 
 	private void postInsert() {
 		final EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_INSERT );
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
 		final EventListenerGroup<PostInsertEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_INSERT );
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
 
 		final EventListenerGroup<PreInsertEventListener> listenerGroup = listenerGroup( EventType.PRE_INSERT );
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
 		final EntityPersister persister = getPersister();
 		if ( success && isCachePutEnabled( persister, getSession() ) ) {
 			final CacheKey ck = getSession().generateCacheKey( getId(), persister.getIdentifierType(), persister.getRootEntityName() );
 			try {
-				getSession().getSessionEventsManager().cachePutStart();
+				getSession().getEventListenerManager().cachePutStart();
 				final boolean put = persister.getCacheAccessStrategy().afterInsert( ck, cacheEntry, version );
 
 				if ( put && getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
 					getSession().getFactory().getStatisticsImplementor()
 							.secondLevelCachePut( getPersister().getCacheAccessStrategy().getRegion().getName() );
 				}
 			}
 			finally {
-				getSession().getSessionEventsManager().cachePutEnd();
+				getSession().getEventListenerManager().cachePutEnd();
 			}
 		}
 		postCommitInsert();
 	}
 
 	@Override
 	protected boolean hasPostCommitEventListeners() {
 		final EventListenerGroup<PostInsertEventListener> group = listenerGroup( EventType.POST_COMMIT_INSERT );
 		for ( PostInsertEventListener listener : group.listeners() ) {
 			if ( listener.requiresPostCommitHanding( getPersister() ) ) {
 				return true;
 			}
 		}
 
 		return false;
 	}
 	
 	private boolean isCachePutEnabled(EntityPersister persister, SessionImplementor session) {
 		return persister.hasCache()
 				&& !persister.isCacheInvalidationRequired()
 				&& session.getCacheMode().isPutEnabled();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
index c6abcb0f49..8cb9c8ef45 100644
--- a/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
+++ b/hibernate-core/src/main/java/org/hibernate/action/internal/EntityUpdateAction.java
@@ -1,338 +1,338 @@
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
 import org.hibernate.engine.spi.CachedNaturalIdValueSource;
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
 
 /**
  * The action for performing entity updates.
  */
 public final class EntityUpdateAction extends EntityAction {
 	private final Object[] state;
 	private final Object[] previousState;
 	private final Object previousVersion;
 	private final int[] dirtyFields;
 	private final boolean hasDirtyCollection;
 	private final Object rowId;
 	private final Object[] previousNaturalIdValues;
 	private Object nextVersion;
 	private Object cacheEntry;
 	private SoftLock lock;
 
 	/**
 	 * Constructs an EntityUpdateAction
 	 *
 	 * @param id The entity identifier
 	 * @param state The current (extracted) entity state
 	 * @param dirtyProperties The indexes (in reference to state) properties with dirty state
 	 * @param hasDirtyCollection Were any collections dirty?
 	 * @param previousState The previous (stored) state
 	 * @param previousVersion The previous (stored) version
 	 * @param nextVersion The incremented version
 	 * @param instance The entity instance
 	 * @param rowId The entity's rowid
 	 * @param persister The entity's persister
 	 * @param session The session
 	 */
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
 			final SessionImplementor session) {
 		super( session, id, instance, persister );
 		this.state = state;
 		this.previousState = previousState;
 		this.previousVersion = previousVersion;
 		this.nextVersion = nextVersion;
 		this.dirtyFields = dirtyProperties;
 		this.hasDirtyCollection = hasDirtyCollection;
 		this.rowId = rowId;
 
 		this.previousNaturalIdValues = determinePreviousNaturalIdValues( persister, previousState, session, id );
 		session.getPersistenceContext().getNaturalIdHelper().manageLocalNaturalIdCrossReference(
 				persister,
 				id,
 				state,
 				previousNaturalIdValues,
 				CachedNaturalIdValueSource.UPDATE
 		);
 	}
 
 	private Object[] determinePreviousNaturalIdValues(
 			EntityPersister persister,
 			Object[] previousState,
 			SessionImplementor session,
 			Serializable id) {
 		if ( ! persister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		if ( previousState != null ) {
 			return session.getPersistenceContext().getNaturalIdHelper().extractNaturalIdValues( previousState, persister );
 		}
 
 		return session.getPersistenceContext().getNaturalIdSnapshot( id, persister );
 	}
 
 	@Override
 	public void execute() throws HibernateException {
 		final Serializable id = getId();
 		final EntityPersister persister = getPersister();
 		final SessionImplementor session = getSession();
 		final Object instance = getInstance();
 
 		final boolean veto = preUpdate();
 
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
 
 		final EntityEntry entry = getSession().getPersistenceContext().getEntry( instance );
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
 				final CacheEntry ce = persister.buildCacheEntry( instance,state, nextVersion, getSession() );
 				cacheEntry = persister.getCacheEntryStructure().structure( ce );
 
 				try {
 
 					final boolean put = persister.getCacheAccessStrategy().update( ck, cacheEntry, nextVersion, previousVersion );
 					if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 						factory.getStatisticsImplementor().secondLevelCachePut( getPersister().getCacheAccessStrategy().getRegion().getName() );
 					}
 				}
 				finally {
 				}
 			}
 		}
 
 		session.getPersistenceContext().getNaturalIdHelper().manageSharedNaturalIdCrossReference(
 				persister,
 				id,
 				state,
 				previousNaturalIdValues,
 				CachedNaturalIdValueSource.UPDATE
 		);
 
 		postUpdate();
 
 		if ( factory.getStatistics().isStatisticsEnabled() && !veto ) {
 			factory.getStatisticsImplementor().updateEntity( getPersister().getEntityName() );
 		}
 	}
 
 	private boolean preUpdate() {
 		boolean veto = false;
 		final EventListenerGroup<PreUpdateEventListener> listenerGroup = listenerGroup( EventType.PRE_UPDATE );
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
 		final EventListenerGroup<PostUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_UPDATE );
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
 		final EventListenerGroup<PostUpdateEventListener> listenerGroup = listenerGroup( EventType.POST_COMMIT_UPDATE );
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
 		final EventListenerGroup<PostUpdateEventListener> group = listenerGroup( EventType.POST_COMMIT_UPDATE );
 		for ( PostUpdateEventListener listener : group.listeners() ) {
 			if ( listener.requiresPostCommitHanding( getPersister() ) ) {
 				return true;
 			}
 		}
 
 		return false;
 	}
 
 	@Override
 	public void doAfterTransactionCompletion(boolean success, SessionImplementor session) throws CacheException {
 		final EntityPersister persister = getPersister();
 		if ( persister.hasCache() ) {
 			
 			final CacheKey ck = getSession().generateCacheKey(
 					getId(),
 					persister.getIdentifierType(), 
 					persister.getRootEntityName()
 			);
 			
 			if ( success && cacheEntry!=null /*!persister.isCacheInvalidationRequired()*/ ) {
 				try {
-					session.getSessionEventsManager().cachePutStart();
+					session.getEventListenerManager().cachePutStart();
 					final boolean put = persister.getCacheAccessStrategy().afterUpdate( ck, cacheEntry, nextVersion, previousVersion, lock );
 
 					if ( put && getSession().getFactory().getStatistics().isStatisticsEnabled() ) {
 						getSession().getFactory().getStatisticsImplementor().secondLevelCachePut( getPersister().getCacheAccessStrategy().getRegion().getName() );
 					}
 				}
 				finally {
-					session.getSessionEventsManager().cachePutEnd();
+					session.getEventListenerManager().cachePutEnd();
 				}
 			}
 			else {
 				persister.getCacheAccessStrategy().unlockItem( ck, lock );
 			}
 		}
 		postCommitUpdate();
 	}
 
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java
index 792d472835..00891085ed 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/StandardQueryCache.java
@@ -1,343 +1,343 @@
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
 package org.hibernate.cache.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Properties;
 import java.util.Set;
 import javax.persistence.EntityNotFoundException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.cache.spi.QueryResultsRegion;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cfg.Settings;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 
 /**
  * The standard implementation of the Hibernate QueryCache interface.  This
  * implementation is very good at recognizing stale query results and
  * and re-running queries when it detects this condition, re-caching the new
  * results.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class StandardQueryCache implements QueryCache {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			StandardQueryCache.class.getName()
 	);
 
 	private static final boolean DEBUGGING = LOG.isDebugEnabled();
 	private static final boolean TRACING = LOG.isTraceEnabled();
 
 	private QueryResultsRegion cacheRegion;
 	private UpdateTimestampsCache updateTimestampsCache;
 
 	/**
 	 * Constructs a StandardQueryCache instance
 	 *
 	 * @param settings The SessionFactory settings.
 	 * @param props Any properties
 	 * @param updateTimestampsCache The update-timestamps cache to use.
 	 * @param regionName The base query cache region name
 	 */
 	public StandardQueryCache(
 			final Settings settings,
 			final Properties props,
 			final UpdateTimestampsCache updateTimestampsCache,
 			final String regionName) {
 		String regionNameToUse = regionName;
 		if ( regionNameToUse == null ) {
 			regionNameToUse = StandardQueryCache.class.getName();
 		}
 		final String prefix = settings.getCacheRegionPrefix();
 		if ( prefix != null ) {
 			regionNameToUse = prefix + '.' + regionNameToUse;
 		}
 		LOG.startingQueryCache( regionNameToUse );
 
 		this.cacheRegion = settings.getRegionFactory().buildQueryResultsRegion( regionNameToUse, props );
 		this.updateTimestampsCache = updateTimestampsCache;
 	}
 
 	@Override
 	public QueryResultsRegion getRegion() {
 		return cacheRegion;
 	}
 
 	@Override
 	public void destroy() {
 		try {
 			cacheRegion.destroy();
 		}
 		catch ( Exception e ) {
 			LOG.unableToDestroyQueryCache( cacheRegion.getName(), e.getMessage() );
 		}
 	}
 
 	@Override
 	public void clear() throws CacheException {
 		cacheRegion.evictAll();
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public boolean put(
 			final QueryKey key,
 			final Type[] returnTypes,
 			final List result,
 			final boolean isNaturalKeyLookup,
 			final SessionImplementor session) throws HibernateException {
 		if ( isNaturalKeyLookup && result.isEmpty() ) {
 			return false;
 		}
 		final long ts = cacheRegion.nextTimestamp();
 
 		if ( DEBUGGING ) {
 			LOG.debugf( "Caching query results in region: %s; timestamp=%s", cacheRegion.getName(), ts );
 		}
 
 		final List cacheable = new ArrayList( result.size() + 1 );
 		logCachedResultDetails( key, null, returnTypes, cacheable );
 		cacheable.add( ts );
 
 		final boolean isSingleResult = returnTypes.length == 1;
 		for ( Object aResult : result ) {
 			final Serializable cacheItem = isSingleResult
 					? returnTypes[0].disassemble( aResult, session, null )
 					: TypeHelper.disassemble( (Object[]) aResult, returnTypes, null, session, null );
 			cacheable.add( cacheItem );
 			logCachedResultRowDetails( returnTypes, aResult );
 		}
 
 		try {
-			session.getSessionEventsManager().cachePutStart();
+			session.getEventListenerManager().cachePutStart();
 			cacheRegion.put( key, cacheable );
 		}
 		finally {
-			session.getSessionEventsManager().cachePutEnd();
+			session.getEventListenerManager().cachePutEnd();
 		}
 
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public List get(
 			final QueryKey key,
 			final Type[] returnTypes,
 			final boolean isNaturalKeyLookup,
 			final Set<Serializable> spaces,
 			final SessionImplementor session) throws HibernateException {
 		if ( DEBUGGING ) {
 			LOG.debugf( "Checking cached query results in region: %s", cacheRegion.getName() );
 		}
 
 		final List cacheable = getCachedResults( key, session );
 		logCachedResultDetails( key, spaces, returnTypes, cacheable );
 
 		if ( cacheable == null ) {
 			if ( DEBUGGING ) {
 				LOG.debug( "Query results were not found in cache" );
 			}
 			return null;
 		}
 
 		final Long timestamp = (Long) cacheable.get( 0 );
 		if ( !isNaturalKeyLookup && !isUpToDate( spaces, timestamp, session ) ) {
 			if ( DEBUGGING ) {
 				LOG.debug( "Cached query results were not up-to-date" );
 			}
 			return null;
 		}
 
 		if ( DEBUGGING ) {
 			LOG.debug( "Returning cached query results" );
 		}
 		final boolean singleResult = returnTypes.length == 1;
 		for ( int i = 1; i < cacheable.size(); i++ ) {
 			if ( singleResult ) {
 				returnTypes[0].beforeAssemble( (Serializable) cacheable.get( i ), session );
 			}
 			else {
 				TypeHelper.beforeAssemble( (Serializable[]) cacheable.get( i ), returnTypes, session );
 			}
 		}
 
 		final List result = new ArrayList( cacheable.size() - 1 );
 		for ( int i = 1; i < cacheable.size(); i++ ) {
 			try {
 				if ( singleResult ) {
 					result.add( returnTypes[0].assemble( (Serializable) cacheable.get( i ), session, null ) );
 				}
 				else {
 					result.add(
 							TypeHelper.assemble( (Serializable[]) cacheable.get( i ), returnTypes, session, null )
 					);
 				}
 				logCachedResultRowDetails( returnTypes, result.get( i - 1 ) );
 			}
 			catch ( RuntimeException ex ) {
 				if ( isNaturalKeyLookup ) {
 					// potentially perform special handling for natural-id look ups.
 					if ( UnresolvableObjectException.class.isInstance( ex )
 							|| EntityNotFoundException.class.isInstance( ex ) ) {
 						if ( DEBUGGING ) {
 							LOG.debug( "Unable to reassemble cached natural-id query result" );
 						}
 						cacheRegion.evict( key );
 
 						// EARLY EXIT !!!!!
 						return null;
 					}
 				}
 				throw ex;
 			}
 		}
 		return result;
 	}
 
 	private List getCachedResults(QueryKey key, SessionImplementor session) {
 		List cacheable = null;
 		try {
-			session.getSessionEventsManager().cacheGetStart();
+			session.getEventListenerManager().cacheGetStart();
 			cacheable = (List) cacheRegion.get( key );
 		}
 		finally {
-			session.getSessionEventsManager().cacheGetEnd( cacheable != null );
+			session.getEventListenerManager().cacheGetEnd( cacheable != null );
 		}
 		return cacheable;
 	}
 
 
 	protected boolean isUpToDate(Set<Serializable> spaces, Long timestamp, SessionImplementor session) {
 		if ( DEBUGGING ) {
 			LOG.debugf( "Checking query spaces are up-to-date: %s", spaces );
 		}
 		return updateTimestampsCache.isUpToDate( spaces, timestamp, session );
 	}
 
 	@Override
 	public String toString() {
 		return "StandardQueryCache(" + cacheRegion.getName() + ')';
 	}
 
 	private static void logCachedResultDetails(QueryKey key, Set querySpaces, Type[] returnTypes, List result) {
 		if ( !TRACING ) {
 			return;
 		}
 		LOG.trace( "key.hashCode=" + key.hashCode() );
 		LOG.trace( "querySpaces=" + querySpaces );
 		if ( returnTypes == null || returnTypes.length == 0 ) {
 			LOG.trace(
 					"Unexpected returnTypes is "
 							+ ( returnTypes == null ? "null" : "empty" ) + "! result"
 							+ ( result == null ? " is null" : ".size()=" + result.size() )
 			);
 		}
 		else {
 			final StringBuilder returnTypeInfo = new StringBuilder();
 			for ( Type returnType : returnTypes ) {
 				returnTypeInfo.append( "typename=" )
 						.append( returnType.getName() )
 						.append( " class=" )
 						.append( returnType.getReturnedClass().getName() )
 						.append( ' ' );
 			}
 			LOG.trace( "unexpected returnTypes is " + returnTypeInfo.toString() + "! result" );
 		}
 	}
 
 	private static void logCachedResultRowDetails(Type[] returnTypes, Object result) {
 		if ( !TRACING ) {
 			return;
 		}
 		logCachedResultRowDetails(
 				returnTypes,
 				( result instanceof Object[] ? (Object[]) result : new Object[] { result } )
 		);
 	}
 
 	private static void logCachedResultRowDetails(Type[] returnTypes, Object[] tuple) {
 		if ( !TRACING ) {
 			return;
 		}
 		if ( tuple == null ) {
 			LOG.tracef(
 					"tuple is null; returnTypes is %s",
 					returnTypes == null ? "null" : "Type[" + returnTypes.length + "]"
 			);
 			if ( returnTypes != null && returnTypes.length > 1 ) {
 				LOG.trace(
 						"Unexpected result tuple! tuple is null; should be Object["
 								+ returnTypes.length + "]!"
 				);
 			}
 		}
 		else {
 			if ( returnTypes == null || returnTypes.length == 0 ) {
 				LOG.trace(
 						"Unexpected result tuple! tuple is null; returnTypes is "
 								+ ( returnTypes == null ? "null" : "empty" )
 				);
 			}
 			LOG.tracef(
 					"tuple is Object[%s]; returnTypes is %s",
 					tuple.length,
 					returnTypes == null ? "null" : "Type[" + returnTypes.length + "]"
 			);
 			if ( returnTypes != null && tuple.length != returnTypes.length ) {
 				LOG.trace(
 						"Unexpected tuple length! transformer= expected="
 								+ returnTypes.length + " got=" + tuple.length
 				);
 			}
 			else {
 				for ( int j = 0; j < tuple.length; j++ ) {
 					if ( tuple[j] != null && returnTypes != null
 							&& ! returnTypes[j].getReturnedClass().isInstance( tuple[j] ) ) {
 						LOG.trace(
 								"Unexpected tuple value type! transformer= expected="
 										+ returnTypes[j].getReturnedClass().getName()
 										+ " got="
 										+ tuple[j].getClass().getName()
 						);
 					}
 				}
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java
index 2a7f86d48e..446709f62c 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java
@@ -1,253 +1,253 @@
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
 package org.hibernate.cache.spi;
 
 import java.io.Serializable;
 import java.util.Properties;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cache.CacheException;
 import org.hibernate.cfg.Settings;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Tracks the timestamps of the most recent updates to particular tables. It is
  * important that the cache timeout of the underlying cache implementation be set
  * to a higher value than the timeouts of any of the query caches. In fact, we
  * recommend that the the underlying cache not be configured for expiry at all.
  * Note, in particular, that an LRU cache expiry policy is never appropriate.
  *
  * @author Gavin King
  * @author Mikheil Kapanadze
  */
 public class UpdateTimestampsCache {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, UpdateTimestampsCache.class.getName() );
 	private static final boolean DEBUG_ENABLED = LOG.isDebugEnabled();
 	/**
 	 * The region name of the update-timestamps cache.
 	 */
 	public static final String REGION_NAME = UpdateTimestampsCache.class.getName();
 
 
 	private final SessionFactoryImplementor factory;
 	private final TimestampsRegion region;
 
 	/**
 	 * Constructs an UpdateTimestampsCache.
 	 *
 	 * @param settings The SessionFactory settings
 	 * @param props Any properties
 	 * @param factory The SessionFactory
 	 */
 	public UpdateTimestampsCache(Settings settings, Properties props, final SessionFactoryImplementor factory) {
 		this.factory = factory;
 		final String prefix = settings.getCacheRegionPrefix();
 		final String regionName = prefix == null ? REGION_NAME : prefix + '.' + REGION_NAME;
 
 		LOG.startingUpdateTimestampsCache( regionName );
 		this.region = settings.getRegionFactory().buildTimestampsRegion( regionName, props );
 	}
 
 	/**
 	 * Constructs an UpdateTimestampsCache.
 	 *
 	 * @param settings The SessionFactory settings
 	 * @param props Any properties
 	 */
 	@SuppressWarnings({"UnusedDeclaration"})
 	public UpdateTimestampsCache(Settings settings, Properties props) {
 		this( settings, props, null );
 	}
 
 	/**
 	 * Perform pre-invalidation.
 	 *
 	 *
 	 * @param spaces The spaces to pre-invalidate
 	 *
 	 * @param session
 	 * @throws CacheException Indicated problem delegating to underlying region.
 	 */
 	public void preInvalidate(Serializable[] spaces, SessionImplementor session) throws CacheException {
 		final boolean stats = factory != null && factory.getStatistics().isStatisticsEnabled();
 
 		final Long ts = region.nextTimestamp() + region.getTimeout();
 
 		for ( Serializable space : spaces ) {
 			if ( DEBUG_ENABLED ) {
 				LOG.debugf( "Pre-invalidating space [%s], timestamp: %s", space, ts );
 			}
 
 			try {
-				session.getSessionEventsManager().cachePutStart();
+				session.getEventListenerManager().cachePutStart();
 
 				//put() has nowait semantics, is this really appropriate?
 				//note that it needs to be async replication, never local or sync
 				region.put( space, ts );
 			}
 			finally {
-				session.getSessionEventsManager().cachePutEnd();
+				session.getEventListenerManager().cachePutEnd();
 			}
 
 			if ( stats ) {
 				factory.getStatisticsImplementor().updateTimestampsCachePut();
 			}
 		}
 	}
 
 	/**
 	 * Perform invalidation.
 	 *
 	 *
 	 * @param spaces The spaces to pre-invalidate
 	 *
 	 * @param session
 	 * @throws CacheException Indicated problem delegating to underlying region.
 	 */
 	public void invalidate(Serializable[] spaces, SessionImplementor session) throws CacheException {
 		final boolean stats = factory != null && factory.getStatistics().isStatisticsEnabled();
 
 		final Long ts = region.nextTimestamp();
 
 		for (Serializable space : spaces) {
 			if ( DEBUG_ENABLED ) {
 				LOG.debugf( "Invalidating space [%s], timestamp: %s", space, ts );
 			}
 
 			try {
-				session.getSessionEventsManager().cachePutStart();
+				session.getEventListenerManager().cachePutStart();
 
 				//put() has nowait semantics, is this really appropriate?
 				//note that it needs to be async replication, never local or sync
 				region.put( space, ts );
 			}
 			finally {
-				session.getSessionEventsManager().cachePutEnd();
+				session.getEventListenerManager().cachePutEnd();
 			}
 
 			if ( stats ) {
 				factory.getStatisticsImplementor().updateTimestampsCachePut();
 			}
 		}
 	}
 
 	/**
 	 * Perform an up-to-date check for the given set of query spaces.
 	 *
 	 *
 	 * @param spaces The spaces to check
 	 * @param timestamp The timestamp against which to check.
 	 *
 	 * @param session
 	 * @return Whether all those spaces are up-to-date
 	 *
 	 * @throws CacheException Indicated problem delegating to underlying region.
 	 */
 	public boolean isUpToDate(Set<Serializable> spaces, Long timestamp, SessionImplementor session) throws CacheException {
 		final boolean stats = factory != null && factory.getStatistics().isStatisticsEnabled();
 
 		for ( Serializable space : spaces ) {
 			final Long lastUpdate = getLastUpdateTimestampForSpace( space, session );
 			if ( lastUpdate == null ) {
 				if ( stats ) {
 					factory.getStatisticsImplementor().updateTimestampsCacheMiss();
 				}
 				//the last update timestamp was lost from the cache
 				//(or there were no updates since startup!)
 				//updateTimestamps.put( space, new Long( updateTimestamps.nextTimestamp() ) );
 				//result = false; // safer
 			}
 			else {
 				if ( DEBUG_ENABLED ) {
 					LOG.debugf(
 							"[%s] last update timestamp: %s",
 							space,
 							lastUpdate + ", result set timestamp: " + timestamp
 					);
 				}
 				if ( stats ) {
 					factory.getStatisticsImplementor().updateTimestampsCacheHit();
 				}
 				if ( lastUpdate >= timestamp ) {
 					return false;
 				}
 			}
 		}
 		return true;
 	}
 
 	private Long getLastUpdateTimestampForSpace(Serializable space, SessionImplementor session) {
 		Long ts = null;
 		try {
-			session.getSessionEventsManager().cacheGetStart();
+			session.getEventListenerManager().cacheGetStart();
 			ts = (Long) region.get( space );
 		}
 		finally {
-			session.getSessionEventsManager().cacheGetEnd( ts != null );
+			session.getEventListenerManager().cacheGetEnd( ts != null );
 		}
 		return ts;
 	}
 
 	/**
 	 * Clear the update-timestamps data.
 	 *
 	 * @throws CacheException Indicates problem delegating call to underlying region.
 	 */
 	public void clear() throws CacheException {
 		region.evictAll();
 	}
 
 	/**
 	 * Destroys the cache.
 	 *
 	 * @throws CacheException Indicates problem delegating call to underlying region.
 	 */
 	public void destroy() {
 		try {
 			region.destroy();
 		}
 		catch (Exception e) {
 			LOG.unableToDestroyUpdateTimestampsCache( region.getName(), e.getMessage() );
 		}
 	}
 
 	/**
 	 * Get the underlying cache region where data is stored..
 	 *
 	 * @return The underlying region.
 	 */
 	public TimestampsRegion getRegion() {
 		return region;
 	}
 
 	@Override
 	public String toString() {
 		return "UpdateTimestampsCache";
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
index a673bf2876..15eb96882d 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AvailableSettings.java
@@ -1,681 +1,681 @@
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
 package org.hibernate.cfg;
 
 /**
  * @author Steve Ebersole
  */
 public interface AvailableSettings {
 	/**
 	 * Defines a name for the {@link org.hibernate.SessionFactory}.  Useful both to<ul>
 	 *     <li>allow serialization and deserialization to work across different jvms</li>
 	 *     <li>optionally allow the SessionFactory to be bound into JNDI</li>
 	 * </ul>
 	 *
 	 * @see #SESSION_FACTORY_NAME_IS_JNDI
 	 */
 	 String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
 
 	/**
 	 * Does the value defined by {@link #SESSION_FACTORY_NAME} represent a {@literal JNDI} namespace into which
 	 * the {@link org.hibernate.SessionFactory} should be bound?
 	 */
 	String SESSION_FACTORY_NAME_IS_JNDI = "hibernate.session_factory_name_is_jndi";
 
 	/**
 	 * Names the {@link org.hibernate.engine.jdbc.connections.spi.ConnectionProvider} to use for obtaining
 	 * JDBC connections.  Can either reference an instance of
 	 * {@link org.hibernate.engine.jdbc.connections.spi.ConnectionProvider} or a {@link Class} or {@link String}
 	 * reference to the {@link org.hibernate.engine.jdbc.connections.spi.ConnectionProvider} implementation
 	 * class.
 	 */
 	String CONNECTION_PROVIDER ="hibernate.connection.provider_class";
 
 	/**
 	 * Names the {@literal JDBC} driver class
 	 */
 	String DRIVER ="hibernate.connection.driver_class";
 
 	/**
 	 * Names the {@literal JDBC} connection url.
 	 */
 	String URL ="hibernate.connection.url";
 
 	/**
 	 * Names the connection user.  This might mean one of 2 things in out-of-the-box Hibernate
 	 * {@link org.hibernate.engine.jdbc.connections.spi.ConnectionProvider}: <ul>
 	 *     <li>The username used to pass along to creating the JDBC connection</li>
 	 *     <li>The username used to obtain a JDBC connection from a data source</li>
 	 * </ul>
 	 */
 	String USER ="hibernate.connection.username";
 
 	/**
 	 * Names the connection password.  See usage discussion on {@link #USER}
 	 */
 	String PASS ="hibernate.connection.password";
 
 	/**
 	 * Names the {@literal JDBC} transaction isolation level
 	 */
 	String ISOLATION ="hibernate.connection.isolation";
 
 	/**
 	 * Names the {@literal JDBC} autocommit mode
 	 */
 	String AUTOCOMMIT ="hibernate.connection.autocommit";
 
 	/**
 	 * Maximum number of inactive connections for the built-in Hibernate connection pool.
 	 */
 	String POOL_SIZE ="hibernate.connection.pool_size";
 
 	/**
 	 * Names a {@link javax.sql.DataSource}.  Can either reference a {@link javax.sql.DataSource} instance or
 	 * a {@literal JNDI} name under which to locate the {@link javax.sql.DataSource}.
 	 */
 	String DATASOURCE ="hibernate.connection.datasource";
 
 	/**
 	 * Names a prefix used to define arbitrary JDBC connection properties.  These properties are passed along to
 	 * the {@literal JDBC} provider when creating a connection.
 	 */
 	String CONNECTION_PREFIX = "hibernate.connection";
 
 	/**
 	 * Names the {@literal JNDI} {@link javax.naming.InitialContext} class.
 	 *
 	 * @see javax.naming.Context#INITIAL_CONTEXT_FACTORY
 	 */
 	String JNDI_CLASS ="hibernate.jndi.class";
 
 	/**
 	 * Names the {@literal JNDI} provider/connection url
 	 *
 	 * @see javax.naming.Context#PROVIDER_URL
 	 */
 	String JNDI_URL ="hibernate.jndi.url";
 
 	/**
 	 * Names a prefix used to define arbitrary {@literal JNDI} {@link javax.naming.InitialContext} properties.  These
 	 * properties are passed along to {@link javax.naming.InitialContext#InitialContext(java.util.Hashtable)}
 	 */
 	String JNDI_PREFIX = "hibernate.jndi";
 
 	/**
 	 * Names the Hibernate {@literal SQL} {@link org.hibernate.dialect.Dialect} class
 	 */
 	String DIALECT ="hibernate.dialect";
 
 	/**
 	 * Names any additional {@link org.hibernate.engine.jdbc.dialect.spi.DialectResolver} implementations to
 	 * register with the standard {@link org.hibernate.engine.jdbc.dialect.spi.DialectFactory}.
 	 */
 	String DIALECT_RESOLVERS = "hibernate.dialect_resolvers";
 
 
 	/**
 	 * A default database schema (owner) name to use for unqualified tablenames
 	 */
 	String DEFAULT_SCHEMA = "hibernate.default_schema";
 	/**
 	 * A default database catalog name to use for unqualified tablenames
 	 */
 	String DEFAULT_CATALOG = "hibernate.default_catalog";
 
 	/**
 	 * Enable logging of generated SQL to the console
 	 */
 	String SHOW_SQL ="hibernate.show_sql";
 	/**
 	 * Enable formatting of SQL logged to the console
 	 */
 	String FORMAT_SQL ="hibernate.format_sql";
 	/**
 	 * Add comments to the generated SQL
 	 */
 	String USE_SQL_COMMENTS ="hibernate.use_sql_comments";
 	/**
 	 * Maximum depth of outer join fetching
 	 */
 	String MAX_FETCH_DEPTH = "hibernate.max_fetch_depth";
 	/**
 	 * The default batch size for batch fetching
 	 */
 	String DEFAULT_BATCH_FETCH_SIZE = "hibernate.default_batch_fetch_size";
 	/**
 	 * Use <tt>java.io</tt> streams to read / write binary data from / to JDBC
 	 */
 	String USE_STREAMS_FOR_BINARY = "hibernate.jdbc.use_streams_for_binary";
 	/**
 	 * Use JDBC scrollable <tt>ResultSet</tt>s. This property is only necessary when there is
 	 * no <tt>ConnectionProvider</tt>, ie. the user is supplying JDBC connections.
 	 */
 	String USE_SCROLLABLE_RESULTSET = "hibernate.jdbc.use_scrollable_resultset";
 	/**
 	 * Tells the JDBC driver to attempt to retrieve row Id with the JDBC 3.0 PreparedStatement.getGeneratedKeys()
 	 * method. In general, performance will be better if this property is set to true and the underlying
 	 * JDBC driver supports getGeneratedKeys().
 	 */
 	String USE_GET_GENERATED_KEYS = "hibernate.jdbc.use_get_generated_keys";
 	/**
 	 * Gives the JDBC driver a hint as to the number of rows that should be fetched from the database
 	 * when more rows are needed. If <tt>0</tt>, JDBC driver default settings will be used.
 	 */
 	String STATEMENT_FETCH_SIZE = "hibernate.jdbc.fetch_size";
 	/**
 	 * Maximum JDBC batch size. A nonzero value enables batch updates.
 	 */
 	String STATEMENT_BATCH_SIZE = "hibernate.jdbc.batch_size";
 	/**
 	 * Select a custom batcher.
 	 */
 	String BATCH_STRATEGY = "hibernate.jdbc.factory_class";
 	/**
 	 * Should versioned data be included in batching?
 	 */
 	String BATCH_VERSIONED_DATA = "hibernate.jdbc.batch_versioned_data";
 	/**
 	 * An XSLT resource used to generate "custom" XML
 	 */
 	String OUTPUT_STYLESHEET ="hibernate.xml.output_stylesheet";
 
 	/**
 	 * Maximum size of C3P0 connection pool
 	 */
 	String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
 	/**
 	 * Minimum size of C3P0 connection pool
 	 */
 	String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
 
 	/**
 	 * Maximum idle time for C3P0 connection pool
 	 */
 	String C3P0_TIMEOUT = "hibernate.c3p0.timeout";
 	/**
 	 * Maximum size of C3P0 statement cache
 	 */
 	String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
 	/**
 	 * Number of connections acquired when pool is exhausted
 	 */
 	String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
 	/**
 	 * Idle time before a C3P0 pooled connection is validated
 	 */
 	String C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";
 
 	/**
 	 * Proxool/Hibernate property prefix
 	 * @deprecated Use {@link #PROXOOL_CONFIG_PREFIX} instead
 	 */
 	String PROXOOL_PREFIX = "hibernate.proxool";
 	/**
 	 * Proxool property to configure the Proxool Provider using an XML (<tt>/path/to/file.xml</tt>)
 	 */
 	String PROXOOL_XML = "hibernate.proxool.xml";
 	/**
 	 * Proxool property to configure the Proxool Provider  using a properties file (<tt>/path/to/proxool.properties</tt>)
 	 */
 	String PROXOOL_PROPERTIES = "hibernate.proxool.properties";
 	/**
 	 * Proxool property to configure the Proxool Provider from an already existing pool (<tt>true</tt> / <tt>false</tt>)
 	 */
 	String PROXOOL_EXISTING_POOL = "hibernate.proxool.existing_pool";
 	/**
 	 * Proxool property with the Proxool pool alias to use
 	 * (Required for <tt>PROXOOL_EXISTING_POOL</tt>, <tt>PROXOOL_PROPERTIES</tt>, or
 	 * <tt>PROXOOL_XML</tt>)
 	 */
 	String PROXOOL_POOL_ALIAS = "hibernate.proxool.pool_alias";
 
 	/**
 	 * Enable automatic session close at end of transaction
 	 */
 	String AUTO_CLOSE_SESSION = "hibernate.transaction.auto_close_session";
 	/**
 	 * Enable automatic flush during the JTA <tt>beforeCompletion()</tt> callback
 	 */
 	String FLUSH_BEFORE_COMPLETION = "hibernate.transaction.flush_before_completion";
 	/**
 	 * Specifies how Hibernate should release JDBC connections.
 	 */
 	String RELEASE_CONNECTIONS = "hibernate.connection.release_mode";
 	/**
 	 * Context scoping impl for {@link org.hibernate.SessionFactory#getCurrentSession()} processing.
 	 */
 	String CURRENT_SESSION_CONTEXT_CLASS = "hibernate.current_session_context_class";
 
 	/**
 	 * Names the implementation of {@link org.hibernate.engine.transaction.spi.TransactionFactory} to use for
 	 * creating {@link org.hibernate.Transaction} instances
 	 */
 	String TRANSACTION_STRATEGY = "hibernate.transaction.factory_class";
 
 	/**
 	 * Names the {@link org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform} implementation to use for integrating
 	 * with {@literal JTA} systems.  Can reference either a {@link org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform}
 	 * instance or the name of the {@link org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform} implementation class
 	 * @since 4.0
 	 */
 	String JTA_PLATFORM = "hibernate.transaction.jta.platform";
 
 	/**
 	 * Names the {@link org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformResolver} implementation to use.
 	 * @since 4.3
 	 */
 	String JTA_PLATFORM_RESOLVER = "hibernate.transaction.jta.platform_resolver";
 
 	/**
 	 * The {@link org.hibernate.cache.spi.RegionFactory} implementation class
 	 */
 	String CACHE_REGION_FACTORY = "hibernate.cache.region.factory_class";
 
 	/**
 	 * The <tt>CacheProvider</tt> implementation class
 	 */
 	String CACHE_PROVIDER_CONFIG = "hibernate.cache.provider_configuration_file_resource_path";
 	/**
 	 * The <tt>CacheProvider</tt> JNDI namespace, if pre-bound to JNDI.
 	 */
 	String CACHE_NAMESPACE = "hibernate.cache.jndi";
 	/**
 	 * Enable the query cache (disabled by default)
 	 */
 	String USE_QUERY_CACHE = "hibernate.cache.use_query_cache";
 	/**
 	 * The <tt>QueryCacheFactory</tt> implementation class.
 	 */
 	String QUERY_CACHE_FACTORY = "hibernate.cache.query_cache_factory";
 	/**
 	 * Enable the second-level cache (enabled by default)
 	 */
 	String USE_SECOND_LEVEL_CACHE = "hibernate.cache.use_second_level_cache";
 	/**
 	 * Optimize the cache for minimal puts instead of minimal gets
 	 */
 	String USE_MINIMAL_PUTS = "hibernate.cache.use_minimal_puts";
 	/**
 	 * The <tt>CacheProvider</tt> region name prefix
 	 */
 	String CACHE_REGION_PREFIX = "hibernate.cache.region_prefix";
 	/**
 	 * Enable use of structured second-level cache entries
 	 */
 	String USE_STRUCTURED_CACHE = "hibernate.cache.use_structured_entries";
 	/**
 	 * Enables the automatic eviction of a bi-directional association's collection cache when an element in the
 	 * ManyToOne collection is added/updated/removed without properly managing the change on the OneToMany side.
 	 */
 	String AUTO_EVICT_COLLECTION_CACHE = "hibernate.cache.auto_evict_collection_cache";
 	/**
 	 * Enable statistics collection
 	 */
 	String GENERATE_STATISTICS = "hibernate.generate_statistics";
 
 	String USE_IDENTIFIER_ROLLBACK = "hibernate.use_identifier_rollback";
 
 	/**
 	 * Use bytecode libraries optimized property access
 	 */
 	String USE_REFLECTION_OPTIMIZER = "hibernate.bytecode.use_reflection_optimizer";
 
 	/**
 	 * The classname of the HQL query parser factory
 	 */
 	String QUERY_TRANSLATOR = "hibernate.query.factory_class";
 
 	/**
 	 * A comma-separated list of token substitutions to use when translating a Hibernate
 	 * query to SQL
 	 */
 	String QUERY_SUBSTITUTIONS = "hibernate.query.substitutions";
 
 	/**
 	 * Should named queries be checked during startup (the default is enabled).
 	 * <p/>
 	 * Mainly intended for test environments.
 	 */
 	String QUERY_STARTUP_CHECKING = "hibernate.query.startup_check";
 
 	/**
 	 * Auto export/update schema using hbm2ddl tool. Valid values are <tt>update</tt>,
 	 * <tt>create</tt>, <tt>create-drop</tt> and <tt>validate</tt>.
 	 */
 	String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
 
 	/**
 	 * Comma-separated names of the optional files containing SQL DML statements executed
 	 * during the SessionFactory creation.
 	 * File order matters, the statements of a give file are executed before the statements of the
 	 * following files.
 	 *
 	 * These statements are only executed if the schema is created ie if <tt>hibernate.hbm2ddl.auto</tt>
 	 * is set to <tt>create</tt> or <tt>create-drop</tt>.
 	 *
 	 * The default value is <tt>/import.sql</tt>
 	 */
 	String HBM2DDL_IMPORT_FILES = "hibernate.hbm2ddl.import_files";
 
 	/**
 	 * {@link String} reference to {@link org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor} implementation class.
 	 * Referenced implementation is required to provide non-argument constructor.
 	 *
 	 * The default value is <tt>org.hibernate.tool.hbm2ddl.SingleLineSqlCommandExtractor</tt>.
 	 */
 	String HBM2DDL_IMPORT_FILES_SQL_EXTRACTOR = "hibernate.hbm2ddl.import_files_sql_extractor";
 
 	/**
 	 * The {@link org.hibernate.exception.spi.SQLExceptionConverter} to use for converting SQLExceptions
 	 * to Hibernate's JDBCException hierarchy.  The default is to use the configured
 	 * {@link org.hibernate.dialect.Dialect}'s preferred SQLExceptionConverter.
 	 */
 	String SQL_EXCEPTION_CONVERTER = "hibernate.jdbc.sql_exception_converter";
 
 	/**
 	 * Enable wrapping of JDBC result sets in order to speed up column name lookups for
 	 * broken JDBC drivers
 	 */
 	String WRAP_RESULT_SETS = "hibernate.jdbc.wrap_result_sets";
 
 	/**
 	 * Enable ordering of update statements by primary key value
 	 */
 	String ORDER_UPDATES = "hibernate.order_updates";
 
 	/**
 	 * Enable ordering of insert statements for the purpose of more efficient JDBC batching.
 	 */
 	String ORDER_INSERTS = "hibernate.order_inserts";
 
 	/**
 	 * Default precedence of null values in {@code ORDER BY} clause.  Supported options: {@code none} (default),
 	 * {@code first}, {@code last}.
 	 */
 	String DEFAULT_NULL_ORDERING = "hibernate.order_by.default_null_ordering";
 
 	/**
 	 * The EntityMode in which set the Session opened from the SessionFactory.
 	 */
     String DEFAULT_ENTITY_MODE = "hibernate.default_entity_mode";
 
 	/**
 	 * Should all database identifiers be quoted.
 	 */
 	String GLOBALLY_QUOTED_IDENTIFIERS = "hibernate.globally_quoted_identifiers";
 
 	/**
 	 * Enable nullability checking.
 	 * Raises an exception if a property marked as not-null is null.
 	 * Default to false if Bean Validation is present in the classpath and Hibernate Annotations is used,
 	 * true otherwise.
 	 */
 	String CHECK_NULLABILITY = "hibernate.check_nullability";
 
 
 	String BYTECODE_PROVIDER = "hibernate.bytecode.provider";
 
 	String JPAQL_STRICT_COMPLIANCE= "hibernate.query.jpaql_strict_compliance";
 
 	/**
 	 * When using pooled {@link org.hibernate.id.enhanced.Optimizer optimizers}, prefer interpreting the
 	 * database value as the lower (lo) boundary.  The default is to interpret it as the high boundary.
 	 */
 	String PREFER_POOLED_VALUES_LO = "hibernate.id.optimizer.pooled.prefer_lo";
 
 	/**
 	 * The maximum number of strong references maintained by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 128.
 	 * @deprecated in favor of {@link #QUERY_PLAN_CACHE_PARAMETER_METADATA_MAX_SIZE}
 	 */
 	@Deprecated
 	String QUERY_PLAN_CACHE_MAX_STRONG_REFERENCES = "hibernate.query.plan_cache_max_strong_references";
 
 	/**
 	 * The maximum number of soft references maintained by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 2048.
 	 * @deprecated in favor of {@link #QUERY_PLAN_CACHE_MAX_SIZE}
 	 */
 	@Deprecated
 	String QUERY_PLAN_CACHE_MAX_SOFT_REFERENCES = "hibernate.query.plan_cache_max_soft_references";
 
 	/**
 	 * The maximum number of entries including:
 	 * <ul>
 	 *     <li>{@link org.hibernate.engine.query.spi.HQLQueryPlan}</li>
 	 *     <li>{@link org.hibernate.engine.query.spi.FilterQueryPlan}</li>
 	 *     <li>{@link org.hibernate.engine.query.spi.NativeSQLQueryPlan}</li>
 	 * </ul>
 	 * 
 	 * maintained by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 2048.
 	 */
 	String QUERY_PLAN_CACHE_MAX_SIZE = "hibernate.query.plan_cache_max_size";
 
 	/**
 	 * The maximum number of {@link org.hibernate.engine.query.spi.ParameterMetadata} maintained 
 	 * by {@link org.hibernate.engine.query.spi.QueryPlanCache}. Default is 128.
 	 */
 	String QUERY_PLAN_CACHE_PARAMETER_METADATA_MAX_SIZE = "hibernate.query.plan_parameter_metadata_max_size";
 
 	/**
 	 * Should we not use contextual LOB creation (aka based on {@link java.sql.Connection#createBlob()} et al).
 	 */
 	String NON_CONTEXTUAL_LOB_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
 
 	/**
 	 * Used to define a {@link java.util.Collection} of the {@link ClassLoader} instances Hibernate should use for
 	 * class-loading and resource-lookups.
 	 *
 	 * @since 5.0
 	 */
 	String CLASSLOADERS = "hibernate.classLoaders";
 
 	/**
 	 * Names the {@link ClassLoader} used to load user application classes.
 	 * @since 4.0
 	 *
 	 * @deprecated Use {@link #CLASSLOADERS} instead
 	 */
 	@Deprecated
 	String APP_CLASSLOADER = "hibernate.classLoader.application";
 
 	/**
 	 * Names the {@link ClassLoader} Hibernate should use to perform resource loading.
 	 * @since 4.0
 	 * @deprecated Use {@link #CLASSLOADERS} instead
 	 */
 	@Deprecated
 	String RESOURCES_CLASSLOADER = "hibernate.classLoader.resources";
 
 	/**
 	 * Names the {@link ClassLoader} responsible for loading Hibernate classes.  By default this is
 	 * the {@link ClassLoader} that loaded this class.
 	 * @since 4.0
 	 * @deprecated Use {@link #CLASSLOADERS} instead
 	 */
 	@Deprecated
 	String HIBERNATE_CLASSLOADER = "hibernate.classLoader.hibernate";
 
 	/**
 	 * Names the {@link ClassLoader} used when Hibernate is unable to locates classes on the
 	 * {@link #APP_CLASSLOADER} or {@link #HIBERNATE_CLASSLOADER}.
 	 * @since 4.0
 	 * @deprecated Use {@link #CLASSLOADERS} instead
 	 */
 	@Deprecated
 	String ENVIRONMENT_CLASSLOADER = "hibernate.classLoader.environment";
 
 
 	String C3P0_CONFIG_PREFIX = "hibernate.c3p0";
 
 	String PROXOOL_CONFIG_PREFIX = "hibernate.proxool";
 
 
 	String JMX_ENABLED = "hibernate.jmx.enabled";
 	String JMX_PLATFORM_SERVER = "hibernate.jmx.usePlatformServer";
 	String JMX_AGENT_ID = "hibernate.jmx.agentId";
 	String JMX_DOMAIN_NAME = "hibernate.jmx.defaultDomain";
 	String JMX_SF_NAME = "hibernate.jmx.sessionFactoryName";
 	String JMX_DEFAULT_OBJ_NAME_DOMAIN = "org.hibernate.core";
 
 	/**
 	 * A configuration value key used to indicate that it is safe to cache
 	 * {@link javax.transaction.TransactionManager} references.
 	 * @since 4.0
 	 */
 	String JTA_CACHE_TM = "hibernate.jta.cacheTransactionManager";
 
 	/**
 	 * A configuration value key used to indicate that it is safe to cache
 	 * {@link javax.transaction.UserTransaction} references.
 	 * @since 4.0
 	 */
 	String JTA_CACHE_UT = "hibernate.jta.cacheUserTransaction";
 
 	/**
 	 * Setting used to give the name of the default {@link org.hibernate.annotations.CacheConcurrencyStrategy}
 	 * to use when either {@link javax.persistence.Cacheable @Cacheable} or
 	 * {@link org.hibernate.annotations.Cache @Cache} is used.  {@link org.hibernate.annotations.Cache @Cache(strategy="..")} is used to override.
 	 */
 	String DEFAULT_CACHE_CONCURRENCY_STRATEGY = "hibernate.cache.default_cache_concurrency_strategy";
 
 	/**
 	 * Setting which indicates whether or not the new {@link org.hibernate.id.IdentifierGenerator} are used
 	 * for AUTO, TABLE and SEQUENCE.
 	 * Default to false to keep backward compatibility.
 	 */
 	String USE_NEW_ID_GENERATOR_MAPPINGS = "hibernate.id.new_generator_mappings";
 
 	/**
 	 * Setting to identify a {@link org.hibernate.CustomEntityDirtinessStrategy} to use.  May point to
 	 * either a class name or instance.
 	 */
 	String CUSTOM_ENTITY_DIRTINESS_STRATEGY = "hibernate.entity_dirtiness_strategy";
 
 	/**
 	 * Strategy for multi-tenancy.
 
 	 * @see org.hibernate.MultiTenancyStrategy
 	 * @since 4.0
 	 */
 	String MULTI_TENANT = "hibernate.multiTenancy";
 
 	/**
 	 * Names a {@link org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider} implementation to
 	 * use.  As MultiTenantConnectionProvider is also a service, can be configured directly through the
 	 * {@link org.hibernate.boot.registry.StandardServiceRegistryBuilder}
 	 *
 	 * @since 4.1
 	 */
 	String MULTI_TENANT_CONNECTION_PROVIDER = "hibernate.multi_tenant_connection_provider";
 
 	/**
 	 * Names a {@link org.hibernate.context.spi.CurrentTenantIdentifierResolver} implementation to use.
 	 * <p/>
 	 * Can be<ul>
 	 *     <li>CurrentTenantIdentifierResolver instance</li>
 	 *     <li>CurrentTenantIdentifierResolver implementation {@link Class} reference</li>
 	 *     <li>CurrentTenantIdentifierResolver implementation class name</li>
 	 * </ul>
 	 *
 	 * @since 4.1
 	 */
 	String MULTI_TENANT_IDENTIFIER_RESOLVER = "hibernate.tenant_identifier_resolver";
 
 	String FORCE_DISCRIMINATOR_IN_SELECTS_BY_DEFAULT = "hibernate.discriminator.force_in_select";
 
     String ENABLE_LAZY_LOAD_NO_TRANS = "hibernate.enable_lazy_load_no_trans";
 
 	String HQL_BULK_ID_STRATEGY = "hibernate.hql.bulk_id_strategy";
 
 	/**
 	 * Names the {@link org.hibernate.loader.BatchFetchStyle} to use.  Can specify either the
 	 * {@link org.hibernate.loader.BatchFetchStyle} name (insensitively), or a
 	 * {@link org.hibernate.loader.BatchFetchStyle} instance.
 	 */
 	String BATCH_FETCH_STYLE = "hibernate.batch_fetch_style";
 
 	/**
 	 * Enable direct storage of entity references into the second level cache when applicable (immutable data, etc).
 	 * Default is to not store direct references.
 	 */
 	String USE_DIRECT_REFERENCE_CACHE_ENTRIES = "hibernate.cache.use_reference_entries";
 
 	/**
 	 * Enable nationalized character support on all string / clob based attribute ( string, char, clob, text etc ).
 	 *
 	 * Default is <clode>false</clode>.
 	 */
 	String USE_NATIONALIZED_CHARACTER_DATA = "hibernate.use_nationalized_character_data";
 	
 	/**
 	 * A transaction can be rolled back by another thread ("tracking by thread")
 	 * -- not the original application. Examples of this include a JTA
 	 * transaction timeout handled by a background reaper thread.  The ability
 	 * to handle this situation requires checking the Thread ID every time
 	 * Session is called.  This can certainly have performance considerations.
 	 * 
 	 * Default is <code>true</code> (enabled).
 	 */
 	String JTA_TRACK_BY_THREAD = "hibernate.jta.track_by_thread";
 
 	String JACC_CONTEXT_ID = "hibernate.jacc_context_id";
 	String JACC_PREFIX = "hibernate.jacc";
 	String JACC_ENABLED = "hibernate.jacc.enabled";
 
 	/**
 	 * If enabled, allows {@link org.hibernate.tool.hbm2ddl.DatabaseMetadata} to
 	 * support synonyms during schema update and validations.  Due to the
 	 * possibility that this would return duplicate tables (especially in
 	 * Oracle), this is disabled by default.
 	 */
 	String ENABLE_SYNONYMS = "hibernate.synonyms";
 	
 	/**
 	 * Unique columns and unique keys both use unique constraints in most dialects.
 	 * SchemaUpdate needs to create these constraints, but DB's
 	 * support for finding existing constraints is extremely inconsistent. Further,
 	 * non-explicitly-named unique constraints use randomly generated characters.
 	 * 
 	 * Therefore, select from these strategies.
 	 * {@link org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy#DROP_RECREATE_QUIETLY} (DEFAULT):
 	 * 			Attempt to drop, then (re-)create each unique constraint.
 	 * 			Ignore any exceptions thrown.
 	 * {@link org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy#RECREATE_QUIETLY}:
 	 * 			attempt to (re-)create unique constraints,
 	 * 			ignoring exceptions thrown if the constraint already existed
 	 * {@link org.hibernate.tool.hbm2ddl.UniqueConstraintSchemaUpdateStrategy#SKIP}:
 	 * 			do not attempt to create unique constraints on a schema update
 	 */
 	String UNIQUE_CONSTRAINT_SCHEMA_UPDATE_STRATEGY = "hibernate.schema_update.unique_constraint_strategy";
 
 	/**
-	 * A setting to control whether to {@link org.hibernate.engine.internal.LoggingSessionEventsListener} is
+	 * A setting to control whether to {@link org.hibernate.engine.internal.StatisticalLoggingSessionEventListener} is
 	 * enabled on all Sessions (unless explicitly disabled for a given Session).  The default value of this
 	 * setting is determined by the value for {@link #GENERATE_STATISTICS}, meaning that if collection of statistics
 	 * is enabled logging of Session metrics is enabled by default too.
 	 */
 	String LOG_SESSION_METRICS = "hibernate.session.events.log";
 
 	String AUTO_SESSION_EVENTS_LISTENER = "hibernate.session.events.auto";
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/BaselineSessionEventsListenerBuilder.java b/hibernate-core/src/main/java/org/hibernate/cfg/BaselineSessionEventsListenerBuilder.java
index a666b76f80..d8a4d468f8 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/BaselineSessionEventsListenerBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/BaselineSessionEventsListenerBuilder.java
@@ -1,85 +1,85 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.HibernateException;
-import org.hibernate.SessionEventsListener;
-import org.hibernate.engine.internal.LoggingSessionEventsListener;
+import org.hibernate.SessionEventListener;
+import org.hibernate.engine.internal.StatisticalLoggingSessionEventListener;
 
 /**
  * @author Steve Ebersole
  */
 public class BaselineSessionEventsListenerBuilder {
 	private boolean logSessionMetrics;
-	private Class<? extends SessionEventsListener> autoListener;
+	private Class<? extends SessionEventListener> autoListener;
 
 	public BaselineSessionEventsListenerBuilder(
 			boolean logSessionMetrics,
-			Class<? extends SessionEventsListener> autoListener) {
+			Class<? extends SessionEventListener> autoListener) {
 		this.logSessionMetrics = logSessionMetrics;
 		this.autoListener = autoListener;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public boolean isLogSessionMetrics() {
 		return logSessionMetrics;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
 	public void setLogSessionMetrics(boolean logSessionMetrics) {
 		this.logSessionMetrics = logSessionMetrics;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
-	public Class<? extends SessionEventsListener> getAutoListener() {
+	public Class<? extends SessionEventListener> getAutoListener() {
 		return autoListener;
 	}
 
 	@SuppressWarnings("UnusedDeclaration")
-	public void setAutoListener(Class<? extends SessionEventsListener> autoListener) {
+	public void setAutoListener(Class<? extends SessionEventListener> autoListener) {
 		this.autoListener = autoListener;
 	}
 
-	public List<SessionEventsListener> buildBaselineList() {
-		List<SessionEventsListener> list = new ArrayList<SessionEventsListener>();
-		if ( logSessionMetrics && LoggingSessionEventsListener.isLoggingEnabled() ) {
-			list.add( new LoggingSessionEventsListener() );
+	public List<SessionEventListener> buildBaselineList() {
+		List<SessionEventListener> list = new ArrayList<SessionEventListener>();
+		if ( logSessionMetrics && StatisticalLoggingSessionEventListener.isLoggingEnabled() ) {
+			list.add( new StatisticalLoggingSessionEventListener() );
 		}
 		if ( autoListener != null ) {
 			try {
 				list.add( autoListener.newInstance() );
 			}
 			catch (Exception e) {
 				throw new HibernateException(
-						"Unable to instantiate specified auto SessionEventsListener : " + autoListener.getName(),
+						"Unable to instantiate specified auto SessionEventListener : " + autoListener.getName(),
 						e
 				);
 			}
 		}
 		return list;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index 12d5480b0d..2ac3c2c956 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,512 +1,511 @@
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
 
 import java.io.Serializable;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.NullPrecedence;
-import org.hibernate.SessionEventsListener;
+import org.hibernate.SessionEventListener;
 import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.cache.internal.NoCachingRegionFactory;
 import org.hibernate.cache.internal.RegionFactoryInitiator;
 import org.hibernate.cache.internal.StandardQueryCacheFactory;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
-import org.hibernate.engine.internal.LoggingSessionEventsListener;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.spi.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.PersistentTableBulkIdStrategy;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.hql.spi.TemporaryTableBulkIdStrategy;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.loader.BatchFetchStyle;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 
 	private static final long serialVersionUID = -1194386144994524825L;
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SettingsFactory.class.getName());
 
 	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
 
 	public SettingsFactory() {
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) {
 		final boolean debugEnabled =  LOG.isDebugEnabled();
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		final StrategySelector strategySelector = serviceRegistry.getService( StrategySelector.class );
 
 		Settings settings = new Settings();
 
 		//SessionFactory name:
 
 		String sessionFactoryName = props.getProperty( AvailableSettings.SESSION_FACTORY_NAME );
 		settings.setSessionFactoryName( sessionFactoryName );
 		settings.setSessionFactoryNameAlsoJndiName(
 				ConfigurationHelper.getBoolean( AvailableSettings.SESSION_FACTORY_NAME_IS_JNDI, props, true )
 		);
 
 		//JDBC and connection settings:
 
 		//Interrogate JDBC metadata
 		ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
 
 		settings.setDataDefinitionImplicitCommit( meta.doesDataDefinitionCauseTransactionCommit() );
 		settings.setDataDefinitionInTransactionSupported( meta.supportsDataDefinitionInTransaction() );
 
 		//use dialect default properties
 		final Properties properties = new Properties();
 		properties.putAll( jdbcServices.getDialect().getDefaultProperties() );
 		properties.putAll( props );
 
 		// Transaction settings:
 		settings.setJtaPlatform( serviceRegistry.getService( JtaPlatform.class ) );
 
 		MultiTableBulkIdStrategy multiTableBulkIdStrategy = strategySelector.resolveStrategy(
 				MultiTableBulkIdStrategy.class,
 				properties.getProperty( AvailableSettings.HQL_BULK_ID_STRATEGY )
 		);
 		if ( multiTableBulkIdStrategy == null ) {
 			multiTableBulkIdStrategy = jdbcServices.getDialect().supportsTemporaryTables()
 					? TemporaryTableBulkIdStrategy.INSTANCE
 					: new PersistentTableBulkIdStrategy();
 		}
 		settings.setMultiTableBulkIdStrategy( multiTableBulkIdStrategy );
 
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(AvailableSettings.FLUSH_BEFORE_COMPLETION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
 		}
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(AvailableSettings.AUTO_CLOSE_SESSION, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
 		}
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(AvailableSettings.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) {
 			batchSize = 0;
 		}
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch size: %s", batchSize );
 		}
 		settings.setJdbcBatchSize(batchSize);
 
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(AvailableSettings.BATCH_VERSIONED_DATA, properties, false);
 		if ( batchSize > 0 && debugEnabled ) {
 			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
 		}
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
 				AvailableSettings.USE_SCROLLABLE_RESULTSET,
 				properties,
 				meta.supportsScrollableResults()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
 		}
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(AvailableSettings.WRAP_RESULT_SETS, properties, false);
 		if ( debugEnabled ) {
 			LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
 		}
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(AvailableSettings.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
 		if ( debugEnabled ) {
 			LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
 		}
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(AvailableSettings.STATEMENT_FETCH_SIZE, properties);
 		if ( statementFetchSize != null && debugEnabled ) {
 			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
 		}
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
 		}
 		settings.setMultiTenancyStrategy( multiTenancyStrategy );
 
 		String releaseModeName = ConfigurationHelper.getString( AvailableSettings.RELEASE_CONNECTIONS, properties, "auto" );
 		if ( debugEnabled ) {
 			LOG.debugf( "Connection release mode: %s", releaseModeName );
 		}
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT ) {
 				// we need to make sure the underlying JDBC connection access supports aggressive release...
 				boolean supportsAgrressiveRelease = multiTenancyStrategy.requiresMultiTenantConnectionProvider()
 						? serviceRegistry.getService( MultiTenantConnectionProvider.class ).supportsAggressiveRelease()
 						: serviceRegistry.getService( ConnectionProvider.class ).supportsAggressiveRelease();
 				if ( ! supportsAgrressiveRelease ) {
 					LOG.unsupportedAfterStatement();
 					releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
 				}
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		final BatchFetchStyle batchFetchStyle = BatchFetchStyle.interpret( properties.get( AvailableSettings.BATCH_FETCH_STYLE ) );
 		LOG.debugf( "Using BatchFetchStyle : " + batchFetchStyle.name() );
 		settings.setBatchFetchStyle( batchFetchStyle );
 
 
 		//SQL Generation settings:
 
 		String defaultSchema = properties.getProperty( AvailableSettings.DEFAULT_SCHEMA );
 		String defaultCatalog = properties.getProperty( AvailableSettings.DEFAULT_CATALOG );
 		if ( defaultSchema != null && debugEnabled ) {
 			LOG.debugf( "Default schema: %s", defaultSchema );
 		}
 		if ( defaultCatalog != null && debugEnabled ) {
 			LOG.debugf( "Default catalog: %s", defaultCatalog );
 		}
 		settings.setDefaultSchemaName( defaultSchema );
 		settings.setDefaultCatalogName( defaultCatalog );
 
 		Integer maxFetchDepth = ConfigurationHelper.getInteger( AvailableSettings.MAX_FETCH_DEPTH, properties );
 		if ( maxFetchDepth != null ) {
 			LOG.debugf( "Maximum outer join fetch depth: %s", maxFetchDepth );
 		}
 		settings.setMaximumFetchDepth( maxFetchDepth );
 
 		int batchFetchSize = ConfigurationHelper.getInt(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
 		if ( debugEnabled ) {
 			LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
 		}
 		settings.setDefaultBatchFetchSize( batchFetchSize );
 
 		boolean comments = ConfigurationHelper.getBoolean( AvailableSettings.USE_SQL_COMMENTS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
 		}
 		settings.setCommentsEnabled( comments );
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean( AvailableSettings.ORDER_UPDATES, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
 		}
 		settings.setOrderUpdatesEnabled( orderUpdates );
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(AvailableSettings.ORDER_INSERTS, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
 		}
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		String defaultNullPrecedence = ConfigurationHelper.getString(
 				AvailableSettings.DEFAULT_NULL_ORDERING, properties, "none", "first", "last"
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Default null ordering: %s", defaultNullPrecedence );
 		}
 		settings.setDefaultNullPrecedence( NullPrecedence.parse( defaultNullPrecedence ) );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory( properties, serviceRegistry ) );
 
 		Map querySubstitutions = ConfigurationHelper.toMap( AvailableSettings.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Query language substitutions: %s", querySubstitutions );
 		}
 		settings.setQuerySubstitutions( querySubstitutions );
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( AvailableSettings.JPAQL_STRICT_COMPLIANCE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
 		}
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( AvailableSettings.USE_SECOND_LEVEL_CACHE, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
 		}
 		settings.setSecondLevelCacheEnabled( useSecondLevelCache );
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(AvailableSettings.USE_QUERY_CACHE, properties);
 		if ( debugEnabled ) {
 			LOG.debugf( "Query cache: %s", enabledDisabled(useQueryCache) );
 		}
 		settings.setQueryCacheEnabled( useQueryCache );
 		if (useQueryCache) {
 			settings.setQueryCacheFactory( createQueryCacheFactory( properties, serviceRegistry ) );
 		}
 
 		settings.setRegionFactory( serviceRegistry.getService( RegionFactory.class ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				AvailableSettings.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
 		}
 		settings.setMinimalPutsEnabled( useMinimalPuts );
 
 		String prefix = properties.getProperty( AvailableSettings.CACHE_REGION_PREFIX );
 		if ( StringHelper.isEmpty(prefix) ) {
 			prefix=null;
 		}
 		if ( prefix != null && debugEnabled ) {
 			LOG.debugf( "Cache region prefix: %s", prefix );
 		}
 		settings.setCacheRegionPrefix( prefix );
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( AvailableSettings.USE_STRUCTURED_CACHE, properties, false );
 		if ( debugEnabled ) {
 			LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
 		}
 		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );
 
 		boolean useDirectReferenceCacheEntries = ConfigurationHelper.getBoolean(
 				AvailableSettings.USE_DIRECT_REFERENCE_CACHE_ENTRIES,
 				properties,
 				false
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Second-level cache direct-reference entries: %s", enabledDisabled(useDirectReferenceCacheEntries) );
 		}
 		settings.setDirectReferenceCacheEntriesEnabled( useDirectReferenceCacheEntries );
 
 		boolean autoEvictCollectionCache = ConfigurationHelper.getBoolean( AvailableSettings.AUTO_EVICT_COLLECTION_CACHE, properties, false);
 		if ( debugEnabled ) {
 			LOG.debugf( "Automatic eviction of collection cache: %s", enabledDisabled(autoEvictCollectionCache) );
 		}
 		settings.setAutoEvictCollectionCache( autoEvictCollectionCache );
 
 		//Statistics and logging:
 
 		boolean useStatistics = ConfigurationHelper.getBoolean( AvailableSettings.GENERATE_STATISTICS, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
 		}
 		settings.setStatisticsEnabled( useStatistics );
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( AvailableSettings.USE_IDENTIFIER_ROLLBACK, properties );
 		if ( debugEnabled ) {
 			LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
 		}
 		settings.setIdentifierRollbackEnabled( useIdentifierRollback );
 
 		//Schema export:
 
 		String autoSchemaExport = properties.getProperty( AvailableSettings.HBM2DDL_AUTO );
 		if ( "validate".equals(autoSchemaExport) ) {
 			settings.setAutoValidateSchema( true );
 		}
 		else if ( "update".equals(autoSchemaExport) ) {
 			settings.setAutoUpdateSchema( true );
 		}
 		else if ( "create".equals(autoSchemaExport) ) {
 			settings.setAutoCreateSchema( true );
 		}
 		else if ( "create-drop".equals( autoSchemaExport ) ) {
 			settings.setAutoCreateSchema( true );
 			settings.setAutoDropSchema( true );
 		}
 		else if ( !StringHelper.isEmpty( autoSchemaExport ) ) {
 			LOG.warn( "Unrecognized value for \"hibernate.hbm2ddl.auto\": " + autoSchemaExport );
 		}
 		settings.setImportFiles( properties.getProperty( AvailableSettings.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( AvailableSettings.DEFAULT_ENTITY_MODE ) );
 		if ( debugEnabled ) {
 			LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
 		}
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( AvailableSettings.QUERY_STARTUP_CHECKING, properties, true );
 		if ( debugEnabled ) {
 			LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
 		}
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(AvailableSettings.CHECK_NULLABILITY, properties, true);
 		if ( debugEnabled ) {
 			LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
 		}
 		settings.setCheckNullability(checkNullability);
 
 		// TODO: Does EntityTuplizerFactory really need to be configurable? revisit for HHH-6383
 		settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
 
 //		String provider = properties.getProperty( AvailableSettings.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
 		boolean initializeLazyStateOutsideTransactionsEnabled = ConfigurationHelper.getBoolean(
 				AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS,
 				properties,
 				false
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "Allow initialization of lazy state outside session : : %s", enabledDisabled( initializeLazyStateOutsideTransactionsEnabled ) );
 		}
 		settings.setInitializeLazyStateOutsideTransactions( initializeLazyStateOutsideTransactionsEnabled );
 
 		boolean jtaTrackByThread = ConfigurationHelper.getBoolean(
 				AvailableSettings.JTA_TRACK_BY_THREAD,
 				properties,
 				true
 		);
 		if ( debugEnabled ) {
 			LOG.debugf( "JTA Track by Thread: %s", enabledDisabled(jtaTrackByThread) );
 		}
 		settings.setJtaTrackByThread( jtaTrackByThread );
 
 		final String autoSessionEventsListenerName = properties.getProperty( AvailableSettings.AUTO_SESSION_EVENTS_LISTENER );
-		final Class<? extends SessionEventsListener> autoSessionEventsListener = autoSessionEventsListenerName == null
+		final Class<? extends SessionEventListener> autoSessionEventsListener = autoSessionEventsListenerName == null
 				? null
-				: strategySelector.selectStrategyImplementor( SessionEventsListener.class, autoSessionEventsListenerName );
+				: strategySelector.selectStrategyImplementor( SessionEventListener.class, autoSessionEventsListenerName );
 
 		final boolean logSessionMetrics = ConfigurationHelper.getBoolean(
 				AvailableSettings.LOG_SESSION_METRICS,
 				properties,
 				useStatistics
 
 		);
 		settings.setBaselineSessionEventsListenerBuilder(
 				new BaselineSessionEventsListenerBuilder( logSessionMetrics, autoSessionEventsListener )
 		);
 
 		return settings;
 
 	}
 
 //	protected BytecodeProvider buildBytecodeProvider(String providerName) {
 //		if ( "javassist".equals( providerName ) ) {
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //		else {
 //            LOG.debug("Using javassist as bytecode provider by default");
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				AvailableSettings.QUERY_CACHE_FACTORY, properties, StandardQueryCacheFactory.class.getName()
 		);
 		LOG.debugf( "Query cache factory: %s", queryCacheFactoryClassName );
 		try {
 			return (QueryCacheFactory) serviceRegistry.getService( ClassLoaderService.class )
 					.classForName( queryCacheFactoryClassName )
 					.newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, e );
 		}
 	}
 	//todo remove this once we move to new metamodel
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
 		// todo : REMOVE!  THIS IS TOTALLY A TEMPORARY HACK FOR org.hibernate.cfg.AnnotationBinder which will be going away
 		String regionFactoryClassName = RegionFactoryInitiator.mapLegacyNames(
 				ConfigurationHelper.getString(
 						AvailableSettings.CACHE_REGION_FACTORY, properties, null
 				)
 		);
 		if ( regionFactoryClassName == null ) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
 		LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException e ) {
 				// no constructor accepting Properties found, try no arg constructor
 				LOG.debugf(
 						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
 						regionFactoryClassName
 				);
 				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
 						.newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 
 	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String className = ConfigurationHelper.getString(
 				AvailableSettings.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
 		);
 		LOG.debugf( "Query translator: %s", className );
 		try {
 			return (QueryTranslatorFactory) serviceRegistry.getService( ClassLoaderService.class )
 					.classForName( className )
 					.newInstance();
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate QueryTranslatorFactory: " + className, e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/SessionEventsManagerImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/SessionEventListenerManagerImpl.java
similarity index 73%
rename from hibernate-core/src/main/java/org/hibernate/engine/internal/SessionEventsManagerImpl.java
rename to hibernate-core/src/main/java/org/hibernate/engine/internal/SessionEventListenerManagerImpl.java
index b6066f8536..5097891fa6 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/SessionEventsManagerImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/SessionEventListenerManagerImpl.java
@@ -1,288 +1,288 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 import java.util.ArrayList;
 import java.util.List;
 
-import org.hibernate.SessionEventsListener;
-import org.hibernate.engine.spi.SessionEventsManager;
+import org.hibernate.SessionEventListener;
+import org.hibernate.engine.spi.SessionEventListenerManager;
 
 /**
  * @author Steve Ebersole
  */
-public class SessionEventsManagerImpl implements SessionEventsManager, Serializable {
-	private List<SessionEventsListener> listenerList;
+public class SessionEventListenerManagerImpl implements SessionEventListenerManager, Serializable {
+	private List<SessionEventListener> listenerList;
 
-	public void addListener(SessionEventsListener... listeners) {
+	public void addListener(SessionEventListener... listeners) {
 		if ( listenerList == null ) {
-			listenerList = new ArrayList<SessionEventsListener>();
+			listenerList = new ArrayList<SessionEventListener>();
 		}
 
 		java.util.Collections.addAll( listenerList, listeners );
 	}
 
 	@Override
 	public void transactionCompletion(boolean successful) {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.transactionCompletion( successful );
 		}
 	}
 
 	@Override
 	public void jdbcConnectionAcquisitionStart() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.jdbcConnectionAcquisitionStart();
 		}
 	}
 
 	@Override
 	public void jdbcConnectionAcquisitionEnd() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.jdbcConnectionAcquisitionEnd();
 		}
 	}
 
 	@Override
 	public void jdbcConnectionReleaseStart() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.jdbcConnectionReleaseStart();
 		}
 	}
 
 	@Override
 	public void jdbcConnectionReleaseEnd() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.jdbcConnectionReleaseEnd();
 		}
 	}
 
 	@Override
 	public void jdbcPrepareStatementStart() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.jdbcPrepareStatementStart();
 		}
 	}
 
 	@Override
 	public void jdbcPrepareStatementEnd() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.jdbcPrepareStatementEnd();
 		}
 	}
 
 	@Override
 	public void jdbcExecuteStatementStart() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.jdbcExecuteStatementStart();
 		}
 	}
 
 	@Override
 	public void jdbcExecuteStatementEnd() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.jdbcExecuteStatementEnd();
 		}
 	}
 
 	@Override
 	public void jdbcExecuteBatchStart() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.jdbcExecuteBatchStart();
 		}
 	}
 
 	@Override
 	public void jdbcExecuteBatchEnd() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.jdbcExecuteBatchEnd();
 		}
 	}
 
 	@Override
 	public void cachePutStart() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.cachePutStart();
 		}
 	}
 
 	@Override
 	public void cachePutEnd() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.cachePutEnd();
 		}
 	}
 
 	@Override
 	public void cacheGetStart() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.cacheGetStart();
 		}
 	}
 
 	@Override
 	public void cacheGetEnd(boolean hit) {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.cacheGetEnd( hit );
 		}
 	}
 
 	@Override
 	public void flushStart() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.flushStart();
 		}
 	}
 
 	@Override
 	public void flushEnd(int numberOfEntities, int numberOfCollections) {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.flushEnd( numberOfEntities, numberOfCollections );
 		}
 	}
 
 	@Override
 	public void partialFlushStart() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.partialFlushStart();
 		}
 	}
 
 	@Override
 	public void partialFlushEnd(int numberOfEntities, int numberOfCollections) {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.partialFlushEnd( numberOfEntities, numberOfCollections );
 		}
 	}
 
 	@Override
 	public void dirtyCalculationStart() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.dirtyCalculationStart();
 		}
 	}
 
 	@Override
 	public void dirtyCalculationEnd(boolean dirty) {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.dirtyCalculationEnd( dirty );
 		}
 	}
 
 	@Override
 	public void end() {
 		if ( listenerList == null ) {
 			return;
 		}
 
-		for ( SessionEventsListener listener : listenerList ) {
+		for ( SessionEventListener listener : listenerList ) {
 			listener.end();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/LoggingSessionEventsListener.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatisticalLoggingSessionEventListener.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/engine/internal/LoggingSessionEventsListener.java
rename to hibernate-core/src/main/java/org/hibernate/engine/internal/StatisticalLoggingSessionEventListener.java
index 9d667cba61..300c3e7b5b 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/LoggingSessionEventsListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatisticalLoggingSessionEventListener.java
@@ -1,314 +1,314 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 
 import org.jboss.logging.Logger;
 
-import org.hibernate.EmptySessionEventsListener;
+import org.hibernate.BaseSessionEventListener;
 
 /**
  * @author Steve Ebersole
  */
-public class LoggingSessionEventsListener extends EmptySessionEventsListener {
-	private static final Logger log = Logger.getLogger( LoggingSessionEventsListener.class );
+public class StatisticalLoggingSessionEventListener extends BaseSessionEventListener {
+	private static final Logger log = Logger.getLogger( StatisticalLoggingSessionEventListener.class );
 
 	/**
 	 * Used by SettingsFactory (in conjunction with stats being enabled) to determine whether to apply this listener
 	 *
 	 * @return {@code true} if logging is enabled for this listener.
 	 */
 	public static boolean isLoggingEnabled() {
 		return log.isInfoEnabled();
 	}
 
 	// cumulative state ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private int jdbcConnectionAcquisitionCount;
 	private long jdbcConnectionAcquisitionTime;
 
 	private int jdbcConnectionReleaseCount;
 	private long jdbcConnectionReleaseTime;
 
 	private int jdbcPrepareStatementCount;
 	private long jdbcPrepareStatementTime;
 
 	private int jdbcExecuteStatementCount;
 	private long jdbcExecuteStatementTime;
 
 	private int jdbcExecuteBatchCount;
 	private long jdbcExecuteBatchTime;
 
 	private int cachePutCount;
 	private long cachePutTime;
 
 	private int cacheHitCount;
 	private long cacheHitTime;
 
 	private int cacheMissCount;
 	private long cacheMissTime;
 
 	private int flushCount;
 	private long flushEntityCount;
 	private long flushCollectionCount;
 	private long flushTime;
 
 	private int partialFlushCount;
 	private long partialFlushEntityCount;
 	private long partialFlushCollectionCount;
 	private long partialFlushTime;
 
 
 	// JDBC Connection acquisition ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private long jdbcConnectionAcquisitionStart = -1;
 
 	@Override
 	public void jdbcConnectionAcquisitionStart() {
 		assert jdbcConnectionAcquisitionStart < 0 : "Nested calls to jdbcConnectionAcquisitionStart";
 		jdbcConnectionAcquisitionStart = System.nanoTime();
 	}
 
 	@Override
 	public void jdbcConnectionAcquisitionEnd() {
 		assert jdbcConnectionAcquisitionStart > 0:
 				"Unexpected call to jdbcConnectionAcquisitionEnd; expecting jdbcConnectionAcquisitionStart";
 
 		jdbcConnectionAcquisitionCount++;
 		jdbcConnectionAcquisitionTime += ( System.nanoTime() - jdbcConnectionAcquisitionStart );
 		jdbcConnectionAcquisitionStart = -1;
 	}
 
 
 	// JDBC Connection release ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private long jdbcConnectionReleaseStart = -1;
 
 	@Override
 	public void jdbcConnectionReleaseStart() {
 		assert jdbcConnectionReleaseStart < 0 : "Nested calls to jdbcConnectionReleaseStart";
 		jdbcConnectionReleaseStart = System.nanoTime();
 	}
 
 	@Override
 	public void jdbcConnectionReleaseEnd() {
 		assert jdbcConnectionReleaseStart > 0:
 				"Unexpected call to jdbcConnectionReleaseEnd; expecting jdbcConnectionReleaseStart";
 
 		jdbcConnectionReleaseCount++;
 		jdbcConnectionReleaseTime += ( System.nanoTime() - jdbcConnectionReleaseStart );
 		jdbcConnectionReleaseStart = -1;
 	}
 
 
 	// JDBC statement preparation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private long jdbcPrepStart = -1;
 
 	@Override
 	public void jdbcPrepareStatementStart() {
 		assert jdbcPrepStart < 0 : "Nested calls to jdbcPrepareStatementStart";
 		jdbcPrepStart = System.nanoTime();
 	}
 
 	@Override
 	public void jdbcPrepareStatementEnd() {
 		assert jdbcPrepStart > 0 : "Unexpected call to jdbcPrepareStatementEnd; expecting jdbcPrepareStatementStart";
 
 		jdbcPrepareStatementCount++;
 		jdbcPrepareStatementTime += ( System.nanoTime() - jdbcPrepStart );
 		jdbcPrepStart = -1;
 	}
 
 
 	// JDBC statement execution ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private long jdbcExecutionStart = -1;
 
 	@Override
 	public void jdbcExecuteStatementStart() {
 		assert jdbcExecutionStart < 0 : "Nested calls to jdbcExecuteStatementStart";
 		jdbcExecutionStart = System.nanoTime();
 	}
 
 	@Override
 	public void jdbcExecuteStatementEnd() {
 		assert jdbcExecutionStart > 0 : "Unexpected call to jdbcExecuteStatementEnd; expecting jdbcExecuteStatementStart";
 
 		jdbcExecuteStatementCount++;
 		jdbcExecuteStatementTime += ( System.nanoTime() - jdbcExecutionStart );
 		jdbcExecutionStart = -1;
 	}
 
 
 	// JDBC batch execution ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private long jdbcBatchExecutionStart = -1;
 
 	@Override
 	public void jdbcExecuteBatchStart() {
 		assert jdbcBatchExecutionStart < 0 : "Nested calls to jdbcExecuteBatchStart";
 		jdbcBatchExecutionStart = System.nanoTime();
 	}
 
 	@Override
 	public void jdbcExecuteBatchEnd() {
 		assert jdbcBatchExecutionStart > 0 : "Unexpected call to jdbcExecuteBatchEnd; expecting jdbcExecuteBatchStart";
 
 		jdbcExecuteBatchCount++;
 		jdbcExecuteBatchTime += ( System.nanoTime() - jdbcBatchExecutionStart );
 		jdbcBatchExecutionStart = -1;
 	}
 
 
 	// Caching - put  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private long cachePutStart = -1;
 
 	@Override
 	public void cachePutStart() {
 		assert cachePutStart < 0 : "Nested calls to cachePutStart";
 		cachePutStart = System.nanoTime();
 	}
 
 	@Override
 	public void cachePutEnd() {
 		assert cachePutStart > 0 : "Unexpected call to cachePutEnd; expecting cachePutStart";
 
 		cachePutCount++;
 		cachePutTime += ( System.nanoTime() - cachePutStart );
 		cachePutStart = -1;
 	}
 
 
 	// Caching - get  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private long cacheGetStart = -1;
 
 	@Override
 	public void cacheGetStart() {
 		assert cacheGetStart < 0 : "Nested calls to cacheGetStart";
 		cacheGetStart = System.nanoTime();
 	}
 
 	@Override
 	public void cacheGetEnd(boolean hit) {
 		assert cacheGetStart > 0 : "Unexpected call to cacheGetEnd; expecting cacheGetStart";
 
 		if ( hit ) {
 			cacheHitCount++;
 			cacheHitTime += ( System.nanoTime() - cacheGetStart );
 		}
 		else {
 			cacheMissCount++;
 			cacheMissTime += ( System.nanoTime() - cacheGetStart );
 		}
 		cacheGetStart = -1;
 	}
 
 
 	// Flushing  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private long flushStart = -1;
 
 	@Override
 	public void flushStart() {
 		assert flushStart < 0 : "Nested calls to flushStart";
 		flushStart = System.nanoTime();
 	}
 
 	@Override
 	public void flushEnd(int numberOfEntities, int numberOfCollections) {
 		assert flushStart > 0 : "Unexpected call to flushEnd; expecting flushStart";
 
 		flushCount++;
 		flushEntityCount += numberOfEntities;
 		flushCollectionCount += numberOfCollections;
 		flushTime += ( System.nanoTime() - flushStart );
 		flushStart = -1;
 	}
 
 
 	// Partial-flushing  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private long partialFlushStart = -1;
 
 	@Override
 	public void partialFlushStart() {
 		assert partialFlushStart < 0 : "Nested calls to partialFlushStart";
 		partialFlushStart = System.nanoTime();
 	}
 
 	@Override
 	public void partialFlushEnd(int numberOfEntities, int numberOfCollections) {
 		assert partialFlushStart > 0 : "Unexpected call to partialFlushEnd; expecting partialFlushStart";
 
 		partialFlushCount++;
 		partialFlushEntityCount += numberOfEntities;
 		partialFlushCollectionCount += numberOfCollections;
 		partialFlushTime += ( System.nanoTime() - flushStart );
 		partialFlushStart = -1;
 	}
 
 	@Override
 	public void end() {
 		log.infof(
 				"Session Metrics {\n" +
 						"    %s nanoseconds spent acquiring %s JDBC connections;\n" +
 						"    %s nanoseconds spent releasing %s JDBC connections;\n" +
 						"    %s nanoseconds spent preparing %s JDBC statements;\n" +
 						"    %s nanoseconds spent executing %s JDBC statements;\n" +
 						"    %s nanoseconds spent executing %s JDBC batches;\n" +
 						"    %s nanoseconds spent performing %s L2C puts;\n" +
 						"    %s nanoseconds spent performing %s L2C hits;\n" +
 						"    %s nanoseconds spent performing %s L2C misses;\n" +
 						"    %s nanoseconds spent executing %s flushes (flushing a total of %s entities and %s collections);\n" +
 						"    %s nanoseconds spent executing %s partial-flushes (flushing a total of %s entities and %s collections)\n" +
 						"}",
 				jdbcConnectionAcquisitionTime,
 				jdbcConnectionAcquisitionCount,
 				jdbcConnectionReleaseTime,
 				jdbcConnectionReleaseCount,
 				jdbcPrepareStatementTime,
 				jdbcPrepareStatementCount,
 				jdbcExecuteStatementTime,
 				jdbcExecuteStatementCount,
 				jdbcExecuteBatchTime,
 				jdbcExecuteBatchCount,
 				cachePutTime,
 				cachePutCount,
 				cacheHitTime,
 				cacheHitCount,
 				cacheMissTime,
 				cacheMissCount,
 				flushTime,
 				flushCount,
 				flushEntityCount,
 				flushCollectionCount,
 				partialFlushTime,
 				partialFlushCount,
 				partialFlushEntityCount,
 				partialFlushCollectionCount
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
index cca0cf04b6..f70726b92c 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
@@ -1,406 +1,406 @@
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
  * Functionality relating to the Hibernate two-phase loading process, that may be reused by persisters
  * that do not use the Loader framework
  *
  * @author Gavin King
  */
 public final class TwoPhaseLoad {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			TwoPhaseLoad.class.getName()
 	);
 
 	private TwoPhaseLoad() {
 	}
 
 	/**
 	 * Register the "hydrated" state of an entity instance, after the first step of 2-phase loading.
 	 *
 	 * Add the "hydrated state" (an array) of an uninitialized entity to the session. We don't try
 	 * to resolve any associations yet, because there might be other entities waiting to be
 	 * read from the JDBC result set we are currently processing
 	 *
 	 * @param persister The persister for the hydrated entity
 	 * @param id The entity identifier
 	 * @param values The entity values
 	 * @param rowId The rowId for the entity
 	 * @param object An optional instance for the entity being loaded
 	 * @param lockMode The lock mode
 	 * @param lazyPropertiesAreUnFetched Whether properties defined as lazy are yet un-fetched
 	 * @param session The Session
 	 */
 	public static void postHydrate(
 			final EntityPersister persister,
 			final Serializable id,
 			final Object[] values,
 			final Object rowId,
 			final Object object,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnFetched,
 			final SessionImplementor session) {
 		final Object version = Versioning.getVersion( values, persister );
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
 				lazyPropertiesAreUnFetched
 			);
 
 		if ( version != null && LOG.isTraceEnabled() ) {
 			final String versionStr = persister.isVersioned()
 					? persister.getVersionType().toLoggableString( version, session.getFactory() )
 					: "null";
 			LOG.tracef( "Version: %s", versionStr );
 		}
 	}
 
 	/**
 	 * Perform the second step of 2-phase load. Fully initialize the entity
 	 * instance.
 	 * <p/>
 	 * After processing a JDBC result set, we "resolve" all the associations
 	 * between the entities which were instantiated and had their state
 	 * "hydrated" into an array
 	 *
 	 * @param entity The entity being loaded
 	 * @param readOnly Is the entity being loaded as read-only
 	 * @param session The Session
 	 * @param preLoadEvent The (re-used) pre-load event
 	 */
 	public static void initializeEntity(
 			final Object entity,
 			final boolean readOnly,
 			final SessionImplementor session,
 			final PreLoadEvent preLoadEvent) {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityEntry entityEntry = persistenceContext.getEntry( entity );
 		if ( entityEntry == null ) {
 			throw new AssertionFailure( "possible non-threadsafe access to the session" );
 		}
 		doInitializeEntity( entity, entityEntry, readOnly, session, preLoadEvent );
 	}
 
 	private static void doInitializeEntity(
 			final Object entity,
 			final EntityEntry entityEntry,
 			final boolean readOnly,
 			final SessionImplementor session,
 			final PreLoadEvent preLoadEvent) throws HibernateException {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityPersister persister = entityEntry.getPersister();
 		final Serializable id = entityEntry.getId();
 		final Object[] hydratedState = entityEntry.getLoadedState();
 
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debugf(
 					"Resolving associations for %s",
 					MessageHelper.infoString( persister, id, session.getFactory() )
 			);
 		}
 
 		final Type[] types = persister.getPropertyTypes();
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
 
 			if ( debugEnabled ) {
 				LOG.debugf(
 						"Adding entity to second-level cache: %s",
 						MessageHelper.infoString( persister, id, session.getFactory() )
 				);
 			}
 
 			final Object version = Versioning.getVersion( hydratedState, persister );
 			final CacheEntry entry = persister.buildCacheEntry( entity, hydratedState, version, session );
 			final CacheKey cacheKey = session.generateCacheKey( id, persister.getIdentifierType(), persister.getRootEntityName() );
 
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
 				try {
-					session.getSessionEventsManager().cachePutStart();
+					session.getEventListenerManager().cachePutStart();
 					final boolean put = persister.getCacheAccessStrategy().putFromLoad(
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
 				finally {
-					session.getSessionEventsManager().cachePutEnd();
+					session.getEventListenerManager().cachePutEnd();
 				}
 			}
 		}
 
 		if ( persister.hasNaturalIdentifier() ) {
 			persistenceContext.getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					id,
 					persistenceContext.getNaturalIdHelper().extractNaturalIdValues( hydratedState, persister )
 			);
 		}
 
 		boolean isReallyReadOnly = readOnly;
 		if ( !persister.isMutable() ) {
 			isReallyReadOnly = true;
 		}
 		else {
 			final Object proxy = persistenceContext.getProxy( entityEntry.getEntityKey() );
 			if ( proxy != null ) {
 				// there is already a proxy for this impl
 				// only set the status to read-only if the proxy is read-only
 				isReallyReadOnly = ( (HibernateProxy) proxy ).getHibernateLazyInitializer().isReadOnly();
 			}
 		}
 		if ( isReallyReadOnly ) {
 			//no need to take a snapshot - this is a
 			//performance optimization, but not really
 			//important, except for entities with huge
 			//mutable property values
 			persistenceContext.setEntryStatus( entityEntry, Status.READ_ONLY );
 		}
 		else {
 			//take a snapshot
 			TypeHelper.deepCopy(
 					hydratedState,
 					persister.getPropertyTypes(),
 					persister.getPropertyUpdateability(),
 					//after setting values to object
 					hydratedState,
 					session
 			);
 			persistenceContext.setEntryStatus( entityEntry, Status.MANAGED );
 		}
 
 		persister.afterInitialize(
 				entity,
 				entityEntry.isLoadedWithLazyPropertiesUnfetched(),
 				session
 		);
 
 		if ( debugEnabled ) {
 			LOG.debugf(
 					"Done materializing entity %s",
 					MessageHelper.infoString( persister, id, session.getFactory() )
 			);
 		}
 
 		if ( factory.getStatistics().isStatisticsEnabled() ) {
 			factory.getStatisticsImplementor().loadEntity( persister.getEntityName() );
 		}
 	}
 	
 	/**
 	 * PostLoad cannot occur during initializeEntity, as that call occurs *before*
 	 * the Set collections are added to the persistence context by Loader.
 	 * Without the split, LazyInitializationExceptions can occur in the Entity's
 	 * postLoad if it acts upon the collection.
 	 *
 	 * HHH-6043
 	 * 
 	 * @param entity The entity
 	 * @param session The Session
 	 * @param postLoadEvent The (re-used) post-load event
 	 */
 	public static void postLoad(
 			final Object entity,
 			final SessionImplementor session,
 			final PostLoadEvent postLoadEvent) {
 		
 		if ( session.isEventSource() ) {
 			final PersistenceContext persistenceContext
 					= session.getPersistenceContext();
 			final EntityEntry entityEntry = persistenceContext.getEntry( entity );
 
 			postLoadEvent.setEntity( entity ).setId( entityEntry.getId() ).setPersister( entityEntry.getPersister() );
 
 			final EventListenerGroup<PostLoadEventListener> listenerGroup = session.getFactory()
 							.getServiceRegistry()
 							.getService( EventListenerRegistry.class )
 							.getEventListenerGroup( EventType.POST_LOAD );
 			for ( PostLoadEventListener listener : listenerGroup.listeners() ) {
 				listener.onPostLoad( postLoadEvent );
 			}
 		}
 	}
 
 	private static boolean useMinimalPuts(SessionImplementor session, EntityEntry entityEntry) {
 		return ( session.getFactory().getSettings().isMinimalPutsEnabled()
 				&& session.getCacheMode()!=CacheMode.REFRESH )
 				|| ( entityEntry.getPersister().hasLazyProperties()
 				&& entityEntry.isLoadedWithLazyPropertiesUnfetched()
 				&& entityEntry.getPersister().isLazyPropertiesCacheable() );
 	}
 
 	/**
 	 * Add an uninitialized instance of an entity class, as a placeholder to ensure object
 	 * identity. Must be called before <tt>postHydrate()</tt>.
 	 *
 	 * Create a "temporary" entry for a newly instantiated entity. The entity is uninitialized,
 	 * but we need the mapping from id to instance in order to guarantee uniqueness.
 	 *
 	 * @param key The entity key
 	 * @param object The entity instance
 	 * @param persister The entity persister
 	 * @param lockMode The lock mode
 	 * @param lazyPropertiesAreUnFetched Are lazy properties still un-fetched?
 	 * @param session The Session
 	 */
 	public static void addUninitializedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnFetched,
 			final SessionImplementor session) {
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
 				lazyPropertiesAreUnFetched
 		);
 	}
 
 	/**
 	 * Same as {@link #addUninitializedEntity}, but here for an entity from the second level cache
 	 *
 	 * @param key The entity key
 	 * @param object The entity instance
 	 * @param persister The entity persister
 	 * @param lockMode The lock mode
 	 * @param lazyPropertiesAreUnFetched Are lazy properties still un-fetched?
 	 * @param version The version
 	 * @param session The Session
 	 */
 	public static void addUninitializedCachedEntity(
 			final EntityKey key,
 			final Object object,
 			final EntityPersister persister,
 			final LockMode lockMode,
 			final boolean lazyPropertiesAreUnFetched,
 			final Object version,
 			final SessionImplementor session) {
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
 				lazyPropertiesAreUnFetched
 			);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
index f7472162ce..9d15f6ca8c 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
@@ -1,384 +1,384 @@
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
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
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
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( CollectionLoadContext.class );
 
 	private final LoadContexts loadContexts;
 	private final ResultSet resultSet;
 	private Set<CollectionKey> localLoadingCollectionKeys = new HashSet<CollectionKey>();
 
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
 					// ignore this row of results! Note the early exit
 					return null;
 				}
 				LOG.trace( "Collection not yet initialized; initializing" );
 			}
 			else {
 				final Object owner = loadContexts.getPersistenceContext().getCollectionOwner( key, persister );
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
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
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
 		List<LoadingCollectionEntry> matches = null;
 		final Iterator itr = localLoadingCollectionKeys.iterator();
 		while ( itr.hasNext() ) {
 			final CollectionKey collectionKey = (CollectionKey) itr.next();
 			final LoadingCollectionEntry lce = loadContexts.locateLoadingCollectionEntry( collectionKey );
 			if ( lce == null ) {
 				LOG.loadingCollectionKeyNotFound( collectionKey );
 			}
 			else if ( lce.getResultSet() == resultSet && lce.getPersister() == persister ) {
 				if ( matches == null ) {
 					matches = new ArrayList<LoadingCollectionEntry>();
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
 				itr.remove();
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
 
 	private void endLoadingCollections(CollectionPersister persister, List<LoadingCollectionEntry> matchedCollectionEntries) {
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( matchedCollectionEntries == null ) {
 			if ( debugEnabled ) {
 				LOG.debugf( "No collections were found in result set for role: %s", persister.getRole() );
 			}
 			return;
 		}
 
 		final int count = matchedCollectionEntries.size();
 		if ( debugEnabled ) {
 			LOG.debugf( "%s collections were found in result set for role: %s", count, persister.getRole() );
 		}
 
 		for ( LoadingCollectionEntry matchedCollectionEntry : matchedCollectionEntries ) {
 			endLoadingCollection( matchedCollectionEntry, persister );
 		}
 
 		if ( debugEnabled ) {
 			LOG.debugf( "%s collections initialized for role: %s", count, persister.getRole() );
 		}
 	}
 
 	private void endLoadingCollection(LoadingCollectionEntry lce, CollectionPersister persister) {
 		LOG.tracev( "Ending loading collection [{0}]", lce );
 		final SessionImplementor session = getLoadContext().getPersistenceContext().getSession();
 
 		// warning: can cause a recursive calls! (proxy initialization)
 		final boolean hasNoQueuedAdds = lce.getCollection().endRead();
 
 		if ( persister.getCollectionType().hasHolder() ) {
 			getLoadContext().getPersistenceContext().addCollectionHolder( lce.getCollection() );
 		}
 
 		CollectionEntry ce = getLoadContext().getPersistenceContext().getCollectionEntry( lce.getCollection() );
 		if ( ce == null ) {
 			ce = getLoadContext().getPersistenceContext().addInitializedCollection( persister, lce.getCollection(), lce.getKey() );
 		}
 		else {
 			ce.postInitialize( lce.getCollection() );
 //			if (ce.getLoadedPersister().getBatchSize() > 1) { // not the best place for doing this, moved into ce.postInitialize
 //				getLoadContext().getPersistenceContext().getBatchFetchQueue().removeBatchLoadableCollection(ce); 
 //			}
 		}
 
 
 		// add to cache if:
 		boolean addToCache =
 				// there were no queued additions
 				hasNoQueuedAdds
 				// and the role has a cache
 				&& persister.hasCache()
 				// and this is not a forced initialization during flush
 				&& session.getCacheMode().isPutEnabled() && !ce.isDoremove();
 		if ( addToCache ) {
 			addCollectionToCache( lce, persister );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Collection fully initialized: %s",
 					MessageHelper.collectionInfoString( persister, lce.getCollection(), lce.getKey(), session )
 			);
 		}
 		if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 			session.getFactory().getStatisticsImplementor().loadCollection( persister.getRole() );
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
 
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debugf( "Caching collection: %s", MessageHelper.collectionInfoString( persister, lce.getCollection(), lce.getKey(), session ) );
 		}
 
 		if ( !session.getEnabledFilters().isEmpty() && persister.isAffectedByEnabledFilters( session ) ) {
 			// some filters affecting the collection are enabled on the session, so do not do the put into the cache.
 			if ( debugEnabled ) {
 				LOG.debug( "Refusing to add to cache due to enabled filters" );
 			}
 			// todo : add the notion of enabled filters to the CacheKey to differentiate filtered collections from non-filtered;
 			//      but CacheKey is currently used for both collections and entities; would ideally need to define two seperate ones;
 			//      currently this works in conjuction with the check on
 			//      DefaultInitializeCollectionEventHandler.initializeCollectionFromCache() (which makes sure to not read from
 			//      cache with enabled filters).
 			// EARLY EXIT!!!!!
 			return;
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
 					final Object linkedOwner = lce.getCollection().getOwner();
 					if ( linkedOwner != null ) {
 						final Serializable ownerKey = persister.getOwnerEntityPersister().getIdentifier( linkedOwner, session );
 						collectionOwner = getLoadContext().getPersistenceContext().getCollectionOwner( ownerKey, persister );
 					}
 				}
 				if ( collectionOwner == null ) {
 					throw new HibernateException(
 							"Unable to resolve owner of loading collection [" +
 									MessageHelper.collectionInfoString( persister, lce.getCollection(), lce.getKey(), session ) +
 									"] for second level caching"
 					);
 				}
 			}
 			version = getLoadContext().getPersistenceContext().getEntry( collectionOwner ).getVersion();
 		}
 		else {
 			version = null;
 		}
 
 		final CollectionCacheEntry entry = new CollectionCacheEntry( lce.getCollection(), persister );
 		final CacheKey cacheKey = session.generateCacheKey( lce.getKey(), persister.getKeyType(), persister.getRole() );
 
 		try {
-			session.getSessionEventsManager().cachePutStart();
+			session.getEventListenerManager().cachePutStart();
 			final boolean put = persister.getCacheAccessStrategy().putFromLoad(
 					cacheKey,
 					persister.getCacheEntryStructure().structure( entry ),
 					session.getTimestamp(),
 					version,
 					factory.getSettings().isMinimalPutsEnabled() && session.getCacheMode()!= CacheMode.REFRESH
 			);
 
 			if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatisticsImplementor().secondLevelCachePut( persister.getCacheAccessStrategy().getRegion().getName() );
 			}
 		}
 		finally {
-			session.getSessionEventsManager().cachePutEnd();
+			session.getEventListenerManager().cachePutEnd();
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java
index 085a475d28..884ac8aa09 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java
@@ -1,813 +1,813 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.IdentifierLoadAccess;
 import org.hibernate.Interceptor;
 import org.hibernate.LobHelper;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.NaturalIdLoadAccess;
 import org.hibernate.Query;
 import org.hibernate.ReplicationMode;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
-import org.hibernate.SessionEventsListener;
+import org.hibernate.SessionEventListener;
 import org.hibernate.SessionFactory;
 import org.hibernate.SharedSessionBuilder;
 import org.hibernate.SimpleNaturalIdLoadAccess;
 import org.hibernate.Transaction;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.stat.SessionStatistics;
 import org.hibernate.type.Type;
 
 /**
  * This class is meant to be extended.
  * 
  * Wraps and delegates all methods to a {@link SessionImplementor} and
  * a {@link Session}. This is useful for custom implementations of this
  * API so that only some methods need to be overridden
  * (Used by Hibernate Search).
  * 
  * @author Sanne Grinovero <sanne@hibernate.org> (C) 2012 Red Hat Inc.
  */
 public class SessionDelegatorBaseImpl implements SessionImplementor, Session {
 
 	protected final SessionImplementor sessionImplementor;
 	protected final Session session;
 
 	public SessionDelegatorBaseImpl(SessionImplementor sessionImplementor, Session session) {
 		if ( sessionImplementor == null ) {
 			throw new IllegalArgumentException( "Unable to create a SessionDelegatorBaseImpl from a null sessionImplementor object" );
 		}
 		if ( session == null ) {
 			throw new IllegalArgumentException( "Unable to create a SessionDelegatorBaseImpl from a null session object" );
 		}
 		this.sessionImplementor = sessionImplementor;
 		this.session = session;
 	}
 
 	// Delegates to SessionImplementor
 
 	@Override
 	public <T> T execute(Callback<T> callback) {
 		return sessionImplementor.execute( callback );
 	}
 
 	@Override
 	public String getTenantIdentifier() {
 		return sessionImplementor.getTenantIdentifier();
 	}
 
 	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		return sessionImplementor.getJdbcConnectionAccess();
 	}
 
 	@Override
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
 		return sessionImplementor.generateEntityKey( id, persister );
 	}
 
 	@Override
 	public CacheKey generateCacheKey(Serializable id, Type type, String entityOrRoleName) {
 		return sessionImplementor.generateCacheKey( id, type, entityOrRoleName );
 	}
 
 	@Override
 	public Interceptor getInterceptor() {
 		return sessionImplementor.getInterceptor();
 	}
 
 	@Override
 	public void setAutoClear(boolean enabled) {
 		sessionImplementor.setAutoClear( enabled );
 	}
 
 	@Override
 	public void disableTransactionAutoJoin() {
 		sessionImplementor.disableTransactionAutoJoin();
 	}
 
 	@Override
 	public boolean isTransactionInProgress() {
 		return sessionImplementor.isTransactionInProgress();
 	}
 
 	@Override
 	public void initializeCollection(PersistentCollection collection, boolean writing) throws HibernateException {
 		sessionImplementor.initializeCollection( collection, writing );
 	}
 
 	@Override
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
 		return sessionImplementor.internalLoad( entityName, id, eager, nullable );
 	}
 
 	@Override
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
 		return sessionImplementor.immediateLoad( entityName, id );
 	}
 
 	@Override
 	public long getTimestamp() {
 		return sessionImplementor.getTimestamp();
 	}
 
 	@Override
 	public SessionFactoryImplementor getFactory() {
 		return sessionImplementor.getFactory();
 	}
 
 	@Override
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.list( query, queryParameters );
 	}
 
 	@Override
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.iterate( query, queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.scroll( query, queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
 		return sessionImplementor.scroll( criteria, scrollMode );
 	}
 
 	@Override
 	public List list(Criteria criteria) {
 		return sessionImplementor.list( criteria );
 	}
 
 	@Override
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.listFilter( collection, filter, queryParameters );
 	}
 
 	@Override
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.iterateFilter( collection, filter, queryParameters );
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException {
 		return sessionImplementor.getEntityPersister( entityName, object );
 	}
 
 	@Override
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		return sessionImplementor.getEntityUsingInterceptor( key );
 	}
 
 	@Override
 	public Serializable getContextEntityIdentifier(Object object) {
 		return sessionImplementor.getContextEntityIdentifier( object );
 	}
 
 	@Override
 	public String bestGuessEntityName(Object object) {
 		return sessionImplementor.bestGuessEntityName( object );
 	}
 
 	@Override
 	public String guessEntityName(Object entity) throws HibernateException {
 		return sessionImplementor.guessEntityName( entity );
 	}
 
 	@Override
 	public Object instantiate(String entityName, Serializable id) throws HibernateException {
 		return sessionImplementor.instantiate( entityName, id );
 	}
 
 	@Override
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.listCustomQuery( customQuery, queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.scrollCustomQuery( customQuery, queryParameters );
 	}
 
 	@Override
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.list( spec, queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.scroll( spec, queryParameters );
 	}
 
 	@Override
 	public Object getFilterParameterValue(String filterParameterName) {
 		return sessionImplementor.getFilterParameterValue( filterParameterName );
 	}
 
 	@Override
 	public Type getFilterParameterType(String filterParameterName) {
 		return sessionImplementor.getFilterParameterType( filterParameterName );
 	}
 
 	@Override
 	public Map getEnabledFilters() {
 		return sessionImplementor.getEnabledFilters();
 	}
 
 	@Override
 	public int getDontFlushFromFind() {
 		return sessionImplementor.getDontFlushFromFind();
 	}
 
 	@Override
 	public PersistenceContext getPersistenceContext() {
 		return sessionImplementor.getPersistenceContext();
 	}
 
 	@Override
 	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.executeUpdate( query, queryParameters );
 	}
 
 	@Override
 	public int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters) throws HibernateException {
 		return sessionImplementor.executeNativeUpdate( specification, queryParameters );
 	}
 
 	@Override
 	public NonFlushedChanges getNonFlushedChanges() throws HibernateException {
 		return sessionImplementor.getNonFlushedChanges();
 	}
 
 	@Override
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) throws HibernateException {
 		sessionImplementor.applyNonFlushedChanges( nonFlushedChanges );
 	}
 
 	@Override
 	public CacheMode getCacheMode() {
 		return sessionImplementor.getCacheMode();
 	}
 
 	@Override
 	public void setCacheMode(CacheMode cm) {
 		sessionImplementor.setCacheMode( cm );
 	}
 
 	@Override
 	public boolean isOpen() {
 		return sessionImplementor.isOpen();
 	}
 
 	@Override
 	public boolean isConnected() {
 		return sessionImplementor.isConnected();
 	}
 
 	@Override
 	public FlushMode getFlushMode() {
 		return sessionImplementor.getFlushMode();
 	}
 
 	@Override
 	public void setFlushMode(FlushMode fm) {
 		sessionImplementor.setFlushMode( fm );
 	}
 
 	@Override
 	public Connection connection() {
 		return sessionImplementor.connection();
 	}
 
 	@Override
 	public void flush() {
 		sessionImplementor.flush();
 	}
 
 	@Override
 	public Query getNamedQuery(String name) {
 		return sessionImplementor.getNamedQuery( name );
 	}
 
 	@Override
 	public Query getNamedSQLQuery(String name) {
 		return sessionImplementor.getNamedSQLQuery( name );
 	}
 
 	@Override
 	public boolean isEventSource() {
 		return sessionImplementor.isEventSource();
 	}
 
 	@Override
 	public void afterScrollOperation() {
 		sessionImplementor.afterScrollOperation();
 	}
 
 	@Override
 	public String getFetchProfile() {
 		return sessionImplementor.getFetchProfile();
 	}
 
 	@Override
 	public void setFetchProfile(String name) {
 		sessionImplementor.setFetchProfile( name );
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		return sessionImplementor.getTransactionCoordinator();
 	}
 
 	@Override
 	public boolean isClosed() {
 		return sessionImplementor.isClosed();
 	}
 
 	@Override
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return sessionImplementor.getLoadQueryInfluencers();
 	}
 
 	@Override
 	public Query createQuery(NamedQueryDefinition namedQueryDefinition) {
 		return sessionImplementor.createQuery( namedQueryDefinition );
 	}
 
 	@Override
 	public SQLQuery createSQLQuery(NamedSQLQueryDefinition namedQueryDefinition) {
 		return sessionImplementor.createSQLQuery( namedQueryDefinition );
 	}
 
 	@Override
-	public SessionEventsManager getSessionEventsManager() {
-		return sessionImplementor.getSessionEventsManager();
+	public SessionEventListenerManager getEventListenerManager() {
+		return sessionImplementor.getEventListenerManager();
 	}
 
 	// Delegates to Session
 
 	@Override
 	public Transaction beginTransaction() {
 		return session.beginTransaction();
 	}
 
 	@Override
 	public Transaction getTransaction() {
 		return session.getTransaction();
 	}
 
 	@Override
 	public Query createQuery(String queryString) {
 		return session.createQuery( queryString );
 	}
 
 	@Override
 	public SQLQuery createSQLQuery(String queryString) {
 		return session.createSQLQuery( queryString );
 	}
 
 	@Override
 	public ProcedureCall getNamedProcedureCall(String name) {
 		return session.getNamedProcedureCall( name );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName) {
 		return session.createStoredProcedureCall( procedureName );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		return session.createStoredProcedureCall( procedureName, resultClasses );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		return session.createStoredProcedureCall( procedureName, resultSetMappings );
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass) {
 		return session.createCriteria( persistentClass );
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		return session.createCriteria( persistentClass, alias );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName) {
 		return session.createCriteria( entityName );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName, String alias) {
 		return session.createCriteria( entityName, alias );
 	}
 
 	@Override
 	public SharedSessionBuilder sessionWithOptions() {
 		return session.sessionWithOptions();
 	}
 
 	@Override
 	public SessionFactory getSessionFactory() {
 		return session.getSessionFactory();
 	}
 
 	@Override
 	public Connection close() throws HibernateException {
 		return session.close();
 	}
 
 	@Override
 	public void cancelQuery() throws HibernateException {
 		session.cancelQuery();
 	}
 
 	@Override
 	public boolean isDirty() throws HibernateException {
 		return session.isDirty();
 	}
 
 	@Override
 	public boolean isDefaultReadOnly() {
 		return session.isDefaultReadOnly();
 	}
 
 	@Override
 	public void setDefaultReadOnly(boolean readOnly) {
 		session.setDefaultReadOnly( readOnly );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) {
 		return session.getIdentifier( object );
 	}
 
 	@Override
 	public boolean contains(Object object) {
 		return session.contains( object );
 	}
 
 	@Override
 	public void evict(Object object) {
 		session.evict( object );
 	}
 
 	@Override
 	public Object load(Class theClass, Serializable id, LockMode lockMode) {
 		return session.load( theClass, id, lockMode );
 	}
 
 	@Override
 	public Object load(Class theClass, Serializable id, LockOptions lockOptions) {
 		return session.load( theClass, id, lockOptions );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id, LockMode lockMode) {
 		return session.load( entityName, id, lockMode );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) {
 		return session.load( entityName, id, lockOptions );
 	}
 
 	@Override
 	public Object load(Class theClass, Serializable id) {
 		return session.load( theClass, id );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id) {
 		return session.load( entityName, id );
 	}
 
 	@Override
 	public void load(Object object, Serializable id) {
 		session.load( object, id );
 	}
 
 	@Override
 	public void replicate(Object object, ReplicationMode replicationMode) {
 		session.replicate( object, replicationMode );
 	}
 
 	@Override
 	public void replicate(String entityName, Object object, ReplicationMode replicationMode) {
 		session.replicate( entityName, object, replicationMode );
 	}
 
 	@Override
 	public Serializable save(Object object) {
 		return session.save( object );
 	}
 
 	@Override
 	public Serializable save(String entityName, Object object) {
 		return session.save( entityName, object );
 	}
 
 	@Override
 	public void saveOrUpdate(Object object) {
 		session.saveOrUpdate( object );
 	}
 
 	@Override
 	public void saveOrUpdate(String entityName, Object object) {
 		session.saveOrUpdate( entityName, object );
 	}
 
 	@Override
 	public void update(Object object) {
 		session.update( object );
 	}
 
 	@Override
 	public void update(String entityName, Object object) {
 		session.update( entityName, object );
 	}
 
 	@Override
 	public Object merge(Object object) {
 		return session.merge( object );
 	}
 
 	@Override
 	public Object merge(String entityName, Object object) {
 		return session.merge( entityName, object );
 	}
 
 	@Override
 	public void persist(Object object) {
 		session.persist( object );
 	}
 
 	@Override
 	public void persist(String entityName, Object object) {
 		session.persist( entityName, object );
 	}
 
 	@Override
 	public void delete(Object object) {
 		session.delete( object );
 	}
 
 	@Override
 	public void delete(String entityName, Object object) {
 		session.delete( entityName, object );
 	}
 
 	@Override
 	public void lock(Object object, LockMode lockMode) {
 		session.lock( object, lockMode );
 	}
 
 	@Override
 	public void lock(String entityName, Object object, LockMode lockMode) {
 		session.lock( entityName, object, lockMode );
 	}
 
 	@Override
 	public LockRequest buildLockRequest(LockOptions lockOptions) {
 		return session.buildLockRequest( lockOptions );
 	}
 
 	@Override
 	public void refresh(Object object) {
 		session.refresh( object );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object) {
 		session.refresh( entityName, object );
 	}
 
 	@Override
 	public void refresh(Object object, LockMode lockMode) {
 		session.refresh( object, lockMode );
 	}
 
 	@Override
 	public void refresh(Object object, LockOptions lockOptions) {
 		session.refresh( object, lockOptions );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object, LockOptions lockOptions) {
 		session.refresh( entityName, object, lockOptions );
 	}
 
 	@Override
 	public LockMode getCurrentLockMode(Object object) {
 		return session.getCurrentLockMode( object );
 	}
 
 	@Override
 	public Query createFilter(Object collection, String queryString) {
 		return session.createFilter( collection, queryString );
 	}
 
 	@Override
 	public void clear() {
 		session.clear();
 	}
 
 	@Override
 	public Object get(Class clazz, Serializable id) {
 		return session.get( clazz, id );
 	}
 
 	@Override
 	public Object get(Class clazz, Serializable id, LockMode lockMode) {
 		return session.get( clazz, id, lockMode );
 	}
 
 	@Override
 	public Object get(Class clazz, Serializable id, LockOptions lockOptions) {
 		return session.get( clazz, id, lockOptions );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id) {
 		return session.get( entityName, id );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockMode lockMode) {
 		return session.get( entityName, id, lockMode );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) {
 		return session.get( entityName, id, lockOptions );
 	}
 
 	@Override
 	public String getEntityName(Object object) {
 		return session.getEntityName( object );
 	}
 
 	@Override
 	public IdentifierLoadAccess byId(String entityName) {
 		return session.byId( entityName );
 	}
 
 	@Override
 	public IdentifierLoadAccess byId(Class entityClass) {
 		return session.byId( entityClass );
 	}
 
 	@Override
 	public NaturalIdLoadAccess byNaturalId(String entityName) {
 		return session.byNaturalId( entityName );
 	}
 
 	@Override
 	public NaturalIdLoadAccess byNaturalId(Class entityClass) {
 		return session.byNaturalId( entityClass );
 	}
 
 	@Override
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName) {
 		return session.bySimpleNaturalId( entityName );
 	}
 
 	@Override
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(Class entityClass) {
 		return session.bySimpleNaturalId( entityClass );
 	}
 
 	@Override
 	public Filter enableFilter(String filterName) {
 		return session.enableFilter( filterName );
 	}
 
 	@Override
 	public Filter getEnabledFilter(String filterName) {
 		return session.getEnabledFilter( filterName );
 	}
 
 	@Override
 	public void disableFilter(String filterName) {
 		session.disableFilter( filterName );
 	}
 
 	@Override
 	public SessionStatistics getStatistics() {
 		return session.getStatistics();
 	}
 
 	@Override
 	public boolean isReadOnly(Object entityOrProxy) {
 		return session.isReadOnly( entityOrProxy );
 	}
 
 	@Override
 	public void setReadOnly(Object entityOrProxy, boolean readOnly) {
 		session.setReadOnly( entityOrProxy, readOnly );
 	}
 
 	@Override
 	public void doWork(Work work) throws HibernateException {
 		session.doWork( work );
 	}
 
 	@Override
 	public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException {
 		return session.doReturningWork( work );
 	}
 
 	@Override
 	public Connection disconnect() {
 		return session.disconnect();
 	}
 
 	@Override
 	public void reconnect(Connection connection) {
 		session.reconnect( connection );
 	}
 
 	@Override
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		return session.isFetchProfileEnabled( name );
 	}
 
 	@Override
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		session.enableFetchProfile( name );
 	}
 
 	@Override
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		session.disableFetchProfile( name );
 	}
 
 	@Override
 	public TypeHelper getTypeHelper() {
 		return session.getTypeHelper();
 	}
 
 	@Override
 	public LobHelper getLobHelper() {
 		return session.getLobHelper();
 	}
 
 	@Override
-	public void addEventsListeners(SessionEventsListener... listeners) {
-		session.addEventsListeners( listeners );
+	public void addEventListeners(SessionEventListener... listeners) {
+		session.addEventListeners( listeners );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionEventsManager.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionEventListenerManager.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/engine/spi/SessionEventsManager.java
rename to hibernate-core/src/main/java/org/hibernate/engine/spi/SessionEventListenerManager.java
index 86eba47ca1..8f26665502 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionEventsManager.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionEventListenerManager.java
@@ -1,32 +1,32 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
 
-import org.hibernate.SessionEventsListener;
+import org.hibernate.SessionEventListener;
 
 /**
  * @author Steve Ebersole
  */
-public interface SessionEventsManager extends SessionEventsListener {
+public interface SessionEventListenerManager extends SessionEventListener {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionImplementor.java
index 74904531fa..f970384f77 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionImplementor.java
@@ -1,412 +1,412 @@
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
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Query;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
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
 	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode);
 	/**
 	 * Execute a criteria query
 	 */
 	public List list(Criteria criteria);
 
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
 	 * @deprecated use {@link #getLoadQueryInfluencers} instead
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
 
 	/**
 	 * Used from EntityManager
 	 *
 	 * @param namedQueryDefinition The named query definition
 	 *
 	 * @return The basic HQL/JPQL query (without saved settings applied)
 	 */
 	Query createQuery(NamedQueryDefinition namedQueryDefinition);
 
 	/**
 	 * Used from EntityManager
 	 *
 	 * @param namedQueryDefinition The named query definition
 	 *
 	 * @return The basic SQL query (without saved settings applied)
 	 */
 	SQLQuery createSQLQuery(NamedSQLQueryDefinition namedQueryDefinition);
 
-	public SessionEventsManager getSessionEventsManager();
+	public SessionEventListenerManager getEventListenerManager();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultAutoFlushEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultAutoFlushEventListener.java
index 913f6292dd..a290997809 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultAutoFlushEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultAutoFlushEventListener.java
@@ -1,104 +1,104 @@
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
 
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.event.spi.AutoFlushEvent;
 import org.hibernate.event.spi.AutoFlushEventListener;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Defines the default flush event listeners used by hibernate for
  * flushing session state in response to generated auto-flush events.
  *
  * @author Steve Ebersole
  */
 public class DefaultAutoFlushEventListener extends AbstractFlushingEventListener implements AutoFlushEventListener {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, DefaultAutoFlushEventListener.class.getName() );
 
 	/**
 	 * Handle the given auto-flush event.
 	 * 
 	 * @param event
 	 *            The auto-flush event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onAutoFlush(AutoFlushEvent event) throws HibernateException {
 		final EventSource source = event.getSession();
 		try {
-			source.getSessionEventsManager().partialFlushStart();
+			source.getEventListenerManager().partialFlushStart();
 
 			if ( flushMightBeNeeded(source) ) {
 				// Need to get the number of collection removals before flushing to executions
 				// (because flushing to executions can add collection removal actions to the action queue).
 				final int oldSize = source.getActionQueue().numberOfCollectionRemovals();
 				flushEverythingToExecutions(event);
 				if ( flushIsReallyNeeded(event, source) ) {
 					LOG.trace( "Need to execute flush" );
 
 					// note: performExecutions() clears all collectionXxxxtion
 					// collections (the collection actions) in the session
 					performExecutions(source);
 					postFlush(source);
 
 					postPostFlush( source );
 
 					if ( source.getFactory().getStatistics().isStatisticsEnabled() ) {
 						source.getFactory().getStatisticsImplementor().flush();
 					}
 				}
 				else {
 					LOG.trace( "Don't need to execute flush" );
 					source.getActionQueue().clearFromFlushNeededCheck( oldSize );
 				}
 
 				event.setFlushRequired( flushIsReallyNeeded( event, source ) );
 			}
 		}
 		finally {
-			source.getSessionEventsManager().partialFlushEnd(
+			source.getEventListenerManager().partialFlushEnd(
 					event.getNumberOfEntitiesProcessed(),
 					event.getNumberOfEntitiesProcessed()
 			);
 		}
 	}
 
 	private boolean flushIsReallyNeeded(AutoFlushEvent event, final EventSource source) {
 		return source.getActionQueue()
 				.areTablesToBeUpdated( event.getQuerySpaces() ) ||
 						source.getFlushMode()==FlushMode.ALWAYS;
 	}
 
 	private boolean flushMightBeNeeded(final EventSource source) {
 		return !source.getFlushMode().lessThan(FlushMode.AUTO) &&
 				source.getDontFlushFromFind() == 0 &&
 				( source.getPersistenceContext().getNumberOfManagedEntities() > 0 ||
 						source.getPersistenceContext().getCollectionEntries().size() > 0 );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
index 56751c488f..f030d0390d 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
@@ -1,680 +1,680 @@
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
 
 import org.hibernate.engine.spi.SelfDirtinessTracker;
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
 					: session.getPersistenceContext().getNaturalIdHelper().extractNaturalIdValues( loaded, persister );
 
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
             if(entity instanceof SelfDirtinessTracker) {
                 if(((SelfDirtinessTracker) entity).$$_hibernate_hasDirtyAttributes()) {
                    dirtyProperties = persister.resolveAttributeIndexes(((SelfDirtinessTracker) entity).$$_hibernate_getDirtyAttributes());
                 }
             }
             else {
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
 		}
 
 		event.setDatabaseSnapshot(null);
 
 		final boolean interceptorHandledDirtyCheck;
 		boolean cannotDirtyCheck;
 
 		if ( dirtyProperties==null ) {
 			// Interceptor returned null, so do the dirtycheck ourself, if possible
 			try {
-				session.getSessionEventsManager().dirtyCalculationStart();
+				session.getEventListenerManager().dirtyCalculationStart();
 
 				interceptorHandledDirtyCheck = false;
 				// object loaded by update()
 				cannotDirtyCheck = loadedState==null;
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
 			finally {
-				session.getSessionEventsManager().dirtyCalculationEnd( dirtyProperties != null );
+				session.getEventListenerManager().dirtyCalculationEnd( dirtyProperties != null );
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
 		if ( dirtyProperties != null && dirtyProperties.length > 0 && LOG.isTraceEnabled() ) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEventListener.java
index 31bdbc21ee..54cbb6b8fc 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEventListener.java
@@ -1,73 +1,73 @@
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
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.FlushEvent;
 import org.hibernate.event.spi.FlushEventListener;
 
 /**
  * Defines the default flush event listeners used by hibernate for 
  * flushing session state in response to generated flush events.
  *
  * @author Steve Ebersole
  */
 public class DefaultFlushEventListener extends AbstractFlushingEventListener implements FlushEventListener {
 
 	/** Handle the given flush event.
 	 *
 	 * @param event The flush event to be handled.
 	 * @throws HibernateException
 	 */
 	public void onFlush(FlushEvent event) throws HibernateException {
 		final EventSource source = event.getSession();
 		final PersistenceContext persistenceContext = source.getPersistenceContext();
 
 		if ( persistenceContext.getNumberOfManagedEntities() > 0 ||
 				persistenceContext.getCollectionEntries().size() > 0 ) {
 
 			try {
-				source.getSessionEventsManager().flushStart();
+				source.getEventListenerManager().flushStart();
 
 				flushEverythingToExecutions( event );
 				performExecutions( source );
 				postFlush( source );
 			}
 			finally {
-				source.getSessionEventsManager().flushEnd(
+				source.getEventListenerManager().flushEnd(
 						event.getNumberOfEntitiesProcessed(),
 						event.getNumberOfCollectionsProcessed()
 				);
 			}
 
 			postPostFlush( source );
 
 			if ( source.getFactory().getStatistics().isStatisticsEnabled() ) {
 				source.getFactory().getStatisticsImplementor().flush();
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java
index 3f9d1eddc6..9d8fc37aba 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultInitializeCollectionEventListener.java
@@ -1,161 +1,161 @@
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
 import org.jboss.logging.Logger;
 
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
 			final boolean traceEnabled = LOG.isTraceEnabled();
 			if ( traceEnabled ) {
 				LOG.tracev( "Initializing collection {0}",
 						MessageHelper.collectionInfoString( ce.getLoadedPersister(), collection, ce.getLoadedKey(), source ) );
 				LOG.trace( "Checking second-level cache" );
 			}
 
 			final boolean foundInCache = initializeCollectionFromCache(
 					ce.getLoadedKey(),
 					ce.getLoadedPersister(),
 					collection,
 					source
 				);
 
 			if ( foundInCache ) {
 				if ( traceEnabled ) {
 					LOG.trace( "Collection initialized from cache" );
 				}
 			}
 			else {
 				if ( traceEnabled ) {
 					LOG.trace( "Collection not cached" );
 				}
 				ce.getLoadedPersister().initialize( ce.getLoadedKey(), source );
 				if ( traceEnabled ) {
 					LOG.trace( "Collection initialized" );
 				}
 
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
 
 		Object ce = null;
 		try {
-			source.getSessionEventsManager().cacheGetStart();
+			source.getEventListenerManager().cacheGetStart();
 			ce = persister.getCacheAccessStrategy().get(ck, source.getTimestamp());
 
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
 		}
 		finally {
-			source.getSessionEventsManager().cacheGetEnd( ce == null );
+			source.getEventListenerManager().cacheGetEnd( ce == null );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
index adea6d6036..aee858de9c 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultLoadEventListener.java
@@ -1,802 +1,802 @@
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
 import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
 import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.internal.Versioning;
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
         if (!persister.hasProxy()) {
 			return load(event, persister, keyToLoad, options);
 		}
 
         final PersistenceContext persistenceContext = event.getSession().getPersistenceContext();
 
 		// look for a proxy
         Object proxy = persistenceContext.getProxy(keyToLoad);
         if (proxy != null) {
 			return returnNarrowedProxy(event, persister, keyToLoad, options, persistenceContext, proxy);
 		}
 
         if (options.isAllowProxyCreation()) {
 			return createProxyIfNecessary(event, persister, keyToLoad, options, persistenceContext);
 		}
 
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
 		}
 		else {
 			if ( traceEnabled ) LOG.tracev( "Object not resolved in any cache: {0}",
 					MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() ) );
 			entity = loadFromDatasource(event, persister, keyToLoad, options);
 		}
 		
 		if (entity != null && persister.hasNaturalIdentifier()) {
 			event.getSession().getPersistenceContext().getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(
 					persister,
 					event.getEntityId(),
 					event.getSession().getPersistenceContext().getNaturalIdHelper().extractNaturalIdValues( entity, persister )
 			);
 		}
 
 
 		return entity;
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
 
 		if ( ! useCache ) {
 			// we can't use cache here
 			return null;
 		}
 
 		final SessionFactoryImplementor factory = source.getFactory();
 		final CacheKey ck = source.generateCacheKey(
 				event.getEntityId(),
 				persister.getIdentifierType(),
 				persister.getRootEntityName()
 		);
 
 		Object ce = null;
 		try {
-			source.getSessionEventsManager().cacheGetStart();
+			source.getEventListenerManager().cacheGetStart();
 			ce = persister.getCacheAccessStrategy().get( ck, source.getTimestamp() );
 
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
 		}
 		finally {
-			source.getSessionEventsManager().cacheGetEnd( ce == null );
+			source.getEventListenerManager().cacheGetEnd( ce == null );
 		}
 
 		if ( ce == null ) {
 			// nothing was found in cache
 			return null;
 		}
 
 		CacheEntry entry = (CacheEntry) persister.getCacheEntryStructure().destructure( ce, factory );
 		return convertCacheEntryToEntity( entry, event.getEntityId(), persister, event );
 	}
 
 	private Object convertCacheEntryToEntity(
 			CacheEntry entry,
 			Serializable entityId,
 			EntityPersister persister,
 			LoadEvent event) {
 
 		final EventSource session = event.getSession();
 		final SessionFactoryImplementor factory = session.getFactory();
 		final EntityPersister subclassPersister = factory.getEntityPersister( entry.getSubclass() );
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"Converting second-level cache entry [%s] into entity : %s",
 					entry,
 					MessageHelper.infoString( persister, entityId, factory )
 			);
 		}
 
 		final Object entity;
 		if ( entry.isReferenceEntry() ) {
 			final Object optionalObject = event.getInstanceToLoad();
 			if ( optionalObject != null ) {
 				throw new HibernateException(
 						String.format(
 								"Attempt to load entity [%s] from cache using provided object instance, but cache " +
 										"is storing references",
 								MessageHelper.infoString( persister, entityId, factory )
 						)
 				);
 			}
 
 			entity = ( (ReferenceCacheEntryImpl) entry ).getReference();
 			if ( entity == null ) {
 				throw new IllegalStateException(
 						"Reference cache entry contained null : " + MessageHelper.infoString( persister, entityId, factory )
 				);
 			}
 		}
 		else {
 			final Object optionalObject = event.getInstanceToLoad();
 			entity = optionalObject == null
 					? session.instantiate( subclassPersister, entityId )
 					: optionalObject;
 		}
 
 		// make it circular-reference safe
 		final EntityKey entityKey = session.generateEntityKey( entityId, subclassPersister );
 		TwoPhaseLoad.addUninitializedCachedEntity(
 				entityKey,
 				entity,
 				subclassPersister,
 				LockMode.NONE,
 				entry.areLazyPropertiesUnfetched(),
 				entry.getVersion(),
 				session
 		);
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final Object[] values;
 		final Object version;
 		final boolean isReadOnly;
 		if ( entry.isReferenceEntry() ) {
 			values = null;
 			version = null;
 			isReadOnly = true;
 		}
 		else {
 			final Type[] types = subclassPersister.getPropertyTypes();
 			// initializes the entity by (desired) side-effect
 			values = ( (StandardCacheEntryImpl) entry).assemble(
 					entity, entityId, subclassPersister, session.getInterceptor(), session
 			);
 			if ( ( (StandardCacheEntryImpl) entry ).isDeepCopyNeeded() ) {
 				TypeHelper.deepCopy(
 						values,
 						types,
 						subclassPersister.getPropertyUpdateability(),
 						values,
 						session
 				);
 			}
 			version = Versioning.getVersion( values, subclassPersister );
 			LOG.tracef( "Cached Version : %s", version );
 
 			final Object proxy = persistenceContext.getProxy( entityKey );
 			if ( proxy != null ) {
 				// there is already a proxy for this impl
 				// only set the status to read-only if the proxy is read-only
 				isReadOnly = ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().isReadOnly();
 			}
 			else {
 				isReadOnly = session.isDefaultReadOnly();
 			}
 		}
 
 		persistenceContext.addEntry(
 				entity,
 				( isReadOnly ? Status.READ_ONLY : Status.MANAGED ),
 				values,
 				null,
 				entityId,
 				version,
 				LockMode.NONE,
 				true,
 				subclassPersister,
 				false,
 				entry.areLazyPropertiesUnfetched()
 		);
 		subclassPersister.afterInitialize( entity, entry.areLazyPropertiesUnfetched(), session );
 		persistenceContext.initializeNonLazyCollections();
 
 		//PostLoad is needed for EJB3
 		PostLoadEvent postLoadEvent = new PostLoadEvent( session )
 				.setEntity( entity )
 				.setId( entityId )
 				.setPersister( persister );
 
 		for ( PostLoadEventListener listener : postLoadEventListeners( session ) ) {
 			listener.onPostLoad( postLoadEvent );
 		}
 
 		return entity;
 	}
 
 	private Object assembleCacheEntry(
 			final StandardCacheEntryImpl entry,
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
diff --git a/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
index 2b635baadd..1f6799003a 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/MultipleHiLoPerTableGenerator.java
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
 package org.hibernate.id;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
-import org.hibernate.engine.spi.SessionEventsManager;
+import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.enhanced.AccessCallback;
 import org.hibernate.id.enhanced.LegacyHiLoAlgorithmOptimizer;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.AbstractReturningWork;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.Type;
 
 /**
  *
  * A hilo <tt>IdentifierGenerator</tt> that returns a <tt>Long</tt>, constructed using
  * a hi/lo algorithm. The hi value MUST be fetched in a seperate transaction
  * to the <tt>Session</tt> transaction so the generator must be able to obtain
  * a new connection and commit it. Hence this implementation may not
  * be used  when the user is supplying connections. In this
  * case a <tt>SequenceHiLoGenerator</tt> would be a better choice (where
  * supported).<br>
  * <br>
  *
  * A hilo <tt>IdentifierGenerator</tt> that uses a database
  * table to store the last generated values. A table can contains
  * several hi values. They are distinct from each other through a key
  * <p/>
  * <p>This implementation is not compliant with a user connection</p>
  * <p/>
  *
  * <p>Allowed parameters (all of them are optional):</p>
  * <ul>
  * <li>table: table name (default <tt>hibernate_sequences</tt>)</li>
  * <li>primary_key_column: key column name (default <tt>sequence_name</tt>)</li>
  * <li>value_column: hi value column name(default <tt>sequence_next_hi_value</tt>)</li>
  * <li>primary_key_value: key value for the current entity (default to the entity's primary table name)</li>
  * <li>primary_key_length: length of the key column in DB represented as a varchar (default to 255)</li>
  * <li>max_lo: max low value before increasing hi (default to Short.MAX_VALUE)</li>
  * </ul>
  *
  * @author Emmanuel Bernard
  * @author <a href="mailto:kr@hbt.de">Klaus Richarz</a>.
  */
 public class MultipleHiLoPerTableGenerator implements PersistentIdentifierGenerator, Configurable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        MultipleHiLoPerTableGenerator.class.getName());
 
 	public static final String ID_TABLE = "table";
 	public static final String PK_COLUMN_NAME = "primary_key_column";
 	public static final String PK_VALUE_NAME = "primary_key_value";
 	public static final String VALUE_COLUMN_NAME = "value_column";
 	public static final String PK_LENGTH_NAME = "primary_key_length";
 
 	private static final int DEFAULT_PK_LENGTH = 255;
 	public static final String DEFAULT_TABLE = "hibernate_sequences";
 	private static final String DEFAULT_PK_COLUMN = "sequence_name";
 	private static final String DEFAULT_VALUE_COLUMN = "sequence_next_hi_value";
 
 	private String tableName;
 	private String pkColumnName;
 	private String valueColumnName;
 	private String query;
 	private String insert;
 	private String update;
 
 	//hilo params
 	public static final String MAX_LO = "max_lo";
 
 	private int maxLo;
 	private LegacyHiLoAlgorithmOptimizer hiloOptimizer;
 
 	private Class returnClass;
 	private int keySize;
 
 
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		return new String[] {
 			new StringBuilder( dialect.getCreateTableString() )
 					.append( ' ' )
 					.append( tableName )
 					.append( " ( " )
 					.append( pkColumnName )
 					.append( ' ' )
 					.append( dialect.getTypeName( Types.VARCHAR, keySize, 0, 0 ) )
 					.append( ",  " )
 					.append( valueColumnName )
 					.append( ' ' )
 					.append( dialect.getTypeName( Types.INTEGER ) )
 					.append( " ) " )
 					.toString()
 		};
 	}
 
 	public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
 		return new String[] { dialect.getDropTableString( tableName ) };
 	}
 
 	public Object generatorKey() {
 		return tableName;
 	}
 
 	public synchronized Serializable generate(final SessionImplementor session, Object obj) {
 		final SqlStatementLogger statementLogger = session.getFactory().getServiceRegistry()
 				.getService( JdbcServices.class )
 				.getSqlStatementLogger();
-		final SessionEventsManager statsCollector = session.getSessionEventsManager();
+		final SessionEventListenerManager statsCollector = session.getEventListenerManager();
 
 		final WorkExecutorVisitable<IntegralDataTypeHolder> work = new AbstractReturningWork<IntegralDataTypeHolder>() {
 			@Override
 			public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
 				IntegralDataTypeHolder value = IdentifierGeneratorHelper.getIntegralDataTypeHolder( returnClass );
 
 				int rows;
 				do {
 					final PreparedStatement queryPreparedStatement = prepareStatement( connection, query, statementLogger, statsCollector );
 					try {
 						final ResultSet rs = executeQuery( queryPreparedStatement, statsCollector );
 						boolean isInitialized = rs.next();
 						if ( !isInitialized ) {
 							value.initialize( 0 );
 							final PreparedStatement insertPreparedStatement = prepareStatement( connection, insert, statementLogger, statsCollector );
 							try {
 								value.bind( insertPreparedStatement, 1 );
 								executeUpdate( insertPreparedStatement, statsCollector );
 							}
 							finally {
 								insertPreparedStatement.close();
 							}
 						}
 						else {
 							value.initialize( rs, 0 );
 						}
 						rs.close();
 					}
 					catch (SQLException sqle) {
 						LOG.unableToReadOrInitHiValue( sqle );
 						throw sqle;
 					}
 					finally {
 						queryPreparedStatement.close();
 					}
 
 
 					final PreparedStatement updatePreparedStatement = prepareStatement( connection, update, statementLogger, statsCollector );
 					try {
 						value.copy().increment().bind( updatePreparedStatement, 1 );
 						value.bind( updatePreparedStatement, 2 );
 
 						rows = executeUpdate( updatePreparedStatement, statsCollector );
 					}
 					catch (SQLException sqle) {
 						LOG.error( LOG.unableToUpdateHiValue( tableName ), sqle );
 						throw sqle;
 					}
 					finally {
 						updatePreparedStatement.close();
 					}
 				} while ( rows==0 );
 
 				return value;
 			}
 		};
 
 		// maxLo < 1 indicates a hilo generator with no hilo :?
 		if ( maxLo < 1 ) {
 			//keep the behavior consistent even for boundary usages
 			IntegralDataTypeHolder value = null;
 			while ( value == null || value.lt( 1 ) ) {
 				value = session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork( work, true );
 			}
 			return value.makeValue();
 		}
 
 		return hiloOptimizer.generate(
 				new AccessCallback() {
 					public IntegralDataTypeHolder getNextValue() {
 						return session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork(
 								work,
 								true
 						);
 					}
 
 					@Override
 					public String getTenantIdentifier() {
 						return session.getTenantIdentifier();
 					}
 				}
 		);
 	}
 
 	private PreparedStatement prepareStatement(
 			Connection connection,
 			String sql,
 			SqlStatementLogger statementLogger,
-			SessionEventsManager statsCollector) throws SQLException {
+			SessionEventListenerManager statsCollector) throws SQLException {
 		statementLogger.logStatement( sql, FormatStyle.BASIC.getFormatter() );
 		try {
 			statsCollector.jdbcPrepareStatementStart();
 			return connection.prepareStatement( sql );
 		}
 		finally {
 			statsCollector.jdbcPrepareStatementEnd();
 		}
 	}
 
-	private int executeUpdate(PreparedStatement ps, SessionEventsManager statsCollector) throws SQLException {
+	private int executeUpdate(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
 		try {
 			statsCollector.jdbcExecuteStatementStart();
 			return ps.executeUpdate();
 		}
 		finally {
 			statsCollector.jdbcExecuteStatementEnd();
 		}
 
 	}
 
-	private ResultSet executeQuery(PreparedStatement ps, SessionEventsManager statsCollector) throws SQLException {
+	private ResultSet executeQuery(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
 		try {
 			statsCollector.jdbcExecuteStatementStart();
 			return ps.executeQuery();
 		}
 		finally {
 			statsCollector.jdbcExecuteStatementEnd();
 		}
 	}
 
 	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
 		ObjectNameNormalizer normalizer = ( ObjectNameNormalizer ) params.get( IDENTIFIER_NORMALIZER );
 
 		tableName = normalizer.normalizeIdentifierQuoting( ConfigurationHelper.getString( ID_TABLE, params, DEFAULT_TABLE ) );
 		if ( tableName.indexOf( '.' ) < 0 ) {
 			tableName = dialect.quote( tableName );
 			final String schemaName = dialect.quote(
 					normalizer.normalizeIdentifierQuoting( params.getProperty( SCHEMA ) )
 			);
 			final String catalogName = dialect.quote(
 					normalizer.normalizeIdentifierQuoting( params.getProperty( CATALOG ) )
 			);
 			tableName = Table.qualify( catalogName, schemaName, tableName );
 		}
 		else {
 			// if already qualified there is not much we can do in a portable manner so we pass it
 			// through and assume the user has set up the name correctly.
 		}
 
 		pkColumnName = dialect.quote(
 				normalizer.normalizeIdentifierQuoting(
 						ConfigurationHelper.getString( PK_COLUMN_NAME, params, DEFAULT_PK_COLUMN )
 				)
 		);
 		valueColumnName = dialect.quote(
 				normalizer.normalizeIdentifierQuoting(
 						ConfigurationHelper.getString( VALUE_COLUMN_NAME, params, DEFAULT_VALUE_COLUMN )
 				)
 		);
 		keySize = ConfigurationHelper.getInt(PK_LENGTH_NAME, params, DEFAULT_PK_LENGTH);
 		String keyValue = ConfigurationHelper.getString(PK_VALUE_NAME, params, params.getProperty(TABLE) );
 
 		query = "select " +
 			valueColumnName +
 			" from " +
 			dialect.appendLockHint( LockMode.PESSIMISTIC_WRITE, tableName ) +
 			" where " + pkColumnName + " = '" + keyValue + "'" +
 			dialect.getForUpdateString();
 
 		update = "update " +
 			tableName +
 			" set " +
 			valueColumnName +
 			" = ? where " +
 			valueColumnName +
 			" = ? and " +
 			pkColumnName +
 			" = '" +
 			keyValue
 			+ "'";
 
 		insert = "insert into " + tableName +
 			"(" + pkColumnName + ", " +	valueColumnName + ") " +
 			"values('"+ keyValue +"', ?)";
 
 
 		//hilo config
 		maxLo = ConfigurationHelper.getInt(MAX_LO, params, Short.MAX_VALUE);
 		returnClass = type.getReturnedClass();
 
 		if ( maxLo >= 1 ) {
 			hiloOptimizer = new LegacyHiLoAlgorithmOptimizer( returnClass, maxLo );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java
index 5a3960bf7d..b4abd52900 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/TableGenerator.java
@@ -1,258 +1,258 @@
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
 package org.hibernate.id;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
-import org.hibernate.engine.spi.SessionEventsManager;
+import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.AbstractReturningWork;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.Type;
 
 /**
  * An <tt>IdentifierGenerator</tt> that uses a database
  * table to store the last generated value. It is not
  * intended that applications use this strategy directly.
  * However, it may be used to build other (efficient)
  * strategies. The returned type is any supported by
  * {@link IntegralDataTypeHolder}
  * <p/>
  * The value MUST be fetched in a separate transaction
  * from that of the main {@link SessionImplementor session}
  * transaction so the generator must be able to obtain a new
  * connection and commit it. Hence this implementation may only
  * be used when Hibernate is fetching connections, not when the
  * user is supplying connections.
  * <p/>
  * Again, the return types supported here are any of the ones
  * supported by {@link IntegralDataTypeHolder}.  This is new
  * as of 3.5.  Prior to that this generator only returned {@link Integer}
  * values.
  * <p/>
  * Mapping parameters supported: table, column
  *
  * @see TableHiLoGenerator
  * @author Gavin King
  * 
  * @deprecated use {@link SequenceStyleGenerator} instead.
  */
 @Deprecated
 public class TableGenerator implements PersistentIdentifierGenerator, Configurable {
 	/* COLUMN and TABLE should be renamed but it would break the public API */
 	/** The column parameter */
 	public static final String COLUMN = "column";
 
 	/** Default column name */
 	public static final String DEFAULT_COLUMN_NAME = "next_hi";
 
 	/** The table parameter */
 	public static final String TABLE = "table";
 
 	/** Default table name */
 	public static final String DEFAULT_TABLE_NAME = "hibernate_unique_key";
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TableGenerator.class.getName());
 
 	private Type identifierType;
 	private String tableName;
 	private String columnName;
 	private String query;
 	private String update;
 
 	public void configure(Type type, Properties params, Dialect dialect) {
 		identifierType = type;
 
 		ObjectNameNormalizer normalizer = ( ObjectNameNormalizer ) params.get( IDENTIFIER_NORMALIZER );
 
 		tableName = ConfigurationHelper.getString( TABLE, params, DEFAULT_TABLE_NAME );
 		if ( tableName.indexOf( '.' ) < 0 ) {
 			final String schemaName = normalizer.normalizeIdentifierQuoting( params.getProperty( SCHEMA ) );
 			final String catalogName = normalizer.normalizeIdentifierQuoting( params.getProperty( CATALOG ) );
 			tableName = Table.qualify(
 					dialect.quote( catalogName ),
 					dialect.quote( schemaName ),
 					dialect.quote( tableName )
 			);
 		}
 		else {
 			// if already qualified there is not much we can do in a portable manner so we pass it
 			// through and assume the user has set up the name correctly.
 		}
 
 		columnName = dialect.quote(
 				normalizer.normalizeIdentifierQuoting(
 						ConfigurationHelper.getString( COLUMN, params, DEFAULT_COLUMN_NAME )
 				)
 		);
 
 		query = "select " +
 			columnName +
 			" from " +
 			dialect.appendLockHint(LockMode.PESSIMISTIC_WRITE, tableName) +
 			dialect.getForUpdateString();
 
 		update = "update " +
 			tableName +
 			" set " +
 			columnName +
 			" = ? where " +
 			columnName +
 			" = ?";
 	}
 
 	public synchronized Serializable generate(SessionImplementor session, Object object) {
 		return generateHolder( session ).makeValue();
 	}
 
 	protected IntegralDataTypeHolder generateHolder(final SessionImplementor session) {
 		final SqlStatementLogger statementLogger = session.getFactory().getServiceRegistry()
 				.getService( JdbcServices.class )
 				.getSqlStatementLogger();
-		final SessionEventsManager statsCollector = session.getSessionEventsManager();
+		final SessionEventListenerManager listeners = session.getEventListenerManager();
 
 		return session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork(
 				new AbstractReturningWork<IntegralDataTypeHolder>() {
 					@Override
 					public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
 						IntegralDataTypeHolder value = buildHolder();
 						int rows;
 
 						// The loop ensures atomicity of the select + update even for no transaction or
 						// read committed isolation level
 						do {
-							final PreparedStatement qps = prepareStatement( connection, query, statementLogger, statsCollector );
+							final PreparedStatement qps = prepareStatement( connection, query, statementLogger, listeners );
 							try {
-								ResultSet rs = executeQuery( qps, statsCollector );
+								ResultSet rs = executeQuery( qps, listeners );
 								if ( !rs.next() ) {
 									String err = "could not read a hi value - you need to populate the table: " + tableName;
 									LOG.error(err);
 									throw new IdentifierGenerationException(err);
 								}
 								value.initialize( rs, 1 );
 								rs.close();
 							}
 							catch (SQLException e) {
 								LOG.error("Could not read a hi value", e);
 								throw e;
 							}
 							finally {
 								qps.close();
 							}
 
-							final PreparedStatement ups = prepareStatement( connection, update, statementLogger, statsCollector );
+							final PreparedStatement ups = prepareStatement( connection, update, statementLogger, listeners );
 							try {
 								value.copy().increment().bind( ups, 1 );
 								value.bind( ups, 2 );
-								rows = executeUpdate( ups, statsCollector );
+								rows = executeUpdate( ups, listeners );
 							}
 							catch (SQLException sqle) {
 								LOG.error(LOG.unableToUpdateHiValue(tableName), sqle);
 								throw sqle;
 							}
 							finally {
 								ups.close();
 							}
 						}
 						while (rows==0);
 						return value;
 					}
 				},
 				true
 		);
 	}
 
 	private PreparedStatement prepareStatement(
 			Connection connection,
 			String sql,
 			SqlStatementLogger statementLogger,
-			SessionEventsManager statsCollector) throws SQLException {
+			SessionEventListenerManager statsCollector) throws SQLException {
 		statementLogger.logStatement( sql, FormatStyle.BASIC.getFormatter() );
 		try {
 			statsCollector.jdbcPrepareStatementStart();
 			return connection.prepareStatement( sql );
 		}
 		finally {
 			statsCollector.jdbcPrepareStatementEnd();
 		}
 	}
 
-	private int executeUpdate(PreparedStatement ps, SessionEventsManager statsCollector) throws SQLException {
+	private int executeUpdate(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
 		try {
 			statsCollector.jdbcExecuteStatementStart();
 			return ps.executeUpdate();
 		}
 		finally {
 			statsCollector.jdbcExecuteStatementEnd();
 		}
 
 	}
 
-	private ResultSet executeQuery(PreparedStatement ps, SessionEventsManager statsCollector) throws SQLException {
+	private ResultSet executeQuery(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
 		try {
 			statsCollector.jdbcExecuteStatementStart();
 			return ps.executeQuery();
 		}
 		finally {
 			statsCollector.jdbcExecuteStatementEnd();
 		}
 	}
 
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		return new String[] {
 			dialect.getCreateTableString() + " " + tableName + " ( " + columnName + " " + dialect.getTypeName(Types.INTEGER) + " )",
 			"insert into " + tableName + " values ( 0 )"
 		};
 	}
 
 	public String[] sqlDropStrings(Dialect dialect) {
 		return new String[] { dialect.getDropTableString( tableName ) };
 	}
 
 	public Object generatorKey() {
 		return tableName;
 	}
 
 	protected IntegralDataTypeHolder buildHolder() {
 		return IdentifierGeneratorHelper.getIntegralDataTypeHolder( identifierType.getReturnedClass() );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
index 20846de589..8f5b0b7fbe 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableGenerator.java
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
 package org.hibernate.id.enhanced;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Collections;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
-import org.hibernate.engine.spi.SessionEventsManager;
+import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.Configurable;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.id.IntegralDataTypeHolder;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.jdbc.AbstractReturningWork;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.Type;
 
 /**
  * An enhanced version of table-based id generation.
  * <p/>
  * Unlike the simplistic legacy one (which, btw, was only ever intended for subclassing
  * support) we "segment" the table into multiple values.  Thus a single table can
  * actually serve as the persistent storage for multiple independent generators.  One
  * approach would be to segment the values by the name of the entity for which we are
  * performing generation, which would mean that we would have a row in the generator
  * table for each entity name.  Or any configuration really; the setup is very flexible.
  * <p/>
  * In this respect it is very similar to the legacy
  * {@link org.hibernate.id.MultipleHiLoPerTableGenerator} in terms of the
  * underlying storage structure (namely a single table capable of holding
  * multiple generator values).  The differentiator is, as with
  * {@link SequenceStyleGenerator} as well, the externalized notion
  * of an optimizer.
  * <p/>
  * <b>NOTE</b> that by default we use a single row for all generators (based
  * on {@link #DEF_SEGMENT_VALUE}).  The configuration parameter
  * {@link #CONFIG_PREFER_SEGMENT_PER_ENTITY} can be used to change that to
  * instead default to using a row for each entity name.
  * <p/>
  * Configuration parameters:
  * <table>
  * 	 <tr>
  *     <td><b>NAME</b></td>
  *     <td><b>DEFAULT</b></td>
  *     <td><b>DESCRIPTION</b></td>
  *   </tr>
  *   <tr>
  *     <td>{@link #TABLE_PARAM}</td>
  *     <td>{@link #DEF_TABLE}</td>
  *     <td>The name of the table to use to store/retrieve values</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #VALUE_COLUMN_PARAM}</td>
  *     <td>{@link #DEF_VALUE_COLUMN}</td>
  *     <td>The name of column which holds the sequence value for the given segment</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #SEGMENT_COLUMN_PARAM}</td>
  *     <td>{@link #DEF_SEGMENT_COLUMN}</td>
  *     <td>The name of the column which holds the segment key</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #SEGMENT_VALUE_PARAM}</td>
  *     <td>{@link #DEF_SEGMENT_VALUE}</td>
  *     <td>The value indicating which segment is used by this generator; refers to values in the {@link #SEGMENT_COLUMN_PARAM} column</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #SEGMENT_LENGTH_PARAM}</td>
  *     <td>{@link #DEF_SEGMENT_LENGTH}</td>
  *     <td>The data length of the {@link #SEGMENT_COLUMN_PARAM} column; used for schema creation</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #INITIAL_PARAM}</td>
  *     <td>{@link #DEFAULT_INITIAL_VALUE}</td>
  *     <td>The initial value to be stored for the given segment</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #INCREMENT_PARAM}</td>
  *     <td>{@link #DEFAULT_INCREMENT_SIZE}</td>
  *     <td>The increment size for the underlying segment; see the discussion on {@link Optimizer} for more details.</td>
  *   </tr>
  *   <tr>
  *     <td>{@link #OPT_PARAM}</td>
  *     <td><i>depends on defined increment size</i></td>
  *     <td>Allows explicit definition of which optimization strategy to use</td>
  *   </tr>
  * </table>
  *
  * @author Steve Ebersole
  */
 public class TableGenerator implements PersistentIdentifierGenerator, Configurable {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			TableGenerator.class.getName()
 	);
 
 	/**
 	 * By default (in the absence of a {@link #SEGMENT_VALUE_PARAM} setting) we use a single row for all
 	 * generators.  This setting can be used to change that to instead default to using a row for each entity name.
 	 */
 	public static final String CONFIG_PREFER_SEGMENT_PER_ENTITY = "prefer_entity_table_as_segment_value";
 
 	/**
 	 * Configures the name of the table to use.  The default value is {@link #DEF_TABLE}
 	 */
 	public static final String TABLE_PARAM = "table_name";
 
 	/**
 	 * The default {@link #TABLE_PARAM} value
 	 */
 	public static final String DEF_TABLE = "hibernate_sequences";
 
 	/**
 	 * The name of column which holds the sequence value.  The default value is {@link #DEF_VALUE_COLUMN}
 	 */
 	public static final String VALUE_COLUMN_PARAM = "value_column_name";
 
 	/**
 	 * The default {@link #VALUE_COLUMN_PARAM} value
 	 */
 	public static final String DEF_VALUE_COLUMN = "next_val";
 
 	/**
 	 * The name of the column which holds the segment key.  The segment defines the different buckets (segments)
 	 * of values currently tracked in the table.  The default value is {@link #DEF_SEGMENT_COLUMN}
 	 */
 	public static final String SEGMENT_COLUMN_PARAM = "segment_column_name";
 
 	/**
 	 * The default {@link #SEGMENT_COLUMN_PARAM} value
 	 */
 	public static final String DEF_SEGMENT_COLUMN = "sequence_name";
 
 	/**
 	 * The value indicating which segment is used by this generator, as indicated by the actual value stored in the
 	 * column indicated by {@link #SEGMENT_COLUMN_PARAM}.  The default value for setting is {@link #DEF_SEGMENT_VALUE},
 	 * although {@link #CONFIG_PREFER_SEGMENT_PER_ENTITY} effects the default as well.
 	 */
 	public static final String SEGMENT_VALUE_PARAM = "segment_value";
 
 	/**
 	 * The default {@link #SEGMENT_VALUE_PARAM} value, unless {@link #CONFIG_PREFER_SEGMENT_PER_ENTITY} is specified
 	 */
 	public static final String DEF_SEGMENT_VALUE = "default";
 
 	/**
 	 * Indicates the length of the column defined by {@link #SEGMENT_COLUMN_PARAM}.  Used in schema export.  The
 	 * default value is {@link #DEF_SEGMENT_LENGTH}
 	 */
 	public static final String SEGMENT_LENGTH_PARAM = "segment_value_length";
 
 	/**
 	 * The default {@link #SEGMENT_LENGTH_PARAM} value
 	 */
 	public static final int DEF_SEGMENT_LENGTH = 255;
 
 	/**
 	 * Indicates the initial value to use.  The default value is {@link #DEFAULT_INITIAL_VALUE}
 	 */
 	public static final String INITIAL_PARAM = "initial_value";
 
 	/**
 	 * The default {@link #INITIAL_PARAM} value
 	 */
 	public static final int DEFAULT_INITIAL_VALUE = 1;
 
 	/**
 	 * Indicates the increment size to use.  The default value is {@link #DEFAULT_INCREMENT_SIZE}
 	 */
 	public static final String INCREMENT_PARAM = "increment_size";
 
 	/**
 	 * The default {@link #INCREMENT_PARAM} value
 	 */
 	public static final int DEFAULT_INCREMENT_SIZE = 1;
 
 	/**
 	 * Indicates the optimizer to use, either naming a {@link Optimizer} implementation class or by naming
 	 * a {@link StandardOptimizerDescriptor} by name
 	 */
 	public static final String OPT_PARAM = "optimizer";
 
 
 	private Type identifierType;
 
 	private String tableName;
 
 	private String segmentColumnName;
 	private String segmentValue;
 	private int segmentValueLength;
 
 	private String valueColumnName;
 	private int initialValue;
 	private int incrementSize;
 
 	private String selectQuery;
 	private String insertQuery;
 	private String updateQuery;
 
 	private Optimizer optimizer;
 	private long accessCount;
 
 	@Override
 	public Object generatorKey() {
 		return tableName;
 	}
 
 	/**
 	 * Type mapping for the identifier.
 	 *
 	 * @return The identifier type mapping.
 	 */
 	public final Type getIdentifierType() {
 		return identifierType;
 	}
 
 	/**
 	 * The name of the table in which we store this generator's persistent state.
 	 *
 	 * @return The table name.
 	 */
 	public final String getTableName() {
 		return tableName;
 	}
 
 	/**
 	 * The name of the column in which we store the segment to which each row
 	 * belongs.  The value here acts as PK.
 	 *
 	 * @return The segment column name
 	 */
 	public final String getSegmentColumnName() {
 		return segmentColumnName;
 	}
 
 	/**
 	 * The value in {@link #getSegmentColumnName segment column} which
 	 * corresponding to this generator instance.  In other words this value
 	 * indicates the row in which this generator instance will store values.
 	 *
 	 * @return The segment value for this generator instance.
 	 */
 	public final String getSegmentValue() {
 		return segmentValue;
 	}
 
 	/**
 	 * The size of the {@link #getSegmentColumnName segment column} in the
 	 * underlying table.
 	 * <p/>
 	 * <b>NOTE</b> : should really have been called 'segmentColumnLength' or
 	 * even better 'segmentColumnSize'
 	 *
 	 * @return the column size.
 	 */
 	public final int getSegmentValueLength() {
 		return segmentValueLength;
 	}
 
 	/**
 	 * The name of the column in which we store our persistent generator value.
 	 *
 	 * @return The name of the value column.
 	 */
 	public final String getValueColumnName() {
 		return valueColumnName;
 	}
 
 	/**
 	 * The initial value to use when we find no previous state in the
 	 * generator table corresponding to our sequence.
 	 *
 	 * @return The initial value to use.
 	 */
 	public final int getInitialValue() {
 		return initialValue;
 	}
 
 	/**
 	 * The amount of increment to use.  The exact implications of this
 	 * depends on the {@link #getOptimizer() optimizer} being used.
 	 *
 	 * @return The increment amount.
 	 */
 	public final int getIncrementSize() {
 		return incrementSize;
 	}
 
 	/**
 	 * The optimizer being used by this generator.
 	 *
 	 * @return Out optimizer.
 	 */
 	public final Optimizer getOptimizer() {
 		return optimizer;
 	}
 
 	/**
 	 * Getter for property 'tableAccessCount'.  Only really useful for unit test
 	 * assertions.
 	 *
 	 * @return Value for property 'tableAccessCount'.
 	 */
 	public final long getTableAccessCount() {
 		return accessCount;
 	}
 
 	@Override
 	public void configure(Type type, Properties params, Dialect dialect) throws MappingException {
 		identifierType = type;
 
 		tableName = determineGeneratorTableName( params, dialect );
 		segmentColumnName = determineSegmentColumnName( params, dialect );
 		valueColumnName = determineValueColumnName( params, dialect );
 
 		segmentValue = determineSegmentValue( params );
 
 		segmentValueLength = determineSegmentColumnSize( params );
 		initialValue = determineInitialValue( params );
 		incrementSize = determineIncrementSize( params );
 
 		this.selectQuery = buildSelectQuery( dialect );
 		this.updateQuery = buildUpdateQuery();
 		this.insertQuery = buildInsertQuery();
 
 		// if the increment size is greater than one, we prefer pooled optimization; but we
 		// need to see if the user prefers POOL or POOL_LO...
 		final String defaultPooledOptimizerStrategy = ConfigurationHelper.getBoolean( Environment.PREFER_POOLED_VALUES_LO, params, false )
 				? StandardOptimizerDescriptor.POOLED_LO.getExternalName()
 				: StandardOptimizerDescriptor.POOLED.getExternalName();
 		final String defaultOptimizerStrategy = incrementSize <= 1
 				? StandardOptimizerDescriptor.NONE.getExternalName()
 				: defaultPooledOptimizerStrategy;
 		final String optimizationStrategy = ConfigurationHelper.getString( OPT_PARAM, params, defaultOptimizerStrategy );
 		optimizer = OptimizerFactory.buildOptimizer(
 				optimizationStrategy,
 				identifierType.getReturnedClass(),
 				incrementSize,
 				ConfigurationHelper.getInt( INITIAL_PARAM, params, -1 )
 		);
 	}
 
 	/**
 	 * Determine the table name to use for the generator values.
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @see #getTableName()
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @param dialect The dialect in effect
 	 * @return The table name to use.
 	 */
 	protected String determineGeneratorTableName(Properties params, Dialect dialect) {
 		String name = ConfigurationHelper.getString( TABLE_PARAM, params, DEF_TABLE );
 		final boolean isGivenNameUnqualified = name.indexOf( '.' ) < 0;
 		if ( isGivenNameUnqualified ) {
 			final ObjectNameNormalizer normalizer = (ObjectNameNormalizer) params.get( IDENTIFIER_NORMALIZER );
 			name = normalizer.normalizeIdentifierQuoting( name );
 			// if the given name is un-qualified we may neen to qualify it
 			final String schemaName = normalizer.normalizeIdentifierQuoting( params.getProperty( SCHEMA ) );
 			final String catalogName = normalizer.normalizeIdentifierQuoting( params.getProperty( CATALOG ) );
 			name = Table.qualify(
 					dialect.quote( catalogName ),
 					dialect.quote( schemaName ),
 					dialect.quote( name)
 			);
 		}
 		// if already qualified there is not much we can do in a portable manner so we pass it
 		// through and assume the user has set up the name correctly.
 
 		return name;
 	}
 
 	/**
 	 * Determine the name of the column used to indicate the segment for each
 	 * row.  This column acts as the primary key.
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @see #getSegmentColumnName()
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @param dialect The dialect in effect
 	 * @return The name of the segment column
 	 */
 	protected String determineSegmentColumnName(Properties params, Dialect dialect) {
 		final ObjectNameNormalizer normalizer = (ObjectNameNormalizer) params.get( IDENTIFIER_NORMALIZER );
 		final String name = ConfigurationHelper.getString( SEGMENT_COLUMN_PARAM, params, DEF_SEGMENT_COLUMN );
 		return dialect.quote( normalizer.normalizeIdentifierQuoting( name ) );
 	}
 
 	/**
 	 * Determine the name of the column in which we will store the generator persistent value.
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @see #getValueColumnName()
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @param dialect The dialect in effect
 	 * @return The name of the value column
 	 */
 	protected String determineValueColumnName(Properties params, Dialect dialect) {
 		final ObjectNameNormalizer normalizer = (ObjectNameNormalizer) params.get( IDENTIFIER_NORMALIZER );
 		final String name = ConfigurationHelper.getString( VALUE_COLUMN_PARAM, params, DEF_VALUE_COLUMN );
 		return dialect.quote( normalizer.normalizeIdentifierQuoting( name ) );
 	}
 
 	/**
 	 * Determine the segment value corresponding to this generator instance.
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @see #getSegmentValue()
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @return The name of the value column
 	 */
 	protected String determineSegmentValue(Properties params) {
 		String segmentValue = params.getProperty( SEGMENT_VALUE_PARAM );
 		if ( StringHelper.isEmpty( segmentValue ) ) {
 			segmentValue = determineDefaultSegmentValue( params );
 		}
 		return segmentValue;
 	}
 
 	/**
 	 * Used in the cases where {@link #determineSegmentValue} is unable to
 	 * determine the value to use.
 	 *
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @return The default segment value to use.
 	 */
 	protected String determineDefaultSegmentValue(Properties params) {
 		final boolean preferSegmentPerEntity = ConfigurationHelper.getBoolean( CONFIG_PREFER_SEGMENT_PER_ENTITY, params, false );
 		final String defaultToUse = preferSegmentPerEntity ? params.getProperty( TABLE ) : DEF_SEGMENT_VALUE;
 		LOG.usingDefaultIdGeneratorSegmentValue( tableName, segmentColumnName, defaultToUse );
 		return defaultToUse;
 	}
 
 	/**
 	 * Determine the size of the {@link #getSegmentColumnName segment column}
 	 * <p/>
 	 * Called during {@link #configure configuration}.
 	 *
 	 * @see #getSegmentValueLength()
 	 * @param params The params supplied in the generator config (plus some standard useful extras).
 	 * @return The size of the segment column
 	 */
 	protected int determineSegmentColumnSize(Properties params) {
 		return ConfigurationHelper.getInt( SEGMENT_LENGTH_PARAM, params, DEF_SEGMENT_LENGTH );
 	}
 
 	protected int determineInitialValue(Properties params) {
 		return ConfigurationHelper.getInt( INITIAL_PARAM, params, DEFAULT_INITIAL_VALUE );
 	}
 
 	protected int determineIncrementSize(Properties params) {
 		return ConfigurationHelper.getInt( INCREMENT_PARAM, params, DEFAULT_INCREMENT_SIZE );
 	}
 
 	protected String buildSelectQuery(Dialect dialect) {
 		final String alias = "tbl";
 		final String query = "select " + StringHelper.qualify( alias, valueColumnName ) +
 				" from " + tableName + ' ' + alias +
 				" where " + StringHelper.qualify( alias, segmentColumnName ) + "=?";
 		final LockOptions lockOptions = new LockOptions( LockMode.PESSIMISTIC_WRITE );
 		lockOptions.setAliasSpecificLockMode( alias, LockMode.PESSIMISTIC_WRITE );
 		final Map updateTargetColumnsMap = Collections.singletonMap( alias, new String[] { valueColumnName } );
 		return dialect.applyLocksToSql( query, lockOptions, updateTargetColumnsMap );
 	}
 
 	protected String buildUpdateQuery() {
 		return "update " + tableName +
 				" set " + valueColumnName + "=? " +
 				" where " + valueColumnName + "=? and " + segmentColumnName + "=?";
 	}
 
 	protected String buildInsertQuery() {
 		return "insert into " + tableName + " (" + segmentColumnName + ", " + valueColumnName + ") " + " values (?,?)";
 	}
 
 	private IntegralDataTypeHolder makeValue() {
 		return IdentifierGeneratorHelper.getIntegralDataTypeHolder( identifierType.getReturnedClass() );
 	}
 
 	@Override
 	public synchronized Serializable generate(final SessionImplementor session, Object obj) {
 		final SqlStatementLogger statementLogger = session.getFactory().getServiceRegistry()
 				.getService( JdbcServices.class )
 				.getSqlStatementLogger();
-		final SessionEventsManager statsCollector = session.getSessionEventsManager();
+		final SessionEventListenerManager statsCollector = session.getEventListenerManager();
 
 		return optimizer.generate(
 				new AccessCallback() {
 					@Override
 					public IntegralDataTypeHolder getNextValue() {
 						return session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork(
 								new AbstractReturningWork<IntegralDataTypeHolder>() {
 									@Override
 									public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
 										final IntegralDataTypeHolder value = makeValue();
 										int rows;
 										do {
 											final PreparedStatement selectPS = prepareStatement( connection, selectQuery, statementLogger, statsCollector );
 
 											try {
 												selectPS.setString( 1, segmentValue );
 												final ResultSet selectRS = executeQuery( selectPS, statsCollector );
 												if ( !selectRS.next() ) {
 													value.initialize( initialValue );
 
 													final PreparedStatement insertPS = prepareStatement( connection, insertQuery, statementLogger, statsCollector );
 													try {
 														insertPS.setString( 1, segmentValue );
 														value.bind( insertPS, 2 );
 														executeUpdate( insertPS, statsCollector );
 													}
 													finally {
 														insertPS.close();
 													}
 												}
 												else {
 													value.initialize( selectRS, 1 );
 												}
 												selectRS.close();
 											}
 											catch (SQLException e) {
 												LOG.unableToReadOrInitHiValue( e );
 												throw e;
 											}
 											finally {
 												selectPS.close();
 											}
 
 
 											final PreparedStatement updatePS = prepareStatement( connection, updateQuery, statementLogger, statsCollector );
 											try {
 												final IntegralDataTypeHolder updateValue = value.copy();
 												if ( optimizer.applyIncrementSizeToSourceValues() ) {
 													updateValue.add( incrementSize );
 												}
 												else {
 													updateValue.increment();
 												}
 												updateValue.bind( updatePS, 1 );
 												value.bind( updatePS, 2 );
 												updatePS.setString( 3, segmentValue );
 												rows = executeUpdate( updatePS, statsCollector );
 											}
 											catch (SQLException e) {
 												LOG.unableToUpdateQueryHiValue( tableName, e );
 												throw e;
 											}
 											finally {
 												updatePS.close();
 											}
 										}
 										while ( rows == 0 );
 
 										accessCount++;
 
 										return value;
 									}
 								},
 								true
 						);
 					}
 
 					@Override
 					public String getTenantIdentifier() {
 						return session.getTenantIdentifier();
 					}
 				}
 		);
 	}
 
 	private PreparedStatement prepareStatement(
 			Connection connection,
 			String sql,
 			SqlStatementLogger statementLogger,
-			SessionEventsManager statsCollector) throws SQLException {
+			SessionEventListenerManager statsCollector) throws SQLException {
 		statementLogger.logStatement( sql, FormatStyle.BASIC.getFormatter() );
 		try {
 			statsCollector.jdbcPrepareStatementStart();
 			return connection.prepareStatement( sql );
 		}
 		finally {
 			statsCollector.jdbcPrepareStatementEnd();
 		}
 	}
 
-	private int executeUpdate(PreparedStatement ps, SessionEventsManager statsCollector) throws SQLException {
+	private int executeUpdate(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
 		try {
 			statsCollector.jdbcExecuteStatementStart();
 			return ps.executeUpdate();
 		}
 		finally {
 			statsCollector.jdbcExecuteStatementEnd();
 		}
 
 	}
 
-	private ResultSet executeQuery(PreparedStatement ps, SessionEventsManager statsCollector) throws SQLException {
+	private ResultSet executeQuery(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
 		try {
 			statsCollector.jdbcExecuteStatementStart();
 			return ps.executeQuery();
 		}
 		finally {
 			statsCollector.jdbcExecuteStatementEnd();
 		}
 	}
 
 	@Override
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		return new String[] {
 				dialect.getCreateTableString() + ' ' + tableName + " ( "
 						+ segmentColumnName + ' ' + dialect.getTypeName( Types.VARCHAR, segmentValueLength, 0, 0 ) + " not null "
 						+ ", " + valueColumnName + ' ' + dialect.getTypeName( Types.BIGINT )
 						+ ", primary key ( " + segmentColumnName + " ) ) "
 		};
 	}
 
 	@Override
 	public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
 		return new String[] { dialect.getDropTableString( tableName ) };
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
index 51a403992e..acab8b8709 100644
--- a/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
+++ b/hibernate-core/src/main/java/org/hibernate/id/enhanced/TableStructure.java
@@ -1,244 +1,244 @@
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
 package org.hibernate.id.enhanced;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.internal.FormatStyle;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
-import org.hibernate.engine.spi.SessionEventsManager;
+import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.id.IdentifierGenerationException;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.id.IntegralDataTypeHolder;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.jdbc.AbstractReturningWork;
 
 /**
  * Describes a table used to mimic sequence behavior
  *
  * @author Steve Ebersole
  */
 public class TableStructure implements DatabaseStructure {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			TableStructure.class.getName()
 	);
 
 	private final String tableName;
 	private final String valueColumnName;
 	private final int initialValue;
 	private final int incrementSize;
 	private final Class numberType;
 	private final String selectQuery;
 	private final String updateQuery;
 
 	private boolean applyIncrementSizeToSourceValues;
 	private int accessCounter;
 
 	public TableStructure(
 			Dialect dialect,
 			String tableName,
 			String valueColumnName,
 			int initialValue,
 			int incrementSize,
 			Class numberType) {
 		this.tableName = tableName;
 		this.initialValue = initialValue;
 		this.incrementSize = incrementSize;
 		this.valueColumnName = valueColumnName;
 		this.numberType = numberType;
 
 		selectQuery = "select " + valueColumnName + " as id_val" +
 				" from " + dialect.appendLockHint( LockMode.PESSIMISTIC_WRITE, tableName ) +
 				dialect.getForUpdateString();
 
 		updateQuery = "update " + tableName +
 				" set " + valueColumnName + "= ?" +
 				" where " + valueColumnName + "=?";
 	}
 
 	@Override
 	public String getName() {
 		return tableName;
 	}
 
 	@Override
 	public int getInitialValue() {
 		return initialValue;
 	}
 
 	@Override
 	public int getIncrementSize() {
 		return incrementSize;
 	}
 
 	@Override
 	public int getTimesAccessed() {
 		return accessCounter;
 	}
 
 	@Override
 	public void prepare(Optimizer optimizer) {
 		applyIncrementSizeToSourceValues = optimizer.applyIncrementSizeToSourceValues();
 	}
 
 	private IntegralDataTypeHolder makeValue() {
 		return IdentifierGeneratorHelper.getIntegralDataTypeHolder( numberType );
 	}
 
 	@Override
 	public AccessCallback buildCallback(final SessionImplementor session) {
 		final SqlStatementLogger statementLogger = session.getFactory().getServiceRegistry()
 				.getService( JdbcServices.class )
 				.getSqlStatementLogger();
-		final SessionEventsManager statsCollector = session.getSessionEventsManager();
+		final SessionEventListenerManager statsCollector = session.getEventListenerManager();
 
 		return new AccessCallback() {
 			@Override
 			public IntegralDataTypeHolder getNextValue() {
 				return session.getTransactionCoordinator().getTransaction().createIsolationDelegate().delegateWork(
 						new AbstractReturningWork<IntegralDataTypeHolder>() {
 							@Override
 							public IntegralDataTypeHolder execute(Connection connection) throws SQLException {
 								final IntegralDataTypeHolder value = makeValue();
 								int rows;
 								do {
 									final PreparedStatement selectStatement = prepareStatement( connection, selectQuery, statementLogger, statsCollector );
 									try {
 										final ResultSet selectRS = executeQuery( selectStatement, statsCollector );
 										if ( !selectRS.next() ) {
 											final String err = "could not read a hi value - you need to populate the table: " + tableName;
 											LOG.error( err );
 											throw new IdentifierGenerationException( err );
 										}
 										value.initialize( selectRS, 1 );
 										selectRS.close();
 									}
 									catch (SQLException sqle) {
 										LOG.error( "could not read a hi value", sqle );
 										throw sqle;
 									}
 									finally {
 										selectStatement.close();
 									}
 
 
 									final PreparedStatement updatePS = prepareStatement( connection, updateQuery, statementLogger, statsCollector );
 									try {
 										final int increment = applyIncrementSizeToSourceValues ? incrementSize : 1;
 										final IntegralDataTypeHolder updateValue = value.copy().add( increment );
 										updateValue.bind( updatePS, 1 );
 										value.bind( updatePS, 2 );
 										rows = executeUpdate( updatePS, statsCollector );
 									}
 									catch (SQLException e) {
 										LOG.unableToUpdateQueryHiValue( tableName, e );
 										throw e;
 									}
 									finally {
 										updatePS.close();
 									}
 								} while ( rows == 0 );
 
 								accessCounter++;
 
 								return value;
 							}
 						},
 						true
 				);
 			}
 
 			@Override
 			public String getTenantIdentifier() {
 				return session.getTenantIdentifier();
 			}
 		};
 	}
 
 	private PreparedStatement prepareStatement(
 			Connection connection,
 			String sql,
 			SqlStatementLogger statementLogger,
-			SessionEventsManager statsCollector) throws SQLException {
+			SessionEventListenerManager statsCollector) throws SQLException {
 		statementLogger.logStatement( sql, FormatStyle.BASIC.getFormatter() );
 		try {
 			statsCollector.jdbcPrepareStatementStart();
 			return connection.prepareStatement( sql );
 		}
 		finally {
 			statsCollector.jdbcPrepareStatementEnd();
 		}
 	}
 
-	private int executeUpdate(PreparedStatement ps, SessionEventsManager statsCollector) throws SQLException {
+	private int executeUpdate(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
 		try {
 			statsCollector.jdbcExecuteStatementStart();
 			return ps.executeUpdate();
 		}
 		finally {
 			statsCollector.jdbcExecuteStatementEnd();
 		}
 
 	}
 
-	private ResultSet executeQuery(PreparedStatement ps, SessionEventsManager statsCollector) throws SQLException {
+	private ResultSet executeQuery(PreparedStatement ps, SessionEventListenerManager statsCollector) throws SQLException {
 		try {
 			statsCollector.jdbcExecuteStatementStart();
 			return ps.executeQuery();
 		}
 		finally {
 			statsCollector.jdbcExecuteStatementEnd();
 		}
 	}
 
 	@Override
 	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
 		return new String[] {
 				dialect.getCreateTableString() + " " + tableName + " ( " + valueColumnName + " " + dialect.getTypeName( Types.BIGINT ) + " )",
 				"insert into " + tableName + " values ( " + initialValue + " )"
 		};
 	}
 
 	@Override
 	public String[] sqlDropStrings(Dialect dialect) throws HibernateException {
 		return new String[] { dialect.getDropTableString( tableName ) };
 	}
 
 	@Override
 	public boolean isPhysicalSequence() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
index 263f0772fa..807744a73a 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
@@ -1,451 +1,450 @@
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
 import java.sql.SQLException;
 import java.util.List;
 import java.util.UUID;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Query;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollableResults;
-import org.hibernate.SessionEventsListener;
+import org.hibernate.SessionEventListener;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionContract;
-import org.hibernate.engine.spi.SessionEventsManager;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.ParameterMetadata;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.id.uuid.StandardRandomStrategy;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.procedure.internal.ProcedureCallImpl;
 import org.hibernate.type.Type;
 
 /**
  * Functionality common to stateless and stateful sessions
  *
  * @author Gavin King
  */
 public abstract class AbstractSessionImpl
 		implements Serializable, SharedSessionContract, SessionImplementor, TransactionContext {
 	protected transient SessionFactoryImpl factory;
 	private final String tenantIdentifier;
 	private boolean closed;
 
 	protected AbstractSessionImpl(SessionFactoryImpl factory, String tenantIdentifier) {
 		this.factory = factory;
 		this.tenantIdentifier = tenantIdentifier;
 		if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 			if ( tenantIdentifier != null ) {
 				throw new HibernateException( "SessionFactory was not configured for multi-tenancy" );
 			}
 		}
 		else {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "SessionFactory configured for multi-tenancy, but no tenant identifier specified" );
 			}
 		}
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
 
 	@Override
 	public boolean isClosed() {
 		return closed || factory.isClosed();
 	}
 
 	protected void setClosed() {
 		closed = true;
 	}
 
 	protected void errorIfClosed() {
 		if ( isClosed() ) {
 			throw new SessionException( "Session is closed!" );
 		}
 	}
 
 	@Override
 	public Query createQuery(NamedQueryDefinition namedQueryDefinition) {
 		String queryString = namedQueryDefinition.getQueryString();
 		final Query query = new QueryImpl(
 				queryString,
 				namedQueryDefinition.getFlushMode(),
 				this,
 				getHQLQueryPlan( queryString, false ).getParameterMetadata()
 		);
 		query.setComment( "named HQL query " + namedQueryDefinition.getName() );
 		if ( namedQueryDefinition.getLockOptions() != null ) {
 			query.setLockOptions( namedQueryDefinition.getLockOptions() );
 		}
 
 		return query;
 	}
 
 	@Override
 	public SQLQuery createSQLQuery(NamedSQLQueryDefinition namedQueryDefinition) {
 		final ParameterMetadata parameterMetadata = factory.getQueryPlanCache().getSQLParameterMetadata( namedQueryDefinition.getQueryString() );
 		final SQLQuery query = new SQLQueryImpl(
 				namedQueryDefinition,
 				this,
 				parameterMetadata
 		);
 		query.setComment( "named native SQL query " + namedQueryDefinition.getName() );
 		return query;
 	}
 
 	@Override
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		NamedQueryDefinition nqd = factory.getNamedQuery( queryName );
 		final Query query;
 		if ( nqd != null ) {
 			query = createQuery( nqd );
 		}
 		else {
 			NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 			if ( nsqlqd==null ) {
 				throw new MappingException( "Named query not known: " + queryName );
 			}
 
 			query = createSQLQuery( nsqlqd );
 			nqd = nsqlqd;
 		}
 		initQuery( query, nqd );
 		return query;
 	}
 
 	@Override
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
 		// todo : cacheable and readonly should be Boolean rather than boolean...
 		query.setCacheable( nqd.isCacheable() );
 		query.setCacheRegion( nqd.getCacheRegion() );
 		query.setReadOnly( nqd.isReadOnly() );
 
 		if ( nqd.getTimeout() != null ) {
 			query.setTimeout( nqd.getTimeout() );
 		}
 		if ( nqd.getFetchSize() != null ) {
 			query.setFetchSize( nqd.getFetchSize() );
 		}
 		if ( nqd.getCacheMode() != null ) {
 			query.setCacheMode( nqd.getCacheMode() );
 		}
 		if ( nqd.getComment() != null ) {
 			query.setComment( nqd.getComment() );
 		}
 		if ( nqd.getFirstResult() != null ) {
 			query.setFirstResult( nqd.getFirstResult() );
 		}
 		if ( nqd.getMaxResults() != null ) {
 			query.setMaxResults( nqd.getMaxResults() );
 		}
 		if ( nqd.getFlushMode() != null ) {
 			query.setFlushMode( nqd.getFlushMode() );
 		}
 	}
 
 	@Override
 	public Query createQuery(String queryString) {
 		errorIfClosed();
 		final QueryImpl query = new QueryImpl(
 				queryString,
 				this,
 				getHQLQueryPlan( queryString, false ).getParameterMetadata()
 		);
 		query.setComment( queryString );
 		return query;
 	}
 
 	@Override
 	public SQLQuery createSQLQuery(String sql) {
 		errorIfClosed();
 		final SQLQueryImpl query = new SQLQueryImpl(
 				sql,
 				this,
 				factory.getQueryPlanCache().getSQLParameterMetadata( sql )
 		);
 		query.setComment( "dynamic native SQL query" );
 		return query;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall getNamedProcedureCall(String name) {
 		errorIfClosed();
 
 		final ProcedureCallMemento memento = factory.getNamedQueryRepository().getNamedProcedureCallMemento( name );
 		if ( memento == null ) {
 			throw new IllegalArgumentException(
 					"Could not find named stored procedure call with that registration name : " + name
 			);
 		}
 		final ProcedureCall procedureCall = memento.makeProcedureCall( this );
 //		procedureCall.setComment( "Named stored procedure call [" + name + "]" );
 		return procedureCall;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall createStoredProcedureCall(String procedureName) {
 		errorIfClosed();
 		final ProcedureCall procedureCall = new ProcedureCallImpl( this, procedureName );
 //		call.setComment( "Dynamic stored procedure call" );
 		return procedureCall;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		errorIfClosed();
 		final ProcedureCall procedureCall = new ProcedureCallImpl( this, procedureName, resultClasses );
 //		call.setComment( "Dynamic stored procedure call" );
 		return procedureCall;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		errorIfClosed();
 		final ProcedureCall procedureCall = new ProcedureCallImpl( this, procedureName, resultSetMappings );
 //		call.setComment( "Dynamic stored procedure call" );
 		return procedureCall;
 	}
 
 	protected HQLQueryPlan getHQLQueryPlan(String query, boolean shallow) throws HibernateException {
 		return factory.getQueryPlanCache().getHQLQueryPlan( query, shallow, getEnabledFilters() );
 	}
 
 	protected NativeSQLQueryPlan getNativeSQLQueryPlan(NativeSQLQuerySpecification spec) throws HibernateException {
 		return factory.getQueryPlanCache().getNativeSQLQueryPlan( spec );
 	}
 
 	@Override
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return listCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException {
 		return scrollCustomQuery( getNativeSQLQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public String getTenantIdentifier() {
 		return tenantIdentifier;
 	}
 
 	@Override
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
 		return new EntityKey( id, persister, getTenantIdentifier() );
 	}
 
 	@Override
 	public CacheKey generateCacheKey(Serializable id, Type type, String entityOrRoleName) {
 		return new CacheKey( id, type, entityOrRoleName, getTenantIdentifier(), getFactory() );
 	}
 
 	private transient JdbcConnectionAccess jdbcConnectionAccess;
 
 	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		if ( jdbcConnectionAccess == null ) {
 			if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 				jdbcConnectionAccess = new NonContextualJdbcConnectionAccess(
-						getSessionEventsManager(),
+						getEventListenerManager(),
 						factory.getServiceRegistry().getService( ConnectionProvider.class )
 				);
 			}
 			else {
 				jdbcConnectionAccess = new ContextualJdbcConnectionAccess(
-						getSessionEventsManager(),
+						getEventListenerManager(),
 						factory.getServiceRegistry().getService( MultiTenantConnectionProvider.class )
 				);
 			}
 		}
 		return jdbcConnectionAccess;
 	}
 
 	private UUID sessionIdentifier;
 
 	public UUID getSessionIdentifier() {
 		if ( sessionIdentifier == null ) {
 			sessionIdentifier = StandardRandomStrategy.INSTANCE.generateUUID( this );
 		}
 		return sessionIdentifier;
 	}
 
 	private static class NonContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
-		private final SessionEventsListener listener;
+		private final SessionEventListener listener;
 		private final ConnectionProvider connectionProvider;
 
 		private NonContextualJdbcConnectionAccess(
-				SessionEventsListener listener,
+				SessionEventListener listener,
 				ConnectionProvider connectionProvider) {
 			this.listener = listener;
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			try {
 				listener.jdbcConnectionAcquisitionStart();
 				return connectionProvider.getConnection();
 			}
 			finally {
 				listener.jdbcConnectionAcquisitionEnd();
 			}
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			try {
 				listener.jdbcConnectionReleaseStart();
 				connectionProvider.closeConnection( connection );
 			}
 			finally {
 				listener.jdbcConnectionReleaseEnd();
 			}
 		}
 
 		@Override
 		public boolean supportsAggressiveRelease() {
 			return connectionProvider.supportsAggressiveRelease();
 		}
 	}
 
 	private class ContextualJdbcConnectionAccess implements JdbcConnectionAccess, Serializable {
-		private final SessionEventsListener listener;
+		private final SessionEventListener listener;
 		private final MultiTenantConnectionProvider connectionProvider;
 
 		private ContextualJdbcConnectionAccess(
-				SessionEventsListener listener,
+				SessionEventListener listener,
 				MultiTenantConnectionProvider connectionProvider) {
 			this.listener = listener;
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 
 			try {
 				listener.jdbcConnectionAcquisitionStart();
 				return connectionProvider.getConnection( tenantIdentifier );
 			}
 			finally {
 				listener.jdbcConnectionAcquisitionEnd();
 			}
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			if ( tenantIdentifier == null ) {
 				throw new HibernateException( "Tenant identifier required!" );
 			}
 
 			try {
 				listener.jdbcConnectionReleaseStart();
 				connectionProvider.releaseConnection( tenantIdentifier, connection );
 			}
 			finally {
 				listener.jdbcConnectionReleaseEnd();
 			}
 		}
 
 		@Override
 		public boolean supportsAggressiveRelease() {
 			return connectionProvider.supportsAggressiveRelease();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 8591a8ee1a..74e69b208a 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1805 +1,1805 @@
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
 package org.hibernate.internal;
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
-import org.hibernate.SessionEventsListener;
+import org.hibernate.SessionEventListener;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.TypeHelper;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.NaturalIdRegion;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
 import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.cfg.SettingsFactory;
 import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jndi.spi.JndiService;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.spi.CacheImplementor;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionBuilderImplementor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.SchemaValidator;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.jboss.logging.Logger;
 
 
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
  * @see org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
  * @see org.hibernate.Session
  * @see org.hibernate.hql.spi.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl
 		implements SessionFactoryImplementor {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionFactoryImpl.class.getName());
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private final String name;
 	private final String uuid;
 
 	private final transient Map<String,EntityPersister> entityPersisters;
 	private final transient Map<String,ClassMetadata> classMetadata;
 	private final transient Map<String,CollectionPersister> collectionPersisters;
 	private final transient Map<String,CollectionMetadata> collectionMetadata;
 	private final transient Map<String,Set<String>> collectionRolesByEntityParticipant;
 	private final transient Map<String,IdentifierGenerator> identifierGenerators;
 	private final transient NamedQueryRepository namedQueryRepository;
 	private final transient Map<String, FilterDefinition> filters;
 	private final transient Map<String, FetchProfile> fetchProfiles;
 	private final transient Map<String,String> imports;
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
 	private final transient JdbcServices jdbcServices;
 	private final transient Dialect dialect;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
 	private final transient ConcurrentHashMap<EntityNameResolver,Object> entityNameResolvers = new ConcurrentHashMap<EntityNameResolver, Object>();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient CacheImplementor cacheAccess;
 	private transient boolean isClosed = false;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 	private final transient TransactionEnvironment transactionEnvironment;
 	private final transient SessionFactoryOptions sessionFactoryOptions;
 	private final transient CustomEntityDirtinessStrategy customEntityDirtinessStrategy;
 	private final transient CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
 
 	@SuppressWarnings( {"unchecked", "ThrowableResultOfMethodCallIgnored"})
 	public SessionFactoryImpl(
 			final Configuration cfg,
 			Mapping mapping,
 			final ServiceRegistry serviceRegistry,
 			Settings settings,
 			SessionFactoryObserver observer) throws HibernateException {
 			LOG.debug( "Building session factory" );
 
 		sessionFactoryOptions = new SessionFactoryOptions() {
 			private EntityNotFoundDelegate entityNotFoundDelegate;
 
 			@Override
 			public StandardServiceRegistry getServiceRegistry() {
 				return (StandardServiceRegistry) serviceRegistry;
 			}
 
 			@Override
 			public Interceptor getInterceptor() {
 				return cfg.getInterceptor();
 			}
 
 			@Override
 			public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 				if ( entityNotFoundDelegate == null ) {
 					if ( cfg.getEntityNotFoundDelegate() != null ) {
 						entityNotFoundDelegate = cfg.getEntityNotFoundDelegate();
 					}
 					else {
 						entityNotFoundDelegate = new EntityNotFoundDelegate() {
 							public void handleEntityNotFound(String entityName, Serializable id) {
 								throw new ObjectNotFoundException( id, entityName );
 							}
 						};
 					}
 				}
 				return entityNotFoundDelegate;
 			}
 		};
 
 		this.settings = settings;
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 
 		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
 				this,
 				cfg
 		);
         this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
         this.dialect = this.jdbcServices.getDialect();
 		this.cacheAccess = this.serviceRegistry.getService( CacheImplementor.class );
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = cfg.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		this.filters.putAll( cfg.getFilterDefinitions() );
 
 		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 		LOG.debugf( "Instantiating session factory with properties: %s", properties );
 
 
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
                 integrators.clear();
 			}
 		}
 
 		final IntegratorObserver integratorObserver = new IntegratorObserver();
 		this.observer.addObserver( integratorObserver );
 		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
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
 
 		imports = new HashMap<String,String>( cfg.getImports() );
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		final RegionFactory regionFactory = cacheAccess.getRegionFactory();
 		final String cacheRegionPrefix = settings.getCacheRegionPrefix() == null ? "" : settings.getCacheRegionPrefix() + ".";
 		final PersisterFactory persisterFactory = serviceRegistry.getService( PersisterFactory.class );
 
 		// todo : consider removing this silliness and just have EntityPersister directly implement ClassMetadata
 		//		EntityPersister.getClassMetadata() for the internal impls simply "return this";
 		//		collapsing those would allow us to remove this "extra" Map
 		//
 		// todo : similar for CollectionPersister/CollectionMetadata
 
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
 				final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 				if ( accessType != null ) {
 					LOG.tracef( "Building shared cache region for entity data [%s]", model.getEntityName() );
 					EntityRegion entityRegion = regionFactory.buildEntityRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					cacheAccess.addCacheRegion( cacheRegionName, entityRegion );
 				}
 			}
 
 			NaturalIdRegionAccessStrategy naturalIdAccessStrategy = null;
 			if ( model.hasNaturalId() && model.getNaturalIdCacheRegionName() != null ) {
 				final String naturalIdCacheRegionName = cacheRegionPrefix + model.getNaturalIdCacheRegionName();
 				naturalIdAccessStrategy = ( NaturalIdRegionAccessStrategy ) entityAccessStrategies.get( naturalIdCacheRegionName );
 
 				if ( naturalIdAccessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 					final CacheDataDescriptionImpl cacheDataDescription = CacheDataDescriptionImpl.decode( model );
 
 					NaturalIdRegion naturalIdRegion = null;
 					try {
 						naturalIdRegion = regionFactory.buildNaturalIdRegion( naturalIdCacheRegionName, properties,
 								cacheDataDescription );
 					}
 					catch ( UnsupportedOperationException e ) {
 						LOG.warnf(
 								"Shared cache region factory [%s] does not support natural id caching; " +
 										"shared NaturalId caching will be disabled for not be enabled for %s",
 								regionFactory.getClass().getName(),
 								model.getEntityName()
 						);
 					}
 
 					if (naturalIdRegion != null) {
 						naturalIdAccessStrategy = naturalIdRegion.buildAccessStrategy( regionFactory.getDefaultAccessType() );
 						entityAccessStrategies.put( naturalIdCacheRegionName, naturalIdAccessStrategy );
 						cacheAccess.addCacheRegion(  naturalIdCacheRegionName, naturalIdRegion );
 					}
 				}
 			}
 
 			EntityPersister cp = persisterFactory.createEntityPersister(
 					model,
 					accessStrategy,
 					naturalIdAccessStrategy,
 					this,
 					mapping
 			);
 			entityPersisters.put( model.getEntityName(), cp );
 			classMeta.put( model.getEntityName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap<String,CollectionPersister>();
 		Map<String,CollectionMetadata> tmpCollectionMetadata = new HashMap<String,CollectionMetadata>();
 		Iterator collections = cfg.getCollectionMappings();
 		while ( collections.hasNext() ) {
 			Collection model = (Collection) collections.next();
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				LOG.tracev( "Building shared cache region for collection data [{0}]", model.getRole() );
 				CollectionRegion collectionRegion = regionFactory.buildCollectionRegion( cacheRegionName, properties, CacheDataDescriptionImpl
 						.decode( model ) );
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = persisterFactory.createCollectionPersister(
 					cfg,
 					model,
 					accessStrategy,
 					this
 			) ;
 			collectionPersisters.put( model.getRole(), persister );
 			tmpCollectionMetadata.put( model.getRole(), persister.getCollectionMetadata() );
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
 		collectionMetadata = Collections.unmodifiableMap( tmpCollectionMetadata );
 		Iterator itr = tmpEntityToCollectionRoleMap.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			entry.setValue( Collections.unmodifiableSet( ( Set ) entry.getValue() ) );
 		}
 		collectionRolesByEntityParticipant = Collections.unmodifiableMap( tmpEntityToCollectionRoleMap );
 
 		//Named Queries:
 		this.namedQueryRepository = new NamedQueryRepository(
 				cfg.getNamedQueries().values(),
 				cfg.getNamedSQLQueries().values(),
 				cfg.getSqlResultSetMappings().values(),
 				toProcedureCallMementos( cfg.getNamedProcedureCallMap(), cfg.getSqlResultSetMappings() )
 		);
 
 		// after *all* persisters and named queries are registered
 		for ( EntityPersister persister : entityPersisters.values() ) {
 			persister.generateEntityDefinition();
 		}
 
 		for ( EntityPersister persister : entityPersisters.values() ) {
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister );
 		}
 		for ( CollectionPersister persister : collectionPersisters.values() ) {
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
 		SessionFactoryRegistry.INSTANCE.addSessionFactory(
 				uuid,
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				this,
 				serviceRegistry.getService( JndiService.class )
 		);
 
 		LOG.debug( "Instantiated session factory" );
 
 		settings.getMultiTableBulkIdStrategy().prepare(
 				jdbcServices,
 				buildLocalConnectionAccess(),
 				cfg.createMappings(),
 				cfg.buildMapping(),
 				properties
 		);
 
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( serviceRegistry, cfg )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( serviceRegistry, cfg ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( serviceRegistry, cfg ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( serviceRegistry, cfg )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			final Map<String,HibernateException> errors = checkNamedQueries();
 			if ( ! errors.isEmpty() ) {
 				StringBuilder failingQueries = new StringBuilder( "Errors in named queries: " );
 				String sep = "";
 				for ( Map.Entry<String,HibernateException> entry : errors.entrySet() ) {
 					LOG.namedQueryError( entry.getKey(), entry.getValue() );
 					failingQueries.append( sep ).append( entry.getKey() );
 					sep = ", ";
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap();
 		itr = cfg.iterateFetchProfiles();
 		while ( itr.hasNext() ) {
 			final org.hibernate.mapping.FetchProfile mappingProfile =
 					( org.hibernate.mapping.FetchProfile ) itr.next();
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			for ( org.hibernate.mapping.FetchProfile.Fetch mappingFetch : mappingProfile.getFetches() ) {
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = entityName == null
 						? null
 						: entityPersisters.get( entityName );
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
 				((Loadable) owner).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.customEntityDirtinessStrategy = determineCustomEntityDirtinessStrategy();
 		this.currentTenantIdentifierResolver = determineCurrentTenantIdentifierResolver( cfg.getCurrentTenantIdentifierResolver() );
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
 	}
 
 	private Map<String, ProcedureCallMemento> toProcedureCallMementos(
 			Map<String, NamedProcedureCallDefinition> definitions,
 			Map<String, ResultSetMappingDefinition> resultSetMappingMap) {
 		final Map<String, ProcedureCallMemento> rtn = new HashMap<String, ProcedureCallMemento>();
 		if ( definitions != null ) {
 			for (String name : definitions.keySet()){
 				rtn.put( name,  definitions.get( name ).toMemento( this, resultSetMappingMap ));
 			}
 		}
 		return rtn;
 	}
 
 	private JdbcConnectionAccess buildLocalConnectionAccess() {
 		return new JdbcConnectionAccess() {
 			@Override
 			public Connection obtainConnection() throws SQLException {
 				return settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE
 						? serviceRegistry.getService( ConnectionProvider.class ).getConnection()
 						: serviceRegistry.getService( MultiTenantConnectionProvider.class ).getAnyConnection();
 			}
 
 			@Override
 			public void releaseConnection(Connection connection) throws SQLException {
 				if ( settings.getMultiTenancyStrategy() == MultiTenancyStrategy.NONE ) {
 					serviceRegistry.getService( ConnectionProvider.class ).closeConnection( connection );
 				}
 				else {
 					serviceRegistry.getService( MultiTenantConnectionProvider.class ).releaseAnyConnection( connection );
 				}
 			}
 
 			@Override
 			public boolean supportsAggressiveRelease() {
 				return false;
 			}
 		};
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private CustomEntityDirtinessStrategy determineCustomEntityDirtinessStrategy() {
 		CustomEntityDirtinessStrategy defaultValue = new CustomEntityDirtinessStrategy() {
 			@Override
 			public boolean canDirtyCheck(Object entity, EntityPersister persister, Session session) {
 				return false;
 			}
 
 			@Override
 			public boolean isDirty(Object entity, EntityPersister persister, Session session) {
 				return false;
 			}
 
 			@Override
 			public void resetDirty(Object entity, EntityPersister persister, Session session) {
 			}
 
 			@Override
 			public void findDirty(
 					Object entity,
 					EntityPersister persister,
 					Session session,
 					DirtyCheckContext dirtyCheckContext) {
 				// todo : implement proper method body
 			}
 		};
 		return serviceRegistry.getService( ConfigurationService.class ).getSetting(
 				AvailableSettings.CUSTOM_ENTITY_DIRTINESS_STRATEGY,
 				CustomEntityDirtinessStrategy.class,
 				defaultValue
 		);
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	private CurrentTenantIdentifierResolver determineCurrentTenantIdentifierResolver(
 			CurrentTenantIdentifierResolver explicitResolver) {
 		if ( explicitResolver != null ) {
 			return explicitResolver;
 		}
 		return serviceRegistry.getService( ConfigurationService.class )
 				.getSetting(
 						AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER,
 						CurrentTenantIdentifierResolver.class,
 						null
 				);
 
 	}
 
 	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
 	public SessionFactoryImpl(
 			MetadataImplementor metadata,
 			SessionFactoryOptions sessionFactoryOptions,
 			SessionFactoryObserver observer) throws HibernateException {
 
 		final boolean traceEnabled = LOG.isTraceEnabled();
 		final boolean debugEnabled = traceEnabled || LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debug( "Building session factory" );
 		}
 
 		this.sessionFactoryOptions = sessionFactoryOptions;
 
 		this.properties = createPropertiesFromMap(
 				metadata.getServiceRegistry().getService( ConfigurationService.class ).getSettings()
 		);
 
 		// TODO: these should be moved into SessionFactoryOptions
 		this.settings = new SettingsFactory().buildSettings(
 				properties,
 				metadata.getServiceRegistry()
 		);
 
 		this.serviceRegistry =
 				sessionFactoryOptions.getServiceRegistry()
 						.getService( SessionFactoryServiceRegistryFactory.class )
 						.buildServiceRegistry( this, metadata );
 
 		this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
 		this.dialect = this.jdbcServices.getDialect();
 		this.cacheAccess = this.serviceRegistry.getService( CacheImplementor.class );
 
 		// TODO: get SQL functions from JdbcServices (HHH-6559)
 		//this.sqlFunctionRegistry = new SQLFunctionRegistry( this.jdbcServices.getSqlFunctions() );
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( this.dialect, new HashMap<String, SQLFunction>() );
 
 		// TODO: get SQL functions from a new service
 		// this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = metadata.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		for ( FilterDefinition filterDefinition : metadata.getFilterDefinitions() ) {
 			filters.put( filterDefinition.getFilterName(), filterDefinition );
 		}
 
 		if ( debugEnabled ) {
 			LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 			LOG.debugf( "Instantiating session factory with properties: %s", properties );
 		}
 		this.queryPlanCache = new QueryPlanCache( this );
 
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
                 integrators.clear();
 			}
 		}
 
         final IntegratorObserver integratorObserver = new IntegratorObserver();
         this.observer.addObserver(integratorObserver);
         for (Integrator integrator : serviceRegistry.getService(IntegratorService.class).getIntegrators()) {
             integrator.integrate(metadata, this, this.serviceRegistry);
             integratorObserver.integrators.add(integrator);
         }
 
 
 		//Generators:
 
 		identifierGenerators = new HashMap<String,IdentifierGenerator>();
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
 			if ( entityBinding.isRoot() ) {
 				identifierGenerators.put(
 						entityBinding.getEntity().getName(),
 						entityBinding.getHierarchyDetails().getEntityIdentifier().getIdentifierGenerator()
 				);
 			}
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		StringBuilder stringBuilder = new StringBuilder();
 		if ( settings.getCacheRegionPrefix() != null) {
 			stringBuilder
 					.append( settings.getCacheRegionPrefix() )
 					.append( '.' );
 		}
 		final String cacheRegionPrefix = stringBuilder.toString();
 
 		entityPersisters = new HashMap<String,EntityPersister>();
 		Map<String, RegionAccessStrategy> entityAccessStrategies = new HashMap<String, RegionAccessStrategy>();
 		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
 		for ( EntityBinding model : metadata.getEntityBindings() ) {
 			// TODO: should temp table prep happen when metadata is being built?
 			//model.prepareTemporaryTables( metadata, getDialect() );
 			// cache region is defined by the root-class in the hierarchy...
 			EntityBinding rootEntityBinding = metadata.getRootEntityBinding( model.getEntity().getName() );
 			EntityRegionAccessStrategy accessStrategy = null;
 			if ( settings.isSecondLevelCacheEnabled() &&
 					rootEntityBinding.getHierarchyDetails().getCaching() != null &&
 					model.getHierarchyDetails().getCaching() != null &&
 					model.getHierarchyDetails().getCaching().getAccessType() != null ) {
 				final String cacheRegionName = cacheRegionPrefix + rootEntityBinding.getHierarchyDetails().getCaching().getRegion();
 				accessStrategy = EntityRegionAccessStrategy.class.cast( entityAccessStrategies.get( cacheRegionName ) );
 				if ( accessStrategy == null ) {
 					final AccessType accessType = model.getHierarchyDetails().getCaching().getAccessType();
 					if ( traceEnabled ) {
 						LOG.tracev( "Building cache for entity data [{0}]", model.getEntity().getName() );
 					}
 					EntityRegion entityRegion = settings.getRegionFactory().buildEntityRegion(
 							cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 					);
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					cacheAccess.addCacheRegion( cacheRegionName, entityRegion );
 				}
 			}
 			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
 					model, accessStrategy, this, metadata
 			);
 			entityPersisters.put( model.getEntity().getName(), cp );
 			classMeta.put( model.getEntity().getName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap<String,CollectionPersister>();
 		Map<String, CollectionMetadata> tmpCollectionMetadata = new HashMap<String, CollectionMetadata>();
 		for ( PluralAttributeBinding model : metadata.getCollectionBindings() ) {
 			if ( model.getAttribute() == null ) {
 				throw new IllegalStateException( "No attribute defined for a AbstractPluralAttributeBinding: " +  model );
 			}
 			if ( model.getAttribute().isSingular() ) {
 				throw new IllegalStateException(
 						"AbstractPluralAttributeBinding has a Singular attribute defined: " + model.getAttribute().getName()
 				);
 			}
 			final String cacheRegionName = cacheRegionPrefix + model.getCaching().getRegion();
 			final AccessType accessType = model.getCaching().getAccessType();
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				if ( traceEnabled ) {
 					LOG.tracev( "Building cache for collection data [{0}]", model.getAttribute().getRole() );
 				}
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion(
 						cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 				);
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				cacheAccess.addCacheRegion(  cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = serviceRegistry
 					.getService( PersisterFactory.class )
 					.createCollectionPersister( metadata, model, accessStrategy, this );
 			collectionPersisters.put( model.getAttribute().getRole(), persister );
 			tmpCollectionMetadata.put( model.getAttribute().getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set<String> roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set<String> roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet<String>();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		collectionMetadata = Collections.unmodifiableMap( tmpCollectionMetadata );
 		for ( Map.Entry<String, Set<String>> entry : tmpEntityToCollectionRoleMap.entrySet() ) {
 			entry.setValue( Collections.unmodifiableSet( entry.getValue() ) );
 		}
 		collectionRolesByEntityParticipant = Collections.unmodifiableMap( tmpEntityToCollectionRoleMap );
 
 
 		//Named Queries:
 		namedQueryRepository = new NamedQueryRepository(
 				metadata.getNamedQueryDefinitions(),
 				metadata.getNamedNativeQueryDefinitions(),
 				metadata.getResultSetMappingDefinitions(),
 				new HashMap<String, ProcedureCallMemento>(  )
 		);
 
 		imports = new HashMap<String,String>();
 		for ( Map.Entry<String,String> importEntry : metadata.getImports() ) {
 			imports.put( importEntry.getKey(), importEntry.getValue() );
 		}
 
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
 		SessionFactoryRegistry.INSTANCE.addSessionFactory(
 				uuid, 
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				this,
 				serviceRegistry.getService( JndiService.class )
 		);
 
 		if ( debugEnabled ) {
 			LOG.debug("Instantiated session factory");
 		}
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) )
 					.create( false, true );
 		}
 
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( metadata )
 					.setImportSqlCommandExtractor( serviceRegistry.getService( ImportSqlCommandExtractor.class ) );
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			final Map<String,HibernateException> errors = checkNamedQueries();
 			if ( ! errors.isEmpty() ) {
 				StringBuilder failingQueries = new StringBuilder( "Errors in named queries: " );
 				String sep = "";
 				for ( Map.Entry<String,HibernateException> entry : errors.entrySet() ) {
 					LOG.namedQueryError( entry.getKey(), entry.getValue() );
 					failingQueries.append( entry.getKey() ).append( sep );
 					sep = ", ";
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap<String,FetchProfile>();
 		for ( org.hibernate.metamodel.binding.FetchProfile mappingProfile : metadata.getFetchProfiles() ) {
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			for ( org.hibernate.metamodel.binding.FetchProfile.Fetch mappingFetch : mappingProfile.getFetches() ) {
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = entityName == null ? null : entityPersisters.get( entityName );
 				if ( owner == null ) {
 					throw new HibernateException(
 							"Unable to resolve entity reference [" + mappingFetch.getEntity()
 									+ "] in fetch profile [" + fetchProfile.getName() + "]"
 					);
 				}
 
 				// validate the specified association fetch
 				Type associationType = owner.getPropertyType( mappingFetch.getAssociation() );
 				if ( associationType == null || ! associationType.isAssociationType() ) {
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
 
 		this.customEntityDirtinessStrategy = determineCustomEntityDirtinessStrategy();
 		this.currentTenantIdentifierResolver = determineCurrentTenantIdentifierResolver( null );
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
 	}
 
 	@SuppressWarnings( {"unchecked"} )
 	private static Properties createPropertiesFromMap(Map map) {
 		Properties properties = new Properties();
 		properties.putAll( map );
 		return properties;
 	}
 
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
 	public SessionBuilderImplementor withOptions() {
 		return new SessionBuilderImpl( this );
 	}
 
 	@Override
 	public StatelessSessionBuilder withStatelessOptions() {
 		return new StatelessSessionBuilderImpl( this );
 	}
 
 	public StatelessSession openStatelessSession() {
 		return withStatelessOptions().openStatelessSession();
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return withStatelessOptions().connection( connection ).openStatelessSession();
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
 		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizer() == null ) {
 			return;
 		}
 		registerEntityNameResolvers( persister.getEntityMetamodel().getTuplizer() );
 	}
 
 	private void registerEntityNameResolvers(EntityTuplizer tuplizer) {
 		EntityNameResolver[] resolvers = tuplizer.getEntityNameResolvers();
 		if ( resolvers == null ) {
 			return;
 		}
 
 		for ( EntityNameResolver resolver : resolvers ) {
 			registerEntityNameResolver( resolver );
 		}
 	}
 
 	private static final Object ENTITY_NAME_RESOLVER_MAP_VALUE = new Object();
 
 	public void registerEntityNameResolver(EntityNameResolver resolver) {
 		entityNameResolvers.put( resolver, ENTITY_NAME_RESOLVER_MAP_VALUE );
 	}
 
 	@Override
 	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
 		return entityNameResolvers.keySet();
 	}
 
 	public QueryPlanCache getQueryPlanCache() {
 		return queryPlanCache;
 	}
 
 	private Map<String,HibernateException> checkNamedQueries() throws HibernateException {
 		return namedQueryRepository.checkNamedQueries( queryPlanCache );
 	}
 
 	public EntityPersister getEntityPersister(String entityName) throws MappingException {
 		EntityPersister result = entityPersisters.get(entityName);
 		if ( result == null ) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 	@Override
 	public Map<String, CollectionPersister> getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	@Override
 	public Map<String, EntityPersister> getEntityPersisters() {
 		return entityPersisters;
 	}
 
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
 		CollectionPersister result = collectionPersisters.get(role);
 		if ( result == null ) {
 			throw new MappingException( "Unknown collection role: " + role );
 		}
 		return result;
 	}
 
 	public Settings getSettings() {
 		return settings;
 	}
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return sessionFactoryOptions;
 	}
 
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
 	}
 
 	public Dialect getDialect() {
 		if ( serviceRegistry == null ) {
 			throw new IllegalStateException( "Cannot determine dialect because serviceRegistry is null." );
 		}
 		return dialect;
 	}
 
 	public Interceptor getInterceptor() {
 		return sessionFactoryOptions.getInterceptor();
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
 
 	@Override
 	public Reference getReference() {
 		// from javax.naming.Referenceable
         LOG.debug( "Returning a Reference to the SessionFactory" );
 		return new Reference(
 				SessionFactoryImpl.class.getName(),
 				new StringRefAddr("uuid", uuid),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	@Override
 	public NamedQueryRepository getNamedQueryRepository() {
 		return namedQueryRepository;
 	}
 
 	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
 		namedQueryRepository.registerNamedQueryDefinition( name, definition );
 	}
 
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return namedQueryRepository.getNamedQueryDefinition( queryName );
 	}
 
 	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
 		namedQueryRepository.registerNamedSQLQueryDefinition( name, definition );
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return namedQueryRepository.getNamedSQLQueryDefinition( queryName );
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String mappingName) {
 		return namedQueryRepository.getResultSetMappingDefinition( mappingName );
 	}
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierPropertyName();
 	}
 
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata()
 				.getReturnTypes();
 	}
 
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, Collections.EMPTY_MAP )
 				.getReturnMetadata()
 				.getReturnAliases();
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getClassMetadata( persistentClass.getName() );
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return collectionMetadata.get(roleName);
 	}
 
 	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
 		return classMetadata.get( entityName );
 	}
 
 	/**
 	 * Given the name of an entity class, determine all the class and interface names by which it can be
 	 * referenced in an HQL query.
 	 *
      * @param className The name of the entity class
 	 *
 	 * @return the names of all persistent (mapped) classes that extend or implement the
 	 *     given class or interface, accounting for implicit/explicit polymorphism settings
 	 *     and excluding mapped subclasses/joined-subclasses of other classes in the result.
 	 * @throws MappingException
 	 */
 	public String[] getImplementors(String className) throws MappingException {
 
 		final Class clazz;
 		try {
 			clazz = serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
 		}
 		catch (ClassLoadingException cnfe) {
 			return new String[] { className }; //for a dynamic-class
 		}
 
 		ArrayList<String> results = new ArrayList<String>();
 		for ( EntityPersister checkPersister : entityPersisters.values() ) {
 			if ( ! Queryable.class.isInstance( checkPersister ) ) {
 				continue;
 			}
 			final Queryable checkQueryable = Queryable.class.cast( checkPersister );
 			final String checkQueryableEntityName = checkQueryable.getEntityName();
 			final boolean isMappedClass = className.equals( checkQueryableEntityName );
 			if ( checkQueryable.isExplicitPolymorphism() ) {
 				if ( isMappedClass ) {
 					return new String[] { className }; //NOTE EARLY EXIT
 				}
 			}
 			else {
 				if ( isMappedClass ) {
 					results.add( checkQueryableEntityName );
 				}
 				else {
 					final Class mappedClass = checkQueryable.getMappedClass();
 					if ( mappedClass != null && clazz.isAssignableFrom( mappedClass ) ) {
 						final boolean assignableSuperclass;
 						if ( checkQueryable.isInherited() ) {
 							Class mappedSuperclass = getEntityPersister( checkQueryable.getMappedSuperclass() ).getMappedClass();
 							assignableSuperclass = clazz.isAssignableFrom( mappedSuperclass );
 						}
 						else {
 							assignableSuperclass = false;
 						}
 						if ( !assignableSuperclass ) {
 							results.add( checkQueryableEntityName );
 						}
 					}
 				}
 			}
 		}
 		return results.toArray( new String[results.size()] );
 	}
 
 	@Override
 	public String getImportedClassName(String className) {
 		String result = imports.get( className );
 		if ( result == null ) {
 			try {
 				serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
 				imports.put( className, className );
 				return className;
 			}
 			catch ( ClassLoadingException cnfe ) {
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
 		return jdbcServices.getConnectionProvider();
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
 	 * @throws HibernateException
 	 */
 	public void close() throws HibernateException {
 
 		if ( isClosed ) {
 			LOG.trace( "Already closed" );
 			return;
 		}
 
 		LOG.closing();
 
 		isClosed = true;
 
 		settings.getMultiTableBulkIdStrategy().release( jdbcServices, buildLocalConnectionAccess() );
 
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
 
 		cacheAccess.close();
 
 		queryPlanCache.cleanup();
 
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport.drop( false, true );
 		}
 
 		SessionFactoryRegistry.INSTANCE.removeSessionFactory(
 				uuid,
 				name,
 				settings.isSessionFactoryNameAlsoJndiName(),
 				serviceRegistry.getService( JndiService.class )
 		);
 
 		observer.sessionFactoryClosed( this );
 		serviceRegistry.destroy();
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
 		cacheAccess.evictQueries();
 	}
 
 	public void evictQueries(String regionName) throws HibernateException {
 		getCache().evictQueryRegion( regionName );
 	}
 
 	public UpdateTimestampsCache getUpdateTimestampsCache() {
 		return cacheAccess.getUpdateTimestampsCache();
 	}
 
 	public QueryCache getQueryCache() {
 		return cacheAccess.getQueryCache();
 	}
 
 	public QueryCache getQueryCache(String regionName) throws HibernateException {
 		return cacheAccess.getQueryCache( regionName );
 	}
 
 	public Region getSecondLevelCacheRegion(String regionName) {
 		return cacheAccess.getSecondLevelCacheRegion( regionName );
 	}
 
 	public Region getNaturalIdCacheRegion(String regionName) {
 		return cacheAccess.getNaturalIdCacheRegion( regionName );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public Map getAllSecondLevelCacheRegions() {
 		return cacheAccess.getAllSecondLevelCacheRegions();
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
 		FilterDefinition def = filters.get( filterName );
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
 		return identifierGenerators.get(rootEntityName);
 	}
 
 	private TransactionFactory transactionFactory() {
 		return serviceRegistry.getService( TransactionFactory.class );
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
 				Class implClass = serviceRegistry.getService( ClassLoaderService.class ).classForName( impl );
 				return ( CurrentSessionContext ) implClass
 						.getConstructor( new Class[] { SessionFactoryImplementor.class } )
 						.newInstance( this );
 			}
 			catch( Throwable t ) {
 				LOG.unableToConstructCurrentSessionContext( impl, t );
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
 		return sessionFactoryOptions.getEntityNotFoundDelegate();
 	}
 
 	public SQLFunctionRegistry getSqlFunctionRegistry() {
 		return sqlFunctionRegistry;
 	}
 
 	public FetchProfile getFetchProfile(String name) {
 		return fetchProfiles.get( name );
 	}
 
 	public TypeHelper getTypeHelper() {
 		return typeHelper;
 	}
 
 	static class SessionBuilderImpl implements SessionBuilderImplementor {
 		private static final Logger log = CoreLogging.logger( SessionBuilderImpl.class );
 
 		private final SessionFactoryImpl sessionFactory;
 		private SessionOwner sessionOwner;
 		private Interceptor interceptor;
 		private Connection connection;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private boolean autoClose;
 		private boolean autoJoinTransactions = true;
 		private boolean flushBeforeCompletion;
 		private String tenantIdentifier;
-		private List<SessionEventsListener> listeners;
+		private List<SessionEventListener> listeners;
 
 		SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 			this.sessionOwner = null;
 			final Settings settings = sessionFactory.settings;
 
 			// set up default builder values...
 			this.interceptor = sessionFactory.getInterceptor();
 			this.connectionReleaseMode = settings.getConnectionReleaseMode();
 			this.autoClose = settings.isAutoCloseSessionEnabled();
 			this.flushBeforeCompletion = settings.isFlushBeforeCompletionEnabled();
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
 
 			listeners = settings.getBaselineSessionEventsListenerBuilder().buildBaselineList();
 		}
 
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			log.tracef( "Opening Hibernate Session.  tenant=%s, owner=%s", tenantIdentifier, sessionOwner );
 			final SessionImpl session = new SessionImpl(
 					connection,
 					sessionFactory,
 					sessionOwner,
 					getTransactionCoordinator(),
 					autoJoinTransactions,
 					sessionFactory.settings.getRegionFactory().nextTimestamp(),
 					interceptor,
 					flushBeforeCompletion,
 					autoClose,
 					connectionReleaseMode,
 					tenantIdentifier
 			);
 
-			for ( SessionEventsListener listener : listeners ) {
-				session.getSessionEventsManager().addListener( listener );
+			for ( SessionEventListener listener : listeners ) {
+				session.getEventListenerManager().addListener( listener );
 			}
 
 			return session;
 		}
 
 		@Override
 		public SessionBuilder owner(SessionOwner sessionOwner) {
 			this.sessionOwner = sessionOwner;
 			return this;
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
 		public SessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 
 		@Override
-		public SessionBuilder eventListeners(SessionEventsListener... listeners) {
+		public SessionBuilder eventListeners(SessionEventListener... listeners) {
 			Collections.addAll( this.listeners, listeners );
 			return this;
 		}
 
 		@Override
 		public SessionBuilder clearEventListeners() {
 			listeners.clear();
 			return this;
 		}
 	}
 
 	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Connection connection;
 		private String tenantIdentifier;
 
 		public StatelessSessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
 
 			if ( sessionFactory.getCurrentTenantIdentifierResolver() != null ) {
 				tenantIdentifier = sessionFactory.getCurrentTenantIdentifierResolver().resolveCurrentTenantIdentifier();
 			}
 		}
 
 		@Override
 		public StatelessSession openStatelessSession() {
 			return new StatelessSessionImpl( connection, tenantIdentifier, sessionFactory );
 		}
 
 		@Override
 		public StatelessSessionBuilder connection(Connection connection) {
 			this.connection = connection;
 			return this;
 		}
 
 		@Override
 		public StatelessSessionBuilder tenantIdentifier(String tenantIdentifier) {
 			this.tenantIdentifier = tenantIdentifier;
 			return this;
 		}
 	}
 
 	@Override
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
 		return customEntityDirtinessStrategy;
 	}
 
 	@Override
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
 		return currentTenantIdentifierResolver;
 	}
 
 
 	// Serialization handling ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly serialized
 	 *
 	 * @param out The stream into which the object is being serialized.
 	 *
 	 * @throws IOException Can be thrown by the stream
 	 */
 	private void writeObject(ObjectOutputStream out) throws IOException {
 		LOG.debugf( "Serializing: %s", uuid );
 		out.defaultWriteObject();
 		LOG.trace( "Serialized" );
 	}
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized
 	 *
 	 * @param in The stream from which the object is being deserialized.
 	 *
 	 * @throws IOException Can be thrown by the stream
 	 * @throws ClassNotFoundException Again, can be thrown by the stream
 	 */
 	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
 		LOG.trace( "Deserializing" );
 		in.defaultReadObject();
 		LOG.debugf( "Deserialized: %s", uuid );
 	}
 
 	/**
 	 * Custom serialization hook defined by Java spec.  Used when the factory is directly deserialized.
 	 * Here we resolve the uuid/name read from the stream previously to resolve the SessionFactory
 	 * instance to use based on the registrations with the {@link SessionFactoryRegistry}
 	 *
 	 * @return The resolved factory to use.
 	 *
 	 * @throws InvalidObjectException Thrown if we could not resolve the factory by uuid/name.
 	 */
 	private Object readResolve() throws InvalidObjectException {
 		LOG.trace( "Resolving serialized SessionFactory" );
 		return locateSessionFactoryOnDeserialization( uuid, name );
 	}
 
 	private static SessionFactory locateSessionFactoryOnDeserialization(String uuid, String name) throws InvalidObjectException{
 		final SessionFactory uuidResult = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 		if ( uuidResult != null ) {
 			LOG.debugf( "Resolved SessionFactory by UUID [%s]", uuid );
 			return uuidResult;
 		}
 
 		// in case we were deserialized in a different JVM, look for an instance with the same name
 		// (provided we were given a name)
 		if ( name != null ) {
 			final SessionFactory namedResult = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
 			if ( namedResult != null ) {
 				LOG.debugf( "Resolved SessionFactory by name [%s]", name );
 				return namedResult;
 			}
 		}
 
 		throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
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
 		LOG.trace( "Deserializing SessionFactory from Session" );
 		final String uuid = ois.readUTF();
 		boolean isNamed = ois.readBoolean();
 		final String name = isNamed ? ois.readUTF() : null;
 		return (SessionFactoryImpl) locateSessionFactoryOnDeserialization( uuid, name );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
index b0a17d169a..422579486f 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
@@ -1,2829 +1,2825 @@
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
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import javax.persistence.EntityNotFoundException;
 
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
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.ReplicationMode;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
-import org.hibernate.SessionEventsListener;
+import org.hibernate.SessionEventListener;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionBuilder;
 import org.hibernate.SimpleNaturalIdLoadAccess;
 import org.hibernate.Transaction;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.criterion.NaturalIdentifier;
-import org.hibernate.engine.internal.SessionEventsManagerImpl;
+import org.hibernate.engine.internal.SessionEventListenerManagerImpl;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.NonContextualLobCreator;
 import org.hibernate.engine.query.spi.FilterQueryPlan;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.ActionQueue;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.NonFlushedChanges;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.engine.transaction.spi.TransactionObserver;
 import org.hibernate.event.service.spi.EventListenerGroup;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.AutoFlushEvent;
 import org.hibernate.event.spi.AutoFlushEventListener;
 import org.hibernate.event.spi.ClearEvent;
 import org.hibernate.event.spi.ClearEventListener;
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
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.stat.SessionStatistics;
 import org.hibernate.stat.internal.SessionStatisticsImpl;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
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
  * @author Brett Meyer
  */
 public final class SessionImpl extends AbstractSessionImpl implements EventSource {
 
 	// todo : need to find a clean way to handle the "event source" role
 	// a separate class responsible for generating/dispatching events just duplicates most of the Session methods...
 	// passing around separate interceptor, factory, actionQueue, and persistentContext is not manageable...
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionImpl.class.getName());
 
    private static final boolean tracing = LOG.isTraceEnabled();
 
 	private transient long timestamp;
 
 	private transient SessionOwner sessionOwner;
 
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
 	private transient TransactionObserver transactionObserver;
 
+	private SessionEventListenerManagerImpl sessionEventsManager = new SessionEventListenerManagerImpl();
+
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
 			final SessionOwner sessionOwner,
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
 		this.sessionOwner = sessionOwner;
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
 			transactionObserver = new TransactionObserver() {
 				@Override
 				public void afterBegin(TransactionImplementor transaction) {
 				}
 
 				@Override
 				public void beforeCompletion(TransactionImplementor transaction) {
 					if ( isOpen() && flushBeforeCompletionEnabled ) {
 						SessionImpl.this.managedFlush();
 					}
 					beforeTransactionCompletion( transaction );
 				}
 
 				@Override
 				public void afterCompletion(boolean successful, TransactionImplementor transaction) {
 					afterTransactionCompletion( transaction, successful );
 					if ( isOpen() && autoCloseSessionEnabled ) {
 						managedClose();
 					}
 					transactionCoordinator.removeObserver( this );
 				}
 			};
 
 			transactionCoordinator.addObserver( transactionObserver );
 		}
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
 		if (factory.getStatistics().isStatisticsEnabled()) {
 			factory.getStatisticsImplementor().openSession();
 		}
 
       if (tracing)
 		   LOG.tracef( "Opened session at timestamp: %s", timestamp );
 	}
 
 	@Override
 	public SharedSessionBuilder sessionWithOptions() {
 		return new SharedSessionBuilderImpl( this );
 	}
 
 	public void clear() {
 		errorIfClosed();
 		// Do not call checkTransactionSynchStatus() here -- if a delayed
 		// afterCompletion exists, it can cause an infinite loop.
 		pulseTransactionCoordinator();
 		internalClear();
 	}
 
 	private void internalClear() {
 		persistenceContext.clear();
 		actionQueue.clear();
 
 		final ClearEvent event = new ClearEvent( this );
 		for ( ClearEventListener listener : listeners( EventType.CLEAR ) ) {
 			listener.onClear( event );
 		}
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
-		getSessionEventsManager().end();
+		getEventListenerManager().end();
 
 		try {
 			if ( !isTransactionCoordinatorShared ) {
 				return transactionCoordinator.close();
 			}
 			else {
 				if ( getActionQueue().hasAfterTransactionActions() ){
 					LOG.warn( "On close, shared Session had after transaction actions that have not yet been processed" );
 				}
 				else {
 					transactionCoordinator.removeObserver( transactionObserver );
 				}
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
 
 	@Override
 	public boolean shouldAutoClose() {
 		if ( isClosed() ) {
 			return false;
 		}
 		else if ( sessionOwner != null ) {
 			return sessionOwner.shouldAutoCloseSession();
 		}
 		else {
 			return isAutoCloseSessionEnabled();
 		}
 	}
 
 	public void managedClose() {
 		LOG.trace( "Automatically closing session" );
 		close();
 	}
 
 	public Connection connection() throws HibernateException {
 		errorIfClosed();
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getConnection();
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
 		transactionCoordinator.getJdbcCoordinator().releaseResources();
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
 
-		getSessionEventsManager().transactionCompletion( successful );
+		getEventListenerManager().transactionCompletion( successful );
 
 		try {
 			interceptor.afterTransactionCompletion( hibernateTransaction );
 		}
 		catch (Throwable t) {
 			LOG.exceptionInAfterTransactionCompletionInterceptor( t );
 		}
 
 		if ( autoClear ) {
 			internalClear();
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
 
-	private SessionEventsManagerImpl sessionEventsManager;
-
 	@Override
-	public SessionEventsManagerImpl getSessionEventsManager() {
-		if ( sessionEventsManager == null ) {
-			sessionEventsManager = new SessionEventsManagerImpl();
-		}
-
+	public SessionEventListenerManagerImpl getEventListenerManager() {
 		return sessionEventsManager;
 	}
 
 	@Override
-	public void addEventsListeners(SessionEventsListener... listeners) {
-		getSessionEventsManager().addListener( listeners );
+	public void addEventListeners(SessionEventListener... listeners) {
+		getEventListenerManager().addListener( listeners );
 	}
 
 	@Override
 	public void startPrepareStatement() {
-		getSessionEventsManager().jdbcPrepareStatementStart();
+		getEventListenerManager().jdbcPrepareStatementStart();
 	}
 
 	@Override
 	public void endPrepareStatement() {
-		getSessionEventsManager().jdbcPrepareStatementEnd();
+		getEventListenerManager().jdbcPrepareStatementEnd();
 	}
 
 	@Override
 	public void startStatementExecution() {
-		getSessionEventsManager().jdbcExecuteStatementStart();
+		getEventListenerManager().jdbcExecuteStatementStart();
 	}
 
 	@Override
 	public void endStatementExecution() {
-		getSessionEventsManager().jdbcExecuteStatementEnd();
+		getEventListenerManager().jdbcExecuteStatementEnd();
 	}
 
 	@Override
 	public void startBatchExecution() {
-		getSessionEventsManager().jdbcExecuteBatchStart();
+		getEventListenerManager().jdbcExecuteBatchStart();
 	}
 
 	@Override
 	public void endBatchExecution() {
-		getSessionEventsManager().jdbcExecuteBatchEnd();
+		getEventListenerManager().jdbcExecuteBatchEnd();
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
 		delayedAfterCompletion();
 	}
 	
 	private void delayedAfterCompletion() {
 		transactionCoordinator.getSynchronizationCallbackCoordinator().delayedAfterCompletion();
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
 		delayedAfterCompletion();
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
 		delayedAfterCompletion();
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
 		delayedAfterCompletion();
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
 		delayedAfterCompletion();
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
 	
 	// TODO: The removeOrphan concept is a temporary "hack" for HHH-6484.  This should be removed once action/task
 	// ordering is improved.
 	public void removeOrphanBeforeUpdates(String entityName, Object child) {
 		fireDelete( new DeleteEvent( entityName, child, false, true, this ) );
 	}
 
 	private void fireDelete(DeleteEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void fireDelete(DeleteEvent event, Set transientEntities) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event, transientEntities );
 		}
 		delayedAfterCompletion();
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
 		delayedAfterCompletion();
 	}
 
 	private void fireResolveNaturalId(ResolveNaturalIdEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( ResolveNaturalIdEventListener listener : listeners( EventType.RESOLVE_NATURAL_ID ) ) {
 			listener.onResolveNaturalId( event );
 		}
 		delayedAfterCompletion();
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
 
 	public void refresh(String entityName, Object object, Map refreshedAlready) throws HibernateException {
 		fireRefresh( refreshedAlready, new RefreshEvent( entityName, object, this ) );
 	}
 
 	private void fireRefresh(RefreshEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
 			listener.onRefresh( event );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void fireRefresh(Map refreshedAlready, RefreshEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
 			listener.onRefresh( event, refreshedAlready );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// replicate() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void replicate(Object obj, ReplicationMode replicationMode) throws HibernateException {
 		fireReplicate( new ReplicateEvent( obj, replicationMode, this ) );
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
 		delayedAfterCompletion();
 	}
 
 
 	// evict() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * remove any hard references to the entity that are held by the infrastructure
 	 * (references held by application or other persistent instances are okay)
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
 		delayedAfterCompletion();
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
 		LOG.debug( "Checking session dirtiness" );
 		if ( actionQueue.areInsertionsOrDeletionsQueued() ) {
 			LOG.debug( "Session dirty (scheduled updates and insertions)" );
 			return true;
 		}
 		DirtyCheckEvent event = new DirtyCheckEvent( this );
 		for ( DirtyCheckEventListener listener : listeners( EventType.DIRTY_CHECK ) ) {
 			listener.onDirtyCheck( event );
 		}
 		delayedAfterCompletion();
 		return event.isDirty();
 	}
 
 	public void flush() throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( persistenceContext.getCascadeLevel() > 0 ) {
 			throw new HibernateException("Flush during cascade is dangerous");
 		}
 		FlushEvent flushEvent = new FlushEvent( this );
 		for ( FlushEventListener listener : listeners( EventType.FLUSH ) ) {
 			listener.onFlush( flushEvent );
 		}
 		delayedAfterCompletion();
 	}
 
 	public void forceFlush(EntityEntry entityEntry) throws HibernateException {
 		errorIfClosed();
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Flushing to force deletion of re-saved object: %s",
 					MessageHelper.infoString( entityEntry.getPersister(), entityEntry.getId(), getFactory() ) );
 		}
 
 		if ( persistenceContext.getCascadeLevel() > 0 ) {
 			throw new ObjectDeletedException(
 				"deleted object would be re-saved by cascade (remove deleted object from associations)",
 				entityEntry.getId(),
 				entityEntry.getPersister().getEntityName()
 			);
 		}
 
 		flush();
 	}
 
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		
 		HQLQueryPlan plan = queryParameters.getQueryPlan();
 		if (plan == null) {
 			plan = getHQLQueryPlan( query, false );
 		}
 		
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		List results = Collections.EMPTY_LIST;
 		boolean success = false;
 
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 			delayedAfterCompletion();
 		}
 		return results;
 	}
 
 	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation(success);
 			delayedAfterCompletion();
 		}
 		return result;
 	}
 
     public int executeNativeUpdate(NativeSQLQuerySpecification nativeQuerySpecification,
             QueryParameters queryParameters) throws HibernateException {
         errorIfClosed();
         checkTransactionSynchStatus();
         queryParameters.validateParameters();
         NativeSQLQueryPlan plan = getNativeSQLQueryPlan( nativeQuerySpecification );
 
 
         autoFlushIfRequired( plan.getCustomQuery().getQuerySpaces() );
 
         boolean success = false;
         int result = 0;
         try {
             result = plan.performExecuteUpdate(queryParameters, this);
             success = true;
         } finally {
             afterOperation(success);
     		delayedAfterCompletion();
         }
         return result;
     }
 
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, true );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
 		try {
 			return plan.performIterate( queryParameters, this );
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return plan.performScroll( queryParameters, this );
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
 	public Query createFilter(Object collection, String queryString) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		CollectionFilterImpl filter = new CollectionFilterImpl(
 				queryString,
 		        collection,
 		        this,
 		        getFilterQueryPlan( collection, queryString, null, false ).getParameterMetadata()
 		);
 		filter.setComment( queryString );
 		delayedAfterCompletion();
 		return filter;
 	}
 
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		Query query = super.getNamedQuery( queryName );
 		delayedAfterCompletion();
 		return query;
 	}
 
 	public Object instantiate(String entityName, Serializable id) throws HibernateException {
 		return instantiate( factory.getEntityPersister( entityName ), id );
 	}
 
 	/**
 	 * give the interceptor an opportunity to override the default instantiation
 	 */
 	public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		Object result = interceptor.instantiate( persister.getEntityName(), persister.getEntityMetamodel().getEntityMode(), id );
 		if ( result == null ) {
 			result = persister.instantiate( id, this );
 		}
 		delayedAfterCompletion();
 		return result;
 	}
 
 	public void setFlushMode(FlushMode flushMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		LOG.tracev( "Setting flush mode to: {0}", flushMode );
 		this.flushMode = flushMode;
 	}
 
 	public FlushMode getFlushMode() {
 		checkTransactionSynchStatus();
 		return flushMode;
 	}
 
 	public CacheMode getCacheMode() {
 		checkTransactionSynchStatus();
 		return cacheMode;
 	}
 
 	public void setCacheMode(CacheMode cacheMode) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		LOG.tracev( "Setting cache mode to: {0}", cacheMode );
 		this.cacheMode= cacheMode;
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
 
 	public EntityPersister getEntityPersister(final String entityName, final Object object) {
 		errorIfClosed();
 		if (entityName==null) {
 			return factory.getEntityPersister( guessEntityName( object ) );
 		}
 		else {
 			// try block is a hack around fact that currently tuplizers are not
 			// given the opportunity to resolve a subclass entity name.  this
 			// allows the (we assume custom) interceptor the ability to
 			// influence this decision if we were not able to based on the
 			// given entityName
 			try {
 				return factory.getEntityPersister( entityName ).getSubclassEntityPersister( object, getFactory() );
 			}
 			catch( HibernateException e ) {
 				try {
 					return getEntityPersister( null, object );
 				}
 				catch( HibernateException e2 ) {
 					throw e;
 				}
 			}
 		}
 	}
 
 	// not for internal use:
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		if ( object instanceof HibernateProxy ) {
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			if ( li.getSession() != this ) {
 				throw new TransientObjectException( "The proxy was not associated with this session" );
 			}
 			return li.getIdentifier();
 		}
 		else {
 			EntityEntry entry = persistenceContext.getEntry(object);
 			if ( entry == null ) {
 				throw new TransientObjectException( "The instance was not associated with this session" );
 			}
 			return entry.getId();
 		}
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
 		List results = Collections.EMPTY_LIST;
 
 		boolean success = false;
 		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation(success);
 			delayedAfterCompletion();
 		}
 		return results;
 	}
 
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, true );
 		Iterator itr = plan.performIterate( queryParameters, this );
 		delayedAfterCompletion();
 		return itr;
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
 
 	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 		
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String entityName = criteriaImpl.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable(entityName),
 				factory,
 				criteriaImpl,
 				entityName,
 				getLoadQueryInfluencers()
 		);
 		autoFlushIfRequired( loader.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return loader.scroll(this, scrollMode);
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
 	public List list(Criteria criteria) throws HibernateException {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 				
 		final NaturalIdLoadAccess naturalIdLoadAccess = this.tryNaturalIdLoadAccess( criteriaImpl );
 		if ( naturalIdLoadAccess != null ) {
 			// EARLY EXIT!
 			return Arrays.asList( naturalIdLoadAccess.load() );
 		}
 
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		String[] implementors = factory.getImplementors( criteriaImpl.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		Set spaces = new HashSet();
 		for( int i=0; i <size; i++ ) {
 
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 					factory,
 					criteriaImpl,
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
 			delayedAfterCompletion();
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
 		delayedAfterCompletion();
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
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName, resultSetMappings );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName, resultClasses );
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
 			delayedAfterCompletion();
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
 			delayedAfterCompletion();
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
 		delayedAfterCompletion();
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
 		pulseTransactionCoordinator();
 		delayedAfterCompletion();
 	}
 
 	private void pulseTransactionCoordinator() {
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
 		sessionOwner = ( SessionOwner ) ois.readObject();
 
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
 		if ( ! transactionCoordinator.getJdbcCoordinator().isReadyForSerialization() ) {
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
 		oos.writeObject( sessionOwner );
 
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
 			// Always use NonContextualLobCreator.  If ContextualLobCreator is
 			// used both here and in WrapperOptions, 
 			return NonContextualLobCreator.INSTANCE;
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
 			super.owner( session.sessionOwner );
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
 
 		@Override
-		public SharedSessionBuilder eventListeners(SessionEventsListener... listeners) {
+		public SharedSessionBuilder eventListeners(SessionEventListener... listeners) {
 			super.eventListeners( listeners );
 			return this;
 		}
 
 		@Override
 		public SessionBuilder clearEventListeners() {
 			super.clearEventListeners();
 			return this;
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
 		private boolean synchronizationEnabled = true;
 
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
 
 		protected void synchronizationEnabled(boolean synchronizationEnabled) {
 			this.synchronizationEnabled = synchronizationEnabled;
 		}
 
 		protected final Serializable resolveNaturalId(Map<String, Object> naturalIdParameters) {
 			performAnyNeededCrossReferenceSynchronizations();
 
 			final ResolveNaturalIdEvent event =
 					new ResolveNaturalIdEvent( naturalIdParameters, entityPersister, SessionImpl.this );
 			fireResolveNaturalId( event );
 
 			if ( event.getEntityId() == PersistenceContext.NaturalIdHelper.INVALID_NATURAL_ID_REFERENCE ) {
 				return null;
 			}
 			else {
 				return event.getEntityId();
 			}
 		}
 
 		protected void performAnyNeededCrossReferenceSynchronizations() {
 			if ( ! synchronizationEnabled ) {
 				// synchronization (this process) was disabled
 				return;
 			}
 			if ( entityPersister.getEntityMetamodel().hasImmutableNaturalId() ) {
 				// only mutable natural-ids need this processing
 				return;
 			}
 			if ( ! isTransactionInProgress() ) {
 				// not in a transaction so skip synchronization
 				return;
 			}
 
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			for ( Serializable pk : getPersistenceContext().getNaturalIdHelper().getCachedPkResolutions( entityPersister ) ) {
 				final EntityKey entityKey = generateEntityKey( pk, entityPersister );
 				final Object entity = getPersistenceContext().getEntity( entityKey );
 				final EntityEntry entry = getPersistenceContext().getEntry( entity );
 
 				if ( entry == null ) {
 					if ( debugEnabled ) {
 						LOG.debug(
 								"Cached natural-id/pk resolution linked to null EntityEntry in persistence context : "
 										+ MessageHelper.infoString( entityPersister, pk, getFactory() )
 						);
 					}
 					continue;
 				}
 
 				if ( !entry.requiresDirtyCheck( entity ) ) {
 					continue;
 				}
 
 				// MANAGED is the only status we care about here...
 				if ( entry.getStatus() != Status.MANAGED ) {
 					continue;
 				}
 
 				getPersistenceContext().getNaturalIdHelper().handleSynchronization(
 						entityPersister,
 						pk,
 						entity
 				);
 			}
 		}
 
 		protected final IdentifierLoadAccess getIdentifierLoadAccess() {
 			final IdentifierLoadAccessImpl identifierLoadAccess = new IdentifierLoadAccessImpl( entityPersister );
 			if ( this.lockOptions != null ) {
 				identifierLoadAccess.with( lockOptions );
 			}
 			return identifierLoadAccess;
 		}
 
 		protected EntityPersister entityPersister() {
 			return entityPersister;
 		}
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
 		public NaturalIdLoadAccessImpl setSynchronizationEnabled(boolean synchronizationEnabled) {
 			super.synchronizationEnabled( synchronizationEnabled );
 			return this;
 		}
 
 		@Override
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
 			try {
 				return this.getIdentifierLoadAccess().load( entityId );
 			}
 			catch (EntityNotFoundException enf) {
 				// OK
 			}
 			catch (ObjectNotFoundException nf) {
 				// OK
 			}
 			return null;
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
 		public SimpleNaturalIdLoadAccessImpl setSynchronizationEnabled(boolean synchronizationEnabled) {
 			super.synchronizationEnabled( synchronizationEnabled );
 			return this;
 		}
 
 		@Override
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
 			try {
 				return this.getIdentifierLoadAccess().load( entityId );
 			}
 			catch (EntityNotFoundException enf) {
 				// OK
 			}
 			catch (ObjectNotFoundException nf) {
 				// OK
 			}
 			return null;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
index 250ac364f2..602ca75301 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
@@ -1,804 +1,804 @@
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
 import org.hibernate.collection.spi.PersistentCollection;
-import org.hibernate.engine.internal.SessionEventsManagerImpl;
+import org.hibernate.engine.internal.SessionEventListenerManagerImpl;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.NonFlushedChanges;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
-import org.hibernate.engine.spi.SessionEventsManager;
+import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.loader.criteria.CriteriaLoader;
 import org.hibernate.loader.custom.CustomLoader;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.type.Type;
 import org.jboss.logging.Logger;
 
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
 
 	@Override
 	public Serializable insert(Object entity) {
 		errorIfClosed();
 		return insert(null, entity);
 	}
 
 	@Override
 	public Serializable insert(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister( entityName, entity );
 		Serializable id = persister.getIdentifierGenerator().generate( this, entity );
 		Object[] state = persister.getPropertyValues( entity );
 		if ( persister.isVersioned() ) {
 			boolean substitute = Versioning.seedVersion(
 					state, persister.getVersionProperty(), persister.getVersionType(), this
 			);
 			if ( substitute ) {
 				persister.setPropertyValues( entity, state );
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
 
 	@Override
 	public void delete(Object entity) {
 		errorIfClosed();
 		delete(null, entity);
 	}
 
 	@Override
 	public void delete(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister(entityName, entity);
 		Serializable id = persister.getIdentifier( entity, this );
 		Object version = persister.getVersion( entity );
 		persister.delete(id, version, entity, this);
 	}
 
 
 	// updates ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void update(Object entity) {
 		errorIfClosed();
 		update(null, entity);
 	}
 
 	@Override
 	public void update(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister(entityName, entity);
 		Serializable id = persister.getIdentifier( entity, this );
 		Object[] state = persister.getPropertyValues( entity );
 		Object oldVersion;
 		if ( persister.isVersioned() ) {
 			oldVersion = persister.getVersion( entity );
 			Object newVersion = Versioning.increment( oldVersion, persister.getVersionType(), this );
 			Versioning.setVersion(state, newVersion, persister);
 			persister.setPropertyValues( entity, state );
 		}
 		else {
 			oldVersion = null;
 		}
 		persister.update(id, state, null, false, null, oldVersion, entity, null, this);
 	}
 
 
 	// loading ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Object get(Class entityClass, Serializable id) {
 		return get( entityClass.getName(), id );
 	}
 
 	@Override
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) {
 		return get( entityClass.getName(), id, lockMode );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id) {
 		return get(entityName, id, LockMode.NONE);
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockMode lockMode) {
 		errorIfClosed();
 		Object result = getFactory().getEntityPersister(entityName)
 				.load(id, null, lockMode, this);
 		if ( temporaryPersistenceContext.isLoadFinished() ) {
 			temporaryPersistenceContext.clear();
 		}
 		return result;
 	}
 
 	@Override
 	public void refresh(Object entity) {
 		refresh( bestGuessEntityName( entity ), entity, LockMode.NONE );
 	}
 
 	@Override
 	public void refresh(String entityName, Object entity) {
 		refresh( entityName, entity, LockMode.NONE );
 	}
 
 	@Override
 	public void refresh(Object entity, LockMode lockMode) {
 		refresh( bestGuessEntityName( entity ), entity, lockMode );
 	}
 
 	@Override
 	public void refresh(String entityName, Object entity, LockMode lockMode) {
 		final EntityPersister persister = this.getEntityPersister( entityName, entity );
 		final Serializable id = persister.getIdentifier( entity, this );
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Refreshing transient {0}", MessageHelper.infoString( persister, id, this.getFactory() ) );
 		}
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
 
 	@Override
 	public Object immediateLoad(String entityName, Serializable id)
 			throws HibernateException {
 		throw new SessionException("proxies cannot be fetched by a stateless session");
 	}
 
 	@Override
 	public void initializeCollection(
 			PersistentCollection collection,
 	        boolean writing) throws HibernateException {
 		throw new SessionException("collections cannot be fetched by a stateless session");
 	}
 
 	@Override
 	public Object instantiate(
 			String entityName,
 	        Serializable id) throws HibernateException {
 		errorIfClosed();
 		return getFactory().getEntityPersister( entityName ).instantiate( id, this );
 	}
 
 	@Override
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
 
 	@Override
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean isOpen() {
 		return !isClosed();
 	}
 
 	@Override
 	public void close() {
 		managedClose();
 	}
 
 	@Override
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return factory.getSettings().getConnectionReleaseMode();
 	}
 
 	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return true;
 	}
 
 	@Override
 	public boolean isAutoCloseSessionEnabled() {
 		return factory.getSettings().isAutoCloseSessionEnabled();
 	}
 
 	@Override
 	public boolean isFlushBeforeCompletionEnabled() {
 		return true;
 	}
 
 	@Override
 	public boolean isFlushModeNever() {
 		return false;
 	}
 
 	@Override
 	public void managedClose() {
 		if ( isClosed() ) {
 			throw new SessionException( "Session was already closed!" );
 		}
 		transactionCoordinator.close();
 		setClosed();
 	}
 
 	@Override
 	public void managedFlush() {
 		errorIfClosed();
 		getTransactionCoordinator().getJdbcCoordinator().executeBatch();
 	}
 
 	@Override
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
 
 	@Override
 	public String onPrepareStatement(String sql) {
 		return sql;
 	}
 
-	private SessionEventsManagerImpl sessionEventsManager;
+	private SessionEventListenerManagerImpl sessionEventsManager;
 
 	@Override
-	public SessionEventsManager getSessionEventsManager() {
+	public SessionEventListenerManager getEventListenerManager() {
 		if ( sessionEventsManager == null ) {
-			sessionEventsManager = new SessionEventsManagerImpl();
+			sessionEventsManager = new SessionEventListenerManagerImpl();
 		}
 		return sessionEventsManager;
 	}
 
 	@Override
 	public void startPrepareStatement() {
 	}
 
 	@Override
 	public void endPrepareStatement() {
 	}
 
 	@Override
 	public void startStatementExecution() {
 	}
 
 	@Override
 	public void endStatementExecution() {
 	}
 
 	@Override
 	public void startBatchExecution() {
 	}
 
 	@Override
 	public void endBatchExecution() {
 	}
 
 	@Override
 	public String bestGuessEntityName(Object object) {
 		if (object instanceof HibernateProxy) {
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation();
 		}
 		return guessEntityName(object);
 	}
 
 	@Override
 	public Connection connection() {
 		errorIfClosed();
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getConnection();
 	}
 
 	@Override
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
 
 	@Override
 	public CacheMode getCacheMode() {
 		return CacheMode.IGNORE;
 	}
 
 	@Override
 	public int getDontFlushFromFind() {
 		return 0;
 	}
 
 	@Override
 	public Map getEnabledFilters() {
 		return Collections.EMPTY_MAP;
 	}
 
 	@Override
 	public Serializable getContextEntityIdentifier(Object object) {
 		errorIfClosed();
 		return null;
 	}
 
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(String entityName, Object object)
 			throws HibernateException {
 		errorIfClosed();
 		if ( entityName==null ) {
 			return factory.getEntityPersister( guessEntityName( object ) );
 		}
 		else {
 			return factory.getEntityPersister( entityName ).getSubclassEntityPersister( object, getFactory() );
 		}
 	}
 
 	@Override
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		errorIfClosed();
 		return null;
 	}
 
 	@Override
 	public Type getFilterParameterType(String filterParameterName) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public Object getFilterParameterValue(String filterParameterName) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public FlushMode getFlushMode() {
 		return FlushMode.COMMIT;
 	}
 
 	@Override
 	public Interceptor getInterceptor() {
 		return EmptyInterceptor.INSTANCE;
 	}
 
 	@Override
 	public PersistenceContext getPersistenceContext() {
 		return temporaryPersistenceContext;
 	}
 
 	@Override
 	public long getTimestamp() {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public String guessEntityName(Object entity) throws HibernateException {
 		errorIfClosed();
 		return entity.getClass().getName();
 	}
 
 	@Override
 	public boolean isConnected() {
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().isPhysicallyConnected();
 	}
 
 	@Override
 	public boolean isTransactionInProgress() {
 		return transactionCoordinator.isTransactionInProgress();
 	}
 
 	@Override
 	public void setAutoClear(boolean enabled) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public void disableTransactionAutoJoin() {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public void setCacheMode(CacheMode cm) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public void setFlushMode(FlushMode fm) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public Transaction getTransaction() throws HibernateException {
 		errorIfClosed();
 		return transactionCoordinator.getTransaction();
 	}
 
 	@Override
 	public Transaction beginTransaction() throws HibernateException {
 		errorIfClosed();
 		Transaction result = getTransaction();
 		result.begin();
 		return result;
 	}
 
 	@Override
 	public boolean isEventSource() {
 		return false;
 	}
 
 	public boolean isDefaultReadOnly() {
 		return false;
 	}
 
 	public void setDefaultReadOnly(boolean readOnly) throws HibernateException {
 		if ( readOnly ) {
 			throw new UnsupportedOperationException();
 		}
 	}
 
 /////////////////////////////////////////////////////////////////////////////////////////////////////
 
 	//TODO: COPY/PASTE FROM SessionImpl, pull up!
 
 	@Override
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		boolean success = false;
 		List results = Collections.EMPTY_LIST;
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
 			transactionCoordinator.afterNonTransactionalQuery( success );
 		}
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		errorIfClosed();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName, String alias) {
 		errorIfClosed();
 		return new CriteriaImpl(entityName, alias, this);
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass) {
 		errorIfClosed();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName) {
 		errorIfClosed();
 		return new CriteriaImpl(entityName, this);
 	}
 
 	@Override
 	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 		
 		errorIfClosed();
 		String entityName = criteriaImpl.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable( entityName ),
 		        factory,
 		        criteriaImpl,
 		        entityName,
 		        getLoadQueryInfluencers()
 		);
 		return loader.scroll(this, scrollMode);
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public List list(Criteria criteria) throws HibernateException {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 		
 		errorIfClosed();
 		String[] implementors = factory.getImplementors( criteriaImpl.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		for( int i=0; i <size; i++ ) {
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 			        factory,
 			        criteriaImpl,
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
 
 	@Override
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
 
 	@Override
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 		return loader.scroll( queryParameters, this );
 	}
 
 	@Override
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		return plan.performScroll( queryParameters, this );
 	}
 
 	@Override
 	public void afterScrollOperation() {
 		temporaryPersistenceContext.clear();
 	}
 
 	@Override
 	public void flush() {
 	}
 
 	@Override
 	public NonFlushedChanges getNonFlushedChanges() {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public String getFetchProfile() {
 		return null;
 	}
 
 	@Override
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return LoadQueryInfluencers.NONE;
 	}
 
 	@Override
 	public void setFetchProfile(String name) {
 	}
 
 	@Override
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
diff --git a/hibernate-core/src/test/resources/log4j.properties b/hibernate-core/src/test/resources/log4j.properties
index 22a8a7ce36..d406433daa 100644
--- a/hibernate-core/src/test/resources/log4j.properties
+++ b/hibernate-core/src/test/resources/log4j.properties
@@ -1,59 +1,59 @@
 #
 # Hibernate, Relational Persistence for Idiomatic Java
 #
 # Copyright (c) 2013, Red Hat Inc. or third-party contributors as
 # indicated by the @author tags or express copyright attribution
 # statements applied by the authors.  All third-party contributions are
 # distributed under license by Red Hat Inc.
 #
 # This copyrighted material is made available to anyone wishing to use, modify,
 # copy, or redistribute it subject to the terms and conditions of the GNU
 # Lesser General Public License, as published by the Free Software Foundation.
 #
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 # or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 # for more details.
 #
 # You should have received a copy of the GNU Lesser General Public License
 # along with this distribution; if not, write to:
 # Free Software Foundation, Inc.
 # 51 Franklin Street, Fifth Floor
 # Boston, MA  02110-1301  USA
 #
 log4j.appender.stdout=org.apache.log4j.ConsoleAppender
 log4j.appender.stdout.Target=System.out
 log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
 log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n
 #log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L (hibernateLoadPlanWalkPath->%X{hibernateLoadPlanWalkPath}) - %m%n
 
 #log4j.appender.stdout-mdc=org.apache.log4j.ConsoleAppender
 #log4j.appender.stdout-mdc.Target=System.out
 #log4j.appender.stdout-mdc.layout=org.apache.log4j.PatternLayout
 #log4j.appender.stdout-mdc.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L (walk path -> %X{hibernateLoadPlanWalkPath}) - %m%n
 
 log4j.rootLogger=info, stdout
 
 #log4j.logger.org.hibernate.loader.plan=trace, stdout-mdc
 #log4j.additivity.org.hibernate.loader.plan=false
 #log4j.logger.org.hibernate.persister.walking=trace, stdout-mdc
 #log4j.additivity.org.hibernate.persister.walking=false
 
 log4j.logger.org.hibernate.tool.hbm2ddl=trace
 log4j.logger.org.hibernate.testing.cache=debug
 
 # SQL Logging - HHH-6833
 log4j.logger.org.hibernate.SQL=debug
 
 log4j.logger.org.hibernate.type.descriptor.sql.BasicBinder=trace
 log4j.logger.org.hibernate.type.descriptor.sql.BasicExtractor=trace
 
 log4j.logger.org.hibernate.hql.internal.ast=debug
 
 log4j.logger.org.hibernate.sql.ordering.antlr=debug
 
 log4j.logger.org.hibernate.loader.plan2.build.internal.LoadPlanImpl=debug
 log4j.logger.org.hibernate.loader.plan2.build.spi.LoadPlanTreePrinter=debug
 log4j.logger.org.hibernate.loader.plan2.exec.spi.EntityLoadQueryDetails=debug
 
-log4j.logger.org.hibernate.engine.internal.LoggingSessionEventsListener=info
\ No newline at end of file
+log4j.logger.org.hibernate.engine.internal.StatisticalLoggingSessionEventListener=info
\ No newline at end of file
