10/report.java
Satd-method: //   public static Node addNode(Cache cache, Fqn fqn, boolean localOnly, boolean resident) throws CacheException {
********************************************
********************************************
10/Between/ [ISPN-6]  9e7e49d1_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-//   public static Node addNode(Cache cache, Fqn fqn, boolean localOnly, boolean resident) throws CacheException {

Lines added: 0. Lines removed: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
// 
-//   public void evict(Object key) throws CacheException {
-//      cache.evict(key);
-//   }
-//      delegate.evict(key);
-//   /**
-//    * HACKY WAY TO GET THE TRANSACTION MANAGER, TODO: resolve it!
-//    */
-//   private static TransactionManager getTransactionManager(Properties properties) {
-////      return cache == null ? null : extractComponent(cache, TransactionManager.class);
-//      return TransactionManagerLookupFactory.getTransactionManager(properties);
-//   }
-//   
-//   public static <T> T extractComponent(Cache cache, Class<T> componentType) {
-//      ComponentRegistry cr = extractComponentRegistry(cache);
-//      return cr.getComponent(componentType);
-//   }
-//   
-//   public static ComponentRegistry extractComponentRegistry(Cache cache) {
-//      return (ComponentRegistry) extractField(cache, "componentRegistry");
-//   }
-//   
-//   public static Object extractField(Object target, String fieldName) {
-//      return extractField(target.getClass(), target, fieldName);
-//   }
-//   
-//   public static Object extractField(Class type, Object target, String fieldName) {
-//      Field field;
-//      try {
-//         field = type.getDeclaredField(fieldName);
-//         field.setAccessible(true);
-//         return field.get(target);
-//      }
-//      catch (Exception e) {
-//         if (type.equals(Object.class)) {
-//            e.printStackTrace();
-//            return null;
-//         } else {
-//            // try with superclass!!
-//            return extractField(type.getSuperclass(), target, fieldName);
-//         }
-//      }
-//   }
-//      Fqn fqn = event.getFqn();
-//      Fqn regFqn = getRegionFqn();
-//      if (fqn.size() == regFqn.size() + 1 && fqn.isChildOf(regFqn)) {
-//         Object key = fqn.get(regFqn.size());
-//         localCache.remove(key);
-//      } else if (fqn.equals(regFqn)) {
-//         localCache.clear();
-//      }
-//   public static void evict(Cache cache, Object key) throws CacheException {
-//      try {
-//         cache.evict(key);
-//      } catch (Exception e) {
-//         throw new CacheException(e);
-//      }
-//   }
-//   public static Node addNode(Cache cache, Fqn fqn, boolean localOnly, boolean resident) throws CacheException {
-//      try {
-//         Option option = null;
-//         if (localOnly) {
-//            option = new Option();
-//            option.setCacheModeLocal(localOnly);
-//         }
-//         Node root = cache.getRoot();
-//         setInvocationOption(cache, option);
-//         // FIXME hack to work around fact that calling
-//         // Node added = root.addChild( fqn ); doesn't
-//         // properly set the version on the node
-//         Node added = null;
-//         if (version == null) {
-//            added = root.addChild(fqn);
-//         } else {
-//            cache.put(fqn, DUMMY, DUMMY);
-//            added = root.getChild(fqn);
-//         }
-//         if (resident)
-//            added.setResident(true);
-//         return added;
-//      } catch (Exception e) {
-//         throw new CacheException(e);
-//      }
-//   }
-//    * Assigns the given Option to the cache's {@link InvocationContext}. Does nothing if
-//    * <code>option</code> is <code>null</code>.
-//    * 
-//    * @param cache
-//    *           the cache. Cannot be <code>null</code>.
-//    * @param option
-//    *           the option. May be <code>null</code>.
-//    * 
-//    * @see {@link Cache#getInvocationContext()}
-//    * @see {@link InvocationContext#setOptionOverrides(Option)}
-//    */
-//   public static void setInvocationOption(Cache cache, Option option) {
-//      if (option != null) {
-//         cache.getInvocationContext().setOptionOverrides(option);
-//      }
-//   }
-//   /**
-//    * Creates an {@link Option} using the given {@link DataVersion} and passes it to
-//    * {@link #setInvocationOption(Cache, Option)}.
-//    * 
-//    * @param cache
-//    *           the cache to set the Option on. Cannot be <code>null</code>.
-//    * @param version
-//    *           the DataVersion to set. Cannot be <code>null</code>.
-//    */
-//   public static void setDataVersionOption(Cache cache, DataVersion version) {
-//      Option option = new Option();
-//      option.setDataVersion(version);
-//      setInvocationOption(cache, option);
-//   }
-//        cfg.setProperty(SharedCacheInstanceManager.CACHE_RESOURCE_PROP, CacheTestUtil.LOCAL_PESSIMISTIC_CACHE);
-//        cfg.setProperty(SharedCacheInstanceManager.CACHE_RESOURCE_PROP, CacheTestUtil.LOCAL_PESSIMISTIC_CACHE);
-        // Make it non-transactional
-//        cfg.setProperty(SharedCacheInstanceManager.CACHE_RESOURCE_PROP, CacheTestUtil.LOCAL_PESSIMISTIC_CACHE);
+      // Make it non-transactional
-//      boolean invalidation = CacheHelper.isClusteredInvalidation(localCache);
-//      String regionName = REGION_PREFIX;
-      // Fqn regionFqn = getRegionFqn(REGION_NAME, REGION_PREFIX);
-      // Node regionRoot = localCache.getRoot().getChild(regionFqn);
-      // assertFalse(regionRoot == null);
-      // assertEquals(0, getValidChildrenCount(regionRoot));
-      // assertTrue(regionRoot.isResident());
-      // regionRoot = remoteCache.getRoot().getChild(regionFqn);
-      // assertFalse(regionRoot == null);
-      // assertEquals(0, getValidChildrenCount(regionRoot));
-      // assertTrue(regionRoot.isResident());
-      // regionRoot = localCache.getRoot().getChild(regionFqn);
-      // assertFalse(regionRoot == null);
-      // assertEquals(0, getValidChildrenCount(regionRoot));
-      // assertTrue(regionRoot.isValid());
-      // assertTrue(regionRoot.isResident());
-      // regionRoot = remoteCache.getRoot().getChild(regionFqn);
-      // assertFalse(regionRoot == null);
-      // assertTrue(regionRoot.isValid());
-      // assertTrue(regionRoot.isResident());
-      //        
-      // assertEquals(0, getValidChildrenCount(regionRoot));
-      // Not invalidation, so we didn't insert a child above
-      // regionRoot = remoteCache.getRoot().getChild(regionFqn);
-      // assertFalse(regionRoot == null);
-      // assertTrue(regionRoot.isValid());
-      // assertTrue(regionRoot.isResident());
-      // // Region root should have 1 child -- the one we added above
-      // assertEquals(1, getValidChildrenCount(regionRoot));
-      // Revalidate the region root
-//      if (!isBlockingReads())
-//      else {
-//         // Reader should be blocking for lock
-//         assertFalse("Threads completed", completionLatch.await(250, TimeUnit.MILLISECONDS));
-//         commitLatch.countDown();
-//         assertTrue("Threads completed", completionLatch.await(1, TimeUnit.SECONDS));
-//      }
-               // This will block w/ pessimistic locking and then
-               // read the new value; w/ optimistic it shouldn't
-               // block and will read the old value
-//               Object expected = !isBlockingReads() ? VALUE1 : VALUE2;
+               // This won't block w/ mvc and will read the old value
-//      if (!isBlockingReads())
-         // Should complete promptly
-//      else {
-//         // Reader thread should be blocking
-//         assertFalse(completionLatch.await(250, TimeUnit.MILLISECONDS));
-//         // Let the writer commit down
-//         commitLatch.countDown();
-//         assertTrue(completionLatch.await(1, TimeUnit.SECONDS));
-//      }
+      // Should complete promptly
-      // sleep(1000);
-//      Fqn regionFqn = getRegionFqn(REGION_NAME, REGION_PREFIX);
-//      Node regionRoot = localCache.getRoot().getChild(regionFqn);
-//      assertFalse(regionRoot == null);
-//      assertTrue(regionRoot.isResident());
-//      regionRoot = remoteCache.getRoot().getChild(regionFqn);
-//      assertFalse(regionRoot == null);
-//      assertTrue(regionRoot.isResident());
-//      regionRoot = localCache.getRoot().getChild(regionFqn);
-//      assertFalse(regionRoot == null);
-//      assertEquals(0, getValidChildrenCount(regionRoot));
-//      assertTrue(regionRoot.isValid());
-//      assertTrue(regionRoot.isResident());
-//      regionRoot = remoteCache.getRoot().getChild(regionFqn);
-//      assertFalse(regionRoot == null);
-//      assertTrue(regionRoot.isValid());
-//      assertTrue(regionRoot.isResident());
-//      // Not invalidation, so we didn't insert a child above
-//      assertEquals(0, getValidChildrenCount(regionRoot));
-      // Revalidate the region root
-//      regionRoot = remoteCache.getRoot().getChild(regionFqn);
-//      assertFalse(regionRoot == null);
-//      assertTrue(regionRoot.isValid());
-//      assertTrue(regionRoot.isResident());
-//      // Region root should have 1 child -- the one we added above
-//      assertEquals(1, getValidChildrenCount(regionRoot));
-//         localCache = localRegionFactory.getCacheManager().getCache("entity");
-//         remoteCache = remoteRegionFactory.getCacheManager().getCache("entity");
-//      getSessions().evictEntity(Item.class.getName());
-//      return org.hibernate.test.tm.ConnectionProviderImpl.class;
-//      return org.hibernate.test.tm.TransactionManagerLookupImpl.class;
-//   public void cleanup() throws Exception {
-//      String deleteContactHQL = "delete from Contact";
-//      String deleteCustomerHQL = "delete from Customer";
-//      tm.begin();
-//      try {
-//         Session session = getSessions().getCurrentSession();
-//         session.createQuery(deleteContactHQL).setFlushMode(FlushMode.AUTO).executeUpdate();
-//         session.createQuery(deleteCustomerHQL).setFlushMode(FlushMode.AUTO).executeUpdate();
-//         tm.commit();
-//      } catch (Exception e) {
-//         try {
-//            tm.rollback();
-//         } catch (Exception ee) {
-//            // ignored
-//         }
-//         throw e;
-//      }
-//   }
-//   HashSet<Fqn<String>> modified = new HashSet<Fqn<String>>();
-//   HashSet<Fqn<String>> accessed = new HashSet<Fqn<String>>();
-//      boolean saw = false;
-//      for (Object key : sawEvents) {
-//         
-//      }
-//      Fqn<String> fqn = Fqn.fromString(regionName);
-//      for (Iterator<Fqn<String>> it = sawEvent.iterator(); it.hasNext();) {
-//         Fqn<String> modified = (Fqn<String>) it.next();
-//         if (modified.isChildOf(fqn)) {
-//            it.remove();
-//            saw = true;
-//         }
-//      }
-//      return saw;
-//      this.cacheConfig = cacheConfig;
-//      // At this point the TCCL cannot see the isolatedClasses
-//      // We want the caches to use this CL as their default classloader
+      // At this point the TCCL cannot see the isolatedClasses
+      // We want the caches to use this CL as their default classloader
-//      org.jgroups.ChannelFactory cf = new org.jgroups.JChannelFactory();
-//      cf.setMultiplexerConfig(DEF_JGROUPS_RESOURCE);
-//      // Use a CacheManager that will inject the desired defaultClassLoader into our caches
-//      CustomClassLoaderCacheManager cm = new CustomClassLoaderCacheManager(DEF_CACHE_FACTORY_RESOURCE, cf, tccl);
-//      cm.start();
-//      CacheManager manager = new DefaultCacheManager("org/hibernate/test/cache/infinispan/functional/classloader/infinispan-configs.xml");
-//      ClusterAwareRegionFactory.addCacheManager(AbstractDualNodeTestCase.LOCAL, manager);
-//      ClusterAwareRegionFactory.addCacheManager(AbstractDualNodeTestCase.REMOTE, manager);
-//      cm.getCache(cacheConfig, true);
-//      // Repeat for the "remote" cache
-//      cf = new org.jgroups.JChannelFactory();
-//      cf.setMultiplexerConfig(DEF_JGROUPS_RESOURCE);
-//      cm = new CustomClassLoaderCacheManager(DEF_CACHE_FACTORY_RESOURCE, cf, tccl);
-//      cm.start();
-//      TestCacheInstanceManager.addTestCacheManager(DualNodeTestUtil.REMOTE, cm);
-//      cm.getCache(cacheConfig, true);
-//         Set<Contact> contacts = customer.getContacts();
-//         for (Contact c : contacts) {
-//            if (c.getName().equals("Kabir")) {
-//               contacts.remove(c);
-//            } else {
-//               contactIds.add(c.getId());
-//            }
-//         }
-//         ids.contactIds = contactIds;
-//         customer.setContacts(contacts);
-//      @CacheEntryModified
-//      public void nodeModified(CacheEntryModifiedEvent event) {
-//         log.debug(event.toString());
-//         if (!event.isPre()) {
-//            MarshalledValue mv = (MarshalledValue) event.getKey();
-//            CacheKey cacheKey = (CacheKey) mv.get();
-//            Integer primKey = (Integer) cacheKey.getKey();
-//            String key = (String) cacheKey.getEntityOrRoleName() + '#' + primKey;
-//            log.debug("MyListener[" + name +"] - Modified key " + key);
-//            // String name = fqn.toString();
-//            String token = ".functional.";
-//            int index = key.indexOf(token);
-//            if (index > -1) {
-//               index += token.length();
-//               key = key.substring(index);
-//               log.debug("MyListener[" + name +"] - recording modification of " + key);
-//               visited.add(key);
-//            }
-//         }
-//      }
-   // public void testGetDoesNotBlockPutPessimisticRepeatableRead() throws Exception {
-   // getDoesNotBlockPutTest();
-   // }
-//         if (!runXaResourcePrepare()) {
-//            status = Status.STATUS_ROLLING_BACK;
-//         } else {
-//            status = Status.STATUS_PREPARED;
-//         }
-//         runXaResourceCommitTx();
-//      status = Status.STATUS_ROLLING_BACK;
-//      runXaResourceRollback();

Lines added containing method: 5. Lines removed containing method: 291. Tot = 296
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* setResident
* getChild
* setCacheModeLocal
* getRoot
* addChild
********************************************
********************************************