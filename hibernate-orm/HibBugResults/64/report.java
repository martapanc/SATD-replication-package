File path: hibernate-core/src/main/java/org/hibernate/tuple/entity/AbstractEntityTuplizer.java
Comment: ODO: redesign how PropertyAccessors are acquired...
Initial commit id: 8a5415d3
Final commit id: 66ce8b7f
   Bugs between [      11]:
66ce8b7fb5 HHH-9466 - Drop metamodel package from source
ac16473511 HHH-9003 Avoid allocating arrays in most methods of ComponentType
8fe5460ec0 HHH-8741 - More checkstyle cleanups
cd590470c0 HHH-8741 - More checkstyle cleanups
803c73c555 HHH-8276 - Integrate LoadPlans into UniqueEntityLoader (PoC)
a102bf2c31 HHH-7841 - Redesign Loader
b11c23fd45 HHH-7561 - Fix and test
ddb3a8cd18 HHH-6858 reuse reference to PersistenceContext
fb0255a87a HHH-6858 non-intrusive performance improvements
129c0f1348 HHH-6732 more logging trace statements are missing guards against unneeded string creation
46102a2be3 HHH-6471 - Redesign how EntityBinding models hierarchy-shared information
   Bugs after [      10]:
3906816ee2 HHH-11274 - EntityManagerFactoryImpl.getIdentifier uses deprecated version of getIdentifier
54f3409b41 HHH-11328 : Persist of transient entity in derived ID that is already in merge process throws javax.persistence.EntityExistsException
f0dfc1269b HHH-10865 - ManyToMany relation dropped from database when lazy loading is active
87e3f0fd28 HHH-10664 - Prep 6.0 feature branch - merge hibernate-entitymanager into hibernate-core (first sucessful full compile of consolidated hibernate-core)
1e44e7420b HHH-10267 - Support defining lazy attribute fetch groups
472f4ab9ef HHH-10280 - Remove legacy bytecode enhancement artifacts
9352546006 HHH-8558 - Bytecode enhancer: skip creation of proxy factory
9e063ffa25 HHH-9837 - Remove reliance during annotation binding on org.hibernate.internal.util.ClassLoaderHelper HHH-9841 - Redesign org.hibernate.property.PropertyAccessorFactory
bd256e4783 HHH-9803 - Checkstyle fix ups - headers
611f8a0e1c HHH-9803 - Checkstyle fix ups

Start block index: 175
End block index: 231
	public AbstractEntityTuplizer(EntityMetamodel entityMetamodel, PersistentClass mappingInfo) {
		this.entityMetamodel = entityMetamodel;

		if ( !entityMetamodel.getIdentifierProperty().isVirtual() ) {
			idGetter = buildPropertyGetter( mappingInfo.getIdentifierProperty(), mappingInfo );
			idSetter = buildPropertySetter( mappingInfo.getIdentifierProperty(), mappingInfo );
		}
		else {
			idGetter = null;
			idSetter = null;
		}

		propertySpan = entityMetamodel.getPropertySpan();

        getters = new Getter[propertySpan];
		setters = new Setter[propertySpan];

		Iterator itr = mappingInfo.getPropertyClosureIterator();
		boolean foundCustomAccessor=false;
		int i=0;
		while ( itr.hasNext() ) {
			//TODO: redesign how PropertyAccessors are acquired...
			Property property = (Property) itr.next();
			getters[i] = buildPropertyGetter(property, mappingInfo);
			setters[i] = buildPropertySetter(property, mappingInfo);
			if ( !property.isBasicPropertyAccessor() ) {
				foundCustomAccessor = true;
			}
			i++;
		}
		hasCustomAccessors = foundCustomAccessor;

        instantiator = buildInstantiator( mappingInfo );

		if ( entityMetamodel.isLazy() ) {
			proxyFactory = buildProxyFactory( mappingInfo, idGetter, idSetter );
			if (proxyFactory == null) {
				entityMetamodel.setLazy( false );
			}
		}
		else {
			proxyFactory = null;
		}

		Component mapper = mappingInfo.getIdentifierMapper();
		if ( mapper == null ) {
			identifierMapperType = null;
			mappedIdentifierValueMarshaller = null;
		}
		else {
			identifierMapperType = (CompositeType) mapper.getType();
			mappedIdentifierValueMarshaller = buildMappedIdentifierValueMarshaller(
					(ComponentType) entityMetamodel.getIdentifierProperty().getType(),
					(ComponentType) identifierMapperType
			);
		}
	}
