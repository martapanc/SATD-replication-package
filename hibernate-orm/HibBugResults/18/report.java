File path: code/core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
Comment: perhaps this should be an exception since it is only ever used
Initial commit id: d8d6d82e
Final commit id: fe8c7183
   Bugs between [       0]:

   Bugs after [       3]:
4ee0d4237d HHH-6198 - Split org.hibernate.event package into api/spi/internal
fb44ad936d HHH-6196 - Split org.hibernate.engine package into api/spi/internal
6504cb6d78 HHH-6098 - Slight naming changes in regards to new logging classes

Start block index: 312
End block index: 331
	private boolean existsInDatabase(Object entity, EventSource source, EntityPersister persister) {
		EntityEntry entry = source.getPersistenceContext().getEntry( entity );
		if ( entry == null ) {
			Serializable id = persister.getIdentifier( entity, source.getEntityMode() );
			if ( id != null ) {
				EntityKey key = new EntityKey( id, persister, source.getEntityMode() );
				Object managedEntity = source.getPersistenceContext().getEntity( key );
				entry = source.getPersistenceContext().getEntry( managedEntity );
			}
		}

		if ( entry == null ) {
			// perhaps this should be an exception since it is only ever used
			// in the above method?
			return false;
		}
		else {
			return entry.isExistsInDatabase();
		}
	}
