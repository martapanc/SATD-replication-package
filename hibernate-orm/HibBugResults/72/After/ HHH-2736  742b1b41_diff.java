diff --git a/hibernate-core/src/main/java/org/hibernate/Criteria.java b/hibernate-core/src/main/java/org/hibernate/Criteria.java
index 84b0beb000..cefff41c1b 100644
--- a/hibernate-core/src/main/java/org/hibernate/Criteria.java
+++ b/hibernate-core/src/main/java/org/hibernate/Criteria.java
@@ -1,570 +1,584 @@
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
 import java.util.List;
 
+import javax.persistence.QueryHint;
+
 import org.hibernate.criterion.CriteriaSpecification;
 import org.hibernate.criterion.Criterion;
 import org.hibernate.criterion.Order;
 import org.hibernate.criterion.Projection;
 import org.hibernate.sql.JoinType;
 import org.hibernate.transform.ResultTransformer;
 
 /**
  * <tt>Criteria</tt> is a simplified API for retrieving entities
  * by composing <tt>Criterion</tt> objects. This is a very
  * convenient approach for functionality like "search" screens
  * where there is a variable number of conditions to be placed
  * upon the result set.<br>
  * <br>
  * The <tt>Session</tt> is a factory for <tt>Criteria</tt>.
  * <tt>Criterion</tt> instances are usually obtained via
  * the factory methods on <tt>Restrictions</tt>. eg.
  * <pre>
  * List cats = session.createCriteria(Cat.class)
  *     .add( Restrictions.like("name", "Iz%") )
  *     .add( Restrictions.gt( "weight", new Float(minWeight) ) )
  *     .addOrder( Order.asc("age") )
  *     .list();
  * </pre>
  * You may navigate associations using <tt>createAlias()</tt> or
  * <tt>createCriteria()</tt>.
  * <pre>
  * List cats = session.createCriteria(Cat.class)
  *     .createCriteria("kittens")
  *         .add( Restrictions.like("name", "Iz%") )
  *     .list();
  * </pre>
  * <pre>
  * List cats = session.createCriteria(Cat.class)
  *     .createAlias("kittens", "kit")
  *     .add( Restrictions.like("kit.name", "Iz%") )
  *     .list();
  * </pre>
  * You may specify projection and aggregation using <tt>Projection</tt>
  * instances obtained via the factory methods on <tt>Projections</tt>.
  * <pre>
  * List cats = session.createCriteria(Cat.class)
  *     .setProjection( Projections.projectionList()
  *         .add( Projections.rowCount() )
  *         .add( Projections.avg("weight") )
  *         .add( Projections.max("weight") )
  *         .add( Projections.min("weight") )
  *         .add( Projections.groupProperty("color") )
  *     )
  *     .addOrder( Order.asc("color") )
  *     .list();
  * </pre>
  *
  * @see Session#createCriteria(java.lang.Class)
  * @see org.hibernate.criterion.Restrictions
  * @see org.hibernate.criterion.Projections
  * @see org.hibernate.criterion.Order
  * @see org.hibernate.criterion.Criterion
  * @see org.hibernate.criterion.Projection
  * @see org.hibernate.criterion.DetachedCriteria a disconnected version of this API
  * @author Gavin King
  */
 public interface Criteria extends CriteriaSpecification {
 
 	/**
 	 * Get the alias of the entity encapsulated by this criteria instance.
 	 *
 	 * @return The alias for the encapsulated entity.
 	 */
 	public String getAlias();
 
 	/**
 	 * Used to specify that the query results will be a projection (scalar in
 	 * nature).  Implicitly specifies the {@link #PROJECTION} result transformer.
 	 * <p/>
 	 * The individual components contained within the given
 	 * {@link Projection projection} determines the overall "shape" of the
 	 * query result.
 	 *
 	 * @param projection The projection representing the overall "shape" of the
 	 * query results.
 	 * @return this (for method chaining)
 	 */
 	public Criteria setProjection(Projection projection);
 
 	/**
 	 * Add a {@link Criterion restriction} to constrain the results to be
 	 * retrieved.
 	 *
 	 * @param criterion The {@link Criterion criterion} object representing the
 	 * restriction to be applied.
 	 * @return this (for method chaining)
 	 */
 	public Criteria add(Criterion criterion);
 
 	/**
 	 * Add an {@link Order ordering} to the result set.
 	 *
 	 * @param order The {@link Order order} object representing an ordering
 	 * to be applied to the results.
 	 * @return this (for method chaining)
 	 */
 	public Criteria addOrder(Order order);
 
 	/**
 	 * Specify an association fetching strategy for an association or a
 	 * collection of values.
 	 *
 	 * @param associationPath a dot seperated property path
 	 * @param mode The fetch mode for the referenced association
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws HibernateException Indicates a problem applying the given fetch mode
 	 */
 	public Criteria setFetchMode(String associationPath, FetchMode mode) throws HibernateException;
 
 	/**
 	 * Set the lock mode of the current entity.
 	 *
 	 * @param lockMode The lock mode to be applied
 	 *
 	 * @return this (for method chaining)
 	 */
 	public Criteria setLockMode(LockMode lockMode);
 
 	/**
 	 * Set the lock mode of the aliased entity.
 	 *
 	 * @param alias The previously assigned alias representing the entity to
 	 *			which the given lock mode should apply.
 	 * @param lockMode The lock mode to be applied
 	 *
 	 * @return this (for method chaining)
 	 */
 	public Criteria setLockMode(String alias, LockMode lockMode);
 
 	/**
 	 * Join an association, assigning an alias to the joined association.
 	 * <p/>
 	 * Functionally equivalent to {@link #createAlias(String, String, JoinType )} using
 	 * {@link JoinType#INNER_JOIN} for the joinType.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createAlias(String associationPath, String alias) throws HibernateException;
 
 	/**
 	 * Join an association using the specified join-type, assigning an alias
 	 * to the joined association.
 	 * <p/>
 	 * The joinType is expected to be one of {@link JoinType#INNER_JOIN} (the default),
 	 * {@link JoinType#FULL_JOIN}, or {@link JoinType#LEFT_OUTER_JOIN}.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createAlias(String associationPath, String alias, JoinType joinType) throws HibernateException;
 
 	/**
 	 * Join an association using the specified join-type, assigning an alias
 	 * to the joined association.
 	 * <p/>
 	 * The joinType is expected to be one of {@link #INNER_JOIN} (the default),
 	 * {@link #FULL_JOIN}, or {@link #LEFT_JOIN}.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 * @deprecated use {@link #createAlias(String, String, org.hibernate.sql.JoinType)}
 	 */
 	@Deprecated
 	public Criteria createAlias(String associationPath, String alias, int joinType) throws HibernateException;
 
 	/**
 	 * Join an association using the specified join-type, assigning an alias
 	 * to the joined association.
 	 * <p/>
 	 * The joinType is expected to be one of {@link JoinType#INNER_JOIN} (the default),
 	 * {@link JoinType#FULL_JOIN}, or {@link JoinType#LEFT_OUTER_JOIN}.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 * @param withClause The criteria to be added to the join condition (<tt>ON</tt> clause)
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException;
 
 	/**
 	 * Join an association using the specified join-type, assigning an alias
 	 * to the joined association.
 	 * <p/>
 	 * The joinType is expected to be one of {@link #INNER_JOIN} (the default),
 	 * {@link #FULL_JOIN}, or {@link #LEFT_JOIN}.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 * @param withClause The criteria to be added to the join condition (<tt>ON</tt> clause)
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 * @deprecated use {@link #createAlias(String, String, JoinType, Criterion)}
 	 */
 	@Deprecated
 	public Criteria createAlias(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity.
 	 * <p/>
 	 * Functionally equivalent to {@link #createCriteria(String, org.hibernate.sql.JoinType)} using
 	 * {@link JoinType#INNER_JOIN} for the joinType.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createCriteria(String associationPath) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity, using the
 	 * specified join type.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param joinType The type of join to use.
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createCriteria(String associationPath, JoinType joinType) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity, using the
 	 * specified join type.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param joinType The type of join to use.
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 * @deprecated use {@link #createAlias(String, String, org.hibernate.sql.JoinType)}
 	 */
 	@Deprecated
 	public Criteria createCriteria(String associationPath, int joinType) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity,
 	 * assigning the given alias.
 	 * <p/>
 	 * Functionally equivalent to {@link #createCriteria(String, String, org.hibernate.sql.JoinType)} using
 	 * {@link JoinType#INNER_JOIN} for the joinType.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createCriteria(String associationPath, String alias) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity,
 	 * assigning the given alias and using the specified join type.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createCriteria(String associationPath, String alias, JoinType joinType) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity,
 	 * assigning the given alias and using the specified join type.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 * @deprecated use {@link #createCriteria(String, org.hibernate.sql.JoinType)}
 	 */
 	@Deprecated
 	public Criteria createCriteria(String associationPath, String alias, int joinType) throws HibernateException;
 
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity,
 	 * assigning the given alias and using the specified join type.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 * @param withClause The criteria to be added to the join condition (<tt>ON</tt> clause)
 	 *
 	 * @return the created "sub criteria"
 	 * 
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 */
 	public Criteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException;
 
 	/**
 	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity,
 	 * assigning the given alias and using the specified join type.
 	 *
 	 * @param associationPath A dot-seperated property path
 	 * @param alias The alias to assign to the joined association (for later reference).
 	 * @param joinType The type of join to use.
 	 * @param withClause The criteria to be added to the join condition (<tt>ON</tt> clause)
 	 *
 	 * @return the created "sub criteria"
 	 *
 	 * @throws HibernateException Indicates a problem creating the sub criteria
 	 * @deprecated use {@link #createCriteria(String, String, org.hibernate.sql.JoinType, org.hibernate.criterion.Criterion)}
 	 */
 	@Deprecated
 	public Criteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause) throws HibernateException;
 
 	/**
 	 * Set a strategy for handling the query results. This determines the
 	 * "shape" of the query result.
 	 *
 	 * @param resultTransformer The transformer to apply
 	 * @return this (for method chaining)
 	 *
 	 * @see #ROOT_ENTITY
 	 * @see #DISTINCT_ROOT_ENTITY
 	 * @see #ALIAS_TO_ENTITY_MAP
 	 * @see #PROJECTION
 	 */
 	public Criteria setResultTransformer(ResultTransformer resultTransformer);
 
 	/**
 	 * Set a limit upon the number of objects to be retrieved.
 	 *
 	 * @param maxResults the maximum number of results
 	 * @return this (for method chaining)
 	 */
 	public Criteria setMaxResults(int maxResults);
 
 	/**
 	 * Set the first result to be retrieved.
 	 *
 	 * @param firstResult the first result to retrieve, numbered from <tt>0</tt>
 	 * @return this (for method chaining)
 	 */
 	public Criteria setFirstResult(int firstResult);
 
 	/**
 	 * Was the read-only/modifiable mode explicitly initialized?
 	 *
 	 * @return true, the read-only/modifiable mode was explicitly initialized; false, otherwise.
 	 *
 	 * @see Criteria#setReadOnly(boolean)
 	 */
 	public boolean isReadOnlyInitialized();
 
 	/**
 	 * Should entities and proxies loaded by this Criteria be put in read-only mode? If the
 	 * read-only/modifiable setting was not initialized, then the default
 	 * read-only/modifiable setting for the persistence context is returned instead.
 	 * @see Criteria#setReadOnly(boolean)
 	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
 	 *
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * Criteria that existed in the session before the Criteria was executed.
 	 *
 	 * @return true, entities and proxies loaded by the criteria will be put in read-only mode
 	 *         false, entities and proxies loaded by the criteria will be put in modifiable mode
 	 * @throws IllegalStateException if <code>isReadOnlyInitialized()</code> returns <code>false</code>
 	 * and this Criteria is not associated with a session.
 	 * @see Criteria#isReadOnlyInitialized()
 	 */
 	public boolean isReadOnly();
 
 	/**
 	 * Set the read-only/modifiable mode for entities and proxies
 	 * loaded by this Criteria. This setting overrides the default setting
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
 	 * returned by the criteria that existed in the session before the criteria was executed.
 	 *
 	 * @param readOnly true, entities and proxies loaded by the criteria will be put in read-only mode
 	 *                 false, entities and proxies loaded by the criteria will be put in modifiable mode
 	 * @return {@code this}, for method chaining
 	 */
 	public Criteria setReadOnly(boolean readOnly);
 
 	/**
 	 * Set a fetch size for the underlying JDBC query.
 	 *
 	 * @param fetchSize the fetch size
 	 * @return this (for method chaining)
 	 *
 	 * @see java.sql.Statement#setFetchSize
 	 */
 	public Criteria setFetchSize(int fetchSize);
 
 	/**
 	 * Set a timeout for the underlying JDBC query.
 	 *
 	 * @param timeout The timeout value to apply.
 	 * @return this (for method chaining)
 	 *
 	 * @see java.sql.Statement#setQueryTimeout
 	 */
 	public Criteria setTimeout(int timeout);
 
 	/**
 	 * Enable caching of this query result, provided query caching is enabled
 	 * for the underlying session factory.
 	 *
 	 * @param cacheable Should the result be considered cacheable; default is
 	 * to not cache (false).
 	 * @return this (for method chaining)
 	 */
 	public Criteria setCacheable(boolean cacheable);
 
 	/**
 	 * Set the name of the cache region to use for query result caching.
 	 *
 	 * @param cacheRegion the name of a query cache region, or <tt>null</tt>
 	 * for the default query cache
 	 * @return this (for method chaining)
 	 *
 	 * @see #setCacheable
 	 */
 	public Criteria setCacheRegion(String cacheRegion);
 
 	/**
 	 * Add a comment to the generated SQL.
 	 *
 	 * @param comment a human-readable string
 	 * @return this (for method chaining)
 	 */
 	public Criteria setComment(String comment);
+	
+	  
+	/**
+	 * Add a DB query hint to the SQL.  These differ from JPA's {@link QueryHint}, which is specific to the JPA
+	 * implementation and ignores DB vendor-specific hints.  Instead, these are intended solely for the vendor-specific
+	 * hints, such as Oracle's optimizers.  Multiple query hints are supported; the Dialect will determine
+	 * concatenation and placement.
+	 * 
+	 * @param hint The database specific query hint to add.
+	 * @return this (for method chaining)
+	 */
+	public Criteria addQueryHint(String hint);
 
 	/**
 	 * Override the flush mode for this particular query.
 	 *
 	 * @param flushMode The flush mode to use.
 	 * @return this (for method chaining)
 	 */
 	public Criteria setFlushMode(FlushMode flushMode);
 
 	/**
 	 * Override the cache mode for this particular query.
 	 *
 	 * @param cacheMode The cache mode to use.
 	 * @return this (for method chaining)
 	 */
 	public Criteria setCacheMode(CacheMode cacheMode);
 
 	/**
 	 * Get the results.
 	 *
 	 * @return The list of matched query results.
 	 *
 	 * @throws HibernateException Indicates a problem either translating the criteria to SQL,
 	 * exeucting the SQL or processing the SQL results.
 	 */
 	public List list() throws HibernateException;
 
 	/**
 	 * Get the results as an instance of {@link ScrollableResults}.
 	 *
 	 * @return The {@link ScrollableResults} representing the matched
 	 * query results.
 	 *
 	 * @throws HibernateException Indicates a problem either translating the criteria to SQL,
 	 * exeucting the SQL or processing the SQL results.
 	 */
 	public ScrollableResults scroll() throws HibernateException;
 
 	/**
 	 * Get the results as an instance of {@link ScrollableResults} based on the
 	 * given scroll mode.
 	 *
 	 * @param scrollMode Indicates the type of underlying database cursor to
 	 * request.
 	 *
 	 * @return The {@link ScrollableResults} representing the matched
 	 * query results.
 	 *
 	 * @throws HibernateException Indicates a problem either translating the criteria to SQL,
 	 * exeucting the SQL or processing the SQL results.
 	 */
 	public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException;
 
 	/**
 	 * Convenience method to return a single instance that matches
 	 * the query, or null if the query returns no results.
 	 *
 	 * @return the single result or <tt>null</tt>
 	 * @throws HibernateException if there is more than one matching result
 	 */
 	public Object uniqueResult() throws HibernateException;
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/Query.java b/hibernate-core/src/main/java/org/hibernate/Query.java
index 3005aa96d0..8e4e5a94d3 100644
--- a/hibernate-core/src/main/java/org/hibernate/Query.java
+++ b/hibernate-core/src/main/java/org/hibernate/Query.java
@@ -1,868 +1,880 @@
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
 
+import javax.persistence.QueryHint;
+
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
 @SuppressWarnings("UnusedDeclaration")
 public interface Query extends BasicQueryContract {
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
 	 * @return {@code this}, for method chaining
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
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see #getFirstResult()
 	 */
 	public Query setFirstResult(int firstResult);
 
 	@Override
 	public Query setFlushMode(FlushMode flushMode);
 
 	@Override
 	public Query setCacheMode(CacheMode cacheMode);
 
 	@Override
 	public Query setCacheable(boolean cacheable);
 
 	@Override
 	public Query setCacheRegion(String cacheRegion);
 
 	@Override
 	public Query setTimeout(int timeout);
 
 	@Override
 	public Query setFetchSize(int fetchSize);
 
 	@Override
 	public Query setReadOnly(boolean readOnly);
 
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
 	 * @param lockOptions The lock options to apply to the query.
 	 *
 	 * @return {@code this}, for method chaining
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
 	 * @param alias a query alias, or {@code "this"} for a collection filter
 	 * @param lockMode The lock mode to apply.
 	 *
 	 * @return {@code this}, for method chaining
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
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see #getComment()
 	 */
 	public Query setComment(String comment);
+	
+	/**
+	 * Add a DB query hint to the SQL.  These differ from JPA's {@link QueryHint}, which is specific to the JPA
+	 * implementation and ignores DB vendor-specific hints.  Instead, these are intended solely for the vendor-specific
+	 * hints, such as Oracle's optimizers.  Multiple query hints are supported; the Dialect will determine
+	 * concatenation and placement.
+	 * 
+	 * @param hint The database specific query hint to add.
+	 */
+	public Query addQueryHint(String hint);
 
 	/**
 	 * Return the HQL select clause aliases, if any.
 	 *
 	 * @return an array of aliases as strings
 	 */
 	public String[] getReturnAliases();
 
 	/**
 	 * Return the names of all named parameters of the query.
 	 *
 	 * @return the parameter names, in no particular order
 	 */
 	public String[] getNamedParameters();
 
 	/**
 	 * Return the query results as an <tt>Iterator</tt>. If the query
 	 * contains multiple results pre row, the results are returned in
 	 * an instance of <tt>Object[]</tt>.<br>
 	 * <br>
 	 * Entities returned as results are initialized on demand. The first
 	 * SQL query returns identifiers only.<br>
 	 *
 	 * @return the result iterator
 	 */
 	public Iterator iterate();
 
 	/**
 	 * Return the query results as <tt>ScrollableResults</tt>. The
 	 * scrollability of the returned results depends upon JDBC driver
 	 * support for scrollable <tt>ResultSet</tt>s.<br>
 	 *
 	 * @see ScrollableResults
 	 *
 	 * @return the result iterator
 	 */
 	public ScrollableResults scroll();
 
 	/**
 	 * Return the query results as ScrollableResults. The scrollability of the returned results
 	 * depends upon JDBC driver support for scrollable ResultSets.
 	 *
 	 * @param scrollMode The scroll mode
 	 *
 	 * @return the result iterator
 	 *
 	 * @see ScrollableResults
 	 * @see ScrollMode
 	 *
 	 */
 	public ScrollableResults scroll(ScrollMode scrollMode);
 
 	/**
 	 * Return the query results as a <tt>List</tt>. If the query contains
 	 * multiple results pre row, the results are returned in an instance
 	 * of <tt>Object[]</tt>.
 	 *
 	 * @return the result list
 	 */
 	public List list();
 
 	/**
 	 * Convenience method to return a single instance that matches
 	 * the query, or null if the query returns no results.
 	 *
 	 * @return the single result or <tt>null</tt>
 	 *
 	 * @throws NonUniqueResultException if there is more than one matching result
 	 */
 	public Object uniqueResult();
 
 	/**
 	 * Execute the update or delete statement.
 	 *
 	 * The semantics are compliant with the ejb3 Query.executeUpdate() method.
 	 *
 	 * @return The number of entities updated or deleted.
 	 */
 	public int executeUpdate();
 
 	/**
 	 * Bind a value to a JDBC-style query parameter.
 	 *
 	 * @param position the position of the parameter in the query
 	 * string, numbered from <tt>0</tt>.
 	 * @param val the possibly-null parameter value
 	 * @param type the Hibernate type
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setParameter(int position, Object val, Type type);
 
 	/**
 	 * Bind a value to a named query parameter.
 	 *
 	 * @param name the name of the parameter
 	 * @param val the possibly-null parameter value
 	 * @param type the Hibernate type
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setParameter(String name, Object val, Type type);
 
 	/**
 	 * Bind a value to a JDBC-style query parameter. The Hibernate type of the parameter is
 	 * first detected via the usage/position in the query and if not sufficient secondly 
 	 * guessed from the class of the given object.
 	 *
 	 * @param position the position of the parameter in the query
 	 * string, numbered from <tt>0</tt>.
 	 * @param val the non-null parameter value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setParameter(int position, Object val);
 
 	/**
 	 * Bind a value to a named query parameter. The Hibernate type of the parameter is
 	 * first detected via the usage/position in the query and if not sufficient secondly 
 	 * guessed from the class of the given object.
 	 *
 	 * @param name the name of the parameter
 	 * @param val the non-null parameter value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setParameter(String name, Object val);
 	
 	/**
 	 * Bind values and types to positional parameters.  Allows binding more than one at a time; no real performance
 	 * impact.
 	 *
 	 * The number of elements in each array should match.  That is, element number-0 in types array corresponds to
 	 * element-0 in the values array, etc,
 	 *
 	 * @param types The types
 	 * @param values The values
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setParameters(Object[] values, Type[] types);
 
 	/**
 	 * Bind multiple values to a named query parameter. This is useful for binding
 	 * a list of values to an expression such as <tt>foo.bar in (:value_list)</tt>.
 	 *
 	 * @param name the name of the parameter
 	 * @param values a collection of values to list
 	 * @param type the Hibernate type of the values
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setParameterList(String name, Collection values, Type type);
 
 	/**
 	 * Bind multiple values to a named query parameter. The Hibernate type of the parameter is
 	 * first detected via the usage/position in the query and if not sufficient secondly 
 	 * guessed from the class of the first object in the collection. This is useful for binding a list of values
 	 * to an expression such as <tt>foo.bar in (:value_list)</tt>.
 	 *
 	 * @param name the name of the parameter
 	 * @param values a collection of values to list
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setParameterList(String name, Collection values);
 
 	/**
 	 * Bind multiple values to a named query parameter. This is useful for binding
 	 * a list of values to an expression such as <tt>foo.bar in (:value_list)</tt>.
 	 *
 	 * @param name the name of the parameter
 	 * @param values a collection of values to list
 	 * @param type the Hibernate type of the values
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setParameterList(String name, Object[] values, Type type);
 
 	/**
 	 * Bind multiple values to a named query parameter. The Hibernate type of the parameter is
 	 * first detected via the usage/position in the query and if not sufficient secondly 
 	 * guessed from the class of the first object in the array. This is useful for binding a list of values
 	 * to an expression such as <tt>foo.bar in (:value_list)</tt>.
 	 *
 	 * @param name the name of the parameter
 	 * @param values a collection of values to list
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setParameterList(String name, Object[] values);
 
 	/**
 	 * Bind the property values of the given bean to named parameters of the query,
 	 * matching property names with parameter names and mapping property types to
 	 * Hibernate types using heuristics.
 	 *
 	 * @param bean any JavaBean or POJO
 	 *
 	 * @return {@code this}, for method chaining
 	 */	
 	public Query setProperties(Object bean);
 	
 	/**
 	 * Bind the values of the given Map for each named parameters of the query,
 	 * matching key names with parameter names and mapping value types to
 	 * Hibernate types using heuristics.
 	 *
 	 * @param bean a java.util.Map
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setProperties(Map bean);
 
 	/**
 	 * Bind a positional String-valued parameter.
 	 *
 	 * @param position The parameter position
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setString(int position, String val);
 
 	/**
 	 * Bind a positional char-valued parameter.
 	 *
 	 * @param position The parameter position
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setCharacter(int position, char val);
 
 	/**
 	 * Bind a positional boolean-valued parameter.
 	 *
 	 * @param position The parameter position
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setBoolean(int position, boolean val);
 
 	/**
 	 * Bind a positional byte-valued parameter.
 	 *
 	 * @param position The parameter position
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setByte(int position, byte val);
 
 	/**
 	 * Bind a positional short-valued parameter.
 	 *
 	 * @param position The parameter position
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setShort(int position, short val);
 
 	/**
 	 * Bind a positional int-valued parameter.
 	 *
 	 * @param position The parameter position
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setInteger(int position, int val);
 
 	/**
 	 * Bind a positional long-valued parameter.
 	 *
 	 * @param position The parameter position
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setLong(int position, long val);
 
 	/**
 	 * Bind a positional float-valued parameter.
 	 *
 	 * @param position The parameter position
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setFloat(int position, float val);
 
 	/**
 	 * Bind a positional double-valued parameter.
 	 *
 	 * @param position The parameter position
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setDouble(int position, double val);
 
 	/**
 	 * Bind a positional binary-valued parameter.
 	 *
 	 * @param position The parameter position
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setBinary(int position, byte[] val);
 
 	/**
 	 * Bind a positional String-valued parameter using streaming.
 	 *
 	 * @param position The parameter position
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setText(int position, String val);
 
 	/**
 	 * Bind a positional binary-valued parameter using serialization.
 	 *
 	 * @param position The parameter position
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setSerializable(int position, Serializable val);
 
 	/**
 	 * Bind a positional Locale-valued parameter.
 	 *
 	 * @param position The parameter position
 	 * @param locale The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setLocale(int position, Locale locale);
 
 	/**
 	 * Bind a positional BigDecimal-valued parameter.
 	 *
 	 * @param position The parameter position
 	 * @param number The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setBigDecimal(int position, BigDecimal number);
 
 	/**
 	 * Bind a positional BigDecimal-valued parameter.
 	 *
 	 * @param position The parameter position
 	 * @param number The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setBigInteger(int position, BigInteger number);
 
 	/**
 	 * Bind a positional Date-valued parameter using just the Date portion.
 	 *
 	 * @param position The parameter position
 	 * @param date The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setDate(int position, Date date);
 
 	/**
 	 * Bind a positional Date-valued parameter using just the Time portion.
 	 *
 	 * @param position The parameter position
 	 * @param date The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setTime(int position, Date date);
 
 	/**
 	 * Bind a positional Date-valued parameter using the full Timestamp.
 	 *
 	 * @param position The parameter position
 	 * @param date The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setTimestamp(int position, Date date);
 
 	/**
 	 * Bind a positional Calendar-valued parameter using the full Timestamp portion.
 	 *
 	 * @param position The parameter position
 	 * @param calendar The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setCalendar(int position, Calendar calendar);
 
 	/**
 	 * Bind a positional Calendar-valued parameter using just the Date portion.
 	 *
 	 * @param position The parameter position
 	 * @param calendar The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setCalendarDate(int position, Calendar calendar);
 
 	/**
 	 * Bind a named String-valued parameter.
 	 *
 	 * @param name The parameter name
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setString(String name, String val);
 
 	/**
 	 * Bind a named char-valued parameter.
 	 *
 	 * @param name The parameter name
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setCharacter(String name, char val);
 
 	/**
 	 * Bind a named boolean-valued parameter.
 	 *
 	 * @param name The parameter name
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setBoolean(String name, boolean val);
 
 	/**
 	 * Bind a named byte-valued parameter.
 	 *
 	 * @param name The parameter name
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setByte(String name, byte val);
 
 	/**
 	 * Bind a named short-valued parameter.
 	 *
 	 * @param name The parameter name
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setShort(String name, short val);
 
 	/**
 	 * Bind a named int-valued parameter.
 	 *
 	 * @param name The parameter name
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setInteger(String name, int val);
 
 	/**
 	 * Bind a named long-valued parameter.
 	 *
 	 * @param name The parameter name
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setLong(String name, long val);
 
 	/**
 	 * Bind a named float-valued parameter.
 	 *
 	 * @param name The parameter name
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setFloat(String name, float val);
 
 	/**
 	 * Bind a named double-valued parameter.
 	 *
 	 * @param name The parameter name
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setDouble(String name, double val);
 
 	/**
 	 * Bind a named binary-valued parameter.
 	 *
 	 * @param name The parameter name
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setBinary(String name, byte[] val);
 
 	/**
 	 * Bind a named String-valued parameter using streaming.
 	 *
 	 * @param name The parameter name
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setText(String name, String val);
 
 	/**
 	 * Bind a named binary-valued parameter using serialization.
 	 *
 	 * @param name The parameter name
 	 * @param val The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setSerializable(String name, Serializable val);
 
 	/**
 	 * Bind a named Locale-valued parameter.
 	 *
 	 * @param name The parameter name
 	 * @param locale The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setLocale(String name, Locale locale);
 
 	/**
 	 * Bind a named BigDecimal-valued parameter.
 	 *
 	 * @param name The parameter name
 	 * @param number The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setBigDecimal(String name, BigDecimal number);
 
 	/**
 	 * Bind a named BigInteger-valued parameter.
 	 *
 	 * @param name The parameter name
 	 * @param number The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setBigInteger(String name, BigInteger number);
 
 	/**
 	 * Bind the date (time is truncated) of a given Date object to a named query parameter.
 	 *
 	 * @param name The name of the parameter
 	 * @param date The date object
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setDate(String name, Date date);
 
 	/**
 	 * Bind the time (date is truncated) of a given Date object to a named query parameter.
 	 *
 	 * @param name The name of the parameter
 	 * @param date The date object
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setTime(String name, Date date);
 
 	/**
 	 * Bind the date and the time of a given Date object to a named query parameter.
 	 *
 	 * @param name The name of the parameter
 	 * @param date The date object
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setTimestamp(String name, Date date);
 
 	/**
 	 * Bind a named Calendar-valued parameter using the full Timestamp.
 	 *
 	 * @param name The parameter name
 	 * @param calendar The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setCalendar(String name, Calendar calendar);
 
 	/**
 	 * Bind a named Calendar-valued parameter using just the Date portion.
 	 *
 	 * @param name The parameter name
 	 * @param calendar The bind value
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setCalendarDate(String name, Calendar calendar);
 
 	/**
 	 * Bind an instance of a mapped persistent class to a JDBC-style query parameter.
 	 * Use {@link #setParameter(int, Object)} for null values.
 	 *
 	 * @param position the position of the parameter in the query
 	 * string, numbered from <tt>0</tt>.
 	 * @param val a non-null instance of a persistent class
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setEntity(int position, Object val);
 
 	/**
 	 * Bind an instance of a mapped persistent class to a named query parameter.  Use
 	 * {@link #setParameter(String, Object)} for null values.
 	 *
 	 * @param name the name of the parameter
 	 * @param val a non-null instance of a persistent class
 	 *
 	 * @return {@code this}, for method chaining
 	 */
 	public Query setEntity(String name, Object val);
 	
 	
 	/**
 	 * Set a strategy for handling the query results. This can be used to change
 	 * "shape" of the query result.
 	 *
 	 * @param transformer The transformer to apply
 	 * @return this (for method chaining)
 	 */
 	public Query setResultTransformer(ResultTransformer transformer);
 
 }
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index 0d6bc2f5d4..d7d62fb5db 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1,1037 +1,1038 @@
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
 package org.hibernate.dialect;
 
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.sql.Blob;
 import java.sql.CallableStatement;
 import java.sql.Clob;
 import java.sql.NClob;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
+import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.NullPrecedence;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.CastFunction;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardAnsiSqlAggregationFunctions;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.dialect.lock.OptimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.OptimisticLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticForceIncrementLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticReadSelectLockingStrategy;
 import org.hibernate.dialect.lock.PessimisticWriteSelectLockingStrategy;
 import org.hibernate.dialect.lock.SelectLockingStrategy;
 import org.hibernate.dialect.pagination.LegacyLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.DefaultUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.spi.ConversionContext;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.SequenceGenerator;
 import org.hibernate.id.TableHiLoGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.io.StreamCopier;
 import org.hibernate.mapping.Column;
 import org.hibernate.metamodel.spi.TypeContributions;
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.sql.ANSICaseFragment;
 import org.hibernate.sql.ANSIJoinFragment;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.jboss.logging.Logger;
 
 /**
  * Represents a dialect of SQL implemented by a particular RDBMS.  Subclasses implement Hibernate compatibility
  * with different systems.  Subclasses should provide a public default constructor that register a set of type
  * mappings and default Hibernate properties.  Subclasses should be immutable.
  *
  * @author Gavin King, David Channon
  */
 @SuppressWarnings("deprecation")
 public abstract class Dialect implements ConversionContext {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			Dialect.class.getName()
 	);
 
 	/**
 	 * Defines a default batch size constant
 	 */
 	public static final String DEFAULT_BATCH_SIZE = "15";
 
 	/**
 	 * Defines a "no batching" batch size constant
 	 */
 	public static final String NO_BATCH = "0";
 
 	/**
 	 * Characters used as opening for quoting SQL identifiers
 	 */
 	public static final String QUOTE = "`\"[";
 
 	/**
 	 * Characters used as closing for quoting SQL identifiers
 	 */
 	public static final String CLOSED_QUOTE = "`\"]";
 
 	private final TypeNames typeNames = new TypeNames();
 	private final TypeNames hibernateTypeNames = new TypeNames();
 
 	private final Properties properties = new Properties();
 	private final Map<String, SQLFunction> sqlFunctions = new HashMap<String, SQLFunction>();
 	private final Set<String> sqlKeywords = new HashSet<String>();
 
 	private final UniqueDelegate uniqueDelegate;
 
 
 	// constructors and factory methods ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected Dialect() {
 		LOG.usingDialect( this );
 		StandardAnsiSqlAggregationFunctions.primeFunctionMap( sqlFunctions );
 
 		// standard sql92 functions (can be overridden by subclasses)
 		registerFunction( "substring", new SQLFunctionTemplate( StandardBasicTypes.STRING, "substring(?1, ?2, ?3)" ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "locate(?1, ?2, ?3)" ) );
 		registerFunction( "trim", new SQLFunctionTemplate( StandardBasicTypes.STRING, "trim(?1 ?2 ?3 ?4)" ) );
 		registerFunction( "length", new StandardSQLFunction( "length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "bit_length", new StandardSQLFunction( "bit_length", StandardBasicTypes.INTEGER ) );
 		registerFunction( "coalesce", new StandardSQLFunction( "coalesce" ) );
 		registerFunction( "nullif", new StandardSQLFunction( "nullif" ) );
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "mod", new StandardSQLFunction( "mod", StandardBasicTypes.INTEGER) );
 		registerFunction( "sqrt", new StandardSQLFunction( "sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "cast", new CastFunction() );
 		registerFunction( "extract", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(?1 ?2 ?3)") );
 
 		//map second/minute/hour/day/month/year to ANSI extract(), override on subclasses
 		registerFunction( "second", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(second from ?1)") );
 		registerFunction( "minute", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(minute from ?1)") );
 		registerFunction( "hour", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(hour from ?1)") );
 		registerFunction( "day", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(day from ?1)") );
 		registerFunction( "month", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(month from ?1)") );
 		registerFunction( "year", new SQLFunctionTemplate(StandardBasicTypes.INTEGER, "extract(year from ?1)") );
 
 		registerFunction( "str", new SQLFunctionTemplate(StandardBasicTypes.STRING, "cast(?1 as char)") );
 
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BOOLEAN, "boolean" );
 		registerColumnType( Types.TINYINT, "tinyint" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.INTEGER, "integer" );
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.FLOAT, "float($p)" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.NUMERIC, "numeric($p,$s)" );
 		registerColumnType( Types.REAL, "real" );
 
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 
 		registerColumnType( Types.VARBINARY, "bit varying($l)" );
 		registerColumnType( Types.LONGVARBINARY, "bit varying($l)" );
 		registerColumnType( Types.BLOB, "blob" );
 
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.LONGVARCHAR, "varchar($l)" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.NCHAR, "nchar($l)" );
 		registerColumnType( Types.NVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.LONGNVARCHAR, "nvarchar($l)" );
 		registerColumnType( Types.NCLOB, "nclob" );
 
 		// register hibernate types for default use in scalar sqlquery type auto detection
 		registerHibernateType( Types.BIGINT, StandardBasicTypes.BIG_INTEGER.getName() );
 		registerHibernateType( Types.BINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.BIT, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.BOOLEAN, StandardBasicTypes.BOOLEAN.getName() );
 		registerHibernateType( Types.CHAR, StandardBasicTypes.CHARACTER.getName() );
 		registerHibernateType( Types.CHAR, 1, StandardBasicTypes.CHARACTER.getName() );
 		registerHibernateType( Types.CHAR, 255, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.DATE, StandardBasicTypes.DATE.getName() );
 		registerHibernateType( Types.DOUBLE, StandardBasicTypes.DOUBLE.getName() );
 		registerHibernateType( Types.FLOAT, StandardBasicTypes.FLOAT.getName() );
 		registerHibernateType( Types.INTEGER, StandardBasicTypes.INTEGER.getName() );
 		registerHibernateType( Types.SMALLINT, StandardBasicTypes.SHORT.getName() );
 		registerHibernateType( Types.TINYINT, StandardBasicTypes.BYTE.getName() );
 		registerHibernateType( Types.TIME, StandardBasicTypes.TIME.getName() );
 		registerHibernateType( Types.TIMESTAMP, StandardBasicTypes.TIMESTAMP.getName() );
 		registerHibernateType( Types.VARCHAR, StandardBasicTypes.STRING.getName() );
 		registerHibernateType( Types.VARBINARY, StandardBasicTypes.BINARY.getName() );
 		registerHibernateType( Types.LONGVARCHAR, StandardBasicTypes.TEXT.getName() );
 		registerHibernateType( Types.LONGVARBINARY, StandardBasicTypes.IMAGE.getName() );
 		registerHibernateType( Types.NUMERIC, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.DECIMAL, StandardBasicTypes.BIG_DECIMAL.getName() );
 		registerHibernateType( Types.BLOB, StandardBasicTypes.BLOB.getName() );
 		registerHibernateType( Types.CLOB, StandardBasicTypes.CLOB.getName() );
 		registerHibernateType( Types.REAL, StandardBasicTypes.FLOAT.getName() );
 
 		uniqueDelegate = new DefaultUniqueDelegate( this );
 	}
 
 	/**
 	 * Get an instance of the dialect specified by the current <tt>System</tt> properties.
 	 *
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect() throws HibernateException {
 		return instantiateDialect( Environment.getProperties().getProperty( Environment.DIALECT ) );
 	}
 
 
 	/**
 	 * Get an instance of the dialect specified by the given properties or by
 	 * the current <tt>System</tt> properties.
 	 *
 	 * @param props The properties to use for finding the dialect class to use.
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect(Properties props) throws HibernateException {
 		final String dialectName = props.getProperty( Environment.DIALECT );
 		if ( dialectName == null ) {
 			return getDialect();
 		}
 		return instantiateDialect( dialectName );
 	}
 
 	private static Dialect instantiateDialect(String dialectName) throws HibernateException {
 		if ( dialectName == null ) {
 			throw new HibernateException( "The dialect was not set. Set the property hibernate.dialect." );
 		}
 		try {
 			return (Dialect) ReflectHelper.classForName( dialectName ).newInstance();
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new HibernateException( "Dialect class not found: " + dialectName );
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "Could not instantiate given dialect class: " + dialectName, e );
 		}
 	}
 
 	/**
 	 * Retrieve a set of default Hibernate properties for this database.
 	 *
 	 * @return a set of Hibernate properties
 	 */
 	public final Properties getDefaultProperties() {
 		return properties;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName();
 	}
 
 
 	// database type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Allows the Dialect to contribute additional types
 	 *
 	 * @param typeContributions Callback to contribute the types
 	 * @param serviceRegistry The service registry
 	 */
 	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
 		// by default, nothing to do
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code) throws HibernateException {
 		final String result = typeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No default type mapping for (java.sql.Types) " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode with the given storage specification
 	 * parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code, long length, int precision, int scale) throws HibernateException {
 		final String result = typeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					String.format( "No type mapping for java.sql.Types code: %s, length: %s", code, length )
 			);
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the database type appropriate for casting operations
 	 * (via the CAST() SQL function) for the given {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return The database type name
 	 */
 	public String getCastTypeName(int code) {
 		return getTypeName( code, Column.DEFAULT_LENGTH, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param length The type length
 	 * @param precision The type precision
 	 * @param scale The type scale
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int length, int precision, int scale) {
 		if ( jdbcTypeCode == Types.CHAR ) {
 			return "cast(" + value + " as char(" + length + "))";
 		}
 		else {
 			return "cast(" + value + "as " + getTypeName( jdbcTypeCode, length, precision, scale ) + ")";
 		}
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type.  Simply calls
 	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_PRECISION} and
 	 * {@link Column#DEFAULT_SCALE} as the precision/scale.
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param length The type length
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int length) {
 		return cast( value, jdbcTypeCode, length, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE );
 	}
 
 	/**
 	 * Return an expression casting the value to the specified type.  Simply calls
 	 * {@link #cast(String, int, int, int, int)} passing {@link Column#DEFAULT_LENGTH} as the length
 	 *
 	 * @param value The value to cast
 	 * @param jdbcTypeCode The JDBC type code to cast to
 	 * @param precision The type precision
 	 * @param scale The type scale
 	 *
 	 * @return The cast expression
 	 */
 	public String cast(String value, int jdbcTypeCode, int precision, int scale) {
 		return cast( value, jdbcTypeCode, Column.DEFAULT_LENGTH, precision, scale );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code and maximum
 	 * column length. <tt>$l</tt> in the type name with be replaced by the
 	 * column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, long capacity, String name) {
 		typeNames.put( code, capacity, name );
 	}
 
 	/**
 	 * Subclasses register a type name for the given type code. <tt>$l</tt> in
 	 * the type name with be replaced by the column length (if appropriate).
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The database type name
 	 */
 	protected void registerColumnType(int code, String name) {
 		typeNames.put( code, name );
 	}
 
 	/**
 	 * Allows the dialect to override a {@link SqlTypeDescriptor}.
 	 * <p/>
 	 * If the passed {@code sqlTypeDescriptor} allows itself to be remapped (per
 	 * {@link org.hibernate.type.descriptor.sql.SqlTypeDescriptor#canBeRemapped()}), then this method uses
 	 * {@link #getSqlTypeDescriptorOverride}  to get an optional override based on the SQL code returned by
 	 * {@link SqlTypeDescriptor#getSqlType()}.
 	 * <p/>
 	 * If this dialect does not provide an override or if the {@code sqlTypeDescriptor} doe not allow itself to be
 	 * remapped, then this method simply returns the original passed {@code sqlTypeDescriptor}
 	 *
 	 * @param sqlTypeDescriptor The {@link SqlTypeDescriptor} to override
 	 * @return The {@link SqlTypeDescriptor} that should be used for this dialect;
 	 *         if there is no override, then original {@code sqlTypeDescriptor} is returned.
 	 * @throws IllegalArgumentException if {@code sqlTypeDescriptor} is null.
 	 *
 	 * @see #getSqlTypeDescriptorOverride
 	 */
 	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 		if ( sqlTypeDescriptor == null ) {
 			throw new IllegalArgumentException( "sqlTypeDescriptor is null" );
 		}
 		if ( ! sqlTypeDescriptor.canBeRemapped() ) {
 			return sqlTypeDescriptor;
 		}
 
 		final SqlTypeDescriptor overridden = getSqlTypeDescriptorOverride( sqlTypeDescriptor.getSqlType() );
 		return overridden == null ? sqlTypeDescriptor : overridden;
 	}
 
 	/**
 	 * Returns the {@link SqlTypeDescriptor} that should be used to handle the given JDBC type code.  Returns
 	 * {@code null} if there is no override.
 	 *
 	 * @param sqlCode A {@link Types} constant indicating the SQL column type
 	 * @return The {@link SqlTypeDescriptor} to use as an override, or {@code null} if there is no override.
 	 */
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.CLOB: {
 				descriptor = useInputStreamToInsertBlob() ? ClobTypeDescriptor.STREAM_BINDING : null;
 				break;
 			}
 			default: {
 				descriptor = null;
 				break;
 			}
 		}
 		return descriptor;
 	}
 
 	/**
 	 * The legacy behavior of Hibernate.  LOBs are not processed by merge
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy LEGACY_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			return target;
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			return target;
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			return target;
 		}
 	};
 
 	/**
 	 * Merge strategy based on transferring contents based on streams.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy STREAM_XFER_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					// the BLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setBinaryStream( 1L );
 					// the BLOB from the detached state
 					final InputStream detachedStream = original.getBinaryStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeBlob( original, target, session );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					// the CLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setAsciiStream( 1L );
 					// the CLOB from the detached state
 					final InputStream detachedStream = original.getAsciiStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeClob( original, target, session );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					// the NCLOB just read during the load phase of merge
 					final OutputStream connectedStream = target.setAsciiStream( 1L );
 					// the NCLOB from the detached state
 					final InputStream detachedStream = original.getAsciiStream();
 					StreamCopier.copy( detachedStream, connectedStream );
 					return target;
 				}
 				catch (SQLException e ) {
 					throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 				}
 			}
 			else {
 				return NEW_LOCATOR_LOB_MERGE_STRATEGY.mergeNClob( original, target, session );
 			}
 		}
 	};
 
 	/**
 	 * Merge strategy based on creating a new LOB locator.
 	 */
 	protected static final LobMergeStrategy NEW_LOCATOR_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createBlob( ArrayHelper.EMPTY_BYTE_ARRAY )
 						: lobCreator.createBlob( original.getBinaryStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge BLOB data" );
 			}
 		}
 
 		@Override
 		public Clob mergeClob(Clob original, Clob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createClob( "" )
 						: lobCreator.createClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge CLOB data" );
 			}
 		}
 
 		@Override
 		public NClob mergeNClob(NClob original, NClob target, SessionImplementor session) {
 			if ( original == null && target == null ) {
 				return null;
 			}
 			try {
 				final LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
 				return original == null
 						? lobCreator.createNClob( "" )
 						: lobCreator.createNClob( original.getCharacterStream(), original.length() );
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getSQLExceptionHelper().convert( e, "unable to merge NCLOB data" );
 			}
 		}
 	};
 
 	public LobMergeStrategy getLobMergeStrategy() {
 		return NEW_LOCATOR_LOB_MERGE_STRATEGY;
 	}
 
 
 	// hibernate type mapping support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated with the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} type code
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getHibernateTypeName(int code) throws HibernateException {
 		final String result = hibernateTypeNames.get( code );
 		if ( result == null ) {
 			throw new HibernateException( "No Hibernate type mapping for java.sql.Types code: " + code );
 		}
 		return result;
 	}
 
 	/**
 	 * Get the name of the Hibernate {@link org.hibernate.type.Type} associated
 	 * with the given {@link java.sql.Types} typecode with the given storage
 	 * specification parameters.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param length The datatype length
 	 * @param precision The datatype precision
 	 * @param scale The datatype scale
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getHibernateTypeName(int code, int length, int precision, int scale) throws HibernateException {
 		final String result = hibernateTypeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					String.format(
 							"No Hibernate type mapping for type [code=%s, length=%s]",
 							code,
 							length
 					)
 			);
 		}
 		return result;
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code and maximum column length.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param capacity The maximum length of database type
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, long capacity, String name) {
 		hibernateTypeNames.put( code, capacity, name);
 	}
 
 	/**
 	 * Registers a Hibernate {@link org.hibernate.type.Type} name for the given
 	 * {@link java.sql.Types} type code.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @param name The Hibernate {@link org.hibernate.type.Type} name
 	 */
 	protected void registerHibernateType(int code, String name) {
 		hibernateTypeNames.put( code, name);
 	}
 
 
 	// function support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerFunction(String name, SQLFunction function) {
 		// HHH-7721: SQLFunctionRegistry expects all lowercase.  Enforce,
 		// just in case a user's customer dialect uses mixed cases.
 		sqlFunctions.put( name.toLowerCase(), function );
 	}
 
 	/**
 	 * Retrieves a map of the dialect's registered functions
 	 * (functionName => {@link org.hibernate.dialect.function.SQLFunction}).
 	 *
 	 * @return The map of registered functions.
 	 */
 	public final Map<String, SQLFunction> getFunctions() {
 		return sqlFunctions;
 	}
 
 
 	// keyword support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerKeyword(String word) {
 		sqlKeywords.add( word );
 	}
 
 	public Set<String> getKeywords() {
 		return sqlKeywords;
 	}
 
 
 	// native identifier generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The class (which implements {@link org.hibernate.id.IdentifierGenerator})
 	 * which acts as this dialects native generation strategy.
 	 * <p/>
 	 * Comes into play whenever the user specifies the native generator.
 	 *
 	 * @return The native generator class.
 	 */
 	public Class getNativeIdentifierGeneratorClass() {
 		if ( supportsIdentityColumns() ) {
 			return IdentityGenerator.class;
 		}
 		else if ( supportsSequences() ) {
 			return SequenceGenerator.class;
 		}
 		else {
 			return TableHiLoGenerator.class;
 		}
 	}
 
 
 	// IDENTITY support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support identity column key generation?
 	 *
 	 * @return True if IDENTITY columns are supported; false otherwise.
 	 */
 	public boolean supportsIdentityColumns() {
 		return false;
 	}
 
 	/**
 	 * Does the dialect support some form of inserting and selecting
 	 * the generated IDENTITY value all in the same statement.
 	 *
 	 * @return True if the dialect supports selecting the just
 	 * generated IDENTITY in the insert statement.
 	 */
 	public boolean supportsInsertSelectIdentity() {
 		return false;
 	}
 
 	/**
 	 * Whether this dialect have an Identity clause added to the data type or a
 	 * completely separate identity data type
 	 *
 	 * @return boolean
 	 */
 	public boolean hasDataTypeInIdentityColumn() {
 		return true;
 	}
 
 	/**
 	 * Provided we {@link #supportsInsertSelectIdentity}, then attach the
 	 * "select identity" clause to the  insert statement.
 	 *  <p/>
 	 * Note, if {@link #supportsInsertSelectIdentity} == false then
 	 * the insert-string should be returned without modification.
 	 *
 	 * @param insertString The insert command
 	 * @return The insert command with any necessary identity select
 	 * clause attached.
 	 */
 	public String appendIdentitySelectToInsert(String insertString) {
 		return insertString;
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value for a particular table
 	 *
 	 * @param table The table into which the insert was done
 	 * @param column The PK column.
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentitySelectString(String table, String column, int type) throws MappingException {
 		return getIdentitySelectString();
 	}
 
 	/**
 	 * Get the select command to use to retrieve the last generated IDENTITY
 	 * value.
 	 *
 	 * @return The appropriate select command
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentitySelectString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY of
 	 * a particular type.
 	 *
 	 * @param type The {@link java.sql.Types} type code.
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	public String getIdentityColumnString(int type) throws MappingException {
 		return getIdentityColumnString();
 	}
 
 	/**
 	 * The syntax used during DDL to define a column as being an IDENTITY.
 	 *
 	 * @return The appropriate DDL fragment.
 	 * @throws MappingException If IDENTITY generation is not supported.
 	 */
 	protected String getIdentityColumnString() throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support identity key generation" );
 	}
 
 	/**
 	 * The keyword used to insert a generated value into an identity column (or null).
 	 * Need if the dialect does not support inserts that specify no column values.
 	 *
 	 * @return The appropriate keyword.
 	 */
 	public String getIdentityInsertString() {
 		return null;
 	}
 
 
 	// SEQUENCE support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support sequences?
 	 *
 	 * @return True if sequences supported; false otherwise.
 	 */
 	public boolean supportsSequences() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support "pooled" sequences.  Not aware of a better
 	 * name for this.  Essentially can we specify the initial and increment values?
 	 *
 	 * @return True if such "pooled" sequences are supported; false otherwise.
 	 * @see #getCreateSequenceStrings(String, int, int)
 	 * @see #getCreateSequenceString(String, int, int)
 	 */
 	public boolean supportsPooledSequences() {
 		return false;
 	}
 
 	/**
 	 * Generate the appropriate select statement to to retrieve the next value
 	 * of a sequence.
 	 * <p/>
 	 * This should be a "stand alone" select statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return String The "nextval" select string.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Generate the select expression fragment that will retrieve the next
 	 * value of a sequence as part of another (typically DML) statement.
 	 * <p/>
 	 * This differs from {@link #getSequenceNextValString(String)} in that this
 	 * should return an expression usable within another statement.
 	 *
 	 * @param sequenceName the name of the sequence
 	 * @return The "nextval" fragment.
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String getSelectSequenceNextValString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * The multiline script used to create a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 * @deprecated Use {@link #getCreateSequenceString(String, int, int)} instead
 	 */
 	@Deprecated
 	public String[] getCreateSequenceStrings(String sequenceName) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName ) };
 	}
 
 	/**
 	 * An optional multi-line form for databases which {@link #supportsPooledSequences()}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getCreateSequenceStrings(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		return new String[] { getCreateSequenceString( sequenceName, initialValue, incrementSize ) };
 	}
 
 	/**
 	 * Typically dialects which support sequences can create a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getCreateSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can create a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to create
 	 * a sequence should instead override {@link #getCreateSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Overloaded form of {@link #getCreateSequenceString(String)}, additionally
 	 * taking the initial value and increment size to be applied to the sequence
 	 * definition.
 	 * </p>
 	 * The default definition is to suffix {@link #getCreateSequenceString(String)}
 	 * with the string: " start with {initialValue} increment by {incrementSize}" where
 	 * {initialValue} and {incrementSize} are replacement placeholders.  Generally
 	 * dialects should only need to override this method if different key phrases
 	 * are used to apply the allocation information.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @param initialValue The initial value to apply to 'create sequence' statement
 	 * @param incrementSize The increment value to apply to 'create sequence' statement
 	 * @return The sequence creation command
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getCreateSequenceString(String sequenceName, int initialValue, int incrementSize) throws MappingException {
 		if ( supportsPooledSequences() ) {
 			return getCreateSequenceString( sequenceName ) + " start with " + initialValue + " increment by " + incrementSize;
 		}
 		throw new MappingException( getClass().getName() + " does not support pooled sequences" );
 	}
 
 	/**
 	 * The multiline script used to drop a sequence.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	public String[] getDropSequenceStrings(String sequenceName) throws MappingException {
 		return new String[]{getDropSequenceString( sequenceName )};
 	}
 
 	/**
 	 * Typically dialects which support sequences can drop a sequence
 	 * with a single command.  This is convenience form of
 	 * {@link #getDropSequenceStrings} to help facilitate that.
 	 * <p/>
 	 * Dialects which support sequences and can drop a sequence in a
 	 * single command need *only* override this method.  Dialects
 	 * which support sequences but require multiple commands to drop
 	 * a sequence should instead override {@link #getDropSequenceStrings}.
 	 *
 	 * @param sequenceName The name of the sequence
 	 * @return The sequence drop commands
 	 * @throws MappingException If sequences are not supported.
 	 */
 	protected String getDropSequenceString(String sequenceName) throws MappingException {
 		throw new MappingException( getClass().getName() + " does not support sequences" );
 	}
 
 	/**
 	 * Get the select command used retrieve the names of all sequences.
 	 *
 	 * @return The select command; or null if sequences are not supported.
 	 * @see org.hibernate.tool.hbm2ddl.SchemaUpdate
 	 */
 	public String getQuerySequencesString() {
 		return null;
 	}
 
 
 	// GUID support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the command used to select a GUID from the underlying database.
 	 * <p/>
 	 * Optional operation.
 	 *
 	 * @return The appropriate command.
 	 */
 	public String getSelectGUIDString() {
 		throw new UnsupportedOperationException( getClass().getName() + " does not support GUIDs" );
 	}
 
 
 	// limit/offset support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support some form of limiting query results
 	 * via a SQL clause?
 	 *
 	 * @return True if this dialect supports some form of LIMIT.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsLimit() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect's LIMIT support (if any) additionally
 	 * support specifying an offset?
 	 *
 	 * @return True if the dialect supports an offset within the limit support.
 	 * @deprecated {@link #buildLimitHandler(String, RowSelection)} should be overridden instead.
 	 */
@@ -1661,1001 +1662,1013 @@ public abstract class Dialect implements ConversionContext {
 	}
 
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support a way to retrieve the database's current
 	 * timestamp value?
 	 *
 	 * @return True if the current timestamp can be retrieved; false otherwise.
 	 */
 	public boolean supportsCurrentTimestampSelection() {
 		return false;
 	}
 
 	/**
 	 * Should the value returned by {@link #getCurrentTimestampSelectString}
 	 * be treated as callable.  Typically this indicates that JDBC escape
 	 * syntax is being used...
 	 *
 	 * @return True if the {@link #getCurrentTimestampSelectString} return
 	 * is callable; false otherwise.
 	 */
 	public boolean isCurrentTimestampSelectStringCallable() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * Retrieve the command used to retrieve the current timestamp from the
 	 * database.
 	 *
 	 * @return The command.
 	 */
 	public String getCurrentTimestampSelectString() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * The name of the database-specific SQL function for retrieving the
 	 * current timestamp.
 	 *
 	 * @return The function name.
 	 */
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
 	}
 
 
 	// SQLException support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Build an instance of the SQLExceptionConverter preferred by this dialect for
 	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.
 	 * <p/>
 	 * The preferred method is to not override this method; if possible,
 	 * {@link #buildSQLExceptionConversionDelegate()} should be overridden
 	 * instead.
 	 *
 	 * If this method is not overridden, the default SQLExceptionConverter
 	 * implementation executes 3 SQLException converter delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>the vendor-specific delegate returned by {@link #buildSQLExceptionConversionDelegate()};
 	 *         (it is strongly recommended that specific Dialect implementations
 	 *         override {@link #buildSQLExceptionConversionDelegate()})</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * If this method is overridden, it is strongly recommended that the
 	 * returned {@link SQLExceptionConverter} interpret SQL errors based on
 	 * vendor-specific error codes rather than the SQLState since the
 	 * interpretation is more accurate when using vendor-specific ErrorCodes.
 	 *
 	 * @return The Dialect's preferred SQLExceptionConverter, or null to
 	 * indicate that the default {@link SQLExceptionConverter} should be used.
 	 *
 	 * @see {@link #buildSQLExceptionConversionDelegate()}
 	 * @deprecated {@link #buildSQLExceptionConversionDelegate()} should be
 	 * overridden instead.
 	 */
 	@Deprecated
 	public SQLExceptionConverter buildSQLExceptionConverter() {
 		return null;
 	}
 
 	/**
 	 * Build an instance of a {@link SQLExceptionConversionDelegate} for
 	 * interpreting dialect-specific error or SQLState codes.
 	 * <p/>
 	 * When {@link #buildSQLExceptionConverter} returns null, the default 
 	 * {@link SQLExceptionConverter} is used to interpret SQLState and
 	 * error codes. If this method is overridden to return a non-null value,
 	 * the default {@link SQLExceptionConverter} will use the returned
 	 * {@link SQLExceptionConversionDelegate} in addition to the following 
 	 * standard delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * It is strongly recommended that specific Dialect implementations override this
 	 * method, since interpretation of a SQL error is much more accurate when based on
 	 * the a vendor-specific ErrorCode rather than the SQLState.
 	 * <p/>
 	 * Specific Dialects may override to return whatever is most appropriate for that vendor.
 	 *
 	 * @return The SQLExceptionConversionDelegate for this dialect
 	 */
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return null;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new ViolatedConstraintNameExtracter() {
 		public String extractConstraintName(SQLException sqle) {
 			return null;
 		}
 	};
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 
 	// union subclass support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Given a {@link java.sql.Types} type code, determine an appropriate
 	 * null value to use in a select clause.
 	 * <p/>
 	 * One thing to consider here is that certain databases might
 	 * require proper casting for the nulls here since the select here
 	 * will be part of a UNION/UNION ALL.
 	 *
 	 * @param sqlType The {@link java.sql.Types} type code.
 	 * @return The appropriate select clause value fragment.
 	 */
 	public String getSelectClauseNullString(int sqlType) {
 		return "null";
 	}
 
 	/**
 	 * Does this dialect support UNION ALL, which is generally a faster
 	 * variant of UNION?
 	 *
 	 * @return True if UNION ALL is supported; false otherwise.
 	 */
 	public boolean supportsUnionAll() {
 		return false;
 	}
 
 
 	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 	/**
 	 * Create a {@link org.hibernate.sql.JoinFragment} strategy responsible
 	 * for handling this dialect's variations in how joins are handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.JoinFragment} strategy.
 	 */
 	public JoinFragment createOuterJoinFragment() {
 		return new ANSIJoinFragment();
 	}
 
 	/**
 	 * Create a {@link org.hibernate.sql.CaseFragment} strategy responsible
 	 * for handling this dialect's variations in how CASE statements are
 	 * handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.CaseFragment} strategy.
 	 */
 	public CaseFragment createCaseFragment() {
 		return new ANSICaseFragment();
 	}
 
 	/**
 	 * The fragment used to insert a row without specifying any column values.
 	 * This is not possible on some databases.
 	 *
 	 * @return The appropriate empty values clause.
 	 */
 	public String getNoColumnsInsertString() {
 		return "values ( )";
 	}
 
 	/**
 	 * The name of the SQL function that transforms a string to
 	 * lowercase
 	 *
 	 * @return The dialect-specific lowercase function.
 	 */
 	public String getLowercaseFunction() {
 		return "lower";
 	}
 
 	/**
 	 * The name of the SQL function that can do case insensitive <b>like</b> comparison.
 	 *
 	 * @return  The dialect-specific "case insensitive" like function.
 	 */
 	public String getCaseInsensitiveLike(){
 		return "like";
 	}
 
 	/**
 	 * Does this dialect support case insensitive LIKE restrictions?
 	 *
 	 * @return {@code true} if the underlying database supports case insensitive like comparison,
 	 * {@code false} otherwise.  The default is {@code false}.
 	 */
 	public boolean supportsCaseInsensitiveLike(){
 		return false;
 	}
 
 	/**
 	 * Meant as a means for end users to affect the select strings being sent
 	 * to the database and perhaps manipulate them in some fashion.
 	 * <p/>
 	 * The recommend approach is to instead use
 	 * {@link org.hibernate.Interceptor#onPrepareStatement(String)}.
 	 *
 	 * @param select The select command
 	 * @return The mutated select command, or the same as was passed in.
 	 */
 	public String transformSelectString(String select) {
 		return select;
 	}
 
 	/**
 	 * What is the maximum length Hibernate can use for generated aliases?
 	 *
 	 * @return The maximum length.
 	 */
 	public int getMaxAliasLength() {
 		return 10;
 	}
 
 	/**
 	 * The SQL literal value to which this database maps boolean values.
 	 *
 	 * @param bool The boolean value
 	 * @return The appropriate SQL literal.
 	 */
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "1" : "0";
 	}
 
 
 	// identifier quoting support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The character specific to this dialect used to begin a quoted identifier.
 	 *
 	 * @return The dialect's specific open quote character.
 	 */
 	public char openQuote() {
 		return '"';
 	}
 
 	/**
 	 * The character specific to this dialect used to close a quoted identifier.
 	 *
 	 * @return The dialect's specific close quote character.
 	 */
 	public char closeQuote() {
 		return '"';
 	}
 
 	/**
 	 * Apply dialect-specific quoting.
 	 * <p/>
 	 * By default, the incoming value is checked to see if its first character
 	 * is the back-tick (`).  If so, the dialect specific quoting is applied.
 	 *
 	 * @param name The value to be quoted.
 	 * @return The quoted (or unmodified, if not starting with back-tick) value.
 	 * @see #openQuote()
 	 * @see #closeQuote()
 	 */
 	public final String quote(String name) {
 		if ( name == null ) {
 			return null;
 		}
 
 		if ( name.charAt( 0 ) == '`' ) {
 			return openQuote() + name.substring( 1, name.length() - 1 ) + closeQuote();
 		}
 		else {
 			return name;
 		}
 	}
 
 
 	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Get the SQL command used to create the named schema
 	 *
 	 * @param schemaName The name of the schema to be created.
 	 *
 	 * @return The creation command
 	 */
 	public String getCreateSchemaCommand(String schemaName) {
 		return "create schema " + schemaName;
 	}
 
 	/**
 	 * Get the SQL command used to drop the named schema
 	 *
 	 * @param schemaName The name of the schema to be dropped.
 	 *
 	 * @return The drop command
 	 */
 	public String getDropSchemaCommand(String schemaName) {
 		return "drop schema " + schemaName;
 	}
 
 	/**
 	 * Does this dialect support the <tt>ALTER TABLE</tt> syntax?
 	 *
 	 * @return True if we support altering of tables; false otherwise.
 	 */
 	public boolean hasAlterTable() {
 		return true;
 	}
 
 	/**
 	 * Do we need to drop constraints before dropping tables in this dialect?
 	 *
 	 * @return True if constraints must be dropped prior to dropping
 	 * the table; false otherwise.
 	 */
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	/**
 	 * Do we need to qualify index names with the schema name?
 	 *
 	 * @return boolean
 	 */
 	public boolean qualifyIndexName() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a column to a table (optional).
 	 *
 	 * @return The "add column" fragment.
 	 */
 	public String getAddColumnString() {
 		throw new UnsupportedOperationException( "No add column syntax supported by " + getClass().getName() );
 	}
 
 	public String getDropForeignKeyString() {
 		return " drop constraint ";
 	}
 
 	public String getTableTypeString() {
 		// grrr... for differentiation of mysql storage engines
 		return "";
 	}
 
 	/**
 	 * The syntax used to add a foreign key constraint to a table.
 	 *
 	 * @param constraintName The FK constraint name.
 	 * @param foreignKey The names of the columns comprising the FK
 	 * @param referencedTable The table referenced by the FK
 	 * @param primaryKey The explicit columns in the referencedTable referenced
 	 * by this FK.
 	 * @param referencesPrimaryKey if false, constraint should be
 	 * explicit about which column names the constraint refers to
 	 *
 	 * @return the "add FK" fragment
 	 */
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final StringBuilder res = new StringBuilder( 30 );
 
 		res.append( " add constraint " )
 				.append( constraintName )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			res.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		return res.toString();
 	}
 
 	/**
 	 * The syntax used to add a primary key constraint to a table.
 	 *
 	 * @param constraintName The name of the PK constraint.
 	 * @return The "add PK" fragment
 	 */
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " primary key ";
 	}
 
 	/**
 	 * Does the database/driver have bug in deleting rows that refer to other rows being deleted in the same query?
 	 *
 	 * @return {@code true} if the database/driver has this bug
 	 */
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return false;
 	}
 
 	/**
 	 * The keyword used to specify a nullable column.
 	 *
 	 * @return String
 	 */
 	public String getNullColumnString() {
 		return "";
 	}
 
 	/**
 	 * Does this dialect/database support commenting on tables, columns, etc?
 	 *
 	 * @return {@code true} if commenting is supported
 	 */
 	public boolean supportsCommentOn() {
 		return false;
 	}
 
 	/**
 	 * Get the comment into a form supported for table definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getTableComment(String comment) {
 		return "";
 	}
 
 	/**
 	 * Get the comment into a form supported for column definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getColumnComment(String comment) {
 		return "";
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied before the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied before the table name
 	 */
 	public boolean supportsIfExistsBeforeTableName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied after the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied after the table name
 	 */
 	public boolean supportsIfExistsAfterTableName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied before the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied before the constraint name
 	 */
 	public boolean supportsIfExistsBeforeConstraintName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied after the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied after the constraint name
 	 */
 	public boolean supportsIfExistsAfterConstraintName() {
 		return false;
 	}
 
 	/**
 	 * Generate a DROP TABLE statement
 	 *
 	 * @param tableName The name of the table to drop
 	 *
 	 * @return The DROP TABLE command
 	 */
 	public String getDropTableString(String tableName) {
 		final StringBuilder buf = new StringBuilder( "drop table " );
 		if ( supportsIfExistsBeforeTableName() ) {
 			buf.append( "if exists " );
 		}
 		buf.append( tableName ).append( getCascadeConstraintsString() );
 		if ( supportsIfExistsAfterTableName() ) {
 			buf.append( " if exists" );
 		}
 		return buf.toString();
 	}
 
 	/**
 	 * Does this dialect support column-level check constraints?
 	 *
 	 * @return True if column-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsColumnCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support table-level check constraints?
 	 *
 	 * @return True if table-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsTableCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support cascaded delete on foreign key definitions?
 	 *
 	 * @return {@code true} indicates that the dialect does support cascaded delete on foreign keys.
 	 */
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	/**
 	 * Completely optional cascading drop clause
 	 *
 	 * @return String
 	 */
 	public String getCascadeConstraintsString() {
 		return "";
 	}
 
 	/**
 	 * Returns the separator to use for defining cross joins when translating HQL queries.
 	 * <p/>
 	 * Typically this will be either [<tt> cross join </tt>] or [<tt>, </tt>]
 	 * <p/>
 	 * Note that the spaces are important!
 	 *
 	 * @return The cross join separator
 	 */
 	public String getCrossJoinSeparator() {
 		return " cross join ";
 	}
 
 	public ColumnAliasExtractor getColumnAliasExtractor() {
 		return ColumnAliasExtractor.COLUMN_LABEL_EXTRACTOR;
 	}
 
 
 	// Informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support empty IN lists?
 	 * <p/>
 	 * For example, is [where XYZ in ()] a supported construct?
 	 *
 	 * @return True if empty in lists are supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsEmptyInList() {
 		return true;
 	}
 
 	/**
 	 * Are string comparisons implicitly case insensitive.
 	 * <p/>
 	 * In other words, does [where 'XYZ' = 'xyz'] resolve to true?
 	 *
 	 * @return True if comparisons are case insensitive.
 	 * @since 3.2
 	 */
 	public boolean areStringComparisonsCaseInsensitive() {
 		return false;
 	}
 
 	/**
 	 * Is this dialect known to support what ANSI-SQL terms "row value
 	 * constructor" syntax; sometimes called tuple syntax.
 	 * <p/>
 	 * Basically, does it support syntax like
 	 * "... where (FIRST_NAME, LAST_NAME) = ('Steve', 'Ebersole') ...".
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntax() {
 		// return false here, as most databases do not properly support this construct...
 		return false;
 	}
 
 	/**
 	 * If the dialect supports {@link #supportsRowValueConstructorSyntax() row values},
 	 * does it offer such support in IN lists as well?
 	 * <p/>
 	 * For example, "... where (FIRST_NAME, LAST_NAME) IN ( (?, ?), (?, ?) ) ..."
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax in the IN list; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return false;
 	}
 
 	/**
 	 * Should LOBs (both BLOB and CLOB) be bound using stream operations (i.e.
 	 * {@link java.sql.PreparedStatement#setBinaryStream}).
 	 *
 	 * @return True if BLOBs and CLOBs should be bound using stream operations.
 	 * @since 3.2
 	 */
 	public boolean useInputStreamToInsertBlob() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support parameters within the <tt>SELECT</tt> clause of
 	 * <tt>INSERT ... SELECT ...</tt> statements?
 	 *
 	 * @return True if this is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsParametersInInsertSelect() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect require that references to result variables
 	 * (i.e, select expresssion aliases) in an ORDER BY clause be
 	 * replaced by column positions (1-origin) as defined
 	 * by the select clause?
 
 	 * @return true if result variable references in the ORDER BY
 	 *              clause should be replaced by column positions;
 	 *         false otherwise.
 	 */
 	public boolean replaceResultVariableInOrderByClauseWithPosition() {
 		return false;
 	}
 
 	/**
 	 * Renders an ordering fragment
 	 *
 	 * @param expression The SQL order expression. In case of {@code @OrderBy} annotation user receives property placeholder
 	 * (e.g. attribute name enclosed in '{' and '}' signs).
 	 * @param collation Collation string in format {@code collate IDENTIFIER}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param order Order direction. Possible values: {@code asc}, {@code desc}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param nulls Nulls precedence. Default value: {@link NullPrecedence#NONE}.
 	 * @return Renders single element of {@code ORDER BY} clause.
 	 */
 	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
 		final StringBuilder orderByElement = new StringBuilder( expression );
 		if ( collation != null ) {
 			orderByElement.append( " " ).append( collation );
 		}
 		if ( order != null ) {
 			orderByElement.append( " " ).append( order );
 		}
 		if ( nulls != NullPrecedence.NONE ) {
 			orderByElement.append( " nulls " ).append( nulls.name().toLowerCase() );
 		}
 		return orderByElement.toString();
 	}
 
 	/**
 	 * Does this dialect require that parameters appearing in the <tt>SELECT</tt> clause be wrapped in <tt>cast()</tt>
 	 * calls to tell the db parser the expected type.
 	 *
 	 * @return True if select clause parameter must be cast()ed
 	 * @since 3.2
 	 */
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support asking the result set its positioning
 	 * information on forward only cursors.  Specifically, in the case of
 	 * scrolling fetches, Hibernate needs to use
 	 * {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst}.  Certain drivers do not
 	 * allow access to these methods for forward only cursors.
 	 * <p/>
 	 * NOTE : this is highly driver dependent!
 	 *
 	 * @return True if methods like {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst} are supported for forward
 	 * only cursors; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support definition of cascade delete constraints
 	 * which can cause circular chains?
 	 *
 	 * @return True if circular cascade delete constraints are supported; false
 	 * otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		return true;
 	}
 
 	/**
 	 * Are subselects supported as the left-hand-side (LHS) of
 	 * IN-predicates.
 	 * <p/>
 	 * In other words, is syntax like "... <subquery> IN (1, 2, 3) ..." supported?
 	 *
 	 * @return True if subselects can appear as the LHS of an in-predicate;
 	 * false otherwise.
 	 * @since 3.2
 	 */
 	public boolean  supportsSubselectAsInPredicateLHS() {
 		return true;
 	}
 
 	/**
 	 * Expected LOB usage pattern is such that I can perform an insert
 	 * via prepared statement with a parameter binding for a LOB value
 	 * without crazy casting to JDBC driver implementation-specific classes...
 	 * <p/>
 	 * Part of the trickiness here is the fact that this is largely
 	 * driver dependent.  For example, Oracle (which is notoriously bad with
 	 * LOB support in their drivers historically) actually does a pretty good
 	 * job with LOB support as of the 10.2.x versions of their drivers...
 	 *
 	 * @return True if normal LOB usage patterns can be used with this driver;
 	 * false if driver-specific hookiness needs to be applied.
 	 * @since 3.2
 	 */
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support propagating changes to LOB
 	 * values back to the database?  Talking about mutating the
 	 * internal value of the locator as opposed to supplying a new
 	 * locator instance...
 	 * <p/>
 	 * For BLOBs, the internal value might be changed by:
 	 * {@link java.sql.Blob#setBinaryStream},
 	 * {@link java.sql.Blob#setBytes(long, byte[])},
 	 * {@link java.sql.Blob#setBytes(long, byte[], int, int)},
 	 * or {@link java.sql.Blob#truncate(long)}.
 	 * <p/>
 	 * For CLOBs, the internal value might be changed by:
 	 * {@link java.sql.Clob#setAsciiStream(long)},
 	 * {@link java.sql.Clob#setCharacterStream(long)},
 	 * {@link java.sql.Clob#setString(long, String)},
 	 * {@link java.sql.Clob#setString(long, String, int, int)},
 	 * or {@link java.sql.Clob#truncate(long)}.
 	 * <p/>
 	 * NOTE : I do not know the correct answer currently for
 	 * databases which (1) are not part of the cruise control process
 	 * or (2) do not {@link #supportsExpectedLobUsagePattern}.
 	 *
 	 * @return True if the changes are propagated back to the
 	 * database; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsLobValueChangePropogation() {
 		// todo : pretty sure this is the same as the java.sql.DatabaseMetaData.locatorsUpdateCopy method added in JDBC 4, see HHH-6046
 		return true;
 	}
 
 	/**
 	 * Is it supported to materialize a LOB locator outside the transaction in
 	 * which it was created?
 	 * <p/>
 	 * Again, part of the trickiness here is the fact that this is largely
 	 * driver dependent.
 	 * <p/>
 	 * NOTE: all database I have tested which {@link #supportsExpectedLobUsagePattern()}
 	 * also support the ability to materialize a LOB outside the owning transaction...
 	 *
 	 * @return True if unbounded materialization is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support referencing the table being mutated in
 	 * a subquery.  The "table being mutated" is the table referenced in
 	 * an UPDATE or a DELETE query.  And so can that table then be
 	 * referenced in a subquery of said UPDATE/DELETE query.
 	 * <p/>
 	 * For example, would the following two syntaxes be supported:<ul>
 	 * <li>delete from TABLE_A where ID not in ( select ID from TABLE_A )</li>
 	 * <li>update TABLE_A set NON_ID = 'something' where ID in ( select ID from TABLE_A)</li>
 	 * </ul>
 	 *
 	 * @return True if this dialect allows references the mutating table from
 	 * a subquery.
 	 */
 	public boolean supportsSubqueryOnMutatingTable() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support an exists statement in the select clause?
 	 *
 	 * @return True if exists checks are allowed in the select clause; false otherwise.
 	 */
 	public boolean supportsExistsInSelect() {
 		return true;
 	}
 
 	/**
 	 * For the underlying database, is READ_COMMITTED isolation implemented by
 	 * forcing readers to wait for write locks to be released?
 	 *
 	 * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
 	 */
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return false;
 	}
 
 	/**
 	 * For the underlying database, is REPEATABLE_READ isolation implemented by
 	 * forcing writers to wait for read locks to be released?
 	 *
 	 * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
 	 */
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support using a JDBC bind parameter as an argument
 	 * to a function or procedure call?
 	 *
 	 * @return Returns {@code true} if the database supports accepting bind params as args, {@code false} otherwise. The
 	 * default is {@code true}.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean supportsBindAsCallableArgument() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support `count(a,b)`?
 	 *
 	 * @return True if the database supports counting tuples; false otherwise.
 	 */
 	public boolean supportsTupleCounts() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support `count(distinct a,b)`?
 	 *
 	 * @return True if the database supports counting distinct tuples; false otherwise.
 	 */
 	public boolean supportsTupleDistinctCounts() {
 		// oddly most database in fact seem to, so true is the default.
 		return true;
 	}
 
 	/**
 	 * Return the limit that the underlying database places on the number elements in an {@code IN} predicate.
 	 * If the database defines no such limits, simply return zero or less-than-zero.
 	 *
 	 * @return int The limit, or zero-or-less to indicate no limit.
 	 */
 	public int getInExpressionCountLimit() {
 		return 0;
 	}
 
 	/**
 	 * HHH-4635
 	 * Oracle expects all Lob values to be last in inserts and updates.
 	 *
 	 * @return boolean True of Lob values should be last, false if it
 	 * does not matter.
 	 */
 	public boolean forceLobAsLastValue() {
 		return false;
 	}
 
 	/**
 	 * Some dialects have trouble applying pessimistic locking depending upon what other query options are
 	 * specified (paging, ordering, etc).  This method allows these dialects to request that locking be applied
 	 * by subsequent selects.
 	 *
 	 * @return {@code true} indicates that the dialect requests that locking be applied by subsequent select;
 	 * {@code false} (the default) indicates that locking should be applied to the main SQL statement..
 	 */
 	public boolean useFollowOnLocking() {
 		return false;
 	}
 
 	/**
 	 * Negate an expression
 	 *
 	 * @param expression The expression to negate
 	 *
 	 * @return The negated expression
 	 */
 	public String getNotExpression(String expression) {
 		return "not " + expression;
 	}
 
 	/**
 	 * Get the UniqueDelegate supported by this dialect
 	 *
 	 * @return The UniqueDelegate
 	 */
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
 
 	/**
 	 * Does this dialect support the <tt>UNIQUE</tt> column syntax?
 	 *
 	 * @return boolean
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsUnique() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support adding Unique constraints via create and alter table ?
 	 *
 	 * @return boolean
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsUniqueConstraintInCreateAlterTable() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a unique constraint to a table.
 	 *
 	 * @param constraintName The name of the unique constraint.
 	 * @return The "add unique" fragment
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public String getAddUniqueConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " unique ";
 	}
 
 	/**
 	 * Is the combination of not-null and unique supported?
 	 *
 	 * @return deprecated
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsNotNullUnique() {
 		return true;
 	}
+	
+	/**
+	 * Apply a hint to the query.  The entire query is provided, allowing the Dialect full control over the placement
+	 * and syntax of the hint.  By default, ignore the hint and simply return the query.
+	 * 
+	 * @param query The query to which to apply the hint.
+	 * @param hints The  hints to apply
+	 * @return The modified SQL
+	 */
+	public String getQueryHintString(String query, List<String> hints) {
+		return query;
+	} 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
index cb5e403ca3..8a8eea16b6 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
@@ -1,585 +1,609 @@
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
 package org.hibernate.dialect;
 
 import java.sql.CallableStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
+import java.util.List;
 
 import org.hibernate.JDBCException;
 import org.hibernate.QueryTimeoutException;
+import org.hibernate.annotations.common.util.StringHelper;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.NvlFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.LockTimeoutException;
 import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.OracleJoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.BitTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * A dialect for Oracle 8i.
  *
  * @author Steve Ebersole
  */
 @SuppressWarnings("deprecation")
 public class Oracle8iDialect extends Dialect {
 	private static final int PARAM_LIST_SIZE_LIMIT = 1000;
 
 	/**
 	 * Constructs a Oracle8iDialect
 	 */
 	public Oracle8iDialect() {
 		super();
 		registerCharacterTypeMappings();
 		registerNumericTypeMappings();
 		registerDateTimeTypeMappings();
 		registerLargeObjectTypeMappings();
 		registerReverseHibernateTypeMappings();
 		registerFunctions();
 		registerDefaultProperties();
 	}
 
 	protected void registerCharacterTypeMappings() {
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, 4000, "varchar2($l)" );
 		registerColumnType( Types.VARCHAR, "long" );
 	}
 
 	protected void registerNumericTypeMappings() {
 		registerColumnType( Types.BIT, "number(1,0)" );
 		registerColumnType( Types.BIGINT, "number(19,0)" );
 		registerColumnType( Types.SMALLINT, "number(5,0)" );
 		registerColumnType( Types.TINYINT, "number(3,0)" );
 		registerColumnType( Types.INTEGER, "number(10,0)" );
 
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.NUMERIC, "number($p,$s)" );
 		registerColumnType( Types.DECIMAL, "number($p,$s)" );
 
 		registerColumnType( Types.BOOLEAN, "number(1,0)" );
 	}
 
 	protected void registerDateTimeTypeMappings() {
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "date" );
 		registerColumnType( Types.TIMESTAMP, "date" );
 	}
 
 	protected void registerLargeObjectTypeMappings() {
 		registerColumnType( Types.BINARY, 2000, "raw($l)" );
 		registerColumnType( Types.BINARY, "long raw" );
 
 		registerColumnType( Types.VARBINARY, 2000, "raw($l)" );
 		registerColumnType( Types.VARBINARY, "long raw" );
 
 		registerColumnType( Types.BLOB, "blob" );
 		registerColumnType( Types.CLOB, "clob" );
 
 		registerColumnType( Types.LONGVARCHAR, "long" );
 		registerColumnType( Types.LONGVARBINARY, "long raw" );
 	}
 
 	protected void registerReverseHibernateTypeMappings() {
 	}
 
 	protected void registerFunctions() {
 		registerFunction( "abs", new StandardSQLFunction("abs") );
 		registerFunction( "sign", new StandardSQLFunction("sign", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "acos", new StandardSQLFunction("acos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "asin", new StandardSQLFunction("asin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "atan", new StandardSQLFunction("atan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "bitand", new StandardSQLFunction("bitand") );
 		registerFunction( "cos", new StandardSQLFunction("cos", StandardBasicTypes.DOUBLE) );
 		registerFunction( "cosh", new StandardSQLFunction("cosh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "exp", new StandardSQLFunction("exp", StandardBasicTypes.DOUBLE) );
 		registerFunction( "ln", new StandardSQLFunction("ln", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sin", new StandardSQLFunction("sin", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sinh", new StandardSQLFunction("sinh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "stddev", new StandardSQLFunction("stddev", StandardBasicTypes.DOUBLE) );
 		registerFunction( "sqrt", new StandardSQLFunction("sqrt", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tan", new StandardSQLFunction("tan", StandardBasicTypes.DOUBLE) );
 		registerFunction( "tanh", new StandardSQLFunction("tanh", StandardBasicTypes.DOUBLE) );
 		registerFunction( "variance", new StandardSQLFunction("variance", StandardBasicTypes.DOUBLE) );
 
 		registerFunction( "round", new StandardSQLFunction("round") );
 		registerFunction( "trunc", new StandardSQLFunction("trunc") );
 		registerFunction( "ceil", new StandardSQLFunction("ceil") );
 		registerFunction( "floor", new StandardSQLFunction("floor") );
 
 		registerFunction( "chr", new StandardSQLFunction("chr", StandardBasicTypes.CHARACTER) );
 		registerFunction( "initcap", new StandardSQLFunction("initcap") );
 		registerFunction( "lower", new StandardSQLFunction("lower") );
 		registerFunction( "ltrim", new StandardSQLFunction("ltrim") );
 		registerFunction( "rtrim", new StandardSQLFunction("rtrim") );
 		registerFunction( "soundex", new StandardSQLFunction("soundex") );
 		registerFunction( "upper", new StandardSQLFunction("upper") );
 		registerFunction( "ascii", new StandardSQLFunction("ascii", StandardBasicTypes.INTEGER) );
 
 		registerFunction( "to_char", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
 		registerFunction( "to_date", new StandardSQLFunction("to_date", StandardBasicTypes.TIMESTAMP) );
 
 		registerFunction( "current_date", new NoArgSQLFunction("current_date", StandardBasicTypes.DATE, false) );
 		registerFunction( "current_time", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIME, false) );
 		registerFunction( "current_timestamp", new NoArgSQLFunction("current_timestamp", StandardBasicTypes.TIMESTAMP, false) );
 
 		registerFunction( "last_day", new StandardSQLFunction("last_day", StandardBasicTypes.DATE) );
 		registerFunction( "sysdate", new NoArgSQLFunction("sysdate", StandardBasicTypes.DATE, false) );
 		registerFunction( "systimestamp", new NoArgSQLFunction("systimestamp", StandardBasicTypes.TIMESTAMP, false) );
 		registerFunction( "uid", new NoArgSQLFunction("uid", StandardBasicTypes.INTEGER, false) );
 		registerFunction( "user", new NoArgSQLFunction("user", StandardBasicTypes.STRING, false) );
 
 		registerFunction( "rowid", new NoArgSQLFunction("rowid", StandardBasicTypes.LONG, false) );
 		registerFunction( "rownum", new NoArgSQLFunction("rownum", StandardBasicTypes.LONG, false) );
 
 		// Multi-param string dialect functions...
 		registerFunction( "concat", new VarArgsSQLFunction(StandardBasicTypes.STRING, "", "||", "") );
 		registerFunction( "instr", new StandardSQLFunction("instr", StandardBasicTypes.INTEGER) );
 		registerFunction( "instrb", new StandardSQLFunction("instrb", StandardBasicTypes.INTEGER) );
 		registerFunction( "lpad", new StandardSQLFunction("lpad", StandardBasicTypes.STRING) );
 		registerFunction( "replace", new StandardSQLFunction("replace", StandardBasicTypes.STRING) );
 		registerFunction( "rpad", new StandardSQLFunction("rpad", StandardBasicTypes.STRING) );
 		registerFunction( "substr", new StandardSQLFunction("substr", StandardBasicTypes.STRING) );
 		registerFunction( "substrb", new StandardSQLFunction("substrb", StandardBasicTypes.STRING) );
 		registerFunction( "translate", new StandardSQLFunction("translate", StandardBasicTypes.STRING) );
 
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "instr(?2,?1)" ) );
 		registerFunction( "bit_length", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "vsize(?1)*8" ) );
 		registerFunction( "coalesce", new NvlFunction() );
 
 		// Multi-param numeric dialect functions...
 		registerFunction( "atan2", new StandardSQLFunction("atan2", StandardBasicTypes.FLOAT) );
 		registerFunction( "log", new StandardSQLFunction("log", StandardBasicTypes.INTEGER) );
 		registerFunction( "mod", new StandardSQLFunction("mod", StandardBasicTypes.INTEGER) );
 		registerFunction( "nvl", new StandardSQLFunction("nvl") );
 		registerFunction( "nvl2", new StandardSQLFunction("nvl2") );
 		registerFunction( "power", new StandardSQLFunction("power", StandardBasicTypes.FLOAT) );
 
 		// Multi-param date dialect functions...
 		registerFunction( "add_months", new StandardSQLFunction("add_months", StandardBasicTypes.DATE) );
 		registerFunction( "months_between", new StandardSQLFunction("months_between", StandardBasicTypes.FLOAT) );
 		registerFunction( "next_day", new StandardSQLFunction("next_day", StandardBasicTypes.DATE) );
 
 		registerFunction( "str", new StandardSQLFunction("to_char", StandardBasicTypes.STRING) );
 	}
 
 	protected void registerDefaultProperties() {
 		getDefaultProperties().setProperty( Environment.USE_STREAMS_FOR_BINARY, "true" );
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 		// Oracle driver reports to support getGeneratedKeys(), but they only
 		// support the version taking an array of the names of the columns to
 		// be returned (via its RETURNING clause).  No other driver seems to
 		// support this overloaded version.
 		getDefaultProperties().setProperty( Environment.USE_GET_GENERATED_KEYS, "false" );
 	}
 
 	@Override
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		return sqlCode == Types.BOOLEAN ? BitTypeDescriptor.INSTANCE : super.getSqlTypeDescriptorOverride( sqlCode );
 	}
 
 
 	// features which change between 8i, 9i, and 10g ~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public JoinFragment createOuterJoinFragment() {
 		return new OracleJoinFragment();
 	}
 
 	@Override
 	public String getCrossJoinSeparator() {
 		return ", ";
 	}
 
 	/**
 	 * Map case support to the Oracle DECODE function.  Oracle did not
 	 * add support for CASE until 9i.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
 
 	@Override
 	public String getLimitString(String sql, boolean hasOffset) {
 		sql = sql.trim();
 		boolean isForUpdate = false;
 		if ( sql.toLowerCase().endsWith( " for update" ) ) {
 			sql = sql.substring( 0, sql.length()-11 );
 			isForUpdate = true;
 		}
 
 		final StringBuilder pagingSelect = new StringBuilder( sql.length()+100 );
 		if (hasOffset) {
 			pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
 		}
 		else {
 			pagingSelect.append( "select * from ( " );
 		}
 		pagingSelect.append( sql );
 		if (hasOffset) {
 			pagingSelect.append( " ) row_ ) where rownum_ <= ? and rownum_ > ?" );
 		}
 		else {
 			pagingSelect.append( " ) where rownum <= ?" );
 		}
 
 		if ( isForUpdate ) {
 			pagingSelect.append( " for update" );
 		}
 
 		return pagingSelect.toString();
 	}
 
 	/**
 	 * Allows access to the basic {@link Dialect#getSelectClauseNullString}
 	 * implementation...
 	 *
 	 * @param sqlType The {@link java.sql.Types} mapping type code
 	 * @return The appropriate select cluse fragment
 	 */
 	public String getBasicSelectClauseNullString(int sqlType) {
 		return super.getSelectClauseNullString( sqlType );
 	}
 
 	@Override
 	public String getSelectClauseNullString(int sqlType) {
 		switch(sqlType) {
 			case Types.VARCHAR:
 			case Types.CHAR:
 				return "to_char(null)";
 			case Types.DATE:
 			case Types.TIMESTAMP:
 			case Types.TIME:
 				return "to_date(null)";
 			default:
 				return "to_number(null)";
 		}
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select sysdate from dual";
 	}
 
 	@Override
 	public String getCurrentTimestampSQLFunctionName() {
 		return "sysdate";
 	}
 
 
 	// features which remain constant across 8i, 9i, and 10g ~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dual";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		//starts with 1, implicitly
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getCascadeConstraintsString() {
 		return " cascade constraints";
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public String getForUpdateNowaitString() {
 		return " for update nowait";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public String getForUpdateString(String aliases) {
 		return getForUpdateString() + " of " + aliases;
 	}
 
 	@Override
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString() + " of " + aliases + " nowait";
 	}
 
 	@Override
 	public boolean bindLimitParametersInReverseOrder() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean forUpdateOfColumns() {
 		return true;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return    " select sequence_name from all_sequences"
 				+ "  union"
 				+ " select synonym_name"
 				+ "   from all_synonyms us, all_sequences asq"
 				+ "  where asq.sequence_name = us.table_name"
 				+ "    and asq.sequence_owner = us.table_owner";
 	}
 
 	@Override
 	public String getSelectGUIDString() {
 		return "select rawtohex(sys_guid()) from dual";
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 
 		/**
 		 * Extract the name of the violated constraint from the given SQLException.
 		 *
 		 * @param sqle The exception that was the result of the constraint violation.
 		 * @return The extracted constraint name.
 		 */
 		public String extractConstraintName(SQLException sqle) {
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 			if ( errorCode == 1 || errorCode == 2291 || errorCode == 2292 ) {
 				return extractUsingTemplate( "(", ")", sqle.getMessage() );
 			}
 			else if ( errorCode == 1400 ) {
 				// simple nullability constraint
 				return null;
 			}
 			else {
 				return null;
 			}
 		}
 
 	};
 
 	@Override
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return new SQLExceptionConversionDelegate() {
 			@Override
 			public JDBCException convert(SQLException sqlException, String message, String sql) {
 				// interpreting Oracle exceptions is much much more precise based on their specific vendor codes.
 
 				final int errorCode = JdbcExceptionHelper.extractErrorCode( sqlException );
 
 
 				// lock timeouts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( errorCode == 30006 ) {
 					// ORA-30006: resource busy; acquire with WAIT timeout expired
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				else if ( errorCode == 54 ) {
 					// ORA-00054: resource busy and acquire with NOWAIT specified or timeout expired
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 				else if ( 4021 == errorCode ) {
 					// ORA-04021 timeout occurred while waiting to lock object
 					throw new LockTimeoutException( message, sqlException, sql );
 				}
 
 
 				// deadlocks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 60 == errorCode ) {
 					// ORA-00060: deadlock detected while waiting for resource
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 				else if ( 4020 == errorCode ) {
 					// ORA-04020 deadlock detected while trying to lock object
 					return new LockAcquisitionException( message, sqlException, sql );
 				}
 
 
 				// query cancelled ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 1013 == errorCode ) {
 					// ORA-01013: user requested cancel of current operation
 					throw new QueryTimeoutException(  message, sqlException, sql );
 				}
 
 
 				// data integrity violation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 				if ( 1407 == errorCode ) {
 					// ORA-01407: cannot update column to NULL
 					final String constraintName = getViolatedConstraintNameExtracter().extractConstraintName( sqlException );
 					return new ConstraintViolationException( message, sqlException, sql, constraintName );
 				}
 
 				return null;
 			}
 		};
 	}
 
 	@Override
 	public int registerResultSetOutParameter(CallableStatement statement, int col) throws SQLException {
 		//	register the type of the out param - an Oracle specific type
 		statement.registerOutParameter( col, OracleTypesHelper.INSTANCE.getOracleCursorTypeSqlType() );
 		col++;
 		return col;
 	}
 
 	@Override
 	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
 		ps.execute();
 		return (ResultSet) ps.getObject( 1 );
 	}
 
 	@Override
 	public boolean supportsUnionAll() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsCommentOn() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsTemporaryTables() {
 		return true;
 	}
 
 	@Override
 	public String generateTemporaryTableName(String baseTableName) {
 		final String name = super.generateTemporaryTableName( baseTableName );
 		return name.length() > 30 ? name.substring( 1, 30 ) : name;
 	}
 
 	@Override
 	public String getCreateTemporaryTableString() {
 		return "create global temporary table";
 	}
 
 	@Override
 	public String getCreateTemporaryTablePostfix() {
 		return "on commit delete rows";
 	}
 
 	@Override
 	public boolean dropTemporaryTableAfterUse() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsEmptyInList() {
 		return false;
 	}
 	
 	@Override
 	public boolean supportsExistsInSelect() {
 		return false;
 	}
 
 	@Override
 	public int getInExpressionCountLimit() {
 		return PARAM_LIST_SIZE_LIMIT;
 	}
 	
 	@Override
 	public boolean forceLobAsLastValue() {
 		return true;
 	}
 
 	@Override
 	public boolean useFollowOnLocking() {
 		return true;
 	}
 	
 	@Override
 	public String getNotExpression( String expression ) {
 		return "not (" + expression + ")";
 	}
+	
+	@Override
+	public String getQueryHintString(String sql, List<String> hints) {
+		final String hint = StringHelper.join( ", ", hints.iterator() );
+		
+		if ( StringHelper.isEmpty( hint ) ) {
+			return sql;
+		}
+
+		final int pos = sql.indexOf( "select" );
+		if ( pos > -1 ) {
+			final StringBuilder buffer = new StringBuilder( sql.length() + hint.length() + 8 );
+			if ( pos > 0 ) {
+				buffer.append( sql.substring( 0, pos ) );
+			}
+			buffer.append( "select /*+ " ).append( hint ).append( " */" )
+					.append( sql.substring( pos + "select".length() ) );
+			sql = buffer.toString();
+		}
+
+		return sql;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java
index 94ecdda162..b0a1215cd1 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java
@@ -1,575 +1,592 @@
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
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.StringTokenizer;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.hql.internal.classic.ParserHelper;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.FilterImpl;
 import org.hibernate.internal.util.EntityPrinter;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 /**
  * @author Gavin King
  */
 public final class QueryParameters {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, QueryParameters.class.getName());
 
 	private Type[] positionalParameterTypes;
 	private Object[] positionalParameterValues;
 	private Map<String,TypedValue> namedParameters;
 	private LockOptions lockOptions;
 	private RowSelection rowSelection;
 	private boolean cacheable;
 	private String cacheRegion;
 	private String comment;
+	private List<String> queryHints;
 	private ScrollMode scrollMode;
 	private Serializable[] collectionKeys;
 	private Object optionalObject;
 	private String optionalEntityName;
 	private Serializable optionalId;
 	private boolean isReadOnlyInitialized;
 	private boolean readOnly;
 	private boolean callable = false;
 	private boolean autodiscovertypes = false;
 	private boolean isNaturalKeyLookup;
 
 	private final ResultTransformer resultTransformer; // why is all others non final ?
 
 	private String processedSQL;
 	private Type[] processedPositionalParameterTypes;
 	private Object[] processedPositionalParameterValues;
 
 	public QueryParameters() {
 		this( ArrayHelper.EMPTY_TYPE_ARRAY, ArrayHelper.EMPTY_OBJECT_ARRAY );
 	}
 
 	public QueryParameters(Type type, Object value) {
 		this( new Type[] { type }, new Object[] { value } );
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] positionalParameterValues,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalObjectId) {
 		this( positionalParameterTypes, positionalParameterValues );
 		this.optionalObject = optionalObject;
 		this.optionalId = optionalObjectId;
 		this.optionalEntityName = optionalEntityName;
 
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] positionalParameterValues) {
-		this( positionalParameterTypes, positionalParameterValues, null, null, false, false, false, null, null, false, null );
+		this( positionalParameterTypes, positionalParameterValues, null, null, false, false, false, null, null, null, false, null );
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] positionalParameterValues,
 			final Serializable[] collectionKeys) {
 		this( positionalParameterTypes, positionalParameterValues, null, collectionKeys );
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] positionalParameterValues,
 			final Map<String,TypedValue> namedParameters,
 			final Serializable[] collectionKeys) {
 		this(
 				positionalParameterTypes,
 				positionalParameterValues,
 				namedParameters,
 				null,
 				null,
 				false,
 				false,
 				false,
 				null,
 				null,
+				null,
 				collectionKeys,
 				null
 		);
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] positionalParameterValues,
 			final LockOptions lockOptions,
 			final RowSelection rowSelection,
 			final boolean isReadOnlyInitialized,
 			final boolean readOnly,
 			final boolean cacheable,
 			final String cacheRegion,
 			//final boolean forceCacheRefresh,
 			final String comment,
+			final List<String> queryHints,
 			final boolean isLookupByNaturalKey,
 			final ResultTransformer transformer) {
 		this(
 				positionalParameterTypes,
 				positionalParameterValues,
 				null,
 				lockOptions,
 				rowSelection,
 				isReadOnlyInitialized,
 				readOnly,
 				cacheable,
 				cacheRegion,
 				comment,
+				queryHints,
 				null,
 				transformer
 		);
 		isNaturalKeyLookup = isLookupByNaturalKey;
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] positionalParameterValues,
 			final Map<String,TypedValue> namedParameters,
 			final LockOptions lockOptions,
 			final RowSelection rowSelection,
 			final boolean isReadOnlyInitialized,
 			final boolean readOnly,
 			final boolean cacheable,
 			final String cacheRegion,
 			//final boolean forceCacheRefresh,
 			final String comment,
+			final List<String> queryHints,
 			final Serializable[] collectionKeys,
 			ResultTransformer transformer) {
 		this.positionalParameterTypes = positionalParameterTypes;
 		this.positionalParameterValues = positionalParameterValues;
 		this.namedParameters = namedParameters;
 		this.lockOptions = lockOptions;
 		this.rowSelection = rowSelection;
 		this.cacheable = cacheable;
 		this.cacheRegion = cacheRegion;
 		//this.forceCacheRefresh = forceCacheRefresh;
 		this.comment = comment;
+		this.queryHints = queryHints;
 		this.collectionKeys = collectionKeys;
 		this.isReadOnlyInitialized = isReadOnlyInitialized;
 		this.readOnly = readOnly;
 		this.resultTransformer = transformer;
 	}
 
 	public QueryParameters(
 			final Type[] positionalParameterTypes,
 			final Object[] positionalParameterValues,
 			final Map<String,TypedValue> namedParameters,
 			final LockOptions lockOptions,
 			final RowSelection rowSelection,
 			final boolean isReadOnlyInitialized,
 			final boolean readOnly,
 			final boolean cacheable,
 			final String cacheRegion,
 			//final boolean forceCacheRefresh,
 			final String comment,
+			final List<String> queryHints,
 			final Serializable[] collectionKeys,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalId,
 			final ResultTransformer transformer) {
 		this(
 				positionalParameterTypes,
 				positionalParameterValues,
 				namedParameters,
 				lockOptions,
 				rowSelection,
 				isReadOnlyInitialized,
 				readOnly,
 				cacheable,
 				cacheRegion,
 				comment,
+				queryHints,
 				collectionKeys,
 				transformer
 		);
 		this.optionalEntityName = optionalEntityName;
 		this.optionalId = optionalId;
 		this.optionalObject = optionalObject;
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean hasRowSelection() {
 		return rowSelection != null;
 	}
 
 	public Map<String,TypedValue> getNamedParameters() {
 		return namedParameters;
 	}
 
 	public Type[] getPositionalParameterTypes() {
 		return positionalParameterTypes;
 	}
 
 	public Object[] getPositionalParameterValues() {
 		return positionalParameterValues;
 	}
 
 	public RowSelection getRowSelection() {
 		return rowSelection;
 	}
 
 	public ResultTransformer getResultTransformer() {
 		return resultTransformer;
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void setNamedParameters(Map<String,TypedValue> map) {
 		namedParameters = map;
 	}
 
 	public void setPositionalParameterTypes(Type[] types) {
 		positionalParameterTypes = types;
 	}
 
 	public void setPositionalParameterValues(Object[] objects) {
 		positionalParameterValues = objects;
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void setRowSelection(RowSelection selection) {
 		rowSelection = selection;
 	}
 
 	public LockOptions getLockOptions() {
 		return lockOptions;
 	}
 
 	public void setLockOptions(LockOptions lockOptions) {
 		this.lockOptions = lockOptions;
 	}
 
 	public void traceParameters(SessionFactoryImplementor factory) throws HibernateException {
 		EntityPrinter print = new EntityPrinter( factory );
 		if ( positionalParameterValues.length != 0 ) {
 			LOG.tracev( "Parameters: {0}", print.toString( positionalParameterTypes, positionalParameterValues ) );
 		}
 		if ( namedParameters != null ) {
 			LOG.tracev( "Named parameters: {0}", print.toString( namedParameters ) );
 		}
 	}
 
 	public boolean isCacheable() {
 		return cacheable;
 	}
 
 	public void setCacheable(boolean b) {
 		cacheable = b;
 	}
 
 	public String getCacheRegion() {
 		return cacheRegion;
 	}
 
 	public void setCacheRegion(String cacheRegion) {
 		this.cacheRegion = cacheRegion;
 	}
 
 	public void validateParameters() throws QueryException {
 		int types = positionalParameterTypes == null ? 0 : positionalParameterTypes.length;
 		int values = positionalParameterValues == null ? 0 : positionalParameterValues.length;
 		if ( types != values ) {
 			throw new QueryException(
 					"Number of positional parameter types:" + types +
 							" does not match number of positional parameters: " + values
 			);
 		}
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
+	  
+	public List<String> getQueryHints() {
+		return queryHints;
+	}
+
+	public void setQueryHints(List<String> queryHints) {
+		this.queryHints = queryHints;
+	}
 
 	public ScrollMode getScrollMode() {
 		return scrollMode;
 	}
 
 	public void setScrollMode(ScrollMode scrollMode) {
 		this.scrollMode = scrollMode;
 	}
 
 	public Serializable[] getCollectionKeys() {
 		return collectionKeys;
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void setCollectionKeys(Serializable[] collectionKeys) {
 		this.collectionKeys = collectionKeys;
 	}
 
 	public String getOptionalEntityName() {
 		return optionalEntityName;
 	}
 
 	public void setOptionalEntityName(String optionalEntityName) {
 		this.optionalEntityName = optionalEntityName;
 	}
 
 	public Serializable getOptionalId() {
 		return optionalId;
 	}
 
 	public void setOptionalId(Serializable optionalId) {
 		this.optionalId = optionalId;
 	}
 
 	public Object getOptionalObject() {
 		return optionalObject;
 	}
 
 	public void setOptionalObject(Object optionalObject) {
 		this.optionalObject = optionalObject;
 	}
 
 	/**
 	 * Has the read-only/modifiable mode been explicitly set?
 	 * @see QueryParameters#setReadOnly(boolean)
 	 * @see QueryParameters#isReadOnly(org.hibernate.engine.spi.SessionImplementor)
 	 *
 	 * @return true, the read-only/modifiable mode was explicitly set
 	 *         false, the read-only/modifiable mode was not explicitly set
 	 */
 	public boolean isReadOnlyInitialized() {
 		return isReadOnlyInitialized;
 	}
 
 	/**
 	 * Should entities and proxies loaded by the Query be put in read-only mode? The
 	 * read-only/modifiable setting must be initialized via QueryParameters#setReadOnly(boolean)
 	 * before calling this method.
 	 *
 	 * @see QueryParameters#isReadOnlyInitialized()
 	 * @see QueryParameters#isReadOnly(org.hibernate.engine.spi.SessionImplementor)
 	 * @see QueryParameters#setReadOnly(boolean)
 	 *
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * query that existed in the session before the query was executed.
 	 *
 	 * @return true, entities and proxies loaded by the Query will be put in read-only mode
 	 *         false, entities and proxies loaded by the Query will be put in modifiable mode
 	 * @throws IllegalStateException if the read-only/modifiable setting has not been
 	 * initialized (i.e., isReadOnlyInitialized() == false).
 	 */
 	public boolean isReadOnly() {
 		if ( ! isReadOnlyInitialized() ) {
 			throw new IllegalStateException( "cannot call isReadOnly() when isReadOnlyInitialized() returns false" );
 		}
 		return readOnly;
 	}
 
 	/**
 	 * Should entities and proxies loaded by the Query be put in read-only mode?  If the
 	 * read-only/modifiable setting was not initialized (i.e., QueryParameters#isReadOnlyInitialized() == false),
 	 * then the default read-only/modifiable setting for the persistence context is returned instead.
 	 * <p/>
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * query that existed in the session before the query was executed.
 	 *
 	 * @param session The originating session
 	 *
 	 * @return {@code true} indicates that entities and proxies loaded by the query will be put in read-only mode;
 	 * {@code false} indicates that entities and proxies loaded by the query will be put in modifiable mode
 	 *
 	 * @see QueryParameters#isReadOnlyInitialized()
 	 * @see QueryParameters#setReadOnly(boolean)
 	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
 	 *
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * query that existed in the session before the query was executed.
 	 *
 	 */
 	public boolean isReadOnly(SessionImplementor session) {
 		return isReadOnlyInitialized
 				? isReadOnly()
 				: session.getPersistenceContext().isDefaultReadOnly();
 	}
 
 	/**
 	 * Set the read-only/modifiable mode for entities and proxies loaded by the query.
 	 * <p/>
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * query that existed in the session before the query was executed.
 	 *
 	 * @param readOnly if {@code true}, entities and proxies loaded by the query will be put in read-only mode; if
 	 * {@code false}, entities and proxies loaded by the query will be put in modifiable mode
 	 *
 	 * @see QueryParameters#isReadOnlyInitialized()
 	 * @see QueryParameters#isReadOnly(org.hibernate.engine.spi.SessionImplementor)
 	 * @see QueryParameters#setReadOnly(boolean)
 	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
 	 */
 	public void setReadOnly(boolean readOnly) {
 		this.readOnly = readOnly;
 		this.isReadOnlyInitialized = true;
 	}
 
 	public void setCallable(boolean callable) {
 		this.callable = callable;
 	}
 
 	public boolean isCallable() {
 		return callable;
 	}
 
 	public boolean hasAutoDiscoverScalarTypes() {
 		return autodiscovertypes;
 	}
 
 	public void processFilters(String sql, SessionImplementor session) {
 		processFilters( sql, session.getLoadQueryInfluencers().getEnabledFilters(), session.getFactory() );
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public void processFilters(String sql, Map filters, SessionFactoryImplementor factory) {
 		if ( filters.size() == 0 || !sql.contains( ParserHelper.HQL_VARIABLE_PREFIX ) ) {
 			// HELLA IMPORTANT OPTIMIZATION!!!
 			processedPositionalParameterValues = getPositionalParameterValues();
 			processedPositionalParameterTypes = getPositionalParameterTypes();
 			processedSQL = sql;
 		}
 		else {
 			final Dialect dialect = factory.getDialect();
 			String symbols = new StringBuilder().append( ParserHelper.HQL_SEPARATORS )
 					.append( dialect.openQuote() )
 					.append( dialect.closeQuote() )
 					.toString();
 			StringTokenizer tokens = new StringTokenizer( sql, symbols, true );
 			StringBuilder result = new StringBuilder();
 
 			List parameters = new ArrayList();
 			List parameterTypes = new ArrayList();
 
 			int positionalIndex = 0;
 			while ( tokens.hasMoreTokens() ) {
 				final String token = tokens.nextToken();
 				if ( token.startsWith( ParserHelper.HQL_VARIABLE_PREFIX ) ) {
 					final String filterParameterName = token.substring( 1 );
 					final String[] parts = LoadQueryInfluencers.parseFilterParameterName( filterParameterName );
 					final FilterImpl filter = ( FilterImpl ) filters.get( parts[0] );
 					final Object value = filter.getParameter( parts[1] );
 					final Type type = filter.getFilterDefinition().getParameterType( parts[1] );
 					if ( value != null && Collection.class.isAssignableFrom( value.getClass() ) ) {
 						Iterator itr = ( ( Collection ) value ).iterator();
 						while ( itr.hasNext() ) {
 							Object elementValue = itr.next();
 							result.append( '?' );
 							parameters.add( elementValue );
 							parameterTypes.add( type );
 							if ( itr.hasNext() ) {
 								result.append( ", " );
 							}
 						}
 					}
 					else {
 						result.append( '?' );
 						parameters.add( value );
 						parameterTypes.add( type );
 					}
 				}
 				else {
 					if ( "?".equals( token ) && positionalIndex < getPositionalParameterValues().length ) {
 						parameters.add( getPositionalParameterValues()[positionalIndex] );
 						parameterTypes.add( getPositionalParameterTypes()[positionalIndex] );
 						positionalIndex++;
 					}
 					result.append( token );
 				}
 			}
 			processedPositionalParameterValues = parameters.toArray();
 			processedPositionalParameterTypes = ( Type[] ) parameterTypes.toArray( new Type[parameterTypes.size()] );
 			processedSQL = result.toString();
 		}
 	}
 
 	public String getFilteredSQL() {
 		return processedSQL;
 	}
 
 	public Object[] getFilteredPositionalParameterValues() {
 		return processedPositionalParameterValues;
 	}
 
 	public Type[] getFilteredPositionalParameterTypes() {
 		return processedPositionalParameterTypes;
 	}
 
 	public boolean isNaturalKeyLookup() {
 		return isNaturalKeyLookup;
 	}
 
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public void setNaturalKeyLookup(boolean isNaturalKeyLookup) {
 		this.isNaturalKeyLookup = isNaturalKeyLookup;
 	}
 
 	public void setAutoDiscoverScalarTypes(boolean autodiscovertypes) {
 		this.autodiscovertypes = autodiscovertypes;
 	}
 
 	public QueryParameters createCopyUsing(RowSelection selection) {
 		QueryParameters copy = new QueryParameters(
 				this.positionalParameterTypes,
 				this.positionalParameterValues,
 				this.namedParameters,
 				this.lockOptions,
 				selection,
 				this.isReadOnlyInitialized,
 				this.readOnly,
 				this.cacheable,
 				this.cacheRegion,
 				this.comment,
+				this.queryHints,
 				this.collectionKeys,
 				this.optionalObject,
 				this.optionalEntityName,
 				this.optionalId,
 				this.resultTransformer
 		);
 		copy.processedSQL = this.processedSQL;
 		copy.processedPositionalParameterTypes = this.processedPositionalParameterTypes;
 		copy.processedPositionalParameterValues = this.processedPositionalParameterValues;
 		return copy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
index 62cfa9d945..156af37ada 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractQueryImpl.java
@@ -1,1019 +1,1027 @@
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
 import org.hibernate.dialect.Dialect;
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
 import org.jboss.logging.Logger;
 
 /**
  * Abstract implementation of the Query interface.
  *
  * @author Gavin King
  * @author Max Andersen
  */
 public abstract class AbstractQueryImpl implements Query {
 	private static final CoreMessageLogger log = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			AbstractQueryImpl.class.getName()
 	);
 
 	private static final Object UNSET_PARAMETER = new MarkerObject("<unset parameter>");
 	private static final Object UNSET_TYPE = new MarkerObject("<unset type>");
 
 	private final String queryString;
 	protected final SessionImplementor session;
 	protected final ParameterMetadata parameterMetadata;
 
 	// parameter bind values...
 	private List values = new ArrayList(4);
 	private List types = new ArrayList(4);
 	private Map<String,TypedValue> namedParameters = new HashMap<String, TypedValue>(4);
 	private Map<String, TypedValue> namedParameterLists = new HashMap<String, TypedValue>(4);
 
 	private Object optionalObject;
 	private Serializable optionalId;
 	private String optionalEntityName;
 
 	private RowSelection selection;
 	private boolean cacheable;
 	private String cacheRegion;
 	private String comment;
+	private final List<String> queryHints = new ArrayList<String>();
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
 
 	@Override
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + queryString + ')';
 	}
 
 	@Override
 	public final String getQueryString() {
 		return queryString;
 	}
 
 	@Override
 	public boolean isCacheable() {
 		return cacheable;
 	}
 
 	@Override
 	public Query setCacheable(boolean cacheable) {
 		this.cacheable = cacheable;
 		return this;
 	}
 
 	@Override
 	public String getCacheRegion() {
 		return cacheRegion;
 	}
 
 	@Override
 	public Query setCacheRegion(String cacheRegion) {
 		if (cacheRegion != null) {
 			this.cacheRegion = cacheRegion.trim();
 		}
 		return this;
 	}
 
 	@Override
 	public FlushMode getFlushMode() {
 		return flushMode;
 	}
 
 	@Override
 	public Query setFlushMode(FlushMode flushMode) {
 		this.flushMode = flushMode;
 		return this;
 	}
 
 	@Override
 	public CacheMode getCacheMode() {
 		return cacheMode;
 	}
 
 	@Override
 	public Query setCacheMode(CacheMode cacheMode) {
 		this.cacheMode = cacheMode;
 		return this;
 	}
 
 	@Override
 	public String getComment() {
 		return comment;
 	}
 
 	@Override
 	public Query setComment(String comment) {
 		this.comment = comment;
 		return this;
 	}
+	  
+	@Override
+	public Query addQueryHint(String queryHint) {
+		queryHints.add( queryHint );
+		return this;
+	} 
 
 	@Override
 	public Integer getFirstResult() {
 		return selection.getFirstRow();
 	}
 
 	@Override
 	public Query setFirstResult(int firstResult) {
 		selection.setFirstRow( firstResult);
 		return this;
 	}
 
 	@Override
 	public Integer getMaxResults() {
 		return selection.getMaxRows();
 	}
 
 	@Override
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
 
 	@Override
 	public Integer getTimeout() {
 		return selection.getTimeout();
 	}
 
 	@Override
 	public Query setTimeout(int timeout) {
 		selection.setTimeout( timeout);
 		return this;
 	}
 
 	@Override
 	public Integer getFetchSize() {
 		return selection.getFetchSize();
 	}
 
 	@Override
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
 
 	@Override
 	public boolean isReadOnly() {
 		return ( readOnly == null ?
 				getSession().getPersistenceContext().isDefaultReadOnly() :
 				readOnly
 		);
 	}
 
 	@Override
 	public Query setReadOnly(boolean readOnly) {
 		this.readOnly = readOnly;
 		return this;
 	}
 	@Override
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
 	@Override
 	public abstract LockOptions getLockOptions();
 
 
 	// Parameter handling code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Returns a shallow copy of the named parameter value map.
 	 *
 	 * @return Shallow copy of the named parameter value map
 	 */
 	protected Map<String, TypedValue> getNamedParams() {
 		return new HashMap<String, TypedValue>( namedParameters );
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
 	@Override
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
 	protected Map<String, TypedValue> getNamedParameterLists() {
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
 			Set<String> missingParams = new HashSet<String>( parameterMetadata.getNamedParameterNames() );
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
 		setParameter( position, Character.valueOf( val ), StandardBasicTypes.CHARACTER );
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
 
 	@Override
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
 		for ( Map.Entry<String, TypedValue> stringTypedValueEntry : namedParameterLists.entrySet() ) {
 			Map.Entry me = (Map.Entry) stringTypedValueEntry;
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
 		
 		// HHH-1123
 		// Some DBs limit number of IN expressions.  For now, warn...
 		final Dialect dialect = session.getFactory().getDialect();
 		final int inExprLimit = dialect.getInExpressionCountLimit();
 		if ( inExprLimit > 0 && vals.size() > inExprLimit ) {
 			log.tooManyInExpressions( dialect.getClass().getName(), inExprLimit, name, vals.size() );
 		}
 
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
 
 	public Query setParameterList(String name, Object[] values) throws HibernateException {
 		return setParameterList( name, Arrays.asList( values ) );
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
 				getRowSelection(),
 				true,
 				isReadOnly(),
 				cacheable,
 				cacheRegion,
 				comment,
+				queryHints,
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/CriteriaImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/CriteriaImpl.java
index 9eb2af6835..90e14273c9 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/CriteriaImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CriteriaImpl.java
@@ -1,722 +1,740 @@
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
 package org.hibernate.internal;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.CacheMode;
 import org.hibernate.Criteria;
 import org.hibernate.FetchMode;
 import org.hibernate.FlushMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.ScrollMode;
 import org.hibernate.ScrollableResults;
 import org.hibernate.criterion.Criterion;
 import org.hibernate.criterion.NaturalIdentifier;
 import org.hibernate.criterion.Order;
 import org.hibernate.criterion.Projection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.sql.JoinType;
 import org.hibernate.transform.ResultTransformer;
 
 /**
  * Implementation of the <tt>Criteria</tt> interface
  * @author Gavin King
  */
 public class CriteriaImpl implements Criteria, Serializable {
 
 	private final String entityOrClassName;
 	private transient SessionImplementor session;
 	private final String rootAlias;
 
 	private List<CriterionEntry> criterionEntries = new ArrayList<CriterionEntry>();
 	private List<OrderEntry> orderEntries = new ArrayList<OrderEntry>();
 	private Projection projection;
 	private Criteria projectionCriteria;
 
 	private List<Subcriteria> subcriteriaList = new ArrayList<Subcriteria>();
 
 	private Map<String, FetchMode> fetchModes = new HashMap<String, FetchMode>();
 	private Map<String, LockMode> lockModes = new HashMap<String, LockMode>();
 
 	private Integer maxResults;
 	private Integer firstResult;
 	private Integer timeout;
 	private Integer fetchSize;
 
 	private boolean cacheable;
 	private String cacheRegion;
 	private String comment;
+	private final List<String> queryHints = new ArrayList<String>();
 
 	private FlushMode flushMode;
 	private CacheMode cacheMode;
 	private FlushMode sessionFlushMode;
 	private CacheMode sessionCacheMode;
 
 	private Boolean readOnly;
 
 	private ResultTransformer resultTransformer = Criteria.ROOT_ENTITY;
 
 
 	// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public CriteriaImpl(String entityOrClassName, SessionImplementor session) {
 		this(entityOrClassName, ROOT_ALIAS, session);
 	}
 
 	public CriteriaImpl(String entityOrClassName, String alias, SessionImplementor session) {
 		this.session = session;
 		this.entityOrClassName = entityOrClassName;
 		this.cacheable = false;
 		this.rootAlias = alias;
 	}
 	@Override
 	public String toString() {
 		return "CriteriaImpl(" +
 			entityOrClassName + ":" +
 			(rootAlias==null ? "" : rootAlias) +
 			subcriteriaList.toString() +
 			criterionEntries.toString() +
 			( projection==null ? "" : projection.toString() ) +
 			')';
 	}
 
 
 	// State ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public SessionImplementor getSession() {
 		return session;
 	}
 
 	public void setSession(SessionImplementor session) {
 		this.session = session;
 	}
 
 	public String getEntityOrClassName() {
 		return entityOrClassName;
 	}
 
 	public Map<String, LockMode> getLockModes() {
 		return lockModes;
 	}
 
 	public Criteria getProjectionCriteria() {
 		return projectionCriteria;
 	}
 
 	public Iterator<Subcriteria> iterateSubcriteria() {
 		return subcriteriaList.iterator();
 	}
 
 	public Iterator<CriterionEntry> iterateExpressionEntries() {
 		return criterionEntries.iterator();
 	}
 
 	public Iterator<OrderEntry> iterateOrderings() {
 		return orderEntries.iterator();
 	}
 
 	public Criteria add(Criteria criteriaInst, Criterion expression) {
 		criterionEntries.add( new CriterionEntry(expression, criteriaInst) );
 		return this;
 	}
 
 
 	// Criteria impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	@Override
 	public String getAlias() {
 		return rootAlias;
 	}
 
 	public Projection getProjection() {
 		return projection;
 	}
 	@Override
 	public Criteria setProjection(Projection projection) {
 		this.projection = projection;
 		this.projectionCriteria = this;
 		setResultTransformer( PROJECTION );
 		return this;
 	}
 	@Override
 	public Criteria add(Criterion expression) {
 		add( this, expression );
 		return this;
 	}
 	@Override
 	public Criteria addOrder(Order ordering) {
 		orderEntries.add( new OrderEntry( ordering, this ) );
 		return this;
 	}
 	public FetchMode getFetchMode(String path) {
 		return fetchModes.get(path);
 	}
 	@Override
 	public Criteria setFetchMode(String associationPath, FetchMode mode) {
 		fetchModes.put( associationPath, mode );
 		return this;
 	}
 	@Override
 	public Criteria setLockMode(LockMode lockMode) {
 		return setLockMode( getAlias(), lockMode );
 	}
 	@Override
 	public Criteria setLockMode(String alias, LockMode lockMode) {
 		lockModes.put( alias, lockMode );
 		return this;
 	}
 	@Override
 	public Criteria createAlias(String associationPath, String alias) {
 		return createAlias( associationPath, alias, JoinType.INNER_JOIN );
 	}
 	@Override
 	public Criteria createAlias(String associationPath, String alias, JoinType joinType) {
 		new Subcriteria( this, associationPath, alias, joinType );
 		return this;
 	}
 
 	@Override
 	public Criteria createAlias(String associationPath, String alias, int joinType) throws HibernateException {
 		return createAlias( associationPath, alias, JoinType.parse( joinType ) );
 	}
 	@Override
 	public Criteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause) {
 		new Subcriteria( this, associationPath, alias, joinType, withClause );
 		return this;
 	}
 
 	@Override
 	public Criteria createAlias(String associationPath, String alias, int joinType, Criterion withClause)
 			throws HibernateException {
 		return createAlias( associationPath, alias, JoinType.parse( joinType ), withClause );
 	}
 	@Override
 	public Criteria createCriteria(String associationPath) {
 		return createCriteria( associationPath, JoinType.INNER_JOIN );
 	}
 	@Override
 	public Criteria createCriteria(String associationPath, JoinType joinType) {
 		return new Subcriteria( this, associationPath, joinType );
 	}
 
 	@Override
 	public Criteria createCriteria(String associationPath, int joinType) throws HibernateException {
 		return createCriteria(associationPath, JoinType.parse( joinType ));
 	}
 	@Override
 	public Criteria createCriteria(String associationPath, String alias) {
 		return createCriteria( associationPath, alias, JoinType.INNER_JOIN );
 	}
 	@Override
 	public Criteria createCriteria(String associationPath, String alias, JoinType joinType) {
 		return new Subcriteria( this, associationPath, alias, joinType );
 	}
 
 	@Override
 	public Criteria createCriteria(String associationPath, String alias, int joinType) throws HibernateException {
 		return createCriteria( associationPath, alias, JoinType.parse( joinType ) );
 	}
 	@Override
 	public Criteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause) {
 		return new Subcriteria( this, associationPath, alias, joinType, withClause );
 	}
 
 	@Override
 	public Criteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause)
 			throws HibernateException {
 		return createCriteria( associationPath, alias, JoinType.parse( joinType ), withClause );
 	}
 
 	public ResultTransformer getResultTransformer() {
 		return resultTransformer;
 	}
 	@Override
 	public Criteria setResultTransformer(ResultTransformer tupleMapper) {
 		this.resultTransformer = tupleMapper;
 		return this;
 	}
 
 	public Integer getMaxResults() {
 		return maxResults;
 	}
 	@Override
 	public Criteria setMaxResults(int maxResults) {
 		this.maxResults = maxResults;
 		return this;
 	}
 
 	public Integer getFirstResult() {
 		return firstResult;
 	}
 	@Override
 	public Criteria setFirstResult(int firstResult) {
 		this.firstResult = firstResult;
 		return this;
 	}
 
 	public Integer getFetchSize() {
 		return fetchSize;
 	}
 	@Override
 	public Criteria setFetchSize(int fetchSize) {
 		this.fetchSize = fetchSize;
 		return this;
 	}
 
 	public Integer getTimeout() {
 		return timeout;
 	}
    @Override
 	public Criteria setTimeout(int timeout) {
 		this.timeout = timeout;
 		return this;
 	}
 
 	@Override
 	public boolean isReadOnlyInitialized() {
 		return readOnly != null;
 	}
 
 	@Override
 	public boolean isReadOnly() {
 		if ( ! isReadOnlyInitialized() && getSession() == null ) {
 			throw new IllegalStateException(
 					"cannot determine readOnly/modifiable setting when it is not initialized and is not initialized and getSession() == null"
 			);
 		}
 		return ( isReadOnlyInitialized() ?
 				readOnly :
 				getSession().getPersistenceContext().isDefaultReadOnly()
 		);
 	}
 
 	@Override
 	public Criteria setReadOnly(boolean readOnly) {
 		this.readOnly = readOnly;
 		return this;
 	}
 
 	public boolean getCacheable() {
 		return this.cacheable;
 	}
 	@Override
 	public Criteria setCacheable(boolean cacheable) {
 		this.cacheable = cacheable;
 		return this;
 	}
 
 	public String getCacheRegion() {
 		return this.cacheRegion;
 	}
 	@Override
 	public Criteria setCacheRegion(String cacheRegion) {
 		this.cacheRegion = cacheRegion.trim();
 		return this;
 	}
 
 	public String getComment() {
 		return comment;
 	}
+	
 	@Override
 	public Criteria setComment(String comment) {
 		this.comment = comment;
 		return this;
 	}
+
+	public List<String> getQueryHints() {
+		return queryHints;
+	}
+
+	@Override
+	public Criteria addQueryHint(String queryHint) {
+		queryHints.add( queryHint );
+		return this;
+	}
+	
 	@Override
 	public Criteria setFlushMode(FlushMode flushMode) {
 		this.flushMode = flushMode;
 		return this;
 	}
 	@Override
 	public Criteria setCacheMode(CacheMode cacheMode) {
 		this.cacheMode = cacheMode;
 		return this;
 	}
 	@Override
 	public List list() throws HibernateException {
 		before();
 		try {
 			return session.list( this );
 		}
 		finally {
 			after();
 		}
 	}
 	@Override
 	public ScrollableResults scroll() {
 		return scroll( ScrollMode.SCROLL_INSENSITIVE );
 	}
 	@Override
 	public ScrollableResults scroll(ScrollMode scrollMode) {
 		before();
 		try {
 			return session.scroll(this, scrollMode);
 		}
 		finally {
 			after();
 		}
 	}
 	@Override
 	public Object uniqueResult() throws HibernateException {
 		return AbstractQueryImpl.uniqueElement( list() );
 	}
 
 	protected void before() {
 		if ( flushMode != null ) {
 			sessionFlushMode = getSession().getFlushMode();
 			getSession().setFlushMode( flushMode );
 		}
 		if ( cacheMode != null ) {
 			sessionCacheMode = getSession().getCacheMode();
 			getSession().setCacheMode( cacheMode );
 		}
 	}
 
 	protected void after() {
 		if ( sessionFlushMode != null ) {
 			getSession().setFlushMode( sessionFlushMode );
 			sessionFlushMode = null;
 		}
 		if ( sessionCacheMode != null ) {
 			getSession().setCacheMode( sessionCacheMode );
 			sessionCacheMode = null;
 		}
 	}
 
 	public boolean isLookupByNaturalKey() {
 		if ( projection != null ) {
 			return false;
 		}
 		if ( subcriteriaList.size() > 0 ) {
 			return false;
 		}
 		if ( criterionEntries.size() != 1 ) {
 			return false;
 		}
 		CriterionEntry ce = criterionEntries.get(0);
 		return ce.getCriterion() instanceof NaturalIdentifier;
 	}
 
 
 	// Inner classes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public final class Subcriteria implements Criteria, Serializable {
 
 		private String alias;
 		private String path;
 		private Criteria parent;
 		private LockMode lockMode;
 		private JoinType joinType = JoinType.INNER_JOIN;
 		private Criterion withClause;
 		private boolean hasRestriction;
 
 		// Constructors ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		private Subcriteria(Criteria parent, String path, String alias, JoinType joinType, Criterion withClause) {
 			this.alias = alias;
 			this.path = path;
 			this.parent = parent;
 			this.joinType = joinType;
 			this.withClause = withClause;
 			this.hasRestriction = withClause != null;
 			CriteriaImpl.this.subcriteriaList.add( this );
 		}
 
 		private Subcriteria(Criteria parent, String path, String alias, JoinType joinType) {
 			this( parent, path, alias, joinType, null );
 		}
 
 		private Subcriteria(Criteria parent, String path, JoinType joinType) {
 			this( parent, path, null, joinType );
 		}
 		@Override
 		public String toString() {
 			return "Subcriteria("
 					+ path + ":"
 					+ (alias==null ? "" : alias)
 					+ ')';
 		}
 
 
 		// State ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		@Override
 		public String getAlias() {
 			return alias;
 		}
 
 		public void setAlias(String alias) {
 			this.alias = alias;
 		}
 
 		public String getPath() {
 			return path;
 		}
 
 		public Criteria getParent() {
 			return parent;
 		}
 
 		public LockMode getLockMode() {
 			return lockMode;
 		}
 		@Override
 		public Criteria setLockMode(LockMode lockMode) {
 			this.lockMode = lockMode;
 			return this;
 		}
 
 		public JoinType getJoinType() {
 			return joinType;
 		}
 
 		public Criterion getWithClause() {
 			return this.withClause;
 		}
 
 		public boolean hasRestriction() {
 			return hasRestriction;
 		}
 
 		// Criteria impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		@Override
 		public Criteria add(Criterion expression) {
 			hasRestriction = true;
 			CriteriaImpl.this.add(this, expression);
 			return this;
 		}
 		@Override
 		public Criteria addOrder(Order order) {
 			CriteriaImpl.this.orderEntries.add( new OrderEntry(order, this) );
 			return this;
 		}
 		@Override
 		public Criteria createAlias(String associationPath, String alias) {
 			return createAlias( associationPath, alias, JoinType.INNER_JOIN );
 		}
 		@Override
 		public Criteria createAlias(String associationPath, String alias, JoinType joinType) throws HibernateException {
 			new Subcriteria( this, associationPath, alias, joinType );
 			return this;
 		}
 
 		@Override
 		public Criteria createAlias(String associationPath, String alias, int joinType) throws HibernateException {
 			return createAlias( associationPath, alias, JoinType.parse( joinType ) );
 		}
 		@Override
 		public Criteria createAlias(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException {
 			new Subcriteria( this, associationPath, alias, joinType, withClause );
 			return this;
 		}
 
 		@Override
 		public Criteria createAlias(String associationPath, String alias, int joinType, Criterion withClause)
 				throws HibernateException {
 			return createAlias( associationPath, alias, JoinType.parse( joinType ), withClause );
 		}
 		@Override
 		public Criteria createCriteria(String associationPath) {
 			return createCriteria( associationPath, JoinType.INNER_JOIN );
 		}
 		@Override
 		public Criteria createCriteria(String associationPath, JoinType joinType) throws HibernateException {
 			return new Subcriteria( Subcriteria.this, associationPath, joinType );
 		}
 
 		@Override
 		public Criteria createCriteria(String associationPath, int joinType) throws HibernateException {
 			return createCriteria( associationPath, JoinType.parse( joinType ) );
 		}
 		@Override
 		public Criteria createCriteria(String associationPath, String alias) {
 			return createCriteria( associationPath, alias, JoinType.INNER_JOIN );
 		}
 		@Override
 		public Criteria createCriteria(String associationPath, String alias, JoinType joinType) throws HibernateException {
 			return new Subcriteria( Subcriteria.this, associationPath, alias, joinType );
 		}
 
 		@Override
 		public Criteria createCriteria(String associationPath, String alias, int joinType) throws HibernateException {
 			return createCriteria( associationPath, alias, JoinType.parse( joinType ) );
 		}
 		@Override
 		public Criteria createCriteria(String associationPath, String alias, JoinType joinType, Criterion withClause) throws HibernateException {
 			return new Subcriteria( this, associationPath, alias, joinType, withClause );
 		}
 
 		@Override
 		public Criteria createCriteria(String associationPath, String alias, int joinType, Criterion withClause)
 				throws HibernateException {
 			return createCriteria( associationPath, alias, JoinType.parse( joinType ), withClause );
 		}
 		@Override
 		public boolean isReadOnly() {
 			return CriteriaImpl.this.isReadOnly();
 		}
 		@Override
 		public boolean isReadOnlyInitialized() {
 			return CriteriaImpl.this.isReadOnlyInitialized();
 		}
 		@Override
 		public Criteria setReadOnly(boolean readOnly) {
 			CriteriaImpl.this.setReadOnly( readOnly );
 			return this;
 		}
 		@Override
 		public Criteria setCacheable(boolean cacheable) {
 			CriteriaImpl.this.setCacheable(cacheable);
 			return this;
 		}
 		@Override
 		public Criteria setCacheRegion(String cacheRegion) {
 			CriteriaImpl.this.setCacheRegion(cacheRegion);
 			return this;
 		}
 		@Override
 		public List list() throws HibernateException {
 			return CriteriaImpl.this.list();
 		}
 		@Override
 		public ScrollableResults scroll() throws HibernateException {
 			return CriteriaImpl.this.scroll();
 		}
 		@Override
 		public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
 			return CriteriaImpl.this.scroll(scrollMode);
 		}
 		@Override
 		public Object uniqueResult() throws HibernateException {
 			return CriteriaImpl.this.uniqueResult();
 		}
 		@Override
 		public Criteria setFetchMode(String associationPath, FetchMode mode) {
 			CriteriaImpl.this.setFetchMode( StringHelper.qualify(path, associationPath), mode);
 			return this;
 		}
 		@Override
 		public Criteria setFlushMode(FlushMode flushMode) {
 			CriteriaImpl.this.setFlushMode(flushMode);
 			return this;
 		}
 		@Override
 		public Criteria setCacheMode(CacheMode cacheMode) {
 			CriteriaImpl.this.setCacheMode(cacheMode);
 			return this;
 		}
 		@Override
 		public Criteria setFirstResult(int firstResult) {
 			CriteriaImpl.this.setFirstResult(firstResult);
 			return this;
 		}
 		@Override
 		public Criteria setMaxResults(int maxResults) {
 			CriteriaImpl.this.setMaxResults(maxResults);
 			return this;
 		}
 		@Override
 		public Criteria setTimeout(int timeout) {
 			CriteriaImpl.this.setTimeout(timeout);
 			return this;
 		}
 		@Override
 		public Criteria setFetchSize(int fetchSize) {
 			CriteriaImpl.this.setFetchSize(fetchSize);
 			return this;
 		}
 		@Override
 		public Criteria setLockMode(String alias, LockMode lockMode) {
 			CriteriaImpl.this.setLockMode(alias, lockMode);
 			return this;
 		}
 		@Override
 		public Criteria setResultTransformer(ResultTransformer resultProcessor) {
 			CriteriaImpl.this.setResultTransformer(resultProcessor);
 			return this;
 		}
 		@Override
 		public Criteria setComment(String comment) {
 			CriteriaImpl.this.setComment(comment);
 			return this;
+		}   
+		@Override
+		public Criteria addQueryHint(String queryHint) {
+			CriteriaImpl.this.addQueryHint( queryHint );
+			return this;
 		}
 		@Override
 		public Criteria setProjection(Projection projection) {
 			CriteriaImpl.this.projection = projection;
 			CriteriaImpl.this.projectionCriteria = this;
 			setResultTransformer(PROJECTION);
 			return this;
 		}
 	}
 
 	public static final class CriterionEntry implements Serializable {
 		private final Criterion criterion;
 		private final Criteria criteria;
 
 		private CriterionEntry(Criterion criterion, Criteria criteria) {
 			this.criteria = criteria;
 			this.criterion = criterion;
 		}
 
 		public Criterion getCriterion() {
 			return criterion;
 		}
 
 		public Criteria getCriteria() {
 			return criteria;
 		}
 		@Override
 		public String toString() {
 			return criterion.toString();
 		}
 	}
 
 	public static final class OrderEntry implements Serializable {
 		private final Order order;
 		private final Criteria criteria;
 
 		private OrderEntry(Order order, Criteria criteria) {
 			this.criteria = criteria;
 			this.order = order;
 		}
 
 		public Order getOrder() {
 			return order;
 		}
 
 		public Criteria getCriteria() {
 			return criteria;
 		}
 		@Override
 		public String toString() {
 			return order.toString();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 34a652cf1e..9c78c5e8a9 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,2692 +1,2699 @@
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
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
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
 import org.hibernate.Session;
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
 import org.hibernate.engine.spi.EntityEntry;
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
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.loader.spi.AfterLoadAction;
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
    	protected static final boolean DEBUG_ENABLED = LOG.isDebugEnabled();
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
+		
+		// Keep this here, rather than moving to Select.  Some Dialects may need the hint to be appended to the very
+		// end or beginning of the finalized SQL statement, so wait until everything is processed.
+		if ( parameters.getQueryHints() != null && parameters.getQueryHints().size() > 0 ) {
+			sql = dialect.getQueryHintString( sql, parameters.getQueryHints() );
+		}
+		
 		return getFactory().getSettings().isCommentsEnabled()
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
 							public void afterLoad(SessionImplementor session, Object entity, Loadable persister) {
 								( (Session) session ).buildLockRequest( lockOptions ).lock( persister.getEntityName(), entity );
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
 	public List doQueryAndInitializeNonLazyCollections(
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
 
 	public List doQueryAndInitializeNonLazyCollections(
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
 				if ( ! keyToRead.equals( loadedKeys[0] ) ) {
 					throw new AssertionFailure(
 							String.format(
 									"Unexpected key read for row; expected [%s]; actual [%s]",
 									keyToRead,
 									loadedKeys[0] )
 					);
 				}
 				if ( result == null ) {
 					result = loaded;
 				}
 			}
 			while ( resultSet.next() &&
 					isCurrentRowForSameEntity( keyToRead, 0, resultSet, session ) );
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
 
 	private boolean isCurrentRowForSameEntity(
 			final EntityKey keyToRead,
 			final int persisterIndex,
 			final ResultSet resultSet,
 			final SessionImplementor session) throws SQLException {
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
 		catch ( SQLException sqle ) {
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
 
 		return forcedResultTransformer == null
 				? getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session )
 				: forcedResultTransformer.transformTuple( getResultRow( row, resultSet, session ), getResultRowAliases() )
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
 			return processResultSet( rs, queryParameters, session, returnProxies, forcedResultTransformer, maxRows, afterLoadActions );
 		}
 		finally {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 		}
 
 	}
 
 	protected List processResultSet(
 			ResultSet rs,
 			QueryParameters queryParameters,
 			SessionImplementor session,
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
 		   if ( DEBUG_ENABLED )
 			   LOG.debugf( "Result set row: %s", count );
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
 
 		if ( LOG.isTraceEnabled() )
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
 
 	private static Set[] transpose( List keys ) {
 		Set[] result = new Set[ ( ( EntityKey[] ) keys.get(0) ).length ];
 		for ( int j=0; j<result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( Object key : keys ) {
 				result[j].add( ( (EntityKey[]) key )[j] );
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
 			for(String name : queryParameters.getNamedParameters().keySet()){
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
 			final boolean readOnly) throws HibernateException {
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSetId,
 				session,
 				readOnly,
 				Collections.<AfterLoadAction>emptyList()
 		);
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SessionImplementor session,
 			final boolean readOnly,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 
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
 			if ( LOG.isTraceEnabled() )
 			   LOG.tracev( "Total objects hydrated: {0}", hydratedObjectsSize );
 			for ( int i = 0; i < hydratedObjectsSize; i++ ) {
 				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre );
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
 		
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur after
 		// endCollectionLoad to ensure the collection is in the
 		// persistence context.
 		if ( hydratedObjects != null ) {
 			for ( Object hydratedObject : hydratedObjects ) {
 				TwoPhaseLoad.postLoad( hydratedObject, session, post );
 				if ( afterLoadActions != null ) {
 					for ( AfterLoadAction afterLoadAction : afterLoadActions ) {
 						final EntityEntry entityEntry = session.getPersistenceContext().getEntry( hydratedObject );
 						if ( entityEntry == null ) {
 							// big problem
 							throw new HibernateException( "Could not locate EntityEntry immediately after two-phase load" );
 						}
 						afterLoadAction.afterLoad( session, hydratedObject, (Loadable) entityEntry.getPersister() );
 					}
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
 	protected Object getResultColumnOrRow(
 			Object[] row,
 			ResultTransformer transformer,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(
 			Object[] row,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
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
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			// this is a collection initializer, so we must create a collection
 			// for each of the passed-in keys, to account for the possibility
 			// that the collection is empty and has no rows in the result set
 			CollectionPersister[] collectionPersisters = getCollectionPersisters();
 			for ( int j=0; j<collectionPersisters.length; j++ ) {
 				for ( int i = 0; i < keys.length; i++ ) {
 					//handle empty collections
 
 					if ( debugEnabled ) {
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
 	protected SqlStatementWrapper executeQueryStatement(
 			final QueryParameters queryParameters,
 			final boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			final SessionImplementor session) throws SQLException {
 		return executeQueryStatement( getSQLString(), queryParameters, scroll, afterLoadActions, session );
 	}
 
 	protected SqlStatementWrapper executeQueryStatement(
 			String sqlStatement,
 			QueryParameters queryParameters,
 			boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			SessionImplementor session) throws SQLException {
 
 		// Processing query filters.
 		queryParameters.processFilters( sqlStatement, session );
 
 		// Applying LIMIT clause.
 		final LimitHandler limitHandler = getLimitHandler(
 				queryParameters.getFilteredSQL(),
 				queryParameters.getRowSelection()
 		);
 		String sql = limitHandler.getProcessedSql();
 
 		// Adding locks and comments.
 		sql = preprocessSQL( sql, queryParameters, getFactory().getDialect(), afterLoadActions );
 
 		final PreparedStatement st = prepareQueryStatement( sql, queryParameters, limitHandler, scroll, session );
 		return new SqlStatementWrapper( st, getResultSet( st, queryParameters.getRowSelection(), limitHandler, queryParameters.hasAutoDiscoverScalarTypes(), session ) );
 	}
 
 	/**
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
 	protected final PreparedStatement prepareQueryStatement(
-	        final String sql,
+	        String sql,
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
-
+		
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
 
 			if ( LOG.isTraceEnabled() )
 			   LOG.tracev( "Bound [{0}] parameters total", col );
 		}
 		catch ( SQLException sqle ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			throw sqle;
 		}
 		catch ( HibernateException he ) {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 			final Map<String, TypedValue> namedParams,
 			final int startIndex,
 			final SessionImplementor session) throws SQLException, HibernateException {
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
 			ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
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
 			session.getTransactionCoordinator().getJdbcCoordinator().release( st );
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
 			   if ( LOG.isDebugEnabled() )
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
 			LOG.trace( "Building columnName -> columnIndex cache" );
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
 	        final Map<String, TypedValue> namedParameters,
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
 	        final Set<Serializable> querySpaces,
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
 			final Set<Serializable> querySpaces,
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
 			final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, true, Collections.<AfterLoadAction>emptyList(), session );
 			final ResultSet rs = wrapper.getResultSet();
 			final PreparedStatement st = (PreparedStatement) wrapper.getStatement();
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
index 30a8047368..4f67d961db 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaQueryTranslator.java
@@ -1,672 +1,673 @@
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
 package org.hibernate.loader.criteria;
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.hibernate.Criteria;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.criterion.CriteriaQuery;
 import org.hibernate.criterion.Criterion;
 import org.hibernate.criterion.EnhancedProjection;
 import org.hibernate.criterion.Projection;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
 import org.hibernate.internal.CriteriaImpl;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.PropertyMapping;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.StringRepresentableType;
 import org.hibernate.type.Type;
 
 /**
  * @author Gavin King
  */
 public class CriteriaQueryTranslator implements CriteriaQuery {
 
 	public static final String ROOT_SQL_ALIAS = Criteria.ROOT_ALIAS + '_';
 
 	private CriteriaQuery outerQueryTranslator;
 
 	private final CriteriaImpl rootCriteria;
 	private final String rootEntityName;
 	private final String rootSQLAlias;
 
 	private final Map<Criteria, CriteriaInfoProvider> criteriaInfoMap = new LinkedHashMap<Criteria, CriteriaInfoProvider>();
 	private final Map<String, CriteriaInfoProvider> nameCriteriaInfoMap = new LinkedHashMap<String, CriteriaInfoProvider>();
 	private final Map<Criteria, String> criteriaSQLAliasMap = new HashMap<Criteria, String>();
 	private final Map<String, Criteria> aliasCriteriaMap = new HashMap<String, Criteria>();
 	private final Map<String, Criteria> associationPathCriteriaMap = new LinkedHashMap<String, Criteria>();
 	private final Map<String, JoinType> associationPathJoinTypesMap = new LinkedHashMap<String,JoinType>();
 	private final Map<String, Criterion> withClauseMap = new HashMap<String, Criterion>();
 	
 	private final SessionFactoryImplementor sessionFactory;
 	private final SessionFactoryHelper helper;
 
 	public CriteriaQueryTranslator(
 			final SessionFactoryImplementor factory,
 	        final CriteriaImpl criteria,
 	        final String rootEntityName,
 	        final String rootSQLAlias,
 	        CriteriaQuery outerQuery) throws HibernateException {
 		this( factory, criteria, rootEntityName, rootSQLAlias );
 		outerQueryTranslator = outerQuery;
 	}
 
 	public CriteriaQueryTranslator(
 			final SessionFactoryImplementor factory,
 	        final CriteriaImpl criteria,
 	        final String rootEntityName,
 	        final String rootSQLAlias) throws HibernateException {
 		this.rootCriteria = criteria;
 		this.rootEntityName = rootEntityName;
 		this.sessionFactory = factory;
 		this.rootSQLAlias = rootSQLAlias;
 		this.helper = new SessionFactoryHelper(factory);
 		createAliasCriteriaMap();
 		createAssociationPathCriteriaMap();
 		createCriteriaEntityNameMap();
 		createCriteriaSQLAliasMap();
 	}
 	@Override
 	public String generateSQLAlias() {
 		int aliasCount = 0;
 		return StringHelper.generateAlias( Criteria.ROOT_ALIAS, aliasCount ) + '_';
 	}
 
 	public String getRootSQLALias() {
 		return rootSQLAlias;
 	}
 
 	private Criteria getAliasedCriteria(String alias) {
 		return aliasCriteriaMap.get( alias );
 	}
 
 	public boolean isJoin(String path) {
 		return associationPathCriteriaMap.containsKey( path );
 	}
 
 	public JoinType getJoinType(String path) {
 		JoinType result = associationPathJoinTypesMap.get( path );
 		return ( result == null ? JoinType.INNER_JOIN : result );
 	}
 
 	public Criteria getCriteria(String path) {
 		return associationPathCriteriaMap.get( path );
 	}
 
 	public Set<Serializable> getQuerySpaces() {
 		Set<Serializable> result = new HashSet<Serializable>();
 		for ( CriteriaInfoProvider info : criteriaInfoMap.values() ) {
 			result.addAll( Arrays.asList( info.getSpaces() ) );
 		}
 		return result;
 	}
 
 	private void createAliasCriteriaMap() {
 		aliasCriteriaMap.put( rootCriteria.getAlias(), rootCriteria );
 		Iterator<CriteriaImpl.Subcriteria> iter = rootCriteria.iterateSubcriteria();
 		while ( iter.hasNext() ) {
 			Criteria subcriteria = iter.next();
 			if ( subcriteria.getAlias() != null ) {
 				Object old = aliasCriteriaMap.put( subcriteria.getAlias(), subcriteria );
 				if ( old != null ) {
 					throw new QueryException( "duplicate alias: " + subcriteria.getAlias() );
 				}
 			}
 		}
 	}
 
 	private void createAssociationPathCriteriaMap() {
 		final Iterator<CriteriaImpl.Subcriteria> iter = rootCriteria.iterateSubcriteria();
 		while ( iter.hasNext() ) {
 			CriteriaImpl.Subcriteria crit = iter.next();
 			String wholeAssociationPath = getWholeAssociationPath( crit );
 			Object old = associationPathCriteriaMap.put( wholeAssociationPath, crit );
 			if ( old != null ) {
 				throw new QueryException( "duplicate association path: " + wholeAssociationPath );
 			}
 			JoinType joinType = crit.getJoinType();
 			old = associationPathJoinTypesMap.put( wholeAssociationPath, joinType );
 			if ( old != null ) {
 				// TODO : not so sure this is needed...
 				throw new QueryException( "duplicate association path: " + wholeAssociationPath );
 			}
 			if ( crit.getWithClause() != null )
 			{
 				this.withClauseMap.put(wholeAssociationPath, crit.getWithClause());
 			}
 		}
 	}
 
 	private String getWholeAssociationPath(CriteriaImpl.Subcriteria subcriteria) {
 		String path = subcriteria.getPath();
 
 		// some messy, complex stuff here, since createCriteria() can take an
 		// aliased path, or a path rooted at the creating criteria instance
 		Criteria parent = null;
 		if ( path.indexOf( '.' ) > 0 ) {
 			// if it is a compound path
 			String testAlias = StringHelper.root( path );
 			if ( !testAlias.equals( subcriteria.getAlias() ) ) {
 				// and the qualifier is not the alias of this criteria
 				//      -> check to see if we belong to some criteria other
 				//          than the one that created us
 				parent = aliasCriteriaMap.get( testAlias );
 			}
 		}
 		if ( parent == null ) {
 			// otherwise assume the parent is the the criteria that created us
 			parent = subcriteria.getParent();
 		}
 		else {
 			path = StringHelper.unroot( path );
 		}
 
 		if ( parent.equals( rootCriteria ) ) {
 			// if its the root criteria, we are done
 			return path;
 		}
 		else {
 			// otherwise, recurse
 			return getWholeAssociationPath( ( CriteriaImpl.Subcriteria ) parent ) + '.' + path;
 		}
 	}
 
 	private void createCriteriaEntityNameMap() {
 		// initialize the rootProvider first
 		CriteriaInfoProvider rootProvider = new EntityCriteriaInfoProvider(( Queryable ) sessionFactory.getEntityPersister( rootEntityName ) );
 		criteriaInfoMap.put( rootCriteria, rootProvider);
 		nameCriteriaInfoMap.put ( rootProvider.getName(), rootProvider );
 
 		for(final String key : associationPathCriteriaMap.keySet() ){
 			Criteria value = associationPathCriteriaMap.get( key );
 			CriteriaInfoProvider info = getPathInfo( key );
 			criteriaInfoMap.put( value, info );
 			nameCriteriaInfoMap.put( info.getName(), info );
 		}
 	}
 
 
 	private CriteriaInfoProvider getPathInfo(String path) {
 		StringTokenizer tokens = new StringTokenizer( path, "." );
 		String componentPath = "";
 
 		// start with the 'rootProvider'
 		CriteriaInfoProvider provider = nameCriteriaInfoMap.get( rootEntityName );
 
 		while ( tokens.hasMoreTokens() ) {
 			componentPath += tokens.nextToken();
 			Type type = provider.getType( componentPath );
 			if ( type.isAssociationType() ) {
 				// CollectionTypes are always also AssociationTypes - but there's not always an associated entity...
 				AssociationType atype = ( AssociationType ) type;
 				CollectionType ctype = type.isCollectionType() ? (CollectionType)type : null;
 				Type elementType = (ctype != null) ? ctype.getElementType( sessionFactory ) : null;
 				// is the association a collection of components or value-types? (i.e a colloction of valued types?)
 				if ( ctype != null  && elementType.isComponentType() ) {
 					provider = new ComponentCollectionCriteriaInfoProvider( helper.getCollectionPersister(ctype.getRole()) );
 				}
 				else if ( ctype != null && !elementType.isEntityType() ) {
 					provider = new ScalarCollectionCriteriaInfoProvider( helper, ctype.getRole() );
 				}
 				else {
 					provider = new EntityCriteriaInfoProvider(( Queryable ) sessionFactory.getEntityPersister(
 											  atype.getAssociatedEntityName( sessionFactory )
 											  ));
 				}
 				
 				componentPath = "";
 			}
 			else if ( type.isComponentType() ) {
 				if (!tokens.hasMoreTokens()) {
 					throw new QueryException("Criteria objects cannot be created directly on components.  Create a criteria on owning entity and use a dotted property to access component property: "+path);
 				} else {
 					componentPath += '.';
 				}
 			}
 			else {
 				throw new QueryException( "not an association: " + componentPath );
 			}
 		}
 		
 		return provider;
 	}
 
 	public int getSQLAliasCount() {
 		return criteriaSQLAliasMap.size();
 	}
 
 	private void createCriteriaSQLAliasMap() {
 		int i = 0;
 		for(final Criteria crit : criteriaInfoMap.keySet()){
 			CriteriaInfoProvider value = criteriaInfoMap.get( crit );
 			String alias = crit.getAlias();
 			if ( alias == null ) {
 				alias = value.getName(); // the entity name
 			}
 			criteriaSQLAliasMap.put( crit, StringHelper.generateAlias( alias, i++ ) );
 		}
 
 		criteriaSQLAliasMap.put( rootCriteria, rootSQLAlias );
 	}
 
 	public CriteriaImpl getRootCriteria() {
 		return rootCriteria;
 	}
 
 	public QueryParameters getQueryParameters() {
 		LockOptions lockOptions = new LockOptions();
 		RowSelection selection = new RowSelection();
 		selection.setFirstRow( rootCriteria.getFirstResult() );
 		selection.setMaxRows( rootCriteria.getMaxResults() );
 		selection.setTimeout( rootCriteria.getTimeout() );
 		selection.setFetchSize( rootCriteria.getFetchSize() );
 		final Map<String, LockMode> lockModeMap = rootCriteria.getLockModes();
 		for ( final String key : lockModeMap.keySet() ) {
 			final Criteria subcriteria = getAliasedCriteria( key );
 			lockOptions.setAliasSpecificLockMode( getSQLAlias( subcriteria ), lockModeMap.get( key ) );
 		}
 		final List<Object> values = new ArrayList<Object>();
 		final List<Type> types = new ArrayList<Type>();
 		final Iterator<CriteriaImpl.Subcriteria> subcriteriaIterator = rootCriteria.iterateSubcriteria();
 		while ( subcriteriaIterator.hasNext() ) {
 			CriteriaImpl.Subcriteria subcriteria = subcriteriaIterator.next();
 			LockMode lm = subcriteria.getLockMode();
 			if ( lm != null ) {
 				lockOptions.setAliasSpecificLockMode( getSQLAlias( subcriteria ), lm );
 			}
 			if ( subcriteria.getWithClause() != null )
 			{
 				TypedValue[] tv = subcriteria.getWithClause().getTypedValues( subcriteria, this );
 				for ( TypedValue aTv : tv ) {
 					values.add( aTv.getValue() );
 					types.add( aTv.getType() );
 				}
 			}
 		}
 
 		// Type and value gathering for the WHERE clause needs to come AFTER lock mode gathering,
 		// because the lock mode gathering loop now contains join clauses which can contain
 		// parameter bindings (as in the HQL WITH clause).
 		Iterator<CriteriaImpl.CriterionEntry> iter = rootCriteria.iterateExpressionEntries();
 		while ( iter.hasNext() ) {
 			CriteriaImpl.CriterionEntry ce = iter.next();
 			TypedValue[] tv = ce.getCriterion().getTypedValues( ce.getCriteria(), this );
 			for ( TypedValue aTv : tv ) {
 				values.add( aTv.getValue() );
 				types.add( aTv.getType() );
 			}
 		}
 
 		Object[] valueArray = values.toArray();
 		Type[] typeArray = ArrayHelper.toTypeArray( types );
 		return new QueryParameters(
 				typeArray,
 		        valueArray,
 		        lockOptions,
 		        selection,
 		        rootCriteria.isReadOnlyInitialized(),
 		        ( rootCriteria.isReadOnlyInitialized() && rootCriteria.isReadOnly() ),
 		        rootCriteria.getCacheable(),
 		        rootCriteria.getCacheRegion(),
 		        rootCriteria.getComment(),
+		        rootCriteria.getQueryHints(),
 		        rootCriteria.isLookupByNaturalKey(),
 		        rootCriteria.getResultTransformer()
 		);
 	}
 
 	public boolean hasProjection() {
 		return rootCriteria.getProjection() != null;
 	}
 
 	public String getGroupBy() {
 		if ( rootCriteria.getProjection().isGrouped() ) {
 			return rootCriteria.getProjection()
 					.toGroupSqlString( rootCriteria.getProjectionCriteria(), this );
 		}
 		else {
 			return "";
 		}
 	}
 
 	public String getSelect() {
 		return rootCriteria.getProjection().toSqlString(
 				rootCriteria.getProjectionCriteria(),
 		        0,
 		        this
 		);
 	}
 
 	/* package-protected */
 	Type getResultType(Criteria criteria) {
 		return getFactory().getTypeResolver().getTypeFactory().manyToOne( getEntityName( criteria ) );
 	}
 
 	public Type[] getProjectedTypes() {
 		return rootCriteria.getProjection().getTypes( rootCriteria, this );
 	}
 
 	public String[] getProjectedColumnAliases() {
 		return rootCriteria.getProjection() instanceof EnhancedProjection ?
 				( ( EnhancedProjection ) rootCriteria.getProjection() ).getColumnAliases( 0, rootCriteria, this ) :
 				rootCriteria.getProjection().getColumnAliases( 0 );
 	}
 
 	public String[] getProjectedAliases() {
 		return rootCriteria.getProjection().getAliases();
 	}
 
 	public String getWhereCondition() {
 		StringBuilder condition = new StringBuilder( 30 );
 		Iterator<CriteriaImpl.CriterionEntry> criterionIterator = rootCriteria.iterateExpressionEntries();
 		while ( criterionIterator.hasNext() ) {
 			CriteriaImpl.CriterionEntry entry = criterionIterator.next();
 			String sqlString = entry.getCriterion().toSqlString( entry.getCriteria(), this );
 			condition.append( sqlString );
 			if ( criterionIterator.hasNext() ) {
 				condition.append( " and " );
 			}
 		}
 		return condition.toString();
 	}
 
 	public String getOrderBy() {
 		StringBuilder orderBy = new StringBuilder( 30 );
 		Iterator<CriteriaImpl.OrderEntry> criterionIterator = rootCriteria.iterateOrderings();
 		while ( criterionIterator.hasNext() ) {
 			CriteriaImpl.OrderEntry oe = criterionIterator.next();
 			orderBy.append( oe.getOrder().toSqlString( oe.getCriteria(), this ) );
 			if ( criterionIterator.hasNext() ) {
 				orderBy.append( ", " );
 			}
 		}
 		return orderBy.toString();
 	}
 	@Override
 	public SessionFactoryImplementor getFactory() {
 		return sessionFactory;
 	}
 	@Override
 	public String getSQLAlias(Criteria criteria) {
 		return criteriaSQLAliasMap.get( criteria );
 	}
 	@Override
 	public String getEntityName(Criteria criteria) {
 		final CriteriaInfoProvider infoProvider = criteriaInfoMap.get( criteria );
 		return infoProvider != null ? infoProvider.getName() : null;
 	}
 	@Override
 	public String getColumn(Criteria criteria, String propertyName) {
 		String[] cols = getColumns( propertyName, criteria );
 		if ( cols.length != 1 ) {
 			throw new QueryException( "property does not map to a single column: " + propertyName );
 		}
 		return cols[0];
 	}
 
 	/**
 	 * Get the names of the columns constrained
 	 * by this criterion.
 	 */
 	@Override
 	public String[] getColumnsUsingProjection(
 			Criteria subcriteria,
 	        String propertyName) throws HibernateException {
 
 		//first look for a reference to a projection alias
 		final Projection projection = rootCriteria.getProjection();
 		String[] projectionColumns = null;
 		if ( projection != null ) {
 			projectionColumns = ( projection instanceof EnhancedProjection ?
 					( ( EnhancedProjection ) projection ).getColumnAliases( propertyName, 0, rootCriteria, this ) :
 					projection.getColumnAliases( propertyName, 0 )
 			);
 		}
 		if ( projectionColumns == null ) {
 			//it does not refer to an alias of a projection,
 			//look for a property
 			try {
 				return getColumns( propertyName, subcriteria );
 			}
 			catch ( HibernateException he ) {
 				//not found in inner query , try the outer query
 				if ( outerQueryTranslator != null ) {
 					return outerQueryTranslator.getColumnsUsingProjection( subcriteria, propertyName );
 				}
 				else {
 					throw he;
 				}
 			}
 		}
 		else {
 			//it refers to an alias of a projection
 			return projectionColumns;
 		}
 	}
 	@Override
 	public String[] getIdentifierColumns(Criteria criteria) {
 		String[] idcols =
 				( ( Loadable ) getPropertyMapping( getEntityName( criteria ) ) ).getIdentifierColumnNames();
 		return StringHelper.qualify( getSQLAlias( criteria ), idcols );
 	}
 	@Override
 	public Type getIdentifierType(Criteria criteria) {
 		return ( ( Loadable ) getPropertyMapping( getEntityName( criteria ) ) ).getIdentifierType();
 	}
 	@Override
 	public TypedValue getTypedIdentifierValue(Criteria criteria, Object value) {
 		final Loadable loadable = ( Loadable ) getPropertyMapping( getEntityName( criteria ) );
 		return new TypedValue( loadable.getIdentifierType(), value );
 	}
 	@Override
 	public String[] getColumns(
 			String propertyName,
 	        Criteria subcriteria) throws HibernateException {
 		return getPropertyMapping( getEntityName( subcriteria, propertyName ) )
 				.toColumns(
 						getSQLAlias( subcriteria, propertyName ),
 				        getPropertyName( propertyName )
 				);
 	}
 
 	/**
 	 * Get the names of the columns mapped by a property path; if the
 	 * property path is not found in subcriteria, try the "outer" query.
 	 * Projection aliases are ignored.
 	 */
 	@Override
 	public String[] findColumns(String propertyName, Criteria subcriteria )
 	throws HibernateException {
 		try {
 			return getColumns( propertyName, subcriteria );
 		}
 		catch ( HibernateException he ) {
 			//not found in inner query, try the outer query
 			if ( outerQueryTranslator != null ) {
 				return outerQueryTranslator.findColumns( propertyName, subcriteria );
 			}
 			else {
 				throw he;
 			}
 		}
 	}
 	@Override
 	public Type getTypeUsingProjection(Criteria subcriteria, String propertyName)
 			throws HibernateException {
 
 		//first look for a reference to a projection alias
 		final Projection projection = rootCriteria.getProjection();
 		Type[] projectionTypes = projection == null ?
 		                         null :
 		                         projection.getTypes( propertyName, subcriteria, this );
 
 		if ( projectionTypes == null ) {
 			try {
 				//it does not refer to an alias of a projection,
 				//look for a property
 				return getType( subcriteria, propertyName );
 			}
 			catch ( HibernateException he ) {
 				//not found in inner query , try the outer query
 				if ( outerQueryTranslator != null ) {
 					return outerQueryTranslator.getType( subcriteria, propertyName );
 				}
 				else {
 					throw he;
 				}
 			}
 		}
 		else {
 			if ( projectionTypes.length != 1 ) {
 				//should never happen, i think
 				throw new QueryException( "not a single-length projection: " + propertyName );
 			}
 			return projectionTypes[0];
 		}
 	}
 	@Override
 	public Type getType(Criteria subcriteria, String propertyName)
 			throws HibernateException {
 		return getPropertyMapping( getEntityName( subcriteria, propertyName ) )
 				.toType( getPropertyName( propertyName ) );
 	}
 
 	/**
 	 * Get the a typed value for the given property value.
 	 */
 	@Override
 	public TypedValue getTypedValue(Criteria subcriteria, String propertyName, Object value)
 			throws HibernateException {
 		// Detect discriminator values...
 		if ( value instanceof Class ) {
 			Class entityClass = ( Class ) value;
 			Queryable q = SessionFactoryHelper.findQueryableUsingImports( sessionFactory, entityClass.getName() );
 			if ( q != null ) {
 				Type type = q.getDiscriminatorType();
 				String stringValue = q.getDiscriminatorSQLValue();
 				if (stringValue != null && stringValue.length() > 2
 						&& stringValue.startsWith("'")
 						&& stringValue.endsWith("'")) { // remove the single
 														// quotes
 					stringValue = stringValue.substring(1,
 							stringValue.length() - 1);
 				}
 				
 				// Convert the string value into the proper type.
 				if ( type instanceof StringRepresentableType ) {
 					StringRepresentableType nullableType = (StringRepresentableType) type;
 					value = nullableType.fromStringValue( stringValue );
 				}
 				else {
 					throw new QueryException( "Unsupported discriminator type " + type );
 				}
 				return new TypedValue( type, value );
 			}
 		}
 		// Otherwise, this is an ordinary value.
 		return new TypedValue( getTypeUsingProjection( subcriteria, propertyName ), value );
 	}
 
 	private PropertyMapping getPropertyMapping(String entityName)
 			throws MappingException {
 		CriteriaInfoProvider info = nameCriteriaInfoMap.get(entityName);
 		if (info==null) {
 			throw new HibernateException( "Unknown entity: " + entityName );
 		}
 		return info.getPropertyMapping();
 	}
 
 	//TODO: use these in methods above
 	@Override
 	public String getEntityName(Criteria subcriteria, String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			String root = StringHelper.root( propertyName );
 			Criteria crit = getAliasedCriteria( root );
 			if ( crit != null ) {
 				return getEntityName( crit );
 			}
 		}
 		return getEntityName( subcriteria );
 	}
 	@Override
 	public String getSQLAlias(Criteria criteria, String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			String root = StringHelper.root( propertyName );
 			Criteria subcriteria = getAliasedCriteria( root );
 			if ( subcriteria != null ) {
 				return getSQLAlias( subcriteria );
 			}
 		}
 		return getSQLAlias( criteria );
 	}
 	@Override
 	public String getPropertyName(String propertyName) {
 		if ( propertyName.indexOf( '.' ) > 0 ) {
 			String root = StringHelper.root( propertyName );
 			Criteria crit = getAliasedCriteria( root );
 			if ( crit != null ) {
 				return propertyName.substring( root.length() + 1 );
 			}
 		}
 		return propertyName;
 	}
 
 	public String getWithClause(String path)
 	{
 		final Criterion crit = withClauseMap.get(path);
 		return crit == null ? null : crit.toSqlString(getCriteria(path), this);
 	}
 
 	public boolean hasRestriction(String path)
 	{
 		final CriteriaImpl.Subcriteria crit = ( CriteriaImpl.Subcriteria ) getCriteria( path );
 		return crit != null && crit.hasRestriction();
 	}
 
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/queryhint/QueryHintTest.java b/hibernate-core/src/test/java/org/hibernate/test/queryhint/QueryHintTest.java
new file mode 100644
index 0000000000..4df7e942d1
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/queryhint/QueryHintTest.java
@@ -0,0 +1,177 @@
+/* 
+ * Hibernate, Relational Persistence for Idiomatic Java
+ * 
+ * JBoss, Home of Professional Open Source
+ * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
+ * as indicated by the @authors tag. All rights reserved.
+ * See the copyright.txt in the distribution for a
+ * full listing of individual contributors.
+ *
+ * This copyrighted material is made available to anyone wishing to use,
+ * modify, copy, or redistribute it subject to the terms and conditions
+ * of the GNU Lesser General Public License, v. 2.1.
+ * This program is distributed in the hope that it will be useful, but WITHOUT A
+ * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
+ * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
+ * You should have received a copy of the GNU Lesser General Public License,
+ * v.2.1 along with this distribution; if not, write to the Free Software
+ * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
+ * MA  02110-1301, USA.
+ */
+package org.hibernate.test.queryhint;
+
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertTrue;
+
+import java.util.List;
+
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.ManyToOne;
+
+import org.hibernate.Criteria;
+import org.hibernate.Query;
+import org.hibernate.Session;
+import org.hibernate.cfg.AvailableSettings;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.criterion.Restrictions;
+import org.hibernate.dialect.Oracle8iDialect;
+import org.hibernate.testing.RequiresDialect;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.junit.Test;
+
+/**
+ * @author Brett Meyer
+ */
+@RequiresDialect( Oracle8iDialect.class )
+public class QueryHintTest extends BaseCoreFunctionalTestCase {
+	
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class<?>[] { Employee.class, Department.class };
+	}
+	
+	@Override
+	protected void configure(Configuration configuration) {
+		configuration.setProperty( AvailableSettings.DIALECT, QueryHintTestDialect.class.getName() );
+		configuration.setProperty( AvailableSettings.USE_SQL_COMMENTS, "true" );
+	}
+	
+	@Test
+	public void testQueryHint() {
+		Department department = new Department();
+		department.name = "Sales";
+		Employee employee1 = new Employee();
+		employee1.department = department;
+		Employee employee2 = new Employee();
+		employee2.department = department;
+		
+		Session s = openSession();
+		s.getTransaction().begin();
+		s.persist( department );		s.persist( employee1 );
+		s.persist( employee2 );
+		s.getTransaction().commit();
+		s.clear();
+
+		// test Query w/ a simple Oracle optimizer hint
+		s.getTransaction().begin();
+		Query query = s.createQuery( "FROM QueryHintTest$Employee e WHERE e.department.name = :departmentName" )
+				.addQueryHint( "ALL_ROWS" )
+				.setParameter( "departmentName", "Sales" );
+		List results = query.list();
+		s.getTransaction().commit();
+		s.clear();
+		
+		assertEquals(results.size(), 2);
+		assertTrue(QueryHintTestDialect.getProcessedSql().contains( "select /*+ ALL_ROWS */"));
+		
+		QueryHintTestDialect.resetProcessedSql();
+		
+		// test multiple hints
+		s.getTransaction().begin();
+		query = s.createQuery( "FROM QueryHintTest$Employee e WHERE e.department.name = :departmentName" )
+				.addQueryHint( "ALL_ROWS" )
+				.addQueryHint( "USE_CONCAT" )
+				.setParameter( "departmentName", "Sales" );
+		results = query.list();
+		s.getTransaction().commit();
+		s.clear();
+		
+		assertEquals(results.size(), 2);
+		assertTrue(QueryHintTestDialect.getProcessedSql().contains( "select /*+ ALL_ROWS, USE_CONCAT */"));
+		
+		QueryHintTestDialect.resetProcessedSql();
+		
+		// ensure the insertion logic can handle a comment appended to the front
+		s.getTransaction().begin();
+		query = s.createQuery( "FROM QueryHintTest$Employee e WHERE e.department.name = :departmentName" )
+				.setComment( "this is a test" )
+				.addQueryHint( "ALL_ROWS" )
+				.setParameter( "departmentName", "Sales" );
+		results = query.list();
+		s.getTransaction().commit();
+		s.clear();
+		
+		assertEquals(results.size(), 2);
+		assertTrue(QueryHintTestDialect.getProcessedSql().contains( "select /*+ ALL_ROWS */"));
+		
+		QueryHintTestDialect.resetProcessedSql();
+		
+		// test Criteria
+		s.getTransaction().begin();
+		Criteria criteria = s.createCriteria( Employee.class )
+				.addQueryHint( "ALL_ROWS" )
+				.createCriteria( "department" ).add( Restrictions.eq( "name", "Sales" ) );
+		results = criteria.list();
+		s.getTransaction().commit();
+		s.close();
+		
+		assertEquals(results.size(), 2);
+		assertTrue(QueryHintTestDialect.getProcessedSql().contains( "select /*+ ALL_ROWS */"));
+	}
+	
+	/**
+	 * Since the query hint is added to the SQL during Loader's executeQueryStatement -> preprocessSQL, rather than
+	 * early on during the QueryTranslator or QueryLoader initialization, there's not an easy way to check the full SQL
+	 * after completely processing it.  Instead, use this ridiculous hack to ensure Loader actually calls Dialect.
+	 * 
+	 * TODO: This is terrible.  Better ideas?
+	 */
+	public static class QueryHintTestDialect extends Oracle8iDialect {
+		private static String processedSql;
+		
+		@Override
+		public String getQueryHintString(String sql, List<String> hints) {
+			processedSql = super.getQueryHintString( sql, hints );
+			return processedSql;
+		}
+		
+		public static String getProcessedSql() {
+			return processedSql;
+		}
+		
+		public static void resetProcessedSql() {
+			processedSql = "";
+		}
+	}
+	
+	@Entity
+	public static class Employee {
+		@Id
+		@GeneratedValue
+		public long id;
+		
+		@ManyToOne
+		public Department department;
+	}
+	
+	@Entity
+	public static class Department {
+		@Id
+		@GeneratedValue
+		public long id;
+		
+		public String name;
+	}
+}
