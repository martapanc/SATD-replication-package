diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index 740cce421e..820db3c5b0 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -1928,1448 +1928,1449 @@ public final class AnnotationBinder {
 						Formula.class
 				) ) {
 					Column ann = property.getAnnotation( Column.class );
 					Formula formulaAnn = property.getAnnotation( Formula.class );
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							new Column[] { ann },
 							formulaAnn,
 							nullability,
 							propertyHolder,
 							virtualProperty,
 							entityBinder.getSecondaryTables(),
 							context
 					);
 				}
 				else if ( property.isAnnotationPresent( Columns.class ) ) {
 					Columns anns = property.getAnnotation( Columns.class );
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							anns.columns(),
 							null,
 							nullability,
 							propertyHolder,
 							virtualProperty,
 							entityBinder.getSecondaryTables(),
 							context
 					);
 				}
 				else {
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							null,
 							null,
 							nullability,
 							propertyHolder,
 							virtualProperty,
 							entityBinder.getSecondaryTables(),
 							context
 					);
 				}
 				{
 					Column[] keyColumns = null;
 					//JPA 2 has priority and has different default column values, differenciate legacy from JPA 2
 					Boolean isJPA2 = null;
 					if ( property.isAnnotationPresent( MapKeyColumn.class ) ) {
 						isJPA2 = Boolean.TRUE;
 						keyColumns = new Column[] { new MapKeyColumnDelegator( property.getAnnotation( MapKeyColumn.class ) ) };
 					}
 
 					//not explicitly legacy
 					if ( isJPA2 == null ) {
 						isJPA2 = Boolean.TRUE;
 					}
 
 					//nullify empty array
 					keyColumns = keyColumns != null && keyColumns.length > 0 ? keyColumns : null;
 
 					//"mapkey" is the legacy column name of the key column pre JPA 2
 					PropertyData mapKeyVirtualProperty = new WrappedInferredData( inferredData, "mapkey" );
 					Ejb3Column[] mapColumns = Ejb3Column.buildColumnFromAnnotation(
 							keyColumns,
 							null,
 							Nullability.FORCED_NOT_NULL,
 							propertyHolder,
 							isJPA2 ? inferredData : mapKeyVirtualProperty,
 							isJPA2 ? "_KEY" : null,
 							entityBinder.getSecondaryTables(),
 							context
 					);
 					collectionBinder.setMapKeyColumns( mapColumns );
 				}
 				{
 					JoinColumn[] joinKeyColumns = null;
 					//JPA 2 has priority and has different default column values, differenciate legacy from JPA 2
 					Boolean isJPA2 = null;
 					if ( property.isAnnotationPresent( MapKeyJoinColumns.class ) ) {
 						isJPA2 = Boolean.TRUE;
 						final MapKeyJoinColumn[] mapKeyJoinColumns = property.getAnnotation( MapKeyJoinColumns.class )
 								.value();
 						joinKeyColumns = new JoinColumn[mapKeyJoinColumns.length];
 						int index = 0;
 						for ( MapKeyJoinColumn joinColumn : mapKeyJoinColumns ) {
 							joinKeyColumns[index] = new MapKeyJoinColumnDelegator( joinColumn );
 							index++;
 						}
 						if ( property.isAnnotationPresent( MapKeyJoinColumn.class ) ) {
 							throw new AnnotationException(
 									"@MapKeyJoinColumn and @MapKeyJoinColumns used on the same property: "
 											+ BinderHelper.getPath( propertyHolder, inferredData )
 							);
 						}
 					}
 					else if ( property.isAnnotationPresent( MapKeyJoinColumn.class ) ) {
 						isJPA2 = Boolean.TRUE;
 						joinKeyColumns = new JoinColumn[] {
 								new MapKeyJoinColumnDelegator(
 										property.getAnnotation(
 												MapKeyJoinColumn.class
 										)
 								)
 						};
 					}
 					//not explicitly legacy
 					if ( isJPA2 == null ) {
 						isJPA2 = Boolean.TRUE;
 					}
 
 					PropertyData mapKeyVirtualProperty = new WrappedInferredData( inferredData, "mapkey" );
 					Ejb3JoinColumn[] mapJoinColumns = Ejb3JoinColumn.buildJoinColumnsWithDefaultColumnSuffix(
 							joinKeyColumns,
 							null,
 							entityBinder.getSecondaryTables(),
 							propertyHolder,
 							isJPA2 ? inferredData.getPropertyName() : mapKeyVirtualProperty.getPropertyName(),
 							isJPA2 ? "_KEY" : null,
 							context
 					);
 					collectionBinder.setMapKeyManyToManyColumns( mapJoinColumns );
 				}
 
 				//potential element
 				collectionBinder.setEmbedded( property.isAnnotationPresent( Embedded.class ) );
 				collectionBinder.setElementColumns( elementColumns );
 				collectionBinder.setProperty( property );
 
 				//TODO enhance exception with @ManyToAny and @CollectionOfElements
 				if ( oneToManyAnn != null && manyToManyAnn != null ) {
 					throw new AnnotationException(
 							"@OneToMany and @ManyToMany on the same property is not allowed: "
 									+ propertyHolder.getEntityName() + "." + inferredData.getPropertyName()
 					);
 				}
 				String mappedBy = null;
 				if ( oneToManyAnn != null ) {
 					for ( Ejb3JoinColumn column : joinColumns ) {
 						if ( column.isSecondary() ) {
 							throw new NotYetImplementedException( "Collections having FK in secondary table" );
 						}
 					}
 					collectionBinder.setFkJoinColumns( joinColumns );
 					mappedBy = oneToManyAnn.mappedBy();
 					collectionBinder.setTargetEntity(
 							context.getBuildingOptions().getReflectionManager().toXClass( oneToManyAnn.targetEntity() )
 					);
 					collectionBinder.setCascadeStrategy(
 							getCascadeStrategy(
 									oneToManyAnn.cascade(), hibernateCascade, oneToManyAnn.orphanRemoval(), false
 							)
 					);
 					collectionBinder.setOneToMany( true );
 				}
 				else if ( elementCollectionAnn != null ) {
 					for ( Ejb3JoinColumn column : joinColumns ) {
 						if ( column.isSecondary() ) {
 							throw new NotYetImplementedException( "Collections having FK in secondary table" );
 						}
 					}
 					collectionBinder.setFkJoinColumns( joinColumns );
 					mappedBy = "";
 					final Class<?> targetElement = elementCollectionAnn.targetClass();
 					collectionBinder.setTargetEntity(
 							context.getBuildingOptions().getReflectionManager().toXClass( targetElement )
 					);
 					//collectionBinder.setCascadeStrategy( getCascadeStrategy( embeddedCollectionAnn.cascade(), hibernateCascade ) );
 					collectionBinder.setOneToMany( true );
 				}
 				else if ( manyToManyAnn != null ) {
 					mappedBy = manyToManyAnn.mappedBy();
 					collectionBinder.setTargetEntity(
 							context.getBuildingOptions().getReflectionManager().toXClass( manyToManyAnn.targetEntity() )
 					);
 					collectionBinder.setCascadeStrategy(
 							getCascadeStrategy(
 									manyToManyAnn.cascade(), hibernateCascade, false, false
 							)
 					);
 					collectionBinder.setOneToMany( false );
 				}
 				else if ( property.isAnnotationPresent( ManyToAny.class ) ) {
 					mappedBy = "";
 					collectionBinder.setTargetEntity(
 							context.getBuildingOptions().getReflectionManager().toXClass( void.class )
 					);
 					collectionBinder.setCascadeStrategy( getCascadeStrategy( null, hibernateCascade, false, false ) );
 					collectionBinder.setOneToMany( false );
 				}
 				collectionBinder.setMappedBy( mappedBy );
 
 				bindJoinedTableAssociation(
 						property,
 						context,
 						entityBinder,
 						collectionBinder,
 						propertyHolder,
 						inferredData,
 						mappedBy
 				);
 
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				collectionBinder.setCascadeDeleteEnabled( onDeleteCascade );
 				if ( isIdentifierMapper ) {
 					collectionBinder.setInsertable( false );
 					collectionBinder.setUpdatable( false );
 				}
 				if ( property.isAnnotationPresent( CollectionId.class ) ) { //do not compute the generators unless necessary
 					HashMap<String, IdentifierGeneratorDefinition> localGenerators = ( HashMap<String, IdentifierGeneratorDefinition> ) classGenerators.clone();
 					localGenerators.putAll( buildLocalGenerators( property, context ) );
 					collectionBinder.setLocalGenerators( localGenerators );
 
 				}
 				collectionBinder.setInheritanceStatePerClass( inheritanceStatePerClass );
 				collectionBinder.setDeclaringClass( inferredData.getDeclaringClass() );
 				collectionBinder.bind();
 
 			}
 			//Either a regular property or a basic @Id or @EmbeddedId while not ignoring id annotations
 			else if ( !isId || !entityBinder.isIgnoreIdAnnotations() ) {
 				//define whether the type is a component or not
 
 				boolean isComponent = false;
 
 				//Overrides from @MapsId if needed
 				boolean isOverridden = false;
 				if ( isId || propertyHolder.isOrWithinEmbeddedId() || propertyHolder.isInIdClass() ) {
 					//the associated entity could be using an @IdClass making the overridden property a component
 					final PropertyData overridingProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 							isId,
 							propertyHolder,
 							property.getName(),
 							context
 					);
 					if ( overridingProperty != null ) {
 						isOverridden = true;
 						final InheritanceState state = inheritanceStatePerClass.get( overridingProperty.getClassOrElement() );
 						if ( state != null ) {
 							isComponent = isComponent || state.hasIdClassOrEmbeddedId();
 						}
 						//Get the new column
 						columns = columnsBuilder.overrideColumnFromMapperOrMapsIdProperty( isId );
 					}
 				}
 
 				isComponent = isComponent
 						|| property.isAnnotationPresent( Embedded.class )
 						|| property.isAnnotationPresent( EmbeddedId.class )
 						|| returnedClass.isAnnotationPresent( Embeddable.class );
 
 
 				if ( isComponent ) {
 					String referencedEntityName = null;
 					if ( isOverridden ) {
 						final PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 								isId, propertyHolder, property.getName(), context
 						);
 						referencedEntityName = mapsIdProperty.getClassOrElementName();
 					}
 					AccessType propertyAccessor = entityBinder.getPropertyAccessor( property );
 					propertyBinder = bindComponent(
 							inferredData,
 							propertyHolder,
 							propertyAccessor,
 							entityBinder,
 							isIdentifierMapper,
 							context,
 							isComponentEmbedded,
 							isId,
 							inheritanceStatePerClass,
 							referencedEntityName,
 							isOverridden ? ( Ejb3JoinColumn[] ) columns : null
 					);
 				}
 				else {
 					//provide the basic property mapping
 					boolean optional = true;
 					boolean lazy = false;
 					if ( property.isAnnotationPresent( Basic.class ) ) {
 						Basic ann = property.getAnnotation( Basic.class );
 						optional = ann.optional();
 						lazy = ann.fetch() == FetchType.LAZY;
 					}
 					//implicit type will check basic types and Serializable classes
 					if ( isId || ( !optional && nullability != Nullability.FORCED_NULL ) ) {
 						//force columns to not null
 						for ( Ejb3Column col : columns ) {
 							if ( isId && col.isFormula() ) {
 								throw new CannotForceNonNullableException(
 										String.format(
 												Locale.ROOT,
 												"Identifier property [%s] cannot contain formula mapping [%s]",
 												HCANNHelper.annotatedElementSignature( property ),
 												col.getFormulaString()
 										)
 								);
 							}
 							col.forceNotNull();
 						}
 					}
 
 					propertyBinder.setLazy( lazy );
 					propertyBinder.setColumns( columns );
 					if ( isOverridden ) {
 						final PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 								isId, propertyHolder, property.getName(), context
 						);
 						propertyBinder.setReferencedEntityName( mapsIdProperty.getClassOrElementName() );
 					}
 
 					propertyBinder.makePropertyValueAndBind();
 
 				}
 				if ( isOverridden ) {
 					final PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 							isId, propertyHolder, property.getName(), context
 					);
 					Map<String, IdentifierGeneratorDefinition> localGenerators = ( HashMap<String, IdentifierGeneratorDefinition> ) classGenerators.clone();
 					final IdentifierGeneratorDefinition.Builder foreignGeneratorBuilder = new IdentifierGeneratorDefinition.Builder();
 					foreignGeneratorBuilder.setName( "Hibernate-local--foreign generator" );
 					foreignGeneratorBuilder.setStrategy( "foreign" );
 					foreignGeneratorBuilder.addParam( "property", mapsIdProperty.getPropertyName() );
 
 					final IdentifierGeneratorDefinition foreignGenerator = foreignGeneratorBuilder.build();
 					localGenerators.put( foreignGenerator.getName(), foreignGenerator );
 
 					BinderHelper.makeIdGenerator(
 							( SimpleValue ) propertyBinder.getValue(),
 							foreignGenerator.getStrategy(),
 							foreignGenerator.getName(),
 							context,
 							localGenerators
 					);
 				}
 				if ( isId ) {
 					//components and regular basic types create SimpleValue objects
 					final SimpleValue value = ( SimpleValue ) propertyBinder.getValue();
 					if ( !isOverridden ) {
 						processId(
 								propertyHolder,
 								inferredData,
 								value,
 								classGenerators,
 								isIdentifierMapper,
 								context
 						);
 					}
 				}
 			}
 		}
 		//init index
 		//process indexes afterQuery everything: in second pass, many to one has to be done beforeQuery indexes
 		Index index = property.getAnnotation( Index.class );
 		if ( index != null ) {
 			if ( joinColumns != null ) {
 
 				for ( Ejb3Column column : joinColumns ) {
 					column.addIndex( index, inSecondPass );
 				}
 			}
 			else {
 				if ( columns != null ) {
 					for ( Ejb3Column column : columns ) {
 						column.addIndex( index, inSecondPass );
 					}
 				}
 			}
 		}
 
 		// Natural ID columns must reside in one single UniqueKey within the Table.
 		// For now, simply ensure consistent naming.
 		// TODO: AFAIK, there really isn't a reason for these UKs to be created
 		// on the secondPass.  This whole area should go away...
 		NaturalId naturalIdAnn = property.getAnnotation( NaturalId.class );
 		if ( naturalIdAnn != null ) {
 			if ( joinColumns != null ) {
 				for ( Ejb3Column column : joinColumns ) {
 					String keyName = "UK_" + Constraint.hashedName( column.getTable().getName() + "_NaturalID" );
 					column.addUniqueKey( keyName, inSecondPass );
 				}
 			}
 			else {
 				for ( Ejb3Column column : columns ) {
 					String keyName = "UK_" + Constraint.hashedName( column.getTable().getName() + "_NaturalID" );
 					column.addUniqueKey( keyName, inSecondPass );
 				}
 			}
 		}
 	}
 
 	private static void setVersionInformation(XProperty property, PropertyBinder propertyBinder) {
 		propertyBinder.getSimpleValueBinder().setVersion( true );
 		if(property.isAnnotationPresent( Source.class )) {
 			Source source = property.getAnnotation( Source.class );
 			propertyBinder.getSimpleValueBinder().setTimestampVersionType( source.value().typeName() );
 		}
 	}
 
 	private static void processId(
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			SimpleValue idValue,
 			HashMap<String, IdentifierGeneratorDefinition> classGenerators,
 			boolean isIdentifierMapper,
 			MetadataBuildingContext buildingContext) {
 		if ( isIdentifierMapper ) {
 			throw new AnnotationException(
 					"@IdClass class should not have @Id nor @EmbeddedId properties: "
 							+ BinderHelper.getPath( propertyHolder, inferredData )
 			);
 		}
 		XClass returnedClass = inferredData.getClassOrElement();
 		XProperty property = inferredData.getProperty();
 		//clone classGenerator and override with local values
 		HashMap<String, IdentifierGeneratorDefinition> localGenerators = ( HashMap<String, IdentifierGeneratorDefinition> ) classGenerators.clone();
 		localGenerators.putAll( buildLocalGenerators( property, buildingContext ) );
 
 		//manage composite related metadata
 		//guess if its a component and find id data access (property, field etc)
 		final boolean isComponent = returnedClass.isAnnotationPresent( Embeddable.class )
 				|| property.isAnnotationPresent( EmbeddedId.class );
 
 		GeneratedValue generatedValue = property.getAnnotation( GeneratedValue.class );
 		String generatorType = generatedValue != null
 				? generatorType( generatedValue.strategy(), buildingContext, returnedClass )
 				: "assigned";
 		String generatorName = generatedValue != null
 				? generatedValue.generator()
 				: BinderHelper.ANNOTATION_STRING_DEFAULT;
 		if ( isComponent ) {
 			//a component must not have any generator
 			generatorType = "assigned";
 		}
 		BinderHelper.makeIdGenerator( idValue, generatorType, generatorName, buildingContext, localGenerators );
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Bind {0} on {1}", ( isComponent ? "@EmbeddedId" : "@Id" ), inferredData.getPropertyName() );
 		}
 	}
 
 	//TODO move that to collection binder?
 
 	private static void bindJoinedTableAssociation(
 			XProperty property,
 			MetadataBuildingContext buildingContext,
 			EntityBinder entityBinder,
 			CollectionBinder collectionBinder,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			String mappedBy) {
 		TableBinder associationTableBinder = new TableBinder();
 		JoinColumn[] annJoins;
 		JoinColumn[] annInverseJoins;
 		JoinTable assocTable = propertyHolder.getJoinTable( property );
 		CollectionTable collectionTable = property.getAnnotation( CollectionTable.class );
 		if ( assocTable != null || collectionTable != null ) {
 
 			final String catalog;
 			final String schema;
 			final String tableName;
 			final UniqueConstraint[] uniqueConstraints;
 			final JoinColumn[] joins;
 			final JoinColumn[] inverseJoins;
 			final javax.persistence.Index[] jpaIndexes;
 
 
 			//JPA 2 has priority
 			if ( collectionTable != null ) {
 				catalog = collectionTable.catalog();
 				schema = collectionTable.schema();
 				tableName = collectionTable.name();
 				uniqueConstraints = collectionTable.uniqueConstraints();
 				joins = collectionTable.joinColumns();
 				inverseJoins = null;
 				jpaIndexes = collectionTable.indexes();
 			}
 			else {
 				catalog = assocTable.catalog();
 				schema = assocTable.schema();
 				tableName = assocTable.name();
 				uniqueConstraints = assocTable.uniqueConstraints();
 				joins = assocTable.joinColumns();
 				inverseJoins = assocTable.inverseJoinColumns();
 				jpaIndexes = assocTable.indexes();
 			}
 
 			collectionBinder.setExplicitAssociationTable( true );
 			if ( jpaIndexes != null && jpaIndexes.length > 0 ) {
 				associationTableBinder.setJpaIndex( jpaIndexes );
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( schema ) ) {
 				associationTableBinder.setSchema( schema );
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( catalog ) ) {
 				associationTableBinder.setCatalog( catalog );
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( tableName ) ) {
 				associationTableBinder.setName( tableName );
 			}
 			associationTableBinder.setUniqueConstraints( uniqueConstraints );
 			associationTableBinder.setJpaIndex( jpaIndexes );
 			//set check constaint in the second pass
 			annJoins = joins.length == 0 ? null : joins;
 			annInverseJoins = inverseJoins == null || inverseJoins.length == 0 ? null : inverseJoins;
 		}
 		else {
 			annJoins = null;
 			annInverseJoins = null;
 		}
 		Ejb3JoinColumn[] joinColumns = Ejb3JoinColumn.buildJoinTableJoinColumns(
 				annJoins,
 				entityBinder.getSecondaryTables(),
 				propertyHolder,
 				inferredData.getPropertyName(),
 				mappedBy,
 				buildingContext
 		);
 		Ejb3JoinColumn[] inverseJoinColumns = Ejb3JoinColumn.buildJoinTableJoinColumns(
 				annInverseJoins,
 				entityBinder.getSecondaryTables(),
 				propertyHolder,
 				inferredData.getPropertyName(),
 				mappedBy,
 				buildingContext
 		);
 		associationTableBinder.setBuildingContext( buildingContext );
 		collectionBinder.setTableBinder( associationTableBinder );
 		collectionBinder.setJoinColumns( joinColumns );
 		collectionBinder.setInverseJoinColumns( inverseJoinColumns );
 	}
 
 	private static PropertyBinder bindComponent(
 			PropertyData inferredData,
 			PropertyHolder propertyHolder,
 			AccessType propertyAccessor,
 			EntityBinder entityBinder,
 			boolean isIdentifierMapper,
 			MetadataBuildingContext buildingContext,
 			boolean isComponentEmbedded,
 			boolean isId, //is a identifier
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			String referencedEntityName, //is a component who is overridden by a @MapsId
 			Ejb3JoinColumn[] columns) {
 		Component comp;
 		if ( referencedEntityName != null ) {
 			comp = createComponent( propertyHolder, inferredData, isComponentEmbedded, isIdentifierMapper, buildingContext );
 			SecondPass sp = new CopyIdentifierComponentSecondPass(
 					comp,
 					referencedEntityName,
 					columns,
 					buildingContext
 			);
 			buildingContext.getMetadataCollector().addSecondPass( sp );
 		}
 		else {
 			comp = fillComponent(
 					propertyHolder, inferredData, propertyAccessor, !isId, entityBinder,
 					isComponentEmbedded, isIdentifierMapper,
 					false, buildingContext, inheritanceStatePerClass
 			);
 		}
 		if ( isId ) {
 			comp.setKey( true );
 			if ( propertyHolder.getPersistentClass().getIdentifier() != null ) {
 				throw new AnnotationException(
 						comp.getComponentClassName()
 								+ " must not have @Id properties when used as an @EmbeddedId: "
 								+ BinderHelper.getPath( propertyHolder, inferredData )
 				);
 			}
 			if ( referencedEntityName == null && comp.getPropertySpan() == 0 ) {
 				throw new AnnotationException(
 						comp.getComponentClassName()
 								+ " has no persistent id property: "
 								+ BinderHelper.getPath( propertyHolder, inferredData )
 				);
 			}
 		}
 		XProperty property = inferredData.getProperty();
 		setupComponentTuplizer( property, comp );
 		PropertyBinder binder = new PropertyBinder();
 		binder.setDeclaringClass(inferredData.getDeclaringClass());
 		binder.setName( inferredData.getPropertyName() );
 		binder.setValue( comp );
 		binder.setProperty( inferredData.getProperty() );
 		binder.setAccessType( inferredData.getDefaultAccess() );
 		binder.setEmbedded( isComponentEmbedded );
 		binder.setHolder( propertyHolder );
 		binder.setId( isId );
 		binder.setEntityBinder( entityBinder );
 		binder.setInheritanceStatePerClass( inheritanceStatePerClass );
 		binder.setBuildingContext( buildingContext );
 		binder.makePropertyAndBind();
 		return binder;
 	}
 
 	public static Component fillComponent(
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			AccessType propertyAccessor,
 			boolean isNullable,
 			EntityBinder entityBinder,
 			boolean isComponentEmbedded,
 			boolean isIdentifierMapper,
 			boolean inSecondPass,
 			MetadataBuildingContext buildingContext,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		return fillComponent(
 				propertyHolder,
 				inferredData,
 				null,
 				propertyAccessor,
 				isNullable,
 				entityBinder,
 				isComponentEmbedded,
 				isIdentifierMapper,
 				inSecondPass,
 				buildingContext,
 				inheritanceStatePerClass
 		);
 	}
 
 	public static Component fillComponent(
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			PropertyData baseInferredData, //base inferred data correspond to the entity reproducing inferredData's properties (ie IdClass)
 			AccessType propertyAccessor,
 			boolean isNullable,
 			EntityBinder entityBinder,
 			boolean isComponentEmbedded,
 			boolean isIdentifierMapper,
 			boolean inSecondPass,
 			MetadataBuildingContext buildingContext,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		/**
 		 * inSecondPass can only be used to apply right away the second pass of a composite-element
 		 * Because it's a value type, there is no bidirectional association, hence second pass
 		 * ordering does not matter
 		 */
 		Component comp = createComponent( propertyHolder, inferredData, isComponentEmbedded, isIdentifierMapper, buildingContext );
 		String subpath = BinderHelper.getPath( propertyHolder, inferredData );
 		LOG.tracev( "Binding component with path: {0}", subpath );
 		PropertyHolder subHolder = PropertyHolderBuilder.buildPropertyHolder(
 				comp,
 				subpath,
 				inferredData,
 				propertyHolder,
 				buildingContext
 		);
 
 
 		// propertyHolder here is the owner of the component property.  Tell it we are about to start the component...
 
 		propertyHolder.startingProperty( inferredData.getProperty() );
 
 		final XClass xClassProcessed = inferredData.getPropertyClass();
 		List<PropertyData> classElements = new ArrayList<PropertyData>();
 		XClass returnedClassOrElement = inferredData.getClassOrElement();
 
 		List<PropertyData> baseClassElements = null;
 		Map<String, PropertyData> orderedBaseClassElements = new HashMap<String, PropertyData>();
 		XClass baseReturnedClassOrElement;
 		if ( baseInferredData != null ) {
 			baseClassElements = new ArrayList<PropertyData>();
 			baseReturnedClassOrElement = baseInferredData.getClassOrElement();
 			bindTypeDefs( baseReturnedClassOrElement, buildingContext );
 			PropertyContainer propContainer = new PropertyContainer( baseReturnedClassOrElement, xClassProcessed, propertyAccessor );
 			addElementsOfClass( baseClassElements,  propContainer, buildingContext );
 			for ( PropertyData element : baseClassElements ) {
 				orderedBaseClassElements.put( element.getPropertyName(), element );
 			}
 		}
 
 		//embeddable elements can have type defs
 		bindTypeDefs( returnedClassOrElement, buildingContext );
 		PropertyContainer propContainer = new PropertyContainer( returnedClassOrElement, xClassProcessed, propertyAccessor );
 		addElementsOfClass( classElements, propContainer, buildingContext );
 
 		//add elements of the embeddable superclass
 		XClass superClass = xClassProcessed.getSuperclass();
 		while ( superClass != null && superClass.isAnnotationPresent( MappedSuperclass.class ) ) {
 			//FIXME: proper support of typevariables incl var resolved at upper levels
 			propContainer = new PropertyContainer( superClass, xClassProcessed, propertyAccessor );
 			addElementsOfClass( classElements, propContainer, buildingContext );
 			superClass = superClass.getSuperclass();
 		}
 		if ( baseClassElements != null ) {
 			//useful to avoid breaking pre JPA 2 mappings
 			if ( !hasAnnotationsOnIdClass( xClassProcessed ) ) {
 				for ( int i = 0; i < classElements.size(); i++ ) {
 					final PropertyData idClassPropertyData = classElements.get( i );
 					final PropertyData entityPropertyData = orderedBaseClassElements.get( idClassPropertyData.getPropertyName() );
 					if ( propertyHolder.isInIdClass() ) {
 						if ( entityPropertyData == null ) {
 							throw new AnnotationException(
 									"Property of @IdClass not found in entity "
 											+ baseInferredData.getPropertyClass().getName() + ": "
 											+ idClassPropertyData.getPropertyName()
 							);
 						}
 						final boolean hasXToOneAnnotation = entityPropertyData.getProperty()
 								.isAnnotationPresent( ManyToOne.class )
 								|| entityPropertyData.getProperty().isAnnotationPresent( OneToOne.class );
 						final boolean isOfDifferentType = !entityPropertyData.getClassOrElement()
 								.equals( idClassPropertyData.getClassOrElement() );
 						if ( hasXToOneAnnotation && isOfDifferentType ) {
 							//don't replace here as we need to use the actual original return type
 							//the annotation overriding will be dealt with by a mechanism similar to @MapsId
 						}
 						else {
 							classElements.set( i, entityPropertyData );  //this works since they are in the same order
 						}
 					}
 					else {
 						classElements.set( i, entityPropertyData );  //this works since they are in the same order
 					}
 				}
 			}
 		}
 		for ( PropertyData propertyAnnotatedElement : classElements ) {
 			processElementAnnotations(
 					subHolder,
 					isNullable
 							? Nullability.NO_CONSTRAINT
 							: Nullability.FORCED_NOT_NULL,
 					propertyAnnotatedElement,
 					new HashMap<String, IdentifierGeneratorDefinition>(),
 					entityBinder,
 					isIdentifierMapper,
 					isComponentEmbedded,
 					inSecondPass,
 					buildingContext,
 					inheritanceStatePerClass
 			);
 
 			XProperty property = propertyAnnotatedElement.getProperty();
 			if ( property.isAnnotationPresent( GeneratedValue.class ) &&
 					property.isAnnotationPresent( Id.class ) ) {
 				//clone classGenerator and override with local values
 				Map<String, IdentifierGeneratorDefinition> localGenerators = new HashMap<String, IdentifierGeneratorDefinition>();
 				localGenerators.putAll( buildLocalGenerators( property, buildingContext ) );
 
 				GeneratedValue generatedValue = property.getAnnotation( GeneratedValue.class );
 				String generatorType = generatedValue != null
 						? generatorType( generatedValue.strategy(), buildingContext, property.getType() )
 						: "assigned";
 				String generator = generatedValue != null ? generatedValue.generator() : BinderHelper.ANNOTATION_STRING_DEFAULT;
 
 				BinderHelper.makeIdGenerator(
 						( SimpleValue ) comp.getProperty( property.getName() ).getValue(),
 						generatorType,
 						generator,
 						buildingContext,
 						localGenerators
 				);
 			}
 
 		}
 		return comp;
 	}
 
 	public static Component createComponent(
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			boolean isComponentEmbedded,
 			boolean isIdentifierMapper,
 			MetadataBuildingContext context) {
 		Component comp = new Component( context.getMetadataCollector(), propertyHolder.getPersistentClass() );
 		comp.setEmbedded( isComponentEmbedded );
 		//yuk
 		comp.setTable( propertyHolder.getTable() );
 		//FIXME shouldn't identifier mapper use getClassOrElementName? Need to be checked.
 		if ( isIdentifierMapper || ( isComponentEmbedded && inferredData.getPropertyName() == null ) ) {
 			comp.setComponentClassName( comp.getOwner().getClassName() );
 		}
 		else {
 			comp.setComponentClassName( inferredData.getClassOrElementName() );
 		}
 		return comp;
 	}
 
 	private static void bindIdClass(
 			String generatorType,
 			String generatorName,
 			PropertyData inferredData,
 			PropertyData baseInferredData,
 			Ejb3Column[] columns,
 			PropertyHolder propertyHolder,
 			boolean isComposite,
 			AccessType propertyAccessor,
 			EntityBinder entityBinder,
 			boolean isEmbedded,
 			boolean isIdentifierMapper,
 			MetadataBuildingContext buildingContext,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 
 		/*
 		 * Fill simple value and property since and Id is a property
 		 */
 		PersistentClass persistentClass = propertyHolder.getPersistentClass();
 		if ( !( persistentClass instanceof RootClass ) ) {
 			throw new AnnotationException(
 					"Unable to define/override @Id(s) on a subclass: "
 							+ propertyHolder.getEntityName()
 			);
 		}
 		RootClass rootClass = ( RootClass ) persistentClass;
 		String persistentClassName = rootClass.getClassName();
 		SimpleValue id;
 		final String propertyName = inferredData.getPropertyName();
 		HashMap<String, IdGenerator> localGenerators = new HashMap<String, IdGenerator>();
 		if ( isComposite ) {
 			id = fillComponent(
 					propertyHolder,
 					inferredData,
 					baseInferredData,
 					propertyAccessor,
 					false,
 					entityBinder,
 					isEmbedded,
 					isIdentifierMapper,
 					false,
 					buildingContext,
 					inheritanceStatePerClass
 			);
 			Component componentId = ( Component ) id;
 			componentId.setKey( true );
 			if ( rootClass.getIdentifier() != null ) {
 				throw new AnnotationException( componentId.getComponentClassName() + " must not have @Id properties when used as an @EmbeddedId" );
 			}
 			if ( componentId.getPropertySpan() == 0 ) {
 				throw new AnnotationException( componentId.getComponentClassName() + " has no persistent id property" );
 			}
 			//tuplizers
 			XProperty property = inferredData.getProperty();
 			setupComponentTuplizer( property, componentId );
 		}
 		else {
 			//TODO I think this branch is never used. Remove.
 
 			for ( Ejb3Column column : columns ) {
 				column.forceNotNull(); //this is an id
 			}
 			SimpleValueBinder value = new SimpleValueBinder();
 			value.setPropertyName( propertyName );
 			value.setReturnedClassName( inferredData.getTypeName() );
 			value.setColumns( columns );
 			value.setPersistentClassName( persistentClassName );
 			value.setBuildingContext( buildingContext );
 			value.setType( inferredData.getProperty(), inferredData.getClassOrElement(), persistentClassName, null );
 			value.setAccessType( propertyAccessor );
 			id = value.make();
 		}
 		rootClass.setIdentifier( id );
 		BinderHelper.makeIdGenerator(
 				id,
 				generatorType,
 				generatorName,
 				buildingContext,
 				Collections.<String, IdentifierGeneratorDefinition>emptyMap()
 		);
 		if ( isEmbedded ) {
 			rootClass.setEmbeddedIdentifier( inferredData.getPropertyClass() == null );
 		}
 		else {
 			PropertyBinder binder = new PropertyBinder();
 			binder.setName( propertyName );
 			binder.setValue( id );
 			binder.setAccessType( inferredData.getDefaultAccess() );
 			binder.setProperty( inferredData.getProperty() );
 			Property prop = binder.makeProperty();
 			rootClass.setIdentifierProperty( prop );
 			//if the id property is on a superclass, update the metamodel
 			final org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(
 					inferredData.getDeclaringClass(),
 					inheritanceStatePerClass,
 					buildingContext
 			);
 			if ( superclass != null ) {
 				superclass.setDeclaredIdentifierProperty( prop );
 			}
 			else {
 				//we know the property is on the actual entity
 				rootClass.setDeclaredIdentifierProperty( prop );
 			}
 		}
 	}
 
 	private static PropertyData getUniqueIdPropertyFromBaseClass(
 			PropertyData inferredData,
 			PropertyData baseInferredData,
 			AccessType propertyAccessor,
 			MetadataBuildingContext context) {
 		List<PropertyData> baseClassElements = new ArrayList<PropertyData>();
 		XClass baseReturnedClassOrElement = baseInferredData.getClassOrElement();
 		PropertyContainer propContainer = new PropertyContainer(
 				baseReturnedClassOrElement,
 				inferredData.getPropertyClass(),
 				propertyAccessor
 		);
 		addElementsOfClass( baseClassElements, propContainer, context );
 		//Id properties are on top and there is only one
 		return baseClassElements.get( 0 );
 	}
 
 	private static void setupComponentTuplizer(XProperty property, Component component) {
 		if ( property == null ) {
 			return;
 		}
 		if ( property.isAnnotationPresent( Tuplizers.class ) ) {
 			for ( Tuplizer tuplizer : property.getAnnotation( Tuplizers.class ).value() ) {
 				EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
 				//todo tuplizer.entityModeType
 				component.addTuplizer( mode, tuplizer.impl().getName() );
 			}
 		}
 		if ( property.isAnnotationPresent( Tuplizer.class ) ) {
 			Tuplizer tuplizer = property.getAnnotation( Tuplizer.class );
 			EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
 			//todo tuplizer.entityModeType
 			component.addTuplizer( mode, tuplizer.impl().getName() );
 		}
 	}
 
 	private static void bindManyToOne(
 			String cascadeStrategy,
 			Ejb3JoinColumn[] columns,
 			boolean optional,
 			boolean ignoreNotFound,
 			boolean cascadeOnDelete,
 			XClass targetEntity,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			boolean unique,
 			boolean isIdentifierMapper,
 			boolean inSecondPass,
 			PropertyBinder propertyBinder,
 			MetadataBuildingContext context) {
 		//All FK columns should be in the same table
 		org.hibernate.mapping.ManyToOne value = new org.hibernate.mapping.ManyToOne( context.getMetadataCollector(), columns[0].getTable() );
 		// This is a @OneToOne mapped to a physical o.h.mapping.ManyToOne
 		if ( unique ) {
 			value.markAsLogicalOneToOne();
 		}
 		value.setReferencedEntityName( ToOneBinder.getReferenceEntityName( inferredData, targetEntity, context ) );
 		final XProperty property = inferredData.getProperty();
 		defineFetchingStrategy( value, property );
 		//value.setFetchMode( fetchMode );
 		value.setIgnoreNotFound( ignoreNotFound );
 		value.setCascadeDeleteEnabled( cascadeOnDelete );
 		//value.setLazy( fetchMode != FetchMode.JOIN );
 		if ( !optional ) {
 			for ( Ejb3JoinColumn column : columns ) {
 				column.setNullable( false );
 			}
 		}
 		if ( property.isAnnotationPresent( MapsId.class ) ) {
 			//read only
 			for ( Ejb3JoinColumn column : columns ) {
 				column.setInsertable( false );
 				column.setUpdatable( false );
 			}
 		}
 		
 		final JoinColumn joinColumn = property.getAnnotation( JoinColumn.class );
 
 		//Make sure that JPA1 key-many-to-one columns are read only tooj
 		boolean hasSpecjManyToOne=false;
 		if ( context.getBuildingOptions().isSpecjProprietarySyntaxEnabled() ) {
 			String columnName = "";
 			for ( XProperty prop : inferredData.getDeclaringClass()
 					.getDeclaredProperties( AccessType.FIELD.getType() ) ) {
 				if ( prop.isAnnotationPresent( Id.class ) && prop.isAnnotationPresent( Column.class ) ) {
 					columnName = prop.getAnnotation( Column.class ).name();
 				}
 
 				if ( property.isAnnotationPresent( ManyToOne.class ) && joinColumn != null
 						&& ! BinderHelper.isEmptyAnnotationValue( joinColumn.name() )
 						&& joinColumn.name().equals( columnName )
 						&& !property.isAnnotationPresent( MapsId.class ) ) {
 					hasSpecjManyToOne = true;
 					for ( Ejb3JoinColumn column : columns ) {
 						column.setInsertable( false );
 						column.setUpdatable( false );
 					}
 				}
 			}
 
 		}
 		value.setTypeName( inferredData.getClassOrElementName() );
 		final String propertyName = inferredData.getPropertyName();
 		value.setTypeUsingReflection( propertyHolder.getClassName(), propertyName );
 
 		if ( joinColumn != null
 				&& joinColumn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT ) {
 			// not ideal...
 			value.setForeignKeyName( "none" );
 		}
 		else {
 			final ForeignKey fk = property.getAnnotation( ForeignKey.class );
 			if ( fk != null && StringHelper.isNotEmpty( fk.name() ) ) {
 				value.setForeignKeyName( fk.name() );
 			}
 			else if ( joinColumn != null ) {
 				value.setForeignKeyName( StringHelper.nullIfEmpty( joinColumn.foreignKey().name() ) );
+				value.setForeignKeyDefinition( StringHelper.nullIfEmpty( joinColumn.foreignKey().foreignKeyDefinition() ) );
 			}
 		}
 
 		String path = propertyHolder.getPath() + "." + propertyName;
 		FkSecondPass secondPass = new ToOneFkSecondPass(
 				value, columns,
 				!optional && unique, //cannot have nullable and unique on certain DBs like Derby
 				propertyHolder.getEntityOwnerClassName(),
 				path,
 				context
 		);
 		if ( inSecondPass ) {
 			secondPass.doSecondPass( context.getMetadataCollector().getEntityBindingMap() );
 		}
 		else {
 			context.getMetadataCollector().addSecondPass( secondPass );
 		}
 		Ejb3Column.checkPropertyConsistency( columns, propertyHolder.getEntityName() + "." + propertyName );
 		//PropertyBinder binder = new PropertyBinder();
 		propertyBinder.setName( propertyName );
 		propertyBinder.setValue( value );
 		//binder.setCascade(cascadeStrategy);
 		if ( isIdentifierMapper ) {
 			propertyBinder.setInsertable( false );
 			propertyBinder.setUpdatable( false );
 		}
 		else if (hasSpecjManyToOne) {
 			propertyBinder.setInsertable( false );
 			propertyBinder.setUpdatable( false );
 		}
 		else {
 			propertyBinder.setInsertable( columns[0].isInsertable() );
 			propertyBinder.setUpdatable( columns[0].isUpdatable() );
 		}
 		propertyBinder.setColumns( columns );
 		propertyBinder.setAccessType( inferredData.getDefaultAccess() );
 		propertyBinder.setCascade( cascadeStrategy );
 		propertyBinder.setProperty( property );
 		propertyBinder.setXToMany( true );
 		propertyBinder.makePropertyAndBind();
 	}
 
 	protected static void defineFetchingStrategy(ToOne toOne, XProperty property) {
 		LazyToOne lazy = property.getAnnotation( LazyToOne.class );
 		Fetch fetch = property.getAnnotation( Fetch.class );
 		ManyToOne manyToOne = property.getAnnotation( ManyToOne.class );
 		OneToOne oneToOne = property.getAnnotation( OneToOne.class );
 		FetchType fetchType;
 		if ( manyToOne != null ) {
 			fetchType = manyToOne.fetch();
 		}
 		else if ( oneToOne != null ) {
 			fetchType = oneToOne.fetch();
 		}
 		else {
 			throw new AssertionFailure(
 					"Define fetch strategy on a property not annotated with @OneToMany nor @OneToOne"
 			);
 		}
 		if ( lazy != null ) {
 			toOne.setLazy( !( lazy.value() == LazyToOneOption.FALSE ) );
 			toOne.setUnwrapProxy( ( lazy.value() == LazyToOneOption.NO_PROXY ) );
 		}
 		else {
 			toOne.setLazy( fetchType == FetchType.LAZY );
 			toOne.setUnwrapProxy( false );
 		}
 		if ( fetch != null ) {
 			if ( fetch.value() == org.hibernate.annotations.FetchMode.JOIN ) {
 				toOne.setFetchMode( FetchMode.JOIN );
 				toOne.setLazy( false );
 				toOne.setUnwrapProxy( false );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SELECT ) {
 				toOne.setFetchMode( FetchMode.SELECT );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SUBSELECT ) {
 				throw new AnnotationException( "Use of FetchMode.SUBSELECT not allowed on ToOne associations" );
 			}
 			else {
 				throw new AssertionFailure( "Unknown FetchMode: " + fetch.value() );
 			}
 		}
 		else {
 			toOne.setFetchMode( getFetchMode( fetchType ) );
 		}
 	}
 
 	private static void bindOneToOne(
 			String cascadeStrategy,
 			Ejb3JoinColumn[] joinColumns,
 			boolean optional,
 			FetchMode fetchMode,
 			boolean ignoreNotFound,
 			boolean cascadeOnDelete,
 			XClass targetEntity,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			String mappedBy,
 			boolean trueOneToOne,
 			boolean isIdentifierMapper,
 			boolean inSecondPass,
 			PropertyBinder propertyBinder,
 			MetadataBuildingContext context) {
 		//column.getTable() => persistentClass.getTable()
 		final String propertyName = inferredData.getPropertyName();
 		LOG.tracev( "Fetching {0} with {1}", propertyName, fetchMode );
 		boolean mapToPK = true;
 		if ( !trueOneToOne ) {
 			//try to find a hidden true one to one (FK == PK columns)
 			KeyValue identifier = propertyHolder.getIdentifier();
 			if ( identifier == null ) {
 				//this is a @OneToOne in a @EmbeddedId (the persistentClass.identifier is not set yet, it's being built)
 				//by definition the PK cannot refers to itself so it cannot map to itself
 				mapToPK = false;
 			}
 			else {
 				Iterator idColumns = identifier.getColumnIterator();
 				List<String> idColumnNames = new ArrayList<String>();
 				org.hibernate.mapping.Column currentColumn;
 				if ( identifier.getColumnSpan() != joinColumns.length ) {
 					mapToPK = false;
 				}
 				else {
 					while ( idColumns.hasNext() ) {
 						currentColumn = ( org.hibernate.mapping.Column ) idColumns.next();
 						idColumnNames.add( currentColumn.getName() );
 					}
 					for ( Ejb3JoinColumn col : joinColumns ) {
 						if ( !idColumnNames.contains( col.getMappingColumn().getName() ) ) {
 							mapToPK = false;
 							break;
 						}
 					}
 				}
 			}
 		}
 		if ( trueOneToOne || mapToPK || !BinderHelper.isEmptyAnnotationValue( mappedBy ) ) {
 			//is a true one-to-one
 			//FIXME referencedColumnName ignored => ordering may fail.
 			OneToOneSecondPass secondPass = new OneToOneSecondPass(
 					mappedBy,
 					propertyHolder.getEntityName(),
 					propertyName,
 					propertyHolder,
 					inferredData,
 					targetEntity,
 					ignoreNotFound,
 					cascadeOnDelete,
 					optional,
 					cascadeStrategy,
 					joinColumns,
 					context
 			);
 			if ( inSecondPass ) {
 				secondPass.doSecondPass( context.getMetadataCollector().getEntityBindingMap() );
 			}
 			else {
 				context.getMetadataCollector().addSecondPass(
 						secondPass,
 						BinderHelper.isEmptyAnnotationValue( mappedBy )
 				);
 			}
 		}
 		else {
 			//has a FK on the table
 			bindManyToOne(
 					cascadeStrategy, joinColumns, optional, ignoreNotFound, cascadeOnDelete,
 					targetEntity,
 					propertyHolder, inferredData, true, isIdentifierMapper, inSecondPass,
 					propertyBinder, context
 			);
 		}
 	}
 
 	private static void bindAny(
 			String cascadeStrategy,
 			Ejb3JoinColumn[] columns,
 			boolean cascadeOnDelete,
 			Nullability nullability,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			EntityBinder entityBinder,
 			boolean isIdentifierMapper,
 			MetadataBuildingContext buildingContext) {
 		org.hibernate.annotations.Any anyAnn = inferredData.getProperty()
 				.getAnnotation( org.hibernate.annotations.Any.class );
 		if ( anyAnn == null ) {
 			throw new AssertionFailure(
 					"Missing @Any annotation: "
 							+ BinderHelper.getPath( propertyHolder, inferredData )
 			);
 		}
 		Any value = BinderHelper.buildAnyValue(
 				anyAnn.metaDef(),
 				columns,
 				anyAnn.metaColumn(),
 				inferredData,
 				cascadeOnDelete,
 				nullability,
 				propertyHolder,
 				entityBinder,
 				anyAnn.optional(),
 				buildingContext
 		);
 
 		PropertyBinder binder = new PropertyBinder();
 		binder.setName( inferredData.getPropertyName() );
 		binder.setValue( value );
 
 		binder.setLazy( anyAnn.fetch() == FetchType.LAZY );
 		//binder.setCascade(cascadeStrategy);
 		if ( isIdentifierMapper ) {
 			binder.setInsertable( false );
 			binder.setUpdatable( false );
 		}
 		else {
 			binder.setInsertable( columns[0].isInsertable() );
 			binder.setUpdatable( columns[0].isUpdatable() );
 		}
 		binder.setAccessType( inferredData.getDefaultAccess() );
 		binder.setCascade( cascadeStrategy );
 		Property prop = binder.makeProperty();
 		//composite FK columns are in the same table so its OK
 		propertyHolder.addProperty( prop, columns, inferredData.getDeclaringClass() );
 	}
 
 	private static String generatorType(
 			GenerationType generatorEnum,
 			final MetadataBuildingContext buildingContext,
 			final XClass javaTypeXClass) {
 		return buildingContext.getBuildingOptions().getIdGenerationTypeInterpreter().determineGeneratorName(
 				generatorEnum,
 				new IdGeneratorStrategyInterpreter.GeneratorNameDeterminationContext() {
 					Class javaType = null;
 					@Override
 					public Class getIdType() {
 						if ( javaType == null ) {
 							javaType = buildingContext.getBuildingOptions()
 									.getReflectionManager()
 									.toClass( javaTypeXClass );
 						}
 						return javaType;
 					}
 				}
 		);
 	}
 
 	private static EnumSet<CascadeType> convertToHibernateCascadeType(javax.persistence.CascadeType[] ejbCascades) {
 		EnumSet<CascadeType> hibernateCascadeSet = EnumSet.noneOf( CascadeType.class );
 		if ( ejbCascades != null && ejbCascades.length > 0 ) {
 			for ( javax.persistence.CascadeType cascade : ejbCascades ) {
 				switch ( cascade ) {
 					case ALL:
 						hibernateCascadeSet.add( CascadeType.ALL );
 						break;
 					case PERSIST:
 						hibernateCascadeSet.add( CascadeType.PERSIST );
 						break;
 					case MERGE:
 						hibernateCascadeSet.add( CascadeType.MERGE );
 						break;
 					case REMOVE:
 						hibernateCascadeSet.add( CascadeType.REMOVE );
 						break;
 					case REFRESH:
 						hibernateCascadeSet.add( CascadeType.REFRESH );
 						break;
 					case DETACH:
 						hibernateCascadeSet.add( CascadeType.DETACH );
 						break;
 				}
 			}
 		}
 
 		return hibernateCascadeSet;
 	}
 
 	private static String getCascadeStrategy(
 			javax.persistence.CascadeType[] ejbCascades,
 			Cascade hibernateCascadeAnnotation,
 			boolean orphanRemoval,
 			boolean forcePersist) {
 		EnumSet<CascadeType> hibernateCascadeSet = convertToHibernateCascadeType( ejbCascades );
 		CascadeType[] hibernateCascades = hibernateCascadeAnnotation == null ?
 				null :
 				hibernateCascadeAnnotation.value();
 
 		if ( hibernateCascades != null && hibernateCascades.length > 0 ) {
 			hibernateCascadeSet.addAll( Arrays.asList( hibernateCascades ) );
 		}
 
 		if ( orphanRemoval ) {
 			hibernateCascadeSet.add( CascadeType.DELETE_ORPHAN );
 			hibernateCascadeSet.add( CascadeType.REMOVE );
 		}
 		if ( forcePersist ) {
 			hibernateCascadeSet.add( CascadeType.PERSIST );
 		}
 
 		StringBuilder cascade = new StringBuilder();
 		for ( CascadeType aHibernateCascadeSet : hibernateCascadeSet ) {
 			switch ( aHibernateCascadeSet ) {
 				case ALL:
 					cascade.append( "," ).append( "all" );
 					break;
 				case SAVE_UPDATE:
 					cascade.append( "," ).append( "save-update" );
 					break;
 				case PERSIST:
 					cascade.append( "," ).append( "persist" );
 					break;
 				case MERGE:
 					cascade.append( "," ).append( "merge" );
 					break;
 				case LOCK:
 					cascade.append( "," ).append( "lock" );
 					break;
 				case REFRESH:
 					cascade.append( "," ).append( "refresh" );
 					break;
 				case REPLICATE:
 					cascade.append( "," ).append( "replicate" );
 					break;
 				case EVICT:
 				case DETACH:
 					cascade.append( "," ).append( "evict" );
 					break;
 				case DELETE:
 					cascade.append( "," ).append( "delete" );
 					break;
 				case DELETE_ORPHAN:
 					cascade.append( "," ).append( "delete-orphan" );
 					break;
 				case REMOVE:
 					cascade.append( "," ).append( "delete" );
 					break;
 			}
 		}
 		return cascade.length() > 0 ?
 				cascade.substring( 1 ) :
 				"none";
 	}
 
 	public static FetchMode getFetchMode(FetchType fetch) {
 		if ( fetch == FetchType.EAGER ) {
 			return FetchMode.JOIN;
 		}
 		else {
 			return FetchMode.SELECT;
 		}
 	}
 
 	private static HashMap<String, IdentifierGeneratorDefinition> buildLocalGenerators(XAnnotatedElement annElt, MetadataBuildingContext context) {
 		HashMap<String, IdentifierGeneratorDefinition> generators = new HashMap<String, IdentifierGeneratorDefinition>();
 		TableGenerator tabGen = annElt.getAnnotation( TableGenerator.class );
 		SequenceGenerator seqGen = annElt.getAnnotation( SequenceGenerator.class );
 		GenericGenerator genGen = annElt.getAnnotation( GenericGenerator.class );
 		if ( tabGen != null ) {
 			IdentifierGeneratorDefinition idGen = buildIdGenerator( tabGen, context );
 			generators.put( idGen.getName(), idGen );
 		}
 		if ( seqGen != null ) {
 			IdentifierGeneratorDefinition idGen = buildIdGenerator( seqGen, context );
 			generators.put( idGen.getName(), idGen );
 		}
 		if ( genGen != null ) {
 			IdentifierGeneratorDefinition idGen = buildIdGenerator( genGen, context );
 			generators.put( idGen.getName(), idGen );
 		}
 		return generators;
 	}
 
 	public static boolean isDefault(XClass clazz, MetadataBuildingContext context) {
 		return context.getBuildingOptions().getReflectionManager().equals( clazz, void.class );
 	}
 
 	/**
 	 * For the mapped entities build some temporary data-structure containing information about the
 	 * inheritance status of a class.
 	 *
 	 * @param orderedClasses Order list of all annotated entities and their mapped superclasses
 	 *
 	 * @return A map of {@code InheritanceState}s keyed against their {@code XClass}.
 	 */
 	public static Map<XClass, InheritanceState> buildInheritanceStates(
 			List<XClass> orderedClasses,
 			MetadataBuildingContext buildingContext) {
 		ReflectionManager reflectionManager = buildingContext.getBuildingOptions().getReflectionManager();
 		Map<XClass, InheritanceState> inheritanceStatePerClass = new HashMap<XClass, InheritanceState>(
 				orderedClasses.size()
 		);
 		for ( XClass clazz : orderedClasses ) {
 			InheritanceState superclassState = InheritanceState.getSuperclassInheritanceState(
 					clazz, inheritanceStatePerClass
 			);
 			InheritanceState state = new InheritanceState( clazz, inheritanceStatePerClass, buildingContext );
 			if ( superclassState != null ) {
 				//the classes are ordered thus preventing an NPE
 				//FIXME if an entity has subclasses annotated @MappedSperclass wo sub @Entity this is wrong
 				superclassState.setHasSiblings( true );
 				InheritanceState superEntityState = InheritanceState.getInheritanceStateOfSuperEntity(
 						clazz, inheritanceStatePerClass
 				);
 				state.setHasParents( superEntityState != null );
 				final boolean nonDefault = state.getType() != null && !InheritanceType.SINGLE_TABLE
 						.equals( state.getType() );
 				if ( superclassState.getType() != null ) {
 					final boolean mixingStrategy = state.getType() != null && !state.getType()
 							.equals( superclassState.getType() );
 					if ( nonDefault && mixingStrategy ) {
 						LOG.invalidSubStrategy( clazz.getName() );
 					}
 					state.setType( superclassState.getType() );
 				}
 			}
 			inheritanceStatePerClass.put( clazz, state );
 		}
 		return inheritanceStatePerClass;
 	}
 
 	private static boolean hasAnnotationsOnIdClass(XClass idClass) {
 //		if(idClass.getAnnotation(Embeddable.class) != null)
 //			return true;
 
 		List<XProperty> properties = idClass.getDeclaredProperties( XClass.ACCESS_FIELD );
 		for ( XProperty property : properties ) {
 			if ( property.isAnnotationPresent( Column.class ) || property.isAnnotationPresent( OneToMany.class ) ||
 					property.isAnnotationPresent( ManyToOne.class ) || property.isAnnotationPresent( Id.class ) ||
 					property.isAnnotationPresent( GeneratedValue.class ) || property.isAnnotationPresent( OneToOne.class ) ||
 					property.isAnnotationPresent( ManyToMany.class )
 					) {
 				return true;
 			}
 		}
 		List<XMethod> methods = idClass.getDeclaredMethods();
 		for ( XMethod method : methods ) {
 			if ( method.isAnnotationPresent( Column.class ) || method.isAnnotationPresent( OneToMany.class ) ||
 					method.isAnnotationPresent( ManyToOne.class ) || method.isAnnotationPresent( Id.class ) ||
 					method.isAnnotationPresent( GeneratedValue.class ) || method.isAnnotationPresent( OneToOne.class ) ||
 					method.isAnnotationPresent( ManyToMany.class )
 					) {
 				return true;
 			}
 		}
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
index c5cdf59393..c3d64ae86c 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
@@ -1,310 +1,311 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg;
 
 import java.util.Iterator;
 import java.util.Map;
 
 import javax.persistence.ConstraintMode;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.ForeignKey;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.cfg.annotations.PropertyBinder;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.OneToOne;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.type.ForeignKeyDirection;
 
 /**
  * We have to handle OneToOne in a second pass because:
  * -
  */
 public class OneToOneSecondPass implements SecondPass {
 	private MetadataBuildingContext buildingContext;
 	private String mappedBy;
 	private String ownerEntity;
 	private String ownerProperty;
 	private PropertyHolder propertyHolder;
 	private boolean ignoreNotFound;
 	private PropertyData inferredData;
 	private XClass targetEntity;
 	private boolean cascadeOnDelete;
 	private boolean optional;
 	private String cascadeStrategy;
 	private Ejb3JoinColumn[] joinColumns;
 
 	//that suck, we should read that from the property mainly
 	public OneToOneSecondPass(
 			String mappedBy,
 			String ownerEntity,
 			String ownerProperty,
 			PropertyHolder propertyHolder,
 			PropertyData inferredData,
 			XClass targetEntity,
 			boolean ignoreNotFound,
 			boolean cascadeOnDelete,
 			boolean optional,
 			String cascadeStrategy,
 			Ejb3JoinColumn[] columns,
 			MetadataBuildingContext buildingContext) {
 		this.ownerEntity = ownerEntity;
 		this.ownerProperty = ownerProperty;
 		this.mappedBy = mappedBy;
 		this.propertyHolder = propertyHolder;
 		this.buildingContext = buildingContext;
 		this.ignoreNotFound = ignoreNotFound;
 		this.inferredData = inferredData;
 		this.targetEntity = targetEntity;
 		this.cascadeOnDelete = cascadeOnDelete;
 		this.optional = optional;
 		this.cascadeStrategy = cascadeStrategy;
 		this.joinColumns = columns;
 	}
 
 	//TODO refactor this code, there is a lot of duplication in this method
 	public void doSecondPass(Map persistentClasses) throws MappingException {
 		org.hibernate.mapping.OneToOne value = new org.hibernate.mapping.OneToOne(
 				buildingContext.getMetadataCollector(),
 				propertyHolder.getTable(),
 				propertyHolder.getPersistentClass()
 		);
 		final String propertyName = inferredData.getPropertyName();
 		value.setPropertyName( propertyName );
 		String referencedEntityName = ToOneBinder.getReferenceEntityName( inferredData, targetEntity, buildingContext );
 		value.setReferencedEntityName( referencedEntityName );  
 		AnnotationBinder.defineFetchingStrategy( value, inferredData.getProperty() );
 		//value.setFetchMode( fetchMode );
 		value.setCascadeDeleteEnabled( cascadeOnDelete );
 		//value.setLazy( fetchMode != FetchMode.JOIN );
 
 		if ( !optional ) value.setConstrained( true );
 		value.setForeignKeyType(
 				value.isConstrained()
 						? ForeignKeyDirection.FROM_PARENT
 						: ForeignKeyDirection.TO_PARENT
 		);
 		PropertyBinder binder = new PropertyBinder();
 		binder.setName( propertyName );
 		binder.setValue( value );
 		binder.setCascade( cascadeStrategy );
 		binder.setAccessType( inferredData.getDefaultAccess() );
 		Property prop = binder.makeProperty();
 		if ( BinderHelper.isEmptyAnnotationValue( mappedBy ) ) {
 			/*
 			 * we need to check if the columns are in the right order
 			 * if not, then we need to create a many to one and formula
 			 * but actually, since entities linked by a one to one need
 			 * to share the same composite id class, this cannot happen in hibernate
 			 */
 			boolean rightOrder = true;
 
 			if ( rightOrder ) {
 				String path = StringHelper.qualify( propertyHolder.getPath(), propertyName );
 				final ToOneFkSecondPass secondPass = new ToOneFkSecondPass(
 						value,
 						joinColumns,
 						!optional, //cannot have nullabe and unique on certain DBs
 						propertyHolder.getEntityOwnerClassName(),
 						path,
 						buildingContext
 				);
 				secondPass.doSecondPass( persistentClasses );
 				//no column associated since its a one to one
 				propertyHolder.addProperty( prop, inferredData.getDeclaringClass() );
 			}
 			else {
 				//this is a many to one with Formula
 
 			}
 		}
 		else {
 			PersistentClass otherSide = (PersistentClass) persistentClasses.get( value.getReferencedEntityName() );
 			Property otherSideProperty;
 			try {
 				if ( otherSide == null ) {
 					throw new MappingException( "Unable to find entity: " + value.getReferencedEntityName() );
 				}
 				otherSideProperty = BinderHelper.findPropertyByName( otherSide, mappedBy );
 			}
 			catch (MappingException e) {
 				throw new AnnotationException(
 						"Unknown mappedBy in: " + StringHelper.qualify( ownerEntity, ownerProperty )
 								+ ", referenced property unknown: "
 								+ StringHelper.qualify( value.getReferencedEntityName(), mappedBy )
 				);
 			}
 			if ( otherSideProperty == null ) {
 				throw new AnnotationException(
 						"Unknown mappedBy in: " + StringHelper.qualify( ownerEntity, ownerProperty )
 								+ ", referenced property unknown: "
 								+ StringHelper.qualify( value.getReferencedEntityName(), mappedBy )
 				);
 			}
 			if ( otherSideProperty.getValue() instanceof OneToOne ) {
 				propertyHolder.addProperty( prop, inferredData.getDeclaringClass() );
 			}
 			else if ( otherSideProperty.getValue() instanceof ManyToOne ) {
 				Iterator it = otherSide.getJoinIterator();
 				Join otherSideJoin = null;
 				while ( it.hasNext() ) {
 					Join otherSideJoinValue = (Join) it.next();
 					if ( otherSideJoinValue.containsProperty( otherSideProperty ) ) {
 						otherSideJoin = otherSideJoinValue;
 						break;
 					}
 				}
 				if ( otherSideJoin != null ) {
 					//@OneToOne @JoinTable
 					Join mappedByJoin = buildJoinFromMappedBySide(
 							(PersistentClass) persistentClasses.get( ownerEntity ), otherSideProperty, otherSideJoin
 					);
 					ManyToOne manyToOne = new ManyToOne( buildingContext.getMetadataCollector(), mappedByJoin.getTable() );
 					//FIXME use ignore not found here
 					manyToOne.setIgnoreNotFound( ignoreNotFound );
 					manyToOne.setCascadeDeleteEnabled( value.isCascadeDeleteEnabled() );
 					manyToOne.setFetchMode( value.getFetchMode() );
 					manyToOne.setLazy( value.isLazy() );
 					manyToOne.setReferencedEntityName( value.getReferencedEntityName() );
 					manyToOne.setUnwrapProxy( value.isUnwrapProxy() );
 					prop.setValue( manyToOne );
 					Iterator otherSideJoinKeyColumns = otherSideJoin.getKey().getColumnIterator();
 					while ( otherSideJoinKeyColumns.hasNext() ) {
 						Column column = (Column) otherSideJoinKeyColumns.next();
 						Column copy = new Column();
 						copy.setLength( column.getLength() );
 						copy.setScale( column.getScale() );
 						copy.setValue( manyToOne );
 						copy.setName( column.getQuotedName() );
 						copy.setNullable( column.isNullable() );
 						copy.setPrecision( column.getPrecision() );
 						copy.setUnique( column.isUnique() );
 						copy.setSqlType( column.getSqlType() );
 						copy.setCheckConstraint( column.getCheckConstraint() );
 						copy.setComment( column.getComment() );
 						copy.setDefaultValue( column.getDefaultValue() );
 						manyToOne.addColumn( copy );
 					}
 					mappedByJoin.addProperty( prop );
 				}
 				else {
 					propertyHolder.addProperty( prop, inferredData.getDeclaringClass() );
 				}
 				
 				value.setReferencedPropertyName( mappedBy );
 
 				// HHH-6813
 				// Foo: @Id long id, @OneToOne(mappedBy="foo") Bar bar
 				// Bar: @Id @OneToOne Foo foo
 				boolean referencesDerivedId = false;
 				try {
 					referencesDerivedId = otherSide.getIdentifier() instanceof Component
 							&& ( (Component) otherSide.getIdentifier() ).getProperty( mappedBy ) != null;
 				}
 				catch ( MappingException e ) {
 					// ignore
 				}
 				boolean referenceToPrimaryKey  = referencesDerivedId || mappedBy == null;
 				value.setReferenceToPrimaryKey( referenceToPrimaryKey );
 				
 				// If the other side is a derived ID, prevent an infinite
 				// loop of attempts to resolve identifiers.
 				if ( referencesDerivedId ) {
 					( (ManyToOne) otherSideProperty.getValue() ).setReferenceToPrimaryKey( false );
 				}
 
 				String propertyRef = value.getReferencedPropertyName();
 				if ( propertyRef != null ) {
 					buildingContext.getMetadataCollector().addUniquePropertyReference(
 							value.getReferencedEntityName(),
 							propertyRef
 					);
 				}
 			}
 			else {
 				throw new AnnotationException(
 						"Referenced property not a (One|Many)ToOne: "
 								+ StringHelper.qualify(
 								otherSide.getEntityName(), mappedBy
 						)
 								+ " in mappedBy of "
 								+ StringHelper.qualify( ownerEntity, ownerProperty )
 				);
 			}
 		}
 
 		final ForeignKey fk = inferredData.getProperty().getAnnotation( ForeignKey.class );
 		if ( fk != null && !BinderHelper.isEmptyAnnotationValue( fk.name() ) ) {
 			value.setForeignKeyName( fk.name() );
 		}
 		else {
 			final javax.persistence.ForeignKey jpaFk = inferredData.getProperty().getAnnotation( javax.persistence.ForeignKey.class );
 			if ( jpaFk != null ) {
 				if ( jpaFk.value() == ConstraintMode.NO_CONSTRAINT ) {
 					value.setForeignKeyName( "none" );
 				}
 				else {
 					value.setForeignKeyName( StringHelper.nullIfEmpty( jpaFk.name() ) );
+					value.setForeignKeyDefinition( StringHelper.nullIfEmpty( jpaFk.foreignKeyDefinition() ) );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Builds the <code>Join</code> instance for the mapped by side of a <i>OneToOne</i> association using 
 	 * a join tables.
 	 * <p>
 	 * Note:<br/>
 	 * <ul>
 	 * <li>From the mappedBy side we should not create the PK nor the FK, this is handled from the other side.</li>
 	 * <li>This method is a dirty dupe of EntityBinder.bindSecondaryTable</i>.
 	 * </p>
 	 */
 	private Join buildJoinFromMappedBySide(PersistentClass persistentClass, Property otherSideProperty, Join originalJoin) {
 		Join join = new Join();
 		join.setPersistentClass( persistentClass );
 
 		//no check constraints available on joins
 		join.setTable( originalJoin.getTable() );
 		join.setInverse( true );
 		SimpleValue key = new DependantValue( buildingContext.getMetadataCollector(), join.getTable(), persistentClass.getIdentifier() );
 		//TODO support @ForeignKey
 		join.setKey( key );
 		join.setSequentialSelect( false );
 		//TODO support for inverse and optional
 		join.setOptional( true ); //perhaps not quite per-spec, but a Good Thing anyway
 		key.setCascadeDeleteEnabled( false );
 		Iterator mappedByColumns = otherSideProperty.getValue().getColumnIterator();
 		while ( mappedByColumns.hasNext() ) {
 			Column column = (Column) mappedByColumns.next();
 			Column copy = new Column();
 			copy.setLength( column.getLength() );
 			copy.setScale( column.getScale() );
 			copy.setValue( key );
 			copy.setName( column.getQuotedName() );
 			copy.setNullable( column.isNullable() );
 			copy.setPrecision( column.getPrecision() );
 			copy.setUnique( column.isUnique() );
 			copy.setSqlType( column.getSqlType() );
 			copy.setCheckConstraint( column.getCheckConstraint() );
 			copy.setComment( column.getComment() );
 			copy.setDefaultValue( column.getDefaultValue() );
 			key.addColumn( copy );
 		}
 		persistentClass.addJoin( join );
 		return join;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
index 1a72915b36..960bc1bdb3 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
@@ -135,1562 +135,1570 @@ public abstract class CollectionBinder {
 	private boolean insertable = true;
 	private boolean updatable = true;
 	private Ejb3JoinColumn[] fkJoinColumns;
 	private boolean isExplicitAssociationTable;
 	private Ejb3Column[] elementColumns;
 	private boolean isEmbedded;
 	private XProperty property;
 	private boolean ignoreNotFound;
 	private TableBinder tableBinder;
 	private Ejb3Column[] mapKeyColumns;
 	private Ejb3JoinColumn[] mapKeyManyToManyColumns;
 	protected HashMap<String, IdentifierGeneratorDefinition> localGenerators;
 	protected Map<XClass, InheritanceState> inheritanceStatePerClass;
 	private XClass declaringClass;
 	private boolean declaringClassSet;
 	private AccessType accessType;
 	private boolean hibernateExtensionMapping;
 
 	private boolean isSortedCollection;
 	private javax.persistence.OrderBy jpaOrderBy;
 	private OrderBy sqlOrderBy;
 	private Sort deprecatedSort;
 	private SortNatural naturalSort;
 	private SortComparator comparatorSort;
 
 	private String explicitType;
 	private final Properties explicitTypeParameters = new Properties();
 
 	protected MetadataBuildingContext getBuildingContext() {
 		return buildingContext;
 	}
 
 	public void setBuildingContext(MetadataBuildingContext buildingContext) {
 		this.buildingContext = buildingContext;
 	}
 
 	public boolean isMap() {
 		return false;
 	}
 
 	public void setIsHibernateExtensionMapping(boolean hibernateExtensionMapping) {
 		this.hibernateExtensionMapping = hibernateExtensionMapping;
 	}
 
 	protected boolean isHibernateExtensionMapping() {
 		return hibernateExtensionMapping;
 	}
 
 	public void setUpdatable(boolean updatable) {
 		this.updatable = updatable;
 	}
 
 	public void setInheritanceStatePerClass(Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		this.inheritanceStatePerClass = inheritanceStatePerClass;
 	}
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public void setCascadeStrategy(String cascadeStrategy) {
 		this.cascadeStrategy = cascadeStrategy;
 	}
 
 	public void setAccessType(AccessType accessType) {
 		this.accessType = accessType;
 	}
 
 	public void setInverseJoinColumns(Ejb3JoinColumn[] inverseJoinColumns) {
 		this.inverseJoinColumns = inverseJoinColumns;
 	}
 
 	public void setJoinColumns(Ejb3JoinColumn[] joinColumns) {
 		this.joinColumns = joinColumns;
 	}
 
 	private Ejb3JoinColumn[] joinColumns;
 
 	public void setPropertyHolder(PropertyHolder propertyHolder) {
 		this.propertyHolder = propertyHolder;
 	}
 
 	public void setBatchSize(BatchSize batchSize) {
 		this.batchSize = batchSize == null ? -1 : batchSize.size();
 	}
 
 	public void setJpaOrderBy(javax.persistence.OrderBy jpaOrderBy) {
 		this.jpaOrderBy = jpaOrderBy;
 	}
 
 	public void setSqlOrderBy(OrderBy sqlOrderBy) {
 		this.sqlOrderBy = sqlOrderBy;
 	}
 
 	public void setSort(Sort deprecatedSort) {
 		this.deprecatedSort = deprecatedSort;
 	}
 
 	public void setNaturalSort(SortNatural naturalSort) {
 		this.naturalSort = naturalSort;
 	}
 
 	public void setComparatorSort(SortComparator comparatorSort) {
 		this.comparatorSort = comparatorSort;
 	}
 
 	/**
 	 * collection binder factory
 	 */
 	public static CollectionBinder getCollectionBinder(
 			String entityName,
 			XProperty property,
 			boolean isIndexed,
 			boolean isHibernateExtensionMapping,
 			MetadataBuildingContext buildingContext) {
 		final CollectionBinder result;
 		if ( property.isArray() ) {
 			if ( property.getElementClass().isPrimitive() ) {
 				result = new PrimitiveArrayBinder();
 			}
 			else {
 				result = new ArrayBinder();
 			}
 		}
 		else if ( property.isCollection() ) {
 			//TODO consider using an XClass
 			Class returnedClass = property.getCollectionClass();
 			if ( java.util.Set.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Set do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new SetBinder( false );
 			}
 			else if ( java.util.SortedSet.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Set do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new SetBinder( true );
 			}
 			else if ( java.util.Map.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Map do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new MapBinder( false );
 			}
 			else if ( java.util.SortedMap.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					throw new AnnotationException( "Map do not support @CollectionId: "
 							+ StringHelper.qualify( entityName, property.getName() ) );
 				}
 				result = new MapBinder( true );
 			}
 			else if ( java.util.Collection.class.equals( returnedClass ) ) {
 				if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					result = new IdBagBinder();
 				}
 				else {
 					result = new BagBinder();
 				}
 			}
 			else if ( java.util.List.class.equals( returnedClass ) ) {
 				if ( isIndexed ) {
 					if ( property.isAnnotationPresent( CollectionId.class ) ) {
 						throw new AnnotationException(
 								"List do not support @CollectionId and @OrderColumn (or @IndexColumn) at the same time: "
 								+ StringHelper.qualify( entityName, property.getName() ) );
 					}
 					result = new ListBinder();
 				}
 				else if ( property.isAnnotationPresent( CollectionId.class ) ) {
 					result = new IdBagBinder();
 				}
 				else {
 					result = new BagBinder();
 				}
 			}
 			else {
 				throw new AnnotationException(
 						returnedClass.getName() + " collection not yet supported: "
 								+ StringHelper.qualify( entityName, property.getName() )
 				);
 			}
 		}
 		else {
 			throw new AnnotationException(
 					"Illegal attempt to map a non collection as a @OneToMany, @ManyToMany or @CollectionOfElements: "
 							+ StringHelper.qualify( entityName, property.getName() )
 			);
 		}
 		result.setIsHibernateExtensionMapping( isHibernateExtensionMapping );
 
 		final CollectionType typeAnnotation = property.getAnnotation( CollectionType.class );
 		if ( typeAnnotation != null ) {
 			final String typeName = typeAnnotation.type();
 			// see if it names a type-def
 			final TypeDefinition typeDef = buildingContext.getMetadataCollector().getTypeDefinition( typeName );
 			if ( typeDef != null ) {
 				result.explicitType = typeDef.getTypeImplementorClass().getName();
 				result.explicitTypeParameters.putAll( typeDef.getParameters() );
 			}
 			else {
 				result.explicitType = typeName;
 				for ( Parameter param : typeAnnotation.parameters() ) {
 					result.explicitTypeParameters.setProperty( param.name(), param.value() );
 				}
 			}
 		}
 
 		return result;
 	}
 
 	protected CollectionBinder(boolean isSortedCollection) {
 		this.isSortedCollection = isSortedCollection;
 	}
 
 	public void setMappedBy(String mappedBy) {
 		this.mappedBy = mappedBy;
 	}
 
 	public void setTableBinder(TableBinder tableBinder) {
 		this.tableBinder = tableBinder;
 	}
 
 	public void setCollectionType(XClass collectionType) {
 		// NOTE: really really badly named.  This is actually NOT the collection-type, but rather the collection-element-type!
 		this.collectionType = collectionType;
 	}
 
 	public void setTargetEntity(XClass targetEntity) {
 		this.targetEntity = targetEntity;
 	}
 
 	protected abstract Collection createCollection(PersistentClass persistentClass);
 
 	public Collection getCollection() {
 		return collection;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public void setDeclaringClass(XClass declaringClass) {
 		this.declaringClass = declaringClass;
 		this.declaringClassSet = true;
 	}
 
 	public void bind() {
 		this.collection = createCollection( propertyHolder.getPersistentClass() );
 		String role = StringHelper.qualify( propertyHolder.getPath(), propertyName );
 		LOG.debugf( "Collection role: %s", role );
 		collection.setRole( role );
 		collection.setMappedByProperty( mappedBy );
 
 		if ( property.isAnnotationPresent( MapKeyColumn.class )
 			&& mapKeyPropertyName != null ) {
 			throw new AnnotationException(
 					"Cannot mix @javax.persistence.MapKey and @MapKeyColumn or @org.hibernate.annotations.MapKey "
 							+ "on the same collection: " + StringHelper.qualify(
 							propertyHolder.getPath(), propertyName
 					)
 			);
 		}
 
 		// set explicit type information
 		if ( explicitType != null ) {
 			final TypeDefinition typeDef = buildingContext.getMetadataCollector().getTypeDefinition( explicitType );
 			if ( typeDef == null ) {
 				collection.setTypeName( explicitType );
 				collection.setTypeParameters( explicitTypeParameters );
 			}
 			else {
 				collection.setTypeName( typeDef.getTypeImplementorClass().getName() );
 				collection.setTypeParameters( typeDef.getParameters() );
 			}
 		}
 
 		//set laziness
 		defineFetchingStrategy();
 		collection.setBatchSize( batchSize );
 
 		collection.setMutable( !property.isAnnotationPresent( Immutable.class ) );
 
 		//work on association
 		boolean isMappedBy = !BinderHelper.isEmptyAnnotationValue( mappedBy );
 
 		final OptimisticLock lockAnn = property.getAnnotation( OptimisticLock.class );
 		final boolean includeInOptimisticLockChecks = ( lockAnn != null )
 				? ! lockAnn.excluded()
 				: ! isMappedBy;
 		collection.setOptimisticLocked( includeInOptimisticLockChecks );
 
 		Persister persisterAnn = property.getAnnotation( Persister.class );
 		if ( persisterAnn != null ) {
 			collection.setCollectionPersisterClass( persisterAnn.impl() );
 		}
 
 		applySortingAndOrdering( collection );
 
 		//set cache
 		if ( StringHelper.isNotEmpty( cacheConcurrencyStrategy ) ) {
 			collection.setCacheConcurrencyStrategy( cacheConcurrencyStrategy );
 			collection.setCacheRegionName( cacheRegionName );
 		}
 
 		//SQL overriding
 		SQLInsert sqlInsert = property.getAnnotation( SQLInsert.class );
 		SQLUpdate sqlUpdate = property.getAnnotation( SQLUpdate.class );
 		SQLDelete sqlDelete = property.getAnnotation( SQLDelete.class );
 		SQLDeleteAll sqlDeleteAll = property.getAnnotation( SQLDeleteAll.class );
 		Loader loader = property.getAnnotation( Loader.class );
 		if ( sqlInsert != null ) {
 			collection.setCustomSQLInsert( sqlInsert.sql().trim(), sqlInsert.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlInsert.check().toString().toLowerCase(Locale.ROOT) )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			collection.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlUpdate.check().toString().toLowerCase(Locale.ROOT) )
 			);
 		}
 		if ( sqlDelete != null ) {
 			collection.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDelete.check().toString().toLowerCase(Locale.ROOT) )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			collection.setCustomSQLDeleteAll( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase(Locale.ROOT) )
 			);
 		}
 		if ( loader != null ) {
 			collection.setLoaderName( loader.namedQuery() );
 		}
 
 		if (isMappedBy
 				&& (property.isAnnotationPresent( JoinColumn.class )
 					|| property.isAnnotationPresent( JoinColumns.class )
 					|| propertyHolder.getJoinTable( property ) != null ) ) {
 			String message = "Associations marked as mappedBy must not define database mappings like @JoinTable or @JoinColumn: ";
 			message += StringHelper.qualify( propertyHolder.getPath(), propertyName );
 			throw new AnnotationException( message );
 		}
 
 		collection.setInverse( isMappedBy );
 
 		//many to many may need some second pass informations
 		if ( !oneToMany && isMappedBy ) {
 			buildingContext.getMetadataCollector().addMappedBy( getCollectionType().getName(), mappedBy, propertyName );
 		}
 		//TODO reducce tableBinder != null and oneToMany
 		XClass collectionType = getCollectionType();
 		if ( inheritanceStatePerClass == null) throw new AssertionFailure( "inheritanceStatePerClass not set" );
 		SecondPass sp = getSecondPass(
 				fkJoinColumns,
 				joinColumns,
 				inverseJoinColumns,
 				elementColumns,
 				mapKeyColumns,
 				mapKeyManyToManyColumns,
 				isEmbedded,
 				property,
 				collectionType,
 				ignoreNotFound,
 				oneToMany,
 				tableBinder,
 				buildingContext
 		);
 		if ( collectionType.isAnnotationPresent( Embeddable.class )
 				|| property.isAnnotationPresent( ElementCollection.class ) //JPA 2
 				) {
 			// do it right away, otherwise @ManyToOne on composite element call addSecondPass
 			// and raise a ConcurrentModificationException
 			//sp.doSecondPass( CollectionHelper.EMPTY_MAP );
 			buildingContext.getMetadataCollector().addSecondPass( sp, !isMappedBy );
 		}
 		else {
 			buildingContext.getMetadataCollector().addSecondPass( sp, !isMappedBy );
 		}
 
 		buildingContext.getMetadataCollector().addCollectionBinding( collection );
 
 		//property building
 		PropertyBinder binder = new PropertyBinder();
 		binder.setName( propertyName );
 		binder.setValue( collection );
 		binder.setCascade( cascadeStrategy );
 		if ( cascadeStrategy != null && cascadeStrategy.indexOf( "delete-orphan" ) >= 0 ) {
 			collection.setOrphanDelete( true );
 		}
 		binder.setLazy( collection.isLazy() );
 		final LazyGroup lazyGroupAnnotation = property.getAnnotation( LazyGroup.class );
 		if ( lazyGroupAnnotation != null ) {
 			binder.setLazyGroup( lazyGroupAnnotation.value() );
 		}
 		binder.setAccessType( accessType );
 		binder.setProperty( property );
 		binder.setInsertable( insertable );
 		binder.setUpdatable( updatable );
 		Property prop = binder.makeProperty();
 		//we don't care about the join stuffs because the column is on the association table.
 		if (! declaringClassSet) throw new AssertionFailure( "DeclaringClass is not set in CollectionBinder while binding" );
 		propertyHolder.addProperty( prop, declaringClass );
 	}
 
 	private void applySortingAndOrdering(Collection collection) {
 		boolean isSorted = isSortedCollection;
 
 		boolean hadOrderBy = false;
 		boolean hadExplicitSort = false;
 
 		Class<? extends Comparator> comparatorClass = null;
 
 		if ( jpaOrderBy == null && sqlOrderBy == null ) {
 			if ( deprecatedSort != null ) {
 				LOG.debug( "Encountered deprecated @Sort annotation; use @SortNatural or @SortComparator instead." );
 				if ( naturalSort != null || comparatorSort != null ) {
 					throw buildIllegalSortCombination();
 				}
 				hadExplicitSort = deprecatedSort.type() != SortType.UNSORTED;
 				if ( deprecatedSort.type() == SortType.NATURAL ) {
 					isSorted = true;
 				}
 				else if ( deprecatedSort.type() == SortType.COMPARATOR ) {
 					isSorted = true;
 					comparatorClass = deprecatedSort.comparator();
 				}
 			}
 			else if ( naturalSort != null ) {
 				if ( comparatorSort != null ) {
 					throw buildIllegalSortCombination();
 				}
 				hadExplicitSort = true;
 			}
 			else if ( comparatorSort != null ) {
 				hadExplicitSort = true;
 				comparatorClass = comparatorSort.value();
 			}
 		}
 		else {
 			if ( jpaOrderBy != null && sqlOrderBy != null ) {
 				throw new AnnotationException(
 						String.format(
 								"Illegal combination of @%s and @%s on %s",
 								javax.persistence.OrderBy.class.getName(),
 								OrderBy.class.getName(),
 								safeCollectionRole()
 						)
 				);
 			}
 
 			hadOrderBy = true;
 			hadExplicitSort = false;
 
 			// we can only apply the sql-based order by up front.  The jpa order by has to wait for second pass
 			if ( sqlOrderBy != null ) {
 				collection.setOrderBy( sqlOrderBy.clause() );
 			}
 		}
 
 		if ( isSortedCollection ) {
 			if ( ! hadExplicitSort && !hadOrderBy ) {
 				throw new AnnotationException(
 						"A sorted collection must define and ordering or sorting : " + safeCollectionRole()
 				);
 			}
 		}
 
 		collection.setSorted( isSortedCollection || hadExplicitSort );
 
 		if ( comparatorClass != null ) {
 			try {
 				collection.setComparator( comparatorClass.newInstance() );
 			}
 			catch (Exception e) {
 				throw new AnnotationException(
 						String.format(
 								"Could not instantiate comparator class [%s] for %s",
 								comparatorClass.getName(),
 								safeCollectionRole()
 						)
 				);
 			}
 		}
 	}
 
 	private AnnotationException buildIllegalSortCombination() {
 		return new AnnotationException(
 				String.format(
 						"Illegal combination of annotations on %s.  Only one of @%s, @%s and @%s can be used",
 						safeCollectionRole(),
 						Sort.class.getName(),
 						SortNatural.class.getName(),
 						SortComparator.class.getName()
 				)
 		);
 	}
 
 	private void defineFetchingStrategy() {
 		LazyCollection lazy = property.getAnnotation( LazyCollection.class );
 		Fetch fetch = property.getAnnotation( Fetch.class );
 		OneToMany oneToMany = property.getAnnotation( OneToMany.class );
 		ManyToMany manyToMany = property.getAnnotation( ManyToMany.class );
 		ElementCollection elementCollection = property.getAnnotation( ElementCollection.class ); //jpa 2
 		ManyToAny manyToAny = property.getAnnotation( ManyToAny.class );
 		FetchType fetchType;
 		if ( oneToMany != null ) {
 			fetchType = oneToMany.fetch();
 		}
 		else if ( manyToMany != null ) {
 			fetchType = manyToMany.fetch();
 		}
 		else if ( elementCollection != null ) {
 			fetchType = elementCollection.fetch();
 		}
 		else if ( manyToAny != null ) {
 			fetchType = FetchType.LAZY;
 		}
 		else {
 			throw new AssertionFailure(
 					"Define fetch strategy on a property not annotated with @ManyToOne nor @OneToMany nor @CollectionOfElements"
 			);
 		}
 		if ( lazy != null ) {
 			collection.setLazy( !( lazy.value() == LazyCollectionOption.FALSE ) );
 			collection.setExtraLazy( lazy.value() == LazyCollectionOption.EXTRA );
 		}
 		else {
 			collection.setLazy( fetchType == FetchType.LAZY );
 			collection.setExtraLazy( false );
 		}
 		if ( fetch != null ) {
 			if ( fetch.value() == org.hibernate.annotations.FetchMode.JOIN ) {
 				collection.setFetchMode( FetchMode.JOIN );
 				collection.setLazy( false );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SELECT ) {
 				collection.setFetchMode( FetchMode.SELECT );
 			}
 			else if ( fetch.value() == org.hibernate.annotations.FetchMode.SUBSELECT ) {
 				collection.setFetchMode( FetchMode.SELECT );
 				collection.setSubselectLoadable( true );
 				collection.getOwner().setSubselectLoadableCollections( true );
 			}
 			else {
 				throw new AssertionFailure( "Unknown FetchMode: " + fetch.value() );
 			}
 		}
 		else {
 			collection.setFetchMode( AnnotationBinder.getFetchMode( fetchType ) );
 		}
 	}
 
 	private XClass getCollectionType() {
 		if ( AnnotationBinder.isDefault( targetEntity, buildingContext ) ) {
 			if ( collectionType != null ) {
 				return collectionType;
 			}
 			else {
 				String errorMsg = "Collection has neither generic type or OneToMany.targetEntity() defined: "
 						+ safeCollectionRole();
 				throw new AnnotationException( errorMsg );
 			}
 		}
 		else {
 			return targetEntity;
 		}
 	}
 
 	public SecondPass getSecondPass(
 			final Ejb3JoinColumn[] fkJoinColumns,
 			final Ejb3JoinColumn[] keyColumns,
 			final Ejb3JoinColumn[] inverseColumns,
 			final Ejb3Column[] elementColumns,
 			final Ejb3Column[] mapKeyColumns,
 			final Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			final boolean isEmbedded,
 			final XProperty property,
 			final XClass collType,
 			final boolean ignoreNotFound,
 			final boolean unique,
 			final TableBinder assocTableBinder,
 			final MetadataBuildingContext buildingContext) {
 		return new CollectionSecondPass( buildingContext, collection ) {
 			@Override
 			public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas) throws MappingException {
 				bindStarToManySecondPass(
 						persistentClasses,
 						collType,
 						fkJoinColumns,
 						keyColumns,
 						inverseColumns,
 						elementColumns,
 						isEmbedded,
 						property,
 						unique,
 						assocTableBinder,
 						ignoreNotFound,
 						buildingContext
 				);
 			}
 		};
 	}
 
 	/**
 	 * return true if it's a Fk, false if it's an association table
 	 */
 	protected boolean bindStarToManySecondPass(
 			Map persistentClasses,
 			XClass collType,
 			Ejb3JoinColumn[] fkJoinColumns,
 			Ejb3JoinColumn[] keyColumns,
 			Ejb3JoinColumn[] inverseColumns,
 			Ejb3Column[] elementColumns,
 			boolean isEmbedded,
 			XProperty property,
 			boolean unique,
 			TableBinder associationTableBinder,
 			boolean ignoreNotFound,
 			MetadataBuildingContext buildingContext) {
 		PersistentClass persistentClass = (PersistentClass) persistentClasses.get( collType.getName() );
 		boolean reversePropertyInJoin = false;
 		if ( persistentClass != null && StringHelper.isNotEmpty( this.mappedBy ) ) {
 			try {
 				reversePropertyInJoin = 0 != persistentClass.getJoinNumber(
 						persistentClass.getRecursiveProperty( this.mappedBy )
 				);
 			}
 			catch (MappingException e) {
 				StringBuilder error = new StringBuilder( 80 );
 				error.append( "mappedBy reference an unknown target entity property: " )
 						.append( collType ).append( "." ).append( this.mappedBy )
 						.append( " in " )
 						.append( collection.getOwnerEntityName() )
 						.append( "." )
 						.append( property.getName() );
 				throw new AnnotationException( error.toString() );
 			}
 		}
 		if ( persistentClass != null
 				&& !reversePropertyInJoin
 				&& oneToMany
 				&& !this.isExplicitAssociationTable
 				&& ( joinColumns[0].isImplicit() && !BinderHelper.isEmptyAnnotationValue( this.mappedBy ) //implicit @JoinColumn
 				|| !fkJoinColumns[0].isImplicit() ) //this is an explicit @JoinColumn
 				) {
 			//this is a Foreign key
 			bindOneToManySecondPass(
 					getCollection(),
 					persistentClasses,
 					fkJoinColumns,
 					collType,
 					cascadeDeleteEnabled,
 					ignoreNotFound,
 					buildingContext,
 					inheritanceStatePerClass
 			);
 			return true;
 		}
 		else {
 			//this is an association table
 			bindManyToManySecondPass(
 					this.collection,
 					persistentClasses,
 					keyColumns,
 					inverseColumns,
 					elementColumns,
 					isEmbedded, collType,
 					ignoreNotFound, unique,
 					cascadeDeleteEnabled,
 					associationTableBinder,
 					property,
 					propertyHolder,
 					buildingContext
 			);
 			return false;
 		}
 	}
 
 	protected void bindOneToManySecondPass(
 			Collection collection,
 			Map persistentClasses,
 			Ejb3JoinColumn[] fkJoinColumns,
 			XClass collectionType,
 			boolean cascadeDeleteEnabled,
 			boolean ignoreNotFound,
 			MetadataBuildingContext buildingContext,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debugf( "Binding a OneToMany: %s.%s through a foreign key", propertyHolder.getEntityName(), propertyName );
 		}
 		org.hibernate.mapping.OneToMany oneToMany = new org.hibernate.mapping.OneToMany( buildingContext.getMetadataCollector(), collection.getOwner() );
 		collection.setElement( oneToMany );
 		oneToMany.setReferencedEntityName( collectionType.getName() );
 		oneToMany.setIgnoreNotFound( ignoreNotFound );
 
 		String assocClass = oneToMany.getReferencedEntityName();
 		PersistentClass associatedClass = (PersistentClass) persistentClasses.get( assocClass );
 		if ( jpaOrderBy != null ) {
 			final String orderByFragment = buildOrderByClauseFromHql(
 					jpaOrderBy.value(),
 					associatedClass,
 					collection.getRole()
 			);
 			if ( StringHelper.isNotEmpty( orderByFragment ) ) {
 				collection.setOrderBy( orderByFragment );
 			}
 		}
 
 		if ( buildingContext == null ) {
 			throw new AssertionFailure(
 					"CollectionSecondPass for oneToMany should not be called with null mappings"
 			);
 		}
 		Map<String, Join> joins = buildingContext.getMetadataCollector().getJoins( assocClass );
 		if ( associatedClass == null ) {
 			throw new MappingException(
 					"Association references unmapped class: " + assocClass
 			);
 		}
 		oneToMany.setAssociatedClass( associatedClass );
 		for (Ejb3JoinColumn column : fkJoinColumns) {
 			column.setPersistentClass( associatedClass, joins, inheritanceStatePerClass );
 			column.setJoins( joins );
 			collection.setCollectionTable( column.getTable() );
 		}
 		if ( debugEnabled ) {
 			LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 		}
 		bindFilters( false );
 		bindCollectionSecondPass( collection, null, fkJoinColumns, cascadeDeleteEnabled, property, buildingContext );
 		if ( !collection.isInverse()
 				&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = oneToMany.getReferencedEntityName();
 			PersistentClass referenced = buildingContext.getMetadataCollector().getEntityBinding( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + fkJoinColumns[0].getPropertyName() + '_' + fkJoinColumns[0].getLogicalColumnName() + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 
 	private void bindFilters(boolean hasAssociationTable) {
 		Filter simpleFilter = property.getAnnotation( Filter.class );
 		//set filtering
 		//test incompatible choices
 		//if ( StringHelper.isNotEmpty( where ) ) collection.setWhere( where );
 		if ( simpleFilter != null ) {
 			if ( hasAssociationTable ) {
 				collection.addManyToManyFilter(simpleFilter.name(), getCondition(simpleFilter), simpleFilter.deduceAliasInjectionPoints(),
 						toAliasTableMap(simpleFilter.aliases()), toAliasEntityMap(simpleFilter.aliases()));
 			}
 			else {
 				collection.addFilter(simpleFilter.name(), getCondition(simpleFilter), simpleFilter.deduceAliasInjectionPoints(),
 						toAliasTableMap(simpleFilter.aliases()), toAliasEntityMap(simpleFilter.aliases()));
 			}
 		}
 		Filters filters = property.getAnnotation( Filters.class );
 		if ( filters != null ) {
 			for (Filter filter : filters.value()) {
 				if ( hasAssociationTable ) {
 					collection.addManyToManyFilter( filter.name(), getCondition(filter), filter.deduceAliasInjectionPoints(),
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 				else {
 					collection.addFilter(filter.name(), getCondition(filter), filter.deduceAliasInjectionPoints(),
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 			}
 		}
 		FilterJoinTable simpleFilterJoinTable = property.getAnnotation( FilterJoinTable.class );
 		if ( simpleFilterJoinTable != null ) {
 			if ( hasAssociationTable ) {
 				collection.addFilter(simpleFilterJoinTable.name(), simpleFilterJoinTable.condition(),
 						simpleFilterJoinTable.deduceAliasInjectionPoints(),
 						toAliasTableMap(simpleFilterJoinTable.aliases()), toAliasEntityMap(simpleFilterJoinTable.aliases()));
 					}
 			else {
 				throw new AnnotationException(
 						"Illegal use of @FilterJoinTable on an association without join table:"
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 		FilterJoinTables filterJoinTables = property.getAnnotation( FilterJoinTables.class );
 		if ( filterJoinTables != null ) {
 			for (FilterJoinTable filter : filterJoinTables.value()) {
 				if ( hasAssociationTable ) {
 					collection.addFilter(filter.name(), filter.condition(),
 							filter.deduceAliasInjectionPoints(),
 							toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 				}
 				else {
 					throw new AnnotationException(
 							"Illegal use of @FilterJoinTable on an association without join table:"
 									+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 					);
 				}
 			}
 		}
 
 		StringBuilder whereBuffer = new StringBuilder();
 		if ( property.getElementClass() != null ) {
 			Where whereOnClass = property.getElementClass().getAnnotation( Where.class );
 			if ( whereOnClass != null ) {
 				String clause = whereOnClass.clause();
 				if ( StringHelper.isNotEmpty( clause ) ) {
 					whereBuffer.append( clause );
 				}
 			}
 		}
 		Where whereOnCollection = property.getAnnotation( Where.class );
 		if ( whereOnCollection != null ) {
 			String clause = whereOnCollection.clause();
 			if ( StringHelper.isNotEmpty( clause ) ) {
 				if ( whereBuffer.length() > 0 ) {
 					whereBuffer.append( StringHelper.WHITESPACE );
 					whereBuffer.append( Junction.Nature.AND.getOperator() );
 					whereBuffer.append( StringHelper.WHITESPACE );
 				}
 				whereBuffer.append( clause );
 			}
 		}
 		if ( whereBuffer.length() > 0 ) {
 			String whereClause = whereBuffer.toString();
 			if ( hasAssociationTable ) {
 				collection.setManyToManyWhere( whereClause );
 			}
 			else {
 				collection.setWhere( whereClause );
 			}
 		}
 
 		WhereJoinTable whereJoinTable = property.getAnnotation( WhereJoinTable.class );
 		String whereJoinTableClause = whereJoinTable == null ? null : whereJoinTable.clause();
 		if ( StringHelper.isNotEmpty( whereJoinTableClause ) ) {
 			if ( hasAssociationTable ) {
 				collection.setWhere( whereJoinTableClause );
 			}
 			else {
 				throw new AnnotationException(
 						"Illegal use of @WhereJoinTable on an association without join table:"
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 //		This cannot happen in annotations since the second fetch is hardcoded to join
 //		if ( ( ! collection.getManyToManyFilterMap().isEmpty() || collection.getManyToManyWhere() != null ) &&
 //		        collection.getFetchMode() == FetchMode.JOIN &&
 //		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 //			throw new MappingException(
 //			        "association with join table  defining filter or where without join fetching " +
 //			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 //				);
 //		}
 	}
 
 	private String getCondition(FilterJoinTable filter) {
 		//set filtering
 		String name = filter.name();
 		String cond = filter.condition();
 		return getCondition( cond, name );
 	}
 
 	private String getCondition(Filter filter) {
 		//set filtering
 		String name = filter.name();
 		String cond = filter.condition();
 		return getCondition( cond, name );
 	}
 
 	private String getCondition(String cond, String name) {
 		if ( BinderHelper.isEmptyAnnotationValue( cond ) ) {
 			cond = buildingContext.getMetadataCollector().getFilterDefinition( name ).getDefaultFilterCondition();
 			if ( StringHelper.isEmpty( cond ) ) {
 				throw new AnnotationException(
 						"no filter condition found for filter " + name + " in "
 								+ StringHelper.qualify( propertyHolder.getPath(), propertyName )
 				);
 			}
 		}
 		return cond;
 	}
 
 	public void setCache(Cache cacheAnn) {
 		if ( cacheAnn != null ) {
 			cacheRegionName = BinderHelper.isEmptyAnnotationValue( cacheAnn.region() ) ? null : cacheAnn.region();
 			cacheConcurrencyStrategy = EntityBinder.getCacheConcurrencyStrategy( cacheAnn.usage() );
 		}
 		else {
 			cacheConcurrencyStrategy = null;
 			cacheRegionName = null;
 		}
 	}
 
 	public void setOneToMany(boolean oneToMany) {
 		this.oneToMany = oneToMany;
 	}
 
 	public void setIndexColumn(IndexColumn indexColumn) {
 		this.indexColumn = indexColumn;
 	}
 
 	public void setMapKey(MapKey key) {
 		if ( key != null ) {
 			mapKeyPropertyName = key.name();
 		}
 	}
 
 	private static String buildOrderByClauseFromHql(String orderByFragment, PersistentClass associatedClass, String role) {
 		if ( orderByFragment != null ) {
 			if ( orderByFragment.length() == 0 ) {
 				//order by id
 				return "id asc";
 			}
 			else if ( "desc".equals( orderByFragment ) ) {
 				return "id desc";
 			}
 		}
 		return orderByFragment;
 	}
 
 	private static String adjustUserSuppliedValueCollectionOrderingFragment(String orderByFragment) {
 		if ( orderByFragment != null ) {
 			// NOTE: "$element$" is a specially recognized collection property recognized by the collection persister
 			if ( orderByFragment.length() == 0 ) {
 				//order by element
 				return "$element$ asc";
 			}
 			else if ( "desc".equals( orderByFragment ) ) {
 				return "$element$ desc";
 			}
 		}
 		return orderByFragment;
 	}
 
 	private static SimpleValue buildCollectionKey(
 			Collection collValue,
 			Ejb3JoinColumn[] joinColumns,
 			boolean cascadeDeleteEnabled,
 			XProperty property,
 			MetadataBuildingContext buildingContext) {
 		//binding key reference using column
 		KeyValue keyVal;
 		//give a chance to override the referenced property name
 		//has to do that here because the referencedProperty creation happens in a FKSecondPass for Many to one yuk!
 		if ( joinColumns.length > 0 && StringHelper.isNotEmpty( joinColumns[0].getMappedBy() ) ) {
 			String entityName = joinColumns[0].getManyToManyOwnerSideEntityName() != null ?
 					"inverse__" + joinColumns[0].getManyToManyOwnerSideEntityName() :
 					joinColumns[0].getPropertyHolder().getEntityName();
 			String propRef = buildingContext.getMetadataCollector().getPropertyReferencedAssociation(
 					entityName,
 					joinColumns[0].getMappedBy()
 			);
 			if ( propRef != null ) {
 				collValue.setReferencedPropertyName( propRef );
 				buildingContext.getMetadataCollector().addPropertyReference( collValue.getOwnerEntityName(), propRef );
 			}
 		}
 		String propRef = collValue.getReferencedPropertyName();
 		if ( propRef == null ) {
 			keyVal = collValue.getOwner().getIdentifier();
 		}
 		else {
 			keyVal = (KeyValue) collValue.getOwner()
 					.getReferencedProperty( propRef )
 					.getValue();
 		}
 		DependantValue key = new DependantValue( buildingContext.getMetadataCollector(), collValue.getCollectionTable(), keyVal );
 		key.setTypeName( null );
 		Ejb3Column.checkPropertyConsistency( joinColumns, collValue.getOwnerEntityName() );
 		key.setNullable( joinColumns.length == 0 || joinColumns[0].isNullable() );
 		key.setUpdateable( joinColumns.length == 0 || joinColumns[0].isUpdatable() );
 		key.setCascadeDeleteEnabled( cascadeDeleteEnabled );
 		collValue.setKey( key );
 		if ( property != null ) {
 			final ForeignKey fk = property.getAnnotation( ForeignKey.class );
 			if ( fk != null && !BinderHelper.isEmptyAnnotationValue( fk.name() ) ) {
 				key.setForeignKeyName( fk.name() );
 			}
 			else {
 				final CollectionTable collectionTableAnn = property.getAnnotation( CollectionTable.class );
 				if ( collectionTableAnn != null ) {
 					if ( collectionTableAnn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT ) {
 						key.setForeignKeyName( "none" );
 					}
 					else {
 						key.setForeignKeyName( StringHelper.nullIfEmpty( collectionTableAnn.foreignKey().name() ) );
+						key.setForeignKeyDefinition( StringHelper.nullIfEmpty( collectionTableAnn.foreignKey().foreignKeyDefinition() ) );
 					}
 				}
 				else {
 					final JoinTable joinTableAnn = property.getAnnotation( JoinTable.class );
 					if ( joinTableAnn != null ) {
 						String foreignKeyName = joinTableAnn.foreignKey().name();
+						String foreignKeyDefinition = joinTableAnn.foreignKey().foreignKeyDefinition();
 						ConstraintMode foreignKeyValue = joinTableAnn.foreignKey().value();
 						if ( joinTableAnn.joinColumns().length != 0 ) {
 							final JoinColumn joinColumnAnn = joinTableAnn.joinColumns()[0];
 							if ( "".equals( foreignKeyName ) ) {
 								foreignKeyName = joinColumnAnn.foreignKey().name();
+								foreignKeyDefinition = joinColumnAnn.foreignKey().foreignKeyDefinition();
 							}
 							if ( foreignKeyValue != ConstraintMode.NO_CONSTRAINT ) {
 								foreignKeyValue = joinColumnAnn.foreignKey().value();
 							}
 						}
 						if ( foreignKeyValue == ConstraintMode.NO_CONSTRAINT ) {
 							key.setForeignKeyName( "none" );
 						}
 						else {
 							key.setForeignKeyName( StringHelper.nullIfEmpty( foreignKeyName ) );
+							key.setForeignKeyDefinition( StringHelper.nullIfEmpty( foreignKeyDefinition ) );
 						}
 					}
 					else {
 						final JoinColumn joinColumnAnn = property.getAnnotation( JoinColumn.class );
 						if ( joinColumnAnn != null ) {
 							if ( joinColumnAnn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT ) {
 								key.setForeignKeyName( "none" );
 							}
 							else {
 								key.setForeignKeyName( StringHelper.nullIfEmpty( joinColumnAnn.foreignKey().name() ) );
+								key.setForeignKeyDefinition( StringHelper.nullIfEmpty( joinColumnAnn.foreignKey().foreignKeyDefinition() ) );
 							}
 						}
 					}
 				}
 			}
 		}
 
 		return key;
 	}
 
 	protected void bindManyToManySecondPass(
 			Collection collValue,
 			Map persistentClasses,
 			Ejb3JoinColumn[] joinColumns,
 			Ejb3JoinColumn[] inverseJoinColumns,
 			Ejb3Column[] elementColumns,
 			boolean isEmbedded,
 			XClass collType,
 			boolean ignoreNotFound, boolean unique,
 			boolean cascadeDeleteEnabled,
 			TableBinder associationTableBinder,
 			XProperty property,
 			PropertyHolder parentPropertyHolder,
 			MetadataBuildingContext buildingContext) throws MappingException {
 		if ( property == null ) {
 			throw new IllegalArgumentException( "null was passed for argument property" );
 		}
 
 		final PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( collType.getName() );
 		final String hqlOrderBy = extractHqlOrderBy( jpaOrderBy );
 
 		boolean isCollectionOfEntities = collectionEntity != null;
 		ManyToAny anyAnn = property.getAnnotation( ManyToAny.class );
 		if ( LOG.isDebugEnabled() ) {
 			String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 			if ( isCollectionOfEntities && unique ) {
 				LOG.debugf("Binding a OneToMany: %s through an association table", path);
 			}
 			else if (isCollectionOfEntities) {
 				LOG.debugf("Binding as ManyToMany: %s", path);
 			}
 			else if (anyAnn != null) {
 				LOG.debugf("Binding a ManyToAny: %s", path);
 			}
 			else {
 				LOG.debugf("Binding a collection of element: %s", path);
 			}
 		}
 		//check for user error
 		if ( !isCollectionOfEntities ) {
 			if ( property.isAnnotationPresent( ManyToMany.class ) || property.isAnnotationPresent( OneToMany.class ) ) {
 				String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 				throw new AnnotationException(
 						"Use of @OneToMany or @ManyToMany targeting an unmapped class: " + path + "[" + collType + "]"
 				);
 			}
 			else if ( anyAnn != null ) {
 				if ( parentPropertyHolder.getJoinTable( property ) == null ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					throw new AnnotationException(
 							"@JoinTable is mandatory when @ManyToAny is used: " + path
 					);
 				}
 			}
 			else {
 				JoinTable joinTableAnn = parentPropertyHolder.getJoinTable( property );
 				if ( joinTableAnn != null && joinTableAnn.inverseJoinColumns().length > 0 ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					throw new AnnotationException(
 							"Use of @JoinTable.inverseJoinColumns targeting an unmapped class: " + path + "[" + collType + "]"
 					);
 				}
 			}
 		}
 
 		boolean mappedBy = !BinderHelper.isEmptyAnnotationValue( joinColumns[0].getMappedBy() );
 		if ( mappedBy ) {
 			if ( !isCollectionOfEntities ) {
 				StringBuilder error = new StringBuilder( 80 )
 						.append(
 								"Collection of elements must not have mappedBy or association reference an unmapped entity: "
 						)
 						.append( collValue.getOwnerEntityName() )
 						.append( "." )
 						.append( joinColumns[0].getPropertyName() );
 				throw new AnnotationException( error.toString() );
 			}
 			Property otherSideProperty;
 			try {
 				otherSideProperty = collectionEntity.getRecursiveProperty( joinColumns[0].getMappedBy() );
 			}
 			catch (MappingException e) {
 				throw new AnnotationException(
 						"mappedBy reference an unknown target entity property: "
 								+ collType + "." + joinColumns[0].getMappedBy() + " in "
 								+ collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName()
 				);
 			}
 			Table table;
 			if ( otherSideProperty.getValue() instanceof Collection ) {
 				//this is a collection on the other side
 				table = ( (Collection) otherSideProperty.getValue() ).getCollectionTable();
 			}
 			else {
 				//This is a ToOne with a @JoinTable or a regular property
 				table = otherSideProperty.getValue().getTable();
 			}
 			collValue.setCollectionTable( table );
 			String entityName = collectionEntity.getEntityName();
 			for (Ejb3JoinColumn column : joinColumns) {
 				//column.setDefaultColumnHeader( joinColumns[0].getMappedBy() ); //seems not to be used, make sense
 				column.setManyToManyOwnerSideEntityName( entityName );
 			}
 		}
 		else {
 			//TODO: only for implicit columns?
 			//FIXME NamingStrategy
 			for (Ejb3JoinColumn column : joinColumns) {
 				String mappedByProperty = buildingContext.getMetadataCollector().getFromMappedBy(
 						collValue.getOwnerEntityName(), column.getPropertyName()
 				);
 				Table ownerTable = collValue.getOwner().getTable();
 				column.setMappedBy(
 						collValue.getOwner().getEntityName(),
 						collValue.getOwner().getJpaEntityName(),
 						buildingContext.getMetadataCollector().getLogicalTableName( ownerTable ),
 						mappedByProperty
 				);
 //				String header = ( mappedByProperty == null ) ? mappings.getLogicalTableName( ownerTable ) : mappedByProperty;
 //				column.setDefaultColumnHeader( header );
 			}
 			if ( StringHelper.isEmpty( associationTableBinder.getName() ) ) {
 				//default value
 				associationTableBinder.setDefaultName(
 						collValue.getOwner().getClassName(),
 						collValue.getOwner().getEntityName(),
 						collValue.getOwner().getJpaEntityName(),
 						buildingContext.getMetadataCollector().getLogicalTableName( collValue.getOwner().getTable() ),
 						collectionEntity != null ? collectionEntity.getClassName() : null,
 						collectionEntity != null ? collectionEntity.getEntityName() : null,
 						collectionEntity != null ? collectionEntity.getJpaEntityName() : null,
 						collectionEntity != null ? buildingContext.getMetadataCollector().getLogicalTableName(
 								collectionEntity.getTable()
 						) : null,
 						joinColumns[0].getPropertyName()
 				);
 			}
 			associationTableBinder.setJPA2ElementCollection( !isCollectionOfEntities && property.isAnnotationPresent( ElementCollection.class ));
 			collValue.setCollectionTable( associationTableBinder.bind() );
 		}
 		bindFilters( isCollectionOfEntities );
 		bindCollectionSecondPass( collValue, collectionEntity, joinColumns, cascadeDeleteEnabled, property, buildingContext );
 
 		ManyToOne element = null;
 		if ( isCollectionOfEntities ) {
 			element = new ManyToOne( buildingContext.getMetadataCollector(),  collValue.getCollectionTable() );
 			collValue.setElement( element );
 			element.setReferencedEntityName( collType.getName() );
 			//element.setFetchMode( fetchMode );
 			//element.setLazy( fetchMode != FetchMode.JOIN );
 			//make the second join non lazy
 			element.setFetchMode( FetchMode.JOIN );
 			element.setLazy( false );
 			element.setIgnoreNotFound( ignoreNotFound );
 			// as per 11.1.38 of JPA 2.0 spec, default to primary key if no column is specified by @OrderBy.
 			if ( hqlOrderBy != null ) {
 				collValue.setManyToManyOrdering(
 						buildOrderByClauseFromHql( hqlOrderBy, collectionEntity, collValue.getRole() )
 				);
 			}
 
 			final ForeignKey fk = property.getAnnotation( ForeignKey.class );
 			if ( fk != null && !BinderHelper.isEmptyAnnotationValue( fk.name() ) ) {
 				element.setForeignKeyName( fk.name() );
 			}
 			else {
 				final JoinTable joinTableAnn = property.getAnnotation( JoinTable.class );
 				if ( joinTableAnn != null ) {
 					String foreignKeyName = joinTableAnn.inverseForeignKey().name();
+					String foreignKeyDefinition = joinTableAnn.inverseForeignKey().foreignKeyDefinition();
 					ConstraintMode foreignKeyValue = joinTableAnn.foreignKey().value();
 					if ( joinTableAnn.inverseJoinColumns().length != 0 ) {
 						final JoinColumn joinColumnAnn = joinTableAnn.inverseJoinColumns()[0];
 						if ( "".equals( foreignKeyName ) ) {
 							foreignKeyName = joinColumnAnn.foreignKey().name();
+							foreignKeyDefinition = joinColumnAnn.foreignKey().foreignKeyDefinition();
 						}
 						if ( foreignKeyValue != ConstraintMode.NO_CONSTRAINT ) {
 							foreignKeyValue = joinColumnAnn.foreignKey().value();
 						}
 					}
 					if ( joinTableAnn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT ) {
 						element.setForeignKeyName( "none" );
 					}
 					else {
 						element.setForeignKeyName( StringHelper.nullIfEmpty( foreignKeyName ) );
+						element.setForeignKeyDefinition( StringHelper.nullIfEmpty( foreignKeyDefinition ) );
 					}
 				}
 			}
 		}
 		else if ( anyAnn != null ) {
 			//@ManyToAny
 			//Make sure that collTyp is never used during the @ManyToAny branch: it will be set to void.class
 			PropertyData inferredData = new PropertyInferredData(null, property, "unsupported", buildingContext.getBuildingOptions().getReflectionManager() );
 			//override the table
 			for (Ejb3Column column : inverseJoinColumns) {
 				column.setTable( collValue.getCollectionTable() );
 			}
 			Any any = BinderHelper.buildAnyValue(
 					anyAnn.metaDef(),
 					inverseJoinColumns,
 					anyAnn.metaColumn(),
 					inferredData,
 					cascadeDeleteEnabled,
 					Nullability.NO_CONSTRAINT,
 					propertyHolder,
 					new EntityBinder(),
 					true,
 					buildingContext
 			);
 			collValue.setElement( any );
 		}
 		else {
 			XClass elementClass;
 			AnnotatedClassType classType;
 
 			CollectionPropertyHolder holder = null;
 			if ( BinderHelper.PRIMITIVE_NAMES.contains( collType.getName() ) ) {
 				classType = AnnotatedClassType.NONE;
 				elementClass = null;
 
 				holder = PropertyHolderBuilder.buildPropertyHolder(
 						collValue,
 						collValue.getRole(),
 						null,
 						property,
 						parentPropertyHolder,
 						buildingContext
 				);
 			}
 			else {
 				elementClass = collType;
 				classType = buildingContext.getMetadataCollector().getClassType( elementClass );
 
 				holder = PropertyHolderBuilder.buildPropertyHolder(
 						collValue,
 						collValue.getRole(),
 						elementClass,
 						property,
 						parentPropertyHolder,
 						buildingContext
 				);
 
 				// 'parentPropertyHolder' is the PropertyHolder for the owner of the collection
 				// 'holder' is the CollectionPropertyHolder.
 				// 'property' is the collection XProperty
 				parentPropertyHolder.startingProperty( property );
 
 				//force in case of attribute override
 				boolean attributeOverride = property.isAnnotationPresent( AttributeOverride.class )
 						|| property.isAnnotationPresent( AttributeOverrides.class );
 				// todo : force in the case of Convert annotation(s) with embedded paths (beyond key/value prefixes)?
 				if ( isEmbedded || attributeOverride ) {
 					classType = AnnotatedClassType.EMBEDDABLE;
 				}
 			}
 
 			if ( AnnotatedClassType.EMBEDDABLE.equals( classType ) ) {
 				EntityBinder entityBinder = new EntityBinder();
 				PersistentClass owner = collValue.getOwner();
 				boolean isPropertyAnnotated;
 				//FIXME support @Access for collection of elements
 				//String accessType = access != null ? access.value() : null;
 				if ( owner.getIdentifierProperty() != null ) {
 					isPropertyAnnotated = owner.getIdentifierProperty().getPropertyAccessorName().equals( "property" );
 				}
 				else if ( owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0 ) {
 					Property prop = (Property) owner.getIdentifierMapper().getPropertyIterator().next();
 					isPropertyAnnotated = prop.getPropertyAccessorName().equals( "property" );
 				}
 				else {
 					throw new AssertionFailure( "Unable to guess collection property accessor name" );
 				}
 
 				PropertyData inferredData;
 				if ( isMap() ) {
 					//"value" is the JPA 2 prefix for map values (used to be "element")
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "element", elementClass );
 					}
 					else {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "value", elementClass );
 					}
 				}
 				else {
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "element", elementClass );
 					}
 					else {
 						//"collection&&element" is not a valid property name => placeholder
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "collection&&element", elementClass );
 					}
 				}
 				//TODO be smart with isNullable
 				boolean isNullable = true;
 				Component component = AnnotationBinder.fillComponent(
 						holder,
 						inferredData,
 						isPropertyAnnotated ? AccessType.PROPERTY : AccessType.FIELD,
 						isNullable,
 						entityBinder,
 						false,
 						false,
 						true,
 						buildingContext,
 						inheritanceStatePerClass
 				);
 
 				collValue.setElement( component );
 
 				if ( StringHelper.isNotEmpty( hqlOrderBy ) ) {
 					String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
 					String orderBy = adjustUserSuppliedValueCollectionOrderingFragment( hqlOrderBy );
 					if ( orderBy != null ) {
 						collValue.setOrderBy( orderBy );
 					}
 				}
 			}
 			else {
 				holder.prepare( property );
 
 				SimpleValueBinder elementBinder = new SimpleValueBinder();
 				elementBinder.setBuildingContext( buildingContext );
 				elementBinder.setReturnedClassName( collType.getName() );
 				if ( elementColumns == null || elementColumns.length == 0 ) {
 					elementColumns = new Ejb3Column[1];
 					Ejb3Column column = new Ejb3Column();
 					column.setImplicit( false );
 					//not following the spec but more clean
 					column.setNullable( true );
 					column.setLength( Ejb3Column.DEFAULT_COLUMN_LENGTH );
 					column.setLogicalColumnName( Collection.DEFAULT_ELEMENT_COLUMN_NAME );
 					//TODO create an EMPTY_JOINS collection
 					column.setJoins( new HashMap<String, Join>() );
 					column.setBuildingContext( buildingContext );
 					column.bind();
 					elementColumns[0] = column;
 				}
 				//override the table
 				for (Ejb3Column column : elementColumns) {
 					column.setTable( collValue.getCollectionTable() );
 				}
 				elementBinder.setColumns( elementColumns );
 				elementBinder.setType(
 						property,
 						elementClass,
 						collValue.getOwnerEntityName(),
 						holder.resolveElementAttributeConverterDescriptor( property, elementClass )
 				);
 				elementBinder.setPersistentClassName( propertyHolder.getEntityName() );
 				elementBinder.setAccessType( accessType );
 				collValue.setElement( elementBinder.make() );
 				String orderBy = adjustUserSuppliedValueCollectionOrderingFragment( hqlOrderBy );
 				if ( orderBy != null ) {
 					collValue.setOrderBy( orderBy );
 				}
 			}
 		}
 
 		checkFilterConditions( collValue );
 
 		//FIXME: do optional = false
 		if ( isCollectionOfEntities ) {
 			bindManytoManyInverseFk( collectionEntity, inverseJoinColumns, element, unique, buildingContext );
 		}
 
 	}
 
 	private String extractHqlOrderBy(javax.persistence.OrderBy jpaOrderBy) {
 		if ( jpaOrderBy != null ) {
 			return jpaOrderBy.value(); // Null not possible. In case of empty expression, apply default ordering.
 		}
 		return null; // @OrderBy not found.
 	}
 
 	private static void checkFilterConditions(Collection collValue) {
 		//for now it can't happen, but sometime soon...
 		if ( ( collValue.getFilters().size() != 0 || StringHelper.isNotEmpty( collValue.getWhere() ) ) &&
 				collValue.getFetchMode() == FetchMode.JOIN &&
 				!( collValue.getElement() instanceof SimpleValue ) && //SimpleValue (CollectionOfElements) are always SELECT but it does not matter
 				collValue.getElement().getFetchMode() != FetchMode.JOIN ) {
 			throw new MappingException(
 					"@ManyToMany or @CollectionOfElements defining filter or where without join fetching "
 							+ "not valid within collection using join fetching[" + collValue.getRole() + "]"
 			);
 		}
 	}
 
 	private static void bindCollectionSecondPass(
 			Collection collValue,
 			PersistentClass collectionEntity,
 			Ejb3JoinColumn[] joinColumns,
 			boolean cascadeDeleteEnabled,
 			XProperty property,
 			MetadataBuildingContext buildingContext) {
 		try {
 			BinderHelper.createSyntheticPropertyReference(
 					joinColumns,
 					collValue.getOwner(),
 					collectionEntity,
 					collValue,
 					false,
 					buildingContext
 			);
 		}
 		catch (AnnotationException ex) {
 			throw new AnnotationException( "Unable to map collection " + collValue.getOwner().getClassName() + "." + property.getName(), ex );
 		}
 		SimpleValue key = buildCollectionKey( collValue, joinColumns, cascadeDeleteEnabled, property, buildingContext );
 		if ( property.isAnnotationPresent( ElementCollection.class ) && joinColumns.length > 0 ) {
 			joinColumns[0].setJPA2ElementCollection( true );
 		}
 		TableBinder.bindFk( collValue.getOwner(), collectionEntity, joinColumns, key, false, buildingContext );
 	}
 
 	public void setCascadeDeleteEnabled(boolean onDeleteCascade) {
 		this.cascadeDeleteEnabled = onDeleteCascade;
 	}
 
 	private String safeCollectionRole() {
 		if ( propertyHolder != null ) {
 			return propertyHolder.getEntityName() + "." + propertyName;
 		}
 		else {
 			return "";
 		}
 	}
 
 
 	/**
 	 * bind the inverse FK of a ManyToMany
 	 * If we are in a mappedBy case, read the columns from the associated
 	 * collection element
 	 * Otherwise delegates to the usual algorithm
 	 */
 	public static void bindManytoManyInverseFk(
 			PersistentClass referencedEntity,
 			Ejb3JoinColumn[] columns,
 			SimpleValue value,
 			boolean unique,
 			MetadataBuildingContext buildingContext) {
 		final String mappedBy = columns[0].getMappedBy();
 		if ( StringHelper.isNotEmpty( mappedBy ) ) {
 			final Property property = referencedEntity.getRecursiveProperty( mappedBy );
 			Iterator mappedByColumns;
 			if ( property.getValue() instanceof Collection ) {
 				mappedByColumns = ( (Collection) property.getValue() ).getKey().getColumnIterator();
 			}
 			else {
 				//find the appropriate reference key, can be in a join
 				Iterator joinsIt = referencedEntity.getJoinIterator();
 				KeyValue key = null;
 				while ( joinsIt.hasNext() ) {
 					Join join = (Join) joinsIt.next();
 					if ( join.containsProperty( property ) ) {
 						key = join.getKey();
 						break;
 					}
 				}
 				if ( key == null ) key = property.getPersistentClass().getIdentifier();
 				mappedByColumns = key.getColumnIterator();
 			}
 			while ( mappedByColumns.hasNext() ) {
 				Column column = (Column) mappedByColumns.next();
 				columns[0].linkValueUsingAColumnCopy( column, value );
 			}
 			String referencedPropertyName =
 					buildingContext.getMetadataCollector().getPropertyReferencedAssociation(
 							"inverse__" + referencedEntity.getEntityName(), mappedBy
 					);
 			if ( referencedPropertyName != null ) {
 				//TODO always a many to one?
 				( (ManyToOne) value ).setReferencedPropertyName( referencedPropertyName );
 				buildingContext.getMetadataCollector().addUniquePropertyReference(
 						referencedEntity.getEntityName(),
 						referencedPropertyName
 				);
 			}
 			( (ManyToOne) value ).setReferenceToPrimaryKey( referencedPropertyName == null );
 			value.createForeignKey();
 		}
 		else {
 			BinderHelper.createSyntheticPropertyReference( columns, referencedEntity, null, value, true, buildingContext );
 			TableBinder.bindFk( referencedEntity, null, columns, value, unique, buildingContext );
 		}
 	}
 
 	public void setFkJoinColumns(Ejb3JoinColumn[] ejb3JoinColumns) {
 		this.fkJoinColumns = ejb3JoinColumns;
 	}
 
 	public void setExplicitAssociationTable(boolean explicitAssocTable) {
 		this.isExplicitAssociationTable = explicitAssocTable;
 	}
 
 	public void setElementColumns(Ejb3Column[] elementColumns) {
 		this.elementColumns = elementColumns;
 	}
 
 	public void setEmbedded(boolean annotationPresent) {
 		this.isEmbedded = annotationPresent;
 	}
 
 	public void setProperty(XProperty property) {
 		this.property = property;
 	}
 
 	public void setIgnoreNotFound(boolean ignoreNotFound) {
 		this.ignoreNotFound = ignoreNotFound;
 	}
 
 	public void setMapKeyColumns(Ejb3Column[] mapKeyColumns) {
 		this.mapKeyColumns = mapKeyColumns;
 	}
 
 	public void setMapKeyManyToManyColumns(Ejb3JoinColumn[] mapJoinColumns) {
 		this.mapKeyManyToManyColumns = mapJoinColumns;
 	}
 
 	public void setLocalGenerators(HashMap<String, IdentifierGeneratorDefinition> localGenerators) {
 		this.localGenerators = localGenerators;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
index 510af35f29..5e3a3cedf5 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
@@ -1,1198 +1,1199 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.cfg.annotations;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import javax.persistence.Access;
 import javax.persistence.ConstraintMode;
 import javax.persistence.Entity;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.NamedEntityGraph;
 import javax.persistence.NamedEntityGraphs;
 import javax.persistence.PrimaryKeyJoinColumn;
 import javax.persistence.SecondaryTable;
 import javax.persistence.SecondaryTables;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.BatchSize;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.DynamicInsert;
 import org.hibernate.annotations.DynamicUpdate;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.Filter;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.Loader;
 import org.hibernate.annotations.NaturalIdCache;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.annotations.OptimisticLocking;
 import org.hibernate.annotations.Persister;
 import org.hibernate.annotations.Polymorphism;
 import org.hibernate.annotations.PolymorphismType;
 import org.hibernate.annotations.Proxy;
 import org.hibernate.annotations.RowId;
 import org.hibernate.annotations.SQLDelete;
 import org.hibernate.annotations.SQLDeleteAll;
 import org.hibernate.annotations.SQLInsert;
 import org.hibernate.annotations.SQLUpdate;
 import org.hibernate.annotations.SelectBeforeUpdate;
 import org.hibernate.annotations.Subselect;
 import org.hibernate.annotations.Synchronize;
 import org.hibernate.annotations.Tables;
 import org.hibernate.annotations.Tuplizer;
 import org.hibernate.annotations.Tuplizers;
 import org.hibernate.annotations.Where;
 import org.hibernate.annotations.common.reflection.ReflectionManager;
 import org.hibernate.annotations.common.reflection.XAnnotatedElement;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.boot.model.naming.EntityNaming;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
 import org.hibernate.boot.model.naming.NamingStrategyHelper;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.InFlightMetadataCollector;
 import org.hibernate.boot.spi.MetadataBuildingContext;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.InheritanceState;
 import org.hibernate.cfg.ObjectNameSource;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.SingleTableSubclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TableOwner;
 import org.hibernate.mapping.Value;
 
 import org.jboss.logging.Logger;
 
 import static org.hibernate.cfg.BinderHelper.toAliasEntityMap;
 import static org.hibernate.cfg.BinderHelper.toAliasTableMap;
 
 
 /**
  * Stateful holder and processor for binding Entity information
  *
  * @author Emmanuel Bernard
  */
 public class EntityBinder {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityBinder.class.getName());
 	private static final String NATURAL_ID_CACHE_SUFFIX = "##NaturalId";
 
 	private MetadataBuildingContext context;
 
 	private String name;
 	private XClass annotatedClass;
 	private PersistentClass persistentClass;
 	private String discriminatorValue = "";
 	private Boolean forceDiscriminator;
 	private Boolean insertableDiscriminator;
 	private boolean dynamicInsert;
 	private boolean dynamicUpdate;
 	private boolean explicitHibernateEntityAnnotation;
 	private OptimisticLockType optimisticLockType;
 	private PolymorphismType polymorphismType;
 	private boolean selectBeforeUpdate;
 	private int batchSize;
 	private boolean lazy;
 	private XClass proxyClass;
 	private String where;
 	// todo : we should defer to InFlightMetadataCollector.EntityTableXref for secondary table tracking;
 	//		atm we use both from here; HBM binding solely uses InFlightMetadataCollector.EntityTableXref
 	private java.util.Map<String, Join> secondaryTables = new HashMap<String, Join>();
 	private java.util.Map<String, Object> secondaryTableJoins = new HashMap<String, Object>();
 	private String cacheConcurrentStrategy;
 	private String cacheRegion;
 	private String naturalIdCacheRegion;
 	private List<Filter> filters = new ArrayList<Filter>();
 	private InheritanceState inheritanceState;
 	private boolean ignoreIdAnnotations;
 	private boolean cacheLazyProperty;
 	private AccessType propertyAccessType = AccessType.DEFAULT;
 	private boolean wrapIdsInEmbeddedComponents;
 	private String subselect;
 
 	public boolean wrapIdsInEmbeddedComponents() {
 		return wrapIdsInEmbeddedComponents;
 	}
 
 	/**
 	 * Use as a fake one for Collection of elements
 	 */
 	public EntityBinder() {
 	}
 
 	public EntityBinder(
 			Entity ejb3Ann,
 			org.hibernate.annotations.Entity hibAnn,
 			XClass annotatedClass,
 			PersistentClass persistentClass,
 			MetadataBuildingContext context) {
 		this.context = context;
 		this.persistentClass = persistentClass;
 		this.annotatedClass = annotatedClass;
 		bindEjb3Annotation( ejb3Ann );
 		bindHibernateAnnotation( hibAnn );
 	}
 
 	/**
 	 * For the most part, this is a simple delegation to {@link PersistentClass#isPropertyDefinedInHierarchy},
 	 * afterQuery verifying that PersistentClass is indeed set here.
 	 *
 	 * @param name The name of the property to check
 	 *
 	 * @return {@code true} if a property by that given name does already exist in the super hierarchy.
 	 */
 	@SuppressWarnings("SimplifiableIfStatement")
 	public boolean isPropertyDefinedInSuperHierarchy(String name) {
 		// Yes, yes... persistentClass can be null because EntityBinder can be used
 		// to bind components as well, of course...
 		if ( persistentClass == null ) {
 			return false;
 		}
 
 		return persistentClass.isPropertyDefinedInSuperHierarchy( name );
 	}
 
 	@SuppressWarnings("SimplifiableConditionalExpression")
 	private void bindHibernateAnnotation(org.hibernate.annotations.Entity hibAnn) {
 		{
 			final DynamicInsert dynamicInsertAnn = annotatedClass.getAnnotation( DynamicInsert.class );
 			this.dynamicInsert = dynamicInsertAnn == null
 					? ( hibAnn == null ? false : hibAnn.dynamicInsert() )
 					: dynamicInsertAnn.value();
 		}
 
 		{
 			final DynamicUpdate dynamicUpdateAnn = annotatedClass.getAnnotation( DynamicUpdate.class );
 			this.dynamicUpdate = dynamicUpdateAnn == null
 					? ( hibAnn == null ? false : hibAnn.dynamicUpdate() )
 					: dynamicUpdateAnn.value();
 		}
 
 		{
 			final SelectBeforeUpdate selectBeforeUpdateAnn = annotatedClass.getAnnotation( SelectBeforeUpdate.class );
 			this.selectBeforeUpdate = selectBeforeUpdateAnn == null
 					? ( hibAnn == null ? false : hibAnn.selectBeforeUpdate() )
 					: selectBeforeUpdateAnn.value();
 		}
 
 		{
 			final OptimisticLocking optimisticLockingAnn = annotatedClass.getAnnotation( OptimisticLocking.class );
 			this.optimisticLockType = optimisticLockingAnn == null
 					? ( hibAnn == null ? OptimisticLockType.VERSION : hibAnn.optimisticLock() )
 					: optimisticLockingAnn.type();
 		}
 
 		{
 			final Polymorphism polymorphismAnn = annotatedClass.getAnnotation( Polymorphism.class );
 			this.polymorphismType = polymorphismAnn == null
 					? ( hibAnn == null ? PolymorphismType.IMPLICIT : hibAnn.polymorphism() )
 					: polymorphismAnn.type();
 		}
 
 		if ( hibAnn != null ) {
 			// used later in bind for logging
 			explicitHibernateEntityAnnotation = true;
 			//persister handled in bind
 		}
 	}
 
 	private void bindEjb3Annotation(Entity ejb3Ann) {
 		if ( ejb3Ann == null ) throw new AssertionFailure( "@Entity should always be not null" );
 		if ( BinderHelper.isEmptyAnnotationValue( ejb3Ann.name() ) ) {
 			name = StringHelper.unqualify( annotatedClass.getName() );
 		}
 		else {
 			name = ejb3Ann.name();
 		}
 	}
 
 	public boolean isRootEntity() {
 		// This is the best option I can think of here since PersistentClass is most likely not yet fully populated
 		return persistentClass instanceof RootClass;
 	}
 
 	public void setDiscriminatorValue(String discriminatorValue) {
 		this.discriminatorValue = discriminatorValue;
 	}
 
 	public void setForceDiscriminator(boolean forceDiscriminator) {
 		this.forceDiscriminator = forceDiscriminator;
 	}
 
 	public void setInsertableDiscriminator(boolean insertableDiscriminator) {
 		this.insertableDiscriminator = insertableDiscriminator;
 	}
 
 	public void bindEntity() {
 		persistentClass.setAbstract( annotatedClass.isAbstract() );
 		persistentClass.setClassName( annotatedClass.getName() );
 		persistentClass.setJpaEntityName(name);
 		//persistentClass.setDynamic(false); //no longer needed with the Entity name refactoring?
 		persistentClass.setEntityName( annotatedClass.getName() );
 		bindDiscriminatorValue();
 
 		persistentClass.setLazy( lazy );
 		if ( proxyClass != null ) {
 			persistentClass.setProxyInterfaceName( proxyClass.getName() );
 		}
 		persistentClass.setDynamicInsert( dynamicInsert );
 		persistentClass.setDynamicUpdate( dynamicUpdate );
 
 		if ( persistentClass instanceof RootClass ) {
 			RootClass rootClass = (RootClass) persistentClass;
 			boolean mutable = true;
 			//priority on @Immutable, then @Entity.mutable()
 			if ( annotatedClass.isAnnotationPresent( Immutable.class ) ) {
 				mutable = false;
 			}
 			else {
 				org.hibernate.annotations.Entity entityAnn =
 						annotatedClass.getAnnotation( org.hibernate.annotations.Entity.class );
 				if ( entityAnn != null ) {
 					mutable = entityAnn.mutable();
 				}
 			}
 			rootClass.setMutable( mutable );
 			rootClass.setExplicitPolymorphism( isExplicitPolymorphism( polymorphismType ) );
 			if ( StringHelper.isNotEmpty( where ) ) rootClass.setWhere( where );
 			if ( cacheConcurrentStrategy != null ) {
 				rootClass.setCacheConcurrencyStrategy( cacheConcurrentStrategy );
 				rootClass.setCacheRegionName( cacheRegion );
 				rootClass.setLazyPropertiesCacheable( cacheLazyProperty );
 			}
 			rootClass.setNaturalIdCacheRegionName( naturalIdCacheRegion );
 			boolean forceDiscriminatorInSelects = forceDiscriminator == null
 					? context.getBuildingOptions().shouldImplicitlyForceDiscriminatorInSelect()
 					: forceDiscriminator;
 			rootClass.setForceDiscriminator( forceDiscriminatorInSelects );
 			if( insertableDiscriminator != null) {
 				rootClass.setDiscriminatorInsertable( insertableDiscriminator );
 			}
 		}
 		else {
 			if (explicitHibernateEntityAnnotation) {
 				LOG.entityAnnotationOnNonRoot(annotatedClass.getName());
 			}
 			if (annotatedClass.isAnnotationPresent(Immutable.class)) {
 				LOG.immutableAnnotationOnNonRoot(annotatedClass.getName());
 			}
 		}
 		persistentClass.setOptimisticLockStyle( getVersioning( optimisticLockType ) );
 		persistentClass.setSelectBeforeUpdate( selectBeforeUpdate );
 
 		//set persister if needed
 		Persister persisterAnn = annotatedClass.getAnnotation( Persister.class );
 		Class persister = null;
 		if ( persisterAnn != null ) {
 			persister = persisterAnn.impl();
 		}
 		else {
 			org.hibernate.annotations.Entity entityAnn = annotatedClass.getAnnotation( org.hibernate.annotations.Entity.class );
 			if ( entityAnn != null && !BinderHelper.isEmptyAnnotationValue( entityAnn.persister() ) ) {
 				try {
 					persister = context.getClassLoaderAccess().classForName( entityAnn.persister() );
 				}
 				catch (ClassLoadingException e) {
 					throw new AnnotationException( "Could not find persister class: " + entityAnn.persister(), e );
 				}
 			}
 		}
 		if ( persister != null ) {
 			persistentClass.setEntityPersisterClass( persister );
 		}
 
 		persistentClass.setBatchSize( batchSize );
 
 		//SQL overriding
 		SQLInsert sqlInsert = annotatedClass.getAnnotation( SQLInsert.class );
 		SQLUpdate sqlUpdate = annotatedClass.getAnnotation( SQLUpdate.class );
 		SQLDelete sqlDelete = annotatedClass.getAnnotation( SQLDelete.class );
 		SQLDeleteAll sqlDeleteAll = annotatedClass.getAnnotation( SQLDeleteAll.class );
 		Loader loader = annotatedClass.getAnnotation( Loader.class );
 
 		if ( sqlInsert != null ) {
 			persistentClass.setCustomSQLInsert( sqlInsert.sql().trim(), sqlInsert.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlInsert.check().toString().toLowerCase(Locale.ROOT) )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			persistentClass.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlUpdate.check().toString().toLowerCase(Locale.ROOT) )
 			);
 		}
 		if ( sqlDelete != null ) {
 			persistentClass.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDelete.check().toString().toLowerCase(Locale.ROOT) )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			persistentClass.setCustomSQLDelete( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase(Locale.ROOT) )
 			);
 		}
 		if ( loader != null ) {
 			persistentClass.setLoaderName( loader.namedQuery() );
 		}
 
 		final JdbcEnvironment jdbcEnvironment = context.getMetadataCollector().getDatabase().getJdbcEnvironment();
 		if ( annotatedClass.isAnnotationPresent( Synchronize.class )) {
 			Synchronize synchronizedWith = annotatedClass.getAnnotation(Synchronize.class);
 
 			String [] tables = synchronizedWith.value();
 			for (String table : tables) {
 				persistentClass.addSynchronizedTable(
 						context.getBuildingOptions().getPhysicalNamingStrategy().toPhysicalTableName(
 								jdbcEnvironment.getIdentifierHelper().toIdentifier( table ),
 								jdbcEnvironment
 						).render( jdbcEnvironment.getDialect() )
 				);
 			}
 		}
 
 		if ( annotatedClass.isAnnotationPresent(Subselect.class )) {
 			Subselect subselect = annotatedClass.getAnnotation(Subselect.class);
 			this.subselect = subselect.value();
 		}
 
 		//tuplizers
 		if ( annotatedClass.isAnnotationPresent( Tuplizers.class ) ) {
 			for (Tuplizer tuplizer : annotatedClass.getAnnotation( Tuplizers.class ).value()) {
 				EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
 				//todo tuplizer.entityModeType
 				persistentClass.addTuplizer( mode, tuplizer.impl().getName() );
 			}
 		}
 		if ( annotatedClass.isAnnotationPresent( Tuplizer.class ) ) {
 			Tuplizer tuplizer = annotatedClass.getAnnotation( Tuplizer.class );
 			EntityMode mode = EntityMode.parse( tuplizer.entityMode() );
 			//todo tuplizer.entityModeType
 			persistentClass.addTuplizer( mode, tuplizer.impl().getName() );
 		}
 
 		for ( Filter filter : filters ) {
 			String filterName = filter.name();
 			String cond = filter.condition();
 			if ( BinderHelper.isEmptyAnnotationValue( cond ) ) {
 				FilterDefinition definition = context.getMetadataCollector().getFilterDefinition( filterName );
 				cond = definition == null ? null : definition.getDefaultFilterCondition();
 				if ( StringHelper.isEmpty( cond ) ) {
 					throw new AnnotationException(
 							"no filter condition found for filter " + filterName + " in " + this.name
 					);
 				}
 			}
 			persistentClass.addFilter(filterName, cond, filter.deduceAliasInjectionPoints(), 
 					toAliasTableMap(filter.aliases()), toAliasEntityMap(filter.aliases()));
 		}
 		LOG.debugf( "Import with entity name %s", name );
 		try {
 			context.getMetadataCollector().addImport( name, persistentClass.getEntityName() );
 			String entityName = persistentClass.getEntityName();
 			if ( !entityName.equals( name ) ) {
 				context.getMetadataCollector().addImport( entityName, entityName );
 			}
 		}
 		catch (MappingException me) {
 			throw new AnnotationException( "Use of the same entity name twice: " + name, me );
 		}
 
 		processNamedEntityGraphs();
 	}
 
 	private void processNamedEntityGraphs() {
 		processNamedEntityGraph( annotatedClass.getAnnotation( NamedEntityGraph.class ) );
 		final NamedEntityGraphs graphs = annotatedClass.getAnnotation( NamedEntityGraphs.class );
 		if ( graphs != null ) {
 			for ( NamedEntityGraph graph : graphs.value() ) {
 				processNamedEntityGraph( graph );
 			}
 		}
 	}
 
 	private void processNamedEntityGraph(NamedEntityGraph annotation) {
 		if ( annotation == null ) {
 			return;
 		}
 		context.getMetadataCollector().addNamedEntityGraph(
 				new NamedEntityGraphDefinition( annotation, name, persistentClass.getEntityName() )
 		);
 	}
 	
 	public void bindDiscriminatorValue() {
 		if ( StringHelper.isEmpty( discriminatorValue ) ) {
 			Value discriminator = persistentClass.getDiscriminator();
 			if ( discriminator == null ) {
 				persistentClass.setDiscriminatorValue( name );
 			}
 			else if ( "character".equals( discriminator.getType().getName() ) ) {
 				throw new AnnotationException(
 						"Using default @DiscriminatorValue for a discriminator of type CHAR is not safe"
 				);
 			}
 			else if ( "integer".equals( discriminator.getType().getName() ) ) {
 				persistentClass.setDiscriminatorValue( String.valueOf( name.hashCode() ) );
 			}
 			else {
 				persistentClass.setDiscriminatorValue( name ); //Spec compliant
 			}
 		}
 		else {
 			//persistentClass.getDiscriminator()
 			persistentClass.setDiscriminatorValue( discriminatorValue );
 		}
 	}
 
 	OptimisticLockStyle getVersioning(OptimisticLockType type) {
 		switch ( type ) {
 			case VERSION:
 				return OptimisticLockStyle.VERSION;
 			case NONE:
 				return OptimisticLockStyle.NONE;
 			case DIRTY:
 				return OptimisticLockStyle.DIRTY;
 			case ALL:
 				return OptimisticLockStyle.ALL;
 			default:
 				throw new AssertionFailure( "optimistic locking not supported: " + type );
 		}
 	}
 
 	private boolean isExplicitPolymorphism(PolymorphismType type) {
 		switch ( type ) {
 			case IMPLICIT:
 				return false;
 			case EXPLICIT:
 				return true;
 			default:
 				throw new AssertionFailure( "Unknown polymorphism type: " + type );
 		}
 	}
 
 	public void setBatchSize(BatchSize sizeAnn) {
 		if ( sizeAnn != null ) {
 			batchSize = sizeAnn.size();
 		}
 		else {
 			batchSize = -1;
 		}
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public void setProxy(Proxy proxy) {
 		if ( proxy != null ) {
 			lazy = proxy.lazy();
 			if ( !lazy ) {
 				proxyClass = null;
 			}
 			else {
 				final ReflectionManager reflectionManager = context.getBuildingOptions().getReflectionManager();
 				if ( AnnotationBinder.isDefault( reflectionManager.toXClass( proxy.proxyClass() ), context ) ) {
 					proxyClass = annotatedClass;
 				}
 				else {
 					proxyClass = reflectionManager.toXClass( proxy.proxyClass() );
 				}
 			}
 		}
 		else {
 			lazy = true; //needed to allow association lazy loading.
 			proxyClass = annotatedClass;
 		}
 	}
 
 	public void setWhere(Where whereAnn) {
 		if ( whereAnn != null ) {
 			where = whereAnn.clause();
 		}
 	}
 
 	public void setWrapIdsInEmbeddedComponents(boolean wrapIdsInEmbeddedComponents) {
 		this.wrapIdsInEmbeddedComponents = wrapIdsInEmbeddedComponents;
 	}
 
 
 	private static class EntityTableObjectNameSource implements ObjectNameSource {
 		private final String explicitName;
 		private final String logicalName;
 
 		private EntityTableObjectNameSource(String explicitName, String entityName) {
 			this.explicitName = explicitName;
 			this.logicalName = StringHelper.isNotEmpty( explicitName )
 					? explicitName
 					: StringHelper.unqualify( entityName );
 		}
 
 		public String getExplicitName() {
 			return explicitName;
 		}
 
 		public String getLogicalName() {
 			return logicalName;
 		}
 	}
 
 	private static class EntityTableNamingStrategyHelper implements NamingStrategyHelper {
 		private final String className;
 		private final String entityName;
 		private final String jpaEntityName;
 
 		private EntityTableNamingStrategyHelper(String className, String entityName, String jpaEntityName) {
 			this.className = className;
 			this.entityName = entityName;
 			this.jpaEntityName = jpaEntityName;
 		}
 
 		public Identifier determineImplicitName(final MetadataBuildingContext buildingContext) {
 			return buildingContext.getBuildingOptions().getImplicitNamingStrategy().determinePrimaryTableName(
 					new ImplicitEntityNameSource() {
 						private final EntityNaming entityNaming = new EntityNaming() {
 							@Override
 							public String getClassName() {
 								return className;
 							}
 
 							@Override
 							public String getEntityName() {
 								return entityName;
 							}
 
 							@Override
 							public String getJpaEntityName() {
 								return jpaEntityName;
 							}
 						};
 
 						@Override
 						public EntityNaming getEntityNaming() {
 							return entityNaming;
 						}
 
 						@Override
 						public MetadataBuildingContext getBuildingContext() {
 							return buildingContext;
 						}
 					}
 			);
 		}
 
 		@Override
 		public Identifier handleExplicitName(String explicitName, MetadataBuildingContext buildingContext) {
 			return buildingContext.getMetadataCollector()
 					.getDatabase()
 					.getJdbcEnvironment()
 					.getIdentifierHelper()
 					.toIdentifier( explicitName );
 		}
 
 		@Override
 		public Identifier toPhysicalName(Identifier logicalName, MetadataBuildingContext buildingContext) {
 			return buildingContext.getBuildingOptions().getPhysicalNamingStrategy().toPhysicalTableName(
 					logicalName,
 					buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment()
 			);
 		}
 	}
 
 	public void bindTableForDiscriminatedSubclass(InFlightMetadataCollector.EntityTableXref superTableXref) {
 		if ( !SingleTableSubclass.class.isInstance( persistentClass ) ) {
 			throw new AssertionFailure(
 					"Was expecting a discriminated subclass [" + SingleTableSubclass.class.getName() +
 							"] but found [" + persistentClass.getClass().getName() + "] for entity [" +
 							persistentClass.getEntityName() + "]"
 			);
 		}
 
 		context.getMetadataCollector().addEntityTableXref(
 				persistentClass.getEntityName(),
 				context.getMetadataCollector().getDatabase().toIdentifier(
 						context.getMetadataCollector().getLogicalTableName(
 								superTableXref.getPrimaryTable()
 						)
 				),
 				superTableXref.getPrimaryTable(),
 				superTableXref
 		);
 	}
 
 	public void bindTable(
 			String schema,
 			String catalog,
 			String tableName,
 			List<UniqueConstraintHolder> uniqueConstraints,
 			String constraints,
 			InFlightMetadataCollector.EntityTableXref denormalizedSuperTableXref) {
 		EntityTableNamingStrategyHelper namingStrategyHelper = new EntityTableNamingStrategyHelper(
 				persistentClass.getClassName(),
 				persistentClass.getEntityName(),
 				name
 		);
 
 		final Identifier logicalName;
 		if ( StringHelper.isNotEmpty( tableName ) ) {
 			logicalName = namingStrategyHelper.handleExplicitName( tableName, context );
 		}
 		else {
 			logicalName = namingStrategyHelper.determineImplicitName( context );
 		}
 
 		final Table table = TableBinder.buildAndFillTable(
 				schema,
 				catalog,
 				logicalName,
 				persistentClass.isAbstract(),
 				uniqueConstraints,
 				null,
 				constraints,
 				context,
 				this.subselect,
 				denormalizedSuperTableXref
 		);
 		final RowId rowId = annotatedClass.getAnnotation( RowId.class );
 		if ( rowId != null ) {
 			table.setRowId( rowId.value() );
 		}
 
 		context.getMetadataCollector().addEntityTableXref(
 				persistentClass.getEntityName(),
 				logicalName,
 				table,
 				denormalizedSuperTableXref
 		);
 
 		if ( persistentClass instanceof TableOwner ) {
 			LOG.debugf( "Bind entity %s on table %s", persistentClass.getEntityName(), table.getName() );
 			( (TableOwner) persistentClass ).setTable( table );
 		}
 		else {
 			throw new AssertionFailure( "binding a table for a subclass" );
 		}
 	}
 
 	public void finalSecondaryTableBinding(PropertyHolder propertyHolder) {
 		/*
 		 * Those operations has to be done afterQuery the id definition of the persistence class.
 		 * ie afterQuery the properties parsing
 		 */
 		Iterator joins = secondaryTables.values().iterator();
 		Iterator joinColumns = secondaryTableJoins.values().iterator();
 
 		while ( joins.hasNext() ) {
 			Object uncastedColumn = joinColumns.next();
 			Join join = (Join) joins.next();
 			createPrimaryColumnsToSecondaryTable( uncastedColumn, propertyHolder, join );
 		}
 	}
 
 	private void createPrimaryColumnsToSecondaryTable(Object uncastedColumn, PropertyHolder propertyHolder, Join join) {
 		Ejb3JoinColumn[] ejb3JoinColumns;
 		PrimaryKeyJoinColumn[] pkColumnsAnn = null;
 		JoinColumn[] joinColumnsAnn = null;
 		if ( uncastedColumn instanceof PrimaryKeyJoinColumn[] ) {
 			pkColumnsAnn = (PrimaryKeyJoinColumn[]) uncastedColumn;
 		}
 		if ( uncastedColumn instanceof JoinColumn[] ) {
 			joinColumnsAnn = (JoinColumn[]) uncastedColumn;
 		}
 		if ( pkColumnsAnn == null && joinColumnsAnn == null ) {
 			ejb3JoinColumns = new Ejb3JoinColumn[1];
 			ejb3JoinColumns[0] = Ejb3JoinColumn.buildJoinColumn(
 					null,
 					null,
 					persistentClass.getIdentifier(),
 					secondaryTables,
 					propertyHolder,
 					context
 			);
 		}
 		else {
 			int nbrOfJoinColumns = pkColumnsAnn != null ?
 					pkColumnsAnn.length :
 					joinColumnsAnn.length;
 			if ( nbrOfJoinColumns == 0 ) {
 				ejb3JoinColumns = new Ejb3JoinColumn[1];
 				ejb3JoinColumns[0] = Ejb3JoinColumn.buildJoinColumn(
 						null,
 						null,
 						persistentClass.getIdentifier(),
 						secondaryTables,
 						propertyHolder,
 						context
 				);
 			}
 			else {
 				ejb3JoinColumns = new Ejb3JoinColumn[nbrOfJoinColumns];
 				if ( pkColumnsAnn != null ) {
 					for (int colIndex = 0; colIndex < nbrOfJoinColumns; colIndex++) {
 						ejb3JoinColumns[colIndex] = Ejb3JoinColumn.buildJoinColumn(
 								pkColumnsAnn[colIndex],
 								null,
 								persistentClass.getIdentifier(),
 								secondaryTables,
 								propertyHolder,
 								context
 						);
 					}
 				}
 				else {
 					for (int colIndex = 0; colIndex < nbrOfJoinColumns; colIndex++) {
 						ejb3JoinColumns[colIndex] = Ejb3JoinColumn.buildJoinColumn(
 								null,
 								joinColumnsAnn[colIndex],
 								persistentClass.getIdentifier(),
 								secondaryTables,
 								propertyHolder,
 								context
 						);
 					}
 				}
 			}
 		}
 
 		for (Ejb3JoinColumn joinColumn : ejb3JoinColumns) {
 			joinColumn.forceNotNull();
 		}
 		bindJoinToPersistentClass( join, ejb3JoinColumns, context );
 	}
 
 	private void bindJoinToPersistentClass(Join join, Ejb3JoinColumn[] ejb3JoinColumns, MetadataBuildingContext buildingContext) {
 		SimpleValue key = new DependantValue( buildingContext.getMetadataCollector(), join.getTable(), persistentClass.getIdentifier() );
 		join.setKey( key );
 		setFKNameIfDefined( join );
 		key.setCascadeDeleteEnabled( false );
 		TableBinder.bindFk( persistentClass, null, ejb3JoinColumns, key, false, buildingContext );
 		join.createPrimaryKey();
 		join.createForeignKey();
 		persistentClass.addJoin( join );
 	}
 
 	private void setFKNameIfDefined(Join join) {
 		// just awful..
 
 		org.hibernate.annotations.Table matchingTable = findMatchingComplimentTableAnnotation( join );
 		if ( matchingTable != null && !BinderHelper.isEmptyAnnotationValue( matchingTable.foreignKey().name() ) ) {
 			( (SimpleValue) join.getKey() ).setForeignKeyName( matchingTable.foreignKey().name() );
 		}
 		else {
 			javax.persistence.SecondaryTable jpaSecondaryTable = findMatchingSecondaryTable( join );
 			if ( jpaSecondaryTable != null ) {
 				if ( jpaSecondaryTable.foreignKey().value() == ConstraintMode.NO_CONSTRAINT ) {
 					( (SimpleValue) join.getKey() ).setForeignKeyName( "none" );
 				}
 				else {
 					( (SimpleValue) join.getKey() ).setForeignKeyName( StringHelper.nullIfEmpty( jpaSecondaryTable.foreignKey().name() ) );
+					( (SimpleValue) join.getKey() ).setForeignKeyDefinition( StringHelper.nullIfEmpty( jpaSecondaryTable.foreignKey().foreignKeyDefinition() ) );
 				}
 			}
 		}
 	}
 
 	private SecondaryTable findMatchingSecondaryTable(Join join) {
 		final String nameToMatch = join.getTable().getQuotedName();
 
 		SecondaryTable secondaryTable = annotatedClass.getAnnotation( SecondaryTable.class );
 		if ( secondaryTable != null && nameToMatch.equals( secondaryTable.name() ) ) {
 			return secondaryTable;
 		}
 
 		SecondaryTables secondaryTables = annotatedClass.getAnnotation( SecondaryTables.class );
 		if ( secondaryTables != null ) {
 			for ( SecondaryTable secondaryTable2 : secondaryTables.value() ) {
 				if ( secondaryTable != null && nameToMatch.equals( secondaryTable.name() ) ) {
 					return secondaryTable;
 				}
 			}
 
 		}
 
 		return null;
 	}
 
 	private org.hibernate.annotations.Table findMatchingComplimentTableAnnotation(Join join) {
 		String tableName = join.getTable().getQuotedName();
 		org.hibernate.annotations.Table table = annotatedClass.getAnnotation( org.hibernate.annotations.Table.class );
 		org.hibernate.annotations.Table matchingTable = null;
 		if ( table != null && tableName.equals( table.appliesTo() ) ) {
 			matchingTable = table;
 		}
 		else {
 			Tables tables = annotatedClass.getAnnotation( Tables.class );
 			if ( tables != null ) {
 				for (org.hibernate.annotations.Table current : tables.value()) {
 					if ( tableName.equals( current.appliesTo() ) ) {
 						matchingTable = current;
 						break;
 					}
 				}
 			}
 		}
 		return matchingTable;
 	}
 
 	public void firstLevelSecondaryTablesBinding(
 			SecondaryTable secTable, SecondaryTables secTables
 	) {
 		if ( secTables != null ) {
 			//loop through it
 			for (SecondaryTable tab : secTables.value()) {
 				addJoin( tab, null, null, false );
 			}
 		}
 		else {
 			if ( secTable != null ) addJoin( secTable, null, null, false );
 		}
 	}
 
 	//Used for @*ToMany @JoinTable
 	public Join addJoin(JoinTable joinTable, PropertyHolder holder, boolean noDelayInPkColumnCreation) {
 		return addJoin( null, joinTable, holder, noDelayInPkColumnCreation );
 	}
 
 	private static class SecondaryTableNameSource implements ObjectNameSource {
 		// always has an explicit name
 		private final String explicitName;
 
 		private SecondaryTableNameSource(String explicitName) {
 			this.explicitName = explicitName;
 		}
 
 		public String getExplicitName() {
 			return explicitName;
 		}
 
 		public String getLogicalName() {
 			return explicitName;
 		}
 	}
 
 	private static class SecondaryTableNamingStrategyHelper implements NamingStrategyHelper {
 		@Override
 		public Identifier determineImplicitName(MetadataBuildingContext buildingContext) {
 			// should maybe throw an exception here
 			return null;
 		}
 
 		@Override
 		public Identifier handleExplicitName(String explicitName, MetadataBuildingContext buildingContext) {
 			return buildingContext.getMetadataCollector()
 					.getDatabase()
 					.getJdbcEnvironment()
 					.getIdentifierHelper()
 					.toIdentifier( explicitName );
 		}
 
 		@Override
 		public Identifier toPhysicalName(Identifier logicalName, MetadataBuildingContext buildingContext) {
 			return buildingContext.getBuildingOptions().getPhysicalNamingStrategy().toPhysicalTableName(
 					logicalName,
 					buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment()
 			);
 		}
 	}
 
 	private static SecondaryTableNamingStrategyHelper SEC_TBL_NS_HELPER = new SecondaryTableNamingStrategyHelper();
 
 	private Join addJoin(
 			SecondaryTable secondaryTable,
 			JoinTable joinTable,
 			PropertyHolder propertyHolder,
 			boolean noDelayInPkColumnCreation) {
 		// A non null propertyHolder means than we process the Pk creation without delay
 		Join join = new Join();
 		join.setPersistentClass( persistentClass );
 
 		final String schema;
 		final String catalog;
 		final SecondaryTableNameSource secondaryTableNameContext;
 		final Object joinColumns;
 		final List<UniqueConstraintHolder> uniqueConstraintHolders;
 
 		final Identifier logicalName;
 		if ( secondaryTable != null ) {
 			schema = secondaryTable.schema();
 			catalog = secondaryTable.catalog();
 			logicalName = context.getMetadataCollector()
 					.getDatabase()
 					.getJdbcEnvironment()
 					.getIdentifierHelper()
 					.toIdentifier( secondaryTable.name() );
 			joinColumns = secondaryTable.pkJoinColumns();
 			uniqueConstraintHolders = TableBinder.buildUniqueConstraintHolders( secondaryTable.uniqueConstraints() );
 		}
 		else if ( joinTable != null ) {
 			schema = joinTable.schema();
 			catalog = joinTable.catalog();
 			logicalName = context.getMetadataCollector()
 					.getDatabase()
 					.getJdbcEnvironment()
 					.getIdentifierHelper()
 					.toIdentifier( joinTable.name() );
 			joinColumns = joinTable.joinColumns();
 			uniqueConstraintHolders = TableBinder.buildUniqueConstraintHolders( joinTable.uniqueConstraints() );
 		}
 		else {
 			throw new AssertionFailure( "Both JoinTable and SecondaryTable are null" );
 		}
 
 		final Table table = TableBinder.buildAndFillTable(
 				schema,
 				catalog,
 				logicalName,
 				false,
 				uniqueConstraintHolders,
 				null,
 				null,
 				context,
 				null,
 				null
 		);
 
 		final InFlightMetadataCollector.EntityTableXref tableXref = context.getMetadataCollector().getEntityTableXref( persistentClass.getEntityName() );
 		assert tableXref != null : "Could not locate EntityTableXref for entity [" + persistentClass.getEntityName() + "]";
 		tableXref.addSecondaryTable( logicalName, join );
 
 		if ( secondaryTable != null ) {
 			TableBinder.addIndexes( table, secondaryTable.indexes(), context );
 		}
 
 			//no check constraints available on joins
 		join.setTable( table );
 
 		//somehow keep joins() for later.
 		//Has to do the work later because it needs persistentClass id!
 		LOG.debugf( "Adding secondary table to entity %s -> %s", persistentClass.getEntityName(), join.getTable().getName() );
 		org.hibernate.annotations.Table matchingTable = findMatchingComplimentTableAnnotation( join );
 		if ( matchingTable != null ) {
 			join.setSequentialSelect( FetchMode.JOIN != matchingTable.fetch() );
 			join.setInverse( matchingTable.inverse() );
 			join.setOptional( matchingTable.optional() );
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlInsert().sql() ) ) {
 				join.setCustomSQLInsert( matchingTable.sqlInsert().sql().trim(),
 						matchingTable.sqlInsert().callable(),
 						ExecuteUpdateResultCheckStyle.fromExternalName(
 								matchingTable.sqlInsert().check().toString().toLowerCase(Locale.ROOT)
 						)
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlUpdate().sql() ) ) {
 				join.setCustomSQLUpdate( matchingTable.sqlUpdate().sql().trim(),
 						matchingTable.sqlUpdate().callable(),
 						ExecuteUpdateResultCheckStyle.fromExternalName(
 								matchingTable.sqlUpdate().check().toString().toLowerCase(Locale.ROOT)
 						)
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlDelete().sql() ) ) {
 				join.setCustomSQLDelete( matchingTable.sqlDelete().sql().trim(),
 						matchingTable.sqlDelete().callable(),
 						ExecuteUpdateResultCheckStyle.fromExternalName(
 								matchingTable.sqlDelete().check().toString().toLowerCase(Locale.ROOT)
 						)
 				);
 			}
 		}
 		else {
 			//default
 			join.setSequentialSelect( false );
 			join.setInverse( false );
 			join.setOptional( true ); //perhaps not quite per-spec, but a Good Thing anyway
 		}
 
 		if ( noDelayInPkColumnCreation ) {
 			createPrimaryColumnsToSecondaryTable( joinColumns, propertyHolder, join );
 		}
 		else {
 			secondaryTables.put( table.getQuotedName(), join );
 			secondaryTableJoins.put( table.getQuotedName(), joinColumns );
 		}
 
 		return join;
 	}
 
 	public java.util.Map<String, Join> getSecondaryTables() {
 		return secondaryTables;
 	}
 
 	public void setCache(Cache cacheAnn) {
 		if ( cacheAnn != null ) {
 			cacheRegion = BinderHelper.isEmptyAnnotationValue( cacheAnn.region() ) ?
 					null :
 					cacheAnn.region();
 			cacheConcurrentStrategy = getCacheConcurrencyStrategy( cacheAnn.usage() );
 			if ( "all".equalsIgnoreCase( cacheAnn.include() ) ) {
 				cacheLazyProperty = true;
 			}
 			else if ( "non-lazy".equalsIgnoreCase( cacheAnn.include() ) ) {
 				cacheLazyProperty = false;
 			}
 			else {
 				throw new AnnotationException( "Unknown lazy property annotations: " + cacheAnn.include() );
 			}
 		}
 		else {
 			cacheConcurrentStrategy = null;
 			cacheRegion = null;
 			cacheLazyProperty = true;
 		}
 	}
 	
 	public void setNaturalIdCache(XClass clazzToProcess, NaturalIdCache naturalIdCacheAnn) {
 		if ( naturalIdCacheAnn != null ) {
 			if ( BinderHelper.isEmptyAnnotationValue( naturalIdCacheAnn.region() ) ) {
 				if (cacheRegion != null) {
 					naturalIdCacheRegion = cacheRegion + NATURAL_ID_CACHE_SUFFIX;
 				}
 				else {
 					naturalIdCacheRegion = clazzToProcess.getName() + NATURAL_ID_CACHE_SUFFIX;
 				}
 			}
 			else {
 				naturalIdCacheRegion = naturalIdCacheAnn.region();
 			}
 		}
 		else {
 			naturalIdCacheRegion = null;
 		}
 	}
 
 	public static String getCacheConcurrencyStrategy(CacheConcurrencyStrategy strategy) {
 		org.hibernate.cache.spi.access.AccessType accessType = strategy.toAccessType();
 		return accessType == null ? null : accessType.getExternalName();
 	}
 
 	public void addFilter(Filter filter) {
 		filters.add(filter);
 	}
 
 	public void setInheritanceState(InheritanceState inheritanceState) {
 		this.inheritanceState = inheritanceState;
 	}
 
 	public boolean isIgnoreIdAnnotations() {
 		return ignoreIdAnnotations;
 	}
 
 	public void setIgnoreIdAnnotations(boolean ignoreIdAnnotations) {
 		this.ignoreIdAnnotations = ignoreIdAnnotations;
 	}
 	public void processComplementaryTableDefinitions(javax.persistence.Table table) {
 		if ( table == null ) return;
 		TableBinder.addIndexes( persistentClass.getTable(), table.indexes(), context );
 	}
 	public void processComplementaryTableDefinitions(org.hibernate.annotations.Table table) {
 		//comment and index are processed here
 		if ( table == null ) return;
 		String appliedTable = table.appliesTo();
 		Iterator tables = persistentClass.getTableClosureIterator();
 		Table hibTable = null;
 		while ( tables.hasNext() ) {
 			Table pcTable = (Table) tables.next();
 			if ( pcTable.getQuotedName().equals( appliedTable ) ) {
 				//we are in the correct table to find columns
 				hibTable = pcTable;
 				break;
 			}
 			hibTable = null;
 		}
 		if ( hibTable == null ) {
 			//maybe a join/secondary table
 			for ( Join join : secondaryTables.values() ) {
 				if ( join.getTable().getQuotedName().equals( appliedTable ) ) {
 					hibTable = join.getTable();
 					break;
 				}
 			}
 		}
 		if ( hibTable == null ) {
 			throw new AnnotationException(
 					"@org.hibernate.annotations.Table references an unknown table: " + appliedTable
 			);
 		}
 		if ( !BinderHelper.isEmptyAnnotationValue( table.comment() ) ) hibTable.setComment( table.comment() );
 		TableBinder.addIndexes( hibTable, table.indexes(), context );
 	}
 
 	public void processComplementaryTableDefinitions(Tables tables) {
 		if ( tables == null ) return;
 		for (org.hibernate.annotations.Table table : tables.value()) {
 			processComplementaryTableDefinitions( table );
 		}
 	}
 
 	public AccessType getPropertyAccessType() {
 		return propertyAccessType;
 	}
 
 	public void setPropertyAccessType(AccessType propertyAccessor) {
 		this.propertyAccessType = getExplicitAccessType( annotatedClass );
 		// only set the access type if there is no explicit access type for this class
 		if( this.propertyAccessType == null ) {
 			this.propertyAccessType = propertyAccessor;
 		}
 	}
 
 	public AccessType getPropertyAccessor(XAnnotatedElement element) {
 		AccessType accessType = getExplicitAccessType( element );
 		if ( accessType == null ) {
 			accessType = propertyAccessType;
 		}
 		return accessType;
 	}
 
 	public AccessType getExplicitAccessType(XAnnotatedElement element) {
 		AccessType accessType = null;
 
 		AccessType hibernateAccessType = null;
 		AccessType jpaAccessType = null;
 
 		org.hibernate.annotations.AccessType accessTypeAnnotation = element.getAnnotation( org.hibernate.annotations.AccessType.class );
 		if ( accessTypeAnnotation != null ) {
 			hibernateAccessType = AccessType.getAccessStrategy( accessTypeAnnotation.value() );
 		}
 
 		Access access = element.getAnnotation( Access.class );
 		if ( access != null ) {
 			jpaAccessType = AccessType.getAccessStrategy( access.value() );
 		}
 
 		if ( hibernateAccessType != null && jpaAccessType != null && hibernateAccessType != jpaAccessType ) {
 			throw new MappingException(
 					"Found @Access and @AccessType with conflicting values on a property in class " + annotatedClass.toString()
 			);
 		}
 
 		if ( hibernateAccessType != null ) {
 			accessType = hibernateAccessType;
 		}
 		else if ( jpaAccessType != null ) {
 			accessType = jpaAccessType;
 		}
 
 		return accessType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
index c65b20ee85..3d57c4a03b 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/Dialect.java
@@ -1076,1690 +1076,1701 @@ public abstract class Dialect implements ConversionContext {
 	 * so that dialects not supporting offsets can generate proper exceptions.
 	 * In general, dialects will override one or the other of this method and
 	 * {@link #getLimitString(String, int, int)}.
 	 *
 	 * @param query The query to which to apply the limit.
 	 * @param hasOffset Is the query requesting an offset?
 	 * @return the modified SQL
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	protected String getLimitString(String query, boolean hasOffset) {
 		throw new UnsupportedOperationException( "Paged queries not supported by " + getClass().getName());
 	}
 
 	/**
 	 * Hibernate APIs explicitly state that setFirstResult() should be a zero-based offset. Here we allow the
 	 * Dialect a chance to convert that value based on what the underlying db or driver will expect.
 	 * <p/>
 	 * NOTE: what gets passed into {@link #getLimitString(String,int,int)} is the zero-based offset.  Dialects which
 	 * do not {@link #supportsVariableLimit} should take care to perform any needed first-row-conversion calls prior
 	 * to injecting the limit values into the SQL string.
 	 *
 	 * @param zeroBasedFirstResult The user-supplied, zero-based first row offset.
 	 * @return The corresponding db/dialect specific offset.
 	 * @see org.hibernate.Query#setFirstResult
 	 * @see org.hibernate.Criteria#setFirstResult
 	 * @deprecated {@link #getLimitHandler()} should be overridden instead.
 	 */
 	@Deprecated
 	public int convertToFirstRowValue(int zeroBasedFirstResult) {
 		return zeroBasedFirstResult;
 	}
 
 
 	// lock acquisition support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Informational metadata about whether this dialect is known to support
 	 * specifying timeouts for requested lock acquisitions.
 	 *
 	 * @return True is this dialect supports specifying lock timeouts.
 	 */
 	public boolean supportsLockTimeouts() {
 		return true;
 
 	}
 
 	/**
 	 * If this dialect supports specifying lock timeouts, are those timeouts
 	 * rendered into the <tt>SQL</tt> string as parameters.  The implication
 	 * is that Hibernate will need to bind the timeout value as a parameter
 	 * in the {@link java.sql.PreparedStatement}.  If true, the param position
 	 * is always handled as the last parameter; if the dialect specifies the
 	 * lock timeout elsewhere in the <tt>SQL</tt> statement then the timeout
 	 * value should be directly rendered into the statement and this method
 	 * should return false.
 	 *
 	 * @return True if the lock timeout is rendered into the <tt>SQL</tt>
 	 * string as a parameter; false otherwise.
 	 */
 	public boolean isLockTimeoutParameterized() {
 		return false;
 	}
 
 	/**
 	 * Get a strategy instance which knows how to acquire a database-level lock
 	 * of the specified mode for this dialect.
 	 *
 	 * @param lockable The persister for the entity to be locked.
 	 * @param lockMode The type of lock to be acquired.
 	 * @return The appropriate locking strategy.
 	 * @since 3.2
 	 */
 	public LockingStrategy getLockingStrategy(Lockable lockable, LockMode lockMode) {
 		switch ( lockMode ) {
 			case PESSIMISTIC_FORCE_INCREMENT:
 				return new PessimisticForceIncrementLockingStrategy( lockable, lockMode );
 			case PESSIMISTIC_WRITE:
 				return new PessimisticWriteSelectLockingStrategy( lockable, lockMode );
 			case PESSIMISTIC_READ:
 				return new PessimisticReadSelectLockingStrategy( lockable, lockMode );
 			case OPTIMISTIC:
 				return new OptimisticLockingStrategy( lockable, lockMode );
 			case OPTIMISTIC_FORCE_INCREMENT:
 				return new OptimisticForceIncrementLockingStrategy( lockable, lockMode );
 			default:
 				return new SelectLockingStrategy( lockable, lockMode );
 		}
 	}
 
 	/**
 	 * Given LockOptions (lockMode, timeout), determine the appropriate for update fragment to use.
 	 *
 	 * @param lockOptions contains the lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockOptions lockOptions) {
 		final LockMode lockMode = lockOptions.getLockMode();
 		return getForUpdateString( lockMode, lockOptions.getTimeOut() );
 	}
 
 	@SuppressWarnings( {"deprecation"})
 	private String getForUpdateString(LockMode lockMode, int timeout){
 		switch ( lockMode ) {
 			case UPGRADE:
 				return getForUpdateString();
 			case PESSIMISTIC_READ:
 				return getReadLockString( timeout );
 			case PESSIMISTIC_WRITE:
 				return getWriteLockString( timeout );
 			case UPGRADE_NOWAIT:
 			case FORCE:
 			case PESSIMISTIC_FORCE_INCREMENT:
 				return getForUpdateNowaitString();
 			case UPGRADE_SKIPLOCKED:
 				return getForUpdateSkipLockedString();
 			default:
 				return "";
 		}
 	}
 
 	/**
 	 * Given a lock mode, determine the appropriate for update fragment to use.
 	 *
 	 * @param lockMode The lock mode to apply.
 	 * @return The appropriate for update fragment.
 	 */
 	public String getForUpdateString(LockMode lockMode) {
 		return getForUpdateString( lockMode, LockOptions.WAIT_FOREVER );
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire locks
 	 * for this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE</tt> clause string.
 	 */
 	public String getForUpdateString() {
 		return " for update";
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getWriteLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the string to append to SELECT statements to acquire WRITE locks
 	 * for this dialect.  Location of the of the returned string is treated
 	 * the same as getForUpdateString.
 	 *
 	 * @param timeout in milliseconds, -1 for indefinite wait and 0 for no wait.
 	 * @return The appropriate <tt>LOCK</tt> clause string.
 	 */
 	public String getReadLockString(int timeout) {
 		return getForUpdateString();
 	}
 
 
 	/**
 	 * Is <tt>FOR UPDATE OF</tt> syntax supported?
 	 *
 	 * @return True if the database supports <tt>FOR UPDATE OF</tt> syntax;
 	 * false otherwise.
 	 */
 	public boolean forUpdateOfColumns() {
 		// by default we report no support
 		return false;
 	}
 
 	/**
 	 * Does this dialect support <tt>FOR UPDATE</tt> in conjunction with
 	 * outer joined rows?
 	 *
 	 * @return True if outer joined rows can be locked via <tt>FOR UPDATE</tt>.
 	 */
 	public boolean supportsOuterJoinForUpdate() {
 		return true;
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	public String getForUpdateString(String aliases) {
 		// by default we simply return the getForUpdateString() result since
 		// the default is to say no support for "FOR UPDATE OF ..."
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list</tt> fragment appropriate for this
 	 * dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @param lockOptions the lock options to apply
 	 * @return The appropriate <tt>FOR UPDATE OF column_list</tt> clause string.
 	 */
 	@SuppressWarnings({"unchecked", "UnusedParameters"})
 	public String getForUpdateString(String aliases, LockOptions lockOptions) {
 		LockMode lockMode = lockOptions.getLockMode();
 		final Iterator<Map.Entry<String, LockMode>> itr = lockOptions.getAliasLockIterator();
 		while ( itr.hasNext() ) {
 			// seek the highest lock mode
 			final Map.Entry<String, LockMode>entry = itr.next();
 			final LockMode lm = entry.getValue();
 			if ( lm.greaterThan( lockMode ) ) {
 				lockMode = lm;
 			}
 		}
 		lockOptions.setLockMode( lockMode );
 		return getForUpdateString( lockOptions );
 	}
 
 	/**
 	 * Retrieves the <tt>FOR UPDATE NOWAIT</tt> syntax specific to this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString() {
 		// by default we report no support for NOWAIT lock semantics
 		return getForUpdateString();
 	}
 
 	/**
 	 * Retrieves the <tt>FOR UPDATE SKIP LOCKED</tt> syntax specific to this dialect.
 	 *
 	 * @return The appropriate <tt>FOR UPDATE SKIP LOCKED</tt> clause string.
 	 */
 	public String getForUpdateSkipLockedString() {
 		// by default we report no support for SKIP_LOCKED lock semantics
 		return getForUpdateString();
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list NOWAIT</tt> fragment appropriate
 	 * for this dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE OF colunm_list NOWAIT</tt> clause string.
 	 */
 	public String getForUpdateNowaitString(String aliases) {
 		return getForUpdateString( aliases );
 	}
 
 	/**
 	 * Get the <tt>FOR UPDATE OF column_list SKIP LOCKED</tt> fragment appropriate
 	 * for this dialect given the aliases of the columns to be write locked.
 	 *
 	 * @param aliases The columns to be write locked.
 	 * @return The appropriate <tt>FOR UPDATE colunm_list SKIP LOCKED</tt> clause string.
 	 */
 	public String getForUpdateSkipLockedString(String aliases) {
 		return getForUpdateString( aliases );
 	}
 
 	/**
 	 * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
 	 * whereby a "lock hint" is appends to the table name in the from clause.
 	 * <p/>
 	 * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
 	 *
 	 * @param mode The lock mode to apply
 	 * @param tableName The name of the table to which to apply the lock hint.
 	 * @return The table with any required lock hints.
 	 * @deprecated use {@code appendLockHint(LockOptions,String)} instead
 	 */
 	@Deprecated
 	public String appendLockHint(LockMode mode, String tableName) {
 		return appendLockHint( new LockOptions( mode ), tableName );
 	}
 	/**
 	 * Some dialects support an alternative means to <tt>SELECT FOR UPDATE</tt>,
 	 * whereby a "lock hint" is appends to the table name in the from clause.
 	 * <p/>
 	 * contributed by <a href="http://sourceforge.net/users/heschulz">Helge Schulz</a>
 	 *
 	 * @param lockOptions The lock options to apply
 	 * @param tableName The name of the table to which to apply the lock hint.
 	 * @return The table with any required lock hints.
 	 */
 	public String appendLockHint(LockOptions lockOptions, String tableName){
 		return tableName;
 	}
 
 	/**
 	 * Modifies the given SQL by applying the appropriate updates for the specified
 	 * lock modes and key columns.
 	 * <p/>
 	 * The behavior here is that of an ANSI SQL <tt>SELECT FOR UPDATE</tt>.  This
 	 * method is really intended to allow dialects which do not support
 	 * <tt>SELECT FOR UPDATE</tt> to achieve this in their own fashion.
 	 *
 	 * @param sql the SQL string to modify
 	 * @param aliasedLockOptions lock options indexed by aliased table names.
 	 * @param keyColumnNames a map of key columns indexed by aliased table names.
 	 * @return the modified SQL string.
 	 */
 	public String applyLocksToSql(String sql, LockOptions aliasedLockOptions, Map<String, String[]> keyColumnNames) {
 		return sql + new ForUpdateFragment( this, aliasedLockOptions, keyColumnNames ).toFragmentString();
 	}
 
 
 	// table support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Command used to create a table.
 	 *
 	 * @return The command used to create a table.
 	 */
 	public String getCreateTableString() {
 		return "create table";
 	}
 
 	/**
 	 * Slight variation on {@link #getCreateTableString}.  Here, we have the
 	 * command used to create a table when there is no primary key and
 	 * duplicate rows are expected.
 	 * <p/>
 	 * Most databases do not care about the distinction; originally added for
 	 * Teradata support which does care.
 	 *
 	 * @return The command used to create a multiset table.
 	 */
 	public String getCreateMultisetTableString() {
 		return getCreateTableString();
 	}
 
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new PersistentTableBulkIdStrategy();
 	}
 
 	// callable statement support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Registers a parameter (either OUT, or the new REF_CURSOR param type available in Java 8) capable of
 	 * returning {@link java.sql.ResultSet} *by position*.  Pre-Java 8, registering such ResultSet-returning
 	 * parameters varied greatly across database and drivers; hence its inclusion as part of the Dialect contract.
 	 *
 	 * @param statement The callable statement.
 	 * @param position The bind position at which to register the output param.
 	 *
 	 * @return The number of (contiguous) bind positions used.
 	 *
 	 * @throws SQLException Indicates problems registering the param.
 	 */
 	public int registerResultSetOutParameter(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 						" does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Registers a parameter (either OUT, or the new REF_CURSOR param type available in Java 8) capable of
 	 * returning {@link java.sql.ResultSet} *by name*.  Pre-Java 8, registering such ResultSet-returning
 	 * parameters varied greatly across database and drivers; hence its inclusion as part of the Dialect contract.
 	 *
 	 * @param statement The callable statement.
 	 * @param name The parameter name (for drivers which support named parameters).
 	 *
 	 * @return The number of (contiguous) bind positions used.
 	 *
 	 * @throws SQLException Indicates problems registering the param.
 	 */
 	@SuppressWarnings("UnusedParameters")
 	public int registerResultSetOutParameter(CallableStatement statement, String name) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() +
 						" does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
 	 *
 	 * @param statement The callable statement.
 	 * @return The extracted result set.
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	public ResultSet getResultSet(CallableStatement statement) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() + " does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet}.
 	 *
 	 * @param statement The callable statement.
 	 * @param position The bind position at which to register the output param.
 	 *
 	 * @return The extracted result set.
 	 *
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	@SuppressWarnings("UnusedParameters")
 	public ResultSet getResultSet(CallableStatement statement, int position) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() + " does not support resultsets via stored procedures"
 		);
 	}
 
 	/**
 	 * Given a callable statement previously processed by {@link #registerResultSetOutParameter},
 	 * extract the {@link java.sql.ResultSet} from the OUT parameter.
 	 *
 	 * @param statement The callable statement.
 	 * @param name The parameter name (for drivers which support named parameters).
 	 *
 	 * @return The extracted result set.
 	 *
 	 * @throws SQLException Indicates problems extracting the result set.
 	 */
 	@SuppressWarnings("UnusedParameters")
 	public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
 		throw new UnsupportedOperationException(
 				getClass().getName() + " does not support resultsets via stored procedures"
 		);
 	}
 
 	// current timestamp support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support a way to retrieve the database's current
 	 * timestamp value?
 	 *
 	 * @return True if the current timestamp can be retrieved; false otherwise.
 	 */
 	public boolean supportsCurrentTimestampSelection() {
 		return false;
 	}
 
 	/**
 	 * Should the value returned by {@link #getCurrentTimestampSelectString}
 	 * be treated as callable.  Typically this indicates that JDBC escape
 	 * syntax is being used...
 	 *
 	 * @return True if the {@link #getCurrentTimestampSelectString} return
 	 * is callable; false otherwise.
 	 */
 	public boolean isCurrentTimestampSelectStringCallable() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * Retrieve the command used to retrieve the current timestamp from the
 	 * database.
 	 *
 	 * @return The command.
 	 */
 	public String getCurrentTimestampSelectString() {
 		throw new UnsupportedOperationException( "Database not known to define a current timestamp function" );
 	}
 
 	/**
 	 * The name of the database-specific SQL function for retrieving the
 	 * current timestamp.
 	 *
 	 * @return The function name.
 	 */
 	public String getCurrentTimestampSQLFunctionName() {
 		// the standard SQL function name is current_timestamp...
 		return "current_timestamp";
 	}
 
 
 	// SQLException support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Build an instance of the SQLExceptionConverter preferred by this dialect for
 	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.
 	 * <p/>
 	 * The preferred method is to not override this method; if possible,
 	 * {@link #buildSQLExceptionConversionDelegate()} should be overridden
 	 * instead.
 	 *
 	 * If this method is not overridden, the default SQLExceptionConverter
 	 * implementation executes 3 SQLException converter delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>the vendor-specific delegate returned by {@link #buildSQLExceptionConversionDelegate()};
 	 *         (it is strongly recommended that specific Dialect implementations
 	 *         override {@link #buildSQLExceptionConversionDelegate()})</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * If this method is overridden, it is strongly recommended that the
 	 * returned {@link SQLExceptionConverter} interpret SQL errors based on
 	 * vendor-specific error codes rather than the SQLState since the
 	 * interpretation is more accurate when using vendor-specific ErrorCodes.
 	 *
 	 * @return The Dialect's preferred SQLExceptionConverter, or null to
 	 * indicate that the default {@link SQLExceptionConverter} should be used.
 	 *
 	 * @see {@link #buildSQLExceptionConversionDelegate()}
 	 * @deprecated {@link #buildSQLExceptionConversionDelegate()} should be
 	 * overridden instead.
 	 */
 	@Deprecated
 	public SQLExceptionConverter buildSQLExceptionConverter() {
 		return null;
 	}
 
 	/**
 	 * Build an instance of a {@link SQLExceptionConversionDelegate} for
 	 * interpreting dialect-specific error or SQLState codes.
 	 * <p/>
 	 * When {@link #buildSQLExceptionConverter} returns null, the default 
 	 * {@link SQLExceptionConverter} is used to interpret SQLState and
 	 * error codes. If this method is overridden to return a non-null value,
 	 * the default {@link SQLExceptionConverter} will use the returned
 	 * {@link SQLExceptionConversionDelegate} in addition to the following 
 	 * standard delegates:
 	 * <ol>
 	 *     <li>a "static" delegate based on the JDBC 4 defined SQLException hierarchy;</li>
 	 *     <li>a delegate that interprets SQLState codes for either X/Open or SQL-2003 codes,
 	 *         depending on java.sql.DatabaseMetaData#getSQLStateType</li>
 	 * </ol>
 	 * <p/>
 	 * It is strongly recommended that specific Dialect implementations override this
 	 * method, since interpretation of a SQL error is much more accurate when based on
 	 * the a vendor-specific ErrorCode rather than the SQLState.
 	 * <p/>
 	 * Specific Dialects may override to return whatever is most appropriate for that vendor.
 	 *
 	 * @return The SQLExceptionConversionDelegate for this dialect
 	 */
 	public SQLExceptionConversionDelegate buildSQLExceptionConversionDelegate() {
 		return null;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new ViolatedConstraintNameExtracter() {
 		public String extractConstraintName(SQLException sqle) {
 			return null;
 		}
 	};
 
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 
 	// union subclass support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Given a {@link java.sql.Types} type code, determine an appropriate
 	 * null value to use in a select clause.
 	 * <p/>
 	 * One thing to consider here is that certain databases might
 	 * require proper casting for the nulls here since the select here
 	 * will be part of a UNION/UNION ALL.
 	 *
 	 * @param sqlType The {@link java.sql.Types} type code.
 	 * @return The appropriate select clause value fragment.
 	 */
 	public String getSelectClauseNullString(int sqlType) {
 		return "null";
 	}
 
 	/**
 	 * Does this dialect support UNION ALL, which is generally a faster
 	 * variant of UNION?
 	 *
 	 * @return True if UNION ALL is supported; false otherwise.
 	 */
 	public boolean supportsUnionAll() {
 		return false;
 	}
 
 
 	// miscellaneous support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 	/**
 	 * Create a {@link org.hibernate.sql.JoinFragment} strategy responsible
 	 * for handling this dialect's variations in how joins are handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.JoinFragment} strategy.
 	 */
 	public JoinFragment createOuterJoinFragment() {
 		return new ANSIJoinFragment();
 	}
 
 	/**
 	 * Create a {@link org.hibernate.sql.CaseFragment} strategy responsible
 	 * for handling this dialect's variations in how CASE statements are
 	 * handled.
 	 *
 	 * @return This dialect's {@link org.hibernate.sql.CaseFragment} strategy.
 	 */
 	public CaseFragment createCaseFragment() {
 		return new ANSICaseFragment();
 	}
 
 	/**
 	 * The fragment used to insert a row without specifying any column values.
 	 * This is not possible on some databases.
 	 *
 	 * @return The appropriate empty values clause.
 	 */
 	public String getNoColumnsInsertString() {
 		return "values ( )";
 	}
 
 	/**
 	 * The name of the SQL function that transforms a string to
 	 * lowercase
 	 *
 	 * @return The dialect-specific lowercase function.
 	 */
 	public String getLowercaseFunction() {
 		return "lower";
 	}
 
 	/**
 	 * The name of the SQL function that can do case insensitive <b>like</b> comparison.
 	 *
 	 * @return  The dialect-specific "case insensitive" like function.
 	 */
 	public String getCaseInsensitiveLike(){
 		return "like";
 	}
 
 	/**
 	 * Does this dialect support case insensitive LIKE restrictions?
 	 *
 	 * @return {@code true} if the underlying database supports case insensitive like comparison,
 	 * {@code false} otherwise.  The default is {@code false}.
 	 */
 	public boolean supportsCaseInsensitiveLike(){
 		return false;
 	}
 
 	/**
 	 * Meant as a means for end users to affect the select strings being sent
 	 * to the database and perhaps manipulate them in some fashion.
 	 * <p/>
 	 * The recommend approach is to instead use
 	 * {@link org.hibernate.Interceptor#onPrepareStatement(String)}.
 	 *
 	 * @param select The select command
 	 * @return The mutated select command, or the same as was passed in.
 	 */
 	public String transformSelectString(String select) {
 		return select;
 	}
 
 	/**
 	 * What is the maximum length Hibernate can use for generated aliases?
 	 * <p/>
 	 * The maximum here should account for the fact that Hibernate often needs to append "uniqueing" information
 	 * to the end of generated aliases.  That "uniqueing" information will be added to the end of a identifier
 	 * generated to the length specified here; so be sure to leave some room (generally speaking 5 positions will
 	 * suffice).
 	 *
 	 * @return The maximum length.
 	 */
 	public int getMaxAliasLength() {
 		return 10;
 	}
 
 	/**
 	 * The SQL literal value to which this database maps boolean values.
 	 *
 	 * @param bool The boolean value
 	 * @return The appropriate SQL literal.
 	 */
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "1" : "0";
 	}
 
 
 	// keyword support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	protected void registerKeyword(String word) {
 		// When tokens are checked for keywords, they are always compared against the lower-case version of the token.
 		// For instance, Template#renderWhereStringTemplate transforms all tokens to lower-case too.
 		sqlKeywords.add(word.toLowerCase(Locale.ROOT));
 	}
 
 	/**
 	 * @deprecated These are only ever used (if at all) from the code that handles identifier quoting.  So
 	 * see {@link #buildIdentifierHelper} instead
 	 */
 	@Deprecated
 	public Set<String> getKeywords() {
 		return sqlKeywords;
 	}
 
 	/**
 	 * Build the IdentifierHelper indicated by this Dialect for handling identifier conversions.
 	 * Returning {@code null} is allowed and indicates that Hibernate should fallback to building a
 	 * "standard" helper.  In the fallback path, any changes made to the IdentifierHelperBuilder
 	 * during this call will still be incorporated into the built IdentifierHelper.
 	 * <p/>
 	 * The incoming builder will have the following set:<ul>
 	 *     <li>{@link IdentifierHelperBuilder#isGloballyQuoteIdentifiers()}</li>
 	 *     <li>{@link IdentifierHelperBuilder#getUnquotedCaseStrategy()} - initialized to UPPER</li>
 	 *     <li>{@link IdentifierHelperBuilder#getQuotedCaseStrategy()} - initialized to MIXED</li>
 	 * </ul>
 	 * <p/>
 	 * By default Hibernate will do the following:<ul>
 	 *     <li>Call {@link IdentifierHelperBuilder#applyIdentifierCasing(DatabaseMetaData)}
 	 *     <li>Call {@link IdentifierHelperBuilder#applyReservedWords(DatabaseMetaData)}
 	 *     <li>Applies {@link AnsiSqlKeywords#sql2003()} as reserved words</li>
 	 *     <li>Applies the {#link #sqlKeywords} collected here as reserved words</li>
 	 *     <li>Applies the Dialect's NameQualifierSupport, if it defines one</li>
 	 * </ul>
 	 *
 	 * @param builder A semi-configured IdentifierHelper builder.
 	 * @param dbMetaData Access to the metadata returned from the driver if needed and if available.  WARNING: may be {@code null}
 	 *
 	 * @return The IdentifierHelper instance to use, or {@code null} to indicate Hibernate should use its fallback path
 	 *
 	 * @throws SQLException Accessing the DatabaseMetaData can throw it.  Just re-throw and Hibernate will handle.
 	 *
 	 * @see #getNameQualifierSupport()
 	 */
 	public IdentifierHelper buildIdentifierHelper(
 			IdentifierHelperBuilder builder,
 			DatabaseMetaData dbMetaData) throws SQLException {
 		builder.applyIdentifierCasing( dbMetaData );
 
 		builder.applyReservedWords( dbMetaData );
 		builder.applyReservedWords( AnsiSqlKeywords.INSTANCE.sql2003() );
 		builder.applyReservedWords( sqlKeywords );
 
 		builder.setNameQualifierSupport( getNameQualifierSupport() );
 
 		return builder.build();
 	}
 
 
 	// identifier quoting support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * The character specific to this dialect used to begin a quoted identifier.
 	 *
 	 * @return The dialect's specific open quote character.
 	 */
 	public char openQuote() {
 		return '"';
 	}
 
 	/**
 	 * The character specific to this dialect used to close a quoted identifier.
 	 *
 	 * @return The dialect's specific close quote character.
 	 */
 	public char closeQuote() {
 		return '"';
 	}
 
 	/**
 	 * Apply dialect-specific quoting.
 	 * <p/>
 	 * By default, the incoming value is checked to see if its first character
 	 * is the back-tick (`).  If so, the dialect specific quoting is applied.
 	 *
 	 * @param name The value to be quoted.
 	 * @return The quoted (or unmodified, if not starting with back-tick) value.
 	 * @see #openQuote()
 	 * @see #closeQuote()
 	 */
 	public final String quote(String name) {
 		if ( name == null ) {
 			return null;
 		}
 
 		if ( name.charAt( 0 ) == '`' ) {
 			return openQuote() + name.substring( 1, name.length() - 1 ) + closeQuote();
 		}
 		else {
 			return name;
 		}
 	}
 
 
 	// DDL support ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private StandardTableExporter tableExporter = new StandardTableExporter( this );
 	private StandardSequenceExporter sequenceExporter = new StandardSequenceExporter( this );
 	private StandardIndexExporter indexExporter = new StandardIndexExporter( this );
 	private StandardForeignKeyExporter foreignKeyExporter = new StandardForeignKeyExporter( this );
 	private StandardUniqueKeyExporter uniqueKeyExporter = new StandardUniqueKeyExporter( this );
 	private StandardAuxiliaryDatabaseObjectExporter auxiliaryObjectExporter = new StandardAuxiliaryDatabaseObjectExporter( this );
 
 	public Exporter<Table> getTableExporter() {
 		return tableExporter;
 	}
 
 	public Exporter<Sequence> getSequenceExporter() {
 		return sequenceExporter;
 	}
 
 	public Exporter<Index> getIndexExporter() {
 		return indexExporter;
 	}
 
 	public Exporter<ForeignKey> getForeignKeyExporter() {
 		return foreignKeyExporter;
 	}
 
 	public Exporter<Constraint> getUniqueKeyExporter() {
 		return uniqueKeyExporter;
 	}
 
 	public Exporter<AuxiliaryDatabaseObject> getAuxiliaryDatabaseObjectExporter() {
 		return auxiliaryObjectExporter;
 	}
 
 	/**
 	 * Does this dialect support catalog creation?
 	 *
 	 * @return True if the dialect supports catalog creation; false otherwise.
 	 */
 	public boolean canCreateCatalog() {
 		return false;
 	}
 
 	/**
 	 * Get the SQL command used to create the named catalog
 	 *
 	 * @param catalogName The name of the catalog to be created.
 	 *
 	 * @return The creation commands
 	 */
 	public String[] getCreateCatalogCommand(String catalogName) {
 		throw new UnsupportedOperationException( "No create catalog syntax supported by " + getClass().getName() );
 	}
 
 	/**
 	 * Get the SQL command used to drop the named catalog
 	 *
 	 * @param catalogName The name of the catalog to be dropped.
 	 *
 	 * @return The drop commands
 	 */
 	public String[] getDropCatalogCommand(String catalogName) {
 		throw new UnsupportedOperationException( "No drop catalog syntax supported by " + getClass().getName() );
 	}
 
 	/**
 	 * Does this dialect support schema creation?
 	 *
 	 * @return True if the dialect supports schema creation; false otherwise.
 	 */
 	public boolean canCreateSchema() {
 		return true;
 	}
 
 	/**
 	 * Get the SQL command used to create the named schema
 	 *
 	 * @param schemaName The name of the schema to be created.
 	 *
 	 * @return The creation commands
 	 */
 	public String[] getCreateSchemaCommand(String schemaName) {
 		return new String[] {"create schema " + schemaName};
 	}
 
 	/**
 	 * Get the SQL command used to drop the named schema
 	 *
 	 * @param schemaName The name of the schema to be dropped.
 	 *
 	 * @return The drop commands
 	 */
 	public String[] getDropSchemaCommand(String schemaName) {
 		return new String[] {"drop schema " + schemaName};
 	}
 
 	/**
 	 * Get the SQL command used to retrieve the current schema name.  Works in conjunction
 	 * with {@link #getSchemaNameResolver()}, unless the return from there does not need this
 	 * information.  E.g., a custom impl might make use of the Java 1.7 addition of
 	 * the {@link java.sql.Connection#getSchema()} method
 	 *
 	 * @return The current schema retrieval SQL
 	 */
 	public String getCurrentSchemaCommand() {
 		return null;
 	}
 
 	/**
 	 * Get the strategy for determining the schema name of a Connection
 	 *
 	 * @return The schema name resolver strategy
 	 */
 	public SchemaNameResolver getSchemaNameResolver() {
 		return DefaultSchemaNameResolver.INSTANCE;
 	}
 
 	/**
 	 * Does this dialect support the <tt>ALTER TABLE</tt> syntax?
 	 *
 	 * @return True if we support altering of tables; false otherwise.
 	 */
 	public boolean hasAlterTable() {
 		return true;
 	}
 
 	/**
 	 * Do we need to drop constraints beforeQuery dropping tables in this dialect?
 	 *
 	 * @return True if constraints must be dropped prior to dropping
 	 * the table; false otherwise.
 	 */
 	public boolean dropConstraints() {
 		return true;
 	}
 
 	/**
 	 * Do we need to qualify index names with the schema name?
 	 *
 	 * @return boolean
 	 */
 	public boolean qualifyIndexName() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a column to a table (optional).
 	 *
 	 * @return The "add column" fragment.
 	 */
 	public String getAddColumnString() {
 		throw new UnsupportedOperationException( "No add column syntax supported by " + getClass().getName() );
 	}
 
 	/**
 	 * The syntax for the suffix used to add a column to a table (optional).
 	 *
 	 * @return The suffix "add column" fragment.
 	 */
 	public String getAddColumnSuffixString() {
 		return "";
 	}
 
 	public String getDropForeignKeyString() {
 		return " drop constraint ";
 	}
 
 	public String getTableTypeString() {
 		// grrr... for differentiation of mysql storage engines
 		return "";
 	}
 
 	/**
 	 * The syntax used to add a foreign key constraint to a table.
 	 *
 	 * @param constraintName The FK constraint name.
 	 * @param foreignKey The names of the columns comprising the FK
 	 * @param referencedTable The table referenced by the FK
 	 * @param primaryKey The explicit columns in the referencedTable referenced
 	 * by this FK.
 	 * @param referencesPrimaryKey if false, constraint should be
 	 * explicit about which column names the constraint refers to
 	 *
 	 * @return the "add FK" fragment
 	 */
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final StringBuilder res = new StringBuilder( 30 );
 
 		res.append( " add constraint " )
 				.append( quote( constraintName ) )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			res.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		return res.toString();
 	}
 
+	public String getAddForeignKeyConstraintString(
+			String constraintName,
+			String foreignKeyDefinition) {
+		return new StringBuilder( 30 )
+				.append( " add constraint " )
+				.append( quote( constraintName ) )
+				.append( " " )
+				.append( foreignKeyDefinition )
+				.toString();
+	}
+
 	/**
 	 * The syntax used to add a primary key constraint to a table.
 	 *
 	 * @param constraintName The name of the PK constraint.
 	 * @return The "add PK" fragment
 	 */
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " primary key ";
 	}
 
 	/**
 	 * Does the database/driver have bug in deleting rows that refer to other rows being deleted in the same query?
 	 *
 	 * @return {@code true} if the database/driver has this bug
 	 */
 	public boolean hasSelfReferentialForeignKeyBug() {
 		return false;
 	}
 
 	/**
 	 * The keyword used to specify a nullable column.
 	 *
 	 * @return String
 	 */
 	public String getNullColumnString() {
 		return "";
 	}
 
 	/**
 	 * Does this dialect/database support commenting on tables, columns, etc?
 	 *
 	 * @return {@code true} if commenting is supported
 	 */
 	public boolean supportsCommentOn() {
 		return false;
 	}
 
 	/**
 	 * Get the comment into a form supported for table definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getTableComment(String comment) {
 		return "";
 	}
 
 	/**
 	 * Get the comment into a form supported for column definition.
 	 *
 	 * @param comment The comment to apply
 	 *
 	 * @return The comment fragment
 	 */
 	public String getColumnComment(String comment) {
 		return "";
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied beforeQuery the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied beforeQuery the table name
 	 */
 	public boolean supportsIfExistsBeforeTableName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a table, can the phrase "if exists" be applied afterQuery the table name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeTableName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied afterQuery the table name
 	 */
 	public boolean supportsIfExistsAfterTableName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied beforeQuery the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsAfterConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied beforeQuery the constraint name
 	 */
 	public boolean supportsIfExistsBeforeConstraintName() {
 		return false;
 	}
 
 	/**
 	 * For dropping a constraint with an "alter table", can the phrase "if exists" be applied afterQuery the constraint name?
 	 * <p/>
 	 * NOTE : Only one or the other (or neither) of this and {@link #supportsIfExistsBeforeConstraintName} should return true
 	 *
 	 * @return {@code true} if the "if exists" can be applied afterQuery the constraint name
 	 */
 	public boolean supportsIfExistsAfterConstraintName() {
 		return false;
 	}
 
 	/**
 	 * Generate a DROP TABLE statement
 	 *
 	 * @param tableName The name of the table to drop
 	 *
 	 * @return The DROP TABLE command
 	 */
 	public String getDropTableString(String tableName) {
 		final StringBuilder buf = new StringBuilder( "drop table " );
 		if ( supportsIfExistsBeforeTableName() ) {
 			buf.append( "if exists " );
 		}
 		buf.append( tableName ).append( getCascadeConstraintsString() );
 		if ( supportsIfExistsAfterTableName() ) {
 			buf.append( " if exists" );
 		}
 		return buf.toString();
 	}
 
 	/**
 	 * Does this dialect support column-level check constraints?
 	 *
 	 * @return True if column-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsColumnCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support table-level check constraints?
 	 *
 	 * @return True if table-level CHECK constraints are supported; false
 	 * otherwise.
 	 */
 	public boolean supportsTableCheck() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support cascaded delete on foreign key definitions?
 	 *
 	 * @return {@code true} indicates that the dialect does support cascaded delete on foreign keys.
 	 */
 	public boolean supportsCascadeDelete() {
 		return true;
 	}
 
 	/**
 	 * Completely optional cascading drop clause
 	 *
 	 * @return String
 	 */
 	public String getCascadeConstraintsString() {
 		return "";
 	}
 
 	/**
 	 * Returns the separator to use for defining cross joins when translating HQL queries.
 	 * <p/>
 	 * Typically this will be either [<tt> cross join </tt>] or [<tt>, </tt>]
 	 * <p/>
 	 * Note that the spaces are important!
 	 *
 	 * @return The cross join separator
 	 */
 	public String getCrossJoinSeparator() {
 		return " cross join ";
 	}
 
 	public ColumnAliasExtractor getColumnAliasExtractor() {
 		return ColumnAliasExtractor.COLUMN_LABEL_EXTRACTOR;
 	}
 
 
 	// Informational metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * Does this dialect support empty IN lists?
 	 * <p/>
 	 * For example, is [where XYZ in ()] a supported construct?
 	 *
 	 * @return True if empty in lists are supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsEmptyInList() {
 		return true;
 	}
 
 	/**
 	 * Are string comparisons implicitly case insensitive.
 	 * <p/>
 	 * In other words, does [where 'XYZ' = 'xyz'] resolve to true?
 	 *
 	 * @return True if comparisons are case insensitive.
 	 * @since 3.2
 	 */
 	public boolean areStringComparisonsCaseInsensitive() {
 		return false;
 	}
 
 	/**
 	 * Is this dialect known to support what ANSI-SQL terms "row value
 	 * constructor" syntax; sometimes called tuple syntax.
 	 * <p/>
 	 * Basically, does it support syntax like
 	 * "... where (FIRST_NAME, LAST_NAME) = ('Steve', 'Ebersole') ...".
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntax() {
 		// return false here, as most databases do not properly support this construct...
 		return false;
 	}
 
 	/**
 	 * If the dialect supports {@link #supportsRowValueConstructorSyntax() row values},
 	 * does it offer such support in IN lists as well?
 	 * <p/>
 	 * For example, "... where (FIRST_NAME, LAST_NAME) IN ( (?, ?), (?, ?) ) ..."
 	 *
 	 * @return True if this SQL dialect is known to support "row value
 	 * constructor" syntax in the IN list; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsRowValueConstructorSyntaxInInList() {
 		return false;
 	}
 
 	/**
 	 * Should LOBs (both BLOB and CLOB) be bound using stream operations (i.e.
 	 * {@link java.sql.PreparedStatement#setBinaryStream}).
 	 *
 	 * @return True if BLOBs and CLOBs should be bound using stream operations.
 	 * @since 3.2
 	 */
 	public boolean useInputStreamToInsertBlob() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support parameters within the <tt>SELECT</tt> clause of
 	 * <tt>INSERT ... SELECT ...</tt> statements?
 	 *
 	 * @return True if this is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsParametersInInsertSelect() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect require that references to result variables
 	 * (i.e, select expresssion aliases) in an ORDER BY clause be
 	 * replaced by column positions (1-origin) as defined
 	 * by the select clause?
 
 	 * @return true if result variable references in the ORDER BY
 	 *              clause should be replaced by column positions;
 	 *         false otherwise.
 	 */
 	public boolean replaceResultVariableInOrderByClauseWithPosition() {
 		return false;
 	}
 
 	/**
 	 * Renders an ordering fragment
 	 *
 	 * @param expression The SQL order expression. In case of {@code @OrderBy} annotation user receives property placeholder
 	 * (e.g. attribute name enclosed in '{' and '}' signs).
 	 * @param collation Collation string in format {@code collate IDENTIFIER}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param order Order direction. Possible values: {@code asc}, {@code desc}, or {@code null}
 	 * if expression has not been explicitly specified.
 	 * @param nulls Nulls precedence. Default value: {@link NullPrecedence#NONE}.
 	 * @return Renders single element of {@code ORDER BY} clause.
 	 */
 	public String renderOrderByElement(String expression, String collation, String order, NullPrecedence nulls) {
 		final StringBuilder orderByElement = new StringBuilder( expression );
 		if ( collation != null ) {
 			orderByElement.append( " " ).append( collation );
 		}
 		if ( order != null ) {
 			orderByElement.append( " " ).append( order );
 		}
 		if ( nulls != NullPrecedence.NONE ) {
 			orderByElement.append( " nulls " ).append( nulls.name().toLowerCase(Locale.ROOT) );
 		}
 		return orderByElement.toString();
 	}
 
 	/**
 	 * Does this dialect require that parameters appearing in the <tt>SELECT</tt> clause be wrapped in <tt>cast()</tt>
 	 * calls to tell the db parser the expected type.
 	 *
 	 * @return True if select clause parameter must be cast()ed
 	 * @since 3.2
 	 */
 	public boolean requiresCastingOfParametersInSelectClause() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support asking the result set its positioning
 	 * information on forward only cursors.  Specifically, in the case of
 	 * scrolling fetches, Hibernate needs to use
 	 * {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst}.  Certain drivers do not
 	 * allow access to these methods for forward only cursors.
 	 * <p/>
 	 * NOTE : this is highly driver dependent!
 	 *
 	 * @return True if methods like {@link java.sql.ResultSet#isAfterLast} and
 	 * {@link java.sql.ResultSet#isBeforeFirst} are supported for forward
 	 * only cursors; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsResultSetPositionQueryMethodsOnForwardOnlyCursor() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support definition of cascade delete constraints
 	 * which can cause circular chains?
 	 *
 	 * @return True if circular cascade delete constraints are supported; false
 	 * otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsCircularCascadeDeleteConstraints() {
 		return true;
 	}
 
 	/**
 	 * Are subselects supported as the left-hand-side (LHS) of
 	 * IN-predicates.
 	 * <p/>
 	 * In other words, is syntax like "... <subquery> IN (1, 2, 3) ..." supported?
 	 *
 	 * @return True if subselects can appear as the LHS of an in-predicate;
 	 * false otherwise.
 	 * @since 3.2
 	 */
 	public boolean  supportsSubselectAsInPredicateLHS() {
 		return true;
 	}
 
 	/**
 	 * Expected LOB usage pattern is such that I can perform an insert
 	 * via prepared statement with a parameter binding for a LOB value
 	 * without crazy casting to JDBC driver implementation-specific classes...
 	 * <p/>
 	 * Part of the trickiness here is the fact that this is largely
 	 * driver dependent.  For example, Oracle (which is notoriously bad with
 	 * LOB support in their drivers historically) actually does a pretty good
 	 * job with LOB support as of the 10.2.x versions of their drivers...
 	 *
 	 * @return True if normal LOB usage patterns can be used with this driver;
 	 * false if driver-specific hookiness needs to be applied.
 	 * @since 3.2
 	 */
 	public boolean supportsExpectedLobUsagePattern() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support propagating changes to LOB
 	 * values back to the database?  Talking about mutating the
 	 * internal value of the locator as opposed to supplying a new
 	 * locator instance...
 	 * <p/>
 	 * For BLOBs, the internal value might be changed by:
 	 * {@link java.sql.Blob#setBinaryStream},
 	 * {@link java.sql.Blob#setBytes(long, byte[])},
 	 * {@link java.sql.Blob#setBytes(long, byte[], int, int)},
 	 * or {@link java.sql.Blob#truncate(long)}.
 	 * <p/>
 	 * For CLOBs, the internal value might be changed by:
 	 * {@link java.sql.Clob#setAsciiStream(long)},
 	 * {@link java.sql.Clob#setCharacterStream(long)},
 	 * {@link java.sql.Clob#setString(long, String)},
 	 * {@link java.sql.Clob#setString(long, String, int, int)},
 	 * or {@link java.sql.Clob#truncate(long)}.
 	 * <p/>
 	 * NOTE : I do not know the correct answer currently for
 	 * databases which (1) are not part of the cruise control process
 	 * or (2) do not {@link #supportsExpectedLobUsagePattern}.
 	 *
 	 * @return True if the changes are propagated back to the
 	 * database; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsLobValueChangePropogation() {
 		// todo : pretty sure this is the same as the java.sql.DatabaseMetaData.locatorsUpdateCopy method added in JDBC 4, see HHH-6046
 		return true;
 	}
 
 	/**
 	 * Is it supported to materialize a LOB locator outside the transaction in
 	 * which it was created?
 	 * <p/>
 	 * Again, part of the trickiness here is the fact that this is largely
 	 * driver dependent.
 	 * <p/>
 	 * NOTE: all database I have tested which {@link #supportsExpectedLobUsagePattern()}
 	 * also support the ability to materialize a LOB outside the owning transaction...
 	 *
 	 * @return True if unbounded materialization is supported; false otherwise.
 	 * @since 3.2
 	 */
 	public boolean supportsUnboundedLobLocatorMaterialization() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support referencing the table being mutated in
 	 * a subquery.  The "table being mutated" is the table referenced in
 	 * an UPDATE or a DELETE query.  And so can that table then be
 	 * referenced in a subquery of said UPDATE/DELETE query.
 	 * <p/>
 	 * For example, would the following two syntaxes be supported:<ul>
 	 * <li>delete from TABLE_A where ID not in ( select ID from TABLE_A )</li>
 	 * <li>update TABLE_A set NON_ID = 'something' where ID in ( select ID from TABLE_A)</li>
 	 * </ul>
 	 *
 	 * @return True if this dialect allows references the mutating table from
 	 * a subquery.
 	 */
 	public boolean supportsSubqueryOnMutatingTable() {
 		return true;
 	}
 
 	/**
 	 * Does the dialect support an exists statement in the select clause?
 	 *
 	 * @return True if exists checks are allowed in the select clause; false otherwise.
 	 */
 	public boolean supportsExistsInSelect() {
 		return true;
 	}
 
 	/**
 	 * For the underlying database, is READ_COMMITTED isolation implemented by
 	 * forcing readers to wait for write locks to be released?
 	 *
 	 * @return True if writers block readers to achieve READ_COMMITTED; false otherwise.
 	 */
 	public boolean doesReadCommittedCauseWritersToBlockReaders() {
 		return false;
 	}
 
 	/**
 	 * For the underlying database, is REPEATABLE_READ isolation implemented by
 	 * forcing writers to wait for read locks to be released?
 	 *
 	 * @return True if readers block writers to achieve REPEATABLE_READ; false otherwise.
 	 */
 	public boolean doesRepeatableReadCauseReadersToBlockWriters() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support using a JDBC bind parameter as an argument
 	 * to a function or procedure call?
 	 *
 	 * @return Returns {@code true} if the database supports accepting bind params as args, {@code false} otherwise. The
 	 * default is {@code true}.
 	 */
 	@SuppressWarnings( {"UnusedDeclaration"})
 	public boolean supportsBindAsCallableArgument() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support `count(a,b)`?
 	 *
 	 * @return True if the database supports counting tuples; false otherwise.
 	 */
 	public boolean supportsTupleCounts() {
 		return false;
 	}
 
 	/**
 	 * Does this dialect support `count(distinct a,b)`?
 	 *
 	 * @return True if the database supports counting distinct tuples; false otherwise.
 	 */
 	public boolean supportsTupleDistinctCounts() {
 		// oddly most database in fact seem to, so true is the default.
 		return true;
 	}
 
 	/**
 	 * If {@link #supportsTupleDistinctCounts()} is true, does the Dialect require the tuple to be wrapped with parens?
 	 *
 	 * @return boolean
 	 */
 	public boolean requiresParensForTupleDistinctCounts() {
 		return false;
 	}
 
 	/**
 	 * Return the limit that the underlying database places on the number elements in an {@code IN} predicate.
 	 * If the database defines no such limits, simply return zero or less-than-zero.
 	 *
 	 * @return int The limit, or zero-or-less to indicate no limit.
 	 */
 	public int getInExpressionCountLimit() {
 		return 0;
 	}
 
 	/**
 	 * HHH-4635
 	 * Oracle expects all Lob values to be last in inserts and updates.
 	 *
 	 * @return boolean True of Lob values should be last, false if it
 	 * does not matter.
 	 */
 	public boolean forceLobAsLastValue() {
 		return false;
 	}
 
 	/**
 	 * Some dialects have trouble applying pessimistic locking depending upon what other query options are
 	 * specified (paging, ordering, etc).  This method allows these dialects to request that locking be applied
 	 * by subsequent selects.
 	 *
 	 * @return {@code true} indicates that the dialect requests that locking be applied by subsequent select;
 	 * {@code false} (the default) indicates that locking should be applied to the main SQL statement..
 	 */
 	public boolean useFollowOnLocking() {
 		return false;
 	}
 
 	/**
 	 * Negate an expression
 	 *
 	 * @param expression The expression to negate
 	 *
 	 * @return The negated expression
 	 */
 	public String getNotExpression(String expression) {
 		return "not " + expression;
 	}
 
 	/**
 	 * Get the UniqueDelegate supported by this dialect
 	 *
 	 * @return The UniqueDelegate
 	 */
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
 
 	/**
 	 * Does this dialect support the <tt>UNIQUE</tt> column syntax?
 	 *
 	 * @return boolean
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsUnique() {
 		return true;
 	}
 
 	/**
 	 * Does this dialect support adding Unique constraints via create and alter table ?
 	 *
 	 * @return boolean
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsUniqueConstraintInCreateAlterTable() {
 		return true;
 	}
 
 	/**
 	 * The syntax used to add a unique constraint to a table.
 	 *
 	 * @param constraintName The name of the unique constraint.
 	 * @return The "add unique" fragment
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public String getAddUniqueConstraintString(String constraintName) {
 		return " add constraint " + constraintName + " unique ";
 	}
 
 	/**
 	 * Is the combination of not-null and unique supported?
 	 *
 	 * @return deprecated
 	 *
 	 * @deprecated {@link #getUniqueDelegate()} should be overridden instead.
 	 */
 	@Deprecated
 	public boolean supportsNotNullUnique() {
 		return true;
 	}
 
 	/**
 	 * Apply a hint to the query.  The entire query is provided, allowing the Dialect full control over the placement
 	 * and syntax of the hint.  By default, ignore the hint and simply return the query.
 	 *
 	 * @param query The query to which to apply the hint.
 	 * @param hints The  hints to apply
 	 * @return The modified SQL
 	 */
 	public String getQueryHintString(String query, List<String> hints) {
 		return query;
 	}
 
 	/**
 	 * Certain dialects support a subset of ScrollModes.  Provide a default to be used by Criteria and Query.
 	 *
 	 * @return ScrollMode
 	 */
 	public ScrollMode defaultScrollMode() {
 		return ScrollMode.SCROLL_INSENSITIVE;
 	}
 
 	/**
 	 * Does this dialect support tuples in subqueries?  Ex:
 	 * delete from Table1 where (col1, col2) in (select col1, col2 from Table2)
 	 *
 	 * @return boolean
 	 */
 	public boolean supportsTuplesInSubqueries() {
 		return true;
 	}
 
 	public CallableStatementSupport getCallableStatementSupport() {
 		// most databases do not support returning cursors (ref_cursor)...
 		return StandardCallableStatementSupport.NO_REF_CURSOR_INSTANCE;
 	}
 
 	/**
 	 * By default interpret this based on DatabaseMetaData.
 	 *
 	 * @return
 	 */
 	public NameQualifierSupport getNameQualifierSupport() {
 		return null;
 	}
 
 	protected final BatchLoadSizingStrategy STANDARD_DEFAULT_BATCH_LOAD_SIZING_STRATEGY = new BatchLoadSizingStrategy() {
 		@Override
 		public int determineOptimalBatchLoadSize(int numberOfKeyColumns, int numberOfKeys) {
 			return 50;
 		}
 	};
 
 	public BatchLoadSizingStrategy getDefaultBatchLoadSizingStrategy() {
 		return STANDARD_DEFAULT_BATCH_LOAD_SIZING_STRATEGY;
 	}
 
 	/**
 	 * Does the fetching JDBC statement warning for logging is enabled by default
 	 *
 	 * @return boolean
 	 *
 	 * @since 5.1
 	 */
 	public boolean isJdbcLogWarningsEnabledByDefault() {
 		return true;
 	}
 
 	public void augmentRecognizedTableTypes(List<String> tableTypesList) {
 		// noihing to do
 	}
 
 	/**
 	 * Does the underlying database support partition by
 	 *
 	 * @return boolean
 	 *
 	 * @since 5.2
 	 */
 	public boolean supportsPartitionBy() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
index 77a2543a6c..fd943096a7 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/InformixDialect.java
@@ -1,285 +1,296 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Locale;
 
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.dialect.identity.IdentityColumnSupport;
 import org.hibernate.dialect.identity.InformixIdentityColumnSupport;
 import org.hibernate.dialect.pagination.FirstLimitHandler;
 import org.hibernate.dialect.pagination.LimitHandler;
 import org.hibernate.dialect.unique.InformixUniqueDelegate;
 import org.hibernate.dialect.unique.UniqueDelegate;
 import org.hibernate.exception.spi.TemplatedViolatedConstraintNameExtracter;
 import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.internal.util.JdbcExceptionHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * Informix dialect.<br>
  * <br>
  * Seems to work with Informix Dynamic Server Version 7.31.UD3,  Informix JDBC driver version 2.21JC3.
  *
  * @author Steve Molitor
  */
 public class InformixDialect extends Dialect {
 	
 	private final UniqueDelegate uniqueDelegate;
 
 	/**
 	 * Creates new <code>InformixDialect</code> instance. Sets up the JDBC /
 	 * Informix type mappings.
 	 */
 	public InformixDialect() {
 		super();
 
 		registerColumnType( Types.BIGINT, "int8" );
 		registerColumnType( Types.BINARY, "byte" );
 		// Informix doesn't have a bit type
 		registerColumnType( Types.BIT, "smallint" );
 		registerColumnType( Types.CHAR, "char($l)" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.DECIMAL, "decimal" );
 		registerColumnType( Types.DOUBLE, "float" );
 		registerColumnType( Types.FLOAT, "smallfloat" );
 		registerColumnType( Types.INTEGER, "integer" );
 		// or BYTE
 		registerColumnType( Types.LONGVARBINARY, "blob" );
 		// or TEXT?
 		registerColumnType( Types.LONGVARCHAR, "clob" );
 		// or MONEY
 		registerColumnType( Types.NUMERIC, "decimal" );
 		registerColumnType( Types.REAL, "smallfloat" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TIMESTAMP, "datetime year to fraction(5)" );
 		registerColumnType( Types.TIME, "datetime hour to second" );
 		registerColumnType( Types.TINYINT, "smallint" );
 		registerColumnType( Types.VARBINARY, "byte" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.VARCHAR, 255, "varchar($l)" );
 		registerColumnType( Types.VARCHAR, 32739, "lvarchar($l)" );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		
 		uniqueDelegate = new InformixUniqueDelegate( this );
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	/**
 	 * Informix constraint name must be at the end.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final StringBuilder result = new StringBuilder( 30 )
 				.append( " add constraint " )
 				.append( " foreign key (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			result.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		result.append( " constraint " ).append( constraintName );
 
 		return result.toString();
 	}
 
+	public String getAddForeignKeyConstraintString(
+			String constraintName,
+			String foreignKeyDefinition) {
+		return new StringBuilder( 30 )
+				.append( " add constraint " )
+				.append( foreignKeyDefinition )
+				.append( " constraint " )
+				.append( constraintName )
+				.toString();
+	}
+
 	/**
 	 * Informix constraint name must be at the end.
 	 * <p/>
 	 * {@inheritDoc}
 	 */
 	@Override
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " add constraint primary key constraint " + constraintName + " ";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName + " restrict";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from informix.systables where tabid=1";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsPooledSequences() {
 		return true;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select tabname from informix.systables where tabtype='Q'";
 	}
 
 	@Override
 	public LimitHandler getLimitHandler() {
 		return FirstLimitHandler.INSTANCE;
 	}
 
 	@Override
 	public boolean supportsLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean useMaxForLimit() {
 		return true;
 	}
 
 	@Override
 	public boolean supportsLimitOffset() {
 		return false;
 	}
 
 	@Override
 	public String getLimitString(String querySelect, int offset, int limit) {
 		if ( offset > 0 ) {
 			throw new UnsupportedOperationException( "query result offset is not supported" );
 		}
 		return new StringBuilder( querySelect.length() + 8 )
 				.append( querySelect )
 				.insert( querySelect.toLowerCase(Locale.ROOT).indexOf( "select" ) + 6, " first " + limit )
 				.toString();
 	}
 
 	@Override
 	public boolean supportsVariableLimit() {
 		return false;
 	}
 
 	@Override
 	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
 		return EXTRACTER;
 	}
 
 	private static final ViolatedConstraintNameExtracter EXTRACTER = new TemplatedViolatedConstraintNameExtracter() {
 		@Override
 		protected String doExtractConstraintName(SQLException sqle) throws NumberFormatException {
 			String constraintName = null;
 			final int errorCode = JdbcExceptionHelper.extractErrorCode( sqle );
 
 			if ( errorCode == -268 ) {
 				constraintName = extractUsingTemplate( "Unique constraint (", ") violated.", sqle.getMessage() );
 			}
 			else if ( errorCode == -691 ) {
 				constraintName = extractUsingTemplate(
 						"Missing key in referenced table for referential constraint (",
 						").",
 						sqle.getMessage()
 				);
 			}
 			else if ( errorCode == -692 ) {
 				constraintName = extractUsingTemplate(
 						"Key value for constraint (",
 						") is still being referenced.",
 						sqle.getMessage()
 				);
 			}
 
 			if ( constraintName != null ) {
 				// strip table-owner because Informix always returns constraint names as "<table-owner>.<constraint-name>"
 				final int i = constraintName.indexOf( '.' );
 				if ( i != -1 ) {
 					constraintName = constraintName.substring( i + 1 );
 				}
 			}
 
 			return constraintName;
 		}
 
 	};
 
 	@Override
 	public boolean supportsCurrentTimestampSelection() {
 		return true;
 	}
 
 	@Override
 	public boolean isCurrentTimestampSelectStringCallable() {
 		return false;
 	}
 
 	@Override
 	public String getCurrentTimestampSelectString() {
 		return "select distinct current timestamp from informix.systables";
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new LocalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String getCreateIdTableCommand() {
 						return "create temp table";
 					}
 
 					@Override
 					public String getCreateIdTableStatementOptions() {
 						return "with no log";
 					}
 				},
 				AfterUseAction.CLEAN,
 				null
 		);
 	}
 	
 	@Override
 	public UniqueDelegate getUniqueDelegate() {
 		return uniqueDelegate;
 	}
 
 	@Override
 	public IdentityColumnSupport getIdentityColumnSupport() {
 		return new InformixIdentityColumnSupport();
 	}
 
 	@Override
 	public String toBooleanValueString(boolean bool) {
 		return bool ? "'t'" : "'f'";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java b/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
index 998887671a..8ec9fa18b0 100644
--- a/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
+++ b/hibernate-core/src/main/java/org/hibernate/dialect/SAPDBDialect.java
@@ -1,227 +1,233 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.dialect;
 
 import java.sql.Types;
 
 import org.hibernate.cfg.Environment;
 import org.hibernate.dialect.function.NoArgSQLFunction;
 import org.hibernate.dialect.function.SQLFunctionTemplate;
 import org.hibernate.dialect.function.StandardSQLFunction;
 import org.hibernate.dialect.function.VarArgsSQLFunction;
 import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
 import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
 import org.hibernate.hql.spi.id.local.AfterUseAction;
 import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.DecodeCaseFragment;
 import org.hibernate.type.StandardBasicTypes;
 
 /**
  * An SQL dialect compatible with SAP DB.
  *
  * @author Brad Clow
  */
 public class SAPDBDialect extends Dialect {
 	/**
 	 * Constructs a SAPDBDialect
 	 */
 	public SAPDBDialect() {
 		super();
 		registerColumnType( Types.BIT, "boolean" );
 		registerColumnType( Types.BIGINT, "fixed(19,0)" );
 		registerColumnType( Types.SMALLINT, "smallint" );
 		registerColumnType( Types.TINYINT, "fixed(3,0)" );
 		registerColumnType( Types.INTEGER, "int" );
 		registerColumnType( Types.CHAR, "char(1)" );
 		registerColumnType( Types.VARCHAR, "varchar($l)" );
 		registerColumnType( Types.FLOAT, "float" );
 		registerColumnType( Types.DOUBLE, "double precision" );
 		registerColumnType( Types.DATE, "date" );
 		registerColumnType( Types.TIME, "time" );
 		registerColumnType( Types.TIMESTAMP, "timestamp" );
 		registerColumnType( Types.VARBINARY, "long byte" );
 		registerColumnType( Types.NUMERIC, "fixed($p,$s)" );
 		registerColumnType( Types.CLOB, "long varchar" );
 		registerColumnType( Types.BLOB, "long byte" );
 
 		registerFunction( "abs", new StandardSQLFunction( "abs" ) );
 		registerFunction( "sign", new StandardSQLFunction( "sign", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "exp", new StandardSQLFunction( "exp", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "ln", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "log", new StandardSQLFunction( "ln", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "pi", new NoArgSQLFunction( "pi", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "power", new StandardSQLFunction( "power" ) );
 		registerFunction( "acos", new StandardSQLFunction( "acos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "asin", new StandardSQLFunction( "asin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan", new StandardSQLFunction( "atan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cos", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cosh", new StandardSQLFunction( "cosh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "cot", new StandardSQLFunction( "cos", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sin", new StandardSQLFunction( "sin", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "sinh", new StandardSQLFunction( "sinh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tan", new StandardSQLFunction( "tan", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "tanh", new StandardSQLFunction( "tanh", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "radians", new StandardSQLFunction( "radians", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "degrees", new StandardSQLFunction( "degrees", StandardBasicTypes.DOUBLE ) );
 		registerFunction( "atan2", new StandardSQLFunction( "atan2", StandardBasicTypes.DOUBLE ) );
 
 		registerFunction( "round", new StandardSQLFunction( "round" ) );
 		registerFunction( "trunc", new StandardSQLFunction( "trunc" ) );
 		registerFunction( "ceil", new StandardSQLFunction( "ceil" ) );
 		registerFunction( "floor", new StandardSQLFunction( "floor" ) );
 		registerFunction( "greatest", new StandardSQLFunction( "greatest" ) );
 		registerFunction( "least", new StandardSQLFunction( "least" ) );
 
 		registerFunction( "time", new StandardSQLFunction( "time", StandardBasicTypes.TIME ) );
 		registerFunction( "timestamp", new StandardSQLFunction( "timestamp", StandardBasicTypes.TIMESTAMP ) );
 		registerFunction( "date", new StandardSQLFunction( "date", StandardBasicTypes.DATE ) );
 		registerFunction( "microsecond", new StandardSQLFunction( "microsecond", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "second", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "second(?1)" ) );
 		registerFunction( "minute", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "minute(?1)" ) );
 		registerFunction( "hour", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "hour(?1)" ) );
 		registerFunction( "day", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "day(?1)" ) );
 		registerFunction( "month", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "month(?1)" ) );
 		registerFunction( "year", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "year(?1)" ) );
 
 		registerFunction( "extract", new SQLFunctionTemplate( StandardBasicTypes.INTEGER, "?1(?3)" ) );
 
 		registerFunction( "dayname", new StandardSQLFunction( "dayname", StandardBasicTypes.STRING ) );
 		registerFunction( "monthname", new StandardSQLFunction( "monthname", StandardBasicTypes.STRING ) );
 		registerFunction( "dayofmonth", new StandardSQLFunction( "dayofmonth", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofweek", new StandardSQLFunction( "dayofweek", StandardBasicTypes.INTEGER ) );
 		registerFunction( "dayofyear", new StandardSQLFunction( "dayofyear", StandardBasicTypes.INTEGER ) );
 		registerFunction( "weekofyear", new StandardSQLFunction( "weekofyear", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "replace", new StandardSQLFunction( "replace", StandardBasicTypes.STRING ) );
 		registerFunction( "translate", new StandardSQLFunction( "translate", StandardBasicTypes.STRING ) );
 		registerFunction( "lpad", new StandardSQLFunction( "lpad", StandardBasicTypes.STRING ) );
 		registerFunction( "rpad", new StandardSQLFunction( "rpad", StandardBasicTypes.STRING ) );
 		registerFunction( "substr", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "initcap", new StandardSQLFunction( "initcap", StandardBasicTypes.STRING ) );
 		registerFunction( "lower", new StandardSQLFunction( "lower", StandardBasicTypes.STRING ) );
 		registerFunction( "ltrim", new StandardSQLFunction( "ltrim", StandardBasicTypes.STRING ) );
 		registerFunction( "rtrim", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
 		registerFunction( "lfill", new StandardSQLFunction( "ltrim", StandardBasicTypes.STRING ) );
 		registerFunction( "rfill", new StandardSQLFunction( "rtrim", StandardBasicTypes.STRING ) );
 		registerFunction( "soundex", new StandardSQLFunction( "soundex", StandardBasicTypes.STRING ) );
 		registerFunction( "upper", new StandardSQLFunction( "upper", StandardBasicTypes.STRING ) );
 		registerFunction( "ascii", new StandardSQLFunction( "ascii", StandardBasicTypes.STRING ) );
 		registerFunction( "index", new StandardSQLFunction( "index", StandardBasicTypes.INTEGER ) );
 
 		registerFunction( "value", new StandardSQLFunction( "value" ) );
 
 		registerFunction( "concat", new VarArgsSQLFunction( StandardBasicTypes.STRING, "(", "||", ")" ) );
 		registerFunction( "substring", new StandardSQLFunction( "substr", StandardBasicTypes.STRING ) );
 		registerFunction( "locate", new StandardSQLFunction( "index", StandardBasicTypes.INTEGER ) );
 		registerFunction( "coalesce", new StandardSQLFunction( "value" ) );
 
 		getDefaultProperties().setProperty( Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE );
 
 	}
 
 	@Override
 	public boolean dropConstraints() {
 		return false;
 	}
 
 	@Override
 	public String getAddColumnString() {
 		return "add";
 	}
 
 	@Override
 	public String getAddForeignKeyConstraintString(
 			String constraintName,
 			String[] foreignKey,
 			String referencedTable,
 			String[] primaryKey,
 			boolean referencesPrimaryKey) {
 		final StringBuilder res = new StringBuilder( 30 )
 				.append( " foreign key " )
 				.append( constraintName )
 				.append( " (" )
 				.append( StringHelper.join( ", ", foreignKey ) )
 				.append( ") references " )
 				.append( referencedTable );
 
 		if ( !referencesPrimaryKey ) {
 			res.append( " (" )
 					.append( StringHelper.join( ", ", primaryKey ) )
 					.append( ')' );
 		}
 
 		return res.toString();
 	}
 
+	public String getAddForeignKeyConstraintString(
+			String constraintName,
+			String foreignKeyDefinition) {
+		return foreignKeyDefinition;
+	}
+
 	@Override
 	public String getAddPrimaryKeyConstraintString(String constraintName) {
 		return " primary key ";
 	}
 
 	@Override
 	public String getNullColumnString() {
 		return " null";
 	}
 
 	@Override
 	public String getSequenceNextValString(String sequenceName) {
 		return "select " + getSelectSequenceNextValString( sequenceName ) + " from dual";
 	}
 
 	@Override
 	public String getSelectSequenceNextValString(String sequenceName) {
 		return sequenceName + ".nextval";
 	}
 
 	@Override
 	public String getCreateSequenceString(String sequenceName) {
 		return "create sequence " + sequenceName;
 	}
 
 	@Override
 	public String getDropSequenceString(String sequenceName) {
 		return "drop sequence " + sequenceName;
 	}
 
 	@Override
 	public String getQuerySequencesString() {
 		return "select sequence_name from domain.sequences";
 	}
 
 	@Override
 	public boolean supportsSequences() {
 		return true;
 	}
 
 	@Override
 	public CaseFragment createCaseFragment() {
 		return new DecodeCaseFragment();
 	}
 
 	@Override
 	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
 		return new LocalTemporaryTableBulkIdStrategy(
 				new IdTableSupportStandardImpl() {
 					@Override
 					public String generateIdTableName(String baseName) {
 						return "temp." + super.generateIdTableName( baseName );
 					}
 
 					@Override
 					public String getCreateIdTableStatementOptions() {
 						return "ignore rollback";
 					}
 				},
 				AfterUseAction.DROP,
 				null
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/DenormalizedTable.java b/hibernate-core/src/main/java/org/hibernate/mapping/DenormalizedTable.java
index 7668f49a33..74362c4691 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/DenormalizedTable.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/DenormalizedTable.java
@@ -1,142 +1,143 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.relational.Namespace;
 import org.hibernate.internal.util.collections.JoinedIterator;
 
 /**
  * @author Gavin King
  */
 @SuppressWarnings("unchecked")
 public class DenormalizedTable extends Table {
 
 	private final Table includedTable;
 
 	public DenormalizedTable(Table includedTable) {
 		this.includedTable = includedTable;
 		includedTable.setHasDenormalizedTables();
 	}
 
 	public DenormalizedTable(Namespace namespace, Identifier physicalTableName, boolean isAbstract, Table includedTable) {
 		super( namespace, physicalTableName, isAbstract );
 		this.includedTable = includedTable;
 		includedTable.setHasDenormalizedTables();
 	}
 
 	public DenormalizedTable(
 			Namespace namespace,
 			Identifier physicalTableName,
 			String subselectFragment,
 			boolean isAbstract,
 			Table includedTable) {
 		super( namespace, physicalTableName, subselectFragment, isAbstract );
 		this.includedTable = includedTable;
 		includedTable.setHasDenormalizedTables();
 	}
 
 	public DenormalizedTable(Namespace namespace, String subselect, boolean isAbstract, Table includedTable) {
 		super( namespace, subselect, isAbstract );
 		this.includedTable = includedTable;
 		includedTable.setHasDenormalizedTables();
 	}
 
 	@Override
 	public void createForeignKeys() {
 		includedTable.createForeignKeys();
 		Iterator iter = includedTable.getForeignKeyIterator();
 		while ( iter.hasNext() ) {
 			ForeignKey fk = (ForeignKey) iter.next();
 			createForeignKey(
 					Constraint.generateName(
 							fk.generatedConstraintNamePrefix(),
 							this,
 							fk.getColumns()
 					),
 					fk.getColumns(),
 					fk.getReferencedEntityName(),
+					fk.getKeyDefinition(),
 					fk.getReferencedColumns()
 			);
 		}
 	}
 
 	@Override
 	public Column getColumn(Column column) {
 		Column superColumn = super.getColumn( column );
 		if ( superColumn != null ) {
 			return superColumn;
 		}
 		else {
 			return includedTable.getColumn( column );
 		}
 	}
 
 	public Column getColumn(Identifier name) {
 		Column superColumn = super.getColumn( name );
 		if ( superColumn != null ) {
 			return superColumn;
 		}
 		else {
 			return includedTable.getColumn( name );
 		}
 	}
 
 	@Override
 	public Iterator getColumnIterator() {
 		return new JoinedIterator(
 				includedTable.getColumnIterator(),
 				super.getColumnIterator()
 		);
 	}
 
 	@Override
 	public boolean containsColumn(Column column) {
 		return super.containsColumn( column ) || includedTable.containsColumn( column );
 	}
 
 	@Override
 	public PrimaryKey getPrimaryKey() {
 		return includedTable.getPrimaryKey();
 	}
 
 	@Override
 	public Iterator getUniqueKeyIterator() {
 		Iterator iter = includedTable.getUniqueKeyIterator();
 		while ( iter.hasNext() ) {
 			UniqueKey uk = (UniqueKey) iter.next();
 			createUniqueKey( uk.getColumns() );
 		}
 		return getUniqueKeys().values().iterator();
 	}
 
 	@Override
 	public Iterator getIndexIterator() {
 		List indexes = new ArrayList();
 		Iterator iter = includedTable.getIndexIterator();
 		while ( iter.hasNext() ) {
 			Index parentIndex = (Index) iter.next();
 			Index index = new Index();
 			index.setName( getName() + parentIndex.getName() );
 			index.setTable( this );
 			index.addColumns( parentIndex.getColumnIterator() );
 			indexes.add( index );
 		}
 		return new JoinedIterator(
 				indexes.iterator(),
 				super.getIndexIterator()
 		);
 	}
 
 	public Table getIncludedTable() {
 		return includedTable;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/ForeignKey.java b/hibernate-core/src/main/java/org/hibernate/mapping/ForeignKey.java
index 09c3bc9851..f0da8b0fe1 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/ForeignKey.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/ForeignKey.java
@@ -1,228 +1,247 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.internal.util.StringHelper;
 
 /**
  * A foreign key constraint
  *
  * @author Gavin King
  */
 public class ForeignKey extends Constraint {
 	private Table referencedTable;
 	private String referencedEntityName;
+	private String keyDefinition;
 	private boolean cascadeDeleteEnabled;
 	private List<Column> referencedColumns = new ArrayList<Column>();
 	private boolean creationEnabled = true;
 
 	public ForeignKey() {
 	}
 
 	@Override
 	public String getExportIdentifier() {
 		// NOt sure name is always set.  Might need some implicit naming
 		return StringHelper.qualify( getTable().getName(), "FK-" + getName() );
 	}
 
 	public void disableCreation() {
 		creationEnabled = false;
 	}
 
 	public boolean isCreationEnabled() {
 		return creationEnabled;
 	}
 
 	@Override
 	public void setName(String name) {
 		super.setName( name );
 		// the FK name "none" is a magic value in the hbm.xml binding that indicated to
 		// not create a FK.
 		if ( "none".equals( name ) ) {
 			disableCreation();
 		}
 	}
 
 	public String sqlConstraintString(
 			Dialect dialect,
 			String constraintName,
 			String defaultCatalog,
 			String defaultSchema) {
 		String[] columnNames = new String[getColumnSpan()];
 		String[] referencedColumnNames = new String[getColumnSpan()];
 
 		final Iterator<Column> referencedColumnItr;
 		if ( isReferenceToPrimaryKey() ) {
 			referencedColumnItr = referencedTable.getPrimaryKey().getColumnIterator();
 		}
 		else {
 			referencedColumnItr = referencedColumns.iterator();
 		}
 
 		Iterator columnItr = getColumnIterator();
 		int i = 0;
 		while ( columnItr.hasNext() ) {
 			columnNames[i] = ( (Column) columnItr.next() ).getQuotedName( dialect );
 			referencedColumnNames[i] = referencedColumnItr.next().getQuotedName( dialect );
 			i++;
 		}
 
-		final String result = dialect.getAddForeignKeyConstraintString(
-				constraintName,
-				columnNames,
-				referencedTable.getQualifiedName( dialect, defaultCatalog, defaultSchema ),
-				referencedColumnNames,
-				isReferenceToPrimaryKey()
-		);
+		final String result = keyDefinition != null ?
+				dialect.getAddForeignKeyConstraintString(
+						constraintName,
+						keyDefinition
+				) :
+				dialect.getAddForeignKeyConstraintString(
+						constraintName,
+						columnNames,
+						referencedTable.getQualifiedName(
+								dialect,
+								defaultCatalog,
+								defaultSchema
+						),
+						referencedColumnNames,
+						isReferenceToPrimaryKey()
+				);
+		
 		return cascadeDeleteEnabled && dialect.supportsCascadeDelete()
 				? result + " on delete cascade"
 				: result;
 	}
 
 	public Table getReferencedTable() {
 		return referencedTable;
 	}
 
 	private void appendColumns(StringBuilder buf, Iterator columns) {
 		while ( columns.hasNext() ) {
 			Column column = (Column) columns.next();
 			buf.append( column.getName() );
 			if ( columns.hasNext() ) {
 				buf.append( "," );
 			}
 		}
 	}
 
 	public void setReferencedTable(Table referencedTable) throws MappingException {
 		//if( isReferenceToPrimaryKey() ) alignColumns(referencedTable); // TODO: possibly remove to allow more piecemal building of a foreignkey.  
 
 		this.referencedTable = referencedTable;
 	}
 
 	/**
 	 * Validates that columnspan of the foreignkey and the primarykey is the same.
 	 * <p/>
 	 * Furthermore it aligns the length of the underlying tables columns.
 	 */
 	public void alignColumns() {
 		if ( isReferenceToPrimaryKey() ) {
 			alignColumns( referencedTable );
 		}
 	}
 
 	private void alignColumns(Table referencedTable) {
 		final int referencedPkColumnSpan = referencedTable.getPrimaryKey().getColumnSpan();
 		if ( referencedPkColumnSpan != getColumnSpan() ) {
 			StringBuilder sb = new StringBuilder();
 			sb.append( "Foreign key (" ).append( getName() ).append( ":" )
 					.append( getTable().getName() )
 					.append( " [" );
 			appendColumns( sb, getColumnIterator() );
 			sb.append( "])" )
 					.append( ") must have same number of columns as the referenced primary key (" )
 					.append( referencedTable.getName() )
 					.append( " [" );
 			appendColumns( sb, referencedTable.getPrimaryKey().getColumnIterator() );
 			sb.append( "])" );
 			throw new MappingException( sb.toString() );
 		}
 
 		Iterator fkCols = getColumnIterator();
 		Iterator pkCols = referencedTable.getPrimaryKey().getColumnIterator();
 		while ( pkCols.hasNext() ) {
 			( (Column) fkCols.next() ).setLength( ( (Column) pkCols.next() ).getLength() );
 		}
 
 	}
 
 	public String getReferencedEntityName() {
 		return referencedEntityName;
 	}
 
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
+	public String getKeyDefinition() {
+		return keyDefinition;
+	}
+
+	public void setKeyDefinition(String keyDefinition) {
+		this.keyDefinition = keyDefinition;
+	}
+	
 	public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		final StringBuilder buf = new StringBuilder( "alter table " );
 		buf.append( getTable().getQualifiedName( dialect, defaultCatalog, defaultSchema ) );
 		buf.append( dialect.getDropForeignKeyString() );
 		if ( dialect.supportsIfExistsBeforeConstraintName() ) {
 			buf.append( "if exists " );
 		}
 		buf.append( dialect.quote( getName() ) );
 		if ( dialect.supportsIfExistsAfterConstraintName() ) {
 			buf.append( " if exists" );
 		}
 		return buf.toString();
 	}
 
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
 		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
 	}
 
 	public boolean isPhysicalConstraint() {
 		return referencedTable.isPhysicalTable()
 				&& getTable().isPhysicalTable()
 				&& !referencedTable.hasDenormalizedTables();
 	}
 
 	/**
 	 * Returns the referenced columns if the foreignkey does not refer to the primary key
 	 */
 	public List getReferencedColumns() {
 		return referencedColumns;
 	}
 
 	/**
 	 * Does this foreignkey reference the primary key of the reference table
 	 */
 	public boolean isReferenceToPrimaryKey() {
 		return referencedColumns.isEmpty();
 	}
 
 	public void addReferencedColumns(Iterator referencedColumnsIterator) {
 		while ( referencedColumnsIterator.hasNext() ) {
 			Selectable col = (Selectable) referencedColumnsIterator.next();
 			if ( !col.isFormula() ) {
 				addReferencedColumn( (Column) col );
 			}
 		}
 	}
 
 	private void addReferencedColumn(Column column) {
 		if ( !referencedColumns.contains( column ) ) {
 			referencedColumns.add( column );
 		}
 	}
 
 	public String toString() {
 		if ( !isReferenceToPrimaryKey() ) {
 			return getClass().getName()
 					+ '(' + getTable().getName() + getColumns()
 					+ " ref-columns:" + '(' + getReferencedColumns() + ") as " + getName() + ")";
 		}
 		else {
 			return super.toString();
 		}
 
 	}
 
 	public String generatedConstraintNamePrefix() {
 		return "FK_";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/ManyToOne.java b/hibernate-core/src/main/java/org/hibernate/mapping/ManyToOne.java
index 52cc461bd6..8c41ae4a01 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/ManyToOne.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/ManyToOne.java
@@ -1,104 +1,105 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.MappingException;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * A many-to-one association mapping
  * @author Gavin King
  */
 public class ManyToOne extends ToOne {
 	private boolean ignoreNotFound;
 	private boolean isLogicalOneToOne;
 	
 	public ManyToOne(MetadataImplementor metadata, Table table) {
 		super( metadata, table );
 	}
 
 	public Type getType() throws MappingException {
 		return getMetadata().getTypeResolver().getTypeFactory().manyToOne(
 				getReferencedEntityName(),
 				referenceToPrimaryKey, 
 				getReferencedPropertyName(),
 				isLazy(),
 				isUnwrapProxy(),
 				isIgnoreNotFound(),
 				isLogicalOneToOne
 		);
 	}
 
 	public void createForeignKey() throws MappingException {
 		// the case of a foreign key to something other than the pk is handled in createPropertyRefConstraints
 		if (referencedPropertyName==null && !hasFormula() ) {
 			createForeignKeyOfEntity( ( (EntityType) getType() ).getAssociatedEntityName() );
 		} 
 	}
 
 	public void createPropertyRefConstraints(Map persistentClasses) {
 		if (referencedPropertyName!=null) {
 			PersistentClass pc = (PersistentClass) persistentClasses.get(getReferencedEntityName() );
 			
 			Property property = pc.getReferencedProperty( getReferencedPropertyName() );
 			
 			if (property==null) {
 				throw new MappingException(
 						"Could not find property " + 
 						getReferencedPropertyName() + 
 						" on " + 
 						getReferencedEntityName() 
 					);
 			} 
 			else {
 				// todo : if "none" another option is to create the ForeignKey object still	but to set its #disableCreation flag
 				if ( !hasFormula() && !"none".equals( getForeignKeyName() ) ) {
 					java.util.List refColumns = new ArrayList();
 					Iterator iter = property.getColumnIterator();
 					while ( iter.hasNext() ) {
 						Column col = (Column) iter.next();
 						refColumns.add( col );							
 					}
 					
 					ForeignKey fk = getTable().createForeignKey( 
 							getForeignKeyName(), 
 							getConstraintColumns(), 
 							( (EntityType) getType() ).getAssociatedEntityName(), 
+							getForeignKeyDefinition(), 
 							refColumns
 					);
 					fk.setCascadeDeleteEnabled(isCascadeDeleteEnabled() );
 				}
 			}
 		}
 	}
 	
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 
 	public boolean isIgnoreNotFound() {
 		return ignoreNotFound;
 	}
 
 	public void setIgnoreNotFound(boolean ignoreNotFound) {
 		this.ignoreNotFound = ignoreNotFound;
 	}
 
 	public void markAsLogicalOneToOne() {
 		this.isLogicalOneToOne = true;
 	}
 
 	public boolean isLogicalOneToOne() {
 		return isLogicalOneToOne;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index 6041da0de2..6da536e8b0 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,699 +1,708 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.lang.annotation.Annotation;
 import java.sql.Types;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Locale;
 import java.util.Properties;
 import javax.persistence.AttributeConverter;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.boot.internal.AttributeConverterDescriptorNonAutoApplicableImpl;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
 import org.hibernate.boot.spi.AttributeConverterDescriptor;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.config.spi.ConfigurationService;
 import org.hibernate.engine.config.spi.StandardConverters;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreLogging;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.JdbcTypeNameMapper;
 import org.hibernate.type.descriptor.converter.AttributeConverterSqlTypeDescriptorAdapter;
 import org.hibernate.type.descriptor.converter.AttributeConverterTypeAdapter;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
 import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
 import org.hibernate.type.descriptor.sql.LobTypeMappings;
 import org.hibernate.type.descriptor.sql.NationalizedTypeMappings;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry;
 import org.hibernate.usertype.DynamicParameterizedType;
 
 /**
  * Any value that maps to columns.
  * @author Gavin King
  */
 public class SimpleValue implements KeyValue {
 	private static final CoreMessageLogger log = CoreLogging.messageLogger( SimpleValue.class );
 
 	public static final String DEFAULT_ID_GEN_STRATEGY = "assigned";
 
 	private final MetadataImplementor metadata;
 
 	private final List<Selectable> columns = new ArrayList<Selectable>();
 
 	private String typeName;
 	private Properties typeParameters;
 	private boolean isNationalized;
 	private boolean isLob;
 
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
+	private String foreignKeyDefinition;
 	private boolean alternateUniqueKey;
 	private boolean cascadeDeleteEnabled;
 
 	private AttributeConverterDescriptor attributeConverterDescriptor;
 	private Type type;
 
 	public SimpleValue(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	public SimpleValue(MetadataImplementor metadata, Table table) {
 		this( metadata );
 		this.table = table;
 	}
 
 	public MetadataImplementor getMetadata() {
 		return metadata;
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return getMetadata().getMetadataBuildingOptions().getServiceRegistry();
 	}
 
 	@Override
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
 		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
 	}
 	
 	public void addColumn(Column column) {
 		if ( !columns.contains(column) ) {
 			columns.add(column);
 		}
 		column.setValue(this);
 		column.setTypeIndex( columns.size() - 1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add( formula );
 	}
 
 	@Override
 	public boolean hasFormula() {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Object o = iter.next();
 			if (o instanceof Formula) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	@Override
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
 	@Override
 	public Iterator<Selectable> getColumnIterator() {
 		return columns.iterator();
 	}
 
 	public List getConstraintColumns() {
 		return columns;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 
 	public void setTypeName(String typeName) {
 		if ( typeName != null && typeName.startsWith( AttributeConverterTypeAdapter.NAME_PREFIX ) ) {
 			final String converterClassName = typeName.substring( AttributeConverterTypeAdapter.NAME_PREFIX.length() );
 			final ClassLoaderService cls = getMetadata().getMetadataBuildingOptions()
 					.getServiceRegistry()
 					.getService( ClassLoaderService.class );
 			try {
 				final Class<AttributeConverter> converterClass = cls.classForName( converterClassName );
 				attributeConverterDescriptor = new AttributeConverterDescriptorNonAutoApplicableImpl( converterClass.newInstance() );
 				return;
 			}
 			catch (Exception e) {
 				log.logBadHbmAttributeConverterType( typeName, e.getMessage() );
 			}
 		}
 
 		this.typeName = typeName;
 	}
 
 	public void makeNationalized() {
 		this.isNationalized = true;
 	}
 
 	public boolean isNationalized() {
 		return isNationalized;
 	}
 
 	public void makeLob() {
 		this.isLob = true;
 	}
 
 	public boolean isLob() {
 		return isLob;
 	}
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	@Override
 	public void createForeignKey() throws MappingException {}
 
 	@Override
 	public void createForeignKeyOfEntity(String entityName) {
 		if ( !hasFormula() && !"none".equals(getForeignKeyName())) {
-			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName );
+			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName, getForeignKeyDefinition() );
 			fk.setCascadeDeleteEnabled(cascadeDeleteEnabled);
 		}
 	}
 
 	private IdentifierGenerator identifierGenerator;
 
 	@Override
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect, 
 			String defaultCatalog, 
 			String defaultSchema, 
 			RootClass rootClass) throws MappingException {
 
 		if ( identifierGenerator != null ) {
 			return identifierGenerator;
 		}
 
 		Properties params = new Properties();
 		
 		//if the hibernate-mapping did not specify a schema/catalog, use the defaults
 		//specified by properties - but note that if the schema/catalog were specified
 		//in hibernate-mapping, or as params, they will already be initialized and
 		//will override the values set here (they are in identifierGeneratorProperties)
 		if ( defaultSchema!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.SCHEMA, defaultSchema);
 		}
 		if ( defaultCatalog!=null ) {
 			params.setProperty(PersistentIdentifierGenerator.CATALOG, defaultCatalog);
 		}
 		
 		//pass the entity-name, if not a collection-id
 		if (rootClass!=null) {
 			params.setProperty( IdentifierGenerator.ENTITY_NAME, rootClass.getEntityName() );
 			params.setProperty( IdentifierGenerator.JPA_ENTITY_NAME, rootClass.getJpaEntityName() );
 		}
 		
 		//init the table here instead of earlier, so that we can get a quoted table name
 		//TODO: would it be better to simply pass the qualified table name, instead of
 		//      splitting it up into schema/catalog/table names
 		String tableName = getTable().getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.TABLE, tableName );
 		
 		//pass the column name (a generated id almost always has a single column)
 		String columnName = ( (Column) getColumnIterator().next() ).getQuotedName(dialect);
 		params.setProperty( PersistentIdentifierGenerator.PK, columnName );
 		
 		if (rootClass!=null) {
 			StringBuilder tables = new StringBuilder();
 			Iterator iter = rootClass.getIdentityTables().iterator();
 			while ( iter.hasNext() ) {
 				Table table= (Table) iter.next();
 				tables.append( table.getQuotedName(dialect) );
 				if ( iter.hasNext() ) {
 					tables.append(", ");
 				}
 			}
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tables.toString() );
 		}
 		else {
 			params.setProperty( PersistentIdentifierGenerator.TABLES, tableName );
 		}
 
 		if (identifierGeneratorProperties!=null) {
 			params.putAll(identifierGeneratorProperties);
 		}
 
 		// TODO : we should pass along all settings once "config lifecycle" is hashed out...
 		final ConfigurationService cs = metadata.getMetadataBuildingOptions().getServiceRegistry()
 				.getService( ConfigurationService.class );
 
 		params.put(
 				AvailableSettings.PREFER_POOLED_VALUES_LO,
 				cs.getSetting( AvailableSettings.PREFER_POOLED_VALUES_LO, StandardConverters.BOOLEAN, false )
 		);
 		if ( cs.getSettings().get( AvailableSettings.PREFERRED_POOLED_OPTIMIZER ) != null ) {
 			params.put(
 					AvailableSettings.PREFERRED_POOLED_OPTIMIZER,
 					cs.getSettings().get( AvailableSettings.PREFERRED_POOLED_OPTIMIZER )
 			);
 		}
 
 		identifierGeneratorFactory.setDialect( dialect );
 		identifierGenerator = identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
 
 		return identifierGenerator;
 	}
 
 	public boolean isUpdateable() {
 		//needed to satisfy KeyValue
 		return true;
 	}
 	
 	public FetchMode getFetchMode() {
 		return FetchMode.SELECT;
 	}
 
 	public Properties getIdentifierGeneratorProperties() {
 		return identifierGeneratorProperties;
 	}
 
 	public String getNullValue() {
 		return nullValue;
 	}
 
 	public Table getTable() {
 		return table;
 	}
 
 	/**
 	 * Returns the identifierGeneratorStrategy.
 	 * @return String
 	 */
 	public String getIdentifierGeneratorStrategy() {
 		return identifierGeneratorStrategy;
 	}
 	
 	public boolean isIdentityColumn(IdentifierGeneratorFactory identifierGeneratorFactory, Dialect dialect) {
 		identifierGeneratorFactory.setDialect( dialect );
 		return IdentityGenerator.class.isAssignableFrom(identifierGeneratorFactory.getIdentifierGeneratorClass( identifierGeneratorStrategy ));
 	}
 
 	/**
 	 * Sets the identifierGeneratorProperties.
 	 * @param identifierGeneratorProperties The identifierGeneratorProperties to set
 	 */
 	public void setIdentifierGeneratorProperties(Properties identifierGeneratorProperties) {
 		this.identifierGeneratorProperties = identifierGeneratorProperties;
 	}
 
 	/**
 	 * Sets the identifierGeneratorStrategy.
 	 * @param identifierGeneratorStrategy The identifierGeneratorStrategy to set
 	 */
 	public void setIdentifierGeneratorStrategy(String identifierGeneratorStrategy) {
 		this.identifierGeneratorStrategy = identifierGeneratorStrategy;
 	}
 
 	/**
 	 * Sets the nullValue.
 	 * @param nullValue The nullValue to set
 	 */
 	public void setNullValue(String nullValue) {
 		this.nullValue = nullValue;
 	}
 
 	public String getForeignKeyName() {
 		return foreignKeyName;
 	}
 
 	public void setForeignKeyName(String foreignKeyName) {
 		this.foreignKeyName = foreignKeyName;
 	}
+	
+	public String getForeignKeyDefinition() {
+		return foreignKeyDefinition;
+	}
+
+	public void setForeignKeyDefinition(String foreignKeyDefinition) {
+		this.foreignKeyDefinition = foreignKeyDefinition;
+	}
 
 	public boolean isAlternateUniqueKey() {
 		return alternateUniqueKey;
 	}
 
 	public void setAlternateUniqueKey(boolean unique) {
 		this.alternateUniqueKey = unique;
 	}
 
 	public boolean isNullable() {
 		Iterator itr = getColumnIterator();
 		while ( itr.hasNext() ) {
 			final Object selectable = itr.next();
 			if ( selectable instanceof Formula ) {
 				// if there are *any* formulas, then the Value overall is
 				// considered nullable
 				return true;
 			}
 			else if ( !( (Column) selectable ).isNullable() ) {
 				// if there is a single non-nullable column, the Value
 				// overall is considered non-nullable.
 				return false;
 			}
 		}
 		// nullable by default
 		return true;
 	}
 
 	public boolean isSimpleValue() {
 		return true;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return getColumnSpan()==getType().getColumnSpan(mapping);
 	}
 
 	public Type getType() throws MappingException {
 		if ( type != null ) {
 			return type;
 		}
 
 		if ( typeName == null ) {
 			throw new MappingException( "No type name" );
 		}
 
 		if ( typeParameters != null
 				&& Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_DYNAMIC ) )
 				&& typeParameters.get( DynamicParameterizedType.PARAMETER_TYPE ) == null ) {
 			createParameterImpl();
 		}
 
 		Type result = metadata.getTypeResolver().heuristicType( typeName, typeParameters );
 		if ( result == null ) {
 			String msg = "Could not determine type for: " + typeName;
 			if ( table != null ) {
 				msg += ", at table: " + table.getName();
 			}
 			if ( columns != null && columns.size() > 0 ) {
 				msg += ", for columns: " + columns;
 			}
 			throw new MappingException( msg );
 		}
 
 		return result;
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
 		// NOTE : this is called as the last piece in setting SimpleValue type information, and implementations
 		// rely on that fact, using it as a signal that all information it is going to get is defined at this point...
 
 		if ( typeName != null ) {
 			// assume either (a) explicit type was specified or (b) determine was already performed
 			return;
 		}
 
 		if ( type != null ) {
 			return;
 		}
 
 		if ( attributeConverterDescriptor == null ) {
 			// this is here to work like legacy.  This should change when we integrate with metamodel to
 			// look for SqlTypeDescriptor and JavaTypeDescriptor individually and create the BasicType (well, really
 			// keep a registry of [SqlTypeDescriptor,JavaTypeDescriptor] -> BasicType...)
 			if ( className == null ) {
 				throw new MappingException( "Attribute types for a dynamic entity must be explicitly specified: " + propertyName );
 			}
 			typeName = ReflectHelper.reflectedPropertyClass( className, propertyName, metadata.getMetadataBuildingOptions().getServiceRegistry().getService( ClassLoaderService.class ) ).getName();
 			// todo : to fully support isNationalized here we need do the process hinted at above
 			// 		essentially, much of the logic from #buildAttributeConverterTypeAdapter wrt resolving
 			//		a (1) SqlTypeDescriptor, a (2) JavaTypeDescriptor and dynamically building a BasicType
 			// 		combining them.
 			return;
 		}
 
 		// we had an AttributeConverter...
 		type = buildAttributeConverterTypeAdapter();
 	}
 
 	/**
 	 * Build a Hibernate Type that incorporates the JPA AttributeConverter.  AttributeConverter works totally in
 	 * memory, meaning it converts between one Java representation (the entity attribute representation) and another
 	 * (the value bound into JDBC statements or extracted from results).  However, the Hibernate Type system operates
 	 * at the lower level of actually dealing directly with those JDBC objects.  So even though we have an
 	 * AttributeConverter, we still need to "fill out" the rest of the BasicType data and bridge calls
 	 * to bind/extract through the converter.
 	 * <p/>
 	 * Essentially the idea here is that an intermediate Java type needs to be used.  Let's use an example as a means
 	 * to illustrate...  Consider an {@code AttributeConverter<Integer,String>}.  This tells Hibernate that the domain
 	 * model defines this attribute as an Integer value (the 'entityAttributeJavaType'), but that we need to treat the
 	 * value as a String (the 'databaseColumnJavaType') when dealing with JDBC (aka, the database type is a
 	 * VARCHAR/CHAR):<ul>
 	 *     <li>
 	 *         When binding values to PreparedStatements we need to convert the Integer value from the entity
 	 *         into a String and pass that String to setString.  The conversion is handled by calling
 	 *         {@link AttributeConverter#convertToDatabaseColumn(Object)}
 	 *     </li>
 	 *     <li>
 	 *         When extracting values from ResultSets (or CallableStatement parameters) we need to handle the
 	 *         value via getString, and convert that returned String to an Integer.  That conversion is handled
 	 *         by calling {@link AttributeConverter#convertToEntityAttribute(Object)}
 	 *     </li>
 	 * </ul>
 	 *
 	 * @return The built AttributeConverter -> Type adapter
 	 *
 	 * @todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
 	 * then we can "play them against each other" in terms of determining proper typing
 	 *
 	 * @todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
 	 */
 	@SuppressWarnings("unchecked")
 	private Type buildAttributeConverterTypeAdapter() {
 		// todo : validate the number of columns present here?
 
 		final Class entityAttributeJavaType = attributeConverterDescriptor.getDomainType();
 		final Class databaseColumnJavaType = attributeConverterDescriptor.getJdbcType();
 
 
 		// resolve the JavaTypeDescriptor ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
 		// the AttributeConverter to resolve the corresponding descriptor.
 		final JavaTypeDescriptor entityAttributeJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
 
 
 		// build the SqlTypeDescriptor adapter ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		// Going back to the illustration, this should be a SqlTypeDescriptor that handles the Integer <-> String
 		//		conversions.  This is the more complicated piece.  First we need to determine the JDBC type code
 		//		corresponding to the AttributeConverter's declared "databaseColumnJavaType" (how we read that value out
 		// 		of ResultSets).  See JdbcTypeJavaClassMappings for details.  Again, given example, this should return
 		// 		VARCHAR/CHAR
 		int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
 		if ( isLob() ) {
 			if ( LobTypeMappings.INSTANCE.hasCorrespondingLobCode( jdbcTypeCode ) ) {
 				jdbcTypeCode = LobTypeMappings.INSTANCE.getCorrespondingLobCode( jdbcTypeCode );
 			}
 			else {
 				if ( Serializable.class.isAssignableFrom( entityAttributeJavaType ) ) {
 					jdbcTypeCode = Types.BLOB;
 				}
 				else {
 					throw new IllegalArgumentException(
 							String.format(
 									Locale.ROOT,
 									"JDBC type-code [%s (%s)] not known to have a corresponding LOB equivalent, and Java type is not Serializable (to use BLOB)",
 									jdbcTypeCode,
 									JdbcTypeNameMapper.getTypeName( jdbcTypeCode )
 							)
 					);
 				}
 			}
 		}
 		if ( isNationalized() ) {
 			jdbcTypeCode = NationalizedTypeMappings.INSTANCE.getCorrespondingNationalizedCode( jdbcTypeCode );
 		}
 		// find the standard SqlTypeDescriptor for that JDBC type code.
 		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
 		// find the JavaTypeDescriptor representing the "intermediate database type representation".  Back to the
 		// 		illustration, this should be the type descriptor for Strings
 		final JavaTypeDescriptor intermediateJavaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( databaseColumnJavaType );
 		// and finally construct the adapter, which injects the AttributeConverter calls into the binding/extraction
 		// 		process...
 		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
 				attributeConverterDescriptor.getAttributeConverter(),
 				sqlTypeDescriptor,
 				intermediateJavaTypeDescriptor
 		);
 
 		// todo : cache the AttributeConverterTypeAdapter in case that AttributeConverter is applied multiple times.
 
 		final String name = AttributeConverterTypeAdapter.NAME_PREFIX + attributeConverterDescriptor.getAttributeConverter().getClass().getName();
 		final String description = String.format(
 				"BasicType adapter for AttributeConverter<%s,%s>",
 				entityAttributeJavaType.getSimpleName(),
 				databaseColumnJavaType.getSimpleName()
 		);
 		return new AttributeConverterTypeAdapter(
 				name,
 				description,
 				attributeConverterDescriptor.getAttributeConverter(),
 				sqlTypeDescriptorAdapter,
 				entityAttributeJavaType,
 				databaseColumnJavaType,
 				entityAttributeJavaTypeDescriptor
 		);
 	}
 
 	public boolean isTypeSpecified() {
 		return typeName!=null;
 	}
 
 	public void setTypeParameters(Properties parameterMap) {
 		this.typeParameters = parameterMap;
 	}
 	
 	public Properties getTypeParameters() {
 		return typeParameters;
 	}
 
 	public void copyTypeFrom( SimpleValue sourceValue ) {
 		setTypeName( sourceValue.getTypeName() );
 		setTypeParameters( sourceValue.getTypeParameters() );
 
 		type = sourceValue.type;
 		attributeConverterDescriptor = sourceValue.attributeConverterDescriptor;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + columns.toString() + ')';
 	}
 
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 	public boolean[] getColumnInsertability() {
 		boolean[] result = new boolean[ getColumnSpan() ];
 		int i = 0;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Selectable s = (Selectable) iter.next();
 			result[i++] = !s.isFormula();
 		}
 		return result;
 	}
 	
 	public boolean[] getColumnUpdateability() {
 		return getColumnInsertability();
 	}
 
 	public void setJpaAttributeConverterDescriptor(AttributeConverterDescriptor attributeConverterDescriptor) {
 		this.attributeConverterDescriptor = attributeConverterDescriptor;
 	}
 
 	private void createParameterImpl() {
 		try {
 			String[] columnsNames = new String[columns.size()];
 			for ( int i = 0; i < columns.size(); i++ ) {
 				Selectable column = columns.get(i);
 				if (column instanceof Column){
 					columnsNames[i] = ((Column) column).getName();
 				}
 			}
 
 			final XProperty xProperty = (XProperty) typeParameters.get( DynamicParameterizedType.XPROPERTY );
 			// todo : not sure this works for handling @MapKeyEnumerated
 			final Annotation[] annotations = xProperty == null
 					? null
 					: xProperty.getAnnotations();
 
 			final ClassLoaderService classLoaderService = getMetadata().getMetadataBuildingOptions()
 					.getServiceRegistry()
 					.getService( ClassLoaderService.class );
 			typeParameters.put(
 					DynamicParameterizedType.PARAMETER_TYPE,
 					new ParameterTypeImpl(
 							classLoaderService.classForName(
 									typeParameters.getProperty( DynamicParameterizedType.RETURNED_CLASS )
 							),
 							annotations,
 							table.getCatalog(),
 							table.getSchema(),
 							table.getName(),
 							Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_PRIMARY_KEY ) ),
 							columnsNames
 					)
 			);
 		}
 		catch ( ClassLoadingException e ) {
 			throw new MappingException( "Could not create DynamicParameterizedType for type: " + typeName, e );
 		}
 	}
 
 	private static final class ParameterTypeImpl implements DynamicParameterizedType.ParameterType {
 
 		private final Class returnedClass;
 		private final Annotation[] annotationsMethod;
 		private final String catalog;
 		private final String schema;
 		private final String table;
 		private final boolean primaryKey;
 		private final String[] columns;
 
 		private ParameterTypeImpl(Class returnedClass, Annotation[] annotationsMethod, String catalog, String schema,
 				String table, boolean primaryKey, String[] columns) {
 			this.returnedClass = returnedClass;
 			this.annotationsMethod = annotationsMethod;
 			this.catalog = catalog;
 			this.schema = schema;
 			this.table = table;
 			this.primaryKey = primaryKey;
 			this.columns = columns;
 		}
 
 		@Override
 		public Class getReturnedClass() {
 			return returnedClass;
 		}
 
 		@Override
 		public Annotation[] getAnnotationsMethod() {
 			return annotationsMethod;
 		}
 
 		@Override
 		public String getCatalog() {
 			return catalog;
 		}
 
 		@Override
 		public String getSchema() {
 			return schema;
 		}
 
 		@Override
 		public String getTable() {
 			return table;
 		}
 
 		@Override
 		public boolean isPrimaryKey() {
 			return primaryKey;
 		}
 
 		@Override
 		public String[] getColumns() {
 			return columns;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
index 803ac23c16..499265e61e 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Table.java
@@ -1,900 +1,902 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Iterator;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.boot.model.naming.Identifier;
 import org.hibernate.boot.model.relational.Exportable;
 import org.hibernate.boot.model.relational.InitCommand;
 import org.hibernate.boot.model.relational.Namespace;
 import org.hibernate.boot.model.relational.QualifiedTableName;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.env.spi.QualifiedObjectNameFormatter;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.tool.hbm2ddl.ColumnMetadata;
 import org.hibernate.tool.hbm2ddl.TableMetadata;
 import org.hibernate.tool.schema.extract.spi.ColumnInformation;
 import org.hibernate.tool.schema.extract.spi.TableInformation;
 
 import org.jboss.logging.Logger;
 
 /**
  * A relational table
  *
  * @author Gavin King
  */
 @SuppressWarnings("unchecked")
 public class Table implements RelationalModel, Serializable, Exportable {
 	private static final Logger log = Logger.getLogger( Table.class );
 
 	private Identifier catalog;
 	private Identifier schema;
 	private Identifier name;
 
 	/**
 	 * contains all columns, including the primary key
 	 */
 	private Map columns = new LinkedHashMap();
 	private KeyValue idValue;
 	private PrimaryKey primaryKey;
 	private Map<ForeignKeyKey, ForeignKey> foreignKeys = new LinkedHashMap<ForeignKeyKey, ForeignKey>();
 	private Map<String, Index> indexes = new LinkedHashMap<String, Index>();
 	private Map<String,UniqueKey> uniqueKeys = new LinkedHashMap<String,UniqueKey>();
 	private int uniqueInteger;
 	private List<String> checkConstraints = new ArrayList<String>();
 	private String rowId;
 	private String subselect;
 	private boolean isAbstract;
 	private boolean hasDenormalizedTables;
 	private String comment;
 
 	private List<InitCommand> initCommands;
 
 	public Table() {
 	}
 
 	public Table(String name) {
 		setName( name );
 	}
 
 	public Table(
 			Namespace namespace,
 			Identifier physicalTableName,
 			boolean isAbstract) {
 		this.catalog = namespace.getPhysicalName().getCatalog();
 		this.schema = namespace.getPhysicalName().getSchema();
 		this.name = physicalTableName;
 		this.isAbstract = isAbstract;
 	}
 
 	public Table(
 			Identifier catalog,
 			Identifier schema,
 			Identifier physicalTableName,
 			boolean isAbstract) {
 		this.catalog = catalog;
 		this.schema = schema;
 		this.name = physicalTableName;
 		this.isAbstract = isAbstract;
 	}
 
 	public Table(Namespace namespace, Identifier physicalTableName, String subselect, boolean isAbstract) {
 		this.catalog = namespace.getPhysicalName().getCatalog();
 		this.schema = namespace.getPhysicalName().getSchema();
 		this.name = physicalTableName;
 		this.subselect = subselect;
 		this.isAbstract = isAbstract;
 	}
 
 	public Table(Namespace namespace, String subselect, boolean isAbstract) {
 		this.catalog = namespace.getPhysicalName().getCatalog();
 		this.schema = namespace.getPhysicalName().getSchema();
 		this.subselect = subselect;
 		this.isAbstract = isAbstract;
 	}
 
 	/**
 	 * @deprecated Should use {@link QualifiedObjectNameFormatter#format} on QualifiedObjectNameFormatter
 	 * obtained from {@link org.hibernate.engine.jdbc.env.spi.JdbcEnvironment}
 	 */
 	@Deprecated
 	public String getQualifiedName(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		if ( subselect != null ) {
 			return "( " + subselect + " )";
 		}
 		String quotedName = getQuotedName( dialect );
 		String usedSchema = schema == null ?
 				defaultSchema :
 				getQuotedSchema( dialect );
 		String usedCatalog = catalog == null ?
 				defaultCatalog :
 				getQuotedCatalog( dialect );
 		return qualify( usedCatalog, usedSchema, quotedName );
 	}
 
 	/**
 	 * @deprecated Should use {@link QualifiedObjectNameFormatter#format} on QualifiedObjectNameFormatter
 	 * obtained from {@link org.hibernate.engine.jdbc.env.spi.JdbcEnvironment}
 	 */
 	@Deprecated
 	public static String qualify(String catalog, String schema, String table) {
 		StringBuilder qualifiedName = new StringBuilder();
 		if ( catalog != null ) {
 			qualifiedName.append( catalog ).append( '.' );
 		}
 		if ( schema != null ) {
 			qualifiedName.append( schema ).append( '.' );
 		}
 		return qualifiedName.append( table ).toString();
 	}
 
 	public void setName(String name) {
 		this.name = Identifier.toIdentifier( name );
 	}
 
 	public String getName() {
 		return name == null ? null : name.getText();
 	}
 
 	public Identifier getNameIdentifier() {
 		return name;
 	}
 
 	public String getQuotedName() {
 		return name == null ? null : name.toString();
 	}
 
 	public String getQuotedName(Dialect dialect) {
 		return name == null ? null : name.render( dialect );
 	}
 
 	public QualifiedTableName getQualifiedTableName() {
 		return name == null ? null : new QualifiedTableName( catalog, schema, name );
 	}
 
 	public boolean isQuoted() {
 		return name.isQuoted();
 	}
 
 	public void setQuoted(boolean quoted) {
 		if ( quoted == name.isQuoted() ) {
 			return;
 		}
 		this.name = new Identifier( name.getText(), quoted );
 	}
 
 	public void setSchema(String schema) {
 		this.schema = Identifier.toIdentifier( schema );
 	}
 
 	public String getSchema() {
 		return schema == null ? null : schema.getText();
 	}
 
 	public String getQuotedSchema() {
 		return schema == null ? null : schema.toString();
 	}
 
 	public String getQuotedSchema(Dialect dialect) {
 		return schema == null ? null : schema.render( dialect );
 	}
 
 	public boolean isSchemaQuoted() {
 		return schema != null && schema.isQuoted();
 	}
 
 	public void setCatalog(String catalog) {
 		this.catalog = Identifier.toIdentifier( catalog );
 	}
 
 	public String getCatalog() {
 		return catalog == null ? null : catalog.getText();
 	}
 
 	public String getQuotedCatalog() {
 		return catalog == null ? null : catalog.render();
 	}
 
 	public String getQuotedCatalog(Dialect dialect) {
 		return catalog == null ? null : catalog.render( dialect );
 	}
 
 	public boolean isCatalogQuoted() {
 		return catalog != null && catalog.isQuoted();
 	}
 
 	/**
 	 * Return the column which is identified by column provided as argument.
 	 *
 	 * @param column column with atleast a name.
 	 * @return the underlying column or null if not inside this table. Note: the instance *can* be different than the input parameter, but the name will be the same.
 	 */
 	public Column getColumn(Column column) {
 		if ( column == null ) {
 			return null;
 		}
 
 		Column myColumn = (Column) columns.get( column.getCanonicalName() );
 
 		return column.equals( myColumn ) ?
 				myColumn :
 				null;
 	}
 
 	public Column getColumn(Identifier name) {
 		if ( name == null ) {
 			return null;
 		}
 
 		return (Column) columns.get( name.getCanonicalName() );
 	}
 
 	public Column getColumn(int n) {
 		Iterator iter = columns.values().iterator();
 		for ( int i = 0; i < n - 1; i++ ) {
 			iter.next();
 		}
 		return (Column) iter.next();
 	}
 
 	public void addColumn(Column column) {
 		Column old = getColumn( column );
 		if ( old == null ) {
 			if ( primaryKey != null ) {
 				for ( Column c : primaryKey.getColumns() ) {
 					if ( c.getCanonicalName().equals( column.getCanonicalName() ) ) {
 						column.setNullable( false );
 						log.debugf(
 								"Forcing column [%s] to be non-null as it is part of the primary key for table [%s]",
 								column.getCanonicalName(),
 								getNameIdentifier().getCanonicalName()
 						);
 					}
 				}
 			}
 			this.columns.put( column.getCanonicalName(), column );
 			column.uniqueInteger = this.columns.size();
 		}
 		else {
 			column.uniqueInteger = old.uniqueInteger;
 		}
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 
 	public Iterator getColumnIterator() {
 		return columns.values().iterator();
 	}
 
 	public Iterator<Index> getIndexIterator() {
 		return indexes.values().iterator();
 	}
 
 	public Iterator getForeignKeyIterator() {
 		return foreignKeys.values().iterator();
 	}
 
 	public Map<ForeignKeyKey, ForeignKey> getForeignKeys() {
 		return Collections.unmodifiableMap( foreignKeys );
 	}
 
 	public Iterator<UniqueKey> getUniqueKeyIterator() {
 		return getUniqueKeys().values().iterator();
 	}
 
 	Map<String, UniqueKey> getUniqueKeys() {
 		cleanseUniqueKeyMapIfNeeded();
 		return uniqueKeys;
 	}
 
 	private int sizeOfUniqueKeyMapOnLastCleanse;
 
 	private void cleanseUniqueKeyMapIfNeeded() {
 		if ( uniqueKeys.size() == sizeOfUniqueKeyMapOnLastCleanse ) {
 			// nothing to do
 			return;
 		}
 		cleanseUniqueKeyMap();
 		sizeOfUniqueKeyMapOnLastCleanse = uniqueKeys.size();
 	}
 
 	private void cleanseUniqueKeyMap() {
 		// We need to account for a few conditions here...
 		// 	1) If there are multiple unique keys contained in the uniqueKeys Map, we need to deduplicate
 		// 		any sharing the same columns as other defined unique keys; this is needed for the annotation
 		// 		processor since it creates unique constraints automagically for the user
 		//	2) Remove any unique keys that share the same columns as the primary key; again, this is
 		//		needed for the annotation processor to handle @Id @OneToOne cases.  In such cases the
 		//		unique key is unnecessary because a primary key is already unique by definition.  We handle
 		//		this case specifically because some databases fail if you try to apply a unique key to
 		//		the primary key columns which causes schema export to fail in these cases.
 		if ( uniqueKeys.isEmpty() ) {
 			// nothing to do
 			return;
 		}
 		else if ( uniqueKeys.size() == 1 ) {
 			// we have to worry about condition 2 above, but not condition 1
 			final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeys.entrySet().iterator().next();
 			if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 				uniqueKeys.remove( uniqueKeyEntry.getKey() );
 			}
 		}
 		else {
 			// we have to check both conditions 1 and 2
 			final Iterator<Map.Entry<String,UniqueKey>> uniqueKeyEntries = uniqueKeys.entrySet().iterator();
 			while ( uniqueKeyEntries.hasNext() ) {
 				final Map.Entry<String,UniqueKey> uniqueKeyEntry = uniqueKeyEntries.next();
 				final UniqueKey uniqueKey = uniqueKeyEntry.getValue();
 				boolean removeIt = false;
 
 				// condition 1 : check against other unique keys
 				for ( UniqueKey otherUniqueKey : uniqueKeys.values() ) {
 					// make sure its not the same unique key
 					if ( uniqueKeyEntry.getValue() == otherUniqueKey ) {
 						continue;
 					}
 					if ( otherUniqueKey.getColumns().containsAll( uniqueKey.getColumns() )
 							&& uniqueKey.getColumns().containsAll( otherUniqueKey.getColumns() ) ) {
 						removeIt = true;
 						break;
 					}
 				}
 
 				// condition 2 : check against pk
 				if ( isSameAsPrimaryKeyColumns( uniqueKeyEntry.getValue() ) ) {
 					removeIt = true;
 				}
 
 				if ( removeIt ) {
 					//uniqueKeys.remove( uniqueKeyEntry.getKey() );
 					uniqueKeyEntries.remove();
 				}
 			}
 
 		}
 	}
 
 	private boolean isSameAsPrimaryKeyColumns(UniqueKey uniqueKey) {
 		if ( primaryKey == null || ! primaryKey.columnIterator().hasNext() ) {
 			// happens for many-to-many tables
 			return false;
 		}
 		return primaryKey.getColumns().containsAll( uniqueKey.getColumns() )
 				&& uniqueKey.getColumns().containsAll( primaryKey.getColumns() );
 	}
 
 	@Override
 	public int hashCode() {
 		final int prime = 31;
 		int result = 1;
 		result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
 		result = prime * result + ((name == null) ? 0 : name.hashCode());
 		result = prime * result + ((schema == null) ? 0 : schema.hashCode());
 		return result;
 	}
 
 	@Override
 	public boolean equals(Object object) {
 		return object instanceof Table && equals((Table) object);
 	}
 
 	public boolean equals(Table table) {
 		if (null == table) {
 			return false;
 		}
 		if (this == table) {
 			return true;
 		}
 
 		return Identifier.areEqual( name, table.name )
 				&& Identifier.areEqual( schema, table.schema )
 				&& Identifier.areEqual( catalog, table.catalog );
 	}
 	
 	public void validateColumns(Dialect dialect, Mapping mapping, TableMetadata tableInfo) {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			ColumnMetadata columnInfo = tableInfo.getColumnMetadata( col.getName() );
 
 			if ( columnInfo == null ) {
 				throw new HibernateException( "Missing column: " + col.getName() + " in " + Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()));
 			}
 			else {
 				final boolean typesMatch = col.getSqlType( dialect, mapping ).toLowerCase(Locale.ROOT)
 						.startsWith( columnInfo.getTypeName().toLowerCase(Locale.ROOT) )
 						|| columnInfo.getTypeCode() == col.getSqlTypeCode( mapping );
 				if ( !typesMatch ) {
 					throw new HibernateException(
 							"Wrong column type in " +
 							Table.qualify( tableInfo.getCatalog(), tableInfo.getSchema(), tableInfo.getName()) +
 							" for column " + col.getName() +
 							". Found: " + columnInfo.getTypeName().toLowerCase(Locale.ROOT) +
 							", expected: " + col.getSqlType( dialect, mapping )
 					);
 				}
 			}
 		}
 
 	}
 
 	public Iterator sqlAlterStrings(
 			Dialect dialect,
 			Mapping p,
 			TableInformation tableInfo,
 			String defaultCatalog,
 			String defaultSchema) throws HibernateException {
 
 		StringBuilder root = new StringBuilder( "alter table " )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( ' ' )
 				.append( dialect.getAddColumnString() );
 
 		Iterator iter = getColumnIterator();
 		List results = new ArrayList();
 		
 		while ( iter.hasNext() ) {
 			final Column column = (Column) iter.next();
 			final ColumnInformation columnInfo = tableInfo.getColumn( Identifier.toIdentifier( column.getName(), column.isQuoted() ) );
 
 			if ( columnInfo == null ) {
 				// the column doesnt exist at all.
 				StringBuilder alter = new StringBuilder( root.toString() )
 						.append( ' ' )
 						.append( column.getQuotedName( dialect ) )
 						.append( ' ' )
 						.append( column.getSqlType( dialect, p ) );
 
 				String defaultValue = column.getDefaultValue();
 				if ( defaultValue != null ) {
 					alter.append( " default " ).append( defaultValue );
 				}
 
 				if ( column.isNullable() ) {
 					alter.append( dialect.getNullColumnString() );
 				}
 				else {
 					alter.append( " not null" );
 				}
 
 				if ( column.isUnique() ) {
 					String keyName = Constraint.generateName( "UK_", this, column );
 					UniqueKey uk = getOrCreateUniqueKey( keyName );
 					uk.addColumn( column );
 					alter.append( dialect.getUniqueDelegate()
 							.getColumnDefinitionUniquenessFragment( column ) );
 				}
 
 				if ( column.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 					alter.append( " check(" )
 							.append( column.getCheckConstraint() )
 							.append( ")" );
 				}
 
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					alter.append( dialect.getColumnComment( columnComment ) );
 				}
 
 				alter.append( dialect.getAddColumnSuffixString() );
 
 				results.add( alter.toString() );
 			}
 
 		}
 
 		if ( results.isEmpty() ) {
 			log.debugf( "No alter strings for table : %s", getQuotedName() );
 		}
 
 		return results.iterator();
 	}
 
 	public boolean hasPrimaryKey() {
 		return getPrimaryKey() != null;
 	}
 
 	public String sqlCreateString(Dialect dialect, Mapping p, String defaultCatalog, String defaultSchema) {
 		StringBuilder buf = new StringBuilder( hasPrimaryKey() ? dialect.getCreateTableString() : dialect.getCreateMultisetTableString() )
 				.append( ' ' )
 				.append( getQualifiedName( dialect, defaultCatalog, defaultSchema ) )
 				.append( " (" );
 
 		boolean identityColumn = idValue != null && idValue.isIdentityColumn( p.getIdentifierGeneratorFactory(), dialect );
 
 		// Try to find out the name of the primary key to create it as identity if the IdentityGenerator is used
 		String pkname = null;
 		if ( hasPrimaryKey() && identityColumn ) {
 			pkname = ( (Column) getPrimaryKey().getColumnIterator().next() ).getQuotedName( dialect );
 		}
 
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Column col = (Column) iter.next();
 
 			buf.append( col.getQuotedName( dialect ) )
 					.append( ' ' );
 
 			if ( identityColumn && col.getQuotedName( dialect ).equals( pkname ) ) {
 				// to support dialects that have their own identity data type
 				if ( dialect.getIdentityColumnSupport().hasDataTypeInIdentityColumn() ) {
 					buf.append( col.getSqlType( dialect, p ) );
 				}
 				buf.append( ' ' )
 						.append( dialect.getIdentityColumnSupport().getIdentityColumnString( col.getSqlTypeCode( p ) ) );
 			}
 			else {
 
 				buf.append( col.getSqlType( dialect, p ) );
 
 				String defaultValue = col.getDefaultValue();
 				if ( defaultValue != null ) {
 					buf.append( " default " ).append( defaultValue );
 				}
 
 				if ( col.isNullable() ) {
 					buf.append( dialect.getNullColumnString() );
 				}
 				else {
 					buf.append( " not null" );
 				}
 
 			}
 			
 			if ( col.isUnique() ) {
 				String keyName = Constraint.generateName( "UK_", this, col );
 				UniqueKey uk = getOrCreateUniqueKey( keyName );
 				uk.addColumn( col );
 				buf.append( dialect.getUniqueDelegate()
 						.getColumnDefinitionUniquenessFragment( col ) );
 			}
 				
 			if ( col.hasCheckConstraint() && dialect.supportsColumnCheck() ) {
 				buf.append( " check (" )
 						.append( col.getCheckConstraint() )
 						.append( ")" );
 			}
 
 			String columnComment = col.getComment();
 			if ( columnComment != null ) {
 				buf.append( dialect.getColumnComment( columnComment ) );
 			}
 
 			if ( iter.hasNext() ) {
 				buf.append( ", " );
 			}
 
 		}
 		if ( hasPrimaryKey() ) {
 			buf.append( ", " )
 					.append( getPrimaryKey().sqlConstraintString( dialect ) );
 		}
 
 		buf.append( dialect.getUniqueDelegate().getTableCreationUniqueConstraintsFragment( this ) );
 
 		if ( dialect.supportsTableCheck() ) {
 			for ( String checkConstraint : checkConstraints ) {
 				buf.append( ", check (" )
 						.append( checkConstraint )
 						.append( ')' );
 			}
 		}
 
 		buf.append( ')' );
 
 		if ( comment != null ) {
 			buf.append( dialect.getTableComment( comment ) );
 		}
 
 		return buf.append( dialect.getTableTypeString() ).toString();
 	}
 
 	public String sqlDropString(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		return dialect.getDropTableString( getQualifiedName( dialect, defaultCatalog, defaultSchema ) );
 	}
 
 	public PrimaryKey getPrimaryKey() {
 		return primaryKey;
 	}
 
 	public void setPrimaryKey(PrimaryKey primaryKey) {
 		this.primaryKey = primaryKey;
 	}
 
 	public Index getOrCreateIndex(String indexName) {
 
 		Index index =  indexes.get( indexName );
 
 		if ( index == null ) {
 			index = new Index();
 			index.setName( indexName );
 			index.setTable( this );
 			indexes.put( indexName, index );
 		}
 
 		return index;
 	}
 
 	public Index getIndex(String indexName) {
 		return  indexes.get( indexName );
 	}
 
 	public Index addIndex(Index index) {
 		Index current =  indexes.get( index.getName() );
 		if ( current != null ) {
 			throw new MappingException( "Index " + index.getName() + " already exists!" );
 		}
 		indexes.put( index.getName(), index );
 		return index;
 	}
 
 	public UniqueKey addUniqueKey(UniqueKey uniqueKey) {
 		UniqueKey current = uniqueKeys.get( uniqueKey.getName() );
 		if ( current != null ) {
 			throw new MappingException( "UniqueKey " + uniqueKey.getName() + " already exists!" );
 		}
 		uniqueKeys.put( uniqueKey.getName(), uniqueKey );
 		return uniqueKey;
 	}
 
 	public UniqueKey createUniqueKey(List keyColumns) {
 		String keyName = Constraint.generateName( "UK_", this, keyColumns );
 		UniqueKey uk = getOrCreateUniqueKey( keyName );
 		uk.addColumns( keyColumns.iterator() );
 		return uk;
 	}
 
 	public UniqueKey getUniqueKey(String keyName) {
 		return uniqueKeys.get( keyName );
 	}
 
 	public UniqueKey getOrCreateUniqueKey(String keyName) {
 		UniqueKey uk = uniqueKeys.get( keyName );
 
 		if ( uk == null ) {
 			uk = new UniqueKey();
 			uk.setName( keyName );
 			uk.setTable( this );
 			uniqueKeys.put( keyName, uk );
 		}
 		return uk;
 	}
 
 	public void createForeignKeys() {
 	}
 
-	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName) {
-		return createForeignKey( keyName, keyColumns, referencedEntityName, null );
+	public ForeignKey createForeignKey(String keyName, List keyColumns, String referencedEntityName, String keyDefinition) {
+		return createForeignKey( keyName, keyColumns, referencedEntityName, keyDefinition, null );
 	}
 
 	public ForeignKey createForeignKey(
 			String keyName,
 			List keyColumns,
 			String referencedEntityName,
+			String keyDefinition,
 			List referencedColumns) {
 		final ForeignKeyKey key = new ForeignKeyKey( keyColumns, referencedEntityName, referencedColumns );
 
 		ForeignKey fk = foreignKeys.get( key );
 		if ( fk == null ) {
 			fk = new ForeignKey();
 			fk.setTable( this );
 			fk.setReferencedEntityName( referencedEntityName );
+			fk.setKeyDefinition(keyDefinition);
 			fk.addColumns( keyColumns.iterator() );
 			if ( referencedColumns != null ) {
 				fk.addReferencedColumns( referencedColumns.iterator() );
 			}
 
 			// NOTE : if the name is null, we will generate an implicit name during second pass processing
 			// afterQuery we know the referenced table name (which might not be resolved yet).
 			fk.setName( keyName );
 
 			foreignKeys.put( key, fk );
 		}
 
 		if ( keyName != null ) {
 			fk.setName( keyName );
 		}
 
 		return fk;
 	}
 
 
 	// This must be done outside of Table, rather than statically, to ensure
 	// deterministic alias names.  See HHH-2448.
 	public void setUniqueInteger( int uniqueInteger ) {
 		this.uniqueInteger = uniqueInteger;
 	}
 
 	public int getUniqueInteger() {
 		return uniqueInteger;
 	}
 
 	public void setIdentifierValue(KeyValue idValue) {
 		this.idValue = idValue;
 	}
 
 	public KeyValue getIdentifierValue() {
 		return idValue;
 	}
 
 	public void addCheckConstraint(String constraint) {
 		checkConstraints.add( constraint );
 	}
 
 	public boolean containsColumn(Column column) {
 		return columns.containsValue( column );
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public String toString() {
 		StringBuilder buf = new StringBuilder().append( getClass().getName() )
 				.append( '(' );
 		if ( getCatalog() != null ) {
 			buf.append( getCatalog() ).append( "." );
 		}
 		if ( getSchema() != null ) {
 			buf.append( getSchema() ).append( "." );
 		}
 		buf.append( getName() ).append( ')' );
 		return buf.toString();
 	}
 
 	public String getSubselect() {
 		return subselect;
 	}
 
 	public void setSubselect(String subselect) {
 		this.subselect = subselect;
 	}
 
 	public boolean isSubselect() {
 		return subselect != null;
 	}
 
 	public boolean isAbstractUnionTable() {
 		return hasDenormalizedTables() && isAbstract;
 	}
 
 	public boolean hasDenormalizedTables() {
 		return hasDenormalizedTables;
 	}
 
 	void setHasDenormalizedTables() {
 		hasDenormalizedTables = true;
 	}
 
 	public void setAbstract(boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public boolean isPhysicalTable() {
 		return !isSubselect() && !isAbstractUnionTable();
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public void setComment(String comment) {
 		this.comment = comment;
 	}
 
 	public Iterator<String> getCheckConstraintsIterator() {
 		return checkConstraints.iterator();
 	}
 
 	public Iterator sqlCommentStrings(Dialect dialect, String defaultCatalog, String defaultSchema) {
 		List comments = new ArrayList();
 		if ( dialect.supportsCommentOn() ) {
 			String tableName = getQualifiedName( dialect, defaultCatalog, defaultSchema );
 			if ( comment != null ) {
 				comments.add( "comment on table " + tableName + " is '" + comment + "'" );
 			}
 			Iterator iter = getColumnIterator();
 			while ( iter.hasNext() ) {
 				Column column = (Column) iter.next();
 				String columnComment = column.getComment();
 				if ( columnComment != null ) {
 					comments.add( "comment on column " + tableName + '.' + column.getQuotedName( dialect ) + " is '" + columnComment + "'" );
 				}
 			}
 		}
 		return comments.iterator();
 	}
 
 	@Override
 	public String getExportIdentifier() {
 		return Table.qualify(
 				render( catalog ),
 				render( schema ),
 				name.render()
 		);
 	}
 
 	private String render(Identifier identifier) {
 		return identifier == null ? null : identifier.render();
 	}
 
 
 	public static class ForeignKeyKey implements Serializable {
 		String referencedClassName;
 		List columns;
 		List referencedColumns;
 
 		ForeignKeyKey(List columns, String referencedClassName, List referencedColumns) {
 			this.referencedClassName = referencedClassName;
 			this.columns = new ArrayList();
 			this.columns.addAll( columns );
 			if ( referencedColumns != null ) {
 				this.referencedColumns = new ArrayList();
 				this.referencedColumns.addAll( referencedColumns );
 			}
 			else {
 				this.referencedColumns = Collections.EMPTY_LIST;
 			}
 		}
 
 		public int hashCode() {
 			return columns.hashCode() + referencedColumns.hashCode();
 		}
 
 		public boolean equals(Object other) {
 			ForeignKeyKey fkk = (ForeignKeyKey) other;
 			return fkk != null && fkk.columns.equals( columns ) && fkk.referencedColumns.equals( referencedColumns );
 		}
 
 		@Override
 		public String toString() {
 			return "ForeignKeyKey{" +
 					"columns=" + StringHelper.join( ",", columns ) +
 					", referencedClassName='" + referencedClassName + '\'' +
 					", referencedColumns=" + StringHelper.join( ",", referencedColumns ) +
 					'}';
 		}
 	}
 
 	public void addInitCommand(InitCommand command) {
 		if ( initCommands == null ) {
 			initCommands = new ArrayList<InitCommand>();
 		}
 		initCommands.add( command );
 	}
 
 	public List<InitCommand> getInitCommands() {
 		if ( initCommands == null ) {
 			return Collections.emptyList();
 		}
 		else {
 			return Collections.unmodifiableList( initCommands );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/StandardForeignKeyExporter.java b/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/StandardForeignKeyExporter.java
index 99babdd899..c48c7d4eb8 100644
--- a/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/StandardForeignKeyExporter.java
+++ b/hibernate-core/src/main/java/org/hibernate/tool/schema/internal/StandardForeignKeyExporter.java
@@ -1,147 +1,152 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.tool.schema.internal;
 
 import java.util.Iterator;
 import java.util.Locale;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.boot.Metadata;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.ForeignKey;
 import org.hibernate.tool.schema.spi.Exporter;
 
 /**
  * @author Steve Ebersole
  */
 public class StandardForeignKeyExporter implements Exporter<ForeignKey> {
 	private static final String  COLUMN_MISMATCH_MSG = "Number of referencing columns [%s] did not " +
 			"match number of referenced columns [%s] in foreign-key [%s] from [%s] to [%s]";
 
 	private final Dialect dialect;
 
 	public StandardForeignKeyExporter(Dialect dialect) {
 		this.dialect = dialect;
 	}
 
 	@Override
 	public String[] getSqlCreateStrings(ForeignKey foreignKey, Metadata metadata) {
 		if ( ! dialect.hasAlterTable() ) {
 			return NO_COMMANDS;
 		}
 		
 		if ( ! foreignKey.isCreationEnabled() ) {
 			return NO_COMMANDS;
 		}
 
 		if ( !foreignKey.isPhysicalConstraint() ) {
 			return NO_COMMANDS;
 		}
 
 		final int numberOfColumns = foreignKey.getColumnSpan();
 		final String[] columnNames = new String[ numberOfColumns ];
 		final String[] targetColumnNames = new String[ numberOfColumns ];
 
 		final Iterator targetItr;
 		if ( foreignKey.isReferenceToPrimaryKey() ) {
 			if ( numberOfColumns != foreignKey.getReferencedTable().getPrimaryKey().getColumnSpan() ) {
 				throw new AssertionFailure(
 						String.format(
 								Locale.ENGLISH,
 								COLUMN_MISMATCH_MSG,
 								numberOfColumns,
 								foreignKey.getReferencedTable().getPrimaryKey().getColumnSpan(),
 								foreignKey.getName(),
 								foreignKey.getTable().getName(),
 								foreignKey.getReferencedTable().getName()
 						)
 				);
 			}
 			targetItr = foreignKey.getReferencedTable().getPrimaryKey().getColumnIterator();
 		}
 		else {
 			if ( numberOfColumns != foreignKey.getReferencedColumns().size() ) {
 				throw new AssertionFailure(
 						String.format(
 								Locale.ENGLISH,
 								COLUMN_MISMATCH_MSG,
 								numberOfColumns,
 								foreignKey.getReferencedColumns().size(),
 								foreignKey.getName(),
 								foreignKey.getTable().getName(),
 								foreignKey.getReferencedTable().getName()
 						)
 				);
 			}
 			targetItr = foreignKey.getReferencedColumns().iterator();
 		}
 
 		int i = 0;
 		final Iterator itr = foreignKey.getColumnIterator();
 		while ( itr.hasNext() ) {
 			columnNames[i] = ( (Column) itr.next() ).getQuotedName( dialect );
 			targetColumnNames[i] = ( (Column) targetItr.next() ).getQuotedName( dialect );
 			i++;
 		}
 
 		final JdbcEnvironment jdbcEnvironment = metadata.getDatabase().getJdbcEnvironment();
 		final String sourceTableName = jdbcEnvironment.getQualifiedObjectNameFormatter().format(
 				foreignKey.getTable().getQualifiedTableName(),
 				dialect
 		);
 		final String targetTableName = jdbcEnvironment.getQualifiedObjectNameFormatter().format(
 				foreignKey.getReferencedTable().getQualifiedTableName(),
 				dialect
 		);
 
 		final StringBuilder buffer = new StringBuilder( "alter table " )
 				.append( sourceTableName )
 				.append(
-						dialect.getAddForeignKeyConstraintString(
-								foreignKey.getName(),
-								columnNames,
-								targetTableName,
-								targetColumnNames,
-								foreignKey.isReferenceToPrimaryKey()
-						)
+						foreignKey.getKeyDefinition() != null ?
+								dialect.getAddForeignKeyConstraintString(
+										foreignKey.getName(),
+										foreignKey.getKeyDefinition()
+								) :
+								dialect.getAddForeignKeyConstraintString(
+										foreignKey.getName(),
+										columnNames,
+										targetTableName,
+										targetColumnNames,
+										foreignKey.isReferenceToPrimaryKey()
+								)
 				);
 
 		if ( dialect.supportsCascadeDelete() ) {
 			if ( foreignKey.isCascadeDeleteEnabled() ) {
 				buffer.append( " on delete cascade" );
 			}
 		}
 
 		return new String[] { buffer.toString() };
 	}
 
 	@Override
 	public String[] getSqlDropStrings(ForeignKey foreignKey, Metadata metadata) {
 		if ( ! dialect.hasAlterTable() ) {
 			return NO_COMMANDS;
 		}
 
 		if ( ! foreignKey.isCreationEnabled() ) {
 			return NO_COMMANDS;
 		}
 
 		if ( !foreignKey.isPhysicalConstraint() ) {
 			return NO_COMMANDS;
 		}
 
 		final JdbcEnvironment jdbcEnvironment = metadata.getDatabase().getJdbcEnvironment();
 		final String sourceTableName = jdbcEnvironment.getQualifiedObjectNameFormatter().format(
 				foreignKey.getTable().getQualifiedTableName(),
 				dialect
 		);
 		return new String[] {
 				"alter table " + sourceTableName + dialect.getDropForeignKeyString() + foreignKey.getName()
 		};
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/ForeignKeyMigrationTest.java b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/ForeignKeyMigrationTest.java
similarity index 97%
rename from hibernate-core/src/test/java/org/hibernate/test/schemaupdate/ForeignKeyMigrationTest.java
rename to hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/ForeignKeyMigrationTest.java
index ccde09faa4..0418004626 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/ForeignKeyMigrationTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/ForeignKeyMigrationTest.java
@@ -1,80 +1,80 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
-package org.hibernate.test.schemaupdate;
+package org.hibernate.test.schemaupdate.foreignkeys;
 
 import java.util.EnumSet;
 import javax.persistence.Entity;
 import javax.persistence.Id;
 import javax.persistence.JoinColumn;
 import javax.persistence.ManyToOne;
 import javax.persistence.Table;
 
 import org.hibernate.boot.MetadataSources;
 import org.hibernate.boot.registry.StandardServiceRegistry;
 import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.schema.TargetType;
 
 import org.hibernate.testing.DialectChecks;
 import org.hibernate.testing.RequiresDialectFeature;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.junit.Test;
 
 /**
  * @author Steve Ebersole
  */
 @RequiresDialectFeature( value = {DialectChecks.SupportCatalogCreation.class})
 public class ForeignKeyMigrationTest extends BaseUnitTestCase {
 	@Test
 	@TestForIssue( jiraKey = "HHH-9716" )
 //	@FailureExpected( jiraKey = "HHH-9716" )
 	public void testMigrationOfForeignKeys() {
 		StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
 		try {
 			final MetadataImplementor metadata = (MetadataImplementor) new MetadataSources( ssr )
 					.addAnnotatedClass( Box.class )
 					.addAnnotatedClass( Thing.class )
 					.buildMetadata();
 			metadata.validate();
 
 			// first create the schema...
 			new SchemaExport().create( EnumSet.of( TargetType.DATABASE ), metadata );
 
 			try {
 				// try to update the just created schema
 				new SchemaUpdate().execute( EnumSet.of( TargetType.DATABASE ), metadata );
 			}
 			finally {
 				// clean up
 				new SchemaExport().drop( EnumSet.of( TargetType.DATABASE ), metadata );
 			}
 		}
 		finally {
 			StandardServiceRegistryBuilder.destroy( ssr );
 		}
 	}
 
 	@Entity(name = "Box")
 	@Table(name = "Box", schema = "PUBLIC", catalog = "DB1")
 	public static class Box {
 		@Id
 		public Integer id;
 		@ManyToOne
 		@JoinColumn
 		public Thing thing1;
 	}
 
 	@Entity(name = "Thing")
 	@Table(name = "Thing", schema = "PUBLIC", catalog = "DB1")
 	public static class Thing {
 		@Id
 		public Integer id;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/AbstractForeignKeyDefinitionTest.java b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/AbstractForeignKeyDefinitionTest.java
new file mode 100644
index 0000000000..cc6f8c87ee
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/AbstractForeignKeyDefinitionTest.java
@@ -0,0 +1,81 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.schemaupdate.foreignkeys.definition;
+
+import java.io.File;
+import java.io.IOException;
+import java.nio.file.Files;
+import java.util.EnumSet;
+
+import org.hibernate.boot.MetadataSources;
+import org.hibernate.boot.registry.StandardServiceRegistry;
+import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
+import org.hibernate.boot.spi.MetadataImplementor;
+import org.hibernate.tool.hbm2ddl.SchemaExport;
+import org.hibernate.tool.schema.TargetType;
+
+import org.hibernate.testing.TestForIssue;
+import org.hibernate.testing.junit4.BaseUnitTestCase;
+import org.junit.After;
+import org.junit.Before;
+import org.junit.Test;
+
+import static org.junit.Assert.assertTrue;
+
+/**
+ * @author Vlad MIhalcea
+ */
+
+public abstract class AbstractForeignKeyDefinitionTest extends BaseUnitTestCase {
+
+	private File output;
+
+	private StandardServiceRegistry ssr;
+
+	private MetadataImplementor metadata;
+
+	@Before
+	public void setUp() throws IOException {
+		output = File.createTempFile( "update_script", ".sql" );
+		output.deleteOnExit();
+		ssr = new StandardServiceRegistryBuilder().build();
+		createSchema();
+	}
+
+	@After
+	public void tearsDown() {
+		StandardServiceRegistryBuilder.destroy( ssr );
+	}
+
+	private void createSchema() {
+		final MetadataSources metadataSources = new MetadataSources( ssr );
+
+		for ( Class c : getAnnotatedClasses() ) {
+			metadataSources.addAnnotatedClass( c );
+		}
+		metadata = (MetadataImplementor) metadataSources.buildMetadata();
+		metadata.validate();
+		new SchemaExport()
+				.setHaltOnError( true )
+				.setOutputFile( output.getAbsolutePath() )
+				.setFormat( false )
+				.create( EnumSet.of( TargetType.SCRIPT ), metadata );
+	}
+
+	protected abstract Class<?>[] getAnnotatedClasses();
+
+	protected abstract boolean validate(String fileContent);
+
+	@Test
+	@TestForIssue(jiraKey = "HHH-10643")
+	public void testForeignKeyDefinitionOverridesDefaultNamingStrategy()
+			throws Exception {
+		String fileContent = new String( Files.readAllBytes( output.toPath() ) );
+		assertTrue( "Script file : " + fileContent, validate( fileContent ) );
+	}
+
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/ForeignKeyDefinitionManyToOneTest.java b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/ForeignKeyDefinitionManyToOneTest.java
new file mode 100644
index 0000000000..f331424e27
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/ForeignKeyDefinitionManyToOneTest.java
@@ -0,0 +1,54 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.schemaupdate.foreignkeys.definition;
+
+import javax.persistence.Entity;
+import javax.persistence.ForeignKey;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.ManyToOne;
+
+import org.hibernate.dialect.H2Dialect;
+
+import org.hibernate.testing.RequiresDialect;
+
+/**
+ * @author Vlad Mihalcea
+ */
+@RequiresDialect(value = H2Dialect.class)
+public class ForeignKeyDefinitionManyToOneTest
+		extends AbstractForeignKeyDefinitionTest {
+
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class<?>[] {
+				Box.class,
+				Thing.class,
+		};
+	}
+
+	@Entity(name = "Box")
+	public static class Box {
+		@Id
+		public Integer id;
+
+		@ManyToOne
+		@JoinColumn(foreignKey = @ForeignKey(name = "thingy", foreignKeyDefinition = "foreign key /* FK */ (thing_id) references Thing"))
+		public Thing thing;
+	}
+
+	@Entity(name = "Thing")
+	public static class Thing {
+		@Id
+		public Integer id;
+	}
+
+	@Override
+	protected boolean validate(String fileContent) {
+		return fileContent.contains( "/* FK */" );
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/ForeignKeyDefinitionOneToManyJoinTableTest.java b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/ForeignKeyDefinitionOneToManyJoinTableTest.java
new file mode 100644
index 0000000000..0a8255eb0d
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/ForeignKeyDefinitionOneToManyJoinTableTest.java
@@ -0,0 +1,69 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.schemaupdate.foreignkeys.definition;
+
+import java.util.ArrayList;
+import java.util.List;
+import javax.persistence.Entity;
+import javax.persistence.ForeignKey;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.JoinTable;
+import javax.persistence.OneToMany;
+
+import org.hibernate.dialect.H2Dialect;
+
+import org.hibernate.testing.RequiresDialect;
+
+/**
+ * @author Vlad Mihalcea
+ */
+@RequiresDialect(value = H2Dialect.class)
+public class ForeignKeyDefinitionOneToManyJoinTableTest
+		extends AbstractForeignKeyDefinitionTest {
+
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class<?>[] {
+				Box.class,
+				Thing.class,
+		};
+	}
+
+	@Entity(name = "Box")
+	public static class Box {
+		@Id
+		public Integer id;
+	}
+
+	@Entity(name = "Thing")
+	public static class Thing {
+		@Id
+		public Integer id;
+
+		@OneToMany
+		@JoinTable(name = "box_thing",
+				joinColumns = @JoinColumn(name = "box_id"),
+				inverseJoinColumns = @JoinColumn(name = "thing_id"),
+				foreignKey = @ForeignKey(
+						name = "thingy",
+						foreignKeyDefinition = "foreign key /* Thing_FK */ (thing_id) references Thing"
+				),
+				inverseForeignKey = @ForeignKey(
+						name = "boxy",
+						foreignKeyDefinition = "foreign key /* Box_FK */ (box_id) references Box"
+				)
+		)
+		public List<Thing> things = new ArrayList<>();
+	}
+
+	@Override
+	protected boolean validate(String fileContent) {
+		return fileContent.contains( "/* Thing_FK */" ) && fileContent.contains(
+				"/* Box_FK */" );
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/ForeignKeyDefinitionOneToOneTest.java b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/ForeignKeyDefinitionOneToOneTest.java
new file mode 100644
index 0000000000..6d44816437
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/ForeignKeyDefinitionOneToOneTest.java
@@ -0,0 +1,54 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.schemaupdate.foreignkeys.definition;
+
+import javax.persistence.Entity;
+import javax.persistence.ForeignKey;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+import javax.persistence.OneToOne;
+
+import org.hibernate.dialect.H2Dialect;
+
+import org.hibernate.testing.RequiresDialect;
+
+/**
+ * @author Vlad Mihalcea
+ */
+@RequiresDialect(value = H2Dialect.class)
+public class ForeignKeyDefinitionOneToOneTest
+		extends AbstractForeignKeyDefinitionTest {
+
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class<?>[] {
+				Box.class,
+				Thing.class,
+		};
+	}
+
+	@Entity(name = "Box")
+	public static class Box {
+		@Id
+		public Integer id;
+
+		@OneToOne
+		@JoinColumn(foreignKey = @ForeignKey(name = "thingy", foreignKeyDefinition = "foreign key /* FK */ (thing_id) references Thing"))
+		public Thing thing;
+	}
+
+	@Entity(name = "Thing")
+	public static class Thing {
+		@Id
+		public Integer id;
+	}
+
+	@Override
+	protected boolean validate(String fileContent) {
+		return fileContent.contains( "/* FK */" );
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/ForeignKeyDefinitionSecondaryTableTest.java b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/ForeignKeyDefinitionSecondaryTableTest.java
new file mode 100644
index 0000000000..3685174361
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/schemaupdate/foreignkeys/definition/ForeignKeyDefinitionSecondaryTableTest.java
@@ -0,0 +1,56 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
+ * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
+ */
+package org.hibernate.test.schemaupdate.foreignkeys.definition;
+
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.ForeignKey;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.SecondaryTable;
+
+import org.hibernate.dialect.H2Dialect;
+
+import org.hibernate.testing.RequiresDialect;
+
+/**
+ * @author Vlad Mihalcea
+ */
+@RequiresDialect(value = H2Dialect.class)
+public class ForeignKeyDefinitionSecondaryTableTest
+		extends AbstractForeignKeyDefinitionTest {
+
+	@Override
+	protected Class<?>[] getAnnotatedClasses() {
+		return new Class<?>[] {
+				User.class,
+		};
+	}
+
+	@Entity(name = "Users")
+	@SecondaryTable(name = "User_details", foreignKey = @ForeignKey(name = "secondary", foreignKeyDefinition = "foreign key /* FK */ (id) references Users"))
+	public class User {
+
+		@Id
+		@GeneratedValue
+		private int id;
+
+		private String emailAddress;
+
+		@Column(name = "SECURITY_USERNAME", table = "User_details")
+		private String username;
+
+		@Column(name = "SECURITY_PASSWORD", table = "User_details")
+		private String password;
+	}
+
+
+	@Override
+	protected boolean validate(String fileContent) {
+		return fileContent.contains( "/* FK */" );
+	}
+}
