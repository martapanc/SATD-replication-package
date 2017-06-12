File path: code/core/src/main/java/org/hibernate/loader/Loader.java
Comment: se of trim() here is ugly?
Initial commit id: d8d6d82e
Final commit id: c46daa4c
   Bugs between [       0]:

   Bugs after [      50]:
d80c6ac0f9 HHH-11600 - Sap HANA PreparedStatement implements CallableStatement and is treated as such by Hibernate
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
1661af2d8f HHH-10965 - Add new Query Hint to avoid passing DISTINCT from entity queries to SQL statements
832b62f7bb HHH-9486: Use follow-on locking when paging only
80472f6a43 HHH-10513 - Fix locking WARN message logged when query LockMode is NONE
eb308a953a HHH-9340 - Streams API for query result processing.
7cae5ba95b HHH-9340 - Streams API for query result processing.
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
bdb458a609 HHH-10269 : JDBC Statement is not closed if exception appeared during query execution
3f37fff04a HHH-10563 : Significant String use/duplication associated with subselect fetch
1e44e7420b HHH-10267 - Support defining lazy attribute fetch groups
cffe71aeba HHH-9840 Change all kinds of CacheKey contract to a raw Object
16ae00a53a HHH-9840 Allow 2nd level cache implementations to customize the various key implementations
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
611f8a0e1c HHH-9803 - Checkstyle fix ups
bbfacee64d HHH-9803 - Checkstyle fix ups
7308e14fed HHH-9803 - Checkstyle fix ups
b605c3534e HHH-9790 - Remove deprecated methods from Session and SessionFactory
b476094d43 HHH-9747 - Import initial reworking of transaction handling (based on JdbcSession work)
4615ae1018 - HHH-9324: Avoids creation of LimitHandler instances for every query.
16b067cd7d HHH-9344 Convert Loader to use nanoTime instead of currentTimeMillis
8a2a78ff33 HHH-9170 StatelessSession is accidentally 2LC enabled in some cases
af5804a49c HHH-8939 Reduce contention on initialization of ColumnNameCache instances by loaders
b3e79f3f40 HHH-3051 corrected CustomLoader returnTypes (different approach)
2a90123991 HHH-3051 corrected Loader returnTypes
cd590470c0 HHH-8741 - More checkstyle cleanups
15adff22ce HHH-8679 Relax synchronized block in Loader#wrapResultSetIfEnabled
449c558365 HHH-8654 - Ability to get notified of interesting Session events
1658a477fb HHH-8647 - hibernate.cache.use_reference_entries + queries
739b88c5ce HHH-5818 exception message typo
ffa67243b8 HHH-8576 - Query not properly locking non-versioned entities associated with PersistenceContext
cc550c08a8 HHH-8441 Hibernate is trying to add values to an Immutable List
742b1b4156 HHH-2736 query hints for Query and Criteria
1825a4762c HHH-8211 Checkstyle and FindBugs fix-ups
6cabc326b8 HHH-8312 - named parameters binding are not correct when used within subquery
b51164aef6 HHH-8159 - Apply fixups indicated by analysis tools
cbbadea538 HHH-7908 Logging level checking
7778aae3b7 HHH-8180 Checks for logging level. Logging level check moved to variable outside for loop.
3d332371bd HHH-7841 - Redesign Loader
5e7b3601a9 HHH-7984 - Oracle callable statement closing
e0cfc6bf2e HHH-6736 Added support for SELECT ... FOR UPDATE SKIP LOCKED
9ce5c32dd7 HHH-7902 Replace JDBC proxies with a set of contracts/helpers
21ade0c798 HHH-1168 - Problem combining locking and paging on Oracle
6e71a0907e HHH-1168 - Problem combining locking and paging on Oracle
4b2871cfba HHH-1168 - Problem combining locking and paging on Oracle
06b0faaf57 HHH-7746 - Investigate alternative batch loading algorithms
3e5184e6d7 HHH-6043 PostLoad method invoked before collection initialised
3a72b45325 HHH-1283 : Join fetched collections using Query.scroll() is correct only for first entity
8eb7d8cf64 HHH-1283 : Join fetched collections using Query.scroll() is correct only for first entity
153eb4a913 HHH-7387 - Integrate Draft 6 of the JPA 2.1 spec : stored procedure queries

Start block index: 1513
End block index: 1594
	protected final PreparedStatement prepareQueryStatement(
	        final QueryParameters queryParameters,
	        final boolean scroll,
	        final SessionImplementor session) throws SQLException, HibernateException {

		queryParameters.processFilters( getSQLString(), session );
		String sql = queryParameters.getFilteredSQL();
		final Dialect dialect = getFactory().getDialect();
		final RowSelection selection = queryParameters.getRowSelection();
		boolean useLimit = useLimit( selection, dialect );
		boolean hasFirstRow = getFirstRow( selection ) > 0;
		boolean useOffset = hasFirstRow && useLimit && dialect.supportsLimitOffset();
		boolean callable = queryParameters.isCallable();
		
		boolean useScrollableResultSetToSkip = hasFirstRow &&
				!useOffset &&
				getFactory().getSettings().isScrollableResultSetsEnabled();
		ScrollMode scrollMode = scroll ? queryParameters.getScrollMode() : ScrollMode.SCROLL_INSENSITIVE;

		if ( useLimit ) {
			sql = dialect.getLimitString( 
					sql.trim(), //use of trim() here is ugly?
					useOffset ? getFirstRow(selection) : 0, 
					getMaxOrLimit(selection, dialect) 
				);
		}

		sql = preprocessSQL( sql, queryParameters, dialect );
		
		PreparedStatement st = null;
		
		if (callable) {
			st = session.getBatcher()
				.prepareCallableQueryStatement( sql, scroll || useScrollableResultSetToSkip, scrollMode );
		} 
		else {
			st = session.getBatcher()
				.prepareQueryStatement( sql, scroll || useScrollableResultSetToSkip, scrollMode );
		}
				

		try {

			int col = 1;
			//TODO: can we limit stored procedures ?!
			if ( useLimit && dialect.bindLimitParametersFirst() ) {
				col += bindLimitParameters( st, col, selection );
			}
			if (callable) {
				col = dialect.registerResultSetOutParameter( (CallableStatement)st, col );
			}

			col += bindParameterValues( st, queryParameters, col, session );

			if ( useLimit && !dialect.bindLimitParametersFirst() ) {
				col += bindLimitParameters( st, col, selection );
			}

			if ( !useLimit ) {
				setMaxRows( st, selection );
			}

			if ( selection != null ) {
				if ( selection.getTimeout() != null ) {
					st.setQueryTimeout( selection.getTimeout().intValue() );
				}
				if ( selection.getFetchSize() != null ) {
					st.setFetchSize( selection.getFetchSize().intValue() );
				}
			}
		}
		catch ( SQLException sqle ) {
			session.getBatcher().closeQueryStatement( st, null );
			throw sqle;
		}
		catch ( HibernateException he ) {
			session.getBatcher().closeQueryStatement( st, null );
			throw he;
		}

		return st;
	}
