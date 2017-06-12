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
