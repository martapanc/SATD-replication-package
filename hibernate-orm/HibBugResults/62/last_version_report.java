	protected void performUpdate(
			SaveOrUpdateEvent event,
			Object entity,
			EntityPersister persister) throws HibernateException {

        if (!persister.isMutable()) LOG.immutableInstancePassedToPerformUpdate();

        if (LOG.isTraceEnabled()) LOG.updating(MessageHelper.infoString(persister,
                                                                        event.getRequestedId(),
                                                                        event.getSession().getFactory()));

        final EventSource source = event.getSession();

		EntityKey key = new EntityKey(event.getRequestedId(), persister, source.getEntityMode());

		source.getPersistenceContext().checkUniqueness(key, entity);

		if (invokeUpdateLifecycle(entity, persister, source)) {
            reassociate(event, event.getObject(), event.getRequestedId(), persister);
            return;
        }

		// this is a transient object with existing persistent state not loaded by the session

		new OnUpdateVisitor(source, event.getRequestedId(), entity).process(entity, persister);

		// TODO: put this stuff back in to read snapshot from
        // the second-level cache (needs some extra work)
        /*Object[] cachedState = null;

        if ( persister.hasCache() ) {
        	CacheEntry entry = (CacheEntry) persister.getCache()
        			.get( event.getRequestedId(), source.getTimestamp() );
            cachedState = entry==null ?
            		null :
            		entry.getState(); //TODO: half-assemble this stuff
        }*/

		source.getPersistenceContext().addEntity(entity, (persister.isMutable() ? Status.MANAGED : Status.READ_ONLY), null, // cachedState,
                                                 key,
                                                 persister.getVersion(entity, source.getEntityMode()),
                                                 LockMode.NONE,
                                                 true,
                                                 persister,
                                                 false,
                                                 true // assume true, since we don't really know, and it doesn't matter
        );

		persister.afterReassociate(entity, source);

        if (LOG.isTraceEnabled()) LOG.updating(MessageHelper.infoString(persister, event.getRequestedId(), source.getFactory()));

        cascadeOnUpdate(event, persister, entity);
	}
