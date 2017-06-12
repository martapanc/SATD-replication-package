diff --git a/hibernate-core/src/main/java/org/hibernate/annotations/QueryHints.java b/hibernate-core/src/main/java/org/hibernate/annotations/QueryHints.java
index 67f591969b..4032b88292 100644
--- a/hibernate-core/src/main/java/org/hibernate/annotations/QueryHints.java
+++ b/hibernate-core/src/main/java/org/hibernate/annotations/QueryHints.java
@@ -1,131 +1,140 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.annotations;
 
 import org.hibernate.engine.spi.QueryParameters;
 
 /**
  * Consolidation of hints available to Hibernate JPA queries.  Mainly used to define features available on
  * Hibernate queries that have no corollary in JPA queries.
  */
 public class QueryHints {
 	/**
 	 * Disallow instantiation.
 	 */
 	private QueryHints() {
 	}
 
 	/**
 	 * The cache mode to use.
 	 *
 	 * @see org.hibernate.Query#setCacheMode
 	 * @see org.hibernate.SQLQuery#setCacheMode
 	 */
 	public static final String CACHE_MODE = "org.hibernate.cacheMode";
 
 	/**
 	 * The cache region to use.
 	 *
 	 * @see org.hibernate.Query#setCacheRegion
 	 * @see org.hibernate.SQLQuery#setCacheRegion
 	 */
 	public static final String CACHE_REGION = "org.hibernate.cacheRegion";
 
 	/**
 	 * Are the query results cacheable?
 	 *
 	 * @see org.hibernate.Query#setCacheable
 	 * @see org.hibernate.SQLQuery#setCacheable
 	 */
 	public static final String CACHEABLE = "org.hibernate.cacheable";
 
 	/**
 	 * Is the query callable?  Note: only valid for named native sql queries.
 	 */
 	public static final String CALLABLE = "org.hibernate.callable";
 
 	/**
 	 * Defines a comment to be applied to the SQL sent to the database.
 	 *
 	 * @see org.hibernate.Query#setComment
 	 * @see org.hibernate.SQLQuery#setComment
 	 */
 	public static final String COMMENT = "org.hibernate.comment";
 
 	/**
 	 * Defines the JDBC fetch size to use.
 	 *
 	 * @see org.hibernate.Query#setFetchSize
 	 * @see org.hibernate.SQLQuery#setFetchSize
 	 */
 	public static final String FETCH_SIZE = "org.hibernate.fetchSize";
 
 	/**
 	 * The flush mode to associate with the execution of the query.
 	 *
 	 * @see org.hibernate.Query#setFlushMode
 	 * @see org.hibernate.SQLQuery#setFlushMode
 	 * @see org.hibernate.Session#setFlushMode
 	 */
 	public static final String FLUSH_MODE = "org.hibernate.flushMode";
 
 	/**
 	 * Should entities returned from the query be set in read only mode?
 	 *
 	 * @see org.hibernate.Query#setReadOnly
 	 * @see org.hibernate.SQLQuery#setReadOnly
 	 * @see org.hibernate.Session#setReadOnly
 	 */
 	public static final String READ_ONLY = "org.hibernate.readOnly";
 
 	/**
 	 * Apply a Hibernate query timeout, which is defined in <b>seconds</b>.
 	 *
 	 * @see org.hibernate.Query#setTimeout
 	 * @see org.hibernate.SQLQuery#setTimeout
 	 */
 	public static final String TIMEOUT_HIBERNATE = "org.hibernate.timeout";
 
 	/**
 	 * Apply a JPA query timeout, which is defined in <b>milliseconds</b>.
 	 */
 	public static final String TIMEOUT_JPA = "javax.persistence.query.timeout";
 
 	/**
 	 * Available to apply lock mode to a native SQL query since JPA requires that
 	 * {@link javax.persistence.Query#setLockMode} throw an IllegalStateException if called for a native query.
 	 * <p/>
 	 * Accepts a {@link javax.persistence.LockModeType} or a {@link org.hibernate.LockMode}
 	 */
 	public static final String NATIVE_LOCKMODE = "org.hibernate.lockMode";
 	
 	/**
 	 * Hint providing a "fetchgraph" EntityGraph.  Attributes explicitly specified as AttributeNodes are treated as
 	 * FetchType.EAGER (via join fetch or subsequent select).
 	 * 
 	 * Note: Currently, attributes that are not specified are treated as FetchType.LAZY or FetchType.EAGER depending
 	 * on the attribute's definition in metadata, rather than forcing FetchType.LAZY.
 	 */
 	public static final String FETCHGRAPH = "javax.persistence.fetchgraph";
 	
 	/**
 	 * Hint providing a "loadgraph" EntityGraph.  Attributes explicitly specified as AttributeNodes are treated as
 	 * FetchType.EAGER (via join fetch or subsequent select).  Attributes that are not specified are treated as
 	 * FetchType.LAZY or FetchType.EAGER depending on the attribute's definition in metadata
 	 */
 	public static final String LOADGRAPH = "javax.persistence.loadgraph";
 
 	/**
 	 * Hint to enable/disable the follow-on-locking mechanism provided by {@link org.hibernate.dialect.Dialect#useFollowOnLocking(QueryParameters)}.
 	 * A value of {@code true} enables follow-on-locking, whereas a value of {@code false} disables it.
 	 * If the value is {@code null}, the the {@code Dialect} strategy is going to be used instead.
 	 *
 	 * @since 5.2
 	 */
 	public static final String FOLLOW_ON_LOCKING = "hibernate.query.followOnLocking";
 
+	/**
+	 * Hint to enable/disable the pass-distinct-through mechanism.
+	 * A value of {@code true} enables pass-distinct-through, whereas a value of {@code false} disables it.
+	 * When the pass-distinct-through is disabled, the HQL and JPQL distinct clause is no longer passed to the SQL statement.
+	 *
+	 * @since 5.2
+	 */
+	public static final String PASS_DISTINCT_THROUGH = "hibernate.query.passDistinctThrough";
+
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java
index cb441868d5..9c1cae142a 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/QueryParameters.java
@@ -1,621 +1,638 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.engine.spi;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.StringTokenizer;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.QueryException;
 import org.hibernate.ScrollMode;
 import org.hibernate.engine.query.spi.HQLQueryPlan;
 import org.hibernate.hql.internal.classic.ParserHelper;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.FilterImpl;
 import org.hibernate.internal.util.EntityPrinter;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.query.internal.QueryParameterBindingsImpl;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 import org.jboss.logging.Logger;
 
 /**
  * @author Gavin King
  */
 public final class QueryParameters {
 	private static final Logger LOG = CoreLogging.logger( QueryParameters.class );
 
 	/**
 	 * Symbols used to split SQL string into tokens in {@link #processFilters(String, Map, SessionFactoryImplementor)}.
 	 */
 	private static final String SYMBOLS = ParserHelper.HQL_SEPARATORS.replace( "'", "" );
 
 	private Type[] positionalParameterTypes;
 	private Object[] positionalParameterValues;
 	private Map<String,TypedValue> namedParameters;
 	private LockOptions lockOptions;
 	private RowSelection rowSelection;
 	private boolean cacheable;
 	private String cacheRegion;
 	private String comment;
 	private List<String> queryHints;
 	private ScrollMode scrollMode;
 	private Serializable[] collectionKeys;
 	private Object optionalObject;
 	private String optionalEntityName;
 	private Serializable optionalId;
 	private boolean isReadOnlyInitialized;
 	private boolean readOnly;
 	private boolean callable;
 	private boolean autodiscovertypes;
 	private boolean isNaturalKeyLookup;
+	private boolean passDistinctThrough = true;
 
 	private final ResultTransformer resultTransformer; // why is all others non final ?
 
 	private String processedSQL;
 	private Type[] processedPositionalParameterTypes;
 	private Object[] processedPositionalParameterValues;
 	
 	private HQLQueryPlan queryPlan;
 
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
 		this( positionalParameterTypes, positionalParameterValues, null, null, false, false, false, null, null, null, false, null );
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
 			final List<String> queryHints,
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
 				queryHints,
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
 			final List<String> queryHints,
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
 		this.queryHints = queryHints;
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
 			final List<String> queryHints,
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
 				queryHints,
 				collectionKeys,
 				transformer
 		);
 		this.optionalEntityName = optionalEntityName;
 		this.optionalId = optionalId;
 		this.optionalObject = optionalObject;
 	}
 
 	public QueryParameters(
 			QueryParameterBindingsImpl queryParameterBindings,
 			LockOptions lockOptions,
 			RowSelection selection,
 			final boolean isReadOnlyInitialized,
 			boolean readOnly,
 			boolean cacheable,
 			String cacheRegion,
 			String comment,
 			List<String> dbHints,
 			final Serializable[] collectionKeys,
 			final Object optionalObject,
 			final String optionalEntityName,
 			final Serializable optionalId,
 			ResultTransformer resultTransformer) {
 		this(
 				queryParameterBindings.collectPositionalBindTypes(),
 				queryParameterBindings.collectPositionalBindValues(),
 				queryParameterBindings.collectNamedParameterBindings(),
 				lockOptions,
 				selection,
 				isReadOnlyInitialized,
 				readOnly,
 				cacheable,
 				cacheRegion,
 				comment,
 				dbHints,
 				collectionKeys,
 				optionalObject,
 				optionalEntityName,
 				optionalId,
 				resultTransformer
 		);
 
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
 		final int types = positionalParameterTypes == null ? 0 : positionalParameterTypes.length;
 		final int values = positionalParameterValues == null ? 0 : positionalParameterValues.length;
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
 	  
 	public List<String> getQueryHints() {
 		return queryHints;
 	}
 
 	public void setQueryHints(List<String> queryHints) {
 		this.queryHints = queryHints;
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
 	 * @see QueryParameters#isReadOnly(SharedSessionContractImplementor)
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
 	 * beforeQuery calling this method.
 	 *
 	 * @see QueryParameters#isReadOnlyInitialized()
 	 * @see QueryParameters#isReadOnly(SharedSessionContractImplementor)
 	 * @see QueryParameters#setReadOnly(boolean)
 	 *
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * query that existed in the session beforeQuery the query was executed.
 	 *
 	 * @return true, entities and proxies loaded by the Query will be put in read-only mode
 	 *         false, entities and proxies loaded by the Query will be put in modifiable mode
 	 * @throws IllegalStateException if the read-only/modifiable setting has not been
 	 * initialized (i.e., isReadOnlyInitialized() == false).
 	 */
 	public boolean isReadOnly() {
 		if ( !isReadOnlyInitialized() ) {
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
 	 * query that existed in the session beforeQuery the query was executed.
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
 	 * query that existed in the session beforeQuery the query was executed.
 	 *
 	 */
 	public boolean isReadOnly(SharedSessionContractImplementor session) {
 		return isReadOnlyInitialized
 				? isReadOnly()
 				: session.getPersistenceContext().isDefaultReadOnly();
 	}
 
 	/**
 	 * Set the read-only/modifiable mode for entities and proxies loaded by the query.
 	 * <p/>
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * query that existed in the session beforeQuery the query was executed.
 	 *
 	 * @param readOnly if {@code true}, entities and proxies loaded by the query will be put in read-only mode; if
 	 * {@code false}, entities and proxies loaded by the query will be put in modifiable mode
 	 *
 	 * @see QueryParameters#isReadOnlyInitialized()
 	 * @see QueryParameters#isReadOnly(SharedSessionContractImplementor)
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
 
+	/**
+	 * Check if this query should pass the {@code distinct} to the database.
+	 * @return the query passes {@code distinct} to the database
+	 */
+	public boolean isPassDistinctThrough() {
+		return passDistinctThrough;
+	}
+
+	/**
+	 * Set if this query should pass the {@code distinct} to the database.
+	 * @param passDistinctThrough the query passes {@code distinct} to the database
+	 */
+	public void setPassDistinctThrough(boolean passDistinctThrough) {
+		this.passDistinctThrough = passDistinctThrough;
+	}
+
 	public void processFilters(String sql, SharedSessionContractImplementor session) {
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
 			final StringTokenizer tokens = new StringTokenizer( sql, SYMBOLS, true );
 			StringBuilder result = new StringBuilder();
 			List parameters = new ArrayList();
 			List parameterTypes = new ArrayList();
 
 			int positionalIndex = 0;
 			while ( tokens.hasMoreTokens() ) {
 				final String token = tokens.nextToken();
 				if ( token.startsWith( ParserHelper.HQL_VARIABLE_PREFIX ) ) {
 					final String filterParameterName = token.substring( 1 );
 					final String[] parts = LoadQueryInfluencers.parseFilterParameterName( filterParameterName );
 					final FilterImpl filter = (FilterImpl) filters.get( parts[0] );
 					final Object value = filter.getParameter( parts[1] );
 					final Type type = filter.getFilterDefinition().getParameterType( parts[1] );
 					if ( value != null && Collection.class.isAssignableFrom( value.getClass() ) ) {
 						Iterator itr = ( (Collection) value ).iterator();
 						while ( itr.hasNext() ) {
 							final Object elementValue = itr.next();
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
 				this.queryHints,
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
 
 	public HQLQueryPlan getQueryPlan() {
 		return queryPlan;
 	}
 
 	public void setQueryPlan(HQLQueryPlan queryPlan) {
 		this.queryPlan = queryPlan;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/jpa/QueryHints.java b/hibernate-core/src/main/java/org/hibernate/jpa/QueryHints.java
index 96c537d12a..45832904d4 100644
--- a/hibernate-core/src/main/java/org/hibernate/jpa/QueryHints.java
+++ b/hibernate-core/src/main/java/org/hibernate/jpa/QueryHints.java
@@ -1,130 +1,133 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.jpa;
 
 import java.util.HashSet;
 import java.util.Set;
 
 import static org.hibernate.annotations.QueryHints.CACHEABLE;
 import static org.hibernate.annotations.QueryHints.CACHE_MODE;
 import static org.hibernate.annotations.QueryHints.CACHE_REGION;
 import static org.hibernate.annotations.QueryHints.COMMENT;
 import static org.hibernate.annotations.QueryHints.FETCHGRAPH;
 import static org.hibernate.annotations.QueryHints.FETCH_SIZE;
 import static org.hibernate.annotations.QueryHints.FLUSH_MODE;
 import static org.hibernate.annotations.QueryHints.FOLLOW_ON_LOCKING;
 import static org.hibernate.annotations.QueryHints.LOADGRAPH;
 import static org.hibernate.annotations.QueryHints.NATIVE_LOCKMODE;
+import static org.hibernate.annotations.QueryHints.PASS_DISTINCT_THROUGH;
 import static org.hibernate.annotations.QueryHints.READ_ONLY;
 import static org.hibernate.annotations.QueryHints.TIMEOUT_HIBERNATE;
 import static org.hibernate.annotations.QueryHints.TIMEOUT_JPA;
 
 /**
  * Defines the supported JPA query hints
  *
  * @author Steve Ebersole
  */
 public class QueryHints {
 	/**
 	 * The hint key for specifying a query timeout per Hibernate O/RM, which defines the timeout in seconds.
 	 *
 	 * @deprecated use {@link #SPEC_HINT_TIMEOUT} instead
 	 */
 	@Deprecated
 	public static final String HINT_TIMEOUT = TIMEOUT_HIBERNATE;
 
 	/**
 	 * The hint key for specifying a query timeout per JPA, which defines the timeout in milliseconds
 	 */
 	public static final String SPEC_HINT_TIMEOUT = TIMEOUT_JPA;
 
 	/**
 	 * The hint key for specifying a comment which is to be embedded into the SQL sent to the database.
 	 */
 	public static final String HINT_COMMENT = COMMENT;
 
 	/**
 	 * The hint key for specifying a JDBC fetch size, used when executing the resulting SQL.
 	 */
 	public static final String HINT_FETCH_SIZE = FETCH_SIZE;
 
 	/**
 	 * The hint key for specifying whether the query results should be cached for the next (cached) execution of the
 	 * "same query".
 	 */
 	public static final String HINT_CACHEABLE = CACHEABLE;
 
 	/**
 	 * The hint key for specifying the name of the cache region (within Hibernate's query result cache region)
 	 * to use for storing the query results.
 	 */
 	public static final String HINT_CACHE_REGION = CACHE_REGION;
 
 	/**
 	 * The hint key for specifying that objects loaded into the persistence context as a result of this query execution
 	 * should be associated with the persistence context as read-only.
 	 */
 	public static final String HINT_READONLY = READ_ONLY;
 
 	/**
 	 * The hint key for specifying the cache mode ({@link org.hibernate.CacheMode}) to be in effect for the
 	 * execution of the hinted query.
 	 */
 	public static final String HINT_CACHE_MODE = CACHE_MODE;
 
 	/**
 	 * The hint key for specifying the flush mode ({@link org.hibernate.FlushMode}) to be in effect for the
 	 * execution of the hinted query.
 	 */
 	public static final String HINT_FLUSH_MODE = FLUSH_MODE;
 
 	public static final String HINT_NATIVE_LOCKMODE = NATIVE_LOCKMODE;
 	
 	/**
 	 * Hint providing a "fetchgraph" EntityGraph.  Attributes explicitly specified as AttributeNodes are treated as
 	 * FetchType.EAGER (via join fetch or subsequent select).
 	 * 
 	 * Note: Currently, attributes that are not specified are treated as FetchType.LAZY or FetchType.EAGER depending
 	 * on the attribute's definition in metadata, rather than forcing FetchType.LAZY.
 	 */
 	public static final String HINT_FETCHGRAPH = FETCHGRAPH;
 	
 	/**
 	 * Hint providing a "loadgraph" EntityGraph.  Attributes explicitly specified as AttributeNodes are treated as
 	 * FetchType.EAGER (via join fetch or subsequent select).  Attributes that are not specified are treated as
 	 * FetchType.LAZY or FetchType.EAGER depending on the attribute's definition in metadata
 	 */
 	public static final String HINT_LOADGRAPH = LOADGRAPH;
 
 	public static final String HINT_FOLLOW_ON_LOCKING = FOLLOW_ON_LOCKING;
 
+	public static final String HINT_PASS_DISTINCT_THROUGH = PASS_DISTINCT_THROUGH;
+
 	private static final Set<String> HINTS = buildHintsSet();
 
 	private static Set<String> buildHintsSet() {
 		HashSet<String> hints = new HashSet<String>();
 		hints.add( HINT_TIMEOUT );
 		hints.add( SPEC_HINT_TIMEOUT );
 		hints.add( HINT_COMMENT );
 		hints.add( HINT_FETCH_SIZE );
 		hints.add( HINT_CACHE_REGION );
 		hints.add( HINT_CACHEABLE );
 		hints.add( HINT_READONLY );
 		hints.add( HINT_CACHE_MODE );
 		hints.add( HINT_FLUSH_MODE );
 		hints.add( HINT_NATIVE_LOCKMODE );
 		hints.add( HINT_FETCHGRAPH );
 		hints.add( HINT_LOADGRAPH );
 		return java.util.Collections.unmodifiableSet( hints );
 	}
 
 	public static Set<String> getDefinedHints() {
 		return HINTS;
 	}
 
 	protected QueryHints() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 9ac4ec113f..4ef9309292 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,1241 +1,1247 @@
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
 import org.hibernate.query.spi.ScrollableResultsImplementor;
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
+
+	public static final String SELECT = "select";
+	public static final String SELECT_DISTINCT = "select distinct";
+
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
 
+		sql = processDistinctKeyword( sql, parameters);
+
 		return getFactory().getSessionFactoryOptions().isCommentsEnabled()
 				? prependComment( sql, parameters )
 				: sql;
 	}
 
 	protected boolean shouldUseFollowOnLocking(
 			QueryParameters parameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) {
 		if ( ( parameters.getLockOptions().getFollowOnLocking() == null && dialect.useFollowOnLocking( parameters ) ) ||
 				( parameters.getLockOptions().getFollowOnLocking() != null && parameters.getLockOptions().getFollowOnLocking() ) ) {
 			// currently only one lock mode is allowed in follow-on locking
 			final LockMode lockMode = determineFollowOnLockMode( parameters.getLockOptions() );
 			final LockOptions lockOptions = new LockOptions( lockMode );
 			if ( lockOptions.getLockMode() != LockMode.UPGRADE_SKIPLOCKED ) {
 				if ( lockOptions.getLockMode() != LockMode.NONE ) {
 					LOG.usingFollowOnLocking();
 				}
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
 			if ( lockOptions.getLockMode() == LockMode.NONE && lockModeToUse == LockMode.NONE ) {
 				return lockModeToUse;
 			}
 			else {
 				LOG.aliasSpecificLockingWithFollowOnLocking( lockModeToUse );
 			}
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
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
 			session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release( st );
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
 		else {
 			return null;
 		}
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SharedSessionContractImplementor session,
 			final boolean readOnly) throws HibernateException {
 		initializeEntitiesAndCollections(
 				hydratedObjects,
 				resultSetId,
 				session,
 				readOnly,
 				Collections.emptyList()
 		);
 	}
 
 	private void initializeEntitiesAndCollections(
 			final List hydratedObjects,
 			final Object resultSetId,
 			final SharedSessionContractImplementor session,
 			final boolean readOnly,
 			List<AfterLoadAction> afterLoadActions) throws HibernateException {
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 			for ( CollectionPersister collectionPersister : collectionPersisters ) {
 				if ( collectionPersister.isArray() ) {
 					//for arrays, we should end the collection load beforeQuery resolving
 					//the entities, since the actual array instances are not instantiated
 					//during loading
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersister );
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
 
 		if ( hydratedObjects != null ) {
 			int hydratedObjectsSize = hydratedObjects.size();
 			LOG.tracev( "Total objects hydrated: {0}", hydratedObjectsSize );
 			for ( Object hydratedObject : hydratedObjects ) {
 				TwoPhaseLoad.initializeEntity( hydratedObject, readOnly, session, pre );
 			}
 		}
 
 		if ( collectionPersisters != null ) {
 			for ( CollectionPersister collectionPersister : collectionPersisters ) {
 				if ( !collectionPersister.isArray() ) {
 					//for sets, we should end the collection load afterQuery resolving
 					//the entities, since we might call hashCode() on the elements
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersister );
 				}
 			}
 		}
 
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur afterQuery
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
 							throw new HibernateException(
 									"Could not locate EntityEntry immediately afterQuery two-phase load"
 							);
 						}
 						afterLoadAction.afterLoad( session, hydratedObject, (Loadable) entityEntry.getPersister() );
 					}
 				}
 			}
 		}
 	}
 
 	private void endCollectionLoad(
 			final Object resultSetId,
 			final SharedSessionContractImplementor session,
 			final CollectionPersister collectionPersister) {
 		//this is a query and we are loading multiple instances of the same collection role
 		session.getPersistenceContext()
 				.getLoadContexts()
 				.getCollectionLoadContext( (ResultSet) resultSetId )
 				.endLoadingCollections( collectionPersister );
 	}
 
 	/**
 	 * Determine the actual ResultTransformer that will be used to
 	 * transform query results.
 	 *
 	 * @param resultTransformer the specified result transformer
 	 *
 	 * @return the actual result transformer
 	 */
 	protected ResultTransformer resolveResultTransformer(ResultTransformer resultTransformer) {
 		return resultTransformer;
 	}
 
 	protected List getResultList(List results, ResultTransformer resultTransformer) throws QueryException {
 		return results;
 	}
 
 	/**
 	 * Are rows transformed immediately afterQuery being read from the ResultSet?
 	 *
 	 * @return true, if getResultColumnOrRow() transforms the results; false, otherwise
 	 */
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return false;
 	}
 
 	/**
 	 * Returns the aliases that corresponding to a result row.
 	 *
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
 			SharedSessionContractImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(
@@ -1789,1001 +1795,1018 @@ public abstract class Loader {
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
 		catch (SQLException | HibernateException e) {
 			session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 			throw e;
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
 		catch (SQLException | HibernateException e) {
 			session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 			throw e;
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
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
 	protected ScrollableResultsImplementor scroll(
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
 			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
+
+	/**
+	 * Remove distinct keyword from SQL statement if the query should not pass it through.
+	 * @param sql SQL string
+	 * @param parameters SQL parameters
+	 * @return SQL string
+	 */
+	protected String processDistinctKeyword(
+			String sql,
+			QueryParameters parameters) {
+		if ( !parameters.isPassDistinctThrough() ) {
+			if ( sql.startsWith( SELECT_DISTINCT ) ) {
+				return SELECT + sql.substring( SELECT_DISTINCT.length() );
+			}
+		}
+		return sql;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/query/internal/AbstractProducedQuery.java b/hibernate-core/src/main/java/org/hibernate/query/internal/AbstractProducedQuery.java
index eafe913233..fdc3b2b7ce 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/internal/AbstractProducedQuery.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/internal/AbstractProducedQuery.java
@@ -1,1543 +1,1561 @@
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
 import java.util.Optional;
 import java.util.Set;
 import java.util.Spliterator;
 import java.util.Spliterators;
 import java.util.stream.Stream;
 import java.util.stream.StreamSupport;
 import javax.persistence.CacheRetrieveMode;
 import javax.persistence.CacheStoreMode;
 import javax.persistence.FlushModeType;
 import javax.persistence.LockModeType;
 import javax.persistence.NoResultException;
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
 import org.hibernate.QueryParameterException;
 import org.hibernate.ScrollMode;
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
 import org.hibernate.query.spi.ScrollableResultsImplementor;
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
 import static org.hibernate.jpa.QueryHints.HINT_FOLLOW_ON_LOCKING;
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
 
+	private Boolean passDistinctThrough;
+
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
 		this.lockOptions.setFollowOnLocking( lockOptions.getFollowOnLocking() );
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
 			final TypedParameterValue  typedValueWrapper = (TypedParameterValue) value;
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
 		return getParameterMetadata().collectAllParametersJpa();
 	}
 
 	@Override
 	public Parameter<?> getParameter(String name) {
 		try {
 			return getParameterMetadata().getQueryParameter( name );
 		}
 		catch ( HibernateException e ) {
 			throw getExceptionConverter().convert( e );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> Parameter<T> getParameter(String name, Class<T> type) {
 		try {
 			final QueryParameter parameter = getParameterMetadata().getQueryParameter( name );
 			if ( !parameter.getParameterType().isAssignableFrom( type ) ) {
 				throw new IllegalArgumentException(
 						"The type [" + parameter.getParameterType().getName() +
 								"] associated with the parameter corresponding to name [" + name +
 								"] is not assignable to requested Java type [" + type.getName() + "]"
 				);
 			}
 			return parameter;
 		}
 		catch ( HibernateException e ) {
 			throw getExceptionConverter().convert( e );
 		}
 	}
 
 	@Override
 	public Parameter<?> getParameter(int position) {
 		// It is important to understand that there are 2 completely distinct conceptualization of
 		// "positional parameters" in play here:
 		//		1) The legacy Hibernate concept is akin to JDBC PreparedStatement parameters.  Very limited and
 		//			deprecated since 5.x.  These are numbered starting from 0 and kept in the
 		//			ParameterMetadata positional-parameter array keyed by this zero-based position
 		//		2) JPA's definition is really just a named parameter, but expected to explicitly be
 		//			sequential integers starting from 1 (ONE); they can repeat.
 		//
 		// It is considered illegal to mix positional-parameter with named parameters of any kind.  So therefore.
 		// if ParameterMetadata reports that it has any positional-parameters it is talking about the
 		// legacy Hibernate concept.
 		// lookup jpa-based positional parameters first by name.
 		try {
 			if ( getParameterMetadata().getPositionalParameterCount() == 0 ) {
 				try {
 					return getParameterMetadata().getQueryParameter( Integer.toString( position ) );
 				}
 				catch (HibernateException e) {
 					throw new QueryParameterException( "could not locate parameter at position [" + position + "]" );
 				}
 			}
 			// fallback to ordinal lookup
 			return getParameterMetadata().getQueryParameter( position );
 		}
 		catch (HibernateException e) {
 			throw getExceptionConverter().convert( e );
 		}
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public <T> Parameter<T> getParameter(int position, Class<T> type) {
 		try {
 			final QueryParameter parameter = getParameterMetadata().getQueryParameter( position );
 			if ( !parameter.getParameterType().isAssignableFrom( type ) ) {
 				throw new IllegalArgumentException(
 						"The type [" + parameter.getParameterType().getName() +
 								"] associated with the parameter corresponding to position [" + position +
 								"] is not assignable to requested Java type [" + type.getName() + "]"
 				);
 			}
 			return parameter;
 		}
 		catch ( HibernateException e ) {
 			throw getExceptionConverter().convert( e );
 		}
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
 			type = getParameterMetadata().getQueryParameter( namedParam ).getType();
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
 				if ( map.containsKey( paramName ) ) {
 					setParameter( paramName, null, determineType( paramName, null ) );
 				}
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
 			else if ( HINT_FOLLOW_ON_LOCKING.equals( hintName ) ) {
 				applied = applyFollowOnLockingHint( ConfigurationHelper.getBoolean( value ) );
 			}
+			else if ( QueryHints.HINT_PASS_DISTINCT_THROUGH.equals( hintName ) ) {
+				applied = applyPassDistinctThrough( ConfigurationHelper.getBoolean( value ) );
+			}
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
 	 * Apply the follow-on-locking hint.
 	 *
 	 * @param followOnLocking The follow-on-locking strategy.
 	 */
 	protected boolean applyFollowOnLockingHint(Boolean followOnLocking) {
 		getLockOptions().setFollowOnLocking( followOnLocking );
 		return true;
 	}
 
 	/**
+	 * Apply the follow-on-locking hint.
+	 *
+	 * @param passDistinctThrough the query passes {@code distinct} to the database
+	 */
+	protected boolean applyPassDistinctThrough(boolean passDistinctThrough) {
+		this.passDistinctThrough = passDistinctThrough;
+		return true;
+	}
+
+	/**
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
+		if ( passDistinctThrough != null ) {
+			queryParameters.setPassDistinctThrough( passDistinctThrough );
+		}
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
 	public ScrollableResultsImplementor scroll() {
 		return scroll( getProducer().getJdbcServices().getJdbcEnvironment().getDialect().defaultScrollMode() );
 	}
 
 	@Override
 	public ScrollableResultsImplementor scroll(ScrollMode scrollMode) {
 		beforeQuery();
 		try {
 			return doScroll( scrollMode );
 		}
 		finally {
 			afterQuery();
 		}
 	}
 
 	protected ScrollableResultsImplementor doScroll(ScrollMode scrollMode) {
 		final String query = queryParameterBindings.expandListValuedParameters( getQueryString(), getProducer() );
 		QueryParameters queryParameters = getQueryParameters();
 		queryParameters.setScrollMode( scrollMode );
 		return getProducer().scroll( query, queryParameters );
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public Stream<R> stream() {
 		final ScrollableResultsImplementor scrollableResults = scroll( ScrollMode.FORWARD_ONLY );
 		final ScrollableResultsIterator<R> iterator = new ScrollableResultsIterator<>( scrollableResults );
 		final Spliterator<R> spliterator = Spliterators.spliteratorUnknownSize( iterator, Spliterator.NONNULL );
 
 		final Stream<R> stream = StreamSupport.stream( spliterator, false );
 		stream.onClose( scrollableResults::close );
 
 		return stream;
 	}
 
 	@Override
 	public Optional<R> uniqueResultOptional() {
 		return Optional.ofNullable( uniqueResult() );
 	}
 
 	@Override
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
 
 	@Override
 	public R getSingleResult() {
 		try {
 			final List<R> list = list();
 			if ( list.size() == 0 ) {
 				throw new NoResultException( "No entity found for query" );
 			}
 			return uniqueElement( list );
 		}
 		catch ( HibernateException e ) {
 			if ( getProducer().getFactory().getSessionFactoryOptions().isJpaBootstrap() ) {
 				throw getExceptionConverter().convert( e );
 			}
 			else {
 				throw e;
 			}
 		}
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
diff --git a/hibernate-core/src/test/java/org/hibernate/test/distinct/SelectDistinctHqlTest.java b/hibernate-core/src/test/java/org/hibernate/test/distinct/SelectDistinctHqlTest.java
new file mode 100644
index 0000000000..9706d719cd
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/distinct/SelectDistinctHqlTest.java
@@ -0,0 +1,143 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.distinct;
+
+import java.util.ArrayList;
+import java.util.List;
+import javax.persistence.CascadeType;
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.Id;
+import javax.persistence.ManyToOne;
+import javax.persistence.OneToMany;
+
+import org.hibernate.boot.SessionFactoryBuilder;
+import org.hibernate.jpa.QueryHints;
+
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
+import org.hibernate.test.util.jdbc.SQLStatementInterceptor;
+import org.junit.Test;
+
+import static org.hibernate.testing.transaction.TransactionUtil.doInHibernate;
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertFalse;
+import static org.junit.Assert.assertTrue;
+
+/**
+ * @author Vlad Mihalcea
+ */
+@TestForIssue( jiraKey = "HHH-10965" )
+public class SelectDistinctHqlTest extends BaseNonConfigCoreFunctionalTestCase {
+
+	private SQLStatementInterceptor sqlStatementInterceptor;
+
+	@Override
+	protected void configureSessionFactoryBuilder(SessionFactoryBuilder sfb) {
+		sqlStatementInterceptor = new SQLStatementInterceptor( sfb );
+	}
+
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class<?>[] {
+			Person.class,
+			Phone.class,
+		};
+	}
+
+	@Test
+	public void test() {
+		doInHibernate( this::sessionFactory, session -> {
+			Person person = new Person();
+			person.id = 1L;
+			session.persist( person );
+
+			person.addPhone( new Phone( "027-123-4567" ) );
+			person.addPhone( new Phone( "028-234-9876" ) );
+		} );
+
+		doInHibernate( this::sessionFactory, session -> {
+			sqlStatementInterceptor.getSqlQueries().clear();
+			List<Person> persons = session.createQuery(
+				"select distinct p from Person p" )
+			.getResultList();
+			String sqlQuery = sqlStatementInterceptor.getSqlQueries().getLast();
+			assertTrue( sqlQuery.contains( " distinct " ) );
+		} );
+
+		doInHibernate( this::sessionFactory, session -> {
+			sqlStatementInterceptor.getSqlQueries().clear();
+			List<Person> persons = session.createQuery(
+				"select distinct p from Person p" )
+			.setHint( QueryHints.HINT_PASS_DISTINCT_THROUGH, false )
+			.getResultList();
+			String sqlQuery = sqlStatementInterceptor.getSqlQueries().getLast();
+			assertFalse( sqlQuery.contains( " distinct " ) );
+		} );
+
+		doInHibernate( this::sessionFactory, session -> {
+			List<Person> persons = session.createQuery(
+				"select p from Person p left join fetch p.phones " )
+			.getResultList();
+			assertEquals(2, persons.size());
+		} );
+
+		doInHibernate( this::sessionFactory, session -> {
+			sqlStatementInterceptor.getSqlQueries().clear();
+			List<Person> persons = session.createQuery(
+				"select distinct p from Person p left join fetch p.phones " )
+			.getResultList();
+			assertEquals(1, persons.size());
+			String sqlQuery = sqlStatementInterceptor.getSqlQueries().getLast();
+			assertTrue( sqlQuery.contains( " distinct " ) );
+		} );
+
+		doInHibernate( this::sessionFactory, session -> {
+			sqlStatementInterceptor.getSqlQueries().clear();
+			List<Person> persons = session.createQuery(
+				"select distinct p from Person p left join fetch p.phones " )
+			.setHint( QueryHints.HINT_PASS_DISTINCT_THROUGH, false )
+			.getResultList();
+			assertEquals(1, persons.size());
+			String sqlQuery = sqlStatementInterceptor.getSqlQueries().getLast();
+			assertFalse( sqlQuery.contains( " distinct " ) );
+		} );
+	}
+
+	@Entity(name = "Person")
+	public static class Person {
+
+		@Id
+		private Long id;
+
+		@OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
+		private List<Phone> phones = new ArrayList<>();
+
+		public void addPhone(Phone phone) {
+			phones.add( phone );
+			phone.person = this;
+		}
+	}
+
+	@Entity(name = "Phone")
+	public static class Phone {
+
+		@Id
+		@Column(name = "`number`")
+		private String number;
+
+		@ManyToOne
+		private Person person;
+
+		public Phone() {
+		}
+
+		public Phone(String number) {
+			this.number = number;
+		}
+	}
+}
