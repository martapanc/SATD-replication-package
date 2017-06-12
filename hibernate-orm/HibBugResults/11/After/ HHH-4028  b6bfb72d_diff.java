diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/CacheInstanceManager.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/CacheInstanceManager.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/CacheInstanceManager.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/CacheInstanceManager.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/TransactionalDataRegionAdapter.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/TransactionalDataRegionAdapter.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/TransactionalDataRegionAdapter.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/TransactionalDataRegionAdapter.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/access/OptimisticTransactionalAccessDelegate.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/access/OptimisticTransactionalAccessDelegate.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/access/OptimisticTransactionalAccessDelegate.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/access/OptimisticTransactionalAccessDelegate.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/access/TransactionalAccessDelegate.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/access/TransactionalAccessDelegate.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/access/TransactionalAccessDelegate.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/access/TransactionalAccessDelegate.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/builder/JndiMultiplexingCacheInstanceManager.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/builder/JndiMultiplexingCacheInstanceManager.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/builder/JndiMultiplexingCacheInstanceManager.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/builder/JndiMultiplexingCacheInstanceManager.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/builder/JndiSharedCacheInstanceManager.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/builder/JndiSharedCacheInstanceManager.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/builder/JndiSharedCacheInstanceManager.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/builder/JndiSharedCacheInstanceManager.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/builder/MultiplexingCacheInstanceManager.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/builder/MultiplexingCacheInstanceManager.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/builder/MultiplexingCacheInstanceManager.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/builder/MultiplexingCacheInstanceManager.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/builder/SharedCacheInstanceManager.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/builder/SharedCacheInstanceManager.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/builder/SharedCacheInstanceManager.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/builder/SharedCacheInstanceManager.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/CollectionRegionImpl.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/collection/CollectionRegionImpl.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/CollectionRegionImpl.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/collection/CollectionRegionImpl.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/OptimisticReadOnlyAccess.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/collection/OptimisticReadOnlyAccess.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/OptimisticReadOnlyAccess.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/collection/OptimisticReadOnlyAccess.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/OptimisticTransactionalAccess.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/collection/OptimisticTransactionalAccess.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/OptimisticTransactionalAccess.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/collection/OptimisticTransactionalAccess.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/ReadOnlyAccess.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/collection/ReadOnlyAccess.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/ReadOnlyAccess.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/collection/ReadOnlyAccess.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/TransactionalAccess.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/collection/TransactionalAccess.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/collection/TransactionalAccess.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/collection/TransactionalAccess.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/EntityRegionImpl.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/entity/EntityRegionImpl.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/EntityRegionImpl.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/entity/EntityRegionImpl.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/OptimisticReadOnlyAccess.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/entity/OptimisticReadOnlyAccess.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/OptimisticReadOnlyAccess.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/entity/OptimisticReadOnlyAccess.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/OptimisticTransactionalAccess.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/entity/OptimisticTransactionalAccess.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/OptimisticTransactionalAccess.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/entity/OptimisticTransactionalAccess.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/ReadOnlyAccess.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/entity/ReadOnlyAccess.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/ReadOnlyAccess.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/entity/ReadOnlyAccess.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/TransactionalAccess.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/entity/TransactionalAccess.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/entity/TransactionalAccess.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/entity/TransactionalAccess.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/query/QueryResultsRegionImpl.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/query/QueryResultsRegionImpl.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/query/QueryResultsRegionImpl.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/query/QueryResultsRegionImpl.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/timestamp/ClusteredConcurrentTimestampsRegionImpl.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/timestamp/ClusteredConcurrentTimestampsRegionImpl.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/timestamp/ClusteredConcurrentTimestampsRegionImpl.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/timestamp/ClusteredConcurrentTimestampsRegionImpl.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/timestamp/TimestampsRegionImpl.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/timestamp/TimestampsRegionImpl.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/timestamp/TimestampsRegionImpl.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/timestamp/TimestampsRegionImpl.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/CacheHelper.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/util/CacheHelper.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/CacheHelper.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/util/CacheHelper.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/CircumventChecksDataVersion.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/util/CircumventChecksDataVersion.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/CircumventChecksDataVersion.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/util/CircumventChecksDataVersion.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/DataVersionAdapter.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/util/DataVersionAdapter.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/DataVersionAdapter.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/util/DataVersionAdapter.java
diff --git a/cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/NonLockingDataVersion.java b/cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/util/NonLockingDataVersion.java
similarity index 100%
rename from cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/NonLockingDataVersion.java
rename to cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/util/NonLockingDataVersion.java
diff --git a/cache-jbosscache2/src/main/resources/org/hibernate/cache/jbc2/builder/jbc2-configs.xml b/cache-jbosscache/src/main/resources/org/hibernate/cache/jbc2/builder/jbc2-configs.xml
similarity index 100%
rename from cache-jbosscache2/src/main/resources/org/hibernate/cache/jbc2/builder/jbc2-configs.xml
rename to cache-jbosscache/src/main/resources/org/hibernate/cache/jbc2/builder/jbc2-configs.xml
diff --git a/cache-jbosscache2/src/main/resources/org/hibernate/cache/jbc2/builder/jgroups-stacks.xml b/cache-jbosscache/src/main/resources/org/hibernate/cache/jbc2/builder/jgroups-stacks.xml
similarity index 100%
rename from cache-jbosscache2/src/main/resources/org/hibernate/cache/jbc2/builder/jgroups-stacks.xml
rename to cache-jbosscache/src/main/resources/org/hibernate/cache/jbc2/builder/jgroups-stacks.xml
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/AbstractEntityCollectionRegionTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/AbstractEntityCollectionRegionTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/AbstractEntityCollectionRegionTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/AbstractEntityCollectionRegionTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/AbstractGeneralDataRegionTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/AbstractGeneralDataRegionTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/AbstractGeneralDataRegionTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/AbstractGeneralDataRegionTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/AbstractJBossCacheTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/AbstractJBossCacheTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/AbstractJBossCacheTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/AbstractJBossCacheTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/AbstractRegionImplTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/AbstractRegionImplTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/AbstractRegionImplTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/AbstractRegionImplTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/JBossCacheComplianceTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/JBossCacheComplianceTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/JBossCacheComplianceTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/JBossCacheComplianceTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/JBossCacheRegionFactoryTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/JBossCacheRegionFactoryTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/JBossCacheRegionFactoryTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/JBossCacheRegionFactoryTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/Jbc2ConfigsXmlValidityTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/Jbc2ConfigsXmlValidityTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/Jbc2ConfigsXmlValidityTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/Jbc2ConfigsXmlValidityTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/builder/CacheInstanceManagerTestBase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/builder/CacheInstanceManagerTestBase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/builder/CacheInstanceManagerTestBase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/builder/CacheInstanceManagerTestBase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/builder/MultiplexedCacheInstanceManagerTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/builder/MultiplexedCacheInstanceManagerTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/builder/MultiplexedCacheInstanceManagerTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/builder/MultiplexedCacheInstanceManagerTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/builder/SharedCacheInstanceManagerTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/builder/SharedCacheInstanceManagerTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/builder/SharedCacheInstanceManagerTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/builder/SharedCacheInstanceManagerTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/AbstractCollectionRegionAccessStrategyTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/AbstractCollectionRegionAccessStrategyTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/AbstractCollectionRegionAccessStrategyTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/AbstractCollectionRegionAccessStrategyTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/AbstractReadOnlyAccessTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/AbstractReadOnlyAccessTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/AbstractReadOnlyAccessTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/AbstractReadOnlyAccessTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/AbstractTransactionalAccessTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/AbstractTransactionalAccessTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/AbstractTransactionalAccessTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/AbstractTransactionalAccessTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/CollectionRegionImplTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/CollectionRegionImplTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/CollectionRegionImplTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/CollectionRegionImplTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccInvalidatedTransactionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccInvalidatedTransactionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccInvalidatedTransactionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccInvalidatedTransactionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccReadOnlyExtraAPITestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccReadOnlyExtraAPITestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccReadOnlyExtraAPITestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccReadOnlyExtraAPITestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccReadOnlyTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccReadOnlyTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccReadOnlyTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccReadOnlyTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccReplicatedTransactionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccReplicatedTransactionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccReplicatedTransactionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccReplicatedTransactionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccTransactionalExtraAPITestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccTransactionalExtraAPITestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccTransactionalExtraAPITestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/MvccTransactionalExtraAPITestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticInvalidatedTransactionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticInvalidatedTransactionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticInvalidatedTransactionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticInvalidatedTransactionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticReadOnlyExtraAPITestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticReadOnlyExtraAPITestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticReadOnlyExtraAPITestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticReadOnlyExtraAPITestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticReadOnlyTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticReadOnlyTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticReadOnlyTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticReadOnlyTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticReplicatedTransactionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticReplicatedTransactionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticReplicatedTransactionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticReplicatedTransactionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticTransactionalExtraAPITestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticTransactionalExtraAPITestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticTransactionalExtraAPITestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/OptimisticTransactionalExtraAPITestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticInvalidatedTransactionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticInvalidatedTransactionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticInvalidatedTransactionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticInvalidatedTransactionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticReadOnlyExtraAPITestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticReadOnlyExtraAPITestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticReadOnlyExtraAPITestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticReadOnlyExtraAPITestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticReadOnlyTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticReadOnlyTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticReadOnlyTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticReadOnlyTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticReplicatedTransactionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticReplicatedTransactionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticReplicatedTransactionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticReplicatedTransactionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticTransactionalExtraAPITestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticTransactionalExtraAPITestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticTransactionalExtraAPITestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/collection/PessimisticTransactionalExtraAPITestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/AbstractEntityRegionAccessStrategyTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/AbstractEntityRegionAccessStrategyTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/AbstractEntityRegionAccessStrategyTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/AbstractEntityRegionAccessStrategyTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/AbstractReadOnlyAccessTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/AbstractReadOnlyAccessTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/AbstractReadOnlyAccessTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/AbstractReadOnlyAccessTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/AbstractTransactionalAccessTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/AbstractTransactionalAccessTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/AbstractTransactionalAccessTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/AbstractTransactionalAccessTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/EntityRegionImplTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/EntityRegionImplTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/EntityRegionImplTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/EntityRegionImplTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccInvalidatedTransactionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccInvalidatedTransactionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccInvalidatedTransactionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccInvalidatedTransactionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccReadOnlyExtraAPITestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccReadOnlyExtraAPITestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccReadOnlyExtraAPITestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccReadOnlyExtraAPITestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccReadOnlyTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccReadOnlyTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccReadOnlyTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccReadOnlyTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccReplicatedTransactionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccReplicatedTransactionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccReplicatedTransactionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccReplicatedTransactionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccTransactionalExtraAPITestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccTransactionalExtraAPITestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccTransactionalExtraAPITestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/MvccTransactionalExtraAPITestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticInvalidatedTransactionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticInvalidatedTransactionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticInvalidatedTransactionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticInvalidatedTransactionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticReadOnlyExtraAPITestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticReadOnlyExtraAPITestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticReadOnlyExtraAPITestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticReadOnlyExtraAPITestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticReadOnlyTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticReadOnlyTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticReadOnlyTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticReadOnlyTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticReplicatedTransactionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticReplicatedTransactionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticReplicatedTransactionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticReplicatedTransactionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticTransactionalExtraAPITestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticTransactionalExtraAPITestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticTransactionalExtraAPITestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/OptimisticTransactionalExtraAPITestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticInvalidatedTransactionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticInvalidatedTransactionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticInvalidatedTransactionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticInvalidatedTransactionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticReadOnlyExtraAPITestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticReadOnlyExtraAPITestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticReadOnlyExtraAPITestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticReadOnlyExtraAPITestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticReadOnlyTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticReadOnlyTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticReadOnlyTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticReadOnlyTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticReplicatedTransactionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticReplicatedTransactionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticReplicatedTransactionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticReplicatedTransactionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticTransactionalExtraAPITestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticTransactionalExtraAPITestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticTransactionalExtraAPITestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/entity/PessimisticTransactionalExtraAPITestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/AbstractEntityCacheFunctionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/AbstractEntityCacheFunctionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/AbstractEntityCacheFunctionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/AbstractEntityCacheFunctionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/AbstractQueryCacheFunctionalTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/AbstractQueryCacheFunctionalTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/AbstractQueryCacheFunctionalTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/AbstractQueryCacheFunctionalTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/CacheTestCaseBase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/CacheTestCaseBase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/CacheTestCaseBase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/CacheTestCaseBase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/Contact.hbm.xml b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/Contact.hbm.xml
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/Contact.hbm.xml
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/Contact.hbm.xml
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/Contact.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/Contact.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/Contact.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/Contact.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/Customer.hbm.xml b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/Customer.hbm.xml
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/Customer.hbm.xml
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/Customer.hbm.xml
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/Customer.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/Customer.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/Customer.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/Customer.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/DualNodeTestCaseBase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/DualNodeTestCaseBase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/DualNodeTestCaseBase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/DualNodeTestCaseBase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/Item.hbm.xml b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/Item.hbm.xml
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/Item.hbm.xml
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/Item.hbm.xml
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/Item.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/Item.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/Item.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/Item.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCConcurrentWriteTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCConcurrentWriteTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCConcurrentWriteTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCConcurrentWriteTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCEntityReplicationTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCEntityReplicationTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCEntityReplicationTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCEntityReplicationTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCJBossCacheTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCJBossCacheTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCJBossCacheTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCJBossCacheTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCSessionRefreshTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCSessionRefreshTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCSessionRefreshTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/MVCCSessionRefreshTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/OptimisticEntityReplicationTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/OptimisticEntityReplicationTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/OptimisticEntityReplicationTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/OptimisticEntityReplicationTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/OptimisticJBossCacheTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/OptimisticJBossCacheTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/OptimisticJBossCacheTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/OptimisticJBossCacheTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/OptimisticSessionRefreshTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/OptimisticSessionRefreshTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/OptimisticSessionRefreshTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/OptimisticSessionRefreshTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticEntityReplicationTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticEntityReplicationTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticEntityReplicationTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticEntityReplicationTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticJBossCacheTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticJBossCacheTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticJBossCacheTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticJBossCacheTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticRepeatableSessionRefreshTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticRepeatableSessionRefreshTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticRepeatableSessionRefreshTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticRepeatableSessionRefreshTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticSessionRefreshTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticSessionRefreshTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticSessionRefreshTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/PessimisticSessionRefreshTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/VersionedItem.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/VersionedItem.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/VersionedItem.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/VersionedItem.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/bulk/MVCCBulkOperationsTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/bulk/MVCCBulkOperationsTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/bulk/MVCCBulkOperationsTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/bulk/MVCCBulkOperationsTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/bulk/OptimisticBulkOperationsTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/bulk/OptimisticBulkOperationsTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/bulk/OptimisticBulkOperationsTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/bulk/OptimisticBulkOperationsTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/bulk/PessimisticBulkOperationsTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/bulk/PessimisticBulkOperationsTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/bulk/PessimisticBulkOperationsTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/bulk/PessimisticBulkOperationsTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/Account.hbm.xml b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/Account.hbm.xml
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/Account.hbm.xml
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/Account.hbm.xml
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/Account.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/Account.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/Account.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/Account.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/AccountHolder.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/AccountHolder.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/AccountHolder.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/AccountHolder.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/CacheAccessListener.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/CacheAccessListener.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/CacheAccessListener.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/CacheAccessListener.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/ClassLoaderTestDAO.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/ClassLoaderTestDAO.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/ClassLoaderTestDAO.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/ClassLoaderTestDAO.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/MVCCIsolatedClassLoaderTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/MVCCIsolatedClassLoaderTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/MVCCIsolatedClassLoaderTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/MVCCIsolatedClassLoaderTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/OptimisticIsolatedClassLoaderTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/OptimisticIsolatedClassLoaderTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/OptimisticIsolatedClassLoaderTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/OptimisticIsolatedClassLoaderTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/PessimisticIsolatedClassLoaderTest.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/PessimisticIsolatedClassLoaderTest.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/PessimisticIsolatedClassLoaderTest.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/classloader/PessimisticIsolatedClassLoaderTest.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/mvcc-treecache.xml b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/mvcc-treecache.xml
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/mvcc-treecache.xml
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/mvcc-treecache.xml
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/optimistic-treecache.xml b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/optimistic-treecache.xml
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/optimistic-treecache.xml
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/optimistic-treecache.xml
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/pessimistic-treecache.xml b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/pessimistic-treecache.xml
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/pessimistic-treecache.xml
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/pessimistic-treecache.xml
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/CustomClassLoaderCacheManager.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/CustomClassLoaderCacheManager.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/CustomClassLoaderCacheManager.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/CustomClassLoaderCacheManager.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeConnectionProviderImpl.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeConnectionProviderImpl.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeConnectionProviderImpl.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeConnectionProviderImpl.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeJtaTransactionImpl.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeJtaTransactionImpl.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeJtaTransactionImpl.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeJtaTransactionImpl.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeJtaTransactionManagerImpl.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeJtaTransactionManagerImpl.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeJtaTransactionManagerImpl.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeJtaTransactionManagerImpl.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeTestUtil.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeTestUtil.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeTestUtil.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeTestUtil.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeTransactionManagerLookup.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeTransactionManagerLookup.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeTransactionManagerLookup.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/DualNodeTransactionManagerLookup.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/IsolatedCacheTestSetup.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/IsolatedCacheTestSetup.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/IsolatedCacheTestSetup.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/IsolatedCacheTestSetup.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/TestCacheInstanceManager.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/TestCacheInstanceManager.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/TestCacheInstanceManager.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/TestCacheInstanceManager.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/TestJBossCacheRegionFactory.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/TestJBossCacheRegionFactory.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/TestJBossCacheRegionFactory.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/functional/util/TestJBossCacheRegionFactory.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/query/QueryRegionImplTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/query/QueryRegionImplTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/query/QueryRegionImplTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/query/QueryRegionImplTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/timestamp/ClusteredConcurrentTimestampRegionTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/timestamp/ClusteredConcurrentTimestampRegionTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/timestamp/ClusteredConcurrentTimestampRegionTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/timestamp/ClusteredConcurrentTimestampRegionTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/timestamp/TimestampsRegionImplTestCase.java b/cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/timestamp/TimestampsRegionImplTestCase.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/timestamp/TimestampsRegionImplTestCase.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc2/timestamp/TimestampsRegionImplTestCase.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/tm/jbc2/BatchModeTransactionManagerLookup.java b/cache-jbosscache/src/test/java/org/hibernate/test/tm/jbc2/BatchModeTransactionManagerLookup.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/tm/jbc2/BatchModeTransactionManagerLookup.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/tm/jbc2/BatchModeTransactionManagerLookup.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/util/CacheManagerTestSetup.java b/cache-jbosscache/src/test/java/org/hibernate/test/util/CacheManagerTestSetup.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/util/CacheManagerTestSetup.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/util/CacheManagerTestSetup.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/util/CacheTestSupport.java b/cache-jbosscache/src/test/java/org/hibernate/test/util/CacheTestSupport.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/util/CacheTestSupport.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/util/CacheTestSupport.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/util/CacheTestUtil.java b/cache-jbosscache/src/test/java/org/hibernate/test/util/CacheTestUtil.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/util/CacheTestUtil.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/util/CacheTestUtil.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/util/SelectedClassnameClassLoader.java b/cache-jbosscache/src/test/java/org/hibernate/test/util/SelectedClassnameClassLoader.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/util/SelectedClassnameClassLoader.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/util/SelectedClassnameClassLoader.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/util/SelectedClassnameClassLoaderTestSetup.java b/cache-jbosscache/src/test/java/org/hibernate/test/util/SelectedClassnameClassLoaderTestSetup.java
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/util/SelectedClassnameClassLoaderTestSetup.java
rename to cache-jbosscache/src/test/java/org/hibernate/test/util/SelectedClassnameClassLoaderTestSetup.java
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/util/optimistic-local-cache.xml b/cache-jbosscache/src/test/java/org/hibernate/test/util/optimistic-local-cache.xml
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/util/optimistic-local-cache.xml
rename to cache-jbosscache/src/test/java/org/hibernate/test/util/optimistic-local-cache.xml
diff --git a/cache-jbosscache2/src/test/java/org/hibernate/test/util/pessimistic-local-cache.xml b/cache-jbosscache/src/test/java/org/hibernate/test/util/pessimistic-local-cache.xml
similarity index 100%
rename from cache-jbosscache2/src/test/java/org/hibernate/test/util/pessimistic-local-cache.xml
rename to cache-jbosscache/src/test/java/org/hibernate/test/util/pessimistic-local-cache.xml
diff --git a/cache-jbosscache2/src/test/resources/hibernate.properties b/cache-jbosscache/src/test/resources/hibernate.properties
similarity index 100%
rename from cache-jbosscache2/src/test/resources/hibernate.properties
rename to cache-jbosscache/src/test/resources/hibernate.properties
diff --git a/cache-jbosscache2/src/test/resources/log4j.properties b/cache-jbosscache/src/test/resources/log4j.properties
similarity index 100%
rename from cache-jbosscache2/src/test/resources/log4j.properties
rename to cache-jbosscache/src/test/resources/log4j.properties
diff --git a/cache-jbosscache2/src/test/resources/treecache.xml b/cache-jbosscache/src/test/resources/treecache.xml
similarity index 100%
rename from cache-jbosscache2/src/test/resources/treecache.xml
rename to cache-jbosscache/src/test/resources/treecache.xml
