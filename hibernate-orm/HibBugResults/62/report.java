File path: code/core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java
Comment: ODO: put this stuff back in to read snapshot from
Initial commit id: d8d6d82e
Final commit id: a9b1425f
   Bugs between [       0]:

   Bugs after [       4]:
4ee0d4237d HHH-6198 - Split org.hibernate.event package into api/spi/internal
fb44ad936d HHH-6196 - Split org.hibernate.engine package into api/spi/internal
6504cb6d78 HHH-6098 - Slight naming changes in regards to new logging classes
fe8c7183d1 HHH-5697 - Support for multi-tenancy

Start block index: 259
End block index: 331
	protected void performUpdate(
			SaveOrUpdateEvent event,
			Object entity,
			EntityPersister persister) throws HibernateException {

		if ( !persister.isMutable() ) {
			log.trace( "immutable instance passed to doUpdate(), locking" );
			reassociate( event, entity, event.getRequestedId(), persister );
		}
		else {

			if ( log.isTraceEnabled() ) {
				log.trace(
						"updating " +
								MessageHelper.infoString(
										persister, event.getRequestedId(), event.getSession().getFactory()
								)
				);
			}

			final EventSource source = event.getSession();

			EntityKey key = new EntityKey( event.getRequestedId(), persister, source.getEntityMode() );

			source.getPersistenceContext().checkUniqueness( key, entity );

			if ( invokeUpdateLifecycle( entity, persister, source ) ) {
				reassociate( event, event.getObject(), event.getRequestedId(), persister );
				return;
			}

			// this is a transient object with existing persistent state not loaded by the session

			new OnUpdateVisitor( source, event.getRequestedId(), entity ).process( entity, persister );

			//TODO: put this stuff back in to read snapshot from
			//      the second-level cache (needs some extra work)
			/*Object[] cachedState = null;

			if ( persister.hasCache() ) {
				CacheEntry entry = (CacheEntry) persister.getCache()
						.get( event.getRequestedId(), source.getTimestamp() );
			    cachedState = entry==null ?
			    		null :
			    		entry.getState(); //TODO: half-assemble this stuff
			}*/

			source.getPersistenceContext().addEntity(
					entity,
					Status.MANAGED,
					null, //cachedState,
					key,
					persister.getVersion( entity, source.getEntityMode() ),
					LockMode.NONE,
					true,
					persister,
					false,
					true //assume true, since we don't really know, and it doesn't matter
			);

			persister.afterReassociate( entity, source );

			if ( log.isTraceEnabled() ) {
				log.trace(
						"updating " +
								MessageHelper.infoString( persister, event.getRequestedId(), source.getFactory() )
				);
			}

			cascadeOnUpdate( event, persister, entity );

		}
	}
