File path: code/core/src/main/java/org/hibernate/engine/loading/LoadContexts.java
Comment: todo : should really move this log statement to CollectionType
Initial commit id: d8d6d82e
Final commit id: a9b1425f
   Bugs between [       0]:

   Bugs after [       4]:
fb44ad936d HHH-6196 - Split org.hibernate.engine package into api/spi/internal
36ba1bcafb HHH-6192 - Split org.hibernate.collection package up into api/sip/internal
6504cb6d78 HHH-6098 - Slight naming changes in regards to new logging classes
0816d00e59 HHH-5986 - Refactor org.hibernate.util package for spi/internal split

Start block index: 171
End block index: 186
	public PersistentCollection locateLoadingCollection(CollectionPersister persister, Serializable ownerKey) {
		LoadingCollectionEntry lce = locateLoadingCollectionEntry( new CollectionKey( persister, ownerKey, getEntityMode() ) );
		if ( lce != null ) {
			if ( log.isTraceEnabled() ) {
				log.trace( "returning loading collection:" + MessageHelper.collectionInfoString( persister, ownerKey, getSession().getFactory() ) );
			}
			return lce.getCollection();
		}
		else {
			// todo : should really move this log statement to CollectionType, where this is used from...
			if ( log.isTraceEnabled() ) {
				log.trace( "creating collection wrapper:" + MessageHelper.collectionInfoString( persister, ownerKey, getSession().getFactory() ) );
			}
			return null;
		}
	}
