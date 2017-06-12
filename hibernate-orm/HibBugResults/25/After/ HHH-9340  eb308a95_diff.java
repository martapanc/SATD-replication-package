diff --git a/hibernate-core/src/main/java/org/hibernate/BasicQueryContract.java b/hibernate-core/src/main/java/org/hibernate/BasicQueryContract.java
index 374bfb07c3..63685e544d 100644
--- a/hibernate-core/src/main/java/org/hibernate/BasicQueryContract.java
+++ b/hibernate-core/src/main/java/org/hibernate/BasicQueryContract.java
@@ -1,237 +1,238 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate;
 
+import org.hibernate.query.CommonQueryContract;
 import org.hibernate.type.Type;
 
 /**
  * Defines the aspects of query definition that apply to all forms of querying.
  *
  * @author Steve Ebersole
  *
- * @deprecated (since 5.2) use {@link org.hibernate.query.BasicQueryContract} instead.
+ * @deprecated (since 5.2) use {@link CommonQueryContract} instead.
  */
 @Deprecated
 public interface BasicQueryContract {
 	/**
 	 * (Re)set the current FlushMode in effect for this query.
 	 *
 	 * @param flushMode The new FlushMode to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see #getHibernateFlushMode()
 	 *
 	 * @deprecated (since 5.2) use {@link #setHibernateFlushMode} instead
 	 */
 	@Deprecated
-	default org.hibernate.query.BasicQueryContract setFlushMode(FlushMode flushMode) {
+	default CommonQueryContract setFlushMode(FlushMode flushMode) {
 		setHibernateFlushMode( flushMode );
-		return (org.hibernate.query.BasicQueryContract) this;
+		return (CommonQueryContract) this;
 	}
 
 	/**
 	 * Obtain the FlushMode in effect for this query.  By default, the query inherits the FlushMode of the Session
 	 * from which it originates.
 	 *
 	 * @return The query FlushMode.
 	 *
 	 * @see Session#getFlushMode()
 	 * @see FlushMode
 	 */
 	FlushMode getHibernateFlushMode();
 
 	/**
 	 * (Re)set the current FlushMode in effect for this query.
 	 *
 	 * @param flushMode The new FlushMode to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see #getHibernateFlushMode()
 	 */
-	org.hibernate.query.BasicQueryContract setHibernateFlushMode(FlushMode flushMode);
+	CommonQueryContract setHibernateFlushMode(FlushMode flushMode);
 
 	/**
 	 * Obtain the CacheMode in effect for this query.  By default, the query inherits the CacheMode of the Session
 	 * from which is originates.
 	 *
 	 * NOTE: The CacheMode here only effects reading/writing of the query cache, not the
 	 * entity/collection caches.
 	 *
 	 * @return The query CacheMode.
 	 *
 	 * @see Session#getCacheMode()
 	 * @see CacheMode
 	 */
 	CacheMode getCacheMode();
 
 	/**
 	 * (Re)set the current CacheMode in effect for this query.
 	 *
 	 * @param cacheMode The new CacheMode to use.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see #getCacheMode()
 	 */
-	org.hibernate.query.BasicQueryContract setCacheMode(CacheMode cacheMode);
+	CommonQueryContract setCacheMode(CacheMode cacheMode);
 
 	/**
 	 * Are the results of this query eligible for second level query caching?  This is different that second level
 	 * caching of any returned entities and collections.
 	 *
 	 * NOTE: the query being "eligible" for caching does not necessarily mean its results will be cached.  Second level
 	 * query caching still has to be enabled on the {@link SessionFactory} for this to happen.  Usually that is
 	 * controlled by the {@code hibernate.cache.use_query_cache} configuration setting.
 	 *
 	 * @return {@code true} if the query results are eligible for caching, {@code false} otherwise.
 	 *
 	 * @see org.hibernate.cfg.AvailableSettings#USE_QUERY_CACHE
 	 */
 	boolean isCacheable();
 
 	/**
 	 * Enable/disable second level query (result) caching for this query.
 	 *
 	 * @param cacheable Should the query results be cacheable?
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see #isCacheable
 	 */
-	org.hibernate.query.BasicQueryContract setCacheable(boolean cacheable);
+	CommonQueryContract setCacheable(boolean cacheable);
 
 	/**
 	 * Obtain the name of the second level query cache region in which query results will be stored (if they are
 	 * cached, see the discussion on {@link #isCacheable()} for more information).  {@code null} indicates that the
 	 * default region should be used.
 	 *
 	 * @return The specified cache region name into which query results should be placed; {@code null} indicates
 	 * the default region.
 	 */
 	String getCacheRegion();
 
 	/**
 	 * Set the name of the cache region where query results should be cached (if cached at all).
 	 *
 	 * @param cacheRegion the name of a query cache region, or {@code null} to indicate that the default region
 	 * should be used.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see #getCacheRegion()
 	 */
-	org.hibernate.query.BasicQueryContract setCacheRegion(String cacheRegion);
+	CommonQueryContract setCacheRegion(String cacheRegion);
 
 	/**
 	 * Obtain the query timeout <b>in seconds</b>.  This value is eventually passed along to the JDBC query via
 	 * {@link java.sql.Statement#setQueryTimeout(int)}.  Zero indicates no timeout.
 	 *
 	 * @return The timeout <b>in seconds</b>
 	 *
 	 * @see java.sql.Statement#getQueryTimeout()
 	 * @see java.sql.Statement#setQueryTimeout(int)
 	 */
 	Integer getTimeout();
 
 	/**
 	 * Set the query timeout <b>in seconds</b>.
 	 *
 	 * NOTE it is important to understand that any value set here is eventually passed directly through to the JDBC
 	 * Statement which expressly disallows negative values.  So negative values should be avoided as a general rule.
 	 *
 	 * @param timeout the timeout <b>in seconds</b>
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see #getTimeout()
 	 */
-	org.hibernate.query.BasicQueryContract setTimeout(int timeout);
+	CommonQueryContract setTimeout(int timeout);
 
 	/**
 	 * Obtain the JDBC fetch size hint in effect for this query.  This value is eventually passed along to the JDBC
 	 * query via {@link java.sql.Statement#setFetchSize(int)}.  As defined b y JDBC, this value is a hint to the
 	 * driver to indicate how many rows to fetch from the database when more rows are needed.
 	 *
 	 * NOTE : JDBC expressly defines this value as a hint.  It may or may not have any effect on the actual
 	 * query execution and ResultSet processing depending on the driver.
 	 *
 	 * @return The timeout <b>in seconds</b>
 	 *
 	 * @see java.sql.Statement#getFetchSize()
 	 * @see java.sql.Statement#setFetchSize(int)
 	 */
 	Integer getFetchSize();
 
 	/**
 	 * Sets a JDBC fetch size hint for the query.
 	 *
 	 * @param fetchSize the fetch size hint
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @see #getFetchSize()
 	 */
-	org.hibernate.query.BasicQueryContract setFetchSize(int fetchSize);
+	CommonQueryContract setFetchSize(int fetchSize);
 
 	/**
 	 * Should entities and proxies loaded by this Query be put in read-only mode? If the
 	 * read-only/modifiable setting was not initialized, then the default
 	 * read-only/modifiable setting for the persistence context is returned instead.
 	 *
 	 * @see #setReadOnly(boolean)
 	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
 	 *
 	 * The read-only/modifiable setting has no impact on entities/proxies returned by the
 	 * query that existed in the session beforeQuery the query was executed.
 	 *
 	 * @return {@code true} if the entities and proxies loaded by the query will be put
 	 * in read-only mode; {@code false} otherwise (they will be modifiable)
 	 */
 	boolean isReadOnly();
 
 	/**
 	 * Set the read-only/modifiable mode for entities and proxies
 	 * loaded by this Query. This setting overrides the default setting
 	 * for the persistence context.
 	 * @see org.hibernate.engine.spi.PersistenceContext#isDefaultReadOnly()
 	 *
 	 * To set the default read-only/modifiable setting used for
 	 * entities and proxies that are loaded into the session:
 	 * @see org.hibernate.engine.spi.PersistenceContext#setDefaultReadOnly(boolean)
 	 * @see Session#setDefaultReadOnly(boolean)
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
 	 * returned by the query that existed in the session beforeQuery the query was executed.
 	 *
 	 * @return {@code this}, for method chaining
 	 *
 	 * @param readOnly {@code true} indicates that entities and proxies loaded by the query
 	 * are to be put in read-only mode; {@code false} indicates that entities and proxies
 	 * loaded by the query will be put in modifiable mode
 	 */
-	org.hibernate.query.BasicQueryContract setReadOnly(boolean readOnly);
+	CommonQueryContract setReadOnly(boolean readOnly);
 
 	/**
 	 * Return the Hibernate types of the query results.
 	 *
 	 * @return an array of types
 	 *
 	 * @deprecated (since 5.2) with no replacement; to be removed in 6.0
 	 */
 	@Deprecated
 	Type[] getReturnTypes();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/HQLQueryPlan.java b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/HQLQueryPlan.java
index 4b758f40e8..113de2d970 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/query/spi/HQLQueryPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/query/spi/HQLQueryPlan.java
@@ -1,442 +1,441 @@
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
-import org.hibernate.ScrollableResults;
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
 import org.hibernate.query.spi.ScrollableResultsImplementor;
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
 	public ScrollableResultsImplementor performScroll(
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/spi/SharedSessionContractImplementor.java b/hibernate-core/src/main/java/org/hibernate/engine/spi/SharedSessionContractImplementor.java
index 81e03d7339..b6d3756bb1 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/spi/SharedSessionContractImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/spi/SharedSessionContractImplementor.java
@@ -1,413 +1,411 @@
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
-import org.hibernate.ScrollableResults;
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
 import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.resource.jdbc.spi.JdbcSessionOwner;
 import org.hibernate.resource.transaction.spi.TransactionCoordinator;
-import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
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
- *         {@link TransactionCoordinatorBuilder#buildTransactionCoordinator}
+ *         {@link org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder#buildTransactionCoordinator}
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
 	ScrollableResultsImplementor scroll(String query, QueryParameters queryParameters) throws HibernateException;
 
 	/**
 	 * Execute a criteria query
 	 */
 	ScrollableResultsImplementor scroll(Criteria criteria, ScrollMode scrollMode);
 
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
 	ScrollableResultsImplementor scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
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
 	ScrollableResultsImplementor scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters);
 
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
index 8442a5f94e..dbb9a56a65 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/ast/QueryTranslatorImpl.java
@@ -1,632 +1,631 @@
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
-import org.hibernate.ScrollableResults;
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
 import org.hibernate.query.spi.ScrollableResultsImplementor;
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
 	public ScrollableResultsImplementor scroll(QueryParameters queryParameters, SharedSessionContractImplementor session)
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
index a02d09af9e..ed6562baea 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
@@ -1,1032 +1,1031 @@
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
-import org.hibernate.ScrollableResults;
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
 import org.hibernate.query.spi.ScrollableResultsImplementor;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/hql/spi/QueryTranslator.java b/hibernate-core/src/main/java/org/hibernate/hql/spi/QueryTranslator.java
index 679479ae70..1095e90f87 100644
--- a/hibernate-core/src/main/java/org/hibernate/hql/spi/QueryTranslator.java
+++ b/hibernate-core/src/main/java/org/hibernate/hql/spi/QueryTranslator.java
@@ -1,174 +1,173 @@
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
-import org.hibernate.ScrollableResults;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.query.spi.ScrollableResultsImplementor;
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
 	ScrollableResultsImplementor scroll(QueryParameters queryParameters, SharedSessionContractImplementor session)
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSharedSessionContract.java b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSharedSessionContract.java
index b301e580fa..7ae0ee5d29 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/AbstractSharedSessionContract.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/AbstractSharedSessionContract.java
@@ -1,1015 +1,1014 @@
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
-import org.hibernate.ScrollableResults;
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
 import org.hibernate.query.spi.ScrollableResultsImplementor;
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
 	public ScrollableResultsImplementor scroll(NativeSQLQuerySpecification spec, QueryParameters queryParameters) {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
index f109b9236c..12baa68b62 100755
--- a/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/StatelessSessionImpl.java
@@ -1,673 +1,672 @@
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
-import org.hibernate.ScrollableResults;
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
 import org.hibernate.query.spi.ScrollableResultsImplementor;
 
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
-		Object result = getFactory().getEntityPersister( entityName )
+		Object result = getFactory().getMetamodel().entityPersister( entityName )
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
-		return getFactory().getEntityPersister( entityName ).instantiate( id, this );
+		return getFactory().getMetamodel().entityPersister( entityName ).instantiate( id, this );
 	}
 
 	@Override
 	public Object internalLoad(
 			String entityName,
 			Serializable id,
 			boolean eager,
 			boolean nullable) throws HibernateException {
 		checkOpen();
-		EntityPersister persister = getFactory().getEntityPersister( entityName );
+		EntityPersister persister = getFactory().getMetamodel().entityPersister( entityName );
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
 	public ScrollableResultsImplementor scroll(Criteria criteria, ScrollMode scrollMode) {
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
 	public ScrollableResultsImplementor scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters)
 			throws HibernateException {
 		checkOpen();
 		CustomLoader loader = new CustomLoader( customQuery, getFactory() );
 		return loader.scroll( queryParameters, this );
 	}
 
 	@Override
 	public ScrollableResultsImplementor scroll(String query, QueryParameters queryParameters) throws HibernateException {
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
index e65a135ef8..01f2125e49 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1,2793 +1,2782 @@
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
-import org.hibernate.ScrollableResults;
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
-			throw factory.getSQLExceptionHelper().convert(
+			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
-			throw factory.getSQLExceptionHelper().convert(
+			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
-			throw factory.getSQLExceptionHelper().convert(
+			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
-			throw factory.getSQLExceptionHelper().convert(
+			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
-			session.getJdbcCoordinator().getResourceRegistry().release( st );
+			session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release( st );
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
 			Object[] row,
 			ResultSet rs,
 			SharedSessionContractImplementor session) throws SQLException, HibernateException {
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
 			final SharedSessionContractImplementor session) {
 
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
 						boolean isOneToOneAssociation = ownerAssociationTypes != null &&
 								ownerAssociationTypes[i] != null &&
 								ownerAssociationTypes[i].isOneToOne();
 						if ( isOneToOneAssociation ) {
 							persistenceContext.addNullProperty(
 									ownerKey,
 									ownerAssociationTypes[i].getPropertyName()
 							);
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
 			final SharedSessionContractImplementor session)
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
 				LOG.debugf(
 						"Found row of collection: %s",
 						MessageHelper.collectionInfoString( persister, collectionRowKey, getFactory() )
 				);
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
 				LOG.debugf(
 						"Result set contains (possibly empty) collection: %s",
 						MessageHelper.collectionInfoString( persister, optionalKey, getFactory() )
 				);
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
 			final SharedSessionContractImplementor session) {
 
 		if ( keys != null ) {
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			// this is a collection initializer, so we must create a collection
 			// for each of the passed-in keys, to account for the possibility
 			// that the collection is empty and has no rows in the result set
 			CollectionPersister[] collectionPersisters = getCollectionPersisters();
 			for ( CollectionPersister collectionPersister : collectionPersisters ) {
 				for ( Serializable key : keys ) {
 					//handle empty collections
 					if ( debugEnabled ) {
 						LOG.debugf(
 								"Result set contains (possibly empty) collection: %s",
 								MessageHelper.collectionInfoString( collectionPersister, key, getFactory() )
 						);
 					}
 
 					session.getPersistenceContext()
 							.getLoadContexts()
 							.getCollectionLoadContext( (ResultSet) resultSetId )
 							.getLoadingCollection( collectionPersister, key );
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
 			final SharedSessionContractImplementor session) throws HibernateException, SQLException {
 
 		Serializable resultId;
 
 		// if we know there is exactly 1 row, we can skip.
 		// it would be great if we could _always_ skip this;
 		// it is a problem for <key-many-to-one>
 
 		if ( isSingleRowLoader() && id != null ) {
 			resultId = id;
 		}
 		else {
 			final Type idType = persister.getIdentifierType();
 			resultId = (Serializable) idType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedKeyAliases(),
 					session,
 					null //problematic for <key-many-to-one>!
 			);
 
 			final boolean idIsResultId = id != null &&
 					resultId != null &&
 					idType.isEqual( id, resultId, factory );
 
 			if ( idIsResultId ) {
 				resultId = id; //use the id passed in
 			}
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
 			final SharedSessionContractImplementor session) throws HibernateException, SQLException {
 
 		Object version = session.getPersistenceContext().getEntry( entity ).getVersion();
 
 		if ( version != null ) { //null version means the object is in the process of being loaded somewhere else in the ResultSet
 			final VersionType versionType = persister.getVersionType();
 			final Object currentVersion = versionType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedVersionAliases(),
 					session,
 					null
 			);
 			if ( !versionType.isEqual( version, currentVersion ) ) {
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatistics().optimisticFailure( persister.getEntityName() );
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
 			final SharedSessionContractImplementor session) throws HibernateException, SQLException {
 		final int cols = persisters.length;
 		final EntityAliases[] descriptors = getEntityAliases();
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Result row: %s", StringHelper.toString( keys ) );
 		}
 
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
 			final LockMode requestedLockMode,
 			final SharedSessionContractImplementor session)
 			throws HibernateException, SQLException {
 		if ( !persister.isInstance( object ) ) {
 			throw new WrongClassException(
 					"loaded object was of wrong class " + object.getClass(),
 					key.getIdentifier(),
 					persister.getEntityName()
 			);
 		}
 
 		if ( LockMode.NONE != requestedLockMode && upgradeLocks() ) { //no point doing this if NONE was requested
 			final EntityEntry entry = session.getPersistenceContext().getEntry( object );
 			if ( entry.getLockMode().lessThan( requestedLockMode ) ) {
 				//we only check the version when _upgrading_ lock modes
 				if ( persister.isVersioned() ) {
 					checkVersion( i, persister, key.getIdentifier(), object, rs, session );
 				}
 				//we need to upgrade the lock mode to the mode requested
 				entry.setLockMode( requestedLockMode );
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
 			final SharedSessionContractImplementor session)
 			throws HibernateException, SQLException {
 		final String instanceClass = getInstanceClass(
 				rs,
 				i,
 				persister,
 				key.getIdentifier(),
 				session
 		);
 
 		// see if the entity defines reference caching, and if so use the cached reference (if one).
 		if ( session.getCacheMode().isGetEnabled() && persister.canUseReferenceCacheEntries() ) {
 			final EntityRegionAccessStrategy cache = persister.getCacheAccessStrategy();
 			final Object ck = cache.generateCacheKey(
 					key.getIdentifier(),
 					persister,
 					session.getFactory(),
 					session.getTenantIdentifier()
 					);
 			final Object cachedEntry = CacheHelper.fromSharedCache( session, ck, cache );
 			if ( cachedEntry != null ) {
 				CacheEntry entry = (CacheEntry) persister.getCacheEntryStructure().destructure( cachedEntry, factory );
 				return ( (ReferenceCacheEntryImpl) entry ).getReference();
 			}
 		}
 
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
-		catch (SQLException sqle) {
-			session.getJdbcCoordinator().getResourceRegistry().release( st );
-			session.getJdbcCoordinator().afterStatementExecution();
-			throw sqle;
-		}
-		catch (HibernateException he) {
-			session.getJdbcCoordinator().getResourceRegistry().release( st );
+		catch (SQLException | HibernateException e) {
+			session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
-			throw he;
+			throw e;
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
-		catch (SQLException sqle) {
-			session.getJdbcCoordinator().getResourceRegistry().release( st );
-			session.getJdbcCoordinator().afterStatementExecution();
-			throw sqle;
-		}
-		catch (HibernateException he) {
-			session.getJdbcCoordinator().getResourceRegistry().release( st );
+		catch (SQLException | HibernateException e) {
+			session.getJdbcCoordinator().getLogicalConnection().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
-			throw he;
+			throw e;
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
-			throw factory.getSQLExceptionHelper().convert(
+			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
-			throw factory.getSQLExceptionHelper().convert(
+			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
-			throw factory.getSQLExceptionHelper().convert(
+			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
-			throw factory.getSQLExceptionHelper().convert(
+			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
-			throw factory.getSQLExceptionHelper().convert(
+			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
-			throw factory.getSQLExceptionHelper().convert(
+			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
-			throw factory.getSQLExceptionHelper().convert(
+			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
-			throw factory.getSQLExceptionHelper().convert(
+			throw factory.getJdbcServices().getSqlExceptionHelper().convert(
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
index c984248b02..9479626213 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/criteria/CriteriaLoader.java
@@ -1,295 +1,294 @@
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
-import org.hibernate.ScrollableResults;
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
 import org.hibernate.query.spi.ScrollableResultsImplementor;
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
 
 	public ScrollableResultsImplementor scroll(SharedSessionContractImplementor session, ScrollMode scrollMode)
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
 
-		final Map keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap() : null;
+		final Map<String,String[]> keyColumnNames = dialect.forUpdateOfColumns() ? new HashMap() : null;
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
index 026174719a..fe53fa1ba2 100755
--- a/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/custom/CustomLoader.java
@@ -1,547 +1,543 @@
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
-import org.hibernate.ScrollableResults;
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
 import org.hibernate.query.spi.ScrollableResultsImplementor;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
-
 /**
  * Extension point for loaders which use a SQL result set with "unexpected" column aliases.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class CustomLoader extends Loader {
 
 	// Currently *not* cachable if autodiscover types is in effect (e.g. "select * ...")
 
 	private final String sql;
-	private final Set<Serializable> querySpaces = new HashSet<Serializable>();
+	private final Set<Serializable> querySpaces = new HashSet<>();
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
 
-		List<Queryable> entityPersisters = new ArrayList<Queryable>();
-		List<Integer> entityOwners = new ArrayList<Integer>();
-		List<EntityAliases> entityAliases = new ArrayList<EntityAliases>();
+		List<Queryable> entityPersisters = new ArrayList<>();
+		List<Integer> entityOwners = new ArrayList<>();
+		List<EntityAliases> entityAliases = new ArrayList<>();
 
-		List<QueryableCollection> collectionPersisters = new ArrayList<QueryableCollection>();
-		List<Integer> collectionOwners = new ArrayList<Integer>();
-		List<CollectionAliases> collectionAliases = new ArrayList<CollectionAliases>();
+		List<QueryableCollection> collectionPersisters = new ArrayList<>();
+		List<Integer> collectionOwners = new ArrayList<>();
+		List<CollectionAliases> collectionAliases = new ArrayList<>();
 
-		List<LockMode> lockModes = new ArrayList<LockMode>();
-		List<ResultColumnProcessor> resultColumnProcessors = new ArrayList<ResultColumnProcessor>();
-		List<Return> nonScalarReturnList = new ArrayList<Return>();
-		List<Type> resultTypes = new ArrayList<Type>();
-		List<String> specifiedAliases = new ArrayList<String>();
+		List<LockMode> lockModes = new ArrayList<>();
+		List<ResultColumnProcessor> resultColumnProcessors = new ArrayList<>();
+		List<Return> nonScalarReturnList = new ArrayList<>();
+		List<Type> resultTypes = new ArrayList<>();
+		List<String> specifiedAliases = new ArrayList<>();
 
 		int returnableCounter = 0;
 		boolean hasScalars = false;
 
-		List<Boolean> includeInResultRowList = new ArrayList<Boolean>();
+		List<Boolean> includeInResultRowList = new ArrayList<>();
 
 		for ( Return rtn : customQuery.getCustomQueryReturns() ) {
 			if ( rtn instanceof ScalarReturn ) {
 				ScalarReturn scalarRtn = (ScalarReturn) rtn;
 				resultTypes.add( scalarRtn.getType() );
 				specifiedAliases.add( scalarRtn.getColumnAlias() );
 				resultColumnProcessors.add(
 						new ScalarResultColumnProcessor(
-								StringHelper.unquote( scalarRtn.getColumnAlias(), factory.getDialect() ),
+								StringHelper.unquote( scalarRtn.getColumnAlias(), factory.getJdbcServices().getDialect() ),
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
-							StringHelper.unquote( scalarReturn.getColumnAlias(), factory.getDialect() ),
+							StringHelper.unquote( scalarReturn.getColumnAlias(), factory.getJdbcServices().getDialect() ),
 							scalarReturn.getType()
 					);
 				}
 
 				resultColumnProcessors.add(
 						new ConstructorResultColumnProcessor( constructorReturn.getTargetClass(), scalarProcessors )
 				);
 			}
 			else if ( rtn instanceof RootReturn ) {
 				RootReturn rootRtn = (RootReturn) rtn;
-				Queryable persister = (Queryable) factory.getEntityPersister( rootRtn.getEntityName() );
+				Queryable persister = (Queryable) factory.getMetamodel().entityPersister( rootRtn.getEntityName() );
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
-				QueryableCollection persister = (QueryableCollection) factory.getCollectionPersister( role );
+				QueryableCollection persister = (QueryableCollection) factory.getMetamodel().collectionPersister( role );
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
-				Queryable persister = (Queryable) factory.getEntityPersister( entityName );
+				Queryable persister = (Queryable) factory.getMetamodel().entityPersister( entityName );
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
-				QueryableCollection persister = (QueryableCollection) factory.getCollectionPersister( role );
+				QueryableCollection persister = (QueryableCollection) factory.getMetamodel().collectionPersister( role );
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
-			CollectionPersister persister = getFactory().getCollectionPersister( role );
+			CollectionPersister persister = getFactory().getMetamodel().collectionPersister( role );
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
 
-		return (Queryable) getFactory().getEntityPersister( entityName );
+		return (Queryable) getFactory().getMetamodel().entityPersister( entityName );
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
 
 	public ScrollableResultsImplementor scroll(final QueryParameters queryParameters, final SharedSessionContractImplementor session)
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
 
-			List<String> aliases = new ArrayList<String>();
-			List<Type> types = new ArrayList<Type>();
+			List<String> aliases = new ArrayList<>();
+			List<Type> types = new ArrayList<>();
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
-	 * As a hacky workaround, override
-	 * {@link #putResultInQueryCache(SharedSessionContractImplementor, QueryParameters, Type[], QueryCache, QueryKey, List)} here
-	 * and provide the {@link #resultTypes}.
+	 * As a hacky workaround, overriden here to provide the {@link #resultTypes}.
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
index e83adbace0..e57e4893fe 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
@@ -1,632 +1,632 @@
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
-import org.hibernate.ScrollableResults;
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
 import org.hibernate.query.spi.ScrollableResultsImplementor;
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
 
-	private final Map<String, String> sqlAliasByEntityAlias = new HashMap<String, String>( 8 );
+	private final Map<String, String> sqlAliasByEntityAlias = new HashMap<>( 8 );
 
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
+				//noinspection StatementWithEmptyBody
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
 
 	public ScrollableResultsImplementor scroll(
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
diff --git a/hibernate-core/src/main/java/org/hibernate/query/BasicQueryContract.java b/hibernate-core/src/main/java/org/hibernate/query/CommonQueryContract.java
similarity index 86%
rename from hibernate-core/src/main/java/org/hibernate/query/BasicQueryContract.java
rename to hibernate-core/src/main/java/org/hibernate/query/CommonQueryContract.java
index dffe3d124e..5c1ee9bf92 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/BasicQueryContract.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/CommonQueryContract.java
@@ -1,18 +1,18 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.query;
 
 /**
  * Defines the aspects of query definition that apply to all forms of
  * querying (HQL, JPQL, criteria) across all forms of persistence contexts
  * (Session, StatelessSession, EntityManager).
  *
  * @author Steve Ebersole
  * @author Gavin King
  */
-public interface BasicQueryContract extends org.hibernate.BasicQueryContract {
+public interface CommonQueryContract extends org.hibernate.BasicQueryContract {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/query/Query.java b/hibernate-core/src/main/java/org/hibernate/query/Query.java
index b32903370a..74a99d0e28 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/Query.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/Query.java
@@ -1,183 +1,182 @@
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
-import java.util.Spliterator;
 import java.util.stream.Stream;
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
-public interface Query<R> extends TypedQuery<R>, org.hibernate.Query<R>, BasicQueryContract {
+public interface Query<R> extends TypedQuery<R>, org.hibernate.Query<R>, CommonQueryContract {
 	/**
 	 * Get the QueryProducer this Query originates from.
 	 */
 	QueryProducer getProducer();
 
 	/**
 	 * "QueryOptions" is a better name, I think, than "RowSelection" -> 6.0
 	 *
 	 * @todo 6.0 rename RowSelection to QueryOptions
 	 *
 	 * @return Return the encapsulation of this query's options, which includes access to
 	 * firstRow, maxRows, timeout and fetchSize.   Important because this gives access to
 	 * those values in their Integer form rather than the primitive form (int) required by JPA.
 	 */
 	RowSelection getQueryOptions();
 
 	/**
 	 * Retrieve a Stream over the query results.
 	 * <p/>
 	 * In the initial implementation (5.2) this returns a simple sequential Stream.  The plan
 	 * is to return a a smarter stream in 6.0 leveraging the SQM model.
 	 *
 	 * @return The results Stream
 	 *
 	 * @since 5.2
 	 */
 	Stream<R> stream();
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/query/internal/CollectionFilterImpl.java b/hibernate-core/src/main/java/org/hibernate/query/internal/CollectionFilterImpl.java
index ee6120a308..3bf8534105 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/internal/CollectionFilterImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/internal/CollectionFilterImpl.java
@@ -1,121 +1,120 @@
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
-import org.hibernate.ScrollableResults;
 import org.hibernate.engine.spi.SharedSessionContractImplementor;
 import org.hibernate.query.Query;
 import org.hibernate.query.spi.ScrollableResultsImplementor;
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
 	public ScrollableResultsImplementor scroll() throws HibernateException {
 		throw new UnsupportedOperationException( "Can't scroll filters" );
 	}
 
 	@Override
 	public ScrollableResultsImplementor scroll(ScrollMode scrollMode) {
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
index 3c22019de7..bffebed89e 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/internal/NativeQueryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/internal/NativeQueryImpl.java
@@ -1,717 +1,719 @@
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
-import org.hibernate.ScrollableResults;
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
 import org.hibernate.query.spi.ScrollableResultsImplementor;
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
 	protected ScrollableResultsImplementor doScroll(ScrollMode scrollMode) {
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
+	@SuppressWarnings("unchecked")
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
-	public NativeQueryImplementor setParameter(String name, Object value, TemporalType temporalType) {
+	public NativeQueryImplementor<T> setParameter(String name, Object value, TemporalType temporalType) {
 		super.setParameter( name, value, temporalType );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setParameter(int position, Object value, TemporalType temporalType) {
+	public NativeQueryImplementor<T> setParameter(int position, Object value, TemporalType temporalType) {
 		super.setParameter( position, value, temporalType );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setParameterList(QueryParameter parameter, Collection values) {
+	public NativeQueryImplementor<T> setParameterList(QueryParameter parameter, Collection values) {
 		super.setParameterList( parameter, values );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setParameterList(String name, Collection values) {
+	public NativeQueryImplementor<T> setParameterList(String name, Collection values) {
 		super.setParameterList( name, values );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setParameterList(String name, Collection values, Type type) {
+	public NativeQueryImplementor<T> setParameterList(String name, Collection values, Type type) {
 		super.setParameterList( name, values, type );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setParameterList(String name, Object[] values, Type type) {
+	public NativeQueryImplementor<T> setParameterList(String name, Object[] values, Type type) {
 		super.setParameterList( name, values, type );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setParameterList(String name, Object[] values) {
+	public NativeQueryImplementor<T> setParameterList(String name, Object[] values) {
 		super.setParameterList( name, values );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setParameter(Parameter param, Calendar value, TemporalType temporalType) {
+	@SuppressWarnings("unchecked")
+	public NativeQueryImplementor<T> setParameter(Parameter param, Calendar value, TemporalType temporalType) {
 		super.setParameter( param, value, temporalType );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setParameter(Parameter param, Date value, TemporalType temporalType) {
+	@SuppressWarnings("unchecked")
+	public NativeQueryImplementor<T> setParameter(Parameter param, Date value, TemporalType temporalType) {
 		super.setParameter( param, value, temporalType );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setParameter(String name, Calendar value, TemporalType temporalType) {
+	public NativeQueryImplementor<T> setParameter(String name, Calendar value, TemporalType temporalType) {
 		super.setParameter( name, value, temporalType );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setParameter(String name, Date value, TemporalType temporalType) {
+	public NativeQueryImplementor<T> setParameter(String name, Date value, TemporalType temporalType) {
 		super.setParameter( name, value, temporalType );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setParameter(int position, Calendar value, TemporalType temporalType) {
+	public NativeQueryImplementor<T> setParameter(int position, Calendar value, TemporalType temporalType) {
 		super.setParameter( position, value, temporalType );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setParameter(int position, Date value, TemporalType temporalType) {
+	public NativeQueryImplementor<T> setParameter(int position, Date value, TemporalType temporalType) {
 		super.setParameter( position, value, temporalType );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setResultTransformer(ResultTransformer transformer) {
+	public NativeQueryImplementor<T> setResultTransformer(ResultTransformer transformer) {
 		super.setResultTransformer( transformer );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setProperties(Map map) {
+	public NativeQueryImplementor<T> setProperties(Map map) {
 		super.setProperties( map );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setProperties(Object bean) {
+	public NativeQueryImplementor<T> setProperties(Object bean) {
 		super.setProperties( bean );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setMaxResults(int maxResult) {
+	public NativeQueryImplementor<T> setMaxResults(int maxResult) {
 		super.setMaxResults( maxResult );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setFirstResult(int startPosition) {
+	public NativeQueryImplementor<T> setFirstResult(int startPosition) {
 		super.setFirstResult( startPosition );
 		return this;
 	}
 
 	@Override
-	public NativeQueryImplementor setHint(String hintName, Object value) {
+	public NativeQueryImplementor<T> setHint(String hintName, Object value) {
 		super.setHint( hintName, value );
 		return this;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/query/internal/ScrollableResultsIterator.java b/hibernate-core/src/main/java/org/hibernate/query/internal/ScrollableResultsIterator.java
index dedabe93f3..99069473d1 100644
--- a/hibernate-core/src/main/java/org/hibernate/query/internal/ScrollableResultsIterator.java
+++ b/hibernate-core/src/main/java/org/hibernate/query/internal/ScrollableResultsIterator.java
@@ -1,41 +1,41 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.query.internal;
 
 import org.hibernate.Incubating;
 import org.hibernate.query.spi.CloseableIterator;
 import org.hibernate.query.spi.ScrollableResultsImplementor;
 
 /**
  * @author Steve Ebersole
  *
  * @since 5.2
  */
 @Incubating
-public class ScrollableResultsIterator<T> implements CloseableIterator {
+class ScrollableResultsIterator<T> implements CloseableIterator {
 	private final ScrollableResultsImplementor scrollableResults;
 
-	public ScrollableResultsIterator(ScrollableResultsImplementor scrollableResults) {
+	ScrollableResultsIterator(ScrollableResultsImplementor scrollableResults) {
 		this.scrollableResults = scrollableResults;
 	}
 
 	@Override
 	public void close() {
 		scrollableResults.close();
 	}
 
 	@Override
 	public boolean hasNext() {
 		return !scrollableResults.isClosed() && scrollableResults.next();
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public T next() {
 		return (T) scrollableResults.get();
 	}
 }
