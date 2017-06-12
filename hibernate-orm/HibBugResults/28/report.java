File path: hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
Comment: todo : remove
Initial commit id: baeb6dc4
Final commit id: 66ce8b7f
   Bugs between [      36]:
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
2a55763e40 HHH-8822 ValueHolder fields need to be transient if owned by a Serializable class
cd590470c0 HHH-8741 - More checkstyle cleanups
c52864d188 HHH-7580 Corrected a few failures after the cherry-pick into master
7976e2396a HHH-7580 - Complete 2-phase SessionFactory building design
4ad49a02c9 HHH-7556 - Clean up packages
a86997c7ba HHH-7442 rename org.hibernate.internal.util.Value
129c0f1348 HHH-6732 more logging trace statements are missing guards against unneeded string creation
f4fa176255 HHH-6683 - Consolidate (consistency) building of service registries
7c39b19ab2 HHH-6091 Move DefaultIdentifierGeneratorFactory; use MutableIdentifierGeneratorFactory for public contracts
88a7edbdd7 HHH-6091 Make IdentifierGeneratorFactory a service
2b0e0281b0 HHH-6506 : Descriminator type is not resolved
631286c77b HHH-6503 - Develop Set-style plural attribute support for new metamodel
e540089783 HHH-6480 - Develop component binding for new metamodel
46102a2be3 HHH-6471 - Redesign how EntityBinding models hierarchy-shared information
acc93a3d8c HHH-6447 - Develop shared binding creation approach
4968ad11fb HHH-6447 - Develop shared binding creation approach
f90f224f60 HHH-6257 : Add IdentifierGenerator to EntityIdentifier binding
c7421837a4 HHH-6447 - Develop shared binding creation approach
c5b013d368 HHH-6447 - Develop shared binding creation approach
d3d1fdf423 HHH-6447 - Develop shared binding creation approach
3f31aa8f69 HHH-6371 Bringing the annotation side into sync with the new push (setter) approach for the binders
9972c7ecb7 HHH-6437 - Improve Database to track default Schema object
8c28d46b07 HHH-6371 - Develop metamodel binding creation using a push approach
c97075c3c8 HHH-6371 - Develop metamodel binding creation using a push approach
24edf42c04 HHH-6371 - Develop metamodel binding creation using a push approach
e339dac91e HHH-6371 - Develop metamodel binding creation using a push approach
594f689d98 HHH-6371 - Develop metamodel binding creation using a push approach
182150769a HHH-6371 - Develop metamodel binding creation using a push approach
30843f2032 HHH-6416 : Move AuxiliaryDatabaseObject into Database
ce47766281 HHH-6278 quote all db identifiers also this commit contains code that make annotation binder applying naming strategy
919cdf7c69 HHH-6372 : Provide a temporary way to initialize HibernateTypeDescriptor.explicitType for "basic" types
159f6205f9 HHH-6340 - Revisit EntityBindingState
98e7f9537f HHH-6229 - Clean up MappingDefaults
814b514933 HHH-6110 : Integrate new metamodel into persisters
ba44ae26cb HHH-6110 : Integrate new metamodel into persisters
   Bugs after [       0]:


Start block index: 102
End block index: 132
	public MetadataImpl(MetadataSources metadataSources, Options options) {
		this.serviceRegistry = metadataSources.getServiceRegistry();
		this.options = options;

		this.mappingDefaults = new MappingDefaultsImpl();

		final Binder[] binders;
		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
			binders = new Binder[] {
					new HbmBinder( this ),
					new AnnotationBinder( this )
			};
		}
		else {
			binders = new Binder[] {
					new AnnotationBinder( this ),
					new HbmBinder( this )
			};
		}

		final ArrayList<String> processedEntityNames = new ArrayList<String>();

		prepare( binders, metadataSources );
		bindIndependentMetadata( binders, metadataSources );
		bindTypeDependentMetadata( binders, metadataSources );
		bindMappingMetadata( binders, metadataSources, processedEntityNames );
		bindMappingDependentMetadata( binders, metadataSources );

		// todo : remove this by coordinated ordering of entity processing
		new EntityReferenceResolver( this ).resolve();
	}