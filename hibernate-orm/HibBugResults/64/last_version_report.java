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
