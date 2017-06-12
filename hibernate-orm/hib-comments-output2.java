// NONE might be a better option moving forward in the case of callable
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
a7179fbc49 HHH-6371 - Develop metamodel binding creation using a push approach
819f8da9ea HHH-5672 - Develop the binding model (binding between logical and relational)
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-			// NONE might be a better option moving forward in the case of callable
a7179fbc4 hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HbmHelper.java
-			// NONE might be a better option moving forward in the case of callable
819f8da9e hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmHelper.java
+			// NONE might be a better option moving forward in the case of callable
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+			// NONE might be a better option moving forward in the case of callable
*_*_*
//perhaps not really necessary...
# shas =  1
*_*_*
// todo : what else to do here?
ba3359fe62 HHH-11152: Added BytecodeProvider based on Byte Buddy
82d2ef4b1f HHH-6025 - Remove cglib dependencies
d8d6d82e30 SVN layout migration for core/trunk
ba3359fe6 hibernate-core/src/main/java/org/hibernate/bytecode/internal/bytebuddy/PassThroughInterceptor.java
+			// todo : what else to do here?
82d2ef4b1 hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
-				// todo : what else to do here?
d8d6d82e3 code/core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
d8d6d82e3 code/core/src/main/java/org/hibernate/bytecode/javassist/ProxyFactoryFactoryImpl.java
+				// todo : what else to do here?
+				// todo : what else to do here?
*_*_*
//TODO: disable batch loading if lockMode > READ?
35ca4c3563 HHH-4546 add JPA 2.0 locking.  Still need more LockRequest support in AbstractEntityPersister.getAppropriateLoader(), may need to refactor.
d8d6d82e30 SVN layout migration for core/trunk
35ca4c356 core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+		//TODO: disable batch loading if lockMode > READ?
d8d6d82e3 code/core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+		//TODO: disable batch loading if lockMode > READ?
*_*_*
// todo : what else to do here?
ba3359fe62 HHH-11152: Added BytecodeProvider based on Byte Buddy
82d2ef4b1f HHH-6025 - Remove cglib dependencies
d8d6d82e30 SVN layout migration for core/trunk
ba3359fe6 hibernate-core/src/main/java/org/hibernate/bytecode/internal/bytebuddy/PassThroughInterceptor.java
+			// todo : what else to do here?
82d2ef4b1 hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
-				// todo : what else to do here?
d8d6d82e3 code/core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
d8d6d82e3 code/core/src/main/java/org/hibernate/bytecode/javassist/ProxyFactoryFactoryImpl.java
+				// todo : what else to do here?
+				// todo : what else to do here?
*_*_*
//TODO: to handle concurrent writes correctly
884f6a2455 HHH-4881 - restrict polymorphic query results
4aa9cbe5b7 HHH-5823 - Poor multithread performance in UpdateTimestampsCache class
ccd23dbd3d HHH-5823 - Poor multithread performance in UpdateTimestampsCache class
d8d6d82e30 SVN layout migration for core/trunk
884f6a245 hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java
-		//TODO: to handle concurrent writes correctly, the client should pass in a Lock
4aa9cbe5b hibernate-core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java
-		//TODO: to handle concurrent writes correctly, this should return a Lock to the client
-	 	//TODO: to handle concurrent writes correctly, the client should pass in a Lock
+		//TODO: to handle concurrent writes correctly, the client should pass in a Lock
ccd23dbd3 hibernate-core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java
-		//TODO: to handle concurrent writes correctly, this should return a Lock to the client
-	 	//TODO: to handle concurrent writes correctly, the client should pass in a Lock
+		//TODO: to handle concurrent writes correctly, the client should pass in a Lock
d8d6d82e3 code/core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java
+		//TODO: to handle concurrent writes correctly, this should return a Lock to the client
+	 	//TODO: to handle concurrent writes correctly, the client should pass in a Lock
*_*_*
// gets a chance to see all hibernate.c3p0.*
# shas =  1
*_*_*
//note there is a wierd implementation in the client side
217898d8aa HHH-5212 - Alter SQLFunction contract to be more flexible
d8d6d82e30 SVN layout migration for core/trunk
217898d8a core/src/main/java/org/hibernate/dialect/function/CastFunction.java
-		return columnType; //note there is a wierd implementation in the client side
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/function/CastFunction.java
+		return columnType; //note there is a wierd implementation in the client side
*_*_*
// this should be refactored to instead expose a method to assemble a EntityEntry based on this
6a388b754c HHH-8159 - Apply fixups indicated by analysis tools
2ff69d24c4 HHH-7872 - Improved L2 cache storage of "reference" data
d8d6d82e30 SVN layout migration for core/trunk
6a388b754 hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntry.java
-	// this should be refactored to instead expose a method to assemble a EntityEntry based on this
2ff69d24c hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StandardCacheEntryImpl.java
-	    // this should be refactored to instead expose a method to assemble a EntityEntry based on this
+	// this should be refactored to instead expose a method to assemble a EntityEntry based on this
+		// this should be refactored to instead expose a method to assemble a EntityEntry based on this
d8d6d82e3 code/core/src/main/java/org/hibernate/cache/entry/CacheEntry.java
+	    // this should be refactored to instead expose a method to assemble a EntityEntry based on this
*_*_*
// Do we need to drop constraints before dropping tables in this dialect?
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
d8d6d82e30 SVN layout migration for core/trunk
3a813dcbb hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Do we need to drop constraints before dropping tables in this dialect?
87e3f0fd2 hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
-		// Do we need to drop constraints before dropping tables in this dialect?
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Do we need to drop constraints before dropping tables in this dialect?
*_*_*
* FIXME Per the RegionFactory class Javadoc
d2c88d55df HHH-5647 - Develop release process using Gradle
c49ef2e2f2 [HHH-4487] Restore versions of the old public API jbc2 package classes
8f458e07e7 Add initial "clustered integration" test
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JndiMultiplexedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JndiSharedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/MultiplexedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/SharedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc/functional/util/TestJBossCacheRegionFactory.java
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
8f458e07e cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/TestJBossCacheRegionFactory.java
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
*_*_*
// Does this dialect support check constraints?
5fc70fc5ab HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
5fc70fc5a hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
-		// Does this dialect support check constraints?
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Does this dialect support check constraints?
*_*_*
// this is called by SessionFactory irregardless
92ad3eed80 HHH-6297 remove legacy cache api
d8d6d82e30 SVN layout migration for core/trunk
92ad3eed8 hibernate-core/src/main/java/org/hibernate/cache/internal/NoCacheProvider.java
-		// this is called by SessionFactory irregardless; we just disregard here;
-		// this is called by SessionFactory irregardless; we just disregard here;
d8d6d82e3 code/core/src/main/java/org/hibernate/cache/NoCacheProvider.java
+		// this is called by SessionFactory irregardless; we just disregard here;
+		// this is called by SessionFactory irregardless; we just disregard here;
*_*_*
// Does this dialect support the UNIQUE column syntax?
49c8a8e4f0 HHH-7797 Finished auditing dialects.  Cleanup and javadocs.  Completed uniqueness test.
d8d6d82e30 SVN layout migration for core/trunk
49c8a8e4f hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
-		// Does this dialect support the UNIQUE column syntax?
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Does this dialect support the UNIQUE column syntax?
*_*_*
// this is done here 'cos we might only know the type here (ugly!)
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
594f689d98 HHH-6371 - Develop metamodel binding creation using a push approach
819f8da9ea HHH-5672 - Develop the binding model (binding between logical and relational)
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+//		// this is done here 'cos we might only know the type here (ugly!)
-		// this is done here 'cos we might only know the type here (ugly!)
594f689d9 hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
-//		// this is done here 'cos we might only know the type here (ugly!)
819f8da9e hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
+//		// this is done here 'cos we might only know the type here (ugly!)
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+		// this is done here 'cos we might only know the type here (ugly!)
*_*_*
// Does this dialect support the FOR UPDATE syntax?
5fc70fc5ab HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
5fc70fc5a hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
-		// Does this dialect support the FOR UPDATE syntax?
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Does this dialect support the FOR UPDATE syntax?
*_*_*
// todo : what is the implication of this?
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
594f689d98 HHH-6371 - Develop metamodel binding creation using a push approach
819f8da9ea HHH-5672 - Develop the binding model (binding between logical and relational)
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+				// todo : what is the implication of this?
-				// todo : what is the implication of this?
594f689d9 hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityProcessor.java
-//				// todo : what is the implication of this?
819f8da9e hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityBinder.java
+//				// todo : what is the implication of this?
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+				// todo : what is the implication of this?
*_*_*
// todo : YUCK!!!
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
8a5415d367 HHH-6359 : Integrate new metamodel into entity tuplizers
d8d6d82e30 SVN layout migration for core/trunk
66ce8b7fb hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
-			// todo : YUCK!!!
8a5415d36 hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+			// todo : YUCK!!!
d8d6d82e3 code/core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+			// todo : YUCK!!!
*_*_*
how *should* this work for non-pojo entities?
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
*_*_*
// Does this dialect support FOR UPDATE OF
5fc70fc5ab HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
5fc70fc5a hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
-		// Does this dialect support FOR UPDATE OF, allowing particular rows to be locked?
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Does this dialect support FOR UPDATE OF, allowing particular rows to be locked?
*_*_*
//TODO: really bad
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-			if ( !node.getName().equals("many-to-many") ) { //TODO: really bad, evil hack to fix!!!
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+			if ( !node.getName().equals("many-to-many") ) { //TODO: really bad, evil hack to fix!!!
*_*_*
//TODO: is it kosher to do it here?
# shas =  1
*_*_*
//TODO: better to degrade to lazy="false" if uninstrumented
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
4968ad11fb HHH-6447 - Develop shared binding creation approach
997dd00880 HHH-6168 : Create an AttributeBinding for many-to-one and implement DomainState and RelationalState for HBM XML
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-			//TODO: better to degrade to lazy="false" if uninstrumented
4968ad11f hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmManyToOneAttributeBindingState.java
-		//TODO: better to degrade to lazy="false" if uninstrumented
997dd0088 hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/domain/HbmManyToOneAttributeDomainState.java
+		//TODO: better to degrade to lazy="false" if uninstrumented
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+			//TODO: better to degrade to lazy="false" if uninstrumented
*_*_*
// Does this dialect support the Oracle-style FOR UPDATE NOWAIT syntax?
5fc70fc5ab HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
5fc70fc5a hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
-		// Does this dialect support the Oracle-style FOR UPDATE NOWAIT syntax?
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Does this dialect support the Oracle-style FOR UPDATE NOWAIT syntax?
*_*_*
* FIXME Per the RegionFactory class Javadoc
d2c88d55df HHH-5647 - Develop release process using Gradle
c49ef2e2f2 [HHH-4487] Restore versions of the old public API jbc2 package classes
8f458e07e7 Add initial "clustered integration" test
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JndiMultiplexedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JndiSharedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/MultiplexedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/SharedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc/functional/util/TestJBossCacheRegionFactory.java
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
-     * FIXME Per the RegionFactory class Javadoc, this constructor version
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
8f458e07e cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/TestJBossCacheRegionFactory.java
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
*_*_*
//Icky workaround for MySQL bug:
377c300071 HHH-8162 Make unique constraint handling on schema update configurable
d8d6d82e30 SVN layout migration for core/trunk
377c30007 hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
-											//Icky workaround for MySQL bug:
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/Configuration.java
+											//Icky workaround for MySQL bug:
*_*_*
// we have to set up the table later!! yuck
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-			// we have to set up the table later!! yuck
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+			// we have to set up the table later!! yuck
*_*_*
//TODO: Somehow add the newly created foreign keys to the internal collection
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
-		//TODO: Somehow add the newly created foreign keys to the internal collection
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/Configuration.java
+		//TODO: Somehow add the newly created foreign keys to the internal collection
*_*_*
// This inner class implements a case statement....perhaps im being a bit over-clever here
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-	// This inner class implements a case statement....perhaps im being a bit over-clever here
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+	// This inner class implements a case statement....perhaps im being a bit over-clever here
*_*_*
//TODO: merge into one method!
# shas =  1
*_*_*
// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-		// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+		// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
*_*_*
// TODO: what type?
fbae6db0ab HHH-3414 : fetch profiles, phase 1 : join fetching
d8d6d82e30 SVN layout migration for core/trunk
fbae6db0a core/src/main/java/org/hibernate/impl/SessionImpl.java
-			throw new IllegalArgumentException("Invalid filter-parameter name format"); // TODO: what type?
d8d6d82e3 code/core/src/main/java/org/hibernate/impl/SessionImpl.java
+			throw new IllegalArgumentException("Invalid filter-parameter name format"); // TODO: what type?
*_*_*
//TODO: inefficient
18079f346d HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC) - Initial reworking to remove SQL references (for reuse in Search, OGM, etc) and to split out conceptual "from clause" and "select clause" into different structures (see QuerySpaces)
a102bf2c31 HHH-7841 - Redesign Loader
d8d6d82e30 SVN layout migration for core/trunk
18079f346 hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationKey.java
-		return table.hashCode(); //TODO: inefficient
a102bf2c3 hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationKey.java
+		return table.hashCode(); //TODO: inefficient
d8d6d82e3 code/core/src/main/java/org/hibernate/action/EntityUpdateAction.java
d8d6d82e3 code/core/src/main/java/org/hibernate/loader/JoinWalker.java
+				//TODO: inefficient if that cache is just going to ignore the updated state!
+			return table.hashCode(); //TODO: inefficient
*_*_*
//FIXME: get the PersistentClass
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ResultSetMappingBinder.java
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
+				//FIXME: get the PersistentClass
-		//FIXME: get the PersistentClass
-		//FIXME: get the PersistentClass
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
+		//FIXME: get the PersistentClass
+		//FIXME: get the PersistentClass
*_*_*
// todo : we can remove this once the deprecated ctor can be made private...
a6ca833e2e HHH-3392 : query cache cluster replication with ResultTransformers
1851bffce7 HHH-3409 : ResultTransformer uniqueing
a6ca833e2 core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
1851bffce core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
*_*_*
//TODO: this is too conservative
# shas =  1
*_*_*
//TODO: redesign how PropertyAccessors are acquired...
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
8a5415d367 HHH-6359 : Integrate new metamodel into entity tuplizers
d8d6d82e30 SVN layout migration for core/trunk
66ce8b7fb hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
-			//TODO: redesign how PropertyAccessors are acquired...
8a5415d36 hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+			//TODO: redesign how PropertyAccessors are acquired...
d8d6d82e3 code/core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+			//TODO: redesign how PropertyAccessors are acquired...
*_*_*
// TODO : not so sure this is needed...
# shas =  1
*_*_*
// todo : we can remove this once the deprecated ctor can be made private...
a6ca833e2e HHH-3392 : query cache cluster replication with ResultTransformers
1851bffce7 HHH-3409 : ResultTransformer uniqueing
a6ca833e2 core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
1851bffce core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
*_*_*
//create an index on the key columns??
# shas =  1
*_*_*
/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-			/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+			/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
*_*_*
//TODO: improve this hack!
f4c36a10f8 HHH-6439: Added getAddUniqueConstraintString method to Dialect and updated UniqueKey classes to use it
53e1a37adf HHH-6431 : Add Exportable.sqlCreateStrings() and sqlDropStrings() and implementations
d8d6d82e30 SVN layout migration for core/trunk
f4c36a10f hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
f4c36a10f hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java
-				//TODO: improve this hack!
-				//TODO: improve this hack!
53e1a37ad hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java
+				//TODO: improve this hack!
d8d6d82e3 code/core/src/main/java/org/hibernate/mapping/UniqueKey.java
+				//TODO: improve this hack!
*_*_*
cos it depends upon ordering of mapping doc
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-		//TODO: bad implementation, cos it depends upon ordering of mapping doc
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+		//TODO: bad implementation, cos it depends upon ordering of mapping doc
*_*_*
//use of trim() here is ugly?
c46daa4cf0 HHH-7440, HHH-7368, HHH-7369, HHH-7370 - Redesign dialect-specific LIMIT clause appliance
d8d6d82e30 SVN layout migration for core/trunk
c46daa4cf hibernate-core/src/main/java/org/hibernate/loader/Loader.java
-					sql.trim(), //use of trim() here is ugly?
d8d6d82e3 code/core/src/main/java/org/hibernate/loader/Loader.java
+					sql.trim(), //use of trim() here is ugly?
*_*_*
//TODO: ideally we need the construction of PropertyAccessor to take the following:
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
d8d6d82e30 SVN layout migration for core/trunk
9e063ffa2 hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
-	//TODO: ideally we need the construction of PropertyAccessor to take the following:
d8d6d82e3 code/core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+	//TODO: ideally we need the construction of PropertyAccessor to take the following:
*_*_*
// can happen because of the multiple ways Cache.remove()
d2c88d55df HHH-5647 - Develop release process using Gradle
640d95bbf6 HHH-4027 - Remove current cache-jbosscache module content
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
d8d6d82e30 SVN layout migration for core/trunk
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/util/DataVersionAdapter.java
-            // can happen because of the multiple ways Cache.remove()
640d95bbf cache-jbosscache/src/main/java/org/hibernate/cache/OptimisticTreeCache.java
-				// can happen because of the multiple ways Cache.remove()
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/DataVersionAdapter.java
+            // can happen because of the multiple ways Cache.remove()
d8d6d82e3 code/cache-jbosscache/src/main/java/org/hibernate/cache/OptimisticTreeCache.java
+				// can happen because of the multiple ways Cache.remove()
*_*_*
//ick!
59ec451c28 break hibernate-commons-annotations back out into separate project
82e5fa8c78 Remove dependency on core
a51b033dba HHH-3549 : import commons-annotations into core
354714caca HHH-3549 : import commons-annotations into core
d8d6d82e30 SVN layout migration for core/trunk
59ec451c2 commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
-			return result + "x"; //ick!
82e5fa8c7 commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
+			return result + "x"; //ick!
a51b033db commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
-			return result + "x"; //ick!
354714cac commons-annotations/src/java/org/hibernate/annotations/common/util/StringHelper.java
+			return result + "x"; //ick!
d8d6d82e3 code/core/src/main/java/org/hibernate/util/StringHelper.java
+			return result + "x"; //ick!
*_*_*
// todo : we can remove this once the deprecated ctor can be made private...
a6ca833e2e HHH-3392 : query cache cluster replication with ResultTransformers
1851bffce7 HHH-3409 : ResultTransformer uniqueing
a6ca833e2 core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
1851bffce core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
*_*_*
// TODO: this is very suboptimal for some subclasses (namely components)
# shas =  1
*_*_*
//TODO: should this be Session.instantiate(Persister
af03365c86 HHH-8573: Persister is taken according to the actual type of the replaced object
b185946c81 DefaultMergeEventListener does not call Interceptor.instantiate() for a new persistent entity (Francesco Degrassi)
99e7f895f9 HHH-3229 : Cascade merge transient entities regardless of property traversal order
d8d6d82e30 SVN layout migration for core/trunk
af03365c8 hibernate-core/src/main/java/org/hibernate/type/EntityType.java
-				//TODO: should this be Session.instantiate(Persister, ...)?
b185946c8 core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
-			//TODO: should this be Session.instantiate(Persister, ...)?
99e7f895f core/src/main/java/org/hibernate/type/EntityType.java
+			//TODO: should this be Session.instantiate(Persister, ...)?
-		final Object copy = persister.instantiate( id, source.getEntityMode() );  //TODO: should this be Session.instantiate(Persister, ...)?
+				//TODO: should this be Session.instantiate(Persister, ...)?
d8d6d82e3 code/core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
+		final Object copy = persister.instantiate( id, source.getEntityMode() );  //TODO: should this be Session.instantiate(Persister, ...)?
*_*_*
//TODO: suck this into initLaziness!
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-		//TODO: suck this into initLaziness!
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+		//TODO: suck this into initLaziness!
*_*_*
// todo : we can remove this once the deprecated ctor can be made private...
a6ca833e2e HHH-3392 : query cache cluster replication with ResultTransformers
1851bffce7 HHH-3409 : ResultTransformer uniqueing
a6ca833e2 core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
1851bffce core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
*_*_*
// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
91d444423b HHH-5765 : Wire in dialect factory and resolvers from service registry
0bfe7869e4 HHH-5638 HHH-5639 HHH-5640 : Import DialectFactory. DialectResolver, ConnectionProvider, and JDBC batching services
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/engine/jdbc/env/internal/JdbcEnvironmentInitiator.java
9caca0ce3 hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
+		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
-		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
91d444423 hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
-		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
0bfe7869e core/src/main/java/org/hibernate/service/jdbc/internal/JdbcServicesImpl.java
+		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
*_*_*
//TODO: is this right??
dc7cdf9d88 HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
4968ad11fb HHH-6447 - Develop shared binding creation approach
eb414295aa HHH-6134 : Migrate processing hbm.xml files to use Jaxb-generated classes
d8d6d82e30 SVN layout migration for core/trunk
dc7cdf9d8 hibernate-core/src/main/java/org/hibernate/type/AnyType.java
-		throw new UnsupportedOperationException(); //TODO: is this right??
4968ad11f hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmSimpleAttributeBindingState.java
-		this.isInsertable = true; //TODO: is this right????
eb414295a hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/domain/HbmSimpleAttributeDomainState.java
+		this.isInsertable = true; //TODO: is this right????
d8d6d82e3 code/core/src/main/java/org/hibernate/type/AnyType.java
+		throw new UnsupportedOperationException(); //TODO: is this right??
*_*_*
//ie. the subquery! yuck!
# shas =  1
*_*_*
//use of a stringbuffer to workaround a JDK bug
7337743c93 HHH-3550 : import annotations into core
d8d6d82e30 SVN layout migration for core/trunk
7337743c9 annotations/src/java/org/hibernate/cfg/DefaultComponentSafeNamingStrategy.java
7337743c9 annotations/src/java/org/hibernate/cfg/EJB3NamingStrategy.java
7337743c9 annotations/src/test/org/hibernate/test/annotations/AlternativeNamingStrategy.java
+			//use of a stringbuffer to workaround a JDK bug
+			//use of a stringbuffer to workaround a JDK bug
+			//use of a stringbuffer to workaround a JDK bug
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/DefaultNamingStrategy.java
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/ImprovedNamingStrategy.java
+			//use of a stringbuffer to workaround a JDK bug
+			//use of a stringbuffer to workaround a JDK bug
*_*_*
//TODO: copy/paste from recreate()
129c0f1348 HHH-6732 more logging trace statements are missing guards against unneeded string creation
d8d6d82e30 SVN layout migration for core/trunk
129c0f134 hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
-							//TODO: copy/paste from recreate()
d8d6d82e3 code/core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+							//TODO: copy/paste from recreate()
*_*_*
for backward compatibility of sets with no
# shas =  1
*_*_*
// todo : this eventually needs to be removed
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
d8d6d82e30 SVN layout migration for core/trunk
9e063ffa2 hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
-	// todo : this eventually needs to be removed
-	// todo : this eventually needs to be removed
d8d6d82e3 code/core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+	// todo : this eventually needs to be removed
+	// todo : this eventually needs to be removed
*_*_*
// todo : remove
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
35edd56907 HHH-8839 : Eager Map with entity key causes IllegalStateException: Collection element (many-to-many) table alias cannot be empty
8e2f2a9da6 HHH-8597 : Delete org.hibernate.loader.plan2 and related code
af1061a42d HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
dc7cdf9d88 HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
a7179fbc49 HHH-6371 - Develop metamodel binding creation using a push approach
baeb6dc400 HHH-6291 - Basic MetadataImpl redesign
1a40b0232f HHH-6117 - Figure out best way to handle SessionFactoryObjectFactory dealing with JNDI
20a120ef6c HHH-5942 - Migrate to JUnit 4
fd3fb8b316 HHH-5942 - Migrate to JUnit 4
21cc90fbf4 HHH-5985 - Remove TransactionHelper in preference of IsolationDelegate
03c004bd13 HHH-5632 - Import initial services work
d8d6d82e30 SVN layout migration for core/trunk
66ce8b7fb hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
66ce8b7fb hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
-	// todo : remove this once the state objects are cleaned up
-		// todo : remove this by coordinated ordering of entity processing
35edd5690 hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/LoadQueryJoinAndFetchProcessor.java
-			// todo : remove this assumption ^^; maybe we make CollectionQuerySpace "special" and rather than have it
8e2f2a9da hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
-	// todo : remove these
af1061a42 hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/LoadQueryJoinAndFetchProcessor.java
+			// todo : remove this assumption ^^
dc7cdf9d8 hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
+	// todo : remove these
a7179fbc4 hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/Helper.java
+	// todo : remove this once the state objects are cleaned up
baeb6dc40 hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+		// todo : remove this by coordinated ordering of entity processing
1a40b0232 hibernate-core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
-	// todo : remove these once we get the services in place and integrated into the SessionFactory
20a120ef6 hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
-		// todo : remove.  this is legacy.  convert usages to configuration()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
fd3fb8b31 hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+		// todo : remove.  this is legacy.  convert usages to configuration()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
21cc90fbf hibernate-core/src/main/java/org/hibernate/engine/TransactionHelper.java
-	// todo : remove this and just have subclasses use IsolationDelegate directly...
03c004bd1 core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
+	// todo : remove these once we get the services in place and integrated into the SessionFactory
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/TransactionHelper.java
d8d6d82e3 code/core/src/main/java/org/hibernate/impl/SessionImpl.java
d8d6d82e3 code/core/src/main/java/org/hibernate/mapping/Component.java
d8d6d82e3 code/core/src/main/java/org/hibernate/mapping/Property.java
d8d6d82e3 code/core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+	// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...
+		// todo : remove
+		// todo : remove this once ComponentMetamodel is complete and merged
+	// todo : remove
+	// todo : remove
+	// todo : remove
+		// todo : remove the identityInsert param and variations;
*_*_*
//just to help out during the load (ugly
# shas =  1
*_*_*
// hack/workaround as sqlquery impl depend on having a key.
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
-			alias = "alias_" + elementCount; // hack/workaround as sqlquery impl depend on having a key.
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
+			alias = "alias_" + elementCount; // hack/workaround as sqlquery impl depend on having a key.
*_*_*
//TODO: handle the case of a foreign key to something other than the pk
# shas =  1
*_*_*
//TODO: assumes all collections disassemble to an array!
# shas =  1
*_*_*
// todo : would love to have this work on a notification basis
3ecbfeb2b2 HHH-5375 - Merge AnnotationConfiguration into Configuration
d8d6d82e30 SVN layout migration for core/trunk
3ecbfeb2b core/src/main/java/org/hibernate/cfg/Configuration.java
-		// todo : would love to have this work on a notification basis
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/Configuration.java
+		// todo : would love to have this work on a notification basis
*_*_*
// TODO: is it really neceassry to provide Configuration to CollectionPersisters ?
996d567731 HHH-6214 Converting RegionFactory to a Service
ddfcc44d76 HHH-5916 - Add support for a programmatic way to define a default EntityPersister and CollectionPersister class implementation
d8d6d82e30 SVN layout migration for core/trunk
996d56773 hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
-	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ?
ddfcc44d7 hibernate-core/src/main/java/org/hibernate/persister/PersisterFactory.java
ddfcc44d7 hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
-	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ? Should it not be enough with associated class ?
+	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ?
d8d6d82e3 code/core/src/main/java/org/hibernate/persister/PersisterFactory.java
+	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ? Should it not be enough with associated class ?
*_*_*
//TODO: deprecated
33074dc2dc HHH-6069 - Tests moved
d7cc102b00 HHH-6069 - Escape entity fields name
d8d6d82e30 SVN layout migration for core/trunk
33074dc2d hibernate-core/src/main/java/org/hibernate/mapping/Column.java
+			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually
d7cc102b0 hibernate-core/src/main/java/org/hibernate/mapping/Column.java
-			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually
d8d6d82e3 code/core/src/main/java/org/hibernate/mapping/Column.java
+			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually
*_*_*
//TODO possibly relax that
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
aa7f7a7822 HHH-3439 : Mappings
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
-				//TODO possibly relax that
aa7f7a782 core/src/main/java/org/hibernate/cfg/Configuration.java
aa7f7a782 core/src/main/java/org/hibernate/cfg/Mappings.java
+				//TODO possibly relax that
-			//TODO possibly relax that
-			//TODO possibly relax that
-			//TODO possibly relax that
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/Mappings.java
+			//TODO possibly relax that
+			//TODO possibly relax that
+			//TODO possibly relax that
*_*_*
come up with a better way to check this (plus see above comment)
# shas =  1
*_*_*
//use a degenerated strategy for backward compatibility
# shas =  1
*_*_*
//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
1d26ac1e12 HHH-6360 : Build basic properties from an AttributeBinding
d8d6d82e30 SVN layout migration for core/trunk
9e063ffa2 hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
-		//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
66ce8b7fb hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
-		//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
1d26ac1e1 hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+		//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
d8d6d82e3 code/core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+		//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
*_*_*
// todo : better to handle dynamic filters through implicit DynamicFilterParameterSpecification
6cabc326b8 HHH-8312 - named parameters binding are not correct when used within subquery
d8d6d82e30 SVN layout migration for core/trunk
6cabc326b hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
-		// todo : better to handle dynamic filters through implicit DynamicFilterParameterSpecification
d8d6d82e3 code/core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
+		// todo : better to handle dynamic filters through implicit DynamicFilterParameterSpecification
*_*_*
// get the right object from the list ... would it be easier to just call getEntity() ??
06b0faaf57 HHH-7746 - Investigate alternative batch loading algorithms
d8d6d82e30 SVN layout migration for core/trunk
06b0faaf5 hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
-		// get the right object from the list ... would it be easier to just call getEntity() ??
d8d6d82e3 code/core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
+		// get the right object from the list ... would it be easier to just call getEntity() ??
*_*_*
// TODO: should "record" how many properties we have reffered to - and if we
# shas =  1
*_*_*
//TODO: this dependency is kinda Bad
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
d8d6d82e30 SVN layout migration for core/trunk
9e063ffa2 hibernate-core/src/main/java/org/hibernate/internal/util/ReflectHelper.java
-	//TODO: this dependency is kinda Bad
d8d6d82e3 code/core/src/main/java/org/hibernate/util/ReflectHelper.java
+	//TODO: this dependency is kinda Bad
*_*_*
//TODO: get SQL rendering out of this package!
8c28ba8463 HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/BetweenExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/IlikeExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/NotNullExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/NullExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/PropertyExpression.java
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
*_*_*
// why does this matter?
8e2f2a9da6 HHH-8597 : Delete org.hibernate.loader.plan2 and related code
b3791bc3c3 HHH-7841 : Redesign Loader
d8d6d82e30 SVN layout migration for core/trunk
8e2f2a9da hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
-			if ( association.getJoinType() == JoinType.LEFT_OUTER_JOIN ) { // why does this matter?
b3791bc3c hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
+			if ( oj.getJoinType() == JoinType.LEFT_OUTER_JOIN ) { // why does this matter?
d8d6d82e3 code/core/src/main/java/org/hibernate/loader/JoinWalker.java
+			if ( oj.getJoinType() == JoinFragment.LEFT_OUTER_JOIN ) { // why does this matter?
*_*_*
to account for newly saved entities in query
# shas =  1
*_*_*
//TODO: get SQL rendering out of this package!
8c28ba8463 HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/BetweenExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/IlikeExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/NotNullExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/NullExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/PropertyExpression.java
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
*_*_*
//TODO: can we *always* use the "null property" approach for everything?
# shas =  1
*_*_*
// does this need holdlock also? : return tableName + " with (updlock
655be65063 HHH-4546 - add JPA 2.0 locking.
d8d6d82e30 SVN layout migration for core/trunk
655be6506 core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
-			// does this need holdlock also? : return tableName + " with (updlock, rowlock, holdlock)";
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
+			// does this need holdlock also? : return tableName + " with (updlock, rowlock, holdlock)";
*_*_*
//TODO: or we could do this polymorphically
# shas =  1
*_*_*
// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...
08d9fe2117 HHH-5949 - Migrate, complete and integrate TransactionFactory as a service
d8d6d82e30 SVN layout migration for core/trunk
08d9fe211 hibernate-core/src/main/java/org/hibernate/engine/TransactionHelper.java
-	// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/TransactionHelper.java
+	// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...
*_*_*
at least needed this dropped after use
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
638b43f04b HHH-3712 - Reorganize the Sybase dialect class hierarchy, add SybaseASE15Dialect, and mark SybaseDialect as deprecated
d8d6d82e30 SVN layout migration for core/trunk
3a813dcbb hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
+				// sql-server, at least needed this dropped after use; strange!
87e3f0fd2 hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
-				// sql-server, at least needed this dropped after use; strange!
638b43f04 core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
638b43f04 core/src/main/java/org/hibernate/dialect/SybaseDialect.java
+		return true;  // sql-server, at least needed this dropped after use; strange!
-		return true;  // sql-server, at least needed this dropped after use; strange!
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/SybaseDialect.java
+		return true;  // sql-server, at least needed this dropped after use; strange!
*_*_*
// is it necessary to register %exact since it can only appear in a where clause?
# shas =  1
*_*_*
// Does this dialect support the ALTER TABLE syntax?
# shas =  1
*_*_*
// TODO: shift it into unsaved-value strategy
# shas =  1
*_*_*
//TODO: reenable if we also fix the above todo
# shas =  1
*_*_*
// orphans should not be deleted during copy??
3402ba3a67 HHH-6028 - Remove o.h.classic.Session/Validatable
d8d6d82e30 SVN layout migration for core/trunk
3402ba3a6 hibernate-core/src/main/java/org/hibernate/engine/CascadingAction.java
-			// orphans should not be deleted during copy??
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/CascadingAction.java
+			// orphans should not be deleted during copy??
*_*_*
// orphans should not be deleted during merge??
6b5a428b3f HHH-7527 - OSGI manifests for hibernate-orm : clean up org.hibernate.engine.spi package duplication between hem and core
d8d6d82e30 SVN layout migration for core/trunk
6b5a428b3 hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingAction.java
6b5a428b3 hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingActions.java
-			// orphans should not be deleted during merge??
+			// orphans should not be deleted during merge??
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/CascadingAction.java
+			// orphans should not be deleted during merge??
*_*_*
//TODO: suck this logic into the collection!
# shas =  1
*_*_*
// todo : we can actually just determine this from the incoming EntityEntry-s
# shas =  1
*_*_*
//TODO: this bit actually has to be called after all cascades!
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
d8d6d82e30 SVN layout migration for core/trunk
3a813dcbb hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
+		//TODO: this bit actually has to be called after all cascades!
87e3f0fd2 hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
-		//TODO: this bit actually has to be called after all cascades!
d8d6d82e3 code/core/src/main/java/org/hibernate/action/EntityIdentityInsertAction.java
+		//TODO: this bit actually has to be called after all cascades!
*_*_*
// this class has no proxies (so do a shortcut)
f74c5a7fa5 HHH-2879 Apply hibernate code templates and formatting
eecee618c6 HHH-2879 Add ResolveNaturalId event, listener and entity persister api
d8d6d82e30 SVN layout migration for core/trunk
f74c5a7fa hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
-//        // this class has no proxies (so do a shortcut)
eecee618c hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
+//        // this class has no proxies (so do a shortcut)
d8d6d82e3 code/core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
+			// this class has no proxies (so do a shortcut)
*_*_*
//TODO: Should this be an InitializeEntityEventListener??? (watch out for performance!)
3800a0e695 HHH-7206 - Manage natural-id synchronization without flushing
d8d6d82e30 SVN layout migration for core/trunk
3800a0e69 hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
-		//TODO: Should this be an InitializeEntityEventListener??? (watch out for performance!)
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
+		//TODO: Should this be an InitializeEntityEventListener??? (watch out for performance!)
*_*_*
// todo : need map? the prob is a proper key
# shas =  1
*_*_*
//TODO: better to pass the id in as an argument?
# shas =  1
*_*_*
//do we even really need this? the update will fail anyway....
# shas =  1
*_*_*
//TODO: would it be better to do a refresh from db?
# shas =  1
*_*_*
* This form used from annotations (?). Essentially the same as the above using a
3edb72db48 HHH-7387 - Integrate Draft 6 of the JPA 2.1 spec : addNamedQuery support
d8d6d82e30 SVN layout migration for core/trunk
3edb72db4 hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinition.java
-	 * This form used from annotations (?).  Essentially the same as the above using a
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/NamedSQLQueryDefinition.java
+	 * This form used from annotations (?).  Essentially the same as the above using a
*_*_*
//TODO: ugly here:
# shas =  1
*_*_*
//TODO: not quite sure about the full implications of this!
9938937fe7 HHH-8637 - Downcasting with TREAT operator should also filter results by the specified Type
d8d6d82e30 SVN layout migration for core/trunk
9938937fe hibernate-core/src/main/java/org/hibernate/engine/internal/JoinSequence.java
-				//TODO: not quite sure about the full implications of this!
-			//TODO: not quite sure about the full implications of this!
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/JoinSequence.java
+			if (includeExtraJoins) { //TODO: not quite sure about the full implications of this!
+			if (includeExtraJoins) { //TODO: not quite sure about the full implications of this!
*_*_*
// this only works because collection entries are kept in a sequenced
7cecc68fb1 HHH-1775
d8d6d82e30 SVN layout migration for core/trunk
7cecc68fb hibernate-core/src/main/java/org/hibernate/engine/spi/BatchFetchQueue.java
-		// this only works because collection entries are kept in a sequenced
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/BatchFetchQueue.java
+		// this only works because collection entries are kept in a sequenced
*_*_*
// todo : i'd much rather have this done from #endLoadingCollection(CollectionPersister
# shas =  1
*_*_*
// NOTE : here we cleanup the load context when we have no more local
# shas =  1
*_*_*
// todo : add the notion of enabled filters to the CacheKey to differentiate filtered collections from non-filtered
e5f4b616d4 HHH-9840 Checkstyle fixes
d8d6d82e30 SVN layout migration for core/trunk
e5f4b616d hibernate-core/src/main/java/org/hibernate/engine/loading/internal/CollectionLoadContext.java
-			// todo : add the notion of enabled filters to the CacheKey to differentiate filtered collections from non-filtered;
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/loading/CollectionLoadContext.java
+			// todo : add the notion of enabled filters to the CacheKey to differentiate filtered collections from non-filtered;
*_*_*
//TODO: reuse the PostLoadEvent...
f74c5a7fa5 HHH-2879 Apply hibernate code templates and formatting
eecee618c6 HHH-2879 Add ResolveNaturalId event, listener and entity persister api
d8d6d82e30 SVN layout migration for core/trunk
f74c5a7fa hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
-//		//TODO: reuse the PostLoadEvent...
eecee618c hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
+//		//TODO: reuse the PostLoadEvent...
d8d6d82e3 code/core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
+		//TODO: reuse the PostLoadEvent...
*_*_*
// todo : should we check the current isolation mode explicitly?
# shas =  1
*_*_*
//TODO: or an array!! we can't lock objects with arrays now??
# shas =  1
*_*_*
// TODO: iteratively get transient entities and retry merge until one of the following conditions:
e11e9631c7 HHH-5472 : Delay saving an entity if it does not cascade the save to non-nullable transient entities
710d983cd6 HHH-3810 : Transient entities can be inserted twice on merge
e11e9631c hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
-		// TODO: iteratively get transient entities and retry merge until one of the following conditions:
710d983cd core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
+		// TODO: iteratively get transient entities and retry merge until one of the following conditions:
*_*_*
// this will also (inefficiently) handle arrays
# shas =  1
*_*_*
// TODO : perhaps we should additionally require that the incoming entity
# shas =  1
*_*_*
* TODO: This will need to be refactored at some point.
241868e1dd HHH-8741 - More checkstyle cleanups
d8d6d82e30 SVN layout migration for core/trunk
241868e1d hibernate-core/src/main/java/org/hibernate/hql/internal/QuerySplitter.java
- * TODO: This will need to be refactored at some point.
d8d6d82e3 code/core/src/main/java/org/hibernate/hql/QuerySplitter.java
+ * TODO: This will need to be refactored at some point.
*_*_*
// TODO : most of below was taken verbatim from DotNode
# shas =  1
*_*_*
// todo : currently expects that the individual with expressions apply to the same sql table join
a9b1425f3f Replaced references to slf4j with references to new jboss.logging.Logger implementations and i18n'd where it was clear how to do so.
d8d6d82e30 SVN layout migration for core/trunk
a9b1425f3 hibernate-core/src/main/java/org/hibernate/hql/ast/HqlSqlWalker.java
-			// todo : currently expects that the individual with expressions apply to the same sql table join.
d8d6d82e3 code/core/src/main/java/org/hibernate/hql/ast/HqlSqlWalker.java
+			// todo : currently expects that the individual with expressions apply to the same sql table join.
*_*_*
// No explicit select expression
# shas =  1
*_*_*
// TODO : better way?!?
# shas =  1
*_*_*
// Attempt to work around "ghost" ImpliedFromElements that occasionally
# shas =  1
*_*_*
// Not possible to simply re-use the versionPropertyNode here as it causes
# shas =  1
*_*_*
// TODO: Downcast to avoid using an interface? Yuck.
77fba4df70 HHH-5173 - hql - average returns double but looses the decimal part
d8d6d82e30 SVN layout migration for core/trunk
77fba4df7 core/src/main/java/org/hibernate/hql/ast/SqlGenerator.java
-			FunctionArguments functionArguments = ( FunctionArguments ) writer;   // TODO: Downcast to avoid using an interface?  Yuck.
d8d6d82e3 code/core/src/main/java/org/hibernate/hql/ast/SqlGenerator.java
+			FunctionArguments functionArguments = ( FunctionArguments ) writer;   // TODO: Downcast to avoid using an interface?  Yuck.
*_*_*
// at some point the generate phase needs to be moved out of here
# shas =  1
*_*_*
//TODO:this is only needed during compilation .. can we eliminate the instvar?
# shas =  1
*_*_*
// TODO : absolutely no usages of this constructor form
fff88d2182 HHH-3908 - Expose way to fully control fetching and result mapping on SQLQuery
d8d6d82e30 SVN layout migration for core/trunk
fff88d218 core/src/main/java/org/hibernate/impl/SQLQueryImpl.java
-		// TODO : absolutely no usages of this constructor form; can it go away?
d8d6d82e3 code/core/src/main/java/org/hibernate/impl/SQLQueryImpl.java
+		// TODO : absolutely no usages of this constructor form; can it go away?
*_*_*
//TODO: is this a bit ugly?
3cdc447654 HHH-10125 - KEY() function in HQL causes inaccurate SQL when map key is an entity; HHH-10132 - ENTRY() function in HQL causes invalid SQL when map key is an entity
d8d6d82e30 SVN layout migration for core/trunk
3cdc44765 hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/SelectClause.java
-				//TODO: is this a bit ugly?
d8d6d82e3 code/core/src/main/java/org/hibernate/hql/ast/tree/SelectClause.java
+				//TODO: is this a bit ugly?
*_*_*
if only properties mapped to the "base table" are referenced
# shas =  1
*_*_*
// TODO : remove these last two as batcher is no longer managing connections
97fef96b98 HHH-5765 : Integrate LogicalConnection into ConnectionManager
d8d6d82e30 SVN layout migration for core/trunk
97fef96b9 hibernate-core/src/main/java/org/hibernate/jdbc/Batcher.java
-	// TODO : remove these last two as batcher is no longer managing connections
d8d6d82e3 code/core/src/main/java/org/hibernate/jdbc/Batcher.java
+	// TODO : remove these last two as batcher is no longer managing connections
*_*_*
// VERY IMPORTANT!!!! - This class needs to be free of any static references
19791a6c7d HHH-6026 - Migrate bytecode provider integrations to api/spi/internal split
d8d6d82e30 SVN layout migration for core/trunk
19791a6c7 hibernate-core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java
-	// VERY IMPORTANT!!!! - This class needs to be free of any static references
d8d6d82e3 code/core/src/main/java/org/hibernate/intercept/FieldInterceptionHelper.java
+	// VERY IMPORTANT!!!! - This class needs to be free of any static references
*_*_*
// TODO : we really need to be able to deal with component paths here also
# shas =  1
*_*_*
// TODO : an optimization here would be to consider cascade deletes and not gen those delete statements
d48f393420 HHH-11262 - Bulk Operations attempt to create temporary tables, but user does not have permission to create table
3e69b7bd53 HHH-7725 - Make handling multi-table bulk HQL operations more pluggable
d8d6d82e30 SVN layout migration for core/trunk
d48f39342 hibernate-core/src/main/java/org/hibernate/hql/spi/id/cte/CteValuesListDeleteHandlerImpl.java
d48f39342 hibernate-core/src/main/java/org/hibernate/hql/spi/id/inline/AbstractInlineIdsDeleteHandlerImpl.java
+			// TODO : an optimization here would be to consider cascade deletes and not gen those delete statements;
+				// TODO : an optimization here would be to consider cascade deletes and not gen those delete statements;
3e69b7bd5 hibernate-core/src/main/java/org/hibernate/hql/internal/ast/exec/MultiTableDeleteExecutor.java
3e69b7bd5 hibernate-core/src/main/java/org/hibernate/hql/spi/TableBasedDeleteHandlerImpl.java
-			// TODO : an optimization here would be to consider cascade deletes and not gen those delete statements;
+			// TODO : an optimization here would be to consider cascade deletes and not gen those delete statements;
d8d6d82e3 code/core/src/main/java/org/hibernate/hql/ast/exec/MultiTableDeleteExecutor.java
+			// TODO : an optimization here would be to consider cascade deletes and not gen those delete statements;
*_*_*
//TODO: switch statements are always evil! We already had bugs because
# shas =  1
*_*_*
// really there are two situations where it should be ok to allow the insertion
# shas =  1
*_*_*
// implicit joins are always(?) ok to reuse
153c4e32ef HHH-9090 : HQL parser is trying to reuse parent implied join for subquery
a0663f0d6c HHH-9305 : HQL FromElement is not reused in some cases resulting in an additional join
153c4e32e hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
-		// implicit joins are always(?) ok to reuse
a0663f0d6 hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
+		// implicit joins are always(?) ok to reuse
*_*_*
//TODO: used to be exprNoParens! was this needed?
# shas =  1
*_*_*
// TODO : we may also want to check that the types here map to exactly one column/JDBC-type
# shas =  1
*_*_*
// we do not know either type
# shas =  1
*_*_*
// TODO: get SQL rendering out of here
# shas =  1
*_*_*
// We would probably refactor to have LogicParser (builds a tree of simple
# shas =  1
*_*_*
// short-circuit for performance...
d1515a2911 HHH-5126 : "in" expression and column-valued-input-parameter
0013a90d20 HHH-5126 : "in" expression and column-valued-input-parameter
d8d6d82e30 SVN layout migration for core/trunk
d1515a291 hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
-			// short-circuit for performance...
+			// short-circuit for performance when only 1 value and the
0013a90d2 hibernate-core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
-			// short-circuit for performance...
+			// short-circuit for performance when only 1 value and the
d8d6d82e3 code/core/src/main/java/org/hibernate/impl/AbstractQueryImpl.java
+			// short-circuit for performance...
*_*_*
// not absolutely necessary
# shas =  1
*_*_*
// try block is a hack around fact that currently tuplizers are not
# shas =  1
*_*_*
//TODO: this is one of the ugliest and most fragile pieces of code in Hibernate....
# shas =  1
*_*_*
//TODO: implement functionality
# shas =  1
*_*_*
//yuck!
8c28ba8463 HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/Example.java
-		if ( buf.length()==1 ) buf.append("1=1"); //yuck!
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/Example.java
d8d6d82e3 code/core/src/main/java/org/hibernate/hql/ast/tree/DotNode.java
d8d6d82e3 code/testsuite/src/test/java/org/hibernate/test/legacy/FooBarTest.java
+		if ( buf.length()==1 ) buf.append("1=1"); //yuck!
+		if ( isSizeProperty ) indexed = true; //yuck!
+		id = new FumTest("").fumKey("abc"); //yuck!!
*_*_*
the incoming property could not be found so we
# shas =  1
*_*_*
//TODO: make this a bit nicer
cb51ef1a64 HHH-5135 - "Ambiguous column" exception thrown with columns having the same name as a function registered with the dialect (e.g. to_date, floor)
538982e8e6 HHH-2802 : order-by mapping -> support for property names
d8d6d82e30 SVN layout migration for core/trunk
cb51ef1a6 core/src/main/java/org/hibernate/sql/Template.java
-		//TODO: make this a bit nicer
538982e8e core/src/main/java/org/hibernate/sql/Template.java
-		//TODO: make this a bit nicer
d8d6d82e3 code/core/src/main/java/org/hibernate/sql/Template.java
+		//TODO: make this a bit nicer
+		//TODO: make this a bit nicer
*_*_*
// yuck!
06f3515244 HHH-2857 : assume schema support in HSQLDialect
d8d6d82e30 SVN layout migration for core/trunk
06f351524 core/src/main/java/org/hibernate/dialect/HSQLDialect.java
-			// yuck! Perhaps we should think about a new dialect?  Especially
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/HSQLDialect.java
d8d6d82e3 code/core/src/main/java/org/hibernate/hql/ast/tree/AssignmentSpecification.java
+			// yuck! Perhaps we should think about a new dialect?  Especially
+		// yuck!
*_*_*
// todo : potentially look at optimizing these two arrays
3e5a8b6603 HHH-9701 - Develop "immutable EntityEntry" impl
5c4dacb83e HHH-9265 - Extract EntityEntry behind a factory + interface
d8d6d82e30 SVN layout migration for core/trunk
3e5a8b660 hibernate-core/src/main/java/org/hibernate/engine/internal/AbstractEntityEntry.java
3e5a8b660 hibernate-core/src/main/java/org/hibernate/engine/internal/MutableEntityEntry.java
+		// todo : potentially look at optimizing these two arrays
-		// todo : potentially look at optimizing these two arrays
5c4dacb83 hibernate-core/src/main/java/org/hibernate/engine/internal/MutableEntityEntry.java
5c4dacb83 hibernate-core/src/main/java/org/hibernate/engine/spi/EntityEntry.java
+		// todo : potentially look at optimizing these two arrays
-		// todo : potentially look at optimizing these two arrays
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/EntityEntry.java
+		// todo : potentially look at optimizing these two arrays
*_*_*
//note that i parameter is now unused (delete it?)
63093dbfd9 HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
63093dbfd hibernate-core/src/main/java/org/hibernate/collection/spi/PersistentCollection.java
-	public boolean entryExists(Object entry, int i); //note that i parameter is now unused (delete it?)
d8d6d82e3 code/core/src/main/java/org/hibernate/collection/PersistentCollection.java
+	public boolean entryExists(Object entry, int i); //note that i parameter is now unused (delete it?)
*_*_*
// TODO: Figure out a better way to get the FROM elements in a proper tree structure
# shas =  1
*_*_*
// TODO : this constructor form is *only* used from constructor directly below us
153eb4a913 HHH-7387 - Integrate Draft 6 of the JPA 2.1 spec : stored procedure queries
d8d6d82e30 SVN layout migration for core/trunk
153eb4a91 hibernate-core/src/main/java/org/hibernate/internal/SQLQueryImpl.java
d8d6d82e3 code/core/src/main/java/org/hibernate/impl/SQLQueryImpl.java
*_*_*
// todo : remove
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
35edd56907 HHH-8839 : Eager Map with entity key causes IllegalStateException: Collection element (many-to-many) table alias cannot be empty
8e2f2a9da6 HHH-8597 : Delete org.hibernate.loader.plan2 and related code
af1061a42d HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
dc7cdf9d88 HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
a7179fbc49 HHH-6371 - Develop metamodel binding creation using a push approach
baeb6dc400 HHH-6291 - Basic MetadataImpl redesign
1a40b0232f HHH-6117 - Figure out best way to handle SessionFactoryObjectFactory dealing with JNDI
20a120ef6c HHH-5942 - Migrate to JUnit 4
fd3fb8b316 HHH-5942 - Migrate to JUnit 4
21cc90fbf4 HHH-5985 - Remove TransactionHelper in preference of IsolationDelegate
03c004bd13 HHH-5632 - Import initial services work
d8d6d82e30 SVN layout migration for core/trunk
66ce8b7fb hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
66ce8b7fb hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
-	// todo : remove this once the state objects are cleaned up
-		// todo : remove this by coordinated ordering of entity processing
35edd5690 hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/LoadQueryJoinAndFetchProcessor.java
-			// todo : remove this assumption ^^; maybe we make CollectionQuerySpace "special" and rather than have it
8e2f2a9da hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
-	// todo : remove these
af1061a42 hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/LoadQueryJoinAndFetchProcessor.java
+			// todo : remove this assumption ^^
dc7cdf9d8 hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
+	// todo : remove these
a7179fbc4 hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/Helper.java
+	// todo : remove this once the state objects are cleaned up
baeb6dc40 hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+		// todo : remove this by coordinated ordering of entity processing
1a40b0232 hibernate-core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
-	// todo : remove these once we get the services in place and integrated into the SessionFactory
20a120ef6 hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
-		// todo : remove.  this is legacy.  convert usages to configuration()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
fd3fb8b31 hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+		// todo : remove.  this is legacy.  convert usages to configuration()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
21cc90fbf hibernate-core/src/main/java/org/hibernate/engine/TransactionHelper.java
-	// todo : remove this and just have subclasses use IsolationDelegate directly...
03c004bd1 core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
+	// todo : remove these once we get the services in place and integrated into the SessionFactory
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/TransactionHelper.java
d8d6d82e3 code/core/src/main/java/org/hibernate/impl/SessionImpl.java
d8d6d82e3 code/core/src/main/java/org/hibernate/mapping/Component.java
d8d6d82e3 code/core/src/main/java/org/hibernate/mapping/Property.java
d8d6d82e3 code/core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+	// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...
+		// todo : remove
+		// todo : remove this once ComponentMetamodel is complete and merged
+	// todo : remove
+	// todo : remove
+	// todo : remove
+		// todo : remove the identityInsert param and variations;
*_*_*
// We should reengineer this class so that
# shas =  1
*_*_*
// todo : should this get moved to PersistentContext?
# shas =  1
*_*_*
// TODO: something much better - look at the type of the other expression!
ac2f06800e HHH-9074 - HQL Query with boolean and @Convert
d8d6d82e30 SVN layout migration for core/trunk
ac2f06800 hibernate-core/src/main/java/org/hibernate/hql/internal/ast/util/LiteralProcessor.java
-		// TODO: something much better - look at the type of the other expression!
d8d6d82e3 code/core/src/main/java/org/hibernate/hql/ast/util/LiteralProcessor.java
+		// TODO: something much better - look at the type of the other expression!
*_*_*
//TODO: this class does too many things! we need a different
# shas =  1
*_*_*
//TODO: optimize this better!
# shas =  1
*_*_*
//TODO: should we allow suffixes on these ?
# shas =  1
*_*_*
//TODO: is this really necessary????
# shas =  1
*_*_*
// TODO : not sure the best way to handle this
# shas =  1
*_*_*
//TODO: is there a better way?
# shas =  1
*_*_*
//The class is now way to complex!
# shas =  1
*_*_*
// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
774b805ce1 initial HEM->CORE consolidation work
7d99ca57f3 HHH-7402 - Improve performance of named query registry
d8d6d82e30 SVN layout migration for core/trunk
87e3f0fd2 hibernate-core/src/main/java/org/hibernate/internal/NamedQueryRepository.java
-				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
774b805ce hibernate-core/src/main/java/org/hibernate/query/spi/NamedQueryRepository.java
+				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
7d99ca57f hibernate-core/src/main/java/org/hibernate/internal/NamedQueryRepository.java
7d99ca57f hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
-				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
d8d6d82e3 code/core/src/main/java/org/hibernate/impl/SessionFactoryImpl.java
+				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
*_*_*
//name from a super query (a bit inelegant that it shows up here)
7308e14fed HHH-9803 - Checkstyle fix ups
d8d6d82e30 SVN layout migration for core/trunk
7308e14fe hibernate-core/src/main/java/org/hibernate/hql/internal/classic/QueryTranslatorImpl.java
-				//name from a super query (a bit inelegant that it shows up here)
d8d6d82e3 code/core/src/main/java/org/hibernate/hql/classic/QueryTranslatorImpl.java
+				//name from a super query (a bit inelegant that it shows up here)
*_*_*
// used to count the nesting of parentheses
# shas =  1
*_*_*
// todo : we can remove this once the deprecated ctor can be made private...
a6ca833e2e HHH-3392 : query cache cluster replication with ResultTransformers
1851bffce7 HHH-3409 : ResultTransformer uniqueing
a6ca833e2 core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
1851bffce core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
*_*_*
//put() has nowait semantics
# shas =  1
*_*_*
// ugly hack for cases like "elements(foo.bar.collection)"
# shas =  1
*_*_*
// FIXME Hacky workaround to JBCACHE-1202
d2c88d55df HHH-5647 - Develop release process using Gradle
834be2837a Redo handling of invalidated region root nodes
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/BasicRegionAdapter.java
-                // FIXME Hacky workaround to JBCACHE-1202
-                     // FIXME Hacky workaround to JBCACHE-1202
834be2837 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
+                // FIXME Hacky workaround to JBCACHE-1202
                      // FIXME Hacky workaround to JBCACHE-1202
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
+                // FIXME Hacky workaround to JBCACHE-1202
*_*_*
//TODO: COPY/PASTE FROM SessionImpl
# shas =  1
*_*_*
// todo : need to find a clean way to handle the "event source" role
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
d8d6d82e30 SVN layout migration for core/trunk
87e3f0fd2 hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
-	// todo : need to find a clean way to handle the "event source" role
d8d6d82e3 code/core/src/main/java/org/hibernate/impl/SessionImpl.java
+	// todo : need to find a clean way to handle the "event source" role
*_*_*
//Must be done here because of side-effect! yuck...
# shas =  1
*_*_*
//We should actually rework this class to not implement Parser
# shas =  1
*_*_*
// TODO : should remove this exposure
b006a6c3c5 HHH-5765 : Refactor JDBCContext/ConnectionManager spi/impl and to use new proxies
d8d6d82e30 SVN layout migration for core/trunk
b006a6c3c hibernate-core/src/main/java/org/hibernate/impl/SessionImpl.java
-		// TODO : should remove this exposure
d8d6d82e3 code/core/src/main/java/org/hibernate/impl/SessionImpl.java
+		// TODO : should remove this exposure
*_*_*
// we may be screwed here since the collection action is about to execute
# shas =  1
*_*_*
// TODO : is there a way to tell whether a persister is truly discrim-column based inheritence?
# shas =  1
*_*_*
//work around a bug in all known connection pools....
3712e1ad7e Give each project a single logger
a9b1425f3f Replaced references to slf4j with references to new jboss.logging.Logger implementations and i18n'd where it was clear how to do so.
b006a6c3c5 HHH-5765 : Refactor JDBCContext/ConnectionManager spi/impl and to use new proxies
d8d6d82e30 SVN layout migration for core/trunk
3712e1ad7 hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
-			//work around a bug in all known connection pools....
a9b1425f3 hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
+			//work around a bug in all known connection pools....
b006a6c3c hibernate-core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
-			//work around a bug in all known connection pools....
d8d6d82e3 code/core/src/main/java/org/hibernate/jdbc/AbstractBatcher.java
+			//work around a bug in all known connection pools....
*_*_*
//TODO: reuse the PreLoadEvent
2ff69d24c4 HHH-7872 - Improved L2 cache storage of "reference" data
d8d6d82e30 SVN layout migration for core/trunk
2ff69d24c hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntry.java
2ff69d24c hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StandardCacheEntryImpl.java
-		//TODO: reuse the PreLoadEvent
+		//TODO: reuse the PreLoadEvent
d8d6d82e3 code/core/src/main/java/org/hibernate/cache/entry/CacheEntry.java
+		//TODO: reuse the PreLoadEvent
*_*_*
// Need a better way to define the suffixes here...
# shas =  1
*_*_*
//workaround for WebLogic
0816d00e59 HHH-5986 - Refactor org.hibernate.util package for spi/internal split
0bfe7869e4 HHH-5638 HHH-5639 HHH-5640 : Import DialectFactory. DialectResolver, ConnectionProvider, and JDBC batching services
e197d208b6 HHH-5373 - Better account for SQLWarnings in temp table creation
d8d6d82e30 SVN layout migration for core/trunk
0816d00e5 hibernate-core/src/main/java/org/hibernate/engine/jdbc/spi/SqlExceptionHelper.java
0816d00e5 hibernate-core/src/main/java/org/hibernate/util/JDBCExceptionReporter.java
-				//workaround for WebLogic
+			//workaround for WebLogic
+			//workaround for WebLogic
-			//workaround for WebLogic
-			//workaround for WebLogic
0bfe7869e core/src/main/java/org/hibernate/service/jdbc/spi/SQLExceptionHelper.java
+				//workaround for WebLogic
e197d208b core/src/main/java/org/hibernate/util/JDBCExceptionReporter.java
-				//workaround for WebLogic
+			//workaround for WebLogic
+			//workaround for WebLogic
d8d6d82e3 code/core/src/main/java/org/hibernate/util/JDBCExceptionReporter.java
+				//workaround for WebLogic
*_*_*
//TODO: temporary
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
d8d6d82e30 SVN layout migration for core/trunk
87e3f0fd2 hibernate-core/src/main/java/org/hibernate/engine/spi/SessionImplementor.java
-	//TODO: temporary
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/SessionImplementor.java
+	//TODO: temporary
*_*_*
// TODO : YUCK!!! fix after HHH-1907 is complete
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
8a5415d367 HHH-6359 : Integrate new metamodel into entity tuplizers
c3d4758f1e HHH-3515 : EntityNameResolver
3a813dcbb hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+		// TODO : YUCK!!!  fix after HHH-1907 is complete
87e3f0fd2 hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
-		// TODO : YUCK!!!  fix after HHH-1907 is complete
66ce8b7fb hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
-		// TODO : YUCK!!!  fix after HHH-1907 is complete
8a5415d36 hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+		// TODO : YUCK!!!  fix after HHH-1907 is complete
c3d4758f1 core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+		// TODO : YUCK!!!  fix after HHH-1907 is complete
*_*_*
// would be great to move all this below here into another method that could also be used
# shas =  1
*_*_*
//TODO: the i==entitySpan-1 bit depends upon subclass implementation (very bad)
460cbf8d96 HHH-2277 - bidirectional <key-many-to-one> both lazy=false fetch=join lead to infinite loop
d8d6d82e30 SVN layout migration for core/trunk
460cbf8d9 core/src/main/java/org/hibernate/loader/Loader.java
-			//TODO: the i==entitySpan-1 bit depends upon subclass implementation (very bad)
d8d6d82e3 code/core/src/main/java/org/hibernate/loader/Loader.java
+			//TODO: the i==entitySpan-1 bit depends upon subclass implementation (very bad)
*_*_*
complex stuff here
# shas =  1
*_*_*
//TODO: add a CriteriaImplementor interface
# shas =  1
*_*_*
// polymorphism not really handled completely correctly
8e2f2a9da6 HHH-8597 : Delete org.hibernate.loader.plan2 and related code
af1061a42d HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
dc7cdf9d88 HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
3d332371bd HHH-7841 - Redesign Loader
d8d6d82e30 SVN layout migration for core/trunk
8e2f2a9da hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReferenceReader.java
8e2f2a9da hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessingContextImpl.java
-				// polymorphism not really handled completely correctly,
-//				// polymorphism not really handled completely correctly,
af1061a42 hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/process/internal/EntityReferenceInitializerImpl.java
+				// polymorphism not really handled completely correctly,
dc7cdf9d8 hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java
dc7cdf9d8 hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/EntityReferenceReader.java
dc7cdf9d8 hibernate-core/src/main/java/org/hibernate/loader/plan/exec/process/internal/ResultSetProcessingContextImpl.java
+				// polymorphism not really handled completely correctly,
3d332371b hibernate-core/src/main/java/org/hibernate/loader/internal/ResultSetProcessingContextImpl.java
+				// polymorphism not really handled completely correctly,
d8d6d82e3 code/core/src/main/java/org/hibernate/loader/Loader.java
+				// polymorphism not really handled completely correctly,
*_*_*
//This is not very nice (and quite slow):
# shas =  1
*_*_*
//TODO: get SQL rendering out of here
# shas =  1
*_*_*
// meant to handle dynamic instantiation queries...(Copy from QueryLoader)
# shas =  1
*_*_*
// TODO: what should be the actual exception type here?
# shas =  1
*_*_*
// worrying about proxies is perhaps a little bit of overkill here...
# shas =  1
*_*_*
// todo : throw exception? maybe warn if not the same?
# shas =  1
*_*_*
//TODO: make EntityPersister *not* depend on SessionFactoryImplementor
ddfcc44d76 HHH-5916 - Add support for a programmatic way to define a default EntityPersister and CollectionPersister class implementation
d8d6d82e30 SVN layout migration for core/trunk
ddfcc44d7 hibernate-core/src/main/java/org/hibernate/persister/PersisterFactory.java
d8d6d82e3 code/core/src/main/java/org/hibernate/persister/PersisterFactory.java
*_*_*
// TODO : what amount of significant digits need to be supported here?
# shas =  1
*_*_*
//for backward compatibility
# shas =  1
*_*_*
// todo : we can remove this once the deprecated ctor can be made private...
a6ca833e2e HHH-3392 : query cache cluster replication with ResultTransformers
1851bffce7 HHH-3409 : ResultTransformer uniqueing
a6ca833e2 core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
1851bffce core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
*_*_*
// TODO: copy/paste from ManyToOneType
# shas =  1
*_*_*
//TODO: code duplication with SingleTableEntityPersister
# shas =  1
*_*_*
// todo : throw exception?
# shas =  1
*_*_*
//should this interface extend PropertyMapping?
# shas =  1
*_*_*
// TODO: here is why we need to make bytecode provider global :(
# shas =  1
*_*_*
//TODO: currently keeps Getters and Setters (instead of PropertyAccessors) because of the way getGetter()
# shas =  1
*_*_*
// todo : we can remove this once the deprecated ctor can be made private...
a6ca833e2e HHH-3392 : query cache cluster replication with ResultTransformers
1851bffce7 HHH-3409 : ResultTransformer uniqueing
a6ca833e2 core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
1851bffce core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
*_*_*
//TODO: Yuck! This is not quite good enough
# shas =  1
*_*_*
//TODO: implement caching?! proxies?!
1c34914455 HHH-11703 - Entity with Natural ID not being cached in the persistenceContext, causing extra queries
d8d6d82e30 SVN layout migration for core/trunk
1c3491445 hibernate-core/src/main/java/org/hibernate/type/EntityType.java
-		//TODO: implement caching?! proxies?!
d8d6d82e3 code/core/src/main/java/org/hibernate/type/EntityType.java
+		//TODO: implement caching?! proxies?!
*_*_*
// is this really necessary?
7308e14fed HHH-9803 - Checkstyle fix ups
d8d6d82e30 SVN layout migration for core/trunk
7308e14fe hibernate-core/src/main/java/org/hibernate/type/AbstractBynaryType.java
-			if (inputStream==null) return toExternalFormat( null ); // is this really necessary?
d8d6d82e3 code/core/src/main/java/org/hibernate/type/AbstractBynaryType.java
+			if (inputStream==null) return toExternalFormat( null ); // is this really necessary?
*_*_*
//TODO: this is a little inefficient
# shas =  1
*_*_*
//TODO: this is a bit arbitrary
9938937fe7 HHH-8637 - Downcasting with TREAT operator should also filter results by the specified Type
d8d6d82e30 SVN layout migration for core/trunk
9938937fe hibernate-core/src/main/java/org/hibernate/type/EntityType.java
-		if ( isReferenceToPrimaryKey() ) { //TODO: this is a bit arbitrary, expose a switch to the user?
d8d6d82e3 code/core/src/main/java/org/hibernate/type/EntityType.java
+		if ( isReferenceToPrimaryKey() ) { //TODO: this is a bit arbitrary, expose a switch to the user?
*_*_*
" CONTRIBUTING.md README.md build.gradle buildSrc changelog.txt databases databases.gradle documentation etc gitLog.sh gradle gradlew gradlew.bat hib-comments-output.java hib-comments-output.txt hib-comments-output2.java hib-comments.txt hibernate-c3p0 hibernate-core hibernate-ehcache hibernate-entitymanager hibernate-envers hibernate-hikaricp hibernate-infinispan hibernate-java8 hibernate-jcache hibernate-orm-modules hibernate-osgi hibernate-proxool hibernate-spatial hibernate-testing hibernate_logo.gif lgpl.txt libraries.gradle migration-guide.adoc release settings.gradle shared tooling utilities.gradle FIXME: even if isInverse=""true""?"
# shas =  0
*_*_*
// TODO: Fix this so it will work for non-POJO entity mode
# shas =  1
*_*_*
//TODO: move these to a new JoinableType abstract class
# shas =  1
*_*_*
//TODO: would be nice to handle proxy classes elegantly!
421789ddcd HHH-5138 - Redesign types + introduce TypeRegistry & TypeResolver
d8d6d82e30 SVN layout migration for core/trunk
421789ddc core/src/main/java/org/hibernate/type/ClassType.java
-		//TODO: would be nice to handle proxy classes elegantly!
d8d6d82e3 code/core/src/main/java/org/hibernate/type/ClassType.java
+		//TODO: would be nice to handle proxy classes elegantly!
*_*_*
//TODO: i'm not sure
# shas =  1
*_*_*
//TODO: is there a more elegant way than downcasting?
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
d8d6d82e30 SVN layout migration for core/trunk
87e3f0fd2 hibernate-core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
-		//TODO: is there a more elegant way than downcasting?
d8d6d82e3 code/core/src/main/java/org/hibernate/persister/collection/NamedQueryCollectionInitializer.java
+		//TODO: is there a more elegant way than downcasting?
*_*_*
// todo : remove
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
35edd56907 HHH-8839 : Eager Map with entity key causes IllegalStateException: Collection element (many-to-many) table alias cannot be empty
8e2f2a9da6 HHH-8597 : Delete org.hibernate.loader.plan2 and related code
af1061a42d HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
dc7cdf9d88 HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
a7179fbc49 HHH-6371 - Develop metamodel binding creation using a push approach
baeb6dc400 HHH-6291 - Basic MetadataImpl redesign
1a40b0232f HHH-6117 - Figure out best way to handle SessionFactoryObjectFactory dealing with JNDI
20a120ef6c HHH-5942 - Migrate to JUnit 4
fd3fb8b316 HHH-5942 - Migrate to JUnit 4
21cc90fbf4 HHH-5985 - Remove TransactionHelper in preference of IsolationDelegate
03c004bd13 HHH-5632 - Import initial services work
d8d6d82e30 SVN layout migration for core/trunk
66ce8b7fb hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
66ce8b7fb hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
-	// todo : remove this once the state objects are cleaned up
-		// todo : remove this by coordinated ordering of entity processing
35edd5690 hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/LoadQueryJoinAndFetchProcessor.java
-			// todo : remove this assumption ^^; maybe we make CollectionQuerySpace "special" and rather than have it
8e2f2a9da hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
-	// todo : remove these
af1061a42 hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/LoadQueryJoinAndFetchProcessor.java
+			// todo : remove this assumption ^^
dc7cdf9d8 hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
+	// todo : remove these
a7179fbc4 hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/Helper.java
+	// todo : remove this once the state objects are cleaned up
baeb6dc40 hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+		// todo : remove this by coordinated ordering of entity processing
1a40b0232 hibernate-core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
-	// todo : remove these once we get the services in place and integrated into the SessionFactory
20a120ef6 hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
-		// todo : remove.  this is legacy.  convert usages to configuration()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
fd3fb8b31 hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+		// todo : remove.  this is legacy.  convert usages to configuration()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
21cc90fbf hibernate-core/src/main/java/org/hibernate/engine/TransactionHelper.java
-	// todo : remove this and just have subclasses use IsolationDelegate directly...
03c004bd1 core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
+	// todo : remove these once we get the services in place and integrated into the SessionFactory
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/TransactionHelper.java
d8d6d82e3 code/core/src/main/java/org/hibernate/impl/SessionImpl.java
d8d6d82e3 code/core/src/main/java/org/hibernate/mapping/Component.java
d8d6d82e3 code/core/src/main/java/org/hibernate/mapping/Property.java
d8d6d82e3 code/core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+	// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...
+		// todo : remove
+		// todo : remove this once ComponentMetamodel is complete and merged
+	// todo : remove
+	// todo : remove
+	// todo : remove
+		// todo : remove the identityInsert param and variations;
*_*_*
//TODO: remove use of instanceof!
# shas =  1
*_*_*
//create an index on the key columns??
# shas =  1
*_*_*
//TODO: make this a bit nicer
cb51ef1a64 HHH-5135 - "Ambiguous column" exception thrown with columns having the same name as a function registered with the dialect (e.g. to_date, floor)
538982e8e6 HHH-2802 : order-by mapping -> support for property names
d8d6d82e30 SVN layout migration for core/trunk
cb51ef1a6 core/src/main/java/org/hibernate/sql/Template.java
-		//TODO: make this a bit nicer
538982e8e core/src/main/java/org/hibernate/sql/Template.java
-		//TODO: make this a bit nicer
d8d6d82e3 code/core/src/main/java/org/hibernate/sql/Template.java
+		//TODO: make this a bit nicer
+		//TODO: make this a bit nicer
*_*_*
// todo : we can remove this once the deprecated ctor can be made private...
a6ca833e2e HHH-3392 : query cache cluster replication with ResultTransformers
1851bffce7 HHH-3409 : ResultTransformer uniqueing
a6ca833e2 core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
a6ca833e2 core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
1851bffce core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
1851bffce core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
*_*_*
//TODO: perhaps this should be some other RuntimeException...
# shas =  1
*_*_*
//Yuck:
cb51ef1a64 HHH-5135 - "Ambiguous column" exception thrown with columns having the same name as a function registered with the dialect (e.g. to_date, floor)
d8d6d82e30 SVN layout migration for core/trunk
cb51ef1a6 core/src/main/java/org/hibernate/sql/Template.java
-			if ( //Yuck:
+			//Yuck:
+//			//Yuck:
d8d6d82e3 code/core/src/main/java/org/hibernate/sql/Template.java
+			if ( //Yuck:
*_*_*
//TODO: need some caching scheme? really comes down to decision
4a4f636caf HHH-6330 - Remove entity mode switching capability
d8d6d82e30 SVN layout migration for core/trunk
4a4f636ca hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
-		//TODO: need some caching scheme? really comes down to decision
d8d6d82e3 code/core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+		//TODO: need some caching scheme? really comes down to decision 
*_*_*
// todo : this eventually needs to be removed
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
d8d6d82e30 SVN layout migration for core/trunk
9e063ffa2 hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
-	// todo : this eventually needs to be removed
-	// todo : this eventually needs to be removed
d8d6d82e3 code/core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+	// todo : this eventually needs to be removed
+	// todo : this eventually needs to be removed
*_*_*
//TODO: design new lifecycle for ProxyFactory
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
8a5415d367 HHH-6359 : Integrate new metamodel into entity tuplizers
d8d6d82e30 SVN layout migration for core/trunk
66ce8b7fb hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
-			//TODO: design new lifecycle for ProxyFactory
8a5415d36 hibernate-core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
+			//TODO: design new lifecycle for ProxyFactory
d8d6d82e3 code/core/src/main/java/org/hibernate/tuple/entity/DynamicMapEntityTuplizer.java
+			//TODO: design new lifecycle for ProxyFactory
*_*_*
// todo: we should really just collect these from the various SelectExpressions
# shas =  1
*_*_*
// TODO: Should this really return null? If not
# shas =  1
*_*_*
// todo : should seriously consider not allowing a txn to begin from a child session
4a4f636caf HHH-6330 - Remove entity mode switching capability
d8d6d82e30 SVN layout migration for core/trunk
4a4f636ca hibernate-core/src/main/java/org/hibernate/internal/SessionImpl.java
-        // todo : should seriously consider not allowing a txn to begin from a child session
d8d6d82e3 code/core/src/main/java/org/hibernate/impl/SessionImpl.java
+			// todo : should seriously consider not allowing a txn to begin from a child session
*_*_*
// should indicate that we are processing an INSERT/UPDATE/DELETE
4ab3caa789 HHH-8318 - Problem determining qualifier to use for column names from HQL query parser in certain circumstances
d8d6d82e30 SVN layout migration for core/trunk
4ab3caa78 hibernate-core/src/main/java/org/hibernate/hql/internal/ast/tree/DotNode.java
-			// should indicate that we are processing an INSERT/UPDATE/DELETE
d8d6d82e3 code/core/src/main/java/org/hibernate/hql/ast/tree/DotNode.java
+			// should indicate that we are processing an INSERT/UPDATE/DELETE
*_*_*
// TODO : this really needs to be delayed unitl after we definitively know all node types
186e9fc115 HHH-4907  Support for tuple syntax in HQL/Criteria on databases which do not support tuple syntax
d8d6d82e30 SVN layout migration for core/trunk
186e9fc11 core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
-		// TODO : this really needs to be delayed unitl after we definitively know all node types
d8d6d82e3 code/core/src/main/java/org/hibernate/hql/ast/tree/BinaryLogicOperatorNode.java
+		// TODO : this really needs to be delayed unitl after we definitively know all node types
*_*_*
// TODO : make this the factory for "entity mode related" sessions
08d9fe2117 HHH-5949 - Migrate, complete and integrate TransactionFactory as a service
d8d6d82e30 SVN layout migration for core/trunk
08d9fe211 hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JDBCContextImpl.java
-	// TODO : make this the factory for "entity mode related" sessions;
d8d6d82e3 code/core/src/main/java/org/hibernate/jdbc/JDBCContext.java
+	// TODO : make this the factory for "entity mode related" sessions;
*_*_*
// create an index on the key columns??
# shas =  1
*_*_*
//is this ok?
4a4f636caf HHH-6330 - Remove entity mode switching capability
d8d6d82e30 SVN layout migration for core/trunk
4a4f636ca hibernate-core/src/main/java/org/hibernate/property/Dom4jAccessor.java
-					owner.setText(null); //is this ok?
d8d6d82e3 code/core/src/main/java/org/hibernate/property/Dom4jAccessor.java
+					owner.setText(null); //is this ok?
*_*_*
//TODO: would it be better to simply pass the qualified table name
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
f90f224f60 HHH-6257 : Add IdentifierGenerator to EntityIdentifier binding
d8d6d82e30 SVN layout migration for core/trunk
66ce8b7fb hibernate-core/src/main/java/org/hibernate/metamodel/binding/BasicAttributeBinding.java
-		//TODO: would it be better to simply pass the qualified table name, instead of
f90f224f6 hibernate-core/src/main/java/org/hibernate/metamodel/binding/SimpleAttributeBinding.java
+		//TODO: would it be better to simply pass the qualified table name, instead of
d8d6d82e3 code/core/src/main/java/org/hibernate/mapping/SimpleValue.java
+		//TODO: would it be better to simply pass the qualified table name, instead of
*_*_*
// perhaps this should be an exception since it is only ever used
fe8c7183d1 HHH-5697 - Support for multi-tenancy
d8d6d82e30 SVN layout migration for core/trunk
fe8c7183d hibernate-core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
-			// perhaps this should be an exception since it is only ever used
d8d6d82e3 code/core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
+			// perhaps this should be an exception since it is only ever used
*_*_*
// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
# shas =  1
*_*_*
// Put a placeholder in entries
# shas =  1
*_*_*
//TODO: should this be Session.instantiate(Persister
af03365c86 HHH-8573: Persister is taken according to the actual type of the replaced object
b185946c81 DefaultMergeEventListener does not call Interceptor.instantiate() for a new persistent entity (Francesco Degrassi)
99e7f895f9 HHH-3229 : Cascade merge transient entities regardless of property traversal order
d8d6d82e30 SVN layout migration for core/trunk
af03365c8 hibernate-core/src/main/java/org/hibernate/type/EntityType.java
-				//TODO: should this be Session.instantiate(Persister, ...)?
b185946c8 core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
-			//TODO: should this be Session.instantiate(Persister, ...)?
99e7f895f core/src/main/java/org/hibernate/type/EntityType.java
+			//TODO: should this be Session.instantiate(Persister, ...)?
-		final Object copy = persister.instantiate( id, source.getEntityMode() );  //TODO: should this be Session.instantiate(Persister, ...)?
+				//TODO: should this be Session.instantiate(Persister, ...)?
d8d6d82e3 code/core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
+		final Object copy = persister.instantiate( id, source.getEntityMode() );  //TODO: should this be Session.instantiate(Persister, ...)?
*_*_*
//TODO: we should throw an exception if we really *know* for sure
# shas =  1
*_*_*
// TODO: The order in which entities are saved may matter (e.g.
e11e9631c7 HHH-5472 : Delay saving an entity if it does not cascade the save to non-nullable transient entities
710d983cd6 HHH-3810 : Transient entities can be inserted twice on merge
e11e9631c hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
-		// TODO: The order in which entities are saved may matter (e.g., a particular transient entity
710d983cd core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
+		// TODO: The order in which entities are saved may matter (e.g., a particular transient entity
*_*_*
//TODO: if object was an uninitialized proxy
# shas =  1
*_*_*
//TODO: put this stuff back in to read snapshot from
a9b1425f3f Replaced references to slf4j with references to new jboss.logging.Logger implementations and i18n'd where it was clear how to do so.
d8d6d82e30 SVN layout migration for core/trunk
a9b1425f3 hibernate-core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java
-			//TODO: put this stuff back in to read snapshot from
d8d6d82e3 code/core/src/main/java/org/hibernate/event/def/DefaultSaveOrUpdateEventListener.java
+			//TODO: put this stuff back in to read snapshot from
*_*_*
//TODO: not quite sure about the full implications of this!
9938937fe7 HHH-8637 - Downcasting with TREAT operator should also filter results by the specified Type
d8d6d82e30 SVN layout migration for core/trunk
9938937fe hibernate-core/src/main/java/org/hibernate/engine/internal/JoinSequence.java
-				//TODO: not quite sure about the full implications of this!
-			//TODO: not quite sure about the full implications of this!
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/JoinSequence.java
+			if (includeExtraJoins) { //TODO: not quite sure about the full implications of this!
+			if (includeExtraJoins) { //TODO: not quite sure about the full implications of this!
*_*_*
//this is kinda the best we can do...
021401835c HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
021401835 hibernate-core/src/main/java/org/hibernate/engine/internal/ForeignKeys.java
-			if (object==LazyPropertyInitializer.UNFETCHED_PROPERTY) return false; //this is kinda the best we can do...
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/ForeignKeys.java
+			if (object==LazyPropertyInitializer.UNFETCHED_PROPERTY) return false; //this is kinda the best we can do...
*_*_*
/* trim done to workaround stupid oracle bug that cant handle whitespaces before a { in a sp buildSrc/ databases/ documentation/ etc/ gradle/ hibernate-c3p0/ hibernate-core/ hibernate-ehcache/ hibernate-entitymanager/ hibernate-envers/ hibernate-hikaricp/ hibernate-infinispan/ hibernate-java8/ hibernate-jcache/ hibernate-orm-modules/ hibernate-osgi/ hibernate-proxool/ hibernate-spatial/ hibernate-testing/ release/ shared/ tooling/
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
3edb72db48 HHH-7387 - Integrate Draft 6 of the JPA 2.1 spec : addNamedQuery support
d8d6d82e30 SVN layout migration for core/trunk
3a813dcbb hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinition.java
87e3f0fd2 hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinition.java
3edb72db4 hibernate-core/src/main/java/org/hibernate/engine/spi/NamedSQLQueryDefinition.java
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/NamedSQLQueryDefinition.java
*_*_*
//TODO: this needn't exclude subclasses...
fb253b0297 HHH-1775 - collection batch fetching
d8d6d82e30 SVN layout migration for core/trunk
fb253b029 hibernate-core/src/main/java/org/hibernate/engine/spi/BatchFetchQueue.java
-		LinkedHashSet<EntityKey> set =  batchLoadableEntityKeys.get( persister.getEntityName() ); //TODO: this needn't exclude subclasses...
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/BatchFetchQueue.java
+			if ( key.getEntityName().equals( persister.getEntityName() ) ) { //TODO: this needn't exclude subclasses...
*_*_*
//TODO: we use two visitors here
# shas =  1
*_*_*
//TODO: does this really need to be here?
# shas =  1
*_*_*
// should leading trim-characters be trimmed?
da647e7deb HHH-4705 - Derby does now in fact support the full ANSI SQL TRIM function
1e45c666e6 HHH-3701 : trim function emulation for sybase
d8d6d82e30 SVN layout migration for core/trunk
da647e7de core/src/main/java/org/hibernate/dialect/DerbyDialect.java
-				boolean leading = true;         // should leading trim-characters be trimmed?
1e45c666e core/src/main/java/org/hibernate/dialect/function/AbstractAnsiTrimEmulationFunction.java
1e45c666e core/src/main/java/org/hibernate/dialect/function/AnsiTrimEmulationFunction.java
+			boolean leading = true;         // should leading trim-characters be trimmed?
-			boolean leading = true;         // should leading trim-characters be trimmed?
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/DerbyDialect.java
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/function/AnsiTrimEmulationFunction.java
+				boolean leading = true;         // should leading trim-characters be trimmed?
+			boolean leading = true;         // should leading trim-characters be trimmed?
*_*_*
//it is possible that the tree-walking in OuterJoinLoader can get to
# shas =  1
*_*_*
// TODO: an alternative is to check if the owner has changed
# shas =  1
*_*_*
//I suppose?
6b5a428b3f HHH-7527 - OSGI manifests for hibernate-orm : clean up org.hibernate.engine.spi package duplication between hem and core
d8d6d82e30 SVN layout migration for core/trunk
6b5a428b3 hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingAction.java
6b5a428b3 hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingActions.java
-			return false; //I suppose?
+			return false; //I suppose?
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/CascadingAction.java
+			return false; //I suppose?
*_*_*
// TBD should this be varbinary($1)?
5fc70fc5ab HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
5fc70fc5a hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
-		// TBD should this be varbinary($1)?
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// TBD should this be varbinary($1)?
*_*_*
//shortcut
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
7337743c93 HHH-3550 : import annotations into core
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
 			return persistentClass; //shortcut for implicit referenced column names
 		if ( columns.length == 0 ) return NO_REFERENCE; //shortcut
-				return nullable; //shortcut
7337743c9 annotations/src/java/org/hibernate/cfg/BinderHelper.java
7337743c9 annotations/src/java/org/hibernate/cfg/Ejb3JoinColumn.java
+			return persistentClass; //shortcut for implicit referenced column names
+		if ( columns.length == 0 ) return NO_REFERENCE; //shortcut
d8d6d82e3 code/core/src/main/java/org/hibernate/mapping/SimpleValue.java
+				return nullable; //shortcut
*_*_*
//TODO: we should provide some way to get keys of collection of statistics to make it easier to retrieve from a GUI perspective
f93d1412a4 HHH-6033 - Migrate stats to api/spi/internal split
39b0774ae3 HHH-5469 - HHH-3659 is only half done, due to HHH-4989 (i.e. no HQL performance log when running Java 5)
8bd2ab12ec HHH-4989 Use a JDK 5 concurrent safe statistics impl when required classes are present, fallback to the existing one otherwise (Alex Snaps)
d8d6d82e30 SVN layout migration for core/trunk
f93d1412a hibernate-core/src/main/java/org/hibernate/stat/StatisticsImpl.java
-	//TODO: we should provide some way to get keys of collection of statistics to make it easier to retrieve from a GUI perspective
39b0774ae core/src/main/java/org/hibernate/stat/ConcurrentStatisticsImpl.java
-	//TODO: we should provide some way to get keys of collection of statistics to make it easier to retrieve from a GUI perspective
8bd2ab12e core/src/main/java/org/hibernate/stat/ConcurrentStatisticsImpl.java
+	//TODO: we should provide some way to get keys of collection of statistics to make it easier to retrieve from a GUI perspective
d8d6d82e3 code/core/src/main/java/org/hibernate/stat/StatisticsImpl.java
+	//TODO: we should provide some way to get keys of collection of statistics to make it easier to retrieve from a GUI perspective
*_*_*
// TODO : probably better to calculate these and pass them in
a41506f404 HHH-4034 - Update org.hibernate.action.BulkOperationCleanupAction to use new Region cache APIs
d8d6d82e30 SVN layout migration for core/trunk
a41506f40 core/src/main/java/org/hibernate/action/BulkOperationCleanupAction.java
-		// TODO : probably better to calculate these and pass them in, as it'll be more performant
d8d6d82e3 code/core/src/main/java/org/hibernate/action/BulkOperationCleanupAction.java
+		// TODO : probably better to calculate these and pass them in, as it'll be more performant
*_*_*
// todo : should we additionally check the current isolation mode explicitly?
# shas =  1
*_*_*
//TODO: simply remove this override
# shas =  1
*_*_*
//TODO: get SQL rendering out of this package!
8c28ba8463 HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/BetweenExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/IlikeExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/NotNullExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/NullExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/PropertyExpression.java
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
*_*_*
// todo : is there really any reason to kkeep trying if this fails once?
5fc70fc5ab HHH-8159 - Apply fixups indicated by analysis tools
b6b1db702e HHH-3912 - Change for HHH-3159 causes InstantiationException
5fc70fc5a hibernate-core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
-			// todo : is there really any reason to kkeep trying if this fails once?
b6b1db702 core/src/main/java/org/hibernate/dialect/Oracle8iDialect.java
+			// todo : is there really any reason to kkeep trying if this fails once?
*_*_*
// TODO : not sure this is correct
# shas =  1
*_*_*
//HSQL has no Blob/Clob support .... but just put these here for now!
# shas =  1
*_*_*
//TODO: perhaps this does need to cascade after all....
6b5a428b3f HHH-7527 - OSGI manifests for hibernate-orm : clean up org.hibernate.engine.spi package duplication between hem and core
d8d6d82e30 SVN layout migration for core/trunk
6b5a428b3 hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingAction.java
-//			//TODO: perhaps this does need to cascade after all....
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/CascadingAction.java
+//			//TODO: perhaps this does need to cascade after all....
*_*_*
// yick! need this for proper serialization/deserialization handling...
# shas =  1
*_*_*
//TODO: should orphans really be deleted during lock???
6b5a428b3f HHH-7527 - OSGI manifests for hibernate-orm : clean up org.hibernate.engine.spi package duplication between hem and core
d8d6d82e30 SVN layout migration for core/trunk
6b5a428b3 hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingAction.java
6b5a428b3 hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingActions.java
-			//TODO: should orphans really be deleted during lock???
+			//TODO: should orphans really be deleted during lock???
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/CascadingAction.java
+			//TODO: should orphans really be deleted during lock???
*_*_*
// todo : move to StringHelper?
# shas =  1
*_*_*
// Sets can be just a view of a part of another collection
# shas =  1
*_*_*
// TODO: It would be really
af1061a42d HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
d8d6d82e30 SVN layout migration for core/trunk
af1061a42 hibernate-core/src/main/java/org/hibernate/persister/entity/PropertyMapping.java
-	// TODO: It would be really, really nice to use this to also model components!
d8d6d82e3 code/core/src/main/java/org/hibernate/persister/entity/PropertyMapping.java
+	// TODO: It would be really, really nice to use this to also model components!
*_*_*
//TODO: move to .sql package!!
# shas =  1
*_*_*
//TODO: look at the owning property and check that it
# shas =  1
*_*_*
// TODO : add this info to the translator and aggregate it here...
# shas =  1
*_*_*
//TODO possibly relax that
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
aa7f7a7822 HHH-3439 : Mappings
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
-				//TODO possibly relax that
aa7f7a782 core/src/main/java/org/hibernate/cfg/Configuration.java
aa7f7a782 core/src/main/java/org/hibernate/cfg/Mappings.java
+				//TODO possibly relax that
-			//TODO possibly relax that
-			//TODO possibly relax that
-			//TODO possibly relax that
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/Mappings.java
+			//TODO possibly relax that
+			//TODO possibly relax that
+			//TODO possibly relax that
*_*_*
// would be better to use the element-type to determine
# shas =  1
*_*_*
//TODO: code duplication with JoinedSubclassEntityPersister
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
5457b6c707 HHH-6411 : Integrate new metamodel into SingleTableEntityPersister
d8d6d82e30 SVN layout migration for core/trunk
66ce8b7fb hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
-		//TODO: code duplication with JoinedSubclassEntityPersister
5457b6c70 hibernate-core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+		//TODO: code duplication with JoinedSubclassEntityPersister
d8d6d82e3 code/core/src/main/java/org/hibernate/persister/entity/SingleTableEntityPersister.java
+		//TODO: code duplication with JoinedSubclassEntityPersister
*_*_*
//TODO: refactor to .sql package
241868e1dd HHH-8741 - More checkstyle cleanups
d8d6d82e30 SVN layout migration for core/trunk
241868e1d hibernate-core/src/main/java/org/hibernate/hql/internal/CollectionSubqueryFactory.java
-	//TODO: refactor to .sql package
d8d6d82e3 code/core/src/main/java/org/hibernate/hql/CollectionSubqueryFactory.java
+	//TODO: refactor to .sql package
*_*_*
//TODO: *two* hashmap lookups here is one too many...
# shas =  1
*_*_*
//note that this method could easily be moved up to BasicEntityPersister
# shas =  1
*_*_*
// from the collection of associations
# shas =  1
*_*_*
//TODO: this class depends directly upon CriteriaImpl
# shas =  1
*_*_*
// Do we need to qualify index names with the schema name?
# shas =  1
*_*_*
//TODO: We probably should have a StatisticsNotPublishedException
37542fe1f1 HHH-6823 - Short-name config values
d8d6d82e30 SVN layout migration for core/trunk
37542fe1f hibernate-core/src/main/java/org/hibernate/jmx/StatisticsService.java
-	//TODO: We probably should have a StatisticsNotPublishedException, to make it clean
d8d6d82e3 code/jmx/src/main/java/org/hibernate/jmx/StatisticsService.java
+	//TODO: We probably should have a StatisticsNotPublishedException, to make it clean
*_*_*
//TODO: get SQL rendering out of this package!
8c28ba8463 HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/BetweenExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/IlikeExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/NotNullExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/NullExpression.java
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/PropertyExpression.java
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
*_*_*
//TODO race conditions can happen here
92ad3eed80 HHH-6297 remove legacy cache api
d8d6d82e30 SVN layout migration for core/trunk
92ad3eed8 hibernate-ehcache/src/main/java/org/hibernate/cache/internal/EhCacheProvider.java
-			//TODO race conditions can happen here
d8d6d82e3 code/cache-ehcache/src/main/java/org/hibernate/cache/EhCacheProvider.java
+			//TODO race conditions can happen here
*_*_*
// TODO: Fix this so it will work for non-POJO entity mode
# shas =  1
*_*_*
//TODO: need to make the majority of this functionality into a top-level support class for custom impl support
# shas =  1
*_*_*
// TODO: it would be better if this was done at the higher level by Printer
4a4f636caf HHH-6330 - Remove entity mode switching capability
d8d6d82e30 SVN layout migration for core/trunk
4a4f636ca hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
-			// TODO: it would be better if this was done at the higher level by Printer
d8d6d82e3 code/core/src/main/java/org/hibernate/type/CollectionType.java
+			// TODO: it would be better if this was done at the higher level by Printer
*_*_*
//TODO: inefficient if that cache is just going to ignore the updated state!
# shas =  1
*_*_*
// TODO: an alternative is to check if the owner has changed
# shas =  1
*_*_*
//TODO:refactor + make this method private
# shas =  1
*_*_*
// TODO : keep seperate notions of QT[] here for shallow/non-shallow queries...
a9b1425f3f Replaced references to slf4j with references to new jboss.logging.Logger implementations and i18n'd where it was clear how to do so.
d8d6d82e30 SVN layout migration for core/trunk
a9b1425f3 hibernate-core/src/main/java/org/hibernate/engine/query/HQLQueryPlan.java
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/query/HQLQueryPlan.java
*_*_*
// needed because currently persister is the one that
# shas =  1
*_*_*
// this is called by SessionFactory irregardless
92ad3eed80 HHH-6297 remove legacy cache api
d8d6d82e30 SVN layout migration for core/trunk
92ad3eed8 hibernate-core/src/main/java/org/hibernate/cache/internal/NoCacheProvider.java
-		// this is called by SessionFactory irregardless; we just disregard here;
-		// this is called by SessionFactory irregardless; we just disregard here;
d8d6d82e3 code/core/src/main/java/org/hibernate/cache/NoCacheProvider.java
+		// this is called by SessionFactory irregardless; we just disregard here;
+		// this is called by SessionFactory irregardless; we just disregard here;
*_*_*
//swaldman 2004-02-07: modify to allow null values to signify fall through to c3p0 PoolConfig defaults
# shas =  1
*_*_*
// work around that crazy issue where the tree contains
# shas =  1
*_*_*
but needed for collections with a "." node mapping
4a4f636caf HHH-6330 - Remove entity mode switching capability
d8d6d82e30 SVN layout migration for core/trunk
4a4f636ca hibernate-core/src/main/java/org/hibernate/property/Dom4jAccessor.java
-			if ( !super.propertyType.isXMLElement() ) { //kinda ugly, but needed for collections with a "." node mapping
d8d6d82e3 code/core/src/main/java/org/hibernate/property/Dom4jAccessor.java
+			if ( !super.propertyType.isXMLElement() ) { //kinda ugly, but needed for collections with a "." node mapping
*_*_*
//FIXME: get the PersistentClass
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
9caca0ce3 hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ResultSetMappingBinder.java
9caca0ce3 hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
+				//FIXME: get the PersistentClass
-		//FIXME: get the PersistentClass
-		//FIXME: get the PersistentClass
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
+		//FIXME: get the PersistentClass
+		//FIXME: get the PersistentClass
*_*_*
// TODO : would be great to have a Mapping#hasNonIdentifierPropertyNamedId method
# shas =  1
*_*_*
// TODO: cache the entity name somewhere so that it is available to this exception
129c0f1348 HHH-6732 more logging trace statements are missing guards against unneeded string creation
99e7f895f9 HHH-3229 : Cascade merge transient entities regardless of property traversal order
129c0f134 hibernate-core/src/main/java/org/hibernate/event/internal/DefaultMergeEventListener.java
-				// TODO: cache the entity name somewhere so that it is available to this exception
99e7f895f core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
+				// TODO: cache the entity name somewhere so that it is available to this exception
*_*_*
//TODO: is this really necessary????
# shas =  1
*_*_*
//TODO: this is kinda slow...
121f495ff8 HHH-4635 Oracle ORA-24816 inserting and updating data for entities containg LOB attributes
d8d6d82e30 SVN layout migration for core/trunk
121f495ff hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
d8d6d82e3 code/core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
*_*_*
//TODO: move to .sql package
# shas =  1
*_*_*
//TODO: I am not so sure about the exception handling in this bit!
# shas =  1
*_*_*
//ugly little workaround for fact that createUniqueKeyLoaders() does not handle component properties
# shas =  1
*_*_*
// append the SQL to return the generated identifier
# shas =  1
*_*_*
//This is really ugly
# shas =  1
*_*_*
// todo : modify the class definition if not already transformed...
472f4ab9ef HHH-10280 - Remove legacy bytecode enhancement artifacts
d8d6d82e30 SVN layout migration for core/trunk
472f4ab9e hibernate-core/src/main/java/org/hibernate/bytecode/internal/javassist/TransformingClassLoader.java
-			// todo : modify the class definition if not already transformed...
d8d6d82e3 code/core/src/main/java/org/hibernate/bytecode/javassist/TransformingClassLoader.java
+	        // todo : modify the class definition if not already transformed...
*_*_*
//TODO: should we use SELECT .. FOR UPDATE?
# shas =  1
*_*_*
/** Create an action that will evict collection and entity regions based on queryspaces (table names).
a41506f404 HHH-4034 - Update org.hibernate.action.BulkOperationCleanupAction to use new Region cache APIs
d8d6d82e30 SVN layout migration for core/trunk
a41506f40 core/src/main/java/org/hibernate/action/BulkOperationCleanupAction.java
d8d6d82e3 code/core/src/main/java/org/hibernate/action/BulkOperationCleanupAction.java
*_*_*
//TODO: is this 100% correct?
# shas =  1
*_*_*
// todo : obviously get rid of all this junk
# shas =  1
*_*_*
// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
# shas =  1
*_*_*
//use of a stringbuffer to workaround a JDK bug
7337743c93 HHH-3550 : import annotations into core
d8d6d82e30 SVN layout migration for core/trunk
7337743c9 annotations/src/java/org/hibernate/cfg/DefaultComponentSafeNamingStrategy.java
7337743c9 annotations/src/java/org/hibernate/cfg/EJB3NamingStrategy.java
7337743c9 annotations/src/test/org/hibernate/test/annotations/AlternativeNamingStrategy.java
+			//use of a stringbuffer to workaround a JDK bug
+			//use of a stringbuffer to workaround a JDK bug
+			//use of a stringbuffer to workaround a JDK bug
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/DefaultNamingStrategy.java
d8d6d82e3 code/core/src/main/java/org/hibernate/cfg/ImprovedNamingStrategy.java
+			//use of a stringbuffer to workaround a JDK bug
+			//use of a stringbuffer to workaround a JDK bug
*_*_*
//TODO: move into collection type
# shas =  1
*_*_*
// TODO should we just collect these like with the collections above?
# shas =  1
*_*_*
// todo : should really move this log statement to CollectionType
a9b1425f3f Replaced references to slf4j with references to new jboss.logging.Logger implementations and i18n'd where it was clear how to do so.
d8d6d82e30 SVN layout migration for core/trunk
a9b1425f3 hibernate-core/src/main/java/org/hibernate/engine/loading/LoadContexts.java
-			// todo : should really move this log statement to CollectionType, where this is used from...
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/loading/LoadContexts.java
+			// todo : should really move this log statement to CollectionType, where this is used from...
*_*_*
// FIXME Hacky workaround to JBCACHE-1202
d2c88d55df HHH-5647 - Develop release process using Gradle
834be2837a Redo handling of invalidated region root nodes
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/BasicRegionAdapter.java
-                // FIXME Hacky workaround to JBCACHE-1202
-                     // FIXME Hacky workaround to JBCACHE-1202
834be2837 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
+                // FIXME Hacky workaround to JBCACHE-1202
                      // FIXME Hacky workaround to JBCACHE-1202
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/BasicRegionAdapter.java
+                // FIXME Hacky workaround to JBCACHE-1202
*_*_*
//hum ... should we cascade anyway? throw an exception? fine like it is?
# shas =  1
*_*_*
// todo: this might really even be moved into the cfg package and used as the basis for all things which are configurable.
a806626a27 HHH-6199 - Split org.hibernate.exception package into api/spi/internal
d8d6d82e30 SVN layout migration for core/trunk
a806626a2 hibernate-core/src/main/java/org/hibernate/exception/Configurable.java
-	// todo: this might really even be moved into the cfg package and used as the basis for all things which are configurable.
d8d6d82e3 code/core/src/main/java/org/hibernate/exception/Configurable.java
+	// todo: this might really even be moved into the cfg package and used as the basis for all things which are configurable.
*_*_*
//TODO: copy/paste from insertRows()
129c0f1348 HHH-6732 more logging trace statements are missing guards against unneeded string creation
d8d6d82e30 SVN layout migration for core/trunk
129c0f134 hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
-								//TODO: copy/paste from insertRows()
d8d6d82e3 code/core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+								//TODO: copy/paste from insertRows()
*_*_*
//TODO: suck into event class
# shas =  1
*_*_*
since getTypeName() actually throws an exception!
c97075c3c8 HHH-6371 - Develop metamodel binding creation using a push approach
d8d6d82e30 SVN layout migration for core/trunk
c97075c3c hibernate-core/src/main/java/org/hibernate/dialect/function/CastFunction.java
-			//TODO: never reached, since getTypeName() actually throws an exception!
d8d6d82e3 code/core/src/main/java/org/hibernate/dialect/function/CastFunction.java
+			//TODO: never reached, since getTypeName() actually throws an exception!
*_*_*
is used by SessionFactoryImpl to hand to the generated SessionImpl
92ad3eed80 HHH-6297 remove legacy cache api
d8d6d82e30 SVN layout migration for core/trunk
92ad3eed8 hibernate-core/src/main/java/org/hibernate/cache/internal/NoCacheProvider.java
-		// This, is used by SessionFactoryImpl to hand to the generated SessionImpl;
d8d6d82e3 code/core/src/main/java/org/hibernate/cache/NoCacheProvider.java
+		// This, is used by SessionFactoryImpl to hand to the generated SessionImpl;
*_*_*
// xmlforest requires a new kind of function constructor
# shas =  1
*_*_*
// TODO Is this a valid operation on a timestamps cache?
a074d3244d HHH-7640 Improve single node Infinispan 2LC performance
d2c88d55df HHH-5647 - Develop release process using Gradle
9ccd912bde [HHH-4103] Initial commit.
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
a074d3244 hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/ClusteredTimestampsRegionImpl.java
+      // TODO Is this a valid operation on a timestamps cache?
       // TODO Is this a valid operation on a timestamps cache?
       // TODO Is this a valid operation on a timestamps cache?
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/timestamp/TimestampsRegionImpl.java
-        // TODO Is this a valid operation on a timestamps cache?
-        // TODO Is this a valid operation on a timestamps cache?
9ccd912bd cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java
+      // TODO Is this a valid operation on a timestamps cache?
+      // TODO Is this a valid operation on a timestamps cache?
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/timestamp/TimestampsRegionImpl.java
+        // TODO Is this a valid operation on a timestamps cache?
+        // TODO Is this a valid operation on a timestamps cache?
*_*_*
// TODO: I considered validating the presence of the TS cache here
d2c88d55df HHH-5647 - Develop release process using Gradle
fb39810294 Add some timestamp cache validation
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/builder/MultiplexingCacheInstanceManager.java
-                   // TODO: I considered validating the presence of the TS cache here,
fb3981029 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/builder/MultiplexingCacheInstanceManager.java
+                   // TODO: I considered validating the presence of the TS cache here,
*_*_*
// TODO Why not use the timestamp in a DataVersion?
d2c88d55df HHH-5647 - Develop release process using Gradle
8663b65640 Evict fixes
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/timestamp/TimestampsRegionImpl.java
-            // TODO Why not use the timestamp in a DataVersion?
8663b6564 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/timestamp/TimestampsRegionImpl.java
+                // TODO Why not use the timestamp in a DataVersion?
*_*_*
// todo : eventually merge this with TreeCache and just add optional opt-lock support there.
640d95bbf6 HHH-4027 - Remove current cache-jbosscache module content
d8d6d82e30 SVN layout migration for core/trunk
640d95bbf cache-jbosscache/src/main/java/org/hibernate/cache/OptimisticTreeCache.java
-	// todo : eventually merge this with TreeCache and just add optional opt-lock support there.
d8d6d82e3 code/cache-jbosscache/src/main/java/org/hibernate/cache/OptimisticTreeCache.java
+	// todo : eventually merge this with TreeCache and just add optional opt-lock support there.
*_*_*
 CONTRIBUTING.md README.md build.gradle buildSrc changelog.txt databases databases.gradle documentation etc gitLog.sh gradle gradlew gradlew.bat hib-comments-output.java hib-comments-output.txt hib-comments-output2.java hib-comments.txt hibernate-c3p0 hibernate-core hibernate-ehcache hibernate-entitymanager hibernate-envers hibernate-hikaricp hibernate-infinispan hibernate-java8 hibernate-jcache hibernate-orm-modules hibernate-osgi hibernate-proxool hibernate-spatial hibernate-testing hibernate_logo.gif lgpl.txt libraries.gradle migration-guide.adoc release settings.gradle shared tooling utilities.gradle FIXME Per the RegionFactory class Javadoc
d2c88d55df HHH-5647 - Develop release process using Gradle
c49ef2e2f2 [HHH-4487] Restore versions of the old public API jbc2 package classes
8f458e07e7 Add initial "clustered integration" test
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JndiMultiplexedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JndiSharedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/MultiplexedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/SharedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
d2c88d55d cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc/functional/util/TestJBossCacheRegionFactory.java
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
c49ef2e2f cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
8f458e07e cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/TestJBossCacheRegionFactory.java
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
*_*_*
// work around the "feature" where tree cache is validating the
d2c88d55df HHH-5647 - Develop release process using Gradle
640d95bbf6 HHH-4027 - Remove current cache-jbosscache module content
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
d8d6d82e30 SVN layout migration for core/trunk
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/util/DataVersionAdapter.java
-            // work around the "feature" where tree cache is validating the
640d95bbf cache-jbosscache/src/main/java/org/hibernate/cache/OptimisticTreeCache.java
-				// work around the "feature" where tree cache is validating the
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/DataVersionAdapter.java
+            // work around the "feature" where tree cache is validating the
d8d6d82e3 code/cache-jbosscache/src/main/java/org/hibernate/cache/OptimisticTreeCache.java
+				// work around the "feature" where tree cache is validating the
*_*_*
// TODO: I considered validating TS cache config here
d2c88d55df HHH-5647 - Develop release process using Gradle
fb39810294 Add some timestamp cache validation
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/builder/MultiplexingCacheInstanceManager.java
-                   // TODO: I considered validating TS cache config here,
fb3981029 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/builder/MultiplexingCacheInstanceManager.java
+                   // TODO: I considered validating TS cache config here,
*_*_*
// can happen because of the multiple ways Cache.remove()
d2c88d55df HHH-5647 - Develop release process using Gradle
640d95bbf6 HHH-4027 - Remove current cache-jbosscache module content
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
d8d6d82e30 SVN layout migration for core/trunk
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/util/DataVersionAdapter.java
-            // can happen because of the multiple ways Cache.remove()
640d95bbf cache-jbosscache/src/main/java/org/hibernate/cache/OptimisticTreeCache.java
-				// can happen because of the multiple ways Cache.remove()
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/DataVersionAdapter.java
+            // can happen because of the multiple ways Cache.remove()
d8d6d82e3 code/cache-jbosscache/src/main/java/org/hibernate/cache/OptimisticTreeCache.java
+				// can happen because of the multiple ways Cache.remove()
*_*_*
//need to do that here rather than in the save event listener to let
# shas =  1
*_*_*
// unhappy about this being public ... is there a better way?
e163f8264d HHH-3512 : id generator short-naming
d8d6d82e30 SVN layout migration for core/trunk
e163f8264 core/src/main/java/org/hibernate/id/IdentifierGeneratorFactory.java
-	// unhappy about this being public ... is there a better way?
d8d6d82e3 code/core/src/main/java/org/hibernate/id/IdentifierGeneratorFactory.java
+	// unhappy about this being public ... is there a better way?
*_*_*
// TODO : safe to interpret "map.remove(key) == null" as non-dirty?
# shas =  1
*_*_*
// FIXME hack to work around fact that calling
d2c88d55df HHH-5647 - Develop release process using Gradle
9e7e49d1f1 [ISPN-6] [HHH-4103] Tidy up commented code.
9ccd912bde [HHH-4103] Initial commit.
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/util/CacheHelper.java
-            // FIXME hack to work around fact that calling
9e7e49d1f cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
-//         // FIXME hack to work around fact that calling
9ccd912bd cache-infinispan/src/main/java/org/hibernate/cache/infinispan/util/CacheHelper.java
+//         // FIXME hack to work around fact that calling
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/CacheHelper.java
+            // FIXME hack to work around fact that calling
*_*_*
// work around the "feature" where tree cache is validating the
d2c88d55df HHH-5647 - Develop release process using Gradle
640d95bbf6 HHH-4027 - Remove current cache-jbosscache module content
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
d8d6d82e30 SVN layout migration for core/trunk
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/util/DataVersionAdapter.java
-            // work around the "feature" where tree cache is validating the
640d95bbf cache-jbosscache/src/main/java/org/hibernate/cache/OptimisticTreeCache.java
-				// work around the "feature" where tree cache is validating the
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/DataVersionAdapter.java
+            // work around the "feature" where tree cache is validating the
d8d6d82e3 code/cache-jbosscache/src/main/java/org/hibernate/cache/OptimisticTreeCache.java
+				// work around the "feature" where tree cache is validating the
*_*_*
// TODO Is this a valid operation on a timestamps cache?
a074d3244d HHH-7640 Improve single node Infinispan 2LC performance
d2c88d55df HHH-5647 - Develop release process using Gradle
9ccd912bde [HHH-4103] Initial commit.
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
a074d3244 hibernate-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/ClusteredTimestampsRegionImpl.java
+      // TODO Is this a valid operation on a timestamps cache?
       // TODO Is this a valid operation on a timestamps cache?
       // TODO Is this a valid operation on a timestamps cache?
d2c88d55d cache-jbosscache/src/main/java/org/hibernate/cache/jbc/timestamp/TimestampsRegionImpl.java
-        // TODO Is this a valid operation on a timestamps cache?
-        // TODO Is this a valid operation on a timestamps cache?
9ccd912bd cache-infinispan/src/main/java/org/hibernate/cache/infinispan/timestamp/TimestampsRegionImpl.java
+      // TODO Is this a valid operation on a timestamps cache?
+      // TODO Is this a valid operation on a timestamps cache?
161e5cc19 cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/timestamp/TimestampsRegionImpl.java
+        // TODO Is this a valid operation on a timestamps cache?
+        // TODO Is this a valid operation on a timestamps cache?
*_*_*
// todo : what else to do here?
ba3359fe62 HHH-11152: Added BytecodeProvider based on Byte Buddy
82d2ef4b1f HHH-6025 - Remove cglib dependencies
d8d6d82e30 SVN layout migration for core/trunk
ba3359fe6 hibernate-core/src/main/java/org/hibernate/bytecode/internal/bytebuddy/PassThroughInterceptor.java
+			// todo : what else to do here?
82d2ef4b1 hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
-				// todo : what else to do here?
d8d6d82e3 code/core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
d8d6d82e3 code/core/src/main/java/org/hibernate/bytecode/javassist/ProxyFactoryFactoryImpl.java
+				// todo : what else to do here?
+				// todo : what else to do here?
*_*_*
// TODO: Replace this with a more elegant solution.
# shas =  1
*_*_*
// short cut check...
59ec451c28 break hibernate-commons-annotations back out into separate project
82e5fa8c78 Remove dependency on core
a51b033dba HHH-3549 : import commons-annotations into core
354714caca HHH-3549 : import commons-annotations into core
d8d6d82e30 SVN layout migration for core/trunk
59ec451c2 commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
-		// short cut check...
82e5fa8c7 commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
+		// short cut check...
a51b033db commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
-		// short cut check...
354714cac commons-annotations/src/java/org/hibernate/annotations/common/util/StringHelper.java
+		// short cut check...
d8d6d82e3 code/core/src/main/java/org/hibernate/util/StringHelper.java
+		// short cut check...
*_*_*
//TODO: to handle concurrent writes correctly
884f6a2455 HHH-4881 - restrict polymorphic query results
4aa9cbe5b7 HHH-5823 - Poor multithread performance in UpdateTimestampsCache class
ccd23dbd3d HHH-5823 - Poor multithread performance in UpdateTimestampsCache class
d8d6d82e30 SVN layout migration for core/trunk
884f6a245 hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java
-		//TODO: to handle concurrent writes correctly, the client should pass in a Lock
4aa9cbe5b hibernate-core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java
-		//TODO: to handle concurrent writes correctly, this should return a Lock to the client
-	 	//TODO: to handle concurrent writes correctly, the client should pass in a Lock
+		//TODO: to handle concurrent writes correctly, the client should pass in a Lock
ccd23dbd3 hibernate-core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java
-		//TODO: to handle concurrent writes correctly, this should return a Lock to the client
-	 	//TODO: to handle concurrent writes correctly, the client should pass in a Lock
+		//TODO: to handle concurrent writes correctly, the client should pass in a Lock
d8d6d82e3 code/core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java
+		//TODO: to handle concurrent writes correctly, this should return a Lock to the client
+	 	//TODO: to handle concurrent writes correctly, the client should pass in a Lock
*_*_*
//put() has nowait semantics
# shas =  1
*_*_*
// this is ugly here
# shas =  1
*_*_*
// TODO : will need reference to session factory to fully complete HHH-1907
# shas =  1
*_*_*
//yuck!
8c28ba8463 HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
8c28ba846 hibernate-core/src/main/java/org/hibernate/criterion/Example.java
-		if ( buf.length()==1 ) buf.append("1=1"); //yuck!
d8d6d82e3 code/core/src/main/java/org/hibernate/criterion/Example.java
d8d6d82e3 code/core/src/main/java/org/hibernate/hql/ast/tree/DotNode.java
d8d6d82e3 code/testsuite/src/test/java/org/hibernate/test/legacy/FooBarTest.java
+		if ( buf.length()==1 ) buf.append("1=1"); //yuck!
+		if ( isSizeProperty ) indexed = true; //yuck!
+		id = new FumTest("").fumKey("abc"); //yuck!!
*_*_*
// inheritance cycle detection (paranoid check)
# shas =  1
*_*_*
// todo : remove
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
35edd56907 HHH-8839 : Eager Map with entity key causes IllegalStateException: Collection element (many-to-many) table alias cannot be empty
8e2f2a9da6 HHH-8597 : Delete org.hibernate.loader.plan2 and related code
af1061a42d HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
dc7cdf9d88 HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
a7179fbc49 HHH-6371 - Develop metamodel binding creation using a push approach
baeb6dc400 HHH-6291 - Basic MetadataImpl redesign
1a40b0232f HHH-6117 - Figure out best way to handle SessionFactoryObjectFactory dealing with JNDI
20a120ef6c HHH-5942 - Migrate to JUnit 4
fd3fb8b316 HHH-5942 - Migrate to JUnit 4
21cc90fbf4 HHH-5985 - Remove TransactionHelper in preference of IsolationDelegate
03c004bd13 HHH-5632 - Import initial services work
d8d6d82e30 SVN layout migration for core/trunk
66ce8b7fb hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
66ce8b7fb hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
-	// todo : remove this once the state objects are cleaned up
-		// todo : remove this by coordinated ordering of entity processing
35edd5690 hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/LoadQueryJoinAndFetchProcessor.java
-			// todo : remove this assumption ^^; maybe we make CollectionQuerySpace "special" and rather than have it
8e2f2a9da hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
-	// todo : remove these
af1061a42 hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/LoadQueryJoinAndFetchProcessor.java
+			// todo : remove this assumption ^^
dc7cdf9d8 hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
+	// todo : remove these
a7179fbc4 hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/Helper.java
+	// todo : remove this once the state objects are cleaned up
baeb6dc40 hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+		// todo : remove this by coordinated ordering of entity processing
1a40b0232 hibernate-core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
-	// todo : remove these once we get the services in place and integrated into the SessionFactory
20a120ef6 hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
-		// todo : remove.  this is legacy.  convert usages to configuration()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
fd3fb8b31 hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+		// todo : remove.  this is legacy.  convert usages to configuration()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
21cc90fbf hibernate-core/src/main/java/org/hibernate/engine/TransactionHelper.java
-	// todo : remove this and just have subclasses use IsolationDelegate directly...
03c004bd1 core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
+	// todo : remove these once we get the services in place and integrated into the SessionFactory
d8d6d82e3 code/core/src/main/java/org/hibernate/engine/TransactionHelper.java
d8d6d82e3 code/core/src/main/java/org/hibernate/impl/SessionImpl.java
d8d6d82e3 code/core/src/main/java/org/hibernate/mapping/Component.java
d8d6d82e3 code/core/src/main/java/org/hibernate/mapping/Property.java
d8d6d82e3 code/core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+	// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...
+		// todo : remove
+		// todo : remove this once ComponentMetamodel is complete and merged
+	// todo : remove
+	// todo : remove
+	// todo : remove
+		// todo : remove the identityInsert param and variations;
*_*_*
