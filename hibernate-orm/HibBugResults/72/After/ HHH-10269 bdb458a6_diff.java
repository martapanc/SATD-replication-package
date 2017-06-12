diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index d95978412b..a9f6a38a9e 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -1133,1659 +1133,1664 @@ public abstract class Loader {
 				TwoPhaseLoad.initializeEntity( hydratedObject, readOnly, session, pre );
 			}
 		}
 
 		if ( collectionPersisters != null ) {
 			for ( CollectionPersister collectionPersister : collectionPersisters ) {
 				if ( !collectionPersister.isArray() ) {
 					//for sets, we should end the collection load after resolving
 					//the entities, since we might call hashCode() on the elements
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersister );
 				}
 			}
 		}
 
 		// Until this entire method is refactored w/ polymorphism, postLoad was
 		// split off from initializeEntity.  It *must* occur after
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
 									"Could not locate EntityEntry immediately after two-phase load"
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
 			final SessionImplementor session,
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
 	 * Are rows transformed immediately after being read from the ResultSet?
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
 			SessionImplementor session) throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(
 			Object[] row,
 			ResultSet rs,
 			SessionImplementor session) throws SQLException, HibernateException {
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
 			final SessionImplementor session) {
 
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
 			final SessionImplementor session)
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
 			final SessionImplementor session) {
 
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
 			final SessionImplementor session) throws HibernateException, SQLException {
 
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
 			final SessionImplementor session) throws HibernateException, SQLException {
 
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
 					session.getFactory().getStatisticsImplementor()
 							.optimisticFailure( persister.getEntityName() );
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
 			final SessionImplementor session) throws HibernateException, SQLException {
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
 			final SessionImplementor session)
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
 			final SessionImplementor session)
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
 			final SessionImplementor session) throws SQLException, HibernateException {
 
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
 			final SessionImplementor session) throws HibernateException, SQLException {
 
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
 			final SessionImplementor session) throws SQLException {
 		return executeQueryStatement( getSQLString(), queryParameters, scroll, afterLoadActions, session );
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
 			final SessionImplementor session) throws SQLException, HibernateException {
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
 		catch (SQLException sqle) {
 			session.getJdbcCoordinator().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 			throw sqle;
 		}
 		catch (HibernateException he) {
 			session.getJdbcCoordinator().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 			throw he;
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
 			final SessionImplementor session) throws SQLException, HibernateException {
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
 			final SessionImplementor session) throws SQLException, HibernateException {
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
 		catch (SQLException sqle) {
 			session.getJdbcCoordinator().getResourceRegistry().release( st );
 			session.getJdbcCoordinator().afterStatementExecution();
 			throw sqle;
 		}
+		catch (HibernateException he) {
+			session.getJdbcCoordinator().getResourceRegistry().release( st );
+			session.getJdbcCoordinator().afterStatementExecution();
+			throw he;
+		}
 	}
 
 	protected void autoDiscoverTypes(ResultSet rs) {
 		throw new AssertionFailure( "Auto discover types not supported in this loader" );
 
 	}
 
 	private ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
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
 			final SessionImplementor session,
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
 			throw factory.getSQLExceptionHelper().convert(
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
 			final SessionImplementor session,
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
 			throw factory.getSQLExceptionHelper().convert(
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
 			final SessionImplementor session,
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
 			throw factory.getSQLExceptionHelper().convert(
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
 			final SessionImplementor session,
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
 			throw factory.getSQLExceptionHelper().convert(
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
 			final SessionImplementor session,
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
 			throw factory.getSQLExceptionHelper().convert(
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
 			final SessionImplementor session,
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
 			throw factory.getSQLExceptionHelper().convert(
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
 			final SessionImplementor session,
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
 
 	private List listIgnoreQueryCache(SessionImplementor session, QueryParameters queryParameters) {
 		return getResultList( doList( session, queryParameters ), queryParameters.getResultTransformer() );
 	}
 
 	private List listUsingQueryCache(
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final Set<Serializable> querySpaces,
 			final Type[] resultTypes) {
 
 		QueryCache queryCache = factory.getQueryCache( queryParameters.getCacheRegion() );
 
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
 			SessionImplementor session,
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
 			final SessionImplementor session,
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
 					factory.getStatisticsImplementor()
 							.queryCacheMiss( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 				else {
 					factory.getStatisticsImplementor()
 							.queryCacheHit( getQueryIdentifier(), queryCache.getRegion().getName() );
 				}
 			}
 		}
 
 		return result;
 	}
 
 	private EntityPersister getEntityPersister(EntityType entityType) {
 		return factory.getEntityPersister( entityType.getAssociatedEntityName() );
 	}
 
 	protected void putResultInQueryCache(
 			final SessionImplementor session,
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
 				factory.getStatisticsImplementor()
 						.queryCachePut( getQueryIdentifier(), queryCache.getRegion().getName() );
 			}
 		}
 	}
 
 	/**
 	 * Actually execute a query, ignoring the query cache
 	 */
 
 	protected List doList(final SessionImplementor session, final QueryParameters queryParameters)
 			throws HibernateException {
 		return doList( session, queryParameters, null );
 	}
 
 	private List doList(
 			final SessionImplementor session,
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
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not execute query",
 					getSQLString()
 			);
 		}
 
 		if ( stats ) {
 			final long endTime = System.nanoTime();
 			final long milliseconds = TimeUnit.MILLISECONDS.convert( endTime - startTime, TimeUnit.NANOSECONDS );
 			getFactory().getStatisticsImplementor().queryExecuted(
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
 	protected ScrollableResults scroll(
 			final QueryParameters queryParameters,
 			final Type[] returnTypes,
 			final HolderInstantiator holderInstantiator,
 			final SessionImplementor session) throws HibernateException {
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
 				getFactory().getStatisticsImplementor().queryExecuted(
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
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not execute query using scroll",
 					getSQLString()
 			);
 		}
 
 	}
 
 	/**
 	 * Calculate and cache select-clause suffixes. Must be
 	 * called by subclasses after instantiation.
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
diff --git a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/query/QueryTest.java b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/query/QueryTest.java
index 16e6642436..f7e95d5d50 100644
--- a/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/query/QueryTest.java
+++ b/hibernate-entitymanager/src/test/java/org/hibernate/jpa/test/query/QueryTest.java
@@ -58,1001 +58,1017 @@ public class QueryTest extends BaseEntityManagerFunctionalTestCase {
 
 	@Override
 	@SuppressWarnings("unchecked")
 	protected void addConfigOptions(Map options) {
 		super.addConfigOptions( options );
 		options.put( AvailableSettings.GENERATE_STATISTICS, "true" );
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH-7192" )
 	public void testTypedManipulationQueryError() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 
 		try {
 			em.createQuery( "delete Item", Item.class );
 			fail();
 		}
 		catch (IllegalArgumentException expected) {
 		}
 
 		try {
 			em.createQuery( "update Item i set i.name = 'someName'", Item.class );
 			fail();
 		}
 		catch (IllegalArgumentException expected) {
 		}
 	}
 
 	@Test
 	public void testPagedQuery() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		item = new Item( "Computer", "Apple II" );
 		em.persist( item );
 		Query q = em.createQuery( "select i from " + Item.class.getName() + " i where i.name like :itemName" );
 		q.setParameter( "itemName", "%" );
 		q.setMaxResults( 1 );
 		q.getSingleResult();
 		q = em.createQuery( "select i from Item i where i.name like :itemName" );
 		q.setParameter( "itemName", "%" );
 		q.setFirstResult( 1 );
 		q.setMaxResults( 1 );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	public void testNullPositionalParameter() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		Query q = em.createQuery( "from Item i where i.intVal=?" );
 		q.setParameter( 1, null );
 		List results = q.getResultList();
 		// null != null
 		assertEquals( 0, results.size() );
 		q = em.createQuery( "from Item i where i.intVal is null and ? is null" );
 		q.setParameter( 1, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		q = em.createQuery( "from Item i where i.intVal is null or i.intVal = ?" );
 		q.setParameter( 1, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	public void testNullPositionalParameterParameter() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		Query q = em.createQuery( "from Item i where i.intVal=?" );
 		Parameter p = new Parameter() {
 			@Override
 			public String getName() {
 				return null;
 			}
 			@Override
 			public Integer getPosition() {
 				return 1;
 			}
 			@Override
 			public Class getParameterType() {
 				return Integer.class;
 			}
 		};
 		q.setParameter( p, null );
 		Parameter pGotten = q.getParameter( p.getPosition() );
 		List results = q.getResultList();
 		// null != null
 		assertEquals( 0, results.size() );
 		q = em.createQuery( "from Item i where i.intVal is null and ? is null" );
 		q.setParameter( p, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		q = em.createQuery( "from Item i where i.intVal is null or i.intVal = ?" );
 		q.setParameter( p, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	public void testNullPositionalParameterParameterIncompatible() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		Query q = em.createQuery( "from Item i where i.intVal=?" );
 		Parameter p = new Parameter() {
 			@Override
 			public String getName() {
 				return null;
 			}
 			@Override
 			public Integer getPosition() {
 				return 1;
 			}
 			@Override
 			public Class getParameterType() {
 				return Long.class;
 			}
 		};
 		q.setParameter( p, null );
 		Parameter pGotten = q.getParameter( p.getPosition() );
 		List results = q.getResultList();
 		// null != null
 		assertEquals( 0, results.size() );
 		q = em.createQuery( "from Item i where i.intVal is null and ? is null" );
 		q.setParameter( p, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		q = em.createQuery( "from Item i where i.intVal is null or i.intVal = ?" );
 		q.setParameter( p, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	public void testNullNamedParameter() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		Query q = em.createQuery( "from Item i where i.intVal=:iVal" );
 		q.setParameter( "iVal", null );
 		List results = q.getResultList();
 		// null != null
 		assertEquals( 0, results.size() );
 		q = em.createQuery( "from Item i where i.intVal is null and :iVal is null" );
 		q.setParameter( "iVal", null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		q = em.createQuery( "from Item i where i.intVal is null or i.intVal = :iVal" );
 		q.setParameter( "iVal", null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	public void testNullNamedParameterParameter() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		Query q = em.createQuery( "from Item i where i.intVal=:iVal" );
 		Parameter p = new Parameter() {
 			@Override
 			public String getName() {
 				return "iVal";
 			}
 			@Override
 			public Integer getPosition() {
 				return null;
 			}
 			@Override
 			public Class getParameterType() {
 				return Integer.class;
 			}
 		};
 		q.setParameter( p, null );
 		List results = q.getResultList();
 		// null != null
 		assertEquals( 0, results.size() );
 		q = em.createQuery( "from Item i where i.intVal is null and :iVal is null" );
 		q.setParameter( p, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		q = em.createQuery( "from Item i where i.intVal is null or i.intVal = :iVal" );
 		q.setParameter( p, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	public void testNullNamedParameterParameterIncompatible() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		Query q = em.createQuery( "from Item i where i.intVal=:iVal" );
 		Parameter p = new Parameter() {
 			@Override
 			public String getName() {
 				return "iVal";
 			}
 			@Override
 			public Integer getPosition() {
 				return null;
 			}
 			@Override
 			public Class getParameterType() {
 				return Long.class;
 			}
 		};
 		q.setParameter( p, null );
 		List results = q.getResultList();
 		// null != null
 		assertEquals( 0, results.size() );
 		q = em.createQuery( "from Item i where i.intVal is null and :iVal is null" );
 		q.setParameter( p, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		q = em.createQuery( "from Item i where i.intVal is null or i.intVal = :iVal" );
 		q.setParameter( p, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	@SkipForDialects(
 			value = {
 					@SkipForDialect(value = Oracle8iDialect.class, jiraKey = "HHH-10161", comment = "Cannot convert untyped null (assumed to be BINARY type) to NUMBER"),
 					@SkipForDialect(value = PostgreSQL9Dialect.class, jiraKey = "HHH-10312", comment = "Cannot convert untyped null (assumed to be bytea type) to bigint"),
 					@SkipForDialect(value = PostgresPlusDialect.class, jiraKey = "HHH-10312", comment = "Cannot convert untyped null (assumed to be bytea type) to bigint")
 			}
 	)
 	public void testNativeQueryNullPositionalParameter() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		// native queries don't seem to flush by default ?!?
 		em.flush();
 		Query q = em.createNativeQuery( "select * from Item i where i.intVal=?" );
 		q.setParameter( 1, null );
 		List results = q.getResultList();
 		// null != null
 		assertEquals( 0, results.size() );
 		q = em.createNativeQuery( "select * from Item i where i.intVal is null and ? is null" );
 		q.setParameter( 1, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		q = em.createNativeQuery( "select * from Item i where i.intVal is null or i.intVal = ?" );
 		q.setParameter( 1, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-10161"  )
 	public void testNativeQueryNullPositionalParameterParameter() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		// native queries don't seem to flush by default ?!?
 		em.flush();
 		Query q = em.createNativeQuery( "select * from Item i where i.intVal=?" );
 		Parameter p = new Parameter() {
 			@Override
 			public String getName() {
 				return null;
 			}
 			@Override
 			public Integer getPosition() {
 				return 1;
 			}
 			@Override
 			public Class getParameterType() {
 				return Integer.class;
 			}
 		};
 		q.setParameter( p, null );
 		Parameter pGotten = q.getParameter( p.getPosition() );
 		List results = q.getResultList();
 		// null != null
 		assertEquals( 0, results.size() );
 		q = em.createNativeQuery( "select * from Item i where i.intVal is null and ? is null" );
 		q.setParameter( p, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		q = em.createNativeQuery( "select * from Item i where i.intVal is null or i.intVal = ?" );
 		q.setParameter( p, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	@SkipForDialects(
 			value = {
 					@SkipForDialect(value = Oracle8iDialect.class, jiraKey = "HHH-10161", comment = "Cannot convert untyped null (assumed to be BINARY type) to NUMBER"),
 					@SkipForDialect(value = PostgreSQL9Dialect.class, jiraKey = "HHH-10312", comment = "Cannot convert untyped null (assumed to be bytea type) to bigint"),
 					@SkipForDialect(value = PostgresPlusDialect.class, jiraKey = "HHH-10312", comment = "Cannot convert untyped null (assumed to be bytea type) to bigint")
 			}
 	)
 	public void testNativeQueryNullNamedParameter() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		// native queries don't seem to flush by default ?!?
 		em.flush();
 		Query q = em.createNativeQuery( "select * from Item i where i.intVal=:iVal" );
 		q.setParameter( "iVal", null );
 		List results = q.getResultList();
 		// null != null
 		assertEquals( 0, results.size() );
 		q = em.createNativeQuery( "select * from Item i where (i.intVal is null) and (:iVal is null)" );
 		q.setParameter( "iVal", null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		q = em.createNativeQuery( "select * from Item i where i.intVal is null or i.intVal = :iVal" );
 		q.setParameter( "iVal", null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-10161"  )
 	public void testNativeQueryNullNamedParameterParameter() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		// native queries don't seem to flush by default ?!?
 		em.flush();
 		Query q = em.createNativeQuery( "select * from Item i where i.intVal=:iVal" );
 		Parameter p = new Parameter() {
 			@Override
 			public String getName() {
 				return "iVal";
 			}
 			@Override
 			public Integer getPosition() {
 				return null;
 			}
 			@Override
 			public Class getParameterType() {
 				return Integer.class;
 			}
 		};
 		q.setParameter( p, null );
 		Parameter pGotten = q.getParameter( p.getName() );
 		List results = q.getResultList();
 		assertEquals( 0, results.size() );
 		q = em.createNativeQuery( "select * from Item i where (i.intVal is null) and (:iVal is null)" );
 		q.setParameter( p, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		q = em.createNativeQuery( "select * from Item i where i.intVal is null or i.intVal = :iVal" );
 		q.setParameter( p, null );
 		results = q.getResultList();
 		assertEquals( 1, results.size() );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	public void testAggregationReturnType() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		item = new Item( "Computer", "Apple II" );
 		em.persist( item );
 		Query q = em.createQuery( "select count(i) from Item i where i.name like :itemName" );
 		q.setParameter( "itemName", "%" );
 		assertTrue( q.getSingleResult() instanceof Long );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	public void testTypeExpression() throws Exception {
 		final EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		final Employee employee = new Employee( "Lukasz", 100.0 );
 		em.persist( employee );
 		final Contractor contractor = new Contractor( "Kinga", 100.0, "Microsoft" );
 		em.persist( contractor );
 		final Query q = em.createQuery( "SELECT e FROM Employee e where TYPE(e) <> Contractor" );
 		final List result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( Arrays.asList( employee ), result );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH_7407" )
 	public void testMultipleParameterLists() throws Exception {
 		final Item item = new Item( "Mouse", "Micro$oft mouse" );
 		final Item item2 = new Item( "Computer", "Dell computer" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		em.persist( item2 );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		List<String> names = Arrays.asList( item.getName() );
 		Query q = em.createQuery( "select item from Item item where item.name in :names or item.name in :names2" );
 		q.setParameter( "names", names );
 		q.setParameter( "names2", names );
 		List result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 1, result.size() );
 
 		List<String> descrs = Arrays.asList( item.getDescr() );
 		q = em.createQuery( "select item from Item item where item.name in :names and ( item.descr is null or item.descr in :descrs )" );
 		q.setParameter( "names", names );
 		q.setParameter( "descrs", descrs );
 		result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 1, result.size() );
 
 		em.getTransaction().begin();
 		em.remove( em.getReference( Item.class, item.getName() ) );
 		em.remove( em.getReference( Item.class, item2.getName() ) );
 		em.getTransaction().commit();
 
 		em.close();
 	}
 
 	@Test
 	@TestForIssue( jiraKey = "HHH_8949" )
 	public void testCacheStoreAndRetrieveModeParameter() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 
 		Query query = em.createQuery( "select item from Item item" );
 
 		query.getHints().clear();
 
 		query.setHint( "javax.persistence.cache.retrieveMode", CacheRetrieveMode.USE );
 		query.setHint( "javax.persistence.cache.storeMode", CacheStoreMode.REFRESH );
 
 		assertEquals( CacheRetrieveMode.USE, query.getHints().get( "javax.persistence.cache.retrieveMode" ) );
 		assertEquals( CacheStoreMode.REFRESH, query.getHints().get( "javax.persistence.cache.storeMode" ) );
 
 		query.getHints().clear();
 
 		query.setHint( "javax.persistence.cache.retrieveMode", "USE" );
 		query.setHint( "javax.persistence.cache.storeMode", "REFRESH" );
 
 		assertEquals( "USE", query.getHints().get( "javax.persistence.cache.retrieveMode" ) );
 		assertEquals( "REFRESH", query.getHints().get( "javax.persistence.cache.storeMode" ) );
 
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testJpaPositionalParameters() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 
 		Query query = em.createQuery( "from Item item where item.name =?1 or item.descr = ?1" );
 		Parameter p1 = query.getParameter( 1 );
 		Assert.assertNotNull( p1 );
 		Assert.assertNotNull( p1.getPosition() );
 		Assert.assertNull( p1.getName() );
 
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testParameterList() throws Exception {
 		final Item item = new Item( "Mouse", "Micro$oft mouse" );
 		final Item item2 = new Item( "Computer", "Dell computer" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		em.persist( item2 );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		Query q = em.createQuery( "select item from Item item where item.name in :names" );
 		//test hint in value and string
 		q.setHint( "org.hibernate.fetchSize", 10 );
 		q.setHint( "org.hibernate.fetchSize", "10" );
 		List params = new ArrayList();
 		params.add( item.getName() );
 		q.setParameter( "names", params );
 		List result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 1, result.size() );
 
 		q = em.createQuery( "select item from Item item where item.name in :names" );
 		//test hint in value and string
 		q.setHint( "org.hibernate.fetchSize", 10 );
 		q.setHint( "org.hibernate.fetchSize", "10" );
 		params.add( item2.getName() );
 		q.setParameter( "names", params );
 		result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 2, result.size() );
 
 		q = em.createQuery( "select item from Item item where item.name in ?1" );
 		params = new ArrayList();
 		params.add( item.getName() );
 		params.add( item2.getName() );
 		// deprecated usage of positional parameter by String
 		q.setParameter( "1", params );
 		result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 2, result.size() );
 		em.remove( result.get( 0 ) );
 		em.remove( result.get( 1 ) );
 		em.getTransaction().commit();
 
 		em.close();
 	}
 
 	@Test
 	public void testParameterListInExistingParens() throws Exception {
 		final Item item = new Item( "Mouse", "Micro$oft mouse" );
 		final Item item2 = new Item( "Computer", "Dell computer" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		em.persist( item2 );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		Query q = em.createQuery( "select item from Item item where item.name in (:names)" );
 		//test hint in value and string
 		q.setHint( "org.hibernate.fetchSize", 10 );
 		q.setHint( "org.hibernate.fetchSize", "10" );
 		List params = new ArrayList();
 		params.add( item.getName() );
 		params.add( item2.getName() );
 		q.setParameter( "names", params );
 		List result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 2, result.size() );
 
 		q = em.createQuery( "select item from Item item where item.name in ( \n :names \n)\n" );
 		//test hint in value and string
 		q.setHint( "org.hibernate.fetchSize", 10 );
 		q.setHint( "org.hibernate.fetchSize", "10" );
 		params = new ArrayList();
 		params.add( item.getName() );
 		params.add( item2.getName() );
 		q.setParameter( "names", params );
 		result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 2, result.size() );
 
 		q = em.createQuery( "select item from Item item where item.name in ( ?1 )" );
 		params = new ArrayList();
 		params.add( item.getName() );
 		params.add( item2.getName() );
 		// deprecated usage of positional parameter by String
 		q.setParameter( "1", params );
 		result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 2, result.size() );
 		em.remove( result.get( 0 ) );
 		em.remove( result.get( 1 ) );
 		em.getTransaction().commit();
 
 		em.close();
 	}
 
 	@Test
 	public void testEscapeCharacter() throws Exception {
 		final Item item = new Item( "Mouse", "Micro_oft mouse" );
 		final Item item2 = new Item( "Computer", "Dell computer" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		em.persist( item2 );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		Query q = em.createQuery( "select item from Item item where item.descr like 'Microk_oft mouse' escape 'k' " );
 		List result = q.getResultList();
 		assertNotNull( result );
 		assertEquals( 1, result.size() );
 		int deleted = em.createQuery( "delete from Item" ).executeUpdate();
 		assertEquals( 2, deleted );
 		em.getTransaction().commit();
 
 		em.close();
 	}
 
 	@Test
 	public void testNativeQueryByEntity() {
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		Statistics stats = em.getEntityManagerFactory().unwrap( SessionFactoryImplementor.class ).getStatistics();
 		stats.clear();
 		assertEquals( 0, stats.getFlushCount() );
 
 		em.getTransaction().begin();
 		item = (Item) em.createNativeQuery( "select * from Item", Item.class ).getSingleResult();
 		assertEquals( 1, stats.getFlushCount() );
 		assertNotNull( item );
 		assertEquals( "Micro$oft mouse", item.getDescr() );
 		em.remove( item );
 		em.getTransaction().commit();
 
 		em.close();
 
 	}
 
 	@Test
 	public void testNativeQueryByResultSet() {
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		item = (Item) em.createNativeQuery( "select name as itemname, descr as itemdescription from Item", "getItem" )
 				.getSingleResult();
 		assertNotNull( item );
 		assertEquals( "Micro$oft mouse", item.getDescr() );
 		em.remove( item );
 		em.getTransaction().commit();
 
 		em.close();
 
 	}
 
 	@Test
 	public void testExplicitPositionalParameter() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Wallet w = new Wallet();
 		w.setBrand( "Lacoste" );
 		w.setModel( "Minimic" );
 		w.setSerial( "0100202002" );
 		em.persist( w );
 		em.getTransaction().commit();
 		em.getTransaction().begin();
 		Query query = em.createQuery( "select w from " + Wallet.class.getName() + " w where w.brand in ?1" );
 		List brands = new ArrayList();
 		brands.add( "Lacoste" );
 		query.setParameter( 1, brands );
 		w = (Wallet) query.getSingleResult();
 		assertNotNull( w );
 		query = em.createQuery( "select w from " + Wallet.class.getName() + " w where w.marketEntrance = ?1" );
 		query.setParameter( 1, new Date(), TemporalType.DATE );
 		//assertNull( query.getSingleResult() );
 		assertEquals( 0, query.getResultList().size() );
 		em.remove( w );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testTemporalTypeBinding() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 
 		Query query = em.createQuery( "select w from " + Wallet.class.getName() + " w where w.marketEntrance = :me" );
 		Parameter parameter = query.getParameter( "me", Date.class );
 		assertEquals( parameter.getParameterType(), Date.class );
 
 		query.setParameter( "me", new Date() );
 		query.setParameter( "me", new Date(), TemporalType.DATE );
 		query.setParameter( "me", new GregorianCalendar(), TemporalType.DATE );
 
 		em.getTransaction().commit();
 		em.close();
 
 	}
 
 	@Test
 	public void testPositionalParameterForms() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Wallet w = new Wallet();
 		w.setBrand( "Lacoste" );
 		w.setModel( "Minimic" );
 		w.setSerial( "0100202002" );
 		em.persist( w );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		// first using jpa-style positional parameter
 		Query query = em.createQuery( "select w from Wallet w where w.brand = ?1" );
 		query.setParameter( 1, "Lacoste" );
 		w = (Wallet) query.getSingleResult();
 		assertNotNull( w );
 
 		// next using jpa-style positional parameter, but as a name (which is how Hibernate core treats these
 		query = em.createQuery( "select w from Wallet w where w.brand = ?1" );
 		// deprecated usage of positional parameter by String
 		query.setParameter( "1", "Lacoste" );
 		w = (Wallet) query.getSingleResult();
 		assertNotNull( w );
 
 		// finally using hql-style positional parameter
 		query = em.createQuery( "select w from Wallet w where w.brand = ?" );
 		query.setParameter( 1, "Lacoste" );
 		w = (Wallet) query.getSingleResult();
 		assertNotNull( w );
 
 		em.remove( w );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testPositionalParameterWithUserError() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Wallet w = new Wallet();
 		w.setBrand( "Lacoste" );
 		w.setModel( "Minimic" );
 		w.setSerial( "0100202002" );
 		em.persist( w );
 		em.flush();
 
 
 		Query query = em.createQuery( "select w from Wallet w where w.brand = ?1 and w.model = ?3" );
 		query.setParameter( 1, "Lacoste" );
 		try {
 			query.setParameter( 2, "Expensive" );
 			fail( "Should fail due to a user error in parameters" );
 		}
 		catch ( IllegalArgumentException e ) {
 			//success
 		}
 		finally {
 			em.getTransaction().rollback();
 			em.close();
 		}
 	}
 
 	@Test
 	public void testNativeQuestionMarkParameter() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Wallet w = new Wallet();
 		w.setBrand( "Lacoste" );
 		w.setModel( "Minimic" );
 		w.setSerial( "0100202002" );
 		em.persist( w );
 		em.getTransaction().commit();
 		em.getTransaction().begin();
 		Query query = em.createNativeQuery( "select * from Wallet w where w.brand = ?", Wallet.class );
 		query.setParameter( 1, "Lacoste" );
 		w = (Wallet) query.getSingleResult();
 		assertNotNull( w );
 		em.remove( w );
 		em.getTransaction().commit();
 		em.close();
 	}
 
 	@Test
 	public void testNativeQueryWithPositionalParameter() {
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		Query query = em.createNativeQuery( "select * from Item where name = ?1", Item.class );
 		query.setParameter( 1, "Mouse" );
 		item = (Item) query.getSingleResult();
 		assertNotNull( item );
 		assertEquals( "Micro$oft mouse", item.getDescr() );
 		query = em.createNativeQuery( "select * from Item where name = ?", Item.class );
 		query.setParameter( 1, "Mouse" );
 		item = (Item) query.getSingleResult();
 		assertNotNull( item );
 		assertEquals( "Micro$oft mouse", item.getDescr() );
 		em.remove( item );
 		em.getTransaction().commit();
 
 		em.close();
 
 	}
 
 	@Test
 	public void testDistinct() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.createQuery( "delete Item" ).executeUpdate();
 		em.createQuery( "delete Distributor" ).executeUpdate();
 		Distributor d1 = new Distributor();
 		d1.setName( "Fnac" );
 		Distributor d2 = new Distributor();
 		d2.setName( "Darty" );
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		item.getDistributors().add( d1 );
 		item.getDistributors().add( d2 );
 		em.persist( d1 );
 		em.persist( d2 );
 		em.persist( item );
 		em.flush();
 		em.clear();
 		Query q = em.createQuery( "select distinct i from Item i left join fetch i.distributors" );
 		item = (Item) q.getSingleResult()
 				;
 		//assertEquals( 1, distinctResult.size() );
 		//item = (Item) distinctResult.get( 0 );
 		assertTrue( Hibernate.isInitialized( item.getDistributors() ) );
 		assertEquals( 2, item.getDistributors().size() );
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	public void testIsNull() throws Exception {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Distributor d1 = new Distributor();
 		d1.setName( "Fnac" );
 		Distributor d2 = new Distributor();
 		d2.setName( "Darty" );
 		Item item = new Item( "Mouse", null );
 		Item item2 = new Item( "Mouse2", "dd" );
 		item.getDistributors().add( d1 );
 		item.getDistributors().add( d2 );
 		em.persist( d1 );
 		em.persist( d2 );
 		em.persist( item );
 		em.persist( item2 );
 		em.flush();
 		em.clear();
 		Query q = em.createQuery(
 				"select i from Item i where i.descr = :descr or (i.descr is null and cast(:descr as string) is null)"
 		);
 		//Query q = em.createQuery( "select i from Item i where (i.descr is null and :descr is null) or (i.descr = :descr");
 		q.setParameter( "descr", "dd" );
 		List result = q.getResultList();
 		assertEquals( 1, result.size() );
 		q.setParameter( "descr", null );
 		result = q.getResultList();
 		assertEquals( 1, result.size() );
 		//item = (Item) distinctResult.get( 0 );
 
 		em.getTransaction().rollback();
 		em.close();
 	}
 
 	@Test
 	public void testUpdateQuery() {
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		assertTrue( em.contains( item ) );
 
 		em.flush();
 		em.clear();
 
 		assertEquals(
 				1, em.createNativeQuery(
 				"update Item set descr = 'Logitech Mouse' where name = 'Mouse'"
 		).executeUpdate()
 		);
 		item = em.find( Item.class, item.getName() );
 		assertEquals( "Logitech Mouse", item.getDescr() );
 		em.remove( item );
 		em.getTransaction().rollback();
 
 		em.close();
 
 	}
 
 	@Test
 	public void testUnavailableNamedQuery() throws Exception {
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		try {
 			em.createNamedQuery( "wrong name" );
 			fail("Wrong named query should raise an exception");
 		}
 		catch (IllegalArgumentException e) {
 			//success
 		}
 		assertTrue(
 				"thrown IllegalArgumentException should of caused transaction to be marked for rollback only",
 				true == em.getTransaction().getRollbackOnly()
 		);
 		em.getTransaction().rollback();		// HHH-8442 changed to rollback since thrown ISE causes
 											// transaction to be marked for rollback only.
 											// No need to remove entity since it was rolled back.
 
 		assertNull(
 				"entity should not of been saved to database since IllegalArgumentException should of" +
 						"caused transaction to be marked for rollback only", em.find( Item.class, item.getName() )
 		);
 		em.close();
 
 	}
 
 	@Test
 	public void testTypedNamedNativeQuery() {
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		em.persist( item );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		item = em.createNamedQuery( "nativeItem1", Item.class ).getSingleResult();
 		item = em.createNamedQuery( "nativeItem2", Item.class ).getSingleResult();
 		assertNotNull( item );
 		assertEquals( "Micro$oft mouse", item.getDescr() );
 		em.remove( item );
 		em.getTransaction().commit();
 
 		em.close();
 	}
 
 	@Test
 	public void testTypedScalarQueries() {
 		EntityManager em = getOrCreateEntityManager();
 		em.getTransaction().begin();
 		Item item = new Item( "Mouse", "Micro$oft mouse" );
 		em.persist( item );
 		assertTrue( em.contains( item ) );
 		em.getTransaction().commit();
 
 		em.getTransaction().begin();
 		Object[] itemData = em.createQuery( "select i.name,i.descr from Item i", Object[].class ).getSingleResult();
 		assertEquals( 2, itemData.length );
 		assertEquals( String.class, itemData[0].getClass() );
 		assertEquals( String.class, itemData[1].getClass() );
 		Tuple itemTuple = em.createQuery( "select i.name,i.descr from Item i", Tuple.class ).getSingleResult();
 		assertEquals( 2, itemTuple.getElements().size() );
 		assertEquals( String.class, itemTuple.get( 0 ).getClass() );
 		assertEquals( String.class, itemTuple.get( 1 ).getClass() );
 		Item itemView = em.createQuery( "select new Item(i.name,i.descr) from Item i", Item.class ).getSingleResult();
 		assertNotNull( itemView );
 		assertEquals( "Micro$oft mouse", itemView.getDescr() );
 		itemView = em.createNamedQuery( "query-construct", Item.class ).getSingleResult();
 		assertNotNull( itemView );
 		assertEquals( "Micro$oft mouse", itemView.getDescr() );
 		em.remove( item );
 		em.getTransaction().commit();
 
 		em.close();
 	}
+
+	@Test
+	@TestForIssue( jiraKey = "HHH-10269")
+	public void testFailingNativeQuery() {
+		final EntityManager entityManager = getOrCreateEntityManager();
+		// Tests that Oracle does not run out of cursors.
+		for (int i = 0; i < 1000; i++) {
+			try {
+				entityManager.createNativeQuery("Select 1 from NotExistedTable").getResultList();
+				fail( "expected PersistenceException" );
+			} catch (PersistenceException e) {
+				// expected
+			}
+		}
+
+	}
 }
