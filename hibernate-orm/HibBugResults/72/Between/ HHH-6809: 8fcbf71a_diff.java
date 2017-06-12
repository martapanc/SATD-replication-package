diff --git a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
index 2fd28dd507..28943af31b 100644
--- a/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
+++ b/hibernate-core/src/main/java/org/hibernate/loader/Loader.java
@@ -661,1948 +661,1948 @@ public abstract class Loader {
 					row[i] = proxy;
 				}
 			}
 		}
 
 		applyPostLoadLocks( row, lockModesArray, session );
 
 		return forcedResultTransformer == null ?
 				getResultColumnOrRow( row, queryParameters.getResultTransformer(), resultSet, session ) :
 				forcedResultTransformer.transformTuple(
 						getResultRow( row, resultSet, session ),
 						getResultRowAliases()
 				)
 		;
 	}
 
 	protected void extractKeysFromResultSet(
 			Loadable[] persisters,
 			QueryParameters queryParameters,
 			ResultSet resultSet,
 			SessionImplementor session,
 			EntityKey[] keys,
 			LockMode[] lockModes,
 			List hydratedObjects) throws SQLException {
 		final int entitySpan = persisters.length;
 
 		final int numberOfPersistersToProcess;
 		final Serializable optionalId = queryParameters.getOptionalId();
 		if ( isSingleRowLoader() && optionalId != null ) {
 			keys[ entitySpan - 1 ] = session.generateEntityKey( optionalId, persisters[ entitySpan - 1 ] );
 			// skip the last persister below...
 			numberOfPersistersToProcess = entitySpan - 1;
 		}
 		else {
 			numberOfPersistersToProcess = entitySpan;
 		}
 
 		final Object[] hydratedKeyState = new Object[numberOfPersistersToProcess];
 
 		for ( int i = 0; i < numberOfPersistersToProcess; i++ ) {
 			final Type idType = persisters[i].getIdentifierType();
 			hydratedKeyState[i] = idType.hydrate( resultSet, getEntityAliases()[i].getSuffixedKeyAliases(), session, null );
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
 
 	protected void applyPostLoadLocks(Object[] row, LockMode[] lockModesArray, SessionImplementor session) {
 	}
 
 	/**
 	 * Read any collection elements contained in a single row of the result set
 	 */
 	private void readCollectionElements(Object[] row, ResultSet resultSet, SessionImplementor session)
 			throws SQLException, HibernateException {
 
 		//TODO: make this handle multiple collection roles!
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 
 			final CollectionAliases[] descriptors = getCollectionAliases();
 			final int[] collectionOwners = getCollectionOwners();
 
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 
 				final boolean hasCollectionOwners = collectionOwners !=null &&
 						collectionOwners[i] > -1;
 				//true if this is a query and we are loading multiple instances of the same collection role
 				//otherwise this is a CollectionInitializer and we are loading up a single collection or batch
 
 				final Object owner = hasCollectionOwners ?
 						row[ collectionOwners[i] ] :
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
 			final SessionImplementor session,
 			final QueryParameters queryParameters,
 			final boolean returnProxies,
 			final ResultTransformer forcedResultTransformer) throws SQLException, HibernateException {
 
 		final RowSelection selection = queryParameters.getRowSelection();
 		final int maxRows = hasMaxRows( selection ) ?
 				selection.getMaxRows().intValue() :
 				Integer.MAX_VALUE;
 
 		final int entitySpan = getEntityPersisters().length;
 
 		final ArrayList hydratedObjects = entitySpan == 0 ? null : new ArrayList( entitySpan * 10 );
 		final PreparedStatement st = prepareQueryStatement( queryParameters, false, session );
 		final ResultSet rs = getResultSet( st, queryParameters.hasAutoDiscoverScalarTypes(), queryParameters.isCallable(), selection, session );
 
 // would be great to move all this below here into another method that could also be used
 // from the new scrolling stuff.
 //
 // Would need to change the way the max-row stuff is handled (i.e. behind an interface) so
 // that I could do the control breaking at the means to know when to stop
 
 		final EntityKey optionalObjectKey = getOptionalObjectKey( queryParameters, session );
 		final LockMode[] lockModesArray = getLockModes( queryParameters.getLockOptions() );
 		final boolean createSubselects = isSubselectLoadingEnabled();
 		final List subselectResultKeys = createSubselects ? new ArrayList() : null;
 		final List results = new ArrayList();
 
 		try {
 
 			handleEmptyCollections( queryParameters.getCollectionKeys(), rs, session );
 
 			EntityKey[] keys = new EntityKey[entitySpan]; //we can reuse it for each row
 
 			LOG.trace( "Processing result set" );
 
 			int count;
 			for ( count = 0; count < maxRows && rs.next(); count++ ) {
 
 				LOG.debugf( "Result set row: %s", count );
 
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
 					subselectResultKeys.add(keys);
 					keys = new EntityKey[entitySpan]; //can't reuse in this case
 				}
 
 			}
 
 			LOG.tracev( "Done processing result set ({0} rows)", count );
 
 		}
 		finally {
 			st.close();
 		}
 
 		initializeEntitiesAndCollections( hydratedObjects, rs, session, queryParameters.isReadOnly( session ) );
 
 		if ( createSubselects ) createSubselects( subselectResultKeys, queryParameters, session );
 
 		return results; //getResultList(results);
 
 	}
 
 	protected boolean isSubselectLoadingEnabled() {
 		return false;
 	}
 
 	protected boolean hasSubselectLoadableCollections() {
 		final Loadable[] loadables = getEntityPersisters();
 		for (int i=0; i<loadables.length; i++ ) {
 			if ( loadables[i].hasSubselectLoadableCollections() ) return true;
 		}
 		return false;
 	}
 
 	private static Set[] transpose( List keys ) {
 		Set[] result = new Set[ ( ( EntityKey[] ) keys.get(0) ).length ];
 		for ( int j=0; j<result.length; j++ ) {
 			result[j] = new HashSet( keys.size() );
 			for ( int i=0; i<keys.size(); i++ ) {
 				result[j].add( ( ( EntityKey[] ) keys.get(i) ) [j] );
 			}
 		}
 		return result;
 	}
 
 	private void createSubselects(List keys, QueryParameters queryParameters, SessionImplementor session) {
 		if ( keys.size() > 1 ) { //if we only returned one entity, query by key is more efficient
 
 			Set[] keySets = transpose(keys);
 
 			Map namedParameterLocMap = buildNamedParameterLocMap( queryParameters );
 
 			final Loadable[] loadables = getEntityPersisters();
 			final String[] aliases = getAliases();
 			final Iterator iter = keys.iterator();
 			while ( iter.hasNext() ) {
 
 				final EntityKey[] rowKeys = (EntityKey[]) iter.next();
 				for ( int i=0; i<rowKeys.length; i++ ) {
 
 					if ( rowKeys[i]!=null && loadables[i].hasSubselectLoadableCollections() ) {
 
 						SubselectFetch subselectFetch = new SubselectFetch(
 								//getSQLString(),
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
 		if ( queryParameters.getNamedParameters()!=null ) {
 			final Map namedParameterLocMap = new HashMap();
 			Iterator piter = queryParameters.getNamedParameters().keySet().iterator();
 			while ( piter.hasNext() ) {
 				String name = (String) piter.next();
 				namedParameterLocMap.put(
 						name,
 						getNamedParameterLocs(name)
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
 			final SessionImplementor session,
 			final boolean readOnly)
 	throws HibernateException {
 
 		final CollectionPersister[] collectionPersisters = getCollectionPersisters();
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( collectionPersisters[i].isArray() ) {
 					//for arrays, we should end the collection load before resolving
 					//the entities, since the actual array instances are not instantiated
 					//during loading
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
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
 
 		if ( hydratedObjects!=null ) {
 			int hydratedObjectsSize = hydratedObjects.size();
 			LOG.tracev( "Total objects hydrated: {0}", hydratedObjectsSize );
 			for ( int i = 0; i < hydratedObjectsSize; i++ ) {
 				TwoPhaseLoad.initializeEntity( hydratedObjects.get(i), readOnly, session, pre, post );
 			}
 		}
 
 		if ( collectionPersisters != null ) {
 			for ( int i=0; i<collectionPersisters.length; i++ ) {
 				if ( !collectionPersisters[i].isArray() ) {
 					//for sets, we should end the collection load after resolving
 					//the entities, since we might call hashCode() on the elements
 					//TODO: or we could do this polymorphically, and have two
 					//      different operations implemented differently for arrays
 					endCollectionLoad( resultSetId, session, collectionPersisters[i] );
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
 				.getCollectionLoadContext( ( ResultSet ) resultSetId )
 				.endLoadingCollections( collectionPersister );
 	}
 
 	/**
 	 * Determine the actual ResultTransformer that will be used to
 	 * transform query results.
 	 *
 	 * @param resultTransformer the specified result transformer
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
 	 * @return true, if getResultColumnOrRow() transforms the results; false, otherwise
 	 */
 	protected boolean areResultSetRowsTransformedImmediately() {
 		return false;
 	}
 
 	/**
 	 * Returns the aliases that corresponding to a result row.
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
 	protected Object getResultColumnOrRow(Object[] row, ResultTransformer transformer, ResultSet rs, SessionImplementor session)
 			throws SQLException, HibernateException {
 		return row;
 	}
 
 	protected boolean[] includeInResultRow() {
 		return null;
 	}
 
 	protected Object[] getResultRow(Object[] row,
 														 ResultSet rs,
 														 SessionImplementor session)
 			throws SQLException, HibernateException {
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
 						boolean isOneToOneAssociation = ownerAssociationTypes!=null &&
 								ownerAssociationTypes[i]!=null &&
 								ownerAssociationTypes[i].isOneToOne();
 						if ( isOneToOneAssociation ) {
 							persistenceContext.addNullProperty( ownerKey,
 									ownerAssociationTypes[i].getPropertyName() );
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
 				LOG.debugf( "Found row of collection: %s",
 						MessageHelper.collectionInfoString( persister, collectionRowKey, getFactory() ) );
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
 				LOG.debugf( "Result set contains (possibly empty) collection: %s",
 						MessageHelper.collectionInfoString( persister, optionalKey, getFactory() ) );
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
 			// this is a collection initializer, so we must create a collection
 			// for each of the passed-in keys, to account for the possibility
 			// that the collection is empty and has no rows in the result set
 
 			CollectionPersister[] collectionPersisters = getCollectionPersisters();
 			for ( int j=0; j<collectionPersisters.length; j++ ) {
 				for ( int i = 0; i < keys.length; i++ ) {
 					//handle empty collections
 
 					if ( LOG.isDebugEnabled() ) {
 						LOG.debugf( "Result set contains (possibly empty) collection: %s",
 								MessageHelper.collectionInfoString( collectionPersisters[j], keys[i], getFactory() ) );
 					}
 
 					session.getPersistenceContext()
 							.getLoadContexts()
 							.getCollectionLoadContext( ( ResultSet ) resultSetId )
 							.getLoadingCollection( collectionPersisters[j], keys[i] );
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
 
 			Type idType = persister.getIdentifierType();
 			resultId = (Serializable) idType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedKeyAliases(),
 					session,
 					null //problematic for <key-many-to-one>!
 				);
 
 			final boolean idIsResultId = id != null &&
 					resultId != null &&
 					idType.isEqual( id, resultId, factory );
 
 			if ( idIsResultId ) resultId = id; //use the id passed in
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
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		Object version = session.getPersistenceContext().getEntry( entity ).getVersion();
 
 		if ( version != null ) { //null version means the object is in the process of being loaded somewhere else in the ResultSet
 			VersionType versionType = persister.getVersionType();
 			Object currentVersion = versionType.nullSafeGet(
 					rs,
 					getEntityAliases()[i].getSuffixedVersionAliases(),
 					session,
 					null
 				);
 			if ( !versionType.isEqual(version, currentVersion) ) {
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
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		final int cols = persisters.length;
 		final EntityAliases[] descriptors = getEntityAliases();
 
 		if ( LOG.isDebugEnabled() ) LOG.debugf( "Result row: %s", StringHelper.toString( keys ) );
 
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
 	        final LockMode lockMode,
 	        final SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( !persister.isInstance( object ) ) {
 			throw new WrongClassException(
 					"loaded object was of wrong class " + object.getClass(),
 					key.getIdentifier(),
 					persister.getEntityName()
 				);
 		}
 
 		if ( LockMode.NONE != lockMode && upgradeLocks() ) { //no point doing this if NONE was requested
 
 			final boolean isVersionCheckNeeded = persister.isVersioned() &&
 					session.getPersistenceContext().getEntry(object)
 							.getLockMode().lessThan( lockMode );
 			// we don't need to worry about existing version being uninitialized
 			// because this block isn't called by a re-entrant load (re-entrant
 			// loads _always_ have lock mode NONE)
 			if (isVersionCheckNeeded) {
 				//we only check the version when _upgrading_ lock modes
 				checkVersion( i, persister, key.getIdentifier(), object, rs, session );
 				//we need to upgrade the lock mode to the mode requested
 				session.getPersistenceContext().getEntry(object)
 						.setLockMode(lockMode);
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
 		return array!=null && array[i];
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
 	        final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		final Serializable id = key.getIdentifier();
 
 		// Get the persister for the _subclass_
 		final Loadable persister = (Loadable) getFactory().getEntityPersister( instanceEntityName );
 
 		if ( LOG.isTraceEnabled() )
 			LOG.tracev( "Initializing object from ResultSet: {0}", MessageHelper.infoString( persister, id, getFactory() ) );
 
 		boolean eagerPropertyFetch = isEagerPropertyFetchEnabled(i);
 
 		// add temp entry so that the next step is circular-reference
 		// safe - only needed because some types don't take proper
 		// advantage of two-phase-load (esp. components)
 		TwoPhaseLoad.addUninitializedEntity(
 				key,
 				object,
 				persister,
 				lockMode,
 				!eagerPropertyFetch,
 				session
 			);
 
 		//This is not very nice (and quite slow):
 		final String[][] cols = persister == rootPersister ?
 				getEntityAliases()[i].getSuffixedPropertyAliases() :
 				getEntityAliases()[i].getSuffixedPropertyAliases(persister);
 
 		final Object[] values = persister.hydrate(
 				rs,
 				id,
 				object,
 				rootPersister,
 				cols,
 				eagerPropertyFetch,
 				session
 			);
 
 		final Object rowId = persister.hasRowId() ? rs.getObject(rowIdAlias) : null;
 
 		final AssociationType[] ownerAssociationTypes = getOwnerAssociationTypes();
 		if ( ownerAssociationTypes != null && ownerAssociationTypes[i] != null ) {
 			String ukName = ownerAssociationTypes[i].getRHSUniqueKeyPropertyName();
 			if (ukName!=null) {
 				final int index = ( (UniqueKeyLoadable) persister ).getPropertyIndex(ukName);
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
 				!eagerPropertyFetch,
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
 	        final SessionImplementor session)
 	throws HibernateException, SQLException {
 
 		if ( persister.hasSubclasses() ) {
 
 			// Code to handle subclasses of topClass
 			Object discriminatorValue = persister.getDiscriminatorType().nullSafeGet(
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
 	private void advance(final ResultSet rs, final RowSelection selection)
 			throws SQLException {
 
 		final int firstRow = getFirstRow( selection );
 		if ( firstRow != 0 ) {
 			if ( getFactory().getSettings().isScrollableResultSetsEnabled() ) {
 				// we can go straight to the first required row
 				rs.absolute( firstRow );
 			}
 			else {
 				// we need to step through the rows one row at a time (slow)
 				for ( int m = 0; m < firstRow; m++ ) rs.next();
 			}
 		}
 	}
 
 	private static boolean hasMaxRows(RowSelection selection) {
-		return selection != null && selection.getMaxRows() != null;
+		return selection != null && selection.getMaxRows() != null && selection.getMaxRows() > 0;
 	}
 
 	private static int getFirstRow(RowSelection selection) {
 		if ( selection == null || selection.getFirstRow() == null ) {
 			return 0;
 		}
 		else {
 			return selection.getFirstRow().intValue();
 		}
 	}
 
 	private int interpretFirstRow(int zeroBasedFirstResult) {
 		return getFactory().getDialect().convertToFirstRowValue( zeroBasedFirstResult );
 	}
 
 	/**
 	 * Should we pre-process the SQL string, adding a dialect-specific
 	 * LIMIT clause.
 	 */
 	private static boolean useLimit(final RowSelection selection, final Dialect dialect) {
 		return dialect.supportsLimit() && hasMaxRows( selection );
 	}
 
 	private ScrollMode getScrollMode(boolean scroll, boolean hasFirstRow, boolean useLimitOffSet, QueryParameters queryParameters) {
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
 	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
 	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
 	 * limit parameters.
 	 */
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
 		boolean useLimitOffset = hasFirstRow && useLimit && dialect.supportsLimitOffset();
 		boolean callable = queryParameters.isCallable();
 
 		final boolean canScroll = getFactory().getSettings().isScrollableResultSetsEnabled();
 		final boolean useScrollableResultSetToSkip = hasFirstRow &&
 				!useLimitOffset && canScroll;
 		final ScrollMode scrollMode = getScrollMode( scroll, hasFirstRow, useLimit, queryParameters );
 //
 //		if(canScroll && ( scroll || useScrollableResultSetToSkip )){
 //			 scrollMode = scroll ? queryParameters.getScrollMode() : ScrollMode.SCROLL_INSENSITIVE;
 //		}else{
 //			scrollMode = null;
 //		}
 		if ( useLimit ) {
 			sql = dialect.getLimitString(
 					sql.trim(), //use of trim() here is ugly?
 					useLimitOffset ? getFirstRow(selection) : 0,
 					getMaxOrLimit(selection, dialect)
 				);
 		}
 
 		sql = preprocessSQL( sql, queryParameters, dialect );
 
 		PreparedStatement st = null;
 
 
 		st = session.getTransactionCoordinator().getJdbcCoordinator().getStatementPreparer().prepareQueryStatement(
 				sql,
 				callable,
 				scrollMode
 		);
 
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
 
 			// handle lock timeout...
 			LockOptions lockOptions = queryParameters.getLockOptions();
 			if ( lockOptions != null ) {
 				if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
                     if (!dialect.supportsLockTimeouts()) LOG.debugf("Lock timeout [%s] requested but dialect reported to not support lock timeouts",
                                                                     lockOptions.getTimeOut());
                     else if (dialect.isLockTimeoutParameterized()) st.setInt(col++, lockOptions.getTimeOut());
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
 
 	/**
 	 * Some dialect-specific LIMIT clauses require the maximum last row number
 	 * (aka, first_row_number + total_row_count), while others require the maximum
 	 * returned row count (the total maximum number of rows to return).
 	 *
 	 * @param selection The selection criteria
 	 * @param dialect The dialect
 	 * @return The appropriate value to bind into the limit clause.
 	 */
 	private static int getMaxOrLimit(final RowSelection selection, final Dialect dialect) {
 		final int firstRow = dialect.convertToFirstRowValue( getFirstRow( selection ) );
 		final int lastRow = selection.getMaxRows().intValue();
 		if ( dialect.useMaxForLimit() ) {
 			return lastRow + firstRow;
 		}
 		else {
 			return lastRow;
 		}
 	}
 
 	/**
 	 * Bind parameter values needed by the dialect-specific LIMIT clause.
 	 *
 	 * @param statement The statement to which to bind limit param values.
 	 * @param index The bind position from which to start binding
 	 * @param selection The selection object containing the limit information.
 	 * @return The number of parameter values bound.
 	 * @throws java.sql.SQLException Indicates problems binding parameter values.
 	 */
 	private int bindLimitParameters(
 			final PreparedStatement statement,
 			final int index,
 			final RowSelection selection) throws SQLException {
 		Dialect dialect = getFactory().getDialect();
 		if ( !dialect.supportsVariableLimit() ) {
 			return 0;
 		}
 		if ( !hasMaxRows( selection ) ) {
 			throw new AssertionFailure( "no max results set" );
 		}
 		int firstRow = interpretFirstRow( getFirstRow( selection ) );
 		int lastRow = getMaxOrLimit( selection, dialect );
 		boolean hasFirstRow = dialect.supportsLimitOffset() && ( firstRow > 0 || dialect.forceLimitUsage() );
 		boolean reverse = dialect.bindLimitParametersInReverseOrder();
 		if ( hasFirstRow ) {
 			statement.setInt( index + ( reverse ? 1 : 0 ), firstRow );
 		}
 		statement.setInt( index + ( reverse || !hasFirstRow ? 0 : 1 ), lastRow );
 		return hasFirstRow ? 2 : 1;
 	}
 
 	/**
 	 * Use JDBC API to limit the number of rows returned by the SQL query if necessary
 	 */
 	private void setMaxRows(
 			final PreparedStatement st,
 			final RowSelection selection) throws SQLException {
 		if ( hasMaxRows( selection ) ) {
 			st.setMaxRows( selection.getMaxRows().intValue() + interpretFirstRow( getFirstRow( selection ) ) );
 		}
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
 			Iterator iter = namedParams.entrySet().iterator();
 			final boolean debugEnabled = LOG.isDebugEnabled();
 			int result = 0;
 			while ( iter.hasNext() ) {
 				Map.Entry e = ( Map.Entry ) iter.next();
 				String name = ( String ) e.getKey();
 				TypedValue typedval = ( TypedValue ) e.getValue();
 				int[] locs = getNamedParameterLocs( name );
 				for ( int i = 0; i < locs.length; i++ ) {
 					if ( debugEnabled ) LOG.debugf( "bindNamedParameters() %s -> %s [%s]", typedval.getValue(), name, locs[i] + startIndex );
 					typedval.getType().nullSafeSet( statement, typedval.getValue(), locs[i] + startIndex, session );
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
 	 * Fetch a <tt>PreparedStatement</tt>, call <tt>setMaxRows</tt> and then execute it,
 	 * advance to the first result and return an SQL <tt>ResultSet</tt>
 	 */
 	protected final ResultSet getResultSet(
 	        final PreparedStatement st,
 	        final boolean autodiscovertypes,
 	        final boolean callable,
 	        final RowSelection selection,
 	        final SessionImplementor session)
 	throws SQLException, HibernateException {
 
 		ResultSet rs = null;
 		try {
 			Dialect dialect = getFactory().getDialect();
 			rs = st.executeQuery();
 			rs = wrapResultSetIfEnabled( rs , session );
 
 			if ( !dialect.supportsLimitOffset() || !useLimit( selection, dialect ) ) {
 				advance( rs, selection );
 			}
 
 			if ( autodiscovertypes ) {
 				autoDiscoverTypes( rs );
 			}
 			return rs;
 		}
 		catch ( SQLException sqle ) {
 			st.close();
 			throw sqle;
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
 				LOG.debugf( "Wrapping result set [%s]", rs );
 				return session.getFactory()
 						.getJdbcServices()
 						.getResultSetWrapper().wrap( rs, retreiveColumnNameToIndexCache( rs ) );
 			}
 			catch(SQLException e) {
 				LOG.unableToWrapResultSet( e );
 				return rs;
 			}
 		}
 		else {
 			return rs;
 		}
 	}
 
 	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
 		if ( columnNameCache == null ) {
 			LOG.trace( "Building columnName->columnIndex cache" );
 			columnNameCache = new ColumnNameCache( rs.getMetaData().getColumnCount() );
 		}
 
 		return columnNameCache;
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 * @param persister only needed for logging
 	 * @param lockOptions
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
 			qp.setPositionalParameterTypes( new Type[] { identifierType } );
 			qp.setPositionalParameterValues( new Object[] { id } );
 			qp.setOptionalObject( optionalObject );
 			qp.setOptionalEntityName( optionalEntityName );
 			qp.setOptionalId( optionalIdentifier );
 			qp.setLockOptions( lockOptions );
 			result = doQueryAndInitializeNonLazyCollections( session, qp, false );
 		}
 		catch ( SQLException sqle ) {
 			final Loadable[] persisters = getEntityPersisters();
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load an entity: " +
 			        MessageHelper.infoString( persisters[persisters.length-1], id, identifierType, getFactory() ),
 			        getSQLString()
 				);
 		}
 
 		LOG.debugf( "Done entity load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that load entities
 	 * @param persister only needed for logging
 	 */
 	protected final List loadEntity(
 	        final SessionImplementor session,
 	        final Object key,
 	        final Object index,
 	        final Type keyType,
 	        final Type indexType,
 	        final EntityPersister persister) throws HibernateException {
 
 		LOG.debugf( "Loading collection element by index" );
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters(
 							new Type[] { keyType, indexType },
 							new Object[] { key, index }
 					),
 					false
 			);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not collection element by index",
 			        getSQLString()
 				);
 		}
 
 		LOG.debugf( "Done entity load" );
 
 		return result;
 
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
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Batch loading entity: %s", MessageHelper.infoString( persister, ids, getFactory() ) );
 
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
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not load an entity batch: " +
 			        MessageHelper.infoString( getEntityPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 
 		LOG.debugf( "Done entity batch load" );
 
 		return result;
 
 	}
 
 	/**
 	 * Called by subclasses that initialize collections
 	 */
 	public final void loadCollection(
 	        final SessionImplementor session,
 	        final Serializable id,
 	        final Type type) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ) );
 
 		Serializable[] ids = new Serializable[]{id};
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( new Type[]{type}, ids, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize a collection: " +
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], id, getFactory() ),
 					getSQLString()
 				);
 		}
 
 		LOG.debugf( "Done loading collection" );
 
 	}
 
 	/**
 	 * Called by wrappers that batch initialize collections
 	 */
 	public final void loadCollectionBatch(
 	        final SessionImplementor session,
 	        final Serializable[] ids,
 	        final Type type) throws HibernateException {
 
 		if ( LOG.isDebugEnabled() )
 			LOG.debugf( "Batch loading collection: %s",
 					MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ) );
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections(
 					session,
 					new QueryParameters( idTypes, ids, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not initialize a collection batch: " +
 			        MessageHelper.collectionInfoString( getCollectionPersisters()[0], ids, getFactory() ),
 			        getSQLString()
 				);
 		}
 
 		LOG.debugf( "Done batch load" );
 
 	}
 
 	/**
 	 * Called by subclasses that batch initialize collections
 	 */
 	protected final void loadCollectionSubselect(
 	        final SessionImplementor session,
 	        final Serializable[] ids,
 	        final Object[] parameterValues,
 	        final Type[] parameterTypes,
 	        final Map namedParameters,
 	        final Type type) throws HibernateException {
 
 		Type[] idTypes = new Type[ids.length];
 		Arrays.fill( idTypes, type );
 		try {
 			doQueryAndInitializeNonLazyCollections( session,
 					new QueryParameters( parameterTypes, parameterValues, namedParameters, ids ),
 					true
 				);
 		}
 		catch ( SQLException sqle ) {
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
 	        final Set querySpaces,
 	        final Type[] resultTypes) throws HibernateException {
 
 		final boolean cacheable = factory.getSettings().isQueryCacheEnabled() &&
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
 			final Set querySpaces,
 			final Type[] resultTypes) {
 
 		QueryCache queryCache = factory.getQueryCache( queryParameters.getCacheRegion() );
 
 		QueryKey key = generateQueryKey( session, queryParameters );
 
 		if ( querySpaces == null || querySpaces.size() == 0 )
 			LOG.tracev( "Unexpected querySpaces is {0}", ( querySpaces == null ? querySpaces : "empty" ) );
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
 			final Set querySpaces,
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
 
 	private void putResultInQueryCache(
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
 		return doList( session, queryParameters, null);
 	}
 
 	private List doList(final SessionImplementor session,
 						final QueryParameters queryParameters,
 						final ResultTransformer forcedResultTransformer)
 			throws HibernateException {
 
 		final boolean stats = getFactory().getStatistics().isStatisticsEnabled();
 		long startTime = 0;
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		List result;
 		try {
 			result = doQueryAndInitializeNonLazyCollections( session, queryParameters, true, forcedResultTransformer );
 		}
 		catch ( SQLException sqle ) {
 			throw factory.getSQLExceptionHelper().convert(
 			        sqle,
 			        "could not execute query",
 			        getSQLString()
 				);
 		}
 
 		if ( stats ) {
 			getFactory().getStatisticsImplementor().queryExecuted(
 					getQueryIdentifier(),
 					result.size(),
 					System.currentTimeMillis() - startTime
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
 		return;
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
 	 * @return The ScrollableResults instance.
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
 		if ( stats ) startTime = System.currentTimeMillis();
 
 		try {
 
 			PreparedStatement st = prepareQueryStatement( queryParameters, true, session );
 			ResultSet rs = getResultSet(st, queryParameters.hasAutoDiscoverScalarTypes(), queryParameters.isCallable(), queryParameters.getRowSelection(), session);
 
 			if ( stats ) {
 				getFactory().getStatisticsImplementor().queryExecuted(
 						getQueryIdentifier(),
 						0,
 						System.currentTimeMillis() - startTime
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
 		catch ( SQLException sqle ) {
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
 	protected void postInstantiate() {}
 
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
 }
diff --git a/hibernate-core/src/matrix/java/org/hibernate/test/legacy/FooBarTest.java b/hibernate-core/src/matrix/java/org/hibernate/test/legacy/FooBarTest.java
index e2ca04c79a..03ecf6d3c5 100644
--- a/hibernate-core/src/matrix/java/org/hibernate/test/legacy/FooBarTest.java
+++ b/hibernate-core/src/matrix/java/org/hibernate/test/legacy/FooBarTest.java
@@ -1006,2004 +1006,2000 @@ public class FooBarTest extends LegacyTestCase {
 		assertTrue( res[0]==h );
 		q = (Qux) s.get(Qux.class, qid);
 		assertTrue( q.getHolder() == h.getOtherHolder() );
 		s.delete(h);
 		s.delete(q);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQueryCollectionOfValues() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		Glarch g = new Glarch();
 		Serializable gid = s.save(g);
 
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) /*&& !(dialect instanceof MckoiDialect)*/ && !(getDialect() instanceof SAPDBDialect) && !(getDialect() instanceof PointbaseDialect) && !(getDialect() instanceof TimesTenDialect) ) {
 			s.createFilter( baz.getFooArray(), "where size(this.bytes) > 0" ).list();
 			s.createFilter( baz.getFooArray(), "where 0 in elements(this.bytes)" ).list();
 		}
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.createQuery( "from Baz baz join baz.fooSet foo join foo.foo.foo foo2 where foo2.string = 'foo'" ).list();
 		s.createQuery( "from Baz baz join baz.fooArray foo join foo.foo.foo foo2 where foo2.string = 'foo'" ).list();
 		s.createQuery( "from Baz baz join baz.stringDateMap date where index(date) = 'foo'" ).list();
 		s.createQuery( "from Baz baz join baz.topGlarchez g where index(g) = 'A'" ).list();
 		s.createQuery( "select index(g) from Baz baz join baz.topGlarchez g" ).list();
 
 		assertTrue( s.createQuery( "from Baz baz left join baz.stringSet" ).list().size()==3 );
 		baz = (Baz) s.createQuery( "from Baz baz join baz.stringSet str where str='foo'" ).list().get(0);
 		assertTrue( !Hibernate.isInitialized( baz.getStringSet() ) );
 		baz = (Baz) s.createQuery( "from Baz baz left join fetch baz.stringSet" ).list().get(0);
 		assertTrue( Hibernate.isInitialized( baz.getStringSet() ) );
 		assertTrue( s.createQuery( "from Baz baz join baz.stringSet string where string='foo'" ).list().size()==1 );
 		assertTrue( s.createQuery( "from Baz baz inner join baz.components comp where comp.name='foo'" ).list().size()==1 );
 		//List bss = s.find("select baz, ss from Baz baz inner join baz.stringSet ss");
 		s.createQuery( "from Glarch g inner join g.fooComponents comp where comp.fee is not null" ).list();
 		s.createQuery( "from Glarch g inner join g.fooComponents comp join comp.fee fee where fee.count > 0" ).list();
 		s.createQuery( "from Glarch g inner join g.fooComponents comp where comp.fee.count is not null" ).list();
 
 		s.delete(baz);
 		s.delete( s.get(Glarch.class, gid) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testBatchLoad() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		SortedSet stringSet = new TreeSet();
 		stringSet.add("foo");
 		stringSet.add("bar");
 		Set fooSet = new HashSet();
 		for (int i=0; i<3; i++) {
 			Foo foo = new Foo();
 			s.save(foo);
 			fooSet.add(foo);
 		}
 		baz.setFooSet(fooSet);
 		baz.setStringSet(stringSet);
 		s.save(baz);
 		Baz baz2 = new Baz();
 		fooSet = new HashSet();
 		for (int i=0; i<2; i++) {
 			Foo foo = new Foo();
 			s.save(foo);
 			fooSet.add(foo);
 		}
 		baz2.setFooSet(fooSet);
 		s.save(baz2);
 		Baz baz3 = new Baz();
 		stringSet = new TreeSet();
 		stringSet.add("foo");
 		stringSet.add("baz");
 		baz3.setStringSet(stringSet);
 		s.save(baz3);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		baz2 = (Baz) s.load( Baz.class, baz2.getCode() );
 		baz3 = (Baz) s.load( Baz.class, baz3.getCode() );
 		assertFalse( Hibernate.isInitialized(baz.getFooSet()) || Hibernate.isInitialized(baz2.getFooSet()) || Hibernate.isInitialized(baz3.getFooSet()) );
 		assertFalse( Hibernate.isInitialized(baz.getStringSet()) || Hibernate.isInitialized(baz2.getStringSet()) || Hibernate.isInitialized(baz3.getStringSet()) );
 		assertTrue( baz.getFooSet().size()==3 );
 		assertTrue( Hibernate.isInitialized(baz.getFooSet()) && Hibernate.isInitialized(baz2.getFooSet()) && Hibernate.isInitialized(baz3.getFooSet()));
 		assertTrue( baz2.getFooSet().size()==2 );
 		assertTrue( baz3.getStringSet().contains("baz") );
 		assertTrue( Hibernate.isInitialized(baz.getStringSet()) && Hibernate.isInitialized(baz2.getStringSet()) && Hibernate.isInitialized(baz3.getStringSet()));
 		assertTrue( baz.getStringSet().size()==2 && baz2.getStringSet().size()==0 );
 		s.delete(baz);
 		s.delete(baz2);
 		s.delete(baz3);
 		Iterator iter = new JoinedIterator( new Iterator[] { baz.getFooSet().iterator(), baz2.getFooSet().iterator() } );
 		while ( iter.hasNext() ) s.delete( iter.next() );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetchInitializedCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Collection fooBag = new ArrayList();
 		fooBag.add( new Foo() );
 		fooBag.add( new Foo() );
 		baz.setFooBag( fooBag );
 		s.save(baz);
 		s.flush();
 		fooBag = baz.getFooBag();
 		s.createQuery( "from Baz baz left join fetch baz.fooBag" ).list();
 		assertTrue( fooBag == baz.getFooBag() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		Object bag = baz.getFooBag();
 		assertFalse( Hibernate.isInitialized( bag ) );
 		s.createQuery( "from Baz baz left join fetch baz.fooBag" ).list();
 		assertTrue( bag==baz.getFooBag() );
 		assertTrue( baz.getFooBag().size() == 2 );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLateCollectionAdd() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		List l = new ArrayList();
 		baz.setStringList(l);
 		l.add( "foo" );
 		Serializable id = s.save(baz);
 		l.add("bar");
 		s.flush();
 		l.add( "baz" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, id);
 		assertTrue( baz.getStringList().size() == 3 && baz.getStringList().contains( "bar" ) );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testUpdate() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		s.save( foo );
 		s.getTransaction().commit();
 		s.close();
 
 		foo = (Foo) SerializationHelper.deserialize( SerializationHelper.serialize(foo) );
 
 		s = openSession();
 		s.beginTransaction();
 		FooProxy foo2 = (FooProxy) s.load( Foo.class, foo.getKey() );
 		foo2.setString("dirty");
 		foo2.setBoolean( new Boolean( false ) );
 		foo2.setBytes( new byte[] {1, 2, 3} );
 		foo2.setDate( null );
 		foo2.setShort( new Short( "69" ) );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo2.setString( "dirty again" );
 		s.update(foo2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo2.setString( "dirty again 2" );
 		s.update( foo2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Foo foo3 = new Foo();
 		s.load( foo3, foo.getKey() );
 		// There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
 		assertTrue( "update", foo2.equalsFoo(foo3) );
 		s.delete( foo3 );
 		doDelete( s, "from Glarch" );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testListRemove() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz b = new Baz();
 		List stringList = new ArrayList();
 		List feeList = new ArrayList();
 		b.setFees(feeList);
 		b.setStringList(stringList);
 		feeList.add( new Fee() );
 		feeList.add( new Fee() );
 		feeList.add( new Fee() );
 		feeList.add( new Fee() );
 		stringList.add("foo");
 		stringList.add("bar");
 		stringList.add("baz");
 		stringList.add("glarch");
 		s.save(b);
 		s.flush();
 		stringList.remove(1);
 		feeList.remove(1);
 		s.flush();
 		s.evict(b);
 		s.refresh(b);
 		assertTrue( b.getFees().size()==3 );
 		stringList = b.getStringList();
 		assertTrue(
 			stringList.size()==3 &&
 			"baz".equals( stringList.get(1) ) &&
 			"foo".equals( stringList.get(0) )
 		);
 		s.delete(b);
 		doDelete( s, "from Fee" );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetchInitializedCollectionDupe() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Collection fooBag = new ArrayList();
 		fooBag.add( new Foo() );
 		fooBag.add( new Foo() );
 		baz.setFooBag(fooBag);
 		s.save( baz );
 		s.flush();
 		fooBag = baz.getFooBag();
 		s.createQuery( "from Baz baz left join fetch baz.fooBag" ).list();
 		assertTrue( Hibernate.isInitialized( fooBag ) );
 		assertTrue( fooBag == baz.getFooBag() );
 		assertTrue( baz.getFooBag().size() == 2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		Object bag = baz.getFooBag();
 		assertFalse( Hibernate.isInitialized(bag) );
 		s.createQuery( "from Baz baz left join fetch baz.fooBag" ).list();
 		assertTrue( Hibernate.isInitialized( bag ) );
 		assertTrue( bag==baz.getFooBag() );
 		assertTrue( baz.getFooBag().size()==2 );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testSortables() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz b = new Baz();
 		b.setName("name");
 		SortedSet ss = new TreeSet();
 		ss.add( new Sortable("foo") );
 		ss.add( new Sortable("bar") );
 		ss.add( new Sortable("baz") );
 		b.setSortablez(ss);
 		s.save(b);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Criteria cr = s.createCriteria(Baz.class);
 		cr.setFetchMode( "topGlarchez", FetchMode.SELECT );
 		List result = cr
 			.addOrder( Order.asc("name") )
 			.list();
 		assertTrue( result.size()==1 );
 		b = (Baz) result.get(0);
 		assertTrue( b.getSortablez().size()==3 );
 		assertEquals( ( (Sortable) b.getSortablez().iterator().next() ).getName(), "bar" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		result = s.createQuery("from Baz baz left join fetch baz.sortablez order by baz.name asc")
 			.list();
 		b = (Baz) result.get(0);
 		assertTrue( b.getSortablez().size()==3 );
 		assertEquals( ( (Sortable) b.getSortablez().iterator().next() ).getName(), "bar" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		result = s.createQuery("from Baz baz order by baz.name asc")
 			.list();
 		b = (Baz) result.get(0);
 		assertTrue( b.getSortablez().size()==3 );
 		assertEquals( ( (Sortable) b.getSortablez().iterator().next() ).getName(), "bar" );
 		s.delete(b);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFetchList() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		Foo foo = new Foo();
 		s.save(foo);
 		Foo foo2 = new Foo();
 		s.save(foo2);
 		s.flush();
 		List list = new ArrayList();
 		for ( int i=0; i<5; i++ ) {
 			Fee fee = new Fee();
 			list.add(fee);
 		}
 		baz.setFees(list);
 		list = s.createQuery( "from Foo foo, Baz baz left join fetch baz.fees" ).list();
 		assertTrue( Hibernate.isInitialized( ( (Baz) ( (Object[]) list.get(0) )[1] ).getFees() ) );
 		s.delete(foo);
 		s.delete(foo2);
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testBagOneToMany() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		List list = new ArrayList();
 		baz.setBazez(list);
 		list.add( new Baz() );
 		s.save(baz);
 		s.flush();
 		list.add( new Baz() );
 		s.flush();
 		list.add( 0, new Baz() );
 		s.flush();
 		s.delete( list.remove(1) );
 		s.flush();
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testQueryLockMode() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Bar bar = new Bar();
 		s.save(bar);
 		s.flush();
 		bar.setString("changed");
 		Baz baz = new Baz();
 		baz.setFoo(bar);
 		s.save(baz);
 		Query q = s.createQuery("from Foo foo, Bar bar");
 		if ( !(getDialect() instanceof DB2Dialect) ) {
 			q.setLockMode("bar", LockMode.UPGRADE);
 		}
 		Object[] result = (Object[]) q.uniqueResult();
 		Object b = result[0];
 		assertTrue( s.getCurrentLockMode(b)==LockMode.WRITE && s.getCurrentLockMode( result[1] )==LockMode.WRITE );
 		tx.commit();
 
 		tx = s.beginTransaction();
 		assertTrue( s.getCurrentLockMode( b ) == LockMode.NONE );
 		s.createQuery( "from Foo foo" ).list();
 		assertTrue( s.getCurrentLockMode(b)==LockMode.NONE );
 		q = s.createQuery("from Foo foo");
 		q.setLockMode( "foo", LockMode.READ );
 		q.list();
 		assertTrue( s.getCurrentLockMode( b ) == LockMode.READ );
 		s.evict( baz );
 		tx.commit();
 
 		tx = s.beginTransaction();
 		assertTrue( s.getCurrentLockMode(b)==LockMode.NONE );
 		s.delete( s.load( Baz.class, baz.getCode() ) );
 		assertTrue( s.getCurrentLockMode(b)==LockMode.NONE );
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		q = s.createQuery("from Foo foo, Bar bar, Bar bar2");
 		if ( !(getDialect() instanceof DB2Dialect) ) {
 			q.setLockMode("bar", LockMode.UPGRADE);
 		}
 		q.setLockMode("bar2", LockMode.READ);
 		result = (Object[]) q.list().get(0);
 		if ( !(getDialect() instanceof DB2Dialect) ) {
 			assertTrue( s.getCurrentLockMode( result[0] )==LockMode.UPGRADE && s.getCurrentLockMode( result[1] )==LockMode.UPGRADE );
 		}
 		s.delete( result[0] );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToManyBag() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Serializable id = s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, id);
 		baz.getFooBag().add( new Foo() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, id);
 		assertTrue( !Hibernate.isInitialized( baz.getFooBag() ) );
 		assertTrue( baz.getFooBag().size()==1 );
 		if ( !(getDialect() instanceof HSQLDialect) ) assertTrue( Hibernate.isInitialized( baz.getFooBag().iterator().next() ) );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testIdBag() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		s.save(baz);
 		List l = new ArrayList();
 		List l2 = new ArrayList();
 		baz.setIdFooBag(l);
 		baz.setByteBag(l2);
 		l.add( new Foo() );
 		l.add( new Bar() );
 		byte[] bytes = "ffo".getBytes();
 		l2.add(bytes);
 		l2.add( "foo".getBytes() );
 		s.flush();
 		l.add( new Foo() );
 		l.add( new Bar() );
 		l2.add( "bar".getBytes() );
 		s.flush();
 		s.delete( l.remove(3) );
 		bytes[1]='o';
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		assertTrue( baz.getIdFooBag().size()==3 );
 		assertTrue( baz.getByteBag().size()==3 );
 		bytes = "foobar".getBytes();
 		Iterator iter = baz.getIdFooBag().iterator();
 		while ( iter.hasNext() ) s.delete( iter.next() );
 		baz.setIdFooBag(null);
 		baz.getByteBag().add(bytes);
 		baz.getByteBag().add(bytes);
 		assertTrue( baz.getByteBag().size()==5 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		assertTrue( baz.getIdFooBag().size()==0 );
 		assertTrue( baz.getByteBag().size()==5 );
 		baz.getIdFooBag().add( new Foo() );
 		iter = baz.getByteBag().iterator();
 		iter.next();
 		iter.remove();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		assertTrue( baz.getIdFooBag().size()==1 );
 		assertTrue( baz.getByteBag().size()==4 );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	private boolean isOuterJoinFetchingDisabled() {
 		return new Integer(0).equals( ( (SessionFactoryImplementor) sessionFactory() ).getSettings().getMaximumFetchDepth() );
 	}
 
 	@Test
 	public void testForceOuterJoin() throws Exception {
 		if ( isOuterJoinFetchingDisabled() ) {
 			return;
 		}
 
 		Session s = openSession();
 		s.beginTransaction();
 		Glarch g = new Glarch();
 		FooComponent fc = new FooComponent();
 		fc.setGlarch(g);
 		FooProxy f = new Foo();
 		FooProxy f2 = new Foo();
 		f.setComponent(fc);
 		f.setFoo(f2);
 		s.save(f2);
 		Serializable id = s.save(f);
 		Serializable gid = s.getIdentifier( f.getComponent().getGlarch() );
 		s.getTransaction().commit();
 		s.close();
 
 		sessionFactory().evict(Foo.class);
 
 		s = openSession();
 		s.beginTransaction();
 		f = (FooProxy) s.load(Foo.class, id);
 		assertFalse( Hibernate.isInitialized(f) );
 		assertTrue( Hibernate.isInitialized( f.getComponent().getGlarch() ) ); //outer-join="true"
 		assertFalse( Hibernate.isInitialized( f.getFoo() ) ); //outer-join="auto"
 		assertEquals( s.getIdentifier( f.getComponent().getGlarch() ), gid );
 		s.delete(f);
 		s.delete( f.getFoo() );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testEmptyCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Serializable id = s.save( new Baz() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Baz baz = (Baz) s.load(Baz.class, id);
 		Set foos = baz.getFooSet();
 		assertTrue( foos.size() == 0 );
 		Foo foo = new Foo();
 		foos.add( foo );
 		s.save(foo);
 		s.flush();
 		s.delete(foo);
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testOneToOneGenerator() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		X x = new X();
 		Y y = new Y();
 		x.setY(y);
 		y.setTheX(x);
 		x.getXxs().add( new X.XX(x) );
 		x.getXxs().add( new X.XX(x) );
 		Serializable id = s.save(y);
 		assertEquals( id, s.save(x) );
 		s.flush();
 		assertTrue( s.contains(y) && s.contains(x) );
 		s.getTransaction().commit();
 		s.close();
 		assertEquals( new Long(x.getId()), y.getId() );
 
 		s = openSession();
 		s.beginTransaction();
 		x = new X();
 		y = new Y();
 		x.setY(y);
 		y.setTheX(x);
 		x.getXxs().add( new X.XX(x) );
 		s.save(y);
 		s.flush();
 		assertTrue( s.contains(y) && s.contains(x) );
 		s.getTransaction().commit();
 		s.close();
 		assertEquals( new Long(x.getId()), y.getId() );
 
 		s = openSession();
 		s.beginTransaction();
 		x = new X();
 		y = new Y();
 		x.setY(y);
 		y.setTheX(x);
 		x.getXxs().add( new X.XX(x) );
 		x.getXxs().add( new X.XX(x) );
 		id = s.save(x);
 		assertEquals( id, y.getId() );
 		assertEquals( id, new Long( x.getId() ) );
 		s.flush();
 		assertTrue( s.contains(y) && s.contains(x) );
 		doDelete( s, "from X x" );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLimit() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		for ( int i=0; i<10; i++ ) s.save( new Foo() );
 		Iterator iter = s.createQuery("from Foo foo")
 			.setMaxResults(4)
 			.setFirstResult(2)
 			.iterate();
 		int count=0;
 		while ( iter.hasNext() ) {
 		    iter.next();
 			count++;
 		}
 		assertEquals(4, count);
 		iter = s.createQuery("select distinct foo from Foo foo")
 			.setMaxResults(2)
 			.setFirstResult(2)
 			.list()
 			.iterator();
 		count=0;
 		while ( iter.hasNext() ) {
 			iter.next();
 			count++;
 		}
 		assertTrue(count==2);
 		iter = s.createQuery("select distinct foo from Foo foo")
 		.setMaxResults(3)
 		.list()
 		.iterator();
 		count=0;
 		while ( iter.hasNext() ) {
 			iter.next();
 			count++;
 		}
 		assertTrue(count==3);
 		assertEquals( 10, doDelete( s, "from Foo foo" ) );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCustom() throws Exception {
 		GlarchProxy g = new Glarch();
 		Multiplicity m = new Multiplicity();
 		m.count = 12;
 		m.glarch = (Glarch) g;
 		g.setMultiple(m);
 
 		Session s = openSession();
 		s.beginTransaction();
 		Serializable gid = s.save(g);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		//g = (Glarch) s.createQuery( "from Glarch g where g.multiple.count=12" ).list().get(0);
 		s.createQuery( "from Glarch g where g.multiple.count=12" ).list().get( 0 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (Glarch) s.createQuery( "from Glarch g where g.multiple.glarch=g and g.multiple.count=12" ).list().get(0);
 		assertTrue( g.getMultiple()!=null );
 		assertEquals( g.getMultiple().count, 12 );
 		assertSame(g.getMultiple().glarch, g);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, gid);
 		assertTrue( g.getMultiple() != null );
 		assertEquals( g.getMultiple().count, 12 );
 		assertSame( g.getMultiple().glarch, g );
 		s.delete(g);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveAddDelete() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Set bars = new HashSet();
 		baz.setCascadingBars( bars );
 		s.save( baz );
 		s.flush();
 		baz.getCascadingBars().add( new Bar() );
 		s.delete(baz);
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testNamedParams() throws Exception {
 		Bar bar = new Bar();
 		Bar bar2 = new Bar();
 		bar.setName("Bar");
 		bar2.setName("Bar Two");
 		bar.setX( 10 );
 		bar2.setX( 1000 );Baz baz = new Baz();
 		baz.setCascadingBars( new HashSet() );
 		baz.getCascadingBars().add(bar);
 		bar.setBaz(baz);
 
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		s.save( baz );
 		s.save( bar2 );
 
 		List list = s.createQuery(
 				"from Bar bar left join bar.baz baz left join baz.cascadingBars b where bar.name like 'Bar %'"
 		).list();
 		Object row = list.iterator().next();
 		assertTrue( row instanceof Object[] && ( (Object[]) row ).length==3 );
 
 		Query q = s.createQuery("select bar, b from Bar bar left join bar.baz baz left join baz.cascadingBars b where bar.name like 'Bar%'");
 		list = q.list();
 		if ( !(getDialect() instanceof SAPDBDialect) ) assertTrue( list.size()==2 );
 
 		q = s.createQuery("select bar, b from Bar bar left join bar.baz baz left join baz.cascadingBars b where ( bar.name in (:nameList) or bar.name in (:nameList) ) and bar.string = :stringVal");
 		HashSet nameList = new HashSet();
 		nameList.add( "bar" );
 		nameList.add( "Bar" );
 		nameList.add( "Bar Two" );
 		q.setParameterList( "nameList", nameList );
 		q.setParameter( "stringVal", "a string" );
 		list = q.list();
 		if ( !(getDialect() instanceof SAPDBDialect) ) assertTrue( list.size()==2 );
 
 		try {
 			q.setParameterList("nameList", (Collection)null);
 			fail("Should throw an queryexception when passing a null!");
 		} catch (QueryException qe) {
 			//should happen
 		}
 
 		q = s.createQuery("select bar, b from Bar bar inner join bar.baz baz inner join baz.cascadingBars b where bar.name like 'Bar%'");
 		Object result = q.uniqueResult();
 		assertTrue( result != null );
 		q = s.createQuery("select bar, b from Bar bar left join bar.baz baz left join baz.cascadingBars b where bar.name like :name and b.name like :name");
 		q.setString( "name", "Bar%" );
 		list = q.list();
 		assertTrue( list.size()==1 );
 
 
 		// This test added for issue HB-297 - there is an named parameter in the Order By clause
 		q = s.createQuery("select bar from Bar bar order by ((bar.x - :valueX)*(bar.x - :valueX))");
 		q.setInteger( "valueX", bar.getX() + 1 );
 		list = q.list();
 		assertTrue( ((Bar) list.get( 0 )).getX() == bar.getX() );
 		q.setInteger( "valueX", bar2.getX() + 1 );
 		list = q.list();
 		assertTrue( ((Bar)list.get(0)).getX() == bar2.getX());
 
 		s.delete(baz);
 		s.delete(bar2);
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	@RequiresDialectFeature(
 			value = DialectChecks.SupportsEmptyInListCheck.class,
 			comment = "Dialect does not support SQL empty in list [x in ()]"
 	)
 	public void testEmptyInListQuery() {
 		Session s = openSession();
 		s.beginTransaction();
 
 		Query q = s.createQuery( "select bar from Bar as bar where bar.name in (:nameList)" );
 		q.setParameterList( "nameList", Collections.EMPTY_LIST );
 		assertEquals( 0, q.list().size() );
 
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testParameterCheck() throws HibernateException {
 		Session s = openSession();
 		try {
 			Query q = s.createQuery("select bar from Bar as bar where bar.x > :myX");
 			q.list();
 			fail("Should throw QueryException for missing myX");
 		}
 		catch (QueryException iae) {
 			// should happen
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		try {
 			Query q = s.createQuery("select bar from Bar as bar where bar.x > ?");
 			q.list();
 			fail("Should throw QueryException for missing ?");
 		}
 		catch (QueryException iae) {
 			// should happen
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		try {
 			Query q = s.createQuery("select bar from Bar as bar where bar.x > ? or bar.short = 1 or bar.string = 'ff ? bb'");
 			q.setInteger(0, 1);
 			q.list();
 		}
 		catch (QueryException iae) {
 			fail("Should not throw QueryException for missing ?");
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		try {
 			Query q = s.createQuery("select bar from Bar as bar where bar.string = ' ? ' or bar.string = '?'");
 			q.list();
 		}
 		catch (QueryException iae) {
 			fail("Should not throw QueryException for ? in quotes");
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		try {
 			Query q = s.createQuery("select bar from Bar as bar where bar.string = ? or bar.string = ? or bar.string = ?");
 			q.setParameter(0, "bull");
 			q.setParameter(2, "shit");
 			q.list();
 			fail("should throw exception telling me i have not set parameter 1");
 		}
 		catch (QueryException iae) {
 			// should happen!
 		}
 		finally {
 			s.close();
 		}
 	}
 
 	@Test
 	public void testDyna() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		GlarchProxy g = new Glarch();
 		g.setName("G");
 		Serializable id = s.save(g);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, id);
 		assertTrue( g.getName().equals("G") );
 		assertTrue( g.getDynaBean().get("foo").equals("foo") && g.getDynaBean().get("bar").equals( new Integer(66) ) );
 		assertTrue( ! (g instanceof Glarch) );
 		g.getDynaBean().put("foo", "bar");
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, id);
 		assertTrue( g.getDynaBean().get("foo").equals("bar") && g.getDynaBean().get("bar").equals( new Integer(66) ) );
 		g.setDynaBean(null);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, id);
 		assertTrue( g.getDynaBean()==null );
 		s.delete(g);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFindByCriteria() throws Exception {
 		if ( getDialect() instanceof DB2Dialect ) {
 			return;
 		}
 
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Foo f = new Foo();
 		s.save( f );
 		s.flush();
 
 		List list = s.createCriteria(Foo.class)
 			.add( Restrictions.eq( "integer", f.getInteger() ) )
 			.add( Restrictions.eqProperty("integer", "integer") )
 			.add( Restrictions.like( "string", f.getString().toUpperCase() ).ignoreCase() )
 			.add( Restrictions.in( "boolean", new Boolean[] { f.getBoolean(), f.getBoolean() } ) )
 			.setFetchMode("foo", FetchMode.JOIN)
 			.setFetchMode("baz", FetchMode.SELECT)
 			.setFetchMode("abstracts", FetchMode.JOIN)
 			.list();
 		assertTrue( list.size() == 1 && list.get( 0 ) == f );
 
 		list = s.createCriteria(Foo.class).add(
 				Restrictions.disjunction()
 					.add( Restrictions.eq( "integer", f.getInteger() ) )
 					.add( Restrictions.like( "string", f.getString() ) )
 					.add( Restrictions.eq( "boolean", f.getBoolean() ) )
 			)
 			.add( Restrictions.isNotNull("boolean") )
 			.list();
 		assertTrue( list.size() == 1 && list.get( 0 ) == f );
 
 		Foo example = new Foo();
 		example.setString("a STRing");
 		list = s.createCriteria(Foo.class).add(
 			Example.create(example)
 				.excludeZeroes()
 				.ignoreCase()
 				.excludeProperty("bool")
 				.excludeProperty("char")
 				.excludeProperty("yesno")
 			)
 			.list();
 		assertTrue(
 				"Example API without like did not work correctly, size was " + list.size(),
 				list.size() == 1 && list.get( 0 ) == f
 		);
 		example.setString("rin");
 
 		list = s.createCriteria(Foo.class).add(
 			Example.create(example)
 				.excludeZeroes()
 				.enableLike(MatchMode.ANYWHERE)
 				.excludeProperty("bool")
 				.excludeProperty("char")
 				.excludeProperty("yesno")
 			)
 			.list();
 		assertTrue( "Example API without like did not work correctly, size was " + list.size(), list.size()==1 && list.get(0)==f );
 
 		list = s.createCriteria(Foo.class)
 			.add( Restrictions.or(
 					Restrictions.and(
 					Restrictions.eq( "integer", f.getInteger() ),
 					Restrictions.like( "string", f.getString() )
 				),
 				Restrictions.eq( "boolean", f.getBoolean() )
 			) )
 			.list();
 		assertTrue( list.size()==1 && list.get(0)==f );
 		list = s.createCriteria(Foo.class)
 			.setMaxResults(5)
 			.addOrder( Order.asc("date") )
 			.list();
 		assertTrue( list.size()==1 && list.get(0)==f );
-		if(!(getDialect() instanceof TimesTenDialect || getDialect() instanceof HSQLDialect)) {
-			list = s.createCriteria(Foo.class).setMaxResults(0).list();
-			assertTrue( list.size()==0 );
-		}
 		list = s.createCriteria(Foo.class)
 			.setFirstResult(1)
 			.addOrder( Order.asc("date") )
 			.addOrder( Order.desc("string") )
 			.list();
 		assertTrue( list.size() == 0 );
 		list = s.createCriteria(Foo.class)
 			.setFetchMode( "component.importantDates", FetchMode.JOIN )
 			.list();
 		assertTrue( list.size() == 3 );
 
 		list = s.createCriteria(Foo.class)
 			.setFetchMode( "component.importantDates", FetchMode.JOIN )
 			.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
 			.list();
 		assertTrue( list.size()==1 );
 
 		f.setFoo( new Foo() );
 		s.save( f.getFoo() );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		list = s.createCriteria(Foo.class)
 			.add( Restrictions.eq( "integer", f.getInteger() ) )
 			.add( Restrictions.like( "string", f.getString() ) )
 			.add( Restrictions.in( "boolean", new Boolean[] { f.getBoolean(), f.getBoolean() } ) )
 			.add( Restrictions.isNotNull("foo") )
 			.setFetchMode( "foo", FetchMode.JOIN )
 			.setFetchMode( "baz", FetchMode.SELECT )
 			.setFetchMode( "component.glarch", FetchMode.SELECT )
 			.setFetchMode( "foo.baz", FetchMode.SELECT )
 			.setFetchMode( "foo.component.glarch", FetchMode.SELECT )
 			.list();
 		f = (Foo) list.get(0);
 		assertTrue( Hibernate.isInitialized( f.getFoo() ) );
 		assertTrue( !Hibernate.isInitialized( f.getComponent().getGlarch() ) );
 
 		s.save( new Bar() );
 		list = s.createCriteria(Bar.class)
 			.list();
 		assertTrue( list.size() == 1 );
 		assertTrue( s.createCriteria(Foo.class).list().size()==3 );
 		s.delete( list.get( 0 ) );
 
 		s.delete( f.getFoo() );
 		s.delete(f);
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testAfterDelete() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		s.save(foo);
 		s.flush();
 		s.delete(foo);
 		s.save(foo);
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCollectionWhere() throws Exception {
 		Foo foo1 = new Foo();
 		Foo foo2 = new Foo();
 		Baz baz = new Baz();
 		Foo[] arr = new Foo[10];
 		arr[0] = foo1;
 		arr[9] = foo2;
 
 		Session s = openSession();
 		s.beginTransaction();
 		s.save( foo1 );
 		s.save(foo2);
 		baz.setFooArray( arr );
 		s.save( baz );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		assertTrue( baz.getFooArray().length == 1 );
 		assertTrue( s.createQuery( "from Baz baz join baz.fooArray foo" ).list().size()==1 );
 		assertTrue( s.createQuery( "from Foo foo" ).list().size()==2 );
 		assertTrue( s.createFilter( baz.getFooArray(), "" ).list().size() == 1 );
 		//assertTrue( s.delete("from java.lang.Object o")==9 );
 		doDelete( s, "from Foo foo" );
 		final String bazid = baz.getCode();
 		s.delete( baz );
 		int rows = s.doReturningWork(
 				new AbstractReturningWork<Integer>() {
 					@Override
 					public Integer execute(Connection connection) throws SQLException {
 						return connection.createStatement()
 								.executeUpdate( "delete from FOO_ARRAY where id_='" + bazid + "' and i>=8" );
 					}
 				}
 		);
 		assertTrue( rows == 1 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testComponentParent() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		BarProxy bar = new Bar();
 		bar.setBarComponent( new FooComponent() );
 		Baz baz = new Baz();
 		baz.setComponents( new FooComponent[] { new FooComponent(), new FooComponent() } );
 		s.save(bar);
 		s.save(baz);
 		t.commit();
 		s.close();
 		s = openSession();
 		t = s.beginTransaction();
 		bar = (BarProxy) s.load(Bar.class, bar.getKey());
 		s.load(baz, baz.getCode());
 		assertTrue( bar.getBarComponent().getParent()==bar );
 		assertTrue( baz.getComponents()[0].getBaz()==baz && baz.getComponents()[1].getBaz()==baz );
 		s.delete(baz);
 		s.delete(bar);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCollectionCache() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.load( Baz.class, baz.getCode() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	public void ntestAssociationId() throws Exception {
 		// IMPL NOTE : previously not being run due to the name
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Bar bar = new Bar();
 		String id = (String) s.save(bar);
 		MoreStuff more = new MoreStuff();
 		more.setName("More Stuff");
 		more.setIntId(12);
 		more.setStringId("id");
 		Stuff stuf = new Stuff();
 		stuf.setMoreStuff(more);
 		more.setStuffs( new ArrayList() );
 		more.getStuffs().add(stuf);
 		stuf.setFoo(bar);
 		stuf.setId(1234);
 		stuf.setProperty( TimeZone.getDefault() );
 		s.save(more);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		List results = s.createQuery(
 				"from Stuff as s where s.foo.id = ? and s.id.id = ? and s.moreStuff.id.intId = ? and s.moreStuff.id.stringId = ?"
 		)
 				.setParameter( 0, bar, s.getTypeHelper().entity(Foo.class) )
 				.setParameter( 1, new Long(1234), StandardBasicTypes.LONG )
 				.setParameter( 2, new Integer(12), StandardBasicTypes.INTEGER )
 				.setParameter( 3, "id", StandardBasicTypes.STRING )
 				.list();
 		assertEquals( 1, results.size() );
 		results = s.createQuery( "from Stuff as s where s.foo.id = ? and s.id.id = ? and s.moreStuff.name = ?" )
 				.setParameter( 0, bar, s.getTypeHelper().entity(Foo.class) )
 				.setParameter( 1, new Long(1234), StandardBasicTypes.LONG )
 				.setParameter( 2, "More Stuff", StandardBasicTypes.STRING )
 				.list();
 		assertEquals( 1, results.size() );
 		s.createQuery( "from Stuff as s where s.foo.string is not null" ).list();
 		assertTrue(
 				s.createQuery( "from Stuff as s where s.foo > '0' order by s.foo" ).list().size()==1
 		);
 		//s.createCriteria(Stuff.class).createCriteria("id.foo").add( Expression.isNull("foo") ).list();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		FooProxy foo = (FooProxy) s.load(Foo.class, id);
 		s.load(more, more);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		Stuff stuff = new Stuff();
 		stuff.setFoo(foo);
 		stuff.setId(1234);
 		stuff.setMoreStuff(more);
 		s.load(stuff, stuff);
 		assertTrue( stuff.getProperty().equals( TimeZone.getDefault() ) );
 		assertTrue( stuff.getMoreStuff().getName().equals("More Stuff") );
 		doDelete( s, "from MoreStuff" );
 		doDelete( s, "from Foo foo" );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCascadeSave() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		List list = new ArrayList();
 		list.add( new Fee() );
 		list.add( new Fee() );
 		baz.setFees( list );
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		assertTrue( baz.getFees().size() == 2 );
 		s.delete(baz);
 		assertTrue( !s.createQuery( "from Fee fee" ).iterate().hasNext() );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testCollectionsInSelect() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Foo[] foos = new Foo[] { null, new Foo() };
 		s.save( foos[1] );
 		Baz baz = new Baz();
 		baz.setDefaults();
 		baz.setFooArray(foos);
 		s.save(baz);
 		Baz baz2 = new Baz();
 		baz2.setDefaults();
 		s.save(baz2);
 
 		Bar bar = new Bar();
 		bar.setBaz(baz);
 		s.save(bar);
 
 		List list = s.createQuery( "select new Result(foo.string, foo.long, foo.integer) from Foo foo" ).list();
 		assertTrue( list.size()==2 && ( list.get(0) instanceof Result ) && ( list.get(1) instanceof Result ) );
 		/*list = s.find("select new Result( baz.name, foo.long, count(elements(baz.fooArray)) ) from Baz baz join baz.fooArray foo group by baz.name, foo.long");
 		assertTrue( list.size()==1 && ( list.get(0) instanceof Result ) );
 		Result r = ((Result) list.get(0) );
 		assertEquals( r.getName(), baz.getName() );
 		assertEquals( r.getCount(), 1 );
 		assertEquals( r.getAmount(), foos[1].getLong().longValue() );*/
 		list = s.createQuery(
 				"select new Result( baz.name, max(foo.long), count(foo) ) from Baz baz join baz.fooArray foo group by baz.name"
 		).list();
 		assertTrue( list.size()==1 && ( list.get(0) instanceof Result ) );
 		Result r = ((Result) list.get(0) );
 		assertEquals( r.getName(), baz.getName() );
 		assertEquals( r.getCount(), 1 );
 		assertTrue( r.getAmount() > 696969696969696000l );
 
 
 		//s.find("select max( elements(bar.baz.fooArray) ) from Bar as bar");
 		//The following test is disabled for databases with no subselects...also for Interbase (not sure why).
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) /*&& !(dialect instanceof MckoiDialect)*/ && !(getDialect() instanceof SAPDBDialect) && !(getDialect() instanceof PointbaseDialect) )  {
 			s.createQuery( "select count(*) from Baz as baz where 1 in indices(baz.fooArray)" ).list();
 			s.createQuery( "select count(*) from Bar as bar where 'abc' in elements(bar.baz.fooArray)" ).list();
 			s.createQuery( "select count(*) from Bar as bar where 1 in indices(bar.baz.fooArray)" ).list();
 			if ( !(getDialect() instanceof DB2Dialect) &&  !(getDialect() instanceof Oracle8iDialect ) && !( getDialect() instanceof SybaseDialect ) && !( getDialect() instanceof Sybase11Dialect ) && !( getDialect() instanceof SybaseASE15Dialect ) && !( getDialect() instanceof PostgreSQLDialect )) {
 				// SybaseAnywhereDialect supports implicit conversions from strings to ints
 				s.createQuery(
 						"select count(*) from Bar as bar, bar.component.glarch.proxyArray as g where g.id in indices(bar.baz.fooArray)"
 				).list();
 				s.createQuery(
 						"select max( elements(bar.baz.fooArray) ) from Bar as bar, bar.component.glarch.proxyArray as g where g.id in indices(bar.baz.fooArray)"
 				).list();
 			}
 			s.createQuery(
 					"select count(*) from Bar as bar where '1' in (from bar.component.glarch.proxyArray g where g.name='foo')"
 			).list();
 			s.createQuery(
 					"select count(*) from Bar as bar where '1' in (from bar.component.glarch.proxyArray g where g.name='foo')"
 			).list();
 			s.createQuery(
 					"select count(*) from Bar as bar left outer join bar.component.glarch.proxyArray as pg where '1' in (from bar.component.glarch.proxyArray)"
 			).list();
 		}
 
 		list = s.createQuery(
 				"from Baz baz left join baz.fooToGlarch join fetch baz.fooArray foo left join fetch foo.foo"
 		).list();
 		assertTrue( list.size()==1 && ( (Object[]) list.get(0) ).length==2 );
 
 		s.createQuery(
 				"select baz.name from Bar bar inner join bar.baz baz inner join baz.fooSet foo where baz.name = bar.string"
 		).list();
 		s.createQuery(
 				"SELECT baz.name FROM Bar AS bar INNER JOIN bar.baz AS baz INNER JOIN baz.fooSet AS foo WHERE baz.name = bar.string"
 		).list();
 
 		if ( !( getDialect() instanceof HSQLDialect ) ) s.createQuery(
 				"select baz.name from Bar bar join bar.baz baz left outer join baz.fooSet foo where baz.name = bar.string"
 		).list();
 
 		s.createQuery( "select baz.name from Bar bar join bar.baz baz join baz.fooSet foo where baz.name = bar.string" )
 				.list();
 		s.createQuery(
 				"SELECT baz.name FROM Bar AS bar JOIN bar.baz AS baz JOIN baz.fooSet AS foo WHERE baz.name = bar.string"
 		).list();
 
 		if ( !( getDialect() instanceof HSQLDialect ) ) {
 			s.createQuery(
 					"select baz.name from Bar bar left join bar.baz baz left join baz.fooSet foo where baz.name = bar.string"
 			).list();
 			s.createQuery( "select foo.string from Bar bar left join bar.baz.fooSet foo where bar.string = foo.string" )
 					.list();
 		}
 
 		s.createQuery(
 				"select baz.name from Bar bar left join bar.baz baz left join baz.fooArray foo where baz.name = bar.string"
 		).list();
 		s.createQuery( "select foo.string from Bar bar left join bar.baz.fooArray foo where bar.string = foo.string" )
 				.list();
 
 		s.createQuery(
 				"select bar.string, foo.string from Bar bar inner join bar.baz as baz inner join baz.fooSet as foo where baz.name = 'name'"
 		).list();
 		s.createQuery( "select foo from Bar bar inner join bar.baz as baz inner join baz.fooSet as foo" ).list();
 		s.createQuery( "select foo from Bar bar inner join bar.baz.fooSet as foo" ).list();
 
 		s.createQuery(
 				"select bar.string, foo.string from Bar bar join bar.baz as baz join baz.fooSet as foo where baz.name = 'name'"
 		).list();
 		s.createQuery( "select foo from Bar bar join bar.baz as baz join baz.fooSet as foo" ).list();
 		s.createQuery( "select foo from Bar bar join bar.baz.fooSet as foo" ).list();
 
 		assertTrue( s.createQuery( "from Bar bar join bar.baz.fooArray foo" ).list().size()==1 );
 
 		assertTrue( s.createQuery( "from Bar bar join bar.baz.fooSet foo" ).list().size()==0 );
 		assertTrue( s.createQuery( "from Bar bar join bar.baz.fooArray foo" ).list().size()==1 );
 
 		s.delete(bar);
 
 		if ( getDialect() instanceof DB2Dialect || getDialect() instanceof PostgreSQLDialect ) {
 			s.createQuery( "select one from One one join one.manies many group by one order by count(many)" ).iterate();
 			s.createQuery( "select one from One one join one.manies many group by one having count(many) < 5" )
 					.iterate();
 		}
 
 		s.createQuery( "from One one join one.manies many where one.id = 1 and many.id = 1" ).list();
 		s.createQuery( "select one.id, elements(one.manies) from One one" ).iterate();
 		s.createQuery( "select max( elements(one.manies) ) from One one" ).iterate();
 		s.createQuery( "select one, elements(one.manies) from One one" ).list();
 		Iterator iter = s.createQuery( "select elements(baz.fooArray) from Baz baz where baz.id=?" )
 				.setParameter( 0, baz.getCode(), StandardBasicTypes.STRING )
 				.iterate();
 		assertTrue( iter.next()==foos[1] && !iter.hasNext() );
 		list = s.createQuery( "select elements(baz.fooArray) from Baz baz where baz.id=?" )
 				.setParameter( 0, baz.getCode(), StandardBasicTypes.STRING )
 				.list();
 		assertEquals( 1, list.size() );
 		iter = s.createQuery( "select indices(baz.fooArray) from Baz baz where baz.id=?" )
 				.setParameter( 0, baz.getCode(), StandardBasicTypes.STRING )
 				.iterate();
 		assertTrue( iter.next().equals( new Integer(1) ) && !iter.hasNext() );
 
 		iter = s.createQuery( "select size(baz.stringSet) from Baz baz where baz.id=?" )
 				.setParameter( 0, baz.getCode(), StandardBasicTypes.STRING )
 				.iterate();
 		assertEquals( new Integer(3), iter.next() );
 
 		s.createQuery( "from Foo foo where foo.component.glarch.id is not null" ).list();
 
 		iter = s.createQuery(
 				"select baz, size(baz.stringSet), count( distinct elements(baz.stringSet) ), max( elements(baz.stringSet) ) from Baz baz group by baz"
 		).iterate();
 		while ( iter.hasNext() ) {
 			Object[] arr = (Object[]) iter.next();
             log.info(arr[0] + " " + arr[1] + " " + arr[2] + " " + arr[3]);
 		}
 
 		s.delete(baz);
 		s.delete(baz2);
 		s.delete( foos[1] );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testNewFlushing() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		s.flush();
 		baz.getStringArray()[0] = "a new value";
 		Iterator iter = s.createQuery( "from Baz baz" ).iterate();//no flush
 		assertTrue( iter.next()==baz );
 		iter = s.createQuery( "select elements(baz.stringArray) from Baz baz" ).iterate();
 		boolean found = false;
 		while ( iter.hasNext() ) {
 			if ( iter.next().equals("a new value") ) found = true;
 		}
 		assertTrue( found );
 		baz.setStringArray( null );
 		s.createQuery( "from Baz baz" ).iterate(); //no flush
 		iter = s.createQuery( "select elements(baz.stringArray) from Baz baz" ).iterate();
 		assertTrue( !iter.hasNext() );
 		baz.getStringList().add( "1E1" );
 		iter = s.createQuery( "from Foo foo" ).iterate();//no flush
 		assertTrue( !iter.hasNext() );
 		iter = s.createQuery( "select elements(baz.stringList) from Baz baz" ).iterate();
 		found = false;
 		while ( iter.hasNext() ) {
 			if ( iter.next().equals("1E1") ) found = true;
 		}
 		assertTrue( found );
 		baz.getStringList().remove( "1E1" );
 		iter = s.createQuery( "select elements(baz.stringArray) from Baz baz" ).iterate(); //no flush
 		iter = s.createQuery( "select elements(baz.stringList) from Baz baz" ).iterate();
 		found = false;
 		while ( iter.hasNext() ) {
 			if ( iter.next().equals("1E1") ) found = true;
 		}
 		assertTrue(!found);
 
 		List newList = new ArrayList();
 		newList.add("value");
 		baz.setStringList( newList );
 		iter = s.createQuery( "from Foo foo" ).iterate();//no flush
 		baz.setStringList( null );
 		iter = s.createQuery( "select elements(baz.stringList) from Baz baz" ).iterate();
 		assertTrue( !iter.hasNext() );
 
 		baz.setStringList(newList);
 		iter = s.createQuery( "from Foo foo" ).iterate();//no flush
 		iter = s.createQuery( "select elements(baz.stringList) from Baz baz" ).iterate();
 		assertTrue( iter.hasNext() );
 
 		s.delete( baz );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"UnnecessaryBoxing", "unchecked"})
 	public void testPersistCollections() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		assertEquals( 0, ( (Long) s.createQuery( "select count(*) from Bar" ).iterate().next() ).longValue() );
 		assertTrue( s.createQuery( "select count(*) from Bar b" ).iterate().next().equals( new Long(0) ) );
 		assertFalse( s.createQuery( "from Glarch g" ).iterate().hasNext() );
 
 		Baz baz = new Baz();
 		s.save(baz);
 		baz.setDefaults();
 		baz.setStringArray( new String[] { "stuff" } );
 		Set bars = new HashSet();
 		bars.add( new Bar() );
 		baz.setCascadingBars(bars);
 		HashMap sgm = new HashMap();
 		sgm.put( "a", new Glarch() );
 		sgm.put( "b", new Glarch() );
 		baz.setStringGlarchMap(sgm);
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		assertEquals( 1L, ((Long) s.createQuery( "select count(*) from Bar" ).iterate().next()).longValue() );
 		baz = (Baz) ( (Object[]) s.createQuery( "select baz, baz from Baz baz" ).list().get(0) )[1];
 		assertTrue( baz.getCascadingBars().size()==1 );
 		//System.out.println( s.print(baz) );
 		Foo foo = new Foo();
 		s.save(foo);
 		Foo foo2 = new Foo() ;
 		s.save(foo2);
 		baz.setFooArray( new Foo[] { foo, foo, null, foo2 } );
 		baz.getFooSet().add(foo);
 		baz.getCustoms().add( new String[] { "new", "custom" } );
 		baz.setStringArray(null);
 		baz.getStringList().set(0, "new value");
 		baz.setStringSet( new TreeSet() );
 		Time time = new java.sql.Time(12345);
 		baz.getTimeArray()[2] = time;
 		//System.out.println(time);
 
 		assertTrue( baz.getStringGlarchMap().size()==1 );
 
 		//The following test is disabled databases with no subselects
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) && !(getDialect() instanceof PointbaseDialect) )  {
 			List list = s.createQuery(
 					"select foo from Foo foo, Baz baz where foo in elements(baz.fooArray) and 3 = some elements(baz.intArray) and 4 > all indices(baz.intArray)"
 			).list();
 			assertTrue( "collection.elements find", list.size()==2 );
 		}
 		if (!(getDialect() instanceof SAPDBDialect) ) { // SAPDB doesn't like distinct with binary type
 			List list = s.createQuery( "select distinct foo from Baz baz join baz.fooArray foo" ).list();
 			assertTrue( "collection.elements find", list.size()==2 );
 		}
 
 		List list = s.createQuery( "select foo from Baz baz join baz.fooSet foo" ).list();
 		assertTrue( "association.elements find", list.size()==1 );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		assertEquals( 1, ((Long) s.createQuery( "select count(*) from Bar" ).iterate().next()).longValue() );
 		baz = (Baz) s.createQuery( "select baz from Baz baz order by baz" ).list().get(0);
 		assertTrue( "collection of custom types - added element", baz.getCustoms().size()==4 && baz.getCustoms().get(0)!=null );
 		assertTrue ( "component of component in collection", baz.getComponents()[1].getSubcomponent()!=null );
 		assertTrue( baz.getComponents()[1].getBaz()==baz );
 		assertTrue( "set of objects", ( (FooProxy) baz.getFooSet().iterator().next() ).getKey().equals( foo.getKey() ));
 		assertTrue( "collection removed", baz.getStringArray().length==0 );
 		assertTrue( "changed element", baz.getStringList().get(0).equals("new value"));
 		assertTrue( "replaced set", baz.getStringSet().size()==0 );
 		assertTrue( "array element change", baz.getTimeArray()[2]!=null );
 		assertTrue( baz.getCascadingBars().size()==1 );
 		//System.out.println( s.print(baz) );
 		baz.getStringSet().add("two");
 		baz.getStringSet().add("one");
 		baz.getBag().add("three");
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		baz = (Baz) s.createQuery( "select baz from Baz baz order by baz" ).list().get(0);
 		assertTrue( baz.getStringSet().size()==2 );
 		assertTrue( baz.getStringSet().first().equals("one") );
 		assertTrue( baz.getStringSet().last().equals("two") );
 		assertTrue( baz.getBag().size()==5 );
 		baz.getStringSet().remove("two");
 		baz.getBag().remove("duplicate");
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		assertEquals( 1, ((Long) s.createQuery( "select count(*) from Bar" ).iterate().next()).longValue() );
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		assertTrue( baz.getCascadingBars().size()==1 );
 		Bar bar = new Bar();
 		Bar bar2 = new Bar();
 		s.save(bar); s.save(bar2);
 		baz.setTopFoos( new HashSet() );
 		baz.getTopFoos().add(bar);
 		baz.getTopFoos().add(bar2);
 		assertTrue( baz.getCascadingBars().size()==1 );
 		baz.setTopGlarchez( new TreeMap() );
 		GlarchProxy g = new Glarch();
 		s.save(g);
 		baz.getTopGlarchez().put( new Character('G'), g );
 		HashMap map = new HashMap();
 		map.put(bar, g);
 		map.put(bar2, g);
 		baz.setFooToGlarch(map);
 		map = new HashMap();
 		map.put( new FooComponent("name", 123, null, null), bar );
 		map.put( new FooComponent("nameName", 12, null, null), bar );
 		baz.setFooComponentToFoo(map);
 		map = new HashMap();
 		map.put(bar, g);
 		baz.setGlarchToFoo(map);
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		baz = (Baz) s.createQuery( "select baz from Baz baz order by baz" ).list().get(0);
 		assertTrue( baz.getCascadingBars().size()==1 );
 
 		Session s2 = openSession();
 		Transaction txn2 = s2.beginTransaction();
 		assertEquals( 3, ((Long) s2.createQuery( "select count(*) from Bar" ).iterate().next()).longValue() );
 		Baz baz2 = (Baz) s2.createQuery( "select baz from Baz baz order by baz" ).list().get(0);
 		Object o = baz2.getFooComponentToFoo().get( new FooComponent("name", 123, null, null) );
 		assertTrue(
 			o==baz2.getFooComponentToFoo().get( new FooComponent("nameName", 12, null, null) ) && o!=null
 		);
 		txn2.commit();
 		s2.close();
 
 		assertTrue( Hibernate.isInitialized( baz.getFooToGlarch() ) );
 		assertTrue( baz.getTopFoos().size()==2 );
 		assertTrue( baz.getTopGlarchez().size()==1 );
 		assertTrue( baz.getTopFoos().iterator().next()!=null );
 		assertTrue( baz.getStringSet().size()==1 );
 		assertTrue( baz.getBag().size()==4 );
 		assertTrue( baz.getFooToGlarch().size()==2 );
 		assertTrue( baz.getFooComponentToFoo().size()==2 );
 		assertTrue( baz.getGlarchToFoo().size()==1 );
 		Iterator iter = baz.getFooToGlarch().keySet().iterator();
 		for (int i=0; i<2; i++ ) assertTrue( iter.next() instanceof BarProxy );
 		FooComponent fooComp = (FooComponent) baz.getFooComponentToFoo().keySet().iterator().next();
 		assertTrue(
 			( (fooComp.getCount()==123 && fooComp.getName().equals("name"))
 			|| (fooComp.getCount()==12 && fooComp.getName().equals("nameName")) )
 			&& ( baz.getFooComponentToFoo().get(fooComp) instanceof BarProxy )
 		);
 		Glarch g2 = new Glarch();
 		s.save(g2);
 		g = (GlarchProxy) baz.getTopGlarchez().get( new Character('G') );
 		baz.getTopGlarchez().put( new Character('H'), g );
 		baz.getTopGlarchez().put( new Character('G'), g2 );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		assertTrue( baz.getTopGlarchez().size()==2 );
 		assertTrue( baz.getCascadingBars().size()==1 );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		assertEquals( 3, ((Long) s.createQuery( "select count(*) from Bar" ).iterate().next()).longValue() );
 		baz = (Baz) s.createQuery( "select baz from Baz baz order by baz" ).list().get(0);
 		assertTrue( baz.getTopGlarchez().size()==2 );
 		assertTrue( baz.getCascadingBars().size()==1 );
 		txn.commit();
 
 		s2 = (Session) SerializationHelper.deserialize( SerializationHelper.serialize(s) );
 		s.close();
 
 		txn2 = s2.beginTransaction();
 		baz = (Baz) s2.load(Baz.class, baz.getCode());
 		assertEquals( 3, ((Long) s2.createQuery( "select count(*) from Bar" ).iterate().next()).longValue() );
 		s2.delete(baz);
 		s2.delete( baz.getTopGlarchez().get( Character.valueOf('G') ) );
 		s2.delete( baz.getTopGlarchez().get( Character.valueOf('H') ) );
 		int rows = s2.doReturningWork(
 				new AbstractReturningWork<Integer>() {
 					@Override
 					public Integer execute(Connection connection) throws SQLException {
 						final String sql = "update " + getDialect().openQuote() + "glarchez" + getDialect().closeQuote() + " set baz_map_id=null where baz_map_index='a'";
 						return connection.createStatement().executeUpdate( sql );
 					}
 				}
 		);
 		assertTrue(rows==1);
 		assertEquals( 2, doDelete( s2, "from Bar bar" ) );
 		FooProxy[] arr = baz.getFooArray();
 		assertTrue( "new array of objects", arr.length==4 && arr[1].getKey().equals( foo.getKey() ) );
 		for ( int i=1; i<arr.length; i++ ) {
 			if ( arr[i]!=null) s2.delete(arr[i]);
 		}
 
 		s2.load( Qux.class, new Long(666) ); //nonexistent
 
 		assertEquals( 1, doDelete( s2, "from Glarch g" ) );
 		txn2.commit();
 
 		s2.disconnect();
 
 		Session s3 = (Session) SerializationHelper.deserialize( SerializationHelper.serialize( s2 ) );
 		s2.close();
 		//s3.reconnect();
 		assertTrue( s3.load( Qux.class, new Long(666) )!=null ); //nonexistent
 		//s3.disconnect();
 		s3.close();
 	}
 
 	@Test
 	public void testSaveFlush() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Fee fee = new Fee();
 		s.save( fee );
 		fee.setFi( "blah" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		fee = (Fee) s.load( Fee.class, fee.getKey() );
 		assertTrue( "blah".equals( fee.getFi() ) );
 		s.delete(fee);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCreateUpdate() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		s.save(foo);
 		foo.setString("dirty");
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Foo foo2 = new Foo();
 		s.load( foo2, foo.getKey() );
 		// There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
 		assertTrue( "create-update", foo.equalsFoo(foo2) );
 		//System.out.println( s.print(foo2) );
 		s.delete(foo2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = new Foo();
 		s.save(foo);
 		foo.setString("dirty");
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.load(foo2, foo.getKey());
 		// There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
 		assertTrue( "create-update", foo.equalsFoo(foo2) );
 		//System.out.println( s.print(foo2) );
 		s.delete(foo2);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testUpdateCollections() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Holder baz = new Holder();
 		baz.setName("123");
 		Foo f1 = new Foo();
 		Foo f2 = new Foo();
 		Foo f3 = new Foo();
 		One o = new One();
 		baz.setOnes( new ArrayList() );
 		baz.getOnes().add(o);
 		Foo[] foos = new Foo[] { f1, null, f2 };
 		baz.setFooArray(foos);
 		baz.setFoos( new HashSet() );
 		baz.getFoos().add(f1);
 		s.save(f1);
 		s.save(f2);
 		s.save(f3);
 		s.save(o);
 		s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		baz.getOnes().set(0, null);
 		baz.getOnes().add(o);
 		baz.getFoos().add(f2);
 		foos[0] = f3;
 		foos[1] = f1;
 
 		s = openSession();
 		s.beginTransaction();
 		s.saveOrUpdate(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Holder h = (Holder) s.load(Holder.class, baz.getId());
 		assertTrue( h.getOnes().get(0)==null );
 		assertTrue( h.getOnes().get(1)!=null );
 		assertTrue( h.getFooArray()[0]!=null);
 		assertTrue( h.getFooArray()[1]!=null);
 		assertTrue( h.getFooArray()[2]!=null);
 		assertTrue( h.getFoos().size()==2 );
 		s.getTransaction().commit();
 		s.close();
 
 		baz.getFoos().remove(f1);
 		baz.getFoos().remove(f2);
 		baz.getFooArray()[0]=null;
 		baz.getFooArray()[0]=null;
 		baz.getFooArray()[0]=null;
 
 		s = openSession();
 		s.beginTransaction();
 		s.saveOrUpdate(baz);
 		doDelete( s, "from Foo" );
 		baz.getOnes().remove(o);
 		doDelete( s, "from One" );
 		s.delete(baz);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCreate() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		s.save(foo);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Foo foo2 = new Foo();
 		s.load( foo2, foo.getKey() );
 		// There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
 		assertTrue( "create", foo.equalsFoo( foo2 ) );
 		s.delete(foo2);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testCallback() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Qux q = new Qux("0");
 		s.save(q);
 		q.setChild( new Qux( "1" ) );
 		s.save( q.getChild() );
 		Qux q2 = new Qux("2");
 		q2.setChild( q.getChild() );
 		Qux q3 = new Qux("3");
 		q.getChild().setChild(q3);
 		s.save( q3 );
 		Qux q4 = new Qux("4");
 		q4.setChild( q3 );
 		s.save(q4);
 		s.save( q2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List l = s.createQuery( "from Qux" ).list();
 		assertTrue( "", l.size() == 5 );
 		s.delete( l.get( 0 ) );
 		s.delete( l.get( 1 ) );
 		s.delete( l.get( 2 ) );
 		s.delete( l.get(3) );
 		s.delete( l.get(4) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testPolymorphism() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Bar bar = new Bar();
 		s.save(bar);
 		bar.setBarString("bar bar");
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		FooProxy foo = (FooProxy) s.load( Foo.class, bar.getKey() );
 		assertTrue( "polymorphic", foo instanceof BarProxy );
 		assertTrue( "subclass property", ( (BarProxy) foo ).getBarString().equals( bar.getBarString() ) );
 		//System.out.println( s.print(foo) );
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testRemoveContains() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save( baz );
 		s.flush();
 		assertTrue( s.contains(baz) );
 		s.evict( baz );
 		assertFalse( s.contains(baz) );
 		Baz baz2 = (Baz) s.load( Baz.class, baz.getCode() );
 		assertFalse( baz == baz2 );
 		s.delete(baz2);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@SuppressWarnings( {"unchecked"})
 	public void testCollectionOfSelf() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Bar bar = new Bar();
 		s.save(bar);
 		bar.setAbstracts( new HashSet() );
 		bar.getAbstracts().add( bar );
 		Bar bar2 = new Bar();
 		bar.getAbstracts().add( bar2 );
 		bar.setFoo(bar);
 		s.save( bar2 );
 		s.getTransaction().commit();
 		s.close();
 
 		bar.setAbstracts( null );
 
 		s = openSession();
 		s.beginTransaction();
 		s.load( bar, bar.getKey() );
 		assertTrue( "collection contains self", bar.getAbstracts().size() == 2 && bar.getAbstracts().contains( bar ) );
 		assertTrue( "association to self", bar.getFoo()==bar );
 		for ( Object o : bar.getAbstracts() ) {
 			s.delete( o );
 		}
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testFind() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 
 		Bar bar = new Bar();
 		s.save(bar);
 		bar.setBarString("bar bar");
 		bar.setString("xxx");
 		Foo foo = new Foo();
 		s.save(foo);
 		foo.setString("foo bar");
 		s.save( new Foo() );
 		s.save( new Bar() );
 		List list1 = s.createQuery( "select foo from Foo foo where foo.string='foo bar'" ).list();
 		assertTrue( "find size", list1.size()==1 );
 		assertTrue( "find ==", list1.get(0)==foo );
 		List list2 = s.createQuery( "from Foo foo order by foo.string, foo.date" ).list();
 		assertTrue( "find size", list2.size()==4 );
 
 		list1 = s.createQuery( "from Foo foo where foo.class='B'" ).list();
 		assertTrue( "class special property", list1.size()==2);
 		list1 = s.createQuery( "from Foo foo where foo.class=Bar" ).list();
 		assertTrue( "class special property", list1.size()==2);
 		list1 = s.createQuery( "from Foo foo where foo.class=Bar" ).list();
 		list2 = s.createQuery( "select bar from Bar bar, Foo foo where bar.string = foo.string and not bar=foo" ).list();
 		assertTrue( "class special property", list1.size()==2);
 		assertTrue( "select from a subclass", list2.size()==1);
 		Trivial t = new Trivial();
 		s.save(t);
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		list1 = s.createQuery( "from Foo foo where foo.string='foo bar'" ).list();
 		assertTrue( "find size", list1.size()==1 );
 		// There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
 		assertTrue( "find equals", ( (Foo) list1.get(0) ).equalsFoo(foo) );
 		list2 = s.createQuery( "select foo from Foo foo" ).list();
 		assertTrue( "find size", list2.size()==5 );
 		List list3 = s.createQuery( "from Bar bar where bar.barString='bar bar'" ).list();
 		assertTrue( "find size", list3.size()==1 );
 		assertTrue( "find same instance", list2.contains( list1.get(0) ) && list2.contains( list2.get(0) ) );
 		assertTrue( s.createQuery( "from Trivial" ).list().size()==1 );
 		doDelete( s, "from Trivial" );
 
 		list2 = s.createQuery( "from Foo foo where foo.date = ?" )
 				.setParameter( 0, new java.sql.Date(123), StandardBasicTypes.DATE )
 				.list();
 		assertTrue ( "find by date", list2.size()==4 );
 		Iterator iter = list2.iterator();
 		while ( iter.hasNext() ) {
 			s.delete( iter.next() );
 		}
 		list2 = s.createQuery( "from Foo foo" ).list();
 		assertTrue( "find deleted", list2.size()==0);
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testDeleteRecursive() throws Exception {
 		Session s = openSession();
