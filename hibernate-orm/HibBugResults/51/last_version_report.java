	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
			throws HibernateException {

		if ( !isInverse && isRowInsertEnabled() ) {

			if ( LOG.isDebugEnabled() ) LOG.debugf( "Inserting rows of collection: %s",
					MessageHelper.collectionInfoString( this, id, getFactory() ) );

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
								expectation.verifyOutcome( st.executeUpdate(), st, -1 );
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
								st.close();
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
								MessageHelper.collectionInfoString( this, id, getFactory() ),
						getSQLInsertRowString()
						);
			}

		}
	}
