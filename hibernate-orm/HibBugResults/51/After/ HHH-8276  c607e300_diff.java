diff --git a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
index eeb63ff359..a07a4bf3f4 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
@@ -834,1251 +834,1281 @@ public abstract class AbstractCollectionPersister
 		if ( index == null ) {
 			throw new HibernateException( "null index column for collection: " + role );
 		}
 		index = decrementIndexByBase( index );
 		return index;
 	}
 
 	protected Object decrementIndexByBase(Object index) {
 		if ( baseIndex != 0 ) {
             index = (Integer)index - baseIndex;
 		}
 		return index;
 	}
 
 	public Object readIdentifier(ResultSet rs, String alias, SessionImplementor session)
 			throws HibernateException, SQLException {
 		Object id = getIdentifierType().nullSafeGet( rs, alias, session, null );
 		if ( id == null ) {
 			throw new HibernateException( "null identifier column for collection: " + role );
 		}
 		return id;
 	}
 
 	public Object readKey(ResultSet rs, String[] aliases, SessionImplementor session)
 			throws HibernateException, SQLException {
 		return getKeyType().nullSafeGet( rs, aliases, session, null );
 	}
 
 	/**
 	 * Write the key to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeKey(PreparedStatement st, Serializable key, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		if ( key == null ) {
 			throw new NullPointerException( "null key for collection: " + role ); // an assertion
 		}
 		getKeyType().nullSafeSet( st, key, i, session );
 		return i + keyColumnAliases.length;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElement(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getElementType().nullSafeSet( st, elt, i, elementColumnIsSettable, session );
 		return i + ArrayHelper.countTrue( elementColumnIsSettable );
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndex(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		getIndexType().nullSafeSet( st, incrementIndexByBase( index ), i, indexColumnIsSettable, session );
 		return i + ArrayHelper.countTrue( indexColumnIsSettable );
 	}
 
 	protected Object incrementIndexByBase(Object index) {
 		if ( baseIndex != 0 ) {
             index = (Integer)index + baseIndex;
 		}
 		return index;
 	}
 
 	/**
 	 * Write the element to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeElementToWhere(PreparedStatement st, Object elt, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( elementIsPureFormula ) {
 			throw new AssertionFailure( "cannot use a formula-based element in the where condition" );
 		}
 		getElementType().nullSafeSet( st, elt, i, elementColumnIsInPrimaryKey, session );
 		return i + elementColumnAliases.length;
 
 	}
 
 	/**
 	 * Write the index to a JDBC <tt>PreparedStatement</tt>
 	 */
 	protected int writeIndexToWhere(PreparedStatement st, Object index, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( indexContainsFormula ) {
 			throw new AssertionFailure( "cannot use a formula-based index in the where condition" );
 		}
 		getIndexType().nullSafeSet( st, incrementIndexByBase( index ), i, session );
 		return i + indexColumnAliases.length;
 	}
 
 	/**
 	 * Write the identifier to a JDBC <tt>PreparedStatement</tt>
 	 */
 	public int writeIdentifier(PreparedStatement st, Object id, int i, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		getIdentifierType().nullSafeSet( st, id, i, session );
 		return i + 1;
 	}
 
 	public boolean isPrimitiveArray() {
 		return isPrimitiveArray;
 	}
 
 	public boolean isArray() {
 		return isArray;
 	}
 
 	public String[] getKeyColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( keyColumnAliases );
 	}
 
 	public String[] getElementColumnAliases(String suffix) {
 		return new Alias( suffix ).toAliasStrings( elementColumnAliases );
 	}
 
 	public String[] getIndexColumnAliases(String suffix) {
 		if ( hasIndex ) {
 			return new Alias( suffix ).toAliasStrings( indexColumnAliases );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String getIdentifierColumnAlias(String suffix) {
 		if ( hasIdentifier ) {
 			return new Alias( suffix ).toAliasString( identifierColumnAlias );
 		}
 		else {
 			return null;
 		}
 	}
 
 	public String getIdentifierColumnName() {
 		if ( hasIdentifier ) {
 			return identifierColumnName;
 		}
 		else {
 			return null;
 		}
 	}
 
 	/**
 	 * Generate a list of collection index, key and element columns
 	 */
 	public String selectFragment(String alias, String columnSuffix) {
 		SelectFragment frag = generateSelectFragment( alias, columnSuffix );
 		appendElementColumns( frag, alias );
 		appendIndexColumns( frag, alias );
 		appendIdentifierColumns( frag, alias );
 
 		return frag.toFragmentString()
 				.substring( 2 ); // strip leading ','
 	}
 
 	protected String generateSelectSizeString(boolean isIntegerIndexed) {
 		String selectValue = isIntegerIndexed ?
 				"max(" + getIndexColumnNames()[0] + ") + 1" : // lists, arrays
 				"count(" + getElementColumnNames()[0] + ")"; // sets, maps, bags
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addColumn( selectValue )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumn( "1" )
 				.toStatementString();
 	}
 
 	protected String generateSelectRowByIndexString() {
 		if ( !hasIndex() ) {
 			return null;
 		}
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getIndexColumnNames(), "=?" )
 				.addCondition( indexFormulas, "=?" )
 				.addColumns( getElementColumnNames(), elementColumnAliases )
 				.addColumns( indexFormulas, indexColumnAliases )
 				.toStatementString();
 	}
 
 	protected String generateDetectRowByElementString() {
 		return new SimpleSelect( dialect )
 				.setTableName( getTableName() )
 				.addCondition( getKeyColumnNames(), "=?" )
 				.addCondition( getElementColumnNames(), "=?" )
 				.addCondition( elementFormulas, "=?" )
 				.addColumn( "1" )
 				.toStatementString();
 	}
 
 	protected SelectFragment generateSelectFragment(String alias, String columnSuffix) {
 		return new SelectFragment()
 				.setSuffix( columnSuffix )
 				.addColumns( alias, keyColumnNames, keyColumnAliases );
 	}
 
 	protected void appendElementColumns(SelectFragment frag, String elemAlias) {
 		for ( int i = 0; i < elementColumnIsSettable.length; i++ ) {
 			if ( elementColumnIsSettable[i] ) {
 				frag.addColumnTemplate( elemAlias, elementColumnReaderTemplates[i], elementColumnAliases[i] );
 			}
 			else {
 				frag.addFormula( elemAlias, elementFormulaTemplates[i], elementColumnAliases[i] );
 			}
 		}
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
-			throw getFactory().getSQLExceptionHelper().convert(
+			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve collection size: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
-					);
+			);
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
-			throw getFactory().getSQLExceptionHelper().convert(
+			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not check row existence: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
-					);
+			);
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
-			throw getFactory().getSQLExceptionHelper().convert(
+			throw getSQLExceptionHelper().convert(
 					sqle,
 					"could not read row: " +
 							MessageHelper.collectionInfoString( this, key, getFactory() ),
 					sqlSelectSizeString
-					);
+			);
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
-				// todo : implement
-				throw new NotYetImplementedException();
+				return new CompositeCollectionElementDefinition() {
+					@Override
+					public String getName() {
+						return "index";
+					}
+
+					@Override
+					public CompositeType getType() {
+						return (CompositeType) getIndexType();
+					}
+
+					@Override
+					public boolean isNullable() {
+						return false;
+					}
+
+					@Override
+					public AttributeSource getSource() {
+						// TODO: what if this is a collection w/in an encapsulated composition attribute?
+						// should return the encapsulated composition attribute instead???
+						return getOwnerEntityPersister();
+					}
+
+					@Override
+					public Iterable<AttributeDefinition> getAttributes() {
+						return CompositionSingularSubAttributesHelper.getCompositeCollectionIndexSubAttributes( this );
+					}
+					@Override
+					public CollectionDefinition getCollectionDefinition() {
+						return AbstractCollectionPersister.this;
+					}
+				};
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
 					public CompositeType getType() {
 						return (CompositeType) getElementType();
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
index b9ef77a3e2..d507cfb8e9 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/CompositionSingularSubAttributesHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/walking/internal/CompositionSingularSubAttributesHelper.java
@@ -1,289 +1,301 @@
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
 	 *
 	 * @return composite ID sub-attribute definitions.
 	 */
 	public static Iterable<AttributeDefinition> getIdentifierSubAttributes(AbstractEntityPersister entityPersister) {
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
 
+	public static Iterable<AttributeDefinition> getCompositeCollectionIndexSubAttributes(CompositeCollectionElementDefinition compositionElementDefinition){
+		final QueryableCollection collectionPersister =
+				(QueryableCollection) compositionElementDefinition.getCollectionDefinition().getCollectionPersister();
+		return getSingularSubAttributes(
+				compositionElementDefinition.getSource(),
+				(OuterJoinLoadable) collectionPersister.getOwnerEntityPersister(),
+				(CompositeType) collectionPersister.getIndexType(),
+				collectionPersister.getTableName(),
+				collectionPersister.getIndexColumnNames()
+		);
+	}
+
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
 
 
 						final boolean[] propertyNullability = compositeType.getPropertyNullability();
 						final boolean nullable = propertyNullability == null || propertyNullability[subAttributeNumber];
 
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
 								public CompositeType getType() {
 									return (CompositeType) type;
 								}
 
 								@Override
 								public boolean isNullable() {
 									return nullable;
 								}
 
 								@Override
 								public AttributeSource getSource() {
 									return source;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionAttribute.java
index bceaf833af..a5c6d02f26 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/AbstractCompositionAttribute.java
@@ -1,254 +1,250 @@
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
 package org.hibernate.tuple.component;
 
 import java.util.Iterator;
 
 import org.hibernate.engine.internal.JoinHelper;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.OuterJoinLoadable;
 import org.hibernate.persister.walking.spi.AssociationKey;
 import org.hibernate.persister.walking.spi.AttributeDefinition;
 import org.hibernate.persister.walking.spi.AttributeSource;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.persister.walking.spi.EntityDefinition;
 import org.hibernate.tuple.AbstractNonIdentifierAttribute;
 import org.hibernate.tuple.BaselineAttributeInformation;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.Type;
 
 import static org.hibernate.engine.internal.JoinHelper.getLHSColumnNames;
 import static org.hibernate.engine.internal.JoinHelper.getLHSTableName;
 import static org.hibernate.engine.internal.JoinHelper.getRHSColumnNames;
 
 /**
  * A base class for a composite, non-identifier attribute.
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractCompositionAttribute extends AbstractNonIdentifierAttribute implements
 																						   CompositionDefinition {
 	protected AbstractCompositionAttribute(
 			AttributeSource source,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			String attributeName,
 			CompositeType attributeType,
 			BaselineAttributeInformation baselineInfo) {
 		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
 	}
 
 	@Override
 	public CompositeType getType() {
 		return (CompositeType) super.getType();
 	}
 
 	@Override
 	public Iterable<AttributeDefinition> getAttributes() {
 		return new Iterable<AttributeDefinition>() {
 			@Override
 			public Iterator<AttributeDefinition> iterator() {
 				return new Iterator<AttributeDefinition>() {
 					private final int numberOfAttributes = getType().getSubtypes().length;
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
 
 						final String name = getType().getPropertyNames()[subAttributeNumber];
 						final Type type = getType().getSubtypes()[subAttributeNumber];
 
 						int columnPosition = currentColumnPosition;
 						currentColumnPosition += type.getColumnSpan( sessionFactory() );
 
 						if ( type.isAssociationType() ) {
 							// we build the association-key here because of the "goofiness" with 'currentColumnPosition'
 							final AssociationKey associationKey;
 							final AssociationType aType = (AssociationType) type;
 							final Joinable joinable = aType.getAssociatedJoinable( sessionFactory() );
 
 							if ( aType.isAnyType() ) {
 								associationKey = new AssociationKey(
 										JoinHelper.getLHSTableName(
 												aType,
 												attributeNumber(),
 												(OuterJoinLoadable) getSource()
 										),
 										JoinHelper.getLHSColumnNames(
 												aType,
 												attributeNumber(),
 												0,
 												(OuterJoinLoadable) getSource(),
 												sessionFactory()
 										)
 								);
 							}
 							else if ( aType.getForeignKeyDirection() == ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT ) {
 								final String lhsTableName;
 								final String[] lhsColumnNames;
 
 								if ( joinable.isCollection() ) {
 									final QueryableCollection collectionPersister = (QueryableCollection) joinable;
 									lhsTableName = collectionPersister.getTableName();
 									lhsColumnNames = collectionPersister.getElementColumnNames();
 								}
 								else {
 									final OuterJoinLoadable entityPersister = (OuterJoinLoadable) locateOwningPersister();
 									lhsTableName = getLHSTableName( aType, attributeNumber(), entityPersister );
 									lhsColumnNames = getLHSColumnNames( aType, attributeNumber(), entityPersister, sessionFactory() );
 								}
 								associationKey = new AssociationKey( lhsTableName, lhsColumnNames );
 
 //								associationKey = new AssociationKey(
 //										getLHSTableName(
 //												aType,
 //												attributeNumber(),
 //												(OuterJoinLoadable) locateOwningPersister()
 //										),
 //										getLHSColumnNames(
 //												aType,
 //												attributeNumber(),
 //												columnPosition,
 //												(OuterJoinLoadable) locateOwningPersister(),
 //												sessionFactory()
 //										)
 //								);
 							}
 							else {
 								associationKey = new AssociationKey(
 										joinable.getTableName(),
 										getRHSColumnNames( aType, sessionFactory() )
 								);
 							}
 
 							final CompositeType cType = getType();
-							final boolean nullable = cType.getPropertyNullability() == null
-									? true
-									: cType.getPropertyNullability()[ subAttributeNumber ];
+							final boolean nullable = cType.getPropertyNullability() == null || cType.getPropertyNullability()[subAttributeNumber];
 
 							return new CompositeBasedAssociationAttribute(
 									AbstractCompositionAttribute.this,
 									sessionFactory(),
 									subAttributeNumber,
 									name,
 									(AssociationType) type,
 									new BaselineAttributeInformation.Builder()
 											.setInsertable( AbstractCompositionAttribute.this.isInsertable() )
 											.setUpdateable( AbstractCompositionAttribute.this.isUpdateable() )
 											.setInsertGenerated( AbstractCompositionAttribute.this.isInsertGenerated() )
 											.setUpdateGenerated( AbstractCompositionAttribute.this.isUpdateGenerated() )
 											.setNullable( nullable )
 											.setDirtyCheckable( true )
 											.setVersionable( AbstractCompositionAttribute.this.isVersionable() )
 											.setCascadeStyle( getType().getCascadeStyle( subAttributeNumber ) )
 											.setFetchMode( getType().getFetchMode( subAttributeNumber ) )
 											.createInformation(),
 									AbstractCompositionAttribute.this.attributeNumber(),
 									associationKey
 							);
 						}
 						else if ( type.isComponentType() ) {
 							return new CompositionBasedCompositionAttribute(
 									AbstractCompositionAttribute.this,
 									sessionFactory(),
 									subAttributeNumber,
 									name,
 									(CompositeType) type,
 									new BaselineAttributeInformation.Builder()
 											.setInsertable( AbstractCompositionAttribute.this.isInsertable() )
 											.setUpdateable( AbstractCompositionAttribute.this.isUpdateable() )
 											.setInsertGenerated( AbstractCompositionAttribute.this.isInsertGenerated() )
 											.setUpdateGenerated( AbstractCompositionAttribute.this.isUpdateGenerated() )
 											.setNullable( getType().getPropertyNullability()[subAttributeNumber] )
 											.setDirtyCheckable( true )
 											.setVersionable( AbstractCompositionAttribute.this.isVersionable() )
 											.setCascadeStyle( getType().getCascadeStyle( subAttributeNumber ) )
 											.setFetchMode( getType().getFetchMode( subAttributeNumber ) )
 											.createInformation()
 							);
 						}
 						else {
 							final CompositeType cType = getType();
-							final boolean nullable = cType.getPropertyNullability() == null
-									? true
-									: cType.getPropertyNullability()[ subAttributeNumber ];
+							final boolean nullable = cType.getPropertyNullability() == null || cType.getPropertyNullability()[subAttributeNumber];
 
 							return new CompositeBasedBasicAttribute(
 									AbstractCompositionAttribute.this,
 									sessionFactory(),
 									subAttributeNumber,
 									name,
 									type,
 									new BaselineAttributeInformation.Builder()
 											.setInsertable( AbstractCompositionAttribute.this.isInsertable() )
 											.setUpdateable( AbstractCompositionAttribute.this.isUpdateable() )
 											.setInsertGenerated( AbstractCompositionAttribute.this.isInsertGenerated() )
 											.setUpdateGenerated( AbstractCompositionAttribute.this.isUpdateGenerated() )
 											.setNullable( nullable )
 											.setDirtyCheckable( true )
 											.setVersionable( AbstractCompositionAttribute.this.isVersionable() )
 											.setCascadeStyle( getType().getCascadeStyle( subAttributeNumber ) )
 											.setFetchMode( getType().getFetchMode( subAttributeNumber ) )
 											.createInformation()
 							);
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
 
 	public EntityPersister locateOwningPersister() {
 		if ( EntityDefinition.class.isInstance( getSource() ) ) {
 			return ( (EntityDefinition) getSource() ).getEntityPersister();
 		}
 		else {
 			return ( (AbstractCompositionAttribute) getSource() ).locateOwningPersister();
 		}
 	}
 
 	@Override
 	protected String loggableMetadata() {
 		return super.loggableMetadata() + ",composition";
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositionBasedCompositionAttribute.java b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositionBasedCompositionAttribute.java
index 4a154f130d..6e22bcc44b 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositionBasedCompositionAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/component/CompositionBasedCompositionAttribute.java
@@ -1,46 +1,45 @@
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
 package org.hibernate.tuple.component;
 
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.walking.spi.CompositionDefinition;
 import org.hibernate.tuple.BaselineAttributeInformation;
 import org.hibernate.type.CompositeType;
 
 /**
  * @author Steve Ebersole
  */
 public class CompositionBasedCompositionAttribute
-		extends AbstractCompositionAttribute
-		implements CompositionDefinition {
+		extends AbstractCompositionAttribute {
 	public CompositionBasedCompositionAttribute(
 			CompositionDefinition source,
 			SessionFactoryImplementor sessionFactory,
 			int attributeNumber,
 			String attributeName,
 			CompositeType attributeType,
 			BaselineAttributeInformation baselineInfo) {
 		super( source, sessionFactory, attributeNumber, attributeName, attributeType, baselineInfo );
 	}
 }
