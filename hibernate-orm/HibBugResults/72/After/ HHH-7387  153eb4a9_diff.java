diff --git a/hibernate-core/src/main/java/org/hibernate/BasicQueryContract.java b/hibernate-core/src/main/java/org/hibernate/BasicQueryContract.java
new file mode 100644
index 0000000000..524299c4ac
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/BasicQueryContract.java
@@ -0,0 +1,215 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate;
+
+import org.hibernate.type.Type;
+
+/**
+ * Defines the aspects of query definition that apply to all forms of querying.
+ *
+ * @author Steve Ebersole
+ */
+public interface BasicQueryContract {
+	/**
+	 * Obtain the FlushMode in effect for this query.  By default, the query inherits the FlushMode of the Session
+	 * from which is originates.
+	 *
+	 * @return The query FlushMode.
+	 *
+	 * @see Session#getFlushMode()
+	 * @see FlushMode
+	 */
+	public FlushMode getFlushMode();
+
+	/**
+	 * (Re)set the current FlushMode in effect for this query.
+	 *
+	 * @param flushMode The new FlushMode to use.
+	 *
+	 * @see #getFlushMode()
+	 */
+	public BasicQueryContract setFlushMode(FlushMode flushMode);
+
+	/**
+	 * Obtain the CacheMode in effect for this query.  By default, the query inherits the CacheMode of the Session
+	 * from which is originates.
+	 *
+	 * NOTE: The CacheMode here only effects reading/writing of the query cache, not the
+	 * entity/collection caches.
+	 *
+	 * @return The query CacheMode.
+	 *
+	 * @see Session#getCacheMode()
+	 * @see CacheMode
+	 */
+	public CacheMode getCacheMode();
+
+	/**
+	 * (Re)set the current CacheMode in effect for this query.
+	 *
+	 * @param cacheMode The new CacheMode to use.
+	 *
+	 * @see #getCacheMode()
+	 */
+	public BasicQueryContract setCacheMode(CacheMode cacheMode);
+
+	/**
+	 * Are the results of this query eligible for second level query caching?  This is different that second level
+	 * caching of any returned entities and collections.
+	 *
+	 * NOTE: the query being "eligible" for caching does not necessarily mean its results will be cached.  Second level
+	 * query caching still has to be enabled on the {@link SessionFactory} for this to happen.  Usually that is
+	 * controlled by the {@code hibernate.cache.use_query_cache} configuration setting.
+	 *
+	 * @return {@code true} if the query results are eligible for caching, {@code false} otherwise.
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#USE_QUERY_CACHE
+	 */
+	public boolean isCacheable();
+
+	/**
+	 * Enable/disable second level query (result) caching for this query.
+	 *
+	 * @param cacheable Should the query results be cacheable?
+	 *
+	 * @see #isCacheable
+	 */
+	public BasicQueryContract setCacheable(boolean cacheable);
+
+	/**
+	 * Obtain the name of the second level query cache region in which query results will be stored (if they are
+	 * cached, see the discussion on {@link #isCacheable()} for more information).  {@code null} indicates that the
+	 * default region should be used.
+	 *
+	 * @return The specified cache region name into which query results should be placed; {@code null} indicates
+	 * the default region.
+	 */
+	public String getCacheRegion();
+
+	/**
+	 * Set the name of the cache region where query results should be cached (if cached at all).
+	 *
+	 * @param cacheRegion the name of a query cache region, or {@code null} to indicate that the default region
+	 * should be used.
+	 *
+	 * @see #getCacheRegion()
+	 */
+	public BasicQueryContract setCacheRegion(String cacheRegion);
+
+	/**
+	 * Obtain the query timeout <b>in seconds</b>.  This value is eventually passed along to the JDBC query via
+	 * {@link java.sql.Statement#setQueryTimeout(int)}.  Zero indicates no timeout.
+	 *
+	 * @return The timeout <b>in seconds</b>
+	 *
+	 * @see java.sql.Statement#getQueryTimeout()
+	 * @see java.sql.Statement#setQueryTimeout(int)
+	 */
+	public Integer getTimeout();
+
+	/**
+	 * Set the query timeout <b>in seconds</b>.
+	 *
+	 * NOTE it is important to understand that any value set here is eventually passed directly through to the JDBC
+	 * Statement which expressly disallows negative values.  So negative values should be avoided as a general rule.
+	 *
+	 * @param timeout the timeout <b>in seconds</b>
+	 *
+	 * @see #getTimeout()
+	 */
+	public BasicQueryContract setTimeout(int timeout);
+
+	/**
+	 * Obtain the JDBC fetch size hint in effect for this query.  This value is eventually passed along to the JDBC
+	 * query via {@link java.sql.Statement#setFetchSize(int)}.  As defined b y JDBC, this value is a hint to the
+	 * driver to indicate how many rows to fetch from the database when more rows are needed.
+	 *
+	 * NOTE : JDBC expressly defines this value as a hint.  It may or may not have any effect on the actual
+	 * query execution and ResultSet processing depending on the driver.
+	 *
+	 * @return The timeout <b>in seconds</b>
+	 *
+	 * @see java.sql.Statement#getFetchSize()
+	 * @see java.sql.Statement#setFetchSize(int)
+	 */
+	public Integer getFetchSize();
+
+	/**
+	 * Sets a JDBC fetch size hint for the query.
+	 *
+	 * @param fetchSize the fetch size hint
+	 *
+	 * @see #getFetchSize()
+	 */
+	public BasicQueryContract setFetchSize(int fetchSize);
+
+	/**
+	 * Should entities and proxies loaded by this Query be put in read-only mode? If the
+	 * read-only/modifiable setting was not initialized, then the default
+	 * read-only/modifiable setting for the persistence context is returned instead.
+	 * @see Query#setReadOnly(boolean)
+	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
+	 *
+	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
+	 * query that existed in the session before the query was executed.
+	 *
+	 * @return true, entities and proxies loaded by the query will be put in read-only mode
+	 *         false, entities and proxies loaded by the query will be put in modifiable mode
+	 */
+	public boolean isReadOnly();
+
+	/**
+	 * Set the read-only/modifiable mode for entities and proxies
+	 * loaded by this Query. This setting overrides the default setting
+	 * for the persistence context.
+	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
+	 *
+	 * To set the default read-only/modifiable setting used for
+	 * entities and proxies that are loaded into the session:
+	 * @see org.hibernate.engine.spi.PersistenceContext#setDefaultReadOnly(boolean)
+	 * @see org.hibernate.Session#setDefaultReadOnly(boolean)
+	 *
+	 * Read-only entities are not dirty-checked and snapshots of persistent
+	 * state are not maintained. Read-only entities can be modified, but
+	 * changes are not persisted.
+	 *
+	 * When a proxy is initialized, the loaded entity will have the same
+	 * read-only/modifiable setting as the uninitialized
+	 * proxy has, regardless of the session's current setting.
+	 *
+	 * The read-only/modifiable setting has no impact on entities/proxies
+	 * returned by the query that existed in the session before the query was executed.
+	 *
+	 * @param readOnly true, entities and proxies loaded by the query will be put in read-only mode
+	 *                 false, entities and proxies loaded by the query will be put in modifiable mode
+	 */
+	public BasicQueryContract setReadOnly(boolean readOnly);
+
+	/**
+	 * Return the Hibernate types of the query results.
+	 *
+	 * @return an array of types
+	 */
+	public Type[] getReturnTypes() throws HibernateException;
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/Query.java b/hibernate-core/src/main/java/org/hibernate/Query.java
index 6b08d63043..c8f202b4f7 100644
--- a/hibernate-core/src/main/java/org/hibernate/Query.java
+++ b/hibernate-core/src/main/java/org/hibernate/Query.java
@@ -1,634 +1,474 @@
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
 package org.hibernate;
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.Calendar;
 import java.util.Collection;
 import java.util.Date;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 /**
  * An object-oriented representation of a Hibernate query. A <tt>Query</tt>
  * instance is obtained by calling <tt>Session.createQuery()</tt>. This
  * interface exposes some extra functionality beyond that provided by
  * <tt>Session.iterate()</tt> and <tt>Session.find()</tt>:
  * <ul>
  * <li>a particular page of the result set may be selected by calling <tt>
  * setMaxResults(), setFirstResult()</tt>
  * <li>named query parameters may be used
  * <li>the results may be returned as an instance of <tt>ScrollableResults</tt>
  * </ul>
  * <br>
  * Named query parameters are tokens of the form <tt>:name</tt> in the
  * query string. A value is bound to the <tt>integer</tt> parameter
  * <tt>:foo</tt> by calling<br>
  * <br>
  * <tt>setParameter("foo", foo, Hibernate.INTEGER);</tt><br>
  * <br>
  * for example. A name may appear multiple times in the query string.<br>
  * <br>
  * JDBC-style <tt>?</tt> parameters are also supported. To bind a
  * value to a JDBC-style parameter use a set method that accepts an
  * <tt>int</tt> positional argument (numbered from zero, contrary
  * to JDBC).<br>
  * <br>
  * You may not mix and match JDBC-style parameters and named parameters
  * in the same query.<br>
  * <br>
  * Queries are executed by calling <tt>list()</tt>, <tt>scroll()</tt> or
  * <tt>iterate()</tt>. A query may be re-executed by subsequent invocations.
  * Its lifespan is, however, bounded by the lifespan of the <tt>Session</tt>
  * that created it.<br>
  * <br>
  * Implementors are not intended to be threadsafe.
  *
  * @see org.hibernate.Session#createQuery(java.lang.String)
  * @see org.hibernate.ScrollableResults
  *
  * @author Gavin King
  */
-public interface Query {
+public interface Query extends BasicQueryContract {
 	/**
 	 * Get the query string.
 	 *
 	 * @return the query string
 	 */
 	public String getQueryString();
 
 	/**
 	 * Obtains the limit set on the maximum number of rows to retrieve.  No set limit means there is no limit set
 	 * on the number of rows returned.  Technically both {@code null} and any negative values are interpreted as no
 	 * limit; however, this method should always return null in such case.
 	 *
 	 * @return The
 	 */
 	public Integer getMaxResults();
 
 	/**
 	 * Set the maximum number of rows to retrieve.
 	 *
 	 * @param maxResults the maximum number of rows
 	 *
 	 * @see #getMaxResults()
 	 */
 	public Query setMaxResults(int maxResults);
 
 	/**
 	 * Obtain the value specified (if any) for the first row to be returned from the query results; zero-based.  Used,
 	 * in conjunction with {@link #getMaxResults()} in "paginated queries".  No value specified means the first result
 	 * is returned.  Zero and negative numbers are the same as no setting.
 	 *
 	 * @return The first result number.
 	 */
 	public Integer getFirstResult();
 
 	/**
 	 * Set the first row to retrieve.
 	 *
 	 * @param firstResult a row number, numbered from <tt>0</tt>
 	 *
 	 * @see #getFirstResult()
 	 */
 	public Query setFirstResult(int firstResult);
 
-	/**
-	 * Obtain the FlushMode in effect for this query.  By default, the query inherits the FlushMode of the Session
-	 * from which is originates.
-	 *
-	 * @return The query FlushMode.
-	 *
-	 * @see Session#getFlushMode()
-	 * @see FlushMode
-	 */
-	public FlushMode getFlushMode();
-
-	/**
-	 * (Re)set the current FlushMode in effect for this query.
-	 *
-	 * @param flushMode The new FlushMode to use.
-	 *
-	 * @see #getFlushMode()
-	 */
+	@Override
 	public Query setFlushMode(FlushMode flushMode);
 
-	/**
-	 * Obtain the CacheMode in effect for this query.  By default, the query inherits the CacheMode of the Session
-	 * from which is originates.
-	 *
-	 * NOTE: The CacheMode here only effects reading/writing of the query cache, not the
-	 * entity/collection caches.
-	 *
-	 * @return The query CacheMode.
-	 *
-	 * @see Session#getCacheMode()
-	 * @see CacheMode
-	 */
-	public CacheMode getCacheMode();
-
-	/**
-	 * (Re)set the current CacheMode in effect for this query.
-	 *
-	 * @param cacheMode The new CacheMode to use.
-	 *
-	 * @see #getCacheMode()
-	 */
+	@Override
 	public Query setCacheMode(CacheMode cacheMode);
 
-	/**
-	 * Are the results of this query eligible for second level query caching?  This is different that second level
-	 * caching of any returned entities and collections.
-	 *
-	 * NOTE: the query being "eligible" for caching does not necessarily mean its results will be cached.  Second level
-	 * query caching still has to be enabled on the {@link SessionFactory} for this to happen.  Usually that is
-	 * controlled by the {@code hibernate.cache.use_query_cache} configuration setting.
-	 *
-	 * @return {@code true} if the query results are eligible for caching, {@code false} otherwise.
-	 *
-	 * @see org.hibernate.cfg.AvailableSettings#USE_QUERY_CACHE
-	 */
-	public boolean isCacheable();
-
-	/**
-	 * Enable/disable second level query (result) caching for this query.
-	 *
-	 * @param cacheable Should the query results be cacheable?
-	 *
-	 * @see #isCacheable
-	 */
+	@Override
 	public Query setCacheable(boolean cacheable);
 
-	/**
-	 * Obtain the name of the second level query cache region in which query results will be stored (if they are
-	 * cached, see the discussion on {@link #isCacheable()} for more information).  {@code null} indicates that the
-	 * default region should be used.
-	 *
-	 * @return The specified cache region name into which query results should be placed; {@code null} indicates
-	 * the default region.
-	 */
-	public String getCacheRegion();
-
-	/**
-	 * Set the name of the cache region where query results should be cached (if cached at all).
-	 *
-	 * @param cacheRegion the name of a query cache region, or {@code null} to indicate that the default region
-	 * should be used.
-	 *
-	 * @see #getCacheRegion()
-	 */
+	@Override
 	public Query setCacheRegion(String cacheRegion);
 
-	/**
-	 * Obtain the query timeout <b>in seconds</b>.  This value is eventually passed along to the JDBC query via
-	 * {@link java.sql.Statement#setQueryTimeout(int)}.  Zero indicates no timeout.
-	 *
-	 * @return The timeout <b>in seconds</b>
-	 *
-	 * @see java.sql.Statement#getQueryTimeout()
-	 * @see java.sql.Statement#setQueryTimeout(int)
-	 */
-	public Integer getTimeout();
-
-	/**
-	 * Set the query timeout <b>in seconds</b>.
-	 *
-	 * NOTE it is important to understand that any value set here is eventually passed directly through to the JDBC
-	 * Statement which expressly disallows negative values.  So negative values should be avoided as a general rule.
-	 *
-	 * @param timeout the timeout <b>in seconds</b>
-	 *
-	 * @see #getTimeout()
-	 */
+	@Override
 	public Query setTimeout(int timeout);
 
-	/**
-	 * Obtain the JDBC fetch size hint in effect for this query.  This value is eventually passed along to the JDBC
-	 * query via {@link java.sql.Statement#setFetchSize(int)}.  As defined b y JDBC, this value is a hint to the
-	 * driver to indicate how many rows to fetch from the database when more rows are needed.
-	 *
-	 * NOTE : JDBC expressly defines this value as a hint.  It may or may not have any effect on the actual
-	 * query execution and ResultSet processing depending on the driver.
-	 *
-	 * @return The timeout <b>in seconds</b>
-	 *
-	 * @see java.sql.Statement#getFetchSize()
-	 * @see java.sql.Statement#setFetchSize(int)
-	 */
-	public Integer getFetchSize();
-
-	/**
-	 * Sets a JDBC fetch size hint for the query.
-	 *
-	 * @param fetchSize the fetch size hint
-	 *
-	 * @see #getFetchSize()
-	 */
+	@Override
 	public Query setFetchSize(int fetchSize);
 
+	@Override
+	public Query setReadOnly(boolean readOnly);
+
 	/**
 	 * Obtains the LockOptions in effect for this query.
 	 *
 	 * @return The LockOptions
 	 *
 	 * @see LockOptions
 	 */
 	public LockOptions getLockOptions();
 
 	/**
 	 * Set the lock options for the query.  Specifically only the following are taken into consideration:<ol>
 	 *     <li>{@link LockOptions#getLockMode()}</li>
 	 *     <li>{@link LockOptions#getScope()}</li>
 	 *     <li>{@link LockOptions#getTimeOut()}</li>
 	 * </ol>
 	 * For alias-specific locking, use {@link #setLockMode(String, LockMode)}.
 	 *
 	 * @see #getLockOptions()
 	 */
 	public Query setLockOptions(LockOptions lockOptions);
 
 	/**
 	 * Set the LockMode to use for specific alias (as defined in the query's <tt>FROM</tt> clause).
 	 *
 	 * The alias-specific lock modes specified here are added to the query's internal
 	 * {@link #getLockOptions() LockOptions}.
 	 *
 	 * The effect of these alias-specific LockModes is somewhat dependent on the driver/database in use.  Generally
 	 * speaking, for maximum portability, this method should only be used to mark that the rows corresponding to
 	 * the given alias should be included in pessimistic locking ({@link LockMode#PESSIMISTIC_WRITE}).
 	 *
 	 * @param alias a query alias, or <tt>this</tt> for a collection filter
 	 *
 	 * @see #getLockOptions()
 	 */
 	public Query setLockMode(String alias, LockMode lockMode);
 
 	/**
 	 * Obtain the comment currently associated with this query.  Provided SQL commenting is enabled
 	 * (generally by enabling the {@code hibernate.use_sql_comments} config setting), this comment will also be added
 	 * to the SQL query sent to the database.  Often useful for identifying the source of troublesome queries on the
 	 * database side.
 	 *
 	 * @return The comment.
 	 */
 	public String getComment();
 
 	/**
 	 * Set the comment for this query.
 	 *
 	 * @param comment The human-readable comment
 	 *
 	 * @see #getComment()
 	 */
 	public Query setComment(String comment);
 
 	/**
-	 * Should entities and proxies loaded by this Query be put in read-only mode? If the
-	 * read-only/modifiable setting was not initialized, then the default
-	 * read-only/modifiable setting for the persistence context is returned instead.
-	 * @see Query#setReadOnly(boolean)
-	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
-	 *
-	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
-	 * query that existed in the session before the query was executed.
-	 *
-	 * @return true, entities and proxies loaded by the query will be put in read-only mode
-	 *         false, entities and proxies loaded by the query will be put in modifiable mode
-	 */
-	public boolean isReadOnly();
-
-	/**
-	 * Set the read-only/modifiable mode for entities and proxies
-	 * loaded by this Query. This setting overrides the default setting
-	 * for the persistence context.
-	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
-	 *
-	 * To set the default read-only/modifiable setting used for
-	 * entities and proxies that are loaded into the session:
-	 * @see org.hibernate.engine.spi.PersistenceContext#setDefaultReadOnly(boolean)
-	 * @see org.hibernate.Session#setDefaultReadOnly(boolean)
-	 *
-	 * Read-only entities are not dirty-checked and snapshots of persistent
-	 * state are not maintained. Read-only entities can be modified, but
-	 * changes are not persisted.
-	 *
-	 * When a proxy is initialized, the loaded entity will have the same
-	 * read-only/modifiable setting as the uninitialized
-	 * proxy has, regardless of the session's current setting.
-	 *
-	 * The read-only/modifiable setting has no impact on entities/proxies
-	 * returned by the query that existed in the session before the query was executed.
-	 *
-	 * @param readOnly true, entities and proxies loaded by the query will be put in read-only mode
-	 *                 false, entities and proxies loaded by the query will be put in modifiable mode
-	 */
-	public Query setReadOnly(boolean readOnly);
-
-	/**
-	 * Return the Hibernate types of the query result set.
-	 * @return an array of types
-	 */
-	public Type[] getReturnTypes() throws HibernateException;
-
-	/**
 	 * Return the HQL select clause aliases (if any)
 	 * @return an array of aliases as strings
 	 */
 	public String[] getReturnAliases() throws HibernateException;
 
 	/**
 	 * Return the names of all named parameters of the query.
 	 * @return the parameter names, in no particular order
 	 */
 	public String[] getNamedParameters() throws HibernateException;
 
 	/**
 	 * Return the query results as an <tt>Iterator</tt>. If the query
 	 * contains multiple results pre row, the results are returned in
 	 * an instance of <tt>Object[]</tt>.<br>
 	 * <br>
 	 * Entities returned as results are initialized on demand. The first
 	 * SQL query returns identifiers only.<br>
 	 *
 	 * @return the result iterator
 	 * @throws HibernateException
 	 */
 	public Iterator iterate() throws HibernateException;
 
 	/**
 	 * Return the query results as <tt>ScrollableResults</tt>. The
 	 * scrollability of the returned results depends upon JDBC driver
 	 * support for scrollable <tt>ResultSet</tt>s.<br>
 	 *
 	 * @see ScrollableResults
 	 * @return the result iterator
 	 * @throws HibernateException
 	 */
 	public ScrollableResults scroll() throws HibernateException;
 
 	/**
 	 * Return the query results as <tt>ScrollableResults</tt>. The
 	 * scrollability of the returned results depends upon JDBC driver
 	 * support for scrollable <tt>ResultSet</tt>s.<br>
 	 *
 	 * @see ScrollableResults
 	 * @see ScrollMode
 	 * @return the result iterator
 	 * @throws HibernateException
 	 */
 	public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException;
 
 	/**
 	 * Return the query results as a <tt>List</tt>. If the query contains
 	 * multiple results pre row, the results are returned in an instance
 	 * of <tt>Object[]</tt>.
 	 *
 	 * @return the result list
 	 * @throws HibernateException
 	 */
 	public List list() throws HibernateException;
 
 	/**
 	 * Convenience method to return a single instance that matches
 	 * the query, or null if the query returns no results.
 	 *
 	 * @return the single result or <tt>null</tt>
 	 * @throws NonUniqueResultException if there is more than one matching result
 	 */
 	public Object uniqueResult() throws HibernateException;
 
 	/**
 	 * Execute the update or delete statement.
 	 * </p>
 	 * The semantics are compliant with the ejb3 Query.executeUpdate()
 	 * method.
 	 *
 	 * @return The number of entities updated or deleted.
 	 * @throws HibernateException
 	 */
 	public int executeUpdate() throws HibernateException;
 
 	/**
 	 * Bind a value to a JDBC-style query parameter.
 	 * @param position the position of the parameter in the query
 	 * string, numbered from <tt>0</tt>.
 	 * @param val the possibly-null parameter value
 	 * @param type the Hibernate type
 	 */
 	public Query setParameter(int position, Object val, Type type);
 
 	/**
 	 * Bind a value to a named query parameter.
 	 * @param name the name of the parameter
 	 * @param val the possibly-null parameter value
 	 * @param type the Hibernate type
 	 */
 	public Query setParameter(String name, Object val, Type type);
 
 	/**
 	 * Bind a value to a JDBC-style query parameter. The Hibernate type of the parameter is
 	 * first detected via the usage/position in the query and if not sufficient secondly 
 	 * guessed from the class of the given object.
 	 * @param position the position of the parameter in the query
 	 * string, numbered from <tt>0</tt>.
 	 * @param val the non-null parameter value
 	 * @throws org.hibernate.HibernateException if no type could be determined
 	 */
 	public Query setParameter(int position, Object val) throws HibernateException;
 
 	/**
 	 * Bind a value to a named query parameter. The Hibernate type of the parameter is
 	 * first detected via the usage/position in the query and if not sufficient secondly 
 	 * guessed from the class of the given object.
 	 * @param name the name of the parameter
 	 * @param val the non-null parameter value
 	 * @throws org.hibernate.HibernateException if no type could be determined
 	 */
 	public Query setParameter(String name, Object val) throws HibernateException;
 	
 	/**
 	 * Bind values and types to positional parameters.
 	 */
 	public Query setParameters(Object[] values, Type[] types) throws HibernateException;
 
 	/**
 	 * Bind multiple values to a named query parameter. This is useful for binding
 	 * a list of values to an expression such as <tt>foo.bar in (:value_list)</tt>.
 	 * @param name the name of the parameter
 	 * @param vals a collection of values to list
 	 * @param type the Hibernate type of the values
 	 */
 	public Query setParameterList(String name, Collection vals, Type type) throws HibernateException;
 
 	/**
 	 * Bind multiple values to a named query parameter. The Hibernate type of the parameter is
 	 * first detected via the usage/position in the query and if not sufficient secondly 
 	 * guessed from the class of the first object in the collection. This is useful for binding a list of values
 	 * to an expression such as <tt>foo.bar in (:value_list)</tt>.
 	 * @param name the name of the parameter
 	 * @param vals a collection of values to list
 	 */
 	public Query setParameterList(String name, Collection vals) throws HibernateException;
 
 	/**
 	 * Bind multiple values to a named query parameter. This is useful for binding
 	 * a list of values to an expression such as <tt>foo.bar in (:value_list)</tt>.
 	 * @param name the name of the parameter
 	 * @param vals a collection of values to list
 	 * @param type the Hibernate type of the values
 	 */
 	public Query setParameterList(String name, Object[] vals, Type type) throws HibernateException;
 
 	/**
 	 * Bind multiple values to a named query parameter. The Hibernate type of the parameter is
 	 * first detected via the usage/position in the query and if not sufficient secondly 
 	 * guessed from the class of the first object in the array. This is useful for binding a list of values
 	 * to an expression such as <tt>foo.bar in (:value_list)</tt>.
 	 * @param name the name of the parameter
 	 * @param vals a collection of values to list
 	 */
 	public Query setParameterList(String name, Object[] vals) throws HibernateException;
 
 	/**
 	 * Bind the property values of the given bean to named parameters of the query,
 	 * matching property names with parameter names and mapping property types to
 	 * Hibernate types using hueristics.
 	 * @param bean any JavaBean or POJO
 	 */	
 	public Query setProperties(Object bean) throws HibernateException;
 	
 	/**
 	 * Bind the values of the given Map for each named parameters of the query,
 	 * matching key names with parameter names and mapping value types to
 	 * Hibernate types using hueristics.
 	 * @param bean a java.util.Map
 	 */
 	public Query setProperties(Map bean) throws HibernateException;
 
 	public Query setString(int position, String val);
 	public Query setCharacter(int position, char val);
 	public Query setBoolean(int position, boolean val);
 	public Query setByte(int position, byte val);
 	public Query setShort(int position, short val);
 	public Query setInteger(int position, int val);
 	public Query setLong(int position, long val);
 	public Query setFloat(int position, float val);
 	public Query setDouble(int position, double val);
 	public Query setBinary(int position, byte[] val);
 	public Query setText(int position, String val);
 	public Query setSerializable(int position, Serializable val);
 	public Query setLocale(int position, Locale locale);
 	public Query setBigDecimal(int position, BigDecimal number);
 	public Query setBigInteger(int position, BigInteger number);
 
 	public Query setDate(int position, Date date);
 	public Query setTime(int position, Date date);
 	public Query setTimestamp(int position, Date date);
 
 	public Query setCalendar(int position, Calendar calendar);
 	public Query setCalendarDate(int position, Calendar calendar);
 
 	public Query setString(String name, String val);
 	public Query setCharacter(String name, char val);
 	public Query setBoolean(String name, boolean val);
 	public Query setByte(String name, byte val);
 	public Query setShort(String name, short val);
 	public Query setInteger(String name, int val);
 	public Query setLong(String name, long val);
 	public Query setFloat(String name, float val);
 	public Query setDouble(String name, double val);
 	public Query setBinary(String name, byte[] val);
 	public Query setText(String name, String val);
 	public Query setSerializable(String name, Serializable val);
 	public Query setLocale(String name, Locale locale);
 	public Query setBigDecimal(String name, BigDecimal number);
 	public Query setBigInteger(String name, BigInteger number);
 
         /**
          * Bind the date (time is truncated) of a given Date object to a named query parameter.
          * 
 	 * @param name The name of the parameter
 	 * @param date The date object
          */
 	public Query setDate(String name, Date date);
 
         /**
          * Bind the time (date is truncated) of a given Date object to a named query parameter.
          * 
 	 * @param name The name of the parameter
 	 * @param date The date object
          */
 	public Query setTime(String name, Date date);
 
         /**
          * Bind the date and the time of a given Date object to a named query parameter.
          *
 	 * @param name The name of the parameter
 	 * @param date The date object
          */
 	public Query setTimestamp(String name, Date date);
 
 	public Query setCalendar(String name, Calendar calendar);
 	public Query setCalendarDate(String name, Calendar calendar);
 
 	/**
 	 * Bind an instance of a mapped persistent class to a JDBC-style query parameter.
 	 * @param position the position of the parameter in the query
 	 * string, numbered from <tt>0</tt>.
 	 * @param val a non-null instance of a persistent class
 	 */
 	public Query setEntity(int position, Object val); // use setParameter for null values
 
 	/**
 	 * Bind an instance of a mapped persistent class to a named query parameter.
 	 * @param name the name of the parameter
 	 * @param val a non-null instance of a persistent class
 	 */
 	public Query setEntity(String name, Object val); // use setParameter for null values
 	
 	
 	/**
 	 * Set a strategy for handling the query results. This can be used to change
 	 * "shape" of the query result.
 	 *
 	 * @param transformer The transformer to apply
 	 * @return this (for method chaining)	
 	 */
 	public Query setResultTransformer(ResultTransformer transformer);
 
 }
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/SQLQuery.java b/hibernate-core/src/main/java/org/hibernate/SQLQuery.java
index 6cc40ae0c8..4938546a27 100755
--- a/hibernate-core/src/main/java/org/hibernate/SQLQuery.java
+++ b/hibernate-core/src/main/java/org/hibernate/SQLQuery.java
@@ -1,368 +1,328 @@
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
 package org.hibernate;
-import java.util.Collection;
 import java.util.List;
 
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.type.Type;
 
 /**
  * Represents a "native sql" query and allows the user to define certain aspects about its execution, such as:<ul>
  * <li>result-set value mapping (see below)</li>
  * <li>
  * 	Tables used via {@link #addSynchronizedQuerySpace}, {@link #addSynchronizedEntityName} and
  *  {@link #addSynchronizedEntityClass}.  This allows Hibernate to properly know how to deal with auto-flush checking
  *  as well as cached query results if the results of the query are being cached.
  * </li>
  * </ul>
  * <p/>
  * In terms of result-set mapping, there are 3 approaches to defining:<ul>
  * <li>If this represents a named sql query, the mapping could be associated with the query as part of its metadata</li>
  * <li>A pre-defined (defined in metadata and named) mapping can be associated with {@link #setResultSetMapping}</li>
  * <li>Defined locally per the various {@link #addEntity}, {@link #addRoot}, {@link #addJoin}, {@link #addFetch} and {@link #addScalar} methods</li>
  *
  * </ul>
  * 
  * @author Gavin King
  * @author Steve Ebersole
  */
-public interface SQLQuery extends Query {
-	/**
-	 * Obtain the list of query spaces (table names) the query is synchronized on.  These spaces affect the process
-	 * of auto-flushing by determining which entities will be processed by auto-flush based on the table to
-	 * which those entities are mapped and which are determined to have pending state changes.
-	 *
-	 * @return The list of query spaces upon which the query is synchronized.
-	 */
-	public Collection<String> getSynchronizedQuerySpaces();
-
-	/**
-	 * Adds a query space (table name) for (a) auto-flush checking and (b) query result cache invalidation checking
-	 *
-	 * @param querySpace The query space to be auto-flushed for this query.
-	 *
-	 * @return this, for method chaining
-	 *
-	 * @see #getSynchronizedQuerySpaces()
-	 */
-	public SQLQuery addSynchronizedQuerySpace(String querySpace);
+public interface SQLQuery extends Query, SynchronizeableQuery {
+	@Override
+	SQLQuery addSynchronizedQuerySpace(String querySpace);
 
-	/**
-	 * Adds an entity name for (a) auto-flush checking and (b) query result cache invalidation checking.  Same as
-	 * {@link #addSynchronizedQuerySpace} for all tables associated with the given entity.
-	 *
-	 * @param entityName The name of the entity upon whose defined query spaces we should additionally synchronize.
-	 *
-	 * @return this, for method chaining
-	 *
-	 * @throws MappingException Indicates the given name could not be resolved as an entity
-	 *
-	 * @see #getSynchronizedQuerySpaces()
-	 */
-	public SQLQuery addSynchronizedEntityName(String entityName) throws MappingException;
+	@Override
+	SQLQuery addSynchronizedEntityName(String entityName) throws MappingException;
 
-	/**
-	 * Adds an entity for (a) auto-flush checking and (b) query result cache invalidation checking.  Same as
-	 * {@link #addSynchronizedQuerySpace} for all tables associated with the given entity.
-	 *
-	 * @param entityClass The class of the entity upon whose defined query spaces we should additionally synchronize.
-	 *
-	 * @return this, for method chaining
-	 *
-	 * @throws MappingException Indicates the given class could not be resolved as an entity
-	 *
-	 * @see #getSynchronizedQuerySpaces()
-	 */
-	public SQLQuery addSynchronizedEntityClass(Class entityClass) throws MappingException;
+	@Override
+	SQLQuery addSynchronizedEntityClass(Class entityClass) throws MappingException;
 
 	/**
 	 * Use a predefined named result-set mapping.  This might be defined by a {@code <result-set/>} element in a
 	 * Hibernate <tt>hbm.xml</tt> file or through a {@link javax.persistence.SqlResultSetMapping} annotation.
 	 *
 	 * @param name The name of the mapping to use.
 	 *
 	 * @return this, for method chaining
 	 */
 	public SQLQuery setResultSetMapping(String name);
 
 	/**
 	 * Is this native-SQL query known to be callable?
 	 *
 	 * @return {@code true} if the query is known to be callable; {@code false} otherwise.
 	 */
 	public boolean isCallable();
 
 	public List<NativeSQLQueryReturn> getQueryReturns();
 
 	/**
 	 * Declare a scalar query result. Hibernate will attempt to automatically detect the underlying type.
 	 * <p/>
 	 * Functions like {@code <return-scalar/>} in {@code hbm.xml} or {@link javax.persistence.ColumnResult}
 	 *
 	 * @param columnAlias The column alias in the result-set to be processed as a scalar result
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SQLQuery addScalar(String columnAlias);
 
 	/**
 	 * Declare a scalar query result.
 	 * <p/>
 	 * Functions like {@code <return-scalar/>} in {@code hbm.xml} or {@link javax.persistence.ColumnResult}
 	 *
 	 * @param columnAlias The column alias in the result-set to be processed as a scalar result
 	 * @param type The Hibernate type as which to treat the value.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SQLQuery addScalar(String columnAlias, Type type);
 
 	/**
 	 * Add a new root return mapping, returning a {@link RootReturn} to allow further definition
 	 *
 	 * @param tableAlias The SQL table alias to map to this entity
 	 * @param entityName The name of the entity.
 	 *
 	 * @return The return config object for further control.
 	 *
 	 * @since 3.6
 	 */
 	public RootReturn addRoot(String tableAlias, String entityName);
 
 	/**
 	 * Add a new root return mapping, returning a {@link RootReturn} to allow further definition
 	 *
 	 * @param tableAlias The SQL table alias to map to this entity
 	 * @param entityType The java type of the entity.
 	 *
 	 * @return The return config object for further control.
 	 *
 	 * @since 3.6
 	 */
 	public RootReturn addRoot(String tableAlias, Class entityType);
 
 	/**
 	 * Declare a "root" entity, without specifying an alias.  The expectation here is that the table alias is the
 	 * same as the unqualified entity name
 	 * <p/>
 	 * Use {@link #addRoot} if you need further control of the mapping
 	 *
 	 * @param entityName The entity name that is the root return of the query.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SQLQuery addEntity(String entityName);
 
 	/**
 	 * Declare a "root" entity
 	 *
 	 * @param tableAlias The SQL table alias
 	 * @param entityName The entity name
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SQLQuery addEntity(String tableAlias, String entityName);
 
 	/**
 	 * Declare a "root" entity, specifying a lock mode
 	 *
 	 * @param tableAlias The SQL table alias
 	 * @param entityName The entity name
 	 * @param lockMode The lock mode for this return.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SQLQuery addEntity(String tableAlias, String entityName, LockMode lockMode);
 
 	/**
 	 * Declare a "root" entity, without specifying an alias.  The expectation here is that the table alias is the
 	 * same as the unqualified entity name
 	 *
 	 * @param entityType The java type of the entity to add as a root
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SQLQuery addEntity(Class entityType);
 
 	/**
 	 * Declare a "root" entity
 	 *
 	 * @param tableAlias The SQL table alias
 	 * @param entityType The java type of the entity to add as a root
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SQLQuery addEntity(String tableAlias, Class entityType);
 
 	/**
 	 * Declare a "root" entity, specifying a lock mode
 	 *
 	 * @param tableAlias The SQL table alias
 	 * @param entityName The entity name
 	 * @param lockMode The lock mode for this return.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SQLQuery addEntity(String tableAlias, Class entityName, LockMode lockMode);
 
 	/**
 	 * Declare a join fetch result.
 	 *
 	 * @param tableAlias The SQL table alias for the data to be mapped to this fetch
 	 * @param ownerTableAlias Identify the table alias of the owner of this association.  Should match the alias of a
 	 * previously added root or fetch
 	 * @param joinPropertyName The name of the property being join fetched.
 	 *
 	 * @return The return config object for further control.
 	 *
 	 * @since 3.6
 	 */
 	public FetchReturn addFetch(String tableAlias, String ownerTableAlias, String joinPropertyName);
 
 	/**
 	 * Declare a join fetch result.
 	 *
 	 * @param tableAlias The SQL table alias for the data to be mapped to this fetch
 	 * @param path The association path ([owner-alias].[property-name]).
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SQLQuery addJoin(String tableAlias, String path);
 
 	/**
 	 * Declare a join fetch result.
 	 *
 	 * @param tableAlias The SQL table alias for the data to be mapped to this fetch
 	 * @param ownerTableAlias Identify the table alias of the owner of this association.  Should match the alias of a
 	 * previously added root or fetch
 	 * @param joinPropertyName The name of the property being join fetched.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @since 3.6
 	 */
 	public SQLQuery addJoin(String tableAlias, String ownerTableAlias, String joinPropertyName);
 
 	/**
 	 * Declare a join fetch result, specifying a lock mode
 	 *
 	 * @param tableAlias The SQL table alias for the data to be mapped to this fetch
 	 * @param path The association path ([owner-alias].[property-name]).
 	 * @param lockMode The lock mode for this return.
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public SQLQuery addJoin(String tableAlias, String path, LockMode lockMode);
 
 	/**
 	 * Allows access to further control how properties within a root or join fetch are mapped back from the result set.
 	 * Generally used in composite value scenarios.
 	 */
 	public static interface ReturnProperty {
 		/**
 		 * Add a column alias to this property mapping.
 		 *
 		 * @param columnAlias The column alias.
 		 *
 		 * @return {@code this}, for method chaining
 		 */
 		public ReturnProperty addColumnAlias(String columnAlias);
 	}
 
 	/**
 	 * Allows access to further control how root returns are mapped back from result sets
 	 */
 	public static interface RootReturn {
 		/**
 		 * Set the lock mode for this return
 		 *
 		 * @param lockMode The new lock mode.
 		 *
 		 * @return {@code this}, for method chaining
 		 */
 		public RootReturn setLockMode(LockMode lockMode);
 
 		/**
 		 * Name the column alias that identifies the entity's discriminator
 		 *
 		 * @param columnAlias The discriminator column alias
 		 *
 		 * @return {@code this}, for method chaining
 		 */
 		public RootReturn setDiscriminatorAlias(String columnAlias);
 
 		/**
 		 * Add a simple property-to-one-column mapping
 		 *
 		 * @param propertyName The name of the property.
 		 * @param columnAlias The name of the column
 		 *
 		 * @return {@code this}, for method chaining
 		 */
 		public RootReturn addProperty(String propertyName, String columnAlias);
 
 		/**
 		 * Add a property, presumably with more than one column.
 		 *
 		 * @param propertyName The name of the property.
 		 *
 		 * @return The config object for further control.
 		 */
 		public ReturnProperty addProperty(String propertyName);
 	}
 
 	/**
 	 * Allows access to further control how join fetch returns are mapped back from result sets
 	 */
 	public static interface FetchReturn {
 		/**
 		 * Set the lock mode for this return
 		 *
 		 * @param lockMode The new lock mode.
 		 *
 		 * @return {@code this}, for method chaining
 		 */
 		public FetchReturn setLockMode(LockMode lockMode);
 
 		/**
 		 * Add a simple property-to-one-column mapping
 		 *
 		 * @param propertyName The name of the property.
 		 * @param columnAlias The name of the column
 		 *
 		 * @return {@code this}, for method chaining
 		 */
 		public FetchReturn addProperty(String propertyName, String columnAlias);
 
 		/**
 		 * Add a property, presumably with more than one column.
 		 *
 		 * @param propertyName The name of the property.
 		 *
 		 * @return The config object for further control.
 		 */
 		public ReturnProperty addProperty(String propertyName);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/SharedSessionContract.java b/hibernate-core/src/main/java/org/hibernate/SharedSessionContract.java
index 125ac0563c..b962573da5 100644
--- a/hibernate-core/src/main/java/org/hibernate/SharedSessionContract.java
+++ b/hibernate-core/src/main/java/org/hibernate/SharedSessionContract.java
@@ -1,125 +1,155 @@
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
 
 import java.io.Serializable;
 
 /**
  * Contract methods shared between {@link Session} and {@link StatelessSession}
  * 
  * @author Steve Ebersole
  */
 public interface SharedSessionContract extends Serializable {
 	/**
 	 * Obtain the tenant identifier associated with this session.
 	 *
 	 * @return The tenant identifier associated with this session, or {@code null}
 	 */
 	public String getTenantIdentifier();
 
 	/**
 	 * Begin a unit of work and return the associated {@link Transaction} object.  If a new underlying transaction is
 	 * required, begin the transaction.  Otherwise continue the new work in the context of the existing underlying
 	 * transaction.
 	 *
 	 * @return a Transaction instance
 	 *
 	 * @see #getTransaction
 	 */
 	public Transaction beginTransaction();
 
 	/**
 	 * Get the {@link Transaction} instance associated with this session.  The concrete type of the returned
 	 * {@link Transaction} object is determined by the {@code hibernate.transaction_factory} property.
 	 *
 	 * @return a Transaction instance
 	 */
 	public Transaction getTransaction();
 
 	/**
 	 * Create a {@link Query} instance for the named query string defined in the metadata.
 	 *
 	 * @param queryName the name of a query defined externally
 	 *
 	 * @return The query instance for manipulation and execution
 	 */
 	public Query getNamedQuery(String queryName);
 
 	/**
 	 * Create a {@link Query} instance for the given HQL query string.
 	 *
 	 * @param queryString The HQL query
 	 *
 	 * @return The query instance for manipulation and execution
 	 */
 	public Query createQuery(String queryString);
 
 	/**
 	 * Create a {@link SQLQuery} instance for the given SQL query string.
 	 *
 	 * @param queryString The SQL query
 	 * 
 	 * @return The query instance for manipulation and execution
 	 */
 	public SQLQuery createSQLQuery(String queryString);
 
 	/**
+	 * Creates a call to a stored procedure.
+	 *
+	 * @param procedureName The name of the procedure.
+	 *
+	 * @return The representation of the procedure call.
+	 */
+	public StoredProcedureCall createStoredProcedureCall(String procedureName);
+
+	/**
+	 * Creates a call to a stored procedure with specific result set entity mappings
+	 *
+	 * @param procedureName The name of the procedure.
+	 * @param resultClasses The entity(s) to map the result on to.
+	 *
+	 * @return The representation of the procedure call.
+	 */
+	public StoredProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses);
+
+	/**
+	 * Creates a call to a stored procedure with specific result set entity mappings
+	 *
+	 * @param procedureName The name of the procedure.
+	 * @param resultSetMappings The explicit result set mapping(s) to use for mapping the results
+	 *
+	 * @return The representation of the procedure call.
+	 */
+	public StoredProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings);
+
+	/**
 	 * Create {@link Criteria} instance for the given class (entity or subclasses/implementors)
 	 *
 	 * @param persistentClass The class, which is an entity, or has entity subclasses/implementors
 	 *
 	 * @return The criteria instance for manipulation and execution
 	 */
 	public Criteria createCriteria(Class persistentClass);
 
 	/**
 	 * Create {@link Criteria} instance for the given class (entity or subclasses/implementors), using a specific
 	 * alias.
 	 *
 	 * @param persistentClass The class, which is an entity, or has entity subclasses/implementors
 	 * @param alias The alias to use
 	 *
 	 * @return The criteria instance for manipulation and execution
 	 */
 	public Criteria createCriteria(Class persistentClass, String alias);
 
 	/**
 	 * Create {@link Criteria} instance for the given entity name.
 	 *
 	 * @param entityName The entity name
 
 	 * @return The criteria instance for manipulation and execution
 	 */
 	public Criteria createCriteria(String entityName);
 
 	/**
 	 * Create {@link Criteria} instance for the given entity name, using a specific alias.
 	 *
 	 * @param entityName The entity name
 	 * @param alias The alias to use
 	 *
 	 * @return The criteria instance for manipulation and execution
 	 */
 	public Criteria createCriteria(String entityName, String alias);
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/StoredProcedureCall.java b/hibernate-core/src/main/java/org/hibernate/StoredProcedureCall.java
new file mode 100644
index 0000000000..4d5bfd1e8e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/StoredProcedureCall.java
@@ -0,0 +1,197 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate;
+
+import javax.persistence.ParameterMode;
+import javax.persistence.TemporalType;
+import java.util.List;
+
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface StoredProcedureCall extends BasicQueryContract, SynchronizeableQuery {
+	@Override
+	StoredProcedureCall addSynchronizedQuerySpace(String querySpace);
+
+	@Override
+	StoredProcedureCall addSynchronizedEntityName(String entityName) throws MappingException;
+
+	@Override
+	StoredProcedureCall addSynchronizedEntityClass(Class entityClass) throws MappingException;
+
+	/**
+	 * Get the name of the stored procedure to be called.
+	 *
+	 * @return The procedure name.
+	 */
+	public String getProcedureName();
+
+
+	/**
+	 * Register a positional parameter.
+	 * All positional parameters must be registered.
+	 *
+	 * @param position parameter position
+	 * @param type type of the parameter
+	 * @param mode parameter mode
+	 *
+	 * @return the same query instance
+	 */
+	StoredProcedureCall registerStoredProcedureParameter(
+			int position,
+			Class type,
+			ParameterMode mode);
+
+	/**
+	 * Register a named parameter.
+	 * When using parameter names, all parameters must be registered
+	 * in the order in which they occur in the parameter list of the
+	 * stored procedure.
+	 *
+	 * @param parameterName name of the parameter as registered or
+	 * <p/>
+	 * specified in metadata
+	 * @param type type of the parameter
+	 * @param mode parameter mode
+	 *
+	 * @return the same query instance
+	 */
+	StoredProcedureCall registerStoredProcedureParameter(
+			String parameterName,
+			Class type,
+			ParameterMode mode);
+
+	/**
+	 * Retrieve all registered parameters.
+	 *
+	 * @return The (immutable) list of all registered parameters.
+	 */
+	public List<StoredProcedureParameter> getRegisteredParameters();
+
+	/**
+	 * Retrieve parameter registered by name.
+	 *
+	 * @param name The name under which the parameter of interest was registered.
+	 *
+	 * @return The registered parameter.
+	 */
+	public StoredProcedureParameter getRegisteredParameter(String name);
+	public StoredProcedureParameter getRegisteredParameter(int position);
+
+	public StoredProcedureOutputs getOutputs();
+
+	/**
+	 * Describes a parameter registered with the stored procedure.  Parameters can be either named or positional
+	 * as the registration mechanism.  Named and positional should not be mixed.
+	 */
+	public static interface StoredProcedureParameter<T> {
+		/**
+		 * The name under which this parameter was registered.  Can be {@code null} which should indicate that
+		 * positional registration was used (and therefore {@link #getPosition()} should return non-null.
+		 *
+		 * @return The name;
+		 */
+		public String getName();
+
+		/**
+		 * The position at which this parameter was registered.  Can be {@code null} which should indicate that
+		 * named registration was used (and therefore {@link #getName()} should return non-null.
+		 *
+		 * @return The name;
+		 */
+		public Integer getPosition();
+
+		/**
+		 * Obtain the Java type of parameter.  This is used to guess the Hibernate type (unless {@link #setHibernateType}
+		 * is called explicitly).
+		 *
+		 * @return The parameter Java type.
+		 */
+		public Class<T> getType();
+
+		/**
+		 * Retrieves the parameter "mode" which describes how the parameter is defined in the actual database procedure
+		 * definition (is it an INPUT parameter?  An OUTPUT parameter? etc).
+		 *
+		 * @return The parameter mode.
+		 */
+		public ParameterMode getMode();
+
+		/**
+		 * Set the Hibernate mapping type for this parameter.
+		 *
+		 * @param type The Hibernate mapping type.
+		 */
+		public void setHibernateType(Type type);
+
+		/**
+		 * Retrieve the binding associated with this parameter.  The binding is only relevant for INPUT parameters.  Can
+		 * return {@code null} if nothing has been bound yet.  To bind a value to the parameter use one of the
+		 * {@link #bindValue} methods.
+		 *
+		 * @return The parameter binding
+		 */
+		public StoredProcedureParameterBind getParameterBind();
+
+		/**
+		 * Bind a value to the parameter.  How this value is bound to the underlying JDBC CallableStatement is
+		 * totally dependent on the Hibernate type.
+		 *
+		 * @param value The value to bind.
+		 */
+		public void bindValue(T value);
+
+		/**
+		 * Bind a value to the parameter, using just a specified portion of the DATE/TIME value.  It is illegal to call
+		 * this form if the parameter is not DATE/TIME type.  The Hibernate type is circumvented in this case and
+		 * an appropriate "precision" Type is used instead.
+		 *
+		 * @param value The value to bind
+		 * @param explicitTemporalType An explicitly supplied TemporalType.
+		 */
+		public void bindValue(T value, TemporalType explicitTemporalType);
+	}
+
+	/**
+	 * Describes an input value binding for any IN/INOUT parameters.
+	 */
+	public static interface StoredProcedureParameterBind<T> {
+		/**
+		 * Retrieves the bound value.
+		 *
+		 * @return The bound value.
+		 */
+		public T getValue();
+
+		/**
+		 * If {@code <T>} represents a DATE/TIME type value, JPA usually allows specifying the particular parts of
+		 * the DATE/TIME value to be bound.  This value represents the particular part the user requested to be bound.
+		 *
+		 * @return The explicitly supplied TemporalType.
+		 */
+		public TemporalType getExplicitTemporalType();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/StoredProcedureOutputs.java b/hibernate-core/src/main/java/org/hibernate/StoredProcedureOutputs.java
new file mode 100644
index 0000000000..7591c7d415
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/StoredProcedureOutputs.java
@@ -0,0 +1,69 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate;
+
+/**
+ * Represents all the outputs of a call to a database stored procedure (or function) through the JDBC
+ * {@link java.sql.CallableStatement} interface.
+ *
+ * @author Steve Ebersole
+ */
+public interface StoredProcedureOutputs {
+	/**
+	 * Retrieve the value of an OUTPUT parameter by the name under which the parameter was registered.
+	 *
+	 * @param name The name under which the parameter was registered.
+	 *
+	 * @return The output value.
+	 *
+	 * @see StoredProcedureCall#registerStoredProcedureParameter(String, Class, javax.persistence.ParameterMode)
+	 */
+	public Object getOutputParameterValue(String name);
+
+	/**
+	 * Retrieve the value of an OUTPUT parameter by the name position under which the parameter was registered.
+	 *
+	 * @param position The position at which the parameter was registered.
+	 *
+	 * @return The output value.
+	 *
+	 * @see StoredProcedureCall#registerStoredProcedureParameter(int, Class, javax.persistence.ParameterMode)
+	 */
+	public Object getOutputParameterValue(int position);
+
+	/**
+	 * Are there any more returns associated with this set of outputs?
+	 *
+	 * @return {@code true} means there are more results available via {@link #getNextReturn()}; {@code false}
+	 * indicates that calling {@link #getNextReturn()} will certainly result in an exception.
+	 */
+	public boolean hasMoreReturns();
+
+	/**
+	 * Retrieve the next return.
+	 *
+	 * @return The next return.
+	 */
+	public StoredProcedureReturn getNextReturn();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/StoredProcedureResultSetReturn.java b/hibernate-core/src/main/java/org/hibernate/StoredProcedureResultSetReturn.java
new file mode 100644
index 0000000000..70b904c26b
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/StoredProcedureResultSetReturn.java
@@ -0,0 +1,48 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate;
+
+import java.util.List;
+
+/**
+ * Models a stored procedure result that is a result set.
+ *
+ * @author Steve Ebersole
+ */
+public interface StoredProcedureResultSetReturn extends StoredProcedureReturn {
+	/**
+	 * Consume the underlying {@link java.sql.ResultSet} and return the resulting List.
+	 *
+	 * @return The consumed ResultSet values.
+	 */
+	public List getResultList();
+
+	/**
+	 * Consume the underlying {@link java.sql.ResultSet} with the expectation that there is just a single level of
+	 * root returns.
+	 *
+	 * @return The single result.
+	 */
+	public Object getSingleResult();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/StoredProcedureReturn.java b/hibernate-core/src/main/java/org/hibernate/StoredProcedureReturn.java
new file mode 100644
index 0000000000..d14c4e11ad
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/StoredProcedureReturn.java
@@ -0,0 +1,31 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface StoredProcedureReturn {
+	public boolean isResultSet();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/StoredProcedureUpdateCountReturn.java b/hibernate-core/src/main/java/org/hibernate/StoredProcedureUpdateCountReturn.java
new file mode 100644
index 0000000000..e4a9324489
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/StoredProcedureUpdateCountReturn.java
@@ -0,0 +1,31 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface StoredProcedureUpdateCountReturn extends StoredProcedureReturn {
+	public int getUpdateCount();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/SynchronizeableQuery.java b/hibernate-core/src/main/java/org/hibernate/SynchronizeableQuery.java
new file mode 100644
index 0000000000..fa8239dd6a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/SynchronizeableQuery.java
@@ -0,0 +1,79 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate;
+
+import java.util.Collection;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface SynchronizeableQuery {
+	/**
+	 * Obtain the list of query spaces (table names) the query is synchronized on.  These spaces affect the process
+	 * of auto-flushing by determining which entities will be processed by auto-flush based on the table to
+	 * which those entities are mapped and which are determined to have pending state changes.
+	 *
+	 * @return The list of query spaces upon which the query is synchronized.
+	 */
+	public Collection<String> getSynchronizedQuerySpaces();
+
+	/**
+	 * Adds a query space (table name) for (a) auto-flush checking and (b) query result cache invalidation checking
+	 *
+	 * @param querySpace The query space to be auto-flushed for this query.
+	 *
+	 * @return this, for method chaining
+	 *
+	 * @see #getSynchronizedQuerySpaces()
+	 */
+	public SynchronizeableQuery addSynchronizedQuerySpace(String querySpace);
+
+	/**
+	 * Adds an entity name for (a) auto-flush checking and (b) query result cache invalidation checking.  Same as
+	 * {@link #addSynchronizedQuerySpace} for all tables associated with the given entity.
+	 *
+	 * @param entityName The name of the entity upon whose defined query spaces we should additionally synchronize.
+	 *
+	 * @return this, for method chaining
+	 *
+	 * @throws MappingException Indicates the given name could not be resolved as an entity
+	 *
+	 * @see #getSynchronizedQuerySpaces()
+	 */
+	public SynchronizeableQuery addSynchronizedEntityName(String entityName) throws MappingException;
+
+	/**
+	 * Adds an entity for (a) auto-flush checking and (b) query result cache invalidation checking.  Same as
+	 * {@link #addSynchronizedQuerySpace} for all tables associated with the given entity.
+	 *
+	 * @param entityClass The class of the entity upon whose defined query spaces we should additionally synchronize.
+	 *
+	 * @return this, for method chaining
+	 *
+	 * @throws MappingException Indicates the given class could not be resolved as an entity
+	 *
+	 * @see #getSynchronizedQuerySpaces()
+	 */
+	public SynchronizeableQuery addSynchronizedEntityClass(Class entityClass) throws MappingException;
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractBasicQueryContractImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractBasicQueryContractImpl.java
new file mode 100644
index 0000000000..f6d32d155f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractBasicQueryContractImpl.java
@@ -0,0 +1,146 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.internal;
+
+import java.io.Serializable;
+import java.util.Map;
+
+import org.hibernate.BasicQueryContract;
+import org.hibernate.CacheMode;
+import org.hibernate.FlushMode;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.RowSelection;
+import org.hibernate.engine.spi.SessionImplementor;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractBasicQueryContractImpl implements BasicQueryContract {
+	private final SessionImplementor session;
+
+	private FlushMode flushMode;
+	private CacheMode cacheMode;
+
+	private boolean cacheable;
+	private String cacheRegion;
+	private boolean readOnly;
+	private RowSelection selection = new RowSelection();
+
+	protected AbstractBasicQueryContractImpl(SessionImplementor session) {
+		this.session = session;
+		this.readOnly = session.getPersistenceContext().isDefaultReadOnly();
+	}
+
+	protected SessionImplementor session() {
+		return session;
+	}
+
+	@Override
+	public FlushMode getFlushMode() {
+		return flushMode;
+	}
+
+	@Override
+	public BasicQueryContract setFlushMode(FlushMode flushMode) {
+		this.flushMode = flushMode;
+		return this;
+	}
+
+	@Override
+	public CacheMode getCacheMode() {
+		return cacheMode;
+	}
+
+	@Override
+	public BasicQueryContract setCacheMode(CacheMode cacheMode) {
+		this.cacheMode = cacheMode;
+		return this;
+	}
+
+	@Override
+	public boolean isCacheable() {
+		return cacheable;
+	}
+
+	@Override
+	public BasicQueryContract setCacheable(boolean cacheable) {
+		this.cacheable = cacheable;
+		return this;
+	}
+
+	@Override
+	public String getCacheRegion() {
+		return cacheRegion;
+	}
+
+	@Override
+	public BasicQueryContract setCacheRegion(String cacheRegion) {
+		if ( cacheRegion != null ) {
+			this.cacheRegion = cacheRegion.trim();
+		}
+		return this;
+	}
+
+	@Override
+	public boolean isReadOnly() {
+		return readOnly;
+	}
+
+	@Override
+	public BasicQueryContract setReadOnly(boolean readOnly) {
+		this.readOnly = readOnly;
+		return this;
+	}
+
+	@Override
+	public Integer getTimeout() {
+		return selection.getTimeout();
+	}
+
+	@Override
+	public BasicQueryContract setTimeout(int timeout) {
+		selection.setTimeout( timeout );
+		return this;
+	}
+
+	@Override
+	public Integer getFetchSize() {
+		return selection.getFetchSize();
+	}
+
+	@Override
+	public BasicQueryContract setFetchSize(int fetchSize) {
+		selection.setFetchSize( fetchSize );
+		return this;
+	}
+
+	public QueryParameters buildQueryParametersObject() {
+		QueryParameters qp = new QueryParameters();
+		qp.setRowSelection( selection );
+		qp.setCacheable( cacheable );
+		qp.setCacheRegion( cacheRegion );
+		qp.setReadOnly( readOnly );
+		return qp;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
index 82215386e0..7e14ce91ba 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
@@ -1,333 +1,361 @@
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
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.Query;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollableResults;
 import org.hibernate.SessionException;
 import org.hibernate.SharedSessionContract;
+import org.hibernate.StoredProcedureCall;
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
 import org.hibernate.jdbc.WorkExecutor;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jdbc.connections.spi.MultiTenantConnectionProvider;
 import org.hibernate.type.Type;
 
 /**
  * Functionality common to stateless and stateful sessions
  *
  * @author Gavin King
  */
 public abstract class AbstractSessionImpl implements Serializable, SharedSessionContract,
 													 SessionImplementor, TransactionContext {
 	protected transient SessionFactoryImpl factory;
 	private final String tenantIdentifier;
 	private boolean closed = false;
 
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
 
 	@Override
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
 			if ( nqd.getLockOptions() != null ) {
 				query.setLockOptions( nqd.getLockOptions() );
 			}
 		}
 		else {
 			NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery( queryName );
 			if ( nsqlqd==null ) {
 				throw new MappingException( "Named query not known: " + queryName );
 			}
 			ParameterMetadata parameterMetadata = factory.getQueryPlanCache().getSQLParameterMetadata( nsqlqd.getQueryString() );
 			query = new SQLQueryImpl(
 					nsqlqd,
 			        this,
 					parameterMetadata
 			);
 			query.setComment( "named native SQL query " + queryName );
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
 
 	@SuppressWarnings("UnnecessaryUnboxing")
 	private void initQuery(Query query, NamedQueryDefinition nqd) {
 		// todo : cacheable and readonly should be Boolean rather than boolean...
 		query.setCacheable( nqd.isCacheable() );
 		query.setCacheRegion( nqd.getCacheRegion() );
 		query.setReadOnly( nqd.isReadOnly() );
 
 		if ( nqd.getTimeout() != null ) {
 			query.setTimeout( nqd.getTimeout().intValue() );
 		}
 		if ( nqd.getFetchSize() != null ) {
 			query.setFetchSize( nqd.getFetchSize().intValue() );
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
 		QueryImpl query = new QueryImpl(
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
 		SQLQueryImpl query = new SQLQueryImpl(
 				sql,
 		        this,
 		        factory.getQueryPlanCache().getSQLParameterMetadata( sql )
 		);
 		query.setComment( "dynamic native SQL query" );
 		return query;
 	}
 
+	@Override
+	@SuppressWarnings("UnnecessaryLocalVariable")
+	public StoredProcedureCall createStoredProcedureCall(String procedureName) {
+		errorIfClosed();
+		final StoredProcedureCall call = new StoredProcedureCallImpl( this, procedureName );
+//		call.setComment( "Dynamic stored procedure call" );
+		return call;
+	}
+
+	@Override
+	@SuppressWarnings("UnnecessaryLocalVariable")
+	public StoredProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
+		errorIfClosed();
+		final StoredProcedureCall call = new StoredProcedureCallImpl( this, procedureName, resultClasses );
+//		call.setComment( "Dynamic stored procedure call" );
+		return call;
+	}
+
+	@Override
+	@SuppressWarnings("UnnecessaryLocalVariable")
+	public StoredProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
+		errorIfClosed();
+		final StoredProcedureCall call = new StoredProcedureCallImpl( this, procedureName, resultSetMappings );
+//		call.setComment( "Dynamic stored procedure call" );
+		return call;
+	}
+
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
index 2f380a8d30..657ab3376b 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
@@ -1,526 +1,496 @@
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
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
-import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.SQLQuery;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.query.spi.ParameterMetadata;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryJoinReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.Type;
 
 /**
  * Implementation of the {@link SQLQuery} contract.
  *
  * @author Max Andersen
  * @author Steve Ebersole
  */
 public class SQLQueryImpl extends AbstractQueryImpl implements SQLQuery {
 
 	private List<NativeSQLQueryReturn> queryReturns;
 	private List<ReturnBuilder> queryReturnBuilders;
 	private boolean autoDiscoverTypes;
 
 	private Collection<String> querySpaces;
 
 	private final boolean callable;
 
 	/**
 	 * Constructs a SQLQueryImpl given a sql query defined in the mappings.
 	 *
 	 * @param queryDef The representation of the defined <sql-query/>.
 	 * @param session The session to which this SQLQueryImpl belongs.
 	 * @param parameterMetadata Metadata about parameters found in the query.
 	 */
 	SQLQueryImpl(NamedSQLQueryDefinition queryDef, SessionImplementor session, ParameterMetadata parameterMetadata) {
 		super( queryDef.getQueryString(), queryDef.getFlushMode(), session, parameterMetadata );
 		if ( queryDef.getResultSetRef() != null ) {
 			ResultSetMappingDefinition definition = session.getFactory()
 					.getResultSetMapping( queryDef.getResultSetRef() );
 			if (definition == null) {
 				throw new MappingException(
 						"Unable to find resultset-ref definition: " +
 						queryDef.getResultSetRef()
 					);
 			}
 			this.queryReturns = Arrays.asList( definition.getQueryReturns() );
 		}
 		else if ( queryDef.getQueryReturns() != null && queryDef.getQueryReturns().length > 0 ) {
 			this.queryReturns = Arrays.asList( queryDef.getQueryReturns() );
 		}
 		else {
 			this.queryReturns = new ArrayList<NativeSQLQueryReturn>();
 		}
 
 		this.querySpaces = queryDef.getQuerySpaces();
 		this.callable = queryDef.isCallable();
 	}
 
-	SQLQueryImpl(
-			final String sql,
-	        final String returnAliases[],
-	        final Class returnClasses[],
-	        final LockMode[] lockModes,
-	        final SessionImplementor session,
-	        final Collection<String> querySpaces,
-	        final FlushMode flushMode,
-	        ParameterMetadata parameterMetadata) {
-		// TODO : this constructor form is *only* used from constructor directly below us; can it go away?
-		super( sql, flushMode, session, parameterMetadata );
-		queryReturns = new ArrayList<NativeSQLQueryReturn>( returnAliases.length );
-		for ( int i=0; i<returnAliases.length; i++ ) {
-			NativeSQLQueryRootReturn ret = new NativeSQLQueryRootReturn(
-					returnAliases[i],
-					returnClasses[i].getName(),
-					lockModes==null ? LockMode.NONE : lockModes[i]
-			);
-			queryReturns.add(ret);
-		}
-		this.querySpaces = querySpaces;
-		this.callable = false;
-	}
-
-	SQLQueryImpl(
-			final String sql,
-	        final String returnAliases[],
-	        final Class returnClasses[],
-	        final SessionImplementor session,
-	        ParameterMetadata parameterMetadata) {
-		this( sql, returnAliases, returnClasses, null, session, null, null, parameterMetadata );
+	SQLQueryImpl(String sql, SessionImplementor session, ParameterMetadata parameterMetadata) {
+		this( sql, false, session, parameterMetadata );
 	}
 
-	SQLQueryImpl(String sql, SessionImplementor session, ParameterMetadata parameterMetadata) {
+	SQLQueryImpl(String sql, boolean callable, SessionImplementor session, ParameterMetadata parameterMetadata) {
 		super( sql, null, session, parameterMetadata );
-		queryReturns = new ArrayList<NativeSQLQueryReturn>();
-		querySpaces = null;
-		callable = false;
+		this.queryReturns = new ArrayList<NativeSQLQueryReturn>();
+		this.querySpaces = null;
+		this.callable = callable;
 	}
 
 	@Override
 	public List<NativeSQLQueryReturn>  getQueryReturns() {
 		prepareQueryReturnsIfNecessary();
 		return queryReturns;
 	}
 
 	@Override
 	public Collection<String> getSynchronizedQuerySpaces() {
 		return querySpaces;
 	}
 
 	@Override
 	public boolean isCallable() {
 		return callable;
 	}
 
 	@Override
 	public List list() throws HibernateException {
 		verifyParameters();
 		before();
 
 		Map namedParams = getNamedParams();
 		NativeSQLQuerySpecification spec = generateQuerySpecification( namedParams );
 
 		try {
 			return getSession().list( spec, getQueryParameters( namedParams ) );
 		}
 		finally {
 			after();
 		}
 	}
 
 	private NativeSQLQuerySpecification generateQuerySpecification(Map namedParams) {
 		return new NativeSQLQuerySpecification(
 		        expandParameterLists(namedParams),
 				queryReturns.toArray( new NativeSQLQueryReturn[queryReturns.size()] ),
 		        querySpaces
 		);
 	}
 
 	public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
 		verifyParameters();
 		before();
 
 		Map namedParams = getNamedParams();
 		NativeSQLQuerySpecification spec = generateQuerySpecification( namedParams );
 
 		QueryParameters qp = getQueryParameters( namedParams );
 		qp.setScrollMode( scrollMode );
 
 		try {
 			return getSession().scroll( spec, qp );
 		}
 		finally {
 			after();
 		}
 	}
 
 	public ScrollableResults scroll() throws HibernateException {
 		return scroll(ScrollMode.SCROLL_INSENSITIVE);
 	}
 
 	public Iterator iterate() throws HibernateException {
 		throw new UnsupportedOperationException("SQL queries do not currently support iteration");
 	}
 
 	@Override
     public QueryParameters getQueryParameters(Map namedParams) {
 		QueryParameters qp = super.getQueryParameters(namedParams);
 		qp.setCallable(callable);
 		qp.setAutoDiscoverScalarTypes( autoDiscoverTypes );
 		return qp;
 	}
 
 	@Override
     protected void verifyParameters() {
 		// verifyParameters is called at the start of all execution type methods, so we use that here to perform
 		// some preparation work.
 		prepareQueryReturnsIfNecessary();
 		verifyParameters( callable );
 		boolean noReturns = queryReturns==null || queryReturns.isEmpty();
 		if ( noReturns ) {
 			this.autoDiscoverTypes = noReturns;
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
 			}
 		}
 	}
 
 	private void prepareQueryReturnsIfNecessary() {
 		if ( queryReturnBuilders != null ) {
 			if ( ! queryReturnBuilders.isEmpty() ) {
 				if ( queryReturns != null ) {
 					queryReturns.clear();
 					queryReturns = null;
 				}
 				queryReturns = new ArrayList<NativeSQLQueryReturn>();
 				for ( ReturnBuilder builder : queryReturnBuilders ) {
 					queryReturns.add( builder.buildReturn() );
 				}
 				queryReturnBuilders.clear();
 			}
 			queryReturnBuilders = null;
 		}
 	}
 
 	@Override
     public String[] getReturnAliases() throws HibernateException {
 		throw new UnsupportedOperationException("SQL queries do not currently support returning aliases");
 	}
 
 	@Override
     public Type[] getReturnTypes() throws HibernateException {
 		throw new UnsupportedOperationException("not yet implemented for SQL queries");
 	}
 
 	public Query setLockMode(String alias, LockMode lockMode) {
 		throw new UnsupportedOperationException("cannot set the lock mode for a native SQL query");
 	}
 
 	public Query setLockOptions(LockOptions lockOptions) {
 		throw new UnsupportedOperationException("cannot set lock options for a native SQL query");
 	}
 
 	@Override
     public LockOptions getLockOptions() {
 		//we never need to apply locks to the SQL
 		return null;
 	}
 
 	public SQLQuery addScalar(final String columnAlias, final Type type) {
 		if ( queryReturnBuilders == null ) {
 			queryReturnBuilders = new ArrayList<ReturnBuilder>();
 		}
 		queryReturnBuilders.add(
 				new ReturnBuilder() {
 					public NativeSQLQueryReturn buildReturn() {
 						return new NativeSQLQueryScalarReturn( columnAlias, type );
 					}
 				}
 		);
 		return this;
 	}
 
 	public SQLQuery addScalar(String columnAlias) {
 		return addScalar( columnAlias, null );
 	}
 
 	public RootReturn addRoot(String tableAlias, String entityName) {
 		RootReturnBuilder builder = new RootReturnBuilder( tableAlias, entityName );
 		if ( queryReturnBuilders == null ) {
 			queryReturnBuilders = new ArrayList<ReturnBuilder>();
 		}
 		queryReturnBuilders.add( builder );
 		return builder;
 	}
 
 	public RootReturn addRoot(String tableAlias, Class entityType) {
 		return addRoot( tableAlias, entityType.getName() );
 	}
 
 	public SQLQuery addEntity(String entityName) {
 		return addEntity( StringHelper.unqualify( entityName ), entityName );
 	}
 
 	public SQLQuery addEntity(String alias, String entityName) {
 		addRoot( alias, entityName );
 		return this;
 	}
 
 	public SQLQuery addEntity(String alias, String entityName, LockMode lockMode) {
 		addRoot( alias, entityName ).setLockMode( lockMode );
 		return this;
 	}
 
 	public SQLQuery addEntity(Class entityType) {
 		return addEntity( entityType.getName() );
 	}
 
 	public SQLQuery addEntity(String alias, Class entityClass) {
 		return addEntity( alias, entityClass.getName() );
 	}
 
 	public SQLQuery addEntity(String alias, Class entityClass, LockMode lockMode) {
 		return addEntity( alias, entityClass.getName(), lockMode );
 	}
 
 	public FetchReturn addFetch(String tableAlias, String ownerTableAlias, String joinPropertyName) {
 		FetchReturnBuilder builder = new FetchReturnBuilder( tableAlias, ownerTableAlias, joinPropertyName );
 		if ( queryReturnBuilders == null ) {
 			queryReturnBuilders = new ArrayList<ReturnBuilder>();
 		}
 		queryReturnBuilders.add( builder );
 		return builder;
 	}
 
 	public SQLQuery addJoin(String tableAlias, String ownerTableAlias, String joinPropertyName) {
 		addFetch( tableAlias, ownerTableAlias, joinPropertyName );
 		return this;
 	}
 
 	public SQLQuery addJoin(String alias, String path) {
 		createFetchJoin( alias, path );
 		return this;
 	}
 
 	private FetchReturn createFetchJoin(String tableAlias, String path) {
 		int loc = path.indexOf('.');
 		if ( loc < 0 ) {
 			throw new QueryException( "not a property path: " + path );
 		}
 		final String ownerTableAlias = path.substring( 0, loc );
 		final String joinedPropertyName = path.substring( loc+1 );
 		return addFetch( tableAlias, ownerTableAlias, joinedPropertyName );
 	}
 
 	public SQLQuery addJoin(String alias, String path, LockMode lockMode) {
 		createFetchJoin( alias, path ).setLockMode( lockMode );
 		return this;
 	}
 
 	public SQLQuery setResultSetMapping(String name) {
 		ResultSetMappingDefinition mapping = session.getFactory().getResultSetMapping( name );
 		if ( mapping == null ) {
 			throw new MappingException( "Unknown SqlResultSetMapping [" + name + "]" );
 		}
 		NativeSQLQueryReturn[] returns = mapping.getQueryReturns();
 		queryReturns.addAll( Arrays.asList( returns ) );
 		return this;
 	}
 
 	public SQLQuery addSynchronizedQuerySpace(String querySpace) {
 		if ( querySpaces == null ) {
 			querySpaces = new ArrayList<String>();
 		}
 		querySpaces.add( querySpace );
 		return this;
 	}
 
 	public SQLQuery addSynchronizedEntityName(String entityName) {
 		return addQuerySpaces( getSession().getFactory().getEntityPersister( entityName ).getQuerySpaces() );
 	}
 
 	public SQLQuery addSynchronizedEntityClass(Class entityClass) {
 		return addQuerySpaces( getSession().getFactory().getEntityPersister( entityClass.getName() ).getQuerySpaces() );
 	}
 
 	private SQLQuery addQuerySpaces(Serializable[] spaces) {
 		if ( spaces != null ) {
 			if ( querySpaces == null ) {
 				querySpaces = new ArrayList<String>();
 			}
 			querySpaces.addAll( Arrays.asList( (String[]) spaces ) );
 		}
 		return this;
 	}
 
 	public int executeUpdate() throws HibernateException {
 		Map namedParams = getNamedParams();
 		before();
 		try {
 			return getSession().executeNativeUpdate(
 					generateQuerySpecification( namedParams ),
 					getQueryParameters( namedParams )
 			);
 		}
 		finally {
 			after();
 		}
 	}
 
 	private class RootReturnBuilder implements RootReturn, ReturnBuilder {
 		private final String alias;
 		private final String entityName;
 		private LockMode lockMode = LockMode.READ;
 		private Map<String,String[]> propertyMappings;
 
 		private RootReturnBuilder(String alias, String entityName) {
 			this.alias = alias;
 			this.entityName = entityName;
 		}
 
 		public RootReturn setLockMode(LockMode lockMode) {
 			this.lockMode = lockMode;
 			return this;
 		}
 
 		public RootReturn setDiscriminatorAlias(String alias) {
 			addProperty( "class", alias );
 			return this;
 		}
 
 		public RootReturn addProperty(String propertyName, String columnAlias) {
 			addProperty( propertyName ).addColumnAlias( columnAlias );
 			return this;
 		}
 
 		public ReturnProperty addProperty(final String propertyName) {
 			if ( propertyMappings == null ) {
 				propertyMappings = new HashMap<String,String[]>();
 			}
 			return new ReturnProperty() {
 				public ReturnProperty addColumnAlias(String columnAlias) {
 					String[] columnAliases = propertyMappings.get( propertyName );
 					if ( columnAliases == null ) {
 						columnAliases = new String[]{columnAlias};
 					}else{
 						 String[] newColumnAliases = new String[columnAliases.length + 1];
 						System.arraycopy( columnAliases, 0, newColumnAliases, 0, columnAliases.length );
 						newColumnAliases[columnAliases.length] = columnAlias;
 						columnAliases = newColumnAliases;
 					}
 					propertyMappings.put( propertyName,columnAliases );
 					return this;
 				}
 			};
 		}
 
 		public NativeSQLQueryReturn buildReturn() {
 			return new NativeSQLQueryRootReturn( alias, entityName, propertyMappings, lockMode );
 		}
 	}
 	private class FetchReturnBuilder implements FetchReturn, ReturnBuilder {
 		private final String alias;
 		private String ownerTableAlias;
 		private final String joinedPropertyName;
 		private LockMode lockMode = LockMode.READ;
 		private Map<String,String[]> propertyMappings;
 
 		private FetchReturnBuilder(String alias, String ownerTableAlias, String joinedPropertyName) {
 			this.alias = alias;
 			this.ownerTableAlias = ownerTableAlias;
 			this.joinedPropertyName = joinedPropertyName;
 		}
 
 		public FetchReturn setLockMode(LockMode lockMode) {
 			this.lockMode = lockMode;
 			return this;
 		}
 
 		public FetchReturn addProperty(String propertyName, String columnAlias) {
 			addProperty( propertyName ).addColumnAlias( columnAlias );
 			return this;
 		}
 
 		public ReturnProperty addProperty(final String propertyName) {
 			if ( propertyMappings == null ) {
 				propertyMappings = new HashMap<String,String[]>();
 			}
 			return new ReturnProperty() {
 				public ReturnProperty addColumnAlias(String columnAlias) {
 					String[] columnAliases = propertyMappings.get( propertyName );
 					if ( columnAliases == null ) {
 						columnAliases = new String[]{columnAlias};
 					}else{
 						 String[] newColumnAliases = new String[columnAliases.length + 1];
 						System.arraycopy( columnAliases, 0, newColumnAliases, 0, columnAliases.length );
 						newColumnAliases[columnAliases.length] = columnAlias;
 						columnAliases = newColumnAliases;
 					}
 					propertyMappings.put( propertyName,columnAliases );
 					return this;
 				}
 			};
 		}
 
 		public NativeSQLQueryReturn buildReturn() {
 			return new NativeSQLQueryJoinReturn( alias, ownerTableAlias, joinedPropertyName, propertyMappings, lockMode );
 		}
 	}
 
 	private interface ReturnBuilder {
 		NativeSQLQueryReturn buildReturn();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
index 2a30488f6d..52824ed5e0 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
@@ -1,2647 +1,2669 @@
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
+import org.hibernate.StoredProcedureCall;
 import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.SharedSessionBuilder;
 import org.hibernate.SimpleNaturalIdLoadAccess;
 import org.hibernate.Transaction;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeHelper;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.criterion.NaturalIdentifier;
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
 					if ( isOpen() ) {
 						if ( flushBeforeCompletionEnabled ){
 							SessionImpl.this.managedFlush();
 						}
 						getActionQueue().beforeTransactionCompletion();
 					}
 					else {
 						if (actionQueue.hasAfterTransactionActions()){
 							LOG.log( Logger.Level.DEBUG, "Session had after transaction actions that were not processed");
 						}
 					}
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
 
 		LOG.debugf( "Opened session at timestamp: %s", timestamp );
 	}
 
 	@Override
 	public SharedSessionBuilder sessionWithOptions() {
 		return new SharedSessionBuilderImpl( this );
 	}
 
 	public void clear() {
 		errorIfClosed();
 		checkTransactionSynchStatus();
 		internalClear();
 	}
 
 	private void internalClear() {
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
 
+	@Override
+	public StoredProcedureCall createStoredProcedureCall(String procedureName) {
+		errorIfClosed();
+		checkTransactionSynchStatus();
+		return super.createStoredProcedureCall( procedureName );
+	}
+
+	@Override
+	public StoredProcedureCall createStoredProcedureCall(String procedureName, String... resultSetMappings) {
+		errorIfClosed();
+		checkTransactionSynchStatus();
+		return super.createStoredProcedureCall( procedureName, resultSetMappings );
+	}
+
+	@Override
+	public StoredProcedureCall createStoredProcedureCall(String procedureName, Class... resultClasses) {
+		errorIfClosed();
+		checkTransactionSynchStatus();
+		return super.createStoredProcedureCall( procedureName, resultClasses );
+	}
+
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
 			if ( ! isTransactionInProgress() ) {
 				// not in a transaction so skip synchronization
 				return;
 			}
 			if ( entityPersister.getEntityMetamodel().hasImmutableNaturalId() ) {
 				// only mutable natural-ids need this processing 
 				return;
 			}
 
 			for ( Serializable pk : getPersistenceContext().getNaturalIdHelper().getCachedPkResolutions( entityPersister ) ) {
 				final EntityKey entityKey = generateEntityKey( pk, entityPersister );
 				final Object entity = getPersistenceContext().getEntity( entityKey );
 				final EntityEntry entry = getPersistenceContext().getEntry( entity );
 
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
 			return this.getIdentifierLoadAccess().load( entityId );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureCallImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureCallImpl.java
new file mode 100644
index 0000000000..ee93509086
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureCallImpl.java
@@ -0,0 +1,555 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.internal;
+
+import javax.persistence.ParameterMode;
+import javax.persistence.TemporalType;
+import java.sql.CallableStatement;
+import java.sql.SQLException;
+import java.util.ArrayList;
+import java.util.Arrays;
+import java.util.Calendar;
+import java.util.Collection;
+import java.util.Collections;
+import java.util.Date;
+import java.util.HashSet;
+import java.util.List;
+import java.util.Set;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.HibernateException;
+import org.hibernate.LockMode;
+import org.hibernate.MappingException;
+import org.hibernate.QueryException;
+import org.hibernate.StoredProcedureCall;
+import org.hibernate.StoredProcedureOutputs;
+import org.hibernate.cfg.NotYetImplementedException;
+import org.hibernate.engine.ResultSetMappingDefinition;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.type.DateType;
+import org.hibernate.type.ProcedureParameterExtractionAware;
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public class StoredProcedureCallImpl extends AbstractBasicQueryContractImpl implements StoredProcedureCall {
+	private static final Logger log = Logger.getLogger( StoredProcedureCallImpl.class );
+
+	private final String procedureName;
+	private final NativeSQLQueryReturn[] queryReturns;
+
+	private TypeOfParameter typeOfParameters = TypeOfParameter.UNKNOWN;
+	private List<StoredProcedureParameterImplementor> registeredParameters = new ArrayList<StoredProcedureParameterImplementor>();
+
+	private Set<String> synchronizedQuerySpaces;
+
+	@SuppressWarnings("unchecked")
+	public StoredProcedureCallImpl(SessionImplementor session, String procedureName) {
+		this( session, procedureName, (List) null );
+	}
+
+	public StoredProcedureCallImpl(SessionImplementor session, String procedureName, List<NativeSQLQueryReturn> queryReturns) {
+		super( session );
+		this.procedureName = procedureName;
+
+		if ( queryReturns == null || queryReturns.isEmpty() ) {
+			this.queryReturns = new NativeSQLQueryReturn[0];
+		}
+		else {
+			this.queryReturns = queryReturns.toArray( new NativeSQLQueryReturn[ queryReturns.size() ] );
+		}
+	}
+
+	public StoredProcedureCallImpl(SessionImplementor session, String procedureName, Class... resultClasses) {
+		this( session, procedureName, collectQueryReturns( resultClasses ) );
+	}
+
+	private static List<NativeSQLQueryReturn> collectQueryReturns(Class[] resultClasses) {
+		if ( resultClasses == null || resultClasses.length == 0 ) {
+			return null;
+		}
+
+		List<NativeSQLQueryReturn> queryReturns = new ArrayList<NativeSQLQueryReturn>( resultClasses.length );
+		int i = 1;
+		for ( Class resultClass : resultClasses ) {
+			queryReturns.add( new NativeSQLQueryRootReturn( "alias" + i, resultClass.getName(), LockMode.READ ) );
+			i++;
+		}
+		return queryReturns;
+	}
+
+	public StoredProcedureCallImpl(SessionImplementor session, String procedureName, String... resultSetMappings) {
+		this( session, procedureName, collectQueryReturns( session, resultSetMappings ) );
+	}
+
+	private static List<NativeSQLQueryReturn> collectQueryReturns(SessionImplementor session, String[] resultSetMappings) {
+		if ( resultSetMappings == null || resultSetMappings.length == 0 ) {
+			return null;
+		}
+
+		List<NativeSQLQueryReturn> queryReturns = new ArrayList<NativeSQLQueryReturn>( resultSetMappings.length );
+		for ( String resultSetMapping : resultSetMappings ) {
+			ResultSetMappingDefinition mapping = session.getFactory().getResultSetMapping( resultSetMapping );
+			if ( mapping == null ) {
+				throw new MappingException( "Unknown SqlResultSetMapping [" + resultSetMapping + "]" );
+			}
+			queryReturns.addAll( Arrays.asList( mapping.getQueryReturns() ) );
+		}
+		return queryReturns;
+	}
+
+//	public StoredProcedureCallImpl(
+//			SessionImplementor session,
+//			String procedureName,
+//			List<StoredProcedureParameter> parameters) {
+//		// this form is intended for named stored procedure calls.
+//		// todo : introduce a NamedProcedureCallDefinition object to hold all needed info and pass that in here; will help with EM.addNamedQuery as well..
+//		this( session, procedureName );
+//		for ( StoredProcedureParameter parameter : parameters ) {
+//			registerParameter( (StoredProcedureParameterImplementor) parameter );
+//		}
+//	}
+
+	@Override
+	public String getProcedureName() {
+		return procedureName;
+	}
+
+	NativeSQLQueryReturn[] getQueryReturns() {
+		return queryReturns;
+	}
+
+	@Override
+	@SuppressWarnings("unchecked")
+	public StoredProcedureCall registerStoredProcedureParameter(int position, Class type, ParameterMode mode) {
+		registerParameter( new PositionalStoredProcedureParameter( this, position, mode, type ) );
+		return this;
+	}
+
+	private void registerParameter(StoredProcedureParameterImplementor parameter) {
+		if ( StringHelper.isNotEmpty( parameter.getName() ) ) {
+			if ( typeOfParameters == TypeOfParameter.POSITIONAL ) {
+				throw new QueryException( "Cannot mix named and positional parameters" );
+			}
+			typeOfParameters = TypeOfParameter.NAMED;
+			registeredParameters.add( parameter );
+		}
+		else if ( parameter.getPosition() != null ) {
+			if ( typeOfParameters == TypeOfParameter.NAMED ) {
+				throw new QueryException( "Cannot mix named and positional parameters" );
+			}
+			typeOfParameters = TypeOfParameter.POSITIONAL;
+			registeredParameters.add( parameter.getPosition(), parameter );
+		}
+		else {
+			throw new IllegalArgumentException( "Given parameter did not define name nor position [" + parameter + "]" );
+		}
+	}
+
+	@Override
+	@SuppressWarnings("unchecked")
+	public StoredProcedureCall registerStoredProcedureParameter(String name, Class type, ParameterMode mode) {
+		registerParameter( new NamedStoredProcedureParameter( this, name, mode, type ) );
+		return this;
+	}
+
+	@Override
+	@SuppressWarnings("unchecked")
+	public List getRegisteredParameters() {
+		return registeredParameters;
+	}
+
+	@Override
+	public StoredProcedureParameterImplementor getRegisteredParameter(String name) {
+		if ( typeOfParameters != TypeOfParameter.NAMED ) {
+			throw new IllegalArgumentException( "Names were not used to register parameters with this stored procedure call" );
+		}
+		for ( StoredProcedureParameterImplementor parameter : registeredParameters ) {
+			if ( name.equals( parameter.getName() ) ) {
+				return parameter;
+			}
+		}
+		throw new IllegalArgumentException( "Could not locate parameter registered under that name [" + name + "]" );	}
+
+	@Override
+	public StoredProcedureParameterImplementor getRegisteredParameter(int position) {
+		try {
+			return registeredParameters.get( position );
+		}
+		catch ( Exception e ) {
+			throw new QueryException( "Could not locate parameter registered using that position [" + position + "]" );
+		}
+	}
+
+	@Override
+	public StoredProcedureOutputs getOutputs() {
+
+		// todo : going to need a very specialized Loader for this.
+		// or, might be a good time to look at splitting Loader up into:
+		//		1) building statement objects
+		//		2) executing statement objects
+		//		3) processing result sets
+
+		// for now assume there are no resultClasses nor mappings defined..
+		// 	TOTAL PROOF-OF-CONCEPT!!!!!!
+
+		final StringBuilder buffer = new StringBuilder().append( "{call " )
+				.append( procedureName )
+				.append( "(" );
+		String sep = "";
+		for ( StoredProcedureParameterImplementor parameter : registeredParameters ) {
+			for ( int i = 0; i < parameter.getSqlTypes().length; i++ ) {
+				buffer.append( sep ).append( "?" );
+				sep = ",";
+			}
+		}
+		buffer.append( ")}" );
+
+		try {
+			final CallableStatement statement = session().getTransactionCoordinator()
+					.getJdbcCoordinator()
+					.getLogicalConnection()
+					.getShareableConnectionProxy()
+					.prepareCall( buffer.toString() );
+
+			// prepare parameters
+			int i = 1;
+			for ( StoredProcedureParameterImplementor parameter : registeredParameters ) {
+				if ( parameter == null ) {
+					throw new QueryException( "Registered stored procedure parameters had gaps" );
+				}
+
+				parameter.prepare( statement, i );
+				i += parameter.getSqlTypes().length;
+			}
+
+			return new StoredProcedureOutputsImpl( this, statement );
+		}
+		catch (SQLException e) {
+			throw session().getFactory().getSQLExceptionHelper().convert(
+					e,
+					"Error preparing CallableStatement",
+					getProcedureName()
+			);
+		}
+	}
+
+
+	@Override
+	public Type[] getReturnTypes() throws HibernateException {
+		throw new NotYetImplementedException();
+	}
+
+	protected Set<String> synchronizedQuerySpaces() {
+		if ( synchronizedQuerySpaces == null ) {
+			synchronizedQuerySpaces = new HashSet<String>();
+		}
+		return synchronizedQuerySpaces;
+	}
+
+	@Override
+	@SuppressWarnings("unchecked")
+	public Collection<String> getSynchronizedQuerySpaces() {
+		if ( synchronizedQuerySpaces == null ) {
+			return Collections.emptySet();
+		}
+		else {
+			return Collections.unmodifiableSet( synchronizedQuerySpaces );
+		}
+	}
+
+	public Set<String> getSynchronizedQuerySpacesSet() {
+		return (Set<String>) getSynchronizedQuerySpaces();
+	}
+
+	@Override
+	public StoredProcedureCallImpl addSynchronizedQuerySpace(String querySpace) {
+		synchronizedQuerySpaces().add( querySpace );
+		return this;
+	}
+
+	@Override
+	public StoredProcedureCallImpl addSynchronizedEntityName(String entityName) {
+		addSynchronizedQuerySpaces( session().getFactory().getEntityPersister( entityName ) );
+		return this;
+	}
+
+	protected void addSynchronizedQuerySpaces(EntityPersister persister) {
+		synchronizedQuerySpaces().addAll( Arrays.asList( (String[]) persister.getQuerySpaces() ) );
+	}
+
+	@Override
+	public StoredProcedureCallImpl addSynchronizedEntityClass(Class entityClass) {
+		addSynchronizedQuerySpaces( session().getFactory().getEntityPersister( entityClass.getName() ) );
+		return this;
+	}
+
+	public QueryParameters buildQueryParametersObject() {
+		QueryParameters qp = super.buildQueryParametersObject();
+		// both of these are for documentation purposes, they are actually handled directly...
+		qp.setAutoDiscoverScalarTypes( true );
+		qp.setCallable( true );
+		return qp;
+	}
+
+	/**
+	 * Ternary logic enum
+	 */
+	private static enum TypeOfParameter {
+		NAMED,
+		POSITIONAL,
+		UNKNOWN
+	}
+
+	protected static interface StoredProcedureParameterImplementor<T> extends StoredProcedureParameter<T> {
+		public void prepare(CallableStatement statement, int i) throws SQLException;
+
+		public int[] getSqlTypes();
+
+		public T extract(CallableStatement statement);
+	}
+
+	public static abstract class AbstractStoredProcedureParameterImpl<T> implements StoredProcedureParameterImplementor<T> {
+		private final StoredProcedureCallImpl procedureCall;
+
+		private final ParameterMode mode;
+		private final Class<T> type;
+
+		private int startIndex;
+		private Type hibernateType;
+		private int[] sqlTypes;
+
+		private StoredProcedureParameterBindImpl bind;
+
+		protected AbstractStoredProcedureParameterImpl(
+				StoredProcedureCallImpl procedureCall,
+				ParameterMode mode,
+				Class<T> type) {
+			this.procedureCall = procedureCall;
+			this.mode = mode;
+			this.type = type;
+
+			setHibernateType( session().getFactory().getTypeResolver().heuristicType( type.getName() ) );
+		}
+
+		@Override
+		public String getName() {
+			return null;
+		}
+
+		@Override
+		public Integer getPosition() {
+			return null;
+		}
+
+		@Override
+		public Class<T> getType() {
+			return type;
+		}
+
+		@Override
+		public ParameterMode getMode() {
+			return mode;
+		}
+
+		@Override
+		public void setHibernateType(Type type) {
+			if ( type == null ) {
+				throw new IllegalArgumentException( "Type cannot be null" );
+			}
+			this.hibernateType = type;
+			this.sqlTypes = hibernateType.sqlTypes( session().getFactory() );
+		}
+
+		protected SessionImplementor session() {
+			return procedureCall.session();
+		}
+
+		@Override
+		public void prepare(CallableStatement statement, int startIndex) throws SQLException {
+			if ( mode == ParameterMode.REF_CURSOR ) {
+				throw new NotYetImplementedException( "Support for REF_CURSOR parameters not yet supported" );
+			}
+
+			this.startIndex = startIndex;
+			if ( mode == ParameterMode.IN || mode == ParameterMode.INOUT || mode == ParameterMode.OUT ) {
+				if ( mode == ParameterMode.INOUT || mode == ParameterMode.OUT ) {
+					if ( sqlTypes.length > 1 ) {
+						if ( ProcedureParameterExtractionAware.class.isInstance( hibernateType )
+								&& ( (ProcedureParameterExtractionAware) hibernateType ).canDoExtraction() ) {
+							// the type can handle multi-param extraction...
+						}
+						else {
+							// it cannot...
+							throw new UnsupportedOperationException(
+									"Type [" + hibernateType + "] does support multi-parameter value extraction"
+							);
+						}
+					}
+					for ( int i = 0; i < sqlTypes.length; i++ ) {
+						statement.registerOutParameter( startIndex + i, sqlTypes[i] );
+					}
+				}
+
+				if ( mode == ParameterMode.INOUT || mode == ParameterMode.IN ) {
+					if ( bind == null || bind.getValue() == null ) {
+						log.debugf(
+								"Stored procedure [%s] IN/INOUT parameter [%s] not bound; assuming procedure defines default value",
+								procedureCall.getProcedureName(),
+								this
+						);
+					}
+					else {
+						final Type typeToUse;
+						if ( bind.getExplicitTemporalType() != null && bind.getExplicitTemporalType() == TemporalType.TIMESTAMP ) {
+							typeToUse = hibernateType;
+						}
+						else if ( bind.getExplicitTemporalType() != null && bind.getExplicitTemporalType() == TemporalType.DATE ) {
+							typeToUse = DateType.INSTANCE;
+						}
+						else {
+							typeToUse = hibernateType;
+						}
+						typeToUse.nullSafeSet( statement, bind.getValue(), startIndex, session() );
+					}
+				}
+			}
+		}
+
+		public int[] getSqlTypes() {
+			return sqlTypes;
+		}
+
+		@Override
+		public StoredProcedureParameterBind getParameterBind() {
+			return bind;
+		}
+
+		@Override
+		public void bindValue(T value) {
+			this.bind = new StoredProcedureParameterBindImpl<T>( value );
+		}
+
+		@Override
+		public void bindValue(T value, TemporalType explicitTemporalType) {
+			if ( explicitTemporalType != null ) {
+				if ( ! isDateTimeType() ) {
+					throw new IllegalArgumentException( "TemporalType should not be specified for non date/time type" );
+				}
+			}
+			this.bind = new StoredProcedureParameterBindImpl<T>( value, explicitTemporalType );
+		}
+
+		private boolean isDateTimeType() {
+			return Date.class.isAssignableFrom( type )
+					|| Calendar.class.isAssignableFrom( type );
+		}
+
+		@Override
+		@SuppressWarnings("unchecked")
+		public T extract(CallableStatement statement) {
+			try {
+				if ( ProcedureParameterExtractionAware.class.isInstance( hibernateType ) ) {
+					return (T) ( (ProcedureParameterExtractionAware) hibernateType ).extract( statement, startIndex, session() );
+				}
+				else {
+					return (T) statement.getObject( startIndex );
+				}
+			}
+			catch (SQLException e) {
+				throw procedureCall.session().getFactory().getSQLExceptionHelper().convert(
+						e,
+						"Unable to extract OUT/INOUT parameter value"
+				);
+			}
+		}
+	}
+
+	public static class StoredProcedureParameterBindImpl<T> implements StoredProcedureParameterBind<T> {
+		private final T value;
+		private final TemporalType explicitTemporalType;
+
+		public StoredProcedureParameterBindImpl(T value) {
+			this( value, null );
+		}
+
+		public StoredProcedureParameterBindImpl(T value, TemporalType explicitTemporalType) {
+			this.value = value;
+			this.explicitTemporalType = explicitTemporalType;
+		}
+
+		@Override
+		public T getValue() {
+			return value;
+		}
+
+		@Override
+		public TemporalType getExplicitTemporalType() {
+			return explicitTemporalType;
+		}
+	}
+
+	public static class NamedStoredProcedureParameter<T> extends AbstractStoredProcedureParameterImpl<T> {
+		private final String name;
+
+		public NamedStoredProcedureParameter(
+				StoredProcedureCallImpl procedureCall,
+				String name,
+				ParameterMode mode,
+				Class<T> type) {
+			super( procedureCall, mode, type );
+			this.name = name;
+		}
+
+		@Override
+		public String getName() {
+			return name;
+		}
+	}
+
+	public static class PositionalStoredProcedureParameter<T> extends AbstractStoredProcedureParameterImpl<T> {
+		private final Integer position;
+
+		public PositionalStoredProcedureParameter(
+				StoredProcedureCallImpl procedureCall,
+				Integer position,
+				ParameterMode mode,
+				Class<T> type) {
+			super( procedureCall, mode, type );
+			this.position = position;
+		}
+
+		@Override
+		public Integer getPosition() {
+			return position;
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureOutputsImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureOutputsImpl.java
new file mode 100644
index 0000000000..f36a7075ad
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StoredProcedureOutputsImpl.java
@@ -0,0 +1,283 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.internal;
+
+import java.sql.CallableStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.util.List;
+import java.util.Map;
+import java.util.Set;
+
+import org.hibernate.JDBCException;
+import org.hibernate.StoredProcedureOutputs;
+import org.hibernate.StoredProcedureResultSetReturn;
+import org.hibernate.StoredProcedureReturn;
+import org.hibernate.StoredProcedureUpdateCountReturn;
+import org.hibernate.engine.spi.QueryParameters;
+import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.loader.custom.CustomLoader;
+import org.hibernate.loader.custom.CustomQuery;
+import org.hibernate.loader.custom.Return;
+import org.hibernate.loader.custom.sql.SQLQueryReturnProcessor;
+
+/**
+ * @author Steve Ebersole
+ */
+public class StoredProcedureOutputsImpl implements StoredProcedureOutputs {
+	private final StoredProcedureCallImpl procedureCall;
+	private final CallableStatement callableStatement;
+
+	private final CustomLoaderExtension loader;
+
+	private CurrentReturnDescriptor currentReturnDescriptor;
+	private boolean executed = false;
+
+	StoredProcedureOutputsImpl(StoredProcedureCallImpl procedureCall, CallableStatement callableStatement) {
+		this.procedureCall = procedureCall;
+		this.callableStatement = callableStatement;
+
+		// For now...
+		this.loader = buildSpecializedCustomLoader( procedureCall );
+	}
+
+	@Override
+	public Object getOutputParameterValue(String name) {
+		return procedureCall.getRegisteredParameter( name ).extract( callableStatement );
+	}
+
+	@Override
+	public Object getOutputParameterValue(int position) {
+		return procedureCall.getRegisteredParameter( position ).extract( callableStatement );
+	}
+
+	@Override
+	public boolean hasMoreReturns() {
+		if ( currentReturnDescriptor == null ) {
+			final boolean isResultSet;
+
+			if ( executed ) {
+				try {
+					isResultSet = callableStatement.getMoreResults();
+				}
+				catch (SQLException e) {
+					throw convert( e, "Error calling CallableStatement.getMoreResults" );
+				}
+			}
+			else {
+				try {
+					isResultSet = callableStatement.execute();
+				}
+				catch (SQLException e) {
+					throw convert( e, "Error calling CallableStatement.execute" );
+				}
+				executed = true;
+			}
+
+			int updateCount = -1;
+			if ( ! isResultSet ) {
+				try {
+					updateCount = callableStatement.getUpdateCount();
+				}
+				catch (SQLException e) {
+					throw convert( e, "Error calling CallableStatement.getUpdateCount" );
+				}
+			}
+
+			currentReturnDescriptor = new CurrentReturnDescriptor( isResultSet, updateCount );
+		}
+
+		return hasMoreResults( currentReturnDescriptor );
+	}
+
+	private boolean hasMoreResults(CurrentReturnDescriptor descriptor) {
+		return currentReturnDescriptor.isResultSet || currentReturnDescriptor.updateCount >= 0;
+	}
+
+	@Override
+	public StoredProcedureReturn getNextReturn() {
+		if ( currentReturnDescriptor == null ) {
+			if ( executed ) {
+				throw new IllegalStateException( "Unexpected condition" );
+			}
+			else {
+				throw new IllegalStateException( "hasMoreReturns() not called before getNextReturn()" );
+			}
+		}
+
+		if ( ! hasMoreResults( currentReturnDescriptor ) ) {
+			throw new IllegalStateException( "Results have been exhausted" );
+		}
+
+		CurrentReturnDescriptor copyReturnDescriptor = currentReturnDescriptor;
+		currentReturnDescriptor = null;
+
+		if ( copyReturnDescriptor.isResultSet ) {
+			try {
+				return new ResultSetReturn( this, callableStatement.getResultSet() );
+			}
+			catch (SQLException e) {
+				throw convert( e, "Error calling CallableStatement.getResultSet" );
+			}
+		}
+		else {
+			return new UpdateCountReturn( this, copyReturnDescriptor.updateCount );
+		}
+	}
+
+	protected JDBCException convert(SQLException e, String message) {
+		return procedureCall.session().getFactory().getSQLExceptionHelper().convert( e, message, procedureCall.getProcedureName() );
+	}
+
+	private static class CurrentReturnDescriptor {
+		private final boolean isResultSet;
+		private final int updateCount;
+
+		private CurrentReturnDescriptor(boolean resultSet, int updateCount) {
+			isResultSet = resultSet;
+			this.updateCount = updateCount;
+		}
+	}
+
+	private static class ResultSetReturn implements StoredProcedureResultSetReturn {
+		private final StoredProcedureOutputsImpl storedProcedureOutputs;
+		private final ResultSet resultSet;
+
+		public ResultSetReturn(StoredProcedureOutputsImpl storedProcedureOutputs, ResultSet resultSet) {
+			this.storedProcedureOutputs = storedProcedureOutputs;
+			this.resultSet = resultSet;
+		}
+
+		@Override
+		public boolean isResultSet() {
+			return true;
+		}
+
+		@Override
+		@SuppressWarnings("unchecked")
+		public List getResultList() {
+			try {
+				return storedProcedureOutputs.loader.processResultSet( resultSet );
+			}
+			catch (SQLException e) {
+				throw storedProcedureOutputs.convert( e, "Error calling ResultSet.next" );
+			}
+		}
+
+		@Override
+		public Object getSingleResult() {
+			List results = getResultList();
+			if ( results == null || results.isEmpty() ) {
+				return null;
+			}
+			else {
+				return results.get( 0 );
+			}
+		}
+	}
+
+	private class UpdateCountReturn implements StoredProcedureUpdateCountReturn {
+		private final StoredProcedureOutputsImpl storedProcedureOutputs;
+		private final int updateCount;
+
+		public UpdateCountReturn(StoredProcedureOutputsImpl storedProcedureOutputs, int updateCount) {
+			this.storedProcedureOutputs = storedProcedureOutputs;
+			this.updateCount = updateCount;
+		}
+
+		@Override
+		public int getUpdateCount() {
+			return updateCount;
+		}
+
+		@Override
+		public boolean isResultSet() {
+			return false;
+		}
+	}
+
+	private static CustomLoaderExtension buildSpecializedCustomLoader(final StoredProcedureCallImpl procedureCall) {
+		final SQLQueryReturnProcessor processor = new SQLQueryReturnProcessor(
+				procedureCall.getQueryReturns(),
+				procedureCall.session().getFactory()
+		);
+		processor.process();
+		final List<Return> customReturns = processor.generateCustomReturns( false );
+
+		CustomQuery customQuery = new CustomQuery() {
+			@Override
+			public String getSQL() {
+				return procedureCall.getProcedureName();
+			}
+
+			@Override
+			public Set<String> getQuerySpaces() {
+				return procedureCall.getSynchronizedQuerySpacesSet();
+			}
+
+			@Override
+			public Map getNamedParameterBindPoints() {
+				// no named parameters in terms of embedded in the SQL string
+				return null;
+			}
+
+			@Override
+			public List<Return> getCustomQueryReturns() {
+				return customReturns;
+			}
+		};
+
+		return new CustomLoaderExtension(
+				customQuery,
+				procedureCall.buildQueryParametersObject(),
+				procedureCall.session()
+		);
+	}
+
+	private static class CustomLoaderExtension extends CustomLoader {
+		private QueryParameters queryParameters;
+		private SessionImplementor session;
+
+		public CustomLoaderExtension(
+				CustomQuery customQuery,
+				QueryParameters queryParameters,
+				SessionImplementor session) {
+			super( customQuery, session.getFactory() );
+			this.queryParameters = queryParameters;
+			this.session = session;
+		}
+
+		public List processResultSet(ResultSet resultSet) throws SQLException {
+			super.autoDiscoverTypes( resultSet );
+			return super.processResultSet(
+					resultSet,
+					queryParameters,
+					session,
+					true,
+					null,
+					Integer.MAX_VALUE
+			);
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 486c62ca57..968d0e899b 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,2525 +1,2538 @@
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
+import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.cache.spi.FilterKey;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.QueryKey;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.dialect.pagination.NoopLimitHandler;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.hql.internal.HolderInstantiator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FetchingScrollableResultsImpl;
 import org.hibernate.internal.ScrollableResultsImpl;
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
 
     protected static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Loader.class.getName());
 
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
 			return new StringBuilder( comment.length() + sql.length() + 5 )
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
 			return session.generateEntityKey( optionalId, session.getEntityPersister( optionalEntityName, optionalObject ) );
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
 
-		return forcedResultTransformer == null ?
-				getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session ) :
-				forcedResultTransformer.transformTuple(
-						getResultRow( row, resultSet, session ),
-						getResultRowAliases()
-				)
+		return forcedResultTransformer == null
+				? getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session )
+				: forcedResultTransformer.transformTuple( getResultRow( row, resultSet, session ), getResultRowAliases() )
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
 			keys[ entitySpan - 1 ] = session.generateEntityKey( optionalId, persisters[ entitySpan - 1 ] );
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
 		final int maxRows = LimitHelper.hasMaxRows( selection ) ?
 				selection.getMaxRows() :
 				Integer.MAX_VALUE;
 
-		final int entitySpan = getEntityPersisters().length;
-
-		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final ResultSet rs = executeQueryStatement( queryParameters, false, session );
-		final PreparedStatement st = (PreparedStatement) rs.getStatement();
+		final Statement st = rs.getStatement();
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
+		try {
+			return processResultSet( rs, queryParameters, session, returnProxies, forcedResultTransformer, maxRows );
+		}
+		finally {
+			st.close();
+		}
+
+	}
+
+	protected List processResultSet(
+			ResultSet rs,
+			QueryParameters queryParameters,
+			SessionImplementor session,
+			boolean returnProxies,
+			ResultTransformer forcedResultTransformer,
+			int maxRows) throws SQLException {
+		final int entitySpan = getEntityPersisters().length;
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
+		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final List results = new ArrayList();
 
-		try {
-			handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
-			EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
-			LOG.trace( "Processing result set" );
-			int count;
-			for ( count = 0; count < maxRows && rs.next(); count++ ) {
-				LOG.debugf( "Result set row: %s", count );
-				Object result = getRowFromResultSet(
-						rs,
-						session,
-						queryParameters,
-						lockModesArray,
-						optionalObjectKey,
-						hydratedObjects,
-						keys,
-						returnProxies,
-						forcedResultTransformer
-				);
-				results.add( result );
-				if ( createSubselects ) {
-					subselectResultKeys.add(keys);
-					keys = new EntityKey[entitySpan]; //can't reuse in this case
-				}
+		handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
+		EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
+		LOG.trace( "Processing result set" );
+		int count;
+		for ( count = 0; count < maxRows && rs.next(); count++ ) {
+			LOG.debugf( "Result set row: %s", count );
+			Object result = getRowFromResultSet(
+					rs,
+					session,
+					queryParameters,
+					lockModesArray,
+					optionalObjectKey,
+					hydratedObjects,
+					keys,
+					returnProxies,
+					forcedResultTransformer
+			);
+			results.add( result );
+			if ( createSubselects ) {
+				subselectResultKeys.add(keys);
+				keys = new EntityKey[entitySpan]; //can't reuse in this case
 			}
+		}
 
-			LOG.tracev( "Done processing result set ({0} rows)", count );
+		LOG.tracev( "Done processing result set ({0} rows)", count );
 
-		}
-		finally {
-			st.close();
-		}
 		initializeEntitiesAndCollections( hydratedObjects, rs, session, queryParameters.isReadOnly( session ) );
 		if ( createSubselects ) createSubselects( subselectResultKeys, queryParameters, session );
 		return results;
-
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
 			LOG.tracev( "Total objects hydrated: {0}", hydratedObjectsSize );
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
-	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
-			throws SQLException, HibernateException {
+	protected Object getResultColumnOrRow(
+			Object[] row,
+			ResultTransformer transformer,
+			ResultSet rs,
+			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
-	protected Object[] getResultRow(Object[] row,
-														 ResultSet rs,
-														 SessionImplementor session)
-			throws SQLException, HibernateException {
+	protected Object[] getResultRow(
+			Object[] row,
+			ResultSet rs,
+			SessionImplementor session) throws SQLException, HibernateException {
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
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Found row of collection: %s",
 						MessageHelper.collectionInfoString( persister, collectionRowKey, getFactory() ) );
 			}
 
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
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Result set contains (possibly empty) collection: %s",
 						MessageHelper.collectionInfoString( persister, optionalKey, getFactory() ) );
 			}
 
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
 
 					if ( LOG.isDebugEnabled() ) {
 						LOG.debugf( "Result set contains (possibly empty) collection: %s",
 								MessageHelper.collectionInfoString( collectionPersisters[j], keys[i], getFactory() ) );
 					}
 
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
 					idType.isEqual( id, resultId, factory );
 
 			if ( idIsResultId ) resultId = id; //use the id passed in
 		}
 
 		return resultId == null ? null : session.generateEntityKey( resultId, persister );
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
 
 		if ( LOG.isDebugEnabled() ) LOG.debugf( "Result row: %s", StringHelper.toString( keys ) );
 
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
 		if ( !persister.isInstance( object ) ) {
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
-			);
+		);
 
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
 
 		if ( LOG.isTraceEnabled() )
 			LOG.tracev( "Initializing object from ResultSet: {0}", MessageHelper.infoString( persister, id, getFactory() ) );
 
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
 
 		final int firstRow = LimitHelper.getFirstRow( selection );
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
 
 	/**
 	 * Build LIMIT clause handler applicable for given selection criteria. Returns {@link NoopLimitHandler} delegate
 	 * if dialect does not support LIMIT expression or processed query does not use pagination.
 	 *
 	 * @param sql Query string.
 	 * @param selection Selection criteria.
 	 * @return LIMIT clause delegate.
 	 */
 	protected LimitHandler getLimitHandler(String sql, RowSelection selection) {
 		final LimitHandler limitHandler = getFactory().getDialect().buildLimitHandler( sql, selection );
 		return LimitHelper.useLimit( limitHandler, selection ) ? limitHandler : new NoopLimitHandler( sql, selection );
 	}
 
 	private ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {
 		final boolean canScroll = getFactory().getSettings().isScrollableResultSetsEnabled();
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
 	protected ResultSet executeQueryStatement(
 			final QueryParameters queryParameters,
 			final boolean scroll,
 			final SessionImplementor session) throws SQLException {
 		// Processing query filters.
 		queryParameters.processFilters( getSQLString(), session );
 
 		// Applying LIMIT clause.
-		final LimitHandler limitHandler = getLimitHandler( queryParameters.getFilteredSQL(), queryParameters.getRowSelection() );
+		final LimitHandler limitHandler = getLimitHandler(
+				queryParameters.getFilteredSQL(),
+				queryParameters.getRowSelection()
+		);
 		String sql = limitHandler.getProcessedSql();
 
 		// Adding locks and comments.
 		sql = preprocessSQL( sql, queryParameters, getFactory().getDialect() );
 
 		final PreparedStatement st = prepareQueryStatement( sql, queryParameters, limitHandler, scroll, session );
 		return getResultSet( st, queryParameters.getRowSelection(), limitHandler, queryParameters.hasAutoDiscoverScalarTypes(), session );
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
 	        final String sql,
 	        final QueryParameters queryParameters,
 	        final LimitHandler limitHandler,
 	        final boolean scroll,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 		final Dialect dialect = getFactory().getDialect();
 		final RowSelection selection = queryParameters.getRowSelection();
 		boolean useLimit = LimitHelper.useLimit( limitHandler, selection );
 		boolean hasFirstRow = LimitHelper.hasFirstRow( selection );
 		boolean useLimitOffset = hasFirstRow && useLimit && limitHandler.supportsLimitOffset();
 		boolean callable = queryParameters.isCallable();
 		final ScrollMode scrollMode = getScrollMode( scroll, hasFirstRow, useLimitOffset, queryParameters );
 
 		PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(
 				sql,
 				callable,
 				scrollMode
 		);
 
 		try {
 
 			int col = 1;
 			//TODO: can we limit stored procedures ?!
 			col += limitHandler.bindLimitParametersAtStartOfQuery( st, col );
 
 			if (callable) {
 				col = dialect.registerResultSetOutParameter( (CallableStatement)st, col );
 			}
 
 			col += bindParameterValues( st, queryParameters, col, session );
 
 			col += limitHandler.bindLimitParametersAtEndOfQuery( st, col );
 
 			limitHandler.setMaxRows( st );
 
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
 
 			LOG.tracev( "Bound [{0}] parameters total", col );
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
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			int result = 0;
 			while ( iter.hasNext() ) {
 				Map.Entry e = ( Map.Entry ) iter.next();
 				String name = ( String ) e.getKey();
 				TypedValue typedval = ( TypedValue ) e.getValue();
 				int[] locs = getNamedParameterLocs( name );
 				for ( int i = 0; i < locs.length; i++ ) {
 					if ( debugEnabled ) LOG.debugf( "bindNamedParameters() %s -> %s [%s]", typedval.getValue(), name, locs[i] + startIndex );
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
 	 * Execute given <tt>PreparedStatement</tt>, advance to the first result and return SQL <tt>ResultSet</tt>.
 	 */
 	protected final ResultSet getResultSet(
 	        final PreparedStatement st,
 	        final RowSelection selection,
 	        final LimitHandler limitHandler,
 	        final boolean autodiscovertypes,
 	        final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		try {
 			ResultSet rs = st.executeQuery();
 			rs = wrapResultSetIfEnabled( rs , session );
 
 			if ( !limitHandler.supportsLimitOffset() || !LimitHelper.useLimit( limitHandler, selection ) ) {
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
 				LOG.debugf( "Wrapping result set [%s]", rs );
 				return session.getFactory()
 						.getJdbcServices()
 						.getResultSetWrapper().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch(SQLException e) {
 				LOG.unableToWrapResultSet( e );
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
 			LOG.trace( "Building columnName->columnIndex cache" );
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
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Loading entity: %s", MessageHelper.infoString( persister, id, identifierType, getFactory() ) );
 		}
 
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
 
 		LOG.debug( "Done entity load" );
 
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
 
 		LOG.debug( "Loading collection element by index" );
 
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
 
 		LOG.debug( "Done entity load" );
 
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
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Batch loading entity: %s", MessageHelper.infoString( persister, ids, getFactory() ) );
 
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
 
 		LOG.debug( "Done entity batch load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that initialize collections
 	 */
 	public final void loadCollection(
 	        final SessionImplementor session,
 	        final Serializable id,
 	        final Type type) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ) );
 
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
 
 		LOG.debug( "Done loading collection" );
 
 	}
 
 	/**
 	 * Called by wrappers that batch initialize collections
 	 */
 	public final void loadCollectionBatch(
 	        final SessionImplementor session,
 	        final Serializable[] ids,
 	        final Type type) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Batch loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ) );
 
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
 
 		LOG.debug( "Done batch load" );
 
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
 
 		if ( querySpaces == null || querySpaces.size() == 0 )
 			LOG.tracev( "Unexpected querySpaces is {0}", ( querySpaces == null ? querySpaces : "empty" ) );
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
 			SessionImplementor session,
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
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Set querySpaces,
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
 					factory.getStatisticsImplementor()
 							.queryCacheMiss( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 				else {
 					factory.getStatisticsImplementor()
 							.queryCacheHit( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 			}
 		}
 
 		return result;
 	}
 
 	private EntityPersister getEntityPersister(EntityType entityType) {
 		return factory.getEntityPersister( entityType.getAssociatedEntityName() );
 	}
 
 	private void putResultInQueryCache(
 			final SessionImplementor session,
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
 				factory.getStatisticsImplementor()
 						.queryCachePut( getQueryIdentifier(), queryCache.getRegion().getName() );
 			}
 		}
 	}
 
 	/**
 	 * Actually execute a query, ignoring the query cache
 	 */
 
 	protected List doList(final SessionImplementor session, final QueryParameters queryParameters)
 			throws HibernateException {
 		return doList( session, queryParameters, null);
 	}
 
 	private List doList(final SessionImplementor session,
 						final QueryParameters queryParameters,
 						final ResultTransformer forcedResultTransformer)
 			throws HibernateException {
 
 		final boolean stats = getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections( session, queryParameters, true, forcedResultTransformer );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not execute query",
 			        getSQLString()
 				);
 		}
 
 		if ( stats ) {
 			getFactory().getStatisticsImplementor().queryExecuted(
 					getQueryIdentifier(),
 					result.size(),
 					System.currentTimeMillis() - startTime
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
 		return;
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
 	 * @return The ScrollableResults instance.
 	 * @throws HibernateException Indicates an error executing the query, or constructing
 	 * the ScrollableResults.
 	 */
 	protected ScrollableResults scroll(
 	        final QueryParameters queryParameters,
 	        final Type[] returnTypes,
 	        final HolderInstantiator holderInstantiator,
 	        final SessionImplementor session) throws HibernateException {
 
 		checkScrollability();
 
 		final boolean stats = getQueryIdentifier() != null &&
 				getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		try {
 			final ResultSet rs = executeQueryStatement( queryParameters, true, session );
 			final PreparedStatement st = (PreparedStatement) rs.getStatement();
 
 			if ( stats ) {
 				getFactory().getStatisticsImplementor().queryExecuted(
 						getQueryIdentifier(),
 						0,
 						System.currentTimeMillis() - startTime
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
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not execute query using scroll",
 			        getSQLString()
 				);
 		}
 
 	}
 
 	/**
 	 * Calculate and cache select-clause suffixes. Must be
 	 * called by subclasses after instantiation.
 	 */
 	protected void postInstantiate() {}
 
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomQuery.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomQuery.java
index be3fe4eab7..ef607040cc 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomQuery.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomQuery.java
@@ -1,77 +1,77 @@
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
 package org.hibernate.loader.custom;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 /**
  * Extension point allowing any SQL query with named and positional parameters
  * to be executed by Hibernate, returning managed entities, collections and
  * simple scalar values.
  * 
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface CustomQuery {
 	/**
 	 * The SQL query string to be performed.
 	 *
 	 * @return The SQL statement string.
 	 */
 	public String getSQL();
 
 	/**
 	 * Any query spaces to apply to the query execution.  Query spaces are
 	 * used in Hibernate's auto-flushing mechanism to determine which
 	 * entities need to be checked for pending changes.
 	 *
 	 * @return The query spaces
 	 */
-	public Set getQuerySpaces();
+	public Set<String> getQuerySpaces();
 
 	/**
 	 * A map representing positions within the supplied {@link #getSQL query} to
 	 * which we need to bind named parameters.
 	 * <p/>
 	 * Optional, may return null if no named parameters.
 	 * <p/>
 	 * The structure of the returned map (if one) as follows:<ol>
 	 * <li>The keys into the map are the named parameter names</li>
 	 * <li>The corresponding value is either an {@link Integer} if the
 	 * parameter occurs only once in the query; or a List of Integers if the
 	 * parameter occurs more than once</li>
 	 * </ol>
 	 */
 	public Map getNamedParameterBindPoints();
 
 	/**
 	 * A collection of {@link Return descriptors} describing the
 	 * JDBC result set to be expected and how to map this result set.
 	 *
 	 * @return List of return descriptors.
 	 */
-	public List getCustomQueryReturns();
+	public List<Return> getCustomQueryReturns();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java
index eb3c85323e..fe2cc4713d 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/sql/SQLQueryReturnProcessor.java
@@ -1,525 +1,521 @@
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
 package org.hibernate.loader.custom.sql;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryCollectionReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryJoinReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryNonScalarReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.BasicLoader;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.ColumnEntityAliases;
 import org.hibernate.loader.DefaultEntityAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.GeneratedCollectionAliases;
 import org.hibernate.loader.custom.CollectionFetchReturn;
 import org.hibernate.loader.custom.CollectionReturn;
 import org.hibernate.loader.custom.ColumnCollectionAliases;
 import org.hibernate.loader.custom.EntityFetchReturn;
 import org.hibernate.loader.custom.FetchReturn;
 import org.hibernate.loader.custom.NonScalarReturn;
 import org.hibernate.loader.custom.Return;
 import org.hibernate.loader.custom.RootReturn;
 import org.hibernate.loader.custom.ScalarReturn;
 import org.hibernate.persister.collection.SQLLoadableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.SQLLoadable;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Responsible for processing the series of {@link org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn returns}
  * defined by a {@link org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification} and
  * breaking them down into a series of {@link Return returns} for use within the
  * {@link org.hibernate.loader.custom.CustomLoader}.
  *
  * @author Gavin King
  * @author Max Andersen
  * @author Steve Ebersole
  */
 public class SQLQueryReturnProcessor {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        SQLQueryReturnProcessor.class.getName());
 
 	private NativeSQLQueryReturn[] queryReturns;
 
 //	private final List persisters = new ArrayList();
 
 	private final Map alias2Return = new HashMap();
 	private final Map alias2OwnerAlias = new HashMap();
 
 	private final Map alias2Persister = new HashMap();
 	private final Map alias2Suffix = new HashMap();
 
 	private final Map alias2CollectionPersister = new HashMap();
 	private final Map alias2CollectionSuffix = new HashMap();
 
 	private final Map entityPropertyResultMaps = new HashMap();
 	private final Map collectionPropertyResultMaps = new HashMap();
 
 //	private final List scalarTypes = new ArrayList();
 //	private final List scalarColumnAliases = new ArrayList();
 
 	private final SessionFactoryImplementor factory;
 
 //	private List collectionOwnerAliases = new ArrayList();
 //	private List collectionAliases = new ArrayList();
 //	private List collectionPersisters = new ArrayList();
 //	private List collectionResults = new ArrayList();
 
 	private int entitySuffixSeed = 0;
 	private int collectionSuffixSeed = 0;
 
 
 	public SQLQueryReturnProcessor(NativeSQLQueryReturn[] queryReturns, SessionFactoryImplementor factory) {
 		this.queryReturns = queryReturns;
 		this.factory = factory;
 	}
 
 	/*package*/ class ResultAliasContext {
 		public SQLLoadable getEntityPersister(String alias) {
 			return ( SQLLoadable ) alias2Persister.get( alias );
 		}
 
 		public SQLLoadableCollection getCollectionPersister(String alias) {
 			return ( SQLLoadableCollection ) alias2CollectionPersister.get( alias );
 		}
 
 		public String getEntitySuffix(String alias) {
 			return ( String ) alias2Suffix.get( alias );
 		}
 
 		public String getCollectionSuffix(String alias) {
 			return ( String ) alias2CollectionSuffix.get ( alias );
 		}
 
 		public String getOwnerAlias(String alias) {
 			return ( String ) alias2OwnerAlias.get( alias );
 		}
 
 		public Map getPropertyResultsMap(String alias) {
 			return internalGetPropertyResultsMap( alias );
 		}
 	}
 
 	private Map internalGetPropertyResultsMap(String alias) {
 		NativeSQLQueryReturn rtn = ( NativeSQLQueryReturn ) alias2Return.get( alias );
 		if ( rtn instanceof NativeSQLQueryNonScalarReturn ) {
 			return ( ( NativeSQLQueryNonScalarReturn ) rtn ).getPropertyResultsMap();
 		}
 		else {
 			return null;
 		}
 	}
 
 	private boolean hasPropertyResultMap(String alias) {
 		Map propertyMaps = internalGetPropertyResultsMap( alias );
 		return propertyMaps != null && ! propertyMaps.isEmpty();
 	}
 
 	public ResultAliasContext process() {
 		// first, break down the returns into maps keyed by alias
 		// so that role returns can be more easily resolved to their owners
-		for ( int i = 0; i < queryReturns.length; i++ ) {
-			if ( queryReturns[i] instanceof NativeSQLQueryNonScalarReturn ) {
-				NativeSQLQueryNonScalarReturn rtn = ( NativeSQLQueryNonScalarReturn ) queryReturns[i];
+		for ( NativeSQLQueryReturn queryReturn : queryReturns ) {
+			if ( queryReturn instanceof NativeSQLQueryNonScalarReturn ) {
+				NativeSQLQueryNonScalarReturn rtn = (NativeSQLQueryNonScalarReturn) queryReturn;
 				alias2Return.put( rtn.getAlias(), rtn );
 				if ( rtn instanceof NativeSQLQueryJoinReturn ) {
-					NativeSQLQueryJoinReturn fetchReturn = ( NativeSQLQueryJoinReturn ) rtn;
+					NativeSQLQueryJoinReturn fetchReturn = (NativeSQLQueryJoinReturn) rtn;
 					alias2OwnerAlias.put( fetchReturn.getAlias(), fetchReturn.getOwnerAlias() );
 				}
 			}
 		}
 
 		// Now, process the returns
-		for ( int i = 0; i < queryReturns.length; i++ ) {
-			processReturn( queryReturns[i] );
+		for ( NativeSQLQueryReturn queryReturn : queryReturns ) {
+			processReturn( queryReturn );
 		}
 
 		return new ResultAliasContext();
 	}
 
-	public List generateCustomReturns(boolean queryHadAliases) {
-		List customReturns = new ArrayList();
-		Map customReturnsByAlias = new HashMap();
-		for ( int i = 0; i < queryReturns.length; i++ ) {
-			if ( queryReturns[i] instanceof NativeSQLQueryScalarReturn ) {
-				NativeSQLQueryScalarReturn rtn = ( NativeSQLQueryScalarReturn ) queryReturns[i];
+	public List<Return> generateCustomReturns(boolean queryHadAliases) {
+		List<Return> customReturns = new ArrayList<Return>();
+		Map<String,Return> customReturnsByAlias = new HashMap<String,Return>();
+		for ( NativeSQLQueryReturn queryReturn : queryReturns ) {
+			if ( queryReturn instanceof NativeSQLQueryScalarReturn ) {
+				NativeSQLQueryScalarReturn rtn = (NativeSQLQueryScalarReturn) queryReturn;
 				customReturns.add( new ScalarReturn( rtn.getType(), rtn.getColumnAlias() ) );
 			}
-			else if ( queryReturns[i] instanceof NativeSQLQueryRootReturn ) {
-				NativeSQLQueryRootReturn rtn = ( NativeSQLQueryRootReturn ) queryReturns[i];
+			else if ( queryReturn instanceof NativeSQLQueryRootReturn ) {
+				NativeSQLQueryRootReturn rtn = (NativeSQLQueryRootReturn) queryReturn;
 				String alias = rtn.getAlias();
 				EntityAliases entityAliases;
 				if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
 					entityAliases = new DefaultEntityAliases(
-							( Map ) entityPropertyResultMaps.get( alias ),
-							( SQLLoadable ) alias2Persister.get( alias ),
-							( String ) alias2Suffix.get( alias )
+							(Map) entityPropertyResultMaps.get( alias ),
+							(SQLLoadable) alias2Persister.get( alias ),
+							(String) alias2Suffix.get( alias )
 					);
 				}
 				else {
 					entityAliases = new ColumnEntityAliases(
-							( Map ) entityPropertyResultMaps.get( alias ),
-							( SQLLoadable ) alias2Persister.get( alias ),
-							( String ) alias2Suffix.get( alias )
+							(Map) entityPropertyResultMaps.get( alias ),
+							(SQLLoadable) alias2Persister.get( alias ),
+							(String) alias2Suffix.get( alias )
 					);
 				}
 				RootReturn customReturn = new RootReturn(
 						alias,
 						rtn.getReturnEntityName(),
 						entityAliases,
 						rtn.getLockMode()
 				);
 				customReturns.add( customReturn );
 				customReturnsByAlias.put( rtn.getAlias(), customReturn );
 			}
-			else if ( queryReturns[i] instanceof NativeSQLQueryCollectionReturn ) {
-				NativeSQLQueryCollectionReturn rtn = ( NativeSQLQueryCollectionReturn ) queryReturns[i];
+			else if ( queryReturn instanceof NativeSQLQueryCollectionReturn ) {
+				NativeSQLQueryCollectionReturn rtn = (NativeSQLQueryCollectionReturn) queryReturn;
 				String alias = rtn.getAlias();
-				SQLLoadableCollection persister = ( SQLLoadableCollection ) alias2CollectionPersister.get( alias );
+				SQLLoadableCollection persister = (SQLLoadableCollection) alias2CollectionPersister.get( alias );
 				boolean isEntityElements = persister.getElementType().isEntityType();
 				CollectionAliases collectionAliases;
 				EntityAliases elementEntityAliases = null;
 				if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
 					collectionAliases = new GeneratedCollectionAliases(
-							( Map ) collectionPropertyResultMaps.get( alias ),
-							( SQLLoadableCollection ) alias2CollectionPersister.get( alias ),
-							( String ) alias2CollectionSuffix.get( alias )
+							(Map) collectionPropertyResultMaps.get( alias ),
+							(SQLLoadableCollection) alias2CollectionPersister.get( alias ),
+							(String) alias2CollectionSuffix.get( alias )
 					);
 					if ( isEntityElements ) {
 						elementEntityAliases = new DefaultEntityAliases(
-								( Map ) entityPropertyResultMaps.get( alias ),
-								( SQLLoadable ) alias2Persister.get( alias ),
-								( String ) alias2Suffix.get( alias )
+								(Map) entityPropertyResultMaps.get( alias ),
+								(SQLLoadable) alias2Persister.get( alias ),
+								(String) alias2Suffix.get( alias )
 						);
 					}
 				}
 				else {
 					collectionAliases = new ColumnCollectionAliases(
-							( Map ) collectionPropertyResultMaps.get( alias ),
-							( SQLLoadableCollection ) alias2CollectionPersister.get( alias )
+							(Map) collectionPropertyResultMaps.get( alias ),
+							(SQLLoadableCollection) alias2CollectionPersister.get( alias )
 					);
 					if ( isEntityElements ) {
 						elementEntityAliases = new ColumnEntityAliases(
-								( Map ) entityPropertyResultMaps.get( alias ),
-								( SQLLoadable ) alias2Persister.get( alias ),
-								( String ) alias2Suffix.get( alias )
+								(Map) entityPropertyResultMaps.get( alias ),
+								(SQLLoadable) alias2Persister.get( alias ),
+								(String) alias2Suffix.get( alias )
 						);
 					}
 				}
 				CollectionReturn customReturn = new CollectionReturn(
 						alias,
 						rtn.getOwnerEntityName(),
 						rtn.getOwnerProperty(),
 						collectionAliases,
-				        elementEntityAliases,
+						elementEntityAliases,
 						rtn.getLockMode()
 				);
 				customReturns.add( customReturn );
 				customReturnsByAlias.put( rtn.getAlias(), customReturn );
 			}
-			else if ( queryReturns[i] instanceof NativeSQLQueryJoinReturn ) {
-				NativeSQLQueryJoinReturn rtn = ( NativeSQLQueryJoinReturn ) queryReturns[i];
+			else if ( queryReturn instanceof NativeSQLQueryJoinReturn ) {
+				NativeSQLQueryJoinReturn rtn = (NativeSQLQueryJoinReturn) queryReturn;
 				String alias = rtn.getAlias();
 				FetchReturn customReturn;
-				NonScalarReturn ownerCustomReturn = ( NonScalarReturn ) customReturnsByAlias.get( rtn.getOwnerAlias() );
+				NonScalarReturn ownerCustomReturn = (NonScalarReturn) customReturnsByAlias.get( rtn.getOwnerAlias() );
 				if ( alias2CollectionPersister.containsKey( alias ) ) {
-					SQLLoadableCollection persister = ( SQLLoadableCollection ) alias2CollectionPersister.get( alias );
+					SQLLoadableCollection persister = (SQLLoadableCollection) alias2CollectionPersister.get( alias );
 					boolean isEntityElements = persister.getElementType().isEntityType();
 					CollectionAliases collectionAliases;
 					EntityAliases elementEntityAliases = null;
 					if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
 						collectionAliases = new GeneratedCollectionAliases(
-								( Map ) collectionPropertyResultMaps.get( alias ),
+								(Map) collectionPropertyResultMaps.get( alias ),
 								persister,
-								( String ) alias2CollectionSuffix.get( alias )
+								(String) alias2CollectionSuffix.get( alias )
 						);
 						if ( isEntityElements ) {
 							elementEntityAliases = new DefaultEntityAliases(
-									( Map ) entityPropertyResultMaps.get( alias ),
-									( SQLLoadable ) alias2Persister.get( alias ),
-									( String ) alias2Suffix.get( alias )
+									(Map) entityPropertyResultMaps.get( alias ),
+									(SQLLoadable) alias2Persister.get( alias ),
+									(String) alias2Suffix.get( alias )
 							);
 						}
 					}
 					else {
 						collectionAliases = new ColumnCollectionAliases(
-								( Map ) collectionPropertyResultMaps.get( alias ),
+								(Map) collectionPropertyResultMaps.get( alias ),
 								persister
 						);
 						if ( isEntityElements ) {
 							elementEntityAliases = new ColumnEntityAliases(
-									( Map ) entityPropertyResultMaps.get( alias ),
-									( SQLLoadable ) alias2Persister.get( alias ),
-									( String ) alias2Suffix.get( alias )
+									(Map) entityPropertyResultMaps.get( alias ),
+									(SQLLoadable) alias2Persister.get( alias ),
+									(String) alias2Suffix.get( alias )
 							);
 						}
 					}
 					customReturn = new CollectionFetchReturn(
 							alias,
 							ownerCustomReturn,
 							rtn.getOwnerProperty(),
 							collectionAliases,
-					        elementEntityAliases,
+							elementEntityAliases,
 							rtn.getLockMode()
 					);
 				}
 				else {
 					EntityAliases entityAliases;
 					if ( queryHadAliases || hasPropertyResultMap( alias ) ) {
 						entityAliases = new DefaultEntityAliases(
-								( Map ) entityPropertyResultMaps.get( alias ),
-								( SQLLoadable ) alias2Persister.get( alias ),
-								( String ) alias2Suffix.get( alias )
+								(Map) entityPropertyResultMaps.get( alias ),
+								(SQLLoadable) alias2Persister.get( alias ),
+								(String) alias2Suffix.get( alias )
 						);
 					}
 					else {
 						entityAliases = new ColumnEntityAliases(
-								( Map ) entityPropertyResultMaps.get( alias ),
-								( SQLLoadable ) alias2Persister.get( alias ),
-								( String ) alias2Suffix.get( alias )
+								(Map) entityPropertyResultMaps.get( alias ),
+								(SQLLoadable) alias2Persister.get( alias ),
+								(String) alias2Suffix.get( alias )
 						);
 					}
 					customReturn = new EntityFetchReturn(
 							alias,
 							entityAliases,
 							ownerCustomReturn,
 							rtn.getOwnerProperty(),
 							rtn.getLockMode()
 					);
 				}
 				customReturns.add( customReturn );
 				customReturnsByAlias.put( alias, customReturn );
 			}
 		}
 		return customReturns;
 	}
 
 	private SQLLoadable getSQLLoadable(String entityName) throws MappingException {
 		EntityPersister persister = factory.getEntityPersister( entityName );
 		if ( !(persister instanceof SQLLoadable) ) {
 			throw new MappingException( "class persister is not SQLLoadable: " + entityName );
 		}
 		return (SQLLoadable) persister;
 	}
 
 	private String generateEntitySuffix() {
 		return BasicLoader.generateSuffixes( entitySuffixSeed++, 1 )[0];
 	}
 
 	private String generateCollectionSuffix() {
 		return collectionSuffixSeed++ + "__";
 	}
 
 	private void processReturn(NativeSQLQueryReturn rtn) {
 		if ( rtn instanceof NativeSQLQueryScalarReturn ) {
 			processScalarReturn( ( NativeSQLQueryScalarReturn ) rtn );
 		}
 		else if ( rtn instanceof NativeSQLQueryRootReturn ) {
 			processRootReturn( ( NativeSQLQueryRootReturn ) rtn );
 		}
 		else if ( rtn instanceof NativeSQLQueryCollectionReturn ) {
 			processCollectionReturn( ( NativeSQLQueryCollectionReturn ) rtn );
 		}
 		else {
 			processJoinReturn( ( NativeSQLQueryJoinReturn ) rtn );
 		}
 	}
 
 	private void processScalarReturn(NativeSQLQueryScalarReturn typeReturn) {
 //		scalarColumnAliases.add( typeReturn.getColumnAlias() );
 //		scalarTypes.add( typeReturn.getType() );
 	}
 
 	private void processRootReturn(NativeSQLQueryRootReturn rootReturn) {
 		if ( alias2Persister.containsKey( rootReturn.getAlias() ) ) {
 			// already been processed...
 			return;
 		}
 
 		SQLLoadable persister = getSQLLoadable( rootReturn.getReturnEntityName() );
 		addPersister( rootReturn.getAlias(), rootReturn.getPropertyResultsMap(), persister );
 	}
 
-	/**
-	 * @param propertyResult
-	 * @param persister
-	 */
 	private void addPersister(String alias, Map propertyResult, SQLLoadable persister) {
 		alias2Persister.put( alias, persister );
 		String suffix = generateEntitySuffix();
 		LOG.tracev( "Mapping alias [{0}] to entity-suffix [{1}]", alias, suffix );
 		alias2Suffix.put( alias, suffix );
 		entityPropertyResultMaps.put( alias, propertyResult );
 	}
 
 	private void addCollection(String role, String alias, Map propertyResults) {
 		SQLLoadableCollection collectionPersister = ( SQLLoadableCollection ) factory.getCollectionPersister( role );
 		alias2CollectionPersister.put( alias, collectionPersister );
 		String suffix = generateCollectionSuffix();
 		LOG.tracev( "Mapping alias [{0}] to collection-suffix [{1}]", alias, suffix );
 		alias2CollectionSuffix.put( alias, suffix );
 		collectionPropertyResultMaps.put( alias, propertyResults );
 
 		if ( collectionPersister.isOneToMany() || collectionPersister.isManyToMany() ) {
 			SQLLoadable persister = ( SQLLoadable ) collectionPersister.getElementPersister();
 			addPersister( alias, filter( propertyResults ), persister );
 		}
 	}
 
 	private Map filter(Map propertyResults) {
 		Map result = new HashMap( propertyResults.size() );
 
 		String keyPrefix = "element.";
 
 		Iterator iter = propertyResults.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry element = ( Map.Entry ) iter.next();
 			String path = ( String ) element.getKey();
 			if ( path.startsWith( keyPrefix ) ) {
 				result.put( path.substring( keyPrefix.length() ), element.getValue() );
 			}
 		}
 
 		return result;
 	}
 
 	private void processCollectionReturn(NativeSQLQueryCollectionReturn collectionReturn) {
 		// we are initializing an owned collection
 		//collectionOwners.add( new Integer(-1) );
 //		collectionOwnerAliases.add( null );
 		String role = collectionReturn.getOwnerEntityName() + '.' + collectionReturn.getOwnerProperty();
 		addCollection(
 				role,
 				collectionReturn.getAlias(),
 				collectionReturn.getPropertyResultsMap()
 		);
 	}
 
 	private void processJoinReturn(NativeSQLQueryJoinReturn fetchReturn) {
 		String alias = fetchReturn.getAlias();
 //		if ( alias2Persister.containsKey( alias ) || collectionAliases.contains( alias ) ) {
 		if ( alias2Persister.containsKey( alias ) || alias2CollectionPersister.containsKey( alias ) ) {
 			// already been processed...
 			return;
 		}
 
 		String ownerAlias = fetchReturn.getOwnerAlias();
 
 		// Make sure the owner alias is known...
 		if ( !alias2Return.containsKey( ownerAlias ) ) {
 			throw new HibernateException( "Owner alias [" + ownerAlias + "] is unknown for alias [" + alias + "]" );
 		}
 
 		// If this return's alias has not been processed yet, do so b4 further processing of this return
 		if ( !alias2Persister.containsKey( ownerAlias ) ) {
 			NativeSQLQueryNonScalarReturn ownerReturn = ( NativeSQLQueryNonScalarReturn ) alias2Return.get(ownerAlias);
 			processReturn( ownerReturn );
 		}
 
 		SQLLoadable ownerPersister = ( SQLLoadable ) alias2Persister.get( ownerAlias );
 		Type returnType = ownerPersister.getPropertyType( fetchReturn.getOwnerProperty() );
 
 		if ( returnType.isCollectionType() ) {
 			String role = ownerPersister.getEntityName() + '.' + fetchReturn.getOwnerProperty();
 			addCollection( role, alias, fetchReturn.getPropertyResultsMap() );
 //			collectionOwnerAliases.add( ownerAlias );
 		}
 		else if ( returnType.isEntityType() ) {
 			EntityType eType = ( EntityType ) returnType;
 			String returnEntityName = eType.getAssociatedEntityName();
 			SQLLoadable persister = getSQLLoadable( returnEntityName );
 			addPersister( alias, fetchReturn.getPropertyResultsMap(), persister );
 		}
 
 	}
 
 //	public List getCollectionAliases() {
 //		return collectionAliases;
 //	}
 //
 //	/*public List getCollectionOwners() {
 //		return collectionOwners;
 //	}*/
 //
 //	public List getCollectionOwnerAliases() {
 //		return collectionOwnerAliases;
 //	}
 //
 //	public List getCollectionPersisters() {
 //		return collectionPersisters;
 //	}
 //
 //	public Map getAlias2Persister() {
 //		return alias2Persister;
 //	}
 //
 //	/*public boolean isCollectionInitializer() {
 //		return isCollectionInitializer;
 //	}*/
 //
 ////	public List getPersisters() {
 ////		return persisters;
 ////	}
 //
 //	public Map getAlias2OwnerAlias() {
 //		return alias2OwnerAlias;
 //	}
 //
 //	public List getScalarTypes() {
 //		return scalarTypes;
 //	}
 //	public List getScalarColumnAliases() {
 //		return scalarColumnAliases;
 //	}
 //
 //	public List getPropertyResults() {
 //		return propertyResults;
 //	}
 //
 //	public List getCollectionPropertyResults() {
 //		return collectionResults;
 //	}
 //
 //
 //	public Map getAlias2Return() {
 //		return alias2Return;
 //	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
index 6f784a9020..f2c452c70c 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
@@ -1,389 +1,418 @@
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
 
 import java.io.Serializable;
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.dom4j.Node;
 
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.MutabilityPlan;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
- * TODO : javadoc
+ * Convenience base class for {@link BasicType} implementations
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractStandardBasicType<T>
-		implements BasicType, StringRepresentableType<T>, XmlRepresentableType<T> {
+		implements BasicType, StringRepresentableType<T>, XmlRepresentableType<T>, ProcedureParameterExtractionAware<T> {
 
 	private static final Size DEFAULT_SIZE = new Size( 19, 2, 255, Size.LobMultiplier.NONE ); // to match legacy behavior
 	private final Size dictatedSize = new Size();
 
 	private final SqlTypeDescriptor sqlTypeDescriptor;
 	private final JavaTypeDescriptor<T> javaTypeDescriptor;
 
 	public AbstractStandardBasicType(SqlTypeDescriptor sqlTypeDescriptor, JavaTypeDescriptor<T> javaTypeDescriptor) {
 		this.sqlTypeDescriptor = sqlTypeDescriptor;
 		this.javaTypeDescriptor = javaTypeDescriptor;
 	}
 
 	public T fromString(String string) {
 		return javaTypeDescriptor.fromString( string );
 	}
 
 	public String toString(T value) {
 		return javaTypeDescriptor.toString( value );
 	}
 
 	public T fromStringValue(String xml) throws HibernateException {
 		return fromString( xml );
 	}
 
 	public String toXMLString(T value, SessionFactoryImplementor factory) throws HibernateException {
 		return toString( value );
 	}
 
 	public T fromXMLString(String xml, Mapping factory) throws HibernateException {
 		return StringHelper.isEmpty( xml ) ? null : fromStringValue( xml );
 	}
 
 	protected MutabilityPlan<T> getMutabilityPlan() {
 		return javaTypeDescriptor.getMutabilityPlan();
 	}
 
 	protected T getReplacement(T original, T target, SessionImplementor session) {
 		if ( !isMutable() ) {
 			return original;
 		}
 		else if ( isEqual( original, target ) ) {
 			return original;
 		}
 		else {
 			return deepCopy( original );
 		}
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return value == null ? ArrayHelper.FALSE : ArrayHelper.TRUE;
 	}
 
 	public String[] getRegistrationKeys() {
 		return registerUnderJavaType()
 				? new String[] { getName(), javaTypeDescriptor.getJavaTypeClass().getName() }
 				: new String[] { getName() };
 	}
 
 	protected boolean registerUnderJavaType() {
 		return false;
 	}
 
 	protected static Size getDefaultSize() {
 		return DEFAULT_SIZE;
 	}
 
 	protected Size getDictatedSize() {
 		return dictatedSize;
 	}
 
 
 	// final implementations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public final JavaTypeDescriptor<T> getJavaTypeDescriptor() {
 		return javaTypeDescriptor;
 	}
 
 	public final SqlTypeDescriptor getSqlTypeDescriptor() {
 		return sqlTypeDescriptor;
 	}
 
 	public final Class getReturnedClass() {
 		return javaTypeDescriptor.getJavaTypeClass();
 	}
 
 	public final int getColumnSpan(Mapping mapping) throws MappingException {
 		return sqlTypes( mapping ).length;
 	}
 
 	public final int[] sqlTypes(Mapping mapping) throws MappingException {
 		return new int[] { sqlTypeDescriptor.getSqlType() };
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return new Size[] { getDictatedSize() };
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return new Size[] { getDefaultSize() };
 	}
 
 	public final boolean isAssociationType() {
 		return false;
 	}
 
 	public final boolean isCollectionType() {
 		return false;
 	}
 
 	public final boolean isComponentType() {
 		return false;
 	}
 
 	public final boolean isEntityType() {
 		return false;
 	}
 
 	public final boolean isAnyType() {
 		return false;
 	}
 
 	public final boolean isXMLElement() {
 		return false;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isSame(Object x, Object y) {
 		return isEqual( x, y );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
 		return isEqual( x, y );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isEqual(Object one, Object another) {
 		return javaTypeDescriptor.areEqual( (T) one, (T) another );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final int getHashCode(Object x) {
 		return javaTypeDescriptor.extractHashCode( (T) x );
 	}
 
 	public final int getHashCode(Object x, SessionFactoryImplementor factory) {
 		return getHashCode( x );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final int compare(Object x, Object y) {
 		return javaTypeDescriptor.getComparator().compare( (T) x, (T) y );
 	}
 
 	public final boolean isDirty(Object old, Object current, SessionImplementor session) {
 		return isDirty( old, current );
 	}
 
 	public final boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) {
 		return checkable[0] && isDirty( old, current );
 	}
 
 	protected final boolean isDirty(Object old, Object current) {
 		return !isSame( old, current );
 	}
 
 	public final boolean isModified(
 			Object oldHydratedState,
 			Object currentState,
 			boolean[] checkable,
 			SessionImplementor session) {
 		return isDirty( oldHydratedState, currentState );
 	}
 
 	public final Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws SQLException {
 		return nullSafeGet( rs, names[0], session );
 	}
 
 	public final Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 			throws SQLException {
 		return nullSafeGet( rs, name, session );
 	}
 
 	public final T nullSafeGet(ResultSet rs, String name, final SessionImplementor session) throws SQLException {
 		// todo : have SessionImplementor extend WrapperOptions
 		final WrapperOptions options = new WrapperOptions() {
 			public boolean useStreamForLobBinding() {
 				return Environment.useStreamsForBinary();
 			}
 
 			public LobCreator getLobCreator() {
 				return Hibernate.getLobCreator( session );
 			}
 
 			public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 				final SqlTypeDescriptor remapped = sqlTypeDescriptor.canBeRemapped()
 						? session.getFactory().getDialect().remapSqlTypeDescriptor( sqlTypeDescriptor )
 						: sqlTypeDescriptor;
 				return remapped == null ? sqlTypeDescriptor : remapped;
 			}
 		};
 
 		return nullSafeGet( rs, name, options );
 	}
 
 	protected final T nullSafeGet(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 		return remapSqlTypeDescriptor( options ).getExtractor( javaTypeDescriptor ).extract( rs, name, options );
 	}
 
 	public Object get(ResultSet rs, String name, SessionImplementor session) throws HibernateException, SQLException {
 		return nullSafeGet( rs, name, session );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			final SessionImplementor session) throws SQLException {
 		// todo : have SessionImplementor extend WrapperOptions
 		final WrapperOptions options = new WrapperOptions() {
 			public boolean useStreamForLobBinding() {
 				return Environment.useStreamsForBinary();
 			}
 
 			public LobCreator getLobCreator() {
 				return Hibernate.getLobCreator( session );
 			}
 
 			public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 				final SqlTypeDescriptor remapped = sqlTypeDescriptor.canBeRemapped()
 						? session.getFactory().getDialect().remapSqlTypeDescriptor( sqlTypeDescriptor )
 						: sqlTypeDescriptor;
 				return remapped == null ? sqlTypeDescriptor : remapped;
 			}
 		};
 
 		nullSafeSet( st, value, index, options );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected final void nullSafeSet(PreparedStatement st, Object value, int index, WrapperOptions options) throws SQLException {
 		remapSqlTypeDescriptor( options ).getBinder( javaTypeDescriptor ).bind( st, ( T ) value, index, options );
 	}
 
 	protected SqlTypeDescriptor remapSqlTypeDescriptor(WrapperOptions options) {
 		return options.remapSqlTypeDescriptor( sqlTypeDescriptor );
 	}
 
 	public void set(PreparedStatement st, T value, int index, SessionImplementor session) throws HibernateException, SQLException {
 		nullSafeSet( st, value, index, session );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final String toLoggableString(Object value, SessionFactoryImplementor factory) {
 		return javaTypeDescriptor.extractLoggableRepresentation( (T) value );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) {
 		node.setText( toString( (T) value ) );
 	}
 
 	public final Object fromXMLNode(Node xml, Mapping factory) {
 		return fromString( xml.getText() );
 	}
 
 	public final boolean isMutable() {
 		return getMutabilityPlan().isMutable();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return deepCopy( (T) value );
 	}
 
 	protected final T deepCopy(T value) {
 		return getMutabilityPlan().deepCopy( value );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return getMutabilityPlan().disassemble( (T) value );
 	}
 
 	public final Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
 		return getMutabilityPlan().assemble( cached );
 	}
 
 	public final void beforeAssemble(Serializable cached, SessionImplementor session) {
 	}
 
 	public final Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return nullSafeGet(rs, names, session, owner);
 	}
 
 	public final Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return value;
 	}
 
 	public final Object semiResolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return value;
 	}
 
 	public final Type getSemiResolvedType(SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache) {
 		return getReplacement( (T) original, (T) target, session );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache,
 			ForeignKeyDirection foreignKeyDirection) {
 		return ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT == foreignKeyDirection
 				? getReplacement( (T) original, (T) target, session )
 				: target;
 	}
+
+	@Override
+	public boolean canDoExtraction() {
+		return true;
+	}
+
+	@Override
+	public T extract(CallableStatement statement, int startIndex, final SessionImplementor session) throws SQLException {
+		// todo : have SessionImplementor extend WrapperOptions
+		final WrapperOptions options = new WrapperOptions() {
+			public boolean useStreamForLobBinding() {
+				return Environment.useStreamsForBinary();
+			}
+
+			public LobCreator getLobCreator() {
+				return Hibernate.getLobCreator( session );
+			}
+
+			public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
+				final SqlTypeDescriptor remapped = sqlTypeDescriptor.canBeRemapped()
+						? session.getFactory().getDialect().remapSqlTypeDescriptor( sqlTypeDescriptor )
+						: sqlTypeDescriptor;
+				return remapped == null ? sqlTypeDescriptor : remapped;
+			}
+		};
+
+		return remapSqlTypeDescriptor( options ).getExtractor( javaTypeDescriptor ).extract( statement, startIndex, options );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
index 74310ca0b7..9c69f10b61 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
@@ -1,723 +1,775 @@
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
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.tuple.component.ComponentTuplizer;
 
 /**
  * Handles "component" mappings
  *
  * @author Gavin King
  */
-public class ComponentType extends AbstractType implements CompositeType {
+public class ComponentType extends AbstractType implements CompositeType, ProcedureParameterExtractionAware {
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyNullability;
 	protected final int propertySpan;
 	private final CascadeStyle[] cascade;
 	private final FetchMode[] joinedFetch;
 	private final boolean isKey;
 
 	protected final EntityMode entityMode;
 	protected final ComponentTuplizer componentTuplizer;
 
 	public ComponentType(TypeFactory.TypeScope typeScope, ComponentMetamodel metamodel) {
 		this.typeScope = typeScope;
 		// for now, just "re-flatten" the metamodel since this is temporary stuff anyway (HHH-1907)
 		this.isKey = metamodel.isKey();
 		this.propertySpan = metamodel.getPropertySpan();
 		this.propertyNames = new String[ propertySpan ];
 		this.propertyTypes = new Type[ propertySpan ];
 		this.propertyNullability = new boolean[ propertySpan ];
 		this.cascade = new CascadeStyle[ propertySpan ];
 		this.joinedFetch = new FetchMode[ propertySpan ];
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			StandardProperty prop = metamodel.getProperty( i );
 			this.propertyNames[i] = prop.getName();
 			this.propertyTypes[i] = prop.getType();
 			this.propertyNullability[i] = prop.isNullable();
 			this.cascade[i] = prop.getCascadeStyle();
 			this.joinedFetch[i] = prop.getFetchMode();
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
 
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		int span = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			span += propertyTypes[i].getColumnSpan( mapping );
 		}
 		return span;
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		int[] sqlTypes = new int[getColumnSpan( mapping )];
 		int n = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int[] subtypes = propertyTypes[i].sqlTypes( mapping );
 			for ( int j = 0; j < subtypes.length; j++ ) {
 				sqlTypes[n++] = subtypes[j];
 			}
 		}
 		return sqlTypes;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
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
 		final Size[] sizes = new Size[ getColumnSpan( mapping ) ];
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
     public boolean isEqual(Object x, Object y)
 			throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i] ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
     public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( xvalues[i], yvalues[i], factory ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
     public int compare(Object x, Object y) {
 		if ( x == y ) {
 			return 0;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int propertyCompare = propertyTypes[i].compare( xvalues[i], yvalues[i] );
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
     public int getHashCode(Object x) {
 		int result = 17;
 		Object[] values = getPropertyValues( x, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = values[i];
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y );
 			}
 		}
 		return result;
 	}
 
 	@Override
     public int getHashCode(Object x, SessionFactoryImplementor factory) {
 		int result = 17;
 		Object[] values = getPropertyValues( x, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = values[i];
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y, factory );
 			}
 		}
 		return result;
 	}
 
 	@Override
     public boolean isDirty(Object x, Object y, SessionImplementor session)
 			throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < xvalues.length; i++ ) {
 			if ( propertyTypes[i].isDirty( xvalues[i], yvalues[i], session ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public boolean isDirty(Object x, Object y, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		int loc = 0;
 		for ( int i = 0; i < xvalues.length; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			if ( len <= 1 ) {
 				final boolean dirty = ( len == 0 || checkable[loc] ) &&
 				                      propertyTypes[i].isDirty( xvalues[i], yvalues[i], session );
 				if ( dirty ) {
 					return true;
 				}
 			}
 			else {
 				boolean[] subcheckable = new boolean[len];
 				System.arraycopy( checkable, loc, subcheckable, 0, len );
 				final boolean dirty = propertyTypes[i].isDirty( xvalues[i], yvalues[i], subcheckable, session );
 				if ( dirty ) {
 					return true;
 				}
 			}
 			loc += len;
 		}
 		return false;
 	}
 
 	@Override
     public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 
 		if ( current == null ) {
 			return old != null;
 		}
 		if ( old == null ) {
 			return current != null;
 		}
 		Object[] currentValues = getPropertyValues( current, session );
 		Object[] oldValues = ( Object[] ) old;
 		int loc = 0;
 		for ( int i = 0; i < currentValues.length; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			boolean[] subcheckable = new boolean[len];
 			System.arraycopy( checkable, loc, subcheckable, 0, len );
 			if ( propertyTypes[i].isModified( oldValues[i], currentValues[i], subcheckable, session ) ) {
 				return true;
 			}
 			loc += len;
 		}
 		return false;
 
 	}
 
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( hydrate( rs, names, session, owner ), session, owner );
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int begin, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, entityMode );
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			propertyTypes[i].nullSafeSet( st, subvalues[i], begin, session );
 			begin += propertyTypes[i].getColumnSpan( session.getFactory() );
 		}
 	}
 
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
 
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	public Object getPropertyValue(Object component, int i, SessionImplementor session)
 			throws HibernateException {
 		return getPropertyValue( component, i, entityMode );
 	}
 
 	public Object getPropertyValue(Object component, int i, EntityMode entityMode)
 			throws HibernateException {
 		return componentTuplizer.getPropertyValue( component, i );
 	}
 
 	public Object[] getPropertyValues(Object component, SessionImplementor session)
 			throws HibernateException {
 		return getPropertyValues( component, entityMode );
 	}
 
 	public Object[] getPropertyValues(Object component, EntityMode entityMode)
 			throws HibernateException {
 		return componentTuplizer.getPropertyValues( component );
 	}
 
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
 			throws HibernateException {
 		componentTuplizer.setPropertyValues( component, values );
 	}
 
 	public Type[] getSubtypes() {
 		return propertyTypes;
 	}
 
 	public String getName() {
 		return "component" + ArrayHelper.toString( propertyNames );
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		Map result = new HashMap();
 		if ( entityMode == null ) {
 			throw new ClassCastException( value.getClass().getName() );
 		}
 		Object[] values = getPropertyValues( value, entityMode );
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			result.put( propertyNames[i], propertyTypes[i].toLoggableString( values[i], factory ) );
 		}
 		return StringHelper.unqualify( getName() ) + result.toString();
 	}
 
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
 
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
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return cascade[i];
 	}
 
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
 			Object[] values = ( Object[] ) object;
 			Object[] assembled = new Object[values.length];
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				assembled[i] = propertyTypes[i].assemble( ( Serializable ) values[i], session, owner );
 			}
 			Object result = instantiate( owner, session );
 			setPropertyValues( result, assembled, entityMode );
 			return result;
 		}
 	}
 
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
 			Object[] values = ( Object[] ) value;
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
 
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	@Override
     public boolean isXMLElement() {
 		return true;
 	}
 
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		return xml;
 	}
 
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
 		replaceNode( node, ( Element ) value );
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan( mapping ) ];
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
 
 	public boolean isEmbedded() {
 		return false;
 	}
 
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
+
+	private Boolean canDoExtraction;
+
+	@Override
+	public boolean canDoExtraction() {
+		if ( canDoExtraction == null ) {
+			canDoExtraction = determineIfProcedureParamExtractionCanBePerformed();
+		}
+		return canDoExtraction;
+	}
+
+	private boolean determineIfProcedureParamExtractionCanBePerformed() {
+		for ( Type propertyType : propertyTypes ) {
+			if ( ! ProcedureParameterExtractionAware.class.isInstance( propertyType ) ) {
+				return false;
+			}
+			if ( ! ( (ProcedureParameterExtractionAware) propertyType ).canDoExtraction() ) {
+				return false;
+			}
+		}
+		return true;
+	}
+
+	@Override
+	public Object extract(CallableStatement statement, int startIndex, SessionImplementor session) throws SQLException {
+		Object[] values = new Object[propertySpan];
+
+		int currentIndex = startIndex;
+		boolean notNull = false;
+		for ( int i = 0; i < propertySpan; i++ ) {
+			// we know this cast is safe from canDoExtraction
+			final ProcedureParameterExtractionAware propertyType = (ProcedureParameterExtractionAware) propertyTypes[i];
+			final Object value = propertyType.extract( statement, currentIndex, session );
+			if ( value == null ) {
+				if ( isKey ) {
+					return null; //different nullability rules for pk/fk
+				}
+			}
+			else {
+				notNull = true;
+			}
+			values[i] = value;
+			currentIndex += propertyType.getColumnSpan( session.getFactory() );
+		}
+
+		if ( ! notNull ) {
+			values = null;
+		}
+
+		return resolve( values, session, null );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/PostgresUUIDType.java b/hibernate-core/src/main/java/org/hibernate/type/PostgresUUIDType.java
index 3b2e3cdbf7..e199d8881a 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/PostgresUUIDType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/PostgresUUIDType.java
@@ -1,90 +1,96 @@
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
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.UUID;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
 import org.hibernate.type.descriptor.sql.BasicBinder;
 import org.hibernate.type.descriptor.sql.BasicExtractor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * Specialized type mapping for {@link UUID} and the Postgres UUID data type (which is mapped as OTHER in its
  * JDBC driver).
  *
  * @author Steve Ebersole
  * @author David Driscoll
  */
 public class PostgresUUIDType extends AbstractSingleColumnStandardBasicType<UUID> {
 	public static final PostgresUUIDType INSTANCE = new PostgresUUIDType();
 
 	public PostgresUUIDType() {
 		super( PostgresUUIDSqlTypeDescriptor.INSTANCE, UUIDTypeDescriptor.INSTANCE );
 	}
 
 	public String getName() {
 		return "pg-uuid";
 	}
 
 	public static class PostgresUUIDSqlTypeDescriptor implements SqlTypeDescriptor {
 		public static final PostgresUUIDSqlTypeDescriptor INSTANCE = new PostgresUUIDSqlTypeDescriptor();
 
 		public int getSqlType() {
 			// ugh
 			return Types.OTHER;
 		}
 
 		@Override
 		public boolean canBeRemapped() {
 			return true;
 		}
 
 		public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
 				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 					st.setObject( index, javaTypeDescriptor.unwrap( value, UUID.class, options ), getSqlType() );
 				}
 			};
 		}
 
 		public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 			return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 				@Override
 				protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 					return javaTypeDescriptor.wrap( rs.getObject( name ), options );
 				}
+
+				@Override
+				protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+					return javaTypeDescriptor.wrap( statement.getObject( index ), options );
+				}
 			};
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ProcedureParameterExtractionAware.java b/hibernate-core/src/main/java/org/hibernate/type/ProcedureParameterExtractionAware.java
new file mode 100644
index 0000000000..d4530cbf07
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/type/ProcedureParameterExtractionAware.java
@@ -0,0 +1,58 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.type;
+
+import java.sql.CallableStatement;
+import java.sql.SQLException;
+
+import org.hibernate.engine.spi.SessionImplementor;
+
+/**
+ * Optional {@link Type} contract for implementations that are aware of how to extract values from
+ * store procedure OUT/INOUT parameters.
+ *
+ * @author Steve Ebersole
+ */
+public interface ProcedureParameterExtractionAware<T> extends Type {
+	/**
+	 * Can the given instance of this type actually perform the parameter value extractions?
+	 *
+	 * @return {@code true} indicates that @{link #extract} calls will not fail due to {@link IllegalStateException}.
+	 */
+	public boolean canDoExtraction();
+
+	/**
+	 * Perform the extraction
+	 *
+	 * @param statement The CallableStatement from which to extract the parameter value(s).
+	 * @param startIndex The parameter index from which to start extracting; assumes the values (if multiple) are contiguous
+	 * @param session The originating session
+	 *
+	 * @return The extracted value.
+	 *
+	 * @throws SQLException Indicates an issue calling into the CallableStatement
+	 * @throws IllegalStateException Thrown if this method is called on instances that return {@code false} for {@link #canDoExtraction}
+	 */
+	public T extract(CallableStatement statement, int startIndex, SessionImplementor session) throws SQLException;
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/ValueExtractor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/ValueExtractor.java
index 0194cad357..4e99135566 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/ValueExtractor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/ValueExtractor.java
@@ -1,46 +1,50 @@
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
 package org.hibernate.type.descriptor;
+
+import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 /**
- * Contract for extracting a value from a {@link ResultSet}.
+ * Contract for extracting value via JDBC (from {@link ResultSet} or as output param from {@link CallableStatement}).
  *
  * @author Steve Ebersole
  */
 public interface ValueExtractor<X> {
 	/**
 	 * Extract value from result set
 	 *
 	 * @param rs The result set from which to extract the value
 	 * @param name The name by which to extract the value from the result set
 	 * @param options The options
 	 *
 	 * @return The extracted value
 	 *
 	 * @throws SQLException Indicates a JDBC error occurred.
 	 */
 	public X extract(ResultSet rs, String name, WrapperOptions options) throws SQLException;
+
+	public X extract(CallableStatement rs, int index, WrapperOptions options) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
index 72643cc4dc..792f39d2f8 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BasicExtractor.java
@@ -1,93 +1,123 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Convenience base implementation of {@link org.hibernate.type.descriptor.ValueExtractor}
  *
  * @author Steve Ebersole
  */
 public abstract class BasicExtractor<J> implements ValueExtractor<J> {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, BasicExtractor.class.getName() );
 
 	private final JavaTypeDescriptor<J> javaDescriptor;
 	private final SqlTypeDescriptor sqlDescriptor;
 
 	public BasicExtractor(JavaTypeDescriptor<J> javaDescriptor, SqlTypeDescriptor sqlDescriptor) {
 		this.javaDescriptor = javaDescriptor;
 		this.sqlDescriptor = sqlDescriptor;
 	}
 
 	public JavaTypeDescriptor<J> getJavaDescriptor() {
 		return javaDescriptor;
 	}
 
 	public SqlTypeDescriptor getSqlDescriptor() {
 		return sqlDescriptor;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public J extract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 		final J value = doExtract( rs, name, options );
 		if ( value == null || rs.wasNull() ) {
 			LOG.tracev( "Found [null] as column [{0}]", name );
 			return null;
 		}
 		else {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Found [{0}] as column [{1}]", getJavaDescriptor().extractLoggableRepresentation( value ), name );
 			}
 			return value;
 		}
 	}
 
 	/**
 	 * Perform the extraction.
 	 * <p/>
 	 * Called from {@link #extract}.  Null checking of the value (as well as consulting {@link ResultSet#wasNull}) is
 	 * done there.
 	 *
 	 * @param rs The result set
 	 * @param name The value name in the result set
 	 * @param options The binding options
 	 *
 	 * @return The extracted value.
 	 *
 	 * @throws SQLException Indicates a problem access the result set
 	 */
 	protected abstract J doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException;
+
+	@Override
+	public J extract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+		final J value = doExtract( statement, index, options );
+		if ( value == null || statement.wasNull() ) {
+			LOG.tracev( "Found [null] as procedure output  parameter [{0}]", index );
+			return null;
+		}
+		else {
+			if ( LOG.isTraceEnabled() ) {
+				LOG.tracev( "Found [{0}] as procedure output parameter [{1}]", getJavaDescriptor().extractLoggableRepresentation( value ), index );
+			}
+			return value;
+		}
+	}
+
+	/**
+	 * Perform the extraction.
+	 * <p/>
+	 * Called from {@link #extract}.  Null checking of the value (as well as consulting {@link ResultSet#wasNull}) is
+	 * done there.
+	 *
+	 * @param statement The callable statement containing the output parameter
+	 * @param index The index (position) of the output parameter
+	 * @param options The binding options
+	 *
+	 * @return The extracted value.
+	 *
+	 * @throws SQLException Indicates a problem accessing the parameter value
+	 */
+	protected abstract J doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
index e861fc0b1b..cf2103d90e 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BigIntTypeDescriptor.java
@@ -1,70 +1,76 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#BIGINT BIGINT} handling.
  *
  * @author Steve Ebersole
  */
 public class BigIntTypeDescriptor implements SqlTypeDescriptor {
 	public static final BigIntTypeDescriptor INSTANCE = new BigIntTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.BIGINT;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setLong( index, javaTypeDescriptor.unwrap( value, Long.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getLong( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getLong( index ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
index a632106326..a60fa08d4a 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BitTypeDescriptor.java
@@ -1,73 +1,79 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#BIT BIT} handling.
  * <p/>
  * Note that JDBC is very specific about its use of the type BIT to mean a single binary digit, whereas
  * SQL defines BIT having a parameterized length.
  *
  * @author Steve Ebersole
  */
 public class BitTypeDescriptor implements SqlTypeDescriptor {
 	public static final BitTypeDescriptor INSTANCE = new BitTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.BIT;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setBoolean( index, javaTypeDescriptor.unwrap( value, Boolean.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBoolean( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getBoolean( index ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
index 9b61c15469..1cda8b54a1 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BlobTypeDescriptor.java
@@ -1,135 +1,141 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.sql.Blob;
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.BinaryStream;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#BLOB BLOB} handling.
  *
  * @author Steve Ebersole
  */
 public abstract class BlobTypeDescriptor implements SqlTypeDescriptor {
 
 	private BlobTypeDescriptor() {}
 
 	public static final BlobTypeDescriptor DEFAULT =
 			new BlobTypeDescriptor() {
 				@Override
                 public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 							if ( options.useStreamForLobBinding() ) {
 								STREAM_BINDING.getBlobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 							else if ( byte[].class.isInstance( value ) ) {
 								// performance shortcut for binding BLOB data in byte[] format
 								PRIMITIVE_ARRAY_BINDING.getBlobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 							else {
 								BLOB_BINDING.getBlobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 						}
 					};
 				}
 			};
 
 	public static final BlobTypeDescriptor PRIMITIVE_ARRAY_BINDING =
 			new BlobTypeDescriptor() {
 				@Override
                 public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						public void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							st.setBytes( index, javaTypeDescriptor.unwrap( value, byte[].class, options ) );
 						}
 					};
 				}
 			};
 
 	public static final BlobTypeDescriptor BLOB_BINDING =
 			new BlobTypeDescriptor() {
 				@Override
                 public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							st.setBlob( index, javaTypeDescriptor.unwrap( value, Blob.class, options ) );
 						}
 					};
 				}
 			};
 
 	public static final BlobTypeDescriptor STREAM_BINDING =
 			new BlobTypeDescriptor() {
 				@Override
                 public <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							final BinaryStream binaryStream = javaTypeDescriptor.unwrap( value, BinaryStream.class, options );
 							st.setBinaryStream( index, binaryStream.getInputStream(), binaryStream.getLength() );
 						}
 					};
 				}
 			};
 
 	protected abstract <X> BasicBinder<X> getBlobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor);
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBlob( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getBlob( index ), options );
+			}
 		};
 	}
 
 	public int getSqlType() {
 		return Types.BLOB;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return getBlobBinder( javaTypeDescriptor );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BooleanTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BooleanTypeDescriptor.java
index a4ae309bf4..afa54c280f 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BooleanTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/BooleanTypeDescriptor.java
@@ -1,70 +1,76 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link java.sql.Types#BOOLEAN BOOLEAN} handling.
  *
  * @author Steve Ebersole
  */
 public class BooleanTypeDescriptor implements SqlTypeDescriptor {
 	public static final BooleanTypeDescriptor INSTANCE = new BooleanTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.BOOLEAN;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setBoolean( index, javaTypeDescriptor.unwrap( value, Boolean.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBoolean( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getBoolean( index ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
index 5d9fe02696..696df0e78e 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/ClobTypeDescriptor.java
@@ -1,112 +1,118 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.Clob;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.CharacterStream;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#CLOB CLOB} handling.
  *
  * @author Steve Ebersole
  */
 public abstract class ClobTypeDescriptor implements SqlTypeDescriptor {
 
 	public static final ClobTypeDescriptor DEFAULT =
 			new ClobTypeDescriptor() {
 				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 							if ( options.useStreamForLobBinding() ) {
 								STREAM_BINDING.getClobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 							else {
 								CLOB_BINDING.getClobBinder( javaTypeDescriptor ).doBind( st, value, index, options );
 							}
 						}
 					};
 				}
 			};
 
 	public static final ClobTypeDescriptor CLOB_BINDING =
 			new ClobTypeDescriptor() {
 				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							st.setClob( index, javaTypeDescriptor.unwrap( value, Clob.class, options ) );
 						}
 					};
 				}
 			};
 
 	public static final ClobTypeDescriptor STREAM_BINDING =
 			new ClobTypeDescriptor() {
 				public <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 								throws SQLException {
 							final CharacterStream characterStream = javaTypeDescriptor.unwrap( value, CharacterStream.class, options );
 							st.setCharacterStream( index, characterStream.getReader(), characterStream.getLength() );
 						}
 					};
 				}
 			};
 
 	protected abstract <X> BasicBinder<X> getClobBinder(final JavaTypeDescriptor<X> javaTypeDescriptor);
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return getClobBinder( javaTypeDescriptor );
 	}
 
 	public int getSqlType() {
 		return Types.CLOB;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getClob( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getClob( index ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
index 4b6dcf6668..4bb7a702e2 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DateTypeDescriptor.java
@@ -1,71 +1,77 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.Date;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#DATE DATE} handling.
  *
  * @author Steve Ebersole
  */
 public class DateTypeDescriptor implements SqlTypeDescriptor {
 	public static final DateTypeDescriptor INSTANCE = new DateTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.DATE;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setDate( index, javaTypeDescriptor.unwrap( value, Date.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getDate( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getDate( index ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
index 0431047e87..397c96f269 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DecimalTypeDescriptor.java
@@ -1,71 +1,77 @@
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
 package org.hibernate.type.descriptor.sql;
 
 import java.math.BigDecimal;
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#DECIMAL DECIMAL} handling.
  *
  * @author Steve Ebersole
  */
 public class DecimalTypeDescriptor implements SqlTypeDescriptor {
 	public static final DecimalTypeDescriptor INSTANCE = new DecimalTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.DECIMAL;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setBigDecimal( index, javaTypeDescriptor.unwrap( value, BigDecimal.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getBigDecimal( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getBigDecimal( index ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
index 8006dabc66..13b6a1a045 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/DoubleTypeDescriptor.java
@@ -1,70 +1,76 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#DOUBLE DOUBLE} handling.
  *
  * @author Steve Ebersole
  */
 public class DoubleTypeDescriptor implements SqlTypeDescriptor {
 	public static final DoubleTypeDescriptor INSTANCE = new DoubleTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.DOUBLE;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setDouble( index, javaTypeDescriptor.unwrap( value, Double.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getDouble( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getDouble( index ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
index 8676cfbc16..8834e2d598 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/IntegerTypeDescriptor.java
@@ -1,70 +1,76 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#INTEGER INTEGER} handling.
  *
  * @author Steve Ebersole
  */
 public class IntegerTypeDescriptor implements SqlTypeDescriptor {
 	public static final IntegerTypeDescriptor INSTANCE = new IntegerTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.INTEGER;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setInt( index, javaTypeDescriptor.unwrap( value, Integer.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getInt( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getInt( index ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
index f57fcdcdb8..85c2a15559 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/RealTypeDescriptor.java
@@ -1,70 +1,76 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#REAL REAL} handling.
  *
  * @author Steve Ebersole
  */
 public class RealTypeDescriptor implements SqlTypeDescriptor {
 	public static final RealTypeDescriptor INSTANCE = new RealTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.REAL;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setFloat( index, javaTypeDescriptor.unwrap( value, Float.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getFloat( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getFloat( index ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
index b980eca205..0786ea242c 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SmallIntTypeDescriptor.java
@@ -1,70 +1,76 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#SMALLINT SMALLINT} handling.
  *
  * @author Steve Ebersole
  */
 public class SmallIntTypeDescriptor implements SqlTypeDescriptor {
 	public static final SmallIntTypeDescriptor INSTANCE = new SmallIntTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.SMALLINT;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setShort( index, javaTypeDescriptor.unwrap( value, Short.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getShort( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getShort( index ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
index 8915930f07..2b1b1b034c 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimeTypeDescriptor.java
@@ -1,71 +1,77 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Time;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#TIME TIME} handling.
  *
  * @author Steve Ebersole
  */
 public class TimeTypeDescriptor implements SqlTypeDescriptor {
 	public static final TimeTypeDescriptor INSTANCE = new TimeTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.TIME;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setTime( index, javaTypeDescriptor.unwrap( value, Time.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getTime( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getTime( index ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
index 28c903c6b5..4401becfc8 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TimestampTypeDescriptor.java
@@ -1,71 +1,77 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Timestamp;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#TIMESTAMP TIMESTAMP} handling.
  *
  * @author Steve Ebersole
  */
 public class TimestampTypeDescriptor implements SqlTypeDescriptor {
 	public static final TimestampTypeDescriptor INSTANCE = new TimestampTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.TIMESTAMP;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setTimestamp( index, javaTypeDescriptor.unwrap( value, Timestamp.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getTimestamp( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getTimestamp( index ), options );
+			}
 		};
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
index 0fa283a8aa..8418a28526 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/TinyIntTypeDescriptor.java
@@ -1,73 +1,79 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#TINYINT TINYINT} handling.
  * <p/>
  * Note that <tt>JDBC</tt> states that TINYINT should be mapped to either byte or short, but points out
  * that using byte can in fact lead to loss of data.
  *
  * @author Steve Ebersole
  */
 public class TinyIntTypeDescriptor implements SqlTypeDescriptor {
 	public static final TinyIntTypeDescriptor INSTANCE = new TinyIntTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.TINYINT;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setByte( index, javaTypeDescriptor.unwrap( value, Byte.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getByte( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getByte( index ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
index 4f75a4aa84..7c7d28a807 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarbinaryTypeDescriptor.java
@@ -1,71 +1,76 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#VARBINARY VARBINARY} handling.
  *
  * @author Steve Ebersole
  */
 public class VarbinaryTypeDescriptor implements SqlTypeDescriptor {
 	public static final VarbinaryTypeDescriptor INSTANCE = new VarbinaryTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.VARBINARY;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setBytes( index, javaTypeDescriptor.unwrap( value, byte[].class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
-				final byte[] bytes = rs.getBytes( name );
-				return javaTypeDescriptor.wrap( bytes, options );
+				return javaTypeDescriptor.wrap( rs.getBytes( name ), options );
+			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getBytes( index ), options );
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
index c5300fca50..50d5f3c5a9 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/VarcharTypeDescriptor.java
@@ -1,70 +1,76 @@
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
 package org.hibernate.type.descriptor.sql;
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for {@link Types#VARCHAR VARCHAR} handling.
  *
  * @author Steve Ebersole
  */
 public class VarcharTypeDescriptor implements SqlTypeDescriptor {
 	public static final VarcharTypeDescriptor INSTANCE = new VarcharTypeDescriptor();
 
 	public int getSqlType() {
 		return Types.VARCHAR;
 	}
 
 	@Override
 	public boolean canBeRemapped() {
 		return true;
 	}
 
 	public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicBinder<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 				st.setString( index, javaTypeDescriptor.unwrap( value, String.class, options ) );
 			}
 		};
 	}
 
 	public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 		return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 			@Override
 			protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 				return javaTypeDescriptor.wrap( rs.getString( name ), options );
 			}
+
+			@Override
+			protected X doExtract(CallableStatement statement, int index, WrapperOptions options) throws SQLException {
+				return javaTypeDescriptor.wrap( statement.getString( index ), options );
+			}
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/sql/storedproc/StoredProcedureTest.java b/hibernate-core/src/test/java/org/hibernate/test/sql/storedproc/StoredProcedureTest.java
new file mode 100644
index 0000000000..f4e54c800b
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/sql/storedproc/StoredProcedureTest.java
@@ -0,0 +1,116 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.test.sql.storedproc;
+
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import java.util.List;
+
+import org.hibernate.HibernateException;
+import org.hibernate.SQLQuery;
+import org.hibernate.Session;
+import org.hibernate.StoredProcedureCall;
+import org.hibernate.StoredProcedureOutputs;
+import org.hibernate.StoredProcedureResultSetReturn;
+import org.hibernate.StoredProcedureReturn;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.dialect.Dialect;
+import org.hibernate.dialect.H2Dialect;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.mapping.AuxiliaryDatabaseObject;
+
+import org.junit.Before;
+import org.junit.Test;
+
+import org.hibernate.testing.RequiresDialect;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.hibernate.testing.junit4.ExtraAssertions;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertNotNull;
+import static org.junit.Assert.assertTrue;
+
+/**
+ * @author Steve Ebersole
+ */
+@RequiresDialect( H2Dialect.class )
+public class StoredProcedureTest extends BaseCoreFunctionalTestCase {
+// this is not working in H2
+//	@Override
+//	protected void configure(Configuration configuration) {
+//		super.configure( configuration );
+//		configuration.addAuxiliaryDatabaseObject(
+//				new AuxiliaryDatabaseObject() {
+//					@Override
+//					public void addDialectScope(String dialectName) {
+//					}
+//
+//					@Override
+//					public boolean appliesToDialect(Dialect dialect) {
+//						return H2Dialect.class.isInstance( dialect );
+//					}
+//
+//					@Override
+//					public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
+//						return "CREATE ALIAS findUser AS $$\n" +
+//								"import org.h2.tools.SimpleResultSet;\n" +
+//								"import java.sql.*;\n" +
+//								"@CODE\n" +
+//								"ResultSet findUser() {\n" +
+//								"    SimpleResultSet rs = new SimpleResultSet();\n" +
+//								"    rs.addColumn(\"ID\", Types.INTEGER, 10, 0);\n" +
+//								"    rs.addColumn(\"NAME\", Types.VARCHAR, 255, 0);\n" +
+//								"    rs.addRow(1, \"Steve\");\n" +
+//								"    return rs;\n" +
+//								"}\n" +
+//								"$$";
+//					}
+//
+//					@Override
+//					public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
+//						return "DROP ALIAS findUser IF EXISTS";
+//					}
+//				}
+//		);
+//	}
+
+	@Test
+	public void baseTest() {
+		Session session = openSession();
+		session.beginTransaction();
+
+		StoredProcedureCall query = session.createStoredProcedureCall( "user");
+		StoredProcedureOutputs outputs = query.getOutputs();
+		assertTrue( "Checking StoredProcedureOutputs has more returns", outputs.hasMoreReturns() );
+		StoredProcedureReturn nextReturn = outputs.getNextReturn();
+		assertNotNull( nextReturn );
+		ExtraAssertions.assertClassAssignability( StoredProcedureResultSetReturn.class, nextReturn.getClass() );
+		StoredProcedureResultSetReturn resultSetReturn = (StoredProcedureResultSetReturn) nextReturn;
+		String name = (String) resultSetReturn.getSingleResult();
+		assertEquals( "SA", name );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/typeoverride/StoredPrefixedStringType.java b/hibernate-core/src/test/java/org/hibernate/test/typeoverride/StoredPrefixedStringType.java
index ee32bb28a7..322a777a5b 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/typeoverride/StoredPrefixedStringType.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/typeoverride/StoredPrefixedStringType.java
@@ -1,107 +1,118 @@
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
 package org.hibernate.test.typeoverride;
 
+import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.type.AbstractSingleColumnStandardBasicType;
 import org.hibernate.type.DiscriminatorType;
 import org.hibernate.type.StringType;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.sql.BasicBinder;
 import org.hibernate.type.descriptor.sql.BasicExtractor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;
 
 /**
  *
  * @author Gail Badner
  */
 public class StoredPrefixedStringType
 		extends AbstractSingleColumnStandardBasicType<String>
 		implements DiscriminatorType<String> {
 
 	public static final String PREFIX = "PRE:";
 
 	public static final SqlTypeDescriptor PREFIXED_VARCHAR_TYPE_DESCRIPTOR =
 			new VarcharTypeDescriptor() {
 				public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicBinder<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options) throws SQLException {
 							String stringValue = javaTypeDescriptor.unwrap( value, String.class, options );
 							st.setString( index, PREFIX + stringValue );
 						}
 					};
 				}
 
 				public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
 					return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 						@Override
 						protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 							String stringValue = rs.getString( name );
 							if ( ! stringValue.startsWith( PREFIX ) ) {
 								throw new AssertionFailure( "Value read from resultset does not have prefix." );
 							}
 							return javaTypeDescriptor.wrap( stringValue.substring( PREFIX.length() ), options );
 						}
+
+						@Override
+						protected X doExtract(CallableStatement statement, int index, WrapperOptions options)
+								throws SQLException {
+							String stringValue = statement.getString( index );
+							if ( ! stringValue.startsWith( PREFIX ) ) {
+								throw new AssertionFailure( "Value read from procedure output param does not have prefix." );
+							}
+							return javaTypeDescriptor.wrap( stringValue.substring( PREFIX.length() ), options );
+						}
 					};
 				}
 			};
 
 
 	public static final StoredPrefixedStringType INSTANCE = new StoredPrefixedStringType();
 
 	public StoredPrefixedStringType() {
 		super( PREFIXED_VARCHAR_TYPE_DESCRIPTOR, StringType.INSTANCE.getJavaTypeDescriptor() );
 	}
 
 	public String getName() {
 		return StringType.INSTANCE.getName();
 	}
 
 	@Override
 	protected boolean registerUnderJavaType() {
 		return true;
 	}
 
 	public String objectToSQLString(String value, Dialect dialect) throws Exception {
 		return StringType.INSTANCE.objectToSQLString( value, dialect );
 	}
 
 	public String stringToObject(String xml) throws Exception {
 		return StringType.INSTANCE.stringToObject( xml );
 	}
 
 	public String toString(String value) {
 		return StringType.INSTANCE.toString( value );
 	}
 }
\ No newline at end of file
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
index 0925c26268..368ff7b9f3 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/AbstractEntityManagerImpl.java
@@ -1,1538 +1,1573 @@
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
 package org.hibernate.ejb;
 
 import javax.persistence.CacheRetrieveMode;
 import javax.persistence.CacheStoreMode;
 import javax.persistence.EntityExistsException;
 import javax.persistence.EntityManager;
 import javax.persistence.EntityNotFoundException;
 import javax.persistence.EntityTransaction;
 import javax.persistence.FlushModeType;
 import javax.persistence.LockModeType;
 import javax.persistence.LockTimeoutException;
 import javax.persistence.NoResultException;
 import javax.persistence.NonUniqueResultException;
 import javax.persistence.OptimisticLockException;
 import javax.persistence.PersistenceContextType;
 import javax.persistence.PersistenceException;
 import javax.persistence.PessimisticLockException;
 import javax.persistence.PessimisticLockScope;
 import javax.persistence.Query;
 import javax.persistence.QueryTimeoutException;
+import javax.persistence.StoredProcedureQuery;
 import javax.persistence.TransactionRequiredException;
 import javax.persistence.Tuple;
 import javax.persistence.TupleElement;
 import javax.persistence.TypedQuery;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.criteria.CriteriaDelete;
 import javax.persistence.criteria.CriteriaQuery;
 import javax.persistence.criteria.CriteriaUpdate;
 import javax.persistence.criteria.Selection;
 import javax.persistence.metamodel.Metamodel;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 import javax.transaction.Status;
 import javax.transaction.SystemException;
 import javax.transaction.TransactionManager;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectDeletedException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.SQLQuery;
 import org.hibernate.Session;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
+import org.hibernate.StoredProcedureCall;
 import org.hibernate.TransientObjectException;
 import org.hibernate.TypeMismatchException;
 import org.hibernate.UnresolvableObjectException;
 import org.hibernate.cfg.Environment;
+import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.dialect.lock.LockingStrategyException;
 import org.hibernate.dialect.lock.OptimisticEntityLockException;
 import org.hibernate.dialect.lock.PessimisticEntityLockException;
 import org.hibernate.ejb.criteria.ValueHandlerFactory;
 import org.hibernate.ejb.criteria.compile.CompilableCriteria;
 import org.hibernate.ejb.criteria.compile.CriteriaCompiler;
 import org.hibernate.ejb.criteria.expression.CompoundSelectionImpl;
 import org.hibernate.ejb.internal.EntityManagerMessageLogger;
 import org.hibernate.ejb.util.CacheModeHelper;
 import org.hibernate.ejb.util.ConfigurationHelper;
 import org.hibernate.ejb.util.LockModeTypeHelper;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
 import org.hibernate.engine.transaction.spi.JoinStatus;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
 import org.hibernate.engine.transaction.synchronization.spi.AfterCompletionAction;
 import org.hibernate.engine.transaction.synchronization.spi.ExceptionMapper;
 import org.hibernate.engine.transaction.synchronization.spi.ManagedFlushChecker;
 import org.hibernate.engine.transaction.synchronization.spi.SynchronizationCallbackCoordinator;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.transform.BasicTransformerAdapter;
 import org.hibernate.type.Type;
 
 
 /**
  * @author <a href="mailto:gavin@hibernate.org">Gavin King</a>
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
 public abstract class AbstractEntityManagerImpl implements HibernateEntityManagerImplementor, Serializable {
 	private static final long serialVersionUID = 78818181L;
 
     private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
                                                                            AbstractEntityManagerImpl.class.getName());
 
 	private static final List<String> entityManagerSpecificProperties = new ArrayList<String>();
 
 	static {
 		entityManagerSpecificProperties.add( AvailableSettings.LOCK_SCOPE );
 		entityManagerSpecificProperties.add( AvailableSettings.LOCK_TIMEOUT );
 		entityManagerSpecificProperties.add( AvailableSettings.FLUSH_MODE );
 		entityManagerSpecificProperties.add( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE );
 		entityManagerSpecificProperties.add( AvailableSettings.SHARED_CACHE_STORE_MODE );
 		entityManagerSpecificProperties.add( QueryHints.SPEC_HINT_TIMEOUT );
 	}
 
 	private EntityManagerFactoryImpl entityManagerFactory;
 	protected transient TransactionImpl tx = new TransactionImpl( this );
 	protected PersistenceContextType persistenceContextType;
 	private PersistenceUnitTransactionType transactionType;
 	private Map<String, Object> properties;
 	private LockOptions lockOptions;
 
 	protected AbstractEntityManagerImpl(
 			EntityManagerFactoryImpl entityManagerFactory,
 			PersistenceContextType type,
 			PersistenceUnitTransactionType transactionType,
 			Map properties) {
 		this.entityManagerFactory = entityManagerFactory;
 		this.persistenceContextType = type;
 		this.transactionType = transactionType;
 
 		this.lockOptions = new LockOptions();
 		this.properties = new HashMap<String, Object>();
 		for ( String key : entityManagerSpecificProperties ) {
 			if ( entityManagerFactory.getProperties().containsKey( key ) ) {
 				this.properties.put( key, entityManagerFactory.getProperties().get( key ) );
 			}
 			if ( properties != null && properties.containsKey( key ) ) {
 				this.properties.put( key, properties.get( key ) );
 			}
 		}
 	}
 
 	public PersistenceUnitTransactionType getTransactionType() {
 		return transactionType;
 	}
 
 	protected void postInit() {
 		//register in Sync if needed
 		if ( PersistenceUnitTransactionType.JTA.equals( transactionType ) ) {
 			joinTransaction( false );
 		}
 
 		setDefaultProperties();
 		applyProperties();
 	}
 
 	private void applyProperties() {
 		getSession().setFlushMode( ConfigurationHelper.getFlushMode( properties.get( AvailableSettings.FLUSH_MODE ) ) );
 		setLockOptions( this.properties, this.lockOptions );
 		getSession().setCacheMode(
 				CacheModeHelper.interpretCacheMode(
 						currentCacheStoreMode(),
 						currentCacheRetrieveMode()
 				)
 		);
 	}
 
 	private Query applyProperties(Query query) {
 		if ( lockOptions.getLockMode() != LockMode.NONE ) {
 			query.setLockMode( getLockMode(lockOptions.getLockMode()));
 		}
 		Object queryTimeout;
 		if ( (queryTimeout = getProperties().get(QueryHints.SPEC_HINT_TIMEOUT)) != null ) {
 			query.setHint ( QueryHints.SPEC_HINT_TIMEOUT, queryTimeout );
 		}
 		Object lockTimeout;
 		if( (lockTimeout = getProperties().get( AvailableSettings.LOCK_TIMEOUT ))!=null){
 			query.setHint( AvailableSettings.LOCK_TIMEOUT, lockTimeout );
 		}
 		return query;
 	}
 
 	private CacheRetrieveMode currentCacheRetrieveMode() {
 		return determineCacheRetrieveMode( properties );
 	}
 
 	private CacheRetrieveMode determineCacheRetrieveMode(Map<String, Object> settings) {
 		return ( CacheRetrieveMode ) settings.get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE );
 	}
 
 	private CacheStoreMode currentCacheStoreMode() {
 		return determineCacheStoreMode( properties );
 	}
 
 	private CacheStoreMode determineCacheStoreMode(Map<String, Object> settings) {
 		return ( CacheStoreMode ) properties.get( AvailableSettings.SHARED_CACHE_STORE_MODE );
 	}
 
 	private void setLockOptions(Map<String, Object> props, LockOptions options) {
 		Object lockScope = props.get( AvailableSettings.LOCK_SCOPE );
 		if ( lockScope instanceof String && PessimisticLockScope.valueOf( ( String ) lockScope ) == PessimisticLockScope.EXTENDED ) {
 			options.setScope( true );
 		}
 		else if ( lockScope instanceof PessimisticLockScope ) {
 			boolean extended = PessimisticLockScope.EXTENDED.equals( lockScope );
 			options.setScope( extended );
 		}
 		else if ( lockScope != null ) {
 			throw new PersistenceException( "Unable to parse " + AvailableSettings.LOCK_SCOPE + ": " + lockScope );
 		}
 
 		Object lockTimeout = props.get( AvailableSettings.LOCK_TIMEOUT );
 		int timeout = 0;
 		boolean timeoutSet = false;
 		if ( lockTimeout instanceof String ) {
 			timeout = Integer.parseInt( ( String ) lockTimeout );
 			timeoutSet = true;
 		}
 		else if ( lockTimeout instanceof Number ) {
 			timeout = ( (Number) lockTimeout ).intValue();
 			timeoutSet = true;
 		}
 		else if ( lockTimeout != null ) {
 			throw new PersistenceException( "Unable to parse " + AvailableSettings.LOCK_TIMEOUT + ": " + lockTimeout );
 		}
 		if ( timeoutSet ) {
 			if ( timeout < 0 ) {
 				options.setTimeOut( LockOptions.WAIT_FOREVER );
 			}
 			else if ( timeout == 0 ) {
 				options.setTimeOut( LockOptions.NO_WAIT );
 			}
 			else {
 				options.setTimeOut( timeout );
 			}
 		}
 	}
 
 	/**
 	 * Sets the default property values for the properties the entity manager supports and which are not already explicitly
 	 * set.
 	 */
 	private void setDefaultProperties() {
 		if ( properties.get( AvailableSettings.FLUSH_MODE ) == null ) {
 			properties.put( AvailableSettings.FLUSH_MODE, getSession().getFlushMode().toString() );
 		}
 		if ( properties.get( AvailableSettings.LOCK_SCOPE ) == null ) {
 			this.properties.put( AvailableSettings.LOCK_SCOPE, PessimisticLockScope.EXTENDED.name() );
 		}
 		if ( properties.get( AvailableSettings.LOCK_TIMEOUT ) == null ) {
 			properties.put( AvailableSettings.LOCK_TIMEOUT, LockOptions.WAIT_FOREVER );
 		}
 		if ( properties.get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE ) == null ) {
 			properties.put( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE, CacheModeHelper.DEFAULT_RETRIEVE_MODE );
 		}
 		if ( properties.get( AvailableSettings.SHARED_CACHE_STORE_MODE ) == null ) {
 			properties.put( AvailableSettings.SHARED_CACHE_STORE_MODE, CacheModeHelper.DEFAULT_STORE_MODE );
 		}
 	}
 
 	public Query createQuery(String jpaqlString) {
 		try {
 			return applyProperties( new QueryImpl<Object>( getSession().createQuery( jpaqlString ), this ) );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public <T> TypedQuery<T> createQuery(String jpaqlString, Class<T> resultClass) {
 		try {
 			// do the translation
 			org.hibernate.Query hqlQuery = getSession().createQuery( jpaqlString );
 
 			resultClassChecking( resultClass, hqlQuery );
 
 			// finally, build/return the query instance
 			return new QueryImpl<T>( hqlQuery, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public static class TupleBuilderTransformer extends BasicTransformerAdapter {
 		private List<TupleElement<?>> tupleElements;
 		private Map<String,HqlTupleElementImpl> tupleElementsByAlias;
 
 		public TupleBuilderTransformer(org.hibernate.Query hqlQuery) {
 			final Type[] resultTypes = hqlQuery.getReturnTypes();
 			final int tupleSize = resultTypes.length;
 
 			this.tupleElements = CollectionHelper.arrayList( tupleSize );
 
 			final String[] aliases = hqlQuery.getReturnAliases();
 			final boolean hasAliases = aliases != null && aliases.length > 0;
 			this.tupleElementsByAlias = hasAliases
 					? CollectionHelper.<String, HqlTupleElementImpl>mapOfSize( tupleSize )
 					: Collections.<String, HqlTupleElementImpl>emptyMap();
 
 			for ( int i = 0; i < tupleSize; i++ ) {
 				final HqlTupleElementImpl tupleElement = new HqlTupleElementImpl(
 						i,
 						aliases == null ? null : aliases[i],
 						resultTypes[i]
 				);
 				tupleElements.add( tupleElement );
 				if ( hasAliases ) {
 					final String alias = aliases[i];
 					if ( alias != null ) {
 						tupleElementsByAlias.put( alias, tupleElement );
 					}
 				}
 			}
 		}
 
 		@Override
 		public Object transformTuple(Object[] tuple, String[] aliases) {
 			if ( tuple.length != tupleElements.size() ) {
 				throw new IllegalArgumentException(
 						"Size mismatch between tuple result [" + tuple.length + "] and expected tuple elements [" +
 								tupleElements.size() + "]"
 				);
 			}
 			return new HqlTupleImpl( tuple );
 		}
 
 		public static class HqlTupleElementImpl<X> implements TupleElement<X> {
 			private final int position;
 			private final String alias;
 			private final Type hibernateType;
 
 			public HqlTupleElementImpl(int position, String alias, Type hibernateType) {
 				this.position = position;
 				this.alias = alias;
 				this.hibernateType = hibernateType;
 			}
 
 			@Override
 			public Class getJavaType() {
 				return hibernateType.getReturnedClass();
 			}
 
 			@Override
 			public String getAlias() {
 				return alias;
 			}
 
 			public int getPosition() {
 				return position;
 			}
 
 			public Type getHibernateType() {
 				return hibernateType;
 			}
 		}
 
 		public class HqlTupleImpl implements Tuple {
 			private Object[] tuple;
 
 			public HqlTupleImpl(Object[] tuple) {
 				this.tuple = tuple;
 			}
 
 			@Override
 			public <X> X get(String alias, Class<X> type) {
 				return (X) get( alias );
 			}
 
 			@Override
 			public Object get(String alias) {
 				HqlTupleElementImpl tupleElement = tupleElementsByAlias.get( alias );
 				if ( tupleElement == null ) {
 					throw new IllegalArgumentException( "Unknown alias [" + alias + "]" );
 				}
 				return tuple[ tupleElement.getPosition() ];
 			}
 
 			@Override
 			public <X> X get(int i, Class<X> type) {
 				return (X) get( i );
 			}
 
 			@Override
 			public Object get(int i) {
 				if ( i < 0 ) {
 					throw new IllegalArgumentException( "requested tuple index must be greater than zero" );
 				}
 				if ( i > tuple.length ) {
 					throw new IllegalArgumentException( "requested tuple index exceeds actual tuple size" );
 				}
 				return tuple[i];
 			}
 
 			@Override
 			public Object[] toArray() {
 				// todo : make a copy?
 				return tuple;
 			}
 
 			@Override
 			public List<TupleElement<?>> getElements() {
 				return tupleElements;
 			}
 
 			@Override
 			public <X> X get(TupleElement<X> tupleElement) {
 				if ( HqlTupleElementImpl.class.isInstance( tupleElement ) ) {
 					return get( ( (HqlTupleElementImpl) tupleElement ).getPosition(), tupleElement.getJavaType() );
 				}
 				else {
 					return get( tupleElement.getAlias(), tupleElement.getJavaType() );
 				}
 			}
 		}
 	}
 
 	public <T> QueryImpl<T> createQuery(
 			String jpaqlString,
 			Class<T> resultClass,
 			Selection selection,
 			Options options) {
 		try {
 			org.hibernate.Query hqlQuery = getSession().createQuery( jpaqlString );
 
 			if ( options.getValueHandlers() == null ) {
 				if ( options.getResultMetadataValidator() != null ) {
 					options.getResultMetadataValidator().validate( hqlQuery.getReturnTypes() );
 				}
 			}
 
 			// determine if we need a result transformer
 			List tupleElements = Tuple.class.equals( resultClass )
 					? ( ( CompoundSelectionImpl<Tuple> ) selection ).getCompoundSelectionItems()
 					: null;
 			if ( options.getValueHandlers() != null || tupleElements != null ) {
 				hqlQuery.setResultTransformer(
 						new CriteriaQueryTransformer( options.getValueHandlers(), tupleElements )
 				);
 			}
 			return new QueryImpl<T>( hqlQuery, this, options.getNamedParameterExplicitTypes() );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	private static class CriteriaQueryTransformer extends BasicTransformerAdapter {
 		private final List<ValueHandlerFactory.ValueHandler> valueHandlers;
 		private final List tupleElements;
 
 		private CriteriaQueryTransformer(List<ValueHandlerFactory.ValueHandler> valueHandlers, List tupleElements) {
 			// todo : should these 2 sizes match *always*?
 			this.valueHandlers = valueHandlers;
 			this.tupleElements = tupleElements;
 		}
 
 		@Override
 		public Object transformTuple(Object[] tuple, String[] aliases) {
 			final Object[] valueHandlerResult;
 			if ( valueHandlers == null ) {
 				valueHandlerResult = tuple;
 			}
 			else {
 				valueHandlerResult = new Object[tuple.length];
 				for ( int i = 0; i < tuple.length; i++ ) {
 					ValueHandlerFactory.ValueHandler valueHandler = valueHandlers.get( i );
 					valueHandlerResult[i] = valueHandler == null
 							? tuple[i]
 							: valueHandler.convert( tuple[i] );
 				}
 			}
 
 			return tupleElements == null
 					? valueHandlerResult.length == 1 ? valueHandlerResult[0] : valueHandlerResult
 					: new TupleImpl( tuple );
 
 		}
 
 		private class TupleImpl implements Tuple {
 			private final Object[] tuples;
 
 			private TupleImpl(Object[] tuples) {
 				if ( tuples.length != tupleElements.size() ) {
 					throw new IllegalArgumentException(
 							"Size mismatch between tuple result [" + tuples.length
 									+ "] and expected tuple elements [" + tupleElements.size() + "]"
 					);
 				}
 				this.tuples = tuples;
 			}
 
 			public <X> X get(TupleElement<X> tupleElement) {
 				int index = tupleElements.indexOf( tupleElement );
 				if ( index < 0 ) {
 					throw new IllegalArgumentException(
 							"Requested tuple element did not correspond to element in the result tuple"
 					);
 				}
 				// index should be "in range" by nature of size check in ctor
 				return ( X ) tuples[index];
 			}
 
 			public Object get(String alias) {
 				int index = -1;
 				if ( alias != null ) {
 					alias = alias.trim();
 					if ( alias.length() > 0 ) {
 						int i = 0;
 						for ( TupleElement selection : ( List<TupleElement> ) tupleElements ) {
 							if ( alias.equals( selection.getAlias() ) ) {
 								index = i;
 								break;
 							}
 							i++;
 						}
 					}
 				}
 				if ( index < 0 ) {
 					throw new IllegalArgumentException(
 							"Given alias [" + alias + "] did not correspond to an element in the result tuple"
 					);
 				}
 				// index should be "in range" by nature of size check in ctor
 				return tuples[index];
 			}
 
 			public <X> X get(String alias, Class<X> type) {
 				return ( X ) get( alias );
 			}
 
 			public Object get(int i) {
 				if ( i >= tuples.length ) {
 					throw new IllegalArgumentException(
 							"Given index [" + i + "] was outside the range of result tuple size [" + tuples.length + "] "
 					);
 				}
 				return tuples[i];
 			}
 
 			public <X> X get(int i, Class<X> type) {
 				return ( X ) get( i );
 			}
 
 			public Object[] toArray() {
 				return tuples;
 			}
 
 			public List<TupleElement<?>> getElements() {
 				return tupleElements;
 			}
 		}
 	}
 
 	private CriteriaCompiler criteriaCompiler;
 
 	protected CriteriaCompiler criteriaCompiler() {
 		if ( criteriaCompiler == null ) {
 			criteriaCompiler = new CriteriaCompiler( this );
 		}
 		return criteriaCompiler;
 	}
 
 	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
 		return (TypedQuery<T>) criteriaCompiler().compile( (CompilableCriteria) criteriaQuery );
 	}
 
 	@Override
 	public Query createQuery(CriteriaUpdate criteriaUpdate) {
 		return criteriaCompiler().compile( (CompilableCriteria) criteriaUpdate );
 	}
 
 	@Override
 	public Query createQuery(CriteriaDelete criteriaDelete) {
 		return criteriaCompiler().compile( (CompilableCriteria) criteriaDelete );
 	}
 
 	public Query createNamedQuery(String name) {
 		try {
 			org.hibernate.Query namedQuery = getSession().getNamedQuery( name );
 			try {
 				return new QueryImpl( namedQuery, this );
 			}
 			catch ( HibernateException he ) {
 				throw convert( he );
 			}
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( "Named query not found: " + name );
 		}
 	}
 
 	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
 		try {
 			/*
 			 * Get the named query.
 			 * If the named query is a SQL query, get the expected returned type from the query definition
 			 * or its associated result set mapping
 			 * If the named query is a HQL query, use getReturnType()
 			 */
 			org.hibernate.Query namedQuery = getSession().getNamedQuery( name );
 			//TODO clean this up to avoid downcasting
 			final SessionFactoryImplementor factoryImplementor = entityManagerFactory.getSessionFactory();
 			final NamedSQLQueryDefinition queryDefinition = factoryImplementor.getNamedSQLQuery( name );
 			try {
 				if ( queryDefinition != null ) {
 					Class<?> actualReturnedClass;
 
 					final NativeSQLQueryReturn[] queryReturns;
 					if ( queryDefinition.getQueryReturns() != null ) {
 						queryReturns = queryDefinition.getQueryReturns();
 					}
 					else if ( queryDefinition.getResultSetRef() != null ) {
 						final ResultSetMappingDefinition rsMapping = factoryImplementor.getResultSetMapping(
 								queryDefinition.getResultSetRef()
 						);
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
 						final String entityClassName = ( ( NativeSQLQueryRootReturn ) nativeSQLQueryReturn ).getReturnEntityName();
 						try {
 							actualReturnedClass = ReflectHelper.classForName( entityClassName, AbstractEntityManagerImpl.class );
 						}
 						catch ( ClassNotFoundException e ) {
 							throw new AssertionFailure( "Unable to instantiate class declared on named native query: " + name + " " + entityClassName );
 						}
 						if ( !resultClass.isAssignableFrom( actualReturnedClass ) ) {
 							throw buildIncompatibleException( resultClass, actualReturnedClass );
 						}
 					}
 					else {
 						//TODO support other NativeSQLQueryReturn type. For now let it go.
 					}
 				}
 				else {
 					resultClassChecking( resultClass, namedQuery );
 
 				}
 				return new QueryImpl<T>( namedQuery, this );
 			}
 			catch ( HibernateException he ) {
 				throw convert( he );
 			}
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( "Named query not found: " + name );
 		}
 	}
 
 	private <T> void resultClassChecking(Class<T> resultClass, org.hibernate.Query query) {
 		// make sure the query is a select -> HHH-7192
 		final SessionImplementor session = unwrap( SessionImplementor.class );
 		final HQLQueryPlan queryPlan = session.getFactory().getQueryPlanCache().getHQLQueryPlan(
 				query.getQueryString(),
 				false,
 				session.getLoadQueryInfluencers().getEnabledFilters()
 		);
 		if ( queryPlan.getTranslators()[0].isManipulationStatement() ) {
 			throw new IllegalArgumentException( "Update/delete queries cannot be typed" );
 		}
 
 		// do some return type validation checking
 		if ( Object[].class.equals( resultClass ) ) {
 			// no validation needed
 		}
 		else if ( Tuple.class.equals( resultClass ) ) {
 			TupleBuilderTransformer tupleTransformer = new TupleBuilderTransformer( query );
 			query.setResultTransformer( tupleTransformer );
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
 			else if ( query.getReturnTypes().length == 1 ) {
 				// if we have only a single return expression, its java type should match with the requested type
 				if ( !resultClass.isAssignableFrom( query.getReturnTypes()[0].getReturnedClass() ) ) {
 					throw new IllegalArgumentException(
 							"Type specified for TypedQuery [" +
 									resultClass.getName() +
 									"] is incompatible with query return type [" +
 									query.getReturnTypes()[0].getReturnedClass() + "]"
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
 
 	private IllegalArgumentException buildIncompatibleException(Class<?> resultClass, Class<?> actualResultClass) {
 		return new IllegalArgumentException(
 							"Type specified for TypedQuery [" +
 									resultClass.getName() +
 									"] is incompatible with query return type [" +
 									actualResultClass + "]"
 					);
 	}
 
 	public Query createNativeQuery(String sqlString) {
 		try {
 			SQLQuery q = getSession().createSQLQuery( sqlString );
 			return new QueryImpl( q, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public Query createNativeQuery(String sqlString, Class resultClass) {
 		try {
 			SQLQuery q = getSession().createSQLQuery( sqlString );
 			q.addEntity( "alias1", resultClass.getName(), LockMode.READ );
 			return new QueryImpl( q, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public Query createNativeQuery(String sqlString, String resultSetMapping) {
 		try {
 			SQLQuery q = getSession().createSQLQuery( sqlString );
 			q.setResultSetMapping( resultSetMapping );
 			return new QueryImpl( q, this );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
+	@Override
+	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
+		throw new NotYetImplementedException();
+	}
+
+	@Override
+	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
+		try {
+			StoredProcedureCall call = getSession().createStoredProcedureCall( procedureName );
+			return new StoredProcedureQueryImpl( call, this );
+		}
+		catch ( HibernateException he ) {
+			throw convert( he );
+		}
+	}
+
+	@Override
+	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
+		try {
+			StoredProcedureCall call = getSession().createStoredProcedureCall( procedureName, resultClasses );
+			return new StoredProcedureQueryImpl( call, this );
+		}
+		catch ( HibernateException he ) {
+			throw convert( he );
+		}
+	}
+
+	@Override
+	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
+		throw new NotYetImplementedException();
+	}
+
 	@SuppressWarnings("unchecked")
 	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
 		try {
 			return ( T ) getSession().load( entityClass, ( Serializable ) primaryKey );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( TypeMismatchException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( ClassCastException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	public <A> A find(Class<A> entityClass, Object primaryKey) {
 		return find( entityClass, primaryKey, null, null );
 	}
 
 	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
 		return find( entityClass, primaryKey, null, properties );
 	}
 
 	@SuppressWarnings("unchecked")
 	public <A> A find(Class<A> entityClass, Object primaryKey, LockModeType lockModeType) {
 		return find( entityClass, primaryKey, lockModeType, null );
 	}
 
 	public <A> A find(Class<A> entityClass, Object primaryKey, LockModeType lockModeType, Map<String, Object> properties) {
 		CacheMode previousCacheMode = getSession().getCacheMode();
 		CacheMode cacheMode = determineAppropriateLocalCacheMode( properties );
 		LockOptions lockOptions = null;
 		try {
 			getSession().setCacheMode( cacheMode );
 			if ( lockModeType != null ) {
 				lockOptions = getLockRequest( lockModeType, properties );
 				return ( A ) getSession().get(
 						entityClass, ( Serializable ) primaryKey, 
 						lockOptions
 				);
 			}
 			else {
 				return ( A ) getSession().get( entityClass, ( Serializable ) primaryKey );
 			}
 		}
 		catch ( ObjectDeletedException e ) {
 			//the spec is silent about people doing remove() find() on the same PC
 			return null;
 		}
 		catch ( ObjectNotFoundException e ) {
 			//should not happen on the entity itself with get
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( TypeMismatchException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( ClassCastException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he, lockOptions );
 		}
 		finally {
 			getSession().setCacheMode( previousCacheMode );
 		}
 	}
 
 	public CacheMode determineAppropriateLocalCacheMode(Map<String, Object> localProperties) {
 		CacheRetrieveMode retrieveMode = null;
 		CacheStoreMode storeMode = null;
 		if ( localProperties != null ) {
 			retrieveMode = determineCacheRetrieveMode( localProperties );
 			storeMode = determineCacheStoreMode( localProperties );
 		}
 		if ( retrieveMode == null ) {
 			// use the EM setting
 			retrieveMode = determineCacheRetrieveMode( this.properties );
 		}
 		if ( storeMode == null ) {
 			// use the EM setting
 			storeMode = determineCacheStoreMode( this.properties );
 		}
 		return CacheModeHelper.interpretCacheMode( storeMode, retrieveMode );
 	}
 
 	private void checkTransactionNeeded() {
 		if ( persistenceContextType == PersistenceContextType.TRANSACTION && !isTransactionInProgress() ) {
 			//no need to mark as rollback, no tx in progress
 			throw new TransactionRequiredException(
 					"no transaction is in progress for a TRANSACTION type persistence context"
 			);
 		}
 	}
 
 	public void persist(Object entity) {
 		checkTransactionNeeded();
 		try {
 			getSession().persist( entity );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage() );
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	public <A> A merge(A entity) {
 		checkTransactionNeeded();
 		try {
 			return ( A ) getSession().merge( entity );
 		}
 		catch ( ObjectDeletedException sse ) {
 			throw new IllegalArgumentException( sse );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( RuntimeException e ) { //including HibernateException
 			throw convert( e );
 		}
 	}
 
 	public void remove(Object entity) {
 		checkTransactionNeeded();
 		try {
 			getSession().delete( entity );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( RuntimeException e ) { //including HibernateException
 			throw convert( e );
 		}
 	}
 
 	public void refresh(Object entity) {
 		refresh( entity, null, null );
 	}
 
 	public void refresh(Object entity, Map<String, Object> properties) {
 		refresh( entity, null, properties );
 	}
 
 	public void refresh(Object entity, LockModeType lockModeType) {
 		refresh( entity, lockModeType, null );
 	}
 
 	public void refresh(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
 		checkTransactionNeeded();
 		CacheMode previousCacheMode = getSession().getCacheMode();
 		CacheMode localCacheMode = determineAppropriateLocalCacheMode( properties );
 		LockOptions lockOptions = null;
 		try {
 			getSession().setCacheMode( localCacheMode );
 			if ( !getSession().contains( entity ) ) {
 				throw new IllegalArgumentException( "Entity not managed" );
 			}
 			if ( lockModeType != null ) {
 				lockOptions = getLockRequest( lockModeType, properties );
 				getSession().refresh( entity, lockOptions );
 			}
 			else {
 				getSession().refresh( entity );
 			}
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he, lockOptions );
 		}
 		finally {
 			getSession().setCacheMode( previousCacheMode );
 		}
 	}
 
 	public boolean contains(Object entity) {
 		try {
 			if ( entity != null
 					&& !( entity instanceof HibernateProxy )
 					&& getSession().getSessionFactory().getClassMetadata( entity.getClass() ) == null ) {
 				throw new IllegalArgumentException( "Not an entity:" + entity.getClass() );
 			}
 			return getSession().contains( entity );
 		}
 		catch ( MappingException e ) {
 			throw new IllegalArgumentException( e.getMessage(), e );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public LockModeType getLockMode(Object entity) {
 		if ( !contains( entity ) ) {
 			throw new IllegalArgumentException( "entity not in the persistence context" );
 		}
 		return getLockModeType( getSession().getCurrentLockMode( entity ) );
 	}
 
 	public void setProperty(String s, Object o) {
 		if ( entityManagerSpecificProperties.contains( s ) ) {
 			properties.put( s, o );
 			applyProperties();
         } else LOG.debugf("Trying to set a property which is not supported on entity manager level");
 	}
 
 	public Map<String, Object> getProperties() {
 		return Collections.unmodifiableMap( properties );
 	}
 
 	public void flush() {
 		if ( !isTransactionInProgress() ) {
 			throw new TransactionRequiredException( "no transaction is in progress" );
 		}
 		try {
 			getSession().flush();
 		}
 		catch ( RuntimeException e ) {
 			throw convert( e );
 		}
 	}
 
 	/**
 	 * return a Session
 	 *
 	 * @throws IllegalStateException if the entity manager is closed
 	 */
 	public abstract Session getSession();
 
 	/**
 	 * Return a Session (even if the entity manager is closed).
 	 *
 	 * @return A session.
 	 */
 	protected abstract Session getRawSession();
 
 	public EntityTransaction getTransaction() {
 		if ( transactionType == PersistenceUnitTransactionType.JTA ) {
 			throw new IllegalStateException( "A JTA EntityManager cannot use getTransaction()" );
 		}
 		return tx;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public EntityManagerFactoryImpl getEntityManagerFactory() {
 		return entityManagerFactory;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public HibernateEntityManagerFactory getFactory() {
 		return entityManagerFactory;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public CriteriaBuilder getCriteriaBuilder() {
 		return getEntityManagerFactory().getCriteriaBuilder();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Metamodel getMetamodel() {
 		return getEntityManagerFactory().getMetamodel();
 	}
 
 	public void setFlushMode(FlushModeType flushModeType) {
 		if ( flushModeType == FlushModeType.AUTO ) {
 			getSession().setFlushMode( FlushMode.AUTO );
 		}
 		else if ( flushModeType == FlushModeType.COMMIT ) {
 			getSession().setFlushMode( FlushMode.COMMIT );
 		}
 		else {
 			throw new AssertionFailure( "Unknown FlushModeType: " + flushModeType );
 		}
 	}
 
 	public void clear() {
 		try {
 			getSession().clear();
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	public void detach(Object entity) {
 		try {
 			getSession().evict( entity );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	/**
 	 * Hibernate can be set in various flush modes that are unknown to
 	 * JPA 2.0. This method can then return null.
 	 * If it returns null, do em.unwrap(Session.class).getFlushMode() to get the
 	 * Hibernate flush mode
 	 */
 	public FlushModeType getFlushMode() {
 		FlushMode mode = getSession().getFlushMode();
 		if ( mode == FlushMode.AUTO ) {
 			return FlushModeType.AUTO;
 		}
 		else if ( mode == FlushMode.COMMIT ) {
 			return FlushModeType.COMMIT;
 		}
 		else {
 			// otherwise this is an unknown mode for EJB3
 			return null;
 		}
 	}
 
 	public void lock(Object entity, LockModeType lockMode) {
 		lock( entity, lockMode, null );
 	}
 
 	public void lock(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
 		LockOptions lockOptions = null;
 		if ( !isTransactionInProgress() ) {
 			throw new TransactionRequiredException( "no transaction is in progress" );
 		}
 
 		try {
 			if ( !contains( entity ) ) {
 				throw new IllegalArgumentException( "entity not in the persistence context" );
 			}
 			lockOptions = getLockRequest( lockModeType, properties );
 			getSession().buildLockRequest( lockOptions ).lock( entity );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he, lockOptions );
 		}
 	}
 
 	public LockOptions getLockRequest(LockModeType lockModeType, Map<String, Object> properties) {
 		LockOptions lockOptions = new LockOptions();
 		LockOptions.copy( this.lockOptions, lockOptions );
 		lockOptions.setLockMode( getLockMode( lockModeType ) );
 		if ( properties != null ) {
 			setLockOptions( properties, lockOptions );
 		}
 		return lockOptions;
 	}
 
 	@SuppressWarnings("deprecation")
 	private static LockModeType getLockModeType(LockMode lockMode) {
 		//TODO check that if we have UPGRADE_NOWAIT we have a timeout of zero?
 		return LockModeTypeHelper.getLockModeType( lockMode );
 	}
 
 
 	private static LockMode getLockMode(LockModeType lockMode) {
 		return LockModeTypeHelper.getLockMode( lockMode );
 	}
 
 	public boolean isTransactionInProgress() {
 		return ( ( SessionImplementor ) getRawSession() ).isTransactionInProgress();
 	}
 
 	private SessionFactoryImplementor sfi() {
 		return (SessionFactoryImplementor) getRawSession().getSessionFactory();
 	}
 
 	@Override
 	public <T> T unwrap(Class<T> clazz) {
 		if ( Session.class.isAssignableFrom( clazz ) ) {
 			return ( T ) getSession();
 		}
 		if ( SessionImplementor.class.isAssignableFrom( clazz ) ) {
 			return ( T ) getSession();
 		}
 		if ( EntityManager.class.isAssignableFrom( clazz ) ) {
 			return ( T ) this;
 		}
 		throw new PersistenceException( "Hibernate cannot unwrap " + clazz );
 	}
 
 	protected void markAsRollback() {
         LOG.debugf("Mark transaction for rollback");
 		if ( tx.isActive() ) {
 			tx.setRollbackOnly();
 		}
 		else {
 			//no explicit use of the tx. boundaries methods
 			if ( PersistenceUnitTransactionType.JTA == transactionType ) {
 				TransactionManager transactionManager = sfi().getServiceRegistry().getService( JtaPlatform.class ).retrieveTransactionManager();
 				if ( transactionManager == null ) {
 					throw new PersistenceException(
 							"Using a JTA persistence context wo setting hibernate.transaction.manager_lookup_class"
 					);
 				}
 				try {
                     if ( transactionManager.getStatus() != Status.STATUS_NO_TRANSACTION ) {
                         transactionManager.setRollbackOnly();
                     }
 				}
 				catch ( SystemException e ) {
 					throw new PersistenceException( "Unable to set the JTA transaction as RollbackOnly", e );
 				}
 			}
 		}
 	}
 
 	@Override
 	public boolean isJoinedToTransaction() {
 		final SessionImplementor session = (SessionImplementor) getSession();
 		final TransactionCoordinator transactionCoordinator = session.getTransactionCoordinator();
 		final TransactionImplementor transaction = transactionCoordinator.getTransaction();
 
 		return isOpen() && transaction.getJoinStatus() == JoinStatus.JOINED;
 	}
 
 	@Override
 	public void joinTransaction() {
 		if( !isOpen() ){
 			throw new IllegalStateException( "EntityManager is closed" );
 		}
 		joinTransaction( true );
 	}
 
 	private void joinTransaction(boolean explicitRequest) {
 		if ( transactionType != PersistenceUnitTransactionType.JTA ) {
 			if ( explicitRequest ) {
 			    LOG.callingJoinTransactionOnNonJtaEntityManager();
 			}
 			return;
 		}
 
 		final SessionImplementor session = (SessionImplementor) getSession();
 		final TransactionCoordinator transactionCoordinator = session.getTransactionCoordinator();
 		final TransactionImplementor transaction = transactionCoordinator.getTransaction();
 
 		transaction.markForJoin();
 		transactionCoordinator.pulse();
 
 		LOG.debug( "Looking for a JTA transaction to join" );
 		if ( ! transactionCoordinator.isTransactionJoinable() ) {
 			if ( explicitRequest ) {
 				// if this is an explicit join request, log a warning so user can track underlying cause
 				// of subsequent exceptions/messages
 				LOG.unableToJoinTransaction(Environment.TRANSACTION_STRATEGY);
 			}
 		}
 
 		try {
 			if ( transaction.getJoinStatus() == JoinStatus.JOINED ) {
 				LOG.debug( "Transaction already joined" );
 				return; // noop
 			}
 
 			// join the transaction and then recheck the status
 			transaction.join();
 			if ( transaction.getJoinStatus() == JoinStatus.NOT_JOINED ) {
 				if ( explicitRequest ) {
 					throw new TransactionRequiredException( "No active JTA transaction on joinTransaction call" );
 				}
 				else {
 					LOG.debug( "Unable to join JTA transaction" );
 					return;
 				}
 			}
 			else if ( transaction.getJoinStatus() == JoinStatus.MARKED_FOR_JOINED ) {
 				throw new AssertionFailure( "Transaction MARKED_FOR_JOINED after isOpen() call" );
 			}
 
 			// register behavior changes
 			SynchronizationCallbackCoordinator callbackCoordinator = transactionCoordinator.getSynchronizationCallbackCoordinator();
 			callbackCoordinator.setManagedFlushChecker( new ManagedFlushCheckerImpl() );
 			callbackCoordinator.setExceptionMapper( new CallbackExceptionMapperImpl() );
 			callbackCoordinator.setAfterCompletionAction( new AfterCompletionActionImpl( session, transactionType ) );
 		}
 		catch ( HibernateException he ) {
 			throw convert( he );
 		}
 	}
 
 	/**
 	 * returns the underlying session
 	 */
 	public Object getDelegate() {
 		return getSession();
 	}
 
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		oos.defaultWriteObject();
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		ois.defaultReadObject();
 		tx = new TransactionImpl( this );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void handlePersistenceException(PersistenceException e) {
 		if ( e instanceof NoResultException ) {
 			return;
 		}
 		if ( e instanceof NonUniqueResultException ) {
 			return;
 		}
 		if ( e instanceof LockTimeoutException ) {
 			return;
 		}
 		if ( e instanceof QueryTimeoutException ) {
 			return;
 		}
 
 		try {
 			markAsRollback();
 		}
 		catch ( Exception ne ) {
 			//we do not want the subsequent exception to swallow the original one
             LOG.unableToMarkForRollbackOnPersistenceException(ne);
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void throwPersistenceException(PersistenceException e) {
 		handlePersistenceException( e );
 		throw e;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	//FIXME should we remove all calls to this method and use convert(RuntimeException) ?
 	public RuntimeException convert(HibernateException e) {
 		return convert( e, null );
 	}
 
 	public RuntimeException convert(RuntimeException e) {
 		RuntimeException result = e;
 		if ( e instanceof HibernateException ) {
 			result = convert( ( HibernateException ) e );
 		}
 		else {
 			markAsRollback();
 		}
 		return result;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public RuntimeException convert(HibernateException e, LockOptions lockOptions) {
 		if ( e instanceof StaleStateException ) {
 			PersistenceException converted = wrapStaleStateException( ( StaleStateException ) e );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof LockingStrategyException ) {
 			PersistenceException converted = wrapLockException( e, lockOptions );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof org.hibernate.exception.LockTimeoutException ) {
 			PersistenceException converted = wrapLockException( e, lockOptions );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof org.hibernate.PessimisticLockException ) {
 			PersistenceException converted = wrapLockException( e, lockOptions );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof org.hibernate.QueryTimeoutException ) {
 			QueryTimeoutException converted = new QueryTimeoutException(e.getMessage(), e);
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof ObjectNotFoundException ) {
 			EntityNotFoundException converted = new EntityNotFoundException( e.getMessage() );
 			handlePersistenceException( converted );
 			return converted;
 		}
         else if ( e instanceof org.hibernate.NonUniqueObjectException ) {
             EntityExistsException converted = new EntityExistsException( e.getMessage() );
             handlePersistenceException( converted );
             return converted;
         }
 		else if ( e instanceof org.hibernate.NonUniqueResultException ) {
 			NonUniqueResultException converted = new NonUniqueResultException( e.getMessage() );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof UnresolvableObjectException ) {
 			EntityNotFoundException converted = new EntityNotFoundException( e.getMessage() );
 			handlePersistenceException( converted );
 			return converted;
 		}
 		else if ( e instanceof QueryException ) {
 			return new IllegalArgumentException( e );
 		}
 		else if ( e instanceof TransientObjectException ) {
 			try {
 				markAsRollback();
 			}
 			catch ( Exception ne ) {
 				//we do not want the subsequent exception to swallow the original one
                 LOG.unableToMarkForRollbackOnTransientObjectException(ne);
 			}
 			return new IllegalStateException( e ); //Spec 3.2.3 Synchronization rules
 		}
 		else {
 			PersistenceException converted = new PersistenceException( e );
 			handlePersistenceException( converted );
 			return converted;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void throwPersistenceException(HibernateException e) {
 		throw convert( e );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public PersistenceException wrapStaleStateException(StaleStateException e) {
 		PersistenceException pe;
 		if ( e instanceof StaleObjectStateException ) {
 			StaleObjectStateException sose = ( StaleObjectStateException ) e;
 			Serializable identifier = sose.getIdentifier();
 			if ( identifier != null ) {
 				try {
 					Object entity = getRawSession().load( sose.getEntityName(), identifier );
 					if ( entity instanceof Serializable ) {
 						//avoid some user errors regarding boundary crossing
 						pe = new OptimisticLockException( null, e, entity );
 					}
 					else {
 						pe = new OptimisticLockException( e );
 					}
 				}
 				catch ( EntityNotFoundException enfe ) {
 					pe = new OptimisticLockException( e );
 				}
 			}
 			else {
 				pe = new OptimisticLockException( e );
 			}
 		}
 		else {
 			pe = new OptimisticLockException( e );
 		}
 		return pe;
 	}
 
 	public PersistenceException wrapLockException(HibernateException e, LockOptions lockOptions) {
 		final PersistenceException pe;
 		if ( e instanceof OptimisticEntityLockException ) {
 			final OptimisticEntityLockException lockException = (OptimisticEntityLockException) e;
 			pe = new OptimisticLockException( lockException.getMessage(), lockException, lockException.getEntity() );
 		}
 		else if ( e instanceof org.hibernate.exception.LockTimeoutException ) {
 			pe = new LockTimeoutException( e.getMessage(), e, null );
 		}
 		else if ( e instanceof PessimisticEntityLockException ) {
 			PessimisticEntityLockException lockException = (PessimisticEntityLockException) e;
 			if ( lockOptions != null && lockOptions.getTimeOut() > -1 ) {
 				// assume lock timeout occurred if a timeout or NO WAIT was specified
 				pe = new LockTimeoutException( lockException.getMessage(), lockException, lockException.getEntity() );
 			}
 			else {
 				pe = new PessimisticLockException( lockException.getMessage(), lockException, lockException.getEntity() );
 			}
 		}
 		else if ( e instanceof org.hibernate.PessimisticLockException ) {
 			org.hibernate.PessimisticLockException jdbcLockException = ( org.hibernate.PessimisticLockException ) e;
 			if ( lockOptions != null && lockOptions.getTimeOut() > -1 ) {
 				// assume lock timeout occurred if a timeout or NO WAIT was specified
 				pe = new LockTimeoutException( jdbcLockException.getMessage(), jdbcLockException, null );
 			}
 			else {
 				pe = new PessimisticLockException( jdbcLockException.getMessage(), jdbcLockException, null );
 			}
 		}
 		else {
 			pe = new OptimisticLockException( e );
 		}
 		return pe;
 	}
 
 	private static class AfterCompletionActionImpl implements AfterCompletionAction {
 		private final SessionImplementor session;
 		private final PersistenceUnitTransactionType transactionType;
 
 		private AfterCompletionActionImpl(SessionImplementor session, PersistenceUnitTransactionType transactionType) {
 			this.session = session;
 			this.transactionType = transactionType;
 		}
 
 		@Override
 		public void doAction(TransactionCoordinator transactionCoordinator, int status) {
 			if ( session.isClosed() ) {
                 LOG.trace("Session was closed; nothing to do");
 				return;
 			}
 
 			final boolean successful = JtaStatusHelper.isCommitted( status );
 			if ( !successful && transactionType == PersistenceUnitTransactionType.JTA ) {
 				( (Session) session ).clear();
 			}
 			session.getTransactionCoordinator().resetJoinStatus();
 		}
 	}
 
 	private static class ManagedFlushCheckerImpl implements ManagedFlushChecker {
 		@Override
 		public boolean shouldDoManagedFlush(TransactionCoordinator coordinator, int jtaStatus) {
 			return ! coordinator.getTransactionContext().isClosed() &&
 					! coordinator.getTransactionContext().isFlushModeNever() &&
 					! JtaStatusHelper.isRollback( jtaStatus );
 		}
 	}
 
 	private class CallbackExceptionMapperImpl implements ExceptionMapper {
 		@Override
 		public RuntimeException mapStatusCheckFailure(String message, SystemException systemException) {
 			throw new PersistenceException( message, systemException );
 		}
 
 		@Override
 		public RuntimeException mapManagedFlushFailure(String message, RuntimeException failure) {
 			if ( HibernateException.class.isInstance( failure ) ) {
 				throw convert( failure );
 			}
 			if ( PersistenceException.class.isInstance( failure ) ) {
 				throw failure;
 			}
 			throw new PersistenceException( message, failure );
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/BaseQueryImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/BaseQueryImpl.java
new file mode 100644
index 0000000000..d0aed68efd
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/BaseQueryImpl.java
@@ -0,0 +1,602 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.ejb;
+
+import javax.persistence.CacheRetrieveMode;
+import javax.persistence.CacheStoreMode;
+import javax.persistence.FlushModeType;
+import javax.persistence.Parameter;
+import javax.persistence.Query;
+import javax.persistence.TemporalType;
+import javax.persistence.TypedQuery;
+
+import java.util.Calendar;
+import java.util.Date;
+import java.util.HashMap;
+import java.util.List;
+import java.util.Map;
+import java.util.Set;
+
+import org.jboss.logging.Logger;
+
+import org.hibernate.CacheMode;
+import org.hibernate.FlushMode;
+import org.hibernate.LockMode;
+import org.hibernate.ejb.internal.EntityManagerMessageLogger;
+import org.hibernate.ejb.util.CacheModeHelper;
+import org.hibernate.ejb.util.ConfigurationHelper;
+import org.hibernate.ejb.util.LockModeTypeHelper;
+
+import static org.hibernate.ejb.QueryHints.HINT_CACHEABLE;
+import static org.hibernate.ejb.QueryHints.HINT_CACHE_MODE;
+import static org.hibernate.ejb.QueryHints.HINT_CACHE_REGION;
+import static org.hibernate.ejb.QueryHints.HINT_COMMENT;
+import static org.hibernate.ejb.QueryHints.HINT_FETCH_SIZE;
+import static org.hibernate.ejb.QueryHints.HINT_FLUSH_MODE;
+import static org.hibernate.ejb.QueryHints.HINT_READONLY;
+import static org.hibernate.ejb.QueryHints.HINT_TIMEOUT;
+import static org.hibernate.ejb.QueryHints.SPEC_HINT_TIMEOUT;
+
+/**
+ * Intended as the base class for all {@link Query} implementations, including {@link TypedQuery} and
+ * {@link javax.persistence.StoredProcedureQuery}.  Care should be taken that all changes here fit with all
+ * those usages.
+ *
+ * @author Steve Ebersole
+ */
+public abstract class BaseQueryImpl implements Query {
+	private static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(
+			EntityManagerMessageLogger.class,
+			AbstractQueryImpl.class.getName()
+	);
+
+	private final HibernateEntityManagerImplementor entityManager;
+
+	private int firstResult;
+	private int maxResults = -1;
+	private Map<String, Object> hints;
+
+
+	public BaseQueryImpl(HibernateEntityManagerImplementor entityManager) {
+		this.entityManager = entityManager;
+	}
+
+	protected HibernateEntityManagerImplementor entityManager() {
+		return entityManager;
+	}
+
+
+	// Limits (first and max results) ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	/**
+	 * Apply the given first-result value.
+	 *
+	 * @param firstResult The specified first-result value.
+	 */
+	protected abstract void applyFirstResult(int firstResult);
+
+	@Override
+	public BaseQueryImpl setFirstResult(int firstResult) {
+		if ( firstResult < 0 ) {
+			throw new IllegalArgumentException(
+					"Negative value (" + firstResult + ") passed to setFirstResult"
+			);
+		}
+		this.firstResult = firstResult;
+		applyFirstResult( firstResult );
+		return this;
+	}
+
+	@Override
+	public int getFirstResult() {
+		return firstResult;
+	}
+
+	/**
+	 * Apply the given max results value.
+	 *
+	 * @param maxResults The specified max results
+	 */
+	protected abstract void applyMaxResults(int maxResults);
+
+	@Override
+	public BaseQueryImpl setMaxResults(int maxResult) {
+		if ( maxResult < 0 ) {
+			throw new IllegalArgumentException(
+					"Negative value (" + maxResult + ") passed to setMaxResults"
+			);
+		}
+		this.maxResults = maxResult;
+		applyMaxResults( maxResult );
+		return this;
+	}
+
+	public int getSpecifiedMaxResults() {
+		return maxResults;
+	}
+
+	@Override
+	public int getMaxResults() {
+		return maxResults == -1
+				? Integer.MAX_VALUE // stupid spec... MAX_VALUE??
+				: maxResults;
+	}
+
+
+	// Hints ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@SuppressWarnings( {"UnusedDeclaration"})
+	public Set<String> getSupportedHints() {
+		return QueryHints.getDefinedHints();
+	}
+
+	@Override
+	public Map<String, Object> getHints() {
+		return hints;
+	}
+
+	/**
+	 * Apply the query timeout hint.
+	 *
+	 * @param timeout The timeout (in seconds!) specified as a hint
+	 *
+	 * @return {@code true} if the hint was "applied"
+	 */
+	protected abstract boolean applyTimeoutHint(int timeout);
+
+	/**
+	 * Apply the lock timeout (in seconds!) hint
+	 *
+	 * @param timeout The timeout (in seconds!) specified as a hint
+	 *
+	 * @return {@code true} if the hint was "applied"
+	 */
+	protected abstract boolean applyLockTimeoutHint(int timeout);
+
+	/**
+	 * Apply the comment hint.
+	 *
+	 * @param comment The comment specified as a hint
+	 *
+	 * @return {@code true} if the hint was "applied"
+	 */
+	protected abstract boolean applyCommentHint(String comment);
+
+	/**
+	 * Apply the fetch size hint
+	 *
+	 * @param fetchSize The fetch size specified as a hint
+	 *
+	 * @return {@code true} if the hint was "applied"
+	 */
+	protected abstract boolean applyFetchSize(int fetchSize);
+
+	/**
+	 * Apply the cacheable (true/false) hint.
+	 *
+	 * @param isCacheable The value specified as hint
+	 *
+	 * @return {@code true} if the hint was "applied"
+	 */
+	protected abstract boolean applyCacheableHint(boolean isCacheable);
+
+	/**
+	 * Apply the cache region hint
+	 *
+	 * @param regionName The name of the cache region specified as a hint
+	 *
+	 * @return {@code true} if the hint was "applied"
+	 */
+	protected abstract boolean applyCacheRegionHint(String regionName);
+
+	/**
+	 * Apply the read-only (true/false) hint.
+	 *
+	 * @param isReadOnly The value specified as hint
+	 *
+	 * @return {@code true} if the hint was "applied"
+	 */
+	protected abstract boolean applyReadOnlyHint(boolean isReadOnly);
+
+	/**
+	 * Apply the CacheMode hint.
+	 *
+	 * @param cacheMode The CacheMode value specified as a hint.
+	 *
+	 * @return {@code true} if the hint was "applied"
+	 */
+	protected abstract boolean applyCacheModeHint(CacheMode cacheMode);
+
+	/**
+	 * Apply the FlushMode hint.
+	 *
+	 * @param flushMode The FlushMode value specified as hint
+	 *
+	 * @return {@code true} if the hint was "applied"
+	 */
+	protected abstract boolean applyFlushModeHint(FlushMode flushMode);
+
+	/**
+	 * Can alias-specific lock modes be applied?
+	 *
+	 * @return {@code true} indicates they can be applied, {@code false} otherwise.
+	 */
+	protected abstract boolean canApplyLockModesHints();
+
+	/**
+	 * Apply the alias specific lock modes.  Assumes {@link #canApplyLockModesHints()} has already been called and
+	 * returned {@code true}.
+	 *
+	 * @param alias The alias to apply the 'lockMode' to.
+	 * @param lockMode The LockMode to apply.
+	 */
+	protected abstract void applyAliasSpecificLockModeHint(String alias, LockMode lockMode);
+
+	@Override
+	@SuppressWarnings( {"deprecation"})
+	public BaseQueryImpl setHint(String hintName, Object value) {
+		boolean applied = false;
+		try {
+			if ( HINT_TIMEOUT.equals( hintName ) ) {
+				applied = applyTimeoutHint( ConfigurationHelper.getInteger( value ) );
+			}
+			else if ( SPEC_HINT_TIMEOUT.equals( hintName ) ) {
+				// convert milliseconds to seconds
+				int timeout = (int)Math.round(ConfigurationHelper.getInteger( value ).doubleValue() / 1000.0 );
+				applied = applyTimeoutHint( timeout );
+			}
+			else if ( AvailableSettings.LOCK_TIMEOUT.equals( hintName ) ) {
+				applied = applyLockTimeoutHint( ConfigurationHelper.getInteger( value ) );
+			}
+			else if ( HINT_COMMENT.equals( hintName ) ) {
+				applied = applyCommentHint( (String) value );
+			}
+			else if ( HINT_FETCH_SIZE.equals( hintName ) ) {
+				applied = applyFetchSize( ConfigurationHelper.getInteger( value ) );
+			}
+			else if ( HINT_CACHEABLE.equals( hintName ) ) {
+				applied = applyCacheableHint( ConfigurationHelper.getBoolean( value ) );
+			}
+			else if ( HINT_CACHE_REGION.equals( hintName ) ) {
+				applied = applyCacheRegionHint( (String) value );
+			}
+			else if ( HINT_READONLY.equals( hintName ) ) {
+				applied = applyReadOnlyHint( ConfigurationHelper.getBoolean( value ) );
+			}
+			else if ( HINT_CACHE_MODE.equals( hintName ) ) {
+				applied = applyCacheModeHint( ConfigurationHelper.getCacheMode( value ) );
+			}
+			else if ( HINT_FLUSH_MODE.equals( hintName ) ) {
+				applied = applyFlushModeHint( ConfigurationHelper.getFlushMode( value ) );
+			}
+			else if ( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE.equals( hintName ) ) {
+				final CacheRetrieveMode retrieveMode = (CacheRetrieveMode) value;
+
+				CacheStoreMode storeMode = hints != null
+						? (CacheStoreMode) hints.get( AvailableSettings.SHARED_CACHE_STORE_MODE )
+						: null;
+				if ( storeMode == null ) {
+					storeMode = (CacheStoreMode) entityManager.getProperties().get( AvailableSettings.SHARED_CACHE_STORE_MODE );
+				}
+				applied = applyCacheModeHint( CacheModeHelper.interpretCacheMode( storeMode, retrieveMode ) );
+			}
+			else if ( AvailableSettings.SHARED_CACHE_STORE_MODE.equals( hintName ) ) {
+				final CacheStoreMode storeMode = (CacheStoreMode) value;
+
+				CacheRetrieveMode retrieveMode = hints != null
+						? (CacheRetrieveMode) hints.get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE )
+						: null;
+				if ( retrieveMode == null ) {
+					retrieveMode = (CacheRetrieveMode) entityManager.getProperties().get( AvailableSettings.SHARED_CACHE_RETRIEVE_MODE );
+				}
+				applied = applyCacheModeHint(
+						CacheModeHelper.interpretCacheMode( storeMode, retrieveMode )
+				);
+			}
+			else if ( hintName.startsWith( AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE ) ) {
+				if ( canApplyLockModesHints() ) {
+					// extract the alias
+					final String alias = hintName.substring( AvailableSettings.ALIAS_SPECIFIC_LOCK_MODE.length() + 1 );
+					// determine the LockMode
+					try {
+						final LockMode lockMode = LockModeTypeHelper.interpretLockMode( value );
+						applyAliasSpecificLockModeHint( alias, lockMode );
+					}
+					catch ( Exception e ) {
+						LOG.unableToDetermineLockModeValue( hintName, value );
+						applied = false;
+					}
+				}
+				else {
+					applied = false;
+				}
+			}
+			else {
+				LOG.ignoringUnrecognizedQueryHint( hintName );
+			}
+		}
+		catch ( ClassCastException e ) {
+			throw new IllegalArgumentException( "Value for hint" );
+		}
+
+		if ( applied ) {
+			if ( hints == null ) {
+				hints = new HashMap<String,Object>();
+			}
+			hints.put( hintName, value );
+		}
+		else {
+			LOG.debugf( "Skipping unsupported query hint [%s]", hintName );
+		}
+
+		return this;
+	}
+
+
+	// FlushMode ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	private FlushModeType jpaFlushMode;
+
+	@Override
+	public BaseQueryImpl setFlushMode(FlushModeType jpaFlushMode) {
+		this.jpaFlushMode = jpaFlushMode;
+		// TODO : treat as hint?
+		if ( jpaFlushMode == FlushModeType.AUTO ) {
+			applyFlushModeHint( FlushMode.AUTO );
+		}
+		else if ( jpaFlushMode == FlushModeType.COMMIT ) {
+			applyFlushModeHint( FlushMode.COMMIT );
+		}
+		return this;
+	}
+
+	@SuppressWarnings( {"UnusedDeclaration"})
+	protected FlushModeType getSpecifiedFlushMode() {
+		return jpaFlushMode;
+	}
+
+	@Override
+	public FlushModeType getFlushMode() {
+		return jpaFlushMode != null
+				? jpaFlushMode
+				: entityManager.getFlushMode();
+	}
+
+
+	// Parameters ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	private List<ParameterImplementor> parameters;
+
+	/**
+	 * Hibernate specific extension to the JPA {@link Parameter} contract.
+	 */
+	protected static interface ParameterImplementor<T> extends Parameter<T> {
+		public boolean isBindable();
+
+		public ParameterValue getBoundValue();
+	}
+
+	protected static class ParameterValue {
+		private final Object value;
+		private final TemporalType specifiedTemporalType;
+
+		public ParameterValue(Object value, TemporalType specifiedTemporalType) {
+			this.value = value;
+			this.specifiedTemporalType = specifiedTemporalType;
+		}
+
+		public Object getValue() {
+			return value;
+		}
+
+		public TemporalType getSpecifiedTemporalType() {
+			return specifiedTemporalType;
+		}
+	}
+
+	private Map<ParameterImplementor<?>,ParameterValue> parameterBindingMap;
+
+	private Map<ParameterImplementor<?>,ParameterValue> parameterBindingMap() {
+		if ( parameterBindingMap == null ) {
+			parameterBindingMap = new HashMap<ParameterImplementor<?>, ParameterValue>();
+		}
+		return parameterBindingMap;
+	}
+
+	protected void registerParameter(ParameterImplementor parameter) {
+		if ( parameter == null ) {
+			throw new IllegalArgumentException( "parameter cannot be null" );
+		}
+
+		if ( parameterBindingMap().containsKey( parameter ) ) {
+			return;
+		}
+
+		parameterBindingMap().put( parameter, null );
+	}
+
+	@SuppressWarnings("unchecked")
+	protected void registerParameterBinding(Parameter parameter, ParameterValue bindValue) {
+		validateParameterBinding( (ParameterImplementor) parameter, bindValue );
+		parameterBindingMap().put( (ParameterImplementor) parameter, bindValue );
+	}
+
+	protected void validateParameterBinding(ParameterImplementor parameter, ParameterValue bindValue) {
+		if ( parameter == null ) {
+			throw new IllegalArgumentException( "parameter cannot be null" );
+		}
+
+		if ( ! parameter.isBindable() ) {
+			throw new IllegalArgumentException( "Parameter [" + parameter + "] not valid for binding" );
+		}
+
+		if ( ! parameterBindingMap().containsKey( parameter ) ) {
+			throw new IllegalArgumentException( "Unknown parameter [" + parameter + "] specified for value binding" );
+		}
+
+		if ( isBound( parameter ) ) {
+			throw new IllegalArgumentException( "Parameter [" + parameter + "] already had bound value" );
+		}
+
+		validateParameterBindingTypes( parameter, bindValue );
+	}
+
+	protected abstract void validateParameterBindingTypes(ParameterImplementor parameter, ParameterValue bindValue);
+
+	protected ParameterValue makeBindValue(Object value) {
+		return new ParameterValue( value, null );
+	}
+
+	protected ParameterValue makeBindValue(Calendar value, TemporalType temporalType) {
+		return new ParameterValue( value, temporalType );
+	}
+
+	protected ParameterValue makeBindValue(Date value, TemporalType temporalType) {
+		return new ParameterValue( value, temporalType );
+	}
+
+	@Override
+	public <T> BaseQueryImpl setParameter(Parameter<T> param, T value) {
+		registerParameterBinding( param, makeBindValue( value ) );
+		return this;
+	}
+
+	@Override
+	public BaseQueryImpl setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
+		registerParameterBinding( param, makeBindValue( value, temporalType ) );
+		return this;
+	}
+
+	@Override
+	public BaseQueryImpl setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
+		registerParameterBinding( param, makeBindValue( value, temporalType ) );
+		return this;
+	}
+
+	@Override
+	public BaseQueryImpl setParameter(String name, Object value) {
+		registerParameterBinding( getParameter( name ), makeBindValue( value ) );
+		return this;
+	}
+
+	@Override
+	public BaseQueryImpl setParameter(String name, Calendar value, TemporalType temporalType) {
+		registerParameterBinding( getParameter( name ), makeBindValue( value, temporalType ) );
+		return this;
+	}
+
+	@Override
+	public BaseQueryImpl setParameter(String name, Date value, TemporalType temporalType) {
+		registerParameterBinding( getParameter( name ), makeBindValue( value, temporalType ) );
+		return this;
+	}
+
+	@Override
+	public BaseQueryImpl setParameter(int position, Object value) {
+		registerParameterBinding( getParameter( position ), makeBindValue( value ) );
+		return this;
+	}
+
+	@Override
+	public BaseQueryImpl setParameter(int position, Calendar value, TemporalType temporalType) {
+		registerParameterBinding( getParameter( position ), makeBindValue( value, temporalType ) );
+		return this;
+	}
+
+	@Override
+	public BaseQueryImpl setParameter(int position, Date value, TemporalType temporalType) {
+		registerParameterBinding( getParameter( position ), makeBindValue( value, temporalType ) );
+		return this;
+	}
+
+	@Override
+	@SuppressWarnings("unchecked")
+	public Set<Parameter<?>> getParameters() {
+		return (Set<Parameter<?>>) parameterBindingMap().keySet();
+	}
+
+	@Override
+	public Parameter<?> getParameter(String name) {
+		if ( parameterBindingMap() != null ) {
+			for ( ParameterImplementor<?> param : parameterBindingMap.keySet() ) {
+				if ( name.equals( param.getName() ) ) {
+					return param;
+				}
+			}
+		}
+		throw new IllegalArgumentException( "Parameter with that name [" + name + "] did not exist" );
+	}
+
+	@Override
+	@SuppressWarnings("unchecked")
+	public <T> Parameter<T> getParameter(String name, Class<T> type) {
+		return (Parameter<T>) getParameter( name );
+	}
+
+	@Override
+	public Parameter<?> getParameter(int position) {
+		if ( parameterBindingMap() != null ) {
+			for ( ParameterImplementor<?> param : parameterBindingMap.keySet() ) {
+				if ( position == param.getPosition() ) {
+					return param;
+				}
+			}
+		}
+		throw new IllegalArgumentException( "Parameter with that position [" + position + "] did not exist" );
+	}
+
+	@Override
+	@SuppressWarnings("unchecked")
+	public <T> Parameter<T> getParameter(int position, Class<T> type) {
+		return (Parameter<T>) getParameter( position );
+	}
+
+	@Override
+	public boolean isBound(Parameter<?> param) {
+		return parameterBindingMap() != null
+				&& parameterBindingMap.get( (ParameterImplementor) param ) != null;
+	}
+
+	@Override
+	@SuppressWarnings("unchecked")
+	public <T> T getParameterValue(Parameter<T> param) {
+		if ( parameterBindingMap != null ) {
+			final ParameterValue boundValue = parameterBindingMap.get( (ParameterImplementor) param );
+			if ( boundValue != null ) {
+				return (T) boundValue.getValue();
+			}
+		}
+		throw new IllegalStateException( "Parameter [" + param + "] has not yet been bound" );
+	}
+
+	@Override
+	public Object getParameterValue(String name) {
+		return getParameterValue( getParameter( name ) );
+	}
+
+	@Override
+	public Object getParameterValue(int position) {
+		return getParameterValue( getParameter( position ) );
+	}
+}
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
index 86e5573e6f..0525efa579 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
@@ -1,414 +1,415 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009, 2012, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.ejb;
 
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import javax.persistence.Cache;
 import javax.persistence.EntityManager;
 import javax.persistence.EntityManagerFactory;
 import javax.persistence.PersistenceContextType;
 import javax.persistence.PersistenceException;
 import javax.persistence.PersistenceUnitUtil;
 import javax.persistence.Query;
 import javax.persistence.criteria.CriteriaBuilder;
 import javax.persistence.metamodel.Metamodel;
 import javax.persistence.spi.LoadState;
 import javax.persistence.spi.PersistenceUnitTransactionType;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.Hibernate;
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cfg.Configuration;
-import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.ejb.criteria.CriteriaBuilderImpl;
 import org.hibernate.ejb.internal.EntityManagerFactoryRegistry;
 import org.hibernate.ejb.metamodel.MetamodelImpl;
 import org.hibernate.ejb.util.PersistenceUtilHelper;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinitionBuilder;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.service.ServiceRegistry;
 
 /**
  * Actual Hibernate implementation of {@link javax.persistence.EntityManagerFactory}.
  *
  * @author Gavin King
  * @author Emmanuel Bernard
  * @author Steve Ebersole
  */
 public class EntityManagerFactoryImpl implements HibernateEntityManagerFactory {
 	private static final long serialVersionUID = 5423543L;
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private static final Logger log = Logger.getLogger( EntityManagerFactoryImpl.class );
 
 	private final transient SessionFactoryImpl sessionFactory;
 	private final transient PersistenceUnitTransactionType transactionType;
 	private final transient boolean discardOnClose;
 	private final transient Class sessionInterceptorClass;
 	private final transient CriteriaBuilderImpl criteriaBuilder;
 	private final transient Metamodel metamodel;
 	private final transient HibernatePersistenceUnitUtil util;
 	private final transient Map<String,Object> properties;
 	private final String entityManagerFactoryName;
 
 	private final transient PersistenceUtilHelper.MetadataCache cache = new PersistenceUtilHelper.MetadataCache();
 
 	@SuppressWarnings( "unchecked" )
 	public EntityManagerFactoryImpl(
 			PersistenceUnitTransactionType transactionType,
 			boolean discardOnClose,
 			Class sessionInterceptorClass,
 			Configuration cfg,
 			ServiceRegistry serviceRegistry,
 			String persistenceUnitName) {
 		this.sessionFactory = (SessionFactoryImpl) cfg.buildSessionFactory( serviceRegistry );
 		this.transactionType = transactionType;
 		this.discardOnClose = discardOnClose;
 		this.sessionInterceptorClass = sessionInterceptorClass;
 		final Iterator<PersistentClass> classes = cfg.getClassMappings();
 		final JpaMetaModelPopulationSetting jpaMetaModelPopulationSetting = determineJpaMetaModelPopulationSetting( cfg );
 		if ( JpaMetaModelPopulationSetting.DISABLED == jpaMetaModelPopulationSetting ) {
 			this.metamodel = null;
 		}
 		else {
 			this.metamodel = MetamodelImpl.buildMetamodel(
 					classes,
 					( SessionFactoryImplementor ) sessionFactory,
 					JpaMetaModelPopulationSetting.IGNORE_UNSUPPORTED == jpaMetaModelPopulationSetting
 			);
 		}
 		this.criteriaBuilder = new CriteriaBuilderImpl( this );
 		this.util = new HibernatePersistenceUnitUtil( this );
 
 		HashMap<String,Object> props = new HashMap<String, Object>();
 		addAll( props, ( (SessionFactoryImplementor) sessionFactory ).getProperties() );
 		addAll( props, cfg.getProperties() );
 		this.properties = Collections.unmodifiableMap( props );
 		String entityManagerFactoryName = (String)this.properties.get(AvailableSettings.ENTITY_MANAGER_FACTORY_NAME);
 		if (entityManagerFactoryName == null) {
 			entityManagerFactoryName = persistenceUnitName;
 		}
 		if (entityManagerFactoryName == null) {
 			entityManagerFactoryName = (String) UUID_GENERATOR.generate(null, null);
 		}
 		this.entityManagerFactoryName = entityManagerFactoryName;
 		EntityManagerFactoryRegistry.INSTANCE.addEntityManagerFactory(entityManagerFactoryName, this);
 	}
 	
 	private enum JpaMetaModelPopulationSetting {
 		ENABLED,
 		DISABLED,
 		IGNORE_UNSUPPORTED;
 		
 		private static JpaMetaModelPopulationSetting parse(String setting) {
 			if ( "enabled".equalsIgnoreCase( setting ) ) {
 				return ENABLED;
 			}
 			else if ( "disabled".equalsIgnoreCase( setting ) ) {
 				return DISABLED;
 			}
 			else {
 				return IGNORE_UNSUPPORTED;
 			}
 		}
 	}
 	
 	protected JpaMetaModelPopulationSetting determineJpaMetaModelPopulationSetting(Configuration cfg) {
 		String setting = ConfigurationHelper.getString(
 				AvailableSettings.JPA_METAMODEL_POPULATION,
 				cfg.getProperties(),
 				null
 		);
 		if ( setting == null ) {
 			setting = ConfigurationHelper.getString( AvailableSettings.JPA_METAMODEL_GENERATION, cfg.getProperties(), null );
 			if ( setting != null ) {
 				log.infof( 
 						"Encountered deprecated setting [%s], use [%s] instead",
 						AvailableSettings.JPA_METAMODEL_GENERATION,
 						AvailableSettings.JPA_METAMODEL_POPULATION
 				);
 			}
 		}
 		return JpaMetaModelPopulationSetting.parse( setting );
 	}
 
 	private static void addAll(HashMap<String, Object> propertyMap, Properties properties) {
 		for ( Map.Entry entry : properties.entrySet() ) {
 			if ( String.class.isInstance( entry.getKey() ) ) {
 				propertyMap.put( (String)entry.getKey(), entry.getValue() );
 			}
 		}
 	}
 
 	public EntityManager createEntityManager() {
 		return createEntityManager( null );
 	}
 
 	public EntityManager createEntityManager(Map map) {
 		//TODO support discardOnClose, persistencecontexttype?, interceptor,
 		return new EntityManagerImpl(
 				this, PersistenceContextType.EXTENDED, transactionType,
 				discardOnClose, sessionInterceptorClass, map
 		);
 	}
 
 	public CriteriaBuilder getCriteriaBuilder() {
 		return criteriaBuilder;
 	}
 
 	public Metamodel getMetamodel() {
 		return metamodel;
 	}
 
 	public void close() {
 		sessionFactory.close();
 		EntityManagerFactoryRegistry.INSTANCE.removeEntityManagerFactory(entityManagerFactoryName, this);
 	}
 
 	public Map<String, Object> getProperties() {
 		return properties;
 	}
 
 	public Cache getCache() {
 		// TODO : cache the cache reference?
 		if ( ! isOpen() ) {
 			throw new IllegalStateException("EntityManagerFactory is closed");
 		}
 		return new JPACache( sessionFactory );
 	}
 
 	public PersistenceUnitUtil getPersistenceUnitUtil() {
 		if ( ! isOpen() ) {
 			throw new IllegalStateException("EntityManagerFactory is closed");
 		}
 		return util;
 	}
 
 	@Override
 	public void addNamedQuery(String name, Query query) {
 		if ( ! isOpen() ) {
 			throw new IllegalStateException( "EntityManagerFactory is closed" );
 		}
 
 		if ( ! HibernateQuery.class.isInstance( query ) ) {
 			throw new PersistenceException( "Cannot use query non-Hibernate EntityManager query as basis for named query" );
 		}
 
 		// create and register the proper NamedQueryDefinition...
 		final org.hibernate.Query hibernateQuery = ( (HibernateQuery) query ).getHibernateQuery();
 		if ( org.hibernate.SQLQuery.class.isInstance( hibernateQuery ) ) {
 			final NamedSQLQueryDefinition namedQueryDefinition = extractSqlQueryDefinition( ( org.hibernate.SQLQuery ) hibernateQuery, name );
 			sessionFactory.registerNamedSQLQueryDefinition( name, namedQueryDefinition );
 		}
 		else {
 			final NamedQueryDefinition namedQueryDefinition = extractHqlQueryDefinition( hibernateQuery, name );
 			sessionFactory.registerNamedQueryDefinition( name, namedQueryDefinition );
 		}
 	}
 
 	private NamedSQLQueryDefinition extractSqlQueryDefinition(org.hibernate.SQLQuery nativeSqlQuery, String name) {
 		final NamedSQLQueryDefinitionBuilder builder = new NamedSQLQueryDefinitionBuilder( name );
 		fillInNamedQueryBuilder( builder, nativeSqlQuery );
 		builder.setCallable( nativeSqlQuery.isCallable() )
 				.setQuerySpaces( nativeSqlQuery.getSynchronizedQuerySpaces() )
 				.setQueryReturns( nativeSqlQuery.getQueryReturns() );
 		return builder.createNamedQueryDefinition();
 	}
 
 	private NamedQueryDefinition extractHqlQueryDefinition(org.hibernate.Query hqlQuery, String name) {
 		final NamedQueryDefinitionBuilder builder = new NamedQueryDefinitionBuilder( name );
 		fillInNamedQueryBuilder( builder, hqlQuery );
 		// LockOptions only valid for HQL/JPQL queries...
 		builder.setLockOptions( hqlQuery.getLockOptions().makeCopy() );
 		return builder.createNamedQueryDefinition();
 	}
 
 	private void fillInNamedQueryBuilder(NamedQueryDefinitionBuilder builder, org.hibernate.Query query) {
 		builder.setQuery( query.getQueryString() )
 				.setComment( query.getComment() )
 				.setCacheable( query.isCacheable() )
 				.setCacheRegion( query.getCacheRegion() )
 				.setCacheMode( query.getCacheMode() )
 				.setTimeout( query.getTimeout() )
 				.setFetchSize( query.getFetchSize() )
 				.setFirstResult( query.getFirstResult() )
 				.setMaxResults( query.getMaxResults() )
 				.setReadOnly( query.isReadOnly() )
 				.setFlushMode( query.getFlushMode() );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> T unwrap(Class<T> cls) {
 		if ( SessionFactory.class.isAssignableFrom( cls ) ) {
 			return ( T ) sessionFactory;
 		}
 		if ( SessionFactoryImplementor.class.isAssignableFrom( cls ) ) {
 			return ( T ) sessionFactory;
 		}
 		if ( EntityManager.class.isAssignableFrom( cls ) ) {
 			return ( T ) this;
 		}
 		throw new PersistenceException( "Hibernate cannot unwrap EntityManagerFactory as " + cls.getName() );
 	}
 
 	public boolean isOpen() {
 		return ! sessionFactory.isClosed();
 	}
 
 	public SessionFactoryImpl getSessionFactory() {
 		return sessionFactory;
 	}
 
 	public String getEntityManagerFactoryName() {
 		return entityManagerFactoryName;
 	}
 
 	private static class JPACache implements Cache {
 		private SessionFactoryImplementor sessionFactory;
 
 		private JPACache(SessionFactoryImplementor sessionFactory) {
 			this.sessionFactory = sessionFactory;
 		}
 
 		public boolean contains(Class entityClass, Object identifier) {
 			return sessionFactory.getCache().containsEntity( entityClass, ( Serializable ) identifier );
 		}
 
 		public void evict(Class entityClass, Object identifier) {
 			sessionFactory.getCache().evictEntity( entityClass, ( Serializable ) identifier );
 		}
 
 		public void evict(Class entityClass) {
 			sessionFactory.getCache().evictEntityRegion( entityClass );
 		}
 
 		public void evictAll() {
 			sessionFactory.getCache().evictEntityRegions();
 // TODO : if we want to allow an optional clearing of all cache data, the additional calls would be:
 //			sessionFactory.getCache().evictCollectionRegions();
 //			sessionFactory.getCache().evictQueryRegions();
 		}
 
 		@Override
 		@SuppressWarnings("unchecked")
 		public <T> T unwrap(Class<T> cls) {
 			if ( RegionFactory.class.isAssignableFrom( cls ) ) {
 				return (T) sessionFactory.getSettings().getRegionFactory();
 			}
 			if ( org.hibernate.Cache.class.isAssignableFrom( cls ) ) {
 				return (T) sessionFactory.getCache();
 			}
 			throw new PersistenceException( "Hibernate cannot unwrap Cache as " + cls.getName() );
 		}
 	}
 
 	private static EntityManagerFactory getNamedEntityManagerFactory(String entityManagerFactoryName) throws InvalidObjectException {
 		Object result = EntityManagerFactoryRegistry.INSTANCE.getNamedEntityManagerFactory(entityManagerFactoryName);
 
 		if ( result == null ) {
 			throw new InvalidObjectException( "could not resolve entity manager factory during entity manager deserialization [name=" + entityManagerFactoryName + "]" );
 		}
 
 		return (EntityManagerFactory)result;
 	}
 
 	private void writeObject(ObjectOutputStream oos) throws IOException {
 		if (entityManagerFactoryName == null) {
 			throw new InvalidObjectException( "could not serialize entity manager factory with null entityManagerFactoryName" );
 		}
 		oos.defaultWriteObject();
 	}
 
 	/**
 	 * After deserialization of an EntityManagerFactory, this is invoked to return the EntityManagerFactory instance
 	 * that is already in use rather than a cloned copy of the object.
 	 *
 	 * @return
 	 * @throws InvalidObjectException
 	 */
 	private Object readResolve() throws InvalidObjectException {
 		return getNamedEntityManagerFactory(entityManagerFactoryName);
 	}
 
 
 
 	private static class HibernatePersistenceUnitUtil implements PersistenceUnitUtil, Serializable {
 		private final HibernateEntityManagerFactory emf;
 		private transient PersistenceUtilHelper.MetadataCache cache;
 
 		private HibernatePersistenceUnitUtil(EntityManagerFactoryImpl emf) {
 			this.emf = emf;
 			this.cache = emf.cache;
 		}
 
 		public boolean isLoaded(Object entity, String attributeName) {
 			// added log message to help with HHH-7454, if state == LoadState,NOT_LOADED, returning true or false is not accurate.
 			log.debug("PersistenceUnitUtil#isLoaded is not always accurate; consider using EntityManager#contains instead");
 			LoadState state = PersistenceUtilHelper.isLoadedWithoutReference( entity, attributeName, cache );
 			if (state == LoadState.LOADED) {
 				return true;
 			}
 			else if (state == LoadState.NOT_LOADED ) {
 				return false;
 			}
 			else {
 				return PersistenceUtilHelper.isLoadedWithReference( entity, attributeName, cache ) != LoadState.NOT_LOADED;
 			}
 		}
 
 		public boolean isLoaded(Object entity) {
 			// added log message to help with HHH-7454, if state == LoadState,NOT_LOADED, returning true or false is not accurate.
 			log.debug("PersistenceUnitUtil#isLoaded is not always accurate; consider using EntityManager#contains instead");
 			return PersistenceUtilHelper.isLoaded( entity ) != LoadState.NOT_LOADED;
 		}
 
 		public Object getIdentifier(Object entity) {
 			final Class entityClass = Hibernate.getClass( entity );
 			final ClassMetadata classMetadata = emf.getSessionFactory().getClassMetadata( entityClass );
 			if (classMetadata == null) {
 				throw new IllegalArgumentException( entityClass + " is not an entity" );
 			}
 			//TODO does that work for @IdClass?
 			return classMetadata.getIdentifier( entity );
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerImpl.java
index d34d476d6b..05ba76dd08 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerImpl.java
@@ -1,192 +1,169 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
+ * Copyright (c) 2009, 2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.ejb;
 
-import java.util.Map;
 import javax.persistence.PersistenceContextType;
 import javax.persistence.PersistenceException;
-import javax.persistence.Query;
-import javax.persistence.StoredProcedureQuery;
 import javax.persistence.spi.PersistenceUnitTransactionType;
+import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.Session;
-import org.hibernate.cfg.NotYetImplementedException;
-import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.annotations.common.util.ReflectHelper;
 import org.hibernate.ejb.internal.EntityManagerMessageLogger;
 import org.hibernate.engine.spi.SessionBuilderImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
+import org.hibernate.engine.spi.SessionOwner;
 
 /**
  * Hibernate implementation of {@link javax.persistence.EntityManager}.
  *
  * @author Gavin King
  */
 public class EntityManagerImpl extends AbstractEntityManagerImpl implements SessionOwner {
 
     public static final EntityManagerMessageLogger LOG = Logger.getMessageLogger(EntityManagerMessageLogger.class,
                                                                           EntityManagerImpl.class.getName());
 
 	protected Session session;
 	protected boolean open;
 	protected boolean discardOnClose;
 	private Class sessionInterceptorClass;
 
 	public EntityManagerImpl(
 			EntityManagerFactoryImpl entityManagerFactory,
 			PersistenceContextType pcType,
 			PersistenceUnitTransactionType transactionType,
 			boolean discardOnClose,
 			Class sessionInterceptorClass,
 			Map properties) {
 		super( entityManagerFactory, pcType, transactionType, properties );
 		this.open = true;
 		this.discardOnClose = discardOnClose;
 		Object localSessionInterceptor = null;
 		if (properties != null) {
 			localSessionInterceptor = properties.get( AvailableSettings.SESSION_INTERCEPTOR );
 		}
 		if ( localSessionInterceptor != null ) {
 			if (localSessionInterceptor instanceof Class) {
 				sessionInterceptorClass = (Class) localSessionInterceptor;
 			}
 			else if (localSessionInterceptor instanceof String) {
 				try {
 					sessionInterceptorClass =
 							ReflectHelper.classForName( (String) localSessionInterceptor, EntityManagerImpl.class );
 				}
 				catch (ClassNotFoundException e) {
 					throw new PersistenceException("Unable to instanciate interceptor: " + localSessionInterceptor, e);
 				}
 			}
 			else {
 				throw new PersistenceException("Unable to instanciate interceptor: " + localSessionInterceptor);
 			}
 		}
 		this.sessionInterceptorClass = sessionInterceptorClass;
 		postInit();
 	}
 
 	@Override
     public Session getSession() {
 		if ( !open ) {
 			throw new IllegalStateException( "EntityManager is closed" );
 		}
 		return getRawSession();
 	}
 
 	@Override
     protected Session getRawSession() {
 		if ( session == null ) {
 			SessionBuilderImplementor sessionBuilder = getEntityManagerFactory().getSessionFactory().withOptions();
 			sessionBuilder.owner( this );
 			if (sessionInterceptorClass != null) {
 				try {
 					Interceptor interceptor = (Interceptor) sessionInterceptorClass.newInstance();
 					sessionBuilder.interceptor( interceptor );
 				}
 				catch (InstantiationException e) {
 					throw new PersistenceException("Unable to instanciate session interceptor: " + sessionInterceptorClass, e);
 				}
 				catch (IllegalAccessException e) {
 					throw new PersistenceException("Unable to instanciate session interceptor: " + sessionInterceptorClass, e);
 				}
 				catch (ClassCastException e) {
 					throw new PersistenceException("Session interceptor does not implement Interceptor: " + sessionInterceptorClass, e);
 				}
 			}
 			sessionBuilder.autoJoinTransactions( getTransactionType() != PersistenceUnitTransactionType.JTA );
 			session = sessionBuilder.openSession();
 			if ( persistenceContextType == PersistenceContextType.TRANSACTION ) {
 				( (SessionImplementor) session ).setAutoClear( true );
 			}
 		}
 		return session;
 	}
 
-	@Override
-	public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
-		throw new NotYetImplementedException();
-	}
-
-	@Override
-	public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
-		throw new NotYetImplementedException();
-	}
-
-	@Override
-	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
-		throw new NotYetImplementedException();
-	}
-
-	@Override
-	public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
-		throw new NotYetImplementedException();
-	}
-
 	public void close() {
 		checkEntityManagerFactory();
 		if ( !open ) {
 			throw new IllegalStateException( "EntityManager is closed" );
 		}
 		if ( discardOnClose || !isTransactionInProgress() ) {
 			//close right now
 			if ( session != null ) {
 				session.close();
 			}
 		}
 		// Otherwise, session auto-close will be enabled by shouldAutoCloseSession().
 		open = false;
 	}
 
 	public boolean isOpen() {
 		//adjustFlushMode(); //don't adjust, can't be done on closed EM
 		checkEntityManagerFactory();
 		try {
 			if ( open ) {
 				getSession().isOpen(); //to force enlistment in tx
 			}
 			return open;
 		}
 		catch (HibernateException he) {
 			throwPersistenceException( he );
 			return false;
 		}
 	}
 
 	@Override
 	public boolean shouldAutoCloseSession() {
 		return !isOpen();
 	}
 
 	private void checkEntityManagerFactory() {
 		if (! getEntityManagerFactory().isOpen()) {
 			open = false;
 		}
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/StoredProcedureQueryImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/StoredProcedureQueryImpl.java
new file mode 100644
index 0000000000..c1c3e4f96b
--- /dev/null
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/StoredProcedureQueryImpl.java
@@ -0,0 +1,283 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2012, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.ejb;
+
+import javax.persistence.FlushModeType;
+import javax.persistence.LockModeType;
+import javax.persistence.Parameter;
+import javax.persistence.ParameterMode;
+import javax.persistence.Query;
+import javax.persistence.StoredProcedureQuery;
+import javax.persistence.TemporalType;
+import java.util.Calendar;
+import java.util.Date;
+import java.util.List;
+
+import org.hibernate.CacheMode;
+import org.hibernate.FlushMode;
+import org.hibernate.LockMode;
+import org.hibernate.StoredProcedureCall;
+import org.hibernate.StoredProcedureOutputs;
+import org.hibernate.StoredProcedureResultSetReturn;
+import org.hibernate.StoredProcedureReturn;
+import org.hibernate.StoredProcedureUpdateCountReturn;
+
+/**
+ * @author Steve Ebersole
+ */
+public class StoredProcedureQueryImpl extends BaseQueryImpl implements StoredProcedureQuery {
+	private final StoredProcedureCall storedProcedureCall;
+	private StoredProcedureOutputs storedProcedureOutputs;
+
+	public StoredProcedureQueryImpl(StoredProcedureCall storedProcedureCall, HibernateEntityManagerImplementor entityManager) {
+		super( entityManager );
+		this.storedProcedureCall = storedProcedureCall;
+	}
+
+	@Override
+	protected boolean applyTimeoutHint(int timeout) {
+		storedProcedureCall.setTimeout( timeout );
+		return true;
+	}
+
+	@Override
+	protected boolean applyCacheableHint(boolean isCacheable) {
+		storedProcedureCall.setCacheable( isCacheable );
+		return true;
+	}
+
+	@Override
+	protected boolean applyCacheRegionHint(String regionName) {
+		storedProcedureCall.setCacheRegion( regionName );
+		return true;
+	}
+
+	@Override
+	protected boolean applyReadOnlyHint(boolean isReadOnly) {
+		storedProcedureCall.setReadOnly( isReadOnly );
+		return true;
+	}
+
+	@Override
+	protected boolean applyCacheModeHint(CacheMode cacheMode) {
+		storedProcedureCall.setCacheMode( cacheMode );
+		return true;
+	}
+
+	@Override
+	protected boolean applyFlushModeHint(FlushMode flushMode) {
+		storedProcedureCall.setFlushMode( flushMode );
+		return true;
+	}
+
+	@Override
+	@SuppressWarnings("unchecked")
+	public StoredProcedureQuery registerStoredProcedureParameter(int position, Class type, ParameterMode mode) {
+		storedProcedureCall.registerStoredProcedureParameter( position, type, mode );
+		return this;
+	}
+
+	@Override
+	public StoredProcedureQuery registerStoredProcedureParameter(String parameterName, Class type, ParameterMode mode) {
+		storedProcedureCall.registerStoredProcedureParameter( parameterName, type, mode );
+		return this;
+	}
+
+	@Override
+	protected void validateParameterBindingTypes(ParameterImplementor parameter, ParameterValue bindValue) {
+	}
+
+
+	// covariant returns ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public StoredProcedureQueryImpl setFlushMode(FlushModeType jpaFlushMode) {
+		return (StoredProcedureQueryImpl) super.setFlushMode( jpaFlushMode );
+	}
+
+	@Override
+	public StoredProcedureQueryImpl setHint(String hintName, Object value) {
+		return (StoredProcedureQueryImpl) super.setHint( hintName, value );
+	}
+
+	@Override
+	public <T> StoredProcedureQueryImpl setParameter(Parameter<T> param, T value) {
+		return (StoredProcedureQueryImpl) super.setParameter( param, value );
+	}
+
+	@Override
+	public StoredProcedureQueryImpl setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
+		return (StoredProcedureQueryImpl) super.setParameter( param, value, temporalType );
+	}
+
+	@Override
+	public StoredProcedureQueryImpl setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
+		return (StoredProcedureQueryImpl) super.setParameter( param, value, temporalType );
+	}
+
+	@Override
+	public StoredProcedureQueryImpl setParameter(String name, Object value) {
+		return ( StoredProcedureQueryImpl) super.setParameter( name, value );
+	}
+
+	@Override
+	public StoredProcedureQueryImpl setParameter(String name, Calendar value, TemporalType temporalType) {
+		return (StoredProcedureQueryImpl) super.setParameter( name, value, temporalType );
+	}
+
+	@Override
+	public StoredProcedureQueryImpl setParameter(String name, Date value, TemporalType temporalType) {
+		return (StoredProcedureQueryImpl) super.setParameter( name, value, temporalType );
+	}
+
+	@Override
+	public StoredProcedureQueryImpl setParameter(int position, Object value) {
+		return (StoredProcedureQueryImpl) super.setParameter( position, value );
+	}
+
+	@Override
+	public StoredProcedureQueryImpl setParameter(int position, Calendar value, TemporalType temporalType) {
+		return (StoredProcedureQueryImpl) super.setParameter( position, value, temporalType );
+	}
+
+	@Override
+	public StoredProcedureQueryImpl setParameter(int position, Date value, TemporalType temporalType) {
+		return (StoredProcedureQueryImpl) super.setParameter( position, value, temporalType );
+	}
+
+
+	// outputs ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	private StoredProcedureOutputs outputs() {
+		if ( storedProcedureOutputs == null ) {
+			storedProcedureOutputs = storedProcedureCall.getOutputs();
+		}
+		return storedProcedureOutputs;
+	}
+
+	@Override
+	public Object getOutputParameterValue(int position) {
+		return outputs().getOutputParameterValue( position );
+	}
+
+	@Override
+	public Object getOutputParameterValue(String parameterName) {
+		return outputs().getOutputParameterValue( parameterName );
+	}
+
+	@Override
+	public boolean execute() {
+		return outputs().hasMoreReturns();
+	}
+
+	@Override
+	public int executeUpdate() {
+		return getUpdateCount();
+	}
+
+	@Override
+	public boolean hasMoreResults() {
+		return outputs().hasMoreReturns();
+	}
+
+	@Override
+	public int getUpdateCount() {
+		final StoredProcedureReturn nextReturn = outputs().getNextReturn();
+		if ( nextReturn.isResultSet() ) {
+			return -1;
+		}
+		return ( (StoredProcedureUpdateCountReturn) nextReturn ).getUpdateCount();
+	}
+
+	@Override
+	public List getResultList() {
+		final StoredProcedureReturn nextReturn = outputs().getNextReturn();
+		if ( ! nextReturn.isResultSet() ) {
+			return null; // todo : what should be thrown/returned here?
+		}
+		return ( (StoredProcedureResultSetReturn) nextReturn ).getResultList();
+	}
+
+	@Override
+	public Object getSingleResult() {
+		final StoredProcedureReturn nextReturn = outputs().getNextReturn();
+		if ( ! nextReturn.isResultSet() ) {
+			return null; // todo : what should be thrown/returned here?
+		}
+		return ( (StoredProcedureResultSetReturn) nextReturn ).getSingleResult();
+	}
+
+	@Override
+	public <T> T unwrap(Class<T> cls) {
+		return null;
+	}
+
+
+	// ugh ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public Query setLockMode(LockModeType lockMode) {
+		return null;
+	}
+
+	@Override
+	public LockModeType getLockMode() {
+		return null;
+	}
+
+
+	// unsupported hints/calls ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	protected void applyFirstResult(int firstResult) {
+	}
+
+	@Override
+	protected void applyMaxResults(int maxResults) {
+	}
+
+	@Override
+	protected boolean canApplyLockModesHints() {
+		return false;
+	}
+
+	@Override
+	protected void applyAliasSpecificLockModeHint(String alias, LockMode lockMode) {
+	}
+
+	@Override
+	protected boolean applyLockTimeoutHint(int timeout) {
+		return false;
+	}
+
+	@Override
+	protected boolean applyCommentHint(String comment) {
+		return false;
+	}
+
+	@Override
+	protected boolean applyFetchSize(int fetchSize) {
+		return false;
+	}
+}
