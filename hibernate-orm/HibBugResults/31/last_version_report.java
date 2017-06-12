	public PersistentCollection locateLoadingCollection(CollectionPersister persister, Serializable ownerKey) {
		LoadingCollectionEntry lce = locateLoadingCollectionEntry( new CollectionKey( persister, ownerKey, getEntityMode() ) );
		if ( lce != null ) {
            if (LOG.isTraceEnabled()) LOG.returningLoadingCollection(MessageHelper.collectionInfoString(persister,
                                                                                                        ownerKey,
                                                                                                        getSession().getFactory()));
			return lce.getCollection();
		}
        // TODO : should really move this log statement to CollectionType, where this is used from...
        if (LOG.isTraceEnabled()) LOG.creatingCollectionWrapper(MessageHelper.collectionInfoString(persister,
                                                                                                   ownerKey,
                                                                                                   getSession().getFactory()));
        return null;
	}
