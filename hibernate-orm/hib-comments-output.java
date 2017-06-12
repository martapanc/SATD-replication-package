
***// NONE might be a better option moving forward in the case of callable
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
a7179fbc49 HHH-6371 - Develop metamodel binding creation using a push approach
819f8da9ea HHH-5672 - Develop the binding model (binding between logical and relational)
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-			// NONE might be a better option moving forward in the case of callable
hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HbmHelper.java
-			// NONE might be a better option moving forward in the case of callable
hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmHelper.java
+			// NONE might be a better option moving forward in the case of callable
code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+			// NONE might be a better option moving forward in the case of callable
***//perhaps not really necessary...
# shas =  1
***// todo : what else to do here?
ba3359fe62 HHH-11152: Added BytecodeProvider based on Byte Buddy
82d2ef4b1f HHH-6025 - Remove cglib dependencies
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/bytecode/internal/bytebuddy/PassThroughInterceptor.java
+			// todo : what else to do here?
hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
-				// todo : what else to do here?
code/core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
code/core/src/main/java/org/hibernate/bytecode/javassist/ProxyFactoryFactoryImpl.java
+				// todo : what else to do here?
+				// todo : what else to do here?
***//TODO: disable batch loading if lockMode > READ?
35ca4c3563 HHH-4546 add JPA 2.0 locking.  Still need more LockRequest support in AbstractEntityPersister.getAppropriateLoader(), may need to refactor.
d8d6d82e30 SVN layout migration for core/trunk
core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+		//TODO: disable batch loading if lockMode > READ?
code/core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+		//TODO: disable batch loading if lockMode > READ?
***// todo : what else to do here?
ba3359fe62 HHH-11152: Added BytecodeProvider based on Byte Buddy
82d2ef4b1f HHH-6025 - Remove cglib dependencies
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/bytecode/internal/bytebuddy/PassThroughInterceptor.java
+			// todo : what else to do here?
hibernate-core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
-				// todo : what else to do here?
code/core/src/main/java/org/hibernate/bytecode/cglib/ProxyFactoryFactoryImpl.java
code/core/src/main/java/org/hibernate/bytecode/javassist/ProxyFactoryFactoryImpl.java
+				// todo : what else to do here?
+				// todo : what else to do here?
***//TODO: to handle concurrent writes correctly
884f6a2455 HHH-4881 - restrict polymorphic query results
4aa9cbe5b7 HHH-5823 - Poor multithread performance in UpdateTimestampsCache class
ccd23dbd3d HHH-5823 - Poor multithread performance in UpdateTimestampsCache class
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cache/spi/UpdateTimestampsCache.java
-		//TODO: to handle concurrent writes correctly, the client should pass in a Lock
hibernate-core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java
-		//TODO: to handle concurrent writes correctly, this should return a Lock to the client
-	 	//TODO: to handle concurrent writes correctly, the client should pass in a Lock
+		//TODO: to handle concurrent writes correctly, the client should pass in a Lock
hibernate-core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java
-		//TODO: to handle concurrent writes correctly, this should return a Lock to the client
-	 	//TODO: to handle concurrent writes correctly, the client should pass in a Lock
+		//TODO: to handle concurrent writes correctly, the client should pass in a Lock
code/core/src/main/java/org/hibernate/cache/UpdateTimestampsCache.java
+		//TODO: to handle concurrent writes correctly, this should return a Lock to the client
+	 	//TODO: to handle concurrent writes correctly, the client should pass in a Lock
***// gets a chance to see all hibernate.c3p0.*
# shas =  1
***//note there is a wierd implementation in the client side
217898d8aa HHH-5212 - Alter SQLFunction contract to be more flexible
d8d6d82e30 SVN layout migration for core/trunk
core/src/main/java/org/hibernate/dialect/function/CastFunction.java
-		return columnType; //note there is a wierd implementation in the client side
code/core/src/main/java/org/hibernate/dialect/function/CastFunction.java
+		return columnType; //note there is a wierd implementation in the client side
***// this should be refactored to instead expose a method to assemble a EntityEntry based on this
6a388b754c HHH-8159 - Apply fixups indicated by analysis tools
2ff69d24c4 HHH-7872 - Improved L2 cache storage of "reference" data
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cache/spi/entry/CacheEntry.java
-	// this should be refactored to instead expose a method to assemble a EntityEntry based on this
hibernate-core/src/main/java/org/hibernate/cache/spi/entry/StandardCacheEntryImpl.java
-	    // this should be refactored to instead expose a method to assemble a EntityEntry based on this
+	// this should be refactored to instead expose a method to assemble a EntityEntry based on this
+		// this should be refactored to instead expose a method to assemble a EntityEntry based on this
code/core/src/main/java/org/hibernate/cache/entry/CacheEntry.java
+	    // this should be refactored to instead expose a method to assemble a EntityEntry based on this
***// Do we need to drop constraints before dropping tables in this dialect?
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Do we need to drop constraints before dropping tables in this dialect?
hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
-		// Do we need to drop constraints before dropping tables in this dialect?
code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Do we need to drop constraints before dropping tables in this dialect?
**** FIXME Per the RegionFactory class Javadoc
d2c88d55df HHH-5647 - Develop release process using Gradle
c49ef2e2f2 [HHH-4487] Restore versions of the old public API jbc2 package classes
8f458e07e7 Add initial "clustered integration" test
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JndiMultiplexedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JndiSharedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc/MultiplexedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc/SharedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc/functional/util/TestJBossCacheRegionFactory.java
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
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/TestJBossCacheRegionFactory.java
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
***// Does this dialect support check constraints?
5fc70fc5ab HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
-		// Does this dialect support check constraints?
code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Does this dialect support check constraints?
***// this is called by SessionFactory irregardless
92ad3eed80 HHH-6297 remove legacy cache api
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cache/internal/NoCacheProvider.java
-		// this is called by SessionFactory irregardless; we just disregard here;
-		// this is called by SessionFactory irregardless; we just disregard here;
code/core/src/main/java/org/hibernate/cache/NoCacheProvider.java
+		// this is called by SessionFactory irregardless; we just disregard here;
+		// this is called by SessionFactory irregardless; we just disregard here;
***// Does this dialect support the UNIQUE column syntax?
49c8a8e4f0 HHH-7797 Finished auditing dialects.  Cleanup and javadocs.  Completed uniqueness test.
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
-		// Does this dialect support the UNIQUE column syntax?
code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Does this dialect support the UNIQUE column syntax?
***// this is done here 'cos we might only know the type here (ugly!)
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
594f689d98 HHH-6371 - Develop metamodel binding creation using a push approach
819f8da9ea HHH-5672 - Develop the binding model (binding between logical and relational)
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java
hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+//		// this is done here 'cos we might only know the type here (ugly!)
-		// this is done here 'cos we might only know the type here (ugly!)
hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
-//		// this is done here 'cos we might only know the type here (ugly!)
hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
+//		// this is done here 'cos we might only know the type here (ugly!)
code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+		// this is done here 'cos we might only know the type here (ugly!)
***// Does this dialect support the FOR UPDATE syntax?
5fc70fc5ab HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
-		// Does this dialect support the FOR UPDATE syntax?
code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Does this dialect support the FOR UPDATE syntax?
***// todo : what is the implication of this?
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
594f689d98 HHH-6371 - Develop metamodel binding creation using a push approach
819f8da9ea HHH-5672 - Develop the binding model (binding between logical and relational)
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java
hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+				// todo : what is the implication of this?
-				// todo : what is the implication of this?
hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityProcessor.java
-//				// todo : what is the implication of this?
hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityBinder.java
+//				// todo : what is the implication of this?
code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+				// todo : what is the implication of this?
***// todo : YUCK!!!
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
8a5415d367 HHH-6359 : Integrate new metamodel into entity tuplizers
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
-			// todo : YUCK!!!
hibernate-core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+			// todo : YUCK!!!
code/core/src/main/java/org/hibernate/tuple/entity/PojoEntityTuplizer.java
+			// todo : YUCK!!!
***how *should* this work for non-pojo entities?
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
***// Does this dialect support FOR UPDATE OF
5fc70fc5ab HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
-		// Does this dialect support FOR UPDATE OF, allowing particular rows to be locked?
code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Does this dialect support FOR UPDATE OF, allowing particular rows to be locked?
***//TODO: really bad
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-			if ( !node.getName().equals("many-to-many") ) { //TODO: really bad, evil hack to fix!!!
code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+			if ( !node.getName().equals("many-to-many") ) { //TODO: really bad, evil hack to fix!!!
***//TODO: is it kosher to do it here?
# shas =  1
***//TODO: better to degrade to lazy="false" if uninstrumented
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
4968ad11fb HHH-6447 - Develop shared binding creation approach
997dd00880 HHH-6168 : Create an AttributeBinding for many-to-one and implement DomainState and RelationalState for HBM XML
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-			//TODO: better to degrade to lazy="false" if uninstrumented
hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmManyToOneAttributeBindingState.java
-		//TODO: better to degrade to lazy="false" if uninstrumented
hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/domain/HbmManyToOneAttributeDomainState.java
+		//TODO: better to degrade to lazy="false" if uninstrumented
code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+			//TODO: better to degrade to lazy="false" if uninstrumented
***// Does this dialect support the Oracle-style FOR UPDATE NOWAIT syntax?
5fc70fc5ab HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
-		// Does this dialect support the Oracle-style FOR UPDATE NOWAIT syntax?
code/core/src/main/java/org/hibernate/dialect/Cache71Dialect.java
+		// Does this dialect support the Oracle-style FOR UPDATE NOWAIT syntax?
**** FIXME Per the RegionFactory class Javadoc
d2c88d55df HHH-5647 - Develop release process using Gradle
c49ef2e2f2 [HHH-4487] Restore versions of the old public API jbc2 package classes
8f458e07e7 Add initial "clustered integration" test
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JndiMultiplexedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc/JndiSharedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc/MultiplexedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc/SharedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
cache-jbosscache/src/test/java/org/hibernate/test/cache/jbc/functional/util/TestJBossCacheRegionFactory.java
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
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
cache-jbosscache/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
cache-jbosscache2/src/test/java/org/hibernate/test/cache/jbc2/functional/util/TestJBossCacheRegionFactory.java
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JBossCacheRegionFactory.java
cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiMultiplexedJBossCacheRegionFactory.java
cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/JndiSharedJBossCacheRegionFactory.java
cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/MultiplexedJBossCacheRegionFactory.java
cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/SharedJBossCacheRegionFactory.java
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
+     * FIXME Per the RegionFactory class Javadoc, this constructor version
***//Icky workaround for MySQL bug:
377c300071 HHH-8162 Make unique constraint handling on schema update configurable
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
-											//Icky workaround for MySQL bug:
code/core/src/main/java/org/hibernate/cfg/Configuration.java
+											//Icky workaround for MySQL bug:
***// we have to set up the table later!! yuck
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-			// we have to set up the table later!! yuck
code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+			// we have to set up the table later!! yuck
***//TODO: Somehow add the newly created foreign keys to the internal collection
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
-		//TODO: Somehow add the newly created foreign keys to the internal collection
code/core/src/main/java/org/hibernate/cfg/Configuration.java
+		//TODO: Somehow add the newly created foreign keys to the internal collection
***// This inner class implements a case statement....perhaps im being a bit over-clever here
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-	// This inner class implements a case statement....perhaps im being a bit over-clever here
code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+	// This inner class implements a case statement....perhaps im being a bit over-clever here
***//TODO: merge into one method!
# shas =  1
***// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-		// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+		// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
***// TODO: what type?
fbae6db0ab HHH-3414 : fetch profiles, phase 1 : join fetching
d8d6d82e30 SVN layout migration for core/trunk
core/src/main/java/org/hibernate/impl/SessionImpl.java
-			throw new IllegalArgumentException("Invalid filter-parameter name format"); // TODO: what type?
code/core/src/main/java/org/hibernate/impl/SessionImpl.java
+			throw new IllegalArgumentException("Invalid filter-parameter name format"); // TODO: what type?
***//TODO: inefficient
18079f346d HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC) - Initial reworking to remove SQL references (for reuse in Search, OGM, etc) and to split out conceptual "from clause" and "select clause" into different structures (see QuerySpaces)
a102bf2c31 HHH-7841 - Redesign Loader
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationKey.java
-		return table.hashCode(); //TODO: inefficient
hibernate-core/src/main/java/org/hibernate/persister/walking/spi/AssociationKey.java
+		return table.hashCode(); //TODO: inefficient
code/core/src/main/java/org/hibernate/action/EntityUpdateAction.java
code/core/src/main/java/org/hibernate/loader/JoinWalker.java
+				//TODO: inefficient if that cache is just going to ignore the updated state!
+			return table.hashCode(); //TODO: inefficient
***//FIXME: get the PersistentClass
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ResultSetMappingBinder.java
hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
+				//FIXME: get the PersistentClass
-		//FIXME: get the PersistentClass
-		//FIXME: get the PersistentClass
code/core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
+		//FIXME: get the PersistentClass
+		//FIXME: get the PersistentClass
***// todo : we can remove this once the deprecated ctor can be made private...
a6ca833e2e HHH-3392 : query cache cluster replication with ResultTransformers
1851bffce7 HHH-3409 : ResultTransformer uniqueing
core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
***//TODO: this is too conservative
# shas =  1
***//TODO: redesign how PropertyAccessors are acquired...
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
8a5415d367 HHH-6359 : Integrate new metamodel into entity tuplizers
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
-			//TODO: redesign how PropertyAccessors are acquired...
hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+			//TODO: redesign how PropertyAccessors are acquired...
code/core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
+			//TODO: redesign how PropertyAccessors are acquired...
***// TODO : not so sure this is needed...
# shas =  1
***// todo : we can remove this once the deprecated ctor can be made private...
a6ca833e2e HHH-3392 : query cache cluster replication with ResultTransformers
1851bffce7 HHH-3409 : ResultTransformer uniqueing
core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
***//create an index on the key columns??
# shas =  1
***/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-			/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+			/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
***//TODO: improve this hack!
f4c36a10f8 HHH-6439: Added getAddUniqueConstraintString method to Dialect and updated UniqueKey classes to use it
53e1a37adf HHH-6431 : Add Exportable.sqlCreateStrings() and sqlDropStrings() and implementations
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/mapping/UniqueKey.java
hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java
-				//TODO: improve this hack!
-				//TODO: improve this hack!
hibernate-core/src/main/java/org/hibernate/metamodel/relational/UniqueKey.java
+				//TODO: improve this hack!
code/core/src/main/java/org/hibernate/mapping/UniqueKey.java
+				//TODO: improve this hack!
***cos it depends upon ordering of mapping doc
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-		//TODO: bad implementation, cos it depends upon ordering of mapping doc
code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+		//TODO: bad implementation, cos it depends upon ordering of mapping doc
***//use of trim() here is ugly?
c46daa4cf0 HHH-7440, HHH-7368, HHH-7369, HHH-7370 - Redesign dialect-specific LIMIT clause appliance
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/loader/Loader.java
-					sql.trim(), //use of trim() here is ugly?
code/core/src/main/java/org/hibernate/loader/Loader.java
+					sql.trim(), //use of trim() here is ugly?
***//TODO: ideally we need the construction of PropertyAccessor to take the following:
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
-	//TODO: ideally we need the construction of PropertyAccessor to take the following:
code/core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+	//TODO: ideally we need the construction of PropertyAccessor to take the following:
***// can happen because of the multiple ways Cache.remove()
d2c88d55df HHH-5647 - Develop release process using Gradle
640d95bbf6 HHH-4027 - Remove current cache-jbosscache module content
161e5cc19f [HHH-2555] Reimplement Hibernate/JBC 2.0 integration
d8d6d82e30 SVN layout migration for core/trunk
cache-jbosscache/src/main/java/org/hibernate/cache/jbc/util/DataVersionAdapter.java
-            // can happen because of the multiple ways Cache.remove()
cache-jbosscache/src/main/java/org/hibernate/cache/OptimisticTreeCache.java
-				// can happen because of the multiple ways Cache.remove()
cache-jbosscache2/src/main/java/org/hibernate/cache/jbc2/util/DataVersionAdapter.java
+            // can happen because of the multiple ways Cache.remove()
code/cache-jbosscache/src/main/java/org/hibernate/cache/OptimisticTreeCache.java
+				// can happen because of the multiple ways Cache.remove()
***//ick!
59ec451c28 break hibernate-commons-annotations back out into separate project
82e5fa8c78 Remove dependency on core
a51b033dba HHH-3549 : import commons-annotations into core
354714caca HHH-3549 : import commons-annotations into core
d8d6d82e30 SVN layout migration for core/trunk
commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
-			return result + "x"; //ick!
commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
+			return result + "x"; //ick!
commons-annotations/src/main/java/org/hibernate/annotations/common/util/StringHelper.java
-			return result + "x"; //ick!
commons-annotations/src/java/org/hibernate/annotations/common/util/StringHelper.java
+			return result + "x"; //ick!
code/core/src/main/java/org/hibernate/util/StringHelper.java
+			return result + "x"; //ick!
***// todo : we can remove this once the deprecated ctor can be made private...
a6ca833e2e HHH-3392 : query cache cluster replication with ResultTransformers
1851bffce7 HHH-3409 : ResultTransformer uniqueing
core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
***// TODO: this is very suboptimal for some subclasses (namely components)
# shas =  1
***//TODO: should this be Session.instantiate(Persister
af03365c86 HHH-8573: Persister is taken according to the actual type of the replaced object
b185946c81 DefaultMergeEventListener does not call Interceptor.instantiate() for a new persistent entity (Francesco Degrassi)
99e7f895f9 HHH-3229 : Cascade merge transient entities regardless of property traversal order
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/type/EntityType.java
-				//TODO: should this be Session.instantiate(Persister, ...)?
core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
-			//TODO: should this be Session.instantiate(Persister, ...)?
core/src/main/java/org/hibernate/type/EntityType.java
+			//TODO: should this be Session.instantiate(Persister, ...)?
-		final Object copy = persister.instantiate( id, source.getEntityMode() );  //TODO: should this be Session.instantiate(Persister, ...)?
+				//TODO: should this be Session.instantiate(Persister, ...)?
code/core/src/main/java/org/hibernate/event/def/DefaultMergeEventListener.java
+		final Object copy = persister.instantiate( id, source.getEntityMode() );  //TODO: should this be Session.instantiate(Persister, ...)?
***//TODO: suck this into initLaziness!
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
-		//TODO: suck this into initLaziness!
code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
+		//TODO: suck this into initLaziness!
***// todo : we can remove this once the deprecated ctor can be made private...
a6ca833e2e HHH-3392 : query cache cluster replication with ResultTransformers
1851bffce7 HHH-3409 : ResultTransformer uniqueing
core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
-		// todo : we can remove this once the deprecated ctor can be made private...
core/src/main/java/org/hibernate/transform/AliasToEntityMapResultTransformer.java
core/src/main/java/org/hibernate/transform/DistinctRootEntityResultTransformer.java
core/src/main/java/org/hibernate/transform/PassThroughResultTransformer.java
core/src/main/java/org/hibernate/transform/RootEntityResultTransformer.java
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
+		// todo : we can remove this once the deprecated ctor can be made private...
***// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
91d444423b HHH-5765 : Wire in dialect factory and resolvers from service registry
0bfe7869e4 HHH-5638 HHH-5639 HHH-5640 : Import DialectFactory. DialectResolver, ConnectionProvider, and JDBC batching services
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/engine/jdbc/env/internal/JdbcEnvironmentInitiator.java
hibernate-core/src/main/java/org/hibernate/engine/jdbc/internal/JdbcServicesImpl.java
+		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
-		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
-		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
core/src/main/java/org/hibernate/service/jdbc/internal/JdbcServicesImpl.java
+		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
code/core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+		// 'hibernate.temp.use_jdbc_metadata_defaults' is a temporary magic value.
***//TODO: is this right??
dc7cdf9d88 HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
4968ad11fb HHH-6447 - Develop shared binding creation approach
eb414295aa HHH-6134 : Migrate processing hbm.xml files to use Jaxb-generated classes
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/type/AnyType.java
-		throw new UnsupportedOperationException(); //TODO: is this right??
hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmSimpleAttributeBindingState.java
-		this.isInsertable = true; //TODO: is this right????
hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/domain/HbmSimpleAttributeDomainState.java
+		this.isInsertable = true; //TODO: is this right????
code/core/src/main/java/org/hibernate/type/AnyType.java
+		throw new UnsupportedOperationException(); //TODO: is this right??
***//ie. the subquery! yuck!
# shas =  1
***//use of a stringbuffer to workaround a JDK bug
7337743c93 HHH-3550 : import annotations into core
d8d6d82e30 SVN layout migration for core/trunk
annotations/src/java/org/hibernate/cfg/DefaultComponentSafeNamingStrategy.java
annotations/src/java/org/hibernate/cfg/EJB3NamingStrategy.java
annotations/src/test/org/hibernate/test/annotations/AlternativeNamingStrategy.java
+			//use of a stringbuffer to workaround a JDK bug
+			//use of a stringbuffer to workaround a JDK bug
+			//use of a stringbuffer to workaround a JDK bug
code/core/src/main/java/org/hibernate/cfg/DefaultNamingStrategy.java
code/core/src/main/java/org/hibernate/cfg/ImprovedNamingStrategy.java
+			//use of a stringbuffer to workaround a JDK bug
+			//use of a stringbuffer to workaround a JDK bug
***//TODO: copy/paste from recreate()
129c0f1348 HHH-6732 more logging trace statements are missing guards against unneeded string creation
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
-							//TODO: copy/paste from recreate()
code/core/src/main/java/org/hibernate/persister/collection/AbstractCollectionPersister.java
+							//TODO: copy/paste from recreate()
***for backward compatibility of sets with no
# shas =  1
***// todo : this eventually needs to be removed
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
-	// todo : this eventually needs to be removed
-	// todo : this eventually needs to be removed
code/core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+	// todo : this eventually needs to be removed
+	// todo : this eventually needs to be removed
***// todo : remove
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
hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/Helper.java
hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
-	// todo : remove this once the state objects are cleaned up
-		// todo : remove this by coordinated ordering of entity processing
hibernate-core/src/main/java/org/hibernate/loader/plan/exec/internal/LoadQueryJoinAndFetchProcessor.java
-			// todo : remove this assumption ^^; maybe we make CollectionQuerySpace "special" and rather than have it
hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
-	// todo : remove these
hibernate-core/src/main/java/org/hibernate/loader/plan2/exec/internal/LoadQueryJoinAndFetchProcessor.java
+			// todo : remove this assumption ^^
hibernate-core/src/main/java/org/hibernate/loader/plan/spi/EntityFetch.java
+	// todo : remove these
hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/Helper.java
+	// todo : remove this once the state objects are cleaned up
hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+		// todo : remove this by coordinated ordering of entity processing
hibernate-core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
-	// todo : remove these once we get the services in place and integrated into the SessionFactory
hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
-		// todo : remove.  this is legacy.  convert usages to configuration()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
-		// todo : remove.  this is legacy.  convert usages to sessionFactory()
hibernate-testing/src/main/java/org/hibernate/testing/junit4/BaseCoreFunctionalTestCase.java
+		// todo : remove.  this is legacy.  convert usages to configuration()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
+		// todo : remove.  this is legacy.  convert usages to sessionFactory()
hibernate-core/src/main/java/org/hibernate/engine/TransactionHelper.java
-	// todo : remove this and just have subclasses use IsolationDelegate directly...
core/src/main/java/org/hibernate/internal/util/jndi/JndiHelper.java
+	// todo : remove these once we get the services in place and integrated into the SessionFactory
code/core/src/main/java/org/hibernate/engine/TransactionHelper.java
code/core/src/main/java/org/hibernate/impl/SessionImpl.java
code/core/src/main/java/org/hibernate/mapping/Component.java
code/core/src/main/java/org/hibernate/mapping/Property.java
code/core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+	// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...
+		// todo : remove
+		// todo : remove this once ComponentMetamodel is complete and merged
+	// todo : remove
+	// todo : remove
+	// todo : remove
+		// todo : remove the identityInsert param and variations;
***//just to help out during the load (ugly
# shas =  1
***// hack/workaround as sqlquery impl depend on having a key.
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
-			alias = "alias_" + elementCount; // hack/workaround as sqlquery impl depend on having a key.
code/core/src/main/java/org/hibernate/cfg/ResultSetMappingBinder.java
+			alias = "alias_" + elementCount; // hack/workaround as sqlquery impl depend on having a key.
***//TODO: handle the case of a foreign key to something other than the pk
# shas =  1
***//TODO: assumes all collections disassemble to an array!
# shas =  1
***// todo : would love to have this work on a notification basis
3ecbfeb2b2 HHH-5375 - Merge AnnotationConfiguration into Configuration
d8d6d82e30 SVN layout migration for core/trunk
core/src/main/java/org/hibernate/cfg/Configuration.java
-		// todo : would love to have this work on a notification basis
code/core/src/main/java/org/hibernate/cfg/Configuration.java
+		// todo : would love to have this work on a notification basis
***// TODO: is it really neceassry to provide Configuration to CollectionPersisters ?
996d567731 HHH-6214 Converting RegionFactory to a Service
ddfcc44d76 HHH-5916 - Add support for a programmatic way to define a default EntityPersister and CollectionPersister class implementation
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
-	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ?
hibernate-core/src/main/java/org/hibernate/persister/PersisterFactory.java
hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
-	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ? Should it not be enough with associated class ?
+	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ?
code/core/src/main/java/org/hibernate/persister/PersisterFactory.java
+	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ? Should it not be enough with associated class ?
***//TODO: deprecated
33074dc2dc HHH-6069 - Tests moved
d7cc102b00 HHH-6069 - Escape entity fields name
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/mapping/Column.java
+			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually
hibernate-core/src/main/java/org/hibernate/mapping/Column.java
-			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually
code/core/src/main/java/org/hibernate/mapping/Column.java
+			Dialect.QUOTE.indexOf( name.charAt(0) ) > -1 //TODO: deprecated, remove eventually
***//TODO possibly relax that
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
aa7f7a7822 HHH-3439 : Mappings
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/cfg/Configuration.java
-				//TODO possibly relax that
core/src/main/java/org/hibernate/cfg/Configuration.java
core/src/main/java/org/hibernate/cfg/Mappings.java
+				//TODO possibly relax that
-			//TODO possibly relax that
-			//TODO possibly relax that
-			//TODO possibly relax that
code/core/src/main/java/org/hibernate/cfg/Mappings.java
+			//TODO possibly relax that
+			//TODO possibly relax that
+			//TODO possibly relax that
***come up with a better way to check this (plus see above comment)
# shas =  1
***//use a degenerated strategy for backward compatibility
# shas =  1
***//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
1d26ac1e12 HHH-6360 : Build basic properties from an AttributeBinding
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
-		//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
-		//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
hibernate-core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+		//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
code/core/src/main/java/org/hibernate/property/PropertyAccessorFactory.java
+		//TODO: this is temporary in that the end result will probably not take a Property reference per-se.
***// todo : better to handle dynamic filters through implicit DynamicFilterParameterSpecification
6cabc326b8 HHH-8312 - named parameters binding are not correct when used within subquery
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
-		// todo : better to handle dynamic filters through implicit DynamicFilterParameterSpecification
code/core/src/main/java/org/hibernate/loader/hql/QueryLoader.java
+		// todo : better to handle dynamic filters through implicit DynamicFilterParameterSpecification
***// get the right object from the list ... would it be easier to just call getEntity() ??
06b0faaf57 HHH-7746 - Investigate alternative batch loading algorithms
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
-		// get the right object from the list ... would it be easier to just call getEntity() ??
code/core/src/main/java/org/hibernate/loader/entity/BatchingEntityLoader.java
+		// get the right object from the list ... would it be easier to just call getEntity() ??
***// TODO: should "record" how many properties we have reffered to - and if we
# shas =  1
***//TODO: this dependency is kinda Bad
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/internal/util/ReflectHelper.java
-	//TODO: this dependency is kinda Bad
code/core/src/main/java/org/hibernate/util/ReflectHelper.java
+	//TODO: this dependency is kinda Bad
***//TODO: get SQL rendering out of this package!
8c28ba8463 HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java
hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
code/core/src/main/java/org/hibernate/criterion/BetweenExpression.java
code/core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
code/core/src/main/java/org/hibernate/criterion/IlikeExpression.java
code/core/src/main/java/org/hibernate/criterion/NotNullExpression.java
code/core/src/main/java/org/hibernate/criterion/NullExpression.java
code/core/src/main/java/org/hibernate/criterion/PropertyExpression.java
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
***// why does this matter?
8e2f2a9da6 HHH-8597 : Delete org.hibernate.loader.plan2 and related code
b3791bc3c3 HHH-7841 : Redesign Loader
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
-			if ( association.getJoinType() == JoinType.LEFT_OUTER_JOIN ) { // why does this matter?
hibernate-core/src/main/java/org/hibernate/loader/internal/AbstractLoadQueryImpl.java
+			if ( oj.getJoinType() == JoinType.LEFT_OUTER_JOIN ) { // why does this matter?
code/core/src/main/java/org/hibernate/loader/JoinWalker.java
+			if ( oj.getJoinType() == JoinFragment.LEFT_OUTER_JOIN ) { // why does this matter?
***to account for newly saved entities in query
# shas =  1
***//TODO: get SQL rendering out of this package!
8c28ba8463 HHH-8159 - Apply fixups indicated by analysis tools
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/criterion/BetweenExpression.java
hibernate-core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
hibernate-core/src/main/java/org/hibernate/criterion/IlikeExpression.java
hibernate-core/src/main/java/org/hibernate/criterion/NotNullExpression.java
hibernate-core/src/main/java/org/hibernate/criterion/NullExpression.java
hibernate-core/src/main/java/org/hibernate/criterion/PropertyExpression.java
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
-		//TODO: get SQL rendering out of this package!
code/core/src/main/java/org/hibernate/criterion/BetweenExpression.java
code/core/src/main/java/org/hibernate/criterion/IdentifierEqExpression.java
code/core/src/main/java/org/hibernate/criterion/IlikeExpression.java
code/core/src/main/java/org/hibernate/criterion/NotNullExpression.java
code/core/src/main/java/org/hibernate/criterion/NullExpression.java
code/core/src/main/java/org/hibernate/criterion/PropertyExpression.java
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
+		//TODO: get SQL rendering out of this package!
***//TODO: can we *always* use the "null property" approach for everything?
# shas =  1
***// does this need holdlock also? : return tableName + " with (updlock
655be65063 HHH-4546 - add JPA 2.0 locking.
d8d6d82e30 SVN layout migration for core/trunk
core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
-			// does this need holdlock also? : return tableName + " with (updlock, rowlock, holdlock)";
code/core/src/main/java/org/hibernate/dialect/SQLServerDialect.java
+			// does this need holdlock also? : return tableName + " with (updlock, rowlock, holdlock)";
***//TODO: or we could do this polymorphically
# shas =  1
***// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...
08d9fe2117 HHH-5949 - Migrate, complete and integrate TransactionFactory as a service
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/engine/TransactionHelper.java
-	// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...
code/core/src/main/java/org/hibernate/engine/TransactionHelper.java
+	// todo : remove this and just have subclasses use Isolater/IsolatedWork directly...
***at least needed this dropped after use
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
638b43f04b HHH-3712 - Reorganize the Sybase dialect class hierarchy, add SybaseASE15Dialect, and mark SybaseDialect as deprecated
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
+				// sql-server, at least needed this dropped after use; strange!
hibernate-core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
-				// sql-server, at least needed this dropped after use; strange!
core/src/main/java/org/hibernate/dialect/AbstractTransactSQLDialect.java
core/src/main/java/org/hibernate/dialect/SybaseDialect.java
+		return true;  // sql-server, at least needed this dropped after use; strange!
-		return true;  // sql-server, at least needed this dropped after use; strange!
code/core/src/main/java/org/hibernate/dialect/SybaseDialect.java
+		return true;  // sql-server, at least needed this dropped after use; strange!
***// is it necessary to register %exact since it can only appear in a where clause?
# shas =  1
***// Does this dialect support the ALTER TABLE syntax?
# shas =  1
***// TODO: shift it into unsaved-value strategy
# shas =  1
***//TODO: reenable if we also fix the above todo
# shas =  1
***// orphans should not be deleted during copy??
3402ba3a67 HHH-6028 - Remove o.h.classic.Session/Validatable
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/engine/CascadingAction.java
-			// orphans should not be deleted during copy??
code/core/src/main/java/org/hibernate/engine/CascadingAction.java
+			// orphans should not be deleted during copy??
***// orphans should not be deleted during merge??
6b5a428b3f HHH-7527 - OSGI manifests for hibernate-orm : clean up org.hibernate.engine.spi package duplication between hem and core
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingAction.java
hibernate-core/src/main/java/org/hibernate/engine/spi/CascadingActions.java
-			// orphans should not be deleted during merge??
+			// orphans should not be deleted during merge??
code/core/src/main/java/org/hibernate/engine/CascadingAction.java
+			// orphans should not be deleted during merge??
***//TODO: suck this logic into the collection!
# shas =  1
***// todo : we can actually just determine this from the incoming EntityEntry-s
# shas =  1
***//TODO: this bit actually has to be called after all cascades!
3a813dcbb4 HHH-11646 revert errant after -> afterQuery search and replace
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
+		//TODO: this bit actually has to be called after all cascades!
hibernate-core/src/main/java/org/hibernate/action/internal/EntityIdentityInsertAction.java
-		//TODO: this bit actually has to be called after all cascades!
code/core/src/main/java/org/hibernate/action/EntityIdentityInsertAction.java
+		//TODO: this bit actually has to be called after all cascades!
***// this class has no proxies (so do a shortcut)
f74c5a7fa5 HHH-2879 Apply hibernate code templates and formatting
eecee618c6 HHH-2879 Add ResolveNaturalId event, listener and entity persister api
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
-//        // this class has no proxies (so do a shortcut)
hibernate-core/src/main/java/org/hibernate/event/internal/DefaultResolveNaturalIdEventListener.java
+//        // this class has no proxies (so do a shortcut)
code/core/src/main/java/org/hibernate/event/def/DefaultLoadEventListener.java
+			// this class has no proxies (so do a shortcut)
***//TODO: Should this be an InitializeEntityEventListener??? (watch out for performance!)
3800a0e695 HHH-7206 - Manage natural-id synchronization without flushing
d8d6d82e30 SVN layout migration for core/trunk
hibernate-core/src/main/java/org/hibernate/engine/internal/TwoPhaseLoad.java
-		//TODO: Should this be an InitializeEntityEventListener??? (watch out for performance!)
code/core/src/main/java/org/hibernate/engine/TwoPhaseLoad.java
+		//TODO: Should this be an InitializeEntityEventListener??? (watch out for performance!)
***// todo : need map? the prob is a proper key
