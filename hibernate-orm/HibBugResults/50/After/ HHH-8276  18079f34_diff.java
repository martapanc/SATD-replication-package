diff --git a/hibernate-core/src/main/java/org/hibernate/loader/entity/plan/AbstractLoadPlanBasedEntityLoader.java b/hibernate-core/src/main/java/org/hibernate/loader/entity/plan/AbstractLoadPlanBasedEntityLoader.java
index 9f66629638..eba9cdab2b 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/entity/plan/AbstractLoadPlanBasedEntityLoader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/entity/plan/AbstractLoadPlanBasedEntityLoader.java
@@ -1,728 +1,722 @@
 package org.hibernate.loader.entity.plan;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Statement;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.LockOptions;
 import org.hibernate.ScrollMode;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.pagination.LimitHelper;
 import org.hibernate.dialect.pagination.NoopLimitHandler;
 import org.hibernate.engine.jdbc.ColumnNameCache;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.TypedValue;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
 import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
+import org.hibernate.loader.plan.exec.spi.EntityLoadQueryDetails;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.LoadPlan;
-import org.hibernate.loader.plan.spi.build.LoadPlanBuilder;
+import org.hibernate.loader.plan.spi.build.MetadataDrivenLoadPlanBuilder;
 import org.hibernate.loader.spi.AfterLoadAction;
-import org.hibernate.loader.spi.NoOpLoadPlanAdvisor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.transform.ResultTransformer;
 import org.hibernate.type.Type;
 
 /**
  * A UniqueEntityLoader implementation based on using LoadPlans
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractLoadPlanBasedEntityLoader implements UniqueEntityLoader {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( AbstractLoadPlanBasedEntityLoader.class );
 
 	private final SessionFactoryImplementor factory;
 	private final OuterJoinLoadable entityPersister;
 	private final Type uniqueKeyType;
 	private final String entityName;
 
 	private final LoadPlan plan;
-	private final LoadQueryDetails staticLoadQuery;
+	private final EntityLoadQueryDetails staticLoadQuery;
 
 	private ColumnNameCache columnNameCache;
 
 	public AbstractLoadPlanBasedEntityLoader(
 			OuterJoinLoadable entityPersister,
 			SessionFactoryImplementor factory,
 			String[] uniqueKeyColumnNames,
 			Type uniqueKeyType,
 			QueryBuildingParameters buildingParameters) {
 		this.entityPersister = entityPersister;
 		this.factory = factory;
 		this.uniqueKeyType = uniqueKeyType;
 		this.entityName = entityPersister.getEntityName();
 
 		final SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 				factory,
 				buildingParameters.getQueryInfluencers()
 		);
 
-		this.plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
-		this.staticLoadQuery = LoadQueryDetails.makeForBatching(
-				uniqueKeyColumnNames,
+		this.plan = MetadataDrivenLoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
+		this.staticLoadQuery = EntityLoadQueryDetails.makeForBatching(
 				plan,
-				factory,
-				buildingParameters
+				uniqueKeyColumnNames,
+				buildingParameters,
+				factory
 		);
 	}
 
 	protected SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
-	protected LoadQueryDetails getStaticLoadQuery() {
+	protected EntityLoadQueryDetails getStaticLoadQuery() {
 		return staticLoadQuery;
 	}
 
 	protected String getEntityName() {
 		return entityName;
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
 
 		if ( log.isDebugEnabled() ) {
 			log.debugf( "Batch loading entity: %s", MessageHelper.infoString( persister, ids, getFactory() ) );
 		}
 
 		final Type[] types = new Type[ids.length];
 		Arrays.fill( types, idType );
 		List result;
 		try {
 			final QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( types );
 			qp.setPositionalParameterValues( ids );
 			qp.setLockOptions( lockOptions );
 
 			result = executeLoad(
 					session,
 					qp,
 					staticLoadQuery,
 					false,
 					null
 			);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not load an entity batch: " + MessageHelper.infoString( entityPersister, ids, getFactory() ),
 					staticLoadQuery.getSqlStatement()
 			);
 		}
 
 		log.debug( "Done entity batch load" );
 
 		return result;
 
 	}
 
 	@Override
 	@Deprecated
 	public Object load(Serializable id, Object optionalObject, SessionImplementor session) throws HibernateException {
 		return load( id, optionalObject, session, LockOptions.NONE );
 	}
 
 	@Override
 	public Object load(Serializable id, Object optionalObject, SessionImplementor session, LockOptions lockOptions) {
 		Object result = null;
 
 		try {
 			final QueryParameters qp = new QueryParameters();
 			qp.setPositionalParameterTypes( new Type[] { entityPersister.getIdentifierType() } );
 			qp.setPositionalParameterValues( new Object[] { id } );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( entityPersister.getEntityName() );
 			qp.setOptionalId( id );
 			qp.setLockOptions( lockOptions );
 
 			final List results = executeLoad(
 					session,
 					qp,
 					staticLoadQuery,
 					false,
 					null
 			);
 			result = extractEntityResult( results );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not load an entity: " + MessageHelper.infoString(
 							entityPersister,
 							id,
 							entityPersister.getIdentifierType(),
 							factory
 					),
 					staticLoadQuery.getSqlStatement()
 			);
 		}
 
 		log.debugf( "Done entity load : %s#%s", getEntityName(), id );
 		return result;
 	}
 
 	protected List executeLoad(
 			SessionImplementor session,
 			QueryParameters queryParameters,
-			LoadQueryDetails loadQueryDetails,
+			EntityLoadQueryDetails loadQueryDetails,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer) throws SQLException {
 		final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 		return executeLoad(
 				session,
 				queryParameters,
 				loadQueryDetails,
 				returnProxies,
 				forcedResultTransformer,
 				afterLoadActions
 		);
 	}
 
 	protected List executeLoad(
 			SessionImplementor session,
 			QueryParameters queryParameters,
-			LoadQueryDetails loadQueryDetails,
+			EntityLoadQueryDetails loadQueryDetails,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer,
 			List<AfterLoadAction> afterLoadActions) throws SQLException {
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final boolean defaultReadOnlyOrig = persistenceContext.isDefaultReadOnly();
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
 		try {
 			List results;
 			final String sql = loadQueryDetails.getSqlStatement();
 			try {
 				final SqlStatementWrapper wrapper = executeQueryStatement( sql, queryParameters, false, afterLoadActions, session );
 				results = loadQueryDetails.getResultSetProcessor().extractResults(
-						// todo : hook in the JPA 2.1 entity graph advisor
-						NoOpLoadPlanAdvisor.INSTANCE,
 						wrapper.getResultSet(),
 						session,
 						queryParameters,
 						new NamedParameterContext() {
 							@Override
 							public int[] getNamedParameterLocations(String name) {
 								return AbstractLoadPlanBasedEntityLoader.this.getNamedParameterLocs( name );
 							}
 						},
-						loadQueryDetails.getAliasResolutionContext(),
 						returnProxies,
 						queryParameters.isReadOnly(),
 						forcedResultTransformer,
 						afterLoadActions
 				);
 			}
 			finally {
 				persistenceContext.afterLoad();
 			}
 			persistenceContext.initializeNonLazyCollections();
 			return results;
 		}
 		finally {
 			// Restore the original default
 			persistenceContext.setDefaultReadOnly( defaultReadOnlyOrig );
 		}
 	}
 
 	protected Object extractEntityResult(List results) {
 		if ( results.size() == 0 ) {
 			return null;
 		}
 		else if ( results.size() == 1 ) {
 			return results.get( 0 );
 		}
 		else {
 			final Object row = results.get( 0 );
 			if ( row.getClass().isArray() ) {
 				// the logical type of the result list is List<Object[]>.  See if the contained
 				// array contains just one element, and return that if so
 				final Object[] rowArray = (Object[]) row;
 				if ( rowArray.length == 1 ) {
 					return rowArray[0];
 				}
 			}
 			else {
 				return row;
 			}
 		}
 
 		throw new HibernateException( "Unable to interpret given query results in terms of a load-entity query" );
 	}
 
 	protected Object doQueryAndLoadEntity(
 			SessionImplementor session,
 			QueryParameters queryParameters,
-			LoadQueryDetails loadQueryDetails,
+			EntityLoadQueryDetails loadQueryDetails,
 			boolean returnProxies,
 			ResultTransformer forcedResultTransformer) throws SQLException {
 
 		final List<AfterLoadAction> afterLoadActions = new ArrayList<AfterLoadAction>();
 
 		final SqlStatementWrapper wrapper = executeQueryStatement( queryParameters, false, afterLoadActions, session );
 
 		try {
 			final List results = loadQueryDetails.getResultSetProcessor().extractResults(
-					NoOpLoadPlanAdvisor.INSTANCE,
 					wrapper.getResultSet(),
 					session,
 					queryParameters,
 					new NamedParameterContext() {
 						@Override
 						public int[] getNamedParameterLocations(String name) {
 							return AbstractLoadPlanBasedEntityLoader.this.getNamedParameterLocs( name );
 						}
 					},
-					loadQueryDetails.getAliasResolutionContext(),
 					returnProxies,
 					queryParameters.isReadOnly(),
 					forcedResultTransformer,
 					afterLoadActions
 			);
 
 
 			if ( results.size() == 0 ) {
 				return null;
 			}
 			else if ( results.size() == 1 ) {
 				return results.get( 0 );
 			}
 			else {
 				final Object row = results.get( 0 );
 				if ( row.getClass().isArray() ) {
 					// the logical type of the result list is List<Object[]>.  See if the contained
 					// array contains just one element, and return that if so
 					final Object[] rowArray = (Object[]) row;
 					if ( rowArray.length == 1 ) {
 						return rowArray[0];
 					}
 				}
 				else {
 					return row;
 				}
 			}
 
 			throw new HibernateException( "Unable to interpret given query results in terms of a load-entity query" );
 		}
 		finally {
 			session.getTransactionCoordinator().getJdbcCoordinator().release( wrapper.getStatement() );
 		}
 	}
 
 
 	protected SqlStatementWrapper executeQueryStatement(
 			final QueryParameters queryParameters,
 			final boolean scroll,
 			List<AfterLoadAction> afterLoadActions,
 			final SessionImplementor session) throws SQLException {
 		return executeQueryStatement( staticLoadQuery.getSqlStatement(), queryParameters, scroll, afterLoadActions, session );
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
 	 * Build LIMIT clause handler applicable for given selection criteria. Returns {@link org.hibernate.dialect.pagination.NoopLimitHandler} delegate
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
 
 	private String preprocessSQL(
 			String sql,
 			QueryParameters queryParameters,
 			Dialect dialect,
 			List<AfterLoadAction> afterLoadActions) {
 		return getFactory().getSettings().isCommentsEnabled()
 				? prependComment( sql, queryParameters )
 				: sql;
 	}
 
 	private String prependComment(String sql, QueryParameters parameters) {
 		final String comment = parameters.getComment();
 		if ( comment == null ) {
 			return sql;
 		}
 		else {
 			return "/* " + comment + " */ " + sql;
 		}
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
 		final boolean useLimit = LimitHelper.useLimit( limitHandler, selection );
 		final boolean hasFirstRow = LimitHelper.hasFirstRow( selection );
 		final boolean useLimitOffset = hasFirstRow && useLimit && limitHandler.supportsLimitOffset();
 		final boolean callable = queryParameters.isCallable();
 		final ScrollMode scrollMode = getScrollMode( scroll, hasFirstRow, useLimitOffset, queryParameters );
 
 		final PreparedStatement st = session.getTransactionCoordinator().getJdbcCoordinator()
 				.getStatementPreparer().prepareQueryStatement( sql, callable, scrollMode );
 
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
 			final LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 					if ( !dialect.supportsLockTimeouts() ) {
 						if ( log.isDebugEnabled() ) {
 							log.debugf(
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
 
 			if ( log.isTraceEnabled() ) {
 				log.tracev( "Bound [{0}] parameters total", col );
 			}
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
 
 	protected ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {
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
 			final Iterator itr = namedParams.entrySet().iterator();
 			final boolean debugEnabled = log.isDebugEnabled();
 			int result = 0;
 			while ( itr.hasNext() ) {
 				final Map.Entry e = (Map.Entry) itr.next();
 				final String name = (String) e.getKey();
 				final TypedValue typedval = (TypedValue) e.getValue();
 				final int[] locs = getNamedParameterLocs( name );
 				for ( int loc : locs ) {
 					if ( debugEnabled ) {
 						log.debugf(
 								"bindNamedParameters() %s -> %s [%s]",
 								typedval.getValue(),
 								name,
 								loc + startIndex
 						);
 					}
 					typedval.getType().nullSafeSet( statement, typedval.getValue(), loc + startIndex, session );
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
 
 	/**
 	 * Advance the cursor to the first required row of the <tt>ResultSet</tt>
 	 */
 	protected void advance(final ResultSet rs, final RowSelection selection) throws SQLException {
 		final int firstRow = LimitHelper.getFirstRow( selection );
 		if ( firstRow != 0 ) {
 			if ( getFactory().getSettings().isScrollableResultSetsEnabled() ) {
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
 
 	protected void autoDiscoverTypes(ResultSet rs) {
 		throw new AssertionFailure("Auto discover types not supported in this loader");
 
 	}
 
 	private synchronized ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
 		// synchronized to avoid multi-thread access issues; defined as method synch to avoid
 		// potential deadlock issues due to nature of code.
 		if ( session.getFactory().getSettings().isWrapResultSetsEnabled() ) {
 			try {
 				if ( log.isDebugEnabled() ) {
 					log.debugf( "Wrapping result set [%s]", rs );
 				}
 				return session.getFactory()
 						.getJdbcServices()
 						.getResultSetWrapper().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch(SQLException e) {
 				log.unableToWrapResultSet( e );
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
 			log.trace( "Building columnName->columnIndex cache" );
 			columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
 		}
 
 		return columnNameCache;
 	}
 
 	/**
 	 * Wrapper class for {@link java.sql.Statement} and associated {@link ResultSet}.
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
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/Helper.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/Helper.java
new file mode 100644
index 0000000000..ee6a466615
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/Helper.java
@@ -0,0 +1,79 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan.exec.internal;
+
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.loader.plan.spi.Return;
+
+/**
+ * @author Steve Ebersole
+ */
+public class Helper {
+	/**
+	 * Singleton access
+	 */
+	public static final Helper INSTANCE = new Helper();
+
+	/**
+	 * Disallow direct instantiation
+	 */
+	private Helper() {
+	}
+
+
+	/**
+	 * Extract the root return of the LoadPlan, assuming there is just one.
+	 *
+	 * @param loadPlan The LoadPlan from which to extract the root return
+	 * @param returnType The Return type expected, passed as an argument
+	 * @param <T> The parameterized type of the specific Return type expected
+	 *
+	 * @return The root Return
+	 *
+	 * @throws IllegalStateException If there is no root, more than one root or the single root
+	 * is not of the expected type.
+	 */
+	@SuppressWarnings("unchecked")
+	public <T extends Return> T extractRootReturn(LoadPlan loadPlan, Class<T> returnType) {
+		if ( loadPlan.getReturns().size() == 0 ) {
+			throw new IllegalStateException( "LoadPlan contained no root returns" );
+		}
+		else if ( loadPlan.getReturns().size() > 1 ) {
+			throw new IllegalStateException( "LoadPlan contained more than one root returns" );
+		}
+
+		final Return rootReturn = loadPlan.getReturns().get( 0 );
+		if ( !returnType.isInstance( rootReturn ) ) {
+			throw new IllegalStateException(
+					String.format(
+							"Unexpected LoadPlan root return; expecting %s, but found %s",
+							returnType.getName(),
+							rootReturn.getClass().getName()
+					)
+			);
+		}
+
+		return (T) rootReturn;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/internal/LoadQueryBuilderHelper.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/LoadQueryBuilderHelper.java
similarity index 55%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/internal/LoadQueryBuilderHelper.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/LoadQueryBuilderHelper.java
index 0f99cb8596..25e4751371 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/internal/LoadQueryBuilderHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/LoadQueryBuilderHelper.java
@@ -1,512 +1,871 @@
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
-package org.hibernate.loader.plan.exec.query.internal;
+package org.hibernate.loader.plan.exec.internal;
+
+import java.util.ArrayList;
+import java.util.Collections;
+import java.util.List;
+
+import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.NotYetImplementedException;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.internal.JoinHelper;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.util.StringHelper;
+import org.hibernate.loader.EntityAliases;
+import org.hibernate.loader.plan.exec.process.internal.CollectionReferenceReader;
+import org.hibernate.loader.plan.exec.process.internal.EntityIdentifierReader;
+import org.hibernate.loader.plan.exec.process.internal.EntityIdentifierReaderImpl;
+import org.hibernate.loader.plan.exec.process.internal.EntityReferenceReader;
+import org.hibernate.loader.plan.exec.process.internal.OneToOneFetchReader;
+import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
 import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
 import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
 import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
+import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
+import org.hibernate.loader.plan.exec.spi.ReaderCollector;
 import org.hibernate.loader.plan.spi.AnyFetch;
+import org.hibernate.loader.plan.spi.BidirectionalEntityFetch;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CompositeElementGraph;
 import org.hibernate.loader.plan.spi.CompositeFetch;
 import org.hibernate.loader.plan.spi.CompositeIndexGraph;
 import org.hibernate.loader.plan.spi.EntityElementGraph;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.loader.plan.spi.FetchOwner;
-import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
-import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
-import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.walking.internal.FetchStrategyHelper;
 import org.hibernate.persister.walking.spi.WalkingException;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.type.AssociationType;
+import org.hibernate.type.Type;
 
 /**
  * Helper for implementors of entity and collection based query building based on LoadPlans providing common
  * functionality
  *
  * @author Steve Ebersole
  */
 public class LoadQueryBuilderHelper {
+	private static final Logger log = CoreLogging.logger( LoadQueryBuilderHelper.class );
+
 	private LoadQueryBuilderHelper() {
 	}
 
-	public static void applyJoinFetches(
+	// used to collect information about fetches.  For now that is only whether there were subselect fetches
+	public static interface FetchStats {
+		public boolean hasSubselectFetches();
+	}
+
+	private static class FetchStatsImpl implements FetchStats {
+		private boolean hasSubselectFetch;
+
+		public void processingFetch(Fetch fetch) {
+			if ( ! hasSubselectFetch ) {
+				if ( fetch.getFetchStrategy().getStyle() == FetchStyle.SUBSELECT
+						&& fetch.getFetchStrategy().getTiming() != FetchTiming.IMMEDIATE ) {
+					hasSubselectFetch = true;
+				}
+			}
+		}
+
+		@Override
+		public boolean hasSubselectFetches() {
+			return hasSubselectFetch;
+		}
+	}
+
+	public static void applyIdentifierJoinFetches(
 			SelectStatementBuilder selectStatementBuilder,
 			SessionFactoryImplementor factory,
 			FetchOwner fetchOwner,
 			QueryBuildingParameters buildingParameters,
-			AliasResolutionContext aliasResolutionContext) {
+			AliasResolutionContext aliasResolutionContext,
+			ReaderCollector readerCollector) {
+
+	}
+
+	public static FetchStats applyJoinFetches(
+			SelectStatementBuilder selectStatementBuilder,
+			SessionFactoryImplementor factory,
+			FetchOwner fetchOwner,
+			QueryBuildingParameters buildingParameters,
+			AliasResolutionContext aliasResolutionContext,
+			ReaderCollector readerCollector) {
+
 		final JoinFragment joinFragment = factory.getDialect().createOuterJoinFragment();
+		final FetchStatsImpl stats = new FetchStatsImpl();
+
+		// if the fetch owner is an entityReference, we should also walk its identifier fetches here...
+		//
+		// what if fetchOwner is a composite fetch (as it would be in the case of a key-many-to-one)?
+		if ( EntityReference.class.isInstance( fetchOwner ) ) {
+			final EntityReference fetchOwnerAsEntityReference = (EntityReference) fetchOwner;
+			for ( Fetch fetch : fetchOwnerAsEntityReference.getIdentifierDescription().getFetches() ) {
+				processFetch(
+						selectStatementBuilder,
+						factory,
+						joinFragment,
+						fetchOwner,
+						fetch,
+						buildingParameters,
+						aliasResolutionContext,
+						readerCollector,
+						stats
+				);
+			}
+		}
 
 		processJoinFetches(
 				selectStatementBuilder,
 				factory,
 				joinFragment,
 				fetchOwner,
 				buildingParameters,
-				aliasResolutionContext
+				aliasResolutionContext,
+				readerCollector,
+				stats
 		);
 
 		selectStatementBuilder.setOuterJoins(
 				joinFragment.toFromFragmentString(),
 				joinFragment.toWhereFragmentString()
 		);
+
+		return stats;
 	}
 
+
 	private static void processJoinFetches(
 			SelectStatementBuilder selectStatementBuilder,
 			SessionFactoryImplementor factory,
 			JoinFragment joinFragment,
 			FetchOwner fetchOwner,
 			QueryBuildingParameters buildingParameters,
-			AliasResolutionContext aliasResolutionContext) {
+			AliasResolutionContext aliasResolutionContext,
+			ReaderCollector readerCollector,
+			FetchStatsImpl stats) {
+
 		for ( Fetch fetch : fetchOwner.getFetches() ) {
 			processFetch(
 					selectStatementBuilder,
 					factory,
 					joinFragment,
 					fetchOwner,
 					fetch,
 					buildingParameters,
-					aliasResolutionContext
+					aliasResolutionContext,
+					readerCollector,
+					stats
 			);
 		}
 	}
 
 	private static void processFetch(
 			SelectStatementBuilder selectStatementBuilder,
 			SessionFactoryImplementor factory,
 			JoinFragment joinFragment,
 			FetchOwner fetchOwner,
 			Fetch fetch,
 			QueryBuildingParameters buildingParameters,
-			AliasResolutionContext aliasResolutionContext) {
+			AliasResolutionContext aliasResolutionContext,
+			ReaderCollector readerCollector,
+			FetchStatsImpl stats) {
 		if ( ! FetchStrategyHelper.isJoinFetched( fetch.getFetchStrategy() ) ) {
 			return;
 		}
 
 		if ( EntityFetch.class.isInstance( fetch ) ) {
+			final EntityFetch entityFetch = (EntityFetch) fetch;
 			processEntityFetch(
 					selectStatementBuilder,
 					factory,
 					joinFragment,
 					fetchOwner,
-					(EntityFetch) fetch,
-					buildingParameters,
-					aliasResolutionContext
-			);
-			processJoinFetches(
-					selectStatementBuilder,
-					factory,
-					joinFragment,
-					(EntityFetch) fetch,
+					entityFetch,
 					buildingParameters,
-					aliasResolutionContext
+					aliasResolutionContext,
+					readerCollector,
+					stats
 			);
 		}
 		else if ( CollectionFetch.class.isInstance( fetch ) ) {
+			final CollectionFetch collectionFetch = (CollectionFetch) fetch;
 			processCollectionFetch(
 					selectStatementBuilder,
 					factory,
 					joinFragment,
 					fetchOwner,
-					(CollectionFetch) fetch,
+					collectionFetch,
 					buildingParameters,
-					aliasResolutionContext
+					aliasResolutionContext,
+					readerCollector,
+					stats
 			);
-			final CollectionFetch collectionFetch = (CollectionFetch) fetch;
 			if ( collectionFetch.getIndexGraph() != null ) {
 				processJoinFetches(
 						selectStatementBuilder,
 						factory,
 						joinFragment,
 						collectionFetch.getIndexGraph(),
 						buildingParameters,
-						aliasResolutionContext
+						aliasResolutionContext,
+						readerCollector,
+						stats
 				);
 			}
 			if ( collectionFetch.getElementGraph() != null ) {
 				processJoinFetches(
 						selectStatementBuilder,
 						factory,
 						joinFragment,
 						collectionFetch.getElementGraph(),
 						buildingParameters,
-						aliasResolutionContext
+						aliasResolutionContext,
+						readerCollector,
+						stats
 				);
 			}
 		}
 		else {
 			// could also be a CompositeFetch, we ignore those here
 			// but do still need to visit their fetches...
 			if ( FetchOwner.class.isInstance( fetch ) ) {
 				processJoinFetches(
 						selectStatementBuilder,
 						factory,
 						joinFragment,
 						(FetchOwner) fetch,
 						buildingParameters,
-						aliasResolutionContext
+						aliasResolutionContext,
+						readerCollector,
+						stats
 				);
 			}
 		}
 	}
 
 	private static void processEntityFetch(
 			SelectStatementBuilder selectStatementBuilder,
 			SessionFactoryImplementor factory,
 			JoinFragment joinFragment,
 			FetchOwner fetchOwner,
 			EntityFetch fetch,
 			QueryBuildingParameters buildingParameters,
+			AliasResolutionContext aliasResolutionContext,
+			ReaderCollector readerCollector,
+			FetchStatsImpl stats) {
+		if ( BidirectionalEntityFetch.class.isInstance( fetch ) ) {
+			log.tracef( "Skipping bi-directional entity fetch [%s]", fetch );
+			return;
+		}
+
+		stats.processingFetch( fetch );
+
+		// write the fragments for this fetch to the in-flight SQL builder
+		final EntityReferenceAliases aliases = renderSqlFragments(
+				selectStatementBuilder,
+				factory,
+				joinFragment,
+				fetchOwner,
+				fetch,
+				buildingParameters,
+				aliasResolutionContext
+		);
+
+		// now we build readers as follows:
+		//		1) readers for any fetches that are part of the identifier
+		final EntityIdentifierReader identifierReader = buildIdentifierReader(
+				selectStatementBuilder,
+				factory,
+				joinFragment,
+				fetch,
+				buildingParameters,
+				aliasResolutionContext,
+				readerCollector,
+				aliases,
+				stats
+		);
+
+		//		2) a reader for this fetch itself
+		// 			todo : not sure this distinction really matters aside form the whole "register nullable property" stuff,
+		// 			but not sure we need a distinct class for just that
+		if ( fetch.getFetchedType().isOneToOne() ) {
+			readerCollector.addReader(
+					new OneToOneFetchReader( fetch, aliases, identifierReader, (EntityReference) fetchOwner )
+			);
+		}
+		else {
+			readerCollector.addReader(
+					new EntityReferenceReader( fetch, aliases, identifierReader )
+			);
+		}
+
+		//		3) and then readers for all fetches not part of the identifier
+		processJoinFetches(
+				selectStatementBuilder,
+				factory,
+				joinFragment,
+				fetch,
+				buildingParameters,
+				aliasResolutionContext,
+				readerCollector,
+				stats
+		);
+	}
+
+	/**
+	 * Renders the pieces indicated by the incoming EntityFetch reference into the in-flight SQL statement builder.
+	 *
+	 * @param selectStatementBuilder The builder containing the in-flight SQL query definition.
+	 * @param factory The SessionFactory SPI
+	 * @param joinFragment The in-flight SQL JOIN definition.
+	 * @param fetchOwner The owner of {@code fetch}
+	 * @param fetch The fetch which indicates the information to be rendered.
+	 * @param buildingParameters The settings/options for SQL building
+	 * @param aliasResolutionContext The reference cache for entity/collection aliases
+	 *
+	 * @return The used aliases
+	 */
+	private static EntityReferenceAliases renderSqlFragments(
+			SelectStatementBuilder selectStatementBuilder,
+			SessionFactoryImplementor factory,
+			JoinFragment joinFragment,
+			FetchOwner fetchOwner,
+			EntityFetch fetch,
+			QueryBuildingParameters buildingParameters,
 			AliasResolutionContext aliasResolutionContext) {
-		final String rhsAlias = aliasResolutionContext.resolveAliases( fetch ).getTableAlias();
+		final EntityReferenceAliases aliases = aliasResolutionContext.resolveAliases( fetch );
+
+		final String rhsAlias = aliases.getTableAlias();
 		final String[] rhsColumnNames = JoinHelper.getRHSColumnNames( fetch.getFetchedType(), factory );
 
 		final String lhsTableAlias = resolveLhsTableAlias( fetchOwner, fetch, aliasResolutionContext );
 		// todo : this is not exactly correct.  it assumes the join refers to the LHS PK
 		final String[] aliasedLhsColumnNames = fetch.toSqlSelectFragments( lhsTableAlias );
 
 		final String additionalJoinConditions = resolveAdditionalJoinCondition(
 				factory,
 				rhsAlias,
 				fetchOwner,
 				fetch,
 				buildingParameters.getQueryInfluencers(),
 				aliasResolutionContext
 		);
 
 		final Joinable joinable = (Joinable) fetch.getEntityPersister();
 
 		addJoins(
 				joinFragment,
 				joinable,
 				fetch.isNullable() ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN,
 				rhsAlias,
 				rhsColumnNames,
 				aliasedLhsColumnNames,
 				additionalJoinConditions
 		);
 
 		// the null arguments here relate to many-to-many fetches
 		selectStatementBuilder.appendSelectClauseFragment(
 				joinable.selectFragment(
 						null,
 						null,
 						rhsAlias,
-						aliasResolutionContext.resolveAliases( fetch ).getColumnAliases().getSuffix(),
+						aliases.getColumnAliases().getSuffix(),
 						null,
 						true
 				)
 		);
+
+		return aliases;
+	}
+
+	private static EntityIdentifierReader buildIdentifierReader(
+			SelectStatementBuilder selectStatementBuilder,
+			SessionFactoryImplementor factory,
+			JoinFragment joinFragment,
+			EntityReference entityReference,
+			QueryBuildingParameters buildingParameters,
+			AliasResolutionContext aliasResolutionContext,
+			ReaderCollector readerCollector,
+			EntityReferenceAliases aliases,
+			FetchStatsImpl stats) {
+		final List<EntityReferenceReader> identifierFetchReaders = new ArrayList<EntityReferenceReader>();
+		final ReaderCollector identifierFetchReaderCollector = new ReaderCollector() {
+			@Override
+			public void addReader(CollectionReferenceReader collectionReferenceReader) {
+				throw new IllegalStateException( "Identifier cannot contain collection fetches" );
+			}
+
+			@Override
+			public void addReader(EntityReferenceReader entityReferenceReader) {
+				identifierFetchReaders.add( entityReferenceReader );
+			}
+		};
+		for ( Fetch fetch : entityReference.getIdentifierDescription().getFetches() ) {
+			processFetch(
+					selectStatementBuilder,
+					factory,
+					joinFragment,
+					(FetchOwner) entityReference,
+					fetch,
+					buildingParameters,
+					aliasResolutionContext,
+					identifierFetchReaderCollector,
+					stats
+			);
+		}
+		return new EntityIdentifierReaderImpl(
+				entityReference,
+				aliases,
+				identifierFetchReaders
+		);
+	}
+
+	private static List<EntityReferenceReader> collectIdentifierFetchReaders(
+			EntityReference entityReference,
+			AliasResolutionContext aliasResolutionContext,
+			ReaderCollector readerCollector) {
+		final Type identifierType = entityReference.getEntityPersister().getIdentifierType();
+		if ( ! identifierType.isComponentType() ) {
+			return Collections.emptyList();
+		}
+
+		final Fetch[] fetches = entityReference.getIdentifierDescription().getFetches();
+		if ( fetches == null || fetches.length == 0 ) {
+			return Collections.emptyList();
+		}
+
+		final List<EntityReferenceReader> readers = new ArrayList<EntityReferenceReader>();
+		for ( Fetch fetch : fetches ) {
+			collectIdentifierFetchReaders( aliasResolutionContext, readers, entityReference, fetch, readerCollector );
+		}
+		return readers;
+	}
+
+
+	private static void collectIdentifierFetchReaders(
+			AliasResolutionContext aliasResolutionContext,
+			List<EntityReferenceReader> readers,
+			EntityReference entityReference,
+			Fetch fetch,
+			ReaderCollector readerCollector) {
+		if ( CompositeFetch.class.isInstance( fetch ) ) {
+			for ( Fetch subFetch : ( (CompositeFetch) fetch).getFetches() ) {
+				collectIdentifierFetchReaders( aliasResolutionContext, readers, entityReference, subFetch, readerCollector );
+			}
+		}
+		else if ( ! EntityReference.class.isInstance( fetch ) ) {
+			throw new IllegalStateException(
+					String.format(
+							"Non-entity (and non-composite) fetch [%s] was found as part of entity identifier : %s",
+							fetch,
+							entityReference.getEntityPersister().getEntityName()
+					)
+			);
+		}
+		else {
+			// todo : add a mapping here from EntityReference -> EntityReferenceReader
+			//
+			// need to be careful here about bi-directionality, just not sure how to best check for bi-directionality here.
+			//
+			final EntityReference fetchedEntityReference = (EntityReference) fetch;
+			final EntityReferenceAliases fetchedAliases = aliasResolutionContext.resolveAliases( fetchedEntityReference );
+
+			if ( BidirectionalEntityFetch.class.isInstance( fetchedEntityReference ) ) {
+				return;
+			}
+
+
+			final EntityReferenceReader reader = new EntityReferenceReader(
+					fetchedEntityReference,
+					aliasResolutionContext.resolveAliases( fetchedEntityReference ),
+					new EntityIdentifierReaderImpl(
+							fetchedEntityReference,
+							fetchedAliases,
+							Collections.<EntityReferenceReader>emptyList()
+					)
+			);
+
+			readerCollector.addReader( reader );
+//			readers.add( reader );
+		}
 	}
 
 	private static String[] resolveAliasedLhsJoinColumns(
 			FetchOwner fetchOwner,
 			Fetch fetch,
 			AliasResolutionContext aliasResolutionContext) {
 		// IMPL NOTE : the fetch-owner is the LHS; the fetch is the RHS
 		final String lhsTableAlias = resolveLhsTableAlias( fetchOwner, fetch, aliasResolutionContext );
 		return fetch.toSqlSelectFragments( lhsTableAlias );
 	}
 
 	private static String resolveLhsTableAlias(
 			FetchOwner fetchOwner,
 			Fetch fetch,
 			AliasResolutionContext aliasResolutionContext) {
 		// IMPL NOTE : the fetch-owner is the LHS; the fetch is the RHS
 
 		if ( AnyFetch.class.isInstance( fetchOwner ) ) {
 			throw new WalkingException( "Any type should never be joined!" );
 		}
 		else if ( EntityReference.class.isInstance( fetchOwner ) ) {
 			return aliasResolutionContext.resolveAliases( (EntityReference) fetchOwner ).getTableAlias();
 		}
 		else if ( CompositeFetch.class.isInstance( fetchOwner ) ) {
 			return aliasResolutionContext.resolveAliases(
 					locateCompositeFetchEntityReferenceSource( (CompositeFetch) fetchOwner )
 			).getTableAlias();
 		}
 		else if ( CompositeElementGraph.class.isInstance( fetchOwner ) ) {
 			final CompositeElementGraph compositeElementGraph = (CompositeElementGraph) fetchOwner;
 			return aliasResolutionContext.resolveAliases( compositeElementGraph.getCollectionReference() ).getCollectionTableAlias();
 		}
 		else if ( CompositeIndexGraph.class.isInstance( fetchOwner ) ) {
 			final CompositeIndexGraph compositeIndexGraph = (CompositeIndexGraph) fetchOwner;
 			return aliasResolutionContext.resolveAliases( compositeIndexGraph.getCollectionReference() ).getCollectionTableAlias();
 		}
 		else {
 			throw new NotYetImplementedException( "Cannot determine LHS alias for FetchOwner." );
 		}
 	}
 
 	private static EntityReference locateCompositeFetchEntityReferenceSource(CompositeFetch composite) {
 		final FetchOwner owner = composite.getOwner();
 		if ( EntityReference.class.isInstance( owner ) ) {
 			return (EntityReference) owner;
 		}
 		if ( CompositeFetch.class.isInstance( owner ) ) {
 			return locateCompositeFetchEntityReferenceSource( (CompositeFetch) owner );
 		}
 
 		throw new WalkingException( "Cannot resolve entity source for a CompositeFetch" );
 	}
 
 	private static String resolveAdditionalJoinCondition(
 			SessionFactoryImplementor factory,
 			String rhsTableAlias,
 			FetchOwner fetchOwner,
 			Fetch fetch,
 			LoadQueryInfluencers influencers,
 			AliasResolutionContext aliasResolutionContext) {
 		final String withClause = StringHelper.isEmpty( fetch.getAdditionalJoinConditions() )
 				? ""
 				: " and ( " + fetch.getAdditionalJoinConditions() + " )";
 		return ( (AssociationType) fetch.getFetchedType() ).getOnCondition(
 				rhsTableAlias,
 				factory,
 				influencers.getEnabledFilters()
 		) + withClause;
 	}
 
 	private static void addJoins(
 			JoinFragment joinFragment,
 			Joinable joinable,
 			JoinType joinType,
 			String rhsAlias,
 			String[] rhsColumnNames,
 			String[] aliasedLhsColumnNames,
 			String additionalJoinConditions) {
 		joinFragment.addJoin(
 				joinable.getTableName(),
 				rhsAlias,
 				aliasedLhsColumnNames,
 				rhsColumnNames,
 				joinType,
 				additionalJoinConditions
 		);
 		joinFragment.addJoins(
 				joinable.fromJoinFragment( rhsAlias, false, true ),
 				joinable.whereJoinFragment( rhsAlias, false, true )
 		);
 	}
 
 	private static void processCollectionFetch(
 			SelectStatementBuilder selectStatementBuilder,
 			SessionFactoryImplementor factory,
 			JoinFragment joinFragment,
 			FetchOwner fetchOwner,
 			CollectionFetch fetch,
 			QueryBuildingParameters buildingParameters,
-			AliasResolutionContext aliasResolutionContext) {
-		final QueryableCollection queryableCollection = (QueryableCollection) fetch.getCollectionPersister();
-		final Joinable joinableCollection = (Joinable) fetch.getCollectionPersister();
+			AliasResolutionContext aliasResolutionContext,
+			ReaderCollector readerCollector,
+			FetchStatsImpl stats) {
+		stats.processingFetch( fetch );
+
+		final CollectionReferenceAliases aliases = aliasResolutionContext.resolveAliases( fetch );
 
 		if ( fetch.getCollectionPersister().isManyToMany() ) {
+			final QueryableCollection queryableCollection = (QueryableCollection) fetch.getCollectionPersister();
+			final Joinable joinableCollection = (Joinable) fetch.getCollectionPersister();
+
 			// for many-to-many we have 3 table aliases.  By way of example, consider a normal m-n: User<->Role
 			// where User is the FetchOwner and Role (User.roles) is the Fetch.  We'd have:
 			//		1) the owner's table : user
 			final String ownerTableAlias = resolveLhsTableAlias( fetchOwner, fetch, aliasResolutionContext );
 			//		2) the m-n table : user_role
-			final String collectionTableAlias = aliasResolutionContext.resolveAliases( fetch ).getCollectionTableAlias();
+			final String collectionTableAlias = aliases.getCollectionTableAlias();
 			//		3) the element table : role
-			final String elementTableAlias = aliasResolutionContext.resolveAliases( fetch ).getElementTableAlias();
+			final String elementTableAlias = aliases.getElementTableAlias();
 
 			{
 				// add join fragments from the owner table -> collection table ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 				final String filterFragment = ( (Joinable) fetch.getCollectionPersister() ).filterFragment(
 						collectionTableAlias,
 						buildingParameters.getQueryInfluencers().getEnabledFilters()
 				);
 
 				joinFragment.addJoin(
 						joinableCollection.getTableName(),
 						collectionTableAlias,
 						StringHelper.qualify( ownerTableAlias, extractJoinable( fetchOwner ).getKeyColumnNames() ),
 						queryableCollection.getKeyColumnNames(),
 						fetch.isNullable() ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN,
 						filterFragment
 				);
 				joinFragment.addJoins(
 						joinableCollection.fromJoinFragment( collectionTableAlias, false, true ),
 						joinableCollection.whereJoinFragment( collectionTableAlias, false, true )
 				);
 
 				// add select fragments from the collection table ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 				selectStatementBuilder.appendSelectClauseFragment(
 						joinableCollection.selectFragment(
 								(Joinable) queryableCollection.getElementPersister(),
 								ownerTableAlias,
 								collectionTableAlias,
-								aliasResolutionContext.resolveAliases( fetch ).getEntityElementColumnAliases().getSuffix(),
-								aliasResolutionContext.resolveAliases( fetch ).getCollectionColumnAliases().getSuffix(),
+								aliases.getEntityElementColumnAliases().getSuffix(),
+								aliases.getCollectionColumnAliases().getSuffix(),
 								true
 						)
 				);
 			}
 
 			{
 				// add join fragments from the collection table -> element entity table ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 				final String additionalJoinConditions = resolveAdditionalJoinCondition(
 						factory,
 						elementTableAlias,
 						fetchOwner,
 						fetch,
 						buildingParameters.getQueryInfluencers(),
 						aliasResolutionContext
 				);
 
 				final String manyToManyFilter = fetch.getCollectionPersister().getManyToManyFilterFragment(
 						collectionTableAlias,
 						buildingParameters.getQueryInfluencers().getEnabledFilters()
 				);
 
 				final String condition;
 				if ( "".equals( manyToManyFilter ) ) {
 					condition = additionalJoinConditions;
 				}
 				else if ( "".equals( additionalJoinConditions ) ) {
 					condition = manyToManyFilter;
 				}
 				else {
 					condition = additionalJoinConditions + " and " + manyToManyFilter;
 				}
 
 				final OuterJoinLoadable elementPersister = (OuterJoinLoadable) queryableCollection.getElementPersister();
 
 				addJoins(
 						joinFragment,
 						elementPersister,
 //						JoinType.INNER_JOIN,
 						JoinType.LEFT_OUTER_JOIN,
 						elementTableAlias,
 						elementPersister.getIdentifierColumnNames(),
 						StringHelper.qualify( collectionTableAlias, queryableCollection.getElementColumnNames() ),
 						condition
 				);
 
 				// add select fragments from the element entity table ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-				final CollectionReferenceAliases aliases = aliasResolutionContext.resolveAliases( fetch );
 				selectStatementBuilder.appendSelectClauseFragment(
 						elementPersister.selectFragment(
 								aliases.getElementTableAlias(),
 								aliases.getEntityElementColumnAliases().getSuffix()
 						)
 				);
 			}
 
 			final String manyToManyOrdering = queryableCollection.getManyToManyOrderByString( collectionTableAlias );
 			if ( StringHelper.isNotEmpty( manyToManyOrdering ) ) {
 				selectStatementBuilder.appendOrderByFragment( manyToManyOrdering );
 			}
 
 			final String ordering = queryableCollection.getSQLOrderByString( collectionTableAlias );
 			if ( StringHelper.isNotEmpty( ordering ) ) {
 				selectStatementBuilder.appendOrderByFragment( ordering );
 			}
+
+
+			final EntityReferenceAliases entityReferenceAliases = new EntityReferenceAliases() {
+				@Override
+				public String getTableAlias() {
+					return aliases.getElementTableAlias();
+				}
+
+				@Override
+				public EntityAliases getColumnAliases() {
+					return aliases.getEntityElementColumnAliases();
+				}
+			};
+
+			final EntityReference elementEntityReference = (EntityReference) fetch.getElementGraph();
+			readerCollector.addReader(
+					new EntityReferenceReader(
+							elementEntityReference,
+							entityReferenceAliases,
+							buildIdentifierReader(
+									selectStatementBuilder,
+									factory,
+									joinFragment,
+									elementEntityReference,
+									buildingParameters,
+									aliasResolutionContext,
+									readerCollector,
+									entityReferenceAliases,
+									stats
+							)
+					)
+			);
 		}
 		else {
-			final String rhsTableAlias = aliasResolutionContext.resolveAliases( fetch ).getElementTableAlias();
+			final QueryableCollection queryableCollection = (QueryableCollection) fetch.getCollectionPersister();
+			final Joinable joinableCollection = (Joinable) fetch.getCollectionPersister();
+
+			final String rhsTableAlias = aliases.getElementTableAlias();
 			final String[] rhsColumnNames = JoinHelper.getRHSColumnNames( fetch.getFetchedType(), factory );
 
 			final String lhsTableAlias = resolveLhsTableAlias( fetchOwner, fetch, aliasResolutionContext );
 			// todo : this is not exactly correct.  it assumes the join refers to the LHS PK
 			final String[] aliasedLhsColumnNames = fetch.toSqlSelectFragments( lhsTableAlias );
 
 			final String on = resolveAdditionalJoinCondition(
 					factory,
 					rhsTableAlias,
 					fetchOwner,
 					fetch,
 					buildingParameters.getQueryInfluencers(),
 					aliasResolutionContext
 			);
 
 			addJoins(
 					joinFragment,
 					joinableCollection,
 					fetch.isNullable() ? JoinType.LEFT_OUTER_JOIN : JoinType.INNER_JOIN,
 					rhsTableAlias,
 					rhsColumnNames,
 					aliasedLhsColumnNames,
 					on
 			);
 
 			// select the "collection columns"
 			selectStatementBuilder.appendSelectClauseFragment(
 					queryableCollection.selectFragment(
 							rhsTableAlias,
-							aliasResolutionContext.resolveAliases( fetch ).getCollectionColumnAliases().getSuffix()
+							aliases.getCollectionColumnAliases().getSuffix()
 					)
 			);
 
 			if ( fetch.getCollectionPersister().isOneToMany() ) {
 				// if the collection elements are entities, select the entity columns as well
-				final CollectionReferenceAliases aliases = aliasResolutionContext.resolveAliases( fetch );
 				final OuterJoinLoadable elementPersister = (OuterJoinLoadable) queryableCollection.getElementPersister();
 				selectStatementBuilder.appendSelectClauseFragment(
 						elementPersister.selectFragment(
 								aliases.getElementTableAlias(),
 								aliases.getEntityElementColumnAliases().getSuffix()
 						)
 				);
+
+				final EntityReferenceAliases entityReferenceAliases = new EntityReferenceAliases() {
+					@Override
+					public String getTableAlias() {
+						return aliases.getElementTableAlias();
+					}
+
+					@Override
+					public EntityAliases getColumnAliases() {
+						return aliases.getEntityElementColumnAliases();
+					}
+				};
+
+				final EntityReference elementEntityReference = (EntityReference) fetch.getElementGraph();
+				readerCollector.addReader(
+						new EntityReferenceReader(
+								elementEntityReference,
+								entityReferenceAliases,
+								buildIdentifierReader(
+										selectStatementBuilder,
+										factory,
+										joinFragment,
+										elementEntityReference,
+										buildingParameters,
+										aliasResolutionContext,
+										readerCollector,
+										entityReferenceAliases,
+										stats
+								)
+						)
+				);
 			}
 
 			final String ordering = queryableCollection.getSQLOrderByString( rhsTableAlias );
 			if ( StringHelper.isNotEmpty( ordering ) ) {
 				selectStatementBuilder.appendOrderByFragment( ordering );
 			}
 		}
 
+		readerCollector.addReader( new CollectionReferenceReader( fetch, aliases ) );
 	}
 
 	private static Joinable extractJoinable(FetchOwner fetchOwner) {
 		// this is used for collection fetches.  At the end of the day, a fetched collection must be owned by
 		// an entity.  Find that entity's persister and return it
 		if ( EntityReference.class.isInstance( fetchOwner ) ) {
 			return (Joinable) ( (EntityReference) fetchOwner ).getEntityPersister();
 		}
 		else if ( CompositeFetch.class.isInstance( fetchOwner ) ) {
 			return (Joinable) locateCompositeFetchEntityReferenceSource( (CompositeFetch) fetchOwner ).getEntityPersister();
 		}
 		else if ( EntityElementGraph.class.isInstance( fetchOwner ) ) {
 			return (Joinable) ( (EntityElementGraph) fetchOwner ).getEntityPersister();
 		}
 
 		throw new IllegalStateException( "Uncertain how to extract Joinable from given FetchOwner : " + fetchOwner );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/package-info.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/package-info.java
new file mode 100644
index 0000000000..57302dcd75
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * This package supports converting a LoadPlan to SQL and generating readers for the resulting ResultSet
+ */
+package org.hibernate.loader.plan.exec;
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReferenceReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReferenceReader.java
index bf340f853f..f838560805 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReferenceReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReferenceReader.java
@@ -1,155 +1,153 @@
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
 package org.hibernate.loader.plan.exec.process.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
 import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
 import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.pretty.MessageHelper;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionReferenceReader {
 	private static final Logger log = CoreLogging.logger( CollectionReferenceReader.class );
 
 	private final CollectionReference collectionReference;
+	private final CollectionReferenceAliases aliases;
 
-	public CollectionReferenceReader(CollectionReference collectionReference) {
+	public CollectionReferenceReader(CollectionReference collectionReference, CollectionReferenceAliases aliases) {
 		this.collectionReference = collectionReference;
+		this.aliases = aliases;
 	}
 
 	public void finishUpRow(ResultSet resultSet, ResultSetProcessingContextImpl context) {
-		final CollectionReferenceAliases aliases = context.getAliasResolutionContext().resolveAliases(
-				collectionReference
-		);
-
 		try {
 			// read the collection key for this reference for the current row.
 			final PersistenceContext persistenceContext = context.getSession().getPersistenceContext();
 			final Serializable collectionRowKey = (Serializable) collectionReference.getCollectionPersister().readKey(
 					resultSet,
 					aliases.getCollectionColumnAliases().getSuffixedKeyAliases(),
 					context.getSession()
 			);
 
 			if ( collectionRowKey != null ) {
 				// we found a collection element in the result set
 
 				if ( log.isDebugEnabled() ) {
 					log.debugf(
 							"Found row of collection: %s",
 							MessageHelper.collectionInfoString(
 									collectionReference.getCollectionPersister(),
 									collectionRowKey,
 									context.getSession().getFactory()
 							)
 					);
 				}
 
 				Object collectionOwner = findCollectionOwner( collectionRowKey, resultSet, context );
 
 				PersistentCollection rowCollection = persistenceContext.getLoadContexts()
 						.getCollectionLoadContext( resultSet )
 						.getLoadingCollection( collectionReference.getCollectionPersister(), collectionRowKey );
 
 				if ( rowCollection != null ) {
 					rowCollection.readFrom(
 							resultSet,
 							collectionReference.getCollectionPersister(),
 							aliases.getCollectionColumnAliases(),
 							collectionOwner
 					);
 				}
 
 			}
 			else {
 				final Serializable optionalKey = findCollectionOwnerKey( context );
 				if ( optionalKey != null ) {
 					// we did not find a collection element in the result set, so we
 					// ensure that a collection is created with the owner's identifier,
 					// since what we have is an empty collection
 					if ( log.isDebugEnabled() ) {
 						log.debugf(
 								"Result set contains (possibly empty) collection: %s",
 								MessageHelper.collectionInfoString(
 										collectionReference.getCollectionPersister(),
 										optionalKey,
 										context.getSession().getFactory()
 								)
 						);
 					}
 					// handle empty collection
 					persistenceContext.getLoadContexts()
 							.getCollectionLoadContext( resultSet )
 							.getLoadingCollection( collectionReference.getCollectionPersister(), optionalKey );
 
 				}
 			}
 			// else no collection element, but also no owner
 		}
 		catch ( SQLException sqle ) {
 			// TODO: would be nice to have the SQL string that failed...
 			throw context.getSession().getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not read next row of results"
 			);
 		}
 
 	}
 
 	protected Object findCollectionOwner(
 			Serializable collectionRowKey,
 			ResultSet resultSet,
 			ResultSetProcessingContextImpl context) {
 		final Object collectionOwner = context.getSession().getPersistenceContext().getCollectionOwner(
 				collectionRowKey,
 				collectionReference.getCollectionPersister()
 		);
 		// todo : try org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext.getOwnerProcessingState() ??
 		//			-- specifically to return its ResultSetProcessingContext.EntityReferenceProcessingState#getEntityInstance()
 		if ( collectionOwner == null ) {
 			//TODO: This is assertion is disabled because there is a bug that means the
 			//	  original owner of a transient, uninitialized collection is not known
 			//	  if the collection is re-referenced by a different object associated
 			//	  with the current Session
 			//throw new AssertionFailure("bug loading unowned collection");
 		}
 		return collectionOwner;
 	}
 
 	protected Serializable findCollectionOwnerKey(ResultSetProcessingContext context) {
 		return null;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReturnReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReturnReader.java
index c71bfbec54..f78f38af5b 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReturnReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/CollectionReturnReader.java
@@ -1,74 +1,75 @@
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
 package org.hibernate.loader.plan.exec.process.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
 import org.hibernate.loader.plan.exec.process.spi.ReturnReader;
+import org.hibernate.loader.plan.exec.spi.CollectionReferenceAliases;
 import org.hibernate.loader.plan.spi.CollectionReturn;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionReturnReader extends CollectionReferenceReader implements ReturnReader {
 	private final CollectionReturn collectionReturn;
 
-	public CollectionReturnReader(CollectionReturn collectionReturn) {
-		super( collectionReturn );
+	public CollectionReturnReader(CollectionReturn collectionReturn, CollectionReferenceAliases aliases) {
+		super( collectionReturn, aliases );
 		this.collectionReturn = collectionReturn;
 	}
 
 	@Override
 	protected Object findCollectionOwner(
 			Serializable collectionRowKey,
 			ResultSet resultSet,
 			ResultSetProcessingContextImpl context) {
 		if ( context.shouldUseOptionalEntityInformation() ) {
 			final Object optionalEntityInstance = context.getQueryParameters().getOptionalObject();
 			if ( optionalEntityInstance != null ) {
 				return optionalEntityInstance;
 			}
 		}
 		return super.findCollectionOwner( collectionRowKey, resultSet, context );
 	}
 
 	@Override
 	protected Serializable findCollectionOwnerKey(ResultSetProcessingContext context) {
 		final EntityKey entityKey = context.shouldUseOptionalEntityInformation()
 				? ResultSetProcessorHelper.getOptionalObjectKey( context.getQueryParameters(), context.getSession() )
 				: null;
 		return entityKey == null
 				? super.findCollectionOwnerKey( context )
 				: entityKey;
 	}
 
 	@Override
 	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		return null;  //To change body of implemented methods use File | Settings | File Templates.
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityIdentifierReaderImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityIdentifierReaderImpl.java
index 254612bebf..c308c27a55 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityIdentifierReaderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityIdentifierReaderImpl.java
@@ -1,326 +1,284 @@
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
 package org.hibernate.loader.plan.exec.process.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
-import java.util.ArrayList;
-import java.util.Collections;
 import java.util.List;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
-import org.hibernate.JDBCException;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
-import org.hibernate.loader.plan.spi.CompositeFetch;
+import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.EntityReturn;
-import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.loader.plan.spi.FetchOwner;
-import org.hibernate.persister.spi.HydratedCompoundValueHandler;
 import org.hibernate.persister.walking.internal.FetchStrategyHelper;
 import org.hibernate.persister.walking.spi.WalkingException;
 import org.hibernate.type.Type;
 
-import static org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext.*;
+import static org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext.EntityReferenceProcessingState;
 
 /**
  * Encapsulates the logic for reading a single entity identifier from a JDBC ResultSet, including support for fetches
  * that are part of the identifier.
  *
  * @author Steve Ebersole
  */
-class EntityIdentifierReaderImpl implements EntityIdentifierReader {
+public class EntityIdentifierReaderImpl implements EntityIdentifierReader {
 	private static final Logger log = CoreLogging.logger( EntityIdentifierReaderImpl.class );
 
 	private final EntityReference entityReference;
-
-	private List<EntityReferenceReader> identifierFetchReaders;
+	private final EntityReferenceAliases aliases;
+	private final List<EntityReferenceReader> identifierFetchReaders;
 
 	private final boolean isReturn;
 	private final Type identifierType;
 
 	/**
 	 * Creates a delegate capable of performing the reading of an entity identifier
 	 *
 	 * @param entityReference The entity reference for which we will be reading the identifier.
 	 */
-	EntityIdentifierReaderImpl(EntityReference entityReference) {
+	public EntityIdentifierReaderImpl(
+			EntityReference entityReference,
+			EntityReferenceAliases aliases,
+			List<EntityReferenceReader> identifierFetchReaders) {
 		this.entityReference = entityReference;
+		this.aliases = aliases;
 		this.isReturn = EntityReturn.class.isInstance( entityReference );
 		this.identifierType = entityReference.getEntityPersister().getIdentifierType();
-
-		identifierFetchReaders = collectIdentifierFetchReaders();
-	}
-
-	private List<EntityReferenceReader> collectIdentifierFetchReaders() {
-		if ( ! identifierType.isComponentType() ) {
-			return Collections.emptyList();
-		}
-		final Fetch[] fetches = entityReference.getIdentifierDescription().getFetches();
-		if ( fetches == null || fetches.length == 0 ) {
-			return Collections.emptyList();
-		}
-
-		final List<EntityReferenceReader> readers = new ArrayList<EntityReferenceReader>();
-		for ( Fetch fetch : fetches ) {
-			collectIdentifierFetchReaders( readers, fetch );
-		}
-		return readers;
-	}
-
-	private void collectIdentifierFetchReaders(List<EntityReferenceReader> readers, Fetch fetch) {
-		if ( CompositeFetch.class.isInstance( fetch ) ) {
-			for ( Fetch subFetch : ( (CompositeFetch) fetch).getFetches() ) {
-				collectIdentifierFetchReaders( readers, subFetch );
-			}
-		}
-		else if ( ! EntityFetch.class.isInstance( fetch ) ) {
-			throw new IllegalStateException(
-					String.format(
-							"non-entity (and non-composite) fetch [%s] was found as part of entity identifier : %s",
-							fetch,
-							entityReference.getEntityPersister().getEntityName()
-					)
-			);
-		}
-		else {
-			final EntityReference fetchedEntityReference = (EntityReference) fetch;
-			readers.add( new EntityReferenceReader( fetchedEntityReference ) );
-		}
+		this.identifierFetchReaders = identifierFetchReaders;
 	}
 
 	@Override
 	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		final EntityReferenceProcessingState processingState = context.getProcessingState( entityReference );
 
 		// if the entity reference we are hydrating is a Return, it is possible that its EntityKey is
 		// supplied by the QueryParameter optional entity information
 		if ( context.shouldUseOptionalEntityInformation() ) {
 			if ( isReturn ) {
 				final EntityKey entityKey = ResultSetProcessorHelper.getOptionalObjectKey(
 						context.getQueryParameters(),
 						context.getSession()
 				);
 
 				if ( entityKey != null ) {
 					processingState.registerEntityKey( entityKey );
 					return;
 				}
 			}
 		}
 
 		// get any previously registered identifier hydrated-state
 		Object identifierHydratedForm = processingState.getIdentifierHydratedForm();
 		if ( identifierHydratedForm == null ) {
 			// if there is none, read it from the result set
 			identifierHydratedForm = readIdentifierHydratedState( resultSet, context );
 
 			// broadcast the fact that a hydrated identifier value just became associated with
 			// this entity reference
 			processingState.registerIdentifierHydratedForm( identifierHydratedForm );
 //			hydrateIdentifierFetchIdentifiers( resultSet, context, identifierHydratedForm );
 			for ( EntityReferenceReader reader : identifierFetchReaders ) {
 				reader.hydrateIdentifier( resultSet, context );
 			}
 		}
 	}
 
 	/**
 	 * Read the identifier state for the entity reference for the currently processing row in the ResultSet
 	 *
 	 * @param resultSet The ResultSet being processed
 	 * @param context The processing context
 	 *
 	 * @return The hydrated state
 	 *
 	 * @throws java.sql.SQLException Indicates a problem accessing the ResultSet
 	 */
 	private Object readIdentifierHydratedState(ResultSet resultSet, ResultSetProcessingContext context)
 			throws SQLException {
 //		if ( EntityReturn.class.isInstance( entityReference ) ) {
 //			// if there is a "optional entity key" associated with the context it would pertain to this
 //			// entity reference, because it is the root return.
 //			final EntityKey suppliedEntityKey = context.getSuppliedOptionalEntityKey();
 //			if ( suppliedEntityKey != null ) {
 //				return suppliedEntityKey.getIdentifier();
 //			}
 //		}
 
 		// Otherwise, read it from the ResultSet
 		final String[] columnNames;
 		if ( EntityFetch.class.isInstance( entityReference )
 				&& !FetchStrategyHelper.isJoinFetched( ((EntityFetch) entityReference).getFetchStrategy() ) ) {
 			final EntityFetch fetch = (EntityFetch) entityReference;
 			final FetchOwner fetchOwner = fetch.getOwner();
 			if ( EntityReference.class.isInstance( fetchOwner ) ) {
 				throw new NotYetImplementedException();
 //					final EntityReference ownerEntityReference = (EntityReference) fetchOwner;
 //					final EntityAliases ownerEntityAliases = context.getAliasResolutionContext()
 //							.resolveEntityColumnAliases( ownerEntityReference );
 //					final int propertyIndex = ownerEntityReference.getEntityPersister()
 //							.getEntityMetamodel()
 //							.getPropertyIndex( fetch.getOwnerPropertyName() );
 //					columnNames = ownerEntityAliases.getSuffixedPropertyAliases()[ propertyIndex ];
 			}
 			else {
 				// todo : better message here...
 				throw new WalkingException( "Cannot locate association column names" );
 			}
 		}
 		else {
-			columnNames = context.getAliasResolutionContext()
-					.resolveAliases( entityReference )
-					.getColumnAliases()
-					.getSuffixedKeyAliases();
+			columnNames = aliases.getColumnAliases().getSuffixedKeyAliases();
 		}
 
 		try {
 			return entityReference.getEntityPersister().getIdentifierType().hydrate(
 					resultSet,
 					columnNames,
 					context.getSession(),
 					null
 			);
 		}
 		catch (Exception e) {
 			throw new HibernateException(
 					"Encountered problem trying to hydrate identifier for entity ["
 							+ entityReference.getEntityPersister() + "]",
 					e
 
 			);
 		}
 	}
 
 //	/**
 //	 * Hydrate the identifiers of all fetches that are part of this entity reference's identifier (key-many-to-one).
 //	 *
 //	 * @param resultSet The ResultSet
 //	 * @param context The processing context
 //	 * @param hydratedIdentifierState The hydrated identifier state of the entity reference.  We can extract the
 //	 * fetch identifier's hydrated state from there if available, without having to read the Result (which can
 //	 * be a performance problem on some drivers).
 //	 */
 //	private void hydrateIdentifierFetchIdentifiers(
 //			ResultSet resultSet,
 //			ResultSetProcessingContext context,
 //			Object hydratedIdentifierState) throws SQLException {
 //		// for all fetches that are part of our identifier...
 //		for ( Fetch fetch : entityReference.getIdentifierDescription().getFetches() ) {
 //			hydrateIdentifierFetchIdentifier( resultSet, context, fetch, hydratedIdentifierState );
 //		}
 //	}
 //
 //	private void hydrateIdentifierFetchIdentifier(
 //			ResultSet resultSet,
 //			ResultSetProcessingContext context,
 //			Fetch fetch,
 //			Object hydratedIdentifierState) throws SQLException {
 //		if ( CompositeFetch.class.isInstance( fetch ) ) {
 //			for ( Fetch subFetch : ( (CompositeFetch) fetch).getFetches() ) {
 //				hydrateIdentifierFetchIdentifier( resultSet, context, subFetch, hydratedIdentifierState );
 //			}
 //		}
 //		else if ( ! EntityFetch.class.isInstance( fetch ) ) {
 //			throw new NotYetImplementedException( "Cannot hydrate identifier Fetch that is not an EntityFetch" );
 //		}
 //		else {
 //			final EntityFetch entityFetch = (EntityFetch) fetch;
 //			final EntityReferenceProcessingState fetchProcessingState = context.getProcessingState( entityFetch );
 //
 //			// if the identifier for the fetch was already hydrated, nothing to do
 //			if ( fetchProcessingState.getIdentifierHydratedForm() != null ) {
 //				return;
 //			}
 //
 //			// we can either hydrate the fetch's identifier from the incoming 'hydratedIdentifierState' (by
 //			// extracting the relevant portion using HydratedCompoundValueHandler) or we can
 //			// read it from the ResultSet
 //			if ( hydratedIdentifierState != null ) {
 //				final HydratedCompoundValueHandler hydratedStateHandler = entityReference.getIdentifierDescription().getHydratedStateHandler( fetch );
 //				if ( hydratedStateHandler != null ) {
 //					final Serializable extracted = (Serializable) hydratedStateHandler.extract( hydratedIdentifierState );
 //					fetchProcessingState.registerIdentifierHydratedForm( extracted );
 //				}
 //			}
 //			else {
 //				// Use a reader to hydrate the fetched entity.
 //				//
 //				// todo : Ideally these should be kept around
 //				new EntityReferenceReader( entityFetch ).hydrateIdentifier( resultSet, context );
 //			}
 //		}
 //	}
 
 
 	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-		// resolve fetched state from the identifier first
-		for ( EntityReferenceReader reader : identifierFetchReaders ) {
-			reader.resolveEntityKey( resultSet, context );
-		}
-		for ( EntityReferenceReader reader : identifierFetchReaders ) {
-			reader.hydrateEntityState( resultSet, context );
-		}
+//		// resolve fetched state from the identifier first
+//		for ( EntityReferenceReader reader : identifierFetchReaders ) {
+//			reader.resolveEntityKey( resultSet, context );
+//		}
+//		for ( EntityReferenceReader reader : identifierFetchReaders ) {
+//			reader.hydrateEntityState( resultSet, context );
+//		}
 
 		final EntityReferenceProcessingState processingState = context.getProcessingState( entityReference );
 
 		// see if we already have an EntityKey associated with this EntityReference in the processing state.
 		// if we do, this should have come from the optional entity identifier...
 		final EntityKey entityKey = processingState.getEntityKey();
 		if ( entityKey != null ) {
 			log.debugf(
 					"On call to EntityIdentifierReaderImpl#resolve [for %s], EntityKey was already known; " +
 							"should only happen on root returns with an optional identifier specified"
 			);
 			return;
 		}
 
 		// Look for the hydrated form
 		final Object identifierHydratedForm = processingState.getIdentifierHydratedForm();
 		if ( identifierHydratedForm == null ) {
 			// we need to register the missing identifier, but that happens later after all readers have had a chance
 			// to resolve its EntityKey
 			return;
 		}
 
 		final Type identifierType = entityReference.getEntityPersister().getIdentifierType();
 		final Serializable resolvedId = (Serializable) identifierType.resolve(
 				identifierHydratedForm,
 				context.getSession(),
 				null
 		);
 		if ( resolvedId != null ) {
 			processingState.registerEntityKey(
 					context.getSession().generateEntityKey( resolvedId, entityReference.getEntityPersister() )
 			);
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReferenceReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReferenceReader.java
index 1fe9c8daf3..fff80a7353 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReferenceReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReferenceReader.java
@@ -1,436 +1,436 @@
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
 package org.hibernate.loader.plan.exec.process.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.LockMode;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
 import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
-import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 import static org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext.EntityReferenceProcessingState;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityReferenceReader {
 	private static final Logger log = CoreLogging.logger( EntityReferenceReader.class );
 
 	private final EntityReference entityReference;
+	private final EntityReferenceAliases entityReferenceAliases;
 	private final EntityIdentifierReader identifierReader;
 
 	private final boolean isReturn;
 
-
-	protected EntityReferenceReader(EntityReference entityReference, EntityIdentifierReader identifierReader) {
+	public EntityReferenceReader(
+			EntityReference entityReference,
+			EntityReferenceAliases entityReferenceAliases,
+			EntityIdentifierReader identifierReader) {
 		this.entityReference = entityReference;
+		this.entityReferenceAliases = entityReferenceAliases;
 		this.identifierReader = identifierReader;
 
 		this.isReturn = EntityReturn.class.isInstance( entityReference );
 	}
 
-	public EntityReferenceReader(EntityReference entityReference) {
-		this( entityReference, new EntityIdentifierReaderImpl( entityReference ) );
-	}
-
 	public EntityReference getEntityReference() {
 		return entityReference;
 	}
 
 	public void hydrateIdentifier(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		identifierReader.hydrate( resultSet, context );
 	}
 
 	public void resolveEntityKey(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		identifierReader.resolve( resultSet, context );
 	}
 
 	public void hydrateEntityState(ResultSet resultSet, ResultSetProcessingContext context) {
 		// hydrate the entity reference.  at this point it is expected that
 
 		final EntityReferenceProcessingState processingState = context.getProcessingState( entityReference );
 
 		// If there is no identifier for this entity reference for this row, nothing to do
 		if ( processingState.isMissingIdentifier() ) {
 			handleMissingIdentifier( context );
 			return;
 		}
 
 		// make sure we have the EntityKey
 		final EntityKey entityKey = processingState.getEntityKey();
 		if ( entityKey == null ) {
 			handleMissingIdentifier( context );
 			return;
 		}
 
 		// Have we already hydrated this entity's state?
 		if ( processingState.getEntityInstance() != null ) {
 			return;
 		}
 
 
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// In getting here, we know that:
 		// 		1) We need to hydrate the entity state
 		//		2) We have a valid EntityKey for the entity
 
 		// see if we have an existing entry in the session for this EntityKey
 		final Object existing = context.getSession().getEntityUsingInterceptor( entityKey );
 		if ( existing != null ) {
 			// It is previously associated with the Session, perform some checks
 			if ( ! entityReference.getEntityPersister().isInstance( existing ) ) {
 				throw new WrongClassException(
 						"loaded object was of wrong class " + existing.getClass(),
 						entityKey.getIdentifier(),
 						entityReference.getEntityPersister().getEntityName()
 				);
 			}
 			checkVersion( resultSet, context, entityKey, existing );
 
 			// use the existing association as the hydrated state
 			processingState.registerEntityInstance( existing );
 			return;
 		}
 
 		// Otherwise, we need to load it from the ResultSet...
 
 		// determine which entity instance to use.  Either the supplied one, or instantiate one
 		Object optionalEntityInstance = null;
 		if ( isReturn && context.shouldUseOptionalEntityInformation() ) {
 			final EntityKey optionalEntityKey = ResultSetProcessorHelper.getOptionalObjectKey(
 					context.getQueryParameters(),
 					context.getSession()
 			);
 			if ( optionalEntityKey != null ) {
 				if ( optionalEntityKey.equals( entityKey ) ) {
 					optionalEntityInstance = context.getQueryParameters().getOptionalObject();
 				}
 			}
 		}
 
 		final String concreteEntityTypeName = getConcreteEntityTypeName( resultSet, context, entityKey );
 
 		final Object entityInstance = optionalEntityInstance != null
 				? optionalEntityInstance
 				: context.getSession().instantiate( concreteEntityTypeName, entityKey.getIdentifier() );
 
 		processingState.registerEntityInstance( entityInstance );
 
 		// need to hydrate it.
 		// grab its state from the ResultSet and keep it in the Session
 		// (but don't yet initialize the object itself)
 		// note that we acquire LockMode.READ even if it was not requested
 		log.trace( "hydrating entity state" );
 		final LockMode requestedLockMode = context.resolveLockMode( entityReference );
 		final LockMode lockModeToAcquire = requestedLockMode == LockMode.NONE
 				? LockMode.READ
 				: requestedLockMode;
 
 		loadFromResultSet(
 				resultSet,
 				context,
 				entityInstance,
 				concreteEntityTypeName,
 				entityKey,
 				lockModeToAcquire
 		);
 	}
 
 	private void handleMissingIdentifier(ResultSetProcessingContext context) {
 		if ( EntityFetch.class.isInstance( entityReference ) ) {
 			final EntityFetch fetch = (EntityFetch) entityReference;
 			final EntityType fetchedType = fetch.getFetchedType();
 			if ( ! fetchedType.isOneToOne() ) {
 				return;
 			}
 
 			final EntityReferenceProcessingState fetchOwnerState = context.getOwnerProcessingState( fetch );
 			if ( fetchOwnerState == null ) {
 				throw new IllegalStateException( "Could not locate fetch owner state" );
 			}
 
 			final EntityKey ownerEntityKey = fetchOwnerState.getEntityKey();
 			if ( ownerEntityKey == null ) {
 				throw new IllegalStateException( "Could not locate fetch owner EntityKey" );
 			}
 
 			context.getSession().getPersistenceContext().addNullProperty(
 					ownerEntityKey,
 					fetchedType.getPropertyName()
 			);
 		}
 	}
 
 	private void loadFromResultSet(
 			ResultSet resultSet,
 			ResultSetProcessingContext context,
 			Object entityInstance,
 			String concreteEntityTypeName,
 			EntityKey entityKey,
 			LockMode lockModeToAcquire) {
 		final Serializable id = entityKey.getIdentifier();
 
 		// Get the persister for the _subclass_
 		final Loadable concreteEntityPersister = (Loadable) context.getSession().getFactory().getEntityPersister( concreteEntityTypeName );
 
 		if ( log.isTraceEnabled() ) {
 			log.tracev(
 					"Initializing object from ResultSet: {0}",
 					MessageHelper.infoString(
 							concreteEntityPersister,
 							id,
 							context.getSession().getFactory()
 					)
 			);
 		}
 
 		// add temp entry so that the next step is circular-reference
 		// safe - only needed because some types don't take proper
 		// advantage of two-phase-load (esp. components)
 		TwoPhaseLoad.addUninitializedEntity(
 				entityKey,
 				entityInstance,
 				concreteEntityPersister,
 				lockModeToAcquire,
 				!context.getLoadPlan().areLazyAttributesForceFetched(),
 				context.getSession()
 		);
 
 		final EntityPersister rootEntityPersister = context.getSession().getFactory().getEntityPersister(
 				concreteEntityPersister.getRootEntityName()
 		);
-		final EntityReferenceAliases aliases = context.getAliasResolutionContext().resolveAliases( entityReference );
 		final Object[] values;
 		try {
 			values = concreteEntityPersister.hydrate(
 					resultSet,
 					id,
 					entityInstance,
 					(Loadable) entityReference.getEntityPersister(),
 					concreteEntityPersister == rootEntityPersister
-							? aliases.getColumnAliases().getSuffixedPropertyAliases()
-							: aliases.getColumnAliases().getSuffixedPropertyAliases( concreteEntityPersister ),
+							? entityReferenceAliases.getColumnAliases().getSuffixedPropertyAliases()
+							: entityReferenceAliases.getColumnAliases().getSuffixedPropertyAliases( concreteEntityPersister ),
 					context.getLoadPlan().areLazyAttributesForceFetched(),
 					context.getSession()
 			);
 
 			context.getProcessingState( entityReference ).registerHydratedState( values );
 		}
 		catch (SQLException e) {
 			throw context.getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 					e,
 					"Could not read entity state from ResultSet : " + entityKey
 			);
 		}
 
 		final Object rowId;
 		try {
-			rowId = concreteEntityPersister.hasRowId() ? resultSet.getObject( aliases.getColumnAliases().getRowIdAlias() ) : null;
+			rowId = concreteEntityPersister.hasRowId()
+					? resultSet.getObject( entityReferenceAliases.getColumnAliases().getRowIdAlias() )
+					: null;
 		}
 		catch (SQLException e) {
 			throw context.getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 					e,
 					"Could not read entity row-id from ResultSet : " + entityKey
 			);
 		}
 
 		final EntityType entityType = EntityFetch.class.isInstance( entityReference )
 				? ( (EntityFetch) entityReference ).getFetchedType()
 				: entityReference.getEntityPersister().getEntityMetamodel().getEntityType();
 
 		if ( entityType != null ) {
 			String ukName = entityType.getRHSUniqueKeyPropertyName();
 			if ( ukName != null ) {
 				final int index = ( (UniqueKeyLoadable) concreteEntityPersister ).getPropertyIndex( ukName );
 				final Type type = concreteEntityPersister.getPropertyTypes()[index];
 
 				// polymorphism not really handled completely correctly,
 				// perhaps...well, actually its ok, assuming that the
 				// entity name used in the lookup is the same as the
 				// the one used here, which it will be
 
 				EntityUniqueKey euk = new EntityUniqueKey(
 						entityReference.getEntityPersister().getEntityName(),
 						ukName,
 						type.semiResolve( values[index], context.getSession(), entityInstance ),
 						type,
 						concreteEntityPersister.getEntityMode(),
 						context.getSession().getFactory()
 				);
 				context.getSession().getPersistenceContext().addEntity( euk, entityInstance );
 			}
 		}
 
 		TwoPhaseLoad.postHydrate(
 				concreteEntityPersister,
 				id,
 				values,
 				rowId,
 				entityInstance,
 				lockModeToAcquire,
 				!context.getLoadPlan().areLazyAttributesForceFetched(),
 				context.getSession()
 		);
 
 		context.registerHydratedEntity( entityReference, entityKey, entityInstance );
 	}
 
 	private String getConcreteEntityTypeName(
 			ResultSet resultSet,
 			ResultSetProcessingContext context,
 			EntityKey entityKey) {
 		final Loadable loadable = (Loadable) entityReference.getEntityPersister();
 		if ( ! loadable.hasSubclasses() ) {
 			return entityReference.getEntityPersister().getEntityName();
 		}
 
 		final Object discriminatorValue;
 		try {
 			discriminatorValue = loadable.getDiscriminatorType().nullSafeGet(
 					resultSet,
-					context.getAliasResolutionContext().resolveAliases( entityReference ).getColumnAliases().getSuffixedDiscriminatorAlias(),
+					entityReferenceAliases.getColumnAliases().getSuffixedDiscriminatorAlias(),
 					context.getSession(),
 					null
 			);
 		}
 		catch (SQLException e) {
 			throw context.getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 					e,
 					"Could not read discriminator value from ResultSet"
 			);
 		}
 
 		final String result = loadable.getSubclassForDiscriminatorValue( discriminatorValue );
 
 		if ( result == null ) {
 			// whoops! we got an instance of another class hierarchy branch
 			throw new WrongClassException(
 					"Discriminator: " + discriminatorValue,
 					entityKey.getIdentifier(),
 					entityReference.getEntityPersister().getEntityName()
 			);
 		}
 
 		return result;
 	}
 
 	private void checkVersion(
 			ResultSet resultSet,
 			ResultSetProcessingContext context,
 			EntityKey entityKey,
 			Object existing) {
 		final LockMode requestedLockMode = context.resolveLockMode( entityReference );
 		if ( requestedLockMode != LockMode.NONE ) {
 			final LockMode currentLockMode = context.getSession().getPersistenceContext().getEntry( existing ).getLockMode();
 			final boolean isVersionCheckNeeded = entityReference.getEntityPersister().isVersioned()
 					&& currentLockMode.lessThan( requestedLockMode );
 
 			// we don't need to worry about existing version being uninitialized because this block isn't called
 			// by a re-entrant load (re-entrant loads *always* have lock mode NONE)
 			if ( isVersionCheckNeeded ) {
 				//we only check the version when *upgrading* lock modes
 				checkVersion(
 						context.getSession(),
 						resultSet,
 						entityReference.getEntityPersister(),
-						context.getAliasResolutionContext().resolveAliases( entityReference ).getColumnAliases(),
+						entityReferenceAliases.getColumnAliases(),
 						entityKey,
 						existing
 				);
 				//we need to upgrade the lock mode to the mode requested
 				context.getSession().getPersistenceContext().getEntry( existing ).setLockMode( requestedLockMode );
 			}
 		}
 	}
 
 	private void checkVersion(
 			SessionImplementor session,
 			ResultSet resultSet,
 			EntityPersister persister,
 			EntityAliases entityAliases,
 			EntityKey entityKey,
 			Object entityInstance) {
 		final Object version = session.getPersistenceContext().getEntry( entityInstance ).getVersion();
 
 		if ( version != null ) {
 			//null version means the object is in the process of being loaded somewhere else in the ResultSet
 			VersionType versionType = persister.getVersionType();
 			final Object currentVersion;
 			try {
 				currentVersion = versionType.nullSafeGet(
 						resultSet,
 						entityAliases.getSuffixedVersionAliases(),
 						session,
 						null
 				);
 			}
 			catch (SQLException e) {
 				throw session.getFactory().getJdbcServices().getSqlExceptionHelper().convert(
 						e,
 						"Could not read version value from result set"
 				);
 			}
 
 			if ( !versionType.isEqual( version, currentVersion ) ) {
 				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
 					session.getFactory().getStatisticsImplementor().optimisticFailure( persister.getEntityName() );
 				}
 				throw new StaleObjectStateException( persister.getEntityName(), entityKey.getIdentifier() );
 			}
 		}
 	}
 
 	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) {
 		//To change body of created methods use File | Settings | File Templates.
 	}
 
 	public void finishUpRow(ResultSet resultSet, ResultSetProcessingContextImpl context) {
 		//To change body of created methods use File | Settings | File Templates.
 	}
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReturnReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReturnReader.java
index 0d5edb86af..4362af7248 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReturnReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReturnReader.java
@@ -1,123 +1,124 @@
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
 package org.hibernate.loader.plan.exec.process.internal;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
 import org.hibernate.loader.plan.exec.process.spi.ReturnReader;
+import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.proxy.HibernateProxy;
 
 import static org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext.EntityReferenceProcessingState;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityReturnReader extends EntityReferenceReader implements ReturnReader {
 	private final EntityReturn entityReturn;
 
-	public EntityReturnReader(EntityReturn entityReturn) {
-		super( entityReturn );
+	public EntityReturnReader(EntityReturn entityReturn, EntityReferenceAliases aliases, EntityIdentifierReader identifierReader) {
+		super( entityReturn, aliases, identifierReader );
 		this.entityReturn = entityReturn;
 	}
 
 //	@Override
 //	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 //		final EntityKey entityKey = getEntityKeyFromContext( context );
 //		if ( entityKey != null ) {
 //			getIdentifierResolutionContext( context ).registerEntityKey( entityKey );
 //			return;
 //		}
 //
 //		entityReturn.getIdentifierDescription().hydrate( resultSet, context );
 //
 //		for ( Fetch fetch : entityReturn.getFetches() ) {
 //			if ( FetchStrategyHelper.isJoinFetched( fetch.getFetchStrategy() ) ) {
 //				fetch.hydrate( resultSet, context );
 //			}
 //		}
 //	}
 
 	private EntityReferenceProcessingState getIdentifierResolutionContext(ResultSetProcessingContext context) {
 		final ResultSetProcessingContext.EntityReferenceProcessingState entityReferenceProcessingState = context.getProcessingState(
 				entityReturn
 		);
 
 		if ( entityReferenceProcessingState == null ) {
 			throw new AssertionFailure(
 					String.format(
 							"Could not locate EntityReferenceProcessingState for root entity return [%s (%s)]",
 							entityReturn.getPropertyPath().getFullPath(),
 							entityReturn.getEntityPersister().getEntityName()
 					)
 			);
 		}
 
 		return entityReferenceProcessingState;
 	}
 
 //	@Override
 //	public void resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 //		final EntityReferenceProcessingState entityReferenceProcessingState = getIdentifierResolutionContext( context );
 //		EntityKey entityKey = entityReferenceProcessingState.getEntityKey();
 //		if ( entityKey != null ) {
 //			return;
 //		}
 //
 //		entityKey = entityReturn.getIdentifierDescription().resolve( resultSet, context );
 //		entityReferenceProcessingState.registerEntityKey( entityKey );
 //
 //		for ( Fetch fetch : entityReturn.getFetches() ) {
 //			if ( FetchStrategyHelper.isJoinFetched( fetch.getFetchStrategy() ) ) {
 //				fetch.resolve( resultSet, context );
 //			}
 //		}
 //	}
 
 	@Override
 	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		final EntityReferenceProcessingState processingState = getIdentifierResolutionContext( context );
 
 		final EntityKey entityKey = processingState.getEntityKey();
 		final Object entityInstance = context.getProcessingState( entityReturn ).getEntityInstance();
 
 		if ( context.shouldReturnProxies() ) {
 			final Object proxy = context.getSession().getPersistenceContext().proxyFor(
 					entityReturn.getEntityPersister(),
 					entityKey,
 					entityInstance
 			);
 			if ( proxy != entityInstance ) {
 				( (HibernateProxy) proxy ).getHibernateLazyInitializer().setImplementation( proxy );
 				return proxy;
 			}
 		}
 
 		return entityInstance;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/OneToOneFetchReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/OneToOneFetchReader.java
index 82bad7b476..beb4aad260 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/OneToOneFetchReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/OneToOneFetchReader.java
@@ -1,46 +1,44 @@
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
 package org.hibernate.loader.plan.exec.process.internal;
 
+import org.hibernate.loader.plan.exec.spi.EntityReferenceAliases;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReference;
 
 /**
  * @author Steve Ebersole
  */
 public class OneToOneFetchReader extends EntityReferenceReader {
 	private final EntityReference ownerEntityReference;
 
-	public OneToOneFetchReader(EntityFetch entityFetch, EntityReference ownerEntityReference) {
-		super( entityFetch, new OneToOneFetchIdentifierReader( entityFetch, ownerEntityReference ) );
+	public OneToOneFetchReader(
+			EntityFetch entityFetch,
+			EntityReferenceAliases aliases,
+			EntityIdentifierReader identifierReader,
+			EntityReference ownerEntityReference) {
+		super( entityFetch, aliases, identifierReader );
 		this.ownerEntityReference = ownerEntityReference;
 	}
-
-	private static class OneToOneFetchIdentifierReader extends EntityIdentifierReaderImpl {
-		public OneToOneFetchIdentifierReader(EntityFetch oneToOne, EntityReference ownerEntityReference) {
-			super( oneToOne );
-		}
-
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessingContextImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessingContextImpl.java
index 30fad0710d..0381191073 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessingContextImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessingContextImpl.java
@@ -1,947 +1,938 @@
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
 package org.hibernate.loader.plan.exec.process.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.IdentityHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.LockMode;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.WrongClassException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.internal.TwoPhaseLoad;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.SubselectFetch;
 import org.hibernate.event.spi.EventSource;
 import org.hibernate.event.spi.PostLoadEvent;
 import org.hibernate.event.spi.PreLoadEvent;
 import org.hibernate.loader.CollectionAliases;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
 import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
 import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
 import org.hibernate.loader.plan.exec.spi.LockModeResolver;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CollectionReturn;
 import org.hibernate.loader.plan.spi.CompositeFetch;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.loader.plan.spi.FetchOwner;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.visit.LoadPlanVisitationStrategyAdapter;
 import org.hibernate.loader.plan.spi.visit.LoadPlanVisitor;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.VersionType;
 
 /**
  * @author Steve Ebersole
  */
 public class ResultSetProcessingContextImpl implements ResultSetProcessingContext {
 	private static final Logger LOG = Logger.getLogger( ResultSetProcessingContextImpl.class );
 
 	private final ResultSet resultSet;
 	private final SessionImplementor session;
 	private final LoadPlan loadPlan;
 	private final boolean readOnly;
 	private final boolean shouldUseOptionalEntityInformation;
 	private final boolean forceFetchLazyAttributes;
 	private final boolean shouldReturnProxies;
 	private final QueryParameters queryParameters;
 	private final NamedParameterContext namedParameterContext;
-	private final AliasResolutionContext aliasResolutionContext;
 	private final boolean hadSubselectFetches;
 
 	private List<HydratedEntityRegistration> currentRowHydratedEntityRegistrationList;
 
 	private Map<EntityPersister,Set<EntityKey>> subselectLoadableEntityKeyMap;
 	private List<HydratedEntityRegistration> hydratedEntityRegistrationList;
 
 	private LockModeResolver lockModeResolverDelegate = new LockModeResolver() {
 		@Override
 		public LockMode resolveLockMode(EntityReference entityReference) {
 			return LockMode.NONE;
 		}
 	};
 
 	/**
 	 * Builds a ResultSetProcessingContextImpl
 	 *
 	 * @param resultSet
 	 * @param session
 	 * @param loadPlan
 	 * @param readOnly
 	 * @param shouldUseOptionalEntityInformation There are times when the "optional entity information" on
 	 * QueryParameters should be used and times when they should not.  Collection initializers, batch loaders, etc
 	 * are times when it should NOT be used.
 	 * @param forceFetchLazyAttributes
 	 * @param shouldReturnProxies
 	 * @param queryParameters
 	 * @param namedParameterContext
-	 * @param aliasResolutionContext
 	 * @param hadSubselectFetches
 	 */
 	public ResultSetProcessingContextImpl(
 			ResultSet resultSet,
 			SessionImplementor session,
 			LoadPlan loadPlan,
 			boolean readOnly,
 			boolean shouldUseOptionalEntityInformation,
 			boolean forceFetchLazyAttributes,
 			boolean shouldReturnProxies,
 			QueryParameters queryParameters,
 			NamedParameterContext namedParameterContext,
-			AliasResolutionContext aliasResolutionContext,
 			boolean hadSubselectFetches) {
 		this.resultSet = resultSet;
 		this.session = session;
 		this.loadPlan = loadPlan;
 		this.readOnly = readOnly;
 		this.shouldUseOptionalEntityInformation = shouldUseOptionalEntityInformation;
 		this.forceFetchLazyAttributes = forceFetchLazyAttributes;
 		this.shouldReturnProxies = shouldReturnProxies;
 		this.queryParameters = queryParameters;
 		this.namedParameterContext = namedParameterContext;
-		this.aliasResolutionContext = aliasResolutionContext;
 		this.hadSubselectFetches = hadSubselectFetches;
 
 		if ( shouldUseOptionalEntityInformation ) {
 			if ( queryParameters.getOptionalId() != null ) {
 				// make sure we have only one return
 				if ( loadPlan.getReturns().size() > 1 ) {
 					throw new IllegalStateException( "Cannot specify 'optional entity' values with multi-return load plans" );
 				}
 			}
 		}
 	}
 
 	@Override
 	public SessionImplementor getSession() {
 		return session;
 	}
 
 	@Override
 	public boolean shouldUseOptionalEntityInformation() {
 		return shouldUseOptionalEntityInformation;
 	}
 
 	@Override
 	public QueryParameters getQueryParameters() {
 		return queryParameters;
 	}
 
 	@Override
 	public boolean shouldReturnProxies() {
 		return shouldReturnProxies;
 	}
 
 	@Override
 	public LoadPlan getLoadPlan() {
 		return loadPlan;
 	}
 
 	@Override
 	public LockMode resolveLockMode(EntityReference entityReference) {
 		final LockMode lockMode = lockModeResolverDelegate.resolveLockMode( entityReference );
 		return LockMode.NONE == lockMode ? LockMode.NONE : lockMode;
 	}
 
 	private Map<EntityReference,EntityReferenceProcessingState> identifierResolutionContextMap;
 
 	@Override
 	public EntityReferenceProcessingState getProcessingState(final EntityReference entityReference) {
 		if ( identifierResolutionContextMap == null ) {
 			identifierResolutionContextMap = new IdentityHashMap<EntityReference, EntityReferenceProcessingState>();
 		}
 
 		EntityReferenceProcessingState context = identifierResolutionContextMap.get( entityReference );
 		if ( context == null ) {
 			context = new EntityReferenceProcessingState() {
 				private boolean wasMissingIdentifier;
 				private Object identifierHydratedForm;
 				private EntityKey entityKey;
 				private Object[] hydratedState;
 				private Object entityInstance;
 
 				@Override
 				public EntityReference getEntityReference() {
 					return entityReference;
 				}
 
 				@Override
 				public void registerMissingIdentifier() {
 					if ( !EntityFetch.class.isInstance( entityReference ) ) {
 						throw new IllegalStateException( "Missing return row identifier" );
 					}
 					ResultSetProcessingContextImpl.this.registerNonExists( (EntityFetch) entityReference );
 					wasMissingIdentifier = true;
 				}
 
 				@Override
 				public boolean isMissingIdentifier() {
 					return wasMissingIdentifier;
 				}
 
 				@Override
 				public void registerIdentifierHydratedForm(Object identifierHydratedForm) {
 					this.identifierHydratedForm = identifierHydratedForm;
 				}
 
 				@Override
 				public Object getIdentifierHydratedForm() {
 					return identifierHydratedForm;
 				}
 
 				@Override
 				public void registerEntityKey(EntityKey entityKey) {
 					this.entityKey = entityKey;
 				}
 
 				@Override
 				public EntityKey getEntityKey() {
 					return entityKey;
 				}
 
 				@Override
 				public void registerHydratedState(Object[] hydratedState) {
 					this.hydratedState = hydratedState;
 				}
 
 				@Override
 				public Object[] getHydratedState() {
 					return hydratedState;
 				}
 
 				@Override
 				public void registerEntityInstance(Object entityInstance) {
 					this.entityInstance = entityInstance;
 				}
 
 				@Override
 				public Object getEntityInstance() {
 					return entityInstance;
 				}
 			};
 			identifierResolutionContextMap.put( entityReference, context );
 		}
 
 		return context;
 	}
 
 	private void registerNonExists(EntityFetch fetch) {
 		final EntityType fetchedType = fetch.getFetchedType();
 		if ( ! fetchedType.isOneToOne() ) {
 			return;
 		}
 
 		final EntityReferenceProcessingState fetchOwnerState = getOwnerProcessingState( fetch );
 		if ( fetchOwnerState == null ) {
 			throw new IllegalStateException( "Could not locate fetch owner state" );
 		}
 
 		final EntityKey ownerEntityKey = fetchOwnerState.getEntityKey();
 		if ( ownerEntityKey == null ) {
 			throw new IllegalStateException( "Could not locate fetch owner EntityKey" );
 		}
 
 		session.getPersistenceContext().addNullProperty(
 				ownerEntityKey,
 				fetchedType.getPropertyName()
 		);
 	}
 
 	@Override
 	public EntityReferenceProcessingState getOwnerProcessingState(Fetch fetch) {
 		return getProcessingState( resolveFetchOwnerEntityReference( fetch ) );
 	}
 
 	private EntityReference resolveFetchOwnerEntityReference(Fetch fetch) {
 		final FetchOwner fetchOwner = fetch.getOwner();
 
 		if ( EntityReference.class.isInstance( fetchOwner ) ) {
 			return (EntityReference) fetchOwner;
 		}
 		else if ( CompositeFetch.class.isInstance( fetchOwner ) ) {
 			return resolveFetchOwnerEntityReference( (CompositeFetch) fetchOwner );
 		}
 
 		throw new IllegalStateException(
 				String.format(
 						"Cannot resolve FetchOwner [%s] of Fetch [%s (%s)] to an EntityReference",
 						fetchOwner,
 						fetch,
 						fetch.getPropertyPath()
 				)
 		);
 	}
 
-	@Override
-	public AliasResolutionContext getAliasResolutionContext() {
-		return aliasResolutionContext;
-	}
-
-	@Override
-	public void checkVersion(
-			ResultSet resultSet,
-			EntityPersister persister,
-			EntityAliases entityAliases,
-			EntityKey entityKey,
-			Object entityInstance) {
-		final Object version = session.getPersistenceContext().getEntry( entityInstance ).getVersion();
-
-		if ( version != null ) {
-			//null version means the object is in the process of being loaded somewhere else in the ResultSet
-			VersionType versionType = persister.getVersionType();
-			final Object currentVersion;
-			try {
-				currentVersion = versionType.nullSafeGet(
-						resultSet,
-						entityAliases.getSuffixedVersionAliases(),
-						session,
-						null
-				);
-			}
-			catch (SQLException e) {
-				throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
-						e,
-						"Could not read version value from result set"
-				);
-			}
-
-			if ( !versionType.isEqual( version, currentVersion ) ) {
-				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
-					session.getFactory().getStatisticsImplementor().optimisticFailure( persister.getEntityName() );
-				}
-				throw new StaleObjectStateException( persister.getEntityName(), entityKey.getIdentifier() );
-			}
-		}
-	}
-
-	@Override
-	public String getConcreteEntityTypeName(
-			final ResultSet rs,
-			final EntityPersister persister,
-			final EntityAliases entityAliases,
-			final EntityKey entityKey) {
-
-		final Loadable loadable = (Loadable) persister;
-		if ( ! loadable.hasSubclasses() ) {
-			return persister.getEntityName();
-		}
-
-		final Object discriminatorValue;
-		try {
-			discriminatorValue = loadable.getDiscriminatorType().nullSafeGet(
-					rs,
-					entityAliases.getSuffixedDiscriminatorAlias(),
-					session,
-					null
-			);
-		}
-		catch (SQLException e) {
-			throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
-					e,
-					"Could not read discriminator value from ResultSet"
-			);
-		}
-
-		final String result = loadable.getSubclassForDiscriminatorValue( discriminatorValue );
-
-		if ( result == null ) {
-			// whoops! we got an instance of another class hierarchy branch
-			throw new WrongClassException(
-					"Discriminator: " + discriminatorValue,
-					entityKey.getIdentifier(),
-					persister.getEntityName()
-			);
-		}
-
-		return result;
-	}
-
-	@Override
-	public Object resolveEntityKey(EntityKey entityKey, EntityKeyResolutionContext entityKeyContext) {
-		final Object existing = getSession().getEntityUsingInterceptor( entityKey );
-
-		if ( existing != null ) {
-			if ( !entityKeyContext.getEntityPersister().isInstance( existing ) ) {
-				throw new WrongClassException(
-						"loaded object was of wrong class " + existing.getClass(),
-						entityKey.getIdentifier(),
-						entityKeyContext.getEntityPersister().getEntityName()
-				);
-			}
-
-			final LockMode requestedLockMode = entityKeyContext.getLockMode() == null
-					? LockMode.NONE
-					: entityKeyContext.getLockMode();
-
-			if ( requestedLockMode != LockMode.NONE ) {
-				final LockMode currentLockMode = getSession().getPersistenceContext().getEntry( existing ).getLockMode();
-				final boolean isVersionCheckNeeded = entityKeyContext.getEntityPersister().isVersioned()
-						&& currentLockMode.lessThan( requestedLockMode );
-
-				// we don't need to worry about existing version being uninitialized because this block isn't called
-				// by a re-entrant load (re-entrant loads *always* have lock mode NONE)
-				if ( isVersionCheckNeeded ) {
-					//we only check the version when *upgrading* lock modes
-					checkVersion(
-							resultSet,
-							entityKeyContext.getEntityPersister(),
-							aliasResolutionContext.resolveAliases( entityKeyContext.getEntityReference() ).getColumnAliases(),
-							entityKey,
-							existing
-					);
-					//we need to upgrade the lock mode to the mode requested
-					getSession().getPersistenceContext().getEntry( existing ).setLockMode( requestedLockMode );
-				}
-			}
-
-			return existing;
-		}
-		else {
-			final String concreteEntityTypeName = getConcreteEntityTypeName(
-					resultSet,
-					entityKeyContext.getEntityPersister(),
-					aliasResolutionContext.resolveAliases( entityKeyContext.getEntityReference() ).getColumnAliases(),
-					entityKey
-			);
-
-			final Object entityInstance;
-//			if ( suppliedOptionalEntityKey != null && entityKey.equals( suppliedOptionalEntityKey ) ) {
-//				// its the given optional object
-//				entityInstance = queryParameters.getOptionalObject();
+//	@Override
+//	public void checkVersion(
+//			ResultSet resultSet,
+//			EntityPersister persister,
+//			EntityAliases entityAliases,
+//			EntityKey entityKey,
+//			Object entityInstance) {
+//		final Object version = session.getPersistenceContext().getEntry( entityInstance ).getVersion();
+//
+//		if ( version != null ) {
+//			//null version means the object is in the process of being loaded somewhere else in the ResultSet
+//			VersionType versionType = persister.getVersionType();
+//			final Object currentVersion;
+//			try {
+//				currentVersion = versionType.nullSafeGet(
+//						resultSet,
+//						entityAliases.getSuffixedVersionAliases(),
+//						session,
+//						null
+//				);
 //			}
-//			else {
-				// instantiate a new instance
-				entityInstance = session.instantiate( concreteEntityTypeName, entityKey.getIdentifier() );
+//			catch (SQLException e) {
+//				throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
+//						e,
+//						"Could not read version value from result set"
+//				);
 //			}
-
-			FetchStrategy fetchStrategy = null;
-			final EntityReference entityReference = entityKeyContext.getEntityReference();
-			if ( EntityFetch.class.isInstance( entityReference ) ) {
-				final EntityFetch fetch = (EntityFetch) entityReference;
-				fetchStrategy = fetch.getFetchStrategy();
-			}
-
-			//need to hydrate it.
-
-			// grab its state from the ResultSet and keep it in the Session
-			// (but don't yet initialize the object itself)
-			// note that we acquire LockMode.READ even if it was not requested
-			final LockMode requestedLockMode = entityKeyContext.getLockMode() == null
-					? LockMode.NONE
-					: entityKeyContext.getLockMode();
-			final LockMode acquiredLockMode = requestedLockMode == LockMode.NONE
-					? LockMode.READ
-					: requestedLockMode;
-
-			loadFromResultSet(
-					resultSet,
-					entityInstance,
-					concreteEntityTypeName,
-					entityKey,
-					aliasResolutionContext.resolveAliases( entityKeyContext.getEntityReference() ).getColumnAliases(),
-					acquiredLockMode,
-					entityKeyContext.getEntityPersister(),
-					fetchStrategy,
-					true,
-					entityKeyContext.getEntityPersister().getEntityMetamodel().getEntityType()
-			);
-
-			// materialize associations (and initialize the object) later
-			registerHydratedEntity( entityKeyContext.getEntityReference(), entityKey, entityInstance );
-
-			return entityInstance;
-		}
-	}
-
-	@Override
-	public void loadFromResultSet(
-			ResultSet resultSet,
-			Object entityInstance,
-			String concreteEntityTypeName,
-			EntityKey entityKey,
-			EntityAliases entityAliases,
-			LockMode acquiredLockMode,
-			EntityPersister rootPersister,
-			FetchStrategy fetchStrategy,
-			boolean eagerFetch,
-			EntityType associationType) {
-
-		final Serializable id = entityKey.getIdentifier();
-
-		// Get the persister for the _subclass_
-		final Loadable persister = (Loadable) getSession().getFactory().getEntityPersister( concreteEntityTypeName );
-
-		if ( LOG.isTraceEnabled() ) {
-			LOG.tracev(
-					"Initializing object from ResultSet: {0}",
-					MessageHelper.infoString(
-							persister,
-							id,
-							getSession().getFactory()
-					)
-			);
-		}
-
-		// add temp entry so that the next step is circular-reference
-		// safe - only needed because some types don't take proper
-		// advantage of two-phase-load (esp. components)
-		TwoPhaseLoad.addUninitializedEntity(
-				entityKey,
-				entityInstance,
-				persister,
-				acquiredLockMode,
-				!forceFetchLazyAttributes,
-				session
-		);
-
-		// This is not very nice (and quite slow):
-		final String[][] cols = persister == rootPersister ?
-				entityAliases.getSuffixedPropertyAliases() :
-				entityAliases.getSuffixedPropertyAliases(persister);
-
-		final Object[] values;
-		try {
-			values = persister.hydrate(
-					resultSet,
-					id,
-					entityInstance,
-					(Loadable) rootPersister,
-					cols,
-					loadPlan.areLazyAttributesForceFetched(),
-					session
-			);
-		}
-		catch (SQLException e) {
-			throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
-					e,
-					"Could not read entity state from ResultSet : " + entityKey
-			);
-		}
-
-		final Object rowId;
-		try {
-			rowId = persister.hasRowId() ? resultSet.getObject( entityAliases.getRowIdAlias() ) : null;
-		}
-		catch (SQLException e) {
-			throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
-					e,
-					"Could not read entity row-id from ResultSet : " + entityKey
-			);
-		}
-
-		if ( associationType != null ) {
-			String ukName = associationType.getRHSUniqueKeyPropertyName();
-			if ( ukName != null ) {
-				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex( ukName );
-				final Type type = persister.getPropertyTypes()[index];
-
-				// polymorphism not really handled completely correctly,
-				// perhaps...well, actually its ok, assuming that the
-				// entity name used in the lookup is the same as the
-				// the one used here, which it will be
-
-				EntityUniqueKey euk = new EntityUniqueKey(
-						rootPersister.getEntityName(), //polymorphism comment above
-						ukName,
-						type.semiResolve( values[index], session, entityInstance ),
-						type,
-						persister.getEntityMode(),
-						session.getFactory()
-				);
-				session.getPersistenceContext().addEntity( euk, entityInstance );
-			}
-		}
-
-		TwoPhaseLoad.postHydrate(
-				persister,
-				id,
-				values,
-				rowId,
-				entityInstance,
-				acquiredLockMode,
-				!loadPlan.areLazyAttributesForceFetched(),
-				session
-		);
-
-	}
-
-	public void readCollectionElements(final Object[] row) {
-			LoadPlanVisitor.visit(
-					loadPlan,
-					new LoadPlanVisitationStrategyAdapter() {
-						@Override
-						public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
-							readCollectionElement(
-									null,
-									null,
-									rootCollectionReturn.getCollectionPersister(),
-									aliasResolutionContext.resolveAliases( rootCollectionReturn ).getCollectionColumnAliases(),
-									resultSet,
-									session
-							);
-						}
-
-						@Override
-						public void startingCollectionFetch(CollectionFetch collectionFetch) {
-							// TODO: determine which element is the owner.
-							final Object owner = row[ 0 ];
-							readCollectionElement(
-									owner,
-									collectionFetch.getCollectionPersister().getCollectionType().getKeyOfOwner( owner, session ),
-									collectionFetch.getCollectionPersister(),
-									aliasResolutionContext.resolveAliases( collectionFetch ).getCollectionColumnAliases(),
-									resultSet,
-									session
-							);
-						}
-
-						private void readCollectionElement(
-								final Object optionalOwner,
-								final Serializable optionalKey,
-								final CollectionPersister persister,
-								final CollectionAliases descriptor,
-								final ResultSet rs,
-								final SessionImplementor session) {
-
-							try {
-								final PersistenceContext persistenceContext = session.getPersistenceContext();
-
-								final Serializable collectionRowKey = (Serializable) persister.readKey(
-										rs,
-										descriptor.getSuffixedKeyAliases(),
-										session
-								);
-
-								if ( collectionRowKey != null ) {
-									// we found a collection element in the result set
-
-									if ( LOG.isDebugEnabled() ) {
-										LOG.debugf( "Found row of collection: %s",
-												MessageHelper.collectionInfoString( persister, collectionRowKey, session.getFactory() ) );
-									}
-
-									Object owner = optionalOwner;
-									if ( owner == null ) {
-										owner = persistenceContext.getCollectionOwner( collectionRowKey, persister );
-										if ( owner == null ) {
-											//TODO: This is assertion is disabled because there is a bug that means the
-											//	  original owner of a transient, uninitialized collection is not known
-											//	  if the collection is re-referenced by a different object associated
-											//	  with the current Session
-											//throw new AssertionFailure("bug loading unowned collection");
-										}
-									}
-
-									PersistentCollection rowCollection = persistenceContext.getLoadContexts()
-											.getCollectionLoadContext( rs )
-											.getLoadingCollection( persister, collectionRowKey );
-
-									if ( rowCollection != null ) {
-										rowCollection.readFrom( rs, persister, descriptor, owner );
-									}
-
-								}
-								else if ( optionalKey != null ) {
-									// we did not find a collection element in the result set, so we
-									// ensure that a collection is created with the owner's identifier,
-									// since what we have is an empty collection
-
-									if ( LOG.isDebugEnabled() ) {
-										LOG.debugf( "Result set contains (possibly empty) collection: %s",
-												MessageHelper.collectionInfoString( persister, optionalKey, session.getFactory() ) );
-									}
-
-									persistenceContext.getLoadContexts()
-											.getCollectionLoadContext( rs )
-											.getLoadingCollection( persister, optionalKey ); // handle empty collection
-
-								}
-
-								// else no collection element, but also no owner
-							}
-							catch ( SQLException sqle ) {
-								// TODO: would be nice to have the SQL string that failed...
-								throw session.getFactory().getSQLExceptionHelper().convert(
-										sqle,
-										"could not read next row of results"
-								);
-							}
-						}
-
-					}
-			);
-	}
+//
+//			if ( !versionType.isEqual( version, currentVersion ) ) {
+//				if ( session.getFactory().getStatistics().isStatisticsEnabled() ) {
+//					session.getFactory().getStatisticsImplementor().optimisticFailure( persister.getEntityName() );
+//				}
+//				throw new StaleObjectStateException( persister.getEntityName(), entityKey.getIdentifier() );
+//			}
+//		}
+//	}
+//
+//	@Override
+//	public String getConcreteEntityTypeName(
+//			final ResultSet rs,
+//			final EntityPersister persister,
+//			final EntityAliases entityAliases,
+//			final EntityKey entityKey) {
+//
+//		final Loadable loadable = (Loadable) persister;
+//		if ( ! loadable.hasSubclasses() ) {
+//			return persister.getEntityName();
+//		}
+//
+//		final Object discriminatorValue;
+//		try {
+//			discriminatorValue = loadable.getDiscriminatorType().nullSafeGet(
+//					rs,
+//					entityAliases.getSuffixedDiscriminatorAlias(),
+//					session,
+//					null
+//			);
+//		}
+//		catch (SQLException e) {
+//			throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
+//					e,
+//					"Could not read discriminator value from ResultSet"
+//			);
+//		}
+//
+//		final String result = loadable.getSubclassForDiscriminatorValue( discriminatorValue );
+//
+//		if ( result == null ) {
+//			// whoops! we got an instance of another class hierarchy branch
+//			throw new WrongClassException(
+//					"Discriminator: " + discriminatorValue,
+//					entityKey.getIdentifier(),
+//					persister.getEntityName()
+//			);
+//		}
+//
+//		return result;
+//	}
+//
+//	@Override
+//	public Object resolveEntityKey(EntityKey entityKey, EntityKeyResolutionContext entityKeyContext) {
+//		final Object existing = getSession().getEntityUsingInterceptor( entityKey );
+//
+//		if ( existing != null ) {
+//			if ( !entityKeyContext.getEntityPersister().isInstance( existing ) ) {
+//				throw new WrongClassException(
+//						"loaded object was of wrong class " + existing.getClass(),
+//						entityKey.getIdentifier(),
+//						entityKeyContext.getEntityPersister().getEntityName()
+//				);
+//			}
+//
+//			final LockMode requestedLockMode = entityKeyContext.getLockMode() == null
+//					? LockMode.NONE
+//					: entityKeyContext.getLockMode();
+//
+//			if ( requestedLockMode != LockMode.NONE ) {
+//				final LockMode currentLockMode = getSession().getPersistenceContext().getEntry( existing ).getLockMode();
+//				final boolean isVersionCheckNeeded = entityKeyContext.getEntityPersister().isVersioned()
+//						&& currentLockMode.lessThan( requestedLockMode );
+//
+//				// we don't need to worry about existing version being uninitialized because this block isn't called
+//				// by a re-entrant load (re-entrant loads *always* have lock mode NONE)
+//				if ( isVersionCheckNeeded ) {
+//					//we only check the version when *upgrading* lock modes
+//					checkVersion(
+//							resultSet,
+//							entityKeyContext.getEntityPersister(),
+//							aliasResolutionContext.resolveAliases( entityKeyContext.getEntityReference() ).getColumnAliases(),
+//							entityKey,
+//							existing
+//					);
+//					//we need to upgrade the lock mode to the mode requested
+//					getSession().getPersistenceContext().getEntry( existing ).setLockMode( requestedLockMode );
+//				}
+//			}
+//
+//			return existing;
+//		}
+//		else {
+//			final String concreteEntityTypeName = getConcreteEntityTypeName(
+//					resultSet,
+//					entityKeyContext.getEntityPersister(),
+//					aliasResolutionContext.resolveAliases( entityKeyContext.getEntityReference() ).getColumnAliases(),
+//					entityKey
+//			);
+//
+//			final Object entityInstance;
+////			if ( suppliedOptionalEntityKey != null && entityKey.equals( suppliedOptionalEntityKey ) ) {
+////				// its the given optional object
+////				entityInstance = queryParameters.getOptionalObject();
+////			}
+////			else {
+//				// instantiate a new instance
+//				entityInstance = session.instantiate( concreteEntityTypeName, entityKey.getIdentifier() );
+////			}
+//
+//			FetchStrategy fetchStrategy = null;
+//			final EntityReference entityReference = entityKeyContext.getEntityReference();
+//			if ( EntityFetch.class.isInstance( entityReference ) ) {
+//				final EntityFetch fetch = (EntityFetch) entityReference;
+//				fetchStrategy = fetch.getFetchStrategy();
+//			}
+//
+//			//need to hydrate it.
+//
+//			// grab its state from the ResultSet and keep it in the Session
+//			// (but don't yet initialize the object itself)
+//			// note that we acquire LockMode.READ even if it was not requested
+//			final LockMode requestedLockMode = entityKeyContext.getLockMode() == null
+//					? LockMode.NONE
+//					: entityKeyContext.getLockMode();
+//			final LockMode acquiredLockMode = requestedLockMode == LockMode.NONE
+//					? LockMode.READ
+//					: requestedLockMode;
+//
+//			loadFromResultSet(
+//					resultSet,
+//					entityInstance,
+//					concreteEntityTypeName,
+//					entityKey,
+//					aliasResolutionContext.resolveAliases( entityKeyContext.getEntityReference() ).getColumnAliases(),
+//					acquiredLockMode,
+//					entityKeyContext.getEntityPersister(),
+//					fetchStrategy,
+//					true,
+//					entityKeyContext.getEntityPersister().getEntityMetamodel().getEntityType()
+//			);
+//
+//			// materialize associations (and initialize the object) later
+//			registerHydratedEntity( entityKeyContext.getEntityReference(), entityKey, entityInstance );
+//
+//			return entityInstance;
+//		}
+//	}
+//
+//	@Override
+//	public void loadFromResultSet(
+//			ResultSet resultSet,
+//			Object entityInstance,
+//			String concreteEntityTypeName,
+//			EntityKey entityKey,
+//			EntityAliases entityAliases,
+//			LockMode acquiredLockMode,
+//			EntityPersister rootPersister,
+//			FetchStrategy fetchStrategy,
+//			boolean eagerFetch,
+//			EntityType associationType) {
+//
+//		final Serializable id = entityKey.getIdentifier();
+//
+//		// Get the persister for the _subclass_
+//		final Loadable persister = (Loadable) getSession().getFactory().getEntityPersister( concreteEntityTypeName );
+//
+//		if ( LOG.isTraceEnabled() ) {
+//			LOG.tracev(
+//					"Initializing object from ResultSet: {0}",
+//					MessageHelper.infoString(
+//							persister,
+//							id,
+//							getSession().getFactory()
+//					)
+//			);
+//		}
+//
+//		// add temp entry so that the next step is circular-reference
+//		// safe - only needed because some types don't take proper
+//		// advantage of two-phase-load (esp. components)
+//		TwoPhaseLoad.addUninitializedEntity(
+//				entityKey,
+//				entityInstance,
+//				persister,
+//				acquiredLockMode,
+//				!forceFetchLazyAttributes,
+//				session
+//		);
+//
+//		// This is not very nice (and quite slow):
+//		final String[][] cols = persister == rootPersister ?
+//				entityAliases.getSuffixedPropertyAliases() :
+//				entityAliases.getSuffixedPropertyAliases(persister);
+//
+//		final Object[] values;
+//		try {
+//			values = persister.hydrate(
+//					resultSet,
+//					id,
+//					entityInstance,
+//					(Loadable) rootPersister,
+//					cols,
+//					loadPlan.areLazyAttributesForceFetched(),
+//					session
+//			);
+//		}
+//		catch (SQLException e) {
+//			throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
+//					e,
+//					"Could not read entity state from ResultSet : " + entityKey
+//			);
+//		}
+//
+//		final Object rowId;
+//		try {
+//			rowId = persister.hasRowId() ? resultSet.getObject( entityAliases.getRowIdAlias() ) : null;
+//		}
+//		catch (SQLException e) {
+//			throw getSession().getFactory().getJdbcServices().getSqlExceptionHelper().convert(
+//					e,
+//					"Could not read entity row-id from ResultSet : " + entityKey
+//			);
+//		}
+//
+//		if ( associationType != null ) {
+//			String ukName = associationType.getRHSUniqueKeyPropertyName();
+//			if ( ukName != null ) {
+//				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex( ukName );
+//				final Type type = persister.getPropertyTypes()[index];
+//
+//				// polymorphism not really handled completely correctly,
+//				// perhaps...well, actually its ok, assuming that the
+//				// entity name used in the lookup is the same as the
+//				// the one used here, which it will be
+//
+//				EntityUniqueKey euk = new EntityUniqueKey(
+//						rootPersister.getEntityName(), //polymorphism comment above
+//						ukName,
+//						type.semiResolve( values[index], session, entityInstance ),
+//						type,
+//						persister.getEntityMode(),
+//						session.getFactory()
+//				);
+//				session.getPersistenceContext().addEntity( euk, entityInstance );
+//			}
+//		}
+//
+//		TwoPhaseLoad.postHydrate(
+//				persister,
+//				id,
+//				values,
+//				rowId,
+//				entityInstance,
+//				acquiredLockMode,
+//				!loadPlan.areLazyAttributesForceFetched(),
+//				session
+//		);
+//
+//	}
+//
+//	public void readCollectionElements(final Object[] row) {
+//			LoadPlanVisitor.visit(
+//					loadPlan,
+//					new LoadPlanVisitationStrategyAdapter() {
+//						@Override
+//						public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
+//							readCollectionElement(
+//									null,
+//									null,
+//									rootCollectionReturn.getCollectionPersister(),
+//									aliasResolutionContext.resolveAliases( rootCollectionReturn ).getCollectionColumnAliases(),
+//									resultSet,
+//									session
+//							);
+//						}
+//
+//						@Override
+//						public void startingCollectionFetch(CollectionFetch collectionFetch) {
+//							// TODO: determine which element is the owner.
+//							final Object owner = row[ 0 ];
+//							readCollectionElement(
+//									owner,
+//									collectionFetch.getCollectionPersister().getCollectionType().getKeyOfOwner( owner, session ),
+//									collectionFetch.getCollectionPersister(),
+//									aliasResolutionContext.resolveAliases( collectionFetch ).getCollectionColumnAliases(),
+//									resultSet,
+//									session
+//							);
+//						}
+//
+//						private void readCollectionElement(
+//								final Object optionalOwner,
+//								final Serializable optionalKey,
+//								final CollectionPersister persister,
+//								final CollectionAliases descriptor,
+//								final ResultSet rs,
+//								final SessionImplementor session) {
+//
+//							try {
+//								final PersistenceContext persistenceContext = session.getPersistenceContext();
+//
+//								final Serializable collectionRowKey = (Serializable) persister.readKey(
+//										rs,
+//										descriptor.getSuffixedKeyAliases(),
+//										session
+//								);
+//
+//								if ( collectionRowKey != null ) {
+//									// we found a collection element in the result set
+//
+//									if ( LOG.isDebugEnabled() ) {
+//										LOG.debugf( "Found row of collection: %s",
+//												MessageHelper.collectionInfoString( persister, collectionRowKey, session.getFactory() ) );
+//									}
+//
+//									Object owner = optionalOwner;
+//									if ( owner == null ) {
+//										owner = persistenceContext.getCollectionOwner( collectionRowKey, persister );
+//										if ( owner == null ) {
+//											//TODO: This is assertion is disabled because there is a bug that means the
+//											//	  original owner of a transient, uninitialized collection is not known
+//											//	  if the collection is re-referenced by a different object associated
+//											//	  with the current Session
+//											//throw new AssertionFailure("bug loading unowned collection");
+//										}
+//									}
+//
+//									PersistentCollection rowCollection = persistenceContext.getLoadContexts()
+//											.getCollectionLoadContext( rs )
+//											.getLoadingCollection( persister, collectionRowKey );
+//
+//									if ( rowCollection != null ) {
+//										rowCollection.readFrom( rs, persister, descriptor, owner );
+//									}
+//
+//								}
+//								else if ( optionalKey != null ) {
+//									// we did not find a collection element in the result set, so we
+//									// ensure that a collection is created with the owner's identifier,
+//									// since what we have is an empty collection
+//
+//									if ( LOG.isDebugEnabled() ) {
+//										LOG.debugf( "Result set contains (possibly empty) collection: %s",
+//												MessageHelper.collectionInfoString( persister, optionalKey, session.getFactory() ) );
+//									}
+//
+//									persistenceContext.getLoadContexts()
+//											.getCollectionLoadContext( rs )
+//											.getLoadingCollection( persister, optionalKey ); // handle empty collection
+//
+//								}
+//
+//								// else no collection element, but also no owner
+//							}
+//							catch ( SQLException sqle ) {
+//								// TODO: would be nice to have the SQL string that failed...
+//								throw session.getFactory().getSQLExceptionHelper().convert(
+//										sqle,
+//										"could not read next row of results"
+//								);
+//							}
+//						}
+//
+//					}
+//			);
+//	}
 
 	@Override
 	public void registerHydratedEntity(EntityReference entityReference, EntityKey entityKey, Object entityInstance) {
 		if ( currentRowHydratedEntityRegistrationList == null ) {
 			currentRowHydratedEntityRegistrationList = new ArrayList<HydratedEntityRegistration>();
 		}
 		currentRowHydratedEntityRegistrationList.add(
 				new HydratedEntityRegistration(
 						entityReference,
 						entityKey,
 						entityInstance
 				)
 		);
 	}
 
 	/**
 	 * Package-protected
 	 */
 	void finishUpRow() {
 		if ( currentRowHydratedEntityRegistrationList == null ) {
 			return;
 		}
 
 
 		// managing the running list of registrations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		if ( hydratedEntityRegistrationList == null ) {
 			hydratedEntityRegistrationList = new ArrayList<HydratedEntityRegistration>();
 		}
 		hydratedEntityRegistrationList.addAll( currentRowHydratedEntityRegistrationList );
 
 
 		// managing the map forms needed for subselect fetch generation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		if ( hadSubselectFetches ) {
 			if ( subselectLoadableEntityKeyMap == null ) {
 				subselectLoadableEntityKeyMap = new HashMap<EntityPersister, Set<EntityKey>>();
 			}
 			for ( HydratedEntityRegistration registration : currentRowHydratedEntityRegistrationList ) {
 				Set<EntityKey> entityKeys = subselectLoadableEntityKeyMap.get( registration.entityReference.getEntityPersister() );
 				if ( entityKeys == null ) {
 					entityKeys = new HashSet<EntityKey>();
 					subselectLoadableEntityKeyMap.put( registration.entityReference.getEntityPersister(), entityKeys );
 				}
 				entityKeys.add( registration.key );
 			}
 		}
 
 		// release the currentRowHydratedEntityRegistrationList entries
 		currentRowHydratedEntityRegistrationList.clear();
 
 		identifierResolutionContextMap.clear();
 	}
 
 	/**
 	 * Package-protected
 	 *
 	 * @param afterLoadActionList List of after-load actions to perform
 	 */
 	void finishUp(List<AfterLoadAction> afterLoadActionList) {
 		initializeEntitiesAndCollections( afterLoadActionList );
 		createSubselects();
 
 		if ( hydratedEntityRegistrationList != null ) {
 			hydratedEntityRegistrationList.clear();
 			hydratedEntityRegistrationList = null;
 		}
 
 		if ( subselectLoadableEntityKeyMap != null ) {
 			subselectLoadableEntityKeyMap.clear();
 			subselectLoadableEntityKeyMap = null;
 		}
 	}
 
 	private void initializeEntitiesAndCollections(List<AfterLoadAction> afterLoadActionList) {
 		// for arrays, we should end the collection load before resolving the entities, since the
 		// actual array instances are not instantiated during loading
 		finishLoadingArrays();
 
 
 		// IMPORTANT: reuse the same event instances for performance!
 		final PreLoadEvent preLoadEvent;
 		final PostLoadEvent postLoadEvent;
 		if ( session.isEventSource() ) {
 			preLoadEvent = new PreLoadEvent( (EventSource) session );
 			postLoadEvent = new PostLoadEvent( (EventSource) session );
 		}
 		else {
 			preLoadEvent = null;
 			postLoadEvent = null;
 		}
 
 		// now finish loading the entities (2-phase load)
 		performTwoPhaseLoad( preLoadEvent );
 
 		// now we can finalize loading collections
 		finishLoadingCollections();
 
 		// finally, perform post-load operations
 		postLoad( postLoadEvent, afterLoadActionList );
 	}
 
 	private void finishLoadingArrays() {
 		LoadPlanVisitor.visit(
 				loadPlan,
 				new LoadPlanVisitationStrategyAdapter() {
 					@Override
 					public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
 						endLoadingArray( rootCollectionReturn.getCollectionPersister() );
 					}
 
 					@Override
 					public void startingCollectionFetch(CollectionFetch collectionFetch) {
 						endLoadingArray( collectionFetch.getCollectionPersister() );
 					}
 
 					private void endLoadingArray(CollectionPersister persister) {
 						if ( persister.isArray() ) {
 							session.getPersistenceContext()
 									.getLoadContexts()
 									.getCollectionLoadContext( resultSet )
 									.endLoadingCollections( persister );
 						}
 					}
 				}
 		);
 	}
 
 	private void performTwoPhaseLoad(PreLoadEvent preLoadEvent) {
 		final int numberOfHydratedObjects = hydratedEntityRegistrationList == null
 				? 0
 				: hydratedEntityRegistrationList.size();
 		LOG.tracev( "Total objects hydrated: {0}", numberOfHydratedObjects );
 
 		if ( hydratedEntityRegistrationList == null ) {
 			return;
 		}
 
 		for ( HydratedEntityRegistration registration : hydratedEntityRegistrationList ) {
 			TwoPhaseLoad.initializeEntity( registration.instance, readOnly, session, preLoadEvent );
 		}
 	}
 
 	private void finishLoadingCollections() {
 		LoadPlanVisitor.visit(
 				loadPlan,
 				new LoadPlanVisitationStrategyAdapter() {
 					@Override
 					public void handleCollectionReturn(CollectionReturn rootCollectionReturn) {
 						endLoadingCollection( rootCollectionReturn.getCollectionPersister() );
 					}
 
 					@Override
 					public void startingCollectionFetch(CollectionFetch collectionFetch) {
 						endLoadingCollection( collectionFetch.getCollectionPersister() );
 					}
 
 					private void endLoadingCollection(CollectionPersister persister) {
 						if ( ! persister.isArray() ) {
 							session.getPersistenceContext()
 									.getLoadContexts()
 									.getCollectionLoadContext( resultSet )
 									.endLoadingCollections( persister );
 						}
 					}
 				}
 		);
 	}
 
 	private void postLoad(PostLoadEvent postLoadEvent, List<AfterLoadAction> afterLoadActionList) {
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur after
 		// endCollectionLoad to ensure the collection is in the
 		// persistence context.
 		if ( hydratedEntityRegistrationList == null ) {
 			return;
 		}
 
 		for ( HydratedEntityRegistration registration : hydratedEntityRegistrationList ) {
 			TwoPhaseLoad.postLoad( registration.instance, session, postLoadEvent );
 			if ( afterLoadActionList != null ) {
 				for ( AfterLoadAction afterLoadAction : afterLoadActionList ) {
 					afterLoadAction.afterLoad( session, registration.instance, (Loadable) registration.entityReference.getEntityPersister() );
 				}
 			}
 		}
 	}
 
 	private void createSubselects() {
 		if ( subselectLoadableEntityKeyMap == null || subselectLoadableEntityKeyMap.size() <= 1 ) {
 			// if we only returned one entity, query by key is more efficient; so do nothing here
 			return;
 		}
 
 		final Map<String, int[]> namedParameterLocMap =
 				ResultSetProcessorHelper.buildNamedParameterLocMap( queryParameters, namedParameterContext );
 
 		for ( Map.Entry<EntityPersister, Set<EntityKey>> entry : subselectLoadableEntityKeyMap.entrySet() ) {
 			if ( ! entry.getKey().hasSubselectLoadableCollections() ) {
 				continue;
 			}
 
 			SubselectFetch subselectFetch = new SubselectFetch(
 					//getSQLString(),
 					null, // aliases[i],
 					(Loadable) entry.getKey(),
 					queryParameters,
 					entry.getValue(),
 					namedParameterLocMap
 			);
 
 			for ( EntityKey key : entry.getValue() ) {
 				session.getPersistenceContext().getBatchFetchQueue().addSubselect( key, subselectFetch );
 			}
 
 		}
 	}
 
 	private static class HydratedEntityRegistration {
 		private final EntityReference entityReference;
 		private final EntityKey key;
 		private Object instance;
 
 		private HydratedEntityRegistration(EntityReference entityReference, EntityKey key, Object instance) {
 			this.entityReference = entityReference;
 			this.key = key;
 			this.instance = instance;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessorImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessorImpl.java
index 60a77179be..cbe2f66855 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessorImpl.java
@@ -1,543 +1,301 @@
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
 package org.hibernate.loader.plan.exec.process.internal;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.dialect.pagination.LimitHelper;
-import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.RowSelection;
 import org.hibernate.engine.spi.SessionImplementor;
-import org.hibernate.loader.plan.exec.process.spi.ReturnReader;
-import org.hibernate.loader.plan.spi.CollectionFetch;
-import org.hibernate.loader.plan.spi.CollectionReference;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
+import org.hibernate.loader.plan.exec.process.spi.ScrollableResultSetProcessor;
+import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
+import org.hibernate.loader.plan.exec.spi.RowReader;
 import org.hibernate.loader.plan.spi.CollectionReturn;
-import org.hibernate.loader.plan.spi.CompositeFetch;
-import org.hibernate.loader.plan.spi.EntityFetch;
-import org.hibernate.loader.plan.spi.EntityReference;
-import org.hibernate.loader.plan.spi.EntityReturn;
-import org.hibernate.loader.plan.spi.Fetch;
-import org.hibernate.loader.plan.spi.FetchOwner;
 import org.hibernate.loader.plan.spi.LoadPlan;
-import org.hibernate.loader.plan.spi.ScalarReturn;
-import org.hibernate.loader.plan.spi.visit.LoadPlanVisitationStrategyAdapter;
-import org.hibernate.loader.plan.spi.visit.LoadPlanVisitor;
-import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.loader.spi.AfterLoadAction;
-import org.hibernate.loader.spi.LoadPlanAdvisor;
-import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
-import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan.exec.process.spi.ScrollableResultSetProcessor;
-import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.transform.ResultTransformer;
 
 /**
  * @author Steve Ebersole
  */
 public class ResultSetProcessorImpl implements ResultSetProcessor {
 	private static final Logger LOG = Logger.getLogger( ResultSetProcessorImpl.class );
 
-	private final LoadPlan baseLoadPlan;
+	private final LoadPlan loadPlan;
 	private final RowReader rowReader;
 
-	private final boolean shouldUseOptionalEntityInstance;
-
 	private final boolean hadSubselectFetches;
 
-	public ResultSetProcessorImpl(
-			LoadPlan loadPlan,
-			boolean shouldUseOptionalEntityInstance) {
-		this.baseLoadPlan = loadPlan;
-		this.rowReader = buildRowReader( loadPlan );
-		this.shouldUseOptionalEntityInstance = shouldUseOptionalEntityInstance;
-
-		LocalVisitationStrategy strategy = new LocalVisitationStrategy();
-		LoadPlanVisitor.visit( loadPlan, strategy );
-		this.hadSubselectFetches = strategy.hadSubselectFetches;
+	public ResultSetProcessorImpl(LoadPlan loadPlan, RowReader rowReader, boolean hadSubselectFetches) {
+		this.loadPlan = loadPlan;
+		this.rowReader = rowReader;
+		this.hadSubselectFetches = hadSubselectFetches;
 	}
 
-	private RowReader buildRowReader(LoadPlan loadPlan) {
-		switch ( loadPlan.getDisposition() ) {
-			case MIXED: {
-				return new MixedReturnRowReader( loadPlan );
-			}
-			case ENTITY_LOADER: {
-				return new EntityLoaderRowReader( loadPlan );
-			}
-			case COLLECTION_INITIALIZER: {
-				return new CollectionInitializerRowReader( loadPlan );
-			}
-			default: {
-				throw new IllegalStateException( "Unrecognized LoadPlan Return dispostion : " + loadPlan.getDisposition() );
-			}
-		}
+	public RowReader getRowReader() {
+		return rowReader;
 	}
 
 	@Override
 	public ScrollableResultSetProcessor toOnDemandForm() {
 		// todo : implement
 		throw new NotYetImplementedException();
 	}
 
 	@Override
 	public List extractResults(
-			LoadPlanAdvisor loadPlanAdvisor,
 			ResultSet resultSet,
 			final SessionImplementor session,
 			QueryParameters queryParameters,
 			NamedParameterContext namedParameterContext,
-			AliasResolutionContext aliasResolutionContext,
 			boolean returnProxies,
 			boolean readOnly,
 			ResultTransformer forcedResultTransformer,
 			List<AfterLoadAction> afterLoadActionList) throws SQLException {
 
-		final LoadPlan loadPlan = loadPlanAdvisor.advise( this.baseLoadPlan );
-		if ( loadPlan == null ) {
-			throw new IllegalStateException( "LoadPlanAdvisor returned null" );
-		}
-
 		handlePotentiallyEmptyCollectionRootReturns( loadPlan, queryParameters.getCollectionKeys(), resultSet, session );
 
 		final int maxRows;
 		final RowSelection selection = queryParameters.getRowSelection();
 		if ( LimitHelper.hasMaxRows( selection ) ) {
 			maxRows = selection.getMaxRows();
 			LOG.tracef( "Limiting ResultSet processing to just %s rows", maxRows );
 		}
 		else {
 			maxRows = Integer.MAX_VALUE;
 		}
 
 		// There are times when the "optional entity information" on QueryParameters should be used and
 		// times when they should be ignored.  Loader uses its isSingleRowLoader method to allow
 		// subclasses to override that.  Collection initializers, batch loaders, e.g. override that
 		// it to be false.  The 'shouldUseOptionalEntityInstance' setting is meant to fill that same role.
 		final boolean shouldUseOptionalEntityInstance = true;
 
 		// Handles the "FETCH ALL PROPERTIES" directive in HQL
 		final boolean forceFetchLazyAttributes = false;
 
 		final ResultSetProcessingContextImpl context = new ResultSetProcessingContextImpl(
 				resultSet,
 				session,
 				loadPlan,
 				readOnly,
 				shouldUseOptionalEntityInstance,
 				forceFetchLazyAttributes,
 				returnProxies,
 				queryParameters,
 				namedParameterContext,
-				aliasResolutionContext,
 				hadSubselectFetches
 		);
 
 		final List loadResults = new ArrayList();
 
 		LOG.trace( "Processing result set" );
 		int count;
 		for ( count = 0; count < maxRows && resultSet.next(); count++ ) {
 			LOG.debugf( "Starting ResultSet row #%s", count );
 
 			Object logicalRow = rowReader.readRow( resultSet, context );
 
 			// todo : apply transformers here?
 
 			loadResults.add( logicalRow );
 
 			context.finishUpRow();
 		}
 
 		LOG.tracev( "Done processing result set ({0} rows)", count );
 
 		context.finishUp( afterLoadActionList );
 
 		session.getPersistenceContext().initializeNonLazyCollections();
 
 		return loadResults;
 	}
 
 
 	private void handlePotentiallyEmptyCollectionRootReturns(
 			LoadPlan loadPlan,
 			Serializable[] collectionKeys,
 			ResultSet resultSet,
 			SessionImplementor session) {
 		if ( collectionKeys == null ) {
 			// this is not a collection initializer (and empty collections will be detected by looking for
 			// the owner's identifier in the result set)
 			return;
 		}
 
 		// this is a collection initializer, so we must create a collection
 		// for each of the passed-in keys, to account for the possibility
 		// that the collection is empty and has no rows in the result set
 		//
 		// todo : move this inside CollectionReturn ?
 		CollectionPersister persister = ( (CollectionReturn) loadPlan.getReturns().get( 0 ) ).getCollectionPersister();
 		for ( Serializable key : collectionKeys ) {
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf(
 						"Preparing collection intializer : %s",
 							MessageHelper.collectionInfoString( persister, key, session.getFactory() )
 				);
 				session.getPersistenceContext()
 						.getLoadContexts()
 						.getCollectionLoadContext( resultSet )
 						.getLoadingCollection( persister, key );
 			}
 		}
 	}
 
 
-	private class LocalVisitationStrategy extends LoadPlanVisitationStrategyAdapter {
-		private boolean hadSubselectFetches = false;
-
-		@Override
-		public void startingEntityFetch(EntityFetch entityFetch) {
-		// only collections are currently supported for subselect fetching.
-		//			hadSubselectFetches = hadSubselectFetches
-		//					|| entityFetch.getFetchStrategy().getStyle() == FetchStyle.SUBSELECT;
-		}
-
-		@Override
-		public void startingCollectionFetch(CollectionFetch collectionFetch) {
-			hadSubselectFetches = hadSubselectFetches
-					|| collectionFetch.getFetchStrategy().getStyle() == FetchStyle.SUBSELECT;
-		}
-	}
-
-	private static interface RowReader {
-		Object readRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException;
-	}
-
-	private static abstract class AbstractRowReader implements RowReader {
-
-		@Override
-		public Object readRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
-			final List<EntityReferenceReader> entityReferenceReaders = getEntityReferenceReaders();
-			final List<CollectionReferenceReader> collectionReferenceReaders = getCollectionReferenceReaders();
-
-			final boolean hasEntityReferenceReaders = entityReferenceReaders != null && entityReferenceReaders.size() > 0;
-			final boolean hasCollectionReferenceReaders = collectionReferenceReaders != null && collectionReferenceReaders.size() > 0;
-
-			if ( hasEntityReferenceReaders ) {
-				// 	1) allow entity references to resolve identifiers (in 2 steps)
-				for ( EntityReferenceReader entityReferenceReader : entityReferenceReaders ) {
-					entityReferenceReader.hydrateIdentifier( resultSet, context );
-				}
-				for ( EntityReferenceReader entityReferenceReader : entityReferenceReaders ) {
-					entityReferenceReader.resolveEntityKey( resultSet, context );
-				}
-
-
-				// 2) allow entity references to resolve their hydrated state and entity instance
-				for ( EntityReferenceReader entityReferenceReader : entityReferenceReaders ) {
-					entityReferenceReader.hydrateEntityState( resultSet, context );
-				}
-			}
-
-
-			// 3) read the logical row
-
-			Object logicalRow = readLogicalRow( resultSet, context );
-
-
-			// 4) allow entities and collection to read their elements
-			if ( hasEntityReferenceReaders ) {
-				for ( EntityReferenceReader entityReferenceReader : entityReferenceReaders ) {
-					entityReferenceReader.finishUpRow( resultSet, context );
-				}
-			}
-			if ( hasCollectionReferenceReaders ) {
-				for ( CollectionReferenceReader collectionReferenceReader : collectionReferenceReaders ) {
-					collectionReferenceReader.finishUpRow( resultSet, context );
-				}
-			}
-
-			return logicalRow;
-		}
-
-		protected abstract List<EntityReferenceReader> getEntityReferenceReaders();
-		protected abstract List<CollectionReferenceReader> getCollectionReferenceReaders();
-
-		protected abstract Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context)
-				throws SQLException;
-
-	}
-
-	private class MixedReturnRowReader extends AbstractRowReader implements RowReader {
-		private final List<ReturnReader> returnReaders;
-		private List<EntityReferenceReader> entityReferenceReaders = new ArrayList<EntityReferenceReader>();
-		private List<CollectionReferenceReader> collectionReferenceReaders = new ArrayList<CollectionReferenceReader>();
-
-		private final int numberOfReturns;
-
-		public MixedReturnRowReader(LoadPlan loadPlan) {
-			LoadPlanVisitor.visit(
-					loadPlan,
-					new LoadPlanVisitationStrategyAdapter() {
-						@Override
-						public void startingEntityFetch(EntityFetch entityFetch) {
-							entityReferenceReaders.add( new EntityReferenceReader( entityFetch ) );
-						}
-
-						@Override
-						public void startingCollectionFetch(CollectionFetch collectionFetch) {
-							collectionReferenceReaders.add( new CollectionReferenceReader( collectionFetch ) );
-						}
-					}
-			);
-
-			final List<ReturnReader> readers = new ArrayList<ReturnReader>();
-
-			for ( Return rtn : loadPlan.getReturns() ) {
-				final ReturnReader returnReader = buildReturnReader( rtn );
-				if ( EntityReferenceReader.class.isInstance( returnReader ) ) {
-					entityReferenceReaders.add( (EntityReferenceReader) returnReader );
-				}
-				readers.add( returnReader );
-			}
-
-			this.returnReaders = readers;
-			this.numberOfReturns = readers.size();
-		}
-
-		@Override
-		protected List<EntityReferenceReader> getEntityReferenceReaders() {
-			return entityReferenceReaders;
-		}
-
-		@Override
-		protected List<CollectionReferenceReader> getCollectionReferenceReaders() {
-			return collectionReferenceReaders;
-		}
-
-		@Override
-		protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
-			Object[] logicalRow = new Object[ numberOfReturns ];
-			int pos = 0;
-			for ( ReturnReader reader : returnReaders ) {
-				logicalRow[pos] = reader.read( resultSet, context );
-				pos++;
-			}
-			return logicalRow;
-		}
-	}
-
-	private static ReturnReader buildReturnReader(Return rtn) {
-		if ( ScalarReturn.class.isInstance( rtn ) ) {
-			return new ScalarReturnReader( (ScalarReturn) rtn );
-		}
-		else if ( EntityReturn.class.isInstance( rtn ) ) {
-			return new EntityReturnReader( (EntityReturn) rtn );
-		}
-		else if ( CollectionReturn.class.isInstance( rtn ) ) {
-			return new CollectionReturnReader( (CollectionReturn) rtn );
-		}
-		else {
-			throw new IllegalStateException( "Unknown Return type : " + rtn );
-		}
-	}
-
-	private static interface EntityReferenceReaderListBuildingAccess {
-		public void add(EntityReferenceReader reader);
-	}
-
-	private static interface CollectionReferenceReaderListBuildingAccess {
-		public void add(CollectionReferenceReader reader);
-	}
-
-
-	private class EntityLoaderRowReader extends AbstractRowReader implements RowReader {
-		private final EntityReturnReader returnReader;
-		private final List<EntityReferenceReader> entityReferenceReaders = new ArrayList<EntityReferenceReader>();
-		private List<CollectionReferenceReader> collectionReferenceReaders = null;
-
-		public EntityLoaderRowReader(LoadPlan loadPlan) {
-			final EntityReturn entityReturn = (EntityReturn) loadPlan.getReturns().get( 0 );
-			this.returnReader = (EntityReturnReader) buildReturnReader( entityReturn );
-
-//			final EntityReferenceReaderListBuildingAccess entityReaders = new EntityReferenceReaderListBuildingAccess() {
-//				@Override
-//				public void add(EntityReferenceReader reader) {
-//					entityReferenceReaders.add( reader );
-//				}
-//			};
+//	private class LocalVisitationStrategy extends LoadPlanVisitationStrategyAdapter {
+//		private boolean hadSubselectFetches = false;
 //
-//			final CollectionReferenceReaderListBuildingAccess collectionReaders = new CollectionReferenceReaderListBuildingAccess() {
-//				@Override
-//				public void add(CollectionReferenceReader reader) {
-//					if ( collectionReferenceReaders == null ) {
-//						collectionReferenceReaders = new ArrayList<CollectionReferenceReader>();
-//					}
-//					collectionReferenceReaders.add( reader );
-//				}
-//			};
+//		@Override
+//		public void startingEntityFetch(EntityFetch entityFetch) {
+//		// only collections are currently supported for subselect fetching.
+//		//			hadSubselectFetches = hadSubselectFetches
+//		//					|| entityFetch.getFetchStrategy().getStyle() == FetchStyle.SUBSELECT;
+//		}
 //
-//			buildFetchReaders( entityReaders, collectionReaders, entityReturn, returnReader );
-
-			LoadPlanVisitor.visit(
-					loadPlan,
-					new LoadPlanVisitationStrategyAdapter() {
-						@Override
-						public void startingEntityFetch(EntityFetch entityFetch) {
-							entityReferenceReaders.add( new EntityReferenceReader( entityFetch ) );
-						}
-
-						@Override
-						public void startingCollectionFetch(CollectionFetch collectionFetch) {
-							if ( collectionReferenceReaders == null ) {
-								collectionReferenceReaders = new ArrayList<CollectionReferenceReader>();
-							}
-							collectionReferenceReaders.add( new CollectionReferenceReader( collectionFetch ) );
-						}
-					}
-			);
-
-			entityReferenceReaders.add( returnReader );
-		}
-
-//		private void buildFetchReaders(
-//				EntityReferenceReaderListBuildingAccess entityReaders,
-//				CollectionReferenceReaderListBuildingAccess collectionReaders,
-//				FetchOwner fetchOwner,
-//				EntityReferenceReader entityReferenceReader) {
-//			for ( Fetch fetch : fetchOwner.getFetches() ) {
-//				if ( CollectionFetch.class.isInstance( fetch ) ) {
-//					final CollectionFetch collectionFetch = (CollectionFetch) fetch;
-//					buildFetchReaders(
-//							entityReaders,
-//							collectionReaders,
-//							collectionFetch.getIndexGraph(),
-//							null
-//					);
-//					buildFetchReaders(
-//							entityReaders,
-//							collectionReaders,
-//							collectionFetch.getElementGraph(),
-//							null
-//					);
-//					collectionReaders.add( new CollectionReferenceReader( collectionFetch ) );
-//				}
-//				else if ( CompositeFetch.class.isInstance( fetch ) ) {
-//					buildFetchReaders(
-//							entityReaders,
-//							collectionReaders,
-//							(CompositeFetch) fetch,
-//							entityReferenceReader
-//					);
-//				}
-//				else {
-//					final EntityFetch entityFetch = (EntityFetch) fetch;
-//					if ( entityFetch.getFetchedType().isOneToOne() ) {
-//						// entityReferenceReader should reference the owner still...
-//						if ( entityReferenceReader == null ) {
-//							throw new IllegalStateException( "Entity reader for one-to-one fetch owner not known" );
+//		@Override
+//		public void startingCollectionFetch(CollectionFetch collectionFetch) {
+//			hadSubselectFetches = hadSubselectFetches
+//					|| collectionFetch.getFetchStrategy().getStyle() == FetchStyle.SUBSELECT;
+//		}
+//	}
+//
+//	private class MixedReturnRowReader extends AbstractRowReader implements RowReader {
+//		private final List<ReturnReader> returnReaders;
+//		private List<EntityReferenceReader> entityReferenceReaders = new ArrayList<EntityReferenceReader>();
+//		private List<CollectionReferenceReader> collectionReferenceReaders = new ArrayList<CollectionReferenceReader>();
+//
+//		private final int numberOfReturns;
+//
+//		public MixedReturnRowReader(LoadPlan loadPlan) {
+//			LoadPlanVisitor.visit(
+//					loadPlan,
+//					new LoadPlanVisitationStrategyAdapter() {
+//						@Override
+//						public void startingEntityFetch(EntityFetch entityFetch) {
+//							entityReferenceReaders.add( new EntityReferenceReader( entityFetch ) );
 //						}
-//						final EntityReferenceReader fetchReader = new OneToOneFetchReader(
-//								entityFetch,
-//								entityReferenceReader.getEntityReference()
-//						);
-//					}
-//					else {
 //
+//						@Override
+//						public void startingCollectionFetch(CollectionFetch collectionFetch) {
+//							collectionReferenceReaders.add( new CollectionReferenceReader( collectionFetch ) );
+//						}
 //					}
+//			);
+//
+//			final List<ReturnReader> readers = new ArrayList<ReturnReader>();
+//
+//			for ( Return rtn : loadPlan.getReturns() ) {
+//				final ReturnReader returnReader = buildReturnReader( rtn );
+//				if ( EntityReferenceReader.class.isInstance( returnReader ) ) {
+//					entityReferenceReaders.add( (EntityReferenceReader) returnReader );
 //				}
+//				readers.add( returnReader );
 //			}
-//			//To change body of created methods use File | Settings | File Templates.
+//
+//			this.returnReaders = readers;
+//			this.numberOfReturns = readers.size();
 //		}
-
-		@Override
-		protected List<EntityReferenceReader> getEntityReferenceReaders() {
-			return entityReferenceReaders;
-		}
-
-		@Override
-		protected List<CollectionReferenceReader> getCollectionReferenceReaders() {
-			return collectionReferenceReaders;
-		}
-
-		@Override
-		protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
-			return returnReader.read( resultSet, context );
-		}
-	}
-
-	private class CollectionInitializerRowReader extends AbstractRowReader implements RowReader {
-		private final CollectionReturnReader returnReader;
-
-		private List<EntityReferenceReader> entityReferenceReaders = null;
-		private final List<CollectionReferenceReader> collectionReferenceReaders = new ArrayList<CollectionReferenceReader>();
-
-		public CollectionInitializerRowReader(LoadPlan loadPlan) {
-			returnReader = (CollectionReturnReader) buildReturnReader( loadPlan.getReturns().get( 0 ) );
-
-			LoadPlanVisitor.visit(
-					loadPlan,
-					new LoadPlanVisitationStrategyAdapter() {
-						@Override
-						public void startingEntityFetch(EntityFetch entityFetch) {
-							if ( entityReferenceReaders == null ) {
-								entityReferenceReaders = new ArrayList<EntityReferenceReader>();
-							}
-							entityReferenceReaders.add( new EntityReferenceReader( entityFetch ) );
-						}
-
-						@Override
-						public void startingCollectionFetch(CollectionFetch collectionFetch) {
-							collectionReferenceReaders.add( new CollectionReferenceReader( collectionFetch ) );
-						}
-					}
-			);
-
-			collectionReferenceReaders.add( returnReader );
-		}
-
-		@Override
-		protected List<EntityReferenceReader> getEntityReferenceReaders() {
-			return entityReferenceReaders;
-		}
-
-		@Override
-		protected List<CollectionReferenceReader> getCollectionReferenceReaders() {
-			return collectionReferenceReaders;
-		}
-
-		@Override
-		protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
-			return returnReader.read( resultSet, context );
-		}
-	}
+//
+//		@Override
+//		protected List<EntityReferenceReader> getEntityReferenceReaders() {
+//			return entityReferenceReaders;
+//		}
+//
+//		@Override
+//		protected List<CollectionReferenceReader> getCollectionReferenceReaders() {
+//			return collectionReferenceReaders;
+//		}
+//
+//		@Override
+//		protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
+//			Object[] logicalRow = new Object[ numberOfReturns ];
+//			int pos = 0;
+//			for ( ReturnReader reader : returnReaders ) {
+//				logicalRow[pos] = reader.read( resultSet, context );
+//				pos++;
+//			}
+//			return logicalRow;
+//		}
+//	}
+//
+//	private class CollectionInitializerRowReader extends AbstractRowReader implements RowReader {
+//		private final CollectionReturnReader returnReader;
+//
+//		private List<EntityReferenceReader> entityReferenceReaders = null;
+//		private final List<CollectionReferenceReader> collectionReferenceReaders = new ArrayList<CollectionReferenceReader>();
+//
+//		public CollectionInitializerRowReader(LoadPlan loadPlan) {
+//			returnReader = (CollectionReturnReader) buildReturnReader( loadPlan.getReturns().get( 0 ) );
+//
+//			LoadPlanVisitor.visit(
+//					loadPlan,
+//					new LoadPlanVisitationStrategyAdapter() {
+//						@Override
+//						public void startingEntityFetch(EntityFetch entityFetch) {
+//							if ( entityReferenceReaders == null ) {
+//								entityReferenceReaders = new ArrayList<EntityReferenceReader>();
+//							}
+//							entityReferenceReaders.add( new EntityReferenceReader( entityFetch ) );
+//						}
+//
+//						@Override
+//						public void startingCollectionFetch(CollectionFetch collectionFetch) {
+//							collectionReferenceReaders.add( new CollectionReferenceReader( collectionFetch ) );
+//						}
+//					}
+//			);
+//
+//			collectionReferenceReaders.add( returnReader );
+//		}
+//
+//		@Override
+//		protected List<EntityReferenceReader> getEntityReferenceReaders() {
+//			return entityReferenceReaders;
+//		}
+//
+//		@Override
+//		protected List<CollectionReferenceReader> getCollectionReferenceReaders() {
+//			return collectionReferenceReaders;
+//		}
+//
+//		@Override
+//		protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
+//			return returnReader.read( resultSet, context );
+//		}
+//	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ScalarReturnReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ScalarReturnReader.java
index e36347f0b0..0ddc12ffe6 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ScalarReturnReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ScalarReturnReader.java
@@ -1,52 +1,54 @@
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
 package org.hibernate.loader.plan.exec.process.internal;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
 import org.hibernate.loader.plan.exec.process.spi.ReturnReader;
 import org.hibernate.loader.plan.spi.ScalarReturn;
 
 /**
  * @author Steve Ebersole
  */
 public class ScalarReturnReader implements ReturnReader {
 	private final ScalarReturn scalarReturn;
+	private final String[] aliases;
 
-	public ScalarReturnReader(ScalarReturn scalarReturn) {
+	public ScalarReturnReader(ScalarReturn scalarReturn, String[] aliases) {
 		this.scalarReturn = scalarReturn;
+		this.aliases = aliases;
 	}
 
 	@Override
 	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
 		return scalarReturn.getType().nullSafeGet(
 				resultSet,
-				context.getAliasResolutionContext().resolveScalarColumnAliases( scalarReturn ),
+				aliases,
 				context.getSession(),
 				null
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/AbstractRowReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/AbstractRowReader.java
new file mode 100644
index 0000000000..0bf98b8c89
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/AbstractRowReader.java
@@ -0,0 +1,91 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan.exec.process.spi;
+
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.util.List;
+
+import org.hibernate.loader.plan.exec.process.internal.CollectionReferenceReader;
+import org.hibernate.loader.plan.exec.process.internal.EntityReferenceReader;
+import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
+import org.hibernate.loader.plan.exec.spi.RowReader;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractRowReader implements RowReader {
+
+	@Override
+	public Object readRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
+		final List<EntityReferenceReader> entityReferenceReaders = getEntityReferenceReaders();
+		final List<CollectionReferenceReader> collectionReferenceReaders = getCollectionReferenceReaders();
+
+		final boolean hasEntityReferenceReaders = entityReferenceReaders != null && entityReferenceReaders.size() > 0;
+		final boolean hasCollectionReferenceReaders = collectionReferenceReaders != null && collectionReferenceReaders.size() > 0;
+
+		if ( hasEntityReferenceReaders ) {
+			// 	1) allow entity references to resolve identifiers (in 2 steps)
+			for ( EntityReferenceReader entityReferenceReader : entityReferenceReaders ) {
+				entityReferenceReader.hydrateIdentifier( resultSet, context );
+			}
+			for ( EntityReferenceReader entityReferenceReader : entityReferenceReaders ) {
+				entityReferenceReader.resolveEntityKey( resultSet, context );
+			}
+
+
+			// 2) allow entity references to resolve their hydrated state and entity instance
+			for ( EntityReferenceReader entityReferenceReader : entityReferenceReaders ) {
+				entityReferenceReader.hydrateEntityState( resultSet, context );
+			}
+		}
+
+
+		// 3) read the logical row
+
+		Object logicalRow = readLogicalRow( resultSet, context );
+
+
+		// 4) allow entities and collection to read their elements
+		if ( hasEntityReferenceReaders ) {
+			for ( EntityReferenceReader entityReferenceReader : entityReferenceReaders ) {
+				entityReferenceReader.finishUpRow( resultSet, context );
+			}
+		}
+		if ( hasCollectionReferenceReaders ) {
+			for ( CollectionReferenceReader collectionReferenceReader : collectionReferenceReaders ) {
+				collectionReferenceReader.finishUpRow( resultSet, context );
+			}
+		}
+
+		return logicalRow;
+	}
+
+	protected abstract List<EntityReferenceReader> getEntityReferenceReaders();
+	protected abstract List<CollectionReferenceReader> getCollectionReferenceReaders();
+
+	protected abstract Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context)
+			throws SQLException;
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessingContext.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessingContext.java
index 43249d2ed8..fb55263317 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessingContext.java
@@ -1,171 +1,169 @@
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
 package org.hibernate.loader.plan.exec.process.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.loader.EntityAliases;
 import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
 import org.hibernate.loader.plan.exec.spi.LockModeResolver;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.EntityType;
 
 /**
  * @author Steve Ebersole
  */
 public interface ResultSetProcessingContext extends LockModeResolver {
 	public SessionImplementor getSession();
 
 	public QueryParameters getQueryParameters();
 
 	public boolean shouldUseOptionalEntityInformation();
 
 	public boolean shouldReturnProxies();
 
 	public LoadPlan getLoadPlan();
 
 	/**
 	 * Holds all pieces of information known about an entity reference in relation to each row as we process the
 	 * result set.  Caches these values and makes it easy for access while processing Fetches.
 	 */
 	public static interface EntityReferenceProcessingState {
 		/**
 		 * The EntityReference for which this is collecting process state
 		 *
 		 * @return The EntityReference
 		 */
 		public EntityReference getEntityReference();
 
 		/**
 		 * Register the fact that no identifier was found on attempt to hydrate it from ResultSet
 		 */
 		public void registerMissingIdentifier();
 
 		/**
 		 *
 		 * @return
 		 */
 		public boolean isMissingIdentifier();
 
 		/**
 		 * Register the hydrated form (raw Type-read ResultSet values) of the entity's identifier for the row
 		 * currently being processed.
 		 *
 		 * @param hydratedForm The entity identifier hydrated state
 		 */
 		public void registerIdentifierHydratedForm(Object hydratedForm);
 
 		/**
 		 * Obtain the hydrated form (the raw Type-read ResultSet values) of the entity's identifier
 		 *
 		 * @return The entity identifier hydrated state
 		 */
 		public Object getIdentifierHydratedForm();
 
 		/**
 		 * Register the processed EntityKey for this Entity for the row currently being processed.
 		 *
 		 * @param entityKey The processed EntityKey for this EntityReference
 		 */
 		public void registerEntityKey(EntityKey entityKey);
 
 		/**
 		 * Obtain the registered EntityKey for this EntityReference for the row currently being processed.
 		 *
 		 * @return The registered EntityKey for this EntityReference
 		 */
 		public EntityKey getEntityKey();
 
 		public void registerHydratedState(Object[] hydratedState);
 		public Object[] getHydratedState();
 
 		// usually uninitialized at this point
 		public void registerEntityInstance(Object instance);
 
 		// may be uninitialized
 		public Object getEntityInstance();
 
 	}
 
 	public EntityReferenceProcessingState getProcessingState(EntityReference entityReference);
 
 	/**
 	 * Find the EntityReferenceProcessingState for the FetchOwner of the given Fetch.
 	 *
 	 * @param fetch The Fetch for which to find the EntityReferenceProcessingState of its FetchOwner.
 	 *
 	 * @return The FetchOwner's EntityReferenceProcessingState
 	 */
 	public EntityReferenceProcessingState getOwnerProcessingState(Fetch fetch);
 
 
-	public AliasResolutionContext getAliasResolutionContext();
-
 	public void registerHydratedEntity(EntityReference entityReference, EntityKey entityKey, Object entityInstance);
 
 	public static interface EntityKeyResolutionContext {
 		public EntityPersister getEntityPersister();
 		public LockMode getLockMode();
 		public EntityReference getEntityReference();
 	}
 
-	public Object resolveEntityKey(EntityKey entityKey, EntityKeyResolutionContext entityKeyContext);
+//	public Object resolveEntityKey(EntityKey entityKey, EntityKeyResolutionContext entityKeyContext);
 
 
 	// should be able to get rid of the methods below here from the interface ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	public void checkVersion(
-			ResultSet resultSet,
-			EntityPersister persister,
-			EntityAliases entityAliases,
-			EntityKey entityKey,
-			Object entityInstance) throws SQLException;
-
-	public String getConcreteEntityTypeName(
-			ResultSet resultSet,
-			EntityPersister persister,
-			EntityAliases entityAliases,
-			EntityKey entityKey) throws SQLException;
-
-	public void loadFromResultSet(
-			ResultSet resultSet,
-			Object entityInstance,
-			String concreteEntityTypeName,
-			EntityKey entityKey,
-			EntityAliases entityAliases,
-			LockMode acquiredLockMode,
-			EntityPersister persister,
-			FetchStrategy fetchStrategy,
-			boolean eagerFetch,
-			EntityType associationType) throws SQLException;
+//	public void checkVersion(
+//			ResultSet resultSet,
+//			EntityPersister persister,
+//			EntityAliases entityAliases,
+//			EntityKey entityKey,
+//			Object entityInstance) throws SQLException;
+//
+//	public String getConcreteEntityTypeName(
+//			ResultSet resultSet,
+//			EntityPersister persister,
+//			EntityAliases entityAliases,
+//			EntityKey entityKey) throws SQLException;
+//
+//	public void loadFromResultSet(
+//			ResultSet resultSet,
+//			Object entityInstance,
+//			String concreteEntityTypeName,
+//			EntityKey entityKey,
+//			EntityAliases entityAliases,
+//			LockMode acquiredLockMode,
+//			EntityPersister persister,
+//			FetchStrategy fetchStrategy,
+//			boolean eagerFetch,
+//			EntityType associationType) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessor.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessor.java
index 211d48d83a..bd965aedc2 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ResultSetProcessor.java
@@ -1,78 +1,74 @@
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
 package org.hibernate.loader.plan.exec.process.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.List;
 
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
 import org.hibernate.loader.spi.AfterLoadAction;
 import org.hibernate.loader.spi.LoadPlanAdvisor;
 import org.hibernate.transform.ResultTransformer;
 
 /**
  * Contract for processing JDBC ResultSets.  Separated because ResultSets can be chained and we'd really like to
  * reuse this logic across all result sets.
  * <p/>
  * todo : investigate having this work with non-JDBC results; maybe just typed as Object? or a special Result contract?
  *
  * @author Steve Ebersole
  */
 public interface ResultSetProcessor {
 
 	public ScrollableResultSetProcessor toOnDemandForm();
 
 	/**
 	 * Process an entire ResultSet, performing all extractions.
 	 *
 	 * Semi-copy of {@link org.hibernate.loader.Loader#doQuery}, with focus on just the ResultSet processing bit.
 	 *
-	 * @param loadPlanAdvisor A dynamic advisor on the load plan.
 	 * @param resultSet The result set being processed.
 	 * @param session The originating session
 	 * @param queryParameters The "parameters" used to build the query
 	 * @param returnProxies Can proxies be returned (not the same as can they be created!)
 	 * @param forcedResultTransformer My old "friend" ResultTransformer...
 	 * @param afterLoadActions Actions to be performed after loading an entity.
 	 *
 	 * @return The extracted results list.
 	 *
 	 * @throws java.sql.SQLException Indicates a problem access the JDBC ResultSet
 	 */
 	public List extractResults(
-			LoadPlanAdvisor loadPlanAdvisor,
 			ResultSet resultSet,
 			SessionImplementor session,
 			QueryParameters queryParameters,
 			NamedParameterContext namedParameterContext,
-			AliasResolutionContext aliasResolutionContext,
 			boolean returnProxies,
 			boolean readOnly,
 			ResultTransformer forcedResultTransformer,
 			List<AfterLoadAction> afterLoadActions) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ReturnReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ReturnReader.java
index a674d8ebd6..bbf487f339 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ReturnReader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/spi/ReturnReader.java
@@ -1,46 +1,46 @@
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
 package org.hibernate.loader.plan.exec.process.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 /**
- * Handles reading results from a JDBC ResultSet relating to a single Return object.
+ * Handles reading a single root Return object
  *
  * @author Steve Ebersole
  */
 public interface ReturnReader {
 	/**
 	 * Essentially performs the second phase of two-phase loading.
 	 *
 	 * @param resultSet The result set being processed
 	 * @param context The context for the processing
 	 *
 	 * @return The read object
 	 *
 	 * @throws SQLException Indicates a problem access the JDBC result set
 	 */
 	public Object read(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/internal/EntityLoadQueryBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/internal/EntityLoadQueryBuilderImpl.java
index 5db0e113e6..b874b870f8 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/internal/EntityLoadQueryBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/internal/EntityLoadQueryBuilderImpl.java
@@ -1,213 +1,215 @@
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
 package org.hibernate.loader.plan.exec.query.internal;
 
-import org.hibernate.LockOptions;
-import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.plan.exec.internal.Helper;
+import org.hibernate.loader.plan.exec.internal.LoadQueryBuilderHelper;
+import org.hibernate.loader.plan.exec.process.internal.CollectionReferenceReader;
+import org.hibernate.loader.plan.exec.process.internal.EntityReferenceReader;
 import org.hibernate.loader.plan.exec.query.spi.EntityLoadQueryBuilder;
 import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
 import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.exec.spi.ReaderCollector;
+import org.hibernate.loader.plan.exec.spi.RowReader;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.LoadPlan;
-import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.sql.ConditionFragment;
 import org.hibernate.sql.DisjunctionFragment;
 import org.hibernate.sql.InFragment;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityLoadQueryBuilderImpl implements EntityLoadQueryBuilder {
 	/**
 	 * Singleton access
 	 */
 	public static final EntityLoadQueryBuilderImpl INSTANCE = new EntityLoadQueryBuilderImpl();
 
 	@Override
 	public String generateSql(
 			LoadPlan loadPlan,
 			SessionFactoryImplementor factory,
 			QueryBuildingParameters buildingParameters,
 			AliasResolutionContext aliasResolutionContext) {
-		final EntityReturn rootReturn = extractRootReturn( loadPlan );
+		final EntityReturn rootReturn = Helper.INSTANCE.extractRootReturn( loadPlan, EntityReturn.class );
 
 		return generateSql(
 				( (Queryable) rootReturn.getEntityPersister() ).getKeyColumnNames(),
 				rootReturn,
 				factory,
 				buildingParameters,
 				aliasResolutionContext
 		);
 	}
 
-	private static EntityReturn extractRootReturn(LoadPlan loadPlan) {
-		if ( loadPlan.getReturns().size() == 0 ) {
-			throw new IllegalStateException( "LoadPlan contained no root returns" );
-		}
-		else if ( loadPlan.getReturns().size() > 1 ) {
-			throw new IllegalStateException( "LoadPlan contained more than one root returns" );
-		}
-
-		final Return rootReturn = loadPlan.getReturns().get( 0 );
-		if ( !EntityReturn.class.isInstance( rootReturn ) ) {
-			throw new IllegalStateException(
-					String.format(
-							"Unexpected LoadPlan root return; expecting %s, but found %s",
-							EntityReturn.class.getName(),
-							rootReturn.getClass().getName()
-					)
-			);
-		}
-
-		return (EntityReturn) rootReturn;
-	}
-
 	@Override
 	public String generateSql(
 			String[] keyColumnNames,
 			LoadPlan loadPlan,
 			SessionFactoryImplementor factory,
 			QueryBuildingParameters buildingParameters,
 			AliasResolutionContext aliasResolutionContext) {
-		final EntityReturn rootReturn = extractRootReturn( loadPlan );
+		final EntityReturn rootReturn = Helper.INSTANCE.extractRootReturn( loadPlan, EntityReturn.class );
+
+		final String[] keyColumnNamesToUse = keyColumnNames != null
+				? keyColumnNames
+				: ( (Queryable) rootReturn.getEntityPersister() ).getIdentifierColumnNames();
 
 		return generateSql(
-				keyColumnNames,
+				keyColumnNamesToUse,
 				rootReturn,
 				factory,
 				buildingParameters,
 				aliasResolutionContext
 		);
 	}
 
 	protected String generateSql(
 			String[] keyColumnNames,
 			EntityReturn rootReturn,
 			SessionFactoryImplementor factory,
 			QueryBuildingParameters buildingParameters,
 			AliasResolutionContext aliasResolutionContext) {
 		final SelectStatementBuilder select = new SelectStatementBuilder( factory.getDialect() );
 
 		// apply root entity return specifics
-		applyRootReturnSpecifics( select, keyColumnNames, rootReturn, factory, buildingParameters, aliasResolutionContext );
+		applyRootReturnSpecifics(
+				select,
+				keyColumnNames,
+				rootReturn,
+				factory,
+				buildingParameters,
+				aliasResolutionContext
+		);
 
 		LoadQueryBuilderHelper.applyJoinFetches(
 				select,
 				factory,
 				rootReturn,
 				buildingParameters,
-				aliasResolutionContext
+				aliasResolutionContext,
+				new ReaderCollector() {
+
+					@Override
+					public void addReader(CollectionReferenceReader collectionReferenceReader) {
+					}
+
+					@Override
+					public void addReader(EntityReferenceReader entityReferenceReader) {
+					}
+				}
 		);
 
 		return select.toStatementString();
 	}
 
 	protected void applyRootReturnSpecifics(
 			SelectStatementBuilder select,
 			String[] keyColumnNames,
 			EntityReturn rootReturn,
 			SessionFactoryImplementor factory,
 			QueryBuildingParameters buildingParameters,
 			AliasResolutionContext aliasResolutionContext) {
 		final String rootAlias = aliasResolutionContext.resolveAliases( rootReturn ).getTableAlias();
 		final OuterJoinLoadable rootLoadable = (OuterJoinLoadable) rootReturn.getEntityPersister();
 		final Queryable rootQueryable = (Queryable) rootReturn.getEntityPersister();
 
 		applyKeyRestriction( select, rootAlias, keyColumnNames, buildingParameters.getBatchSize() );
 		select.appendRestrictions(
 				rootQueryable.filterFragment(
 						rootAlias,
 						buildingParameters.getQueryInfluencers().getEnabledFilters()
 				)
 		);
 		select.appendRestrictions( rootLoadable.whereJoinFragment( rootAlias, true, true ) );
 		select.appendSelectClauseFragment(
 				rootLoadable.selectFragment(
 						rootAlias,
 						aliasResolutionContext.resolveAliases( rootReturn ).getColumnAliases().getSuffix()
 				)
 		);
 
 		final String fromTableFragment;
 		if ( buildingParameters.getLockOptions() != null ) {
 			fromTableFragment = factory.getDialect().appendLockHint(
 					buildingParameters.getLockOptions(),
 					rootLoadable.fromTableFragment( rootAlias )
 			);
 			select.setLockOptions( buildingParameters.getLockOptions() );
 		}
 		else if ( buildingParameters.getLockMode() != null ) {
 			fromTableFragment = factory.getDialect().appendLockHint(
 					buildingParameters.getLockMode(),
 					rootLoadable.fromTableFragment( rootAlias )
 			);
 			select.setLockMode( buildingParameters.getLockMode() );
 		}
 		else {
 			fromTableFragment = rootLoadable.fromTableFragment( rootAlias );
 		}
 		select.appendFromClauseFragment( fromTableFragment + rootLoadable.fromJoinFragment( rootAlias, true, true ) );
 	}
 
 	private void applyKeyRestriction(SelectStatementBuilder select, String alias, String[] keyColumnNames, int batchSize) {
 		if ( keyColumnNames.length==1 ) {
 			// NOT A COMPOSITE KEY
 			// 		for batching, use "foo in (?, ?, ?)" for batching
 			//		for no batching, use "foo = ?"
 			// (that distinction is handled inside InFragment)
 			final InFragment in = new InFragment().setColumn( alias, keyColumnNames[0] );
 			for ( int i = 0; i < batchSize; i++ ) {
 				in.addValue( "?" );
 			}
 			select.appendRestrictions( in.toFragmentString() );
 		}
 		else {
 			// A COMPOSITE KEY...
 			final ConditionFragment keyRestrictionBuilder = new ConditionFragment()
 					.setTableAlias( alias )
 					.setCondition( keyColumnNames, "?" );
 			final String keyRestrictionFragment = keyRestrictionBuilder.toFragmentString();
 
 			StringBuilder restrictions = new StringBuilder();
 			if ( batchSize==1 ) {
 				// for no batching, use "foo = ? and bar = ?"
 				restrictions.append( keyRestrictionFragment );
 			}
 			else {
 				// for batching, use "( (foo = ? and bar = ?) or (foo = ? and bar = ?) )"
 				restrictions.append( '(' );
 				DisjunctionFragment df = new DisjunctionFragment();
 				for ( int i=0; i<batchSize; i++ ) {
 					df.addCondition( keyRestrictionFragment );
 				}
 				restrictions.append( df.toFragmentString() );
 				restrictions.append( ')' );
 			}
 			select.appendRestrictions( restrictions.toString() );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/spi/EntityLoadQueryBuilder.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/spi/EntityLoadQueryBuilder.java
index 16a9a06a26..a777454a4a 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/spi/EntityLoadQueryBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/query/spi/EntityLoadQueryBuilder.java
@@ -1,73 +1,71 @@
 /*
  * jDocBook, processing of DocBook sources
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
 package org.hibernate.loader.plan.exec.query.spi;
 
-import org.hibernate.LockOptions;
-import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
 import org.hibernate.loader.plan.spi.LoadPlan;
 
 /**
  * Contract for generating the query (currently the SQL string specifically) based on a LoadPlan with a
  * single root EntityReturn
  *
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public interface EntityLoadQueryBuilder {
 	/**
 	 * Generates the query for the performing load.
 	 *
 	 * @param loadPlan The load
 	 * @param factory The session factory.
 	 * @param buildingParameters Parameters influencing the building of the query
 	 * @param aliasResolutionContext The alias resolution context.
 	 *
 	 * @return the SQL string for performing the load
 	 */
 	String generateSql(
 			LoadPlan loadPlan,
 			SessionFactoryImplementor factory,
 			QueryBuildingParameters buildingParameters,
 			AliasResolutionContext aliasResolutionContext);
 
 	/**
 	 * Generates the query for the performing load, based on the specified key column(s).
 	 *
 	 * @param keyColumnNames The names of the key columns to use
 	 * @param loadPlan The load
 	 * @param factory The session factory.
 	 * @param buildingParameters Parameters influencing the building of the query
 	 * @param aliasResolutionContext The alias resolution context.
 	 *
 	 * @return the SQL string for performing the load
 	 */
 	String generateSql(
 			String[] keyColumnNames,
 			LoadPlan loadPlan,
 			SessionFactoryImplementor factory,
 			QueryBuildingParameters buildingParameters,
 			AliasResolutionContext aliasResolutionContext);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/EntityLoadQueryDetails.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/EntityLoadQueryDetails.java
new file mode 100644
index 0000000000..234d56006a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/EntityLoadQueryDetails.java
@@ -0,0 +1,315 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan.exec.spi;
+
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.util.ArrayList;
+import java.util.Collections;
+import java.util.List;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.internal.Helper;
+import org.hibernate.loader.plan.exec.internal.LoadQueryBuilderHelper;
+import org.hibernate.loader.plan.exec.process.internal.CollectionReferenceReader;
+import org.hibernate.loader.plan.exec.process.internal.EntityIdentifierReaderImpl;
+import org.hibernate.loader.plan.exec.process.internal.EntityReferenceReader;
+import org.hibernate.loader.plan.exec.process.internal.EntityReturnReader;
+import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
+import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
+import org.hibernate.loader.plan.exec.process.spi.AbstractRowReader;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
+import org.hibernate.loader.plan.exec.query.internal.SelectStatementBuilder;
+import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
+import org.hibernate.loader.plan.spi.EntityReturn;
+import org.hibernate.loader.plan.spi.LoadPlan;
+import org.hibernate.persister.entity.OuterJoinLoadable;
+import org.hibernate.persister.entity.Queryable;
+import org.hibernate.sql.ConditionFragment;
+import org.hibernate.sql.DisjunctionFragment;
+import org.hibernate.sql.InFragment;
+
+import static org.hibernate.loader.plan.exec.internal.LoadQueryBuilderHelper.FetchStats;
+
+/**
+ * Handles interpreting a LoadPlan (for loading of an entity) by:<ul>
+ *     <li>generating the SQL query to perform</li>
+ *     <li>creating the readers needed to read the results from the SQL's ResultSet</li>
+ * </ul>
+ *
+ * @author Steve Ebersole
+ */
+public class EntityLoadQueryDetails implements LoadQueryDetails {
+	// todo : keep around the LoadPlan?  Any benefit?
+	private final LoadPlan loadPlan;
+
+	private final String sqlStatement;
+	private final ResultSetProcessor resultSetProcessor;
+
+	/**
+	 * Constructs a EntityLoadQueryDetails object from the given inputs.
+	 *
+	 * @param loadPlan The load plan
+	 * @param keyColumnNames The columns to load the entity by (the PK columns or some other unique set of columns)
+	 * @param buildingParameters And influencers that would affect the generated SQL (mostly we are concerned with those
+	 * that add additional joins here)
+	 * @param factory The SessionFactory
+	 *
+	 * @return The EntityLoadQueryDetails
+	 */
+	public static EntityLoadQueryDetails makeForBatching(
+			LoadPlan loadPlan,
+			String[] keyColumnNames,
+			QueryBuildingParameters buildingParameters,
+			SessionFactoryImplementor factory) {
+		final int batchSize = buildingParameters.getBatchSize();
+		final boolean shouldUseOptionalEntityInformation = batchSize == 1;
+
+		return new EntityLoadQueryDetails(
+				loadPlan,
+				keyColumnNames,
+				shouldUseOptionalEntityInformation,
+				buildingParameters,
+				factory
+		);
+	}
+
+	protected EntityLoadQueryDetails(
+			LoadPlan loadPlan,
+			String[] keyColumnNames,
+			boolean shouldUseOptionalEntityInformation,
+			QueryBuildingParameters buildingParameters,
+			SessionFactoryImplementor factory) {
+		this.loadPlan = loadPlan;
+
+		final SelectStatementBuilder select = new SelectStatementBuilder( factory.getDialect() );
+		final EntityReturn rootReturn = Helper.INSTANCE.extractRootReturn( loadPlan, EntityReturn.class );
+		final AliasResolutionContext aliasResolutionContext = new AliasResolutionContextImpl( factory );
+		final ReaderCollectorImpl readerCollector = new ReaderCollectorImpl();
+
+		final String[] keyColumnNamesToUse = keyColumnNames != null
+				? keyColumnNames
+				: ( (Queryable) rootReturn.getEntityPersister() ).getIdentifierColumnNames();
+
+		// apply root entity return specifics
+		applyRootReturnSpecifics(
+				select,
+				keyColumnNamesToUse,
+				rootReturn,
+				factory,
+				buildingParameters,
+				aliasResolutionContext
+		);
+		readerCollector.addReader(
+				new EntityReturnReader(
+						rootReturn,
+						aliasResolutionContext.resolveAliases( rootReturn ),
+						new EntityIdentifierReaderImpl(
+								rootReturn,
+								aliasResolutionContext.resolveAliases( rootReturn ),
+								Collections.<EntityReferenceReader>emptyList()
+						)
+				)
+		);
+
+		FetchStats fetchStats = LoadQueryBuilderHelper.applyJoinFetches(
+				select,
+				factory,
+				rootReturn,
+				buildingParameters,
+				aliasResolutionContext,
+				readerCollector
+		);
+
+		this.sqlStatement = select.toStatementString();
+		this.resultSetProcessor = new ResultSetProcessorImpl(
+				loadPlan,
+				readerCollector.buildRowReader(),
+				fetchStats.hasSubselectFetches()
+		);
+	}
+
+	protected void applyRootReturnSpecifics(
+			SelectStatementBuilder select,
+			String[] keyColumnNames,
+			EntityReturn rootReturn,
+			SessionFactoryImplementor factory,
+			QueryBuildingParameters buildingParameters,
+			AliasResolutionContext aliasResolutionContext) {
+		final String rootAlias = aliasResolutionContext.resolveAliases( rootReturn ).getTableAlias();
+		final OuterJoinLoadable rootLoadable = (OuterJoinLoadable) rootReturn.getEntityPersister();
+		final Queryable rootQueryable = (Queryable) rootReturn.getEntityPersister();
+
+		applyKeyRestriction( select, rootAlias, keyColumnNames, buildingParameters.getBatchSize() );
+		select.appendRestrictions(
+				rootQueryable.filterFragment(
+						rootAlias,
+						buildingParameters.getQueryInfluencers().getEnabledFilters()
+				)
+		);
+		select.appendRestrictions( rootLoadable.whereJoinFragment( rootAlias, true, true ) );
+		select.appendSelectClauseFragment(
+				rootLoadable.selectFragment(
+						rootAlias,
+						aliasResolutionContext.resolveAliases( rootReturn ).getColumnAliases().getSuffix()
+				)
+		);
+
+		final String fromTableFragment;
+		if ( buildingParameters.getLockOptions() != null ) {
+			fromTableFragment = factory.getDialect().appendLockHint(
+					buildingParameters.getLockOptions(),
+					rootLoadable.fromTableFragment( rootAlias )
+			);
+			select.setLockOptions( buildingParameters.getLockOptions() );
+		}
+		else if ( buildingParameters.getLockMode() != null ) {
+			fromTableFragment = factory.getDialect().appendLockHint(
+					buildingParameters.getLockMode(),
+					rootLoadable.fromTableFragment( rootAlias )
+			);
+			select.setLockMode( buildingParameters.getLockMode() );
+		}
+		else {
+			fromTableFragment = rootLoadable.fromTableFragment( rootAlias );
+		}
+		select.appendFromClauseFragment( fromTableFragment + rootLoadable.fromJoinFragment( rootAlias, true, true ) );
+	}
+
+	private void applyKeyRestriction(SelectStatementBuilder select, String alias, String[] keyColumnNames, int batchSize) {
+		if ( keyColumnNames.length==1 ) {
+			// NOT A COMPOSITE KEY
+			// 		for batching, use "foo in (?, ?, ?)" for batching
+			//		for no batching, use "foo = ?"
+			// (that distinction is handled inside InFragment)
+			final InFragment in = new InFragment().setColumn( alias, keyColumnNames[0] );
+			for ( int i = 0; i < batchSize; i++ ) {
+				in.addValue( "?" );
+			}
+			select.appendRestrictions( in.toFragmentString() );
+		}
+		else {
+			// A COMPOSITE KEY...
+			final ConditionFragment keyRestrictionBuilder = new ConditionFragment()
+					.setTableAlias( alias )
+					.setCondition( keyColumnNames, "?" );
+			final String keyRestrictionFragment = keyRestrictionBuilder.toFragmentString();
+
+			StringBuilder restrictions = new StringBuilder();
+			if ( batchSize==1 ) {
+				// for no batching, use "foo = ? and bar = ?"
+				restrictions.append( keyRestrictionFragment );
+			}
+			else {
+				// for batching, use "( (foo = ? and bar = ?) or (foo = ? and bar = ?) )"
+				restrictions.append( '(' );
+				DisjunctionFragment df = new DisjunctionFragment();
+				for ( int i=0; i<batchSize; i++ ) {
+					df.addCondition( keyRestrictionFragment );
+				}
+				restrictions.append( df.toFragmentString() );
+				restrictions.append( ')' );
+			}
+			select.appendRestrictions( restrictions.toString() );
+		}
+	}
+
+	@Override
+	public String getSqlStatement() {
+		return sqlStatement;
+	}
+
+	@Override
+	public ResultSetProcessor getResultSetProcessor() {
+		return resultSetProcessor;
+	}
+
+	private static class ReaderCollectorImpl implements ReaderCollector {
+		private EntityReturnReader rootReturnReader;
+		private List<EntityReferenceReader> entityReferenceReaders;
+		private List<CollectionReferenceReader> collectionReferenceReaders;
+
+		@Override
+		public void addReader(CollectionReferenceReader collectionReferenceReader) {
+			if ( collectionReferenceReaders == null ) {
+				collectionReferenceReaders = new ArrayList<CollectionReferenceReader>();
+			}
+			collectionReferenceReaders.add( collectionReferenceReader );
+		}
+
+		@Override
+		public void addReader(EntityReferenceReader entityReferenceReader) {
+			if ( EntityReturnReader.class.isInstance( entityReferenceReader ) ) {
+				if ( rootReturnReader != null ) {
+					throw new IllegalStateException( "Root return reader already set" );
+				}
+				rootReturnReader = (EntityReturnReader) entityReferenceReader;
+			}
+
+			if ( entityReferenceReaders == null ) {
+				entityReferenceReaders = new ArrayList<EntityReferenceReader>();
+			}
+			entityReferenceReaders.add( entityReferenceReader );
+		}
+
+		public RowReader buildRowReader() {
+			return new EntityLoaderRowReader( rootReturnReader, entityReferenceReaders, collectionReferenceReaders );
+		}
+	}
+
+	public static class EntityLoaderRowReader extends AbstractRowReader implements RowReader {
+		private final EntityReturnReader rootReturnReader;
+		private final List<EntityReferenceReader> entityReferenceReaders;
+		private final List<CollectionReferenceReader> collectionReferenceReaders;
+
+		public EntityLoaderRowReader(
+				EntityReturnReader rootReturnReader,
+				List<EntityReferenceReader> entityReferenceReaders,
+				List<CollectionReferenceReader> collectionReferenceReaders) {
+			this.rootReturnReader = rootReturnReader;
+			this.entityReferenceReaders = entityReferenceReaders != null
+					? entityReferenceReaders
+					: Collections.<EntityReferenceReader>emptyList();
+			this.collectionReferenceReaders = collectionReferenceReaders != null
+					? collectionReferenceReaders
+					: Collections.<CollectionReferenceReader>emptyList();
+		}
+
+		@Override
+		protected List<EntityReferenceReader> getEntityReferenceReaders() {
+			return entityReferenceReaders;
+		}
+
+		@Override
+		protected List<CollectionReferenceReader> getCollectionReferenceReaders() {
+			return collectionReferenceReaders;
+		}
+
+		@Override
+		protected Object readLogicalRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException {
+			return rootReturnReader.read( resultSet, context );
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/LoadQueryDetails.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/LoadQueryDetails.java
index a9447578c1..70f702c47e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/LoadQueryDetails.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/LoadQueryDetails.java
@@ -1,111 +1,36 @@
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
 package org.hibernate.loader.plan.exec.spi;
 
-import org.hibernate.LockMode;
-import org.hibernate.LockOptions;
-import org.hibernate.engine.spi.LoadQueryInfluencers;
-import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
-import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
-import org.hibernate.loader.plan.exec.query.internal.EntityLoadQueryBuilderImpl;
-import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan.spi.LoadPlan;
 
 /**
- * Wraps a LoadPlan (for an entity load) and exposes details about the query and its execution.
- *
  * @author Steve Ebersole
  */
-public class LoadQueryDetails {
-	private final SessionFactoryImplementor factory;
-	private final LoadPlan loadPlan;
-
-	private final AliasResolutionContext aliasResolutionContext;
-	private final String sqlStatement;
-	private final ResultSetProcessor resultSetProcessor;
-
-	/**
-	 * Constructs a LoadQueryDetails object from the given inputs.
-	 *
-	 *
-	 * @param uniqueKeyColumnNames
-	 * @param loadPlan The load plan
-	 * @param factory The SessionFactory
-	 * @param buildingParameters And influencers that would affect the generated SQL (mostly we are concerned with those
-	 * that add additional joins here)
-	 *
-	 * @return The LoadQueryDetails
-	 */
-	public static LoadQueryDetails makeForBatching(
-			String[] uniqueKeyColumnNames,
-			LoadPlan loadPlan,
-			SessionFactoryImplementor factory,
-			QueryBuildingParameters buildingParameters) {
-		final AliasResolutionContext aliasResolutionContext = new AliasResolutionContextImpl( factory );
-		final ResultSetProcessor resultSetProcessor = new ResultSetProcessorImpl( loadPlan, false );
-		final String sqlStatement = EntityLoadQueryBuilderImpl.INSTANCE.generateSql(
-				uniqueKeyColumnNames,
-				loadPlan,
-				factory,
-				buildingParameters,
-				aliasResolutionContext
-		);
-		return new LoadQueryDetails( factory, loadPlan, aliasResolutionContext, resultSetProcessor, sqlStatement );
-	}
-
-	private LoadQueryDetails(
-			SessionFactoryImplementor factory,
-			LoadPlan loadPlan,
-			AliasResolutionContext aliasResolutionContext,
-			ResultSetProcessor resultSetProcessor,
-			String sqlStatement) {
-		this.factory = factory;
-		this.loadPlan = loadPlan;
-		this.aliasResolutionContext = aliasResolutionContext;
-		this.resultSetProcessor = resultSetProcessor;
-		this.sqlStatement = sqlStatement;
-	}
-
-	public SessionFactoryImplementor getFactory() {
-		return factory;
-	}
-
-	public LoadPlan getLoadPlan() {
-		return loadPlan;
-	}
-
-	public AliasResolutionContext getAliasResolutionContext() {
-		return aliasResolutionContext;
-	}
+public interface LoadQueryDetails {
+	public String getSqlStatement();
 
-	public String getSqlStatement() {
-		return sqlStatement;
-	}
+	public ResultSetProcessor getResultSetProcessor();
 
-	public ResultSetProcessor getResultSetProcessor() {
-		return resultSetProcessor;
-	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/ReaderCollector.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/ReaderCollector.java
new file mode 100644
index 0000000000..1981f27250
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/ReaderCollector.java
@@ -0,0 +1,38 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan.exec.spi;
+
+import org.hibernate.loader.plan.exec.process.internal.CollectionReferenceReader;
+import org.hibernate.loader.plan.exec.process.internal.EntityReferenceReader;
+
+/**
+ * Used as a callback mechanism while building the SQL statement to collect the needed ResultSet readers
+ *
+ * @author Steve Ebersole
+ */
+public interface ReaderCollector {
+	public void addReader(CollectionReferenceReader collectionReferenceReader);
+
+	public void addReader(EntityReferenceReader entityReferenceReader);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/RowReader.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/RowReader.java
new file mode 100644
index 0000000000..4a56afc277
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/exec/spi/RowReader.java
@@ -0,0 +1,36 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan.exec.spi;
+
+import java.sql.ResultSet;
+import java.sql.SQLException;
+
+import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessingContextImpl;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface RowReader {
+	Object readRow(ResultSet resultSet, ResultSetProcessingContextImpl context) throws SQLException;
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/EntityReturnImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/EntityReturnImpl.java
new file mode 100644
index 0000000000..ed8c731871
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/EntityReturnImpl.java
@@ -0,0 +1,64 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan.internal;
+
+import org.hibernate.loader.plan.spi.EntityReturn2;
+import org.hibernate.loader.plan.spi.IdentifierDescription;
+import org.hibernate.loader.plan.spi.build.IdentifierDescriptionInjectable;
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EntityReturnImpl implements EntityReturn2, IdentifierDescriptionInjectable {
+	private final String entityQuerySpaceUid;
+	private final EntityPersister entityPersister;
+
+	private IdentifierDescription identifierDescription;
+
+	public EntityReturnImpl(String entityQuerySpaceUid, EntityPersister entityPersister) {
+		this.entityQuerySpaceUid = entityQuerySpaceUid;
+		this.entityPersister = entityPersister;
+	}
+
+	@Override
+	public String getQuerySpaceUid() {
+		return entityQuerySpaceUid;
+	}
+
+	@Override
+	public EntityPersister getEntityPersister() {
+		return entityPersister;
+	}
+
+	@Override
+	public IdentifierDescription getIdentifierDescription() {
+		return identifierDescription;
+	}
+
+	@Override
+	public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
+		this.identifierDescription = identifierDescription;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanImpl.java
index f2d190abad..2fc265837e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/LoadPlanImpl.java
@@ -1,81 +1,102 @@
 /*
  * jDocBook, processing of DocBook sources
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
 package org.hibernate.loader.plan.internal;
 
 import java.util.Collections;
 import java.util.List;
 
 import org.hibernate.loader.plan.spi.CollectionReturn;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.loader.plan.spi.Return;
 
 /**
  * Implementation of LoadPlan.
  *
  * @author Steve Ebersole
  */
 public class LoadPlanImpl implements LoadPlan {
 	private final List<? extends Return> returns;
 	private final Disposition disposition;
 	private final boolean areLazyAttributesForceFetched;
 
 	protected LoadPlanImpl(List<? extends Return> returns, Disposition disposition, boolean areLazyAttributesForceFetched) {
 		this.returns = returns;
 		this.disposition = disposition;
 		this.areLazyAttributesForceFetched = areLazyAttributesForceFetched;
 	}
 
+	/**
+	 * Creates a {@link Disposition#ENTITY_LOADER} LoadPlan.
+	 *
+	 * @param rootReturn The EntityReturn representation of the entity being loaded.
+	 */
 	public LoadPlanImpl(EntityReturn rootReturn) {
 		this( Collections.singletonList( rootReturn ), Disposition.ENTITY_LOADER, false );
 	}
 
+	/**
+	 * Creates a {@link Disposition#COLLECTION_INITIALIZER} LoadPlan.
+	 *
+	 * @param rootReturn The CollectionReturn representation of the collection being initialized.
+	 */
 	public LoadPlanImpl(CollectionReturn rootReturn) {
-		this( Collections.singletonList( rootReturn ), Disposition.ENTITY_LOADER, false );
+		this( Collections.singletonList( rootReturn ), Disposition.COLLECTION_INITIALIZER, false );
 	}
 
+	/**
+	 * Creates a {@link Disposition#MIXED} LoadPlan.
+	 *
+	 * @param returns The mixed Return references
+	 * @param areLazyAttributesForceFetched Should lazy attributes (bytecode enhanced laziness) be fetched also?  This
+	 * effects the eventual SQL SELECT-clause which is why we have it here.  Currently this is "all-or-none"; you
+	 * can request that all lazy properties across all entities in the loadplan be force fetched or none.  There is
+	 * no entity-by-entity option.  {@code FETCH ALL PROPERTIES} is the way this is requested in HQL.  Would be nice to
+	 * consider this entity-by-entity, as opposed to all-or-none.  For example, "fetch the LOB value for the Item.image
+	 * attribute, but no others (leave them lazy)".  Not too concerned about having it at the attribute level.
+	 */
 	public LoadPlanImpl(List<? extends Return> returns, boolean areLazyAttributesForceFetched) {
 		this( returns, Disposition.MIXED, areLazyAttributesForceFetched );
 	}
 
 	@Override
 	public List<? extends Return> getReturns() {
 		return returns;
 	}
 
 	@Override
 	public Disposition getDisposition() {
 		return disposition;
 	}
 
 	@Override
 	public boolean areLazyAttributesForceFetched() {
 		return areLazyAttributesForceFetched;
 	}
 
 	@Override
 	public boolean hasAnyScalarReturns() {
 		return disposition == Disposition.MIXED;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/ScalarReturnImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/ScalarReturnImpl.java
new file mode 100644
index 0000000000..9c455f0c2a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/internal/ScalarReturnImpl.java
@@ -0,0 +1,30 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan.internal;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ScalarReturnImpl {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AnyFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AnyFetch.java
index a26cf822da..13c9bfa110 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AnyFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/AnyFetch.java
@@ -1,141 +1,141 @@
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
 package org.hibernate.loader.plan.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
 import org.hibernate.persister.walking.spi.AnyMappingDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.type.AnyType;
 
 /**
  * @author Steve Ebersole
  */
 public class AnyFetch extends AbstractPlanNode implements Fetch {
 	private final FetchOwner owner;
 	private final AttributeDefinition fetchedAttribute;
 	private final AnyMappingDefinition definition;
 	private final FetchStrategy fetchStrategy;
 
 	private final PropertyPath propertyPath;
 
 	public AnyFetch(
 			SessionFactoryImplementor sessionFactory,
 			FetchOwner owner,
 			AttributeDefinition ownerProperty,
 			AnyMappingDefinition definition,
 			FetchStrategy fetchStrategy) {
 		super( sessionFactory );
 
 		this.owner = owner;
 		this.fetchedAttribute = ownerProperty;
 		this.definition = definition;
 		this.fetchStrategy = fetchStrategy;
 
 		this.propertyPath = owner.getPropertyPath().append( ownerProperty.getName() );
 
 		owner.addFetch( this );
 	}
 
 	/**
 	 * Copy constructor.
 	 *
 	 * @param original The original fetch
 	 * @param copyContext Access to contextual needs for the copy operation
 	 */
 	protected AnyFetch(AnyFetch original, CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		super( original );
 		this.owner = fetchOwnerCopy;
 		this.fetchedAttribute = original.fetchedAttribute;
 		this.definition = original.definition;
 		this.fetchStrategy = original.fetchStrategy;
 		this.propertyPath = original.propertyPath;
 	}
 
 	@Override
 	public FetchOwner getOwner() {
 		return owner;
 	}
 
 	@Override
 	public AnyType getFetchedType() {
 		return (AnyType) fetchedAttribute.getType();
 	}
 
 	@Override
 	public boolean isNullable() {
 		return owner.isNullable( this );
 	}
 
 	@Override
 	public String[] toSqlSelectFragments(String alias) {
 		return owner.toSqlSelectFragments( this, alias );
 	}
 
 	@Override
 	public String getAdditionalJoinConditions() {
 		// only pertinent for HQL...
 		return null;
 	}
 
 	@Override
 	public FetchStrategy getFetchStrategy() {
 		return fetchStrategy;
 	}
 
 	@Override
 	public PropertyPath getPropertyPath() {
 		return propertyPath;
 	}
 
-	@Override
-	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-		throw new NotYetImplementedException();
-	}
-
-	@Override
-	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-		throw new NotYetImplementedException();
-	}
-
-	@Override
-	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException {
-		throw new NotYetImplementedException();
-	}
+//	@Override
+//	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+//		throw new NotYetImplementedException();
+//	}
+//
+//	@Override
+//	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+//		throw new NotYetImplementedException();
+//	}
+//
+//	@Override
+//	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException {
+//		throw new NotYetImplementedException();
+//	}
 
 	@Override
 	public AnyFetch makeCopy(CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		copyContext.getReturnGraphVisitationStrategy().startingAnyFetch( this );
 		final AnyFetch copy = new AnyFetch( this, copyContext, fetchOwnerCopy );
 		copyContext.getReturnGraphVisitationStrategy().startingAnyFetch( this );
 		return copy;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/BidirectionalEntityFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/BidirectionalEntityFetch.java
index 8e00dbda3e..359c8af7a7 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/BidirectionalEntityFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/BidirectionalEntityFetch.java
@@ -1,35 +1,48 @@
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
 package org.hibernate.loader.plan.spi;
 
 /**
  * Represents the circular side of a bi-directional entity fetch.  Wraps a reference to an EntityReference
  * as an EntityFetch.  We can use the special type as a trigger in AliasResolutionContext, etc to lookup information
  * based on the wrapped reference.
+ * <p/>
+ * This relies on reference lookups against the EntityReference instances, therefore this allows representation of the
+ * circularity but with a little protection against potential stack overflows.  This is unfortunately still a cyclic
+ * graph.  An alternative approach is to make the graph acyclic (DAG) would be to follow the process I adopted in the
+ * original HQL Antlr v3 work with regard to always applying an alias to the "persister reference", even where that
+ * meant creating a generated, unique identifier as the alias.  That allows other parts of the tree to refer to the
+ * "persister reference" by that alias without the need for potentially cyclic graphs (think ALIAS_REF in the current
+ * ORM parser).  Those aliases can then be mapped/catalogued against the "persister reference" for retrieval as needed.
  *
  * @author Steve Ebersole
  */
 public interface BidirectionalEntityFetch {
+	/**
+	 * Get the targeted EntityReference
+	 *
+	 * @return The targeted EntityReference
+	 */
 	public EntityReference getTargetEntityReference();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
index 9c9b0f2a52..88315dd79e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CollectionFetch.java
@@ -1,207 +1,196 @@
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
 package org.hibernate.loader.plan.spi;
 
-import java.io.Serializable;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-
 import org.jboss.logging.Logger;
 
 import org.hibernate.LockMode;
-import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.FetchStrategy;
-import org.hibernate.engine.spi.EntityKey;
-import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreLogging;
-import org.hibernate.loader.CollectionAliases;
-import org.hibernate.loader.plan.exec.process.internal.Helper;
-import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
-import org.hibernate.pretty.MessageHelper;
 import org.hibernate.type.CollectionType;
 
 /**
  * @author Steve Ebersole
  */
 public class CollectionFetch extends AbstractCollectionReference implements Fetch {
 	private static final Logger log = CoreLogging.logger( CollectionFetch.class );
 
 	private final FetchOwner fetchOwner;
 	private final AttributeDefinition fetchedAttribute;
 	private final FetchStrategy fetchStrategy;
 
 	public CollectionFetch(
 			SessionFactoryImplementor sessionFactory,
 			LockMode lockMode,
 			FetchOwner fetchOwner,
 			FetchStrategy fetchStrategy,
 			AttributeDefinition fetchedAttribute) {
 		super(
 				sessionFactory,
 				lockMode,
 				sessionFactory.getCollectionPersister( ( (CollectionType) fetchedAttribute.getType() ).getRole() ),
 				fetchOwner.getPropertyPath().append( fetchedAttribute.getName() ),
 				(EntityReference) fetchOwner
 		);
 		this.fetchOwner = fetchOwner;
 		this.fetchedAttribute = fetchedAttribute;
 		this.fetchStrategy = fetchStrategy;
 		fetchOwner.addFetch( this );
 	}
 
 	protected CollectionFetch(CollectionFetch original, CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		super( original, copyContext );
 		this.fetchOwner = fetchOwnerCopy;
 		this.fetchedAttribute = original.fetchedAttribute;
 		this.fetchStrategy = original.fetchStrategy;
 	}
 
 	@Override
 	public FetchOwner getOwner() {
 		return fetchOwner;
 	}
 
 	@Override
 	public CollectionType getFetchedType() {
 		return (CollectionType) fetchedAttribute.getType();
 	}
 
 	@Override
 	public boolean isNullable() {
 		return true;
 	}
 
 	@Override
 	public String getAdditionalJoinConditions() {
 		// only pertinent for HQL...
 		return null;
 	}
 
 	@Override
 	public String[] toSqlSelectFragments(String alias) {
 		return getOwner().toSqlSelectFragmentResolver().toSqlSelectFragments( alias, fetchedAttribute );
 	}
 
 	@Override
 	public FetchStrategy getFetchStrategy() {
 		return fetchStrategy;
 	}
 
-	@Override
-	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-		//To change body of implemented methods use File | Settings | File Templates.
-	}
-
-	@Override
-	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-		return null;
-	}
-
-	@Override
-	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException {
-		final Serializable collectionRowKey = (Serializable) getCollectionPersister().readKey(
-				resultSet,
-				context.getAliasResolutionContext().resolveAliases( this ).getCollectionColumnAliases().getSuffixedKeyAliases(),
-				context.getSession()
-		);
-
-		final PersistenceContext persistenceContext = context.getSession().getPersistenceContext();
-
-		if ( collectionRowKey == null ) {
-			// we did not find a collection element in the result set, so we
-			// ensure that a collection is created with the owner's identifier,
-			// since what we have is an empty collection
-			final EntityKey ownerEntityKey = findOwnerEntityKey( context );
-			if ( ownerEntityKey == null ) {
-				// should not happen
-				throw new IllegalStateException(
-						"Could not locate owner's EntityKey during attempt to read collection element fro JDBC row : " +
-								getPropertyPath().getFullPath()
-				);
-			}
-
-			if ( log.isDebugEnabled() ) {
-				log.debugf(
-						"Result set contains (possibly empty) collection: %s",
-						MessageHelper.collectionInfoString(
-								getCollectionPersister(),
-								ownerEntityKey,
-								context.getSession().getFactory()
-						)
-				);
-			}
-
-			persistenceContext.getLoadContexts()
-					.getCollectionLoadContext( resultSet )
-					.getLoadingCollection( getCollectionPersister(), ownerEntityKey );
-		}
-		else {
-			// we found a collection element in the result set
-			if ( log.isDebugEnabled() ) {
-				log.debugf(
-						"Found row of collection: %s",
-						MessageHelper.collectionInfoString(
-								getCollectionPersister(),
-								collectionRowKey,
-								context.getSession().getFactory()
-						)
-				);
-			}
-
-			PersistentCollection rowCollection = persistenceContext.getLoadContexts()
-					.getCollectionLoadContext( resultSet )
-					.getLoadingCollection( getCollectionPersister(), collectionRowKey );
-
-			final CollectionAliases descriptor = context.getAliasResolutionContext().resolveAliases( this ).getCollectionColumnAliases();
-
-			if ( rowCollection != null ) {
-				final Object element = rowCollection.readFrom( resultSet, getCollectionPersister(), descriptor, owner );
-
-				if ( getElementGraph() != null ) {
-					for ( Fetch fetch : getElementGraph().getFetches() ) {
-						fetch.read( resultSet, context, element );
-					}
-				}
-			}
-		}
-	}
-
-	private EntityKey findOwnerEntityKey(ResultSetProcessingContext context) {
-		return context.getProcessingState( findOwnerEntityReference( getOwner() ) ).getEntityKey();
-	}
-
-	private EntityReference findOwnerEntityReference(FetchOwner owner) {
-		return Helper.INSTANCE.findOwnerEntityReference( owner );
-	}
+//	@Override
+//	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+//		//To change body of implemented methods use File | Settings | File Templates.
+//	}
+//
+//	@Override
+//	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+//		return null;
+//	}
+//
+//	@Override
+//	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException {
+//		final Serializable collectionRowKey = (Serializable) getCollectionPersister().readKey(
+//				resultSet,
+//				context.getAliasResolutionContext().resolveAliases( this ).getCollectionColumnAliases().getSuffixedKeyAliases(),
+//				context.getSession()
+//		);
+//
+//		final PersistenceContext persistenceContext = context.getSession().getPersistenceContext();
+//
+//		if ( collectionRowKey == null ) {
+//			// we did not find a collection element in the result set, so we
+//			// ensure that a collection is created with the owner's identifier,
+//			// since what we have is an empty collection
+//			final EntityKey ownerEntityKey = findOwnerEntityKey( context );
+//			if ( ownerEntityKey == null ) {
+//				// should not happen
+//				throw new IllegalStateException(
+//						"Could not locate owner's EntityKey during attempt to read collection element fro JDBC row : " +
+//								getPropertyPath().getFullPath()
+//				);
+//			}
+//
+//			if ( log.isDebugEnabled() ) {
+//				log.debugf(
+//						"Result set contains (possibly empty) collection: %s",
+//						MessageHelper.collectionInfoString(
+//								getCollectionPersister(),
+//								ownerEntityKey,
+//								context.getSession().getFactory()
+//						)
+//				);
+//			}
+//
+//			persistenceContext.getLoadContexts()
+//					.getCollectionLoadContext( resultSet )
+//					.getLoadingCollection( getCollectionPersister(), ownerEntityKey );
+//		}
+//		else {
+//			// we found a collection element in the result set
+//			if ( log.isDebugEnabled() ) {
+//				log.debugf(
+//						"Found row of collection: %s",
+//						MessageHelper.collectionInfoString(
+//								getCollectionPersister(),
+//								collectionRowKey,
+//								context.getSession().getFactory()
+//						)
+//				);
+//			}
+//
+//			PersistentCollection rowCollection = persistenceContext.getLoadContexts()
+//					.getCollectionLoadContext( resultSet )
+//					.getLoadingCollection( getCollectionPersister(), collectionRowKey );
+//
+//			final CollectionAliases descriptor = context.getAliasResolutionContext().resolveAliases( this ).getCollectionColumnAliases();
+//
+//			if ( rowCollection != null ) {
+//				final Object element = rowCollection.readFrom( resultSet, getCollectionPersister(), descriptor, owner );
+//
+//				if ( getElementGraph() != null ) {
+//					for ( Fetch fetch : getElementGraph().getFetches() ) {
+//						fetch.read( resultSet, context, element );
+//					}
+//				}
+//			}
+//		}
+//	}
+//
+//	private EntityKey findOwnerEntityKey(ResultSetProcessingContext context) {
+//		return context.getProcessingState( findOwnerEntityReference( getOwner() ) ).getEntityKey();
+//	}
+//
+//	private EntityReference findOwnerEntityReference(FetchOwner owner) {
+//		return Helper.INSTANCE.findOwnerEntityReference( owner );
+//	}
 
 	@Override
 	public CollectionFetch makeCopy(CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		copyContext.getReturnGraphVisitationStrategy().startingCollectionFetch( this );
 		final CollectionFetch copy = new CollectionFetch( this, copyContext, fetchOwnerCopy );
 		copyContext.getReturnGraphVisitationStrategy().finishingCollectionFetch( this );
 		return copy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
index 18f16accc0..e802041b40 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/CompositeFetch.java
@@ -1,140 +1,140 @@
 /*
  * jDocBook, processing of DocBook sources
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
 package org.hibernate.loader.plan.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.plan.exec.process.internal.Helper;
 import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.type.CompositeType;
 
 /**
  * Represents a {@link Fetch} for a composite attribute as well as a
  * {@link FetchOwner} for any sub-attributes fetches.
  *
  * @author Steve Ebersole
  * @author Gail Badner
  */
 public class CompositeFetch extends AbstractSingularAttributeFetch {
 	private static final FetchStrategy FETCH_PLAN = new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
 
 	private final CompositeBasedSqlSelectFragmentResolver sqlSelectFragmentResolver;
 
 	/**
 	 * Constructs a {@link CompositeFetch} object.
 	 *
 	 * @param sessionFactory - the session factory.
 	 * @param owner - the fetch owner for this fetch.
 	 * @param fetchedAttribute - the owner's property referring to this fetch.
 	 */
 	public CompositeFetch(
 			SessionFactoryImplementor sessionFactory,
 			final FetchOwner owner,
 			final AttributeDefinition fetchedAttribute) {
 		super( sessionFactory, owner, fetchedAttribute, FETCH_PLAN );
 
 		this.sqlSelectFragmentResolver = new CompositeBasedSqlSelectFragmentResolver(
 				sessionFactory,
 				(CompositeType) fetchedAttribute.getType(),
 				new CompositeBasedSqlSelectFragmentResolver.BaseSqlSelectFragmentResolver() {
 					@Override
 					public String[] toSqlSelectFragments(String alias) {
 						return owner.toSqlSelectFragmentResolver().toSqlSelectFragments( alias, fetchedAttribute );
 					}
 				}
 		);
 	}
 
 	public CompositeFetch(CompositeFetch original, CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		super( original, copyContext, fetchOwnerCopy );
 		this.sqlSelectFragmentResolver = original.sqlSelectFragmentResolver;
 	}
 
 	@Override
 	public SqlSelectFragmentResolver toSqlSelectFragmentResolver() {
 		return sqlSelectFragmentResolver;
 	}
 
 	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return getOwner().retrieveFetchSourcePersister();
 	}
 
-	@Override
-	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-		// anything to do?
-	}
-
-	@Override
-	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-		// anything to do?
-		return null;
-	}
-
-	@Override
-	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException {
-		final EntityReference ownerEntityReference = Helper.INSTANCE.findOwnerEntityReference( this );
-		final ResultSetProcessingContext.EntityReferenceProcessingState entityReferenceProcessingState = context.getProcessingState(
-				ownerEntityReference
-		);
-		final EntityKey entityKey = entityReferenceProcessingState.getEntityKey();
-		final Object entity = context.resolveEntityKey( entityKey, Helper.INSTANCE.findOwnerEntityReference( (FetchOwner) ownerEntityReference ) );
-		for ( Fetch fetch : getFetches() ) {
-			fetch.read( resultSet, context, entity );
-		}
-	}
+//	@Override
+//	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+//		// anything to do?
+//	}
+//
+//	@Override
+//	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+//		// anything to do?
+//		return null;
+//	}
+//
+//	@Override
+//	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException {
+//		final EntityReference ownerEntityReference = Helper.INSTANCE.findOwnerEntityReference( this );
+//		final ResultSetProcessingContext.EntityReferenceProcessingState entityReferenceProcessingState = context.getProcessingState(
+//				ownerEntityReference
+//		);
+//		final EntityKey entityKey = entityReferenceProcessingState.getEntityKey();
+//		final Object entity = context.resolveEntityKey( entityKey, Helper.INSTANCE.findOwnerEntityReference( (FetchOwner) ownerEntityReference ) );
+//		for ( Fetch fetch : getFetches() ) {
+//			fetch.read( resultSet, context, entity );
+//		}
+//	}
 
 	@Override
 	public CompositeFetch makeCopy(CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		copyContext.getReturnGraphVisitationStrategy().startingCompositeFetch( this );
 		final CompositeFetch copy = new CompositeFetch( this, copyContext, fetchOwnerCopy );
 		copyContext.getReturnGraphVisitationStrategy().finishingCompositeFetch( this );
 		return copy;
 	}
 
 	@Override
 	public CollectionFetch buildCollectionFetch(
 			AssociationAttributeDefinition attributeDefinition,
 			FetchStrategy fetchStrategy,
 			LoadPlanBuildingContext loadPlanBuildingContext) {
 		return new CollectionFetch(
 				loadPlanBuildingContext.getSessionFactory(),
 				LockMode.NONE, // todo : for now
 				this,
 				fetchStrategy,
 				attributeDefinition
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
index 770f93ff9a..63a99892bb 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
@@ -1,299 +1,299 @@
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
 package org.hibernate.loader.plan.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.LockMode;
 import org.hibernate.WrongClassException;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
 import org.hibernate.type.EntityType;
 
 import static org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext.EntityReferenceProcessingState;
 
 /**
  * Represents a {@link Fetch} for an entity association attribute as well as a
  * {@link FetchOwner} of the entity association sub-attribute fetches.
 
  * @author Steve Ebersole
  */
 public class EntityFetch extends AbstractSingularAttributeFetch implements EntityReference, Fetch {
 	private final EntityPersister persister;
 	private final EntityPersisterBasedSqlSelectFragmentResolver sqlSelectFragmentResolver;
 
 	private IdentifierDescription identifierDescription;
 
 	// todo : remove these
 	private final LockMode lockMode;
 
 	/**
 	 * Constructs an {@link EntityFetch} object.
 	 *
 	 * @param sessionFactory - the session factory.
 	 * @param lockMode - the lock mode.
 	 * @param owner - the fetch owner for this fetch.
 	 * @param fetchedAttribute - the attribute being fetched
 	 * @param fetchStrategy - the fetch strategy for this fetch.
 	 */
 	public EntityFetch(
 			SessionFactoryImplementor sessionFactory,
 			LockMode lockMode,
 			FetchOwner owner,
 			AttributeDefinition fetchedAttribute,
 			FetchStrategy fetchStrategy) {
 		super( sessionFactory, owner, fetchedAttribute, fetchStrategy );
 
 		this.persister = sessionFactory.getEntityPersister( getFetchedType().getAssociatedEntityName() );
 		if ( persister == null ) {
 			throw new WalkingException(
 					String.format(
 							"Unable to locate EntityPersister [%s] for fetch [%s]",
 							getFetchedType().getAssociatedEntityName(),
 							fetchedAttribute.getName()
 					)
 			);
 		}
 		this.sqlSelectFragmentResolver = new EntityPersisterBasedSqlSelectFragmentResolver( (Queryable) persister );
 
 		this.lockMode = lockMode;
 	}
 
 	/**
 	 * Copy constructor.
 	 *
 	 * @param original The original fetch
 	 * @param copyContext Access to contextual needs for the copy operation
 	 */
 	protected EntityFetch(EntityFetch original, CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		super( original, copyContext, fetchOwnerCopy );
 		this.persister = original.persister;
 		this.sqlSelectFragmentResolver = original.sqlSelectFragmentResolver;
 
 		this.lockMode = original.lockMode;
 	}
 
 	@Override
 	public EntityType getFetchedType() {
 		return (EntityType) super.getFetchedType();
 	}
 
 	@Override
 	public String[] toSqlSelectFragments(String alias) {
 		return getOwner().toSqlSelectFragmentResolver().toSqlSelectFragments( alias, getFetchedAttribute() );
 	}
 
 	@Override
 	public EntityReference getEntityReference() {
 		return this;
 	}
 
 	@Override
 	public EntityPersister getEntityPersister() {
 		return persister;
 	}
 
 	@Override
 	public SqlSelectFragmentResolver toSqlSelectFragmentResolver() {
 		return sqlSelectFragmentResolver;
 	}
 
 	@Override
 	public IdentifierDescription getIdentifierDescription() {
 		return identifierDescription;
 	}
 
 	@Override
 	public LockMode getLockMode() {
 		return lockMode;
 	}
 
 	@Override
 	public EntityPersister retrieveFetchSourcePersister() {
 		return persister;
 	}
 
 	@Override
 	public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
 		this.identifierDescription = identifierDescription;
 	}
 
-	@Override
-	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-		identifierDescription.hydrate( resultSet, context );
-
-		for ( Fetch fetch : getFetches() ) {
-			fetch.hydrate( resultSet, context );
-		}
-	}
-
-	@Override
-	public EntityKey resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-		final ResultSetProcessingContext.EntityReferenceProcessingState entityReferenceProcessingState = context.getProcessingState(
-				this
-		);
-		EntityKey entityKey = entityReferenceProcessingState.getEntityKey();
-		if ( entityKey == null ) {
-			entityKey = identifierDescription.resolve( resultSet, context );
-			if ( entityKey == null ) {
-				// register the non-existence (though only for one-to-one associations)
-				if ( getFetchedType().isOneToOne() ) {
-					// first, find our owner's entity-key...
-					final EntityKey ownersEntityKey = context.getProcessingState( (EntityReference) getOwner() ).getEntityKey();
-					if ( ownersEntityKey != null ) {
-						context.getSession().getPersistenceContext()
-								.addNullProperty( ownersEntityKey, getFetchedType().getPropertyName() );
-					}
-				}
-			}
-
-			entityReferenceProcessingState.registerEntityKey( entityKey );
-
-			for ( Fetch fetch : getFetches() ) {
-				fetch.resolve( resultSet, context );
-			}
-		}
-
-		return entityKey;
-	}
-
-	@Override
-	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException {
-		final EntityReferenceProcessingState entityReferenceProcessingState = context.getProcessingState( this );
-		final EntityKey entityKey = entityReferenceProcessingState.getEntityKey();
-		if ( entityKey == null ) {
-			return;
-		}
-		final Object entity = context.resolveEntityKey( entityKey, this );
-		for ( Fetch fetch : getFetches() ) {
-			fetch.read( resultSet, context, entity );
-		}
-	}
-
-	/**
-	 * Resolve any fetches required to resolve the identifier as well
-	 * as the entity key for this fetch..
-	 *
-	 * @param resultSet - the result set.
-	 * @param context - the result set processing context.
-	 * @return the entity key for this fetch.
-	 *
-	 * @throws SQLException
-	 */
-	public EntityKey resolveInIdentifier(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-		// todo : may not need to do this if entitykey is already part of the resolution context
-
-		final EntityKey entityKey = resolve( resultSet, context );
-
-		final Object existing = context.getSession().getEntityUsingInterceptor( entityKey );
-
-		if ( existing != null ) {
-			if ( !persister.isInstance( existing ) ) {
-				throw new WrongClassException(
-						"loaded object was of wrong class " + existing.getClass(),
-						entityKey.getIdentifier(),
-						persister.getEntityName()
-				);
-			}
-
-			if ( getLockMode() != null && getLockMode() != LockMode.NONE ) {
-				final boolean isVersionCheckNeeded = persister.isVersioned()
-						&& context.getSession().getPersistenceContext().getEntry( existing ).getLockMode().lessThan( getLockMode() );
-
-				// we don't need to worry about existing version being uninitialized because this block isn't called
-				// by a re-entrant load (re-entrant loads _always_ have lock mode NONE)
-				if ( isVersionCheckNeeded ) {
-					//we only check the version when _upgrading_ lock modes
-					context.checkVersion(
-							resultSet,
-							persister,
-							context.getAliasResolutionContext().resolveAliases( this ).getColumnAliases(),
-							entityKey,
-							existing
-					);
-					//we need to upgrade the lock mode to the mode requested
-					context.getSession().getPersistenceContext().getEntry( existing ).setLockMode( getLockMode() );
-				}
-			}
-		}
-		else {
-			final String concreteEntityTypeName = context.getConcreteEntityTypeName(
-					resultSet,
-					persister,
-					context.getAliasResolutionContext().resolveAliases( this ).getColumnAliases(),
-					entityKey
-			);
-
-			final Object entityInstance = context.getSession().instantiate(
-					concreteEntityTypeName,
-					entityKey.getIdentifier()
-			);
-
-			//need to hydrate it.
-
-			// grab its state from the ResultSet and keep it in the Session
-			// (but don't yet initialize the object itself)
-			// note that we acquire LockMode.READ even if it was not requested
-			LockMode acquiredLockMode = getLockMode() == LockMode.NONE ? LockMode.READ : getLockMode();
-
-			context.loadFromResultSet(
-					resultSet,
-					entityInstance,
-					concreteEntityTypeName,
-					entityKey,
-					context.getAliasResolutionContext().resolveAliases( this ).getColumnAliases(),
-					acquiredLockMode,
-					persister,
-					getFetchStrategy(),
-					true,
-					getFetchedType()
-			);
-
-			// materialize associations (and initialize the object) later
-			context.registerHydratedEntity( this, entityKey, entityInstance );
-		}
-
-		return entityKey;
-	}
+//	@Override
+//	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+//		identifierDescription.hydrate( resultSet, context );
+//
+//		for ( Fetch fetch : getFetches() ) {
+//			fetch.hydrate( resultSet, context );
+//		}
+//	}
+//
+//	@Override
+//	public EntityKey resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+//		final ResultSetProcessingContext.EntityReferenceProcessingState entityReferenceProcessingState = context.getProcessingState(
+//				this
+//		);
+//		EntityKey entityKey = entityReferenceProcessingState.getEntityKey();
+//		if ( entityKey == null ) {
+//			entityKey = identifierDescription.resolve( resultSet, context );
+//			if ( entityKey == null ) {
+//				// register the non-existence (though only for one-to-one associations)
+//				if ( getFetchedType().isOneToOne() ) {
+//					// first, find our owner's entity-key...
+//					final EntityKey ownersEntityKey = context.getProcessingState( (EntityReference) getOwner() ).getEntityKey();
+//					if ( ownersEntityKey != null ) {
+//						context.getSession().getPersistenceContext()
+//								.addNullProperty( ownersEntityKey, getFetchedType().getPropertyName() );
+//					}
+//				}
+//			}
+//
+//			entityReferenceProcessingState.registerEntityKey( entityKey );
+//
+//			for ( Fetch fetch : getFetches() ) {
+//				fetch.resolve( resultSet, context );
+//			}
+//		}
+//
+//		return entityKey;
+//	}
+//
+//	@Override
+//	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException {
+//		final EntityReferenceProcessingState entityReferenceProcessingState = context.getProcessingState( this );
+//		final EntityKey entityKey = entityReferenceProcessingState.getEntityKey();
+//		if ( entityKey == null ) {
+//			return;
+//		}
+//		final Object entity = context.resolveEntityKey( entityKey, this );
+//		for ( Fetch fetch : getFetches() ) {
+//			fetch.read( resultSet, context, entity );
+//		}
+//	}
+
+//	/**
+//	 * Resolve any fetches required to resolve the identifier as well
+//	 * as the entity key for this fetch..
+//	 *
+//	 * @param resultSet - the result set.
+//	 * @param context - the result set processing context.
+//	 * @return the entity key for this fetch.
+//	 *
+//	 * @throws SQLException
+//	 */
+//	public EntityKey resolveInIdentifier(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
+//		// todo : may not need to do this if entitykey is already part of the resolution context
+//
+//		final EntityKey entityKey = resolve( resultSet, context );
+//
+//		final Object existing = context.getSession().getEntityUsingInterceptor( entityKey );
+//
+//		if ( existing != null ) {
+//			if ( !persister.isInstance( existing ) ) {
+//				throw new WrongClassException(
+//						"loaded object was of wrong class " + existing.getClass(),
+//						entityKey.getIdentifier(),
+//						persister.getEntityName()
+//				);
+//			}
+//
+//			if ( getLockMode() != null && getLockMode() != LockMode.NONE ) {
+//				final boolean isVersionCheckNeeded = persister.isVersioned()
+//						&& context.getSession().getPersistenceContext().getEntry( existing ).getLockMode().lessThan( getLockMode() );
+//
+//				// we don't need to worry about existing version being uninitialized because this block isn't called
+//				// by a re-entrant load (re-entrant loads _always_ have lock mode NONE)
+//				if ( isVersionCheckNeeded ) {
+//					//we only check the version when _upgrading_ lock modes
+//					context.checkVersion(
+//							resultSet,
+//							persister,
+//							context.getAliasResolutionContext().resolveAliases( this ).getColumnAliases(),
+//							entityKey,
+//							existing
+//					);
+//					//we need to upgrade the lock mode to the mode requested
+//					context.getSession().getPersistenceContext().getEntry( existing ).setLockMode( getLockMode() );
+//				}
+//			}
+//		}
+//		else {
+//			final String concreteEntityTypeName = context.getConcreteEntityTypeName(
+//					resultSet,
+//					persister,
+//					context.getAliasResolutionContext().resolveAliases( this ).getColumnAliases(),
+//					entityKey
+//			);
+//
+//			final Object entityInstance = context.getSession().instantiate(
+//					concreteEntityTypeName,
+//					entityKey.getIdentifier()
+//			);
+//
+//			//need to hydrate it.
+//
+//			// grab its state from the ResultSet and keep it in the Session
+//			// (but don't yet initialize the object itself)
+//			// note that we acquire LockMode.READ even if it was not requested
+//			LockMode acquiredLockMode = getLockMode() == LockMode.NONE ? LockMode.READ : getLockMode();
+//
+//			context.loadFromResultSet(
+//					resultSet,
+//					entityInstance,
+//					concreteEntityTypeName,
+//					entityKey,
+//					context.getAliasResolutionContext().resolveAliases( this ).getColumnAliases(),
+//					acquiredLockMode,
+//					persister,
+//					getFetchStrategy(),
+//					true,
+//					getFetchedType()
+//			);
+//
+//			// materialize associations (and initialize the object) later
+//			context.registerHydratedEntity( this, entityKey, entityInstance );
+//		}
+//
+//		return entityKey;
+//	}
 
 	@Override
 	public String toString() {
 		return "EntityFetch(" + getPropertyPath().getFullPath() + " -> " + persister.getEntityName() + ")";
 	}
 
 	@Override
 	public EntityFetch makeCopy(CopyContext copyContext, FetchOwner fetchOwnerCopy) {
 		copyContext.getReturnGraphVisitationStrategy().startingEntityFetch( this );
 		final EntityFetch copy = new EntityFetch( this, copyContext, fetchOwnerCopy );
 		copyContext.getReturnGraphVisitationStrategy().finishingEntityFetch( this );
 		return copy;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
index 6c2c0f46e8..8c4f68cbd9 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference.java
@@ -1,52 +1,53 @@
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
 package org.hibernate.loader.plan.spi;
 
 import org.hibernate.LockMode;
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
+import org.hibernate.loader.plan.spi.build.IdentifierDescriptionInjectable;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Represents a reference to an entity either as a return or as a fetch
  *
  * @author Steve Ebersole
  */
 public interface EntityReference
 		extends IdentifierDescriptionInjectable, ResultSetProcessingContext.EntityKeyResolutionContext {
 	/**
 	 * Retrieve the lock mode associated with this return.
 	 *
 	 * @return The lock mode.
 	 */
 	public LockMode getLockMode();
 
 	/**
 	 * Retrieves the EntityPersister describing the entity associated with this Return.
 	 *
 	 * @return The EntityPersister.
 	 */
 	public EntityPersister getEntityPersister();
 
 	public IdentifierDescription getIdentifierDescription();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference2.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference2.java
new file mode 100644
index 0000000000..fd2084c6aa
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReference2.java
@@ -0,0 +1,47 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan.spi;
+
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * Represents a reference to an entity either as a return or as a fetch
+ *
+ * @author Steve Ebersole
+ */
+public interface EntityReference2 {
+	/**
+	 * Retrieves the EntityPersister describing the entity associated with this Return.
+	 *
+	 * @return The EntityPersister.
+	 */
+	public EntityPersister getEntityPersister();
+
+	/**
+	 * Get the description of this entity's identifier.
+	 *
+	 * @return The identifier description.
+	 */
+	public IdentifierDescription getIdentifierDescription();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn2.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn2.java
new file mode 100644
index 0000000000..abafc44d85
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityReturn2.java
@@ -0,0 +1,31 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan.spi;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface EntityReturn2 extends EntityReference2 {
+	public String getQuerySpaceUid();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
index 240c3c505c..5571da6be4 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/Fetch.java
@@ -1,89 +1,89 @@
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
 package org.hibernate.loader.plan.spi;
 
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
 import org.hibernate.type.Type;
 
 /**
  * Contract for associations that are being fetched.
  * <p/>
  * NOTE : can represent components/embeddables
  *
  * @author Steve Ebersole
  */
 public interface Fetch extends CopyableFetch {
 	/**
 	 * Obtain the owner of this fetch.
 	 *
 	 * @return The fetch owner.
 	 */
 	public FetchOwner getOwner();
 
 	/**
 	 * Get the property path to this fetch
 	 *
 	 * @return The property path
 	 */
 	public PropertyPath getPropertyPath();
 
 	public Type getFetchedType();
 
 	/**
 	 * Gets the fetch strategy for this fetch.
 	 *
 	 * @return the fetch strategy for this fetch.
 	 */
 	public FetchStrategy getFetchStrategy();
 
 	/**
 	 * Is this fetch nullable?
 	 *
 	 * @return true, if this fetch is nullable; false, otherwise.
 	 */
 	public boolean isNullable();
 
 	public String getAdditionalJoinConditions();
 
 	/**
 	 * Generates the SQL select fragments for this fetch.  A select fragment is the column and formula references.
 	 *
 	 * @return the select fragments
 	 */
 	public String[] toSqlSelectFragments(String alias);
 
-	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
-
-	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
-
-	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException;
+//	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
+//
+//	public Object resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
+//
+//	public void read(ResultSet resultSet, ResultSetProcessingContext context, Object owner) throws SQLException;
 
 	@Override
 	public Fetch makeCopy(CopyContext copyContext, FetchOwner fetchOwnerCopy);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/IdentifierDescription.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/IdentifierDescription.java
index 2b676bcaeb..26e7ed5e4c 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/IdentifierDescription.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/IdentifierDescription.java
@@ -1,44 +1,37 @@
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
 package org.hibernate.loader.plan.spi;
 
-import java.sql.ResultSet;
-import java.sql.SQLException;
-
-import org.hibernate.engine.spi.EntityKey;
-import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
-import org.hibernate.persister.spi.HydratedCompoundValueHandler;
-
 /**
  * @author Steve Ebersole
  */
 public interface IdentifierDescription {
+	/**
+	 * Obtain fetches that are specific to the identifier.  These will only be either of type EntityFetch
+	 * (many-key-to-one) or CompositeFetch (composite ids, possibly with nested CompositeFetches and EntityFetches).
+	 *
+	 * @return This identifier's fetches.
+	 */
 	public Fetch[] getFetches();
-
-	public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
-
-	public EntityKey resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException;
-
-	HydratedCompoundValueHandler getHydratedStateHandler(Fetch fetch);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/KeyManyToOneBidirectionalEntityFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/KeyManyToOneBidirectionalEntityFetch.java
index 73bbf9e361..71b59770a5 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/KeyManyToOneBidirectionalEntityFetch.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/KeyManyToOneBidirectionalEntityFetch.java
@@ -1,62 +1,65 @@
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
 package org.hibernate.loader.plan.spi;
 
 import org.hibernate.LockMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 
 /**
- * We can use the special type as a trigger in AliasResolutionContext, etc to lookup information based on
- * the wrapped reference.  E.g.
+ * Represents a key-many-to-one fetch that is bi-directionally join fetched.
+ * <p/>
+ * For example, consider an Order entity whose primary key is partially made up of the Customer entity to which
+ * it is associated.  When we join fetch Customer -> Order(s) and then Order -> Customer we have a bi-directional
+ * fetch.  This class would be used to represent the Order -> Customer part of that link.
  *
  * @author Steve Ebersole
  */
 public class KeyManyToOneBidirectionalEntityFetch extends EntityFetch implements BidirectionalEntityFetch {
 	private final EntityReference targetEntityReference;
 
 	public KeyManyToOneBidirectionalEntityFetch(
 			SessionFactoryImplementor sessionFactory,
 			LockMode lockMode,
 			FetchOwner owner,
 			AttributeDefinition fetchedAttribute,
 			EntityReference targetEntityReference,
 			FetchStrategy fetchStrategy) {
 		super( sessionFactory, lockMode, owner, fetchedAttribute, fetchStrategy );
 		this.targetEntityReference = targetEntityReference;
 	}
 
 	public KeyManyToOneBidirectionalEntityFetch(
 			KeyManyToOneBidirectionalEntityFetch original,
 			CopyContext copyContext,
 			FetchOwner fetchOwnerCopy) {
 		super( original, copyContext, fetchOwnerCopy );
 		this.targetEntityReference = original.targetEntityReference;
 	}
 
 	public EntityReference getTargetEntityReference() {
 		return targetEntityReference;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java
index fe5f3c89fe..4a7dc88d02 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/LoadPlan.java
@@ -1,100 +1,128 @@
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
 package org.hibernate.loader.plan.spi;
 
 import java.util.List;
 
 /**
  * Describes a plan for performing a load of results.
  *
  * Generally speaking there are 3 forms of load plans:<ul>
  *     <li>
- *         An entity load plan for handling get/load handling.  This form will typically have a single
- *         return (of type {@link EntityReturn}) defined by {@link #getReturns()}, possibly defining fetches.
+ *         {@link org.hibernate.loader.plan.spi.LoadPlan.Disposition#ENTITY_LOADER} - An entity load plan for
+ *         handling get/load handling.  This form will typically have a single return (of type {@link EntityReturn})
+ *         defined by {@link #getReturns()}, possibly defining fetches.
  *     </li>
  *     <li>
- *         A collection initializer, used to load the contents of a collection.  This form will typically have a
- *         single return (of type {@link CollectionReturn} defined by {@link #getReturns()}, possibly defining fetches
+ *         {@link org.hibernate.loader.plan.spi.LoadPlan.Disposition#COLLECTION_INITIALIZER} - A collection initializer,
+ *         used to load the contents of a collection.  This form will typically have a single return (of
+ *         type {@link CollectionReturn}) defined by {@link #getReturns()}, possibly defining fetches
  *     </li>
  *     <li>
- *         A query load plan which can contain multiple returns of mixed type (though implementing {@link Return}).
- *         Again, may possibly define fetches.
+ *         {@link org.hibernate.loader.plan.spi.LoadPlan.Disposition#MIXED} - A query load plan which can contain
+ *         multiple returns of mixed type (though all implementing {@link Return}).  Again, may possibly define fetches.
  *     </li>
  * </ul>
+ * <p/>
+ * todo : would also like to see "call back" style access for handling "subsequent actions" such as...<ul>
+ *     <li>follow-on locking</li>
+ *     <li>join fetch conversions to subselect fetches</li>
+ * </ul>
  *
  * @author Steve Ebersole
  */
 public interface LoadPlan {
-	public List<? extends Return> getReturns();
 
+	/**
+	 * What is the disposition of this LoadPlan, in terms of its returns.
+	 *
+	 * @return The LoadPlan's disposition
+	 */
 	public Disposition getDisposition();
 
 	/**
+	 * Get the returns indicated by this LoadPlan.<ul>
+	 *     <li>
+	 *         A {@link Disposition#ENTITY_LOADER} LoadPlan would have just a single Return of type {@link EntityReturn}.
+	 *     </li>
+	 *     <li>
+	 *         A {@link Disposition#COLLECTION_INITIALIZER} LoadPlan would have just a single Return of type
+	 *         {@link CollectionReturn}.
+	 *     </li>
+	 *     <li>
+	 *         A {@link Disposition#MIXED} LoadPlan would contain a mix of {@link EntityReturn} and
+	 *         {@link ScalarReturn} elements, but no {@link CollectionReturn}.
+	 *     </li>
+	 * </ul>
+	 *
+	 * @return The Returns for this LoadPlan.
+	 *
+	 * @see Disposition
+	 */
+	public List<? extends Return> getReturns();
+
+	/**
 	 * Does this load plan indicate that lazy attributes are to be force fetched?
 	 * <p/>
 	 * Here we are talking about laziness in regards to the legacy bytecode enhancement which adds support for
 	 * partial selects of an entity's state (e.g., skip loading a lob initially, wait until/if it is needed)
 	 * <p/>
 	 * This one would effect the SQL that needs to get generated as well as how the result set would be read.
 	 * Therefore we make this part of the LoadPlan contract.
 	 * <p/>
 	 * NOTE that currently this is only relevant for HQL loaders when the HQL has specified the {@code FETCH ALL PROPERTIES}
 	 * key-phrase.  In all other cases, this returns false.
 
 	 * @return Whether or not to
 	 */
 	public boolean areLazyAttributesForceFetched();
 
 	/**
 	 * Convenient form of checking {@link #getReturns()} for scalar root returns.
 	 *
 	 * @return {@code true} if {@link #getReturns()} contained any scalar returns; {@code false} otherwise.
 	 */
 	public boolean hasAnyScalarReturns();
 
 	/**
 	 * Enumerated possibilities for describing the disposition of this LoadPlan.
 	 */
 	public static enum Disposition {
 		/**
 		 * This is an "entity loader" load plan, which describes a plan for loading one or more entity instances of
 		 * the same entity type.  There is a single return, which will be of type {@link EntityReturn}
 		 */
 		ENTITY_LOADER,
 		/**
 		 * This is a "collection initializer" load plan, which describes a plan for loading one or more entity instances of
 		 * the same collection type.  There is a single return, which will be of type {@link CollectionReturn}
 		 */
 		COLLECTION_INITIALIZER,
 		/**
 		 * We have a mixed load plan, which will have one or more returns of {@link EntityReturn} and {@link ScalarReturn}
 		 * (NOT {@link CollectionReturn}).
 		 */
 		MIXED
 	}
-
-	// todo : would also like to see "call back" style access for handling "subsequent actions" such as:
-	// 		1) follow-on locking
-	//		2) join fetch conversions to subselect fetches
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java
index 110da5e802..37e3854302 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/AbstractLoadPlanBuilderStrategy.java
@@ -1,903 +1,923 @@
 /*
- * jDocBook, processing of DocBook sources
+ * Hibernate, Relational Persistence for Idiomatic Java
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
 package org.hibernate.loader.plan.spi.build;
 
-import java.io.Serializable;
-import java.sql.ResultSet;
-import java.sql.SQLException;
 import java.util.ArrayDeque;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 import org.jboss.logging.MDC;
 
 import org.hibernate.HibernateException;
 import org.hibernate.LockMode;
-import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
-import org.hibernate.engine.spi.EntityKey;
+import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.loader.plan.spi.AbstractFetchOwner;
 import org.hibernate.loader.plan.spi.AnyFetch;
+import org.hibernate.loader.plan.spi.BidirectionalEntityFetch;
 import org.hibernate.loader.plan.spi.CollectionFetch;
 import org.hibernate.loader.plan.spi.CollectionReference;
 import org.hibernate.loader.plan.spi.CollectionReturn;
 import org.hibernate.loader.plan.spi.CompositeElementGraph;
 import org.hibernate.loader.plan.spi.CompositeFetch;
+import org.hibernate.loader.plan.spi.CopyContext;
 import org.hibernate.loader.plan.spi.EntityFetch;
 import org.hibernate.loader.plan.spi.EntityPersisterBasedSqlSelectFragmentResolver;
 import org.hibernate.loader.plan.spi.EntityReference;
 import org.hibernate.loader.plan.spi.EntityReturn;
 import org.hibernate.loader.plan.spi.Fetch;
 import org.hibernate.loader.plan.spi.FetchOwner;
 import org.hibernate.loader.plan.spi.IdentifierDescription;
 import org.hibernate.loader.plan.spi.KeyManyToOneBidirectionalEntityFetch;
 import org.hibernate.loader.plan.spi.Return;
 import org.hibernate.loader.plan.spi.SqlSelectFragmentResolver;
-import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessingContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.HydratedCompoundValueHandler;
-import org.hibernate.persister.walking.internal.FetchStrategyHelper;
 import org.hibernate.persister.walking.spi.AnyMappingDefinition;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * @author Steve Ebersole
  */
 public abstract class AbstractLoadPlanBuilderStrategy implements LoadPlanBuilderStrategy, LoadPlanBuildingContext {
 	private static final Logger log = Logger.getLogger( AbstractLoadPlanBuilderStrategy.class );
 	private static final String MDC_KEY = "hibernateLoadPlanWalkPath";
 
 	private final SessionFactoryImplementor sessionFactory;
 
 	private ArrayDeque<FetchOwner> fetchOwnerStack = new ArrayDeque<FetchOwner>();
 	private ArrayDeque<CollectionReference> collectionReferenceStack = new ArrayDeque<CollectionReference>();
 
 	protected AbstractLoadPlanBuilderStrategy(SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 	}
 
 	public SessionFactoryImplementor sessionFactory() {
 		return sessionFactory;
 	}
 
 	protected FetchOwner currentFetchOwner() {
 		return fetchOwnerStack.peekFirst();
 	}
 
 	@Override
 	public void start() {
 		if ( ! fetchOwnerStack.isEmpty() ) {
 			throw new WalkingException(
 					"Fetch owner stack was not empty on start; " +
 							"be sure to not use LoadPlanBuilderStrategy instances concurrently"
 			);
 		}
 		if ( ! collectionReferenceStack.isEmpty() ) {
 			throw new WalkingException(
 					"Collection reference stack was not empty on start; " +
 							"be sure to not use LoadPlanBuilderStrategy instances concurrently"
 			);
 		}
 		MDC.put( MDC_KEY, new MDCStack() );
 	}
 
 	@Override
 	public void finish() {
 		MDC.remove( MDC_KEY );
 		fetchOwnerStack.clear();
 		collectionReferenceStack.clear();
 	}
 
 	@Override
 	public void startingEntity(EntityDefinition entityDefinition) {
 		log.tracef(
 				"%s Starting entity : %s",
 				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
 				entityDefinition.getEntityPersister().getEntityName()
 		);
 
 		if ( fetchOwnerStack.isEmpty() ) {
 			// this is a root...
 			if ( ! supportsRootEntityReturns() ) {
 				throw new HibernateException( "This strategy does not support root entity returns" );
 			}
 			final EntityReturn entityReturn = buildRootEntityReturn( entityDefinition );
 			addRootReturn( entityReturn );
 			pushToStack( entityReturn );
 		}
 		// otherwise this call should represent a fetch which should have been handled in #startingAttribute
 	}
 
 	protected boolean supportsRootEntityReturns() {
 		return false;
 	}
 
 	protected abstract void addRootReturn(Return rootReturn);
 
 	@Override
 	public void finishingEntity(EntityDefinition entityDefinition) {
 		// pop the current fetch owner, and make sure what we just popped represents this entity
 		final FetchOwner poppedFetchOwner = popFromStack();
 
 		if ( ! EntityReference.class.isInstance( poppedFetchOwner ) ) {
 			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
 		}
 
 		final EntityReference entityReference = (EntityReference) poppedFetchOwner;
 		// NOTE : this is not the most exhaustive of checks because of hierarchical associations (employee/manager)
 		if ( ! entityReference.getEntityPersister().equals( entityDefinition.getEntityPersister() ) ) {
 			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
 		}
 
 		log.tracef(
 				"%s Finished entity : %s",
 				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
 				entityDefinition.getEntityPersister().getEntityName()
 		);
 	}
 
 	@Override
 	public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 		log.tracef(
 				"%s Starting entity identifier : %s",
 				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
 				entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 		);
 
 		final EntityReference entityReference = (EntityReference) currentFetchOwner();
 
 		// perform some stack validation
 		if ( ! entityReference.getEntityPersister().equals( entityIdentifierDefinition.getEntityDefinition().getEntityPersister() ) ) {
 			throw new WalkingException(
 					String.format(
 							"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
 							entityReference.getEntityPersister().getEntityName(),
 							entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 					)
 			);
 		}
 
 		final FetchOwner identifierAttributeCollector;
 		if ( entityIdentifierDefinition.isEncapsulated() ) {
 			identifierAttributeCollector = new EncapsulatedIdentifierAttributeCollector( sessionFactory, entityReference );
 		}
 		else {
 			identifierAttributeCollector = new NonEncapsulatedIdentifierAttributeCollector( sessionFactory, entityReference );
 		}
 		pushToStack( identifierAttributeCollector );
 	}
 
 	@Override
 	public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 		// perform some stack validation on exit, first on the current stack element we want to pop
 		{
 			final FetchOwner poppedFetchOwner = popFromStack();
 
 			if ( ! AbstractIdentifierAttributeCollector.class.isInstance( poppedFetchOwner ) ) {
 				throw new WalkingException( "Unexpected state in FetchOwner stack" );
 			}
 
 			final EntityReference entityReference = (EntityReference) poppedFetchOwner;
 			if ( ! entityReference.getEntityPersister().equals( entityIdentifierDefinition.getEntityDefinition().getEntityPersister() ) ) {
 				throw new WalkingException(
 						String.format(
 								"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
 								entityReference.getEntityPersister().getEntityName(),
 								entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 						)
 				);
 			}
 		}
 
 		// and then on the element before it
 		{
 			final FetchOwner currentFetchOwner = currentFetchOwner();
 			if ( ! EntityReference.class.isInstance( currentFetchOwner ) ) {
 				throw new WalkingException( "Unexpected state in FetchOwner stack" );
 			}
 			final EntityReference entityReference = (EntityReference) currentFetchOwner;
 			if ( ! entityReference.getEntityPersister().equals( entityIdentifierDefinition.getEntityDefinition().getEntityPersister() ) ) {
 				throw new WalkingException(
 						String.format(
 								"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
 								entityReference.getEntityPersister().getEntityName(),
 								entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 						)
 				);
 			}
 		}
 
 		log.tracef(
 				"%s Finished entity identifier : %s",
 				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
 				entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
 		);
 	}
 
 	@Override
 	public void startingCollection(CollectionDefinition collectionDefinition) {
 		log.tracef(
 				"%s Starting collection : %s",
 				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
 				collectionDefinition.getCollectionPersister().getRole()
 		);
 
 		if ( fetchOwnerStack.isEmpty() ) {
 			// this is a root...
 			if ( ! supportsRootCollectionReturns() ) {
 				throw new HibernateException( "This strategy does not support root collection returns" );
 			}
 			final CollectionReturn collectionReturn = buildRootCollectionReturn( collectionDefinition );
 			addRootReturn( collectionReturn );
 			pushToCollectionStack( collectionReturn );
 		}
 	}
 
 	protected boolean supportsRootCollectionReturns() {
 		return false;
 	}
 
 	@Override
 	public void startingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
 		final Type indexType = collectionIndexDefinition.getType();
 		if ( indexType.isAssociationType() || indexType.isComponentType() ) {
 			final CollectionReference collectionReference = collectionReferenceStack.peekFirst();
 			final FetchOwner indexGraph = collectionReference.getIndexGraph();
 			if ( indexGraph == null ) {
 				throw new WalkingException( "Collection reference did not return index handler" );
 			}
 			pushToStack( indexGraph );
 		}
 	}
 
 	@Override
 	public void finishingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
 		// nothing to do here
 		// 	- the element graph pushed while starting would be popped in finishing/Entity/finishingComposite
 	}
 
 	@Override
 	public void startingCollectionElements(CollectionElementDefinition elementDefinition) {
 		if ( elementDefinition.getType().isAssociationType() || elementDefinition.getType().isComponentType() ) {
 			final CollectionReference collectionReference = collectionReferenceStack.peekFirst();
 			final FetchOwner elementGraph = collectionReference.getElementGraph();
 			if ( elementGraph == null ) {
 				throw new WalkingException( "Collection reference did not return element handler" );
 			}
 			pushToStack( elementGraph );
 		}
 	}
 
 	@Override
 	public void finishingCollectionElements(CollectionElementDefinition elementDefinition) {
 		// nothing to do here
 		// 	- the element graph pushed while starting would be popped in finishing/Entity/finishingComposite
 	}
 
 	@Override
 	public void startingCompositeCollectionElement(CompositeCollectionElementDefinition compositeElementDefinition) {
 		log.tracef(
 				"%s Starting composite collection element for (%s)",
 				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
 				compositeElementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 		);
 	}
 
 	@Override
 	public void finishingCompositeCollectionElement(CompositeCollectionElementDefinition compositeElementDefinition) {
 		// pop the current fetch owner, and make sure what we just popped represents this composition
 		final FetchOwner poppedFetchOwner = popFromStack();
 
 		if ( ! CompositeElementGraph.class.isInstance( poppedFetchOwner ) ) {
 			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
 		}
 
 		// NOTE : not much else we can really check here atm since on the walking spi side we do not have path
 
 		log.tracef(
 				"%s Finished composite element for  : %s",
 				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
 				compositeElementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
 		);
 	}
 
 	@Override
 	public void finishingCollection(CollectionDefinition collectionDefinition) {
 		// pop the current fetch owner, and make sure what we just popped represents this collection
 		final CollectionReference collectionReference = popFromCollectionStack();
 		if ( ! collectionReference.getCollectionPersister().equals( collectionDefinition.getCollectionPersister() ) ) {
 			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
 		}
 
 		log.tracef(
 				"%s Finished collection : %s",
 				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
 				collectionDefinition.getCollectionPersister().getRole()
 		);
 	}
 
 	@Override
 	public void startingComposite(CompositionDefinition compositionDefinition) {
 		log.tracef(
 				"%s Starting composition : %s",
 				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
 				compositionDefinition.getName()
 		);
 
 		if ( fetchOwnerStack.isEmpty() ) {
 			throw new HibernateException( "A component cannot be the root of a walk nor a graph" );
 		}
 	}
 
 	@Override
 	public void finishingComposite(CompositionDefinition compositionDefinition) {
 		// pop the current fetch owner, and make sure what we just popped represents this composition
 		final FetchOwner poppedFetchOwner = popFromStack();
 
 		if ( ! CompositeFetch.class.isInstance( poppedFetchOwner ) ) {
 			throw new WalkingException( "Mismatched FetchOwner from stack on pop" );
 		}
 
 		// NOTE : not much else we can really check here atm since on the walking spi side we do not have path
 
 		log.tracef(
 				"%s Finished composition : %s",
 				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
 				compositionDefinition.getName()
 		);
 	}
 
 	@Override
 	public boolean startingAttribute(AttributeDefinition attributeDefinition) {
 		log.tracef(
 				"%s Starting attribute %s",
 				StringHelper.repeat( ">>", fetchOwnerStack.size() ),
 				attributeDefinition
 		);
 
 		final Type attributeType = attributeDefinition.getType();
 
 		final boolean isComponentType = attributeType.isComponentType();
 		final boolean isAssociationType = attributeType.isAssociationType();
 		final boolean isBasicType = ! ( isComponentType || isAssociationType );
 
 		if ( isBasicType ) {
 			return true;
 		}
 		else if ( isAssociationType ) {
 			return handleAssociationAttribute( (AssociationAttributeDefinition) attributeDefinition );
 		}
 		else {
 			return handleCompositeAttribute( (CompositionDefinition) attributeDefinition );
 		}
 	}
 
 	@Override
 	public void finishingAttribute(AttributeDefinition attributeDefinition) {
 		log.tracef(
 				"%s Finishing up attribute : %s",
 				StringHelper.repeat( "<<", fetchOwnerStack.size() ),
 				attributeDefinition
 		);
 	}
 
+	private Map<AssociationKey,FetchOwner> fetchedAssociationKeyOwnerMap = new HashMap<AssociationKey, FetchOwner>();
+
+	@Override
+	public void associationKeyRegistered(AssociationKey associationKey) {
+		// todo : use this information to maintain a map of AssociationKey->FetchOwner mappings (associationKey + current fetchOwner stack entry)
+		//		that mapping can then be used in #foundCircularAssociationKey to build the proper BiDirectionalEntityFetch
+		//		based on the mapped owner
+		fetchedAssociationKeyOwnerMap.put( associationKey, currentFetchOwner() );
+	}
+
+	@Override
+	public void foundCircularAssociationKey(AssociationKey associationKey, AttributeDefinition attributeDefinition) {
+		// todo : use this information to create the BiDirectionalEntityFetch instances
+		final FetchOwner fetchOwner = fetchedAssociationKeyOwnerMap.get( associationKey );
+		if ( fetchOwner == null ) {
+			throw new IllegalStateException(
+					String.format(
+							"Expecting AssociationKey->FetchOwner mapping for %s",
+							associationKey.toString()
+					)
+			);
+		}
+
+		currentFetchOwner().addFetch( new CircularFetch( currentFetchOwner(), fetchOwner, attributeDefinition ) );
+	}
+
+	public static class CircularFetch implements BidirectionalEntityFetch, EntityReference, Fetch {
+		private final FetchOwner circularFetchOwner;
+		private final FetchOwner associationOwner;
+		private final AttributeDefinition attributeDefinition;
+
+		private final EntityReference targetEntityReference;
+
+		private final FetchStrategy fetchStrategy = new FetchStrategy(
+				FetchTiming.IMMEDIATE,
+				FetchStyle.JOIN
+		);
+
+		public CircularFetch(FetchOwner circularFetchOwner, FetchOwner associationOwner, AttributeDefinition attributeDefinition) {
+			this.circularFetchOwner = circularFetchOwner;
+			this.associationOwner = associationOwner;
+			this.attributeDefinition = attributeDefinition;
+			this.targetEntityReference = resolveEntityReference( associationOwner );
+		}
+
+		@Override
+		public EntityReference getTargetEntityReference() {
+			return targetEntityReference;
+		}
+
+		protected static EntityReference resolveEntityReference(FetchOwner owner) {
+			if ( EntityReference.class.isInstance( owner ) ) {
+				return (EntityReference) owner;
+			}
+			if ( CompositeFetch.class.isInstance( owner ) ) {
+				return resolveEntityReference( ( (CompositeFetch) owner ).getOwner() );
+			}
+			// todo : what others?
+
+			throw new UnsupportedOperationException(
+					"Unexpected FetchOwner type [" + owner + "] encountered trying to build circular fetch"
+			);
+
+		}
+
+		@Override
+		public FetchOwner getOwner() {
+			return circularFetchOwner;
+		}
+
+		@Override
+		public PropertyPath getPropertyPath() {
+			return null;  //To change body of implemented methods use File | Settings | File Templates.
+		}
+
+		@Override
+		public Type getFetchedType() {
+			return attributeDefinition.getType();
+		}
+
+		@Override
+		public FetchStrategy getFetchStrategy() {
+			return fetchStrategy;
+		}
+
+		@Override
+		public boolean isNullable() {
+			return attributeDefinition.isNullable();
+		}
+
+		@Override
+		public String getAdditionalJoinConditions() {
+			return null;
+		}
+
+		@Override
+		public String[] toSqlSelectFragments(String alias) {
+			return new String[0];
+		}
+
+		@Override
+		public Fetch makeCopy(CopyContext copyContext, FetchOwner fetchOwnerCopy) {
+			// todo : will need this implemented
+			return null;
+		}
+
+		@Override
+		public LockMode getLockMode() {
+			return targetEntityReference.getLockMode();
+		}
+
+		@Override
+		public EntityReference getEntityReference() {
+			return targetEntityReference;
+		}
+
+		@Override
+		public EntityPersister getEntityPersister() {
+			return targetEntityReference.getEntityPersister();
+		}
+
+		@Override
+		public IdentifierDescription getIdentifierDescription() {
+			return targetEntityReference.getIdentifierDescription();
+		}
+
+		@Override
+		public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
+			throw new IllegalStateException( "IdentifierDescription should never be injected from circular fetch side" );
+		}
+	}
+
 	@Override
 	public void foundAny(AssociationAttributeDefinition attributeDefinition, AnyMappingDefinition anyDefinition) {
 		// for ANY mappings we need to build a Fetch:
 		//		1) fetch type is SELECT, timing might be IMMEDIATE or DELAYED depending on whether it was defined as lazy
 		//		2) (because the fetch cannot be a JOIN...) do not push it to the stack
 		final FetchStrategy fetchStrategy = determineFetchPlan( attributeDefinition );
 
 		final FetchOwner fetchOwner = currentFetchOwner();
 		fetchOwner.validateFetchPlan( fetchStrategy, attributeDefinition );
 
 		fetchOwner.buildAnyFetch(
 				attributeDefinition,
 				anyDefinition,
 				fetchStrategy,
 				this
 		);
 	}
 
 	protected boolean handleCompositeAttribute(CompositionDefinition attributeDefinition) {
 		final FetchOwner fetchOwner = currentFetchOwner();
 		final CompositeFetch fetch = fetchOwner.buildCompositeFetch( attributeDefinition, this );
 		pushToStack( fetch );
 		return true;
 	}
 
 	protected boolean handleAssociationAttribute(AssociationAttributeDefinition attributeDefinition) {
 		// todo : this seems to not be correct for one-to-one
 		final FetchStrategy fetchStrategy = determineFetchPlan( attributeDefinition );
 		if ( fetchStrategy.getStyle() != FetchStyle.JOIN ) {
 			return false;
 		}
 //		if ( fetchStrategy.getTiming() != FetchTiming.IMMEDIATE ) {
 //			return false;
 //		}
 
 		final FetchOwner fetchOwner = currentFetchOwner();
 		fetchOwner.validateFetchPlan( fetchStrategy, attributeDefinition );
 
 		final Fetch associationFetch;
 		final AssociationAttributeDefinition.AssociationNature nature = attributeDefinition.getAssociationNature();
 		if ( nature == AssociationAttributeDefinition.AssociationNature.ANY ) {
 			return false;
 //			throw new NotYetImplementedException( "AnyType support still in progress" );
 		}
 		else if ( nature == AssociationAttributeDefinition.AssociationNature.ENTITY ) {
 			associationFetch = fetchOwner.buildEntityFetch(
 					attributeDefinition,
 					fetchStrategy,
 					this
 			);
 		}
 		else {
 			associationFetch = fetchOwner.buildCollectionFetch( attributeDefinition, fetchStrategy, this );
 			pushToCollectionStack( (CollectionReference) associationFetch );
 		}
 
 		if ( FetchOwner.class.isInstance( associationFetch) ) {
 			pushToStack( (FetchOwner) associationFetch );
 		}
 
 		return true;
 	}
 
 	protected abstract FetchStrategy determineFetchPlan(AssociationAttributeDefinition attributeDefinition);
 
 	protected int currentDepth() {
 		return fetchOwnerStack.size();
 	}
 
 	protected boolean isTooManyCollections() {
 		return false;
 	}
 
 	private void pushToStack(FetchOwner fetchOwner) {
 		log.trace( "Pushing fetch owner to stack : " + fetchOwner );
 		mdcStack().push( fetchOwner.getPropertyPath() );
 		fetchOwnerStack.addFirst( fetchOwner );
 	}
 
 	private MDCStack mdcStack() {
 		return (MDCStack) MDC.get( MDC_KEY );
 	}
 
 	private FetchOwner popFromStack() {
 		final FetchOwner last = fetchOwnerStack.removeFirst();
 		log.trace( "Popped fetch owner from stack : " + last );
 		mdcStack().pop();
 		if ( FetchStackAware.class.isInstance( last ) ) {
 			( (FetchStackAware) last ).poppedFromStack();
 		}
 		return last;
 	}
 
 	private void pushToCollectionStack(CollectionReference collectionReference) {
 		log.trace( "Pushing collection reference to stack : " + collectionReference );
 		mdcStack().push( collectionReference.getPropertyPath() );
 		collectionReferenceStack.addFirst( collectionReference );
 	}
 
 	private CollectionReference popFromCollectionStack() {
 		final CollectionReference last = collectionReferenceStack.removeFirst();
 		log.trace( "Popped collection reference from stack : " + last );
 		mdcStack().pop();
 		if ( FetchStackAware.class.isInstance( last ) ) {
 			( (FetchStackAware) last ).poppedFromStack();
 		}
 		return last;
 	}
 
 	protected abstract EntityReturn buildRootEntityReturn(EntityDefinition entityDefinition);
 
 	protected abstract CollectionReturn buildRootCollectionReturn(CollectionDefinition collectionDefinition);
 
 
 
 	// LoadPlanBuildingContext impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory();
 	}
 
 	public static interface FetchStackAware {
 		public void poppedFromStack();
 	}
 
-	protected static abstract class AbstractIdentifierAttributeCollector extends AbstractFetchOwner
+	protected static abstract class AbstractIdentifierAttributeCollector
+			extends AbstractFetchOwner
 			implements FetchOwner, EntityReference, FetchStackAware {
 		protected final EntityReference entityReference;
 		private final EntityPersisterBasedSqlSelectFragmentResolver sqlSelectFragmentResolver;
 		protected final Map<Fetch,HydratedCompoundValueHandler> fetchToHydratedStateExtractorMap
 				= new HashMap<Fetch, HydratedCompoundValueHandler>();
 
 		public AbstractIdentifierAttributeCollector(SessionFactoryImplementor sessionFactory, EntityReference entityReference) {
 			super( sessionFactory );
 			this.entityReference = entityReference;
 			this.sqlSelectFragmentResolver = new EntityPersisterBasedSqlSelectFragmentResolver(
 					(Queryable) entityReference.getEntityPersister()
 			);
 		}
 
 		@Override
 		public LockMode getLockMode() {
 			return entityReference.getLockMode();
 		}
 
 		@Override
 		public EntityReference getEntityReference() {
 			return this;
 		}
 
 		@Override
 		public EntityPersister getEntityPersister() {
 			return entityReference.getEntityPersister();
 		}
 
 		@Override
 		public IdentifierDescription getIdentifierDescription() {
 			return entityReference.getIdentifierDescription();
 		}
 
 		@Override
 		public CollectionFetch buildCollectionFetch(
 				AssociationAttributeDefinition attributeDefinition,
 				FetchStrategy fetchStrategy,
 				LoadPlanBuildingContext loadPlanBuildingContext) {
 			throw new WalkingException( "Entity identifier cannot contain persistent collections" );
 		}
 
 		@Override
 		public AnyFetch buildAnyFetch(
 				AttributeDefinition attribute,
 				AnyMappingDefinition anyDefinition,
 				FetchStrategy fetchStrategy,
 				LoadPlanBuildingContext loadPlanBuildingContext) {
 			throw new WalkingException( "Entity identifier cannot contain ANY type mappings" );
 		}
 
 		@Override
 		public EntityFetch buildEntityFetch(
 				AssociationAttributeDefinition attributeDefinition,
 				FetchStrategy fetchStrategy,
 				LoadPlanBuildingContext loadPlanBuildingContext) {
 			// we have a key-many-to-one
 			//
 			// IMPL NOTE: we pass ourselves as the FetchOwner which will route the fetch back through our #addFetch
 			// 		impl.  We collect them there and later build the IdentifierDescription
 
 			// if `this` is a fetch and its owner is "the same" (bi-directionality) as the attribute to be join fetched
 			// we should wrap our FetchOwner as an EntityFetch.  That should solve everything except for the alias
 			// context lookups because of the different instances (because of wrapping).  So somehow the consumer of this
 			// needs to be able to unwrap it to do the alias lookup, and would have to know to do that.
 			//
 			//
 			// we are processing the EntityReference(Address) identifier.  we come across its key-many-to-one reference
 			// to Person.  Now, if EntityReference(Address) is an instance of EntityFetch(Address) there is a strong
 			// likelihood that we have a bi-directionality and need to handle that specially.
 			//
 			// how to best (a) find the bi-directionality and (b) represent that?
 
 			if ( EntityFetch.class.isInstance( entityReference ) ) {
 				// we just confirmed that EntityReference(Address) is an instance of EntityFetch(Address),
 				final EntityFetch entityFetch = (EntityFetch) entityReference;
 				final FetchOwner entityFetchOwner = entityFetch.getOwner();
 				// so at this point we need to see if entityFetchOwner and attributeDefinition refer to the
 				// "same thing".  "same thing" == "same type" && "same column(s)"?
 				//
 				// i make assumptions here that that the attribute type is the EntityType, is that always valid?
 				final EntityType attributeDefinitionTypeAsEntityType = (EntityType) attributeDefinition.getType();
 
 				final boolean sameType = attributeDefinitionTypeAsEntityType.getAssociatedEntityName().equals(
 						entityFetchOwner.retrieveFetchSourcePersister().getEntityName()
 				);
 
 				if ( sameType ) {
 					// check same columns as well?
 
 					return new KeyManyToOneBidirectionalEntityFetch(
 							sessionFactory(),
 							//ugh
 							LockMode.READ,
 							this,
 							attributeDefinition,
 							(EntityReference) entityFetchOwner,
 							fetchStrategy
 					);
 				}
 			}
 
 			final EntityFetch fetch = super.buildEntityFetch( attributeDefinition, fetchStrategy, loadPlanBuildingContext );
 
 			// pretty sure this HydratedCompoundValueExtractor stuff is not needed...
 			fetchToHydratedStateExtractorMap.put( fetch, attributeDefinition.getHydratedCompoundValueExtractor() );
 
 			return fetch;
 		}
 
 
 		@Override
 		public Type getType(Fetch fetch) {
 			return fetch.getFetchedType();
 		}
 
 		@Override
 		public boolean isNullable(Fetch fetch) {
 			return  fetch.isNullable();
 		}
 
 		@Override
 		public String[] toSqlSelectFragments(Fetch fetch, String alias) {
 			return fetch.toSqlSelectFragments( alias );
 		}
 
 		@Override
 		public SqlSelectFragmentResolver toSqlSelectFragmentResolver() {
 			return sqlSelectFragmentResolver;
 		}
 
 		@Override
 		public void poppedFromStack() {
 			final IdentifierDescription identifierDescription = buildIdentifierDescription();
 			entityReference.injectIdentifierDescription( identifierDescription );
 		}
 
 		protected abstract IdentifierDescription buildIdentifierDescription();
 
 		@Override
 		public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
 			( (FetchOwner) entityReference ).validateFetchPlan( fetchStrategy, attributeDefinition );
 		}
 
 		@Override
 		public EntityPersister retrieveFetchSourcePersister() {
 			return ( (FetchOwner) entityReference ).retrieveFetchSourcePersister();
 		}
 
 		@Override
 		public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
 			throw new WalkingException(
 					"IdentifierDescription collector should not get injected with IdentifierDescription"
 			);
 		}
 	}
 
 	protected static class EncapsulatedIdentifierAttributeCollector extends AbstractIdentifierAttributeCollector {
 		private final PropertyPath propertyPath;
 
 		public EncapsulatedIdentifierAttributeCollector(
 				final SessionFactoryImplementor sessionFactory,
 				final EntityReference entityReference) {
 			super( sessionFactory, entityReference );
 			this.propertyPath = ( (FetchOwner) entityReference ).getPropertyPath();
 		}
 
 		@Override
 		protected IdentifierDescription buildIdentifierDescription() {
 			return new IdentifierDescriptionImpl(
 					entityReference,
 					getFetches(),
 					null
 			);
 		}
 
 		@Override
 		public PropertyPath getPropertyPath() {
 			return propertyPath;
 		}
 	}
 
 	protected static class NonEncapsulatedIdentifierAttributeCollector extends AbstractIdentifierAttributeCollector {
 		private final PropertyPath propertyPath;
 
 		public NonEncapsulatedIdentifierAttributeCollector(
 				final SessionFactoryImplementor sessionfactory,
 				final EntityReference entityReference) {
 			super( sessionfactory, entityReference );
 			this.propertyPath = ( (FetchOwner) entityReference ).getPropertyPath().append( "<id>" );
 		}
 
 		@Override
 		protected IdentifierDescription buildIdentifierDescription() {
 			return new IdentifierDescriptionImpl(
 					entityReference,
 					getFetches(),
 					fetchToHydratedStateExtractorMap
 			);
 		}
 
 		@Override
 		public PropertyPath getPropertyPath() {
 			return propertyPath;
 		}
 	}
 
 	private static class IdentifierDescriptionImpl implements IdentifierDescription {
 		private final EntityReference entityReference;
 		private final Fetch[] identifierFetches;
 		private final Map<Fetch,HydratedCompoundValueHandler> fetchToHydratedStateExtractorMap;
 
 		private IdentifierDescriptionImpl(
 				EntityReference entityReference,
 				Fetch[] identifierFetches,
 				Map<Fetch, HydratedCompoundValueHandler> fetchToHydratedStateExtractorMap) {
 			this.entityReference = entityReference;
 			this.identifierFetches = identifierFetches;
 			this.fetchToHydratedStateExtractorMap = fetchToHydratedStateExtractorMap;
 		}
 
 		@Override
 		public Fetch[] getFetches() {
 			return identifierFetches;
 		}
-
-		@Override
-		public HydratedCompoundValueHandler getHydratedStateHandler(Fetch fetch) {
-			return fetchToHydratedStateExtractorMap == null ? null : fetchToHydratedStateExtractorMap.get( fetch );
-		}
-
-		@Override
-		public void hydrate(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-			final ResultSetProcessingContext.EntityReferenceProcessingState ownerEntityReferenceProcessingState =
-					context.getProcessingState( entityReference );
-			final Object ownerIdentifierHydratedState = ownerEntityReferenceProcessingState.getIdentifierHydratedForm();
-
-			if ( ownerIdentifierHydratedState != null ) {
-				for ( Fetch fetch : identifierFetches ) {
-					if ( fetch instanceof EntityFetch ) {
-						final ResultSetProcessingContext.EntityReferenceProcessingState fetchEntityReferenceProcessingState =
-								context.getProcessingState( (EntityFetch) fetch );
-						// if the identifier was already hydrated, nothing to do
-						if ( fetchEntityReferenceProcessingState.getIdentifierHydratedForm() != null ) {
-							continue;
-						}
-						// try to extract the sub-hydrated value from the owners tuple array
-						if ( fetchToHydratedStateExtractorMap != null && ownerIdentifierHydratedState != null ) {
-							Serializable extracted = (Serializable) fetchToHydratedStateExtractorMap.get( fetch )
-									.extract( ownerIdentifierHydratedState );
-							fetchEntityReferenceProcessingState.registerIdentifierHydratedForm( extracted );
-							continue;
-						}
-
-						// if we can't, then read from result set
-						fetch.hydrate( resultSet, context );
-					}
-					else {
-						throw new NotYetImplementedException( "Cannot hydrate identifier Fetch that is not an EntityFetch" );
-					}
-				}
-				return;
-			}
-
-			final String[] columnNames;
-			if ( EntityFetch.class.isInstance( entityReference )
-					&& !FetchStrategyHelper.isJoinFetched( ((EntityFetch) entityReference).getFetchStrategy() ) ) {
-				final EntityFetch fetch = (EntityFetch) entityReference;
-				final FetchOwner fetchOwner = fetch.getOwner();
-				if ( EntityReference.class.isInstance( fetchOwner ) ) {
-					throw new NotYetImplementedException();
-//					final EntityReference ownerEntityReference = (EntityReference) fetchOwner;
-//					final EntityAliases ownerEntityAliases = context.getAliasResolutionContext()
-//							.resolveEntityColumnAliases( ownerEntityReference );
-//					final int propertyIndex = ownerEntityReference.getEntityPersister()
-//							.getEntityMetamodel()
-//							.getPropertyIndex( fetch.getOwnerPropertyName() );
-//					columnNames = ownerEntityAliases.getSuffixedPropertyAliases()[ propertyIndex ];
-				}
-				else {
-					// todo : better message here...
-					throw new WalkingException( "Cannot locate association column names" );
-				}
-			}
-			else {
-				columnNames = context.getAliasResolutionContext()
-						.resolveAliases( entityReference )
-						.getColumnAliases()
-						.getSuffixedKeyAliases();
-			}
-
-			final Object hydratedIdentifierState = entityReference.getEntityPersister().getIdentifierType().hydrate(
-					resultSet,
-					columnNames,
-					context.getSession(),
-					null
-			);
-			context.getProcessingState( entityReference ).registerIdentifierHydratedForm( hydratedIdentifierState );
-		}
-
-		@Override
-		public EntityKey resolve(ResultSet resultSet, ResultSetProcessingContext context) throws SQLException {
-			for ( Fetch fetch : identifierFetches ) {
-				resolveIdentifierFetch( resultSet, context, fetch );
-			}
-
-			final ResultSetProcessingContext.EntityReferenceProcessingState ownerEntityReferenceProcessingState =
-					context.getProcessingState( entityReference );
-			Object hydratedState = ownerEntityReferenceProcessingState.getIdentifierHydratedForm();
-			Serializable resolvedId = (Serializable) entityReference.getEntityPersister()
-					.getIdentifierType()
-					.resolve( hydratedState, context.getSession(), null );
-			return context.getSession().generateEntityKey( resolvedId, entityReference.getEntityPersister() );
-		}
-	}
-
-	private static void resolveIdentifierFetch(
-			ResultSet resultSet,
-			ResultSetProcessingContext context,
-			Fetch fetch) throws SQLException {
-		if ( fetch instanceof EntityFetch ) {
-			EntityFetch entityFetch = (EntityFetch) fetch;
-			final ResultSetProcessingContext.EntityReferenceProcessingState entityReferenceProcessingState =
-					context.getProcessingState( entityFetch );
-			if ( entityReferenceProcessingState.getEntityKey() != null ) {
-				return;
-			}
-
-			EntityKey fetchKey = entityFetch.resolveInIdentifier( resultSet, context );
-			entityReferenceProcessingState.registerEntityKey( fetchKey );
-		}
-		else if ( fetch instanceof CompositeFetch ) {
-			for ( Fetch subFetch : ( (CompositeFetch) fetch ).getFetches() ) {
-				resolveIdentifierFetch( resultSet, context, subFetch );
-			}
-		}
 	}
 
+
 	public static class MDCStack {
 		private ArrayDeque<PropertyPath> pathStack = new ArrayDeque<PropertyPath>();
 
 		public void push(PropertyPath path) {
 			pathStack.addFirst( path );
 		}
 
 		public void pop() {
 			pathStack.removeFirst();
 		}
 
 		public String toString() {
 			final PropertyPath path = pathStack.peekFirst();
 			return path == null ? "<no-path>" : path.getFullPath();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/IdentifierDescriptionInjectable.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/IdentifierDescriptionInjectable.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan/spi/IdentifierDescriptionInjectable.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/IdentifierDescriptionInjectable.java
index 4528bfa605..6b629cc632 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/IdentifierDescriptionInjectable.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/IdentifierDescriptionInjectable.java
@@ -1,33 +1,35 @@
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
-package org.hibernate.loader.plan.spi;
+package org.hibernate.loader.plan.spi.build;
+
+import org.hibernate.loader.plan.spi.IdentifierDescription;
 
 /**
- * Ugh
+ * Ugh.
  *
  * @author Steve Ebersole
  */
 public interface IdentifierDescriptionInjectable {
 	public void injectIdentifierDescription(IdentifierDescription identifierDescription);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/LoadPlanBuilder.java b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/MetadataDrivenLoadPlanBuilder.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/LoadPlanBuilder.java
rename to hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/MetadataDrivenLoadPlanBuilder.java
index 4c3d025489..e1f82b89ce 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/LoadPlanBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan/spi/build/MetadataDrivenLoadPlanBuilder.java
@@ -1,66 +1,67 @@
 /*
  * jDocBook, processing of DocBook sources
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
 package org.hibernate.loader.plan.spi.build;
 
 import org.hibernate.loader.plan.spi.LoadPlan;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.MetadataDrivenModelGraphVisitor;
 
 /**
- * Coordinates building of a {@link org.hibernate.loader.plan.spi.LoadPlan} between the
- * {@link org.hibernate.persister.walking.spi.MetadataDrivenModelGraphVisitor} and
- * {@link LoadPlanBuilderStrategy}
+ * A metadata-driven builder of LoadPlans.  Coordinates between the {@link MetadataDrivenModelGraphVisitor} and
+ * {@link LoadPlanBuilderStrategy}.
  *
  * @author Steve Ebersole
+ *
+ * @see MetadataDrivenModelGraphVisitor
  */
-public class LoadPlanBuilder {
+public class MetadataDrivenLoadPlanBuilder {
 	/**
 	 * Coordinates building a LoadPlan that defines just a single root entity return (may have fetches).
 	 * <p/>
 	 * Typically this includes building load plans for entity loading or cascade loading.
 	 *
 	 * @param strategy The strategy defining the load plan shaping
 	 * @param persister The persister for the entity forming the root of the load plan.
 	 *
 	 * @return The built load plan.
 	 */
 	public static LoadPlan buildRootEntityLoadPlan(LoadPlanBuilderStrategy strategy, EntityPersister persister) {
 		MetadataDrivenModelGraphVisitor.visitEntity( strategy, persister );
 		return strategy.buildLoadPlan();
 	}
 
 	/**
 	 * Coordinates building a LoadPlan that defines just a single root collection return (may have fetches).
 	 *
 	 * @param strategy The strategy defining the load plan shaping
 	 * @param persister The persister for the collection forming the root of the load plan.
 	 *
 	 * @return The built load plan.
 	 */
 	public static LoadPlan buildRootCollectionLoadPlan(LoadPlanBuilderStrategy strategy, CollectionPersister persister) {
 		MetadataDrivenModelGraphVisitor.visitCollection( strategy, persister );
 		return strategy.buildLoadPlan();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/CascadeLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/CascadeLoadPlanBuilderStrategy.java
new file mode 100644
index 0000000000..29bb0e6275
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/CascadeLoadPlanBuilderStrategy.java
@@ -0,0 +1,58 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.internal;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.FetchTiming;
+import org.hibernate.engine.spi.CascadingAction;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+
+/**
+ * A LoadPlan building strategy for cascade processing; meaning, it builds the LoadPlan for loading related to
+ * cascading a particular action across associations
+ *
+ * @author Steve Ebersole
+ */
+public class CascadeLoadPlanBuilderStrategy extends StandardFetchBasedLoadPlanBuilderStrategy {
+	private static final FetchStrategy EAGER = new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
+	private static final FetchStrategy DELAYED = new FetchStrategy( FetchTiming.DELAYED, FetchStyle.SELECT );
+
+	private final CascadingAction cascadeActionToMatch;
+
+	public CascadeLoadPlanBuilderStrategy(
+			CascadingAction cascadeActionToMatch,
+			SessionFactoryImplementor sessionFactory,
+			LoadQueryInfluencers loadQueryInfluencers) {
+		super( sessionFactory, loadQueryInfluencers );
+		this.cascadeActionToMatch = cascadeActionToMatch;
+	}
+
+	@Override
+	protected FetchStrategy determineFetchStrategy(AssociationAttributeDefinition attributeDefinition) {
+		return attributeDefinition.determineCascadeStyle().doCascade( cascadeActionToMatch ) ? EAGER : DELAYED;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/CollectionQuerySpaceImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/CollectionQuerySpaceImpl.java
new file mode 100644
index 0000000000..33fdeb9794
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/CollectionQuerySpaceImpl.java
@@ -0,0 +1,252 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.internal;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.plan2.build.spi.AbstractQuerySpace;
+import org.hibernate.loader.plan2.build.spi.LoadPlanBuildingContext;
+import org.hibernate.loader.plan2.spi.CollectionQuerySpace;
+import org.hibernate.loader.plan2.spi.CompositeQuerySpace;
+import org.hibernate.loader.plan2.spi.Join;
+import org.hibernate.loader.plan2.spi.QuerySpace;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.type.CompositeType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CollectionQuerySpaceImpl extends AbstractQuerySpace implements CollectionQuerySpace {
+	private final CollectionPersister persister;
+
+	public CollectionQuerySpaceImpl(
+			CollectionPersister persister,
+			String uid,
+			QuerySpacesImpl querySpaces,
+			SessionFactoryImplementor sessionFactory) {
+		super( uid, Disposition.COLLECTION, querySpaces, sessionFactory );
+		this.persister = persister;
+	}
+
+	@Override
+	public CollectionPersister getCollectionPersister() {
+		return persister;
+	}
+
+	public static interface CollectionIndexEntityJoin extends Join {
+		public CollectionQuerySpaceImpl getCollectionQuerySpace();
+
+		@Override
+		CollectionQuerySpaceImpl getLeftHandSide();
+
+		@Override
+		EntityQuerySpaceImpl getRightHandSide();
+	}
+
+	public CollectionIndexEntityJoin addIndexEntityJoin(
+			EntityPersister indexPersister,
+			LoadPlanBuildingContext context) {
+		final String entityQuerySpaceUid = getQuerySpaces().generateImplicitUid();
+		final EntityQuerySpaceImpl entityQuerySpace = new EntityQuerySpaceImpl(
+				indexPersister,
+				entityQuerySpaceUid,
+				getQuerySpaces(),
+				sessionFactory()
+		);
+		( (QuerySpacesImpl) context.getQuerySpaces() ).registerQuerySpace( entityQuerySpace );
+
+		final CollectionIndexEntityJoin join = new CollectionIndexEntityJoin() {
+			@Override
+			public CollectionQuerySpaceImpl getCollectionQuerySpace() {
+				return CollectionQuerySpaceImpl.this;
+			}
+
+			@Override
+			public CollectionQuerySpaceImpl getLeftHandSide() {
+				return CollectionQuerySpaceImpl.this;
+			}
+
+			@Override
+			public EntityQuerySpaceImpl getRightHandSide() {
+				return entityQuerySpace;
+			}
+
+			@Override
+			public boolean isRightHandSideOptional() {
+				return false;
+			}
+		};
+		internalGetJoins().add( join );
+
+		return join;
+	}
+
+	public static interface CollectionIndexCompositeJoin extends Join {
+		public CollectionQuerySpaceImpl getCollectionQuerySpace();
+
+		@Override
+		CollectionQuerySpaceImpl getLeftHandSide();
+
+		@Override
+		CompositeQuerySpaceImpl getRightHandSide();
+	}
+
+	public CollectionIndexCompositeJoin addIndexCompositeJoin(
+			CompositeType compositeType,
+			LoadPlanBuildingContext context) {
+		final String compositeQuerySpaceUid = getQuerySpaces().generateImplicitUid();
+		final CompositeQuerySpaceImpl compositeQuerySpace = new CompositeQuerySpaceImpl(
+				CollectionQuerySpaceImpl.this.getUid(),
+				compositeType,
+				compositeQuerySpaceUid,
+				Disposition.COMPOSITE,
+				getQuerySpaces(),
+				sessionFactory()
+		);
+		( (QuerySpacesImpl) context.getQuerySpaces() ).registerQuerySpace( compositeQuerySpace );
+
+		final CollectionIndexCompositeJoin join = new CollectionIndexCompositeJoin() {
+			@Override
+			public CollectionQuerySpaceImpl getCollectionQuerySpace() {
+				return CollectionQuerySpaceImpl.this;
+			}
+
+			@Override
+			public CollectionQuerySpaceImpl getLeftHandSide() {
+				return CollectionQuerySpaceImpl.this;
+			}
+
+			@Override
+			public CompositeQuerySpaceImpl getRightHandSide() {
+				return compositeQuerySpace;
+			}
+
+			@Override
+			public boolean isRightHandSideOptional() {
+				return false;
+			}
+		};
+		internalGetJoins().add( join );
+
+		return join;
+	}
+
+	public static interface CollectionElementEntityJoin extends Join {
+		public CollectionQuerySpaceImpl getCollectionQuerySpace();
+
+		@Override
+		CollectionQuerySpaceImpl getLeftHandSide();
+
+		@Override
+		EntityQuerySpaceImpl getRightHandSide();
+	}
+
+	public CollectionElementEntityJoin addElementEntityJoin(
+			EntityPersister elementPersister,
+			LoadPlanBuildingContext context) {
+		final String entityQuerySpaceUid = getQuerySpaces().generateImplicitUid();
+		final EntityQuerySpaceImpl entityQuerySpace = new EntityQuerySpaceImpl(
+				elementPersister,
+				entityQuerySpaceUid,
+				getQuerySpaces(),
+				sessionFactory()
+		);
+		( (QuerySpacesImpl) context.getQuerySpaces() ).registerQuerySpace( entityQuerySpace );
+
+		final CollectionElementEntityJoin join = new CollectionElementEntityJoin() {
+			@Override
+			public CollectionQuerySpaceImpl getCollectionQuerySpace() {
+				return CollectionQuerySpaceImpl.this;
+			}
+
+			@Override
+			public CollectionQuerySpaceImpl getLeftHandSide() {
+				return CollectionQuerySpaceImpl.this;
+			}
+
+			@Override
+			public EntityQuerySpaceImpl getRightHandSide() {
+				return entityQuerySpace;
+			}
+
+			@Override
+			public boolean isRightHandSideOptional() {
+				return false;
+			}
+		};
+		internalGetJoins().add( join );
+
+		return join;
+	}
+
+	public static interface CollectionElementCompositeJoin extends Join {
+		public CollectionQuerySpaceImpl getCollectionQuerySpace();
+
+		@Override
+		CollectionQuerySpaceImpl getLeftHandSide();
+
+		@Override
+		CompositeQuerySpaceImpl getRightHandSide();
+	}
+
+	public CollectionElementCompositeJoin addElementCompositeJoin(
+			CompositeType compositeType,
+			LoadPlanBuildingContext context) {
+		final String compositeQuerySpaceUid = getQuerySpaces().generateImplicitUid();
+		final CompositeQuerySpaceImpl compositeQuerySpace = new CompositeQuerySpaceImpl(
+				CollectionQuerySpaceImpl.this.getUid(),
+				compositeType,
+				compositeQuerySpaceUid,
+				Disposition.COMPOSITE,
+				getQuerySpaces(),
+				sessionFactory()
+		);
+		( (QuerySpacesImpl) context.getQuerySpaces() ).registerQuerySpace( compositeQuerySpace );
+
+		final CollectionElementCompositeJoin join = new CollectionElementCompositeJoin() {
+			@Override
+			public CollectionQuerySpaceImpl getCollectionQuerySpace() {
+				return CollectionQuerySpaceImpl.this;
+			}
+
+			@Override
+			public CollectionQuerySpaceImpl getLeftHandSide() {
+				return CollectionQuerySpaceImpl.this;
+			}
+
+			@Override
+			public CompositeQuerySpaceImpl getRightHandSide() {
+				return compositeQuerySpace;
+			}
+
+			@Override
+			public boolean isRightHandSideOptional() {
+				return false;
+			}
+		};
+		internalGetJoins().add( join );
+
+		return join;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/CompositeQuerySpaceImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/CompositeQuerySpaceImpl.java
new file mode 100644
index 0000000000..507a8b9995
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/CompositeQuerySpaceImpl.java
@@ -0,0 +1,76 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.internal;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.plan2.build.spi.AbstractQuerySpace;
+import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpace;
+import org.hibernate.loader.plan2.spi.CompositeQuerySpace;
+import org.hibernate.loader.plan2.spi.JoinDefinedByMetadata;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.type.CompositeType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CompositeQuerySpaceImpl extends AbstractQuerySpace implements CompositeQuerySpace, ExpandingQuerySpace {
+	private final String ownerUid;
+	private final CompositeType compositeType;
+
+	public CompositeQuerySpaceImpl(
+			String ownerUid,
+			CompositeType compositeType,
+			String uid,
+			Disposition disposition,
+			QuerySpacesImpl querySpaces,
+			SessionFactoryImplementor sessionFactory) {
+		super( uid, disposition, querySpaces, sessionFactory );
+		this.ownerUid = ownerUid;
+		this.compositeType = compositeType;
+	}
+
+	@Override
+	public JoinDefinedByMetadata addCompositeJoin(
+			CompositionDefinition compositionDefinition, String querySpaceUid) {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public JoinDefinedByMetadata addEntityJoin(
+			AttributeDefinition attributeDefinition,
+			EntityPersister persister,
+			String querySpaceUid,
+			boolean optional) {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public JoinDefinedByMetadata addCollectionJoin(
+			AttributeDefinition attributeDefinition, CollectionPersister collectionPersister, String querySpaceUid) {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/EntityIdentifierDescriptionImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/EntityIdentifierDescriptionImpl.java
new file mode 100644
index 0000000000..0feab79117
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/EntityIdentifierDescriptionImpl.java
@@ -0,0 +1,200 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.internal;
+
+import java.util.ArrayList;
+import java.util.List;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan2.build.spi.ExpandingEntityIdentifierDescription;
+import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpace;
+import org.hibernate.loader.plan2.build.spi.LoadPlanBuildingContext;
+import org.hibernate.loader.plan2.internal.EntityFetchImpl;
+import org.hibernate.loader.plan2.spi.CollectionFetch;
+import org.hibernate.loader.plan2.spi.CompositeFetch;
+import org.hibernate.loader.plan2.spi.EntityFetch;
+import org.hibernate.loader.plan2.spi.EntityReference;
+import org.hibernate.loader.plan2.spi.Fetch;
+import org.hibernate.loader.plan2.spi.Join;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.persister.walking.spi.WalkingException;
+import org.hibernate.type.EntityType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EntityIdentifierDescriptionImpl implements ExpandingEntityIdentifierDescription {
+	private final EntityReference entityReference;
+	private final PropertyPath propertyPath;
+
+	private List<Fetch> fetches;
+
+	public EntityIdentifierDescriptionImpl(EntityReference entityReference) {
+		this.entityReference = entityReference;
+		this.propertyPath = entityReference.getPropertyPath().append( "<id>" );
+	}
+
+	// IdentifierDescription impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public PropertyPath getPropertyPath() {
+		return propertyPath;
+	}
+
+	@Override
+	public Fetch[] getFetches() {
+		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
+	}
+
+
+	// FetchContainer impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
+		if ( attributeDefinition.getType().isCollectionType() ) {
+			throw new WalkingException(
+					"Encountered collection attribute in identifier fetches: " + attributeDefinition.getSource() +
+							"." + attributeDefinition.getName()
+			);
+		}
+		// todo : allow bi-directional key-many-to-one fetches?
+		//		those do cause problems in Loader; question is whether those are indicative of that situation or
+		// 		of Loaders ability to handle it.
+	}
+
+	@Override
+	public EntityFetch buildEntityFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		// we have a key-many-to-one
+		//
+		// IMPL NOTE: we pass ourselves as the FetchOwner which will route the fetch back through our #addFetch
+		// 		impl.  We collect them there and later build the IdentifierDescription
+
+		// if `this` is a fetch and its owner is "the same" (bi-directionality) as the attribute to be join fetched
+		// we should wrap our FetchOwner as an EntityFetch.  That should solve everything except for the alias
+		// context lookups because of the different instances (because of wrapping).  So somehow the consumer of this
+		// needs to be able to unwrap it to do the alias lookup, and would have to know to do that.
+		//
+		//
+		// we are processing the EntityReference(Address) identifier.  we come across its key-many-to-one reference
+		// to Person.  Now, if EntityReference(Address) is an instance of EntityFetch(Address) there is a strong
+		// likelihood that we have a bi-directionality and need to handle that specially.
+		//
+		// how to best (a) find the bi-directionality and (b) represent that?
+
+		final EntityType fetchedType = (EntityType) attributeDefinition.getType();
+		final EntityPersister fetchedPersister = loadPlanBuildingContext.getSessionFactory().getEntityPersister(
+				fetchedType.getAssociatedEntityName()
+		);
+
+		if ( fetchedPersister == null ) {
+			throw new WalkingException(
+					String.format(
+							"Unable to locate EntityPersister [%s] for fetch [%s]",
+							fetchedType.getAssociatedEntityName(),
+							attributeDefinition.getName()
+					)
+			);
+		}
+
+		if ( EntityFetch.class.isInstance( entityReference ) ) {
+//			// we just confirmed that EntityReference(Address) is an instance of EntityFetch(Address),
+//			final EntityFetch entityFetch = (EntityFetch) entityReference;
+//			final FetchSource entityFetchSource = entityFetch.getSource();
+//			// so at this point we need to see if entityFetchSource and attributeDefinition refer to the
+//			// "same thing".  "same thing" == "same type" && "same column(s)"?
+//			//
+//			// i make assumptions here that that the attribute type is the EntityType, is that always valid?
+//
+//			final boolean sameType = fetchedPersister.getEntityName().equals(
+//					entityFetchSource.retrieveFetchSourcePersister().getEntityName()
+//			);
+//
+//			if ( sameType ) {
+//				// check same columns as well?
+//
+//				return new KeyManyToOneBidirectionalEntityFetch(
+//						sessionFactory(),
+//						//ugh
+//						LockMode.READ,
+//						this,
+//						attributeDefinition,
+//						(EntityReference) entityFetchSource,
+//						fetchStrategy
+//				);
+//			}
+		}
+
+		final ExpandingQuerySpace leftHandSide = (ExpandingQuerySpace) loadPlanBuildingContext.getQuerySpaces().findQuerySpaceByUid(
+				entityReference.getQuerySpaceUid()
+		);
+		Join join = leftHandSide.addEntityJoin(
+				attributeDefinition,
+				fetchedPersister,
+				loadPlanBuildingContext.getQuerySpaces().generateImplicitUid(),
+				attributeDefinition.isNullable()
+		);
+		final EntityFetch fetch = new EntityFetchImpl(
+				this,
+				attributeDefinition,
+				fetchedPersister,
+				fetchStrategy,
+				join
+		);
+		addFetch( fetch );
+
+//		this.sqlSelectFragmentResolver = new EntityPersisterBasedSqlSelectFragmentResolver( (Queryable) persister );
+
+		return fetch;
+	}
+
+	private void addFetch(EntityFetch fetch) {
+		if ( fetches == null ) {
+			fetches = new ArrayList<Fetch>();
+		}
+		fetches.add( fetch );
+	}
+
+	@Override
+	public CompositeFetch buildCompositeFetch(
+			CompositionDefinition attributeDefinition,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		throw new WalkingException( "Entity identifier cannot contain persistent collections" );
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/EntityQuerySpaceImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/EntityQuerySpaceImpl.java
new file mode 100644
index 0000000000..0369751495
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/EntityQuerySpaceImpl.java
@@ -0,0 +1,107 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.internal;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.plan2.build.spi.AbstractQuerySpace;
+import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpace;
+import org.hibernate.loader.plan2.spi.EntityQuerySpace;
+import org.hibernate.loader.plan2.spi.JoinDefinedByMetadata;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.type.CompositeType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EntityQuerySpaceImpl extends AbstractQuerySpace implements ExpandingQuerySpace, EntityQuerySpace {
+	private final EntityPersister persister;
+
+	public EntityQuerySpaceImpl(
+			EntityPersister persister,
+			String uid,
+			QuerySpacesImpl querySpaces,
+			SessionFactoryImplementor sessionFactory) {
+		super( uid, Disposition.ENTITY, querySpaces, sessionFactory );
+		this.persister = persister;
+	}
+
+	@Override
+	public EntityPersister getEntityPersister() {
+		return persister;
+	}
+
+	@Override
+	public JoinDefinedByMetadata addCompositeJoin(CompositionDefinition compositionDefinition, String querySpaceUid) {
+		final CompositeQuerySpaceImpl rhs = new CompositeQuerySpaceImpl(
+				getUid(),
+				compositionDefinition.getType(),
+				querySpaceUid,
+				Disposition.COMPOSITE,
+				getQuerySpaces(),
+				sessionFactory()
+		);
+		final JoinDefinedByMetadata join = new JoinImpl( this, compositionDefinition, rhs, compositionDefinition.isNullable() );
+		internalGetJoins().add( join );
+		getQuerySpaces().registerQuerySpace( rhs );
+		return join;
+	}
+
+	public JoinDefinedByMetadata addEntityJoin(
+			AttributeDefinition attribute,
+			EntityPersister persister,
+			String querySpaceUid,
+			boolean optional) {
+		final EntityQuerySpaceImpl rhs = new EntityQuerySpaceImpl(
+				persister,
+				querySpaceUid,
+				getQuerySpaces(),
+				sessionFactory()
+		);
+		final JoinDefinedByMetadata join = new JoinImpl( this, attribute, rhs, optional );
+		internalGetJoins().add( join );
+		getQuerySpaces().registerQuerySpace( rhs );
+		return join;
+	}
+
+	@Override
+	public JoinDefinedByMetadata addCollectionJoin(
+			AttributeDefinition attributeDefinition,
+			CollectionPersister collectionPersister,
+			String querySpaceUid) {
+		final CollectionQuerySpaceImpl rhs = new CollectionQuerySpaceImpl(
+				collectionPersister,
+				querySpaceUid,
+				getQuerySpaces(),
+				sessionFactory()
+		);
+		final JoinDefinedByMetadata join = new JoinImpl( this, attributeDefinition, rhs, attributeDefinition.isNullable() );
+		internalGetJoins().add( join );
+		getQuerySpaces().registerQuerySpace( rhs );
+		return join;
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/JoinImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/JoinImpl.java
new file mode 100644
index 0000000000..95c03f3aef
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/JoinImpl.java
@@ -0,0 +1,71 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.internal;
+
+import org.hibernate.loader.plan2.spi.JoinDefinedByMetadata;
+import org.hibernate.loader.plan2.spi.QuerySpace;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+
+/**
+ * @author Steve Ebersole
+ */
+public class JoinImpl implements JoinDefinedByMetadata {
+	private final QuerySpace leftHandSide;
+
+	private final AttributeDefinition attributeDefinition;
+
+	private final QuerySpace rightHandSide;
+	private final boolean rightHandSideOptional;
+
+	public JoinImpl(
+			QuerySpace leftHandSide,
+			AttributeDefinition attributeDefinition,
+			QuerySpace rightHandSide,
+			boolean rightHandSideOptional) {
+		this.leftHandSide = leftHandSide;
+		this.attributeDefinition = attributeDefinition;
+		this.rightHandSide = rightHandSide;
+		this.rightHandSideOptional = rightHandSideOptional;
+	}
+
+	@Override
+	public QuerySpace getLeftHandSide() {
+		return leftHandSide;
+	}
+
+	@Override
+	public AttributeDefinition getAttributeDefiningJoin() {
+		return attributeDefinition;
+	}
+
+	@Override
+	public QuerySpace getRightHandSide() {
+		return rightHandSide;
+	}
+
+	@Override
+	public boolean isRightHandSideOptional() {
+		return rightHandSideOptional;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/LoadPlanImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/LoadPlanImpl.java
new file mode 100644
index 0000000000..c2de9784c2
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/LoadPlanImpl.java
@@ -0,0 +1,112 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.internal;
+
+import java.util.Collections;
+import java.util.List;
+
+import org.hibernate.loader.plan2.spi.CollectionReturn;
+import org.hibernate.loader.plan2.spi.EntityReturn;
+import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan2.spi.QuerySpaces;
+import org.hibernate.loader.plan2.spi.Return;
+
+/**
+ * @author Steve Ebersole
+ */
+public class LoadPlanImpl implements LoadPlan {
+	private final List<? extends Return> returns;
+	private final QuerySpaces querySpaces;
+	private final Disposition disposition;
+	private final boolean areLazyAttributesForceFetched;
+
+	protected LoadPlanImpl(
+			List<? extends Return> returns,
+			QuerySpaces querySpaces,
+			Disposition disposition,
+			boolean areLazyAttributesForceFetched) {
+		this.returns = returns;
+		this.querySpaces = querySpaces;
+		this.disposition = disposition;
+		this.areLazyAttributesForceFetched = areLazyAttributesForceFetched;
+	}
+
+	/**
+	 * Creates a {@link Disposition#ENTITY_LOADER} LoadPlan.
+	 *
+	 * @param rootReturn The EntityReturn representation of the entity being loaded.
+	 */
+	public LoadPlanImpl(EntityReturn rootReturn, QuerySpaces querySpaces) {
+		this( Collections.singletonList( rootReturn ), querySpaces, Disposition.ENTITY_LOADER, false );
+	}
+
+	/**
+	 * Creates a {@link Disposition#COLLECTION_INITIALIZER} LoadPlan.
+	 *
+	 * @param rootReturn The CollectionReturn representation of the collection being initialized.
+	 */
+	public LoadPlanImpl(CollectionReturn rootReturn, QuerySpaces querySpaces) {
+		this( Collections.singletonList( rootReturn ), querySpaces, Disposition.COLLECTION_INITIALIZER, false );
+	}
+
+	/**
+	 * Creates a {@link Disposition#MIXED} LoadPlan.
+	 *
+	 * @param returns The mixed Return references
+	 * @param areLazyAttributesForceFetched Should lazy attributes (bytecode enhanced laziness) be fetched also?  This
+	 * effects the eventual SQL SELECT-clause which is why we have it here.  Currently this is "all-or-none"; you
+	 * can request that all lazy properties across all entities in the loadplan be force fetched or none.  There is
+	 * no entity-by-entity option.  {@code FETCH ALL PROPERTIES} is the way this is requested in HQL.  Would be nice to
+	 * consider this entity-by-entity, as opposed to all-or-none.  For example, "fetch the LOB value for the Item.image
+	 * attribute, but no others (leave them lazy)".  Not too concerned about having it at the attribute level.
+	 */
+	public LoadPlanImpl(List<? extends Return> returns, QuerySpaces querySpaces, boolean areLazyAttributesForceFetched) {
+		this( returns, querySpaces, Disposition.MIXED, areLazyAttributesForceFetched );
+	}
+
+	@Override
+	public List<? extends Return> getReturns() {
+		return returns;
+	}
+
+	@Override
+	public QuerySpaces getQuerySpaces() {
+		return querySpaces;
+	}
+
+	@Override
+	public Disposition getDisposition() {
+		return disposition;
+	}
+
+	@Override
+	public boolean areLazyAttributesForceFetched() {
+		return areLazyAttributesForceFetched;
+	}
+
+	@Override
+	public boolean hasAnyScalarReturns() {
+		return disposition == Disposition.MIXED;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/QuerySpacesImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/QuerySpacesImpl.java
new file mode 100644
index 0000000000..2cbc8e25db
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/QuerySpacesImpl.java
@@ -0,0 +1,121 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.internal;
+
+import java.util.ArrayList;
+import java.util.List;
+import java.util.Map;
+import java.util.concurrent.ConcurrentHashMap;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.plan2.build.spi.ExpandingQuerySpaces;
+import org.hibernate.loader.plan2.spi.EntityQuerySpace;
+import org.hibernate.loader.plan2.spi.QuerySpace;
+import org.hibernate.loader.plan2.spi.QuerySpaces;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * @author Steve Ebersole
+ */
+public class QuerySpacesImpl implements QuerySpaces, ExpandingQuerySpaces {
+	private final SessionFactoryImplementor sessionFactory;
+	private final List<QuerySpace> roots = new ArrayList<QuerySpace>();
+	private final Map<String,QuerySpace> querySpaceByUid = new ConcurrentHashMap<String, QuerySpace>();
+
+	public QuerySpacesImpl(SessionFactoryImplementor sessionFactory) {
+		this.sessionFactory = sessionFactory;
+	}
+
+
+	// QuerySpaces impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public Iterable<QuerySpace> getRootQuerySpaces() {
+		return roots;
+	}
+
+	@Override
+	public QuerySpace findQuerySpaceByUid(String uid) {
+		return querySpaceByUid.get( uid );
+	}
+
+
+
+	// ExpandingQuerySpaces impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	private int implicitUidBase = 0;
+
+	@Override
+	public String generateImplicitUid() {
+		return "<gen:" + implicitUidBase++ + ">";
+	}
+
+	@Override
+	public EntityQuerySpace makeEntityQuerySpace(String uid, EntityPersister entityPersister) {
+		if ( querySpaceByUid.containsKey( uid ) ) {
+			throw new IllegalStateException( "Encountered duplicate QuerySpace uid : " + uid );
+		}
+
+		final EntityQuerySpaceImpl space = new EntityQuerySpaceImpl(
+				entityPersister,
+				uid,
+				this,
+				sessionFactory
+		);
+		roots.add( space );
+
+		return space;
+	}
+
+	@Override
+	public CollectionQuerySpaceImpl makeCollectionQuerySpace(String uid, CollectionPersister collectionPersister) {
+		if ( querySpaceByUid.containsKey( uid ) ) {
+			throw new IllegalStateException( "Encountered duplicate QuerySpace uid : " + uid );
+		}
+
+		final CollectionQuerySpaceImpl space = new CollectionQuerySpaceImpl(
+				collectionPersister,
+				uid,
+				this,
+				sessionFactory
+		);
+		roots.add( space );
+
+		return space;
+	}
+
+	/**
+	 * Feeds a QuerySpace into this spaces group.
+	 *
+	 * @param querySpace The space
+	 */
+	protected void registerQuerySpace(QuerySpace querySpace) {
+		final QuerySpace previous = querySpaceByUid.put( querySpace.getUid(), querySpace );
+		if ( previous != null ) {
+			throw new IllegalStateException( "Encountered duplicate QuerySpace uid : " + querySpace.getUid() );
+		}
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/StandardFetchBasedLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/StandardFetchBasedLoadPlanBuilderStrategy.java
new file mode 100644
index 0000000000..9694454cc5
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/internal/StandardFetchBasedLoadPlanBuilderStrategy.java
@@ -0,0 +1,153 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.internal;
+
+import org.hibernate.HibernateException;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.FetchTiming;
+import org.hibernate.engine.spi.LoadQueryInfluencers;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan2.build.spi.AbstractMetamodelWalkingLoadPlanBuilderStrategy;
+import org.hibernate.loader.plan2.build.spi.LoadPlanBuilderStrategy;
+import org.hibernate.loader.plan2.spi.CollectionReturn;
+import org.hibernate.loader.plan2.spi.EntityReturn;
+import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.loader.plan2.spi.Return;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AssociationKey;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+
+/**
+ * LoadPlanBuilderStrategy implementation used for building LoadPlans based on metamodel-defined fetching.  Built
+ * LoadPlans contain a single root return object, either an {@link EntityReturn} or a {@link CollectionReturn}.
+ *
+ * @author Steve Ebersole
+ */
+public class StandardFetchBasedLoadPlanBuilderStrategy
+		extends AbstractMetamodelWalkingLoadPlanBuilderStrategy
+		implements LoadPlanBuilderStrategy {
+
+	private final LoadQueryInfluencers loadQueryInfluencers;
+
+	private Return rootReturn;
+
+	private PropertyPath propertyPath = new PropertyPath( "" );
+
+	public StandardFetchBasedLoadPlanBuilderStrategy(
+			SessionFactoryImplementor sessionFactory,
+			LoadQueryInfluencers loadQueryInfluencers) {
+		super( sessionFactory );
+		this.loadQueryInfluencers = loadQueryInfluencers;
+	}
+
+	@Override
+	protected boolean supportsRootEntityReturns() {
+		return true;
+	}
+
+	@Override
+	protected boolean supportsRootCollectionReturns() {
+		return true;
+	}
+
+	@Override
+	protected void addRootReturn(Return rootReturn) {
+		if ( this.rootReturn != null ) {
+			throw new HibernateException( "Root return already identified" );
+		}
+		this.rootReturn = rootReturn;
+	}
+
+	@Override
+	public LoadPlan buildLoadPlan() {
+		if ( EntityReturn.class.isInstance( rootReturn ) ) {
+			return new LoadPlanImpl( (EntityReturn) rootReturn, getQuerySpaces() );
+		}
+		else if ( CollectionReturn.class.isInstance( rootReturn ) ) {
+			return new LoadPlanImpl( (CollectionReturn) rootReturn, getQuerySpaces() );
+		}
+		else {
+			throw new IllegalStateException( "Unexpected root Return type : " + rootReturn );
+		}
+	}
+
+	@Override
+	protected FetchStrategy determineFetchStrategy(AssociationAttributeDefinition attributeDefinition) {
+		FetchStrategy fetchStrategy = attributeDefinition.determineFetchPlan( loadQueryInfluencers, propertyPath );
+		if ( fetchStrategy.getTiming() == FetchTiming.IMMEDIATE && fetchStrategy.getStyle() == FetchStyle.JOIN ) {
+			// see if we need to alter the join fetch to another form for any reason
+			fetchStrategy = adjustJoinFetchIfNeeded( attributeDefinition, fetchStrategy );
+		}
+		return fetchStrategy;
+	}
+
+	protected FetchStrategy adjustJoinFetchIfNeeded(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy) {
+		if ( currentDepth() > sessionFactory().getSettings().getMaximumFetchDepth() ) {
+			return new FetchStrategy( fetchStrategy.getTiming(), FetchStyle.SELECT );
+		}
+
+		if ( attributeDefinition.getType().isCollectionType() && isTooManyCollections() ) {
+			// todo : have this revert to batch or subselect fetching once "sql gen redesign" is in place
+			return new FetchStrategy( fetchStrategy.getTiming(), FetchStyle.SELECT );
+		}
+
+		return fetchStrategy;
+	}
+
+	@Override
+	protected boolean isTooManyCollections() {
+		return false;
+	}
+
+	@Override
+	public void foundCircularAssociationKey(AssociationKey associationKey, AttributeDefinition attributeDefinition) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+
+//	@Override
+//	protected EntityReturn buildRootEntityReturn(EntityDefinition entityDefinition) {
+//		final String entityName = entityDefinition.getEntityPersister().getEntityName();
+//		return new EntityReturn(
+//				sessionFactory(),
+//				LockMode.NONE, // todo : for now
+//				entityName
+//		);
+//	}
+//
+//	@Override
+//	protected CollectionReturn buildRootCollectionReturn(CollectionDefinition collectionDefinition) {
+//		final CollectionPersister persister = collectionDefinition.getCollectionPersister();
+//		final String collectionRole = persister.getRole();
+//		return new CollectionReturn(
+//				sessionFactory(),
+//				LockMode.NONE, // todo : for now
+//				persister.getOwnerEntityPersister().getEntityName(),
+//				StringHelper.unqualify( collectionRole )
+//		);
+//	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/AbstractMetamodelWalkingLoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/AbstractMetamodelWalkingLoadPlanBuilderStrategy.java
new file mode 100644
index 0000000000..c09327b4f7
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/AbstractMetamodelWalkingLoadPlanBuilderStrategy.java
@@ -0,0 +1,700 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.spi;
+
+import java.util.ArrayDeque;
+import java.util.HashMap;
+import java.util.Map;
+
+import org.jboss.logging.Logger;
+import org.jboss.logging.MDC;
+
+import org.hibernate.HibernateException;
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan2.build.internal.QuerySpacesImpl;
+import org.hibernate.loader.plan2.internal.CollectionReturnImpl;
+import org.hibernate.loader.plan2.internal.EntityReturnImpl;
+import org.hibernate.loader.plan2.spi.CollectionFetch;
+import org.hibernate.loader.plan2.spi.CollectionFetchableElement;
+import org.hibernate.loader.plan2.spi.CollectionFetchableIndex;
+import org.hibernate.loader.plan2.spi.CollectionReference;
+import org.hibernate.loader.plan2.spi.CollectionReturn;
+import org.hibernate.loader.plan2.spi.CompositeFetch;
+import org.hibernate.loader.plan2.spi.EntityFetch;
+import org.hibernate.loader.plan2.spi.EntityReference;
+import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan2.spi.Return;
+import org.hibernate.persister.walking.spi.AnyMappingDefinition;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AssociationKey;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.CollectionDefinition;
+import org.hibernate.persister.walking.spi.CollectionElementDefinition;
+import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
+import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.persister.walking.spi.EntityDefinition;
+import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
+import org.hibernate.persister.walking.spi.WalkingException;
+import org.hibernate.type.Type;
+
+/**
+ * A LoadPlanBuilderStrategy is a strategy for building a LoadPlan.  LoadPlanBuilderStrategy is also a
+ * AssociationVisitationStrategy, which is used in conjunction with visiting associations via walking
+ * metamodel definitions.
+ * <p/>
+ * So this strategy defines a AssociationVisitationStrategy that walks the metamodel defined associations after
+ * which is can then build a LoadPlan based on the visited associations.  {@link #determineFetchStrategy} Is the
+ * main decision point
+ *
+ * @author Steve Ebersole
+ *
+ * @see org.hibernate.loader.plan.spi.build.LoadPlanBuilderStrategy
+ * @see org.hibernate.persister.walking.spi.AssociationVisitationStrategy
+ */
+public abstract class AbstractMetamodelWalkingLoadPlanBuilderStrategy implements LoadPlanBuilderStrategy, LoadPlanBuildingContext {
+	private static final Logger log = Logger.getLogger( AbstractMetamodelWalkingLoadPlanBuilderStrategy.class );
+	private static final String MDC_KEY = "hibernateLoadPlanWalkPath";
+
+	private final SessionFactoryImplementor sessionFactory;
+	private final QuerySpacesImpl querySpaces;
+
+	private final ArrayDeque<ExpandingFetchSource> fetchSourceStack = new ArrayDeque<ExpandingFetchSource>();
+
+	protected AbstractMetamodelWalkingLoadPlanBuilderStrategy(SessionFactoryImplementor sessionFactory) {
+		this.sessionFactory = sessionFactory;
+		this.querySpaces = new QuerySpacesImpl( sessionFactory );
+	}
+
+	public SessionFactoryImplementor sessionFactory() {
+		return sessionFactory;
+	}
+
+	@Override
+	public ExpandingQuerySpaces getQuerySpaces() {
+		return querySpaces;
+	}
+
+
+	// stack management ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public static interface FetchStackAware {
+		public void poppedFromStack();
+	}
+
+	private void pushToStack(ExpandingFetchSource fetchSource) {
+		log.trace( "Pushing fetch source to stack : " + fetchSource );
+		mdcStack().push( fetchSource.getPropertyPath() );
+		fetchSourceStack.addFirst( fetchSource );
+	}
+
+	private MDCStack mdcStack() {
+		return (MDCStack) MDC.get( MDC_KEY );
+	}
+
+	private ExpandingFetchSource popFromStack() {
+		final ExpandingFetchSource last = fetchSourceStack.removeFirst();
+		log.trace( "Popped fetch owner from stack : " + last );
+		mdcStack().pop();
+		if ( FetchStackAware.class.isInstance( last ) ) {
+			( (FetchStackAware) last ).poppedFromStack();
+		}
+		return last;
+	}
+
+	private ExpandingFetchSource currentSource() {
+		return fetchSourceStack.peekFirst();
+	}
+
+
+	// top-level AssociationVisitationStrategy hooks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public void start() {
+		if ( ! fetchSourceStack.isEmpty() ) {
+			throw new WalkingException(
+					"Fetch owner stack was not empty on start; " +
+							"be sure to not use LoadPlanBuilderStrategy instances concurrently"
+			);
+		}
+		MDC.put( MDC_KEY, new MDCStack() );
+	}
+
+	@Override
+	public void finish() {
+		MDC.remove( MDC_KEY );
+		fetchSourceStack.clear();
+	}
+
+
+	protected abstract void addRootReturn(Return rootReturn);
+
+
+	// Entity-level AssociationVisitationStrategy hooks ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	protected boolean supportsRootEntityReturns() {
+		return true;
+	}
+
+	@Override
+	public void startingEntity(EntityDefinition entityDefinition) {
+		log.tracef(
+				"%s Starting entity : %s",
+				StringHelper.repeat( ">>", fetchSourceStack.size() ),
+				entityDefinition.getEntityPersister().getEntityName()
+		);
+
+		// see if the EntityDefinition is a root...
+		final boolean isRoot = fetchSourceStack.isEmpty();
+		if ( ! isRoot ) {
+			// if not, this call should represent a fetch which should have been handled in #startingAttribute
+			return;
+		}
+
+		// if we get here, it is a root
+		if ( !supportsRootEntityReturns() ) {
+			throw new HibernateException( "This strategy does not support root entity returns" );
+		}
+
+		final EntityReturnImpl entityReturn = new EntityReturnImpl( entityDefinition, this );
+		addRootReturn( entityReturn );
+		pushToStack( entityReturn );
+	}
+
+
+	@Override
+	public void finishingEntity(EntityDefinition entityDefinition) {
+		// pop the current fetch owner, and make sure what we just popped represents this entity
+		final ExpandingFetchSource fetchSource = popFromStack();
+
+		if ( ! EntityReference.class.isInstance( fetchSource ) ) {
+			throw new WalkingException( "Mismatched FetchSource from stack on pop" );
+		}
+
+		final EntityReference entityReference = (EntityReference) fetchSource;
+		// NOTE : this is not the most exhaustive of checks because of hierarchical associations (employee/manager)
+		if ( ! entityReference.getEntityPersister().equals( entityDefinition.getEntityPersister() ) ) {
+			throw new WalkingException( "Mismatched FetchSource from stack on pop" );
+		}
+
+		log.tracef(
+				"%s Finished entity : %s",
+				StringHelper.repeat( "<<", fetchSourceStack.size() ),
+				entityDefinition.getEntityPersister().getEntityName()
+		);
+	}
+
+	@Override
+	public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
+		log.tracef(
+				"%s Starting entity identifier : %s",
+				StringHelper.repeat( ">>", fetchSourceStack.size() ),
+				entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
+		);
+
+		final EntityReference entityReference = (EntityReference) currentSource();
+
+		// perform some stack validation
+		if ( ! entityReference.getEntityPersister().equals( entityIdentifierDefinition.getEntityDefinition().getEntityPersister() ) ) {
+			throw new WalkingException(
+					String.format(
+							"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
+							entityReference.getEntityPersister().getEntityName(),
+							entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
+					)
+			);
+		}
+
+		pushToStack( (ExpandingEntityIdentifierDescription) entityReference.getIdentifierDescription() );
+	}
+
+	@Override
+	public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
+		final ExpandingFetchSource popped = popFromStack();
+
+		// perform some stack validation on exit, first on the current stack element we want to pop
+		if ( ! ExpandingEntityIdentifierDescription.class.isInstance( popped ) ) {
+			throw new WalkingException( "Unexpected state in ProcessNode stack" );
+		}
+
+		final ExpandingEntityIdentifierDescription identifierDescription = (ExpandingEntityIdentifierDescription) popped;
+
+		// and then on the node before it (which should be the entity that owns the identifier being described)
+		final ExpandingFetchSource entitySource = currentSource();
+		if ( ! EntityReference.class.isInstance( entitySource ) ) {
+			throw new WalkingException( "Unexpected state in ProcessNode stack" );
+		}
+		final EntityReference entityReference = (EntityReference) entitySource;
+		if ( entityReference.getIdentifierDescription() != identifierDescription ) {
+			throw new WalkingException(
+					String.format(
+							"Encountered unexpected fetch owner [%s] in stack while processing entity identifier for [%s]",
+							entityReference.getEntityPersister().getEntityName(),
+							entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
+					)
+			);
+		}
+
+		log.tracef(
+				"%s Finished entity identifier : %s",
+				StringHelper.repeat( "<<", fetchSourceStack.size() ),
+				entityIdentifierDefinition.getEntityDefinition().getEntityPersister().getEntityName()
+		);
+	}
+
+
+	// Collections ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	private ArrayDeque<CollectionReference> collectionReferenceStack = new ArrayDeque<CollectionReference>();
+
+	private void pushToCollectionStack(CollectionReference collectionReference) {
+		log.trace( "Pushing collection reference to stack : " + collectionReference );
+		mdcStack().push( collectionReference.getPropertyPath() );
+		collectionReferenceStack.addFirst( collectionReference );
+	}
+
+	private CollectionReference popFromCollectionStack() {
+		final CollectionReference last = collectionReferenceStack.removeFirst();
+		log.trace( "Popped collection reference from stack : " + last );
+		mdcStack().pop();
+		if ( FetchStackAware.class.isInstance( last ) ) {
+			( (FetchStackAware) last ).poppedFromStack();
+		}
+		return last;
+	}
+
+	@Override
+	public void startingCollection(CollectionDefinition collectionDefinition) {
+		log.tracef(
+				"%s Starting collection : %s",
+				StringHelper.repeat( ">>", fetchSourceStack.size() ),
+				collectionDefinition.getCollectionPersister().getRole()
+		);
+
+		// see if the EntityDefinition is a root...
+		final boolean isRoot = fetchSourceStack.isEmpty();
+		if ( ! isRoot ) {
+			// if not, this call should represent a fetch which should have been handled in #startingAttribute
+			return;
+		}
+
+		// if we get here, it is a root
+		if ( ! supportsRootCollectionReturns() ) {
+			throw new HibernateException( "This strategy does not support root collection returns" );
+		}
+
+		final CollectionReturn collectionReturn = new CollectionReturnImpl( collectionDefinition, this );
+		collectionReferenceStack.addFirst( collectionReturn );
+		addRootReturn( collectionReturn );
+	}
+
+	protected boolean supportsRootCollectionReturns() {
+		return true;
+	}
+
+	@Override
+	public void finishingCollection(CollectionDefinition collectionDefinition) {
+		// pop the current fetch owner, and make sure what we just popped represents this collection
+		final CollectionReference collectionReference = popFromCollectionStack();
+		if ( ! collectionReference.getCollectionPersister().equals( collectionDefinition.getCollectionPersister() ) ) {
+			throw new WalkingException( "Mismatched FetchSource from stack on pop" );
+		}
+
+		log.tracef(
+				"%s Finished collection : %s",
+				StringHelper.repeat( "<<", fetchSourceStack.size() ),
+				collectionDefinition.getCollectionPersister().getRole()
+		);
+	}
+
+	@Override
+	public void startingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
+		final Type indexType = collectionIndexDefinition.getType();
+		if ( indexType.isAssociationType() || indexType.isComponentType() ) {
+			final CollectionReference collectionReference = collectionReferenceStack.peekFirst();
+			final CollectionFetchableIndex indexGraph = collectionReference.getIndexGraph();
+			if ( indexGraph == null ) {
+				throw new WalkingException( "Collection reference did not return index handler" );
+			}
+			pushToStack( (ExpandingFetchSource) indexGraph );
+		}
+	}
+
+	@Override
+	public void finishingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
+		// nothing to do here
+		// 	- the element graph pushed while starting would be popped in finishingEntity/finishingComposite
+	}
+
+	@Override
+	public void startingCollectionElements(CollectionElementDefinition elementDefinition) {
+		if ( elementDefinition.getType().isAssociationType() || elementDefinition.getType().isComponentType() ) {
+			final CollectionReference collectionReference = collectionReferenceStack.peekFirst();
+			final CollectionFetchableElement elementGraph = collectionReference.getElementGraph();
+			if ( elementGraph == null ) {
+				throw new WalkingException( "Collection reference did not return element handler" );
+			}
+			pushToStack( (ExpandingFetchSource) elementGraph );
+		}
+	}
+
+	@Override
+	public void finishingCollectionElements(CollectionElementDefinition elementDefinition) {
+		// nothing to do here
+		// 	- the element graph pushed while starting would be popped in finishing/Entity/finishingComposite
+	}
+
+	@Override
+	public void startingCompositeCollectionElement(CompositeCollectionElementDefinition compositeElementDefinition) {
+		log.tracef(
+				"%s Starting composite collection element for (%s)",
+				StringHelper.repeat( ">>", fetchSourceStack.size() ),
+				compositeElementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
+		);
+	}
+
+	@Override
+	public void finishingCompositeCollectionElement(CompositeCollectionElementDefinition compositeElementDefinition) {
+		// pop the current fetch owner, and make sure what we just popped represents this composition
+		final ExpandingFetchSource popped = popFromStack();
+
+		if ( ! CollectionFetchableElement.class.isInstance( popped ) ) {
+			throw new WalkingException( "Mismatched FetchSource from stack on pop" );
+		}
+
+		// NOTE : not much else we can really check here atm since on the walking spi side we do not have path
+
+		log.tracef(
+				"%s Finished composite element for  : %s",
+				StringHelper.repeat( "<<", fetchSourceStack.size() ),
+				compositeElementDefinition.getCollectionDefinition().getCollectionPersister().getRole()
+		);
+	}
+
+	@Override
+	public void startingComposite(CompositionDefinition compositionDefinition) {
+		log.tracef(
+				"%s Starting composition : %s",
+				StringHelper.repeat( ">>", fetchSourceStack.size() ),
+				compositionDefinition.getName()
+		);
+
+		if ( fetchSourceStack.isEmpty() ) {
+			throw new HibernateException( "A component cannot be the root of a walk nor a graph" );
+		}
+	}
+
+	@Override
+	public void finishingComposite(CompositionDefinition compositionDefinition) {
+		// pop the current fetch owner, and make sure what we just popped represents this composition
+		final ExpandingFetchSource popped = popFromStack();
+
+		if ( ! CompositeFetch.class.isInstance( popped ) ) {
+			throw new WalkingException( "Mismatched FetchSource from stack on pop" );
+		}
+
+		// NOTE : not much else we can really check here atm since on the walking spi side we do not have path
+
+		log.tracef(
+				"%s Finished composition : %s",
+				StringHelper.repeat( "<<", fetchSourceStack.size() ),
+				compositionDefinition.getName()
+		);
+	}
+
+	@Override
+	public boolean startingAttribute(AttributeDefinition attributeDefinition) {
+		log.tracef(
+				"%s Starting attribute %s",
+				StringHelper.repeat( ">>", fetchSourceStack.size() ),
+				attributeDefinition
+		);
+
+		final Type attributeType = attributeDefinition.getType();
+
+		final boolean isComponentType = attributeType.isComponentType();
+		final boolean isAssociationType = attributeType.isAssociationType();
+		final boolean isBasicType = ! ( isComponentType || isAssociationType );
+
+		if ( isBasicType ) {
+			return true;
+		}
+		else if ( isAssociationType ) {
+			return handleAssociationAttribute( (AssociationAttributeDefinition) attributeDefinition );
+		}
+		else {
+			return handleCompositeAttribute( (CompositionDefinition) attributeDefinition );
+		}
+	}
+
+	@Override
+	public void finishingAttribute(AttributeDefinition attributeDefinition) {
+		log.tracef(
+				"%s Finishing up attribute : %s",
+				StringHelper.repeat( "<<", fetchSourceStack.size() ),
+				attributeDefinition
+		);
+	}
+
+	private Map<AssociationKey,FetchSource> fetchedAssociationKeyOwnerMap = new HashMap<AssociationKey, FetchSource>();
+
+	@Override
+	public void associationKeyRegistered(AssociationKey associationKey) {
+		// todo : use this information to maintain a map of AssociationKey->FetchOwner mappings (associationKey + current fetchOwner stack entry)
+		//		that mapping can then be used in #foundCircularAssociationKey to build the proper BiDirectionalEntityFetch
+		//		based on the mapped owner
+		fetchedAssociationKeyOwnerMap.put( associationKey, currentSource() );
+	}
+//
+//	@Override
+//	public void foundCircularAssociationKey(AssociationKey associationKey, AttributeDefinition attributeDefinition) {
+//		// todo : use this information to create the BiDirectionalEntityFetch instances
+//		final FetchOwner fetchOwner = fetchedAssociationKeyOwnerMap.get( associationKey );
+//		if ( fetchOwner == null ) {
+//			throw new IllegalStateException(
+//					String.format(
+//							"Expecting AssociationKey->FetchOwner mapping for %s",
+//							associationKey.toString()
+//					)
+//			);
+//		}
+//
+//		currentFetchOwner().addFetch( new CircularFetch( currentFetchOwner(), fetchOwner, attributeDefinition ) );
+//	}
+//
+//	public static class CircularFetch implements BidirectionalEntityFetch, EntityReference, Fetch {
+//		private final FetchOwner circularFetchOwner;
+//		private final FetchOwner associationOwner;
+//		private final AttributeDefinition attributeDefinition;
+//
+//		private final EntityReference targetEntityReference;
+//
+//		private final FetchStrategy fetchStrategy = new FetchStrategy(
+//				FetchTiming.IMMEDIATE,
+//				FetchStyle.JOIN
+//		);
+//
+//		public CircularFetch(FetchOwner circularFetchOwner, FetchOwner associationOwner, AttributeDefinition attributeDefinition) {
+//			this.circularFetchOwner = circularFetchOwner;
+//			this.associationOwner = associationOwner;
+//			this.attributeDefinition = attributeDefinition;
+//			this.targetEntityReference = resolveEntityReference( associationOwner );
+//		}
+//
+//		@Override
+//		public EntityReference getTargetEntityReference() {
+//			return targetEntityReference;
+//		}
+//
+//		protected static EntityReference resolveEntityReference(FetchOwner owner) {
+//			if ( EntityReference.class.isInstance( owner ) ) {
+//				return (EntityReference) owner;
+//			}
+//			if ( CompositeFetch.class.isInstance( owner ) ) {
+//				return resolveEntityReference( ( (CompositeFetch) owner ).getOwner() );
+//			}
+//			// todo : what others?
+//
+//			throw new UnsupportedOperationException(
+//					"Unexpected FetchOwner type [" + owner + "] encountered trying to build circular fetch"
+//			);
+//
+//		}
+//
+//		@Override
+//		public FetchOwner getSource() {
+//			return circularFetchOwner;
+//		}
+//
+//		@Override
+//		public PropertyPath getPropertyPath() {
+//			return null;  //To change body of implemented methods use File | Settings | File Templates.
+//		}
+//
+//		@Override
+//		public Type getFetchedType() {
+//			return attributeDefinition.getType();
+//		}
+//
+//		@Override
+//		public FetchStrategy getFetchStrategy() {
+//			return fetchStrategy;
+//		}
+//
+//		@Override
+//		public boolean isNullable() {
+//			return attributeDefinition.isNullable();
+//		}
+//
+//		@Override
+//		public String getAdditionalJoinConditions() {
+//			return null;
+//		}
+//
+//		@Override
+//		public String[] toSqlSelectFragments(String alias) {
+//			return new String[0];
+//		}
+//
+//		@Override
+//		public Fetch makeCopy(CopyContext copyContext, FetchOwner fetchOwnerCopy) {
+//			// todo : will need this implemented
+//			return null;
+//		}
+//
+//		@Override
+//		public LockMode getLockMode() {
+//			return targetEntityReference.getLockMode();
+//		}
+//
+//		@Override
+//		public EntityReference getEntityReference() {
+//			return targetEntityReference;
+//		}
+//
+//		@Override
+//		public EntityPersister getEntityPersister() {
+//			return targetEntityReference.getEntityPersister();
+//		}
+//
+//		@Override
+//		public IdentifierDescription getIdentifierDescription() {
+//			return targetEntityReference.getIdentifierDescription();
+//		}
+//
+//		@Override
+//		public void injectIdentifierDescription(IdentifierDescription identifierDescription) {
+//			throw new IllegalStateException( "IdentifierDescription should never be injected from circular fetch side" );
+//		}
+//	}
+
+	@Override
+	public void foundAny(AssociationAttributeDefinition attributeDefinition, AnyMappingDefinition anyDefinition) {
+		// for ANY mappings we need to build a Fetch:
+		//		1) fetch type is SELECT, timing might be IMMEDIATE or DELAYED depending on whether it was defined as lazy
+		//		2) (because the fetch cannot be a JOIN...) do not push it to the stack
+		final FetchStrategy fetchStrategy = determineFetchStrategy( attributeDefinition );
+
+//		final FetchOwner fetchOwner = currentFetchOwner();
+//		fetchOwner.validateFetchPlan( fetchStrategy, attributeDefinition );
+//
+//		fetchOwner.buildAnyFetch(
+//				attributeDefinition,
+//				anyDefinition,
+//				fetchStrategy,
+//				this
+//		);
+	}
+
+	protected boolean handleCompositeAttribute(CompositionDefinition attributeDefinition) {
+		final ExpandingFetchSource currentSource = currentSource();
+		final CompositeFetch fetch = currentSource.buildCompositeFetch( attributeDefinition, this );
+		pushToStack( (ExpandingFetchSource) fetch );
+		return true;
+	}
+
+	protected boolean handleAssociationAttribute(AssociationAttributeDefinition attributeDefinition) {
+		// todo : this seems to not be correct for one-to-one
+		final FetchStrategy fetchStrategy = determineFetchStrategy( attributeDefinition );
+		if ( fetchStrategy.getStyle() != FetchStyle.JOIN ) {
+			return false;
+		}
+//		if ( fetchStrategy.getTiming() != FetchTiming.IMMEDIATE ) {
+//			return false;
+//		}
+
+		final ExpandingFetchSource currentSource = currentSource();
+		currentSource.validateFetchPlan( fetchStrategy, attributeDefinition );
+
+		final AssociationAttributeDefinition.AssociationNature nature = attributeDefinition.getAssociationNature();
+		if ( nature == AssociationAttributeDefinition.AssociationNature.ANY ) {
+			return false;
+		}
+
+		if ( nature == AssociationAttributeDefinition.AssociationNature.ENTITY ) {
+			EntityFetch fetch = currentSource.buildEntityFetch(
+					attributeDefinition,
+					fetchStrategy,
+					this
+			);
+			pushToStack( (ExpandingFetchSource) fetch );
+		}
+		else {
+			// Collection
+			CollectionFetch fetch = currentSource.buildCollectionFetch( attributeDefinition, fetchStrategy, this );
+			pushToCollectionStack( fetch );
+		}
+
+		return true;
+	}
+
+	protected abstract FetchStrategy determineFetchStrategy(AssociationAttributeDefinition attributeDefinition);
+
+	protected int currentDepth() {
+		return fetchSourceStack.size();
+	}
+
+	protected boolean isTooManyCollections() {
+		return false;
+	}
+
+//	protected abstract EntityReturn buildRootEntityReturn(EntityDefinition entityDefinition);
+//
+//	protected abstract CollectionReturn buildRootCollectionReturn(CollectionDefinition collectionDefinition);
+
+
+
+	// LoadPlanBuildingContext impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	@Override
+	public SessionFactoryImplementor getSessionFactory() {
+		return sessionFactory();
+	}
+
+	/**
+	 * Used as the MDC object for logging purposes.  Because of the recursive calls it is often useful (while debugging)
+	 * to be able to see the "property path" as part of the logging output.  This class helps fulfill that role
+	 * here by acting as the object that gets put into the logging libraries underlying MDC.
+	 */
+	public static class MDCStack {
+		private ArrayDeque<PropertyPath> pathStack = new ArrayDeque<PropertyPath>();
+
+		public void push(PropertyPath path) {
+			pathStack.addFirst( path );
+		}
+
+		public void pop() {
+			pathStack.removeFirst();
+		}
+
+		public String toString() {
+			final PropertyPath path = pathStack.peekFirst();
+			return path == null ? "<no-path>" : path.getFullPath();
+		}
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/AbstractQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/AbstractQuerySpace.java
new file mode 100644
index 0000000000..b0110e7cb1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/AbstractQuerySpace.java
@@ -0,0 +1,95 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.spi;
+
+import java.util.ArrayList;
+import java.util.Collections;
+import java.util.List;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.plan.spi.AbstractPlanNode;
+import org.hibernate.loader.plan2.build.internal.QuerySpacesImpl;
+import org.hibernate.loader.plan2.spi.Join;
+import org.hibernate.loader.plan2.spi.QuerySpace;
+
+/**
+ * Convenience base class for QuerySpace implementations.
+ *
+ * @author Steve Ebersole
+ */
+public abstract class AbstractQuerySpace extends AbstractPlanNode implements QuerySpace {
+	private final String uid;
+	private final Disposition disposition;
+	private final QuerySpacesImpl querySpaces;
+
+	private List<Join> joins;
+
+	public AbstractQuerySpace(
+			String uid,
+			Disposition disposition,
+			QuerySpacesImpl querySpaces,
+			SessionFactoryImplementor sessionFactory) {
+		super( sessionFactory );
+		this.uid = uid;
+		this.disposition = disposition;
+		this.querySpaces = querySpaces;
+	}
+
+	// todo : copy ctor - that depends how graph copying works here...
+
+
+	/**
+	 * Provides subclasses access to the spaces to which this space belongs.
+	 *
+	 * @return The query spaces
+	 */
+	protected QuerySpacesImpl getQuerySpaces() {
+		return querySpaces;
+	}
+
+	@Override
+	public String getUid() {
+		return uid;
+	}
+
+	@Override
+	public Disposition getDisposition() {
+		return disposition;
+	}
+
+	@Override
+	public Iterable<Join> getJoins() {
+		return joins == null
+				? Collections.<Join>emptyList()
+				: joins;
+	}
+
+	protected List<Join> internalGetJoins() {
+		if ( joins == null ) {
+			joins = new ArrayList<Join>();
+		}
+
+		return joins;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingEntityIdentifierDescription.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingEntityIdentifierDescription.java
new file mode 100644
index 0000000000..d727b4545b
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingEntityIdentifierDescription.java
@@ -0,0 +1,33 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.spi;
+
+import org.hibernate.loader.plan2.spi.EntityIdentifierDescription;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface ExpandingEntityIdentifierDescription extends EntityIdentifierDescription, ExpandingFetchSource {
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingFetchSource.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingFetchSource.java
new file mode 100644
index 0000000000..5dbdadd3c6
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingFetchSource.java
@@ -0,0 +1,64 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.spi;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.loader.plan2.spi.CollectionFetch;
+import org.hibernate.loader.plan2.spi.CompositeFetch;
+import org.hibernate.loader.plan2.spi.EntityFetch;
+import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+
+/**
+ * Describes the internal contract for things which can contain fetches.  Used to request building
+ * the different types of fetches.
+ *
+ * @author Steve Ebersole
+ */
+public interface ExpandingFetchSource extends FetchSource {
+	/**
+	 * Is the asserted plan valid from this owner to a fetch?
+	 *
+	 * @param fetchStrategy The type of fetch to validate
+	 * @param attributeDefinition The attribute to be fetched
+	 */
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition);
+
+	public EntityFetch buildEntityFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext);
+
+	public CompositeFetch buildCompositeFetch(
+			CompositionDefinition attributeDefinition,
+			LoadPlanBuildingContext loadPlanBuildingContext);
+
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext);
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingQuerySpace.java
new file mode 100644
index 0000000000..3f1b3be45d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingQuerySpace.java
@@ -0,0 +1,50 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.spi;
+
+import org.hibernate.loader.plan2.spi.JoinDefinedByMetadata;
+import org.hibernate.loader.plan2.spi.QuerySpace;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface ExpandingQuerySpace extends QuerySpace {
+
+	public JoinDefinedByMetadata addCompositeJoin(CompositionDefinition compositionDefinition, String querySpaceUid);
+
+	public JoinDefinedByMetadata addEntityJoin(
+			AttributeDefinition attributeDefinition,
+			EntityPersister persister,
+			String querySpaceUid,
+			boolean optional);
+
+	public JoinDefinedByMetadata addCollectionJoin(
+			AttributeDefinition attributeDefinition,
+			CollectionPersister collectionPersister,
+			String querySpaceUid);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingQuerySpaces.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingQuerySpaces.java
new file mode 100644
index 0000000000..0a1d74a368
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/ExpandingQuerySpaces.java
@@ -0,0 +1,41 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.spi;
+
+import org.hibernate.loader.plan2.build.internal.CollectionQuerySpaceImpl;
+import org.hibernate.loader.plan2.spi.EntityQuerySpace;
+import org.hibernate.loader.plan2.spi.QuerySpaces;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface ExpandingQuerySpaces extends QuerySpaces {
+	public String generateImplicitUid();
+
+	public EntityQuerySpace makeEntityQuerySpace(String uid, EntityPersister entityPersister);
+
+	public CollectionQuerySpaceImpl makeCollectionQuerySpace(String uid, CollectionPersister collectionPersister);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanBuilderStrategy.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanBuilderStrategy.java
new file mode 100644
index 0000000000..ec0463342b
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanBuilderStrategy.java
@@ -0,0 +1,42 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.spi;
+
+import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.persister.walking.spi.AssociationVisitationStrategy;
+
+/**
+ * Specialized {@link org.hibernate.persister.walking.spi.AssociationVisitationStrategy} implementation for
+ * building {@link org.hibernate.loader.plan.spi.LoadPlan} instances.
+ *
+ * @author Steve Ebersole
+ */
+public interface LoadPlanBuilderStrategy extends AssociationVisitationStrategy {
+	/**
+	 * After visitation is done, build the load plan.
+	 *
+	 * @return The load plan
+	 */
+	public LoadPlan buildLoadPlan();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanBuildingContext.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanBuildingContext.java
new file mode 100644
index 0000000000..c4a1fa6a2e
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/LoadPlanBuildingContext.java
@@ -0,0 +1,43 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.spi;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.plan2.spi.QuerySpaces;
+
+/**
+ * Provides access to context needed in building a LoadPlan.
+ *
+ * @author Steve Ebersole
+ */
+public interface LoadPlanBuildingContext {
+	/**
+	 * Access to the SessionFactory
+	 *
+	 * @return The SessionFactory
+	 */
+	public SessionFactoryImplementor getSessionFactory();
+
+	public ExpandingQuerySpaces getQuerySpaces();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/MetadataDrivenLoadPlanBuilder.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/MetadataDrivenLoadPlanBuilder.java
new file mode 100644
index 0000000000..f3b6474fbe
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/MetadataDrivenLoadPlanBuilder.java
@@ -0,0 +1,67 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.build.spi;
+
+import org.hibernate.loader.plan2.spi.LoadPlan;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.MetadataDrivenModelGraphVisitor;
+
+/**
+ * A metadata-driven builder of LoadPlans.  Coordinates between the {@link org.hibernate.persister.walking.spi.MetadataDrivenModelGraphVisitor} and
+ * {@link LoadPlanBuilderStrategy}.
+ *
+ * @author Steve Ebersole
+ *
+ * @see org.hibernate.persister.walking.spi.MetadataDrivenModelGraphVisitor
+ */
+public class MetadataDrivenLoadPlanBuilder {
+	/**
+	 * Coordinates building a LoadPlan that defines just a single root entity return (may have fetches).
+	 * <p/>
+	 * Typically this includes building load plans for entity loading or cascade loading.
+	 *
+	 * @param strategy The strategy defining the load plan shaping
+	 * @param persister The persister for the entity forming the root of the load plan.
+	 *
+	 * @return The built load plan.
+	 */
+	public static LoadPlan buildRootEntityLoadPlan(LoadPlanBuilderStrategy strategy, EntityPersister persister) {
+		MetadataDrivenModelGraphVisitor.visitEntity( strategy, persister );
+		return strategy.buildLoadPlan();
+	}
+
+	/**
+	 * Coordinates building a LoadPlan that defines just a single root collection return (may have fetches).
+	 *
+	 * @param strategy The strategy defining the load plan shaping
+	 * @param persister The persister for the collection forming the root of the load plan.
+	 *
+	 * @return The built load plan.
+	 */
+	public static LoadPlan buildRootCollectionLoadPlan(LoadPlanBuilderStrategy strategy, CollectionPersister persister) {
+		MetadataDrivenModelGraphVisitor.visitCollection( strategy, persister );
+		return strategy.buildLoadPlan();
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/package-info.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/package-info.java
new file mode 100644
index 0000000000..dfb031e0a6
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/build/spi/package-info.java
@@ -0,0 +1,4 @@
+/**
+ * Defines the SPI for building a metamodel-driven LoadPlan
+ */
+package org.hibernate.loader.plan2.build.spi;
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/AbstractCollectionReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/AbstractCollectionReference.java
new file mode 100644
index 0000000000..95c64ea4bb
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/AbstractCollectionReference.java
@@ -0,0 +1,147 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.internal;
+
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan.spi.CompositeElementGraph;
+import org.hibernate.loader.plan.spi.CompositeIndexGraph;
+import org.hibernate.loader.plan2.build.internal.CollectionQuerySpaceImpl;
+import org.hibernate.loader.plan2.build.spi.LoadPlanBuildingContext;
+import org.hibernate.loader.plan2.spi.CollectionFetchableElement;
+import org.hibernate.loader.plan2.spi.CollectionFetchableIndex;
+import org.hibernate.loader.plan2.spi.CollectionReference;
+import org.hibernate.persister.collection.CollectionPersister;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.type.CompositeType;
+import org.hibernate.type.EntityType;
+import org.hibernate.type.Type;
+
+import static org.hibernate.loader.plan2.build.internal.CollectionQuerySpaceImpl.CollectionElementCompositeJoin;
+import static org.hibernate.loader.plan2.build.internal.CollectionQuerySpaceImpl.CollectionElementEntityJoin;
+import static org.hibernate.loader.plan2.build.internal.CollectionQuerySpaceImpl.CollectionIndexCompositeJoin;
+import static org.hibernate.loader.plan2.build.internal.CollectionQuerySpaceImpl.CollectionIndexEntityJoin;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractCollectionReference implements CollectionReference {
+	private final CollectionQuerySpaceImpl collectionQuerySpace;
+	private final PropertyPath propertyPath;
+
+	private final CollectionFetchableIndex index;
+	private final CollectionFetchableElement element;
+
+	protected AbstractCollectionReference(
+			CollectionQuerySpaceImpl collectionQuerySpace,
+			PropertyPath propertyPath,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		this.collectionQuerySpace = collectionQuerySpace;
+		this.propertyPath = propertyPath;
+
+		this.index = buildIndexGraph( collectionQuerySpace, loadPlanBuildingContext );
+		this.element = buildElementGraph( collectionQuerySpace, loadPlanBuildingContext );
+	}
+
+	private CollectionFetchableIndex buildIndexGraph(
+			CollectionQuerySpaceImpl collectionQuerySpace,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		final CollectionPersister persister = collectionQuerySpace.getCollectionPersister();
+		if ( persister.hasIndex() ) {
+			final Type type = persister.getIndexType();
+			if ( type.isAssociationType() ) {
+				if ( type.isEntityType() ) {
+					final EntityPersister indexPersister = persister.getFactory().getEntityPersister(
+							( (EntityType) type ).getAssociatedEntityName()
+					);
+
+					final CollectionIndexEntityJoin join = collectionQuerySpace.addIndexEntityJoin(
+							indexPersister,
+							loadPlanBuildingContext
+					);
+					return new CollectionFetchableIndexEntityGraph( this, indexPersister, join );
+				}
+			}
+			else if ( type.isComponentType() ) {
+				final CollectionIndexCompositeJoin join = collectionQuerySpace.addIndexCompositeJoin(
+						(CompositeType) type,
+						loadPlanBuildingContext
+				);
+				return new CollectionFetchableIndexCompositeGraph( this, join );
+			}
+		}
+
+		return null;
+	}
+
+	private CollectionFetchableElement buildElementGraph(
+			CollectionQuerySpaceImpl collectionQuerySpace,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		final CollectionPersister persister = collectionQuerySpace.getCollectionPersister();
+		final Type type = persister.getElementType();
+		if ( type.isAssociationType() ) {
+			if ( type.isEntityType() ) {
+				final EntityPersister indexPersister = persister.getFactory().getEntityPersister(
+						( (EntityType) type ).getAssociatedEntityName()
+				);
+
+				final CollectionElementEntityJoin join = collectionQuerySpace.addElementEntityJoin( indexPersister, loadPlanBuildingContext );
+				return new CollectionFetchableElementEntityGraph( this, indexPersister, join );
+			}
+		}
+		else if ( type.isComponentType() ) {
+			final CollectionElementCompositeJoin join = collectionQuerySpace.addElementCompositeJoin(
+					(CompositeType) type,
+					loadPlanBuildingContext
+			);
+			return new CollectionFetchableElementCompositeGraph( this, join );
+		}
+
+		return null;
+	}
+
+	@Override
+	public String getQuerySpaceUid() {
+		return collectionQuerySpace.getUid();
+	}
+
+	@Override
+	public CollectionPersister getCollectionPersister() {
+		return collectionQuerySpace.getCollectionPersister();
+	}
+
+	@Override
+	public CollectionFetchableIndex getIndexGraph() {
+		return index;
+	}
+
+	@Override
+	public CollectionFetchableElement getElementGraph() {
+		return element;
+	}
+
+	@Override
+	public PropertyPath getPropertyPath() {
+		return propertyPath;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/AbstractCompositeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/AbstractCompositeFetch.java
new file mode 100644
index 0000000000..b29e67cace
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/AbstractCompositeFetch.java
@@ -0,0 +1,126 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.internal;
+
+import java.util.List;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.engine.FetchTiming;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan2.build.spi.ExpandingFetchSource;
+import org.hibernate.loader.plan2.build.spi.LoadPlanBuildingContext;
+import org.hibernate.loader.plan2.spi.CollectionFetch;
+import org.hibernate.loader.plan2.spi.CompositeFetch;
+import org.hibernate.loader.plan2.spi.EntityFetch;
+import org.hibernate.loader.plan2.spi.Fetch;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.type.CompositeType;
+import org.hibernate.type.Type;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractCompositeFetch implements CompositeFetch, ExpandingFetchSource {
+	private static final FetchStrategy FETCH_STRATEGY = new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
+
+	private final CompositeType compositeType;
+	private final PropertyPath propertyPath;
+
+	private List<Fetch> fetches;
+
+	protected AbstractCompositeFetch(CompositeType compositeType, PropertyPath propertyPath) {
+		this.compositeType = compositeType;
+		this.propertyPath = propertyPath;
+	}
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
+		// anything to do here?
+	}
+
+	@Override
+	public EntityFetch buildEntityFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		// todo : implement
+		return null;
+	}
+
+	@Override
+	public CompositeFetch buildCompositeFetch(
+			CompositionDefinition attributeDefinition, LoadPlanBuildingContext loadPlanBuildingContext) {
+		// todo : implement
+		return null;
+	}
+
+	@Override
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		// todo : implement
+		return null;
+	}
+
+	@Override
+	public PropertyPath getPropertyPath() {
+		return propertyPath;
+	}
+
+	@Override
+	public FetchStrategy getFetchStrategy() {
+		return FETCH_STRATEGY;
+	}
+
+	@Override
+	public Type getFetchedType() {
+		return compositeType;
+	}
+
+	@Override
+	public boolean isNullable() {
+		return true;
+	}
+
+	@Override
+	public String getAdditionalJoinConditions() {
+		return null;
+	}
+
+	@Override
+	public Fetch[] getFetches() {
+		return (fetches == null) ? NO_FETCHES : fetches.toArray( new Fetch[fetches.size()] );
+	}
+
+
+	// this is being removed to be more ogm/search friendly
+	@Override
+	public String[] toSqlSelectFragments(String alias) {
+		return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/AbstractEntityReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/AbstractEntityReference.java
new file mode 100644
index 0000000000..7022fc2373
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/AbstractEntityReference.java
@@ -0,0 +1,110 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.internal;
+
+import java.util.List;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan2.build.internal.EntityIdentifierDescriptionImpl;
+import org.hibernate.loader.plan2.build.spi.ExpandingEntityIdentifierDescription;
+import org.hibernate.loader.plan2.build.spi.ExpandingFetchSource;
+import org.hibernate.loader.plan2.build.spi.LoadPlanBuildingContext;
+import org.hibernate.loader.plan2.spi.CollectionFetch;
+import org.hibernate.loader.plan2.spi.CompositeFetch;
+import org.hibernate.loader.plan2.spi.EntityFetch;
+import org.hibernate.loader.plan2.spi.EntityQuerySpace;
+import org.hibernate.loader.plan2.spi.EntityReference;
+import org.hibernate.loader.plan2.spi.Fetch;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+
+/**
+ * @author Steve Ebersole
+ */
+public abstract class AbstractEntityReference implements EntityReference, ExpandingFetchSource {
+	private final EntityPersister entityPersister;
+	private final PropertyPath propertyPath;
+
+	private ExpandingEntityIdentifierDescription identifierDescription;
+
+	private List<Fetch> fetches;
+
+	public AbstractEntityReference(EntityPersister entityPersister, PropertyPath propertyPath) {
+		this.entityPersister = entityPersister;
+		this.propertyPath = propertyPath;
+
+		this.identifierDescription = new EntityIdentifierDescriptionImpl( this );
+	}
+
+	protected abstract EntityQuerySpace getEntityQuerySpace();
+
+	@Override
+	public String getQuerySpaceUid() {
+		return getEntityQuerySpace().getUid();
+	}
+
+	@Override
+	public EntityPersister getEntityPersister() {
+		return entityPersister;
+	}
+
+	@Override
+	public ExpandingEntityIdentifierDescription getIdentifierDescription() {
+		return identifierDescription;
+	}
+
+	@Override
+	public PropertyPath getPropertyPath() {
+		return propertyPath;
+	}
+
+	@Override
+	public Fetch[] getFetches() {
+		return fetches == null ? NO_FETCHES : fetches.toArray( new Fetch[ fetches.size() ] );
+	}
+
+	@Override
+	public EntityFetch buildEntityFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public CompositeFetch buildCompositeFetch(
+			CompositionDefinition attributeDefinition, LoadPlanBuildingContext loadPlanBuildingContext) {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		return null;  //To change body of implemented methods use File | Settings | File Templates.
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionFetchableElementCompositeGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionFetchableElementCompositeGraph.java
new file mode 100644
index 0000000000..f8824b2654
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionFetchableElementCompositeGraph.java
@@ -0,0 +1,77 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.internal;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.loader.plan2.build.spi.LoadPlanBuildingContext;
+import org.hibernate.loader.plan2.spi.CollectionFetch;
+import org.hibernate.loader.plan2.spi.CollectionFetchableElement;
+import org.hibernate.loader.plan2.spi.CollectionReference;
+import org.hibernate.loader.plan2.spi.CompositeFetch;
+import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.WalkingException;
+import org.hibernate.type.CompositeType;
+
+import static org.hibernate.loader.plan2.build.internal.CollectionQuerySpaceImpl.CollectionElementCompositeJoin;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CollectionFetchableElementCompositeGraph
+		extends AbstractCompositeFetch
+		implements CompositeFetch, CollectionFetchableElement {
+
+	private final CollectionReference collectionReference;
+	private final CollectionElementCompositeJoin compositeJoin;
+
+	public CollectionFetchableElementCompositeGraph(
+			CollectionReference collectionReference,
+			CollectionElementCompositeJoin compositeJoin) {
+		super(
+				(CompositeType) compositeJoin.getCollectionQuerySpace().getCollectionPersister().getIndexType(),
+				collectionReference.getPropertyPath().append( "<element>" )
+		);
+		this.collectionReference = collectionReference;
+		this.compositeJoin = compositeJoin;
+	}
+
+	@Override
+	public CollectionReference getCollectionReference() {
+		return collectionReference;
+	}
+
+	@Override
+	public FetchSource getSource() {
+		return collectionReference.getIndexGraph();
+	}
+
+	@Override
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		throw new WalkingException( "Encountered collection as part of fetched Collection composite-element" );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionFetchableElementEntityGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionFetchableElementEntityGraph.java
new file mode 100644
index 0000000000..ffa1876bf1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionFetchableElementEntityGraph.java
@@ -0,0 +1,65 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.internal;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.loader.plan2.spi.CollectionFetchableElement;
+import org.hibernate.loader.plan2.spi.CollectionReference;
+import org.hibernate.loader.plan2.spi.EntityQuerySpace;
+import org.hibernate.loader.plan2.spi.Join;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CollectionFetchableElementEntityGraph extends AbstractEntityReference implements CollectionFetchableElement {
+	private final CollectionReference collectionReference;
+	private final EntityQuerySpace entityQuerySpace;
+
+	public CollectionFetchableElementEntityGraph(
+			CollectionReference collectionReference,
+			EntityPersister elementPersister,
+			Join entityJoin) {
+		super( elementPersister, collectionReference.getPropertyPath().append( "<elements>" ) );
+
+		this.collectionReference = collectionReference;
+		this.entityQuerySpace = (EntityQuerySpace) entityJoin.getRightHandSide();
+	}
+
+	@Override
+	protected EntityQuerySpace getEntityQuerySpace() {
+		return entityQuerySpace;
+	}
+
+	@Override
+	public CollectionReference getCollectionReference() {
+		return collectionReference;
+	}
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
+		//To change body of implemented methods use File | Settings | File Templates.
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionFetchableIndexCompositeGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionFetchableIndexCompositeGraph.java
new file mode 100644
index 0000000000..9fd44f8f86
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionFetchableIndexCompositeGraph.java
@@ -0,0 +1,95 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.internal;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.engine.FetchStyle;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan2.build.internal.CollectionQuerySpaceImpl;
+import org.hibernate.loader.plan2.build.spi.LoadPlanBuildingContext;
+import org.hibernate.loader.plan2.spi.CollectionFetch;
+import org.hibernate.loader.plan2.spi.CompositeFetch;
+import org.hibernate.loader.plan2.spi.CollectionFetchableIndex;
+import org.hibernate.loader.plan2.spi.CollectionReference;
+import org.hibernate.loader.plan2.spi.Fetch;
+import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan2.spi.Join;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.WalkingException;
+import org.hibernate.type.CompositeType;
+import org.hibernate.type.Type;
+
+import static org.hibernate.loader.plan2.build.internal.CollectionQuerySpaceImpl.CollectionIndexCompositeJoin;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CollectionFetchableIndexCompositeGraph
+		extends AbstractCompositeFetch
+		implements CompositeFetch, CollectionFetchableIndex {
+
+	private final CollectionReference collectionReference;
+	private final CollectionIndexCompositeJoin compositeJoin;
+
+	public CollectionFetchableIndexCompositeGraph(
+			CollectionReference collectionReference,
+			CollectionIndexCompositeJoin compositeJoin) {
+		super(
+				(CompositeType) compositeJoin.getCollectionQuerySpace().getCollectionPersister().getIndexType(),
+				collectionReference.getPropertyPath().append( "<index>" )
+		);
+		this.collectionReference = collectionReference;
+		this.compositeJoin = compositeJoin;
+	}
+
+	@Override
+	public CollectionReference getCollectionReference() {
+		return collectionReference;
+	}
+
+	@Override
+	public FetchSource getSource() {
+		return collectionReference.getIndexGraph();
+	}
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
+		// metamodel should already disallow collections to be defined as part of a collection composite-index
+		// so, nothing to do here
+		super.validateFetchPlan( fetchStrategy, attributeDefinition );
+	}
+
+	// todo : override buildCompositeFetch as well to account for nested composites?
+	// 		the idea would be to find nested composites attempting to define a collection.  but again, the metramodel
+	//		is supposed to disallow that anyway.
+
+	@Override
+	public CollectionFetch buildCollectionFetch(
+			AssociationAttributeDefinition attributeDefinition,
+			FetchStrategy fetchStrategy,
+			LoadPlanBuildingContext loadPlanBuildingContext) {
+		throw new WalkingException( "Encountered collection as part of the Map composite-index" );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionFetchableIndexEntityGraph.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionFetchableIndexEntityGraph.java
new file mode 100644
index 0000000000..9fc04df7ee
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionFetchableIndexEntityGraph.java
@@ -0,0 +1,64 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.internal;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.loader.plan2.spi.CollectionFetchableIndex;
+import org.hibernate.loader.plan2.spi.CollectionReference;
+import org.hibernate.loader.plan2.spi.EntityQuerySpace;
+import org.hibernate.loader.plan2.spi.Join;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CollectionFetchableIndexEntityGraph extends AbstractEntityReference implements CollectionFetchableIndex {
+	private final CollectionReference collectionReference;
+	private final EntityQuerySpace entityQuerySpace;
+
+	public CollectionFetchableIndexEntityGraph(
+			CollectionReference collectionReference,
+			EntityPersister indexPersister,
+			Join entityJoin) {
+		super( indexPersister, collectionReference.getPropertyPath().append( "<index>" ) );
+
+		this.collectionReference = collectionReference;
+		this.entityQuerySpace = (EntityQuerySpace) entityJoin.getRightHandSide();
+	}
+
+	@Override
+	public CollectionReference getCollectionReference() {
+		return collectionReference;
+	}
+
+	@Override
+	protected EntityQuerySpace getEntityQuerySpace() {
+		return entityQuerySpace;
+	}
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionReturnImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionReturnImpl.java
new file mode 100644
index 0000000000..8931d42899
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/CollectionReturnImpl.java
@@ -0,0 +1,46 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.internal;
+
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan2.build.spi.LoadPlanBuildingContext;
+import org.hibernate.loader.plan2.spi.CollectionReturn;
+import org.hibernate.persister.walking.spi.CollectionDefinition;
+
+/**
+ * @author Steve Ebersole
+ */
+public class CollectionReturnImpl extends AbstractCollectionReference implements CollectionReturn {
+
+	public CollectionReturnImpl(CollectionDefinition collectionDefinition, LoadPlanBuildingContext context) {
+		super(
+				context.getQuerySpaces().makeCollectionQuerySpace(
+						context.getQuerySpaces().generateImplicitUid(),
+						collectionDefinition.getCollectionPersister()
+				),
+				new PropertyPath( "[" + collectionDefinition.getCollectionPersister().getRole() + "]" ),
+				context
+		);
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/EntityFetchImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/EntityFetchImpl.java
new file mode 100644
index 0000000000..83a4ed0762
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/EntityFetchImpl.java
@@ -0,0 +1,114 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.internal;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.loader.plan2.build.spi.ExpandingFetchSource;
+import org.hibernate.loader.plan2.spi.EntityFetch;
+import org.hibernate.loader.plan2.spi.EntityQuerySpace;
+import org.hibernate.loader.plan2.spi.FetchSource;
+import org.hibernate.loader.plan2.spi.Join;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.WalkingException;
+import org.hibernate.type.EntityType;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EntityFetchImpl extends AbstractEntityReference implements EntityFetch {
+	private final FetchSource fetchSource;
+	private final AttributeDefinition fetchedAttribute;
+	private final EntityPersister fetchedPersister;
+	private final FetchStrategy fetchStrategy;
+
+	private final Join fetchedJoin;
+
+	public EntityFetchImpl(
+			ExpandingFetchSource fetchSource,
+			AssociationAttributeDefinition fetchedAttribute,
+			EntityPersister fetchedPersister,
+			FetchStrategy fetchStrategy,
+			Join fetchedJoin) {
+		super(
+				fetchedPersister,
+				fetchSource.getPropertyPath().append( fetchedAttribute.getName() )
+		);
+
+		this.fetchSource = fetchSource;
+		this.fetchedAttribute = fetchedAttribute;
+		this.fetchedPersister = fetchedPersister;
+		this.fetchStrategy = fetchStrategy;
+		this.fetchedJoin = fetchedJoin;
+	}
+
+	@Override
+	protected EntityQuerySpace getEntityQuerySpace() {
+		return (EntityQuerySpace) fetchedJoin.getRightHandSide();
+	}
+
+	@Override
+	public FetchSource getSource() {
+		return fetchSource;
+	}
+
+	@Override
+	public FetchStrategy getFetchStrategy() {
+		return fetchStrategy;
+	}
+
+	@Override
+	public EntityType getFetchedType() {
+		return (EntityType) fetchedAttribute.getType();
+	}
+
+	@Override
+	public boolean isNullable() {
+		return fetchedAttribute.isNullable();
+	}
+
+	@Override
+	public String getAdditionalJoinConditions() {
+		return null;
+	}
+
+	@Override
+	public String[] toSqlSelectFragments(String alias) {
+		return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
+	}
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
+		if ( attributeDefinition.getType().isCollectionType() ) {
+			throw new WalkingException(
+					"Encountered collection attribute in identifier fetches: " + attributeDefinition.getSource() +
+							"." + attributeDefinition.getName()
+			);
+		}
+		// todo : allow bi-directional key-many-to-one fetches?
+		//		those do cause problems in Loader; question is whether those are indicative of that situation or
+		// 		of Loaders ability to handle it.
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/EntityReturnImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/EntityReturnImpl.java
new file mode 100644
index 0000000000..a2a7177e8f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/EntityReturnImpl.java
@@ -0,0 +1,61 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.internal;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.loader.plan2.build.spi.ExpandingFetchSource;
+import org.hibernate.loader.plan2.build.spi.LoadPlanBuildingContext;
+import org.hibernate.loader.plan2.spi.EntityQuerySpace;
+import org.hibernate.loader.plan2.spi.EntityReturn;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.EntityDefinition;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EntityReturnImpl extends AbstractEntityReference implements EntityReturn, ExpandingFetchSource {
+	private final EntityQuerySpace entityQuerySpace;
+
+	public EntityReturnImpl(EntityDefinition entityDefinition, LoadPlanBuildingContext loadPlanBuildingContext) {
+		super(
+				entityDefinition.getEntityPersister(),
+				new PropertyPath( entityDefinition.getEntityPersister().getEntityName() )
+		);
+		this.entityQuerySpace = loadPlanBuildingContext.getQuerySpaces().makeEntityQuerySpace(
+				loadPlanBuildingContext.getQuerySpaces().generateImplicitUid(),
+				entityDefinition.getEntityPersister()
+		);
+	}
+
+	@Override
+	protected EntityQuerySpace getEntityQuerySpace() {
+		return entityQuerySpace;
+	}
+
+	@Override
+	public void validateFetchPlan(FetchStrategy fetchStrategy, AttributeDefinition attributeDefinition) {
+		// nothing to do here really
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/ScalarReturnImpl.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/ScalarReturnImpl.java
new file mode 100644
index 0000000000..6eb6cbe67b
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/internal/ScalarReturnImpl.java
@@ -0,0 +1,30 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.internal;
+
+/**
+ * @author Steve Ebersole
+ */
+public class ScalarReturnImpl {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetch.java
new file mode 100644
index 0000000000..d90a0e6f14
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetch.java
@@ -0,0 +1,37 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+import org.hibernate.type.CollectionType;
+
+/**
+ * Models the requested fetching of a persistent collection attribute.
+ *
+ * @author Steve Ebersole
+ */
+public interface CollectionFetch extends Fetch, CollectionReference {
+	@Override
+	public CollectionType getFetchedType();
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetchableElement.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetchableElement.java
new file mode 100644
index 0000000000..de16f5ebb3
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetchableElement.java
@@ -0,0 +1,38 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+/**
+ * A collection element which can be a FetchSource.
+ *
+ * @author Steve Ebersole
+ */
+public interface CollectionFetchableElement extends FetchSource {
+	/**
+	 * Reference back to the collection to which this element belongs
+	 *
+	 * @return the collection reference.
+	 */
+	public CollectionReference getCollectionReference();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetchableIndex.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetchableIndex.java
new file mode 100644
index 0000000000..8c2e6d0c22
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionFetchableIndex.java
@@ -0,0 +1,38 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+/**
+ * A collection index which can be a FetchSource.
+ *
+ * @author Steve Ebersole
+ */
+public interface CollectionFetchableIndex extends FetchSource {
+	/**
+	 * Reference back to the collection to which this index belongs
+	 *
+	 * @return the collection reference.
+	 */
+	public CollectionReference getCollectionReference();
+}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionQuerySpace.java
new file mode 100644
index 0000000000..9aa768f09d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionQuerySpace.java
@@ -0,0 +1,42 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+import org.hibernate.persister.collection.CollectionPersister;
+
+/**
+ * Models a QuerySpace for a persistent collection.
+ * <p/>
+ * It's {@link #getDisposition()} result will be {@link Disposition#COLLECTION}
+ *
+ * @author Steve Ebersole
+ */
+public interface CollectionQuerySpace extends QuerySpace {
+	/**
+	 * Retrieve the collection persister this QuerySpace refers to.
+	 *
+	 * @return The collection persister.
+	 */
+	public CollectionPersister getCollectionPersister();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionReference.java
new file mode 100644
index 0000000000..1b404ac004
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionReference.java
@@ -0,0 +1,80 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.persister.collection.CollectionPersister;
+
+/**
+ * Represents a reference to a persistent collection either as a Return or as a Fetch
+ *
+ * @author Steve Ebersole
+ */
+public interface CollectionReference {
+	/**
+	 * Obtain the UID of the QuerySpace (specifically a {@link CollectionQuerySpace}) that this CollectionReference
+	 * refers to.
+	 *
+	 * @return The UID
+	 */
+	public String getQuerySpaceUid();
+
+	/**
+	 * Retrieves the CollectionPersister describing the collection associated with this Return.
+	 *
+	 * @return The CollectionPersister.
+	 */
+	public CollectionPersister getCollectionPersister();
+
+	/**
+	 * Retrieve the metadata about the index of this collection *as a FetchSource*.  Will return
+	 * {@code null} when:<ul>
+	 *     <li>the collection is not indexed</li>
+	 *     <li>the index is not a composite or entity (cannot act as a FetchSource)</li>
+	 * </ul>
+	 * <p/>
+	 * Works only for map keys, since a List index (int type) cannot act as a FetchSource.
+	 * <p/>
+	 *
+	 * @return The collection index metadata as a FetchSource, or {@code null}.
+	 */
+	public CollectionFetchableIndex getIndexGraph();
+
+	/**
+	 * Retrieve the metadata about the elements of this collection *as a FetchSource*.  Will return
+	 * {@code null} when the element is not a composite or entity (cannot act as a FetchSource).
+	 * Works only for map keys, since a List index cannot be anything other than an int which cannot be a FetchSource.
+	 * <p/>
+	 *
+	 * @return The collection element metadata as a FetchSource, or {@code null}.
+	 */
+	public CollectionFetchableElement getElementGraph();
+
+	/**
+	 * Retrieve the PropertyPath to this reference.
+	 *
+	 * @return The PropertyPath
+	 */
+	public PropertyPath getPropertyPath();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionReturn.java
new file mode 100644
index 0000000000..d49e56d13b
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CollectionReturn.java
@@ -0,0 +1,33 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+/**
+ * Models the a persistent collection as root {@link Return}.  Pertinent to collection initializer
+ * ({@link org.hibernate.loader.plan2.spi.LoadPlan.Disposition#COLLECTION_INITIALIZER}) LoadPlans only,
+ *
+ * @author Steve Ebersole
+ */
+public interface CollectionReturn extends CollectionReference, Return {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeFetch.java
new file mode 100644
index 0000000000..3bc26b9dc3
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeFetch.java
@@ -0,0 +1,32 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+/**
+ * Models the requested fetching of a composite attribute.
+ *
+ * @author Steve Ebersole
+ */
+public interface CompositeFetch extends Fetch, FetchSource {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeQuerySpace.java
new file mode 100644
index 0000000000..af4778501a
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/CompositeQuerySpace.java
@@ -0,0 +1,34 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+/**
+ * Models a QuerySpace for a composition (component/embeddable).
+ * <p/>
+ * It's {@link #getDisposition()} result will be {@link Disposition#COMPOSITE}
+ *
+ * @author Steve Ebersole
+ */
+public interface CompositeQuerySpace extends QuerySpace {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityFetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityFetch.java
new file mode 100644
index 0000000000..e7b0405259
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityFetch.java
@@ -0,0 +1,30 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface EntityFetch extends Fetch, EntityReference {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityIdentifierDescription.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityIdentifierDescription.java
new file mode 100644
index 0000000000..be88599539
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityIdentifierDescription.java
@@ -0,0 +1,32 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+/**
+ * Descriptor for the identifier of an entity as a FetchSource (which allows for key-many-to-one handling).
+ *
+ * @author Steve Ebersole
+ */
+public interface EntityIdentifierDescription extends FetchSource {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityQuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityQuerySpace.java
new file mode 100644
index 0000000000..e955bd66dc
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityQuerySpace.java
@@ -0,0 +1,42 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * Models a QuerySpace specific to an entity (EntityPersister).
+ * <p/>
+ * It's {@link #getDisposition()} result will be {@link Disposition#ENTITY}
+ *
+ * @author Steve Ebersole
+ */
+public interface EntityQuerySpace extends QuerySpace {
+	 /**
+	 * Retrieve the EntityPersister that this QuerySpace refers to.
+	  *
+	 * @return The entity persister
+	 */
+	public EntityPersister getEntityPersister();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityReference.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityReference.java
new file mode 100644
index 0000000000..f43f7b982f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityReference.java
@@ -0,0 +1,56 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+import org.hibernate.persister.entity.EntityPersister;
+
+/**
+ * Represents a reference to an entity either as a return or as a fetch
+ *
+ * @author Steve Ebersole
+ */
+public interface EntityReference extends FetchSource {
+
+	/**
+	 * Obtain the UID of the QuerySpace (specifically a {@link CollectionQuerySpace}) that this CollectionReference
+	 * refers to.
+	 *
+	 * @return The UID
+	 */
+	public String getQuerySpaceUid();
+
+	/**
+	 * Retrieves the EntityPersister describing the entity associated with this Return.
+	 *
+	 * @return The EntityPersister.
+	 */
+	public EntityPersister getEntityPersister();
+
+	/**
+	 * Get the description of this entity's identifier.
+	 *
+	 * @return The identifier description.
+	 */
+	public EntityIdentifierDescription getIdentifierDescription();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityReturn.java
new file mode 100644
index 0000000000..170d63ca44
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/EntityReturn.java
@@ -0,0 +1,34 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+/**
+ * Models the an entity as root {@link Return}.  Pertinent to entity loader
+ * ({@link org.hibernate.loader.plan2.spi.LoadPlan.Disposition#ENTITY_LOADER}) and mixed
+ * ({@link org.hibernate.loader.plan2.spi.LoadPlan.Disposition#MIXED}) LoadPlans
+ *
+ * @author Steve Ebersole
+ */
+public interface EntityReturn extends EntityReference, Return {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Fetch.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Fetch.java
new file mode 100644
index 0000000000..b2477bbb16
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Fetch.java
@@ -0,0 +1,85 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.loader.PropertyPath;
+import org.hibernate.type.Type;
+
+/**
+ * Contract for associations that are being fetched.
+ * <p/>
+ * NOTE : can represent components/embeddables
+ *
+ * @author Steve Ebersole
+ */
+public interface Fetch {
+	/**
+	 * Obtain the owner of this fetch.
+	 *
+	 * @return The fetch owner.
+	 */
+	public FetchSource getSource();
+
+	/**
+	 * Get the property path to this fetch
+	 *
+	 * @return The property path
+	 */
+	public PropertyPath getPropertyPath();
+
+	/**
+	 * Gets the fetch strategy for this fetch.
+	 *
+	 * @return the fetch strategy for this fetch.
+	 */
+	public FetchStrategy getFetchStrategy();
+
+	/**
+	 * Get the Hibernate Type that describes the fetched attribute
+	 *
+	 * @return The Type of the fetched attribute
+	 */
+	public Type getFetchedType();
+
+	/**
+	 * Is this fetch nullable?
+	 *
+	 * @return true, if this fetch is nullable; false, otherwise.
+	 */
+	public boolean isNullable();
+
+
+
+	// Hoping to make these go away ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	public String getAdditionalJoinConditions();
+
+	/**
+	 * Generates the SQL select fragments for this fetch.  A select fragment is the column and formula references.
+	 *
+	 * @return the select fragments
+	 */
+	public String[] toSqlSelectFragments(String alias);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/FetchSource.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/FetchSource.java
new file mode 100644
index 0000000000..08d2364a32
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/FetchSource.java
@@ -0,0 +1,121 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+import org.hibernate.engine.FetchStrategy;
+import org.hibernate.loader.PropertyPath;
+
+import org.hibernate.loader.plan.spi.SqlSelectFragmentResolver;
+import org.hibernate.loader.plan.spi.build.LoadPlanBuildingContext;
+import org.hibernate.persister.entity.EntityPersister;
+import org.hibernate.persister.walking.spi.AnyMappingDefinition;
+import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+import org.hibernate.persister.walking.spi.CompositionDefinition;
+import org.hibernate.type.Type;
+
+/**
+ * Contract for a FetchSource (aka, the thing that owns the fetched attribute).
+ *
+ *
+ * @author Steve Ebersole
+ */
+public interface FetchSource {
+	/**
+	 * Convenient constant for returning no fetches from {@link #getFetches()}
+	 */
+	public static final Fetch[] NO_FETCHES = new Fetch[0];
+
+	/**
+	 * Get the property path to this fetch owner
+	 *
+	 * @return The property path
+	 */
+	public PropertyPath getPropertyPath();
+
+	/**
+	 * Retrieve the fetches owned by this return.
+	 *
+	 * @return The owned fetches.
+	 */
+	public Fetch[] getFetches();
+
+
+
+	// Stuff I can hopefully remove ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+
+	/**
+	 * The idea of addFetch() below has moved to {@link org.hibernate.loader.plan2.build.spi.ExpandingFetchSource}.
+	 * <p/>
+	 * Most of the others are already part of Fetch
+	 */
+
+
+//
+//
+//
+//	/**
+//	 * Returns the type of the specified fetch.
+//	 *
+//	 * @param fetch - the owned fetch.
+//	 *
+//	 * @return the type of the specified fetch.
+//	 */
+//	public Type getType(Fetch fetch);
+//
+//	/**
+//	 * Is the specified fetch nullable?
+//	 *
+//	 * @param fetch - the owned fetch.
+//	 *
+//	 * @return true, if the fetch is nullable; false, otherwise.
+//	 */
+//	public boolean isNullable(Fetch fetch);
+//
+//	/**
+//	 * Generates the SQL select fragments for the specified fetch.  A select fragment is the column and formula
+//	 * references.
+//	 *
+//	 * @param fetch - the owned fetch.
+//	 * @param alias The table alias to apply to the fragments (used to qualify column references)
+//	 *
+//	 * @return the select fragments
+//	 */
+//	public String[] toSqlSelectFragments(Fetch fetch, String alias);
+//
+//	/**
+//	 * Contract to add fetches to this owner.  Care should be taken in calling this method; it is intended
+//	 * for Hibernate usage
+//	 *
+//	 * @param fetch The fetch to add
+//	 */
+//	public void addFetch(Fetch fetch);
+//
+//
+//	public SqlSelectFragmentResolver toSqlSelectFragmentResolver();
+//
+//
+//
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Join.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Join.java
new file mode 100644
index 0000000000..d3dcf7b0f1
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Join.java
@@ -0,0 +1,44 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+/**
+ * Represents a join in the QuerySpace-sense.  In HQL/JP-QL, this would be an implicit/explicit join; in
+ * metamodel-driven LoadPlans, this would be joins indicated by the metamodel.
+ */
+public interface Join {
+	// todo : would be good to have the SQL alias info here because we know it when we would be building this Join,
+	// and to do it afterwards would require lot of logic to recreate.
+	// But we do want this model to be workable in Search/OGM as well, plus the HQL parser has shown time-and-again
+	// that it is best to put off resolving and injecting physical aliases etc until as-late-as-possible.
+
+	// todo : do we know enough here to declare the "owner" side?  aka, the "fk direction"
+	// and if we do ^^, is that enough to figure out the SQL aliases more easily (see above)?
+
+	public QuerySpace getLeftHandSide();
+
+	public QuerySpace getRightHandSide();
+
+	public boolean isRightHandSideOptional();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/JoinDefinedByMetadata.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/JoinDefinedByMetadata.java
new file mode 100644
index 0000000000..b870229f79
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/JoinDefinedByMetadata.java
@@ -0,0 +1,40 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+import org.hibernate.persister.walking.spi.AttributeDefinition;
+
+/**
+ * Specialization of a Join that is defined by the metadata of a persistent attribute.
+ *
+ * @author Steve Ebersole
+ */
+public interface JoinDefinedByMetadata extends Join {
+	/**
+	 * Get a reference to the attribute whose metadata defines this join.
+	 *
+	 * @return The attribute defining the join.
+	 */
+	public AttributeDefinition getAttributeDefiningJoin();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/LoadPlan.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/LoadPlan.java
new file mode 100644
index 0000000000..a41430a91f
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/LoadPlan.java
@@ -0,0 +1,139 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+import java.util.List;
+
+/**
+ * Describes a plan for performing a load of results.
+ *
+ * Generally speaking there are 3 forms of load plans:<ul>
+ *     <li>
+ *         {@link org.hibernate.loader.plan2.spi.LoadPlan.Disposition#ENTITY_LOADER} - An entity load plan for
+ *         handling get/load handling.  This form will typically have a single return (of type {@link org.hibernate.loader.plan.spi.EntityReturn})
+ *         defined by {@link #getReturns()}, possibly defining fetches.
+ *     </li>
+ *     <li>
+ *         {@link org.hibernate.loader.plan2.spi.LoadPlan.Disposition#COLLECTION_INITIALIZER} - A collection initializer,
+ *         used to load the contents of a collection.  This form will typically have a single return (of
+ *         type {@link org.hibernate.loader.plan.spi.CollectionReturn}) defined by {@link #getReturns()}, possibly defining fetches
+ *     </li>
+ *     <li>
+ *         {@link org.hibernate.loader.plan2.spi.LoadPlan.Disposition#MIXED} - A query load plan which can contain
+ *         multiple returns of mixed type (though all implementing {@link org.hibernate.loader.plan.spi.Return}).  Again, may possibly define fetches.
+ *     </li>
+ * </ul>
+ * <p/>
+ * todo : would also like to see "call back" style access for handling "subsequent actions" such as...<ul>
+ *     <li>follow-on locking</li>
+ *     <li>join fetch conversions to subselect fetches</li>
+ * </ul>
+ *
+ * @author Steve Ebersole
+ */
+public interface LoadPlan {
+
+	/**
+	 * What is the disposition of this LoadPlan, in terms of its returns.
+	 *
+	 * @return The LoadPlan's disposition
+	 */
+	public Disposition getDisposition();
+
+	/**
+	 * Get the returns indicated by this LoadPlan.<ul>
+	 *     <li>
+	 *         A {@link Disposition#ENTITY_LOADER} LoadPlan would have just a single Return of type {@link org.hibernate.loader.plan.spi.EntityReturn}.
+	 *     </li>
+	 *     <li>
+	 *         A {@link Disposition#COLLECTION_INITIALIZER} LoadPlan would have just a single Return of type
+	 *         {@link org.hibernate.loader.plan.spi.CollectionReturn}.
+	 *     </li>
+	 *     <li>
+	 *         A {@link Disposition#MIXED} LoadPlan would contain a mix of {@link org.hibernate.loader.plan.spi.EntityReturn} and
+	 *         {@link org.hibernate.loader.plan.spi.ScalarReturn} elements, but no {@link org.hibernate.loader.plan.spi.CollectionReturn}.
+	 *     </li>
+	 * </ul>
+	 *
+	 * @return The Returns for this LoadPlan.
+	 *
+	 * @see Disposition
+	 */
+	public List<? extends Return> getReturns();
+
+	/**
+	 * todo : document this...
+	 * <p/>
+	 * this is the stuff that was added in this plan2 package...  splitting the "from clause" and "select clause"
+	 * graphs and removing all (started) SQL references.  QuerySpaces represents the "from clause".  The
+	 * "select clause" is represented by {@link #getReturns()}.
+	 *
+	 * @return The QuerySpaces
+	 */
+	public QuerySpaces getQuerySpaces();
+
+	/**
+	 * Does this load plan indicate that lazy attributes are to be force fetched?
+	 * <p/>
+	 * Here we are talking about laziness in regards to the legacy bytecode enhancement which adds support for
+	 * partial selects of an entity's state (e.g., skip loading a lob initially, wait until/if it is needed)
+	 * <p/>
+	 * This one would effect the SQL that needs to get generated as well as how the result set would be read.
+	 * Therefore we make this part of the LoadPlan contract.
+	 * <p/>
+	 * NOTE that currently this is only relevant for HQL loaders when the HQL has specified the {@code FETCH ALL PROPERTIES}
+	 * key-phrase.  In all other cases, this returns false.
+
+	 * @return Whether or not to
+	 */
+	public boolean areLazyAttributesForceFetched();
+
+	/**
+	 * Convenient form of checking {@link #getReturns()} for scalar root returns.
+	 *
+	 * @return {@code true} if {@link #getReturns()} contained any scalar returns; {@code false} otherwise.
+	 */
+	public boolean hasAnyScalarReturns();
+
+	/**
+	 * Enumerated possibilities for describing the disposition of this LoadPlan.
+	 */
+	public static enum Disposition {
+		/**
+		 * This is an "entity loader" load plan, which describes a plan for loading one or more entity instances of
+		 * the same entity type.  There is a single return, which will be of type {@link org.hibernate.loader.plan.spi.EntityReturn}
+		 */
+		ENTITY_LOADER,
+		/**
+		 * This is a "collection initializer" load plan, which describes a plan for loading one or more entity instances of
+		 * the same collection type.  There is a single return, which will be of type {@link org.hibernate.loader.plan.spi.CollectionReturn}
+		 */
+		COLLECTION_INITIALIZER,
+		/**
+		 * We have a mixed load plan, which will have one or more returns of {@link org.hibernate.loader.plan.spi.EntityReturn} and {@link org.hibernate.loader.plan.spi.ScalarReturn}
+		 * (NOT {@link org.hibernate.loader.plan.spi.CollectionReturn}).
+		 */
+		MIXED
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpace.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpace.java
new file mode 100644
index 0000000000..716a1443d2
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpace.java
@@ -0,0 +1,82 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+/**
+ * Defines a persister reference (either entity or collection) or a composite reference.  JPA terms this
+ * an "abstract schema type" when discussing JPQL or JPA Criteria queries.  This models a single source of attributes
+ * (and fetches).
+ *
+ * @author Steve Ebersole
+ */
+public interface QuerySpace {
+	/**
+	 * The uid/alias which uniquely identifies this QuerySpace.  Can be used to uniquely reference this
+	 * QuerySpace elsewhere.
+	 *
+	 * @return The uid
+	 *
+	 * @see QuerySpaces#findQuerySpaceByUid(java.lang.String)
+	 */
+	public String getUid();
+
+	/**
+	 * Enumeration of the different types of QuerySpaces we can have.
+	 */
+	public static enum Disposition {
+		/**
+		 * We have an entity-based QuerySpace.  It is castable to {@link EntityQuerySpace} for more details.
+		 */
+		ENTITY,
+		/**
+		 * We have a collection-based QuerySpace.  It is castable to {@link CollectionQuerySpace} for more details.
+		 */
+		COLLECTION,
+		/**
+		 * We have a composition-based QuerySpace.  It is castable to {@link CompositeQuerySpace} for more details.
+		 */
+		COMPOSITE
+	}
+
+	/**
+	 * What type of QuerySpace (more-specific) is this?
+	 *
+	 * @return The enum value representing the more-specific type of QuerySpace
+	 */
+	public Disposition getDisposition();
+
+	/**
+	 * Obtain all joins which originate from this QuerySpace, in other words, all the joins which this QuerySpace is
+	 * the right-hand-side of.
+	 * <p/>
+	 * For all the joins returned here, {@link Join#getRightHandSide()} should point back to this QuerySpace such that
+	 * <code>
+	 *     space.getJoins().forEach{ join -> join.getRightHandSide() == space }
+	 * </code>
+	 * is true for all.
+	 *
+	 * @return The joins which originate from this query space.
+	 */
+	public Iterable<Join> getJoins();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpaces.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpaces.java
new file mode 100644
index 0000000000..82401b6318
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/QuerySpaces.java
@@ -0,0 +1,51 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+/**
+ * Models a collection of {@link QuerySpace} references and exposes the ability to find a {@link QuerySpace} by its UID
+ * <p/>
+ * todo : make this hierarchical... that would be needed to truly work for hql parser
+ *
+ * @author Steve Ebersole
+ */
+public interface QuerySpaces {
+	/**
+	 * Gets the root QuerySpace references.
+	 *
+	 * @return The roots
+	 */
+	public Iterable<QuerySpace> getRootQuerySpaces();
+
+	/**
+	 * Locate a QuerySpace by its uid.
+	 *
+	 * @param uid The QuerySpace uid to match
+	 *
+	 * @return The match, {@code null} is returned if no match.
+	 *
+	 * @see QuerySpace#getUid()
+	 */
+	public QuerySpace findQuerySpaceByUid(String uid);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Return.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Return.java
new file mode 100644
index 0000000000..b9a00f7a45
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/Return.java
@@ -0,0 +1,38 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+/**
+ * Represents a return value in the query results.  Not the same as a result (column) in the JDBC ResultSet!
+ * <p/>
+ * Return is distinctly different from a {@link org.hibernate.loader.plan.spi.Fetch} and so modeled as completely separate hierarchy.
+ *
+ * @see ScalarReturn
+ * @see EntityReturn
+ * @see CollectionReturn
+ *
+ * @author Steve Ebersole
+ */
+public interface Return {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/ScalarReturn.java b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/ScalarReturn.java
new file mode 100644
index 0000000000..0176784e01
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/loader/plan2/spi/ScalarReturn.java
@@ -0,0 +1,48 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2013, Red Hat Inc. or third-party contributors as
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
+package org.hibernate.loader.plan2.spi;
+
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.loader.plan.spi.AbstractPlanNode;
+import org.hibernate.loader.plan.spi.Return;
+import org.hibernate.type.Type;
+
+/**
+ * Represent a simple scalar return within a query result.  Generally this would be values of basic (String, Integer,
+ * etc) or composite types.
+ *
+ * @author Steve Ebersole
+ */
+public class ScalarReturn extends AbstractPlanNode implements Return {
+	private final Type type;
+
+	public ScalarReturn(SessionFactoryImplementor factory, Type type) {
+		super( factory );
+		this.type = type;
+	}
+
+	public Type getType() {
+		return type;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index 732058a398..eeb63ff359 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -1055,1030 +1055,1030 @@ public abstract class AbstractCollectionPersister
 	}
 
 	protected void appendIndexColumns(SelectFragment frag, String alias) {
 		if ( hasIndex ) {
 			for ( int i = 0; i < indexColumnIsSettable.length; i++ ) {
 				if ( indexColumnIsSettable[i] ) {
 					frag.addColumn( alias, indexColumnNames[i], indexColumnAliases[i] );
 				}
 				else {
 					frag.addFormula( alias, indexFormulaTemplates[i], indexColumnAliases[i] );
 				}
 			}
 		}
 	}
 
 	protected void appendIdentifierColumns(SelectFragment frag, String alias) {
 		if ( hasIdentifier ) {
 			frag.addColumn( alias, identifierColumnName, identifierColumnAlias );
 		}
 	}
 
 	public String[] getIndexColumnNames() {
 		return indexColumnNames;
 	}
 
 	public String[] getIndexFormulas() {
 		return indexFormulas;
 	}
 
 	public String[] getIndexColumnNames(String alias) {
 		return qualify( alias, indexColumnNames, indexFormulaTemplates );
 
 	}
 
 	public String[] getElementColumnNames(String alias) {
 		return qualify( alias, elementColumnNames, elementFormulaTemplates );
 	}
 
 	private static String[] qualify(String alias, String[] columnNames, String[] formulaTemplates) {
 		int span = columnNames.length;
 		String[] result = new String[span];
 		for ( int i = 0; i < span; i++ ) {
 			if ( columnNames[i] == null ) {
 				result[i] = StringHelper.replace( formulaTemplates[i], Template.TEMPLATE, alias );
 			}
 			else {
 				result[i] = StringHelper.qualify( alias, columnNames[i] );
 			}
 		}
 		return result;
 	}
 
 	public String[] getElementColumnNames() {
 		return elementColumnNames; // TODO: something with formulas...
 	}
 
 	public String[] getKeyColumnNames() {
 		return keyColumnNames;
 	}
 
 	public boolean hasIndex() {
 		return hasIndex;
 	}
 
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public boolean isInverse() {
 		return isInverse;
 	}
 
 	public String getTableName() {
 		return qualifiedTableName;
 	}
 
 	private BasicBatchKey removeBatchKey;
 
 	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Deleting collection: %s",
 						MessageHelper.collectionInfoString( this, id, getFactory() ) );
 			}
 
 			// Remove all the old entries
 
 			try {
 				int offset = 1;
 				PreparedStatement st = null;
 				Expectation expectation = Expectations.appropriateExpectation( getDeleteAllCheckStyle() );
 				boolean callable = isDeleteAllCallable();
 				boolean useBatch = expectation.canBeBatched();
 				String sql = getSQLDeleteString();
 				if ( useBatch ) {
 					if ( removeBatchKey == null ) {
 						removeBatchKey = new BasicBatchKey(
 								getRole() + "#REMOVE",
 								expectation
 								);
 					}
 					st = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getBatch( removeBatchKey )
 							.getBatchStatement( sql, callable );
 				}
 				else {
 					st = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql, callable );
 				}
 
 				try {
 					offset += expectation.prepare( st );
 
 					writeKey( st, id, offset, session );
 					if ( useBatch ) {
 						session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getBatch( removeBatchKey )
 								.addToBatch();
 					}
 					else {
 						expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
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
 						session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 					}
 				}
 
 				LOG.debug( "Done deleting collection" );
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not delete collection: " +
 								MessageHelper.collectionInfoString( this, id, getFactory() ),
 						getSQLDeleteString()
 						);
 			}
 
 		}
 
 	}
 
 	protected BasicBatchKey recreateBatchKey;
 
 	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Inserting collection: %s",
 						MessageHelper.collectionInfoString( this, collection, id, session ) );
 			}
 
 			try {
 				// create all the new entries
 				Iterator entries = collection.entries( this );
 				if ( entries.hasNext() ) {
 					Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 					collection.preInsert( this );
 					int i = 0;
 					int count = 0;
 					while ( entries.hasNext() ) {
 
 						final Object entry = entries.next();
 						if ( collection.entryExists( entry, i ) ) {
 							int offset = 1;
 							PreparedStatement st = null;
 							boolean callable = isInsertCallable();
 							boolean useBatch = expectation.canBeBatched();
 							String sql = getSQLInsertRowString();
 
 							if ( useBatch ) {
 								if ( recreateBatchKey == null ) {
 									recreateBatchKey = new BasicBatchKey(
 											getRole() + "#RECREATE",
 											expectation
 											);
 								}
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( recreateBatchKey )
 										.getBatchStatement( sql, callable );
 							}
 							else {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getStatementPreparer()
 										.prepareStatement( sql, callable );
 							}
 
 							try {
 								offset += expectation.prepare( st );
 
 								// TODO: copy/paste from insertRows()
 								int loc = writeKey( st, id, offset, session );
 								if ( hasIdentifier ) {
 									loc = writeIdentifier( st, collection.getIdentifier( entry, i ), loc, session );
 								}
 								if ( hasIndex /* && !indexIsFormula */) {
 									loc = writeIndex( st, collection.getIndex( entry, i, this ), loc, session );
 								}
 								loc = writeElement( st, collection.getElement( entry ), loc, session );
 
 								if ( useBatch ) {
 									session.getTransactionCoordinator()
 											.getJdbcCoordinator()
 											.getBatch( recreateBatchKey )
 											.addToBatch();
 								}
 								else {
 									expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 								}
 
 								collection.afterRowInsert( this, entry, i );
 								count++;
 							}
 							catch ( SQLException sqle ) {
 								if ( useBatch ) {
 									session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 								}
 								throw sqle;
 							}
 							finally {
 								if ( !useBatch ) {
 									session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 								}
 							}
 
 						}
 						i++;
 					}
 
 					LOG.debugf( "Done inserting collection: %s rows inserted", count );
 
 				}
 				else {
 					LOG.debug( "Collection was empty" );
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not insert collection: " +
 								MessageHelper.collectionInfoString( this, collection, id, session ),
 						getSQLInsertRowString()
 						);
 			}
 		}
 	}
 
 	protected boolean isRowDeleteEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	public void deleteRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowDeleteEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Deleting rows of collection: %s",
 						MessageHelper.collectionInfoString( this, collection, id, session ) );
 			}
 
 			boolean deleteByIndex = !isOneToMany() && hasIndex && !indexContainsFormula;
 			final Expectation expectation = Expectations.appropriateExpectation( getDeleteCheckStyle() );
 			try {
 				// delete all the deleted entries
 				Iterator deletes = collection.getDeletes( this, !deleteByIndex );
 				if ( deletes.hasNext() ) {
 					int offset = 1;
 					int count = 0;
 					while ( deletes.hasNext() ) {
 						PreparedStatement st = null;
 						boolean callable = isDeleteCallable();
 						boolean useBatch = expectation.canBeBatched();
 						String sql = getSQLDeleteRowString();
 
 						if ( useBatch ) {
 							if ( deleteBatchKey == null ) {
 								deleteBatchKey = new BasicBatchKey(
 										getRole() + "#DELETE",
 										expectation
 										);
 							}
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getBatch( deleteBatchKey )
 									.getBatchStatement( sql, callable );
 						}
 						else {
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getStatementPreparer()
 									.prepareStatement( sql, callable );
 						}
 
 						try {
 							expectation.prepare( st );
 
 							Object entry = deletes.next();
 							int loc = offset;
 							if ( hasIdentifier ) {
 								writeIdentifier( st, entry, loc, session );
 							}
 							else {
 								loc = writeKey( st, id, loc, session );
 								if ( deleteByIndex ) {
 									writeIndexToWhere( st, entry, loc, session );
 								}
 								else {
 									writeElementToWhere( st, entry, loc, session );
 								}
 							}
 
 							if ( useBatch ) {
 								session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( deleteBatchKey )
 										.addToBatch();
 							}
 							else {
 								expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 							}
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 							}
 							throw sqle;
 						}
 						finally {
 							if ( !useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 							}
 						}
 
 						LOG.debugf( "Done deleting collection rows: %s deleted", count );
 					}
 				}
 				else {
 					LOG.debug( "No rows to delete" );
 				}
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not delete collection rows: " +
 								MessageHelper.collectionInfoString( this, collection, id, session ),
 						getSQLDeleteRowString()
 						);
 			}
 		}
 	}
 
 	protected boolean isRowInsertEnabled() {
 		return true;
 	}
 
 	private BasicBatchKey insertBatchKey;
 
 	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && isRowInsertEnabled() ) {
 
 			if ( LOG.isDebugEnabled() ) LOG.debugf( "Inserting rows of collection: %s",
 					MessageHelper.collectionInfoString( this, collection, id, session ) );
 
 			try {
 				// insert all the new entries
 				collection.preInsert( this );
 				Iterator entries = collection.entries( this );
 				Expectation expectation = Expectations.appropriateExpectation( getInsertCheckStyle() );
 				boolean callable = isInsertCallable();
 				boolean useBatch = expectation.canBeBatched();
 				String sql = getSQLInsertRowString();
 				int i = 0;
 				int count = 0;
 				while ( entries.hasNext() ) {
 					int offset = 1;
 					Object entry = entries.next();
 					PreparedStatement st = null;
 					if ( collection.needsInserting( entry, i, elementType ) ) {
 
 						if ( useBatch ) {
 							if ( insertBatchKey == null ) {
 								insertBatchKey = new BasicBatchKey(
 										getRole() + "#INSERT",
 										expectation
 										);
 							}
 							if ( st == null ) {
 								st = session.getTransactionCoordinator()
 										.getJdbcCoordinator()
 										.getBatch( insertBatchKey )
 										.getBatchStatement( sql, callable );
 							}
 						}
 						else {
 							st = session.getTransactionCoordinator()
 									.getJdbcCoordinator()
 									.getStatementPreparer()
 									.prepareStatement( sql, callable );
 						}
 
 						try {
 							offset += expectation.prepare( st );
 							// TODO: copy/paste from recreate()
 							offset = writeKey( st, id, offset, session );
 							if ( hasIdentifier ) {
 								offset = writeIdentifier( st, collection.getIdentifier( entry, i ), offset, session );
 							}
 							if ( hasIndex /* && !indexIsFormula */) {
 								offset = writeIndex( st, collection.getIndex( entry, i, this ), offset, session );
 							}
 							writeElement( st, collection.getElement( entry ), offset, session );
 
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().getBatch( insertBatchKey ).addToBatch();
 							}
 							else {
 								expectation.verifyOutcome( session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().executeUpdate( st ), st, -1 );
 							}
 							collection.afterRowInsert( this, entry, i );
 							count++;
 						}
 						catch ( SQLException sqle ) {
 							if ( useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 							}
 							throw sqle;
 						}
 						finally {
 							if ( !useBatch ) {
 								session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 							}
 						}
 					}
 					i++;
 				}
 				LOG.debugf( "Done inserting rows: %s inserted", count );
 			}
 			catch ( SQLException sqle ) {
 				throw sqlExceptionHelper.convert(
 						sqle,
 						"could not insert collection rows: " +
 								MessageHelper.collectionInfoString( this, collection, id, session ),
 						getSQLInsertRowString()
 						);
 			}
 
 		}
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public String getOwnerEntityName() {
 		return entityName;
 	}
 
 	public EntityPersister getOwnerEntityPersister() {
 		return ownerPersister;
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() {
 		return identifierGenerator;
 	}
 
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 	public boolean hasOrphanDelete() {
 		return hasOrphanDelete;
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return indexType;
 		}
 		return elementPropertyMapping.toType( propertyName );
 	}
 
 	public abstract boolean isManyToMany();
 
 	public String getManyToManyFilterFragment(String alias, Map enabledFilters) {
 		StringBuilder buffer = new StringBuilder();
 		manyToManyFilterHelper.render( buffer, elementPersister.getFilterAliasGenerator(alias), enabledFilters );
 
 		if ( manyToManyWhereString != null ) {
 			buffer.append( " and " )
 					.append( StringHelper.replace( manyToManyWhereTemplate, Template.TEMPLATE, alias ) );
 		}
 
 		return buffer.toString();
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			return qualify( alias, indexColumnNames, indexFormulaTemplates );
 		}
 		return elementPropertyMapping.toColumns( alias, propertyName );
 	}
 
 	private String[] indexFragments;
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public String[] toColumns(String propertyName) throws QueryException {
 		if ( "index".equals( propertyName ) ) {
 			if ( indexFragments == null ) {
 				String[] tmp = new String[indexColumnNames.length];
 				for ( int i = 0; i < indexColumnNames.length; i++ ) {
 					tmp[i] = indexColumnNames[i] == null
 							? indexFormulas[i]
 							: indexColumnNames[i];
 					indexFragments = tmp;
 				}
 			}
 			return indexFragments;
 		}
 
 		return elementPropertyMapping.toColumns( propertyName );
 	}
 
 	public Type getType() {
 		return elementPropertyMapping.getType(); // ==elementType ??
 	}
 
 	public String getName() {
 		return getRole();
 	}
 
 	public EntityPersister getElementPersister() {
 		if ( elementPersister == null ) {
 			throw new AssertionFailure( "not an association" );
 		}
 		return elementPersister;
 	}
 
 	public boolean isCollection() {
 		return true;
 	}
 
 	public Serializable[] getCollectionSpaces() {
 		return spaces;
 	}
 
 	protected abstract String generateDeleteString();
 
 	protected abstract String generateDeleteRowString();
 
 	protected abstract String generateUpdateRowString();
 
 	protected abstract String generateInsertRowString();
 
 	public void updateRows(PersistentCollection collection, Serializable id, SessionImplementor session)
 			throws HibernateException {
 
 		if ( !isInverse && collection.isRowUpdatePossible() ) {
 
 			LOG.debugf( "Updating rows of collection: %s#%s", role, id );
 
 			// update all the modified entries
 			int count = doUpdateRows( id, collection, session );
 
 			LOG.debugf( "Done updating rows: %s updated", count );
 		}
 	}
 
 	protected abstract int doUpdateRows(Serializable key, PersistentCollection collection, SessionImplementor session)
 			throws HibernateException;
 
 	public void processQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 			throws HibernateException {
 		if ( collection.hasQueuedOperations() ) {
 			doProcessQueuedOps( collection, key, session );
 		}
 	}
 
 	protected abstract void doProcessQueuedOps(PersistentCollection collection, Serializable key, SessionImplementor session)
 			throws HibernateException;
 
 	public CollectionMetadata getCollectionMetadata() {
 		return this;
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	protected String filterFragment(String alias) throws MappingException {
 		return hasWhere() ? " and " + getSQLWhereString( alias ) : "";
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 
 		StringBuilder sessionFilterFragment = new StringBuilder();
 		filterHelper.render( sessionFilterFragment, getFilterAliasGenerator(alias), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	protected boolean isInsertCallable() {
 		return insertCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getInsertCheckStyle() {
 		return insertCheckStyle;
 	}
 
 	protected boolean isUpdateCallable() {
 		return updateCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getUpdateCheckStyle() {
 		return updateCheckStyle;
 	}
 
 	protected boolean isDeleteCallable() {
 		return deleteCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteCheckStyle() {
 		return deleteCheckStyle;
 	}
 
 	protected boolean isDeleteAllCallable() {
 		return deleteAllCallable;
 	}
 
 	protected ExecuteUpdateResultCheckStyle getDeleteAllCheckStyle() {
 		return deleteAllCheckStyle;
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) + '(' + role + ')';
 	}
 
 	public boolean isVersioned() {
 		return isVersioned && getOwnerEntityPersister().isVersioned();
 	}
 
 	public String getNodeName() {
 		return nodeName;
 	}
 
 	public String getElementNodeName() {
 		return elementNodeName;
 	}
 
 	public String getIndexNodeName() {
 		return indexNodeName;
 	}
 
 	// TODO: deprecate???
 	protected SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	// TODO: needed???
 	protected SqlExceptionHelper getSQLExceptionHelper() {
 		return sqlExceptionHelper;
 	}
 
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
 	}
 
 	public boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return filterHelper.isAffectedBy( session.getEnabledFilters() ) ||
 				( isManyToMany() && manyToManyFilterHelper.isAffectedBy( session.getEnabledFilters() ) );
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 
 	public boolean isMutable() {
 		return isMutable;
 	}
 
 	public String[] getCollectionPropertyColumnAliases(String propertyName, String suffix) {
 		String rawAliases[] = (String[]) collectionPropertyColumnAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String result[] = new String[rawAliases.length];
 		for ( int i = 0; i < rawAliases.length; i++ ) {
 			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 
 	// TODO: formulas ?
 	public void initCollectionPropertyMap() {
 
 		initCollectionPropertyMap( "key", keyType, keyColumnAliases, keyColumnNames );
 		initCollectionPropertyMap( "element", elementType, elementColumnAliases, elementColumnNames );
 		if ( hasIndex ) {
 			initCollectionPropertyMap( "index", indexType, indexColumnAliases, indexColumnNames );
 		}
 		if ( hasIdentifier ) {
 			initCollectionPropertyMap(
 					"id",
 					identifierType,
 					new String[] { identifierColumnAlias },
 					new String[] { identifierColumnName } );
 		}
 	}
 
 	private void initCollectionPropertyMap(String aliasName, Type type, String[] columnAliases, String[] columnNames) {
 
 		collectionPropertyColumnAliases.put( aliasName, columnAliases );
 		collectionPropertyColumnNames.put( aliasName, columnNames );
 
 		if ( type.isComponentType() ) {
 			CompositeType ct = (CompositeType) type;
 			String[] propertyNames = ct.getPropertyNames();
 			for ( int i = 0; i < propertyNames.length; i++ ) {
 				String name = propertyNames[i];
 				collectionPropertyColumnAliases.put( aliasName + "." + name, columnAliases[i] );
 				collectionPropertyColumnNames.put( aliasName + "." + name, columnNames[i] );
 			}
 		}
 
 	}
 
 	public int getSize(Serializable key, SessionImplementor session) {
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectSizeString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next() ? rs.getInt( 1 ) - baseIndex : 0;
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve collection size: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 					);
 		}
 	}
 
 	public boolean indexExists(Serializable key, Object index, SessionImplementor session) {
 		return exists( key, incrementIndexByBase( index ), getIndexType(), sqlDetectRowByIndexString, session );
 	}
 
 	public boolean elementExists(Serializable key, Object element, SessionImplementor session) {
 		return exists( key, element, getElementType(), sqlDetectRowByElementString, session );
 	}
 
 	private boolean exists(Serializable key, Object indexOrElement, Type indexOrElementType, String sql, SessionImplementor session) {
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				indexOrElementType.nullSafeSet( st, indexOrElement, keyColumnNames.length + 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					return rs.next();
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			catch ( TransientObjectException e ) {
 				return false;
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not check row existence: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 					);
 		}
 	}
 
 	public Object getElementByIndex(Serializable key, Object index, SessionImplementor session, Object owner) {
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sqlSelectRowByIndexString );
 			try {
 				getKeyType().nullSafeSet( st, key, 1, session );
 				getIndexType().nullSafeSet( st, incrementIndexByBase( index ), keyColumnNames.length + 1, session );
 				ResultSet rs = session.getTransactionCoordinator().getJdbcCoordinator().getResultSetReturn().extract( st );
 				try {
 					if ( rs.next() ) {
 						return getElementType().nullSafeGet( rs, elementColumnAliases, session, owner );
 					}
 					else {
 						return null;
 					}
 				}
 				finally {
 					session.getTransactionCoordinator().getJdbcCoordinator().release( rs, st );
 				}
 			}
 			finally {
 				session.getTransactionCoordinator().getJdbcCoordinator().release( st );
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not read row: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
 					);
 		}
 	}
 
 	public boolean isExtraLazy() {
 		return isExtraLazy;
 	}
 
 	protected Dialect getDialect() {
 		return dialect;
 	}
 
 	/**
 	 * Intended for internal use only. In fact really only currently used from
 	 * test suite for assertion purposes.
 	 *
 	 * @return The default collection initializer for this persister/collection.
 	 */
 	public CollectionInitializer getInitializer() {
 		return initializer;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public String getMappedByProperty() {
 		return mappedByProperty;
 	}
 
 	private class StandardOrderByAliasResolver implements OrderByAliasResolver {
 		private final String rootAlias;
 
 		private StandardOrderByAliasResolver(String rootAlias) {
 			this.rootAlias = rootAlias;
 		}
 
 		@Override
 		public String resolveTableAlias(String columnReference) {
 			if ( elementPersister == null ) {
 				// we have collection of non-entity elements...
 				return rootAlias;
 			}
 			else {
 				return ( (Loadable) elementPersister ).getTableAliasForColumn( columnReference, rootAlias );
 			}
 		}
 	}
 
 	public abstract FilterAliasGenerator getFilterAliasGenerator(final String rootAlias);
 
 	// ColectionDefinition impl ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public CollectionPersister getCollectionPersister() {
 		return this;
 	}
 
 	@Override
 	public CollectionIndexDefinition getIndexDefinition() {
 		if ( ! hasIndex() ) {
 			return null;
 		}
 
 		return new CollectionIndexDefinition() {
 			@Override
 			public CollectionDefinition getCollectionDefinition() {
 				return AbstractCollectionPersister.this;
 			}
 
 			@Override
 			public Type getType() {
 				return getIndexType();
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
 				if ( getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat composite collection index type as entity" );
 				}
 				return (EntityPersister) ( (AssociationType) getIndexType() ).getAssociatedJoinable( getFactory() );
 			}
 
 			@Override
 			public CompositionDefinition toCompositeDefinition() {
 				if ( ! getType().isComponentType() ) {
 					throw new IllegalStateException( "Cannot treat entity collection index type as composite" );
 				}
 				// todo : implement
 				throw new NotYetImplementedException();
 			}
 		};
 	}
 
 	@Override
 	public CollectionElementDefinition getElementDefinition() {
 		return new CollectionElementDefinition() {
 			@Override
 			public CollectionDefinition getCollectionDefinition() {
 				return AbstractCollectionPersister.this;
 			}
 
 			@Override
 			public Type getType() {
 				return getElementType();
 			}
 
 			@Override
 			public AnyMappingDefinition toAnyMappingDefinition() {
 				final Type type = getType();
 				if ( ! type.isAnyType() ) {
 					throw new WalkingException( "Cannot treat collection element type as ManyToAny" );
 				}
 				return new StandardAnyTypeDefinition( (AnyType) type, isLazy() || isExtraLazy() );
 			}
 
 			@Override
 			public EntityDefinition toEntityDefinition() {
 				if ( getType().isComponentType() ) {
 					throw new WalkingException( "Cannot treat composite collection element type as entity" );
 				}
 				return getElementPersister();
 			}
 
 			@Override
 			public CompositeCollectionElementDefinition toCompositeElementDefinition() {
 
 				if ( ! getType().isComponentType() ) {
 					throw new WalkingException( "Cannot treat entity collection element type as composite" );
 				}
 
 				return new CompositeCollectionElementDefinition() {
 					@Override
 					public String getName() {
 						return "";
 					}
 
 					@Override
-					public Type getType() {
-						return getElementType();
+					public CompositeType getType() {
+						return (CompositeType) getElementType();
 					}
 
 					@Override
 					public boolean isNullable() {
 						return false;
 					}
 
 					@Override
 					public AttributeSource getSource() {
 						// TODO: what if this is a collection w/in an encapsulated composition attribute?
 						// should return the encapsulated composition attribute instead???
 						return getOwnerEntityPersister();
 					}
 
 					@Override
 					public Iterable<AttributeDefinition> getAttributes() {
 						return CompositionSingularSubAttributesHelper.getCompositeCollectionElementSubAttributes( this );
 					}
 
 					@Override
 					public CollectionDefinition getCollectionDefinition() {
 						return AbstractCollectionPersister.this;
 					}
 				};
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/CompositionSingularSubAttributesHelper.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/CompositionSingularSubAttributesHelper.java
index f3b80c14f6..c253950772 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/CompositionSingularSubAttributesHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/CompositionSingularSubAttributesHelper.java
@@ -1,287 +1,287 @@
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
 package org.hibernate.persister.walking.internal;
 
 import java.util.Iterator;
 
 import org.hibernate.FetchMode;
 import org.hibernate.engine.FetchStrategy;
 import org.hibernate.engine.FetchStyle;
 import org.hibernate.engine.FetchTiming;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.AbstractEntityPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.spi.HydratedCompoundValueHandler;
 import org.hibernate.persister.walking.spi.AnyMappingDefinition;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
 import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeSource;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.WalkingException;
 import org.hibernate.type.AnyType;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * A helper for getting attributes from a composition that is known
  * to have only singular attributes; for example, sub-attributes of a
  * composite ID or a composite collection element.
  *
  * TODO: This should be refactored into a delegate and renamed.
  *
  * @author Gail Badner
  */
 public class CompositionSingularSubAttributesHelper {
 
 	/**
 	 * Get composite ID sub-attribute definitions.
 	 *
 	 * @param entityPersister - the entity persister.
 	 * @return composite ID sub-attribute definitions.
 	 */
 	public static Iterable<AttributeDefinition> getIdentifierSubAttributes(
 			final AbstractEntityPersister entityPersister) {
 		return getSingularSubAttributes(
 				entityPersister,
 				entityPersister,
 				(CompositeType) entityPersister.getIdentifierType(),
 				entityPersister.getTableName(),
 				entityPersister.getRootTableIdentifierColumnNames()
 		);
 	}
 
 	/**
 	 * Get sub-attribute definitions for a composite collection element.
 	 * @param compositionElementDefinition - composite collection element definition.
 	 * @return sub-attribute definitions for a composite collection element.
 	 */
 	public static Iterable<AttributeDefinition> getCompositeCollectionElementSubAttributes(
 			CompositeCollectionElementDefinition compositionElementDefinition) {
 		final QueryableCollection collectionPersister =
 				(QueryableCollection) compositionElementDefinition.getCollectionDefinition().getCollectionPersister();
 		return getSingularSubAttributes(
 				compositionElementDefinition.getSource(),
 				(OuterJoinLoadable) collectionPersister.getOwnerEntityPersister(),
 				(CompositeType) collectionPersister.getElementType(),
 				collectionPersister.getTableName(),
 				collectionPersister.getElementColumnNames()
 		);
 	}
 
 	private static Iterable<AttributeDefinition> getSingularSubAttributes(
 			final AttributeSource source,
 			final OuterJoinLoadable ownerEntityPersister,
 			final CompositeType compositeType,
 			final String lhsTableName,
 			final String[] lhsColumns) {
 		return new Iterable<AttributeDefinition>() {
 			@Override
 			public Iterator<AttributeDefinition> iterator() {
 				return new Iterator<AttributeDefinition>() {
 					private final int numberOfAttributes = compositeType.getSubtypes().length;
 					private int currentSubAttributeNumber = 0;
 					private int currentColumnPosition = 0;
 
 					@Override
 					public boolean hasNext() {
 						return currentSubAttributeNumber < numberOfAttributes;
 					}
 
 					@Override
 					public AttributeDefinition next() {
 						final int subAttributeNumber = currentSubAttributeNumber;
 						currentSubAttributeNumber++;
 
 						final String name = compositeType.getPropertyNames()[subAttributeNumber];
 						final Type type = compositeType.getSubtypes()[subAttributeNumber];
 
 						final int columnPosition = currentColumnPosition;
 						final int columnSpan = type.getColumnSpan( ownerEntityPersister.getFactory() );
 						final String[] subAttributeLhsColumns = ArrayHelper.slice( lhsColumns, columnPosition, columnSpan );
 
 						final boolean nullable = compositeType.getPropertyNullability()[subAttributeNumber];
 
 						currentColumnPosition += columnSpan;
 
 						if ( type.isAssociationType() ) {
 							final AssociationType aType = (AssociationType) type;
 							return new AssociationAttributeDefinition() {
 								@Override
 								public AssociationKey getAssociationKey() {
 									return new AssociationKey( lhsTableName, subAttributeLhsColumns );
 								}
 
 
 								@Override
 								public AssociationNature getAssociationNature() {
 									if ( type.isAnyType() ) {
 										return AssociationNature.ANY;
 									}
 									else {
 										// cannot be a collection
 										return AssociationNature.ENTITY;
 									}
 								}
 
 								@Override
 								public EntityDefinition toEntityDefinition() {
 									if ( getAssociationNature() != AssociationNature.ENTITY ) {
 										throw new WalkingException(
 												"Cannot build EntityDefinition from non-entity-typed attribute"
 										);
 									}
 									return (EntityPersister) aType.getAssociatedJoinable( ownerEntityPersister.getFactory() );
 								}
 
 								@Override
 								public AnyMappingDefinition toAnyDefinition() {
 									if ( getAssociationNature() != AssociationNature.ANY ) {
 										throw new WalkingException(
 												"Cannot build AnyMappingDefinition from non-any-typed attribute"
 										);
 									}
 									// todo : not sure how lazy is propogated into the component for a subattribute of type any
 									return new StandardAnyTypeDefinition( (AnyType) aType, false );
 								}
 
 								@Override
 								public CollectionDefinition toCollectionDefinition() {
 									throw new WalkingException( "A collection cannot be mapped to a composite ID sub-attribute." );
 								}
 
 								@Override
 								public FetchStrategy determineFetchPlan(LoadQueryInfluencers loadQueryInfluencers, PropertyPath propertyPath) {
 									return new FetchStrategy( FetchTiming.IMMEDIATE, FetchStyle.JOIN );
 								}
 
 								@Override
 								public CascadeStyle determineCascadeStyle() {
 									return CascadeStyles.NONE;
 								}
 
 								@Override
 								public HydratedCompoundValueHandler getHydratedCompoundValueExtractor() {
 									return null;
 								}
 
 								@Override
 								public String getName() {
 									return name;
 								}
 
 								@Override
 								public AssociationType getType() {
 									return aType;
 								}
 
 								@Override
 								public boolean isNullable() {
 									return nullable;
 								}
 
 								@Override
 								public AttributeSource getSource() {
 									return source;
 								}
 							};
 						}
 						else if ( type.isComponentType() ) {
 							return new CompositionDefinition() {
 								@Override
 								public String getName() {
 									return name;
 								}
 
 								@Override
-								public Type getType() {
-									return type;
+								public CompositeType getType() {
+									return (CompositeType) type;
 								}
 
 								@Override
 								public boolean isNullable() {
 									return nullable;
 								}
 
 								@Override
 								public AttributeSource getSource() {
 									return this;
 								}
 
 								@Override
 								public Iterable<AttributeDefinition> getAttributes() {
 									return CompositionSingularSubAttributesHelper.getSingularSubAttributes(
 											this,
 											ownerEntityPersister,
 											(CompositeType) type,
 											lhsTableName,
 											subAttributeLhsColumns
 									);
 								}
 							};
 						}
 						else {
 							return new AttributeDefinition() {
 								@Override
 								public String getName() {
 									return name;
 								}
 
 								@Override
 								public Type getType() {
 									return type;
 								}
 
 								@Override
 								public boolean isNullable() {
 									return nullable;
 								}
 
 								@Override
 								public AttributeSource getSource() {
 									return source;
 								}
 							};
 						}
 					}
 
 					@Override
 					public void remove() {
 						throw new UnsupportedOperationException( "Remove operation not supported here" );
 					}
 				};
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/EntityIdentifierDefinitionHelper.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/EntityIdentifierDefinitionHelper.java
index 3a3aba11b4..cb9e5c91eb 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/EntityIdentifierDefinitionHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/EntityIdentifierDefinitionHelper.java
@@ -1,157 +1,167 @@
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
 package org.hibernate.persister.walking.internal;
 
 import org.hibernate.persister.entity.AbstractEntityPersister;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeSource;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EncapsulatedEntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.NonEncapsulatedEntityIdentifierDefinition;
+import org.hibernate.type.CompositeType;
 import org.hibernate.type.Type;
 
 /**
  * @author Gail Badner
  */
 public class EntityIdentifierDefinitionHelper {
 
 	public static EntityIdentifierDefinition buildSimpleEncapsulatedIdentifierDefinition(final AbstractEntityPersister entityPersister) {
 		return new EncapsulatedEntityIdentifierDefinition() {
+			private final AttributeDefinitionAdapter attr = new AttributeDefinitionAdapter( entityPersister);
+
 			@Override
 			public AttributeDefinition getAttributeDefinition() {
-				return new AttributeDefinitionAdapter( entityPersister);
+				return attr;
 			}
 
 			@Override
 			public boolean isEncapsulated() {
 				return true;
 			}
 
 			@Override
 			public EntityDefinition getEntityDefinition() {
 				return entityPersister;
 			}
 		};
 	}
 
 	public static EntityIdentifierDefinition buildEncapsulatedCompositeIdentifierDefinition(
 			final AbstractEntityPersister entityPersister) {
 
 		return new EncapsulatedEntityIdentifierDefinition() {
+			private final CompositionDefinitionAdapter compositionDefinition = new CompositionDefinitionAdapter( entityPersister );
+
 			@Override
 			public AttributeDefinition getAttributeDefinition() {
-				return new CompositionDefinitionAdapter( entityPersister );
+				return compositionDefinition;
 			}
 
 			@Override
 			public boolean isEncapsulated() {
 				return true;
 			}
 
 			@Override
 			public EntityDefinition getEntityDefinition() {
 				return entityPersister;
 			}
 		};
 	}
 
 	public static EntityIdentifierDefinition buildNonEncapsulatedCompositeIdentifierDefinition(final AbstractEntityPersister entityPersister) {
 		return new NonEncapsulatedEntityIdentifierDefinition() {
 			@Override
 			public Iterable<AttributeDefinition> getAttributes() {
 				return CompositionSingularSubAttributesHelper.getIdentifierSubAttributes( entityPersister );
 			}
 
 			@Override
 			public Class getSeparateIdentifierMappingClass() {
 				return entityPersister.getEntityMetamodel().getIdentifierProperty().getType().getReturnedClass();
 			}
 
 			@Override
 			public boolean isEncapsulated() {
 				return false;
 			}
 
 			@Override
 			public EntityDefinition getEntityDefinition() {
 				return entityPersister;
 			}
 		};
 	}
 
 	private static class AttributeDefinitionAdapter implements AttributeDefinition {
 		private final AbstractEntityPersister entityPersister;
 
 		AttributeDefinitionAdapter(AbstractEntityPersister entityPersister) {
 			this.entityPersister = entityPersister;
 		}
 
 		@Override
 		public String getName() {
 			return entityPersister.getEntityMetamodel().getIdentifierProperty().getName();
 		}
 
 		@Override
 		public Type getType() {
 			return entityPersister.getEntityMetamodel().getIdentifierProperty().getType();
 		}
 
 		@Override
 		public boolean isNullable() {
 			return false;
 		}
 
 		@Override
 		public AttributeSource getSource() {
 			return entityPersister;
 		}
 
 		@Override
 		public String toString() {
 			return "<identifier-property:" + getName() + ">";
 		}
 
 		protected AbstractEntityPersister getEntityPersister() {
 			return entityPersister;
 		}
 	}
 
 	private static class CompositionDefinitionAdapter extends AttributeDefinitionAdapter implements CompositionDefinition {
 		CompositionDefinitionAdapter(AbstractEntityPersister entityPersister) {
 			super( entityPersister );
 		}
 
 		@Override
 		public String toString() {
 			return "<identifier-property:" + getName() + ">";
 		}
 
 		@Override
+		public CompositeType getType() {
+			return (CompositeType) super.getType();
+		}
+
+		@Override
 		public Iterable<AttributeDefinition> getAttributes() {
 			return  CompositionSingularSubAttributesHelper.getIdentifierSubAttributes( getEntityPersister() );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationKey.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationKey.java
index 352c4a548f..5cfd6c843f 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationKey.java
@@ -1,53 +1,87 @@
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
 package org.hibernate.persister.walking.spi;
 
 import java.util.Arrays;
 
+import org.hibernate.internal.util.StringHelper;
+
 /**
- * Used to uniquely identify a foreign key, so that we don't join it more than once creating circularities.
+ * Used to uniquely identify a foreign key, so that we don't join it more than once creating circularities.  Note
+ * that the table+columns refers to the association owner.  These are used to help detect bi-directional associations
+ * since the Hibernate runtime metamodel (persisters) do not inherently know this information.  For example, consider
+ * the Order -> Customer and Customer -> Order(s) bi-directional association; both would be mapped to the
+ * {@code ORDER_TABLE.CUST_ID} column.  That is the purpose of this struct.
  * <p/>
- * bit of a misnomer to call this an association attribute.  But this follows the legacy use of AssociationKey
+ * Bit of a misnomer to call this an association attribute.  But this follows the legacy use of AssociationKey
  * from old JoinWalkers to denote circular join detection
+ *
+ * @author Steve Ebersole
+ * @author Gail Badner
+ * @author Gavin King
  */
 public class AssociationKey {
 	private final String table;
 	private final String[] columns;
 
+	/**
+	 * Create the AssociationKey.
+	 *
+	 * @param table The table part of the association key
+	 * @param columns The columns that define the association key
+	 */
 	public AssociationKey(String table, String[] columns) {
 		this.table = table;
 		this.columns = columns;
 	}
 
 	@Override
-	public boolean equals(Object other) {
-		AssociationKey that = (AssociationKey) other;
-		return that.table.equals(table) && Arrays.equals( columns, that.columns );
+	public boolean equals(Object o) {
+		if ( this == o ) {
+			return true;
+		}
+		if ( o == null || getClass() != o.getClass() ) {
+			return false;
+		}
+
+		final AssociationKey that = (AssociationKey) o;
+		return table.equals( that.table ) && Arrays.equals( columns, that.columns );
+
 	}
 
 	@Override
 	public int hashCode() {
-		return table.hashCode(); //TODO: inefficient
+		return table.hashCode();
+	}
+
+	private String str;
+
+	@Override
+	public String toString() {
+		if ( str == null ) {
+			str = "AssociationKey[table=" + table + ", columns={" + StringHelper.join( ",", columns ) + "}]";
+		}
+		return str;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
index 26a8a5c098..16a2226af1 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationVisitationStrategy.java
@@ -1,65 +1,98 @@
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
 package org.hibernate.persister.walking.spi;
 
 /**
+ * Strategy for walking associations as defined by the Hibernate metamodel.
+ * <p/>
+ * {@link #start()} and {@link #finish()} are called at the start and at the finish of the process.
+ * <p/>
+ * Walking might start with an entity or a collection depending on where the walker is asked to start.  When starting
+ * with an entity, {@link #startingEntity}/{@link #finishingEntity} ()} will be the outer set of calls.  When starting
+ * with a collection, {@link #startingCollection}/{@link #finishingCollection} will be the outer set of calls.
+ *
  * @author Steve Ebersole
  */
 public interface AssociationVisitationStrategy {
 	/**
 	 * Notification we are preparing to start visitation.
 	 */
 	public void start();
 
 	/**
 	 * Notification we are finished visitation.
 	 */
 	public void finish();
 
+	/**
+	 * Notification we are starting to walk an entity.
+	 *
+	 * @param entityDefinition The entity we are starting to walk
+	 */
 	public void startingEntity(EntityDefinition entityDefinition);
+
+	/**
+	 * Notification we are finishing walking an entity.
+	 *
+	 * @param entityDefinition The entity we are finishing walking.
+	 */
 	public void finishingEntity(EntityDefinition entityDefinition);
 
+	/**
+	 * Notification we are starting to walk the identifier of an entity.
+	 *
+	 * @param entityIdentifierDefinition The identifier we are starting to walk
+	 */
 	public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition);
+
+	/**
+	 * Notification we are finishing walking an entity.
+	 *
+	 * @param entityIdentifierDefinition The identifier we are finishing walking.
+	 */
 	public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition);
 
 	public void startingCollection(CollectionDefinition collectionDefinition);
 	public void finishingCollection(CollectionDefinition collectionDefinition);
 
 	public void startingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition);
 	public void finishingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition);
 
 	public void startingCollectionElements(CollectionElementDefinition elementDefinition);
 	public void finishingCollectionElements(CollectionElementDefinition elementDefinition);
 
 	public void startingComposite(CompositionDefinition compositionDefinition);
 	public void finishingComposite(CompositionDefinition compositionDefinition);
 
 	public void startingCompositeCollectionElement(CompositeCollectionElementDefinition compositionElementDefinition);
 	public void finishingCompositeCollectionElement(CompositeCollectionElementDefinition compositionElementDefinition);
 
 	public boolean startingAttribute(AttributeDefinition attributeDefinition);
 	public void finishingAttribute(AttributeDefinition attributeDefinition);
 
 	public void foundAny(AssociationAttributeDefinition attributeDefinition, AnyMappingDefinition anyDefinition);
+
+	public void associationKeyRegistered(AssociationKey associationKey);
+	public void foundCircularAssociationKey(AssociationKey associationKey, AttributeDefinition attributeDefinition);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositionDefinition.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositionDefinition.java
index e9d7c251cf..4160df526a 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositionDefinition.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/CompositionDefinition.java
@@ -1,30 +1,34 @@
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
 package org.hibernate.persister.walking.spi;
 
+import org.hibernate.type.CompositeType;
+
 /**
  * @author Steve Ebersole
  */
 public interface CompositionDefinition extends AttributeDefinition, AttributeSource {
+	@Override
+	CompositeType getType();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
index 89453781b8..4e9c25208a 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/spi/MetadataDrivenModelGraphVisitor.java
@@ -1,261 +1,266 @@
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
 package org.hibernate.persister.walking.spi;
 
 import java.util.HashSet;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.PropertyPath;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 /**
  * Provides model graph visitation based on the defined metadata (as opposed to based on the incoming graph
  * as we see in cascade processing).  In layman terms, we are walking the graph of the users model as defined by
  * mapped associations.
  * <p/>
  * Re-implementation of the legacy {@link org.hibernate.loader.JoinWalker} contract to leverage load plans.
  *
  * @author Steve Ebersole
  */
 public class MetadataDrivenModelGraphVisitor {
 	private static final Logger log = Logger.getLogger( MetadataDrivenModelGraphVisitor.class );
 
 	public static void visitEntity(AssociationVisitationStrategy strategy, EntityPersister persister) {
 		strategy.start();
 		try {
 			new MetadataDrivenModelGraphVisitor( strategy, persister.getFactory() )
 					.visitEntityDefinition( persister );
 		}
 		finally {
 			strategy.finish();
 		}
 	}
 
 	public static void visitCollection(AssociationVisitationStrategy strategy, CollectionPersister persister) {
 		strategy.start();
 		try {
 			new MetadataDrivenModelGraphVisitor( strategy, persister.getFactory() )
 					.visitCollectionDefinition( persister );
 		}
 		finally {
 			strategy.finish();
 		}
 	}
 
 	private final AssociationVisitationStrategy strategy;
 	private final SessionFactoryImplementor factory;
 
 	// todo : add a getDepth() method to PropertyPath
 	private PropertyPath currentPropertyPath = new PropertyPath();
 
 	public MetadataDrivenModelGraphVisitor(AssociationVisitationStrategy strategy, SessionFactoryImplementor factory) {
 		this.strategy = strategy;
 		this.factory = factory;
 	}
 
 	private void visitEntityDefinition(EntityDefinition entityDefinition) {
 		strategy.startingEntity( entityDefinition );
 
 		visitAttributes( entityDefinition );
 		visitIdentifierDefinition( entityDefinition.getEntityKeyDefinition() );
 
 		strategy.finishingEntity( entityDefinition );
 	}
 
 	private void visitIdentifierDefinition(EntityIdentifierDefinition entityIdentifierDefinition) {
 		strategy.startingEntityIdentifier( entityIdentifierDefinition );
 
 		if ( entityIdentifierDefinition.isEncapsulated() ) {
 			visitAttributeDefinition( ( (EncapsulatedEntityIdentifierDefinition) entityIdentifierDefinition).getAttributeDefinition() );
 		}
 		else {
 			for ( AttributeDefinition attributeDefinition : ( (NonEncapsulatedEntityIdentifierDefinition) entityIdentifierDefinition).getAttributes() ) {
 				visitAttributeDefinition( attributeDefinition );
 			}
 		}
 
 		strategy.finishingEntityIdentifier( entityIdentifierDefinition );
 	}
 
 	private void visitAttributes(AttributeSource attributeSource) {
 		final Iterable<AttributeDefinition> attributeDefinitions = attributeSource.getAttributes();
 		if ( attributeDefinitions == null ) {
 			return;
 		}
 		for ( AttributeDefinition attributeDefinition : attributeSource.getAttributes() ) {
 			visitAttributeDefinition( attributeDefinition );
 		}
 	}
 
 	private void visitAttributeDefinition(AttributeDefinition attributeDefinition) {
 		final PropertyPath subPath = currentPropertyPath.append( attributeDefinition.getName() );
 		log.debug( "Visiting attribute path : " + subPath.getFullPath() );
 
-		final boolean continueWalk;
-		if ( attributeDefinition.getType().isAssociationType() &&
-				isDuplicateAssociationKey( ( (AssociationAttributeDefinition) attributeDefinition ).getAssociationKey() ) ) {
-			log.debug( "Property path deemed to be circular : " + subPath.getFullPath() );
-			continueWalk = false;
-		}
-		else {
-			continueWalk = strategy.startingAttribute( attributeDefinition );
+		if ( attributeDefinition.getType().isAssociationType() ) {
+			final AssociationKey associationKey = ( (AssociationAttributeDefinition) attributeDefinition ).getAssociationKey();
+			if ( isDuplicateAssociationKey( associationKey ) ) {
+				log.debug( "Property path deemed to be circular : " + subPath.getFullPath() );
+				strategy.foundCircularAssociationKey( associationKey, attributeDefinition );
+				// EARLY EXIT!!!
+				return;
+			}
 		}
+
+		final boolean continueWalk = strategy.startingAttribute( attributeDefinition );
 		if ( continueWalk ) {
 			final PropertyPath old = currentPropertyPath;
 			currentPropertyPath = subPath;
 			try {
 				final Type attributeType = attributeDefinition.getType();
 				if ( attributeType.isAssociationType() ) {
 					visitAssociation( (AssociationAttributeDefinition) attributeDefinition );
 				}
 				else if ( attributeType.isComponentType() ) {
 					visitCompositeDefinition( (CompositionDefinition) attributeDefinition );
 				}
 			}
 			finally {
 				currentPropertyPath = old;
 			}
 		}
 		strategy.finishingAttribute( attributeDefinition );
 	}
 
 	private void visitAssociation(AssociationAttributeDefinition attribute) {
 		// todo : do "too deep" checks; but see note about adding depth to PropertyPath
+		//
+		// may also need to better account for "composite fetches" in terms of "depth".
 
 		addAssociationKey( attribute.getAssociationKey() );
 
 		final AssociationAttributeDefinition.AssociationNature nature = attribute.getAssociationNature();
 		if ( nature == AssociationAttributeDefinition.AssociationNature.ANY ) {
 			visitAnyDefinition( attribute, attribute.toAnyDefinition() );
 		}
 		else if ( nature == AssociationAttributeDefinition.AssociationNature.COLLECTION ) {
 			visitCollectionDefinition( attribute.toCollectionDefinition() );
 		}
 		else {
 			visitEntityDefinition( attribute.toEntityDefinition() );
 		}
 	}
 
 	private void visitAnyDefinition(AssociationAttributeDefinition attributeDefinition, AnyMappingDefinition anyDefinition) {
 		strategy.foundAny( attributeDefinition, anyDefinition );
 	}
 
 	private void visitCompositeDefinition(CompositionDefinition compositionDefinition) {
 		strategy.startingComposite( compositionDefinition );
 
 		visitAttributes( compositionDefinition );
 
 		strategy.finishingComposite( compositionDefinition );
 	}
 
 	private void visitCollectionDefinition(CollectionDefinition collectionDefinition) {
 		strategy.startingCollection( collectionDefinition );
 
 		visitCollectionIndex( collectionDefinition );
 		visitCollectionElements( collectionDefinition );
 
 		strategy.finishingCollection( collectionDefinition );
 	}
 
 	private void visitCollectionIndex(CollectionDefinition collectionDefinition) {
 		final CollectionIndexDefinition collectionIndexDefinition = collectionDefinition.getIndexDefinition();
 		if ( collectionIndexDefinition == null ) {
 			return;
 		}
 
 		strategy.startingCollectionIndex( collectionIndexDefinition );
 
 		log.debug( "Visiting index for collection :  " + currentPropertyPath.getFullPath() );
 		currentPropertyPath = currentPropertyPath.append( "<index>" );
 
 		try {
 			final Type collectionIndexType = collectionIndexDefinition.getType();
 			if ( collectionIndexType.isComponentType() ) {
 				visitCompositeDefinition( collectionIndexDefinition.toCompositeDefinition() );
 			}
 			else if ( collectionIndexType.isAssociationType() ) {
 				visitEntityDefinition( collectionIndexDefinition.toEntityDefinition() );
 			}
 		}
 		finally {
 			currentPropertyPath = currentPropertyPath.getParent();
 		}
 
 		strategy.finishingCollectionIndex( collectionIndexDefinition );
 	}
 
 	private void visitCollectionElements(CollectionDefinition collectionDefinition) {
 		final CollectionElementDefinition elementDefinition = collectionDefinition.getElementDefinition();
 		strategy.startingCollectionElements( elementDefinition );
 
 		if ( elementDefinition.getType().isComponentType() ) {
 			visitCompositeCollectionElementDefinition( elementDefinition.toCompositeElementDefinition() );
 		}
 		else if ( elementDefinition.getType().isEntityType() ) {
 			visitEntityDefinition( elementDefinition.toEntityDefinition() );
 		}
 
 		strategy.finishingCollectionElements( elementDefinition );
 	}
 
 	private void visitCompositeCollectionElementDefinition(CompositeCollectionElementDefinition compositionElementDefinition) {
 		strategy.startingCompositeCollectionElement( compositionElementDefinition );
 
 		visitAttributes( compositionElementDefinition );
 
 		strategy.finishingCompositeCollectionElement( compositionElementDefinition );
 	}
 
 	private final Set<AssociationKey> visitedAssociationKeys = new HashSet<AssociationKey>();
 
 	/**
 	 * Add association key to indicate the association is being visited.
 	 * @param associationKey - the association key.
 	 * @throws WalkingException if the association with the specified association key
 	 *                          has already been visited.
 	 */
 	protected void addAssociationKey(AssociationKey associationKey) {
 		if ( ! visitedAssociationKeys.add( associationKey ) ) {
 			throw new WalkingException(
 					String.format( "Association has already been visited: %s", associationKey )
 			);
 		}
+		strategy.associationKeyRegistered( associationKey );
 	}
 
 	/**
 	 * Has an association with the specified key been visited already?
 	 * @param associationKey - the association key.
 	 * @return true, if the association with the specified association key has already been visited;
 	 *         false, otherwise.
 	 */
 	protected boolean isDuplicateAssociationKey(AssociationKey associationKey) {
 		return visitedAssociationKeys.contains( associationKey );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/EncapsulatedCompositeAttributeResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/EncapsulatedCompositeAttributeResultSetProcessorTest.java
index a8285a674c..863b3a0b4b 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/EncapsulatedCompositeAttributeResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/EncapsulatedCompositeAttributeResultSetProcessorTest.java
@@ -1,417 +1,402 @@
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
 package org.hibernate.loader;
 
 import javax.persistence.CollectionTable;
 import javax.persistence.Column;
 import javax.persistence.ElementCollection;
 import javax.persistence.Embeddable;
 import javax.persistence.Embedded;
 import javax.persistence.Entity;
 import javax.persistence.EnumType;
 import javax.persistence.Enumerated;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import java.io.Serializable;
 import java.math.BigDecimal;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Date;
 import java.util.List;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
 import org.hibernate.loader.entity.EntityJoinWalker;
-import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
-import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
 import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
 import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.exec.spi.EntityLoadQueryDetails;
 import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.LoadPlan;
-import org.hibernate.loader.plan.spi.build.LoadPlanBuilder;
-import org.hibernate.loader.spi.NoOpLoadPlanAdvisor;
+import org.hibernate.loader.plan.spi.build.MetadataDrivenLoadPlanBuilder;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 
 /**
  * @author Gail Badner
  */
 public class EncapsulatedCompositeAttributeResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Person.class, Customer.class };
 	}
 
 	@Test
 	public void testSimpleNestedCompositeAttributeProcessing() throws Exception {
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Person person = new Person();
 		person.id = 1;
 		person.name = "Joe Blow";
 		person.address = new Address();
 		person.address.address1 = "1313 Mockingbird Lane";
 		person.address.city = "Pleasantville";
 		person.address.country = "USA";
 		AddressType addressType = new AddressType();
 		addressType.typeName = "snail mail";
 		person.address.type = addressType;
 		session.save( person );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		Person personGotten = (Person) session.get( Person.class, person.id );
 		assertEquals( person.id, personGotten.id );
 		assertEquals( person.address.address1, personGotten.address.address1 );
 		assertEquals( person.address.city, personGotten.address.city );
 		assertEquals( person.address.country, personGotten.address.country );
 		assertEquals( person.address.type.typeName, personGotten.address.type.typeName );
 		session.getTransaction().commit();
 		session.close();
 
 		List results = getResults( sessionFactory().getEntityPersister( Person.class.getName() ) );
 		assertEquals( 1, results.size() );
 		Object result = results.get( 0 );
 		assertNotNull( result );
 
 		Person personWork = ExtraAssertions.assertTyping( Person.class, result );
 		assertEquals( person.id, personWork.id );
 		assertEquals( person.address.address1, personWork.address.address1 );
 		assertEquals( person.address.city, personWork.address.city );
 		assertEquals( person.address.country, personWork.address.country );
 		assertEquals( person.address.type.typeName, personGotten.address.type.typeName );
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.createQuery( "delete Person" ).executeUpdate();
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testNestedCompositeElementCollectionQueryBuilding() {
 		doCompare(
 				sessionFactory(),
 				(OuterJoinLoadable) sessionFactory().getClassMetadata( Customer.class )
 		);
 	}
 
 	private void doCompare(SessionFactoryImplementor sf, OuterJoinLoadable persister) {
 		final LoadQueryInfluencers influencers = LoadQueryInfluencers.NONE;
 		final LockMode lockMode = LockMode.NONE;
 		final int batchSize = 1;
 
 		final EntityJoinWalker walker = new EntityJoinWalker(
 				persister,
 				persister.getKeyColumnNames(),
 				batchSize,
 				lockMode,
 				sf,
 				influencers
 		);
 
 
 		SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy( sf, influencers );
-		LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, persister );
-		LoadQueryDetails details = LoadQueryDetails.makeForBatching(
-				persister.getKeyColumnNames(),
-				plan,
-				sf,
+		LoadPlan plan = MetadataDrivenLoadPlanBuilder.buildRootEntityLoadPlan( strategy, persister );
+		EntityLoadQueryDetails details = EntityLoadQueryDetails.makeForBatching(
+				plan, persister.getKeyColumnNames(),
 				new QueryBuildingParameters() {
 					@Override
 					public LoadQueryInfluencers getQueryInfluencers() {
 						return influencers;
 					}
 
 					@Override
 					public int getBatchSize() {
 						return batchSize;
 					}
 
 					@Override
 					public LockMode getLockMode() {
 						return null;
 					}
 
 					@Override
 					public LockOptions getLockOptions() {
 						return null;
 					}
-				}
+				}, sf
 		);
 
 		compare( walker, details );
 	}
 
-	private void compare(JoinWalker walker, LoadQueryDetails details) {
+	private void compare(JoinWalker walker, EntityLoadQueryDetails details) {
 		System.out.println( "WALKER    : " + walker.getSQLString() );
 		System.out.println( "LOAD-PLAN : " + details.getSqlStatement() );
 		System.out.println();
 	}
 
 	@Test
 	public void testNestedCompositeElementCollectionProcessing() throws Exception {
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Person person = new Person();
 		person.id = 1;
 		person.name = "Joe Blow";
 		session.save( person );
 		Customer customer = new Customer();
 		customer.id = 1L;
 		Investment investment1 = new Investment();
 		investment1.description = "stock";
 		investment1.date = new Date();
 		investment1.monetaryAmount = new MonetaryAmount();
 		investment1.monetaryAmount.currency = MonetaryAmount.CurrencyCode.USD;
 		investment1.monetaryAmount.amount = BigDecimal.valueOf( 1234, 2 );
 		investment1.performedBy = person;
 		Investment investment2 = new Investment();
 		investment2.description = "bond";
 		investment2.date = new Date();
 		investment2.monetaryAmount = new MonetaryAmount();
 		investment2.monetaryAmount.currency = MonetaryAmount.CurrencyCode.EUR;
 		investment2.monetaryAmount.amount = BigDecimal.valueOf( 98176, 1 );
 		customer.investments.add( investment1 );
 		customer.investments.add( investment2 );
 		session.save( customer );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		Customer customerGotten = (Customer) session.get( Customer.class, customer.id );
 		assertEquals( customer.id, customerGotten.id );
 		session.getTransaction().commit();
 		session.close();
 
 		List results = getResults( sessionFactory().getEntityPersister( Customer.class.getName() ) );
 
 		assertEquals( 2, results.size() );
 		assertSame( results.get( 0 ), results.get( 1 ) );
 		Object result = results.get( 0 );
 		assertNotNull( result );
 
 		Customer customerWork = ExtraAssertions.assertTyping( Customer.class, result );
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.delete( customerWork.investments.get( 0 ).performedBy );
 		session.delete( customerWork );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private List<?> getResults(EntityPersister entityPersister ) {
 		final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
 
-		// ultimately, using a LoadPlan requires that it be interpreted into 2 pieces of information:
-		//		1) The query to execute
-		//		2) The ResultSetProcessor to use.
-		//
-		// Those 2 pieces of information share some common context:
-		//		1) alias resolution context
-		//
-
-		final AliasResolutionContext aliasResolutionContext = new AliasResolutionContextImpl( sessionFactory() );
-
-		final String sql = Helper.INSTANCE.generateSql( sessionFactory(), plan, aliasResolutionContext );
+		final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
+		final String sql = queryDetails.getSqlStatement();
+		final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
-		final ResultSetProcessorImpl resultSetProcessor = new ResultSetProcessorImpl( plan, true );
 		final List results = new ArrayList();
 
 		final Session workSession = openSession();
 		workSession.beginTransaction();
 		workSession.doWork(
 				new Work() {
 					@Override
 					public void execute(Connection connection) throws SQLException {
 						PreparedStatement ps = connection.prepareStatement( sql );
 						ps.setInt( 1, 1 );
 						ResultSet resultSet = ps.executeQuery();
 						results.addAll(
 								resultSetProcessor.extractResults(
-										NoOpLoadPlanAdvisor.INSTANCE,
 										resultSet,
 										(SessionImplementor) workSession,
 										new QueryParameters(),
 										new NamedParameterContext() {
 											@Override
 											public int[] getNamedParameterLocations(String name) {
 												return new int[0];
 											}
 										},
-										aliasResolutionContext,
 										true,
 										false,
 										null,
 										null
 								)
 						);
 						resultSet.close();
 						ps.close();
 					}
 				}
 		);
 		workSession.getTransaction().commit();
 		workSession.close();
 		return results;
 	}
 
 	@Entity( name = "Person" )
 	public static class Person implements Serializable {
 		@Id
 		Integer id;
 		String name;
 
 		@Embedded
 		Address address;
 	}
 
 	@Embeddable
 	public static class Address implements Serializable {
 		String address1;
 		String city;
 		String country;
 		AddressType type;
 	}
 
 	@Embeddable
 	public static class AddressType {
 		String typeName;
 	}
 
 	@Entity( name = "Customer" )
 	public static class Customer {
 		private Long id;
 		private List<Investment> investments = new ArrayList<Investment>();
 
 		@Id
 		public Long getId() {
 			return id;
 		}
 		public void setId(Long id) {
 			this.id = id;
 		}
 
 		@ElementCollection(fetch = FetchType.EAGER)
 		@CollectionTable( name = "investments", joinColumns = @JoinColumn( name = "customer_id" ) )
 		public List<Investment> getInvestments() {
 			return investments;
 		}
 		public void setInvestments(List<Investment> investments) {
 			this.investments = investments;
 		}
 	}
 
 	@Embeddable
 	public static class Investment {
 		private MonetaryAmount monetaryAmount;
 		private String description;
 		private Date date;
 		private Person performedBy;
 
 		@Embedded
 		public MonetaryAmount getMonetaryAmount() {
 			return monetaryAmount;
 		}
 		public void setMonetaryAmount(MonetaryAmount monetaryAmount) {
 			this.monetaryAmount = monetaryAmount;
 		}
 		public String getDescription() {
 			return description;
 		}
 		public void setDescription(String description) {
 			this.description = description;
 		}
 		public Date getDate() {
 			return date;
 		}
 		public void setDate(Date date) {
 			this.date = date;
 		}
 		@ManyToOne
 		public Person getPerformedBy() {
 			return performedBy;
 		}
 		public void setPerformedBy(Person performedBy) {
 			this.performedBy = performedBy;
 		}
 	}
 
 	@Embeddable
 	public static class MonetaryAmount {
 		public static enum CurrencyCode {
 			USD,
 			EUR
 		}
 		private BigDecimal amount;
 		@Column(length = 3)
 		@Enumerated(EnumType.STRING)
 		private CurrencyCode currency;
 
 		public BigDecimal getAmount() {
 			return amount;
 		}
 		public void setAmount(BigDecimal amount) {
 			this.amount = amount;
 		}
 
 		public CurrencyCode getCurrency() {
 			return currency;
 		}
 		public void setCurrency(CurrencyCode currency) {
 			this.currency = currency;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/EncapsulatedCompositeIdResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/EncapsulatedCompositeIdResultSetProcessorTest.java
index 69026877f5..f0c21098d1 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/EncapsulatedCompositeIdResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/EncapsulatedCompositeIdResultSetProcessorTest.java
@@ -1,439 +1,434 @@
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
 package org.hibernate.loader;
 
 import javax.persistence.Embeddable;
 import javax.persistence.EmbeddedId;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.ManyToOne;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.LockOptions;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
-import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
 import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
 import org.hibernate.loader.plan.spi.LoadPlan;
-import org.hibernate.loader.spi.NoOpLoadPlanAdvisor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Gail Badner
  */
 public class EncapsulatedCompositeIdResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Parent.class, CardField.class, Card.class };
 	}
 
 	@Test
 	public void testSimpleCompositeId() throws Exception {
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Parent parent = new Parent();
 		parent.id = new ParentPK();
 		parent.id.firstName = "Joe";
 		parent.id.lastName = "Blow";
 		session.save( parent );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		Parent parentGotten = (Parent) session.get( Parent.class, parent.id );
 		assertEquals( parent, parentGotten );
 		session.getTransaction().commit();
 		session.close();
 
 		final List results = getResults(
 				sessionFactory().getEntityPersister( Parent.class.getName() ),
 				new Callback() {
 					@Override
 					public void bind(PreparedStatement ps) throws SQLException {
 						ps.setString( 1, "Joe" );
 						ps.setString( 2, "Blow" );
 					}
 
 					@Override
 					public QueryParameters getQueryParameters() {
 						return new QueryParameters();
 					}
 
 				}
 		);
 		assertEquals( 1, results.size() );
 		Object result = results.get( 0 );
 		assertNotNull( result );
 
 		Parent parentWork = ExtraAssertions.assertTyping( Parent.class, result );
 		assertEquals( parent, parentWork );
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.createQuery( "delete Parent" ).executeUpdate();
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testCompositeIdWithKeyManyToOne() throws Exception {
 		final String cardId = "ace-of-spades";
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Card card = new Card( cardId );
 		final CardField cardField = new CardField( card, 1 );
 		session.persist( card );
 		session.persist( cardField );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		Card cardProxy = (Card) session.load( Card.class, cardId );
 		final CardFieldPK cardFieldPK = new CardFieldPK( cardProxy, 1 );
 		CardField cardFieldGotten = (CardField) session.get( CardField.class, cardFieldPK );
 
 		//assertEquals( card, cardGotten );
 		session.getTransaction().commit();
 		session.close();
 
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( CardField.class.getName() );
 
 		final List results = getResults(
 				entityPersister,
 				new Callback() {
 					@Override
 					public void bind(PreparedStatement ps) throws SQLException {
 						ps.setString( 1, cardField.primaryKey.card.id );
 						ps.setInt( 2, cardField.primaryKey.fieldNumber );
 					}
 
 					@Override
 					public QueryParameters getQueryParameters() {
 						QueryParameters qp = new QueryParameters();
 						qp.setPositionalParameterTypes( new Type[] { entityPersister.getIdentifierType() } );
 						qp.setPositionalParameterValues( new Object[] { cardFieldPK } );
 						qp.setOptionalObject( null );
 						qp.setOptionalEntityName( entityPersister.getEntityName() );
 						qp.setOptionalId( cardFieldPK );
 						qp.setLockOptions( LockOptions.NONE );
 						return qp;
 					}
 
 				}
 		);
 		assertEquals( 1, results.size() );
 		Object result = results.get( 0 );
 		assertNotNull( result );
 
 		CardField cardFieldWork = ExtraAssertions.assertTyping( CardField.class, result );
 		assertEquals( cardFieldGotten, cardFieldWork );
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.createQuery( "delete CardField" ).executeUpdate();
 		session.createQuery( "delete Card" ).executeUpdate();
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	private List getResults(final EntityPersister entityPersister, final Callback callback) {
 		final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
-		final AliasResolutionContext aliasResolutionContext = new AliasResolutionContextImpl( sessionFactory(), 0 );
 
-		final String sql = Helper.INSTANCE.generateSql( sessionFactory(), plan, aliasResolutionContext );
-
-		final ResultSetProcessorImpl resultSetProcessor = new ResultSetProcessorImpl( plan, true );
+		final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
+		final String sql = queryDetails.getSqlStatement();
+		final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
 		final List results = new ArrayList();
 
 		final Session workSession = openSession();
 		workSession.beginTransaction();
 		workSession.doWork(
 				new Work() {
 					@Override
 					public void execute(Connection connection) throws SQLException {
 						PreparedStatement ps = connection.prepareStatement( sql );
 						callback.bind( ps );
 						ResultSet resultSet = ps.executeQuery();
 						//callback.beforeExtractResults( workSession );
 						results.addAll(
 								resultSetProcessor.extractResults(
-										NoOpLoadPlanAdvisor.INSTANCE,
 										resultSet,
 										(SessionImplementor) workSession,
 										callback.getQueryParameters(),
 										new NamedParameterContext() {
 											@Override
 											public int[] getNamedParameterLocations(String name) {
 												return new int[0];
 											}
 										},
-										aliasResolutionContext,
 										true,
 										false,
 										null,
 										null
 								)
 						);
 						resultSet.close();
 						ps.close();
 					}
 				}
 		);
 		workSession.getTransaction().commit();
 		workSession.close();
 
 		return results;
 	}
 
 
 	private interface Callback {
 		void bind(PreparedStatement ps) throws SQLException;
 		QueryParameters getQueryParameters ();
 	}
 
 	@Entity ( name = "Parent" )
 	public static class Parent {
 		@EmbeddedId
 		public ParentPK id;
 
 		public boolean equals(Object o) {
 			if ( this == o ) return true;
 			if ( !( o instanceof Parent ) ) return false;
 
 			final Parent parent = (Parent) o;
 
 			if ( !id.equals( parent.id ) ) return false;
 
 			return true;
 		}
 
 		public int hashCode() {
 			return id.hashCode();
 		}
 	}
 
 	@Embeddable
 	public static class ParentPK implements Serializable {
 		private String firstName;
 		private String lastName;
 
 		public boolean equals(Object o) {
 			if ( this == o ) return true;
 			if ( !( o instanceof ParentPK ) ) return false;
 
 			final ParentPK parentPk = (ParentPK) o;
 
 			if ( !firstName.equals( parentPk.firstName ) ) return false;
 			if ( !lastName.equals( parentPk.lastName ) ) return false;
 
 			return true;
 		}
 
 		public int hashCode() {
 			int result;
 			result = firstName.hashCode();
 			result = 29 * result + lastName.hashCode();
 			return result;
 		}
 	}
 
 	@Entity ( name = "CardField" )
 	public static class CardField implements Serializable {
 
 		@EmbeddedId
 		private CardFieldPK primaryKey;
 
 		CardField(Card card, int fieldNumber) {
 			this.primaryKey = new CardFieldPK(card, fieldNumber);
 		}
 
 		CardField() {
 		}
 
 		public CardFieldPK getPrimaryKey() {
 			return primaryKey;
 		}
 
 		public void setPrimaryKey(CardFieldPK primaryKey) {
 			this.primaryKey = primaryKey;
 		}
 
 		@Override
 		public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			CardField cardField = (CardField) o;
 
 			if ( primaryKey != null ? !primaryKey.equals( cardField.primaryKey ) : cardField.primaryKey != null ) {
 				return false;
 			}
 
 			return true;
 		}
 
 		@Override
 		public int hashCode() {
 			return primaryKey != null ? primaryKey.hashCode() : 0;
 		}
 	}
 
 	@Embeddable
 	public static class CardFieldPK implements Serializable {
 		@ManyToOne(optional = false)
 		private Card card;
 
 		private int fieldNumber;
 
 		public CardFieldPK(Card card, int fieldNumber) {
 			this.card = card;
 			this.fieldNumber = fieldNumber;
 		}
 
 		CardFieldPK() {
 		}
 
 		public Card getCard() {
 			return card;
 		}
 
 		public void setCard(Card card) {
 			this.card = card;
 		}
 
 		public int getFieldNumber() {
 			return fieldNumber;
 		}
 
 		public void setFieldNumber(int fieldNumber) {
 			this.fieldNumber = fieldNumber;
 		}
 
 		@Override
 		public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			CardFieldPK that = (CardFieldPK) o;
 
 			if ( fieldNumber != that.fieldNumber ) {
 				return false;
 			}
 			if ( card != null ? !card.equals( that.card ) : that.card != null ) {
 				return false;
 			}
 
 			return true;
 		}
 
 		@Override
 		public int hashCode() {
 			int result = card != null ? card.hashCode() : 0;
 			result = 31 * result + fieldNumber;
 			return result;
 		}
 	}
 
 	@Entity ( name = "Card" )
 	public static class Card implements Serializable {
 		@Id
 		private String id;
 
 		public Card(String id) {
 			this();
 			this.id = id;
 		}
 
 		Card() {
 		}
 
 		public String getId() {
 			return id;
 		}
 
 		public void setId(String id) {
 			this.id = id;
 		}
 
 		@Override
 		public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			Card card = (Card) o;
 
 			if ( !id.equals( card.id ) ) {
 				return false;
 			}
 
 			return true;
 		}
 
 		@Override
 		public int hashCode() {
 			return id.hashCode();
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java
index bccdcf5584..cbdb24cf6e 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/EntityAssociationResultSetProcessorTest.java
@@ -1,298 +1,292 @@
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
 package org.hibernate.loader;
 
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
-import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
 import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
 import org.hibernate.loader.plan.spi.LoadPlan;
-import org.hibernate.loader.spi.NoOpLoadPlanAdvisor;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gail Badner
  */
 public class EntityAssociationResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Message.class, Poster.class, ReportedMessage.class };
 	}
 
 	@Test
 	public void testManyToOneEntityProcessing() throws Exception {
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( Message.class.getName() );
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Message message = new Message( 1, "the message" );
 		Poster poster = new Poster( 2, "the poster" );
 		session.save( message );
 		session.save( poster );
 		message.poster = poster;
 		poster.messages.add( message );
 		session.getTransaction().commit();
 		session.close();
 
 		{
 			final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
-			final AliasResolutionContext aliasResolutionContext = new AliasResolutionContextImpl( sessionFactory(), 0 );
 
-			final String sql = Helper.INSTANCE.generateSql( sessionFactory(), plan, aliasResolutionContext );
+			final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
+			final String sql = queryDetails.getSqlStatement();
+			final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
-			final ResultSetProcessorImpl resultSetProcessor = new ResultSetProcessorImpl( plan, true );
 			final List results = new ArrayList();
 
 			final Session workSession = openSession();
 			workSession.beginTransaction();
 			workSession.doWork(
 					new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							PreparedStatement ps = connection.prepareStatement( sql );
 							ps.setInt( 1, 1 );
 							ResultSet resultSet = ps.executeQuery();
 							results.addAll(
 									resultSetProcessor.extractResults(
-											NoOpLoadPlanAdvisor.INSTANCE,
 											resultSet,
 											(SessionImplementor) workSession,
 											new QueryParameters(),
 											new NamedParameterContext() {
 												@Override
 												public int[] getNamedParameterLocations(String name) {
 													return new int[0];
 												}
 											},
-											aliasResolutionContext,
 											true,
 											false,
 											null,
 											null
 									)
 							);
 							resultSet.close();
 							ps.close();
 						}
 					}
 			);
 			assertEquals( 1, results.size() );
 			Object result = results.get( 0 );
 			assertNotNull( result );
 
 			Message workMessage = ExtraAssertions.assertTyping( Message.class, result );
 			assertEquals( 1, workMessage.mid.intValue() );
 			assertEquals( "the message", workMessage.msgTxt );
 			assertTrue( Hibernate.isInitialized( workMessage.poster ) );
 			Poster workPoster = workMessage.poster;
 			assertEquals( 2, workPoster.pid.intValue() );
 			assertEquals( "the poster", workPoster.name );
 			assertFalse( Hibernate.isInitialized( workPoster.messages ) );
 
 			workSession.getTransaction().commit();
 			workSession.close();
 		}
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.createQuery( "delete Message" ).executeUpdate();
 		session.createQuery( "delete Poster" ).executeUpdate();
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Test
 	public void testNestedManyToOneEntityProcessing() throws Exception {
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( ReportedMessage.class.getName() );
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Message message = new Message( 1, "the message" );
 		Poster poster = new Poster( 2, "the poster" );
 		session.save( message );
 		session.save( poster );
 		message.poster = poster;
 		poster.messages.add( message );
 		ReportedMessage reportedMessage = new ReportedMessage( 0, "inappropriate", message );
 		session.save( reportedMessage );
 		session.getTransaction().commit();
 		session.close();
 
 		{
 			final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
-			final AliasResolutionContext aliasResolutionContext = new AliasResolutionContextImpl( sessionFactory(), 0 );
 
-			final String sql = Helper.INSTANCE.generateSql( sessionFactory(), plan, aliasResolutionContext );
+			final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
+			final String sql = queryDetails.getSqlStatement();
+			final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
-			final ResultSetProcessorImpl resultSetProcessor = new ResultSetProcessorImpl( plan, true );
 			final List results = new ArrayList();
 
 			final Session workSession = openSession();
 			workSession.beginTransaction();
 			workSession.doWork(
 					new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							PreparedStatement ps = connection.prepareStatement( sql );
 							ps.setInt( 1, 0 );
 							ResultSet resultSet = ps.executeQuery();
 							results.addAll(
 									resultSetProcessor.extractResults(
-											NoOpLoadPlanAdvisor.INSTANCE,
 											resultSet,
 											(SessionImplementor) workSession,
 											new QueryParameters(),
 											new NamedParameterContext() {
 												@Override
 												public int[] getNamedParameterLocations(String name) {
 													return new int[0];
 												}
 											},
-											aliasResolutionContext,
 											true,
 											false,
 											null,
 											null
 									)
 							);
 							resultSet.close();
 							ps.close();
 						}
 					}
 			);
 			assertEquals( 1, results.size() );
 			Object result = results.get( 0 );
 			assertNotNull( result );
 
 			ReportedMessage workReportedMessage = ExtraAssertions.assertTyping( ReportedMessage.class, result );
 			assertEquals( 0, workReportedMessage.id.intValue() );
 			assertEquals( "inappropriate", workReportedMessage.reason );
 			Message workMessage = workReportedMessage.message;
 			assertNotNull( workMessage );
 			assertTrue( Hibernate.isInitialized( workMessage ) );
 			assertEquals( 1, workMessage.mid.intValue() );
 			assertEquals( "the message", workMessage.msgTxt );
 			assertTrue( Hibernate.isInitialized( workMessage.poster ) );
 			Poster workPoster = workMessage.poster;
 			assertEquals( 2, workPoster.pid.intValue() );
 			assertEquals( "the poster", workPoster.name );
 			assertFalse( Hibernate.isInitialized( workPoster.messages ) );
 
 			workSession.getTransaction().commit();
 			workSession.close();
 		}
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.createQuery( "delete ReportedMessage" ).executeUpdate();
 		session.createQuery( "delete Message" ).executeUpdate();
 		session.createQuery( "delete Poster" ).executeUpdate();
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Entity( name = "ReportedMessage" )
 	public static class ReportedMessage {
 		@Id
 		private Integer id;
 		private String reason;
 		@ManyToOne
 		@JoinColumn
 		private Message message;
 
 		public ReportedMessage() {}
 
 		public ReportedMessage(Integer id, String reason, Message message) {
 			this.id = id;
 			this.reason = reason;
 			this.message = message;
 		}
 	}
 
 	@Entity( name = "Message" )
 	public static class Message {
 		@Id
 		private Integer mid;
 		private String msgTxt;
 		@ManyToOne( cascade = CascadeType.MERGE )
 		@JoinColumn
 		private Poster poster;
 
 		public Message() {}
 
 		public Message(Integer mid, String msgTxt) {
 			this.mid = mid;
 			this.msgTxt = msgTxt;
 		}
 	}
 
 	@Entity( name = "Poster" )
 	public static class Poster {
 		@Id
 		private Integer pid;
 		private String name;
 		@OneToMany(mappedBy = "poster")
 		private List<Message> messages = new ArrayList<Message>();
 
 		public Poster() {}
 
 		public Poster(Integer pid, String name) {
 			this.pid = pid;
 			this.name = name;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/EntityWithNonLazyCollectionResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/EntityWithNonLazyCollectionResultSetProcessorTest.java
index 5fc9a90b82..ceb968eee7 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/EntityWithNonLazyCollectionResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/EntityWithNonLazyCollectionResultSetProcessorTest.java
@@ -1,166 +1,163 @@
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
 package org.hibernate.loader;
 
 import javax.persistence.CollectionTable;
 import javax.persistence.Column;
 import javax.persistence.ElementCollection;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
-import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
 import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
 import org.hibernate.loader.plan.spi.LoadPlan;
-import org.hibernate.loader.spi.NoOpLoadPlanAdvisor;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gail Badner
  */
 public class EntityWithNonLazyCollectionResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Person.class };
 	}
 
 	@Test
 	public void testEntityWithSet() throws Exception {
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( Person.class.getName() );
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Person person = new Person();
 		person.id = 1;
 		person.name = "John Doe";
 		person.nickNames.add( "Jack" );
 		person.nickNames.add( "Johnny" );
 		session.save( person );
 		session.getTransaction().commit();
 		session.close();
 
 		{
+
 			final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
-			final AliasResolutionContext aliasResolutionContext = new AliasResolutionContextImpl( sessionFactory(), 0 );
 
-			final String sql = Helper.INSTANCE.generateSql( sessionFactory(), plan, aliasResolutionContext );
+			final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
+			final String sql = queryDetails.getSqlStatement();
+			final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
-			final ResultSetProcessorImpl resultSetProcessor = new ResultSetProcessorImpl( plan, true );
 			final List results = new ArrayList();
 
 			final Session workSession = openSession();
 			workSession.beginTransaction();
 			workSession.doWork(
 					new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							PreparedStatement ps = connection.prepareStatement( sql );
 							ps.setInt( 1, 1 );
 							ResultSet resultSet = ps.executeQuery();
 							results.addAll(
 									resultSetProcessor.extractResults(
-											NoOpLoadPlanAdvisor.INSTANCE,
 											resultSet,
 											(SessionImplementor) workSession,
 											new QueryParameters(),
 											new NamedParameterContext() {
 												@Override
 												public int[] getNamedParameterLocations(String name) {
 													return new int[0];
 												}
 											},
-											aliasResolutionContext,
 											true,
 											false,
 											null,
 											null
 									)
 							);
 							resultSet.close();
 							ps.close();
 						}
 					}
 			);
 			assertEquals( 2, results.size() );
 			Object result1 = results.get( 0 );
 			assertSame( result1, results.get( 1 ) );
 			assertNotNull( result1 );
 
 			Person workPerson = ExtraAssertions.assertTyping( Person.class, result1 );
 			assertEquals( 1, workPerson.id.intValue() );
 			assertEquals( person.name, workPerson.name );
 			assertTrue( Hibernate.isInitialized( workPerson.nickNames ) );
 			assertEquals( 2, workPerson.nickNames.size() );
 			assertEquals( person.nickNames, workPerson.nickNames );
 			workSession.getTransaction().commit();
 			workSession.close();
 		}
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.delete( person );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Entity( name = "Person" )
 	public static class Person {
 		@Id
 		private Integer id;
 		private String name;
 		@ElementCollection( fetch = FetchType.EAGER )
 		@CollectionTable( name = "nick_names", joinColumns = @JoinColumn( name = "pid" ) )
 		@Column( name = "nick" )
 		private Set<String> nickNames = new HashSet<String>();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/EntityWithNonLazyOneToManyListResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/EntityWithNonLazyOneToManyListResultSetProcessorTest.java
index a061775a74..681f6658ce 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/EntityWithNonLazyOneToManyListResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/EntityWithNonLazyOneToManyListResultSetProcessorTest.java
@@ -1,199 +1,196 @@
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
 package org.hibernate.loader;
 
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
-import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
 import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
 import org.hibernate.loader.plan.spi.LoadPlan;
-import org.hibernate.loader.spi.NoOpLoadPlanAdvisor;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Gail Badner
  */
 public class EntityWithNonLazyOneToManyListResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Poster.class, Message.class };
 	}
 
 	@Test
 	public void testEntityWithList() throws Exception {
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( Poster.class.getName() );
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Poster poster = new Poster();
 		poster.pid = 0;
 		poster.name = "John Doe";
 		Message message1 = new Message();
 		message1.mid = 1;
 		message1.msgTxt = "Howdy!";
 		message1.poster = poster;
 		poster.messages.add( message1 );
 		Message message2 = new Message();
 		message2.mid = 2;
 		message2.msgTxt = "Bye!";
 		message2.poster = poster;
 		poster.messages.add( message2 );
 		session.save( poster );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		Poster posterGotten = (Poster) session.get( Poster.class, poster.pid );
 		assertEquals( 0, posterGotten.pid.intValue() );
 		assertEquals( poster.name, posterGotten.name );
 		assertTrue( Hibernate.isInitialized( posterGotten.messages ) );
 		assertEquals( 2, posterGotten.messages.size() );
 		assertEquals( message1.msgTxt, posterGotten.messages.get( 0 ).msgTxt );
 		assertEquals( message2.msgTxt, posterGotten.messages.get( 1 ).msgTxt );
 		assertSame( posterGotten, posterGotten.messages.get( 0 ).poster );
 		assertSame( posterGotten, posterGotten.messages.get( 1 ).poster );
 		session.getTransaction().commit();
 		session.close();
 
 		{
+
 			final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
-			final AliasResolutionContext aliasResolutionContext = new AliasResolutionContextImpl( sessionFactory(), 0 );
 
-			final String sql = Helper.INSTANCE.generateSql( sessionFactory(), plan, aliasResolutionContext );
+			final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
+			final String sql = queryDetails.getSqlStatement();
+			final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
-			final ResultSetProcessorImpl resultSetProcessor = new ResultSetProcessorImpl( plan, true );
 			final List results = new ArrayList();
 
 			final Session workSession = openSession();
 			workSession.beginTransaction();
 			workSession.doWork(
 					new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							PreparedStatement ps = connection.prepareStatement( sql );
 							ps.setInt( 1, 0 );
 							ResultSet resultSet = ps.executeQuery();
 							results.addAll(
 									resultSetProcessor.extractResults(
-											NoOpLoadPlanAdvisor.INSTANCE,
 											resultSet,
 											(SessionImplementor) workSession,
 											new QueryParameters(),
 											new NamedParameterContext() {
 												@Override
 												public int[] getNamedParameterLocations(String name) {
 													return new int[0];
 												}
 											},
-											aliasResolutionContext,
 											true,
 											false,
 											null,
 											null
 									)
 							);
 							resultSet.close();
 							ps.close();
 						}
 					}
 			);
 			assertEquals( 2, results.size() );
 			Object result1 = results.get( 0 );
 			assertNotNull( result1 );
 			assertSame( result1, results.get( 1 ) );
 
 			Poster workPoster = ExtraAssertions.assertTyping( Poster.class, result1 );
 			assertEquals( 0, workPoster.pid.intValue() );
 			assertEquals( poster.name, workPoster.name );
 			assertTrue( Hibernate.isInitialized( workPoster.messages ) );
 			assertEquals( 2, workPoster.messages.size() );
 			assertTrue( Hibernate.isInitialized( posterGotten.messages ) );
 			assertEquals( 2, workPoster.messages.size() );
 			assertEquals( message1.msgTxt, workPoster.messages.get( 0 ).msgTxt );
 			assertEquals( message2.msgTxt, workPoster.messages.get( 1 ).msgTxt );
 			assertSame( workPoster, workPoster.messages.get( 0 ).poster );
 			assertSame( workPoster, workPoster.messages.get( 1 ).poster );
 			workSession.getTransaction().commit();
 			workSession.close();
 		}
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.delete( poster );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Entity( name = "Message" )
 	public static class Message {
 		@Id
 		private Integer mid;
 		private String msgTxt;
 		@ManyToOne
 		@JoinColumn
 		private Poster poster;
 	}
 
 	@Entity( name = "Poster" )
 	public static class Poster {
 		@Id
 		private Integer pid;
 		private String name;
 		@OneToMany(mappedBy = "poster", fetch = FetchType.EAGER, cascade = CascadeType.ALL )
 		private List<Message> messages = new ArrayList<Message>();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/EntityWithNonLazyOneToManySetResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/EntityWithNonLazyOneToManySetResultSetProcessorTest.java
index 5a84e8a3a4..d54b3e3a6b 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/EntityWithNonLazyOneToManySetResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/EntityWithNonLazyOneToManySetResultSetProcessorTest.java
@@ -1,218 +1,214 @@
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
 package org.hibernate.loader;
 
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.FetchType;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.Hibernate;
 import org.hibernate.Session;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
-import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
 import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
 import org.hibernate.loader.plan.spi.LoadPlan;
-import org.hibernate.loader.spi.NoOpLoadPlanAdvisor;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;
 
 /**
  * @author Gail Badner
  */
 public class EntityWithNonLazyOneToManySetResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Poster.class, Message.class };
 	}
 
 	@Test
 	public void testEntityWithSet() throws Exception {
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( Poster.class.getName() );
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		Poster poster = new Poster();
 		poster.pid = 0;
 		poster.name = "John Doe";
 		Message message1 = new Message();
 		message1.mid = 1;
 		message1.msgTxt = "Howdy!";
 		message1.poster = poster;
 		poster.messages.add( message1 );
 		Message message2 = new Message();
 		message2.mid = 2;
 		message2.msgTxt = "Bye!";
 		message2.poster = poster;
 		poster.messages.add( message2 );
 		session.save( poster );
 		session.getTransaction().commit();
 		session.close();
 
 		session = openSession();
 		session.beginTransaction();
 		Poster posterGotten = (Poster) session.get( Poster.class, poster.pid );
 		assertEquals( 0, posterGotten.pid.intValue() );
 		assertEquals( poster.name, posterGotten.name );
 		assertTrue( Hibernate.isInitialized( posterGotten.messages ) );
 		assertEquals( 2, posterGotten.messages.size() );
 		for ( Message message : posterGotten.messages ) {
 			if ( message.mid == 1 ) {
 				assertEquals( message1.msgTxt, message.msgTxt );
 			}
 			else if ( message.mid == 2 ) {
 				assertEquals( message2.msgTxt, message.msgTxt );
 			}
 			else {
 				fail( "unexpected message id." );
 			}
 			assertSame( posterGotten, message.poster );
 		}
 		session.getTransaction().commit();
 		session.close();
 
 		{
 			final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
-			final AliasResolutionContext aliasResolutionContext = new AliasResolutionContextImpl( sessionFactory(), 0 );
 
-			final String sql = Helper.INSTANCE.generateSql( sessionFactory(), plan, aliasResolutionContext );
+			final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
+			final String sql = queryDetails.getSqlStatement();
+			final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
-			final ResultSetProcessorImpl resultSetProcessor = new ResultSetProcessorImpl( plan, true );
 			final List results = new ArrayList();
 
 			final Session workSession = openSession();
 			workSession.beginTransaction();
 			workSession.doWork(
 					new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							PreparedStatement ps = connection.prepareStatement( sql );
 							ps.setInt( 1, 0 );
 							ResultSet resultSet = ps.executeQuery();
 							results.addAll(
 									resultSetProcessor.extractResults(
-											NoOpLoadPlanAdvisor.INSTANCE,
 											resultSet,
 											(SessionImplementor) workSession,
 											new QueryParameters(),
 											new NamedParameterContext() {
 												@Override
 												public int[] getNamedParameterLocations(String name) {
 													return new int[0];
 												}
 											},
-											aliasResolutionContext,
 											true,
 											false,
 											null,
 											null
 									)
 							);
 							resultSet.close();
 							ps.close();
 						}
 					}
 			);
 			assertEquals( 2, results.size() );
 			Object result1 = results.get( 0 );
 			assertNotNull( result1 );
 			assertSame( result1, results.get( 1 ) );
 
 			Poster workPoster = ExtraAssertions.assertTyping( Poster.class, result1 );
 			assertEquals( 0, workPoster.pid.intValue() );
 			assertEquals( poster.name, workPoster.name );
 			assertTrue( Hibernate.isInitialized( workPoster.messages ) );
 			assertEquals( 2, workPoster.messages.size() );
 			assertTrue( Hibernate.isInitialized( posterGotten.messages ) );
 			assertEquals( 2, workPoster.messages.size() );
 			for ( Message message : workPoster.messages ) {
 				if ( message.mid == 1 ) {
 					assertEquals( message1.msgTxt, message.msgTxt );
 				}
 				else if ( message.mid == 2 ) {
 					assertEquals( message2.msgTxt, message.msgTxt );
 				}
 				else {
 					fail( "unexpected message id." );
 				}
 				assertSame( workPoster, message.poster );
 			}
 			workSession.getTransaction().commit();
 			workSession.close();
 		}
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.delete( poster );
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Entity( name = "Message" )
 	public static class Message {
 		@Id
 		private Integer mid;
 		private String msgTxt;
 		@ManyToOne
 		@JoinColumn
 		private Poster poster;
 	}
 
 	@Entity( name = "Poster" )
 	public static class Poster {
 		@Id
 		private Integer pid;
 		private String name;
 		@OneToMany(mappedBy = "poster", fetch = FetchType.EAGER, cascade = CascadeType.ALL )
 		private Set<Message> messages = new HashSet<Message>();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/Helper.java b/hibernate-core/src/test/java/org/hibernate/loader/Helper.java
index 257b899b15..7508071e53 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/Helper.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/Helper.java
@@ -1,86 +1,96 @@
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
 package org.hibernate.loader;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.plan.exec.query.internal.EntityLoadQueryBuilderImpl;
 import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
 import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.exec.spi.EntityLoadQueryDetails;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.spi.LoadPlan;
-import org.hibernate.loader.plan.spi.build.LoadPlanBuilder;
+import org.hibernate.loader.plan.spi.build.MetadataDrivenLoadPlanBuilder;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * @author Steve Ebersole
  */
 public class Helper implements QueryBuildingParameters {
 	/**
 	 * Singleton access
 	 */
 	public static final Helper INSTANCE = new Helper();
 
 	private Helper() {
 	}
 
 	public LoadPlan buildLoadPlan(SessionFactoryImplementor sf, EntityPersister entityPersister) {
 		final SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 				sf,
 				LoadQueryInfluencers.NONE
 		);
-		return LoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
+		return MetadataDrivenLoadPlanBuilder.buildRootEntityLoadPlan( strategy, entityPersister );
+	}
+
+	public EntityLoadQueryDetails buildLoadQueryDetails(LoadPlan loadPlan, SessionFactoryImplementor sf) {
+		return EntityLoadQueryDetails.makeForBatching(
+				loadPlan,
+				null,
+				this,
+				sf
+		);
 	}
 
 	public String generateSql(SessionFactoryImplementor sf, LoadPlan plan, AliasResolutionContext aliasResolutionContext) {
 		return EntityLoadQueryBuilderImpl.INSTANCE.generateSql(
 				plan,
 				sf,
 				this,
 				aliasResolutionContext
 		);
 	}
 
 	@Override
 	public LoadQueryInfluencers getQueryInfluencers() {
 		return LoadQueryInfluencers.NONE;
 	}
 
 	@Override
 	public int getBatchSize() {
 		return 1;
 	}
 
 	@Override
 	public LockMode getLockMode() {
 		return null;
 	}
 
 	@Override
 	public LockOptions getLockOptions() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/NonEncapsulatedCompositeIdResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/NonEncapsulatedCompositeIdResultSetProcessorTest.java
index 81c2833de9..9889974b5b 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/NonEncapsulatedCompositeIdResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/NonEncapsulatedCompositeIdResultSetProcessorTest.java
@@ -1,215 +1,211 @@
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
 package org.hibernate.loader;
 
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.LockOptions;
 import org.hibernate.Session;
 import org.hibernate.Transaction;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
-import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
 import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
 import org.hibernate.loader.plan.spi.LoadPlan;
-import org.hibernate.loader.spi.NoOpLoadPlanAdvisor;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.Type;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.test.onetoone.formula.Address;
 import org.hibernate.test.onetoone.formula.Person;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Steve Ebersole
  */
 public class NonEncapsulatedCompositeIdResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected String[] getMappings() {
 		return new String[] { "onetoone/formula/Person.hbm.xml" };
 	}
 
 	@Test
 	public void testCompositeIdWithKeyManyToOne() throws Exception {
 		final String personId = "John Doe";
 
 		Person p = new Person();
 		p.setName( personId );
 		final Address a = new Address();
 		a.setPerson( p );
 		p.setAddress( a );
 		a.setType( "HOME" );
 		a.setStreet( "Main St" );
 		a.setState( "Sweet Home Alabama" );
 		a.setZip( "3181" );
 
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		s.persist( p );
 		t.commit();
 		s.close();
 
 		final EntityPersister personPersister = sessionFactory().getEntityPersister( Person.class.getName() );
 		final EntityPersister addressPersister = sessionFactory().getEntityPersister( Address.class.getName() );
 
 		{
 			final List results = getResults(
 					addressPersister,
 					new Callback() {
 						@Override
 						public void bind(PreparedStatement ps) throws SQLException {
 							ps.setString( 1, personId );
 							ps.setString( 2, "HOME" );
 						}
 
 						@Override
 						public QueryParameters getQueryParameters() {
 							QueryParameters qp = new QueryParameters();
 							qp.setPositionalParameterTypes( new Type[] { addressPersister.getIdentifierType() } );
 							qp.setPositionalParameterValues( new Object[] { a } );
 							qp.setOptionalObject( a );
 							qp.setOptionalEntityName( addressPersister.getEntityName() );
 							qp.setOptionalId( a );
 							qp.setLockOptions( LockOptions.NONE );
 							return qp;
 						}
 
 					}
 			);
 			assertEquals( 1, results.size() );
 			Object result = results.get( 0 );
 			assertNotNull( result );
 		}
 
 		// test loading the Person (the entity with normal id def, but mixed composite fk to Address)
 		{
 			final List results = getResults(
 					personPersister,
 					new Callback() {
 						@Override
 						public void bind(PreparedStatement ps) throws SQLException {
 							ps.setString( 1, personId );
 						}
 
 						@Override
 						public QueryParameters getQueryParameters() {
 							QueryParameters qp = new QueryParameters();
 							qp.setPositionalParameterTypes( new Type[] { personPersister.getIdentifierType() } );
 							qp.setPositionalParameterValues( new Object[] { personId } );
 							qp.setOptionalObject( null );
 							qp.setOptionalEntityName( personPersister.getEntityName() );
 							qp.setOptionalId( personId );
 							qp.setLockOptions( LockOptions.NONE );
 							return qp;
 						}
 
 					}
 			);
 			assertEquals( 1, results.size() );
 			Object result = results.get( 0 );
 			assertNotNull( result );
 		}
 
 //		CardField cardFieldWork = ExtraAssertions.assertTyping( CardField.class, result );
 //		assertEquals( cardFieldGotten, cardFieldWork );
 
 		// clean up test data
 		s = openSession();
 		s.beginTransaction();
 		s.createQuery( "delete Address" ).executeUpdate();
 		s.createQuery( "delete Person" ).executeUpdate();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	private List getResults(final EntityPersister entityPersister, final Callback callback) {
 		final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
-		final AliasResolutionContext aliasResolutionContext = new AliasResolutionContextImpl( sessionFactory(), 0 );
 
-		final String sql = Helper.INSTANCE.generateSql( sessionFactory(), plan, aliasResolutionContext );
+		final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
+		final String sql = queryDetails.getSqlStatement();
+		final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
-		final ResultSetProcessorImpl resultSetProcessor = new ResultSetProcessorImpl( plan, true );
 		final List results = new ArrayList();
 
 		final Session workSession = openSession();
 		workSession.beginTransaction();
 		workSession.doWork(
 				new Work() {
 					@Override
 					public void execute(Connection connection) throws SQLException {
 						System.out.println( "SQL : " + sql );
 						PreparedStatement ps = connection.prepareStatement( sql );
 						callback.bind( ps );
 						ResultSet resultSet = ps.executeQuery();
 						//callback.beforeExtractResults( workSession );
 						results.addAll(
 								resultSetProcessor.extractResults(
-										NoOpLoadPlanAdvisor.INSTANCE,
 										resultSet,
 										(SessionImplementor) workSession,
 										callback.getQueryParameters(),
 										new NamedParameterContext() {
 											@Override
 											public int[] getNamedParameterLocations(String name) {
 												return new int[0];
 											}
 										},
-										aliasResolutionContext,
 										true,
 										false,
 										null,
 										null
 								)
 						);
 						resultSet.close();
 						ps.close();
 					}
 				}
 		);
 		workSession.getTransaction().commit();
 		workSession.close();
 
 		return results;
 	}
 
 
 	private interface Callback {
 		void bind(PreparedStatement ps) throws SQLException;
 		QueryParameters getQueryParameters();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/SimpleResultSetProcessorTest.java b/hibernate-core/src/test/java/org/hibernate/loader/SimpleResultSetProcessorTest.java
index e0cdd76818..c80c587067 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/SimpleResultSetProcessorTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/SimpleResultSetProcessorTest.java
@@ -1,151 +1,147 @@
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
 package org.hibernate.loader;
 
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import java.sql.Connection;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.Session;
 import org.hibernate.engine.spi.QueryParameters;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.jdbc.Work;
-import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
-import org.hibernate.loader.plan.exec.internal.AliasResolutionContextImpl;
+import org.hibernate.loader.plan.exec.process.spi.ResultSetProcessor;
 import org.hibernate.loader.plan.exec.query.spi.NamedParameterContext;
-import org.hibernate.loader.plan.exec.spi.AliasResolutionContext;
+import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
 import org.hibernate.loader.plan.spi.LoadPlan;
-import org.hibernate.loader.spi.NoOpLoadPlanAdvisor;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Steve Ebersole
  */
 public class SimpleResultSetProcessorTest extends BaseCoreFunctionalTestCase {
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { SimpleEntity.class };
 	}
 
 	@Test
 	public void testSimpleEntityProcessing() throws Exception {
 		final EntityPersister entityPersister = sessionFactory().getEntityPersister( SimpleEntity.class.getName() );
 
 		// create some test data
 		Session session = openSession();
 		session.beginTransaction();
 		session.save( new SimpleEntity( 1, "the only" ) );
 		session.getTransaction().commit();
 		session.close();
 
 		{
 			final LoadPlan plan = Helper.INSTANCE.buildLoadPlan( sessionFactory(), entityPersister );
-			final AliasResolutionContext aliasResolutionContext = new AliasResolutionContextImpl( sessionFactory(), 0 );
 
-			final String sql = Helper.INSTANCE.generateSql( sessionFactory(), plan, aliasResolutionContext );
+			final LoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( plan, sessionFactory() );
+			final String sql = queryDetails.getSqlStatement();
+			final ResultSetProcessor resultSetProcessor = queryDetails.getResultSetProcessor();
 
-			final ResultSetProcessorImpl resultSetProcessor = new ResultSetProcessorImpl( plan, true );
 			final List results = new ArrayList();
 
 			final Session workSession = openSession();
 			workSession.beginTransaction();
 			workSession.doWork(
 					new Work() {
 						@Override
 						public void execute(Connection connection) throws SQLException {
 							( (SessionImplementor) workSession ).getFactory().getJdbcServices().getSqlStatementLogger().logStatement( sql );
 							PreparedStatement ps = connection.prepareStatement( sql );
 							ps.setInt( 1, 1 );
 							ResultSet resultSet = ps.executeQuery();
 							results.addAll(
 									resultSetProcessor.extractResults(
-											NoOpLoadPlanAdvisor.INSTANCE,
 											resultSet,
 											(SessionImplementor) workSession,
 											new QueryParameters(),
 											new NamedParameterContext() {
 												@Override
 												public int[] getNamedParameterLocations(String name) {
 													return new int[0];
 												}
 											},
-											aliasResolutionContext,
 											true,
 											false,
 											null,
 											null
 									)
 							);
 							resultSet.close();
 							ps.close();
 						}
 					}
 			);
 			assertEquals( 1, results.size() );
 			Object result = results.get( 0 );
 			assertNotNull( result );
 
 			SimpleEntity workEntity = ExtraAssertions.assertTyping( SimpleEntity.class, result );
 			assertEquals( 1, workEntity.id.intValue() );
 			assertEquals( "the only", workEntity.name );
 			workSession.getTransaction().commit();
 			workSession.close();
 		}
 
 		// clean up test data
 		session = openSession();
 		session.beginTransaction();
 		session.createQuery( "delete SimpleEntity" ).executeUpdate();
 		session.getTransaction().commit();
 		session.close();
 	}
 
 	@Entity(name = "SimpleEntity")
 	public static class SimpleEntity {
 		@Id public Integer id;
 		public String name;
 
 		public SimpleEntity() {
 		}
 
 		public SimpleEntity(Integer id, String name) {
 			this.id = id;
 			this.name = name;
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java b/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java
index 28bb284be9..3c5e71d2ce 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanBuilderTest.java
@@ -1,141 +1,141 @@
 /*
  * jDocBook, processing of DocBook sources
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
 package org.hibernate.loader.plan.spi;
 
 import javax.persistence.CascadeType;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import java.util.List;
 
 import org.hibernate.engine.spi.CascadingActions;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.loader.plan.internal.CascadeLoadPlanBuilderStrategy;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
-import org.hibernate.loader.plan.spi.build.LoadPlanBuilder;
+import org.hibernate.loader.plan.spi.build.MetadataDrivenLoadPlanBuilder;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.hibernate.testing.junit4.ExtraAssertions;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertFalse;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Steve Ebersole
  */
 public class LoadPlanBuilderTest extends BaseCoreFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Message.class, Poster.class };
 	}
 
 	@Test
 	public void testSimpleBuild() {
 		EntityPersister ep = (EntityPersister) sessionFactory().getClassMetadata(Message.class);
 		SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 				sessionFactory(),
 				LoadQueryInfluencers.NONE
 		);
-		LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, ep );
+		LoadPlan plan = MetadataDrivenLoadPlanBuilder.buildRootEntityLoadPlan( strategy, ep );
 		assertFalse( plan.hasAnyScalarReturns() );
 		assertEquals( 1, plan.getReturns().size() );
 		Return rtn = plan.getReturns().get( 0 );
 		EntityReturn entityReturn = ExtraAssertions.assertTyping( EntityReturn.class, rtn );
 		assertNotNull( entityReturn.getFetches() );
 		assertEquals( 1, entityReturn.getFetches().length );
 		Fetch fetch = entityReturn.getFetches()[0];
 		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
 		assertNotNull( entityFetch.getFetches() );
 		assertEquals( 0, entityFetch.getFetches().length );
 	}
 
 	@Test
 	public void testCascadeBasedBuild() {
 		EntityPersister ep = (EntityPersister) sessionFactory().getClassMetadata(Message.class);
 		CascadeLoadPlanBuilderStrategy strategy = new CascadeLoadPlanBuilderStrategy(
 				CascadingActions.MERGE,
 				sessionFactory(),
 				LoadQueryInfluencers.NONE
 		);
-		LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, ep );
+		LoadPlan plan = MetadataDrivenLoadPlanBuilder.buildRootEntityLoadPlan( strategy, ep );
 		assertFalse( plan.hasAnyScalarReturns() );
 		assertEquals( 1, plan.getReturns().size() );
 		Return rtn = plan.getReturns().get( 0 );
 		EntityReturn entityReturn = ExtraAssertions.assertTyping( EntityReturn.class, rtn );
 		assertNotNull( entityReturn.getFetches() );
 		assertEquals( 1, entityReturn.getFetches().length );
 		Fetch fetch = entityReturn.getFetches()[0];
 		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
 		assertNotNull( entityFetch.getFetches() );
 		assertEquals( 0, entityFetch.getFetches().length );
 	}
 
 	@Test
 	public void testCollectionInitializerCase() {
 		CollectionPersister cp = sessionFactory().getCollectionPersister( Poster.class.getName() + ".messages" );
 		SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy(
 				sessionFactory(),
 				LoadQueryInfluencers.NONE
 		);
-		LoadPlan plan = LoadPlanBuilder.buildRootCollectionLoadPlan( strategy, cp );
+		LoadPlan plan = MetadataDrivenLoadPlanBuilder.buildRootCollectionLoadPlan( strategy, cp );
 		assertFalse( plan.hasAnyScalarReturns() );
 		assertEquals( 1, plan.getReturns().size() );
 		Return rtn = plan.getReturns().get( 0 );
 		CollectionReturn collectionReturn = ExtraAssertions.assertTyping( CollectionReturn.class, rtn );
 
 		assertNotNull( collectionReturn.getElementGraph().getFetches() );
 		assertEquals( 1, collectionReturn.getElementGraph().getFetches().length ); // the collection elements are fetched
 		Fetch fetch = collectionReturn.getElementGraph().getFetches()[0];
 		EntityFetch entityFetch = ExtraAssertions.assertTyping( EntityFetch.class, fetch );
 		assertNotNull( entityFetch.getFetches() );
 		assertEquals( 0, entityFetch.getFetches().length );
 	}
 
 	@Entity( name = "Message" )
 	public static class Message {
 		@Id
 		private Integer mid;
 		private String msgTxt;
 		@ManyToOne( cascade = CascadeType.MERGE )
 		@JoinColumn
 		private Poster poster;
 	}
 
 	@Entity( name = "Poster" )
 	public static class Poster {
 		@Id
 		private Integer pid;
 		private String name;
 		@OneToMany(mappedBy = "poster")
 		private List<Message> messages;
 	}
 
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanStructureAssertionHelper.java b/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanStructureAssertionHelper.java
index a828a7a784..261a9cd76a 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanStructureAssertionHelper.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanStructureAssertionHelper.java
@@ -1,125 +1,133 @@
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
 package org.hibernate.loader.plan.spi;
 
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.loader.JoinWalker;
 import org.hibernate.loader.entity.EntityJoinWalker;
-import org.hibernate.loader.entity.EntityLoader;
 import org.hibernate.loader.plan.exec.query.spi.QueryBuildingParameters;
-import org.hibernate.loader.plan.exec.spi.LoadQueryDetails;
+import org.hibernate.loader.plan.exec.spi.EntityLoadQueryDetails;
 import org.hibernate.loader.plan.internal.SingleRootReturnLoadPlanBuilderStrategy;
-import org.hibernate.loader.plan.spi.build.LoadPlanBuilder;
+import org.hibernate.loader.plan.spi.build.MetadataDrivenLoadPlanBuilder;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 
 /**
  * Perform assertions based on a LoadPlan, specifically against the outputs/expectations of the legacy Loader approach.
  * <p/>
  * Mainly this is intended to be a transitory set of help since it is expected that Loader will go away replaced by
  * LoadPlans, QueryBuilders and ResultSetProcessors.  For now I want to make sure that the outputs (e.g., the SQL,
  * the extraction aliases) are the same given the same input.  That makes sure we have the best possibility of success
  * in designing and implementing the "replacement parts".
  *
  * @author Steve Ebersole
  */
 public class LoadPlanStructureAssertionHelper {
 	/**
 	 * Singleton access to the helper
 	 */
 	public static final LoadPlanStructureAssertionHelper INSTANCE = new LoadPlanStructureAssertionHelper();
 
 	/**
 	 * Performs a basic comparison.  Builds a LoadPlan for the given persister and compares it against the
 	 * expectations according to the Loader/Walker corollary.
 	 *
 	 * @param sf The SessionFactory
 	 * @param persister The entity persister for which to build a LoadPlan and compare against the Loader/Walker
 	 * expectations.
 	 */
 	public void performBasicComparison(SessionFactoryImplementor sf, OuterJoinLoadable persister) {
 		// todo : allow these to be passed in by tests?
 		final LoadQueryInfluencers influencers = LoadQueryInfluencers.NONE;
 		final LockMode lockMode = LockMode.NONE;
 		final int batchSize = 1;
 
 		// legacy Loader-based contracts...
 		final EntityJoinWalker walker = new EntityJoinWalker(
 				persister,
 				persister.getKeyColumnNames(),
 				batchSize,
 				lockMode,
 				sf,
 				influencers
 		);
 //		final EntityLoader loader = new EntityLoader( persister, lockMode, sf, influencers );
 
-		SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy( sf, influencers );
-		LoadPlan plan = LoadPlanBuilder.buildRootEntityLoadPlan( strategy, persister );
-		LoadQueryDetails details = LoadQueryDetails.makeForBatching(
-				persister.getKeyColumnNames(),
-				plan,
-				sf,
+		LoadPlan plan = buildLoadPlan( sf, persister, influencers );
+		EntityLoadQueryDetails details = EntityLoadQueryDetails.makeForBatching(
+				plan, persister.getKeyColumnNames(),
 				new QueryBuildingParameters() {
 					@Override
 					public LoadQueryInfluencers getQueryInfluencers() {
 						return influencers;
 					}
 
 					@Override
 					public int getBatchSize() {
 						return batchSize;
 					}
 
 					@Override
 					public LockMode getLockMode() {
 						return lockMode;
 					}
 
 					@Override
 					public LockOptions getLockOptions() {
 						return null;
 					}
-				}
+				}, sf
 		);
 
 		compare( walker, details );
 	}
 
-	private void compare(JoinWalker walker, LoadQueryDetails details) {
+	public LoadPlan buildLoadPlan(
+			SessionFactoryImplementor sf,
+			OuterJoinLoadable persister,
+			LoadQueryInfluencers influencers) {
+		SingleRootReturnLoadPlanBuilderStrategy strategy = new SingleRootReturnLoadPlanBuilderStrategy( sf, influencers );
+		return MetadataDrivenLoadPlanBuilder.buildRootEntityLoadPlan( strategy, persister );
+	}
+
+	public LoadPlan buildLoadPlan(SessionFactoryImplementor sf, OuterJoinLoadable persister) {
+		return buildLoadPlan( sf, persister, LoadQueryInfluencers.NONE );
+	}
+
+	private void compare(JoinWalker walker, EntityLoadQueryDetails details) {
 		System.out.println( "------ SQL -----------------------------------------------------------------" );
 		System.out.println( "WALKER    : " + walker.getSQLString() );
 		System.out.println( "LOAD-PLAN : " + details.getSqlStatement() );
 		System.out.println( "----------------------------------------------------------------------------" );
 		System.out.println( );
 		System.out.println( "------ SUFFIXES ------------------------------------------------------------" );
 		System.out.println( "WALKER    : " + StringHelper.join( ", ",  walker.getSuffixes() ) + " : "
 									+ StringHelper.join( ", ", walker.getCollectionSuffixes() ) );
 		System.out.println( "----------------------------------------------------------------------------" );
 		System.out.println( );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanStructureAssertionTest.java b/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanStructureAssertionTest.java
index 83d71adc35..2cd61e4ea3 100644
--- a/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanStructureAssertionTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/loader/plan/spi/LoadPlanStructureAssertionTest.java
@@ -1,86 +1,206 @@
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
 package org.hibernate.loader.plan.spi;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.loader.EncapsulatedCompositeIdResultSetProcessorTest;
+import org.hibernate.loader.Helper;
+import org.hibernate.loader.plan.exec.process.internal.ResultSetProcessorImpl;
+import org.hibernate.loader.plan.exec.spi.EntityLoadQueryDetails;
+import org.hibernate.loader.plan.exec.spi.RowReader;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 
 import org.junit.Test;
 
+import junit.framework.Assert;
+
 import org.hibernate.testing.junit4.BaseUnitTestCase;
+import org.hibernate.test.annotations.cid.keymanytoone.Card;
+import org.hibernate.test.annotations.cid.keymanytoone.CardField;
+import org.hibernate.test.annotations.cid.keymanytoone.Key;
+import org.hibernate.test.annotations.cid.keymanytoone.PrimaryKey;
+
+import static junit.framework.Assert.assertNotNull;
+import static org.hibernate.testing.junit4.ExtraAssertions.assertTyping;
+import static org.junit.Assert.assertEquals;
+import static org.junit.Assert.assertSame;
 
 /**
  * Used to assert that "fetch graphs" between JoinWalker and LoadPlan are same.
  *
  * @author Steve Ebersole
  */
 public class LoadPlanStructureAssertionTest extends BaseUnitTestCase {
 	@Test
 	public void testJoinedOneToOne() {
 		// tests the mappings defined in org.hibernate.test.onetoone.joined.JoinedSubclassOneToOneTest
 		Configuration cfg = new Configuration();
 		cfg.addResource( "org/hibernate/test/onetoone/joined/Person.hbm.xml" );
 		SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
 
 //		doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( org.hibernate.test.onetoone.joined.Person.class ) );
 		doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( org.hibernate.test.onetoone.joined.Entity.class ) );
 
 //		doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( org.hibernate.test.onetoone.joined.Address.class ) );
 	}
 
 	@Test
 	public void testSpecialOneToOne() {
 		// tests the mappings defined in org.hibernate.test.onetoone.joined.JoinedSubclassOneToOneTest
 		Configuration cfg = new Configuration();
 		cfg.addResource( "org/hibernate/test/onetoone/formula/Person.hbm.xml" );
 		SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
 
 		doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( org.hibernate.test.onetoone.formula.Person.class ) );
 	}
 
 	@Test
-	public void testEncapsulatedCompositeId() {
+	public void testEncapsulatedCompositeIdNoFetches() {
+		// CardField is an entity with a composite identifier mapped via a @EmbeddedId class (CardFieldPK) defining
+		// a @ManyToOne
+		//
+		// Parent is an entity with a composite identifier mapped via a @EmbeddedId class (ParentPK) which is defined
+		// using just basic types (strings, ints, etc)
 		Configuration cfg = new Configuration();
 		cfg.addAnnotatedClass( EncapsulatedCompositeIdResultSetProcessorTest.Parent.class );
 		cfg.addAnnotatedClass( EncapsulatedCompositeIdResultSetProcessorTest.CardField.class );
 		cfg.addAnnotatedClass( EncapsulatedCompositeIdResultSetProcessorTest.Card.class );
 		SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
 		doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( EncapsulatedCompositeIdResultSetProcessorTest.CardField.class ) );
+		doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( EncapsulatedCompositeIdResultSetProcessorTest.Card.class ) );
+		doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( EncapsulatedCompositeIdResultSetProcessorTest.Parent.class ) );
+	}
+
+	@Test
+	public void testEncapsulatedCompositeIdWithFetches1() {
+		Configuration cfg = new Configuration();
+		cfg.addAnnotatedClass( Card.class );
+		cfg.addAnnotatedClass( CardField.class );
+		cfg.addAnnotatedClass( Key.class );
+		cfg.addAnnotatedClass( PrimaryKey.class );
+
+		SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
+
+		final OuterJoinLoadable cardFieldPersister = (OuterJoinLoadable) sf.getClassMetadata( CardField.class );
+		doCompare( sf, cardFieldPersister );
+		final LoadPlan loadPlan = LoadPlanStructureAssertionHelper.INSTANCE.buildLoadPlan( sf, cardFieldPersister );
+		assertEquals( LoadPlan.Disposition.ENTITY_LOADER, loadPlan.getDisposition() );
+		assertEquals( 1, loadPlan.getReturns().size() );
+		final EntityReturn cardFieldReturn = assertTyping( EntityReturn.class, loadPlan.getReturns().get( 0 ) );
+		assertEquals( 0, cardFieldReturn.getFetches().length );
+		assertEquals( 1, cardFieldReturn.getIdentifierDescription().getFetches().length );
+		final CompositeFetch cardFieldCompositePkFetch = assertTyping(
+				CompositeFetch.class,
+				cardFieldReturn.getIdentifierDescription().getFetches()[0]
+		);
+		assertEquals( 2, cardFieldCompositePkFetch.getFetches().length );
+		final EntityFetch cardFetch = assertTyping( EntityFetch.class, cardFieldCompositePkFetch.getFetches()[0] );
+		// i think this one might be a mistake; i think the collection reader still needs to be registered.  Its zero
+		// because the inverse of the key-many-to-one already had a registered AssociationKey and so saw the
+		// CollectionFetch as a circularity (I think)
+		assertEquals( 0, cardFetch.getFetches().length );
+		assertEquals( 0, cardFetch.getIdentifierDescription().getFetches().length );
+
+		final EntityFetch keyFetch = assertTyping( EntityFetch.class, cardFieldCompositePkFetch.getFetches()[1] );
+		assertEquals( 0, keyFetch.getFetches().length );
+		assertEquals( 0, keyFetch.getIdentifierDescription().getFetches().length );
+
+		// we need the readers ordered in a certain manner.  Here specifically: Fetch(Card), Fetch(Key), Return(CardField)
+		//
+		// additionally, we need Fetch(Card) and Fetch(Key) to be hydrated/semi-resolved before attempting to
+		// resolve the EntityKey for Return(CardField)
+		//
+		// together those sound like argument enough to continue keeping readers for "identifier fetches" as part of
+		// a special "identifier reader".  generated aliases could help here too to remove cyclic-ness from the graph.
+		// but at any rate, we need to know still when this becomes circularity
+	}
+
+	@Test
+	public void testEncapsulatedCompositeIdWithFetches2() {
+		Configuration cfg = new Configuration();
+		cfg.addAnnotatedClass( Card.class );
+		cfg.addAnnotatedClass( CardField.class );
+		cfg.addAnnotatedClass( Key.class );
+		cfg.addAnnotatedClass( PrimaryKey.class );
+
+		SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
+
+		final OuterJoinLoadable cardPersister = (OuterJoinLoadable) sf.getClassMetadata( Card.class );
+		doCompare( sf, cardPersister );
+		final LoadPlan cardLoadPlan = LoadPlanStructureAssertionHelper.INSTANCE.buildLoadPlan( sf, cardPersister );
+		assertEquals( LoadPlan.Disposition.ENTITY_LOADER, cardLoadPlan.getDisposition() );
+		assertEquals( 1, cardLoadPlan.getReturns().size() );
+		final EntityReturn cardReturn = assertTyping( EntityReturn.class, cardLoadPlan.getReturns().get( 0 ) );
+		assertEquals( 0, cardReturn.getIdentifierDescription().getFetches().length );
+		assertEquals( 1, cardReturn.getFetches().length );
+		final CollectionFetch fieldsFetch = assertTyping( CollectionFetch.class, cardReturn.getFetches()[0] );
+		assertNotNull( fieldsFetch.getElementGraph() );
+		final EntityElementGraph fieldsElementElementGraph = assertTyping( EntityElementGraph.class, fieldsFetch.getElementGraph() );
+		assertEquals( 0, fieldsElementElementGraph.getFetches().length );
+		assertEquals( 1, fieldsElementElementGraph.getIdentifierDescription().getFetches().length );
+		final CompositeFetch fieldsElementCompositeIdFetch = assertTyping(
+				CompositeFetch.class,
+				fieldsElementElementGraph.getIdentifierDescription().getFetches()[0]
+		);
+		assertEquals( 2, fieldsElementCompositeIdFetch.getFetches().length );
+
+		BidirectionalEntityFetch circularCardFetch = assertTyping(
+				BidirectionalEntityFetch.class,
+				fieldsElementCompositeIdFetch.getFetches()[0]
+		);
+		assertSame( circularCardFetch.getTargetEntityReference(), cardReturn );
+
+		// the fetch above is to the other key-many-to-one for CardField.primaryKey composite: key
+		EntityFetch keyFetch = assertTyping(
+				EntityFetch.class,
+				fieldsElementCompositeIdFetch.getFetches()[1]
+		);
+		assertEquals( Key.class.getName(), keyFetch.getEntityPersister().getEntityName() );
+
+
+		final EntityLoadQueryDetails queryDetails = Helper.INSTANCE.buildLoadQueryDetails( cardLoadPlan, sf );
+		final ResultSetProcessorImpl resultSetProcessor = assertTyping(
+				ResultSetProcessorImpl.class,
+				queryDetails.getResultSetProcessor()
+		);
+		final EntityLoadQueryDetails.EntityLoaderRowReader rowReader = assertTyping(
+				EntityLoadQueryDetails.EntityLoaderRowReader.class,
+				resultSetProcessor.getRowReader()
+		);
 	}
 
 	@Test
 	public void testManyToMany() {
 		Configuration cfg = new Configuration();
 		cfg.addResource( "org/hibernate/test/immutable/entitywithmutablecollection/inverse/ContractVariation.hbm.xml" );
 		SessionFactoryImplementor sf = (SessionFactoryImplementor) cfg.buildSessionFactory();
 		doCompare( sf, (OuterJoinLoadable) sf.getClassMetadata( org.hibernate.test.immutable.entitywithmutablecollection.Contract.class ) );
 
 	}
 
 	private void doCompare(SessionFactoryImplementor sf, OuterJoinLoadable persister) {
 		LoadPlanStructureAssertionHelper.INSTANCE.performBasicComparison( sf, persister );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java b/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
index 5a7ef43eeb..3b1c9fbc20 100644
--- a/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/persister/walking/BasicWalkingTest.java
@@ -1,239 +1,263 @@
 /*
  * jDocBook, processing of DocBook sources
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
 package org.hibernate.persister.walking;
 
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.OneToMany;
 import java.util.List;
 
 import org.hibernate.annotations.common.util.StringHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.walking.spi.AnyMappingDefinition;
 import org.hibernate.persister.walking.spi.AssociationAttributeDefinition;
+import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.AssociationVisitationStrategy;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.CollectionDefinition;
 import org.hibernate.persister.walking.spi.CollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CollectionIndexDefinition;
 import org.hibernate.persister.walking.spi.CompositeCollectionElementDefinition;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
 import org.hibernate.persister.walking.spi.MetadataDrivenModelGraphVisitor;
 
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 /**
  * @author Steve Ebersole
  */
 public class BasicWalkingTest extends BaseCoreFunctionalTestCase {
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class[] { Message.class, Poster.class };
 	}
 
 	@Test
 	public void testIt() {
 		EntityPersister ep = (EntityPersister) sessionFactory().getClassMetadata(Message.class);
 		MetadataDrivenModelGraphVisitor.visitEntity(
 				new AssociationVisitationStrategy() {
 					private int depth = 0;
 
 					@Override
 					public void start() {
 						System.out.println( ">> Start" );
 					}
 
 					@Override
 					public void finish() {
 						System.out.println( "<< Finish" );
 					}
 
 					@Override
 					public void startingEntity(EntityDefinition entityDefinition) {
 						System.out.println(
 								String.format(
 										"%s Starting entity (%s)",
 										StringHelper.repeat( ">>", ++depth ),
 										entityDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void finishingEntity(EntityDefinition entityDefinition) {
 						System.out.println(
 								String.format(
 										"%s Finishing entity (%s)",
 										StringHelper.repeat( "<<", depth-- ),
 										entityDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void startingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 						//To change body of implemented methods use File | Settings | File Templates.
 					}
 
 					@Override
 					public void finishingEntityIdentifier(EntityIdentifierDefinition entityIdentifierDefinition) {
 						//To change body of implemented methods use File | Settings | File Templates.
 					}
 
 					@Override
 					public void startingCollection(CollectionDefinition collectionDefinition) {
 						System.out.println(
 								String.format(
 										"%s Starting collection (%s)",
 										StringHelper.repeat( ">>", ++depth ),
 										collectionDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void finishingCollection(CollectionDefinition collectionDefinition) {
 						System.out.println(
 								String.format(
 										"%s Finishing collection (%s)",
 										StringHelper.repeat( ">>", depth-- ),
 										collectionDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void startingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
 						//To change body of implemented methods use File | Settings | File Templates.
 					}
 
 					@Override
 					public void finishingCollectionIndex(CollectionIndexDefinition collectionIndexDefinition) {
 						//To change body of implemented methods use File | Settings | File Templates.
 					}
 
 					@Override
 					public void startingCollectionElements(CollectionElementDefinition elementDefinition) {
 						//To change body of implemented methods use File | Settings | File Templates.
 					}
 
 					@Override
 					public void finishingCollectionElements(CollectionElementDefinition elementDefinition) {
 						//To change body of implemented methods use File | Settings | File Templates.
 					}
 
 					@Override
 					public void startingComposite(CompositionDefinition compositionDefinition) {
 						System.out.println(
 								String.format(
 										"%s Starting composite (%s)",
 										StringHelper.repeat( ">>", ++depth ),
 										compositionDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void finishingComposite(CompositionDefinition compositionDefinition) {
 						System.out.println(
 								String.format(
 										"%s Finishing composite (%s)",
 										StringHelper.repeat( ">>", depth-- ),
 										compositionDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void startingCompositeCollectionElement(CompositeCollectionElementDefinition compositionElementDefinition) {
 						System.out.println(
 								String.format(
 										"%s Starting composite (%s)",
 										StringHelper.repeat( ">>", ++depth ),
 										compositionElementDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public void finishingCompositeCollectionElement(CompositeCollectionElementDefinition compositionElementDefinition) {
 						System.out.println(
 								String.format(
 										"%s Finishing composite (%s)",
 										StringHelper.repeat( ">>", depth-- ),
 										compositionElementDefinition.toString()
 								)
 						);
 					}
 
 					@Override
 					public boolean startingAttribute(AttributeDefinition attributeDefinition) {
 						System.out.println(
 								String.format(
 										"%s Handling attribute (%s)",
 										StringHelper.repeat( ">>", depth + 1 ),
 										attributeDefinition.toString()
 								)
 						);
 						return true;
 					}
 
 					@Override
 					public void finishingAttribute(AttributeDefinition attributeDefinition) {
 						// nothing to do
 					}
 
 					@Override
 					public void foundAny(AssociationAttributeDefinition attributeDefinition, AnyMappingDefinition anyDefinition) {
 					}
+
+					@Override
+					public void associationKeyRegistered(AssociationKey associationKey) {
+						System.out.println(
+								String.format(
+										"%s AssociationKey registered : %s",
+										StringHelper.repeat( ">>", depth + 1 ),
+										associationKey.toString()
+								)
+						);
+					}
+
+					@Override
+					public void foundCircularAssociationKey(AssociationKey associationKey, AttributeDefinition attributeDefinition) {
+						System.out.println(
+								String.format(
+										"%s Handling circular association attribute (%s) : %s",
+										StringHelper.repeat( ">>", depth + 1 ),
+										attributeDefinition.toString(),
+										associationKey.toString()
+								)
+						);
+					}
 				},
 				ep
 		);
 	}
 
 	@Entity( name = "Message" )
 	public static class Message {
 		@Id
 		private Integer id;
 		private String name;
 		@ManyToOne
 		@JoinColumn
 		private Poster poster;
 	}
 
 	@Entity( name = "Poster" )
 	public static class Poster {
 		@Id
 		private Integer id;
 		private String name;
 		@OneToMany(mappedBy = "poster")
 		private List<Message> messages;
 	}
 }
