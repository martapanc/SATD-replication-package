File path: hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
Comment: ODO: reuse the PostLoadEvent...
Initial commit id: eecee618
Final commit id: f74c5a7f
   Bugs between [       1]:
f74c5a7fa5 HHH-2879 Apply hibernate code templates and formatting
   Bugs after [      13]:
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
7308e14fed HHH-9803 - Checkstyle fix ups
23936fd510 HHH-9344 Convert DefaultResolveNaturalIdEventListener to use nanoTime instead of currentTimeMillis
b6c9a56136 HHH-8741 - More checkstyle cleanups
3800a0e695 HHH-7206 - Manage natural-id synchronization without flushing
d3b640cb75 HHH-7197 reimport imports
9f4fd48603 HHH-7085 - Entities marked as @Immutable that have a @NaturalId fail to be inserted with NPE
368c4cc2bc HHH-6974 Redirect naturalId criteria queries to new API
1569e6194b HHH-6974 Complete second level caching of natural id resolution
ef22e31068 HHH-6974 Adding hooks into NaturalIdRegionAccessStrategy
72fe79a3f2 HHH-6974 Addition of NaturalIdRegion SPI
33d399f186 HHH-6974 - Add caching to new "load access" api for natural id loading
fb3566b467 HHH-2879 - initial clean implementation with no caching

Start block index: 529
End block index: 614
//	private Object assembleCacheEntry(
//			final CacheEntry entry,
//			final Serializable id,
//			final EntityPersister persister,
//			final LoadEvent event) throws HibernateException {
//
//		final Object optionalObject = event.getInstanceToLoad();
//		final EventSource session = event.getSession();
//		final SessionFactoryImplementor factory = session.getFactory();
//
//        if (LOG.isTraceEnabled()) LOG.trace("Assembling entity from second-level cache: "
//                                            + MessageHelper.infoString(persister, id, factory));
//
//		EntityPersister subclassPersister = factory.getEntityPersister( entry.getSubclass() );
//		Object result = optionalObject == null ?
//				session.instantiate( subclassPersister, id ) : optionalObject;
//
//		// make it circular-reference safe
//		final EntityKey entityKey = session.generateEntityKey( id, subclassPersister );
//		TwoPhaseLoad.addUninitializedCachedEntity(
//				entityKey,
//				result,
//				subclassPersister,
//				LockMode.NONE,
//				entry.areLazyPropertiesUnfetched(),
//				entry.getVersion(),
//				session
//			);
//
//		Type[] types = subclassPersister.getPropertyTypes();
//		Object[] values = entry.assemble( result, id, subclassPersister, session.getInterceptor(), session ); // intializes result by side-effect
//		TypeHelper.deepCopy(
//				values,
//				types,
//				subclassPersister.getPropertyUpdateability(),
//				values,
//				session
//		);
//
//		Object version = Versioning.getVersion( values, subclassPersister );
//        if (LOG.isTraceEnabled()) LOG.trace("Cached Version: " + version);
//
//		final PersistenceContext persistenceContext = session.getPersistenceContext();
//		boolean isReadOnly = session.isDefaultReadOnly();
//		if ( persister.isMutable() ) {
//			Object proxy = persistenceContext.getProxy( entityKey );
//			if ( proxy != null ) {
//				// there is already a proxy for this impl
//				// only set the status to read-only if the proxy is read-only
//				isReadOnly = ( ( HibernateProxy ) proxy ).getHibernateLazyInitializer().isReadOnly();
//			}
//		}
//		else {
//			isReadOnly = true;
//		}
//		persistenceContext.addEntry(
//				result,
//				( isReadOnly ? Status.READ_ONLY : Status.MANAGED ),
//				values,
//				null,
//				id,
//				version,
//				LockMode.NONE,
//				true,
//				subclassPersister,
//				false,
//				entry.areLazyPropertiesUnfetched()
//			);
//		subclassPersister.afterInitialize( result, entry.areLazyPropertiesUnfetched(), session );
//		persistenceContext.initializeNonLazyCollections();
//		// upgrade the lock if necessary:
//		//lock(result, lockMode);
//
//		//PostLoad is needed for EJB3
//		//TODO: reuse the PostLoadEvent...
//		PostLoadEvent postLoadEvent = new PostLoadEvent( session )
//				.setEntity( result )
//				.setId( id )
//				.setPersister( persister );
//
//		for ( PostLoadEventListener listener : postLoadEventListeners( session ) ) {
//			listener.onPostLoad( postLoadEvent );
//		}
//
//		return result;
//	}
