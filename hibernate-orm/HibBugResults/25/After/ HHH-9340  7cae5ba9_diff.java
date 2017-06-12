diff --git a/hibernate-core/src/main/java/org/hibernate/ScrollableResults.java b/hibernate-core/src/main/java/org/hibernate/ScrollableResults.java
index 012ebe789a..0242684190 100644
--- a/hibernate-core/src/main/java/org/hibernate/ScrollableResults.java
+++ b/hibernate-core/src/main/java/org/hibernate/ScrollableResults.java
@@ -1,358 +1,360 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate;
+
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Locale;
 import java.util.TimeZone;
 
 import org.hibernate.type.Type;
 
 /**
  * A result iterator that allows moving around within the results
  * by arbitrary increments. The <tt>Query</tt> / <tt>ScrollableResults</tt>
  * pattern is very similar to the JDBC <tt>PreparedStatement</tt>/
  * <tt>ResultSet</tt> pattern and the semantics of methods of this interface
  * are similar to the similarly named methods on <tt>ResultSet</tt>.<br>
  * <br>
  * Contrary to JDBC, columns of results are numbered from zero.
  *
  * @see Query#scroll()
  *
  * @author Gavin King
  */
-public interface ScrollableResults extends java.io.Closeable {
+public interface ScrollableResults extends AutoCloseable {
+
+	/**
+	 * Release resources immediately.
+	 */
+	void close();
+
 	/**
 	 * Advance to the next result.
 	 *
 	 * @return {@code true} if there is another result
 	 */
-	public boolean next();
+	boolean next();
 
 	/**
 	 * Retreat to the previous result.
 	 *
 	 * @return {@code true} if there is a previous result
 	 */
-	public boolean previous();
+	boolean previous();
 
 	/**
 	 * Scroll the specified number of positions from the current position.
 	 *
 	 * @param positions a positive (forward) or negative (backward) number of rows
 	 *
 	 * @return {@code true} if there is a result at the new location
 	 */
-	public boolean scroll(int positions);
+	boolean scroll(int positions);
 
 	/**
 	 * Go to the last result.
 	 *
 	 * @return {@code true} if there are any results
 	 */
-	public boolean last();
+	boolean last();
 
 	/**
 	 * Go to the first result.
 	 *
 	 * @return {@code true} if there are any results
 	 */
-	public boolean first();
+	boolean first();
 
 	/**
 	 * Go to a location just beforeQuery first result,  This is the location of the cursor on a newly returned
 	 * scrollable result.
 	 */
-	public void beforeFirst();
+	void beforeFirst();
 
 	/**
 	 * Go to a location just afterQuery the last result.
 	 */
-	public void afterLast();
+	void afterLast();
 
 	/**
 	 * Is this the first result?
 	 *
 	 * @return {@code true} if this is the first row of results, otherwise {@code false}
 	 */
-	public boolean isFirst();
+	boolean isFirst();
 
 	/**
 	 * Is this the last result?
 	 *
 	 * @return {@code true} if this is the last row of results.
 	 */
-	public boolean isLast();
+	boolean isLast();
 
 	/**
 	 * Get the current position in the results. The first position is number 0 (unlike JDBC).
 	 *
 	 * @return The current position number, numbered from 0; -1 indicates that there is no current row
 	 */
-	public int getRowNumber();
+	int getRowNumber();
 
 	/**
 	 * Set the current position in the result set.  Can be numbered from the first position (positive number) or
 	 * the last row (negative number).
 	 *
 	 * @param rowNumber the row number.  A positive number indicates a value numbered from the first row; a
 	 * negative number indicates a value numbered from the last row.
 	 *
 	 * @return true if there is a row at that row number
 	 */
-	public boolean setRowNumber(int rowNumber);
-
-	/**
-	 * Release resources immediately.
-	 */
-	public void close();
+	boolean setRowNumber(int rowNumber);
 
 	/**
 	 * Get the current row of results.
 	 *
 	 * @return The array of results
 	 */
-	public Object[] get();
+	Object[] get();
 
 	/**
 	 * Get the <tt>i</tt>th object in the current row of results, without
 	 * initializing any other results in the row. This method may be used
 	 * safely, regardless of the type of the column (ie. even for scalar
 	 * results).
 	 *
 	 * @param i the column, numbered from zero
 	 *
 	 * @return The requested result object; may return {@code null}
 	 *
 	 * @throws IndexOutOfBoundsException If i is an invalid index.
 	 */
-	public Object get(int i);
+	Object get(int i);
 
 	/**
 	 * Get the type of the <tt>i</tt>th column of results.
 	 *
 	 * @param i the column, numbered from zero
 	 *
 	 * @return the Hibernate type
 	 *
 	 * @throws IndexOutOfBoundsException If i is an invalid index.
 	 */
-	public Type getType(int i);
+	Type getType(int i);
 
 	/**
 	 * Convenience method to read an integer.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as an integer
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public Integer getInteger(int col);
+	Integer getInteger(int col);
 
 	/**
 	 * Convenience method to read a long.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a long
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public Long getLong(int col);
+	Long getLong(int col);
 
 	/**
 	 * Convenience method to read a float.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a float
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public Float getFloat(int col);
+	Float getFloat(int col);
 
 	/**
 	 * Convenience method to read a boolean.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a boolean
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public Boolean getBoolean(int col);
+	Boolean getBoolean(int col);
 
 	/**
 	 * Convenience method to read a double.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a double
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public Double getDouble(int col);
+	Double getDouble(int col);
 
 	/**
 	 * Convenience method to read a short.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a short
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public Short getShort(int col);
+	Short getShort(int col);
 
 	/**
 	 * Convenience method to read a byte.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a byte
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public Byte getByte(int col);
+	Byte getByte(int col);
 
 	/**
 	 * Convenience method to read a char.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a char
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public Character getCharacter(int col);
+	Character getCharacter(int col);
 
 	/**
 	 * Convenience method to read a binary (byte[]).
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a binary (byte[])
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public byte[] getBinary(int col);
+	byte[] getBinary(int col);
 
 	/**
 	 * Convenience method to read a String using streaming.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a String
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public String getText(int col);
+	String getText(int col);
 
 	/**
 	 * Convenience method to read a blob.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a Blob
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public Blob getBlob(int col);
+	Blob getBlob(int col);
 
 	/**
 	 * Convenience method to read a clob.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a Clob
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public Clob getClob(int col);
+	Clob getClob(int col);
 
 	/**
 	 * Convenience method to read a string.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a String
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public String getString(int col);
+	String getString(int col);
 
 	/**
 	 * Convenience method to read a BigDecimal.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a BigDecimal
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public BigDecimal getBigDecimal(int col);
+	BigDecimal getBigDecimal(int col);
 
 	/**
 	 * Convenience method to read a BigInteger.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a BigInteger
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public BigInteger getBigInteger(int col);
+	BigInteger getBigInteger(int col);
 
 	/**
 	 * Convenience method to read a Date.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a Date
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public Date getDate(int col);
+	Date getDate(int col);
 
 	/**
 	 * Convenience method to read a Locale.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a Locale
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public Locale getLocale(int col);
+	Locale getLocale(int col);
 
 	/**
 	 * Convenience method to read a Calendar.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a Calendar
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public Calendar getCalendar(int col);
+	Calendar getCalendar(int col);
 
 	/**
 	 * Convenience method to read a TimeZone.
 	 *
 	 * @param col The column, numbered from zero
 	 *
 	 * @return The column value as a TimeZone
 	 *
 	 * @throws IndexOutOfBoundsException If col is an invalid index.
 	 */
-	public TimeZone getTimeZone(int col);
+	TimeZone getTimeZone(int col);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/Session.java b/hibernate-core/src/main/java/org/hibernate/Session.java
index 373f7c4192..dafb0fd7b6 100644
--- a/hibernate-core/src/main/java/org/hibernate/Session.java
+++ b/hibernate-core/src/main/java/org/hibernate/Session.java
@@ -1,1083 +1,1083 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import javax.persistence.EntityManager;
 import javax.persistence.FlushModeType;
 import javax.persistence.criteria.CriteriaDelete;
 import javax.persistence.criteria.CriteriaQuery;
 import javax.persistence.criteria.CriteriaUpdate;
 
 import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.jpa.HibernateEntityManager;
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
  * be consistent with the database afterQuery the exception occurs.
  *
  * @see SessionFactory
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
-public interface Session extends SharedSessionContract, EntityManager, HibernateEntityManager, java.io.Closeable {
+public interface Session extends SharedSessionContract, EntityManager, HibernateEntityManager, AutoCloseable {
 	/**
 	 * Obtain a {@link Session} builder with the ability to grab certain information from this session.
 	 *
 	 * @return The session builder
 	 */
 	SharedSessionBuilder sessionWithOptions();
 
 	/**
 	 * Force this session to flush. Must be called at the end of a
 	 * unit of work, beforeQuery committing the transaction and closing the
 	 * session (depending on {@link #setFlushMode(FlushMode)},
 	 * {@link Transaction#commit()} calls this method).
 	 * <p/>
 	 * <i>Flushing</i> is the process of synchronizing the underlying persistent
 	 * store with persistable state held in memory.
 	 *
 	 * @throws HibernateException Indicates problems flushing the session or
 	 * talking to the database.
 	 */
 	void flush() throws HibernateException;
 
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
 	 *
 	 * @deprecated (since 5.2) use {@link #setHibernateFlushMode(FlushMode)} instead
 	 */
 	@Deprecated
 	void setFlushMode(FlushMode flushMode);
 
 	/**
 	 * {@inheritDoc}
 	 * <p/>
 	 * For users of the Hibernate native APIs, we've had to rename this method
 	 * as defined by Hibernate historically because the JPA contract defines a method of the same
 	 * name, but returning the JPA {@link FlushModeType} rather than Hibernate's {@link FlushMode}.  For
 	 * the former behavior, use {@link Session#getHibernateFlushMode()} instead.
 	 *
 	 * @return The FlushModeType in effect for this Session.
 	 */
 	@Override
 	FlushModeType getFlushMode();
 
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
 	 */
 	void setHibernateFlushMode(FlushMode flushMode);
 
 	/**
 	 * Get the current flush mode for this session.
 	 *
 	 * @return The flush mode
 	 */
 	FlushMode getHibernateFlushMode();
 
 	/**
 	 * Set the cache mode.
 	 * <p/>
 	 * Cache mode determines the manner in which this session can interact with
 	 * the second level cache.
 	 *
 	 * @param cacheMode The new cache mode.
 	 */
 	void setCacheMode(CacheMode cacheMode);
 
 	/**
 	 * Get the current cache mode.
 	 *
 	 * @return The current cache mode.
 	 */
 	CacheMode getCacheMode();
 
 	/**
 	 * Get the session factory which created this session.
 	 *
 	 * @return The session factory.
 	 * @see SessionFactory
 	 */
 	SessionFactory getSessionFactory();
 
 	/**
 	 * Cancel the execution of the current query.
 	 * <p/>
 	 * This is the sole method on session which may be safely called from
 	 * another thread.
 	 *
 	 * @throws HibernateException There was a problem canceling the query
 	 */
 	void cancelQuery() throws HibernateException;
 
 	/**
 	 * Does this session contain any changes which must be synchronized with
 	 * the database?  In other words, would any DML operations be executed if
 	 * we flushed this session?
 	 *
 	 * @return True if the session contains pending changes; false otherwise.
 	 * @throws HibernateException could not perform dirtying checking
 	 */
 	boolean isDirty() throws HibernateException;
 
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
 	boolean isDefaultReadOnly();
 
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
 	void setDefaultReadOnly(boolean readOnly);
 
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
 	Serializable getIdentifier(Object object);
 
 	/**
 	 * Check if this entity is associated with this Session.  This form caters to
 	 * non-POJO entities, by allowing the entity-name to be passed in
 	 *
 	 * @param entityName The entity name
 	 * @param object an instance of a persistent class
 	 *
 	 * @return true if the given instance is associated with this <tt>Session</tt>
 	 */
 	boolean contains(String entityName, Object object);
 
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
 	void evict(Object object);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 * <p/>
 	 * Convenient form of {@link #load(Class, Serializable, LockOptions)}
 	 *
 	 * @param theClass a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockMode the lock level
 	 *
 	 * @return the persistent instance or proxy
 	 *
 	 * @see #load(Class, Serializable, LockOptions)
 	 */
 	<T> T load(Class<T> theClass, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param theClass a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockOptions contains the lock level
 	 * @return the persistent instance or proxy
 	 */
 	<T> T load(Class<T> theClass, Serializable id, LockOptions lockOptions);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 * <p/>
 	 * Convenient form of {@link #load(String, Serializable, LockOptions)}
 	 *
 	 * @param entityName a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockMode the lock level
 	 *
 	 * @return the persistent instance or proxy
 	 *
 	 * @see #load(String, Serializable, LockOptions)
 	 */
 	Object load(String entityName, Serializable id, LockMode lockMode);
 
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
 	Object load(String entityName, Serializable id, LockOptions lockOptions);
 
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
 	<T> T load(Class<T> theClass, Serializable id);
 
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
 	Object load(String entityName, Serializable id);
 
 	/**
 	 * Read the persistent state associated with the given identifier into the given transient
 	 * instance.
 	 *
 	 * @param object an "empty" instance of the persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 */
 	void load(Object object, Serializable id);
 
 	/**
 	 * Persist the state of the given detached instance, reusing the current
 	 * identifier value.  This operation cascades to associated instances if
 	 * the association is mapped with {@code cascade="replicate"}
 	 *
 	 * @param object a detached instance of a persistent class
 	 * @param replicationMode The replication mode to use
 	 */
 	void replicate(Object object, ReplicationMode replicationMode);
 
 	/**
 	 * Persist the state of the given detached instance, reusing the current
 	 * identifier value.  This operation cascades to associated instances if
 	 * the association is mapped with {@code cascade="replicate"}
 	 *
 	 * @param entityName The entity name
 	 * @param object a detached instance of a persistent class
 	 * @param replicationMode The replication mode to use
 	 */
 	void replicate(String entityName, Object object, ReplicationMode replicationMode) ;
 
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
 	Serializable save(Object object);
 
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
 	Serializable save(String entityName, Object object);
 
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
 	void saveOrUpdate(Object object);
 
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
 	void saveOrUpdate(String entityName, Object object);
 
 	/**
 	 * Update the persistent instance with the identifier of the given detached
 	 * instance. If there is a persistent instance with the same identifier,
 	 * an exception is thrown. This operation cascades to associated instances
 	 * if the association is mapped with {@code cascade="save-update"}
 	 *
 	 * @param object a detached instance containing updated state
 	 */
 	void update(Object object);
 
 	/**
 	 * Update the persistent instance with the identifier of the given detached
 	 * instance. If there is a persistent instance with the same identifier,
 	 * an exception is thrown. This operation cascades to associated instances
 	 * if the association is mapped with {@code cascade="save-update"}
 	 *
 	 * @param entityName The entity name
 	 * @param object a detached instance containing updated state
 	 */
 	void update(String entityName, Object object);
 
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
 	Object merge(Object object);
 
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
 	Object merge(String entityName, Object object);
 
 	/**
 	 * Make a transient instance persistent. This operation cascades to associated
 	 * instances if the association is mapped with {@code cascade="persist"}
 	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param object a transient instance to be made persistent
 	 */
 	void persist(Object object);
 	/**
 	 * Make a transient instance persistent. This operation cascades to associated
 	 * instances if the association is mapped with {@code cascade="persist"}
 	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param entityName The entity name
 	 * @param object a transient instance to be made persistent
 	 */
 	void persist(String entityName, Object object);
 
 	/**
 	 * Remove a persistent instance from the datastore. The argument may be
 	 * an instance associated with the receiving <tt>Session</tt> or a transient
 	 * instance with an identifier associated with existing persistent state.
 	 * This operation cascades to associated instances if the association is mapped
 	 * with {@code cascade="delete"}
 	 *
 	 * @param object the instance to be removed
 	 */
 	void delete(Object object);
 
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
 	void delete(String entityName, Object object);
 
 	/**
 	 * Obtain the specified lock level upon the given object. This may be used to
 	 * perform a version check (<tt>LockMode.READ</tt>), to upgrade to a pessimistic
 	 * lock (<tt>LockMode.PESSIMISTIC_WRITE</tt>), or to simply reassociate a transient instance
 	 * with a session (<tt>LockMode.NONE</tt>). This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="lock"</tt>.
 	 * <p/>
 	 * Convenient form of {@link LockRequest#lock(Object)} via {@link #buildLockRequest(LockOptions)}
 	 *
 	 * @param object a persistent or transient instance
 	 * @param lockMode the lock level
 	 *
 	 * @see #buildLockRequest(LockOptions)
 	 * @see LockRequest#lock(Object)
 	 */
 	void lock(Object object, LockMode lockMode);
 
 	/**
 	 * Obtain the specified lock level upon the given object. This may be used to
 	 * perform a version check (<tt>LockMode.OPTIMISTIC</tt>), to upgrade to a pessimistic
 	 * lock (<tt>LockMode.PESSIMISTIC_WRITE</tt>), or to simply reassociate a transient instance
 	 * with a session (<tt>LockMode.NONE</tt>). This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="lock"</tt>.
 	 * <p/>
 	 * Convenient form of {@link LockRequest#lock(String, Object)} via {@link #buildLockRequest(LockOptions)}
 	 *
 	 * @param entityName The name of the entity
 	 * @param object a persistent or transient instance
 	 * @param lockMode the lock level
 	 *
 	 * @see #buildLockRequest(LockOptions)
 	 * @see LockRequest#lock(String, Object)
 	 */
 	void lock(String entityName, Object object, LockMode lockMode);
 
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
 	LockRequest buildLockRequest(LockOptions lockOptions);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database. It is
 	 * inadvisable to use this to implement long-running sessions that span many
 	 * business tasks. This method is, however, useful in certain special circumstances.
 	 * For example
 	 * <ul>
 	 * <li>where a database trigger alters the object state upon insert or update
 	 * <li>afterQuery executing direct SQL (eg. a mass update) in the same session
 	 * <li>afterQuery inserting a <tt>Blob</tt> or <tt>Clob</tt>
 	 * </ul>
 	 *
 	 * @param object a persistent or detached instance
 	 */
 	void refresh(Object object);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database. It is
 	 * inadvisable to use this to implement long-running sessions that span many
 	 * business tasks. This method is, however, useful in certain special circumstances.
 	 * For example
 	 * <ul>
 	 * <li>where a database trigger alters the object state upon insert or update
 	 * <li>afterQuery executing direct SQL (eg. a mass update) in the same session
 	 * <li>afterQuery inserting a <tt>Blob</tt> or <tt>Clob</tt>
 	 * </ul>
 	 *
 	 * @param entityName a persistent class
 	 * @param object a persistent or detached instance
 	 */
 	void refresh(String entityName, Object object);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
 	 * <p/>
 	 * Convenient form of {@link #refresh(Object, LockOptions)}
 	 *
 	 * @param object a persistent or detached instance
 	 * @param lockMode the lock mode to use
 	 *
 	 * @see #refresh(Object, LockOptions)
 	 */
 	void refresh(Object object, LockMode lockMode);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
 	 *
 	 * @param object a persistent or detached instance
 	 * @param lockOptions contains the lock mode to use
 	 */
 	void refresh(Object object, LockOptions lockOptions);
 
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
 	void refresh(String entityName, Object object, LockOptions lockOptions);
 
 	/**
 	 * Determine the current lock mode of the given object.
 	 *
 	 * @param object a persistent instance
 	 *
 	 * @return the current lock mode
 	 */
 	LockMode getCurrentLockMode(Object object);
 
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
 	org.hibernate.query.Query createFilter(Object collection, String queryString);
 
 	/**
 	 * Completely clear the session. Evict all loaded instances and cancel all pending
 	 * saves, updates and deletions. Do not close open iterators or instances of
 	 * <tt>ScrollableResults</tt>.
 	 */
 	void clear();
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 *
 	 * @param entityType The entity type
 	 * @param id an identifier
 	 *
 	 * @return a persistent instance or null
 	 */
 	<T> T get(Class<T> entityType, Serializable id);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 * <p/>
 	 * Convenient form of {@link #get(Class, Serializable, LockOptions)}
 	 *
 	 * @param entityType The entity type
 	 * @param id an identifier
 	 * @param lockMode the lock mode
 	 *
 	 * @return a persistent instance or null
 	 *
 	 * @see #get(Class, Serializable, LockOptions)
 	 */
 	<T> T get(Class<T> entityType, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param entityType The entity type
 	 * @param id an identifier
 	 * @param lockOptions the lock mode
 	 *
 	 * @return a persistent instance or null
 	 */
 	<T> T get(Class<T> entityType, Serializable id, LockOptions lockOptions);
 
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
 	Object get(String entityName, Serializable id);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 * <p/>
 	 * Convenient form of {@link #get(String, Serializable, LockOptions)}
 	 *
 	 * @param entityName the entity name
 	 * @param id an identifier
 	 * @param lockMode the lock mode
 	 *
 	 * @return a persistent instance or null
 	 *
 	 * @see #get(String, Serializable, LockOptions)
 	 */
 	Object get(String entityName, Serializable id, LockMode lockMode);
 
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
 	Object get(String entityName, Serializable id, LockOptions lockOptions);
 
 	/**
 	 * Return the entity name for a persistent entity.
 	 *   
 	 * @param object a persistent entity
 	 *
 	 * @return the entity name
 	 */
 	String getEntityName(Object object);
 	
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
 	IdentifierLoadAccess byId(String entityName);
 
 	/**
 	 * Create a {@link MultiIdentifierLoadAccess} instance to retrieve multiple entities at once
 	 * as specified by primary key values.
 	 *
 	 * @param entityClass The entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by primary key values
 	 *
 	 * @throws HibernateException If the specified Class cannot be resolved as a mapped entity
 	 */
 	<T> MultiIdentifierLoadAccess<T> byMultipleIds(Class<T> entityClass);
 
 	/**
 	 * Create a {@link MultiIdentifierLoadAccess} instance to retrieve multiple entities at once
 	 * as specified by primary key values.
 	 *
 	 * @param entityName The entity name of the entity type to be retrieved
 	 *
 	 * @return load delegate for loading the specified entity type by primary key values
 	 *
 	 * @throws HibernateException If the specified entity name cannot be resolved as an entity name
 	 */
 	MultiIdentifierLoadAccess byMultipleIds(String entityName);
 
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
 	<T> IdentifierLoadAccess<T> byId(Class<T> entityClass);
 
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
 	NaturalIdLoadAccess byNaturalId(String entityName);
 
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
 	<T> NaturalIdLoadAccess<T> byNaturalId(Class<T> entityClass);
 
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
 	SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName);
 
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
 	<T> SimpleNaturalIdLoadAccess<T> bySimpleNaturalId(Class<T> entityClass);
 
 	/**
 	 * Enable the named filter for this current session.
 	 *
 	 * @param filterName The name of the filter to be enabled.
 	 *
 	 * @return The Filter instance representing the enabled filter.
 	 */
 	Filter enableFilter(String filterName);
 
 	/**
 	 * Retrieve a currently enabled filter by name.
 	 *
 	 * @param filterName The name of the filter to be retrieved.
 	 *
 	 * @return The Filter instance representing the enabled filter.
 	 */
 	Filter getEnabledFilter(String filterName);
 
 	/**
 	 * Disable the named filter for the current session.
 	 *
 	 * @param filterName The name of the filter to be disabled.
 	 */
 	void disableFilter(String filterName);
 	
 	/**
 	 * Get the statistics for this session.
 	 *
 	 * @return The session statistics being collected for this session
 	 */
 	SessionStatistics getStatistics();
 
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
 	boolean isReadOnly(Object entityOrProxy);
 
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
 	void setReadOnly(Object entityOrProxy, boolean readOnly);
 
 	/**
 	 * Controller for allowing users to perform JDBC related work using the Connection managed by this Session.
 	 *
 	 * @param work The work to be performed.
 	 * @throws HibernateException Generally indicates wrapped {@link java.sql.SQLException}
 	 */
 	void doWork(Work work) throws HibernateException;
 
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
 	<T> T doReturningWork(ReturningWork<T> work) throws HibernateException;
 
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
 	boolean isFetchProfileEnabled(String name) throws UnknownProfileException;
 
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
 	void enableFetchProfile(String name) throws UnknownProfileException;
 
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
 	void disableFetchProfile(String name) throws UnknownProfileException;
 
 	/**
 	 * Convenience access to the {@link TypeHelper} associated with this session's {@link SessionFactory}.
 	 * <p/>
 	 * Equivalent to calling {@link #getSessionFactory()}.{@link SessionFactory#getTypeHelper getTypeHelper()}
 	 *
 	 * @return The {@link TypeHelper} associated with this session's {@link SessionFactory}
 	 */
 	TypeHelper getTypeHelper();
 
 	/**
 	 * Retrieve this session's helper/delegate for creating LOB instances.
 	 *
 	 * @return This session's LOB helper
 	 */
 	LobHelper getLobHelper();
 
 	/**
 	 * Contains locking details (LockMode, Timeout and Scope).
 	 */
 	interface LockRequest {
 		/**
 		 * Constant usable as a time out value that indicates no wait semantics should be used in
 		 * attempting to acquire locks.
 		 */
 		int PESSIMISTIC_NO_WAIT = 0;
 		/**
 		 * Constant usable as a time out value that indicates that attempting to acquire locks should be allowed to
 		 * wait forever (apply no timeout).
 		 */
 		int PESSIMISTIC_WAIT_FOREVER = -1;
 
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/StatelessSession.java b/hibernate-core/src/main/java/org/hibernate/StatelessSession.java
index 109aa4a21b..a0ace4eb2b 100755
--- a/hibernate-core/src/main/java/org/hibernate/StatelessSession.java
+++ b/hibernate-core/src/main/java/org/hibernate/StatelessSession.java
@@ -1,172 +1,172 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate;
 
 import java.io.Serializable;
 import java.sql.Connection;
 
 /**
  * A command-oriented API for performing bulk operations against a database.
  * <p/>
  * A stateless session does not implement a first-level cache nor interact
  * with any second-level cache, nor does it implement transactional
  * write-behind or automatic dirty checking, nor do operations cascade to
  * associated instances. Collections are ignored by a stateless session.
  * Operations performed via a stateless session bypass Hibernate's event model
  * and interceptors.  Stateless sessions are vulnerable to data aliasing
  * effects, due to the lack of a first-level cache.
  * <p/>
  * For certain kinds of transactions, a stateless session may perform slightly
  * faster than a stateful session.
  *
  * @author Gavin King
  */
-public interface StatelessSession extends SharedSessionContract, java.io.Closeable {
+public interface StatelessSession extends SharedSessionContract, AutoCloseable {
 	/**
 	 * Close the stateless session and release the JDBC connection.
 	 */
-	public void close();
+	void close();
 
 	/**
 	 * Insert a row.
 	 *
 	 * @param entity a new transient instance
 	 *
 	 * @return The identifier of the inserted entity
 	 */
-	public Serializable insert(Object entity);
+	Serializable insert(Object entity);
 
 	/**
 	 * Insert a row.
 	 *
 	 * @param entityName The entityName for the entity to be inserted
 	 * @param entity a new transient instance
 	 *
 	 * @return the identifier of the instance
 	 */
-	public Serializable insert(String entityName, Object entity);
+	Serializable insert(String entityName, Object entity);
 
 	/**
 	 * Update a row.
 	 *
 	 * @param entity a detached entity instance
 	 */
-	public void update(Object entity);
+	void update(Object entity);
 
 	/**
 	 * Update a row.
 	 *
 	 * @param entityName The entityName for the entity to be updated
 	 * @param entity a detached entity instance
 	 */
-	public void update(String entityName, Object entity);
+	void update(String entityName, Object entity);
 
 	/**
 	 * Delete a row.
 	 *
 	 * @param entity a detached entity instance
 	 */
-	public void delete(Object entity);
+	void delete(Object entity);
 
 	/**
 	 * Delete a row.
 	 *
 	 * @param entityName The entityName for the entity to be deleted
 	 * @param entity a detached entity instance
 	 */
-	public void delete(String entityName, Object entity);
+	void delete(String entityName, Object entity);
 
 	/**
 	 * Retrieve a row.
 	 *
 	 * @param entityName The name of the entity to retrieve
 	 * @param id The id of the entity to retrieve
 	 *
 	 * @return a detached entity instance
 	 */
-	public Object get(String entityName, Serializable id);
+	Object get(String entityName, Serializable id);
 
 	/**
 	 * Retrieve a row.
 	 *
 	 * @param entityClass The class of the entity to retrieve
 	 * @param id The id of the entity to retrieve
 	 *
 	 * @return a detached entity instance
 	 */
-	public Object get(Class entityClass, Serializable id);
+	Object get(Class entityClass, Serializable id);
 
 	/**
 	 * Retrieve a row, obtaining the specified lock mode.
 	 *
 	 * @param entityName The name of the entity to retrieve
 	 * @param id The id of the entity to retrieve
 	 * @param lockMode The lock mode to apply to the entity
 	 *
 	 * @return a detached entity instance
 	 */
-	public Object get(String entityName, Serializable id, LockMode lockMode);
+	Object get(String entityName, Serializable id, LockMode lockMode);
 
 	/**
 	 * Retrieve a row, obtaining the specified lock mode.
 	 *
 	 * @param entityClass The class of the entity to retrieve
 	 * @param id The id of the entity to retrieve
 	 * @param lockMode The lock mode to apply to the entity
 	 *
 	 * @return a detached entity instance
 	 */
-	public Object get(Class entityClass, Serializable id, LockMode lockMode);
+	Object get(Class entityClass, Serializable id, LockMode lockMode);
 
 	/**
 	 * Refresh the entity instance state from the database.
 	 *
 	 * @param entity The entity to be refreshed.
 	 */
-	public void refresh(Object entity);
+	void refresh(Object entity);
 
 	/**
 	 * Refresh the entity instance state from the database.
 	 *
 	 * @param entityName The entityName for the entity to be refreshed.
 	 * @param entity The entity to be refreshed.
 	 */
-	public void refresh(String entityName, Object entity);
+	void refresh(String entityName, Object entity);
 
 	/**
 	 * Refresh the entity instance state from the database.
 	 *
 	 * @param entity The entity to be refreshed.
 	 * @param lockMode The LockMode to be applied.
 	 */
-	public void refresh(Object entity, LockMode lockMode);
+	void refresh(Object entity, LockMode lockMode);
 
 	/**
 	 * Refresh the entity instance state from the database.
 	 *
 	 * @param entityName The entityName for the entity to be refreshed.
 	 * @param entity The entity to be refreshed.
 	 * @param lockMode The LockMode to be applied.
 	 */
-	public void refresh(String entityName, Object entity, LockMode lockMode);
+	void refresh(String entityName, Object entity, LockMode lockMode);
 
 	/**
 	 * Returns the current JDBC connection associated with this
 	 * instance.<br>
 	 * <br>
 	 * If the session is using aggressive connection release (as in a
 	 * CMT environment), it is the application's responsibility to
 	 * close the connection returned by this call. Otherwise, the
 	 * application should not close the connection.
 	 *
 	 * @deprecated just missed when deprecating same method from {@link Session}
 	 *
 	 * @return The connection associated with this stateless session
 	 */
 	@Deprecated
-	public Connection connection();
+	Connection connection();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/HibernateIterator.java b/hibernate-core/src/main/java/org/hibernate/engine/HibernateIterator.java
index 933a15b8a6..5fd5c79194 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/HibernateIterator.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/HibernateIterator.java
@@ -1,28 +1,28 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine;
 
 import java.util.Iterator;
 
 import org.hibernate.JDBCException;
 
 /**
  * Hibernate-specific iterator that may be closed
  *
  * @see org.hibernate.Query#iterate()
  * @see org.hibernate.Hibernate#close(java.util.Iterator)
  *
  * @author Gavin King
  */
-public interface HibernateIterator extends Iterator, java.io.Closeable {
+public interface HibernateIterator extends Iterator, AutoCloseable {
 	/**
 	 * Close the Hibernate query result iterator
 	 *
 	 * @throws JDBCException Indicates a problem releasing the underlying JDBC resources.
 	 */
-	public void close() throws JDBCException;
+	void close() throws JDBCException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/HQLQueryPlan.java b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/HQLQueryPlan.java
index 5168eb01af..4b758f40e8 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/HQLQueryPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/HQLQueryPlan.java
@@ -1,441 +1,442 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.query.spi;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.Filter;
 import org.hibernate.HibernateException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.QuerySplitter;
 import org.hibernate.hql.spi.FilterTranslator;
 import org.hibernate.hql.spi.ParameterTranslations;
 import org.hibernate.hql.spi.QueryTranslator;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.query.internal.ParameterMetadataImpl;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Defines a query execution plan for an HQL query (or filter).
  *
  * @author Steve Ebersole
  */
 public class HQLQueryPlan implements Serializable {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( HQLQueryPlan.class );
 
     // TODO : keep separate notions of QT[] here for shallow/non-shallow queries...
 
 	private final String sourceQuery;
 	private final QueryTranslator[] translators;
 	private final String[] sqlStrings;
 
 	private final ParameterMetadataImpl parameterMetadata;
 	private final ReturnMetadata returnMetadata;
 	private final Set querySpaces;
 
 	private final Set<String> enabledFilterNames;
 	private final boolean shallow;
 
 	/**
 	* We'll check the trace level only once per instance
 	*/
 	private final boolean traceEnabled = LOG.isTraceEnabled();
 
 	/**
 	 * Constructs a HQLQueryPlan
 	 *
 	 * @param hql The HQL query
 	 * @param shallow Whether the execution is to be shallow or not
 	 * @param enabledFilters The enabled filters (we only keep the names)
 	 * @param factory The factory
 	 */
 	public HQLQueryPlan(String hql, boolean shallow, Map<String,Filter> enabledFilters,
 			SessionFactoryImplementor factory) {
 		this( hql, null, shallow, enabledFilters, factory, null );
 	}
 	
 	public HQLQueryPlan(String hql, boolean shallow, Map<String,Filter> enabledFilters,
 			SessionFactoryImplementor factory, EntityGraphQueryHint entityGraphQueryHint) {
 		this( hql, null, shallow, enabledFilters, factory, entityGraphQueryHint );
 	}
 
 	@SuppressWarnings("unchecked")
 	protected HQLQueryPlan(
 			String hql,
 			String collectionRole,
 			boolean shallow,
 			Map<String,Filter> enabledFilters,
 			SessionFactoryImplementor factory,
 			EntityGraphQueryHint entityGraphQueryHint) {
 		this.sourceQuery = hql;
 		this.shallow = shallow;
 
 		final Set<String> copy = new HashSet<String>();
 		copy.addAll( enabledFilters.keySet() );
 		this.enabledFilterNames = java.util.Collections.unmodifiableSet( copy );
 
 		final String[] concreteQueryStrings = QuerySplitter.concreteQueries( hql, factory );
 		final int length = concreteQueryStrings.length;
 		this.translators = new QueryTranslator[length];
 
 		final List<String> sqlStringList = new ArrayList<String>();
 		final Set<Serializable> combinedQuerySpaces = new HashSet<Serializable>();
 
 		final Map querySubstitutions = factory.getSessionFactoryOptions().getQuerySubstitutions();
 		final QueryTranslatorFactory queryTranslatorFactory = factory.getServiceRegistry().getService( QueryTranslatorFactory.class );
 
 
 		for ( int i=0; i<length; i++ ) {
 			if ( collectionRole == null ) {
 				translators[i] = queryTranslatorFactory
 						.createQueryTranslator( hql, concreteQueryStrings[i], enabledFilters, factory, entityGraphQueryHint );
 				translators[i].compile( querySubstitutions, shallow );
 			}
 			else {
 				translators[i] = queryTranslatorFactory
 						.createFilterTranslator( hql, concreteQueryStrings[i], enabledFilters, factory );
 				( (FilterTranslator) translators[i] ).compile( collectionRole, querySubstitutions, shallow );
 			}
 			combinedQuerySpaces.addAll( translators[i].getQuerySpaces() );
 			sqlStringList.addAll( translators[i].collectSqlStrings() );
 		}
 
 		this.sqlStrings = ArrayHelper.toStringArray( sqlStringList );
 		this.querySpaces = combinedQuerySpaces;
 
 		if ( length == 0 ) {
 			parameterMetadata = new ParameterMetadataImpl( null, null );
 			returnMetadata = null;
 		}
 		else {
 			this.parameterMetadata = buildParameterMetadata( translators[0].getParameterTranslations(), hql );
 			if ( translators[0].isManipulationStatement() ) {
 				returnMetadata = null;
 			}
 			else {
 				final Type[] types = ( length > 1 ) ? new Type[translators[0].getReturnTypes().length] : translators[0].getReturnTypes();
 				returnMetadata = new ReturnMetadata( translators[0].getReturnAliases(), types );
 			}
 		}
 	}
 
 	public String getSourceQuery() {
 		return sourceQuery;
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	public ParameterMetadataImpl getParameterMetadata() {
 		return parameterMetadata;
 	}
 
 	public ReturnMetadata getReturnMetadata() {
 		return returnMetadata;
 	}
 
 	public Set getEnabledFilterNames() {
 		return enabledFilterNames;
 	}
 
 	public String[] getSqlStrings() {
 		return sqlStrings;
 	}
 
 	public Set getUtilizedFilterNames() {
 		// TODO : add this info to the translator and aggregate it here...
 		return null;
 	}
 
 	public boolean isShallow() {
 		return shallow;
 	}
 
 	/**
 	 * Coordinates the efforts to perform a list across all the included query translators.
 	 *
 	 * @param queryParameters The query parameters
 	 * @param session The session
 	 *
 	 * @return The query result list
 	 *
 	 * @throws HibernateException Indicates a problem performing the query
 	 */
 	@SuppressWarnings("unchecked")
 	public List performList(
 			QueryParameters queryParameters,
 			SharedSessionContractImplementor session) throws HibernateException {
 		if ( traceEnabled ) {
 			LOG.tracev( "Find: {0}", getSourceQuery() );
 			queryParameters.traceParameters( session.getFactory() );
 		}
 
 		final RowSelection rowSelection = queryParameters.getRowSelection();
 		final boolean hasLimit = rowSelection != null
 				&& rowSelection.definesLimits();
 		final boolean needsLimit = hasLimit && translators.length > 1;
 
 		final QueryParameters queryParametersToUse;
 		if ( needsLimit ) {
 			LOG.needsLimit();
 			final RowSelection selection = new RowSelection();
 			selection.setFetchSize( queryParameters.getRowSelection().getFetchSize() );
 			selection.setTimeout( queryParameters.getRowSelection().getTimeout() );
 			queryParametersToUse = queryParameters.createCopyUsing( selection );
 		}
 		else {
 			queryParametersToUse = queryParameters;
 		}
 
 		//fast path to avoid unnecessary allocation and copying
 		if ( translators.length == 1 ) {
 			return translators[0].list( session, queryParametersToUse );
 		}
 		final int guessedResultSize = guessResultSize( rowSelection );
 		final List combinedResults = new ArrayList( guessedResultSize );
 		final IdentitySet distinction;
 		if ( needsLimit ) {
 			distinction = new IdentitySet( guessedResultSize );
 		}
 		else {
 			distinction = null;
 		}
 		int includedCount = -1;
 		translator_loop:
 		for ( QueryTranslator translator : translators ) {
 			final List tmp = translator.list( session, queryParametersToUse );
 			if ( needsLimit ) {
 				// NOTE : firstRow is zero-based
 				final int first = queryParameters.getRowSelection().getFirstRow() == null
 						? 0
 						: queryParameters.getRowSelection().getFirstRow();
 				final int max = queryParameters.getRowSelection().getMaxRows() == null
 						? -1
 						: queryParameters.getRowSelection().getMaxRows();
 				for ( final Object result : tmp ) {
 					if ( !distinction.add( result ) ) {
 						continue;
 					}
 					includedCount++;
 					if ( includedCount < first ) {
 						continue;
 					}
 					combinedResults.add( result );
 					if ( max >= 0 && includedCount > max ) {
 						// break the outer loop !!!
 						break translator_loop;
 					}
 				}
 			}
 			else {
 				combinedResults.addAll( tmp );
 			}
 		}
 		return combinedResults;
 	}
 
 	/**
 	 * If we're able to guess a likely size of the results we can optimize allocation
 	 * of our datastructures.
 	 * Essentially if we detect the user is not using pagination, we attempt to use the FetchSize
 	 * as a reasonable hint. If fetch size is not being set either, it is reasonable to expect
 	 * that we're going to have a single hit. In such a case it would be tempting to return a constant
 	 * of value one, but that's dangerous as it doesn't scale up appropriately for example
 	 * with an ArrayList if the guess is wrong.
 	 *
 	 * @param rowSelection
 	 * @return a reasonable size to use for allocation
 	 */
 	@SuppressWarnings("UnnecessaryUnboxing")
 	private int guessResultSize(RowSelection rowSelection) {
 		if ( rowSelection != null ) {
 			final int maxReasonableAllocation = rowSelection.getFetchSize() != null ? rowSelection.getFetchSize().intValue() : 100;
 			if ( rowSelection.getMaxRows() != null && rowSelection.getMaxRows().intValue() > 0 ) {
 				return Math.min( maxReasonableAllocation, rowSelection.getMaxRows().intValue() );
 			}
 			else if ( rowSelection.getFetchSize() != null && rowSelection.getFetchSize().intValue() > 0 ) {
 				return rowSelection.getFetchSize().intValue();
 			}
 		}
 		return 7;//magic number guessed as a reasonable default.
 	}
 
 	/**
 	 * Coordinates the efforts to perform an iterate across all the included query translators.
 	 *
 	 * @param queryParameters The query parameters
 	 * @param session The session
 	 *
 	 * @return The query result iterator
 	 *
 	 * @throws HibernateException Indicates a problem performing the query
 	 */
 	@SuppressWarnings("unchecked")
 	public Iterator performIterate(
 			QueryParameters queryParameters,
 			EventSource session) throws HibernateException {
 		if ( traceEnabled ) {
 			LOG.tracev( "Iterate: {0}", getSourceQuery() );
 			queryParameters.traceParameters( session.getFactory() );
 		}
 		if ( translators.length == 0 ) {
 			return EmptyIterator.INSTANCE;
 		}
 
 		final boolean many = translators.length > 1;
 		Iterator[] results = null;
 		if ( many ) {
 			results = new Iterator[translators.length];
 		}
 
 		Iterator result = null;
 		for ( int i = 0; i < translators.length; i++ ) {
 			result = translators[i].iterate( queryParameters, session );
 			if ( many ) {
 				results[i] = result;
 			}
 		}
 
 		return many ? new JoinedIterator( results ) : result;
 	}
 
 	/**
 	 * Coordinates the efforts to perform a scroll across all the included query translators.
 	 *
 	 * @param queryParameters The query parameters
 	 * @param session The session
 	 *
 	 * @return The query result iterator
 	 *
 	 * @throws HibernateException Indicates a problem performing the query
 	 */
-	public ScrollableResults performScroll(
+	public ScrollableResultsImplementor performScroll(
 			QueryParameters queryParameters,
 			SharedSessionContractImplementor session) throws HibernateException {
 		if ( traceEnabled ) {
 			LOG.tracev( "Iterate: {0}", getSourceQuery() );
 			queryParameters.traceParameters( session.getFactory() );
 		}
 		if ( translators.length != 1 ) {
 			throw new QueryException( "implicit polymorphism not supported for scroll() queries" );
 		}
 		if ( queryParameters.getRowSelection().definesLimits() && translators[0].containsCollectionFetches() ) {
 			throw new QueryException( "firstResult/maxResults not supported in conjunction with scroll() of a query containing collection fetches" );
 		}
 
 		return translators[0].scroll( queryParameters, session );
 	}
 
 	/**
 	 * Coordinates the efforts to perform an execution across all the included query translators.
 	 *
 	 * @param queryParameters The query parameters
 	 * @param session The session
 	 *
 	 * @return The aggregated "affected row" count
 	 *
 	 * @throws HibernateException Indicates a problem performing the execution
 	 */
 	public int performExecuteUpdate(QueryParameters queryParameters, SharedSessionContractImplementor session)
 			throws HibernateException {
 		if ( traceEnabled ) {
 			LOG.tracev( "Execute update: {0}", getSourceQuery() );
 			queryParameters.traceParameters( session.getFactory() );
 		}
 		if ( translators.length != 1 ) {
 			LOG.splitQueries( getSourceQuery(), translators.length );
 		}
 		int result = 0;
 		for ( QueryTranslator translator : translators ) {
 			result += translator.executeUpdate( queryParameters, session );
 		}
 		return result;
 	}
 
 	private ParameterMetadataImpl buildParameterMetadata(ParameterTranslations parameterTranslations, String hql) {
 		final long start = traceEnabled ? System.nanoTime() : 0;
 		final ParamLocationRecognizer recognizer = ParamLocationRecognizer.parseLocations( hql );
 
 		if ( traceEnabled ) {
 			final long end = System.nanoTime();
 			LOG.tracev( "HQL param location recognition took {0} nanoseconds ({1})", ( end - start ), hql );
 		}
 
 		int ordinalParamCount = parameterTranslations.getOrdinalParameterCount();
 		final int[] locations = ArrayHelper.toIntArray( recognizer.getOrdinalParameterLocationList() );
 		if ( parameterTranslations.supportsOrdinalParameterMetadata() && locations.length != ordinalParamCount ) {
 			throw new HibernateException( "ordinal parameter mismatch" );
 		}
 		ordinalParamCount = locations.length;
 
 		final OrdinalParameterDescriptor[] ordinalParamDescriptors = new OrdinalParameterDescriptor[ordinalParamCount];
 		for ( int i = 0; i < ordinalParamCount; i++ ) {
 			ordinalParamDescriptors[ i ] = new OrdinalParameterDescriptor(
 					i,
 					parameterTranslations.supportsOrdinalParameterMetadata()
 							? parameterTranslations.getOrdinalParameterExpectedType( i )
 							: null,
 					locations[ i ]
 			);
 		}
 
 		final Map<String, NamedParameterDescriptor> namedParamDescriptorMap = new HashMap<String, NamedParameterDescriptor>();
 		final Map<String, ParamLocationRecognizer.NamedParameterDescription> map = recognizer.getNamedParameterDescriptionMap();
 		for ( final String name : map.keySet() ) {
 			final ParamLocationRecognizer.NamedParameterDescription description = map.get( name );
 			namedParamDescriptorMap.put(
 					name,
 					new NamedParameterDescriptor(
 							name,
 							parameterTranslations.getNamedParameterExpectedType( name ),
 							description.buildPositionsArray(),
 							description.isJpaStyle()
 					)
 			);
 		}
 		return new ParameterMetadataImpl( ordinalParamDescriptors, namedParamDescriptorMap );
 	}
 
 	/**
 	 * Access to the underlying translators associated with this query
 	 *
 	 * @return The translators
 	 */
 	public QueryTranslator[] getTranslators() {
 		final QueryTranslator[] copy = new QueryTranslator[translators.length];
 		System.arraycopy( translators, 0, copy, 0, copy.length );
 		return copy;
 	}
 
 	public Class getDynamicInstantiationResultType() {
 		return translators[0].getDynamicInstantiationResultType();
 	}
 
 	public boolean isSelect() {
 		return !translators[0].isManipulationStatement();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java
index a180857eef..ccf9975b08 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionDelegatorBaseImpl.java
@@ -1,1160 +1,1159 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.spi;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.UUID;
 import javax.persistence.EntityGraph;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.FlushModeType;
 import javax.persistence.LockModeType;
 import javax.persistence.StoredProcedureQuery;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.criteria.CriteriaDelete;
 import javax.persistence.criteria.CriteriaQuery;
 import javax.persistence.criteria.CriteriaUpdate;
 import javax.persistence.criteria.Selection;
 import javax.persistence.metamodel.Metamodel;
 
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
 import org.hibernate.MultiIdentifierLoadAccess;
 import org.hibernate.NaturalIdLoadAccess;
 import org.hibernate.ReplicationMode;
 import org.hibernate.ScrollMode;
-import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.SessionEventListener;
 import org.hibernate.SharedSessionBuilder;
 import org.hibernate.SimpleNaturalIdLoadAccess;
-import org.hibernate.StaleStateException;
 import org.hibernate.Transaction;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.query.spi.NativeQueryImplementor;
 import org.hibernate.query.spi.QueryImplementor;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
 import org.hibernate.resource.transaction.spi.TransactionCoordinator;
 import org.hibernate.stat.SessionStatistics;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
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
 @SuppressWarnings("deprecation")
 public class SessionDelegatorBaseImpl implements SessionImplementor {
 
 	protected final SessionImplementor delegate;
 
 	/**
 	 * @deprecated (snce 6.0) SessionDelegatorBaseImpl should take just one argument, the SessionImplementor.
 	 * Use the {@link #SessionDelegatorBaseImpl(SessionImplementor)} form instead
 	 */
 	@Deprecated
 	public SessionDelegatorBaseImpl(SessionImplementor delegate, Session session) {
 		if ( delegate == null ) {
 			throw new IllegalArgumentException( "Unable to create a SessionDelegatorBaseImpl from a null delegate object" );
 		}
 		if ( session == null ) {
 			throw new IllegalArgumentException( "Unable to create a SessionDelegatorBaseImpl from a null Session" );
 		}
 		if ( delegate != session ) {
 			throw new IllegalArgumentException( "Unable to create a SessionDelegatorBaseImpl from different Session/SessionImplementor references" );
 		}
 
 		this.delegate = delegate;
 	}
 
 	public SessionDelegatorBaseImpl(SessionImplementor delegate) {
 		this( delegate, delegate );
 	}
 
 	@Override
 	public <T> T execute(Callback<T> callback) {
 		return delegate.execute( callback );
 	}
 
 	@Override
 	public String getTenantIdentifier() {
 		return delegate.getTenantIdentifier();
 	}
 
 	@Override
 	public UUID getSessionIdentifier() {
 		return delegate.getSessionIdentifier();
 	}
 
 	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		return delegate.getJdbcConnectionAccess();
 	}
 
 	@Override
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
 		return delegate.generateEntityKey( id, persister );
 	}
 
 	@Override
 	public Interceptor getInterceptor() {
 		return delegate.getInterceptor();
 	}
 
 	@Override
 	public void setAutoClear(boolean enabled) {
 		delegate.setAutoClear( enabled );
 	}
 
 	@Override
 	public boolean isTransactionInProgress() {
 		return delegate.isTransactionInProgress();
 	}
 
 	@Override
 	public LockOptions getLockRequest(LockModeType lockModeType, Map<String, Object> properties) {
 		return delegate.getLockRequest( lockModeType, properties );
 	}
 
 	@Override
 	public LockOptions buildLockOptions(LockModeType lockModeType, Map<String, Object> properties) {
 		return delegate.buildLockOptions( lockModeType, properties );
 	}
 
 	@Override
 	public <T> QueryImplementor<T> createQuery(
 			String jpaqlString,
 			Class<T> resultClass,
 			Selection selection,
 			QueryOptions queryOptions) {
 		return delegate.createQuery( jpaqlString,resultClass, selection, queryOptions );
 	}
 
 	@Override
 	public void initializeCollection(PersistentCollection collection, boolean writing) throws HibernateException {
 		delegate.initializeCollection( collection, writing );
 	}
 
 	@Override
 	public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
 		return delegate.internalLoad( entityName, id, eager, nullable );
 	}
 
 	@Override
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
 		return delegate.immediateLoad( entityName, id );
 	}
 
 	@Override
 	public long getTimestamp() {
 		return delegate.getTimestamp();
 	}
 
 	@Override
 	public SessionFactoryImplementor getFactory() {
 		return delegate.getFactory();
 	}
 
 	@Override
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		return delegate.list( query, queryParameters );
 	}
 
 	@Override
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		return delegate.iterate( query, queryParameters );
 	}
 
 	@Override
-	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
+	public ScrollableResultsImplementor scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		return delegate.scroll( query, queryParameters );
 	}
 
 	@Override
-	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
+	public ScrollableResultsImplementor scroll(Criteria criteria, ScrollMode scrollMode) {
 		return delegate.scroll( criteria, scrollMode );
 	}
 
 	@Override
 	public List list(Criteria criteria) {
 		return delegate.list( criteria );
 	}
 
 	@Override
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
 		return delegate.listFilter( collection, filter, queryParameters );
 	}
 
 	@Override
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
 		return delegate.iterateFilter( collection, filter, queryParameters );
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException {
 		return delegate.getEntityPersister( entityName, object );
 	}
 
 	@Override
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		return delegate.getEntityUsingInterceptor( key );
 	}
 
 	@Override
 	public Serializable getContextEntityIdentifier(Object object) {
 		return delegate.getContextEntityIdentifier( object );
 	}
 
 	@Override
 	public String bestGuessEntityName(Object object) {
 		return delegate.bestGuessEntityName( object );
 	}
 
 	@Override
 	public String guessEntityName(Object entity) throws HibernateException {
 		return delegate.guessEntityName( entity );
 	}
 
 	@Override
 	public Object instantiate(String entityName, Serializable id) throws HibernateException {
 		return delegate.instantiate( entityName, id );
 	}
 
 	@Override
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
 		return delegate.listCustomQuery( customQuery, queryParameters );
 	}
 
 	@Override
-	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
+	public ScrollableResultsImplementor scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
 		return delegate.scrollCustomQuery( customQuery, queryParameters );
 	}
 
 	@Override
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
 		return delegate.list( spec, queryParameters );
 	}
 
 	@Override
-	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
+	public ScrollableResultsImplementor scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) throws HibernateException {
 		return delegate.scroll( spec, queryParameters );
 	}
 
 	@Override
 	public int getDontFlushFromFind() {
 		return delegate.getDontFlushFromFind();
 	}
 
 	@Override
 	public PersistenceContext getPersistenceContext() {
 		return delegate.getPersistenceContext();
 	}
 
 	@Override
 	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
 		return delegate.executeUpdate( query, queryParameters );
 	}
 
 	@Override
 	public int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters) throws HibernateException {
 		return delegate.executeNativeUpdate( specification, queryParameters );
 	}
 
 	@Override
 	public CacheMode getCacheMode() {
 		return delegate.getCacheMode();
 	}
 
 	@Override
 	public void setCacheMode(CacheMode cm) {
 		delegate.setCacheMode( cm );
 	}
 
 	@Override
 	public boolean isOpen() {
 		return delegate.isOpen();
 	}
 
 	@Override
 	public boolean isConnected() {
 		return delegate.isConnected();
 	}
 
 	@Override
 	public void checkOpen(boolean markForRollbackIfClosed) {
 		delegate.checkOpen( markForRollbackIfClosed );
 	}
 
 	@Override
 	public void markForRollbackOnly() {
 		delegate.markForRollbackOnly();
 	}
 
 	@Override
 	public FlushModeType getFlushMode() {
 		return delegate.getFlushMode();
 	}
 
 	@Override
 	public void setFlushMode(FlushModeType flushModeType) {
 		delegate.setFlushMode( flushModeType );
 	}
 
 	@Override
 	public void setHibernateFlushMode(FlushMode flushMode) {
 		delegate.setHibernateFlushMode( flushMode );
 	}
 
 	@Override
 	public FlushMode getHibernateFlushMode() {
 		return delegate.getHibernateFlushMode();
 	}
 
 	@Override
 	public void setFlushMode(FlushMode fm) {
 		delegate.setHibernateFlushMode( fm );
 	}
 
 	@Override
 	public void lock(Object entity, LockModeType lockMode) {
 		delegate.lock( entity, lockMode );
 	}
 
 	@Override
 	public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {
 		delegate.lock( entity, lockMode, properties );
 	}
 
 	@Override
 	public Connection connection() {
 		return delegate.connection();
 	}
 
 	@Override
 	public void flush() {
 		delegate.flush();
 	}
 
 	@Override
 	public boolean isEventSource() {
 		return delegate.isEventSource();
 	}
 
 	@Override
 	public void afterScrollOperation() {
 		delegate.afterScrollOperation();
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		return delegate.getTransactionCoordinator();
 	}
 
 	@Override
 	public JdbcCoordinator getJdbcCoordinator() {
 		return delegate.getJdbcCoordinator();
 	}
 
 	@Override
 	public JdbcServices getJdbcServices() {
 		return delegate.getJdbcServices();
 	}
 
 	@Override
 	public JdbcSessionContext getJdbcSessionContext() {
 		return delegate.getJdbcSessionContext();
 	}
 
 	@Override
 	public boolean isClosed() {
 		return delegate.isClosed();
 	}
 
 	@Override
 	public boolean shouldAutoClose() {
 		return delegate.shouldAutoClose();
 	}
 
 	@Override
 	public boolean isAutoCloseSessionEnabled() {
 		return delegate.isAutoCloseSessionEnabled();
 	}
 
 	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return delegate.shouldAutoJoinTransaction();
 	}
 
 	@Override
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return delegate.getLoadQueryInfluencers();
 	}
 
 	@Override
 	public ExceptionConverter getExceptionConverter() {
 		return delegate.getExceptionConverter();
 	}
 
 	@Override
 	public SessionEventListenerManager getEventListenerManager() {
 		return delegate.getEventListenerManager();
 	}
 
 	@Override
 	public Transaction accessTransaction() {
 		return delegate.accessTransaction();
 	}
 
 	@Override
 	public Transaction beginTransaction() {
 		return delegate.beginTransaction();
 	}
 
 	@Override
 	public Transaction getTransaction() {
 		return delegate.getTransaction();
 	}
 
 	@Override
 	public void afterTransactionBegin() {
 		delegate.afterTransactionBegin();
 	}
 
 	@Override
 	public void beforeTransactionCompletion() {
 		delegate.beforeTransactionCompletion();
 	}
 
 	@Override
 	public void afterTransactionCompletion(boolean successful, boolean delayed) {
 		delegate.afterTransactionCompletion( successful, delayed );
 	}
 
 	@Override
 	public void flushBeforeTransactionCompletion() {
 		delegate.flushBeforeTransactionCompletion();
 	}
 
 	@Override
 	public EntityManagerFactory getEntityManagerFactory() {
 		return delegate.getFactory();
 	}
 
 	@Override
 	public CriteriaBuilder getCriteriaBuilder() {
 		return delegate.getCriteriaBuilder();
 	}
 
 	@Override
 	public Metamodel getMetamodel() {
 		return delegate.getMetamodel();
 	}
 
 	@Override
 	public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
 		return delegate.createEntityGraph( rootType );
 	}
 
 	@Override
 	public EntityGraph<?> createEntityGraph(String graphName) {
 		return delegate.createEntityGraph( graphName );
 	}
 
 	@Override
 	public EntityGraph<?> getEntityGraph(String graphName) {
 		return delegate.getEntityGraph( graphName );
 	}
 
 	@Override
 	public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
 		return delegate.getEntityGraphs( entityClass );
 	}
 
 	@Override
 	public QueryImplementor getNamedQuery(String name) {
 		return delegate.getNamedQuery( name );
 	}
 
 	@Override
 	public NativeQueryImplementor getNamedSQLQuery(String name) {
 		return delegate.getNamedSQLQuery( name );
 	}
 
 	@Override
 	public NativeQueryImplementor getNamedNativeQuery(String name) {
 		return delegate.getNamedNativeQuery( name );
 	}
 
 	@Override
 	public QueryImplementor createQuery(String queryString) {
 		return delegate.createQuery( queryString );
 	}
 
 	@Override
 	public <T> QueryImplementor<T> createQuery(String queryString, Class<T> resultType) {
 		return delegate.createQuery( queryString, resultType );
 	}
 
 	@Override
 	public <T> QueryImplementor<T> createQuery(CriteriaQuery<T> criteriaQuery) {
 		return delegate.createQuery( criteriaQuery );
 	}
 
 	@Override
 	public QueryImplementor createQuery(CriteriaUpdate updateQuery) {
 		return delegate.createQuery( updateQuery );
 	}
 
 	@Override
 	public QueryImplementor createQuery(CriteriaDelete deleteQuery) {
 		return delegate.createQuery( deleteQuery );
 	}
 
 	@Override
 	public QueryImplementor createNamedQuery(String name) {
 		return delegate.createNamedQuery( name );
 	}
 
 	@Override
 	public <T> QueryImplementor<T> createNamedQuery(String name, Class<T> resultClass) {
 		return delegate.createNamedQuery( name, resultClass );
 	}
 
 	@Override
 	public NativeQueryImplementor createNativeQuery(String sqlString) {
 		return delegate.createNativeQuery( sqlString );
 	}
 
 	@Override
 	public NativeQueryImplementor createNativeQuery(String sqlString, Class resultClass) {
 		return delegate.createNativeQuery( sqlString, resultClass );
 	}
 
 	@Override
 	public NativeQueryImplementor createNativeQuery(String sqlString, String resultSetMapping) {
 		return delegate.createNativeQuery( sqlString, resultSetMapping );
 	}
 
 	@Override
 	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
 		return delegate.createNamedStoredProcedureQuery( name );
 	}
 
 	@Override
 	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
 		return delegate.createNamedStoredProcedureQuery( procedureName );
 	}
 
 	@Override
 	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
 		return delegate.createStoredProcedureQuery( procedureName, resultClasses );
 	}
 
 	@Override
 	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
 		return delegate.createStoredProcedureQuery( procedureName, resultSetMappings );
 	}
 
 	@Override
 	public void joinTransaction() {
 		delegate.joinTransaction();
 	}
 
 	@Override
 	public boolean isJoinedToTransaction() {
 		return delegate.isJoinedToTransaction();
 	}
 
 	@Override
 	public <T> T unwrap(Class<T> cls) {
 		return delegate.unwrap( cls );
 	}
 
 	@Override
 	public Object getDelegate() {
 		return delegate;
 	}
 
 	@Override
 	public NativeQueryImplementor createSQLQuery(String queryString) {
 		return delegate.createSQLQuery( queryString );
 	}
 
 	@Override
 	public ProcedureCall getNamedProcedureCall(String name) {
 		return delegate.getNamedProcedureCall( name );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName) {
 		return delegate.createStoredProcedureCall( procedureName );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		return delegate.createStoredProcedureCall( procedureName, resultClasses );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		return delegate.createStoredProcedureCall( procedureName, resultSetMappings );
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass) {
 		return delegate.createCriteria( persistentClass );
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		return delegate.createCriteria( persistentClass, alias );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName) {
 		return delegate.createCriteria( entityName );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName, String alias) {
 		return delegate.createCriteria( entityName, alias );
 	}
 
 	@Override
 	public SharedSessionBuilder sessionWithOptions() {
 		return delegate.sessionWithOptions();
 	}
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		return delegate.getSessionFactory();
 	}
 
 	@Override
 	public void close() throws HibernateException {
 		delegate.close();
 	}
 
 	@Override
 	public void cancelQuery() throws HibernateException {
 		delegate.cancelQuery();
 	}
 
 	@Override
 	public boolean isDirty() throws HibernateException {
 		return delegate.isDirty();
 	}
 
 	@Override
 	public boolean isDefaultReadOnly() {
 		return delegate.isDefaultReadOnly();
 	}
 
 	@Override
 	public void setDefaultReadOnly(boolean readOnly) {
 		delegate.setDefaultReadOnly( readOnly );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) {
 		return delegate.getIdentifier( object );
 	}
 
 	@Override
 	public boolean contains(String entityName, Object object) {
 		return delegate.contains( entityName, object );
 	}
 
 	@Override
 	public boolean contains(Object object) {
 		return delegate.contains( object );
 	}
 
 	@Override
 	public LockModeType getLockMode(Object entity) {
 		return delegate.getLockMode( entity );
 	}
 
 	@Override
 	public void setProperty(String propertyName, Object value) {
 		delegate.setProperty( propertyName, value );
 	}
 
 	@Override
 	public Map<String, Object> getProperties() {
 		return delegate.getProperties();
 	}
 
 	@Override
 	public void evict(Object object) {
 		delegate.evict( object );
 	}
 
 	@Override
 	public <T> T load(Class<T> theClass, Serializable id, LockMode lockMode) {
 		return delegate.load( theClass, id, lockMode );
 	}
 
 	@Override
 	public <T> T load(Class<T> theClass, Serializable id, LockOptions lockOptions) {
 		return delegate.load( theClass, id, lockOptions );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id, LockMode lockMode) {
 		return delegate.load( entityName, id, lockMode );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) {
 		return delegate.load( entityName, id, lockOptions );
 	}
 
 	@Override
 	public <T> T load(Class<T> theClass, Serializable id) {
 		return delegate.load( theClass, id );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id) {
 		return delegate.load( entityName, id );
 	}
 
 	@Override
 	public void load(Object object, Serializable id) {
 		delegate.load( object, id );
 	}
 
 	@Override
 	public void replicate(Object object, ReplicationMode replicationMode) {
 		delegate.replicate( object, replicationMode );
 	}
 
 	@Override
 	public void replicate(String entityName, Object object, ReplicationMode replicationMode) {
 		delegate.replicate( entityName, object, replicationMode );
 	}
 
 	@Override
 	public Serializable save(Object object) {
 		return delegate.save( object );
 	}
 
 	@Override
 	public Serializable save(String entityName, Object object) {
 		return delegate.save( entityName, object );
 	}
 
 	@Override
 	public void saveOrUpdate(Object object) {
 		delegate.saveOrUpdate( object );
 	}
 
 	@Override
 	public void saveOrUpdate(String entityName, Object object) {
 		delegate.saveOrUpdate( entityName, object );
 	}
 
 	@Override
 	public void update(Object object) {
 		delegate.update( object );
 	}
 
 	@Override
 	public void update(String entityName, Object object) {
 		delegate.update( entityName, object );
 	}
 
 	@Override
 	public Object merge(Object object) {
 		return delegate.merge( object );
 	}
 
 	@Override
 	public Object merge(String entityName, Object object) {
 		return delegate.merge( entityName, object );
 	}
 
 	@Override
 	public void persist(Object object) {
 		delegate.persist( object );
 	}
 
 	@Override
 	public void remove(Object entity) {
 		delegate.remove( entity );
 	}
 
 	@Override
 	public <T> T find(Class<T> entityClass, Object primaryKey) {
 		return delegate.find( entityClass, primaryKey );
 	}
 
 	@Override
 	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
 		return delegate.find( entityClass, primaryKey, properties );
 	}
 
 	@Override
 	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
 		return delegate.find( entityClass, primaryKey, lockMode );
 	}
 
 	@Override
 	public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
 		return delegate.find( entityClass, primaryKey, lockMode, properties );
 	}
 
 	@Override
 	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
 		return delegate.getReference( entityClass, primaryKey );
 	}
 
 	@Override
 	public void persist(String entityName, Object object) {
 		delegate.persist( entityName, object );
 	}
 
 	@Override
 	public void delete(Object object) {
 		delegate.delete( object );
 	}
 
 	@Override
 	public void delete(String entityName, Object object) {
 		delegate.delete( entityName, object );
 	}
 
 	@Override
 	public void lock(Object object, LockMode lockMode) {
 		delegate.lock( object, lockMode );
 	}
 
 	@Override
 	public void lock(String entityName, Object object, LockMode lockMode) {
 		delegate.lock( entityName, object, lockMode );
 	}
 
 	@Override
 	public LockRequest buildLockRequest(LockOptions lockOptions) {
 		return delegate.buildLockRequest( lockOptions );
 	}
 
 	@Override
 	public void refresh(Object object) {
 		delegate.refresh( object );
 	}
 
 	@Override
 	public void refresh(Object entity, Map<String, Object> properties) {
 		delegate.refresh( entity, properties );
 	}
 
 	@Override
 	public void refresh(Object entity, LockModeType lockMode) {
 		delegate.refresh( entity, lockMode );
 	}
 
 	@Override
 	public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {
 		delegate.refresh( entity, lockMode, properties );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object) {
 		delegate.refresh( entityName, object );
 	}
 
 	@Override
 	public void refresh(Object object, LockMode lockMode) {
 		delegate.refresh( object, lockMode );
 	}
 
 	@Override
 	public void refresh(Object object, LockOptions lockOptions) {
 		delegate.refresh( object, lockOptions );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object, LockOptions lockOptions) {
 		delegate.refresh( entityName, object, lockOptions );
 	}
 
 	@Override
 	public LockMode getCurrentLockMode(Object object) {
 		return delegate.getCurrentLockMode( object );
 	}
 
 	@Override
 	public org.hibernate.query.Query createFilter(Object collection, String queryString) {
 		return delegate.createFilter( collection, queryString );
 	}
 
 	@Override
 	public void clear() {
 		delegate.clear();
 	}
 
 	@Override
 	public void detach(Object entity) {
 		delegate.detach( entity );
 	}
 
 	@Override
 	public <T> T get(Class<T> theClass, Serializable id) {
 		return delegate.get( theClass, id );
 	}
 
 	@Override
 	public <T> T get(Class<T> theClass, Serializable id, LockMode lockMode) {
 		return delegate.get( theClass, id, lockMode );
 	}
 
 	@Override
 	public <T> T get(Class<T> theClass, Serializable id, LockOptions lockOptions) {
 		return delegate.get( theClass, id, lockOptions );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id) {
 		return delegate.get( entityName, id );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockMode lockMode) {
 		return delegate.get( entityName, id, lockMode );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) {
 		return delegate.get( entityName, id, lockOptions );
 	}
 
 	@Override
 	public String getEntityName(Object object) {
 		return delegate.getEntityName( object );
 	}
 
 	@Override
 	public IdentifierLoadAccess byId(String entityName) {
 		return delegate.byId( entityName );
 	}
 
 	@Override
 	public <T> MultiIdentifierLoadAccess<T> byMultipleIds(Class<T> entityClass) {
 		return delegate.byMultipleIds( entityClass );
 	}
 
 	@Override
 	public MultiIdentifierLoadAccess byMultipleIds(String entityName) {
 		return delegate.byMultipleIds( entityName );
 	}
 
 	@Override
 	public <T> IdentifierLoadAccess<T> byId(Class<T> entityClass) {
 		return delegate.byId( entityClass );
 	}
 
 	@Override
 	public NaturalIdLoadAccess byNaturalId(String entityName) {
 		return delegate.byNaturalId( entityName );
 	}
 
 	@Override
 	public <T> NaturalIdLoadAccess<T> byNaturalId(Class<T> entityClass) {
 		return delegate.byNaturalId( entityClass );
 	}
 
 	@Override
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName) {
 		return delegate.bySimpleNaturalId( entityName );
 	}
 
 	@Override
 	public <T> SimpleNaturalIdLoadAccess<T> bySimpleNaturalId(Class<T> entityClass) {
 		return delegate.bySimpleNaturalId( entityClass );
 	}
 
 	@Override
 	public Filter enableFilter(String filterName) {
 		return delegate.enableFilter( filterName );
 	}
 
 	@Override
 	public Filter getEnabledFilter(String filterName) {
 		return delegate.getEnabledFilter( filterName );
 	}
 
 	@Override
 	public void disableFilter(String filterName) {
 		delegate.disableFilter( filterName );
 	}
 
 	@Override
 	public SessionStatistics getStatistics() {
 		return delegate.getStatistics();
 	}
 
 	@Override
 	public boolean isReadOnly(Object entityOrProxy) {
 		return delegate.isReadOnly( entityOrProxy );
 	}
 
 	@Override
 	public void setReadOnly(Object entityOrProxy, boolean readOnly) {
 		delegate.setReadOnly( entityOrProxy, readOnly );
 	}
 
 	@Override
 	public void doWork(Work work) throws HibernateException {
 		delegate.doWork( work );
 	}
 
 	@Override
 	public <T> T doReturningWork(ReturningWork<T> work) throws HibernateException {
 		return delegate.doReturningWork( work );
 	}
 
 	@Override
 	public Connection disconnect() {
 		return delegate.disconnect();
 	}
 
 	@Override
 	public void reconnect(Connection connection) {
 		delegate.reconnect( connection );
 	}
 
 	@Override
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		return delegate.isFetchProfileEnabled( name );
 	}
 
 	@Override
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		delegate.enableFetchProfile( name );
 	}
 
 	@Override
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		delegate.disableFetchProfile( name );
 	}
 
 	@Override
 	public TypeHelper getTypeHelper() {
 		return delegate.getTypeHelper();
 	}
 
 	@Override
 	public LobHelper getLobHelper() {
 		return delegate.getLobHelper();
 	}
 
 	@Override
 	public void addEventListeners(SessionEventListener... listeners) {
 		delegate.addEventListeners( listeners );
 	}
 
 	@Override
 	public boolean isFlushBeforeCompletionEnabled() {
 		return delegate.isFlushBeforeCompletionEnabled();
 	}
 
 	@Override
 	public ActionQueue getActionQueue() {
 		return delegate.getActionQueue();
 	}
 
 	@Override
 	public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
 		return delegate.instantiate( persister, id );
 	}
 
 	@Override
 	public void forceFlush(EntityEntry e) throws HibernateException {
 		delegate.forceFlush( e );
 	}
 
 	@Override
 	public void merge(String entityName, Object object, Map copiedAlready) throws HibernateException {
 		delegate.merge( entityName, object, copiedAlready );
 	}
 
 	@Override
 	public void persist(String entityName, Object object, Map createdAlready) throws HibernateException {
 		delegate.persist( entityName, object, createdAlready );
 	}
 
 	@Override
 	public void persistOnFlush(String entityName, Object object, Map copiedAlready) {
 		delegate.persistOnFlush( entityName, object, copiedAlready );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object, Map refreshedAlready) throws HibernateException {
 		delegate.refresh( entityName, object, refreshedAlready );
 	}
 
 	@Override
 	public void delete(String entityName, Object child, boolean isCascadeDeleteEnabled, Set transientEntities) {
 		delegate.delete( entityName, child, isCascadeDeleteEnabled, transientEntities );
 	}
 
 	@Override
 	public void removeOrphanBeforeUpdates(String entityName, Object child) {
 		delegate.removeOrphanBeforeUpdates( entityName, child );
 	}
 
 	@Override
 	public SessionImplementor getSession() {
 		return this;
 	}
 
 	@Override
 	public boolean useStreamForLobBinding() {
 		return delegate.useStreamForLobBinding();
 	}
 
 	@Override
 	public LobCreator getLobCreator() {
 		return delegate.getLobCreator();
 	}
 
 	@Override
 	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 		return delegate.remapSqlTypeDescriptor( sqlTypeDescriptor );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SharedSessionContractImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SharedSessionContractImplementor.java
index 8166fbe28c..81e03d7339 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SharedSessionContractImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SharedSessionContractImplementor.java
@@ -1,412 +1,413 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.spi;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.UUID;
 import javax.persistence.FlushModeType;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SharedSessionContract;
 import org.hibernate.Transaction;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.query.spi.QueryProducerImplementor;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.resource.jdbc.spi.JdbcSessionOwner;
 import org.hibernate.resource.transaction.spi.TransactionCoordinator;
 import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
 import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder.Options;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
  * Defines the internal contract shared between {@link org.hibernate.Session} and
  * {@link org.hibernate.StatelessSession} as used by other parts of Hibernate (such as
  * {@link org.hibernate.type.Type}, {@link EntityPersister} and
  * {@link org.hibernate.persister.collection.CollectionPersister} implementors
  *
  * A Session, through this interface and SharedSessionContractImplementor, implements:<ul>
  *     <li>
  *         {@link org.hibernate.resource.jdbc.spi.JdbcSessionOwner} to drive the behavior of a "JDBC session".
  *         Can therefor be used to construct a JdbcCoordinator, which (for now) models a "JDBC session"
  *     </li>
  *     <li>
  *         {@link Options}
  *         to drive the creation of the {@link TransactionCoordinator} delegate.
  *         This allows it to be passed along to
  *         {@link TransactionCoordinatorBuilder#buildTransactionCoordinator}
  *     </li>
  *     <li>
  *         {@link org.hibernate.engine.jdbc.LobCreationContext} to act as the context for JDBC LOB instance creation
  *     </li>
  *     <li>
  *         {@link org.hibernate.type.descriptor.WrapperOptions} to fulfill the behavior needed while
  *         binding/extracting values to/from JDBC as part of the Type contracts
  *     </li>
  * </ul>
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface SharedSessionContractImplementor
 		extends SharedSessionContract, JdbcSessionOwner, Options, LobCreationContext, WrapperOptions, QueryProducerImplementor {
 
 	// todo : this is the shared contract between Session and StatelessSession, but it defines methods that StatelessSession does not implement
 	//	(it just throws UnsupportedOperationException).  To me it seems like it is better to properly isolate those methods
 	//	into just the Session hierarchy.  They include (at least):
 	//		1) get/set CacheMode
 	//		2) get/set FlushMode
 	//		3) get/set (default) read-only
 	//		4) #setAutoClear
 	//		5) #disableTransactionAutoJoin
 
 	/**
 	 * Get the creating <tt>SessionFactoryImplementor</tt>
 	 */
 	SessionFactoryImplementor getFactory();
 
 	SessionEventListenerManager getEventListenerManager();
 
 	/**
 	 * Get the persistence context for this session
 	 */
 	PersistenceContext getPersistenceContext();
 
 	JdbcCoordinator getJdbcCoordinator();
 
 	JdbcServices getJdbcServices();
 
 	/**
 	 * The multi-tenancy tenant identifier, if one.
 	 *
 	 * @return The tenant identifier; may be {@code null}
 	 */
 	String getTenantIdentifier();
 
 	/**
 	 * A UUID associated with each Session.  Useful mainly for logging.
 	 *
 	 * @return The UUID
 	 */
 	UUID getSessionIdentifier();
 
 	/**
 	 * Checks whether the session is closed.  Provided separately from
 	 * {@link #isOpen()} as this method does not attempt any JTA synchronization
 	 * registration, where as {@link #isOpen()} does; which makes this one
 	 * nicer to use for most internal purposes.
 	 *
 	 * @return {@code true} if the session is closed; {@code false} otherwise.
 	 */
 	boolean isClosed();
 
 	/**
 	 * Performs a check whether the Session is open, and if not:<ul>
 	 *     <li>marks current transaction (if one) for rollback only</li>
 	 *     <li>throws an IllegalStateException (JPA defines the exception type)</li>
 	 * </ul>
 	 */
 	default void checkOpen() {
 		checkOpen( true );
 	}
 
 	/**
 	 * Performs a check whether the Session is open, and if not:<ul>
 	 *     <li>if {@code markForRollbackIfClosed} is true, marks current transaction (if one) for rollback only</li>
 	 *     <li>throws an IllegalStateException (JPA defines the exception type)</li>
 	 * </ul>
 	 */
 	void checkOpen(boolean markForRollbackIfClosed);
 
 	/**
 	 * Marks current transaction (if one) for rollback only
 	 */
 	void markForRollbackOnly();
 
 	/**
 	 * System time beforeQuery the start of the transaction
 	 */
 	long getTimestamp();
 
 	/**
 	 * Does this <tt>Session</tt> have an active Hibernate transaction
 	 * or is there a JTA transaction in progress?
 	 */
 	boolean isTransactionInProgress();
 
 	/**
 	 * Provides access to the underlying transaction or creates a new transaction if
 	 * one does not already exist or is active.  This is primarily for internal or
 	 * integrator use.
 	 *
 	 * @return the transaction
      */
 	Transaction accessTransaction();
 
 	/**
 	 * Hide the changing requirements of entity key creation
 	 *
 	 * @param id The entity id
 	 * @param persister The entity persister
 	 *
 	 * @return The entity key
 	 */
 	EntityKey generateEntityKey(Serializable id, EntityPersister persister);
 
 	/**
 	 * Retrieves the interceptor currently in use by this event source.
 	 *
 	 * @return The interceptor.
 	 */
 	Interceptor getInterceptor();
 
 	/**
 	 * Enable/disable automatic cache clearing from afterQuery transaction
 	 * completion (for EJB3)
 	 */
 	void setAutoClear(boolean enabled);
 
 	/**
 	 * Initialize the collection (if not already initialized)
 	 */
 	void initializeCollection(PersistentCollection collection, boolean writing)
 			throws HibernateException;
 
 	/**
 	 * Load an instance without checking if it was deleted.
 	 * <p/>
 	 * When <tt>nullable</tt> is disabled this method may create a new proxy or
 	 * return an existing proxy; if it does not exist, throw an exception.
 	 * <p/>
 	 * When <tt>nullable</tt> is enabled, the method does not create new proxies
 	 * (but might return an existing proxy); if it does not exist, return
 	 * <tt>null</tt>.
 	 * <p/>
 	 * When <tt>eager</tt> is enabled, the object is eagerly fetched
 	 */
 	Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable)
 			throws HibernateException;
 
 	/**
 	 * Load an instance immediately. This method is only called when lazily initializing a proxy.
 	 * Do not return the proxy.
 	 */
 	Object immediateLoad(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * Execute a <tt>find()</tt> query
 	 */
 	List list(String query, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Execute an <tt>iterate()</tt> query
 	 */
 	Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Execute a <tt>scroll()</tt> query
 	 */
-	ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException;
+	ScrollableResultsImplementor scroll(String query, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Execute a criteria query
 	 */
-	ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode);
+	ScrollableResultsImplementor scroll(Criteria criteria, ScrollMode scrollMode);
 
 	/**
 	 * Execute a criteria query
 	 */
 	List list(Criteria criteria);
 
 	/**
 	 * Execute a filter
 	 */
 	List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Iterate a filter
 	 */
 	Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 			throws HibernateException;
 
 	/**
 	 * Get the <tt>EntityPersister</tt> for any instance
 	 *
 	 * @param entityName optional entity name
 	 * @param object the entity instance
 	 */
 	EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException;
 
 	/**
 	 * Get the entity instance associated with the given <tt>Key</tt>,
 	 * calling the Interceptor if necessary
 	 */
 	Object getEntityUsingInterceptor(EntityKey key) throws HibernateException;
 
 	/**
 	 * Return the identifier of the persistent object, or null if
 	 * not associated with the session
 	 */
 	Serializable getContextEntityIdentifier(Object object);
 
 	/**
 	 * The best guess entity name for an entity not in an association
 	 */
 	String bestGuessEntityName(Object object);
 
 	/**
 	 * The guessed entity name for an entity not in an association
 	 */
 	String guessEntityName(Object entity) throws HibernateException;
 
 	/**
 	 * Instantiate the entity class, initializing with the given identifier
 	 */
 	Object instantiate(String entityName, Serializable id) throws HibernateException;
 
 	/**
 	 * Execute an SQL Query
 	 */
 	List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 			throws HibernateException;
 
 	/**
 	 * Execute an SQL Query
 	 */
-	ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
+	ScrollableResultsImplementor scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 			throws HibernateException;
 
 	/**
 	 * Execute a native SQL query, and return the results as a fully built list.
 	 *
 	 * @param spec The specification of the native SQL query to execute.
 	 * @param queryParameters The parameters by which to perform the execution.
 	 *
 	 * @return The result list.
 	 *
 	 * @throws HibernateException
 	 */
 	List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters)
 			throws HibernateException;
 
 	/**
 	 * Execute a native SQL query, and return the results as a scrollable result.
 	 *
 	 * @param spec The specification of the native SQL query to execute.
 	 * @param queryParameters The parameters by which to perform the execution.
 	 *
 	 * @return The resulting scrollable result.
 	 *
 	 * @throws HibernateException
 	 */
-	ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters);
+	ScrollableResultsImplementor scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters);
 
 	int getDontFlushFromFind();
 
 	/**
 	 * Execute a HQL update or delete query
 	 */
 	int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Execute a native SQL update or delete query
 	 */
 	int executeNativeUpdate(NativeSQLQuerySpecification specification, QueryParameters queryParameters)
 			throws HibernateException;
 
 
 	CacheMode getCacheMode();
 
 	void setCacheMode(CacheMode cm);
 
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
 	 *
 	 * @deprecated (since 5.2) use {@link #setHibernateFlushMode(FlushMode)} instead
 	 */
 	@Deprecated
 	void setFlushMode(FlushMode flushMode);
 
 	/**
 	 * Get the flush mode for this session.
 	 * <p/>
 	 * For users of the Hibernate native APIs, we've had to rename this method
 	 * as defined by Hibernate historically because the JPA contract defines a method of the same
 	 * name, but returning the JPA {@link FlushModeType} rather than Hibernate's {@link FlushMode}.  For
 	 * the former behavior, use {@link #getHibernateFlushMode()} instead.
 	 *
 	 * @return The FlushModeType in effect for this Session.
 	 */
 	FlushModeType getFlushMode();
 
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
 	 */
 	void setHibernateFlushMode(FlushMode flushMode);
 
 	/**
 	 * Get the current flush mode for this session.
 	 *
 	 * @return The flush mode
 	 */
 	FlushMode getHibernateFlushMode();
 
 	Connection connection();
 
 	void flush();
 
 	boolean isEventSource();
 
 	void afterScrollOperation();
 
 	boolean shouldAutoClose();
 
 	boolean isAutoCloseSessionEnabled();
 
 	/**
 	 * Get the load query influencers associated with this session.
 	 *
 	 * @return the load query influencers associated with this session;
 	 *         should never be null.
 	 */
 	LoadQueryInfluencers getLoadQueryInfluencers();
 
 	ExceptionConverter getExceptionConverter();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
index 35235e23e6..8442a5f94e 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
@@ -1,631 +1,632 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.ast;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.engine.query.spi.EntityGraphQueryHint;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.QueryExecutionRequestException;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.antlr.HqlTokenTypes;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.exec.BasicExecutor;
 import org.hibernate.hql.internal.ast.exec.DeleteExecutor;
 import org.hibernate.hql.internal.ast.exec.MultiTableDeleteExecutor;
 import org.hibernate.hql.internal.ast.exec.MultiTableUpdateExecutor;
 import org.hibernate.hql.internal.ast.exec.StatementExecutor;
 import org.hibernate.hql.internal.ast.tree.AggregatedSelectExpression;
 import org.hibernate.hql.internal.ast.tree.FromElement;
 import org.hibernate.hql.internal.ast.tree.InsertStatement;
 import org.hibernate.hql.internal.ast.tree.QueryNode;
 import org.hibernate.hql.internal.ast.tree.Statement;
 import org.hibernate.hql.internal.ast.util.ASTPrinter;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.internal.ast.util.NodeTraverser;
 import org.hibernate.hql.spi.FilterTranslator;
 import org.hibernate.hql.spi.ParameterTranslations;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.IdentitySet;
 import org.hibernate.loader.hql.QueryLoader;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.entity.Queryable;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 import antlr.ANTLRException;
 import antlr.RecognitionException;
 import antlr.TokenStreamException;
 import antlr.collections.AST;
 
 /**
  * A QueryTranslator that uses an Antlr-based parser.
  *
  * @author Joshua Davis (pgmjsd@sourceforge.net)
  */
 public class QueryTranslatorImpl implements FilterTranslator {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			QueryTranslatorImpl.class.getName()
 	);
 
 	private SessionFactoryImplementor factory;
 
 	private final String queryIdentifier;
 	private String hql;
 	private boolean shallowQuery;
 	private Map tokenReplacements;
 
 	//TODO:this is only needed during compilation .. can we eliminate the instvar?
 	private Map enabledFilters;
 
 	private boolean compiled;
 	private QueryLoader queryLoader;
 	private StatementExecutor statementExecutor;
 
 	private Statement sqlAst;
 	private String sql;
 
 	private ParameterTranslations paramTranslations;
 	private List<ParameterSpecification> collectedParameterSpecifications;
 	
 	private EntityGraphQueryHint entityGraphQueryHint;
 
 
 	/**
 	 * Creates a new AST-based query translator.
 	 *
 	 * @param queryIdentifier The query-identifier (used in stats collection)
 	 * @param query The hql query to translate
 	 * @param enabledFilters Currently enabled filters
 	 * @param factory The session factory constructing this translator instance.
 	 */
 	public QueryTranslatorImpl(
 			String queryIdentifier,
 			String query,
 			Map enabledFilters,
 			SessionFactoryImplementor factory) {
 		this.queryIdentifier = queryIdentifier;
 		this.hql = query;
 		this.compiled = false;
 		this.shallowQuery = false;
 		this.enabledFilters = enabledFilters;
 		this.factory = factory;
 	}
 	
 	public QueryTranslatorImpl(
 			String queryIdentifier,
 			String query,
 			Map enabledFilters,
 			SessionFactoryImplementor factory,
 			EntityGraphQueryHint entityGraphQueryHint) {
 		this( queryIdentifier, query, enabledFilters, factory );
 		this.entityGraphQueryHint = entityGraphQueryHint;
 	}
 
 	/**
 	 * Compile a "normal" query. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 *
 	 * @param replacements Defined query substitutions.
 	 * @param shallow      Does this represent a shallow (scalar or entity-id) select?
 	 * @throws QueryException   There was a problem parsing the query string.
 	 * @throws MappingException There was a problem querying defined mappings.
 	 */
 	@Override
 	public void compile(
 			Map replacements,
 			boolean shallow) throws QueryException, MappingException {
 		doCompile( replacements, shallow, null );
 	}
 
 	/**
 	 * Compile a filter. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 *
 	 * @param collectionRole the role name of the collection used as the basis for the filter.
 	 * @param replacements   Defined query substitutions.
 	 * @param shallow        Does this represent a shallow (scalar or entity-id) select?
 	 * @throws QueryException   There was a problem parsing the query string.
 	 * @throws MappingException There was a problem querying defined mappings.
 	 */
 	@Override
 	public void compile(
 			String collectionRole,
 			Map replacements,
 			boolean shallow) throws QueryException, MappingException {
 		doCompile( replacements, shallow, collectionRole );
 	}
 
 	/**
 	 * Performs both filter and non-filter compiling.
 	 *
 	 * @param replacements   Defined query substitutions.
 	 * @param shallow        Does this represent a shallow (scalar or entity-id) select?
 	 * @param collectionRole the role name of the collection used as the basis for the filter, NULL if this
 	 *                       is not a filter.
 	 */
 	private synchronized void doCompile(Map replacements, boolean shallow, String collectionRole) {
 		// If the query is already compiled, skip the compilation.
 		if ( compiled ) {
 			LOG.debug( "compile() : The query is already compiled, skipping..." );
 			return;
 		}
 
 		// Remember the parameters for the compilation.
 		this.tokenReplacements = replacements;
 		if ( tokenReplacements == null ) {
 			tokenReplacements = new HashMap();
 		}
 		this.shallowQuery = shallow;
 
 		try {
 			// PHASE 1 : Parse the HQL into an AST.
 			final HqlParser parser = parse( true );
 
 			// PHASE 2 : Analyze the HQL AST, and produce an SQL AST.
 			final HqlSqlWalker w = analyze( parser, collectionRole );
 
 			sqlAst = (Statement) w.getAST();
 
 			// at some point the generate phase needs to be moved out of here,
 			// because a single object-level DML might spawn multiple SQL DML
 			// command executions.
 			//
 			// Possible to just move the sql generation for dml stuff, but for
 			// consistency-sake probably best to just move responsiblity for
 			// the generation phase completely into the delegates
 			// (QueryLoader/StatementExecutor) themselves.  Also, not sure why
 			// QueryLoader currently even has a dependency on this at all; does
 			// it need it?  Ideally like to see the walker itself given to the delegates directly...
 
 			if ( sqlAst.needsExecutor() ) {
 				statementExecutor = buildAppropriateStatementExecutor( w );
 			}
 			else {
 				// PHASE 3 : Generate the SQL.
 				generate( (QueryNode) sqlAst );
 				queryLoader = new QueryLoader( this, factory, w.getSelectClause() );
 			}
 
 			compiled = true;
 		}
 		catch ( QueryException qe ) {
 			if ( qe.getQueryString() == null ) {
 				throw qe.wrapWithQueryString( hql );
 			}
 			else {
 				throw qe;
 			}
 		}
 		catch ( RecognitionException e ) {
 			// we do not actually propagate ANTLRExceptions as a cause, so
 			// log it here for diagnostic purposes
 			LOG.trace( "Converted antlr.RecognitionException", e );
 			throw QuerySyntaxException.convert( e, hql );
 		}
 		catch ( ANTLRException e ) {
 			// we do not actually propagate ANTLRExceptions as a cause, so
 			// log it here for diagnostic purposes
 			LOG.trace( "Converted antlr.ANTLRException", e );
 			throw new QueryException( e.getMessage(), hql );
 		}
 
 		//only needed during compilation phase...
 		this.enabledFilters = null;
 	}
 
 	private void generate(AST sqlAst) throws QueryException, RecognitionException {
 		if ( sql == null ) {
 			final SqlGenerator gen = new SqlGenerator( factory );
 			gen.statement( sqlAst );
 			sql = gen.getSQL();
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "HQL: %s", hql );
 				LOG.debugf( "SQL: %s", sql );
 			}
 			gen.getParseErrorHandler().throwQueryException();
 			collectedParameterSpecifications = gen.getCollectedParameters();
 		}
 	}
 
 	private static final ASTPrinter SQL_TOKEN_PRINTER = new ASTPrinter( SqlTokenTypes.class );
 
 	private HqlSqlWalker analyze(HqlParser parser, String collectionRole) throws QueryException, RecognitionException {
 		final HqlSqlWalker w = new HqlSqlWalker( this, factory, parser, tokenReplacements, collectionRole );
 		final AST hqlAst = parser.getAST();
 
 		// Transform the tree.
 		w.statement( hqlAst );
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debug( SQL_TOKEN_PRINTER.showAsString( w.getAST(), "--- SQL AST ---" ) );
 		}
 
 		w.getParseErrorHandler().throwQueryException();
 
 		return w;
 	}
 
 	private HqlParser parse(boolean filter) throws TokenStreamException, RecognitionException {
 		// Parse the query string into an HQL AST.
 		final HqlParser parser = HqlParser.getInstance( hql );
 		parser.setFilter( filter );
 
 		LOG.debugf( "parse() - HQL: %s", hql );
 		parser.statement();
 
 		final AST hqlAst = parser.getAST();
 
 		final NodeTraverser walker = new NodeTraverser( new JavaConstantConverter( factory ) );
 		walker.traverseDepthFirst( hqlAst );
 
 		showHqlAst( hqlAst );
 
 		parser.getParseErrorHandler().throwQueryException();
 		return parser;
 	}
 
 	private static final ASTPrinter HQL_TOKEN_PRINTER = new ASTPrinter( HqlTokenTypes.class );
 
 	void showHqlAst(AST hqlAst) {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debug( HQL_TOKEN_PRINTER.showAsString( hqlAst, "--- HQL AST ---" ) );
 		}
 	}
 
 	private void errorIfDML() throws HibernateException {
 		if ( sqlAst.needsExecutor() ) {
 			throw new QueryExecutionRequestException( "Not supported for DML operations", hql );
 		}
 	}
 
 	private void errorIfSelect() throws HibernateException {
 		if ( !sqlAst.needsExecutor() ) {
 			throw new QueryExecutionRequestException( "Not supported for select queries", hql );
 		}
 	}
 	@Override
 	public String getQueryIdentifier() {
 		return queryIdentifier;
 	}
 
 	public Statement getSqlAST() {
 		return sqlAst;
 	}
 
 	private HqlSqlWalker getWalker() {
 		return sqlAst.getWalker();
 	}
 
 	/**
 	 * Types of the return values of an <tt>iterate()</tt> style query.
 	 *
 	 * @return an array of <tt>Type</tt>s.
 	 */
 	@Override
 	public Type[] getReturnTypes() {
 		errorIfDML();
 		return getWalker().getReturnTypes();
 	}
 	@Override
 	public String[] getReturnAliases() {
 		errorIfDML();
 		return getWalker().getReturnAliases();
 	}
 	@Override
 	public String[][] getColumnNames() {
 		errorIfDML();
 		return getWalker().getSelectClause().getColumnNames();
 	}
 	@Override
 	public Set<Serializable> getQuerySpaces() {
 		return getWalker().getQuerySpaces();
 	}
 
 	@Override
 	public List list(SharedSessionContractImplementor session, QueryParameters queryParameters)
 			throws HibernateException {
 		// Delegate to the QueryLoader...
 		errorIfDML();
 
 		final QueryNode query = (QueryNode) sqlAst;
 		final boolean hasLimit = queryParameters.getRowSelection() != null && queryParameters.getRowSelection().definesLimits();
 		final boolean needsDistincting = ( query.getSelectClause().isDistinct() || hasLimit ) && containsCollectionFetches();
 
 		QueryParameters queryParametersToUse;
 		if ( hasLimit && containsCollectionFetches() ) {
 			LOG.firstOrMaxResultsSpecifiedWithCollectionFetch();
 			RowSelection selection = new RowSelection();
 			selection.setFetchSize( queryParameters.getRowSelection().getFetchSize() );
 			selection.setTimeout( queryParameters.getRowSelection().getTimeout() );
 			queryParametersToUse = queryParameters.createCopyUsing( selection );
 		}
 		else {
 			queryParametersToUse = queryParameters;
 		}
 
 		List results = queryLoader.list( session, queryParametersToUse );
 
 		if ( needsDistincting ) {
 			int includedCount = -1;
 			// NOTE : firstRow is zero-based
 			int first = !hasLimit || queryParameters.getRowSelection().getFirstRow() == null
 						? 0
 						: queryParameters.getRowSelection().getFirstRow();
 			int max = !hasLimit || queryParameters.getRowSelection().getMaxRows() == null
 						? -1
 						: queryParameters.getRowSelection().getMaxRows();
 			List tmp = new ArrayList();
 			IdentitySet distinction = new IdentitySet();
 			for ( final Object result : results ) {
 				if ( !distinction.add( result ) ) {
 					continue;
 				}
 				includedCount++;
 				if ( includedCount < first ) {
 					continue;
 				}
 				tmp.add( result );
 				// NOTE : ( max - 1 ) because first is zero-based while max is not...
 				if ( max >= 0 && ( includedCount - first ) >= ( max - 1 ) ) {
 					break;
 				}
 			}
 			results = tmp;
 		}
 
 		return results;
 	}
 
 	/**
 	 * Return the query results as an iterator
 	 */
 	@Override
 	public Iterator iterate(QueryParameters queryParameters, EventSource session)
 			throws HibernateException {
 		// Delegate to the QueryLoader...
 		errorIfDML();
 		return queryLoader.iterate( queryParameters, session );
 	}
 
 	/**
 	 * Return the query results, as an instance of <tt>ScrollableResults</tt>
 	 */
 	@Override
-	public ScrollableResults scroll(QueryParameters queryParameters, SharedSessionContractImplementor session)
+	public ScrollableResultsImplementor scroll(QueryParameters queryParameters, SharedSessionContractImplementor session)
 			throws HibernateException {
 		// Delegate to the QueryLoader...
 		errorIfDML();
 		return queryLoader.scroll( queryParameters, session );
 	}
 	@Override
 	public int executeUpdate(QueryParameters queryParameters, SharedSessionContractImplementor session)
 			throws HibernateException {
 		errorIfSelect();
 		return statementExecutor.execute( queryParameters, session );
 	}
 
 	/**
 	 * The SQL query string to be called; implemented by all subclasses
 	 */
 	@Override
 	public String getSQLString() {
 		return sql;
 	}
 	@Override
 	public List<String> collectSqlStrings() {
 		ArrayList<String> list = new ArrayList<String>();
 		if ( isManipulationStatement() ) {
 			String[] sqlStatements = statementExecutor.getSqlStatements();
 			Collections.addAll( list, sqlStatements );
 		}
 		else {
 			list.add( sql );
 		}
 		return list;
 	}
 
 	// -- Package local methods for the QueryLoader delegate --
 
 	public boolean isShallowQuery() {
 		return shallowQuery;
 	}
 	@Override
 	public String getQueryString() {
 		return hql;
 	}
 	@Override
 	public Map getEnabledFilters() {
 		return enabledFilters;
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		return getWalker().getNamedParameterLocations( name );
 	}
 	@Override
 	public boolean containsCollectionFetches() {
 		errorIfDML();
 		List collectionFetches = ( (QueryNode) sqlAst ).getFromClause().getCollectionFetches();
 		return collectionFetches != null && collectionFetches.size() > 0;
 	}
 	@Override
 	public boolean isManipulationStatement() {
 		return sqlAst.needsExecutor();
 	}
 	@Override
 	public void validateScrollability() throws HibernateException {
 		// Impl Note: allows multiple collection fetches as long as the
 		// entire fecthed graph still "points back" to a single
 		// root entity for return
 
 		errorIfDML();
 
 		final QueryNode query = (QueryNode) sqlAst;
 
 		// If there are no collection fetches, then no further checks are needed
 		List collectionFetches = query.getFromClause().getCollectionFetches();
 		if ( collectionFetches.isEmpty() ) {
 			return;
 		}
 
 		// A shallow query is ok (although technically there should be no fetching here...)
 		if ( isShallowQuery() ) {
 			return;
 		}
 
 		// Otherwise, we have a non-scalar select with defined collection fetch(es).
 		// Make sure that there is only a single root entity in the return (no tuples)
 		if ( getReturnTypes().length > 1 ) {
 			throw new HibernateException( "cannot scroll with collection fetches and returned tuples" );
 		}
 
 		FromElement owner = null;
 		for ( Object o : query.getSelectClause().getFromElementsForLoad() ) {
 			// should be the first, but just to be safe...
 			final FromElement fromElement = (FromElement) o;
 			if ( fromElement.getOrigin() == null ) {
 				owner = fromElement;
 				break;
 			}
 		}
 
 		if ( owner == null ) {
 			throw new HibernateException( "unable to locate collection fetch(es) owner for scrollability checks" );
 		}
 
 		// This is not strictly true.  We actually just need to make sure that
 		// it is ordered by root-entity PK and that that order-by comes beforeQuery
 		// any non-root-entity ordering...
 
 		AST primaryOrdering = query.getOrderByClause().getFirstChild();
 		if ( primaryOrdering != null ) {
 			// TODO : this is a bit dodgy, come up with a better way to check this (plus see above comment)
 			String [] idColNames = owner.getQueryable().getIdentifierColumnNames();
 			String expectedPrimaryOrderSeq = StringHelper.join(
 					", ",
 					StringHelper.qualify( owner.getTableAlias(), idColNames )
 			);
 			if (  !primaryOrdering.getText().startsWith( expectedPrimaryOrderSeq ) ) {
 				throw new HibernateException( "cannot scroll results with collection fetches which are not ordered primarily by the root entity's PK" );
 			}
 		}
 	}
 
 	private StatementExecutor buildAppropriateStatementExecutor(HqlSqlWalker walker) {
 		final Statement statement = (Statement) walker.getAST();
 		if ( walker.getStatementType() == HqlSqlTokenTypes.DELETE ) {
 			final FromElement fromElement = walker.getFinalFromClause().getFromElement();
 			final Queryable persister = fromElement.getQueryable();
 			if ( persister.isMultiTable() ) {
 				return new MultiTableDeleteExecutor( walker );
 			}
 			else {
 				return new DeleteExecutor( walker, persister );
 			}
 		}
 		else if ( walker.getStatementType() == HqlSqlTokenTypes.UPDATE ) {
 			final FromElement fromElement = walker.getFinalFromClause().getFromElement();
 			final Queryable persister = fromElement.getQueryable();
 			if ( persister.isMultiTable() ) {
 				// even here, if only properties mapped to the "base table" are referenced
 				// in the set and where clauses, this could be handled by the BasicDelegate.
 				// TODO : decide if it is better performance-wise to doAfterTransactionCompletion that check, or to simply use the MultiTableUpdateDelegate
 				return new MultiTableUpdateExecutor( walker );
 			}
 			else {
 				return new BasicExecutor( walker, persister );
 			}
 		}
 		else if ( walker.getStatementType() == HqlSqlTokenTypes.INSERT ) {
 			return new BasicExecutor( walker, ( (InsertStatement) statement ).getIntoClause().getQueryable() );
 		}
 		else {
 			throw new QueryException( "Unexpected statement type" );
 		}
 	}
 	@Override
 	public ParameterTranslations getParameterTranslations() {
 		if ( paramTranslations == null ) {
 			paramTranslations = new ParameterTranslationsImpl( getWalker().getParameters() );
 		}
 		return paramTranslations;
 	}
 
 	public List<ParameterSpecification> getCollectedParameterSpecifications() {
 		return collectedParameterSpecifications;
 	}
 
 	@Override
 	public Class getDynamicInstantiationResultType() {
 		AggregatedSelectExpression aggregation = queryLoader.getAggregatedSelectExpression();
 		return aggregation == null ? null : aggregation.getAggregationResultType();
 	}
 
 	public static class JavaConstantConverter implements NodeTraverser.VisitationStrategy {
 		private final SessionFactoryImplementor factory;
 		private AST dotRoot;
 
 		public JavaConstantConverter(SessionFactoryImplementor factory) {
 
 			this.factory = factory;
 		}
 
 		@Override
 		public void visit(AST node) {
 			if ( dotRoot != null ) {
 				// we are already processing a dot-structure
 				if ( ASTUtil.isSubtreeChild( dotRoot, node ) ) {
 					return;
 				}
 				// we are now at a new tree level
 				dotRoot = null;
 			}
 
 			if ( node.getType() == HqlTokenTypes.DOT ) {
 				dotRoot = node;
 				handleDotStructure( dotRoot );
 			}
 		}
 		private void handleDotStructure(AST dotStructureRoot) {
 			final String expression = ASTUtil.getPathText( dotStructureRoot );
 			final Object constant = ReflectHelper.getConstantValue( expression, factory.getServiceRegistry().getService( ClassLoaderService.class ) );
 			if ( constant != null ) {
 				dotStructureRoot.setFirstChild( null );
 				dotStructureRoot.setType( HqlTokenTypes.JAVA_CONSTANT );
 				dotStructureRoot.setText( expression );
 			}
 		}
 	}
 
 	public EntityGraphQueryHint getEntityGraphQueryHint() {
 		return entityGraphQueryHint;
 	}
 
 	public void setEntityGraphQueryHint(EntityGraphQueryHint entityGraphQueryHint) {
 		this.entityGraphQueryHint = entityGraphQueryHint;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
index f3c09251f5..a02d09af9e 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
@@ -1,1307 +1,1308 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.internal.classic;
 
 import java.io.Serializable;
 import java.lang.reflect.Constructor;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.TimeUnit;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.hql.internal.NameGenerator;
 import org.hibernate.hql.spi.FilterTranslator;
 import org.hibernate.hql.spi.ParameterTranslations;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.IteratorImpl;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.BasicLoader;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.sql.QuerySelect;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * An instance of <tt>QueryTranslator</tt> translates a Hibernate
  * query string to SQL.
  */
 public class QueryTranslatorImpl extends BasicLoader implements FilterTranslator {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( QueryTranslatorImpl.class );
 
 	private static final String[] NO_RETURN_ALIASES = new String[] {};
 
 	private final String queryIdentifier;
 	private final String queryString;
 
 	private final Map typeMap = new LinkedHashMap();
 	private final Map collections = new LinkedHashMap();
 	private List returnedTypes = new ArrayList();
 	private final List fromTypes = new ArrayList();
 	private final List scalarTypes = new ArrayList();
 	private final Map namedParameters = new HashMap();
 	private final Map aliasNames = new HashMap();
 	private final Map oneToOneOwnerNames = new HashMap();
 	private final Map uniqueKeyOwnerReferences = new HashMap();
 	private final Map decoratedPropertyMappings = new HashMap();
 
 	private final List scalarSelectTokens = new ArrayList();
 	private final List whereTokens = new ArrayList();
 	private final List havingTokens = new ArrayList();
 	private final Map joins = new LinkedHashMap();
 	private final List orderByTokens = new ArrayList();
 	private final List groupByTokens = new ArrayList();
 	private final Set<Serializable> querySpaces = new HashSet<Serializable>();
 	private final Set entitiesToFetch = new HashSet();
 
 	private final Map pathAliases = new HashMap();
 	private final Map pathJoins = new HashMap();
 
 	private Queryable[] persisters;
 	private int[] owners;
 	private EntityType[] ownerAssociationTypes;
 	private String[] names;
 	private boolean[] includeInSelect;
 	private int selectLength;
 	private Type[] returnTypes;
 	private Type[] actualReturnTypes;
 	private String[][] scalarColumnNames;
 	private Map tokenReplacements;
 	private int nameCount;
 	private int parameterCount;
 	private boolean distinct;
 	private boolean compiled;
 	private String sqlString;
 	private Class holderClass;
 	private Constructor holderConstructor;
 	private boolean hasScalars;
 	private boolean shallowQuery;
 	private QueryTranslatorImpl superQuery;
 
 	private QueryableCollection collectionPersister;
 	private int collectionOwnerColumn = -1;
 	private String collectionOwnerName;
 	private String fetchName;
 
 	private String[] suffixes;
 
 	private Map enabledFilters;
 
 	/**
 	 * Construct a query translator
 	 *
 	 * @param queryIdentifier A unique identifier for the query of which this
 	 * translation is part; typically this is the original, user-supplied query string.
 	 * @param queryString The "preprocessed" query string; at the very least
 	 * already processed by {@link org.hibernate.hql.internal.QuerySplitter}.
 	 * @param enabledFilters Any enabled filters.
 	 * @param factory The session factory.
 	 */
 	public QueryTranslatorImpl(
 			String queryIdentifier,
 			String queryString,
 			Map enabledFilters,
 			SessionFactoryImplementor factory) {
 		super( factory );
 		this.queryIdentifier = queryIdentifier;
 		this.queryString = queryString;
 		this.enabledFilters = enabledFilters;
 	}
 
 	/**
 	 * Construct a query translator; this form used internally.
 	 *
 	 * @param queryString The query string to process.
 	 * @param enabledFilters Any enabled filters.
 	 * @param factory The session factory.
 	 */
 	public QueryTranslatorImpl(
 			String queryString,
 			Map enabledFilters,
 			SessionFactoryImplementor factory) {
 		this( queryString, queryString, enabledFilters, factory );
 	}
 
 	/**
 	 * Compile a subquery.
 	 *
 	 * @param superquery The containing query of the query to be compiled.
 	 *
 	 * @throws org.hibernate.MappingException Indicates problems resolving
 	 * things referenced in the query.
 	 * @throws org.hibernate.QueryException Generally some form of syntatic
 	 * failure.
 	 */
 	void compile(QueryTranslatorImpl superquery) throws QueryException, MappingException {
 		this.tokenReplacements = superquery.tokenReplacements;
 		this.superQuery = superquery;
 		this.shallowQuery = true;
 		this.enabledFilters = superquery.getEnabledFilters();
 		compile();
 	}
 
 
 	/**
 	 * Compile a "normal" query. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 */
 	public synchronized void compile(
 			Map replacements,
 			boolean scalar) throws QueryException, MappingException {
 		if ( !compiled ) {
 			this.tokenReplacements = replacements;
 			this.shallowQuery = scalar;
 			compile();
 		}
 	}
 
 	/**
 	 * Compile a filter. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 */
 	public synchronized void compile(
 			String collectionRole,
 			Map replacements,
 			boolean scalar) throws QueryException, MappingException {
 
 		if ( !isCompiled() ) {
 			addFromAssociation( "this", collectionRole );
 			compile( replacements, scalar );
 		}
 	}
 
 	/**
 	 * Compile the query (generate the SQL).
 	 *
 	 * @throws org.hibernate.MappingException Indicates problems resolving
 	 * things referenced in the query.
 	 * @throws org.hibernate.QueryException Generally some form of syntatic
 	 * failure.
 	 */
 	private void compile() throws QueryException, MappingException {
 		LOG.trace( "Compiling query" );
 		try {
 			ParserHelper.parse(
 					new PreprocessingParser( tokenReplacements ),
 					queryString,
 					ParserHelper.HQL_SEPARATORS,
 					this
 			);
 			renderSQL();
 		}
 		catch (QueryException qe) {
 			if ( qe.getQueryString() == null ) {
 				throw qe.wrapWithQueryString( queryString );
 			}
 			else {
 				throw qe;
 			}
 		}
 		catch (MappingException me) {
 			throw me;
 		}
 		catch (Exception e) {
 			LOG.debug( "Unexpected query compilation problem", e );
 			e.printStackTrace();
 			throw new QueryException( "Incorrect query syntax", queryString, e );
 		}
 
 		postInstantiate();
 
 		compiled = true;
 
 	}
 
 	@Override
 	public String getSQLString() {
 		return sqlString;
 	}
 
 	public List<String> collectSqlStrings() {
 		return ArrayHelper.toList( new String[] {sqlString} );
 	}
 
 	public String getQueryString() {
 		return queryString;
 	}
 
 	/**
 	 * Persisters for the return values of a <tt>find()</tt> style query.
 	 *
 	 * @return an array of <tt>EntityPersister</tt>s.
 	 */
 	@Override
 	protected Loadable[] getEntityPersisters() {
 		return persisters;
 	}
 
 	/**
 	 * Types of the return values of an <tt>iterate()</tt> style query.
 	 *
 	 * @return an array of <tt>Type</tt>s.
 	 */
 	public Type[] getReturnTypes() {
 		return actualReturnTypes;
 	}
 
 	public String[] getReturnAliases() {
 		// return aliases not supported in classic translator!
 		return NO_RETURN_ALIASES;
 	}
 
 	public String[][] getColumnNames() {
 		return scalarColumnNames;
 	}
 
 	private static void logQuery(String hql, String sql) {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "HQL: %s", hql );
 			LOG.debugf( "SQL: %s", sql );
 		}
 	}
 
 	void setAliasName(String alias, String name) {
 		aliasNames.put( alias, name );
 	}
 
 	public String getAliasName(String alias) {
 		String name = (String) aliasNames.get( alias );
 		if ( name == null ) {
 			if ( superQuery != null ) {
 				name = superQuery.getAliasName( alias );
 			}
 			else {
 				name = alias;
 			}
 		}
 		return name;
 	}
 
 	String unalias(String path) {
 		String alias = StringHelper.root( path );
 		String name = getAliasName( alias );
 		if ( name != null ) {
 			return name + path.substring( alias.length() );
 		}
 		return path;
 	}
 
 	void addEntityToFetch(String name, String oneToOneOwnerName, AssociationType ownerAssociationType) {
 		addEntityToFetch( name );
 		if ( oneToOneOwnerName != null ) {
 			oneToOneOwnerNames.put( name, oneToOneOwnerName );
 		}
 		if ( ownerAssociationType != null ) {
 			uniqueKeyOwnerReferences.put( name, ownerAssociationType );
 		}
 	}
 
 	private void addEntityToFetch(String name) {
 		entitiesToFetch.add( name );
 	}
 
 	private int nextCount() {
 		return ( superQuery == null ) ? nameCount++ : superQuery.nameCount++;
 	}
 
 	String createNameFor(String type) {
 		return StringHelper.generateAlias( type, nextCount() );
 	}
 
 	String createNameForCollection(String role) {
 		return StringHelper.generateAlias( role, nextCount() );
 	}
 
 	private String getType(String name) {
 		String type = (String) typeMap.get( name );
 		if ( type == null && superQuery != null ) {
 			type = superQuery.getType( name );
 		}
 		return type;
 	}
 
 	private String getRole(String name) {
 		String role = (String) collections.get( name );
 		if ( role == null && superQuery != null ) {
 			role = superQuery.getRole( name );
 		}
 		return role;
 	}
 
 	boolean isName(String name) {
 		return aliasNames.containsKey( name ) ||
 				typeMap.containsKey( name ) ||
 				collections.containsKey( name ) || (
 				superQuery != null && superQuery.isName( name )
 		);
 	}
 
 	PropertyMapping getPropertyMapping(String name) throws QueryException {
 		PropertyMapping decorator = getDecoratedPropertyMapping( name );
 		if ( decorator != null ) {
 			return decorator;
 		}
 
 		String type = getType( name );
 		if ( type == null ) {
 			String role = getRole( name );
 			if ( role == null ) {
 				throw new QueryException( "alias not found: " + name );
 			}
 			return getCollectionPersister( role ); //.getElementPropertyMapping();
 		}
 		else {
 			Queryable persister = getEntityPersister( type );
 			if ( persister == null ) {
 				throw new QueryException( "persistent class not found: " + type );
 			}
 			return persister;
 		}
 	}
 
 	private PropertyMapping getDecoratedPropertyMapping(String name) {
 		return (PropertyMapping) decoratedPropertyMappings.get( name );
 	}
 
 	void decoratePropertyMapping(String name, PropertyMapping mapping) {
 		decoratedPropertyMappings.put( name, mapping );
 	}
 
 	private Queryable getEntityPersisterForName(String name) throws QueryException {
 		String type = getType( name );
 		Queryable persister = getEntityPersister( type );
 		if ( persister == null ) {
 			throw new QueryException( "persistent class not found: " + type );
 		}
 		return persister;
 	}
 
 	Queryable getEntityPersisterUsingImports(String className) {
 		final String importedClassName = getFactory().getMetamodel().getImportedClassName( className );
 		if ( importedClassName == null ) {
 			return null;
 		}
 		try {
 			return (Queryable) getFactory().getMetamodel().entityPersister( importedClassName );
 		}
 		catch (MappingException me) {
 			return null;
 		}
 	}
 
 	Queryable getEntityPersister(String entityName) throws QueryException {
 		try {
 			return (Queryable) getFactory().getMetamodel().entityPersister( entityName );
 		}
 		catch (Exception e) {
 			throw new QueryException( "persistent class not found: " + entityName );
 		}
 	}
 
 	QueryableCollection getCollectionPersister(String role) throws QueryException {
 		try {
 			return (QueryableCollection) getFactory().getMetamodel().collectionPersister( role );
 		}
 		catch (ClassCastException cce) {
 			throw new QueryException( "collection role is not queryable: " + role );
 		}
 		catch (Exception e) {
 			throw new QueryException( "collection role not found: " + role );
 		}
 	}
 
 	void addType(String name, String type) {
 		typeMap.put( name, type );
 	}
 
 	void addCollection(String name, String role) {
 		collections.put( name, role );
 	}
 
 	void addFrom(String name, String type, JoinSequence joinSequence)
 			throws QueryException {
 		addType( name, type );
 		addFrom( name, joinSequence );
 	}
 
 	void addFromCollection(String name, String collectionRole, JoinSequence joinSequence)
 			throws QueryException {
 		//register collection role
 		addCollection( name, collectionRole );
 		addJoin( name, joinSequence );
 	}
 
 	void addFrom(String name, JoinSequence joinSequence)
 			throws QueryException {
 		fromTypes.add( name );
 		addJoin( name, joinSequence );
 	}
 
 	void addFromClass(String name, Queryable classPersister)
 			throws QueryException {
 		JoinSequence joinSequence = new JoinSequence( getFactory() )
 				.setRoot( classPersister, name );
 		//crossJoins.add(name);
 		addFrom( name, classPersister.getEntityName(), joinSequence );
 	}
 
 	void addSelectClass(String name) {
 		returnedTypes.add( name );
 	}
 
 	void addSelectScalar(Type type) {
 		scalarTypes.add( type );
 	}
 
 	void appendWhereToken(String token) {
 		whereTokens.add( token );
 	}
 
 	void appendHavingToken(String token) {
 		havingTokens.add( token );
 	}
 
 	void appendOrderByToken(String token) {
 		orderByTokens.add( token );
 	}
 
 	void appendGroupByToken(String token) {
 		groupByTokens.add( token );
 	}
 
 	void appendScalarSelectToken(String token) {
 		scalarSelectTokens.add( token );
 	}
 
 	void appendScalarSelectTokens(String[] tokens) {
 		scalarSelectTokens.add( tokens );
 	}
 
 	void addFromJoinOnly(String name, JoinSequence joinSequence) throws QueryException {
 		addJoin( name, joinSequence.getFromPart() );
 	}
 
 	void addJoin(String name, JoinSequence joinSequence) throws QueryException {
 		if ( !joins.containsKey( name ) ) {
 			joins.put( name, joinSequence );
 		}
 	}
 
 	void addNamedParameter(String name) {
 		if ( superQuery != null ) {
 			superQuery.addNamedParameter( name );
 		}
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
 	public int[] getNamedParameterLocs(String name) throws QueryException {
 		Object o = namedParameters.get( name );
 		if ( o == null ) {
 			throw new QueryException( ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR + name, queryString );
 		}
 		if ( o instanceof Integer ) {
 			return new int[] {(Integer) o};
 		}
 		else {
 			return ArrayHelper.toIntArray( (ArrayList) o );
 		}
 	}
 
 	private void renderSQL() throws QueryException, MappingException {
 
 		final int rtsize;
 		if ( returnedTypes.size() == 0 && scalarTypes.size() == 0 ) {
 			//ie no select clause in HQL
 			returnedTypes = fromTypes;
 			rtsize = returnedTypes.size();
 		}
 		else {
 			rtsize = returnedTypes.size();
 			Iterator iter = entitiesToFetch.iterator();
 			while ( iter.hasNext() ) {
 				returnedTypes.add( iter.next() );
 			}
 		}
 		int size = returnedTypes.size();
 		persisters = new Queryable[size];
 		names = new String[size];
 		owners = new int[size];
 		ownerAssociationTypes = new EntityType[size];
 		suffixes = new String[size];
 		includeInSelect = new boolean[size];
 		for ( int i = 0; i < size; i++ ) {
 			String name = (String) returnedTypes.get( i );
 			//if ( !isName(name) ) throw new QueryException("unknown type: " + name);
 			persisters[i] = getEntityPersisterForName( name );
 			// TODO: cannot use generateSuffixes() - it handles the initial suffix differently.
 			suffixes[i] = ( size == 1 ) ? "" : Integer.toString( i ) + '_';
 			names[i] = name;
 			includeInSelect[i] = !entitiesToFetch.contains( name );
 			if ( includeInSelect[i] ) {
 				selectLength++;
 			}
 			if ( name.equals( collectionOwnerName ) ) {
 				collectionOwnerColumn = i;
 			}
 			String oneToOneOwner = (String) oneToOneOwnerNames.get( name );
 			owners[i] = ( oneToOneOwner == null ) ? -1 : returnedTypes.indexOf( oneToOneOwner );
 			ownerAssociationTypes[i] = (EntityType) uniqueKeyOwnerReferences.get( name );
 		}
 
 		if ( ArrayHelper.isAllNegative( owners ) ) {
 			owners = null;
 		}
 
 		String scalarSelect = renderScalarSelect(); //Must be done here because of side-effect! yuck...
 
 		int scalarSize = scalarTypes.size();
 		hasScalars = scalarTypes.size() != rtsize;
 
 		returnTypes = new Type[scalarSize];
 		for ( int i = 0; i < scalarSize; i++ ) {
 			returnTypes[i] = (Type) scalarTypes.get( i );
 		}
 
 		QuerySelect sql = new QuerySelect( getFactory().getDialect() );
 		sql.setDistinct( distinct );
 
 		if ( !shallowQuery ) {
 			renderIdentifierSelect( sql );
 			renderPropertiesSelect( sql );
 		}
 
 		if ( collectionPersister != null ) {
 			sql.addSelectFragmentString( collectionPersister.selectFragment( fetchName, "__" ) );
 		}
 
 		if ( hasScalars || shallowQuery ) {
 			sql.addSelectFragmentString( scalarSelect );
 		}
 
 		//TODO: for some dialects it would be appropriate to add the renderOrderByPropertiesSelect() to other select strings
 		mergeJoins( sql.getJoinFragment() );
 
 		sql.setWhereTokens( whereTokens.iterator() );
 
 		sql.setGroupByTokens( groupByTokens.iterator() );
 		sql.setHavingTokens( havingTokens.iterator() );
 		sql.setOrderByTokens( orderByTokens.iterator() );
 
 		if ( collectionPersister != null && collectionPersister.hasOrdering() ) {
 			sql.addOrderBy( collectionPersister.getSQLOrderByString( fetchName ) );
 		}
 
 		scalarColumnNames = NameGenerator.generateColumnNames( returnTypes, getFactory() );
 
 		// initialize the Set of queried identifier spaces (ie. tables)
 		Iterator iter = collections.values().iterator();
 		while ( iter.hasNext() ) {
 			CollectionPersister p = getCollectionPersister( (String) iter.next() );
 			addQuerySpaces( p.getCollectionSpaces() );
 		}
 		iter = typeMap.keySet().iterator();
 		while ( iter.hasNext() ) {
 			Queryable p = getEntityPersisterForName( (String) iter.next() );
 			addQuerySpaces( p.getQuerySpaces() );
 		}
 
 		sqlString = sql.toQueryString();
 
 		if ( holderClass != null ) {
 			holderConstructor = ReflectHelper.getConstructor( holderClass, returnTypes );
 		}
 
 		if ( hasScalars ) {
 			actualReturnTypes = returnTypes;
 		}
 		else {
 			actualReturnTypes = new Type[selectLength];
 			int j = 0;
 			for ( int i = 0; i < persisters.length; i++ ) {
 				if ( includeInSelect[i] ) {
 					actualReturnTypes[j++] = getFactory().getTypeResolver()
 							.getTypeFactory()
 							.manyToOne( persisters[i].getEntityName(), shallowQuery );
 				}
 			}
 		}
 
 	}
 
 	private void renderIdentifierSelect(QuerySelect sql) {
 		int size = returnedTypes.size();
 
 		for ( int k = 0; k < size; k++ ) {
 			String name = (String) returnedTypes.get( k );
 			String suffix = size == 1 ? "" : Integer.toString( k ) + '_';
 			sql.addSelectFragmentString( persisters[k].identifierSelectFragment( name, suffix ) );
 		}
 
 	}
 
 	/*private String renderOrderByPropertiesSelect() {
 		StringBuffer buf = new StringBuffer(10);
 
 		//add the columns we are ordering by to the select ID select clause
 		Iterator iter = orderByTokens.iterator();
 		while ( iter.hasNext() ) {
 			String token = (String) iter.next();
 			if ( token.lastIndexOf(".") > 0 ) {
 				//ie. it is of form "foo.bar", not of form "asc" or "desc"
 				buf.append(StringHelper.COMMA_SPACE).append(token);
 			}
 		}
 
 		return buf.toString();
 	}*/
 
 	private void renderPropertiesSelect(QuerySelect sql) {
 		int size = returnedTypes.size();
 		for ( int k = 0; k < size; k++ ) {
 			String suffix = size == 1 ? "" : Integer.toString( k ) + '_';
 			String name = (String) returnedTypes.get( k );
 			sql.addSelectFragmentString( persisters[k].propertySelectFragment( name, suffix, false ) );
 		}
 	}
 
 	/**
 	 * WARNING: side-effecty
 	 */
 	private String renderScalarSelect() {
 
 		boolean isSubselect = superQuery != null;
 
 		StringBuilder buf = new StringBuilder( 20 );
 
 		if ( scalarTypes.size() == 0 ) {
 			//ie. no select clause
 			int size = returnedTypes.size();
 			for ( int k = 0; k < size; k++ ) {
 
 				scalarTypes.add(
 						getFactory().getTypeResolver().getTypeFactory().manyToOne(
 								persisters[k].getEntityName(),
 								shallowQuery
 						)
 				);
 
 				String[] idColumnNames = persisters[k].getIdentifierColumnNames();
 				for ( int i = 0; i < idColumnNames.length; i++ ) {
 					buf.append( returnedTypes.get( k ) ).append( '.' ).append( idColumnNames[i] );
 					if ( !isSubselect ) {
 						buf.append( " as " ).append( NameGenerator.scalarName( k, i ) );
 					}
 					if ( i != idColumnNames.length - 1 || k != size - 1 ) {
 						buf.append( ", " );
 					}
 				}
 
 			}
 
 		}
 		else {
 			//there _was_ a select clause
 			Iterator iter = scalarSelectTokens.iterator();
 			int c = 0;
 			boolean nolast = false; //real hacky...
 			int parenCount = 0; // used to count the nesting of parentheses
 			while ( iter.hasNext() ) {
 				Object next = iter.next();
 				if ( next instanceof String ) {
 					String token = (String) next;
 
 					if ( "(".equals( token ) ) {
 						parenCount++;
 					}
 					else if ( ")".equals( token ) ) {
 						parenCount--;
 					}
 
 					String lc = token.toLowerCase( Locale.ROOT );
 					if ( lc.equals( ", " ) ) {
 						if ( nolast ) {
 							nolast = false;
 						}
 						else {
 							if ( !isSubselect && parenCount == 0 ) {
 								int x = c++;
 								buf.append( " as " ).append( NameGenerator.scalarName( x, 0 ) );
 							}
 						}
 					}
 					buf.append( token );
 					if ( lc.equals( "distinct" ) || lc.equals( "all" ) ) {
 						buf.append( ' ' );
 					}
 				}
 				else {
 					nolast = true;
 					String[] tokens = (String[]) next;
 					for ( int i = 0; i < tokens.length; i++ ) {
 						buf.append( tokens[i] );
 						if ( !isSubselect ) {
 							buf.append( " as " ).append( NameGenerator.scalarName( c, i ) );
 						}
 						if ( i != tokens.length - 1 ) {
 							buf.append( ", " );
 						}
 					}
 					c++;
 				}
 			}
 			if ( !isSubselect && !nolast ) {
 				int x = c++;
 				buf.append( " as " ).append( NameGenerator.scalarName( x, 0 ) );
 			}
 
 		}
 
 		return buf.toString();
 	}
 
 	private void mergeJoins(JoinFragment ojf) throws MappingException, QueryException {
 
 		Iterator iter = joins.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			String name = (String) me.getKey();
 			JoinSequence join = (JoinSequence) me.getValue();
 			join.setSelector(
 					new JoinSequence.Selector() {
 						@Override
 						public boolean includeSubclasses(String alias) {
 							return returnedTypes.contains( alias ) && !isShallowQuery();
 						}
 					}
 			);
 
 			if ( typeMap.containsKey( name ) ) {
 				ojf.addFragment( join.toJoinFragment( enabledFilters, true ) );
 			}
 			else if ( collections.containsKey( name ) ) {
 				ojf.addFragment( join.toJoinFragment( enabledFilters, true ) );
 			}
 		}
 
 	}
 
 	@Override
 	public final Set<Serializable> getQuerySpaces() {
 		return querySpaces;
 	}
 
 	/**
 	 * Is this query called by scroll() or iterate()?
 	 *
 	 * @return true if it is, false if it is called by find() or list()
 	 */
 	boolean isShallowQuery() {
 		return shallowQuery;
 	}
 
 	void addQuerySpaces(Serializable[] spaces) {
 		Collections.addAll( querySpaces, spaces );
 		if ( superQuery != null ) {
 			superQuery.addQuerySpaces( spaces );
 		}
 	}
 
 	void setDistinct(boolean distinct) {
 		this.distinct = distinct;
 	}
 
 	boolean isSubquery() {
 		return superQuery != null;
 	}
 
 	@Override
 	public CollectionPersister[] getCollectionPersisters() {
 		return collectionPersister == null ? null : new CollectionPersister[] {collectionPersister};
 	}
 
 	@Override
 	protected String[] getCollectionSuffixes() {
 		return collectionPersister == null ? null : new String[] {"__"};
 	}
 
 	void setCollectionToFetch(String role, String name, String ownerName, String entityName)
 			throws QueryException {
 		fetchName = name;
 		collectionPersister = getCollectionPersister( role );
 		collectionOwnerName = ownerName;
 		if ( collectionPersister.getElementType().isEntityType() ) {
 			addEntityToFetch( entityName );
 		}
 	}
 
 	@Override
 	protected String[] getSuffixes() {
 		return suffixes;
 	}
 
 	@Override
 	protected String[] getAliases() {
 		return names;
 	}
 
 	/**
 	 * Used for collection filters
 	 */
 	private void addFromAssociation(final String elementName, final String collectionRole)
 			throws QueryException {
 		//q.addCollection(collectionName, collectionRole);
 		QueryableCollection persister = getCollectionPersister( collectionRole );
 		Type collectionElementType = persister.getElementType();
 		if ( !collectionElementType.isEntityType() ) {
 			throw new QueryException( "collection of values in filter: " + elementName );
 		}
 
 		String[] keyColumnNames = persister.getKeyColumnNames();
 		//if (keyColumnNames.length!=1) throw new QueryException("composite-key collection in filter: " + collectionRole);
 
 		String collectionName;
 		JoinSequence join = new JoinSequence( getFactory() );
 		collectionName = persister.isOneToMany() ?
 				elementName :
 				createNameForCollection( collectionRole );
 		join.setRoot( persister, collectionName );
 		if ( !persister.isOneToMany() ) {
 			//many-to-many
 			addCollection( collectionName, collectionRole );
 			try {
 				join.addJoin(
 						(AssociationType) persister.getElementType(),
 						elementName,
 						JoinType.INNER_JOIN,
 						persister.getElementColumnNames( collectionName )
 				);
 			}
 			catch (MappingException me) {
 				throw new QueryException( me );
 			}
 		}
 		join.addCondition( collectionName, keyColumnNames, " = ?" );
 		//if ( persister.hasWhere() ) join.addCondition( persister.getSQLWhereString(collectionName) );
 		EntityType elemType = (EntityType) collectionElementType;
 		addFrom( elementName, elemType.getAssociatedEntityName(), join );
 
 	}
 
 	String getPathAlias(String path) {
 		return (String) pathAliases.get( path );
 	}
 
 	JoinSequence getPathJoin(String path) {
 		return (JoinSequence) pathJoins.get( path );
 	}
 
 	void addPathAliasAndJoin(String path, String alias, JoinSequence joinSequence) {
 		pathAliases.put( path, alias );
 		pathJoins.put( path, joinSequence );
 	}
 
 	@Override
 	public List list(SharedSessionContractImplementor session, QueryParameters queryParameters)
 			throws HibernateException {
 		return list( session, queryParameters, getQuerySpaces(), actualReturnTypes );
 	}
 
 	/**
 	 * Return the query results as an iterator
 	 */
 	@Override
 	public Iterator iterate(QueryParameters queryParameters, EventSource session)
 			throws HibernateException {
 
 		boolean stats = session.getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) {
 			startTime = System.nanoTime();
 		}
 
 		try {
 			final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 			final SqlStatementWrapper wrapper = executeQueryStatement(
 					queryParameters,
 					false,
 					afterLoadActions,
 					session
 			);
 			final ResultSet rs = wrapper.getResultSet();
 			final PreparedStatement st = (PreparedStatement) wrapper.getStatement();
 			HolderInstantiator hi = HolderInstantiator.createClassicHolderInstantiator(
 					holderConstructor,
 					queryParameters.getResultTransformer()
 			);
 			Iterator result = new IteratorImpl(
 					rs,
 					st,
 					session,
 					queryParameters.isReadOnly( session ),
 					returnTypes,
 					getColumnNames(),
 					hi
 			);
 
 			if ( stats ) {
 				final long endTime = System.nanoTime();
 				final long milliseconds = TimeUnit.MILLISECONDS.convert( endTime - startTime, TimeUnit.NANOSECONDS );
 				session.getFactory().getStatistics().queryExecuted(
 						"HQL: " + queryString,
 						0,
 						milliseconds
 				);
 			}
 
 			return result;
 
 		}
 		catch (SQLException sqle) {
 			throw getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 					sqle,
 					"could not execute query using iterate",
 					getSQLString()
 			);
 		}
 
 	}
 
 	@Override
 	public int executeUpdate(QueryParameters queryParameters, SharedSessionContractImplementor session) throws HibernateException {
 		throw new UnsupportedOperationException( "Not supported!  Use the AST translator..." );
 	}
 
 	@Override
 	protected boolean[] includeInResultRow() {
 		boolean[] isResultReturned = includeInSelect;
 		if ( hasScalars ) {
 			isResultReturned = new boolean[returnedTypes.size()];
 			Arrays.fill( isResultReturned, true );
 		}
 		return isResultReturned;
 	}
 
 
 	@Override
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return HolderInstantiator.resolveClassicResultTransformer(
 				holderConstructor,
 				resultTransformer
 		);
 	}
 
 	@Override
 	protected Object getResultColumnOrRow(
 			Object[] row,
 			ResultTransformer transformer,
 			ResultSet rs,
 			SharedSessionContractImplementor session)
 			throws SQLException, HibernateException {
 		Object[] resultRow = getResultRow( row, rs, session );
 		return ( holderClass == null && resultRow.length == 1 ?
 				resultRow[0] :
 				resultRow
 		);
 	}
 
 	@Override
 	protected Object[] getResultRow(Object[] row, ResultSet rs, SharedSessionContractImplementor session)
 			throws SQLException, HibernateException {
 		Object[] resultRow;
 		if ( hasScalars ) {
 			String[][] scalarColumns = getColumnNames();
 			int queryCols = returnTypes.length;
 			resultRow = new Object[queryCols];
 			for ( int i = 0; i < queryCols; i++ ) {
 				resultRow[i] = returnTypes[i].nullSafeGet( rs, scalarColumns[i], session, null );
 			}
 		}
 		else {
 			resultRow = toResultRow( row );
 		}
 		return resultRow;
 	}
 
 	@Override
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		if ( holderClass != null ) {
 			for ( int i = 0; i < results.size(); i++ ) {
 				Object[] row = (Object[]) results.get( i );
 				try {
 					results.set( i, holderConstructor.newInstance( row ) );
 				}
 				catch (Exception e) {
 					throw new QueryException( "could not instantiate: " + holderClass, e );
 				}
 			}
 		}
 		return results;
 	}
 
 	private Object[] toResultRow(Object[] row) {
 		if ( selectLength == row.length ) {
 			return row;
 		}
 		else {
 			Object[] result = new Object[selectLength];
 			int j = 0;
 			for ( int i = 0; i < row.length; i++ ) {
 				if ( includeInSelect[i] ) {
 					result[j++] = row[i];
 				}
 			}
 			return result;
 		}
 	}
 
 	void setHolderClass(Class clazz) {
 		holderClass = clazz;
 	}
 
 	@Override
 	protected LockMode[] getLockModes(LockOptions lockOptions) {
 
 		// unfortunately this stuff can't be cached because
 		// it is per-invocation, not constant for the
 		// QueryTranslator instance
 		HashMap nameLockOptions = new HashMap();
 		if ( lockOptions == null ) {
 			lockOptions = LockOptions.NONE;
 		}
 
 		if ( lockOptions.getAliasLockCount() > 0 ) {
 			Iterator iter = lockOptions.getAliasLockIterator();
 			while ( iter.hasNext() ) {
 				Map.Entry me = (Map.Entry) iter.next();
 				nameLockOptions.put(
 						getAliasName( (String) me.getKey() ),
 						me.getValue()
 				);
 			}
 		}
 		LockMode[] lockModesArray = new LockMode[names.length];
 		for ( int i = 0; i < names.length; i++ ) {
 			LockMode lm = (LockMode) nameLockOptions.get( names[i] );
 			//if ( lm == null ) lm = LockOptions.NONE;
 			if ( lm == null ) {
 				lm = lockOptions.getLockMode();
 			}
 			lockModesArray[i] = lm;
 		}
 		return lockModesArray;
 	}
 
 	@Override
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws QueryException {
 		// can't cache this stuff either (per-invocation)
 		final LockOptions lockOptions = parameters.getLockOptions();
 		final String result;
 		if ( lockOptions == null ||
 				( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sql;
 		}
 		else {
 			LockOptions locks = new LockOptions();
 			locks.setLockMode( lockOptions.getLockMode() );
 			locks.setTimeOut( lockOptions.getTimeOut() );
 			locks.setScope( lockOptions.getScope() );
 			Iterator iter = lockOptions.getAliasLockIterator();
 			while ( iter.hasNext() ) {
 				Map.Entry me = (Map.Entry) iter.next();
 				locks.setAliasSpecificLockMode( getAliasName( (String) me.getKey() ), (LockMode) me.getValue() );
 			}
 			Map keyColumnNames = null;
 			if ( dialect.forUpdateOfColumns() ) {
 				keyColumnNames = new HashMap();
 				for ( int i = 0; i < names.length; i++ ) {
 					keyColumnNames.put( names[i], persisters[i].getIdentifierColumnNames() );
 				}
 			}
 			result = dialect.applyLocksToSql( sql, locks, keyColumnNames );
 		}
 		logQuery( queryString, result );
 		return result;
 	}
 
 	@Override
 	protected boolean upgradeLocks() {
 		return true;
 	}
 
 	@Override
 	protected int[] getCollectionOwners() {
 		return new int[] {collectionOwnerColumn};
 	}
 
 	protected boolean isCompiled() {
 		return compiled;
 	}
 
 	@Override
 	public String toString() {
 		return queryString;
 	}
 
 	@Override
 	protected int[] getOwners() {
 		return owners;
 	}
 
 	@Override
 	protected EntityType[] getOwnerAssociationTypes() {
 		return ownerAssociationTypes;
 	}
 
 	public Class getHolderClass() {
 		return holderClass;
 	}
 
 	@Override
 	public Map getEnabledFilters() {
 		return enabledFilters;
 	}
 
 	@Override
-	public ScrollableResults scroll(
+	public ScrollableResultsImplementor scroll(
 			final QueryParameters queryParameters,
 			final SharedSessionContractImplementor session) throws HibernateException {
 		HolderInstantiator hi = HolderInstantiator.createClassicHolderInstantiator(
 				holderConstructor,
 				queryParameters.getResultTransformer()
 		);
 		return scroll( queryParameters, returnTypes, hi, session );
 	}
 
 	@Override
 	public String getQueryIdentifier() {
 		return queryIdentifier;
 	}
 
 	@Override
 	protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 
 	@Override
 	public void validateScrollability() throws HibernateException {
 		// This is the legacy behaviour for HQL queries...
 		if ( getCollectionPersisters() != null ) {
 			throw new HibernateException( "Cannot scroll queries which initialize collections" );
 		}
 	}
 
 	@Override
 	public boolean containsCollectionFetches() {
 		return false;
 	}
 
 	@Override
 	public boolean isManipulationStatement() {
 		// classic parser does not support bulk manipulation statements
 		return false;
 	}
 
 	@Override
 	public Class getDynamicInstantiationResultType() {
 		return holderClass;
 	}
 
 	@Override
 	public ParameterTranslations getParameterTranslations() {
 		return new ParameterTranslations() {
 			@Override
 			public boolean supportsOrdinalParameterMetadata() {
 				// classic translator does not support collection of ordinal
 				// param metadata
 				return false;
 			}
 
 			@Override
 			public int getOrdinalParameterCount() {
 				return 0; // not known!
 			}
 
 			@Override
 			public int getOrdinalParameterSqlLocation(int ordinalPosition) {
 				return 0; // not known!
 			}
 
 			@Override
 			public Type getOrdinalParameterExpectedType(int ordinalPosition) {
 				return null; // not known!
 			}
 
 			@Override
 			public Set getNamedParameterNames() {
 				return namedParameters.keySet();
 			}
 
 			@Override
 			public int[] getNamedParameterSqlLocations(String name) {
 				return getNamedParameterLocs( name );
 			}
 
 			@Override
 			public Type getNamedParameterExpectedType(String name) {
 				return null; // not known!
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/QueryTranslator.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/QueryTranslator.java
index 9220f832aa..679479ae70 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/QueryTranslator.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/QueryTranslator.java
@@ -1,173 +1,174 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.hql.spi;
 
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.event.spi.EventSource;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Defines the contract of an HQL->SQL translator.
  *
  * @author josh
  */
 public interface QueryTranslator {
 	String ERROR_CANNOT_FETCH_WITH_ITERATE = "fetch may not be used with scroll() or iterate()";
 	String ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR = "Named parameter does not appear in Query: ";
 	String ERROR_CANNOT_DETERMINE_TYPE = "Could not determine type of: ";
 	String ERROR_CANNOT_FORMAT_LITERAL =  "Could not format constant value to SQL literal: ";
 
 	/**
 	 * Compile a "normal" query. This method may be called multiple
 	 * times. Subsequent invocations are no-ops.
 	 *
 	 * @param replacements Defined query substitutions.
 	 * @param shallow      Does this represent a shallow (scalar or entity-id) select?
 	 * @throws QueryException   There was a problem parsing the query string.
 	 * @throws MappingException There was a problem querying defined mappings.
 	 */
 	void compile(Map replacements, boolean shallow) throws QueryException, MappingException;
 
 	/**
 	 * Perform a list operation given the underlying query definition.
 	 *
 	 * @param session         The session owning this query.
 	 * @param queryParameters The query bind parameters.
 	 * @return The query list results.
 	 * @throws HibernateException
 	 */
 	List list(SharedSessionContractImplementor session, QueryParameters queryParameters)
 			throws HibernateException;
 
 	/**
 	 * Perform an iterate operation given the underlying query definition.
 	 *
 	 * @param queryParameters The query bind parameters.
 	 * @param session         The session owning this query.
 	 * @return An iterator over the query results.
 	 * @throws HibernateException
 	 */
 	Iterator iterate(QueryParameters queryParameters, EventSource session)
 			throws HibernateException;
 
 	/**
 	 * Perform a scroll operation given the underlying query definition.
 	 *
 	 * @param queryParameters The query bind parameters.
 	 * @param session         The session owning this query.
 	 * @return The ScrollableResults wrapper around the query results.
 	 * @throws HibernateException
 	 */
-	ScrollableResults scroll(QueryParameters queryParameters, SharedSessionContractImplementor session)
+	ScrollableResultsImplementor scroll(QueryParameters queryParameters, SharedSessionContractImplementor session)
 			throws HibernateException;
 
 	/**
 	 * Perform a bulk update/delete operation given the underlying query definition.
 	 *
 	 * @param queryParameters The query bind parameters.
 	 * @param session         The session owning this query.
 	 * @return The number of entities updated or deleted.
 	 * @throws HibernateException
 	 */
 	int executeUpdate(QueryParameters queryParameters, SharedSessionContractImplementor session)
 			throws HibernateException;
 
 	/**
 	 * Returns the set of query spaces (table names) that the query refers to.
 	 *
 	 * @return A set of query spaces (table names).
 	 */
 	Set<Serializable> getQuerySpaces();
 
 	/**
 	 * Retrieve the query identifier for this translator.  The query identifier is
 	 * used in states collection.
 	 *
 	 * @return the identifier
 	 */
 	String getQueryIdentifier();
 
 	/**
 	 * Returns the SQL string generated by the translator.
 	 *
 	 * @return the SQL string generated by the translator.
 	 */
 	String getSQLString();
 
 	List<String> collectSqlStrings();
 
 	/**
 	 * Returns the HQL string processed by the translator.
 	 *
 	 * @return the HQL string processed by the translator.
 	 */
 	String getQueryString();
 
 	/**
 	 * Returns the filters enabled for this query translator.
 	 *
 	 * @return Filters enabled for this query execution.
 	 */
 	Map getEnabledFilters();
 
 	/**
 	 * Returns an array of Types represented in the query result.
 	 *
 	 * @return Query return types.
 	 */
 	Type[] getReturnTypes();
 	
 	/**
 	 * Returns an array of HQL aliases
 	 */
 	String[] getReturnAliases();
 
 	/**
 	 * Returns the column names in the generated SQL.
 	 *
 	 * @return the column names in the generated SQL.
 	 */
 	String[][] getColumnNames();
 
 	/**
 	 * Return information about any parameters encountered during
 	 * translation.
 	 *
 	 * @return The parameter information.
 	 */
 	ParameterTranslations getParameterTranslations();
 
 	/**
 	 * Validate the scrollability of the translated query.
 	 *
 	 * @throws HibernateException
 	 */
 	void validateScrollability() throws HibernateException;
 
 	/**
 	 * Does the translated query contain collection fetches?
 	 *
 	 * @return true if the query does contain collection fetched;
 	 * false otherwise.
 	 */
 	boolean containsCollectionFetches();
 
 	boolean isManipulationStatement();
 
 	Class getDynamicInstantiationResultType();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractScrollableResults.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractScrollableResults.java
index a8f2ca69d1..6dab2d6ef4 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractScrollableResults.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractScrollableResults.java
@@ -1,278 +1,305 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal;
 
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.sql.Blob;
 import java.sql.Clob;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Locale;
 import java.util.TimeZone;
 
 import org.hibernate.HibernateException;
-import org.hibernate.ScrollableResults;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.loader.Loader;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Base implementation of the ScrollableResults interface.
  *
  * @author Steve Ebersole
  */
-public abstract class AbstractScrollableResults implements ScrollableResults {
+public abstract class AbstractScrollableResults implements ScrollableResultsImplementor {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( AbstractScrollableResults.class );
 
 	private final ResultSet resultSet;
 	private final PreparedStatement ps;
 	private final SharedSessionContractImplementor session;
 	private final Loader loader;
 	private final QueryParameters queryParameters;
 	private final Type[] types;
 	private HolderInstantiator holderInstantiator;
+	private boolean closed;
 
 	protected AbstractScrollableResults(
 			ResultSet rs,
 			PreparedStatement ps,
 			SharedSessionContractImplementor sess,
 			Loader loader,
 			QueryParameters queryParameters,
 			Type[] types,
 			HolderInstantiator holderInstantiator) {
 		this.resultSet = rs;
 		this.ps = ps;
 		this.session = sess;
 		this.loader = loader;
 		this.queryParameters = queryParameters;
 		this.types = types;
 		this.holderInstantiator = holderInstantiator != null && holderInstantiator.isRequired()
 				? holderInstantiator
 				: null;
 	}
 
 	protected abstract Object[] getCurrentRow();
 
 	protected ResultSet getResultSet() {
 		return resultSet;
 	}
 
 	protected PreparedStatement getPs() {
 		return ps;
 	}
 
 	protected SharedSessionContractImplementor getSession() {
 		return session;
 	}
 
 	protected Loader getLoader() {
 		return loader;
 	}
 
 	protected QueryParameters getQueryParameters() {
 		return queryParameters;
 	}
 
 	protected Type[] getTypes() {
 		return types;
 	}
 
 	protected HolderInstantiator getHolderInstantiator() {
 		return holderInstantiator;
 	}
 
 	@Override
 	public final void close() {
+		if ( this.closed ) {
+			// noop if already closed
+			return;
+		}
+
 		// not absolutely necessary, but does help with aggressive release
 		//session.getJDBCContext().getConnectionManager().closeQueryStatement( ps, resultSet );
 		session.getJdbcCoordinator().getResourceRegistry().release( ps );
 		session.getJdbcCoordinator().afterStatementExecution();
 		try {
 			session.getPersistenceContext().getLoadContexts().cleanup( resultSet );
 		}
 		catch (Throwable ignore) {
 			// ignore this error for now
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Exception trying to cleanup load context : {0}", ignore.getMessage() );
 			}
 		}
+
+		this.closed = true;
+	}
+
+	@Override
+	public boolean isClosed() {
+		return this.closed;
 	}
 
 	@Override
 	public final Object[] get() throws HibernateException {
+		if ( closed ) {
+			throw new IllegalStateException( "ScrollableResults is closed" );
+		}
 		return getCurrentRow();
 	}
 
 	@Override
 	public final Object get(int col) throws HibernateException {
+		if ( closed ) {
+			throw new IllegalStateException( "ScrollableResults is closed" );
+		}
 		return getCurrentRow()[col];
 	}
 
 	/**
 	 * Check that the requested type is compatible with the result type, and
 	 * return the column value.  This version makes sure the the classes
 	 * are identical.
 	 *
 	 * @param col the column
 	 * @param returnType a "final" type
 	 */
 	protected final Object getFinal(int col, Type returnType) throws HibernateException {
+		if ( closed ) {
+			throw new IllegalStateException( "ScrollableResults is closed" );
+		}
+
 		if ( holderInstantiator != null ) {
 			throw new HibernateException( "query specifies a holder class" );
 		}
 
 		if ( returnType.getReturnedClass() == types[col].getReturnedClass() ) {
 			return get( col );
 		}
 		else {
 			return throwInvalidColumnTypeException( col, types[col], returnType );
 		}
 	}
 
 	/**
 	 * Check that the requested type is compatible with the result type, and
 	 * return the column value.  This version makes sure the the classes
 	 * are "assignable".
 	 *
 	 * @param col the column
 	 * @param returnType any type
 	 */
 	protected final Object getNonFinal(int col, Type returnType) throws HibernateException {
+		if ( closed ) {
+			throw new IllegalStateException( "ScrollableResults is closed" );
+		}
+
 		if ( holderInstantiator != null ) {
 			throw new HibernateException( "query specifies a holder class" );
 		}
 
 		if ( returnType.getReturnedClass().isAssignableFrom( types[col].getReturnedClass() ) ) {
 			return get( col );
 		}
 		else {
 			return throwInvalidColumnTypeException( col, types[col], returnType );
 		}
 	}
 
 	@Override
 	public final BigDecimal getBigDecimal(int col) throws HibernateException {
 		return (BigDecimal) getFinal( col, StandardBasicTypes.BIG_DECIMAL );
 	}
 
 	@Override
 	public final BigInteger getBigInteger(int col) throws HibernateException {
 		return (BigInteger) getFinal( col, StandardBasicTypes.BIG_INTEGER );
 	}
 
 	@Override
 	public final byte[] getBinary(int col) throws HibernateException {
 		return (byte[]) getFinal( col, StandardBasicTypes.BINARY );
 	}
 
 	@Override
 	public final String getText(int col) throws HibernateException {
 		return (String) getFinal( col, StandardBasicTypes.TEXT );
 	}
 
 	@Override
 	public final Blob getBlob(int col) throws HibernateException {
 		return (Blob) getNonFinal( col, StandardBasicTypes.BLOB );
 	}
 
 	@Override
 	public final Clob getClob(int col) throws HibernateException {
 		return (Clob) getNonFinal( col, StandardBasicTypes.CLOB );
 	}
 
 	@Override
 	public final Boolean getBoolean(int col) throws HibernateException {
 		return (Boolean) getFinal( col, StandardBasicTypes.BOOLEAN );
 	}
 
 	@Override
 	public final Byte getByte(int col) throws HibernateException {
 		return (Byte) getFinal( col, StandardBasicTypes.BYTE );
 	}
 
 	@Override
 	public final Character getCharacter(int col) throws HibernateException {
 		return (Character) getFinal( col, StandardBasicTypes.CHARACTER );
 	}
 
 	@Override
 	public final Date getDate(int col) throws HibernateException {
 		return (Date) getNonFinal( col, StandardBasicTypes.TIMESTAMP );
 	}
 
 	@Override
 	public final Calendar getCalendar(int col) throws HibernateException {
 		return (Calendar) getNonFinal( col, StandardBasicTypes.CALENDAR );
 	}
 
 	@Override
 	public final Double getDouble(int col) throws HibernateException {
 		return (Double) getFinal( col, StandardBasicTypes.DOUBLE );
 	}
 
 	@Override
 	public final Float getFloat(int col) throws HibernateException {
 		return (Float) getFinal( col, StandardBasicTypes.FLOAT );
 	}
 
 	@Override
 	public final Integer getInteger(int col) throws HibernateException {
 		return (Integer) getFinal( col, StandardBasicTypes.INTEGER );
 	}
 
 	@Override
 	public final Long getLong(int col) throws HibernateException {
 		return (Long) getFinal( col, StandardBasicTypes.LONG );
 	}
 
 	@Override
 	public final Short getShort(int col) throws HibernateException {
 		return (Short) getFinal( col, StandardBasicTypes.SHORT );
 	}
 
 	@Override
 	public final String getString(int col) throws HibernateException {
 		return (String) getFinal( col, StandardBasicTypes.STRING );
 	}
 
 	@Override
 	public final Locale getLocale(int col) throws HibernateException {
 		return (Locale) getFinal( col, StandardBasicTypes.LOCALE );
 	}
 
 	@Override
 	public final TimeZone getTimeZone(int col) throws HibernateException {
 		return (TimeZone) getNonFinal( col, StandardBasicTypes.TIMEZONE );
 	}
 
 	@Override
 	public final Type getType(int i) {
 		return types[i];
 	}
 
 	private Object throwInvalidColumnTypeException(
 			int i,
 			Type type,
 			Type returnType) throws HibernateException {
 		throw new HibernateException(
 				"incompatible column types: " +
 						type.getName() +
 						", " +
 						returnType.getName()
 		);
 	}
 
 	protected void afterScrollOperation() {
 		session.afterScrollOperation();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
index eeb8fdad4a..eec853c469 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
@@ -1,34 +1,32 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.resource.jdbc.spi.JdbcSessionOwner;
 import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder.Options;
 import org.hibernate.type.descriptor.WrapperOptions;
 
 /**
  * Functionality common to stateless and stateful sessions
  *
  * @author Gavin King
  */
 public abstract class AbstractSessionImpl
 		extends AbstractSharedSessionContract
 		implements Serializable, SharedSessionContractImplementor, JdbcSessionOwner, SessionImplementor, EventSource,
 		Options, WrapperOptions {
 
-	private static final CoreMessageLogger log = CoreLogging.messageLogger( AbstractSessionImpl.class );
-
 	protected AbstractSessionImpl(SessionFactoryImpl factory, SessionCreationOptions options) {
 		super( factory, options );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSharedSessionContract.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSharedSessionContract.java
index 0f9003ceb6..b301e580fa 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSharedSessionContract.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSharedSessionContract.java
@@ -1,1014 +1,1015 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal;
 
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.sql.SQLException;
 import java.util.List;
 import java.util.UUID;
 import javax.persistence.FlushModeType;
 import javax.persistence.Tuple;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.FlushMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.LockMode;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.Transaction;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.internal.SessionEventListenerManagerImpl;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.engine.jdbc.internal.JdbcCoordinatorImpl;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryConstructorReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExceptionConverter;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionEventListenerManager;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.engine.transaction.internal.TransactionImpl;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.id.uuid.StandardRandomStrategy;
 import org.hibernate.jpa.internal.util.FlushModeTypeHelper;
 import org.hibernate.jpa.spi.TupleBuilderTransformer;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.procedure.internal.ProcedureCallImpl;
 import org.hibernate.query.ParameterMetadata;
 import org.hibernate.query.Query;
 import org.hibernate.query.internal.NativeQueryImpl;
 import org.hibernate.query.internal.QueryImpl;
 import org.hibernate.query.spi.NativeQueryImplementor;
 import org.hibernate.query.spi.QueryImplementor;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.resource.jdbc.spi.JdbcSessionContext;
 import org.hibernate.resource.jdbc.spi.StatementInspector;
 import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl;
 import org.hibernate.resource.transaction.spi.TransactionCoordinator;
 import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
 import org.hibernate.resource.transaction.spi.TransactionStatus;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * Base class for SharedSessionContract/SharedSessionContractImplementor
  * implementations.  Intended for Session and StatelessSession implementations
  * <P/>
  * NOTE: This implementation defines access to a number of instance state values
  * in a manner that is not exactly concurrent-access safe.  However, a Session/EntityManager
  * is never intended to be used concurrently; therefore the condition is not expected
  * and so a more synchronized/concurrency-safe is not defined to be as negligent
  * (performance-wise) as possible.  Some of these methods include:<ul>
  *     <li>{@link #getEventListenerManager()}</li>
  *     <li>{@link #getJdbcConnectionAccess()}</li>
  *     <li>{@link #getJdbcServices()}</li>
  *     <li>{@link #getTransaction()} (and therefore related methods such as {@link #beginTransaction()}, etc)</li>
  * </ul>
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings("WeakerAccess")
 public abstract class AbstractSharedSessionContract implements SharedSessionContractImplementor {
 	private static final EntityManagerMessageLogger log = HEMLogging.messageLogger( SessionImpl.class );
 
 	private transient SessionFactoryImpl factory;
 	private final String tenantIdentifier;
 	private final UUID sessionIdentifier;
 
 	private final boolean isTransactionCoordinatorShared;
 	private final Interceptor interceptor;
 
 	private FlushMode flushMode;
 	private boolean autoJoinTransactions;
 
 	private CacheMode cacheMode;
 
 	private boolean closed;
 
 	// transient & non-final for Serialization purposes - ugh
 	private transient SessionEventListenerManagerImpl sessionEventsManager = new SessionEventListenerManagerImpl();
 	private transient EntityNameResolver entityNameResolver;
 	private transient JdbcConnectionAccess jdbcConnectionAccess;
 	private transient JdbcSessionContext jdbcSessionContext;
 	private transient JdbcCoordinator jdbcCoordinator;
 	private transient TransactionImplementor currentHibernateTransaction;
 	private transient TransactionCoordinator transactionCoordinator;
 	private transient Boolean useStreamForLobBinding;
 	private transient long timestamp;
 
 	protected transient ExceptionConverter exceptionConverter;
 
 	public AbstractSharedSessionContract(SessionFactoryImpl factory, SessionCreationOptions options) {
 		this.factory = factory;
 		this.sessionIdentifier = StandardRandomStrategy.INSTANCE.generateUUID( null );
 		this.timestamp = factory.getCache().getRegionFactory().nextTimestamp();
 
 		this.flushMode = options.getInitialSessionFlushMode();
 
 		this.tenantIdentifier = options.getTenantIdentifier();
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
 
 		this.interceptor = interpret( options.getInterceptor() );
 
 		final StatementInspector statementInspector = interpret( options.getStatementInspector() );
 		this.jdbcSessionContext = new JdbcSessionContextImpl( this, statementInspector );
 
 		this.entityNameResolver = new CoordinatingEntityNameResolver( factory, interceptor );
 
 		if ( options instanceof SharedSessionCreationOptions && ( (SharedSessionCreationOptions) options ).isTransactionCoordinatorShared() ) {
 			if ( options.getConnection() != null ) {
 				throw new SessionException( "Cannot simultaneously share transaction context and specify connection" );
 			}
 
 			this.isTransactionCoordinatorShared = true;
 
 			final SharedSessionCreationOptions sharedOptions = (SharedSessionCreationOptions) options;
 			this.transactionCoordinator = sharedOptions.getTransactionCoordinator();
 			this.jdbcCoordinator = sharedOptions.getJdbcCoordinator();
 
 			// todo : "wrap" the transaction to no-op cmmit/rollback attempts?
 			this.currentHibernateTransaction = sharedOptions.getTransaction();
 
 			if ( sharedOptions.shouldAutoJoinTransactions() ) {
 				log.debug(
 						"Session creation specified 'autoJoinTransactions', which is invalid in conjunction " +
 								"with sharing JDBC connection between sessions; ignoring"
 				);
 				autoJoinTransactions = false;
 			}
 			if ( sharedOptions.getPhysicalConnectionHandlingMode() != this.jdbcCoordinator.getLogicalConnection().getConnectionHandlingMode() ) {
 				log.debug(
 						"Session creation specified 'PhysicalConnectionHandlingMode which is invalid in conjunction " +
 								"with sharing JDBC connection between sessions; ignoring"
 				);
 			}
 
 			addSharedSessionTransactionObserver( transactionCoordinator );
 		}
 		else {
 			this.isTransactionCoordinatorShared = false;
 			this.autoJoinTransactions = options.shouldAutoJoinTransactions();
 
 			this.jdbcCoordinator = new JdbcCoordinatorImpl( options.getConnection(), this );
 			this.transactionCoordinator = factory.getServiceRegistry()
 					.getService( TransactionCoordinatorBuilder.class )
 					.buildTransactionCoordinator( jdbcCoordinator, this );
 		}
 		exceptionConverter = new ExceptionConverterImpl( this );
 	}
 
 	protected void addSharedSessionTransactionObserver(TransactionCoordinator transactionCoordinator) {
 	}
 
 	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return autoJoinTransactions;
 	}
 
 	private Interceptor interpret(Interceptor interceptor) {
 		return interceptor == null ? EmptyInterceptor.INSTANCE : interceptor;
 	}
 
 	private StatementInspector interpret(StatementInspector statementInspector) {
 		if ( statementInspector == null ) {
 			// If there is no StatementInspector specified, map to the call
 			//		to the (deprecated) Interceptor #onPrepareStatement method
 			return (StatementInspector) interceptor::onPrepareStatement;
 		}
 		return statementInspector;
 	}
 
 	@Override
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public Interceptor getInterceptor() {
 		checkTransactionSynchStatus();
 		return interceptor;
 	}
 
 	@Override
 	public JdbcCoordinator getJdbcCoordinator() {
 		return jdbcCoordinator;
 	}
 
 	@Override
 	public TransactionCoordinator getTransactionCoordinator() {
 		return transactionCoordinator;
 	}
 
 	@Override
 	public JdbcSessionContext getJdbcSessionContext() {
 		return this.jdbcSessionContext;
 	}
 
 	public EntityNameResolver getEntityNameResolver() {
 		return entityNameResolver;
 	}
 
 	@Override
 	public SessionEventListenerManager getEventListenerManager() {
 		return sessionEventsManager;
 	}
 
 	@Override
 	public UUID getSessionIdentifier() {
 		return sessionIdentifier;
 	}
 
 	@Override
 	public String getTenantIdentifier() {
 		return tenantIdentifier;
 	}
 
 	@Override
 	public long getTimestamp() {
 		return timestamp;
 	}
 
 	@Override
 	public boolean isOpen() {
 		return !isClosed();
 	}
 
 	@Override
 	public boolean isClosed() {
 		return closed || factory.isClosed();
 	}
 
 	@Override
 	public void close() {
 		if ( closed ) {
 			return;
 		}
 
 		if ( sessionEventsManager != null ) {
 			sessionEventsManager.end();
 		}
 
 		if ( currentHibernateTransaction != null ) {
 			currentHibernateTransaction.invalidate();
 		}
 
 		try {
 			if ( shouldCloseJdbcCoordinatorOnClose( isTransactionCoordinatorShared ) ) {
 				jdbcCoordinator.close();
 			}
 		}
 		finally {
 			setClosed();
 		}
 	}
 
 	protected void setClosed() {
 		closed = true;
 		cleanupOnClose();
 	}
 
 	protected boolean shouldCloseJdbcCoordinatorOnClose(boolean isTransactionCoordinatorShared) {
 		return true;
 	}
 
 	protected void cleanupOnClose() {
 		// nothing to do in base impl, here for SessionImpl hook
 	}
 
 	@Override
 	public void checkOpen(boolean markForRollbackIfClosed) {
 		if ( isClosed() ) {
 			if ( markForRollbackIfClosed ) {
 				markForRollbackOnly();
 			}
 			throw new IllegalStateException( "Session/EntityManager is closed" );
 		}
 	}
 
 	/**
 	 * @deprecated (since 5.2) use {@link #checkOpen()} instead
 	 */
 	@Deprecated
 	protected void errorIfClosed() {
 		checkOpen();
 	}
 
 	@Override
 	public void markForRollbackOnly() {
 		accessTransaction().markRollbackOnly();
 	}
 
 	@Override
 	public boolean isTransactionInProgress() {
 		return !isClosed()
 				&& transactionCoordinator.isJoined()
 				&& transactionCoordinator.getTransactionDriverControl().getStatus() == TransactionStatus.ACTIVE;
 	}
 
 	@Override
 	public Transaction getTransaction() throws HibernateException {
 		if ( getFactory().getSessionFactoryOptions().isJpaBootstrap() ) {
 			// JPA requires that we throw IllegalStateException if this is called
 			// on a JTA EntityManager
 			if ( getTransactionCoordinator().getTransactionCoordinatorBuilder().isJta() ) {
 				if ( !getFactory().getSessionFactoryOptions().isJtaTransactionAccessEnabled() ) {
 					throw new IllegalStateException( "A JTA EntityManager cannot use getTransaction()" );
 				}
 			}
 		}
 		return accessTransaction();
 	}
 
 	@Override
 	public Transaction accessTransaction() {
 		if ( this.currentHibernateTransaction == null || this.currentHibernateTransaction.getStatus() != TransactionStatus.ACTIVE ) {
 			this.currentHibernateTransaction = new TransactionImpl(
 					getTransactionCoordinator(),
 					getExceptionConverter()
 			);
 
 		}
 		if ( !isClosed() ) {
 			getTransactionCoordinator().pulse();
 		}
 		return this.currentHibernateTransaction;
 	}
 
 	@Override
 	public Transaction beginTransaction() {
 		checkOpen();
 
 		Transaction result = getTransaction();
 		result.begin();
 
 		this.timestamp = factory.getCache().getRegionFactory().nextTimestamp();
 
 		return result;
 	}
 
 	protected void checkTransactionSynchStatus() {
 		pulseTransactionCoordinator();
 		delayedAfterCompletion();
 	}
 
 	protected void pulseTransactionCoordinator() {
 		if ( !isClosed() ) {
 			transactionCoordinator.pulse();
 		}
 	}
 
 	protected void delayedAfterCompletion() {
 		if ( transactionCoordinator instanceof JtaTransactionCoordinatorImpl ) {
 			( (JtaTransactionCoordinatorImpl) transactionCoordinator ).getSynchronizationCallbackCoordinator()
 					.processAnyDelayedAfterCompletion();
 		}
 	}
 
 	protected TransactionImplementor getCurrentTransaction() {
 		return currentHibernateTransaction;
 	}
 
 	@Override
 	public boolean isConnected() {
 		checkTransactionSynchStatus();
 		return jdbcCoordinator.getLogicalConnection().isOpen();
 	}
 
 	@Override
 	public JdbcConnectionAccess getJdbcConnectionAccess() {
 		// See class-level JavaDocs for a discussion of the concurrent-access safety of this method
 		if ( jdbcConnectionAccess == null ) {
 			if ( MultiTenancyStrategy.NONE == factory.getSettings().getMultiTenancyStrategy() ) {
 				jdbcConnectionAccess = new NonContextualJdbcConnectionAccess(
 						getEventListenerManager(),
 						factory.getServiceRegistry().getService( ConnectionProvider.class )
 				);
 			}
 			else {
 				jdbcConnectionAccess = new ContextualJdbcConnectionAccess(
 						getTenantIdentifier(),
 						getEventListenerManager(),
 						factory.getServiceRegistry().getService( MultiTenantConnectionProvider.class )
 				);
 			}
 		}
 		return jdbcConnectionAccess;
 	}
 
 	@Override
 	public EntityKey generateEntityKey(Serializable id, EntityPersister persister) {
 		return new EntityKey( id, persister );
 	}
 
 	@Override
 	public boolean useStreamForLobBinding() {
 		if ( useStreamForLobBinding == null ) {
 			useStreamForLobBinding = Environment.useStreamsForBinary()
 					|| getJdbcServices().getJdbcEnvironment().getDialect().useInputStreamToInsertBlob();
 		}
 		return useStreamForLobBinding;
 	}
 
 	@Override
 	public LobCreator getLobCreator() {
 		return Hibernate.getLobCreator( this );
 	}
 
 	@Override
 	public <T> T execute(final LobCreationContext.Callback<T> callback) {
 		return getJdbcCoordinator().coordinateWork(
 				(workExecutor, connection) -> {
 					try {
 						return callback.executeOnConnection( connection );
 					}
 					catch (SQLException e) {
 						throw exceptionConverter.convert(
 								e,
 								"Error creating contextual LOB : " + e.getMessage()
 						);
 					}
 				}
 		);
 	}
 
 	@Override
 	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 		if ( !sqlTypeDescriptor.canBeRemapped() ) {
 			return sqlTypeDescriptor;
 		}
 
 		final Dialect dialect = getJdbcServices().getJdbcEnvironment().getDialect();
 		final SqlTypeDescriptor remapped = dialect.remapSqlTypeDescriptor( sqlTypeDescriptor );
 		return remapped == null ? sqlTypeDescriptor : remapped;
 	}
 
 	@Override
 	public JdbcServices getJdbcServices() {
 		return getFactory().getJdbcServices();
 	}
 
 	@Override
 	public void setFlushMode(FlushMode flushMode) {
 		setHibernateFlushMode( flushMode );
 	}
 
 	@Override
 	public FlushModeType getFlushMode() {
 		return FlushModeTypeHelper.getFlushModeType( this.flushMode );
 	}
 
 	@Override
 	public void setHibernateFlushMode(FlushMode flushMode) {
 		this.flushMode = flushMode;
 	}
 
 	@Override
 	public FlushMode getHibernateFlushMode() {
 		return flushMode;
 	}
 
 	@Override
 	public CacheMode getCacheMode() {
 		return cacheMode;
 	}
 
 	@Override
 	public void setCacheMode(CacheMode cacheMode) {
 		this.cacheMode = cacheMode;
 	}
 
 	protected HQLQueryPlan getQueryPlan(String query, boolean shallow) throws HibernateException {
 		return getFactory().getQueryPlanCache().getHQLQueryPlan( query, shallow, getLoadQueryInfluencers().getEnabledFilters() );
 	}
 
 	protected NativeSQLQueryPlan getNativeQueryPlan(NativeSQLQuerySpecification spec) throws HibernateException {
 		return getFactory().getQueryPlanCache().getNativeSQLQueryPlan( spec );
 	}
 
 	@Override
 	public QueryImplementor getNamedQuery(String name) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		delayedAfterCompletion();
 
 		// look as HQL/JPQL first
 		final NamedQueryDefinition queryDefinition = factory.getNamedQueryRepository().getNamedQueryDefinition( name );
 		if ( queryDefinition != null ) {
 			return createQuery( queryDefinition );
 		}
 
 		// then as a native query
 		final NamedSQLQueryDefinition nativeQueryDefinition = factory.getNamedQueryRepository().getNamedSQLQueryDefinition( name );
 		if ( nativeQueryDefinition != null ) {
 			return createNativeQuery( nativeQueryDefinition );
 		}
 
 		throw exceptionConverter.convert( new IllegalArgumentException( "No query defined for that name [" + name + "]" ) );
 	}
 
 	protected QueryImplementor createQuery(NamedQueryDefinition queryDefinition) {
 		String queryString = queryDefinition.getQueryString();
 		final QueryImpl query = new QueryImpl(
 				this,
 				getQueryPlan( queryString, false ).getParameterMetadata(),
 				queryString
 		);
 		query.setHibernateFlushMode( queryDefinition.getFlushMode() );
 		query.setComment( queryDefinition.getComment() != null ? queryDefinition.getComment() : queryDefinition.getName() );
 		if ( queryDefinition.getLockOptions() != null ) {
 			query.setLockOptions( queryDefinition.getLockOptions() );
 		}
 
 		initQueryFromNamedDefinition( query, queryDefinition );
 //		applyQuerySettingsAndHints( query );
 
 		return query;
 	}
 
 	private NativeQueryImplementor createNativeQuery(NamedSQLQueryDefinition queryDefinition) {
 		final ParameterMetadata parameterMetadata = factory.getQueryPlanCache().getSQLParameterMetadata(
 				queryDefinition.getQueryString()
 		);
 		final NativeQueryImpl query = new NativeQueryImpl(
 				queryDefinition,
 				this,
 				parameterMetadata
 		);
 		query.setComment( queryDefinition.getComment() != null ? queryDefinition.getComment() : queryDefinition.getName() );
 
 		initQueryFromNamedDefinition( query, queryDefinition );
 		applyQuerySettingsAndHints( query );
 
 		return query;
 	}
 
 	protected void initQueryFromNamedDefinition(Query query, NamedQueryDefinition nqd) {
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
 			query.setHibernateFlushMode( nqd.getFlushMode() );
 		}
 	}
 
 	@Override
 	public QueryImplementor createQuery(String queryString) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		delayedAfterCompletion();
 
 		try {
 			final QueryImpl query = new QueryImpl(
 					this,
 					getQueryPlan( queryString, false ).getParameterMetadata(),
 					queryString
 			);
 			query.setComment( queryString );
 			applyQuerySettingsAndHints( query );
 			return query;
 		}
 		catch (RuntimeException e) {
 			throw exceptionConverter.convert( e );
 		}
 	}
 
 	protected void applyQuerySettingsAndHints(Query query) {
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> QueryImplementor<T> createQuery(String queryString, Class<T> resultClass) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		delayedAfterCompletion();
 
 		try {
 			// do the translation
 			final QueryImplementor<T> query = createQuery( queryString );
 			resultClassChecking( resultClass, query );
 			return query;
 		}
 		catch ( RuntimeException e ) {
 			throw exceptionConverter.convert( e );
 		}
 	}
 
 	@SuppressWarnings({"unchecked", "WeakerAccess", "StatementWithEmptyBody"})
 	protected void resultClassChecking(Class resultClass, org.hibernate.Query hqlQuery) {
 		// make sure the query is a select -> HHH-7192
 		final HQLQueryPlan queryPlan = getFactory().getQueryPlanCache().getHQLQueryPlan(
 				hqlQuery.getQueryString(),
 				false,
 				getLoadQueryInfluencers().getEnabledFilters()
 		);
 		if ( queryPlan.getTranslators()[0].isManipulationStatement() ) {
 			throw new IllegalArgumentException( "Update/delete queries cannot be typed" );
 		}
 
 		// do some return type validation checking
 		if ( Object[].class.equals( resultClass ) ) {
 			// no validation needed
 		}
 		else if ( Tuple.class.equals( resultClass ) ) {
 			TupleBuilderTransformer tupleTransformer = new TupleBuilderTransformer( hqlQuery );
 			hqlQuery.setResultTransformer( tupleTransformer  );
 		}
 		else {
 			final Class dynamicInstantiationClass = queryPlan.getDynamicInstantiationResultType();
 			if ( dynamicInstantiationClass != null ) {
 				if ( ! resultClass.isAssignableFrom( dynamicInstantiationClass ) ) {
 					throw new IllegalArgumentException(
 							"Mismatch in requested result type [" + resultClass.getName() +
 									"] and actual result type [" + dynamicInstantiationClass.getName() + "]"
 					);
 				}
 			}
 			else if ( queryPlan.getTranslators()[0].getReturnTypes().length == 1 ) {
 				// if we have only a single return expression, its java type should match with the requested type
 				final Type queryResultType = queryPlan.getTranslators()[0].getReturnTypes()[0];
 				if ( !resultClass.isAssignableFrom( queryResultType.getReturnedClass() ) ) {
 					throw new IllegalArgumentException(
 							"Type specified for TypedQuery [" +
 									resultClass.getName() +
 									"] is incompatible with query return type [" +
 									queryResultType.getReturnedClass() + "]"
 					);
 				}
 			}
 			else {
 				throw new IllegalArgumentException(
 						"Cannot create TypedQuery for query with more than one return using requested result type [" +
 								resultClass.getName() + "]"
 				);
 			}
 		}
 	}
 
 	@Override
 	public QueryImplementor createNamedQuery(String name) {
 		return buildQueryFromName( name, null );
 	}
 
 	protected  <T> QueryImplementor<T> buildQueryFromName(String name, Class<T> resultType) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		delayedAfterCompletion();
 
 		// todo : apply stored setting at the JPA Query level too
 
 		final NamedQueryDefinition namedQueryDefinition = getFactory().getNamedQueryRepository().getNamedQueryDefinition( name );
 		if ( namedQueryDefinition != null ) {
 			return createQuery( namedQueryDefinition, resultType );
 		}
 
 		final NamedSQLQueryDefinition nativeQueryDefinition = getFactory().getNamedQueryRepository().getNamedSQLQueryDefinition( name );
 		if ( nativeQueryDefinition != null ) {
 			return (QueryImplementor<T>) createNativeQuery( nativeQueryDefinition, resultType );
 		}
 
 		throw exceptionConverter.convert( new IllegalArgumentException( "No query defined for that name [" + name + "]" ) );
 	}
 
 	@SuppressWarnings({"WeakerAccess", "unchecked"})
 	protected <T> QueryImplementor<T> createQuery(NamedQueryDefinition namedQueryDefinition, Class<T> resultType) {
 		final QueryImplementor query = createQuery( namedQueryDefinition );
 		if ( resultType != null ) {
 			resultClassChecking( resultType, query );
 		}
 		return query;
 	}
 
 	@SuppressWarnings({"WeakerAccess", "unchecked"})
 	protected <T> NativeQueryImplementor createNativeQuery(NamedSQLQueryDefinition queryDefinition, Class<T> resultType) {
 		if ( resultType != null ) {
 			resultClassChecking( resultType, queryDefinition );
 		}
 
 		final NativeQueryImpl query = new NativeQueryImpl(
 				queryDefinition,
 				this,
 				factory.getQueryPlanCache().getSQLParameterMetadata( queryDefinition.getQueryString() )
 		);
 		query.setHibernateFlushMode( queryDefinition.getFlushMode() );
 		query.setComment( queryDefinition.getComment() != null ? queryDefinition.getComment() : queryDefinition.getName() );
 		if ( queryDefinition.getLockOptions() != null ) {
 			query.setLockOptions( queryDefinition.getLockOptions() );
 		}
 
 		initQueryFromNamedDefinition( query, queryDefinition );
 		applyQuerySettingsAndHints( query );
 
 		return query;
 	}
 
 	@SuppressWarnings({"unchecked", "WeakerAccess"})
 	protected void resultClassChecking(Class resultType, NamedSQLQueryDefinition namedQueryDefinition) {
 		final NativeSQLQueryReturn[] queryReturns;
 		if ( namedQueryDefinition.getQueryReturns() != null ) {
 			queryReturns = namedQueryDefinition.getQueryReturns();
 		}
 		else if ( namedQueryDefinition.getResultSetRef() != null ) {
 			final ResultSetMappingDefinition rsMapping = getFactory().getNamedQueryRepository().getResultSetMappingDefinition( namedQueryDefinition.getResultSetRef() );
 			queryReturns = rsMapping.getQueryReturns();
 		}
 		else {
 			throw new AssertionFailure( "Unsupported named query model. Please report the bug in Hibernate EntityManager");
 		}
 
 		if ( queryReturns.length > 1 ) {
 			throw new IllegalArgumentException( "Cannot create TypedQuery for query with more than one return" );
 		}
 
 		final NativeSQLQueryReturn nativeSQLQueryReturn = queryReturns[0];
 
 		if ( nativeSQLQueryReturn instanceof NativeSQLQueryRootReturn ) {
 			final Class<?> actualReturnedClass;
 			final String entityClassName = ( (NativeSQLQueryRootReturn) nativeSQLQueryReturn ).getReturnEntityName();
 			try {
 				actualReturnedClass = getFactory().getServiceRegistry().getService( ClassLoaderService.class ).classForName( entityClassName );
 			}
 			catch ( ClassLoadingException e ) {
 				throw new AssertionFailure(
 						"Unable to load class [" + entityClassName + "] declared on named native query [" +
 								namedQueryDefinition.getName() + "]"
 				);
 			}
 			if ( !resultType.isAssignableFrom( actualReturnedClass ) ) {
 				throw buildIncompatibleException( resultType, actualReturnedClass );
 			}
 		}
 		else if ( nativeSQLQueryReturn instanceof NativeSQLQueryConstructorReturn ) {
 			final NativeSQLQueryConstructorReturn ctorRtn = (NativeSQLQueryConstructorReturn) nativeSQLQueryReturn;
 			if ( !resultType.isAssignableFrom( ctorRtn.getTargetClass() ) ) {
 				throw buildIncompatibleException( resultType, ctorRtn.getTargetClass() );
 			}
 		}
 		else {
 			log.debugf( "Skiping unhandled NativeSQLQueryReturn type : " + nativeSQLQueryReturn );
 		}
 	}
 
 	private IllegalArgumentException buildIncompatibleException(Class<?> resultClass, Class<?> actualResultClass) {
 		return new IllegalArgumentException(
 				"Type specified for TypedQuery [" + resultClass.getName() +
 						"] is incompatible with query return type [" + actualResultClass + "]"
 		);
 	}
 
 	@Override
 	public <R> QueryImplementor<R> createNamedQuery(String name, Class<R> resultClass) {
 		return buildQueryFromName( name, resultClass );
 	}
 
 	@Override
 	public NativeQueryImplementor createNativeQuery(String sqlString) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		delayedAfterCompletion();
 
 		try {
 			NativeQueryImpl query = new NativeQueryImpl(
 					sqlString,
 					false,
 					this,
 					getFactory().getQueryPlanCache().getSQLParameterMetadata( sqlString )
 			);
 			query.setComment( "dynamic native SQL query" );
 			return query;
 		}
 		catch ( RuntimeException he ) {
 			throw exceptionConverter.convert( he );
 		}
 	}
 
 	@Override
 	public NativeQueryImplementor createNativeQuery(String sqlString, Class resultClass) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		delayedAfterCompletion();
 
 		try {
 			NativeQueryImplementor query = createNativeQuery( sqlString );
 			query.addEntity( "alias1", resultClass.getName(), LockMode.READ );
 			return query;
 		}
 		catch ( RuntimeException he ) {
 			throw exceptionConverter.convert( he );
 		}
 	}
 
 	@Override
 	public NativeQueryImplementor createNativeQuery(String sqlString, String resultSetMapping) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		delayedAfterCompletion();
 
 		try {
 			NativeQueryImplementor query = createNativeQuery( sqlString );
 			query.setResultSetMapping( resultSetMapping );
 			return query;
 		}
 		catch ( RuntimeException he ) {
 			throw exceptionConverter.convert( he );
 		}
 	}
 
 	@Override
 	public NativeQueryImplementor getNamedNativeQuery(String name) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		delayedAfterCompletion();
 
 		final NamedSQLQueryDefinition nativeQueryDefinition = factory.getNamedQueryRepository().getNamedSQLQueryDefinition( name );
 		if ( nativeQueryDefinition != null ) {
 			return createNativeQuery( nativeQueryDefinition );
 		}
 
 		throw exceptionConverter.convert( new IllegalArgumentException( "No query defined for that name [" + name + "]" ) );
 	}
 
 	@Override
 	public NativeQueryImplementor createSQLQuery(String queryString) {
 		return createNativeQuery( queryString );
 	}
 
 	@Override
 	public NativeQueryImplementor getNamedSQLQuery(String name) {
 		return getNamedNativeQuery( name );
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall getNamedProcedureCall(String name) {
 		checkOpen();
 
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
 		checkOpen();
 		final ProcedureCall procedureCall = new ProcedureCallImpl( this, procedureName );
 //		call.setComment( "Dynamic stored procedure call" );
 		return procedureCall;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		checkOpen();
 		final ProcedureCall procedureCall = new ProcedureCallImpl( this, procedureName, resultClasses );
 //		call.setComment( "Dynamic stored procedure call" );
 		return procedureCall;
 	}
 
 	@Override
 	@SuppressWarnings("UnnecessaryLocalVariable")
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		checkOpen();
 		final ProcedureCall procedureCall = new ProcedureCallImpl( this, procedureName, resultSetMappings );
 //		call.setComment( "Dynamic stored procedure call" );
 		return procedureCall;
 	}
 
 	protected abstract Object load(String entityName, Serializable identifier);
 
 	@Override
 	public List list(NativeSQLQuerySpecification spec, QueryParameters queryParameters) {
 		return listCustomQuery( getNativeQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
-	public ScrollableResults scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) {
+	public ScrollableResultsImplementor scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) {
 		return scrollCustomQuery( getNativeQueryPlan( spec ).getCustomQuery(), queryParameters );
 	}
 
 	@Override
 	public ExceptionConverter getExceptionConverter(){
 		return exceptionConverter;
 	}
 
 	@SuppressWarnings("unused")
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		log.trace( "Serializing " + getClass().getSimpleName() + " [" );
 
 		if ( !jdbcCoordinator.isReadyForSerialization() ) {
 			// throw a more specific (helpful) exception message when this happens from Session,
 			//		as opposed to more generic exception from jdbcCoordinator#serialize call later
 			throw new IllegalStateException( "Cannot serialize " + getClass().getSimpleName() + " [" + getSessionIdentifier() + "] while connected" );
 		}
 
 		if ( isTransactionCoordinatorShared ) {
 			throw new IllegalStateException( "Cannot serialize " + getClass().getSimpleName() + " [" + getSessionIdentifier() + "] as it has a shared TransactionCoordinator" );
 		}
 
 		// todo : (5.2) come back and review serialization plan...
 		//		this was done quickly during initial HEM consolidation into CORE and is likely not perfect :)
 		//
 		//		be sure to review state fields in terms of transient modifiers
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Step 1 :: write non-transient state...
 		oos.defaultWriteObject();
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Step 2 :: write transient state...
 		// 		-- see concurrent access discussion
 
 		factory.serialize( oos );
 		oos.writeObject( jdbcSessionContext.getStatementInspector() );
 		jdbcCoordinator.serialize( oos );
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException, SQLException {
 		log.trace( "Deserializing " + getClass().getSimpleName() );
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Step 1 :: read back non-transient state...
 		ois.defaultReadObject();
 		sessionEventsManager = new SessionEventListenerManagerImpl();
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Step 2 :: read back transient state...
 		//		-- see above
 
 		factory = SessionFactoryImpl.deserialize( ois );
 		jdbcSessionContext = new JdbcSessionContextImpl( this, (StatementInspector) ois.readObject() );
 		jdbcCoordinator = JdbcCoordinatorImpl.deserialize( ois, this );
 
 		this.transactionCoordinator = factory.getServiceRegistry()
 				.getService( TransactionCoordinatorBuilder.class )
 				.buildTransactionCoordinator( jdbcCoordinator, this );
 
 		entityNameResolver = new CoordinatingEntityNameResolver( factory, interceptor );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
index 4994383a1a..70c68b7080 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
@@ -1,3077 +1,3078 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal;
 
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
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import javax.persistence.CacheRetrieveMode;
 import javax.persistence.CacheStoreMode;
 import javax.persistence.EntityGraph;
 import javax.persistence.EntityManager;
 import javax.persistence.EntityNotFoundException;
 import javax.persistence.FlushModeType;
 import javax.persistence.LockModeType;
 import javax.persistence.PersistenceException;
 import javax.persistence.PessimisticLockScope;
 import javax.persistence.StoredProcedureQuery;
 import javax.persistence.TransactionRequiredException;
 import javax.persistence.Tuple;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.criteria.CriteriaDelete;
 import javax.persistence.criteria.CriteriaQuery;
 import javax.persistence.criteria.CriteriaUpdate;
 import javax.persistence.criteria.Selection;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.IdentifierLoadAccess;
 import org.hibernate.LobHelper;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.MultiIdentifierLoadAccess;
 import org.hibernate.NaturalIdLoadAccess;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.ReplicationMode;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.SessionEventListener;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionBuilder;
 import org.hibernate.SimpleNaturalIdLoadAccess;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeHelper;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.criterion.NaturalIdentifier;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.NonContextualLobCreator;
 import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
 import org.hibernate.engine.query.spi.FilterQueryPlan;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.ActionQueue;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.engine.spi.Status;
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
 import org.hibernate.graph.spi.EntityGraphImplementor;
 import org.hibernate.internal.CriteriaImpl.CriterionEntry;
 import org.hibernate.internal.log.DeprecationLogger;
 import org.hibernate.jdbc.ReturningWork;
 import org.hibernate.jdbc.Work;
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.jpa.AvailableSettings;
 import org.hibernate.jpa.QueryHints;
 import org.hibernate.jpa.graph.internal.EntityGraphImpl;
 import org.hibernate.jpa.internal.util.CacheModeHelper;
 import org.hibernate.jpa.internal.util.ConfigurationHelper;
 import org.hibernate.jpa.internal.util.FlushModeTypeHelper;
 import org.hibernate.jpa.internal.util.LockModeTypeHelper;
 import org.hibernate.jpa.spi.CriteriaQueryTupleTransformer;
 import org.hibernate.jpa.spi.HibernateEntityManagerImplementor;
 import org.hibernate.loader.criteria.CriteriaLoader;
 import org.hibernate.loader.custom.CustomLoader;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.metamodel.spi.MetamodelImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.MultiLoadOptions;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.procedure.ProcedureCall;
 import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.procedure.UnknownSqlResultSetMappingException;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.query.Query;
 import org.hibernate.query.criteria.internal.compile.CompilableCriteria;
 import org.hibernate.query.criteria.internal.compile.CriteriaCompiler;
 import org.hibernate.query.criteria.internal.expression.CompoundSelectionImpl;
 import org.hibernate.query.internal.CollectionFilterImpl;
 import org.hibernate.query.spi.QueryImplementor;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.resource.transaction.TransactionRequiredForJoinException;
 import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl;
 import org.hibernate.resource.transaction.backend.jta.internal.synchronization.AfterCompletionAction;
 import org.hibernate.resource.transaction.backend.jta.internal.synchronization.ExceptionMapper;
 import org.hibernate.resource.transaction.backend.jta.internal.synchronization.ManagedFlushChecker;
 import org.hibernate.resource.transaction.spi.TransactionCoordinator;
 import org.hibernate.resource.transaction.spi.TransactionStatus;
 import org.hibernate.stat.SessionStatistics;
 import org.hibernate.stat.internal.SessionStatisticsImpl;
 
 import static org.hibernate.cfg.AvailableSettings.JPA_LOCK_SCOPE;
 import static org.hibernate.cfg.AvailableSettings.JPA_LOCK_TIMEOUT;
 import static org.hibernate.cfg.AvailableSettings.JPA_SHARED_CACHE_RETRIEVE_MODE;
 import static org.hibernate.cfg.AvailableSettings.JPA_SHARED_CACHE_STORE_MODE;
 
 /**
  * Concrete implementation of a Session.
  * <p/>
  * Exposes two interfaces:<ul>
  * <li>{@link org.hibernate.Session} to the application</li>
  * <li>{@link org.hibernate.engine.spi.SessionImplementor} to other Hibernate components (SPI)</li>
  * </ul>
  * <p/>
  * This class is not thread-safe.
  *
  * @author Gavin King
  * @author Steve Ebersole
  * @author Brett Meyer
  * @author Chris Cranford
  */
 public final class SessionImpl
 		extends AbstractSessionImpl
 		implements EventSource, SessionImplementor, HibernateEntityManagerImplementor {
 	private static final EntityManagerMessageLogger log = HEMLogging.messageLogger( SessionImpl.class );
 	private static final boolean TRACE_ENABLED = log.isTraceEnabled();
 
 
 	private static final List<String> ENTITY_MANAGER_SPECIFIC_PROPERTIES = new ArrayList<String>();
 
 	static {
 		ENTITY_MANAGER_SPECIFIC_PROPERTIES.add( JPA_LOCK_SCOPE );
 		ENTITY_MANAGER_SPECIFIC_PROPERTIES.add( JPA_LOCK_TIMEOUT );
 		ENTITY_MANAGER_SPECIFIC_PROPERTIES.add( AvailableSettings.FLUSH_MODE );
 		ENTITY_MANAGER_SPECIFIC_PROPERTIES.add( JPA_SHARED_CACHE_RETRIEVE_MODE );
 		ENTITY_MANAGER_SPECIFIC_PROPERTIES.add( JPA_SHARED_CACHE_STORE_MODE );
 		ENTITY_MANAGER_SPECIFIC_PROPERTIES.add( QueryHints.SPEC_HINT_TIMEOUT );
 	}
 
 	private transient SessionOwner sessionOwner;
 
 	private Map<String, Object> properties = new HashMap<>();
 
 	private transient ActionQueue actionQueue;
 	private transient StatefulPersistenceContext persistenceContext;
 
 	private transient LoadQueryInfluencers loadQueryInfluencers;
 
 	// todo : (5.2) HEM always initialized this.  Is that really needed?
 	private LockOptions lockOptions = new LockOptions();
 
 	private transient boolean autoClear;
 	private transient boolean autoClose;
 
 	private transient int dontFlushFromFind;
 
 	private transient ExceptionMapper exceptionMapper;
 	private transient ManagedFlushChecker managedFlushChecker;
 	private transient AfterCompletionAction afterCompletionAction;
 
 	private transient LoadEvent loadEvent; //cached LoadEvent instance
 
 	public SessionImpl(SessionFactoryImpl factory, SessionCreationOptions options) {
 		super( factory, options );
 
 		this.actionQueue = new ActionQueue( this );
 		this.persistenceContext = new StatefulPersistenceContext( this );
 
 		this.sessionOwner = options.getSessionOwner();
 		initializeFromSessionOwner( sessionOwner );
 
 		this.autoClear = options.shouldAutoClear();
 		this.autoClose = options.shouldAutoClose();
 
 		if ( options instanceof SharedSessionCreationOptions && ( (SharedSessionCreationOptions) options ).isTransactionCoordinatorShared() ) {
 			final SharedSessionCreationOptions sharedOptions = (SharedSessionCreationOptions) options;
 			if ( sharedOptions.getTransactionCompletionProcesses() != null ) {
 				actionQueue.setTransactionCompletionProcesses( sharedOptions.getTransactionCompletionProcesses(), true );
 			}
 		}
 
 		loadQueryInfluencers = new LoadQueryInfluencers( factory );
 
 		if ( getFactory().getStatistics().isStatisticsEnabled() ) {
 			getFactory().getStatistics().openSession();
 		}
 
 		// NOTE : pulse() already handles auto-join-ability correctly
 		getTransactionCoordinator().pulse();
 
 		setDefaultProperties();
 		applyProperties();
 
 		if ( TRACE_ENABLED ) {
 			log.tracef( "Opened Session [%s] at timestamp: %s", getSessionIdentifier(), getTimestamp() );
 		}
 	}
 
 	private void setDefaultProperties() {
 		properties.putIfAbsent( AvailableSettings.FLUSH_MODE, getHibernateFlushMode().name() );
 		properties.putIfAbsent( JPA_LOCK_SCOPE, PessimisticLockScope.EXTENDED.name() );
 		properties.putIfAbsent( JPA_LOCK_TIMEOUT, LockOptions.WAIT_FOREVER );
 		properties.putIfAbsent( JPA_SHARED_CACHE_RETRIEVE_MODE, CacheModeHelper.DEFAULT_RETRIEVE_MODE );
 		properties.putIfAbsent( JPA_SHARED_CACHE_STORE_MODE, CacheModeHelper.DEFAULT_STORE_MODE );
 	}
 
 
 	private void applyProperties() {
 		applyEntityManagerSpecificProperties();
 		setHibernateFlushMode( ConfigurationHelper.getFlushMode( properties.get( AvailableSettings.FLUSH_MODE ), FlushMode.AUTO ) );
 		setLockOptions( this.properties, this.lockOptions );
 		getSession().setCacheMode(
 				CacheModeHelper.interpretCacheMode(
 						currentCacheStoreMode(),
 						currentCacheRetrieveMode()
 				)
 		);
 	}
 
 	private void applyEntityManagerSpecificProperties() {
 		for ( String key : ENTITY_MANAGER_SPECIFIC_PROPERTIES ) {
 			if ( getFactory().getProperties().containsKey( key ) ) {
 				this.properties.put( key, getFactory().getProperties().get( key ) );
 			}
 		}
 	}
 
 	protected void applyQuerySettingsAndHints(Query query) {
 		if ( lockOptions.getLockMode() != LockMode.NONE ) {
 			query.setLockMode( getLockMode( lockOptions.getLockMode() ) );
 		}
 		Object queryTimeout;
 		if ( (queryTimeout = getProperties().get( QueryHints.SPEC_HINT_TIMEOUT ) ) != null ) {
 			query.setHint( QueryHints.SPEC_HINT_TIMEOUT, queryTimeout );
 		}
 		Object lockTimeout;
 		if( (lockTimeout = getProperties().get( JPA_LOCK_TIMEOUT ))!=null){
 			query.setHint( JPA_LOCK_TIMEOUT, lockTimeout );
 		}
 	}
 
 	private CacheRetrieveMode currentCacheRetrieveMode() {
 		return determineCacheRetrieveMode( properties );
 	}
 
 	private CacheStoreMode currentCacheStoreMode() {
 		return determineCacheStoreMode( properties );
 	}
 
 
 	private void initializeFromSessionOwner(SessionOwner sessionOwner) {
 		if ( sessionOwner != null ) {
 			if ( sessionOwner.getExceptionMapper() != null ) {
 				exceptionMapper = sessionOwner.getExceptionMapper();
 			}
 			else {
 				exceptionMapper = ExceptionMapperStandardImpl.INSTANCE;
 			}
 			if ( sessionOwner.getAfterCompletionAction() != null ) {
 				afterCompletionAction = sessionOwner.getAfterCompletionAction();
 			}
 			else {
 				afterCompletionAction = STANDARD_AFTER_COMPLETION_ACTION;
 			}
 			if ( sessionOwner.getManagedFlushChecker() != null ) {
 				managedFlushChecker = sessionOwner.getManagedFlushChecker();
 			}
 			else {
 				managedFlushChecker = STANDARD_MANAGED_FLUSH_CHECKER;
 			}
 		}
 		else {
 			exceptionMapper = ExceptionMapperStandardImpl.INSTANCE;
 			afterCompletionAction = STANDARD_AFTER_COMPLETION_ACTION;
 			managedFlushChecker = STANDARD_MANAGED_FLUSH_CHECKER;
 		}
 	}
 
 	@Override
 	public SharedSessionBuilder sessionWithOptions() {
 		return new SharedSessionBuilderImpl( this );
 	}
 
 	@Override
 	public void clear() {
 		checkOpen();
 
 		// Do not call checkTransactionSynchStatus() here -- if a delayed
 		// afterCompletion exists, it can cause an infinite loop.
 		pulseTransactionCoordinator();
 
 		try {
 			internalClear();
 		}
 		catch (RuntimeException e) {
 			throw exceptionConverter.convert( e );
 		}
 	}
 
 	private void internalClear() {
 		persistenceContext.clear();
 		actionQueue.clear();
 
 		final ClearEvent event = new ClearEvent( this );
 		for ( ClearEventListener listener : listeners( EventType.CLEAR ) ) {
 			listener.onClear( event );
 		}
 	}
 
 
 
 	@Override
 	public void close() throws HibernateException {
 		log.tracef( "Closing session [%s]", getSessionIdentifier() );
 
 		// todo : we want this check if usage is JPA, but not native Hibernate usage
 		if ( getSessionFactory().getSessionFactoryOptions().isJpaBootstrap() ) {
 			checkOpen();
 		}
 
 		super.close();
 
 		if ( getFactory().getStatistics().isStatisticsEnabled() ) {
 			getFactory().getStatistics().closeSession();
 		}
 
 // Original hibernate-entitymanager EM#close behavior
 // does any of this need to be integrated?
 //		checkSessionFactoryOpen();
 //		checkOpen();
 //
 //		if ( discardOnClose || !isTransactionInProgress() ) {
 //			//close right now
 //			if ( session != null ) {
 //				session.close();
 //			}
 //		}
 //		// Otherwise, session auto-close will be enabled by shouldAutoCloseSession().
 //		open = false;
 	}
 
 	@Override
 	protected boolean shouldCloseJdbcCoordinatorOnClose(boolean isTransactionCoordinatorShared) {
 		if ( !isTransactionCoordinatorShared ) {
 			return super.shouldCloseJdbcCoordinatorOnClose( isTransactionCoordinatorShared );
 		}
 
 		if ( getActionQueue().hasBeforeTransactionActions() || getActionQueue().hasAfterTransactionActions() ) {
 			log.warn(
 					"On close, shared Session had beforeQuery/afterQuery transaction actions that have not yet been processed"
 			);
 		}
 		return false;
 	}
 
 	@Override
 	public boolean isAutoCloseSessionEnabled() {
 		return autoClose;
 	}
 
 	@Override
 	public boolean isOpen() {
 		checkSessionFactoryOpen();
 		checkTransactionSynchStatus();
 		try {
 			return !isClosed();
 		}
 		catch (HibernateException he) {
 			throw exceptionConverter.convert( he );
 		}
 	}
 
 	protected void checkSessionFactoryOpen() {
 		if ( !getFactory().isOpen() ) {
 			log.debug( "Forcing Session/EntityManager closed as SessionFactory/EntityManagerFactory has been closed" );
 			setClosed();
 		}
 	}
 
 	private boolean isFlushModeNever() {
 		return FlushMode.isManualFlushMode( getHibernateFlushMode() );
 	}
 
 	private void managedFlush() {
 		if ( isClosed() ) {
 			log.trace( "Skipping auto-flush due to session closed" );
 			return;
 		}
 		log.trace( "Automatically flushing session" );
 		flush();
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
 			// JPA technically requires that this be a PersistentUnityTransactionType#JTA to work,
 			// but we do not assert that here...
 			//return isAutoCloseSessionEnabled() && getTransactionCoordinator().getTransactionCoordinatorBuilder().isJta();
 			return isAutoCloseSessionEnabled();
 		}
 	}
 
 	private void managedClose() {
 		log.trace( "Automatically closing session" );
 		close();
 	}
 
 	@Override
 	public Connection connection() throws HibernateException {
 		checkOpen();
 		return getJdbcCoordinator().getLogicalConnection().getPhysicalConnection();
 	}
 
 	@Override
 	public Connection disconnect() throws HibernateException {
 		checkOpen();
 		log.debug( "Disconnecting session" );
 		return getJdbcCoordinator().getLogicalConnection().manualDisconnect();
 	}
 
 	@Override
 	public void reconnect(Connection conn) throws HibernateException {
 		checkOpen();
 		log.debug( "Reconnecting session" );
 		checkTransactionSynchStatus();
 		getJdbcCoordinator().getLogicalConnection().manualReconnect( conn );
 	}
 
 	@Override
 	public void setAutoClear(boolean enabled) {
 		checkOpen();
 		autoClear = enabled;
 	}
 
 	/**
 	 * Check if there is a Hibernate or JTA transaction in progress and,
 	 * if there is not, flush if necessary, make sure the connection has
 	 * been committed (if it is not in autocommit mode) and run the afterQuery
 	 * completion processing
 	 *
 	 * @param success Was the operation a success
 	 */
 	public void afterOperation(boolean success) {
 		if ( !isTransactionInProgress() ) {
 			getJdbcCoordinator().afterTransaction();
 		}
 	}
 
 	@Override
 	public void addEventListeners(SessionEventListener... listeners) {
 		getEventListenerManager().addListener( listeners );
 	}
 
 	/**
 	 * clear all the internal collections, just
 	 * to help the garbage collector, does not
 	 * clear anything that is needed during the
 	 * afterTransactionCompletion() phase
 	 */
 	@Override
 	protected void cleanupOnClose() {
 		persistenceContext.clear();
 	}
 
 	@Override
 	public LockMode getCurrentLockMode(Object object) throws HibernateException {
 		checkOpen();
 		checkTransactionSynchStatus();
 		if ( object == null ) {
 			throw new NullPointerException( "null object passed to getCurrentLockMode()" );
 		}
 		if ( object instanceof HibernateProxy ) {
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation( this );
 			if ( object == null ) {
 				return LockMode.NONE;
 			}
 		}
 		EntityEntry e = persistenceContext.getEntry( object );
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
 
 	@Override
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		checkOpen();
 		// todo : should this get moved to PersistentContext?
 		// logically, is PersistentContext the "thing" to which an interceptor gets attached?
 		final Object result = persistenceContext.getEntity( key );
 		if ( result == null ) {
 			final Object newObject = getInterceptor().getEntity( key.getEntityName(), key.getIdentifier() );
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
 			throw new IllegalStateException( "There are delayed insert actions beforeQuery operation as cascade level 0." );
 		}
 	}
 
 	private void checkNoUnresolvedActionsAfterOperation() {
 		if ( persistenceContext.getCascadeLevel() == 0 ) {
 			actionQueue.checkNoUnresolvedActionsAfterOperation();
 		}
 		delayedAfterCompletion();
 	}
 
 	@Override
 	protected void delayedAfterCompletion() {
 		if ( getTransactionCoordinator() instanceof JtaTransactionCoordinatorImpl ) {
 			( (JtaTransactionCoordinatorImpl) getTransactionCoordinator() ).getSynchronizationCallbackCoordinator()
 					.processAnyDelayedAfterCompletion();
 		}
 	}
 
 	// saveOrUpdate() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void saveOrUpdate(Object object) throws HibernateException {
 		saveOrUpdate( null, object );
 	}
 
 	@Override
 	public void saveOrUpdate(String entityName, Object obj) throws HibernateException {
 		fireSaveOrUpdate( new SaveOrUpdateEvent( entityName, obj, this ) );
 	}
 
 	private void fireSaveOrUpdate(SaveOrUpdateEvent event) {
 		checkOpen();
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
 		return getFactory().getServiceRegistry().getService( EventListenerRegistry.class ).getEventListenerGroup( type );
 	}
 
 
 	// save() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Serializable save(Object obj) throws HibernateException {
 		return save( null, obj );
 	}
 
 	@Override
 	public Serializable save(String entityName, Object object) throws HibernateException {
 		return fireSave( new SaveOrUpdateEvent( entityName, object, this ) );
 	}
 
 	private Serializable fireSave(SaveOrUpdateEvent event) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.SAVE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 		return event.getResultId();
 	}
 
 
 	// update() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void update(Object obj) throws HibernateException {
 		update( null, obj );
 	}
 
 	@Override
 	public void update(String entityName, Object object) throws HibernateException {
 		fireUpdate( new SaveOrUpdateEvent( entityName, object, this ) );
 	}
 
 	private void fireUpdate(SaveOrUpdateEvent event) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( SaveOrUpdateEventListener listener : listeners( EventType.UPDATE ) ) {
 			listener.onSaveOrUpdate( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 	}
 
 
 	// lock() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException {
 		fireLock( new LockEvent( entityName, object, lockMode, this ) );
 	}
 
 	@Override
 	public LockRequest buildLockRequest(LockOptions lockOptions) {
 		return new LockRequestImpl( lockOptions );
 	}
 
 	@Override
 	public void lock(Object object, LockMode lockMode) throws HibernateException {
 		fireLock( new LockEvent( object, lockMode, this ) );
 	}
 
 	private void fireLock(String entityName, Object object, LockOptions options) {
 		fireLock( new LockEvent( entityName, object, options, this ) );
 	}
 
 	private void fireLock(Object object, LockOptions options) {
 		fireLock( new LockEvent( object, options, this ) );
 	}
 
 	private void fireLock(LockEvent event) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		for ( LockEventListener listener : listeners( EventType.LOCK ) ) {
 			listener.onLock( event );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// persist() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void persist(String entityName, Object object) throws HibernateException {
 		firePersist( new PersistEvent( entityName, object, this ) );
 	}
 
 	@Override
 	public void persist(Object object) throws HibernateException {
 		persist( null, object );
 	}
 
 	@Override
 	public void persist(String entityName, Object object, Map copiedAlready) throws HibernateException {
 		firePersist( copiedAlready, new PersistEvent( entityName, object, this ) );
 	}
 
 	private void firePersist(PersistEvent event) {
 		checkOpen();
 		try {
 			checkTransactionSynchStatus();
 			checkNoUnresolvedActionsBeforeOperation();
 
 			for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
 				listener.onPersist( event );
 			}
 		}
 		catch (MappingException e) {
 			throw exceptionConverter.convert( new IllegalArgumentException( e.getMessage() ) );
 		}
 		catch (RuntimeException e) {
 			throw exceptionConverter.convert( e );
 		}
 		finally {
 			try {
 				checkNoUnresolvedActionsAfterOperation();
 			}
 			catch (RuntimeException e) {
 				throw exceptionConverter.convert( e );
 			}
 		}
 	}
 
 	private void firePersist(Map copiedAlready, PersistEvent event) {
 		checkOpen();
 		checkTransactionSynchStatus();
 
 		try {
 			for ( PersistEventListener listener : listeners( EventType.PERSIST ) ) {
 				listener.onPersist( event, copiedAlready );
 			}
 		}
 		catch ( MappingException e ) {
 			throw exceptionConverter.convert( new IllegalArgumentException( e.getMessage() ) ) ;
 		}
 		catch ( RuntimeException e ) {
 			throw exceptionConverter.convert( e );
 		}
 		finally {
 			delayedAfterCompletion();
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
 
 	@Override
 	public void persistOnFlush(String entityName, Object object, Map copiedAlready)
 			throws HibernateException {
 		firePersistOnFlush( copiedAlready, new PersistEvent( entityName, object, this ) );
 	}
 
 	private void firePersistOnFlush(Map copiedAlready, PersistEvent event) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
 			listener.onPersist( event, copiedAlready );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void firePersistOnFlush(PersistEvent event) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		checkNoUnresolvedActionsBeforeOperation();
 		for ( PersistEventListener listener : listeners( EventType.PERSIST_ONFLUSH ) ) {
 			listener.onPersist( event );
 		}
 		checkNoUnresolvedActionsAfterOperation();
 	}
 
 
 	// merge() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Object merge(String entityName, Object object) throws HibernateException {
 		return fireMerge( new MergeEvent( entityName, object, this ) );
 	}
 
 	@Override
 	public Object merge(Object object) throws HibernateException {
 		return merge( null, object );
 	}
 
 	@Override
 	public void merge(String entityName, Object object, Map copiedAlready) throws HibernateException {
 		fireMerge( copiedAlready, new MergeEvent( entityName, object, this ) );
 	}
 
 	private Object fireMerge(MergeEvent event) {
 		checkOpen();
 		try {
 			checkTransactionSynchStatus();
 			checkNoUnresolvedActionsBeforeOperation();
 			for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
 				listener.onMerge( event );
 			}
 			checkNoUnresolvedActionsAfterOperation();
 		}
 		catch ( ObjectDeletedException sse ) {
 			throw exceptionConverter.convert( new IllegalArgumentException( sse ) );
 		}
 		catch ( MappingException e ) {
 			throw exceptionConverter.convert( new IllegalArgumentException( e.getMessage(), e ) );
 		}
 		catch ( RuntimeException e ) {
 			//including HibernateException
 			throw exceptionConverter.convert( e );
 		}
 
 		return event.getResult();
 	}
 
 	private void fireMerge(Map copiedAlready, MergeEvent event) {
 		checkOpen();
 
 		try {
 			checkTransactionSynchStatus();
 			for ( MergeEventListener listener : listeners( EventType.MERGE ) ) {
 				listener.onMerge( event, copiedAlready );
 			}
 		}
 		catch ( ObjectDeletedException sse ) {
 			throw exceptionConverter.convert( new IllegalArgumentException( sse ) );
 		}
 		catch ( MappingException e ) {
 			throw exceptionConverter.convert( new IllegalArgumentException( e.getMessage(), e ) );
 		}
 		catch ( RuntimeException e ) {
 			//including HibernateException
 			throw exceptionConverter.convert( e );
 		}
 		finally {
 			delayedAfterCompletion();
 		}
 	}
 
 
 	// delete() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void delete(Object object) throws HibernateException {
 		fireDelete( new DeleteEvent( object, this ) );
 	}
 
 	@Override
 	public void delete(String entityName, Object object) throws HibernateException {
 		fireDelete( new DeleteEvent( entityName, object, this ) );
 	}
 
 	@Override
 	public void delete(String entityName, Object object, boolean isCascadeDeleteEnabled, Set transientEntities)
 			throws HibernateException {
 		if ( TRACE_ENABLED && persistenceContext.isRemovingOrphanBeforeUpates() ) {
 			logRemoveOrphanBeforeUpdates( "beforeQuery continuing", entityName, object );
 		}
 		fireDelete(
 				new DeleteEvent(
 						entityName,
 						object,
 						isCascadeDeleteEnabled,
 						persistenceContext.isRemovingOrphanBeforeUpates(),
 						this
 				),
 				transientEntities
 		);
 		if ( TRACE_ENABLED && persistenceContext.isRemovingOrphanBeforeUpates() ) {
 			logRemoveOrphanBeforeUpdates( "afterQuery continuing", entityName, object );
 		}
 	}
 
 	@Override
 	public void removeOrphanBeforeUpdates(String entityName, Object child) {
 		// TODO: The removeOrphan concept is a temporary "hack" for HHH-6484.  This should be removed once action/task
 		// ordering is improved.
 		if ( TRACE_ENABLED ) {
 			logRemoveOrphanBeforeUpdates( "begin", entityName, child );
 		}
 		persistenceContext.beginRemoveOrphanBeforeUpdates();
 		try {
 			fireDelete( new DeleteEvent( entityName, child, false, true, this ) );
 		}
 		finally {
 			persistenceContext.endRemoveOrphanBeforeUpdates();
 			if ( TRACE_ENABLED ) {
 				logRemoveOrphanBeforeUpdates( "end", entityName, child );
 			}
 		}
 	}
 
 	private void logRemoveOrphanBeforeUpdates(String timing, String entityName, Object entity) {
 		final EntityEntry entityEntry = persistenceContext.getEntry( entity );
 		log.tracef(
 				"%s remove orphan beforeQuery updates: [%s]",
 				timing,
 				entityEntry == null ? entityName : MessageHelper.infoString( entityName, entityEntry.getId() )
 		);
 	}
 
 	private void fireDelete(DeleteEvent event) {
 		checkOpen();
 		try{
 		checkTransactionSynchStatus();
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event );
 		}
 		}
 		catch ( ObjectDeletedException sse ) {
 			throw exceptionConverter.convert( new IllegalArgumentException( sse ) );
 		}
 		catch ( MappingException e ) {
 			throw exceptionConverter.convert( new IllegalArgumentException( e.getMessage(), e ) );
 		}
 		catch ( RuntimeException e ) {
 			//including HibernateException
 			throw exceptionConverter.convert( e );
 		}
 		finally {
 			delayedAfterCompletion();
 		}
 	}
 
 	private void fireDelete(DeleteEvent event, Set transientEntities) {
 		checkOpen();
 		try{
 		checkTransactionSynchStatus();
 		for ( DeleteEventListener listener : listeners( EventType.DELETE ) ) {
 			listener.onDelete( event, transientEntities );
 		}
 		}
 		catch ( ObjectDeletedException sse ) {
 			throw exceptionConverter.convert( new IllegalArgumentException( sse ) );
 		}
 		catch ( MappingException e ) {
 			throw exceptionConverter.convert( new IllegalArgumentException( e.getMessage(), e ) );
 		}
 		catch ( RuntimeException e ) {
 			//including HibernateException
 			throw exceptionConverter.convert( e );
 		}
 		finally {
 			delayedAfterCompletion();
 		}
 	}
 
 
 	// load()/get() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void load(Object object, Serializable id) throws HibernateException {
 		LoadEvent event = loadEvent;
 		loadEvent = null;
 		if ( event == null ) {
 			event = new LoadEvent( id, object, this );
 		}
 		else {
 			event.setEntityClassName( null );
 			event.setEntityId( id );
 			event.setInstanceToLoad( object );
 			event.setLockMode( LoadEvent.DEFAULT_LOCK_MODE );
 			event.setLockScope( LoadEvent.DEFAULT_LOCK_OPTIONS.getScope() );
 			event.setLockTimeout( LoadEvent.DEFAULT_LOCK_OPTIONS.getTimeOut() );
 		}
 
 		fireLoad( event, LoadEventListener.RELOAD );
 
 		if ( loadEvent == null ) {
 			event.setEntityClassName( null );
 			event.setEntityId( null );
 			event.setInstanceToLoad( null );
 			event.setResult( null );
 			loadEvent = event;
 		}
 	}
 
 	@Override
 	public <T> T load(Class<T> entityClass, Serializable id) throws HibernateException {
 		return this.byId( entityClass ).getReference( id );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id) throws HibernateException {
 		return this.byId( entityName ).getReference( id );
 	}
 
 	@Override
 	public <T> T get(Class<T> entityClass, Serializable id) throws HibernateException {
 		return this.byId( entityClass ).load( id );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id) throws HibernateException {
 		return this.byId( entityName ).load( id );
 	}
 
 	/**
 	 * Load the data for the object with the specified id into a newly created object.
 	 * This is only called when lazily initializing a proxy.
 	 * Do NOT return a proxy.
 	 */
 	@Override
 	public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
 		if ( log.isDebugEnabled() ) {
 			EntityPersister persister = getFactory().getMetamodel().entityPersister( entityName );
 			log.debugf( "Initializing proxy: %s", MessageHelper.infoString( persister, id, getFactory() ) );
 		}
 		LoadEvent event = loadEvent;
 		loadEvent = null;
 		event = recycleEventInstance( event, id, entityName );
 		fireLoad( event, LoadEventListener.IMMEDIATE_LOAD );
 		Object result = event.getResult();
 		if ( loadEvent == null ) {
 			event.setEntityClassName( null );
 			event.setEntityId( null );
 			event.setInstanceToLoad( null );
 			event.setResult( null );
 			loadEvent = event;
 		}
 		return result;
 	}
 
 	@Override
 	public final Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable)
 			throws HibernateException {
 		// todo : remove
 		LoadEventListener.LoadType type = nullable
 				? LoadEventListener.INTERNAL_LOAD_NULLABLE
 				: eager
 				? LoadEventListener.INTERNAL_LOAD_EAGER
 				: LoadEventListener.INTERNAL_LOAD_LAZY;
 
 		LoadEvent event = loadEvent;
 		loadEvent = null;
 		event = recycleEventInstance( event, id, entityName );
 		fireLoad( event, type );
 		Object result = event.getResult();
 		if ( !nullable ) {
 			UnresolvableObjectException.throwIfNull( result, id, entityName );
 		}
 		if ( loadEvent == null ) {
 			event.setEntityClassName( null );
 			event.setEntityId( null );
 			event.setInstanceToLoad( null );
 			event.setResult( null );
 			loadEvent = event;
 		}
 		return result;
 	}
 
 	/**
 	 * Helper to avoid creating many new instances of LoadEvent: it's an allocation hot spot.
 	 */
 	private LoadEvent recycleEventInstance(final LoadEvent event, final Serializable id, final String entityName) {
 		if ( event == null ) {
 			return new LoadEvent( id, entityName, true, this );
 		}
 		else {
 			event.setEntityClassName( entityName );
 			event.setEntityId( id );
 			event.setInstanceToLoad( null );
 			event.setLockMode( LoadEvent.DEFAULT_LOCK_MODE );
 			event.setLockScope( LoadEvent.DEFAULT_LOCK_OPTIONS.getScope() );
 			event.setLockTimeout( LoadEvent.DEFAULT_LOCK_OPTIONS.getTimeOut() );
 			return event;
 		}
 	}
 
 	@Override
 	public <T> T load(Class<T> entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityClass ).with( new LockOptions( lockMode ) ).getReference( id );
 	}
 
 	@Override
 	public <T> T load(Class<T> entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityClass ).with( lockOptions ).getReference( id );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityName ).with( new LockOptions( lockMode ) ).getReference( id );
 	}
 
 	@Override
 	public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityName ).with( lockOptions ).getReference( id );
 	}
 
 	@Override
 	public <T> T get(Class<T> entityClass, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityClass ).with( new LockOptions( lockMode ) ).load( id );
 	}
 
 	@Override
 	public <T> T get(Class<T> entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityClass ).with( lockOptions ).load( id );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
 		return this.byId( entityName ).with( new LockOptions( lockMode ) ).load( id );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
 		return this.byId( entityName ).with( lockOptions ).load( id );
 	}
 
 	@Override
 	public IdentifierLoadAccessImpl byId(String entityName) {
 		return new IdentifierLoadAccessImpl( entityName );
 	}
 
 	@Override
 	public <T> IdentifierLoadAccessImpl<T> byId(Class<T> entityClass) {
 		return new IdentifierLoadAccessImpl<T>( entityClass );
 	}
 
 	@Override
 	public <T> MultiIdentifierLoadAccess<T> byMultipleIds(Class<T> entityClass) {
 		return new MultiIdentifierLoadAccessImpl<T>( locateEntityPersister( entityClass ) );
 	}
 
 	@Override
 	public MultiIdentifierLoadAccess byMultipleIds(String entityName) {
 		return new MultiIdentifierLoadAccessImpl( locateEntityPersister( entityName ) );
 	}
 
 	@Override
 	public NaturalIdLoadAccess byNaturalId(String entityName) {
 		return new NaturalIdLoadAccessImpl( entityName );
 	}
 
 	@Override
 	public <T> NaturalIdLoadAccess<T> byNaturalId(Class<T> entityClass) {
 		return new NaturalIdLoadAccessImpl<T>( entityClass );
 	}
 
 	@Override
 	public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName) {
 		return new SimpleNaturalIdLoadAccessImpl( entityName );
 	}
 
 	@Override
 	public <T> SimpleNaturalIdLoadAccess<T> bySimpleNaturalId(Class<T> entityClass) {
 		return new SimpleNaturalIdLoadAccessImpl<T>( entityClass );
 	}
 
 	private void fireLoad(LoadEvent event, LoadType loadType) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		for ( LoadEventListener listener : listeners( EventType.LOAD ) ) {
 			listener.onLoad( event, loadType );
 		}
 		delayedAfterCompletion();
 	}
 
 	private void fireResolveNaturalId(ResolveNaturalIdEvent event) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		for ( ResolveNaturalIdEventListener listener : listeners( EventType.RESOLVE_NATURAL_ID ) ) {
 			listener.onResolveNaturalId( event );
 		}
 		delayedAfterCompletion();
 	}
 
 
 	// refresh() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void refresh(Object object) throws HibernateException {
 		refresh( null, object );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object) throws HibernateException {
 		fireRefresh( new RefreshEvent( entityName, object, this ) );
 	}
 
 	@Override
 	public void refresh(Object object, LockMode lockMode) throws HibernateException {
 		fireRefresh( new RefreshEvent( object, lockMode, this ) );
 	}
 
 	@Override
 	public void refresh(Object object, LockOptions lockOptions) throws HibernateException {
 		refresh( null, object, lockOptions );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object, LockOptions lockOptions) throws HibernateException {
 		fireRefresh( new RefreshEvent( entityName, object, lockOptions, this ) );
 	}
 
 	@Override
 	public void refresh(String entityName, Object object, Map refreshedAlready) throws HibernateException {
 		fireRefresh( refreshedAlready, new RefreshEvent( entityName, object, this ) );
 	}
 
 	private void fireRefresh(RefreshEvent event) {
 		try {
 			if ( getSessionFactory().getSessionFactoryOptions().isJpaBootstrap() ) {
 				if ( !contains( event.getObject() ) ) {
 					throw new IllegalArgumentException( "Entity not managed" );
 				}
 			}
 			checkOpen();
 			checkTransactionSynchStatus();
 			for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
 				listener.onRefresh( event );
 			}
 		}
 		catch (RuntimeException e) {
 			if ( !getSessionFactory().getSessionFactoryOptions().isJpaBootstrap() ) {
 				if ( e instanceof HibernateException ) {
 					throw e;
 				}
 			}
 			//including HibernateException
 			throw exceptionConverter.convert( e );
 		}
 		finally {
 			delayedAfterCompletion();
 		}
 	}
 
 	private void fireRefresh(Map refreshedAlready, RefreshEvent event) {
 		try {
 			checkOpen();
 			checkTransactionSynchStatus();
 			for ( RefreshEventListener listener : listeners( EventType.REFRESH ) ) {
 				listener.onRefresh( event, refreshedAlready );
 			}
 			delayedAfterCompletion();
 		}
 		catch (RuntimeException e) {
 			throw exceptionConverter.convert( e );
 		}
 		finally {
 			delayedAfterCompletion();
 		}
 	}
 
 
 	// replicate() operations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void replicate(Object obj, ReplicationMode replicationMode) throws HibernateException {
 		fireReplicate( new ReplicateEvent( obj, replicationMode, this ) );
 	}
 
 	@Override
 	public void replicate(String entityName, Object obj, ReplicationMode replicationMode)
 			throws HibernateException {
 		fireReplicate( new ReplicateEvent( entityName, obj, replicationMode, this ) );
 	}
 
 	private void fireReplicate(ReplicateEvent event) {
 		checkOpen();
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
 	@Override
 	public void evict(Object object) throws HibernateException {
 		fireEvict( new EvictEvent( object, this ) );
 	}
 
 	private void fireEvict(EvictEvent event) {
 		checkOpen();
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
 		checkOpen();
 		if ( !isTransactionInProgress() ) {
 			// do not auto-flush while outside a transaction
 			return false;
 		}
 		AutoFlushEvent event = new AutoFlushEvent( querySpaces, this );
 		listeners( EventType.AUTO_FLUSH );
 		for ( AutoFlushEventListener listener : listeners( EventType.AUTO_FLUSH ) ) {
 			listener.onAutoFlush( event );
 		}
 		return event.isFlushRequired();
 	}
 
 	@Override
 	public boolean isDirty() throws HibernateException {
 		checkOpen();
 		checkTransactionSynchStatus();
 		log.debug( "Checking session dirtiness" );
 		if ( actionQueue.areInsertionsOrDeletionsQueued() ) {
 			log.debug( "Session dirty (scheduled updates and insertions)" );
 			return true;
 		}
 		DirtyCheckEvent event = new DirtyCheckEvent( this );
 		for ( DirtyCheckEventListener listener : listeners( EventType.DIRTY_CHECK ) ) {
 			listener.onDirtyCheck( event );
 		}
 		delayedAfterCompletion();
 		return event.isDirty();
 	}
 
 	@Override
 	public void flush() throws HibernateException {
 		checkOpen();
 		checkTransactionNeeded();
 		checkTransactionSynchStatus();
 
 
 		try {
 			if ( persistenceContext.getCascadeLevel() > 0 ) {
 				throw new HibernateException( "Flush during cascade is dangerous" );
 			}
 
 			FlushEvent flushEvent = new FlushEvent( this );
 			for ( FlushEventListener listener : listeners( EventType.FLUSH ) ) {
 				listener.onFlush( flushEvent );
 			}
 
 			delayedAfterCompletion();
 		}
 		catch ( RuntimeException e ) {
 			throw exceptionConverter.convert( e );
 		}
 	}
 
 	@Override
 	public void setFlushMode(FlushModeType flushModeType) {
 		setHibernateFlushMode( FlushModeTypeHelper.getFlushMode( flushModeType ) );
 	}
 
 	@Override
 	public void forceFlush(EntityEntry entityEntry) throws HibernateException {
 		if ( log.isDebugEnabled() ) {
 			log.debugf(
 					"Flushing to force deletion of re-saved object: %s",
 					MessageHelper.infoString( entityEntry.getPersister(), entityEntry.getId(), getFactory() )
 			);
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
 
 	@Override
 	public List list(String query, QueryParameters queryParameters) throws HibernateException {
 		checkOpen();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 
 		HQLQueryPlan plan = queryParameters.getQueryPlan();
 		if ( plan == null ) {
 			plan = getQueryPlan( query, false );
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
 			afterOperation( success );
 			delayedAfterCompletion();
 		}
 		return results;
 	}
 
 	@Override
 	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
 		checkOpen();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getQueryPlan( query, false );
 		autoFlushIfRequired( plan.getQuerySpaces() );
 
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation( success );
 			delayedAfterCompletion();
 		}
 		return result;
 	}
 
 	@Override
 	public int executeNativeUpdate(
 			NativeSQLQuerySpecification nativeQuerySpecification,
 			QueryParameters queryParameters) throws HibernateException {
 		checkOpen();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 		NativeSQLQueryPlan plan = getNativeQueryPlan( nativeQuerySpecification );
 
 
 		autoFlushIfRequired( plan.getCustomQuery().getQuerySpaces() );
 
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation( success );
 			delayedAfterCompletion();
 		}
 		return result;
 	}
 
 	@Override
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		checkOpen();
 		checkTransactionSynchStatus();
 		queryParameters.validateParameters();
 
 		HQLQueryPlan plan = queryParameters.getQueryPlan();
 		if ( plan == null ) {
 			plan = getQueryPlan( query, true );
 		}
 
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
 
 	@Override
-	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
+	public ScrollableResultsImplementor scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		checkOpen();
 		checkTransactionSynchStatus();
 		
 		HQLQueryPlan plan = queryParameters.getQueryPlan();
 		if ( plan == null ) {
 			plan = getQueryPlan( query, false );
 		}
 		
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
 
 	@Override
 	public org.hibernate.query.Query createFilter(Object collection, String queryString) {
 		checkOpen();
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
 
 
 	@Override
 	public Object instantiate(String entityName, Serializable id) throws HibernateException {
 		return instantiate( getFactory().getMetamodel().entityPersister( entityName ), id );
 	}
 
 	/**
 	 * give the interceptor an opportunity to override the default instantiation
 	 */
 	@Override
 	public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
 		checkOpen();
 		checkTransactionSynchStatus();
 		Object result = getInterceptor().instantiate(
 				persister.getEntityName(),
 				persister.getEntityMetamodel().getEntityMode(),
 				id
 		);
 		if ( result == null ) {
 			result = persister.instantiate( id, this );
 		}
 		delayedAfterCompletion();
 		return result;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(final String entityName, final Object object) {
 		checkOpen();
 		if ( entityName == null ) {
 			return getFactory().getMetamodel().entityPersister( guessEntityName( object ) );
 		}
 		else {
 			// try block is a hack around fact that currently tuplizers are not
 			// given the opportunity to resolve a subclass entity name.  this
 			// allows the (we assume custom) interceptor the ability to
 			// influence this decision if we were not able to based on the
 			// given entityName
 			try {
 				return getFactory().getMetamodel().entityPersister( entityName ).getSubclassEntityPersister( object, getFactory() );
 			}
 			catch (HibernateException e) {
 				try {
 					return getEntityPersister( null, object );
 				}
 				catch (HibernateException e2) {
 					throw e;
 				}
 			}
 		}
 	}
 
 	// not for internal use:
 	@Override
 	public Serializable getIdentifier(Object object) throws HibernateException {
 		checkOpen();
 		checkTransactionSynchStatus();
 		if ( object instanceof HibernateProxy ) {
 			LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			if ( li.getSession() != this ) {
 				throw new TransientObjectException( "The proxy was not associated with this session" );
 			}
 			return li.getIdentifier();
 		}
 		else {
 			EntityEntry entry = persistenceContext.getEntry( object );
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
 	@Override
 	public Serializable getContextEntityIdentifier(Object object) {
 		checkOpen();
 		if ( object instanceof HibernateProxy ) {
 			return getProxyIdentifier( object );
 		}
 		else {
 			EntityEntry entry = persistenceContext.getEntry( object );
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
 		final CollectionPersister roleBeforeFlush = ( entry == null ) ? null : entry.getLoadedPersister();
 
 		FilterQueryPlan plan = null;
 		if ( roleBeforeFlush == null ) {
 			// if it was previously unreferenced, we need to flush in order to
 			// get its state into the database in order to execute query
 			flush();
 			entry = persistenceContext.getCollectionEntryOrNull( collection );
 			CollectionPersister roleAfterFlush = ( entry == null ) ? null : entry.getLoadedPersister();
 			if ( roleAfterFlush == null ) {
 				throw new QueryException( "The collection was unreferenced" );
 			}
 			plan = getFactory().getQueryPlanCache().getFilterQueryPlan(
 					filter,
 					roleAfterFlush.getRole(),
 					shallow,
 					getLoadQueryInfluencers().getEnabledFilters()
 			);
 		}
 		else {
 			// otherwise, we only need to flush if there are in-memory changes
 			// to the queried tables
 			plan = getFactory().getQueryPlanCache().getFilterQueryPlan(
 					filter,
 					roleBeforeFlush.getRole(),
 					shallow,
 					getLoadQueryInfluencers().getEnabledFilters()
 			);
 			if ( autoFlushIfRequired( plan.getQuerySpaces() ) ) {
 				// might need to run a different filter entirely afterQuery the flush
 				// because the collection role may have changed
 				entry = persistenceContext.getCollectionEntryOrNull( collection );
 				CollectionPersister roleAfterFlush = ( entry == null ) ? null : entry.getLoadedPersister();
 				if ( roleBeforeFlush != roleAfterFlush ) {
 					if ( roleAfterFlush == null ) {
 						throw new QueryException( "The collection was dereferenced" );
 					}
 					plan = getFactory().getQueryPlanCache().getFilterQueryPlan(
 							filter,
 							roleAfterFlush.getRole(),
 							shallow,
 							getLoadQueryInfluencers().getEnabledFilters()
 					);
 				}
 			}
 		}
 
 		if ( parameters != null ) {
 			parameters.getPositionalParameterValues()[0] = entry.getLoadedKey();
 			parameters.getPositionalParameterTypes()[0] = entry.getLoadedPersister().getKeyType();
 		}
 
 		return plan;
 	}
 
 	@Override
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters) {
 		checkOpen();
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
 			afterOperation( success );
 			delayedAfterCompletion();
 		}
 		return results;
 	}
 
 	@Override
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		FilterQueryPlan plan = getFilterQueryPlan( collection, filter, queryParameters, true );
 		Iterator itr = plan.performIterate( queryParameters, this );
 		delayedAfterCompletion();
 		return itr;
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		DeprecationLogger.DEPRECATION_LOGGER.deprecatedLegacyCriteria();
 		checkOpen();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName, String alias) {
 		DeprecationLogger.DEPRECATION_LOGGER.deprecatedLegacyCriteria();
 		checkOpen();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( entityName, alias, this );
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass) {
 		DeprecationLogger.DEPRECATION_LOGGER.deprecatedLegacyCriteria();
 		checkOpen();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName) {
 		DeprecationLogger.DEPRECATION_LOGGER.deprecatedLegacyCriteria();
 		checkOpen();
 		checkTransactionSynchStatus();
 		return new CriteriaImpl( entityName, this );
 	}
 
 	@Override
-	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
+	public ScrollableResultsImplementor scroll(Criteria criteria, ScrollMode scrollMode) {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 
 		checkOpen();
 		checkTransactionSynchStatus();
 
 		String entityName = criteriaImpl.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable( entityName ),
 				getFactory(),
 				criteriaImpl,
 				entityName,
 				getLoadQueryInfluencers()
 		);
 		autoFlushIfRequired( loader.getQuerySpaces() );
 		dontFlushFromFind++;
 		try {
 			return loader.scroll( this, scrollMode );
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
 	@Override
 	public List list(Criteria criteria) throws HibernateException {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 
 		final NaturalIdLoadAccess naturalIdLoadAccess = this.tryNaturalIdLoadAccess( criteriaImpl );
 		if ( naturalIdLoadAccess != null ) {
 			// EARLY EXIT!
 			return Arrays.asList( naturalIdLoadAccess.load() );
 		}
 
 
 		checkOpen();
 //		checkTransactionSynchStatus();
 
 		String[] implementors = getFactory().getMetamodel().getImplementors( criteriaImpl.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		Set spaces = new HashSet();
 		for ( int i = 0; i < size; i++ ) {
 
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 					getFactory(),
 					criteriaImpl,
 					implementors[i],
 					getLoadQueryInfluencers()
 			);
 
 			spaces.addAll( loaders[i].getQuerySpaces() );
 
 		}
 
 		autoFlushIfRequired( spaces );
 
 		List results = Collections.EMPTY_LIST;
 		dontFlushFromFind++;
 		boolean success = false;
 		try {
 			for ( int i = 0; i < size; i++ ) {
 				final List currentResults = loaders[i].list( this );
 				currentResults.addAll( results );
 				results = currentResults;
 			}
 			success = true;
 		}
 		finally {
 			dontFlushFromFind--;
 			afterOperation( success );
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
 	 * should be performed
 	 */
 	private NaturalIdLoadAccess tryNaturalIdLoadAccess(CriteriaImpl criteria) {
 		// See if the criteria lookup is by naturalId
 		if ( !criteria.isLookupByNaturalKey() ) {
 			return null;
 		}
 
 		final String entityName = criteria.getEntityOrClassName();
 		final EntityPersister entityPersister = getFactory().getMetamodel().entityPersister( entityName );
 
 		// Verify the entity actually has a natural id, needed for legacy support as NaturalIdentifier criteria
 		// queries did no natural id validation
 		if ( !entityPersister.hasNaturalIdentifier() ) {
 			return null;
 		}
 
 		// Since isLookupByNaturalKey is true there can be only one CriterionEntry and getCriterion() will
 		// return an instanceof NaturalIdentifier
 		final CriterionEntry criterionEntry = criteria.iterateExpressionEntries().next();
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
 		for ( int naturalIdentifierProperty : naturalIdentifierProperties ) {
 			final String naturalIdProperty = propertyNames[naturalIdentifierProperty];
 			final Object naturalIdValue = naturalIdValues.get( naturalIdProperty );
 
 			if ( naturalIdValue == null ) {
 				// A NaturalId property is missing from the critera query, can't use NaturalIdLoadAccess
 				return null;
 			}
 
 			naturalIdLoader.using( naturalIdProperty, naturalIdValue );
 		}
 
 		// Criteria query contains a valid naturalId, use the new API
 		log.warn(
 				"Session.byNaturalId(" + entityName
 						+ ") should be used for naturalId queries instead of Restrictions.naturalId() from a Criteria"
 		);
 
 		return naturalIdLoader;
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
 		EntityPersister persister = getFactory().getMetamodel().entityPersister( entityName );
 		if ( !( persister instanceof OuterJoinLoadable ) ) {
 			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
 		}
 		return (OuterJoinLoadable) persister;
 	}
 
 	@Override
 	public boolean contains(Object object) {
 		checkOpen();
 		checkTransactionSynchStatus();
 
 		if ( object == null ) {
 			return false;
 		}
 
 		try {
 			if ( object instanceof HibernateProxy ) {
 				//do not use proxiesByKey, since not all
 				//proxies that point to this session's
 				//instances are in that collection!
 				LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 				if ( li.isUninitialized() ) {
 					//if it is an uninitialized proxy, pointing
 					//with this session, then when it is accessed,
 					//the underlying instance will be "contained"
 					return li.getSession() == this;
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
 
 			if ( entry == null ) {
 				if ( !HibernateProxy.class.isInstance( object ) && persistenceContext.getEntry( object ) == null ) {
 					// check if it is even an entity -> if not throw an exception (per JPA)
 					try {
 						final String entityName = getEntityNameResolver().resolveEntityName( object );
 						if ( entityName == null ) {
 							throw new IllegalArgumentException( "Could not resolve entity-name [" + object + "]" );
 						}
 						getSessionFactory().getMetamodel().entityPersister( object.getClass() );
 					}
 					catch (HibernateException e) {
 						throw new IllegalArgumentException( "Not an entity [" + object.getClass() + "]", e );
 					}
 				}
 				return false;
 			}
 			else {
 				return entry.getStatus() != Status.DELETED && entry.getStatus() != Status.GONE;
 			}
 		}
 		catch (MappingException e) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch (RuntimeException e) {
 			throw exceptionConverter.convert( e );
 		}
 	}
 
 	@Override
 	public boolean contains(String entityName, Object object) {
 		checkOpen();
 		checkTransactionSynchStatus();
 
 		if ( object == null ) {
 			return false;
 		}
 
 		try {
 			if ( !HibernateProxy.class.isInstance( object ) && persistenceContext.getEntry( object ) == null ) {
 				// check if it is an entity -> if not throw an exception (per JPA)
 				try {
 					getSessionFactory().getMetamodel().entityPersister( entityName );
 				}
 				catch (HibernateException e) {
 					throw new IllegalArgumentException( "Not an entity [" + entityName + "] : " + object );
 				}
 			}
 
 			if ( object instanceof HibernateProxy ) {
 				//do not use proxiesByKey, since not all
 				//proxies that point to this session's
 				//instances are in that collection!
 				LazyInitializer li = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 				if ( li.isUninitialized() ) {
 					//if it is an uninitialized proxy, pointing
 					//with this session, then when it is accessed,
 					//the underlying instance will be "contained"
 					return li.getSession() == this;
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
 		catch (MappingException e) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch (RuntimeException e) {
 			throw exceptionConverter.convert( e );
 		}
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName) {
 		checkOpen();
 //		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
 		checkOpen();
 //		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName, resultSetMappings );
 	}
 
 	@Override
 	public ProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
 		checkOpen();
 //		checkTransactionSynchStatus();
 		return super.createStoredProcedureCall( procedureName, resultClasses );
 	}
 
 	@Override
-	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) {
+	public ScrollableResultsImplementor scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) {
 		checkOpen();
 //		checkTransactionSynchStatus();
 
 		if ( log.isTraceEnabled() ) {
 			log.tracev( "Scroll SQL query: {0}", customQuery.getSQL() );
 		}
 
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		autoFlushIfRequired( loader.getQuerySpaces() );
 
 		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
 		try {
 			return loader.scroll( queryParameters, this );
 		}
 		finally {
 			delayedAfterCompletion();
 			dontFlushFromFind--;
 		}
 	}
 
 	// basically just an adapted copy of find(CriteriaImpl)
 	@Override
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) {
 		checkOpen();
 //		checkTransactionSynchStatus();
 
 		if ( log.isTraceEnabled() ) {
 			log.tracev( "SQL query: {0}", customQuery.getSQL() );
 		}
 
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		autoFlushIfRequired( loader.getQuerySpaces() );
 
 		dontFlushFromFind++;
 		boolean success = false;
 		try {
 			List results = loader.list( this, queryParameters );
 			success = true;
 			return results;
 		}
 		finally {
 			dontFlushFromFind--;
 			delayedAfterCompletion();
 			afterOperation( success );
 		}
 	}
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 //		checkTransactionSynchStatus();
 		return getFactory();
 	}
 
 	@Override
 	public void initializeCollection(PersistentCollection collection, boolean writing) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		InitializeCollectionEvent event = new InitializeCollectionEvent( collection, this );
 		for ( InitializeCollectionEventListener listener : listeners( EventType.INIT_COLLECTION ) ) {
 			listener.onInitializeCollection( event );
 		}
 		delayedAfterCompletion();
 	}
 
 	@Override
 	public String bestGuessEntityName(Object object) {
 		if ( object instanceof HibernateProxy ) {
 			LazyInitializer initializer = ( (HibernateProxy) object ).getHibernateLazyInitializer();
 			// it is possible for this method to be called during flush processing,
 			// so make certain that we do not accidentally initialize an uninitialized proxy
 			if ( initializer.isUninitialized() ) {
 				return initializer.getEntityName();
 			}
 			object = initializer.getImplementation();
 		}
 		EntityEntry entry = persistenceContext.getEntry( object );
 		if ( entry == null ) {
 			return guessEntityName( object );
 		}
 		else {
 			return entry.getPersister().getEntityName();
 		}
 	}
 
 	@Override
 	public String getEntityName(Object object) {
 		checkOpen();
 //		checkTransactionSynchStatus();
 		if ( object instanceof HibernateProxy ) {
 			if ( !persistenceContext.containsProxy( object ) ) {
 				throw new TransientObjectException( "proxy was not associated with the session" );
 			}
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation();
 		}
 
 		EntityEntry entry = persistenceContext.getEntry( object );
 		if ( entry == null ) {
 			throwTransientObjectException( object );
 		}
 		return entry.getPersister().getEntityName();
 	}
 
 	private void throwTransientObjectException(Object object) throws HibernateException {
 		throw new TransientObjectException(
 				"object references an unsaved transient instance - save the transient instance beforeQuery flushing: " +
 						guessEntityName( object )
 		);
 	}
 
 	@Override
 	public String guessEntityName(Object object) throws HibernateException {
 		checkOpen();
 		return getEntityNameResolver().resolveEntityName( object );
 	}
 
 	@Override
 	public void cancelQuery() throws HibernateException {
 		checkOpen();
 		getJdbcCoordinator().cancelLastQuery();
 	}
 
 
 	@Override
 	public int getDontFlushFromFind() {
 		return dontFlushFromFind;
 	}
 
 	@Override
 	public String toString() {
 		StringBuilder buf = new StringBuilder( 500 )
 				.append( "SessionImpl(" );
 		if ( !isClosed() ) {
 			buf.append( persistenceContext )
 					.append( ";" )
 					.append( actionQueue );
 		}
 		else {
 			buf.append( "<closed>" );
 		}
 		return buf.append( ')' ).toString();
 	}
 
 	@Override
 	public ActionQueue getActionQueue() {
 		checkOpen();
 //		checkTransactionSynchStatus();
 		return actionQueue;
 	}
 
 	@Override
 	public PersistenceContext getPersistenceContext() {
 		checkOpen();
 //		checkTransactionSynchStatus();
 		return persistenceContext;
 	}
 
 	@Override
 	public SessionStatistics getStatistics() {
 		checkTransactionSynchStatus();
 		return new SessionStatisticsImpl( this );
 	}
 
 	@Override
 	public boolean isEventSource() {
 		checkTransactionSynchStatus();
 		return true;
 	}
 
 	@Override
 	public boolean isDefaultReadOnly() {
 		return persistenceContext.isDefaultReadOnly();
 	}
 
 	@Override
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		persistenceContext.setDefaultReadOnly( defaultReadOnly );
 	}
 
 	@Override
 	public boolean isReadOnly(Object entityOrProxy) {
 		checkOpen();
 //		checkTransactionSynchStatus();
 		return persistenceContext.isReadOnly( entityOrProxy );
 	}
 
 	@Override
 	public void setReadOnly(Object entity, boolean readOnly) {
 		checkOpen();
 //		checkTransactionSynchStatus();
 		persistenceContext.setReadOnly( entity, readOnly );
 	}
 
 	@Override
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
 
 	@Override
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
 		return getJdbcCoordinator().coordinateWork( work );
 	}
 
 	@Override
 	public void afterScrollOperation() {
 		// nothing to do in a stateful session
 	}
 
 	@Override
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return loadQueryInfluencers;
 	}
 
 	// filter support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Filter getEnabledFilter(String filterName) {
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.getEnabledFilter( filterName );
 	}
 
 	@Override
 	public Filter enableFilter(String filterName) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		return loadQueryInfluencers.enableFilter( filterName );
 	}
 
 	@Override
 	public void disableFilter(String filterName) {
 		checkOpen();
 		checkTransactionSynchStatus();
 		loadQueryInfluencers.disableFilter( filterName );
 	}
 
 
 	// fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		return loadQueryInfluencers.isFetchProfileEnabled( name );
 	}
 
 	@Override
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.enableFetchProfile( name );
 	}
 
 	@Override
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		loadQueryInfluencers.disableFetchProfile( name );
 	}
 
 	@Override
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
 
 	@Override
 	public void beforeTransactionCompletion() {
 		log.tracef( "SessionImpl#beforeTransactionCompletion()" );
 		flushBeforeTransactionCompletion();
 		actionQueue.beforeTransactionCompletion();
 		try {
 			getInterceptor().beforeTransactionCompletion( getCurrentTransaction() );
 		}
 		catch (Throwable t) {
 			log.exceptionInBeforeTransactionCompletionInterceptor( t );
 		}
 	}
 
 	@Override
 	public void afterTransactionCompletion(boolean successful, boolean delayed) {
 		log.tracef( "SessionImpl#afterTransactionCompletion(successful=%s, delayed=%s)", successful, delayed );
 
 		if ( !isClosed() ) {
 			if ( autoClear ||!successful ) {
 				internalClear();
 			}
 		}
 
 		persistenceContext.afterTransactionCompletion();
 		actionQueue.afterTransactionCompletion( successful );
 
 		getEventListenerManager().transactionCompletion( successful );
 
 		if ( getFactory().getStatistics().isStatisticsEnabled() ) {
 			getFactory().getStatistics().endTransaction( successful );
 		}
 
 		try {
 			getInterceptor().afterTransactionCompletion( getCurrentTransaction() );
 		}
 		catch (Throwable t) {
 			log.exceptionInAfterTransactionCompletionInterceptor( t );
 		}
 
 		if ( !delayed ) {
 			if ( shouldAutoClose() && !isClosed() ) {
 				managedClose();
 			}
 		}
 	}
 
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
 
 	private static class SharedSessionBuilderImpl<T extends SharedSessionBuilder>
 			extends SessionFactoryImpl.SessionBuilderImpl<T>
 			implements SharedSessionBuilder<T>, SharedSessionCreationOptions {
 		private final SessionImpl session;
 		private boolean shareTransactionContext;
 
 		private SharedSessionBuilderImpl(SessionImpl session) {
 			super( (SessionFactoryImpl) session.getFactory() );
 			this.session = session;
 			super.owner( session.sessionOwner );
 			super.tenantIdentifier( session.getTenantIdentifier() );
 		}
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// SharedSessionBuilder
 
 
 		@Override
 		public T tenantIdentifier(String tenantIdentifier) {
 			// todo : is this always true?  Or just in the case of sharing JDBC resources?
 			throw new SessionException( "Cannot redefine tenant identifier on child session" );
 		}
 
 		@Override
 		public T interceptor() {
 			return interceptor( session.getInterceptor() );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public T connection() {
 			this.shareTransactionContext = true;
 			return (T) this;
 		}
 
 		@Override
 		public T connectionReleaseMode() {
 			return connectionReleaseMode( session.getJdbcCoordinator().getLogicalConnection().getConnectionHandlingMode().getReleaseMode() );
 		}
 
 		@Override
 		public T connectionHandlingMode() {
 			return connectionHandlingMode( session.getJdbcCoordinator().getLogicalConnection().getConnectionHandlingMode() );
 		}
 
 		@Override
 		public T autoJoinTransactions() {
 			return autoJoinTransactions( session.isAutoCloseSessionEnabled() );
 		}
 
 		@Override
 		public T flushMode() {
 			return flushMode( session.getHibernateFlushMode() );
 		}
 
 		@Override
 		public T autoClose() {
 			return autoClose( session.autoClose );
 		}
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// SharedSessionCreationOptions
 
 		@Override
 		public boolean isTransactionCoordinatorShared() {
 			return shareTransactionContext;
 		}
 
 		@Override
 		public TransactionCoordinator getTransactionCoordinator() {
 			return shareTransactionContext ? session.getTransactionCoordinator() : null;
 		}
 
 		@Override
 		public JdbcCoordinator getJdbcCoordinator() {
 			return shareTransactionContext ? session.getJdbcCoordinator() : null;
 		}
 
 		@Override
 		public TransactionImplementor getTransaction() {
 			return shareTransactionContext ? session.getCurrentTransaction() : null;
 		}
 
 		@Override
 		public ActionQueue.TransactionCompletionProcesses getTransactionCompletionProcesses() {
 			return shareTransactionContext ?
 					session.getActionQueue().getTransactionCompletionProcesses() :
 					null;
 		}
 
 	}
 
 	private class LockRequestImpl implements LockRequest {
 		private final LockOptions lockOptions;
 
 		private LockRequestImpl(LockOptions lo) {
 			lockOptions = new LockOptions();
 			LockOptions.copy( lo, lockOptions );
 		}
 
 		@Override
 		public LockMode getLockMode() {
 			return lockOptions.getLockMode();
 		}
 
 		@Override
 		public LockRequest setLockMode(LockMode lockMode) {
 			lockOptions.setLockMode( lockMode );
 			return this;
 		}
 
 		@Override
 		public int getTimeOut() {
 			return lockOptions.getTimeOut();
 		}
 
 		@Override
 		public LockRequest setTimeOut(int timeout) {
 			lockOptions.setTimeOut( timeout );
 			return this;
 		}
 
 		@Override
 		public boolean getScope() {
 			return lockOptions.getScope();
 		}
 
 		@Override
 		public LockRequest setScope(boolean scope) {
 			lockOptions.setScope( scope );
 			return this;
 		}
 
 		@Override
 		public void lock(String entityName, Object object) throws HibernateException {
 			fireLock( entityName, object, lockOptions );
 		}
 
 		@Override
 		public void lock(Object object) throws HibernateException {
 			fireLock( object, lockOptions );
 		}
 	}
 
 	@Override
 	protected void addSharedSessionTransactionObserver(TransactionCoordinator transactionCoordinator) {
 		transactionCoordinator.addObserver(
 				new TransactionObserver() {
 					@Override
 					public void afterBegin() {
 					}
 
 					@Override
 					public void beforeCompletion() {
 						if ( isOpen() && getHibernateFlushMode() !=  FlushMode.MANUAL ) {
 							managedFlush();
 						}
 						actionQueue.beforeTransactionCompletion();
 						try {
 							getInterceptor().beforeTransactionCompletion( getCurrentTransaction() );
 						}
 						catch (Throwable t) {
 							log.exceptionInBeforeTransactionCompletionInterceptor( t );
 						}
 					}
 
 					@Override
 					public void afterCompletion(boolean successful, boolean delayed) {
 						afterTransactionCompletion( successful, delayed );
 						if ( !isClosed() && autoClose ) {
 							managedClose();
 						}
 					}
 				}
 		);
 	}
 
 	private class IdentifierLoadAccessImpl<T> implements IdentifierLoadAccess<T> {
 		private final EntityPersister entityPersister;
 		private LockOptions lockOptions;
 		private CacheMode cacheMode;
 
 		private IdentifierLoadAccessImpl(EntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 		}
 
 		private IdentifierLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private IdentifierLoadAccessImpl(Class<T> entityClass) {
 			this( locateEntityPersister( entityClass ) );
 		}
 
 		@Override
 		public final IdentifierLoadAccessImpl<T> with(LockOptions lockOptions) {
 			this.lockOptions = lockOptions;
 			return this;
 		}
 
 		@Override
 		public IdentifierLoadAccess<T> with(CacheMode cacheMode) {
 			this.cacheMode = cacheMode;
 			return this;
 		}
 
 		@Override
 		public final T getReference(Serializable id) {
 			CacheMode sessionCacheMode = getCacheMode();
 			boolean cacheModeChanged = false;
 			if ( cacheMode != null ) {
 				// naive check for now...
 				// todo : account for "conceptually equal"
 				if ( cacheMode != sessionCacheMode ) {
 					setCacheMode( cacheMode );
 					cacheModeChanged = true;
 				}
 			}
 
 			try {
 				return doGetReference( id );
 			}
 			finally {
 				if ( cacheModeChanged ) {
 					// change it back
 					setCacheMode( sessionCacheMode );
 				}
 			}
 		}
 
 		@SuppressWarnings("unchecked")
 		protected T doGetReference(Serializable id) {
 			if ( this.lockOptions != null ) {
 				LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), lockOptions, SessionImpl.this );
 				fireLoad( event, LoadEventListener.LOAD );
 				return (T) event.getResult();
 			}
 
 			LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), false, SessionImpl.this );
 			boolean success = false;
 			try {
 				fireLoad( event, LoadEventListener.LOAD );
 				if ( event.getResult() == null ) {
 					getFactory().getEntityNotFoundDelegate().handleEntityNotFound(
 							entityPersister.getEntityName(),
 							id
 					);
 				}
 				success = true;
 				return (T) event.getResult();
 			}
 			finally {
 				afterOperation( success );
 			}
 		}
 
 		@Override
 		public final T load(Serializable id) {
 			CacheMode sessionCacheMode = getCacheMode();
 			boolean cacheModeChanged = false;
 			if ( cacheMode != null ) {
 				// naive check for now...
 				// todo : account for "conceptually equal"
 				if ( cacheMode != sessionCacheMode ) {
 					setCacheMode( cacheMode );
 					cacheModeChanged = true;
 				}
 			}
 
 			try {
 				return doLoad( id );
 			}
 			finally {
 				if ( cacheModeChanged ) {
 					// change it back
 					setCacheMode( sessionCacheMode );
 				}
 			}
 		}
 
 		@SuppressWarnings("unchecked")
 		protected final T doLoad(Serializable id) {
 			if ( this.lockOptions != null ) {
 				LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), lockOptions, SessionImpl.this );
 				fireLoad( event, LoadEventListener.GET );
 				return (T) event.getResult();
 			}
 
 			LoadEvent event = new LoadEvent( id, entityPersister.getEntityName(), false, SessionImpl.this );
 			boolean success = false;
 			try {
 				fireLoad( event, LoadEventListener.GET );
 				success = true;
 			}
 			catch (ObjectNotFoundException e) {
 				// if session cache contains proxy for non-existing object
 			}
 			finally {
 				afterOperation( success );
 			}
 			return (T) event.getResult();
 		}
 	}
 
 	private class MultiIdentifierLoadAccessImpl<T> implements MultiIdentifierLoadAccess<T>, MultiLoadOptions {
 		private final EntityPersister entityPersister;
 		private LockOptions lockOptions;
 		private CacheMode cacheMode;
 		private Integer batchSize;
 		private boolean sessionCheckingEnabled;
 
 		public MultiIdentifierLoadAccessImpl(EntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 		}
 
 		@Override
 		public LockOptions getLockOptions() {
 			return lockOptions;
 		}
 
 		@Override
 		public final MultiIdentifierLoadAccessImpl<T> with(LockOptions lockOptions) {
 			this.lockOptions = lockOptions;
 			return this;
 		}
 
 		@Override
 		public MultiIdentifierLoadAccessImpl<T> with(CacheMode cacheMode) {
 			this.cacheMode = cacheMode;
 			return this;
 		}
 
 		@Override
 		public Integer getBatchSize() {
 			return batchSize;
 		}
 
 		@Override
 		public MultiIdentifierLoadAccess<T> withBatchSize(int batchSize) {
 			if ( batchSize < 1 ) {
 				this.batchSize = null;
 			}
 			else {
 				this.batchSize = batchSize;
 			}
 			return this;
 		}
 
 		@Override
 		public boolean isSessionCheckingEnabled() {
 			return sessionCheckingEnabled;
 		}
 
 		@Override
 		public MultiIdentifierLoadAccess<T> enableSessionCheck(boolean enabled) {
 			this.sessionCheckingEnabled = enabled;
 			return this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public <K extends Serializable> List<T> multiLoad(K... ids) {
 			CacheMode sessionCacheMode = getCacheMode();
 			boolean cacheModeChanged = false;
 			if ( cacheMode != null ) {
 				// naive check for now...
 				// todo : account for "conceptually equal"
 				if ( cacheMode != sessionCacheMode ) {
 					setCacheMode( cacheMode );
 					cacheModeChanged = true;
 				}
 			}
 
 			try {
 				return entityPersister.multiLoad( ids, SessionImpl.this, this );
 			}
 			finally {
 				if ( cacheModeChanged ) {
 					// change it back
 					setCacheMode( sessionCacheMode );
 				}
 			}
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public <K extends Serializable> List<T> multiLoad(List<K> ids) {
 			CacheMode sessionCacheMode = getCacheMode();
 			boolean cacheModeChanged = false;
 			if ( cacheMode != null ) {
 				// naive check for now...
 				// todo : account for "conceptually equal"
 				if ( cacheMode != sessionCacheMode ) {
 					setCacheMode( cacheMode );
 					cacheModeChanged = true;
 				}
 			}
 
 			try {
 				return entityPersister.multiLoad( ids.toArray( new Serializable[ ids.size() ] ), SessionImpl.this, this );
 			}
 			finally {
 				if ( cacheModeChanged ) {
 					// change it back
 					setCacheMode( sessionCacheMode );
 				}
 			}
 		}
 	}
 
 	private EntityPersister locateEntityPersister(Class entityClass) {
 		return getFactory().getMetamodel().locateEntityPersister( entityClass );
 	}
 
 	private EntityPersister locateEntityPersister(String entityName) {
 		return getFactory().getMetamodel().locateEntityPersister( entityName );
 	}
 
 	private abstract class BaseNaturalIdLoadAccessImpl<T> {
 		private final EntityPersister entityPersister;
 		private LockOptions lockOptions;
 		private boolean synchronizationEnabled = true;
 
 		private BaseNaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 
 			if ( !entityPersister.hasNaturalIdentifier() ) {
 				throw new HibernateException(
 						String.format( "Entity [%s] did not define a natural id", entityPersister.getEntityName() )
 				);
 			}
 		}
 
 		public BaseNaturalIdLoadAccessImpl<T> with(LockOptions lockOptions) {
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
 			if ( !synchronizationEnabled ) {
 				// synchronization (this process) was disabled
 				return;
 			}
 			if ( entityPersister.getEntityMetamodel().hasImmutableNaturalId() ) {
 				// only mutable natural-ids need this processing
 				return;
 			}
 			if ( !isTransactionInProgress() ) {
 				// not in a transaction so skip synchronization
 				return;
 			}
 
 			final boolean debugEnabled = log.isDebugEnabled();
 			for ( Serializable pk : getPersistenceContext().getNaturalIdHelper()
 					.getCachedPkResolutions( entityPersister ) ) {
 				final EntityKey entityKey = generateEntityKey( pk, entityPersister );
 				final Object entity = getPersistenceContext().getEntity( entityKey );
 				final EntityEntry entry = getPersistenceContext().getEntry( entity );
 
 				if ( entry == null ) {
 					if ( debugEnabled ) {
 						log.debug(
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
 
 	private class NaturalIdLoadAccessImpl<T> extends BaseNaturalIdLoadAccessImpl<T> implements NaturalIdLoadAccess<T> {
 		private final Map<String, Object> naturalIdParameters = new LinkedHashMap<String, Object>();
 
 		private NaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			super( entityPersister );
 		}
 
 		private NaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private NaturalIdLoadAccessImpl(Class entityClass) {
 			this( locateEntityPersister( entityClass ) );
 		}
 
 		@Override
 		public NaturalIdLoadAccessImpl<T> with(LockOptions lockOptions) {
 			return (NaturalIdLoadAccessImpl<T>) super.with( lockOptions );
 		}
 
 		@Override
 		public NaturalIdLoadAccess<T> using(String attributeName, Object value) {
 			naturalIdParameters.put( attributeName, value );
 			return this;
 		}
 
 		@Override
 		public NaturalIdLoadAccessImpl<T> setSynchronizationEnabled(boolean synchronizationEnabled) {
 			super.synchronizationEnabled( synchronizationEnabled );
 			return this;
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public final T getReference() {
 			final Serializable entityId = resolveNaturalId( this.naturalIdParameters );
 			if ( entityId == null ) {
 				return null;
 			}
 			return (T) this.getIdentifierLoadAccess().getReference( entityId );
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public final T load() {
 			final Serializable entityId = resolveNaturalId( this.naturalIdParameters );
 			if ( entityId == null ) {
 				return null;
 			}
 			try {
 				return (T) this.getIdentifierLoadAccess().load( entityId );
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
 
 	private class SimpleNaturalIdLoadAccessImpl<T> extends BaseNaturalIdLoadAccessImpl<T>
 			implements SimpleNaturalIdLoadAccess<T> {
 		private final String naturalIdAttributeName;
 
 		private SimpleNaturalIdLoadAccessImpl(EntityPersister entityPersister) {
 			super( entityPersister );
 
 			if ( entityPersister.getNaturalIdentifierProperties().length != 1 ) {
 				throw new HibernateException(
 						String.format(
 								"Entity [%s] did not define a simple natural id",
 								entityPersister.getEntityName()
 						)
 				);
 			}
 
 			final int naturalIdAttributePosition = entityPersister.getNaturalIdentifierProperties()[0];
 			this.naturalIdAttributeName = entityPersister.getPropertyNames()[naturalIdAttributePosition];
 		}
 
 		private SimpleNaturalIdLoadAccessImpl(String entityName) {
 			this( locateEntityPersister( entityName ) );
 		}
 
 		private SimpleNaturalIdLoadAccessImpl(Class entityClass) {
 			this( locateEntityPersister( entityClass ) );
 		}
 
 		@Override
 		public final SimpleNaturalIdLoadAccessImpl<T> with(LockOptions lockOptions) {
 			return (SimpleNaturalIdLoadAccessImpl<T>) super.with( lockOptions );
 		}
 
 		private Map<String, Object> getNaturalIdParameters(Object naturalIdValue) {
 			return Collections.singletonMap( naturalIdAttributeName, naturalIdValue );
 		}
 
 		@Override
 		public SimpleNaturalIdLoadAccessImpl<T> setSynchronizationEnabled(boolean synchronizationEnabled) {
 			super.synchronizationEnabled( synchronizationEnabled );
 			return this;
 		}
 
 		@Override
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
index 02a2ad8b7a..f109b9236c 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
@@ -1,672 +1,673 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.internal;
 
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.List;
 import javax.transaction.SystemException;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.EntityMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.StatelessSession;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.internal.StatefulPersistenceContext;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
 import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
 import org.hibernate.id.IdentifierGeneratorHelper;
 import org.hibernate.loader.criteria.CriteriaLoader;
 import org.hibernate.loader.custom.CustomLoader;
 import org.hibernate.loader.custom.CustomQuery;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 
 /**
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class StatelessSessionImpl extends AbstractSharedSessionContract implements StatelessSession {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( StatelessSessionImpl.class );
 
 	private static LoadQueryInfluencers NO_INFLUENCERS = new LoadQueryInfluencers( null ) {
 		@Override
 		public String getInternalFetchProfile() {
 			return null;
 		}
 
 		@Override
 		public void setInternalFetchProfile(String internalFetchProfile) {
 		}
 	};
 
 	private PersistenceContext temporaryPersistenceContext = new StatefulPersistenceContext( this );
 
 	StatelessSessionImpl(SessionFactoryImpl factory, SessionCreationOptions options) {
 		super( factory, options );
 	}
 
 	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return true;
 	}
 
 	// inserts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public Serializable insert(Object entity) {
 		checkOpen();
 		return insert( null, entity );
 	}
 
 	@Override
 	public Serializable insert(String entityName, Object entity) {
 		checkOpen();
 		EntityPersister persister = getEntityPersister( entityName, entity );
 		Serializable id = persister.getIdentifierGenerator().generate( this, entity );
 		Object[] state = persister.getPropertyValues( entity );
 		if ( persister.isVersioned() ) {
 			boolean substitute = Versioning.seedVersion(
 					state,
 					persister.getVersionProperty(),
 					persister.getVersionType(),
 					this
 			);
 			if ( substitute ) {
 				persister.setPropertyValues( entity, state );
 			}
 		}
 		if ( id == IdentifierGeneratorHelper.POST_INSERT_INDICATOR ) {
 			id = persister.insert( state, entity, this );
 		}
 		else {
 			persister.insert( id, state, entity, this );
 		}
 		persister.setIdentifier( entity, id, this );
 		return id;
 	}
 
 
 	// deletes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void delete(Object entity) {
 		checkOpen();
 		delete( null, entity );
 	}
 
 	@Override
 	public void delete(String entityName, Object entity) {
 		checkOpen();
 		EntityPersister persister = getEntityPersister( entityName, entity );
 		Serializable id = persister.getIdentifier( entity, this );
 		Object version = persister.getVersion( entity );
 		persister.delete( id, version, entity, this );
 	}
 
 
 	// updates ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void update(Object entity) {
 		checkOpen();
 		update( null, entity );
 	}
 
 	@Override
 	public void update(String entityName, Object entity) {
 		checkOpen();
 		EntityPersister persister = getEntityPersister( entityName, entity );
 		Serializable id = persister.getIdentifier( entity, this );
 		Object[] state = persister.getPropertyValues( entity );
 		Object oldVersion;
 		if ( persister.isVersioned() ) {
 			oldVersion = persister.getVersion( entity );
 			Object newVersion = Versioning.increment( oldVersion, persister.getVersionType(), this );
 			Versioning.setVersion( state, newVersion, persister );
 			persister.setPropertyValues( entity, state );
 		}
 		else {
 			oldVersion = null;
 		}
 		persister.update( id, state, null, false, null, oldVersion, entity, null, this );
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
 		return get( entityName, id, LockMode.NONE );
 	}
 
 	@Override
 	public Object get(String entityName, Serializable id, LockMode lockMode) {
 		checkOpen();
 		Object result = getFactory().getEntityPersister( entityName )
 				.load( id, null, lockMode, this );
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
 			final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
 			final Object ck = cache.generateCacheKey( id, persister, getFactory(), getTenantIdentifier() );
 			cache.evict( ck );
 		}
 		String previousFetchProfile = this.getLoadQueryInfluencers().getInternalFetchProfile();
 		Object result = null;
 		try {
 			this.getLoadQueryInfluencers().setInternalFetchProfile( "refresh" );
 			result = persister.load( id, entity, lockMode, this );
 		}
 		finally {
 			this.getLoadQueryInfluencers().setInternalFetchProfile( previousFetchProfile );
 		}
 		UnresolvableObjectException.throwIfNull( result, id, persister.getEntityName() );
 	}
 
 	@Override
 	public Object immediateLoad(String entityName, Serializable id)
 			throws HibernateException {
 		throw new SessionException( "proxies cannot be fetched by a stateless session" );
 	}
 
 	@Override
 	public void initializeCollection(
 			PersistentCollection collection,
 			boolean writing) throws HibernateException {
 		throw new SessionException( "collections cannot be fetched by a stateless session" );
 	}
 
 	@Override
 	public Object instantiate(
 			String entityName,
 			Serializable id) throws HibernateException {
 		checkOpen();
 		return getFactory().getEntityPersister( entityName ).instantiate( id, this );
 	}
 
 	@Override
 	public Object internalLoad(
 			String entityName,
 			Serializable id,
 			boolean eager,
 			boolean nullable) throws HibernateException {
 		checkOpen();
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
 	public boolean isAutoCloseSessionEnabled() {
 		return getFactory().getSessionFactoryOptions().isAutoCloseSessionEnabled();
 	}
 
 	@Override
 	public boolean shouldAutoClose() {
 		return isAutoCloseSessionEnabled() && !isClosed();
 	}
 
 
 	private boolean isFlushModeNever() {
 		return false;
 	}
 
 	private void managedClose() {
 		if ( isClosed() ) {
 			throw new SessionException( "Session was already closed!" );
 		}
 		close();
 	}
 
 	private void managedFlush() {
 		checkOpen();
 		getJdbcCoordinator().executeBatch();
 	}
 
 	@Override
 	public String bestGuessEntityName(Object object) {
 		if ( object instanceof HibernateProxy ) {
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation();
 		}
 		return guessEntityName( object );
 	}
 
 	@Override
 	public Connection connection() {
 		checkOpen();
 		return getJdbcCoordinator().getLogicalConnection().getPhysicalConnection();
 	}
 
 	@Override
 	public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
 		checkOpen();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getQueryPlan( query, false );
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation( success );
 		}
 		temporaryPersistenceContext.clear();
 		return result;
 	}
 
 	@Override
 	public CacheMode getCacheMode() {
 		return CacheMode.IGNORE;
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
 	public void setHibernateFlushMode(FlushMode flushMode) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public int getDontFlushFromFind() {
 		return 0;
 	}
 
 	@Override
 	public Serializable getContextEntityIdentifier(Object object) {
 		checkOpen();
 		return null;
 	}
 
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	@Override
 	public String guessEntityName(Object entity) throws HibernateException {
 		checkOpen();
 		return entity.getClass().getName();
 	}
 
 	@Override
 	public EntityPersister getEntityPersister(String entityName, Object object)
 			throws HibernateException {
 		checkOpen();
 		if ( entityName == null ) {
 			return getFactory().getMetamodel().entityPersister( guessEntityName( object ) );
 		}
 		else {
 			return getFactory().getMetamodel().entityPersister( entityName ).getSubclassEntityPersister( object, getFactory() );
 		}
 	}
 
 	@Override
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		checkOpen();
 		return null;
 	}
 
 	@Override
 	public PersistenceContext getPersistenceContext() {
 		return temporaryPersistenceContext;
 	}
 
 	@Override
 	public void setAutoClear(boolean enabled) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	protected Object load(String entityName, Serializable identifier) {
 		return null;
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
 		checkOpen();
 		queryParameters.validateParameters();
 		HQLQueryPlan plan = getQueryPlan( query, false );
 		boolean success = false;
 		List results = Collections.EMPTY_LIST;
 		try {
 			results = plan.performList( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation( success );
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	public void afterOperation(boolean success) {
 		if ( !isTransactionInProgress() ) {
 			getJdbcCoordinator().afterTransaction();
 		}
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		checkOpen();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName, String alias) {
 		checkOpen();
 		return new CriteriaImpl( entityName, alias, this );
 	}
 
 	@Override
 	public Criteria createCriteria(Class persistentClass) {
 		checkOpen();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
 	@Override
 	public Criteria createCriteria(String entityName) {
 		checkOpen();
 		return new CriteriaImpl( entityName, this );
 	}
 
 	@Override
-	public ScrollableResults scroll(Criteria criteria, ScrollMode scrollMode) {
+	public ScrollableResultsImplementor scroll(Criteria criteria, ScrollMode scrollMode) {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 
 		checkOpen();
 		String entityName = criteriaImpl.getEntityOrClassName();
 		CriteriaLoader loader = new CriteriaLoader(
 				getOuterJoinLoadable( entityName ),
 				getFactory(),
 				criteriaImpl,
 				entityName,
 				getLoadQueryInfluencers()
 		);
 		return loader.scroll( this, scrollMode );
 	}
 
 	@Override
 	@SuppressWarnings({"unchecked"})
 	public List list(Criteria criteria) throws HibernateException {
 		// TODO: Is this guaranteed to always be CriteriaImpl?
 		CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
 
 		checkOpen();
 		String[] implementors = getFactory().getMetamodel().getImplementors( criteriaImpl.getEntityOrClassName() );
 		int size = implementors.length;
 
 		CriteriaLoader[] loaders = new CriteriaLoader[size];
 		for ( int i = 0; i < size; i++ ) {
 			loaders[i] = new CriteriaLoader(
 					getOuterJoinLoadable( implementors[i] ),
 					getFactory(),
 					criteriaImpl,
 					implementors[i],
 					getLoadQueryInfluencers()
 			);
 		}
 
 
 		List results = Collections.EMPTY_LIST;
 		boolean success = false;
 		try {
 			for ( int i = 0; i < size; i++ ) {
 				final List currentResults = loaders[i].list( this );
 				currentResults.addAll( results );
 				results = currentResults;
 			}
 			success = true;
 		}
 		finally {
 			afterOperation( success );
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
 		EntityPersister persister = getFactory().getMetamodel().entityPersister( entityName );
 		if ( !( persister instanceof OuterJoinLoadable ) ) {
 			throw new MappingException( "class persister is not OuterJoinLoadable: " + entityName );
 		}
 		return (OuterJoinLoadable) persister;
 	}
 
 	@Override
 	public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 			throws HibernateException {
 		checkOpen();
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 
 		boolean success = false;
 		List results;
 		try {
 			results = loader.list( this, queryParameters );
 			success = true;
 		}
 		finally {
 			afterOperation( success );
 		}
 		temporaryPersistenceContext.clear();
 		return results;
 	}
 
 	@Override
-	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
+	public ScrollableResultsImplementor scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 			throws HibernateException {
 		checkOpen();
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 		return loader.scroll( queryParameters, this );
 	}
 
 	@Override
-	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
+	public ScrollableResultsImplementor scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		checkOpen();
 		HQLQueryPlan plan = getQueryPlan( query, false );
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
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return NO_INFLUENCERS;
 	}
 
 	@Override
 	public int executeNativeUpdate(
 			NativeSQLQuerySpecification nativeSQLQuerySpecification,
 			QueryParameters queryParameters) throws HibernateException {
 		checkOpen();
 		queryParameters.validateParameters();
 		NativeSQLQueryPlan plan = getNativeQueryPlan( nativeSQLQuerySpecification );
 
 		boolean success = false;
 		int result = 0;
 		try {
 			result = plan.performExecuteUpdate( queryParameters, this );
 			success = true;
 		}
 		finally {
 			afterOperation( success );
 		}
 		temporaryPersistenceContext.clear();
 		return result;
 	}
 
 	@Override
 	public void afterTransactionBegin() {
 
 	}
 
 	@Override
 	public void beforeTransactionCompletion() {
 		flushBeforeTransactionCompletion();
 	}
 
 	@Override
 	public void afterTransactionCompletion(boolean successful, boolean delayed) {
 		if ( shouldAutoClose() && !isClosed() ) {
 			managedClose();
 		}
 	}
 
 	@Override
 	public void flushBeforeTransactionCompletion() {
 		boolean flush = false;
 		try {
 			flush = (
 					!isClosed()
 							&& !isFlushModeNever()
 							&& !JtaStatusHelper.isRollback(
 							getJtaPlatform().getCurrentStatus()
 					) );
 		}
 		catch (SystemException se) {
 			throw new HibernateException( "could not determine transaction status in beforeCompletion()", se );
 		}
 		if ( flush ) {
 			managedFlush();
 		}
 	}
 
 	private JtaPlatform getJtaPlatform() {
 		return getFactory().getServiceRegistry().getService( JtaPlatform.class );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 3c47df3a98..e65a135ef8 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,1076 +1,1077 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.loader;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.TimeUnit;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.cache.spi.FilterKey;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.dialect.pagination.NoopLimitHandler;
 import org.hibernate.engine.internal.CacheHelper;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FetchingScrollableResultsImpl;
 import org.hibernate.internal.ScrollableResultsImpl;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.transform.CacheableResultTransformer;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
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
 	protected static final CoreMessageLogger LOG = CoreLogging.messageLogger( Loader.class );
 	protected static final boolean DEBUG_ENABLED = LOG.isDebugEnabled();
 
 	private final SessionFactoryImplementor factory;
 	private volatile ColumnNameCache columnNameCache;
 
 	private final boolean referenceCachingEnabled;
 
 	public Loader(SessionFactoryImplementor factory) {
 		this.factory = factory;
 		this.referenceCachingEnabled = factory.getSessionFactoryOptions().isDirectReferenceCacheEntriesEnabled();
 	}
 
 	/**
 	 * The SQL query string to be called; implemented by all subclasses
 	 *
 	 * @return The sql command this loader should use to get its {@link ResultSet}.
 	 */
 	public abstract String getSQLString();
 
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
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
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
 	protected String preprocessSQL(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 		sql = applyLocks( sql, parameters, dialect, afterLoadActions );
 
 		// Keep this here, rather than moving to Select.  Some Dialects may need the hint to be appended to the very
 		// end or beginning of the finalized SQL statement, so wait until everything is processed.
 		if ( parameters.getQueryHints() != null && parameters.getQueryHints().size() > 0 ) {
 			sql = dialect.getQueryHintString( sql, parameters.getQueryHints() );
 		}
 
 		return getFactory().getSessionFactoryOptions().isCommentsEnabled()
 				? prependComment( sql, parameters )
 				: sql;
 	}
 
 	protected boolean shouldUseFollowOnLocking(
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) {
 		if ( dialect.useFollowOnLocking() ) {
 			// currently only one lock mode is allowed in follow-on locking
 			final LockMode lockMode = determineFollowOnLockMode( parameters.getLockOptions() );
 			final LockOptions lockOptions = new LockOptions( lockMode );
 			if ( lockOptions.getLockMode() != LockMode.UPGRADE_SKIPLOCKED ) {
 				LOG.usingFollowOnLocking();
 				lockOptions.setTimeOut( parameters.getLockOptions().getTimeOut() );
 				lockOptions.setScope( parameters.getLockOptions().getScope() );
 				afterLoadActions.add(
 						new AfterLoadAction() {
 							@Override
 							public void afterLoad(SharedSessionContractImplementor session, Object entity, Loadable persister) {
 								( (Session) session ).buildLockRequest( lockOptions ).lock(
 										persister.getEntityName(),
 										entity
 								);
 							}
 						}
 				);
 				parameters.setLockOptions( new LockOptions() );
 				return true;
 			}
 		}
 		return false;
 	}
 
 	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
 		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
 
 		if ( lockOptions.hasAliasSpecificLockModes() ) {
 			LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
 		}
 
 		return lockModeToUse;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return "/* " + comment + " */ " + sql;
 		}
 	}
 
 	/**
 	 * Execute an SQL query and attempt to instantiate instances of the class mapped by the given
 	 * persister from each row of the <tt>ResultSet</tt>. If an object is supplied, will attempt to
 	 * initialize that object. If a collection is supplied, attempt to initialize that collection.
 	 */
 	public List doQueryAndInitializeNonLazyCollections(
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies) throws HibernateException, SQLException {
 		return doQueryAndInitializeNonLazyCollections(
 				session,
 				queryParameters,
 				returnProxies,
 				null
 		);
 	}
 
 	public List doQueryAndInitializeNonLazyCollections(
 			final SharedSessionContractImplementor session,
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
 	 *
 	 * @return The loaded "row".
 	 *
 	 * @throws HibernateException
 	 */
 	public Object loadSingleRow(
 			final ResultSet resultSet,
 			final SharedSessionContractImplementor session,
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
 		catch (SQLException sqle) {
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
 			final SharedSessionContractImplementor session,
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
 				if ( !keyToRead.equals( loadedKeys[0] ) ) {
 					throw new AssertionFailure(
 							String.format(
 									"Unexpected key read for row; expected [%s]; actual [%s]",
 									keyToRead,
 									loadedKeys[0]
 							)
 					);
 				}
 				if ( result == null ) {
 					result = loaded;
 				}
 			}
 			while ( resultSet.next() &&
 					isCurrentRowForSameEntity( keyToRead, 0, resultSet, session ) );
 		}
 		catch (SQLException sqle) {
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
 
 	private boolean isCurrentRowForSameEntity(
 			final EntityKey keyToRead,
 			final int persisterIndex,
 			final ResultSet resultSet,
 			final SharedSessionContractImplementor session) throws SQLException {
 		EntityKey currentRowKey = getKeyFromResultSet(
 				persisterIndex, getEntityPersisters()[persisterIndex], null, resultSet, session
 		);
 		return keyToRead.equals( currentRowKey );
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
 	 *
 	 * @return The loaded "row".
 	 *
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsForward(
 			final ResultSet resultSet,
 			final SharedSessionContractImplementor session,
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
 			// key value upon which to perform the breaking logic.  However,
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
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not perform sequential read of results (forward)",
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
 	 *
 	 * @return The loaded "row".
 	 *
 	 * @throws HibernateException
 	 */
 	public Object loadSequentialRowsReverse(
 			final ResultSet resultSet,
 			final SharedSessionContractImplementor session,
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
 			// afterQuery the last physical row for the current logical row;
 			// thus if we are afterQuery the last physical row, this might be
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
 				// physical row afterQuery the "last processed", we need to jump
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
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not doAfterTransactionCompletion sequential read of results (forward)",
 					getSQLString()
 			);
 		}
 	}
 
 	private static EntityKey getOptionalObjectKey(QueryParameters queryParameters, SharedSessionContractImplementor session) {
 		final Object optionalObject = queryParameters.getOptionalObject();
 		final Serializable optionalId = queryParameters.getOptionalId();
 		final String optionalEntityName = queryParameters.getOptionalEntityName();
 
 		if ( optionalObject != null && optionalEntityName != null ) {
 			return session.generateEntityKey(
 					optionalId, session.getEntityPersister(
 							optionalEntityName,
 							optionalObject
 					)
 			);
 		}
 		else {
 			return null;
 		}
 
 	}
 
 	private Object getRowFromResultSet(
 			final ResultSet resultSet,
 			final SharedSessionContractImplementor session,
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
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final LockMode[] lockModesArray,
 			final EntityKey optionalObjectKey,
 			final List hydratedObjects,
 			final EntityKey[] keys,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 		final Loadable[] persisters = getEntityPersisters();
 		final int entitySpan = persisters.length;
 		extractKeysFromResultSet(
 				persisters,
 				queryParameters,
 				resultSet,
 				session,
 				keys,
 				lockModesArray,
 				hydratedObjects
 		);
 
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
 					( (HibernateProxy) proxy ).getHibernateLazyInitializer().setImplementation( entity );
 					row[i] = proxy;
 				}
 			}
 		}
 
 		applyPostLoadLocks( row, lockModesArray, session );
 
 		return forcedResultTransformer == null
 				? getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session )
 				: forcedResultTransformer.transformTuple(
 				getResultRow( row, resultSet, session ),
 				getResultRowAliases()
 		)
 				;
 	}
 
 	protected void extractKeysFromResultSet(
 			Loadable[] persisters,
 			QueryParameters queryParameters,
 			ResultSet resultSet,
 			SharedSessionContractImplementor session,
 			EntityKey[] keys,
 			LockMode[] lockModes,
 			List hydratedObjects) throws SQLException {
 		final int entitySpan = persisters.length;
 
 		final int numberOfPersistersToProcess;
 		final Serializable optionalId = queryParameters.getOptionalId();
 		if ( isSingleRowLoader() && optionalId != null ) {
 			keys[entitySpan - 1] = session.generateEntityKey( optionalId, persisters[entitySpan - 1] );
 			// skip the last persister below...
 			numberOfPersistersToProcess = entitySpan - 1;
 		}
 		else {
 			numberOfPersistersToProcess = entitySpan;
 		}
 
 		final Object[] hydratedKeyState = new Object[numberOfPersistersToProcess];
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			hydratedKeyState[i] = idType.hydrate(
 					resultSet,
 					getEntityAliases()[i].getSuffixedKeyAliases(),
 					session,
 					null
 			);
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
 							keys[targetIndex] = session.generateEntityKey( targetId, persisters[targetIndex] );
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
 							instanceNotYetLoaded(
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
 			keys[i] = resolvedId == null ? null : session.generateEntityKey( resolvedId, persisters[i] );
 		}
 	}
 
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SharedSessionContractImplementor session) {
 	}
 
 	/**
 	 * Read any collection elements contained in a single row of the result set
 	 */
 	private void readCollectionElements(Object[] row, ResultSet resultSet, SharedSessionContractImplementor session)
 			throws SQLException, HibernateException {
 
 		//TODO: make this handle multiple collection roles!
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 
 			final CollectionAliases[] descriptors = getCollectionAliases();
 			final int[] collectionOwners = getCollectionOwners();
 
 			for ( int i = 0; i < collectionPersisters.length; i++ ) {
 
 				final boolean hasCollectionOwners = collectionOwners != null &&
 						collectionOwners[i] > -1;
 				//true if this is a query and we are loading multiple instances of the same collection role
 				//otherwise this is a CollectionInitializer and we are loading up a single collection or batch
 
 				final Object owner = hasCollectionOwners ?
 						row[collectionOwners[i]] :
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
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 
 		final RowSelection selection = queryParameters.getRowSelection();
 		final int maxRows = LimitHelper.hasMaxRows( selection ) ?
 				selection.getMaxRows() :
 				Integer.MAX_VALUE;
 
 		final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 
 		final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, false, afterLoadActions, session );
 		final ResultSet rs = wrapper.getResultSet();
 		final Statement st = wrapper.getStatement();
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		try {
 			return processResultSet(
 					rs,
 					queryParameters,
 					session,
 					returnProxies,
 					forcedResultTransformer,
 					maxRows,
 					afterLoadActions
 			);
 		}
 		finally {
 			session.getJdbcCoordinator().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 		}
 
 	}
 
 	protected List processResultSet(
 			ResultSet rs,
 			QueryParameters queryParameters,
 			SharedSessionContractImplementor session,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer,
 			int maxRows,
 			List<AfterLoadAction> afterLoadActions) throws SQLException {
 		final int entitySpan = getEntityPersisters().length;
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
 		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final List results = new ArrayList();
 
 		handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
 		EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
 		LOG.trace( "Processing result set" );
 		int count;
 
 		for ( count = 0; count < maxRows && rs.next(); count++ ) {
 			if ( DEBUG_ENABLED ) {
 				LOG.debugf( "Result set row: %s", count );
 			}
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
 				subselectResultKeys.add( keys );
 				keys = new EntityKey[entitySpan]; //can't reuse in this case
 			}
 		}
 
 		LOG.tracev( "Done processing result set ({0} rows)", count );
 
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				rs,
 				session,
 				queryParameters.isReadOnly( session ),
 				afterLoadActions
 		);
 		if ( createSubselects ) {
 			createSubselects( subselectResultKeys, queryParameters, session );
 		}
 		return results;
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return false;
 	}
 
 	protected boolean hasSubselectLoadableCollections() {
 		final Loadable[] loadables = getEntityPersisters();
 		for ( Loadable loadable : loadables ) {
 			if ( loadable.hasSubselectLoadableCollections() ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private static Set[] transpose(List keys) {
 		Set[] result = new Set[( (EntityKey[]) keys.get( 0 ) ).length];
 		for ( int j = 0; j < result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( Object key : keys ) {
 				result[j].add( ( (EntityKey[]) key )[j] );
 			}
 		}
 		return result;
 	}
 
 	private void createSubselects(List keys, QueryParameters queryParameters, SharedSessionContractImplementor session) {
 		if ( keys.size() > 1 ) { //if we only returned one entity, query by key is more efficient
 
 			Set[] keySets = transpose( keys );
 
 			Map namedParameterLocMap = buildNamedParameterLocMap( queryParameters );
 
 			final Loadable[] loadables = getEntityPersisters();
 			final String[] aliases = getAliases();
 			final String subselectQueryString = SubselectFetch.createSubselectFetchQueryFragment( queryParameters );
 			for ( Object key : keys ) {
 				final EntityKey[] rowKeys = (EntityKey[]) key;
 				for ( int i = 0; i < rowKeys.length; i++ ) {
 
 					if ( rowKeys[i] != null && loadables[i].hasSubselectLoadableCollections() ) {
 
 						SubselectFetch subselectFetch = new SubselectFetch(
 								subselectQueryString,
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
 		if ( queryParameters.getNamedParameters() != null ) {
 			final Map namedParameterLocMap = new HashMap();
 			for ( String name : queryParameters.getNamedParameters().keySet() ) {
 				namedParameterLocMap.put(
 						name,
 						getNamedParameterLocs( name )
 				);
 			}
 			return namedParameterLocMap;
 		}
@@ -1671,1122 +1672,1122 @@ public abstract class Loader {
 
 	private boolean isEagerPropertyFetchEnabled(int i) {
 		boolean[] array = getEntityEagerPropertyFetches();
 		return array != null && array[i];
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
 			final SharedSessionContractImplementor session) throws SQLException, HibernateException {
 
 		final Serializable id = key.getIdentifier();
 
 		// Get the persister for the _subclass_
 		final Loadable persister = (Loadable) getFactory().getEntityPersister( instanceEntityName );
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef(
 					"Initializing object from ResultSet: %s",
 					MessageHelper.infoString(
 							persister,
 							id,
 							getFactory()
 					)
 			);
 		}
 
 		boolean fetchAllPropertiesRequested = isEagerPropertyFetchEnabled( i );
 
 		// add temp entry so that the next step is circular-reference
 		// safe - only needed because some types don't take proper
 		// advantage of two-phase-load (esp. components)
 		TwoPhaseLoad.addUninitializedEntity(
 				key,
 				object,
 				persister,
 				lockMode,
 				session
 		);
 
 		//This is not very nice (and quite slow):
 		final String[][] cols = persister == rootPersister ?
 				getEntityAliases()[i].getSuffixedPropertyAliases() :
 				getEntityAliases()[i].getSuffixedPropertyAliases( persister );
 
 		final Object[] values = persister.hydrate(
 				rs,
 				id,
 				object,
 				rootPersister,
 				cols,
 				fetchAllPropertiesRequested,
 				session
 		);
 
 		final Object rowId = persister.hasRowId() ? rs.getObject( rowIdAlias ) : null;
 
 		final AssociationType[] ownerAssociationTypes = getOwnerAssociationTypes();
 		if ( ownerAssociationTypes != null && ownerAssociationTypes[i] != null ) {
 			String ukName = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName();
 			if ( ukName != null ) {
 				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex( ukName );
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
 						persister.getEntityMode(),
 						session.getFactory()
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
 			final SharedSessionContractImplementor session) throws HibernateException, SQLException {
 
 		if ( persister.hasSubclasses() ) {
 
 			// Code to handle subclasses of topClass
 			final Object discriminatorValue = persister.getDiscriminatorType().nullSafeGet(
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
 	private void advance(final ResultSet rs, final RowSelection selection) throws SQLException {
 
 		final int firstRow = LimitHelper.getFirstRow( selection );
 		if ( firstRow != 0 ) {
 			if ( getFactory().getSessionFactoryOptions().isScrollableResultSetsEnabled() ) {
 				// we can go straight to the first required row
 				rs.absolute( firstRow );
 			}
 			else {
 				// we need to step through the rows one row at a time (slow)
 				for ( int m = 0; m < firstRow; m++ ) {
 					rs.next();
 				}
 			}
 		}
 	}
 
 	/**
 	 * Build LIMIT clause handler applicable for given selection criteria. Returns {@link NoopLimitHandler} delegate
 	 * if dialect does not support LIMIT expression or processed query does not use pagination.
 	 *
 	 * @param selection Selection criteria.
 	 *
 	 * @return LIMIT clause delegate.
 	 */
 	protected LimitHandler getLimitHandler(RowSelection selection) {
 		final LimitHandler limitHandler = getFactory().getDialect().getLimitHandler();
 		return LimitHelper.useLimit( limitHandler, selection ) ? limitHandler : NoopLimitHandler.INSTANCE;
 	}
 
 	private ScrollMode getScrollMode(
 			boolean scroll,
 			boolean hasFirstRow,
 			boolean useLimitOffSet,
 			QueryParameters queryParameters) {
 		final boolean canScroll = getFactory().getSessionFactoryOptions().isScrollableResultSetsEnabled();
 		if ( canScroll ) {
 			if ( scroll ) {
 				return queryParameters.getScrollMode();
 			}
 			if ( hasFirstRow && !useLimitOffSet ) {
 				return ScrollMode.SCROLL_INSENSITIVE;
 			}
 		}
 		return null;
 	}
 
 	/**
 	 * Process query string by applying filters, LIMIT clause, locks and comments if necessary.
 	 * Finally execute SQL statement and advance to the first row.
 	 */
 	protected SqlStatementWrapper executeQueryStatement(
 			final QueryParameters queryParameters,
 			final boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			final SharedSessionContractImplementor session) throws SQLException {
 		return executeQueryStatement( getSQLString(), queryParameters, scroll, afterLoadActions, session );
 	}
 
 	protected SqlStatementWrapper executeQueryStatement(
 			String sqlStatement,
 			QueryParameters queryParameters,
 			boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			SharedSessionContractImplementor session) throws SQLException {
 
 		// Processing query filters.
 		queryParameters.processFilters( sqlStatement, session );
 
 		// Applying LIMIT clause.
 		final LimitHandler limitHandler = getLimitHandler(
 				queryParameters.getRowSelection()
 		);
 		String sql = limitHandler.processSql( queryParameters.getFilteredSQL(), queryParameters.getRowSelection() );
 
 		// Adding locks and comments.
 		sql = preprocessSQL( sql, queryParameters, getFactory().getDialect(), afterLoadActions );
 
 		final PreparedStatement st = prepareQueryStatement( sql, queryParameters, limitHandler, scroll, session );
 		return new SqlStatementWrapper(
 				st, getResultSet(
 				st,
 				queryParameters.getRowSelection(),
 				limitHandler,
 				queryParameters.hasAutoDiscoverScalarTypes(),
 				session
 		)
 		);
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
 			String sql,
 			final QueryParameters queryParameters,
 			final LimitHandler limitHandler,
 			final boolean scroll,
 			final SharedSessionContractImplementor session) throws SQLException, HibernateException {
 		final Dialect dialect = getFactory().getDialect();
 		final RowSelection selection = queryParameters.getRowSelection();
 		final boolean useLimit = LimitHelper.useLimit( limitHandler, selection );
 		final boolean hasFirstRow = LimitHelper.hasFirstRow( selection );
 		final boolean useLimitOffset = hasFirstRow && useLimit && limitHandler.supportsLimitOffset();
 		final boolean callable = queryParameters.isCallable();
 		final ScrollMode scrollMode = getScrollMode( scroll, hasFirstRow, useLimitOffset, queryParameters );
 
 		PreparedStatement st = session.getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(
 				sql,
 				callable,
 				scrollMode
 		);
 
 		try {
 
 			int col = 1;
 			//TODO: can we limit stored procedures ?!
 			col += limitHandler.bindLimitParametersAtStartOfQuery( selection, st, col );
 
 			if ( callable ) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement) st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
 			col += limitHandler.bindLimitParametersAtEndOfQuery( selection, st, col );
 
 			limitHandler.setMaxRows( selection, st );
 
 			if ( selection != null ) {
 				if ( selection.getTimeout() != null ) {
 					st.setQueryTimeout( selection.getTimeout() );
 				}
 				if ( selection.getFetchSize() != null ) {
 					st.setFetchSize( selection.getFetchSize() );
 				}
 			}
 
 			// handle lock timeout...
 			LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 					if ( !dialect.supportsLockTimeouts() ) {
 						if ( LOG.isDebugEnabled() ) {
 							LOG.debugf(
 									"Lock timeout [%s] requested but dialect reported to not support lock timeouts",
 									lockOptions.getTimeOut()
 							);
 						}
 					}
 					else if ( dialect.isLockTimeoutParameterized() ) {
 						st.setInt( col++, lockOptions.getTimeOut() );
 					}
 				}
 			}
 
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Bound [{0}] parameters total", col );
 			}
 		}
 		catch (SQLException sqle) {
 			session.getJdbcCoordinator().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 			throw sqle;
 		}
 		catch (HibernateException he) {
 			session.getJdbcCoordinator().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 			throw he;
 		}
 
 		return st;
 	}
 
 	/**
 	 * Bind all parameter values into the prepared statement in preparation
 	 * for execution.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 *
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 *
 	 * @throws SQLException Indicates problems performing the binding.
 	 */
 	protected int bindParameterValues(
 			PreparedStatement statement,
 			QueryParameters queryParameters,
 			int startIndex,
 			SharedSessionContractImplementor session) throws SQLException {
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
 	 * beforeQuery any named parameters in the source query.
 	 *
 	 * @param statement The JDBC prepared statement
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 *
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 *
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindPositionalParameters(
 			final PreparedStatement statement,
 			final QueryParameters queryParameters,
 			final int startIndex,
 			final SharedSessionContractImplementor session) throws SQLException, HibernateException {
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
 	 *
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 *
 	 * @throws SQLException Indicates problems performing the binding.
 	 * @throws org.hibernate.HibernateException Indicates problems delegating binding to the types.
 	 */
 	protected int bindNamedParameters(
 			final PreparedStatement statement,
 			final Map<String, TypedValue> namedParams,
 			final int startIndex,
 			final SharedSessionContractImplementor session) throws SQLException, HibernateException {
 		int result = 0;
 		if ( CollectionHelper.isEmpty( namedParams ) ) {
 			return result;
 		}
 
 		for ( String name : namedParams.keySet() ) {
 			TypedValue typedValue = namedParams.get( name );
 			int columnSpan = typedValue.getType().getColumnSpan( getFactory() );
 			int[] locs = getNamedParameterLocs( name );
 			for ( int loc : locs ) {
 				if ( DEBUG_ENABLED ) {
 					LOG.debugf(
 							"bindNamedParameters() %s -> %s [%s]",
 							typedValue.getValue(),
 							name,
 							loc + startIndex
 					);
 				}
 				int start = loc * columnSpan + startIndex;
 				typedValue.getType().nullSafeSet( statement, typedValue.getValue(), start, session );
 			}
 			result += locs.length;
 		}
 		return result;
 	}
 
 	public int[] getNamedParameterLocs(String name) {
 		throw new AssertionFailure( "no named parameters" );
 	}
 
 	/**
 	 * Execute given <tt>PreparedStatement</tt>, advance to the first result and return SQL <tt>ResultSet</tt>.
 	 */
 	protected final ResultSet getResultSet(
 			final PreparedStatement st,
 			final RowSelection selection,
 			final LimitHandler limitHandler,
 			final boolean autodiscovertypes,
 			final SharedSessionContractImplementor session) throws SQLException, HibernateException {
 		try {
 			ResultSet rs = session.getJdbcCoordinator().getResultSetReturn().extract( st );
 			rs = wrapResultSetIfEnabled( rs, session );
 
 			if ( !limitHandler.supportsLimitOffset() || !LimitHelper.useLimit( limitHandler, selection ) ) {
 				advance( rs, selection );
 			}
 
 			if ( autodiscovertypes ) {
 				autoDiscoverTypes( rs );
 			}
 			return rs;
 		}
 		catch (SQLException sqle) {
 			session.getJdbcCoordinator().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 			throw sqle;
 		}
 		catch (HibernateException he) {
 			session.getJdbcCoordinator().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 			throw he;
 		}
 	}
 
 	protected void autoDiscoverTypes(ResultSet rs) {
 		throw new AssertionFailure( "Auto discover types not supported in this loader" );
 
 	}
 
 	private ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SharedSessionContractImplementor session) {
 		if ( session.getFactory().getSessionFactoryOptions().isWrapResultSetsEnabled() ) {
 			try {
 				LOG.debugf( "Wrapping result set [%s]", rs );
 				return session.getFactory()
 						.getServiceRegistry()
 						.getService( JdbcServices.class )
 						.getResultSetWrapper().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch (SQLException e) {
 				LOG.unableToWrapResultSet( e );
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(final ResultSet rs) throws SQLException {
 		final ColumnNameCache cache = columnNameCache;
 		if ( cache == null ) {
 			//there is no need for a synchronized second check, as in worst case
 			//we'll have allocated an unnecessary ColumnNameCache
 			LOG.trace( "Building columnName -> columnIndex cache" );
 			columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
 			return columnNameCache;
 		}
 		else {
 			return cache;
 		}
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 */
 	protected final List loadEntity(
 			final SharedSessionContractImplementor session,
 			final Object id,
 			final Type identifierType,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalIdentifier,
 			final EntityPersister persister,
 			LockOptions lockOptions) throws HibernateException {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Loading entity: %s", MessageHelper.infoString( persister, id, identifierType, getFactory() ) );
 		}
 
 		List result;
 		try {
 			QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( new Type[] {identifierType} );
 			qp.setPositionalParameterValues( new Object[] {id} );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( optionalEntityName );
 			qp.setOptionalId( optionalIdentifier );
 			qp.setLockOptions( lockOptions );
 			result = doQueryAndInitializeNonLazyCollections( session, qp, false );
 		}
 		catch (SQLException sqle) {
 			final Loadable[] persisters = getEntityPersisters();
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not load an entity: " +
 							MessageHelper.infoString(
 									persisters[persisters.length - 1],
 									id,
 									identifierType,
 									getFactory()
 							),
 					getSQLString()
 			);
 		}
 
 		LOG.debug( "Done entity load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 *
 	 * @param persister only needed for logging
 	 */
 	protected final List loadEntity(
 			final SharedSessionContractImplementor session,
 			final Object key,
 			final Object index,
 			final Type keyType,
 			final Type indexType,
 			final EntityPersister persister) throws HibernateException {
 		LOG.debug( "Loading collection element by index" );
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters(
 							new Type[] {keyType, indexType},
 							new Object[] {key, index}
 					),
 					false
 			);
 		}
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not load collection element by index",
 					getSQLString()
 			);
 		}
 
 		LOG.debug( "Done entity load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by wrappers that batch load entities
 	 */
 	public final List loadEntityBatch(
 			final SharedSessionContractImplementor session,
 			final Serializable[] ids,
 			final Type idType,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalId,
 			final EntityPersister persister,
 			LockOptions lockOptions) throws HibernateException {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Batch loading entity: %s", MessageHelper.infoString( persister, ids, getFactory() ) );
 		}
 
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
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not load an entity batch: " +
 							MessageHelper.infoString( getEntityPersisters()[0], ids, getFactory() ),
 					getSQLString()
 			);
 		}
 
 		LOG.debug( "Done entity batch load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that initialize collections
 	 */
 	public final void loadCollection(
 			final SharedSessionContractImplementor session,
 			final Serializable id,
 			final Type type) throws HibernateException {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() )
 			);
 		}
 
 		Serializable[] ids = new Serializable[] {id};
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( new Type[] {type}, ids, ids ),
 					true
 			);
 		}
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize a collection: " +
 							MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ),
 					getSQLString()
 			);
 		}
 
 		LOG.debug( "Done loading collection" );
 	}
 
 	/**
 	 * Called by wrappers that batch initialize collections
 	 */
 	public final void loadCollectionBatch(
 			final SharedSessionContractImplementor session,
 			final Serializable[] ids,
 			final Type type) throws HibernateException {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"Batch loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() )
 			);
 		}
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( idTypes, ids, ids ),
 					true
 			);
 		}
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize a collection batch: " +
 							MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 					getSQLString()
 			);
 		}
 
 		LOG.debug( "Done batch load" );
 	}
 
 	/**
 	 * Called by subclasses that batch initialize collections
 	 */
 	protected final void loadCollectionSubselect(
 			final SharedSessionContractImplementor session,
 			final Serializable[] ids,
 			final Object[] parameterValues,
 			final Type[] parameterTypes,
 			final Map<String, TypedValue> namedParameters,
 			final Type type) throws HibernateException {
 		final Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( parameterTypes, parameterValues, namedParameters, ids ),
 					true
 			);
 		}
 		catch (SQLException sqle) {
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
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final Set<Serializable> querySpaces,
 			final Type[] resultTypes) throws HibernateException {
 		final boolean cacheable = factory.getSessionFactoryOptions().isQueryCacheEnabled() &&
 				queryParameters.isCacheable();
 
 		if ( cacheable ) {
 			return listUsingQueryCache( session, queryParameters, querySpaces, resultTypes );
 		}
 		else {
 			return listIgnoreQueryCache( session, queryParameters );
 		}
 	}
 
 	private List listIgnoreQueryCache(SharedSessionContractImplementor session, QueryParameters queryParameters) {
 		return getResultList( doList( session, queryParameters ), queryParameters.getResultTransformer() );
 	}
 
 	private List listUsingQueryCache(
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final Set<Serializable> querySpaces,
 			final Type[] resultTypes) {
 
 		QueryCache queryCache = factory.getCache().getQueryCache( queryParameters.getCacheRegion() );
 
 		QueryKey key = generateQueryKey( session, queryParameters );
 
 		if ( querySpaces == null || querySpaces.size() == 0 ) {
 			LOG.tracev( "Unexpected querySpaces is {0}", ( querySpaces == null ? querySpaces : "empty" ) );
 		}
 		else {
 			LOG.tracev( "querySpaces is {0}", querySpaces );
 		}
 
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
 					result
 			);
 		}
 
 		ResultTransformer resolvedTransformer = resolveResultTransformer( queryParameters.getResultTransformer() );
 		if ( resolvedTransformer != null ) {
 			result = (
 					areResultSetRowsTransformedImmediately() ?
 							key.getResultTransformer().retransformResults(
 									result,
 									getResultRowAliases(),
 									queryParameters.getResultTransformer(),
 									includeInResultRow()
 							) :
 							key.getResultTransformer().untransformToTuples(
 									result
 							)
 			);
 		}
 
 		return getResultList( result, queryParameters.getResultTransformer() );
 	}
 
 	private QueryKey generateQueryKey(
 			SharedSessionContractImplementor session,
 			QueryParameters queryParameters) {
 		return QueryKey.generateQueryKey(
 				getSQLString(),
 				queryParameters,
 				FilterKey.createFilterKeys( session.getLoadQueryInfluencers().getEnabledFilters() ),
 				session,
 				createCacheableResultTransformer( queryParameters )
 		);
 	}
 
 	private CacheableResultTransformer createCacheableResultTransformer(QueryParameters queryParameters) {
 		return CacheableResultTransformer.create(
 				queryParameters.getResultTransformer(),
 				getResultRowAliases(),
 				includeInResultRow()
 		);
 	}
 
 	private List getResultFromQueryCache(
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final Set<Serializable> querySpaces,
 			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key) {
 		List result = null;
 
 		if ( session.getCacheMode().isGetEnabled() ) {
 			boolean isImmutableNaturalKeyLookup =
 					queryParameters.isNaturalKeyLookup() &&
 							resultTypes.length == 1 &&
 							resultTypes[0].isEntityType() &&
 							getEntityPersister( EntityType.class.cast( resultTypes[0] ) )
 									.getEntityMetamodel()
 									.hasImmutableNaturalId();
 
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
 			try {
 				result = queryCache.get(
 						key,
 						key.getResultTransformer().getCachedResultTypes( resultTypes ),
 						isImmutableNaturalKeyLookup,
 						querySpaces,
 						session
 				);
 			}
 			finally {
 				persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 			}
 
 			if ( factory.getStatistics().isStatisticsEnabled() ) {
 				if ( result == null ) {
 					factory.getStatistics().queryCacheMiss( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 				else {
 					factory.getStatistics().queryCacheHit( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 			}
 		}
 
 		return result;
 	}
 
 	private EntityPersister getEntityPersister(EntityType entityType) {
 		return factory.getMetamodel().entityPersister( entityType.getAssociatedEntityName() );
 	}
 
 	protected void putResultInQueryCache(
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key,
 			final List result) {
 		if ( session.getCacheMode().isPutEnabled() ) {
 			boolean put = queryCache.put(
 					key,
 					key.getResultTransformer().getCachedResultTypes( resultTypes ),
 					result,
 					queryParameters.isNaturalKeyLookup(),
 					session
 			);
 			if ( put && factory.getStatistics().isStatisticsEnabled() ) {
 				factory.getStatistics().queryCachePut( getQueryIdentifier(), queryCache.getRegion().getName() );
 			}
 		}
 	}
 
 	/**
 	 * Actually execute a query, ignoring the query cache
 	 */
 
 	protected List doList(final SharedSessionContractImplementor session, final QueryParameters queryParameters)
 			throws HibernateException {
 		return doList( session, queryParameters, null );
 	}
 
 	private List doList(
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final ResultTransformer forcedResultTransformer)
 			throws HibernateException {
 
 		final boolean stats = getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) {
 			startTime = System.nanoTime();
 		}
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections( session, queryParameters, true, forcedResultTransformer );
 		}
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not execute query",
 					getSQLString()
 			);
 		}
 
 		if ( stats ) {
 			final long endTime = System.nanoTime();
 			final long milliseconds = TimeUnit.MILLISECONDS.convert( endTime - startTime, TimeUnit.NANOSECONDS );
 			getFactory().getStatistics().queryExecuted(
 					getQueryIdentifier(),
 					result.size(),
 					milliseconds
 			);
 		}
 
 		return result;
 	}
 
 	/**
 	 * Check whether the current loader can support returning ScrollableResults.
 	 *
 	 * @throws HibernateException
 	 */
 	protected void checkScrollability() throws HibernateException {
 		// Allows various loaders (ok mainly the QueryLoader :) to check
 		// whether scrolling of their result set should be allowed.
 		//
 		// By default it is allowed.
 	}
 
 	/**
 	 * Does the result set to be scrolled contain collection fetches?
 	 *
 	 * @return True if it does, and thus needs the special fetching scroll
 	 * functionality; false otherwise.
 	 */
 	protected boolean needsFetchingScroll() {
 		return false;
 	}
 
 	/**
 	 * Return the query results, as an instance of <tt>ScrollableResults</tt>
 	 *
 	 * @param queryParameters The parameters with which the query should be executed.
 	 * @param returnTypes The expected return types of the query
 	 * @param holderInstantiator If the return values are expected to be wrapped
 	 * in a holder, this is the thing that knows how to wrap them.
 	 * @param session The session from which the scroll request originated.
 	 *
 	 * @return The ScrollableResults instance.
 	 *
 	 * @throws HibernateException Indicates an error executing the query, or constructing
 	 * the ScrollableResults.
 	 */
-	protected ScrollableResults scroll(
+	protected ScrollableResultsImplementor scroll(
 			final QueryParameters queryParameters,
 			final Type[] returnTypes,
 			final HolderInstantiator holderInstantiator,
 			final SharedSessionContractImplementor session) throws HibernateException {
 		checkScrollability();
 
 		final boolean stats = getQueryIdentifier() != null &&
 				getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) {
 			startTime = System.nanoTime();
 		}
 
 		try {
 			// Don't use Collections#emptyList() here -- follow on locking potentially adds AfterLoadActions,
 			// so the list cannot be immutable.
 			final SqlStatementWrapper wrapper = executeQueryStatement(
 					queryParameters,
 					true,
 					new ArrayList<AfterLoadAction>(),
 					session
 			);
 			final ResultSet rs = wrapper.getResultSet();
 			final PreparedStatement st = (PreparedStatement) wrapper.getStatement();
 
 			if ( stats ) {
 				final long endTime = System.nanoTime();
 				final long milliseconds = TimeUnit.MILLISECONDS.convert( endTime - startTime, TimeUnit.NANOSECONDS );
 				getFactory().getStatistics().queryExecuted(
 						getQueryIdentifier(),
 						0,
 						milliseconds
 				);
 			}
 
 			if ( needsFetchingScroll() ) {
 				return new FetchingScrollableResultsImpl(
 						rs,
 						st,
 						session,
 						this,
 						queryParameters,
 						returnTypes,
 						holderInstantiator
 				);
 			}
 			else {
 				return new ScrollableResultsImpl(
 						rs,
 						st,
 						session,
 						this,
 						queryParameters,
 						returnTypes,
 						holderInstantiator
 				);
 			}
 
 		}
 		catch (SQLException sqle) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not execute query using scroll",
 					getSQLString()
 			);
 		}
 
 	}
 
 	/**
 	 * Calculate and cache select-clause suffixes. Must be
 	 * called by subclasses afterQuery instantiation.
 	 */
 	protected void postInstantiate() {
 	}
 
 	/**
 	 * Get the result set descriptor
 	 */
 	protected abstract EntityAliases[] getEntityAliases();
 
 	protected abstract CollectionAliases[] getCollectionAliases();
 
 	/**
 	 * Identifies the query for statistics reporting, if null,
 	 * no statistics will be reported
 	 */
 	protected String getQueryIdentifier() {
 		return null;
 	}
 
 	public final SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getSQLString() + ')';
 	}
 
 	/**
 	 * Wrapper class for {@link Statement} and associated {@link ResultSet}.
 	 */
 	protected static class SqlStatementWrapper {
 		private final Statement statement;
 		private final ResultSet resultSet;
 
 		private SqlStatementWrapper(Statement statement, ResultSet resultSet) {
 			this.resultSet = resultSet;
 			this.statement = statement;
 		}
 
 		public ResultSet getResultSet() {
 			return resultSet;
 		}
 
 		public Statement getStatement() {
 			return statement;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
index 3a31278638..c984248b02 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
@@ -1,294 +1,295 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.loader.criteria;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.OuterJoinLoader;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 /**
  * A <tt>Loader</tt> for <tt>Criteria</tt> queries. Note that criteria queries are
  * more like multi-object <tt>load()</tt>s than like HQL queries.
  *
  * @author Gavin King
  */
 public class CriteriaLoader extends OuterJoinLoader {
 
 	//TODO: this class depends directly upon CriteriaImpl, 
 	//      in the impl package ... add a CriteriaImplementor 
 	//      interface
 
 	//NOTE: unlike all other Loaders, this one is NOT
 	//      multithreaded, or cacheable!!
 
 	private final CriteriaQueryTranslator translator;
 	private final Set<Serializable> querySpaces;
 	private final Type[] resultTypes;
 	//the user visible aliases, which are unknown to the superclass,
 	//these are not the actual "physical" SQL aliases
 	private final String[] userAliases;
 	private final boolean[] includeInResultRow;
 	private final int resultRowLength;
 
 	public CriteriaLoader(
 			final OuterJoinLoadable persister,
 			final SessionFactoryImplementor factory,
 			final CriteriaImpl criteria,
 			final String rootEntityName,
 			final LoadQueryInfluencers loadQueryInfluencers) throws HibernateException {
 		super( factory, loadQueryInfluencers );
 
 		translator = new CriteriaQueryTranslator(
 				factory,
 				criteria,
 				rootEntityName,
 				CriteriaQueryTranslator.ROOT_SQL_ALIAS
 		);
 
 		querySpaces = translator.getQuerySpaces();
 
 		CriteriaJoinWalker walker = new CriteriaJoinWalker(
 				persister,
 				translator,
 				factory,
 				criteria,
 				rootEntityName,
 				loadQueryInfluencers
 		);
 
 		initFromWalker( walker );
 
 		userAliases = walker.getUserAliases();
 		resultTypes = walker.getResultTypes();
 		includeInResultRow = walker.includeInResultRow();
 		resultRowLength = ArrayHelper.countTrue( includeInResultRow );
 
 		postInstantiate();
 
 	}
 
-	public ScrollableResults scroll(SharedSessionContractImplementor session, ScrollMode scrollMode)
+	public ScrollableResultsImplementor scroll(SharedSessionContractImplementor session, ScrollMode scrollMode)
 			throws HibernateException {
 		QueryParameters qp = translator.getQueryParameters();
 		qp.setScrollMode( scrollMode );
 		return scroll( qp, resultTypes, null, session );
 	}
 
 	public List list(SharedSessionContractImplementor session)
 			throws HibernateException {
 		return list( session, translator.getQueryParameters(), querySpaces, resultTypes );
 
 	}
 
 	@Override
 	protected String[] getResultRowAliases() {
 		return userAliases;
 	}
 
 	@Override
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return translator.getRootCriteria().getResultTransformer();
 	}
 
 	@Override
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return true;
 	}
 
 	@Override
 	protected boolean[] includeInResultRow() {
 		return includeInResultRow;
 	}
 
 	@Override
 	protected Object getResultColumnOrRow(
 			Object[] row,
 			ResultTransformer transformer,
 			ResultSet rs,
 			SharedSessionContractImplementor session)
 			throws SQLException, HibernateException {
 		return resolveResultTransformer( transformer ).transformTuple(
 				getResultRow( row, rs, session ),
 				getResultRowAliases()
 		);
 	}
 
 	@Override
 	protected Object[] getResultRow(Object[] row, ResultSet rs, SharedSessionContractImplementor session)
 			throws SQLException, HibernateException {
 		final Object[] result;
 		if ( translator.hasProjection() ) {
 			Type[] types = translator.getProjectedTypes();
 			result = new Object[types.length];
 			String[] columnAliases = translator.getProjectedColumnAliases();
 			for ( int i = 0, pos = 0; i < result.length; i++ ) {
 				int numColumns = types[i].getColumnSpan( session.getFactory() );
 				if ( numColumns > 1 ) {
 					String[] typeColumnAliases = ArrayHelper.slice( columnAliases, pos, numColumns );
 					result[i] = types[i].nullSafeGet( rs, typeColumnAliases, session, null );
 				}
 				else {
 					result[i] = types[i].nullSafeGet( rs, columnAliases[pos], session, null );
 				}
 				pos += numColumns;
 			}
 		}
 		else {
 			result = toResultRow( row );
 		}
 		return result;
 	}
 
 	private Object[] toResultRow(Object[] row) {
 		if ( resultRowLength == row.length ) {
 			return row;
 		}
 		else {
 			Object[] result = new Object[resultRowLength];
 			int j = 0;
 			for ( int i = 0; i < row.length; i++ ) {
 				if ( includeInResultRow[i] ) {
 					result[j++] = row[i];
 				}
 			}
 			return result;
 		}
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	@Override
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws QueryException {
 		final LockOptions lockOptions = parameters.getLockOptions();
 
 		if ( lockOptions == null ||
 				( lockOptions.getLockMode() == LockMode.NONE && ( lockOptions.getAliasLockCount() == 0
 						|| ( lockOptions.getAliasLockCount() == 1 && lockOptions
 						.getAliasSpecificLockMode( "this_" ) == LockMode.NONE )
 				) ) ) {
 			return sql;
 		}
 
 		if ( dialect.useFollowOnLocking() ) {
 			final LockMode lockMode = determineFollowOnLockMode( lockOptions );
 			if ( lockMode != LockMode.UPGRADE_SKIPLOCKED ) {
 				// Dialect prefers to perform locking in a separate step
 				LOG.usingFollowOnLocking();
 
 				final LockOptions lockOptionsToUse = new LockOptions( lockMode );
 				lockOptionsToUse.setTimeOut( lockOptions.getTimeOut() );
 				lockOptionsToUse.setScope( lockOptions.getScope() );
 
 				afterLoadActions.add(
 						new AfterLoadAction() {
 							@Override
 							public void afterLoad(SharedSessionContractImplementor session, Object entity, Loadable persister) {
 								( (Session) session ).buildLockRequest( lockOptionsToUse )
 										.lock( persister.getEntityName(), entity );
 							}
 						}
 				);
 				parameters.setLockOptions( new LockOptions() );
 				return sql;
 			}
 		}
 		final LockOptions locks = new LockOptions( lockOptions.getLockMode() );
 		locks.setScope( lockOptions.getScope() );
 		locks.setTimeOut( lockOptions.getTimeOut() );
 
 		final Map keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap() : null;
 		final String[] drivingSqlAliases = getAliases();
 		for ( int i = 0; i < drivingSqlAliases.length; i++ ) {
 			final LockMode lockMode = lockOptions.getAliasSpecificLockMode( drivingSqlAliases[i] );
 			if ( lockMode != null ) {
 				final Lockable drivingPersister = (Lockable) getEntityPersisters()[i];
 				final String rootSqlAlias = drivingPersister.getRootTableAlias( drivingSqlAliases[i] );
 				locks.setAliasSpecificLockMode( rootSqlAlias, lockMode );
 				if ( keyColumnNames != null ) {
 					keyColumnNames.put( rootSqlAlias, drivingPersister.getRootTableIdentifierColumnNames() );
 				}
 			}
 		}
 		return dialect.applyLocksToSql( sql, locks, keyColumnNames );
 	}
 
 
 	@Override
 	protected LockMode determineFollowOnLockMode(LockOptions lockOptions) {
 		final LockMode lockModeToUse = lockOptions.findGreatestLockMode();
 
 		if ( lockOptions.getAliasLockCount() > 1 ) {
 			// > 1 here because criteria always uses alias map for the root lock mode (under 'this_')
 			LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
 		}
 
 		return lockModeToUse;
 	}
 
 	@Override
 	protected LockMode[] getLockModes(LockOptions lockOptions) {
 		final String[] entityAliases = getAliases();
 		if ( entityAliases == null ) {
 			return null;
 		}
 		final int size = entityAliases.length;
 		LockMode[] lockModesArray = new LockMode[size];
 		for ( int i = 0; i < size; i++ ) {
 			LockMode lockMode = lockOptions.getAliasSpecificLockMode( entityAliases[i] );
 			lockModesArray[i] = lockMode == null ? lockOptions.getLockMode() : lockMode;
 		}
 		return lockModesArray;
 	}
 
 	@Override
 	protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 
 	@Override
 	protected List getResultList(List results, ResultTransformer resultTransformer) {
 		return resolveResultTransformer( resultTransformer ).transformList( results );
 	}
 
 	@Override
 	protected String getQueryIdentifier() {
 		return "[CRITERIA] " + getSQLString();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
index 4df3fd732b..026174719a 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
@@ -1,546 +1,547 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.loader.custom;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.Session;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.Loader;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 
 /**
  * Extension point for loaders which use a SQL result set with "unexpected" column aliases.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class CustomLoader extends Loader {
 
 	// Currently *not* cachable if autodiscover types is in effect (e.g. "select * ...")
 
 	private final String sql;
 	private final Set<Serializable> querySpaces = new HashSet<Serializable>();
 	private final Map namedParameterBindPoints;
 
 	private final Queryable[] entityPersisters;
 	private final int[] entiytOwners;
 	private final EntityAliases[] entityAliases;
 
 	private final QueryableCollection[] collectionPersisters;
 	private final int[] collectionOwners;
 	private final CollectionAliases[] collectionAliases;
 
 	private final LockMode[] lockModes;
 
 	private boolean[] includeInResultRow;
 
 	//	private final String[] sqlAliases;
 //	private final String[] sqlAliasSuffixes;
 	private final ResultRowProcessor rowProcessor;
 
 	// this is only needed (afaict) for processing results from the query cache;
 	// however, this cannot possibly work in the case of discovered types...
 	private Type[] resultTypes;
 
 	// this is only needed (afaict) for ResultTransformer processing...
 	private String[] transformerAliases;
 
 	public CustomLoader(CustomQuery customQuery, SessionFactoryImplementor factory) {
 		super( factory );
 
 		this.sql = customQuery.getSQL();
 		this.querySpaces.addAll( customQuery.getQuerySpaces() );
 		this.namedParameterBindPoints = customQuery.getNamedParameterBindPoints();
 
 		List<Queryable> entityPersisters = new ArrayList<Queryable>();
 		List<Integer> entityOwners = new ArrayList<Integer>();
 		List<EntityAliases> entityAliases = new ArrayList<EntityAliases>();
 
 		List<QueryableCollection> collectionPersisters = new ArrayList<QueryableCollection>();
 		List<Integer> collectionOwners = new ArrayList<Integer>();
 		List<CollectionAliases> collectionAliases = new ArrayList<CollectionAliases>();
 
 		List<LockMode> lockModes = new ArrayList<LockMode>();
 		List<ResultColumnProcessor> resultColumnProcessors = new ArrayList<ResultColumnProcessor>();
 		List<Return> nonScalarReturnList = new ArrayList<Return>();
 		List<Type> resultTypes = new ArrayList<Type>();
 		List<String> specifiedAliases = new ArrayList<String>();
 
 		int returnableCounter = 0;
 		boolean hasScalars = false;
 
 		List<Boolean> includeInResultRowList = new ArrayList<Boolean>();
 
 		for ( Return rtn : customQuery.getCustomQueryReturns() ) {
 			if ( rtn instanceof ScalarReturn ) {
 				ScalarReturn scalarRtn = (ScalarReturn) rtn;
 				resultTypes.add( scalarRtn.getType() );
 				specifiedAliases.add( scalarRtn.getColumnAlias() );
 				resultColumnProcessors.add(
 						new ScalarResultColumnProcessor(
 								StringHelper.unquote( scalarRtn.getColumnAlias(), factory.getDialect() ),
 								scalarRtn.getType()
 						)
 				);
 				includeInResultRowList.add( true );
 				hasScalars = true;
 			}
 			else if ( ConstructorReturn.class.isInstance( rtn ) ) {
 				final ConstructorReturn constructorReturn = (ConstructorReturn) rtn;
 				resultTypes.add( null ); // this bit makes me nervous
 				includeInResultRowList.add( true );
 				hasScalars = true;
 
 				ScalarResultColumnProcessor[] scalarProcessors = new ScalarResultColumnProcessor[constructorReturn.getScalars().length];
 				int i = 0;
 				for ( ScalarReturn scalarReturn : constructorReturn.getScalars() ) {
 					scalarProcessors[i++] = new ScalarResultColumnProcessor(
 							StringHelper.unquote( scalarReturn.getColumnAlias(), factory.getDialect() ),
 							scalarReturn.getType()
 					);
 				}
 
 				resultColumnProcessors.add(
 						new ConstructorResultColumnProcessor( constructorReturn.getTargetClass(), scalarProcessors )
 				);
 			}
 			else if ( rtn instanceof RootReturn ) {
 				RootReturn rootRtn = (RootReturn) rtn;
 				Queryable persister = (Queryable) factory.getEntityPersister( rootRtn.getEntityName() );
 				entityPersisters.add( persister );
 				lockModes.add( ( rootRtn.getLockMode() ) );
 				resultColumnProcessors.add( new NonScalarResultColumnProcessor( returnableCounter++ ) );
 				nonScalarReturnList.add( rtn );
 				entityOwners.add( -1 );
 				resultTypes.add( persister.getType() );
 				specifiedAliases.add( rootRtn.getAlias() );
 				entityAliases.add( rootRtn.getEntityAliases() );
 				ArrayHelper.addAll( querySpaces, persister.getQuerySpaces() );
 				includeInResultRowList.add( true );
 			}
 			else if ( rtn instanceof CollectionReturn ) {
 				CollectionReturn collRtn = (CollectionReturn) rtn;
 				String role = collRtn.getOwnerEntityName() + "." + collRtn.getOwnerProperty();
 				QueryableCollection persister = (QueryableCollection) factory.getCollectionPersister( role );
 				collectionPersisters.add( persister );
 				lockModes.add( collRtn.getLockMode() );
 				resultColumnProcessors.add( new NonScalarResultColumnProcessor( returnableCounter++ ) );
 				nonScalarReturnList.add( rtn );
 				collectionOwners.add( -1 );
 				resultTypes.add( persister.getType() );
 				specifiedAliases.add( collRtn.getAlias() );
 				collectionAliases.add( collRtn.getCollectionAliases() );
 				// determine if the collection elements are entities...
 				Type elementType = persister.getElementType();
 				if ( elementType.isEntityType() ) {
 					Queryable elementPersister = (Queryable) ( (EntityType) elementType ).getAssociatedJoinable( factory );
 					entityPersisters.add( elementPersister );
 					entityOwners.add( -1 );
 					entityAliases.add( collRtn.getElementEntityAliases() );
 					ArrayHelper.addAll( querySpaces, elementPersister.getQuerySpaces() );
 				}
 				includeInResultRowList.add( true );
 			}
 			else if ( rtn instanceof EntityFetchReturn ) {
 				EntityFetchReturn fetchRtn = (EntityFetchReturn) rtn;
 				NonScalarReturn ownerDescriptor = fetchRtn.getOwner();
 				int ownerIndex = nonScalarReturnList.indexOf( ownerDescriptor );
 				entityOwners.add( ownerIndex );
 				lockModes.add( fetchRtn.getLockMode() );
 				Queryable ownerPersister = determineAppropriateOwnerPersister( ownerDescriptor );
 				EntityType fetchedType = (EntityType) ownerPersister.getPropertyType( fetchRtn.getOwnerProperty() );
 				String entityName = fetchedType.getAssociatedEntityName( getFactory() );
 				Queryable persister = (Queryable) factory.getEntityPersister( entityName );
 				entityPersisters.add( persister );
 				nonScalarReturnList.add( rtn );
 				specifiedAliases.add( fetchRtn.getAlias() );
 				entityAliases.add( fetchRtn.getEntityAliases() );
 				ArrayHelper.addAll( querySpaces, persister.getQuerySpaces() );
 				includeInResultRowList.add( false );
 			}
 			else if ( rtn instanceof CollectionFetchReturn ) {
 				CollectionFetchReturn fetchRtn = (CollectionFetchReturn) rtn;
 				NonScalarReturn ownerDescriptor = fetchRtn.getOwner();
 				int ownerIndex = nonScalarReturnList.indexOf( ownerDescriptor );
 				collectionOwners.add( ownerIndex );
 				lockModes.add( fetchRtn.getLockMode() );
 				Queryable ownerPersister = determineAppropriateOwnerPersister( ownerDescriptor );
 				String role = ownerPersister.getEntityName() + '.' + fetchRtn.getOwnerProperty();
 				QueryableCollection persister = (QueryableCollection) factory.getCollectionPersister( role );
 				collectionPersisters.add( persister );
 				nonScalarReturnList.add( rtn );
 				specifiedAliases.add( fetchRtn.getAlias() );
 				collectionAliases.add( fetchRtn.getCollectionAliases() );
 				// determine if the collection elements are entities...
 				Type elementType = persister.getElementType();
 				if ( elementType.isEntityType() ) {
 					Queryable elementPersister = (Queryable) ( (EntityType) elementType ).getAssociatedJoinable( factory );
 					entityPersisters.add( elementPersister );
 					entityOwners.add( ownerIndex );
 					entityAliases.add( fetchRtn.getElementEntityAliases() );
 					ArrayHelper.addAll( querySpaces, elementPersister.getQuerySpaces() );
 				}
 				includeInResultRowList.add( false );
 			}
 			else {
 				throw new HibernateException( "unexpected custom query return type : " + rtn.getClass().getName() );
 			}
 		}
 
 		this.entityPersisters = new Queryable[entityPersisters.size()];
 		for ( int i = 0; i < entityPersisters.size(); i++ ) {
 			this.entityPersisters[i] = entityPersisters.get( i );
 		}
 		this.entiytOwners = ArrayHelper.toIntArray( entityOwners );
 		this.entityAliases = new EntityAliases[entityAliases.size()];
 		for ( int i = 0; i < entityAliases.size(); i++ ) {
 			this.entityAliases[i] = entityAliases.get( i );
 		}
 
 		this.collectionPersisters = new QueryableCollection[collectionPersisters.size()];
 		for ( int i = 0; i < collectionPersisters.size(); i++ ) {
 			this.collectionPersisters[i] = collectionPersisters.get( i );
 		}
 		this.collectionOwners = ArrayHelper.toIntArray( collectionOwners );
 		this.collectionAliases = new CollectionAliases[collectionAliases.size()];
 		for ( int i = 0; i < collectionAliases.size(); i++ ) {
 			this.collectionAliases[i] = collectionAliases.get( i );
 		}
 
 		this.lockModes = new LockMode[lockModes.size()];
 		for ( int i = 0; i < lockModes.size(); i++ ) {
 			this.lockModes[i] = lockModes.get( i );
 		}
 
 		this.resultTypes = ArrayHelper.toTypeArray( resultTypes );
 		this.transformerAliases = ArrayHelper.toStringArray( specifiedAliases );
 
 		this.rowProcessor = new ResultRowProcessor(
 				hasScalars,
 				resultColumnProcessors.toArray( new ResultColumnProcessor[resultColumnProcessors.size()] )
 		);
 
 		this.includeInResultRow = ArrayHelper.toBooleanArray( includeInResultRowList );
 	}
 
 	private Queryable determineAppropriateOwnerPersister(NonScalarReturn ownerDescriptor) {
 		String entityName = null;
 		if ( ownerDescriptor instanceof RootReturn ) {
 			entityName = ( (RootReturn) ownerDescriptor ).getEntityName();
 		}
 		else if ( ownerDescriptor instanceof CollectionReturn ) {
 			CollectionReturn collRtn = (CollectionReturn) ownerDescriptor;
 			String role = collRtn.getOwnerEntityName() + "." + collRtn.getOwnerProperty();
 			CollectionPersister persister = getFactory().getCollectionPersister( role );
 			EntityType ownerType = (EntityType) persister.getElementType();
 			entityName = ownerType.getAssociatedEntityName( getFactory() );
 		}
 		else if ( ownerDescriptor instanceof FetchReturn ) {
 			FetchReturn fetchRtn = (FetchReturn) ownerDescriptor;
 			Queryable persister = determineAppropriateOwnerPersister( fetchRtn.getOwner() );
 			Type ownerType = persister.getPropertyType( fetchRtn.getOwnerProperty() );
 			if ( ownerType.isEntityType() ) {
 				entityName = ( (EntityType) ownerType ).getAssociatedEntityName( getFactory() );
 			}
 			else if ( ownerType.isCollectionType() ) {
 				Type ownerCollectionElementType = ( (CollectionType) ownerType ).getElementType( getFactory() );
 				if ( ownerCollectionElementType.isEntityType() ) {
 					entityName = ( (EntityType) ownerCollectionElementType ).getAssociatedEntityName( getFactory() );
 				}
 			}
 		}
 
 		if ( entityName == null ) {
 			throw new HibernateException( "Could not determine fetch owner : " + ownerDescriptor );
 		}
 
 		return (Queryable) getFactory().getEntityPersister( entityName );
 	}
 
 	@Override
 	protected String getQueryIdentifier() {
 		return sql;
 	}
 
 	@Override
 	public String getSQLString() {
 		return sql;
 	}
 
 	public Set getQuerySpaces() {
 		return querySpaces;
 	}
 
 	@Override
 	protected LockMode[] getLockModes(LockOptions lockOptions) {
 		return lockModes;
 	}
 
 	@Override
 	protected Loadable[] getEntityPersisters() {
 		return entityPersisters;
 	}
 
 	@Override
 	protected CollectionPersister[] getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	@Override
 	protected int[] getCollectionOwners() {
 		return collectionOwners;
 	}
 
 	@Override
 	protected int[] getOwners() {
 		return entiytOwners;
 	}
 
 	public List list(SharedSessionContractImplementor session, QueryParameters queryParameters) throws HibernateException {
 		return list( session, queryParameters, querySpaces, resultTypes );
 	}
 
 	@Override
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws QueryException {
 		final LockOptions lockOptions = parameters.getLockOptions();
 		if ( lockOptions == null ||
 				( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sql;
 		}
 
 		// user is request locking, lets see if we can apply locking directly to the SQL...
 
 		// 		some dialects wont allow locking with paging...
 		afterLoadActions.add(
 				new AfterLoadAction() {
 					private final LockOptions originalLockOptions = lockOptions.makeCopy();
 
 					@Override
 					public void afterLoad(SharedSessionContractImplementor session, Object entity, Loadable persister) {
 						( (Session) session ).buildLockRequest( originalLockOptions ).lock(
 								persister.getEntityName(),
 								entity
 						);
 					}
 				}
 		);
 		parameters.getLockOptions().setLockMode( LockMode.READ );
 
 		return sql;
 	}
 
-	public ScrollableResults scroll(final QueryParameters queryParameters, final SharedSessionContractImplementor session)
+	public ScrollableResultsImplementor scroll(final QueryParameters queryParameters, final SharedSessionContractImplementor session)
 			throws HibernateException {
 		return scroll(
 				queryParameters,
 				resultTypes,
 				getHolderInstantiator( queryParameters.getResultTransformer(), getReturnAliasesForTransformer() ),
 				session
 		);
 	}
 
 	static private HolderInstantiator getHolderInstantiator(
 			ResultTransformer resultTransformer,
 			String[] queryReturnAliases) {
 		if ( resultTransformer == null ) {
 			return HolderInstantiator.NOOP_INSTANTIATOR;
 		}
 		else {
 			return new HolderInstantiator( resultTransformer, queryReturnAliases );
 		}
 	}
 
 	@Override
 	protected String[] getResultRowAliases() {
 		return transformerAliases;
 	}
 
 	@Override
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return HolderInstantiator.resolveResultTransformer( null, resultTransformer );
 	}
 
 	@Override
 	protected boolean[] includeInResultRow() {
 		return includeInResultRow;
 	}
 
 	@Override
 	protected Object getResultColumnOrRow(
 			Object[] row,
 			ResultTransformer transformer,
 			ResultSet rs,
 			SharedSessionContractImplementor session) throws SQLException, HibernateException {
 		return rowProcessor.buildResultRow( row, rs, transformer != null, session );
 	}
 
 	@Override
 	protected Object[] getResultRow(Object[] row, ResultSet rs, SharedSessionContractImplementor session)
 			throws SQLException, HibernateException {
 		return rowProcessor.buildResultRow( row, rs, session );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		// meant to handle dynamic instantiation queries...(Copy from QueryLoader)
 		HolderInstantiator holderInstantiator = HolderInstantiator.getHolderInstantiator(
 				null,
 				resultTransformer,
 				getReturnAliasesForTransformer()
 		);
 		if ( holderInstantiator.isRequired() ) {
 			for ( int i = 0; i < results.size(); i++ ) {
 				Object[] row = (Object[]) results.get( i );
 				Object result = holderInstantiator.instantiate( row );
 				results.set( i, result );
 			}
 
 			return resultTransformer.transformList( results );
 		}
 		else {
 			return results;
 		}
 	}
 
 	private String[] getReturnAliasesForTransformer() {
 		return transformerAliases;
 	}
 
 	@Override
 	protected EntityAliases[] getEntityAliases() {
 		return entityAliases;
 	}
 
 	@Override
 	protected CollectionAliases[] getCollectionAliases() {
 		return collectionAliases;
 	}
 
 	@Override
 	public int[] getNamedParameterLocs(String name) throws QueryException {
 		Object loc = namedParameterBindPoints.get( name );
 		if ( loc == null ) {
 			throw new QueryException(
 					"Named parameter does not appear in Query: " + name,
 					sql
 			);
 		}
 		if ( loc instanceof Integer ) {
 			return new int[] {(Integer) loc};
 		}
 		else {
 			return ArrayHelper.toIntArray( (List) loc );
 		}
 	}
 
 
 	@Override
 	protected void autoDiscoverTypes(ResultSet rs) {
 		try {
 			JdbcResultMetadata metadata = new JdbcResultMetadata( getFactory(), rs );
 			rowProcessor.prepareForAutoDiscovery( metadata );
 
 			List<String> aliases = new ArrayList<String>();
 			List<Type> types = new ArrayList<Type>();
 			for ( ResultColumnProcessor resultProcessor : rowProcessor.getColumnProcessors() ) {
 				resultProcessor.performDiscovery( metadata, types, aliases );
 			}
 
 			validateAliases( aliases );
 
 			resultTypes = ArrayHelper.toTypeArray( types );
 			transformerAliases = ArrayHelper.toStringArray( aliases );
 		}
 		catch (SQLException e) {
 			throw new HibernateException( "Exception while trying to autodiscover types.", e );
 		}
 	}
 
 	private void validateAliases(List<String> aliases) {
 		// lets make sure we did not end up with duplicate aliases.  this can occur when the user supplied query
 		// did not rename same-named columns.  e.g.:
 		//		select u.username, u2.username from t_user u, t_user u2 ...
 		//
 		// the above will lead to an unworkable situation in most cases (the difference is how the driver/db
 		// handle this situation.  But if the 'aliases' variable contains duplicate names, then we have that
 		// troublesome condition, so lets throw an error.  See HHH-5992
 		final HashSet<String> aliasesSet = new HashSet<String>();
 		for ( String alias : aliases ) {
 			validateAlias( alias );
 			boolean alreadyExisted = !aliasesSet.add( alias );
 			if ( alreadyExisted ) {
 				throw new NonUniqueDiscoveredSqlAliasException(
 						"Encountered a duplicated sql alias [" + alias + "] during auto-discovery of a native-sql query"
 				);
 			}
 		}
 	}
 
 	@SuppressWarnings("UnusedParameters")
 	protected void validateAlias(String alias) {
 	}
 
 	/**
 	 * {@link #resultTypes} can be overridden by {@link #autoDiscoverTypes(ResultSet)},
 	 * *afterQuery* {@link #list(SharedSessionContractImplementor, QueryParameters)} has already been called.  It's a bit of a
 	 * chicken-and-the-egg issue since {@link #autoDiscoverTypes(ResultSet)} needs the {@link ResultSet}.
 	 * <p/>
 	 * As a hacky workaround, override
 	 * {@link #putResultInQueryCache(SharedSessionContractImplementor, QueryParameters, Type[], QueryCache, QueryKey, List)} here
 	 * and provide the {@link #resultTypes}.
 	 *
 	 * see HHH-3051
 	 */
 	@Override
 	protected void putResultInQueryCache(
 			final SharedSessionContractImplementor session,
 			final QueryParameters queryParameters,
 			final Type[] resultTypes,
 			final QueryCache queryCache,
 			final QueryKey key,
 			final List result) {
 		super.putResultInQueryCache( session, queryParameters, this.resultTypes, queryCache, key, result );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
index 884589b83c..e83adbace0 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
@@ -1,631 +1,632 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.loader.hql;
 
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.TimeUnit;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollableResults;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
 import org.hibernate.hql.internal.ast.tree.AggregatedSelectExpression;
 import org.hibernate.hql.internal.ast.tree.FromElement;
 import org.hibernate.hql.internal.ast.tree.QueryNode;
 import org.hibernate.hql.internal.ast.tree.SelectClause;
 import org.hibernate.internal.IteratorImpl;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.BasicLoader;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.persister.entity.Queryable;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * A delegate that implements the Loader part of QueryTranslator.
  *
  * @author josh
  */
 public class QueryLoader extends BasicLoader {
 
 	/**
 	 * The query translator that is delegating to this object.
 	 */
 	private QueryTranslatorImpl queryTranslator;
 
 	private Queryable[] entityPersisters;
 	private String[] entityAliases;
 	private String[] sqlAliases;
 	private String[] sqlAliasSuffixes;
 	private boolean[] includeInSelect;
 
 	private String[] collectionSuffixes;
 
 	private boolean hasScalars;
 	private String[][] scalarColumnNames;
 	//private Type[] sqlResultTypes;
 	private Type[] queryReturnTypes;
 
 	private final Map<String, String> sqlAliasByEntityAlias = new HashMap<String, String>( 8 );
 
 	private EntityType[] ownerAssociationTypes;
 	private int[] owners;
 	private boolean[] entityEagerPropertyFetches;
 
 	private int[] collectionOwners;
 	private QueryableCollection[] collectionPersisters;
 
 	private int selectLength;
 
 	private AggregatedSelectExpression aggregatedSelectExpression;
 	private String[] queryReturnAliases;
 
 	private LockMode[] defaultLockModes;
 
 
 	/**
 	 * Creates a new Loader implementation.
 	 *
 	 * @param queryTranslator The query translator that is the delegator.
 	 * @param factory The factory from which this loader is being created.
 	 * @param selectClause The AST representing the select clause for loading.
 	 */
 	public QueryLoader(
 			final QueryTranslatorImpl queryTranslator,
 			final SessionFactoryImplementor factory,
 			final SelectClause selectClause) {
 		super( factory );
 		this.queryTranslator = queryTranslator;
 		initialize( selectClause );
 		postInstantiate();
 	}
 
 	private void initialize(SelectClause selectClause) {
 
 		List fromElementList = selectClause.getFromElementsForLoad();
 
 		hasScalars = selectClause.isScalarSelect();
 		scalarColumnNames = selectClause.getColumnNames();
 		//sqlResultTypes = selectClause.getSqlResultTypes();
 		queryReturnTypes = selectClause.getQueryReturnTypes();
 
 		aggregatedSelectExpression = selectClause.getAggregatedSelectExpression();
 		queryReturnAliases = selectClause.getQueryReturnAliases();
 
 		List collectionFromElements = selectClause.getCollectionFromElements();
 		if ( collectionFromElements != null && collectionFromElements.size() != 0 ) {
 			int length = collectionFromElements.size();
 			collectionPersisters = new QueryableCollection[length];
 			collectionOwners = new int[length];
 			collectionSuffixes = new String[length];
 			for ( int i = 0; i < length; i++ ) {
 				FromElement collectionFromElement = (FromElement) collectionFromElements.get( i );
 				collectionPersisters[i] = collectionFromElement.getQueryableCollection();
 				collectionOwners[i] = fromElementList.indexOf( collectionFromElement.getOrigin() );
 //				collectionSuffixes[i] = collectionFromElement.getColumnAliasSuffix();
 //				collectionSuffixes[i] = Integer.toString( i ) + "_";
 				collectionSuffixes[i] = collectionFromElement.getCollectionSuffix();
 			}
 		}
 
 		int size = fromElementList.size();
 		entityPersisters = new Queryable[size];
 		entityEagerPropertyFetches = new boolean[size];
 		entityAliases = new String[size];
 		sqlAliases = new String[size];
 		sqlAliasSuffixes = new String[size];
 		includeInSelect = new boolean[size];
 		owners = new int[size];
 		ownerAssociationTypes = new EntityType[size];
 
 		for ( int i = 0; i < size; i++ ) {
 			final FromElement element = (FromElement) fromElementList.get( i );
 			entityPersisters[i] = (Queryable) element.getEntityPersister();
 
 			if ( entityPersisters[i] == null ) {
 				throw new IllegalStateException( "No entity persister for " + element.toString() );
 			}
 
 			entityEagerPropertyFetches[i] = element.isAllPropertyFetch();
 			sqlAliases[i] = element.getTableAlias();
 			entityAliases[i] = element.getClassAlias();
 			sqlAliasByEntityAlias.put( entityAliases[i], sqlAliases[i] );
 			// TODO should we just collect these like with the collections above?
 			sqlAliasSuffixes[i] = ( size == 1 ) ? "" : Integer.toString( i ) + "_";
 //			sqlAliasSuffixes[i] = element.getColumnAliasSuffix();
 			includeInSelect[i] = !element.isFetch();
 			if ( includeInSelect[i] ) {
 				selectLength++;
 			}
 
 			owners[i] = -1; //by default
 			if ( element.isFetch() ) {
 				if ( element.isCollectionJoin() || element.getQueryableCollection() != null ) {
 					// This is now handled earlier in this method.
 				}
 				else if ( element.getDataType().isEntityType() ) {
 					EntityType entityType = (EntityType) element.getDataType();
 					if ( entityType.isOneToOne() ) {
 						owners[i] = fromElementList.indexOf( element.getOrigin() );
 					}
 					ownerAssociationTypes[i] = entityType;
 				}
 			}
 		}
 
 		//NONE, because its the requested lock mode, not the actual! 
 		defaultLockModes = ArrayHelper.fillArray( LockMode.NONE, size );
 	}
 
 	public AggregatedSelectExpression getAggregatedSelectExpression() {
 		return aggregatedSelectExpression;
 	}
 
 
 	// -- Loader implementation --
 
 	public final void validateScrollability() throws HibernateException {
 		queryTranslator.validateScrollability();
 	}
 
 	@Override
 	protected boolean needsFetchingScroll() {
 		return queryTranslator.containsCollectionFetches();
 	}
 
 	@Override
 	public Loadable[] getEntityPersisters() {
 		return entityPersisters;
 	}
 
 	@Override
 	public String[] getAliases() {
 		return sqlAliases;
 	}
 
 	public String[] getSqlAliasSuffixes() {
 		return sqlAliasSuffixes;
 	}
 
 	@Override
 	public String[] getSuffixes() {
 		return getSqlAliasSuffixes();
 	}
 
 	@Override
 	public String[] getCollectionSuffixes() {
 		return collectionSuffixes;
 	}
 
 	@Override
 	protected String getQueryIdentifier() {
 		return queryTranslator.getQueryIdentifier();
 	}
 
 	/**
 	 * The SQL query string to be called.
 	 */
 	@Override
 	public String getSQLString() {
 		return queryTranslator.getSQLString();
 	}
 
 	/**
 	 * An (optional) persister for a collection to be initialized; only collection loaders
 	 * return a non-null value
 	 */
 	@Override
 	protected CollectionPersister[] getCollectionPersisters() {
 		return collectionPersisters;
 	}
 
 	@Override
 	protected int[] getCollectionOwners() {
 		return collectionOwners;
 	}
 
 	@Override
 	protected boolean[] getEntityEagerPropertyFetches() {
 		return entityEagerPropertyFetches;
 	}
 
 	/**
 	 * An array of indexes of the entity that owns a one-to-one association
 	 * to the entity at the given index (-1 if there is no "owner")
 	 */
 	@Override
 	protected int[] getOwners() {
 		return owners;
 	}
 
 	@Override
 	protected EntityType[] getOwnerAssociationTypes() {
 		return ownerAssociationTypes;
 	}
 
 	// -- Loader overrides --
 	@Override
 	protected boolean isSubselectLoadingEnabled() {
 		return hasSubselectLoadableCollections();
 	}
 
 	/**
 	 * @param lockOptions a collection of lock modes specified dynamically via the Query interface
 	 */
 	@Override
 	protected LockMode[] getLockModes(LockOptions lockOptions) {
 		if ( lockOptions == null ) {
 			return defaultLockModes;
 		}
 
 		if ( lockOptions.getAliasLockCount() == 0
 				&& ( lockOptions.getLockMode() == null || LockMode.NONE.equals( lockOptions.getLockMode() ) ) ) {
 			return defaultLockModes;
 		}
 
 		// unfortunately this stuff can't be cached because
 		// it is per-invocation, not constant for the
 		// QueryTranslator instance
 
 		LockMode[] lockModesArray = new LockMode[entityAliases.length];
 		for ( int i = 0; i < entityAliases.length; i++ ) {
 			LockMode lockMode = lockOptions.getEffectiveLockMode( entityAliases[i] );
 			if ( lockMode == null ) {
 				//NONE, because its the requested lock mode, not the actual!
 				lockMode = LockMode.NONE;
 			}
 			lockModesArray[i] = lockMode;
 		}
 
 		return lockModesArray;
 	}
 
 	@Override
 	protected String applyLocks(
 			String sql,
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) throws QueryException {
 		// can't cache this stuff either (per-invocation)
 		// we are given a map of user-alias -> lock mode
 		// create a new map of sql-alias -> lock mode
 
 		final LockOptions lockOptions = parameters.getLockOptions();
 
 		if ( lockOptions == null ||
 				( lockOptions.getLockMode() == LockMode.NONE && lockOptions.getAliasLockCount() == 0 ) ) {
 			return sql;
 		}
 
 
 		// user is request locking, lets see if we can apply locking directly to the SQL...
 
 		// 		some dialects wont allow locking with paging...
 		if ( shouldUseFollowOnLocking( parameters, dialect, afterLoadActions ) ) {
 			return sql;
 		}
 
 		//		there are other conditions we might want to add here, such as checking the result types etc
 		//		but those are better served afterQuery we have redone the SQL generation to use ASTs.
 
 
 		// we need both the set of locks and the columns to reference in locks
 		// as the ultimate output of this section...
 		final LockOptions locks = new LockOptions( lockOptions.getLockMode() );
 		final Map<String, String[]> keyColumnNames = dialect.forUpdateOfColumns()
 				? new HashMap<>()
 				: null;
 
 		locks.setScope( lockOptions.getScope() );
 		locks.setTimeOut( lockOptions.getTimeOut() );
 
 		for ( Map.Entry<String, String> entry : sqlAliasByEntityAlias.entrySet() ) {
 			final String userAlias = entry.getKey();
 			final String drivingSqlAlias = entry.getValue();
 			if ( drivingSqlAlias == null ) {
 				throw new IllegalArgumentException( "could not locate alias to apply lock mode : " + userAlias );
 			}
 			// at this point we have (drivingSqlAlias) the SQL alias of the driving table
 			// corresponding to the given user alias.  However, the driving table is not
 			// (necessarily) the table against which we want to apply locks.  Mainly,
 			// the exception case here is joined-subclass hierarchies where we instead
 			// want to apply the lock against the root table (for all other strategies,
 			// it just happens that driving and root are the same).
 			final QueryNode select = (QueryNode) queryTranslator.getSqlAST();
 			final Lockable drivingPersister = (Lockable) select.getFromClause()
 					.findFromElementByUserOrSqlAlias( userAlias, drivingSqlAlias )
 					.getQueryable();
 			final String sqlAlias = drivingPersister.getRootTableAlias( drivingSqlAlias );
 
 			final LockMode effectiveLockMode = lockOptions.getEffectiveLockMode( userAlias );
 			locks.setAliasSpecificLockMode( sqlAlias, effectiveLockMode );
 
 			if ( keyColumnNames != null ) {
 				keyColumnNames.put( sqlAlias, drivingPersister.getRootTableIdentifierColumnNames() );
 			}
 		}
 
 		// apply the collected locks and columns
 		return dialect.applyLocksToSql( sql, locks, keyColumnNames );
 	}
 
 	@Override
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SharedSessionContractImplementor session) {
 		// todo : scalars???
 //		if ( row.length != lockModesArray.length ) {
 //			return;
 //		}
 //
 //		for ( int i = 0; i < lockModesArray.length; i++ ) {
 //			if ( LockMode.OPTIMISTIC_FORCE_INCREMENT.equals( lockModesArray[i] ) ) {
 //				final EntityEntry pcEntry =
 //			}
 //			else if ( LockMode.PESSIMISTIC_FORCE_INCREMENT.equals( lockModesArray[i] ) ) {
 //
 //			}
 //		}
 	}
 
 	@Override
 	protected boolean upgradeLocks() {
 		return true;
 	}
 
 	private boolean hasSelectNew() {
 		return aggregatedSelectExpression != null && aggregatedSelectExpression.getResultTransformer() != null;
 	}
 
 	@Override
 	protected String[] getResultRowAliases() {
 		return queryReturnAliases;
 	}
 
 	@Override
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		final ResultTransformer implicitResultTransformer = aggregatedSelectExpression == null
 				? null
 				: aggregatedSelectExpression.getResultTransformer();
 		return HolderInstantiator.resolveResultTransformer( implicitResultTransformer, resultTransformer );
 	}
 
 	@Override
 	protected boolean[] includeInResultRow() {
 		boolean[] includeInResultTuple = includeInSelect;
 		if ( hasScalars ) {
 			includeInResultTuple = new boolean[queryReturnTypes.length];
 			Arrays.fill( includeInResultTuple, true );
 		}
 		return includeInResultTuple;
 	}
 
 	@Override
 	protected Object getResultColumnOrRow(
 			Object[] row,
 			ResultTransformer transformer,
 			ResultSet rs,
 			SharedSessionContractImplementor session)
 			throws SQLException, HibernateException {
 
 		Object[] resultRow = getResultRow( row, rs, session );
 		boolean hasTransform = hasSelectNew() || transformer != null;
 		return ( !hasTransform && resultRow.length == 1 ?
 				resultRow[0] :
 				resultRow
 		);
 	}
 
 	@Override
 	protected Object[] getResultRow(Object[] row, ResultSet rs, SharedSessionContractImplementor session)
 			throws SQLException, HibernateException {
 		Object[] resultRow;
 		if ( hasScalars ) {
 			String[][] scalarColumns = scalarColumnNames;
 			int queryCols = queryReturnTypes.length;
 			resultRow = new Object[queryCols];
 			for ( int i = 0; i < queryCols; i++ ) {
 				resultRow[i] = queryReturnTypes[i].nullSafeGet( rs, scalarColumns[i], session, null );
 			}
 		}
 		else {
 			resultRow = toResultRow( row );
 		}
 		return resultRow;
 	}
 
 	@SuppressWarnings("unchecked")
 	@Override
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		// meant to handle dynamic instantiation queries...
 		HolderInstantiator holderInstantiator = buildHolderInstantiator( resultTransformer );
 		if ( holderInstantiator.isRequired() ) {
 			for ( int i = 0; i < results.size(); i++ ) {
 				Object[] row = (Object[]) results.get( i );
 				Object result = holderInstantiator.instantiate( row );
 				results.set( i, result );
 			}
 
 			if ( !hasSelectNew() && resultTransformer != null ) {
 				return resultTransformer.transformList( results );
 			}
 			else {
 				return results;
 			}
 		}
 		else {
 			return results;
 		}
 	}
 
 	private HolderInstantiator buildHolderInstantiator(ResultTransformer queryLocalResultTransformer) {
 		final ResultTransformer implicitResultTransformer = aggregatedSelectExpression == null
 				? null
 				: aggregatedSelectExpression.getResultTransformer();
 		return HolderInstantiator.getHolderInstantiator(
 				implicitResultTransformer,
 				queryLocalResultTransformer,
 				queryReturnAliases
 		);
 	}
 	// --- Query translator methods ---
 
 	public List list(
 			SharedSessionContractImplementor session,
 			QueryParameters queryParameters) throws HibernateException {
 		checkQuery( queryParameters );
 		return list( session, queryParameters, queryTranslator.getQuerySpaces(), queryReturnTypes );
 	}
 
 	private void checkQuery(QueryParameters queryParameters) {
 		if ( hasSelectNew() && queryParameters.getResultTransformer() != null ) {
 			throw new QueryException( "ResultTransformer is not allowed for 'select new' queries." );
 		}
 	}
 
 	public Iterator iterate(
 			QueryParameters queryParameters,
 			EventSource session) throws HibernateException {
 		checkQuery( queryParameters );
 		final boolean stats = session.getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) {
 			startTime = System.nanoTime();
 		}
 
 		try {
 			if ( queryParameters.isCallable() ) {
 				throw new QueryException( "iterate() not supported for callable statements" );
 			}
 			final SqlStatementWrapper wrapper = executeQueryStatement(
 					queryParameters,
 					false,
 					Collections.emptyList(),
 					session
 			);
 			final ResultSet rs = wrapper.getResultSet();
 			final PreparedStatement st = (PreparedStatement) wrapper.getStatement();
 			final Iterator result = new IteratorImpl(
 					rs,
 					st,
 					session,
 					queryParameters.isReadOnly( session ),
 					queryReturnTypes,
 					queryTranslator.getColumnNames(),
 					buildHolderInstantiator( queryParameters.getResultTransformer() )
 			);
 
 			if ( stats ) {
 				final long endTime = System.nanoTime();
 				final long milliseconds = TimeUnit.MILLISECONDS.convert( endTime - startTime, TimeUnit.NANOSECONDS );
 				session.getFactory().getStatistics().queryExecuted(
 //						"HQL: " + queryTranslator.getQueryString(),
 						getQueryIdentifier(),
 						0,
 						milliseconds
 				);
 			}
 
 			return result;
 
 		}
 		catch (SQLException sqle) {
 			throw session.getJdbcServices().getSqlExceptionHelper().convert(
 					sqle,
 					"could not execute query using iterate",
 					getSQLString()
 			);
 		}
 
 	}
 
-	public ScrollableResults scroll(
+	public ScrollableResultsImplementor scroll(
 			final QueryParameters queryParameters,
 			final SharedSessionContractImplementor session) throws HibernateException {
 		checkQuery( queryParameters );
 		return scroll(
 				queryParameters,
 				queryReturnTypes,
 				buildHolderInstantiator( queryParameters.getResultTransformer() ),
 				session
 		);
 	}
 
 	// -- Implementation private methods --
 
 	private Object[] toResultRow(Object[] row) {
 		if ( selectLength == row.length ) {
 			return row;
 		}
 		else {
 			Object[] result = new Object[selectLength];
 			int j = 0;
 			for ( int i = 0; i < row.length; i++ ) {
 				if ( includeInSelect[i] ) {
 					result[j++] = row[i];
 				}
 			}
 			return result;
 		}
 	}
 
 	/**
 	 * Returns the locations of all occurrences of the named parameter.
 	 */
 	@Override
 	public int[] getNamedParameterLocs(String name) throws QueryException {
 		return queryTranslator.getParameterTranslations().getNamedParameterSqlLocations( name );
 	}
 
 	/**
 	 * We specifically override this method here, because in general we know much more
 	 * about the parameters and their appropriate bind positions here then we do in
 	 * our super because we track them explicitly here through the ParameterSpecification
 	 * interface.
 	 *
 	 * @param queryParameters The encapsulation of the parameter values to be bound.
 	 * @param startIndex The position from which to start binding parameter values.
 	 * @param session The originating session.
 	 *
 	 * @return The number of JDBC bind positions actually bound during this method execution.
 	 *
 	 * @throws SQLException Indicates problems performing the binding.
 	 */
 	@Override
 	protected int bindParameterValues(
 			final PreparedStatement statement,
 			final QueryParameters queryParameters,
 			final int startIndex,
 			final SharedSessionContractImplementor session) throws SQLException {
 		int position = startIndex;
 		List<ParameterSpecification> parameterSpecs = queryTranslator.getCollectedParameterSpecifications();
 		for ( ParameterSpecification spec : parameterSpecs ) {
 			position += spec.bind( statement, queryParameters, session, position );
 		}
 		return position - startIndex;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/query/Query.java b/hibernate-core/src/main/java/org/hibernate/query/Query.java
index aa419060d8..b32903370a 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/Query.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/Query.java
@@ -1,167 +1,183 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.query;
 
 import java.time.Instant;
 import java.time.LocalDateTime;
 import java.time.OffsetDateTime;
 import java.time.ZonedDateTime;
 import java.util.Calendar;
 import java.util.Date;
+import java.util.Spliterator;
+import java.util.stream.Stream;
 import javax.persistence.FlushModeType;
 import javax.persistence.LockModeType;
 import javax.persistence.Parameter;
 import javax.persistence.TemporalType;
 import javax.persistence.TypedQuery;
 
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.Incubating;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.engine.spi.RowSelection;
 
 /**
  * Represents an HQL/JPQL query or a compiled Criteria query.  Also acts as the Hibernate
  * extension to the JPA Query/TypedQuery contract
  * <p/>
  * NOTE: {@link org.hibernate.Query} is deprecated, and slated for removal in 6.0.
  * For the time being we leave all methods defined on {@link org.hibernate.Query}
  * rather than here because it was previously the public API so we want to leave that
  * unchanged in 5.x.  For 6.0 we will move those methods here and then delete that class.
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
 @Incubating
 @SuppressWarnings("UnusedDeclaration")
 public interface Query<R> extends TypedQuery<R>, org.hibernate.Query<R>, BasicQueryContract {
 	/**
 	 * Get the QueryProducer this Query originates from.
 	 */
 	QueryProducer getProducer();
 
 	/**
 	 * "QueryOptions" is a better name, I think, than "RowSelection" -> 6.0
 	 *
+	 * @todo 6.0 rename RowSelection to QueryOptions
+	 *
 	 * @return Return the encapsulation of this query's options, which includes access to
 	 * firstRow, maxRows, timeout and fetchSize.   Important because this gives access to
 	 * those values in their Integer form rather than the primitive form (int) required by JPA.
 	 */
 	RowSelection getQueryOptions();
 
+	/**
+	 * Retrieve a Stream over the query results.
+	 * <p/>
+	 * In the initial implementation (5.2) this returns a simple sequential Stream.  The plan
+	 * is to return a a smarter stream in 6.0 leveraging the SQM model.
+	 *
+	 * @return The results Stream
+	 *
+	 * @since 5.2
+	 */
+	Stream<R> stream();
+
 	Query<R> setParameter(Parameter<Instant> param, Instant value, TemporalType temporalType);
 
 	Query<R> setParameter(Parameter<LocalDateTime> param, LocalDateTime value, TemporalType temporalType);
 
 	Query<R> setParameter(Parameter<ZonedDateTime> param, ZonedDateTime value, TemporalType temporalType);
 
 	Query<R> setParameter(Parameter<OffsetDateTime> param, OffsetDateTime value, TemporalType temporalType);
 
 	Query<R> setParameter(String name, Instant value, TemporalType temporalType);
 
 	Query<R> setParameter(String name, LocalDateTime value, TemporalType temporalType);
 
 	Query<R> setParameter(String name, ZonedDateTime value, TemporalType temporalType);
 
 	Query<R> setParameter(String name, OffsetDateTime value, TemporalType temporalType);
 
 	Query<R> setParameter(int position, Instant value, TemporalType temporalType);
 
 	Query<R> setParameter(int position, LocalDateTime value, TemporalType temporalType);
 
 	Query<R> setParameter(int position, ZonedDateTime value, TemporalType temporalType);
 
 	Query<R> setParameter(int position, OffsetDateTime value, TemporalType temporalType);
 
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// covariant overrides
 
 
 	@Override
 	Query<R> setMaxResults(int maxResult);
 
 	@Override
 	Query<R> setFirstResult(int startPosition);
 
 	@Override
 	Query<R> setHint(String hintName, Object value);
 
 	@Override
 	<T> Query<R> setParameter(Parameter<T> param, T value);
 
 	@Override
 	Query<R> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType);
 
 	@Override
 	Query<R> setParameter(Parameter<Date> param, Date value, TemporalType temporalType);
 
 	@Override
 	Query<R> setParameter(String name, Object value);
 
 	@Override
 	Query<R> setParameter(String name, Calendar value, TemporalType temporalType);
 
 	@Override
 	Query<R> setParameter(String name, Date value, TemporalType temporalType);
 
 	@Override
 	Query<R> setParameter(int position, Object value);
 
 	@Override
 	Query<R> setParameter(int position, Calendar value, TemporalType temporalType);
 
 	@Override
 	Query<R> setParameter(int position, Date value, TemporalType temporalType);
 
 	@Override
 	Query<R> setFlushMode(FlushModeType flushMode);
 
 	@Override
 	Query<R> setLockMode(LockModeType lockMode);
 
 	@Override
 	Query<R> setReadOnly(boolean readOnly);
 
 	@Override
 	Query<R> setHibernateFlushMode(FlushMode flushMode);
 
 	@Override
 	default Query<R> setFlushMode(FlushMode flushMode) {
 		setHibernateFlushMode( flushMode );
 		return this;
 	}
 
 	@Override
 	Query<R> setCacheMode(CacheMode cacheMode);
 
 	@Override
 	Query<R> setCacheable(boolean cacheable);
 
 	@Override
 	Query<R> setCacheRegion(String cacheRegion);
 
 	@Override
 	Query<R> setTimeout(int timeout);
 
 	@Override
 	Query<R> setFetchSize(int fetchSize);
 
 	@Override
 	Query<R> setLockOptions(LockOptions lockOptions);
 
 	@Override
 	Query<R> setLockMode(String alias, LockMode lockMode);
 
 	@Override
 	Query<R> setComment(String comment);
 
 	@Override
 	Query<R> addQueryHint(String hint);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/query/internal/AbstractProducedQuery.java b/hibernate-core/src/main/java/org/hibernate/query/internal/AbstractProducedQuery.java
index 4cce49fbf3..fe842f6044 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/internal/AbstractProducedQuery.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/internal/AbstractProducedQuery.java
@@ -1,1459 +1,1476 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.query.internal;
 
 import java.io.Serializable;
 import java.time.Instant;
 import java.time.LocalDateTime;
 import java.time.OffsetDateTime;
 import java.time.ZonedDateTime;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Calendar;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
+import java.util.Spliterator;
+import java.util.Spliterators;
+import java.util.stream.Stream;
+import java.util.stream.StreamSupport;
 import javax.persistence.CacheRetrieveMode;
 import javax.persistence.CacheStoreMode;
 import javax.persistence.FlushModeType;
 import javax.persistence.LockModeType;
 import javax.persistence.Parameter;
 import javax.persistence.TemporalType;
 import javax.persistence.TransactionRequiredException;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Filter;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.NonUniqueResultException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.ScrollMode;
-import org.hibernate.ScrollableResults;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.engine.query.spi.EntityGraphQueryHint;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.spi.ExceptionConverter;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.hql.internal.QueryExecutionRequestException;
 import org.hibernate.internal.EntityManagerMessageLogger;
 import org.hibernate.internal.HEMLogging;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jpa.QueryHints;
 import org.hibernate.jpa.TypedParameterValue;
 import org.hibernate.jpa.graph.internal.EntityGraphImpl;
 import org.hibernate.jpa.internal.util.CacheModeHelper;
 import org.hibernate.jpa.internal.util.ConfigurationHelper;
 import org.hibernate.jpa.internal.util.FlushModeTypeHelper;
 import org.hibernate.jpa.internal.util.LockModeTypeHelper;
 import org.hibernate.property.access.spi.BuiltInPropertyAccessStrategies;
 import org.hibernate.property.access.spi.Getter;
 import org.hibernate.property.access.spi.PropertyAccess;
 import org.hibernate.query.ParameterMetadata;
 import org.hibernate.query.QueryParameter;
 import org.hibernate.query.spi.QueryImplementor;
 import org.hibernate.query.spi.QueryParameterBinding;
 import org.hibernate.query.spi.QueryParameterListBinding;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 import static org.hibernate.LockOptions.WAIT_FOREVER;
 import static org.hibernate.cfg.AvailableSettings.JPA_LOCK_SCOPE;
 import static org.hibernate.cfg.AvailableSettings.JPA_LOCK_TIMEOUT;
 import static org.hibernate.cfg.AvailableSettings.JPA_SHARED_CACHE_RETRIEVE_MODE;
 import static org.hibernate.cfg.AvailableSettings.JPA_SHARED_CACHE_STORE_MODE;
 import static org.hibernate.jpa.AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE;
 import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;
 import static org.hibernate.jpa.QueryHints.HINT_CACHE_MODE;
 import static org.hibernate.jpa.QueryHints.HINT_CACHE_REGION;
 import static org.hibernate.jpa.QueryHints.HINT_COMMENT;
 import static org.hibernate.jpa.QueryHints.HINT_FETCHGRAPH;
 import static org.hibernate.jpa.QueryHints.HINT_FETCH_SIZE;
 import static org.hibernate.jpa.QueryHints.HINT_FLUSH_MODE;
 import static org.hibernate.jpa.QueryHints.HINT_LOADGRAPH;
 import static org.hibernate.jpa.QueryHints.HINT_READONLY;
 import static org.hibernate.jpa.QueryHints.HINT_TIMEOUT;
 import static org.hibernate.jpa.QueryHints.SPEC_HINT_TIMEOUT;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractProducedQuery<R> implements QueryImplementor<R> {
 	private static final EntityManagerMessageLogger log = HEMLogging.messageLogger( AbstractProducedQuery.class );
 
 	private final SharedSessionContractImplementor producer;
 	private final ParameterMetadata parameterMetadata;
 	private final QueryParameterBindingsImpl queryParameterBindings;
 
 	private FlushMode flushMode;
 	private CacheStoreMode cacheStoreMode;
 	private CacheRetrieveMode cacheRetrieveMode;
 	private boolean cacheable;
 	private String cacheRegion;
 	private Boolean readOnly;
 
 	private LockOptions lockOptions = new LockOptions();
 
 	private String comment;
 	private final List<String> dbHints = new ArrayList<>();
 
 	private ResultTransformer resultTransformer;
 	private RowSelection queryOptions = new RowSelection();
 
 	private EntityGraphQueryHint entityGraphQueryHint;
 
 	private Object optionalObject;
 	private Serializable optionalId;
 	private String optionalEntityName;
 
 	public AbstractProducedQuery(
 			SharedSessionContractImplementor producer,
 			ParameterMetadata parameterMetadata) {
 		this.producer = producer;
 		this.parameterMetadata = parameterMetadata;
 		this.queryParameterBindings = QueryParameterBindingsImpl.from( parameterMetadata, producer.getFactory() );
 	}
 
 	@Override
 	public SharedSessionContractImplementor getProducer() {
 		return producer;
 	}
 
 	@Override
 	public FlushMode getHibernateFlushMode() {
 		return flushMode;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setHibernateFlushMode(FlushMode flushMode) {
 		this.flushMode = flushMode;
 		return this;
 	}
 
 	@Override
 	public QueryImplementor setFlushMode(FlushMode flushMode) {
 		return setHibernateFlushMode( flushMode );
 	}
 
 	@Override
 	public FlushModeType getFlushMode() {
 		return ( flushMode == null ?
 				getProducer().getFlushMode() :
 				FlushModeTypeHelper.getFlushModeType( flushMode )
 		);
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setFlushMode(FlushModeType flushModeType) {
 		setHibernateFlushMode( FlushModeTypeHelper.getFlushMode( flushModeType ) );
 		return this;
 	}
 
 	@Override
 	public CacheMode getCacheMode() {
 		return CacheModeHelper.interpretCacheMode( cacheStoreMode, cacheRetrieveMode );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setCacheMode(CacheMode cacheMode) {
 		this.cacheStoreMode = CacheModeHelper.interpretCacheStoreMode( cacheMode );
 		this.cacheRetrieveMode = CacheModeHelper.interpretCacheRetrieveMode( cacheMode );
 		return this;
 	}
 
 	@Override
 	public boolean isCacheable() {
 		return cacheable;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setCacheable(boolean cacheable) {
 		this.cacheable = cacheable;
 		return this;
 	}
 
 	@Override
 	public String getCacheRegion() {
 		return cacheRegion;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setCacheRegion(String cacheRegion) {
 		this.cacheRegion = cacheRegion;
 		return this;
 	}
 
 	@Override
 	public Integer getTimeout() {
 		return queryOptions.getTimeout();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setTimeout(int timeout) {
 		queryOptions.setTimeout( timeout );
 		return this;
 	}
 
 	@Override
 	public Integer getFetchSize() {
 		return queryOptions.getFetchSize();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setFetchSize(int fetchSize) {
 		queryOptions.setFetchSize( fetchSize );
 		return this;
 	}
 
 	@Override
 	public boolean isReadOnly() {
 		return ( readOnly == null ?
 				producer.getPersistenceContext().isDefaultReadOnly() :
 				readOnly
 		);
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setReadOnly(boolean readOnly) {
 		this.readOnly = readOnly;
 		return this;
 	}
 
 	@Override
 	public LockOptions getLockOptions() {
 		return lockOptions;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setLockOptions(LockOptions lockOptions) {
 		this.lockOptions.setLockMode( lockOptions.getLockMode() );
 		this.lockOptions.setScope( lockOptions.getScope() );
 		this.lockOptions.setTimeOut( lockOptions.getTimeOut() );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setLockMode(String alias, LockMode lockMode) {
 		lockOptions.setAliasSpecificLockMode( alias, lockMode );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setLockMode(LockModeType lockModeType) {
 		if ( !LockModeType.NONE.equals( lockModeType ) ) {
 			if ( !isSelect() ) {
 				throw new IllegalStateException( "Illegal attempt to set lock mode on a non-SELECT query" );
 			}
 		}
 		lockOptions.setLockMode( LockModeTypeHelper.getLockMode( lockModeType ) );
 		return this;
 	}
 
 	@Override
 	public String getComment() {
 		return comment;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setComment(String comment) {
 		this.comment = comment;
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor addQueryHint(String hint) {
 		this.dbHints.add( hint );
 		return this;
 	}
 
 	@Override
 	public ParameterMetadata getParameterMetadata() {
 		return parameterMetadata;
 	}
 
 	@Override
 	public String[] getNamedParameters() {
 		return ArrayHelper.toStringArray( getParameterMetadata().getNamedParameterNames() );
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(Parameter<Instant> param, Instant value, TemporalType temporalType) {
 		locateBinding( param ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(Parameter<LocalDateTime> param, LocalDateTime value, TemporalType temporalType) {
 		locateBinding( param ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(Parameter<ZonedDateTime> param, ZonedDateTime value, TemporalType temporalType) {
 		locateBinding( param ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(Parameter<OffsetDateTime> param, OffsetDateTime value, TemporalType temporalType) {
 		locateBinding( param ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(String name, Instant value, TemporalType temporalType) {
 		locateBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(String name, LocalDateTime value, TemporalType temporalType) {
 		locateBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(String name, ZonedDateTime value, TemporalType temporalType) {
 		locateBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(String name, OffsetDateTime value, TemporalType temporalType) {
 		locateBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(int position, Instant value, TemporalType temporalType) {
 		locateBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(int position, LocalDateTime value, TemporalType temporalType) {
 		locateBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(int position, ZonedDateTime value, TemporalType temporalType) {
 		locateBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public QueryImplementor<R> setParameter(int position, OffsetDateTime value, TemporalType temporalType) {
 		locateBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <P> QueryImplementor setParameter(QueryParameter<P> parameter, P value) {
 		locateBinding( parameter ).setBindValue( value );
 		return this;
 	}
 
 	@SuppressWarnings("unchecked")
 	private <P> QueryParameterBinding<P> locateBinding(Parameter<P> parameter) {
 		if ( parameter instanceof QueryParameter ) {
 			return queryParameterBindings.getBinding( (QueryParameter) parameter );
 		}
 		else if ( parameter.getName() != null ) {
 			return queryParameterBindings.getBinding( parameter.getName() );
 		}
 		else if ( parameter.getPosition() != null ) {
 			return queryParameterBindings.getBinding( parameter.getPosition() );
 		}
 
 		throw getExceptionConverter().convert(
 				new IllegalArgumentException( "Could not resolve binding for given parameter reference [" + parameter + "]" )
 		);
 	}
 
 	private  <P> QueryParameterBinding<P> locateBinding(QueryParameter<P> parameter) {
 		return queryParameterBindings.getBinding( parameter );
 	}
 
 	private <P> QueryParameterBinding<P> locateBinding(String name) {
 		return queryParameterBindings.getBinding( name );
 	}
 
 	private <P> QueryParameterBinding<P> locateBinding(int position) {
 		return queryParameterBindings.getBinding( position );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <P> QueryImplementor setParameter(Parameter<P> parameter, P value) {
 		if ( value instanceof TypedParameterValue ) {
 			setParameter( parameter, ( (TypedParameterValue) value ).getValue(), ( (TypedParameterValue) value ).getType() );
 		}
 		else if ( value instanceof Collection ) {
 			locateListBinding( parameter ).setBindValues( (Collection) value );
 		}
 		else {
 			locateBinding( parameter ).setBindValue( value );
 		}
 
 		return this;
 	}
 
 	@SuppressWarnings("unchecked")
 	private <P> void setParameter(Parameter<P> parameter, Object value, Type type) {
 		if ( parameter instanceof QueryParameter ) {
 			setParameter( (QueryParameter) parameter, value, type );
 		}
 		else if ( value == null ) {
 			locateBinding( parameter ).setBindValue( null, type );
 		}
 		else if ( value instanceof Collection ) {
 			locateListBinding( parameter ).setBindValues( (Collection) value, type );
 		}
 		else {
 			locateBinding( parameter ).setBindValue( (P) value, type );
 		}
 	}
 
 	private QueryParameterListBinding locateListBinding(Parameter parameter) {
 		if ( parameter instanceof QueryParameter ) {
 			return queryParameterBindings.getQueryParameterListBinding( (QueryParameter) parameter );
 		}
 		else {
 			return queryParameterBindings.getQueryParameterListBinding( parameter.getName() );
 		}
 	}
 
 	private QueryParameterListBinding locateListBinding(String name) {
 		return queryParameterBindings.getQueryParameterListBinding( name );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(String name, Object value) {
 		if ( value instanceof TypedParameterValue ) {
 			final TypedParameterValue  typedValueWrapper = (TypedParameterValue ) value;
 			setParameter( name, typedValueWrapper.getValue(), typedValueWrapper.getType() );
 		}
 		else if ( value instanceof Collection ) {
 			setParameterList( name, (Collection) value );
 		}
 		else {
 			queryParameterBindings.getBinding( name ).setBindValue( value );
 		}
 
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(int position, Object value) {
 		if ( value instanceof TypedParameterValue ) {
 			final TypedParameterValue typedParameterValue = (TypedParameterValue) value;
 			setParameter( position, typedParameterValue.getValue(), typedParameterValue.getType() );
 		}
 		if ( value instanceof Collection ) {
 			setParameterList( Integer.toString( position ), (Collection) value );
 		}
 		else {
 			queryParameterBindings.getBinding( position ).setBindValue( value );
 		}
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <P> QueryImplementor setParameter(QueryParameter<P> parameter, P value, Type type) {
 		queryParameterBindings.getBinding( parameter ).setBindValue( value, type );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(String name, Object value, Type type) {
 		queryParameterBindings.getBinding( name ).setBindValue( value, type );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(int position, Object value, Type type) {
 		queryParameterBindings.getBinding( position ).setBindValue( value, type );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <P> QueryImplementor setParameter(QueryParameter<P> parameter, P value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( parameter ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(String name, Object value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(int position, Object value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <P> QueryImplementor<R> setParameterList(QueryParameter<P> parameter, Collection<P> values) {
 		queryParameterBindings.getQueryParameterListBinding( parameter ).setBindValues( values );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameterList(String name, Collection values) {
 		queryParameterBindings.getQueryParameterListBinding( name ).setBindValues( values );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameterList(String name, Collection values, Type type) {
 		queryParameterBindings.getQueryParameterListBinding( name ).setBindValues( values, type );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameterList(String name, Object[] values, Type type) {
 		queryParameterBindings.getQueryParameterListBinding( name ).setBindValues( Arrays.asList( values ), type );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameterList(String name, Object[] values) {
 		queryParameterBindings.getQueryParameterListBinding( name ).setBindValues( Arrays.asList( values ) );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( (QueryParameter) param ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( (QueryParameter) param ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(String name, Calendar value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(String name, Date value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( name ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(int position, Calendar value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setParameter(int position, Date value, TemporalType temporalType) {
 		queryParameterBindings.getBinding( position ).setBindValue( value, temporalType );
 		return this;
 	}
 
 	@Override
 	public Set<Parameter<?>> getParameters() {
 		return parameterMetadata.collectAllParametersJpa();
 	}
 
 	@Override
 	public Parameter<?> getParameter(String name) {
 		return parameterMetadata.getQueryParameter( name );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> Parameter<T> getParameter(String name, Class<T> type) {
 		final QueryParameter parameter = parameterMetadata.getQueryParameter( name );
 		if ( !parameter.getParameterType().isAssignableFrom( type ) ) {
 			throw new IllegalArgumentException(
 					"The type [" + parameter.getParameterType().getName() +
 							"] associated with the parameter corresponding to name [" + name +
 							"] is not assignable to requested Java type [" + type.getName() + "]"
 			);
 		}
 		return parameter;
 	}
 
 	@Override
 	public Parameter<?> getParameter(int position) {
 		// It is important to understand that there are 2 completely distinct conceptualization of
 		// "positional parameters" in play here:
 		//		1) The legacy Hibernate concept is akin to JDBC PreparedStatement parameters.  Very limited and
 		//			deprecated since 5.x.  These are numbered starting from 0 and kept in the
 		//			ParameterMetadata positional-parameter array keyed by this zero-based position
 		//		2) JPA's definition is really just a named parameter, but expected to explicitly be
 		//			sequential intergers starting from 0 (ZERO); they can repeat.
 		//
 		// It is considered illegal to mix positional-parameter with named parameters of any kind.  So therefore.
 		// if ParameterMetadata reports that it has any positional-parameters it is talking about the
 		// legacy Hibernate concept.
 		// lookup jpa-based positional parameters first by name.
 		if ( parameterMetadata.getPositionalParameterCount() == 0 ) {
 			return parameterMetadata.getQueryParameter( Integer.toString( position ) );
 		}
 		// fallback to oridinal lookup
 		return parameterMetadata.getQueryParameter( position );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> Parameter<T> getParameter(int position, Class<T> type) {
 		final QueryParameter parameter = parameterMetadata.getQueryParameter( position );
 		if ( !parameter.getParameterType().isAssignableFrom( type ) ) {
 			throw new IllegalArgumentException(
 					"The type [" + parameter.getParameterType().getName() +
 							"] associated with the parameter corresponding to position [" + position +
 							"] is not assignable to requested Java type [" + type.getName() + "]"
 			);
 		}
 		return parameter;
 	}
 
 	@Override
 	public boolean isBound(Parameter<?> parameter) {
 		return queryParameterBindings.isBound( (QueryParameter) parameter );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> T getParameterValue(Parameter<T> parameter) {
 		return (T) queryParameterBindings.getBinding( (QueryParameter) parameter ).getBindValue();
 	}
 
 	@Override
 	public Object getParameterValue(String name) {
 		return queryParameterBindings.getBinding( name ).getBindValue();
 	}
 
 	@Override
 	public Object getParameterValue(int position) {
 		return queryParameterBindings.getBinding( position ).getBindValue();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setProperties(Object bean) {
 		Class clazz = bean.getClass();
 		String[] params = getNamedParameters();
 		for ( String namedParam : params ) {
 			try {
 				final PropertyAccess propertyAccess = BuiltInPropertyAccessStrategies.BASIC.getStrategy().buildPropertyAccess(
 						clazz,
 						namedParam
 				);
 				final Getter getter = propertyAccess.getGetter();
 				final Class retType = getter.getReturnType();
 				final Object object = getter.get( bean );
 				if ( Collection.class.isAssignableFrom( retType ) ) {
 					setParameterList( namedParam, (Collection) object );
 				}
 				else if ( retType.isArray() ) {
 					setParameterList( namedParam, (Object[]) object );
 				}
 				else {
 					Type type = determineType( namedParam, retType );
 					setParameter( namedParam, object, type );
 				}
 			}
 			catch (PropertyNotFoundException pnfe) {
 				// ignore
 			}
 		}
 		return this;
 	}
 
 	protected Type determineType(String namedParam, Class retType) {
 		Type type = queryParameterBindings.getBinding( namedParam ).getBindType();
 		if ( type == null ) {
 			type = parameterMetadata.getQueryParameter( namedParam ).getType();
 		}
 		if ( type == null ) {
 			type = getProducer().getFactory().resolveParameterBindType( retType );
 		}
 		return type;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setProperties(Map map) {
 		final String[] namedParameterNames = getNamedParameters();
 		for ( String paramName : namedParameterNames ) {
 			final Object object = map.get( paramName );
 			if ( object == null ) {
 				setParameter( paramName, null, determineType( paramName, null ) );
 			}
 			else {
 				Class retType = object.getClass();
 				if ( Collection.class.isAssignableFrom( retType ) ) {
 					setParameterList( paramName, (Collection) object );
 				}
 				else if ( retType.isArray() ) {
 					setParameterList( paramName, (Object[]) object );
 				}
 				else {
 					setParameter( paramName, object, determineType( paramName, retType ) );
 				}
 			}
 		}
 		return this;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setResultTransformer(ResultTransformer transformer) {
 		this.resultTransformer = transformer;
 		return this;
 	}
 
 	@Override
 	public RowSelection getQueryOptions() {
 		return queryOptions;
 	}
 
 	@Override
 	public int getMaxResults() {
 		// to be JPA compliant this method returns an int - specifically the "magic number" Integer.MAX_VALUE defined by the spec.
 		// For access to the Integer (for checking), use #getQueryOptions#getMaxRows instead
 		return queryOptions.getMaxRows() == null ? Integer.MAX_VALUE : queryOptions.getMaxRows();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setMaxResults(int maxResult) {
 		if ( maxResult <= 0 ) {
 			// treat zero and negatives specially as meaning no limit...
 			queryOptions.setMaxRows( null );
 		}
 		else {
 			queryOptions.setMaxRows( maxResult );
 		}
 		return this;
 	}
 
 	@Override
 	public int getFirstResult() {
 		// to be JPA compliant this method returns an int - specifically the "magic number" 0 (ZERO) defined by the spec.
 		// For access to the Integer (for checking), use #getQueryOptions#getFirstRow instead
 		return queryOptions.getFirstRow() == null ? 0 : queryOptions.getFirstRow();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setFirstResult(int startPosition) {
 		queryOptions.setFirstRow( startPosition );
 		return this;
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public Set<String> getSupportedHints() {
 		return QueryHints.getDefinedHints();
 	}
 
 	@Override
 	public Map<String, Object> getHints() {
 		// Technically this should rollback, but that's insane :)
 		// If the TCK ever adds a check for this, we may need to change this behavior
 		getProducer().checkOpen( false );
 
 		final Map<String,Object> hints = new HashMap<>();
 		collectBaselineHints( hints );
 		collectHints( hints );
 		return hints;
 	}
 
 	protected void collectBaselineHints(Map<String, Object> hints) {
 		// nothing to do in this form
 	}
 
 	protected void collectHints(Map<String, Object> hints) {
 		if ( getQueryOptions().getTimeout() != null ) {
 			hints.put( HINT_TIMEOUT, getQueryOptions().getTimeout() );
 			hints.put( SPEC_HINT_TIMEOUT, getQueryOptions().getTimeout() * 1000 );
 		}
 
 		if ( getLockOptions().getTimeOut() != WAIT_FOREVER ) {
 			hints.put( JPA_LOCK_TIMEOUT, getLockOptions().getTimeOut() );
 		}
 
 		if ( getLockOptions().getScope() ) {
 			hints.put( JPA_LOCK_SCOPE, getLockOptions().getScope() );
 		}
 
 		if ( getLockOptions().hasAliasSpecificLockModes() && canApplyAliasSpecificLockModeHints() ) {
 			for ( Map.Entry<String, LockMode> entry : getLockOptions().getAliasSpecificLocks() ) {
 				hints.put(
 						ALIAS_SPECIFIC_LOCK_MODE + '.' + entry.getKey(),
 						entry.getValue().name()
 				);
 			}
 		}
 
 		putIfNotNull( hints, HINT_COMMENT, getComment() );
 		putIfNotNull( hints, HINT_FETCH_SIZE, getQueryOptions().getFetchSize() );
 		putIfNotNull( hints, HINT_FLUSH_MODE, getHibernateFlushMode() );
 
 		if ( cacheStoreMode != null || cacheRetrieveMode != null ) {
 			putIfNotNull( hints, HINT_CACHE_MODE, CacheModeHelper.interpretCacheMode( cacheStoreMode, cacheRetrieveMode ) );
 			putIfNotNull( hints, JPA_SHARED_CACHE_RETRIEVE_MODE, cacheRetrieveMode );
 			putIfNotNull( hints, JPA_SHARED_CACHE_STORE_MODE, cacheStoreMode );
 		}
 
 		if ( isCacheable() ) {
 			hints.put( HINT_CACHEABLE, true );
 			putIfNotNull( hints, HINT_CACHE_REGION, getCacheRegion() );
 		}
 
 		if ( isReadOnly() ) {
 			hints.put( HINT_READONLY, true );
 		}
 
 		if ( entityGraphQueryHint != null ) {
 			hints.put( entityGraphQueryHint.getHintName(), entityGraphQueryHint.getOriginEntityGraph() );
 		}
 	}
 
 	protected void putIfNotNull(Map<String, Object> hints, String hintName, Enum hintValue) {
 		// centralized spot to handle the decision whether to put enums directly into the hints map
 		// or whether to put the enum name
 		if ( hintValue != null ) {
 			hints.put( hintName, hintValue );
 //			hints.put( hintName, hintValue.name() );
 		}
 	}
 
 	protected void putIfNotNull(Map<String, Object> hints, String hintName, Object hintValue) {
 		if ( hintValue != null ) {
 			hints.put( hintName, hintValue );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public QueryImplementor setHint(String hintName, Object value) {
 		getProducer().checkOpen( true );
 		boolean applied = false;
 		try {
 			if ( HINT_TIMEOUT.equals( hintName ) ) {
 				applied = applyTimeoutHint( ConfigurationHelper.getInteger( value ) );
 			}
 			else if ( SPEC_HINT_TIMEOUT.equals( hintName ) ) {
 				// convert milliseconds to seconds
 				int timeout = (int)Math.round( ConfigurationHelper.getInteger( value ).doubleValue() / 1000.0 );
 				applied = applyTimeoutHint( timeout );
 			}
 			else if ( JPA_LOCK_TIMEOUT.equals( hintName ) ) {
 				applied = applyLockTimeoutHint( ConfigurationHelper.getInteger( value ) );
 			}
 			else if ( HINT_COMMENT.equals( hintName ) ) {
 				applied = applyCommentHint( (String) value );
 			}
 			else if ( HINT_FETCH_SIZE.equals( hintName ) ) {
 				applied = applyFetchSizeHint( ConfigurationHelper.getInteger( value ) );
 			}
 			else if ( HINT_CACHEABLE.equals( hintName ) ) {
 				applied = applyCacheableHint( ConfigurationHelper.getBoolean( value ) );
 			}
 			else if ( HINT_CACHE_REGION.equals( hintName ) ) {
 				applied = applyCacheRegionHint( (String) value );
 			}
 			else if ( HINT_READONLY.equals( hintName ) ) {
 				applied = applyReadOnlyHint( ConfigurationHelper.getBoolean( value ) );
 			}
 			else if ( HINT_FLUSH_MODE.equals( hintName ) ) {
 				applied = applyFlushModeHint( ConfigurationHelper.getFlushMode( value ) );
 			}
 			else if ( HINT_CACHE_MODE.equals( hintName ) ) {
 				applied = applyCacheModeHint( ConfigurationHelper.getCacheMode( value ) );
 			}
 			else if ( JPA_SHARED_CACHE_RETRIEVE_MODE.equals( hintName ) ) {
 				final CacheRetrieveMode retrieveMode = value != null ? CacheRetrieveMode.valueOf( value.toString() ) : null;
 				applied = applyJpaCacheRetrieveMode( retrieveMode );
 			}
 			else if ( JPA_SHARED_CACHE_STORE_MODE.equals( hintName ) ) {
 				final CacheStoreMode storeMode = value != null ? CacheStoreMode.valueOf( value.toString() ) : null;
 				applied = applyJpaCacheStoreMode( storeMode );
 			}
 			else if ( QueryHints.HINT_NATIVE_LOCKMODE.equals( hintName ) ) {
 				applied = applyNativeQueryLockMode( value );
 			}
 			else if ( hintName.startsWith( ALIAS_SPECIFIC_LOCK_MODE ) ) {
 				if ( canApplyAliasSpecificLockModeHints() ) {
 					// extract the alias
 					final String alias = hintName.substring( ALIAS_SPECIFIC_LOCK_MODE.length() + 1 );
 					// determine the LockMode
 					try {
 						final LockMode lockMode = LockModeTypeHelper.interpretLockMode( value );
 						applyAliasSpecificLockModeHint( alias, lockMode );
 					}
 					catch ( Exception e ) {
 						log.unableToDetermineLockModeValue( hintName, value );
 						applied = false;
 					}
 				}
 				else {
 					applied = false;
 				}
 			}
 			else if ( HINT_FETCHGRAPH.equals( hintName ) || HINT_LOADGRAPH.equals( hintName ) ) {
 				if (value instanceof EntityGraphImpl ) {
 					applyEntityGraphQueryHint( new EntityGraphQueryHint( hintName, (EntityGraphImpl) value ) );
 				}
 				else {
 					log.warnf( "The %s hint was set, but the value was not an EntityGraph!", hintName );
 				}
 				applied = true;
 			}
 			else {
 				log.ignoringUnrecognizedQueryHint( hintName );
 			}
 		}
 		catch ( ClassCastException e ) {
 			throw new IllegalArgumentException( "Value for hint" );
 		}
 
 		if ( !applied ) {
 			log.debugf( "Skipping unsupported query hint [%s]", hintName );
 		}
 
 		return this;
 	}
 
 	protected boolean applyJpaCacheRetrieveMode(CacheRetrieveMode mode) {
 		this.cacheRetrieveMode = mode;
 		return true;
 	}
 
 	protected boolean applyJpaCacheStoreMode(CacheStoreMode storeMode) {
 		this.cacheStoreMode = storeMode;
 		return true;
 	}
 
 	protected boolean applyNativeQueryLockMode(Object value) {
 		if ( !isNativeQuery() ) {
 			throw new IllegalStateException(
 					"Illegal attempt to set lock mode on non-native query via hint; use Query#setLockMode instead"
 			);
 		}
 
 		return false;
 	}
 
 	/**
 	 * Apply the query timeout hint.
 	 *
 	 * @param timeout The timeout (in seconds!) specified as a hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyTimeoutHint(int timeout) {
 		setTimeout( timeout );
 		return true;
 	}
 
 	/**
 	 * Apply the lock timeout (in seconds!) hint
 	 *
 	 * @param timeout The timeout (in seconds!) specified as a hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyLockTimeoutHint(int timeout) {
 		getLockOptions().setTimeOut( timeout );
 		return true;
 	}
 
 	/**
 	 * Apply the comment hint.
 	 *
 	 * @param comment The comment specified as a hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyCommentHint(String comment) {
 		setComment( comment );
 		return true;
 	}
 
 	/**
 	 * Apply the fetch size hint
 	 *
 	 * @param fetchSize The fetch size specified as a hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyFetchSizeHint(int fetchSize) {
 		setFetchSize( fetchSize );
 		return true;
 	}
 
 	/**
 	 * Apply the cacheable (true/false) hint.
 	 *
 	 * @param isCacheable The value specified as hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyCacheableHint(boolean isCacheable) {
 		setCacheable( isCacheable );
 		return true;
 	}
 
 	/**
 	 * Apply the cache region hint
 	 *
 	 * @param regionName The name of the cache region specified as a hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyCacheRegionHint(String regionName) {
 		setCacheRegion( regionName );
 		return true;
 	}
 
 	/**
 	 * Apply the read-only (true/false) hint.
 	 *
 	 * @param isReadOnly The value specified as hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyReadOnlyHint(boolean isReadOnly) {
 		setReadOnly( isReadOnly );
 		return true;
 	}
 
 	/**
 	 * Apply the CacheMode hint.
 	 *
 	 * @param cacheMode The CacheMode value specified as a hint.
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyCacheModeHint(CacheMode cacheMode) {
 		setCacheMode( cacheMode );
 		return true;
 	}
 
 	/**
 	 * Apply the FlushMode hint.
 	 *
 	 * @param flushMode The FlushMode value specified as hint
 	 *
 	 * @return {@code true} if the hint was "applied"
 	 */
 	protected boolean applyFlushModeHint(FlushMode flushMode) {
 		setFlushMode( flushMode );
 		return true;
 	}
 
 	/**
 	 * Can alias-specific lock modes be applied?
 	 *
 	 * @return {@code true} indicates they can be applied, {@code false} otherwise.
 	 */
 	protected boolean canApplyAliasSpecificLockModeHints() {
 		// only procedure/function calls cannot i believe
 		return true;
 	}
 
 	protected boolean applyLockModeTypeHint(LockModeType lockModeType) {
 		getLockOptions().setLockMode( LockModeTypeHelper.getLockMode( lockModeType ) );
 		return true;
 	}
 
 	protected boolean applyHibernateLockModeHint(LockMode lockMode) {
 		getLockOptions().setLockMode( lockMode );
 		return true;
 	}
 
 	/**
 	 * Apply the alias specific lock modes.  Assumes {@link #canApplyAliasSpecificLockModeHints()} has already been
 	 * called and returned {@code true}.
 	 *
 	 * @param alias The alias to apply the 'lockMode' to.
 	 * @param lockMode The LockMode to apply.
 	 */
 	protected void applyAliasSpecificLockModeHint(String alias, LockMode lockMode) {
 		getLockOptions().setAliasSpecificLockMode( alias, lockMode );
 	}
 
 	/**
 	 * Used from HEM code as a (hopefully temporary) means to apply a custom query plan
 	 * in regards to a JPA entity graph.
 	 *
 	 * @param hint The entity graph hint object
 	 */
 	protected void applyEntityGraphQueryHint(EntityGraphQueryHint hint) {
 		this.entityGraphQueryHint = hint;
 	}
 
 	/**
 	 * Is the query represented here a native (SQL) query?
 	 *
 	 * @return {@code true} if it is a native query; {@code false} otherwise
 	 */
 	protected abstract boolean isNativeQuery();
 
 	@Override
 	public LockModeType getLockMode() {
 		return LockModeTypeHelper.getLockModeType( lockOptions.getLockMode() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> T unwrap(Class<T> cls) {
 		if ( cls.isInstance( getProducer() ) ) {
 			return (T) getProducer();
 		}
 		if ( cls.isInstance( getParameterMetadata() ) ) {
 			return (T) getParameterMetadata();
 		}
 		if ( cls.isInstance( queryParameterBindings ) ) {
 			return (T) queryParameterBindings;
 		}
 		if ( cls.isInstance( this ) ) {
 			return (T) this;
 		}
 
 		throw new HibernateException( "Could not unwrap this [" + toString() + "] as requested Java type [" + cls.getName() + "]" );
 //		throw new IllegalArgumentException( "Could not unwrap this [" + toString() + "] as requested Java type [" + cls.getName() + "]" );
 	}
 
 	public QueryParameters getQueryParameters() {
 		final HQLQueryPlan entityGraphHintedQueryPlan;
 		if ( entityGraphQueryHint == null) {
 			entityGraphHintedQueryPlan = null;
 		}
 		else {
 			queryParameterBindings.verifyParametersBound( false );
 
 			// todo : ideally we'd update the instance state related to queryString but that is final atm
 
 			final String expandedQuery = queryParameterBindings.expandListValuedParameters( getQueryString(), getProducer() );
 			entityGraphHintedQueryPlan = new HQLQueryPlan(
 					expandedQuery,
 					false,
 					getProducer().getLoadQueryInfluencers().getEnabledFilters(),
 					getProducer().getFactory(),
 					entityGraphQueryHint
 			);
 		}
 
 		QueryParameters queryParameters = new QueryParameters(
 				getPositionalParameterTypes(),
 				getPositionalParameterValues(),
 				getNamedParameterMap(),
 				getLockOptions(),
 				queryOptions,
 				true,
 				isReadOnly(),
 				cacheable,
 				cacheRegion,
 				comment,
 				dbHints,
 				null,
 				optionalObject,
 				optionalEntityName,
 				optionalId,
 				resultTransformer
 		);
 		queryParameters.setQueryPlan( entityGraphHintedQueryPlan );
 		return queryParameters;
 	}
 
 	@SuppressWarnings("deprecation")
 	protected Type[] getPositionalParameterTypes() {
 		return queryParameterBindings.collectPositionalBindTypes();
 	}
 
 	@SuppressWarnings("deprecation")
 	protected Object[] getPositionalParameterValues() {
 		return queryParameterBindings.collectPositionalBindValues();
 	}
 
 	@SuppressWarnings("deprecation")
 	protected Map<String, TypedValue> getNamedParameterMap() {
 		return queryParameterBindings.collectNamedParameterBindings();
 	}
 
 	private FlushMode sessionFlushMode;
 	private CacheMode sessionCacheMode;
 
 	protected void beforeQuery() {
 		queryParameterBindings.verifyParametersBound( isCallable() );
 
 		assert sessionFlushMode == null;
 		assert sessionCacheMode == null;
 
 		if ( flushMode != null ) {
 			sessionFlushMode = getProducer().getHibernateFlushMode();
 			getProducer().setHibernateFlushMode( flushMode );
 		}
 		final CacheMode effectiveCacheMode = CacheModeHelper.effectiveCacheMode( cacheStoreMode, cacheRetrieveMode );
 		if ( effectiveCacheMode != null ) {
 			sessionCacheMode = getProducer().getCacheMode();
 			getProducer().setCacheMode( effectiveCacheMode );
 		}
 	}
 
 	protected void afterQuery() {
 		if ( sessionFlushMode != null ) {
 			getProducer().setHibernateFlushMode( sessionFlushMode );
 			sessionFlushMode = null;
 		}
 		if ( sessionCacheMode != null ) {
 			getProducer().setCacheMode( sessionCacheMode );
 			sessionCacheMode = null;
 		}
 	}
 
 	@Override
 	public Iterator<R> iterate() {
 		beforeQuery();
 		try {
 			return doIterate();
 		}
 		finally {
 			afterQuery();
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	protected Iterator<R> doIterate() {
 		return getProducer().iterate(
 				queryParameterBindings.expandListValuedParameters( getQueryString(), getProducer() ),
 				getQueryParameters()
 		);
 	}
 
 	@Override
-	public ScrollableResults scroll() {
+	public ScrollableResultsImplementor scroll() {
 		return scroll( getProducer().getJdbcServices().getJdbcEnvironment().getDialect().defaultScrollMode() );
 	}
 
 	@Override
-	public ScrollableResults scroll(ScrollMode scrollMode) {
+	public ScrollableResultsImplementor scroll(ScrollMode scrollMode) {
 		beforeQuery();
 		try {
 			return doScroll( scrollMode );
 		}
 		finally {
 			afterQuery();
 		}
 	}
 
-	protected ScrollableResults doScroll(ScrollMode scrollMode) {
+	protected ScrollableResultsImplementor doScroll(ScrollMode scrollMode) {
 		QueryParameters queryParameters = getQueryParameters();
 		queryParameters.setScrollMode( scrollMode );
 		return getProducer().scroll(
 				queryParameterBindings.expandListValuedParameters( getQueryString(), getProducer() ),
 				queryParameters
 		);
 	}
 
 	@Override
+	@SuppressWarnings("unchecked")
+	public Stream<R> stream() {
+		final ScrollableResultsImplementor scrollableResults = scroll( ScrollMode.FORWARD_ONLY );
+		final ScrollableResultsIterator<R> iterator = new ScrollableResultsIterator<>( scrollableResults );
+		final Spliterator<R> spliterator = Spliterators.spliteratorUnknownSize( iterator, Spliterator.NONNULL );
+
+		final Stream<R> stream = StreamSupport.stream( spliterator, false );
+		stream.onClose( scrollableResults::close );
+
+		return stream;
+	}
+
+	@Override
 	public List<R> list() {
 		beforeQuery();
 		try {
 			return doList();
 		}
 		catch (QueryExecutionRequestException he) {
 			throw new IllegalStateException( he );
 		}
 		catch (TypeMismatchException e) {
 			throw new IllegalArgumentException( e );
 		}
 		catch (HibernateException he) {
 			throw getExceptionConverter().convert( he );
 		}
 		finally {
 			afterQuery();
 		}
 	}
 
 	protected boolean isCallable() {
 		return false;
 	}
 
 	@SuppressWarnings("unchecked")
 	protected List<R> doList() {
 		if ( lockOptions.getLockMode() != null && lockOptions.getLockMode() != LockMode.NONE ) {
 			if ( !getProducer().isTransactionInProgress() ) {
 				throw new TransactionRequiredException( "no transaction is in progress" );
 			}
 		}
 
 		return getProducer().list(
 				queryParameterBindings.expandListValuedParameters( getQueryString(), getProducer() ),
 				getQueryParameters()
 		);
 	}
 
 	public QueryParameterBindingsImpl getQueryParameterBindings() {
 		return queryParameterBindings;
 	}
 
 	@Override
 	public R uniqueResult() {
 		return uniqueElement( list() );
 	}
 
 	public static <R> R uniqueElement(List<R> list) throws NonUniqueResultException {
 		int size = list.size();
 		if ( size == 0 ) {
 			return null;
 		}
 		R first = list.get( 0 );
 		for ( int i = 1; i < size; i++ ) {
 			if ( list.get( i ) != first ) {
 				throw new NonUniqueResultException( list.size() );
 			}
 		}
 		return first;
 	}
 
 	@Override
 	public int executeUpdate() throws HibernateException {
 		if ( ! getProducer().isTransactionInProgress() ) {
 			throw getProducer().getExceptionConverter().convert(
 					new TransactionRequiredException(
 							"Executing an update/delete query"
 					)
 			);
 		}
 		beforeQuery();
 		try {
 			return doExecuteUpdate();
 		}
 		catch ( QueryExecutionRequestException e) {
 			throw new IllegalStateException( e );
 		}
 		catch( TypeMismatchException e ) {
 			throw new IllegalArgumentException( e );
 		}
 		catch ( HibernateException e) {
 			if ( getProducer().getFactory().getSessionFactoryOptions().isJpaBootstrap() ) {
 				throw getExceptionConverter().convert( e );
 			}
 			else {
 				throw e;
 			}
 		}
 		finally {
 			afterQuery();
 		}
 	}
 
 	protected int doExecuteUpdate() {
 		return getProducer().executeUpdate(
 				queryParameterBindings.expandListValuedParameters( getQueryString(), getProducer() ),
 				getQueryParameters()
 		);
 	}
 
 	protected String resolveEntityName(Object val) {
 		if ( val == null ) {
 			throw new IllegalArgumentException( "entity for parameter binding cannot be null" );
 		}
 		return getProducer().bestGuessEntityName( val );
 	}
 
 	@Override
 	public void setOptionalEntityName(String optionalEntityName) {
 		this.optionalEntityName = optionalEntityName;
 	}
 
 	@Override
 	public void setOptionalId(Serializable optionalId) {
 		this.optionalId = optionalId;
 	}
 
 	@Override
 	public void setOptionalObject(Object optionalObject) {
 		this.optionalObject = optionalObject;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Type determineProperBooleanType(String name, Object value, Type defaultType) {
 		final QueryParameterBinding binding = getQueryParameterBindings().getBinding( name );
 		return binding.getBindType() != null
 				? binding.getBindType()
 				: defaultType;
 	}
 
 	@Override
 	public Type determineProperBooleanType(int position, Object value, Type defaultType) {
 		final QueryParameterBinding binding = getQueryParameterBindings().getBinding( position );
 		return binding.getBindType() != null
 				? binding.getBindType()
 				: defaultType;
 	}
 
 	private boolean isSelect() {
 		return getProducer().getFactory().getQueryPlanCache()
 				.getHQLQueryPlan( getQueryString(), false, Collections.<String, Filter>emptyMap() )
 				.isSelect();
 	}
 
 	protected ExceptionConverter getExceptionConverter(){
 		return producer.getExceptionConverter();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/query/internal/CollectionFilterImpl.java b/hibernate-core/src/main/java/org/hibernate/query/internal/CollectionFilterImpl.java
index 8bc5086df2..ee6120a308 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/internal/CollectionFilterImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/internal/CollectionFilterImpl.java
@@ -1,120 +1,121 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.query.internal;
 
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.HibernateException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.query.Query;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.type.Type;
 
 /**
  * implementation of the <tt>Query</tt> interface for collection filters
  *
  * @author Gavin King
  */
 public class CollectionFilterImpl extends org.hibernate.query.internal.AbstractProducedQuery {
 	private final String queryString;
 	private Object collection;
 
 	public CollectionFilterImpl(
 			String queryString,
 			Object collection,
 			SharedSessionContractImplementor session,
 			ParameterMetadataImpl parameterMetadata) {
 		super( session, parameterMetadata );
 		this.queryString = queryString;
 		this.collection = collection;
 	}
 
 	@Override
 	protected boolean isNativeQuery() {
 		return false;
 	}
 
 	@Override
 	public String getQueryString() {
 		return queryString;
 	}
 
 	@Override
 	public Iterator iterate() throws HibernateException {
 		getQueryParameterBindings().verifyParametersBound( false );
 		return getProducer().iterateFilter(
 				collection,
 				getQueryParameterBindings().expandListValuedParameters( getQueryString(), getProducer() ),
 				getQueryParameters()
 		);
 	}
 
 	@Override
 	public List list() throws HibernateException {
 		getQueryParameterBindings().verifyParametersBound( false );
 		return getProducer().listFilter(
 				collection,
 				getQueryParameterBindings().expandListValuedParameters( getQueryString(), getProducer() ),
 				getQueryParameters()
 		);
 	}
 
 	@Override
-	public ScrollableResults scroll() throws HibernateException {
+	public ScrollableResultsImplementor scroll() throws HibernateException {
 		throw new UnsupportedOperationException( "Can't scroll filters" );
 	}
 
 	@Override
-	public ScrollableResults scroll(ScrollMode scrollMode) {
+	public ScrollableResultsImplementor scroll(ScrollMode scrollMode) {
 		throw new UnsupportedOperationException( "Can't scroll filters" );
 	}
 
 	@Override
 	protected Type[] getPositionalParameterTypes() {
 		final Type[] explicitParameterTypes = super.getPositionalParameterTypes();
 		final Type[] expandedParameterTypes = new Type[ explicitParameterTypes.length + 1 ];
 
 		// previously this logic would only add an additional slot in the array, not fill it.  carry that logic here, for now
 		System.arraycopy( explicitParameterTypes, 0, expandedParameterTypes, 1, explicitParameterTypes.length );
 
 		return expandedParameterTypes;
 	}
 
 	@SuppressWarnings("deprecation")
 	protected Object[] getPositionalParameterValues() {
 		final Object[] explicitParameterValues = super.getPositionalParameterValues();
 		final Object[] expandedParameterValues = new Object[ explicitParameterValues.length + 1 ];
 
 		// previously this logic would only add an additional slot in the array, not fill it.  carry that logic here, for now
 		System.arraycopy( explicitParameterValues, 0, expandedParameterValues, 1, explicitParameterValues.length );
 
 		return expandedParameterValues;
 	}
 
 	@Override
 	public Type[] getReturnTypes() {
 		return getProducer().getFactory().getReturnTypes( getQueryString() );
 	}
 
 	@Override
 	public String[] getReturnAliases() {
 		return getProducer().getFactory().getReturnAliases( getQueryString() );
 	}
 
 	@Override
 	public Query setEntity(int position, Object val) {
 		return setParameter( position, val, getProducer().getFactory().getTypeHelper().entity( resolveEntityName( val ) ) );
 	}
 
 	@Override
 	public Query setEntity(String name, Object val) {
 		return setParameter( name, val, getProducer().getFactory().getTypeHelper().entity( resolveEntityName( val ) ) );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/query/internal/NativeQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/query/internal/NativeQueryImpl.java
index fac13d4b46..3c22019de7 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/internal/NativeQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/internal/NativeQueryImpl.java
@@ -1,716 +1,717 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.query.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Calendar;
 import java.util.Collection;
 import java.util.Date;
 import java.util.List;
 import java.util.Map;
 import javax.persistence.FlushModeType;
 import javax.persistence.LockModeType;
 import javax.persistence.Parameter;
 import javax.persistence.TemporalType;
 
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryConstructorReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.query.NativeQuery;
 import org.hibernate.query.ParameterMetadata;
 import org.hibernate.query.QueryParameter;
 import org.hibernate.query.spi.NativeQueryImplementor;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 import static org.hibernate.jpa.QueryHints.HINT_NATIVE_LOCKMODE;
 
 /**
  * @author Steve Ebersole
  */
 public class NativeQueryImpl<T> extends AbstractProducedQuery<T> implements NativeQueryImplementor<T> {
 	private final String sqlString;
 	private List<NativeSQLQueryReturn> queryReturns;
 	private List<NativeQueryReturnBuilder> queryReturnBuilders;
 	private boolean autoDiscoverTypes;
 
 	private Collection<String> querySpaces;
 
 	private final boolean callable;
 	private final LockOptions lockOptions = new LockOptions();
 	private Serializable collectionKey;
 
 	/**
 	 * Constructs a NativeQueryImpl given a sql query defined in the mappings.
 	 *
 	 * @param queryDef The representation of the defined <sql-query/>.
 	 * @param session The session to which this NativeQuery belongs.
 	 * @param parameterMetadata Metadata about parameters found in the query.
 	 */
 	public NativeQueryImpl(
 			NamedSQLQueryDefinition queryDef,
 			SharedSessionContractImplementor session,
 			ParameterMetadata parameterMetadata) {
 		super( session, parameterMetadata );
 
 		this.sqlString = queryDef.getQueryString();
 		this.callable = queryDef.isCallable();
 		this.querySpaces = queryDef.getQuerySpaces();
 
 		if ( queryDef.getResultSetRef() != null ) {
 			ResultSetMappingDefinition definition = session.getFactory()
 					.getNamedQueryRepository()
 					.getResultSetMappingDefinition( queryDef.getResultSetRef() );
 			if ( definition == null ) {
 				throw new MappingException(
 						"Unable to find resultset-ref definition: " +
 								queryDef.getResultSetRef()
 				);
 			}
 			this.queryReturns = new ArrayList<>( Arrays.asList( definition.getQueryReturns() ) );
 		}
 		else if ( queryDef.getQueryReturns() != null && queryDef.getQueryReturns().length > 0 ) {
 			this.queryReturns = new ArrayList<>( Arrays.asList( queryDef.getQueryReturns() ) );
 		}
 		else {
 			this.queryReturns = new ArrayList<>();
 		}
 	}
 
 	public NativeQueryImpl(
 			String sqlString,
 			boolean callable,
 			SharedSessionContractImplementor session,
 			ParameterMetadata sqlParameterMetadata) {
 		super( session, sqlParameterMetadata );
 
 		this.queryReturns = new ArrayList<>();
 		this.sqlString = sqlString;
 		this.callable = callable;
 		this.querySpaces = new ArrayList<>();
 	}
 
 	@Override
 	public NativeQuery setResultSetMapping(String name) {
 		ResultSetMappingDefinition mapping = getProducer().getFactory().getNamedQueryRepository().getResultSetMappingDefinition( name );
 		if ( mapping == null ) {
 			throw new MappingException( "Unknown SqlResultSetMapping [" + name + "]" );
 		}
 		NativeSQLQueryReturn[] returns = mapping.getQueryReturns();
 		queryReturns.addAll( Arrays.asList( returns ) );
 		return this;
 	}
 
 	@Override
 	public String getQueryString() {
 		return sqlString;
 	}
 
 	@Override
 	public boolean isCallable() {
 		return callable;
 	}
 
 	@Override
 	public List<NativeSQLQueryReturn> getQueryReturns() {
 		prepareQueryReturnsIfNecessary();
 		return queryReturns;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	protected List<T> doList() {
 		return getProducer().list(
 				generateQuerySpecification(),
 				getQueryParameters()
 		);
 	}
 
 	private NativeSQLQuerySpecification generateQuerySpecification() {
 		return new NativeSQLQuerySpecification(
 				getQueryParameterBindings().expandListValuedParameters( getQueryString(), getProducer() ),
 				queryReturns.toArray( new NativeSQLQueryReturn[queryReturns.size()] ),
 				querySpaces
 		);
 	}
 
 	@Override
 	public QueryParameters getQueryParameters() {
 		final QueryParameters queryParameters = super.getQueryParameters();
 		queryParameters.setCallable( callable );
 		queryParameters.setAutoDiscoverScalarTypes( autoDiscoverTypes );
 		if ( collectionKey != null ) {
 			queryParameters.setCollectionKeys( new Serializable[] {collectionKey} );
 		}
 		return queryParameters;
 	}
 
 	private void prepareQueryReturnsIfNecessary() {
 		if ( queryReturnBuilders != null ) {
 			if ( !queryReturnBuilders.isEmpty() ) {
 				if ( queryReturns != null ) {
 					queryReturns.clear();
 					queryReturns = null;
 				}
 				queryReturns = new ArrayList<>();
 				for ( NativeQueryReturnBuilder builder : queryReturnBuilders ) {
 					queryReturns.add( builder.buildReturn() );
 				}
 				queryReturnBuilders.clear();
 			}
 			queryReturnBuilders = null;
 		}
 	}
 
 	@Override
-	protected ScrollableResults doScroll(ScrollMode scrollMode) {
+	protected ScrollableResultsImplementor doScroll(ScrollMode scrollMode) {
 		return getProducer().scroll(
 				generateQuerySpecification(),
 				getQueryParameters()
 		);
 	}
 
 	@Override
 	protected void beforeQuery() {
 		prepareQueryReturnsIfNecessary();
 		boolean noReturns = queryReturns == null || queryReturns.isEmpty();
 		if ( noReturns ) {
 			this.autoDiscoverTypes = true;
 		}
 		else {
 			for ( NativeSQLQueryReturn queryReturn : queryReturns ) {
 				if ( queryReturn instanceof NativeSQLQueryScalarReturn ) {
 					NativeSQLQueryScalarReturn scalar = (NativeSQLQueryScalarReturn) queryReturn;
 					if ( scalar.getType() == null ) {
 						autoDiscoverTypes = true;
 						break;
 					}
 				}
 				else if ( NativeSQLQueryConstructorReturn.class.isInstance( queryReturn ) ) {
 					autoDiscoverTypes = true;
 					break;
 				}
 			}
 		}
 
 		super.beforeQuery();
 
 
 		if ( getSynchronizedQuerySpaces() != null && !getSynchronizedQuerySpaces().isEmpty() ) {
 			// The application defined query spaces on the Hibernate native SQLQuery which means the query will already
 			// perform a partial flush according to the defined query spaces, no need to do a full flush.
 			return;
 		}
 
 		// otherwise we need to flush.  the query itself is not required to execute in a transaction; if there is
 		// no transaction, the flush would throw a TransactionRequiredException which would potentially break existing
 		// apps, so we only do the flush if a transaction is in progress.
 		//
 		// NOTE : this was added for JPA initially.  Perhaps we want to only do this from JPA usage?
 		if ( shouldFlush() ) {
 			getProducer().flush();
 		}
 	}
 
 	private boolean shouldFlush() {
 		if ( getProducer().isTransactionInProgress() ) {
 			FlushMode effectiveFlushMode = getHibernateFlushMode();
 			if ( effectiveFlushMode == null ) {
 				effectiveFlushMode = getProducer().getHibernateFlushMode();
 			}
 
 			if ( effectiveFlushMode == FlushMode.ALWAYS ) {
 				return true;
 			}
 
 			if ( effectiveFlushMode == FlushMode.AUTO ) {
 				if ( getProducer().getFactory().getSessionFactoryOptions().isJpaBootstrap() ) {
 					return true;
 				}
 			}
 		}
 
 		return false;
 	}
 
 	protected int doExecuteUpdate() {
 		return getProducer().executeNativeUpdate(
 				generateQuerySpecification(),
 				getQueryParameters()
 		);
 	}
 
 	@Override
 	public NativeQueryImplementor setCollectionKey(Serializable key) {
 		this.collectionKey = key;
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addScalar(String columnAlias) {
 		return addScalar( columnAlias, null );
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addScalar(String columnAlias, Type type) {
 		addReturnBuilder(
 				new NativeQueryReturnBuilder() {
 					public NativeSQLQueryReturn buildReturn() {
 						return new NativeSQLQueryScalarReturn( columnAlias, type );
 					}
 				}
 		);
 		return this;
 	}
 
 	protected void addReturnBuilder(NativeQueryReturnBuilder builder) {
 		if ( queryReturnBuilders == null ) {
 			queryReturnBuilders = new ArrayList<>();
 		}
 
 		queryReturnBuilders.add( builder );
 	}
 
 	@Override
 	public RootReturn addRoot(String tableAlias, String entityName) {
 		NativeQueryReturnBuilderRootImpl builder = new NativeQueryReturnBuilderRootImpl( tableAlias, entityName );
 		addReturnBuilder( builder );
 		return builder;
 	}
 
 	@Override
 	public RootReturn addRoot(String tableAlias, Class entityType) {
 		return addRoot( tableAlias, entityType.getName() );
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addEntity(String entityName) {
 		return addEntity( StringHelper.unqualify( entityName ), entityName );
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addEntity(String tableAlias, String entityName) {
 		addRoot( tableAlias, entityName );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addEntity(String tableAlias, String entityName, LockMode lockMode) {
 		addRoot( tableAlias, entityName ).setLockMode( lockMode );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addEntity(Class entityType) {
 		return addEntity( entityType.getName() );
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addEntity(String tableAlias, Class entityClass) {
 		return addEntity( tableAlias, entityClass.getName() );
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addEntity(String tableAlias, Class entityClass, LockMode lockMode) {
 		return addEntity( tableAlias, entityClass.getName(), lockMode );
 	}
 
 	@Override
 	public FetchReturn addFetch(String tableAlias, String ownerTableAlias, String joinPropertyName) {
 		NativeQueryReturnBuilderFetchImpl builder = new NativeQueryReturnBuilderFetchImpl( tableAlias, ownerTableAlias, joinPropertyName );
 		addReturnBuilder( builder );
 		return builder;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addJoin(String tableAlias, String path) {
 		createFetchJoin( tableAlias, path );
 		return this;
 	}
 
 	private FetchReturn createFetchJoin(String tableAlias, String path) {
 		int loc = path.indexOf( '.' );
 		if ( loc < 0 ) {
 			throw new QueryException( "not a property path: " + path );
 		}
 		final String ownerTableAlias = path.substring( 0, loc );
 		final String joinedPropertyName = path.substring( loc + 1 );
 		return addFetch( tableAlias, ownerTableAlias, joinedPropertyName );
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addJoin(String tableAlias, String ownerTableAlias, String joinPropertyName) {
 		addFetch( tableAlias, ownerTableAlias, joinPropertyName );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addJoin(String tableAlias, String path, LockMode lockMode) {
 		createFetchJoin( tableAlias, path ).setLockMode( lockMode );
 		return this;
 	}
 
 	@Override
 	public String[] getReturnAliases() {
 		throw new UnsupportedOperationException( "Native (SQL) queries do not support returning aliases" );
 	}
 
 	@Override
 	public Type[] getReturnTypes() {
 		throw new UnsupportedOperationException( "Native (SQL) queries do not support returning 'return types'" );
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setEntity(int position, Object val) {
 		setParameter( position, val, getProducer().getFactory().getTypeHelper().entity( resolveEntityName( val ) ) );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setEntity(String name, Object val) {
 		setParameter( name, val, getProducer().getFactory().getTypeHelper().entity( resolveEntityName( val ) ) );
 		return this;
 	}
 
 	@Override
 	public Collection<String> getSynchronizedQuerySpaces() {
 		return querySpaces;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addSynchronizedQuerySpace(String querySpace) {
 		addQuerySpaces( querySpace );
 		return this;
 	}
 
 	protected void addQuerySpaces(String... spaces) {
 		if ( spaces != null ) {
 			if ( querySpaces == null ) {
 				querySpaces = new ArrayList<>();
 			}
 			querySpaces.addAll( Arrays.asList( (String[]) spaces ) );
 		}
 	}
 
 	protected void addQuerySpaces(Serializable... spaces) {
 		if ( spaces != null ) {
 			if ( querySpaces == null ) {
 				querySpaces = new ArrayList<>();
 			}
 			querySpaces.addAll( Arrays.asList( (String[]) spaces ) );
 		}
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addSynchronizedEntityName(String entityName) throws MappingException {
 		addQuerySpaces( getProducer().getFactory().getMetamodel().entityPersister( entityName ).getQuerySpaces() );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addSynchronizedEntityClass(Class entityClass) throws MappingException {
 		addQuerySpaces( getProducer().getFactory().getMetamodel().entityPersister( entityClass.getName() ).getQuerySpaces() );
 		return this;
 	}
 
 	@Override
 	protected boolean isNativeQuery() {
 		return true;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setHibernateFlushMode(FlushMode flushMode) {
 		super.setHibernateFlushMode( flushMode );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setFlushMode(FlushMode flushMode) {
 		super.setFlushMode( flushMode );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setFlushMode(FlushModeType flushModeType) {
 		super.setFlushMode( flushModeType );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setCacheMode(CacheMode cacheMode) {
 		super.setCacheMode( cacheMode );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setCacheable(boolean cacheable) {
 		super.setCacheable( cacheable );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setCacheRegion(String cacheRegion) {
 		super.setCacheRegion( cacheRegion );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setTimeout(int timeout) {
 		super.setTimeout( timeout );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setFetchSize(int fetchSize) {
 		super.setFetchSize( fetchSize );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setReadOnly(boolean readOnly) {
 		super.setReadOnly( readOnly );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setLockOptions(LockOptions lockOptions) {
 		super.setLockOptions( lockOptions );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setLockMode(String alias, LockMode lockMode) {
 		super.setLockMode( alias, lockMode );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setLockMode(LockModeType lockModeType) {
 		throw new IllegalStateException( "Illegal attempt to set lock mode on a native SQL query" );
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setComment(String comment) {
 		super.setComment( comment );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> addQueryHint(String hint) {
 		super.addQueryHint( hint );
 		return this;
 	}
 
 	@Override
 	protected void collectHints(Map<String, Object> hints) {
 		super.collectHints( hints );
 
 		putIfNotNull( hints, HINT_NATIVE_LOCKMODE, getLockOptions().getLockMode() );
 	}
 
 	@Override
 	protected boolean applyNativeQueryLockMode(Object value) {
 		if ( LockMode.class.isInstance( value ) ) {
 			applyHibernateLockModeHint( (LockMode) value );
 		}
 		else if ( LockModeType.class.isInstance( value ) ) {
 			applyLockModeTypeHint( (LockModeType) value );
 		}
 		else {
 			throw new IllegalArgumentException(
 					String.format(
 							"Native lock-mode hint [%s] must specify %s or %s.  Encountered type : %s",
 							HINT_NATIVE_LOCKMODE,
 							LockMode.class.getName(),
 							LockModeType.class.getName(),
 							value.getClass().getName()
 					)
 			);
 		}
 
 		return true;
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public NativeQueryImplementor<T> setParameter(QueryParameter parameter, Object value) {
 		super.setParameter( (Parameter<Object>) parameter, value );
 		return this;
 	}
 
 	@Override
 	public <P> NativeQueryImplementor<T> setParameter(Parameter<P> parameter, P value) {
 		super.setParameter( parameter, value );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setParameter(String name, Object value) {
 		super.setParameter( name, value );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setParameter(int position, Object value) {
 		super.setParameter( position, value );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setParameter(QueryParameter parameter, Object value, Type type) {
 		super.setParameter( parameter, value, type );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setParameter(String name, Object value, Type type) {
 		super.setParameter( name, value, type );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor<T> setParameter(int position, Object value, Type type) {
 		super.setParameter( position, value, type );
 		return this;
 	}
 
 	@Override
 	public <P> NativeQueryImplementor<T> setParameter(QueryParameter<P> parameter, P value, TemporalType temporalType) {
 		super.setParameter( parameter, value, temporalType );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setParameter(String name, Object value, TemporalType temporalType) {
 		super.setParameter( name, value, temporalType );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setParameter(int position, Object value, TemporalType temporalType) {
 		super.setParameter( position, value, temporalType );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setParameterList(QueryParameter parameter, Collection values) {
 		super.setParameterList( parameter, values );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setParameterList(String name, Collection values) {
 		super.setParameterList( name, values );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setParameterList(String name, Collection values, Type type) {
 		super.setParameterList( name, values, type );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setParameterList(String name, Object[] values, Type type) {
 		super.setParameterList( name, values, type );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setParameterList(String name, Object[] values) {
 		super.setParameterList( name, values );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setParameter(Parameter param, Calendar value, TemporalType temporalType) {
 		super.setParameter( param, value, temporalType );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setParameter(Parameter param, Date value, TemporalType temporalType) {
 		super.setParameter( param, value, temporalType );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setParameter(String name, Calendar value, TemporalType temporalType) {
 		super.setParameter( name, value, temporalType );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setParameter(String name, Date value, TemporalType temporalType) {
 		super.setParameter( name, value, temporalType );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setParameter(int position, Calendar value, TemporalType temporalType) {
 		super.setParameter( position, value, temporalType );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setParameter(int position, Date value, TemporalType temporalType) {
 		super.setParameter( position, value, temporalType );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setResultTransformer(ResultTransformer transformer) {
 		super.setResultTransformer( transformer );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setProperties(Map map) {
 		super.setProperties( map );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setProperties(Object bean) {
 		super.setProperties( bean );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setMaxResults(int maxResult) {
 		super.setMaxResults( maxResult );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setFirstResult(int startPosition) {
 		super.setFirstResult( startPosition );
 		return this;
 	}
 
 	@Override
 	public NativeQueryImplementor setHint(String hintName, Object value) {
 		super.setHint( hintName, value );
 		return this;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/query/internal/ScrollableResultsIterator.java b/hibernate-core/src/main/java/org/hibernate/query/internal/ScrollableResultsIterator.java
new file mode 100644
index 0000000000..444d78961d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/query/internal/ScrollableResultsIterator.java
@@ -0,0 +1,41 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.query.internal;
+
+import org.hibernate.Incubating;
+import org.hibernate.query.spi.CloseableIterator;
+import org.hibernate.query.spi.ScrollableResultsImplementor;
+
+/**
+ * @author Steve Ebersole
+ *
+ * @since 5.2
+ */
+@Incubating
+public class ScrollableResultsIterator<T> implements CloseableIterator {
+	private final ScrollableResultsImplementor scrollableResults;
+
+	public ScrollableResultsIterator(ScrollableResultsImplementor scrollableResults) {
+		this.scrollableResults = scrollableResults;
+	}
+
+	@Override
+	public void close() {
+		scrollableResults.close();
+	}
+
+	@Override
+	public boolean hasNext() {
+		return !scrollableResults.isClosed() && scrollableResults.next();
+	}
+
+	@Override
+	@SuppressWarnings("unchecked")
+	public T next() {
+		return (T) scrollableResults.get();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/query/spi/CloseableIterator.java b/hibernate-core/src/main/java/org/hibernate/query/spi/CloseableIterator.java
new file mode 100644
index 0000000000..946cae174c
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/query/spi/CloseableIterator.java
@@ -0,0 +1,24 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.query.spi;
+
+import java.util.Iterator;
+
+import org.hibernate.Incubating;
+
+/**
+ * Unification of Iterator and AutoCloseable
+ *
+ * @author Steve Ebersole
+ *
+ * @since 5.2
+ */
+@Incubating
+public interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {
+	@Override
+	void close();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/query/spi/ScrollableResultsImplementor.java b/hibernate-core/src/main/java/org/hibernate/query/spi/ScrollableResultsImplementor.java
new file mode 100644
index 0000000000..c0198cbf82
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/query/spi/ScrollableResultsImplementor.java
@@ -0,0 +1,20 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.query.spi;
+
+import org.hibernate.Incubating;
+import org.hibernate.ScrollableResults;
+
+/**
+ * @author Steve Ebersole
+ *
+ * @since 5.2
+ */
+@Incubating
+public interface ScrollableResultsImplementor extends ScrollableResults {
+	boolean isClosed();
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/stream/basic/BasicStreamTest.java b/hibernate-core/src/test/java/org/hibernate/test/stream/basic/BasicStreamTest.java
new file mode 100644
index 0000000000..9b2612911a
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/stream/basic/BasicStreamTest.java
@@ -0,0 +1,61 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later
+ * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
+ */
+package org.hibernate.test.stream.basic;
+
+import java.util.stream.Stream;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.Table;
+
+import org.hibernate.Session;
+import org.hibernate.boot.MetadataSources;
+import org.hibernate.engine.spi.SessionImplementor;
+
+import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
+import org.junit.Test;
+
+import static org.hamcrest.MatcherAssert.assertThat;
+import static org.hamcrest.core.Is.is;
+
+/**
+ * @author Steve Ebersole
+ */
+public class BasicStreamTest extends BaseNonConfigCoreFunctionalTestCase {
+
+	@Override
+	protected void applyMetadataSources(MetadataSources metadataSources) {
+		super.applyMetadataSources( metadataSources );
+		metadataSources.addAnnotatedClass( MyEntity.class );
+	}
+
+	@Test
+	public void basicStreamTest() {
+		Session session = openSession();
+		session.getTransaction().begin();
+
+		// mainly we want to make sure that closing the Stream releases the ScrollableResults too
+		assertThat( ( (SessionImplementor) session ).getJdbcCoordinator().getLogicalConnection().getResourceRegistry().hasRegisteredResources(), is( false ) );
+		final Stream<MyEntity> stream = session.createQuery( "from MyEntity", MyEntity.class ).stream();
+		assertThat( ( (SessionImplementor) session ).getJdbcCoordinator().getLogicalConnection().getResourceRegistry().hasRegisteredResources(), is( true ) );
+		stream.forEach( System.out::println );
+		assertThat( ( (SessionImplementor) session ).getJdbcCoordinator().getLogicalConnection().getResourceRegistry().hasRegisteredResources(), is( true ) );
+		stream.close();
+		assertThat( ( (SessionImplementor) session ).getJdbcCoordinator().getLogicalConnection().getResourceRegistry().hasRegisteredResources(), is( false ) );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Entity(name = "MyEntity")
+	@Table(name="MyEntity")
+	public static class MyEntity {
+		@Id
+		public Integer id;
+		public String name;
+	}
+
+}
