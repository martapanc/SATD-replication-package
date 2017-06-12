File path: cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
Comment: FIXME Hacky workaround to JBCACHE-1202
Initial commit id: 161e5cc1
Final commit id: 834be283
   Bugs between [       0]:

   Bugs after [       2]:
b6bfb72da6 HHH-4028 - Move current cache-jbosscache2 module content to cache-jbosscache
eb60160109 [HHH-3817] Don't cache stale data via putFromLoad [HHH-3818] Handle evictAll "without regard for transactions"

Start block index: 73
End block index: 117
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
                if (jbcRegion.isActive() == false) {
                    jbcRegion.activate();
                }
            }
            
            // If we are using replication, we may remove the root node
            // and then need to re-add it. In that case, the fact
            // that it is resident will not replicate, so use a listener
            // to set it as resident
            if (CacheHelper.isClusteredReplication(cfg.getCacheMode())) {
                listener = new SetResidentListener();
                jbcCache.addCacheListener(listener);
            }
            
            // Make sure the root node for the region exists and 
            // has a DataVersion that never complains
            Node regionRoot = jbcCache.getRoot().getChild( regionFqn );
            if (regionRoot == null) {                
                // Establish the region root node with a non-locking data version
                DataVersion version = optimistic ? NonLockingDataVersion.INSTANCE : null;
                regionRoot = CacheHelper.addNode(jbcCache, regionFqn, true, true, version);    
            }
            else if (optimistic && regionRoot instanceof NodeSPI) {
                // FIXME Hacky workaround to JBCACHE-1202
                if ((((NodeSPI) regionRoot).getVersion() instanceof NonLockingDataVersion) == false) {
                    ((NodeSPI) regionRoot).setVersion(NonLockingDataVersion.INSTANCE);
                }
            }
            // Never evict this node
            regionRoot.setResident(true);
        }
        catch (Exception e) {
            throw new CacheException(e.getMessage(), e);
        }
    }
