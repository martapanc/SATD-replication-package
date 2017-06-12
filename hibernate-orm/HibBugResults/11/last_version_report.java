    protected void activateLocalClusterNode() {
        try {
            Configuration cfg = jbcCache.getConfiguration();
            if (cfg.isUseRegionBasedMarshalling()) {
                org.jboss.cache.Region jbcRegion = jbcCache.getRegion(regionFqn, true);
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                if (classLoader == null) {
                    classLoader = getClass().getClassLoader();
                }
                jbcRegion.registerContextClassLoader(classLoader);
                if ( !jbcRegion.isActive() ) {
                    jbcRegion.activate();
                }
            }
            
//            // If we are using replication, we may remove the root node
//            // and then need to re-add it. In that case, the fact
//            // that it is resident will not replicate, so use a listener
//            // to set it as resident
//            if (CacheHelper.isClusteredReplication(cfg.getCacheMode()) 
//                  || CacheHelper.isClusteredInvalidation(cfg.getCacheMode())) {
//                listener = new RegionRootListener();
//                jbcCache.addCacheListener(listener);
//            }
            
            regionRoot = jbcCache.getRoot().getChild( regionFqn );
            if (regionRoot == null || !regionRoot.isValid()) {
               // Establish the region root node with a non-locking data version
               DataVersion version = optimistic ? NonLockingDataVersion.INSTANCE : null;
               regionRoot = CacheHelper.addNode(jbcCache, regionFqn, true, true, version);
            }
            else if (optimistic && regionRoot instanceof NodeSPI) {
                // FIXME Hacky workaround to JBCACHE-1202
                if ( !( ( ( NodeSPI ) regionRoot ).getVersion() instanceof NonLockingDataVersion ) ) {
                    ((NodeSPI) regionRoot).setVersion(NonLockingDataVersion.INSTANCE);
                }
            }
            if (!regionRoot.isResident()) {
               regionRoot.setResident(true);
            }
        }
        catch (Exception e) {
            throw new CacheException(e.getMessage(), e);
        }
    }
