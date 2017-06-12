diff --git a/hibernate-core/src/main/java/org/hibernate/CacheMode.java b/hibernate-core/src/main/java/org/hibernate/CacheMode.java
index 80d2e75755..43d381e88f 100755
--- a/hibernate-core/src/main/java/org/hibernate/CacheMode.java
+++ b/hibernate-core/src/main/java/org/hibernate/CacheMode.java
@@ -1,80 +1,105 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc..
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
 package org.hibernate;
 
+import org.hibernate.tool.hbm2ddl.SchemaExportTask;
+
 /**
  * Controls how the session interacts with the second-level
  * cache and query cache.
  *
  * @author Gavin King
  * @author Strong Liu
  * @see Session#setCacheMode(CacheMode)
  */
 public enum CacheMode {
 
 	/**
 	 * The session may read items from the cache, and add items to the cache
 	 */
-	NORMAL( true, true),
+	NORMAL( true, true ),
 	/**
 	 * The session will never interact with the cache, except to invalidate
 	 * cache items when updates occur
 	 */
-	IGNORE( false, false),
+	IGNORE( false, false ),
 	/**
 	 * The session may read items from the cache, but will not add items,
 	 * except to invalidate items when updates occur
 	 */
-	GET( false, true),
+	GET( false, true ),
 	/**
 	 * The session will never read items from the cache, but will add items
 	 * to the cache as it reads them from the database.
 	 */
-	PUT( true, false),
+	PUT( true, false ),
 	/**
 	 * The session will never read items from the cache, but will add items
 	 * to the cache as it reads them from the database. In this mode, the
 	 * effect of <tt>hibernate.cache.use_minimal_puts</tt> is bypassed, in
 	 * order to <em>force</em> a cache refresh
 	 */
-	REFRESH( true, false);
+	REFRESH( true, false );
 
 
 	private final boolean isPutEnabled;
 	private final boolean isGetEnabled;
 
 	CacheMode( boolean isPutEnabled, boolean isGetEnabled) {
 		this.isPutEnabled = isPutEnabled;
 		this.isGetEnabled = isGetEnabled;
 	}
 
 	public boolean isGetEnabled() {
 		return isGetEnabled;
 	}
 
 	public boolean isPutEnabled() {
 		return isPutEnabled;
 	}
+
+	public static CacheMode interpretExternalSetting(String setting) {
+		if (setting == null) {
+			return null;
+		}
+
+		if ( GET.name().equalsIgnoreCase( setting ) ) {
+			return CacheMode.GET;
+		}
+		if ( IGNORE.name().equalsIgnoreCase( setting ) ) {
+			return CacheMode.IGNORE;
+		}
+		if ( NORMAL.name().equalsIgnoreCase( setting ) ) {
+			return CacheMode.NORMAL;
+		}
+		if ( PUT.name().equalsIgnoreCase( setting ) ) {
+			return CacheMode.PUT;
+		}
+		if ( REFRESH.name().equalsIgnoreCase( setting ) ) {
+			return CacheMode.REFRESH;
+		}
+
+		throw new MappingException( "Unknown Cache Mode: " + setting );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/FlushMode.java b/hibernate-core/src/main/java/org/hibernate/FlushMode.java
index 4c906a6d62..7c223f0731 100644
--- a/hibernate-core/src/main/java/org/hibernate/FlushMode.java
+++ b/hibernate-core/src/main/java/org/hibernate/FlushMode.java
@@ -1,87 +1,110 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008 Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 /**
  * Represents a flushing strategy. The flush process synchronizes
  * database state with session state by detecting state changes
  * and executing SQL statements.
  *
  * @see Session#setFlushMode(FlushMode)
  * @see Query#setFlushMode(FlushMode)
  * @see Criteria#setFlushMode(FlushMode)
  *
  * @author Gavin King
  */
 public enum FlushMode {
 		/**
 	 * The {@link Session} is never flushed unless {@link Session#flush}
 	 * is explicitly called by the application. This mode is very
 	 * efficient for read only transactions.
 	 *
 	 * @deprecated use {@link #MANUAL} instead.
 	 */
 	NEVER ( 0 ),
 
 	/**
 	 * The {@link Session} is only ever flushed when {@link Session#flush}
 	 * is explicitly called by the application. This mode is very
 	 * efficient for read only transactions.
 	 */
 	MANUAL( 0 ),
 
 	/**
 	 * The {@link Session} is flushed when {@link Transaction#commit}
 	 * is called.
 	 */
 	COMMIT(5 ),
 
 	/**
 	 * The {@link Session} is sometimes flushed before query execution
 	 * in order to ensure that queries never return stale state. This
 	 * is the default flush mode.
 	 */
 	AUTO(10 ),
 
 	/**
 	 * The {@link Session} is flushed before every query. This is
 	 * almost always unnecessary and inefficient.
 	 */
 	ALWAYS(20 );
 
 	private final int level;
 
 	private FlushMode(int level) {
 		this.level = level;
 	}
 	
 	public boolean lessThan(FlushMode other) {
 		return this.level<other.level;
 	}
 
 	public static boolean isManualFlushMode(FlushMode mode) {
 		return MANUAL.level == mode.level;
 	}
+
+	public static FlushMode interpretExternalSetting(String setting) {
+		if ( setting == null ) {
+			return null;
+		}
+
+		if ( AUTO.name().equalsIgnoreCase( setting ) ) {
+			return FlushMode.AUTO;
+		}
+		if ( COMMIT.name().equalsIgnoreCase( setting ) ) {
+			return FlushMode.COMMIT;
+		}
+		if ( NEVER.name().equalsIgnoreCase( setting ) ) {
+			return FlushMode.NEVER;
+		}
+		if ( MANUAL.name().equalsIgnoreCase( setting ) ) {
+			return FlushMode.MANUAL;
+		}
+		if ( ALWAYS.name().equalsIgnoreCase( setting ) ) {
+			return FlushMode.ALWAYS;
+		}
+
+		throw new MappingException( "unknown FlushMode : " + setting );
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/LockOptions.java b/hibernate-core/src/main/java/org/hibernate/LockOptions.java
index ed47606f5e..a86b8b4f91 100644
--- a/hibernate-core/src/main/java/org/hibernate/LockOptions.java
+++ b/hibernate-core/src/main/java/org/hibernate/LockOptions.java
@@ -1,257 +1,263 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009-2012, Red Hat Inc. or third-party contributors as
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
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 /**
  * Contains locking details (LockMode, Timeout and Scope).
  * 
  * @author Scott Marlow
  */
 public class LockOptions implements Serializable {
 	/**
 	 * NONE represents LockMode.NONE (timeout + scope do not apply)
 	 */
 	public static final LockOptions NONE = new LockOptions(LockMode.NONE);
 
 	/**
 	 * READ represents LockMode.READ (timeout + scope do not apply)
 	 */
 	public static final LockOptions READ = new LockOptions(LockMode.READ);
 
 	/**
 	 * UPGRADE represents LockMode.UPGRADE (will wait forever for lock and
 	 * scope of false meaning only entity is locked)
 	 */
 	public static final LockOptions UPGRADE = new LockOptions(LockMode.UPGRADE);
 
 	/**
 	 * Indicates that the database should not wait at all to acquire the pessimistic lock.
 	 * @see #getTimeOut
 	 */
 	public static final int NO_WAIT = 0;
 
 	/**
 	 * Indicates that there is no timeout for the acquisition.
 	 * @see #getTimeOut
 	 */
 	public static final int WAIT_FOREVER = -1;
 
 	private LockMode lockMode = LockMode.NONE;
 	private int timeout = WAIT_FOREVER;
 	private Map aliasSpecificLockModes = null; //initialize lazily as LockOptions is frequently created without needing this
 
 	public LockOptions() {
 	}
 
 	public LockOptions( LockMode lockMode) {
 		this.lockMode = lockMode;
 	}
 
 
 	/**
 	 * Retrieve the overall lock mode in effect for this set of options.
 	 * <p/>
 	 * In certain contexts (hql and criteria), lock-modes can be defined in an
 	 * even more granular {@link #setAliasSpecificLockMode(String, LockMode) per-alias} fashion
 	 *
 	 * @return The overall lock mode.
 	 */
 	public LockMode getLockMode() {
 		return lockMode;
 	}
 
 	/**
 	 * Set the overall {@link LockMode} to be used.  The default is
 	 * {@link LockMode#NONE}
 	 *
 	 * @param lockMode The new overall lock mode to use.
 	 *
 	 * @return this (for method chaining).
 	 */
 	public LockOptions setLockMode(LockMode lockMode) {
 		this.lockMode = lockMode;
 		return this;
 	}
 
 
 	/**
 	 * Specify the {@link LockMode} to be used for a specific query alias.
 	 *
 	 * @param alias used to reference the LockMode.
 	 * @param lockMode The lock mode to apply to the given alias
 	 * @return this LockRequest instance for operation chaining.
 	 *
 	 * @see Query#setLockMode(String, LockMode)
 	 * @see Criteria#setLockMode(LockMode)
 	 * @see Criteria#setLockMode(String, LockMode)
 	 */
 	public LockOptions setAliasSpecificLockMode(String alias, LockMode lockMode) {
 		if ( aliasSpecificLockModes == null ) {
 			aliasSpecificLockModes = new HashMap();
 		}
 		aliasSpecificLockModes.put( alias, lockMode );
 		return this;
 	}
 
 	/**
 	 * Get the {@link LockMode} explicitly specified for the given alias via
 	 * {@link #setAliasSpecificLockMode}
 	 * <p/>
 	 * Differs from {@link #getEffectiveLockMode} in that here we only return
 	 * explicitly specified alias-specific lock modes.
 	 *
 	 * @param alias The alias for which to locate the explicit lock mode.
 	 *
 	 * @return The explicit lock mode for that alias.
 	 */
 	public LockMode getAliasSpecificLockMode(String alias) {
 		if ( aliasSpecificLockModes == null ) {
 			return null;
 		}
 		return (LockMode) aliasSpecificLockModes.get( alias );
 	}
 
 	/**
 	 * Determine the {@link LockMode} to apply to the given alias.  If no
 	 * mode was explicitly {@link #setAliasSpecificLockMode set}, the
 	 * {@link #getLockMode overall mode} is returned.  If the overall lock mode is
 	 * <tt>null</tt> as well, {@link LockMode#NONE} is returned.
 	 * <p/>
 	 * Differs from {@link #getAliasSpecificLockMode} in that here we fallback to we only return
 	 * the overall lock mode.
 	 *
 	 * @param alias The alias for which to locate the effective lock mode.
 	 *
 	 * @return The effective lock mode.
 	 */
 	public LockMode getEffectiveLockMode(String alias) {
 		LockMode lockMode = getAliasSpecificLockMode( alias );
 		if ( lockMode == null ) {
 			lockMode = this.lockMode;
 		}
 		return lockMode == null ? LockMode.NONE : lockMode;
 	}
 
 	/**
 	 * Get the number of aliases that have specific lock modes defined.
 	 *
 	 * @return the number of explicitly defined alias lock modes.
 	 */
 	public int getAliasLockCount() {
 		if ( aliasSpecificLockModes == null ) {
 			return 0;
 		}
 		return aliasSpecificLockModes.size();
 	}
 
 	/**
 	 * Iterator for accessing Alias (key) and LockMode (value) as Map.Entry
 	 *
 	 * @return Iterator for accessing the Map.Entry's
 	 */
 	public Iterator getAliasLockIterator() {
 		if ( aliasSpecificLockModes == null ) {
 			return Collections.emptyList().iterator();
 		}
 		return aliasSpecificLockModes.entrySet().iterator();
 	}
 
 	/**
 	 * Retrieve the current timeout setting.
 	 * <p/>
 	 * The timeout is the amount of time, in milliseconds, we should instruct the database
 	 * to wait for any requested pessimistic lock acquisition.
 	 * <p/>
 	 * {@link #NO_WAIT} and {@link #WAIT_FOREVER} represent 2 "magic" values.
 	 *
 	 * @return timeout in milliseconds, or {@link #NO_WAIT} or {@link #WAIT_FOREVER}
 	 */
 	public int getTimeOut() {
 		return timeout;
 	}
 
 	/**
 	 * Set the timeout setting.
 	 * <p/>
 	 * See {@link #getTimeOut} for a discussion of meaning.
 	 *
 	 * @param timeout The new timeout setting.
 	 *
 	 * @return this (for method chaining).
 	 *
 	 * @see #getTimeOut
 	 */
 	public LockOptions setTimeOut(int timeout) {
 		this.timeout = timeout;
 		return this;
 	}
 
 	private boolean scope=false;
 
 	/**
 	 * Retrieve the current lock scope setting.
 	 * <p/>
 	 * "scope" is a JPA defined term.  It is basically a cascading of the lock to associations.
 	 *
 	 * @return true if locking will be extended to owned associations
 	 */
 	public boolean getScope() {
 		return scope;
 	}
 
 	/**
 	 * Set the cope.
 	 *
 	 * @param scope The new scope setting
 	 *
 	 * @return this (for method chaining).
 	 */
 	public LockOptions setScope(boolean scope) {
 		this.scope = scope;
 		return this;
 	}
 
+	public LockOptions makeCopy() {
+		final LockOptions copy = new LockOptions();
+		copy( this, copy );
+		return copy;
+	}
+
 	/**
 	 * Shallow copy From to Dest
 	 *
 	 * @param from is copied from
 	 * @param dest is copied to
 	 * @return dest
 	 */
 	public static LockOptions copy(LockOptions from, LockOptions dest) {
 		dest.setLockMode(from.getLockMode());
 		dest.setScope(from.getScope());
 		dest.setTimeOut(from.getTimeOut());
 		if ( from.aliasSpecificLockModes != null ) {
 			dest.aliasSpecificLockModes = new HashMap( from.aliasSpecificLockModes );
 		}
 		return dest;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/Query.java b/hibernate-core/src/main/java/org/hibernate/Query.java
index 6f74d5758e..6b08d63043 100644
--- a/hibernate-core/src/main/java/org/hibernate/Query.java
+++ b/hibernate-core/src/main/java/org/hibernate/Query.java
@@ -1,468 +1,634 @@
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
+ *
  * @author Gavin King
  */
 public interface Query {
 	/**
 	 * Get the query string.
 	 *
 	 * @return the query string
 	 */
 	public String getQueryString();
+
 	/**
-	 * Return the Hibernate types of the query result set.
-	 * @return an array of types
+	 * Obtains the limit set on the maximum number of rows to retrieve.  No set limit means there is no limit set
+	 * on the number of rows returned.  Technically both {@code null} and any negative values are interpreted as no
+	 * limit; however, this method should always return null in such case.
+	 *
+	 * @return The
 	 */
-	public Type[] getReturnTypes() throws HibernateException;
+	public Integer getMaxResults();
+
 	/**
-	 * Return the HQL select clause aliases (if any)
-	 * @return an array of aliases as strings
+	 * Set the maximum number of rows to retrieve.
+	 *
+	 * @param maxResults the maximum number of rows
+	 *
+	 * @see #getMaxResults()
 	 */
-	public String[] getReturnAliases() throws HibernateException;
+	public Query setMaxResults(int maxResults);
+
 	/**
-	 * Return the names of all named parameters of the query.
-	 * @return the parameter names, in no particular order
+	 * Obtain the value specified (if any) for the first row to be returned from the query results; zero-based.  Used,
+	 * in conjunction with {@link #getMaxResults()} in "paginated queries".  No value specified means the first result
+	 * is returned.  Zero and negative numbers are the same as no setting.
+	 *
+	 * @return The first result number.
 	 */
-	public String[] getNamedParameters() throws HibernateException;
+	public Integer getFirstResult();
+
 	/**
-	 * Return the query results as an <tt>Iterator</tt>. If the query
-	 * contains multiple results pre row, the results are returned in
-	 * an instance of <tt>Object[]</tt>.<br>
-	 * <br>
-	 * Entities returned as results are initialized on demand. The first
-	 * SQL query returns identifiers only.<br>
+	 * Set the first row to retrieve.
 	 *
-	 * @return the result iterator
-	 * @throws HibernateException
+	 * @param firstResult a row number, numbered from <tt>0</tt>
+	 *
+	 * @see #getFirstResult()
 	 */
-	public Iterator iterate() throws HibernateException;
+	public Query setFirstResult(int firstResult);
+
 	/**
-	 * Return the query results as <tt>ScrollableResults</tt>. The
-	 * scrollability of the returned results depends upon JDBC driver
-	 * support for scrollable <tt>ResultSet</tt>s.<br>
+	 * Obtain the FlushMode in effect for this query.  By default, the query inherits the FlushMode of the Session
+	 * from which is originates.
 	 *
-	 * @see ScrollableResults
-	 * @return the result iterator
-	 * @throws HibernateException
+	 * @return The query FlushMode.
+	 *
+	 * @see Session#getFlushMode()
+	 * @see FlushMode
 	 */
-	public ScrollableResults scroll() throws HibernateException;
+	public FlushMode getFlushMode();
+
 	/**
-	 * Return the query results as <tt>ScrollableResults</tt>. The
-	 * scrollability of the returned results depends upon JDBC driver
-	 * support for scrollable <tt>ResultSet</tt>s.<br>
+	 * (Re)set the current FlushMode in effect for this query.
 	 *
-	 * @see ScrollableResults
-	 * @see ScrollMode
-	 * @return the result iterator
-	 * @throws HibernateException
+	 * @param flushMode The new FlushMode to use.
+	 *
+	 * @see #getFlushMode()
 	 */
-	public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException;
+	public Query setFlushMode(FlushMode flushMode);
+
 	/**
-	 * Return the query results as a <tt>List</tt>. If the query contains
-	 * multiple results pre row, the results are returned in an instance
-	 * of <tt>Object[]</tt>.
+	 * Obtain the CacheMode in effect for this query.  By default, the query inherits the CacheMode of the Session
+	 * from which is originates.
 	 *
-	 * @return the result list
-	 * @throws HibernateException
+	 * NOTE: The CacheMode here only effects reading/writing of the query cache, not the
+	 * entity/collection caches.
+	 *
+	 * @return The query CacheMode.
+	 *
+	 * @see Session#getCacheMode()
+	 * @see CacheMode
 	 */
-	public List list() throws HibernateException;
+	public CacheMode getCacheMode();
+
 	/**
-	 * Convenience method to return a single instance that matches
-	 * the query, or null if the query returns no results.
+	 * (Re)set the current CacheMode in effect for this query.
 	 *
-	 * @return the single result or <tt>null</tt>
-	 * @throws NonUniqueResultException if there is more than one matching result
+	 * @param cacheMode The new CacheMode to use.
+	 *
+	 * @see #getCacheMode()
 	 */
-	public Object uniqueResult() throws HibernateException;
+	public Query setCacheMode(CacheMode cacheMode);
 
 	/**
-	 * Execute the update or delete statement.
-	 * </p>
-	 * The semantics are compliant with the ejb3 Query.executeUpdate()
-	 * method.
+	 * Are the results of this query eligible for second level query caching?  This is different that second level
+	 * caching of any returned entities and collections.
 	 *
-	 * @return The number of entities updated or deleted.
-	 * @throws HibernateException
+	 * NOTE: the query being "eligible" for caching does not necessarily mean its results will be cached.  Second level
+	 * query caching still has to be enabled on the {@link SessionFactory} for this to happen.  Usually that is
+	 * controlled by the {@code hibernate.cache.use_query_cache} configuration setting.
+	 *
+	 * @return {@code true} if the query results are eligible for caching, {@code false} otherwise.
+	 *
+	 * @see org.hibernate.cfg.AvailableSettings#USE_QUERY_CACHE
 	 */
-	public int executeUpdate() throws HibernateException;
+	public boolean isCacheable();
 
 	/**
-	 * Set the maximum number of rows to retrieve. If not set,
-	 * there is no limit to the number of rows retrieved.
-	 * @param maxResults the maximum number of rows
+	 * Enable/disable second level query (result) caching for this query.
+	 *
+	 * @param cacheable Should the query results be cacheable?
+	 *
+	 * @see #isCacheable
 	 */
-	public Query setMaxResults(int maxResults);
+	public Query setCacheable(boolean cacheable);
+
 	/**
-	 * Set the first row to retrieve. If not set, rows will be
-	 * retrieved beginnning from row <tt>0</tt>.
-	 * @param firstResult a row number, numbered from <tt>0</tt>
+	 * Obtain the name of the second level query cache region in which query results will be stored (if they are
+	 * cached, see the discussion on {@link #isCacheable()} for more information).  {@code null} indicates that the
+	 * default region should be used.
+	 *
+	 * @return The specified cache region name into which query results should be placed; {@code null} indicates
+	 * the default region.
 	 */
-	public Query setFirstResult(int firstResult);
-	
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
+	public Query setCacheRegion(String cacheRegion);
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
+	public Query setTimeout(int timeout);
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
+	public Query setFetchSize(int fetchSize);
+
+	/**
+	 * Obtains the LockOptions in effect for this query.
+	 *
+	 * @return The LockOptions
+	 *
+	 * @see LockOptions
+	 */
+	public LockOptions getLockOptions();
+
+	/**
+	 * Set the lock options for the query.  Specifically only the following are taken into consideration:<ol>
+	 *     <li>{@link LockOptions#getLockMode()}</li>
+	 *     <li>{@link LockOptions#getScope()}</li>
+	 *     <li>{@link LockOptions#getTimeOut()}</li>
+	 * </ol>
+	 * For alias-specific locking, use {@link #setLockMode(String, LockMode)}.
+	 *
+	 * @see #getLockOptions()
+	 */
+	public Query setLockOptions(LockOptions lockOptions);
+
+	/**
+	 * Set the LockMode to use for specific alias (as defined in the query's <tt>FROM</tt> clause).
+	 *
+	 * The alias-specific lock modes specified here are added to the query's internal
+	 * {@link #getLockOptions() LockOptions}.
+	 *
+	 * The effect of these alias-specific LockModes is somewhat dependent on the driver/database in use.  Generally
+	 * speaking, for maximum portability, this method should only be used to mark that the rows corresponding to
+	 * the given alias should be included in pessimistic locking ({@link LockMode#PESSIMISTIC_WRITE}).
+	 *
+	 * @param alias a query alias, or <tt>this</tt> for a collection filter
+	 *
+	 * @see #getLockOptions()
+	 */
+	public Query setLockMode(String alias, LockMode lockMode);
+
+	/**
+	 * Obtain the comment currently associated with this query.  Provided SQL commenting is enabled
+	 * (generally by enabling the {@code hibernate.use_sql_comments} config setting), this comment will also be added
+	 * to the SQL query sent to the database.  Often useful for identifying the source of troublesome queries on the
+	 * database side.
+	 *
+	 * @return The comment.
+	 */
+	public String getComment();
+
+	/**
+	 * Set the comment for this query.
+	 *
+	 * @param comment The human-readable comment
+	 *
+	 * @see #getComment()
+	 */
+	public Query setComment(String comment);
+
 	/**
 	 * Should entities and proxies loaded by this Query be put in read-only mode? If the
 	 * read-only/modifiable setting was not initialized, then the default
 	 * read-only/modifiable setting for the persistence context is returned instead.
 	 * @see Query#setReadOnly(boolean)
 	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
 	 *
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * query that existed in the session before the query was executed.
 	 *
 	 * @return true, entities and proxies loaded by the query will be put in read-only mode
 	 *         false, entities and proxies loaded by the query will be put in modifiable mode
 	 */
 	public boolean isReadOnly();
 
 	/**
 	 * Set the read-only/modifiable mode for entities and proxies
 	 * loaded by this Query. This setting overrides the default setting
 	 * for the persistence context.
 	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
 	 *
 	 * To set the default read-only/modifiable setting used for
 	 * entities and proxies that are loaded into the session:
 	 * @see org.hibernate.engine.spi.PersistenceContext#setDefaultReadOnly(boolean)
 	 * @see org.hibernate.Session#setDefaultReadOnly(boolean)
 	 *
 	 * Read-only entities are not dirty-checked and snapshots of persistent
 	 * state are not maintained. Read-only entities can be modified, but
 	 * changes are not persisted.
 	 *
 	 * When a proxy is initialized, the loaded entity will have the same
 	 * read-only/modifiable setting as the uninitialized
 	 * proxy has, regardless of the session's current setting.
 	 *
 	 * The read-only/modifiable setting has no impact on entities/proxies
 	 * returned by the query that existed in the session before the query was executed.
 	 *
 	 * @param readOnly true, entities and proxies loaded by the query will be put in read-only mode
 	 *                 false, entities and proxies loaded by the query will be put in modifiable mode
 	 */
 	public Query setReadOnly(boolean readOnly);
 
 	/**
-	 * Enable caching of this query result set.
-	 * @param cacheable Should the query results be cacheable?
+	 * Return the Hibernate types of the query result set.
+	 * @return an array of types
 	 */
-	public Query setCacheable(boolean cacheable);
+	public Type[] getReturnTypes() throws HibernateException;
 
 	/**
-	 * Set the name of the cache region.
-	 * @param cacheRegion the name of a query cache region, or <tt>null</tt>
-	 * for the default query cache
+	 * Return the HQL select clause aliases (if any)
+	 * @return an array of aliases as strings
 	 */
-	public Query setCacheRegion(String cacheRegion);
+	public String[] getReturnAliases() throws HibernateException;
 
 	/**
-	 * Set a timeout for the underlying JDBC query.
-	 * @param timeout the timeout in seconds
+	 * Return the names of all named parameters of the query.
+	 * @return the parameter names, in no particular order
 	 */
-	public Query setTimeout(int timeout);
+	public String[] getNamedParameters() throws HibernateException;
+
 	/**
-	 * Set a fetch size for the underlying JDBC query.
-	 * @param fetchSize the fetch size
+	 * Return the query results as an <tt>Iterator</tt>. If the query
+	 * contains multiple results pre row, the results are returned in
+	 * an instance of <tt>Object[]</tt>.<br>
+	 * <br>
+	 * Entities returned as results are initialized on demand. The first
+	 * SQL query returns identifiers only.<br>
+	 *
+	 * @return the result iterator
+	 * @throws HibernateException
 	 */
-	public Query setFetchSize(int fetchSize);
+	public Iterator iterate() throws HibernateException;
 
 	/**
-	 * Set the lock options for the objects idententified by the
-	 * given alias that appears in the <tt>FROM</tt> clause.
+	 * Return the query results as <tt>ScrollableResults</tt>. The
+	 * scrollability of the returned results depends upon JDBC driver
+	 * support for scrollable <tt>ResultSet</tt>s.<br>
+	 *
+	 * @see ScrollableResults
+	 * @return the result iterator
+	 * @throws HibernateException
 	 */
-	public Query setLockOptions(LockOptions lockOptions);
+	public ScrollableResults scroll() throws HibernateException;
 
 	/**
-	 * Set the lockmode for the objects idententified by the
-	 * given alias that appears in the <tt>FROM</tt> clause.
-	 * @param alias a query alias, or <tt>this</tt> for a collection filter
+	 * Return the query results as <tt>ScrollableResults</tt>. The
+	 * scrollability of the returned results depends upon JDBC driver
+	 * support for scrollable <tt>ResultSet</tt>s.<br>
+	 *
+	 * @see ScrollableResults
+	 * @see ScrollMode
+	 * @return the result iterator
+	 * @throws HibernateException
 	 */
-	public Query setLockMode(String alias, LockMode lockMode);
+	public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException;
 
 	/**
-	 * Add a comment to the generated SQL.
-	 * @param comment a human-readable string
+	 * Return the query results as a <tt>List</tt>. If the query contains
+	 * multiple results pre row, the results are returned in an instance
+	 * of <tt>Object[]</tt>.
+	 *
+	 * @return the result list
+	 * @throws HibernateException
 	 */
-	public Query setComment(String comment);
-	
+	public List list() throws HibernateException;
+
 	/**
-	 * Override the current session flush mode, just for
-	 * this query.
-	 * @see org.hibernate.FlushMode
+	 * Convenience method to return a single instance that matches
+	 * the query, or null if the query returns no results.
+	 *
+	 * @return the single result or <tt>null</tt>
+	 * @throws NonUniqueResultException if there is more than one matching result
 	 */
-	public Query setFlushMode(FlushMode flushMode);
+	public Object uniqueResult() throws HibernateException;
 
 	/**
-	 * Override the current session cache mode, just for
-	 * this query.
-	 * @see org.hibernate.CacheMode
+	 * Execute the update or delete statement.
+	 * </p>
+	 * The semantics are compliant with the ejb3 Query.executeUpdate()
+	 * method.
+	 *
+	 * @return The number of entities updated or deleted.
+	 * @throws HibernateException
 	 */
-	public Query setCacheMode(CacheMode cacheMode);
+	public int executeUpdate() throws HibernateException;
 
 	/**
 	 * Bind a value to a JDBC-style query parameter.
 	 * @param position the position of the parameter in the query
 	 * string, numbered from <tt>0</tt>.
 	 * @param val the possibly-null parameter value
 	 * @param type the Hibernate type
 	 */
 	public Query setParameter(int position, Object val, Type type);
+
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
+
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
index 361c37eb54..6cc40ae0c8 100755
--- a/hibernate-core/src/main/java/org/hibernate/SQLQuery.java
+++ b/hibernate-core/src/main/java/org/hibernate/SQLQuery.java
@@ -1,341 +1,368 @@
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
+import java.util.Collection;
+import java.util.List;
+
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
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
 public interface SQLQuery extends Query {
+	/**
+	 * Obtain the list of query spaces (table names) the query is synchronized on.  These spaces affect the process
+	 * of auto-flushing by determining which entities will be processed by auto-flush based on the table to
+	 * which those entities are mapped and which are determined to have pending state changes.
+	 *
+	 * @return The list of query spaces upon which the query is synchronized.
+	 */
+	public Collection<String> getSynchronizedQuerySpaces();
 
 	/**
 	 * Adds a query space (table name) for (a) auto-flush checking and (b) query result cache invalidation checking
 	 *
 	 * @param querySpace The query space to be auto-flushed for this query.
 	 *
 	 * @return this, for method chaining
+	 *
+	 * @see #getSynchronizedQuerySpaces()
 	 */
 	public SQLQuery addSynchronizedQuerySpace(String querySpace);
 
 	/**
 	 * Adds an entity name for (a) auto-flush checking and (b) query result cache invalidation checking.  Same as
 	 * {@link #addSynchronizedQuerySpace} for all tables associated with the given entity.
 	 *
 	 * @param entityName The name of the entity upon whose defined query spaces we should additionally synchronize.
 	 *
 	 * @return this, for method chaining
 	 *
 	 * @throws MappingException Indicates the given name could not be resolved as an entity
+	 *
+	 * @see #getSynchronizedQuerySpaces()
 	 */
 	public SQLQuery addSynchronizedEntityName(String entityName) throws MappingException;
 
 	/**
 	 * Adds an entity for (a) auto-flush checking and (b) query result cache invalidation checking.  Same as
 	 * {@link #addSynchronizedQuerySpace} for all tables associated with the given entity.
 	 *
 	 * @param entityClass The class of the entity upon whose defined query spaces we should additionally synchronize.
 	 *
 	 * @return this, for method chaining
 	 *
 	 * @throws MappingException Indicates the given class could not be resolved as an entity
+	 *
+	 * @see #getSynchronizedQuerySpaces()
 	 */
 	public SQLQuery addSynchronizedEntityClass(Class entityClass) throws MappingException;
 
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
+	 * Is this native-SQL query known to be callable?
+	 *
+	 * @return {@code true} if the query is known to be callable; {@code false} otherwise.
+	 */
+	public boolean isCallable();
+
+	public List<NativeSQLQueryReturn> getQueryReturns();
+
+	/**
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 5dcef8e599..aed5cff60d 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -1,3186 +1,3152 @@
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
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Properties;
 import java.util.StringTokenizer;
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 
 import org.hibernate.CacheMode;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.FlushMode;
 import org.hibernate.MappingException;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
+import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.xml.XmlDocument;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Array;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.Backref;
 import org.hibernate.mapping.Bag;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.Fetchable;
 import org.hibernate.mapping.Filterable;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.IdentifierBag;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.IndexBackref;
 import org.hibernate.mapping.IndexedCollection;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.JoinedSubclass;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.List;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.Map;
 import org.hibernate.mapping.MetaAttribute;
 import org.hibernate.mapping.MetadataSource;
 import org.hibernate.mapping.OneToMany;
 import org.hibernate.mapping.OneToOne;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.PrimitiveArray;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Set;
 import org.hibernate.mapping.SimpleAuxiliaryDatabaseObject;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.SingleTableSubclass;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.TypeDef;
 import org.hibernate.mapping.UnionSubclass;
 import org.hibernate.mapping.UniqueKey;
 import org.hibernate.mapping.Value;
 import org.hibernate.type.DiscriminatorType;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.Type;
 
 /**
  * Walks an XML mapping document and produces the Hibernate configuration-time metamodel (the
  * classes in the <tt>mapping</tt> package)
  *
  * @author Gavin King
  */
 public final class HbmBinder {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, HbmBinder.class.getName());
 
 	/**
 	 * Private constructor to disallow instantiation.
 	 */
 	private HbmBinder() {
 	}
 
 	/**
 	 * The main contract into the hbm.xml-based binder. Performs necessary binding operations
 	 * represented by the given DOM.
 	 *
 	 * @param metadataXml The DOM to be parsed and bound.
 	 * @param mappings Current bind state.
 	 * @param inheritedMetas Any inherited meta-tag information.
 	 * @param entityNames Any state
 	 *
 	 * @throws MappingException
 	 */
 	public static void bindRoot(
 			XmlDocument metadataXml,
 			Mappings mappings,
 			java.util.Map inheritedMetas,
 			java.util.Set<String> entityNames) throws MappingException {
 
 		final Document doc = metadataXml.getDocumentTree();
 		final Element hibernateMappingElement = doc.getRootElement();
 
 		java.util.List<String> names = HbmBinder.getExtendsNeeded( metadataXml, mappings );
 		if ( !names.isEmpty() ) {
 			// classes mentioned in extends not available - so put it in queue
 			Attribute packageAttribute = hibernateMappingElement.attribute( "package" );
 			String packageName = packageAttribute == null ? null : packageAttribute.getValue();
 			for ( String name : names ) {
 				mappings.addToExtendsQueue( new ExtendsQueueEntry( name, packageName, metadataXml, entityNames ) );
 			}
 			return;
 		}
 
 		// get meta's from <hibernate-mapping>
 		inheritedMetas = getMetas( hibernateMappingElement, inheritedMetas, true );
 		extractRootAttributes( hibernateMappingElement, mappings );
 
 		Iterator rootChildren = hibernateMappingElement.elementIterator();
 		while ( rootChildren.hasNext() ) {
 			final Element element = (Element) rootChildren.next();
 			final String elementName = element.getName();
 
 			if ( "filter-def".equals( elementName ) ) {
 				parseFilterDef( element, mappings );
 			}
 			else if ( "fetch-profile".equals( elementName ) ) {
 				parseFetchProfile( element, mappings, null );
 			}
 			else if ( "identifier-generator".equals( elementName ) ) {
 				parseIdentifierGeneratorRegistration( element, mappings );
 			}
 			else if ( "typedef".equals( elementName ) ) {
 				bindTypeDef( element, mappings );
 			}
 			else if ( "class".equals( elementName ) ) {
 				RootClass rootclass = new RootClass();
 				bindRootClass( element, rootclass, mappings, inheritedMetas );
 				mappings.addClass( rootclass );
 			}
 			else if ( "subclass".equals( elementName ) ) {
 				PersistentClass superModel = getSuperclass( mappings, element );
 				handleSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( "joined-subclass".equals( elementName ) ) {
 				PersistentClass superModel = getSuperclass( mappings, element );
 				handleJoinedSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( "union-subclass".equals( elementName ) ) {
 				PersistentClass superModel = getSuperclass( mappings, element );
 				handleUnionSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( "query".equals( elementName ) ) {
 				bindNamedQuery( element, null, mappings );
 			}
 			else if ( "sql-query".equals( elementName ) ) {
 				bindNamedSQLQuery( element, null, mappings );
 			}
 			else if ( "resultset".equals( elementName ) ) {
 				bindResultSetMappingDefinition( element, null, mappings );
 			}
 			else if ( "import".equals( elementName ) ) {
 				bindImport( element, mappings );
 			}
 			else if ( "database-object".equals( elementName ) ) {
 				bindAuxiliaryDatabaseObject( element, mappings );
 			}
 		}
 	}
 
 	private static void parseIdentifierGeneratorRegistration(Element element, Mappings mappings) {
 		String strategy = element.attributeValue( "name" );
 		if ( StringHelper.isEmpty( strategy ) ) {
 			throw new MappingException( "'name' attribute expected for identifier-generator elements" );
 		}
 		String generatorClassName = element.attributeValue( "class" );
 		if ( StringHelper.isEmpty( generatorClassName ) ) {
 			throw new MappingException( "'class' attribute expected for identifier-generator [identifier-generator@name=" + strategy + "]" );
 		}
 
 		try {
 			Class generatorClass = ReflectHelper.classForName( generatorClassName );
 			mappings.getIdentifierGeneratorFactory().register( strategy, generatorClass );
 		}
 		catch ( ClassNotFoundException e ) {
 			throw new MappingException( "Unable to locate identifier-generator class [name=" + strategy + ", class=" + generatorClassName + "]" );
 		}
 
 	}
 
 	private static void bindImport(Element importNode, Mappings mappings) {
 		String className = getClassName( importNode.attribute( "class" ), mappings );
 		Attribute renameNode = importNode.attribute( "rename" );
 		String rename = ( renameNode == null ) ?
 						StringHelper.unqualify( className ) :
 						renameNode.getValue();
 		LOG.debugf( "Import: %s -> %s", rename, className );
 		mappings.addImport( className, rename );
 	}
 
 	private static void bindTypeDef(Element typedefNode, Mappings mappings) {
 		String typeClass = typedefNode.attributeValue( "class" );
 		String typeName = typedefNode.attributeValue( "name" );
 		Iterator paramIter = typedefNode.elementIterator( "param" );
 		Properties parameters = new Properties();
 		while ( paramIter.hasNext() ) {
 			Element param = (Element) paramIter.next();
 			parameters.setProperty( param.attributeValue( "name" ), param.getTextTrim() );
 		}
 		mappings.addTypeDef( typeName, typeClass, parameters );
 	}
 
 	private static void bindAuxiliaryDatabaseObject(Element auxDbObjectNode, Mappings mappings) {
 		AuxiliaryDatabaseObject auxDbObject = null;
 		Element definitionNode = auxDbObjectNode.element( "definition" );
 		if ( definitionNode != null ) {
 			try {
 				auxDbObject = ( AuxiliaryDatabaseObject ) ReflectHelper
 						.classForName( definitionNode.attributeValue( "class" ) )
 						.newInstance();
 			}
 			catch( ClassNotFoundException e ) {
 				throw new MappingException(
 						"could not locate custom database object class [" +
 						definitionNode.attributeValue( "class" ) + "]"
 					);
 			}
 			catch( Throwable t ) {
 				throw new MappingException(
 						"could not instantiate custom database object class [" +
 						definitionNode.attributeValue( "class" ) + "]"
 					);
 			}
 		}
 		else {
 			auxDbObject = new SimpleAuxiliaryDatabaseObject(
 					auxDbObjectNode.elementTextTrim( "create" ),
 					auxDbObjectNode.elementTextTrim( "drop" )
 				);
 		}
 
 		Iterator dialectScopings = auxDbObjectNode.elementIterator( "dialect-scope" );
 		while ( dialectScopings.hasNext() ) {
 			Element dialectScoping = ( Element ) dialectScopings.next();
 			auxDbObject.addDialectScope( dialectScoping.attributeValue( "name" ) );
 		}
 
 		mappings.addAuxiliaryDatabaseObject( auxDbObject );
 	}
 
 	private static void extractRootAttributes(Element hmNode, Mappings mappings) {
 		Attribute schemaNode = hmNode.attribute( "schema" );
 		mappings.setSchemaName( ( schemaNode == null ) ? null : schemaNode.getValue() );
 
 		Attribute catalogNode = hmNode.attribute( "catalog" );
 		mappings.setCatalogName( ( catalogNode == null ) ? null : catalogNode.getValue() );
 
 		Attribute dcNode = hmNode.attribute( "default-cascade" );
 		mappings.setDefaultCascade( ( dcNode == null ) ? "none" : dcNode.getValue() );
 
 		Attribute daNode = hmNode.attribute( "default-access" );
 		mappings.setDefaultAccess( ( daNode == null ) ? "property" : daNode.getValue() );
 
 		Attribute dlNode = hmNode.attribute( "default-lazy" );
 		mappings.setDefaultLazy( dlNode == null || dlNode.getValue().equals( "true" ) );
 
 		Attribute aiNode = hmNode.attribute( "auto-import" );
 		mappings.setAutoImport( ( aiNode == null ) || "true".equals( aiNode.getValue() ) );
 
 		Attribute packNode = hmNode.attribute( "package" );
 		if ( packNode != null ) mappings.setDefaultPackage( packNode.getValue() );
 	}
 
 	/**
 	 * Responsible for performing the bind operation related to an &lt;class/&gt; mapping element.
 	 *
 	 * @param node The DOM Element for the &lt;class/&gt; element.
 	 * @param rootClass The mapping instance to which to bind the information.
 	 * @param mappings The current bind state.
 	 * @param inheritedMetas Any inherited meta-tag information.
 	 * @throws MappingException
 	 */
 	public static void bindRootClass(Element node, RootClass rootClass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		bindClass( node, rootClass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <class>
 		bindRootPersistentClassCommonValues( node, inheritedMetas, mappings, rootClass );
 	}
 
 	private static void bindRootPersistentClassCommonValues(Element node,
 			java.util.Map inheritedMetas, Mappings mappings, RootClass entity)
 			throws MappingException {
 
 		// DB-OBJECTNAME
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table table = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( entity, node, schema, catalog, null, mappings ),
 				getSubselect( node ),
 		        entity.isAbstract() != null && entity.isAbstract()
 			);
 		entity.setTable( table );
 		bindComment(table, node);
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping class: %s -> %s", entity.getEntityName(), entity.getTable().getName() );
 		}
 
 		// MUTABLE
 		Attribute mutableNode = node.attribute( "mutable" );
 		entity.setMutable( ( mutableNode == null ) || mutableNode.getValue().equals( "true" ) );
 
 		// WHERE
 		Attribute whereNode = node.attribute( "where" );
 		if ( whereNode != null ) entity.setWhere( whereNode.getValue() );
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) table.addCheckConstraint( chNode.getValue() );
 
 		// POLYMORPHISM
 		Attribute polyNode = node.attribute( "polymorphism" );
 		entity.setExplicitPolymorphism( ( polyNode != null )
 			&& polyNode.getValue().equals( "explicit" ) );
 
 		// ROW ID
 		Attribute rowidNode = node.attribute( "rowid" );
 		if ( rowidNode != null ) table.setRowId( rowidNode.getValue() );
 
 		Iterator subnodes = node.elementIterator();
 		while ( subnodes.hasNext() ) {
 
 			Element subnode = (Element) subnodes.next();
 			String name = subnode.getName();
 
 			if ( "id".equals( name ) ) {
 				// ID
 				bindSimpleId( subnode, entity, mappings, inheritedMetas );
 			}
 			else if ( "composite-id".equals( name ) ) {
 				// COMPOSITE-ID
 				bindCompositeId( subnode, entity, mappings, inheritedMetas );
 			}
 			else if ( "version".equals( name ) || "timestamp".equals( name ) ) {
 				// VERSION / TIMESTAMP
 				bindVersioningProperty( table, subnode, mappings, name, entity, inheritedMetas );
 			}
 			else if ( "discriminator".equals( name ) ) {
 				// DISCRIMINATOR
 				bindDiscriminatorProperty( table, entity, subnode, mappings );
 			}
 			else if ( "cache".equals( name ) ) {
 				entity.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
 				entity.setCacheRegionName( subnode.attributeValue( "region" ) );
 				entity.setLazyPropertiesCacheable( !"non-lazy".equals( subnode.attributeValue( "include" ) ) );
 			}
 
 		}
 
 		// Primary key constraint
 		entity.createPrimaryKey();
 
 		createClassProperties( node, entity, mappings, inheritedMetas );
 	}
 
 	private static void bindSimpleId(Element idNode, RootClass entity, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		String propertyName = idNode.attributeValue( "name" );
 
 		SimpleValue id = new SimpleValue( mappings, entity.getTable() );
 		entity.setIdentifier( id );
 
 		// if ( propertyName == null || entity.getPojoRepresentation() == null ) {
 		// bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 		// if ( !id.isTypeSpecified() ) {
 		// throw new MappingException( "must specify an identifier type: " + entity.getEntityName()
 		// );
 		// }
 		// }
 		// else {
 		// bindSimpleValue( idNode, id, false, propertyName, mappings );
 		// PojoRepresentation pojo = entity.getPojoRepresentation();
 		// id.setTypeUsingReflection( pojo.getClassName(), propertyName );
 		//
 		// Property prop = new Property();
 		// prop.setValue( id );
 		// bindProperty( idNode, prop, mappings, inheritedMetas );
 		// entity.setIdentifierProperty( prop );
 		// }
 
 		if ( propertyName == null ) {
 			bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 		}
 		else {
 			bindSimpleValue( idNode, id, false, propertyName, mappings );
 		}
 
 		if ( propertyName == null || !entity.hasPojoRepresentation() ) {
 			if ( !id.isTypeSpecified() ) {
 				throw new MappingException( "must specify an identifier type: "
 					+ entity.getEntityName() );
 			}
 		}
 		else {
 			id.setTypeUsingReflection( entity.getClassName(), propertyName );
 		}
 
 		if ( propertyName != null ) {
 			Property prop = new Property();
 			prop.setValue( id );
 			bindProperty( idNode, prop, mappings, inheritedMetas );
 			entity.setIdentifierProperty( prop );
 			entity.setDeclaredIdentifierProperty( prop );
 		}
 
 		// TODO:
 		/*
 		 * if ( id.getHibernateType().getReturnedClass().isArray() ) throw new MappingException(
 		 * "illegal use of an array as an identifier (arrays don't reimplement equals)" );
 		 */
 		makeIdentifier( idNode, id, mappings );
 	}
 
 	private static void bindCompositeId(Element idNode, RootClass entity, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		String propertyName = idNode.attributeValue( "name" );
 		Component id = new Component( mappings, entity );
 		entity.setIdentifier( id );
 		bindCompositeId( idNode, id, entity, propertyName, mappings, inheritedMetas );
 		if ( propertyName == null ) {
 			entity.setEmbeddedIdentifier( id.isEmbedded() );
 			if ( id.isEmbedded() ) {
 				// todo : what is the implication of this?
 				id.setDynamic( !entity.hasPojoRepresentation() );
 				/*
 				 * Property prop = new Property(); prop.setName("id");
 				 * prop.setPropertyAccessorName("embedded"); prop.setValue(id);
 				 * entity.setIdentifierProperty(prop);
 				 */
 			}
 		}
 		else {
 			Property prop = new Property();
 			prop.setValue( id );
 			bindProperty( idNode, prop, mappings, inheritedMetas );
 			entity.setIdentifierProperty( prop );
 			entity.setDeclaredIdentifierProperty( prop );
 		}
 
 		makeIdentifier( idNode, id, mappings );
 
 	}
 
 	private static void bindVersioningProperty(Table table, Element subnode, Mappings mappings,
 			String name, RootClass entity, java.util.Map inheritedMetas) {
 
 		String propertyName = subnode.attributeValue( "name" );
 		SimpleValue val = new SimpleValue( mappings, table );
 		bindSimpleValue( subnode, val, false, propertyName, mappings );
 		if ( !val.isTypeSpecified() ) {
 			// this is either a <version/> tag with no type attribute,
 			// or a <timestamp/> tag
 			if ( "version".equals( name ) ) {
 				val.setTypeName( "integer" );
 			}
 			else {
 				if ( "db".equals( subnode.attributeValue( "source" ) ) ) {
 					val.setTypeName( "dbtimestamp" );
 				}
 				else {
 					val.setTypeName( "timestamp" );
 				}
 			}
 		}
 		Property prop = new Property();
 		prop.setValue( val );
 		bindProperty( subnode, prop, mappings, inheritedMetas );
 		// for version properties marked as being generated, make sure they are "always"
 		// generated; aka, "insert" is invalid; this is dis-allowed by the DTD,
 		// but just to make sure...
 		if ( prop.getGeneration() == PropertyGeneration.INSERT ) {
 			throw new MappingException( "'generated' attribute cannot be 'insert' for versioning property" );
 		}
 		makeVersion( subnode, val );
 		entity.setVersion( prop );
 		entity.addProperty( prop );
 	}
 
 	private static void bindDiscriminatorProperty(Table table, RootClass entity, Element subnode,
 			Mappings mappings) {
 		SimpleValue discrim = new SimpleValue( mappings, table );
 		entity.setDiscriminator( discrim );
 		bindSimpleValue(
 				subnode,
 				discrim,
 				false,
 				RootClass.DEFAULT_DISCRIMINATOR_COLUMN_NAME,
 				mappings
 			);
 		if ( !discrim.isTypeSpecified() ) {
 			discrim.setTypeName( "string" );
 			// ( (Column) discrim.getColumnIterator().next() ).setType(type);
 		}
 		entity.setPolymorphic( true );
 		final String explicitForceValue = subnode.attributeValue( "force" );
 		boolean forceDiscriminatorInSelects = explicitForceValue == null
 				? mappings.forceDiscriminatorInSelectsByDefault()
 				: "true".equals( explicitForceValue );
 		entity.setForceDiscriminator( forceDiscriminatorInSelects );
 		if ( "false".equals( subnode.attributeValue( "insert" ) ) ) {
 			entity.setDiscriminatorInsertable( false );
 		}
 	}
 
 	public static void bindClass(Element node, PersistentClass persistentClass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		// transfer an explicitly defined entity name
 		// handle the lazy attribute
 		Attribute lazyNode = node.attribute( "lazy" );
 		boolean lazy = lazyNode == null ?
 				mappings.isDefaultLazy() :
 				"true".equals( lazyNode.getValue() );
 		// go ahead and set the lazy here, since pojo.proxy can override it.
 		persistentClass.setLazy( lazy );
 
 		String entityName = node.attributeValue( "entity-name" );
 		if ( entityName == null ) entityName = getClassName( node.attribute("name"), mappings );
 		if ( entityName==null ) {
 			throw new MappingException( "Unable to determine entity name" );
 		}
 		persistentClass.setEntityName( entityName );
 		persistentClass.setJpaEntityName( StringHelper.unqualify( entityName ) );
 
 		bindPojoRepresentation( node, persistentClass, mappings, inheritedMetas );
 		bindDom4jRepresentation( node, persistentClass, mappings, inheritedMetas );
 		bindMapRepresentation( node, persistentClass, mappings, inheritedMetas );
 
 		Iterator itr = node.elementIterator( "fetch-profile" );
 		while ( itr.hasNext() ) {
 			final Element profileElement = ( Element ) itr.next();
 			parseFetchProfile( profileElement, mappings, entityName );
 		}
 
 		bindPersistentClassCommonValues( node, persistentClass, mappings, inheritedMetas );
 	}
 
 	private static void bindPojoRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map metaTags) {
 
 		String className = getClassName( node.attribute( "name" ), mappings );
 		String proxyName = getClassName( node.attribute( "proxy" ), mappings );
 
 		entity.setClassName( className );
 
 		if ( proxyName != null ) {
 			entity.setProxyInterfaceName( proxyName );
 			entity.setLazy( true );
 		}
 		else if ( entity.isLazy() ) {
 			entity.setProxyInterfaceName( className );
 		}
 
 		Element tuplizer = locateTuplizerDefinition( node, EntityMode.POJO );
 		if ( tuplizer != null ) {
 			entity.addTuplizer( EntityMode.POJO, tuplizer.attributeValue( "class" ) );
 		}
 	}
 
 	private static void bindDom4jRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) {
 		String nodeName = node.attributeValue( "node" );
 		if (nodeName==null) nodeName = StringHelper.unqualify( entity.getEntityName() );
 		entity.setNodeName(nodeName);
 
 //		Element tuplizer = locateTuplizerDefinition( node, EntityMode.DOM4J );
 //		if ( tuplizer != null ) {
 //			entity.addTuplizer( EntityMode.DOM4J, tuplizer.attributeValue( "class" ) );
 //		}
 	}
 
 	private static void bindMapRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) {
 		Element tuplizer = locateTuplizerDefinition( node, EntityMode.MAP );
 		if ( tuplizer != null ) {
 			entity.addTuplizer( EntityMode.MAP, tuplizer.attributeValue( "class" ) );
 		}
 	}
 
 	/**
 	 * Locate any explicit tuplizer definition in the metadata, for the given entity-mode.
 	 *
 	 * @param container The containing element (representing the entity/component)
 	 * @param entityMode The entity-mode for which to locate the tuplizer element
 	 * @return The tuplizer element, or null.
 	 */
 	private static Element locateTuplizerDefinition(Element container, EntityMode entityMode) {
 		Iterator itr = container.elements( "tuplizer" ).iterator();
 		while( itr.hasNext() ) {
 			final Element tuplizerElem = ( Element ) itr.next();
 			if ( entityMode.toString().equals( tuplizerElem.attributeValue( "entity-mode") ) ) {
 				return tuplizerElem;
 			}
 		}
 		return null;
 	}
 
 	private static void bindPersistentClassCommonValues(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 		// DISCRIMINATOR
 		Attribute discriminatorNode = node.attribute( "discriminator-value" );
 		entity.setDiscriminatorValue( ( discriminatorNode == null )
 			? entity.getEntityName()
 			: discriminatorNode.getValue() );
 
 		// DYNAMIC UPDATE
 		Attribute dynamicNode = node.attribute( "dynamic-update" );
 		entity.setDynamicUpdate(
 				dynamicNode != null && "true".equals( dynamicNode.getValue() )
 		);
 
 		// DYNAMIC INSERT
 		Attribute insertNode = node.attribute( "dynamic-insert" );
 		entity.setDynamicInsert(
 				insertNode != null && "true".equals( insertNode.getValue() )
 		);
 
 		// IMPORT
 		mappings.addImport( entity.getEntityName(), entity.getEntityName() );
 		if ( mappings.isAutoImport() && entity.getEntityName().indexOf( '.' ) > 0 ) {
 			mappings.addImport(
 					entity.getEntityName(),
 					StringHelper.unqualify( entity.getEntityName() )
 				);
 		}
 
 		// BATCH SIZE
 		Attribute batchNode = node.attribute( "batch-size" );
 		if ( batchNode != null ) entity.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
 
 		// SELECT BEFORE UPDATE
 		Attribute sbuNode = node.attribute( "select-before-update" );
 		if ( sbuNode != null ) entity.setSelectBeforeUpdate( "true".equals( sbuNode.getValue() ) );
 
 		// OPTIMISTIC LOCK MODE
 		Attribute olNode = node.attribute( "optimistic-lock" );
 		entity.setOptimisticLockMode( getOptimisticLockMode( olNode ) );
 
 		entity.setMetaAttributes( getMetas( node, inheritedMetas ) );
 
 		// PERSISTER
 		Attribute persisterNode = node.attribute( "persister" );
 		if ( persisterNode != null ) {
 			try {
 				entity.setEntityPersisterClass( ReflectHelper.classForName(
 						persisterNode
 								.getValue()
 				) );
 			}
 			catch (ClassNotFoundException cnfe) {
 				throw new MappingException( "Could not find persister class: "
 					+ persisterNode.getValue() );
 			}
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, entity );
 
 		Iterator tables = node.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			entity.addSynchronizedTable( ( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 
 		Attribute abstractNode = node.attribute( "abstract" );
 		Boolean isAbstract = abstractNode == null
 				? null
 		        : "true".equals( abstractNode.getValue() )
 						? Boolean.TRUE
 	                    : "false".equals( abstractNode.getValue() )
 								? Boolean.FALSE
 	                            : null;
 		entity.setAbstract( isAbstract );
 	}
 
 	private static void handleCustomSQL(Element node, PersistentClass model)
 			throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "loader" );
 		if ( element != null ) {
 			model.setLoaderName( element.attributeValue( "query-ref" ) );
 		}
 	}
 
 	private static void handleCustomSQL(Element node, Join model) throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 	}
 
 	private static void handleCustomSQL(Element node, Collection model) throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete-all" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLDeleteAll( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 	}
 
 	private static boolean isCallable(Element e) throws MappingException {
 		return isCallable( e, true );
 	}
 
 	private static boolean isCallable(Element element, boolean supportsCallable)
 			throws MappingException {
 		Attribute attrib = element.attribute( "callable" );
 		if ( attrib != null && "true".equals( attrib.getValue() ) ) {
 			if ( !supportsCallable ) {
 				throw new MappingException( "callable attribute not supported yet!" );
 			}
 			return true;
 		}
 		return false;
 	}
 
 	private static ExecuteUpdateResultCheckStyle getResultCheckStyle(Element element, boolean callable) throws MappingException {
 		Attribute attr = element.attribute( "check" );
 		if ( attr == null ) {
 			// use COUNT as the default.  This mimics the old behavior, although
 			// NONE might be a better option moving forward in the case of callable
 			return ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		return ExecuteUpdateResultCheckStyle.fromExternalName( attr.getValue() );
 	}
 
 	public static void bindUnionSubclass(Element node, UnionSubclass unionSubclass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, unionSubclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <subclass>
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table denormalizedSuperTable = unionSubclass.getSuperclass().getTable();
 		Table mytable = mappings.addDenormalizedTable(
 				schema,
 				catalog,
 				getClassTableName(unionSubclass, node, schema, catalog, denormalizedSuperTable, mappings ),
 		        unionSubclass.isAbstract() != null && unionSubclass.isAbstract(),
 				getSubselect( node ),
 				denormalizedSuperTable
 			);
 		unionSubclass.setTable( mytable );
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping union-subclass: %s -> %s", unionSubclass.getEntityName(), unionSubclass.getTable().getName() );
 		}
 
 		createClassProperties( node, unionSubclass, mappings, inheritedMetas );
 
 	}
 
 	public static void bindSubclass(Element node, Subclass subclass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, subclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <subclass>
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping subclass: %s -> %s", subclass.getEntityName(), subclass.getTable().getName() );
 		}
 
 		// properties
 		createClassProperties( node, subclass, mappings, inheritedMetas );
 	}
 
 	private static String getClassTableName(
 			PersistentClass model,
 			Element node,
 			String schema,
 			String catalog,
 			Table denormalizedSuperTable,
 			Mappings mappings) {
 		Attribute tableNameNode = node.attribute( "table" );
 		String logicalTableName;
 		String physicalTableName;
 		if ( tableNameNode == null ) {
 			logicalTableName = StringHelper.unqualify( model.getEntityName() );
 			physicalTableName = mappings.getNamingStrategy().classToTableName( model.getEntityName() );
 		}
 		else {
 			logicalTableName = tableNameNode.getValue();
 			physicalTableName = mappings.getNamingStrategy().tableName( logicalTableName );
 		}
 		mappings.addTableBinding( schema, catalog, logicalTableName, physicalTableName, denormalizedSuperTable );
 		return physicalTableName;
 	}
 
 	public static void bindJoinedSubclass(Element node, JoinedSubclass joinedSubclass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, joinedSubclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from
 																	// <joined-subclass>
 
 		// joined subclasses
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table mytable = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( joinedSubclass, node, schema, catalog, null, mappings ),
 				getSubselect( node ),
 				false
 			);
 		joinedSubclass.setTable( mytable );
 		bindComment(mytable, node);
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping joined-subclass: %s -> %s", joinedSubclass.getEntityName(), joinedSubclass.getTable().getName() );
 		}
 
 		// KEY
 		Element keyNode = node.element( "key" );
 		SimpleValue key = new DependantValue( mappings, mytable, joinedSubclass.getIdentifier() );
 		joinedSubclass.setKey( key );
 		key.setCascadeDeleteEnabled( "cascade".equals( keyNode.attributeValue( "on-delete" ) ) );
 		bindSimpleValue( keyNode, key, false, joinedSubclass.getEntityName(), mappings );
 
 		// model.getKey().setType( new Type( model.getIdentifier() ) );
 		joinedSubclass.createPrimaryKey();
 		joinedSubclass.createForeignKey();
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) mytable.addCheckConstraint( chNode.getValue() );
 
 		// properties
 		createClassProperties( node, joinedSubclass, mappings, inheritedMetas );
 
 	}
 
 	private static void bindJoin(Element node, Join join, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		PersistentClass persistentClass = join.getPersistentClass();
 		String path = persistentClass.getEntityName();
 
 		// TABLENAME
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 		Table primaryTable = persistentClass.getTable();
 		Table table = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( persistentClass, node, schema, catalog, primaryTable, mappings ),
 				getSubselect( node ),
 				false
 			);
 		join.setTable( table );
 		bindComment(table, node);
 
 		Attribute fetchNode = node.attribute( "fetch" );
 		if ( fetchNode != null ) {
 			join.setSequentialSelect( "select".equals( fetchNode.getValue() ) );
 		}
 
 		Attribute invNode = node.attribute( "inverse" );
 		if ( invNode != null ) {
 			join.setInverse( "true".equals( invNode.getValue() ) );
 		}
 
 		Attribute nullNode = node.attribute( "optional" );
 		if ( nullNode != null ) {
 			join.setOptional( "true".equals( nullNode.getValue() ) );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping class join: %s -> %s", persistentClass.getEntityName(), join.getTable().getName() );
 		}
 
 		// KEY
 		Element keyNode = node.element( "key" );
 		SimpleValue key = new DependantValue( mappings, table, persistentClass.getIdentifier() );
 		join.setKey( key );
 		key.setCascadeDeleteEnabled( "cascade".equals( keyNode.attributeValue( "on-delete" ) ) );
 		bindSimpleValue( keyNode, key, false, persistentClass.getEntityName(), mappings );
 
 		// join.getKey().setType( new Type( lazz.getIdentifier() ) );
 		join.createPrimaryKey();
 		join.createForeignKey();
 
 		// PROPERTIES
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = subnode.attributeValue( "name" );
 
 			Value value = null;
 			if ( "many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, table );
 				bindManyToOne( subnode, (ManyToOne) value, propertyName, true, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, table );
 				bindAny( subnode, (Any) value, true, mappings );
 			}
 			else if ( "property".equals( name ) ) {
 				value = new SimpleValue( mappings, table );
 				bindSimpleValue( subnode, (SimpleValue) value, true, propertyName, mappings );
 			}
 			else if ( "component".equals( name ) || "dynamic-component".equals( name ) ) {
 				String subpath = StringHelper.qualify( path, propertyName );
 				value = new Component( mappings, join );
 				bindComponent(
 						subnode,
 						(Component) value,
 						join.getPersistentClass().getClassName(),
 						propertyName,
 						subpath,
 						true,
 						false,
 						mappings,
 						inheritedMetas,
 						false
 					);
 			}
 
 			if ( value != null ) {
 				Property prop = createProperty( value, propertyName, persistentClass
 					.getEntityName(), subnode, mappings, inheritedMetas );
 				prop.setOptional( join.isOptional() );
 				join.addProperty( prop );
 			}
 
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, join );
 
 	}
 
 	public static void bindColumns(final Element node, final SimpleValue simpleValue,
 			final boolean isNullable, final boolean autoColumn, final String propertyPath,
 			final Mappings mappings) throws MappingException {
 
 		Table table = simpleValue.getTable();
 
 		// COLUMN(S)
 		Attribute columnAttribute = node.attribute( "column" );
 		if ( columnAttribute == null ) {
 			Iterator itr = node.elementIterator();
 			int count = 0;
 			while ( itr.hasNext() ) {
 				Element columnElement = (Element) itr.next();
 				if ( columnElement.getName().equals( "column" ) ) {
 					Column column = new Column();
 					column.setValue( simpleValue );
 					column.setTypeIndex( count++ );
 					bindColumn( columnElement, column, isNullable );
 					final String columnName = columnElement.attributeValue( "name" );
 					String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
 							columnName, propertyPath
 					);
 					column.setName( mappings.getNamingStrategy().columnName(
 						columnName ) );
 					if ( table != null ) {
 						table.addColumn( column ); // table=null -> an association
 						                           // - fill it in later
 						//TODO fill in the mappings for table == null
 						mappings.addColumnBinding( logicalColumnName, column, table );
 					}
 
 
 					simpleValue.addColumn( column );
 					// column index
 					bindIndex( columnElement.attribute( "index" ), table, column, mappings );
 					bindIndex( node.attribute( "index" ), table, column, mappings );
 					//column unique-key
 					bindUniqueKey( columnElement.attribute( "unique-key" ), table, column, mappings );
 					bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 				}
 				else if ( columnElement.getName().equals( "formula" ) ) {
 					Formula formula = new Formula();
 					formula.setFormula( columnElement.getText() );
 					simpleValue.addFormula( formula );
 				}
 			}
 
 			// todo : another GoodThing would be to go back after all parsing and see if all the columns
 			// (and no formulas) are contained in a defined unique key that only contains these columns.
 			// That too would mark this as a logical one-to-one
 			final Attribute uniqueAttribute = node.attribute( "unique" );
 			if ( uniqueAttribute != null
 					&& "true".equals( uniqueAttribute.getValue() )
 					&& ManyToOne.class.isInstance( simpleValue ) ) {
 				( (ManyToOne) simpleValue ).markAsLogicalOneToOne();
 			}
 		}
 		else {
 			if ( node.elementIterator( "column" ).hasNext() ) {
 				throw new MappingException(
 					"column attribute may not be used together with <column> subelement" );
 			}
 			if ( node.elementIterator( "formula" ).hasNext() ) {
 				throw new MappingException(
 					"column attribute may not be used together with <formula> subelement" );
 			}
 
 			Column column = new Column();
 			column.setValue( simpleValue );
 			bindColumn( node, column, isNullable );
 			if ( column.isUnique() && ManyToOne.class.isInstance( simpleValue ) ) {
 				( (ManyToOne) simpleValue ).markAsLogicalOneToOne();
 			}
 			final String columnName = columnAttribute.getValue();
 			String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
 					columnName, propertyPath
 			);
 			column.setName( mappings.getNamingStrategy().columnName( columnName ) );
 			if ( table != null ) {
 				table.addColumn( column ); // table=null -> an association - fill
 				                           // it in later
 				//TODO fill in the mappings for table == null
 				mappings.addColumnBinding( logicalColumnName, column, table );
 			}
 			simpleValue.addColumn( column );
 			bindIndex( node.attribute( "index" ), table, column, mappings );
 			bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 		}
 
 		if ( autoColumn && simpleValue.getColumnSpan() == 0 ) {
 			Column column = new Column();
 			column.setValue( simpleValue );
 			bindColumn( node, column, isNullable );
 			column.setName( mappings.getNamingStrategy().propertyToColumnName( propertyPath ) );
 			String logicalName = mappings.getNamingStrategy().logicalColumnName( null, propertyPath );
 			mappings.addColumnBinding( logicalName, column, table );
 			/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
 			 * slightly higer level in the stack (to get all the information we need)
 			 * Right now HbmMetadataSourceProcessorImpl does not support the
 			 */
 			simpleValue.getTable().addColumn( column );
 			simpleValue.addColumn( column );
 			bindIndex( node.attribute( "index" ), table, column, mappings );
 			bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 		}
 
 	}
 
 	private static void bindIndex(Attribute indexAttribute, Table table, Column column, Mappings mappings) {
 		if ( indexAttribute != null && table != null ) {
 			StringTokenizer tokens = new StringTokenizer( indexAttribute.getValue(), ", " );
 			while ( tokens.hasMoreTokens() ) {
 				table.getOrCreateIndex( tokens.nextToken() ).addColumn( column );
 			}
 		}
 	}
 
 	private static void bindUniqueKey(Attribute uniqueKeyAttribute, Table table, Column column, Mappings mappings) {
 		if ( uniqueKeyAttribute != null && table != null ) {
 			StringTokenizer tokens = new StringTokenizer( uniqueKeyAttribute.getValue(), ", " );
 			while ( tokens.hasMoreTokens() ) {
 				table.getOrCreateUniqueKey( tokens.nextToken() ).addColumn( column );
 			}
 		}
 	}
 
 	// automatically makes a column with the default name if none is specifed by XML
 	public static void bindSimpleValue(Element node, SimpleValue simpleValue, boolean isNullable,
 			String path, Mappings mappings) throws MappingException {
 		bindSimpleValueType( node, simpleValue, mappings );
 
 		bindColumnsOrFormula( node, simpleValue, path, isNullable, mappings );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) simpleValue.setForeignKeyName( fkNode.getValue() );
 	}
 
 	private static void bindSimpleValueType(Element node, SimpleValue simpleValue, Mappings mappings)
 			throws MappingException {
 		String typeName = null;
 
 		Properties parameters = new Properties();
 
 		Attribute typeNode = node.attribute( "type" );
         if ( typeNode == null ) {
             typeNode = node.attribute( "id-type" ); // for an any
         }
         else {
             typeName = typeNode.getValue();
         }
 
 		Element typeChild = node.element( "type" );
 		if ( typeName == null && typeChild != null ) {
 			typeName = typeChild.attribute( "name" ).getValue();
 			Iterator typeParameters = typeChild.elementIterator( "param" );
 
 			while ( typeParameters.hasNext() ) {
 				Element paramElement = (Element) typeParameters.next();
 				parameters.setProperty(
 						paramElement.attributeValue( "name" ),
 						paramElement.getTextTrim()
 					);
 			}
 		}
 
 		TypeDef typeDef = mappings.getTypeDef( typeName );
 		if ( typeDef != null ) {
 			typeName = typeDef.getTypeClass();
 			// parameters on the property mapping should
 			// override parameters in the typedef
 			Properties allParameters = new Properties();
 			allParameters.putAll( typeDef.getParameters() );
 			allParameters.putAll( parameters );
 			parameters = allParameters;
 		}
 
 		if ( !parameters.isEmpty() ) simpleValue.setTypeParameters( parameters );
 
 		if ( typeName != null ) simpleValue.setTypeName( typeName );
 	}
 
 	public static void bindProperty(
 			Element node,
 	        Property property,
 	        Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		String propName = node.attributeValue( "name" );
 		property.setName( propName );
 		String nodeName = node.attributeValue( "node" );
 		if (nodeName==null) nodeName = propName;
 		property.setNodeName( nodeName );
 
 		// TODO:
 		//Type type = model.getValue().getType();
 		//if (type==null) throw new MappingException(
 		//"Could not determine a property type for: " + model.getName() );
 
 		Attribute accessNode = node.attribute( "access" );
 		if ( accessNode != null ) {
 			property.setPropertyAccessorName( accessNode.getValue() );
 		}
 		else if ( node.getName().equals( "properties" ) ) {
 			property.setPropertyAccessorName( "embedded" );
 		}
 		else {
 			property.setPropertyAccessorName( mappings.getDefaultAccess() );
 		}
 
 		Attribute cascadeNode = node.attribute( "cascade" );
 		property.setCascade( cascadeNode == null ? mappings.getDefaultCascade() : cascadeNode
 			.getValue() );
 
 		Attribute updateNode = node.attribute( "update" );
 		property.setUpdateable( updateNode == null || "true".equals( updateNode.getValue() ) );
 
 		Attribute insertNode = node.attribute( "insert" );
 		property.setInsertable( insertNode == null || "true".equals( insertNode.getValue() ) );
 
 		Attribute lockNode = node.attribute( "optimistic-lock" );
 		property.setOptimisticLocked( lockNode == null || "true".equals( lockNode.getValue() ) );
 
 		Attribute generatedNode = node.attribute( "generated" );
         String generationName = generatedNode == null ? null : generatedNode.getValue();
         PropertyGeneration generation = PropertyGeneration.parse( generationName );
 		property.setGeneration( generation );
 
         if ( generation == PropertyGeneration.ALWAYS || generation == PropertyGeneration.INSERT ) {
 	        // generated properties can *never* be insertable...
 	        if ( property.isInsertable() ) {
 		        if ( insertNode == null ) {
 			        // insertable simply because that is the user did not specify
 			        // anything; just override it
 					property.setInsertable( false );
 		        }
 		        else {
 			        // the user specifically supplied insert="true",
 			        // which constitutes an illegal combo
 					throw new MappingException(
 							"cannot specify both insert=\"true\" and generated=\"" + generation.getName() +
 							"\" for property: " +
 							propName
 					);
 		        }
 	        }
 
 	        // properties generated on update can never be updateable...
 	        if ( property.isUpdateable() && generation == PropertyGeneration.ALWAYS ) {
 		        if ( updateNode == null ) {
 			        // updateable only because the user did not specify
 			        // anything; just override it
 			        property.setUpdateable( false );
 		        }
 		        else {
 			        // the user specifically supplied update="true",
 			        // which constitutes an illegal combo
 					throw new MappingException(
 							"cannot specify both update=\"true\" and generated=\"" + generation.getName() +
 							"\" for property: " +
 							propName
 					);
 		        }
 	        }
         }
 
 		boolean isLazyable = "property".equals( node.getName() ) ||
 				"component".equals( node.getName() ) ||
 				"many-to-one".equals( node.getName() ) ||
 				"one-to-one".equals( node.getName() ) ||
 				"any".equals( node.getName() );
 		if ( isLazyable ) {
 			Attribute lazyNode = node.attribute( "lazy" );
 			property.setLazy( lazyNode != null && "true".equals( lazyNode.getValue() ) );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			String msg = "Mapped property: " + property.getName();
 			String columns = columns( property.getValue() );
 			if ( columns.length() > 0 ) msg += " -> " + columns;
 			// TODO: this fails if we run with debug on!
 			// if ( model.getType()!=null ) msg += ", type: " + model.getType().getName();
 			LOG.debug( msg );
 		}
 
 		property.setMetaAttributes( getMetas( node, inheritedMetas ) );
 
 	}
 
 	private static String columns(Value val) {
 		StringBuilder columns = new StringBuilder();
 		Iterator iter = val.getColumnIterator();
 		while ( iter.hasNext() ) {
 			columns.append( ( (Selectable) iter.next() ).getText() );
 			if ( iter.hasNext() ) columns.append( ", " );
 		}
 		return columns.toString();
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollection(Element node, Collection collection, String className,
 			String path, Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		// ROLENAME
 		collection.setRole(path);
 
 		Attribute inverseNode = node.attribute( "inverse" );
 		if ( inverseNode != null ) {
 			collection.setInverse( "true".equals( inverseNode.getValue() ) );
 		}
 
 		Attribute mutableNode = node.attribute( "mutable" );
 		if ( mutableNode != null ) {
 			collection.setMutable( !"false".equals( mutableNode.getValue() ) );
 		}
 
 		Attribute olNode = node.attribute( "optimistic-lock" );
 		collection.setOptimisticLocked( olNode == null || "true".equals( olNode.getValue() ) );
 
 		Attribute orderNode = node.attribute( "order-by" );
 		if ( orderNode != null ) {
 			collection.setOrderBy( orderNode.getValue() );
 		}
 		Attribute whereNode = node.attribute( "where" );
 		if ( whereNode != null ) {
 			collection.setWhere( whereNode.getValue() );
 		}
 		Attribute batchNode = node.attribute( "batch-size" );
 		if ( batchNode != null ) {
 			collection.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
 		}
 
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		collection.setNodeName( nodeName );
 		String embed = node.attributeValue( "embed-xml" );
 		collection.setEmbedded( embed==null || "true".equals(embed) );
 
 
 		// PERSISTER
 		Attribute persisterNode = node.attribute( "persister" );
 		if ( persisterNode != null ) {
 			try {
 				collection.setCollectionPersisterClass( ReflectHelper.classForName( persisterNode
 					.getValue() ) );
 			}
 			catch (ClassNotFoundException cnfe) {
 				throw new MappingException( "Could not find collection persister class: "
 					+ persisterNode.getValue() );
 			}
 		}
 
 		Attribute typeNode = node.attribute( "collection-type" );
 		if ( typeNode != null ) {
 			String typeName = typeNode.getValue();
 			TypeDef typeDef = mappings.getTypeDef( typeName );
 			if ( typeDef != null ) {
 				collection.setTypeName( typeDef.getTypeClass() );
 				collection.setTypeParameters( typeDef.getParameters() );
 			}
 			else {
 				collection.setTypeName( typeName );
 			}
 		}
 
 		// FETCH STRATEGY
 
 		initOuterJoinFetchSetting( node, collection );
 
 		if ( "subselect".equals( node.attributeValue("fetch") ) ) {
 			collection.setSubselectLoadable(true);
 			collection.getOwner().setSubselectLoadableCollections(true);
 		}
 
 		initLaziness( node, collection, mappings, "true", mappings.isDefaultLazy() );
 		//TODO: suck this into initLaziness!
 		if ( "extra".equals( node.attributeValue("lazy") ) ) {
 			collection.setLazy(true);
 			collection.setExtraLazy(true);
 		}
 
 		Element oneToManyNode = node.element( "one-to-many" );
 		if ( oneToManyNode != null ) {
 			OneToMany oneToMany = new OneToMany( mappings, collection.getOwner() );
 			collection.setElement( oneToMany );
 			bindOneToMany( oneToManyNode, oneToMany, mappings );
 			// we have to set up the table later!! yuck
 		}
 		else {
 			// TABLE
 			Attribute tableNode = node.attribute( "table" );
 			String tableName;
 			if ( tableNode != null ) {
 				tableName = mappings.getNamingStrategy().tableName( tableNode.getValue() );
 			}
 			else {
 				//tableName = mappings.getNamingStrategy().propertyToTableName( className, path );
 				Table ownerTable = collection.getOwner().getTable();
 				//TODO mappings.getLogicalTableName(ownerTable)
 				String logicalOwnerTableName = ownerTable.getName();
 				//FIXME we don't have the associated entity table name here, has to be done in a second pass
 				tableName = mappings.getNamingStrategy().collectionTableName(
 						collection.getOwner().getEntityName(),
 						logicalOwnerTableName ,
 						null,
 						null,
 						path
 				);
 				if ( ownerTable.isQuoted() ) {
 					tableName = StringHelper.quote( tableName );
 				}
 			}
 			Attribute schemaNode = node.attribute( "schema" );
 			String schema = schemaNode == null ?
 					mappings.getSchemaName() : schemaNode.getValue();
 
 			Attribute catalogNode = node.attribute( "catalog" );
 			String catalog = catalogNode == null ?
 					mappings.getCatalogName() : catalogNode.getValue();
 
 			Table table = mappings.addTable(
 					schema,
 					catalog,
 					tableName,
 					getSubselect( node ),
 					false
 				);
 			collection.setCollectionTable( table );
 			bindComment(table, node);
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 			}
 		}
 
 		// SORT
 		Attribute sortedAtt = node.attribute( "sort" );
 		// unsorted, natural, comparator.class.name
 		if ( sortedAtt == null || sortedAtt.getValue().equals( "unsorted" ) ) {
 			collection.setSorted( false );
 		}
 		else {
 			collection.setSorted( true );
 			String comparatorClassName = sortedAtt.getValue();
 			if ( !comparatorClassName.equals( "natural" ) ) {
 				collection.setComparatorClassName(comparatorClassName);
 			}
 		}
 
 		// ORPHAN DELETE (used for programmer error detection)
 		Attribute cascadeAtt = node.attribute( "cascade" );
 		if ( cascadeAtt != null && cascadeAtt.getValue().indexOf( "delete-orphan" ) >= 0 ) {
 			collection.setOrphanDelete( true );
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, collection );
 		// set up second pass
 		if ( collection instanceof List ) {
 			mappings.addSecondPass( new ListSecondPass( node, mappings, (List) collection, inheritedMetas ) );
 		}
 		else if ( collection instanceof Map ) {
 			mappings.addSecondPass( new MapSecondPass( node, mappings, (Map) collection, inheritedMetas ) );
 		}
 		else if ( collection instanceof IdentifierCollection ) {
 			mappings.addSecondPass( new IdentifierCollectionSecondPass(
 					node,
 					mappings,
 					collection,
 					inheritedMetas
 				) );
 		}
 		else {
 			mappings.addSecondPass( new CollectionSecondPass( node, mappings, collection, inheritedMetas ) );
 		}
 
 		Iterator iter = node.elementIterator( "filter" );
 		while ( iter.hasNext() ) {
 			final Element filter = (Element) iter.next();
 			parseFilter( filter, collection, mappings );
 		}
 
 		Iterator tables = node.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			collection.getSynchronizedTables().add(
 				( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 
 		Element element = node.element( "loader" );
 		if ( element != null ) {
 			collection.setLoaderName( element.attributeValue( "query-ref" ) );
 		}
 
 		collection.setReferencedPropertyName( node.element( "key" ).attributeValue( "property-ref" ) );
 	}
 
 	private static void initLaziness(
 			Element node,
 			Fetchable fetchable,
 			Mappings mappings,
 			String proxyVal,
 			boolean defaultLazy
 	) {
 		Attribute lazyNode = node.attribute( "lazy" );
 		boolean isLazyTrue = lazyNode == null ?
 				defaultLazy && fetchable.isLazy() : //fetch="join" overrides default laziness
 				lazyNode.getValue().equals(proxyVal); //fetch="join" overrides default laziness
 		fetchable.setLazy( isLazyTrue );
 	}
 
 	private static void initLaziness(
 			Element node,
 			ToOne fetchable,
 			Mappings mappings,
 			boolean defaultLazy
 	) {
 		if ( "no-proxy".equals( node.attributeValue( "lazy" ) ) ) {
 			fetchable.setUnwrapProxy(true);
-			fetchable.setLazy(true);
+			fetchable.setLazy( true );
 			//TODO: better to degrade to lazy="false" if uninstrumented
 		}
 		else {
-			initLaziness(node, fetchable, mappings, "proxy", defaultLazy);
+			initLaziness( node, fetchable, mappings, "proxy", defaultLazy );
 		}
 	}
 
 	private static void bindColumnsOrFormula(Element node, SimpleValue simpleValue, String path,
 			boolean isNullable, Mappings mappings) {
 		Attribute formulaNode = node.attribute( "formula" );
 		if ( formulaNode != null ) {
 			Formula f = new Formula();
 			f.setFormula( formulaNode.getText() );
 			simpleValue.addFormula( f );
 		}
 		else {
 			bindColumns( node, simpleValue, isNullable, true, path, mappings );
 		}
 	}
 
 	private static void bindComment(Table table, Element node) {
 		Element comment = node.element("comment");
 		if (comment!=null) table.setComment( comment.getTextTrim() );
 	}
 
 	public static void bindManyToOne(Element node, ManyToOne manyToOne, String path,
 			boolean isNullable, Mappings mappings) throws MappingException {
 
 		bindColumnsOrFormula( node, manyToOne, path, isNullable, mappings );
 		initOuterJoinFetchSetting( node, manyToOne );
 		initLaziness( node, manyToOne, mappings, true );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) {
 			manyToOne.setReferencedPropertyName( ukName.getValue() );
 		}
 
 		manyToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		manyToOne.setEmbedded( embed == null || "true".equals( embed ) );
 
 		String notFound = node.attributeValue( "not-found" );
 		manyToOne.setIgnoreNotFound( "ignore".equals( notFound ) );
 
 		if( ukName != null && !manyToOne.isIgnoreNotFound() ) {
 			if ( !node.getName().equals("many-to-many") ) { //TODO: really bad, evil hack to fix!!!
 				mappings.addSecondPass( new ManyToOneSecondPass(manyToOne) );
 			}
 		}
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) manyToOne.setForeignKeyName( fkNode.getValue() );
 
 		String cascade = node.attributeValue( "cascade" );
 		if ( cascade != null && cascade.indexOf( "delete-orphan" ) >= 0 ) {
 			if ( !manyToOne.isLogicalOneToOne() ) {
 				throw new MappingException(
 						"many-to-one attribute [" + path + "] does not support orphan delete as it is not unique"
 				);
 			}
 		}
 	}
 
 	public static void bindAny(Element node, Any any, boolean isNullable, Mappings mappings)
 			throws MappingException {
 		any.setIdentifierType( getTypeFromXML( node ) );
 		Attribute metaAttribute = node.attribute( "meta-type" );
 		if ( metaAttribute != null ) {
 			any.setMetaType( metaAttribute.getValue() );
 
 			Iterator iter = node.elementIterator( "meta-value" );
 			if ( iter.hasNext() ) {
 				HashMap values = new HashMap();
 				org.hibernate.type.Type metaType = mappings.getTypeResolver().heuristicType( any.getMetaType() );
 				while ( iter.hasNext() ) {
 					Element metaValue = (Element) iter.next();
 					try {
 						Object value = ( (DiscriminatorType) metaType ).stringToObject( metaValue
 							.attributeValue( "value" ) );
 						String entityName = getClassName( metaValue.attribute( "class" ), mappings );
 						values.put( value, entityName );
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException( "meta-type was not a DiscriminatorType: "
 							+ metaType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException( "could not interpret meta-value", e );
 					}
 				}
 				any.setMetaValues( values );
 			}
 
 		}
 
 		bindColumns( node, any, isNullable, false, null, mappings );
 	}
 
 	public static void bindOneToOne(Element node, OneToOne oneToOne, String path, boolean isNullable,
 			Mappings mappings) throws MappingException {
 
 		bindColumns( node, oneToOne, isNullable, false, null, mappings );
 
 		Attribute constrNode = node.attribute( "constrained" );
 		boolean constrained = constrNode != null && constrNode.getValue().equals( "true" );
 		oneToOne.setConstrained( constrained );
 
 		oneToOne.setForeignKeyType( constrained ?
 				ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT :
 				ForeignKeyDirection.FOREIGN_KEY_TO_PARENT );
 
 		initOuterJoinFetchSetting( node, oneToOne );
 		initLaziness( node, oneToOne, mappings, true );
 
 		oneToOne.setEmbedded( "true".equals( node.attributeValue( "embed-xml" ) ) );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) oneToOne.setForeignKeyName( fkNode.getValue() );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) oneToOne.setReferencedPropertyName( ukName.getValue() );
 
 		oneToOne.setPropertyName( node.attributeValue( "name" ) );
 
 		oneToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String cascade = node.attributeValue( "cascade" );
 		if ( cascade != null && cascade.indexOf( "delete-orphan" ) >= 0 ) {
 			if ( oneToOne.isConstrained() ) {
 				throw new MappingException(
 						"one-to-one attribute [" + path + "] does not support orphan delete as it is constrained"
 				);
 			}
 		}
 	}
 
 	public static void bindOneToMany(Element node, OneToMany oneToMany, Mappings mappings)
 			throws MappingException {
 
 		oneToMany.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		oneToMany.setEmbedded( embed == null || "true".equals( embed ) );
 
 		String notFound = node.attributeValue( "not-found" );
 		oneToMany.setIgnoreNotFound( "ignore".equals( notFound ) );
 
 	}
 
 	public static void bindColumn(Element node, Column column, boolean isNullable) throws MappingException {
 		Attribute lengthNode = node.attribute( "length" );
 		if ( lengthNode != null ) column.setLength( Integer.parseInt( lengthNode.getValue() ) );
 		Attribute scalNode = node.attribute( "scale" );
 		if ( scalNode != null ) column.setScale( Integer.parseInt( scalNode.getValue() ) );
 		Attribute precNode = node.attribute( "precision" );
 		if ( precNode != null ) column.setPrecision( Integer.parseInt( precNode.getValue() ) );
 
 		Attribute nullNode = node.attribute( "not-null" );
 		column.setNullable( nullNode == null ? isNullable : nullNode.getValue().equals( "false" ) );
 
 		Attribute unqNode = node.attribute( "unique" );
 		if ( unqNode != null ) column.setUnique( unqNode.getValue().equals( "true" ) );
 
 		column.setCheckConstraint( node.attributeValue( "check" ) );
 		column.setDefaultValue( node.attributeValue( "default" ) );
 
 		Attribute typeNode = node.attribute( "sql-type" );
 		if ( typeNode != null ) column.setSqlType( typeNode.getValue() );
 
 		String customWrite = node.attributeValue( "write" );
 		if(customWrite != null && !customWrite.matches("[^?]*\\?[^?]*")) {
 			throw new MappingException("write expression must contain exactly one value placeholder ('?') character");
 		}
 		column.setCustomWrite( customWrite );
 		column.setCustomRead( node.attributeValue( "read" ) );
 
 		Element comment = node.element("comment");
 		if (comment!=null) column.setComment( comment.getTextTrim() );
 
 	}
 
 	/**
 	 * Called for arrays and primitive arrays
 	 */
 	public static void bindArray(Element node, Array array, String prefix, String path,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollection( node, array, prefix, path, mappings, inheritedMetas );
 
 		Attribute att = node.attribute( "element-class" );
 		if ( att != null ) array.setElementClassName( getClassName( att, mappings ) );
 
 	}
 
 	private static Class reflectedPropertyClass(String className, String propertyName)
 			throws MappingException {
 		if ( className == null ) return null;
 		return ReflectHelper.reflectedPropertyClass( className, propertyName );
 	}
 
 	public static void bindComposite(Element node, Component component, String path,
 			boolean isNullable, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 		bindComponent(
 				node,
 				component,
 				null,
 				null,
 				path,
 				isNullable,
 				false,
 				mappings,
 				inheritedMetas,
 				false
-			);
+		);
 	}
 
 	public static void bindCompositeId(Element node, Component component,
 			PersistentClass persistentClass, String propertyName, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		component.setKey( true );
 
 		String path = StringHelper.qualify(
 				persistentClass.getEntityName(),
 				propertyName == null ? "id" : propertyName );
 
 		bindComponent(
 				node,
 				component,
 				persistentClass.getClassName(),
 				propertyName,
 				path,
 				false,
 				node.attribute( "class" ) == null
 						&& propertyName == null,
 				mappings,
 				inheritedMetas,
 				false
 			);
 
 		if ( "true".equals( node.attributeValue("mapped") ) ) {
 			if ( propertyName!=null ) {
 				throw new MappingException("cannot combine mapped=\"true\" with specified name");
 			}
 			Component mapper = new Component( mappings, persistentClass );
 			bindComponent(
 					node,
 					mapper,
 					persistentClass.getClassName(),
 					null,
 					path,
 					false,
 					true,
 					mappings,
 					inheritedMetas,
 					true
 				);
 			persistentClass.setIdentifierMapper(mapper);
 			Property property = new Property();
 			property.setName("_identifierMapper");
 			property.setNodeName("id");
 			property.setUpdateable(false);
 			property.setInsertable(false);
 			property.setValue(mapper);
 			property.setPropertyAccessorName( "embedded" );
 			persistentClass.addProperty(property);
 		}
 
 	}
 
 	public static void bindComponent(
 			Element node,
 			Component component,
 			String ownerClassName,
 			String parentProperty,
 			String path,
 			boolean isNullable,
 			boolean isEmbedded,
 			Mappings mappings,
 			java.util.Map inheritedMetas,
 			boolean isIdentifierMapper) throws MappingException {
 
 		component.setEmbedded( isEmbedded );
 		component.setRoleName( path );
 
 		inheritedMetas = getMetas( node, inheritedMetas );
 		component.setMetaAttributes( inheritedMetas );
 
 		Attribute classNode = isIdentifierMapper ? null : node.attribute( "class" );
 		if ( classNode != null ) {
 			component.setComponentClassName( getClassName( classNode, mappings ) );
 		}
 		else if ( "dynamic-component".equals( node.getName() ) ) {
 			component.setDynamic( true );
 		}
 		else if ( isEmbedded ) {
 			// an "embedded" component (composite ids and unique)
 			// note that this does not handle nested components
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				component.setComponentClassName( component.getOwner().getClassName() );
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 		else {
 			// todo : again, how *should* this work for non-pojo entities?
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				Class reflectedClass = reflectedPropertyClass( ownerClassName, parentProperty );
 				if ( reflectedClass != null ) {
 					component.setComponentClassName( reflectedClass.getName() );
 				}
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		if ( nodeName == null ) nodeName = component.getOwner().getNodeName();
 		component.setNodeName( nodeName );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = getPropertyName( subnode );
 			String subpath = propertyName == null ? null : StringHelper
 				.qualify( path, propertyName );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						subpath,
 						component.getOwner(),
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) || "key-many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindManyToOne( subnode, (ManyToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, component.getTable(), component.getOwner() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindOneToOne( subnode, (OneToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, component.getTable() );
 				bindAny( subnode, (Any) value, isNullable, mappings );
 			}
 			else if ( "property".equals( name ) || "key-property".equals( name ) ) {
 				value = new SimpleValue( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindSimpleValue( subnode, (SimpleValue) value, isNullable, relativePath, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "nested-composite-element".equals( name ) ) {
 				value = new Component( mappings, component ); // a nested composite element
 				bindComponent(
 						subnode,
 						(Component) value,
 						component.getComponentClassName(),
 						propertyName,
 						subpath,
 						isNullable,
 						isEmbedded,
 						mappings,
 						inheritedMetas,
 						isIdentifierMapper
 					);
 			}
 			else if ( "parent".equals( name ) ) {
 				component.setParentProperty( propertyName );
 			}
 
 			if ( value != null ) {
 				Property property = createProperty( value, propertyName, component
 					.getComponentClassName(), subnode, mappings, inheritedMetas );
 				if (isIdentifierMapper) {
 					property.setInsertable(false);
 					property.setUpdateable(false);
 				}
 				component.addProperty( property );
 			}
 		}
 
 		if ( "true".equals( node.attributeValue( "unique" ) ) ) {
 			iter = component.getColumnIterator();
 			ArrayList cols = new ArrayList();
 			while ( iter.hasNext() ) {
 				cols.add( iter.next() );
 			}
 			component.getOwner().getTable().createUniqueKey( cols );
 		}
 
 		iter = node.elementIterator( "tuplizer" );
 		while ( iter.hasNext() ) {
 			final Element tuplizerElem = ( Element ) iter.next();
 			EntityMode mode = EntityMode.parse( tuplizerElem.attributeValue( "entity-mode" ) );
 			component.addTuplizer( mode, tuplizerElem.attributeValue( "class" ) );
 		}
 	}
 
 	public static String getTypeFromXML(Element node) throws MappingException {
 		// TODO: handle TypeDefs
 		Attribute typeNode = node.attribute( "type" );
 		if ( typeNode == null ) typeNode = node.attribute( "id-type" ); // for an any
 		if ( typeNode == null ) return null; // we will have to use reflection
 		return typeNode.getValue();
 	}
 
 	private static void initOuterJoinFetchSetting(Element node, Fetchable model) {
 		Attribute fetchNode = node.attribute( "fetch" );
 		final FetchMode fetchStyle;
 		boolean lazy = true;
 		if ( fetchNode == null ) {
 			Attribute jfNode = node.attribute( "outer-join" );
 			if ( jfNode == null ) {
 				if ( "many-to-many".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// default to join and non-lazy for the "second join"
 					// of the many-to-many
 					lazy = false;
 					fetchStyle = FetchMode.JOIN;
 				}
 				else if ( "one-to-one".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// one-to-one constrained=false cannot be proxied,
 					// so default to join and non-lazy
 					lazy = ( (OneToOne) model ).isConstrained();
 					fetchStyle = lazy ? FetchMode.DEFAULT : FetchMode.JOIN;
 				}
 				else {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 			}
 			else {
 				// use old (HB 2.1) defaults if outer-join is specified
 				String eoj = jfNode.getValue();
 				if ( "auto".equals( eoj ) ) {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 				else {
 					boolean join = "true".equals( eoj );
 					fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 				}
 			}
 		}
 		else {
 			boolean join = "join".equals( fetchNode.getValue() );
 			//lazy = !join;
 			fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 		}
 		model.setFetchMode( fetchStyle );
 		model.setLazy(lazy);
 	}
 
 	private static void makeIdentifier(Element node, SimpleValue model, Mappings mappings) {
 
 		// GENERATOR
 		Element subnode = node.element( "generator" );
 		if ( subnode != null ) {
 			final String generatorClass = subnode.attributeValue( "class" );
 			model.setIdentifierGeneratorStrategy( generatorClass );
 
 			Properties params = new Properties();
 			// YUCK!  but cannot think of a clean way to do this given the string-config based scheme
 			params.put( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, mappings.getObjectNameNormalizer() );
 
 			if ( mappings.getSchemaName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.SCHEMA,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getSchemaName() )
 				);
 			}
 			if ( mappings.getCatalogName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.CATALOG,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getCatalogName() )
 				);
 			}
 
 			Iterator iter = subnode.elementIterator( "param" );
 			while ( iter.hasNext() ) {
 				Element childNode = (Element) iter.next();
 				params.setProperty( childNode.attributeValue( "name" ), childNode.getTextTrim() );
 			}
 
 			model.setIdentifierGeneratorProperties( params );
 		}
 
 		model.getTable().setIdentifierValue( model );
 
 		// ID UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			if ( "assigned".equals( model.getIdentifierGeneratorStrategy() ) ) {
 				model.setNullValue( "undefined" );
 			}
 			else {
 				model.setNullValue( null );
 			}
 		}
 	}
 
 	private static final void makeVersion(Element node, SimpleValue model) {
 
 		// VERSION UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			model.setNullValue( "undefined" );
 		}
 
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 		createClassProperties(node, persistentClass, mappings, inheritedMetas, null, true, true, false);
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas, UniqueKey uniqueKey,
 			boolean mutable, boolean nullable, boolean naturalId) throws MappingException {
 
 		String entityName = persistentClass.getEntityName();
 		Table table = persistentClass.getTable();
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = subnode.attributeValue( "name" );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						StringHelper.qualify( entityName, propertyName ),
 						persistentClass,
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, table );
 				bindManyToOne( subnode, (ManyToOne) value, propertyName, nullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, table );
 				bindAny( subnode, (Any) value, nullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, table, persistentClass );
 				bindOneToOne( subnode, (OneToOne) value, propertyName, true, mappings );
 			}
 			else if ( "property".equals( name ) ) {
 				value = new SimpleValue( mappings, table );
 				bindSimpleValue( subnode, (SimpleValue) value, nullable, propertyName, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "properties".equals( name ) ) {
 				String subpath = StringHelper.qualify( entityName, propertyName );
 				value = new Component( mappings, persistentClass );
 
 				bindComponent(
 						subnode,
 						(Component) value,
 						persistentClass.getClassName(),
 						propertyName,
 						subpath,
 						true,
 						"properties".equals( name ),
 						mappings,
 						inheritedMetas,
 						false
 					);
 			}
 			else if ( "join".equals( name ) ) {
 				Join join = new Join();
 				join.setPersistentClass( persistentClass );
 				bindJoin( subnode, join, mappings, inheritedMetas );
 				persistentClass.addJoin( join );
 			}
 			else if ( "subclass".equals( name ) ) {
 				handleSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "joined-subclass".equals( name ) ) {
 				handleJoinedSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "union-subclass".equals( name ) ) {
 				handleUnionSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "filter".equals( name ) ) {
 				parseFilter( subnode, persistentClass, mappings );
 			}
 			else if ( "natural-id".equals( name ) ) {
 				UniqueKey uk = new UniqueKey();
 				uk.setName("_UniqueKey");
 				uk.setTable(table);
 				//by default, natural-ids are "immutable" (constant)
 				boolean mutableId = "true".equals( subnode.attributeValue("mutable") );
 				createClassProperties(
 						subnode,
 						persistentClass,
 						mappings,
 						inheritedMetas,
 						uk,
 						mutableId,
 						false,
 						true
 					);
 				table.addUniqueKey(uk);
 			}
 			else if ( "query".equals(name) ) {
 				bindNamedQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "sql-query".equals(name) ) {
 				bindNamedSQLQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "resultset".equals(name) ) {
 				bindResultSetMappingDefinition( subnode, persistentClass.getEntityName(), mappings );
 			}
 
 			if ( value != null ) {
 				final Property property = createProperty(
 						value,
 						propertyName,
 						persistentClass.getClassName(),
 						subnode,
 						mappings,
 						inheritedMetas
 				);
 				if ( !mutable ) {
 					property.setUpdateable(false);
 				}
 				if ( naturalId ) {
 					property.setNaturalIdentifier( true );
 				}
 				persistentClass.addProperty( property );
 				if ( uniqueKey!=null ) {
 					uniqueKey.addColumns( property.getColumnIterator() );
 				}
 			}
 
 		}
 	}
 
 	private static Property createProperty(
 			final Value value,
 	        final String propertyName,
 			final String className,
 	        final Element subnode,
 	        final Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		if ( StringHelper.isEmpty( propertyName ) ) {
 			throw new MappingException( subnode.getName() + " mapping must defined a name attribute [" + className + "]" );
 		}
 
 		value.setTypeUsingReflection( className, propertyName );
 
 		// this is done here 'cos we might only know the type here (ugly!)
 		// TODO: improve this a lot:
 		if ( value instanceof ToOne ) {
 			ToOne toOne = (ToOne) value;
 			String propertyRef = toOne.getReferencedPropertyName();
 			if ( propertyRef != null ) {
 				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
 			}
 		}
 		else if ( value instanceof Collection ) {
 			Collection coll = (Collection) value;
 			String propertyRef = coll.getReferencedPropertyName();
 			// not necessarily a *unique* property reference
 			if ( propertyRef != null ) {
 				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
 			}
 		}
 
 		value.createForeignKey();
 		Property prop = new Property();
 		prop.setValue( value );
 		bindProperty( subnode, prop, mappings, inheritedMetas );
 		return prop;
 	}
 
 	private static void handleUnionSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		UnionSubclass subclass = new UnionSubclass( model );
 		bindUnionSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleJoinedSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		JoinedSubclass subclass = new JoinedSubclass( model );
 		bindJoinedSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleSubclass(PersistentClass model, Mappings mappings, Element subnode,
 			java.util.Map inheritedMetas) throws MappingException {
 		Subclass subclass = new SingleTableSubclass( model );
 		bindSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	/**
 	 * Called for Lists, arrays, primitive arrays
 	 */
 	public static void bindListSecondPass(Element node, List list, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, list, classes, mappings, inheritedMetas );
 
 		Element subnode = node.element( "list-index" );
 		if ( subnode == null ) subnode = node.element( "index" );
 		SimpleValue iv = new SimpleValue( mappings, list.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				iv,
 				list.isOneToMany(),
 				IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 				mappings
-			);
+		);
 		iv.setTypeName( "integer" );
 		list.setIndex( iv );
 		String baseIndex = subnode.attributeValue( "base" );
 		if ( baseIndex != null ) list.setBaseIndex( Integer.parseInt( baseIndex ) );
 		list.setIndexNodeName( subnode.attributeValue("node") );
 
 		if ( list.isOneToMany() && !list.getKey().isNullable() && !list.isInverse() ) {
 			String entityName = ( (OneToMany) list.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + list.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( list.getRole() );
 			ib.setEntityName( list.getOwner().getEntityName() );
 			ib.setValue( list.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	public static void bindIdentifierCollectionSecondPass(Element node,
 			IdentifierCollection collection, java.util.Map persistentClasses, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, collection, persistentClasses, mappings, inheritedMetas );
 
 		Element subnode = node.element( "collection-id" );
 		SimpleValue id = new SimpleValue( mappings, collection.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				id,
 				false,
 				IdentifierCollection.DEFAULT_IDENTIFIER_COLUMN_NAME,
 				mappings
 			);
 		collection.setIdentifier( id );
 		makeIdentifier( subnode, id, mappings );
 
 	}
 
 	/**
 	 * Called for Maps
 	 */
 	public static void bindMapSecondPass(Element node, Map map, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, map, classes, mappings, inheritedMetas );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "index".equals( name ) || "map-key".equals( name ) ) {
 				SimpleValue value = new SimpleValue( mappings, map.getCollectionTable() );
 				bindSimpleValue(
 						subnode,
 						value,
 						map.isOneToMany(),
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						mappings
 					);
 				if ( !value.isTypeSpecified() ) {
 					throw new MappingException( "map index element must specify a type: "
 						+ map.getRole() );
 				}
 				map.setIndex( value );
 				map.setIndexNodeName( subnode.attributeValue("node") );
 			}
 			else if ( "index-many-to-many".equals( name ) || "map-key-many-to-many".equals( name ) ) {
 				ManyToOne mto = new ManyToOne( mappings, map.getCollectionTable() );
 				bindManyToOne(
 						subnode,
 						mto,
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						map.isOneToMany(),
 						mappings
 					);
 				map.setIndex( mto );
 
 			}
 			else if ( "composite-index".equals( name ) || "composite-map-key".equals( name ) ) {
 				Component component = new Component( mappings, map );
 				bindComposite(
 						subnode,
 						component,
 						map.getRole() + ".index",
 						map.isOneToMany(),
 						mappings,
 						inheritedMetas
 					);
 				map.setIndex( component );
 			}
 			else if ( "index-many-to-any".equals( name ) ) {
 				Any any = new Any( mappings, map.getCollectionTable() );
 				bindAny( subnode, any, map.isOneToMany(), mappings );
 				map.setIndex( any );
 			}
 		}
 
 		// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
 		boolean indexIsFormula = false;
 		Iterator colIter = map.getIndex().getColumnIterator();
 		while ( colIter.hasNext() ) {
 			if ( ( (Selectable) colIter.next() ).isFormula() ) indexIsFormula = true;
 		}
 
 		if ( map.isOneToMany() && !map.getKey().isNullable() && !map.isInverse() && !indexIsFormula ) {
 			String entityName = ( (OneToMany) map.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + map.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( map.getRole() );
 			ib.setEntityName( map.getOwner().getEntityName() );
 			ib.setValue( map.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollectionSecondPass(Element node, Collection collection,
 			java.util.Map persistentClasses, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 
 		if ( collection.isOneToMany() ) {
 			OneToMany oneToMany = (OneToMany) collection.getElement();
 			String assocClass = oneToMany.getReferencedEntityName();
 			PersistentClass persistentClass = (PersistentClass) persistentClasses.get( assocClass );
 			if ( persistentClass == null ) {
 				throw new MappingException( "Association references unmapped class: " + assocClass );
 			}
 			oneToMany.setAssociatedClass( persistentClass );
 			collection.setCollectionTable( persistentClass.getTable() );
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 			}
 		}
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) {
 			collection.getCollectionTable().addCheckConstraint( chNode.getValue() );
 		}
 
 		// contained elements:
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "key".equals( name ) ) {
 				KeyValue keyVal;
 				String propRef = collection.getReferencedPropertyName();
 				if ( propRef == null ) {
 					keyVal = collection.getOwner().getIdentifier();
 				}
 				else {
 					keyVal = (KeyValue) collection.getOwner().getRecursiveProperty( propRef ).getValue();
 				}
 				SimpleValue key = new DependantValue( mappings, collection.getCollectionTable(), keyVal );
 				key.setCascadeDeleteEnabled( "cascade"
 					.equals( subnode.attributeValue( "on-delete" ) ) );
 				bindSimpleValue(
 						subnode,
 						key,
 						collection.isOneToMany(),
 						Collection.DEFAULT_KEY_COLUMN_NAME,
 						mappings
 					);
 				collection.setKey( key );
 
 				Attribute notNull = subnode.attribute( "not-null" );
 				( (DependantValue) key ).setNullable( notNull == null
 					|| notNull.getValue().equals( "false" ) );
 				Attribute updateable = subnode.attribute( "update" );
 				( (DependantValue) key ).setUpdateable( updateable == null
 					|| updateable.getValue().equals( "true" ) );
 
 			}
 			else if ( "element".equals( name ) ) {
 				SimpleValue elt = new SimpleValue( mappings, collection.getCollectionTable() );
 				collection.setElement( elt );
 				bindSimpleValue(
 						subnode,
 						elt,
 						true,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						mappings
 					);
 			}
 			else if ( "many-to-many".equals( name ) ) {
 				ManyToOne element = new ManyToOne( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindManyToOne(
 						subnode,
 						element,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						false,
 						mappings
 					);
 				bindManyToManySubelements( collection, subnode, mappings );
 			}
 			else if ( "composite-element".equals( name ) ) {
 				Component element = new Component( mappings, collection );
 				collection.setElement( element );
 				bindComposite(
 						subnode,
 						element,
 						collection.getRole() + ".element",
 						true,
 						mappings,
 						inheritedMetas
 					);
 			}
 			else if ( "many-to-any".equals( name ) ) {
 				Any element = new Any( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindAny( subnode, element, true, mappings );
 			}
 			else if ( "cache".equals( name ) ) {
 				collection.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
 				collection.setCacheRegionName( subnode.attributeValue( "region" ) );
 			}
 
 			String nodeName = subnode.attributeValue( "node" );
 			if ( nodeName != null ) collection.setElementNodeName( nodeName );
 
 		}
 
 		if ( collection.isOneToMany()
 			&& !collection.isInverse()
 			&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = ( (OneToMany) collection.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + collection.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 	private static void bindManyToManySubelements(
 	        Collection collection,
 	        Element manyToManyNode,
 	        Mappings model) throws MappingException {
 		// Bind the where
 		Attribute where = manyToManyNode.attribute( "where" );
 		String whereCondition = where == null ? null : where.getValue();
 		collection.setManyToManyWhere( whereCondition );
 
 		// Bind the order-by
 		Attribute order = manyToManyNode.attribute( "order-by" );
 		String orderFragment = order == null ? null : order.getValue();
 		collection.setManyToManyOrdering( orderFragment );
 
 		// Bind the filters
 		Iterator filters = manyToManyNode.elementIterator( "filter" );
 		if ( ( filters.hasNext() || whereCondition != null ) &&
 		        collection.getFetchMode() == FetchMode.JOIN &&
 		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 			throw new MappingException(
 			        "many-to-many defining filter or where without join fetching " +
 			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 				);
 		}
 		while ( filters.hasNext() ) {
 			final Element filterElement = ( Element ) filters.next();
 			final String name = filterElement.attributeValue( "name" );
 			String condition = filterElement.getTextTrim();
 			if ( StringHelper.isEmpty(condition) ) condition = filterElement.attributeValue( "condition" );
 			if ( StringHelper.isEmpty(condition) ) {
 				condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 			}
 			if ( condition==null) {
 				throw new MappingException("no filter condition found for filter: " + name);
 			}
 			Iterator aliasesIterator = filterElement.elementIterator("aliases");
 			java.util.Map<String, String> aliasTables = new HashMap<String, String>();
 			while (aliasesIterator.hasNext()){
 				Element alias = (Element) aliasesIterator.next();
 				aliasTables.put(alias.attributeValue("alias"), alias.attributeValue("table"));
 			}
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Applying many-to-many filter [%s] as [%s] to role [%s]", name, condition, collection.getRole() );
 			}
 			String autoAliasInjectionText = filterElement.attributeValue("autoAliasInjection");
 			boolean autoAliasInjection = StringHelper.isEmpty(autoAliasInjectionText) ? true : Boolean.parseBoolean(autoAliasInjectionText);
 			collection.addManyToManyFilter(name, condition, autoAliasInjection, aliasTables, null);
 		}
 	}
 
-	public static final FlushMode getFlushMode(String flushMode) {
-		if ( flushMode == null ) {
-			return null;
-		}
-		else if ( "auto".equals( flushMode ) ) {
-			return FlushMode.AUTO;
-		}
-		else if ( "commit".equals( flushMode ) ) {
-			return FlushMode.COMMIT;
-		}
-		else if ( "never".equals( flushMode ) ) {
-			return FlushMode.NEVER;
-		}
-		else if ( "manual".equals( flushMode ) ) {
-			return FlushMode.MANUAL;
-		}
-		else if ( "always".equals( flushMode ) ) {
-			return FlushMode.ALWAYS;
-		}
-		else {
-			throw new MappingException( "unknown flushmode" );
-		}
-	}
-
 	private static void bindNamedQuery(Element queryElem, String path, Mappings mappings) {
 		String queryName = queryElem.attributeValue( "name" );
 		if (path!=null) queryName = path + '.' + queryName;
 		String query = queryElem.getText();
 		LOG.debugf( "Named query: %s -> %s", queryName, query );
 
 		boolean cacheable = "true".equals( queryElem.attributeValue( "cacheable" ) );
 		String region = queryElem.attributeValue( "cache-region" );
 		Attribute tAtt = queryElem.attribute( "timeout" );
 		Integer timeout = tAtt == null ? null : Integer.valueOf( tAtt.getValue() );
 		Attribute fsAtt = queryElem.attribute( "fetch-size" );
 		Integer fetchSize = fsAtt == null ? null : Integer.valueOf( fsAtt.getValue() );
 		Attribute roAttr = queryElem.attribute( "read-only" );
 		boolean readOnly = roAttr != null && "true".equals( roAttr.getValue() );
 		Attribute cacheModeAtt = queryElem.attribute( "cache-mode" );
 		String cacheMode = cacheModeAtt == null ? null : cacheModeAtt.getValue();
 		Attribute cmAtt = queryElem.attribute( "comment" );
 		String comment = cmAtt == null ? null : cmAtt.getValue();
 
-		NamedQueryDefinition namedQuery = new NamedQueryDefinition(
-				queryName,
-				query,
-				cacheable,
-				region,
-				timeout,
-				fetchSize,
-				getFlushMode( queryElem.attributeValue( "flush-mode" ) ) ,
-				getCacheMode( cacheMode ),
-				readOnly,
-				comment,
-				getParameterTypes(queryElem)
-			);
+		NamedQueryDefinition namedQuery = new NamedQueryDefinitionBuilder().setName( queryName )
+				.setQuery( query )
+				.setCacheable( cacheable )
+				.setCacheRegion( region )
+				.setTimeout( timeout )
+				.setFetchSize( fetchSize )
+				.setFlushMode( FlushMode.interpretExternalSetting( queryElem.attributeValue( "flush-mode" ) ) )
+				.setCacheMode( CacheMode.interpretExternalSetting( cacheMode ) )
+				.setReadOnly( readOnly )
+				.setComment( comment )
+				.setParameterTypes( getParameterTypes( queryElem ) )
+				.createNamedQueryDefinition();
 
 		mappings.addQuery( namedQuery.getName(), namedQuery );
 	}
 
-	public static CacheMode getCacheMode(String cacheMode) {
-		if (cacheMode == null) return null;
-		if ( "get".equals( cacheMode ) ) return CacheMode.GET;
-		if ( "ignore".equals( cacheMode ) ) return CacheMode.IGNORE;
-		if ( "normal".equals( cacheMode ) ) return CacheMode.NORMAL;
-		if ( "put".equals( cacheMode ) ) return CacheMode.PUT;
-		if ( "refresh".equals( cacheMode ) ) return CacheMode.REFRESH;
-		throw new MappingException("Unknown Cache Mode: " + cacheMode);
-	}
-
 	public static java.util.Map getParameterTypes(Element queryElem) {
 		java.util.Map result = new java.util.LinkedHashMap();
 		Iterator iter = queryElem.elementIterator("query-param");
 		while ( iter.hasNext() ) {
 			Element element = (Element) iter.next();
 			result.put( element.attributeValue("name"), element.attributeValue("type") );
 		}
 		return result;
 	}
 
 	private static void bindResultSetMappingDefinition(Element resultSetElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new ResultSetMappingSecondPass( resultSetElem, path, mappings ) );
 	}
 
 	private static void bindNamedSQLQuery(Element queryElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new NamedSQLQuerySecondPass( queryElem, path, mappings ) );
 	}
 
 	private static String getPropertyName(Element node) {
 		return node.attributeValue( "name" );
 	}
 
 	private static PersistentClass getSuperclass(Mappings mappings, Element subnode)
 			throws MappingException {
 		String extendsName = subnode.attributeValue( "extends" );
 		PersistentClass superModel = mappings.getClass( extendsName );
 		if ( superModel == null ) {
 			String qualifiedExtendsName = getClassName( extendsName, mappings );
 			superModel = mappings.getClass( qualifiedExtendsName );
 		}
 
 		if ( superModel == null ) {
 			throw new MappingException( "Cannot extend unmapped class " + extendsName );
 		}
 		return superModel;
 	}
 
 	static class CollectionSecondPass extends org.hibernate.cfg.CollectionSecondPass {
 		Element node;
 
 		CollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super(mappings, collection, inheritedMetas);
 			this.node = node;
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindCollectionSecondPass(
 					node,
 					collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 	}
 
 	static class IdentifierCollectionSecondPass extends CollectionSecondPass {
 		IdentifierCollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindIdentifierCollectionSecondPass(
 					node,
 					(IdentifierCollection) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	static class MapSecondPass extends CollectionSecondPass {
 		MapSecondPass(Element node, Mappings mappings, Map collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindMapSecondPass(
 					node,
 					(Map) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 
 	static class ManyToOneSecondPass implements SecondPass {
 		private final ManyToOne manyToOne;
 
 		ManyToOneSecondPass(ManyToOne manyToOne) {
 			this.manyToOne = manyToOne;
 		}
 
 		public void doSecondPass(java.util.Map persistentClasses) throws MappingException {
 			manyToOne.createPropertyRefConstraints(persistentClasses);
 		}
 
 	}
 
 	static class ListSecondPass extends CollectionSecondPass {
 		ListSecondPass(Element node, Mappings mappings, List collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindListSecondPass(
 					node,
 					(List) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	// This inner class implements a case statement....perhaps im being a bit over-clever here
 	abstract static class CollectionType {
 		private String xmlTag;
 
 		public abstract Collection create(Element node, String path, PersistentClass owner,
 				Mappings mappings, java.util.Map inheritedMetas) throws MappingException;
 
 		CollectionType(String xmlTag) {
 			this.xmlTag = xmlTag;
 		}
 
 		public String toString() {
 			return xmlTag;
 		}
 
 		private static final CollectionType MAP = new CollectionType( "map" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Map map = new Map( mappings, owner );
 				bindCollection( node, map, owner.getEntityName(), path, mappings, inheritedMetas );
 				return map;
 			}
 		};
 		private static final CollectionType SET = new CollectionType( "set" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Set set = new Set( mappings, owner );
 				bindCollection( node, set, owner.getEntityName(), path, mappings, inheritedMetas );
 				return set;
 			}
 		};
 		private static final CollectionType LIST = new CollectionType( "list" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				List list = new List( mappings, owner );
 				bindCollection( node, list, owner.getEntityName(), path, mappings, inheritedMetas );
 				return list;
 			}
 		};
 		private static final CollectionType BAG = new CollectionType( "bag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Bag bag = new Bag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType IDBAG = new CollectionType( "idbag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				IdentifierBag bag = new IdentifierBag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType ARRAY = new CollectionType( "array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Array array = new Array( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final CollectionType PRIMITIVE_ARRAY = new CollectionType( "primitive-array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				PrimitiveArray array = new PrimitiveArray( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final HashMap INSTANCES = new HashMap();
 
 		static {
 			INSTANCES.put( MAP.toString(), MAP );
 			INSTANCES.put( BAG.toString(), BAG );
 			INSTANCES.put( IDBAG.toString(), IDBAG );
 			INSTANCES.put( SET.toString(), SET );
 			INSTANCES.put( LIST.toString(), LIST );
 			INSTANCES.put( ARRAY.toString(), ARRAY );
 			INSTANCES.put( PRIMITIVE_ARRAY.toString(), PRIMITIVE_ARRAY );
 		}
 
 		public static CollectionType collectionTypeFromString(String xmlTagName) {
 			return (CollectionType) INSTANCES.get( xmlTagName );
 		}
 	}
 
 	private static int getOptimisticLockMode(Attribute olAtt) throws MappingException {
 
 		if ( olAtt == null ) return Versioning.OPTIMISTIC_LOCK_VERSION;
 		String olMode = olAtt.getValue();
 		if ( olMode == null || "version".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_VERSION;
 		}
 		else if ( "dirty".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_DIRTY;
 		}
 		else if ( "all".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_ALL;
 		}
 		else if ( "none".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_NONE;
 		}
 		else {
 			throw new MappingException( "Unsupported optimistic-lock style: " + olMode );
 		}
 	}
 
 	private static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta) {
 		return getMetas( node, inheritedMeta, false );
 	}
 
 	public static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta,
 			boolean onlyInheritable) {
 		java.util.Map map = new HashMap();
 		map.putAll( inheritedMeta );
 
 		Iterator iter = node.elementIterator( "meta" );
 		while ( iter.hasNext() ) {
 			Element metaNode = (Element) iter.next();
 			boolean inheritable = Boolean
 				.valueOf( metaNode.attributeValue( "inherit" ) )
 				.booleanValue();
 			if ( onlyInheritable & !inheritable ) {
 				continue;
 			}
 			String name = metaNode.attributeValue( "attribute" );
 
 			MetaAttribute meta = (MetaAttribute) map.get( name );
 			MetaAttribute inheritedAttribute = (MetaAttribute) inheritedMeta.get( name );
 			if ( meta == null  ) {
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			} else if (meta == inheritedAttribute) { // overriding inherited meta attribute. HBX-621 & HBX-793
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			}
 			meta.addValue( metaNode.getText() );
 		}
 		return map;
 	}
 
 	public static String getEntityName(Element elem, Mappings model) {
 		String entityName = elem.attributeValue( "entity-name" );
 		return entityName == null ? getClassName( elem.attribute( "class" ), model ) : entityName;
 	}
 
 	private static String getClassName(Attribute att, Mappings model) {
 		if ( att == null ) return null;
 		return getClassName( att.getValue(), model );
 	}
 
 	public static String getClassName(String unqualifiedName, Mappings model) {
 		return getClassName( unqualifiedName, model.getDefaultPackage() );
 	}
 
 	public static String getClassName(String unqualifiedName, String defaultPackage) {
 		if ( unqualifiedName == null ) return null;
 		if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
 			return defaultPackage + '.' + unqualifiedName;
 		}
 		return unqualifiedName;
 	}
 
 	private static void parseFilterDef(Element element, Mappings mappings) {
 		String name = element.attributeValue( "name" );
 		LOG.debugf( "Parsing filter-def [%s]", name );
 		String defaultCondition = element.getTextTrim();
 		if ( StringHelper.isEmpty( defaultCondition ) ) {
 			defaultCondition = element.attributeValue( "condition" );
 		}
 		HashMap paramMappings = new HashMap();
 		Iterator params = element.elementIterator( "filter-param" );
 		while ( params.hasNext() ) {
 			final Element param = (Element) params.next();
 			final String paramName = param.attributeValue( "name" );
 			final String paramType = param.attributeValue( "type" );
 			LOG.debugf( "Adding filter parameter : %s -> %s", paramName, paramType );
 			final Type heuristicType = mappings.getTypeResolver().heuristicType( paramType );
 			LOG.debugf( "Parameter heuristic type : %s", heuristicType );
 			paramMappings.put( paramName, heuristicType );
 		}
 		LOG.debugf( "Parsed filter-def [%s]", name );
 		FilterDefinition def = new FilterDefinition( name, defaultCondition, paramMappings );
 		mappings.addFilterDefinition( def );
 	}
 
 	private static void parseFilter(Element filterElement, Filterable filterable, Mappings model) {
 		final String name = filterElement.attributeValue( "name" );
 		String condition = filterElement.getTextTrim();
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = filterElement.attributeValue( "condition" );
 		}
 		//TODO: bad implementation, cos it depends upon ordering of mapping doc
 		//      fixing this requires that Collection/PersistentClass gain access
 		//      to the Mappings reference from Configuration (or the filterDefinitions
 		//      map directly) sometime during Configuration.buildSessionFactory
 		//      (after all the types/filter-defs are known and before building
 		//      persisters).
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 		}
 		if ( condition==null) {
 			throw new MappingException("no filter condition found for filter: " + name);
 		}
 		Iterator aliasesIterator = filterElement.elementIterator("aliases");
 		java.util.Map<String, String> aliasTables = new HashMap<String, String>();
 		while (aliasesIterator.hasNext()){
 			Element alias = (Element) aliasesIterator.next();
 			aliasTables.put(alias.attributeValue("alias"), alias.attributeValue("table"));
 		}
 		LOG.debugf( "Applying filter [%s] as [%s]", name, condition );
 		String autoAliasInjectionText = filterElement.attributeValue("autoAliasInjection");
 		boolean autoAliasInjection = StringHelper.isEmpty(autoAliasInjectionText) ? true : Boolean.parseBoolean(autoAliasInjectionText);
 		filterable.addFilter(name, condition, autoAliasInjection, aliasTables, null);
 	}
 
 	private static void parseFetchProfile(Element element, Mappings mappings, String containingEntityName) {
 		String profileName = element.attributeValue( "name" );
 		FetchProfile profile = mappings.findOrCreateFetchProfile( profileName, MetadataSource.HBM );
 		Iterator itr = element.elementIterator( "fetch" );
 		while ( itr.hasNext() ) {
 			final Element fetchElement = ( Element ) itr.next();
 			final String association = fetchElement.attributeValue( "association" );
 			final String style = fetchElement.attributeValue( "style" );
 			String entityName = fetchElement.attributeValue( "entity" );
 			if ( entityName == null ) {
 				entityName = containingEntityName;
 			}
 			if ( entityName == null ) {
 				throw new MappingException( "could not determine entity for fetch-profile fetch [" + profileName + "]:[" + association + "]" );
 			}
 			profile.addFetch( entityName, association, style );
 		}
 	}
 
 	private static String getSubselect(Element element) {
 		String subselect = element.attributeValue( "subselect" );
 		if ( subselect != null ) {
 			return subselect;
 		}
 		else {
 			Element subselectElement = element.element( "subselect" );
 			return subselectElement == null ? null : subselectElement.getText();
 		}
 	}
 
 	/**
 	 * For the given document, locate all extends attributes which refer to
 	 * entities (entity-name or class-name) not defined within said document.
 	 *
 	 * @param metadataXml The document to check
 	 * @param mappings The already processed mappings.
 	 * @return The list of unresolved extends names.
 	 */
 	public static java.util.List<String> getExtendsNeeded(XmlDocument metadataXml, Mappings mappings) {
 		java.util.List<String> extendz = new ArrayList<String>();
 		Iterator[] subclasses = new Iterator[3];
 		final Element hmNode = metadataXml.getDocumentTree().getRootElement();
 
 		Attribute packNode = hmNode.attribute( "package" );
 		final String packageName = packNode == null ? null : packNode.getValue();
 		if ( packageName != null ) {
 			mappings.setDefaultPackage( packageName );
 		}
 
 		// first, iterate over all elements capable of defining an extends attribute
 		// collecting all found extends references if they cannot be resolved
 		// against the already processed mappings.
 		subclasses[0] = hmNode.elementIterator( "subclass" );
 		subclasses[1] = hmNode.elementIterator( "joined-subclass" );
 		subclasses[2] = hmNode.elementIterator( "union-subclass" );
 
 		Iterator iterator = new JoinedIterator( subclasses );
 		while ( iterator.hasNext() ) {
 			final Element element = (Element) iterator.next();
 			final String extendsName = element.attributeValue( "extends" );
 			// mappings might contain either the "raw" extends name (in the case of
 			// an entity-name mapping) or a FQN (in the case of a POJO mapping).
 			if ( mappings.getClass( extendsName ) == null && mappings.getClass( getClassName( extendsName, mappings ) ) == null ) {
 				extendz.add( extendsName );
 			}
 		}
 
 		if ( !extendz.isEmpty() ) {
 			// we found some extends attributes referencing entities which were
 			// not already processed.  here we need to locate all entity-names
 			// and class-names contained in this document itself, making sure
 			// that these get removed from the extendz list such that only
 			// extends names which require us to delay processing (i.e.
 			// external to this document and not yet processed) are contained
 			// in the returned result
 			final java.util.Set<String> set = new HashSet<String>( extendz );
 			EntityElementHandler handler = new EntityElementHandler() {
 				public void handleEntity(String entityName, String className, Mappings mappings) {
 					if ( entityName != null ) {
 						set.remove( entityName );
 					}
 					else {
 						String fqn = getClassName( className, packageName );
 						set.remove( fqn );
 						if ( packageName != null ) {
 							set.remove( StringHelper.unqualify( fqn ) );
 						}
 					}
 				}
 			};
 			recognizeEntities( mappings, hmNode, handler );
 			extendz.clear();
 			extendz.addAll( set );
 		}
 
 		return extendz;
 	}
 
 	/**
 	 * Given an entity-containing-element (startNode) recursively locate all
 	 * entity names defined within that element.
 	 *
 	 * @param mappings The already processed mappings
 	 * @param startNode The containing element
 	 * @param handler The thing that knows what to do whenever we recognize an
 	 * entity-name
 	 */
 	private static void recognizeEntities(
 			Mappings mappings,
 	        final Element startNode,
 			EntityElementHandler handler) {
 		Iterator[] classes = new Iterator[4];
 		classes[0] = startNode.elementIterator( "class" );
 		classes[1] = startNode.elementIterator( "subclass" );
 		classes[2] = startNode.elementIterator( "joined-subclass" );
 		classes[3] = startNode.elementIterator( "union-subclass" );
 
 		Iterator classIterator = new JoinedIterator( classes );
 		while ( classIterator.hasNext() ) {
 			Element element = (Element) classIterator.next();
 			handler.handleEntity(
 					element.attributeValue( "entity-name" ),
 		            element.attributeValue( "name" ),
 			        mappings
 			);
 			recognizeEntities( mappings, element, handler );
 		}
 	}
 
 	private static interface EntityElementHandler {
 		public void handleEntity(String entityName, String className, Mappings mappings);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
index 1df87eaeaf..686ed21d36 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/NamedSQLQuerySecondPass.java
@@ -1,129 +1,130 @@
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
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.dom4j.Attribute;
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 
+import org.hibernate.CacheMode;
+import org.hibernate.FlushMode;
 import org.hibernate.MappingException;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
+import org.hibernate.engine.spi.NamedSQLQueryDefinitionBuilder;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * @author Emmanuel Bernard
  */
 public class NamedSQLQuerySecondPass extends ResultSetMappingBinder implements QuerySecondPass {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        NamedSQLQuerySecondPass.class.getName());
 
 	private Element queryElem;
 	private String path;
 	private Mappings mappings;
 
 	public NamedSQLQuerySecondPass(Element queryElem, String path, Mappings mappings) {
 		this.queryElem = queryElem;
 		this.path = path;
 		this.mappings = mappings;
 	}
 
 	public void doSecondPass(Map persistentClasses) throws MappingException {
 		String queryName = queryElem.attribute( "name" ).getValue();
 		if (path!=null) queryName = path + '.' + queryName;
 
 		boolean cacheable = "true".equals( queryElem.attributeValue( "cacheable" ) );
 		String region = queryElem.attributeValue( "cache-region" );
 		Attribute tAtt = queryElem.attribute( "timeout" );
 		Integer timeout = tAtt == null ? null : Integer.valueOf( tAtt.getValue() );
 		Attribute fsAtt = queryElem.attribute( "fetch-size" );
 		Integer fetchSize = fsAtt == null ? null : Integer.valueOf( fsAtt.getValue() );
 		Attribute roAttr = queryElem.attribute( "read-only" );
 		boolean readOnly = roAttr != null && "true".equals( roAttr.getValue() );
 		Attribute cacheModeAtt = queryElem.attribute( "cache-mode" );
 		String cacheMode = cacheModeAtt == null ? null : cacheModeAtt.getValue();
 		Attribute cmAtt = queryElem.attribute( "comment" );
 		String comment = cmAtt == null ? null : cmAtt.getValue();
 
 		java.util.List<String> synchronizedTables = new ArrayList<String>();
 		Iterator tables = queryElem.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			synchronizedTables.add( ( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 		boolean callable = "true".equals( queryElem.attributeValue( "callable" ) );
 
 		NamedSQLQueryDefinition namedQuery;
 		Attribute ref = queryElem.attribute( "resultset-ref" );
 		String resultSetRef = ref == null ? null : ref.getValue();
 		if ( StringHelper.isNotEmpty( resultSetRef ) ) {
-			namedQuery = new NamedSQLQueryDefinition(
-					queryName,
-					queryElem.getText(),
-					resultSetRef,
-					synchronizedTables,
-					cacheable,
-					region,
-					timeout,
-					fetchSize,
-					HbmBinder.getFlushMode( queryElem.attributeValue( "flush-mode" ) ),
-					HbmBinder.getCacheMode( cacheMode ),
-					readOnly,
-					comment,
-					HbmBinder.getParameterTypes( queryElem ),
-					callable
-			);
+			namedQuery = new NamedSQLQueryDefinitionBuilder().setName( queryName )
+					.setQuery( queryElem.getText() )
+					.setResultSetRef( resultSetRef )
+					.setQuerySpaces( synchronizedTables )
+					.setCacheable( cacheable )
+					.setCacheRegion( region )
+					.setTimeout( timeout )
+					.setFetchSize( fetchSize )
+					.setFlushMode( FlushMode.interpretExternalSetting( queryElem.attributeValue( "flush-mode" ) ) )
+					.setCacheMode( CacheMode.interpretExternalSetting( cacheMode ) )
+					.setReadOnly( readOnly )
+					.setComment( comment )
+					.setParameterTypes( HbmBinder.getParameterTypes( queryElem ) )
+					.setCallable( callable )
+					.createNamedQueryDefinition();
 			//TODO check there is no actual definition elemnents when a ref is defined
 		}
 		else {
 			ResultSetMappingDefinition definition = buildResultSetMappingDefinition( queryElem, path, mappings );
-			namedQuery = new NamedSQLQueryDefinition(
-					queryName,
-					queryElem.getText(),
-					definition.getQueryReturns(),
-					synchronizedTables,
-					cacheable,
-					region,
-					timeout,
-					fetchSize,
-					HbmBinder.getFlushMode( queryElem.attributeValue( "flush-mode" ) ),
-					HbmBinder.getCacheMode( cacheMode ),
-					readOnly,
-					comment,
-					HbmBinder.getParameterTypes( queryElem ),
-					callable
-			);
+			namedQuery = new NamedSQLQueryDefinitionBuilder().setName( queryName )
+					.setQuery( queryElem.getText() )
+					.setQueryReturns( definition.getQueryReturns() )
+					.setQuerySpaces( synchronizedTables )
+					.setCacheable( cacheable )
+					.setCacheRegion( region )
+					.setTimeout( timeout )
+					.setFetchSize( fetchSize )
+					.setFlushMode( FlushMode.interpretExternalSetting( queryElem.attributeValue( "flush-mode" ) ) )
+					.setCacheMode( CacheMode.interpretExternalSetting( cacheMode ) )
+					.setReadOnly( readOnly )
+					.setComment( comment )
+					.setParameterTypes( HbmBinder.getParameterTypes( queryElem ) )
+					.setCallable( callable )
+					.createNamedQueryDefinition();
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Named SQL query: %s -> %s", namedQuery.getName(), namedQuery.getQueryString() );
 		}
 		mappings.addSQLQuery( queryName, namedQuery );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
index 178eee9022..54f623c556 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/QueryBinder.java
@@ -1,437 +1,462 @@
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
 package org.hibernate.cfg.annotations;
 import java.util.HashMap;
+import javax.persistence.LockModeType;
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.NamedQueries;
 import javax.persistence.NamedQuery;
 import javax.persistence.QueryHint;
 import javax.persistence.SqlResultSetMapping;
 import javax.persistence.SqlResultSetMappings;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.LockMode;
+import org.hibernate.LockOptions;
 import org.hibernate.annotations.CacheModeType;
 import org.hibernate.annotations.FlushModeType;
+import org.hibernate.annotations.QueryHints;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.NamedQueryDefinition;
+import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
+import org.hibernate.engine.spi.NamedSQLQueryDefinitionBuilder;
 import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.internal.util.LockModeConverter;
 
 /**
  * Query binder
  *
  * @author Emmanuel Bernard
  */
 public abstract class QueryBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, QueryBinder.class.getName());
 
 	public static void bindQuery(NamedQuery queryAnn, Mappings mappings, boolean isDefault) {
 		if ( queryAnn == null ) return;
 		if ( BinderHelper.isEmptyAnnotationValue( queryAnn.name() ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 		//EJBQL Query
 		QueryHint[] hints = queryAnn.hints();
 		String queryName = queryAnn.query();
-		NamedQueryDefinition query = new NamedQueryDefinition(
-				queryAnn.name(),
-				queryName,
-				getBoolean( queryName, "org.hibernate.cacheable", hints ),
-				getString( queryName, "org.hibernate.cacheRegion", hints ),
-				getTimeout( queryName, hints ),
-				getInteger( queryName, "javax.persistence.lock.timeout", hints ),
-				getInteger( queryName, "org.hibernate.fetchSize", hints ),
-				getFlushMode( queryName, hints ),
-				getCacheMode( queryName, hints ),
-				getBoolean( queryName, "org.hibernate.readOnly", hints ),
-				getString( queryName, "org.hibernate.comment", hints ),
-				null
-		);
+		NamedQueryDefinition queryDefinition = new NamedQueryDefinitionBuilder( queryAnn.name() )
+				.setLockOptions( determineLockOptions( queryAnn, hints ) )
+				.setQuery( queryName )
+				.setCacheable( getBoolean( queryName, "org.hibernate.cacheable", hints ) )
+				.setCacheRegion( getString( queryName, "org.hibernate.cacheRegion", hints ) )
+				.setTimeout( getTimeout( queryName, hints ) )
+				.setFetchSize( getInteger( queryName, "org.hibernate.fetchSize", hints ) )
+				.setFlushMode( getFlushMode( queryName, hints ) )
+				.setCacheMode( getCacheMode( queryName, hints ) )
+				.setReadOnly( getBoolean( queryName, "org.hibernate.readOnly", hints ) )
+				.setComment( getString( queryName, "org.hibernate.comment", hints ) )
+				.setParameterTypes( null )
+				.createNamedQueryDefinition();
+
 		if ( isDefault ) {
-			mappings.addDefaultQuery( query.getName(), query );
+			mappings.addDefaultQuery( queryDefinition.getName(), queryDefinition );
 		}
 		else {
-			mappings.addQuery( query.getName(), query );
+			mappings.addQuery( queryDefinition.getName(), queryDefinition );
 		}
 		if ( LOG.isDebugEnabled() ) {
-			LOG.debugf( "Binding named query: %s => %s", query.getName(), query.getQueryString() );
+			LOG.debugf( "Binding named query: %s => %s", queryDefinition.getName(), queryDefinition.getQueryString() );
 		}
 	}
 
+	private static LockOptions determineLockOptions(NamedQuery namedQueryAnnotation, QueryHint[] hints) {
+		LockModeType lockModeType = namedQueryAnnotation.lockMode();
+		Integer lockTimeoutHint = getInteger( namedQueryAnnotation.name(), "javax.persistence.lock.timeout", hints );
+
+		LockOptions lockOptions = new LockOptions( LockModeConverter.convertToLockMode( lockModeType ) );
+		if ( lockTimeoutHint != null ) {
+			lockOptions.setTimeOut( lockTimeoutHint );
+		}
+
+		return lockOptions;
+	}
+
 	public static void bindNativeQuery(NamedNativeQuery queryAnn, Mappings mappings, boolean isDefault) {
 		if ( queryAnn == null ) return;
 		//ResultSetMappingDefinition mappingDefinition = mappings.getResultSetMapping( queryAnn.resultSetMapping() );
 		if ( BinderHelper.isEmptyAnnotationValue( queryAnn.name() ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 		NamedSQLQueryDefinition query;
 		String resultSetMapping = queryAnn.resultSetMapping();
 		QueryHint[] hints = queryAnn.hints();
 		String queryName = queryAnn.query();
 		if ( !BinderHelper.isEmptyAnnotationValue( resultSetMapping ) ) {
 			//sql result set usage
-			query = new NamedSQLQueryDefinition(
-					queryAnn.name(),
-					queryName,
-					resultSetMapping,
-					null,
-					getBoolean( queryName, "org.hibernate.cacheable", hints ),
-					getString( queryName, "org.hibernate.cacheRegion", hints ),
-					getTimeout( queryName, hints ),
-					getInteger( queryName, "org.hibernate.fetchSize", hints ),
-					getFlushMode( queryName, hints ),
-					getCacheMode( queryName, hints ),
-					getBoolean( queryName, "org.hibernate.readOnly", hints ),
-					getString( queryName, "org.hibernate.comment", hints ),
-					null,
-					getBoolean( queryName, "org.hibernate.callable", hints )
-			);
+			query = new NamedSQLQueryDefinitionBuilder( queryAnn.name() )
+					.setQuery( queryName )
+					.setResultSetRef( resultSetMapping )
+					.setQuerySpaces( null )
+					.setCacheable( getBoolean( queryName, "org.hibernate.cacheable", hints ) )
+					.setCacheRegion( getString( queryName, "org.hibernate.cacheRegion", hints ) )
+					.setTimeout( getTimeout( queryName, hints ) )
+					.setFetchSize( getInteger( queryName, "org.hibernate.fetchSize", hints ) )
+					.setFlushMode( getFlushMode( queryName, hints ) )
+					.setCacheMode( getCacheMode( queryName, hints ) )
+					.setReadOnly( getBoolean( queryName, "org.hibernate.readOnly", hints ) )
+					.setComment( getString( queryName, "org.hibernate.comment", hints ) )
+					.setParameterTypes( null )
+					.setCallable( getBoolean( queryName, "org.hibernate.callable", hints ) )
+					.createNamedQueryDefinition();
 		}
 		else if ( !void.class.equals( queryAnn.resultClass() ) ) {
 			//class mapping usage
 			//FIXME should be done in a second pass due to entity name?
 			final NativeSQLQueryRootReturn entityQueryReturn =
 					new NativeSQLQueryRootReturn( "alias1", queryAnn.resultClass().getName(), new HashMap(), LockMode.READ );
-			query = new NamedSQLQueryDefinition(
-					queryAnn.name(),
-					queryName,
-					new NativeSQLQueryReturn[] { entityQueryReturn },
-					null,
-					getBoolean( queryName, "org.hibernate.cacheable", hints ),
-					getString( queryName, "org.hibernate.cacheRegion", hints ),
-					getTimeout( queryName, hints ),
-					getInteger( queryName, "org.hibernate.fetchSize", hints ),
-					getFlushMode( queryName, hints ),
-					getCacheMode( queryName, hints ),
-					getBoolean( queryName, "org.hibernate.readOnly", hints ),
-					getString( queryName, "org.hibernate.comment", hints ),
-					null,
-					getBoolean( queryName, "org.hibernate.callable", hints )
-			);
+			query = new NamedSQLQueryDefinitionBuilder( queryAnn.name() )
+					.setQuery( queryName )
+					.setQueryReturns( new NativeSQLQueryReturn[] {entityQueryReturn} )
+					.setQuerySpaces( null )
+					.setCacheable( getBoolean( queryName, "org.hibernate.cacheable", hints ) )
+					.setCacheRegion( getString( queryName, "org.hibernate.cacheRegion", hints ) )
+					.setTimeout( getTimeout( queryName, hints ) )
+					.setFetchSize( getInteger( queryName, "org.hibernate.fetchSize", hints ) )
+					.setFlushMode( getFlushMode( queryName, hints ) )
+					.setCacheMode( getCacheMode( queryName, hints ) )
+					.setReadOnly( getBoolean( queryName, "org.hibernate.readOnly", hints ) )
+					.setComment( getString( queryName, "org.hibernate.comment", hints ) )
+					.setParameterTypes( null )
+					.setCallable( getBoolean( queryName, "org.hibernate.callable", hints ) )
+					.createNamedQueryDefinition();
 		}
 		else {
 			throw new NotYetImplementedException( "Pure native scalar queries are not yet supported" );
 		}
 		if ( isDefault ) {
 			mappings.addDefaultSQLQuery( query.getName(), query );
 		}
 		else {
 			mappings.addSQLQuery( query.getName(), query );
 		}
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding named native query: %s => %s", queryAnn.name(), queryAnn.query() );
 		}
 	}
 
 	public static void bindNativeQuery(org.hibernate.annotations.NamedNativeQuery queryAnn, Mappings mappings) {
 		if ( queryAnn == null ) return;
 		//ResultSetMappingDefinition mappingDefinition = mappings.getResultSetMapping( queryAnn.resultSetMapping() );
 		if ( BinderHelper.isEmptyAnnotationValue( queryAnn.name() ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 		NamedSQLQueryDefinition query;
 		String resultSetMapping = queryAnn.resultSetMapping();
 		if ( !BinderHelper.isEmptyAnnotationValue( resultSetMapping ) ) {
 			//sql result set usage
-			query = new NamedSQLQueryDefinition(
-					queryAnn.name(),
-					queryAnn.query(),
-					resultSetMapping,
-					null,
-					queryAnn.cacheable(),
-					BinderHelper.isEmptyAnnotationValue( queryAnn.cacheRegion() ) ? null : queryAnn.cacheRegion(),
-					queryAnn.timeout() < 0 ? null : queryAnn.timeout(),
-					queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize(),
-					getFlushMode( queryAnn.flushMode() ),
-					getCacheMode( queryAnn.cacheMode() ),
-					queryAnn.readOnly(),
-					BinderHelper.isEmptyAnnotationValue( queryAnn.comment() ) ? null : queryAnn.comment(),
-					null,
-					queryAnn.callable()
-			);
+			query = new NamedSQLQueryDefinitionBuilder().setName( queryAnn.name() )
+					.setQuery( queryAnn.query() )
+					.setResultSetRef( resultSetMapping )
+					.setQuerySpaces( null )
+					.setCacheable( queryAnn.cacheable() )
+					.setCacheRegion(
+							BinderHelper.isEmptyAnnotationValue( queryAnn.cacheRegion() ) ?
+									null :
+									queryAnn.cacheRegion()
+					)
+					.setTimeout( queryAnn.timeout() < 0 ? null : queryAnn.timeout() )
+					.setFetchSize( queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize() )
+					.setFlushMode( getFlushMode( queryAnn.flushMode() ) )
+					.setCacheMode( getCacheMode( queryAnn.cacheMode() ) )
+					.setReadOnly( queryAnn.readOnly() )
+					.setComment( BinderHelper.isEmptyAnnotationValue( queryAnn.comment() ) ? null : queryAnn.comment() )
+					.setParameterTypes( null )
+					.setCallable( queryAnn.callable() )
+					.createNamedQueryDefinition();
 		}
 		else if ( !void.class.equals( queryAnn.resultClass() ) ) {
 			//class mapping usage
 			//FIXME should be done in a second pass due to entity name?
 			final NativeSQLQueryRootReturn entityQueryReturn =
 					new NativeSQLQueryRootReturn( "alias1", queryAnn.resultClass().getName(), new HashMap(), LockMode.READ );
-			query = new NamedSQLQueryDefinition(
-					queryAnn.name(),
-					queryAnn.query(),
-					new NativeSQLQueryReturn[] { entityQueryReturn },
-					null,
-					queryAnn.cacheable(),
-					BinderHelper.isEmptyAnnotationValue( queryAnn.cacheRegion() ) ? null : queryAnn.cacheRegion(),
-					queryAnn.timeout() < 0 ? null : queryAnn.timeout(),
-					queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize(),
-					getFlushMode( queryAnn.flushMode() ),
-					getCacheMode( queryAnn.cacheMode() ),
-					queryAnn.readOnly(),
-					BinderHelper.isEmptyAnnotationValue( queryAnn.comment() ) ? null : queryAnn.comment(),
-					null,
-					queryAnn.callable()
-			);
+			query = new NamedSQLQueryDefinitionBuilder().setName( queryAnn.name() )
+					.setQuery( queryAnn.query() )
+					.setQueryReturns( new NativeSQLQueryReturn[] {entityQueryReturn} )
+					.setQuerySpaces( null )
+					.setCacheable( queryAnn.cacheable() )
+					.setCacheRegion(
+							BinderHelper.isEmptyAnnotationValue( queryAnn.cacheRegion() ) ?
+									null :
+									queryAnn.cacheRegion()
+					)
+					.setTimeout( queryAnn.timeout() < 0 ? null : queryAnn.timeout() )
+					.setFetchSize( queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize() )
+					.setFlushMode( getFlushMode( queryAnn.flushMode() ) )
+					.setCacheMode( getCacheMode( queryAnn.cacheMode() ) )
+					.setReadOnly( queryAnn.readOnly() )
+					.setComment( BinderHelper.isEmptyAnnotationValue( queryAnn.comment() ) ? null : queryAnn.comment() )
+					.setParameterTypes( null )
+					.setCallable( queryAnn.callable() )
+					.createNamedQueryDefinition();
 		}
 		else {
 			throw new NotYetImplementedException( "Pure native scalar queries are not yet supported" );
 		}
 		mappings.addSQLQuery( query.getName(), query );
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding named native query: %s => %s", query.getName(), queryAnn.query() );
 		}
 	}
 
 	public static void bindQueries(NamedQueries queriesAnn, Mappings mappings, boolean isDefault) {
 		if ( queriesAnn == null ) return;
 		for (NamedQuery q : queriesAnn.value()) {
 			bindQuery( q, mappings, isDefault );
 		}
 	}
 
 	public static void bindNativeQueries(NamedNativeQueries queriesAnn, Mappings mappings, boolean isDefault) {
 		if ( queriesAnn == null ) return;
 		for (NamedNativeQuery q : queriesAnn.value()) {
 			bindNativeQuery( q, mappings, isDefault );
 		}
 	}
 
 	public static void bindNativeQueries(
 			org.hibernate.annotations.NamedNativeQueries queriesAnn, Mappings mappings
 	) {
 		if ( queriesAnn == null ) return;
 		for (org.hibernate.annotations.NamedNativeQuery q : queriesAnn.value()) {
 			bindNativeQuery( q, mappings );
 		}
 	}
 
 	public static void bindQuery(org.hibernate.annotations.NamedQuery queryAnn, Mappings mappings) {
 		if ( queryAnn == null ) return;
 		if ( BinderHelper.isEmptyAnnotationValue( queryAnn.name() ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 		FlushMode flushMode;
 		flushMode = getFlushMode( queryAnn.flushMode() );
 
-		NamedQueryDefinition query = new NamedQueryDefinition(
-				queryAnn.name(),
-				queryAnn.query(),
-				queryAnn.cacheable(),
-				BinderHelper.isEmptyAnnotationValue( queryAnn.cacheRegion() ) ? null : queryAnn.cacheRegion(),
-				queryAnn.timeout() < 0 ? null : queryAnn.timeout(),
-				queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize(),
-				flushMode,
-				getCacheMode( queryAnn.cacheMode() ),
-				queryAnn.readOnly(),
-				BinderHelper.isEmptyAnnotationValue( queryAnn.comment() ) ? null : queryAnn.comment(),
-				null
-		);
+		NamedQueryDefinition query = new NamedQueryDefinitionBuilder().setName( queryAnn.name() )
+				.setQuery( queryAnn.query() )
+				.setCacheable( queryAnn.cacheable() )
+				.setCacheRegion(
+						BinderHelper.isEmptyAnnotationValue( queryAnn.cacheRegion() ) ?
+								null :
+								queryAnn.cacheRegion()
+				)
+				.setTimeout( queryAnn.timeout() < 0 ? null : queryAnn.timeout() )
+				.setFetchSize( queryAnn.fetchSize() < 0 ? null : queryAnn.fetchSize() )
+				.setFlushMode( flushMode )
+				.setCacheMode( getCacheMode( queryAnn.cacheMode() ) )
+				.setReadOnly( queryAnn.readOnly() )
+				.setComment( BinderHelper.isEmptyAnnotationValue( queryAnn.comment() ) ? null : queryAnn.comment() )
+				.setParameterTypes( null )
+				.createNamedQueryDefinition();
 
 		mappings.addQuery( query.getName(), query );
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding named query: %s => %s", query.getName(), query.getQueryString() );
 		}
 	}
 
 	private static FlushMode getFlushMode(FlushModeType flushModeType) {
 		FlushMode flushMode;
 		switch ( flushModeType ) {
 			case ALWAYS:
 				flushMode = FlushMode.ALWAYS;
 				break;
 			case AUTO:
 				flushMode = FlushMode.AUTO;
 				break;
 			case COMMIT:
 				flushMode = FlushMode.COMMIT;
 				break;
 			case NEVER:
 				flushMode = FlushMode.MANUAL;
 				break;
 			case MANUAL:
 				flushMode = FlushMode.MANUAL;
 				break;
 			case PERSISTENCE_CONTEXT:
 				flushMode = null;
 				break;
 			default:
 				throw new AssertionFailure( "Unknown flushModeType: " + flushModeType );
 		}
 		return flushMode;
 	}
 
 	private static CacheMode getCacheMode(CacheModeType cacheModeType) {
 		switch ( cacheModeType ) {
 			case GET:
 				return CacheMode.GET;
 			case IGNORE:
 				return CacheMode.IGNORE;
 			case NORMAL:
 				return CacheMode.NORMAL;
 			case PUT:
 				return CacheMode.PUT;
 			case REFRESH:
 				return CacheMode.REFRESH;
 			default:
 				throw new AssertionFailure( "Unknown cacheModeType: " + cacheModeType );
 		}
 	}
 
 
 	public static void bindQueries(org.hibernate.annotations.NamedQueries queriesAnn, Mappings mappings) {
 		if ( queriesAnn == null ) return;
 		for (org.hibernate.annotations.NamedQuery q : queriesAnn.value()) {
 			bindQuery( q, mappings );
 		}
 	}
 
 	public static void bindSqlResultsetMappings(SqlResultSetMappings ann, Mappings mappings, boolean isDefault) {
 		if ( ann == null ) return;
 		for (SqlResultSetMapping rs : ann.value()) {
 			//no need to handle inSecondPass
 			mappings.addSecondPass( new ResultsetMappingSecondPass( rs, mappings, true ) );
 		}
 	}
 
 	public static void bindSqlResultsetMapping(SqlResultSetMapping ann, Mappings mappings, boolean isDefault) {
 		//no need to handle inSecondPass
 		mappings.addSecondPass( new ResultsetMappingSecondPass( ann, mappings, isDefault ) );
 	}
 
 	private static CacheMode getCacheMode(String query, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( "org.hibernate.cacheMode".equals( hint.name() ) ) {
 				if ( hint.value().equalsIgnoreCase( CacheMode.GET.toString() ) ) {
 					return CacheMode.GET;
 				}
 				else if ( hint.value().equalsIgnoreCase( CacheMode.IGNORE.toString() ) ) {
 					return CacheMode.IGNORE;
 				}
 				else if ( hint.value().equalsIgnoreCase( CacheMode.NORMAL.toString() ) ) {
 					return CacheMode.NORMAL;
 				}
 				else if ( hint.value().equalsIgnoreCase( CacheMode.PUT.toString() ) ) {
 					return CacheMode.PUT;
 				}
 				else if ( hint.value().equalsIgnoreCase( CacheMode.REFRESH.toString() ) ) {
 					return CacheMode.REFRESH;
 				}
 				else {
 					throw new AnnotationException( "Unknown CacheMode in hint: " + query + ":" + hint.name() );
 				}
 			}
 		}
 		return null;
 	}
 
 	private static FlushMode getFlushMode(String query, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( "org.hibernate.flushMode".equals( hint.name() ) ) {
 				if ( hint.value().equalsIgnoreCase( FlushMode.ALWAYS.toString() ) ) {
 					return FlushMode.ALWAYS;
 				}
 				else if ( hint.value().equalsIgnoreCase( FlushMode.AUTO.toString() ) ) {
 					return FlushMode.AUTO;
 				}
 				else if ( hint.value().equalsIgnoreCase( FlushMode.COMMIT.toString() ) ) {
 					return FlushMode.COMMIT;
 				}
 				else if ( hint.value().equalsIgnoreCase( FlushMode.NEVER.toString() ) ) {
 					return FlushMode.MANUAL;
 				}
 				else if ( hint.value().equalsIgnoreCase( FlushMode.MANUAL.toString() ) ) {
 					return FlushMode.MANUAL;
 				}
 				else {
 					throw new AnnotationException( "Unknown FlushMode in hint: " + query + ":" + hint.name() );
 				}
 			}
 		}
 		return null;
 	}
 
 	private static boolean getBoolean(String query, String hintName, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( hintName.equals( hint.name() ) ) {
 				if ( hint.value().equalsIgnoreCase( "true" ) ) {
 					return true;
 				}
 				else if ( hint.value().equalsIgnoreCase( "false" ) ) {
 					return false;
 				}
 				else {
 					throw new AnnotationException( "Not a boolean in hint: " + query + ":" + hint.name() );
 				}
 			}
 		}
 		return false;
 	}
 
 	private static String getString(String query, String hintName, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( hintName.equals( hint.name() ) ) {
 				return hint.value();
 			}
 		}
 		return null;
 	}
 
 	private static Integer getInteger(String query, String hintName, QueryHint[] hints) {
 		for (QueryHint hint : hints) {
 			if ( hintName.equals( hint.name() ) ) {
 				try {
 					return Integer.decode( hint.value() );
 				}
 				catch (NumberFormatException nfe) {
 					throw new AnnotationException( "Not an integer in hint: " + query + ":" + hint.name(), nfe );
 				}
 			}
 		}
 		return null;
 	}
 
 	private static Integer getTimeout(String queryName, QueryHint[] hints) {
 		Integer timeout = getInteger( queryName, "javax.persistence.query.timeout", hints );
 
 		if ( timeout != null ) {
 			// convert milliseconds to seconds
 			timeout = (int)Math.round(timeout.doubleValue() / 1000.0 );
 		}
 		else {
 			// timeout is already in seconds
 			timeout = getInteger( queryName, "org.hibernate.timeout", hints );
 		}
 		return timeout;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedQueryDefinition.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedQueryDefinition.java
index d10de98aa6..31d08e1096 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedQueryDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedQueryDefinition.java
@@ -1,176 +1,254 @@
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
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.LockOptions;
 
 /**
- * Definition of a named query, defined in the mapping metadata.
+ * Definition of a named query, defined in the mapping metadata.  Additional, as of JPA 2.1, named query definition
+ * can also come from a compiled query.
  *
  * @author Gavin King
+ * @author Steve Ebersole
  */
 public class NamedQueryDefinition implements Serializable {
 	private final String name;
 	private final String query;
 	private final boolean cacheable;
 	private final String cacheRegion;
 	private final Integer timeout;
-	private final Integer lockTimeout;
+	private final LockOptions lockOptions;
 	private final Integer fetchSize;
 	private final FlushMode flushMode;
 	private final Map parameterTypes;
-	private CacheMode cacheMode;
-	private boolean readOnly;
-	private String comment;
-
-	// kept for backward compatibility until after the 3.1beta5 release of HA
-	// TODO: is this still needed?
+	private final CacheMode cacheMode;
+	private final boolean readOnly;
+	private final String comment;
+
+	// added for jpa 2.1
+	private final Integer firstResult;
+	private final Integer maxResults;
+
+	/**
+	 * This form is used to bind named queries from Hibernate metadata, both {@code hbm.xml} files and
+	 * {@link org.hibernate.annotations.NamedQuery} annotation.
+	 *
+	 * @param name The name under which to key/register the query
+	 * @param query The query string.
+	 * @param cacheable Is the query cacheable?
+	 * @param cacheRegion If cacheable, was there a specific region named?
+	 * @param timeout Query timeout, {@code null} indicates no timeout
+	 * @param fetchSize Fetch size associated with the query, {@code null} indicates no limit
+	 * @param flushMode Flush mode associated with query
+	 * @param cacheMode Cache mode associated with query
+	 * @param readOnly Should entities returned from this query (those not already associated with the Session anyway)
+	 * 		be loaded as read-only?
+	 * @param comment SQL comment to be used in the generated SQL, {@code null} indicates none
+	 * @param parameterTypes (no idea, afaict this is always passed as null)
+	 *
+	 * @deprecated Use {@link NamedQueryDefinitionBuilder} instead.
+	 */
+	@Deprecated
 	public NamedQueryDefinition(
+			String name,
 			String query,
 			boolean cacheable,
 			String cacheRegion,
 			Integer timeout,
 			Integer fetchSize,
 			FlushMode flushMode,
+			CacheMode cacheMode,
+			boolean readOnly,
+			String comment,
 			Map parameterTypes) {
 		this(
-				null,
+				name,
 				query,
 				cacheable,
 				cacheRegion,
 				timeout,
+				LockOptions.WAIT_FOREVER,
 				fetchSize,
 				flushMode,
-				null,
-				false,
-				null,
+				cacheMode,
+				readOnly,
+				comment,
 				parameterTypes
 		);
 	}
 
+	/**
+	 * This version is used to bind named queries defined via {@link javax.persistence.NamedQuery}.
+	 *
+	 * @param name The name under which to key/register the query
+	 * @param query The query string.
+	 * @param cacheable Is the query cacheable?
+	 * @param cacheRegion If cacheable, was there a specific region named?
+	 * @param timeout Query timeout, {@code null} indicates no timeout
+	 * @param lockTimeout Specifies the lock timeout for queries that apply lock modes.
+	 * @param fetchSize Fetch size associated with the query, {@code null} indicates no limit
+	 * @param flushMode Flush mode associated with query
+	 * @param cacheMode Cache mode associated with query
+	 * @param readOnly Should entities returned from this query (those not already associated with the Session anyway)
+	 * 		be loaded as read-only?
+	 * @param comment SQL comment to be used in the generated SQL, {@code null} indicates none
+	 * @param parameterTypes (no idea, afaict this is always passed as null)
+	 *
+	 * @deprecated Use {@link NamedQueryDefinitionBuilder} instead.
+	 */
+	@Deprecated
 	public NamedQueryDefinition(
 			String name,
 			String query,
 			boolean cacheable,
 			String cacheRegion,
 			Integer timeout,
+			Integer lockTimeout,
 			Integer fetchSize,
 			FlushMode flushMode,
 			CacheMode cacheMode,
 			boolean readOnly,
 			String comment,
 			Map parameterTypes) {
-		this(name, query, cacheable, cacheRegion,
-				timeout, LockOptions.WAIT_FOREVER, fetchSize,
-				flushMode, cacheMode, readOnly, comment, parameterTypes);
+		this(
+				name,
+				query,
+				cacheable,
+				cacheRegion,
+				timeout,
+				new LockOptions().setTimeOut( lockTimeout ),
+				fetchSize,
+				flushMode,
+				cacheMode,
+				readOnly,
+				comment,
+				parameterTypes,
+				null,		// firstResult
+				null		// maxResults
+		);
 	}
 
-	public NamedQueryDefinition(
+	NamedQueryDefinition(
 			String name,
 			String query,
 			boolean cacheable,
 			String cacheRegion,
 			Integer timeout,
-			Integer lockTimeout,
+			LockOptions lockOptions,
 			Integer fetchSize,
 			FlushMode flushMode,
 			CacheMode cacheMode,
 			boolean readOnly,
 			String comment,
-			Map parameterTypes) {
+			Map parameterTypes,
+			Integer firstResult,
+			Integer maxResults) {
 		this.name = name;
 		this.query = query;
 		this.cacheable = cacheable;
 		this.cacheRegion = cacheRegion;
 		this.timeout = timeout;
-		this.lockTimeout = lockTimeout;
+		this.lockOptions = lockOptions;
 		this.fetchSize = fetchSize;
 		this.flushMode = flushMode;
 		this.parameterTypes = parameterTypes;
 		this.cacheMode = cacheMode;
 		this.readOnly = readOnly;
 		this.comment = comment;
+
+		this.firstResult = firstResult;
+		this.maxResults = maxResults;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public String getQueryString() {
 		return query;
 	}
 
 	public boolean isCacheable() {
 		return cacheable;
 	}
 
 	public String getCacheRegion() {
 		return cacheRegion;
 	}
 
 	public Integer getFetchSize() {
 		return fetchSize;
 	}
 
 	public Integer getTimeout() {
 		return timeout;
 	}
 
 	public FlushMode getFlushMode() {
 		return flushMode;
 	}
 
-	public String toString() {
-		return getClass().getName() + '(' + query + ')';
-	}
-
 	public Map getParameterTypes() {
+		// todo : currently these are never used...
 		return parameterTypes;
 	}
 
 	public String getQuery() {
 		return query;
 	}
 
 	public CacheMode getCacheMode() {
 		return cacheMode;
 	}
 
 	public boolean isReadOnly() {
 		return readOnly;
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
-	public Integer getLockTimeout() {
-		return lockTimeout;
+	public LockOptions getLockOptions() {
+		return lockOptions;
+	}
+
+	public Integer getFirstResult() {
+		return firstResult;
+	}
+
+	public Integer getMaxResults() {
+		return maxResults;
+	}
+
+	@Override
+	public String toString() {
+		return getClass().getName() + '(' + name + " [" + query + "])";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedQueryDefinitionBuilder.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedQueryDefinitionBuilder.java
new file mode 100644
index 0000000000..4a18d82290
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedQueryDefinitionBuilder.java
@@ -0,0 +1,143 @@
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
+package org.hibernate.engine.spi;
+
+import java.util.Map;
+
+import org.hibernate.CacheMode;
+import org.hibernate.FlushMode;
+import org.hibernate.LockOptions;
+
+public class NamedQueryDefinitionBuilder {
+	protected String name;
+	protected String query;
+	protected boolean cacheable;
+	protected String cacheRegion;
+	protected Integer timeout;
+	protected Integer fetchSize;
+	protected FlushMode flushMode;
+	protected CacheMode cacheMode;
+	protected boolean readOnly;
+	protected String comment;
+	protected Map parameterTypes;
+	protected LockOptions lockOptions;
+	protected Integer firstResult;
+	protected Integer maxResults;
+
+	public NamedQueryDefinitionBuilder() {
+	}
+
+	public NamedQueryDefinitionBuilder(String name) {
+		this.name = name;
+	}
+
+	public NamedQueryDefinitionBuilder setName(String name) {
+		this.name = name;
+		return this;
+	}
+
+	public NamedQueryDefinitionBuilder setQuery(String query) {
+		this.query = query;
+		return this;
+	}
+
+	public NamedQueryDefinitionBuilder setCacheable(boolean cacheable) {
+		this.cacheable = cacheable;
+		return this;
+	}
+
+	public NamedQueryDefinitionBuilder setCacheRegion(String cacheRegion) {
+		this.cacheRegion = cacheRegion;
+		return this;
+	}
+
+	public NamedQueryDefinitionBuilder setTimeout(Integer timeout) {
+		this.timeout = timeout;
+		return this;
+	}
+
+	public NamedQueryDefinitionBuilder setFetchSize(Integer fetchSize) {
+		this.fetchSize = fetchSize;
+		return this;
+	}
+
+	public NamedQueryDefinitionBuilder setFlushMode(FlushMode flushMode) {
+		this.flushMode = flushMode;
+		return this;
+	}
+
+	public NamedQueryDefinitionBuilder setCacheMode(CacheMode cacheMode) {
+		this.cacheMode = cacheMode;
+		return this;
+	}
+
+	public NamedQueryDefinitionBuilder setReadOnly(boolean readOnly) {
+		this.readOnly = readOnly;
+		return this;
+	}
+
+	public NamedQueryDefinitionBuilder setComment(String comment) {
+		this.comment = comment;
+		return this;
+	}
+
+	public NamedQueryDefinitionBuilder setParameterTypes(Map parameterTypes) {
+		this.parameterTypes = parameterTypes;
+		return this;
+	}
+
+	public NamedQueryDefinitionBuilder setLockOptions(LockOptions lockOptions) {
+		this.lockOptions = lockOptions;
+		return this;
+	}
+
+	public NamedQueryDefinitionBuilder setFirstResult(Integer firstResult) {
+		this.firstResult = firstResult;
+		return this;
+	}
+
+	public NamedQueryDefinitionBuilder setMaxResults(Integer maxResults) {
+		this.maxResults = maxResults;
+		return this;
+	}
+
+	public NamedQueryDefinition createNamedQueryDefinition() {
+		return new NamedQueryDefinition(
+				name,
+				query,
+				cacheable,
+				cacheRegion,
+				timeout,
+				lockOptions,
+				fetchSize,
+				flushMode,
+				cacheMode,
+				readOnly,
+				comment,
+				parameterTypes,
+				firstResult,
+				maxResults
+		);
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinition.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinition.java
index 2edc621a29..a77bfa9daf 100755
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinition.java
@@ -1,213 +1,219 @@
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
 
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 
 /**
  * Definition of a named native SQL query, defined in the mapping metadata.
  * 
  * @author Max Andersen
+ * @author Steve Ebersole
  */
 public class NamedSQLQueryDefinition extends NamedQueryDefinition {
 
 	private NativeSQLQueryReturn[] queryReturns;
 	private final List<String> querySpaces;
 	private final boolean callable;
 	private String resultSetRef;
 
 	/**
-	 * This form used to construct a NamedSQLQueryDefinition from the binder
-	 * code when a the result-set mapping information is explicitly
-	 * provided in the query definition (i.e., no resultset-mapping used)
+	 * This form was initially used to construct a NamedSQLQueryDefinition from the binder code when a the
+	 * result-set mapping information is not explicitly  provided in the query definition
+	 * (i.e., no resultset-mapping used).
 	 *
 	 * @param name The name of named query
 	 * @param query The sql query string
 	 * @param queryReturns The in-lined query return definitions
 	 * @param querySpaces Any specified query spaces (used for auto-flushing)
 	 * @param cacheable Whether the query results are cacheable
 	 * @param cacheRegion If cacheable, the region into which to store the results
 	 * @param timeout A JDBC-level timeout to be applied
 	 * @param fetchSize A JDBC-level fetch-size to be applied
 	 * @param flushMode The flush mode to use for this query
 	 * @param cacheMode The cache mode to use during execution and subsequent result loading
 	 * @param readOnly Whether returned entities should be marked as read-only in the session
 	 * @param comment Any sql comment to be applied to the query
 	 * @param parameterTypes parameter type map
 	 * @param callable Does the query string represent a callable object (i.e., proc)
+	 *
+	 * @deprecated Use {@link NamedSQLQueryDefinitionBuilder} instead.
 	 */
+	@Deprecated
 	public NamedSQLQueryDefinition(
 			String name,
 			String query,
 			NativeSQLQueryReturn[] queryReturns,
 			List<String> querySpaces,
 			boolean cacheable,
 			String cacheRegion,
 			Integer timeout,
 			Integer fetchSize,
 			FlushMode flushMode,
 			CacheMode cacheMode,
 			boolean readOnly,
 			String comment,
 			Map parameterTypes,
 			boolean callable) {
-		super(
+		this(
 				name,
-				query.trim(), /* trim done to workaround stupid oracle bug that cant handle whitespaces before a { in a sp */
+				query,
 				cacheable,
 				cacheRegion,
 				timeout,
 				fetchSize,
 				flushMode,
 				cacheMode,
 				readOnly,
 				comment,
-				parameterTypes
+				parameterTypes,
+				null,		// firstResult
+				null,		// maxResults
+				null, 		// resultSetRef
+				querySpaces,
+				callable,
+				queryReturns
 		);
-		this.queryReturns = queryReturns;
-		this.querySpaces = querySpaces;
-		this.callable = callable;
 	}
 
 	/**
-	 * This form used to construct a NamedSQLQueryDefinition from the binder
-	 * code when a resultset-mapping reference is used.
+	 * This form was initially used to construct a NamedSQLQueryDefinition from the binder code when a
+	 * resultset-mapping reference is used.
 	 *
 	 * @param name The name of named query
 	 * @param query The sql query string
 	 * @param resultSetRef The resultset-mapping name
 	 * @param querySpaces Any specified query spaces (used for auto-flushing)
 	 * @param cacheable Whether the query results are cacheable
 	 * @param cacheRegion If cacheable, the region into which to store the results
 	 * @param timeout A JDBC-level timeout to be applied
 	 * @param fetchSize A JDBC-level fetch-size to be applied
 	 * @param flushMode The flush mode to use for this query
 	 * @param cacheMode The cache mode to use during execution and subsequent result loading
 	 * @param readOnly Whether returned entities should be marked as read-only in the session
 	 * @param comment Any sql comment to be applied to the query
 	 * @param parameterTypes parameter type map
 	 * @param callable Does the query string represent a callable object (i.e., proc)
+	 *
+	 * @deprecated Use {@link NamedSQLQueryDefinitionBuilder} instead.
 	 */
+	@Deprecated
 	public NamedSQLQueryDefinition(
 			String name,
 			String query,
 			String resultSetRef,
 			List<String> querySpaces,
 			boolean cacheable,
 			String cacheRegion,
 			Integer timeout,
 			Integer fetchSize,
 			FlushMode flushMode,
 			CacheMode cacheMode,
 			boolean readOnly,
 			String comment,
 			Map parameterTypes,
 			boolean callable) {
-		super(
+
+		this(
 				name,
-				query.trim(), /* trim done to workaround stupid oracle bug that cant handle whitespaces before a { in a sp */
+				query,
 				cacheable,
 				cacheRegion,
 				timeout,
 				fetchSize,
 				flushMode,
 				cacheMode,
 				readOnly,
 				comment,
-				parameterTypes
+				parameterTypes,
+				null,		// firstResult
+				null,		// maxResults
+				resultSetRef,
+				querySpaces,
+				callable,
+				null		// queryReturns
 		);
-		this.resultSetRef = resultSetRef;
-		this.querySpaces = querySpaces;
-		this.callable = callable;
 	}
 
-	/**
-	 * This form used from annotations (?).  Essentially the same as the above using a
-	 * resultset-mapping reference, but without cacheMode, readOnly, and comment.
-	 *
-	 * FIXME: annotations do not use it, so it can be remove from my POV
-	 * @deprecated
-	 *
-	 *
-	 * @param query The sql query string
-	 * @param resultSetRef The result-set-mapping name
-	 * @param querySpaces Any specified query spaces (used for auto-flushing)
-	 * @param cacheable Whether the query results are cacheable
-	 * @param cacheRegion If cacheable, the region into which to store the results
-	 * @param timeout A JDBC-level timeout to be applied
-	 * @param fetchSize A JDBC-level fetch-size to be applied
-	 * @param flushMode The flush mode to use for this query
-	 * @param parameterTypes parameter type map
-	 * @param callable Does the query string represent a callable object (i.e., proc)
-	 */
-	public NamedSQLQueryDefinition(
+	NamedSQLQueryDefinition(
+			String name,
 			String query,
-			String resultSetRef,
-			List<String> querySpaces,
 			boolean cacheable,
 			String cacheRegion,
 			Integer timeout,
 			Integer fetchSize,
 			FlushMode flushMode,
+			CacheMode cacheMode,
+			boolean readOnly,
+			String comment,
 			Map parameterTypes,
-			boolean callable) {
-		this(
-				null,
-				query,
-				resultSetRef,
-				querySpaces,
+			Integer firstResult,
+			Integer maxResults,
+			String resultSetRef,
+			List<String> querySpaces,
+			boolean callable,
+			NativeSQLQueryReturn[] queryReturns) {
+		super(
+				name,
+				query.trim(), /* trim done to workaround stupid oracle bug that cant handle whitespaces before a { in a sp */
 				cacheable,
 				cacheRegion,
 				timeout,
+				null,		// lockOptions
 				fetchSize,
 				flushMode,
-				null,
-				false,
-				null,
+				cacheMode,
+				readOnly,
+				comment,
 				parameterTypes,
-				callable
+				firstResult,
+				maxResults
 		);
+		this.resultSetRef = resultSetRef;
+		this.querySpaces = querySpaces;
+		this.callable = callable;
+		this.queryReturns = queryReturns;
 	}
 
 	public NativeSQLQueryReturn[] getQueryReturns() {
 		return queryReturns;
 	}
 
 	public List<String> getQuerySpaces() {
 		return querySpaces;
 	}
 
 	public boolean isCallable() {
 		return callable;
 	}
 
 	public String getResultSetRef() {
 		return resultSetRef;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinitionBuilder.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinitionBuilder.java
new file mode 100644
index 0000000000..9917c9b889
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinitionBuilder.java
@@ -0,0 +1,184 @@
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
+package org.hibernate.engine.spi;
+
+import java.util.ArrayList;
+import java.util.Collection;
+import java.util.List;
+import java.util.Map;
+
+import org.hibernate.CacheMode;
+import org.hibernate.FlushMode;
+import org.hibernate.LockOptions;
+import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
+
+public class NamedSQLQueryDefinitionBuilder extends NamedQueryDefinitionBuilder {
+	private NativeSQLQueryReturn[] queryReturns;
+	private Collection<String> querySpaces;
+	private boolean callable;
+	private String resultSetRef;
+
+	public NamedSQLQueryDefinitionBuilder() {
+	}
+
+	public NamedSQLQueryDefinitionBuilder(String name) {
+		super( name );
+	}
+
+	public NamedSQLQueryDefinitionBuilder setQueryReturns(NativeSQLQueryReturn[] queryReturns) {
+		this.queryReturns = queryReturns;
+		return this;
+	}
+
+	public NamedSQLQueryDefinitionBuilder setQueryReturns(List<NativeSQLQueryReturn> queryReturns) {
+		if ( queryReturns != null ) {
+			this.queryReturns = queryReturns.toArray( new NativeSQLQueryReturn[ queryReturns.size() ] );
+		}
+		else {
+			this.queryReturns = null;
+		}
+		return this;
+	}
+
+	public NamedSQLQueryDefinitionBuilder setQuerySpaces(List<String> querySpaces) {
+		this.querySpaces = querySpaces;
+		return this;
+	}
+
+	public NamedSQLQueryDefinitionBuilder setQuerySpaces(Collection<String> synchronizedQuerySpaces) {
+		this.querySpaces = synchronizedQuerySpaces;
+		return this;
+	}
+
+	public NamedSQLQueryDefinitionBuilder setResultSetRef(String resultSetRef) {
+		this.resultSetRef = resultSetRef;
+		return this;
+	}
+
+	public NamedSQLQueryDefinitionBuilder setCallable(boolean callable) {
+		this.callable = callable;
+		return this;
+	}
+
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setName(String name) {
+		return (NamedSQLQueryDefinitionBuilder) super.setName( name );
+	}
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setQuery(String query) {
+		return (NamedSQLQueryDefinitionBuilder) super.setQuery( query );
+	}
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setCacheable(boolean cacheable) {
+		return (NamedSQLQueryDefinitionBuilder) super.setCacheable( cacheable );
+	}
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setCacheRegion(String cacheRegion) {
+		return (NamedSQLQueryDefinitionBuilder) super.setCacheRegion( cacheRegion );
+	}
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setTimeout(Integer timeout) {
+		return (NamedSQLQueryDefinitionBuilder) super.setTimeout( timeout );
+	}
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setFetchSize(Integer fetchSize) {
+		return (NamedSQLQueryDefinitionBuilder) super.setFetchSize( fetchSize );
+	}
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setFlushMode(FlushMode flushMode) {
+		return (NamedSQLQueryDefinitionBuilder) super.setFlushMode( flushMode );
+	}
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setCacheMode(CacheMode cacheMode) {
+		return (NamedSQLQueryDefinitionBuilder) super.setCacheMode( cacheMode );
+	}
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setReadOnly(boolean readOnly) {
+		return (NamedSQLQueryDefinitionBuilder) super.setReadOnly( readOnly );
+	}
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setComment(String comment) {
+		return (NamedSQLQueryDefinitionBuilder) super.setComment( comment );
+	}
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setParameterTypes(Map parameterTypes) {
+		return (NamedSQLQueryDefinitionBuilder) super.setParameterTypes( parameterTypes );
+	}
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setLockOptions(LockOptions lockOptions) {
+		// todo : maybe throw an exception here instead? since this is not valid for native-0sql queries?
+		return (NamedSQLQueryDefinitionBuilder) super.setLockOptions( lockOptions );
+	}
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setFirstResult(Integer firstResult) {
+		return (NamedSQLQueryDefinitionBuilder) super.setFirstResult( firstResult );
+	}
+
+	@Override
+	public NamedSQLQueryDefinitionBuilder setMaxResults(Integer maxResults) {
+		return (NamedSQLQueryDefinitionBuilder) super.setMaxResults( maxResults );
+	}
+
+	@Override
+	public NamedSQLQueryDefinition createNamedQueryDefinition() {
+		return new NamedSQLQueryDefinition(
+				name,
+				query,
+				cacheable,
+				cacheRegion,
+				timeout,
+				fetchSize,
+				flushMode,
+				cacheMode,
+				readOnly,
+				comment,
+				parameterTypes,
+				firstResult,
+				maxResults,
+				resultSetRef,
+				querySpacesCopy(),
+				callable,
+				queryReturns
+		);
+	}
+
+	private List<String> querySpacesCopy() {
+		return querySpaces == null
+				? null
+				: new ArrayList<String>( querySpaces );
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
index 71d7d59140..bbc38adc99 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
@@ -1,272 +1,278 @@
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
 
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.Session;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cfg.Settings;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Defines the internal contract between the <tt>SessionFactory</tt> and other parts of
  * Hibernate such as implementors of <tt>Type</tt>.
  *
  * @see org.hibernate.SessionFactory
  * @see org.hibernate.internal.SessionFactoryImpl
  * @author Gavin King
  */
 public interface SessionFactoryImplementor extends Mapping, SessionFactory {
 	@Override
 	public SessionBuilderImplementor withOptions();
 
 	/**
 	 * Retrieve the {@link Type} resolver associated with this factory.
 	 *
 	 * @return The type resolver
 	 */
 	public TypeResolver getTypeResolver();
 
 	/**
 	 * Get a copy of the Properties used to configure this session factory.
 	 *
 	 * @return The properties.
 	 */
 	public Properties getProperties();
 
 	/**
 	 * Get the persister for the named entity
 	 *
 	 * @param entityName The name of the entity for which to retrieve the persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that name.
 	 */
 	public EntityPersister getEntityPersister(String entityName) throws MappingException;
 
 	/**
 	 * Get all entity persisters as a Map, which entity name its the key and the persister is the value.
 	 *
 	 * @return The Map contains all entity persisters.
 	 */
 	public Map<String,EntityPersister> getEntityPersisters();
 
 	/**
 	 * Get the persister object for a collection role.
 	 *
 	 * @param role The role (name) of the collection for which to retrieve the
 	 * persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that role.
 	 */
 	public CollectionPersister getCollectionPersister(String role) throws MappingException;
 
 	/**
 	 * Get all collection persisters as a Map, which collection role as the key and the persister is the value.
 	 *
 	 * @return The Map contains all collection persisters.
 	 */
 	public Map<String, CollectionPersister> getCollectionPersisters();
 
 	/**
 	 * Get the JdbcServices.
 	 * @return the JdbcServices
 	 */
 	public JdbcServices getJdbcServices();
 
 	/**
 	 * Get the SQL dialect.
 	 * <p/>
 	 * Shorthand for {@code getJdbcServices().getDialect()}
 	 *
 	 * @return The dialect
 	 */
 	public Dialect getDialect();
 
 	/**
 	 * Get the factory scoped interceptor for this factory.
 	 *
 	 * @return The factory scope interceptor, or null if none.
 	 */
 	public Interceptor getInterceptor();
 
 	public QueryPlanCache getQueryPlanCache();
 
 	/**
 	 * Get the return types of a query
 	 */
 	public Type[] getReturnTypes(String queryString) throws HibernateException;
 
 	/**
 	 * Get the return aliases of a query
 	 */
 	public String[] getReturnAliases(String queryString) throws HibernateException;
 
 	/**
 	 * Get the connection provider
 	 */
 	public ConnectionProvider getConnectionProvider();
 	/**
 	 * Get the names of all persistent classes that implement/extend the given interface/class
 	 */
 	public String[] getImplementors(String className) throws MappingException;
 	/**
 	 * Get a class name, using query language imports
 	 */
 	public String getImportedClassName(String name);
 
 	/**
 	 * Get the default query cache
 	 */
 	public QueryCache getQueryCache();
 	/**
 	 * Get a particular named query cache, or the default cache
 	 * @param regionName the name of the cache region, or null for the default query cache
 	 * @return the existing cache, or a newly created cache if none by that region name
 	 */
 	public QueryCache getQueryCache(String regionName) throws HibernateException;
 
 	/**
 	 * Get the cache of table update timestamps
 	 */
 	public UpdateTimestampsCache getUpdateTimestampsCache();
 	/**
 	 * Statistics SPI
 	 */
 	public StatisticsImplementor getStatisticsImplementor();
 
 	public NamedQueryDefinition getNamedQuery(String queryName);
+
+	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition);
+
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName);
+
+	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition);
+
 	public ResultSetMappingDefinition getResultSetMapping(String name);
 
 	/**
 	 * Get the identifier generator for the hierarchy
 	 */
 	public IdentifierGenerator getIdentifierGenerator(String rootEntityName);
 
 	/**
 	 * Get a named second-level cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	public Region getSecondLevelCacheRegion(String regionName);
 	
 	/**
 	 * Get a named naturalId cache region
 	 *
 	 * @param regionName The name of the region to retrieve.
 	 * @return The region
 	 */
 	public Region getNaturalIdCacheRegion(String regionName);
 
 	/**
 	 * Get a map of all the second level cache regions currently maintained in
 	 * this session factory.  The map is structured with the region name as the
 	 * key and the {@link Region} instances as the values.
 	 *
 	 * @return The map of regions
 	 */
 	public Map getAllSecondLevelCacheRegions();
 
 	/**
 	 * Retrieves the SQLExceptionConverter in effect for this SessionFactory.
 	 *
 	 * @return The SQLExceptionConverter for this SessionFactory.
 	 *
 	 */
 	public SQLExceptionConverter getSQLExceptionConverter();
 	   // TODO: deprecate???
 
 	/**
 	 * Retrieves the SqlExceptionHelper in effect for this SessionFactory.
 	 *
 	 * @return The SqlExceptionHelper for this SessionFactory.
 	 *
 	 */
     public SqlExceptionHelper getSQLExceptionHelper();
 
 	public Settings getSettings();
 
 	/**
 	 * Get a nontransactional "current" session for Hibernate EntityManager
 	 */
 	public Session openTemporarySession() throws HibernateException;
 
 	/**
 	 * Retrieves a set of all the collection roles in which the given entity
 	 * is a participant, as either an index or an element.
 	 *
 	 * @param entityName The entity name for which to get the collection roles.
 	 * @return set of all the collection roles in which the given entityName participates.
 	 */
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName);
 
 	public EntityNotFoundDelegate getEntityNotFoundDelegate();
 
 	public SQLFunctionRegistry getSqlFunctionRegistry();
 
 	/**
 	 * Retrieve fetch profile by name.
 	 *
 	 * @param name The name of the profile to retrieve.
 	 * @return The profile definition
 	 */
 	public FetchProfile getFetchProfile(String name);
 
 	public ServiceRegistryImplementor getServiceRegistry();
 
 	public void addObserver(SessionFactoryObserver observer);
 
 	public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();
 
 	public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
index cbb121a62d..432adf922d 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
@@ -1,958 +1,1007 @@
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
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Calendar;
 import java.util.Collection;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueResultException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.Query;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.engine.query.spi.ParameterMetadata;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.hql.internal.classic.ParserHelper;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.property.Getter;
 import org.hibernate.proxy.HibernateProxyHelper;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.SerializableType;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * Abstract implementation of the Query interface.
  *
  * @author Gavin King
  * @author Max Andersen
  */
 public abstract class AbstractQueryImpl implements Query {
 
 	private static final Object UNSET_PARAMETER = new MarkerObject("<unset parameter>");
 	private static final Object UNSET_TYPE = new MarkerObject("<unset type>");
 
 	private final String queryString;
 	protected final SessionImplementor session;
 	protected final ParameterMetadata parameterMetadata;
 
 	// parameter bind values...
 	private List values = new ArrayList(4);
 	private List types = new ArrayList(4);
 	private Map<String,TypedValue> namedParameters = new HashMap<String, TypedValue>(4);
 	private Map namedParameterLists = new HashMap(4);
 
 	private Object optionalObject;
 	private Serializable optionalId;
 	private String optionalEntityName;
 
 	private RowSelection selection;
 	private boolean cacheable;
 	private String cacheRegion;
 	private String comment;
 	private FlushMode flushMode;
 	private CacheMode cacheMode;
 	private FlushMode sessionFlushMode;
 	private CacheMode sessionCacheMode;
 	private Serializable collectionKey;
 	private Boolean readOnly;
 	private ResultTransformer resultTransformer;
 
 	public AbstractQueryImpl(
 			String queryString,
 	        FlushMode flushMode,
 	        SessionImplementor session,
 	        ParameterMetadata parameterMetadata) {
 		this.session = session;
 		this.queryString = queryString;
 		this.selection = new RowSelection();
 		this.flushMode = flushMode;
 		this.cacheMode = null;
 		this.parameterMetadata = parameterMetadata;
 	}
 
 	public ParameterMetadata getParameterMetadata() {
 		return parameterMetadata;
 	}
 
+	@Override
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + queryString + ')';
 	}
 
+	@Override
 	public final String getQueryString() {
 		return queryString;
 	}
 
-	//TODO: maybe call it getRowSelection() ?
-	public RowSelection getSelection() {
-		return selection;
+	@Override
+	public boolean isCacheable() {
+		return cacheable;
 	}
-	
-	public Query setFlushMode(FlushMode flushMode) {
-		this.flushMode = flushMode;
+
+	@Override
+	public Query setCacheable(boolean cacheable) {
+		this.cacheable = cacheable;
 		return this;
 	}
-	
-	public Query setCacheMode(CacheMode cacheMode) {
-		this.cacheMode = cacheMode;
+
+	@Override
+	public String getCacheRegion() {
+		return cacheRegion;
+	}
+
+	@Override
+	public Query setCacheRegion(String cacheRegion) {
+		if (cacheRegion != null) {
+			this.cacheRegion = cacheRegion.trim();
+		}
 		return this;
 	}
 
+	@Override
+	public FlushMode getFlushMode() {
+		return flushMode;
+	}
+
+	@Override
+	public Query setFlushMode(FlushMode flushMode) {
+		this.flushMode = flushMode;
+		return this;
+	}
+
+	@Override
 	public CacheMode getCacheMode() {
 		return cacheMode;
 	}
 
-	public Query setCacheable(boolean cacheable) {
-		this.cacheable = cacheable;
+	@Override
+	public Query setCacheMode(CacheMode cacheMode) {
+		this.cacheMode = cacheMode;
 		return this;
 	}
 
-	public Query setCacheRegion(String cacheRegion) {
-		if (cacheRegion != null)
-			this.cacheRegion = cacheRegion.trim();
-		return this;
+	@Override
+	public String getComment() {
+		return comment;
 	}
 
+	@Override
 	public Query setComment(String comment) {
 		this.comment = comment;
 		return this;
 	}
 
+	@Override
+	public Integer getFirstResult() {
+		return selection.getFirstRow();
+	}
+
+	@Override
 	public Query setFirstResult(int firstResult) {
 		selection.setFirstRow( firstResult);
 		return this;
 	}
 
+	@Override
+	public Integer getMaxResults() {
+		return selection.getMaxRows();
+	}
+
+	@Override
 	public Query setMaxResults(int maxResults) {
 		if ( maxResults < 0 ) {
 			// treat negatives specically as meaning no limit...
 			selection.setMaxRows( null );
 		}
 		else {
 			selection.setMaxRows( maxResults);
 		}
 		return this;
 	}
 
+	@Override
+	public Integer getTimeout() {
+		return selection.getTimeout();
+	}
+
+	@Override
 	public Query setTimeout(int timeout) {
 		selection.setTimeout( timeout);
 		return this;
 	}
+
+	@Override
+	public Integer getFetchSize() {
+		return selection.getFetchSize();
+	}
+
+	@Override
 	public Query setFetchSize(int fetchSize) {
 		selection.setFetchSize( fetchSize);
 		return this;
 	}
 
 	public Type[] getReturnTypes() throws HibernateException {
 		return session.getFactory().getReturnTypes( queryString );
 	}
 
 	public String[] getReturnAliases() throws HibernateException {
 		return session.getFactory().getReturnAliases( queryString );
 	}
 
 	public Query setCollectionKey(Serializable collectionKey) {
 		this.collectionKey = collectionKey;
 		return this;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isReadOnly() {
 		return ( readOnly == null ?
 				getSession().getPersistenceContext().isDefaultReadOnly() :
 				readOnly.booleanValue() 
 		);
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Query setReadOnly(boolean readOnly) {
 		this.readOnly = Boolean.valueOf( readOnly );
 		return this;
 	}
 
 	public Query setResultTransformer(ResultTransformer transformer) {
 		this.resultTransformer = transformer;
 		return this;
 	}
 	
 	public void setOptionalEntityName(String optionalEntityName) {
 		this.optionalEntityName = optionalEntityName;
 	}
 
 	public void setOptionalId(Serializable optionalId) {
 		this.optionalId = optionalId;
 	}
 
 	public void setOptionalObject(Object optionalObject) {
 		this.optionalObject = optionalObject;
 	}
 
 	SessionImplementor getSession() {
 		return session;
 	}
 
 	public abstract LockOptions getLockOptions();
 
 
 	// Parameter handling code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Returns a shallow copy of the named parameter value map.
 	 *
 	 * @return Shallow copy of the named parameter value map
 	 */
 	protected Map getNamedParams() {
 		return new HashMap( namedParameters );
 	}
 
 	/**
 	 * Returns an array representing all named parameter names encountered
 	 * during (intial) parsing of the query.
 	 * <p/>
 	 * Note <i>initial</i> here means different things depending on whether
 	 * this is a native-sql query or an HQL/filter query.  For native-sql, a
 	 * precursory inspection of the query string is performed specifically to
 	 * locate defined parameters.  For HQL/filter queries, this is the
 	 * information returned from the query-translator.  This distinction
 	 * holds true for all parameter metadata exposed here.
 	 *
 	 * @return Array of named parameter names.
 	 * @throws HibernateException
 	 */
 	public String[] getNamedParameters() throws HibernateException {
 		return ArrayHelper.toStringArray( parameterMetadata.getNamedParameterNames() );
 	}
 
 	/**
 	 * Does this query contain named parameters?
 	 *
 	 * @return True if the query was found to contain named parameters; false
 	 * otherwise;
 	 */
 	public boolean hasNamedParameters() {
 		return parameterMetadata.getNamedParameterNames().size() > 0;
 	}
 
 	/**
 	 * Retreive the value map for any named parameter lists (i.e., for
 	 * auto-expansion) bound to this query.
 	 *
 	 * @return The parameter list value map.
 	 */
 	protected Map getNamedParameterLists() {
 		return namedParameterLists;
 	}
 
 	/**
 	 * Retreives the list of parameter values bound to this query for
 	 * ordinal parameters.
 	 *
 	 * @return The ordinal parameter values.
 	 */
 	protected List getValues() {
 		return values;
 	}
 
 	/**
 	 * Retreives the list of parameter {@link Type type}s bound to this query for
 	 * ordinal parameters.
 	 *
 	 * @return The ordinal parameter types.
 	 */
 	protected List getTypes() {
 		return types;
 	}
 
 	/**
 	 * Perform parameter validation.  Used prior to executing the encapsulated
 	 * query.
 	 *
 	 * @throws QueryException
 	 */
 	protected void verifyParameters() throws QueryException {
 		verifyParameters(false);
 	}
 
 	/**
 	 * Perform parameter validation.  Used prior to executing the encapsulated
 	 * query.
 	 *
 	 * @param reserveFirstParameter if true, the first ? will not be verified since
 	 * its needed for e.g. callable statements returning a out parameter
 	 * @throws HibernateException
 	 */
 	protected void verifyParameters(boolean reserveFirstParameter) throws HibernateException {
 		if ( parameterMetadata.getNamedParameterNames().size() != namedParameters.size() + namedParameterLists.size() ) {
 			Set missingParams = new HashSet( parameterMetadata.getNamedParameterNames() );
 			missingParams.removeAll( namedParameterLists.keySet() );
 			missingParams.removeAll( namedParameters.keySet() );
 			throw new QueryException( "Not all named parameters have been set: " + missingParams, getQueryString() );
 		}
 
 		int positionalValueSpan = 0;
 		for ( int i = 0; i < values.size(); i++ ) {
 			Object object = types.get( i );
 			if( values.get( i ) == UNSET_PARAMETER || object == UNSET_TYPE ) {
 				if ( reserveFirstParameter && i==0 ) {
 					continue;
 				}
 				else {
 					throw new QueryException( "Unset positional parameter at position: " + i, getQueryString() );
 				}
 			}
 			positionalValueSpan += ( (Type) object ).getColumnSpan( session.getFactory() );
 		}
 
 		if ( parameterMetadata.getOrdinalParameterCount() != positionalValueSpan ) {
 			if ( reserveFirstParameter && parameterMetadata.getOrdinalParameterCount() - 1 != positionalValueSpan ) {
 				throw new QueryException(
 				 		"Expected positional parameter count: " +
 				 		(parameterMetadata.getOrdinalParameterCount()-1) +
 				 		", actual parameters: " +
 				 		values,
 				 		getQueryString()
 				 	);
 			}
 			else if ( !reserveFirstParameter ) {
 				throw new QueryException(
 				 		"Expected positional parameter count: " +
 				 		parameterMetadata.getOrdinalParameterCount() +
 				 		", actual parameters: " +
 				 		values,
 				 		getQueryString()
 				 	);
 			}
 		}
 	}
 
 	public Query setParameter(int position, Object val, Type type) {
 		if ( parameterMetadata.getOrdinalParameterCount() == 0 ) {
 			throw new IllegalArgumentException("No positional parameters in query: " + getQueryString() );
 		}
 		if ( position < 0 || position > parameterMetadata.getOrdinalParameterCount() - 1 ) {
 			throw new IllegalArgumentException("Positional parameter does not exist: " + position + " in query: " + getQueryString() );
 		}
 		int size = values.size();
 		if ( position < size ) {
 			values.set( position, val );
 			types.set( position, type );
 		}
 		else {
 			// prepend value and type list with null for any positions before the wanted position.
 			for ( int i = 0; i < position - size; i++ ) {
 				values.add( UNSET_PARAMETER );
 				types.add( UNSET_TYPE );
 			}
 			values.add( val );
 			types.add( type );
 		}
 		return this;
 	}
 
 	public Query setParameter(String name, Object val, Type type) {
 		if ( !parameterMetadata.getNamedParameterNames().contains( name ) ) {
 			throw new IllegalArgumentException("Parameter " + name + " does not exist as a named parameter in [" + getQueryString() + "]");
 		}
 		else {
 			 namedParameters.put( name, new TypedValue( type, val  ) );
 			 return this;
 		}
 	}
 
 	public Query setParameter(int position, Object val) throws HibernateException {
 		if (val == null) {
 			setParameter( position, val, StandardBasicTypes.SERIALIZABLE );
 		}
 		else {
 			setParameter( position, val, determineType( position, val ) );
 		}
 		return this;
 	}
 
 	public Query setParameter(String name, Object val) throws HibernateException {
 		if (val == null) {
 			Type type = parameterMetadata.getNamedParameterExpectedType( name );
 			if ( type == null ) {
 				type = StandardBasicTypes.SERIALIZABLE;
 			}
 			setParameter( name, val, type );
 		}
 		else {
 			setParameter( name, val, determineType( name, val ) );
 		}
 		return this;
 	}
 
 	protected Type determineType(int paramPosition, Object paramValue, Type defaultType) {
 		Type type = parameterMetadata.getOrdinalParameterExpectedType( paramPosition + 1 );
 		if ( type == null ) {
 			type = defaultType;
 		}
 		return type;
 	}
 
 	protected Type determineType(int paramPosition, Object paramValue) throws HibernateException {
 		Type type = parameterMetadata.getOrdinalParameterExpectedType( paramPosition + 1 );
 		if ( type == null ) {
 			type = guessType( paramValue );
 		}
 		return type;
 	}
 
 	protected Type determineType(String paramName, Object paramValue, Type defaultType) {
 		Type type = parameterMetadata.getNamedParameterExpectedType( paramName );
 		if ( type == null ) {
 			type = defaultType;
 		}
 		return type;
 	}
 
 	protected Type determineType(String paramName, Object paramValue) throws HibernateException {
 		Type type = parameterMetadata.getNamedParameterExpectedType( paramName );
 		if ( type == null ) {
 			type = guessType( paramValue );
 		}
 		return type;
 	}
 
 	protected Type determineType(String paramName, Class clazz) throws HibernateException {
 		Type type = parameterMetadata.getNamedParameterExpectedType( paramName );
 		if ( type == null ) {
 			type = guessType( clazz );
 		}
 		return type;
 	}
 
 	private Type guessType(Object param) throws HibernateException {
 		Class clazz = HibernateProxyHelper.getClassWithoutInitializingProxy( param );
 		return guessType( clazz );
 	}
 
 	private Type guessType(Class clazz) throws HibernateException {
 		String typename = clazz.getName();
 		Type type = session.getFactory().getTypeResolver().heuristicType(typename);
 		boolean serializable = type!=null && type instanceof SerializableType;
 		if (type==null || serializable) {
 			try {
 				session.getFactory().getEntityPersister( clazz.getName() );
 			}
 			catch (MappingException me) {
 				if (serializable) {
 					return type;
 				}
 				else {
 					throw new HibernateException("Could not determine a type for class: " + typename);
 				}
 			}
 			return ( (Session) session ).getTypeHelper().entity( clazz );
 		}
 		else {
 			return type;
 		}
 	}
 
 	public Query setString(int position, String val) {
 		setParameter(position, val, StandardBasicTypes.STRING);
 		return this;
 	}
 
 	public Query setCharacter(int position, char val) {
 		setParameter(position, new Character(val), StandardBasicTypes.CHARACTER);
 		return this;
 	}
 
 	public Query setBoolean(int position, boolean val) {
 		Boolean valueToUse = val;
 		Type typeToUse = determineType( position, valueToUse, StandardBasicTypes.BOOLEAN );
 		setParameter( position, valueToUse, typeToUse );
 		return this;
 	}
 
 	public Query setByte(int position, byte val) {
 		setParameter(position, val, StandardBasicTypes.BYTE);
 		return this;
 	}
 
 	public Query setShort(int position, short val) {
 		setParameter(position, val, StandardBasicTypes.SHORT);
 		return this;
 	}
 
 	public Query setInteger(int position, int val) {
 		setParameter(position, val, StandardBasicTypes.INTEGER);
 		return this;
 	}
 
 	public Query setLong(int position, long val) {
 		setParameter(position, val, StandardBasicTypes.LONG);
 		return this;
 	}
 
 	public Query setFloat(int position, float val) {
 		setParameter(position, val, StandardBasicTypes.FLOAT);
 		return this;
 	}
 
 	public Query setDouble(int position, double val) {
 		setParameter(position, val, StandardBasicTypes.DOUBLE);
 		return this;
 	}
 
 	public Query setBinary(int position, byte[] val) {
 		setParameter(position, val, StandardBasicTypes.BINARY);
 		return this;
 	}
 
 	public Query setText(int position, String val) {
 		setParameter(position, val, StandardBasicTypes.TEXT);
 		return this;
 	}
 
 	public Query setSerializable(int position, Serializable val) {
 		setParameter(position, val, StandardBasicTypes.SERIALIZABLE);
 		return this;
 	}
 
 	public Query setDate(int position, Date date) {
 		setParameter(position, date, StandardBasicTypes.DATE);
 		return this;
 	}
 
 	public Query setTime(int position, Date date) {
 		setParameter(position, date, StandardBasicTypes.TIME);
 		return this;
 	}
 
 	public Query setTimestamp(int position, Date date) {
 		setParameter(position, date, StandardBasicTypes.TIMESTAMP);
 		return this;
 	}
 
 	public Query setEntity(int position, Object val) {
 		setParameter( position, val, ( (Session) session ).getTypeHelper().entity( resolveEntityName( val ) ) );
 		return this;
 	}
 
 	private String resolveEntityName(Object val) {
 		if ( val == null ) {
 			throw new IllegalArgumentException( "entity for parameter binding cannot be null" );
 		}
 		return session.bestGuessEntityName( val );
 	}
 
 	public Query setLocale(int position, Locale locale) {
 		setParameter(position, locale, StandardBasicTypes.LOCALE);
 		return this;
 	}
 
 	public Query setCalendar(int position, Calendar calendar) {
 		setParameter(position, calendar, StandardBasicTypes.CALENDAR);
 		return this;
 	}
 
 	public Query setCalendarDate(int position, Calendar calendar) {
 		setParameter(position, calendar, StandardBasicTypes.CALENDAR_DATE);
 		return this;
 	}
 
 	public Query setBinary(String name, byte[] val) {
 		setParameter(name, val, StandardBasicTypes.BINARY);
 		return this;
 	}
 
 	public Query setText(String name, String val) {
 		setParameter(name, val, StandardBasicTypes.TEXT);
 		return this;
 	}
 
 	public Query setBoolean(String name, boolean val) {
 		Boolean valueToUse = val;
 		Type typeToUse = determineType( name, valueToUse, StandardBasicTypes.BOOLEAN );
 		setParameter( name, valueToUse, typeToUse );
 		return this;
 	}
 
 	public Query setByte(String name, byte val) {
 		setParameter(name, val, StandardBasicTypes.BYTE);
 		return this;
 	}
 
 	public Query setCharacter(String name, char val) {
 		setParameter(name, val, StandardBasicTypes.CHARACTER);
 		return this;
 	}
 
 	public Query setDate(String name, Date date) {
 		setParameter(name, date, StandardBasicTypes.DATE);
 		return this;
 	}
 
 	public Query setDouble(String name, double val) {
 		setParameter(name, val, StandardBasicTypes.DOUBLE);
 		return this;
 	}
 
 	public Query setEntity(String name, Object val) {
 		setParameter( name, val, ( (Session) session ).getTypeHelper().entity( resolveEntityName( val ) ) );
 		return this;
 	}
 
 	public Query setFloat(String name, float val) {
 		setParameter(name, val, StandardBasicTypes.FLOAT);
 		return this;
 	}
 
 	public Query setInteger(String name, int val) {
 		setParameter(name, val, StandardBasicTypes.INTEGER);
 		return this;
 	}
 
 	public Query setLocale(String name, Locale locale) {
 		setParameter(name, locale, StandardBasicTypes.LOCALE);
 		return this;
 	}
 
 	public Query setCalendar(String name, Calendar calendar) {
 		setParameter(name, calendar, StandardBasicTypes.CALENDAR);
 		return this;
 	}
 
 	public Query setCalendarDate(String name, Calendar calendar) {
 		setParameter(name, calendar, StandardBasicTypes.CALENDAR_DATE);
 		return this;
 	}
 
 	public Query setLong(String name, long val) {
 		setParameter(name, val, StandardBasicTypes.LONG);
 		return this;
 	}
 
 	public Query setSerializable(String name, Serializable val) {
 		setParameter(name, val, StandardBasicTypes.SERIALIZABLE);
 		return this;
 	}
 
 	public Query setShort(String name, short val) {
 		setParameter(name, val, StandardBasicTypes.SHORT);
 		return this;
 	}
 
 	public Query setString(String name, String val) {
 		setParameter(name, val, StandardBasicTypes.STRING);
 		return this;
 	}
 
 	public Query setTime(String name, Date date) {
 		setParameter(name, date, StandardBasicTypes.TIME);
 		return this;
 	}
 
 	public Query setTimestamp(String name, Date date) {
 		setParameter(name, date, StandardBasicTypes.TIMESTAMP);
 		return this;
 	}
 
 	public Query setBigDecimal(int position, BigDecimal number) {
 		setParameter(position, number, StandardBasicTypes.BIG_DECIMAL);
 		return this;
 	}
 
 	public Query setBigDecimal(String name, BigDecimal number) {
 		setParameter(name, number, StandardBasicTypes.BIG_DECIMAL);
 		return this;
 	}
 
 	public Query setBigInteger(int position, BigInteger number) {
 		setParameter(position, number, StandardBasicTypes.BIG_INTEGER);
 		return this;
 	}
 
 	public Query setBigInteger(String name, BigInteger number) {
 		setParameter(name, number, StandardBasicTypes.BIG_INTEGER);
 		return this;
 	}
 
 	public Query setParameterList(String name, Collection vals, Type type) throws HibernateException {
 		if ( !parameterMetadata.getNamedParameterNames().contains( name ) ) {
 			throw new IllegalArgumentException("Parameter " + name + " does not exist as a named parameter in [" + getQueryString() + "]");
 		}
 		namedParameterLists.put( name, new TypedValue( type, vals ) );
 		return this;
 	}
 	
 	/**
 	 * Warning: adds new parameters to the argument by side-effect, as well as
 	 * mutating the query string!
 	 */
 	protected String expandParameterLists(Map namedParamsCopy) {
 		String query = this.queryString;
 		Iterator iter = namedParameterLists.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			query = expandParameterList( query, (String) me.getKey(), (TypedValue) me.getValue(), namedParamsCopy );
 		}
 		return query;
 	}
 
 	/**
 	 * Warning: adds new parameters to the argument by side-effect, as well as
 	 * mutating the query string!
 	 */
 	private String expandParameterList(String query, String name, TypedValue typedList, Map namedParamsCopy) {
 		Collection vals = (Collection) typedList.getValue();
 		Type type = typedList.getType();
 
 		boolean isJpaPositionalParam = parameterMetadata.getNamedParameterDescriptor( name ).isJpaStyle();
 		String paramPrefix = isJpaPositionalParam ? "?" : ParserHelper.HQL_VARIABLE_PREFIX;
 		String placeholder =
 				new StringBuilder( paramPrefix.length() + name.length() )
 						.append( paramPrefix ).append(  name )
 						.toString();
 
 		if ( query == null ) {
 			return query;
 		}
 		int loc = query.indexOf( placeholder );
 
 		if ( loc < 0 ) {
 			return query;
 		}
 
 		String beforePlaceholder = query.substring( 0, loc );
 		String afterPlaceholder =  query.substring( loc + placeholder.length() );
 
 		// check if placeholder is already immediately enclosed in parentheses
 		// (ignoring whitespace)
 		boolean isEnclosedInParens =
 				StringHelper.getLastNonWhitespaceCharacter( beforePlaceholder ) == '(' &&
 				StringHelper.getFirstNonWhitespaceCharacter( afterPlaceholder ) == ')';
 
 		if ( vals.size() == 1  && isEnclosedInParens ) {
 			// short-circuit for performance when only 1 value and the
 			// placeholder is already enclosed in parentheses...
 			namedParamsCopy.put( name, new TypedValue( type, vals.iterator().next() ) );
 			return query;
 		}
 
 		StringBuilder list = new StringBuilder( 16 );
 		Iterator iter = vals.iterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			String alias = ( isJpaPositionalParam ? 'x' + name : name ) + i++ + '_';
 			namedParamsCopy.put( alias, new TypedValue( type, iter.next() ) );
 			list.append( ParserHelper.HQL_VARIABLE_PREFIX ).append( alias );
 			if ( iter.hasNext() ) {
 				list.append( ", " );
 			}
 		}
 		return StringHelper.replace(
 				beforePlaceholder,
 				afterPlaceholder,
 				placeholder.toString(),
 				list.toString(),
 				true,
 				true
 		);
 	}
 
 	public Query setParameterList(String name, Collection vals) throws HibernateException {
 		if ( vals == null ) {
 			throw new QueryException( "Collection must be not null!" );
 		}
 
 		if( vals.size() == 0 ) {
 			setParameterList( name, vals, null );
 		}
 		else {
 			setParameterList(name, vals, determineType( name, vals.iterator().next() ) );
 		}
 
 		return this;
 	}
 
 	public Query setParameterList(String name, Object[] vals, Type type) throws HibernateException {
 		return setParameterList( name, Arrays.asList(vals), type );
 	}
 
 	public Query setParameterList(String name, Object[] vals) throws HibernateException {
 		return setParameterList( name, Arrays.asList(vals) );
 	}
 
 	public Query setProperties(Map map) throws HibernateException {
 		String[] params = getNamedParameters();
 		for (int i = 0; i < params.length; i++) {
 			String namedParam = params[i];
 				final Object object = map.get(namedParam);
 				if(object==null) {
 					continue;
 				}
 				Class retType = object.getClass();
 				if ( Collection.class.isAssignableFrom( retType ) ) {
 					setParameterList( namedParam, ( Collection ) object );
 				}
 				else if ( retType.isArray() ) {
 					setParameterList( namedParam, ( Object[] ) object );
 				}
 				else {
 					setParameter( namedParam, object, determineType( namedParam, retType ) );
 				}
 
 			
 		}
 		return this;				
 	}
 	
 	public Query setProperties(Object bean) throws HibernateException {
 		Class clazz = bean.getClass();
 		String[] params = getNamedParameters();
 		for (int i = 0; i < params.length; i++) {
 			String namedParam = params[i];
 			try {
 				Getter getter = ReflectHelper.getGetter( clazz, namedParam );
 				Class retType = getter.getReturnType();
 				final Object object = getter.get( bean );
 				if ( Collection.class.isAssignableFrom( retType ) ) {
 					setParameterList( namedParam, ( Collection ) object );
 				}
 				else if ( retType.isArray() ) {
 				 	setParameterList( namedParam, ( Object[] ) object );
 				}
 				else {
 					setParameter( namedParam, object, determineType( namedParam, retType ) );
 				}
 			}
 			catch (PropertyNotFoundException pnfe) {
 				// ignore
 			}
 		}
 		return this;
 	}
 
 	public Query setParameters(Object[] values, Type[] types) {
 		this.values = Arrays.asList(values);
 		this.types = Arrays.asList(types);
 		return this;
 	}
 
 
 	// Execution methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Object uniqueResult() throws HibernateException {
 		return uniqueElement( list() );
 	}
 
 	static Object uniqueElement(List list) throws NonUniqueResultException {
 		int size = list.size();
 		if (size==0) return null;
 		Object first = list.get(0);
 		for ( int i=1; i<size; i++ ) {
 			if ( list.get(i)!=first ) {
 				throw new NonUniqueResultException( list.size() );
 			}
 		}
 		return first;
 	}
 
 	protected RowSelection getRowSelection() {
 		return selection;
 	}
 
 	public Type[] typeArray() {
 		return ArrayHelper.toTypeArray( getTypes() );
 	}
 	
 	public Object[] valueArray() {
 		return getValues().toArray();
 	}
 
 	public QueryParameters getQueryParameters(Map namedParams) {
 		return new QueryParameters(
 				typeArray(),
 				valueArray(),
 				namedParams,
 				getLockOptions(),
-				getSelection(),
+				getRowSelection(),
 				true,
 				isReadOnly(),
 				cacheable,
 				cacheRegion,
 				comment,
 				collectionKey == null ? null : new Serializable[] { collectionKey },
 				optionalObject,
 				optionalEntityName,
 				optionalId,
 				resultTransformer
 		);
 	}
 	
 	protected void before() {
 		if ( flushMode!=null ) {
 			sessionFlushMode = getSession().getFlushMode();
 			getSession().setFlushMode(flushMode);
 		}
 		if ( cacheMode!=null ) {
 			sessionCacheMode = getSession().getCacheMode();
 			getSession().setCacheMode(cacheMode);
 		}
 	}
 	
 	protected void after() {
 		if (sessionFlushMode!=null) {
 			getSession().setFlushMode(sessionFlushMode);
 			sessionFlushMode = null;
 		}
 		if (sessionCacheMode!=null) {
 			getSession().setCacheMode(sessionCacheMode);
 			sessionCacheMode = null;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
index 3deb7a82e1..82215386e0 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSessionImpl.java
@@ -1,313 +1,333 @@
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
-			if ( nqd.getLockTimeout() != null ) {
-				( (QueryImpl) query ).getLockOptions().setTimeOut( nqd.getLockTimeout() );
+			if ( nqd.getLockOptions() != null ) {
+				query.setLockOptions( nqd.getLockOptions() );
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
 
+	@SuppressWarnings("UnnecessaryUnboxing")
 	private void initQuery(Query query, NamedQueryDefinition nqd) {
+		// todo : cacheable and readonly should be Boolean rather than boolean...
 		query.setCacheable( nqd.isCacheable() );
 		query.setCacheRegion( nqd.getCacheRegion() );
-		if ( nqd.getTimeout()!=null ) query.setTimeout( nqd.getTimeout().intValue() );
-		if ( nqd.getFetchSize()!=null ) query.setFetchSize( nqd.getFetchSize().intValue() );
-		if ( nqd.getCacheMode() != null ) query.setCacheMode( nqd.getCacheMode() );
 		query.setReadOnly( nqd.isReadOnly() );
-		if ( nqd.getComment() != null ) query.setComment( nqd.getComment() );
+
+		if ( nqd.getTimeout() != null ) {
+			query.setTimeout( nqd.getTimeout().intValue() );
+		}
+		if ( nqd.getFetchSize() != null ) {
+			query.setFetchSize( nqd.getFetchSize().intValue() );
+		}
+		if ( nqd.getCacheMode() != null ) {
+			query.setCacheMode( nqd.getCacheMode() );
+		}
+		if ( nqd.getComment() != null ) {
+			query.setComment( nqd.getComment() );
+		}
+		if ( nqd.getFirstResult() != null ) {
+			query.setFirstResult( nqd.getFirstResult() );
+		}
+		if ( nqd.getMaxResults() != null ) {
+			query.setMaxResults( nqd.getMaxResults() );
+		}
+		if ( nqd.getFlushMode() != null ) {
+			query.setFlushMode( nqd.getFlushMode() );
+		}
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
index a89d2b865a..2f380a8d30 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
@@ -1,513 +1,526 @@
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
 
 import org.hibernate.FlushMode;
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
 
 	SQLQueryImpl(
 			final String sql,
 	        final String returnAliases[],
 	        final Class returnClasses[],
 	        final LockMode[] lockModes,
 	        final SessionImplementor session,
 	        final Collection<String> querySpaces,
 	        final FlushMode flushMode,
 	        ParameterMetadata parameterMetadata) {
 		// TODO : this constructor form is *only* used from constructor directly below us; can it go away?
 		super( sql, flushMode, session, parameterMetadata );
 		queryReturns = new ArrayList<NativeSQLQueryReturn>( returnAliases.length );
 		for ( int i=0; i<returnAliases.length; i++ ) {
 			NativeSQLQueryRootReturn ret = new NativeSQLQueryRootReturn(
 					returnAliases[i],
 					returnClasses[i].getName(),
 					lockModes==null ? LockMode.NONE : lockModes[i]
 			);
 			queryReturns.add(ret);
 		}
 		this.querySpaces = querySpaces;
 		this.callable = false;
 	}
 
 	SQLQueryImpl(
 			final String sql,
 	        final String returnAliases[],
 	        final Class returnClasses[],
 	        final SessionImplementor session,
 	        ParameterMetadata parameterMetadata) {
 		this( sql, returnAliases, returnClasses, null, session, null, null, parameterMetadata );
 	}
 
 	SQLQueryImpl(String sql, SessionImplementor session, ParameterMetadata parameterMetadata) {
 		super( sql, null, session, parameterMetadata );
 		queryReturns = new ArrayList<NativeSQLQueryReturn>();
 		querySpaces = null;
 		callable = false;
 	}
 
-	private NativeSQLQueryReturn[] getQueryReturns() {
-		return queryReturns.toArray( new NativeSQLQueryReturn[queryReturns.size()] );
+	@Override
+	public List<NativeSQLQueryReturn>  getQueryReturns() {
+		prepareQueryReturnsIfNecessary();
+		return queryReturns;
+	}
+
+	@Override
+	public Collection<String> getSynchronizedQuerySpaces() {
+		return querySpaces;
 	}
 
+	@Override
+	public boolean isCallable() {
+		return callable;
+	}
+
+	@Override
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
-		        getQueryReturns(),
+				queryReturns.toArray( new NativeSQLQueryReturn[queryReturns.size()] ),
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
-		prepare();
+		prepareQueryReturnsIfNecessary();
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
 
-	private void prepare() {
+	private void prepareQueryReturnsIfNecessary() {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index f0172bf7f2..1c444807a9 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1758 +1,1781 @@
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
 
+import javax.naming.Reference;
+import javax.naming.StringRefAddr;
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
-import java.util.concurrent.ConcurrentMap;
-import javax.naming.Reference;
-import javax.naming.StringRefAddr;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.CustomEntityDirtinessStrategy;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
-import org.hibernate.engine.spi.CacheImplementor;
-import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.TypeHelper;
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
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
+import org.hibernate.engine.spi.CacheImplementor;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionBuilderImplementor;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionOwner;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
-import org.hibernate.internal.util.ReflectHelper;
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
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 import org.hibernate.service.config.spi.ConfigurationService;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jndi.spi.JndiService;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
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
  * @see org.hibernate.service.jdbc.connections.spi.ConnectionProvider
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
 	private final transient Map<String, NamedQueryDefinition> namedQueries;
 	private final transient Map<String, NamedSQLQueryDefinition> namedSqlQueries;
 	private final transient Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
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
 			ServiceRegistry serviceRegistry,
 			Settings settings,
 			SessionFactoryObserver observer) throws HibernateException {
 			LOG.debug( "Building session factory" );
 
 		sessionFactoryOptions = new SessionFactoryOptions() {
 			private EntityNotFoundDelegate entityNotFoundDelegate;
 
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
 		final RegionFactory regionFactory = cacheAccess.getRegionFactory();
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
 
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		final String cacheRegionPrefix = settings.getCacheRegionPrefix() == null ? "" : settings.getCacheRegionPrefix() + ".";
 
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
 			
 			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
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
 			CollectionPersister persister = serviceRegistry.getService( PersisterFactory.class ).createCollectionPersister(
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
 		namedQueries = new HashMap<String, NamedQueryDefinition>( cfg.getNamedQueries() );
 		namedSqlQueries = new HashMap<String, NamedSQLQueryDefinition>( cfg.getNamedSQLQueries() );
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>( cfg.getSqlResultSetMappings() );
 		imports = new HashMap<String,String>( cfg.getImports() );
 
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
 
 		LOG.debug( "Instantiated session factory" );
 
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
 					failingQueries.append( entry.getKey() ).append( sep );
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
 		LOG.debug( "Building session factory" );
 
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
 				metadata.getServiceRegistry()
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
 
 		LOG.debugf( "Session factory constructed with filter configurations : %s", filters );
 		LOG.debugf( "Instantiating session factory with properties: %s", properties );
 
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
 					if ( LOG.isTraceEnabled() ) {
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
 				if ( LOG.isTraceEnabled() ) {
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
 		namedQueries = new HashMap<String,NamedQueryDefinition>();
 		for ( NamedQueryDefinition namedQueryDefinition :  metadata.getNamedQueryDefinitions() ) {
 			namedQueries.put( namedQueryDefinition.getName(), namedQueryDefinition );
 		}
 		namedSqlQueries = new HashMap<String, NamedSQLQueryDefinition>();
 		for ( NamedSQLQueryDefinition namedNativeQueryDefinition: metadata.getNamedNativeQueryDefinitions() ) {
 			namedSqlQueries.put( namedNativeQueryDefinition.getName(), namedNativeQueryDefinition );
 		}
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 		for( ResultSetMappingDefinition resultSetMappingDefinition : metadata.getResultSetMappingDefinitions() ) {
 			sqlResultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 		}
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
 
 		LOG.debug("Instantiated session factory");
 
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
 
 	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
 		return entityNameResolvers.keySet();
 	}
 
 	public QueryPlanCache getQueryPlanCache() {
 		return queryPlanCache;
 	}
 
 	@SuppressWarnings( {"ThrowableResultOfMethodCallIgnored"})
 	private Map<String,HibernateException> checkNamedQueries() throws HibernateException {
 		Map<String,HibernateException> errors = new HashMap<String,HibernateException>();
 
 		// Check named HQL queries
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Checking %s named HQL queries", namedQueries.size() );
 		}
 		Iterator itr = namedQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedQueryDefinition qd = ( NamedQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				LOG.debugf( "Checking named query: %s", queryName );
 				//TODO: BUG! this currently fails for named queries for non-POJO entities
 				queryPlanCache.getHQLQueryPlan( qd.getQueryString(), false, Collections.EMPTY_MAP );
 			}
 			catch ( QueryException e ) {
 				errors.put( queryName, e );
 			}
 			catch ( MappingException e ) {
 				errors.put( queryName, e );
 			}
 
 
 		}
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Checking %s named SQL queries", namedSqlQueries.size() );
 		}
 		itr = namedSqlQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedSQLQueryDefinition qd = ( NamedSQLQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
 				LOG.debugf( "Checking named SQL query: %s", queryName );
 				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
 				// currently not doable though because of the resultset-ref stuff...
 				NativeSQLQuerySpecification spec;
 				if ( qd.getResultSetRef() != null ) {
 					ResultSetMappingDefinition definition = sqlResultSetMappings.get( qd.getResultSetRef() );
 					if ( definition == null ) {
 						throw new MappingException( "Unable to find resultset-ref definition: " + qd.getResultSetRef() );
 					}
 					spec = new NativeSQLQuerySpecification(
 							qd.getQueryString(),
 					        definition.getQueryReturns(),
 					        qd.getQuerySpaces()
 					);
 				}
 				else {
 					spec =  new NativeSQLQuerySpecification(
 							qd.getQueryString(),
 					        qd.getQueryReturns(),
 					        qd.getQuerySpaces()
 					);
 				}
 				queryPlanCache.getNativeSQLQueryPlan( spec );
 			}
 			catch ( QueryException e ) {
 				errors.put( queryName, e );
 			}
 			catch ( MappingException e ) {
 				errors.put( queryName, e );
 			}
 
 		}
 
 		return errors;
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
 
+	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
+		if ( NamedSQLQueryDefinition.class.isInstance( definition ) ) {
+			throw new IllegalArgumentException( "NamedSQLQueryDefinition instance incorrectly passed to registerNamedQueryDefinition" );
+		}
+		final NamedQueryDefinition previous = namedQueries.put( name, definition );
+		if ( previous != null ) {
+			LOG.debugf(
+					"registering named query definition [%s] overriding previously registered definition [%s]",
+					name,
+					previous
+			);
+		}
+	}
+
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return namedQueries.get( queryName );
 	}
 
+	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
+		final NamedSQLQueryDefinition previous = namedSqlQueries.put( name, definition );
+		if ( previous != null ) {
+			LOG.debugf(
+					"registering named SQL query definition [%s] overriding previously registered definition [%s]",
+					name,
+					previous
+			);
+		}
+	}
+
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return namedSqlQueries.get( queryName );
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String resultSetName) {
 		return sqlResultSetMappings.get( resultSetName );
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
 
 	public String getImportedClassName(String className) {
 		String result = imports.get(className);
 		if (result==null) {
 			try {
 				serviceRegistry.getService( ClassLoaderService.class ).classForName( className );
 				return className;
 			}
 			catch (ClassLoadingException cnfe) {
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
 
 	private org.hibernate.engine.transaction.spi.TransactionFactory transactionFactory() {
 		return serviceRegistry.getService( org.hibernate.engine.transaction.spi.TransactionFactory.class );
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
 		private final SessionFactoryImpl sessionFactory;
 		private SessionOwner sessionOwner;
 		private Interceptor interceptor;
 		private Connection connection;
 		private ConnectionReleaseMode connectionReleaseMode;
 		private boolean autoClose;
 		private boolean autoJoinTransactions = true;
 		private boolean flushBeforeCompletion;
 		private String tenantIdentifier;
 
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
 		}
 
 		protected TransactionCoordinatorImpl getTransactionCoordinator() {
 			return null;
 		}
 
 		@Override
 		public Session openSession() {
 			return new SessionImpl(
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
 	}
 
 	public static class StatelessSessionBuilderImpl implements StatelessSessionBuilder {
 		private final SessionFactoryImpl sessionFactory;
 		private Connection connection;
 		private String tenantIdentifier;
 
 		public StatelessSessionBuilderImpl(SessionFactoryImpl sessionFactory) {
 			this.sessionFactory = sessionFactory;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/util/LockModeConverter.java b/hibernate-core/src/main/java/org/hibernate/internal/util/LockModeConverter.java
new file mode 100644
index 0000000000..3168734251
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/internal/util/LockModeConverter.java
@@ -0,0 +1,105 @@
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
+package org.hibernate.internal.util;
+
+import javax.persistence.LockModeType;
+
+import org.hibernate.AssertionFailure;
+import org.hibernate.LockMode;
+
+/**
+ * Helper to deal with conversions (both directions) between {@link org.hibernate.LockMode} and
+ * {@link javax.persistence.LockModeType}.
+ *
+ * @author Steve Ebersole
+ */
+public class LockModeConverter {
+	/**
+	 * Convert from the Hibernate specific LockMode to the JPA defined LockModeType.
+	 *
+	 * @param lockMode The Hibernate LockMode.
+	 *
+	 * @return The JPA LockModeType
+	 */
+	public static LockModeType convertToLockModeType(LockMode lockMode) {
+		if ( lockMode == LockMode.NONE ) {
+			return LockModeType.NONE;
+		}
+		else if ( lockMode == LockMode.OPTIMISTIC || lockMode == LockMode.READ ) {
+			return LockModeType.OPTIMISTIC;
+		}
+		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT || lockMode == LockMode.WRITE ) {
+			return LockModeType.OPTIMISTIC_FORCE_INCREMENT;
+		}
+		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
+			return LockModeType.PESSIMISTIC_READ;
+		}
+		else if ( lockMode == LockMode.PESSIMISTIC_WRITE
+				|| lockMode == LockMode.UPGRADE
+				|| lockMode == LockMode.UPGRADE_NOWAIT ) {
+			return LockModeType.PESSIMISTIC_WRITE;
+		}
+		else if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT
+				|| lockMode == LockMode.FORCE ) {
+			return LockModeType.PESSIMISTIC_FORCE_INCREMENT;
+		}
+		throw new AssertionFailure( "unhandled lock mode " + lockMode );
+	}
+
+
+	/**
+	 * Convert from JPA defined LockModeType to Hibernate specific LockMode.
+	 *
+	 * @param lockMode The JPA LockModeType
+	 *
+	 * @return The Hibernate LockMode.
+	 */
+	public static LockMode convertToLockMode(LockModeType lockMode) {
+		switch ( lockMode ) {
+			case READ:
+			case OPTIMISTIC: {
+				return LockMode.OPTIMISTIC;
+			}
+			case OPTIMISTIC_FORCE_INCREMENT:
+			case WRITE: {
+				return LockMode.OPTIMISTIC_FORCE_INCREMENT;
+			}
+			case PESSIMISTIC_READ: {
+				return LockMode.PESSIMISTIC_READ;
+			}
+			case PESSIMISTIC_WRITE: {
+				return LockMode.PESSIMISTIC_WRITE;
+			}
+			case PESSIMISTIC_FORCE_INCREMENT: {
+				return LockMode.PESSIMISTIC_FORCE_INCREMENT;
+			}
+			case NONE: {
+				return LockMode.NONE;
+			}
+			default: {
+				throw new AssertionFailure( "Unknown LockModeType: " + lockMode );
+			}
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java
index e1aa54a4a9..c31091d63d 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java
@@ -1,348 +1,366 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
 import java.util.HashMap;
 import java.util.List;
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.NamedQueries;
 import javax.persistence.NamedQuery;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.LockMode;
 import org.hibernate.annotations.QueryHints;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
-import org.hibernate.engine.spi.NamedQueryDefinition;
+import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
+import org.hibernate.engine.spi.NamedSQLQueryDefinitionBuilder;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.annotations.AnnotationBindingContext;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.JandexHelper;
 
 /**
  * Binds {@link NamedQuery}, {@link NamedQueries}, {@link NamedNativeQuery}, {@link NamedNativeQueries},
  * {@link org.hibernate.annotations.NamedQuery}, {@link org.hibernate.annotations.NamedQueries},
  * {@link org.hibernate.annotations.NamedNativeQuery}, and {@link org.hibernate.annotations.NamedNativeQueries}.
  *
  * @author Hardy Ferentschik
  */
 public class QueryBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			QueryBinder.class.getName()
 	);
 
 	private QueryBinder() {
 	}
 
 	/**
 	 * Binds all {@link NamedQuery}, {@link NamedQueries}, {@link NamedNativeQuery}, {@link NamedNativeQueries},
 	 * {@link org.hibernate.annotations.NamedQuery}, {@link org.hibernate.annotations.NamedQueries},
 	 * {@link org.hibernate.annotations.NamedNativeQuery}, and {@link org.hibernate.annotations.NamedNativeQueries}
 	 * annotations to the supplied metadata.
 	 *
 	 * @param bindingContext the context for annotation binding
 	 */
 	public static void bind(AnnotationBindingContext bindingContext) {
 		List<AnnotationInstance> annotations = bindingContext.getIndex().getAnnotations( JPADotNames.NAMED_QUERY );
 		for ( AnnotationInstance query : annotations ) {
 			bindNamedQuery( bindingContext.getMetadataImplementor(), query );
 		}
 
 		annotations = bindingContext.getIndex().getAnnotations( JPADotNames.NAMED_QUERIES );
 		for ( AnnotationInstance queries : annotations ) {
 			for ( AnnotationInstance query : JandexHelper.getValue( queries, "value", AnnotationInstance[].class ) ) {
 				bindNamedQuery( bindingContext.getMetadataImplementor(), query );
 			}
 		}
 
 		annotations = bindingContext.getIndex().getAnnotations( JPADotNames.NAMED_NATIVE_QUERY );
 		for ( AnnotationInstance query : annotations ) {
 			bindNamedNativeQuery( bindingContext.getMetadataImplementor(), query );
 		}
 
 		annotations = bindingContext.getIndex().getAnnotations( JPADotNames.NAMED_NATIVE_QUERIES );
 		for ( AnnotationInstance queries : annotations ) {
 			for ( AnnotationInstance query : JandexHelper.getValue( queries, "value", AnnotationInstance[].class ) ) {
 				bindNamedNativeQuery( bindingContext.getMetadataImplementor(), query );
 			}
 		}
 
 		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.NAMED_QUERY );
 		for ( AnnotationInstance query : annotations ) {
 			bindNamedQuery( bindingContext.getMetadataImplementor(), query );
 		}
 
 		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.NAMED_QUERIES );
 		for ( AnnotationInstance queries : annotations ) {
 			for ( AnnotationInstance query : JandexHelper.getValue( queries, "value", AnnotationInstance[].class ) ) {
 				bindNamedQuery( bindingContext.getMetadataImplementor(), query );
 			}
 		}
 
 		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERY );
 		for ( AnnotationInstance query : annotations ) {
 			bindNamedNativeQuery( bindingContext.getMetadataImplementor(), query );
 		}
 
 		annotations = bindingContext.getIndex().getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERIES );
 		for ( AnnotationInstance queries : annotations ) {
 			for ( AnnotationInstance query : JandexHelper.getValue( queries, "value", AnnotationInstance[].class ) ) {
 				bindNamedNativeQuery( bindingContext.getMetadataImplementor(), query );
 			}
 		}
 	}
 
 	/**
 	 * Binds {@link javax.persistence.NamedQuery} as well as {@link org.hibernate.annotations.NamedQuery}.
 	 *
 	 * @param metadata the current metadata
 	 * @param annotation the named query annotation
 	 */
 	private static void bindNamedQuery(MetadataImplementor metadata, AnnotationInstance annotation) {
 		String name = JandexHelper.getValue( annotation, "name", String.class );
 		if ( StringHelper.isEmpty( name ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 
 		String query = JandexHelper.getValue( annotation, "query", String.class );
 
 		AnnotationInstance[] hints = JandexHelper.getValue( annotation, "hints", AnnotationInstance[].class );
 
 		String cacheRegion = getString( hints, QueryHints.CACHE_REGION );
 		if ( StringHelper.isEmpty( cacheRegion ) ) {
 			cacheRegion = null;
 		}
 
 		Integer timeout = getTimeout( hints, query );
 		if ( timeout != null && timeout < 0 ) {
 			timeout = null;
 		}
 
 		Integer fetchSize = getInteger( hints, QueryHints.FETCH_SIZE, name );
 		if ( fetchSize != null && fetchSize < 0 ) {
 			fetchSize = null;
 		}
 
 		String comment = getString( hints, QueryHints.COMMENT );
 		if ( StringHelper.isEmpty( comment ) ) {
 			comment = null;
 		}
 
 		metadata.addNamedQuery(
-				new NamedQueryDefinition(
-						name,
-						query, getBoolean( hints, QueryHints.CACHEABLE, name ), cacheRegion,
-						timeout, fetchSize, getFlushMode( hints, QueryHints.FLUSH_MODE, name ),
-						getCacheMode( hints, QueryHints.CACHE_MODE, name ),
-						getBoolean( hints, QueryHints.READ_ONLY, name ), comment, null
-				)
+				new NamedQueryDefinitionBuilder().setName( name ).setQuery( query ).setCacheable(
+						getBoolean(
+								hints,
+								QueryHints.CACHEABLE,
+								name
+						)
+				).setCacheRegion( cacheRegion ).setTimeout( timeout ).setFetchSize( fetchSize ).setFlushMode(
+						getFlushMode( hints, QueryHints.FLUSH_MODE, name )
+				).setCacheMode( getCacheMode( hints, QueryHints.CACHE_MODE, name ) ).setReadOnly(
+						getBoolean(
+								hints,
+								QueryHints.READ_ONLY,
+								name
+						)
+				).setComment( comment ).setParameterTypes( null ).createNamedQueryDefinition()
 		);
 		LOG.debugf( "Binding named query: %s => %s", name, query );
 	}
 
 	private static void bindNamedNativeQuery(MetadataImplementor metadata, AnnotationInstance annotation) {
 		String name = JandexHelper.getValue( annotation, "name", String.class );
 		if ( StringHelper.isEmpty( name ) ) {
 			throw new AnnotationException( "A named native query must have a name when used in class or package level" );
 		}
 
 		String query = JandexHelper.getValue( annotation, "query", String.class );
 
 		String resultSetMapping = JandexHelper.getValue( annotation, "resultSetMapping", String.class );
 
 		AnnotationInstance[] hints = JandexHelper.getValue( annotation, "hints", AnnotationInstance[].class );
 
 		boolean cacheable = getBoolean( hints, "org.hibernate.cacheable", name );
 		String cacheRegion = getString( hints, QueryHints.CACHE_REGION );
 		if ( StringHelper.isEmpty( cacheRegion ) ) {
 			cacheRegion = null;
 		}
 
 		Integer timeout = getTimeout( hints, query );
 		if ( timeout != null && timeout < 0 ) {
 			timeout = null;
 		}
 
 		Integer fetchSize = getInteger( hints, QueryHints.FETCH_SIZE, name );
 		if ( fetchSize != null && fetchSize < 0 ) {
 			fetchSize = null;
 		}
 
 		FlushMode flushMode = getFlushMode( hints, QueryHints.FLUSH_MODE, name );
 		CacheMode cacheMode = getCacheMode( hints, QueryHints.CACHE_MODE, name );
 
 		boolean readOnly = getBoolean( hints, QueryHints.READ_ONLY, name );
 
 		String comment = getString( hints, QueryHints.COMMENT );
 		if ( StringHelper.isEmpty( comment ) ) {
 			comment = null;
 		}
 
 		boolean callable = getBoolean( hints, QueryHints.CALLABLE, name );
 		NamedSQLQueryDefinition def;
 		if ( StringHelper.isNotEmpty( resultSetMapping ) ) {
-			def = new NamedSQLQueryDefinition(
-					name,
-					query, resultSetMapping, null, cacheable,
-					cacheRegion, timeout, fetchSize,
-					flushMode, cacheMode, readOnly, comment,
-					null, callable
-			);
+			def = new NamedSQLQueryDefinitionBuilder().setName( name )
+					.setQuery( query )
+					.setResultSetRef(
+							resultSetMapping
+					)
+					.setQuerySpaces( null )
+					.setCacheable( cacheable )
+					.setCacheRegion( cacheRegion )
+					.setTimeout( timeout )
+					.setFetchSize( fetchSize )
+					.setFlushMode( flushMode )
+					.setCacheMode( cacheMode )
+					.setReadOnly( readOnly )
+					.setComment( comment )
+					.setParameterTypes( null )
+					.setCallable( callable )
+					.createNamedQueryDefinition();
 		}
 		else {
 			AnnotationValue annotationValue = annotation.value( "resultClass" );
 			if ( annotationValue == null ) {
 				throw new NotYetImplementedException( "Pure native scalar queries are not yet supported" );
 			}
 			NativeSQLQueryRootReturn queryRoots[] = new NativeSQLQueryRootReturn[] {
 					new NativeSQLQueryRootReturn(
 							"alias1",
 							annotationValue.asString(),
 							new HashMap<String, String[]>(),
 							LockMode.READ
 					)
 			};
-			def = new NamedSQLQueryDefinition(
-					name,
-					query,
-					queryRoots,
-					null,
-					cacheable,
-					cacheRegion,
-					timeout,
-					fetchSize,
-					flushMode,
-					cacheMode,
-					readOnly,
-					comment,
-					null,
-					callable
-			);
+			def = new NamedSQLQueryDefinitionBuilder().setName( name )
+					.setQuery( query )
+					.setQueryReturns( queryRoots )
+					.setQuerySpaces( null )
+					.setCacheable( cacheable )
+					.setCacheRegion( cacheRegion )
+					.setTimeout( timeout )
+					.setFetchSize( fetchSize )
+					.setFlushMode( flushMode )
+					.setCacheMode( cacheMode )
+					.setReadOnly( readOnly )
+					.setComment( comment )
+					.setParameterTypes( null )
+					.setCallable( callable )
+					.createNamedQueryDefinition();
 		}
 		metadata.addNamedNativeQuery( def );
 		LOG.debugf( "Binding named native query: %s => %s", name, query );
 	}
 
 	private static boolean getBoolean(AnnotationInstance[] hints, String element, String query) {
 		String val = getString( hints, element );
 		if ( val == null || val.equalsIgnoreCase( "false" ) ) {
 			return false;
 		}
 		if ( val.equalsIgnoreCase( "true" ) ) {
 			return true;
 		}
 		throw new AnnotationException( "Not a boolean in hint: " + query + ":" + element );
 	}
 
 	private static CacheMode getCacheMode(AnnotationInstance[] hints, String element, String query) {
 		String val = getString( hints, element );
 		if ( val == null ) {
 			return null;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.GET.toString() ) ) {
 			return CacheMode.GET;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.IGNORE.toString() ) ) {
 			return CacheMode.IGNORE;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.NORMAL.toString() ) ) {
 			return CacheMode.NORMAL;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.PUT.toString() ) ) {
 			return CacheMode.PUT;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.REFRESH.toString() ) ) {
 			return CacheMode.REFRESH;
 		}
 		throw new AnnotationException( "Unknown CacheMode in hint: " + query + ":" + element );
 	}
 
 	private static FlushMode getFlushMode(AnnotationInstance[] hints, String element, String query) {
 		String val = getString( hints, element );
 		if ( val == null ) {
 			return null;
 		}
 		if ( val.equalsIgnoreCase( FlushMode.ALWAYS.toString() ) ) {
 			return FlushMode.ALWAYS;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.AUTO.toString() ) ) {
 			return FlushMode.AUTO;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.COMMIT.toString() ) ) {
 			return FlushMode.COMMIT;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.NEVER.toString() ) ) {
 			return FlushMode.MANUAL;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.MANUAL.toString() ) ) {
 			return FlushMode.MANUAL;
 		}
 		else {
 			throw new AnnotationException( "Unknown FlushMode in hint: " + query + ":" + element );
 		}
 	}
 
 	private static Integer getInteger(AnnotationInstance[] hints, String element, String query) {
 		String val = getString( hints, element );
 		if ( val == null ) {
 			return null;
 		}
 		try {
 			return Integer.decode( val );
 		}
 		catch ( NumberFormatException nfe ) {
 			throw new AnnotationException( "Not an integer in hint: " + query + ":" + element, nfe );
 		}
 	}
 
 	private static String getString(AnnotationInstance[] hints, String element) {
 		for ( AnnotationInstance hint : hints ) {
 			if ( element.equals( JandexHelper.getValue( hint, "name", String.class ) ) ) {
 				return JandexHelper.getValue( hint, "value", String.class );
 			}
 		}
 		return null;
 	}
 
 	private static Integer getTimeout(AnnotationInstance[] hints, String query) {
 		Integer timeout = getInteger( hints, QueryHints.TIMEOUT_JPA, query );
 		if ( timeout == null ) {
 			return getInteger( hints, QueryHints.TIMEOUT_HIBERNATE, query ); // timeout is already in seconds
 		}
 		return ( ( timeout + 500 ) / 1000 ); // convert milliseconds to seconds (rounded)
 	}
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
index 040dc7b0b6..d330dfe03c 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/EntityManagerFactoryImpl.java
@@ -1,373 +1,412 @@
 /*
  * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
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
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.ejb.criteria.CriteriaBuilderImpl;
 import org.hibernate.ejb.internal.EntityManagerFactoryRegistry;
 import org.hibernate.ejb.metamodel.MetamodelImpl;
 import org.hibernate.ejb.util.PersistenceUtilHelper;
+import org.hibernate.engine.spi.NamedQueryDefinition;
+import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
+import org.hibernate.engine.spi.NamedSQLQueryDefinition;
+import org.hibernate.engine.spi.NamedSQLQueryDefinitionBuilder;
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
 
-		// the spec requires that we keep information such as max results, hints etc.  Currently the information
-		// stored about a named query does not contain all that information (see org.hibernate.engine.spi.NamedQueryDefinition)
-		//
-		// the most simple solution is to just add these needed values to org.hibernate.engine.spi.NamedQueryDefinition
+		// create and register the proper NamedQueryDefinition...
+		final org.hibernate.Query hibernateQuery = ( (HibernateQuery) query ).getHibernateQuery();
+		if ( org.hibernate.SQLQuery.class.isInstance( hibernateQuery ) ) {
+			final NamedSQLQueryDefinition namedQueryDefinition = extractSqlQueryDefinition( ( org.hibernate.SQLQuery ) hibernateQuery, name );
+			sessionFactory.registerNamedSQLQueryDefinition( name, namedQueryDefinition );
+		}
+		else {
+			final NamedQueryDefinition namedQueryDefinition = extractHqlQueryDefinition( hibernateQuery, name );
+			sessionFactory.registerNamedQueryDefinition( name, namedQueryDefinition );
+		}
+	}
+
+	private NamedSQLQueryDefinition extractSqlQueryDefinition(org.hibernate.SQLQuery nativeSqlQuery, String name) {
+		final NamedSQLQueryDefinitionBuilder builder = new NamedSQLQueryDefinitionBuilder( name );
+		fillInNamedQueryBuilder( builder, nativeSqlQuery );
+		builder.setCallable( nativeSqlQuery.isCallable() )
+				.setQuerySpaces( nativeSqlQuery.getSynchronizedQuerySpaces() )
+				.setQueryReturns( nativeSqlQuery.getQueryReturns() );
+		return builder.createNamedQueryDefinition();
+	}
+
+	private NamedQueryDefinition extractHqlQueryDefinition(org.hibernate.Query hqlQuery, String name) {
+		final NamedQueryDefinitionBuilder builder = new NamedQueryDefinitionBuilder( name );
+		fillInNamedQueryBuilder( builder, hqlQuery );
+		// LockOptions only valid for HQL/JPQL queries...
+		builder.setLockOptions( hqlQuery.getLockOptions().makeCopy() );
+		return builder.createNamedQueryDefinition();
+	}
 
-		throw new NotYetImplementedException();
+	private void fillInNamedQueryBuilder(NamedQueryDefinitionBuilder builder, org.hibernate.Query query) {
+		builder.setQuery( query.getQueryString() )
+				.setComment( query.getComment() )
+				.setCacheable( query.isCacheable() )
+				.setCacheRegion( query.getCacheRegion() )
+				.setCacheMode( query.getCacheMode() )
+				.setTimeout( query.getTimeout() )
+				.setFetchSize( query.getFetchSize() )
+				.setFirstResult( query.getFirstResult() )
+				.setMaxResults( query.getMaxResults() )
+				.setReadOnly( query.isReadOnly() )
+				.setFlushMode( query.getFlushMode() );
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
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/HibernateQuery.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/HibernateQuery.java
index 3e48fcc81b..9535d445d3 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/HibernateQuery.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/HibernateQuery.java
@@ -1,29 +1,42 @@
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
  * Boston, MA  02110-1301  USA\
  */
 package org.hibernate.ejb;
+
 import javax.persistence.Query;
 
+/**
+ * Marker interface for Hibernate generated JPA queries so that we can access the underlying Hibernate query objects.
+ *
+ * @author Gavin King
+ * @author Emmanuel Bernard
+ * @author Steve Ebersole
+ */
 public interface HibernateQuery extends Query {
+	/**
+	 * Gives access to the underlying Hibernate query object..
+	 *
+	 * @return THe Hibernate query object.
+	 */
 	public org.hibernate.Query getHibernateQuery();
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/LockModeTypeHelper.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/LockModeTypeHelper.java
index 41b10bb287..e004f79c5e 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/LockModeTypeHelper.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/util/LockModeTypeHelper.java
@@ -1,119 +1,74 @@
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
 package org.hibernate.ejb.util;
 
 import javax.persistence.LockModeType;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.LockMode;
+import org.hibernate.internal.util.LockModeConverter;
 
 /**
  * Helper to deal with {@link LockModeType} <-> {@link LockMode} conversions.
  *
  * @author Steve Ebersole
  */
 public class LockModeTypeHelper {
 	public static LockModeType getLockModeType(LockMode lockMode) {
-		if ( lockMode == LockMode.NONE ) {
-			return LockModeType.NONE;
-		}
-		else if ( lockMode == LockMode.OPTIMISTIC || lockMode == LockMode.READ ) {
-			return LockModeType.OPTIMISTIC;
-		}
-		else if ( lockMode == LockMode.OPTIMISTIC_FORCE_INCREMENT || lockMode == LockMode.WRITE ) {
-			return LockModeType.OPTIMISTIC_FORCE_INCREMENT;
-		}
-		else if ( lockMode == LockMode.PESSIMISTIC_READ ) {
-			return LockModeType.PESSIMISTIC_READ;
-		}
-		else if ( lockMode == LockMode.PESSIMISTIC_WRITE
-				|| lockMode == LockMode.UPGRADE
-				|| lockMode == LockMode.UPGRADE_NOWAIT ) {
-			return LockModeType.PESSIMISTIC_WRITE;
-		}
-		else if ( lockMode == LockMode.PESSIMISTIC_FORCE_INCREMENT
-				|| lockMode == LockMode.FORCE ) {
-			return LockModeType.PESSIMISTIC_FORCE_INCREMENT;
-		}
-		throw new AssertionFailure( "unhandled lock mode " + lockMode );
+		return LockModeConverter.convertToLockModeType( lockMode );
 	}
 
-
-	public static LockMode getLockMode(LockModeType lockMode) {
-		switch ( lockMode ) {
-			case READ:
-			case OPTIMISTIC: {
-				return LockMode.OPTIMISTIC;
-			}
-			case OPTIMISTIC_FORCE_INCREMENT:
-			case WRITE: {
-				return LockMode.OPTIMISTIC_FORCE_INCREMENT;
-			}
-			case PESSIMISTIC_READ: {
-				return LockMode.PESSIMISTIC_READ;
-			}
-			case PESSIMISTIC_WRITE: {
-				return LockMode.PESSIMISTIC_WRITE;
-			}
-			case PESSIMISTIC_FORCE_INCREMENT: {
-				return LockMode.PESSIMISTIC_FORCE_INCREMENT;
-			}
-			case NONE: {
-				return LockMode.NONE;
-			}
-			default: {
-				throw new AssertionFailure( "Unknown LockModeType: " + lockMode );
-			}
-		}
+	public static LockMode getLockMode(LockModeType lockModeType) {
+		return LockModeConverter.convertToLockMode( lockModeType );
 	}
 
 	public static LockMode interpretLockMode(Object value) {
 		if ( value == null ) {
 			return LockMode.NONE;
 		}
 		if ( LockMode.class.isInstance( value ) ) {
 			return (LockMode) value;
 		}
 		else if ( LockModeType.class.isInstance( value ) ) {
 			return getLockMode( (LockModeType) value );
 		}
 		else if ( String.class.isInstance( value ) ) {
 			// first try LockMode name
 			LockMode lockMode = LockMode.valueOf( (String) value );
 			if ( lockMode == null ) {
 				try {
 					lockMode = getLockMode( LockModeType.valueOf( (String) value ) );
 				}
 				catch ( Exception ignore ) {
 				}
 			}
 			if ( lockMode != null ) {
 				return lockMode;
 			}
 		}
 
 		throw new IllegalArgumentException( "Unknown lock mode source : " + value );
 	}
 
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/Item.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/Item.java
index 01ad2162ae..29d8de5180 100755
--- a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/Item.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/Item.java
@@ -1,87 +1,107 @@
 //$Id$
 package org.hibernate.ejb.test;
 import java.io.Serializable;
 import java.util.HashSet;
 import java.util.Set;
 import javax.persistence.Column;
 import javax.persistence.Entity;
 import javax.persistence.EntityResult;
 import javax.persistence.FieldResult;
 import javax.persistence.Id;
+import javax.persistence.LockModeType;
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
+import javax.persistence.NamedQueries;
 import javax.persistence.NamedQuery;
 import javax.persistence.OneToMany;
+import javax.persistence.QueryHint;
 import javax.persistence.SqlResultSetMapping;
 
+import org.hibernate.annotations.QueryHints;
+
 /**
  * @author Gavin King
  */
 @Entity(name = "Item")
 @SqlResultSetMapping(name = "getItem", entities =
 	@EntityResult(entityClass = org.hibernate.ejb.test.Item.class, fields = {
 		@FieldResult(name = "name", column = "itemname"),
 		@FieldResult(name = "descr", column = "itemdescription")
 	})
 )
 @NamedNativeQueries({
 	@NamedNativeQuery(
 		name = "nativeItem1",
 		query = "select name as itemname, descr as itemdescription from Item",
 		resultSetMapping = "getItem"
 	),
 	@NamedNativeQuery(
 		name = "nativeItem2",
 		query = "select * from Item",
 		resultClass = Item.class
 	)
 })
-@NamedQuery(name = "query-construct", query = "select new Item(i.name,i.descr) from Item i")
-//@Cache(region="Item", usage=NONSTRICT_READ_WRITE)
+@NamedQueries({
+		@NamedQuery(
+				name = "itemJpaQueryWithLockModeAndHints",
+				query = "select i from Item i",
+				lockMode = LockModeType.PESSIMISTIC_WRITE,
+				hints = {
+						@QueryHint( name = QueryHints.TIMEOUT_JPA, value = "3000" ),
+						@QueryHint( name = QueryHints.CACHE_MODE, value = "ignore" ),
+						@QueryHint( name = QueryHints.CACHEABLE, value = "true" ),
+						@QueryHint( name = QueryHints.READ_ONLY, value = "true" ),
+						@QueryHint( name = QueryHints.COMMENT, value = "custom static comment" ),
+						@QueryHint( name = QueryHints.FETCH_SIZE, value = "512" ),
+						@QueryHint( name = QueryHints.FLUSH_MODE, value = "manual" )
+				}
+		),
+		@NamedQuery(name = "query-construct", query = "select new Item(i.name,i.descr) from Item i")
+})
 public class Item implements Serializable {
 
 	private String name;
 	private String descr;
 	private Set<Distributor> distributors = new HashSet<Distributor>();
 
 	public Item() {
 	}
 
 	public Item(String name, String desc) {
 		this.name = name;
 		this.descr = desc;
 	}
 
 	@Column(length = 200)
 	public String getDescr() {
 		return descr;
 	}
 
 	public void setDescr(String desc) {
 		this.descr = desc;
 	}
 
 	@Id
 	@Column(length = 30)
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	@OneToMany
 	public Set<Distributor> getDistributors() {
 		return distributors;
 	}
 
 	public void setDistributors(Set<Distributor> distributors) {
 		this.distributors = distributors;
 	}
 
 	public void addDistributor(Distributor d) {
 		if ( distributors == null ) distributors = new HashSet();
 		distributors.add( d );
 	}
 }
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/query/AddNamedQueryTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/query/AddNamedQueryTest.java
new file mode 100644
index 0000000000..f080f446bc
--- /dev/null
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/ejb/test/query/AddNamedQueryTest.java
@@ -0,0 +1,127 @@
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
+package org.hibernate.ejb.test.query;
+
+import javax.persistence.EntityManager;
+import javax.persistence.FlushModeType;
+import javax.persistence.Query;
+import javax.persistence.QueryHint;
+
+import org.hibernate.CacheMode;
+import org.hibernate.FlushMode;
+import org.hibernate.LockMode;
+import org.hibernate.ejb.HibernateQuery;
+import org.hibernate.ejb.QueryHints;
+import org.hibernate.ejb.test.BaseEntityManagerFunctionalTestCase;
+import org.hibernate.ejb.test.Distributor;
+import org.hibernate.ejb.test.Item;
+import org.hibernate.ejb.test.Wallet;
+
+import org.junit.Test;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertNull;
+import static org.junit.Assert.assertTrue;
+
+/**
+ * Tests for {@link javax.persistence.EntityManagerFactory#addNamedQuery} handling.
+ *
+ * @author Steve Ebersole
+ */
+public class AddNamedQueryTest extends BaseEntityManagerFunctionalTestCase {
+	@Override
+	public Class[] getAnnotatedClasses() {
+		return new Class[]{
+				Item.class,
+				Distributor.class,
+				Wallet.class
+		};
+	}
+
+	@Test
+	public void basicTest() {
+		// just making sure we can add one and that it is usable when we get it back
+		EntityManager em = getOrCreateEntityManager();
+		Query query = em.createQuery( "from Item" );
+		final String name = "myBasicItemQuery";
+		em.getEntityManagerFactory().addNamedQuery( name, query );
+		Query query2 = em.createNamedQuery( name );
+		query2.getResultList();
+		em.close();
+	}
+
+	@Test
+	public void testConfigValueHandling() {
+		final String name = "itemJpaQueryWithLockModeAndHints";
+		EntityManager em = getOrCreateEntityManager();
+		Query query = em.createNamedQuery( name );
+		org.hibernate.Query hibernateQuery = ( (HibernateQuery) query ).getHibernateQuery();
+		// assert the state of the query config settings based on the initial named query
+		assertNull( hibernateQuery.getFirstResult() );
+		assertNull( hibernateQuery.getMaxResults() );
+		assertEquals( FlushMode.MANUAL, hibernateQuery.getFlushMode() ); // todo : we need to fix this to stick to AUTO/COMMIT when used from JPA
+		assertEquals( CacheMode.IGNORE, hibernateQuery.getCacheMode() );
+		assertEquals( LockMode.PESSIMISTIC_WRITE, hibernateQuery.getLockOptions().getLockMode() );
+		assertEquals( (Integer) 3, hibernateQuery.getTimeout() ); // jpa timeout is in milliseconds, whereas Hibernate's is in seconds
+
+		query.setHint( QueryHints.HINT_TIMEOUT, 10 );
+		em.getEntityManagerFactory().addNamedQuery( name, query );
+
+		query = em.createNamedQuery( name );
+		hibernateQuery = ( (HibernateQuery) query ).getHibernateQuery();
+		// assert the state of the query config settings based on the initial named query
+		assertNull( hibernateQuery.getFirstResult() );
+		assertNull( hibernateQuery.getMaxResults() );
+		assertEquals( FlushMode.MANUAL, hibernateQuery.getFlushMode() );
+		assertEquals( CacheMode.IGNORE, hibernateQuery.getCacheMode() );
+		assertEquals( LockMode.PESSIMISTIC_WRITE, hibernateQuery.getLockOptions().getLockMode() );
+		assertEquals( (Integer) 10, hibernateQuery.getTimeout() );
+
+		query.setHint( QueryHints.SPEC_HINT_TIMEOUT, 10000 );
+		em.getEntityManagerFactory().addNamedQuery( name, query );
+
+		query = em.createNamedQuery( name );
+		hibernateQuery = ( (HibernateQuery) query ).getHibernateQuery();
+		// assert the state of the query config settings based on the initial named query
+		assertNull( hibernateQuery.getFirstResult() );
+		assertNull( hibernateQuery.getMaxResults() );
+		assertEquals( FlushMode.MANUAL, hibernateQuery.getFlushMode() );
+		assertEquals( CacheMode.IGNORE, hibernateQuery.getCacheMode() );
+		assertEquals( LockMode.PESSIMISTIC_WRITE, hibernateQuery.getLockOptions().getLockMode() );
+		assertEquals( (Integer) 10, hibernateQuery.getTimeout() );
+
+		query.setFirstResult( 51 );
+		em.getEntityManagerFactory().addNamedQuery( name, query );
+
+		query = em.createNamedQuery( name );
+		hibernateQuery = ( (HibernateQuery) query ).getHibernateQuery();
+		// assert the state of the query config settings based on the initial named query
+		assertEquals( (Integer) 51, hibernateQuery.getFirstResult() );
+		assertNull( hibernateQuery.getMaxResults() );
+		assertEquals( FlushMode.MANUAL, hibernateQuery.getFlushMode() );
+		assertEquals( CacheMode.IGNORE, hibernateQuery.getCacheMode() );
+		assertEquals( LockMode.PESSIMISTIC_WRITE, hibernateQuery.getLockOptions().getLockMode() );
+		assertEquals( (Integer) 10, hibernateQuery.getTimeout() );
+	}
+}
