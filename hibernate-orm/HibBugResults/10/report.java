File path: cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
Comment: FIXME hack to work around fact that calling
Initial commit id: 9ccd912b
Final commit id: 9e7e49d1
   Bugs between [       1]:
9e7e49d1f1 [ISPN-6] [HHH-4103] Tidy up commented code.
   Bugs after [       3]:
8beaccc7eb HHH-5616 - Switch to Gradle for builds .. infinispan
abc165eaba [HHH-4520] (Infinispan second level cache integration can cache stale collection data) Ported fix. testManyUsers has been disabled while ISPN-277 gets fixed. Finally, Infinispan version has been upgraded to 4.0.0.CR2.
ec31e277ec [HHH-4519] (Hibernate/Infinispan integration doesn't property handle Entity/CollectionRegionAccessStrategy evictAll) Fixed and got provider to work with forthcoming Infinispan 4.0.0.CR2 which has been just tagged but the maven repo has not been updated yet.

Start block index: 386
End block index: 412
//   public static Node addNode(Cache cache, Fqn fqn, boolean localOnly, boolean resident) throws CacheException {
//      try {
//         Option option = null;
//         if (localOnly) {
//            option = new Option();
//            option.setCacheModeLocal(localOnly);
//         }
//
//         Node root = cache.getRoot();
//         setInvocationOption(cache, option);
//         // FIXME hack to work around fact that calling
//         // Node added = root.addChild( fqn ); doesn't
//         // properly set the version on the node
//         Node added = null;
//         if (version == null) {
//            added = root.addChild(fqn);
//         } else {
//            cache.put(fqn, DUMMY, DUMMY);
//            added = root.getChild(fqn);
//         }
//         if (resident)
//            added.setResident(true);
//         return added;
//      } catch (Exception e) {
//         throw new CacheException(e);
//      }
//   }
