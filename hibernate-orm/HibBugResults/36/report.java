File path: hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
Comment: TODO: is it really neceassry to provide Configuration to CollectionPersisters ?
Initial commit id: ddfcc44d
Final commit id: 996d5677
   Bugs between [       5]:
996d567731 HHH-6214 Converting RegionFactory to a Service
fb44ad936d HHH-6196 - Split org.hibernate.engine package into api/spi/internal
c930ebcd7d HHH-6191 - repackage org.hibernate.cache per api/spi/internal split
815baf4348 HHH-6051 - Create a sessionfactory scoped ServiceRegistry
731d00fd6d HHH-6047 - allow nesting of ServiceRegistry
   Bugs after [      10]:
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
9caca0ce37 HHH-9490 - Migrate from dom4j to jaxb for XML processing; HHH-9492 - Migrate to new bootstrap API (MetadataSources, etc); HHH-7078 - Split NamingStrategy into ImplicitNamingStrategy/PhysicalNamingStrategy; HHH-6005 - Better handling of implicit column naming with @ElementCollection of @Embeddables; HHH-9633 - Add tests that explicitly test the "main" NamingStrategy impls
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
ef22e31068 HHH-6974 Adding hooks into NaturalIdRegionAccessStrategy
631286c77b HHH-6503 - Develop Set-style plural attribute support for new metamodel
acc93a3d8c HHH-6447 - Develop shared binding creation approach
e339dac91e HHH-6371 - Develop metamodel binding creation using a push approach
594f689d98 HHH-6371 - Develop metamodel binding creation using a push approach
814b514933 HHH-6110 : Integrate new metamodel into persisters
86ddbc09d9 HHH-6110 : Integrate new metamodel into persisters

Start block index: 44
End block index: 69
	// TODO: is it really neceassry to provide Configuration to CollectionPersisters ?
	// Should it not be enough with associated class ? or why does EntityPersister's not get access to configuration ?
	//
	// The only reason I could see that Configuration gets passed to collection persisters
	// is so that they can look up the dom4j node name of the entity element in case
	// no explicit node name was applied at the collection element level.  Are you kidding me?
	// Trivial to fix then.  Just store and expose the node name on the entity persister
	// (which the collection persister looks up anyway via other means...).

	/**
	 * Create an entity persister instance.
	 *
	 * @param model The O/R mapping metamodel definition for the entity
	 * @param cacheAccessStrategy The caching strategy for this entity
	 * @param factory The session factory
	 * @param cfg The overall mapping
	 *
	 * @return An appropriate entity persister instance.
	 *
	 * @throws HibernateException Indicates a problem building the persister.
	 */
	public EntityPersister createEntityPersister(
			PersistentClass model,
			EntityRegionAccessStrategy cacheAccessStrategy,
			SessionFactoryImplementor factory,
			Mapping cfg) throws HibernateException;
