diff --git a/hibernate-core/src/main/java/org/hibernate/Criteria.java b/hibernate-core/src/main/java/org/hibernate/Criteria.java
index 8a63102c28..1a10390cc2 100644
--- a/hibernate-core/src/main/java/org/hibernate/Criteria.java
+++ b/hibernate-core/src/main/java/org/hibernate/Criteria.java
@@ -1,569 +1,570 @@
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
 	 * Set the lock mode of the current entity
 	 *
 	 * @param lockMode The lock mode to be applied
 	 *
 	 * @return this (for method chaining)
 	 */
 	public Criteria setLockMode(LockMode lockMode);
 
 	/**
 	 * Set the lock mode of the aliased entity
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
-	 * @deprecated use {@link #createAlias(String, String, JoinType, Criterion}
+	 * @deprecated use {@link #createAlias(String, String, JoinType, Criterion)}
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
+	 * @return {@code this}, for method chaining
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
 	 * Get the results as an instance of {@link ScrollableResults}
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
diff --git a/hibernate-core/src/main/java/org/hibernate/Interceptor.java b/hibernate-core/src/main/java/org/hibernate/Interceptor.java
index 98d412dc23..7664c36eb6 100644
--- a/hibernate-core/src/main/java/org/hibernate/Interceptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/Interceptor.java
@@ -1,177 +1,272 @@
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
 package org.hibernate;
+
 import java.io.Serializable;
 import java.util.Iterator;
 
 import org.hibernate.type.Type;
 
 /**
  * Allows user code to inspect and/or change property values.
  * <br><br>
  * Inspection occurs before property values are written and after they are read
  * from the database.<br>
  * <br>
  * There might be a single instance of <tt>Interceptor</tt> for a <tt>SessionFactory</tt>, or a new instance
  * might be specified for each <tt>Session</tt>. Whichever approach is used, the interceptor must be
  * serializable if the <tt>Session</tt> is to be serializable. This means that <tt>SessionFactory</tt>-scoped
  * interceptors should implement <tt>readResolve()</tt>.<br>
  * <br>
  * The <tt>Session</tt> may not be invoked from a callback (nor may a callback cause a collection or proxy to
  * be lazily initialized).<br>
  * <br>
  * Instead of implementing this interface directly, it is usually better to extend <tt>EmptyInterceptor</tt>
  * and override only the callback methods of interest.
  *
- * @see SessionFactory#openSession(Interceptor)
+ * @see SessionBuilder#interceptor(Interceptor)
+ * @see SharedSessionBuilder#interceptor()
  * @see org.hibernate.cfg.Configuration#setInterceptor(Interceptor)
  * @see EmptyInterceptor
+ *
  * @author Gavin King
  */
 public interface Interceptor {
 	/**
 	 * Called just before an object is initialized. The interceptor may change the <tt>state</tt>, which will
 	 * be propagated to the persistent object. Note that when this method is called, <tt>entity</tt> will be
 	 * an empty uninitialized instance of the class.
+	 * <p/>
+	 * NOTE: The indexes across the <tt>state</tt>, <tt>propertyNames</tt> and <tt>types</tt> arrays match.
 	 *
-	 * @return <tt>true</tt> if the user modified the <tt>state</tt> in any way.
+	 * @param entity The entity instance being loaded
+	 * @param id The identifier value being loaded
+	 * @param state The entity state (which will be pushed into the entity instance)
+	 * @param propertyNames The names of the entity properties, corresponding to the <tt>state</tt>.
+	 * @param types The types of the entity properties, corresponding to the <tt>state</tt>.
+	 *
+	 * @return {@code true} if the user modified the <tt>state</tt> in any way.
+	 *
+	 * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
 	 */
 	public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException;
+
 	/**
 	 * Called when an object is detected to be dirty, during a flush. The interceptor may modify the detected
 	 * <tt>currentState</tt>, which will be propagated to both the database and the persistent object.
 	 * Note that not all flushes end in actual synchronization with the database, in which case the
 	 * new <tt>currentState</tt> will be propagated to the object, but not necessarily (immediately) to
 	 * the database. It is strongly recommended that the interceptor <b>not</b> modify the <tt>previousState</tt>.
+	 * <p/>
+	 * NOTE: The indexes across the <tt>currentState</tt>, <tt>previousState</tt>, <tt>propertyNames</tt> and
+	 * <tt>types</tt> arrays match.
+	 *
+	 * @param entity The entity instance detected as being dirty and being flushed
+	 * @param id The identifier of the entity
+	 * @param currentState The entity's current state
+	 * @param previousState The entity's previous (load time) state.
+	 * @param propertyNames The names of the entity properties
+	 * @param types The types of the entity properties
+	 *
+	 * @return {@code true} if the user modified the <tt>currentState</tt> in any way.
 	 *
-	 * @return <tt>true</tt> if the user modified the <tt>currentState</tt> in any way.
+	 * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
 	 */
 	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException;
+
 	/**
 	 * Called before an object is saved. The interceptor may modify the <tt>state</tt>, which will be used for
 	 * the SQL <tt>INSERT</tt> and propagated to the persistent object.
 	 *
+	 * @param entity The entity instance whose state is being inserted
+	 * @param id The identifier of the entity
+	 * @param state The state of the entity which will be inserted
+	 * @param propertyNames The names of the entity properties.
+	 * @param types The types of the entity properties
+	 *
 	 * @return <tt>true</tt> if the user modified the <tt>state</tt> in any way.
+	 *
+	 * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
 	 */
 	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException;
+
 	/**
 	 *  Called before an object is deleted. It is not recommended that the interceptor modify the <tt>state</tt>.
+	 *
+	 * @param entity The entity instance being deleted
+	 * @param id The identifier of the entity
+	 * @param state The state of the entity
+	 * @param propertyNames The names of the entity properties.
+	 * @param types The types of the entity properties
+	 *
+	 * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
 	 */
 	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException;
+
 	/**
 	 * Called before a collection is (re)created.
+	 *
+	 * @param collection The collection instance.
+	 * @param key The collection key value.
+	 *
+	 * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
 	 */
 	public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException;
+
 	/**
 	 * Called before a collection is deleted.
+	 *
+	 * @param collection The collection instance.
+	 * @param key The collection key value.
+	 *
+	 * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
 	 */
 	public void onCollectionRemove(Object collection, Serializable key) throws CallbackException;
+
 	/**
 	 * Called before a collection is updated.
+	 *
+	 * @param collection The collection instance.
+	 * @param key The collection key value.
+	 *
+	 * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
 	 */
 	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException;
+
 	/**
 	 * Called before a flush
+	 *
+	 * @param entities The entities to be flushed
+	 *
+	 * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
 	 */
 	public void preFlush(Iterator entities) throws CallbackException;
+
 	/**
 	 * Called after a flush that actually ends in execution of the SQL statements required to synchronize
 	 * in-memory state with the database.
+	 *
+	 * @param entities The entities that were flushed.
+	 *
+	 * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
 	 */
 	public void postFlush(Iterator entities) throws CallbackException;
+
 	/**
 	 * Called to distinguish between transient and detached entities. The return value determines the
 	 * state of the entity with respect to the current session.
 	 * <ul>
 	 * <li><tt>Boolean.TRUE</tt> - the entity is transient
 	 * <li><tt>Boolean.FALSE</tt> - the entity is detached
 	 * <li><tt>null</tt> - Hibernate uses the <tt>unsaved-value</tt> mapping and other heuristics to 
 	 * determine if the object is unsaved
 	 * </ul>
 	 * @param entity a transient or detached entity
 	 * @return Boolean or <tt>null</tt> to choose default behaviour
 	 */
 	public Boolean isTransient(Object entity);
+
 	/**
 	 * Called from <tt>flush()</tt>. The return value determines whether the entity is updated
 	 * <ul>
 	 * <li>an array of property indices - the entity is dirty
 	 * <li>an empty array - the entity is not dirty
 	 * <li><tt>null</tt> - use Hibernate's default dirty-checking algorithm
 	 * </ul>
-	 * @param entity a persistent entity
-	 * @return array of dirty property indices or <tt>null</tt> to choose default behaviour
+	 *
+	 * @param entity The entity for which to find dirty properties.
+	 * @param id The identifier of the entity
+	 * @param currentState The current entity state as taken from the entity instance
+	 * @param previousState The state of the entity when it was last synchronized (generally when it was loaded)
+	 * @param propertyNames The names of the entity properties.
+	 * @param types The types of the entity properties
+	 *
+	 * @return array of dirty property indices or {@code null} to indicate Hibernate should perform default behaviour
+	 *
+	 * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
 	 */
 	public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types);
 	/**
 	 * Instantiate the entity class. Return <tt>null</tt> to indicate that Hibernate should use
 	 * the default constructor of the class. The identifier property of the returned instance
 	 * should be initialized with the given identifier.
 	 *
 	 * @param entityName the name of the entity
 	 * @param entityMode The type of entity instance to be returned.
 	 * @param id the identifier of the new instance
 	 * @return an instance of the class, or <tt>null</tt> to choose default behaviour
+	 *
+	 * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
 	 */
 	public Object instantiate(String entityName, EntityMode entityMode, Serializable id) throws CallbackException;
 
 	/**
 	 * Get the entity name for a persistent or transient instance
 	 * @param object an entity instance
 	 * @return the name of the entity
+	 *
+	 * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
 	 */
 	public String getEntityName(Object object) throws CallbackException;
 
 	/**
 	 * Get a fully loaded entity instance that is cached externally
 	 * @param entityName the name of the entity
 	 * @param id the instance identifier
 	 * @return a fully initialized entity
-	 * @throws CallbackException
+	 *
+	 * @throws CallbackException Thrown if the interceptor encounters any problems handling the callback.
 	 */
 	public Object getEntity(String entityName, Serializable id) throws CallbackException;
 	
 	/**
 	 * Called when a Hibernate transaction is begun via the Hibernate <tt>Transaction</tt> 
 	 * API. Will not be called if transactions are being controlled via some other 
 	 * mechanism (CMT, for example).
+	 *
+	 * @param tx The Hibernate transaction facade object
 	 */
 	public void afterTransactionBegin(Transaction tx);
+
 	/**
 	 * Called before a transaction is committed (but not before rollback).
+	 *
+	 * @param tx The Hibernate transaction facade object
 	 */
 	public void beforeTransactionCompletion(Transaction tx);
+
 	/**
 	 * Called after a transaction is committed or rolled back.
+	 *
+	 * @param tx The Hibernate transaction facade object
 	 */
 	public void afterTransactionCompletion(Transaction tx);
 
 	/**
 	 * Called when sql string is being prepared. 
 	 * @param sql sql to be prepared
 	 * @return original or modified sql
 	 */
 	public String onPrepareStatement(String sql);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/Session.java b/hibernate-core/src/main/java/org/hibernate/Session.java
index ce275a0abb..e47cacf89e 100644
--- a/hibernate-core/src/main/java/org/hibernate/Session.java
+++ b/hibernate-core/src/main/java/org/hibernate/Session.java
@@ -1,1043 +1,1051 @@
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
-	public Serializable getIdentifier(Object object) throws HibernateException;
+	public Serializable getIdentifier(Object object);
 
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
-	 * @throws HibernateException
 	 */
-	public void evict(Object object) throws HibernateException;
+	public void evict(Object object);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param theClass a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockMode the lock level
+	 *
 	 * @return the persistent instance or proxy
-	 * @throws HibernateException
+	 *
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
-	public Object load(Class theClass, Serializable id, LockMode lockMode) throws HibernateException;
+	public Object load(Class theClass, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param theClass a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockOptions contains the lock level
 	 * @return the persistent instance or proxy
-	 * @throws HibernateException
 	 */
-	public Object load(Class theClass, Serializable id, LockOptions lockOptions) throws HibernateException;
+	public Object load(Class theClass, Serializable id, LockOptions lockOptions);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param entityName a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockMode the lock level
+	 *
 	 * @return the persistent instance or proxy
-	 * @throws HibernateException
+	 *
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
-	public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException;
+	public Object load(String entityName, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * obtaining the specified lock mode, assuming the instance exists.
 	 *
 	 * @param entityName a persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
 	 * @param lockOptions contains the lock level
+	 *
 	 * @return the persistent instance or proxy
-	 * @throws HibernateException
 	 */
-	public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException;
+	public Object load(String entityName, Serializable id, LockOptions lockOptions);
 
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
+	 *
 	 * @return the persistent instance or proxy
-	 * @throws HibernateException
 	 */
-	public Object load(Class theClass, Serializable id) throws HibernateException;
+	public Object load(Class theClass, Serializable id);
 
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
+	 *
 	 * @return the persistent instance or proxy
-	 * @throws HibernateException
 	 */
-	public Object load(String entityName, Serializable id) throws HibernateException;
+	public Object load(String entityName, Serializable id);
 
 	/**
 	 * Read the persistent state associated with the given identifier into the given transient
 	 * instance.
 	 *
 	 * @param object an "empty" instance of the persistent class
 	 * @param id a valid identifier of an existing persistent instance of the class
-	 * @throws HibernateException
 	 */
-	public void load(Object object, Serializable id) throws HibernateException;
+	public void load(Object object, Serializable id);
 
 	/**
 	 * Persist the state of the given detached instance, reusing the current
 	 * identifier value.  This operation cascades to associated instances if
-	 * the association is mapped with <tt>cascade="replicate"</tt>.
+	 * the association is mapped with {@code cascade="replicate"}
 	 *
 	 * @param object a detached instance of a persistent class
+	 * @param replicationMode The replication mode to use
 	 */
-	public void replicate(Object object, ReplicationMode replicationMode) throws HibernateException;
+	public void replicate(Object object, ReplicationMode replicationMode);
 
 	/**
 	 * Persist the state of the given detached instance, reusing the current
 	 * identifier value.  This operation cascades to associated instances if
-	 * the association is mapped with <tt>cascade="replicate"</tt>.
+	 * the association is mapped with {@code cascade="replicate"}
 	 *
+	 * @param entityName The entity name
 	 * @param object a detached instance of a persistent class
+	 * @param replicationMode The replication mode to use
 	 */
-	public void replicate(String entityName, Object object, ReplicationMode replicationMode) throws HibernateException;
+	public void replicate(String entityName, Object object, ReplicationMode replicationMode) ;
 
 	/**
 	 * Persist the given transient instance, first assigning a generated identifier. (Or
 	 * using the current value of the identifier property if the <tt>assigned</tt>
 	 * generator is used.) This operation cascades to associated instances if the
-	 * association is mapped with <tt>cascade="save-update"</tt>.
+	 * association is mapped with {@code cascade="save-update"}
 	 *
 	 * @param object a transient instance of a persistent class
+	 *
 	 * @return the generated identifier
-	 * @throws HibernateException
 	 */
-	public Serializable save(Object object) throws HibernateException;
+	public Serializable save(Object object);
 
 	/**
 	 * Persist the given transient instance, first assigning a generated identifier. (Or
 	 * using the current value of the identifier property if the <tt>assigned</tt>
 	 * generator is used.)  This operation cascades to associated instances if the
-	 * association is mapped with <tt>cascade="save-update"</tt>.
+	 * association is mapped with {@code cascade="save-update"}
 	 *
+	 * @param entityName The entity name
 	 * @param object a transient instance of a persistent class
+	 *
 	 * @return the generated identifier
-	 * @throws HibernateException
 	 */
-	public Serializable save(String entityName, Object object) throws HibernateException;
+	public Serializable save(String entityName, Object object);
 
 	/**
 	 * Either {@link #save(Object)} or {@link #update(Object)} the given
 	 * instance, depending upon resolution of the unsaved-value checks (see the
 	 * manual for discussion of unsaved-value checking).
 	 * <p/>
 	 * This operation cascades to associated instances if the association is mapped
-	 * with <tt>cascade="save-update"</tt>.
+	 * with {@code cascade="save-update"}
+	 *
+	 * @param object a transient or detached instance containing new or updated state
 	 *
 	 * @see Session#save(java.lang.Object)
 	 * @see Session#update(Object object)
-	 * @param object a transient or detached instance containing new or updated state
-	 * @throws HibernateException
 	 */
-	public void saveOrUpdate(Object object) throws HibernateException;
+	public void saveOrUpdate(Object object);
 
 	/**
 	 * Either {@link #save(String, Object)} or {@link #update(String, Object)}
 	 * the given instance, depending upon resolution of the unsaved-value checks
 	 * (see the manual for discussion of unsaved-value checking).
 	 * <p/>
 	 * This operation cascades to associated instances if the association is mapped
-	 * with <tt>cascade="save-update"</tt>.
+	 * with {@code cascade="save-update"}
+	 *
+	 * @param entityName The entity name
+	 * @param object a transient or detached instance containing new or updated state
 	 *
 	 * @see Session#save(String,Object)
 	 * @see Session#update(String,Object)
-	 * @param object a transient or detached instance containing new or updated state
-	 * @throws HibernateException
 	 */
-	public void saveOrUpdate(String entityName, Object object) throws HibernateException;
+	public void saveOrUpdate(String entityName, Object object);
 
 	/**
 	 * Update the persistent instance with the identifier of the given detached
 	 * instance. If there is a persistent instance with the same identifier,
 	 * an exception is thrown. This operation cascades to associated instances
-	 * if the association is mapped with <tt>cascade="save-update"</tt>.
+	 * if the association is mapped with {@code cascade="save-update"}
 	 *
 	 * @param object a detached instance containing updated state
-	 * @throws HibernateException
 	 */
-	public void update(Object object) throws HibernateException;
+	public void update(Object object);
 
 	/**
 	 * Update the persistent instance with the identifier of the given detached
 	 * instance. If there is a persistent instance with the same identifier,
 	 * an exception is thrown. This operation cascades to associated instances
-	 * if the association is mapped with <tt>cascade="save-update"</tt>.
+	 * if the association is mapped with {@code cascade="save-update"}
 	 *
+	 * @param entityName The entity name
 	 * @param object a detached instance containing updated state
-	 * @throws HibernateException
 	 */
-	public void update(String entityName, Object object) throws HibernateException;
+	public void update(String entityName, Object object);
 
 	/**
 	 * Copy the state of the given object onto the persistent object with the same
 	 * identifier. If there is no persistent instance currently associated with
 	 * the session, it will be loaded. Return the persistent instance. If the
 	 * given instance is unsaved, save a copy of and return it as a newly persistent
 	 * instance. The given instance does not become associated with the session.
 	 * This operation cascades to associated instances if the association is mapped
-	 * with <tt>cascade="merge"</tt>.<br>
-	 * <br>
+	 * with {@code cascade="merge"}
+	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param object a detached instance with state to be copied
+	 *
 	 * @return an updated persistent instance
 	 */
-	public Object merge(Object object) throws HibernateException;
+	public Object merge(Object object);
 
 	/**
 	 * Copy the state of the given object onto the persistent object with the same
 	 * identifier. If there is no persistent instance currently associated with
 	 * the session, it will be loaded. Return the persistent instance. If the
 	 * given instance is unsaved, save a copy of and return it as a newly persistent
 	 * instance. The given instance does not become associated with the session.
 	 * This operation cascades to associated instances if the association is mapped
-	 * with <tt>cascade="merge"</tt>.<br>
-	 * <br>
+	 * with {@code cascade="merge"}
+	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
+	 * @param entityName The entity name
 	 * @param object a detached instance with state to be copied
+	 *
 	 * @return an updated persistent instance
 	 */
-	public Object merge(String entityName, Object object) throws HibernateException;
+	public Object merge(String entityName, Object object);
 
 	/**
 	 * Make a transient instance persistent. This operation cascades to associated
-	 * instances if the association is mapped with <tt>cascade="persist"</tt>.<br>
-	 * <br>
+	 * instances if the association is mapped with {@code cascade="persist"}
+	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
 	 * @param object a transient instance to be made persistent
 	 */
-	public void persist(Object object) throws HibernateException;
+	public void persist(Object object);
 	/**
 	 * Make a transient instance persistent. This operation cascades to associated
-	 * instances if the association is mapped with <tt>cascade="persist"</tt>.<br>
-	 * <br>
+	 * instances if the association is mapped with {@code cascade="persist"}
+	 * <p/>
 	 * The semantics of this method are defined by JSR-220.
 	 *
+	 * @param entityName The entity name
 	 * @param object a transient instance to be made persistent
 	 */
-	public void persist(String entityName, Object object) throws HibernateException;
+	public void persist(String entityName, Object object);
 
 	/**
 	 * Remove a persistent instance from the datastore. The argument may be
 	 * an instance associated with the receiving <tt>Session</tt> or a transient
 	 * instance with an identifier associated with existing persistent state.
 	 * This operation cascades to associated instances if the association is mapped
-	 * with <tt>cascade="delete"</tt>.
+	 * with {@code cascade="delete"}
 	 *
 	 * @param object the instance to be removed
-	 * @throws HibernateException
 	 */
-	public void delete(Object object) throws HibernateException;
+	public void delete(Object object);
 
 	/**
 	 * Remove a persistent instance from the datastore. The <b>object</b> argument may be
 	 * an instance associated with the receiving <tt>Session</tt> or a transient
 	 * instance with an identifier associated with existing persistent state.
 	 * This operation cascades to associated instances if the association is mapped
-	 * with <tt>cascade="delete"</tt>.
+	 * with {@code cascade="delete"}
 	 *
 	 * @param entityName The entity name for the instance to be removed.
 	 * @param object the instance to be removed
-	 * @throws HibernateException
 	 */
-	public void delete(String entityName, Object object) throws HibernateException;
+	public void delete(String entityName, Object object);
 
 	/**
 	 * Obtain the specified lock level upon the given object. This may be used to
 	 * perform a version check (<tt>LockMode.READ</tt>), to upgrade to a pessimistic
 	 * lock (<tt>LockMode.PESSIMISTIC_WRITE</tt>), or to simply reassociate a transient instance
 	 * with a session (<tt>LockMode.NONE</tt>). This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="lock"</tt>.
 	 *
 	 * @param object a persistent or transient instance
 	 * @param lockMode the lock level
-	 * @throws HibernateException
+	 *
 	 * @deprecated instead call buildLockRequest(LockMode).lock(object)
 	 */
 	@Deprecated
-	public void lock(Object object, LockMode lockMode) throws HibernateException;
+	public void lock(Object object, LockMode lockMode);
 
 	/**
 	 * Obtain the specified lock level upon the given object. This may be used to
 	 * perform a version check (<tt>LockMode.OPTIMISTIC</tt>), to upgrade to a pessimistic
 	 * lock (<tt>LockMode.PESSIMISTIC_WRITE</tt>), or to simply reassociate a transient instance
 	 * with a session (<tt>LockMode.NONE</tt>). This operation cascades to associated
 	 * instances if the association is mapped with <tt>cascade="lock"</tt>.
 	 *
 	 * @param object a persistent or transient instance
 	 * @param lockMode the lock level
-	 * @throws HibernateException
+	 *
 	 * @deprecated instead call buildLockRequest(LockMode).lock(entityName, object)
 	 */
+	@SuppressWarnings( {"JavaDoc"})
 	@Deprecated
-	public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException;
+	public void lock(String entityName, Object object, LockMode lockMode);
 
 	/**
 	 * Build a LockRequest that specifies the LockMode, pessimistic lock timeout and lock scope.
 	 * timeout and scope is ignored for optimistic locking.  After building the LockRequest,
 	 * call LockRequest.lock to perform the requested locking. 
-	 *
-	 * Use: session.buildLockRequest().
-	 *      setLockMode(LockMode.PESSIMISTIC_WRITE).setTimeOut(1000 * 60).lock(entity);
+	 * <p/>
+	 * Example usage:
+	 * {@code session.buildLockRequest().setLockMode(LockMode.PESSIMISTIC_WRITE).setTimeOut(60000).lock(entity);}
 	 *
 	 * @param lockOptions contains the lock level
+	 *
 	 * @return a lockRequest that can be used to lock the passed object.
-	 * @throws HibernateException
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
-	 * @throws HibernateException
 	 */
-	public void refresh(Object object) throws HibernateException;
+	public void refresh(Object object);
 
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
-	 * @throws HibernateException
 	 */
-	public void refresh(String entityName, Object object) throws HibernateException;
+	public void refresh(String entityName, Object object);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
 	 *
 	 * @param object a persistent or detached instance
 	 * @param lockMode the lock mode to use
-	 * @throws HibernateException
+	 *
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
-	public void refresh(Object object, LockMode lockMode) throws HibernateException;
+	public void refresh(Object object, LockMode lockMode);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
 	 *
 	 * @param object a persistent or detached instance
 	 * @param lockOptions contains the lock mode to use
-	 * @throws HibernateException
 	 */
-	public void refresh(Object object, LockOptions lockOptions) throws HibernateException;
+	public void refresh(Object object, LockOptions lockOptions);
 
 	/**
 	 * Re-read the state of the given instance from the underlying database, with
 	 * the given <tt>LockMode</tt>. It is inadvisable to use this to implement
 	 * long-running sessions that span many business tasks. This method is, however,
 	 * useful in certain special circumstances.
 	 *
 	 * @param entityName a persistent class
 	 * @param object a persistent or detached instance
 	 * @param lockOptions contains the lock mode to use
-	 * @throws HibernateException
 	 */
-	public void refresh(String entityName, Object object, LockOptions lockOptions) throws HibernateException;
+	public void refresh(String entityName, Object object, LockOptions lockOptions);
+
 	/**
 	 * Determine the current lock mode of the given object.
 	 *
 	 * @param object a persistent instance
+	 *
 	 * @return the current lock mode
-	 * @throws HibernateException
 	 */
-	public LockMode getCurrentLockMode(Object object) throws HibernateException;
+	public LockMode getCurrentLockMode(Object object);
 
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
+	 *
 	 * @return a persistent instance or null
-	 * @throws HibernateException
 	 */
-	public Object get(Class clazz, Serializable id) throws HibernateException;
+	public Object get(Class clazz, Serializable id);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param clazz a persistent class
 	 * @param id an identifier
 	 * @param lockMode the lock mode
+	 *
 	 * @return a persistent instance or null
-	 * @throws HibernateException
+	 *
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
-	public Object get(Class clazz, Serializable id, LockMode lockMode) throws HibernateException;
+	public Object get(Class clazz, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param clazz a persistent class
 	 * @param id an identifier
 	 * @param lockOptions the lock mode
+	 *
 	 * @return a persistent instance or null
-	 * @throws HibernateException
 	 */
-	public Object get(Class clazz, Serializable id, LockOptions lockOptions) throws HibernateException;
+	public Object get(Class clazz, Serializable id, LockOptions lockOptions);
 
 	/**
 	 * Return the persistent instance of the given named entity with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 *
 	 * @param entityName the entity name
 	 * @param id an identifier
+	 *
 	 * @return a persistent instance or null
-	 * @throws HibernateException
 	 */
-	public Object get(String entityName, Serializable id) throws HibernateException;
+	public Object get(String entityName, Serializable id);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param entityName the entity name
 	 * @param id an identifier
 	 * @param lockMode the lock mode
+	 *
 	 * @return a persistent instance or null
-	 * @throws HibernateException
+	 *
 	 * @deprecated LockMode parameter should be replaced with LockOptions
 	 */
 	@Deprecated
-	public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException;
+	public Object get(String entityName, Serializable id, LockMode lockMode);
 
 	/**
 	 * Return the persistent instance of the given entity class with the given identifier,
 	 * or null if there is no such persistent instance. (If the instance is already associated
 	 * with the session, return that instance. This method never returns an uninitialized instance.)
 	 * Obtain the specified lock mode if the instance exists.
 	 *
 	 * @param entityName the entity name
 	 * @param id an identifier
 	 * @param lockOptions contains the lock mode
+	 *
 	 * @return a persistent instance or null
-	 * @throws HibernateException
 	 */
-	public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException;
+	public Object get(String entityName, Serializable id, LockOptions lockOptions);
 
 	/**
 	 * Return the entity name for a persistent entity
 	 *   
 	 * @param object a persistent entity
+	 *
 	 * @return the entity name
-	 * @throws HibernateException
 	 */
-	public String getEntityName(Object object) throws HibernateException;
+	public String getEntityName(Object object);
 	
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
 	 * Enable the named filter for this current session.
 	 *
 	 * @param filterName The name of the filter to be enabled.
+	 *
 	 * @return The Filter instance representing the enabled filter.
 	 */
 	public Filter enableFilter(String filterName);
 
 	/**
 	 * Retrieve a currently enabled filter by name.
 	 *
 	 * @param filterName The name of the filter to be retrieved.
+	 *
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
+	 *
+	 * @return The session statistics being collected for this session
 	 */
 	public SessionStatistics getStatistics();
 
 	/**
 	 * Is the specified entity or proxy read-only?
 	 *
 	 * To get the default read-only/modifiable setting used for
 	 * entities and proxies that are loaded into the session:
 	 * @see org.hibernate.Session#isDefaultReadOnly()
 	 *
-	 * @param entityOrProxy, an entity or HibernateProxy
-	 * @return true, the entity or proxy is read-only;
-	 *         false, the entity or proxy is modifiable.
+	 * @param entityOrProxy an entity or HibernateProxy
+	 * @return {@code true} if the entity or proxy is read-only, {@code false} if the entity or proxy is modifiable.
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
-	 * @param entityOrProxy, an entity or HibernateProxy
-	 * @param readOnly, if true, the entity or proxy is made read-only;
-	 *                  if false, the entity or proxy is made modifiable.
+	 * @param entityOrProxy an entity or HibernateProxy
+	 * @param readOnly {@code true} if the entity or proxy should be made read-only; {@code false} if the entity or
+	 * proxy should be made modifiable
 	 */
 	public void setReadOnly(Object entityOrProxy, boolean readOnly);
 
 	/**
-	 * Controller for allowing users to perform JDBC related work using the Connection
-	 * managed by this Session.
+	 * Controller for allowing users to perform JDBC related work using the Connection managed by this Session.
 	 *
 	 * @param work The work to be performed.
 	 * @throws HibernateException Generally indicates wrapped {@link java.sql.SQLException}
 	 */
 	public void doWork(Work work) throws HibernateException;
 
 	/**
-	 * Controller for allowing users to perform JDBC related work using the Connection
-	 * managed by this Session, returning the result from calling <code>work.execute()</code>
-	 * ({@link ReturningWork<T>.execute(Connection)}/
+	 * Controller for allowing users to perform JDBC related work using the Connection managed by this Session.  After
+	 * execution returns the result of the {@link ReturningWork#execute} call.
 	 *
 	 * @param work The work to be performed.
-	 * @return the result from calling <code>work.execute()</code>.
+	 *
+	 * @return the result from calling {@link ReturningWork#execute}.
+	 *
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
-	 * @return the application-supplied connection or {@literal null}
+	 * @return the application-supplied connection or {@code null}
 	 *
 	 * @see #reconnect(Connection)
 	 */
-	Connection disconnect() throws HibernateException;
+	Connection disconnect();
 
 	/**
 	 * Reconnect to the given JDBC connection.
 	 *
 	 * @param connection a JDBC connection
 	 * 
 	 * @see #disconnect()
 	 */
-	void reconnect(Connection connection) throws HibernateException;
+	void reconnect(Connection connection);
 
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
-		 * @param lockMode
+		 * @param lockMode The lock mode to use for this request
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
diff --git a/hibernate-core/src/main/java/org/hibernate/Transaction.java b/hibernate-core/src/main/java/org/hibernate/Transaction.java
index c9d83ce125..6351603da0 100644
--- a/hibernate-core/src/main/java/org/hibernate/Transaction.java
+++ b/hibernate-core/src/main/java/org/hibernate/Transaction.java
@@ -1,168 +1,171 @@
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
 package org.hibernate;
 
 import javax.transaction.Synchronization;
 
 import org.hibernate.engine.transaction.spi.LocalStatus;
 
 /**
  * Defines the contract for abstracting applications from the configured underlying means of transaction management.
  * Allows the application to define units of work, while maintaining abstraction from the underlying transaction
  * implementation (eg. JTA, JDBC).
  * <p/>
  * A transaction is associated with a {@link Session} and is usually initiated by a call to
  * {@link org.hibernate.Session#beginTransaction()}.  A single session might span multiple transactions since
  * the notion of a session (a conversation between the application and the datastore) is of coarser granularity than
  * the notion of a transaction.  However, it is intended that there be at most one uncommitted transaction associated
  * with a particular {@link Session} at any time.
  * <p/>
  * Implementers are not intended to be thread-safe.
  *
  * @author Anton van Straaten
  * @author Steve Ebersole
  */
 public interface Transaction {
 	/**
 	 * Is this transaction the initiator of any underlying transaction?
 	 *
-	 * @return {@literal true} if this transaction initiated the underlying transaction; {@literal false} otherwise.
+	 * @return {@code true} if this transaction initiated the underlying transaction; {@code false} otherwise.
 	 */
 	public boolean isInitiator();
 
 	/**
 	 * Begin this transaction.  No-op if the transaction has already been begun.  Note that this is not necessarily
 	 * symmetrical since usually multiple calls to {@link #commit} or {@link #rollback} will error.
 	 *
 	 * @throws HibernateException Indicates a problem beginning the transaction.
 	 */
 	public void begin();
 
 	/**
 	 * Commit this transaction.  This might entail a number of things depending on the context:<ul>
 	 *     <li>
 	 *         If this transaction is the {@link #isInitiator initiator}, {@link Session#flush} the {@link Session}
 	 *         with which it is associated (unless {@link Session} is in {@link FlushMode#MANUAL}).
 	 *     </li>
 	 *     <li>
 	 *         If this transaction is the {@link #isInitiator initiator}, commit the underlying transaction.
 	 *     </li>
 	 *     <li>
 	 *         Coordinate various callbacks
 	 *     </li>
 	 * </ul>
 	 *
 	 * @throws HibernateException Indicates a problem committing the transaction.
 	 */
 	public void commit();
 
 	/**
 	 * Rollback this transaction.  Either rolls back the underlying transaction or ensures it cannot later commit
 	 * (depending on the actual underlying strategy).
 	 *
 	 * @throws HibernateException Indicates a problem rolling back the transaction.
 	 */
 	public void rollback();
 
 	/**
 	 * Get the current local status of this transaction.
 	 * <p/>
 	 * This only accounts for the local view of the transaction status.  In other words it does not check the status
 	 * of the actual underlying transaction.
 	 *
 	 * @return The current local status.
 	 */
 	public LocalStatus getLocalStatus();
 
 	/**
 	 * Is this transaction still active?
 	 * <p/>
 	 * Answers on a best effort basis.  For example, in the case of JDBC based transactions we cannot know that a
 	 * transaction is active when it is initiated directly through the JDBC {@link java.sql.Connection}, only when
 	 * it is initiated from here.
 	 *
-	 * @return {@literal true} if the transaction is still active; {@literal false} otherwise.
+	 * @return {@code true} if the transaction is still active; {@code false} otherwise.
 	 *
 	 * @throws HibernateException Indicates a problem checking the transaction status.
 	 */
 	public boolean isActive();
 
 	/**
 	 * Is Hibernate participating in the underlying transaction?
 	 * <p/>
 	 * Generally speaking this will be the same as {@link #isActive()}.
 	 * 
-	 * @return
+	 * @return {@code true} if Hibernate is known to be participating in the underlying transaction; {@code false}
+	 * otherwise.
 	 */
 	public boolean isParticipating();
 
 	/**
 	 * Was this transaction committed?
 	 * <p/>
 	 * Answers on a best effort basis.  For example, in the case of JDBC based transactions we cannot know that a
 	 * transaction was committed when the commit was performed directly through the JDBC {@link java.sql.Connection},
 	 * only when the commit was done from this.
 	 *
-	 * @return {@literal true} if the transaction is rolled back; {@literal false} otherwise.
+	 * @return {@code true} if the transaction is rolled back; {@code false} otherwise.
 	 *
 	 * @throws HibernateException Indicates a problem checking the transaction status.
 	 */
+	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean wasCommitted();
 
 	/**
 	 * Was this transaction rolled back or set to rollback only?
 	 * <p/>
 	 * Answers on a best effort basis.  For example, in the case of JDBC based transactions we cannot know that a
 	 * transaction was rolled back when rollback was performed directly through the JDBC {@link java.sql.Connection},
 	 * only when it was rolled back  from here.
 	 *
 	 * @return {@literal true} if the transaction is rolled back; {@literal false} otherwise.
 	 *
 	 * @throws HibernateException Indicates a problem checking the transaction status.
 	 */
+	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean wasRolledBack();
 
 	/**
 	 * Register a user synchronization callback for this transaction.
 	 *
 	 * @param synchronization The Synchronization callback to register.
 	 *
 	 * @throws HibernateException Indicates a problem registering the synchronization.
 	 */
 	public void registerSynchronization(Synchronization synchronization) throws HibernateException;
 
 	/**
 	 * Set the transaction timeout for any transaction started by a subsequent call to {@link #begin} on this instance.
 	 *
 	 * @param seconds The number of seconds before a timeout.
 	 */
 	public void setTimeout(int seconds);
 
 	/**
 	 * Retrieve the transaction timeout set for this transaction.  A negative indicates no timeout has been set.
 	 *
 	 * @return The timeout, in seconds.
 	 */
 	public int getTimeout();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/package.html b/hibernate-core/src/main/java/org/hibernate/cache/package.html
index d486044d52..eec40fcd79 100755
--- a/hibernate-core/src/main/java/org/hibernate/cache/package.html
+++ b/hibernate-core/src/main/java/org/hibernate/cache/package.html
@@ -1,62 +1,32 @@
 <!--
   ~ Hibernate, Relational Persistence for Idiomatic Java
   ~
-  ~ Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+  ~ Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
   ~ indicated by the @author tags or express copyright attribution
   ~ statements applied by the authors.  All third-party contributions are
-  ~ distributed under license by Red Hat Middleware LLC.
+  ~ distributed under license by Red Hat Inc.
   ~
   ~ This copyrighted material is made available to anyone wishing to use, modify,
   ~ copy, or redistribute it subject to the terms and conditions of the GNU
   ~ Lesser General Public License, as published by the Free Software Foundation.
   ~
   ~ This program is distributed in the hope that it will be useful,
   ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
   ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
   ~ for more details.
   ~
   ~ You should have received a copy of the GNU Lesser General Public License
   ~ along with this distribution; if not, write to:
   ~ Free Software Foundation, Inc.
   ~ 51 Franklin Street, Fifth Floor
   ~ Boston, MA  02110-1301  USA
-  ~
   -->
-
 <html>
-    <head>
-    </head>
-
     <body>
         <p>
-            This package defines Hibernate second level cache service.  {@link org.hibernate.cache.spi} defines the
-            SPI used to integrate with Hibernate internals.
-        </p>
-        <p>
-            The legacy (and now deprecated) approach to caching is defined by the {@link org.hibernate.cache.CacheProvider} and
-            {@link org.hibernate.cache.Cache} interfaces as well as the {@link org.hibernate.cache.CacheConcurrencyStrategy}
-            interface along with the various implementations of all these interfaces.  In that scheme, a
-            {@link org.hibernate.cache.CacheProvider} defined how to configure and perform lifecycle operations
-            in regards to a particular underlying caching library; it also defined how to build {@link org.hibernate.cache.Cache}
-            instances which in turn defined how to access the "regions" of the underlying cache instance.
-            For entity and collection data cache regions, {@link org.hibernate.cache.CacheConcurrencyStrategy} wrapped
-            access to those cache regions to apply transactional/concurrent access semantics.
-        </p>
-        <p>
-            The improved approach is based on {@link org.hibernate.cache.RegionFactory}, the various
-            {@link org.hibernate.cache.Region} specializations and the two access strategies contracts
-            ({@link org.hibernate.cache.access.EntityRegionAccessStrategy} and
-            {@link org.hibernate.cache.access.CollectionRegionAccessStrategy}).  The general approach here is that
-            {@link org.hibernate.cache.RegionFactory} defined how to configure and perform lifecycle operations
-            in regards to a particular underlying caching library (<b>or libraries</b>).
-            {@link org.hibernate.cache.RegionFactory} also defines how to build specialized
-            {@link org.hibernate.cache.Region} instances based on the type of data we will be storing in that given
-            region.  The fact that {@link org.hibernate.cache.RegionFactory} is asked to build <b>specialized</b>
-            regions (as opposed to just general access) is the first <i>improvement</i> over the legacy scheme.  The
-            second <i>improvement</i> is the fact that the regions (well the ones like entity and collection regions
-            that are responsible for storing {@link org.hibernate.cache.TransactionalDataRegion transactional} data) are
-            asked to build their own access strategies (see {@link org.hibernate.cache.EntityRegion#buildAccessStrategy}
-            and {@link org.hibernate.cache.CollectionRegion#buildAccessStrategy}).
+            This package defines API of the Hibernate second level cache service.  The
+            <a href="{@docRoot}/org/hibernate/cache/spi">org.hibernate.cache.spi</a> package defines the SPI used to
+            integrate with Hibernate internals.
         </p>
     </body>
 </html>
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheDataDescription.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheDataDescription.java
index 64daa6f07d..4ecaad58ca 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheDataDescription.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/CacheDataDescription.java
@@ -1,58 +1,57 @@
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
 package org.hibernate.cache.spi;
 
 import java.util.Comparator;
 
 /**
  * Describes attributes regarding the type of data to be cached.
  *
  * @author Steve Ebersole
  */
 public interface CacheDataDescription {
 	/**
 	 * Is the data marked as being mutable?
 	 *
-	 * @return True if the data is mutable; false otherwise.
+	 * @return {@code true} if the data is mutable; {@code false} otherwise.
 	 */
 	public boolean isMutable();
 
 	/**
 	 * Is the data to be cached considered versioned?
-	 * <p/>
-	 * If true, it is illegal for {@link #getVersionComparator} to return
-	 * null.
 	 *
-	 * @return True if the data is versioned; false otherwise.
+	 * If {@code true}, it is illegal for {@link #getVersionComparator} to return {@code null}.
+	 *
+	 * @return {@code true} if the data is versioned; {@code false} otherwise.
 	 */
 	public boolean isVersioned();
 
 	/**
-	 * Get the comparator used to compare two different version values.
-	 * <p/>
-	 * May return null <b>if</b> {@link #isVersioned()} returns false.
-	 * @return
+	 * Get the comparator used to compare two different version values.  May return {@code null} <b>if</b>
+	 * {@link #isVersioned()} returns false.
+	 *
+	 * @return The comparator for versions, or {@code null}
 	 */
 	public Comparator getVersionComparator();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/TransactionAwareCache.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/TransactionAwareCache.java
index f4edb4074a..50a860ae0c 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/TransactionAwareCache.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/TransactionAwareCache.java
@@ -1,33 +1,33 @@
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
 package org.hibernate.cache.spi;
 
 /**
- * Marker interface for identifying {@link Cache} implementations which are aware of JTA transactions
+ * Marker interface for identifying {@link org.hibernate.Cache} implementations which are aware of JTA transactions
  *
  * @author Steve Ebersole
  */
 public interface TransactionAwareCache {
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/SoftLock.java b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/SoftLock.java
index 54ff19a0b0..bf4344fb62 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/SoftLock.java
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/SoftLock.java
@@ -1,33 +1,32 @@
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
 package org.hibernate.cache.spi.access;
 
 /**
- * Moved up from inner definition on the now deprecated
- * {@link org.hibernate.cache.spi.CacheConcurrencyStrategy}.
+ * Marker object for use by synchronous concurrency strategies
  *
  * @author Steve Ebersole
  */
 public interface SoftLock {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/package.html b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/package.html
index 9b6f2e3afe..54af786eeb 100644
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/access/package.html
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/access/package.html
@@ -1,66 +1,64 @@
 <!--
   ~ Hibernate, Relational Persistence for Idiomatic Java
   ~
-  ~ Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+  ~ Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
   ~ indicated by the @author tags or express copyright attribution
   ~ statements applied by the authors.  All third-party contributions are
-  ~ distributed under license by Red Hat Middleware LLC.
+  ~ distributed under license by Red Hat Inc.
   ~
   ~ This copyrighted material is made available to anyone wishing to use, modify,
   ~ copy, or redistribute it subject to the terms and conditions of the GNU
   ~ Lesser General Public License, as published by the Free Software Foundation.
   ~
   ~ This program is distributed in the hope that it will be useful,
   ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
   ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
   ~ for more details.
   ~
   ~ You should have received a copy of the GNU Lesser General Public License
   ~ along with this distribution; if not, write to:
   ~ Free Software Foundation, Inc.
   ~ 51 Franklin Street, Fifth Floor
   ~ Boston, MA  02110-1301  USA
-  ~
   -->
 
 <html>
-<head></head>
 <body>
 <p>
 	Defines contracts for transactional and concurrent access to cached
-    {@link org.hibernate.cache.access.EntityRegionAccessStrategy entity} and
-    {@link org.hibernate.cache.access.CollectionRegionAccessStrategy collection} data.  Transactions pass in a
+    {@link org.hibernate.cache.spi.access.EntityRegionAccessStrategy entity} and
+    {@link org.hibernate.cache.spi.access.CollectionRegionAccessStrategy collection} data.  Transactions pass in a
     timestamp indicating transaction start time which is then used to protect against concurrent access (exactly how
     that occurs is based on the actual access-strategy impl used). Two different implementation patterns are provided
-    for.
+    for:
     <ul>
         <li>
             A transaction-aware cache implementation might be wrapped by a <i>synchronous</i> access strategy,
             where updates to the cache are written to the cache inside the transaction.
         </li>
         <li>
             A non-transaction-aware cache would be wrapped by an <i>asynchronous</i> access strategy, where items
             are merely "soft locked" during the transaction and then updated during the "after transaction completion"
             phase; the soft lock is not an actual lock on the database row - only upon the cached representation of the
             item.
         </li>
     </ul>
-    The <i>asynchronous</i> access strategies are: {@link org.hibernate.cache.access.AccessType.READ_ONLY read-only},
-    {@link org.hibernate.cache.access.AccessType.READ_WRITE read-write} and
-    {@link org.hibernate.cache.access.AccessType.NONSTRICT_READ_WRITE nonstrict-read-write}.  The only
-    <i>synchronous</i> access strategy is {@link org.hibernate.cache.access.AccessType.TRANSACTIONAL transactional}.
+    The <i>asynchronous</i> access strategies are: {@link org.hibernate.cache.spi.access.AccessType#READ_ONLY read-only},
+    {@link org.hibernate.cache.spi.access.AccessType#READ_WRITE read-write} and
+    {@link org.hibernate.cache.spi.access.AccessType#NONSTRICT_READ_WRITE nonstrict-read-write}.  The only
+    <i>synchronous</i> access strategy is {@link org.hibernate.cache.spi.access.AccessType#TRANSACTIONAL transactional}.
 </p>
 <p>
     Note that, for an <i>asynchronous</i> cache, cache invalidation must be a two step process (lock->unlock or
     lock->afterUpdate), since this is the only way to guarantee consistency with the database for a nontransactional
     cache implementation. For a <i>synchronous</i> cache, cache invalidation is a single step process (evict or update).
-    Hence, these contracts ({@link org.hibernate.cache.access.EntityRegionAcessStrategy} and
-    {@link org.hibernate.cache.access.CollectionRegionAccessStrategy}) define a three step process to cater for both 
+    Hence, these contracts ({@link org.hibernate.cache.spi.access.EntityRegionAccessStrategy} and
+    {@link org.hibernate.cache.spi.access.CollectionRegionAccessStrategy}) define a three step process to cater for both
     models (see the individual contracts for details).
 </p>
 <p>
     Note that query result caching does not go through an access strategy; those caches are managed directly against
-    the underlying {@link org.hibernate.cache.QueryResultsRegion}.
+    the underlying {@link org.hibernate.cache.spi.QueryResultsRegion}.
 </p>
 </body>
 </html>
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/package.html b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/package.html
index 63529aaa39..851f05847d 100755
--- a/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/package.html
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/entry/package.html
@@ -1,35 +1,31 @@
 <!--
   ~ Hibernate, Relational Persistence for Idiomatic Java
   ~
-  ~ Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+  ~ Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
   ~ indicated by the @author tags or express copyright attribution
   ~ statements applied by the authors.  All third-party contributions are
-  ~ distributed under license by Red Hat Middleware LLC.
+  ~ distributed under license by Red Hat Inc.
   ~
   ~ This copyrighted material is made available to anyone wishing to use, modify,
   ~ copy, or redistribute it subject to the terms and conditions of the GNU
   ~ Lesser General Public License, as published by the Free Software Foundation.
   ~
   ~ This program is distributed in the hope that it will be useful,
   ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
   ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
   ~ for more details.
   ~
   ~ You should have received a copy of the GNU Lesser General Public License
   ~ along with this distribution; if not, write to:
   ~ Free Software Foundation, Inc.
   ~ 51 Franklin Street, Fifth Floor
   ~ Boston, MA  02110-1301  USA
-  ~
   -->
 
 <html>
-<head>
-</head>
 <body>
 <p>
-	This package defines formats for disassembled state
-	kept in the second level cache.
+	This package defines formats for disassembled state kept in the second level cache.
 </p>
 </body>
 </html>
diff --git a/hibernate-core/src/main/java/org/hibernate/cache/spi/package.html b/hibernate-core/src/main/java/org/hibernate/cache/spi/package.html
new file mode 100644
index 0000000000..11eef904a8
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/cache/spi/package.html
@@ -0,0 +1,37 @@
+<!--
+  ~ Hibernate, Relational Persistence for Idiomatic Java
+  ~
+  ~ Copyright (c) 2011, Red Hat Inc. or third-party contributors as
+  ~ indicated by the @author tags or express copyright attribution
+  ~ statements applied by the authors.  All third-party contributions are
+  ~ distributed under license by Red Hat Inc.
+  ~
+  ~ This copyrighted material is made available to anyone wishing to use, modify,
+  ~ copy, or redistribute it subject to the terms and conditions of the GNU
+  ~ Lesser General Public License, as published by the Free Software Foundation.
+  ~
+  ~ This program is distributed in the hope that it will be useful,
+  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+  ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+  ~ for more details.
+  ~
+  ~ You should have received a copy of the GNU Lesser General Public License
+  ~ along with this distribution; if not, write to:
+  ~ Free Software Foundation, Inc.
+  ~ 51 Franklin Street, Fifth Floor
+  ~ Boston, MA  02110-1301  USA
+  -->
+
+<html>
+    <body>
+        <p>
+            Defines the Hibernate second level caching SPI.
+        </p>
+        <p>
+            The initial contract here is {@link org.hibernate.cache.spi.RegionFactory} whose implementations are
+            responsible for configuring and managing lifecycle operations in regards to the particular underlying
+            caching library.  Its other main purpose is to build specializations {@link org.hibernate.cache.spi.Region}
+            instances based on the type of data we will be storing in that given region.
+        </p>
+    </body>
+</html>
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
index f8a5235ac2..7395c77bb3 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
@@ -1,3519 +1,3521 @@
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
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.ObjectInputStream;
 import java.io.Serializable;
 import java.io.StringReader;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collections;
 import java.util.Enumeration;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.ListIterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 import java.util.TreeMap;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 import javax.persistence.Embeddable;
 import javax.persistence.Entity;
 import javax.persistence.MapsId;
 
 import org.dom4j.Attribute;
 import org.dom4j.Document;
 import org.dom4j.DocumentException;
 import org.dom4j.Element;
 import org.jboss.logging.Logger;
 import org.xml.sax.EntityResolver;
 import org.xml.sax.InputSource;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.InvalidMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.MappingNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.common.reflection.MetadataProvider;
 import org.hibernate.annotations.common.reflection.MetadataProviderInjector;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
 import org.hibernate.cfg.annotations.reflection.JPAMetadataProvider;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.MySQLDialect;
 import org.hibernate.dialect.function.SQLFunction;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentifierGeneratorAggregator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.id.factory.internal.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.internal.util.ConfigHelper;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.SerializationHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.internal.util.xml.MappingReader;
 import org.hibernate.internal.util.xml.Origin;
 import org.hibernate.internal.util.xml.OriginImpl;
 import org.hibernate.internal.util.xml.XMLHelper;
 import org.hibernate.internal.util.xml.XmlDocument;
 import org.hibernate.internal.util.xml.XmlDocumentImpl;
 import org.hibernate.mapping.AuxiliaryDatabaseObject;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.DenormalizedTable;
 import org.hibernate.mapping.FetchProfile;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.IdentifierCollection;
 import org.hibernate.mapping.Index;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.MetadataSource;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TypeDef;
 import org.hibernate.mapping.UniqueKey;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.secure.internal.JACCConfiguration;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.StandardServiceRegistryImpl;
 import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
 import org.hibernate.tool.hbm2ddl.IndexMetadata;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.UserType;
 
 /**
  * An instance of <tt>Configuration</tt> allows the application
  * to specify properties and mapping documents to be used when
  * creating a <tt>SessionFactory</tt>. Usually an application will create
  * a single <tt>Configuration</tt>, build a single instance of
  * <tt>SessionFactory</tt> and then instantiate <tt>Session</tt>s in
  * threads servicing client requests. The <tt>Configuration</tt> is meant
  * only as an initialization-time object. <tt>SessionFactory</tt>s are
  * immutable and do not retain any association back to the
  * <tt>Configuration</tt>.<br>
  * <br>
  * A new <tt>Configuration</tt> will use the properties specified in
  * <tt>hibernate.properties</tt> by default.
  * <p/>
  * NOTE : This will be replaced by use of {@link ServiceRegistryBuilder} and
  * {@link org.hibernate.metamodel.MetadataSources} instead after the 4.0 release at which point this class will become
  * deprecated and scheduled for removal in 5.0.  See
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6183">HHH-6183</a>,
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-2578">HHH-2578</a> and
  * <a href="http://opensource.atlassian.com/projects/hibernate/browse/HHH-6586">HHH-6586</a> for details
  *
  * @author Gavin King
  * @see org.hibernate.SessionFactory
  */
+@SuppressWarnings( {"UnusedDeclaration"})
 public class Configuration implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Configuration.class.getName());
 
 	public static final String DEFAULT_CACHE_CONCURRENCY_STRATEGY = AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY;
 
 	public static final String USE_NEW_ID_GENERATOR_MAPPINGS = AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS;
 
 	public static final String ARTEFACT_PROCESSING_ORDER = "hibernate.mapping.precedence";
 
 	/**
 	 * Class name of the class needed to enable Search.
 	 */
 	private static final String SEARCH_STARTUP_CLASS = "org.hibernate.search.event.EventListenerRegister";
 
 	/**
 	 * Method to call to enable Search.
 	 */
 	private static final String SEARCH_STARTUP_METHOD = "enableHibernateSearch";
 
 	protected MetadataSourceQueue metadataSourceQueue;
 	private transient ReflectionManager reflectionManager;
 
 	protected Map<String, PersistentClass> classes;
 	protected Map<String, String> imports;
 	protected Map<String, Collection> collections;
 	protected Map<String, Table> tables;
 	protected List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects;
 
 	protected Map<String, NamedQueryDefinition> namedQueries;
 	protected Map<String, NamedSQLQueryDefinition> namedSqlQueries;
 	protected Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
 
 	protected Map<String, TypeDef> typeDefs;
 	protected Map<String, FilterDefinition> filterDefinitions;
 	protected Map<String, FetchProfile> fetchProfiles;
 
 	protected Map tableNameBinding;
 	protected Map columnNameBindingPerTable;
 
 	protected List<SecondPass> secondPasses;
 	protected List<Mappings.PropertyReference> propertyReferences;
 	protected Map<ExtendsQueueEntry, ?> extendsQueue;
 
 	protected Map<String, SQLFunction> sqlFunctions;
 	private TypeResolver typeResolver = new TypeResolver();
 
 	private EntityTuplizerFactory entityTuplizerFactory;
 //	private ComponentTuplizerFactory componentTuplizerFactory; todo : HHH-3517 and HHH-1907
 
 	private Interceptor interceptor;
 	private Properties properties;
 	private EntityResolver entityResolver;
 	private EntityNotFoundDelegate entityNotFoundDelegate;
 
 	protected transient XMLHelper xmlHelper;
 	protected NamingStrategy namingStrategy;
 	private SessionFactoryObserver sessionFactoryObserver;
 
 	protected final SettingsFactory settingsFactory;
 
 	private transient Mapping mapping = buildMapping();
 
 	private MutableIdentifierGeneratorFactory identifierGeneratorFactory;
 
 	private Map<Class<?>, org.hibernate.mapping.MappedSuperclass> mappedSuperClasses;
 
 	private Map<String, IdGenerator> namedGenerators;
 	private Map<String, Map<String, Join>> joins;
 	private Map<String, AnnotatedClassType> classTypes;
 	private Set<String> defaultNamedQueryNames;
 	private Set<String> defaultNamedNativeQueryNames;
 	private Set<String> defaultSqlResultSetMappingNames;
 	private Set<String> defaultNamedGenerators;
 	private Map<String, Properties> generatorTables;
 	private Map<Table, List<UniqueConstraintHolder>> uniqueConstraintHoldersByTable;
 	private Map<String, String> mappedByResolver;
 	private Map<String, String> propertyRefResolver;
 	private Map<String, AnyMetaDef> anyMetaDefs;
 	private List<CacheHolder> caches;
 	private boolean inSecondPass = false;
 	private boolean isDefaultProcessed = false;
 	private boolean isValidatorNotPresentLogged;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithMapsId;
 	private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithIdAndToOne;
 	private boolean specjProprietarySyntaxEnabled;
 
 
 	protected Configuration(SettingsFactory settingsFactory) {
 		this.settingsFactory = settingsFactory;
 		reset();
 	}
 
 	public Configuration() {
 		this( new SettingsFactory() );
 	}
 
 	protected void reset() {
 		metadataSourceQueue = new MetadataSourceQueue();
 		createReflectionManager();
 
 		classes = new HashMap<String,PersistentClass>();
 		imports = new HashMap<String,String>();
 		collections = new HashMap<String,Collection>();
 		tables = new TreeMap<String,Table>();
 
 		namedQueries = new HashMap<String,NamedQueryDefinition>();
 		namedSqlQueries = new HashMap<String,NamedSQLQueryDefinition>();
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 
 		typeDefs = new HashMap<String,TypeDef>();
 		filterDefinitions = new HashMap<String, FilterDefinition>();
 		fetchProfiles = new HashMap<String, FetchProfile>();
 		auxiliaryDatabaseObjects = new ArrayList<AuxiliaryDatabaseObject>();
 
 		tableNameBinding = new HashMap();
 		columnNameBindingPerTable = new HashMap();
 
 		secondPasses = new ArrayList<SecondPass>();
 		propertyReferences = new ArrayList<Mappings.PropertyReference>();
 		extendsQueue = new HashMap<ExtendsQueueEntry, String>();
 
 		xmlHelper = new XMLHelper();
 		interceptor = EmptyInterceptor.INSTANCE;
 		properties = Environment.getProperties();
 		entityResolver = XMLHelper.DEFAULT_DTD_RESOLVER;
 
 		sqlFunctions = new HashMap<String, SQLFunction>();
 
 		entityTuplizerFactory = new EntityTuplizerFactory();
 //		componentTuplizerFactory = new ComponentTuplizerFactory();
 
 		identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
 
 		mappedSuperClasses = new HashMap<Class<?>, MappedSuperclass>();
 
 		metadataSourcePrecedence = Collections.emptyList();
 
 		namedGenerators = new HashMap<String, IdGenerator>();
 		joins = new HashMap<String, Map<String, Join>>();
 		classTypes = new HashMap<String, AnnotatedClassType>();
 		generatorTables = new HashMap<String, Properties>();
 		defaultNamedQueryNames = new HashSet<String>();
 		defaultNamedNativeQueryNames = new HashSet<String>();
 		defaultSqlResultSetMappingNames = new HashSet<String>();
 		defaultNamedGenerators = new HashSet<String>();
 		uniqueConstraintHoldersByTable = new HashMap<Table, List<UniqueConstraintHolder>>();
 		mappedByResolver = new HashMap<String, String>();
 		propertyRefResolver = new HashMap<String, String>();
 		caches = new ArrayList<CacheHolder>();
 		namingStrategy = EJB3NamingStrategy.INSTANCE;
 		setEntityResolver( new EJB3DTDEntityResolver() );
 		anyMetaDefs = new HashMap<String, AnyMetaDef>();
 		propertiesAnnotatedWithMapsId = new HashMap<XClass, Map<String, PropertyData>>();
 		propertiesAnnotatedWithIdAndToOne = new HashMap<XClass, Map<String, PropertyData>>();
 		specjProprietarySyntaxEnabled = System.getProperty( "hibernate.enable_specj_proprietary_syntax" ) != null;
 	}
 
 	public EntityTuplizerFactory getEntityTuplizerFactory() {
 		return entityTuplizerFactory;
 	}
 
 	public ReflectionManager getReflectionManager() {
 		return reflectionManager;
 	}
 
 //	public ComponentTuplizerFactory getComponentTuplizerFactory() {
 //		return componentTuplizerFactory;
 //	}
 
 	/**
 	 * Iterate the entity mappings
 	 *
 	 * @return Iterator of the entity mappings currently contained in the configuration.
 	 */
 	public Iterator<PersistentClass> getClassMappings() {
 		return classes.values().iterator();
 	}
 
 	/**
 	 * Iterate the collection mappings
 	 *
 	 * @return Iterator of the collection mappings currently contained in the configuration.
 	 */
 	public Iterator getCollectionMappings() {
 		return collections.values().iterator();
 	}
 
 	/**
 	 * Iterate the table mappings
 	 *
 	 * @return Iterator of the table mappings currently contained in the configuration.
 	 */
 	public Iterator<Table> getTableMappings() {
 		return tables.values().iterator();
 	}
 
 	/**
 	 * Iterate the mapped super class mappings
 	 * EXPERIMENTAL Consider this API as PRIVATE
 	 *
 	 * @return iterator over the MappedSuperclass mapping currently contained in the configuration.
 	 */
 	public Iterator<MappedSuperclass> getMappedSuperclassMappings() {
 		return mappedSuperClasses.values().iterator();
 	}
 
 	/**
 	 * Get the mapping for a particular entity
 	 *
 	 * @param entityName An entity name.
 	 * @return the entity mapping information
 	 */
 	public PersistentClass getClassMapping(String entityName) {
 		return classes.get( entityName );
 	}
 
 	/**
 	 * Get the mapping for a particular collection role
 	 *
 	 * @param role a collection role
 	 * @return The collection mapping information
 	 */
 	public Collection getCollectionMapping(String role) {
 		return collections.get( role );
 	}
 
 	/**
 	 * Set a custom entity resolver. This entity resolver must be
 	 * set before addXXX(misc) call.
 	 * Default value is {@link org.hibernate.internal.util.xml.DTDEntityResolver}
 	 *
 	 * @param entityResolver entity resolver to use
 	 */
 	public void setEntityResolver(EntityResolver entityResolver) {
 		this.entityResolver = entityResolver;
 	}
 
 	public EntityResolver getEntityResolver() {
 		return entityResolver;
 	}
 
 	/**
 	 * Retrieve the user-supplied delegate to handle non-existent entity
 	 * scenarios.  May be null.
 	 *
 	 * @return The user-supplied delegate
 	 */
 	public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 		return entityNotFoundDelegate;
 	}
 
 	/**
 	 * Specify a user-supplied delegate to be used to handle scenarios where an entity could not be
 	 * located by specified id.  This is mainly intended for EJB3 implementations to be able to
 	 * control how proxy initialization errors should be handled...
 	 *
 	 * @param entityNotFoundDelegate The delegate to use
 	 */
 	public void setEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate) {
 		this.entityNotFoundDelegate = entityNotFoundDelegate;
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param xmlFile a path to a file
 	 * @return this (for method chaining purposes)
 	 * @throws org.hibernate.MappingException Indicates inability to locate or parse
 	 * the specified mapping file.
 	 * @see #addFile(java.io.File)
 	 */
 	public Configuration addFile(String xmlFile) throws MappingException {
 		return addFile( new File( xmlFile ) );
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param xmlFile a path to a file
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates inability to locate the specified mapping file.  Historically this could
 	 * have indicated a problem parsing the XML document, but that is now delayed until after {@link #buildMappings}
 	 */
 	public Configuration addFile(final File xmlFile) throws MappingException {
 		LOG.readingMappingsFromFile( xmlFile.getPath() );
 		final String name =  xmlFile.getAbsolutePath();
 		final InputSource inputSource;
 		try {
 			inputSource = new InputSource( new FileInputStream( xmlFile ) );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( "file", xmlFile.toString() );
 		}
 		add( inputSource, "file", name );
 		return this;
 	}
 
 	private XmlDocument add(InputSource inputSource, String originType, String originName) {
 		return add( inputSource, new OriginImpl( originType, originName ) );
 	}
 
 	private XmlDocument add(InputSource inputSource, Origin origin) {
 		XmlDocument metadataXml = MappingReader.INSTANCE.readMappingDocument( entityResolver, inputSource, origin );
 		add( metadataXml );
 		return metadataXml;
 	}
 
 	public void add(XmlDocument metadataXml) {
 		if ( inSecondPass || !isOrmXml( metadataXml ) ) {
 			metadataSourceQueue.add( metadataXml );
 		}
 		else {
 			final MetadataProvider metadataProvider = ( (MetadataProviderInjector) reflectionManager ).getMetadataProvider();
 			JPAMetadataProvider jpaMetadataProvider = ( JPAMetadataProvider ) metadataProvider;
 			List<String> classNames = jpaMetadataProvider.getXMLContext().addDocument( metadataXml.getDocumentTree() );
 			for ( String className : classNames ) {
 				try {
 					metadataSourceQueue.add( reflectionManager.classForName( className, this.getClass() ) );
 				}
 				catch ( ClassNotFoundException e ) {
 					throw new AnnotationException( "Unable to load class defined in XML: " + className, e );
 				}
 			}
 		}
 	}
 
 	private static boolean isOrmXml(XmlDocument xmlDocument) {
 		return "entity-mappings".equals( xmlDocument.getDocumentTree().getRootElement().getName() );
 	}
 
 	/**
 	 * Add a cached mapping file.  A cached file is a serialized representation
 	 * of the DOM structure of a particular mapping.  It is saved from a previous
 	 * call as a file with the name <tt>xmlFile + ".bin"</tt> where xmlFile is
 	 * the name of the original mapping file.
 	 * </p>
 	 * If a cached <tt>xmlFile + ".bin"</tt> exists and is newer than
 	 * <tt>xmlFile</tt> the <tt>".bin"</tt> file will be read directly. Otherwise
 	 * xmlFile is read and then serialized to <tt>xmlFile + ".bin"</tt> for use
 	 * the next time.
 	 *
 	 * @param xmlFile The cacheable mapping file to be added.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the cached file or processing
 	 * the non-cached file.
 	 */
 	public Configuration addCacheableFile(File xmlFile) throws MappingException {
 		File cachedFile = determineCachedDomFile( xmlFile );
 
 		try {
 			return addCacheableFileStrictly( xmlFile );
 		}
 		catch ( SerializationException e ) {
 			LOG.unableToDeserializeCache( cachedFile.getPath(), e );
 		}
 		catch ( FileNotFoundException e ) {
 			LOG.cachedFileNotFound( cachedFile.getPath(), e );
 		}
 
 		final String name = xmlFile.getAbsolutePath();
 		final InputSource inputSource;
 		try {
 			inputSource = new InputSource( new FileInputStream( xmlFile ) );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( "file", xmlFile.toString() );
 		}
 
 		LOG.readingMappingsFromFile( xmlFile.getPath() );
 		XmlDocument metadataXml = add( inputSource, "file", name );
 
 		try {
 			LOG.debugf( "Writing cache file for: %s to: %s", xmlFile, cachedFile );
 			SerializationHelper.serialize( ( Serializable ) metadataXml.getDocumentTree(), new FileOutputStream( cachedFile ) );
 		}
 		catch ( Exception e ) {
 			LOG.unableToWriteCachedFile( cachedFile.getPath(), e.getMessage() );
 		}
 
 		return this;
 	}
 
 	private File determineCachedDomFile(File xmlFile) {
 		return new File( xmlFile.getAbsolutePath() + ".bin" );
 	}
 
 	/**
 	 * <b>INTENDED FOR TESTSUITE USE ONLY!</b>
 	 * <p/>
 	 * Much like {@link #addCacheableFile(File)} except that here we will fail immediately if
 	 * the cache version cannot be found or used for whatever reason
 	 *
 	 * @param xmlFile The xml file, not the bin!
 	 *
 	 * @return The dom "deserialized" from the cached file.
 	 *
 	 * @throws SerializationException Indicates a problem deserializing the cached dom tree
 	 * @throws FileNotFoundException Indicates that the cached file was not found or was not usable.
 	 */
 	public Configuration addCacheableFileStrictly(File xmlFile) throws SerializationException, FileNotFoundException {
 		final File cachedFile = determineCachedDomFile( xmlFile );
 
 		final boolean useCachedFile = xmlFile.exists()
 				&& cachedFile.exists()
 				&& xmlFile.lastModified() < cachedFile.lastModified();
 
 		if ( ! useCachedFile ) {
 			throw new FileNotFoundException( "Cached file could not be found or could not be used" );
 		}
 
 		LOG.readingCachedMappings( cachedFile );
 		Document document = ( Document ) SerializationHelper.deserialize( new FileInputStream( cachedFile ) );
 		add( new XmlDocumentImpl( document, "file", xmlFile.getAbsolutePath() ) );
 		return this;
 	}
 
 	/**
 	 * Add a cacheable mapping file.
 	 *
 	 * @param xmlFile The name of the file to be added.  This must be in a form
 	 * useable to simply construct a {@link java.io.File} instance.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the cached file or processing
 	 * the non-cached file.
 	 * @see #addCacheableFile(java.io.File)
 	 */
 	public Configuration addCacheableFile(String xmlFile) throws MappingException {
 		return addCacheableFile( new File( xmlFile ) );
 	}
 
 
 	/**
 	 * Read mappings from a <tt>String</tt>
 	 *
 	 * @param xml an XML string
 	 * @return this (for method chaining purposes)
 	 * @throws org.hibernate.MappingException Indicates problems parsing the
 	 * given XML string
 	 */
 	public Configuration addXML(String xml) throws MappingException {
 		LOG.debugf( "Mapping XML:\n%s", xml );
 		final InputSource inputSource = new InputSource( new StringReader( xml ) );
 		add( inputSource, "string", "XML String" );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a <tt>URL</tt>
 	 *
 	 * @param url The url for the mapping document to be read.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the URL or processing
 	 * the mapping document.
 	 */
 	public Configuration addURL(URL url) throws MappingException {
 		final String urlExternalForm = url.toExternalForm();
 
 		LOG.debugf( "Reading mapping document from URL : %s", urlExternalForm );
 
 		try {
 			add( url.openStream(), "URL", urlExternalForm );
 		}
 		catch ( IOException e ) {
 			throw new InvalidMappingException( "Unable to open url stream [" + urlExternalForm + "]", "URL", urlExternalForm, e );
 		}
 		return this;
 	}
 
 	private XmlDocument add(InputStream inputStream, final String type, final String name) {
 		final InputSource inputSource = new InputSource( inputStream );
 		try {
 			return add( inputSource, type, name );
 		}
 		finally {
 			try {
 				inputStream.close();
 			}
 			catch ( IOException ignore ) {
 				LOG.trace( "Was unable to close input stream");
 			}
 		}
 	}
 
 	/**
 	 * Read mappings from a DOM <tt>Document</tt>
 	 *
 	 * @param doc The DOM document
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the DOM or processing
 	 * the mapping document.
 	 */
 	public Configuration addDocument(org.w3c.dom.Document doc) throws MappingException {
 		LOG.debugf( "Mapping Document:\n%s", doc );
 
 		final Document document = xmlHelper.createDOMReader().read( doc );
 		add( new XmlDocumentImpl( document, "unknown", null ) );
 
 		return this;
 	}
 
 	/**
 	 * Read mappings from an {@link java.io.InputStream}.
 	 *
 	 * @param xmlInputStream The input stream containing a DOM.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the stream, or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addInputStream(InputStream xmlInputStream) throws MappingException {
 		add( xmlInputStream, "input stream", null );
 		return this;
 	}
 
 	/**
 	 * Read mappings as a application resource (i.e. classpath lookup).
 	 *
 	 * @param resourceName The resource name
 	 * @param classLoader The class loader to use.
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addResource(String resourceName, ClassLoader classLoader) throws MappingException {
 		LOG.readingMappingsFromResource( resourceName );
 		InputStream resourceInputStream = classLoader.getResourceAsStream( resourceName );
 		if ( resourceInputStream == null ) {
 			throw new MappingNotFoundException( "resource", resourceName );
 		}
 		add( resourceInputStream, "resource", resourceName );
 		return this;
 	}
 
 	/**
 	 * Read mappings as a application resourceName (i.e. classpath lookup)
 	 * trying different class loaders.
 	 *
 	 * @param resourceName The resource name
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addResource(String resourceName) throws MappingException {
 		LOG.readingMappingsFromResource( resourceName );
 		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
 		InputStream resourceInputStream = null;
 		if ( contextClassLoader != null ) {
 			resourceInputStream = contextClassLoader.getResourceAsStream( resourceName );
 		}
 		if ( resourceInputStream == null ) {
 			resourceInputStream = Environment.class.getClassLoader().getResourceAsStream( resourceName );
 		}
 		if ( resourceInputStream == null ) {
 			throw new MappingNotFoundException( "resource", resourceName );
 		}
 		add( resourceInputStream, "resource", resourceName );
 		return this;
 	}
 
 	/**
 	 * Read a mapping as an application resource using the convention that a class
 	 * named <tt>foo.bar.Foo</tt> is mapped by a file <tt>foo/bar/Foo.hbm.xml</tt>
 	 * which can be resolved as a classpath resource.
 	 *
 	 * @param persistentClass The mapped class
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems locating the resource or
 	 * processing the contained mapping document.
 	 */
 	public Configuration addClass(Class persistentClass) throws MappingException {
 		String mappingResourceName = persistentClass.getName().replace( '.', '/' ) + ".hbm.xml";
 		LOG.readingMappingsFromResource( mappingResourceName );
 		return addResource( mappingResourceName, persistentClass.getClassLoader() );
 	}
 
 	/**
 	 * Read metadata from the annotations associated with this class.
 	 *
 	 * @param annotatedClass The class containing annotations
 	 *
 	 * @return this (for method chaining)
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public Configuration addAnnotatedClass(Class annotatedClass) {
 		XClass xClass = reflectionManager.toXClass( annotatedClass );
 		metadataSourceQueue.add( xClass );
 		return this;
 	}
 
 	/**
 	 * Read package-level metadata.
 	 *
 	 * @param packageName java package name
 	 *
 	 * @return this (for method chaining)
 	 *
 	 * @throws MappingException in case there is an error in the mapping data
 	 */
 	public Configuration addPackage(String packageName) throws MappingException {
 		LOG.debugf( "Mapping Package %s", packageName );
 		try {
 			AnnotationBinder.bindPackage( packageName, createMappings() );
 			return this;
 		}
 		catch ( MappingException me ) {
 			LOG.unableToParseMetadata( packageName );
 			throw me;
 		}
 	}
 
 	/**
 	 * Read all mappings from a jar file
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param jar a jar file
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the jar file or
 	 * processing the contained mapping documents.
 	 */
 	public Configuration addJar(File jar) throws MappingException {
 		LOG.searchingForMappingDocuments( jar.getName() );
 		JarFile jarFile = null;
 		try {
 			try {
 				jarFile = new JarFile( jar );
 			}
 			catch (IOException ioe) {
 				throw new InvalidMappingException(
 						"Could not read mapping documents from jar: " + jar.getName(), "jar", jar.getName(),
 						ioe
 				);
 			}
 			Enumeration jarEntries = jarFile.entries();
 			while ( jarEntries.hasMoreElements() ) {
 				ZipEntry ze = (ZipEntry) jarEntries.nextElement();
 				if ( ze.getName().endsWith( ".hbm.xml" ) ) {
 					LOG.foundMappingDocument( ze.getName() );
 					try {
 						addInputStream( jarFile.getInputStream( ze ) );
 					}
 					catch (Exception e) {
 						throw new InvalidMappingException(
 								"Could not read mapping documents from jar: " + jar.getName(),
 								"jar",
 								jar.getName(),
 								e
 						);
 					}
 				}
 			}
 		}
 		finally {
 			try {
 				if ( jarFile != null ) {
 					jarFile.close();
 				}
 			}
 			catch (IOException ioe) {
 				LOG.unableToCloseJar( ioe.getMessage() );
 			}
 		}
 
 		return this;
 	}
 
 	/**
 	 * Read all mapping documents from a directory tree.
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param dir The directory
 	 * @return this (for method chaining purposes)
 	 * @throws MappingException Indicates problems reading the jar file or
 	 * processing the contained mapping documents.
 	 */
 	public Configuration addDirectory(File dir) throws MappingException {
 		File[] files = dir.listFiles();
 		for ( File file : files ) {
 			if ( file.isDirectory() ) {
 				addDirectory( file );
 			}
 			else if ( file.getName().endsWith( ".hbm.xml" ) ) {
 				addFile( file );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Create a new <tt>Mappings</tt> to add class and collection mappings to.
 	 *
 	 * @return The created mappings
 	 */
 	public Mappings createMappings() {
 		return new MappingsImpl();
 	}
 
 
 	@SuppressWarnings({ "unchecked" })
 	private Iterator<IdentifierGenerator> iterateGenerators(Dialect dialect) throws MappingException {
 
 		TreeMap generators = new TreeMap();
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		for ( PersistentClass pc : classes.values() ) {
 			if ( !pc.isInherited() ) {
 				IdentifierGenerator ig = pc.getIdentifier().createIdentifierGenerator(
 						getIdentifierGeneratorFactory(),
 						dialect,
 						defaultCatalog,
 						defaultSchema,
 						(RootClass) pc
 				);
 
 				if ( ig instanceof PersistentIdentifierGenerator ) {
 					generators.put( ( (PersistentIdentifierGenerator) ig ).generatorKey(), ig );
 				}
 				else if ( ig instanceof IdentifierGeneratorAggregator ) {
 					( (IdentifierGeneratorAggregator) ig ).registerPersistentGenerators( generators );
 				}
 			}
 		}
 
 		for ( Collection collection : collections.values() ) {
 			if ( collection.isIdentified() ) {
 				IdentifierGenerator ig = ( ( IdentifierCollection ) collection ).getIdentifier().createIdentifierGenerator(
 						getIdentifierGeneratorFactory(),
 						dialect,
 						defaultCatalog,
 						defaultSchema,
 						null
 				);
 
 				if ( ig instanceof PersistentIdentifierGenerator ) {
 					generators.put( ( (PersistentIdentifierGenerator) ig ).generatorKey(), ig );
 				}
 			}
 		}
 
 		return generators.values().iterator();
 	}
 
 	/**
 	 * Generate DDL for dropping tables
 	 *
 	 * @param dialect The dialect for which to generate the drop script
 
 	 * @return The sequence of DDL commands to drop the schema objects
 
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	public String[] generateDropSchemaScript(Dialect dialect) throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 
 		// drop them in reverse order in case db needs it done that way...
 		{
 			ListIterator itr = auxiliaryDatabaseObjects.listIterator( auxiliaryDatabaseObjects.size() );
 			while ( itr.hasPrevious() ) {
 				AuxiliaryDatabaseObject object = (AuxiliaryDatabaseObject) itr.previous();
 				if ( object.appliesToDialect( dialect ) ) {
 					script.add( object.sqlDropString( dialect, defaultCatalog, defaultSchema ) );
 				}
 			}
 		}
 
 		if ( dialect.dropConstraints() ) {
 			Iterator itr = getTableMappings();
 			while ( itr.hasNext() ) {
 				Table table = (Table) itr.next();
 				if ( table.isPhysicalTable() ) {
 					Iterator subItr = table.getForeignKeyIterator();
 					while ( subItr.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subItr.next();
 						if ( fk.isPhysicalConstraint() ) {
 							script.add(
 									fk.sqlDropString(
 											dialect,
 											defaultCatalog,
 											defaultSchema
 										)
 								);
 						}
 					}
 				}
 			}
 		}
 
 
 		Iterator itr = getTableMappings();
 		while ( itr.hasNext() ) {
 
 			Table table = (Table) itr.next();
 			if ( table.isPhysicalTable() ) {
 
 				/*Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					Index index = (Index) subIter.next();
 					if ( !index.isForeignKey() || !dialect.hasImplicitIndexForForeignKey() ) {
 						script.add( index.sqlDropString(dialect) );
 					}
 				}*/
 
 				script.add(
 						table.sqlDropString(
 								dialect,
 								defaultCatalog,
 								defaultSchema
 							)
 					);
 
 			}
 
 		}
 
 		itr = iterateGenerators( dialect );
 		while ( itr.hasNext() ) {
 			String[] lines = ( (PersistentIdentifierGenerator) itr.next() ).sqlDropStrings( dialect );
 			script.addAll( Arrays.asList( lines ) );
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	/**
 	 * @param dialect The dialect for which to generate the creation script
 	 *
 	 * @return The sequence of DDL commands to create the schema objects
 	 *
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 	 *
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public String[] generateSchemaCreationScript(Dialect dialect) throws HibernateException {
 		secondPassCompile();
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 				script.add(
 						table.sqlCreateString(
 								dialect,
 								mapping,
 								defaultCatalog,
 								defaultSchema
 							)
 					);
 				Iterator<String> comments = table.sqlCommentStrings( dialect, defaultCatalog, defaultSchema );
 				while ( comments.hasNext() ) {
 					script.add( comments.next() );
 				}
 			}
 		}
 
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 				if ( !dialect.supportsUniqueConstraintInCreateAlterTable() ) {
 					Iterator subIter = table.getUniqueKeyIterator();
 					while ( subIter.hasNext() ) {
 						UniqueKey uk = (UniqueKey) subIter.next();
 						String constraintString = uk.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema );
 						if (constraintString != null) script.add( constraintString );
 					}
 				}
 
 
 				Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					Index index = (Index) subIter.next();
 					script.add(
 							index.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 								)
 						);
 				}
 
 				if ( dialect.hasAlterTable() ) {
 					subIter = table.getForeignKeyIterator();
 					while ( subIter.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subIter.next();
 						if ( fk.isPhysicalConstraint() ) {
 							script.add(
 									fk.sqlCreateString(
 											dialect, mapping,
 											defaultCatalog,
 											defaultSchema
 										)
 								);
 						}
 					}
 				}
 
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			String[] lines = ( (PersistentIdentifierGenerator) iter.next() ).sqlCreateStrings( dialect );
 			script.addAll( Arrays.asList( lines ) );
 		}
 
 		for ( AuxiliaryDatabaseObject auxiliaryDatabaseObject : auxiliaryDatabaseObjects ) {
 			if ( auxiliaryDatabaseObject.appliesToDialect( dialect ) ) {
 				script.add( auxiliaryDatabaseObject.sqlCreateString( dialect, mapping, defaultCatalog, defaultSchema ) );
 			}
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	/**
 	 * @param dialect The dialect for which to generate the creation script
 	 * @param databaseMetadata The database catalog information for the database to be updated; needed to work out what
 	 * should be created/altered
 	 *
 	 * @return The sequence of DDL commands to apply the schema objects
 	 *
 	 * @throws HibernateException Generally indicates a problem calling {@link #buildMappings()}
 	 *
 	 * @see org.hibernate.tool.hbm2ddl.SchemaExport
 	 */
 	@SuppressWarnings({ "unchecked" })
 	public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata)
 			throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		ArrayList<String> script = new ArrayList<String>( 50 );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						( table.getSchema() == null ) ? defaultSchema : table.getSchema(),
 						( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog(),
 								table.isQuoted()
 
 					);
 				if ( tableInfo == null ) {
 					script.add(
 							table.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 								)
 						);
 				}
 				else {
 					Iterator<String> subiter = table.sqlAlterStrings(
 							dialect,
 							mapping,
 							tableInfo,
 							defaultCatalog,
 							defaultSchema
 						);
 					while ( subiter.hasNext() ) {
 						script.add( subiter.next() );
 					}
 				}
 
 				Iterator<String> comments = table.sqlCommentStrings( dialect, defaultCatalog, defaultSchema );
 				while ( comments.hasNext() ) {
 					script.add( comments.next() );
 				}
 
 			}
 		}
 
 		iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						table.getSchema(),
 						table.getCatalog(),
 						table.isQuoted()
 					);
 
 				if ( dialect.hasAlterTable() ) {
 					Iterator subIter = table.getForeignKeyIterator();
 					while ( subIter.hasNext() ) {
 						ForeignKey fk = (ForeignKey) subIter.next();
 						if ( fk.isPhysicalConstraint() ) {
 							boolean create = tableInfo == null || (
 									tableInfo.getForeignKeyMetadata( fk ) == null && (
 											//Icky workaround for MySQL bug:
 											!( dialect instanceof MySQLDialect ) ||
 													tableInfo.getIndexMetadata( fk.getName() ) == null
 										)
 								);
 							if ( create ) {
 								script.add(
 										fk.sqlCreateString(
 												dialect,
 												mapping,
 												defaultCatalog,
 												defaultSchema
 											)
 									);
 							}
 						}
 					}
 				}
 
 				Iterator subIter = table.getIndexIterator();
 				while ( subIter.hasNext() ) {
 					final Index index = (Index) subIter.next();
 					// Skip if index already exists
 					if ( tableInfo != null && StringHelper.isNotEmpty( index.getName() ) ) {
 						final IndexMetadata meta = tableInfo.getIndexMetadata( index.getName() );
 						if ( meta != null ) {
 							continue;
 						}
 					}
 					script.add(
 							index.sqlCreateString(
 									dialect,
 									mapping,
 									defaultCatalog,
 									defaultSchema
 							)
 					);
 				}
 
 //broken, 'cos we don't generate these with names in SchemaExport
 //				subIter = table.getUniqueKeyIterator();
 //				while ( subIter.hasNext() ) {
 //					UniqueKey uk = (UniqueKey) subIter.next();
 //					if ( tableInfo==null || tableInfo.getIndexMetadata( uk.getFilterName() ) == null ) {
 //						script.add( uk.sqlCreateString(dialect, mapping) );
 //					}
 //				}
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				String[] lines = generator.sqlCreateStrings( dialect );
 				script.addAll( Arrays.asList( lines ) );
 			}
 		}
 
 		return ArrayHelper.toStringArray( script );
 	}
 
 	public void validateSchema(Dialect dialect, DatabaseMetadata databaseMetadata)throws HibernateException {
 		secondPassCompile();
 
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 
 		Iterator iter = getTableMappings();
 		while ( iter.hasNext() ) {
 			Table table = (Table) iter.next();
 			if ( table.isPhysicalTable() ) {
 
 
 				TableMetadata tableInfo = databaseMetadata.getTableMetadata(
 						table.getName(),
 						( table.getSchema() == null ) ? defaultSchema : table.getSchema(),
 						( table.getCatalog() == null ) ? defaultCatalog : table.getCatalog(),
 								table.isQuoted());
 				if ( tableInfo == null ) {
 					throw new HibernateException( "Missing table: " + table.getName() );
 				}
 				else {
 					table.validateColumns( dialect, mapping, tableInfo );
 				}
 
 			}
 		}
 
 		iter = iterateGenerators( dialect );
 		while ( iter.hasNext() ) {
 			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
 			Object key = generator.generatorKey();
 			if ( !databaseMetadata.isSequence( key ) && !databaseMetadata.isTable( key ) ) {
 				throw new HibernateException( "Missing sequence or table: " + key );
 			}
 		}
 	}
 
 	private void validate() throws MappingException {
 		Iterator iter = classes.values().iterator();
 		while ( iter.hasNext() ) {
 			( (PersistentClass) iter.next() ).validate( mapping );
 		}
 		iter = collections.values().iterator();
 		while ( iter.hasNext() ) {
 			( (Collection) iter.next() ).validate( mapping );
 		}
 	}
 
 	/**
 	 * Call this to ensure the mappings are fully compiled/built. Usefull to ensure getting
 	 * access to all information in the metamodel when calling e.g. getClassMappings().
 	 */
 	public void buildMappings() {
 		secondPassCompile();
 	}
 
 	protected void secondPassCompile() throws MappingException {
 		LOG.trace( "Starting secondPassCompile() processing" );
 
 		//process default values first
 		{
 			if ( !isDefaultProcessed ) {
 				//use global delimiters if orm.xml declare it
 				Map defaults = reflectionManager.getDefaults();
 				final Object isDelimited = defaults.get( "delimited-identifier" );
 				if ( isDelimited != null && isDelimited == Boolean.TRUE ) {
 					getProperties().put( Environment.GLOBALLY_QUOTED_IDENTIFIERS, "true" );
 				}
 				// Set default schema name if orm.xml declares it.
 				final String schema = (String) defaults.get( "schema" );
 				if ( StringHelper.isNotEmpty( schema ) ) {
 					getProperties().put( Environment.DEFAULT_SCHEMA, schema );
 				}
 				// Set default catalog name if orm.xml declares it.
 				final String catalog = (String) defaults.get( "catalog" );
 				if ( StringHelper.isNotEmpty( catalog ) ) {
 					getProperties().put( Environment.DEFAULT_CATALOG, catalog );
 				}
 
 				AnnotationBinder.bindDefaults( createMappings() );
 				isDefaultProcessed = true;
 			}
 		}
 
 		// process metadata queue
 		{
 			metadataSourceQueue.syncAnnotatedClasses();
 			metadataSourceQueue.processMetadata( determineMetadataSourcePrecedence() );
 		}
 
 		// process cache queue
 		{
 			for ( CacheHolder holder : caches ) {
 				if ( holder.isClass ) {
 					applyCacheConcurrencyStrategy( holder );
 				}
 				else {
 					applyCollectionCacheConcurrencyStrategy( holder );
 				}
 			}
 			caches.clear();
 		}
 
 		try {
 			inSecondPass = true;
 			processSecondPassesOfType( PkDrivenByDefaultMapsIdSecondPass.class );
 			processSecondPassesOfType( SetSimpleValueTypeSecondPass.class );
 			processSecondPassesOfType( CopyIdentifierComponentSecondPass.class );
 			processFkSecondPassInOrder();
 			processSecondPassesOfType( CreateKeySecondPass.class );
 			processSecondPassesOfType( SecondaryTableSecondPass.class );
 
 			originalSecondPassCompile();
 
 			inSecondPass = false;
 		}
 		catch ( RecoverableException e ) {
 			//the exception was not recoverable after all
 			throw ( RuntimeException ) e.getCause();
 		}
 
 		for ( Map.Entry<Table, List<UniqueConstraintHolder>> tableListEntry : uniqueConstraintHoldersByTable.entrySet() ) {
 			final Table table = tableListEntry.getKey();
 			final List<UniqueConstraintHolder> uniqueConstraints = tableListEntry.getValue();
 			int uniqueIndexPerTable = 0;
 			for ( UniqueConstraintHolder holder : uniqueConstraints ) {
 				uniqueIndexPerTable++;
 				final String keyName = StringHelper.isEmpty( holder.getName() )
 						? "key" + uniqueIndexPerTable
 						: holder.getName();
 				buildUniqueKeyFromColumnNames( table, keyName, holder.getColumns() );
 			}
 		}
 	}
 
 	private void processSecondPassesOfType(Class<? extends SecondPass> type) {
 		Iterator iter = secondPasses.iterator();
 		while ( iter.hasNext() ) {
 			SecondPass sp = ( SecondPass ) iter.next();
 			//do the second pass of simple value types first and remove them
 			if ( type.isInstance( sp ) ) {
 				sp.doSecondPass( classes );
 				iter.remove();
 			}
 		}
 	}
 
 	/**
 	 * Processes FKSecondPass instances trying to resolve any
 	 * graph circularity (ie PK made of a many to one linking to
 	 * an entity having a PK made of a ManyToOne ...).
 	 */
 	private void processFkSecondPassInOrder() {
 		LOG.debug("Processing fk mappings (*ToOne and JoinedSubclass)");
 		List<FkSecondPass> fkSecondPasses = getFKSecondPassesOnly();
 
 		if ( fkSecondPasses.size() == 0 ) {
 			return; // nothing to do here
 		}
 
 		// split FkSecondPass instances into primary key and non primary key FKs.
 		// While doing so build a map of class names to FkSecondPass instances depending on this class.
 		Map<String, Set<FkSecondPass>> isADependencyOf = new HashMap<String, Set<FkSecondPass>>();
 		List<FkSecondPass> endOfQueueFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( FkSecondPass sp : fkSecondPasses ) {
 			if ( sp.isInPrimaryKey() ) {
 				String referenceEntityName = sp.getReferencedEntityName();
 				PersistentClass classMapping = getClassMapping( referenceEntityName );
 				String dependentTable = classMapping.getTable().getQuotedName();
 				if ( !isADependencyOf.containsKey( dependentTable ) ) {
 					isADependencyOf.put( dependentTable, new HashSet<FkSecondPass>() );
 				}
 				isADependencyOf.get( dependentTable ).add( sp );
 			}
 			else {
 				endOfQueueFkSecondPasses.add( sp );
 			}
 		}
 
 		// using the isADependencyOf map we order the FkSecondPass recursively instances into the right order for processing
 		List<FkSecondPass> orderedFkSecondPasses = new ArrayList<FkSecondPass>( fkSecondPasses.size() );
 		for ( String tableName : isADependencyOf.keySet() ) {
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, tableName, tableName );
 		}
 
 		// process the ordered FkSecondPasses
 		for ( FkSecondPass sp : orderedFkSecondPasses ) {
 			sp.doSecondPass( classes );
 		}
 
 		processEndOfQueue( endOfQueueFkSecondPasses );
 	}
 
 	/**
 	 * @return Returns a list of all <code>secondPasses</code> instances which are a instance of
 	 *         <code>FkSecondPass</code>.
 	 */
 	private List<FkSecondPass> getFKSecondPassesOnly() {
 		Iterator iter = secondPasses.iterator();
 		List<FkSecondPass> fkSecondPasses = new ArrayList<FkSecondPass>( secondPasses.size() );
 		while ( iter.hasNext() ) {
 			SecondPass sp = ( SecondPass ) iter.next();
 			//do the second pass of fk before the others and remove them
 			if ( sp instanceof FkSecondPass ) {
 				fkSecondPasses.add( ( FkSecondPass ) sp );
 				iter.remove();
 			}
 		}
 		return fkSecondPasses;
 	}
 
 	/**
 	 * Recursively builds a list of FkSecondPass instances ready to be processed in this order.
 	 * Checking all dependencies recursively seems quite expensive, but the original code just relied
 	 * on some sort of table name sorting which failed in certain circumstances.
 	 * <p/>
 	 * See <tt>ANN-722</tt> and <tt>ANN-730</tt>
 	 *
 	 * @param orderedFkSecondPasses The list containing the <code>FkSecondPass<code> instances ready
 	 * for processing.
 	 * @param isADependencyOf Our lookup data structure to determine dependencies between tables
 	 * @param startTable Table name to start recursive algorithm.
 	 * @param currentTable The current table name used to check for 'new' dependencies.
 	 */
 	private void buildRecursiveOrderedFkSecondPasses(
 			List<FkSecondPass> orderedFkSecondPasses,
 			Map<String, Set<FkSecondPass>> isADependencyOf,
 			String startTable,
 			String currentTable) {
 
 		Set<FkSecondPass> dependencies = isADependencyOf.get( currentTable );
 
 		// bottom out
 		if ( dependencies == null || dependencies.size() == 0 ) {
 			return;
 		}
 
 		for ( FkSecondPass sp : dependencies ) {
 			String dependentTable = sp.getValue().getTable().getQuotedName();
 			if ( dependentTable.compareTo( startTable ) == 0 ) {
 				StringBuilder sb = new StringBuilder(
 						"Foreign key circularity dependency involving the following tables: "
 				);
 				throw new AnnotationException( sb.toString() );
 			}
 			buildRecursiveOrderedFkSecondPasses( orderedFkSecondPasses, isADependencyOf, startTable, dependentTable );
 			if ( !orderedFkSecondPasses.contains( sp ) ) {
 				orderedFkSecondPasses.add( 0, sp );
 			}
 		}
 	}
 
 	private void processEndOfQueue(List<FkSecondPass> endOfQueueFkSecondPasses) {
 		/*
 		 * If a second pass raises a recoverableException, queue it for next round
 		 * stop of no pass has to be processed or if the number of pass to processes
 		 * does not diminish between two rounds.
 		 * If some failing pass remain, raise the original exception
 		 */
 		boolean stopProcess = false;
 		RuntimeException originalException = null;
 		while ( !stopProcess ) {
 			List<FkSecondPass> failingSecondPasses = new ArrayList<FkSecondPass>();
-			Iterator<FkSecondPass> it = endOfQueueFkSecondPasses.listIterator();
-			while ( it.hasNext() ) {
-				final FkSecondPass pass = it.next();
+			for ( FkSecondPass pass : endOfQueueFkSecondPasses ) {
 				try {
 					pass.doSecondPass( classes );
 				}
-				catch ( RecoverableException e ) {
+				catch (RecoverableException e) {
 					failingSecondPasses.add( pass );
 					if ( originalException == null ) {
-						originalException = ( RuntimeException ) e.getCause();
+						originalException = (RuntimeException) e.getCause();
 					}
 				}
 			}
 			stopProcess = failingSecondPasses.size() == 0 || failingSecondPasses.size() == endOfQueueFkSecondPasses.size();
 			endOfQueueFkSecondPasses = failingSecondPasses;
 		}
 		if ( endOfQueueFkSecondPasses.size() > 0 ) {
 			throw originalException;
 		}
 	}
 
 	private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames) {
 		keyName = normalizer.normalizeIdentifierQuoting( keyName );
 
 		UniqueKey uc;
 		int size = columnNames.length;
 		Column[] columns = new Column[size];
 		Set<Column> unbound = new HashSet<Column>();
 		Set<Column> unboundNoLogical = new HashSet<Column>();
 		for ( int index = 0; index < size; index++ ) {
 			final String logicalColumnName = normalizer.normalizeIdentifierQuoting( columnNames[index] );
 			try {
 				final String columnName = createMappings().getPhysicalColumnName( logicalColumnName, table );
 				columns[index] = new Column( columnName );
 				unbound.add( columns[index] );
 				//column equals and hashcode is based on column name
 			}
 			catch ( MappingException e ) {
 				unboundNoLogical.add( new Column( logicalColumnName ) );
 			}
 		}
 		for ( Column column : columns ) {
 			if ( table.containsColumn( column ) ) {
 				uc = table.getOrCreateUniqueKey( keyName );
 				uc.addColumn( table.getColumn( column ) );
 				unbound.remove( column );
 			}
 		}
 		if ( unbound.size() > 0 || unboundNoLogical.size() > 0 ) {
 			StringBuilder sb = new StringBuilder( "Unable to create unique key constraint (" );
 			for ( String columnName : columnNames ) {
 				sb.append( columnName ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( ") on table " ).append( table.getName() ).append( ": database column " );
 			for ( Column column : unbound ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			for ( Column column : unboundNoLogical ) {
 				sb.append( column.getName() ).append( ", " );
 			}
 			sb.setLength( sb.length() - 2 );
 			sb.append( " not found. Make sure that you use the correct column name which depends on the naming strategy in use (it may not be the same as the property name in the entity, especially for relational types)" );
 			throw new AnnotationException( sb.toString() );
 		}
 	}
 
 	private void originalSecondPassCompile() throws MappingException {
 		LOG.debug( "Processing extends queue" );
 		processExtendsQueue();
 
 		LOG.debug( "Processing collection mappings" );
 		Iterator itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			if ( ! (sp instanceof QuerySecondPass) ) {
 				sp.doSecondPass( classes );
 				itr.remove();
 			}
 		}
 
 		LOG.debug( "Processing native query and ResultSetMapping mappings" );
 		itr = secondPasses.iterator();
 		while ( itr.hasNext() ) {
 			SecondPass sp = (SecondPass) itr.next();
 			sp.doSecondPass( classes );
 			itr.remove();
 		}
 
 		LOG.debug( "Processing association property references" );
 
 		itr = propertyReferences.iterator();
 		while ( itr.hasNext() ) {
 			Mappings.PropertyReference upr = (Mappings.PropertyReference) itr.next();
 
 			PersistentClass clazz = getClassMapping( upr.referencedClass );
 			if ( clazz == null ) {
 				throw new MappingException(
 						"property-ref to unmapped class: " +
 						upr.referencedClass
 					);
 			}
 
 			Property prop = clazz.getReferencedProperty( upr.propertyName );
 			if ( upr.unique ) {
 				( (SimpleValue) prop.getValue() ).setAlternateUniqueKey( true );
 			}
 		}
 
 		//TODO: Somehow add the newly created foreign keys to the internal collection
 
 		LOG.debug( "Processing foreign key constraints" );
 
 		itr = getTableMappings();
-		Set done = new HashSet();
+		Set<ForeignKey> done = new HashSet<ForeignKey>();
 		while ( itr.hasNext() ) {
 			secondPassCompileForeignKeys( (Table) itr.next(), done );
 		}
 
 	}
 
 	private int processExtendsQueue() {
 		LOG.debug( "Processing extends queue" );
 		int added = 0;
 		ExtendsQueueEntry extendsQueueEntry = findPossibleExtends();
 		while ( extendsQueueEntry != null ) {
 			metadataSourceQueue.processHbmXml( extendsQueueEntry.getMetadataXml(), extendsQueueEntry.getEntityNames() );
 			extendsQueueEntry = findPossibleExtends();
 		}
 
 		if ( extendsQueue.size() > 0 ) {
 			Iterator iterator = extendsQueue.keySet().iterator();
-			StringBuffer buf = new StringBuffer( "Following super classes referenced in extends not found: " );
+			StringBuilder buf = new StringBuilder( "Following super classes referenced in extends not found: " );
 			while ( iterator.hasNext() ) {
 				final ExtendsQueueEntry entry = ( ExtendsQueueEntry ) iterator.next();
 				buf.append( entry.getExplicitName() );
 				if ( entry.getMappingPackage() != null ) {
 					buf.append( "[" ).append( entry.getMappingPackage() ).append( "]" );
 				}
 				if ( iterator.hasNext() ) {
 					buf.append( "," );
 				}
 			}
 			throw new MappingException( buf.toString() );
 		}
 
 		return added;
 	}
 
 	protected ExtendsQueueEntry findPossibleExtends() {
 		Iterator<ExtendsQueueEntry> itr = extendsQueue.keySet().iterator();
 		while ( itr.hasNext() ) {
 			final ExtendsQueueEntry entry = itr.next();
 			boolean found = getClassMapping( entry.getExplicitName() ) != null
 					|| getClassMapping( HbmBinder.getClassName( entry.getExplicitName(), entry.getMappingPackage() ) ) != null;
 			if ( found ) {
 				itr.remove();
 				return entry;
 			}
 		}
 		return null;
 	}
 
-	protected void secondPassCompileForeignKeys(Table table, Set done) throws MappingException {
+	protected void secondPassCompileForeignKeys(Table table, Set<ForeignKey> done) throws MappingException {
 		table.createForeignKeys();
 		Iterator iter = table.getForeignKeyIterator();
 		while ( iter.hasNext() ) {
 
 			ForeignKey fk = (ForeignKey) iter.next();
 			if ( !done.contains( fk ) ) {
 				done.add( fk );
 				final String referencedEntityName = fk.getReferencedEntityName();
 				if ( referencedEntityName == null ) {
 					throw new MappingException(
 							"An association from the table " +
 							fk.getTable().getName() +
 							" does not specify the referenced entity"
 						);
 				}
 				LOG.debugf( "Resolving reference to class: %s", referencedEntityName );
 				PersistentClass referencedClass = classes.get( referencedEntityName );
 				if ( referencedClass == null ) {
 					throw new MappingException(
 							"An association from the table " +
 							fk.getTable().getName() +
 							" refers to an unmapped class: " +
 							referencedEntityName
 						);
 				}
 				if ( referencedClass.isJoinedSubclass() ) {
 					secondPassCompileForeignKeys( referencedClass.getSuperclass().getTable(), done );
 				}
 				fk.setReferencedTable( referencedClass.getTable() );
 				fk.alignColumns();
 			}
 		}
 	}
 
 	public Map<String, NamedQueryDefinition> getNamedQueries() {
 		return namedQueries;
 	}
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
-	 * @return The build {@link SessionFactory}
+	 * @param serviceRegistry The registry of services to be used in creating this session factory.
+	 *
+	 * @return The built {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 */
 	public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
 		LOG.debugf( "Preparing to build session factory with filters : %s", filterDefinitions );
 
 		secondPassCompile();
 		if ( !metadataSourceQueue.isEmpty() ) {
 			LOG.incompleteMappingMetadataCacheProcessing();
 		}
 
 		validate();
 
 		Environment.verifyProperties( properties );
 		Properties copy = new Properties();
 		copy.putAll( properties );
 		ConfigurationHelper.resolvePlaceHolders( copy );
 		Settings settings = buildSettings( copy, serviceRegistry );
 
 		return new SessionFactoryImpl(
 				this,
 				mapping,
 				serviceRegistry,
 				settings,
 				sessionFactoryObserver
 			);
 	}
 
 	/**
 	 * Create a {@link SessionFactory} using the properties and mappings in this configuration. The
 	 * {@link SessionFactory} will be immutable, so changes made to {@code this} {@link Configuration} after
 	 * building the {@link SessionFactory} will not affect it.
 	 *
 	 * @return The build {@link SessionFactory}
 	 *
 	 * @throws HibernateException usually indicates an invalid configuration or invalid mapping information
 	 *
 	 * @deprecated Use {@link #buildSessionFactory(ServiceRegistry)} instead
 	 */
 	public SessionFactory buildSessionFactory() throws HibernateException {
 		Environment.verifyProperties( properties );
 		ConfigurationHelper.resolvePlaceHolders( properties );
 		final ServiceRegistry serviceRegistry =  new ServiceRegistryBuilder()
 				.applySettings( properties )
 				.buildServiceRegistry();
 		setSessionFactoryObserver(
 				new SessionFactoryObserver() {
 					@Override
 					public void sessionFactoryCreated(SessionFactory factory) {
 					}
 
 					@Override
 					public void sessionFactoryClosed(SessionFactory factory) {
 						( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
 					}
 				}
 		);
 		return buildSessionFactory( serviceRegistry );
 	}
 
 	/**
-	 * Rterieve the configured {@link Interceptor}.
+	 * Retrieve the configured {@link Interceptor}.
 	 *
 	 * @return The current {@link Interceptor}
 	 */
 	public Interceptor getInterceptor() {
 		return interceptor;
 	}
 
 	/**
 	 * Set the current {@link Interceptor}
 	 *
-	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory) built}
+	 * @param interceptor The {@link Interceptor} to use for the {@link #buildSessionFactory built}
 	 * {@link SessionFactory}.
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setInterceptor(Interceptor interceptor) {
 		this.interceptor = interceptor;
 		return this;
 	}
 
 	/**
 	 * Get all properties
 	 *
 	 * @return all properties
 	 */
 	public Properties getProperties() {
 		return properties;
 	}
 
 	/**
 	 * Get a property value by name
 	 *
 	 * @param propertyName The name of the property
 	 *
 	 * @return The value curently associated with that property name; may be null.
 	 */
 	public String getProperty(String propertyName) {
 		return properties.getProperty( propertyName );
 	}
 
 	/**
 	 * Specify a completely new set of properties
 	 *
 	 * @param properties The new set of properties
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setProperties(Properties properties) {
 		this.properties = properties;
 		return this;
 	}
 
 	/**
 	 * Add the given properties to ours.
 	 *
 	 * @param extraProperties The properties to add.
 	 *
 	 * @return this for method chaining
 	 *
 	 */
 	public Configuration addProperties(Properties extraProperties) {
 		this.properties.putAll( extraProperties );
 		return this;
 	}
 
 	/**
 	 * Adds the incoming properties to the internal properties structure, as long as the internal structure does not
 	 * already contain an entry for the given key.
 	 *
 	 * @param properties The properties to merge
 	 *
 	 * @return this for ethod chaining
 	 */
 	public Configuration mergeProperties(Properties properties) {
 		for ( Map.Entry entry : properties.entrySet() ) {
 			if ( this.properties.containsKey( entry.getKey() ) ) {
 				continue;
 			}
 			this.properties.setProperty( (String) entry.getKey(), (String) entry.getValue() );
 		}
 		return this;
 	}
 
 	/**
 	 * Set a property value by name
 	 *
 	 * @param propertyName The name of the property to set
 	 * @param value The new property value
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setProperty(String propertyName, String value) {
 		properties.setProperty( propertyName, value );
 		return this;
 	}
 
 	private void addProperties(Element parent) {
 		Iterator itr = parent.elementIterator( "property" );
 		while ( itr.hasNext() ) {
 			Element node = (Element) itr.next();
 			String name = node.attributeValue( "name" );
 			String value = node.getText().trim();
 			LOG.debugf( "%s=%s", name, value );
 			properties.setProperty( name, value );
 			if ( !name.startsWith( "hibernate" ) ) {
 				properties.setProperty( "hibernate." + name, value );
 			}
 		}
 		Environment.verifyProperties( properties );
 	}
 
 	/**
 	 * Use the mappings and properties specified in an application resource named <tt>hibernate.cfg.xml</tt>.
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find <tt>hibernate.cfg.xml</tt>
 	 *
 	 * @see #configure(String)
 	 */
 	public Configuration configure() throws HibernateException {
 		configure( "/hibernate.cfg.xml" );
 		return this;
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application resource. The format of the resource is
 	 * defined in <tt>hibernate-configuration-3.0.dtd</tt>.
 	 * <p/>
 	 * The resource is found via {@link #getConfigurationInputStream}
 	 *
 	 * @param resource The resource to use
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(String resource) throws HibernateException {
 		LOG.configuringFromResource( resource );
 		InputStream stream = getConfigurationInputStream( resource );
 		return doConfigure( stream, resource );
 	}
 
 	/**
 	 * Get the configuration file as an <tt>InputStream</tt>. Might be overridden
 	 * by subclasses to allow the configuration to be located by some arbitrary
 	 * mechanism.
 	 * <p/>
 	 * By default here we use classpath resource resolution
 	 *
 	 * @param resource The resource to locate
 	 *
 	 * @return The stream
 	 *
 	 * @throws HibernateException Generally indicates we cannot find the named resource
 	 */
 	protected InputStream getConfigurationInputStream(String resource) throws HibernateException {
 		LOG.configurationResource( resource );
 		return ConfigHelper.getResourceAsStream( resource );
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given document. The format of the document is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param url URL from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the url
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(URL url) throws HibernateException {
 		LOG.configuringFromUrl( url );
 		try {
 			return doConfigure( url.openStream(), url.toString() );
 		}
 		catch (IOException ioe) {
 			throw new HibernateException( "could not configure from URL: " + url, ioe );
 		}
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given application file. The format of the file is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param configFile File from which you wish to load the configuration
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Generally indicates a problem access the file
 	 *
 	 * @see #doConfigure(java.io.InputStream, String)
 	 */
 	public Configuration configure(File configFile) throws HibernateException {
 		LOG.configuringFromFile( configFile.getName() );
 		try {
 			return doConfigure( new FileInputStream( configFile ), configFile.toString() );
 		}
 		catch (FileNotFoundException fnfe) {
 			throw new HibernateException( "could not find file: " + configFile, fnfe );
 		}
 	}
 
 	/**
 	 * Configure this configuration's state from the contents of the given input stream.  The expectation is that
 	 * the stream contents represent an XML document conforming to the Hibernate Configuration DTD.  See
 	 * {@link #doConfigure(Document)} for further details.
 	 *
 	 * @param stream The input stream from which to read
 	 * @param resourceName The name to use in warning/error messages
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Indicates a problem reading the stream contents.
 	 */
 	protected Configuration doConfigure(InputStream stream, String resourceName) throws HibernateException {
 		try {
 			List errors = new ArrayList();
 			Document document = xmlHelper.createSAXReader( resourceName, errors, entityResolver )
 					.read( new InputSource( stream ) );
 			if ( errors.size() != 0 ) {
 				throw new MappingException( "invalid configuration", (Throwable) errors.get( 0 ) );
 			}
 			doConfigure( document );
 		}
 		catch (DocumentException e) {
 			throw new HibernateException( "Could not parse configuration: " + resourceName, e );
 		}
 		finally {
 			try {
 				stream.close();
 			}
 			catch (IOException ioe) {
 				LOG.unableToCloseInputStreamForResource( resourceName, ioe );
 			}
 		}
 		return this;
 	}
 
 	/**
 	 * Use the mappings and properties specified in the given XML document.
 	 * The format of the file is defined in
 	 * <tt>hibernate-configuration-3.0.dtd</tt>.
 	 *
 	 * @param document an XML document from which you wish to load the configuration
 	 * @return A configuration configured via the <tt>Document</tt>
 	 * @throws HibernateException if there is problem in accessing the file.
 	 */
 	public Configuration configure(org.w3c.dom.Document document) throws HibernateException {
 		LOG.configuringFromXmlDocument();
 		return doConfigure( xmlHelper.createDOMReader().read( document ) );
 	}
 
 	/**
 	 * Parse a dom4j document conforming to the Hibernate Configuration DTD (<tt>hibernate-configuration-3.0.dtd</tt>)
 	 * and use its information to configure this {@link Configuration}'s state
 	 *
 	 * @param doc The dom4j document
 	 *
 	 * @return this for method chaining
 	 *
 	 * @throws HibernateException Indicates a problem performing the configuration task
 	 */
 	protected Configuration doConfigure(Document doc) throws HibernateException {
 		Element sfNode = doc.getRootElement().element( "session-factory" );
 		String name = sfNode.attributeValue( "name" );
 		if ( name != null ) {
 			properties.setProperty( Environment.SESSION_FACTORY_NAME, name );
 		}
 		addProperties( sfNode );
 		parseSessionFactory( sfNode, name );
 
 		Element secNode = doc.getRootElement().element( "security" );
 		if ( secNode != null ) {
 			parseSecurity( secNode );
 		}
 
 		LOG.configuredSessionFactory( name );
 		LOG.debugf( "Properties: %s", properties );
 
 		return this;
 	}
 
 
 	private void parseSessionFactory(Element sfNode, String name) {
 		Iterator elements = sfNode.elementIterator();
 		while ( elements.hasNext() ) {
 			Element subelement = (Element) elements.next();
 			String subelementName = subelement.getName();
 			if ( "mapping".equals( subelementName ) ) {
 				parseMappingElement( subelement, name );
 			}
 			else if ( "class-cache".equals( subelementName ) ) {
 				String className = subelement.attributeValue( "class" );
 				Attribute regionNode = subelement.attribute( "region" );
 				final String region = ( regionNode == null ) ? className : regionNode.getValue();
 				boolean includeLazy = !"non-lazy".equals( subelement.attributeValue( "include" ) );
 				setCacheConcurrencyStrategy( className, subelement.attributeValue( "usage" ), region, includeLazy );
 			}
 			else if ( "collection-cache".equals( subelementName ) ) {
 				String role = subelement.attributeValue( "collection" );
 				Attribute regionNode = subelement.attribute( "region" );
 				final String region = ( regionNode == null ) ? role : regionNode.getValue();
 				setCollectionCacheConcurrencyStrategy( role, subelement.attributeValue( "usage" ), region );
 			}
 		}
 	}
 
 	private void parseMappingElement(Element mappingElement, String name) {
 		final Attribute resourceAttribute = mappingElement.attribute( "resource" );
 		final Attribute fileAttribute = mappingElement.attribute( "file" );
 		final Attribute jarAttribute = mappingElement.attribute( "jar" );
 		final Attribute packageAttribute = mappingElement.attribute( "package" );
 		final Attribute classAttribute = mappingElement.attribute( "class" );
 
 		if ( resourceAttribute != null ) {
 			final String resourceName = resourceAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named resource [%s] for mapping", name, resourceName );
 			addResource( resourceName );
 		}
 		else if ( fileAttribute != null ) {
 			final String fileName = fileAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named file [%s] for mapping", name, fileName );
 			addFile( fileName );
 		}
 		else if ( jarAttribute != null ) {
 			final String jarFileName = jarAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named jar file [%s] for mapping", name, jarFileName );
 			addJar( new File( jarFileName ) );
 		}
 		else if ( packageAttribute != null ) {
 			final String packageName = packageAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named package [%s] for mapping", name, packageName );
 			addPackage( packageName );
 		}
 		else if ( classAttribute != null ) {
 			final String className = classAttribute.getValue();
 			LOG.debugf( "Session-factory config [%s] named class [%s] for mapping", name, className );
 			try {
 				addAnnotatedClass( ReflectHelper.classForName( className ) );
 			}
 			catch ( Exception e ) {
 				throw new MappingException(
 						"Unable to load class [ " + className + "] declared in Hibernate configuration <mapping/> entry",
 						e
 				);
 			}
 		}
 		else {
 			throw new MappingException( "<mapping> element in configuration specifies no known attributes" );
 		}
 	}
 
 	private void parseSecurity(Element secNode) {
 		String contextId = secNode.attributeValue( "context" );
 		setProperty( Environment.JACC_CONTEXTID, contextId );
 		LOG.jaccContextId( contextId );
 		JACCConfiguration jcfg = new JACCConfiguration( contextId );
 		Iterator grantElements = secNode.elementIterator();
 		while ( grantElements.hasNext() ) {
 			Element grantElement = (Element) grantElements.next();
 			String elementName = grantElement.getName();
 			if ( "grant".equals( elementName ) ) {
 				jcfg.addPermission(
 						grantElement.attributeValue( "role" ),
 						grantElement.attributeValue( "entity-name" ),
 						grantElement.attributeValue( "actions" )
 					);
 			}
 		}
 	}
 
 	RootClass getRootClassMapping(String clazz) throws MappingException {
 		try {
 			return (RootClass) getClassMapping( clazz );
 		}
 		catch (ClassCastException cce) {
 			throw new MappingException( "You may only specify a cache for root <class> mappings" );
 		}
 	}
 
 	/**
 	 * Set up a cache for an entity class
 	 *
 	 * @param entityName The name of the entity to which we shoudl associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy) {
 		setCacheConcurrencyStrategy( entityName, concurrencyStrategy, entityName );
 		return this;
 	}
 
 	/**
 	 * Set up a cache for an entity class, giving an explicit region name
 	 *
 	 * @param entityName The name of the entity to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 * @param region The name of the cache region to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCacheConcurrencyStrategy(String entityName, String concurrencyStrategy, String region) {
 		setCacheConcurrencyStrategy( entityName, concurrencyStrategy, region, true );
 		return this;
 	}
 
 	public void setCacheConcurrencyStrategy(
 			String entityName,
 			String concurrencyStrategy,
 			String region,
 			boolean cacheLazyProperty) throws MappingException {
 		caches.add( new CacheHolder( entityName, concurrencyStrategy, region, true, cacheLazyProperty ) );
 	}
 
 	private void applyCacheConcurrencyStrategy(CacheHolder holder) {
 		RootClass rootClass = getRootClassMapping( holder.role );
 		if ( rootClass == null ) {
 			throw new MappingException( "Cannot cache an unknown entity: " + holder.role );
 		}
 		rootClass.setCacheConcurrencyStrategy( holder.usage );
 		rootClass.setCacheRegionName( holder.region );
 		rootClass.setLazyPropertiesCacheable( holder.cacheLazy );
 	}
 
 	/**
 	 * Set up a cache for a collection role
 	 *
 	 * @param collectionRole The name of the collection to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy) {
 		setCollectionCacheConcurrencyStrategy( collectionRole, concurrencyStrategy, collectionRole );
 		return this;
 	}
 
 	/**
 	 * Set up a cache for a collection role, giving an explicit region name
 	 *
 	 * @param collectionRole The name of the collection to which we should associate these cache settings
 	 * @param concurrencyStrategy The cache strategy to use
 	 * @param region The name of the cache region to use
-	 *
-	 * @return this for method chaining
 	 */
 	public void setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy, String region) {
 		caches.add( new CacheHolder( collectionRole, concurrencyStrategy, region, false, false ) );
 	}
 
 	private void applyCollectionCacheConcurrencyStrategy(CacheHolder holder) {
 		Collection collection = getCollectionMapping( holder.role );
 		if ( collection == null ) {
 			throw new MappingException( "Cannot cache an unknown collection: " + holder.role );
 		}
 		collection.setCacheConcurrencyStrategy( holder.usage );
 		collection.setCacheRegionName( holder.region );
 	}
 
 	/**
 	 * Get the query language imports
 	 *
 	 * @return a mapping from "import" names to fully qualified class names
 	 */
 	public Map<String,String> getImports() {
 		return imports;
 	}
 
 	/**
 	 * Create an object-oriented view of the configuration properties
 	 *
+	 * @param serviceRegistry The registry of services to be used in building these settings.
+	 *
 	 * @return The build settings
 	 */
 	public Settings buildSettings(ServiceRegistry serviceRegistry) {
 		Properties clone = ( Properties ) properties.clone();
 		ConfigurationHelper.resolvePlaceHolders( clone );
 		return buildSettingsInternal( clone, serviceRegistry );
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) throws HibernateException {
 		return buildSettingsInternal( props, serviceRegistry );
 	}
 
 	private Settings buildSettingsInternal(Properties props, ServiceRegistry serviceRegistry) {
 		final Settings settings = settingsFactory.buildSettings( props, serviceRegistry );
 		settings.setEntityTuplizerFactory( this.getEntityTuplizerFactory() );
 //		settings.setComponentTuplizerFactory( this.getComponentTuplizerFactory() );
 		return settings;
 	}
 
 	public Map getNamedSQLQueries() {
 		return namedSqlQueries;
 	}
 
 	public Map getSqlResultSetMappings() {
 		return sqlResultSetMappings;
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
 	/**
 	 * Set a custom naming strategy
 	 *
 	 * @param namingStrategy the NamingStrategy to set
 	 *
 	 * @return this for method chaining
 	 */
 	public Configuration setNamingStrategy(NamingStrategy namingStrategy) {
 		this.namingStrategy = namingStrategy;
 		return this;
 	}
 
 	/**
 	 * Retrieve the IdentifierGeneratorFactory in effect for this configuration.
 	 *
 	 * @return This configuration's IdentifierGeneratorFactory.
 	 */
 	public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	public Mapping buildMapping() {
 		return new Mapping() {
 			public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 				return identifierGeneratorFactory;
 			}
 
 			/**
 			 * Returns the identifier type of a mapped class
 			 */
 			public Type getIdentifierType(String entityName) throws MappingException {
 				PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				return pc.getIdentifier().getType();
 			}
 
 			public String getIdentifierPropertyName(String entityName) throws MappingException {
 				final PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				if ( !pc.hasIdentifierProperty() ) {
 					return null;
 				}
 				return pc.getIdentifierProperty().getName();
 			}
 
 			public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 				final PersistentClass pc = classes.get( entityName );
 				if ( pc == null ) {
 					throw new MappingException( "persistent class not known: " + entityName );
 				}
 				Property prop = pc.getReferencedProperty( propertyName );
 				if ( prop == null ) {
 					throw new MappingException(
 							"property not known: " +
 							entityName + '.' + propertyName
 						);
 				}
 				return prop.getType();
 			}
 		};
 	}
 
 	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 		//we need  reflectionManager before reading the other components (MetadataSourceQueue in particular)
 		final MetadataProvider metadataProvider = (MetadataProvider) ois.readObject();
 		this.mapping = buildMapping();
 		xmlHelper = new XMLHelper();
 		createReflectionManager(metadataProvider);
 		ois.defaultReadObject();
 	}
 
 	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
 		//We write MetadataProvider first as we need  reflectionManager before reading the other components
 		final MetadataProvider metadataProvider = ( ( MetadataProviderInjector ) reflectionManager ).getMetadataProvider();
 		out.writeObject( metadataProvider );
 		out.defaultWriteObject();
 	}
 
 	private void createReflectionManager() {
 		createReflectionManager( new JPAMetadataProvider() );
 	}
 
 	private void createReflectionManager(MetadataProvider metadataProvider) {
 		reflectionManager = new JavaReflectionManager();
 		( ( MetadataProviderInjector ) reflectionManager ).setMetadataProvider( metadataProvider );
 	}
 
 	public Map getFilterDefinitions() {
 		return filterDefinitions;
 	}
 
 	public void addFilterDefinition(FilterDefinition definition) {
 		filterDefinitions.put( definition.getFilterName(), definition );
 	}
 
 	public Iterator iterateFetchProfiles() {
 		return fetchProfiles.values().iterator();
 	}
 
 	public void addFetchProfile(FetchProfile fetchProfile) {
 		fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 	}
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
 		auxiliaryDatabaseObjects.add( object );
 	}
 
 	public Map getSqlFunctions() {
 		return sqlFunctions;
 	}
 
 	public void addSqlFunction(String functionName, SQLFunction function) {
 		sqlFunctions.put( functionName, function );
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	/**
 	 * Allows registration of a type into the type registry.  The phrase 'override' in the method name simply
 	 * reminds that registration *potentially* replaces a previously registered type .
 	 *
 	 * @param type The type to register.
 	 */
 	public void registerTypeOverride(BasicType type) {
 		getTypeResolver().registerTypeOverride( type );
 	}
 
 
 	public void registerTypeOverride(UserType type, String[] keys) {
 		getTypeResolver().registerTypeOverride( type, keys );
 	}
 
 	public void registerTypeOverride(CompositeUserType type, String[] keys) {
 		getTypeResolver().registerTypeOverride( type, keys );
 	}
 
 	public SessionFactoryObserver getSessionFactoryObserver() {
 		return sessionFactoryObserver;
 	}
 
 	public void setSessionFactoryObserver(SessionFactoryObserver sessionFactoryObserver) {
 		this.sessionFactoryObserver = sessionFactoryObserver;
 	}
 
 
 	// Mappings impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Internal implementation of the Mappings interface giving access to the Configuration's internal
 	 * <tt>metadata repository</tt> state ({@link Configuration#classes}, {@link Configuration#tables}, etc).
 	 */
+	@SuppressWarnings( {"deprecation", "unchecked"})
 	protected class MappingsImpl implements ExtendedMappings, Serializable {
 
 		private String schemaName;
 
 		public String getSchemaName() {
 			return schemaName;
 		}
 
 		public void setSchemaName(String schemaName) {
 			this.schemaName = schemaName;
 		}
 
 
 		private String catalogName;
 
 		public String getCatalogName() {
 			return catalogName;
 		}
 
 		public void setCatalogName(String catalogName) {
 			this.catalogName = catalogName;
 		}
 
 
 		private String defaultPackage;
 
 		public String getDefaultPackage() {
 			return defaultPackage;
 		}
 
 		public void setDefaultPackage(String defaultPackage) {
 			this.defaultPackage = defaultPackage;
 		}
 
 
 		private boolean autoImport;
 
 		public boolean isAutoImport() {
 			return autoImport;
 		}
 
 		public void setAutoImport(boolean autoImport) {
 			this.autoImport = autoImport;
 		}
 
 
 		private boolean defaultLazy;
 
 		public boolean isDefaultLazy() {
 			return defaultLazy;
 		}
 
 		public void setDefaultLazy(boolean defaultLazy) {
 			this.defaultLazy = defaultLazy;
 		}
 
 
 		private String defaultCascade;
 
 		public String getDefaultCascade() {
 			return defaultCascade;
 		}
 
 		public void setDefaultCascade(String defaultCascade) {
 			this.defaultCascade = defaultCascade;
 		}
 
 
 		private String defaultAccess;
 
 		public String getDefaultAccess() {
 			return defaultAccess;
 		}
 
 		public void setDefaultAccess(String defaultAccess) {
 			this.defaultAccess = defaultAccess;
 		}
 
 
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 
 		public void setNamingStrategy(NamingStrategy namingStrategy) {
 			Configuration.this.namingStrategy = namingStrategy;
 		}
 
 		public TypeResolver getTypeResolver() {
 			return typeResolver;
 		}
 
 		public Iterator<PersistentClass> iterateClasses() {
 			return classes.values().iterator();
 		}
 
 		public PersistentClass getClass(String entityName) {
 			return classes.get( entityName );
 		}
 
 		public PersistentClass locatePersistentClassByEntityName(String entityName) {
 			PersistentClass persistentClass = classes.get( entityName );
 			if ( persistentClass == null ) {
 				String actualEntityName = imports.get( entityName );
 				if ( StringHelper.isNotEmpty( actualEntityName ) ) {
 					persistentClass = classes.get( actualEntityName );
 				}
 			}
 			return persistentClass;
 		}
 
 		public void addClass(PersistentClass persistentClass) throws DuplicateMappingException {
 			Object old = classes.put( persistentClass.getEntityName(), persistentClass );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "class/entity", persistentClass.getEntityName() );
 			}
 		}
 
 		public void addImport(String entityName, String rename) throws DuplicateMappingException {
 			String existing = imports.put( rename, entityName );
 			if ( existing != null ) {
                 if (existing.equals(entityName)) LOG.duplicateImport(entityName, rename);
                 else throw new DuplicateMappingException("duplicate import: " + rename + " refers to both " + entityName + " and "
                                                          + existing + " (try using auto-import=\"false\")", "import", rename);
 			}
 		}
 
 		public Collection getCollection(String role) {
 			return collections.get( role );
 		}
 
 		public Iterator<Collection> iterateCollections() {
 			return collections.values().iterator();
 		}
 
 		public void addCollection(Collection collection) throws DuplicateMappingException {
 			Object old = collections.put( collection.getRole(), collection );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "collection role", collection.getRole() );
 			}
 		}
 
 		public Table getTable(String schema, String catalog, String name) {
 			String key = Table.qualify(catalog, schema, name);
 			return tables.get(key);
 		}
 
 		public Iterator<Table> iterateTables() {
 			return tables.values().iterator();
 		}
 
 		public Table addTable(
 				String schema,
 				String catalog,
 				String name,
 				String subselect,
 				boolean isAbstract) {
 			name = getObjectNameNormalizer().normalizeIdentifierQuoting( name );
 			schema = getObjectNameNormalizer().normalizeIdentifierQuoting( schema );
 			catalog = getObjectNameNormalizer().normalizeIdentifierQuoting( catalog );
 
 			String key = subselect == null ? Table.qualify( catalog, schema, name ) : subselect;
 			Table table = tables.get( key );
 
 			if ( table == null ) {
 				table = new Table();
 				table.setAbstract( isAbstract );
 				table.setName( name );
 				table.setSchema( schema );
 				table.setCatalog( catalog );
 				table.setSubselect( subselect );
 				tables.put( key, table );
 			}
 			else {
 				if ( !isAbstract ) {
 					table.setAbstract( false );
 				}
 			}
 
 			return table;
 		}
 
 		public Table addDenormalizedTable(
 				String schema,
 				String catalog,
 				String name,
 				boolean isAbstract,
 				String subselect,
 				Table includedTable) throws DuplicateMappingException {
 			name = getObjectNameNormalizer().normalizeIdentifierQuoting( name );
 			schema = getObjectNameNormalizer().normalizeIdentifierQuoting( schema );
 			catalog = getObjectNameNormalizer().normalizeIdentifierQuoting( catalog );
 
 			String key = subselect == null ? Table.qualify(catalog, schema, name) : subselect;
 			if ( tables.containsKey( key ) ) {
 				throw new DuplicateMappingException( "table", name );
 			}
 
 			Table table = new DenormalizedTable( includedTable );
 			table.setAbstract( isAbstract );
 			table.setName( name );
 			table.setSchema( schema );
 			table.setCatalog( catalog );
 			table.setSubselect( subselect );
 
 			tables.put( key, table );
 			return table;
 		}
 
 		public NamedQueryDefinition getQuery(String name) {
 			return namedQueries.get( name );
 		}
 
 		public void addQuery(String name, NamedQueryDefinition query) throws DuplicateMappingException {
 			if ( !defaultNamedQueryNames.contains( name ) ) {
 				applyQuery( name, query );
 			}
 		}
 
 		private void applyQuery(String name, NamedQueryDefinition query) {
 			checkQueryName( name );
 			namedQueries.put( name.intern(), query );
 		}
 
 		private void checkQueryName(String name) throws DuplicateMappingException {
 			if ( namedQueries.containsKey( name ) || namedSqlQueries.containsKey( name ) ) {
 				throw new DuplicateMappingException( "query", name );
 			}
 		}
 
 		public void addDefaultQuery(String name, NamedQueryDefinition query) {
 			applyQuery( name, query );
 			defaultNamedQueryNames.add( name );
 		}
 
 		public NamedSQLQueryDefinition getSQLQuery(String name) {
 			return namedSqlQueries.get( name );
 		}
 
 		public void addSQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException {
 			if ( !defaultNamedNativeQueryNames.contains( name ) ) {
 				applySQLQuery( name, query );
 			}
 		}
 
 		private void applySQLQuery(String name, NamedSQLQueryDefinition query) throws DuplicateMappingException {
 			checkQueryName( name );
 			namedSqlQueries.put( name.intern(), query );
 		}
 
 		public void addDefaultSQLQuery(String name, NamedSQLQueryDefinition query) {
 			applySQLQuery( name, query );
 			defaultNamedNativeQueryNames.add( name );
 		}
 
 		public ResultSetMappingDefinition getResultSetMapping(String name) {
 			return sqlResultSetMappings.get(name);
 		}
 
 		public void addResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
 			if ( !defaultSqlResultSetMappingNames.contains( sqlResultSetMapping.getName() ) ) {
 				applyResultSetMapping( sqlResultSetMapping );
 			}
 		}
 
 		public void applyResultSetMapping(ResultSetMappingDefinition sqlResultSetMapping) throws DuplicateMappingException {
 			Object old = sqlResultSetMappings.put( sqlResultSetMapping.getName(), sqlResultSetMapping );
 			if ( old != null ) {
 				throw new DuplicateMappingException( "resultSet",  sqlResultSetMapping.getName() );
 			}
 		}
 
 		public void addDefaultResultSetMapping(ResultSetMappingDefinition definition) {
 			final String name = definition.getName();
 			if ( !defaultSqlResultSetMappingNames.contains( name ) && getResultSetMapping( name ) != null ) {
 				removeResultSetMapping( name );
 			}
 			applyResultSetMapping( definition );
 			defaultSqlResultSetMappingNames.add( name );
 		}
 
 		protected void removeResultSetMapping(String name) {
 			sqlResultSetMappings.remove( name );
 		}
 
 		public TypeDef getTypeDef(String typeName) {
 			return typeDefs.get( typeName );
 		}
 
 		public void addTypeDef(String typeName, String typeClass, Properties paramMap) {
 			TypeDef def = new TypeDef( typeClass, paramMap );
 			typeDefs.put( typeName, def );
 			LOG.debugf( "Added %s with class %s", typeName, typeClass );
 		}
 
 		public Map getFilterDefinitions() {
 			return filterDefinitions;
 		}
 
 		public FilterDefinition getFilterDefinition(String name) {
 			return filterDefinitions.get( name );
 		}
 
 		public void addFilterDefinition(FilterDefinition definition) {
 			filterDefinitions.put( definition.getFilterName(), definition );
 		}
 
 		public FetchProfile findOrCreateFetchProfile(String name, MetadataSource source) {
 			FetchProfile profile = fetchProfiles.get( name );
 			if ( profile == null ) {
 				profile = new FetchProfile( name, source );
 				fetchProfiles.put( name, profile );
 			}
 			return profile;
 		}
 
 		public Iterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjects() {
 			return iterateAuxiliaryDatabaseObjects();
 		}
 
 		public Iterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjects() {
 			return auxiliaryDatabaseObjects.iterator();
 		}
 
 		public ListIterator<AuxiliaryDatabaseObject> iterateAuxliaryDatabaseObjectsInReverse() {
 			return iterateAuxiliaryDatabaseObjectsInReverse();
 		}
 
 		public ListIterator<AuxiliaryDatabaseObject> iterateAuxiliaryDatabaseObjectsInReverse() {
 			return auxiliaryDatabaseObjects.listIterator( auxiliaryDatabaseObjects.size() );
 		}
 
 		public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
 			auxiliaryDatabaseObjects.add( auxiliaryDatabaseObject );
 		}
 
 		/**
 		 * Internal struct used to help track physical table names to logical table names.
 		 */
 		private class TableDescription implements Serializable {
 			final String logicalName;
 			final Table denormalizedSupertable;
 
 			TableDescription(String logicalName, Table denormalizedSupertable) {
 				this.logicalName = logicalName;
 				this.denormalizedSupertable = denormalizedSupertable;
 			}
 		}
 
 		public String getLogicalTableName(Table table) throws MappingException {
 			return getLogicalTableName( table.getQuotedSchema(), table.getCatalog(), table.getQuotedName() );
 		}
 
 		private String getLogicalTableName(String schema, String catalog, String physicalName) throws MappingException {
 			String key = buildTableNameKey( schema, catalog, physicalName );
 			TableDescription descriptor = (TableDescription) tableNameBinding.get( key );
 			if (descriptor == null) {
 				throw new MappingException( "Unable to find physical table: " + physicalName);
 			}
 			return descriptor.logicalName;
 		}
 
 		public void addTableBinding(
 				String schema,
 				String catalog,
 				String logicalName,
 				String physicalName,
 				Table denormalizedSuperTable) throws DuplicateMappingException {
 			String key = buildTableNameKey( schema, catalog, physicalName );
 			TableDescription tableDescription = new TableDescription( logicalName, denormalizedSuperTable );
 			TableDescription oldDescriptor = ( TableDescription ) tableNameBinding.put( key, tableDescription );
 			if ( oldDescriptor != null && ! oldDescriptor.logicalName.equals( logicalName ) ) {
 				//TODO possibly relax that
 				throw new DuplicateMappingException(
 						"Same physical table name [" + physicalName + "] references several logical table names: [" +
 								oldDescriptor.logicalName + "], [" + logicalName + ']',
 						"table",
 						physicalName
 				);
 			}
 		}
 
 		private String buildTableNameKey(String schema, String catalog, String finalName) {
-			StringBuffer keyBuilder = new StringBuffer();
+			StringBuilder keyBuilder = new StringBuilder();
 			if (schema != null) keyBuilder.append( schema );
 			keyBuilder.append( ".");
 			if (catalog != null) keyBuilder.append( catalog );
 			keyBuilder.append( ".");
 			keyBuilder.append( finalName );
 			return keyBuilder.toString();
 		}
 
 		/**
 		 * Internal struct used to maintain xref between physical and logical column
 		 * names for a table.  Mainly this is used to ensure that the defined
 		 * {@link NamingStrategy} is not creating duplicate column names.
 		 */
 		private class TableColumnNameBinding implements Serializable {
 			private final String tableName;
 			private Map/*<String, String>*/ logicalToPhysical = new HashMap();
 			private Map/*<String, String>*/ physicalToLogical = new HashMap();
 
 			private TableColumnNameBinding(String tableName) {
 				this.tableName = tableName;
 			}
 
 			public void addBinding(String logicalName, Column physicalColumn) {
 				bindLogicalToPhysical( logicalName, physicalColumn );
 				bindPhysicalToLogical( logicalName, physicalColumn );
 			}
 
 			private void bindLogicalToPhysical(String logicalName, Column physicalColumn) throws DuplicateMappingException {
 				final String logicalKey = logicalName.toLowerCase();
 				final String physicalName = physicalColumn.getQuotedName();
 				final String existingPhysicalName = ( String ) logicalToPhysical.put( logicalKey, physicalName );
 				if ( existingPhysicalName != null ) {
 					boolean areSamePhysicalColumn = physicalColumn.isQuoted()
 							? existingPhysicalName.equals( physicalName )
 							: existingPhysicalName.equalsIgnoreCase( physicalName );
 					if ( ! areSamePhysicalColumn ) {
 						throw new DuplicateMappingException(
 								" Table [" + tableName + "] contains logical column name [" + logicalName
 										+ "] referenced by multiple physical column names: [" + existingPhysicalName
 										+ "], [" + physicalName + "]",
 								"column-binding",
 								tableName + "." + logicalName
 						);
 					}
 				}
 			}
 
 			private void bindPhysicalToLogical(String logicalName, Column physicalColumn) throws DuplicateMappingException {
 				final String physicalName = physicalColumn.getQuotedName();
 				final String existingLogicalName = ( String ) physicalToLogical.put( physicalName, logicalName );
 				if ( existingLogicalName != null && ! existingLogicalName.equals( logicalName ) ) {
 					throw new DuplicateMappingException(
 							" Table [" + tableName + "] contains phyical column name [" + physicalName
 									+ "] represented by different logical column names: [" + existingLogicalName
 									+ "], [" + logicalName + "]",
 							"column-binding",
 							tableName + "." + physicalName
 					);
 				}
 			}
 		}
 
 		public void addColumnBinding(String logicalName, Column physicalColumn, Table table) throws DuplicateMappingException {
 			TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( table );
 			if ( binding == null ) {
 				binding = new TableColumnNameBinding( table.getName() );
 				columnNameBindingPerTable.put( table, binding );
 			}
 			binding.addBinding( logicalName, physicalColumn );
 		}
 
 		public String getPhysicalColumnName(String logicalName, Table table) throws MappingException {
 			logicalName = logicalName.toLowerCase();
 			String finalName = null;
 			Table currentTable = table;
 			do {
 				TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( currentTable );
 				if ( binding != null ) {
 					finalName = ( String ) binding.logicalToPhysical.get( logicalName );
 				}
 				String key = buildTableNameKey(
 						currentTable.getQuotedSchema(), currentTable.getCatalog(), currentTable.getQuotedName()
 				);
 				TableDescription description = ( TableDescription ) tableNameBinding.get( key );
 				if ( description != null ) {
 					currentTable = description.denormalizedSupertable;
 				}
 				else {
 					currentTable = null;
 				}
 			} while ( finalName == null && currentTable != null );
 
 			if ( finalName == null ) {
 				throw new MappingException(
 						"Unable to find column with logical name " + logicalName + " in table " + table.getName()
 				);
 			}
 			return finalName;
 		}
 
 		public String getLogicalColumnName(String physicalName, Table table) throws MappingException {
 			String logical = null;
 			Table currentTable = table;
 			TableDescription description = null;
 			do {
 				TableColumnNameBinding binding = ( TableColumnNameBinding ) columnNameBindingPerTable.get( currentTable );
 				if ( binding != null ) {
 					logical = ( String ) binding.physicalToLogical.get( physicalName );
 				}
 				String key = buildTableNameKey(
 						currentTable.getQuotedSchema(), currentTable.getCatalog(), currentTable.getQuotedName()
 				);
 				description = ( TableDescription ) tableNameBinding.get( key );
 				if ( description != null ) {
 					currentTable = description.denormalizedSupertable;
 				}
 				else {
 					currentTable = null;
 				}
 			}
 			while ( logical == null && currentTable != null && description != null );
 			if ( logical == null ) {
 				throw new MappingException(
 						"Unable to find logical column name from physical name "
 								+ physicalName + " in table " + table.getName()
 				);
 			}
 			return logical;
 		}
 
 		public void addSecondPass(SecondPass sp) {
 			addSecondPass( sp, false );
 		}
 
 		public void addSecondPass(SecondPass sp, boolean onTopOfTheQueue) {
 			if ( onTopOfTheQueue ) {
 				secondPasses.add( 0, sp );
 			}
 			else {
 				secondPasses.add( sp );
 			}
 		}
 
 		public void addPropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, false ) );
 		}
 
 		public void addUniquePropertyReference(String referencedClass, String propertyName) {
 			propertyReferences.add( new PropertyReference( referencedClass, propertyName, true ) );
 		}
 
 		public void addToExtendsQueue(ExtendsQueueEntry entry) {
 			extendsQueue.put( entry, null );
 		}
 
 		public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 			return identifierGeneratorFactory;
 		}
 
 		public void addMappedSuperclass(Class type, MappedSuperclass mappedSuperclass) {
 			mappedSuperClasses.put( type, mappedSuperclass );
 		}
 
 		public MappedSuperclass getMappedSuperclass(Class type) {
 			return mappedSuperClasses.get( type );
 		}
 
 		public ObjectNameNormalizer getObjectNameNormalizer() {
 			return normalizer;
 		}
 
 		public Properties getConfigurationProperties() {
 			return properties;
 		}
 
 
 		private Boolean useNewGeneratorMappings;
 
 		public void addDefaultGenerator(IdGenerator generator) {
 			this.addGenerator( generator );
 			defaultNamedGenerators.add( generator.getName() );
 		}
 
 		public boolean isInSecondPass() {
 			return inSecondPass;
 		}
 
 		public PropertyData getPropertyAnnotatedWithMapsId(XClass entityType, String propertyName) {
 			final Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			return map == null ? null : map.get( propertyName );
 		}
 
 		public void addPropertyAnnotatedWithMapsId(XClass entityType, PropertyData property) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithMapsId.put( entityType, map );
 			}
 			map.put( property.getProperty().getAnnotation( MapsId.class ).value(), property );
 		}
 
 		public boolean isSpecjProprietarySyntaxEnabled() {
 			return specjProprietarySyntaxEnabled;
 		}
 
 		public void addPropertyAnnotatedWithMapsIdSpecj(XClass entityType, PropertyData property, String mapsIdValue) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithMapsId.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithMapsId.put( entityType, map );
 			}
 			map.put( mapsIdValue, property );
 		}
 
 		public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName) {
 			final Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 			return map == null ? null : map.get( propertyName );
 		}
 
 		public void addToOneAndIdProperty(XClass entityType, PropertyData property) {
 			Map<String, PropertyData> map = propertiesAnnotatedWithIdAndToOne.get( entityType );
 			if ( map == null ) {
 				map = new HashMap<String, PropertyData>();
 				propertiesAnnotatedWithIdAndToOne.put( entityType, map );
 			}
 			map.put( property.getPropertyName(), property );
 		}
 
 		@SuppressWarnings({ "UnnecessaryUnboxing" })
 		public boolean useNewGeneratorMappings() {
 			if ( useNewGeneratorMappings == null ) {
 				final String booleanName = getConfigurationProperties().getProperty( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS );
 				useNewGeneratorMappings = Boolean.valueOf( booleanName );
 			}
 			return useNewGeneratorMappings.booleanValue();
 		}
 
 		public IdGenerator getGenerator(String name) {
 			return getGenerator( name, null );
 		}
 
 		public IdGenerator getGenerator(String name, Map<String, IdGenerator> localGenerators) {
 			if ( localGenerators != null ) {
 				IdGenerator result = localGenerators.get( name );
 				if ( result != null ) {
 					return result;
 				}
 			}
 			return namedGenerators.get( name );
 		}
 
 		public void addGenerator(IdGenerator generator) {
 			if ( !defaultNamedGenerators.contains( generator.getName() ) ) {
 				IdGenerator old = namedGenerators.put( generator.getName(), generator );
 				if ( old != null ) {
 					LOG.duplicateGeneratorName( old.getName() );
 				}
 			}
 		}
 
 		public void addGeneratorTable(String name, Properties params) {
 			Object old = generatorTables.put( name, params );
 			if ( old != null ) {
 				LOG.duplicateGeneratorTable( name );
 			}
 		}
 
 		public Properties getGeneratorTableProperties(String name, Map<String, Properties> localGeneratorTables) {
 			if ( localGeneratorTables != null ) {
 				Properties result = localGeneratorTables.get( name );
 				if ( result != null ) {
 					return result;
 				}
 			}
 			return generatorTables.get( name );
 		}
 
 		public Map<String, Join> getJoins(String entityName) {
 			return joins.get( entityName );
 		}
 
 		public void addJoins(PersistentClass persistentClass, Map<String, Join> joins) {
 			Object old = Configuration.this.joins.put( persistentClass.getEntityName(), joins );
 			if ( old != null ) {
 				LOG.duplicateJoins( persistentClass.getEntityName() );
 			}
 		}
 
 		public AnnotatedClassType getClassType(XClass clazz) {
 			AnnotatedClassType type = classTypes.get( clazz.getName() );
 			if ( type == null ) {
 				return addClassType( clazz );
 			}
 			else {
 				return type;
 			}
 		}
 
 		//FIXME should be private but is part of the ExtendedMapping contract
 
 		public AnnotatedClassType addClassType(XClass clazz) {
 			AnnotatedClassType type;
 			if ( clazz.isAnnotationPresent( Entity.class ) ) {
 				type = AnnotatedClassType.ENTITY;
 			}
 			else if ( clazz.isAnnotationPresent( Embeddable.class ) ) {
 				type = AnnotatedClassType.EMBEDDABLE;
 			}
 			else if ( clazz.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 				type = AnnotatedClassType.EMBEDDABLE_SUPERCLASS;
 			}
 			else {
 				type = AnnotatedClassType.NONE;
 			}
 			classTypes.put( clazz.getName(), type );
 			return type;
 		}
 
 		/**
 		 * {@inheritDoc}
 		 */
 		public Map<Table, List<String[]>> getTableUniqueConstraints() {
 			final Map<Table, List<String[]>> deprecatedStructure = new HashMap<Table, List<String[]>>(
 					CollectionHelper.determineProperSizing( getUniqueConstraintHoldersByTable() ),
 					CollectionHelper.LOAD_FACTOR
 			);
 			for ( Map.Entry<Table, List<UniqueConstraintHolder>> entry : getUniqueConstraintHoldersByTable().entrySet() ) {
 				List<String[]> columnsPerConstraint = new ArrayList<String[]>(
 						CollectionHelper.determineProperSizing( entry.getValue().size() )
 				);
 				deprecatedStructure.put( entry.getKey(), columnsPerConstraint );
 				for ( UniqueConstraintHolder holder : entry.getValue() ) {
 					columnsPerConstraint.add( holder.getColumns() );
 				}
 			}
 			return deprecatedStructure;
 		}
 
 		public Map<Table, List<UniqueConstraintHolder>> getUniqueConstraintHoldersByTable() {
 			return uniqueConstraintHoldersByTable;
 		}
 
 		@SuppressWarnings({ "unchecked" })
 		public void addUniqueConstraints(Table table, List uniqueConstraints) {
 			List<UniqueConstraintHolder> constraintHolders = new ArrayList<UniqueConstraintHolder>(
 					CollectionHelper.determineProperSizing( uniqueConstraints.size() )
 			);
 
 			int keyNameBase = determineCurrentNumberOfUniqueConstraintHolders( table );
 			for ( String[] columns : ( List<String[]> ) uniqueConstraints ) {
 				final String keyName = "key" + keyNameBase++;
 				constraintHolders.add(
 						new UniqueConstraintHolder().setName( keyName ).setColumns( columns )
 				);
 			}
 			addUniqueConstraintHolders( table, constraintHolders );
 		}
 
 		private int determineCurrentNumberOfUniqueConstraintHolders(Table table) {
 			List currentHolders = getUniqueConstraintHoldersByTable().get( table );
 			return currentHolders == null
 					? 0
 					: currentHolders.size();
 		}
 
 		public void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraintHolders) {
 			List<UniqueConstraintHolder> holderList = getUniqueConstraintHoldersByTable().get( table );
 			if ( holderList == null ) {
 				holderList = new ArrayList<UniqueConstraintHolder>();
 				getUniqueConstraintHoldersByTable().put( table, holderList );
 			}
 			holderList.addAll( uniqueConstraintHolders );
 		}
 
 		public void addMappedBy(String entityName, String propertyName, String inversePropertyName) {
 			mappedByResolver.put( entityName + "." + propertyName, inversePropertyName );
 		}
 
 		public String getFromMappedBy(String entityName, String propertyName) {
 			return mappedByResolver.get( entityName + "." + propertyName );
 		}
 
 		public void addPropertyReferencedAssociation(String entityName, String propertyName, String propertyRef) {
 			propertyRefResolver.put( entityName + "." + propertyName, propertyRef );
 		}
 
 		public String getPropertyReferencedAssociation(String entityName, String propertyName) {
 			return propertyRefResolver.get( entityName + "." + propertyName );
 		}
 
 		public ReflectionManager getReflectionManager() {
 			return reflectionManager;
 		}
 
 		public Map getClasses() {
 			return classes;
 		}
 
 		public void addAnyMetaDef(AnyMetaDef defAnn) throws AnnotationException {
 			if ( anyMetaDefs.containsKey( defAnn.name() ) ) {
 				throw new AnnotationException( "Two @AnyMetaDef with the same name defined: " + defAnn.name() );
 			}
 			anyMetaDefs.put( defAnn.name(), defAnn );
 		}
 
 		public AnyMetaDef getAnyMetaDef(String name) {
 			return anyMetaDefs.get( name );
 		}
 	}
 
 	final ObjectNameNormalizer normalizer = new ObjectNameNormalizerImpl();
 
 	final class ObjectNameNormalizerImpl extends ObjectNameNormalizer implements Serializable {
 		public boolean isUseQuotedIdentifiersGlobally() {
 			//Do not cache this value as we lazily set it in Hibernate Annotation (AnnotationConfiguration)
 			//TODO use a dedicated protected useQuotedIdentifier flag in Configuration (overriden by AnnotationConfiguration)
 			String setting = (String) properties.get( Environment.GLOBALLY_QUOTED_IDENTIFIERS );
 			return setting != null && Boolean.valueOf( setting ).booleanValue();
 		}
 
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 	}
 
 	protected class MetadataSourceQueue implements Serializable {
 		private LinkedHashMap<XmlDocument, Set<String>> hbmMetadataToEntityNamesMap
 				= new LinkedHashMap<XmlDocument, Set<String>>();
 		private Map<String, XmlDocument> hbmMetadataByEntityNameXRef = new HashMap<String, XmlDocument>();
 
 		//XClass are not serializable by default
 		private transient List<XClass> annotatedClasses = new ArrayList<XClass>();
 		//only used during the secondPhaseCompile pass, hence does not need to be serialized
 		private transient Map<String, XClass> annotatedClassesByEntityNameMap = new HashMap<String, XClass>();
 
 		private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
 			ois.defaultReadObject();
 			annotatedClassesByEntityNameMap = new HashMap<String, XClass>();
 
 			//build back annotatedClasses
 			@SuppressWarnings( "unchecked" )
 			List<Class> serializableAnnotatedClasses = (List<Class>) ois.readObject();
 			annotatedClasses = new ArrayList<XClass>( serializableAnnotatedClasses.size() );
 			for ( Class clazz : serializableAnnotatedClasses ) {
 				annotatedClasses.add( reflectionManager.toXClass( clazz ) );
 			}
 		}
 
 		private void writeObject(java.io.ObjectOutputStream out) throws IOException {
 			out.defaultWriteObject();
 			List<Class> serializableAnnotatedClasses = new ArrayList<Class>( annotatedClasses.size() );
 			for ( XClass xClass : annotatedClasses ) {
 				serializableAnnotatedClasses.add( reflectionManager.toClass( xClass ) );
 			}
 			out.writeObject( serializableAnnotatedClasses );
 		}
 
 		public void add(XmlDocument metadataXml) {
 			final Document document = metadataXml.getDocumentTree();
 			final Element hmNode = document.getRootElement();
 			Attribute packNode = hmNode.attribute( "package" );
 			String defaultPackage = packNode != null ? packNode.getValue() : "";
 			Set<String> entityNames = new HashSet<String>();
 			findClassNames( defaultPackage, hmNode, entityNames );
 			for ( String entity : entityNames ) {
 				hbmMetadataByEntityNameXRef.put( entity, metadataXml );
 			}
 			this.hbmMetadataToEntityNamesMap.put( metadataXml, entityNames );
 		}
 
 		private void findClassNames(String defaultPackage, Element startNode, Set<String> names) {
 			// if we have some extends we need to check if those classes possibly could be inside the
 			// same hbm.xml file...
 			Iterator[] classes = new Iterator[4];
 			classes[0] = startNode.elementIterator( "class" );
 			classes[1] = startNode.elementIterator( "subclass" );
 			classes[2] = startNode.elementIterator( "joined-subclass" );
 			classes[3] = startNode.elementIterator( "union-subclass" );
 
 			Iterator classIterator = new JoinedIterator( classes );
 			while ( classIterator.hasNext() ) {
 				Element element = ( Element ) classIterator.next();
 				String entityName = element.attributeValue( "entity-name" );
 				if ( entityName == null ) {
 					entityName = getClassName( element.attribute( "name" ), defaultPackage );
 				}
 				names.add( entityName );
 				findClassNames( defaultPackage, element, names );
 			}
 		}
 
 		private String getClassName(Attribute name, String defaultPackage) {
 			if ( name == null ) {
 				return null;
 			}
 			String unqualifiedName = name.getValue();
 			if ( unqualifiedName == null ) {
 				return null;
 			}
 			if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
 				return defaultPackage + '.' + unqualifiedName;
 			}
 			return unqualifiedName;
 		}
 
 		public void add(XClass annotatedClass) {
 			annotatedClasses.add( annotatedClass );
 		}
 
 		protected void syncAnnotatedClasses() {
 			final Iterator<XClass> itr = annotatedClasses.iterator();
 			while ( itr.hasNext() ) {
 				final XClass annotatedClass = itr.next();
 				if ( annotatedClass.isAnnotationPresent( Entity.class ) ) {
 					annotatedClassesByEntityNameMap.put( annotatedClass.getName(), annotatedClass );
 					continue;
 				}
 
 				if ( !annotatedClass.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 					itr.remove();
 				}
 			}
 		}
 
 		protected void processMetadata(List<MetadataSourceType> order) {
 			syncAnnotatedClasses();
 
 			for ( MetadataSourceType type : order ) {
 				if ( MetadataSourceType.HBM.equals( type ) ) {
 					processHbmXmlQueue();
 				}
 				else if ( MetadataSourceType.CLASS.equals( type ) ) {
 					processAnnotatedClassesQueue();
 				}
 			}
 		}
 
 		private void processHbmXmlQueue() {
 			LOG.debug( "Processing hbm.xml files" );
 			for ( Map.Entry<XmlDocument, Set<String>> entry : hbmMetadataToEntityNamesMap.entrySet() ) {
 				// Unfortunately we have to create a Mappings instance for each iteration here
 				processHbmXml( entry.getKey(), entry.getValue() );
 			}
 			hbmMetadataToEntityNamesMap.clear();
 			hbmMetadataByEntityNameXRef.clear();
 		}
 
 		private void processHbmXml(XmlDocument metadataXml, Set<String> entityNames) {
 			try {
 				HbmBinder.bindRoot( metadataXml, createMappings(), CollectionHelper.EMPTY_MAP, entityNames );
 			}
 			catch ( MappingException me ) {
 				throw new InvalidMappingException(
 						metadataXml.getOrigin().getType(),
 						metadataXml.getOrigin().getName(),
 						me
 				);
 			}
 
 			for ( String entityName : entityNames ) {
 				if ( annotatedClassesByEntityNameMap.containsKey( entityName ) ) {
 					annotatedClasses.remove( annotatedClassesByEntityNameMap.get( entityName ) );
 					annotatedClassesByEntityNameMap.remove( entityName );
 				}
 			}
 		}
 
 		private void processAnnotatedClassesQueue() {
 			LOG.debug( "Process annotated classes" );
 			//bind classes in the correct order calculating some inheritance state
 			List<XClass> orderedClasses = orderAndFillHierarchy( annotatedClasses );
 			Mappings mappings = createMappings();
 			Map<XClass, InheritanceState> inheritanceStatePerClass = AnnotationBinder.buildInheritanceStates(
 					orderedClasses, mappings
 			);
 
 
 			for ( XClass clazz : orderedClasses ) {
 				AnnotationBinder.bindClass( clazz, inheritanceStatePerClass, mappings );
 
 				final String entityName = clazz.getName();
 				if ( hbmMetadataByEntityNameXRef.containsKey( entityName ) ) {
 					hbmMetadataToEntityNamesMap.remove( hbmMetadataByEntityNameXRef.get( entityName ) );
 					hbmMetadataByEntityNameXRef.remove( entityName );
 				}
 			}
 			annotatedClasses.clear();
 			annotatedClassesByEntityNameMap.clear();
 		}
 
 		private List<XClass> orderAndFillHierarchy(List<XClass> original) {
 			List<XClass> copy = new ArrayList<XClass>( original );
 			insertMappedSuperclasses( original, copy );
 
 			// order the hierarchy
 			List<XClass> workingCopy = new ArrayList<XClass>( copy );
 			List<XClass> newList = new ArrayList<XClass>( copy.size() );
 			while ( workingCopy.size() > 0 ) {
 				XClass clazz = workingCopy.get( 0 );
 				orderHierarchy( workingCopy, newList, copy, clazz );
 			}
 			return newList;
 		}
 
 		private void insertMappedSuperclasses(List<XClass> original, List<XClass> copy) {
 			for ( XClass clazz : original ) {
 				XClass superClass = clazz.getSuperclass();
 				while ( superClass != null
 						&& !reflectionManager.equals( superClass, Object.class )
 						&& !copy.contains( superClass ) ) {
 					if ( superClass.isAnnotationPresent( Entity.class )
 							|| superClass.isAnnotationPresent( javax.persistence.MappedSuperclass.class ) ) {
 						copy.add( superClass );
 					}
 					superClass = superClass.getSuperclass();
 				}
 			}
 		}
 
 		private void orderHierarchy(List<XClass> copy, List<XClass> newList, List<XClass> original, XClass clazz) {
 			if ( clazz == null || reflectionManager.equals( clazz, Object.class ) ) {
 				return;
 			}
 			//process superclass first
 			orderHierarchy( copy, newList, original, clazz.getSuperclass() );
 			if ( original.contains( clazz ) ) {
 				if ( !newList.contains( clazz ) ) {
 					newList.add( clazz );
 				}
 				copy.remove( clazz );
 			}
 		}
 
 		public boolean isEmpty() {
 			return hbmMetadataToEntityNamesMap.isEmpty() && annotatedClasses.isEmpty();
 		}
 
 	}
 
 
 	public static final MetadataSourceType[] DEFAULT_ARTEFACT_PROCESSING_ORDER = new MetadataSourceType[] {
 			MetadataSourceType.HBM,
 			MetadataSourceType.CLASS
 	};
 
 	private List<MetadataSourceType> metadataSourcePrecedence;
 
 	private List<MetadataSourceType> determineMetadataSourcePrecedence() {
 		if ( metadataSourcePrecedence.isEmpty()
 				&& StringHelper.isNotEmpty( getProperties().getProperty( ARTEFACT_PROCESSING_ORDER ) ) ) {
 			metadataSourcePrecedence = parsePrecedence( getProperties().getProperty( ARTEFACT_PROCESSING_ORDER ) );
 		}
 		if ( metadataSourcePrecedence.isEmpty() ) {
 			metadataSourcePrecedence = Arrays.asList( DEFAULT_ARTEFACT_PROCESSING_ORDER );
 		}
 		metadataSourcePrecedence = Collections.unmodifiableList( metadataSourcePrecedence );
 
 		return metadataSourcePrecedence;
 	}
 
 	public void setPrecedence(String precedence) {
 		this.metadataSourcePrecedence = parsePrecedence( precedence );
 	}
 
 	private List<MetadataSourceType> parsePrecedence(String s) {
 		if ( StringHelper.isEmpty( s ) ) {
 			return Collections.emptyList();
 		}
 		StringTokenizer precedences = new StringTokenizer( s, ",; ", false );
 		List<MetadataSourceType> tmpPrecedences = new ArrayList<MetadataSourceType>();
 		while ( precedences.hasMoreElements() ) {
 			tmpPrecedences.add( MetadataSourceType.parsePrecedence( ( String ) precedences.nextElement() ) );
 		}
 		return tmpPrecedences;
 	}
 
 	private static class CacheHolder {
 		public CacheHolder(String role, String usage, String region, boolean isClass, boolean cacheLazy) {
 			this.role = role;
 			this.usage = usage;
 			this.region = region;
 			this.isClass = isClass;
 			this.cacheLazy = cacheLazy;
 		}
 
 		public String role;
 		public String usage;
 		public String region;
 		public boolean isClass;
 		public boolean cacheLazy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
index ed13771fe7..afb8d40b81 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/TableBinder.java
@@ -1,581 +1,569 @@
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
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import javax.persistence.UniqueConstraint;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.Index;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.IndexOrUniqueKeySecondPass;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.cfg.ObjectNameSource;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.JoinedSubclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 
 /**
  * Table related operations
  *
  * @author Emmanuel Bernard
  */
 @SuppressWarnings("unchecked")
 public class TableBinder {
 	//TODO move it to a getter/setter strategy
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TableBinder.class.getName());
 
 	private String schema;
 	private String catalog;
 	private String name;
 	private boolean isAbstract;
 	private List<UniqueConstraintHolder> uniqueConstraints;
 //	private List<String[]> uniqueConstraints;
 	String constraints;
 	Table denormalizedSuperTable;
 	Mappings mappings;
 	private String ownerEntityTable;
 	private String associatedEntityTable;
 	private String propertyName;
 	private String ownerEntity;
 	private String associatedEntity;
 	private boolean isJPA2ElementCollection;
 
 	public void setSchema(String schema) {
 		this.schema = schema;
 	}
 
 	public void setCatalog(String catalog) {
 		this.catalog = catalog;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	public void setAbstract(boolean anAbstract) {
 		isAbstract = anAbstract;
 	}
 
 	public void setUniqueConstraints(UniqueConstraint[] uniqueConstraints) {
 		this.uniqueConstraints = TableBinder.buildUniqueConstraintHolders( uniqueConstraints );
 	}
 
 	public void setConstraints(String constraints) {
 		this.constraints = constraints;
 	}
 
 	public void setDenormalizedSuperTable(Table denormalizedSuperTable) {
 		this.denormalizedSuperTable = denormalizedSuperTable;
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	public void setJPA2ElementCollection(boolean isJPA2ElementCollection) {
 		this.isJPA2ElementCollection = isJPA2ElementCollection;
 	}
 
 	private static class AssociationTableNameSource implements ObjectNameSource {
 		private final String explicitName;
 		private final String logicalName;
 
 		private AssociationTableNameSource(String explicitName, String logicalName) {
 			this.explicitName = explicitName;
 			this.logicalName = logicalName;
 		}
 
 		public String getExplicitName() {
 			return explicitName;
 		}
 
 		public String getLogicalName() {
 			return logicalName;
 		}
 	}
 
 	// only bind association table currently
 	public Table bind() {
 		//logicalName only accurate for assoc table...
 		final String unquotedOwnerTable = StringHelper.unquote( ownerEntityTable );
 		final String unquotedAssocTable = StringHelper.unquote( associatedEntityTable );
 
 		//@ElementCollection use ownerEntity_property instead of the cleaner ownerTableName_property
 		// ownerEntity can be null when the table name is explicitly set
 		final String ownerObjectName = isJPA2ElementCollection && ownerEntity != null ?
 				StringHelper.unqualify( ownerEntity ) : unquotedOwnerTable;
 		final ObjectNameSource nameSource = buildNameContext(
 				ownerObjectName,
 				unquotedAssocTable );
 
 		final boolean ownerEntityTableQuoted = StringHelper.isQuoted( ownerEntityTable );
 		final boolean associatedEntityTableQuoted = StringHelper.isQuoted( associatedEntityTable );
 		final ObjectNameNormalizer.NamingStrategyHelper namingStrategyHelper = new ObjectNameNormalizer.NamingStrategyHelper() {
 			public String determineImplicitName(NamingStrategy strategy) {
 
 				final String strategyResult = strategy.collectionTableName(
 						ownerEntity,
 						ownerObjectName,
 						associatedEntity,
 						unquotedAssocTable,
 						propertyName
 
 				);
 				return ownerEntityTableQuoted || associatedEntityTableQuoted
 						? StringHelper.quote( strategyResult )
 						: strategyResult;
 			}
 
 			public String handleExplicitName(NamingStrategy strategy, String name) {
 				return strategy.tableName( name );
 			}
 		};
 
 		return buildAndFillTable(
 				schema,
 				catalog,
 				nameSource,
 				namingStrategyHelper,
 				isAbstract,
 				uniqueConstraints,
 				constraints,
 				denormalizedSuperTable,
 				mappings,
 				null
 		);
 	}
 
 	private ObjectNameSource buildNameContext(String unquotedOwnerTable, String unquotedAssocTable) {
 		String logicalName = mappings.getNamingStrategy().logicalCollectionTableName(
 				name,
 				unquotedOwnerTable,
 				unquotedAssocTable,
 				propertyName
 		);
 		if ( StringHelper.isQuoted( ownerEntityTable ) || StringHelper.isQuoted( associatedEntityTable ) ) {
 			logicalName = StringHelper.quote( logicalName );
 		}
 
 		return new AssociationTableNameSource( name, logicalName );
 	}
 
 	public static Table buildAndFillTable(
 			String schema,
 			String catalog,
 			ObjectNameSource nameSource,
 			ObjectNameNormalizer.NamingStrategyHelper namingStrategyHelper,
 			boolean isAbstract,
 			List<UniqueConstraintHolder> uniqueConstraints,
 			String constraints,
 			Table denormalizedSuperTable,
 			Mappings mappings,
 			String subselect) {
 		schema = BinderHelper.isEmptyAnnotationValue( schema ) ? mappings.getSchemaName() : schema;
 		catalog = BinderHelper.isEmptyAnnotationValue( catalog ) ? mappings.getCatalogName() : catalog;
 
 		String realTableName = mappings.getObjectNameNormalizer().normalizeDatabaseIdentifier(
 				nameSource.getExplicitName(),
 				namingStrategyHelper
 		);
 
 		final Table table;
 		if ( denormalizedSuperTable != null ) {
 			table = mappings.addDenormalizedTable(
 					schema,
 					catalog,
 					realTableName,
 					isAbstract,
 					subselect,
 					denormalizedSuperTable
 			);
 		}
 		else {
 			table = mappings.addTable(
 					schema,
 					catalog,
 					realTableName,
 					subselect,
 					isAbstract
 			);
 		}
 
 		if ( uniqueConstraints != null && uniqueConstraints.size() > 0 ) {
 			mappings.addUniqueConstraintHolders( table, uniqueConstraints );
 		}
 
 		if ( constraints != null ) table.addCheckConstraint( constraints );
 
 		// logicalName is null if we are in the second pass
 		final String logicalName = nameSource.getLogicalName();
 		if ( logicalName != null ) {
 			mappings.addTableBinding( schema, catalog, logicalName, realTableName, denormalizedSuperTable );
 		}
 		return table;
 	}
 
 	/**
-	 *
-	 * @param schema
-	 * @param catalog
-	 * @param realTableName
-	 * @param logicalName
-	 * @param isAbstract
-	 * @param uniqueConstraints
-	 * @param constraints
-	 * @param denormalizedSuperTable
-	 * @param mappings
-	 * @return
-	 *
 	 * @deprecated Use {@link #buildAndFillTable} instead.
 	 */
 	@Deprecated
     @SuppressWarnings({ "JavaDoc" })
 	public static Table fillTable(
 			String schema,
 			String catalog,
 			String realTableName,
 			String logicalName,
 			boolean isAbstract,
 			List uniqueConstraints,
 			String constraints,
 			Table denormalizedSuperTable,
 			Mappings mappings) {
 		schema = BinderHelper.isEmptyAnnotationValue( schema ) ? mappings.getSchemaName() : schema;
 		catalog = BinderHelper.isEmptyAnnotationValue( catalog ) ? mappings.getCatalogName() : catalog;
 		Table table;
 		if ( denormalizedSuperTable != null ) {
 			table = mappings.addDenormalizedTable(
 					schema,
 					catalog,
 					realTableName,
 					isAbstract,
 					null, //subselect
 					denormalizedSuperTable
 			);
 		}
 		else {
 			table = mappings.addTable(
 					schema,
 					catalog,
 					realTableName,
 					null, //subselect
 					isAbstract
 			);
 		}
 		if ( uniqueConstraints != null && uniqueConstraints.size() > 0 ) {
 			mappings.addUniqueConstraints( table, uniqueConstraints );
 		}
 		if ( constraints != null ) table.addCheckConstraint( constraints );
 		//logicalName is null if we are in the second pass
 		if ( logicalName != null ) {
 			mappings.addTableBinding( schema, catalog, logicalName, realTableName, denormalizedSuperTable );
 		}
 		return table;
 	}
 
 	public static void bindFk(
 			PersistentClass referencedEntity,
 			PersistentClass destinationEntity,
 			Ejb3JoinColumn[] columns,
 			SimpleValue value,
 			boolean unique,
 			Mappings mappings) {
 		PersistentClass associatedClass;
 		if ( destinationEntity != null ) {
 			//overridden destination
 			associatedClass = destinationEntity;
 		}
 		else {
 			associatedClass = columns[0].getPropertyHolder() == null
 					? null
 					: columns[0].getPropertyHolder().getPersistentClass();
 		}
 		final String mappedByProperty = columns[0].getMappedBy();
 		if ( StringHelper.isNotEmpty( mappedByProperty ) ) {
 			/**
 			 * Get the columns of the mapped-by property
 			 * copy them and link the copy to the actual value
 			 */
 			LOG.debugf( "Retrieving property %s.%s", associatedClass.getEntityName(), mappedByProperty );
 
 			final Property property = associatedClass.getRecursiveProperty( columns[0].getMappedBy() );
 			Iterator mappedByColumns;
 			if ( property.getValue() instanceof Collection ) {
 				Collection collection = ( (Collection) property.getValue() );
 				Value element = collection.getElement();
 				if ( element == null ) {
 					throw new AnnotationException(
 							"Illegal use of mappedBy on both sides of the relationship: "
 									+ associatedClass.getEntityName() + "." + mappedByProperty
 					);
 				}
 				mappedByColumns = element.getColumnIterator();
 			}
 			else {
 				mappedByColumns = property.getValue().getColumnIterator();
 			}
 			while ( mappedByColumns.hasNext() ) {
 				Column column = (Column) mappedByColumns.next();
 				columns[0].overrideFromReferencedColumnIfNecessary( column );
 				columns[0].linkValueUsingAColumnCopy( column, value );
 			}
 		}
 		else if ( columns[0].isImplicit() ) {
 			/**
 			 * if columns are implicit, then create the columns based on the
 			 * referenced entity id columns
 			 */
 			Iterator idColumns;
 			if ( referencedEntity instanceof JoinedSubclass ) {
 				idColumns = referencedEntity.getKey().getColumnIterator();
 			}
 			else {
 				idColumns = referencedEntity.getIdentifier().getColumnIterator();
 			}
 			while ( idColumns.hasNext() ) {
 				Column column = (Column) idColumns.next();
 				columns[0].overrideFromReferencedColumnIfNecessary( column );
 				columns[0].linkValueUsingDefaultColumnNaming( column, referencedEntity, value );
 			}
 		}
 		else {
 			int fkEnum = Ejb3JoinColumn.checkReferencedColumnsType( columns, referencedEntity, mappings );
 
 			if ( Ejb3JoinColumn.NON_PK_REFERENCE == fkEnum ) {
 				String referencedPropertyName;
 				if ( value instanceof ToOne ) {
 					referencedPropertyName = ( (ToOne) value ).getReferencedPropertyName();
 				}
 				else if ( value instanceof DependantValue ) {
 					String propertyName = columns[0].getPropertyName();
 					if ( propertyName != null ) {
 						Collection collection = (Collection) referencedEntity.getRecursiveProperty( propertyName )
 								.getValue();
 						referencedPropertyName = collection.getReferencedPropertyName();
 					}
 					else {
 						throw new AnnotationException( "SecondaryTable JoinColumn cannot reference a non primary key" );
 					}
 
 				}
 				else {
 					throw new AssertionFailure(
 							"Do a property ref on an unexpected Value type: "
 									+ value.getClass().getName()
 					);
 				}
 				if ( referencedPropertyName == null ) {
 					throw new AssertionFailure(
 							"No property ref found while expected"
 					);
 				}
 				Property synthProp = referencedEntity.getRecursiveProperty( referencedPropertyName );
 				if ( synthProp == null ) {
 					throw new AssertionFailure(
 							"Cannot find synthProp: " + referencedEntity.getEntityName() + "." + referencedPropertyName
 					);
 				}
 				linkJoinColumnWithValueOverridingNameIfImplicit(
 						referencedEntity, synthProp.getColumnIterator(), columns, value
 				);
 
 			}
 			else {
 				if ( Ejb3JoinColumn.NO_REFERENCE == fkEnum ) {
 					//implicit case, we hope PK and FK columns are in the same order
 					if ( columns.length != referencedEntity.getIdentifier().getColumnSpan() ) {
 						throw new AnnotationException(
 								"A Foreign key refering " + referencedEntity.getEntityName()
 										+ " from " + associatedClass.getEntityName()
 										+ " has the wrong number of column. should be " + referencedEntity.getIdentifier()
 										.getColumnSpan()
 						);
 					}
 					linkJoinColumnWithValueOverridingNameIfImplicit(
 							referencedEntity,
 							referencedEntity.getIdentifier().getColumnIterator(),
 							columns,
 							value
 					);
 				}
 				else {
 					//explicit referencedColumnName
 					Iterator idColItr = referencedEntity.getKey().getColumnIterator();
 					org.hibernate.mapping.Column col;
 					Table table = referencedEntity.getTable(); //works cause the pk has to be on the primary table
 					if ( !idColItr.hasNext() ) {
 						LOG.debug( "No column in the identifier!" );
 					}
 					while ( idColItr.hasNext() ) {
 						boolean match = false;
 						//for each PK column, find the associated FK column.
 						col = (org.hibernate.mapping.Column) idColItr.next();
 						for (Ejb3JoinColumn joinCol : columns) {
 							String referencedColumn = joinCol.getReferencedColumn();
 							referencedColumn = mappings.getPhysicalColumnName( referencedColumn, table );
 							//In JPA 2 referencedColumnName is case insensitive
 							if ( referencedColumn.equalsIgnoreCase( col.getQuotedName() ) ) {
 								//proper join column
 								if ( joinCol.isNameDeferred() ) {
 									joinCol.linkValueUsingDefaultColumnNaming(
 											col, referencedEntity, value
 									);
 								}
 								else {
 									joinCol.linkWithValue( value );
 								}
 								joinCol.overrideFromReferencedColumnIfNecessary( col );
 								match = true;
 								break;
 							}
 						}
 						if ( !match ) {
 							throw new AnnotationException(
 									"Column name " + col.getName() + " of "
 											+ referencedEntity.getEntityName() + " not found in JoinColumns.referencedColumnName"
 							);
 						}
 					}
 				}
 			}
 		}
 		value.createForeignKey();
 		if ( unique ) {
 			createUniqueConstraint( value );
 		}
 	}
 
 	public static void linkJoinColumnWithValueOverridingNameIfImplicit(
 			PersistentClass referencedEntity,
 			Iterator columnIterator,
 			Ejb3JoinColumn[] columns,
 			SimpleValue value) {
 		for (Ejb3JoinColumn joinCol : columns) {
 			Column synthCol = (Column) columnIterator.next();
 			if ( joinCol.isNameDeferred() ) {
 				//this has to be the default value
 				joinCol.linkValueUsingDefaultColumnNaming( synthCol, referencedEntity, value );
 			}
 			else {
 				joinCol.linkWithValue( value );
 				joinCol.overrideFromReferencedColumnIfNecessary( synthCol );
 			}
 		}
 	}
 
 	public static void createUniqueConstraint(Value value) {
 		Iterator iter = value.getColumnIterator();
 		ArrayList cols = new ArrayList();
 		while ( iter.hasNext() ) {
 			cols.add( iter.next() );
 		}
 		value.getTable().createUniqueKey( cols );
 	}
 
 	public static void addIndexes(Table hibTable, Index[] indexes, Mappings mappings) {
 		for (Index index : indexes) {
 			//no need to handle inSecondPass here since it is only called from EntityBinder
 			mappings.addSecondPass(
 					new IndexOrUniqueKeySecondPass( hibTable, index.name(), index.columnNames(), mappings )
 			);
 		}
 	}
 
 	/**
 	 * @deprecated Use {@link #buildUniqueConstraintHolders} instead
 	 */
 	@Deprecated
 	@SuppressWarnings({ "JavaDoc" })
 	public static List<String[]> buildUniqueConstraints(UniqueConstraint[] constraintsArray) {
 		List<String[]> result = new ArrayList<String[]>();
 		if ( constraintsArray.length != 0 ) {
 			for (UniqueConstraint uc : constraintsArray) {
 				result.add( uc.columnNames() );
 			}
 		}
 		return result;
 	}
 
 	/**
 	 * Build a list of {@link org.hibernate.cfg.UniqueConstraintHolder} instances given a list of
 	 * {@link UniqueConstraint} annotations.
 	 *
 	 * @param annotations The {@link UniqueConstraint} annotations.
 	 *
 	 * @return The built {@link org.hibernate.cfg.UniqueConstraintHolder} instances.
 	 */
 	public static List<UniqueConstraintHolder> buildUniqueConstraintHolders(UniqueConstraint[] annotations) {
 		List<UniqueConstraintHolder> result;
 		if ( annotations == null || annotations.length == 0 ) {
 			result = java.util.Collections.emptyList();
 		}
 		else {
 			result = new ArrayList<UniqueConstraintHolder>( CollectionHelper.determineProperSizing( annotations.length ) );
 			for ( UniqueConstraint uc : annotations ) {
 				result.add(
 						new UniqueConstraintHolder()
 								.setName( uc.name() )
 								.setColumns( uc.columnNames() )
 				);
 			}
 		}
 		return result;
 	}
 
 	public void setDefaultName(
 			String ownerEntity, String ownerEntityTable, String associatedEntity, String associatedEntityTable,
 			String propertyName
 	) {
 		this.ownerEntity = ownerEntity;
 		this.ownerEntityTable = ownerEntityTable;
 		this.associatedEntity = associatedEntity;
 		this.associatedEntityTable = associatedEntityTable;
 		this.propertyName = propertyName;
 		this.name = null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/criterion/Subqueries.java b/hibernate-core/src/main/java/org/hibernate/criterion/Subqueries.java
index 3d974c7168..c179d739d8 100755
--- a/hibernate-core/src/main/java/org/hibernate/criterion/Subqueries.java
+++ b/hibernate-core/src/main/java/org/hibernate/criterion/Subqueries.java
@@ -1,201 +1,201 @@
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
 package org.hibernate.criterion;
 
-
 /**
  * Factory class for criterion instances that represent expressions
  * involving subqueries.
  * 
- * @see Restriction
+ * @see Restrictions
  * @see Projection
  * @see org.hibernate.Criteria
+ *
  * @author Gavin King
  * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
  */
+@SuppressWarnings( {"UnusedDeclaration"})
 public class Subqueries {
 		
 	public static Criterion exists(DetachedCriteria dc) {
 		return new ExistsSubqueryExpression("exists", dc);
 	}
 	
 	public static Criterion notExists(DetachedCriteria dc) {
 		return new ExistsSubqueryExpression("not exists", dc);
 	}
 	
 	public static Criterion propertyEqAll(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, "=", "all", dc);
 	}
 	
 	public static Criterion propertyIn(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, "in", null, dc);
 	}
 	
 	public static Criterion propertyNotIn(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, "not in", null, dc);
 	}
 	
 	public static Criterion propertyEq(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, "=", null, dc);
 	}
 
 	public static Criterion propertiesEq(String[] propertyNames, DetachedCriteria dc) {
 		return new PropertiesSubqueryExpression(propertyNames, "=", dc);
 	}
 
 	public static Criterion propertiesNotEq(String[] propertyNames, DetachedCriteria dc) {
 		return new PropertiesSubqueryExpression(propertyNames, "<>", dc);
 	}
 
 	public static Criterion propertiesIn(String[] propertyNames, DetachedCriteria dc) {
 		return new PropertiesSubqueryExpression(propertyNames, "in", dc);
 	}
 
 	public static Criterion propertiesNotIn(String[] propertyNames, DetachedCriteria dc) {
 		return new PropertiesSubqueryExpression(propertyNames, "not in", dc);
 	}
 	
 	public static Criterion propertyNe(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, "<>", null, dc);
 	}
 	
 	public static Criterion propertyGt(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, ">", null, dc);
 	}
 	
 	public static Criterion propertyLt(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, "<", null, dc);
 	}
 	
 	public static Criterion propertyGe(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, ">=", null, dc);
 	}
 	
 	public static Criterion propertyLe(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, "<=", null, dc);
 	}
 	
 	public static Criterion propertyGtAll(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, ">", "all", dc);
 	}
 	
 	public static Criterion propertyLtAll(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, "<", "all", dc);
 	}
 	
 	public static Criterion propertyGeAll(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, ">=", "all", dc);
 	}
 	
 	public static Criterion propertyLeAll(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, "<=", "all", dc);
 	}
 	
 	public static Criterion propertyGtSome(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, ">", "some", dc);
 	}
 	
 	public static Criterion propertyLtSome(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, "<", "some", dc);
 	}
 	
 	public static Criterion propertyGeSome(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, ">=", "some", dc);
 	}
 	
 	public static Criterion propertyLeSome(String propertyName, DetachedCriteria dc) {
 		return new PropertySubqueryExpression(propertyName, "<=", "some", dc);
 	}
 	
 	public static Criterion eqAll(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, "=", "all", dc);
 	}
 	
 	public static Criterion in(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, "in", null, dc);
 	}
 	
 	public static Criterion notIn(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, "not in", null, dc);
 	}
 	
 	public static Criterion eq(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, "=", null, dc);
 	}
 	
 	public static Criterion gt(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, ">", null, dc);
 	}
 	
 	public static Criterion lt(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, "<", null, dc);
 	}
 	
 	public static Criterion ge(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, ">=", null, dc);
 	}
 	
 	public static Criterion le(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, "<=", null, dc);
 	}
 	
 	public static Criterion ne(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, "<>", null, dc);
 	}
 	
 	public static Criterion gtAll(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, ">", "all", dc);
 	}
 	
 	public static Criterion ltAll(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, "<", "all", dc);
 	}
 	
 	public static Criterion geAll(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, ">=", "all", dc);
 	}
 	
 	public static Criterion leAll(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, "<=", "all", dc);
 	}
 	
 	public static Criterion gtSome(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, ">", "some", dc);
 	}
 	
 	public static Criterion ltSome(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, "<", "some", dc);
 	}
 	
 	public static Criterion geSome(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, ">=", "some", dc);
 	}
 	
 	public static Criterion leSome(Object value, DetachedCriteria dc) {
 		return new SimpleSubqueryExpression(value, "<=", "some", dc);
 	}
 	
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index cad5dfc289..b39fd2d501 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1,2196 +1,2199 @@
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
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
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
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.exception.internal.SQLStateConverter;
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
 import org.hibernate.persister.entity.Lockable;
 import org.hibernate.sql.ANSICaseFragment;
 import org.hibernate.sql.ANSIJoinFragment;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.ForUpdateFragment;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.descriptor.sql.BlobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.ClobTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 /**
  * Represents a dialect of SQL implemented by a particular RDBMS.
  * Subclasses implement Hibernate compatibility with different systems.<br>
  * <br>
  * Subclasses should provide a public default constructor that <tt>register()</tt>
  * a set of type mappings and default Hibernate properties.<br>
  * <br>
  * Subclasses should be immutable.
  *
  * @author Gavin King, David Channon
  */
 public abstract class Dialect {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Dialect.class.getName());
 
 	public static final String DEFAULT_BATCH_SIZE = "15";
 	public static final String NO_BATCH = "0";
 
 	/**
 	 * Characters used for quoting SQL identifiers
 	 */
 	public static final String QUOTE = "`\"[";
 	public static final String CLOSED_QUOTE = "`\"]";
 
 	private final TypeNames typeNames = new TypeNames();
 	private final TypeNames hibernateTypeNames = new TypeNames();
 
 	private final Properties properties = new Properties();
 	private final Map<String, SQLFunction> sqlFunctions = new HashMap<String, SQLFunction>();
 	private final Set<String> sqlKeywords = new HashSet<String>();
 
 
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
 	}
 
 	/**
 	 * Get an instance of the dialect specified by the current <tt>System</tt> properties.
 	 *
 	 * @return The specified Dialect
 	 * @throws HibernateException If no dialect was specified, or if it could not be instantiated.
 	 */
 	public static Dialect getDialect() throws HibernateException {
 		String dialectName = Environment.getProperties().getProperty( Environment.DIALECT );
 		return instantiateDialect( dialectName );
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
 		String dialectName = props.getProperty( Environment.DIALECT );
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
 			return ( Dialect ) ReflectHelper.classForName( dialectName ).newInstance();
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
 	 * Get the name of the database type associated with the given
 	 * {@link java.sql.Types} typecode.
 	 *
 	 * @param code The {@link java.sql.Types} typecode
 	 * @return the database type name
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
 	public String getTypeName(int code) throws HibernateException {
 		String result = typeNames.get( code );
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
 		String result = typeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(String.format( "No type mapping for java.sql.Types code: %s, length: %s", code, length ));
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
-	 * If <code>sqlTypeDescriptor</code> is a "standard basic" SQL type
-	 * descriptor, then this method uses {@link #getSqlTypeDescriptorOverride}
-	 * to get an optional override based on the SQL code returned by
+	 * If the passed {@code sqlTypeDescriptor} allows itself to be remapped (per
+	 * {@link org.hibernate.type.descriptor.sql.SqlTypeDescriptor#canBeRemapped()}), then this method uses
+	 * {@link #getSqlTypeDescriptorOverride}  to get an optional override based on the SQL code returned by
 	 * {@link SqlTypeDescriptor#getSqlType()}.
 	 * <p/>
-	 * If this dialect does not provide an override, then this method
-	 * simply returns <code>sqlTypeDescriptor</code>
+	 * If this dialect does not provide an override or if the {@code sqlTypeDescriptor} doe not allow itself to be
+	 * remapped, then this method simply returns the original passed {@code sqlTypeDescriptor}
 	 *
 	 * @param sqlTypeDescriptor The {@link SqlTypeDescriptor} to override
 	 * @return The {@link SqlTypeDescriptor} that should be used for this dialect;
-	 *         if there is no override, then <code>sqlTypeDescriptor</code> is returned.
-	 * @throws IllegalArgumentException if <code>sqlTypeDescriptor</code> is null.
+	 *         if there is no override, then original {@code sqlTypeDescriptor} is returned.
+	 * @throws IllegalArgumentException if {@code sqlTypeDescriptor} is null.
 	 *
-	 * @see {@link #getSqlTypeDescriptorOverride}
+	 * @see #getSqlTypeDescriptorOverride
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
-	 * Returns the {@link SqlTypeDescriptor} that should override the
-	 * "standard basic" SQL type descriptor for values of the specified
-	 * column type, or null, if there is no override.
+	 * Returns the {@link SqlTypeDescriptor} that should be used to handle the given JDBC type code.  Returns
+	 * {@code null} if there is no override.
 	 *
 	 * @param sqlCode A {@link Types} constant indicating the SQL column type
-	 * @return The {@link SqlTypeDescriptor} that should override the
-	 * "standard basic" SQL type descriptor, or null, if there is no override.
+	 * @return The {@link SqlTypeDescriptor} to use as an override, or {@code null} if there is no override.
 	 */
 	protected SqlTypeDescriptor getSqlTypeDescriptorOverride(int sqlCode) {
 		SqlTypeDescriptor descriptor;
 		switch ( sqlCode ) {
 			case Types.BLOB: {
 				descriptor = useInputStreamToInsertBlob() ? BlobTypeDescriptor.STREAM_BINDING : null;
 				break;
 			}
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
+	@SuppressWarnings( {"UnusedDeclaration"})
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
+	@SuppressWarnings( {"UnusedDeclaration"})
 	protected static final LobMergeStrategy STREAM_XFER_LOB_MERGE_STRATEGY = new LobMergeStrategy() {
 		@Override
 		public Blob mergeBlob(Blob original, Blob target, SessionImplementor session) {
 			if ( original != target ) {
 				try {
 					OutputStream connectedStream = target.setBinaryStream( 1L );  // the BLOB just read during the load phase of merge
 					InputStream detachedStream = original.getBinaryStream();      // the BLOB from the detached state
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
 					OutputStream connectedStream = target.setAsciiStream( 1L );  // the CLOB just read during the load phase of merge
 					InputStream detachedStream = original.getAsciiStream();      // the CLOB from the detached state
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
 					OutputStream connectedStream = target.setAsciiStream( 1L );  // the NCLOB just read during the load phase of merge
 					InputStream detachedStream = original.getAsciiStream();      // the NCLOB from the detached state
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
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
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
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
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
 				LobCreator lobCreator = session.getFactory().getJdbcServices().getLobCreator( session );
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
-	 * {@link java.sql.Types} typecode.
+	 * {@link java.sql.Types} type code.
 	 *
-	 * @param code The {@link java.sql.Types} typecode
+	 * @param code The {@link java.sql.Types} type code
 	 * @return The Hibernate {@link org.hibernate.type.Type} name.
 	 * @throws HibernateException If no mapping was specified for that type.
 	 */
+	@SuppressWarnings( {"UnusedDeclaration"})
 	public String getHibernateTypeName(int code) throws HibernateException {
 		String result = hibernateTypeNames.get( code );
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
 		String result = hibernateTypeNames.get( code, length, precision, scale );
 		if ( result == null ) {
 			throw new HibernateException(
 					"No Hibernate type mapping for java.sql.Types code: " +
 					code +
 					", length: " +
 					length
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
 		sqlFunctions.put( name, function );
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
 		sqlKeywords.add(word);
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
 	 */
 	public boolean supportsLimit() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect's LIMIT support (if any) additionally
 	 * support specifying an offset?
 	 *
 	 * @return True if the dialect supports an offset within the limit support.
 	 */
 	public boolean supportsLimitOffset() {
 		return supportsLimit();
 	}
 
 	/**
 	 * Does this dialect support bind variables (i.e., prepared statement
 	 * parameters) for its limit/offset?
 	 *
 	 * @return True if bind variables can be used; false otherwise.
 	 */
 	public boolean supportsVariableLimit() {
 		return supportsLimit();
 	}
 
 	/**
 	 * ANSI SQL defines the LIMIT clause to be in the form LIMIT offset, limit.
 	 * Does this dialect require us to bind the parameters in reverse order?
 	 *
 	 * @return true if the correct order is limit, offset
 	 */
 	public boolean bindLimitParametersInReverseOrder() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause come at the start of the
 	 * <tt>SELECT</tt> statement, rather than at the end?
 	 *
 	 * @return true if limit parameters should come before other parameters
 	 */
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	/**
 	 * Does the <tt>LIMIT</tt> clause take a "maximum" row number instead
 	 * of a total number of returned rows?
 	 * <p/>
 	 * This is easiest understood via an example.  Consider you have a table
 	 * with 20 rows, but you only want to retrieve rows number 11 through 20.
 	 * Generally, a limit with offset would say that the offset = 11 and the
 	 * limit = 10 (we only want 10 rows at a time); this is specifying the
 	 * total number of returned rows.  Some dialects require that we instead
 	 * specify offset = 11 and limit = 20, where 20 is the "last" row we want
 	 * relative to offset (i.e. total number of rows = 20 - 11 = 9)
 	 * <p/>
 	 * So essentially, is limit relative from offset?  Or is limit absolute?
 	 *
 	 * @return True if limit is relative from offset; false otherwise.
 	 */
 	public boolean useMaxForLimit() {
 		return false;
 	}
 
 	/**
 	 * Generally, if there is no limit applied to a Hibernate query we do not apply any limits
 	 * to the SQL query.  This option forces that the limit be written to the SQL query.
 	 *
 	 * @return True to force limit into SQL query even if none specified in Hibernate query; false otherwise.
 	 */
 	public boolean forceLimitUsage() {
 		return false;
 	}
 
 	/**
 	 * Given a limit and an offset, apply the limit clause to the query.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param offset The offset of the limit
 	 * @param limit The limit of the limit ;)
 	 * @return The modified query statement with the limit applied.
 	 */
 	public String getLimitString(String query, int offset, int limit) {
 		return getLimitString( query, ( offset > 0 || forceLimitUsage() )  );
 	}
 
 	/**
 	 * Apply s limit clause to the query.
 	 * <p/>
 	 * Typically dialects utilize {@link #supportsVariableLimit() variable}
 	 * limit clauses when they support limits.  Thus, when building the
 	 * select command we do not actually need to know the limit or the offest
 	 * since we will just be using placeholders.
 	 * <p/>
 	 * Here we do still pass along whether or not an offset was specified
 	 * so that dialects not supporting offsets can generate proper exceptions.
 	 * In general, dialects will override one or the other of this method and
 	 * {@link #getLimitString(String, int, int)}.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param hasOffset Is the query requesting an offset?
 	 * @return the modified SQL
 	 */
 	protected String getLimitString(String query, boolean hasOffset) {
 		throw new UnsupportedOperationException( "Paged queries not supported by " + getClass().getName());
 	}
 
 	/**
 	 * Hibernate APIs explicitly state that setFirstResult() should be a zero-based offset. Here we allow the
 	 * Dialect a chance to convert that value based on what the underlying db or driver will expect.
 	 * <p/>
 	 * NOTE: what gets passed into {@link #getLimitString(String,int,int)} is the zero-based offset.  Dialects which
-	 * do not {@link #supportsVariableLimit} should take care to perform any needed {@link #convertToFirstRowValue}
-	 * calls prior to injecting the limit values into the SQL string.
+	 * do not {@link #supportsVariableLimit} should take care to perform any needed first-row-conversion calls prior
+	 * to injecting the limit values into the SQL string.
 	 *
 	 * @param zeroBasedFirstResult The user-supplied, zero-based first row offset.
 	 *
 	 * @return The corresponding db/dialect specific offset.
 	 *
 	 * @see org.hibernate.Query#setFirstResult
 	 * @see org.hibernate.Criteria#setFirstResult
 	 */
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Informational metadata about whether this dialect is known to support
 	 * specifying timeouts for requested lock acquisitions.
 	 *
 	 * @return True is this dialect supports specifying lock timeouts.
 	 */
 	public boolean supportsLockTimeouts() {
 		return true;
 
 	}
 
 	/**
 	 * If this dialect supports specifying lock timeouts, are those timeouts
 	 * rendered into the <tt>SQL</tt> string as parameters.  The implication
 	 * is that Hibernate will need to bind the timeout value as a parameter
 	 * in the {@link java.sql.PreparedStatement}.  If true, the param position
 	 * is always handled as the last parameter; if the dialect specifies the
 	 * lock timeout elsewhere in the <tt>SQL</tt> statement then the timeout
 	 * value should be directly rendered into the statement and this method
 	 * should return false.
 	 *
 	 * @return True if the lock timeout is rendered into the <tt>SQL</tt>
 	 * string as a parameter; false otherwise.
 	 */
 	public boolean isLockTimeoutParameterized() {
 		return false;
 	}
 
 	/**
 	 * Get a strategy instance which knows how to acquire a database-level lock
 	 * of the specified mode for this dialect.
 	 *
 	 * @param lockable The persister for the entity to be locked.
 	 * @param lockMode The type of lock to be acquired.
 	 * @return The appropriate locking strategy.
 	 * @since 3.2
 	 */
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
         switch ( lockMode ) {
             case PESSIMISTIC_FORCE_INCREMENT:
                 return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
             case PESSIMISTIC_WRITE:
                 return new PessimisticWriteSelectLockingStrategy( lockable, lockMode );
             case PESSIMISTIC_READ:
                 return new PessimisticReadSelectLockingStrategy( lockable, lockMode );
             case OPTIMISTIC:
                 return new OptimisticLockingStrategy( lockable, lockMode );
             case OPTIMISTIC_FORCE_INCREMENT:
                 return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
             default:
                 return new SelectLockingStrategy( lockable, lockMode );
         }
 	}
 
 	/**
 	 * Given LockOptions (lockMode, timeout), determine the appropriate for update fragment to use.
 	 *
 	 * @param lockOptions contains the lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockOptions lockOptions) {
         LockMode lockMode = lockOptions.getLockMode();
         return getForUpdateString( lockMode, lockOptions.getTimeOut() );
 	}
 
-    private String getForUpdateString(LockMode lockMode, int timeout){
+    @SuppressWarnings( {"deprecation"})
+	private String getForUpdateString(LockMode lockMode, int timeout){
        switch ( lockMode ) {
             case UPGRADE:
                 return getForUpdateString();
             case PESSIMISTIC_READ:
                 return getReadLockString( timeout );
             case PESSIMISTIC_WRITE:
                 return getWriteLockString( timeout );
             case UPGRADE_NOWAIT:
             case FORCE:
             case PESSIMISTIC_FORCE_INCREMENT:
                 return getForUpdateNowaitString();
             default:
                 return "";
         }
     }
 
 	/**
 	 * Given a lock mode, determine the appropriate for update fragment to use.
 	 *
 	 * @param lockMode The lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockMode lockMode) {
 		return getForUpdateString( lockMode, LockOptions.WAIT_FOREVER );
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire locks
 	 * for this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE</tt> clause string.
 	 */
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getWriteLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getReadLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 
 	/**
 	 * Is <tt>FOR UPDATE OF</tt> syntax supported?
 	 *
 	 * @return True if the database supports <tt>FOR UPDATE OF</tt> syntax;
 	 * false otherwise.
 	 */
 	public boolean forUpdateOfColumns() {
 		// by default we report no support
 		return false;
 	}
 
 	/**
 	 * Does this dialect support <tt>FOR UPDATE</tt> in conjunction with
 	 * outer joined rows?
 	 *
 	 * @return True if outer joined rows can be locked via <tt>FOR UPDATE</tt>.
 	 */
 	public boolean supportsOuterJoinForUpdate() {
 		return true;
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	public String getForUpdateString(String aliases) {
 		// by default we simply return the getForUpdateString() result since
 		// the default is to say no support for "FOR UPDATE OF ..."
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @param lockOptions the lock options to apply
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	@SuppressWarnings( {"unchecked"})
 	public String getForUpdateString(String aliases, LockOptions lockOptions) {
 		LockMode lockMode = lockOptions.getLockMode();
 		Iterator<Map.Entry<String, LockMode>> itr = lockOptions.getAliasLockIterator();
 		while ( itr.hasNext() ) {
 			// seek the highest lock mode
 			final Map.Entry<String, LockMode>entry = itr.next();
 			final LockMode lm = entry.getValue();
 			if ( lm.greaterThan(lockMode) ) {
 				lockMode = lm;
 			}
 		}
 		lockOptions.setLockMode( lockMode );
 		return getForUpdateString( lockOptions );
 	}
 
 	/**
 	 * Retrieves the <tt>FOR UPDATE NOWAIT</tt> syntax specific to this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString() {
 		// by default we report no support for NOWAIT lock semantics
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list NOWAIT</tt> fragment appropriate
 	 * for this dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF colunm_list NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString( aliases );
 	}
 
 	/**
 	 * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
 	 * whereby a "lock hint" is appends to the table name in the from clause.
 	 * <p/>
 	 * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
 	 *
 	 * @param mode The lock mode to apply
 	 * @param tableName The name of the table to which to apply the lock hint.
 	 * @return The table with any required lock hints.
 	 */
 	public String appendLockHint(LockMode mode, String tableName) {
 		return tableName;
 	}
 
 	/**
 	 * Modifies the given SQL by applying the appropriate updates for the specified
 	 * lock modes and key columns.
 	 * <p/>
 	 * The behavior here is that of an ANSI SQL <tt>SELECT FOR UPDATE</tt>.  This
 	 * method is really intended to allow dialects which do not support
 	 * <tt>SELECT FOR UPDATE</tt> to achieve this in their own fashion.
 	 *
 	 * @param sql the SQL string to modify
 	 * @param aliasedLockOptions lock options indexed by aliased table names.
 	 * @param keyColumnNames a map of key columns indexed by aliased table names.
 	 * @return the modified SQL string.
 	 */
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map keyColumnNames) {
 		return sql + new ForUpdateFragment( this, aliasedLockOptions, keyColumnNames ).toFragmentString();
 	}
 
 
 	// table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Command used to create a table.
 	 *
 	 * @return The command used to create a table.
 	 */
 	public String getCreateTableString() {
 		return "create table";
 	}
 
 	/**
 	 * Slight variation on {@link #getCreateTableString}.  Here, we have the
 	 * command used to create a table when there is no primary key and
 	 * duplicate rows are expected.
 	 * <p/>
 	 * Most databases do not care about the distinction; originally added for
 	 * Teradata support which does care.
 	 *
 	 * @return The command used to create a multiset table.
 	 */
 	public String getCreateMultisetTableString() {
 		return getCreateTableString();
 	}
 
 
 	// temporary table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support temporary tables?
 	 *
 	 * @return True if temp tables are supported; false otherwise.
 	 */
 	public boolean supportsTemporaryTables() {
 		return false;
 	}
 
 	/**
 	 * Generate a temporary table name given the base table.
 	 *
 	 * @param baseTableName The table name from which to base the temp table name.
 	 * @return The generated temp table name.
 	 */
 	public String generateTemporaryTableName(String baseTableName) {
 		return "HT_" + baseTableName;
 	}
 
 	/**
 	 * Command used to create a temporary table.
 	 *
 	 * @return The command used to create a temporary table.
 	 */
 	public String getCreateTemporaryTableString() {
 		return "create table";
 	}
 
 	/**
 	 * Get any fragments needing to be postfixed to the command for
 	 * temporary table creation.
 	 *
 	 * @return Any required postfix.
 	 */
 	public String getCreateTemporaryTablePostfix() {
 		return "";
 	}
 
 	/**
 	 * Command used to drop a temporary table.
 	 *
 	 * @return The command used to drop a temporary table.
 	 */
 	public String getDropTemporaryTableString() {
 		return "drop table";
 	}
 
 	/**
 	 * Does the dialect require that temporary table DDL statements occur in
 	 * isolation from other statements?  This would be the case if the creation
 	 * would cause any current transaction to get committed implicitly.
 	 * <p/>
 	 * JDBC defines a standard way to query for this information via the
 	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}
 	 * method.  However, that does not distinguish between temporary table
 	 * DDL and other forms of DDL; MySQL, for example, reports DDL causing a
 	 * transaction commit via its driver, even though that is not the case for
 	 * temporary table DDL.
 	 * <p/>
 	 * Possible return values and their meanings:<ul>
 	 * <li>{@link Boolean#TRUE} - Unequivocally, perform the temporary table DDL
 	 * in isolation.</li>
 	 * <li>{@link Boolean#FALSE} - Unequivocally, do <b>not</b> perform the
 	 * temporary table DDL in isolation.</li>
 	 * <li><i>null</i> - defer to the JDBC driver response in regards to
 	 * {@link java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()}</li>
 	 * </ul>
 	 *
 	 * @return see the result matrix above.
 	 */
 	public Boolean performTemporaryTableDDLInIsolation() {
 		return null;
 	}
 
 	/**
 	 * Do we need to drop the temporary table after use?
 	 *
 	 * @return True if the table should be dropped.
 	 */
 	public boolean dropTemporaryTableAfterUse() {
 		return true;
 	}
 
 
 	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Registers an OUT parameter which will be returning a
 	 * {@link java.sql.ResultSet}.  How this is accomplished varies greatly
 	 * from DB to DB, hence its inclusion (along with {@link #getResultSet}) here.
 	 *
 	 * @param statement The callable statement.
 	 * @param position The bind position at which to register the OUT param.
 	 * @return The number of (contiguous) bind positions used.
 	 * @throws SQLException Indicates problems registering the OUT param.
 	 */
 	public int registerResultSetOutParameter(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 				" does not support resultsets via stored procedures"
 			);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
 	 *
 	 * @param statement The callable statement.
 	 * @return The extracted result set.
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	public ResultSet getResultSet(CallableStatement statement) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 				" does not support resultsets via stored procedures"
 			);
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
 	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.  The default
 	 * Dialect implementation simply returns a converter based on X/Open SQLState codes.
 	 * <p/>
 	 * It is strongly recommended that specific Dialect implementations override this
 	 * method, since interpretation of a SQL error is much more accurate when based on
 	 * the ErrorCode rather than the SQLState.  Unfortunately, the ErrorCode is a vendor-
 	 * specific approach.
 	 *
 	 * @return The Dialect's preferred SQLExceptionConverter.
 	 */
 	public SQLExceptionConverter buildSQLExceptionConverter() {
 		// The default SQLExceptionConverter for all dialects is based on SQLState
 		// since SQLErrorCode is extremely vendor-specific.  Specific Dialects
 		// may override to return whatever is most appropriate for that vendor.
 //		return new SQLStateConverter( getViolatedConstraintNameExtracter() );
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
 	 * @return  The dialect-specific "case insensitive" like function.
 	 */
 	public String getCaseInsensitiveLike(){
 		return "like";
 	}
 
 	/**
 	 * @return {@code true} if the underlying Database supports case insensitive like comparison, {@code false} otherwise.
 	 * The default is {@code false}.
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
 	 * Does this dialect support the <tt>UNIQUE</tt> column syntax?
 	 *
 	 * @return boolean
 	 */
 	public boolean supportsUnique() {
 		return true;
 	}
 
     /**
      * Does this dialect support adding Unique constraints via create and alter table ?
      * @return boolean
      */
 	public boolean supportsUniqueConstraintInCreateAlterTable() {
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
 		StringBuilder res = new StringBuilder( 30 );
 
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
      * The syntax used to add a unique constraint to a table.
      *
      * @param constraintName The name of the unique constraint.
      * @return The "add unique" fragment
      */
     public String getAddUniqueConstraintString(String constraintName) {
         return " add constraint " + constraintName + " unique ";
     }
 
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
 
 	public boolean supportsCommentOn() {
 		return false;
 	}
 
 	public String getTableComment(String comment) {
 		return "";
 	}
 
 	public String getColumnComment(String comment) {
 		return "";
 	}
 
 	public boolean supportsIfExistsBeforeTableName() {
 		return false;
 	}
 
 	public boolean supportsIfExistsAfterTableName() {
 		return false;
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
 
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	public boolean supportsNotNullUnique() {
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
 	 * @return Returns the separator to use for defining cross joins when translating HQL queries.
 	 * <p/>
 	 * Typically this will be either [<tt> cross join </tt>] or [<tt>, </tt>]
 	 * <p/>
 	 * Note that the spaces are important!
 	 *
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
+	@SuppressWarnings( {"UnusedDeclaration"})
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java
index 83896f25f5..5084dcbf7e 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Oracle9iDialect.java
@@ -1,139 +1,137 @@
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
 import java.sql.Types;
 
 import org.hibernate.LockOptions;
 import org.hibernate.sql.ANSICaseFragment;
 import org.hibernate.sql.CaseFragment;
 
 /**
  * A dialect for Oracle 9i databases.
  * <p/>
- * Unlike the older (deprecated) {@link Oracle9Dialect), this version specifies
- * to not use "ANSI join syntax" because 9i does not seem to properly
- * handle it in all cases.
+ * Specifies to not use "ANSI join syntax" because 9i does not seem to properly handle it in all cases.
  *
  * @author Steve Ebersole
  */
 public class Oracle9iDialect extends Oracle8iDialect {
 	protected void registerCharacterTypeMappings() {
 		registerColumnType( Types.CHAR, "char(1 char)" );
 		registerColumnType( Types.VARCHAR, 4000, "varchar2($l char)" );
 		registerColumnType( Types.VARCHAR, "long" );
 	}
 
 	protected void registerDateTimeTypeMappings() {
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "date" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 	}
 
 	public CaseFragment createCaseFragment() {
 		// Oracle did add support for ANSI CASE statements in 9i
 		return new ANSICaseFragment();
 	}
 
 	public String getLimitString(String sql, boolean hasOffset) {
 		sql = sql.trim();
 		String forUpdateClause = null;
 		boolean isForUpdate = false;
 		final int forUpdateIndex = sql.toLowerCase().lastIndexOf( "for update") ;
 		if ( forUpdateIndex > -1 ) {
 			// save 'for update ...' and then remove it
 			forUpdateClause = sql.substring( forUpdateIndex );
 			sql = sql.substring( 0, forUpdateIndex-1 );
 			isForUpdate = true;
 		}
 
-		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 );
+		StringBuilder pagingSelect = new StringBuilder( sql.length() + 100 );
 		if (hasOffset) {
 			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
 		}
 		else {
 			pagingSelect.append("select * from ( ");
 		}
 		pagingSelect.append(sql);
 		if (hasOffset) {
 			pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
 		}
 		else {
 			pagingSelect.append(" ) where rownum <= ?");
 		}
 
 		if ( isForUpdate ) {
 			pagingSelect.append( " " );
 			pagingSelect.append( forUpdateClause );
 		}
 
 		return pagingSelect.toString();
 	}
 
 	public String getSelectClauseNullString(int sqlType) {
 		return getBasicSelectClauseNullString( sqlType );
 	}
 
 	public String getCurrentTimestampSelectString() {
 		return "select systimestamp from dual";
 	}
 
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
 	}
 
 	// locking support
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	public String getWriteLockString(int timeout) {
 		if ( timeout == LockOptions.NO_WAIT ) {
 			return " for update nowait";
 		}
 		else if ( timeout > 0 ) {
 			// convert from milliseconds to seconds
 			float seconds = timeout / 1000.0f;
 			timeout = Math.round(seconds);
 			return " for update wait " + timeout;
 		}
 		else
 			return " for update";
 	}
 
 	public String getReadLockString(int timeout) {
 		return getWriteLockString( timeout );
 	}
 	/**
 	 * HHH-4907, I don't know if oracle 8 supports this syntax, so I'd think it is better add this 
 	 * method here. Reopen this issue if you found/know 8 supports it.
 	 */
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return true;
 	}
 
 	public boolean supportsTupleDistinctCounts() {
 		return false;
 	}	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2005Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2005Dialect.java
index 70972281f4..0f88b27306 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2005Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SQLServer2005Dialect.java
@@ -1,199 +1,198 @@
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
 
 import java.sql.Types;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * A dialect for Microsoft SQL 2005. (HHH-3936 fix)
  *
  * @author Yoryos Valotasios
  */
 public class SQLServer2005Dialect extends SQLServerDialect {
 	private static final String SELECT = "select";
 	private static final String FROM = "from";
 	private static final String DISTINCT = "distinct";
 	private static final int MAX_LENGTH = 8000;
 
 	/**
 	 * Regular expression for stripping alias
 	 */
 	private static final Pattern ALIAS_PATTERN = Pattern.compile( "\\sas[^,]+(,?)" );
 
 	public SQLServer2005Dialect() {
 		// HHH-3965 fix
 		// As per http://www.sql-server-helper.com/faq/sql-server-2005-varchar-max-p01.aspx
 		// use varchar(max) and varbinary(max) instead of TEXT and IMAGE types
 		registerColumnType( Types.BLOB, "varbinary(MAX)" );
 		registerColumnType( Types.VARBINARY, "varbinary(MAX)" );
 		registerColumnType( Types.VARBINARY, MAX_LENGTH, "varbinary($l)" );
 		registerColumnType( Types.LONGVARBINARY, "varbinary(MAX)" );
 
 		registerColumnType( Types.CLOB, "varchar(MAX)" );
 		registerColumnType( Types.LONGVARCHAR, "varchar(MAX)" );
 		registerColumnType( Types.VARCHAR, "varchar(MAX)" );
 		registerColumnType( Types.VARCHAR, MAX_LENGTH, "varchar($l)" );
 
 		registerColumnType( Types.BIGINT, "bigint" );
 		registerColumnType( Types.BIT, "bit" );
 		registerColumnType( Types.BOOLEAN, "bit" );
 
 
 		registerFunction( "row_number", new NoArgSQLFunction( "row_number", StandardBasicTypes.INTEGER, true ) );
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return true;
 	}
 
 	@Override
 	public boolean bindLimitParametersFirst() {
 		return false;
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return true;
 	}
 
 	@Override
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		// Our dialect paginated results aren't zero based. The first row should get the number 1 and so on
 		return zeroBasedFirstResult + 1;
 	}
 
 	@Override
 	public String getLimitString(String query, int offset, int limit) {
 		// We transform the query to one with an offset and limit if we have an offset and limit to bind
 		if ( offset > 1 || limit > 1 ) {
 			return getLimitString( query, true );
 		}
 		return query;
 	}
 
 	/**
 	 * Add a LIMIT clause to the given SQL SELECT (HHH-2655: ROW_NUMBER for Paging)
 	 *
 	 * The LIMIT SQL will look like:
 	 *
 	 * <pre>
 	 * WITH query AS (
 	 *   SELECT ROW_NUMBER() OVER (ORDER BY orderby) as __hibernate_row_nr__,
 	 *   original_query_without_orderby
 	 * )
 	 * SELECT * FROM query WHERE __hibernate_row_nr__ BEETWIN offset AND offset + last
 	 * </pre>
 	 *
 	 * @param querySqlString The SQL statement to base the limit query off of.
 	 * @param hasOffset Is the query requesting an offset?
 	 *
 	 * @return A new SQL statement with the LIMIT clause applied.
 	 */
 	@Override
 	public String getLimitString(String querySqlString, boolean hasOffset) {
 		StringBuilder sb = new StringBuilder( querySqlString.trim().toLowerCase() );
 
 		int orderByIndex = sb.indexOf( "order by" );
 		CharSequence orderby = orderByIndex > 0 ? sb.subSequence( orderByIndex, sb.length() )
 				: "ORDER BY CURRENT_TIMESTAMP";
 
 		// Delete the order by clause at the end of the query
 		if ( orderByIndex > 0 ) {
 			sb.delete( orderByIndex, orderByIndex + orderby.length() );
 		}
 
 		// HHH-5715 bug fix
 		replaceDistinctWithGroupBy( sb );
 
 		insertRowNumberFunction( sb, orderby );
 
 		// Wrap the query within a with statement:
 		sb.insert( 0, "WITH query AS (" ).append( ") SELECT * FROM query " );
 		sb.append( "WHERE __hibernate_row_nr__ >= ? AND __hibernate_row_nr__ < ?" );
 
 		return sb.toString();
 	}
 
 	/**
 	 * Utility method that checks if the given sql query is a select distinct one and if so replaces the distinct select
-	 * with an equivalent simple select with a group by clause. See
-	 * {@link SQLServer2005DialectTestCase#testReplaceDistinctWithGroupBy()}
+	 * with an equivalent simple select with a group by clause.
 	 *
 	 * @param sql an sql query
 	 */
 	protected static void replaceDistinctWithGroupBy(StringBuilder sql) {
 		int distinctIndex = sql.indexOf( DISTINCT );
 		if ( distinctIndex > 0 ) {
 			sql.delete( distinctIndex, distinctIndex + DISTINCT.length() + 1 );
 			sql.append( " group by" ).append( getSelectFieldsWithoutAliases( sql ) );
 		}
 	}
 
 	/**
 	 * This utility method searches the given sql query for the fields of the select statement and returns them without
-	 * the aliases. See {@link SQLServer2005DialectTestCase#testGetSelectFieldsWithoutAliases()}
+	 * the aliases.
 	 *
 	 * @param sql sql query
 	 *
 	 * @return the fields of the select statement without their alias
 	 */
 	protected static CharSequence getSelectFieldsWithoutAliases(StringBuilder sql) {
 		String select = sql.substring( sql.indexOf( SELECT ) + SELECT.length(), sql.indexOf( FROM ) );
 
 		// Strip the as clauses
 		return stripAliases( select );
 	}
 
 	/**
-	 * Utility method that strips the aliases. See {@link SQLServer2005DialectTestCase#testStripAliases()}
+	 * Utility method that strips the aliases.
 	 *
 	 * @param str string to replace the as statements
 	 *
 	 * @return a string without the as statements
 	 */
 	protected static String stripAliases(String str) {
 		Matcher matcher = ALIAS_PATTERN.matcher( str );
 		return matcher.replaceAll( "$1" );
 	}
 
 	/**
 	 * Right after the select statement of a given query we must place the row_number function
 	 *
 	 * @param sql the initial sql query without the order by clause
 	 * @param orderby the order by clause of the query
 	 */
 	protected static void insertRowNumberFunction(StringBuilder sql, CharSequence orderby) {
 		// Find the end of the select statement
 		int selectEndIndex = sql.indexOf( FROM );
 
 		// Insert after the select statement the row_number() function:
 		sql.insert( selectEndIndex - 1, ", ROW_NUMBER() OVER (" + orderby + ") as __hibernate_row_nr__" );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
index 40b2c230ee..1fedb944ad 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/StatefulPersistenceContext.java
@@ -1,1682 +1,1692 @@
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
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.IdentityHashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Map.Entry;
 
 import org.apache.commons.collections.map.AbstractReferenceMap;
 import org.apache.commons.collections.map.ReferenceMap;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
 import org.hibernate.MappingException;
 import org.hibernate.NonUniqueObjectException;
 import org.hibernate.PersistentObjectException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.loading.internal.LoadContexts;
 import org.hibernate.engine.spi.AssociationKey;
 import org.hibernate.engine.spi.BatchFetchQueue;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.Status;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 import org.hibernate.tuple.ElementWrapper;
 
 /**
- * A <tt>PersistenceContext</tt> represents the state of persistent "stuff" which
- * Hibernate is tracking.  This includes persistent entities, collections,
- * as well as proxies generated.
+ * A <strong>stateful</strong> implementation of the {@link PersistenceContext} contract meaning that we maintain this
+ * state throughout the life of the persistence context.
  * </p>
- * There is meant to be a one-to-one correspondence between a SessionImpl and
- * a PersistentContext.  The SessionImpl uses the PersistentContext to track
- * the current state of its context.  Event-listeners then use the
- * PersistentContext to drive their processing.
+ * IMPL NOTE: There is meant to be a one-to-one correspondence between a {@link org.hibernate.internal.SessionImpl}
+ * and a PersistentContext.  Event listeners and other Session collaborators then use the PersistentContext to drive
+ * their processing.
  *
  * @author Steve Ebersole
  */
 public class StatefulPersistenceContext implements PersistenceContext {
 
-	public static final Object NO_ROW = new MarkerObject( "NO_ROW" );
-
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, StatefulPersistenceContext.class.getName() );
 
+	public static final Object NO_ROW = new MarkerObject( "NO_ROW" );
+
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
-	private Map parentsByChild;
+	private Map<Object,Object> parentsByChild;
 
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
 
-		entitiesByKey = new HashMap( INIT_COLL_SIZE );
-		entitiesByUniqueKey = new HashMap( INIT_COLL_SIZE );
-		proxiesByKey = new ReferenceMap( AbstractReferenceMap.HARD, AbstractReferenceMap.WEAK );
-		entitySnapshotsByKey = new HashMap( INIT_COLL_SIZE );
+		entitiesByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
+		entitiesByUniqueKey = new HashMap<EntityUniqueKey, Object>( INIT_COLL_SIZE );
+		//noinspection unchecked
+		proxiesByKey = (Map<EntityKey, Object>) new ReferenceMap( AbstractReferenceMap.HARD, AbstractReferenceMap.WEAK );
+		entitySnapshotsByKey = new HashMap<EntityKey, Object>( INIT_COLL_SIZE );
 
 		entityEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		collectionEntries = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 		parentsByChild = IdentityMap.instantiateSequenced( INIT_COLL_SIZE );
 
-		collectionsByKey = new HashMap( INIT_COLL_SIZE );
+		collectionsByKey = new HashMap<CollectionKey, PersistentCollection>( INIT_COLL_SIZE );
 		arrayHolders = new IdentityHashMap<Object, PersistentCollection>( INIT_COLL_SIZE );
 
-		nullifiableEntityKeys = new HashSet();
+		nullifiableEntityKeys = new HashSet<EntityKey>();
 
 		initTransientState();
 	}
 
 	private void initTransientState() {
-		nullAssociations = new HashSet( INIT_COLL_SIZE );
-		nonlazyCollections = new ArrayList( INIT_COLL_SIZE );
+		nullAssociations = new HashSet<AssociationKey>( INIT_COLL_SIZE );
+		nonlazyCollections = new ArrayList<PersistentCollection>( INIT_COLL_SIZE );
 	}
 
+	@Override
 	public boolean isStateless() {
 		return false;
 	}
 
+	@Override
 	public SessionImplementor getSession() {
 		return session;
 	}
 
+	@Override
 	public LoadContexts getLoadContexts() {
 		if ( loadContexts == null ) {
 			loadContexts = new LoadContexts( this );
 		}
 		return loadContexts;
 	}
 
+	@Override
 	public void addUnownedCollection(CollectionKey key, PersistentCollection collection) {
 		if (unownedCollections==null) {
 			unownedCollections = new HashMap<CollectionKey,PersistentCollection>(8);
 		}
 		unownedCollections.put( key, collection );
 	}
 
+	@Override
 	public PersistentCollection useUnownedCollection(CollectionKey key) {
 		if ( unownedCollections == null ) {
 			return null;
 		}
 		else {
 			return unownedCollections.remove(key);
 		}
 	}
 
-	/**
-	 * Get the <tt>BatchFetchQueue</tt>, instantiating one if
-	 * necessary.
-	 */
+	@Override
 	public BatchFetchQueue getBatchFetchQueue() {
 		if (batchFetchQueue==null) {
 			batchFetchQueue = new BatchFetchQueue(this);
 		}
 		return batchFetchQueue;
 	}
 
+	@Override
 	public void clear() {
 		for ( Object o : proxiesByKey.values() ) {
 			final LazyInitializer li = ((HibernateProxy) o).getHibernateLazyInitializer();
 			li.unsetSession();
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
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isDefaultReadOnly() {
 		return defaultReadOnly;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void setDefaultReadOnly(boolean defaultReadOnly) {
 		this.defaultReadOnly = defaultReadOnly;
 	}
 
+	@Override
 	public boolean hasNonReadOnlyEntities() {
 		return hasNonReadOnlyEntities;
 	}
 
+	@Override
 	public void setEntryStatus(EntityEntry entry, Status status) {
 		entry.setStatus(status);
 		setHasNonReadOnlyEnties(status);
 	}
 
 	private void setHasNonReadOnlyEnties(Status status) {
 		if ( status==Status.DELETED || status==Status.MANAGED || status==Status.SAVING ) {
 			hasNonReadOnlyEntities = true;
 		}
 	}
 
+	@Override
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
+	@Override
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
 
+	@Override
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
-		for ( int i = 0; i < props.length; i++ ) {
-			if ( !updateable[ props[i] ] ) {
+		for ( int prop : props ) {
+			if ( !updateable[prop] ) {
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
+	@Override
 	public Object[] getCachedDatabaseSnapshot(EntityKey key) {
 		Object snapshot = entitySnapshotsByKey.get( key );
 		if ( snapshot == NO_ROW ) {
 			throw new IllegalStateException( "persistence context reported no row snapshot for " + MessageHelper.infoString( key.getEntityName(), key.getIdentifier() ) );
 		}
 		return ( Object[] ) snapshot;
 	}
 
-	/*public void removeDatabaseSnapshot(EntityKey key) {
-		entitySnapshotsByKey.remove(key);
-	}*/
-
+	@Override
 	public void addEntity(EntityKey key, Object entity) {
 		entitiesByKey.put(key, entity);
 		getBatchFetchQueue().removeBatchLoadableEntityKey(key);
 	}
 
 	/**
 	 * Get the entity instance associated with the given
 	 * <tt>EntityKey</tt>
 	 */
+	@Override
 	public Object getEntity(EntityKey key) {
 		return entitiesByKey.get(key);
 	}
 
+	@Override
 	public boolean containsEntity(EntityKey key) {
 		return entitiesByKey.containsKey(key);
 	}
 
 	/**
 	 * Remove an entity from the session cache, also clear
 	 * up other state associated with the entity, all except
 	 * for the <tt>EntityEntry</tt>
 	 */
+	@Override
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
+	@Override
 	public Object getEntity(EntityUniqueKey euk) {
 		return entitiesByUniqueKey.get(euk);
 	}
 
 	/**
 	 * Add an entity to the cache by unique key
 	 */
+	@Override
 	public void addEntity(EntityUniqueKey euk, Object entity) {
 		entitiesByUniqueKey.put(euk, entity);
 	}
 
 	/**
 	 * Retrieve the EntityEntry representation of the given entity.
 	 *
 	 * @param entity The entity for which to locate the EntityEntry.
 	 * @return The EntityEntry for the given entity.
 	 */
+	@Override
 	public EntityEntry getEntry(Object entity) {
 		return entityEntries.get(entity);
 	}
 
 	/**
 	 * Remove an entity entry from the session cache
 	 */
+	@Override
 	public EntityEntry removeEntry(Object entity) {
 		return entityEntries.remove(entity);
 	}
 
 	/**
 	 * Is there an EntityEntry for this instance?
 	 */
+	@Override
 	public boolean isEntryFor(Object entity) {
 		return entityEntries.containsKey(entity);
 	}
 
 	/**
 	 * Get the collection entry for a persistent collection
 	 */
+	@Override
 	public CollectionEntry getCollectionEntry(PersistentCollection coll) {
 		return collectionEntries.get(coll);
 	}
 
 	/**
 	 * Adds an entity to the internal caches.
 	 */
+	@Override
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
-			boolean lazyPropertiesAreUnfetched
-	) {
-
+			boolean lazyPropertiesAreUnfetched) {
 		addEntity( entityKey, entity );
-
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
-			);
+		);
 	}
 
 
 	/**
 	 * Generates an appropriate EntityEntry instance and adds it
 	 * to the event source's internal caches.
 	 */
+	@Override
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
 				lazyPropertiesAreUnfetched
 		);
 		entityEntries.put(entity, e);
 
 		setHasNonReadOnlyEnties(status);
 		return e;
 	}
 
+	@Override
 	public boolean containsCollection(PersistentCollection collection) {
 		return collectionEntries.containsKey(collection);
 	}
 
+	@Override
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
+	@Override
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
+	@Override
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
+	@Override
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
+	@Override
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
+	@Override
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
+	@Override
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
+	@Override
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
+	@Override
 	public Object proxyFor(Object impl) throws HibernateException {
 		EntityEntry e = getEntry(impl);
 		return proxyFor( e.getPersister(), e.getEntityKey(), impl );
 	}
 
 	/**
 	 * Get the entity that owns this persistent collection
 	 */
+	@Override
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
+	@Override
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
+	@Override
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
+	@Override
 	public void addUninitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) {
 		CollectionEntry ce = new CollectionEntry(collection, persister, id, flushing);
 		addCollection(collection, ce, id);
 	}
 
 	/**
 	 * add a detached uninitialized collection
 	 */
+	@Override
 	public void addUninitializedDetachedCollection(CollectionPersister persister, PersistentCollection collection) {
 		CollectionEntry ce = new CollectionEntry( persister, collection.getKey() );
 		addCollection( collection, ce, collection.getKey() );
 	}
 
 	/**
 	 * Add a new collection (ie. a newly created one, just instantiated by the
 	 * application, with no database state or snapshot)
 	 * @param collection The collection to be associated with the persistence context
 	 */
+	@Override
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
+	@Override
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
+	@Override
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
+	@Override
 	public PersistentCollection getCollection(CollectionKey collectionKey) {
 		return collectionsByKey.get( collectionKey );
 	}
 
 	/**
 	 * Register a collection for non-lazy loading at the end of the
 	 * two-phase load
 	 */
+	@Override
 	public void addNonLazyCollection(PersistentCollection collection) {
 		nonlazyCollections.add(collection);
 	}
 
 	/**
 	 * Force initialization of all non-lazy collections encountered during
 	 * the current two-phase load (actually, this is a no-op, unless this
 	 * is the "outermost" load)
 	 */
+	@Override
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
+	@Override
 	public PersistentCollection getCollectionHolder(Object array) {
 		return arrayHolders.get(array);
 	}
 
 	/**
 	 * Register a <tt>PersistentCollection</tt> object for an array.
 	 * Associates a holder with an array - MUST be called after loading
 	 * array, since the array instance is not created until endLoad().
 	 */
+	@Override
 	public void addCollectionHolder(PersistentCollection holder) {
 		//TODO:refactor + make this method private
 		arrayHolders.put( holder.getValue(), holder );
 	}
 
+	@Override
 	public PersistentCollection removeCollectionHolder(Object array) {
 		return arrayHolders.remove(array);
 	}
 
 	/**
 	 * Get the snapshot of the pre-flush collection state
 	 */
+	@Override
 	public Serializable getSnapshot(PersistentCollection coll) {
 		return getCollectionEntry(coll).getSnapshot();
 	}
 
 	/**
 	 * Get the collection entry for a collection passed to filter,
 	 * which might be a collection wrapper, an array, or an unwrapped
 	 * collection. Return null if there is no entry.
 	 */
+	@Override
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
+	@Override
 	public Object getProxy(EntityKey key) {
 		return proxiesByKey.get(key);
 	}
 
 	/**
 	 * Add a proxy to the session cache
 	 */
+	@Override
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
+	@Override
 	public Object removeProxy(EntityKey key) {
 		if ( batchFetchQueue != null ) {
 			batchFetchQueue.removeBatchLoadableEntityKey( key );
 			batchFetchQueue.removeSubselect( key );
 		}
 		return proxiesByKey.remove( key );
 	}
 
 	/**
-	 * Record the fact that an entity does not exist in the database
-	 *
-	 * @param key the primary key of the entity
-	 */
-	/*public void addNonExistantEntityKey(EntityKey key) {
-		nonExistantEntityKeys.add(key);
-	}*/
-
-	/**
-	 * Record the fact that an entity does not exist in the database
-	 *
-	 * @param key a unique key of the entity
-	 */
-	/*public void addNonExistantEntityUniqueKey(EntityUniqueKey key) {
-		nonExistentEntityUniqueKeys.add(key);
-	}*/
-
-	/*public void removeNonExist(EntityKey key) {
-		nonExistantEntityKeys.remove(key);
-	}*/
-
-	/**
 	 * Retrieve the set of EntityKeys representing nullifiable references
 	 */
+	@Override
 	public HashSet getNullifiableEntityKeys() {
 		return nullifiableEntityKeys;
 	}
 
+	@Override
 	public Map getEntitiesByKey() {
 		return entitiesByKey;
 	}
 
 	public Map getProxiesByKey() {
 		return proxiesByKey;
 	}
 
+	@Override
 	public Map getEntityEntries() {
 		return entityEntries;
 	}
 
+	@Override
 	public Map getCollectionEntries() {
 		return collectionEntries;
 	}
 
+	@Override
 	public Map getCollectionsByKey() {
 		return collectionsByKey;
 	}
 
-	/**
-	 * Do we already know that the entity does not exist in the
-	 * database?
-	 */
-	/*public boolean isNonExistant(EntityKey key) {
-		return nonExistantEntityKeys.contains(key);
-	}*/
-
-	/**
-	 * Do we already know that the entity does not exist in the
-	 * database?
-	 */
-	/*public boolean isNonExistant(EntityUniqueKey key) {
-		return nonExistentEntityUniqueKeys.contains(key);
-	}*/
-
+	@Override
 	public int getCascadeLevel() {
 		return cascading;
 	}
 
+	@Override
 	public int incrementCascadeLevel() {
 		return ++cascading;
 	}
 
+	@Override
 	public int decrementCascadeLevel() {
 		return --cascading;
 	}
 
+	@Override
 	public boolean isFlushing() {
 		return flushing;
 	}
 
+	@Override
 	public void setFlushing(boolean flushing) {
 		this.flushing = flushing;
 	}
 
 	/**
 	 * Call this before beginning a two-phase load
 	 */
+	@Override
 	public void beforeLoad() {
 		loadCounter++;
 	}
 
 	/**
 	 * Call this after finishing a two-phase load
 	 */
+	@Override
 	public void afterLoad() {
 		loadCounter--;
 	}
 
+	@Override
 	public boolean isLoadFinished() {
 		return loadCounter == 0;
 	}
+
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
+	@Override
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
-			Iterator mergeMapItr = mergeMap.entrySet().iterator();
-			while ( mergeMapItr.hasNext() ) {
-				final Map.Entry mergeMapEntry = ( Map.Entry ) mergeMapItr.next();
+			for ( Object o : mergeMap.entrySet() ) {
+				final Entry mergeMapEntry = (Entry) o;
 				if ( mergeMapEntry.getKey() instanceof HibernateProxy ) {
-					final HibernateProxy proxy = ( HibernateProxy ) mergeMapEntry.getKey();
+					final HibernateProxy proxy = (HibernateProxy) mergeMapEntry.getKey();
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
+	@Override
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
-			Object potentialParent
-	){
+			Object potentialParent){
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
+	@Override
 	public void addNullProperty(EntityKey ownerKey, String propertyName) {
 		nullAssociations.add( new AssociationKey(ownerKey, propertyName) );
 	}
 
 	/**
 	 * Is the association property belonging to the keyed entity null?
 	 */
+	@Override
 	public boolean isPropertyNull(EntityKey ownerKey, String propertyName) {
 		return nullAssociations.contains( new AssociationKey(ownerKey, propertyName) );
 	}
 
 	private void clearNullProperties() {
 		nullAssociations.clear();
 	}
 
+	@Override
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
 
+	@Override
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
 
+	@Override
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
-		Iterator<EntityKey> entityKeyIterator = nullifiableEntityKeys.iterator();
-		while ( entityKeyIterator.hasNext() ) {
-			EntityKey entry = entityKeyIterator.next();
+		for ( EntityKey entry : nullifiableEntityKeys ) {
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
-			rtn.entitiesByKey = new HashMap( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
+			rtn.entitiesByKey = new HashMap<EntityKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entitiesByUniqueKey entries");
-			rtn.entitiesByUniqueKey = new HashMap( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
+			rtn.entitiesByUniqueKey = new HashMap<EntityUniqueKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitiesByUniqueKey.put( EntityUniqueKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] proxiesByKey entries");
+			//noinspection unchecked
 			rtn.proxiesByKey = new ReferenceMap( AbstractReferenceMap.HARD, AbstractReferenceMap.WEAK, count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count, .75f );
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
-			rtn.entitySnapshotsByKey = new HashMap( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
+			rtn.entitySnapshotsByKey = new HashMap<EntityKey,Object>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				rtn.entitySnapshotsByKey.put( EntityKey.deserialize( ois, session ), ois.readObject() );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] entityEntries entries");
 			rtn.entityEntries = IdentityMap.instantiateSequenced( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
 			for ( int i = 0; i < count; i++ ) {
 				Object entity = ois.readObject();
 				EntityEntry entry = EntityEntry.deserialize( ois, session );
 				rtn.entityEntries.put( entity, entry );
 			}
 
 			count = ois.readInt();
 			if ( tracing ) LOG.trace("Starting deserialization of [" + count + "] collectionsByKey entries");
-			rtn.collectionsByKey = new HashMap( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
+			rtn.collectionsByKey = new HashMap<CollectionKey,PersistentCollection>( count < INIT_COLL_SIZE ? INIT_COLL_SIZE : count );
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
-			rtn.nullifiableEntityKeys = new HashSet();
+			rtn.nullifiableEntityKeys = new HashSet<EntityKey>();
 			for ( int i = 0; i < count; i++ ) {
 				rtn.nullifiableEntityKeys.add( EntityKey.deserialize( ois, session ) );
 			}
 
 		}
 		catch ( HibernateException he ) {
 			throw new InvalidObjectException( he.getMessage() );
 		}
 
 		return rtn;
 	}
 
-	/**
-	 * @see org.hibernate.engine.spi.PersistenceContext#addChildParent(java.lang.Object, java.lang.Object)
-	 */
+	@Override
 	public void addChildParent(Object child, Object parent) {
 		parentsByChild.put(child, parent);
 	}
 
-	/**
-	 * @see org.hibernate.engine.spi.PersistenceContext#removeChildParent(java.lang.Object)
-	 */
+	@Override
 	public void removeChildParent(Object child) {
 	   parentsByChild.remove(child);
 	}
 
 
 	private HashMap<String,List<Serializable>> insertedKeysMap;
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void registerInsertedKey(EntityPersister persister, Serializable id) {
-		// we only are about regsitering these if the persister defines caching
+		// we only are worried about registering these if the persister defines caching
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
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ExtractedDatabaseMetaData.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ExtractedDatabaseMetaData.java
index 67d360d309..6d61608fc8 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ExtractedDatabaseMetaData.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/ExtractedDatabaseMetaData.java
@@ -1,133 +1,144 @@
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
 package org.hibernate.engine.jdbc.spi;
 
 import java.util.LinkedHashSet;
 import java.util.Set;
 
 import org.hibernate.engine.jdbc.internal.TypeInfo;
 
 /**
  * Information extracted from {@link java.sql.DatabaseMetaData} regarding what the JDBC driver reports as
  * being supported or not.  Obviously {@link java.sql.DatabaseMetaData} reports many things, these are a few in
  * which we have particular interest.
  *
  * @author Steve Ebersole
  */
+@SuppressWarnings( {"UnusedDeclaration"})
 public interface ExtractedDatabaseMetaData {
 
 	public enum SQLStateType {
 		XOpen,
 		SQL99,
 		UNKOWN
 	}
 
 	/**
 	 * Did the driver report to supporting scrollable result sets?
 	 *
 	 * @return True if the driver reported to support {@link java.sql.ResultSet#TYPE_SCROLL_INSENSITIVE}.
+	 *
 	 * @see java.sql.DatabaseMetaData#supportsResultSetType
 	 */
 	public boolean supportsScrollableResults();
 
 	/**
 	 * Did the driver report to supporting retrieval of generated keys?
 	 *
 	 * @return True if the if the driver reported to support calls to {@link java.sql.Statement#getGeneratedKeys}
+	 *
 	 * @see java.sql.DatabaseMetaData#supportsGetGeneratedKeys
 	 */
 	public boolean supportsGetGeneratedKeys();
 
 	/**
 	 * Did the driver report to supporting batched updates?
 	 *
 	 * @return True if the driver supports batched updates
+	 *
 	 * @see java.sql.DatabaseMetaData#supportsBatchUpdates
 	 */
 	public boolean supportsBatchUpdates();
 
 	/**
 	 * Did the driver report to support performing DDL within transactions?
 	 *
 	 * @return True if the drivers supports DDL statements within transactions.
+	 *
 	 * @see java.sql.DatabaseMetaData#dataDefinitionIgnoredInTransactions
 	 */
 	public boolean supportsDataDefinitionInTransaction();
 
 	/**
 	 * Did the driver report to DDL statements performed within a transaction performing an implicit commit of the
 	 * transaction.
 	 *
 	 * @return True if the driver/database performs an implicit commit of transaction when DDL statement is
 	 * performed
+	 *
 	 * @see java.sql.DatabaseMetaData#dataDefinitionCausesTransactionCommit()
 	 */
 	public boolean doesDataDefinitionCauseTransactionCommit();
 
 	/**
 	 * Get the list of extra keywords (beyond standard SQL92 keywords) reported by the driver.
 	 *
 	 * @return The extra keywords used by this database.
+	 *
 	 * @see java.sql.DatabaseMetaData#getSQLKeywords()
 	 */
 	public Set<String> getExtraKeywords();
 
 	/**
 	 * Retrieve the type of codes the driver says it uses for {@code SQLState}.  They might follow either
 	 * the X/Open standard or the SQL92 standard.
 	 *
 	 * @return The SQLState strategy reportedly used by this driver/database.
+	 *
 	 * @see java.sql.DatabaseMetaData#getSQLStateType()
 	 */
 	public SQLStateType getSqlStateType();
 
 	/**
 	 * Did the driver report that updates to a LOB locator affect a copy of the LOB?
 	 *
 	 * @return True if updates to the state of a LOB locator update only a copy.
+	 *
 	 * @see java.sql.DatabaseMetaData#locatorsUpdateCopy()
 	 */
 	public boolean doesLobLocatorUpdateCopy();
 
 	/**
 	 * Retrieve the name of the schema in effect when we connected to the database.
 	 *
 	 * @return The schema name
 	 */
 	public String getConnectionSchemaName();
 
 	/**
 	 * Retrieve the name of the catalog in effect when we connected to the database.
 	 *
 	 * @return The catalog name
 	 */
 	public String getConnectionCatalogName();
 
 	/**
 	 * Set of type info reported by the driver.
 	 *
-	 * @return
+	 * @return The type information obtained from the driver.
+	 *
+	 * @see java.sql.DatabaseMetaData#getTypeInfo()
 	 */
 	public LinkedHashSet<TypeInfo> getTypeInfoSet();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcCoordinator.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcCoordinator.java
index c35cf42e95..7240f6014a 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcCoordinator.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/JdbcCoordinator.java
@@ -1,140 +1,140 @@
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
 package org.hibernate.engine.jdbc.spi;
 
 import java.io.Serializable;
 import java.sql.Connection;
 
 import org.hibernate.engine.jdbc.batch.spi.Batch;
 import org.hibernate.engine.jdbc.batch.spi.BatchKey;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.jdbc.WorkExecutorVisitable;
 
 /**
  * Coordinates JDBC-related activities.
  *
  * @author Steve Ebersole
  */
 public interface JdbcCoordinator extends Serializable {
 	/**
 	 * Retrieve the transaction coordinator associated with this JDBC coordinator.
 	 *
 	 * @return The transaction coordinator
 	 */
 	public TransactionCoordinator getTransactionCoordinator();
 
 	/**
 	 * Retrieves the logical connection associated with this JDBC coordinator.
 	 *
 	 * @return The logical connection
 	 */
 	public LogicalConnectionImplementor getLogicalConnection();
 
 	/**
 	 * Get a batch instance.
 	 *
 	 * @param key The unique batch key.
 	 *
 	 * @return The batch
 	 */
 	public Batch getBatch(BatchKey key);
 
 	/**
 	 * Execute the currently managed batch (if any)
 	 */
 	public void executeBatch();
 
 	/**
 	 * Abort the currently managed batch (if any)
 	 */
 	public void abortBatch();
 
 	/**
 	 * Obtain the statement preparer associated with this JDBC coordinator.
 	 *
 	 * @return This coordinator's statement preparer
 	 */
 	public StatementPreparer getStatementPreparer();
 
 	/**
 	 * Callback to let us know that a flush is beginning.  We use this fact
 	 * to temporarily circumvent aggressive connection releasing until after
 	 * the flush cycle is complete {@link #flushEnding()}
 	 */
 	public void flushBeginning();
 
 	/**
 	 * Callback to let us know that a flush is ending.  We use this fact to
 	 * stop circumventing aggressive releasing connections.
 	 */
 	public void flushEnding();
 
 	/**
 	 * Close this coordinator and release and resources.
 	 *
 	 * @return The {@link Connection} associated with the managed {@link #getLogicalConnection() logical connection}
 	 *
-	 * @see {@link LogicalConnection#close()}
+	 * @see LogicalConnection#close
 	 */
 	public Connection close();
 
 	/**
 	 * Signals the end of transaction.
 	 * <p/>
 	 * Intended for use from the transaction coordinator, after local transaction completion.  Used to conditionally
 	 * release the JDBC connection aggressively if the configured release mode indicates.
 	 */
 	public void afterTransaction();
 
 	/**
 	 * Perform the requested work handling exceptions, coordinating and handling return processing.
 	 *
 	 * @param work The work to be performed.
 	 * @param <T> The result type.
 	 * @return The work result.
 	 */
 	public <T> T coordinateWork(WorkExecutorVisitable<T> work);
 
 	/**
 	 * Attempt to cancel the last query sent to the JDBC driver.
 	 */
 	public void cancelLastQuery();
 
 	/**
 	 * Set the effective transaction timeout period for the current transaction, in seconds.
 	 *
 	 * @param seconds The number of seconds before a time out should occur.
 	 */
 	public void setTransactionTimeOut(int seconds);
 
     /**
 	 * Calculate the amount of time, in seconds, still remaining before transaction timeout occurs.
 	 *
 	 * @return The number of seconds remaining until until a transaction timeout occurs.  A negative value indicates
 	 * no timeout was requested.
 	 *
 	 * @throws org.hibernate.TransactionException Indicates the time out period has already been exceeded.
 	 */
     public int determineRemainingTransactionTimeOutPeriod();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnection.java b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnection.java
index f267d371d2..ac546e8647 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnection.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/LogicalConnection.java
@@ -1,93 +1,93 @@
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
 package org.hibernate.engine.jdbc.spi;
 
 import java.io.Serializable;
 import java.sql.Connection;
 
 /**
  * LogicalConnection contract
  *
  * @author Steve Ebersole
  */
 public interface LogicalConnection extends Serializable {
 	/**
 	 * Is this logical connection open?  Another phraseology sometimes used is: "are we
 	 * logically connected"?
 	 *
 	 * @return True if logically connected; false otherwise.
 	 */
 	public boolean isOpen();
 
 	/**
 	 * Is this logical connection instance "physically" connected.  Meaning
 	 * do we currently internally have a cached connection.
 	 *
 	 * @return True if physically connected; false otherwise.
 	 */
 	public boolean isPhysicallyConnected();
 
 	/**
 	 * Retrieves the connection currently "logically" managed by this LogicalConnectionImpl.
 	 * <p/>
 	 * Note, that we may need to obtain a connection to return here if a
 	 * connection has either not yet been obtained (non-UserSuppliedConnectionProvider)
 	 * or has previously been aggressively released.
 	 *
 	 * @todo ?? Move this to {@link LogicalConnectionImplementor} in lieu of {@link #getShareableConnectionProxy} and {@link #getDistinctConnectionProxy} ??
 	 *
 	 * @return The current Connection.
 	 */
 	public Connection getConnection();
 
 	/**
-	 * Retrieves the shareable connection proxy (see {@link org.hibernate.engine.jdbc.internal.proxy} for details).
+	 * Retrieves the shareable connection proxy.
 	 *
 	 * @return The shareable connection proxy.
 	 */
 	public Connection getShareableConnectionProxy();
 
 	/**
-	 * Retrieves a distinct connection proxy (see {@link org.hibernate.engine.jdbc.internal.proxy} for details).  It
-	 * is distinct in that it is not shared with others unless the caller explicitly shares it.
+	 * Retrieves a distinct connection proxy.  It is distinct in that it is not shared with others unless the caller
+	 * explicitly shares it.
 	 *
 	 * @return The distinct connection proxy.
 	 */
 	public Connection getDistinctConnectionProxy();
 
 	/**
 	 * Release the underlying connection and clean up any other resources associated
 	 * with this logical connection.
 	 * <p/>
 	 * This leaves the logical connection in a "no longer usable" state.
 	 *
 	 * @return The application-supplied connection, or {@code null} if Hibernate was managing connection.
 	 */
 	public Connection close();
 
 	/**
 	 * Signals the end of current transaction in which this logical connection operated.
 	 */
 	public void afterTransaction();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/EntityLoadContext.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/EntityLoadContext.java
index d7cc97d92c..e84abeabf5 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/EntityLoadContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/EntityLoadContext.java
@@ -1,62 +1,63 @@
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
 
 import java.sql.ResultSet;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.internal.CoreMessageLogger;
 
 /**
- * {@inheritDoc}
+ * Tracks information about loading of entities specific to a given result set.  These can be hierarchical.
  *
  * @author Steve Ebersole
  */
 public class EntityLoadContext {
-
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, EntityLoadContext.class.getName() );
 
 	private final LoadContexts loadContexts;
 	private final ResultSet resultSet;
 	private final List hydratingEntities = new ArrayList( 20 ); // todo : need map? the prob is a proper key, right?
 
 	public EntityLoadContext(LoadContexts loadContexts, ResultSet resultSet) {
 		this.loadContexts = loadContexts;
 		this.resultSet = resultSet;
 	}
 
 	void cleanup() {
-		if ( !hydratingEntities.isEmpty() ) LOG.hydratingEntitiesCount( hydratingEntities.size() );
+		if ( !hydratingEntities.isEmpty() ) {
+			LOG.hydratingEntitiesCount( hydratingEntities.size() );
+		}
 		hydratingEntities.clear();
 	}
 
 
 	@Override
 	public String toString() {
 		return super.toString() + "<rs=" + resultSet + ">";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java
index 5d7378555f..ba56e22870 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/loading/internal/LoadContexts.java
@@ -1,315 +1,314 @@
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
 import java.util.HashMap;
 import java.util.IdentityHashMap;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.collections.IdentityMap;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 
 /**
- * Maps {@link ResultSet result-sets} to specific contextual data
- * related to processing that {@link ResultSet result-sets}.
+ * Maps {@link ResultSet result-sets} to specific contextual data related to processing that result set
  * <p/>
- * Implementation note: internally an {@link IdentityMap} is used to maintain
- * the mappings; {@link IdentityMap} was chosen because I'd rather not be
- * dependent upon potentially bad {@link ResultSet#equals} and {ResultSet#hashCode}
- * implementations.
+ * Implementation note: internally an {@link IdentityMap} is used to maintain the mappings mainly because I'd
+ * rather not be dependent upon potentially bad {@link Object#equals} and {@link Object#hashCode} implementations on
+ * the JDBC result sets
  * <p/>
- * Considering the JDBC-redesign work, would further like this contextual info
- * not mapped seperately, but available based on the result set being processed.
- * This would also allow maintaining a single mapping as we could reliably get
- * notification of the result-set closing...
+ * Considering the JDBC-redesign work, would further like this contextual info not mapped separately, but available
+ * based on the result set being processed.  This would also allow maintaining a single mapping as we could reliably
+ * get notification of the result-set closing...
  *
  * @author Steve Ebersole
  */
 public class LoadContexts {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, LoadContexts.class.getName());
 
 	private final PersistenceContext persistenceContext;
 	private Map<ResultSet,CollectionLoadContext> collectionLoadContexts;
 	private Map<ResultSet,EntityLoadContext> entityLoadContexts;
 
 	private Map<CollectionKey,LoadingCollectionEntry> xrefLoadingCollectionEntries;
 
 	/**
 	 * Creates and binds this to the given persistence context.
 	 *
 	 * @param persistenceContext The persistence context to which this
 	 * will be bound.
 	 */
 	public LoadContexts(PersistenceContext persistenceContext) {
 		this.persistenceContext = persistenceContext;
 	}
 
 	/**
 	 * Retrieves the persistence context to which this is bound.
 	 *
 	 * @return The persistence context to which this is bound.
 	 */
 	public PersistenceContext getPersistenceContext() {
 		return persistenceContext;
 	}
 
 	private SessionImplementor getSession() {
 		return getPersistenceContext().getSession();
 	}
 
 
 	// cleanup code ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
  	/**
 	 * Release internal state associated with the given result set.
 	 * <p/>
 	 * This should be called when we are done with processing said result set,
 	 * ideally as the result set is being closed.
 	 *
 	 * @param resultSet The result set for which it is ok to release
 	 * associated resources.
 	 */
 	public void cleanup(ResultSet resultSet) {
 		if ( collectionLoadContexts != null ) {
 			CollectionLoadContext collectionLoadContext = collectionLoadContexts.remove( resultSet );
 			collectionLoadContext.cleanup();
 		}
 		if ( entityLoadContexts != null ) {
 			EntityLoadContext entityLoadContext = entityLoadContexts.remove( resultSet );
 			entityLoadContext.cleanup();
 		}
 	}
 
 	/**
 	 * Release internal state associated with *all* result sets.
 	 * <p/>
 	 * This is intended as a "failsafe" process to make sure we get everything
 	 * cleaned up and released.
 	 */
 	public void cleanup() {
 		if ( collectionLoadContexts != null ) {
 			for ( CollectionLoadContext collectionLoadContext : collectionLoadContexts.values() ) {
 				LOG.failSafeCollectionsCleanup( collectionLoadContext );
 				collectionLoadContext.cleanup();
 			}
 			collectionLoadContexts.clear();
 		}
 		if ( entityLoadContexts != null ) {
 			for ( EntityLoadContext entityLoadContext : entityLoadContexts.values() ) {
 				LOG.failSafeEntitiesCleanup( entityLoadContext );
 				entityLoadContext.cleanup();
 			}
 			entityLoadContexts.clear();
 		}
 	}
 
 
 	// Collection load contexts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Do we currently have any internal entries corresponding to loading
 	 * collections?
 	 *
 	 * @return True if we currently hold state pertaining to loading collections;
 	 * false otherwise.
 	 */
 	public boolean hasLoadingCollectionEntries() {
 		return ( collectionLoadContexts != null && !collectionLoadContexts.isEmpty() );
 	}
 
 	/**
 	 * Do we currently have any registered internal entries corresponding to loading
 	 * collections?
 	 *
 	 * @return True if we currently hold state pertaining to a registered loading collections;
 	 * false otherwise.
 	 */
 	public boolean hasRegisteredLoadingCollectionEntries() {
 		return ( xrefLoadingCollectionEntries != null && !xrefLoadingCollectionEntries.isEmpty() );
 	}
 
 
 	/**
 	 * Get the {@link CollectionLoadContext} associated with the given
 	 * {@link ResultSet}, creating one if needed.
 	 *
 	 * @param resultSet The result set for which to retrieve the context.
 	 * @return The processing context.
 	 */
 	public CollectionLoadContext getCollectionLoadContext(ResultSet resultSet) {
 		CollectionLoadContext context = null;
 		if ( collectionLoadContexts == null ) {
 			collectionLoadContexts = new IdentityHashMap<ResultSet, CollectionLoadContext>( 8 );
 		}
 		else {
 			context = collectionLoadContexts.get(resultSet);
 		}
 		if ( context == null ) {
 			LOG.tracev( "Constructing collection load context for result set [{0}]", resultSet );
 			context = new CollectionLoadContext( this, resultSet );
 			collectionLoadContexts.put( resultSet, context );
 		}
 		return context;
 	}
 
 	/**
 	 * Attempt to locate the loading collection given the owner's key.  The lookup here
 	 * occurs against all result-set contexts...
 	 *
 	 * @param persister The collection persister
 	 * @param ownerKey The owner key
 	 * @return The loading collection, or null if not found.
 	 */
 	public PersistentCollection locateLoadingCollection(CollectionPersister persister, Serializable ownerKey) {
 		LoadingCollectionEntry lce = locateLoadingCollectionEntry( new CollectionKey( persister, ownerKey ) );
 		if ( lce != null ) {
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracef(
 						"Returning loading collection: %s",
 						MessageHelper.collectionInfoString( persister, ownerKey, getSession().getFactory() )
 				);
 			}
 			return lce.getCollection();
 		}
 		// TODO : should really move this log statement to CollectionType, where this is used from...
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracef( "Creating collection wrapper: %s",
 					MessageHelper.collectionInfoString( persister, ownerKey, getSession().getFactory() ) );
 		}
 		return null;
 	}
 
 	// loading collection xrefs ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Register a loading collection xref.
 	 * <p/>
 	 * This xref map is used because sometimes a collection is in process of
 	 * being loaded from one result set, but needs to be accessed from the
 	 * context of another "nested" result set processing.
 	 * <p/>
 	 * Implementation note: package protected, as this is meant solely for use
 	 * by {@link CollectionLoadContext} to be able to locate collections
 	 * being loaded by other {@link CollectionLoadContext}s/{@link ResultSet}s.
 	 *
 	 * @param entryKey The xref collection key
 	 * @param entry The corresponding loading collection entry
 	 */
 	void registerLoadingCollectionXRef(CollectionKey entryKey, LoadingCollectionEntry entry) {
 		if ( xrefLoadingCollectionEntries == null ) {
 			xrefLoadingCollectionEntries = new HashMap<CollectionKey,LoadingCollectionEntry>();
 		}
 		xrefLoadingCollectionEntries.put( entryKey, entry );
 	}
 
 	/**
 	 * The inverse of {@link #registerLoadingCollectionXRef}.  Here, we are done
 	 * processing the said collection entry, so we remove it from the
 	 * load context.
 	 * <p/>
 	 * The idea here is that other loading collections can now reference said
 	 * collection directly from the {@link PersistenceContext} because it
 	 * has completed its load cycle.
 	 * <p/>
 	 * Implementation note: package protected, as this is meant solely for use
 	 * by {@link CollectionLoadContext} to be able to locate collections
 	 * being loaded by other {@link CollectionLoadContext}s/{@link ResultSet}s.
 	 *
 	 * @param key The key of the collection we are done processing.
 	 */
 	void unregisterLoadingCollectionXRef(CollectionKey key) {
 		if ( !hasRegisteredLoadingCollectionEntries() ) {
 			return;
 		}
 		xrefLoadingCollectionEntries.remove(key);
 	 }
 
-	/*package*/Map getLoadingCollectionXRefs() {
+	@SuppressWarnings( {"UnusedDeclaration"})
+	Map getLoadingCollectionXRefs() {
  		return xrefLoadingCollectionEntries;
  	}
 
 
 	/**
 	 * Locate the LoadingCollectionEntry within *any* of the tracked
 	 * {@link CollectionLoadContext}s.
 	 * <p/>
 	 * Implementation note: package protected, as this is meant solely for use
 	 * by {@link CollectionLoadContext} to be able to locate collections
 	 * being loaded by other {@link CollectionLoadContext}s/{@link ResultSet}s.
 	 *
 	 * @param key The collection key.
 	 * @return The located entry; or null.
 	 */
 	LoadingCollectionEntry locateLoadingCollectionEntry(CollectionKey key) {
 		if ( xrefLoadingCollectionEntries == null ) {
 			return null;
 		}
 		LOG.tracev( "Attempting to locate loading collection entry [{0}] in any result-set context", key );
 		LoadingCollectionEntry rtn = xrefLoadingCollectionEntries.get( key );
 		if ( rtn == null ) {
 			LOG.tracev( "Collection [{0}] not located in load context", key );
 		}
 		else {
 			LOG.tracev( "Collection [{0}] located in load context", key );
 		}
 		return rtn;
 	}
 
 	/*package*/void cleanupCollectionXRefs(Set<CollectionKey> entryKeys) {
 		for ( CollectionKey entryKey : entryKeys ) {
 			xrefLoadingCollectionEntries.remove( entryKey );
 		}
 	}
 
 
 	// Entity load contexts ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// 	* currently, not yet used...
 
+	@SuppressWarnings( {"UnusedDeclaration"})
 	public EntityLoadContext getEntityLoadContext(ResultSet resultSet) {
 		EntityLoadContext context = null;
 		if ( entityLoadContexts == null ) {
 			entityLoadContexts = new IdentityHashMap<ResultSet, EntityLoadContext>( 8 );
 		}
 		else {
 			context = entityLoadContexts.get( resultSet );
 		}
 		if ( context == null ) {
 			context = new EntityLoadContext( this, resultSet );
 			entityLoadContexts.put( resultSet, context );
 		}
 		return context;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/Mapping.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/Mapping.java
index 6bfbea16a7..f67bdbfc58 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/Mapping.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/Mapping.java
@@ -1,50 +1,52 @@
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
 
 import org.hibernate.MappingException;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.type.Type;
 
 /**
  * Defines operations common to "compiled" mappings (ie. <tt>SessionFactory</tt>)
  * and "uncompiled" mappings (ie. <tt>Configuration</tt>) that are used by
  * implementors of <tt>Type</tt>.
  *
  * @see org.hibernate.type.Type
  * @see org.hibernate.internal.SessionFactoryImpl
  * @see org.hibernate.cfg.Configuration
  * @author Gavin King
  */
 public interface Mapping {
 	/**
 	 * Allow access to the id generator factory, though this is only needed/allowed from configuration.
-	 * @return
+	 *
+	 * @return Access to the identifier generator factory
+	 *
 	 * @deprecated temporary solution 
 	 */
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory();
 	public Type getIdentifierType(String className) throws MappingException;
 	public String getIdentifierPropertyName(String className) throws MappingException;
 	public Type getReferencedPropertyType(String className, String propertyName) throws MappingException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java
index b8e6249df5..e0dc536472 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/PersistenceContext.java
@@ -1,603 +1,685 @@
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
- * Holds the state of the persistence context, including the 
- * first-level cache, entries, snapshots, proxies, etc.
+ * Represents the state of "stuff" Hibernate is tracking, including (not exhaustive):
+ * <ul>
+ *     <li>entities</li>
+ *     <li>collections</li>
+ *     <li>snapshots</li>
+ *     <li>proxies</li>
+ * </ul>
+ * <p/>
+ * Often referred to as the "first level cache".
  * 
  * @author Gavin King
+ * @author Steve Ebersole
  */
+@SuppressWarnings( {"JavaDoc"})
 public interface PersistenceContext {
 	
+	@SuppressWarnings( {"UnusedDeclaration"})
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
+	 *
+	 * @param key The collection key under which to add the collection
+	 * @param collection The collection to add
 	 */
 	public void addUnownedCollection(CollectionKey key, PersistentCollection collection);
 
 	/**
-	 * Get and remove a collection whose owner is not yet loaded,
-	 * when its owner is being loaded
+	 * Take ownership of a previously unowned collection, if one.  This method returns {@code null} if no such
+	 * collection was previous added () or was previously removed.
+	 * <p/>
+	 * This should indicate the owner is being loaded and we are ready to "link" them.
+	 *
+	 * @param key The collection key for which to locate a collection collection
+	 *
+	 * @return The unowned collection, or {@code null}
 	 */
 	public PersistentCollection useUnownedCollection(CollectionKey key);
 
 	/**
-	 * Get the <tt>BatchFetchQueue</tt>, instantiating one if
-	 * necessary.
+	 * Get the {@link BatchFetchQueue}, instantiating one if necessary.
+	 *
+	 * @return The batch fetch queue in effect for this persistence context
 	 */
 	public BatchFetchQueue getBatchFetchQueue();
 	
 	/**
 	 * Clear the state of the persistence context
 	 */
 	public void clear();
 
 	/**
 	 * @return false if we know for certain that all the entities are read-only
 	 */
+	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean hasNonReadOnlyEntities();
 
 	/**
 	 * Set the status of an entry
+	 *
+	 * @param entry The entry for which to set the status
+	 * @param status The new status
 	 */
 	public void setEntryStatus(EntityEntry entry, Status status);
 
 	/**
 	 * Called after transactions end
 	 */
 	public void afterTransactionCompletion();
 
 	/**
-	 * Get the current state of the entity as known to the underlying
-	 * database, or null if there is no corresponding row 
+	 * Get the current state of the entity as known to the underlying database, or null if there is no
+	 * corresponding row
+	 *
+	 * @param id The identifier of the entity for which to grab a snapshot
+	 * @param persister The persister of the entity.
+	 *
+	 * @return The entity's (non-cached) snapshot
+	 *
+	 * @see #getCachedDatabaseSnapshot
 	 */
-	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister)
-			throws HibernateException;
+	public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister);
 
+	/**
+	 * Get the current database state of the entity, using the cached state snapshot if one is available.
+	 *
+	 * @param key The entity key
+	 *
+	 * @return The entity's (non-cached) snapshot
+	 */
 	public Object[] getCachedDatabaseSnapshot(EntityKey key);
 
 	/**
-	 * Get the values of the natural id fields as known to the underlying 
-	 * database, or null if the entity has no natural id or there is no 
-	 * corresponding row.
+	 * Get the values of the natural id fields as known to the underlying database, or null if the entity has no
+	 * natural id or there is no corresponding row.
+	 *
+	 * @param id The identifier of the entity for which to grab a snapshot
+	 * @param persister The persister of the entity.
+	 *
+	 * @return The current (non-cached) snapshot of the entity's natural id state.
 	 */
-	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister)
-	throws HibernateException;
+	public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister);
 
 	/**
 	 * Add a canonical mapping from entity key to entity instance
+	 *
+	 * @param key The key under which to add an entity
+	 * @param entity The entity instance to add
 	 */
 	public void addEntity(EntityKey key, Object entity);
 
 	/**
-	 * Get the entity instance associated with the given 
-	 * <tt>EntityKey</tt>
+	 * Get the entity instance associated with the given key
+	 *
+	 * @param key The key under which to look for an entity
+	 *
+	 * @return The matching entity, or {@code null}
 	 */
 	public Object getEntity(EntityKey key);
 
 	/**
 	 * Is there an entity with the given key in the persistence context
+	 *
+	 * @param key The key under which to look for an entity
+	 *
+	 * @return {@code true} indicates an entity was found; otherwise {@code false}
 	 */
 	public boolean containsEntity(EntityKey key);
 
 	/**
-	 * Remove an entity from the session cache, also clear
-	 * up other state associated with the entity, all except
-	 * for the <tt>EntityEntry</tt>
+	 * Remove an entity.  Also clears up all other state associated with the entity aside from the {@link EntityEntry}
+	 *
+	 * @param key The key whose matching entity should be removed
+	 *
+	 * @return The matching entity
 	 */
 	public Object removeEntity(EntityKey key);
 
 	/**
-	 * Get an entity cached by unique key
+	 * Add an entity to the cache by unique key
+	 *
+	 * @param euk The unique (non-primary) key under which to add an entity
+	 * @param entity The entity instance
 	 */
-	public Object getEntity(EntityUniqueKey euk);
+	public void addEntity(EntityUniqueKey euk, Object entity);
 
 	/**
-	 * Add an entity to the cache by unique key
+	 * Get an entity cached by unique key
+	 *
+	 * @param euk The unique (non-primary) key under which to look for an entity
+	 *
+	 * @return The located entity
 	 */
-	public void addEntity(EntityUniqueKey euk, Object entity);
+	public Object getEntity(EntityUniqueKey euk);
 
 	/**
-	 * Retreive the EntityEntry representation of the given entity.
+	 * Retrieve the {@link EntityEntry} representation of the given entity.
 	 *
-	 * @param entity The entity for which to locate the EntityEntry.
-	 * @return The EntityEntry for the given entity.
+	 * @param entity The entity instance for which to locate the corresponding entry
+	 * @return The entry
 	 */
 	public EntityEntry getEntry(Object entity);
 
 	/**
 	 * Remove an entity entry from the session cache
+	 *
+	 * @param entity The entity instance for which to remove the corresponding entry
+	 * @return The matching entry
 	 */
 	public EntityEntry removeEntry(Object entity);
 
 	/**
-	 * Is there an EntityEntry for this instance?
+	 * Is there an {@link EntityEntry} registration for this entity instance?
+	 *
+	 * @param entity The entity instance for which to check for an entry
+	 *
+	 * @return {@code true} indicates a matching entry was found.
 	 */
 	public boolean isEntryFor(Object entity);
 
 	/**
 	 * Get the collection entry for a persistent collection
+	 *
+	 * @param coll The persistent collection instance for which to locate the collection entry
+	 *
+	 * @return The matching collection entry
 	 */
 	public CollectionEntry getCollectionEntry(PersistentCollection coll);
 
 	/**
 	 * Adds an entity to the internal caches.
 	 */
-	public EntityEntry addEntity(final Object entity, final Status status,
-			final Object[] loadedState, final EntityKey entityKey, final Object version,
-			final LockMode lockMode, final boolean existsInDatabase,
-			final EntityPersister persister, final boolean disableVersionIncrement, boolean lazyPropertiesAreUnfetched);
+	public EntityEntry addEntity(
+			final Object entity,
+			final Status status,
+			final Object[] loadedState,
+			final EntityKey entityKey,
+			final Object version,
+			final LockMode lockMode,
+			final boolean existsInDatabase,
+			final EntityPersister persister,
+			final boolean disableVersionIncrement,
+			boolean lazyPropertiesAreUnfetched);
 
 	/**
 	 * Generates an appropriate EntityEntry instance and adds it 
 	 * to the event source's internal caches.
 	 */
-	public EntityEntry addEntry(final Object entity, final Status status,
-			final Object[] loadedState, final Object rowId, final Serializable id,
-			final Object version, final LockMode lockMode, final boolean existsInDatabase,
-			final EntityPersister persister, final boolean disableVersionIncrement, boolean lazyPropertiesAreUnfetched);
+	public EntityEntry addEntry(
+			final Object entity,
+			final Status status,
+			final Object[] loadedState,
+			final Object rowId,
+			final Serializable id,
+			final Object version,
+			final LockMode lockMode,
+			final boolean existsInDatabase,
+			final EntityPersister persister,
+			final boolean disableVersionIncrement,
+			boolean lazyPropertiesAreUnfetched);
 
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
+	@SuppressWarnings( {"UnusedDeclaration"})
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
+	 * <p/>
+	 * To determine the default read-only/modifiable setting used for entities and proxies that are loaded into the
+	 * session use {@link org.hibernate.Session#isDefaultReadOnly}
 	 *
-	 * To get the default read-only/modifiable setting used for
-	 * entities and proxies that are loaded into the session:
-	 * @see org.hibernate.Session#isDefaultReadOnly()
+	 * @param entityOrProxy an entity or proxy
 	 *
-	 * @param entityOrProxy
-	 * @return true, the object is read-only; false, the object is modifiable.
+	 * @return {@code true} if the object is read-only; otherwise {@code false} to indicate that the object is
+	 * modifiable.
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
-	 * To set the default read-only/modifiable setting used for
-	 * entities and proxies that are loaded into this persistence context:
-	 * @see PersistenceContext#setDefaultReadOnly(boolean)
-	 * @see org.hibernate.Session#setDefaultReadOnly(boolean)
+	 * @param entityOrProxy an entity or proxy
+	 * @param readOnly if {@code true}, the entity or proxy is made read-only; otherwise, the entity or proxy is made
+	 * modifiable.
 	 *
-	 * To override this persistence context's read-only/modifiable setting
-	 * for entities and proxies loaded by a Query:
-	 * @see org.hibernate.Query#setReadOnly(boolean)
-	 *
-	 * @param entityOrProxy, an entity or HibernateProxy
-	 * @param readOnly, if true, the entity or proxy is made read-only;
-	 *                  if false, the entity or proxy is made modifiable.
-	 *
-	 * @see org.hibernate.Session#setReadOnly(Object, boolean)
+	 * @see org.hibernate.Session#setDefaultReadOnly
+	 * @see org.hibernate.Session#setReadOnly
+	 * @see org.hibernate.Query#setReadOnly
 	 */
 	public void setReadOnly(Object entityOrProxy, boolean readOnly);
 
 	void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId);
 
 	/**
-	 * Put child/parent relation to cache for cascading op
-	 * @param parent
-	 * @param child
+	 * Add a child/parent relation to cache for cascading op
+	 *
+	 * @param child The child of the relationship
+	 * @param parent The parent of the relationship
 	 */
-	public void addChildParent(Object parent, Object child);
+	public void addChildParent(Object child, Object parent);
 
 	/**
-	 * Remove child/parent relation from cache 
-	 * @param parent
+	 * Remove child/parent relation from cache
+	 *
+	 * @param child The child to be removed.
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java
index 4fca5922e0..a320e7eafb 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java
@@ -1,566 +1,575 @@
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
-
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, QueryParameters.class.getName());
 
 	private Type[] positionalParameterTypes;
 	private Object[] positionalParameterValues;
 	private Map<String,TypedValue> namedParameters;
 	private LockOptions lockOptions;
 	private RowSelection rowSelection;
 	private boolean cacheable;
 	private String cacheRegion;
 	private String comment;
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
 		this( positionalParameterTypes, positionalParameterValues, null, null, false, false, false, null, null, false, null );
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
 				collectionKeys,
 				transformer
 		);
 		this.optionalEntityName = optionalEntityName;
 		this.optionalId = optionalId;
 		this.optionalObject = optionalObject;
 	}
 
+	@SuppressWarnings( {"UnusedDeclaration"})
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
 
+	@SuppressWarnings( {"UnusedDeclaration"})
 	public void setNamedParameters(Map<String,TypedValue> map) {
 		namedParameters = map;
 	}
 
 	public void setPositionalParameterTypes(Type[] types) {
 		positionalParameterTypes = types;
 	}
 
 	public void setPositionalParameterValues(Object[] objects) {
 		positionalParameterValues = objects;
 	}
 
+	@SuppressWarnings( {"UnusedDeclaration"})
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
 
 	public ScrollMode getScrollMode() {
 		return scrollMode;
 	}
 
 	public void setScrollMode(ScrollMode scrollMode) {
 		this.scrollMode = scrollMode;
 	}
 
 	public Serializable[] getCollectionKeys() {
 		return collectionKeys;
 	}
 
+	@SuppressWarnings( {"UnusedDeclaration"})
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
-	 * Should entities and proxies loaded by the Query be put in read-only mode? If the
-	 * read-only/modifiable setting was not initialized
-	 * (i.e., QueryParameters#isReadOnlyInitialized() == false), then the default
-	 * read-only/modifiable setting for the persistence context is returned instead.
+	 * Should entities and proxies loaded by the Query be put in read-only mode?  If the
+	 * read-only/modifiable setting was not initialized (i.e., QueryParameters#isReadOnlyInitialized() == false),
+	 * then the default read-only/modifiable setting for the persistence context is returned instead.
+	 * <p/>
+	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
+	 * query that existed in the session before the query was executed.
+	 *
+	 * @param session The originating session
+	 *
+	 * @return {@code true} indicates that entities and proxies loaded by the query will be put in read-only mode;
+	 * {@code false} indicates that entities and proxies loaded by the query will be put in modifiable mode
 	 *
 	 * @see QueryParameters#isReadOnlyInitialized()
 	 * @see QueryParameters#setReadOnly(boolean)
 	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
 	 *
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * query that existed in the session before the query was executed.
 	 *
-	 * @return true, entities and proxies loaded by the query will be put in read-only mode
-	 *         false, entities and proxies loaded by the query will be put in modifiable mode
 	 */
 	public boolean isReadOnly(SessionImplementor session) {
-		return ( isReadOnlyInitialized ?
-				isReadOnly() :
-				session.getPersistenceContext().isDefaultReadOnly()
-		);
+		return isReadOnlyInitialized
+				? isReadOnly()
+				: session.getPersistenceContext().isDefaultReadOnly();
 	}
 
 	/**
 	 * Set the read-only/modifiable mode for entities and proxies loaded by the query.
-	 * 	 *
+	 * <p/>
+	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
+	 * query that existed in the session before the query was executed.
+	 *
+	 * @param readOnly if {@code true}, entities and proxies loaded by the query will be put in read-only mode; if
+	 * {@code false}, entities and proxies loaded by the query will be put in modifiable mode
+	 *
 	 * @see QueryParameters#isReadOnlyInitialized()
 	 * @see QueryParameters#isReadOnly(org.hibernate.engine.spi.SessionImplementor)
 	 * @see QueryParameters#setReadOnly(boolean)
 	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
-	 *
-	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
-	 * query that existed in the session before the query was executed.
-	 *
-	 * @return true, entities and proxies loaded by the query will be put in read-only mode
-	 *         false, entities and proxies loaded by the query will be put in modifiable mode
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
 
+	@SuppressWarnings( {"unchecked"})
 	public void processFilters(String sql, Map filters, SessionFactoryImplementor factory) {
-		if ( filters.size() == 0 || sql.indexOf( ParserHelper.HQL_VARIABLE_PREFIX ) < 0 ) {
+		if ( filters.size() == 0 || !sql.contains( ParserHelper.HQL_VARIABLE_PREFIX ) ) {
 			// HELLA IMPORTANT OPTIMIZATION!!!
 			processedPositionalParameterValues = getPositionalParameterValues();
 			processedPositionalParameterTypes = getPositionalParameterTypes();
 			processedSQL = sql;
 		}
 		else {
 			final Dialect dialect = factory.getDialect();
 			String symbols = new StringBuffer().append( ParserHelper.HQL_SEPARATORS )
 					.append( dialect.openQuote() )
 					.append( dialect.closeQuote() )
 					.toString();
 			StringTokenizer tokens = new StringTokenizer( sql, symbols, true );
-			StringBuffer result = new StringBuffer();
+			StringBuilder result = new StringBuilder();
 
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
 
+	@SuppressWarnings( {"UnusedDeclaration"})
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
index f4c1bdef0c..7528c4c2e6 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SessionFactoryImplementor.java
@@ -1,241 +1,241 @@
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
 	 * Get the persister object for a collection role.
 	 *
 	 * @param role The role (name) of the collection for which to retrieve the
 	 * persister.
 	 * @return The persister
 	 * @throws MappingException Indicates persister could not be found with that role.
 	 */
 	public CollectionPersister getCollectionPersister(String role) throws MappingException;
 
 	/**
 	 * Get the JdbcServices.
 	 * @return the JdbcServices
 	 */
 	public JdbcServices getJdbcServices();
 
 	/**
 	 * Get the SQL dialect.
 	 * <p/>
-	 * Shorthand for {@link #getJdbcServices().getDialect()}.{@link JdbcServices#getDialect()}
+	 * Shorthand for {@code getJdbcServices().getDialect()}
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
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName);
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
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/InvalidWithClauseException.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/InvalidWithClauseException.java
index bb407afa44..aa7f97e336 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/InvalidWithClauseException.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/InvalidWithClauseException.java
@@ -1,41 +1,40 @@
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
 package org.hibernate.hql.internal.ast;
 
-
 /**
- * {@inheritDoc}
+ * Indicates an issue with the encountered with-clause.
  *
  * @author Steve Ebersole
  */
 public class InvalidWithClauseException extends QuerySyntaxException {
 	public InvalidWithClauseException(String message) {
 		super( message );
 	}
 
 	public InvalidWithClauseException(String message, String queryString) {
 		super( message, queryString );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/SelectExpression.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/SelectExpression.java
index a8e1503154..263c7da196 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/SelectExpression.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/SelectExpression.java
@@ -1,98 +1,104 @@
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
 
 import org.hibernate.type.Type;
 
 /**
  * Represents an element of a projection list, i.e. a select expression.
  *
  * @author josh
  */
 public interface SelectExpression {
 	/**
 	 * Returns the data type of the select expression.
 	 *
 	 * @return The data type of the select expression.
 	 */
 	Type getDataType();
 
 	/**
 	 * Appends AST nodes that represent the columns after the current AST node.
 	 * (e.g. 'as col0_O_')
 	 *
 	 * @param i The index of the select expression in the projection list.
+	 *
+	 * @throws antlr.SemanticException if a semantic error occurs
 	 */
 	void setScalarColumnText(int i) throws SemanticException;
 
 	/**
 	 * Sets the index and text for select expression in the projection list.
 	 *  
 	 * @param i The index of the select expression in the projection list.
-	 * @throws SemanticException
+	 *
+	 * @throws SemanticException if a semantic error occurs
 	 */
 	void setScalarColumn(int i) throws SemanticException;
 
 	/**
 	 * Gets index of the select expression in the projection list.
 	 *
-	 * @returns The index of the select expression in the projection list.
+	 * @return The index of the select expression in the projection list.
 	 */
 	int getScalarColumnIndex();
 	
 	/**
 	 * Returns the FROM element that this expression refers to.
 	 *
 	 * @return The FROM element.
 	 */
 	FromElement getFromElement();
 
 	/**
 	 * Returns true if the element is a constructor (e.g. new Foo).
 	 *
 	 * @return true if the element is a constructor (e.g. new Foo).
 	 */
+	@SuppressWarnings( {"UnusedDeclaration"})
 	boolean isConstructor();
 
 	/**
 	 * Returns true if this select expression represents an entity that can be returned.
 	 *
 	 * @return true if this select expression represents an entity that can be returned.
+	 *
+	 * @throws SemanticException if a semantic error occurs
 	 */
 	boolean isReturnableEntity() throws SemanticException;
 
 	/**
 	 * Sets the text of the node.
 	 *
 	 * @param text the new node text.
 	 */
 	void setText(String text);
 
 	boolean isScalar() throws SemanticException;
 	
 	void setAlias(String alias);
 	String getAlias();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/LiteralProcessor.java b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/LiteralProcessor.java
index 1aee475634..25d743d211 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/LiteralProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/LiteralProcessor.java
@@ -1,326 +1,342 @@
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
 
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.text.DecimalFormat;
 
 import antlr.SemanticException;
 import antlr.collections.AST;
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.hql.internal.antlr.HqlSqlTokenTypes;
 import org.hibernate.hql.internal.antlr.SqlTokenTypes;
 import org.hibernate.hql.internal.ast.HqlSqlWalker;
 import org.hibernate.hql.internal.ast.InvalidPathException;
 import org.hibernate.hql.internal.ast.tree.DotNode;
 import org.hibernate.hql.internal.ast.tree.FromClause;
 import org.hibernate.hql.internal.ast.tree.IdentNode;
 import org.hibernate.hql.spi.QueryTranslator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.InFragment;
 import org.hibernate.type.LiteralType;
 import org.hibernate.type.Type;
 
 /**
  * A delegate that handles literals and constants for HqlSqlWalker, performing the token replacement functions and
  * classifying literals.
  *
  * @author josh
  */
 public class LiteralProcessor implements HqlSqlTokenTypes {
+	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, LiteralProcessor.class.getName());
+
 	/**
-	 * Indicates that Float and Double literal values should
-	 * be treated using the SQL "exact" format (i.e., '.001')
-	 */
-	public static final int EXACT = 0;
-	/**
-	 * Indicates that Float and Double literal values should
-	 * be treated using the SQL "approximate" format (i.e., '1E-3')
-	 */
-	public static final int APPROXIMATE = 1;
-	/**
-	 * In what format should Float and Double literal values be sent
-	 * to the database?
-	 * @see #EXACT, #APPROXIMATE
+	 * In what format should Float and Double literal values be sent to the database?
 	 */
-	public static int DECIMAL_LITERAL_FORMAT = EXACT;
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, LiteralProcessor.class.getName());
+	public static DecimalLiteralFormat DECIMAL_LITERAL_FORMAT = DecimalLiteralFormat.EXACT;
 
 	private HqlSqlWalker walker;
 
 	public LiteralProcessor(HqlSqlWalker hqlSqlWalker) {
 		this.walker = hqlSqlWalker;
 	}
 
 	public boolean isAlias(String alias) {
 		FromClause from = walker.getCurrentFromClause();
 		while ( from.isSubQuery() ) {
 			if ( from.containsClassAlias(alias) ) {
 				return true;
 			}
 			from = from.getParentFromClause();
 		}
 		return from.containsClassAlias(alias);
 	}
 
 	public void processConstant(AST constant, boolean resolveIdent) throws SemanticException {
 		// If the constant is an IDENT, figure out what it means...
 		boolean isIdent = ( constant.getType() == IDENT || constant.getType() == WEIRD_IDENT );
 		if ( resolveIdent && isIdent && isAlias( constant.getText() ) ) { // IDENT is a class alias in the FROM.
 			IdentNode ident = ( IdentNode ) constant;
 			// Resolve to an identity column.
 			ident.resolve(false, true);
 		}
 		else {	// IDENT might be the name of a class.
 			Queryable queryable = walker.getSessionFactoryHelper().findQueryableUsingImports( constant.getText() );
 			if ( isIdent && queryable != null ) {
 				constant.setText( queryable.getDiscriminatorSQLValue() );
 			}
 			// Otherwise, it's a literal.
 			else {
 				processLiteral( constant );
 			}
 		}
 	}
 
 	public void lookupConstant(DotNode node) throws SemanticException {
 		String text = ASTUtil.getPathText( node );
 		Queryable persister = walker.getSessionFactoryHelper().findQueryableUsingImports( text );
 		if ( persister != null ) {
 			// the name of an entity class
 			final String discrim = persister.getDiscriminatorSQLValue();
 			node.setDataType( persister.getDiscriminatorType() );
             if (InFragment.NULL.equals(discrim) || InFragment.NOT_NULL.equals(discrim)) throw new InvalidPathException(
                                                                                                                        "subclass test not allowed for null or not null discriminator: '"
                                                                                                                        + text + "'");
             setSQLValue(node, text, discrim); // the class discriminator value
 		}
 		else {
 			Object value = ReflectHelper.getConstantValue( text );
             if (value == null) throw new InvalidPathException("Invalid path: '" + text + "'");
             setConstantValue(node, text, value);
 		}
 	}
 
 	private void setSQLValue(DotNode node, String text, String value) {
 		LOG.debugf( "setSQLValue() %s -> %s", text, value );
 		node.setFirstChild( null );	// Chop off the rest of the tree.
 		node.setType( SqlTokenTypes.SQL_TOKEN );
 		node.setText(value);
 		node.setResolvedConstant( text );
 	}
 
 	private void setConstantValue(DotNode node, String text, Object value) {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "setConstantValue() %s -> %s %s", text, value, value.getClass().getName() );
 		}
 		node.setFirstChild( null );	// Chop off the rest of the tree.
 		if ( value instanceof String ) {
 			node.setType( SqlTokenTypes.QUOTED_STRING );
 		}
 		else if ( value instanceof Character ) {
 			node.setType( SqlTokenTypes.QUOTED_STRING );
 		}
 		else if ( value instanceof Byte ) {
 			node.setType( SqlTokenTypes.NUM_INT );
 		}
 		else if ( value instanceof Short ) {
 			node.setType( SqlTokenTypes.NUM_INT );
 		}
 		else if ( value instanceof Integer ) {
 			node.setType( SqlTokenTypes.NUM_INT );
 		}
 		else if ( value instanceof Long ) {
 			node.setType( SqlTokenTypes.NUM_LONG );
 		}
 		else if ( value instanceof Double ) {
 			node.setType( SqlTokenTypes.NUM_DOUBLE );
 		}
 		else if ( value instanceof Float ) {
 			node.setType( SqlTokenTypes.NUM_FLOAT );
 		}
 		else {
 			node.setType( SqlTokenTypes.CONSTANT );
 		}
 		Type type;
 		try {
 			type = walker.getSessionFactoryHelper().getFactory().getTypeResolver().heuristicType( value.getClass().getName() );
 		}
 		catch ( MappingException me ) {
 			throw new QueryException( me );
 		}
 		if ( type == null ) {
 			throw new QueryException( QueryTranslator.ERROR_CANNOT_DETERMINE_TYPE + node.getText() );
 		}
 		try {
 			LiteralType literalType = ( LiteralType ) type;
 			Dialect dialect = walker.getSessionFactoryHelper().getFactory().getDialect();
+			//noinspection unchecked
 			node.setText( literalType.objectToSQLString( value, dialect ) );
 		}
 		catch ( Exception e ) {
 			throw new QueryException( QueryTranslator.ERROR_CANNOT_FORMAT_LITERAL + node.getText(), e );
 		}
 		node.setDataType( type );
 		node.setResolvedConstant( text );
 	}
 
 	public void processBoolean(AST constant) {
 		// TODO: something much better - look at the type of the other expression!
 		// TODO: Have comparisonExpression and/or arithmeticExpression rules complete the resolution of boolean nodes.
 		String replacement = ( String ) walker.getTokenReplacements().get( constant.getText() );
 		if ( replacement != null ) {
 			constant.setText( replacement );
 		}
 		else {
 			boolean bool = "true".equals( constant.getText().toLowerCase() );
 			Dialect dialect = walker.getSessionFactoryHelper().getFactory().getDialect();
 			constant.setText( dialect.toBooleanValueString(bool) );
 		}
 	}
 
 	private void processLiteral(AST constant) {
 		String replacement = ( String ) walker.getTokenReplacements().get( constant.getText() );
 		if ( replacement != null ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf("processConstant() : Replacing '%s' with '%s'", constant.getText(), replacement);
 			}
 			constant.setText( replacement );
 		}
 	}
 
 	public void processNumeric(AST literal) {
 		if ( literal.getType() == NUM_INT
 				|| literal.getType() == NUM_LONG
 				|| literal.getType() == NUM_BIG_INTEGER ) {
 			literal.setText( determineIntegerRepresentation( literal.getText(), literal.getType() ) );
         } else if (literal.getType() == NUM_FLOAT
 				|| literal.getType() == NUM_DOUBLE
 				|| literal.getType() == NUM_BIG_DECIMAL ) {
 			literal.setText( determineDecimalRepresentation( literal.getText(), literal.getType() ) );
         } else LOG.unexpectedLiteralTokenType(literal.getType());
 	}
 
 	private String determineIntegerRepresentation(String text, int type) {
 		try {
 			if ( type == NUM_BIG_INTEGER ) {
 				String literalValue = text;
 				if ( literalValue.endsWith( "bi" ) || literalValue.endsWith( "BI" ) ) {
 					literalValue = literalValue.substring( 0, literalValue.length() - 2 );
 				}
 				return new BigInteger( literalValue ).toString();
 			}
 			if ( type == NUM_INT ) {
 				try {
 					return Integer.valueOf( text ).toString();
 				}
 				catch( NumberFormatException e ) {
 					LOG.tracev(
 							"Could not format incoming text [{0}] as a NUM_INT; assuming numeric overflow and attempting as NUM_LONG",
 							text );
 				}
 			}
 			String literalValue = text;
 			if ( literalValue.endsWith( "l" ) || literalValue.endsWith( "L" ) ) {
 				literalValue = literalValue.substring( 0, literalValue.length() - 1 );
 			}
 			return Long.valueOf( literalValue ).toString();
 		}
 		catch( Throwable t ) {
 			throw new HibernateException( "Could not parse literal [" + text + "] as integer", t );
 		}
 	}
 
 	public String determineDecimalRepresentation(String text, int type) {
 		String literalValue = text;
 		if ( type == NUM_FLOAT ) {
 			if ( literalValue.endsWith( "f" ) || literalValue.endsWith( "F" ) ) {
 				literalValue = literalValue.substring( 0, literalValue.length() - 1 );
 			}
 		}
 		else if ( type == NUM_DOUBLE ) {
 			if ( literalValue.endsWith( "d" ) || literalValue.endsWith( "D" ) ) {
 				literalValue = literalValue.substring( 0, literalValue.length() - 1 );
 			}
 		}
 		else if ( type == NUM_BIG_DECIMAL ) {
 			if ( literalValue.endsWith( "bd" ) || literalValue.endsWith( "BD" ) ) {
 				literalValue = literalValue.substring( 0, literalValue.length() - 2 );
 			}
 		}
 
-		BigDecimal number = null;
+		final BigDecimal number;
 		try {
 			number = new BigDecimal( literalValue );
 		}
 		catch( Throwable t ) {
 			throw new HibernateException( "Could not parse literal [" + text + "] as big-decimal", t );
 		}
 
-		return formatters[ DECIMAL_LITERAL_FORMAT ].format( number );
+		return DECIMAL_LITERAL_FORMAT.getFormatter().format( number );
 	}
 
 
 	private static interface DecimalFormatter {
 		String format(BigDecimal number);
 	}
 
 	private static class ExactDecimalFormatter implements DecimalFormatter {
+		public static final ExactDecimalFormatter INSTANCE = new ExactDecimalFormatter();
+
 		public String format(BigDecimal number) {
 			return number.toString();
 		}
 	}
 
 	private static class ApproximateDecimalFormatter implements DecimalFormatter {
+		public static final ApproximateDecimalFormatter INSTANCE = new ApproximateDecimalFormatter();
+
 		private static final String FORMAT_STRING = "#0.0E0";
+
 		public String format(BigDecimal number) {
 			try {
 				// TODO : what amount of significant digits need to be supported here?
 				//      - from the DecimalFormat docs:
 				//          [significant digits] = [minimum integer digits] + [maximum fraction digits]
 				DecimalFormat jdkFormatter = new DecimalFormat( FORMAT_STRING );
 				jdkFormatter.setMinimumIntegerDigits( 1 );
 				jdkFormatter.setMaximumFractionDigits( Integer.MAX_VALUE );
 				return jdkFormatter.format( number );
 			}
 			catch( Throwable t ) {
 				throw new HibernateException( "Unable to format decimal literal in approximate format [" + number.toString() + "]", t );
 			}
 		}
 	}
 
-	private static final DecimalFormatter[] formatters = new DecimalFormatter[] {
-			new ExactDecimalFormatter(),
-			new ApproximateDecimalFormatter()
-	};
+	public static enum DecimalLiteralFormat {
+		/**
+		 * Indicates that Float and Double literal values should
+		 * be treated using the SQL "exact" format (i.e., '.001')
+		 */
+		EXACT {
+			@Override
+			public DecimalFormatter getFormatter() {
+				return ExactDecimalFormatter.INSTANCE;
+			}
+		},
+		/**
+		 * Indicates that Float and Double literal values should
+		 * be treated using the SQL "approximate" format (i.e., '1E-3')
+		 */
+		@SuppressWarnings( {"UnusedDeclaration"})
+		APPROXIMATE {
+			@Override
+			public DecimalFormatter getFormatter() {
+				return ApproximateDecimalFormatter.INSTANCE;
+			}
+		};
+
+		public abstract DecimalFormatter getFormatter();
+	}
+
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/integrator/spi/Integrator.java b/hibernate-core/src/main/java/org/hibernate/integrator/spi/Integrator.java
index 5f6d6d3e7c..8b748c78f4 100644
--- a/hibernate-core/src/main/java/org/hibernate/integrator/spi/Integrator.java
+++ b/hibernate-core/src/main/java/org/hibernate/integrator/spi/Integrator.java
@@ -1,79 +1,81 @@
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
 package org.hibernate.integrator.spi;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 /**
  * Contract for stuff that integrates with Hibernate.
  * <p/>
  * IMPL NOTE: called during session factory initialization (constructor), so not all parts of the passed session factory
  * will be available.
- *
- * @todo : the signature here *will* change, guaranteed
- *
- * @todo : better name ?
+ * <p/>
+ * For more information, see the following jiras:<ul>
+ *     <li><a href="https://hibernate.onjira.com/browse/HHH-5562">HHH-5562</a></li>
+ *     <li><a href="https://hibernate.onjira.com/browse/HHH-6081">HHH-6081</a></li>
+ * </ul>
  *
  * @author Steve Ebersole
  * @since 4.0
- * @jira HHH-5562
- * @jira HHH-6081
+ *
+ * @todo : the signature here *will* change, guaranteed
+ * @todo : better name ?
  */
 public interface Integrator {
 
 	/**
 	 * Perform integration.
 	 *
 	 * @param configuration The configuration used to create the session factory
 	 * @param sessionFactory The session factory being created
 	 * @param serviceRegistry The session factory's service registry
 	 */
 	public void integrate(
 			Configuration configuration,
 			SessionFactoryImplementor sessionFactory,
 			SessionFactoryServiceRegistry serviceRegistry);
 
 	/**
      * Perform integration.
      *
      * @param metadata The metadata used to create the session factory
      * @param sessionFactory The session factory being created
      * @param serviceRegistry The session factory's service registry
      */
     public void integrate( MetadataImplementor metadata,
                            SessionFactoryImplementor sessionFactory,
                            SessionFactoryServiceRegistry serviceRegistry );
 
 	/**
 	 * Tongue-in-cheek name for a shutdown callback.
 	 *
 	 * @param sessionFactory The session factory being closed.
 	 * @param serviceRegistry That session factory's service registry
 	 */
 	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
index 82998da27a..bb746a698c 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
@@ -1,715 +1,764 @@
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
-import java.util.Set;
 
 import org.jboss.logging.Logger;
 
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
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionCoordinator;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.engine.transaction.spi.TransactionImplementor;
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
 
+	@Override
 	public Serializable insert(Object entity) {
 		errorIfClosed();
 		return insert(null, entity);
 	}
 
+	@Override
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
 
+	@Override
 	public void delete(Object entity) {
 		errorIfClosed();
 		delete(null, entity);
 	}
 
+	@Override
 	public void delete(String entityName, Object entity) {
 		errorIfClosed();
 		EntityPersister persister = getEntityPersister(entityName, entity);
 		Serializable id = persister.getIdentifier( entity, this );
 		Object version = persister.getVersion( entity );
 		persister.delete(id, version, entity, this);
 	}
 
 
 	// updates ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
+	@Override
 	public void update(Object entity) {
 		errorIfClosed();
 		update(null, entity);
 	}
 
+	@Override
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
 
+	@Override
 	public Object get(Class entityClass, Serializable id) {
 		return get( entityClass.getName(), id );
 	}
 
+	@Override
 	public Object get(Class entityClass, Serializable id, LockMode lockMode) {
 		return get( entityClass.getName(), id, lockMode );
 	}
 
+	@Override
 	public Object get(String entityName, Serializable id) {
 		return get(entityName, id, LockMode.NONE);
 	}
 
+	@Override
 	public Object get(String entityName, Serializable id, LockMode lockMode) {
 		errorIfClosed();
 		Object result = getFactory().getEntityPersister(entityName)
 				.load(id, null, lockMode, this);
 		if ( temporaryPersistenceContext.isLoadFinished() ) {
 			temporaryPersistenceContext.clear();
 		}
 		return result;
 	}
 
+	@Override
 	public void refresh(Object entity) {
 		refresh( bestGuessEntityName( entity ), entity, LockMode.NONE );
 	}
 
+	@Override
 	public void refresh(String entityName, Object entity) {
 		refresh( entityName, entity, LockMode.NONE );
 	}
 
+	@Override
 	public void refresh(Object entity, LockMode lockMode) {
 		refresh( bestGuessEntityName( entity ), entity, lockMode );
 	}
 
+	@Override
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
 
+	@Override
 	public Object immediateLoad(String entityName, Serializable id)
 			throws HibernateException {
 		throw new SessionException("proxies cannot be fetched by a stateless session");
 	}
 
+	@Override
 	public void initializeCollection(
 			PersistentCollection collection,
 	        boolean writing) throws HibernateException {
 		throw new SessionException("collections cannot be fetched by a stateless session");
 	}
 
+	@Override
 	public Object instantiate(
 			String entityName,
 	        Serializable id) throws HibernateException {
 		errorIfClosed();
-		return getFactory().getEntityPersister( entityName )
-				.instantiate( id, this );
+		return getFactory().getEntityPersister( entityName ).instantiate( id, this );
 	}
 
+	@Override
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
 
+	@Override
 	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
+	@Override
 	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
+	@Override
 	public List listFilter(Object collection, String filter, QueryParameters queryParameters)
 	throws HibernateException {
 		throw new UnsupportedOperationException();
 	}
 
-
+	@Override
 	public boolean isOpen() {
 		return !isClosed();
 	}
 
+	@Override
 	public void close() {
 		managedClose();
 	}
 
+	@Override
 	public ConnectionReleaseMode getConnectionReleaseMode() {
 		return factory.getSettings().getConnectionReleaseMode();
 	}
 
 	@Override
 	public boolean shouldAutoJoinTransaction() {
 		return true;
 	}
 
+	@Override
 	public boolean isAutoCloseSessionEnabled() {
 		return factory.getSettings().isAutoCloseSessionEnabled();
 	}
 
+	@Override
 	public boolean isFlushBeforeCompletionEnabled() {
 		return true;
 	}
 
+	@Override
 	public boolean isFlushModeNever() {
 		return false;
 	}
 
+	@Override
 	public void managedClose() {
 		if ( isClosed() ) {
 			throw new SessionException( "Session was already closed!" );
 		}
 		transactionCoordinator.close();
 		setClosed();
 	}
 
+	@Override
 	public void managedFlush() {
 		errorIfClosed();
 		getTransactionCoordinator().getJdbcCoordinator().executeBatch();
 	}
 
+	@Override
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
 
+	@Override
 	public String bestGuessEntityName(Object object) {
 		if (object instanceof HibernateProxy) {
 			object = ( (HibernateProxy) object ).getHibernateLazyInitializer().getImplementation();
 		}
 		return guessEntityName(object);
 	}
 
+	@Override
 	public Connection connection() {
 		errorIfClosed();
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().getDistinctConnectionProxy();
 	}
 
+	@Override
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
 
+	@Override
 	public CacheMode getCacheMode() {
 		return CacheMode.IGNORE;
 	}
 
+	@Override
 	public int getDontFlushFromFind() {
 		return 0;
 	}
 
+	@Override
 	public Map getEnabledFilters() {
 		return CollectionHelper.EMPTY_MAP;
 	}
 
+	@Override
 	public Serializable getContextEntityIdentifier(Object object) {
 		errorIfClosed();
 		return null;
 	}
 
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
+	@Override
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
 
+	@Override
 	public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
 		errorIfClosed();
 		return null;
 	}
 
+	@Override
 	public Type getFilterParameterType(String filterParameterName) {
 		throw new UnsupportedOperationException();
 	}
 
+	@Override
 	public Object getFilterParameterValue(String filterParameterName) {
 		throw new UnsupportedOperationException();
 	}
 
+	@Override
 	public FlushMode getFlushMode() {
 		return FlushMode.COMMIT;
 	}
 
+	@Override
 	public Interceptor getInterceptor() {
 		return EmptyInterceptor.INSTANCE;
 	}
 
+	@Override
 	public PersistenceContext getPersistenceContext() {
 		return temporaryPersistenceContext;
 	}
 
+	@Override
 	public long getTimestamp() {
 		throw new UnsupportedOperationException();
 	}
 
+	@Override
 	public String guessEntityName(Object entity) throws HibernateException {
 		errorIfClosed();
 		return entity.getClass().getName();
 	}
 
-
+	@Override
 	public boolean isConnected() {
 		return transactionCoordinator.getJdbcCoordinator().getLogicalConnection().isPhysicallyConnected();
 	}
 
+	@Override
 	public boolean isTransactionInProgress() {
 		return transactionCoordinator.isTransactionInProgress();
 	}
 
+	@Override
 	public void setAutoClear(boolean enabled) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public void disableTransactionAutoJoin() {
 		throw new UnsupportedOperationException();
 	}
 
+	@Override
 	public void setCacheMode(CacheMode cm) {
 		throw new UnsupportedOperationException();
 	}
 
+	@Override
 	public void setFlushMode(FlushMode fm) {
 		throw new UnsupportedOperationException();
 	}
 
+	@Override
 	public Transaction getTransaction() throws HibernateException {
 		errorIfClosed();
 		return transactionCoordinator.getTransaction();
 	}
 
+	@Override
 	public Transaction beginTransaction() throws HibernateException {
 		errorIfClosed();
 		Transaction result = getTransaction();
 		result.begin();
 		return result;
 	}
 
+	@Override
 	public boolean isEventSource() {
 		return false;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	public boolean isDefaultReadOnly() {
 		return false;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	public void setDefaultReadOnly(boolean readOnly) throws HibernateException {
-		if ( readOnly == true ) {
+		if ( readOnly ) {
 			throw new UnsupportedOperationException();
 		}
 	}
 
 /////////////////////////////////////////////////////////////////////////////////////////////////////
 
 	//TODO: COPY/PASTE FROM SessionImpl, pull up!
 
+	@Override
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
-			transactionCoordinator.afterNonTransactionalQuery( success );;
+			transactionCoordinator.afterNonTransactionalQuery( success );
 		}
 	}
 
+	@Override
 	public Criteria createCriteria(Class persistentClass, String alias) {
 		errorIfClosed();
 		return new CriteriaImpl( persistentClass.getName(), alias, this );
 	}
 
+	@Override
 	public Criteria createCriteria(String entityName, String alias) {
 		errorIfClosed();
 		return new CriteriaImpl(entityName, alias, this);
 	}
 
+	@Override
 	public Criteria createCriteria(Class persistentClass) {
 		errorIfClosed();
 		return new CriteriaImpl( persistentClass.getName(), this );
 	}
 
+	@Override
 	public Criteria createCriteria(String entityName) {
 		errorIfClosed();
 		return new CriteriaImpl(entityName, this);
 	}
 
+	@Override
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
 
+	@Override
+	@SuppressWarnings( {"unchecked"})
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
 
+	@Override
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
 
+	@Override
 	public ScrollableResults scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 	throws HibernateException {
 		errorIfClosed();
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 		return loader.scroll( queryParameters, this );
 	}
 
+	@Override
 	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {
 		errorIfClosed();
 		HQLQueryPlan plan = getHQLQueryPlan( query, false );
 		return plan.performScroll( queryParameters, this );
 	}
 
+	@Override
 	public void afterScrollOperation() {
 		temporaryPersistenceContext.clear();
 	}
 
-	public void flush() {}
+	@Override
+	public void flush() {
+	}
 
+	@Override
 	public NonFlushedChanges getNonFlushedChanges() {
 		throw new UnsupportedOperationException();
 	}
 
+	@Override
 	public void applyNonFlushedChanges(NonFlushedChanges nonFlushedChanges) {
 		throw new UnsupportedOperationException();
 	}
 
+	@Override
 	public String getFetchProfile() {
 		return null;
 	}
 
+	@Override
 	public LoadQueryInfluencers getLoadQueryInfluencers() {
 		return LoadQueryInfluencers.NONE;
 	}
 
-	public void registerInsertedKey(EntityPersister persister, Serializable id) {
-		errorIfClosed();
-		// nothing to do
-	}
-
-	public boolean wasInsertedDuringTransaction(EntityPersister persister, Serializable id) {
-		errorIfClosed();
-		// not in any meaning we need to worry about here.
-		return false;
-	}
-
-	public void setFetchProfile(String name) {}
-
-	protected boolean autoFlushIfRequired(Set querySpaces) throws HibernateException {
-		// no auto-flushing to support in stateless session
-		return false;
+	@Override
+	public void setFetchProfile(String name) {
 	}
 
+	@Override
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/package.html b/hibernate-core/src/main/java/org/hibernate/internal/package.html
old mode 100755
new mode 100644
index e104e88efe..b1e911abde
--- a/hibernate-core/src/main/java/org/hibernate/internal/package.html
+++ b/hibernate-core/src/main/java/org/hibernate/internal/package.html
@@ -1,33 +1,32 @@
 <!--
   ~ Hibernate, Relational Persistence for Idiomatic Java
   ~
-  ~ Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
+  ~ Copyright (c) 2011, Red Hat Inc. or third-party contributors as
   ~ indicated by the @author tags or express copyright attribution
   ~ statements applied by the authors.  All third-party contributions are
   ~ distributed under license by Red Hat Inc.
   ~
   ~ This copyrighted material is made available to anyone wishing to use, modify,
   ~ copy, or redistribute it subject to the terms and conditions of the GNU
   ~ Lesser General Public License, as published by the Free Software Foundation.
   ~
   ~ This program is distributed in the hope that it will be useful,
   ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
   ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
   ~ for more details.
   ~
   ~ You should have received a copy of the GNU Lesser General Public License
   ~ along with this distribution; if not, write to:
   ~ Free Software Foundation, Inc.
   ~ 51 Franklin Street, Fifth Floor
   ~ Boston, MA  02110-1301  USA
   -->
 
 <html>
 <head></head>
 <body>
 <p>
-    An internal package containing mostly implementations of central Hibernate APIs of the
-    {@link org.hibernate} package.
+    An internal package containing mostly implementations of central Hibernate APIs.
 </p>
 </body>
 </html>
diff --git a/hibernate-core/src/main/java/org/hibernate/jdbc/package.html b/hibernate-core/src/main/java/org/hibernate/jdbc/package.html
index e8e3d0d377..d683eb8bfe 100755
--- a/hibernate-core/src/main/java/org/hibernate/jdbc/package.html
+++ b/hibernate-core/src/main/java/org/hibernate/jdbc/package.html
@@ -1,33 +1,32 @@
 <!--
   ~ Hibernate, Relational Persistence for Idiomatic Java
   ~
-  ~ Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
+  ~ Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
   ~ indicated by the @author tags or express copyright attribution
   ~ statements applied by the authors.  All third-party contributions are
-  ~ distributed under license by Red Hat Middleware LLC.
+  ~ distributed under license by Red Hat Inc.
   ~
   ~ This copyrighted material is made available to anyone wishing to use, modify,
   ~ copy, or redistribute it subject to the terms and conditions of the GNU
   ~ Lesser General Public License, as published by the Free Software Foundation.
   ~
   ~ This program is distributed in the hope that it will be useful,
   ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
   ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
   ~ for more details.
   ~
   ~ You should have received a copy of the GNU Lesser General Public License
   ~ along with this distribution; if not, write to:
   ~ Free Software Foundation, Inc.
   ~ 51 Franklin Street, Fifth Floor
   ~ Boston, MA  02110-1301  USA
-  ~
   -->
 
 <html>
-<head></head>
 <body>
 <p>
-    Essentially defines {@link Work}, {@link ReturningWork} and {@link Expectation} as well as some exceptions
+    Essentially defines {@link org.hibernate.jdbc.Work}, {@link org.hibernate.jdbc.ReturningWork} and
+    {@link org.hibernate.jdbc.Expectation} as well as some exceptions
 </p>
 </body>
 </html>
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/entity/UniqueEntityLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/UniqueEntityLoader.java
index ab7b8260bc..4e70150733 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/UniqueEntityLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/UniqueEntityLoader.java
@@ -1,62 +1,62 @@
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
 package org.hibernate.loader.entity;
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * Loads entities for a <tt>EntityPersister</tt>
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface UniqueEntityLoader {
 	/**
 	 * Load an entity instance. If <tt>optionalObject</tt> is supplied,
 	 * load the entity state into the given (uninitialized) object.
 	 *
 	 * @deprecated use {@link #load(java.io.Serializable, Object, SessionImplementor, LockOptions)} instead.
-	 * @noinspection JavaDoc
 	 */
+	@SuppressWarnings( {"JavaDoc"})
 	public Object load(Serializable id, Object optionalObject, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Load an entity instance by id.  If <tt>optionalObject</tt> is supplied (non-<tt>null</tt>,
 	 * the entity state is loaded into that object instance instead of instantiating a new one.
 	 *
 	 * @param id The id to be loaded
 	 * @param optionalObject The (optional) entity instance in to which to load the state
 	 * @param session The session from which the request originated
 	 * @param lockOptions The lock options.
 	 *
 	 * @return The loaded entity
 	 *
 	 * @throws HibernateException indicates problem performing the load.
 	 */
 	public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metadata/ClassMetadata.java b/hibernate-core/src/main/java/org/hibernate/metadata/ClassMetadata.java
index c4560c588b..c27c095f8f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metadata/ClassMetadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/metadata/ClassMetadata.java
@@ -1,224 +1,227 @@
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
 package org.hibernate.metadata;
 
 import java.io.Serializable;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.type.Type;
 
 /**
  * Exposes entity class metadata to the application
  *
  * @see org.hibernate.SessionFactory#getClassMetadata(Class)
  * @author Gavin King
  */
+@SuppressWarnings( {"JavaDoc"})
 public interface ClassMetadata {
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     // stuff that is persister-centric and/or EntityInfo-centric ~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The name of the entity
 	 */
 	public String getEntityName();
 
 	/**
 	 * Get the name of the identifier property (or return null)
 	 */
 	public String getIdentifierPropertyName();
 
 	/**
 	 * Get the names of the class' persistent properties
 	 */
 	public String[] getPropertyNames();
 
 	/**
 	 * Get the identifier Hibernate type
 	 */
 	public Type getIdentifierType();
 
 	/**
 	 * Get the Hibernate types of the class properties
 	 */
 	public Type[] getPropertyTypes();
 
 	/**
 	 * Get the type of a particular (named) property
 	 */
 	public Type getPropertyType(String propertyName) throws HibernateException;
 
 	/**
 	 * Does this class support dynamic proxies?
 	 */
 	public boolean hasProxy();
 
 	/**
 	 * Are instances of this class mutable?
 	 */
 	public boolean isMutable();
 
 	/**
 	 * Are instances of this class versioned by a timestamp or version number column?
 	 */
 	public boolean isVersioned();
 
 	/**
 	 * Get the index of the version property
 	 */
 	public int getVersionProperty();
 
 	/**
 	 * Get the nullability of the class' persistent properties
 	 */
 	public boolean[] getPropertyNullability();
 
 
 	/**
 	 * Get the "laziness" of the properties of this class
 	 */
 	public boolean[] getPropertyLaziness();
 
 	/**
 	 * Does this class have an identifier property?
 	 */
 	public boolean hasIdentifierProperty();
 
 	/**
 	 * Does this entity declare a natural id?
 	 */
 	public boolean hasNaturalIdentifier();
 
 	/**
 	 * Which properties hold the natural id?
 	 */
 	public int[] getNaturalIdentifierProperties();
 	
 	/**
 	 * Does this entity have mapped subclasses?
 	 */
 	public boolean hasSubclasses();
 	
 	/**
 	 * Does this entity extend a mapped superclass?
 	 */
 	public boolean isInherited();
 	
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff that is tuplizer-centric, but is passed a session ~~~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Return the values of the mapped properties of the object
 	 */
-	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session) 
+	@SuppressWarnings( {"UnusedDeclaration"})
+	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
 	throws HibernateException;
 
 
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// stuff that is Tuplizer-centric ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The persistent class, or null
 	 */
 	public Class getMappedClass();
 
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
 	 * Get the value of a particular (named) property
 	 */
 	public Object getPropertyValue(Object object, String propertyName) throws HibernateException;
 
 	/**
 	 * Extract the property values from the given entity.
 	 *
 	 * @param entity The entity from which to extract the property values.
 	 * @return The property values.
 	 * @throws HibernateException
 	 */
 	public Object[] getPropertyValues(Object entity) throws HibernateException;
 
 	/**
 	 * Set the value of a particular (named) property
 	 */
 	public void setPropertyValue(Object object, String propertyName, Object value) throws HibernateException;
 
 	/**
 	 * Set the given values to the mapped properties of the given object
 	 */
 	public void setPropertyValues(Object object, Object[] values) throws HibernateException;
 
 	/**
 	 * Get the identifier of an instance (throw an exception if no identifier property)
 	 *
 	 * @deprecated Use {@link #getIdentifier(Object,SessionImplementor)} instead
-	 * @noinspection JavaDoc
 	 */
+	@SuppressWarnings( {"JavaDoc"})
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
 	 * Does the class implement the <tt>Lifecycle</tt> interface?
 	 */
+	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean implementsLifecycle();
 
 	/**
 	 * Get the version number (or timestamp) from the object's version property
 	 * (or return null if not versioned)
 	 */
 	public Object getVersion(Object object) throws HibernateException;
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AssociationAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AssociationAttributeBinding.java
index 7480a42dc1..3224be343e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AssociationAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AssociationAttributeBinding.java
@@ -1,66 +1,66 @@
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
 package org.hibernate.metamodel.binding;
 
 import org.hibernate.FetchMode;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.CascadeStyle;
 
 /**
  * Contract describing a binding for attributes which model associations.
  *
  * @author Steve Ebersole
  */
 public interface AssociationAttributeBinding extends AttributeBinding {
 	/**
 	 * Obtain the cascade style in effect for this association.
 	 *
 	 * @return The (potentially aggregated) cascade style.
 	 */
 	public CascadeStyle getCascadeStyle();
 
 	/**
 	 * Set the cascade styles in effect for this association.
 	 *
 	 * @param cascadeStyles The cascade styles.
 	 */
 	public void setCascadeStyles(Iterable<CascadeStyle> cascadeStyles);
 
 	public FetchTiming getFetchTiming();
 	public void setFetchTiming(FetchTiming fetchTiming);
 
 	public FetchStyle getFetchStyle();
 	public void setFetchStyle(FetchStyle fetchStyle);
 
 
 	/**
-	 * Temporary.  Needed for integration with legacy {@link org.hibernate.mapping} configuration of persisters.
+	 * Temporary.  Needed for integration with legacy org.hibernate.mapping configuration of persisters.
 	 *
 	 * @deprecated
 	 */
 	@Deprecated
 	@SuppressWarnings( {"JavaDoc"})
 	public FetchMode getFetchMode();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAssociationAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAssociationAttributeBinding.java
index 853d49c4bb..73b7631558 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAssociationAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/SingularAssociationAttributeBinding.java
@@ -1,64 +1,65 @@
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
 package org.hibernate.metamodel.binding;
 
 /**
  * Contract describing the attribute binding for singular associations ({@code many-to-one}, {@code one-to-one}).
  *
  * @author Gail Badner
  * @author Steve Ebersole
  */
+@SuppressWarnings( {"JavaDoc", "UnusedDeclaration"})
 public interface SingularAssociationAttributeBinding extends SingularAttributeBinding, AssociationAttributeBinding {
 	/**
 	 * Is this association based on a property reference (non PK column(s) as target of FK)?
 	 * <p/>
 	 * Convenience form of checking {@link #getReferencedAttributeName()} for {@code null}.
 	 * 
 	 * @return
 	 */
 	public boolean isPropertyReference();
 
 	/**
 	 * Obtain the name of the referenced entity.
 	 *
 	 * @return The referenced entity name
 	 */
 	public String getReferencedEntityName();
 
 	/**
 	 * Set the name of the
 	 * @param referencedEntityName
 	 */
 	public void setReferencedEntityName(String referencedEntityName);
 
 	public String getReferencedAttributeName();
 	public void setReferencedAttributeName(String referencedAttributeName);
 
 
 	// "resolvable"
 	public void resolveReference(AttributeBinding attributeBinding);
 	public boolean isReferenceResolved();
 	public EntityBinding getReferencedEntityBinding();
 	public AttributeBinding getReferencedAttributeBinding();
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Exportable.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Exportable.java
index 601fd119d6..7d720c2322 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Exportable.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Exportable.java
@@ -1,56 +1,59 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
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
 package org.hibernate.metamodel.relational;
 
 import org.hibernate.dialect.Dialect;
 
 /**
  * Contract for entities (in the ERD sense) which can be exported via {@code CREATE}, {@code ALTER}, etc
  *
  * @author Steve Ebersole
  */
 public interface Exportable {
 	/**
 	 * Get a unique identifier to make sure we are not exporting the same database structure multiple times.
 	 *
 	 * @return The exporting identifier.
 	 */
 	public String getExportIdentifier();
 
 	/**
 	 * Gets the SQL strings for creating the database object.
 	 *
-	 * @param dialect
+	 * @param dialect The dialect for which to generate the SQL creation strings
+	 *
 	 * @return the SQL strings for creating the database object.
 	 */
 	public String[] sqlCreateStrings(Dialect dialect);
 
 	/**
 	 * Gets the SQL strings for dropping the database object.
 	 *
-	 * @param dialect@return the SQL strings for dropping the database object.
+	 * @param dialect The dialect for which to generate the SQL drop strings
+	 *
+	 * @return the SQL strings for dropping the database object.
 	 */
 	public String[] sqlDropStrings(Dialect dialect);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Identifier.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Identifier.java
index d3d777565b..3cd04f9479 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Identifier.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Identifier.java
@@ -1,144 +1,144 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010 by Red Hat Inc and/or its affiliates or by
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
 package org.hibernate.metamodel.relational;
 
 
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * Models an identifier (name).
  *
  * @author Steve Ebersole
  */
 public class Identifier {
 	private final String name;
 	private final boolean isQuoted;
 
 	/**
 	 * Means to generate an {@link Identifier} instance from its simple name
 	 *
 	 * @param name The name
 	 *
-	 * @return
+	 * @return The identifier form of the name.
 	 */
 	public static Identifier toIdentifier(String name) {
 		if ( StringHelper.isEmpty( name ) ) {
 			return null;
 		}
 		final String trimmedName = name.trim();
 		if ( isQuoted( trimmedName ) ) {
 			final String bareName = trimmedName.substring( 1, trimmedName.length() - 1 );
 			return new Identifier( bareName, true );
 		}
 		else {
 			return new Identifier( trimmedName, false );
 		}
 	}
 
 	public static boolean isQuoted(String name) {
 		return name.startsWith( "`" ) && name.endsWith( "`" );
 	}
 
 	/**
 	 * Constructs an identifier instance.
 	 *
 	 * @param name The identifier text.
 	 * @param quoted Is this a quoted identifier?
 	 */
 	public Identifier(String name, boolean quoted) {
 		if ( StringHelper.isEmpty( name ) ) {
 			throw new IllegalIdentifierException( "Identifier text cannot be null" );
 		}
 		if ( isQuoted( name ) ) {
 			throw new IllegalIdentifierException( "Identifier text should not contain quote markers (`)" );
 		}
 		this.name = name;
 		this.isQuoted = quoted;
 	}
 
 	/**
 	 * Get the identifiers name (text)
 	 *
 	 * @return The name
 	 */
 	public String getName() {
 		return name;
 	}
 
 	/**
 	 * Is this a quoted identifier>
 	 *
 	 * @return True if this is a quote identifier; false otherwise.
 	 */
 	public boolean isQuoted() {
 		return isQuoted;
 	}
 
 	/**
 	 * If this is a quoted identifier, then return the identifier name
 	 * enclosed in dialect-specific open- and end-quotes; otherwise,
 	 * simply return the identifier name.
 	 *
-	 * @param dialect
-	 * @return if quoted, identifier name enclosed in dialect-specific
-	 *         open- and end-quotes; otherwise, the identifier name.
+	 * @param dialect The dialect whose dialect-specific quoting should be used.
+	 * @return if quoted, identifier name enclosed in dialect-specific open- and end-quotes; otherwise, the
+	 * identifier name.
 	 */
 	public String encloseInQuotesIfQuoted(Dialect dialect) {
 		return isQuoted ?
 				new StringBuilder( name.length() + 2 )
 						.append( dialect.openQuote() )
 						.append( name )
 						.append( dialect.closeQuote() )
 						.toString() :
 				name;
 	}
 
 	@Override
 	public String toString() {
 		return isQuoted
 				? '`' + getName() + '`'
 				: getName();
 	}
 
 	@Override
 	public boolean equals(Object o) {
 		if ( this == o ) {
 			return true;
 		}
 		if ( o == null || getClass() != o.getClass() ) {
 			return false;
 		}
 
 		Identifier that = (Identifier) o;
 
 		return isQuoted == that.isQuoted
 				&& name.equals( that.name );
 	}
 
 	@Override
 	public int hashCode() {
 		return name.hashCode();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
index 86f353adfb..f05b10abb8 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/EntityPersister.java
@@ -1,736 +1,736 @@
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
 
 	/**
 	 * Load the id for the entity based on the natural id.
 	 */
 	public Serializable loadEntityIdByNaturalId(Map<String, ?> naturalIdParameters, LockOptions lockOptions,
 			SessionImplementor session);
 
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
-	 * @noinspection JavaDoc
 	 */
+	@SuppressWarnings( {"JavaDoc"})
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
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/AbstractSerializableProxy.java b/hibernate-core/src/main/java/org/hibernate/proxy/AbstractSerializableProxy.java
index e858b9a05c..79f5bc8ddb 100644
--- a/hibernate-core/src/main/java/org/hibernate/proxy/AbstractSerializableProxy.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/AbstractSerializableProxy.java
@@ -1,71 +1,71 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Middleware LLC or third-party contributors as
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
 package org.hibernate.proxy;
 import java.io.Serializable;
 
 /**
  * Convenience base class for SerializableProxy.
  * 
  * @author Gail Badner
  */
 public abstract class AbstractSerializableProxy implements Serializable {
 	private String entityName;
 	private Serializable id;
 	private Boolean readOnly;
 
 	/**
 	 * For serialization
 	 */
 	protected AbstractSerializableProxy() {
 	}
 
 	protected AbstractSerializableProxy(String entityName, Serializable id, Boolean readOnly) {
 		this.entityName = entityName;
 		this.id = id;
 		this.readOnly = readOnly;
 	}
 
 	protected String getEntityName() {
 		return entityName;
 	}
 
 	protected Serializable getId() {
 		return id;
 	}
 
 	/**
 	 * Set the read-only/modifiable setting from this object in an AbstractLazyInitializer.
 	 *
 	 * This method should only be called during deserialization, before associating the
 	 * AbstractLazyInitializer with a session.
 	 *
-	 * @param li, the read-only/modifiable setting to use when
+	 * @param li the read-only/modifiable setting to use when
 	 * associated with a session; null indicates that the default should be used.
 	 * @throws IllegalStateException if isReadOnlySettingAvailable() == true
 	 */
 	protected void setReadOnlyBeforeAttachedToSession(AbstractLazyInitializer li) {
 		li.setReadOnlyBeforeAttachedToSession( readOnly );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/proxy/LazyInitializer.java b/hibernate-core/src/main/java/org/hibernate/proxy/LazyInitializer.java
index 557628e15a..2eb5d8c29d 100644
--- a/hibernate-core/src/main/java/org/hibernate/proxy/LazyInitializer.java
+++ b/hibernate-core/src/main/java/org/hibernate/proxy/LazyInitializer.java
@@ -1,182 +1,182 @@
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
 package org.hibernate.proxy;
 import java.io.Serializable;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 
 /**
  * Handles fetching of the underlying entity for a proxy
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface LazyInitializer {
 	/**
 	 * Initialize the proxy, fetching the target entity if necessary.
 	 *
 	 * @throws HibernateException Indicates a problem initializing the proxy.
 	 */
 	public void initialize() throws HibernateException;
 
 	/**
 	 * Retrieve the identifier value for the entity our owning proxy represents.
 	 *
 	 * @return The identifier value.
 	 */
 	public Serializable getIdentifier();
 
 	/**
 	 * Set the identifier value for the entity our owning proxy represents.
 	 *
 	 * @param id The identifier value.
 	 */
 	public void setIdentifier(Serializable id);
 
 	/**
 	 * The entity-name of the entity our owning proxy represents.
 	 *
 	 * @return The entity-name.
 	 */
 	public String getEntityName();
 
 	/**
 	 * Get the actual class of the entity.  Generally, {@link #getEntityName()} should be used instead.
 	 *
 	 * @return The actual entity class.
 	 */
 	public Class getPersistentClass();
 
 	/**
 	 * Is the proxy uninitialzed?
 	 *
 	 * @return True if uninitialized; false otherwise.
 	 */
 	public boolean isUninitialized();
 
 	/**
 	 * Return the underlying persistent object, initializing if necessary
 	 *
 	 * @return The underlying target entity.
 	 */
 	public Object getImplementation();
 
 	/**
 	 * Return the underlying persistent object in the given session, or null if not contained in this session's
 	 * persistence context.
 	 *
 	 * @param session The session to check
 	 *
 	 * @return The target, or null.
 	 *
 	 * @throws HibernateException Indicates problem locating the target.
 	 */
 	public Object getImplementation(SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Initialize the proxy manually by injecting its target.
 	 *
 	 * @param target The proxy target (the actual entity being proxied).
 	 */
 	public void setImplementation(Object target);
 
 	/**
 	 * Is the proxy's read-only/modifiable setting available?
 	 * @return true, if the setting is available
 	 *         false, if the proxy is detached or its associated session is closed
 	 */
 	public boolean isReadOnlySettingAvailable();
 
 	/**
 	 * Is the proxy read-only?.
 	 *
 	 * The read-only/modifiable setting is not available when the proxy is
 	 * detached or its associated session is closed.
 	 *
 	 * To check if the read-only/modifiable setting is available:
-	 * @see org.hibernate.proxy.LazyInitializer#isReadOnlySettingAvailable()
 	 *
 	 * @return true, if this proxy is read-only; false, otherwise
 	 * @throws org.hibernate.TransientObjectException if the proxy is detached (getSession() == null)
 	 * @throws org.hibernate.SessionException if the proxy is associated with a sesssion that is closed
 	 *
+	 * @see org.hibernate.proxy.LazyInitializer#isReadOnlySettingAvailable()
 	 * @see org.hibernate.Session#isReadOnly(Object entityOrProxy)
 	 */
 	public boolean isReadOnly();
 
 	/**
 	 * Set an associated modifiable proxy to read-only mode, or a read-only
 	 * proxy to modifiable mode. If the proxy is currently initialized, its
 	 * implementation will be set to the same mode; otherwise, when the
 	 * proxy is initialized, its implementation will have the same read-only/
 	 * modifiable setting as the proxy. In read-only mode, no snapshot is
 	 * maintained and the instance is never dirty checked.
 	 *
 	 * If the associated proxy already has the specified read-only/modifiable
 	 * setting, then this method does nothing.
 	 *
 	 * @param readOnly if true, the associated proxy is made read-only;
 	 *                  if false, the associated proxy is made modifiable.
 	 * @throws org.hibernate.TransientObjectException if the proxy is not association with a session
-	 * @throws org.hibernate.SessionException if the proxy is associated with a sesssion that is closed
+	 * @throws org.hibernate.SessionException if the proxy is associated with a session that is closed
 	 * 
-	 * @see {@link org.hibernate.Session#setReadOnly(Object entityOrProxy, boolean readOnly)}
+	 * @see org.hibernate.Session#setReadOnly(Object entityOrProxy, boolean readOnly)
 	 */
 	public void setReadOnly(boolean readOnly);
 
 	/**
 	 * Get the session to which this proxy is associated, or null if it is not attached.
 	 *
 	 * @return The associated session.
 	 */
 	public SessionImplementor getSession();
 
 	/**
 	 * Associate the proxy with the given session.
 	 * <p/>
 	 * Care should be given to make certain that the proxy is added to the session's persistence context as well
 	 * to maintain the symetry of the association.  That must be done seperately as this method simply sets an
 	 * internal reference.  We do also check that if there is already an associated session that the proxy
 	 * reference was removed from that previous session's persistence contet.
 	 *
 	 * @param session The session
 	 * @throws HibernateException Indicates that the proxy was still contained in the persistence context of the
 	 * "previous session".
 	 */
 	public void setSession(SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Unset this initializer's reference to session.  It is assumed that the caller is also taking care or
 	 * cleaning up the owning proxy's reference in the persistence context.
 	 * <p/>
 	 * Generally speaking this is intended to be called only during {@link org.hibernate.Session#evict} and
 	 * {@link org.hibernate.Session#clear} processing; most other use-cases should call {@link #setSession} instead.
 	 */
 	public void unsetSession();
 	
 	public void setUnwrap(boolean unwrap);
 	public boolean isUnwrap();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/sql/InFragment.java b/hibernate-core/src/main/java/org/hibernate/sql/InFragment.java
index 7c11244171..0236b3b6aa 100644
--- a/hibernate-core/src/main/java/org/hibernate/sql/InFragment.java
+++ b/hibernate-core/src/main/java/org/hibernate/sql/InFragment.java
@@ -1,132 +1,132 @@
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
 package org.hibernate.sql;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * An SQL IN expression.
  * <br>
  * <code>... in(...)</code>
  * <br>
  * @author Gavin King
  */
 public class InFragment {
 
 	public static final String NULL = "null";
 	public static final String NOT_NULL = "not null";
 
 	private String columnName;
-	private List values = new ArrayList();
+	private List<Object> values = new ArrayList<Object>();
 
 	/**
-	 * @param value, an SQL literal, NULL, or NOT_NULL
+	 * @param value an SQL literal, NULL, or NOT_NULL
+	 *
+	 * @return {@code this}, for method chaining
 	 */
 	public InFragment addValue(Object value) {
 		values.add(value);
 		return this;
 	}
 
 	public InFragment setColumn(String columnName) {
 		this.columnName = columnName;
 		return this;
 	}
 
 	public InFragment setColumn(String alias, String columnName) {
 		this.columnName = StringHelper.qualify( alias, columnName );
 		return setColumn(this.columnName);
 	}
 
 	public InFragment setFormula(String alias, String formulaTemplate) {
 		this.columnName = StringHelper.replace(formulaTemplate, Template.TEMPLATE, alias);
 		return setColumn(this.columnName);
 	}
 
 	public String toFragmentString() {
 
                 if (values.size() == 0) {
                    return "1=2";
                 }
 
                 StringBuilder buf = new StringBuilder(values.size() * 5);
 
                 if (values.size() == 1) {
                    Object value = values.get(0);
                    buf.append(columnName);
 
                    if (NULL.equals(value)) {
                       buf.append(" is null");
                    } else {
                       if (NOT_NULL.equals(value)) {
                          buf.append(" is not null");
                       } else {
                          buf.append('=').append(value);
                       }
                    }
                    return buf.toString();
                 }
                    
                 boolean allowNull = false;
 
                 for (Object value : values) {
                    if (NULL.equals(value)) {
                       allowNull = true;
                    } else {
                       if (NOT_NULL.equals(value)) {
                          throw new IllegalArgumentException("not null makes no sense for in expression");
                       }
                    }
                 }
 
                 if (allowNull) {
                    buf.append('(').append(columnName).append(" is null or ").append(columnName).append(" in (");
                 } else {
                    buf.append(columnName).append(" in (");
                 }
 
                 for (Object value : values) {
-                   if (NULL.equals(value)) {
-                      ;
-                   } else {
+                   if ( ! NULL.equals(value) ) {
                       buf.append(value);
                       buf.append(", ");
                    }
                 }
 
                 buf.setLength(buf.length() - 2);
 
                 if (allowNull) {
                    buf.append("))");
                 } else {
                    buf.append(')');
                 }
 
                 return buf.toString();
 
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/transform/CacheableResultTransformer.java b/hibernate-core/src/main/java/org/hibernate/transform/CacheableResultTransformer.java
index fece870183..dc9b20de6b 100644
--- a/hibernate-core/src/main/java/org/hibernate/transform/CacheableResultTransformer.java
+++ b/hibernate-core/src/main/java/org/hibernate/transform/CacheableResultTransformer.java
@@ -1,347 +1,335 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
- * Copyright (c) 2010, Red Hat Middleware LLC or third-party contributors as
+ * Copyright (c) 20102011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.transform;
 
 import java.lang.reflect.Array;
 import java.util.Arrays;
 import java.util.List;
 
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.type.Type;
 
 /**
- * A ResultTransformer that is used to transfor tuples to a value(s)
+ * A ResultTransformer that is used to transform tuples to a value(s)
  * that can be cached.
  *
  * @author Gail Badner
  */
 public class CacheableResultTransformer implements ResultTransformer {
 
 	// would be nice to be able to have this class extend
 	// PassThroughResultTransformer, but the default constructor
 	// is private (as it should be for a singleton)
 	private final static PassThroughResultTransformer ACTUAL_TRANSFORMER =
 			PassThroughResultTransformer.INSTANCE;
 	private final int tupleLength;
 	private final int tupleSubsetLength;
 
 	// array with the i-th element indicating whether the i-th
 	// expression returned by a query is included in the tuple;
 	// IMPLLEMENTATION NOTE:
 	// "joined" and "fetched" associations may use the same SQL,
 	// but result in different tuple and cached values. This is
 	// because "fetched" associations are excluded from the tuple.
 	//  includeInTuple provides a way to distinguish these 2 cases.
 	private final boolean[] includeInTuple;
 
 	// indexes for tuple that are included in the transformation;
 	// set to null if all elements in the tuple are included
 	private final int[] includeInTransformIndex;
 
 	/**
 	 * Returns a CacheableResultTransformer that is used to transform
 	 * tuples to a value(s) that can be cached.
 	 *
 	 * @param transformer - result transformer that will ultimately be
 	 *        be used (after caching results)
 	 * @param aliases - the aliases that correspond to the tuple;
 	 *        if it is non-null, its length must equal the number
 	 *        of true elements in includeInTuple[]
 	 * @param includeInTuple - array with the i-th element indicating
 	 *        whether the i-th expression returned by a query is
 	 *        included in the tuple; the number of true values equals
 	 *        the length of the tuple that will be transformed;
 	 *        must be non-null
+	 *
 	 * @return a CacheableResultTransformer that is used to transform
 	 *         tuples to a value(s) that can be cached.
 	 */
-	public static CacheableResultTransformer create(ResultTransformer transformer,
-													String[] aliases,
-													boolean[] includeInTuple) {
-		return transformer instanceof TupleSubsetResultTransformer ?
-				create( ( TupleSubsetResultTransformer ) transformer, aliases, includeInTuple ) :
-				create( includeInTuple )
-		;
+	public static CacheableResultTransformer create(
+			ResultTransformer transformer,
+			String[] aliases,
+			boolean[] includeInTuple) {
+		return transformer instanceof TupleSubsetResultTransformer
+				? create( ( TupleSubsetResultTransformer ) transformer, aliases, includeInTuple )
+				: create( includeInTuple );
 	}
 
 	/**
 	 * Returns a CacheableResultTransformer that is used to transform
 	 * tuples to a value(s) that can be cached.
 	 *
 	 * @param transformer - a tuple subset result transformer;
 	 *        must be non-null;
 	 * @param aliases - the aliases that correspond to the tuple;
 	 *        if it is non-null, its length must equal the number
 	 *        of true elements in includeInTuple[]
 	 * @param includeInTuple - array with the i-th element indicating
 	 *        whether the i-th expression returned by a query is
 	 *        included in the tuple; the number of true values equals
 	 *        the length of the tuple that will be transformed;
 	 *        must be non-null
+	 *
 	 * @return a CacheableResultTransformer that is used to transform
 	 *         tuples to a value(s) that can be cached.
 	 */
-	private static CacheableResultTransformer create(TupleSubsetResultTransformer transformer,
-													 String[] aliases,
-													 boolean[] includeInTuple) {
+	private static CacheableResultTransformer create(
+			TupleSubsetResultTransformer transformer,
+			String[] aliases,
+			boolean[] includeInTuple) {
 		if ( transformer == null ) {
 			throw new IllegalArgumentException( "transformer cannot be null" );
 		}
 		int tupleLength = ArrayHelper.countTrue( includeInTuple );
 		if ( aliases != null && aliases.length != tupleLength ) {
 			throw new IllegalArgumentException(
 					"if aliases is not null, then the length of aliases[] must equal the number of true elements in includeInTuple; " +
 							"aliases.length=" + aliases.length + "tupleLength=" + tupleLength
 			);
 		}
 		return new CacheableResultTransformer(
 				includeInTuple,
 				transformer.includeInTransform( aliases, tupleLength )
 		);
 	}
 
 	/**
 	 * Returns a CacheableResultTransformer that is used to transform
 	 * tuples to a value(s) that can be cached.
 	 *
 	 * @param includeInTuple - array with the i-th element indicating
 	 *        whether the i-th expression returned by a query is
 	 *        included in the tuple; the number of true values equals
 	 *        the length of the tuple that will be transformed;
 	 *        must be non-null
+	 *
 	 * @return a CacheableResultTransformer that is used to transform
 	 *         tuples to a value(s) that can be cached.
 	 */
 	private static CacheableResultTransformer create(boolean[] includeInTuple) {
 		return new CacheableResultTransformer( includeInTuple, null );
 	}
 
 	private CacheableResultTransformer(boolean[] includeInTuple, boolean[] includeInTransform) {
 		if ( includeInTuple == null ) {
 			throw new IllegalArgumentException( "includeInTuple cannot be null" );
 		}
 		this.includeInTuple = includeInTuple;
 		tupleLength = ArrayHelper.countTrue( includeInTuple );
 		tupleSubsetLength = (
 				includeInTransform == null ?
 						tupleLength :
 						ArrayHelper.countTrue( includeInTransform )
 		);
 		if ( tupleSubsetLength == tupleLength ) {
 			includeInTransformIndex = null;
 		}
 		else {
 			includeInTransformIndex = new int[tupleSubsetLength];
 			for ( int i = 0, j = 0 ; i < includeInTransform.length ; i++ ) {
 				if ( includeInTransform[ i ] ) {
 					includeInTransformIndex[ j ] =  i;
 					j++;
 				}
 			}
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Object transformTuple(Object[] tuple, String aliases[]) {
 		if ( aliases != null && aliases.length != tupleLength ) {
 			throw new IllegalStateException(
 					"aliases expected length is " + tupleLength +
 					"; actual length is " + aliases.length );
 		}
 		// really more correct to pass index( aliases.getClass(), aliases )
 		// as the 2nd arg to the following statement;
 		// passing null instead because it ends up being ignored.
 		return ACTUAL_TRANSFORMER.transformTuple( index( tuple.getClass(), tuple ), null );
 	}
 
 	/**
 	 * Re-transforms, if necessary, a List of values previously
 	 * transformed by this (or an equivalent) CacheableResultTransformer.
 	 * Each element of the list is re-transformed in place (i.e, List
 	 * elements are replaced with re-transformed values) and the original
 	 * List is returned.
 	 * <p/>
 	 * If re-transformation is unnecessary, the original List is returned
 	 * unchanged.
 	 *
 	 * @param transformedResults - results that were previously transformed
 	 * @param aliases - the aliases that correspond to the untransformed tuple;
 	 * @param transformer - the transformer for the re-transformation
+	 * @param includeInTuple indicates the indexes of
+	 *
 	 * @return transformedResults, with each element re-transformed (if nececessary)
 	 */
-	public List retransformResults(List transformedResults,
-								   String aliases[],
-								   ResultTransformer transformer,
-								   boolean[] includeInTuple) {
+	@SuppressWarnings( {"unchecked"})
+	public List retransformResults(
+			List transformedResults,
+			String aliases[],
+			ResultTransformer transformer,
+			boolean[] includeInTuple) {
 		if ( transformer == null ) {
 			throw new IllegalArgumentException( "transformer cannot be null" );
 		}
 		if ( ! this.equals( create( transformer, aliases, includeInTuple ) ) ) {
 			throw new IllegalStateException(
 					"this CacheableResultTransformer is inconsistent with specified arguments; cannot re-transform"
 			);
 		}
 		boolean requiresRetransform = true;
 		String[] aliasesToUse = aliases == null ? null : index( ( aliases.getClass() ), aliases );
 		if ( transformer == ACTUAL_TRANSFORMER ) {
 			requiresRetransform = false;
 		}
 		else if ( transformer instanceof TupleSubsetResultTransformer ) {
 			requiresRetransform =  ! ( ( TupleSubsetResultTransformer ) transformer ).isTransformedValueATupleElement(
 					aliasesToUse,
 					tupleLength
 			);
 		}
 		if ( requiresRetransform ) {
 			for ( int i = 0 ; i < transformedResults.size() ; i++ ) {
 				Object[] tuple = ACTUAL_TRANSFORMER.untransformToTuple(
 									transformedResults.get( i ),
 									tupleSubsetLength == 1
 				);
 				transformedResults.set( i, transformer.transformTuple( tuple, aliasesToUse ) );
 			}
 		}
 		return transformedResults;
 	}
 
 	/**
 	 * Untransforms, if necessary, a List of values previously
 	 * transformed by this (or an equivalent) CacheableResultTransformer.
 	 * Each element of the list is untransformed in place (i.e, List
 	 * elements are replaced with untransformed values) and the original
 	 * List is returned.
 	 * <p/>
 	 * If not unnecessary, the original List is returned
 	 * unchanged.
 	 * <p/>
 	 * NOTE: If transformed values are a subset of the original
 	 *       tuple, then, on return, elements corresponding to
 	 *       excluded tuple elements will be null.
 	 * @param results - results that were previously transformed
 	 * @return results, with each element untransformed (if nececessary)
 	 */
+	@SuppressWarnings( {"unchecked"})
 	public List untransformToTuples(List results) {
 		if ( includeInTransformIndex == null ) {
 			results = ACTUAL_TRANSFORMER.untransformToTuples(
 					results,
 					tupleSubsetLength == 1
 			);
 		}
 		else {
 			for ( int i = 0 ; i < results.size() ; i++ ) {
 				Object[] tuple = ACTUAL_TRANSFORMER.untransformToTuple(
 									results.get( i ),
 									tupleSubsetLength == 1
 				);
 				results.set( i, unindex( tuple.getClass(), tuple ) );
 			}
 
 		}
 		return results;
 	}
 
-	/**
-	 * Returns the result types for the transformed value.
-	 * @param tupleResultTypes
-	 * @return
-	 */
 	public Type[] getCachedResultTypes(Type[] tupleResultTypes) {
-		return tupleLength != tupleSubsetLength ?
-				index( tupleResultTypes.getClass(), tupleResultTypes ) :
-				tupleResultTypes
-		;
+		return tupleLength != tupleSubsetLength
+				? index( tupleResultTypes.getClass(), tupleResultTypes )
+				: tupleResultTypes;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public List transformList(List list) {
 		return list;
 	}
 
 	private <T> T[] index(Class<? extends T[]> clazz, T[] objects) {
 		T[] objectsIndexed = objects;
 		if ( objects != null &&
 				includeInTransformIndex != null &&
 				objects.length != tupleSubsetLength ) {
 			objectsIndexed = clazz.cast( Array.newInstance( clazz.getComponentType(), tupleSubsetLength ) );
 			for ( int i = 0 ; i < tupleSubsetLength; i++ ) {
 				objectsIndexed[ i ] = objects[ includeInTransformIndex[ i ] ];
 			}
 		}
 		return objectsIndexed;
 	}
 
 	private <T> T[] unindex(Class<? extends T[]> clazz, T[] objects) {
 		T[] objectsUnindexed = objects;
 		if ( objects != null &&
 				includeInTransformIndex != null &&
 				objects.length != tupleLength ) {
 			objectsUnindexed = clazz.cast( Array.newInstance( clazz.getComponentType(), tupleLength ) );
 			for ( int i = 0 ; i < tupleSubsetLength; i++ ) {
 				objectsUnindexed[ includeInTransformIndex[ i ] ] = objects[ i ];
 			}
 		}
 		return objectsUnindexed;
 	}
 
 	@Override
 	public boolean equals(Object o) {
 		if ( this == o ) {
 			return true;
 		}
 		if ( o == null || getClass() != o.getClass() ) {
 			return false;
 		}
 
 		CacheableResultTransformer that = ( CacheableResultTransformer ) o;
 
-		if ( tupleLength != that.tupleLength ) {
-			return false;
-		}
-		if ( tupleSubsetLength != that.tupleSubsetLength ) {
-			return false;
-		}
-		if ( !Arrays.equals( includeInTuple, that.includeInTuple ) ) {
-			return false;
-		}
-		if ( !Arrays.equals( includeInTransformIndex, that.includeInTransformIndex ) ) {
-			return false;
-		}
-
-		return true;
+		return tupleLength == that.tupleLength
+				&& tupleSubsetLength == that.tupleSubsetLength
+				&& Arrays.equals( includeInTuple, that.includeInTuple )
+				&& Arrays.equals( includeInTransformIndex, that.includeInTransformIndex );
 	}
 
 	@Override
 	public int hashCode() {
 		int result = tupleLength;
 		result = 31 * result + tupleSubsetLength;
 		result = 31 * result + ( includeInTuple != null ? Arrays.hashCode( includeInTuple ) : 0 );
 		result = 31 * result + ( includeInTransformIndex != null ? Arrays.hashCode( includeInTransformIndex ) : 0 );
 		return result;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java
index a6e6046a01..b652105468 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityTuplizer.java
@@ -1,302 +1,305 @@
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
 package org.hibernate.tuple.entity;
 import java.io.Serializable;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.property.Getter;
 import org.hibernate.tuple.Tuplizer;
 
 /**
  * Defines further responsibilities reagarding tuplization based on
  * a mapped entity.
  * <p/>
  * EntityTuplizer implementations should have the following constructor signatures:
  *      (org.hibernate.tuple.entity.EntityMetamodel, org.hibernate.mapping.PersistentClass)
  *      (org.hibernate.tuple.entity.EntityMetamodel, org.hibernate.metamodel.binding.EntityBinding)
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface EntityTuplizer extends Tuplizer {
 	/**
 	 * Return the entity-mode handled by this tuplizer instance.
 	 *
 	 * @return The entity-mode
 	 */
 	public EntityMode getEntityMode();
 
     /**
      * Create an entity instance initialized with the given identifier.
      *
      * @param id The identifier value for the entity to be instantiated.
      * @return The instantiated entity.
      * @throws HibernateException
 	 *
 	 * @deprecated Use {@link #instantiate(Serializable, SessionImplementor)} instead.
-	 * @noinspection JavaDoc
      */
+	@SuppressWarnings( {"JavaDoc"})
 	public Object instantiate(Serializable id) throws HibernateException;
 
     /**
      * Create an entity instance initialized with the given identifier.
      *
      * @param id The identifier value for the entity to be instantiated.
 	 * @param session The session from which is requests originates
 	 *
      * @return The instantiated entity.
      */
 	public Object instantiate(Serializable id, SessionImplementor session);
 
     /**
      * Extract the identifier value from the given entity.
      *
      * @param entity The entity from which to extract the identifier value.
 	 *
      * @return The identifier value.
 	 *
      * @throws HibernateException If the entity does not define an identifier property, or an
      * error occurs accessing its value.
 	 *
 	 * @deprecated Use {@link #getIdentifier(Object,SessionImplementor)} instead.
      */
 	public Serializable getIdentifier(Object entity) throws HibernateException;
 
     /**
      * Extract the identifier value from the given entity.
      *
      * @param entity The entity from which to extract the identifier value.
 	 * @param session The session from which is requests originates
 	 *
      * @return The identifier value.
      */
 	public Serializable getIdentifier(Object entity, SessionImplementor session);
 
     /**
      * Inject the identifier value into the given entity.
      * </p>
      * Has no effect if the entity does not define an identifier property
      *
      * @param entity The entity to inject with the identifier value.
      * @param id The value to be injected as the identifier.
 	 *
 	 * @deprecated Use {@link #setIdentifier(Object, Serializable, SessionImplementor)} instead.
-	 * @noinspection JavaDoc
      */
+	@SuppressWarnings( {"JavaDoc"})
 	public void setIdentifier(Object entity, Serializable id) throws HibernateException;
 
     /**
      * Inject the identifier value into the given entity.
      * </p>
      * Has no effect if the entity does not define an identifier property
      *
      * @param entity The entity to inject with the identifier value.
      * @param id The value to be injected as the identifier.
 	 * @param session The session from which is requests originates
      */
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session);
 
 	/**
 	 * Inject the given identifier and version into the entity, in order to
 	 * "roll back" to their original values.
 	 *
 	 * @param entity The entity for which to reset the id/version values
 	 * @param currentId The identifier value to inject into the entity.
 	 * @param currentVersion The version value to inject into the entity.
 	 *
 	 * @deprecated Use {@link #resetIdentifier(Object, Serializable, Object, SessionImplementor)} instead
 	 */
+	@SuppressWarnings( {"UnusedDeclaration"})
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion);
 
 	/**
 	 * Inject the given identifier and version into the entity, in order to
 	 * "roll back" to their original values.
 	 *
 	 * @param entity The entity for which to reset the id/version values
 	 * @param currentId The identifier value to inject into the entity.
 	 * @param currentVersion The version value to inject into the entity.
 	 * @param session The session from which the request originated
 	 */
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session);
 
     /**
      * Extract the value of the version property from the given entity.
      *
      * @param entity The entity from which to extract the version value.
      * @return The value of the version property, or null if not versioned.
-     * @throws HibernateException
+	 * @throws HibernateException Indicates a problem accessing the version property
      */
 	public Object getVersion(Object entity) throws HibernateException;
 
 	/**
 	 * Inject the value of a particular property.
 	 *
 	 * @param entity The entity into which to inject the value.
 	 * @param i The property's index.
 	 * @param value The property value to inject.
-	 * @throws HibernateException
+	 * @throws HibernateException Indicates a problem access the property
 	 */
 	public void setPropertyValue(Object entity, int i, Object value) throws HibernateException;
 
 	/**
 	 * Inject the value of a particular property.
 	 *
 	 * @param entity The entity into which to inject the value.
 	 * @param propertyName The name of the property.
 	 * @param value The property value to inject.
-	 * @throws HibernateException
+	 * @throws HibernateException Indicates a problem access the property
 	 */
 	public void setPropertyValue(Object entity, String propertyName, Object value) throws HibernateException;
 
 	/**
 	 * Extract the values of the insertable properties of the entity (including backrefs)
 	 *
 	 * @param entity The entity from which to extract.
 	 * @param mergeMap a map of instances being merged to merged instances
 	 * @param session The session in which the resuest is being made.
 	 * @return The insertable property values.
-	 * @throws HibernateException
+	 * @throws HibernateException Indicates a problem access the properties
 	 */
 	public Object[] getPropertyValuesToInsert(Object entity, Map mergeMap, SessionImplementor session)
 	throws HibernateException;
 
 	/**
 	 * Extract the value of a particular property from the given entity.
 	 *
 	 * @param entity The entity from which to extract the property value.
 	 * @param propertyName The name of the property for which to extract the value.
 	 * @return The current value of the given property on the given entity.
-	 * @throws HibernateException
+	 * @throws HibernateException Indicates a problem access the property
 	 */
 	public Object getPropertyValue(Object entity, String propertyName) throws HibernateException;
 
     /**
      * Called just after the entities properties have been initialized.
      *
      * @param entity The entity being initialized.
      * @param lazyPropertiesAreUnfetched Are defined lazy properties currently unfecthed
      * @param session The session initializing this entity.
      */
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session);
 
 	/**
 	 * Does this entity, for this mode, present a possibility for proxying?
 	 *
 	 * @return True if this tuplizer can generate proxies for this entity.
 	 */
 	public boolean hasProxy();
 
 	/**
 	 * Generates an appropriate proxy representation of this entity for this
 	 * entity-mode.
 	 *
 	 * @param id The id of the instance for which to generate a proxy.
 	 * @param session The session to which the proxy should be bound.
 	 * @return The generate proxies.
 	 * @throws HibernateException Indicates an error generating the proxy.
 	 */
 	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Does the {@link #getMappedClass() class} managed by this tuplizer implement
 	 * the {@link org.hibernate.classic.Lifecycle} interface.
 	 *
 	 * @return True if the Lifecycle interface is implemented; false otherwise.
 	 */
 	public boolean isLifecycleImplementor();
 
 	/**
 	 * Returns the java class to which generated proxies will be typed.
 	 * <p/>
 	 * todo : look at fully encapsulating {@link org.hibernate.engine.spi.PersistenceContext#narrowProxy} here,
 	 * since that is the only external use of this method
 	 *
 	 * @return The java class to which generated proxies will be typed
 	 */
 	public Class getConcreteProxyClass();
 	
     /**
      * Does the given entity instance have any currently uninitialized lazy properties?
      *
      * @param entity The entity to be check for uninitialized lazy properties.
      * @return True if uninitialized lazy properties were found; false otherwise.
      */
 	public boolean hasUninitializedLazyProperties(Object entity);
 	
 	/**
 	 * Is it an instrumented POJO?
+	 *
+	 * @return {@code true} if the entity class is instrumented; {@code false} otherwise.
 	 */
 	public boolean isInstrumented();
 
 	/**
 	 * Get any {@link EntityNameResolver EntityNameResolvers} associated with this {@link Tuplizer}.
 	 *
 	 * @return The associated resolvers.  May be null or empty.
 	 */
 	public EntityNameResolver[] getEntityNameResolvers();
 
 	/**
 	 * Given an entity instance, determine the most appropriate (most targeted) entity-name which represents it.
 	 * This is called in situations where we already know an entity name for the given entityInstance; we are being
 	 * asked to determine if there is a more appropriate entity-name to use, specifically within an inheritence
 	 * hierarchy.
 	 * <p/>
 	 * For example, consider a case where a user calls <tt>session.update( "Animal", cat );</tt>.  Here, the
 	 * user has explicitly provided <tt>Animal</tt> as the entity-name.  However, they have passed in an instance
 	 * of <tt>Cat</tt> which is a subclass of <tt>Animal</tt>.  In this case, we would return <tt>Cat</tt> as the
 	 * entity-name.
 	 * <p/>
 	 * <tt>null</tt> may be returned from calls to this method.  The meaining of <tt>null</tt> in that case is assumed
 	 * to be that we should use whatever explicit entity-name the user provided (<tt>Animal</tt> rather than <tt>Cat</tt>
 	 * in the example above).
 	 *
 	 * @param entityInstance The entity instance.
 	 * @param factory Reference to the SessionFactory.
 	 *
 	 * @return The most appropriate entity name to use.
 	 *
 	 * @throws HibernateException If we are unable to determine an entity-name within the inheritence hierarchy.
 	 */
 	public String determineConcreteSubclassEntityName(Object entityInstance, SessionFactoryImplementor factory);
 
 	/**
 	 * Retrieve the getter for the identifier property.  May return null.
 	 *
 	 * @return The getter for the identifier property.
 	 */
 	public Getter getIdentifierGetter();
 
 	/**
 	 * Retrieve the getter for the version property.  May return null.
 	 *
 	 * @return The getter for the version property.
 	 */
 	public Getter getVersionGetter();
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/type/Type.java b/hibernate-core/src/main/java/org/hibernate/type/Type.java
index eb395b151c..d2b3aa058d 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/Type.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/Type.java
@@ -1,540 +1,605 @@
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
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.dom4j.Node;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.metamodel.relational.Size;
 
 /**
  * Defines a mapping between a Java type and one or more JDBC {@linkplain java.sql.Types types}, as well
  * as describing the in-memory semantics of the given java type (how do we check it for 'dirtiness', how do
  * we copy values, etc).
  * <p/>
  * Application developers needing custom types can implement this interface (either directly or via subclassing an
  * existing impl) or by the (slightly more stable, though more limited) {@link org.hibernate.usertype.UserType}
  * interface.
  * <p/>
  * Implementations of this interface must certainly be thread-safe.  It is recommended that they be immutable as
  * well, though that is difficult to achieve completely given the no-arg constructor requirement for custom types.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface Type extends Serializable {
 	/**
 	 * Return true if the implementation is castable to {@link AssociationType}. This does not necessarily imply that
 	 * the type actually represents an association.  Essentially a polymorphic version of
 	 * {@code (type instanceof AssociationType.class)}
 	 *
 	 * @return True if this type is also an {@link AssociationType} implementor; false otherwise.
 	 */
 	public boolean isAssociationType();
 
 	/**
 	 * Return true if the implementation is castable to {@link CollectionType}. Essentially a polymorphic version of
 	 * {@code (type instanceof CollectionType.class)}
 	 * <p/>
 	 * A {@link CollectionType} is additionally an {@link AssociationType}; so if this method returns true,
 	 * {@link #isAssociationType()} should also return true.
 	 *
 	 * @return True if this type is also an {@link CollectionType} implementor; false otherwise.
 	 */
 	public boolean isCollectionType();
 
 	/**
 	 * Return true if the implementation is castable to {@link EntityType}. Essentially a polymorphic
 	 * version of {@code (type instanceof EntityType.class)}.
 	 * <p/>
 	 * An {@link EntityType} is additionally an {@link AssociationType}; so if this method returns true,
 	 * {@link #isAssociationType()} should also return true.
 	 *
 	 * @return True if this type is also an {@link EntityType} implementor; false otherwise.
 	 */
 	public boolean isEntityType();
 
 	/**
 	 * Return true if the implementation is castable to {@link AnyType}. Essentially a polymorphic
 	 * version of {@code (type instanceof AnyType.class)}.
 	 * <p/>
 	 * An {@link AnyType} is additionally an {@link AssociationType}; so if this method returns true,
 	 * {@link #isAssociationType()} should also return true.
 	 *
 	 * @return True if this type is also an {@link AnyType} implementor; false otherwise.
 	 */
 	public boolean isAnyType();
 
 	/**
 	 * Return true if the implementation is castable to {@link CompositeType}. Essentially a polymorphic
 	 * version of {@code (type instanceof CompositeType.class)}.  A component type may own collections or
 	 * associations and hence must provide certain extra functionality.
 	 *
 	 * @return True if this type is also an {@link CompositeType} implementor; false otherwise.
 	 */
 	public boolean isComponentType();
 
 	/**
 	 * How many columns are used to persist this type.  Always the same as {@code sqlTypes(mapping).length}
 	 *
 	 * @param mapping The mapping object :/
 	 *
 	 * @return The number of columns
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
 	public int getColumnSpan(Mapping mapping) throws MappingException;
 
 	/**
 	 * Return the JDBC types codes (per {@link java.sql.Types}) for the columns mapped by this type.
 	 * <p/>
 	 * NOTE: The number of elements in this array matches the return from {@link #getColumnSpan}.
 	 *
 	 * @param mapping The mapping object :/
 	 *
 	 * @return The JDBC type codes.
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
 	public int[] sqlTypes(Mapping mapping) throws MappingException;
 
 	/**
 	 * Return the column sizes dictated by this type.  For example, the mapping for a {@code char}/{@link Character} would
 	 * have a dictated length limit of 1; for a string-based {@link java.util.UUID} would have a size limit of 36; etc.
 	 * <p/>
 	 * NOTE: The number of elements in this array matches the return from {@link #getColumnSpan}.
 	 *
 	 * @param mapping The mapping object :/
 	 * @todo Would be much much better to have this aware of Dialect once the service/metamodel split is done
 	 *
 	 * @return The dictated sizes.
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException;
 
 	/**
 	 * Defines the column sizes to use according to this type if the user did not explicitly say (and if no
 	 * {@link #dictatedSizes} were given).
 	 * <p/>
 	 * NOTE: The number of elements in this array matches the return from {@link #getColumnSpan}.
 	 *
 	 * @param mapping The mapping object :/
 	 * @todo Would be much much better to have this aware of Dialect once the service/metamodel split is done
 	 *
 	 * @return The default sizes.
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
 	public Size[] defaultSizes(Mapping mapping) throws MappingException;
 
 	/**
 	 * The class returned by {@link #nullSafeGet} methods. This is used to  establish the class of an array of
 	 * this type.
 	 *
 	 * @return The java type class handled by this type.
 	 */
 	public Class getReturnedClass();
 	
+	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean isXMLElement();
 
 	/**
 	 * Compare two instances of the class mapped by this type for persistence "equality" (equality of persistent
 	 * state) taking a shortcut for entity references.
 	 * <p/>
-	 * For most types this should equate to {@link #equals} check on the values.  For associations the implication
-	 * is a bit different.  For most types it is conceivable to simply delegate to {@link #isEqual}
+	 * For most types this should equate to an {@link Object#equals equals} check on the values.  For associations
+	 * the implication is a bit different.  For most types it is conceivable to simply delegate to {@link #isEqual}
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 *
 	 * @return True if there are considered the same (see discussion above).
 	 *
 	 * @throws HibernateException A problem occurred performing the comparison
 	 */
 	public boolean isSame(Object x, Object y) throws HibernateException;
 
 	/**
 	 * Compare two instances of the class mapped by this type for persistence "equality" (equality of persistent
 	 * state).
 	 * <p/>
 	 * This should always equate to some form of comparison of the value's internal state.  As an example, for
 	 * something like a date the comparison should be based on its internal "time" state based on the specific portion
 	 * it is meant to represent (timestamp, date, time).
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 *
 	 * @return True if there are considered equal (see discussion above).
 	 *
 	 * @throws HibernateException A problem occurred performing the comparison
 	 */
 	public boolean isEqual(Object x, Object y) throws HibernateException;
 
 	/**
 	 * Compare two instances of the class mapped by this type for persistence "equality" (equality of persistent
 	 * state).
 	 * <p/>
 	 * This should always equate to some form of comparison of the value's internal state.  As an example, for
 	 * something like a date the comparison should be based on its internal "time" state based on the specific portion
 	 * it is meant to represent (timestamp, date, time).
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 * @param factory The session factory
 	 *
 	 * @return True if there are considered equal (see discussion above).
 	 *
 	 * @throws HibernateException A problem occurred performing the comparison
 	 */
 	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) throws HibernateException;
 
 	/**
 	 * Get a hash code, consistent with persistence "equality".  Again for most types the normal usage is to
-	 * delegate to the value's {@link #hashCode}.
+	 * delegate to the value's {@link Object#hashCode hashCode}.
 	 *
 	 * @param x The value for which to retrieve a hash code
 	 * @return The hash code
 	 *
 	 * @throws HibernateException A problem occurred calculating the hash code
 	 */
 	public int getHashCode(Object x) throws HibernateException;
 
 	/**
 	 * Get a hash code, consistent with persistence "equality".  Again for most types the normal usage is to
-	 * delegate to the value's {@link #hashCode}.
+	 * delegate to the value's {@link Object#hashCode hashCode}.
 	 *
 	 * @param x The value for which to retrieve a hash code
 	 * @param factory The session factory
 	 *
 	 * @return The hash code
 	 *
 	 * @throws HibernateException A problem occurred calculating the hash code
 	 */
 	public int getHashCode(Object x, SessionFactoryImplementor factory) throws HibernateException;
 	
 	/**
 	 * Perform a {@link java.util.Comparator} style comparison between values
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 *
 	 * @return The comparison result.  See {@link java.util.Comparator#compare} for a discussion.
 	 */
 	public int compare(Object x, Object y);
 
 	/**
 	 * Should the parent be considered dirty, given both the old and current value?
 	 * 
 	 * @param old the old value
 	 * @param current the current value
 	 * @param session The session from which the request originated.
 	 *
 	 * @return true if the field is dirty
 	 *
 	 * @throws HibernateException A problem occurred performing the checking
 	 */
 	public boolean isDirty(Object old, Object current, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Should the parent be considered dirty, given both the old and current value?
 	 *
 	 * @param oldState the old value
 	 * @param currentState the current value
 	 * @param checkable An array of booleans indicating which columns making up the value are actually checkable
 	 * @param session The session from which the request originated.
 	 *
 	 * @return true if the field is dirty
 	 *
 	 * @throws HibernateException A problem occurred performing the checking
 	 */
 	public boolean isDirty(Object oldState, Object currentState, boolean[] checkable, SessionImplementor session)
 			throws HibernateException;
 
 	/**
 	 * Has the value been modified compared to the current database state?  The difference between this
 	 * and the {@link #isDirty} methods is that here we need to account for "partially" built values.  This is really
 	 * only an issue with association types.  For most type implementations it is enough to simply delegate to
 	 * {@link #isDirty} here/
 	 *
 	 * @param dbState the database state, in a "hydrated" form, with identifiers unresolved
 	 * @param currentState the current state of the object
 	 * @param checkable which columns are actually updatable
 	 * @param session The session from which the request originated.
 	 *
 	 * @return true if the field has been modified
 	 *
 	 * @throws HibernateException A problem occurred performing the checking
 	 */
 	public boolean isModified(Object dbState, Object currentState, boolean[] checkable, SessionImplementor session)
 			throws HibernateException;
 
 	/**
-	 * Retrieve an instance of the mapped class from a JDBC resultset. Implementors
+	 * Extract a value of the {@link #getReturnedClass() mapped class} from the JDBC result set. Implementors
 	 * should handle possibility of null values.
 	 *
-	 * @see Type#hydrate(ResultSet, String[], SessionImplementor, Object) alternative, 2-phase property initialization
-	 * @param rs
-	 * @param names the column names
-	 * @param session
+	 * @param rs The result set from which to extract value.
+	 * @param names the column names making up this type value (use to read from result set)
+	 * @param session The originating session
 	 * @param owner the parent entity
-	 * @return Object
-	 * @throws HibernateException
-	 * @throws SQLException
+	 *
+	 * @return The extracted value
+	 *
+	 * @throws HibernateException An error from Hibernate
+	 * @throws SQLException An error from the JDBC driver
+	 *
+	 * @see Type#hydrate(ResultSet, String[], SessionImplementor, Object) alternative, 2-phase property initialization
 	 */
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException;
 
 	/**
-	 * Retrieve an instance of the mapped class from a JDBC resultset. Implementations
-	 * should handle possibility of null values. This method might be called if the
-	 * type is known to be a single-column type.
+	 * Extract a value of the {@link #getReturnedClass() mapped class} from the JDBC result set. Implementors
+	 * should handle possibility of null values.  This form might be called if the type is known to be a
+	 * single-column type.
 	 *
-	 * @param rs
-	 * @param name the column name
-	 * @param session
+	 * @param rs The result set from which to extract value.
+	 * @param name the column name making up this type value (use to read from result set)
+	 * @param session The originating session
 	 * @param owner the parent entity
-	 * @return Object
-	 * @throws HibernateException
-	 * @throws SQLException
+	 *
+	 * @return The extracted value
+	 *
+	 * @throws HibernateException An error from Hibernate
+	 * @throws SQLException An error from the JDBC driver
 	 */
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException;
 
 	/**
-	 * Write an instance of the mapped class to a prepared statement, ignoring some columns. 
-	 * Implementors should handle possibility of null values. A multi-column type should be 
-	 * written to parameters starting from <tt>index</tt>.
-	 * @param st
+	 * Bind a value represented by an instance of the {@link #getReturnedClass() mapped class} to the JDBC prepared
+	 * statement, ignoring some columns as dictated by the 'settable' parameter.  Implementors should handle the
+	 * possibility of null values.  A multi-column type should bind parameters starting from <tt>index</tt>.
+	 *
+	 * @param st The JDBC prepared statement to which to bind
 	 * @param value the object to write
-	 * @param index statement parameter index
-	 * @param settable an array indicating which columns to ignore
-	 * @param session
+	 * @param index starting parameter bind index
+	 * @param settable an array indicating which columns to bind/ignore
+	 * @param session The originating session
 	 *
-	 * @throws HibernateException
-	 * @throws SQLException
+	 * @throws HibernateException An error from Hibernate
+	 * @throws SQLException An error from the JDBC driver
 	 */
 	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session)
 	throws HibernateException, SQLException;
 
 	/**
-	 * Write an instance of the mapped class to a prepared statement. Implementors
-	 * should handle possibility of null values. A multi-column type should be written
-	 * to parameters starting from <tt>index</tt>.
-	 * @param st
+	 * Bind a value represented by an instance of the {@link #getReturnedClass() mapped class} to the JDBC prepared
+	 * statement.  Implementors should handle possibility of null values.  A multi-column type should bind parameters
+	 * starting from <tt>index</tt>.
+	 *
+	 * @param st The JDBC prepared statement to which to bind
 	 * @param value the object to write
-	 * @param index statement parameter index
-	 * @param session
+	 * @param index starting parameter bind index
+	 * @param session The originating session
 	 *
-	 * @throws HibernateException
-	 * @throws SQLException
+	 * @throws HibernateException An error from Hibernate
+	 * @throws SQLException An error from the JDBC driver
 	 */
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
 	throws HibernateException, SQLException;
 
 	/**
-	 * A representation of the value to be embedded in an XML element.
+	 * Generate a representation of the value for logging purposes.
 	 *
-	 * @param value
-	 * @param factory
-	 * @return String
-	 * @throws HibernateException
+	 * @param value The value to be logged
+	 * @param factory The session factory
+	 *
+	 * @return The loggable representation
+	 *
+	 * @throws HibernateException An error from Hibernate
 	 */
-	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory)
+	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 	throws HibernateException;
 
 	/**
-	 * A representation of the value to be embedded in a log file.
+	 * A representation of the value to be embedded in an XML element.
+	 *
+	 * @param node The XML node to which to write the value
+	 * @param value The value to write
+	 * @param factory The session factory
 	 *
-	 * @param value
-	 * @param factory
-	 * @return String
-	 * @throws HibernateException
+	 * @throws HibernateException An error from Hibernate
 	 */
-	public String toLoggableString(Object value, SessionFactoryImplementor factory)
+	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory)
 	throws HibernateException;
 
 	/**
 	 * Parse the XML representation of an instance.
-	 * @param xml
-	 * @param factory
 	 *
-	 * @return an instance of the type
-	 * @throws HibernateException
+	 * @param xml The XML node from which to read the value
+	 * @param factory The session factory
+	 *
+	 * @return an instance of the {@link #getReturnedClass() mapped class}
+	 *
+	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException;
 
 	/**
 	 * Returns the abbreviated name of the type.
 	 *
 	 * @return String the Hibernate type name
 	 */
 	public String getName();
 
 	/**
-	 * Return a deep copy of the persistent state, stopping at entities and at
-	 * collections.
+	 * Return a deep copy of the persistent state, stopping at entities and at collections.
+	 *
+	 * @param value The value to be copied
+	 * @param factory The session factory
 	 *
-	 * @param value generally a collection element or entity field
-	 * @param factory
-	 * @return Object a copy
+	 * @return The deep copy
+	 *
+	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 	throws HibernateException;
 
 	/**
 	 * Are objects of this type mutable. (With respect to the referencing object ...
 	 * entities and collections are considered immutable because they manage their
 	 * own internal state.)
 	 *
 	 * @return boolean
 	 */
 	public boolean isMutable();
 
 	/**
-	 * Return a cacheable "disassembled" representation of the object.
+	 * Return a disassembled representation of the object.  This is the value Hibernate will use in second level
+	 * caching, so care should be taken to break values down to their simplest forms; for entities especially, this
+	 * means breaking them down into their constituent parts.
+	 *
 	 * @param value the value to cache
-	 * @param session the session
+	 * @param session the originating session
 	 * @param owner optional parent entity object (needed for collections)
+	 *
 	 * @return the disassembled, deep cloned state
+	 *
+	 * @throws HibernateException An error from Hibernate
 	 */
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException;
 
 	/**
-	 * Reconstruct the object from its cached "disassembled" state.
+	 * Reconstruct the object from its disassembled state.  This method is the reciprocal of {@link #disassemble}
+	 *
 	 * @param cached the disassembled state from the cache
-	 * @param session the session
+	 * @param session the originating session
 	 * @param owner the parent entity object
-	 * @return the the object
+	 *
+	 * @return the (re)assembled object
+	 *
+	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 	throws HibernateException;
 	
 	/**
 	 * Called before assembling a query result set from the query cache, to allow batch fetching
 	 * of entities missing from the second-level cache.
+	 *
+	 * @param cached The key
+	 * @param session The originating session
 	 */
 	public void beforeAssemble(Serializable cached, SessionImplementor session);
 
 	/**
-	 * Retrieve an instance of the mapped class, or the identifier of an entity or collection, 
-	 * from a JDBC resultset. This is useful for 2-phase property initialization - the second 
-	 * phase is a call to <tt>resolveIdentifier()</tt>.
+	 * Extract a value from the JDBC result set.  This is useful for 2-phase property initialization - the second
+	 * phase is a call to {@link #resolve}
+	 * This hydrated value will be either:<ul>
+	 *     <li>in the case of an entity or collection type, the key</li>
+	 *     <li>otherwise, the value itself</li>
+	 * </ul>
 	 * 
-	 * @see Type#resolve(Object, SessionImplementor, Object)
-	 * @param rs
-	 * @param names the column names
-	 * @param session the session
+	 * @param rs The JDBC result set
+	 * @param names the column names making up this type value (use to read from result set)
+	 * @param session The originating session
 	 * @param owner the parent entity
-	 * @return Object an identifier or actual value
-	 * @throws HibernateException
-	 * @throws SQLException
+	 *
+	 * @return An entity or collection key, or an actual value.
+	 *
+	 * @throws HibernateException An error from Hibernate
+	 * @throws SQLException An error from the JDBC driver
+	 *
+	 * @see #resolve
 	 */
 	public Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException;
 
 	/**
-	 * Map identifiers to entities or collections. This is the second phase of 2-phase property 
-	 * initialization.
+	 * The second phase of 2-phase loading.  Only really pertinent for entities and collections.  Here we resolve the
+	 * identifier to an entity or collection instance
 	 * 
-	 * @see Type#hydrate(ResultSet, String[], SessionImplementor, Object)
 	 * @param value an identifier or value returned by <tt>hydrate()</tt>
 	 * @param owner the parent entity
 	 * @param session the session
+	 * 
 	 * @return the given value, or the value associated with the identifier
-	 * @throws HibernateException
+	 *
+	 * @throws HibernateException An error from Hibernate
+	 *
+	 * @see #hydrate
 	 */
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException;
 	
 	/**
-	 * Given a hydrated, but unresolved value, return a value that may be used to
-	 * reconstruct property-ref associations.
+	 * Given a hydrated, but unresolved value, return a value that may be used to reconstruct property-ref
+	 * associations.
+	 *
+	 * @param value The unresolved, hydrated value
+	 * @param session THe originating session
+	 * @param owner The value owner
+	 *
+	 * @return The semi-resolved value
+	 *
+	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException;
 	
 	/**
-	 * Get the type of a semi-resolved value.
+	 * As part of 2-phase loading, when we perform resolving what is the resolved type for this type?  Generally
+	 * speaking the type and its semi-resolved type will be the same.  The main deviation from this is in the
+	 * case of an entity where the type would be the entity type and semi-resolved type would be its identifier type
+	 *
+	 * @param factory The session factory
+	 *
+	 * @return The semi-resolved type
 	 */
 	public Type getSemiResolvedType(SessionFactoryImplementor factory);
 
 	/**
 	 * During merge, replace the existing (target) value in the entity we are merging to
 	 * with a new (original) value from the detached entity we are merging. For immutable
 	 * objects, or null values, it is safe to simply return the first parameter. For
 	 * mutable objects, it is safe to return a copy of the first parameter. For objects
 	 * with component values, it might make sense to recursively replace component values.
 	 *
 	 * @param original the value from the detached entity being merged
 	 * @param target the value in the managed entity
+	 * @param session The originating session
+	 * @param owner The owner of the value
+	 * @param copyCache The cache of already copied/replaced values
+	 *
 	 * @return the value to be merged
+	 *
+	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object replace(
 			Object original, 
 			Object target, 
 			SessionImplementor session, 
 			Object owner, 
-			Map copyCache)
-	throws HibernateException;
+			Map copyCache) throws HibernateException;
 	
 	/**
 	 * During merge, replace the existing (target) value in the entity we are merging to
 	 * with a new (original) value from the detached entity we are merging. For immutable
 	 * objects, or null values, it is safe to simply return the first parameter. For
 	 * mutable objects, it is safe to return a copy of the first parameter. For objects
 	 * with component values, it might make sense to recursively replace component values.
 	 *
 	 * @param original the value from the detached entity being merged
 	 * @param target the value in the managed entity
+	 * @param session The originating session
+	 * @param owner The owner of the value
+	 * @param copyCache The cache of already copied/replaced values
+	 * @param foreignKeyDirection For associations, which direction does the foreign key point?
+	 *
 	 * @return the value to be merged
+	 *
+	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object replace(
 			Object original, 
 			Object target, 
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache, 
-			ForeignKeyDirection foreignKeyDirection)
-	throws HibernateException;
+			ForeignKeyDirection foreignKeyDirection) throws HibernateException;
 	
 	/**
 	 * Given an instance of the type, return an array of boolean, indicating
 	 * which mapped columns would be null.
 	 * 
 	 * @param value an instance of the type
+	 * @param mapping The mapping abstraction
+	 *
+	 * @return array indicating column nullness for a value instance
 	 */
 	public boolean[] toColumnNullness(Object value, Mapping mapping);
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java
index 88ef8be9f2..de8ae5279b 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/descriptor/sql/SqlTypeDescriptor.java
@@ -1,58 +1,58 @@
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
 
 import java.io.Serializable;
 
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 
 /**
  * Descriptor for the <tt>SQL</tt>/<tt>JDBC</tt> side of a value mapping.
  *
  * @author Steve Ebersole
  */
 public interface SqlTypeDescriptor extends Serializable {
 	/**
 	 * Return the {@linkplain java.sql.Types JDBC type-code} for the column mapped by this type.
 	 *
 	 * @return typeCode The JDBC type-code
 	 */
 	public int getSqlType();
 
 	/**
 	 * Is this descriptor available for remapping?
 	 *
-	 * @return
+	 * @return {@code true} indicates this descriptor can be remapped; otherwise, {@code false}
 	 *
 	 * @see org.hibernate.type.descriptor.WrapperOptions#remapSqlTypeDescriptor
 	 * @see org.hibernate.dialect.Dialect#remapSqlTypeDescriptor
 	 */
 	public boolean canBeRemapped();
 
 	public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor);
 
 	public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor);
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/HibernateEntityManager.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/HibernateEntityManager.java
index 19a0b01556..511924519b 100755
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/HibernateEntityManager.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/HibernateEntityManager.java
@@ -1,38 +1,41 @@
 /*
- * Copyright (c) 2009, Red Hat Middleware LLC or third-party contributors as
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2009-2011, Red Hat Inc. or third-party contributors as
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
 import javax.persistence.EntityManager;
 import org.hibernate.Session;
 
 /**
  * Additional contract for Hibernate implementations of {@link EntityManager} providing access to various Hibernate
  * specific functionality.
  *
  * @author Gavin King
  */
 public interface HibernateEntityManager extends EntityManager {
 	/**
 	 * Retrieve a reference to the Hibernate {@link Session} used by this {@link EntityManager}.
-	 * @return
+	 *
+	 * @return The session
 	 */
 	public Session getSession();
 }
diff --git a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/path/AbstractFromImpl.java b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/path/AbstractFromImpl.java
index 51c615eb97..6c1042977d 100644
--- a/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/path/AbstractFromImpl.java
+++ b/hibernate-entitymanager/src/main/java/org/hibernate/ejb/criteria/path/AbstractFromImpl.java
@@ -1,651 +1,601 @@
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
 package org.hibernate.ejb.criteria.path;
 import java.io.Serializable;
 import java.util.Collections;
 import java.util.LinkedHashSet;
 import java.util.Set;
 import javax.persistence.criteria.CollectionJoin;
 import javax.persistence.criteria.Fetch;
 import javax.persistence.criteria.From;
 import javax.persistence.criteria.Join;
 import javax.persistence.criteria.JoinType;
 import javax.persistence.criteria.ListJoin;
 import javax.persistence.criteria.MapJoin;
 import javax.persistence.criteria.SetJoin;
 import javax.persistence.metamodel.Attribute;
 import javax.persistence.metamodel.CollectionAttribute;
 import javax.persistence.metamodel.ListAttribute;
 import javax.persistence.metamodel.ManagedType;
 import javax.persistence.metamodel.MapAttribute;
 import javax.persistence.metamodel.PluralAttribute;
 import javax.persistence.metamodel.SetAttribute;
 import javax.persistence.metamodel.SingularAttribute;
 import javax.persistence.metamodel.Type;
 import org.hibernate.ejb.criteria.BasicPathUsageException;
 import org.hibernate.ejb.criteria.CollectionJoinImplementor;
 import org.hibernate.ejb.criteria.CriteriaBuilderImpl;
 import org.hibernate.ejb.criteria.CriteriaQueryCompiler;
 import org.hibernate.ejb.criteria.CriteriaSubqueryImpl;
 import org.hibernate.ejb.criteria.FromImplementor;
 import org.hibernate.ejb.criteria.JoinImplementor;
 import org.hibernate.ejb.criteria.ListJoinImplementor;
 import org.hibernate.ejb.criteria.MapJoinImplementor;
 import org.hibernate.ejb.criteria.PathSource;
 import org.hibernate.ejb.criteria.SetJoinImplementor;
 
 /**
  * Convenience base class for various {@link javax.persistence.criteria.From} implementors.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractFromImpl<Z,X>
 		extends AbstractPathImpl<X>
 		implements From<Z,X>, FromImplementor<Z,X>, Serializable {
 
 	public static final JoinType DEFAULT_JOIN_TYPE = JoinType.INNER;
 
     private Set<Join<X, ?>> joins;
     private Set<Fetch<X, ?>> fetches;
 
 	public AbstractFromImpl(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType) {
 		this( criteriaBuilder, javaType, null );
 	}
 
 	public AbstractFromImpl(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType, PathSource pathSource) {
 		super( criteriaBuilder, javaType, pathSource );
 	}
 
 	@Override
 	@SuppressWarnings({ "unchecked" })
 	public PathSource<Z> getPathSource() {
 		return super.getPathSource();
 	}
 
 	@Override
 	public String getPathIdentifier() {
 		return getAlias();
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	@Override
 	protected boolean canBeDereferenced() {
 		return true;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public void prepareAlias(CriteriaQueryCompiler.RenderingContext renderingContext) {
 		if ( getAlias() == null ) {
 			if ( isCorrelated() ) {
 				setAlias( getCorrelationParent().getAlias() );
 			}
 			else {
 				setAlias( renderingContext.generateAlias() );
 			}
 		}
 	}
 
 	@Override
 	public String renderProjection(CriteriaQueryCompiler.RenderingContext renderingContext) {
 		prepareAlias( renderingContext );
 		return getAlias();
 	}
 
 	@Override
 	public String render(CriteriaQueryCompiler.RenderingContext renderingContext) {
 		return renderProjection( renderingContext );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public Attribute<?, ?> getAttribute() {
 		return null;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
 	public From<?, Z> getParent() {
 		return null;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	protected Attribute<X, ?> locateAttributeInternal(String name) {
 		return (Attribute<X, ?>) locateManagedType().getAttribute( name );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected ManagedType<? super X> locateManagedType() {
 		// by default, this should be the model
 		return (ManagedType<? super X>) getModel();
 	}
 
 
 	// CORRELATION ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	// IMPL NOTE : another means from handling correlations is to create a series of
 	//		specialized From implementations that represent the correlation roots.  While
 	//		that may be cleaner code-wise, it is certainly means creating a lot of "extra"
 	//		classes since we'd need one for each Subquery#correlate method
 
 	private FromImplementor<Z,X> correlationParent;
 
 	private JoinScope<X> joinScope = new BasicJoinScope();
 
 	/**
 	 * Helper contract used to define who/what keeps track of joins and fetches made from this <tt>FROM</tt>.
 	 */
 	public static interface JoinScope<X> extends Serializable {
 		public void addJoin(Join<X, ?> join);
 		public void addFetch(Fetch<X,?> fetch);
 	}
 
 	protected class BasicJoinScope implements JoinScope<X> {
+		@Override
 		public void addJoin(Join<X, ?> join) {
 			if ( joins == null ) {
 				joins = new LinkedHashSet<Join<X,?>>();
 			}
 			joins.add( join );
 		}
 
+		@Override
 		public void addFetch(Fetch<X, ?> fetch) {
 			if ( fetches == null ) {
 				fetches = new LinkedHashSet<Fetch<X,?>>();
 			}
 			fetches.add( fetch );
 		}
 	}
 
 	protected class CorrelationJoinScope implements JoinScope<X> {
+		@Override
 		public void addJoin(Join<X, ?> join) {
 			if ( joins == null ) {
 				joins = new LinkedHashSet<Join<X,?>>();
 			}
 			joins.add( join );
 		}
 
+		@Override
 		public void addFetch(Fetch<X, ?> fetch) {
 			throw new UnsupportedOperationException( "Cannot define fetch from a subquery correlation" );
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public boolean isCorrelated() {
 		return getCorrelationParent() != null;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public FromImplementor<Z,X> getCorrelationParent() {
 		return correlationParent;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public FromImplementor<Z, X> correlateTo(CriteriaSubqueryImpl subquery) {
 		final FromImplementor<Z, X> correlationDelegate = createCorrelationDelegate();
 		correlationDelegate.prepareCorrelationDelegate( this );
 		return correlationDelegate;
 	}
 
 	protected abstract FromImplementor<Z, X> createCorrelationDelegate();
 
+	@Override
 	public void prepareCorrelationDelegate(FromImplementor<Z, X> parent) {
 		this.joinScope = new CorrelationJoinScope();
 		this.correlationParent = parent;
 	}
 
 	@Override
 	public String getAlias() {
 		return isCorrelated() ? getCorrelationParent().getAlias() : super.getAlias();
 	}
 
 	// JOINS ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected abstract boolean canBeJoinSource();
 
 	private RuntimeException illegalJoin() {
 		return new IllegalArgumentException(
 				"Collection of values [" + getPathIdentifier() + "] cannot be source of a join"
 		);
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public Set<Join<X, ?>> getJoins() {
 		return joins == null
 				? Collections.EMPTY_SET
 				: joins;
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> singularAttribute) {
 		return join( singularAttribute, DEFAULT_JOIN_TYPE );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attribute, JoinType jt) {
 		if ( ! canBeJoinSource() ) {
 			throw illegalJoin();
 		}
 
 		Join<X, Y> join = constructJoin( attribute, jt );
 		joinScope.addJoin( join );
 		return join;
 	}
 
 	private <Y> JoinImplementor<X, Y> constructJoin(SingularAttribute<? super X, Y> attribute, JoinType jt) {
 		if ( Type.PersistenceType.BASIC.equals( attribute.getType().getPersistenceType() ) ) {
 			throw new BasicPathUsageException( "Cannot join to attribute of basic type", attribute );
         }
 
 		// TODO : runtime check that the attribute in fact belongs to this From's model/bindable
 
 		if ( jt.equals( JoinType.RIGHT ) ) {
 			throw new UnsupportedOperationException( "RIGHT JOIN not supported" );
 		}
 
 		final Class<Y> attributeType = attribute.getBindableJavaType();
 		return new SingularAttributeJoin<X,Y>(
 				criteriaBuilder(),
 				attributeType,
 				this,
 				attribute,
 				jt
 		);
 	}
-	/**
-	 * {@inheritDoc}
-	 */
+
+	@Override
 	public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> collection) {
 		return join( collection, DEFAULT_JOIN_TYPE );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> collection, JoinType jt) {
 		if ( ! canBeJoinSource() ) {
 			throw illegalJoin();
 		}
 
 		final CollectionJoin<X, Y> join = constructJoin( collection, jt );
 		joinScope.addJoin( join );
 		return join;
 	}
 
 	private <Y> CollectionJoinImplementor<X, Y> constructJoin(CollectionAttribute<? super X, Y> collection, JoinType jt) {
 		if ( jt.equals( JoinType.RIGHT ) ) {
 			throw new UnsupportedOperationException( "RIGHT JOIN not supported" );
 		}
 
 		// TODO : runtime check that the attribute in fact belongs to this From's model/bindable
 
 		final Class<Y> attributeType = collection.getBindableJavaType();
 		return new CollectionAttributeJoin<X, Y>(
 				criteriaBuilder(),
 				attributeType,
 				this,
 				collection,
 				jt
 		);
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <Y> SetJoin<X, Y> join(SetAttribute<? super X, Y> set) {
 		return join( set, DEFAULT_JOIN_TYPE );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <Y> SetJoin<X, Y> join(SetAttribute<? super X, Y> set, JoinType jt) {
 		if ( ! canBeJoinSource() ) {
 			throw illegalJoin();
 		}
 
 		final SetJoin<X, Y> join = constructJoin( set, jt );
 		joinScope.addJoin( join );
 		return join;
 	}
 
 	private <Y> SetJoinImplementor<X, Y> constructJoin(SetAttribute<? super X, Y> set, JoinType jt) {
 		if ( jt.equals( JoinType.RIGHT ) ) {
 			throw new UnsupportedOperationException( "RIGHT JOIN not supported" );
 		}
 
 		// TODO : runtime check that the attribute in fact belongs to this From's model/bindable
 
 		final Class<Y> attributeType = set.getBindableJavaType();
 		return new SetAttributeJoin<X,Y>( criteriaBuilder(), attributeType, this, set, jt );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> list) {
 		return join( list, DEFAULT_JOIN_TYPE );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> list, JoinType jt) {
 		if ( ! canBeJoinSource() ) {
 			throw illegalJoin();
 		}
 
 		final ListJoin<X, Y> join = constructJoin( list, jt );
 		joinScope.addJoin( join );
 		return join;
 	}
 
 	private  <Y> ListJoinImplementor<X, Y> constructJoin(ListAttribute<? super X, Y> list, JoinType jt) {
 		if ( jt.equals( JoinType.RIGHT ) ) {
 			throw new UnsupportedOperationException( "RIGHT JOIN not supported" );
 		}
 
 		// TODO : runtime check that the attribute in fact belongs to this From's model/bindable
 
 		final Class<Y> attributeType = list.getBindableJavaType();
 		return new ListAttributeJoin<X,Y>( criteriaBuilder(), attributeType, this, list, jt );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> map) {
 		return join( map, DEFAULT_JOIN_TYPE );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> map, JoinType jt) {
 		if ( ! canBeJoinSource() ) {
 			throw illegalJoin();
 		}
 
 		final MapJoin<X, K, V> join = constructJoin( map, jt );
 		joinScope.addJoin( join );
 		return join;
 	}
 
 	private <K, V> MapJoinImplementor<X, K, V> constructJoin(MapAttribute<? super X, K, V> map, JoinType jt) {
 		if ( jt.equals( JoinType.RIGHT ) ) {
 			throw new UnsupportedOperationException( "RIGHT JOIN not supported" );
 		}
 
 		// TODO : runtime check that the attribute in fact belongs to this From's model/bindable
 
 		final Class<V> attributeType = map.getBindableJavaType();
 		return new MapAttributeJoin<X, K, V>( criteriaBuilder(), attributeType, this, map, jt );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <X,Y> Join<X, Y> join(String attributeName) {
 		return join( attributeName, DEFAULT_JOIN_TYPE );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X,Y> Join<X, Y> join(String attributeName, JoinType jt) {
 		if ( ! canBeJoinSource() ) {
 			throw illegalJoin();
 		}
 
 		if ( jt.equals( JoinType.RIGHT ) ) {
 			throw new UnsupportedOperationException( "RIGHT JOIN not supported" );
 		}
 
 		final Attribute<X,?> attribute = (Attribute<X, ?>) locateAttribute( attributeName );
 		if ( attribute.isCollection() ) {
 			final PluralAttribute pluralAttribute = ( PluralAttribute ) attribute;
 			if ( PluralAttribute.CollectionType.COLLECTION.equals( pluralAttribute.getCollectionType() ) ) {
 				return (Join<X,Y>) join( (CollectionAttribute) attribute, jt );
 			}
 			else if ( PluralAttribute.CollectionType.LIST.equals( pluralAttribute.getCollectionType() ) ) {
 				return (Join<X,Y>) join( (ListAttribute) attribute, jt );
 			}
 			else if ( PluralAttribute.CollectionType.SET.equals( pluralAttribute.getCollectionType() ) ) {
 				return (Join<X,Y>) join( (SetAttribute) attribute, jt );
 			}
 			else {
 				return (Join<X,Y>) join( (MapAttribute) attribute, jt );
 			}
 		}
 		else {
 			return (Join<X,Y>) join( (SingularAttribute)attribute, jt );
 		}
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <X,Y> CollectionJoin<X, Y> joinCollection(String attributeName) {
 		return joinCollection( attributeName, DEFAULT_JOIN_TYPE );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X,Y> CollectionJoin<X, Y> joinCollection(String attributeName, JoinType jt) {
 		final Attribute<X,?> attribute = (Attribute<X, ?>) locateAttribute( attributeName );
 		if ( ! attribute.isCollection() ) {
             throw new IllegalArgumentException( "Requested attribute was not a collection" );
 		}
 
 		final PluralAttribute pluralAttribute = ( PluralAttribute ) attribute;
 		if ( ! PluralAttribute.CollectionType.COLLECTION.equals( pluralAttribute.getCollectionType() ) ) {
             throw new IllegalArgumentException( "Requested attribute was not a collection" );
 		}
 
 		return (CollectionJoin<X,Y>) join( (CollectionAttribute) attribute, jt );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <X,Y> SetJoin<X, Y> joinSet(String attributeName) {
 		return joinSet( attributeName, DEFAULT_JOIN_TYPE );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X,Y> SetJoin<X, Y> joinSet(String attributeName, JoinType jt) {
 		final Attribute<X,?> attribute = (Attribute<X, ?>) locateAttribute( attributeName );
 		if ( ! attribute.isCollection() ) {
             throw new IllegalArgumentException( "Requested attribute was not a set" );
 		}
 
 		final PluralAttribute pluralAttribute = ( PluralAttribute ) attribute;
 		if ( ! PluralAttribute.CollectionType.SET.equals( pluralAttribute.getCollectionType() ) ) {
             throw new IllegalArgumentException( "Requested attribute was not a set" );
 		}
 
 		return (SetJoin<X,Y>) join( (SetAttribute) attribute, jt );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <X,Y> ListJoin<X, Y> joinList(String attributeName) {
 		return joinList( attributeName, DEFAULT_JOIN_TYPE );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X,Y> ListJoin<X, Y> joinList(String attributeName, JoinType jt) {
 		final Attribute<X,?> attribute = (Attribute<X, ?>) locateAttribute( attributeName );
 		if ( ! attribute.isCollection() ) {
             throw new IllegalArgumentException( "Requested attribute was not a list" );
 		}
 
 		final PluralAttribute pluralAttribute = ( PluralAttribute ) attribute;
 		if ( ! PluralAttribute.CollectionType.LIST.equals( pluralAttribute.getCollectionType() ) ) {
             throw new IllegalArgumentException( "Requested attribute was not a list" );
 		}
 
 		return (ListJoin<X,Y>) join( (ListAttribute) attribute, jt );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	public <X, K, V> MapJoin<X, K, V> joinMap(String attributeName) {
 		return joinMap( attributeName, DEFAULT_JOIN_TYPE );
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X, K, V> MapJoin<X, K, V> joinMap(String attributeName, JoinType jt) {
 		final Attribute<X,?> attribute = (Attribute<X, ?>) locateAttribute( attributeName );
 		if ( ! attribute.isCollection() ) {
             throw new IllegalArgumentException( "Requested attribute was not a map" );
 		}
 
 		final PluralAttribute pluralAttribute = ( PluralAttribute ) attribute;
 		if ( ! PluralAttribute.CollectionType.MAP.equals( pluralAttribute.getCollectionType() ) ) {
             throw new IllegalArgumentException( "Requested attribute was not a map" );
 		}
 
 		return (MapJoin<X,K,V>) join( (MapAttribute) attribute, jt );
 	}
 
 
 	// FETCHES ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected boolean canBeFetchSource() {
 		// the conditions should be the same...
 		return canBeJoinSource();
 	}
 
 	private RuntimeException illegalFetch() {
 		return new IllegalArgumentException(
 				"Collection of values [" + getPathIdentifier() + "] cannot be source of a fetch"
 		);
 	}
 
-	/**
-	 * {@inheritDoc}
-	 */
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public Set<Fetch<X, ?>> getFetches() {
 		return fetches == null
 				? Collections.EMPTY_SET
 				: fetches;
 	}
 
+	@Override
 	public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> singularAttribute) {
 		return fetch( singularAttribute, DEFAULT_JOIN_TYPE );
 	}
 
+	@Override
 	public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attribute, JoinType jt) {
 		if ( ! canBeFetchSource() ) {
 			throw illegalFetch();
 		}
 
 		Fetch<X, Y> fetch = constructJoin( attribute, jt );
 		joinScope.addFetch( fetch );
 		return fetch;
 	}
 
+	@Override
 	public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> pluralAttribute) {
 		return fetch( pluralAttribute, DEFAULT_JOIN_TYPE );
 	}
 
+	@Override
 	public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> pluralAttribute, JoinType jt) {
 		if ( ! canBeFetchSource() ) {
 			throw illegalFetch();
 		}
 
 		final Fetch<X, Y> fetch;
 		// TODO : combine Fetch and Join hierarchies (JoinImplementor extends Join,Fetch???)
 		if ( PluralAttribute.CollectionType.COLLECTION.equals( pluralAttribute.getCollectionType() ) ) {
 			fetch = constructJoin( (CollectionAttribute<X,Y>) pluralAttribute, jt );
 		}
 		else if ( PluralAttribute.CollectionType.LIST.equals( pluralAttribute.getCollectionType() ) ) {
 			fetch = constructJoin( (ListAttribute<X,Y>) pluralAttribute, jt );
 		}
 		else if ( PluralAttribute.CollectionType.SET.equals( pluralAttribute.getCollectionType() ) ) {
 			fetch = constructJoin( (SetAttribute<X,Y>) pluralAttribute, jt );
 		}
 		else {
 			fetch = constructJoin( (MapAttribute<X,?,Y>) pluralAttribute, jt );
 		}
 		joinScope.addFetch( fetch );
 		return fetch;
 	}
 
+	@Override
 	public <X,Y> Fetch<X, Y> fetch(String attributeName) {
 		return fetch( attributeName, DEFAULT_JOIN_TYPE );
 	}
 
+	@Override
 	@SuppressWarnings({ "unchecked" })
 	public <X,Y> Fetch<X, Y> fetch(String attributeName, JoinType jt) {
 		if ( ! canBeFetchSource() ) {
 			throw illegalFetch();
 		}
 
 		Attribute<X,?> attribute = (Attribute<X, ?>) locateAttribute( attributeName );
 		if ( attribute.isCollection() ) {
 			return (Fetch<X, Y>) fetch( (PluralAttribute) attribute, jt );
 		}
 		else {
 			return (Fetch<X, Y>) fetch( (SingularAttribute) attribute, jt );
 		}
 	}
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/AuditEntityNameRegister.java b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/AuditEntityNameRegister.java
index 45f898dc57..53eb159a44 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/AuditEntityNameRegister.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/AuditEntityNameRegister.java
@@ -1,49 +1,51 @@
 package org.hibernate.envers.configuration.metadata;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.hibernate.MappingException;
 
 /**
  * A register of all audit entity names used so far.
  * @author Adam Warski (adam at warski dot org)
  */
 public class AuditEntityNameRegister {
     private final Set<String> auditEntityNames = new HashSet<String>();
 
     /**
      * @param auditEntityName Name of the audit entity.
      * @return True if the given audit entity name is already used.
      */
     private boolean check(String auditEntityName) {
         return auditEntityNames.contains(auditEntityName);
     }
 
     /**
      * Register an audit entity name. If the name is already registered, an exception is thrown.
      * @param auditEntityName Name of the audit entity.
      */
     public void register(String auditEntityName) {
         if (auditEntityNames.contains(auditEntityName)) {
             throw new MappingException("The audit entity name '" + auditEntityName + "' is already registered.");
         }
         
         auditEntityNames.add(auditEntityName);
     }
 
     /**
      * Creates a unique (not yet registered) audit entity name by appending consecutive numbers to the base
      * name. If the base name is not yet used, it is returned unmodified.
+	 *
      * @param baseAuditEntityName The base entity name.
-     * @return 
+	 *
+     * @return A unique audit entity name
      */
     public String createUnique(final String baseAuditEntityName) {
         String auditEntityName = baseAuditEntityName;
         int count = 1;
         while (check(auditEntityName)) {
             auditEntityName = baseAuditEntityName + count++;
         }
 
         return auditEntityName;
     }
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/reader/FirstLevelCache.java b/hibernate-envers/src/main/java/org/hibernate/envers/reader/FirstLevelCache.java
index d3cf4d11fe..a145c1dc43 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/reader/FirstLevelCache.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/reader/FirstLevelCache.java
@@ -1,113 +1,116 @@
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
 package org.hibernate.envers.reader;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.envers.internal.EnversMessageLogger;
 import org.hibernate.envers.tools.Triple;
 
 import static org.hibernate.envers.tools.Tools.newHashMap;
 import static org.hibernate.envers.tools.Triple.make;
 
 /**
  * First level cache for versioned entities, versions reader-scoped. Each entity is uniquely identified by a
  * revision number and entity id.
  * @author Adam Warski (adam at warski dot org)
  * @author Hern&aacute;n Chanfreau
  */
 public class FirstLevelCache {
 
     public static final EnversMessageLogger LOG = Logger.getMessageLogger(EnversMessageLogger.class, FirstLevelCache.class.getName());
 
     /**
      * cache for resolve an object for a given id, revision and entityName.
      */
     private final Map<Triple<String, Number, Object>, Object> cache;
 
     /**
      * used to resolve the entityName for a given id, revision and entity.
      */
     private final Map<Triple<Object, Number, Object>, String> entityNameCache;
 
     public FirstLevelCache() {
         cache = newHashMap();
         entityNameCache = newHashMap();
     }
 
     public Object get(String entityName, Number revision, Object id) {
         LOG.debugf("Resolving object from First Level Cache: EntityName:%s - primaryKey:%s - revision:%s", entityName, id, revision);
         return cache.get(make(entityName, revision, id));
     }
 
     public void put(String entityName, Number revision, Object id, Object entity) {
         LOG.debugf("Caching entity on First Level Cache:  - primaryKey:%s - revision:%s - entityName:%s", id, revision, entityName);
         cache.put(make(entityName, revision, id), entity);
     }
 
     public boolean contains(String entityName, Number revision, Object id) {
         return cache.containsKey(make(entityName, revision, id));
     }
 
     /**
      * Adds the entityName into the cache. The key is a triple make with primaryKey, revision and entity
-     * @param id, primaryKey
-     * @param revision, revision number
-     * @param entity, object retrieved by envers
-     * @param entityName, value of the cache
+     * @param id primaryKey
+     * @param revision revision number
+     * @param entity object retrieved by envers
+     * @param entityName value of the cache
      */
     public void putOnEntityNameCache(Object id, Number revision, Object entity, String entityName) {
         LOG.debugf("Caching entityName on First Level Cache:  - primaryKey:%s - revision:%s - entity:%s -> entityName:%s",
                    id,
                    revision,
                    entity.getClass().getName(),
                    entityName);
     	entityNameCache.put(make(id, revision, entity), entityName);
     }
 
     /**
      * Gets the entityName from the cache. The key is a triple make with primaryKey, revision and entity
-     * @param id, primaryKey
-     * @param revision, revision number
-     * @param entity, object retrieved by envers
+	 *
+     * @param id primaryKey
+     * @param revision revision number
+     * @param entity object retrieved by envers
+	 *
+	 * @return The appropriate entity name
      */
     public String getFromEntityNameCache(Object id, Number revision, Object entity) {
         LOG.debugf("Trying to resolve entityName from First Level Cache: - primaryKey:%s - revision:%s - entity:%s",
                    id,
                    revision,
                    entity);
     	return entityNameCache.get(make(id, revision, entity));
     }
 
 	/**
 	 * @param id primaryKey
 	 * @param revision revision number
 	 * @param entity object retrieved by envers
 	 * @return true if entityNameCache contains the triple
 	 */
     public boolean containsEntityName(Object id, Number revision, Object entity) {
     	return entityNameCache.containsKey(make(id, revision, entity));
     }
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/DialectCheck.java b/hibernate-testing/src/main/java/org/hibernate/testing/DialectCheck.java
index d3775c935d..6be7019dd1 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/DialectCheck.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/DialectCheck.java
@@ -1,43 +1,44 @@
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
 package org.hibernate.testing;
 
 import org.hibernate.dialect.Dialect;
 
 /**
  * Defines a means to check {@link Dialect} features for use in "test protection" checks.  Used from
  * {@link RequiresDialectFeature}
  *
  * @author Hardy Ferentschik
  * @author Steve Ebersole
  */
 public interface DialectCheck {
 	/**
 	 * Does the given dialect match the defined check?
 	 *
-	 * @param dialect
-	 * @return
+	 * @param dialect The dialect against which to check
+	 *
+	 * @return {@code true} if it matches; {@code false} otherwise.
 	 */
 	public boolean isMatch(Dialect dialect);
 }
diff --git a/hibernate-testing/src/main/java/org/hibernate/testing/OnExpectedFailure.java b/hibernate-testing/src/main/java/org/hibernate/testing/OnExpectedFailure.java
index 9457c07366..5027bbc8b6 100644
--- a/hibernate-testing/src/main/java/org/hibernate/testing/OnExpectedFailure.java
+++ b/hibernate-testing/src/main/java/org/hibernate/testing/OnExpectedFailure.java
@@ -1,40 +1,39 @@
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
 package org.hibernate.testing;
 
 import java.lang.annotation.ElementType;
 import java.lang.annotation.Retention;
 import java.lang.annotation.RetentionPolicy;
 import java.lang.annotation.Target;
 
 /**
  * Annotation used to identify a method as a callback to be executed whenever a {@link FailureExpected} is handled.
  *
  * @author Steve Ebersole
- * @see
  */
 @Retention( RetentionPolicy.RUNTIME )
 @Target( ElementType.METHOD )
 public @interface OnExpectedFailure {
 }
diff --git a/release/release.gradle b/release/release.gradle
index fd73c836fc..6c5dcd3ffd 100644
--- a/release/release.gradle
+++ b/release/release.gradle
@@ -1,206 +1,219 @@
 apply plugin: 'base'
 apply plugin: 'idea'
 
 buildDir = "target"
 
 ideaModule {
 }
 
 
 
 // Javadocs ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 javadocBuildDir = dir( buildDirName + "/documentation/javadocs" )
 
-def List subProjectsToSkipForJavadoc = ['release','documentation'];
-def List sourceSetsToSkipForJavadoc = ['test','matrix'];
-
 def copyRightYear = new java.util.GregorianCalendar().get( java.util.Calendar.YEAR );
 
 task aggregateJavadocs(type: Javadoc) {
+    // exclude any generated sources (this is not working: http://forums.gradle.org/gradle/topics/excluding_generated_source_from_javadoc)
+    exclude "**/generated-src/**"
+
+    // process each project, building up:
+    //      1) appropriate sources
+    //      2) classpath
+    //      3) the package list for groups
     Set<String> apiPackages = new HashSet<String>()
     Set<String> spiPackages = new HashSet<String>()
     Set<String> internalPackages = new HashSet<String>()
-
-    parent.subprojects.each{ subProject->
-        if ( !subProjectsToSkipForJavadoc.contains( subProject.name ) ) {
-            subProject.sourceSets.each { sourceSet ->
-                if ( !sourceSetsToSkipForJavadoc.contains( sourceSet.name ) ) {
+    parent.subprojects.each{ Project subProject->
+        // skip certain sub-projects
+        if ( ! ['release','documentation'].contains( subProject.name ) ) {
+            subProject.sourceSets.each { SourceSet sourceSet ->
+                // skip certain source sets
+                if ( ! ['test','matrix'].contains( sourceSet.name ) ) {
                     source sourceSet.java
 
                     if( classpath ) {
                         classpath += sourceSet.classes + sourceSet.compileClasspath
                     }
                     else {
                         classpath = sourceSet.classes + sourceSet.compileClasspath
                     }
 
                     sourceSet.java.each { javaFile ->
                         final String packageName = determinePackageName( sourceSet.java, javaFile );
                         if ( packageName.endsWith( ".internal" ) || packageName.contains( ".internal." ) ) {
                             internalPackages.add( packageName );
                         }
                         else if ( packageName.endsWith( ".spi" ) || packageName.contains( ".spi." ) ) {
                             spiPackages.add( packageName );
                         }
+                        else if ( packageName.startsWith( "org.hibernate.testing" ) ) {
+                            // do nothing as testing support is already handled...
+                        }
                         else {
                             apiPackages.add( packageName );
                         }
                     }
                 }
             }
         }
     }
 
+    // apply standard config
     description = "Build the aggregated JavaDocs for all modules"
     maxMemory = '512m'
     destinationDir = javadocBuildDir.dir
     configure( options ) {
-        overview = new File( projectDir, 'src/javadoc/package.html' )
+        overview = new File( projectDir, 'src/javadoc/overview.html' )
         stylesheetFile = new File( projectDir, 'src/javadoc/stylesheet.css' )
         windowTitle = 'Hibernate JavaDocs'
         docTitle = "Hibernate JavaDoc ($project.version)"
         bottom = "Copyright &copy; 2001-$copyRightYear <a href=\"http://redhat.com\">Red Hat, Inc.</a>  All Rights Reserved."
         use = true
         links = [ 'http://download.oracle.com/javase/6/docs/api/', 'http://download.oracle.com/javaee/6/api/' ]
         group( 'API', apiPackages.asList() )
         group( 'SPI', spiPackages.asList() )
         group( 'Internal', internalPackages.asList() )
         group ( 'Testing Support', ['org.hibernate.testing*'] )
+// ugh, http://issues.gradle.org/browse/GRADLE-1563
+//        tags ["todo:X"]
+// work around:
+        addStringOption( "tag", "todo:X" )
     }
 }
 
 String determinePackageName(SourceDirectorySet sourceDirectorySet, File javaFile) {
     final javaFileAbsolutePath = javaFile.absolutePath;
     for ( File sourceDirectory : sourceDirectorySet.srcDirs ) {
         final String sourceDirectoryAbsolutePath = sourceDirectory.absolutePath;
         if ( javaFileAbsolutePath.startsWith( sourceDirectoryAbsolutePath ) ) {
             final String javaFileRelativePath = javaFileAbsolutePath.substring(
                     sourceDirectoryAbsolutePath.length() + 1,
                     javaFileAbsolutePath.lastIndexOf( '/' )
             );
             return javaFileRelativePath.replace( '/', '.' );
         }
     }
     throw new RuntimeException( "ugh" );
 }
 
 aggregateJavadocs.doLast {
     copy {
         from new File( projectDir, 'src/javadoc/images' )
         into new File( javadocBuildDir.dir, "/images" )
     }
 }
 
 // release bundles ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 releaseBuildDir = dir( buildDirName )
 task prepareReleaseBundles( dependsOn: [parent.project( 'documentation' ).tasks.buildDocs,parent.project('hibernate-entitymanager' ).tasks.buildDocs,aggregateJavadocs] )
 
 
 releaseCopySpec = copySpec {
     into( "hibernate-release-$project.version" ) {
         from new File( parent.projectDir, 'lgpl.txt' )
         from new File( parent.projectDir, 'changelog.txt' )
         from new File( parent.projectDir, 'hibernate_logo.gif' )
 
         into('lib/required') {
             from parent.project( 'hibernate-core' ).configurations.provided.files { dep -> dep.name == 'jta' }
             from parent.project( 'hibernate-core' ).configurations.runtime
             from parent.project( 'hibernate-core' ).configurations.archives.allArtifactFiles.filter{ file -> !file.name.endsWith('-sources.jar') }
             // for now, 
             from parent.project( 'hibernate-core' ).configurations.provided.files { dep -> dep.name == 'javassist' }
         }
 
 //        into('lib/bytecode/javassist') {
 //            from parent.project( 'hibernate-core' ).configurations.provided.files { dep -> dep.name == 'javassist' }
 //        }
 
         into( 'lib/jpa' ) {
             from parent.project( 'hibernate-entitymanager' ).configurations.archives.allArtifactFiles.filter{ file -> !file.name.endsWith('-sources.jar') }
         }
 
         into( 'lib/envers' ) {
             from(
                     ( parent.project( 'hibernate-envers' ).configurations.archives.allArtifactFiles.filter{ file -> !file.name.endsWith('-sources.jar') }
                             + parent.project( 'hibernate-envers' ).configurations.runtime )
                             - parent.project( 'hibernate-core' ).configurations.runtime
                             - parent.project( 'hibernate-core' ).configurations.archives.allArtifactFiles
                             - parent.project( 'hibernate-entitymanager' ).configurations.runtime
                             - parent.project( 'hibernate-entitymanager' ).configurations.archives.allArtifactFiles
             )
         }
 
         // todo : this closure is problematic as it does not write into the hibernate-release-$project.version directory
         // due to http://issues.gradle.org/browse/GRADLE-1450
         [ 'hibernate-c3p0', 'hibernate-proxool', 'hibernate-ehcache', 'hibernate-infinispan' ].each { feature ->
             final String shortName = feature.substring( 'hibernate-'.length() );
 // WORKAROUND http://issues.gradle.org/browse/GRADLE-1450
 //            into('lib/optional/' + shortName) {
             owner.into('lib/optional/' + shortName) {
                 from (
                         ( parent.project( feature ).configurations.archives.allArtifactFiles.filter{ file -> !file.name.endsWith('-sources.jar') }
                                 + parent.project( feature ).configurations.runtime )
                                 - parent.project( 'hibernate-core' ).configurations.runtime
                                 - parent.project( 'hibernate-core' ).configurations.archives.allArtifactFiles
                 )
             }
         }
 
         into('documentation') {
             from new File( parent.project( 'documentation' ).buildDir, 'docbook/publish' )
         }
 
         into('documentation/hem') {
             from new File( parent.project( 'hibernate-entitymanager' ).buildDir, 'docbook/publish' )
         }
 
         into('documentation/javadocs') {
             from javadocBuildDir.dir
         }
 
         into( 'project' ) {
             from ( rootProject.projectDir ) {
                 exclude( '.git' )
                 exclude( '.gitignore' )
                 exclude( 'changelog.txt' )
                 exclude( 'lgpl.txt' )
                 exclude( 'hibernate_logo.gif' )
                 exclude( 'tagRelease.sh' )
                 exclude( 'gradlew' )
                 exclude( 'gradlew.bat' )
                 exclude( 'wrapper/*' )
                 exclude( '**/.gradle/**' )
                 exclude( '**/target/**' )
                 exclude( '.idea' )
                 exclude( '**/*.ipr' )
                 exclude( '**/*.iml' )
                 exclude( '**/*.iws' )
                 exclude( '**/atlassian-ide-plugin.xml' )
                 exclude( '**/.classpath' )
                 exclude( '**/.project' )
                 exclude( '**/.settings' )
                 exclude( '**/.nbattrs' )
             }
         }
     }
 }
 
 task buildReleaseZip( type: Zip, dependsOn: [prepareReleaseBundles] ) {
     description = "Build release bundle in ZIP format"
     baseName = 'hibernate-release'
     destinationDir = releaseBuildDir.dir
     with project.releaseCopySpec
 }
 
 task buildReleaseTgz( type: Tar, dependsOn: [prepareReleaseBundles] ) {
     description = "Build release bundle in GZIP format"
     baseName = 'hibernate-release'
     destinationDir = releaseBuildDir.dir
     compression = Compression.GZIP
     with project.releaseCopySpec
 }
 
 task buildReleaseBundles( dependsOn: [buildReleaseZip,buildReleaseTgz] ) {
     description = "Build release bundle in all formats"
 }
diff --git a/release/src/javadoc/package.html b/release/src/javadoc/overview.html
similarity index 98%
rename from release/src/javadoc/package.html
rename to release/src/javadoc/overview.html
index 4b9c61e813..3e9dc3289d 100644
--- a/release/src/javadoc/package.html
+++ b/release/src/javadoc/overview.html
@@ -1,81 +1,82 @@
 <!--
   ~ Hibernate, Relational Persistence for Idiomatic Java
   ~
   ~ Copyright (c) 2010, Red Hat Inc. or third-party contributors as
   ~ indicated by the @author tags or express copyright attribution
   ~ statements applied by the authors.  All third-party contributions are
   ~ distributed under license by Red Hat Inc.
   ~
   ~ This copyrighted material is made available to anyone wishing to use, modify,
   ~ copy, or redistribute it subject to the terms and conditions of the GNU
   ~ Lesser General Public License, as published by the Free Software Foundation.
   ~
   ~ This program is distributed in the hope that it will be useful,
   ~ but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
   ~ or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
   ~ for more details.
   ~
   ~ You should have received a copy of the GNU Lesser General Public License
   ~ along with this distribution; if not, write to:
   ~ Free Software Foundation, Inc.
   ~ 51 Franklin Street, Fifth Floor
   ~ Boston, MA  02110-1301  USA
   -->
 <body>
 
 <h2>Aggregated Hibernate Core JavaDocs</h2>
 
 Hibernate provides both<ul>
     <li>
         a native API comprised centrally around {@link org.hibernate.SessionFactory} and {@link org.hibernate.Session}
     </li>
     <li>
         an implementation of the <a href="http://jcp.org/en/jsr/detail?id=317">JSR-317</a> Java Persistence API (JPA)
         specification comprised centrally around {@link org.hibernate.ejb.EntityManagerFactoryImpl} and
         {@link org.hibernate.ejb.EntityManagerImpl} (the Hibernate implementations of
         {@link javax.persistence.EntityManager} and{@link javax.persistence.EntityManagerFactory}, respectively)
     </li>
 </ul>
 <hr/>
 
 <h3>Native API</h3>
 In addition to SessionFactory and Session, applications using the native API will often utilize the following
 interfaces:<ul>
     <li>{@link org.hibernate.cfg.Configuration}</li>
     <li>{@link org.hibernate.Transaction}</li>
     <li>{@link org.hibernate.Query}</li>
     <li>{@link org.hibernate.Criteria}</li>
     <li>{@link org.hibernate.criterion.Projection}</li>
     <li>{@link org.hibernate.criterion.Projections}</li>
     <li>{@link org.hibernate.criterion.Criterion}</li>
     <li>{@link org.hibernate.criterion.Restrictions}</li>
     <li>{@link org.hibernate.criterion.Order}</li>
     <li>{@link org.hibernate.criterion.Example}</li>
 </ul>
 These interfaces are fully intended to be exposed to application code.
 <hr/>
 
 <h3>JPA</h3>
 The JPA interfaces are all defined by the JPA specification.  For details see {@link javax.persistence}
 <hr/>
 
 <h3>Package Groups</h3>
 This documentation groups packages into the following 3 categories:<ul>
     <li>
         <strong>API</strong> - classes to which application code will generally bind directly.
     </li>
     <li>
         <strong>SPI</strong> - classes to which application developers or integrators will commonly bind directly in
         order to develop extensions to Hibernate, or to alter its behavior in some way.
     </li>
     <li>
         <strong>Internal</strong> - classes which are intended only to be used by Hibernate.  Use of these classes
         outside of Hibernate specific use cases is not supported.  Moreover, these contracts can change frequently
         between releases whereas APIs and SPIs are more stable.
     </li>
 </ul>
+Additionally, we highlight a 4th category 
 <hr/>
 
 Complete Hibernate documentation may be found online at <a href="http://docs.jboss.org/hibernate/">http://docs.jboss.org/hibernate/</a>
 
 </body>
\ No newline at end of file
