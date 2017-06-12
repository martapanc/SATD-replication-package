diff --git a/hibernate-core/src/main/java/org/hibernate/IdentifierLoadAccess.java b/hibernate-core/src/main/java/org/hibernate/IdentifierLoadAccess.java
index a6e7bb613d..26f4a80967 100644
--- a/hibernate-core/src/main/java/org/hibernate/IdentifierLoadAccess.java
+++ b/hibernate-core/src/main/java/org/hibernate/IdentifierLoadAccess.java
@@ -1,70 +1,74 @@
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
 package org.hibernate;
 
 import java.io.Serializable;
 
 /**
  * Loads an entity by its primary identifier
  * 
  * @author Eric Dalquist
  * @version $Revision$
  */
 public interface IdentifierLoadAccess<T> {
-    /**
-     * Set the {@link LockOptions} to use when retrieving the entity.
-     */
-    public IdentifierLoadAccess<T> with(LockOptions lockOptions);
+	/**
+	 * Set the {@link LockOptions} to use when retrieving the entity.
+	 */
+	public IdentifierLoadAccess<T> with(LockOptions lockOptions);
 
-    /**
-     * Return the persistent instance of the given entity class with the given identifier,
-     * assuming that the instance exists. This method might return a proxied instance that
-     * is initialized on-demand, when a non-identifier method is accessed.
-     * <br><br>
-     * You should not use this method to determine if an instance exists (use <tt>get()</tt>
-     * instead). Use this only to retrieve an instance that you assume exists, where non-existence
-     * would be an actual error.
-     * <br><br>
-     * Due to the nature of the proxy functionality the return type of this method cannot use
-     * the generic type.
-     *
-     * @param theClass a persistent class
-     * @param id a valid identifier of an existing persistent instance of the class
-     * @return the persistent instance or proxy
-     * @throws HibernateException
-     */
-    public Object getReference(Serializable id);
+	/**
+	 * Return the persistent instance of the given entity class with the given identifier,
+	 * assuming that the instance exists. This method might return a proxied instance that
+	 * is initialized on-demand, when a non-identifier method is accessed. <br>
+	 * <br>
+	 * You should not use this method to determine if an instance exists (use <tt>get()</tt> instead). Use this only to
+	 * retrieve an instance that you assume exists, where non-existence
+	 * would be an actual error. <br>
+	 * <br>
+	 * Due to the nature of the proxy functionality the return type of this method cannot use
+	 * the generic type.
+	 * 
+	 * @param theClass
+	 *            a persistent class
+	 * @param id
+	 *            a valid identifier of an existing persistent instance of the class
+	 * @return the persistent instance or proxy
+	 * @throws HibernateException
+	 */
+	public Object getReference(Serializable id);
 
-    /**
-     * Return the persistent instance of the given entity class with the given identifier,
-     * or null if there is no such persistent instance. (If the instance is already associated
-     * with the session, return that instance. This method never returns an uninitialized instance.)
-     *
-     * @param clazz a persistent class
-     * @param id an identifier
-     * @return a persistent instance or null
-     * @throws HibernateException
-     */
-    public Object load(Serializable id);
+	/**
+	 * Return the persistent instance of the given entity class with the given identifier,
+	 * or null if there is no such persistent instance. (If the instance is already associated
+	 * with the session, return that instance. This method never returns an uninitialized instance.)
+	 * 
+	 * @param clazz
+	 *            a persistent class
+	 * @param id
+	 *            an identifier
+	 * @return a persistent instance or null
+	 * @throws HibernateException
+	 */
+	public Object load(Serializable id);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/NaturalIdLoadAccess.java b/hibernate-core/src/main/java/org/hibernate/NaturalIdLoadAccess.java
index bba27abcd6..b175800a16 100644
--- a/hibernate-core/src/main/java/org/hibernate/NaturalIdLoadAccess.java
+++ b/hibernate-core/src/main/java/org/hibernate/NaturalIdLoadAccess.java
@@ -1,63 +1,64 @@
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
 package org.hibernate;
 
-
 /**
  * Loads an entity by its natural identifier
  * 
  * @author Eric Dalquist
- * @version $Revision$
  * @see org.hibernate.annotations.NaturalId
  */
 public interface NaturalIdLoadAccess<T> {
-    /**
-     * Set the {@link LockOptions} to use when retrieving the entity.
-     */
-    public NaturalIdLoadAccess<T> with(LockOptions lockOptions);
+	/**
+	 * Set the {@link LockOptions} to use when retrieving the entity.
+	 */
+	public NaturalIdLoadAccess<T> with(LockOptions lockOptions);
 
-    /**
-     * Add a NaturalId attribute value.
-     * 
-     * @param attributeName The entity attribute name that is marked as a NaturalId
-     * @param value The value of the attribute
-     */
-    public NaturalIdLoadAccess<T> using(String attributeName, Object value);
+	/**
+	 * Add a NaturalId attribute value.
+	 * 
+	 * @param attributeName
+	 *            The entity attribute name that is marked as a NaturalId
+	 * @param value
+	 *            The value of the attribute
+	 */
+	public NaturalIdLoadAccess<T> using(String attributeName, Object value);
 
-    /**
-     * Same behavior as {@link Session#load(Class, java.io.Serializable)}
-     * 
-     * @return The entity 
-     * @throws HibernateException if the entity does not exist
-     */
-    public Object getReference();
+	/**
+	 * Same behavior as {@link Session#load(Class, java.io.Serializable)}
+	 * 
+	 * @return The entity
+	 * @throws HibernateException
+	 *             if the entity does not exist
+	 */
+	public Object getReference();
 
-    /**
-     * Same behavior as {@link Session#get(Class, java.io.Serializable)}
-     * 
-     * @return The entity or null if it does not exist
-     */
-    public Object load();
+	/**
+	 * Same behavior as {@link Session#get(Class, java.io.Serializable)}
+	 * 
+	 * @return The entity or null if it does not exist
+	 */
+	public Object load();
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/Session.java b/hibernate-core/src/main/java/org/hibernate/Session.java
index ad50ed1e99..75166989f2 100644
--- a/hibernate-core/src/main/java/org/hibernate/Session.java
+++ b/hibernate-core/src/main/java/org/hibernate/Session.java
@@ -1,1031 +1,1039 @@
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
 package org.hibernate;
 
 import java.io.Serializable;
 import java.sql.Connection;
 
 import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.stat.SessionStatistics;
 
 /**
  * The main runtime interface between a Java application and Hibernate. This is the
  * central API class abstracting the notion of a persistence service.<br>
  * <br>
  * The lifecycle of a <tt>Session</tt> is bounded by the beginning and end of a logical
  * transaction. (Long transactions might span several database transactions.)<br>
  * <br>
  * The main function of the <tt>Session</tt> is to offer create, read and delete operations
  * for instances of mapped entity classes. Instances may exist in one of three states:<br>
  * <br>
  * <i>transient:</i> never persistent, not associated with any <tt>Session</tt><br>
  * <i>persistent:</i> associated with a unique <tt>Session</tt><br>
  * <i>detached:</i> previously persistent, not associated with any <tt>Session</tt><br>
  * <br>
  * Transient instances may be made persistent by calling <tt>save()</tt>,
  * <tt>persist()</tt> or <tt>saveOrUpdate()</tt>. Persistent instances may be made transient
  * by calling<tt> delete()</tt>. Any instance returned by a <tt>get()</tt> or
  * <tt>load()</tt> method is persistent. Detached instances may be made persistent
  * by calling <tt>update()</tt>, <tt>saveOrUpdate()</tt>, <tt>lock()</tt> or <tt>replicate()</tt>. 
  * The state of a transient or detached instance may also be made persistent as a new
  * persistent instance by calling <tt>merge()</tt>.<br>
  * <br>
  * <tt>save()</tt> and <tt>persist()</tt> result in an SQL <tt>INSERT</tt>, <tt>delete()</tt>
  * in an SQL <tt>DELETE</tt> and <tt>update()</tt> or <tt>merge()</tt> in an SQL <tt>UPDATE</tt>. 
  * Changes to <i>persistent</i> instances are detected at flush time and also result in an SQL
  * <tt>UPDATE</tt>. <tt>saveOrUpdate()</tt> and <tt>replicate()</tt> result in either an
  * <tt>INSERT</tt> or an <tt>UPDATE</tt>.<br>
  * <br>
  * It is not intended that implementors be threadsafe. Instead each thread/transaction
  * should obtain its own instance from a <tt>SessionFactory</tt>.<br>
  * <br>
  * A <tt>Session</tt> instance is serializable if its persistent classes are serializable.<br>
  * <br>
  * A typical transaction should use the following idiom:
  * <pre>
  * Session sess = factory.openSession();
  * Transaction tx;
  * try {
  *     tx = sess.beginTransaction();
  *     //do some work
  *     ...
  *     tx.commit();
  * }
  * catch (Exception e) {
  *     if (tx!=null) tx.rollback();
  *     throw e;
  * }
  * finally {
  *     sess.close();
  * }
  * </pre>
  * <br>
  * If the <tt>Session</tt> throws an exception, the transaction must be rolled back
  * and the session discarded. The internal state of the <tt>Session</tt> might not
  * be consistent with the database after the exception occurs.
  *
  * @see SessionFactory
  * @author Gavin King
  */
 public interface Session extends SharedSessionContract {
 	/**
 	 * Obtain a {@link Session} builder with the ability to grab certain information from this session.
 	 *
 	 * @return The session builder
 	 */
 	public SharedSessionBuilder sessionWithOptions();
 
 	/**
 	 * Force this session to flush. Must be called at the end of a
 	 * unit of work, before committing the transaction and closing the
 	 * session (depending on {@link #setFlushMode(FlushMode)},
 	 * {@link Transaction#commit()} calls this method).
 	 * <p/>
 	 * <i>Flushing</i> is the process of synchronizing the underlying persistent
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
 	public Serializable getIdentifier(Object object) throws HibernateException;
 
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
 	 * @param object a persistent instance
 	 * @throws HibernateException
 	 */
 	public void evict(Object object) throws HibernateException;
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param theClass a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockMode the lock level
 	 * @return the persistent instance or proxy
 	 * @throws HibernateException
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
 	public Object load(Class theClass, Serializable id, LockMode lockMode) throws HibernateException;
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param theClass a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockOptions contains the lock level
 	 * @return the persistent instance or proxy
 	 * @throws HibernateException
 	 */
 	public Object load(Class theClass, Serializable id, LockOptions lockOptions) throws HibernateException;
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param entityName a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockMode the lock level
 	 * @return the persistent instance or proxy
 	 * @throws HibernateException
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
 	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException;
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param entityName a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockOptions contains the lock level
 	 * @return the persistent instance or proxy
 	 * @throws HibernateException
 	 */
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException;
 
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
 	 * @return the persistent instance or proxy
 	 * @throws HibernateException
 	 */
 	public Object load(Class theClass, Serializable id) throws HibernateException;
 
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
 	 * @return the persistent instance or proxy
 	 * @throws HibernateException
 	 */
 	public Object load(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * Read the persistent state associated with the given identifier into the given transient
 	 * instance.
 	 *
 	 * @param object an "empty" instance of the persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @throws HibernateException
 	 */
 	public void load(Object object, Serializable id) throws HibernateException;
 
 	/**
 	 * Persist the state of the given detached instance, reusing the current
 	 * identifier value.  This operation cascades to associated instances if
 	 * the association is mapped with <tt>cascade="replicate"</tt>.
 	 *
 	 * @param object a detached instance of a persistent class
 	 */
 	public void replicate(Object object, ReplicationMode replicationMode) throws HibernateException;
 
 	/**
 	 * Persist the state of the given detached instance, reusing the current
 	 * identifier value.  This operation cascades to associated instances if
 	 * the association is mapped with <tt>cascade="replicate"</tt>.
 	 *
 	 * @param object a detached instance of a persistent class
 	 */
 	public void replicate(String entityName, Object object, ReplicationMode replicationMode) throws HibernateException;
 
 	/**
 	 * Persist the given transient instance, first assigning a generated identifier. (Or
 	 * using the current value of the identifier property if the <tt>assigned</tt>
 	 * generator is used.) This operation cascades to associated instances if the
 	 * association is mapped with <tt>cascade="save-update"</tt>.
 	 *
 	 * @param object a transient instance of a persistent class
 	 * @return the generated identifier
 	 * @throws HibernateException
 	 */
 	public Serializable save(Object object) throws HibernateException;
 
 	/**
 	 * Persist the given transient instance, first assigning a generated identifier. (Or
 	 * using the current value of the identifier property if the <tt>assigned</tt>
 	 * generator is used.)  This operation cascades to associated instances if the
 	 * association is mapped with <tt>cascade="save-update"</tt>.
 	 *
 	 * @param object a transient instance of a persistent class
 	 * @return the generated identifier
 	 * @throws HibernateException
 	 */
 	public Serializable save(String entityName, Object object) throws HibernateException;
 
 	/**
 	 * Either {@link #save(Object)} or {@link #update(Object)} the given
 	 * instance, depending upon resolution of the unsaved-value checks (see the
 	 * manual for discussion of unsaved-value checking).
 	 * <p/>
 	 * This operation cascades to associated instances if the association is mapped
 	 * with <tt>cascade="save-update"</tt>.
 	 *
 	 * @see Session#save(java.lang.Object)
 	 * @see Session#update(Object object)
 	 * @param object a transient or detached instance containing new or updated state
 	 * @throws HibernateException
 	 */
 	public void saveOrUpdate(Object object) throws HibernateException;
 
 	/**
 	 * Either {@link #save(String, Object)} or {@link #update(String, Object)}
 	 * the given instance, depending upon resolution of the unsaved-value checks
 	 * (see the manual for discussion of unsaved-value checking).
 	 * <p/>
 	 * This operation cascades to associated instances if the association is mapped
 	 * with <tt>cascade="save-update"</tt>.
 	 *
 	 * @see Session#save(String,Object)
 	 * @see Session#update(String,Object)
 	 * @param object a transient or detached instance containing new or updated state
 	 * @throws HibernateException
 	 */
 	public void saveOrUpdate(String entityName, Object object) throws HibernateException;
 
 	/**
 	 * Update the persistent instance with the identifier of the given detached
 	 * instance. If there is a persistent instance with the same identifier,
 	 * an exception is thrown. This operation cascades to associated instances
 	 * if the association is mapped with <tt>cascade="save-update"</tt>.
 	 *
 	 * @param object a detached instance containing updated state
 	 * @throws HibernateException
 	 */
 	public void update(Object object) throws HibernateException;
 
 	/**
 	 * Update the persistent instance with the identifier of the given detached
 	 * instance. If there is a persistent instance with the same identifier,
 	 * an exception is thrown. This operation cascades to associated instances
 	 * if the association is mapped with <tt>cascade="save-update"</tt>.
 	 *
 	 * @param object a detached instance containing updated state
 	 * @throws HibernateException
 	 */
 	public void update(String entityName, Object object) throws HibernateException;
 
 	/**
 	 * Copy the state of the given object onto the persistent object with the same
 	 * identifier. If there is no persistent instance currently associated with
 	 * the session, it will be loaded. Return the persistent instance. If the
 	 * given instance is unsaved, save a copy of and return it as a newly persistent
 	 * instance. The given instance does not become associated with the session.
 	 * This operation cascades to associated instances if the association is mapped
 	 * with <tt>cascade="merge"</tt>.<br>
 	 * <br>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param object a detached instance with state to be copied
 	 * @return an updated persistent instance
 	 */
 	public Object merge(Object object) throws HibernateException;
 
 	/**
 	 * Copy the state of the given object onto the persistent object with the same
 	 * identifier. If there is no persistent instance currently associated with
 	 * the session, it will be loaded. Return the persistent instance. If the
 	 * given instance is unsaved, save a copy of and return it as a newly persistent
 	 * instance. The given instance does not become associated with the session.
 	 * This operation cascades to associated instances if the association is mapped
 	 * with <tt>cascade="merge"</tt>.<br>
 	 * <br>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param object a detached instance with state to be copied
 	 * @return an updated persistent instance
 	 */
 	public Object merge(String entityName, Object object) throws HibernateException;
 
 	/**
 	 * Make a transient instance persistent. This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="persist"</tt>.<br>
 	 * <br>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param object a transient instance to be made persistent
 	 */
 	public void persist(Object object) throws HibernateException;
 	/**
 	 * Make a transient instance persistent. This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="persist"</tt>.<br>
 	 * <br>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param object a transient instance to be made persistent
 	 */
 	public void persist(String entityName, Object object) throws HibernateException;
 
 	/**
 	 * Remove a persistent instance from the datastore. The argument may be
 	 * an instance associated with the receiving <tt>Session</tt> or a transient
 	 * instance with an identifier associated with existing persistent state.
 	 * This operation cascades to associated instances if the association is mapped
 	 * with <tt>cascade="delete"</tt>.
 	 *
 	 * @param object the instance to be removed
 	 * @throws HibernateException
 	 */
 	public void delete(Object object) throws HibernateException;
 
 	/**
 	 * Remove a persistent instance from the datastore. The <b>object</b> argument may be
 	 * an instance associated with the receiving <tt>Session</tt> or a transient
 	 * instance with an identifier associated with existing persistent state.
 	 * This operation cascades to associated instances if the association is mapped
 	 * with <tt>cascade="delete"</tt>.
 	 *
 	 * @param entityName The entity name for the instance to be removed.
 	 * @param object the instance to be removed
 	 * @throws HibernateException
 	 */
 	public void delete(String entityName, Object object) throws HibernateException;
 
 	/**
 	 * Obtain the specified lock level upon the given object. This may be used to
 	 * perform a version check (<tt>LockMode.READ</tt>), to upgrade to a pessimistic
 	 * lock (<tt>LockMode.PESSIMISTIC_WRITE</tt>), or to simply reassociate a transient instance
 	 * with a session (<tt>LockMode.NONE</tt>). This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="lock"</tt>.
 	 *
 	 * @param object a persistent or transient instance
 	 * @param lockMode the lock level
 	 * @throws HibernateException
 	 * @deprecated instead call buildLockRequest(LockMode).lock(object)
 	 */
 	@Deprecated
 	public void lock(Object object, LockMode lockMode) throws HibernateException;
 
 	/**
 	 * Obtain the specified lock level upon the given object. This may be used to
 	 * perform a version check (<tt>LockMode.OPTIMISTIC</tt>), to upgrade to a pessimistic
 	 * lock (<tt>LockMode.PESSIMISTIC_WRITE</tt>), or to simply reassociate a transient instance
 	 * with a session (<tt>LockMode.NONE</tt>). This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="lock"</tt>.
 	 *
 	 * @param object a persistent or transient instance
 	 * @param lockMode the lock level
 	 * @throws HibernateException
 	 * @deprecated instead call buildLockRequest(LockMode).lock(entityName, object)
 	 */
 	@Deprecated
 	public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException;
 
 	/**
 	 * Build a LockRequest that specifies the LockMode, pessimistic lock timeout and lock scope.
 	 * timeout and scope is ignored for optimistic locking.  After building the LockRequest,
 	 * call LockRequest.lock to perform the requested locking. 
 	 *
 	 * Use: session.buildLockRequest().
 	 *      setLockMode(LockMode.PESSIMISTIC_WRITE).setTimeOut(1000 * 60).lock(entity);
 	 *
 	 * @param lockOptions contains the lock level
 	 * @return a lockRequest that can be used to lock the passed object.
 	 * @throws HibernateException
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
 	 * @throws HibernateException
 	 */
 	public void refresh(Object object) throws HibernateException;
 
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
 	 * @throws HibernateException
 	 */
 	public void refresh(String entityName, Object object) throws HibernateException;
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
 	 *
 	 * @param object a persistent or detached instance
 	 * @param lockMode the lock mode to use
 	 * @throws HibernateException
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
 	public void refresh(Object object, LockMode lockMode) throws HibernateException;
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
 	 *
 	 * @param object a persistent or detached instance
 	 * @param lockOptions contains the lock mode to use
 	 * @throws HibernateException
 	 */
 	public void refresh(Object object, LockOptions lockOptions) throws HibernateException;
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
 	 *
 	 * @param entityName a persistent class
 	 * @param object a persistent or detached instance
 	 * @param lockOptions contains the lock mode to use
 	 * @throws HibernateException
 	 */
 	public void refresh(String entityName, Object object, LockOptions lockOptions) throws HibernateException;
 	/**
 	 * Determine the current lock mode of the given object.
 	 *
 	 * @param object a persistent instance
 	 * @return the current lock mode
 	 * @throws HibernateException
 	 */
 	public LockMode getCurrentLockMode(Object object) throws HibernateException;
 
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
 	 * @return a persistent instance or null
 	 * @throws HibernateException
 	 */
 	public Object get(Class clazz, Serializable id) throws HibernateException;
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param clazz a persistent class
 	 * @param id an identifier
 	 * @param lockMode the lock mode
 	 * @return a persistent instance or null
 	 * @throws HibernateException
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
 	public Object get(Class clazz, Serializable id, LockMode lockMode) throws HibernateException;
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param clazz a persistent class
 	 * @param id an identifier
 	 * @param lockOptions the lock mode
 	 * @return a persistent instance or null
 	 * @throws HibernateException
 	 */
 	public Object get(Class clazz, Serializable id, LockOptions lockOptions) throws HibernateException;
 
 	/**
 	 * Return the persistent instance of the given named entity with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 *
 	 * @param entityName the entity name
 	 * @param id an identifier
 	 * @return a persistent instance or null
 	 * @throws HibernateException
 	 */
 	public Object get(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param entityName the entity name
 	 * @param id an identifier
 	 * @param lockMode the lock mode
 	 * @return a persistent instance or null
 	 * @throws HibernateException
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
 	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException;
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param entityName the entity name
 	 * @param id an identifier
 	 * @param lockOptions contains the lock mode
 	 * @return a persistent instance or null
 	 * @throws HibernateException
 	 */
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException;
 
 	/**
 	 * Return the entity name for a persistent entity
 	 *   
 	 * @param object a persistent entity
 	 * @return the entity name
 	 * @throws HibernateException
 	 */
 	public String getEntityName(Object object) throws HibernateException;
 	
 	/**
 	 * Create an {@link IdentifierLoadAccess} instance to retrieve the specified entity by
 	 * primary key.
 	 * 
-	 * @param entityName The name of the entity that will be retrieved
-	 * @throws HibernateException If the specified entity name is not found
+	 * @param entityName
+	 *            The name of the entity that will be retrieved
+	 * @throws HibernateException
+	 *             If the specified entity name is not found
 	 */
 	public IdentifierLoadAccess<Object> byId(String entityName);
-	
+
+	/**
+	 * Create an {@link IdentifierLoadAccess} instance to retrieve the specified entity by
+	 * primary key.
+	 * 
+	 * @param entityClass
+	 *            The type of the entity that will be retrieved
+	 * @throws HibernateException
+	 *             If the specified Class is not an entity
+	 */
+	public <T> IdentifierLoadAccess<T> byId(Class<T> entityClass);
+
 	/**
-     * Create an {@link IdentifierLoadAccess} instance to retrieve the specified entity by
-     * primary key.
-     * 
-     * @param entityClass The type of the entity that will be retrieved
-     * @throws HibernateException If the specified Class is not an entity
-     */
-    public <T> IdentifierLoadAccess<T> byId(Class<T> entityClass);
-    
-    /**
-     * Create an {@link NaturalIdLoadAccess} instance to retrieve the specified entity by
-     * its natural id.
-     * 
-     * @param entityName The name of the entity that will be retrieved
-     * @throws HibernateException If the specified entity name is not found or if the entity does not have a natural id specified
-     */
-    public NaturalIdLoadAccess<Object> byNaturalId(String entityName);
-    
-    /**
-     * Create an {@link NaturalIdLoadAccess} instance to retrieve the specified entity by
-     * its natural id.
-     * 
-     * @param entityClass The type of the entity that will be retrieved
-     * @throws HibernateException If the specified Class is not an entity or if the entity does not have a natural id specified 
-     */
-    public <T> NaturalIdLoadAccess<T> byNaturalId(Class<T> entityClass);
+	 * Create an {@link NaturalIdLoadAccess} instance to retrieve the specified entity by
+	 * its natural id.
+	 * 
+	 * @param entityName
+	 *            The name of the entity that will be retrieved
+	 * @throws HibernateException
+	 *             If the specified entity name is not found or if the entity does not have a natural id specified
+	 */
+	public NaturalIdLoadAccess<Object> byNaturalId(String entityName);
+
+	/**
+	 * Create an {@link NaturalIdLoadAccess} instance to retrieve the specified entity by
+	 * its natural id.
+	 * 
+	 * @param entityClass
+	 *            The type of the entity that will be retrieved
+	 * @throws HibernateException
+	 *             If the specified Class is not an entity or if the entity does not have a natural id specified
+	 */
+	public <T> NaturalIdLoadAccess<T> byNaturalId(Class<T> entityClass);
 
 	/**
 	 * Enable the named filter for this current session.
 	 *
 	 * @param filterName The name of the filter to be enabled.
 	 * @return The Filter instance representing the enabled filter.
 	 */
 	public Filter enableFilter(String filterName);
 
 	/**
 	 * Retrieve a currently enabled filter by name.
 	 *
 	 * @param filterName The name of the filter to be retrieved.
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
 	 */
 	public SessionStatistics getStatistics();
 
 	/**
 	 * Is the specified entity or proxy read-only?
 	 *
 	 * To get the default read-only/modifiable setting used for
 	 * entities and proxies that are loaded into the session:
 	 * @see org.hibernate.Session#isDefaultReadOnly()
 	 *
 	 * @param entityOrProxy, an entity or HibernateProxy
 	 * @return true, the entity or proxy is read-only;
 	 *         false, the entity or proxy is modifiable.
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
 	 * @param entityOrProxy, an entity or HibernateProxy
 	 * @param readOnly, if true, the entity or proxy is made read-only;
 	 *                  if false, the entity or proxy is made modifiable.
 	 */
 	public void setReadOnly(Object entityOrProxy, boolean readOnly);
 
 	/**
 	 * Controller for allowing users to perform JDBC related work using the Connection
 	 * managed by this Session.
 	 *
 	 * @param work The work to be performed.
 	 * @throws HibernateException Generally indicates wrapped {@link java.sql.SQLException}
 	 */
 	public void doWork(Work work) throws HibernateException;
 
 	/**
 	 * Controller for allowing users to perform JDBC related work using the Connection
 	 * managed by this Session, returning the result from calling <code>work.execute()</code>
 	 * ({@link ReturningWork<T>.execute(Connection)}/
 	 *
 	 * @param work The work to be performed.
 	 * @return the result from calling <code>work.execute()</code>.
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
 	 * @return the application-supplied connection or {@literal null}
 	 *
 	 * @see #reconnect(Connection)
 	 */
 	Connection disconnect() throws HibernateException;
 
 	/**
 	 * Reconnect to the given JDBC connection.
 	 *
 	 * @param connection a JDBC connection
 	 * 
 	 * @see #disconnect()
 	 */
 	void reconnect(Connection connection) throws HibernateException;
 
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
 		static final int PESSIMISTIC_NO_WAIT = 0;
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
 		 * @param lockMode
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
 
 		void lock(String entityName, Object object) throws HibernateException;
 
 		public void lock(Object object) throws HibernateException;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
index 4fd85511cc..f9bac82b04 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
@@ -1,625 +1,275 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
-import org.hibernate.LockOptions;
-import org.hibernate.NonUniqueObjectException;
-import org.hibernate.PersistentObjectException;
-import org.hibernate.cache.spi.CacheKey;
-import org.hibernate.cache.spi.access.SoftLock;
-import org.hibernate.cache.spi.entry.CacheEntry;
-import org.hibernate.engine.internal.TwoPhaseLoad;
-import org.hibernate.engine.internal.Versioning;
-import org.hibernate.engine.spi.EntityEntry;
-import org.hibernate.engine.spi.EntityKey;
-import org.hibernate.engine.spi.PersistenceContext;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.engine.spi.Status;
-import org.hibernate.event.service.spi.EventListenerRegistry;
-import org.hibernate.event.spi.EventSource;
-import org.hibernate.event.spi.EventType;
-import org.hibernate.event.spi.LoadEvent;
-import org.hibernate.event.spi.LoadEventListener;
-import org.hibernate.event.spi.PostLoadEvent;
-import org.hibernate.event.spi.PostLoadEventListener;
 import org.hibernate.event.spi.ResolveNaturalIdEvent;
 import org.hibernate.event.spi.ResolveNaturalIdEventListener;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
-import org.hibernate.proxy.HibernateProxy;
-import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.entity.EntityMetamodel;
-import org.hibernate.type.EmbeddedComponentType;
-import org.hibernate.type.Type;
-import org.hibernate.type.TypeHelper;
 import org.jboss.logging.Logger;
 
 /**
  * Defines the default load event listeners used by hibernate for loading entities
  * in response to generated load events.
- *
- * @author Steve Ebersole
+ * 
+ * @author Eric Dalquist
  */
-public class DefaultResolveNaturalIdEventListener extends AbstractLockUpgradeEventListener implements ResolveNaturalIdEventListener {
+public class DefaultResolveNaturalIdEventListener extends AbstractLockUpgradeEventListener implements
+		ResolveNaturalIdEventListener {
 
 	public static final Object REMOVED_ENTITY_MARKER = new Object();
 	public static final Object INCONSISTENT_RTN_CLASS_MARKER = new Object();
 	public static final LockMode DEFAULT_LOCK_MODE = LockMode.NONE;
 
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
-                                                                       DefaultResolveNaturalIdEventListener.class.getName());
-
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class,
+			DefaultResolveNaturalIdEventListener.class.getName() );
 
-	/* (non-Javadoc)
-     * @see org.hibernate.event.spi.ResolveNaturalIdEventListener#onResolveNaturalId(org.hibernate.event.spi.ResolveNaturalIdEvent)
-     */
-    @Override
-    public void onResolveNaturalId(ResolveNaturalIdEvent event) throws HibernateException {
+	/*
+	 * (non-Javadoc)
+	 * 
+	 * @see org.hibernate.event.spi.ResolveNaturalIdEventListener#onResolveNaturalId(org.hibernate.event.spi.
+	 * ResolveNaturalIdEvent)
+	 */
+	@Override
+	public void onResolveNaturalId(ResolveNaturalIdEvent event) throws HibernateException {
 		final SessionImplementor source = event.getSession();
 
 		EntityPersister persister = source.getFactory().getEntityPersister( event.getEntityClassName() );
 		if ( persister == null ) {
-			throw new HibernateException(
-					"Unable to locate persister: " +
-					event.getEntityClassName()
-				);
+			throw new HibernateException( "Unable to locate persister: " + event.getEntityClassName() );
 		}
-		
-		//Verify that the entity has a natural id and that the properties match up with the event.
+
+		// Verify that the entity has a natural id and that the properties match up with the event.
 		final EntityMetamodel entityMetamodel = persister.getEntityMetamodel();
 		final int[] naturalIdentifierProperties = entityMetamodel.getNaturalIdentifierProperties();
-		if (naturalIdentifierProperties == null || naturalIdentifierProperties.length == 0) {
-		    throw new HibernateException(event.getEntityClassName() + " does not have a natural id");
+		if ( naturalIdentifierProperties == null || naturalIdentifierProperties.length == 0 ) {
+			throw new HibernateException( event.getEntityClassName() + " does not have a natural id" );
 		}
-		
+
 		final Map<String, Object> naturalIdParams = event.getNaturalId();
-		if (naturalIdentifierProperties.length != naturalIdParams.size()) {
-		    throw new HibernateException(event.getEntityClassName() + " has " + naturalIdentifierProperties.length + " properties in its natural id but " + naturalIdParams.size() + " properties were specified: " + naturalIdParams);
+		if ( naturalIdentifierProperties.length != naturalIdParams.size() ) {
+			throw new HibernateException( event.getEntityClassName() + " has " + naturalIdentifierProperties.length
+					+ " properties in its natural id but " + naturalIdParams.size() + " properties were specified: "
+					+ naturalIdParams );
 		}
-		
+
 		final StandardProperty[] properties = entityMetamodel.getProperties();
-		for (int idPropIdx = 0; idPropIdx < naturalIdentifierProperties.length; idPropIdx++) {
-		    final StandardProperty property = properties[naturalIdentifierProperties[idPropIdx]];
-		    final String name = property.getName();
-            if (!naturalIdParams.containsKey(name)) {
-		        throw new HibernateException(event.getEntityClassName() + " natural id property " + name + " is missing from the map of natural id parameters: " + naturalIdParams);
-		    }
+		for ( int idPropIdx = 0; idPropIdx < naturalIdentifierProperties.length; idPropIdx++ ) {
+			final StandardProperty property = properties[naturalIdentifierProperties[idPropIdx]];
+			final String name = property.getName();
+			if ( !naturalIdParams.containsKey( name ) ) {
+				throw new HibernateException( event.getEntityClassName() + " natural id property " + name
+						+ " is missing from the map of natural id parameters: " + naturalIdParams );
+			}
 		}
-		
-		final Serializable entityId = doResolveNaturalId(event, persister);
-		event.setEntityId(entityId);
-    }
-
-
 
+		final Serializable entityId = doResolveNaturalId( event, persister );
+		event.setEntityId( entityId );
+	}
 
-    /**
-     * Coordinates the efforts to load a given entity.  First, an attempt is
-     * made to load the entity from the session-level cache.  If not found there,
-     * an attempt is made to locate it in second-level cache.  Lastly, an
-     * attempt is made to load it directly from the datasource.
-     *
-     * @param event The load event
-     * @param persister The persister for the entity being requested for load
-     * @param keyToLoad The EntityKey representing the entity to be loaded.
-     * @param options The load options.
-     * @return The loaded entity, or null.
-     */
-    protected Serializable doResolveNaturalId(
-            final ResolveNaturalIdEvent event,
-            final EntityPersister persister) {
+	/**
+	 * Coordinates the efforts to load a given entity. First, an attempt is
+	 * made to load the entity from the session-level cache. If not found there,
+	 * an attempt is made to locate it in second-level cache. Lastly, an
+	 * attempt is made to load it directly from the datasource.
+	 * 
+	 * @param event
+	 *            The load event
+	 * @param persister
+	 *            The persister for the entity being requested for load
+	 * @param keyToLoad
+	 *            The EntityKey representing the entity to be loaded.
+	 * @param options
+	 *            The load options.
+	 * @return The loaded entity, or null.
+	 */
+	protected Serializable doResolveNaturalId(final ResolveNaturalIdEvent event, final EntityPersister persister) {
 
-        if (LOG.isTraceEnabled()) LOG.trace("Attempting to resolve: "
-                                            + MessageHelper.infoString(persister,
-                                                                       event.getNaturalId(),
-                                                                       event.getSession().getFactory()));
+		if ( LOG.isTraceEnabled() )
+			LOG.trace( "Attempting to resolve: "
+					+ MessageHelper.infoString( persister, event.getNaturalId(), event.getSession().getFactory() ) );
 
-        Serializable entityId = loadFromSessionCache(event, persister);
-        if ( entityId == REMOVED_ENTITY_MARKER ) {
-            LOG.debugf("Load request found matching entity in context, but it is scheduled for removal; returning null");
-            return null;
-        }
-        if ( entityId == INCONSISTENT_RTN_CLASS_MARKER ) {
-            LOG.debugf("Load request found matching entity in context, but the matched entity was of an inconsistent return type; returning null");
-            return null;
-        }
-        if ( entityId != null ) {
-            if (LOG.isTraceEnabled()) LOG.trace("Resolved object in session cache: "
-                                                + MessageHelper.infoString(persister,
-                                                                           event.getNaturalId(),
-                                                                           event.getSession().getFactory()));
-            return entityId;
-        }
-
-        entityId = loadFromSecondLevelCache(event, persister);
-        if ( entityId != null ) {
-            if (LOG.isTraceEnabled()) LOG.trace("Resolved object in second-level cache: "
-                                                + MessageHelper.infoString(persister,
-                                                                           event.getNaturalId(),
-                                                                           event.getSession().getFactory()));
-            return entityId;
-        }
-
-        if (LOG.isTraceEnabled()) LOG.trace("Object not resolved in any cache: "
-                                            + MessageHelper.infoString(persister,
-                                                                       event.getNaturalId(),
-                                                                       event.getSession().getFactory()));
+		Serializable entityId = loadFromSessionCache( event, persister );
+		if ( entityId == REMOVED_ENTITY_MARKER ) {
+			LOG.debugf( "Load request found matching entity in context, but it is scheduled for removal; returning null" );
+			return null;
+		}
+		if ( entityId == INCONSISTENT_RTN_CLASS_MARKER ) {
+			LOG.debugf( "Load request found matching entity in context, but the matched entity was of an inconsistent return type; returning null" );
+			return null;
+		}
+		if ( entityId != null ) {
+			if ( LOG.isTraceEnabled() )
+				LOG.trace( "Resolved object in session cache: "
+						+ MessageHelper.infoString( persister, event.getNaturalId(), event.getSession().getFactory() ) );
+			return entityId;
+		}
 
-        return loadFromDatasource(event, persister);
-    }
+		entityId = loadFromSecondLevelCache( event, persister );
+		if ( entityId != null ) {
+			if ( LOG.isTraceEnabled() )
+				LOG.trace( "Resolved object in second-level cache: "
+						+ MessageHelper.infoString( persister, event.getNaturalId(), event.getSession().getFactory() ) );
+			return entityId;
+		}
 
-    /**
-     * Attempts to locate the entity in the session-level cache.
-     * <p/>
-     * If allowed to return nulls, then if the entity happens to be found in
-     * the session cache, we check the entity type for proper handling
-     * of entity hierarchies.
-     * <p/>
-     * If checkDeleted was set to true, then if the entity is found in the
-     * session-level cache, it's current status within the session cache
-     * is checked to see if it has previously been scheduled for deletion.
-     *
-     * @param event The load event
-     * @param keyToLoad The EntityKey representing the entity to be loaded.
-     * @param options The load options.
-     * @return The entity from the session-level cache, or null.
-     * @throws HibernateException Generally indicates problems applying a lock-mode.
-     */
-    protected Serializable loadFromSessionCache(
-            final ResolveNaturalIdEvent event,
-            final EntityPersister persister) throws HibernateException {
-//        SessionImplementor session = event.getSession();
-//        Object old = session.getEntityUsingInterceptor( keyToLoad );
-//
-//        if ( old != null ) {
-//            // this object was already loaded
-//            EntityEntry oldEntry = session.getPersistenceContext().getEntry( old );
-//            if ( options.isCheckDeleted() ) {
-//                Status status = oldEntry.getStatus();
-//                if ( status == Status.DELETED || status == Status.GONE ) {
-//                    return REMOVED_ENTITY_MARKER;
-//                }
-//            }
-//            if ( options.isAllowNulls() ) {
-//                final EntityPersister persister = event.getSession().getFactory().getEntityPersister( keyToLoad.getEntityName() );
-//                if ( ! persister.isInstance( old ) ) {
-//                    return INCONSISTENT_RTN_CLASS_MARKER;
-//                }
-//            }
-//            upgradeLock( old, oldEntry, event.getLockOptions(), event.getSession() );
-//        }
+		if ( LOG.isTraceEnabled() )
+			LOG.trace( "Object not resolved in any cache: "
+					+ MessageHelper.infoString( persister, event.getNaturalId(), event.getSession().getFactory() ) );
 
-        return null;
-    }
+		return loadFromDatasource( event, persister );
+	}
 
-    /**
-     * Attempts to load the entity from the second-level cache.
-     *
-     * @param event The load event
-     * @param persister The persister for the entity being requested for load
-     * @param options The load options.
-     * @return The entity from the second-level cache, or null.
-     */
-    protected Serializable loadFromSecondLevelCache(
-            final ResolveNaturalIdEvent event,
-            final EntityPersister persister) {
+	/**
+	 * Attempts to locate the entity in the session-level cache.
+	 * <p/>
+	 * If allowed to return nulls, then if the entity happens to be found in the session cache, we check the entity type
+	 * for proper handling of entity hierarchies.
+	 * <p/>
+	 * If checkDeleted was set to true, then if the entity is found in the session-level cache, it's current status
+	 * within the session cache is checked to see if it has previously been scheduled for deletion.
+	 * 
+	 * @param event
+	 *            The load event
+	 * @param keyToLoad
+	 *            The EntityKey representing the entity to be loaded.
+	 * @param options
+	 *            The load options.
+	 * @return The entity from the session-level cache, or null.
+	 * @throws HibernateException
+	 *             Generally indicates problems applying a lock-mode.
+	 */
+	protected Serializable loadFromSessionCache(final ResolveNaturalIdEvent event, final EntityPersister persister)
+			throws HibernateException {
+		// SessionImplementor session = event.getSession();
+		// Object old = session.getEntityUsingInterceptor( keyToLoad );
+		//
+		// if ( old != null ) {
+		// // this object was already loaded
+		// EntityEntry oldEntry = session.getPersistenceContext().getEntry( old );
+		// if ( options.isCheckDeleted() ) {
+		// Status status = oldEntry.getStatus();
+		// if ( status == Status.DELETED || status == Status.GONE ) {
+		// return REMOVED_ENTITY_MARKER;
+		// }
+		// }
+		// if ( options.isAllowNulls() ) {
+		// final EntityPersister persister = event.getSession().getFactory().getEntityPersister(
+		// keyToLoad.getEntityName() );
+		// if ( ! persister.isInstance( old ) ) {
+		// return INCONSISTENT_RTN_CLASS_MARKER;
+		// }
+		// }
+		// upgradeLock( old, oldEntry, event.getLockOptions(), event.getSession() );
+		// }
 
-//        final SessionImplementor source = event.getSession();
-//
-//        final boolean useCache = persister.hasCache()
-//                && source.getCacheMode().isGetEnabled();
-//
-//        if ( useCache ) {
-//
-//            final SessionFactoryImplementor factory = source.getFactory();
-//
-//            final CacheKey ck = source.generateCacheKey(
-//                    event.getNaturalId(),
-//                    persister.getIdentifierType(),
-//                    persister.getRootEntityName()
-//            );
-//            Object ce = persister.getCacheAccessStrategy().get( ck, source.getTimestamp() );
-//            if ( factory.getStatistics().isStatisticsEnabled() ) {
-//                if ( ce == null ) {
-//                    factory.getStatisticsImplementor().secondLevelCacheMiss(
-//                            persister.getCacheAccessStrategy().getRegion().getName()
-//                    );
-//                }
-//                else {
-//                    factory.getStatisticsImplementor().secondLevelCacheHit(
-//                            persister.getCacheAccessStrategy().getRegion().getName()
-//                    );
-//                }
-//            }
-//
-//            if ( ce != null ) {
-//                CacheEntry entry = (CacheEntry) persister.getCacheEntryStructure().destructure( ce, factory );
-//
-//                // Entity was found in second-level cache...
-//                return assembleCacheEntry(
-//                        entry,
-//                        event.getEntityId(),
-//                        persister,
-//                        event
-//                );
-//            }
-//        }
+		return null;
+	}
 
-        return null;
-    }
-    
+	/**
+	 * Attempts to load the entity from the second-level cache.
+	 * 
+	 * @param event
+	 *            The load event
+	 * @param persister
+	 *            The persister for the entity being requested for load
+	 * @param options
+	 *            The load options.
+	 * @return The entity from the second-level cache, or null.
+	 */
+	protected Serializable loadFromSecondLevelCache(final ResolveNaturalIdEvent event, final EntityPersister persister) {
 
+		// final SessionImplementor source = event.getSession();
+		//
+		// final boolean useCache = persister.hasCache()
+		// && source.getCacheMode().isGetEnabled();
+		//
+		// if ( useCache ) {
+		//
+		// final SessionFactoryImplementor factory = source.getFactory();
+		//
+		// final CacheKey ck = source.generateCacheKey(
+		// event.getNaturalId(),
+		// persister.getIdentifierType(),
+		// persister.getRootEntityName()
+		// );
+		// Object ce = persister.getCacheAccessStrategy().get( ck, source.getTimestamp() );
+		// if ( factory.getStatistics().isStatisticsEnabled() ) {
+		// if ( ce == null ) {
+		// factory.getStatisticsImplementor().secondLevelCacheMiss(
+		// persister.getCacheAccessStrategy().getRegion().getName()
+		// );
+		// }
+		// else {
+		// factory.getStatisticsImplementor().secondLevelCacheHit(
+		// persister.getCacheAccessStrategy().getRegion().getName()
+		// );
+		// }
+		// }
+		//
+		// if ( ce != null ) {
+		// CacheEntry entry = (CacheEntry) persister.getCacheEntryStructure().destructure( ce, factory );
+		//
+		// // Entity was found in second-level cache...
+		// return assembleCacheEntry(
+		// entry,
+		// event.getEntityId(),
+		// persister,
+		// event
+		// );
+		// }
+		// }
 
-    /**
-     * Performs the process of loading an entity from the configured
-     * underlying datasource.
-     *
-     * @param event The load event
-     * @param persister The persister for the entity being requested for load
-     * @param keyToLoad The EntityKey representing the entity to be loaded.
-     * @param options The load options.
-     * @return The object loaded from the datasource, or null if not found.
-     */
-    protected Serializable loadFromDatasource(
-            final ResolveNaturalIdEvent event,
-            final EntityPersister persister) {
-        final SessionImplementor source = event.getSession();
-        
-        return persister.loadEntityIdByNaturalId(
-                event.getNaturalId(), 
-                event.getLockOptions(), 
-                event.getSession());
-        
-        /*
-        Object entity = persister.load(
-                event.getEntityId(),
-                event.getInstanceToLoad(),
-                event.getLockOptions(),
-                source
-        );
+		return null;
+	}
 
-        if ( event.isAssociationFetch() && source.getFactory().getStatistics().isStatisticsEnabled() ) {
-            source.getFactory().getStatisticsImplementor().fetchEntity( event.getEntityClassName() );
-        }
+	/**
+	 * Performs the process of loading an entity from the configured
+	 * underlying datasource.
+	 * 
+	 * @param event
+	 *            The load event
+	 * @param persister
+	 *            The persister for the entity being requested for load
+	 * @param keyToLoad
+	 *            The EntityKey representing the entity to be loaded.
+	 * @param options
+	 *            The load options.
+	 * @return The object loaded from the datasource, or null if not found.
+	 */
+	protected Serializable loadFromDatasource(final ResolveNaturalIdEvent event, final EntityPersister persister) {
+		final SessionImplementor source = event.getSession();
 
-        return entity;
-         */
-    }
-    
-    
-//	private void loadByDerivedIdentitySimplePkValue(
-//			LoadEvent event,
-//			LoadEventListener.LoadType options,
-//			EntityPersister dependentPersister,
-//			EmbeddedComponentType dependentIdType,
-//			EntityPersister parentPersister) {
-//		final EntityKey parentEntityKey = event.getSession().generateEntityKey( event.getEntityId(), parentPersister );
-//		final Object parent = doLoad( event, parentPersister, parentEntityKey, options );
-//
-//		final Serializable dependent = (Serializable) dependentIdType.instantiate( parent, event.getSession() );
-//		dependentIdType.setPropertyValues( dependent, new Object[] {parent}, dependentPersister.getEntityMode() );
-//		final EntityKey dependentEntityKey = event.getSession().generateEntityKey( dependent, dependentPersister );
-//		event.setEntityId( dependent );
-//
-//		event.setResult( doLoad( event, dependentPersister, dependentEntityKey, options ) );
-//	}
-//
-//	/**
-//	 * Performs the load of an entity.
-//	 *
-//	 * @param event The initiating load request event
-//	 * @param persister The persister corresponding to the entity to be loaded
-//	 * @param keyToLoad The key of the entity to be loaded
-//	 * @param options The defined load options
-//	 * @return The loaded entity.
-//	 * @throws HibernateException
-//	 */
-//	protected Object load(
-//		final LoadEvent event,
-//		final EntityPersister persister,
-//		final EntityKey keyToLoad,
-//		final LoadEventListener.LoadType options) {
-//
-//		if ( event.getInstanceToLoad() != null ) {
-//			if ( event.getSession().getPersistenceContext().getEntry( event.getInstanceToLoad() ) != null ) {
-//				throw new PersistentObjectException(
-//						"attempted to load into an instance that was already associated with the session: " +
-//						MessageHelper.infoString( persister, event.getEntityId(), event.getSession().getFactory() )
-//					);
-//			}
-//			persister.setIdentifier( event.getInstanceToLoad(), event.getEntityId(), event.getSession() );
-//		}
-//
-//		Object entity = doLoad(event, persister, keyToLoad, options);
-//
-//		boolean isOptionalInstance = event.getInstanceToLoad() != null;
-//
-//		if ( !options.isAllowNulls() || isOptionalInstance ) {
-//			if ( entity == null ) {
-//				event.getSession().getFactory().getEntityNotFoundDelegate().handleEntityNotFound( event.getEntityClassName(), event.getEntityId() );
-//			}
-//		}
-//
-//		if ( isOptionalInstance && entity != event.getInstanceToLoad() ) {
-//			throw new NonUniqueObjectException( event.getEntityId(), event.getEntityClassName() );
-//		}
-//
-//		return entity;
-//	}
-//
-//	/**
-//	 * Based on configured options, will either return a pre-existing proxy,
-//	 * generate a new proxy, or perform an actual load.
-//	 *
-//	 * @param event The initiating load request event
-//	 * @param persister The persister corresponding to the entity to be loaded
-//	 * @param keyToLoad The key of the entity to be loaded
-//	 * @param options The defined load options
-//	 * @return The result of the proxy/load operation.
-//	 */
-//	protected Object proxyOrLoad(
-//		final LoadEvent event,
-//		final EntityPersister persister,
-//		final EntityKey keyToLoad,
-//		final LoadEventListener.LoadType options) {
-//
-//        if (LOG.isTraceEnabled()) LOG.trace("Loading entity: "
-//                                            + MessageHelper.infoString(persister,
-//                                                                             event.getEntityId(),
-//                                                                             event.getSession().getFactory()));
-//
-//        // this class has no proxies (so do a shortcut)
-//        if (!persister.hasProxy()) return load(event, persister, keyToLoad, options);
-//        final PersistenceContext persistenceContext = event.getSession().getPersistenceContext();
-//
-//		// look for a proxy
-//        Object proxy = persistenceContext.getProxy(keyToLoad);
-//        if (proxy != null) return returnNarrowedProxy(event, persister, keyToLoad, options, persistenceContext, proxy);
-//        if (options.isAllowProxyCreation()) return createProxyIfNecessary(event, persister, keyToLoad, options, persistenceContext);
-//        // return a newly loaded object
-//        return load(event, persister, keyToLoad, options);
-//	}
-//
-//	/**
-//	 * Given a proxy, initialize it and/or narrow it provided either
-//	 * is necessary.
-//	 *
-//	 * @param event The initiating load request event
-//	 * @param persister The persister corresponding to the entity to be loaded
-//	 * @param keyToLoad The key of the entity to be loaded
-//	 * @param options The defined load options
-//	 * @param persistenceContext The originating session
-//	 * @param proxy The proxy to narrow
-//	 * @return The created/existing proxy
-//	 */
-//	private Object returnNarrowedProxy(
-//			final LoadEvent event,
-//			final EntityPersister persister,
-//			final EntityKey keyToLoad,
-//			final LoadEventListener.LoadType options,
-//			final PersistenceContext persistenceContext,
-//			final Object proxy) {
-//        LOG.trace("Entity proxy found in session cache");
-//		LazyInitializer li = ( (HibernateProxy) proxy ).getHibernateLazyInitializer();
-//		if ( li.isUnwrap() ) {
-//			return li.getImplementation();
-//		}
-//		Object impl = null;
-//		if ( !options.isAllowProxyCreation() ) {
-//			impl = load( event, persister, keyToLoad, options );
-//			if ( impl == null ) {
-//				event.getSession().getFactory().getEntityNotFoundDelegate().handleEntityNotFound( persister.getEntityName(), keyToLoad.getIdentifier());
-//			}
-//		}
-//		return persistenceContext.narrowProxy( proxy, persister, keyToLoad, impl );
-//	}
-//
-//	/**
-//	 * If there is already a corresponding proxy associated with the
-//	 * persistence context, return it; otherwise create a proxy, associate it
-//	 * with the persistence context, and return the just-created proxy.
-//	 *
-//	 * @param event The initiating load request event
-//	 * @param persister The persister corresponding to the entity to be loaded
-//	 * @param keyToLoad The key of the entity to be loaded
-//	 * @param options The defined load options
-//	 * @param persistenceContext The originating session
-//	 * @return The created/existing proxy
-//	 */
-//	private Object createProxyIfNecessary(
-//			final LoadEvent event,
-//			final EntityPersister persister,
-//			final EntityKey keyToLoad,
-//			final LoadEventListener.LoadType options,
-//			final PersistenceContext persistenceContext) {
-//		Object existing = persistenceContext.getEntity( keyToLoad );
-//		if ( existing != null ) {
-//			// return existing object or initialized proxy (unless deleted)
-//            LOG.trace("Entity found in session cache");
-//			if ( options.isCheckDeleted() ) {
-//				EntityEntry entry = persistenceContext.getEntry( existing );
-//				Status status = entry.getStatus();
-//				if ( status == Status.DELETED || status == Status.GONE ) {
-//					return null;
-//				}
-//			}
-//			return existing;
-//		}
-//        LOG.trace("Creating new proxy for entity");
-//        // return new uninitialized proxy
-//        Object proxy = persister.createProxy(event.getEntityId(), event.getSession());
-//        persistenceContext.getBatchFetchQueue().addBatchLoadableEntityKey(keyToLoad);
-//        persistenceContext.addProxy(keyToLoad, proxy);
-//        return proxy;
-//	}
-//
-//	/**
-//	 * If the class to be loaded has been configured with a cache, then lock
-//	 * given id in that cache and then perform the load.
-//	 *
-//	 * @param event The initiating load request event
-//	 * @param persister The persister corresponding to the entity to be loaded
-//	 * @param keyToLoad The key of the entity to be loaded
-//	 * @param options The defined load options
-//	 * @param source The originating session
-//	 * @return The loaded entity
-//	 * @throws HibernateException
-//	 */
-//	protected Object lockAndLoad(
-//			final LoadEvent event,
-//			final EntityPersister persister,
-//			final EntityKey keyToLoad,
-//			final LoadEventListener.LoadType options,
-//			final SessionImplementor source) {
-//		SoftLock lock = null;
-//		final CacheKey ck;
-//		if ( persister.hasCache() ) {
-//			ck = source.generateCacheKey(
-//					event.getEntityId(),
-//					persister.getIdentifierType(),
-//					persister.getRootEntityName()
-//			);
-//			lock = persister.getCacheAccessStrategy().lockItem( ck, null );
-//		}
-//		else {
-//			ck = null;
-//		}
-//
-//		Object entity;
-//		try {
-//			entity = load(event, persister, keyToLoad, options);
-//		}
-//		finally {
-//			if ( persister.hasCache() ) {
-//				persister.getCacheAccessStrategy().unlockItem( ck, lock );
-//			}
-//		}
-//
-//		return event.getSession().getPersistenceContext().proxyFor( persister, keyToLoad, entity );
-//	}
-//
-//	private Object assembleCacheEntry(
-//			final CacheEntry entry,
-//			final Serializable id,
-//			final EntityPersister persister,
-//			final LoadEvent event) throws HibernateException {
-//
-//		final Object optionalObject = event.getInstanceToLoad();
-//		final EventSource session = event.getSession();
-//		final SessionFactoryImplementor factory = session.getFactory();
-//
-//        if (LOG.isTraceEnabled()) LOG.trace("Assembling entity from second-level cache: "
-//                                            + MessageHelper.infoString(persister, id, factory));
-//
-//		EntityPersister subclassPersister = factory.getEntityPersister( entry.getSubclass() );
-//		Object result = optionalObject == null ?
-//				session.instantiate( subclassPersister, id ) : optionalObject;
-//
-//		// make it circular-reference safe
-//		final EntityKey entityKey = session.generateEntityKey( id, subclassPersister );
-//		TwoPhaseLoad.addUninitializedCachedEntity(
-//				entityKey,
-//				result,
-//				subclassPersister,
-//				LockMode.NONE,
-//				entry.areLazyPropertiesUnfetched(),
-//				entry.getVersion(),
-//				session
-//			);
-//
-//		Type[] types = subclassPersister.getPropertyTypes();
-//		Object[] values = entry.assemble( result, id, subclassPersister, session.getInterceptor(), session ); // intializes result by side-effect
-//		TypeHelper.deepCopy(
-//				values,
-//				types,
-//				subclassPersister.getPropertyUpdateability(),
-//				values,
-//				session
-//		);
-//
-//		Object version = Versioning.getVersion( values, subclassPersister );
-//        if (LOG.isTraceEnabled()) LOG.trace("Cached Version: " + version);
-//
-//		final PersistenceContext persistenceContext = session.getPersistenceContext();
-//		boolean isReadOnly = session.isDefaultReadOnly();
-//		if ( persister.isMutable() ) {
-//			Object proxy = persistenceContext.getProxy( entityKey );
-//			if ( proxy != null ) {
-//				// there is already a proxy for this impl
-//				// only set the status to read-only if the proxy is read-only
-//				isReadOnly = ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().isReadOnly();
-//			}
-//		}
-//		else {
-//			isReadOnly = true;
-//		}
-//		persistenceContext.addEntry(
-//				result,
-//				( isReadOnly ? Status.READ_ONLY : Status.MANAGED ),
-//				values,
-//				null,
-//				id,
-//				version,
-//				LockMode.NONE,
-//				true,
-//				subclassPersister,
-//				false,
-//				entry.areLazyPropertiesUnfetched()
-//			);
-//		subclassPersister.afterInitialize( result, entry.areLazyPropertiesUnfetched(), session );
-//		persistenceContext.initializeNonLazyCollections();
-//		// upgrade the lock if necessary:
-//		//lock(result, lockMode);
-//
-//		//PostLoad is needed for EJB3
-//		//TODO: reuse the PostLoadEvent...
-//		PostLoadEvent postLoadEvent = new PostLoadEvent( session )
-//				.setEntity( result )
-//				.setId( id )
-//				.setPersister( persister );
-//
-//		for ( PostLoadEventListener listener : postLoadEventListeners( session ) ) {
-//			listener.onPostLoad( postLoadEvent );
-//		}
-//
-//		return result;
-//	}
-//
-//	private Iterable<PostLoadEventListener> postLoadEventListeners(EventSource session) {
-//		return session
-//				.getFactory()
-//				.getServiceRegistry()
-//				.getService( EventListenerRegistry.class )
-//				.getEventListenerGroup( EventType.POST_LOAD )
-//				.listeners();
-//	}
+		return persister.loadEntityIdByNaturalId( event.getNaturalId(), event.getLockOptions(), event.getSession() );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerRegistryImpl.java
index 1084292f5b..6735e28e78 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerRegistryImpl.java
@@ -1,432 +1,432 @@
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
 package org.hibernate.event.service.internal;
 
 import java.lang.reflect.Array;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.event.internal.DefaultAutoFlushEventListener;
 import org.hibernate.event.internal.DefaultDeleteEventListener;
 import org.hibernate.event.internal.DefaultDirtyCheckEventListener;
 import org.hibernate.event.internal.DefaultEvictEventListener;
 import org.hibernate.event.internal.DefaultFlushEntityEventListener;
 import org.hibernate.event.internal.DefaultFlushEventListener;
 import org.hibernate.event.internal.DefaultInitializeCollectionEventListener;
 import org.hibernate.event.internal.DefaultLoadEventListener;
 import org.hibernate.event.internal.DefaultLockEventListener;
 import org.hibernate.event.internal.DefaultMergeEventListener;
 import org.hibernate.event.internal.DefaultPersistEventListener;
 import org.hibernate.event.internal.DefaultPersistOnFlushEventListener;
 import org.hibernate.event.internal.DefaultPostLoadEventListener;
 import org.hibernate.event.internal.DefaultPreLoadEventListener;
 import org.hibernate.event.internal.DefaultRefreshEventListener;
 import org.hibernate.event.internal.DefaultReplicateEventListener;
 import org.hibernate.event.internal.DefaultResolveNaturalIdEventListener;
 import org.hibernate.event.internal.DefaultSaveEventListener;
 import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
 import org.hibernate.event.internal.DefaultUpdateEventListener;
 import org.hibernate.event.service.spi.DuplicationStrategy;
 import org.hibernate.event.service.spi.EventListenerRegistrationException;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventType;
 
 import static org.hibernate.event.spi.EventType.AUTO_FLUSH;
 import static org.hibernate.event.spi.EventType.DELETE;
 import static org.hibernate.event.spi.EventType.DIRTY_CHECK;
 import static org.hibernate.event.spi.EventType.EVICT;
 import static org.hibernate.event.spi.EventType.FLUSH;
 import static org.hibernate.event.spi.EventType.FLUSH_ENTITY;
 import static org.hibernate.event.spi.EventType.INIT_COLLECTION;
 import static org.hibernate.event.spi.EventType.LOAD;
 import static org.hibernate.event.spi.EventType.LOCK;
 import static org.hibernate.event.spi.EventType.MERGE;
 import static org.hibernate.event.spi.EventType.PERSIST;
 import static org.hibernate.event.spi.EventType.PERSIST_ONFLUSH;
 import static org.hibernate.event.spi.EventType.POST_COLLECTION_RECREATE;
 import static org.hibernate.event.spi.EventType.POST_COLLECTION_REMOVE;
 import static org.hibernate.event.spi.EventType.POST_COLLECTION_UPDATE;
 import static org.hibernate.event.spi.EventType.POST_COMMIT_DELETE;
 import static org.hibernate.event.spi.EventType.POST_COMMIT_INSERT;
 import static org.hibernate.event.spi.EventType.POST_COMMIT_UPDATE;
 import static org.hibernate.event.spi.EventType.POST_DELETE;
 import static org.hibernate.event.spi.EventType.POST_INSERT;
 import static org.hibernate.event.spi.EventType.POST_LOAD;
 import static org.hibernate.event.spi.EventType.POST_UPDATE;
 import static org.hibernate.event.spi.EventType.PRE_COLLECTION_RECREATE;
 import static org.hibernate.event.spi.EventType.PRE_COLLECTION_REMOVE;
 import static org.hibernate.event.spi.EventType.PRE_COLLECTION_UPDATE;
 import static org.hibernate.event.spi.EventType.PRE_DELETE;
 import static org.hibernate.event.spi.EventType.PRE_INSERT;
 import static org.hibernate.event.spi.EventType.PRE_LOAD;
 import static org.hibernate.event.spi.EventType.PRE_UPDATE;
 import static org.hibernate.event.spi.EventType.REFRESH;
 import static org.hibernate.event.spi.EventType.REPLICATE;
 import static org.hibernate.event.spi.EventType.RESOLVE_NATURAL_ID;
 import static org.hibernate.event.spi.EventType.SAVE;
 import static org.hibernate.event.spi.EventType.SAVE_UPDATE;
 import static org.hibernate.event.spi.EventType.UPDATE;
 
 /**
  * @author Steve Ebersole
  */
 public class EventListenerRegistryImpl implements EventListenerRegistry {
 	private Map<Class,Object> listenerClassToInstanceMap = new HashMap<Class, Object>();
 
 	private Map<EventType,EventListenerGroupImpl> registeredEventListenersMap = prepareListenerMap();
 
 	@SuppressWarnings({ "unchecked" })
 	public <T> EventListenerGroupImpl<T> getEventListenerGroup(EventType<T> eventType) {
 		EventListenerGroupImpl<T> listeners = registeredEventListenersMap.get( eventType );
 		if ( listeners == null ) {
 			throw new HibernateException( "Unable to find listeners for type [" + eventType.eventName() + "]" );
 		}
 		return listeners;
 	}
 
 	@Override
 	public void addDuplicationStrategy(DuplicationStrategy strategy) {
 		for ( EventListenerGroupImpl group : registeredEventListenersMap.values() ) {
 			group.addDuplicationStrategy( strategy );
 		}
 	}
 
 	@Override
 	public <T> void setListeners(EventType<T> type, Class<T>... listenerClasses) {
 		setListeners( type, resolveListenerInstances( type, listenerClasses ) );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	private <T> T[] resolveListenerInstances(EventType<T> type, Class<T>... listenerClasses) {
 		T[] listeners = (T[]) Array.newInstance( type.baseListenerInterface(), listenerClasses.length );
 		for ( int i = 0; i < listenerClasses.length; i++ ) {
 			listeners[i] = resolveListenerInstance( listenerClasses[i] );
 		}
 		return listeners;
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	private <T> T resolveListenerInstance(Class<T> listenerClass) {
 		T listenerInstance = (T) listenerClassToInstanceMap.get( listenerClass );
 		if ( listenerInstance == null ) {
 			listenerInstance = instantiateListener( listenerClass );
 			listenerClassToInstanceMap.put( listenerClass, listenerInstance );
 		}
 		return listenerInstance;
 	}
 
 	private <T> T instantiateListener(Class<T> listenerClass) {
 		try {
 			return listenerClass.newInstance();
 		}
 		catch ( Exception e ) {
 			throw new EventListenerRegistrationException(
 					"Unable to instantiate specified event listener class: " + listenerClass.getName(),
 					e
 			);
 		}
 	}
 
 	@Override
 	public <T> void setListeners(EventType<T> type, T... listeners) {
 		EventListenerGroupImpl<T> registeredListeners = getEventListenerGroup( type );
 		registeredListeners.clear();
 		if ( listeners != null ) {
 			for ( int i = 0, max = listeners.length; i < max; i++ ) {
 				registeredListeners.appendListener( listeners[i] );
 			}
 		}
 	}
 
 	@Override
 	public <T> void appendListeners(EventType<T> type, Class<T>... listenerClasses) {
 		appendListeners( type, resolveListenerInstances( type, listenerClasses ) );
 	}
 
 	@Override
 	public <T> void appendListeners(EventType<T> type, T... listeners) {
 		getEventListenerGroup( type ).appendListeners( listeners );
 	}
 
 	@Override
 	public <T> void prependListeners(EventType<T> type, Class<T>... listenerClasses) {
 		prependListeners( type, resolveListenerInstances( type, listenerClasses ) );
 	}
 
 	@Override
 	public <T> void prependListeners(EventType<T> type, T... listeners) {
 		getEventListenerGroup( type ).prependListeners( listeners );
 	}
 
 	private static Map<EventType,EventListenerGroupImpl> prepareListenerMap() {
 		final Map<EventType,EventListenerGroupImpl> workMap = new HashMap<EventType, EventListenerGroupImpl>();
 
 		// auto-flush listeners
 		prepareListeners(
 				AUTO_FLUSH,
 				new DefaultAutoFlushEventListener(),
 				workMap
 		);
 
 		// create listeners
 		prepareListeners(
 				PERSIST,
 				new DefaultPersistEventListener(),
 				workMap
 		);
 
 		// create-onflush listeners
 		prepareListeners(
 				PERSIST_ONFLUSH,
 				new DefaultPersistOnFlushEventListener(),
 				workMap
 		);
 
 		// delete listeners
 		prepareListeners(
 				DELETE,
 				new DefaultDeleteEventListener(),
 				workMap
 		);
 
 		// dirty-check listeners
 		prepareListeners(
 				DIRTY_CHECK,
 				new DefaultDirtyCheckEventListener(),
 				workMap
 		);
 
 		// evict listeners
 		prepareListeners(
 				EVICT,
 				new DefaultEvictEventListener(),
 				workMap
 		);
 
 		// flush listeners
 		prepareListeners(
 				FLUSH,
 				new DefaultFlushEventListener(),
 				workMap
 		);
 
 		// flush-entity listeners
 		prepareListeners(
 				FLUSH_ENTITY,
 				new DefaultFlushEntityEventListener(),
 				workMap
 		);
 
 		// load listeners
 		prepareListeners(
 				LOAD,
 				new DefaultLoadEventListener(),
 				workMap
 		);
 
-        // resolve natural-id listeners
-        prepareListeners(
-                RESOLVE_NATURAL_ID,
-                new DefaultResolveNaturalIdEventListener(),
-                workMap
-        );
+		// resolve natural-id listeners
+		prepareListeners( 
+				RESOLVE_NATURAL_ID, 
+				new DefaultResolveNaturalIdEventListener(), 
+				workMap 
+		);
 
 		// load-collection listeners
 		prepareListeners(
 				INIT_COLLECTION,
 				new DefaultInitializeCollectionEventListener(),
 				workMap
 		);
 
 		// lock listeners
 		prepareListeners(
 				LOCK,
 				new DefaultLockEventListener(),
 				workMap
 		);
 
 		// merge listeners
 		prepareListeners(
 				MERGE,
 				new DefaultMergeEventListener(),
 				workMap
 		);
 
 		// pre-collection-recreate listeners
 		prepareListeners(
 				PRE_COLLECTION_RECREATE,
 				workMap
 		);
 
 		// pre-collection-remove listeners
 		prepareListeners(
 				PRE_COLLECTION_REMOVE,
 				workMap
 		);
 
 		// pre-collection-update listeners
 		prepareListeners(
 				PRE_COLLECTION_UPDATE,
 				workMap
 		);
 
 		// pre-delete listeners
 		prepareListeners(
 				PRE_DELETE,
 				workMap
 		);
 
 		// pre-insert listeners
 		prepareListeners(
 				PRE_INSERT,
 				workMap
 		);
 
 		// pre-load listeners
 		prepareListeners(
 				PRE_LOAD,
 				new DefaultPreLoadEventListener(),
 				workMap
 		);
 
 		// pre-update listeners
 		prepareListeners(
 				PRE_UPDATE,
 				workMap
 		);
 
 		// post-collection-recreate listeners
 		prepareListeners(
 				POST_COLLECTION_RECREATE,
 				workMap
 		);
 
 		// post-collection-remove listeners
 		prepareListeners(
 				POST_COLLECTION_REMOVE,
 				workMap
 		);
 
 		// post-collection-update listeners
 		prepareListeners(
 				POST_COLLECTION_UPDATE,
 				workMap
 		);
 
 		// post-commit-delete listeners
 		prepareListeners(
 				POST_COMMIT_DELETE,
 				workMap
 		);
 
 		// post-commit-insert listeners
 		prepareListeners(
 				POST_COMMIT_INSERT,
 				workMap
 		);
 
 		// post-commit-update listeners
 		prepareListeners(
 				POST_COMMIT_UPDATE,
 				workMap
 		);
 
 		// post-delete listeners
 		prepareListeners(
 				POST_DELETE,
 				workMap
 		);
 
 		// post-insert listeners
 		prepareListeners(
 				POST_INSERT,
 				workMap
 		);
 
 		// post-load listeners
 		prepareListeners(
 				POST_LOAD,
 				new DefaultPostLoadEventListener(),
 				workMap
 		);
 
 		// post-update listeners
 		prepareListeners(
 				POST_UPDATE,
 				workMap
 		);
 
 		// update listeners
 		prepareListeners(
 				UPDATE,
 				new DefaultUpdateEventListener(),
 				workMap
 		);
 
 		// refresh listeners
 		prepareListeners(
 				REFRESH,
 				new DefaultRefreshEventListener(),
 				workMap
 		);
 
 		// replicate listeners
 		prepareListeners(
 				REPLICATE,
 				new DefaultReplicateEventListener(),
 				workMap
 		);
 
 		// save listeners
 		prepareListeners(
 				SAVE,
 				new DefaultSaveEventListener(),
 				workMap
 		);
 
 		// save-update listeners
 		prepareListeners(
 				SAVE_UPDATE,
 				new DefaultSaveOrUpdateEventListener(),
 				workMap
 		);
 
 		return Collections.unmodifiableMap( workMap );
 	}
 
 	private static <T> void prepareListeners(EventType<T> type, Map<EventType,EventListenerGroupImpl> map) {
 		prepareListeners( type, null, map );
 	}
 
 	private static <T> void prepareListeners(EventType<T> type, T defaultListener, Map<EventType,EventListenerGroupImpl> map) {
 		final EventListenerGroupImpl<T> listeners = new EventListenerGroupImpl<T>( type );
 		if ( defaultListener != null ) {
 			listeners.appendListener( defaultListener );
 		}
 		map.put( type, listeners  );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/spi/EventType.java b/hibernate-core/src/main/java/org/hibernate/event/spi/EventType.java
index bc01b93ccb..aa63f0cc3b 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/spi/EventType.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/spi/EventType.java
@@ -1,202 +1,202 @@
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
 package org.hibernate.event.spi;
 
 import java.lang.reflect.Field;
 import java.security.AccessController;
 import java.security.PrivilegedAction;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 
 /**
  * Enumeration of the recognized types of events, including meta-information about each.
  *
  * @author Steve Ebersole
  */
 public class EventType<T> {
 	public static final EventType<LoadEventListener> LOAD
 			= new EventType<LoadEventListener>( "load", LoadEventListener.class );
-	   public static final EventType<ResolveNaturalIdEventListener> RESOLVE_NATURAL_ID
-       = new EventType<ResolveNaturalIdEventListener>( "resolve-natural-id", ResolveNaturalIdEventListener.class );
+	public static final EventType<ResolveNaturalIdEventListener> RESOLVE_NATURAL_ID
+			= new EventType<ResolveNaturalIdEventListener>( "resolve-natural-id", ResolveNaturalIdEventListener.class );
 	public static final EventType<InitializeCollectionEventListener> INIT_COLLECTION
 			= new EventType<InitializeCollectionEventListener>( "load-collection", InitializeCollectionEventListener.class );
 
 	public static final EventType<SaveOrUpdateEventListener> SAVE_UPDATE
 			= new EventType<SaveOrUpdateEventListener>( "save-update", SaveOrUpdateEventListener.class );
 	public static final EventType<SaveOrUpdateEventListener> UPDATE
 			= new EventType<SaveOrUpdateEventListener>( "update", SaveOrUpdateEventListener.class );
 	public static final EventType<SaveOrUpdateEventListener> SAVE
 			= new EventType<SaveOrUpdateEventListener>( "save", SaveOrUpdateEventListener.class );
 	public static final EventType<PersistEventListener> PERSIST
 			= new EventType<PersistEventListener>( "create", PersistEventListener.class );
 	public static final EventType<PersistEventListener> PERSIST_ONFLUSH
 			= new EventType<PersistEventListener>( "create-onflush", PersistEventListener.class );
 
 	public static final EventType<MergeEventListener> MERGE
 			= new EventType<MergeEventListener>( "merge", MergeEventListener.class );
 
 	public static final EventType<DeleteEventListener> DELETE
 			= new EventType<DeleteEventListener>( "delete", DeleteEventListener.class );
 
 	public static final EventType<ReplicateEventListener> REPLICATE
 			= new EventType<ReplicateEventListener>( "replicate", ReplicateEventListener.class );
 
 	public static final EventType<FlushEventListener> FLUSH
 			= new EventType<FlushEventListener>( "flush", FlushEventListener.class );
 	public static final EventType<AutoFlushEventListener> AUTO_FLUSH
 			= new EventType<AutoFlushEventListener>( "auto-flush", AutoFlushEventListener.class );
 	public static final EventType<DirtyCheckEventListener> DIRTY_CHECK
 			= new EventType<DirtyCheckEventListener>( "dirty-check", DirtyCheckEventListener.class );
 	public static final EventType<FlushEntityEventListener> FLUSH_ENTITY
 			= new EventType<FlushEntityEventListener>( "flush-entity", FlushEntityEventListener.class );
 
 	public static final EventType<EvictEventListener> EVICT
 			= new EventType<EvictEventListener>( "evict", EvictEventListener.class );
 
 	public static final EventType<LockEventListener> LOCK
 			= new EventType<LockEventListener>( "lock", LockEventListener.class );
 
 	public static final EventType<RefreshEventListener> REFRESH
 			= new EventType<RefreshEventListener>( "refresh", RefreshEventListener.class );
 
 	public static final EventType<PreLoadEventListener> PRE_LOAD
 			= new EventType<PreLoadEventListener>( "pre-load", PreLoadEventListener.class );
 	public static final EventType<PreDeleteEventListener> PRE_DELETE
 			= new EventType<PreDeleteEventListener>( "pre-delete", PreDeleteEventListener.class );
 	public static final EventType<PreUpdateEventListener> PRE_UPDATE
 			= new EventType<PreUpdateEventListener>( "pre-update", PreUpdateEventListener.class );
 	public static final EventType<PreInsertEventListener> PRE_INSERT
 			= new EventType<PreInsertEventListener>( "pre-insert", PreInsertEventListener.class );
 
 	public static final EventType<PostLoadEventListener> POST_LOAD
 			= new EventType<PostLoadEventListener>( "post-load", PostLoadEventListener.class );
 	public static final EventType<PostDeleteEventListener> POST_DELETE
 			= new EventType<PostDeleteEventListener>( "post-delete", PostDeleteEventListener.class );
 	public static final EventType<PostUpdateEventListener> POST_UPDATE
 			= new EventType<PostUpdateEventListener>( "post-update", PostUpdateEventListener.class );
 	public static final EventType<PostInsertEventListener> POST_INSERT
 			= new EventType<PostInsertEventListener>( "post-insert", PostInsertEventListener.class );
 
 	public static final EventType<PostDeleteEventListener> POST_COMMIT_DELETE
 			= new EventType<PostDeleteEventListener>( "post-commit-delete", PostDeleteEventListener.class );
 	public static final EventType<PostUpdateEventListener> POST_COMMIT_UPDATE
 			= new EventType<PostUpdateEventListener>( "post-commit-update", PostUpdateEventListener.class );
 	public static final EventType<PostInsertEventListener> POST_COMMIT_INSERT
 			= new EventType<PostInsertEventListener>( "post-commit-insert", PostInsertEventListener.class );
 
 	public static final EventType<PreCollectionRecreateEventListener> PRE_COLLECTION_RECREATE
 			= new EventType<PreCollectionRecreateEventListener>( "pre-collection-recreate", PreCollectionRecreateEventListener.class );
 	public static final EventType<PreCollectionRemoveEventListener> PRE_COLLECTION_REMOVE
 			= new EventType<PreCollectionRemoveEventListener>( "pre-collection-remove", PreCollectionRemoveEventListener.class );
 	public static final EventType<PreCollectionUpdateEventListener> PRE_COLLECTION_UPDATE
 			= new EventType<PreCollectionUpdateEventListener>( "pre-collection-update", PreCollectionUpdateEventListener.class );
 
 	public static final EventType<PostCollectionRecreateEventListener> POST_COLLECTION_RECREATE
 			= new EventType<PostCollectionRecreateEventListener>( "post-collection-recreate", PostCollectionRecreateEventListener.class );
 	public static final EventType<PostCollectionRemoveEventListener> POST_COLLECTION_REMOVE
 			= new EventType<PostCollectionRemoveEventListener>( "post-collection-remove", PostCollectionRemoveEventListener.class );
 	public static final EventType<PostCollectionUpdateEventListener> POST_COLLECTION_UPDATE
 			= new EventType<PostCollectionUpdateEventListener>( "post-collection-update", PostCollectionUpdateEventListener.class );
 
 
 	/**
 	 * Maintain a map of {@link EventType} instances keyed by name for lookup by name as well as {@link #values()}
 	 * resolution.
 	 */
 	public static final Map<String,EventType> eventTypeByNameMap = AccessController.doPrivileged(
 			new PrivilegedAction<Map<String, EventType>>() {
 				@Override
 				public Map<String, EventType> run() {
 					final Map<String, EventType> typeByNameMap = new HashMap<String, EventType>();
 					final Field[] fields = EventType.class.getDeclaredFields();
 					for ( int i = 0, max = fields.length; i < max; i++ ) {
 						if ( EventType.class.isAssignableFrom( fields[i].getType() ) ) {
 							try {
 								final EventType typeField = ( EventType ) fields[i].get( null );
 								typeByNameMap.put( typeField.eventName(), typeField );
 							}
 							catch( Exception t ) {
 								throw new HibernateException( "Unable to initialize EventType map", t );
 							}
 						}
 					}
 					return typeByNameMap;
 				}
 			}
 	);
 
 	/**
 	 * Find an {@link EventType} by its name
 	 *
 	 * @param eventName The name
 	 *
 	 * @return The {@link EventType} instance.
 	 *
 	 * @throws HibernateException If eventName is null, or if eventName does not correlate to any known event type.
 	 */
 	public static EventType resolveEventTypeByName(final String eventName) {
 		if ( eventName == null ) {
 			throw new HibernateException( "event name to resolve cannot be null" );
 		}
 		final EventType eventType = eventTypeByNameMap.get( eventName );
 		if ( eventType == null ) {
 			throw new HibernateException( "Unable to locate proper event type for event name [" + eventName + "]" );
 		}
 		return eventType;
 	}
 
 	/**
 	 * Get a collection of all {@link EventType} instances.
 	 *
 	 * @return All {@link EventType} instances
 	 */
 	public static Collection<EventType> values() {
 		return eventTypeByNameMap.values();
 	}
 
 
 	private final String eventName;
 	private final Class<? extends T> baseListenerInterface;
 
 	private EventType(String eventName, Class<? extends T> baseListenerInterface) {
 		this.eventName = eventName;
 		this.baseListenerInterface = baseListenerInterface;
 	}
 
 	public String eventName() {
 		return eventName;
 	}
 
 	public Class baseListenerInterface() {
 		return baseListenerInterface;
 	}
 
 	@Override
 	public String toString() {
 		return eventName();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/spi/ResolveNaturalIdEvent.java b/hibernate-core/src/main/java/org/hibernate/event/spi/ResolveNaturalIdEvent.java
index d8704dc80b..7531c1a18b 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/spi/ResolveNaturalIdEvent.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/spi/ResolveNaturalIdEvent.java
@@ -1,99 +1,100 @@
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
 package org.hibernate.event.spi;
 
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.Map;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 
 /**
- *  Defines an event class for the resolving of an entity id from the entity's natural-id
- *
- * @author Steve Ebersole
+ * Defines an event class for the resolving of an entity id from the entity's natural-id
+ * 
+ * @author Eric Dalquist
  */
 public class ResolveNaturalIdEvent extends AbstractEvent {
 	public static final LockMode DEFAULT_LOCK_MODE = LockMode.NONE;
 
 	private Map<String, Object> naturalId;
 	private LockOptions lockOptions;
 	private String entityClassName;
 	private Serializable entityId;
 
 	public ResolveNaturalIdEvent(Map<String, Object> naturalId, String entityClassName, EventSource source) {
-	    this(naturalId, entityClassName, new LockOptions(), source);
+		this( naturalId, entityClassName, new LockOptions(), source );
 	}
-	
-	public ResolveNaturalIdEvent(Map<String, Object> naturalId, String entityClassName, LockOptions lockOptions, EventSource source) {
-		super(source);
+
+	public ResolveNaturalIdEvent(Map<String, Object> naturalId, String entityClassName, LockOptions lockOptions,
+			EventSource source) {
+		super( source );
 
 		if ( naturalId == null || naturalId.isEmpty() ) {
-			throw new IllegalArgumentException("id to load is required for loading");
+			throw new IllegalArgumentException( "id to load is required for loading" );
 		}
 
-        if ( lockOptions.getLockMode() == LockMode.WRITE ) {
-            throw new IllegalArgumentException("Invalid lock mode for loading");
-        }
-        else if ( lockOptions.getLockMode() == null ) {
-            lockOptions.setLockMode(DEFAULT_LOCK_MODE);
-        }
+		if ( lockOptions.getLockMode() == LockMode.WRITE ) {
+			throw new IllegalArgumentException( "Invalid lock mode for loading" );
+		}
+		else if ( lockOptions.getLockMode() == null ) {
+			lockOptions.setLockMode( DEFAULT_LOCK_MODE );
+		}
 
 		this.naturalId = naturalId;
 		this.entityClassName = entityClassName;
 	}
 
 	public Map<String, Object> getNaturalId() {
-		return Collections.unmodifiableMap(naturalId);
+		return Collections.unmodifiableMap( naturalId );
 	}
 
 	public void setNaturalId(Map<String, Object> naturalId) {
 		this.naturalId = naturalId;
 	}
 
 	public String getEntityClassName() {
 		return entityClassName;
 	}
 
 	public void setEntityClassName(String entityClassName) {
 		this.entityClassName = entityClassName;
 	}
 
 	public Serializable getEntityId() {
 		return entityId;
 	}
 
 	public void setEntityId(Serializable entityId) {
 		this.entityId = entityId;
 	}
 
-    public LockOptions getLockOptions() {
-        return lockOptions;
-    }
+	public LockOptions getLockOptions() {
+		return lockOptions;
+	}
 
-    public void setLockOptions(LockOptions lockOptions) {
-        this.lockOptions = lockOptions;
-    }
+	public void setLockOptions(LockOptions lockOptions) {
+		this.lockOptions = lockOptions;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/spi/ResolveNaturalIdEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/spi/ResolveNaturalIdEventListener.java
index a1dc43e3da..4939cccb84 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/spi/ResolveNaturalIdEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/spi/ResolveNaturalIdEventListener.java
@@ -1,45 +1,46 @@
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
 package org.hibernate.event.spi;
 
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 
 /**
  * Defines the contract for handling of resolve natural id events generated from a session.
- *
- * @author Steve Ebersole
+ * 
+ * @author Eric Dalquist
  */
 public interface ResolveNaturalIdEventListener extends Serializable {
 
-	/** 
+	/**
 	 * Handle the given resolve natural id event.
-     *
-     * @param event The resolve natural id event to be handled.
-     * @throws HibernateException
-     */
+	 * 
+	 * @param event
+	 *            The resolve natural id event to be handled.
+	 * @throws HibernateException
+	 */
 	public void onResolveNaturalId(ResolveNaturalIdEvent event) throws HibernateException;
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
index 4face8d2ed..2f4c2dafd8 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
@@ -1,2336 +1,2299 @@
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
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
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
 import org.hibernate.Transaction;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.jdbc.LobCreator;
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
 import org.hibernate.engine.spi.Status;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
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
 			return transactionCoordinator.close();
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
 		replacePersistenceContext( ( ( NonFlushedChangesImpl ) nonFlushedChanges ).getPersistenceContext() );
 		replaceActionQueue( ( ( NonFlushedChangesImpl ) nonFlushedChanges ).getActionQueue() );
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
-	    return this.byId(entityClass).getReference(id);
+		return this.byId(entityClass).getReference(id);
 	}
 
 	public Object load(String entityName, Serializable id) throws HibernateException {
-	    return this.byId(entityName).getReference(id);
+		return this.byId(entityName).getReference(id);
 	}
 
 	public Object get(Class entityClass, Serializable id) throws HibernateException {
         return this.byId(entityClass).load(id);
 	}
 
 	public Object get(String entityName, Serializable id) throws HibernateException {
         return this.byId(entityName).load(id);
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
-	    return this.byId(entityClass).with(lockMode).getReference(id);
+		return this.byId( entityClass ).with( lockMode ).getReference( id );
 	}
 
 	public Object load(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
-	    return this.byId(entityClass).with(lockOptions).getReference(id);
+		return this.byId( entityClass ).with( lockOptions ).getReference( id );
 	}
 
 	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
-	    return this.byId(entityName).with(lockMode).getReference(id);
+		return this.byId( entityName ).with( lockMode ).getReference( id );
 	}
 
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
-	    return this.byId(entityName).with(lockOptions).getReference(id);
+		return this.byId( entityName ).with( lockOptions ).getReference( id );
 	}
 
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) throws HibernateException {
-	    return this.byId(entityClass).with(lockMode).load(id);
+		return this.byId( entityClass ).with( lockMode ).load( id );
 	}
 
 	public Object get(Class entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
-	    return this.byId(entityClass).with(lockOptions).load(id);
+		return this.byId( entityClass ).with( lockOptions ).load( id );
 	}
 
 	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
-	    return this.byId(entityName).with(lockMode).load(id);
+		return this.byId( entityName ).with( lockMode ).load( id );
 	}
 
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
-	    return this.byId(entityName).with(lockOptions).load(id);
+		return this.byId( entityName ).with( lockOptions ).load( id );
 	}
 	
-	/* (non-Javadoc)
-     * @see org.hibernate.Session#byId(java.lang.String)
-     */
-    @Override
-    public IdentifierLoadAccessImpl<Object> byId(String entityName) {
-        return new IdentifierLoadAccessImpl<Object>(entityName, Object.class);
-    }
+	@Override
+	public IdentifierLoadAccessImpl<Object> byId(String entityName) {
+		return new IdentifierLoadAccessImpl<Object>( entityName, Object.class );
+	}
 
-    /* (non-Javadoc)
-     * @see org.hibernate.Session#byId(java.lang.Class)
-     */
-    @Override
-    public <T> IdentifierLoadAccessImpl<T> byId(Class<T> entityClass) {
-        return new IdentifierLoadAccessImpl<T>(entityClass.getName(), entityClass);
-    }
+	@Override
+	public <T> IdentifierLoadAccessImpl<T> byId(Class<T> entityClass) {
+		return new IdentifierLoadAccessImpl<T>( entityClass.getName(), entityClass );
+	}
 
-    /* (non-Javadoc)
-     * @see org.hibernate.Session#byNaturalId(java.lang.String)
-     */
-    @Override
-    public NaturalIdLoadAccess<Object> byNaturalId(String entityName) {
-        return new NaturalIdLoadAccessImpl<Object>(entityName, Object.class);
-    }
+	@Override
+	public NaturalIdLoadAccess<Object> byNaturalId(String entityName) {
+		return new NaturalIdLoadAccessImpl<Object>( entityName, Object.class );
+	}
 
-    /* (non-Javadoc)
-     * @see org.hibernate.Session#byNaturalId(java.lang.Class)
-     */
-    @Override
-    public <T> NaturalIdLoadAccess<T> byNaturalId(Class<T> entityClass) {
-        return new NaturalIdLoadAccessImpl<T>(entityClass.getName(), entityClass);
-    }
+	@Override
+	public <T> NaturalIdLoadAccess<T> byNaturalId(Class<T> entityClass) {
+		return new NaturalIdLoadAccessImpl<T>( entityClass.getName(), entityClass );
+	}
 
-    private void fireLoad(LoadEvent event, LoadType loadType) {
+	private void fireLoad(LoadEvent event, LoadType loadType) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( LoadEventListener listener : listeners( EventType.LOAD ) ) {
 			listener.onLoad( event, loadType );
 		}
 	}
 
-    private void fireResolveNaturalId(ResolveNaturalIdEvent event) {
-        errorIfClosed();
-        checkTransactionSynchStatus();
-        for ( ResolveNaturalIdEventListener listener : listeners( EventType.RESOLVE_NATURAL_ID ) ) {
-            listener.onResolveNaturalId(event);
-        }
-    }
+	private void fireResolveNaturalId(ResolveNaturalIdEvent event) {
+		errorIfClosed();
+		checkTransactionSynchStatus();
+		for ( ResolveNaturalIdEventListener listener : listeners( EventType.RESOLVE_NATURAL_ID ) ) {
+			listener.onResolveNaturalId( event );
+		}
+	}
 
 
 	// refresh() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void refresh(Object object) throws HibernateException {
 		refresh( null, object );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object) throws HibernateException {
 		fireRefresh( new RefreshEvent( entityName, object, this ) );
 	}
 
 	public void refresh(Object object, LockMode lockMode) throws HibernateException {
 		fireRefresh( new RefreshEvent(object, lockMode, this) );
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
 
 	private void fireRefresh(Map refreshedAlready, RefreshEvent event) {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
 			listener.onRefresh( event, refreshedAlready );
 		}
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
 	}
 
 
 	// evict() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * remove any hard references to the entity that are held by the infrastructure
 	 * (references held by application or other persistant instances are okay)
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
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
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
 		return filter;
 	}
 
 	public Query getNamedQuery(String queryName) throws MappingException {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		return super.getNamedQuery( queryName );
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
 		StringBuffer buf = new StringBuffer(500)
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
 		persistenceContext.setReadOnly(entity, readOnly);
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
 			return connection(
 					session.transactionCoordinator
 							.getJdbcCoordinator()
 							.getLogicalConnection()
 							.getDistinctConnectionProxy()
 			);
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
 
 		@Override
 		public SharedSessionBuilder transactionContext() {
 			this.shareTransactionContext = true;
 			return this;
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
     
-    private class IdentifierLoadAccessImpl<T> implements IdentifierLoadAccess<T> {
-        private final String entityName;
-        private final Class<T> entityClass;
-        private LockOptions lockOptions;
-        
-        /**
-         */
-        private IdentifierLoadAccessImpl(String entityName, Class<T> entityClass) {
-            this.entityName = entityName;
-            this.entityClass = entityClass;
-        }
+	private class IdentifierLoadAccessImpl<T> implements IdentifierLoadAccess<T> {
+		private final String entityName;
+		private final Class<T> entityClass;
+		private LockOptions lockOptions;
 
-        /* (non-Javadoc)
-         * @see org.hibernate.IdentifierLoadAccess#with(org.hibernate.LockOptions)
-         */
-        @Override
-        public final IdentifierLoadAccessImpl<T> with(LockOptions lockOptions) {
-            this.lockOptions = lockOptions;
-            return this;
-        }
+		private IdentifierLoadAccessImpl(String entityName, Class<T> entityClass) {
+			this.entityName = entityName;
+			this.entityClass = entityClass;
+		}
+
+		@Override
+		public final IdentifierLoadAccessImpl<T> with(LockOptions lockOptions) {
+			this.lockOptions = lockOptions;
+			return this;
+		}
 
-        /**
+		/**
          * Support for legacy {@link Session#load(Class, Serializable, LockMode)} and {@link Session#load(String, Serializable, LockMode)
          * @deprecated
          */
-        @Deprecated
-        public final IdentifierLoadAccessImpl<T> with(LockMode lockMode) {
-            this.lockOptions = new LockOptions();
-            this.lockOptions.setLockMode(lockMode);
-            return this;
-        }
+		@Deprecated
+		public final IdentifierLoadAccessImpl<T> with(LockMode lockMode) {
+			this.lockOptions = new LockOptions();
+			this.lockOptions.setLockMode( lockMode );
+			return this;
+		}
 
-        /* (non-Javadoc)
-         * @see org.hibernate.IdentifierLoadAccess#getReference(java.io.Serializable)
-         */
-        @Override
-        public final Object getReference(Serializable id) {
-            if (this.lockOptions != null) {
-                LoadEvent event = new LoadEvent(id, entityName, lockOptions, SessionImpl.this);
-                fireLoad( event, LoadEventListener.LOAD );
-                return event.getResult();
-            }
-            
-            LoadEvent event = new LoadEvent(id, entityName, false, SessionImpl.this);
-            boolean success = false;
-            try {
-                fireLoad( event, LoadEventListener.LOAD );
-                if ( event.getResult() == null ) {
-                    getFactory().getEntityNotFoundDelegate().handleEntityNotFound( entityName, id );
-                }
-                success = true;
-                return event.getResult();
-            }
-            finally {
-                afterOperation(success);
-            }
-        }
+		@Override
+		public final Object getReference(Serializable id) {
+			if ( this.lockOptions != null ) {
+				LoadEvent event = new LoadEvent( id, entityName, lockOptions, SessionImpl.this );
+				fireLoad( event, LoadEventListener.LOAD );
+				return event.getResult();
+			}
 
-        /* (non-Javadoc)
-         * @see org.hibernate.IdentifierLoadAccess#load(java.io.Serializable)
-         */
-        @Override
-        public final Object load(Serializable id) {
-            if (this.lockOptions != null) {
-                LoadEvent event = new LoadEvent(id, entityName, lockOptions, SessionImpl.this);
-                fireLoad( event, LoadEventListener.GET );
-                return event.getResult();
-            } 
-            
-            LoadEvent event = new LoadEvent(id, entityName, false, SessionImpl.this);
-            boolean success = false;
-            try {
-                fireLoad(event, LoadEventListener.GET);
-                success = true;
-                return event.getResult();
-            }
-            finally {
-                afterOperation(success);
-            }
-        }
+			LoadEvent event = new LoadEvent( id, entityName, false, SessionImpl.this );
+			boolean success = false;
+			try {
+				fireLoad( event, LoadEventListener.LOAD );
+				if ( event.getResult() == null ) {
+					getFactory().getEntityNotFoundDelegate().handleEntityNotFound( entityName, id );
+				}
+				success = true;
+				return event.getResult();
+			}
+			finally {
+				afterOperation( success );
+			}
+		}
+
+		@Override
+		public final Object load(Serializable id) {
+			if ( this.lockOptions != null ) {
+				LoadEvent event = new LoadEvent( id, entityName, lockOptions, SessionImpl.this );
+				fireLoad( event, LoadEventListener.GET );
+				return event.getResult();
+			}
+
+			LoadEvent event = new LoadEvent( id, entityName, false, SessionImpl.this );
+			boolean success = false;
+			try {
+				fireLoad( event, LoadEventListener.GET );
+				success = true;
+				return event.getResult();
+			}
+			finally {
+				afterOperation( success );
+			}
+		}
 	}
-    
-    private class NaturalIdLoadAccessImpl<T> implements NaturalIdLoadAccess<T> {
-        private final String entityName;
-        private final Class<T> entityClass;
-        private final Map<String, Object> naturalIdParameters = new LinkedHashMap<String, Object>();
-        private LockOptions lockOptions;
-        
-        /**
-         * Note that the specified entity MUST be castable using {@link Class#cast(Object)}
-         * on the specified entityClass
-         */
-        private NaturalIdLoadAccessImpl(String entityName, Class<T> entityClass) {
-            this.entityName = entityName;
-            this.entityClass = entityClass;
-        }
 
-        /* (non-Javadoc)
-         * @see org.hibernate.IdentifierLoadAccess#with(org.hibernate.LockOptions)
-         */
-        @Override
-        public final NaturalIdLoadAccess<T> with(LockOptions lockOptions) {
-            this.lockOptions = lockOptions;
-            return this;
-        }
-        
-        /* (non-Javadoc)
-         * @see org.hibernate.NaturalIdLoadAccess#using(java.lang.String, java.lang.Object)
-         */
-        @Override
-        public NaturalIdLoadAccess<T> using(String attributeName, Object value) {
-            naturalIdParameters.put(attributeName, value);
-            return this;
-        }
-        
-        protected Serializable resolveNaturalId() {
-            final ResolveNaturalIdEvent event = new ResolveNaturalIdEvent(naturalIdParameters, entityName, SessionImpl.this);
-            fireResolveNaturalId(event);
-            return event.getEntityId();
-        }
-        
-        protected IdentifierLoadAccess<T> getIdentifierLoadAccess() {
-            final IdentifierLoadAccessImpl<T> identifierLoadAccess = new SessionImpl.IdentifierLoadAccessImpl<T>(entityName, entityClass);
-            if (this.lockOptions != null) {
-                identifierLoadAccess.with(lockOptions);
-            }
-            return identifierLoadAccess;
-        }
+	private class NaturalIdLoadAccessImpl<T> implements NaturalIdLoadAccess<T> {
+		private final String entityName;
+		private final Class<T> entityClass;
+		private final Map<String, Object> naturalIdParameters = new LinkedHashMap<String, Object>();
+		private LockOptions lockOptions;
 
-        /* (non-Javadoc)
-         * @see org.hibernate.NaturalIdLoadAccess#getReference()
-         */
-        @Override
-        public final Object getReference() {
-            final Serializable entityId = resolveNaturalId();
-            if (entityId == null) {
-                return null;
-            }
-            return this.getIdentifierLoadAccess().getReference(entityId);
-        }
+		private NaturalIdLoadAccessImpl(String entityName, Class<T> entityClass) {
+			this.entityName = entityName;
+			this.entityClass = entityClass;
+		}
 
-        /* (non-Javadoc)
-         * @see org.hibernate.NaturalIdLoadAccess#load()
-         */
-        @Override
-        public final Object load() {
-            final Serializable entityId = resolveNaturalId();
-            if (entityId == null) {
-                return null;
-            }
-            return this.getIdentifierLoadAccess().load(entityId);
-        }
-    }
+		@Override
+		public final NaturalIdLoadAccess<T> with(LockOptions lockOptions) {
+			this.lockOptions = lockOptions;
+			return this;
+		}
+
+		@Override
+		public NaturalIdLoadAccess<T> using(String attributeName, Object value) {
+			naturalIdParameters.put( attributeName, value );
+			return this;
+		}
+
+		protected Serializable resolveNaturalId() {
+			final ResolveNaturalIdEvent event = new ResolveNaturalIdEvent( naturalIdParameters, entityName,
+					SessionImpl.this );
+			fireResolveNaturalId( event );
+			return event.getEntityId();
+		}
+
+		protected IdentifierLoadAccess<T> getIdentifierLoadAccess() {
+			final IdentifierLoadAccessImpl<T> identifierLoadAccess = new SessionImpl.IdentifierLoadAccessImpl<T>(
+					entityName, entityClass );
+			if ( this.lockOptions != null ) {
+				identifierLoadAccess.with( lockOptions );
+			}
+			return identifierLoadAccess;
+		}
+
+		@Override
+		public final Object getReference() {
+			final Serializable entityId = resolveNaturalId();
+			if ( entityId == null ) {
+				return null;
+			}
+			return this.getIdentifierLoadAccess().getReference( entityId );
+		}
+
+		@Override
+		public final Object load() {
+			final Serializable entityId = resolveNaturalId();
+			if ( entityId == null ) {
+				return null;
+			}
+			return this.getIdentifierLoadAccess().load( entityId );
+		}
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 9a43ddabd6..59180165da 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -2393,2238 +2393,2230 @@ public abstract class AbstractEntityPersister
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
 
 		if ( j == 0 && isVersioned() && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 			// this is the root (versioned) table, and we are using version-based
 			// optimistic locking;  if we are not updating the version, also don't
 			// check it (unless this is a "generated" version column)!
 			if ( checkVersion( includeProperty ) ) {
 				update.setVersionColumnName( getVersionColumnName() );
 				hasColumns = true;
 			}
 		}
 		else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 			// we are using "all" or "dirty" property-based optimistic locking
 
 			boolean[] includeInWhere = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
 					? getPropertyUpdateability() //optimistic-lock="all", include all updatable properties
 					: includeProperty; 			 //optimistic-lock="dirty", include all properties we are updating this time
 
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Dehydrating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Hydrating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Inserting entity: {0} (native id)", getEntityName() );
 			if ( isVersioned() ) {
 				LOG.tracev( "Version: {0}", Versioning.getVersion( fields, this ) );
 			}
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Inserting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( j == 0 && isVersioned() )
 				LOG.tracev( "Version: {0}", Versioning.getVersion( fields, this ) );
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Updating entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion )
 				LOG.tracev( "Existing version: {0} -> New version:{1}", oldVersion, fields[getVersionProperty()] );
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
 				if ( useVersion && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 					if ( checkVersion( includeProperty ) ) {
 						getVersionType().nullSafeSet( update, oldVersion, index, session );
 					}
 				}
 				else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 					boolean[] versionability = getPropertyVersionability(); //TODO: is this really necessary????
 					boolean[] includeOldField = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
 							? getPropertyUpdateability()
 							: includeProperty;
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Deleting entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 			if ( useVersion )
 				LOG.tracev( "Version: {0}", version );
 		}
 
 		if ( isTableCascadeDeleteEnabled( j ) ) {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Delete handled by foreign key constraint: {0}", getTableName( j ) );
 			}
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
 				else if ( isAllOrDirtyOptLocking() && loadedState != null ) {
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
 					hasUninitializedLazyProperties( object )
 			);
 			propsToUpdate = getPropertyUpdateability( object );
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
 		boolean isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && isAllOrDirtyOptLocking();
 		Object[] loadedState = null;
 		if ( isImpliedOptimisticLocking ) {
 			// need to treat this as if it where optimistic-lock="all" (dirty does *not* make sense);
 			// first we need to locate the "loaded" state
 			//
 			// Note, it potentially could be a proxy, so doAfterTransactionCompletion the location the safe way...
 			final EntityKey key = session.generateEntityKey( id, this );
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
 
 	private boolean isAllOrDirtyOptLocking() {
 		return entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.DIRTY
 				|| entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL;
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
             if (sqlEntityIdByNaturalIdString != null) LOG.debugf("Id by Natural Id: %s",
-                    sqlEntityIdByNaturalIdString);
+            													 sqlEntityIdByNaturalIdString);
 		}
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		final StringBuilder sessionFilterFragment = new StringBuilder();
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
 						JoinType.INNER_JOIN : //we can inner join to superclass tables (the row MUST be there)
 						JoinType.LEFT_OUTER_JOIN //we can never inner join to subclass tables
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
 					JoinType.LEFT_OUTER_JOIN :
 					JoinType.INNER_JOIN );
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
 		
 		if (hasNaturalIdentifier()) {
 		    sqlEntityIdByNaturalIdString = generateEntityIdByNaturalIdSql();
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
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Fetching entity: {0}", MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
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
 			// restrictions) these need to be next in precedence
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
 	 * @param session The session in which the check is occurring.
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
 				hasUninitializedLazyProperties( entity ),
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
 	 * @param session The session in which the check is occurring.
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
 				hasUninitializedLazyProperties( entity ),
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
 	protected boolean[] getPropertyUpdateability(Object entity) {
 		return hasUninitializedLazyProperties( entity )
 				? getNonLazyPropertyUpdateability()
 				: getPropertyUpdateability();
 	}
 
 	private void logDirtyProperties(int[] props) {
 		if ( LOG.isTraceEnabled() ) {
 			for ( int i = 0; i < props.length; i++ ) {
 				String propertyName = entityMetamodel.getProperties()[ props[i] ].getName();
 				LOG.trace( StringHelper.qualify( getEntityName(), propertyName ) + " is dirty" );
 			}
 		}
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
 		InstrumentationService instrumentationService = session.getFactory()
 				.getServiceRegistry()
 				.getService( InstrumentationService.class );
 		if ( instrumentationService.isInstrumented( entity ) ) {
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
 		final Object version = getVersion( entity );
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
 			CacheKey ck = session.generateCacheKey( id, getIdentifierType(), getRootEntityName() );
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
 		return propertyMapping.toType( propertyName );
 	}
 
 	public Type getType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return entityMetamodel.isSelectBeforeUpdate();
 	}
 
 	protected final OptimisticLockStyle optimisticLockStyle() {
 		return entityMetamodel.getOptimisticLockStyle();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 		return entityMetamodel.getTuplizer().createProxy( id, session );
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
 
 	public boolean isInstrumented() {
 		return getEntityTuplizer().isInstrumented();
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
 		getEntityTuplizer().afterInitialize( entity, lazyPropertiesAreUnfetched, session );
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
 
 	public final Class getMappedClass() {
 		return getEntityTuplizer().getMappedClass();
 	}
 
 	public boolean implementsLifecycle() {
 		return getEntityTuplizer().isLifecycleImplementor();
 	}
 
 	public Class getConcreteProxyClass() {
 		return getEntityTuplizer().getConcreteProxyClass();
 	}
 
 	public void setPropertyValues(Object object, Object[] values) {
 		getEntityTuplizer().setPropertyValues( object, values );
 	}
 
 	public void setPropertyValue(Object object, int i, Object value) {
 		getEntityTuplizer().setPropertyValue( object, i, value );
 	}
 
 	public Object[] getPropertyValues(Object object) {
 		return getEntityTuplizer().getPropertyValues( object );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) {
 		return getEntityTuplizer().getPropertyValue( object, i );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) {
 		return getEntityTuplizer().getPropertyValue( object, propertyName );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) {
 		return getEntityTuplizer().getIdentifier( object, null );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return getEntityTuplizer().getIdentifier( entity, session );
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		getEntityTuplizer().setIdentifier( entity, id, session );
 	}
 
 	@Override
 	public Object getVersion(Object object) {
 		return getEntityTuplizer().getVersion( object );
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		return getEntityTuplizer().instantiate( id, session );
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return getEntityTuplizer().isInstance( object );
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return getEntityTuplizer().hasUninitializedLazyProperties( object );
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		getEntityTuplizer().resetIdentifier( entity, currentId, currentVersion, session );
 	}
 
 	@Override
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		if ( !hasSubclasses() ) {
 			return this;
 		}
 		else {
 			final String concreteEntityName = getEntityTuplizer().determineConcreteSubclassEntityName(
 					instance,
 					factory
 			);
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
 		return getEntityTuplizer().getPropertyValuesToInsert( object, mergeMap, session );
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
 							setPropertyValue( entity, i, state[i] );
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
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Getting current natural-id snapshot state for: {0}",
 					MessageHelper.infoString( this, id, getFactory() ) );
 		}
 
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
 					final EntityKey key = session.generateEntityKey( id, this );
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
     
-    /* (non-Javadoc)
-     * @see org.hibernate.persister.entity.EntityPersister#loadEntityIdByNaturalId(java.util.Map, org.hibernate.engine.spi.SessionImplementor)
-     */
-    @Override
-    public Serializable loadEntityIdByNaturalId(Map<String, ?> naturalIdParameters, LockOptions lockOptions, SessionImplementor session) {
-        if ( !hasNaturalIdentifier() ) {
-            throw new MappingException( "persistent class did not define a natural-id : " + MessageHelper.infoString( this ) );
-        }
-        if (LOG.isTraceEnabled()) LOG.trace("Getting entity id for natural-id for: "
-                                            + MessageHelper.infoString(this, naturalIdParameters, getFactory()));
-
-        try {
-            PreparedStatement ps = session.getTransactionCoordinator()
-                    .getJdbcCoordinator()
-                    .getStatementPreparer()
-                    .prepareStatement( sqlEntityIdByNaturalIdString );
-            try {
-                int positions = 1;
-                final int[] naturalIdPropertyIndexes = this.getNaturalIdentifierProperties();
-                for (int propIdx = 0; propIdx < naturalIdPropertyIndexes.length; propIdx++) {
-                    final int naturalIdIdx = naturalIdPropertyIndexes[propIdx];
-                    
-                    final StandardProperty[] properties = entityMetamodel.getProperties();
-                    final StandardProperty property = properties[naturalIdIdx];
-                    
-                    final Object value = naturalIdParameters.get(property.getName());
-                    
-                    //TODO am I setting the positions var correctly here?
-                    final Type propertyType = property.getType();
-                    propertyType.nullSafeSet( ps, value, positions, session );
-                    positions += propertyType.getColumnSpan(session.getFactory());
-                }
-                ResultSet rs = ps.executeQuery();
-                try {
-                    //if there is no resulting row, return null
-                    if ( !rs.next() ) {
-                        return null;
-                    }
-
-                    //entity ID has to be serializable right?
-                    return (Serializable) getIdentifierType().hydrate(rs, getIdentifierAliases(), session, null);
-                }
-                finally {
-                    rs.close();
-                }
-            }
-            finally {
-                ps.close();
-            }
-        }
-        catch ( SQLException e ) {
-            throw getFactory().getSQLExceptionHelper().convert(
-                    e,
-                    "could not retrieve entity id: " + MessageHelper.infoString( this, naturalIdParameters, getFactory() ),
-                    sqlEntityIdByNaturalIdString
-            );
-        }
-    }
-
-    /**
-     * @return
-     */
-    private String generateEntityIdByNaturalIdSql() {
-        Select select = new Select( getFactory().getDialect() );
-        if ( getFactory().getSettings().isCommentsEnabled() ) {
-            select.setComment( "get current natural-id->entity-id state " + getEntityName() );
-        }
-        
-        final String rootAlias = getRootAlias();
-        
-        select.setSelectClause( identifierSelectFragment(rootAlias, "") );
-        select.setFromClause( fromTableFragment( rootAlias ) + fromJoinFragment( rootAlias, true, false ) );
-        
-        final StringBuilder whereClause = new StringBuilder();
-        final int[] propertyTableNumbers = getPropertyTableNumbers();
-        final int[] naturalIdPropertyIndexes = this.getNaturalIdentifierProperties();
-        for (int propIdx = 0; propIdx < naturalIdPropertyIndexes.length; propIdx++) {
-            if (propIdx > 0) {
-                whereClause.append(" and ");
-            }
-            
-            final int naturalIdIdx = naturalIdPropertyIndexes[propIdx];
-            final String tableAlias = generateTableAlias( rootAlias, propertyTableNumbers[naturalIdIdx] );
-            
-            final String[] propertyColumnNames = getPropertyColumnNames(naturalIdIdx);
-            final String[] aliasedPropertyColumns = StringHelper.qualify( rootAlias, propertyColumnNames );
-            
-            whereClause.append( StringHelper.join( "=? and ", aliasedPropertyColumns ) ).append( "=?" );
-        }
-        
-        whereClause.append( whereJoinFragment( getRootAlias(), true, false ) );
-        
-        String sql = select.setOuterJoins( "", "" )
-                .setWhereClause( whereClause.toString() )
-                .toStatementString();
-        return sql;
-    }
+	@Override
+	public Serializable loadEntityIdByNaturalId(Map<String, ?> naturalIdParameters, LockOptions lockOptions,
+			SessionImplementor session) {
+		if ( !hasNaturalIdentifier() ) {
+			throw new MappingException( "persistent class did not define a natural-id : "
+					+ MessageHelper.infoString( this ) );
+		}
+		if ( LOG.isTraceEnabled() )
+			LOG.trace( "Getting entity id for natural-id for: "
+					+ MessageHelper.infoString( this, naturalIdParameters, getFactory() ) );
+
+		try {
+			PreparedStatement ps = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer()
+					.prepareStatement( sqlEntityIdByNaturalIdString );
+			try {
+				int positions = 1;
+				final int[] naturalIdPropertyIndexes = this.getNaturalIdentifierProperties();
+				for ( int propIdx = 0; propIdx < naturalIdPropertyIndexes.length; propIdx++ ) {
+					final int naturalIdIdx = naturalIdPropertyIndexes[propIdx];
+
+					final StandardProperty[] properties = entityMetamodel.getProperties();
+					final StandardProperty property = properties[naturalIdIdx];
+
+					final Object value = naturalIdParameters.get( property.getName() );
+
+					final Type propertyType = property.getType();
+					propertyType.nullSafeSet( ps, value, positions, session );
+					positions += propertyType.getColumnSpan( session.getFactory() );
+				}
+				ResultSet rs = ps.executeQuery();
+				try {
+					// if there is no resulting row, return null
+					if ( !rs.next() ) {
+						return null;
+					}
+
+					// entity ID has to be serializable right?
+					return (Serializable) getIdentifierType().hydrate( rs, getIdentifierAliases(), session, null );
+				}
+				finally {
+					rs.close();
+				}
+			}
+			finally {
+				ps.close();
+			}
+		}
+		catch ( SQLException e ) {
+			throw getFactory().getSQLExceptionHelper().convert(
+					e,
+					"could not retrieve entity id: "
+							+ MessageHelper.infoString( this, naturalIdParameters, getFactory() ),
+					sqlEntityIdByNaturalIdString );
+		}
+	}
+
+	private String generateEntityIdByNaturalIdSql() {
+		Select select = new Select( getFactory().getDialect() );
+		if ( getFactory().getSettings().isCommentsEnabled() ) {
+			select.setComment( "get current natural-id->entity-id state " + getEntityName() );
+		}
+
+		final String rootAlias = getRootAlias();
+
+		select.setSelectClause( identifierSelectFragment( rootAlias, "" ) );
+		select.setFromClause( fromTableFragment( rootAlias ) + fromJoinFragment( rootAlias, true, false ) );
+
+		final StringBuilder whereClause = new StringBuilder();
+		final int[] propertyTableNumbers = getPropertyTableNumbers();
+		final int[] naturalIdPropertyIndexes = this.getNaturalIdentifierProperties();
+		for ( int propIdx = 0; propIdx < naturalIdPropertyIndexes.length; propIdx++ ) {
+			if ( propIdx > 0 ) {
+				whereClause.append( " and " );
+			}
+
+			final int naturalIdIdx = naturalIdPropertyIndexes[propIdx];
+			final String tableAlias = generateTableAlias( rootAlias, propertyTableNumbers[naturalIdIdx] );
+
+			final String[] propertyColumnNames = getPropertyColumnNames( naturalIdIdx );
+			final String[] aliasedPropertyColumns = StringHelper.qualify( rootAlias, propertyColumnNames );
+
+			whereClause.append( StringHelper.join( "=? and ", aliasedPropertyColumns ) ).append( "=?" );
+		}
+
+		whereClause.append( whereJoinFragment( getRootAlias(), true, false ) );
+
+		String sql = select.setOuterJoins( "", "" ).setWhereClause( whereClause.toString() ).toStatementString();
+		return sql;
+	}
 
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
 
 	public void setPropertyValue(Object object, String propertyName, Object value) {
 		getEntityTuplizer().setPropertyValue( object, propertyName, value );
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return entityMetamodel.getEntityMode();
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return entityTuplizer;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
index 6b9ac1fa9e..86f353adfb 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
@@ -1,735 +1,736 @@
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
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.OptimisticCacheSource;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * Implementors define mapping and persistence logic for a particular
  * strategy of entity mapping.  An instance of entity persisters corresponds
  * to a given mapped entity.
  * <p/>
  * Implementors must be threadsafe (preferrably immutable) and must provide a constructor
  * matching the signature of: {@link org.hibernate.mapping.PersistentClass}, {@link org.hibernate.engine.spi.SessionFactoryImplementor}
  *
  * @author Gavin King
  */
 public interface EntityPersister extends OptimisticCacheSource {
 
 	/**
 	 * The property name of the "special" identifier property in HQL
 	 */
 	public static final String ENTITY_ID = "id";
 
 	/**
 	 * Finish the initialization of this object.
 	 * <p/>
 	 * Called only once per {@link org.hibernate.SessionFactory} lifecycle,
 	 * after all entity persisters have been instantiated.
 	 *
 	 * @throws org.hibernate.MappingException Indicates an issue in the metadata.
 	 */
 	public void postInstantiate() throws MappingException;
 
 	/**
 	 * Return the SessionFactory to which this persister "belongs".
 	 *
 	 * @return The owning SessionFactory.
 	 */
 	public SessionFactoryImplementor getFactory();
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     // stuff that is persister-centric and/or EntityInfo-centric ~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Returns an object that identifies the space in which identifiers of
 	 * this entity hierarchy are unique.  Might be a table name, a JNDI URL, etc.
 	 *
 	 * @return The root entity name.
 	 */
 	public String getRootEntityName();
 
 	/**
 	 * The entity name which this persister maps.
 	 *
 	 * @return The name of the entity which this persister maps.
 	 */
 	public String getEntityName();
 
 	/**
 	 * Retrieve the underlying entity metamodel instance...
 	 *
 	 *@return The metamodel
 	 */
 	public EntityMetamodel getEntityMetamodel();
 
 	/**
 	 * Determine whether the given name represents a subclass entity
 	 * (or this entity itself) of the entity mapped by this persister.
 	 *
 	 * @param entityName The entity name to be checked.
 	 * @return True if the given entity name represents either the entity
 	 * mapped by this persister or one of its subclass entities; false
 	 * otherwise.
 	 */
 	public boolean isSubclassEntityName(String entityName);
 
 	/**
 	 * Returns an array of objects that identify spaces in which properties of
 	 * this entity are persisted, for instances of this class only.
 	 * <p/>
 	 * For most implementations, this returns the complete set of table names
 	 * to which instances of the mapped entity are persisted (not accounting
 	 * for superclass entity mappings).
 	 *
 	 * @return The property spaces.
 	 */
 	public Serializable[] getPropertySpaces();
 
 	/**
 	 * Returns an array of objects that identify spaces in which properties of
 	 * this entity are persisted, for instances of this class and its subclasses.
 	 * <p/>
 	 * Much like {@link #getPropertySpaces()}, except that here we include subclass
 	 * entity spaces.
 	 *
 	 * @return The query spaces.
 	 */
 	public Serializable[] getQuerySpaces();
 
 	/**
 	 * Determine whether this entity supports dynamic proxies.
 	 *
 	 * @return True if the entity has dynamic proxy support; false otherwise.
 	 */
 	public boolean hasProxy();
 
 	/**
 	 * Determine whether this entity contains references to persistent collections.
 	 *
 	 * @return True if the entity does contain persistent collections; false otherwise.
 	 */
 	public boolean hasCollections();
 
 	/**
 	 * Determine whether any properties of this entity are considered mutable.
 	 *
 	 * @return True if any properties of the entity are mutable; false otherwise (meaning none are).
 	 */
 	public boolean hasMutableProperties();
 
 	/**
 	 * Determine whether this entity contains references to persistent collections
 	 * which are fetchable by subselect?
 	 *
 	 * @return True if the entity contains collections fetchable by subselect; false otherwise.
 	 */
 	public boolean hasSubselectLoadableCollections();
 
 	/**
 	 * Determine whether this entity has any non-none cascading.
 	 *
 	 * @return True if the entity has any properties with a cascade other than NONE;
 	 * false otherwise (aka, no cascading).
 	 */
 	public boolean hasCascades();
 
 	/**
 	 * Determine whether instances of this entity are considered mutable.
 	 *
 	 * @return True if the entity is considered mutable; false otherwise.
 	 */
 	public boolean isMutable();
 
 	/**
 	 * Determine whether the entity is inherited one or more other entities.
 	 * In other words, is this entity a subclass of other entities.
 	 *
 	 * @return True if other entities extend this entity; false otherwise.
 	 */
 	public boolean isInherited();
 
 	/**
 	 * Are identifiers of this entity assigned known before the insert execution?
 	 * Or, are they generated (in the database) by the insert execution.
 	 *
 	 * @return True if identifiers for this entity are generated by the insert
 	 * execution.
 	 */
 	public boolean isIdentifierAssignedByInsert();
 
 	/**
 	 * Get the type of a particular property by name.
 	 *
 	 * @param propertyName The name of the property for which to retrieve
 	 * the type.
 	 * @return The type.
 	 * @throws org.hibernate.MappingException Typically indicates an unknown
 	 * property name.
 	 */
 	public Type getPropertyType(String propertyName) throws MappingException;
 
 	/**
 	 * Compare the two snapshots to determine if they represent dirty state.
 	 *
 	 * @param currentState The current snapshot
 	 * @param previousState The baseline snapshot
 	 * @param owner The entity containing the state
 	 * @param session The originating session
 	 * @return The indices of all dirty properties, or null if no properties
 	 * were dirty.
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object owner, SessionImplementor session);
 
 	/**
 	 * Compare the two snapshots to determine if they represent modified state.
 	 *
 	 * @param old The baseline snapshot
 	 * @param current The current snapshot
 	 * @param object The entity containing the state
 	 * @param session The originating session
 	 * @return The indices of all modified properties, or null if no properties
 	 * were modified.
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session);
 
 	/**
 	 * Determine whether the entity has a particular property holding
 	 * the identifier value.
 	 *
 	 * @return True if the entity has a specific property holding identifier value.
 	 */
 	public boolean hasIdentifierProperty();
 
 	/**
 	 * Determine whether detached instances of this entity carry their own
 	 * identifier value.
 	 * <p/>
 	 * The other option is the deprecated feature where users could supply
 	 * the id during session calls.
 	 *
 	 * @return True if either (1) {@link #hasIdentifierProperty()} or
 	 * (2) the identifier is an embedded composite identifier; false otherwise.
 	 */
 	public boolean canExtractIdOutOfEntity();
 
 	/**
 	 * Determine whether optimistic locking by column is enabled for this
 	 * entity.
 	 *
 	 * @return True if optimistic locking by column (i.e., <version/> or
 	 * <timestamp/>) is enabled; false otherwise.
 	 */
 	public boolean isVersioned();
 
 	/**
 	 * If {@link #isVersioned()}, then what is the type of the property
 	 * holding the locking value.
 	 *
 	 * @return The type of the version property; or null, if not versioned.
 	 */
 	public VersionType getVersionType();
 
 	/**
 	 * If {@link #isVersioned()}, then what is the index of the property
 	 * holding the locking value.
 	 *
 	 * @return The type of the version property; or -66, if not versioned.
 	 */
 	public int getVersionProperty();
 
 	/**
 	 * Determine whether this entity defines a natural identifier.
 	 *
 	 * @return True if the entity defines a natural id; false otherwise.
 	 */
 	public boolean hasNaturalIdentifier();
 
 	/**
 	 * If the entity defines a natural id ({@link #hasNaturalIdentifier()}), which
 	 * properties make up the natural id.
 	 *
 	 * @return The indices of the properties making of the natural id; or
 	 * null, if no natural id is defined.
 	 */
 	public int[] getNaturalIdentifierProperties();
 
 	/**
 	 * Retrieve the current state of the natural-id properties from the database.
 	 *
 	 * @param id The identifier of the entity for which to retrieve the natural-id values.
 	 * @param session The session from which the request originated.
 	 * @return The natural-id snapshot.
 	 */
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session);
 
 	/**
 	 * Determine which identifier generation strategy is used for this entity.
 	 *
 	 * @return The identifier generation strategy.
 	 */
 	public IdentifierGenerator getIdentifierGenerator();
 
 	/**
 	 * Determine whether this entity defines any lazy properties (ala
 	 * bytecode instrumentation).
 	 *
 	 * @return True if the entity has properties mapped as lazy; false otherwise.
 	 */
 	public boolean hasLazyProperties();
-	
+
 	/**
 	 * Load the id for the entity based on the natural id.
 	 */
-	public Serializable loadEntityIdByNaturalId(Map<String, ?> naturalIdParameters, LockOptions lockOptions, SessionImplementor session);
+	public Serializable loadEntityIdByNaturalId(Map<String, ?> naturalIdParameters, LockOptions lockOptions,
+			SessionImplementor session);
 
 	/**
 	 * Load an instance of the persistent class.
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Load an instance of the persistent class.
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Do a version check (optional operation)
 	 */
 	public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Do a version check (optional operation)
 	 */
 	public void lock(Serializable id, Object version, Object object, LockOptions lockOptions, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Persist an instance
 	 */
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Persist an instance, using a natively generated identifier (optional operation)
 	 */
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Delete a persistent instance
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Update a persistent instance
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
 	) throws HibernateException;
 
 	/**
 	 * Get the Hibernate types of the class properties
 	 */
 	public Type[] getPropertyTypes();
 
 	/**
 	 * Get the names of the class properties - doesn't have to be the names of the
 	 * actual Java properties (used for XML generation only)
 	 */
 	public String[] getPropertyNames();
 
 	/**
 	 * Get the "insertability" of the properties of this class
 	 * (does the property appear in an SQL INSERT)
 	 */
 	public boolean[] getPropertyInsertability();
 
 	/**
 	 * Which of the properties of this class are database generated values on insert?
 	 */
 	public ValueInclusion[] getPropertyInsertGenerationInclusions();
 
 	/**
 	 * Which of the properties of this class are database generated values on update?
 	 */
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions();
 
 	/**
 	 * Get the "updateability" of the properties of this class
 	 * (does the property appear in an SQL UPDATE)
 	 */
 	public boolean[] getPropertyUpdateability();
 
 	/**
 	 * Get the "checkability" of the properties of this class
 	 * (is the property dirty checked, does the cache need
 	 * to be updated)
 	 */
 	public boolean[] getPropertyCheckability();
 
 	/**
 	 * Get the nullability of the properties of this class
 	 */
 	public boolean[] getPropertyNullability();
 
 	/**
 	 * Get the "versionability" of the properties of this class
 	 * (is the property optimistic-locked)
 	 */
 	public boolean[] getPropertyVersionability();
 	public boolean[] getPropertyLaziness();
 	/**
 	 * Get the cascade styles of the properties (optional operation)
 	 */
 	public CascadeStyle[] getPropertyCascadeStyles();
 
 	/**
 	 * Get the identifier type
 	 */
 	public Type getIdentifierType();
 
 	/**
 	 * Get the name of the identifier property (or return null) - need not return the
 	 * name of an actual Java property
 	 */
 	public String getIdentifierPropertyName();
 
 	/**
 	 * Should we always invalidate the cache instead of
 	 * recaching updated state
 	 */
 	public boolean isCacheInvalidationRequired();
 	/**
 	 * Should lazy properties of this entity be cached?
 	 */
 	public boolean isLazyPropertiesCacheable();
 	/**
 	 * Does this class have a cache.
 	 */
 	public boolean hasCache();
 	/**
 	 * Get the cache (optional operation)
 	 */
 	public EntityRegionAccessStrategy getCacheAccessStrategy();
 	/**
 	 * Get the cache structure
 	 */
 	public CacheEntryStructure getCacheEntryStructure();
 
 	/**
 	 * Get the user-visible metadata for the class (optional operation)
 	 */
 	public ClassMetadata getClassMetadata();
 
 	/**
 	 * Is batch loading enabled?
 	 */
 	public boolean isBatchLoadable();
 
 	/**
 	 * Is select snapshot before update enabled?
 	 */
 	public boolean isSelectBeforeUpdateRequired();
 
 	/**
 	 * Get the current database state of the object, in a "hydrated" form, without
 	 * resolving identifiers
 	 * @return null if there is no row in the database
 	 */
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Get the current version of the object, or return null if there is no row for
 	 * the given identifier. In the case of unversioned data, return any object
 	 * if the row exists.
 	 */
 	public Object getCurrentVersion(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Has the class actually been bytecode instrumented?
 	 */
 	public boolean isInstrumented();
 
 	/**
 	 * Does this entity define any properties as being database generated on insert?
 	 *
 	 * @return True if this entity contains at least one property defined
 	 * as generated (including version property, but not identifier).
 	 */
 	public boolean hasInsertGeneratedProperties();
 
 	/**
 	 * Does this entity define any properties as being database generated on update?
 	 *
 	 * @return True if this entity contains at least one property defined
 	 * as generated (including version property, but not identifier).
 	 */
 	public boolean hasUpdateGeneratedProperties();
 
 	/**
 	 * Does this entity contain a version property that is defined
 	 * to be database generated?
 	 *
 	 * @return true if this entity contains a version property and that
 	 * property has been marked as generated.
 	 */
 	public boolean isVersionPropertyGenerated();
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff that is tuplizer-centric, but is passed a session ~~~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Called just after the entities properties have been initialized
 	 */
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session);
 
 	/**
 	 * Called just after the entity has been reassociated with the session
 	 */
 	public void afterReassociate(Object entity, SessionImplementor session);
 
 	/**
 	 * Create a new proxy instance
 	 */
 	public Object createProxy(Serializable id, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Is this a new transient instance?
 	 */
 	public Boolean isTransient(Object object, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Return the values of the insertable properties of the object (including backrefs)
 	 */
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Perform a select to retrieve the values of any generated properties
 	 * back from the database, injecting these generated values into the
 	 * given entity as well as writing this state to the
 	 * {@link org.hibernate.engine.spi.PersistenceContext}.
 	 * <p/>
 	 * Note, that because we update the PersistenceContext here, callers
 	 * need to take care that they have already written the initial snapshot
 	 * to the PersistenceContext before calling this method.
 	 *
 	 * @param id The entity's id value.
 	 * @param entity The entity for which to get the state.
 	 * @param state
 	 * @param session The session
 	 */
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session);
 	/**
 	 * Perform a select to retrieve the values of any generated properties
 	 * back from the database, injecting these generated values into the
 	 * given entity as well as writing this state to the
 	 * {@link org.hibernate.engine.spi.PersistenceContext}.
 	 * <p/>
 	 * Note, that because we update the PersistenceContext here, callers
 	 * need to take care that they have already written the initial snapshot
 	 * to the PersistenceContext before calling this method.
 	 *
 	 * @param id The entity's id value.
 	 * @param entity The entity for which to get the state.
 	 * @param state
 	 * @param session The session
 	 */
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session);
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff that is Tuplizer-centric ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The persistent class, or null
 	 */
 	public Class getMappedClass();
 
 	/**
 	 * Does the class implement the {@link org.hibernate.classic.Lifecycle} interface.
 	 */
 	public boolean implementsLifecycle();
 
 	/**
 	 * Get the proxy interface that instances of <em>this</em> concrete class will be
 	 * cast to (optional operation).
 	 */
 	public Class getConcreteProxyClass();
 
 	/**
 	 * Set the given values to the mapped properties of the given object
 	 */
 	public void setPropertyValues(Object object, Object[] values);
 
 	/**
 	 * Set the value of a particular property
 	 */
 	public void setPropertyValue(Object object, int i, Object value);
 
 	/**
 	 * Return the (loaded) values of the mapped properties of the object (not including backrefs)
 	 */
 	public Object[] getPropertyValues(Object object);
 
 	/**
 	 * Get the value of a particular property
 	 */
 	public Object getPropertyValue(Object object, int i) throws HibernateException;
 
 	/**
 	 * Get the value of a particular property
 	 */
 	public Object getPropertyValue(Object object, String propertyName);
 
 	/**
 	 * Get the identifier of an instance (throw an exception if no identifier property)
 	 *
 	 * @deprecated Use {@link #getIdentifier(Object,SessionImplementor)} instead
 	 * @noinspection JavaDoc
 	 */
 	public Serializable getIdentifier(Object object) throws HibernateException;
 
 	/**
 	 * Get the identifier of an instance (throw an exception if no identifier property)
 	 *
 	 * @param entity The entity for which to get the identifier
 	 * @param session The session from which the request originated
 	 *
 	 * @return The identifier
 	 */
 	public Serializable getIdentifier(Object entity, SessionImplementor session);
 
     /**
      * Inject the identifier value into the given entity.
      *
      * @param entity The entity to inject with the identifier value.
      * @param id The value to be injected as the identifier.
 	 * @param session The session from which is requests originates
      */
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session);
 
 	/**
 	 * Get the version number (or timestamp) from the object's version property (or return null if not versioned)
 	 */
 	public Object getVersion(Object object) throws HibernateException;
 
 	/**
 	 * Create a class instance initialized with the given identifier
 	 *
 	 * @param id The identifier value to use (may be null to represent no value)
 	 * @param session The session from which the request originated.
 	 *
 	 * @return The instantiated entity.
 	 */
 	public Object instantiate(Serializable id, SessionImplementor session);
 
 	/**
 	 * Is the given object an instance of this entity?
 	 */
 	public boolean isInstance(Object object);
 
 	/**
 	 * Does the given instance have any uninitialized lazy properties?
 	 */
 	public boolean hasUninitializedLazyProperties(Object object);
 
 	/**
 	 * Set the identifier and version of the given instance back to its "unsaved" value.
 	 *
 	 * @param entity The entity instance
 	 * @param currentId The currently assigned identifier value.
 	 * @param currentVersion The currently assigned version value.
 	 * @param session The session from which the request originated.
 	 */
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session);
 
 	/**
 	 * A request has already identified the entity-name of this persister as the mapping for the given instance.
 	 * However, we still need to account for possible subclassing and potentially re-route to the more appropriate
 	 * persister.
 	 * <p/>
 	 * For example, a request names <tt>Animal</tt> as the entity-name which gets resolved to this persister.  But the
 	 * actual instance is really an instance of <tt>Cat</tt> which is a subclass of <tt>Animal</tt>.  So, here the
 	 * <tt>Animal</tt> persister is being asked to return the persister specific to <tt>Cat</tt>.
 	 * <p/>
 	 * It is also possible that the instance is actually an <tt>Animal</tt> instance in the above example in which
 	 * case we would return <tt>this</tt> from this method.
 	 *
 	 * @param instance The entity instance
 	 * @param factory Reference to the SessionFactory
 	 *
 	 * @return The appropriate persister
 	 *
 	 * @throws HibernateException Indicates that instance was deemed to not be a subclass of the entity mapped by
 	 * this persister.
 	 */
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory);
 
 	public EntityMode getEntityMode();
 	public EntityTuplizer getEntityTuplizer();
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/jpa/naturalid/ImmutableNaturalIdTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/jpa/naturalid/ImmutableNaturalIdTest.java
index 3d12299759..8e25e10517 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/jpa/naturalid/ImmutableNaturalIdTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/jpa/naturalid/ImmutableNaturalIdTest.java
@@ -1,326 +1,321 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.test.jpa.naturalid;
 
 import org.junit.Test;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.criterion.Restrictions;
 import org.hibernate.test.jpa.AbstractJPATest;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertNull;
 import static org.junit.Assert.fail;
 
 /**
  * copied from {@link org.hibernate.test.naturalid.immutable.ImmutableNaturalIdTest}
  *
  * @author Steve Ebersole
  */
 public class ImmutableNaturalIdTest extends AbstractJPATest {
 	@Override
 	public String[] getMappings() {
 		return new String[] { "jpa/naturalid/User.hbm.xml" };
 	}
 
 	@Override
 	public void configure(Configuration cfg) {
 		super.configure( cfg );
 		cfg.setProperty( Environment.USE_SECOND_LEVEL_CACHE, "true" );
 		cfg.setProperty( Environment.USE_QUERY_CACHE, "true" );
 		cfg.setProperty( Environment.GENERATE_STATISTICS, "true" );
 	}
 
 	@Test
 	public void testUpdate() {
 		// prepare some test data...
 		Session session = openSession();
     	session.beginTransaction();
 	  	User user = new User();
     	user.setUserName( "steve" );
     	user.setEmail( "steve@hibernate.org" );
     	user.setPassword( "brewhaha" );
 		session.save( user );
     	session.getTransaction().commit();
     	session.close();
 
 		// 'user' is now a detached entity, so lets change a property and reattch...
 		user.setPassword( "homebrew" );
 		session = openSession();
 		session.beginTransaction();
 		session.update( user );
 		session.getTransaction().commit();
 		session.close();
 
 		// clean up
 		session = openSession();
 		session.beginTransaction();
 		session.delete( user );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testNaturalIdCheck() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		u.setUserName( "Steve" );
 		try {
 			s.flush();
 			fail();
 		}
 		catch ( HibernateException he ) {
 		}
 		u.setUserName( "steve" );
 		s.delete( u );
 		t.commit();
 		s.close();
 	}
-	
-
-
-    @Test
-    public void testNaturalIdLoadAccessCache() {
-        Session s = openSession();
-        s.beginTransaction();
-        User u = new User( "steve", "superSecret" );
-        s.persist( u );
-        s.getTransaction().commit();
-        s.close();
-
-        sessionFactory().getStatistics().clear();
-//        sessionFactory().getStatistics().logSummary();
-
-        s = openSession();
-        s.beginTransaction();
-        u = ( User )s.byNaturalId(User.class).using( "userName", "steve" ).load();
-        assertNotNull( u );
-        s.getTransaction().commit();
-        s.close();
-        
-//        sessionFactory().getStatistics().logSummary();
-
-        assertEquals( 1, sessionFactory().getStatistics().getEntityLoadCount() );
-        assertEquals( 0, sessionFactory().getStatistics().getSecondLevelCacheMissCount() );
-        assertEquals( 0, sessionFactory().getStatistics().getSecondLevelCacheHitCount() );
-        assertEquals( 0, sessionFactory().getStatistics().getSecondLevelCachePutCount() );
-        assertEquals( 0, sessionFactory().getStatistics().getQueryExecutionCount() );
-        assertEquals( 0, sessionFactory().getStatistics().getQueryCacheHitCount() );
-        assertEquals( 0, sessionFactory().getStatistics().getQueryCachePutCount() );
-
-        s = openSession();
-        s.beginTransaction();
-        User v = new User( "gavin", "supsup" );
-        s.persist( v );
-        s.getTransaction().commit();
-        s.close();
-
-        sessionFactory().getStatistics().clear();
-
-        s = openSession();
-        s.beginTransaction();
-        u = ( User )s.byNaturalId(User.class).using( "userName", "steve" ).load();
-        assertNotNull( u );
-        assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-        assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-        u = ( User )s.byNaturalId(User.class).using( "userName", "steve" ).load();
-        assertNotNull( u );
-        assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
-        assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
-        s.getTransaction().commit();
-        s.close();
-
-        s = openSession();
-        s.beginTransaction();
-        s.createQuery( "delete User" ).executeUpdate();
-        s.getTransaction().commit();
-        s.close();
-    }
+
+	@Test
+	public void testNaturalIdLoadAccessCache() {
+		Session s = openSession();
+		s.beginTransaction();
+		User u = new User( "steve", "superSecret" );
+		s.persist( u );
+		s.getTransaction().commit();
+		s.close();
+
+		sessionFactory().getStatistics().clear();
+
+		s = openSession();
+		s.beginTransaction();
+		u = (User) s.byNaturalId( User.class ).using( "userName", "steve" ).load();
+		assertNotNull( u );
+		s.getTransaction().commit();
+		s.close();
+
+		assertEquals( 1, sessionFactory().getStatistics().getEntityLoadCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getSecondLevelCacheMissCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getSecondLevelCacheHitCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getSecondLevelCachePutCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getQueryExecutionCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getQueryCacheHitCount() );
+		assertEquals( 0, sessionFactory().getStatistics().getQueryCachePutCount() );
+
+		s = openSession();
+		s.beginTransaction();
+		User v = new User( "gavin", "supsup" );
+		s.persist( v );
+		s.getTransaction().commit();
+		s.close();
+
+		sessionFactory().getStatistics().clear();
+
+		s = openSession();
+		s.beginTransaction();
+		u = (User) s.byNaturalId( User.class ).using( "userName", "steve" ).load();
+		assertNotNull( u );
+		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
+		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
+		u = (User) s.byNaturalId( User.class ).using( "userName", "steve" ).load();
+		assertNotNull( u );
+		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
+		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
+		s.getTransaction().commit();
+		s.close();
+
+		s = openSession();
+		s.beginTransaction();
+		s.createQuery( "delete User" ).executeUpdate();
+		s.getTransaction().commit();
+		s.close();
+	}
 
 	@Test
 	public void testNaturalIdCache() {
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		s.getTransaction().commit();
 		s.close();
 
 		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
 		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
 		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 1 );
 
 		s = openSession();
 		s.beginTransaction();
 		User v = new User( "gavin", "supsup" );
 		s.persist( v );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
 		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 1 );
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
 		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.createQuery( "delete User" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNaturalIdDeleteUsingCache() {
 		Session s = openSession();
 		s.beginTransaction();
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		s.getTransaction().commit();
 		s.close();
 
 		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
 		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
 		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 1 );
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
 		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 1 );
 
 		s.delete( u );
 
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
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
 		User u = new User( "steve", "superSecret" );
 		s.persist( u );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().getStatistics().clear();
 
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 
 		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 1 );
 		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 0 );
 		assertEquals( sessionFactory().getStatistics().getQueryCachePutCount(), 1 );
 
 		sessionFactory().getStatistics().clear();
 		s.getTransaction().commit();
 		s.close();
 		s = openSession();
 		s.beginTransaction();
 		u = ( User ) s.createCriteria( User.class )
 				.add( Restrictions.naturalId().set( "userName", "steve" ) )
 				.setCacheable( true )
 				.uniqueResult();
 		assertNotNull( u );
 		assertEquals( sessionFactory().getStatistics().getQueryExecutionCount(), 0 );
 		assertEquals( sessionFactory().getStatistics().getQueryCacheHitCount(), 1 );
 
 		s.delete( u );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
index 113d676780..1c02fb18cb 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/cfg/persister/GoofyPersisterClassProvider.java
@@ -1,775 +1,775 @@
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
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
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
-		
-        @Override
-        public Serializable loadEntityIdByNaturalId(Map<String, ?> naturalIdParameters, LockOptions lockOptions,
-                SessionImplementor session) {
-            return null;
-        }
-
-        @Override
+
+		@Override
+		public Serializable loadEntityIdByNaturalId(Map<String, ?> naturalIdParameters, LockOptions lockOptions,
+				SessionImplementor session) {
+			return null;
+		}
+
+		@Override
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
 		public CacheEntryStructure getCacheEntryStructure() {
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
index d70ea99c0a..ff3254d1c0 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/CustomPersister.java
@@ -1,630 +1,630 @@
 package org.hibernate.test.legacy;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Hashtable;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDHexGenerator;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.StandardBasicTypes;
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
 
 	public boolean isInherited() {
 		return false;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
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
 		return ( (Custom) object ).id==null;
 	}
 
 	@Override
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) {
 		return getPropertyValues( object );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 	}
 
 	public void retrieveGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean implementsLifecycle() {
 		return false;
 	}
 
 	@Override
 	public Class getConcreteProxyClass() {
 		return Custom.class;
 	}
 
 	@Override
 	public void setPropertyValues(Object object, Object[] values) {
 		setPropertyValue( object, 0, values[0] );
 	}
 
 	@Override
 	public void setPropertyValue(Object object, int i, Object value) {
 		( (Custom) object ).setName( (String) value );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object object) throws HibernateException {
 		Custom c = (Custom) object;
 		return new Object[] { c.getName() };
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) throws HibernateException {
 		return ( (Custom) object ).getName();
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		return ( (Custom) object ).id;
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return ( (Custom) entity ).id;
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		( (Custom) entity ).id = (String) id;
 	}
 
 	@Override
 	public Object getVersion(Object object) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		Custom c = new Custom();
 		c.id = (String) id;
 		return c;
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return object instanceof Custom;
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return false;
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		( ( Custom ) entity ).id = ( String ) currentId;
 	}
 
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	public int[] findDirty(
 		Object[] x,
 		Object[] y,
 		Object owner,
 		SessionImplementor session) throws HibernateException {
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
 		SessionImplementor session) throws HibernateException {
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
 					session.generateEntityKey( id, this ),
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
 
 	private static final Type[] TYPES = new Type[] { StandardBasicTypes.STRING };
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
 		return StandardBasicTypes.STRING;
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
 
 	@Override
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session)
 			throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public boolean isCacheInvalidationRequired() {
 		return false;
 	}
 
 	@Override
 	public void afterInitialize(Object entity, boolean fetched, SessionImplementor session) {
 	}
 
 	@Override
 	public void afterReassociate(Object entity, SessionImplementor session) {
 	}
 
 	@Override
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
 
 	@Override
 	public boolean[] getPropertyVersionability() {
 		return MUTABILITY;
 	}
 
 	@Override
 	public CacheEntryStructure getCacheEntryStructure() {
 		return new UnstructuredCacheEntry();
 	}
 
 	@Override
 	public boolean hasSubselectLoadableCollections() {
 		return false;
 	}
 
 	@Override
 	public int[] getNaturalIdentifierProperties() {
 		return null;
 	}
 
 	@Override
 	public boolean hasNaturalIdentifier() {
 		return false;
 	}
 
 	@Override
 	public boolean hasMutableProperties() {
 		return false;
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
 	public boolean[] getPropertyLaziness() {
 		return null;
 	}
 
 	@Override
 	public boolean isLazyPropertiesCacheable() {
 		return true;
 	}
 
 	@Override
 	public boolean isVersionPropertyGenerated() {
 		return false;
 	}
 
 	@Override
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		return null;
 	}
-	
-    @Override
-    public Serializable loadEntityIdByNaturalId(Map<String, ?> naturalIdParameters, LockOptions lockOptions,
-            SessionImplementor session) {
-        return null;
-    }
-
-    @Override
+
+	@Override
+	public Serializable loadEntityIdByNaturalId(Map<String, ?> naturalIdParameters, LockOptions lockOptions,
+			SessionImplementor session) {
+		return null;
+	}
+
+	@Override
 	public Comparator getVersionComparator() {
 		return null;
 	}
 
 	@Override
 	public EntityMetamodel getEntityMetamodel() {
 		return null;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return null;
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
index bcd202a0f0..489c38377b 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/ejb3configuration/PersisterClassProviderTest.java
@@ -1,571 +1,571 @@
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
 package org.hibernate.ejb.test.ejb3configuration;
 
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.PersistenceException;
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Map;
 
 import org.junit.Assert;
 import org.junit.Test;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.ejb.Ejb3Configuration;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.internal.PersisterClassResolverInitiator;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Emmanuel Bernard <emmanuel@hibernate.org>
  */
 public class PersisterClassProviderTest {
     @Test
 	public void testPersisterClassProvider() {
 		Ejb3Configuration conf = new Ejb3Configuration();
 		conf.getProperties().put( PersisterClassResolverInitiator.IMPL_NAME, GoofyPersisterClassProvider.class );
 		conf.addAnnotatedClass( Bell.class );
 		try {
 			final EntityManagerFactory entityManagerFactory = conf.buildEntityManagerFactory();
 			entityManagerFactory.close();
 		}
 		catch ( PersistenceException e ) {
             Assert.assertNotNull( e.getCause() );
 			Assert.assertNotNull( e.getCause().getCause() );
 			Assert.assertEquals( GoofyException.class, e.getCause().getCause().getClass() );
 
 		}
 	}
 
 	public static class GoofyPersisterClassProvider implements PersisterClassResolver {
 		@Override
 		public Class<? extends EntityPersister> getEntityPersisterClass(PersistentClass metadata) {
 			return GoofyProvider.class;
 		}
 
 		@Override
 		public Class<? extends EntityPersister> getEntityPersisterClass(EntityBinding metadata) {
 			return GoofyProvider.class;
 		}
 
 		@Override
 		public Class<? extends CollectionPersister> getCollectionPersisterClass(Collection metadata) {
 			return null;
 		}
 
 		@Override
 		public Class<? extends CollectionPersister> getCollectionPersisterClass(PluralAttributeBinding metadata) {
 			return null;
 		}
 	}
 
 	public static class GoofyProvider implements EntityPersister {
 
 		@SuppressWarnings( {"UnusedParameters"})
 		public GoofyProvider(
 				org.hibernate.mapping.PersistentClass persistentClass,
 				org.hibernate.cache.spi.access.EntityRegionAccessStrategy strategy,
 				SessionFactoryImplementor sf,
 				Mapping mapping) {
 			throw new GoofyException();
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
-		
-        @Override
-        public Serializable loadEntityIdByNaturalId(Map<String, ?> naturalIdParameters, LockOptions lockOptions,
-                SessionImplementor session) {
-            return null;
-        }
+
+		@Override
+		public Serializable loadEntityIdByNaturalId(Map<String, ?> naturalIdParameters, LockOptions lockOptions,
+				SessionImplementor session) {
+			return null;
+		}
 
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
 		public CacheEntryStructure getCacheEntryStructure() {
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
 	}
 
 	public static class GoofyException extends RuntimeException {
 
 	}
 }
