diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
index cc6802ff51..bdd7c68499 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/AnnotationBinder.java
@@ -1499,1571 +1499,1572 @@ public final class AnnotationBinder {
 						"@IdClass class should not have @Version property"
 				);
 			}
 			if ( !( propertyHolder.getPersistentClass() instanceof RootClass ) ) {
 				throw new AnnotationException(
 						"Unable to define/override @Version on a subclass: "
 								+ propertyHolder.getEntityName()
 				);
 			}
 			if ( !propertyHolder.isEntity() ) {
 				throw new AnnotationException(
 						"Unable to define @Version on an embedded class: "
 								+ propertyHolder.getEntityName()
 				);
 			}
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "{0} is a version property", inferredData.getPropertyName() );
 			}
 			RootClass rootClass = ( RootClass ) propertyHolder.getPersistentClass();
 			propertyBinder.setColumns( columns );
 			Property prop = propertyBinder.makePropertyValueAndBind();
 			setVersionInformation( property, propertyBinder );
 			rootClass.setVersion( prop );
 
 			//If version is on a mapped superclass, update the mapping
 			final org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(
 					inferredData.getDeclaringClass(),
 					inheritanceStatePerClass,
 					mappings
 			);
 			if ( superclass != null ) {
 				superclass.setDeclaredVersion( prop );
 			}
 			else {
 				//we know the property is on the actual entity
 				rootClass.setDeclaredVersion( prop );
 			}
 
 			SimpleValue simpleValue = ( SimpleValue ) prop.getValue();
 			simpleValue.setNullValue( "undefined" );
 			rootClass.setOptimisticLockMode( Versioning.OPTIMISTIC_LOCK_VERSION );
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracev( "Version name: {0}, unsavedValue: {1}", rootClass.getVersion().getName(),
 						( (SimpleValue) rootClass.getVersion().getValue() ).getNullValue() );
 			}
 		}
 		else {
 			final boolean forcePersist = property.isAnnotationPresent( MapsId.class )
 					|| property.isAnnotationPresent( Id.class );
 			if ( property.isAnnotationPresent( ManyToOne.class ) ) {
 				ManyToOne ann = property.getAnnotation( ManyToOne.class );
 
 				//check validity
 				if ( property.isAnnotationPresent( Column.class )
 						|| property.isAnnotationPresent( Columns.class ) ) {
 					throw new AnnotationException(
 							"@Column(s) not allowed on a @ManyToOne property: "
 									+ BinderHelper.getPath( propertyHolder, inferredData )
 					);
 				}
 
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				NotFound notFound = property.getAnnotation( NotFound.class );
 				boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				JoinTable assocTable = propertyHolder.getJoinTable( property );
 				if ( assocTable != null ) {
 					Join join = propertyHolder.addJoin( assocTable, false );
 					for ( Ejb3JoinColumn joinColumn : joinColumns ) {
 						joinColumn.setSecondaryTableName( join.getTable().getName() );
 					}
 				}
 				final boolean mandatory = !ann.optional() || forcePersist;
 				bindManyToOne(
 						getCascadeStrategy( ann.cascade(), hibernateCascade, false, forcePersist ),
 						joinColumns,
 						!mandatory,
 						ignoreNotFound, onDeleteCascade,
 						ToOneBinder.getTargetEntity( inferredData, mappings ),
 						propertyHolder,
 						inferredData, false, isIdentifierMapper,
 						inSecondPass, propertyBinder, mappings
 				);
 			}
 			else if ( property.isAnnotationPresent( OneToOne.class ) ) {
 				OneToOne ann = property.getAnnotation( OneToOne.class );
 
 				//check validity
 				if ( property.isAnnotationPresent( Column.class )
 						|| property.isAnnotationPresent( Columns.class ) ) {
 					throw new AnnotationException(
 							"@Column(s) not allowed on a @OneToOne property: "
 									+ BinderHelper.getPath( propertyHolder, inferredData )
 					);
 				}
 
 				//FIXME support a proper PKJCs
 				boolean trueOneToOne = property.isAnnotationPresent( PrimaryKeyJoinColumn.class )
 						|| property.isAnnotationPresent( PrimaryKeyJoinColumns.class );
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				NotFound notFound = property.getAnnotation( NotFound.class );
 				boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				JoinTable assocTable = propertyHolder.getJoinTable( property );
 				if ( assocTable != null ) {
 					Join join = propertyHolder.addJoin( assocTable, false );
 					for ( Ejb3JoinColumn joinColumn : joinColumns ) {
 						joinColumn.setSecondaryTableName( join.getTable().getName() );
 					}
 				}
 				//MapsId means the columns belong to the pk => not null
 				//@OneToOne with @PKJC can still be optional
 				final boolean mandatory = !ann.optional() || forcePersist;
 				bindOneToOne(
 						getCascadeStrategy( ann.cascade(), hibernateCascade, ann.orphanRemoval(), forcePersist ),
 						joinColumns,
 						!mandatory,
 						getFetchMode( ann.fetch() ),
 						ignoreNotFound, onDeleteCascade,
 						ToOneBinder.getTargetEntity( inferredData, mappings ),
 						propertyHolder,
 						inferredData,
 						ann.mappedBy(),
 						trueOneToOne,
 						isIdentifierMapper,
 						inSecondPass,
 						propertyBinder,
 						mappings
 				);
 			}
 			else if ( property.isAnnotationPresent( org.hibernate.annotations.Any.class ) ) {
 
 				//check validity
 				if ( property.isAnnotationPresent( Column.class )
 						|| property.isAnnotationPresent( Columns.class ) ) {
 					throw new AnnotationException(
 							"@Column(s) not allowed on a @Any property: "
 									+ BinderHelper.getPath( propertyHolder, inferredData )
 					);
 				}
 
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				JoinTable assocTable = propertyHolder.getJoinTable( property );
 				if ( assocTable != null ) {
 					Join join = propertyHolder.addJoin( assocTable, false );
 					for ( Ejb3JoinColumn joinColumn : joinColumns ) {
 						joinColumn.setSecondaryTableName( join.getTable().getName() );
 					}
 				}
 				bindAny(
 						getCascadeStrategy( null, hibernateCascade, false, forcePersist ),
 						//@Any has not cascade attribute
 						joinColumns,
 						onDeleteCascade,
 						nullability,
 						propertyHolder,
 						inferredData,
 						entityBinder,
 						isIdentifierMapper,
 						mappings
 				);
 			}
 			else if ( property.isAnnotationPresent( OneToMany.class )
 					|| property.isAnnotationPresent( ManyToMany.class )
 					|| property.isAnnotationPresent( ElementCollection.class )
 					|| property.isAnnotationPresent( ManyToAny.class ) ) {
 				OneToMany oneToManyAnn = property.getAnnotation( OneToMany.class );
 				ManyToMany manyToManyAnn = property.getAnnotation( ManyToMany.class );
 				ElementCollection elementCollectionAnn = property.getAnnotation( ElementCollection.class );
 
 				final IndexColumn indexColumn;
 
 				if ( property.isAnnotationPresent( OrderColumn.class ) ) {
 					indexColumn = IndexColumn.buildColumnFromAnnotation(
 							property.getAnnotation( OrderColumn.class ),
 							propertyHolder,
 							inferredData,
 							entityBinder.getSecondaryTables(),
 							mappings
 					);
 				}
 				else {
 					//if @IndexColumn is not there, the generated IndexColumn is an implicit column and not used.
 					//so we can leave the legacy processing as the default
 					indexColumn = IndexColumn.buildColumnFromAnnotation(
 							property.getAnnotation( org.hibernate.annotations.IndexColumn.class ),
 							propertyHolder,
 							inferredData,
 							mappings
 					);
 				}
 				CollectionBinder collectionBinder = CollectionBinder.getCollectionBinder(
 						propertyHolder.getEntityName(),
 						property,
 						!indexColumn.isImplicit(),
 						property.isAnnotationPresent( MapKeyType.class ),
 						mappings
 				);
 				collectionBinder.setIndexColumn( indexColumn );
 				collectionBinder.setMapKey( property.getAnnotation( MapKey.class ) );
 				collectionBinder.setPropertyName( inferredData.getPropertyName() );
 				BatchSize batchAnn = property.getAnnotation( BatchSize.class );
 				collectionBinder.setBatchSize( batchAnn );
 				javax.persistence.OrderBy ejb3OrderByAnn = property.getAnnotation( javax.persistence.OrderBy.class );
 				OrderBy orderByAnn = property.getAnnotation( OrderBy.class );
 				collectionBinder.setEjb3OrderBy( ejb3OrderByAnn );
 				collectionBinder.setSqlOrderBy( orderByAnn );
 				Sort sortAnn = property.getAnnotation( Sort.class );
 				collectionBinder.setSort( sortAnn );
 				Cache cachAnn = property.getAnnotation( Cache.class );
 				collectionBinder.setCache( cachAnn );
 				collectionBinder.setPropertyHolder( propertyHolder );
 				Cascade hibernateCascade = property.getAnnotation( Cascade.class );
 				NotFound notFound = property.getAnnotation( NotFound.class );
 				boolean ignoreNotFound = notFound != null && notFound.action().equals( NotFoundAction.IGNORE );
 				collectionBinder.setIgnoreNotFound( ignoreNotFound );
 				collectionBinder.setCollectionType( inferredData.getProperty().getElementClass() );
 				collectionBinder.setMappings( mappings );
 				collectionBinder.setAccessType( inferredData.getDefaultAccess() );
 
 				Ejb3Column[] elementColumns;
 				//do not use "element" if you are a JPA 2 @ElementCollection only for legacy Hibernate mappings
 				boolean isJPA2ForValueMapping = property.isAnnotationPresent( ElementCollection.class );
 				PropertyData virtualProperty = isJPA2ForValueMapping ? inferredData : new WrappedInferredData(
 						inferredData, "element"
 				);
 				if ( property.isAnnotationPresent( Column.class ) || property.isAnnotationPresent(
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
 							mappings
 					);
 				}
 				else if ( property.isAnnotationPresent( Columns.class ) ) {
 					Columns anns = property.getAnnotation( Columns.class );
 					elementColumns = Ejb3Column.buildColumnFromAnnotation(
 							anns.columns(), null, nullability, propertyHolder, virtualProperty,
 							entityBinder.getSecondaryTables(), mappings
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
 							mappings
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
 							mappings
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
 							mappings
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
 							mappings.getReflectionManager().toXClass( oneToManyAnn.targetEntity() )
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
 							mappings.getReflectionManager().toXClass( targetElement )
 					);
 					//collectionBinder.setCascadeStrategy( getCascadeStrategy( embeddedCollectionAnn.cascade(), hibernateCascade ) );
 					collectionBinder.setOneToMany( true );
 				}
 				else if ( manyToManyAnn != null ) {
 					mappedBy = manyToManyAnn.mappedBy();
 					collectionBinder.setTargetEntity(
 							mappings.getReflectionManager().toXClass( manyToManyAnn.targetEntity() )
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
 							mappings.getReflectionManager().toXClass( void.class )
 					);
 					collectionBinder.setCascadeStrategy( getCascadeStrategy( null, hibernateCascade, false, false ) );
 					collectionBinder.setOneToMany( false );
 				}
 				collectionBinder.setMappedBy( mappedBy );
 
 				bindJoinedTableAssociation(
 						property, mappings, entityBinder, collectionBinder, propertyHolder, inferredData, mappedBy
 				);
 
 				OnDelete onDeleteAnn = property.getAnnotation( OnDelete.class );
 				boolean onDeleteCascade = onDeleteAnn != null && OnDeleteAction.CASCADE.equals( onDeleteAnn.action() );
 				collectionBinder.setCascadeDeleteEnabled( onDeleteCascade );
 				if ( isIdentifierMapper ) {
 					collectionBinder.setInsertable( false );
 					collectionBinder.setUpdatable( false );
 				}
 				if ( property.isAnnotationPresent( CollectionId.class ) ) { //do not compute the generators unless necessary
 					HashMap<String, IdGenerator> localGenerators = ( HashMap<String, IdGenerator> ) classGenerators.clone();
 					localGenerators.putAll( buildLocalGenerators( property, mappings ) );
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
 							isId, propertyHolder, property.getName(), mappings
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
 								isId, propertyHolder, property.getName(), mappings
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
 							mappings,
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
 							col.forceNotNull();
 						}
 					}
 
 					propertyBinder.setLazy( lazy );
 					propertyBinder.setColumns( columns );
 					if ( isOverridden ) {
 						final PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 								isId, propertyHolder, property.getName(), mappings
 						);
 						propertyBinder.setReferencedEntityName( mapsIdProperty.getClassOrElementName() );
 					}
 
 					propertyBinder.makePropertyValueAndBind();
 
 				}
 				if ( isOverridden ) {
 					final PropertyData mapsIdProperty = BinderHelper.getPropertyOverriddenByMapperOrMapsId(
 							isId, propertyHolder, property.getName(), mappings
 					);
 					Map<String, IdGenerator> localGenerators = ( HashMap<String, IdGenerator> ) classGenerators.clone();
 					final IdGenerator foreignGenerator = new IdGenerator();
 					foreignGenerator.setIdentifierGeneratorStrategy( "assigned" );
 					foreignGenerator.setName( "Hibernate-local--foreign generator" );
 					foreignGenerator.setIdentifierGeneratorStrategy( "foreign" );
 					foreignGenerator.addParam( "property", mapsIdProperty.getPropertyName() );
 					localGenerators.put( foreignGenerator.getName(), foreignGenerator );
 
 					BinderHelper.makeIdGenerator(
 							( SimpleValue ) propertyBinder.getValue(),
 							foreignGenerator.getIdentifierGeneratorStrategy(),
 							foreignGenerator.getName(),
 							mappings,
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
 								mappings
 						);
 					}
 				}
 			}
 		}
 		//init index
 		//process indexes after everything: in second pass, many to one has to be done before indexes
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
 
 		NaturalId naturalIdAnn = property.getAnnotation( NaturalId.class );
 		if ( naturalIdAnn != null ) {
 			if ( joinColumns != null ) {
 				for ( Ejb3Column column : joinColumns ) {
 					column.addUniqueKey( "_UniqueKey", inSecondPass );
 				}
 			}
 			else {
 				for ( Ejb3Column column : columns ) {
 					column.addUniqueKey( "_UniqueKey", inSecondPass );
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
 			HashMap<String, IdGenerator> classGenerators,
 			boolean isIdentifierMapper,
 			Mappings mappings) {
 		if ( isIdentifierMapper ) {
 			throw new AnnotationException(
 					"@IdClass class should not have @Id nor @EmbeddedId properties: "
 							+ BinderHelper.getPath( propertyHolder, inferredData )
 			);
 		}
 		XClass returnedClass = inferredData.getClassOrElement();
 		XProperty property = inferredData.getProperty();
 		//clone classGenerator and override with local values
 		HashMap<String, IdGenerator> localGenerators = ( HashMap<String, IdGenerator> ) classGenerators.clone();
 		localGenerators.putAll( buildLocalGenerators( property, mappings ) );
 
 		//manage composite related metadata
 		//guess if its a component and find id data access (property, field etc)
 		final boolean isComponent = returnedClass.isAnnotationPresent( Embeddable.class )
 				|| property.isAnnotationPresent( EmbeddedId.class );
 
 		GeneratedValue generatedValue = property.getAnnotation( GeneratedValue.class );
 		String generatorType = generatedValue != null ?
 				generatorType( generatedValue.strategy(), mappings ) :
 				"assigned";
 		String generatorName = generatedValue != null ?
 				generatedValue.generator() :
 				BinderHelper.ANNOTATION_STRING_DEFAULT;
 		if ( isComponent ) {
 			generatorType = "assigned";
 		} //a component must not have any generator
 		BinderHelper.makeIdGenerator( idValue, generatorType, generatorName, mappings, localGenerators );
 
 		if ( LOG.isTraceEnabled() ) {
 			LOG.tracev( "Bind {0} on {1}", ( isComponent ? "@EmbeddedId" : "@Id" ), inferredData.getPropertyName() );
 		}
 	}
 
 	//TODO move that to collection binder?
 
 	private static void bindJoinedTableAssociation(
 			XProperty property,
 			Mappings mappings,
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
 
 			//JPA 2 has priority
 			if ( collectionTable != null ) {
 				catalog = collectionTable.catalog();
 				schema = collectionTable.schema();
 				tableName = collectionTable.name();
 				uniqueConstraints = collectionTable.uniqueConstraints();
 				joins = collectionTable.joinColumns();
 				inverseJoins = null;
 			}
 			else {
 				catalog = assocTable.catalog();
 				schema = assocTable.schema();
 				tableName = assocTable.name();
 				uniqueConstraints = assocTable.uniqueConstraints();
 				joins = assocTable.joinColumns();
 				inverseJoins = assocTable.inverseJoinColumns();
 			}
 
 			collectionBinder.setExplicitAssociationTable( true );
 
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
 
 			//set check constaint in the second pass
 			annJoins = joins.length == 0 ? null : joins;
 			annInverseJoins = inverseJoins == null || inverseJoins.length == 0 ? null : inverseJoins;
 		}
 		else {
 			annJoins = null;
 			annInverseJoins = null;
 		}
 		Ejb3JoinColumn[] joinColumns = Ejb3JoinColumn.buildJoinTableJoinColumns(
 				annJoins, entityBinder.getSecondaryTables(), propertyHolder, inferredData.getPropertyName(), mappedBy,
 				mappings
 		);
 		Ejb3JoinColumn[] inverseJoinColumns = Ejb3JoinColumn.buildJoinTableJoinColumns(
 				annInverseJoins, entityBinder.getSecondaryTables(), propertyHolder, inferredData.getPropertyName(),
 				mappedBy, mappings
 		);
 		associationTableBinder.setMappings( mappings );
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
 			Mappings mappings,
 			boolean isComponentEmbedded,
 			boolean isId, //is a identifier
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			String referencedEntityName, //is a component who is overridden by a @MapsId
 			Ejb3JoinColumn[] columns) {
 		Component comp;
 		if ( referencedEntityName != null ) {
 			comp = createComponent( propertyHolder, inferredData, isComponentEmbedded, isIdentifierMapper, mappings );
 			SecondPass sp = new CopyIdentifierComponentSecondPass(
 					comp,
 					referencedEntityName,
 					columns,
 					mappings
 			);
 			mappings.addSecondPass( sp );
 		}
 		else {
 			comp = fillComponent(
 					propertyHolder, inferredData, propertyAccessor, !isId, entityBinder,
 					isComponentEmbedded, isIdentifierMapper,
 					false, mappings, inheritanceStatePerClass
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
 		binder.setName( inferredData.getPropertyName() );
 		binder.setValue( comp );
 		binder.setProperty( inferredData.getProperty() );
 		binder.setAccessType( inferredData.getDefaultAccess() );
 		binder.setEmbedded( isComponentEmbedded );
 		binder.setHolder( propertyHolder );
 		binder.setId( isId );
 		binder.setEntityBinder( entityBinder );
 		binder.setInheritanceStatePerClass( inheritanceStatePerClass );
 		binder.setMappings( mappings );
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
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		return fillComponent(
 				propertyHolder, inferredData, null, propertyAccessor,
 				isNullable, entityBinder, isComponentEmbedded, isIdentifierMapper, inSecondPass, mappings,
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
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		/**
 		 * inSecondPass can only be used to apply right away the second pass of a composite-element
 		 * Because it's a value type, there is no bidirectional association, hence second pass
 		 * ordering does not matter
 		 */
 		Component comp = createComponent( propertyHolder, inferredData, isComponentEmbedded, isIdentifierMapper, mappings );
 		String subpath = BinderHelper.getPath( propertyHolder, inferredData );
 		LOG.tracev( "Binding component with path: {0}", subpath );
 		PropertyHolder subHolder = PropertyHolderBuilder.buildPropertyHolder(
 				comp, subpath,
 				inferredData, propertyHolder, mappings
 		);
 
 		final XClass xClassProcessed = inferredData.getPropertyClass();
 		List<PropertyData> classElements = new ArrayList<PropertyData>();
 		XClass returnedClassOrElement = inferredData.getClassOrElement();
 
 		List<PropertyData> baseClassElements = null;
 		Map<String, PropertyData> orderedBaseClassElements = new HashMap<String, PropertyData>();
 		XClass baseReturnedClassOrElement;
 		if ( baseInferredData != null ) {
 			baseClassElements = new ArrayList<PropertyData>();
 			baseReturnedClassOrElement = baseInferredData.getClassOrElement();
 			bindTypeDefs( baseReturnedClassOrElement, mappings );
 			PropertyContainer propContainer = new PropertyContainer( baseReturnedClassOrElement, xClassProcessed );
 			addElementsOfClass( baseClassElements, propertyAccessor, propContainer, mappings );
 			for ( PropertyData element : baseClassElements ) {
 				orderedBaseClassElements.put( element.getPropertyName(), element );
 			}
 		}
 
 		//embeddable elements can have type defs
 		bindTypeDefs( returnedClassOrElement, mappings );
 		PropertyContainer propContainer = new PropertyContainer( returnedClassOrElement, xClassProcessed );
 		addElementsOfClass( classElements, propertyAccessor, propContainer, mappings );
 
 		//add elements of the embeddable superclass
 		XClass superClass = xClassProcessed.getSuperclass();
 		while ( superClass != null && superClass.isAnnotationPresent( MappedSuperclass.class ) ) {
 			//FIXME: proper support of typevariables incl var resolved at upper levels
 			propContainer = new PropertyContainer( superClass, xClassProcessed );
 			addElementsOfClass( classElements, propertyAccessor, propContainer, mappings );
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
 					subHolder, isNullable ?
 							Nullability.NO_CONSTRAINT :
 							Nullability.FORCED_NOT_NULL,
 					propertyAnnotatedElement,
 					new HashMap<String, IdGenerator>(), entityBinder, isIdentifierMapper, isComponentEmbedded,
 					inSecondPass, mappings, inheritanceStatePerClass
 			);
 
 			XProperty property = propertyAnnotatedElement.getProperty();
 			if ( property.isAnnotationPresent( GeneratedValue.class ) &&
 					property.isAnnotationPresent( Id.class ) ) {
 				//clone classGenerator and override with local values
 				Map<String, IdGenerator> localGenerators = new HashMap<String, IdGenerator>();
 				localGenerators.putAll( buildLocalGenerators( property, mappings ) );
 
 				GeneratedValue generatedValue = property.getAnnotation( GeneratedValue.class );
 				String generatorType = generatedValue != null ? generatorType(
 						generatedValue.strategy(), mappings
 				) : "assigned";
 				String generator = generatedValue != null ? generatedValue.generator() : BinderHelper.ANNOTATION_STRING_DEFAULT;
 
 				BinderHelper.makeIdGenerator(
 						( SimpleValue ) comp.getProperty( property.getName() ).getValue(),
 						generatorType,
 						generator,
 						mappings,
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
 			Mappings mappings) {
 		Component comp = new Component( mappings, propertyHolder.getPersistentClass() );
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
 		comp.setNodeName( inferredData.getPropertyName() );
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
 			Mappings mappings,
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
 					propertyHolder, inferredData, baseInferredData, propertyAccessor,
 					false, entityBinder, isEmbedded, isIdentifierMapper, false, mappings, inheritanceStatePerClass
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
 			value.setMappings( mappings );
 			value.setType( inferredData.getProperty(), inferredData.getClassOrElement() );
+			value.setAccessType( propertyAccessor );
 			id = value.make();
 		}
 		rootClass.setIdentifier( id );
 		BinderHelper.makeIdGenerator( id, generatorType, generatorName, mappings, localGenerators );
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
 					mappings
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
 			Mappings mappings) {
 		List<PropertyData> baseClassElements = new ArrayList<PropertyData>();
 		XClass baseReturnedClassOrElement = baseInferredData.getClassOrElement();
 		PropertyContainer propContainer = new PropertyContainer(
 				baseReturnedClassOrElement, inferredData.getPropertyClass()
 		);
 		addElementsOfClass( baseClassElements, propertyAccessor, propContainer, mappings );
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
 			Mappings mappings) {
 		//All FK columns should be in the same table
 		org.hibernate.mapping.ManyToOne value = new org.hibernate.mapping.ManyToOne( mappings, columns[0].getTable() );
 		// This is a @OneToOne mapped to a physical o.h.mapping.ManyToOne
 		if ( unique ) {
 			value.markAsLogicalOneToOne();
 		}
 		value.setReferencedEntityName( ToOneBinder.getReferenceEntityName( inferredData, targetEntity, mappings ) );
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
 
 		//Make sure that JPA1 key-many-to-one columns are read only tooj
 		boolean hasSpecjManyToOne=false;
 		if ( mappings.isSpecjProprietarySyntaxEnabled() ) {
 			String columnName = "";
 			for ( XProperty prop : inferredData.getDeclaringClass()
 					.getDeclaredProperties( AccessType.FIELD.getType() ) ) {
 				if ( prop.isAnnotationPresent( Id.class ) && prop.isAnnotationPresent( Column.class ) ) {
 					columnName = prop.getAnnotation( Column.class ).name();
 				}
 
 				final JoinColumn joinColumn = property.getAnnotation( JoinColumn.class );
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
 
 		ForeignKey fk = property.getAnnotation( ForeignKey.class );
 		String fkName = fk != null ?
 				fk.name() :
 				"";
 		if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) {
 			value.setForeignKeyName( fkName );
 		}
 
 		String path = propertyHolder.getPath() + "." + propertyName;
 		FkSecondPass secondPass = new ToOneFkSecondPass(
 				value, columns,
 				!optional && unique, //cannot have nullable and unique on certain DBs like Derby
 				propertyHolder.getEntityOwnerClassName(),
 				path, mappings
 		);
 		if ( inSecondPass ) {
 			secondPass.doSecondPass( mappings.getClasses() );
 		}
 		else {
 			mappings.addSecondPass(
 					secondPass
 			);
 		}
 		Ejb3Column.checkPropertyConsistency( columns, propertyHolder.getEntityName() + propertyName );
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
 			Mappings mappings) {
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
 					propertyHolder, inferredData, targetEntity, ignoreNotFound, cascadeOnDelete,
 					optional, cascadeStrategy, joinColumns, mappings
 			);
 			if ( inSecondPass ) {
 				secondPass.doSecondPass( mappings.getClasses() );
 			}
 			else {
 				mappings.addSecondPass(
 						secondPass, BinderHelper.isEmptyAnnotationValue( mappedBy )
 				);
 			}
 		}
 		else {
 			//has a FK on the table
 			bindManyToOne(
 					cascadeStrategy, joinColumns, optional, ignoreNotFound, cascadeOnDelete,
 					targetEntity,
 					propertyHolder, inferredData, true, isIdentifierMapper, inSecondPass,
 					propertyBinder, mappings
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
 			Mappings mappings) {
 		org.hibernate.annotations.Any anyAnn = inferredData.getProperty()
 				.getAnnotation( org.hibernate.annotations.Any.class );
 		if ( anyAnn == null ) {
 			throw new AssertionFailure(
 					"Missing @Any annotation: "
 							+ BinderHelper.getPath( propertyHolder, inferredData )
 			);
 		}
 		Any value = BinderHelper.buildAnyValue(
 				anyAnn.metaDef(), columns, anyAnn.metaColumn(), inferredData,
 				cascadeOnDelete, nullability, propertyHolder, entityBinder, anyAnn.optional(), mappings
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
 
 	private static String generatorType(GenerationType generatorEnum, Mappings mappings) {
 		boolean useNewGeneratorMappings = mappings.useNewGeneratorMappings();
 		switch ( generatorEnum ) {
 			case IDENTITY:
 				return "identity";
 			case AUTO:
 				return useNewGeneratorMappings
 						? org.hibernate.id.enhanced.SequenceStyleGenerator.class.getName()
 						: "native";
 			case TABLE:
 				return useNewGeneratorMappings
 						? org.hibernate.id.enhanced.TableGenerator.class.getName()
 						: MultipleHiLoPerTableGenerator.class.getName();
 			case SEQUENCE:
 				return useNewGeneratorMappings
 						? org.hibernate.id.enhanced.SequenceStyleGenerator.class.getName()
 						: "seqhilo";
 		}
 		throw new AssertionFailure( "Unknown GeneratorType: " + generatorEnum );
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
 
 	private static HashMap<String, IdGenerator> buildLocalGenerators(XAnnotatedElement annElt, Mappings mappings) {
 		HashMap<String, IdGenerator> generators = new HashMap<String, IdGenerator>();
 		TableGenerator tabGen = annElt.getAnnotation( TableGenerator.class );
 		SequenceGenerator seqGen = annElt.getAnnotation( SequenceGenerator.class );
 		GenericGenerator genGen = annElt.getAnnotation( GenericGenerator.class );
 		if ( tabGen != null ) {
 			IdGenerator idGen = buildIdGenerator( tabGen, mappings );
 			generators.put( idGen.getName(), idGen );
 		}
 		if ( seqGen != null ) {
 			IdGenerator idGen = buildIdGenerator( seqGen, mappings );
 			generators.put( idGen.getName(), idGen );
 		}
 		if ( genGen != null ) {
 			IdGenerator idGen = buildIdGenerator( genGen, mappings );
 			generators.put( idGen.getName(), idGen );
 		}
 		return generators;
 	}
 
 	public static boolean isDefault(XClass clazz, Mappings mappings) {
 		return mappings.getReflectionManager().equals( clazz, void.class );
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
 			Mappings mappings) {
 		ReflectionManager reflectionManager = mappings.getReflectionManager();
 		Map<XClass, InheritanceState> inheritanceStatePerClass = new HashMap<XClass, InheritanceState>(
 				orderedClasses.size()
 		);
 		for ( XClass clazz : orderedClasses ) {
 			InheritanceState superclassState = InheritanceState.getSuperclassInheritanceState(
 					clazz, inheritanceStatePerClass
 			);
 			InheritanceState state = new InheritanceState( clazz, inheritanceStatePerClass, mappings );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java b/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java
index 3625acb98b..03aafe41db 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java
@@ -1,847 +1,847 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.cfg;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.AnyMetaDef;
 import org.hibernate.annotations.AnyMetaDefs;
 import org.hibernate.annotations.MetaValue;
 import org.hibernate.annotations.SqlFragmentAlias;
 import org.hibernate.annotations.common.reflection.XAnnotatedElement;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XPackage;
 import org.hibernate.cfg.annotations.EntityBinder;
 import org.hibernate.cfg.annotations.Nullability;
 import org.hibernate.cfg.annotations.TableBinder;
 import org.hibernate.id.MultipleHiLoPerTableGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.MappedSuperclass;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.SyntheticProperty;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 
 /**
  * @author Emmanuel Bernard
  */
 public class BinderHelper {
 
 	public static final String ANNOTATION_STRING_DEFAULT = "";
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, BinderHelper.class.getName());
 
 	private BinderHelper() {
 	}
 
 	static {
 		Set<String> primitiveNames = new HashSet<String>();
 		primitiveNames.add( byte.class.getName() );
 		primitiveNames.add( short.class.getName() );
 		primitiveNames.add( int.class.getName() );
 		primitiveNames.add( long.class.getName() );
 		primitiveNames.add( float.class.getName() );
 		primitiveNames.add( double.class.getName() );
 		primitiveNames.add( char.class.getName() );
 		primitiveNames.add( boolean.class.getName() );
 		PRIMITIVE_NAMES = Collections.unmodifiableSet( primitiveNames );
 	}
 
 	public static final Set<String> PRIMITIVE_NAMES;
 
 	/**
 	 * create a property copy reusing the same value
 	 */
 	public static Property shallowCopy(Property property) {
 		Property clone = new Property();
 		clone.setCascade( property.getCascade() );
 		clone.setInsertable( property.isInsertable() );
 		clone.setLazy( property.isLazy() );
 		clone.setName( property.getName() );
 		clone.setNodeName( property.getNodeName() );
 		clone.setNaturalIdentifier( property.isNaturalIdentifier() );
 		clone.setOptimisticLocked( property.isOptimisticLocked() );
 		clone.setOptional( property.isOptional() );
 		clone.setPersistentClass( property.getPersistentClass() );
 		clone.setPropertyAccessorName( property.getPropertyAccessorName() );
 		clone.setSelectable( property.isSelectable() );
 		clone.setUpdateable( property.isUpdateable() );
 		clone.setValue( property.getValue() );
 		return clone;
 	}
 
 // This is sooooooooo close in terms of not generating a synthetic property if we do not have to (where property ref
 // refers to a single property).  The sticking point is cases where the `referencedPropertyName` come from subclasses
 // or secondary tables.  Part of the problem is in PersistentClass itself during attempts to resolve the referenced
 // property; currently it only considers non-subclass and non-joined properties.  Part of the problem is in terms
 // of SQL generation.
 //	public static void createSyntheticPropertyReference(
 //			Ejb3JoinColumn[] columns,
 //			PersistentClass ownerEntity,
 //			PersistentClass associatedEntity,
 //			Value value,
 //			boolean inverse,
 //			Mappings mappings) {
 //		//associated entity only used for more precise exception, yuk!
 //		if ( columns[0].isImplicit() || StringHelper.isNotEmpty( columns[0].getMappedBy() ) ) return;
 //		int fkEnum = Ejb3JoinColumn.checkReferencedColumnsType( columns, ownerEntity, mappings );
 //		PersistentClass associatedClass = columns[0].getPropertyHolder() != null ?
 //				columns[0].getPropertyHolder().getPersistentClass() :
 //				null;
 //		if ( Ejb3JoinColumn.NON_PK_REFERENCE == fkEnum ) {
 //			//find properties associated to a certain column
 //			Object columnOwner = findColumnOwner( ownerEntity, columns[0].getReferencedColumn(), mappings );
 //			List<Property> properties = findPropertiesByColumns( columnOwner, columns, mappings );
 //
 //			if ( properties == null ) {
 //				//TODO use a ToOne type doing a second select
 //				StringBuilder columnsList = new StringBuilder();
 //				columnsList.append( "referencedColumnNames(" );
 //				for (Ejb3JoinColumn column : columns) {
 //					columnsList.append( column.getReferencedColumn() ).append( ", " );
 //				}
 //				columnsList.setLength( columnsList.length() - 2 );
 //				columnsList.append( ") " );
 //
 //				if ( associatedEntity != null ) {
 //					//overidden destination
 //					columnsList.append( "of " )
 //							.append( associatedEntity.getEntityName() )
 //							.append( "." )
 //							.append( columns[0].getPropertyName() )
 //							.append( " " );
 //				}
 //				else {
 //					if ( columns[0].getPropertyHolder() != null ) {
 //						columnsList.append( "of " )
 //								.append( columns[0].getPropertyHolder().getEntityName() )
 //								.append( "." )
 //								.append( columns[0].getPropertyName() )
 //								.append( " " );
 //					}
 //				}
 //				columnsList.append( "referencing " )
 //						.append( ownerEntity.getEntityName() )
 //						.append( " not mapped to a single property" );
 //				throw new AnnotationException( columnsList.toString() );
 //			}
 //
 //			final String referencedPropertyName;
 //
 //			if ( properties.size() == 1 ) {
 //				referencedPropertyName = properties.get(0).getName();
 //			}
 //			else {
 //				// Create a synthetic (embedded composite) property to use as the referenced property which
 //				// contains all the properties mapped to the referenced columns.  We need to make a shallow copy
 //				// of the properties to mark them as non-insertable/updatable.
 //
 //				// todo : what if the columns all match with an existing component?
 //
 //				StringBuilder propertyNameBuffer = new StringBuilder( "_" );
 //				propertyNameBuffer.append( associatedClass.getEntityName().replace( '.', '_' ) );
 //				propertyNameBuffer.append( "_" ).append( columns[0].getPropertyName() );
 //				String syntheticPropertyName = propertyNameBuffer.toString();
 //				//create an embeddable component
 //
 //				//todo how about properties.size() == 1, this should be much simpler
 //				Component embeddedComp = columnOwner instanceof PersistentClass ?
 //						new Component( mappings, (PersistentClass) columnOwner ) :
 //						new Component( mappings, (Join) columnOwner );
 //				embeddedComp.setEmbedded( true );
 //				embeddedComp.setNodeName( syntheticPropertyName );
 //				embeddedComp.setComponentClassName( embeddedComp.getOwner().getClassName() );
 //				for (Property property : properties) {
 //					Property clone = BinderHelper.shallowCopy( property );
 //					clone.setInsertable( false );
 //					clone.setUpdateable( false );
 //					clone.setNaturalIdentifier( false );
 //					clone.setGeneration( property.getGeneration() );
 //					embeddedComp.addProperty( clone );
 //				}
 //				SyntheticProperty synthProp = new SyntheticProperty();
 //				synthProp.setName( syntheticPropertyName );
 //				synthProp.setNodeName( syntheticPropertyName );
 //				synthProp.setPersistentClass( ownerEntity );
 //				synthProp.setUpdateable( false );
 //				synthProp.setInsertable( false );
 //				synthProp.setValue( embeddedComp );
 //				synthProp.setPropertyAccessorName( "embedded" );
 //				ownerEntity.addProperty( synthProp );
 //				//make it unique
 //				TableBinder.createUniqueConstraint( embeddedComp );
 //
 //				referencedPropertyName = syntheticPropertyName;
 //			}
 //
 //			/**
 //			 * creating the property ref to the new synthetic property
 //			 */
 //			if ( value instanceof ToOne ) {
 //				( (ToOne) value ).setReferencedPropertyName( referencedPropertyName );
 //				mappings.addUniquePropertyReference( ownerEntity.getEntityName(), referencedPropertyName );
 //			}
 //			else if ( value instanceof Collection ) {
 //				( (Collection) value ).setReferencedPropertyName( referencedPropertyName );
 //				//not unique because we could create a mtm wo association table
 //				mappings.addPropertyReference( ownerEntity.getEntityName(), referencedPropertyName );
 //			}
 //			else {
 //				throw new AssertionFailure(
 //						"Do a property ref on an unexpected Value type: "
 //								+ value.getClass().getName()
 //				);
 //			}
 //			mappings.addPropertyReferencedAssociation(
 //					( inverse ? "inverse__" : "" ) + associatedClass.getEntityName(),
 //					columns[0].getPropertyName(),
 //					referencedPropertyName
 //			);
 //		}
 //	}
 
 	public static void createSyntheticPropertyReference(
 			Ejb3JoinColumn[] columns,
 			PersistentClass ownerEntity,
 			PersistentClass associatedEntity,
 			Value value,
 			boolean inverse,
 			Mappings mappings) {
 		//associated entity only used for more precise exception, yuk!
 		if ( columns[0].isImplicit() || StringHelper.isNotEmpty( columns[0].getMappedBy() ) ) return;
 		int fkEnum = Ejb3JoinColumn.checkReferencedColumnsType( columns, ownerEntity, mappings );
 		PersistentClass associatedClass = columns[0].getPropertyHolder() != null ?
 				columns[0].getPropertyHolder().getPersistentClass() :
 				null;
 		if ( Ejb3JoinColumn.NON_PK_REFERENCE == fkEnum ) {
 			/**
 			 * Create a synthetic property to refer to including an
 			 * embedded component value containing all the properties
 			 * mapped to the referenced columns
 			 * We need to shallow copy those properties to mark them
 			 * as non insertable / non updatable
 			 */
 			StringBuilder propertyNameBuffer = new StringBuilder( "_" );
 			propertyNameBuffer.append( associatedClass.getEntityName().replace( '.', '_' ) );
-			propertyNameBuffer.append( "_" ).append( columns[0].getPropertyName() );
+			propertyNameBuffer.append( "_" ).append( columns[0].getPropertyName().replace( '.', '_' ) );
 			String syntheticPropertyName = propertyNameBuffer.toString();
 			//find properties associated to a certain column
 			Object columnOwner = findColumnOwner( ownerEntity, columns[0].getReferencedColumn(), mappings );
 			List<Property> properties = findPropertiesByColumns( columnOwner, columns, mappings );
 			//create an embeddable component
-			Property synthProp = null;
+                        Property synthProp = null;
 			if ( properties != null ) {
-				//todo how about properties.size() == 1, this should be much simpler
+                        //todo how about properties.size() == 1, this should be much simpler
 				Component embeddedComp = columnOwner instanceof PersistentClass ?
 						new Component( mappings, (PersistentClass) columnOwner ) :
 						new Component( mappings, (Join) columnOwner );
 				embeddedComp.setEmbedded( true );
 				embeddedComp.setNodeName( syntheticPropertyName );
 				embeddedComp.setComponentClassName( embeddedComp.getOwner().getClassName() );
 				for (Property property : properties) {
 					Property clone = BinderHelper.shallowCopy( property );
 					clone.setInsertable( false );
 					clone.setUpdateable( false );
 					clone.setNaturalIdentifier( false );
 					clone.setGeneration( property.getGeneration() );
 					embeddedComp.addProperty( clone );
-				}
-				synthProp = new SyntheticProperty();
+                                }
+                                    synthProp = new SyntheticProperty();
 				synthProp.setName( syntheticPropertyName );
 				synthProp.setNodeName( syntheticPropertyName );
 				synthProp.setPersistentClass( ownerEntity );
 				synthProp.setUpdateable( false );
 				synthProp.setInsertable( false );
 				synthProp.setValue( embeddedComp );
 				synthProp.setPropertyAccessorName( "embedded" );
 				ownerEntity.addProperty( synthProp );
-				//make it unique
+                                //make it unique
 				TableBinder.createUniqueConstraint( embeddedComp );
-			}
+                            }
 			else {
 				//TODO use a ToOne type doing a second select
 				StringBuilder columnsList = new StringBuilder();
 				columnsList.append( "referencedColumnNames(" );
 				for (Ejb3JoinColumn column : columns) {
 					columnsList.append( column.getReferencedColumn() ).append( ", " );
 				}
 				columnsList.setLength( columnsList.length() - 2 );
 				columnsList.append( ") " );
 
 				if ( associatedEntity != null ) {
 					//overidden destination
 					columnsList.append( "of " )
 							.append( associatedEntity.getEntityName() )
 							.append( "." )
 							.append( columns[0].getPropertyName() )
 							.append( " " );
 				}
 				else {
 					if ( columns[0].getPropertyHolder() != null ) {
 						columnsList.append( "of " )
 								.append( columns[0].getPropertyHolder().getEntityName() )
 								.append( "." )
 								.append( columns[0].getPropertyName() )
 								.append( " " );
 					}
 				}
 				columnsList.append( "referencing " )
 						.append( ownerEntity.getEntityName() )
 						.append( " not mapped to a single property" );
 				throw new AnnotationException( columnsList.toString() );
 			}
 
 			/**
 			 * creating the property ref to the new synthetic property
 			 */
 			if ( value instanceof ToOne ) {
 				( (ToOne) value ).setReferencedPropertyName( syntheticPropertyName );
 				mappings.addUniquePropertyReference( ownerEntity.getEntityName(), syntheticPropertyName );
 			}
 			else if ( value instanceof Collection ) {
 				( (Collection) value ).setReferencedPropertyName( syntheticPropertyName );
 				//not unique because we could create a mtm wo association table
 				mappings.addPropertyReference( ownerEntity.getEntityName(), syntheticPropertyName );
 			}
 			else {
 				throw new AssertionFailure(
 						"Do a property ref on an unexpected Value type: "
 								+ value.getClass().getName()
 				);
 			}
 			mappings.addPropertyReferencedAssociation(
 					( inverse ? "inverse__" : "" ) + associatedClass.getEntityName(),
 					columns[0].getPropertyName(),
 					syntheticPropertyName
 			);
 		}
 	}
 
 
 	private static List<Property> findPropertiesByColumns(
 			Object columnOwner,
 			Ejb3JoinColumn[] columns,
 			Mappings mappings) {
 		Map<Column, Set<Property>> columnsToProperty = new HashMap<Column, Set<Property>>();
 		List<Column> orderedColumns = new ArrayList<Column>( columns.length );
 		Table referencedTable = null;
 		if ( columnOwner instanceof PersistentClass ) {
 			referencedTable = ( (PersistentClass) columnOwner ).getTable();
 		}
 		else if ( columnOwner instanceof Join ) {
 			referencedTable = ( (Join) columnOwner ).getTable();
 		}
 		else {
 			throw new AssertionFailure(
 					columnOwner == null ?
 							"columnOwner is null" :
 							"columnOwner neither PersistentClass nor Join: " + columnOwner.getClass()
 			);
 		}
 		//build the list of column names
 		for (Ejb3JoinColumn column1 : columns) {
 			Column column = new Column(
 					mappings.getPhysicalColumnName( column1.getReferencedColumn(), referencedTable )
 			);
 			orderedColumns.add( column );
 			columnsToProperty.put( column, new HashSet<Property>() );
 		}
 		boolean isPersistentClass = columnOwner instanceof PersistentClass;
 		Iterator it = isPersistentClass ?
 				( (PersistentClass) columnOwner ).getPropertyIterator() :
 				( (Join) columnOwner ).getPropertyIterator();
 		while ( it.hasNext() ) {
 			matchColumnsByProperty( (Property) it.next(), columnsToProperty );
 		}
 		if ( isPersistentClass ) {
 			matchColumnsByProperty( ( (PersistentClass) columnOwner ).getIdentifierProperty(), columnsToProperty );
 		}
 
 		//first naive implementation
 		//only check 1 columns properties
 		//TODO make it smarter by checking correctly ordered multi column properties
 		List<Property> orderedProperties = new ArrayList<Property>();
 		for (Column column : orderedColumns) {
 			boolean found = false;
 			for (Property property : columnsToProperty.get( column ) ) {
 				if ( property.getColumnSpan() == 1 ) {
 					orderedProperties.add( property );
 					found = true;
 					break;
 				}
 			}
 			if ( !found ) return null; //have to find it the hard way
 		}
 		return orderedProperties;
 	}
 
 	private static void matchColumnsByProperty(Property property, Map<Column, Set<Property>> columnsToProperty) {
 		if ( property == null ) return;
 		if ( "noop".equals( property.getPropertyAccessorName() )
 				|| "embedded".equals( property.getPropertyAccessorName() ) ) {
 			return;
 		}
 // FIXME cannot use subproperties becasue the caller needs top level properties
 //		if ( property.isComposite() ) {
 //			Iterator subProperties = ( (Component) property.getValue() ).getPropertyIterator();
 //			while ( subProperties.hasNext() ) {
 //				matchColumnsByProperty( (Property) subProperties.next(), columnsToProperty );
 //			}
 //		}
 		else {
 			Iterator columnIt = property.getColumnIterator();
 			while ( columnIt.hasNext() ) {
 				Object column = columnIt.next(); //can be a Formula so we don't cast
 				//noinspection SuspiciousMethodCalls
 				if ( columnsToProperty.containsKey( column ) ) {
 					columnsToProperty.get( column ).add( property );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Retrieve the property by path in a recursive way, including IndetifierProperty in the loop
 	 * If propertyName is null or empty, the IdentifierProperty is returned
 	 */
 	public static Property findPropertyByName(PersistentClass associatedClass, String propertyName) {
 		Property property = null;
 		Property idProperty = associatedClass.getIdentifierProperty();
 		String idName = idProperty != null ? idProperty.getName() : null;
 		try {
 			if ( propertyName == null
 					|| propertyName.length() == 0
 					|| propertyName.equals( idName ) ) {
 				//default to id
 				property = idProperty;
 			}
 			else {
 				if ( propertyName.indexOf( idName + "." ) == 0 ) {
 					property = idProperty;
 					propertyName = propertyName.substring( idName.length() + 1 );
 				}
 				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
 				while ( st.hasMoreElements() ) {
 					String element = (String) st.nextElement();
 					if ( property == null ) {
 						property = associatedClass.getProperty( element );
 					}
 					else {
 						if ( !property.isComposite() ) return null;
 						property = ( (Component) property.getValue() ).getProperty( element );
 					}
 				}
 			}
 		}
 		catch (MappingException e) {
 			try {
 				//if we do not find it try to check the identifier mapper
 				if ( associatedClass.getIdentifierMapper() == null ) return null;
 				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
 				while ( st.hasMoreElements() ) {
 					String element = (String) st.nextElement();
 					if ( property == null ) {
 						property = associatedClass.getIdentifierMapper().getProperty( element );
 					}
 					else {
 						if ( !property.isComposite() ) return null;
 						property = ( (Component) property.getValue() ).getProperty( element );
 					}
 				}
 			}
 			catch (MappingException ee) {
 				return null;
 			}
 		}
 		return property;
 	}
 
 	/**
 	 * Retrieve the property by path in a recursive way
 	 */
 	public static Property findPropertyByName(Component component, String propertyName) {
 		Property property = null;
 		try {
 			if ( propertyName == null
 					|| propertyName.length() == 0) {
 				// Do not expect to use a primary key for this case
 				return null;
 			}
 			else {
 				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
 				while ( st.hasMoreElements() ) {
 					String element = (String) st.nextElement();
 					if ( property == null ) {
 						property = component.getProperty( element );
 					}
 					else {
 						if ( !property.isComposite() ) return null;
 						property = ( (Component) property.getValue() ).getProperty( element );
 					}
 				}
 			}
 		}
 		catch (MappingException e) {
 			try {
 				//if we do not find it try to check the identifier mapper
 				if ( component.getOwner().getIdentifierMapper() == null ) return null;
 				StringTokenizer st = new StringTokenizer( propertyName, ".", false );
 				while ( st.hasMoreElements() ) {
 					String element = (String) st.nextElement();
 					if ( property == null ) {
 						property = component.getOwner().getIdentifierMapper().getProperty( element );
 					}
 					else {
 						if ( !property.isComposite() ) return null;
 						property = ( (Component) property.getValue() ).getProperty( element );
 					}
 				}
 			}
 			catch (MappingException ee) {
 				return null;
 			}
 		}
 		return property;
 	}
 
 	public static String getRelativePath(PropertyHolder propertyHolder, String propertyName) {
 		if ( propertyHolder == null ) return propertyName;
 		String path = propertyHolder.getPath();
 		String entityName = propertyHolder.getPersistentClass().getEntityName();
 		if ( path.length() == entityName.length() ) {
 			return propertyName;
 		}
 		else {
 			return StringHelper.qualify( path.substring( entityName.length() + 1 ), propertyName );
 		}
 	}
 
 	/**
 	 * Find the column owner (ie PersistentClass or Join) of columnName.
 	 * If columnName is null or empty, persistentClass is returned
 	 */
 	public static Object findColumnOwner(
 			PersistentClass persistentClass,
 			String columnName,
 			Mappings mappings) {
 		if ( StringHelper.isEmpty( columnName ) ) {
 			return persistentClass; //shortcut for implicit referenced column names
 		}
 		PersistentClass current = persistentClass;
 		Object result;
 		boolean found = false;
 		do {
 			result = current;
 			Table currentTable = current.getTable();
 			try {
 				mappings.getPhysicalColumnName( columnName, currentTable );
 				found = true;
 			}
 			catch (MappingException me) {
 				//swallow it
 			}
 			Iterator joins = current.getJoinIterator();
 			while ( !found && joins.hasNext() ) {
 				result = joins.next();
 				currentTable = ( (Join) result ).getTable();
 				try {
 					mappings.getPhysicalColumnName( columnName, currentTable );
 					found = true;
 				}
 				catch (MappingException me) {
 					//swallow it
 				}
 			}
 			current = current.getSuperclass();
 		}
 		while ( !found && current != null );
 		return found ? result : null;
 	}
 
 	/**
 	 * apply an id generator to a SimpleValue
 	 */
 	public static void makeIdGenerator(
 			SimpleValue id,
 			String generatorType,
 			String generatorName,
 			Mappings mappings,
 			Map<String, IdGenerator> localGenerators) {
 		Table table = id.getTable();
 		table.setIdentifierValue( id );
 		//generator settings
 		id.setIdentifierGeneratorStrategy( generatorType );
 		Properties params = new Properties();
 		//always settable
 		params.setProperty(
 				PersistentIdentifierGenerator.TABLE, table.getName()
 		);
 
 		if ( id.getColumnSpan() == 1 ) {
 			params.setProperty(
 					PersistentIdentifierGenerator.PK,
 					( (org.hibernate.mapping.Column) id.getColumnIterator().next() ).getName()
 			);
 		}
 		// YUCK!  but cannot think of a clean way to do this given the string-config based scheme
 		params.put( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, mappings.getObjectNameNormalizer() );
 
 		if ( !isEmptyAnnotationValue( generatorName ) ) {
 			//we have a named generator
 			IdGenerator gen = mappings.getGenerator( generatorName, localGenerators );
 			if ( gen == null ) {
 				throw new AnnotationException( "Unknown Id.generator: " + generatorName );
 			}
 			//This is quite vague in the spec but a generator could override the generate choice
 			String identifierGeneratorStrategy = gen.getIdentifierGeneratorStrategy();
 			//yuk! this is a hack not to override 'AUTO' even if generator is set
 			final boolean avoidOverriding =
 					identifierGeneratorStrategy.equals( "identity" )
 							|| identifierGeneratorStrategy.equals( "seqhilo" )
 							|| identifierGeneratorStrategy.equals( MultipleHiLoPerTableGenerator.class.getName() );
 			if ( generatorType == null || !avoidOverriding ) {
 				id.setIdentifierGeneratorStrategy( identifierGeneratorStrategy );
 			}
 			//checkIfMatchingGenerator(gen, generatorType, generatorName);
 			Iterator genParams = gen.getParams().entrySet().iterator();
 			while ( genParams.hasNext() ) {
 				Map.Entry elt = (Map.Entry) genParams.next();
 				params.setProperty( (String) elt.getKey(), (String) elt.getValue() );
 			}
 		}
 		if ( "assigned".equals( generatorType ) ) id.setNullValue( "undefined" );
 		id.setIdentifierGeneratorProperties( params );
 	}
 
 	public static boolean isEmptyAnnotationValue(String annotationString) {
 		return annotationString != null && annotationString.length() == 0;
 		//equivalent to (but faster) ANNOTATION_STRING_DEFAULT.equals( annotationString );
 	}
 
 	public static Any buildAnyValue(
 			String anyMetaDefName,
 			Ejb3JoinColumn[] columns,
 			javax.persistence.Column metaColumn,
 			PropertyData inferredData,
 			boolean cascadeOnDelete,
 			Nullability nullability,
 			PropertyHolder propertyHolder,
 			EntityBinder entityBinder,
 			boolean optional,
 			Mappings mappings) {
 		//All FK columns should be in the same table
 		Any value = new Any( mappings, columns[0].getTable() );
 		AnyMetaDef metaAnnDef = inferredData.getProperty().getAnnotation( AnyMetaDef.class );
 
 		if ( metaAnnDef != null ) {
 			//local has precedence over general and can be mapped for future reference if named
 			bindAnyMetaDefs( inferredData.getProperty(), mappings );
 		}
 		else {
 			metaAnnDef = mappings.getAnyMetaDef( anyMetaDefName );
 		}
 		if ( metaAnnDef != null ) {
 			value.setIdentifierType( metaAnnDef.idType() );
 			value.setMetaType( metaAnnDef.metaType() );
 
 			HashMap values = new HashMap();
 			org.hibernate.type.Type metaType = mappings.getTypeResolver().heuristicType( value.getMetaType() );
 			for (MetaValue metaValue : metaAnnDef.metaValues()) {
 				try {
 					Object discrim = ( (org.hibernate.type.DiscriminatorType) metaType ).stringToObject( metaValue
 							.value() );
 					String entityName = metaValue.targetEntity().getName();
 					values.put( discrim, entityName );
 				}
 				catch (ClassCastException cce) {
 					throw new MappingException( "metaType was not a DiscriminatorType: "
 							+ metaType.getName() );
 				}
 				catch (Exception e) {
 					throw new MappingException( "could not interpret metaValue", e );
 				}
 			}
 			if ( !values.isEmpty() ) value.setMetaValues( values );
 		}
 		else {
 			throw new AnnotationException( "Unable to find @AnyMetaDef for an @(ManyTo)Any mapping: "
 					+ StringHelper.qualify( propertyHolder.getPath(), inferredData.getPropertyName() ) );
 		}
 
 		value.setCascadeDeleteEnabled( cascadeOnDelete );
 		if ( !optional ) {
 			for (Ejb3JoinColumn column : columns) {
 				column.setNullable( false );
 			}
 		}
 
 		Ejb3Column[] metaColumns = Ejb3Column.buildColumnFromAnnotation(
 				new javax.persistence.Column[] { metaColumn }, null,
 				nullability, propertyHolder, inferredData, entityBinder.getSecondaryTables(), mappings
 		);
 		//set metaColumn to the right table
 		for (Ejb3Column column : metaColumns) {
 			column.setTable( value.getTable() );
 		}
 		//meta column
 		for (Ejb3Column column : metaColumns) {
 			column.linkWithValue( value );
 		}
 
 		//id columns
 		final String propertyName = inferredData.getPropertyName();
 		Ejb3Column.checkPropertyConsistency( columns, propertyHolder.getEntityName() + propertyName );
 		for (Ejb3JoinColumn column : columns) {
 			column.linkWithValue( value );
 		}
 		return value;
 	}
 
 	public static void bindAnyMetaDefs(XAnnotatedElement annotatedElement, Mappings mappings) {
 		AnyMetaDef defAnn = annotatedElement.getAnnotation( AnyMetaDef.class );
 		AnyMetaDefs defsAnn = annotatedElement.getAnnotation( AnyMetaDefs.class );
 		boolean mustHaveName = XClass.class.isAssignableFrom( annotatedElement.getClass() )
 				|| XPackage.class.isAssignableFrom( annotatedElement.getClass() );
 		if ( defAnn != null ) {
 			checkAnyMetaDefValidity( mustHaveName, defAnn, annotatedElement );
 			bindAnyMetaDef( defAnn, mappings );
 		}
 		if ( defsAnn != null ) {
 			for (AnyMetaDef def : defsAnn.value()) {
 				checkAnyMetaDefValidity( mustHaveName, def, annotatedElement );
 				bindAnyMetaDef( def, mappings );
 			}
 		}
 	}
 
 	private static void checkAnyMetaDefValidity(boolean mustHaveName, AnyMetaDef defAnn, XAnnotatedElement annotatedElement) {
 		if ( mustHaveName && isEmptyAnnotationValue( defAnn.name() ) ) {
 			String name = XClass.class.isAssignableFrom( annotatedElement.getClass() ) ?
 					( (XClass) annotatedElement ).getName() :
 					( (XPackage) annotatedElement ).getName();
 			throw new AnnotationException( "@AnyMetaDef.name cannot be null on an entity or a package: " + name );
 		}
 	}
 
 	private static void bindAnyMetaDef(AnyMetaDef defAnn, Mappings mappings) {
 		if ( isEmptyAnnotationValue( defAnn.name() ) ) return; //don't map not named definitions
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding Any Meta definition: %s", defAnn.name() );
 		}
 		mappings.addAnyMetaDef( defAnn );
 	}
 
 	public static MappedSuperclass getMappedSuperclassOrNull(
 			XClass declaringClass,
 			Map<XClass, InheritanceState> inheritanceStatePerClass,
 			Mappings mappings) {
 		boolean retrieve = false;
 		if ( declaringClass != null ) {
 			final InheritanceState inheritanceState = inheritanceStatePerClass.get( declaringClass );
 			if ( inheritanceState == null ) {
 				throw new org.hibernate.annotations.common.AssertionFailure(
 						"Declaring class is not found in the inheritance state hierarchy: " + declaringClass
 				);
 			}
 			if ( inheritanceState.isEmbeddableSuperclass() ) {
 				retrieve = true;
 			}
 		}
 		return retrieve ?
 				mappings.getMappedSuperclass( mappings.getReflectionManager().toClass( declaringClass ) ) :
 		        null;
 	}
 
 	public static String getPath(PropertyHolder holder, PropertyData property) {
 		return StringHelper.qualify( holder.getPath(), property.getPropertyName() );
 	}
 
 	static PropertyData getPropertyOverriddenByMapperOrMapsId(
 			boolean isId,
 			PropertyHolder propertyHolder,
 			String propertyName,
 			Mappings mappings) {
 		final XClass persistentXClass;
 		try {
 			 persistentXClass = mappings.getReflectionManager()
 					.classForName( propertyHolder.getPersistentClass().getClassName(), AnnotationBinder.class );
 		}
 		catch ( ClassNotFoundException e ) {
 			throw new AssertionFailure( "PersistentClass name cannot be converted into a Class", e);
 		}
 		if ( propertyHolder.isInIdClass() ) {
 			PropertyData pd = mappings.getPropertyAnnotatedWithIdAndToOne( persistentXClass, propertyName );
 			if ( pd == null && mappings.isSpecjProprietarySyntaxEnabled() ) {
 				pd = mappings.getPropertyAnnotatedWithMapsId( persistentXClass, propertyName );
 			}
 			return pd;
 		}
         String propertyPath = isId ? "" : propertyName;
         return mappings.getPropertyAnnotatedWithMapsId(persistentXClass, propertyPath);
 	}
 	
 	public static Map<String,String> toAliasTableMap(SqlFragmentAlias[] aliases){
 		Map<String,String> ret = new HashMap<String,String>();
 		for (int i = 0; i < aliases.length; i++){
 			if (StringHelper.isNotEmpty(aliases[i].table())){
 				ret.put(aliases[i].alias(), aliases[i].table());
-			}
+}
 		}
 		return ret;
 	}
 	
 	public static Map<String,String> toAliasEntityMap(SqlFragmentAlias[] aliases){
 		Map<String,String> ret = new HashMap<String,String>();
 		for (int i = 0; i < aliases.length; i++){
 			if (aliases[i].entity() != void.class){
 				ret.put(aliases[i].alias(), aliases[i].entity().getName());
 			}
 		}
 		return ret;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
index 5234bde5ed..1e663cb879 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
@@ -292,1151 +292,1153 @@ public abstract class CollectionBinder {
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
 			final TypeDef typeDef = mappings.getTypeDef( typeName );
 			if ( typeDef != null ) {
 				result.explicitType = typeDef.getTypeClass();
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
 
 	protected CollectionBinder() {
 	}
 
 	protected CollectionBinder(boolean sorted) {
 		this.hasToBeSorted = sorted;
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
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
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
 		collection.setNodeName( propertyName );
 
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
 			final TypeDef typeDef = mappings.getTypeDef( explicitType );
 			if ( typeDef == null ) {
 				collection.setTypeName( explicitType );
 				collection.setTypeParameters( explicitTypeParameters );
 			}
 			else {
 				collection.setTypeName( typeDef.getTypeClass() );
 				collection.setTypeParameters( typeDef.getParameters() );
 			}
 		}
 
 		//set laziness
 		defineFetchingStrategy();
 		collection.setBatchSize( batchSize );
 		if ( orderBy != null && hqlOrderBy != null ) {
 			throw new AnnotationException(
 					"Cannot use sql order by clause in conjunction of EJB3 order by clause: " + safeCollectionRole()
 			);
 		}
 
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
 
 		// set ordering
 		if ( orderBy != null ) collection.setOrderBy( orderBy );
 		if ( isSorted ) {
 			collection.setSorted( true );
 			if ( comparator != null ) {
 				try {
 					collection.setComparator( (Comparator) comparator.newInstance() );
 				}
 				catch (ClassCastException e) {
 					throw new AnnotationException(
 							"Comparator not implementing java.util.Comparator class: "
 									+ comparator.getName() + "(" + safeCollectionRole() + ")"
 					);
 				}
 				catch (Exception e) {
 					throw new AnnotationException(
 							"Could not instantiate comparator class: "
 									+ comparator.getName() + "(" + safeCollectionRole() + ")"
 					);
 				}
 			}
 		}
 		else {
 			if ( hasToBeSorted ) {
 				throw new AnnotationException(
 						"A sorted collection has to define @Sort: "
 								+ safeCollectionRole()
 				);
 			}
 		}
 
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
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlInsert.check().toString().toLowerCase() )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			collection.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlUpdate.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDelete != null ) {
 			collection.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDelete.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			collection.setCustomSQLDeleteAll( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase() )
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
 			mappings.addMappedBy( getCollectionType().getName(), mappedBy, propertyName );
 		}
 		//TODO reducce tableBinder != null and oneToMany
 		XClass collectionType = getCollectionType();
 		if ( inheritanceStatePerClass == null) throw new AssertionFailure( "inheritanceStatePerClass not set" );
 		SecondPass sp = getSecondPass(
 				fkJoinColumns,
 				joinColumns,
 				inverseJoinColumns,
 				elementColumns,
 				mapKeyColumns, mapKeyManyToManyColumns, isEmbedded,
 				property, collectionType,
 				ignoreNotFound, oneToMany,
 				tableBinder, mappings
 		);
 		if ( collectionType.isAnnotationPresent( Embeddable.class )
 				|| property.isAnnotationPresent( ElementCollection.class ) //JPA 2
 				) {
 			// do it right away, otherwise @ManyToOne on composite element call addSecondPass
 			// and raise a ConcurrentModificationException
 			//sp.doSecondPass( CollectionHelper.EMPTY_MAP );
 			mappings.addSecondPass( sp, !isMappedBy );
 		}
 		else {
 			mappings.addSecondPass( sp, !isMappedBy );
 		}
 
 		mappings.addCollection( collection );
 
 		//property building
 		PropertyBinder binder = new PropertyBinder();
 		binder.setName( propertyName );
 		binder.setValue( collection );
 		binder.setCascade( cascadeStrategy );
 		if ( cascadeStrategy != null && cascadeStrategy.indexOf( "delete-orphan" ) >= 0 ) {
 			collection.setOrphanDelete( true );
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
 		if ( AnnotationBinder.isDefault( targetEntity, mappings ) ) {
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
 			final Mappings mappings) {
 		return new CollectionSecondPass( mappings, collection ) {
 			@Override
             public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas) throws MappingException {
 				bindStarToManySecondPass(
 						persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns,
 						isEmbedded, property, unique, assocTableBinder, ignoreNotFound, mappings
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
 			Mappings mappings) {
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
 					ignoreNotFound, hqlOrderBy,
 					mappings,
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
 					associationTableBinder, property, propertyHolder, hqlOrderBy, mappings
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
 			String hqlOrderBy,
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Binding a OneToMany: %s.%s through a foreign key", propertyHolder.getEntityName(), propertyName );
 		}
 		org.hibernate.mapping.OneToMany oneToMany = new org.hibernate.mapping.OneToMany( mappings, collection.getOwner() );
 		collection.setElement( oneToMany );
 		oneToMany.setReferencedEntityName( collectionType.getName() );
 		oneToMany.setIgnoreNotFound( ignoreNotFound );
 
 		String assocClass = oneToMany.getReferencedEntityName();
 		PersistentClass associatedClass = (PersistentClass) persistentClasses.get( assocClass );
 		String orderBy = buildOrderByClauseFromHql( hqlOrderBy, associatedClass, collection.getRole() );
 		if ( orderBy != null ) collection.setOrderBy( orderBy );
 		if ( mappings == null ) {
 			throw new AssertionFailure(
 					"CollectionSecondPass for oneToMany should not be called with null mappings"
 			);
 		}
 		Map<String, Join> joins = mappings.getJoins( assocClass );
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
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 		}
 		bindFilters( false );
 		bindCollectionSecondPass( collection, null, fkJoinColumns, cascadeDeleteEnabled, property, mappings );
 		if ( !collection.isInverse()
 				&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = oneToMany.getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + fkJoinColumns[0].getPropertyName() + "Backref" );
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
 
 		Where where = property.getAnnotation( Where.class );
 		String whereClause = where == null ? null : where.clause();
 		if ( StringHelper.isNotEmpty( whereClause ) ) {
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
 			cond = mappings.getFilterDefinition( name ).getDefaultFilterCondition();
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
 			Mappings mappings) {
 		//binding key reference using column
 		KeyValue keyVal;
 		//give a chance to override the referenced property name
 		//has to do that here because the referencedProperty creation happens in a FKSecondPass for Many to one yuk!
 		if ( joinColumns.length > 0 && StringHelper.isNotEmpty( joinColumns[0].getMappedBy() ) ) {
 			String entityName = joinColumns[0].getManyToManyOwnerSideEntityName() != null ?
 					"inverse__" + joinColumns[0].getManyToManyOwnerSideEntityName() :
 					joinColumns[0].getPropertyHolder().getEntityName();
 			String propRef = mappings.getPropertyReferencedAssociation(
 					entityName,
 					joinColumns[0].getMappedBy()
 			);
 			if ( propRef != null ) {
 				collValue.setReferencedPropertyName( propRef );
 				mappings.addPropertyReference( collValue.getOwnerEntityName(), propRef );
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
 		DependantValue key = new DependantValue( mappings, collValue.getCollectionTable(), keyVal );
 		key.setTypeName( null );
 		Ejb3Column.checkPropertyConsistency( joinColumns, collValue.getOwnerEntityName() );
 		key.setNullable( joinColumns.length == 0 || joinColumns[0].isNullable() );
 		key.setUpdateable( joinColumns.length == 0 || joinColumns[0].isUpdatable() );
 		key.setCascadeDeleteEnabled( cascadeDeleteEnabled );
 		collValue.setKey( key );
 		ForeignKey fk = property != null ? property.getAnnotation( ForeignKey.class ) : null;
 		String fkName = fk != null ? fk.name() : "";
 		if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) key.setForeignKeyName( fkName );
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
 			String hqlOrderBy,
 			Mappings mappings) throws MappingException {
 
 		PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( collType.getName() );
 		boolean isCollectionOfEntities = collectionEntity != null;
 		ManyToAny anyAnn = property.getAnnotation( ManyToAny.class );
         if (LOG.isDebugEnabled()) {
 			String path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
             if (isCollectionOfEntities && unique) LOG.debugf("Binding a OneToMany: %s through an association table", path);
             else if (isCollectionOfEntities) LOG.debugf("Binding as ManyToMany: %s", path);
             else if (anyAnn != null) LOG.debugf("Binding a ManyToAny: %s", path);
             else LOG.debugf("Binding a collection of element: %s", path);
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
 				StringBuilder error = new StringBuilder( 80 );
 				error.append( "mappedBy reference an unknown target entity property: " )
 						.append( collType ).append( "." ).append( joinColumns[0].getMappedBy() )
 						.append( " in " )
 						.append( collValue.getOwnerEntityName() )
 						.append( "." )
 						.append( joinColumns[0].getPropertyName() );
 				throw new AnnotationException( error.toString() );
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
 				String mappedByProperty = mappings.getFromMappedBy(
 						collValue.getOwnerEntityName(), column.getPropertyName()
 				);
 				Table ownerTable = collValue.getOwner().getTable();
 				column.setMappedBy(
 						collValue.getOwner().getEntityName(), mappings.getLogicalTableName( ownerTable ),
 						mappedByProperty
 				);
 //				String header = ( mappedByProperty == null ) ? mappings.getLogicalTableName( ownerTable ) : mappedByProperty;
 //				column.setDefaultColumnHeader( header );
 			}
 			if ( StringHelper.isEmpty( associationTableBinder.getName() ) ) {
 				//default value
 				associationTableBinder.setDefaultName(
 						collValue.getOwner().getEntityName(),
 						mappings.getLogicalTableName( collValue.getOwner().getTable() ),
 						collectionEntity != null ? collectionEntity.getEntityName() : null,
 						collectionEntity != null ? mappings.getLogicalTableName( collectionEntity.getTable() ) : null,
 						joinColumns[0].getPropertyName()
 				);
 			}
 			associationTableBinder.setJPA2ElementCollection( !isCollectionOfEntities && property.isAnnotationPresent( ElementCollection.class ));
 			collValue.setCollectionTable( associationTableBinder.bind() );
 		}
 		bindFilters( isCollectionOfEntities );
 		bindCollectionSecondPass( collValue, collectionEntity, joinColumns, cascadeDeleteEnabled, property, mappings );
 
 		ManyToOne element = null;
 		if ( isCollectionOfEntities ) {
 			element =
 					new ManyToOne( mappings,  collValue.getCollectionTable() );
 			collValue.setElement( element );
 			element.setReferencedEntityName( collType.getName() );
 			//element.setFetchMode( fetchMode );
 			//element.setLazy( fetchMode != FetchMode.JOIN );
 			//make the second join non lazy
 			element.setFetchMode( FetchMode.JOIN );
 			element.setLazy( false );
 			element.setIgnoreNotFound( ignoreNotFound );
 			// as per 11.1.38 of JPA-2 spec, default to primary key if no column is specified by @OrderBy.
 			if ( hqlOrderBy != null ) {
 				collValue.setManyToManyOrdering(
 						buildOrderByClauseFromHql( hqlOrderBy, collectionEntity, collValue.getRole() )
 				);
 			}
 			ForeignKey fk = property != null ? property.getAnnotation( ForeignKey.class ) : null;
 			String fkName = fk != null ? fk.inverseName() : "";
 			if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) element.setForeignKeyName( fkName );
 		}
 		else if ( anyAnn != null ) {
 			//@ManyToAny
 			//Make sure that collTyp is never used during the @ManyToAny branch: it will be set to void.class
 			PropertyData inferredData = new PropertyInferredData(null, property, "unsupported", mappings.getReflectionManager() );
 			//override the table
 			for (Ejb3Column column : inverseJoinColumns) {
 				column.setTable( collValue.getCollectionTable() );
 			}
 			Any any = BinderHelper.buildAnyValue( anyAnn.metaDef(), inverseJoinColumns, anyAnn.metaColumn(),
 					inferredData, cascadeDeleteEnabled, Nullability.NO_CONSTRAINT,
 					propertyHolder, new EntityBinder(), true, mappings );
 			collValue.setElement( any );
 		}
 		else {
 			XClass elementClass;
 			AnnotatedClassType classType;
 
 			PropertyHolder holder = null;
 			if ( BinderHelper.PRIMITIVE_NAMES.contains( collType.getName() ) ) {
 				classType = AnnotatedClassType.NONE;
 				elementClass = null;
 			}
 			else {
 				elementClass = collType;
 				classType = mappings.getClassType( elementClass );
 
 				holder = PropertyHolderBuilder.buildPropertyHolder(
 						collValue,
 						collValue.getRole(),
 						elementClass,
 						property, parentPropertyHolder, mappings
 				);
 				//force in case of attribute override
 				boolean attributeOverride = property.isAnnotationPresent( AttributeOverride.class )
 						|| property.isAnnotationPresent( AttributeOverrides.class );
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
 				Component component = AnnotationBinder.fillComponent(
 						holder, inferredData, isPropertyAnnotated ? AccessType.PROPERTY : AccessType.FIELD, true,
 						entityBinder, false, false,
 						true, mappings, inheritanceStatePerClass
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
 				SimpleValueBinder elementBinder = new SimpleValueBinder();
 				elementBinder.setMappings( mappings );
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
 					column.setMappings( mappings );
 					column.bind();
 					elementColumns[0] = column;
 				}
 				//override the table
 				for (Ejb3Column column : elementColumns) {
 					column.setTable( collValue.getCollectionTable() );
 				}
 				elementBinder.setColumns( elementColumns );
 				elementBinder.setType( property, elementClass );
+				elementBinder.setPersistentClassName( propertyHolder.getEntityName() );
+				elementBinder.setAccessType( accessType );
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
 			bindManytoManyInverseFk( collectionEntity, inverseJoinColumns, element, unique, mappings );
 		}
 
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
 			Mappings mappings) {
 		BinderHelper.createSyntheticPropertyReference(
 				joinColumns, collValue.getOwner(), collectionEntity, collValue, false, mappings
 		);
 		SimpleValue key = buildCollectionKey( collValue, joinColumns, cascadeDeleteEnabled, property, mappings );
 		if ( property.isAnnotationPresent( ElementCollection.class ) && joinColumns.length > 0 ) {
 			joinColumns[0].setJPA2ElementCollection( true );
 		}
 		TableBinder.bindFk( collValue.getOwner(), collectionEntity, joinColumns, key, false, mappings );
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
 			Mappings mappings) {
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
 					mappings.getPropertyReferencedAssociation(
 							"inverse__" + referencedEntity.getEntityName(), mappedBy
 					);
 			if ( referencedPropertyName != null ) {
 				//TODO always a many to one?
 				( (ManyToOne) value ).setReferencedPropertyName( referencedPropertyName );
 				mappings.addUniquePropertyReference( referencedEntity.getEntityName(), referencedPropertyName );
 			}
 			value.createForeignKey();
 		}
 		else {
 			BinderHelper.createSyntheticPropertyReference( columns, referencedEntity, null, value, true, mappings );
 			TableBinder.bindFk( referencedEntity, null, columns, value, unique, mappings );
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
 
 	public void setLocalGenerators(HashMap<String, IdGenerator> localGenerators) {
 		this.localGenerators = localGenerators;
 	}
-}
\ No newline at end of file
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
index a21074c3be..d62ddc3fb0 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/MapBinder.java
@@ -1,439 +1,440 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.cfg.annotations;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Random;
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.MapKeyClass;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.MapKeyType;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.CollectionSecondPass;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.PropertyHolderBuilder;
 import org.hibernate.cfg.PropertyPreloadedData;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.dialect.HSQLDialect;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Formula;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.OneToMany;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 import org.hibernate.sql.Template;
 
 /**
  * Implementation to bind a Map
  *
  * @author Emmanuel Bernard
  */
 public class MapBinder extends CollectionBinder {
 	public MapBinder(boolean sorted) {
 		super( sorted );
 	}
 
 	public MapBinder() {
 		super();
 	}
 
 	public boolean isMap() {
 		return true;
 	}
 
 	protected Collection createCollection(PersistentClass persistentClass) {
 		return new org.hibernate.mapping.Map( getMappings(), persistentClass );
 	}
 
 	@Override
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
 			final Mappings mappings) {
 		return new CollectionSecondPass( mappings, MapBinder.this.collection ) {
 			public void secondPass(Map persistentClasses, Map inheritedMetas)
 					throws MappingException {
 				bindStarToManySecondPass(
 						persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns,
 						isEmbedded, property, unique, assocTableBinder, ignoreNotFound, mappings
 				);
 				bindKeyFromAssociationTable(
 						collType, persistentClasses, mapKeyPropertyName, property, isEmbedded, mappings,
 						mapKeyColumns, mapKeyManyToManyColumns,
 						inverseColumns != null ? inverseColumns[0].getPropertyName() : null
 				);
 			}
 		};
 	}
 
 	private void bindKeyFromAssociationTable(
 			XClass collType,
 			Map persistentClasses,
 			String mapKeyPropertyName,
 			XProperty property,
 			boolean isEmbedded,
 			Mappings mappings,
 			Ejb3Column[] mapKeyColumns,
 			Ejb3JoinColumn[] mapKeyManyToManyColumns,
 			String targetPropertyName) {
 		if ( mapKeyPropertyName != null ) {
 			//this is an EJB3 @MapKey
 			PersistentClass associatedClass = (PersistentClass) persistentClasses.get( collType.getName() );
 			if ( associatedClass == null ) throw new AnnotationException( "Associated class not found: " + collType );
 			Property mapProperty = BinderHelper.findPropertyByName( associatedClass, mapKeyPropertyName );
 			if ( mapProperty == null ) {
 				throw new AnnotationException(
 						"Map key property not found: " + collType + "." + mapKeyPropertyName
 				);
 			}
 			org.hibernate.mapping.Map map = (org.hibernate.mapping.Map) this.collection;
 			Value indexValue = createFormulatedValue(
 					mapProperty.getValue(), map, targetPropertyName, associatedClass, mappings
 			);
 			map.setIndex( indexValue );
 		}
 		else {
 			//this is a true Map mapping
 			//TODO ugly copy/pastle from CollectionBinder.bindManyToManySecondPass
 			String mapKeyType;
 			Class target = void.class;
 			/*
 			 * target has priority over reflection for the map key type
 			 * JPA 2 has priority
 			 */
 			if ( property.isAnnotationPresent( MapKeyClass.class ) ) {
 				target = property.getAnnotation( MapKeyClass.class ).value();
 			}
 			if ( !void.class.equals( target ) ) {
 				mapKeyType = target.getName();
 			}
 			else {
 				mapKeyType = property.getMapKey().getName();
 			}
 			PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( mapKeyType );
 			boolean isIndexOfEntities = collectionEntity != null;
 			ManyToOne element = null;
 			org.hibernate.mapping.Map mapValue = (org.hibernate.mapping.Map) this.collection;
 			if ( isIndexOfEntities ) {
 				element = new ManyToOne( mappings, mapValue.getCollectionTable() );
 				mapValue.setIndex( element );
 				element.setReferencedEntityName( mapKeyType );
 				//element.setFetchMode( fetchMode );
 				//element.setLazy( fetchMode != FetchMode.JOIN );
 				//make the second join non lazy
 				element.setFetchMode( FetchMode.JOIN );
 				element.setLazy( false );
 				//does not make sense for a map key element.setIgnoreNotFound( ignoreNotFound );
 			}
 			else {
 				XClass elementClass;
 				AnnotatedClassType classType;
 				PropertyHolder holder = null;
 				if ( BinderHelper.PRIMITIVE_NAMES.contains( mapKeyType ) ) {
 					classType = AnnotatedClassType.NONE;
 					elementClass = null;
 				}
 				else {
 					try {
 						elementClass = mappings.getReflectionManager().classForName( mapKeyType, MapBinder.class );
 					}
 					catch (ClassNotFoundException e) {
 						throw new AnnotationException( "Unable to find class: " + mapKeyType, e );
 					}
 					classType = mappings.getClassType( elementClass );
 
 					holder = PropertyHolderBuilder.buildPropertyHolder(
 							mapValue,
 							StringHelper.qualify( mapValue.getRole(), "mapkey" ),
 							elementClass,
 							property, propertyHolder, mappings
 					);
 					//force in case of attribute override
 					boolean attributeOverride = property.isAnnotationPresent( AttributeOverride.class )
 							|| property.isAnnotationPresent( AttributeOverrides.class );
 					if ( isEmbedded || attributeOverride ) {
 						classType = AnnotatedClassType.EMBEDDABLE;
 					}
 				}
 
+				PersistentClass owner = mapValue.getOwner();
+				AccessType accessType;
+				// FIXME support @Access for collection of elements
+				// String accessType = access != null ? access.value() : null;
+				if ( owner.getIdentifierProperty() != null ) {
+					accessType = owner.getIdentifierProperty().getPropertyAccessorName().equals( "property" ) ? AccessType.PROPERTY
+							: AccessType.FIELD;
+				}
+				else if ( owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0 ) {
+					Property prop = (Property) owner.getIdentifierMapper().getPropertyIterator().next();
+					accessType = prop.getPropertyAccessorName().equals( "property" ) ? AccessType.PROPERTY
+							: AccessType.FIELD;
+				}
+				else {
+					throw new AssertionFailure( "Unable to guess collection property accessor name" );
+				}
+
 				if ( AnnotatedClassType.EMBEDDABLE.equals( classType ) ) {
 					EntityBinder entityBinder = new EntityBinder();
-					PersistentClass owner = mapValue.getOwner();
-					boolean isPropertyAnnotated;
-					//FIXME support @Access for collection of elements
-					//String accessType = access != null ? access.value() : null;
-					if ( owner.getIdentifierProperty() != null ) {
-						isPropertyAnnotated = owner.getIdentifierProperty()
-								.getPropertyAccessorName()
-								.equals( "property" );
-					}
-					else
-					if ( owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0 ) {
-						Property prop = (Property) owner.getIdentifierMapper().getPropertyIterator().next();
-						isPropertyAnnotated = prop.getPropertyAccessorName().equals( "property" );
-					}
-					else {
-						throw new AssertionFailure( "Unable to guess collection property accessor name" );
-					}
-
 
 					PropertyData inferredData;
 					if ( isHibernateExtensionMapping() ) {
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "index", elementClass );
 					}
 					else {
 						//"key" is the JPA 2 prefix for map keys
 						inferredData = new PropertyPreloadedData( AccessType.PROPERTY, "key", elementClass );
 					}
 
 					//TODO be smart with isNullable
 					Component component = AnnotationBinder.fillComponent(
-							holder, inferredData, isPropertyAnnotated ? AccessType.PROPERTY : AccessType.FIELD, true,
+							holder, inferredData, accessType, true,
 							entityBinder, false, false,
 							true, mappings, inheritanceStatePerClass
 					);
 					mapValue.setIndex( component );
 				}
 				else {
 					SimpleValueBinder elementBinder = new SimpleValueBinder();
 					elementBinder.setMappings( mappings );
 					elementBinder.setReturnedClassName( mapKeyType );
 
 					Ejb3Column[] elementColumns = mapKeyColumns;
 					if ( elementColumns == null || elementColumns.length == 0 ) {
 						elementColumns = new Ejb3Column[1];
 						Ejb3Column column = new Ejb3Column();
 						column.setImplicit( false );
 						column.setNullable( true );
 						column.setLength( Ejb3Column.DEFAULT_COLUMN_LENGTH );
 						column.setLogicalColumnName( Collection.DEFAULT_KEY_COLUMN_NAME );
 						//TODO create an EMPTY_JOINS collection
 						column.setJoins( new HashMap<String, Join>() );
 						column.setMappings( mappings );
 						column.bind();
 						elementColumns[0] = column;
 					}
 					//override the table
 					for (Ejb3Column column : elementColumns) {
 						column.setTable( mapValue.getCollectionTable() );
 					}
 					elementBinder.setColumns( elementColumns );
 					//do not call setType as it extract the type from @Type
 					//the algorithm generally does not apply for map key anyway
 					elementBinder.setKey(true);
 					MapKeyType mapKeyTypeAnnotation = property.getAnnotation( MapKeyType.class );
 					if ( mapKeyTypeAnnotation != null && !BinderHelper.isEmptyAnnotationValue(
 							mapKeyTypeAnnotation.value()
 									.type()
 					) ) {
 						elementBinder.setExplicitType( mapKeyTypeAnnotation.value() );
 					}
 					else {
 						elementBinder.setType( property, elementClass );
 					}
+					elementBinder.setPersistentClassName( propertyHolder.getEntityName() );
+					elementBinder.setAccessType( accessType );
 					mapValue.setIndex( elementBinder.make() );
 				}
 			}
 			//FIXME pass the Index Entity JoinColumns
 			if ( !collection.isOneToMany() ) {
 				//index column shoud not be null
 				for (Ejb3JoinColumn col : mapKeyManyToManyColumns) {
 					col.forceNotNull();
 				}
 			}
 			if ( isIndexOfEntities ) {
 				bindManytoManyInverseFk(
 						collectionEntity,
 						mapKeyManyToManyColumns,
 						element,
 						false, //a map key column has no unique constraint
 						mappings
 				);
 			}
 		}
 	}
 
 	protected Value createFormulatedValue(
 			Value value,
 			Collection collection,
 			String targetPropertyName,
 			PersistentClass associatedClass,
 			Mappings mappings) {
 		Value element = collection.getElement();
 		String fromAndWhere = null;
 		if ( !( element instanceof OneToMany ) ) {
 			String referencedPropertyName = null;
 			if ( element instanceof ToOne ) {
 				referencedPropertyName = ( (ToOne) element ).getReferencedPropertyName();
 			}
 			else if ( element instanceof DependantValue ) {
 				//TODO this never happen I think
 				if ( propertyName != null ) {
 					referencedPropertyName = collection.getReferencedPropertyName();
 				}
 				else {
 					throw new AnnotationException( "SecondaryTable JoinColumn cannot reference a non primary key" );
 				}
 			}
 			Iterator referencedEntityColumns;
 			if ( referencedPropertyName == null ) {
 				referencedEntityColumns = associatedClass.getIdentifier().getColumnIterator();
 			}
 			else {
 				Property referencedProperty = associatedClass.getRecursiveProperty( referencedPropertyName );
 				referencedEntityColumns = referencedProperty.getColumnIterator();
 			}
 			String alias = "$alias$";
 			StringBuilder fromAndWhereSb = new StringBuilder( " from " )
 					.append( associatedClass.getTable().getName() )
 							//.append(" as ") //Oracle doesn't support it in subqueries
 					.append( " " )
 					.append( alias ).append( " where " );
 			Iterator collectionTableColumns = element.getColumnIterator();
 			while ( collectionTableColumns.hasNext() ) {
 				Column colColumn = (Column) collectionTableColumns.next();
 				Column refColumn = (Column) referencedEntityColumns.next();
 				fromAndWhereSb.append( alias ).append( '.' ).append( refColumn.getQuotedName() )
 						.append( '=' ).append( colColumn.getQuotedName() ).append( " and " );
 			}
 			fromAndWhere = fromAndWhereSb.substring( 0, fromAndWhereSb.length() - 5 );
 		}
 
 		if ( value instanceof Component ) {
 			Component component = (Component) value;
 			Iterator properties = component.getPropertyIterator();
 			Component indexComponent = new Component( mappings, collection );
 			indexComponent.setComponentClassName( component.getComponentClassName() );
 			//TODO I don't know if this is appropriate
 			indexComponent.setNodeName( "index" );
 			while ( properties.hasNext() ) {
 				Property current = (Property) properties.next();
 				Property newProperty = new Property();
 				newProperty.setCascade( current.getCascade() );
 				newProperty.setGeneration( current.getGeneration() );
 				newProperty.setInsertable( false );
 				newProperty.setUpdateable( false );
 				newProperty.setMetaAttributes( current.getMetaAttributes() );
 				newProperty.setName( current.getName() );
 				newProperty.setNodeName( current.getNodeName() );
 				newProperty.setNaturalIdentifier( false );
 				//newProperty.setOptimisticLocked( false );
 				newProperty.setOptional( false );
 				newProperty.setPersistentClass( current.getPersistentClass() );
 				newProperty.setPropertyAccessorName( current.getPropertyAccessorName() );
 				newProperty.setSelectable( current.isSelectable() );
 				newProperty.setValue(
 						createFormulatedValue(
 								current.getValue(), collection, targetPropertyName, associatedClass, mappings
 						)
 				);
 				indexComponent.addProperty( newProperty );
 			}
 			return indexComponent;
 		}
 		else if ( value instanceof SimpleValue ) {
 			SimpleValue sourceValue = (SimpleValue) value;
 			SimpleValue targetValue;
 			if ( value instanceof ManyToOne ) {
 				ManyToOne sourceManyToOne = (ManyToOne) sourceValue;
 				ManyToOne targetManyToOne = new ManyToOne( mappings, collection.getCollectionTable() );
 				targetManyToOne.setFetchMode( FetchMode.DEFAULT );
 				targetManyToOne.setLazy( true );
 				//targetValue.setIgnoreNotFound( ); does not make sense for a map key
 				targetManyToOne.setReferencedEntityName( sourceManyToOne.getReferencedEntityName() );
 				targetValue = targetManyToOne;
 			}
 			else {
 				targetValue = new SimpleValue( mappings, collection.getCollectionTable() );
 				targetValue.setTypeName( sourceValue.getTypeName() );
 				targetValue.setTypeParameters( sourceValue.getTypeParameters() );
 			}
 			Iterator columns = sourceValue.getColumnIterator();
 			Random random = new Random();
 			while ( columns.hasNext() ) {
 				Object current = columns.next();
 				Formula formula = new Formula();
 				String formulaString;
 				if ( current instanceof Column ) {
 					formulaString = ( (Column) current ).getQuotedName();
 				}
 				else if ( current instanceof Formula ) {
 					formulaString = ( (Formula) current ).getFormula();
 				}
 				else {
 					throw new AssertionFailure( "Unknown element in column iterator: " + current.getClass() );
 				}
 				if ( fromAndWhere != null ) {
 					formulaString = Template.renderWhereStringTemplate( formulaString, "$alias$", new HSQLDialect() );
 					formulaString = "(select " + formulaString + fromAndWhere + ")";
 					formulaString = StringHelper.replace(
 							formulaString,
 							"$alias$",
 							"a" + random.nextInt( 16 )
 					);
 				}
 				formula.setFormula( formulaString );
 				targetValue.addFormula( formula );
 
 			}
 			return targetValue;
 		}
 		else {
 			throw new AssertionFailure( "Unknown type encounters for map key: " + value.getClass() );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
index 84d30b332c..b4251816b7 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
@@ -1,364 +1,365 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.cfg.annotations;
 
 import java.util.Map;
 import javax.persistence.EmbeddedId;
 import javax.persistence.Id;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.annotations.Generated;
 import org.hibernate.annotations.GenerationTime;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.NaturalId;
 import org.hibernate.annotations.OptimisticLock;
 import org.hibernate.annotations.common.AssertionFailure;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.InheritanceState;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.PropertyPreloadedData;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.ToOne;
 import org.hibernate.mapping.Value;
 
 /**
  * @author Emmanuel Bernard
  */
 public class PropertyBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, PropertyBinder.class.getName());
 
 	private String name;
 	private String returnedClassName;
 	private boolean lazy;
 	private AccessType accessType;
 	private Ejb3Column[] columns;
 	private PropertyHolder holder;
 	private Mappings mappings;
 	private Value value;
 	private boolean insertable = true;
 	private boolean updatable = true;
 	private String cascade;
 	private SimpleValueBinder simpleValueBinder;
 	private XClass declaringClass;
 	private boolean declaringClassSet;
 	private boolean embedded;
 	private EntityBinder entityBinder;
 	private boolean isXToMany;
 	private String referencedEntityName;
 
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
 	public void setEntityBinder(EntityBinder entityBinder) {
 		this.entityBinder = entityBinder;
 	}
 
 	/*
 			 * property can be null
 			 * prefer propertyName to property.getName() since some are overloaded
 			 */
 	private XProperty property;
 	private XClass returnedClass;
 	private boolean isId;
 	private Map<XClass, InheritanceState> inheritanceStatePerClass;
 	private Property mappingProperty;
 
 	public void setInsertable(boolean insertable) {
 		this.insertable = insertable;
 	}
 
 	public void setUpdatable(boolean updatable) {
 		this.updatable = updatable;
 	}
 
 	public void setName(String name) {
 		this.name = name;
 	}
 
 	public void setReturnedClassName(String returnedClassName) {
 		this.returnedClassName = returnedClassName;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public void setAccessType(AccessType accessType) {
 		this.accessType = accessType;
 	}
 
 	public void setColumns(Ejb3Column[] columns) {
 		insertable = columns[0].isInsertable();
 		updatable = columns[0].isUpdatable();
 		//consistency is checked later when we know the property name
 		this.columns = columns;
 	}
 
 	public void setHolder(PropertyHolder holder) {
 		this.holder = holder;
 	}
 
 	public void setValue(Value value) {
 		this.value = value;
 	}
 
 	public void setCascade(String cascadeStrategy) {
 		this.cascade = cascadeStrategy;
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	public void setDeclaringClass(XClass declaringClass) {
 		this.declaringClass = declaringClass;
 		this.declaringClassSet = true;
 	}
 
 	private void validateBind() {
 		if ( property.isAnnotationPresent( Immutable.class ) ) {
 			throw new AnnotationException(
 					"@Immutable on property not allowed. " +
 							"Only allowed on entity level or on a collection."
 			);
 		}
 		if ( !declaringClassSet ) {
 			throw new AssertionFailure( "declaringClass has not been set before a bind" );
 		}
 	}
 
 	private void validateMake() {
 		//TODO check necessary params for a make
 	}
 
 	private Property makePropertyAndValue() {
 		validateBind();
 		LOG.debugf( "MetadataSourceProcessor property %s with lazy=%s", name, lazy );
 		String containerClassName = holder == null ?
 				null :
 				holder.getClassName();
 		simpleValueBinder = new SimpleValueBinder();
 		simpleValueBinder.setMappings( mappings );
 		simpleValueBinder.setPropertyName( name );
 		simpleValueBinder.setReturnedClassName( returnedClassName );
 		simpleValueBinder.setColumns( columns );
 		simpleValueBinder.setPersistentClassName( containerClassName );
 		simpleValueBinder.setType( property, returnedClass );
 		simpleValueBinder.setMappings( mappings );
 		simpleValueBinder.setReferencedEntityName( referencedEntityName );
+		simpleValueBinder.setAccessType( accessType );
 		SimpleValue propertyValue = simpleValueBinder.make();
 		setValue( propertyValue );
 		return makeProperty();
 	}
 
 	//used when value is provided
 	public Property makePropertyAndBind() {
 		return bind( makeProperty() );
 	}
 
 	//used to build everything from scratch
 	public Property makePropertyValueAndBind() {
 		return bind( makePropertyAndValue() );
 	}
 
 	public void setXToMany(boolean xToMany) {
 		this.isXToMany = xToMany;
 	}
 
 	private Property bind(Property prop) {
 		if (isId) {
 			final RootClass rootClass = ( RootClass ) holder.getPersistentClass();
 			//if an xToMany, it as to be wrapped today.
 			//FIXME this pose a problem as the PK is the class instead of the associated class which is not really compliant with the spec
 			if ( isXToMany || entityBinder.wrapIdsInEmbeddedComponents() ) {
 				Component identifier = (Component) rootClass.getIdentifier();
 				if (identifier == null) {
 					identifier = AnnotationBinder.createComponent( holder, new PropertyPreloadedData(null, null, null), true, false, mappings );
 					rootClass.setIdentifier( identifier );
 					identifier.setNullValue( "undefined" );
 					rootClass.setEmbeddedIdentifier( true );
 					rootClass.setIdentifierMapper( identifier );
 				}
 				//FIXME is it good enough?
 				identifier.addProperty( prop );
 			}
 			else {
 				rootClass.setIdentifier( ( KeyValue ) getValue() );
 				if (embedded) {
 					rootClass.setEmbeddedIdentifier( true );
 				}
 				else {
 					rootClass.setIdentifierProperty( prop );
 					final org.hibernate.mapping.MappedSuperclass superclass = BinderHelper.getMappedSuperclassOrNull(
 							declaringClass,
 							inheritanceStatePerClass,
 							mappings
 					);
 					if (superclass != null) {
 						superclass.setDeclaredIdentifierProperty(prop);
 					}
 					else {
 						//we know the property is on the actual entity
 						rootClass.setDeclaredIdentifierProperty( prop );
 					}
 				}
 			}
 		}
 		else {
 			holder.addProperty( prop, columns, declaringClass );
 		}
 		return prop;
 	}
 
 	//used when the value is provided and the binding is done elsewhere
 	public Property makeProperty() {
 		validateMake();
 		LOG.debugf( "Building property %s", name );
 		Property prop = new Property();
 		prop.setName( name );
 		prop.setNodeName( name );
 		prop.setValue( value );
 		prop.setLazy( lazy );
 		prop.setCascade( cascade );
 		prop.setPropertyAccessorName( accessType.getType() );
 		Generated ann = property != null ?
 				property.getAnnotation( Generated.class ) :
 				null;
 		GenerationTime generated = ann != null ?
 				ann.value() :
 				null;
 		if ( generated != null ) {
 			if ( !GenerationTime.NEVER.equals( generated ) ) {
 				if ( property.isAnnotationPresent( javax.persistence.Version.class )
 						&& GenerationTime.INSERT.equals( generated ) ) {
 					throw new AnnotationException(
 							"@Generated(INSERT) on a @Version property not allowed, use ALWAYS: "
 									+ StringHelper.qualify( holder.getPath(), name )
 					);
 				}
 				insertable = false;
 				if ( GenerationTime.ALWAYS.equals( generated ) ) {
 					updatable = false;
 				}
 				prop.setGeneration( PropertyGeneration.parse( generated.toString().toLowerCase() ) );
 			}
 		}
 		NaturalId naturalId = property != null ? property.getAnnotation( NaturalId.class ) : null;
 		if ( naturalId != null ) {
 			if ( ! entityBinder.isRootEntity() ) {
 				throw new AnnotationException( "@NaturalId only valid on root entity (or its @MappedSuperclasses)" );
 			}
 			if ( ! naturalId.mutable() ) {
 				updatable = false;
 			}
 			prop.setNaturalIdentifier( true );
 		}
 		prop.setInsertable( insertable );
 		prop.setUpdateable( updatable );
 
 		// this is already handled for collections in CollectionBinder...
 		if ( Collection.class.isInstance( value ) ) {
 			prop.setOptimisticLocked( ( (Collection) value ).isOptimisticLocked() );
 		}
 		else {
 			final OptimisticLock lockAnn = property != null
 					? property.getAnnotation( OptimisticLock.class )
 					: null;
 			if ( lockAnn != null ) {
 				//TODO this should go to the core as a mapping validation checking
 				if ( lockAnn.excluded() && (
 						property.isAnnotationPresent( javax.persistence.Version.class )
 								|| property.isAnnotationPresent( Id.class )
 								|| property.isAnnotationPresent( EmbeddedId.class ) ) ) {
 					throw new AnnotationException(
 							"@OptimisticLock.exclude=true incompatible with @Id, @EmbeddedId and @Version: "
 									+ StringHelper.qualify( holder.getPath(), name )
 					);
 				}
 			}
 			final boolean isOwnedValue = !isToOneValue( value ) || insertable; // && updatable as well???
 			final boolean includeInOptimisticLockChecks = ( lockAnn != null )
 					? ! lockAnn.excluded()
 					: isOwnedValue;
 			prop.setOptimisticLocked( includeInOptimisticLockChecks );
 		}
 
 		LOG.tracev( "Cascading {0} with {1}", name, cascade );
 		this.mappingProperty = prop;
 		return prop;
 	}
 
 	private boolean isCollection(Value value) {
 		return Collection.class.isInstance( value );
 	}
 
 	private boolean isToOneValue(Value value) {
 		return ToOne.class.isInstance( value );
 	}
 
 	public void setProperty(XProperty property) {
 		this.property = property;
 	}
 
 	public void setReturnedClass(XClass returnedClass) {
 		this.returnedClass = returnedClass;
 	}
 
 	public SimpleValueBinder getSimpleValueBinder() {
 		return simpleValueBinder;
 	}
 
 	public Value getValue() {
 		return value;
 	}
 
 	public void setId(boolean id) {
 		this.isId = id;
 	}
 
 	public void setInheritanceStatePerClass(Map<XClass, InheritanceState> inheritanceStatePerClass) {
 		this.inheritanceStatePerClass = inheritanceStatePerClass;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
index 24f7219df7..1a5c7b90c8 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/SimpleValueBinder.java
@@ -1,591 +1,628 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.cfg.annotations;
 
 import java.io.Serializable;
+import java.lang.annotation.Annotation;
 import java.lang.reflect.TypeVariable;
 import java.sql.Types;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Properties;
 import javax.persistence.AttributeConverter;
 import javax.persistence.Convert;
 import javax.persistence.Converts;
 import javax.persistence.Enumerated;
 import javax.persistence.Id;
 import javax.persistence.Lob;
 import javax.persistence.MapKeyEnumerated;
 import javax.persistence.MapKeyTemporal;
 import javax.persistence.Temporal;
 import javax.persistence.TemporalType;
 
-import org.jboss.logging.Logger;
-
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
+import org.hibernate.MappingException;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Type;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.annotations.common.util.ReflectHelper;
+import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.cfg.PkDrivenByDefaultMapsIdSecondPass;
 import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.type.CharacterArrayClobType;
 import org.hibernate.type.EnumType;
 import org.hibernate.type.PrimitiveCharacterArrayClobType;
 import org.hibernate.type.SerializableToBlobType;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.WrappedMaterializedBlobType;
+import org.hibernate.usertype.DynamicParameterizedType;
+import org.jboss.logging.Logger;
 
 /**
  * @author Emmanuel Bernard
  */
 public class SimpleValueBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SimpleValueBinder.class.getName());
 
 	private String propertyName;
 	private String returnedClassName;
 	private Ejb3Column[] columns;
 	private String persistentClassName;
 	private String explicitType = "";
+	private String defaultType = "";
 	private Properties typeParameters = new Properties();
 	private Mappings mappings;
 	private Table table;
 	private SimpleValue simpleValue;
 	private boolean isVersion;
 	private String timeStampVersionType;
 	//is a Map key
 	private boolean key;
 	private String referencedEntityName;
+	private XProperty xproperty;
+	private AccessType accessType;
 
 	private AttributeConverterDefinition attributeConverterDefinition;
 
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName;
 	}
 
 	public boolean isVersion() {
 		return isVersion;
 	}
 
 	public void setVersion(boolean isVersion) {
 		this.isVersion = isVersion;
 	}
 
 	public void setTimestampVersionType(String versionType) {
 		this.timeStampVersionType = versionType;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName;
 	}
 
 	public void setReturnedClassName(String returnedClassName) {
 		this.returnedClassName = returnedClassName;
+
+		if ( defaultType.length() == 0 ) {
+			defaultType = returnedClassName;
+		}
 	}
 
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public void setColumns(Ejb3Column[] columns) {
 		this.columns = columns;
 	}
 
 
 	public void setPersistentClassName(String persistentClassName) {
 		this.persistentClassName = persistentClassName;
 	}
 
 	//TODO execute it lazily to be order safe
 
 	public void setType(XProperty property, XClass returnedClass) {
 		if ( returnedClass == null ) {
 			return;
 		} //we cannot guess anything
 		XClass returnedClassOrElement = returnedClass;
-		boolean isArray = false;
+                boolean isArray = false;
 		if ( property.isArray() ) {
 			returnedClassOrElement = property.getElementClass();
 			isArray = true;
 		}
+		this.xproperty = property;
 		Properties typeParameters = this.typeParameters;
 		typeParameters.clear();
 		String type = BinderHelper.ANNOTATION_STRING_DEFAULT;
-		if ( ( !key && property.isAnnotationPresent( Temporal.class ) )
+
+		Type annType = property.getAnnotation( Type.class );
+		if ( annType != null ) {
+			setExplicitType( annType );
+			type = explicitType;
+		}
+		else if ( ( !key && property.isAnnotationPresent( Temporal.class ) )
 				|| ( key && property.isAnnotationPresent( MapKeyTemporal.class ) ) ) {
 
 			boolean isDate;
 			if ( mappings.getReflectionManager().equals( returnedClassOrElement, Date.class ) ) {
 				isDate = true;
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Calendar.class ) ) {
 				isDate = false;
 			}
 			else {
 				throw new AnnotationException(
 						"@Temporal should only be set on a java.util.Date or java.util.Calendar property: "
 								+ StringHelper.qualify( persistentClassName, propertyName )
 				);
 			}
 			final TemporalType temporalType = getTemporalType( property );
 			switch ( temporalType ) {
 				case DATE:
 					type = isDate ? "date" : "calendar_date";
 					break;
 				case TIME:
 					type = "time";
 					if ( !isDate ) {
 						throw new NotYetImplementedException(
 								"Calendar cannot persist TIME only"
 										+ StringHelper.qualify( persistentClassName, propertyName )
 						);
 					}
 					break;
 				case TIMESTAMP:
 					type = isDate ? "timestamp" : "calendar";
 					break;
 				default:
 					throw new AssertionFailure( "Unknown temporal type: " + temporalType );
 			}
+			explicitType = type;
 		}
 		else if ( property.isAnnotationPresent( Lob.class ) ) {
 			if ( mappings.getReflectionManager().equals( returnedClassOrElement, java.sql.Clob.class ) ) {
 				type = "clob";
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, java.sql.Blob.class ) ) {
 				type = "blob";
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, String.class ) ) {
 				type = StandardBasicTypes.MATERIALIZED_CLOB.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Character.class ) && isArray ) {
 				type = CharacterArrayClobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, char.class ) && isArray ) {
 				type = PrimitiveCharacterArrayClobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, Byte.class ) && isArray ) {
 				type = WrappedMaterializedBlobType.class.getName();
 			}
 			else if ( mappings.getReflectionManager().equals( returnedClassOrElement, byte.class ) && isArray ) {
 				type = StandardBasicTypes.MATERIALIZED_BLOB.getName();
 			}
 			else if ( mappings.getReflectionManager()
 					.toXClass( Serializable.class )
 					.isAssignableFrom( returnedClassOrElement ) ) {
 				type = SerializableToBlobType.class.getName();
 				//typeParameters = new Properties();
 				typeParameters.setProperty(
 						SerializableToBlobType.CLASS_NAME,
 						returnedClassOrElement.getName()
 				);
 			}
 			else {
 				type = "blob";
 			}
+			explicitType = type;
+		}
+		else if ( ( !key && property.isAnnotationPresent( Enumerated.class ) )
+				|| ( key && property.isAnnotationPresent( MapKeyEnumerated.class ) ) ) {
+			type = EnumType.class.getName();
+			explicitType = type;
 		}
-		//implicit type will check basic types and Serializable classes
+
+		// implicit type will check basic types and Serializable classes
 		if ( columns == null ) {
 			throw new AssertionFailure( "SimpleValueBinder.setColumns should be set before SimpleValueBinder.setType" );
 		}
+
 		if ( BinderHelper.ANNOTATION_STRING_DEFAULT.equals( type ) ) {
 			if ( returnedClassOrElement.isEnum() ) {
 				type = EnumType.class.getName();
-				typeParameters = new Properties();
-				typeParameters.setProperty( EnumType.ENUM, returnedClassOrElement.getName() );
-				String schema = columns[0].getTable().getSchema();
-				schema = schema == null ? "" : schema;
-				String catalog = columns[0].getTable().getCatalog();
-				catalog = catalog == null ? "" : catalog;
-				typeParameters.setProperty( EnumType.SCHEMA, schema );
-				typeParameters.setProperty( EnumType.CATALOG, catalog );
-				typeParameters.setProperty( EnumType.TABLE, columns[0].getTable().getName() );
-				typeParameters.setProperty( EnumType.COLUMN, columns[0].getName() );
-				javax.persistence.EnumType enumType = getEnumType( property );
-				if ( enumType != null ) {
-					if ( javax.persistence.EnumType.ORDINAL.equals( enumType ) ) {
-						typeParameters.setProperty( EnumType.TYPE, String.valueOf( Types.INTEGER ) );
-					}
-					else if ( javax.persistence.EnumType.STRING.equals( enumType ) ) {
-						typeParameters.setProperty( EnumType.TYPE, String.valueOf( Types.VARCHAR ) );
-					}
-					else {
-						throw new AssertionFailure( "Unknown EnumType: " + enumType );
-					}
-				}
 			}
 		}
-		explicitType = type;
+
+		defaultType = BinderHelper.isEmptyAnnotationValue( type ) ? returnedClassName : type;
 		this.typeParameters = typeParameters;
-		Type annType = property.getAnnotation( Type.class );
-		setExplicitType( annType );
 
 		applyAttributeConverter( property );
 	}
 
 	private void applyAttributeConverter(XProperty property) {
 		final boolean canBeConverted = ! property.isAnnotationPresent( Id.class )
 				&& ! isVersion
 				&& ! isAssociation()
 				&& ! property.isAnnotationPresent( Temporal.class )
 				&& ! property.isAnnotationPresent( Enumerated.class );
 
 		if ( canBeConverted ) {
 			// @Convert annotations take precedence
 			final Convert convertAnnotation = locateConvertAnnotation( property );
 			if ( convertAnnotation != null ) {
 				if ( ! convertAnnotation.disableConversion() ) {
 					attributeConverterDefinition = mappings.locateAttributeConverter( convertAnnotation.converter() );
 				}
 			}
 			else {
 				attributeConverterDefinition = locateAutoApplyAttributeConverter( property );
 			}
 		}
 	}
 
 	private AttributeConverterDefinition locateAutoApplyAttributeConverter(XProperty property) {
 		final Class propertyType = mappings.getReflectionManager().toClass( property.getType() );
 		for ( AttributeConverterDefinition attributeConverterDefinition : mappings.getAttributeConverters() ) {
 			if ( areTypeMatch( attributeConverterDefinition.getEntityAttributeType(), propertyType ) ) {
 				return attributeConverterDefinition;
 			}
 		}
 		return null;
 	}
 
 	private boolean isAssociation() {
 		// todo : this information is only known to caller(s), need to pass that information in somehow.
 		// or, is this enough?
 		return referencedEntityName != null;
 	}
 
 	@SuppressWarnings("unchecked")
 	private Convert locateConvertAnnotation(XProperty property) {
 		// first look locally on the property for @Convert
 		Convert localConvertAnnotation = property.getAnnotation( Convert.class );
 		if ( localConvertAnnotation != null ) {
 			return localConvertAnnotation;
 		}
 
 		if ( persistentClassName == null ) {
 			LOG.debug( "Persistent Class name not known during attempt to locate @Convert annotations" );
 			return null;
 		}
 
 		final XClass owner;
 		try {
 			final Class ownerClass = ReflectHelper.classForName( persistentClassName );
 			owner = mappings.getReflectionManager().classForName( persistentClassName, ownerClass  );
 		}
 		catch (ClassNotFoundException e) {
 			throw new AnnotationException( "Unable to resolve Class reference during attempt to locate @Convert annotations" );
 		}
 
 		return lookForEntityDefinedConvertAnnotation( property, owner );
 	}
 
 	private Convert lookForEntityDefinedConvertAnnotation(XProperty property, XClass owner) {
 		if ( owner == null ) {
 			// we have hit the root of the entity hierarchy
 			return null;
 		}
 
 		{
 			Convert convertAnnotation = owner.getAnnotation( Convert.class );
 			if ( convertAnnotation != null && isMatch( convertAnnotation, property ) ) {
 				return convertAnnotation;
 			}
 		}
 
 		{
 			Converts convertsAnnotation = owner.getAnnotation( Converts.class );
 			if ( convertsAnnotation != null ) {
 				for ( Convert convertAnnotation : convertsAnnotation.value() ) {
 					if ( isMatch( convertAnnotation, property ) ) {
 						return convertAnnotation;
 					}
 				}
 			}
 		}
 
 		// finally, look on superclass
 		return lookForEntityDefinedConvertAnnotation( property, owner.getSuperclass() );
 	}
 
 	@SuppressWarnings("unchecked")
 	private boolean isMatch(Convert convertAnnotation, XProperty property) {
 		return property.getName().equals( convertAnnotation.attributeName() )
 				&& isTypeMatch( convertAnnotation.converter(), property );
 	}
 
 	private boolean isTypeMatch(Class<? extends AttributeConverter> attributeConverterClass, XProperty property) {
 		return areTypeMatch(
 				extractEntityAttributeType( attributeConverterClass ),
 				mappings.getReflectionManager().toClass( property.getType() )
 		);
 	}
 
 	private Class extractEntityAttributeType(Class<? extends AttributeConverter> attributeConverterClass) {
 		// this is duplicated in SimpleValue...
 		final TypeVariable[] attributeConverterTypeInformation = attributeConverterClass.getTypeParameters();
 		if ( attributeConverterTypeInformation == null || attributeConverterTypeInformation.length < 2 ) {
 			throw new AnnotationException(
 					"AttributeConverter [" + attributeConverterClass.getName()
 							+ "] did not retain parameterized type information"
 			);
 		}
 
 		if ( attributeConverterTypeInformation.length > 2 ) {
 			LOG.debug(
 					"AttributeConverter [" + attributeConverterClass.getName()
 							+ "] specified more than 2 parameterized types"
 			);
 		}
 		final Class entityAttributeJavaType = extractType( attributeConverterTypeInformation[0] );
 		if ( entityAttributeJavaType == null ) {
 			throw new AnnotationException(
 					"Could not determine 'entity attribute' type from given AttributeConverter [" +
 							attributeConverterClass.getName() + "]"
 			);
 		}
 		return entityAttributeJavaType;
 	}
 
 	private Class extractType(TypeVariable typeVariable) {
 		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
 		if ( boundTypes == null || boundTypes.length != 1 ) {
 			return null;
 		}
 
 		return (Class) boundTypes[0];
 	}
 
 	private boolean areTypeMatch(Class converterDefinedType, Class propertyType) {
 		if ( converterDefinedType == null ) {
 			throw new AnnotationException( "AttributeConverter defined java type cannot be null" );
 		}
 		if ( propertyType == null ) {
 			throw new AnnotationException( "Property defined java type cannot be null" );
 		}
 
 		return converterDefinedType.equals( propertyType )
 				|| arePrimitiveWrapperEquivalents( converterDefinedType, propertyType );
 	}
 
 	private boolean arePrimitiveWrapperEquivalents(Class converterDefinedType, Class propertyType) {
 		if ( converterDefinedType.isPrimitive() ) {
 			return getWrapperEquivalent( converterDefinedType ).equals( propertyType );
 		}
 		else if ( propertyType.isPrimitive() ) {
 			return getWrapperEquivalent( propertyType ).equals( converterDefinedType );
 		}
 		return false;
 	}
 
 	private static Class getWrapperEquivalent(Class primitive) {
 		if ( ! primitive.isPrimitive() ) {
 			throw new AssertionFailure( "Passed type for which to locate wrapper equivalent was not a primitive" );
 		}
 
 		if ( boolean.class.equals( primitive ) ) {
 			return Boolean.class;
 		}
 		else if ( char.class.equals( primitive ) ) {
 			return Character.class;
 		}
 		else if ( byte.class.equals( primitive ) ) {
 			return Byte.class;
 		}
 		else if ( short.class.equals( primitive ) ) {
 			return Short.class;
 		}
 		else if ( int.class.equals( primitive ) ) {
 			return Integer.class;
 		}
 		else if ( long.class.equals( primitive ) ) {
 			return Long.class;
 		}
 		else if ( float.class.equals( primitive ) ) {
 			return Float.class;
 		}
 		else if ( double.class.equals( primitive ) ) {
 			return Double.class;
 		}
 
 		throw new AssertionFailure( "Unexpected primitive type (VOID most likely) passed to getWrapperEquivalent" );
 	}
-
-	private javax.persistence.EnumType getEnumType(XProperty property) {
-		javax.persistence.EnumType enumType = null;
-		if ( key ) {
-			MapKeyEnumerated enumAnn = property.getAnnotation( MapKeyEnumerated.class );
-			if ( enumAnn != null ) {
-				enumType = enumAnn.value();
-			}
-		}
-		else {
-			Enumerated enumAnn = property.getAnnotation( Enumerated.class );
-			if ( enumAnn != null ) {
-				enumType = enumAnn.value();
-			}
-		}
-		return enumType;
-	}
-
+	
 	private TemporalType getTemporalType(XProperty property) {
 		if ( key ) {
 			MapKeyTemporal ann = property.getAnnotation( MapKeyTemporal.class );
 			return ann.value();
 		}
 		else {
 			Temporal ann = property.getAnnotation( Temporal.class );
 			return ann.value();
 		}
 	}
 
 	public void setExplicitType(String explicitType) {
 		this.explicitType = explicitType;
 	}
 
 	//FIXME raise an assertion failure  if setResolvedTypeMapping(String) and setResolvedTypeMapping(Type) are use at the same time
 
 	public void setExplicitType(Type typeAnn) {
 		if ( typeAnn != null ) {
 			explicitType = typeAnn.type();
 			typeParameters.clear();
 			for ( Parameter param : typeAnn.parameters() ) {
 				typeParameters.setProperty( param.name(), param.value() );
 			}
 		}
 	}
 
 	public void setMappings(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	private void validate() {
 		//TODO check necessary params
 		Ejb3Column.checkPropertyConsistency( columns, propertyName );
 	}
 
 	public SimpleValue make() {
 
 		validate();
 		LOG.debugf( "building SimpleValue for %s", propertyName );
 		if ( table == null ) {
 			table = columns[0].getTable();
 		}
 		simpleValue = new SimpleValue( mappings, table );
 
 		linkWithValue();
 
 		boolean isInSecondPass = mappings.isInSecondPass();
 		SetSimpleValueTypeSecondPass secondPass = new SetSimpleValueTypeSecondPass( this );
 		if ( !isInSecondPass ) {
 			//Defer this to the second pass
 			mappings.addSecondPass( secondPass );
 		}
 		else {
 			//We are already in second pass
 			fillSimpleValue();
 		}
 		return simpleValue;
 	}
 
 	public void linkWithValue() {
 		if ( columns[0].isNameDeferred() && !mappings.isInSecondPass() && referencedEntityName != null ) {
 			mappings.addSecondPass(
 					new PkDrivenByDefaultMapsIdSecondPass(
 							referencedEntityName, ( Ejb3JoinColumn[] ) columns, simpleValue
 					)
 			);
 		}
 		else {
 			for ( Ejb3Column column : columns ) {
 				column.linkWithValue( simpleValue );
 			}
 		}
 	}
 
 	public void fillSimpleValue() {
 		LOG.debugf( "Setting SimpleValue typeName for %s", propertyName );
-
+                
 		if ( attributeConverterDefinition != null ) {
 			if ( ! BinderHelper.isEmptyAnnotationValue( explicitType ) ) {
 				throw new AnnotationException(
 						String.format(
 								"AttributeConverter and explicit Type cannot be applied to same attribute [%s.%s];" +
 										"remove @Type or specify @Convert(disableConversion = true)",
 								persistentClassName,
 								propertyName
 						)
 				);
 			}
 			simpleValue.setJpaAttributeConverterDefinition( attributeConverterDefinition );
 		}
 		else {
-			String type = BinderHelper.isEmptyAnnotationValue( explicitType ) ? returnedClassName : explicitType;
-			org.hibernate.mapping.TypeDef typeDef = mappings.getTypeDef( type );
+			String type;
+			org.hibernate.mapping.TypeDef typeDef;
+
+			if ( !BinderHelper.isEmptyAnnotationValue( explicitType ) ) {
+				type = explicitType;
+				typeDef = mappings.getTypeDef( type );
+			}
+			else {
+				// try implicit type
+				org.hibernate.mapping.TypeDef implicitTypeDef = mappings.getTypeDef( returnedClassName );
+				if ( implicitTypeDef != null ) {
+					typeDef = implicitTypeDef;
+					type = returnedClassName;
+				}
+				else {
+					typeDef = mappings.getTypeDef( defaultType );
+					type = defaultType;
+				}
+			}
+
 			if ( typeDef != null ) {
 				type = typeDef.getTypeClass();
 				simpleValue.setTypeParameters( typeDef.getParameters() );
 			}
 			if ( typeParameters != null && typeParameters.size() != 0 ) {
 				//explicit type params takes precedence over type def params
 				simpleValue.setTypeParameters( typeParameters );
 			}
 			simpleValue.setTypeName( type );
 		}
 
 		if ( persistentClassName != null || attributeConverterDefinition != null ) {
 			simpleValue.setTypeUsingReflection( persistentClassName, propertyName );
 		}
 
 		if ( !simpleValue.isTypeSpecified() && isVersion() ) {
 			simpleValue.setTypeName( "integer" );
 		}
 
 		// HHH-5205
 		if ( timeStampVersionType != null ) {
 			simpleValue.setTypeName( timeStampVersionType );
 		}
+		
+		if ( simpleValue.getTypeName() != null && simpleValue.getTypeName().length() > 0
+				&& simpleValue.getMappings().getTypeResolver().basic( simpleValue.getTypeName() ) == null ) {
+			try {
+				Class typeClass = ReflectHelper.classForName( simpleValue.getTypeName() );
+
+				if ( typeClass != null && DynamicParameterizedType.class.isAssignableFrom( typeClass ) ) {
+					Properties parameters = simpleValue.getTypeParameters();
+					if ( parameters == null ) {
+						parameters = new Properties();
+					}
+					parameters.put( DynamicParameterizedType.IS_DYNAMIC, Boolean.toString( true ) );
+					parameters.put( DynamicParameterizedType.RETURNED_CLASS, returnedClassName );
+					parameters.put( DynamicParameterizedType.IS_PRIMARY_KEY, Boolean.toString( key ) );
+
+					parameters.put( DynamicParameterizedType.ENTITY, persistentClassName );
+					parameters.put( DynamicParameterizedType.PROPERTY, xproperty.getName() );
+					parameters.put( DynamicParameterizedType.ACCESS_TYPE, accessType.getType() );
+					simpleValue.setTypeParameters( parameters );
+				}
+			}
+			catch ( ClassNotFoundException cnfe ) {
+				throw new MappingException( "Could not determine type for: " + simpleValue.getTypeName(), cnfe );
+			}
+		}
+
 	}
 
 	public void setKey(boolean key) {
 		this.key = key;
 	}
 
-}
+	public AccessType getAccessType() {
+		return accessType;
+	}
+
+	public void setAccessType(AccessType accessType) {
+		this.accessType = accessType;
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
index 38d3d3b480..c6cde9d0af 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/SimpleValue.java
@@ -1,513 +1,615 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.mapping;
 
+import java.lang.annotation.Annotation;
+import java.lang.reflect.Field;
 import javax.persistence.AttributeConverter;
 import java.lang.reflect.TypeVariable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
-import org.hibernate.AnnotationException;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
+import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AttributeConverterDefinition;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.IdentityGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.property.DirectPropertyAccessor;
 import org.hibernate.type.AbstractSingleColumnStandardBasicType;
 import org.hibernate.type.Type;
 import org.hibernate.type.descriptor.ValueBinder;
 import org.hibernate.type.descriptor.ValueExtractor;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
 import org.hibernate.type.descriptor.sql.BasicBinder;
 import org.hibernate.type.descriptor.sql.BasicExtractor;
 import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptorRegistry;
+import org.hibernate.usertype.DynamicParameterizedType;
 
 /**
  * Any value that maps to columns.
  * @author Gavin King
  */
 public class SimpleValue implements KeyValue {
 	private static final Logger log = Logger.getLogger( SimpleValue.class );
 
 	public static final String DEFAULT_ID_GEN_STRATEGY = "assigned";
 
 	private final Mappings mappings;
 
 	private final List columns = new ArrayList();
 	private String typeName;
 	private Properties identifierGeneratorProperties;
 	private String identifierGeneratorStrategy = DEFAULT_ID_GEN_STRATEGY;
 	private String nullValue;
 	private Table table;
 	private String foreignKeyName;
 	private boolean alternateUniqueKey;
 	private Properties typeParameters;
 	private boolean cascadeDeleteEnabled;
 
 	private AttributeConverterDefinition jpaAttributeConverterDefinition;
 	private Type type;
 
 	public SimpleValue(Mappings mappings) {
 		this.mappings = mappings;
 	}
 
 	public SimpleValue(Mappings mappings, Table table) {
 		this( mappings );
 		this.table = table;
 	}
 
 	public Mappings getMappings() {
 		return mappings;
 	}
 
 	public boolean isCascadeDeleteEnabled() {
 		return cascadeDeleteEnabled;
 	}
 
 	public void setCascadeDeleteEnabled(boolean cascadeDeleteEnabled) {
 		this.cascadeDeleteEnabled = cascadeDeleteEnabled;
 	}
 	
 	public void addColumn(Column column) {
 		if ( !columns.contains(column) ) columns.add(column);
 		column.setValue(this);
 		column.setTypeIndex( columns.size()-1 );
 	}
 	
 	public void addFormula(Formula formula) {
 		columns.add(formula);
 	}
 	
 	public boolean hasFormula() {
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			Object o = iter.next();
 			if (o instanceof Formula) return true;
 		}
 		return false;
 	}
 
 	public int getColumnSpan() {
 		return columns.size();
 	}
 	public Iterator getColumnIterator() {
 		return columns.iterator();
 	}
 	public List getConstraintColumns() {
 		return columns;
 	}
 	public String getTypeName() {
 		return typeName;
 	}
 	public void setTypeName(String type) {
 		this.typeName = type;
 	}
 	public void setTable(Table table) {
 		this.table = table;
 	}
 
 	public void createForeignKey() throws MappingException {}
 
 	public void createForeignKeyOfEntity(String entityName) {
 		if ( !hasFormula() && !"none".equals(getForeignKeyName())) {
 			ForeignKey fk = table.createForeignKey( getForeignKeyName(), getConstraintColumns(), entityName );
 			fk.setCascadeDeleteEnabled(cascadeDeleteEnabled);
 		}
 	}
 
 	public IdentifierGenerator createIdentifierGenerator(
 			IdentifierGeneratorFactory identifierGeneratorFactory,
 			Dialect dialect, 
 			String defaultCatalog, 
 			String defaultSchema, 
 			RootClass rootClass) throws MappingException {
 		
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
 				if ( iter.hasNext() ) tables.append(", ");
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
 		params.put(
 				Environment.PREFER_POOLED_VALUES_LO,
 				mappings.getConfigurationProperties().getProperty( Environment.PREFER_POOLED_VALUES_LO, "false" )
 		);
 
 		identifierGeneratorFactory.setDialect( dialect );
 		return identifierGeneratorFactory.createIdentifierGenerator( identifierGeneratorStrategy, getType(), params );
 		
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
 		return identifierGeneratorFactory.getIdentifierGeneratorClass( identifierGeneratorStrategy )
 				.equals( IdentityGenerator.class );
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
 
 	public boolean isAlternateUniqueKey() {
 		return alternateUniqueKey;
 	}
 
 	public void setAlternateUniqueKey(boolean unique) {
 		this.alternateUniqueKey = unique;
 	}
 
 	public boolean isNullable() {
 		if ( hasFormula() ) return true;
 		boolean nullable = true;
 		Iterator iter = getColumnIterator();
 		while ( iter.hasNext() ) {
 			if ( !( (Column) iter.next() ).isNullable() ) {
 				nullable = false;
 				return nullable; //shortcut
 			}
 		}
 		return nullable;
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
+		if ( typeParameters != null
+				&& Boolean.valueOf( typeParameters.getProperty( DynamicParameterizedType.IS_DYNAMIC ) )
+				&& typeParameters.get( DynamicParameterizedType.PARAMETER_TYPE ) == null ) {
+			createParameterImpl();
+		}
 
 		Type result = mappings.getTypeResolver().heuristicType( typeName, typeParameters );
 		if ( result == null ) {
 			String msg = "Could not determine type for: " + typeName;
 			if ( table != null ) {
 				msg += ", at table: " + table.getName();
 			}
-			if ( columns!=null && columns.size()>0 ) {
+			if ( columns != null && columns.size() > 0 ) {
 				msg += ", for columns: " + columns;
 			}
 			throw new MappingException( msg );
 		}
 
 		return result;
 	}
 
 	@SuppressWarnings("unchecked")
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
 
 		if ( jpaAttributeConverterDefinition == null ) {
 			// this is here to work like legacy.  This should change when we integrate with metamodel to
 			// look for SqlTypeDescriptor and JavaTypeDescriptor individually and create the BasicType (well, really
 			// keep a registry of [SqlTypeDescriptor,JavaTypeDescriptor] -> BasicType...)
 			if ( className == null ) {
 				throw new MappingException( "you must specify types for a dynamic entity: " + propertyName );
 			}
 			typeName = ReflectHelper.reflectedPropertyClass( className, propertyName ).getName();
 			return;
 		}
 
 		// we had an AttributeConverter...
 
 		// todo : we should validate the number of columns present
 		// todo : ultimately I want to see attributeConverterJavaType and attributeConverterJdbcTypeCode specify-able separately
 		//		then we can "play them against each other" in terms of determining proper typing
 		// todo : see if we already have previously built a custom on-the-fly BasicType for this AttributeConverter; see note below about caching
 
 		// AttributeConverter works totally in memory, meaning it converts between one Java representation (the entity
 		// attribute representation) and another (the value bound into JDBC statements or extracted from results).
 		// However, the Hibernate Type system operates at the lower level of actually dealing with those JDBC objects.
 		// So even though we have an AttributeConverter, we still need to "fill out" the rest of the BasicType
 		// data.  For the JavaTypeDescriptor portion we simply resolve the "entity attribute representation" part of
 		// the AttributeConverter to resolve the corresponding descriptor.  For the SqlTypeDescriptor portion we use the
 		// "database column representation" part of the AttributeConverter to resolve the "recommended" JDBC type-code
 		// and use that type-code to resolve the SqlTypeDescriptor to use.
 		final Class entityAttributeJavaType = jpaAttributeConverterDefinition.getEntityAttributeType();
 		final Class databaseColumnJavaType = jpaAttributeConverterDefinition.getDatabaseColumnType();
 		final int jdbcTypeCode = JdbcTypeJavaClassMappings.INSTANCE.determineJdbcTypeCodeForJavaClass( databaseColumnJavaType );
 
 		final JavaTypeDescriptor javaTypeDescriptor = JavaTypeDescriptorRegistry.INSTANCE.getDescriptor( entityAttributeJavaType );
 		final SqlTypeDescriptor sqlTypeDescriptor = SqlTypeDescriptorRegistry.INSTANCE.getDescriptor( jdbcTypeCode );
 		// the adapter here injects the AttributeConverter calls into the binding/extraction process...
 		final SqlTypeDescriptor sqlTypeDescriptorAdapter = new AttributeConverterSqlTypeDescriptorAdapter(
 				jpaAttributeConverterDefinition.getAttributeConverter(),
 				sqlTypeDescriptor
 		);
 
 		final String name = "BasicType adapter for AttributeConverter<" + entityAttributeJavaType + "," + databaseColumnJavaType + ">";
 		type = new AbstractSingleColumnStandardBasicType( sqlTypeDescriptorAdapter, javaTypeDescriptor ) {
 			@Override
 			public String getName() {
 				return name;
 			}
 		};
 		log.debug( "Created : " + name );
 
 		// todo : cache the BasicType we just created in case that AttributeConverter is applied multiple times.
 	}
 
 	private Class extractType(TypeVariable typeVariable) {
 		java.lang.reflect.Type[] boundTypes = typeVariable.getBounds();
 		if ( boundTypes == null || boundTypes.length != 1 ) {
 			return null;
 		}
 
 		return (Class) boundTypes[0];
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
 
 	public void setJpaAttributeConverterDefinition(AttributeConverterDefinition jpaAttributeConverterDefinition) {
 		this.jpaAttributeConverterDefinition = jpaAttributeConverterDefinition;
 	}
 
 	public static class AttributeConverterSqlTypeDescriptorAdapter implements SqlTypeDescriptor {
 		private final AttributeConverter converter;
 		private final SqlTypeDescriptor delegate;
 
 		public AttributeConverterSqlTypeDescriptorAdapter(AttributeConverter converter, SqlTypeDescriptor delegate) {
 			this.converter = converter;
 			this.delegate = delegate;
 		}
 
 		@Override
 		public int getSqlType() {
 			return delegate.getSqlType();
 		}
 
 		@Override
 		public boolean canBeRemapped() {
 			return delegate.canBeRemapped();
 		}
 
 		@Override
 		public <X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor) {
 			final ValueBinder realBinder = delegate.getBinder( javaTypeDescriptor );
 			return new BasicBinder<X>( javaTypeDescriptor, this ) {
 				@Override
 				@SuppressWarnings("unchecked")
 				protected void doBind(PreparedStatement st, X value, int index, WrapperOptions options)
 						throws SQLException {
 					realBinder.bind( st, converter.convertToDatabaseColumn( value ), index, options );
 				}
 			};
 		}
 
 		@Override
 		public <X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor) {
 			final ValueExtractor realExtractor = delegate.getExtractor( javaTypeDescriptor );
 			return new BasicExtractor<X>( javaTypeDescriptor, this ) {
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( rs, name, options ) );
 				}
 
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(CallableStatement statement, int index, WrapperOptions options)
 						throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, index, options ) );
 				}
 
 				@Override
 				@SuppressWarnings("unchecked")
 				protected X doExtract(CallableStatement statement, String name, WrapperOptions options) throws SQLException {
 					return (X) converter.convertToEntityAttribute( realExtractor.extract( statement, new String[] {name}, options ) );
 				}
 			};
 		}
 	}
-}
+
+	private void createParameterImpl() {
+		try {
+			String[] columnsNames = new String[columns.size()];
+			for ( int i = 0; i < columns.size(); i++ ) {
+				columnsNames[i] = ( (Column) columns.get( i ) ).getName();
+			}
+
+			AccessType accessType = AccessType.getAccessStrategy( typeParameters
+					.getProperty( DynamicParameterizedType.ACCESS_TYPE ) );
+			final Class classEntity = ReflectHelper.classForName( typeParameters
+					.getProperty( DynamicParameterizedType.ENTITY ) );
+			final String propertyName = typeParameters.getProperty( DynamicParameterizedType.PROPERTY );
+
+			Annotation[] annotations;
+			if ( accessType == AccessType.FIELD ) {
+				annotations = ( (Field) new DirectPropertyAccessor().getGetter( classEntity, propertyName ).getMember() )
+						.getAnnotations();
+
+			}
+			else {
+				annotations = ReflectHelper.getGetter( classEntity, propertyName ).getMethod().getAnnotations();
+			}
+
+			typeParameters.put(
+					DynamicParameterizedType.PARAMETER_TYPE,
+					new ParameterTypeImpl( ReflectHelper.classForName( typeParameters
+							.getProperty( DynamicParameterizedType.RETURNED_CLASS ) ), annotations, table.getCatalog(),
+							table.getSchema(), table.getName(), Boolean.valueOf( typeParameters
+									.getProperty( DynamicParameterizedType.IS_PRIMARY_KEY ) ), columnsNames ) );
+
+		}
+		catch ( ClassNotFoundException cnfe ) {
+			throw new MappingException( "Could not create DynamicParameterizedType for type: " + typeName, cnfe );
+		}
+	}
+
+	private final class ParameterTypeImpl implements DynamicParameterizedType.ParameterType {
+
+		private final Class returnedClass;
+		private final Annotation[] annotationsMethod;
+		private final String catalog;
+		private final String schema;
+		private final String table;
+		private final boolean primaryKey;
+		private final String[] columns;
+
+		private ParameterTypeImpl(Class returnedClass, Annotation[] annotationsMethod, String catalog, String schema,
+				String table, boolean primaryKey, String[] columns) {
+			this.returnedClass = returnedClass;
+			this.annotationsMethod = annotationsMethod;
+			this.catalog = catalog;
+			this.schema = schema;
+			this.table = table;
+			this.primaryKey = primaryKey;
+			this.columns = columns;
+		}
+
+		@Override
+		public Class getReturnedClass() {
+			return returnedClass;
+		}
+
+		@Override
+		public Annotation[] getAnnotationsMethod() {
+			return annotationsMethod;
+		}
+
+		@Override
+		public String getCatalog() {
+			return catalog;
+		}
+
+		@Override
+		public String getSchema() {
+			return schema;
+		}
+
+		@Override
+		public String getTable() {
+			return table;
+		}
+
+		@Override
+		public boolean isPrimaryKey() {
+			return primaryKey;
+		}
+
+		@Override
+		public String[] getColumns() {
+			return columns;
+		}
+	}
+}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/type/EnumType.java b/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
index 7c87540def..809948c80f 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EnumType.java
@@ -1,237 +1,285 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Middleware LLC.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
+import java.lang.annotation.Annotation;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.sql.Types;
 import java.util.Properties;
-
-import org.jboss.logging.Logger;
-
+import javax.persistence.Enumerated;
+import javax.persistence.MapKeyEnumerated;
+import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.usertype.DynamicParameterizedType;
 import org.hibernate.usertype.EnhancedUserType;
-import org.hibernate.usertype.ParameterizedType;
+import org.jboss.logging.Logger;
 
 /**
  * Enum type mapper
  * Try and find the appropriate SQL type depending on column metadata
  * <p/>
  * TODO implements readobject/writeobject to recalculate the enumclasses
  *
  * @author Emmanuel Bernard
  * @author Hardy Ferentschik
  */
 @SuppressWarnings("unchecked")
-public class EnumType implements EnhancedUserType, ParameterizedType, Serializable {
+public class EnumType implements EnhancedUserType, DynamicParameterizedType, Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EnumType.class.getName());
 
 	public static final String ENUM = "enumClass";
 	public static final String SCHEMA = "schema";
 	public static final String CATALOG = "catalog";
 	public static final String TABLE = "table";
 	public static final String COLUMN = "column";
 	public static final String TYPE = "type";
 
 	private Class<? extends Enum> enumClass;
 	private transient Object[] enumValues;
 	private int sqlType = Types.INTEGER; //before any guessing
 
 	public int[] sqlTypes() {
 		return new int[] { sqlType };
 	}
 
 	public Class<? extends Enum> returnedClass() {
 		return enumClass;
 	}
 
 	public boolean equals(Object x, Object y) throws HibernateException {
 		return x == y;
 	}
 
 	public int hashCode(Object x) throws HibernateException {
 		return x == null ? 0 : x.hashCode();
 	}
 
 
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
 		Object object = rs.getObject( names[0] );
 		if ( rs.wasNull() ) {
 			if ( LOG.isTraceEnabled() ) LOG.tracev( "Returning null as column {0}", names[0] );
 			return null;
 		}
 		if ( object instanceof Number ) {
 			initEnumValues();
 			int ordinal = ( ( Number ) object ).intValue();
             if (ordinal < 0 || ordinal >= enumValues.length) throw new IllegalArgumentException("Unknown ordinal value for enum "
                                                                                                 + enumClass + ": " + ordinal);
 			if ( LOG.isTraceEnabled() ) LOG.tracev( "Returning '{0}' as column {1}", ordinal, names[0] );
 			return enumValues[ordinal];
 		}
 		else {
 			String name = ( String ) object;
 			if ( LOG.isTraceEnabled() ) LOG.tracev( "Returning '{0}' as column {1}", name, names[0] );
 			try {
 				return Enum.valueOf( enumClass, name );
 			}
 			catch ( IllegalArgumentException iae ) {
 				throw new IllegalArgumentException( "Unknown name value for enum " + enumClass + ": " + name, iae );
 			}
 		}
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
 		if ( value == null ) {
 			if ( LOG.isTraceEnabled() ) LOG.tracev( "Binding null to parameter: {0}", index );
 			st.setNull( index, sqlType );
 		}
 		else {
 			boolean isOrdinal = isOrdinal( sqlType );
 			if ( isOrdinal ) {
 				int ordinal = ( ( Enum<?> ) value ).ordinal();
 				if ( LOG.isTraceEnabled() ) LOG.tracev( "Binding '{0}' to parameter: '{1}", ordinal, index );
 				st.setObject( index, Integer.valueOf( ordinal ), sqlType );
 			}
 			else {
 				String enumString = ( ( Enum<?> ) value ).name();
 				if ( LOG.isTraceEnabled() ) LOG.tracev( "Binding '{0}' to parameter: {1}", enumString, index );
 				st.setObject( index, enumString, sqlType );
 			}
 		}
 	}
 
 	private boolean isOrdinal(int paramType) {
 		switch ( paramType ) {
 			case Types.INTEGER:
 			case Types.NUMERIC:
 			case Types.SMALLINT:
 			case Types.TINYINT:
 			case Types.BIGINT:
 			case Types.DECIMAL: //for Oracle Driver
 			case Types.DOUBLE:  //for Oracle Driver
 			case Types.FLOAT:   //for Oracle Driver
 				return true;
 			case Types.CHAR:
 			case Types.LONGVARCHAR:
 			case Types.VARCHAR:
 				return false;
 			default:
 				throw new HibernateException( "Unable to persist an Enum in a column of SQL Type: " + paramType );
 		}
 	}
 
 	public Object deepCopy(Object value) throws HibernateException {
 		return value;
 	}
 
 	public boolean isMutable() {
 		return false;
 	}
 
 	public Serializable disassemble(Object value) throws HibernateException {
 		return ( Serializable ) value;
 	}
 
 	public Object assemble(Serializable cached, Object owner) throws HibernateException {
 		return cached;
 	}
 
 	public Object replace(Object original, Object target, Object owner) throws HibernateException {
 		return original;
 	}
 
 	public void setParameterValues(Properties parameters) {
-		String enumClassName = parameters.getProperty( ENUM );
-		try {
-			enumClass = ReflectHelper.classForName( enumClassName, this.getClass() ).asSubclass( Enum.class );
-		}
-		catch ( ClassNotFoundException exception ) {
-			throw new HibernateException( "Enum class not found", exception );
+		ParameterType reader = (ParameterType) parameters.get( PARAMETER_TYPE );
+
+		if ( reader != null ) {
+			enumClass = reader.getReturnedClass().asSubclass( Enum.class );
+
+			javax.persistence.EnumType enumType = getEnumType( reader );
+			if ( enumType != null ) {
+				if ( javax.persistence.EnumType.ORDINAL.equals( enumType ) ) {
+					sqlType = Types.INTEGER;
+				}
+				else if ( javax.persistence.EnumType.STRING.equals( enumType ) ) {
+					sqlType = Types.VARCHAR;
+				}
+				else {
+					throw new AssertionFailure( "Unknown EnumType: " + enumType );
+				}
+			}
 		}
+		else {
+			String enumClassName = (String) parameters.get( ENUM );
+			try {
+				enumClass = ReflectHelper.classForName( enumClassName, this.getClass() ).asSubclass( Enum.class );
+			}
+			catch ( ClassNotFoundException exception ) {
+				throw new HibernateException( "Enum class not found", exception );
+			}
 
-		String type = parameters.getProperty( TYPE );
-		if ( type != null ) {
-			sqlType = Integer.decode( type );
+			String type = (String) parameters.get( TYPE );
+			if ( type != null ) {
+				sqlType = Integer.decode( type );
+			}
 		}
 	}
 
 	/**
 	 * Lazy init of {@link #enumValues}.
 	 */
 	private void initEnumValues() {
 		if ( enumValues == null ) {
 			this.enumValues = enumClass.getEnumConstants();
 			if ( enumValues == null ) {
 				throw new NullPointerException( "Failed to init enumValues" );
 			}
 		}
 	}
 
 	public String objectToSQLString(Object value) {
 		boolean isOrdinal = isOrdinal( sqlType );
 		if ( isOrdinal ) {
 			int ordinal = ( ( Enum ) value ).ordinal();
 			return Integer.toString( ordinal );
 		}
 		else {
 			return '\'' + ( ( Enum ) value ).name() + '\'';
 		}
 	}
 
 	public String toXMLString(Object value) {
 		boolean isOrdinal = isOrdinal( sqlType );
 		if ( isOrdinal ) {
 			int ordinal = ( ( Enum ) value ).ordinal();
 			return Integer.toString( ordinal );
 		}
 		else {
 			return ( ( Enum ) value ).name();
 		}
 	}
 
 	public Object fromXMLString(String xmlValue) {
 		try {
 			int ordinal = Integer.parseInt( xmlValue );
 			initEnumValues();
 			if ( ordinal < 0 || ordinal >= enumValues.length ) {
 				throw new IllegalArgumentException( "Unknown ordinal value for enum " + enumClass + ": " + ordinal );
 			}
 			return enumValues[ordinal];
 		}
 		catch ( NumberFormatException e ) {
 			try {
 				return Enum.valueOf( enumClass, xmlValue );
 			}
 			catch ( IllegalArgumentException iae ) {
 				throw new IllegalArgumentException( "Unknown name value for enum " + enumClass + ": " + xmlValue, iae );
 			}
 		}
 	}
+
+	private javax.persistence.EnumType getEnumType(ParameterType reader) {
+		javax.persistence.EnumType enumType = null;
+		if ( reader.isPrimaryKey() ) {
+			MapKeyEnumerated enumAnn = getAnnotation( reader.getAnnotationsMethod(), MapKeyEnumerated.class );
+			if ( enumAnn != null ) {
+				enumType = enumAnn.value();
+			}
+		}
+		else {
+			Enumerated enumAnn = getAnnotation( reader.getAnnotationsMethod(), Enumerated.class );
+			if ( enumAnn != null ) {
+				enumType = enumAnn.value();
+			}
+		}
+		return enumType;
+	}
+
+	private <T extends Annotation> T getAnnotation(Annotation[] annotations, Class<T> anClass) {
+		for ( Annotation annotation : annotations ) {
+			if ( anClass.isInstance( annotation ) ) {
+				return (T) annotation;
+			}
+		}
+		return null;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/usertype/DynamicParameterizedType.java b/hibernate-core/src/main/java/org/hibernate/usertype/DynamicParameterizedType.java
new file mode 100644
index 0000000000..6b072c3611
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/usertype/DynamicParameterizedType.java
@@ -0,0 +1,45 @@
+package org.hibernate.usertype;
+
+import java.lang.annotation.Annotation;
+
+/**
+ * {@inheritDoc}
+ * 
+ * Types who implements this interface will have in the setParameterValues an
+ * instance of the class DynamicParameterizedType$ParameterType instead of
+ * the key PARAMETER_TYPE = "org.hibernate.type.ParameterType"
+ * 
+ * The interface ParameterType provides some methods to read information
+ * dynamically for build the type
+ * 
+ * @author Janario Oliveira
+ */
+public interface DynamicParameterizedType extends ParameterizedType {
+	public static final String PARAMETER_TYPE = "org.hibernate.type.ParameterType";
+
+	public static final String IS_DYNAMIC = "org.hibernate.type.ParameterType.dynamic";
+
+	public static final String RETURNED_CLASS = "org.hibernate.type.ParameterType.returnedClass";
+	public static final String IS_PRIMARY_KEY = "org.hibernate.type.ParameterType.primaryKey";
+	public static final String ENTITY = "org.hibernate.type.ParameterType.entityClass";
+	public static final String PROPERTY = "org.hibernate.type.ParameterType.propertyName";
+	public static final String ACCESS_TYPE = "org.hibernate.type.ParameterType.accessType";
+
+	public static interface ParameterType {
+
+		public Class getReturnedClass();
+
+		public Annotation[] getAnnotationsMethod();
+
+		public String getCatalog();
+
+		public String getSchema();
+
+		public String getTable();
+
+		public boolean isPrimaryKey();
+
+		public String[] getColumns();
+
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/EntityEnum.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/EntityEnum.java
new file mode 100644
index 0000000000..8016c10abc
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/EntityEnum.java
@@ -0,0 +1,93 @@
+package org.hibernate.test.annotations.enumerated;
+
+import javax.persistence.Entity;
+import javax.persistence.EnumType;
+import javax.persistence.Enumerated;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import org.hibernate.annotations.Type;
+import org.hibernate.annotations.TypeDef;
+import org.hibernate.annotations.TypeDefs;
+
+/**
+ * @author Janario Oliveira
+ */
+@Entity
+@TypeDefs({ @TypeDef(typeClass = LastNumberType.class, defaultForType = EntityEnum.LastNumber.class) })
+public class EntityEnum {
+
+	enum Common {
+
+		A1, A2, B1, B2
+	}
+
+	enum FirstLetter {
+
+		A_LETTER, B_LETTER, C_LETTER
+	}
+
+	enum LastNumber {
+
+		NUMBER_1, NUMBER_2, NUMBER_3
+	}
+
+	@Id
+	@GeneratedValue
+	private long id;
+	private Common ordinal;
+	@Enumerated(EnumType.STRING)
+	private Common string;
+	@Type(type = "org.hibernate.test.annotations.enumerated.FirstLetterType")
+	private FirstLetter firstLetter;
+	private LastNumber lastNumber;
+	@Enumerated(EnumType.STRING)
+	private LastNumber explicitOverridingImplicit;
+
+	public long getId() {
+		return id;
+	}
+
+	public void setId(long id) {
+		this.id = id;
+	}
+
+	public Common getOrdinal() {
+		return ordinal;
+	}
+
+	public void setOrdinal(Common ordinal) {
+		this.ordinal = ordinal;
+	}
+
+	public Common getString() {
+		return string;
+	}
+
+	public void setString(Common string) {
+		this.string = string;
+	}
+
+	public FirstLetter getFirstLetter() {
+		return firstLetter;
+	}
+
+	public void setFirstLetter(FirstLetter firstLetter) {
+		this.firstLetter = firstLetter;
+	}
+
+	public LastNumber getLastNumber() {
+		return lastNumber;
+	}
+
+	public void setLastNumber(LastNumber lastNumber) {
+		this.lastNumber = lastNumber;
+	}
+
+	public LastNumber getExplicitOverridingImplicit() {
+		return explicitOverridingImplicit;
+	}
+
+	public void setExplicitOverridingImplicit(LastNumber explicitOverridingImplicit) {
+		this.explicitOverridingImplicit = explicitOverridingImplicit;
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/EnumeratedTypeTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/EnumeratedTypeTest.java
new file mode 100644
index 0000000000..10e2071604
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/EnumeratedTypeTest.java
@@ -0,0 +1,335 @@
+package org.hibernate.test.annotations.enumerated;
+
+import java.io.Serializable;
+import org.hibernate.Session;
+import org.hibernate.cfg.Configuration;
+import org.hibernate.criterion.Restrictions;
+import org.hibernate.mapping.PersistentClass;
+import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
+import org.hibernate.type.EnumType;
+import org.hibernate.type.Type;
+import org.junit.Test;
+import static org.junit.Assert.assertEquals;
+import static org.hibernate.test.annotations.enumerated.EntityEnum.*;
+
+/**
+ * Test type definition for enum
+ * 
+ * @author Janario Oliveira
+ */
+public class EnumeratedTypeTest extends BaseCoreFunctionalTestCase {
+
+	@Test
+	public void testTypeDefinition() {
+		Configuration cfg = configuration();
+		PersistentClass pc = cfg.getClassMapping( EntityEnum.class.getName() );
+
+		// ordinal default of EnumType
+		Type ordinalEnum = pc.getProperty( "ordinal" ).getType();
+		assertEquals( Common.class, ordinalEnum.getReturnedClass() );
+		assertEquals( EnumType.class.getName(), ordinalEnum.getName() );
+
+		// string defined by Enumerated(STRING)
+		Type stringEnum = pc.getProperty( "string" ).getType();
+		assertEquals( Common.class, stringEnum.getReturnedClass() );
+		assertEquals( EnumType.class.getName(), stringEnum.getName() );
+
+		// explicit defined by @Type
+		Type first = pc.getProperty( "firstLetter" ).getType();
+		assertEquals( FirstLetter.class, first.getReturnedClass() );
+		assertEquals( FirstLetterType.class.getName(), first.getName() );
+
+		// implicit defined by @TypeDef in somewhere
+		Type last = pc.getProperty( "lastNumber" ).getType();
+		assertEquals( LastNumber.class, last.getReturnedClass() );
+		assertEquals( LastNumberType.class.getName(), last.getName() );
+
+		// implicit defined by @TypeDef in anywhere, but overrided by Enumerated(STRING)
+		Type implicitOverrideExplicit = pc.getProperty( "explicitOverridingImplicit" ).getType();
+		assertEquals( LastNumber.class, implicitOverrideExplicit.getReturnedClass() );
+		assertEquals( EnumType.class.getName(), implicitOverrideExplicit.getName() );
+	}
+
+	@Test
+	public void testTypeQuery() {
+		Session session = openSession();
+		session.getTransaction().begin();
+
+		// persist
+		EntityEnum entityEnum = new EntityEnum();
+		entityEnum.setOrdinal( Common.A2 );
+		Serializable id = session.save( entityEnum );
+
+		session.getTransaction().commit();
+		session.close();
+		session = openSession();
+		session.getTransaction().begin();
+
+		// find
+		entityEnum = (EntityEnum) session.createQuery( "from EntityEnum ee where ee.ordinal=1" ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( Common.A2, entityEnum.getOrdinal() );
+		// find parameter
+		entityEnum = (EntityEnum) session.createQuery( "from EntityEnum ee where ee.ordinal=:ordinal" )
+				.setParameter( "ordinal", Common.A2 ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( Common.A2, entityEnum.getOrdinal() );
+		// delete
+		assertEquals( 1, session.createSQLQuery( "DELETE FROM EntityEnum where ordinal=1" ).executeUpdate() );
+
+		session.getTransaction().commit();
+		session.close();
+
+		// **************
+		session = openSession();
+		session.getTransaction().begin();
+
+		// persist
+		entityEnum = new EntityEnum();
+		entityEnum.setString( Common.B1 );
+		id = session.save( entityEnum );
+
+		session.getTransaction().commit();
+		session.close();
+		session = openSession();
+		session.getTransaction().begin();
+
+		// find
+		entityEnum = (EntityEnum) session.createQuery( "from EntityEnum ee where ee.string='B1'" ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( Common.B1, entityEnum.getString() );
+		// find parameter
+		entityEnum = (EntityEnum) session.createQuery( "from EntityEnum ee where ee.string=:string" )
+				.setParameter( "string", Common.B1 ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( Common.B1, entityEnum.getString() );
+		// delete
+		assertEquals( 1, session.createSQLQuery( "DELETE FROM EntityEnum where string='B1'" ).executeUpdate() );
+		session.getTransaction().commit();
+		session.close();
+
+		// **************
+		session = openSession();
+		session.getTransaction().begin();
+
+		// persist
+		entityEnum = new EntityEnum();
+		entityEnum.setFirstLetter( FirstLetter.C_LETTER );
+		id = session.save( entityEnum );
+
+		session.getTransaction().commit();
+		session.close();
+		session = openSession();
+		session.getTransaction().begin();
+
+		// find
+		entityEnum = (EntityEnum) session.createQuery( "from EntityEnum ee where ee.firstLetter='C'" ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( FirstLetter.C_LETTER, entityEnum.getFirstLetter() );
+		// find parameter
+		entityEnum = (EntityEnum) session.createQuery( "from EntityEnum ee where ee.firstLetter=:firstLetter" )
+				.setParameter( "firstLetter", FirstLetter.C_LETTER ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( FirstLetter.C_LETTER, entityEnum.getFirstLetter() );
+		// delete
+		assertEquals( 1, session.createSQLQuery( "DELETE FROM EntityEnum where firstLetter='C'" ).executeUpdate() );
+
+		session.getTransaction().commit();
+		session.close();
+
+		// **************
+		session = openSession();
+		session.getTransaction().begin();
+
+		// persist
+		entityEnum = new EntityEnum();
+		entityEnum.setLastNumber( LastNumber.NUMBER_1 );
+		id = session.save( entityEnum );
+
+		session.getTransaction().commit();
+		session.close();
+		session = openSession();
+		session.getTransaction().begin();
+
+		// find
+		entityEnum = (EntityEnum) session.createQuery( "from EntityEnum ee where ee.lastNumber='1'" ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( LastNumber.NUMBER_1, entityEnum.getLastNumber() );
+		// find parameter
+		entityEnum = (EntityEnum) session.createQuery( "from EntityEnum ee where ee.lastNumber=:lastNumber" )
+				.setParameter( "lastNumber", LastNumber.NUMBER_1 ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( LastNumber.NUMBER_1, entityEnum.getLastNumber() );
+		// delete
+		assertEquals( 1, session.createSQLQuery( "DELETE FROM EntityEnum where lastNumber='1'" ).executeUpdate() );
+
+		session.getTransaction().commit();
+		session.close();
+
+		// **************
+		session = openSession();
+		session.getTransaction().begin();
+
+		// persist
+		entityEnum = new EntityEnum();
+		entityEnum.setExplicitOverridingImplicit( LastNumber.NUMBER_2 );
+		id = session.save( entityEnum );
+
+		session.getTransaction().commit();
+		session.close();
+		session = openSession();
+		session.getTransaction().begin();
+
+		// find
+		entityEnum = (EntityEnum) session.createQuery(
+				"from EntityEnum ee where ee.explicitOverridingImplicit='NUMBER_2'" ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( LastNumber.NUMBER_2, entityEnum.getExplicitOverridingImplicit() );
+		// find parameter
+		entityEnum = (EntityEnum) session
+				.createQuery( "from EntityEnum ee where ee.explicitOverridingImplicit=:override" )
+				.setParameter( "override", LastNumber.NUMBER_2 ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( LastNumber.NUMBER_2, entityEnum.getExplicitOverridingImplicit() );
+		// delete
+		assertEquals( 1, session.createSQLQuery( "DELETE FROM EntityEnum where explicitOverridingImplicit='NUMBER_2'" )
+				.executeUpdate() );
+
+		session.getTransaction().commit();
+		session.close();
+	}
+
+	@Test
+	public void testTypeCriteria() {
+		Session session = openSession();
+		session.getTransaction().begin();
+
+		// persist
+		EntityEnum entityEnum = new EntityEnum();
+		entityEnum.setOrdinal( Common.A1 );
+		Serializable id = session.save( entityEnum );
+
+		session.getTransaction().commit();
+		session.close();
+		session = openSession();
+		session.getTransaction().begin();
+
+		// find
+		entityEnum = (EntityEnum) session.createCriteria( EntityEnum.class )
+				.add( Restrictions.eq( "ordinal", Common.A1 ) ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( Common.A1, entityEnum.getOrdinal() );
+		// delete
+		assertEquals( 1, session.createSQLQuery( "DELETE FROM EntityEnum where ordinal=0" ).executeUpdate() );
+
+		session.getTransaction().commit();
+		session.close();
+
+		// **************
+		session = openSession();
+		session.getTransaction().begin();
+
+		// persist
+		entityEnum = new EntityEnum();
+		entityEnum.setString( Common.B2 );
+		id = session.save( entityEnum );
+
+		session.getTransaction().commit();
+		session.close();
+		session = openSession();
+		session.getTransaction().begin();
+
+		// find
+		entityEnum = (EntityEnum) session.createCriteria( EntityEnum.class )
+				.add( Restrictions.eq( "string", Common.B2 ) ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( Common.B2, entityEnum.getString() );
+		// delete
+		assertEquals( 1, session.createSQLQuery( "DELETE FROM EntityEnum where string='B2'" ).executeUpdate() );
+
+		session.getTransaction().commit();
+		session.close();
+
+		// **************
+		session = openSession();
+		session.getTransaction().begin();
+
+		// persist
+		entityEnum = new EntityEnum();
+		entityEnum.setFirstLetter( FirstLetter.A_LETTER );
+		id = session.save( entityEnum );
+
+		session.getTransaction().commit();
+		session.close();
+		session = openSession();
+		session.getTransaction().begin();
+
+		// find
+		entityEnum = (EntityEnum) session.createCriteria( EntityEnum.class )
+				.add( Restrictions.eq( "firstLetter", FirstLetter.A_LETTER ) ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( FirstLetter.A_LETTER, entityEnum.getFirstLetter() );
+		// delete
+		assertEquals( 1, session.createSQLQuery( "DELETE FROM EntityEnum where firstLetter='A'" ).executeUpdate() );
+
+		session.getTransaction().commit();
+		session.close();
+
+		// **************
+		session = openSession();
+		session.getTransaction().begin();
+
+		// persist
+		entityEnum = new EntityEnum();
+		entityEnum.setLastNumber( LastNumber.NUMBER_3 );
+		id = session.save( entityEnum );
+
+		session.getTransaction().commit();
+		session.close();
+		session = openSession();
+		session.getTransaction().begin();
+
+		// find
+		entityEnum = (EntityEnum) session.createCriteria( EntityEnum.class )
+				.add( Restrictions.eq( "lastNumber", LastNumber.NUMBER_3 ) ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( LastNumber.NUMBER_3, entityEnum.getLastNumber() );
+		// delete
+		assertEquals( 1, session.createSQLQuery( "DELETE FROM EntityEnum where lastNumber='3'" ).executeUpdate() );
+
+		session.getTransaction().commit();
+		session.close();
+
+		// **************
+		session = openSession();
+		session.getTransaction().begin();
+
+		// persist
+		entityEnum = new EntityEnum();
+		entityEnum.setExplicitOverridingImplicit( LastNumber.NUMBER_2 );
+		id = session.save( entityEnum );
+
+		session.getTransaction().commit();
+		session.close();
+		session = openSession();
+		session.getTransaction().begin();
+
+		// find
+		entityEnum = (EntityEnum) session.createCriteria( EntityEnum.class )
+				.add( Restrictions.eq( "explicitOverridingImplicit", LastNumber.NUMBER_2 ) ).uniqueResult();
+		assertEquals( id, entityEnum.getId() );
+		assertEquals( LastNumber.NUMBER_2, entityEnum.getExplicitOverridingImplicit() );
+		// delete
+		assertEquals( 1, session.createSQLQuery( "DELETE FROM EntityEnum where explicitOverridingImplicit='NUMBER_2'" )
+				.executeUpdate() );
+
+		session.getTransaction().commit();
+		session.close();
+
+	}
+
+	@Override
+	protected Class[] getAnnotatedClasses() {
+		return new Class[] { EntityEnum.class };
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/FirstLetterType.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/FirstLetterType.java
new file mode 100644
index 0000000000..83ab61f2ee
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/FirstLetterType.java
@@ -0,0 +1,41 @@
+package org.hibernate.test.annotations.enumerated;
+
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+import org.hibernate.HibernateException;
+import org.hibernate.engine.spi.SessionImplementor;
+
+/**
+ * @author Janario Oliveira
+ */
+public class FirstLetterType extends org.hibernate.type.EnumType {
+
+	@Override
+	public int[] sqlTypes() {
+		return new int[] { Types.VARCHAR };
+	}
+
+	@Override
+	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
+			throws HibernateException, SQLException {
+		String persistValue = (String) rs.getObject( names[0] );
+		if ( rs.wasNull() ) {
+			return null;
+		}
+		return Enum.valueOf( returnedClass(), persistValue + "_LETTER" );
+	}
+
+	@Override
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
+			throws HibernateException, SQLException {
+		if ( value == null ) {
+			st.setNull( index, sqlTypes()[0] );
+		}
+		else {
+			String enumString = ( (Enum<?>) value ).name();
+			st.setObject( index, enumString.charAt( 0 ), sqlTypes()[0] );
+		}
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/LastNumberType.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/LastNumberType.java
new file mode 100644
index 0000000000..65e38525f9
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/enumerated/LastNumberType.java
@@ -0,0 +1,42 @@
+package org.hibernate.test.annotations.enumerated;
+
+import java.sql.PreparedStatement;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.sql.Types;
+import org.hibernate.HibernateException;
+import org.hibernate.engine.spi.SessionImplementor;
+
+/**
+ * @author Janario Oliveira
+ */
+public class LastNumberType extends org.hibernate.type.EnumType {
+
+	@Override
+	public int[] sqlTypes() {
+		return new int[] { Types.VARCHAR };
+	}
+
+	@Override
+	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
+			throws HibernateException, SQLException {
+		String persistValue = (String) rs.getObject( names[0] );
+		if ( rs.wasNull() ) {
+			return null;
+		}
+		return Enum.valueOf( returnedClass(), "NUMBER_" + persistValue );
+	}
+
+	@Override
+	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
+			throws HibernateException, SQLException {
+		if ( value == null ) {
+			st.setNull( index, sqlTypes()[0] );
+		}
+		else {
+
+			String enumString = ( (Enum<?>) value ).name();
+			st.setObject( index, enumString.charAt( enumString.length() - 1 ), sqlTypes()[0] );
+		}
+	}
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/HousePlaces.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/HousePlaces.java
new file mode 100644
index 0000000000..125c5b56b9
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/HousePlaces.java
@@ -0,0 +1,29 @@
+package org.hibernate.test.annotations.referencedcolumnname;
+
+import javax.persistence.AssociationOverride;
+import javax.persistence.AssociationOverrides;
+import javax.persistence.Embedded;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+import javax.persistence.JoinColumn;
+
+/**
+ * @author Janario Oliveira
+ */
+@Entity
+public class HousePlaces {
+
+	@Id
+	@GeneratedValue
+	int id;
+	@Embedded
+	Places places;
+	@Embedded
+	@AssociationOverrides({
+			@AssociationOverride(name = "livingRoom", joinColumns = {
+					@JoinColumn(name = "NEIGHBOUR_LIVINGROOM", referencedColumnName = "NAME"),
+					@JoinColumn(name = "NEIGHBOUR_LIVINGROOM_OWNER", referencedColumnName = "OWNER") }),
+			@AssociationOverride(name = "kitchen", joinColumns = @JoinColumn(name = "NEIGHBOUR_KITCHEN", referencedColumnName = "NAME")) })
+	Places neighbourPlaces;
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Place.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Place.java
new file mode 100644
index 0000000000..ac9977773f
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Place.java
@@ -0,0 +1,21 @@
+package org.hibernate.test.annotations.referencedcolumnname;
+
+import javax.persistence.Column;
+import javax.persistence.Entity;
+import javax.persistence.GeneratedValue;
+import javax.persistence.Id;
+
+/**
+ * @author Janario Oliveira
+ */
+@Entity
+public class Place {
+
+	@Id
+	@GeneratedValue
+	int id;
+	@Column(name = "NAME")
+	String name;
+	@Column(name = "OWNER")
+	String owner;
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Places.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Places.java
new file mode 100644
index 0000000000..ccd1dd074b
--- /dev/null
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/Places.java
@@ -0,0 +1,22 @@
+package org.hibernate.test.annotations.referencedcolumnname;
+
+import javax.persistence.CascadeType;
+import javax.persistence.Embeddable;
+import javax.persistence.JoinColumn;
+import javax.persistence.JoinColumns;
+import javax.persistence.ManyToOne;
+
+/**
+ * @author Janario Oliveira
+ */
+@Embeddable
+public class Places {
+
+	@ManyToOne(cascade = CascadeType.ALL)
+	@JoinColumns({ @JoinColumn(name = "LIVING_ROOM", referencedColumnName = "NAME"),
+			@JoinColumn(name = "LIVING_ROOM_OWNER", referencedColumnName = "OWNER") })
+	Place livingRoom;
+	@ManyToOne(cascade = CascadeType.ALL)
+	@JoinColumn(name = "KITCHEN", referencedColumnName = "NAME")
+	Place kitchen;
+}
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/ReferencedColumnNameTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/ReferencedColumnNameTest.java
index 078f96a8d1..aa51b685b4 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/ReferencedColumnNameTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/referencedcolumnname/ReferencedColumnNameTest.java
@@ -1,225 +1,290 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.test.annotations.referencedcolumnname;
 
 import java.math.BigDecimal;
 import java.util.Iterator;
 
 import org.junit.Test;
 
 import org.hibernate.Session;
 import org.hibernate.Transaction;
+import org.hibernate.criterion.Restrictions;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 /**
  * @author Emmanuel Bernard
  */
 public class ReferencedColumnNameTest extends BaseCoreFunctionalTestCase {
 	@Test
 	public void testManyToOne() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 		Postman pm = new Postman( "Bob", "A01" );
 		House house = new House();
 		house.setPostman( pm );
 		house.setAddress( "Rue des pres" );
 		s.persist( pm );
 		s.persist( house );
 		tx.commit();
 		s.close();
 		s = openSession();
 		tx = s.beginTransaction();
 		house = (House) s.get( House.class, house.getId() );
 		assertNotNull( house.getPostman() );
 		assertEquals( "Bob", house.getPostman().getName() );
 		pm = house.getPostman();
 		s.delete( house );
 		s.delete( pm );
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testOneToMany() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 
 		Rambler rambler = new Rambler( "Emmanuel" );
 		Bag bag = new Bag( "0001", rambler );
 		rambler.getBags().add( bag );
 		s.persist( rambler );
 
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 
 		bag = (Bag) s.createQuery( "select b from Bag b left join fetch b.owner" ).uniqueResult();
 		assertNotNull( bag );
 		assertNotNull( bag.getOwner() );
 
 		rambler = (Rambler) s.createQuery( "select r from Rambler r left join fetch r.bags" ).uniqueResult();
 		assertNotNull( rambler );
 		assertNotNull( rambler.getBags() );
 		assertEquals( 1, rambler.getBags().size() );
 		s.delete( rambler.getBags().iterator().next() );
 		s.delete( rambler );
 
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testUnidirectionalOneToMany() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 
 		Clothes clothes = new Clothes( "underwear", "interesting" );
 		Luggage luggage = new Luggage( "Emmanuel", "Cabin Luggage" );
 		luggage.getHasInside().add( clothes );
 		s.persist( luggage );
 
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 
 		luggage = (Luggage) s.createQuery( "select l from Luggage l left join fetch l.hasInside" ).uniqueResult();
 		assertNotNull( luggage );
 		assertNotNull( luggage.getHasInside() );
 		assertEquals( 1, luggage.getHasInside().size() );
 
 		s.delete( luggage.getHasInside().iterator().next() );
 		s.delete( luggage );
 
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToMany() throws Exception {
 		Session s;
 		Transaction tx;
 		s = openSession();
 		tx = s.beginTransaction();
 
 		House whiteHouse = new House();
 		whiteHouse.setAddress( "1600 Pennsylvania Avenue, Washington" );
 		Inhabitant bill = new Inhabitant();
 		bill.setName( "Bill Clinton" );
 		Inhabitant george = new Inhabitant();
 		george.setName( "George W Bush" );
 		s.persist( george );
 		s.persist( bill );
 		whiteHouse.getHasInhabitants().add( bill );
 		whiteHouse.getHasInhabitants().add( george );
 		//bill.getLivesIn().add( whiteHouse );
 		//george.getLivesIn().add( whiteHouse );
 
 		s.persist( whiteHouse );
 		tx.commit();
 		s = openSession();
 		tx = s.beginTransaction();
 
 		whiteHouse = (House) s.get( House.class, whiteHouse.getId() );
 		assertNotNull( whiteHouse );
 		assertEquals( 2, whiteHouse.getHasInhabitants().size() );
 
 		tx.commit();
 		s.clear();
 		tx = s.beginTransaction();
 		bill = (Inhabitant) s.get( Inhabitant.class, bill.getId() );
 		assertNotNull( bill );
 		assertEquals( 1, bill.getLivesIn().size() );
 		assertEquals( whiteHouse.getAddress(), bill.getLivesIn().iterator().next().getAddress() );
 
 		whiteHouse = bill.getLivesIn().iterator().next();
 		s.delete( whiteHouse );
 		Iterator it = whiteHouse.getHasInhabitants().iterator();
 		while ( it.hasNext() ) {
 			s.delete( it.next() );
 		}
 		tx.commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToOneReferenceManyToOne() throws Exception {
 		Item item = new Item();
 		item.setId( 1 );
 		Vendor vendor = new Vendor();
 		vendor.setId( 1 );
 		ItemCost cost = new ItemCost();
 		cost.setCost( new BigDecimal(1) );
 		cost.setId( 1 );
 		cost.setItem( item );
 		cost.setVendor( vendor );
 		WarehouseItem wItem = new WarehouseItem();
 		wItem.setDefaultCost( cost );
 		wItem.setId( 1 );
 		wItem.setItem( item );
 		wItem.setQtyInStock( new BigDecimal(1) );
 		wItem.setVendor( vendor );
 		Session s = openSession( );
 		s.getTransaction().begin();
 		s.persist( item );
 		s.persist( vendor );
 		s.persist( cost );
 		s.persist( wItem );
 		s.flush();
 		s.clear();
 		wItem = (WarehouseItem) s.get(WarehouseItem.class, wItem.getId() );
 		assertNotNull( wItem.getDefaultCost().getItem() );
 		s.getTransaction().rollback();
 		s.close();
 	}
 
+	@Test
+	public void testManyToOneInsideComponentReferencedColumn() {
+		HousePlaces house = new HousePlaces();
+		house.places = new Places();
+
+		house.places.livingRoom = new Place();
+		house.places.livingRoom.name = "First";
+		house.places.livingRoom.owner = "mine";
+
+		house.places.kitchen = new Place();
+		house.places.kitchen.name = "Kitchen 1";
+
+		house.neighbourPlaces = new Places();
+		house.neighbourPlaces.livingRoom = new Place();
+		house.neighbourPlaces.livingRoom.name = "Neighbour";
+		house.neighbourPlaces.livingRoom.owner = "his";
+
+		house.neighbourPlaces.kitchen = new Place();
+		house.neighbourPlaces.kitchen.name = "His Kitchen";
+
+		Session s = openSession();
+		Transaction tx = s.beginTransaction();
+		s.save( house );
+		s.flush();
+
+		HousePlaces get = (HousePlaces) s.get( HousePlaces.class, house.id );
+		assertEquals( house.id, get.id );
+
+		HousePlaces uniqueResult = (HousePlaces) s.createQuery( "from HousePlaces h where h.places.livingRoom.name='First'" )
+				.uniqueResult();
+		assertNotNull( uniqueResult );
+		assertEquals( uniqueResult.places.livingRoom.name, "First" );
+		assertEquals( uniqueResult.places.livingRoom.owner, "mine" );
+
+		uniqueResult = (HousePlaces) s.createQuery( "from HousePlaces h where h.places.livingRoom.owner=:owner" )
+				.setParameter( "owner", "mine" ).uniqueResult();
+		assertNotNull( uniqueResult );
+		assertEquals( uniqueResult.places.livingRoom.name, "First" );
+		assertEquals( uniqueResult.places.livingRoom.owner, "mine" );
+
+		assertNotNull( s.createCriteria( HousePlaces.class ).add( Restrictions.eq( "places.livingRoom.owner", "mine" ) )
+				.uniqueResult() );
+
+		// override
+		uniqueResult = (HousePlaces) s.createQuery( "from HousePlaces h where h.neighbourPlaces.livingRoom.owner='his'" )
+				.uniqueResult();
+		assertNotNull( uniqueResult );
+		assertEquals( uniqueResult.neighbourPlaces.livingRoom.name, "Neighbour" );
+		assertEquals( uniqueResult.neighbourPlaces.livingRoom.owner, "his" );
+
+		uniqueResult = (HousePlaces) s.createQuery( "from HousePlaces h where h.neighbourPlaces.livingRoom.name=:name" )
+				.setParameter( "name", "Neighbour" ).uniqueResult();
+		assertNotNull( uniqueResult );
+		assertEquals( uniqueResult.neighbourPlaces.livingRoom.name, "Neighbour" );
+		assertEquals( uniqueResult.neighbourPlaces.livingRoom.owner, "his" );
+
+		assertNotNull( s.createCriteria( HousePlaces.class )
+				.add( Restrictions.eq( "neighbourPlaces.livingRoom.owner", "his" ) ).uniqueResult() );
+
+		tx.rollback();
+	}
+
 	@Override
 	protected Class[] getAnnotatedClasses() {
 		return new Class[]{
 				House.class,
 				Postman.class,
 				Bag.class,
 				Rambler.class,
 				Luggage.class,
 				Clothes.class,
 				Inhabitant.class,
 				Item.class,
 				ItemCost.class,
 				Vendor.class,
-				WarehouseItem.class
+				WarehouseItem.class,
+				Place.class,
+				HousePlaces.class
 		};
 	}
 }
diff --git a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/BasicMetadataGenerator.java b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/BasicMetadataGenerator.java
index c575deac75..3dbe8dba75 100644
--- a/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/BasicMetadataGenerator.java
+++ b/hibernate-envers/src/main/java/org/hibernate/envers/configuration/metadata/BasicMetadataGenerator.java
@@ -1,85 +1,91 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008, Red Hat Middleware LLC or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Middleware LLC.
  *
  * This copyrighted material is made available to anyone wishing to use, modify,
  * copy, or redistribute it subject to the terms and conditions of the GNU
  * Lesser General Public License, as published by the Free Software Foundation.
  *
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
  * for more details.
  *
  * You should have received a copy of the GNU Lesser General Public License
  * along with this distribution; if not, write to:
  * Free Software Foundation, Inc.
  * 51 Franklin Street, Fifth Floor
  * Boston, MA  02110-1301  USA
  */
 package org.hibernate.envers.configuration.metadata;
 
 import java.util.Properties;
 
 import org.dom4j.Element;
 
 import org.hibernate.envers.configuration.metadata.reader.PropertyAuditingData;
 import org.hibernate.envers.entities.mapper.SimpleMapperBuilder;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Value;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.SerializableToBlobType;
 import org.hibernate.type.Type;
 
 /**
  * Generates metadata for basic properties: immutable types (including enums).
  * @author Adam Warski (adam at warski dot org)
  */
 public final class BasicMetadataGenerator {
     @SuppressWarnings({"unchecked"})
 	boolean addBasic(Element parent, PropertyAuditingData propertyAuditingData,
 					 Value value, SimpleMapperBuilder mapper, boolean insertable, boolean key) {
 		Type type = value.getType();
 
 		if (type instanceof BasicType || type instanceof SerializableToBlobType ||
                 "org.hibernate.type.PrimitiveByteArrayBlobType".equals(type.getClass().getName())) {
             if (parent != null) {
                 boolean addNestedType = (value instanceof SimpleValue) && ((SimpleValue) value).getTypeParameters() != null;
 
                 String typeName = type.getName();
                 if (typeName == null) {
                     typeName = type.getClass().getName();
                 }
 
                 Element prop_mapping = MetadataTools.addProperty(parent, propertyAuditingData.getName(),
                         addNestedType ? null : typeName, propertyAuditingData.isForceInsertable() || insertable, key);
                 MetadataTools.addColumns(prop_mapping, value.getColumnIterator());
 
                 if (addNestedType) {
                     Properties typeParameters = ((SimpleValue) value).getTypeParameters();
                     Element type_mapping = prop_mapping.addElement("type");
                     type_mapping.addAttribute("name", typeName);
 
-                    for (java.util.Map.Entry paramKeyValue : typeParameters.entrySet()) {
-                        Element type_param = type_mapping.addElement("param");
-                        type_param.addAttribute("name", (String) paramKeyValue.getKey());
-                        type_param.setText((String) paramKeyValue.getValue());
+                    
+                    for (Object object : typeParameters.keySet()) {
+                        String keyType = (String) object;
+                        String property = typeParameters.getProperty(keyType);
+                        
+                        if (property != null) {
+                            Element type_param = type_mapping.addElement("param");
+                            type_param.addAttribute("name", keyType);
+                            type_param.setText(property);
+                        }
                     }
                 }
             }
 
             // A null mapper means that we only want to add xml mappings
             if (mapper != null) {
                 mapper.add(propertyAuditingData.getPropertyData());
             }
 		} else {
 			return false;
 		}
 
 		return true;
 	}
 }
