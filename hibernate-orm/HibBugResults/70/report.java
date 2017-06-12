File path: code/core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java
Comment: ODO: to handle concurrent writes correctly
Initial commit id: d8d6d82e
Final commit id: 4aa9cbe5
   Bugs between [       0]:

   Bugs after [       2]:
c930ebcd7d HHH-6191 - repackage org.hibernate.cache per api/spi/internal split
6504cb6d78 HHH-6098 - Slight naming changes in regards to new logging classes

Start block index: 37
End block index: 49
	public synchronized void preinvalidate(Serializable[] spaces) throws CacheException {
		//TODO: to handle concurrent writes correctly, this should return a Lock to the client
		Long ts = new Long( region.nextTimestamp() + region.getTimeout() );
		for ( int i=0; i<spaces.length; i++ ) {
			if ( log.isDebugEnabled() ) {
				log.debug( "Pre-invalidating space [" + spaces[i] + "]" );
			}
			//put() has nowait semantics, is this really appropriate?
			//note that it needs to be async replication, never local or sync
			region.put( spaces[i], ts );
		}
		//TODO: return new Lock(ts);
	}
