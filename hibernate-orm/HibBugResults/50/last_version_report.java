	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
			throws HibernateException {

		if ( !isInverse && isRowInsertEnabled() ) {

			if ( LOG.isDebugEnabled() ) {
				LOG.debugf( "Inserting collection: %s",
						MessageHelper.collectionInfoString( this, id, getFactory() ) );
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

					LOG.debugf( "Done inserting collection: %s rows inserted", count );

				}
				else {
					LOG.debugf( "Collection was empty" );
				}
			}
			catch ( SQLException sqle ) {
				throw sqlExceptionHelper.convert(
						sqle,
						"could not insert collection: " +
								MessageHelper.collectionInfoString( this, id, getFactory() ),
						getSQLInsertRowString()
						);
			}
		}
	}
