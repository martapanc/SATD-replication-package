diff --git a/hibernate-core/src/main/java/org/hibernate/Criteria.java b/hibernate-core/src/main/java/org/hibernate/Criteria.java
index cefff41c1b..f8c57e93d4 100644
--- a/hibernate-core/src/main/java/org/hibernate/Criteria.java
+++ b/hibernate-core/src/main/java/org/hibernate/Criteria.java
@@ -1,584 +1,583 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 2008, 2013, Red Hat Inc. or third-party contributors as
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
-import java.util.List;
 
-import javax.persistence.QueryHint;
+import java.util.List;
 
 import org.hibernate.criterion.CriteriaSpecification;
 import org.hibernate.criterion.Criterion;
 import org.hibernate.criterion.Order;
 import org.hibernate.criterion.Projection;
 import org.hibernate.sql.JoinType;
 import org.hibernate.transform.ResultTransformer;
 
+
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
 	
 	  
 	/**
-	 * Add a DB query hint to the SQL.  These differ from JPA's {@link QueryHint}, which is specific to the JPA
-	 * implementation and ignores DB vendor-specific hints.  Instead, these are intended solely for the vendor-specific
-	 * hints, such as Oracle's optimizers.  Multiple query hints are supported; the Dialect will determine
-	 * concatenation and placement.
+	 * Add a DB query hint to the SQL.  These differ from JPA's {@link javax.persistence.QueryHint}, which is specific
+	 * to the JPA implementation and ignores DB vendor-specific hints.  Instead, these are intended solely for the
+	 * vendor-specific hints, such as Oracle's optimizers.  Multiple query hints are supported; the Dialect will
+	 * determine concatenation and placement.
 	 * 
 	 * @param hint The database specific query hint to add.
 	 * @return this (for method chaining)
 	 */
 	public Criteria addQueryHint(String hint);
 
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
index dc4b315d5a..f9f2a20e82 100644
--- a/hibernate-core/src/main/java/org/hibernate/Query.java
+++ b/hibernate-core/src/main/java/org/hibernate/Query.java
@@ -1,880 +1,878 @@
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
 
-import javax.persistence.QueryHint;
-
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
 	
 	/**
-	 * Add a DB query hint to the SQL.  These differ from JPA's {@link QueryHint}, which is specific to the JPA
-	 * implementation and ignores DB vendor-specific hints.  Instead, these are intended solely for the vendor-specific
-	 * hints, such as Oracle's optimizers.  Multiple query hints are supported; the Dialect will determine
-	 * concatenation and placement.
+	 * Add a DB query hint to the SQL.  These differ from JPA's {@link javax.persistence.QueryHint}, which is specific
+	 * to the JPA implementation and ignores DB vendor-specific hints.  Instead, these are intended solely for the
+	 * vendor-specific hints, such as Oracle's optimizers.  Multiple query hints are supported; the Dialect will
+	 * determine concatenation and placement.
 	 * 
 	 * @param hint The database specific query hint to add.
 	 */
 	public Query addQueryHint(String hint);
 
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
 	 * multiple results per row, the results are returned in an instance
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
 
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/BootstrapServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/BootstrapServiceRegistryImpl.java
index 47542569d0..4f884d969c 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/BootstrapServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/registry/internal/BootstrapServiceRegistryImpl.java
@@ -1,230 +1,230 @@
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
 package org.hibernate.boot.registry.internal;
 
 import java.util.LinkedHashSet;
 
+import org.jboss.logging.Logger;
+
+import org.hibernate.boot.registry.BootstrapServiceRegistry;
+import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
+import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
+import org.hibernate.boot.registry.selector.internal.StrategySelectorImpl;
+import org.hibernate.boot.registry.selector.spi.StrategySelector;
 import org.hibernate.integrator.internal.IntegratorServiceImpl;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.boot.registry.BootstrapServiceRegistry;
 import org.hibernate.service.Service;
 import org.hibernate.service.ServiceRegistry;
-import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl;
-import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
-import org.hibernate.boot.registry.selector.internal.StrategySelectorImpl;
-import org.hibernate.boot.registry.selector.spi.StrategySelector;
-import org.hibernate.service.internal.AbstractServiceRegistryImpl;
 import org.hibernate.service.spi.ServiceBinding;
 import org.hibernate.service.spi.ServiceException;
 import org.hibernate.service.spi.ServiceInitiator;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.Stoppable;
-import org.jboss.logging.Logger;
 
 /**
  * {@link ServiceRegistry} implementation containing specialized "bootstrap" services, specifically:<ul>
  *     <li>{@link ClassLoaderService}</li>
  *     <li>{@link IntegratorService}</li>
  *     <li>{@link StrategySelector}</li>
  * </ul>
  *
  * IMPL NOTE : Currently implements the deprecated {@link org.hibernate.service.BootstrapServiceRegistry} contract
  * so that the registry returned from the builder works on the deprecated sense.  Once
  * {@link org.hibernate.service.BootstrapServiceRegistry} goes away, this should be updated to instead implement
  * {@link org.hibernate.boot.registry.BootstrapServiceRegistry}.
  *
  * @author Steve Ebersole
  */
 public class BootstrapServiceRegistryImpl
 		implements ServiceRegistryImplementor, BootstrapServiceRegistry, ServiceBinding.ServiceLifecycleOwner {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			BootstrapServiceRegistryImpl.class.getName()
 	);
 	
 	private static final LinkedHashSet<Integrator> NO_INTEGRATORS = new LinkedHashSet<Integrator>();
 
 	private final ServiceBinding<ClassLoaderService> classLoaderServiceBinding;
 	private final ServiceBinding<StrategySelector> strategySelectorBinding;
 	private final ServiceBinding<IntegratorService> integratorServiceBinding;
 
 	/**
 	 * Constructs a BootstrapServiceRegistryImpl.
 	 *
 	 * Do not use directly generally speaking.  Use {@link org.hibernate.boot.registry.BootstrapServiceRegistryBuilder}
 	 * instead.
 	 *
 	 * @see org.hibernate.boot.registry.BootstrapServiceRegistryBuilder
 	 */
 	public BootstrapServiceRegistryImpl() {
 		this( new ClassLoaderServiceImpl(), NO_INTEGRATORS );
 	}
 
 	/**
 	 * Constructs a BootstrapServiceRegistryImpl.
 	 *
 	 * Do not use directly generally speaking.  Use {@link org.hibernate.boot.registry.BootstrapServiceRegistryBuilder}
 	 * instead.
 	 *
 	 * @param classLoaderService The ClassLoaderService to use
 	 * @param providedIntegrators The group of explicitly provided integrators
 	 *
 	 * @see org.hibernate.boot.registry.BootstrapServiceRegistryBuilder
 	 */
 	public BootstrapServiceRegistryImpl(
 			ClassLoaderService classLoaderService,
 			LinkedHashSet<Integrator> providedIntegrators) {
 		this.classLoaderServiceBinding = new ServiceBinding<ClassLoaderService>(
 				this,
 				ClassLoaderService.class,
 				classLoaderService
 		);
 
 		final StrategySelectorImpl strategySelector = new StrategySelectorImpl( classLoaderService );
 		this.strategySelectorBinding = new ServiceBinding<StrategySelector>(
 				this,
 				StrategySelector.class,
 				strategySelector
 		);
 
 		this.integratorServiceBinding = new ServiceBinding<IntegratorService>(
 				this,
 				IntegratorService.class,
 				new IntegratorServiceImpl( providedIntegrators, classLoaderService )
 		);
 	}
 
 
 	/**
 	 * Constructs a BootstrapServiceRegistryImpl.
 	 *
 	 * Do not use directly generally speaking.  Use {@link org.hibernate.boot.registry.BootstrapServiceRegistryBuilder}
 	 * instead.
 	 *
 	 * @param classLoaderService The ClassLoaderService to use
 	 * @param strategySelector The StrategySelector to use
 	 * @param integratorService The IntegratorService to use
 	 *
 	 * @see org.hibernate.boot.registry.BootstrapServiceRegistryBuilder
 	 */
 	public BootstrapServiceRegistryImpl(
 			ClassLoaderService classLoaderService,
 			StrategySelector strategySelector,
 			IntegratorService integratorService) {
 		this.classLoaderServiceBinding = new ServiceBinding<ClassLoaderService>(
 				this,
 				ClassLoaderService.class,
 				classLoaderService
 		);
 
 		this.strategySelectorBinding = new ServiceBinding<StrategySelector>(
 				this,
 				StrategySelector.class,
 				strategySelector
 		);
 
 		this.integratorServiceBinding = new ServiceBinding<IntegratorService>(
 				this,
 				IntegratorService.class,
 				integratorService
 		);
 	}
 
 
 
 	@Override
 	public <R extends Service> R getService(Class<R> serviceRole) {
 		final ServiceBinding<R> binding = locateServiceBinding( serviceRole );
 		return binding == null ? null : binding.getService();
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <R extends Service> ServiceBinding<R> locateServiceBinding(Class<R> serviceRole) {
 		if ( ClassLoaderService.class.equals( serviceRole ) ) {
 			return (ServiceBinding<R>) classLoaderServiceBinding;
 		}
 		else if ( StrategySelector.class.equals( serviceRole) ) {
 			return (ServiceBinding<R>) strategySelectorBinding;
 		}
 		else if ( IntegratorService.class.equals( serviceRole ) ) {
 			return (ServiceBinding<R>) integratorServiceBinding;
 		}
 
 		return null;
 	}
 
 	@Override
 	public void destroy() {
 		destroy( classLoaderServiceBinding );
 		destroy( strategySelectorBinding );
 		destroy( integratorServiceBinding );
 	}
 	
 	private void destroy(ServiceBinding serviceBinding) {
 		serviceBinding.getLifecycleOwner().stopService( serviceBinding );
 	}
 
 	@Override
 	public ServiceRegistry getParentServiceRegistry() {
 		return null;
 	}
 
 	@Override
 	public <R extends Service> R initiateService(ServiceInitiator<R> serviceInitiator) {
 		throw new ServiceException( "Boot-strap registry should only contain provided services" );
 	}
 
 	@Override
 	public <R extends Service> void configureService(ServiceBinding<R> binding) {
 		throw new ServiceException( "Boot-strap registry should only contain provided services" );
 	}
 
 	@Override
 	public <R extends Service> void injectDependencies(ServiceBinding<R> binding) {
 		throw new ServiceException( "Boot-strap registry should only contain provided services" );
 	}
 
 	@Override
 	public <R extends Service> void startService(ServiceBinding<R> binding) {
 		throw new ServiceException( "Boot-strap registry should only contain provided services" );
 	}
 
 	@Override
 	public <R extends Service> void stopService(ServiceBinding<R> binding) {
 		final Service service = binding.getService();
 		if ( Stoppable.class.isInstance( service ) ) {
 			try {
 				( (Stoppable) service ).stop();
 			}
 			catch ( Exception e ) {
 				LOG.unableToStopService( service.getClass(), e.toString() );
 			}
 		}
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/internal/CollectionCacheInvalidator.java b/hibernate-core/src/main/java/org/hibernate/cache/internal/CollectionCacheInvalidator.java
index b4c0635152..05836ac30d 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/internal/CollectionCacheInvalidator.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/internal/CollectionCacheInvalidator.java
@@ -1,169 +1,167 @@
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
-
 package org.hibernate.cache.internal;
 
 import java.io.Serializable;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cache.spi.CacheKey;
-import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.service.spi.EventListenerRegistry;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.EventType;
 import org.hibernate.event.spi.PostDeleteEvent;
 import org.hibernate.event.spi.PostDeleteEventListener;
 import org.hibernate.event.spi.PostInsertEvent;
 import org.hibernate.event.spi.PostInsertEventListener;
 import org.hibernate.event.spi.PostUpdateEvent;
 import org.hibernate.event.spi.PostUpdateEventListener;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 /**
  * Allows the collection cache to be automatically evicted if an element is inserted/removed/updated *without* properly
  * managing both sides of the association (ie, the ManyToOne collection is changed w/o properly managing the OneToMany).
  * 
- * For this functionality to be used, {@link AvailableSettings#AUTO_EVICT_COLLECTION_CACHE} must be enabled.  For
- * performance reasons, it's disabled by default.
+ * For this functionality to be used, {@link org.hibernate.cfg.AvailableSettings#AUTO_EVICT_COLLECTION_CACHE} must be
+ * enabled.  For performance reasons, it's disabled by default.
  * 
  * @author Andreas Berger
  */
 public class CollectionCacheInvalidator implements Integrator, PostInsertEventListener, PostDeleteEventListener,
 		PostUpdateEventListener {
 	private static final Logger LOG = Logger.getLogger( CollectionCacheInvalidator.class.getName() );
 
 	@Override
 	public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactory,
 			SessionFactoryServiceRegistry serviceRegistry) {
 		integrate( serviceRegistry, sessionFactory );
 	}
 
 	@Override
 	public void integrate(MetadataImplementor metadata, SessionFactoryImplementor sessionFactory,
 			SessionFactoryServiceRegistry serviceRegistry) {
 		integrate( serviceRegistry, sessionFactory );
 	}
 
 	@Override
 	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
 	}
 
 	@Override
 	public void onPostInsert(PostInsertEvent event) {
 		evictCache( event.getEntity(), event.getPersister(), event.getSession(), null );
 	}
 
 	@Override
 	public boolean requiresPostCommitHanding(EntityPersister persister) {
 		return true;
 	}
 
 	@Override
 	public void onPostDelete(PostDeleteEvent event) {
 		evictCache( event.getEntity(), event.getPersister(), event.getSession(), null );
 	}
 
 	@Override
 	public void onPostUpdate(PostUpdateEvent event) {
 		evictCache( event.getEntity(), event.getPersister(), event.getSession(), event.getOldState() );
 	}
 
 	private void integrate(SessionFactoryServiceRegistry serviceRegistry, SessionFactoryImplementor sessionFactory) {
 		if ( !sessionFactory.getSettings().isAutoEvictCollectionCache() ) {
 			// feature is disabled
 			return;
 		}
 		if ( !sessionFactory.getSettings().isSecondLevelCacheEnabled() ) {
 			// Nothing to do, if caching is disabled
 			return;
 		}
 		EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
 		eventListenerRegistry.appendListeners( EventType.POST_INSERT, this );
 		eventListenerRegistry.appendListeners( EventType.POST_DELETE, this );
 		eventListenerRegistry.appendListeners( EventType.POST_UPDATE, this );
 	}
 
 	private void evictCache(Object entity, EntityPersister persister, EventSource session, Object[] oldState) {
 		try {
 			SessionFactoryImplementor factory = persister.getFactory();
 
 			Set<String> collectionRoles = factory.getCollectionRolesByEntityParticipant( persister.getEntityName() );
 			if ( collectionRoles == null || collectionRoles.isEmpty() ) {
 				return;
 			}
 			for ( String role : collectionRoles ) {
 				CollectionPersister collectionPersister = factory.getCollectionPersister( role );
 				if ( !collectionPersister.hasCache() ) {
 					// ignore collection if no caching is used
 					continue;
 				}
 				// this is the property this OneToMany relation is mapped by
 				String mappedBy = collectionPersister.getMappedByProperty();
 				if ( mappedBy != null ) {
 					int i = persister.getEntityMetamodel().getPropertyIndex( mappedBy );
 					Serializable oldId = null;
 					if ( oldState != null ) {
 						// in case of updating an entity we perhaps have to decache 2 entity collections, this is the
 						// old one
 						oldId = session.getIdentifier( oldState[i] );
 					}
 					Object ref = persister.getPropertyValue( entity, i );
 					Serializable id = null;
 					if ( ref != null ) {
 						id = session.getIdentifier( ref );
 					}
 					// only evict if the related entity has changed
 					if ( id != null && !id.equals( oldId ) ) {
 						evict( id, collectionPersister, session );
 						if ( oldId != null ) {
 							evict( oldId, collectionPersister, session );
 						}
 					}
 				}
 				else {
 					LOG.debug( "Evict CollectionRegion " + role );
 					collectionPersister.getCacheAccessStrategy().evictAll();
 				}
 			}
 		}
 		catch ( Exception e ) {
 			// don't let decaching influence other logic
 			LOG.error( "", e );
 		}
 	}
 
 	private void evict(Serializable id, CollectionPersister collectionPersister, EventSource session) {
 		LOG.debug( "Evict CollectionRegion " + collectionPersister.getRole() + " for id " + id );
 		CacheKey key = session.generateCacheKey( id, collectionPersister.getKeyType(), collectionPersister.getRole() );
 		collectionPersister.getCacheAccessStrategy().evict( key );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/ResultSetMappingDefinition.java b/hibernate-core/src/main/java/org/hibernate/engine/ResultSetMappingDefinition.java
index ed97293acd..1dc49c9346 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/ResultSetMappingDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/ResultSetMappingDefinition.java
@@ -1,100 +1,98 @@
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
 import java.util.ArrayList;
 import java.util.List;
 
-import org.jboss.logging.Logger;
-
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
 
 /**
  * Keep a description of the resultset mapping
  *
  * @author Emmanuel Bernard
  */
 public class ResultSetMappingDefinition implements Serializable {
 	private final String name;
 	private final List<NativeSQLQueryReturn> queryReturns = new ArrayList<NativeSQLQueryReturn>();
 
 	/**
 	 * Constructs a ResultSetMappingDefinition
 	 *
 	 * @param name The mapping name
 	 */
 	public ResultSetMappingDefinition(String name) {
 		this.name = name;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	/**
 	 * Adds a return.
 	 *
 	 * @param queryReturn The return
 	 */
 	public void addQueryReturn(NativeSQLQueryReturn queryReturn) {
 		queryReturns.add( queryReturn );
 	}
 
 // We could also keep these if needed for binary compatibility with annotations, provided
 // it only uses the addXXX() methods...
 //	public void addEntityQueryReturn(NativeSQLQueryNonScalarReturn entityQueryReturn) {
 //		entityQueryReturns.add(entityQueryReturn);
 //	}
 //
 //	public void addScalarQueryReturn(NativeSQLQueryScalarReturn scalarQueryReturn) {
 //		scalarQueryReturns.add(scalarQueryReturn);
 //	}
 
 	public NativeSQLQueryReturn[] getQueryReturns() {
 		return queryReturns.toArray( new NativeSQLQueryReturn[queryReturns.size()] );
 	}
 
 	public String traceLoggableFormat() {
 		final StringBuilder buffer = new StringBuilder()
 				.append( "ResultSetMappingDefinition[\n" )
 				.append( "    name=" ).append( name ).append( "\n" )
 				.append( "    returns=[\n" );
 
 		for ( NativeSQLQueryReturn rtn : queryReturns ) {
 			rtn.traceLog(
 					new NativeSQLQueryReturn.TraceLogger() {
 						@Override
 						public void writeLine(String traceLine) {
-							buffer.append( "        " + traceLine + "\n" );
+							buffer.append( "        " ).append( traceLine ).append( "\n" );
 						}
 					}
 			);
 		}
 
 		buffer.append( "    ]\n" ).append( "]" );
 
 		return buffer.toString();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/Cascade.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/Cascade.java
index 6a751dc009..b208c74692 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/Cascade.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Cascade.java
@@ -1,448 +1,443 @@
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
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Stack;
 
 import org.hibernate.HibernateException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
+import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.ForeignKeyDirection;
-import org.hibernate.type.ManyToOneType;
-import org.hibernate.type.OneToOneType;
 import org.hibernate.type.Type;
-import org.jboss.logging.Logger;
 
 /**
  * Delegate responsible for, in conjunction with the various
  * {@link org.hibernate.engine.spi.CascadingAction actions}, implementing cascade processing.
  *
  * @author Gavin King
  * @see org.hibernate.engine.spi.CascadingAction
  */
 public final class Cascade {
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
-			CoreMessageLogger.class,
-			Cascade.class.getName()
-	);
+	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( Cascade.class );
 
 	private final CascadingAction action;
 	private final EventSource eventSource;
 	private CascadePoint cascadePoint;
 
 	/**
 	 * Constructs a Cascade
 	 *
 	 * @param action The action we are cascading
 	 * @param cascadePoint The point in the action at which we are trying to cascade currently
 	 * @param eventSource The session
 	 */
 	public Cascade(final CascadingAction action, final CascadePoint cascadePoint, final EventSource eventSource) {
 		this.cascadePoint = cascadePoint;
 		this.eventSource = eventSource;
 		this.action = action;
 	}
 
 	/**
 	 * Cascade an action from the parent entity instance to all its children.
 	 *
 	 * @param persister The parent's entity persister
 	 * @param parent The parent reference.
 	 */
 	public void cascade(final EntityPersister persister, final Object parent) {
 		cascade( persister, parent, null );
 	}
 
 	/**
 	 * Cascade an action from the parent entity instance to all its children.  This
 	 * form is typically called from within cascade actions.
 	 *
 	 * @param persister The parent's entity persister
 	 * @param parent The parent reference.
 	 * @param anything Anything ;)   Typically some form of cascade-local cache
 	 * which is specific to each CascadingAction type
 	 */
 	public void cascade(final EntityPersister persister, final Object parent, final Object anything) {
 		if ( persister.hasCascades() || action.requiresNoCascadeChecking() ) {
 			// performance opt
 			final boolean traceEnabled = LOG.isTraceEnabled();
 			if ( traceEnabled ) {
 				LOG.tracev( "Processing cascade {0} for: {1}", action, persister.getEntityName() );
 			}
 
 			final Type[] types = persister.getPropertyTypes();
 			final CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();
 			final boolean hasUninitializedLazyProperties = persister.hasUninitializedLazyProperties( parent );
 			for ( int i=0; i<types.length; i++) {
 				final CascadeStyle style = cascadeStyles[i];
 				final String propertyName = persister.getPropertyNames()[i];
 				if ( hasUninitializedLazyProperties && persister.getPropertyLaziness()[i] && ! action.performOnLazyProperty() ) {
 					//do nothing to avoid a lazy property initialization
 					continue;
 				}
 
 				if ( style.doCascade( action ) ) {
 					cascadeProperty(
 							parent,
 							persister.getPropertyValue( parent, i ),
 							types[i],
 							style,
 							propertyName,
 							anything,
 							false
 					);
 				}
 				else if ( action.requiresNoCascadeChecking() ) {
 					action.noCascade(
 							eventSource,
 							persister.getPropertyValue( parent, i ),
 							parent,
 							persister,
 							i
 					);
 				}
 			}
 
 			if ( traceEnabled ) {
 				LOG.tracev( "Done processing cascade {0} for: {1}", action, persister.getEntityName() );
 			}
 		}
 	}
 
 	/**
 	 * Cascade an action to the child or children
 	 */
 	private void cascadeProperty(
 			final Object parent,
 			final Object child,
 			final Type type,
 			final CascadeStyle style,
 			final String propertyName,
 			final Object anything,
 			final boolean isCascadeDeleteEnabled) throws HibernateException {
 		
 		if ( child != null ) {
 			if ( type.isAssociationType() ) {
 				final AssociationType associationType = (AssociationType) type;
 				if ( cascadeAssociationNow( associationType ) ) {
 					cascadeAssociation(
 							parent,
 							child,
 							type,
 							style,
 							anything,
 							isCascadeDeleteEnabled
 						);
 				}
 			}
 			else if ( type.isComponentType() ) {
 				cascadeComponent( parent, child, (CompositeType) type, propertyName, anything );
 			}
 		}
 		
 		// potentially we need to handle orphan deletes for one-to-ones here...
 		if ( isLogicalOneToOne( type ) ) {
 			// We have a physical or logical one-to-one.  See if the attribute cascade settings and action-type require
 			// orphan checking
 			if ( style.hasOrphanDelete() && action.deleteOrphans() ) {
 				// value is orphaned if loaded state for this property shows not null
 				// because it is currently null.
 				final EntityEntry entry = eventSource.getPersistenceContext().getEntry( parent );
 				if ( entry != null && entry.getStatus() != Status.SAVING ) {
 					final Object loadedValue;
 					if ( componentPathStack.isEmpty() ) {
 						// association defined on entity
 						loadedValue = entry.getLoadedValue( propertyName );
 					}
 					else {
 						// association defined on component
 						// 		todo : this is currently unsupported because of the fact that
 						//		we do not know the loaded state of this value properly
 						//		and doing so would be very difficult given how components and
 						//		entities are loaded (and how 'loaded state' is put into the
 						//		EntityEntry).  Solutions here are to either:
 						//			1) properly account for components as a 2-phase load construct
 						//			2) just assume the association was just now orphaned and
 						// 				issue the orphan delete.  This would require a special
 						//				set of SQL statements though since we do not know the
 						//				orphaned value, something a delete with a subquery to
 						// 				match the owner.
 //							final EntityType entityType = (EntityType) type;
 //							final String getPropertyPath = composePropertyPath( entityType.getPropertyName() );
 						loadedValue = null;
 					}
 					
 					// orphaned if the association was nulled (child == null) or receives a new value while the
 					// entity is managed (without first nulling and manually flushing).
 					if ( child == null || ( loadedValue != null && child != loadedValue ) ) {
 						final EntityEntry valueEntry = eventSource
 								.getPersistenceContext().getEntry( 
 										loadedValue );
 						// Need to check this in case the context has
 						// already been flushed.  See HHH-7829.
 						if ( valueEntry != null ) {
 							final String entityName = valueEntry.getPersister().getEntityName();
 							if ( LOG.isTraceEnabled() ) {
 								final Serializable id = valueEntry.getPersister().getIdentifier( loadedValue, eventSource );
 								final String description = MessageHelper.infoString( entityName, id );
 								LOG.tracev( "Deleting orphaned entity instance: {0}", description );
 							}
 							
 							if (type.isAssociationType() && ((AssociationType)type).getForeignKeyDirection().equals(
 											ForeignKeyDirection.FOREIGN_KEY_TO_PARENT )) {
 								// If FK direction is to-parent, we must remove the orphan *before* the queued update(s)
 								// occur.  Otherwise, replacing the association on a managed entity, without manually
 								// nulling and flushing, causes FK constraint violations.
 								eventSource.removeOrphanBeforeUpdates( entityName, loadedValue );
 							}
 							else {
 								// Else, we must delete after the updates.
 								eventSource.delete( entityName, loadedValue, isCascadeDeleteEnabled, new HashSet() );
 							}
 						}
 					}
 				}
 			}
 		}
 	}
 
 	/**
 	 * Check if the association is a one to one in the logical model (either a shared-pk
 	 * or unique fk).
 	 *
 	 * @param type The type representing the attribute metadata
 	 *
 	 * @return True if the attribute represents a logical one to one association
 	 */
 	private boolean isLogicalOneToOne(Type type) {
 		return type.isEntityType() && ( (EntityType) type ).isLogicalOneToOne();
 	}
 
 	private Stack<String> componentPathStack = new Stack<String>();
 
 	private boolean cascadeAssociationNow(AssociationType associationType) {
 		return associationType.getForeignKeyDirection().cascadeNow( cascadePoint );
 	}
 
 	private void cascadeComponent(
 			final Object parent,
 			final Object child,
 			final CompositeType componentType,
 			final String componentPropertyName,
 			final Object anything) {
 		componentPathStack.push( componentPropertyName );
 		final Object[] children = componentType.getPropertyValues( child, eventSource );
 		final Type[] types = componentType.getSubtypes();
 		for ( int i=0; i<types.length; i++ ) {
 			final CascadeStyle componentPropertyStyle = componentType.getCascadeStyle( i );
 			final String subPropertyName = componentType.getPropertyNames()[i];
 			if ( componentPropertyStyle.doCascade( action ) ) {
 				cascadeProperty(
 						parent,
 						children[i],
 						types[i],
 						componentPropertyStyle,
 						subPropertyName,
 						anything,
 						false
 					);
 			}
 		}
 		componentPathStack.pop();
 	}
 
 	private void cascadeAssociation(
 			final Object parent,
 			final Object child,
 			final Type type,
 			final CascadeStyle style,
 			final Object anything,
 			final boolean isCascadeDeleteEnabled) {
 		if ( type.isEntityType() || type.isAnyType() ) {
 			cascadeToOne( parent, child, type, style, anything, isCascadeDeleteEnabled );
 		}
 		else if ( type.isCollectionType() ) {
 			cascadeCollection( parent, child, style, anything, (CollectionType) type );
 		}
 	}
 
 	/**
 	 * Cascade an action to a collection
 	 */
 	private void cascadeCollection(
 			final Object parent,
 			final Object child,
 			final CascadeStyle style,
 			final Object anything,
 			final CollectionType type) {
 		final CollectionPersister persister = eventSource.getFactory().getCollectionPersister( type.getRole() );
 		final Type elemType = persister.getElementType();
 
 		final CascadePoint originalCascadePoint = cascadePoint;
 		if ( cascadePoint == CascadePoint.AFTER_INSERT_BEFORE_DELETE) {
 			cascadePoint = CascadePoint.AFTER_INSERT_BEFORE_DELETE_VIA_COLLECTION;
 		}
 
 		//cascade to current collection elements
 		if ( elemType.isEntityType() || elemType.isAnyType() || elemType.isComponentType() ) {
 			cascadeCollectionElements(
 				parent,
 				child,
 				type,
 				style,
 				elemType,
 				anything,
 				persister.isCascadeDeleteEnabled()
 			);
 		}
 
 		cascadePoint = originalCascadePoint;
 	}
 
 	/**
 	 * Cascade an action to a to-one association or any type
 	 */
 	private void cascadeToOne(
 			final Object parent,
 			final Object child,
 			final Type type,
 			final CascadeStyle style,
 			final Object anything,
 			final boolean isCascadeDeleteEnabled) {
 		final String entityName = type.isEntityType()
 				? ( (EntityType) type ).getAssociatedEntityName()
 				: null;
 		if ( style.reallyDoCascade( action ) ) {
 			//not really necessary, but good for consistency...
 			eventSource.getPersistenceContext().addChildParent( child, parent );
 			try {
 				action.cascade( eventSource, child, entityName, anything, isCascadeDeleteEnabled );
 			}
 			finally {
 				eventSource.getPersistenceContext().removeChildParent( child );
 			}
 		}
 	}
 
 	/**
 	 * Cascade to the collection elements
 	 */
 	private void cascadeCollectionElements(
 			final Object parent,
 			final Object child,
 			final CollectionType collectionType,
 			final CascadeStyle style,
 			final Type elemType,
 			final Object anything,
 			final boolean isCascadeDeleteEnabled) throws HibernateException {
 		final boolean reallyDoCascade = style.reallyDoCascade( action ) && child != CollectionType.UNFETCHED_COLLECTION;
 
 		if ( reallyDoCascade ) {
 			final boolean traceEnabled = LOG.isTraceEnabled();
 			if ( traceEnabled ) {
 				LOG.tracev( "Cascade {0} for collection: {1}", action, collectionType.getRole() );
 			}
 
 			final Iterator itr = action.getCascadableChildrenIterator( eventSource, collectionType, child );
 			while ( itr.hasNext() ) {
 				cascadeProperty(
 						parent,
 						itr.next(),
 						elemType,
 						style,
 						null,
 						anything,
 						isCascadeDeleteEnabled
 				);
 			}
 
 			if ( traceEnabled ) {
 				LOG.tracev( "Done cascade {0} for collection: {1}", action, collectionType.getRole() );
 			}
 		}
 
 		final boolean deleteOrphans = style.hasOrphanDelete()
 				&& action.deleteOrphans()
 				&& elemType.isEntityType()
 				// a newly instantiated collection can't have orphans
 				&& child instanceof PersistentCollection;
 
 		if ( deleteOrphans ) {
 			final boolean traceEnabled = LOG.isTraceEnabled();
 			if ( traceEnabled ) {
 				LOG.tracev( "Deleting orphans for collection: {0}", collectionType.getRole() );
 			}
 			// we can do the cast since orphan-delete does not apply to:
 			// 1. newly instantiated collections
 			// 2. arrays (we can't track orphans for detached arrays)
 			final String entityName = collectionType.getAssociatedEntityName( eventSource.getFactory() );
 			deleteOrphans( entityName, (PersistentCollection) child );
 
 			if ( traceEnabled ) {
 				LOG.tracev( "Done deleting orphans for collection: {0}", collectionType.getRole() );
 			}
 		}
 	}
 
 	/**
 	 * Delete any entities that were removed from the collection
 	 */
 	private void deleteOrphans(String entityName, PersistentCollection pc) throws HibernateException {
 		//TODO: suck this logic into the collection!
 		final Collection orphans;
 		if ( pc.wasInitialized() ) {
 			final CollectionEntry ce = eventSource.getPersistenceContext().getCollectionEntry( pc );
 			orphans = ce==null
 					? java.util.Collections.EMPTY_LIST
 					: ce.getOrphans( entityName, pc );
 		}
 		else {
 			orphans = pc.getQueuedOrphans( entityName );
 		}
 
 		for ( Object orphan : orphans ) {
 			if ( orphan != null ) {
 				LOG.tracev( "Deleting orphaned entity instance: {0}", entityName );
 				eventSource.delete( entityName, orphan, false, new HashSet() );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/BasicSQLExceptionConverter.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/BasicSQLExceptionConverter.java
index 02f31fc993..52cf00b21b 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/BasicSQLExceptionConverter.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/dialect/spi/BasicSQLExceptionConverter.java
@@ -1,73 +1,72 @@
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
 package org.hibernate.engine.jdbc.dialect.spi;
-import java.sql.SQLException;
 
-import org.jboss.logging.Logger;
+import java.sql.SQLException;
 
 import org.hibernate.JDBCException;
 import org.hibernate.exception.internal.SQLStateConverter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * A helper to centralize conversion of {@link java.sql.SQLException}s to {@link org.hibernate.JDBCException}s.
  * <p/>
  * Used while querying JDBC metadata during bootstrapping
  *
  * @author Steve Ebersole
  */
 public class BasicSQLExceptionConverter {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( BasicSQLExceptionConverter.class );
 
 	/**
 	 * Singleton access
 	 */
 	public static final BasicSQLExceptionConverter INSTANCE = new BasicSQLExceptionConverter();
 
 	/**
 	 * Message
 	 */
 	public static final String MSG = LOG.unableToQueryDatabaseMetadata();
 
 	private static final SQLStateConverter CONVERTER = new SQLStateConverter( new ConstraintNameExtracter() );
 
 	/**
 	 * Perform a conversion.
 	 *
 	 * @param sqlException The exception to convert.
 	 * @return The converted exception.
 	 */
 	public JDBCException convert(SQLException sqlException) {
 		return CONVERTER.convert( sqlException, MSG, null );
 	}
 
 	private static class ConstraintNameExtracter implements ViolatedConstraintNameExtracter {
 		@Override
 		public String extractConstraintName(SQLException sqle) {
 			return "???";
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
index f3c39a0a95..fc5e45f818 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
@@ -1,518 +1,513 @@
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
 package org.hibernate.engine.jdbc.internal;
 
 import java.lang.reflect.InvocationTargetException;
 import java.sql.Connection;
 import java.sql.DatabaseMetaData;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.LinkedHashSet;
 import java.util.Map;
 import java.util.Set;
 
-import org.jboss.logging.Logger;
-
 import org.hibernate.HibernateException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.LobCreationContext;
 import org.hibernate.engine.jdbc.LobCreator;
-import org.hibernate.engine.jdbc.dialect.spi.BasicSQLExceptionConverter;
+import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
+import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
+import org.hibernate.engine.jdbc.cursor.internal.StandardRefCursorSupport;
 import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
+import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
 import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
 import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfoSource;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.ResultSetWrapper;
 import org.hibernate.engine.jdbc.spi.SchemaNameResolver;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.jdbc.spi.SqlStatementLogger;
 import org.hibernate.engine.jdbc.spi.TypeInfo;
 import org.hibernate.exception.internal.SQLExceptionTypeDelegate;
 import org.hibernate.exception.internal.SQLStateConversionDelegate;
 import org.hibernate.exception.internal.StandardSQLExceptionConverter;
 import org.hibernate.exception.spi.SQLExceptionConverter;
+import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
-import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
-import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
-import org.hibernate.engine.jdbc.cursor.internal.StandardRefCursorSupport;
-import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
 import org.hibernate.service.spi.Configurable;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 /**
  * Standard implementation of the {@link JdbcServices} contract
  *
  * @author Steve Ebersole
  */
 public class JdbcServicesImpl implements JdbcServices, ServiceRegistryAwareService, Configurable {
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
-			CoreMessageLogger.class,
-			JdbcServicesImpl.class.getName()
-	);
+	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( JdbcServicesImpl.class );
 
 	private ServiceRegistryImplementor serviceRegistry;
 
 	private Dialect dialect;
 	private ConnectionProvider connectionProvider;
 	private SqlStatementLogger sqlStatementLogger;
 	private SqlExceptionHelper sqlExceptionHelper;
 	private ExtractedDatabaseMetaData extractedMetaDataSupport;
 	private LobCreatorBuilder lobCreatorBuilder;
 
 	@Override
 	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	@Override
 	public void configure(Map configValues) {
 		final JdbcConnectionAccess jdbcConnectionAccess = buildJdbcConnectionAccess( configValues );
 		final DialectFactory dialectFactory = serviceRegistry.getService( DialectFactory.class );
 
 		Dialect dialect = null;
 		LobCreatorBuilder lobCreatorBuilder = null;
 
 		boolean metaSupportsRefCursors = false;
 		boolean metaSupportsNamedParams = false;
 		boolean metaSupportsScrollable = false;
 		boolean metaSupportsGetGeneratedKeys = false;
 		boolean metaSupportsBatchUpdates = false;
 		boolean metaReportsDDLCausesTxnCommit = false;
 		boolean metaReportsDDLInTxnSupported = true;
 		String extraKeywordsString = "";
 		int sqlStateType = -1;
 		boolean lobLocatorUpdateCopy = false;
 		String catalogName = null;
 		String schemaName = null;
 		final LinkedHashSet<TypeInfo> typeInfoSet = new LinkedHashSet<TypeInfo>();
 
 		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
 		// The need for it is intended to be alleviated with future development, thus it is
 		// not defined as an Environment constant...
 		//
 		// it is used to control whether we should consult the JDBC metadata to determine
 		// certain Settings default values; it is useful to *not* do this when the database
 		// may not be available (mainly in tools usage).
 		final boolean useJdbcMetadata = ConfigurationHelper.getBoolean( "hibernate.temp.use_jdbc_metadata_defaults", configValues, true );
 		if ( useJdbcMetadata ) {
 			try {
 				final Connection connection = jdbcConnectionAccess.obtainConnection();
 				try {
 					final DatabaseMetaData meta = connection.getMetaData();
 					if ( LOG.isDebugEnabled() ) {
 						LOG.debugf(
 								"Database ->\n"
 										+ "       name : %s\n"
 										+ "    version : %s\n"
 										+ "      major : %s\n"
 										+ "      minor : %s",
 								meta.getDatabaseProductName(),
 								meta.getDatabaseProductVersion(),
 								meta.getDatabaseMajorVersion(),
 								meta.getDatabaseMinorVersion()
 						);
 						LOG.debugf(
 								"Driver ->\n"
 										+ "       name : %s\n"
 										+ "    version : %s\n"
 										+ "      major : %s\n"
 										+ "      minor : %s",
 								meta.getDriverName(),
 								meta.getDriverVersion(),
 								meta.getDriverMajorVersion(),
 								meta.getDriverMinorVersion()
 						);
 						LOG.debugf( "JDBC version : %s.%s", meta.getJDBCMajorVersion(), meta.getJDBCMinorVersion() );
 					}
 
 					metaSupportsRefCursors = StandardRefCursorSupport.supportsRefCursors( meta );
 					metaSupportsNamedParams = meta.supportsNamedParameters();
 					metaSupportsScrollable = meta.supportsResultSetType( ResultSet.TYPE_SCROLL_INSENSITIVE );
 					metaSupportsBatchUpdates = meta.supportsBatchUpdates();
 					metaReportsDDLCausesTxnCommit = meta.dataDefinitionCausesTransactionCommit();
 					metaReportsDDLInTxnSupported = !meta.dataDefinitionIgnoredInTransactions();
 					metaSupportsGetGeneratedKeys = meta.supportsGetGeneratedKeys();
 					extraKeywordsString = meta.getSQLKeywords();
 					sqlStateType = meta.getSQLStateType();
 					lobLocatorUpdateCopy = meta.locatorsUpdateCopy();
 					typeInfoSet.addAll( TypeInfo.extractTypeInfo( meta ) );
 
 					dialect = dialectFactory.buildDialect(
 							configValues,
 							new DialectResolutionInfoSource() {
 								@Override
 								public DialectResolutionInfo getDialectResolutionInfo() {
 									try {
 										return new DatabaseMetaDataDialectResolutionInfoAdapter( connection.getMetaData() );
 									}
 									catch ( SQLException sqlException ) {
 										throw new HibernateException(
 												"Unable to access java.sql.DatabaseMetaData to determine appropriate Dialect to use",
 												sqlException
 										);
 									}
 								}
 							}
 					);
 
 					catalogName = connection.getCatalog();
 					final SchemaNameResolver schemaNameResolver = determineExplicitSchemaNameResolver( configValues );
 					if ( schemaNameResolver == null ) {
 // todo : add dialect method
 //						schemaNameResolver = dialect.getSchemaNameResolver();
 					}
 					if ( schemaNameResolver != null ) {
 						schemaName = schemaNameResolver.resolveSchemaName( connection );
 					}
 					lobCreatorBuilder = new LobCreatorBuilder( configValues, connection );
 				}
 				catch ( SQLException sqle ) {
 					LOG.unableToObtainConnectionMetadata( sqle.getMessage() );
 				}
 				finally {
 					if ( connection != null ) {
 						jdbcConnectionAccess.releaseConnection( connection );
 					}
 				}
 			}
 			catch ( SQLException sqle ) {
 				LOG.unableToObtainConnectionToQueryMetadata( sqle.getMessage() );
 				dialect = dialectFactory.buildDialect( configValues, null );
 			}
 			catch ( UnsupportedOperationException uoe ) {
 				// user supplied JDBC connections
 				dialect = dialectFactory.buildDialect( configValues, null );
 			}
 		}
 		else {
 			dialect = dialectFactory.buildDialect( configValues, null );
 		}
 
 		final boolean showSQL = ConfigurationHelper.getBoolean( Environment.SHOW_SQL, configValues, false );
 		final boolean formatSQL = ConfigurationHelper.getBoolean( Environment.FORMAT_SQL, configValues, false );
 
 		this.dialect = dialect;
 		this.lobCreatorBuilder = (
 				lobCreatorBuilder == null ?
 						new LobCreatorBuilder( configValues, null ) :
 						lobCreatorBuilder
 		);
 
 		this.sqlStatementLogger =  new SqlStatementLogger( showSQL, formatSQL );
 
 		this.extractedMetaDataSupport = new ExtractedDatabaseMetaDataImpl(
 				metaSupportsRefCursors,
 				metaSupportsNamedParams,
 				metaSupportsScrollable,
 				metaSupportsGetGeneratedKeys,
 				metaSupportsBatchUpdates,
 				metaReportsDDLInTxnSupported,
 				metaReportsDDLCausesTxnCommit,
 				parseKeywords( extraKeywordsString ),
 				parseSQLStateType( sqlStateType ),
 				lobLocatorUpdateCopy,
 				schemaName,
 				catalogName,
 				typeInfoSet
 		);
 
 		SQLExceptionConverter sqlExceptionConverter = dialect.buildSQLExceptionConverter();
 		if ( sqlExceptionConverter == null ) {
 			final StandardSQLExceptionConverter converter = new StandardSQLExceptionConverter();
 			sqlExceptionConverter = converter;
 			converter.addDelegate( dialect.buildSQLExceptionConversionDelegate() );
 			converter.addDelegate( new SQLExceptionTypeDelegate( dialect ) );
 			// todo : vary this based on extractedMetaDataSupport.getSqlStateType()
 			converter.addDelegate( new SQLStateConversionDelegate( dialect ) );
 		}
 		this.sqlExceptionHelper = new SqlExceptionHelper( sqlExceptionConverter );
 	}
 
 	private JdbcConnectionAccess buildJdbcConnectionAccess(Map configValues) {
 		final MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( configValues );
 
 		if ( MultiTenancyStrategy.NONE == multiTenancyStrategy ) {
 			connectionProvider = serviceRegistry.getService( ConnectionProvider.class );
 			return new ConnectionProviderJdbcConnectionAccess( connectionProvider );
 		}
 		else {
 			connectionProvider = null;
 			final MultiTenantConnectionProvider multiTenantConnectionProvider = serviceRegistry.getService( MultiTenantConnectionProvider.class );
 			return new MultiTenantConnectionProviderJdbcConnectionAccess( multiTenantConnectionProvider );
 		}
 	}
 
 	private static class ConnectionProviderJdbcConnectionAccess implements JdbcConnectionAccess {
 		private final ConnectionProvider connectionProvider;
 
 		public ConnectionProviderJdbcConnectionAccess(ConnectionProvider connectionProvider) {
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
 
 		@Override
 		public boolean supportsAggressiveRelease() {
 			return connectionProvider.supportsAggressiveRelease();
 		}
 	}
 
 	private static class MultiTenantConnectionProviderJdbcConnectionAccess implements JdbcConnectionAccess {
 		private final MultiTenantConnectionProvider connectionProvider;
 
 		public MultiTenantConnectionProviderJdbcConnectionAccess(MultiTenantConnectionProvider connectionProvider) {
 			this.connectionProvider = connectionProvider;
 		}
 
 		@Override
 		public Connection obtainConnection() throws SQLException {
 			return connectionProvider.getAnyConnection();
 		}
 
 		@Override
 		public void releaseConnection(Connection connection) throws SQLException {
 			connectionProvider.releaseAnyConnection( connection );
 		}
 
 		@Override
 		public boolean supportsAggressiveRelease() {
 			return connectionProvider.supportsAggressiveRelease();
 		}
 	}
 
 
 	/**
 	 * A constant naming the setting used to identify the {@link SchemaNameResolver} to use
 	 * <p/>
 	 * TODO : add to Environment
 	 */
 	public static final String SCHEMA_NAME_RESOLVER = "hibernate.schema_name_resolver";
 
 	private SchemaNameResolver determineExplicitSchemaNameResolver(Map configValues) {
 		final Object setting = configValues.get( SCHEMA_NAME_RESOLVER );
 		if ( SchemaNameResolver.class.isInstance( setting ) ) {
 			return (SchemaNameResolver) setting;
 		}
 
 		final String resolverClassName = (String) setting;
 		if ( resolverClassName != null ) {
 			try {
 				final Class resolverClass = ReflectHelper.classForName( resolverClassName, getClass() );
 				return (SchemaNameResolver) ReflectHelper.getDefaultConstructor( resolverClass ).newInstance();
 			}
 			catch ( ClassNotFoundException e ) {
 				LOG.unableToLocateConfiguredSchemaNameResolver( resolverClassName, e.toString() );
 			}
 			catch ( InvocationTargetException e ) {
 				LOG.unableToInstantiateConfiguredSchemaNameResolver( resolverClassName, e.getTargetException().toString() );
 			}
 			catch ( Exception e ) {
 				LOG.unableToInstantiateConfiguredSchemaNameResolver( resolverClassName, e.toString() );
 			}
 		}
 		return null;
 	}
 
 	private Set<String> parseKeywords(String extraKeywordsString) {
 		final Set<String> keywordSet = new HashSet<String>();
 		keywordSet.addAll( Arrays.asList( extraKeywordsString.split( "," ) ) );
 		return keywordSet;
 	}
 
 	private ExtractedDatabaseMetaData.SQLStateType parseSQLStateType(int sqlStateType) {
 		switch ( sqlStateType ) {
 			case DatabaseMetaData.sqlStateSQL99 : {
 				return ExtractedDatabaseMetaData.SQLStateType.SQL99;
 			}
 			case DatabaseMetaData.sqlStateXOpen : {
 				return ExtractedDatabaseMetaData.SQLStateType.XOpen;
 			}
 			default : {
 				return ExtractedDatabaseMetaData.SQLStateType.UNKOWN;
 			}
 		}
 	}
 
 	private static class ExtractedDatabaseMetaDataImpl implements ExtractedDatabaseMetaData {
 		private final boolean supportsRefCursors;
 		private final boolean supportsNamedParameters;
 		private final boolean supportsScrollableResults;
 		private final boolean supportsGetGeneratedKeys;
 		private final boolean supportsBatchUpdates;
 		private final boolean supportsDataDefinitionInTransaction;
 		private final boolean doesDataDefinitionCauseTransactionCommit;
 		private final Set<String> extraKeywords;
 		private final SQLStateType sqlStateType;
 		private final boolean lobLocatorUpdateCopy;
 		private final String connectionSchemaName;
 		private final String connectionCatalogName;
 		private final LinkedHashSet<TypeInfo> typeInfoSet;
 
 		private ExtractedDatabaseMetaDataImpl(
 				boolean supportsRefCursors,
 				boolean supportsNamedParameters,
 				boolean supportsScrollableResults,
 				boolean supportsGetGeneratedKeys,
 				boolean supportsBatchUpdates,
 				boolean supportsDataDefinitionInTransaction,
 				boolean doesDataDefinitionCauseTransactionCommit,
 				Set<String> extraKeywords,
 				SQLStateType sqlStateType,
 				boolean lobLocatorUpdateCopy,
 				String connectionSchemaName,
 				String connectionCatalogName,
 				LinkedHashSet<TypeInfo> typeInfoSet) {
 			this.supportsRefCursors = supportsRefCursors;
 			this.supportsNamedParameters = supportsNamedParameters;
 			this.supportsScrollableResults = supportsScrollableResults;
 			this.supportsGetGeneratedKeys = supportsGetGeneratedKeys;
 			this.supportsBatchUpdates = supportsBatchUpdates;
 			this.supportsDataDefinitionInTransaction = supportsDataDefinitionInTransaction;
 			this.doesDataDefinitionCauseTransactionCommit = doesDataDefinitionCauseTransactionCommit;
 			this.extraKeywords = extraKeywords;
 			this.sqlStateType = sqlStateType;
 			this.lobLocatorUpdateCopy = lobLocatorUpdateCopy;
 			this.connectionSchemaName = connectionSchemaName;
 			this.connectionCatalogName = connectionCatalogName;
 			this.typeInfoSet = typeInfoSet;
 		}
 
 		@Override
 		public boolean supportsRefCursors() {
 			return supportsRefCursors;
 		}
 
 		@Override
 		public boolean supportsNamedParameters() {
 			return supportsNamedParameters;
 		}
 
 		@Override
 		public boolean supportsScrollableResults() {
 			return supportsScrollableResults;
 		}
 
 		@Override
 		public boolean supportsGetGeneratedKeys() {
 			return supportsGetGeneratedKeys;
 		}
 
 		@Override
 		public boolean supportsBatchUpdates() {
 			return supportsBatchUpdates;
 		}
 
 		@Override
 		public boolean supportsDataDefinitionInTransaction() {
 			return supportsDataDefinitionInTransaction;
 		}
 
 		@Override
 		public boolean doesDataDefinitionCauseTransactionCommit() {
 			return doesDataDefinitionCauseTransactionCommit;
 		}
 
 		@Override
 		public Set<String> getExtraKeywords() {
 			return extraKeywords;
 		}
 
 		@Override
 		public SQLStateType getSqlStateType() {
 			return sqlStateType;
 		}
 
 		@Override
 		public boolean doesLobLocatorUpdateCopy() {
 			return lobLocatorUpdateCopy;
 		}
 
 		@Override
 		public String getConnectionSchemaName() {
 			return connectionSchemaName;
 		}
 
 		@Override
 		public String getConnectionCatalogName() {
 			return connectionCatalogName;
 		}
 
 		@Override
 		public LinkedHashSet<TypeInfo> getTypeInfoSet() {
 			return typeInfoSet;
 		}
 	}
 
 	@Override
 	public ConnectionProvider getConnectionProvider() {
 		return connectionProvider;
 	}
 
 	@Override
 	public SqlStatementLogger getSqlStatementLogger() {
 		return sqlStatementLogger;
 	}
 
 	@Override
 	public SqlExceptionHelper getSqlExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	@Override
 	public Dialect getDialect() {
 		return dialect;
 	}
 
 	@Override
 	public ExtractedDatabaseMetaData getExtractedMetaDataSupport() {
 		return extractedMetaDataSupport;
 	}
 
 	@Override
 	public LobCreator getLobCreator(LobCreationContext lobCreationContext) {
 		return lobCreatorBuilder.buildLobCreator( lobCreationContext );
 	}
 
 	@Override
 	public ResultSetWrapper getResultSetWrapper() {
 		return ResultSetWrapperImpl.INSTANCE;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/LoadQueryInfluencers.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/LoadQueryInfluencers.java
index 86cb49c34c..d29aeed0c8 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/LoadQueryInfluencers.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/LoadQueryInfluencers.java
@@ -1,217 +1,215 @@
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
 
+import javax.persistence.EntityGraph;
 import java.io.Serializable;
-import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
-import javax.persistence.EntityGraph;
-
 import org.hibernate.Filter;
 import org.hibernate.UnknownProfileException;
 import org.hibernate.internal.FilterImpl;
 import org.hibernate.type.Type;
 
 /**
  * Centralize all options which can influence the SQL query needed to load an
  * entity.  Currently such influencers are defined as:<ul>
  * <li>filters</li>
  * <li>fetch profiles</li>
  * <li>internal fetch profile (merge profile, etc)</li>
  * </ul>
  *
  * @author Steve Ebersole
  */
 public class LoadQueryInfluencers implements Serializable {
 	/**
 	 * Static reference useful for cases where we are creating load SQL
 	 * outside the context of any influencers.  One such example is
 	 * anything created by the session factory.
 	 */
 	public static final LoadQueryInfluencers NONE = new LoadQueryInfluencers();
 
 	private final SessionFactoryImplementor sessionFactory;
 	private String internalFetchProfile;
 	private final Map<String,Filter> enabledFilters;
 	private final Set<String> enabledFetchProfileNames;
 	private EntityGraph fetchGraph;
 	private EntityGraph loadGraph;
 
 	public LoadQueryInfluencers() {
 		this( null );
 	}
 
 	public LoadQueryInfluencers(SessionFactoryImplementor sessionFactory) {
 		this( sessionFactory, new HashMap<String,Filter>(), new HashSet<String>() );
 	}
 
 	private LoadQueryInfluencers(SessionFactoryImplementor sessionFactory, Map<String,Filter> enabledFilters, Set<String> enabledFetchProfileNames) {
 		this.sessionFactory = sessionFactory;
 		this.enabledFilters = enabledFilters;
 		this.enabledFetchProfileNames = enabledFetchProfileNames;
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 
 	// internal fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public String getInternalFetchProfile() {
 		return internalFetchProfile;
 	}
 
 	public void setInternalFetchProfile(String internalFetchProfile) {
 		if ( sessionFactory == null ) {
 			// thats the signal that this is the immutable, context-less
 			// variety
 			throw new IllegalStateException( "Cannot modify context-less LoadQueryInfluencers" );
 		}
 		this.internalFetchProfile = internalFetchProfile;
 	}
 
 
 	// filter support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean hasEnabledFilters() {
 		return !enabledFilters.isEmpty();
 	}
 
 	public Map<String,Filter> getEnabledFilters() {
 		// First, validate all the enabled filters...
 		//TODO: this implementation has bad performance
 		for ( Filter filter : enabledFilters.values() ) {
 			filter.validate();
 		}
 		return enabledFilters;
 	}
 
 	/**
 	 * Returns an unmodifiable Set of enabled filter names.
 	 * @return an unmodifiable Set of enabled filter names.
 	 */
 	public Set<String> getEnabledFilterNames() {
 		return java.util.Collections.unmodifiableSet( enabledFilters.keySet() );
 	}
 
 	public Filter getEnabledFilter(String filterName) {
 		return enabledFilters.get( filterName );
 	}
 
 	public Filter enableFilter(String filterName) {
 		FilterImpl filter = new FilterImpl( sessionFactory.getFilterDefinition( filterName ) );
 		enabledFilters.put( filterName, filter );
 		return filter;
 	}
 
 	public void disableFilter(String filterName) {
 		enabledFilters.remove( filterName );
 	}
 
 	public Object getFilterParameterValue(String filterParameterName) {
 		String[] parsed = parseFilterParameterName( filterParameterName );
 		FilterImpl filter = ( FilterImpl ) enabledFilters.get( parsed[0] );
 		if ( filter == null ) {
 			throw new IllegalArgumentException( "Filter [" + parsed[0] + "] currently not enabled" );
 		}
 		return filter.getParameter( parsed[1] );
 	}
 
 	public Type getFilterParameterType(String filterParameterName) {
 		String[] parsed = parseFilterParameterName( filterParameterName );
 		FilterDefinition filterDef = sessionFactory.getFilterDefinition( parsed[0] );
 		if ( filterDef == null ) {
 			throw new IllegalArgumentException( "Filter [" + parsed[0] + "] not defined" );
 		}
 		Type type = filterDef.getParameterType( parsed[1] );
 		if ( type == null ) {
 			// this is an internal error of some sort...
 			throw new InternalError( "Unable to locate type for filter parameter" );
 		}
 		return type;
 	}
 
 	public static String[] parseFilterParameterName(String filterParameterName) {
 		int dot = filterParameterName.indexOf( '.' );
 		if ( dot <= 0 ) {
 			throw new IllegalArgumentException( "Invalid filter-parameter name format" );
 		}
 		String filterName = filterParameterName.substring( 0, dot );
 		String parameterName = filterParameterName.substring( dot + 1 );
 		return new String[] { filterName, parameterName };
 	}
 
 
 	// fetch profile support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public boolean hasEnabledFetchProfiles() {
 		return !enabledFetchProfileNames.isEmpty();
 	}
 
 	public Set<String> getEnabledFetchProfileNames() {
 		return enabledFetchProfileNames;
 	}
 
 	private void checkFetchProfileName(String name) {
 		if ( !sessionFactory.containsFetchProfileDefinition( name ) ) {
 			throw new UnknownProfileException( name );
 		}
 	}
 
 	public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
 		checkFetchProfileName( name );
 		return enabledFetchProfileNames.contains( name );
 	}
 
 	public void enableFetchProfile(String name) throws UnknownProfileException {
 		checkFetchProfileName( name );
 		enabledFetchProfileNames.add( name );
 	}
 
 	public void disableFetchProfile(String name) throws UnknownProfileException {
 		checkFetchProfileName( name );
 		enabledFetchProfileNames.remove( name );
 	}
 
 	public EntityGraph getFetchGraph() {
 		return fetchGraph;
 	}
 
 	public void setFetchGraph(final EntityGraph fetchGraph) {
 		this.fetchGraph = fetchGraph;
 	}
 
 	public EntityGraph getLoadGraph() {
 		return loadGraph;
 	}
 
 	public void setLoadGraph(final EntityGraph loadGraph) {
 		this.loadGraph = loadGraph;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
index 6a2fab2e10..a8537b10df 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
@@ -1,295 +1,294 @@
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
 import org.hibernate.EntityNameResolver;
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
+import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.NamedQueryRepository;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
-import org.hibernate.procedure.ProcedureCallMemento;
 import org.hibernate.proxy.EntityNotFoundDelegate;
-import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
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
 	 *
 	 * @deprecated Access to connections via {@link org.hibernate.engine.jdbc.spi.JdbcConnectionAccess} should
 	 * be preferred over access via {@link ConnectionProvider}, whenever possible.
 	 * {@link org.hibernate.engine.jdbc.spi.JdbcConnectionAccess} is tied to the Hibernate Session to
 	 * properly account for contextual information.  See {@link SessionImplementor#getJdbcConnectionAccess()}
 	 */
 	@Deprecated
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
 
 	public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition);
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName);
 
 	public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition);
 
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
 
 	/**
 	 * Provides access to the named query repository
 	 *
-	 * @return
+	 * @return The repository for named query definitions
 	 */
 	public NamedQueryRepository getNamedQueryRepository();
 
 	Iterable<EntityNameResolver> iterateEntityNameResolvers();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/transaction/synchronization/internal/SynchronizationCallbackCoordinatorNonTrackingImpl.java b/hibernate-core/src/main/java/org/hibernate/engine/transaction/synchronization/internal/SynchronizationCallbackCoordinatorNonTrackingImpl.java
index 24539c9539..3e1d76707d 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/transaction/synchronization/internal/SynchronizationCallbackCoordinatorNonTrackingImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/transaction/synchronization/internal/SynchronizationCallbackCoordinatorNonTrackingImpl.java
@@ -1,192 +1,190 @@
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
 package org.hibernate.engine.transaction.synchronization.internal;
 
 import javax.transaction.SystemException;
 
-import org.jboss.logging.Logger;
-
 import org.hibernate.TransactionException;
 import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
 import org.hibernate.engine.transaction.spi.TransactionContext;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.synchronization.spi.AfterCompletionAction;
 import org.hibernate.engine.transaction.synchronization.spi.ExceptionMapper;
 import org.hibernate.engine.transaction.synchronization.spi.ManagedFlushChecker;
 import org.hibernate.engine.transaction.synchronization.spi.SynchronizationCallbackCoordinator;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
  * Manages callbacks from the {@link javax.transaction.Synchronization} registered by Hibernate.
  * 
  * @author Steve Ebersole
  */
 public class SynchronizationCallbackCoordinatorNonTrackingImpl implements SynchronizationCallbackCoordinator {
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger(
 			SynchronizationCallbackCoordinatorNonTrackingImpl.class
 	);
 
 	private final TransactionCoordinator transactionCoordinator;
 
 	private ManagedFlushChecker managedFlushChecker;
 	private AfterCompletionAction afterCompletionAction;
 	private ExceptionMapper exceptionMapper;
 
 	public SynchronizationCallbackCoordinatorNonTrackingImpl(TransactionCoordinator transactionCoordinator) {
 		this.transactionCoordinator = transactionCoordinator;
 		reset();
 	}
 
 	public void reset() {
 		managedFlushChecker = STANDARD_MANAGED_FLUSH_CHECKER;
 		exceptionMapper = STANDARD_EXCEPTION_MAPPER;
 		afterCompletionAction = STANDARD_AFTER_COMPLETION_ACTION;
 	}
 
 	private TransactionContext transactionContext() {
 		return transactionCoordinator.getTransactionContext();
 	}
 
 	@Override
 	public void setManagedFlushChecker(ManagedFlushChecker managedFlushChecker) {
 		this.managedFlushChecker = managedFlushChecker;
 	}
 
 	@Override
 	public void setExceptionMapper(ExceptionMapper exceptionMapper) {
 		this.exceptionMapper = exceptionMapper;
 	}
 
 	@Override
 	public void setAfterCompletionAction(AfterCompletionAction afterCompletionAction) {
 		this.afterCompletionAction = afterCompletionAction;
 	}
 
 	// sync callbacks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public void beforeCompletion() {
 		LOG.trace( "Transaction before completion callback" );
 
 		if ( !transactionCoordinator.isActive() ) {
 			return;
 		}
 
 		boolean flush;
 		try {
 			final int status = transactionCoordinator.getTransactionContext().getTransactionEnvironment()
 					.getJtaPlatform().getCurrentStatus();
 			flush = managedFlushChecker.shouldDoManagedFlush( transactionCoordinator, status );
 		}
 		catch ( SystemException se ) {
 			setRollbackOnly();
 			throw exceptionMapper.mapStatusCheckFailure(
 					"could not determine transaction status in beforeCompletion()", se );
 		}
 
 		try {
 			if ( flush ) {
 				LOG.trace( "Automatically flushing session" );
 				transactionCoordinator.getTransactionContext().managedFlush();
 			}
 		}
 		catch ( RuntimeException re ) {
 			setRollbackOnly();
 			throw exceptionMapper.mapManagedFlushFailure( "error during managed flush", re );
 		}
 		finally {
 			transactionCoordinator.sendBeforeTransactionCompletionNotifications( null );
 			transactionCoordinator.getTransactionContext().beforeTransactionCompletion( null );
 		}
 	}
 
 	private void setRollbackOnly() {
 		transactionCoordinator.setRollbackOnly();
 	}
 
 	@Override
 	public void afterCompletion(int status) {
 		doAfterCompletion( status );
 	}
 
 	protected void doAfterCompletion(int status) {
 		LOG.tracef( "Starting transaction afterCompletion callback [status=%s]", status );
 		if ( !transactionCoordinator.isActive() ) {
 			return;
 		}
 
 		try {
 			afterCompletionAction.doAction( transactionCoordinator, status );
 			transactionCoordinator.afterTransaction( null, status );
 		}
 		finally {
 			reset();
 			if ( transactionContext().shouldAutoClose() && !transactionContext().isClosed() ) {
 				LOG.trace( "Automatically closing session" );
 				transactionContext().managedClose();
 			}
 		}
 	}
 
 	@Override
 	public void synchronizationRegistered() {
 	}
 
 	@Override
 	public void processAnyDelayedAfterCompletion() {
 	}
 
 	private static final ManagedFlushChecker STANDARD_MANAGED_FLUSH_CHECKER = new ManagedFlushChecker() {
 		@Override
 		public boolean shouldDoManagedFlush(TransactionCoordinator coordinator, int jtaStatus) {
 			return !coordinator.getTransactionContext().isClosed()
 					&& !coordinator.getTransactionContext().isFlushModeNever()
 					&& coordinator.getTransactionContext().isFlushBeforeCompletionEnabled()
 					&& !JtaStatusHelper.isRollback( jtaStatus );
 		}
 	};
 
 	private static final ExceptionMapper STANDARD_EXCEPTION_MAPPER = new ExceptionMapper() {
 		@Override
 		public RuntimeException mapStatusCheckFailure(String message, SystemException systemException) {
 			LOG.error( LOG.unableToDetermineTransactionStatus(), systemException );
 			return new TransactionException( "could not determine transaction status in beforeCompletion()",
 					systemException );
 		}
 
 		@Override
 		public RuntimeException mapManagedFlushFailure(String message, RuntimeException failure) {
 			LOG.unableToPerformManagedFlush( failure.getMessage() );
 			return failure;
 		}
 	};
 
 	private static final AfterCompletionAction STANDARD_AFTER_COMPLETION_ACTION = new AfterCompletionAction() {
 		@Override
 		public void doAction(TransactionCoordinator transactionCoordinator, int status) {
 			// nothing to do by default.
 		}
 	};
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
index c021d8b402..a04acb4948 100755
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultFlushEntityEventListener.java
@@ -1,694 +1,691 @@
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
 
-import org.hibernate.engine.spi.SelfDirtinessTracker;
-
-import org.jboss.logging.Logger;
-
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
+import org.hibernate.engine.spi.SelfDirtinessTracker;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.FlushEntityEvent;
 import org.hibernate.event.spi.FlushEntityEventListener;
 import org.hibernate.internal.CoreLogging;
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
 	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( DefaultFlushEntityEventListener.class );
 
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
 			if ( id == null ) {
 				throw new AssertionFailure( "null id in " + persister.getEntityName() + " entry (don't flush the Session after an exception occurs)" );
 			}
 			if ( !persister.getIdentifierType().isEqual( id, oid, session.getFactory() ) ) {
 				throw new HibernateException(
 						"identifier of an instance of " + persister.getEntityName() + " was altered from "
 								+ id + " to " + oid
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
 			if ( !persister.getEntityMetamodel().hasImmutableNaturalId() ) {
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
 
 			for ( int i = 0; i < naturalIdentifierPropertiesIndexes.length; i++ ) {
 				final int naturalIdentifierPropertyIndex = naturalIdentifierPropertiesIndexes[i];
 				if ( propertyUpdateability[naturalIdentifierPropertyIndex] ) {
 					// if the given natural id property is updatable (mutable), there is nothing to check
 					continue;
 				}
 
 				final Type propertyType = propertyTypes[naturalIdentifierPropertyIndex];
 				if ( !propertyType.isEqual( current[naturalIdentifierPropertyIndex], snapshot[i] ) ) {
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
 
 		final boolean mightBeDirty = entry.requiresDirtyCheck( entity );
 
 		final Object[] values = getValues( entity, entry, mightBeDirty, session );
 
 		event.setPropertyValues( values );
 
 		//TODO: avoid this for non-new instances where mightBeDirty==false
 		boolean substitute = wrapCollections( session, persister, types, values );
 
 		if ( isUpdateNecessary( event, mightBeDirty ) ) {
 			substitute = scheduleUpdate( event ) || substitute;
 		}
 
 		if ( status != Status.DELETED ) {
 			// now update the object .. has to be outside the main if block above (because of collections)
 			if ( substitute ) {
 				persister.setPropertyValues( entity, values );
 			}
 
 			// Search for collections by reachability, updating their role.
 			// We don't want to touch collections reachable from a deleted object
 			if ( persister.hasCollections() ) {
 				new FlushVisitor( session, entity ).processEntityPropertyValues( values, types );
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
 		else if ( !mightBeDirty && loadedState != null ) {
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
 
 			WrapVisitor visitor = new WrapVisitor( session );
 			// substitutes into values by side-effect
 			visitor.processEntityPropertyValues( values, types );
 			return visitor.isSubstitutionRequired();
 		}
 		else {
 			return false;
 		}
 	}
 
 	private boolean isUpdateNecessary(final FlushEntityEvent event, final boolean mightBeDirty) {
 		final Status status = event.getEntityEntry().getStatus();
 		if ( mightBeDirty || status == Status.DELETED ) {
 			// compare to cached state (ignoring collections unless versioned)
 			dirtyCheck( event );
 			if ( isUpdateNecessary( event ) ) {
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
 					LOG.tracev(
 							"Updating immutable, deleted entity: {0}",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() )
 					);
 				}
 				else if ( !entry.isModifiableEntity() ) {
 					LOG.tracev(
 							"Updating non-modifiable, deleted entity: {0}",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() )
 					);
 				}
 				else {
 					LOG.tracev(
 							"Updating deleted entity: ",
 							MessageHelper.infoString( persister, entry.getId(), session.getFactory() )
 					);
 				}
 			}
 			else {
 				LOG.tracev(
 						"Updating entity: {0}",
 						MessageHelper.infoString( persister, entry.getId(), session.getFactory() )
 				);
 			}
 		}
 
 		final boolean intercepted = !entry.isBeingReplicated() && handleInterception( event );
 
 		// increment the version number (if necessary)
 		final Object nextVersion = getNextVersion( event );
 
 		// if it was dirtied by a collection only
 		int[] dirtyProperties = event.getDirtyProperties();
 		if ( event.isDirtyCheckPossible() && dirtyProperties == null ) {
 			if ( !intercepted && !event.hasDirtyCollection() ) {
 				throw new AssertionFailure( "dirty, but no dirty properties" );
 			}
 			dirtyProperties = ArrayHelper.EMPTY_INT_ARRAY;
 		}
 
 		// check nullability but do not doAfterTransactionCompletion command execute
 		// we'll use scheduled updates for that.
 		new Nullability( session ).checkNullability( values, persister, true );
 
 		// schedule the update
 		// note that we intentionally do _not_ pass in currentPersistentState!
 		session.getActionQueue().addAction(
 				new EntityUpdateAction(
 						entry.getId(),
 						values,
 						dirtyProperties,
 						event.hasDirtyCollection(),
 						( status == Status.DELETED && !entry.isModifiableEntity() ?
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
 			event.setDirtyProperties( dirtyProperties );
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
 				return Versioning.getVersion( values, persister );
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
 
 				Versioning.setVersion( values, nextVersion, persister );
 
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
 		final boolean isVersionIncrementRequired = entry.getStatus() != Status.DELETED && (
 				dirtyProperties == null ||
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
 			if ( dirtyProperties != null && dirtyProperties.length != 0 ) {
 				return true; //TODO: suck into event class
 			}
 			else {
 				return hasDirtyCollections( event, persister, status );
 			}
 
 		}
 	}
 
 	private boolean hasDirtyCollections(FlushEntityEvent event, EntityPersister persister, Status status) {
 		if ( isCollectionDirtyCheckNecessary( persister, status ) ) {
 			DirtyCollectionSearchVisitor visitor = new DirtyCollectionSearchVisitor(
 					event.getSession(),
 					persister.getPropertyVersionability()
 			);
 			visitor.processEntityPropertyValues( event.getPropertyValues(), persister.getPropertyTypes() );
 			boolean hasDirtyCollections = visitor.wasDirtyCollectionFound();
 			event.setHasDirtyCollection( hasDirtyCollections );
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
 			if ( entity instanceof SelfDirtinessTracker ) {
 				if ( ( (SelfDirtinessTracker) entity ).$$_hibernate_hasDirtyAttributes() ) {
 					dirtyProperties = persister.resolveAttributeIndexes( ( (SelfDirtinessTracker) entity ).$$_hibernate_getDirtyAttributes() );
 				}
 			}
 			else {
 				// see if the custom dirtiness strategy can tell us...
 				class DirtyCheckContextImpl implements CustomEntityDirtinessStrategy.DirtyCheckContext {
 					int[] found;
 
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
 
 		event.setDatabaseSnapshot( null );
 
 		final boolean interceptorHandledDirtyCheck;
 		boolean cannotDirtyCheck;
 
 		if ( dirtyProperties == null ) {
 			// Interceptor returned null, so do the dirtycheck ourself, if possible
 			try {
 				session.getEventListenerManager().dirtyCalculationStart();
 
 				interceptorHandledDirtyCheck = false;
 				// object loaded by update()
 				cannotDirtyCheck = loadedState == null;
 				if ( !cannotDirtyCheck ) {
 					// dirty check against the usual snapshot of the entity
 					dirtyProperties = persister.findDirty( values, loadedState, entity, session );
 				}
 				else if ( entry.getStatus() == Status.DELETED && !event.getEntityEntry().isModifiableEntity() ) {
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
 					final Object[] databaseSnapshot = getDatabaseSnapshot( session, persister, id );
 					if ( databaseSnapshot != null ) {
 						dirtyProperties = persister.findModified( databaseSnapshot, values, entity, session );
 						cannotDirtyCheck = false;
 						event.setDatabaseSnapshot( databaseSnapshot );
 					}
 				}
 			}
 			finally {
 				session.getEventListenerManager().dirtyCalculationEnd( dirtyProperties != null );
 			}
 		}
 		else {
 			// the Interceptor handled the dirty checking
 			cannotDirtyCheck = false;
 			interceptorHandledDirtyCheck = true;
 		}
 
 		logDirtyProperties( id, dirtyProperties, persister );
 
 		event.setDirtyProperties( dirtyProperties );
 		event.setDirtyCheckHandledByInterceptor( interceptorHandledDirtyCheck );
 		event.setDirtyCheckPossible( !cannotDirtyCheck );
 
 	}
 
 	private class DirtyCheckAttributeInfoImpl implements CustomEntityDirtinessStrategy.AttributeInformation {
 		private final FlushEntityEvent event;
 		private final EntityPersister persister;
 		private final int numberOfAttributes;
 		private int index;
 
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
 			return persister.getPropertyNames()[index];
 		}
 
 		@Override
 		public Type getType() {
 			return persister.getPropertyTypes()[index];
 		}
 
 		@Override
 		public Object getCurrentValue() {
 			return event.getPropertyValues()[index];
 		}
 
 		Object[] databaseSnapshot;
 
 		@Override
 		public Object getLoadedValue() {
 			if ( databaseSnapshot == null ) {
 				databaseSnapshot = getDatabaseSnapshot( event.getSession(), persister, event.getEntityEntry().getId() );
 			}
 			return databaseSnapshot[index];
 		}
 
 		public int[] visitAttributes(CustomEntityDirtinessStrategy.AttributeChecker attributeChecker) {
 			databaseSnapshot = null;
 			index = 0;
 
 			final int[] indexes = new int[numberOfAttributes];
 			int count = 0;
 			for (; index < numberOfAttributes; index++ ) {
 				if ( attributeChecker.isDirty( this ) ) {
 					indexes[count++] = index;
 				}
 			}
 			return Arrays.copyOf( indexes, count );
 		}
 	}
 
 	private void logDirtyProperties(Serializable id, int[] dirtyProperties, EntityPersister persister) {
 		if ( dirtyProperties != null && dirtyProperties.length > 0 && LOG.isTraceEnabled() ) {
 			final String[] allPropertyNames = persister.getPropertyNames();
 			final String[] dirtyPropertyNames = new String[dirtyProperties.length];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				dirtyPropertyNames[i] = allPropertyNames[dirtyProperties[i]];
 			}
 			LOG.tracev(
 					"Found dirty properties [{0}] : {1}",
 					MessageHelper.infoString( persister.getEntityName(), id ),
 					dirtyPropertyNames
 			);
 		}
 	}
 
 	private Object[] getDatabaseSnapshot(SessionImplementor session, EntityPersister persister, Serializable id) {
 		if ( persister.isSelectBeforeUpdateRequired() ) {
 			Object[] snapshot = session.getPersistenceContext()
 					.getDatabaseSnapshot( id, persister );
 			if ( snapshot == null ) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
index 5c59ea3977..c9a5318899 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
@@ -1,154 +1,149 @@
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
 
-import org.jboss.logging.Logger;
-
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
-import org.hibernate.engine.spi.CachedNaturalIdValueSource;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.spi.ResolveNaturalIdEvent;
 import org.hibernate.event.spi.ResolveNaturalIdEventListener;
+import org.hibernate.internal.CoreLogging;
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
 
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
-			CoreMessageLogger.class,
-			DefaultResolveNaturalIdEventListener.class.getName()
-	);
+	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( DefaultResolveNaturalIdEventListener.class );
 
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
 		return event.getSession().getPersistenceContext().getNaturalIdHelper().findCachedNaturalIdResolution(
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
 			event.getSession().getPersistenceContext().getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(
 					event.getEntityPersister(),
 					pk,
 					event.getOrderedNaturalIdValues()
 			);
 		}
 		
 		return pk;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/exception/internal/SQLExceptionTypeDelegate.java b/hibernate-core/src/main/java/org/hibernate/exception/internal/SQLExceptionTypeDelegate.java
index 07a7ef1569..b375164429 100644
--- a/hibernate-core/src/main/java/org/hibernate/exception/internal/SQLExceptionTypeDelegate.java
+++ b/hibernate-core/src/main/java/org/hibernate/exception/internal/SQLExceptionTypeDelegate.java
@@ -1,99 +1,97 @@
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
 package org.hibernate.exception.internal;
 
 import java.sql.DataTruncation;
 import java.sql.SQLClientInfoException;
 import java.sql.SQLDataException;
 import java.sql.SQLException;
 import java.sql.SQLIntegrityConstraintViolationException;
 import java.sql.SQLInvalidAuthorizationSpecException;
 import java.sql.SQLNonTransientConnectionException;
 import java.sql.SQLSyntaxErrorException;
 import java.sql.SQLTimeoutException;
 import java.sql.SQLTransactionRollbackException;
 import java.sql.SQLTransientConnectionException;
 
 import org.hibernate.JDBCException;
 import org.hibernate.QueryTimeoutException;
 import org.hibernate.exception.ConstraintViolationException;
 import org.hibernate.exception.DataException;
 import org.hibernate.exception.JDBCConnectionException;
 import org.hibernate.exception.LockAcquisitionException;
 import org.hibernate.exception.SQLGrammarException;
 import org.hibernate.exception.spi.AbstractSQLExceptionConversionDelegate;
 import org.hibernate.exception.spi.ConversionContext;
-import org.hibernate.exception.spi.SQLExceptionConverter;
 
 /**
- * {@link SQLExceptionConverter} implementation that does conversion based on the
+ * {@link org.hibernate.exception.spi.SQLExceptionConverter} implementation that does conversion based on the
  * JDBC 4 defined {@link SQLException} sub-type hierarchy.
  *
  * @author Steve Ebersole
  */
 public class SQLExceptionTypeDelegate extends AbstractSQLExceptionConversionDelegate {
 	public SQLExceptionTypeDelegate(ConversionContext conversionContext) {
 		super( conversionContext );
 	}
 
-
 	@Override
 	public JDBCException convert(SQLException sqlException, String message, String sql) {
 		if ( SQLClientInfoException.class.isInstance( sqlException )
 				|| SQLInvalidAuthorizationSpecException.class.isInstance( sqlException )
 				|| SQLNonTransientConnectionException.class.isInstance( sqlException )
 				|| SQLTransientConnectionException.class.isInstance( sqlException ) ) {
 			return new JDBCConnectionException( message, sqlException, sql );
 		}
 		else if ( DataTruncation.class.isInstance( sqlException ) ||
 				SQLDataException.class.isInstance( sqlException ) ) {
 			throw new DataException( message, sqlException, sql );
 		}
 		else if ( SQLIntegrityConstraintViolationException.class.isInstance( sqlException ) ) {
 			return new ConstraintViolationException(
 					message,
 					sqlException,
 					sql,
 					getConversionContext().getViolatedConstraintNameExtracter().extractConstraintName( sqlException )
 			);
 		}
 		else if ( SQLSyntaxErrorException.class.isInstance( sqlException ) ) {
 			return new SQLGrammarException( message, sqlException, sql );
 		}
 		else if ( SQLTimeoutException.class.isInstance( sqlException ) ) {
 			return new QueryTimeoutException( message, sqlException, sql );
 		}
 		else if ( SQLTransactionRollbackException.class.isInstance( sqlException ) ) {
 			// Not 100% sure this is completely accurate.  The JavaDocs for SQLTransactionRollbackException state that
 			// it indicates sql states starting with '40' and that those usually indicate that:
 			//		<quote>
 			//			the current statement was automatically rolled back by the database because of deadlock or
 			// 			other transaction serialization failures.
 			//		</quote>
 			return new LockAcquisitionException( message, sqlException, sql );
 		}
 
 		return null; // allow other delegates the chance to look
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/AbstractMapComponentNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/AbstractMapComponentNode.java
index a382f5c7d7..f664920f4c 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/AbstractMapComponentNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/AbstractMapComponentNode.java
@@ -1,128 +1,127 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2009, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.hql.internal.ast.tree;
 
 import java.util.Map;
 
 import antlr.SemanticException;
 import antlr.collections.AST;
 
-import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.ast.util.ColumnHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 /**
  * Basic support for KEY, VALUE and ENTRY based "qualified identification variables".
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractMapComponentNode extends FromReferenceNode implements HqlSqlTokenTypes {
 	private String[] columns;
 
 	public FromReferenceNode getMapReference() {
 		return ( FromReferenceNode ) getFirstChild();
 	}
 
 	public String[] getColumns() {
 		return columns;
 	}
 
 	@Override
 	public void setScalarColumnText(int i) throws SemanticException {
 		ColumnHelper.generateScalarColumns( this, getColumns(), i );
 	}
 
 	@Override
 	public void resolve(
 			boolean generateJoin,
 			boolean implicitJoin,
 			String classAlias,
 			AST parent) throws SemanticException {
 		if ( parent != null ) {
 			throw attemptedDereference();
 		}
 
 		FromReferenceNode mapReference = getMapReference();
 		mapReference.resolve( true, true );
 
 		FromElement sourceFromElement = null;
 		if ( isAliasRef( mapReference ) ) {
 			QueryableCollection collectionPersister = mapReference.getFromElement().getQueryableCollection();
 			if ( Map.class.isAssignableFrom( collectionPersister.getCollectionType().getReturnedClass() ) ) {
 				sourceFromElement = mapReference.getFromElement();
 			}
 		}
 		else {
 			if ( mapReference.getDataType().isCollectionType() ) {
 				CollectionType collectionType = (CollectionType) mapReference.getDataType();
 				if ( Map.class.isAssignableFrom( collectionType.getReturnedClass() ) ) {
 					sourceFromElement = mapReference.getFromElement();
 				}
 			}
 		}
 
 		if ( sourceFromElement == null ) {
 			throw nonMap();
 		}
 
 		setFromElement( sourceFromElement );
 		setDataType( resolveType( sourceFromElement.getQueryableCollection() ) );
 		this.columns = resolveColumns( sourceFromElement.getQueryableCollection() );
 		initText( this.columns );
 		setFirstChild( null );
 	}
 
 	private boolean isAliasRef(FromReferenceNode mapReference) {
 		return ALIAS_REF == mapReference.getType();
 	}
 
 	private void initText(String[] columns) {
 		String text = StringHelper.join( ", ", columns );
 		if ( columns.length > 1 && getWalker().isComparativeExpressionClause() ) {
 			text = "(" + text + ")";
 		}
 		setText( text );
 	}
 
 	protected abstract String expressionDescription();
 	protected abstract String[] resolveColumns(QueryableCollection collectionPersister);
 	protected abstract Type resolveType(QueryableCollection collectionPersister);
 
 	protected SemanticException attemptedDereference() {
 		return new SemanticException( expressionDescription() + " expression cannot be further de-referenced" );
 	}
 
 	protected SemanticException nonMap() {
 		return new SemanticException( expressionDescription() + " expression did not reference map property" );
 	}
 
 	@Override
 	public void resolveIndex(AST parent) throws SemanticException {
 		throw new UnsupportedOperationException( expressionDescription() + " expression cannot be the source for an index operation" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
index 2ccebe1dc4..12fd68d220 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
@@ -1,727 +1,725 @@
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
 package org.hibernate.hql.internal.ast.tree;
 
 import antlr.SemanticException;
 import antlr.collections.AST;
 
-import org.jboss.logging.Logger;
-
 import org.hibernate.QueryException;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.hql.internal.CollectionProperties;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.util.ASTUtil;
 import org.hibernate.hql.internal.ast.util.ColumnHelper;
+import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Represents a reference to a property or alias expression.  This should duplicate the relevant behaviors in
  * PathExpressionParser.
  *
  * @author Joshua Davis
  */
 public class DotNode extends FromReferenceNode implements DisplayableNode, SelectExpression {
+	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( DotNode.class );
 
 	///////////////////////////////////////////////////////////////////////////
 	// USED ONLY FOR REGRESSION TESTING!!!!
 	//
 	// todo : obviously get rid of all this junk ;)
 	///////////////////////////////////////////////////////////////////////////
 	public static boolean useThetaStyleImplicitJoins;
 	public static boolean REGRESSION_STYLE_JOIN_SUPPRESSION;
 	public static interface IllegalCollectionDereferenceExceptionBuilder {
 		public QueryException buildIllegalCollectionDereferenceException(String collectionPropertyName, FromReferenceNode lhs);
 	}
 	public static final IllegalCollectionDereferenceExceptionBuilder DEF_ILLEGAL_COLL_DEREF_EXCP_BUILDER = new IllegalCollectionDereferenceExceptionBuilder() {
 		public QueryException buildIllegalCollectionDereferenceException(String propertyName, FromReferenceNode lhs) {
 			String lhsPath = ASTUtil.getPathText( lhs );
 			return new QueryException( "illegal attempt to dereference collection [" + lhsPath + "] with element property reference [" + propertyName + "]" );
 		}
 	};
 	public static IllegalCollectionDereferenceExceptionBuilder ILLEGAL_COLL_DEREF_EXCP_BUILDER = DEF_ILLEGAL_COLL_DEREF_EXCP_BUILDER;
 	///////////////////////////////////////////////////////////////////////////
 
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, DotNode.class.getName());
-
 	public static enum DereferenceType {
 		UNKNOWN,
 		ENTITY,
 		COMPONENT,
 		COLLECTION,
 		PRIMITIVE,
 		IDENTIFIER,
 		JAVA_CONSTANT
 	}
 
 	/**
 	 * The identifier that is the name of the property.
 	 */
 	private String propertyName;
 	/**
 	 * The full path, to the root alias of this dot node.
 	 */
 	private String path;
 	/**
 	 * The unresolved property path relative to this dot node.
 	 */
 	private String propertyPath;
 
 	/**
 	 * The column names that this resolves to.
 	 */
 	private String[] columns;
 
 	/**
 	 * The type of join to create.   Default is an inner join.
 	 */
 	private JoinType joinType = JoinType.INNER_JOIN;
 
 	/**
 	 * Fetch join or not.
 	 */
 	private boolean fetch = false;
 
 	/**
 	 * The type of dereference that hapened
 	 */
 	private DereferenceType dereferenceType = DereferenceType.UNKNOWN;
 
 	private FromElement impliedJoin;
 
 	/**
 	 * Sets the join type for this '.' node structure.
 	 *
 	 * @param joinType The type of join to use.
 	 * @see JoinFragment
 	 */
 	public void setJoinType(JoinType joinType) {
 		this.joinType = joinType;
 	}
 
 	private String[] getColumns() throws QueryException {
 		if ( columns == null ) {
 			// Use the table fromElement and the property name to get the array of column names.
 			String tableAlias = getLhs().getFromElement().getTableAlias();
 			columns = getFromElement().toColumns( tableAlias, propertyPath, false );
 		}
 		return columns;
 	}
 
 	@Override
     public String getDisplayText() {
 		StringBuilder buf = new StringBuilder();
 		FromElement fromElement = getFromElement();
 		buf.append( "{propertyName=" ).append( propertyName );
 		buf.append( ",dereferenceType=" ).append( dereferenceType.name() );
 		buf.append( ",getPropertyPath=" ).append( propertyPath );
 		buf.append( ",path=" ).append( getPath() );
 		if ( fromElement != null ) {
 			buf.append( ",tableAlias=" ).append( fromElement.getTableAlias() );
 			buf.append( ",className=" ).append( fromElement.getClassName() );
 			buf.append( ",classAlias=" ).append( fromElement.getClassAlias() );
 		}
 		else {
 			buf.append( ",no from element" );
 		}
 		buf.append( '}' );
 		return buf.toString();
 	}
 
 	/**
 	 * Resolves the left hand side of the DOT.
 	 *
 	 * @throws SemanticException
 	 */
 	@Override
     public void resolveFirstChild() throws SemanticException {
 		FromReferenceNode lhs = ( FromReferenceNode ) getFirstChild();
 		SqlNode property = ( SqlNode ) lhs.getNextSibling();
 
 		// Set the attributes of the property reference expression.
 		String propName = property.getText();
 		propertyName = propName;
 		// If the uresolved property path isn't set yet, just use the property name.
 		if ( propertyPath == null ) {
 			propertyPath = propName;
 		}
 		// Resolve the LHS fully, generate implicit joins.  Pass in the property name so that the resolver can
 		// discover foreign key (id) properties.
 		lhs.resolve( true, true, null, this );
 		setFromElement( lhs.getFromElement() );			// The 'from element' that the property is in.
 
 		checkSubclassOrSuperclassPropertyReference( lhs, propName );
 	}
 
 	@Override
     public void resolveInFunctionCall(boolean generateJoin, boolean implicitJoin) throws SemanticException {
 		if ( isResolved() ) {
 			return;
 		}
 		Type propertyType = prepareLhs();			// Prepare the left hand side and get the data type.
 		if ( propertyType!=null && propertyType.isCollectionType() ) {
 			resolveIndex(null);
 		}
 		else {
 			resolveFirstChild();
 			super.resolve(generateJoin, implicitJoin);
 		}
 	}
 
 
 	public void resolveIndex(AST parent) throws SemanticException {
 		if ( isResolved() ) {
 			return;
 		}
 		Type propertyType = prepareLhs();			// Prepare the left hand side and get the data type.
 		dereferenceCollection( ( CollectionType ) propertyType, true, true, null, parent );
 	}
 
 	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent)
 	throws SemanticException {
 		// If this dot has already been resolved, stop now.
 		if ( isResolved() ) {
 			return;
 		}
 		Type propertyType = prepareLhs(); // Prepare the left hand side and get the data type.
 
 		// If there is no data type for this node, and we're at the end of the path (top most dot node), then
 		// this might be a Java constant.
 		if ( propertyType == null ) {
 			if ( parent == null ) {
 				getWalker().getLiteralProcessor().lookupConstant( this );
 			}
 			// If the propertyType is null and there isn't a parent, just
 			// stop now... there was a problem resolving the node anyway.
 			return;
 		}
 
 		if ( propertyType.isComponentType() ) {
 			// The property is a component...
 			checkLhsIsNotCollection();
 			dereferenceComponent( parent );
 			initText();
 		}
 		else if ( propertyType.isEntityType() ) {
 			// The property is another class..
 			checkLhsIsNotCollection();
 			dereferenceEntity( ( EntityType ) propertyType, implicitJoin, classAlias, generateJoin, parent );
 			initText();
 		}
 		else if ( propertyType.isCollectionType() ) {
 			// The property is a collection...
 			checkLhsIsNotCollection();
 			dereferenceCollection( ( CollectionType ) propertyType, implicitJoin, false, classAlias, parent );
 		}
 		else {
 			// Otherwise, this is a primitive type.
 			if ( ! CollectionProperties.isAnyCollectionProperty( propertyName ) ) {
 				checkLhsIsNotCollection();
 			}
 			dereferenceType = DereferenceType.PRIMITIVE;
 			initText();
 		}
 		setResolved();
 	}
 
 	private void initText() {
 		String[] cols = getColumns();
 		String text = StringHelper.join( ", ", cols );
 		if ( cols.length > 1 && getWalker().isComparativeExpressionClause() ) {
 			text = "(" + text + ")";
 		}
 		setText( text );
 	}
 
 	private Type prepareLhs() throws SemanticException {
 		FromReferenceNode lhs = getLhs();
 		lhs.prepareForDot( propertyName );
 		return getDataType();
 	}
 
 	private void dereferenceCollection(CollectionType collectionType, boolean implicitJoin, boolean indexed, String classAlias, AST parent)
 	throws SemanticException {
 
 		dereferenceType = DereferenceType.COLLECTION;
 		String role = collectionType.getRole();
 
 		//foo.bars.size (also handles deprecated stuff like foo.bars.maxelement for backwardness)
 		boolean isSizeProperty = getNextSibling()!=null &&
 			CollectionProperties.isAnyCollectionProperty( getNextSibling().getText() );
 
 		if ( isSizeProperty ) indexed = true; //yuck!
 
 		QueryableCollection queryableCollection = getSessionFactoryHelper().requireQueryableCollection( role );
 		String propName = getPath();
 		FromClause currentFromClause = getWalker().getCurrentFromClause();
 
 		// determine whether we should use the table name or table alias to qualify the column names...
 		// we need to use the table-name when:
 		//		1) the top-level statement is not a SELECT
 		//		2) the LHS FromElement is *the* FromElement from the top-level statement
 		//
 		// there is a caveat here.. if the update/delete statement are "multi-table" we should continue to use
 		// the alias also, even if the FromElement is the root one...
 		//
 		// in all other cases, we should use the table alias
 		final FromElement lhsFromElement = getLhs().getFromElement();
 		if ( getWalker().getStatementType() != SqlTokenTypes.SELECT ) {
 			if ( isFromElementUpdateOrDeleteRoot( lhsFromElement ) ) {
 				// at this point we know we have the 2 conditions above,
 				// lets see if we have the mentioned "multi-table" caveat...
 				boolean useAlias = false;
 				if ( getWalker().getStatementType() != SqlTokenTypes.INSERT ) {
 					final Queryable persister = lhsFromElement.getQueryable();
 					if ( persister.isMultiTable() ) {
 						useAlias = true;
 					}
 				}
 				if ( ! useAlias ) {
 					final String lhsTableName = lhsFromElement.getQueryable().getTableName();
 					columns = getFromElement().toColumns( lhsTableName, propertyPath, false, true );
 				}
 			}
 		}
 
 		// We do not look for an existing join on the same path, because
 		// it makes sense to join twice on the same collection role
 		FromElementFactory factory = new FromElementFactory(
 		        currentFromClause,
 		        getLhs().getFromElement(),
 		        propName,
 				classAlias,
 		        getColumns(),
 		        implicitJoin
 		);
 		FromElement elem = factory.createCollection( queryableCollection, role, joinType, fetch, indexed );
 
 		LOG.debugf( "dereferenceCollection() : Created new FROM element for %s : %s", propName, elem );
 
 		setImpliedJoin( elem );
 		setFromElement( elem );	// This 'dot' expression now refers to the resulting from element.
 
 		if ( isSizeProperty ) {
 			elem.setText("");
 			elem.setUseWhereFragment(false);
 		}
 
 		if ( !implicitJoin ) {
 			EntityPersister entityPersister = elem.getEntityPersister();
 			if ( entityPersister != null ) {
 				getWalker().addQuerySpaces( entityPersister.getQuerySpaces() );
 			}
 		}
 		getWalker().addQuerySpaces( queryableCollection.getCollectionSpaces() );	// Always add the collection's query spaces.
 	}
 
 	private void dereferenceEntity(EntityType entityType, boolean implicitJoin, String classAlias, boolean generateJoin, AST parent) throws SemanticException {
 		checkForCorrelatedSubquery( "dereferenceEntity" );
 		// three general cases we check here as to whether to render a physical SQL join:
 		// 1) is our parent a DotNode as well?  If so, our property reference is
 		// 		being further de-referenced...
 		// 2) is this a DML statement
 		// 3) we were asked to generate any needed joins (generateJoins==true) *OR*
 		//		we are currently processing a select or from clause
 		// (an additional check is the REGRESSION_STYLE_JOIN_SUPPRESSION check solely intended for the test suite)
 		//
 		// The REGRESSION_STYLE_JOIN_SUPPRESSION is an additional check
 		// intended solely for use within the test suite.  This forces the
 		// implicit join resolution to behave more like the classic parser.
 		// The underlying issue is that classic translator is simply wrong
 		// about its decisions on whether or not to render an implicit join
 		// into a physical SQL join in a lot of cases.  The piece it generally
 		// tends to miss is that INNER joins effect the results by further
 		// restricting the data set!  A particular manifestation of this is
 		// the fact that the classic translator will skip the physical join
 		// for ToOne implicit joins *if the query is shallow*; the result
 		// being that Query.list() and Query.iterate() could return
 		// different number of results!
 		DotNode parentAsDotNode = null;
 		String property = propertyName;
 		final boolean joinIsNeeded;
 
 		if ( isDotNode( parent ) ) {
 			// our parent is another dot node, meaning we are being further dereferenced.
 			// thus we need to generate a join unless the parent refers to the associated
 			// entity's PK (because 'our' table would know the FK).
 			parentAsDotNode = ( DotNode ) parent;
 			property = parentAsDotNode.propertyName;
 			joinIsNeeded = generateJoin && !isReferenceToPrimaryKey( parentAsDotNode.propertyName, entityType );
 		}
 		else if ( ! getWalker().isSelectStatement() ) {
 			// in non-select queries, the only time we should need to join is if we are in a subquery from clause
 			joinIsNeeded = getWalker().getCurrentStatementType() == SqlTokenTypes.SELECT && getWalker().isInFrom();
 		}
 		else if ( REGRESSION_STYLE_JOIN_SUPPRESSION ) {
 			// this is the regression style determination which matches the logic of the classic translator
 			joinIsNeeded = generateJoin && ( !getWalker().isInSelect() || !getWalker().isShallowQuery() );
 		}
 		else {
 			joinIsNeeded = generateJoin || ( getWalker().isInSelect() || getWalker().isInFrom() );
 		}
 
 		if ( joinIsNeeded ) {
 			dereferenceEntityJoin( classAlias, entityType, implicitJoin, parent );
 		}
 		else {
 			dereferenceEntityIdentifier( property, parentAsDotNode );
 		}
 
 	}
 
 	private boolean isDotNode(AST n) {
 		return n != null && n.getType() == SqlTokenTypes.DOT;
 	}
 
 	private void dereferenceEntityJoin(String classAlias, EntityType propertyType, boolean impliedJoin, AST parent)
 	throws SemanticException {
 		dereferenceType = DereferenceType.ENTITY;
         if ( LOG.isDebugEnabled() ) {
 			LOG.debugf(
 					"dereferenceEntityJoin() : generating join for %s in %s (%s) parent = %s",
 					propertyName,
 					getFromElement().getClassName(),
 					classAlias == null ? "<no alias>" : classAlias,
 					ASTUtil.getDebugString(parent)
 			);
 		}
 		// Create a new FROM node for the referenced class.
 		String associatedEntityName = propertyType.getAssociatedEntityName();
 		String tableAlias = getAliasGenerator().createName( associatedEntityName );
 
 		String[] joinColumns = getColumns();
 		String joinPath = getPath();
 
 		if ( impliedJoin && getWalker().isInFrom() ) {
 			joinType = getWalker().getImpliedJoinType();
 		}
 
 		FromClause currentFromClause = getWalker().getCurrentFromClause();
 		FromElement elem = currentFromClause.findJoinByPath( joinPath );
 
 ///////////////////////////////////////////////////////////////////////////////
 //
 // This is the piece which recognizes the condition where an implicit join path
 // resolved earlier in a correlated subquery is now being referenced in the
 // outer query.  For 3.0final, we just let this generate a second join (which
 // is exactly how the old parser handles this).  Eventually we need to add this
 // logic back in and complete the logic in FromClause.promoteJoin; however,
 // FromClause.promoteJoin has its own difficulties (see the comments in
 // FromClause.promoteJoin).
 //
 //		if ( elem == null ) {
 //			// see if this joinPath has been used in a "child" FromClause, and if so
 //			// promote that element to the outer query
 //			FromClause currentNodeOwner = getFromElement().getFromClause();
 //			FromClause currentJoinOwner = currentNodeOwner.locateChildFromClauseWithJoinByPath( joinPath );
 //			if ( currentJoinOwner != null && currentNodeOwner != currentJoinOwner ) {
 //				elem = currentJoinOwner.findJoinByPathLocal( joinPath );
 //				if ( elem != null ) {
 //					currentFromClause.promoteJoin( elem );
 //					// EARLY EXIT!!!
 //					return;
 //				}
 //			}
 //		}
 //
 ///////////////////////////////////////////////////////////////////////////////
 
 		boolean found = elem != null;
 		// even though we might find a pre-existing element by join path, for FromElements originating in a from-clause
 		// we should only ever use the found element if the aliases match (null != null here).  Implied joins are
 		// always (?) ok to reuse.
 		boolean useFoundFromElement = found && ( elem.isImplied() || areSame( classAlias, elem.getClassAlias() ) );
 
 		if ( ! useFoundFromElement ) {
 			// If this is an implied join in a from element, then use the impled join type which is part of the
 			// tree parser's state (set by the gramamar actions).
 			JoinSequence joinSequence = getSessionFactoryHelper()
 				.createJoinSequence( impliedJoin, propertyType, tableAlias, joinType, joinColumns );
 
 			// If the lhs of the join is a "component join", we need to go back to the
 			// first non-component-join as the origin to properly link aliases and
 			// join columns
 			FromElement lhsFromElement = getLhs().getFromElement();
 			while ( lhsFromElement != null &&  ComponentJoin.class.isInstance( lhsFromElement ) ) {
 				lhsFromElement = lhsFromElement.getOrigin();
 			}
 			if ( lhsFromElement == null ) {
 				throw new QueryException( "Unable to locate appropriate lhs" );
 			}
 			
 			String role = lhsFromElement.getClassName() + "." + propertyName;
 
 			FromElementFactory factory = new FromElementFactory(
 			        currentFromClause,
 					lhsFromElement,
 					joinPath,
 					classAlias,
 					joinColumns,
 					impliedJoin
 			);
 			elem = factory.createEntityJoin(
 					associatedEntityName,
 					tableAlias,
 					joinSequence,
 					fetch,
 					getWalker().isInFrom(),
 					propertyType,
 					role,
 					joinPath
 			);
 		}
 		else {
 			// NOTE : addDuplicateAlias() already performs nullness checks on the alias.
 			currentFromClause.addDuplicateAlias( classAlias, elem );
 		}
 		setImpliedJoin( elem );
 		getWalker().addQuerySpaces( elem.getEntityPersister().getQuerySpaces() );
 		setFromElement( elem );	// This 'dot' expression now refers to the resulting from element.
 	}
 
 	private boolean areSame(String alias1, String alias2) {
 		// again, null != null here
 		return !StringHelper.isEmpty( alias1 ) && !StringHelper.isEmpty( alias2 ) && alias1.equals( alias2 );
 	}
 
 	private void setImpliedJoin(FromElement elem) {
 		this.impliedJoin = elem;
 		if ( getFirstChild().getType() == SqlTokenTypes.DOT ) {
 			DotNode dotLhs = ( DotNode ) getFirstChild();
 			if ( dotLhs.getImpliedJoin() != null ) {
 				this.impliedJoin = dotLhs.getImpliedJoin();
 			}
 		}
 	}
 
 	@Override
     public FromElement getImpliedJoin() {
 		return impliedJoin;
 	}
 
 	/**
 	 * Is the given property name a reference to the primary key of the associated
 	 * entity construed by the given entity type?
 	 * <p/>
 	 * For example, consider a fragment like order.customer.id
 	 * (where order is a from-element alias).  Here, we'd have:
 	 * propertyName = "id" AND
 	 * owningType = ManyToOneType(Customer)
 	 * and are being asked to determine whether "customer.id" is a reference
 	 * to customer's PK...
 	 *
 	 * @param propertyName The name of the property to check.
 	 * @param owningType The type represeting the entity "owning" the property
 	 * @return True if propertyName references the entity's (owningType->associatedEntity)
 	 * primary key; false otherwise.
 	 */
 	private boolean isReferenceToPrimaryKey(String propertyName, EntityType owningType) {
 		EntityPersister persister = getSessionFactoryHelper()
 				.getFactory()
 				.getEntityPersister( owningType.getAssociatedEntityName() );
 		if ( persister.getEntityMetamodel().hasNonIdentifierPropertyNamedId() ) {
 			// only the identifier property field name can be a reference to the associated entity's PK...
 			return propertyName.equals( persister.getIdentifierPropertyName() ) && owningType.isReferenceToPrimaryKey();
 		}
         // here, we have two possibilities:
         // 1) the property-name matches the explicitly identifier property name
         // 2) the property-name matches the implicit 'id' property name
         // the referenced node text is the special 'id'
         if (EntityPersister.ENTITY_ID.equals(propertyName)) return owningType.isReferenceToPrimaryKey();
         String keyPropertyName = getSessionFactoryHelper().getIdentifierOrUniqueKeyPropertyName(owningType);
         return keyPropertyName != null && keyPropertyName.equals(propertyName) && owningType.isReferenceToPrimaryKey();
 	}
 
 	private void checkForCorrelatedSubquery(String methodName) {
 		if ( isCorrelatedSubselect() ) {
 			LOG.debugf( "%s() : correlated subquery", methodName );
 		}
 	}
 
 	private boolean isCorrelatedSubselect() {
 		return getWalker().isSubQuery() &&
 			getFromElement().getFromClause() != getWalker().getCurrentFromClause();
 	}
 
 	private void checkLhsIsNotCollection() throws SemanticException {
 		if ( getLhs().getDataType() != null && getLhs().getDataType().isCollectionType() ) {
 			throw ILLEGAL_COLL_DEREF_EXCP_BUILDER.buildIllegalCollectionDereferenceException( propertyName, getLhs() );
 		}
 	}
 	private void dereferenceComponent(AST parent) {
 		dereferenceType = DereferenceType.COMPONENT;
 		setPropertyNameAndPath( parent );
 	}
 
 	private void dereferenceEntityIdentifier(String propertyName, DotNode dotParent) {
 		// special shortcut for id properties, skip the join!
 		// this must only occur at the _end_ of a path expression
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "dereferenceShortcut() : property %s in %s does not require a join.",
 					propertyName,
 					getFromElement().getClassName() );
 		}
 
 		initText();
 		setPropertyNameAndPath( dotParent ); // Set the unresolved path in this node and the parent.
 		// Set the text for the parent.
 		if ( dotParent != null ) {
 			dotParent.dereferenceType = DereferenceType.IDENTIFIER;
 			dotParent.setText( getText() );
 			dotParent.columns = getColumns();
 		}
 	}
 
 	private void setPropertyNameAndPath(AST parent) {
 		if ( isDotNode( parent ) ) {
 			DotNode dotNode = ( DotNode ) parent;
 			AST lhs = dotNode.getFirstChild();
 			AST rhs = lhs.getNextSibling();
 			propertyName = rhs.getText();
 			propertyPath = propertyPath + "." + propertyName; // Append the new property name onto the unresolved path.
 			dotNode.propertyPath = propertyPath;
 			LOG.debugf( "Unresolved property path is now '%s'", dotNode.propertyPath );
 		}
 		else {
 			LOG.debugf( "Terminal getPropertyPath = [%s]", propertyPath );
 		}
 	}
 
 	@Override
     public Type getDataType() {
 		if ( super.getDataType() == null ) {
 			FromElement fromElement = getLhs().getFromElement();
 			if ( fromElement == null ) return null;
 			// If the lhs is a collection, use CollectionPropertyMapping
 			Type propertyType = fromElement.getPropertyType( propertyName, propertyPath );
 			LOG.debugf( "getDataType() : %s -> %s", propertyPath, propertyType );
 			super.setDataType( propertyType );
 		}
 		return super.getDataType();
 	}
 
 	public void setPropertyPath(String propertyPath) {
 		this.propertyPath = propertyPath;
 	}
 
 	public String getPropertyPath() {
 		return propertyPath;
 	}
 
 	public FromReferenceNode getLhs() {
 		FromReferenceNode lhs = ( ( FromReferenceNode ) getFirstChild() );
 		if ( lhs == null ) {
 			throw new IllegalStateException( "DOT node with no left-hand-side!" );
 		}
 		return lhs;
 	}
 
 	/**
 	 * Returns the full path of the node.
 	 *
 	 * @return the full path of the node.
 	 */
 	@Override
     public String getPath() {
 		if ( path == null ) {
 			FromReferenceNode lhs = getLhs();
 			if ( lhs == null ) {
 				path = getText();
 			}
 			else {
 				SqlNode rhs = ( SqlNode ) lhs.getNextSibling();
 				path = lhs.getPath() + "." + rhs.getOriginalText();
 			}
 		}
 		return path;
 	}
 
 	public void setFetch(boolean fetch) {
 		this.fetch = fetch;
 	}
 
 	public void setScalarColumnText(int i) throws SemanticException {
 		String[] sqlColumns = getColumns();
 		ColumnHelper.generateScalarColumns( this, sqlColumns, i );
 	}
 
 	/**
 	 * Special method to resolve expressions in the SELECT list.
 	 *
 	 * @throws SemanticException if this cannot be resolved.
 	 */
 	public void resolveSelectExpression() throws SemanticException {
 		if ( getWalker().isShallowQuery() || getWalker().getCurrentFromClause().isSubQuery() ) {
 			resolve(false, true);
 		}
 		else {
 			resolve(true, false);
 			Type type = getDataType();
 			if ( type.isEntityType() ) {
 				FromElement fromElement = getFromElement();
 				fromElement.setIncludeSubclasses( true ); // Tell the destination fromElement to 'includeSubclasses'.
 				if ( useThetaStyleImplicitJoins ) {
 					fromElement.getJoinSequence().setUseThetaStyle( true );	// Use theta style (for regression)
 					// Move the node up, after the origin node.
 					FromElement origin = fromElement.getOrigin();
 					if ( origin != null ) {
 						ASTUtil.makeSiblingOfParent( origin, fromElement );
 					}
 				}
 			}
 		}
 
 		FromReferenceNode lhs = getLhs();
 		while ( lhs != null ) {
 			checkSubclassOrSuperclassPropertyReference( lhs, lhs.getNextSibling().getText() );
 			lhs = ( FromReferenceNode ) lhs.getFirstChild();
 		}
 	}
 
 	public void setResolvedConstant(String text) {
 		path = text;
 		dereferenceType = DereferenceType.JAVA_CONSTANT;
 		setResolved(); // Don't resolve the node again.
 	}
 
 	private boolean checkSubclassOrSuperclassPropertyReference(FromReferenceNode lhs, String propertyName) {
 		if ( lhs != null && !( lhs instanceof IndexNode ) ) {
 			final FromElement source = lhs.getFromElement();
 			if ( source != null ) {
 				source.handlePropertyBeingDereferenced( lhs.getDataType(), propertyName );
 			}
 		}
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IndexNode.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IndexNode.java
index 0c12b17c5e..a40f1080c2 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IndexNode.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/IndexNode.java
@@ -1,217 +1,216 @@
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
 package org.hibernate.hql.internal.ast.tree;
+
 import java.sql.PreparedStatement;
 import java.sql.SQLException;
-import java.util.Iterator;
 import java.util.List;
 
 import antlr.RecognitionException;
 import antlr.SemanticException;
 import antlr.collections.AST;
-import org.jboss.logging.Logger;
 
 import org.hibernate.QueryException;
 import org.hibernate.engine.internal.JoinSequence;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.ast.SqlGenerator;
 import org.hibernate.hql.internal.ast.util.SessionFactoryHelper;
+import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 /**
  * Represents the [] operator and provides it's semantics.
  *
  * @author josh
  */
 public class IndexNode extends FromReferenceNode {
-
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, IndexNode.class.getName() );
+	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( IndexNode.class );
 
 	public void setScalarColumnText(int i) throws SemanticException {
 		throw new UnsupportedOperationException( "An IndexNode cannot generate column text!" );
 	}
 
 	@Override
     public void prepareForDot(String propertyName) throws SemanticException {
 		FromElement fromElement = getFromElement();
 		if ( fromElement == null ) {
 			throw new IllegalStateException( "No FROM element for index operator!" );
 		}
 		QueryableCollection queryableCollection = fromElement.getQueryableCollection();
 		if ( queryableCollection != null && !queryableCollection.isOneToMany() ) {
 
 			FromReferenceNode collectionNode = ( FromReferenceNode ) getFirstChild();
 			String path = collectionNode.getPath() + "[]." + propertyName;
 			LOG.debugf( "Creating join for many-to-many elements for %s", path );
 			FromElementFactory factory = new FromElementFactory( fromElement.getFromClause(), fromElement, path );
 			// This will add the new from element to the origin.
 			FromElement elementJoin = factory.createElementJoin( queryableCollection );
 			setFromElement( elementJoin );
 		}
 	}
 
 	public void resolveIndex(AST parent) throws SemanticException {
 		throw new UnsupportedOperationException();
 	}
 
 	public void resolve(boolean generateJoin, boolean implicitJoin, String classAlias, AST parent)
 	throws SemanticException {
 		if ( isResolved() ) {
 			return;
 		}
 		FromReferenceNode collectionNode = ( FromReferenceNode ) getFirstChild();
 		SessionFactoryHelper sessionFactoryHelper = getSessionFactoryHelper();
 		collectionNode.resolveIndex( this );		// Fully resolve the map reference, create implicit joins.
 
 		Type type = collectionNode.getDataType();
 		if ( !type.isCollectionType() ) {
 			throw new SemanticException( "The [] operator cannot be applied to type " + type.toString() );
 		}
 		String collectionRole = ( ( CollectionType ) type ).getRole();
 		QueryableCollection queryableCollection = sessionFactoryHelper.requireQueryableCollection( collectionRole );
 		if ( !queryableCollection.hasIndex() ) {
 			throw new QueryException( "unindexed fromElement before []: " + collectionNode.getPath() );
 		}
 
 		// Generate the inner join -- The elements need to be joined to the collection they are in.
 		FromElement fromElement = collectionNode.getFromElement();
 		String elementTable = fromElement.getTableAlias();
 		FromClause fromClause = fromElement.getFromClause();
 		String path = collectionNode.getPath();
 
 		FromElement elem = fromClause.findCollectionJoin( path );
 		if ( elem == null ) {
 			FromElementFactory factory = new FromElementFactory( fromClause, fromElement, path );
 			elem = factory.createCollectionElementsJoin( queryableCollection, elementTable );
 			LOG.debugf( "No FROM element found for the elements of collection join path %s, created %s", path, elem );
 		}
 		else {
 			LOG.debugf( "FROM element found for collection join path %s", path );
 		}
 
 		// The 'from element' that represents the elements of the collection.
 		setFromElement( fromElement );
 
 		// Add the condition to the join sequence that qualifies the indexed element.
 		AST selector = collectionNode.getNextSibling();
 		if ( selector == null ) {
 			throw new QueryException( "No index value!" );
 		}
 
 		// Sometimes use the element table alias, sometimes use the... umm... collection table alias (many to many)
 		String collectionTableAlias = elementTable;
 		if ( elem.getCollectionTableAlias() != null ) {
 			collectionTableAlias = elem.getCollectionTableAlias();
 		}
 
 		// TODO: get SQL rendering out of here, create an AST for the join expressions.
 		// Use the SQL generator grammar to generate the SQL text for the index expression.
 		JoinSequence joinSequence = fromElement.getJoinSequence();
 		String[] indexCols = queryableCollection.getIndexColumnNames();
 		if ( indexCols.length != 1 ) {
 			throw new QueryException( "composite-index appears in []: " + collectionNode.getPath() );
 		}
 		SqlGenerator gen = new SqlGenerator( getSessionFactoryHelper().getFactory() );
 		try {
 			gen.simpleExpr( selector ); //TODO: used to be exprNoParens! was this needed?
 		}
 		catch ( RecognitionException e ) {
 			throw new QueryException( e.getMessage(), e );
 		}
 		String selectorExpression = gen.getSQL();
 		joinSequence.addCondition( collectionTableAlias + '.' + indexCols[0] + " = " + selectorExpression );
 		List<ParameterSpecification> paramSpecs = gen.getCollectedParameters();
 		if ( paramSpecs != null ) {
 			switch ( paramSpecs.size() ) {
 				case 0 :
 					// nothing to do
 					break;
 				case 1 :
 					ParameterSpecification paramSpec = paramSpecs.get( 0 );
 					paramSpec.setExpectedType( queryableCollection.getIndexType() );
 					fromElement.setIndexCollectionSelectorParamSpec( paramSpec );
 					break;
 				default:
 					fromElement.setIndexCollectionSelectorParamSpec(
 							new AggregatedIndexCollectionSelectorParameterSpecifications( paramSpecs )
 					);
 					break;
 			}
 		}
 
 		// Now, set the text for this node.  It should be the element columns.
 		String[] elementColumns = queryableCollection.getElementColumnNames( elementTable );
 		setText( elementColumns[0] );
 		setResolved();
 	}
 
 	/**
 	 * In the (rare?) case where the index selector contains multiple parameters...
 	 */
 	private static class AggregatedIndexCollectionSelectorParameterSpecifications implements ParameterSpecification {
 		private final List<ParameterSpecification> paramSpecs;
 
 		public AggregatedIndexCollectionSelectorParameterSpecifications(List<ParameterSpecification> paramSpecs) {
 			this.paramSpecs = paramSpecs;
 		}
 
 		@Override
 		public int bind(PreparedStatement statement, QueryParameters qp, SessionImplementor session, int position)
 		throws SQLException {
 			int bindCount = 0;
 			for ( ParameterSpecification paramSpec : paramSpecs ) {
 				bindCount += paramSpec.bind( statement, qp, session, position + bindCount );
 			}
 			return bindCount;
 		}
 
 		@Override
 		public Type getExpectedType() {
 			return null;
 		}
 
 		@Override
 		public void setExpectedType(Type expectedType) {
 		}
 
 		@Override
 		public String renderDisplayInfo() {
 			return "index-selector [" + collectDisplayInfo() + "]" ;
 		}
 
 		private String collectDisplayInfo() {
 			StringBuilder buffer = new StringBuilder();
 			for ( ParameterSpecification paramSpec : paramSpecs ) {
 				buffer.append( ( paramSpec ).renderDisplayInfo() );
 			}
 			return buffer.toString();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/NodeTraverser.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/NodeTraverser.java
index 689ee535f4..cc195603bc 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/NodeTraverser.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/NodeTraverser.java
@@ -1,90 +1,89 @@
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
 package org.hibernate.hql.internal.ast.util;
+
 import java.util.ArrayDeque;
 import java.util.Deque;
-import java.util.Stack;
 
 import antlr.collections.AST;
 
 /**
  * A visitor for traversing an AST tree.
  * 
  * @author Steve Ebersole
  * @author Philip R. "Pib" Burns.
  * @author Strong Liu
  * 
  */
-
 public class NodeTraverser {
 	public static interface VisitationStrategy {
 		public void visit( AST node );
 	}
 
 	private final VisitationStrategy strategy;
 
 	public NodeTraverser( VisitationStrategy strategy ) {
 		this.strategy = strategy;
 	}
 
 	/**
 	 * Traverse the AST tree depth first.
 	 * 
 	 * @param ast
 	 *            Root node of subtree to traverse.
 	 * 
 	 *            <p>
 	 *            Note that the AST passed in is not visited itself. Visitation
 	 *            starts with its children.
 	 *            </p>
 	 */
 	public void traverseDepthFirst( AST ast ) {
 		if ( ast == null ) {
 			throw new IllegalArgumentException(
 					"node to traverse cannot be null!" );
 		}
 		visitDepthFirst( ast.getFirstChild() );
 	}
 
 	private void visitDepthFirst(AST ast) {
 		if ( ast == null ) {
 			return;
 		}
 		Deque<AST> stack = new ArrayDeque<AST>();
 		stack.addLast( ast );
 		while ( !stack.isEmpty() ) {
 			ast = stack.removeLast();
 			strategy.visit( ast );
 			if ( ast.getNextSibling() != null ) {
 				stack.addLast( ast.getNextSibling() );
 			}
 			if ( ast.getFirstChild() != null ) {
 				stack.addLast( ast.getFirstChild() );
 			}
 		}
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
index 3e0814bad2..69acd2e7f5 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
@@ -1,1044 +1,1042 @@
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
 import java.util.Map;
 import java.util.Set;
 
-import org.jboss.logging.Logger;
-
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
 import org.hibernate.engine.spi.SessionImplementor;
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
 			ParserHelper.parse( new PreprocessingParser( tokenReplacements ),
 					queryString,
 					ParserHelper.HQL_SEPARATORS,
 					this );
 			renderSQL();
 		}
 		catch ( QueryException qe ) {
 			if ( qe.getQueryString() == null ) {
 				throw qe.wrapWithQueryString( queryString );
 			}
 			else {
 				throw qe;
 			}
 		}
 		catch ( MappingException me ) {
 			throw me;
 		}
 		catch ( Exception e ) {
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
 		return ArrayHelper.toList( new String[] { sqlString } );
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
 		String name = ( String ) aliasNames.get( alias );
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
         if (name != null) return name + path.substring(alias.length());
         return path;
 	}
 
 	void addEntityToFetch(String name, String oneToOneOwnerName, AssociationType ownerAssociationType) {
 		addEntityToFetch( name );
 		if ( oneToOneOwnerName != null ) oneToOneOwnerNames.put( name, oneToOneOwnerName );
 		if ( ownerAssociationType != null ) uniqueKeyOwnerReferences.put( name, ownerAssociationType );
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
 		String type = ( String ) typeMap.get( name );
 		if ( type == null && superQuery != null ) {
 			type = superQuery.getType( name );
 		}
 		return type;
 	}
 
 	private String getRole(String name) {
 		String role = ( String ) collections.get( name );
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
 		if ( decorator != null ) return decorator;
 
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
 			if ( persister == null ) throw new QueryException( "persistent class not found: " + type );
 			return persister;
 		}
 	}
 
 	private PropertyMapping getDecoratedPropertyMapping(String name) {
 		return ( PropertyMapping ) decoratedPropertyMappings.get( name );
 	}
 
 	void decoratePropertyMapping(String name, PropertyMapping mapping) {
 		decoratedPropertyMappings.put( name, mapping );
 	}
 
 	private Queryable getEntityPersisterForName(String name) throws QueryException {
 		String type = getType( name );
 		Queryable persister = getEntityPersister( type );
 		if ( persister == null ) throw new QueryException( "persistent class not found: " + type );
 		return persister;
 	}
 
 	Queryable getEntityPersisterUsingImports(String className) {
 		final String importedClassName = getFactory().getImportedClassName( className );
 		if ( importedClassName == null ) {
 			return null;
 		}
 		try {
 			return ( Queryable ) getFactory().getEntityPersister( importedClassName );
 		}
 		catch ( MappingException me ) {
 			return null;
 		}
 	}
 
 	Queryable getEntityPersister(String entityName) throws QueryException {
 		try {
 			return ( Queryable ) getFactory().getEntityPersister( entityName );
 		}
 		catch ( Exception e ) {
 			throw new QueryException( "persistent class not found: " + entityName );
 		}
 	}
 
 	QueryableCollection getCollectionPersister(String role) throws QueryException {
 		try {
 			return ( QueryableCollection ) getFactory().getCollectionPersister( role );
 		}
 		catch ( ClassCastException cce ) {
 			throw new QueryException( "collection role is not queryable: " + role );
 		}
 		catch ( Exception e ) {
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
 		if ( !joins.containsKey( name ) ) joins.put( name, joinSequence );
 	}
 
 	void addNamedParameter(String name) {
 		if ( superQuery != null ) superQuery.addNamedParameter( name );
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
 			( ( ArrayList ) o ).add( loc );
 		}
 	}
 
 	@Override
     public int[] getNamedParameterLocs(String name) throws QueryException {
 		Object o = namedParameters.get( name );
 		if ( o == null ) {
 			throw new QueryException( ERROR_NAMED_PARAMETER_DOES_NOT_APPEAR + name, queryString );
 		}
 		if ( o instanceof Integer ) return new int[] { (Integer) o };
 		else {
 			return ArrayHelper.toIntArray( ( ArrayList ) o );
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
 			String name = ( String ) returnedTypes.get( i );
 			//if ( !isName(name) ) throw new QueryException("unknown type: " + name);
 			persisters[i] = getEntityPersisterForName( name );
 			// TODO: cannot use generateSuffixes() - it handles the initial suffix differently.
 			suffixes[i] = ( size == 1 ) ? "" : Integer.toString( i ) + '_';
 			names[i] = name;
 			includeInSelect[i] = !entitiesToFetch.contains( name );
 			if ( includeInSelect[i] ) selectLength++;
 			if ( name.equals( collectionOwnerName ) ) collectionOwnerColumn = i;
 			String oneToOneOwner = ( String ) oneToOneOwnerNames.get( name );
 			owners[i] = ( oneToOneOwner == null ) ? -1 : returnedTypes.indexOf( oneToOneOwner );
 			ownerAssociationTypes[i] = (EntityType) uniqueKeyOwnerReferences.get( name );
 		}
 
 		if ( ArrayHelper.isAllNegative( owners ) ) owners = null;
 
 		String scalarSelect = renderScalarSelect(); //Must be done here because of side-effect! yuck...
 
 		int scalarSize = scalarTypes.size();
 		hasScalars = scalarTypes.size() != rtsize;
 
 		returnTypes = new Type[scalarSize];
 		for ( int i = 0; i < scalarSize; i++ ) {
 			returnTypes[i] = ( Type ) scalarTypes.get( i );
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
 
 		if ( hasScalars || shallowQuery ) sql.addSelectFragmentString( scalarSelect );
 
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
 			CollectionPersister p = getCollectionPersister( ( String ) iter.next() );
 			addQuerySpaces( p.getCollectionSpaces() );
 		}
 		iter = typeMap.keySet().iterator();
 		while ( iter.hasNext() ) {
 			Queryable p = getEntityPersisterForName( ( String ) iter.next() );
 			addQuerySpaces( p.getQuerySpaces() );
 		}
 
 		sqlString = sql.toQueryString();
 
 		if ( holderClass != null ) holderConstructor = ReflectHelper.getConstructor( holderClass, returnTypes );
 
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
 			String name = ( String ) returnedTypes.get( k );
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
 			String name = ( String ) returnedTypes.get( k );
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
 						getFactory().getTypeResolver().getTypeFactory().manyToOne( persisters[k].getEntityName(), shallowQuery )
 				);
 
 				String[] idColumnNames = persisters[k].getIdentifierColumnNames();
 				for ( int i = 0; i < idColumnNames.length; i++ ) {
 					buf.append( returnedTypes.get( k ) ).append( '.' ).append( idColumnNames[i] );
 					if ( !isSubselect ) buf.append( " as " ).append( NameGenerator.scalarName( k, i ) );
 					if ( i != idColumnNames.length - 1 || k != size - 1 ) buf.append( ", " );
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
 					String token = ( String ) next;
 
 					if ( "(".equals( token ) ) {
 						parenCount++;
 					}
 					else if ( ")".equals( token ) ) {
 						parenCount--;
 					}
 
 					String lc = token.toLowerCase();
 					if ( lc.equals( ", " ) ) {
 						if ( nolast ) {
 							nolast = false;
 						}
 						else {
 							if ( !isSubselect && parenCount == 0 ) {
 								int x = c++;
 								buf.append( " as " )
 										.append( NameGenerator.scalarName( x, 0 ) );
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
 					String[] tokens = ( String[] ) next;
 					for ( int i = 0; i < tokens.length; i++ ) {
 						buf.append( tokens[i] );
 						if ( !isSubselect ) {
 							buf.append( " as " )
 									.append( NameGenerator.scalarName( c, i ) );
 						}
 						if ( i != tokens.length - 1 ) buf.append( ", " );
 					}
 					c++;
 				}
 			}
 			if ( !isSubselect && !nolast ) {
 				int x = c++;
 				buf.append( " as " )
 						.append( NameGenerator.scalarName( x, 0 ) );
 			}
 
 		}
 
 		return buf.toString();
 	}
 
 	private void mergeJoins(JoinFragment ojf) throws MappingException, QueryException {
 
 		Iterator iter = joins.entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = ( Map.Entry ) iter.next();
 			String name = ( String ) me.getKey();
 			JoinSequence join = ( JoinSequence ) me.getValue();
 			join.setSelector( new JoinSequence.Selector() {
 				public boolean includeSubclasses(String alias) {
 					boolean include = returnedTypes.contains( alias ) && !isShallowQuery();
 					return include;
 				}
 			} );
 
 			if ( typeMap.containsKey( name ) ) {
 				ojf.addFragment( join.toJoinFragment( enabledFilters, true ) );
 			}
 			else if ( collections.containsKey( name ) ) {
 				ojf.addFragment( join.toJoinFragment( enabledFilters, true ) );
 			}
 			else {
 				//name from a super query (a bit inelegant that it shows up here)
 			}
 
 		}
 
 	}
 
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
 		if ( superQuery != null ) superQuery.addQuerySpaces( spaces );
 	}
 
 	void setDistinct(boolean distinct) {
 		this.distinct = distinct;
 	}
 
 	boolean isSubquery() {
 		return superQuery != null;
 	}
 
 	/**
 	 * Overrides method from Loader
 	 */
 	@Override
     public CollectionPersister[] getCollectionPersisters() {
 		return collectionPersister == null ? null : new CollectionPersister[] { collectionPersister };
 	}
 
 	@Override
     protected String[] getCollectionSuffixes() {
 		return collectionPersister == null ? null : new String[] { "__" };
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
 				join.addJoin( ( AssociationType ) persister.getElementType(),
 						elementName,
 						JoinType.INNER_JOIN,
 						persister.getElementColumnNames(collectionName) );
 			}
 			catch ( MappingException me ) {
 				throw new QueryException( me );
 			}
 		}
 		join.addCondition( collectionName, keyColumnNames, " = ?" );
 		//if ( persister.hasWhere() ) join.addCondition( persister.getSQLWhereString(collectionName) );
 		EntityType elemType = ( EntityType ) collectionElementType;
 		addFrom( elementName, elemType.getAssociatedEntityName(), join );
 
 	}
 
 	String getPathAlias(String path) {
 		return ( String ) pathAliases.get( path );
 	}
 
 	JoinSequence getPathJoin(String path) {
 		return ( JoinSequence ) pathJoins.get( path );
 	}
 
 	void addPathAliasAndJoin(String path, String alias, JoinSequence joinSequence) {
 		pathAliases.put( path, alias );
 		pathJoins.put( path, joinSequence );
 	}
 
 	@Override
 	public List list(SessionImplementor session, QueryParameters queryParameters)
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
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		try {
 			final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 			final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, false, afterLoadActions, session );
 			final ResultSet rs = wrapper.getResultSet();
 			final PreparedStatement st = (PreparedStatement) wrapper.getStatement();
 			HolderInstantiator hi = HolderInstantiator.createClassicHolderInstantiator(holderConstructor, queryParameters.getResultTransformer());
 			Iterator result = new IteratorImpl( rs, st, session, queryParameters.isReadOnly( session ), returnTypes, getColumnNames(), hi );
 
 			if ( stats ) {
 				session.getFactory().getStatisticsImplementor().queryExecuted(
 						"HQL: " + queryString,
 						0,
 						System.currentTimeMillis() - startTime
 					);
 			}
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not execute query using iterate",
 					getSQLString()
 				);
 		}
 
 	}
 
 	public int executeUpdate(QueryParameters queryParameters, SessionImplementor session) throws HibernateException {
 		throw new UnsupportedOperationException( "Not supported!  Use the AST translator...");
 	}
 
 	@Override
     protected boolean[] includeInResultRow() {
 		boolean[] isResultReturned = includeInSelect;
 		if ( hasScalars ) {
 			isResultReturned = new boolean[ returnedTypes.size() ];
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
     protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		Object[] resultRow = getResultRow( row, rs, session );
 		return ( holderClass == null && resultRow.length == 1 ?
 				resultRow[ 0 ] :
 				resultRow
 		);
 	}
 
 	@Override
     protected Object[] getResultRow(Object[] row, ResultSet rs, SessionImplementor session)
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
 				Object[] row = ( Object[] ) results.get( i );
 				try {
 					results.set( i, holderConstructor.newInstance( row ) );
 				}
 				catch ( Exception e ) {
 					throw new QueryException( "could not instantiate: " + holderClass, e );
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/AbstractTableBasedBulkIdHandler.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/AbstractTableBasedBulkIdHandler.java
index eb6e7f296e..733fb1a657 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/AbstractTableBasedBulkIdHandler.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/AbstractTableBasedBulkIdHandler.java
@@ -1,184 +1,183 @@
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
 package org.hibernate.hql.spi;
 
 import java.sql.SQLException;
 import java.util.Collections;
 import java.util.List;
 
 import antlr.RecognitionException;
 import antlr.collections.AST;
 
 import org.hibernate.HibernateException;
 import org.hibernate.JDBCException;
-import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
 import org.hibernate.hql.internal.ast.SqlGenerator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Table;
 import org.hibernate.param.ParameterSpecification;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.InsertSelect;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectValues;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractTableBasedBulkIdHandler {
 	private final SessionFactoryImplementor sessionFactory;
 	private final HqlSqlWalker walker;
 
 	private final String catalog;
 	private final String schema;
 
 	public AbstractTableBasedBulkIdHandler(
 			SessionFactoryImplementor sessionFactory,
 			HqlSqlWalker walker,
 			String catalog,
 			String schema) {
 		this.sessionFactory = sessionFactory;
 		this.walker = walker;
 		this.catalog = catalog;
 		this.schema = schema;
 	}
 
 	protected SessionFactoryImplementor factory() {
 		return sessionFactory;
 	}
 
 	protected HqlSqlWalker walker() {
 		return walker;
 	}
 
 	protected JDBCException convert(SQLException e, String message, String sql) {
 		throw factory().getSQLExceptionHelper().convert( e, message, sql );
 	}
 
 	protected static class ProcessedWhereClause {
 		public static final ProcessedWhereClause NO_WHERE_CLAUSE = new ProcessedWhereClause();
 
 		private final String userWhereClauseFragment;
 		private final List<ParameterSpecification> idSelectParameterSpecifications;
 
 		private ProcessedWhereClause() {
 			this( "", Collections.<ParameterSpecification>emptyList() );
 		}
 
 		public ProcessedWhereClause(String userWhereClauseFragment, List<ParameterSpecification> idSelectParameterSpecifications) {
 			this.userWhereClauseFragment = userWhereClauseFragment;
 			this.idSelectParameterSpecifications = idSelectParameterSpecifications;
 		}
 
 		public String getUserWhereClauseFragment() {
 			return userWhereClauseFragment;
 		}
 
 		public List<ParameterSpecification> getIdSelectParameterSpecifications() {
 			return idSelectParameterSpecifications;
 		}
 	}
 
 	@SuppressWarnings("unchecked")
 	protected ProcessedWhereClause processWhereClause(AST whereClause) {
 		if ( whereClause.getNumberOfChildren() != 0 ) {
 			// If a where clause was specified in the update/delete query, use it to limit the
 			// returned ids here...
 			try {
 				SqlGenerator sqlGenerator = new SqlGenerator( sessionFactory );
 				sqlGenerator.whereClause( whereClause );
 				String userWhereClause = sqlGenerator.getSQL().substring( 7 );  // strip the " where "
 				List<ParameterSpecification> idSelectParameterSpecifications = sqlGenerator.getCollectedParameters();
 
 				return new ProcessedWhereClause( userWhereClause, idSelectParameterSpecifications );
 			}
 			catch ( RecognitionException e ) {
 				throw new HibernateException( "Unable to generate id select for DML operation", e );
 			}
 		}
 		else {
 			return ProcessedWhereClause.NO_WHERE_CLAUSE;
 		}
 	}
 
 	protected String generateIdInsertSelect(Queryable persister, String tableAlias, ProcessedWhereClause whereClause) {
 		Select select = new Select( sessionFactory.getDialect() );
 		SelectValues selectClause = new SelectValues( sessionFactory.getDialect() )
 				.addColumns( tableAlias, persister.getIdentifierColumnNames(), persister.getIdentifierColumnNames() );
 		addAnyExtraIdSelectValues( selectClause );
 		select.setSelectClause( selectClause.render() );
 
 		String rootTableName = persister.getTableName();
 		String fromJoinFragment = persister.fromJoinFragment( tableAlias, true, false );
 		String whereJoinFragment = persister.whereJoinFragment( tableAlias, true, false );
 
 		select.setFromClause( rootTableName + ' ' + tableAlias + fromJoinFragment );
 
 		if ( whereJoinFragment == null ) {
 			whereJoinFragment = "";
 		}
 		else {
 			whereJoinFragment = whereJoinFragment.trim();
 			if ( whereJoinFragment.startsWith( "and" ) ) {
 				whereJoinFragment = whereJoinFragment.substring( 4 );
 			}
 		}
 
 		if ( whereClause.getUserWhereClauseFragment().length() > 0 ) {
 			if ( whereJoinFragment.length() > 0 ) {
 				whereJoinFragment += " and ";
 			}
 		}
 		select.setWhereClause( whereJoinFragment + whereClause.getUserWhereClauseFragment() );
 
 		InsertSelect insert = new InsertSelect( sessionFactory.getDialect() );
 		if ( sessionFactory.getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert-select for " + persister.getEntityName() + " ids" );
 		}
 		insert.setTableName( determineIdTableName( persister ) );
 		insert.setSelect( select );
 		return insert.toStatementString();
 	}
 
 	protected void addAnyExtraIdSelectValues(SelectValues selectClause) {
 	}
 
 	protected String determineIdTableName(Queryable persister) {
 		// todo : use the identifier/name qualifier service once we pull that over to master
 		return Table.qualify( catalog, schema, persister.getTemporaryIdTableName() );
 	}
 
 	protected String generateIdSubselect(Queryable persister) {
 		return "select " + StringHelper.join( ", ", persister.getIdentifierColumnNames() ) +
 				" from " + determineIdTableName( persister );
 	}
 
 	protected void prepareForUse(Queryable persister, SessionImplementor session) {
 	}
 
 	protected void releaseFromUse(Queryable persister, SessionImplementor session) {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdStrategy.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdStrategy.java
index c714ec5bf1..da82c391fd 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/MultiTableBulkIdStrategy.java
@@ -1,106 +1,105 @@
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
 package org.hibernate.hql.spi;
 
 import java.util.Map;
 
 import org.hibernate.cfg.Mappings;
-import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.spi.JdbcConnectionAccess;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
 import org.hibernate.persister.entity.Queryable;
 
 /**
  * Generalized strategy contract for handling multi-table bulk HQL operations.
  *
  * @author Steve Ebersole
  */
 public interface MultiTableBulkIdStrategy {
 	/**
 	 * Prepare the strategy.  Called as the SessionFactory is being built.  Intended patterns here include:<ul>
 	 *     <li>Adding tables to the passed Mappings, to be picked by by "schema management tools"</li>
 	 *     <li>Manually creating the tables immediately through the passed JDBC Connection access</li>
 	 * </ul>
 	 *
 	 * @param jdbcServices The JdbcService object
 	 * @param connectionAccess Access to the JDBC Connection
 	 * @param mappings The Hibernate Mappings object, for access to O/RM mapping information
 	 * @param mapping The Hibernate Mapping contract, mainly for use in DDL generation
 	 * @param settings Configuration settings
 	 */
 	public void prepare(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess, Mappings mappings, Mapping mapping, Map settings);
 
 	/**
 	 * Release the strategy.   Called as the SessionFactory is being shut down.
 	 *
 	 * @param jdbcServices The JdbcService object
 	 * @param connectionAccess Access to the JDBC Connection
 	 */
 	public void release(JdbcServices jdbcServices, JdbcConnectionAccess connectionAccess);
 
 	/**
 	 * Handler for dealing with multi-table HQL bulk update statements.
 	 */
 	public static interface UpdateHandler {
 		public Queryable getTargetedQueryable();
 		public String[] getSqlStatements();
 
 		public int execute(SessionImplementor session, QueryParameters queryParameters);
 	}
 
 	/**
 	 * Build a handler capable of handling the bulk update indicated by the given walker.
 	 *
 	 * @param factory The SessionFactory
 	 * @param walker The AST walker, representing the update query
 	 *
 	 * @return The handler
 	 */
 	public UpdateHandler buildUpdateHandler(SessionFactoryImplementor factory, HqlSqlWalker walker);
 
 	/**
 	 * Handler for dealing with multi-table HQL bulk delete statements.
 	 */
 	public static interface DeleteHandler {
 		public Queryable getTargetedQueryable();
 		public String[] getSqlStatements();
 
 		public int execute(SessionImplementor session, QueryParameters queryParameters);
 	}
 
 	/**
 	 * Build a handler capable of handling the bulk delete indicated by the given walker.
 	 *
 	 * @param factory The SessionFactory
 	 * @param walker The AST walker, representing the delete query
 	 *
 	 * @return The handler
 	 */
 	public DeleteHandler buildDeleteHandler(SessionFactoryImplementor factory, HqlSqlWalker walker);
 }
