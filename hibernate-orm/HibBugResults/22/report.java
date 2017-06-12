File path: hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
Comment: this class has no proxies (so do a shortcut)
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

Start block index: 388
End block index: 409
//	protected Object proxyOrLoad(
//		final LoadEvent event,
//		final EntityPersister persister,
//		final EntityKey keyToLoad,
//		final LoadEventListener.LoadType options) {
//
//        if (LOG.isTraceEnabled()) LOG.trace("Loading entity: "
//                                            + MessageHelper.infoString(persister,
//                                                                             event.getEntityId(),
//                                                                             event.getSession().getFactory()));
//
//        // this class has no proxies (so do a shortcut)
//        if (!persister.hasProxy()) return load(event, persister, keyToLoad, options);
//        final PersistenceContext persistenceContext = event.getSession().getPersistenceContext();
//
//		// look for a proxy
//        Object proxy = persistenceContext.getProxy(keyToLoad);
//        if (proxy != null) return returnNarrowedProxy(event, persister, keyToLoad, options, persistenceContext, proxy);
//        if (options.isAllowProxyCreation()) return createProxyIfNecessary(event, persister, keyToLoad, options, persistenceContext);
//        // return a newly loaded object
//        return load(event, persister, keyToLoad, options);
//	}
