diff --git a/hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java b/hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java
index 19bb05c809..238d816183 100644
--- a/hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/boot/model/source/internal/hbm/ModelBinder.java
@@ -1118,2120 +1118,2118 @@ public class ModelBinder {
 
 		for ( AttributeSource attributeSource : entitySource.attributeSources() ) {
 			if ( PluralAttributeSource.class.isInstance( attributeSource ) ) {
 				// plural attribute
 				final Property attribute = createPluralAttribute(
 						mappingDocument,
 						(PluralAttributeSource) attributeSource,
 						entityDescriptor
 				);
 				entityDescriptor.addProperty( attribute );
 			}
 			else {
 				// singular attribute
 				if ( SingularAttributeSourceBasic.class.isInstance( attributeSource ) ) {
 					final SingularAttributeSourceBasic basicAttributeSource = (SingularAttributeSourceBasic) attributeSource;
 					final Identifier tableName = determineTable( mappingDocument, basicAttributeSource.getName(), basicAttributeSource );
 					final AttributeContainer attributeContainer;
 					final Table table;
 					final Join secondaryTableJoin = entityTableXref.locateJoin( tableName );
 					if ( secondaryTableJoin == null ) {
 						table = entityDescriptor.getTable();
 						attributeContainer = entityDescriptor;
 					}
 					else {
 						table = secondaryTableJoin.getTable();
 						attributeContainer = secondaryTableJoin;
 					}
 
 					final Property attribute = createBasicAttribute(
 							mappingDocument,
 							basicAttributeSource,
 							new SimpleValue( mappingDocument.getMetadataCollector(), table ),
 							entityDescriptor.getClassName()
 					);
 
 					if ( secondaryTableJoin != null ) {
 						attribute.setOptional( secondaryTableJoin.isOptional() );
 					}
 
 					attributeContainer.addProperty( attribute );
 
 					handleNaturalIdBinding(
 							mappingDocument,
 							entityDescriptor,
 							attribute,
 							basicAttributeSource.getNaturalIdMutability()
 					);
 				}
 				else if ( SingularAttributeSourceEmbedded.class.isInstance( attributeSource ) ) {
 					final SingularAttributeSourceEmbedded embeddedAttributeSource = (SingularAttributeSourceEmbedded) attributeSource;
 					final Identifier tableName = determineTable( mappingDocument, embeddedAttributeSource );
 					final AttributeContainer attributeContainer;
 					final Table table;
 					final Join secondaryTableJoin = entityTableXref.locateJoin( tableName );
 					if ( secondaryTableJoin == null ) {
 						table = entityDescriptor.getTable();
 						attributeContainer = entityDescriptor;
 					}
 					else {
 						table = secondaryTableJoin.getTable();
 						attributeContainer = secondaryTableJoin;
 					}
 
 					final Property attribute = createEmbeddedAttribute(
 							mappingDocument,
 							(SingularAttributeSourceEmbedded) attributeSource,
 							new Component( mappingDocument.getMetadataCollector(), table, entityDescriptor ),
 							entityDescriptor.getClassName()
 					);
 
 					if ( secondaryTableJoin != null ) {
 						attribute.setOptional( secondaryTableJoin.isOptional() );
 					}
 
 					attributeContainer.addProperty( attribute );
 
 					handleNaturalIdBinding(
 							mappingDocument,
 							entityDescriptor,
 							attribute,
 							embeddedAttributeSource.getNaturalIdMutability()
 					);
 				}
 				else if ( SingularAttributeSourceManyToOne.class.isInstance( attributeSource ) ) {
 					final SingularAttributeSourceManyToOne manyToOneAttributeSource = (SingularAttributeSourceManyToOne) attributeSource;
 					final Identifier tableName = determineTable( mappingDocument, manyToOneAttributeSource.getName(), manyToOneAttributeSource );
 					final AttributeContainer attributeContainer;
 					final Table table;
 					final Join secondaryTableJoin = entityTableXref.locateJoin( tableName );
 					if ( secondaryTableJoin == null ) {
 						table = entityDescriptor.getTable();
 						attributeContainer = entityDescriptor;
 					}
 					else {
 						table = secondaryTableJoin.getTable();
 						attributeContainer = secondaryTableJoin;
 					}
 
 					final Property attribute = createManyToOneAttribute(
 							mappingDocument,
 							manyToOneAttributeSource,
 							new ManyToOne( mappingDocument.getMetadataCollector(), table ),
 							entityDescriptor.getClassName()
 					);
 
 					if ( secondaryTableJoin != null ) {
 						attribute.setOptional( secondaryTableJoin.isOptional() );
 					}
 
 					attributeContainer.addProperty( attribute );
 
 					handleNaturalIdBinding(
 							mappingDocument,
 							entityDescriptor,
 							attribute,
 							manyToOneAttributeSource.getNaturalIdMutability()
 					);
 				}
 				else if ( SingularAttributeSourceOneToOne.class.isInstance( attributeSource ) ) {
 					final SingularAttributeSourceOneToOne oneToOneAttributeSource = (SingularAttributeSourceOneToOne) attributeSource;
 					final Table table = entityDescriptor.getTable();
 					final Property attribute = createOneToOneAttribute(
 							mappingDocument,
 							oneToOneAttributeSource,
 							new OneToOne( mappingDocument.getMetadataCollector(), table, entityDescriptor ),
 							entityDescriptor.getClassName()
 					);
 					entityDescriptor.addProperty( attribute );
 
 					handleNaturalIdBinding(
 							mappingDocument,
 							entityDescriptor,
 							attribute,
 							oneToOneAttributeSource.getNaturalIdMutability()
 					);
 				}
 				else if ( SingularAttributeSourceAny.class.isInstance( attributeSource ) ) {
 					final SingularAttributeSourceAny anyAttributeSource = (SingularAttributeSourceAny) attributeSource;
 					final Identifier tableName = determineTable(
 							mappingDocument,
 							anyAttributeSource.getName(),
 							anyAttributeSource.getKeySource().getRelationalValueSources()
 					);
 					final AttributeContainer attributeContainer;
 					final Table table;
 					final Join secondaryTableJoin = entityTableXref.locateJoin( tableName );
 					if ( secondaryTableJoin == null ) {
 						table = entityDescriptor.getTable();
 						attributeContainer = entityDescriptor;
 					}
 					else {
 						table = secondaryTableJoin.getTable();
 						attributeContainer = secondaryTableJoin;
 					}
 
 					final Property attribute = createAnyAssociationAttribute(
 							mappingDocument,
 							anyAttributeSource,
 							new Any( mappingDocument.getMetadataCollector(), table ),
 							entityDescriptor.getEntityName()
 					);
 
 					if ( secondaryTableJoin != null ) {
 						attribute.setOptional( secondaryTableJoin.isOptional() );
 					}
 
 					attributeContainer.addProperty( attribute );
 
 					handleNaturalIdBinding(
 							mappingDocument,
 							entityDescriptor,
 							attribute,
 							anyAttributeSource.getNaturalIdMutability()
 					);
 				}
 			}
 		}
 
 		registerConstraintSecondPasses( mappingDocument, entitySource, entityTableXref );
 	}
 
 	private void handleNaturalIdBinding(
 			MappingDocument mappingDocument,
 			PersistentClass entityBinding,
 			Property attributeBinding,
 			NaturalIdMutability naturalIdMutability) {
 		if ( naturalIdMutability == NaturalIdMutability.NOT_NATURAL_ID ) {
 			return;
 		}
 
 		attributeBinding.setNaturalIdentifier( true );
 
 		if ( naturalIdMutability == NaturalIdMutability.IMMUTABLE ) {
 			attributeBinding.setUpdateable( false );
 		}
 
 		NaturalIdUniqueKeyBinder ukBinder = mappingDocument.getMetadataCollector().locateNaturalIdUniqueKeyBinder(
 				entityBinding.getEntityName()
 		);
 
 		if ( ukBinder == null ) {
 			ukBinder = new NaturalIdUniqueKeyBinderImpl( mappingDocument, entityBinding );
 			mappingDocument.getMetadataCollector().registerNaturalIdUniqueKeyBinder(
 					entityBinding.getEntityName(),
 					ukBinder
 			);
 		}
 
 		ukBinder.addAttributeBinding( attributeBinding );
 	}
 
 	private void registerConstraintSecondPasses(
 			MappingDocument mappingDocument,
 			EntitySource entitySource,
 			final EntityTableXref entityTableXref) {
 		if ( entitySource.getConstraints() == null ) {
 			return;
 		}
 
 		for ( ConstraintSource constraintSource : entitySource.getConstraints() ) {
 			final String logicalTableName = constraintSource.getTableName();
 			final Table table = entityTableXref.resolveTable( database.toIdentifier( logicalTableName ) );
 
 			mappingDocument.getMetadataCollector().addSecondPass(
 					new ConstraintSecondPass(
 							mappingDocument,
 							table,
 							constraintSource
 					)
 			);
 		}
 	}
 
 	private Property createPluralAttribute(
 			MappingDocument sourceDocument,
 			PluralAttributeSource attributeSource,
 			PersistentClass entityDescriptor) {
 		final Collection collectionBinding;
 
 		if ( attributeSource instanceof PluralAttributeSourceListImpl ) {
 			collectionBinding = new org.hibernate.mapping.List( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			registerSecondPass(
 					new PluralAttributeListSecondPass(
 							sourceDocument,
 							(IndexedPluralAttributeSource) attributeSource,
 							(org.hibernate.mapping.List) collectionBinding
 					),
 					sourceDocument
 			);
 		}
 		else if ( attributeSource instanceof PluralAttributeSourceSetImpl ) {
 			collectionBinding = new Set( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			registerSecondPass(
 					new PluralAttributeSetSecondPass( sourceDocument, attributeSource, collectionBinding ),
 					sourceDocument
 			);
 		}
 		else if ( attributeSource instanceof PluralAttributeSourceMapImpl ) {
 			collectionBinding = new org.hibernate.mapping.Map( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			registerSecondPass(
 					new PluralAttributeMapSecondPass(
 							sourceDocument,
 							(IndexedPluralAttributeSource) attributeSource,
 							(org.hibernate.mapping.Map) collectionBinding
 					),
 					sourceDocument
 			);
 		}
 		else if ( attributeSource instanceof PluralAttributeSourceBagImpl ) {
 			collectionBinding = new Bag( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			registerSecondPass(
 					new PluralAttributeBagSecondPass( sourceDocument, attributeSource, collectionBinding ),
 					sourceDocument
 			);
 		}
 		else if ( attributeSource instanceof PluralAttributeSourceIdBagImpl ) {
 			collectionBinding = new IdentifierBag( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			registerSecondPass(
 					new PluralAttributeIdBagSecondPass( sourceDocument, attributeSource, collectionBinding ),
 					sourceDocument
 			);
 		}
 		else if ( attributeSource instanceof PluralAttributeSourceArrayImpl ) {
 			final PluralAttributeSourceArray arraySource = (PluralAttributeSourceArray) attributeSource;
 			collectionBinding = new Array( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			( (Array) collectionBinding ).setElementClassName(
 					sourceDocument.qualifyClassName( arraySource.getElementClass() )
 			);
 
 			registerSecondPass(
 					new PluralAttributeArraySecondPass(
 							sourceDocument,
 							arraySource,
 							(Array) collectionBinding
 					),
 					sourceDocument
 			);
 		}
 		else if ( attributeSource instanceof PluralAttributeSourcePrimitiveArrayImpl ) {
 			collectionBinding = new PrimitiveArray( sourceDocument.getMetadataCollector(), entityDescriptor );
 			bindCollectionMetadata( sourceDocument, attributeSource, collectionBinding );
 
 			registerSecondPass(
 					new PluralAttributePrimitiveArraySecondPass(
 							sourceDocument,
 							(IndexedPluralAttributeSource) attributeSource,
 							(PrimitiveArray) collectionBinding
 					),
 					sourceDocument
 			);
 		}
 		else {
 			throw new AssertionFailure(
 					"Unexpected PluralAttributeSource type : " + attributeSource.getClass().getName()
 			);
 		}
 
 		sourceDocument.getMetadataCollector().addCollectionBinding( collectionBinding );
 
 		final Property attribute = new Property();
 		attribute.setValue( collectionBinding );
 		bindProperty(
 				sourceDocument,
 				attributeSource,
 				attribute
 		);
 
 		return attribute;
 	}
 
 	private void bindCollectionMetadata(MappingDocument mappingDocument, PluralAttributeSource source, Collection binding) {
 		binding.setRole( source.getAttributeRole().getFullPath() );
 		binding.setInverse( source.isInverse() );
 		binding.setMutable( source.isMutable() );
 		binding.setOptimisticLocked( source.isIncludedInOptimisticLocking() );
 
 		if ( source.getCustomPersisterClassName() != null ) {
 			binding.setCollectionPersisterClass(
 					mappingDocument.getClassLoaderAccess().classForName(
 							mappingDocument.qualifyClassName( source.getCustomPersisterClassName() )
 					)
 			);
 		}
 
 		applyCaching( mappingDocument, source.getCaching(), binding );
 
 		// bind the collection type info
 		String typeName = source.getTypeInformation().getName();
 		Map typeParameters = new HashMap();
 		if ( typeName != null ) {
 			// see if there is a corresponding type-def
 			final TypeDefinition typeDef = mappingDocument.getMetadataCollector().getTypeDefinition( typeName );
 			if ( typeDef != null ) {
 				typeName = typeDef.getTypeImplementorClass().getName();
 				if ( typeDef.getParameters() != null ) {
 					typeParameters.putAll( typeDef.getParameters() );
 				}
 			}
 			else {
 				// it could be a unqualified class name, in which case we should qualify
 				// it with the implicit package name for this context, if one.
 				typeName = mappingDocument.qualifyClassName( typeName );
 			}
 		}
 		if ( source.getTypeInformation().getParameters() != null ) {
 			typeParameters.putAll( source.getTypeInformation().getParameters() );
 		}
 
 		binding.setTypeName( typeName );
 		binding.setTypeParameters( typeParameters );
 
 		if ( source.getFetchCharacteristics().getFetchTiming() == FetchTiming.DELAYED ) {
 			binding.setLazy( true );
 			binding.setExtraLazy( source.getFetchCharacteristics().isExtraLazy() );
 		}
 		else {
 			binding.setLazy( false );
 		}
 
 		switch ( source.getFetchCharacteristics().getFetchStyle() ) {
 			case SELECT: {
 				binding.setFetchMode( FetchMode.SELECT );
 				break;
 			}
 			case JOIN: {
 				binding.setFetchMode( FetchMode.JOIN );
 				break;
 			}
 			case BATCH: {
 				binding.setFetchMode( FetchMode.SELECT );
 				binding.setBatchSize( source.getFetchCharacteristics().getBatchSize() );
 				break;
 			}
 			case SUBSELECT: {
 				binding.setFetchMode( FetchMode.SELECT );
 				binding.setSubselectLoadable( true );
 				// todo : this could totally be done using a "symbol map" approach
 				binding.getOwner().setSubselectLoadableCollections( true );
 				break;
 			}
 			default: {
 				throw new AssertionFailure( "Unexpected FetchStyle : " + source.getFetchCharacteristics().getFetchStyle().name() );
 			}
 		}
 
 		for ( String name : source.getSynchronizedTableNames() ) {
 			binding.getSynchronizedTables().add( name );
 		}
 
 		binding.setWhere( source.getWhere() );
 		binding.setLoaderName( source.getCustomLoaderName() );
 		if ( source.getCustomSqlInsert() != null ) {
 			binding.setCustomSQLInsert(
 					source.getCustomSqlInsert().getSql(),
 					source.getCustomSqlInsert().isCallable(),
 					source.getCustomSqlInsert().getCheckStyle()
 			);
 		}
 		if ( source.getCustomSqlUpdate() != null ) {
 			binding.setCustomSQLUpdate(
 					source.getCustomSqlUpdate().getSql(),
 					source.getCustomSqlUpdate().isCallable(),
 					source.getCustomSqlUpdate().getCheckStyle()
 			);
 		}
 		if ( source.getCustomSqlDelete() != null ) {
 			binding.setCustomSQLDelete(
 					source.getCustomSqlDelete().getSql(),
 					source.getCustomSqlDelete().isCallable(),
 					source.getCustomSqlDelete().getCheckStyle()
 			);
 		}
 		if ( source.getCustomSqlDeleteAll() != null ) {
 			binding.setCustomSQLDeleteAll(
 					source.getCustomSqlDeleteAll().getSql(),
 					source.getCustomSqlDeleteAll().isCallable(),
 					source.getCustomSqlDeleteAll().getCheckStyle()
 			);
 		}
 
 		if ( source instanceof Sortable ) {
 			final Sortable sortable = (Sortable) source;
 			if ( sortable.isSorted() ) {
 				binding.setSorted( true );
 				if ( ! sortable.getComparatorName().equals( "natural" ) ) {
 					binding.setComparatorClassName( sortable.getComparatorName() );
 				}
 			}
 			else {
 				binding.setSorted( false );
 			}
 		}
 
 		if ( source instanceof Orderable ) {
 			if ( ( (Orderable) source ).isOrdered() ) {
 				binding.setOrderBy( ( (Orderable) source ).getOrder() );
 			}
 		}
 
 		final String cascadeStyle = source.getCascadeStyleName();
 		if ( cascadeStyle != null && cascadeStyle.contains( "delete-orphan" ) ) {
 			binding.setOrphanDelete( true );
 		}
 
 		for ( FilterSource filterSource : source.getFilterSources() ) {
 			String condition = filterSource.getCondition();
 			if ( condition == null ) {
 				final FilterDefinition filterDefinition = mappingDocument.getMetadataCollector().getFilterDefinition( filterSource.getName() );
 				if ( filterDefinition != null ) {
 					condition = filterDefinition.getDefaultFilterCondition();
 				}
 			}
 
 			binding.addFilter(
 					filterSource.getName(),
 					condition,
 					filterSource.shouldAutoInjectAliases(),
 					filterSource.getAliasToTableMap(),
 					filterSource.getAliasToEntityMap()
 			);
 		}
 	}
 
 	private void applyCaching(MappingDocument mappingDocument, Caching caching, Collection collection) {
 		if ( caching == null || caching.getRequested() == TruthValue.UNKNOWN ) {
 			// see if JPA's SharedCacheMode indicates we should implicitly apply caching
 			switch ( mappingDocument.getBuildingOptions().getSharedCacheMode() ) {
 				case ALL: {
 					caching = new Caching(
 							null,
 							mappingDocument.getBuildingOptions().getImplicitCacheAccessType(),
 							false,
 							TruthValue.UNKNOWN
 					);
 					break;
 				}
 				case NONE: {
 					// Ideally we'd disable all caching...
 					break;
 				}
 				case ENABLE_SELECTIVE: {
 					// this is default behavior for hbm.xml
 					break;
 				}
 				case DISABLE_SELECTIVE: {
 					// really makes no sense for hbm.xml
 					break;
 				}
 				default: {
 					// null or UNSPECIFIED, nothing to do.  IMO for hbm.xml this is equivalent
 					// to ENABLE_SELECTIVE
 					break;
 				}
 			}
 		}
 
 		if ( caching == null || caching.getRequested() == TruthValue.FALSE ) {
 			return;
 		}
 
 		if ( caching.getAccessType() != null ) {
 			collection.setCacheConcurrencyStrategy( caching.getAccessType().getExternalName() );
 		}
 		else {
 			collection.setCacheConcurrencyStrategy( mappingDocument.getBuildingOptions().getImplicitCacheAccessType().getExternalName() );
 		}
 		collection.setCacheRegionName( caching.getRegion() );
 //		collection.setCachingExplicitlyRequested( caching.getRequested() != TruthValue.UNKNOWN );
 	}
 
 	private Identifier determineTable(
 			MappingDocument sourceDocument,
 			String attributeName,
 			RelationalValueSourceContainer relationalValueSourceContainer) {
 		return determineTable( sourceDocument, attributeName, relationalValueSourceContainer.getRelationalValueSources() );
 	}
 
 	private Identifier determineTable(
 			MappingDocument mappingDocument,
 			SingularAttributeSourceEmbedded embeddedAttributeSource) {
 		Identifier tableName = null;
 		for ( AttributeSource attributeSource : embeddedAttributeSource.getEmbeddableSource().attributeSources() ) {
 			final Identifier determinedName;
 			if ( RelationalValueSourceContainer.class.isInstance( attributeSource ) ) {
 				determinedName = determineTable(
 						mappingDocument,
 						embeddedAttributeSource.getAttributeRole().getFullPath(),
 						(RelationalValueSourceContainer) attributeSource
 
 				);
 			}
 			else if ( SingularAttributeSourceEmbedded.class.isInstance( attributeSource ) ) {
 				determinedName = determineTable( mappingDocument, (SingularAttributeSourceEmbedded) attributeSource );
 			}
 			else if ( SingularAttributeSourceAny.class.isInstance( attributeSource ) ) {
 				determinedName = determineTable(
 						mappingDocument,
 						attributeSource.getAttributeRole().getFullPath(),
 						( (SingularAttributeSourceAny) attributeSource ).getKeySource().getRelationalValueSources()
 				);
 			}
 			else {
 				continue;
 			}
 
 			if (  EqualsHelper.equals( tableName, determinedName ) ) {
 				continue;
 			}
 
 			if ( tableName != null ) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"Attribute [%s] referenced columns from multiple tables: %s, %s",
 								embeddedAttributeSource.getAttributeRole().getFullPath(),
 								tableName,
 								determinedName
 						),
 						mappingDocument.getOrigin()
 				);
 			}
 
 			tableName = determinedName;
 		}
 
 		return tableName;
 	}
 
 	private Identifier determineTable(
 			MappingDocument mappingDocument,
 			String attributeName,
 			List<RelationalValueSource> relationalValueSources) {
 		String tableName = null;
 		for ( RelationalValueSource relationalValueSource : relationalValueSources ) {
 			if ( ColumnSource.class.isInstance( relationalValueSource ) ) {
 				final ColumnSource columnSource = (ColumnSource) relationalValueSource;
 				if ( EqualsHelper.equals( tableName, columnSource.getContainingTableName() ) ) {
 					continue;
 				}
 
 				if ( tableName != null ) {
 					throw new MappingException(
 							String.format(
 									Locale.ENGLISH,
 									"Attribute [%s] referenced columns from multiple tables: %s, %s",
 									attributeName,
 									tableName,
 									columnSource.getContainingTableName()
 							),
 							mappingDocument.getOrigin()
 					);
 				}
 
 				tableName = columnSource.getContainingTableName();
 			}
 		}
 
 		return database.toIdentifier( tableName );
 	}
 
 	private void bindSecondaryTable(
 			MappingDocument mappingDocument,
 			SecondaryTableSource secondaryTableSource,
 			Join secondaryTableJoin,
 			final EntityTableXref entityTableXref) {
 		final PersistentClass persistentClass = secondaryTableJoin.getPersistentClass();
 
 		final Identifier catalogName = determineCatalogName( secondaryTableSource.getTableSource() );
 		final Identifier schemaName = determineSchemaName( secondaryTableSource.getTableSource() );
 		final Namespace namespace = database.locateNamespace( catalogName, schemaName );
 
 		Table secondaryTable;
 		final Identifier logicalTableName;
 
 		if ( TableSource.class.isInstance( secondaryTableSource.getTableSource() ) ) {
 			final TableSource tableSource = (TableSource) secondaryTableSource.getTableSource();
 			logicalTableName = database.toIdentifier( tableSource.getExplicitTableName() );
 			secondaryTable = namespace.locateTable( logicalTableName );
 			if ( secondaryTable == null ) {
 				secondaryTable = namespace.createTable( logicalTableName, false );
 			}
 			else {
 				secondaryTable.setAbstract( false );
 			}
 
 			secondaryTable.setComment( tableSource.getComment() );
 		}
 		else {
 			final InLineViewSource inLineViewSource = (InLineViewSource) secondaryTableSource.getTableSource();
 			secondaryTable = new Table(
 					namespace,
 					inLineViewSource.getSelectStatement(),
 					false
 			);
 			logicalTableName = Identifier.toIdentifier( inLineViewSource.getLogicalName() );
 		}
 
 		secondaryTableJoin.setTable( secondaryTable );
 		entityTableXref.addSecondaryTable( mappingDocument, logicalTableName, secondaryTableJoin );
 
 		bindCustomSql(
 				mappingDocument,
 				secondaryTableSource,
 				secondaryTableJoin
 		);
 
 		secondaryTableJoin.setSequentialSelect( secondaryTableSource.getFetchStyle() == FetchStyle.SELECT );
 		secondaryTableJoin.setInverse( secondaryTableSource.isInverse() );
 		secondaryTableJoin.setOptional( secondaryTableSource.isOptional() );
 
 		if ( log.isDebugEnabled() ) {
 			log.debugf(
 					"Mapping entity secondary-table: %s -> %s",
 					persistentClass.getEntityName(),
 					secondaryTable.getName()
 			);
 		}
 
 		final SimpleValue keyBinding = new DependantValue(
 				mappingDocument.getMetadataCollector(),
 				secondaryTable,
 				persistentClass.getIdentifier()
 		);
 		if ( mappingDocument.getBuildingOptions().useNationalizedCharacterData() ) {
 			keyBinding.makeNationalized();
 		}
 		secondaryTableJoin.setKey( keyBinding );
 
 		keyBinding.setCascadeDeleteEnabled( secondaryTableSource.isCascadeDeleteEnabled() );
 
 		// NOTE : no Type info to bind...
 
 		relationalObjectBinder.bindColumns(
 				mappingDocument,
 				secondaryTableSource.getPrimaryKeyColumnSources(),
 				keyBinding,
 				secondaryTableSource.isOptional(),
 				new RelationalObjectBinder.ColumnNamingDelegate() {
 					int count = 0;
 					@Override
 					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 						final Column correspondingColumn = entityTableXref.getPrimaryTable().getPrimaryKey().getColumn( count++ );
 						return database.toIdentifier( correspondingColumn.getQuotedName() );
 					}
 				}
 		);
 
 		keyBinding.setForeignKeyName( secondaryTableSource.getExplicitForeignKeyName() );
 
 		secondaryTableJoin.createPrimaryKey();
 		secondaryTableJoin.createForeignKey();
 	}
 
 	private Property createEmbeddedAttribute(
 			MappingDocument sourceDocument,
 			SingularAttributeSourceEmbedded embeddedSource,
 			Component componentBinding,
 			String containingClassName) {
 		final String attributeName = embeddedSource.getName();
 
 		bindComponent(
 				sourceDocument,
 				embeddedSource.getEmbeddableSource(),
 				componentBinding,
 				containingClassName,
 				attributeName,
 				embeddedSource.getXmlNodeName(),
 				embeddedSource.isVirtualAttribute()
 		);
 
 		prepareValueTypeViaReflection(
 				sourceDocument,
 				componentBinding,
 				componentBinding.getComponentClassName(),
 				attributeName,
 				embeddedSource.getAttributeRole()
 		);
 
 		componentBinding.createForeignKey();
 
 		final Property attribute;
 		if ( embeddedSource.isVirtualAttribute() ) {
 			attribute = new SyntheticProperty() {
 				@Override
 				public String getPropertyAccessorName() {
 					return "embedded";
 				}
 			};
 		}
 		else {
 			attribute = new Property();
 		}
 		attribute.setValue( componentBinding );
 		bindProperty(
 				sourceDocument,
 				embeddedSource,
 				attribute
 		);
 
 		final String xmlNodeName = determineXmlNodeName( embeddedSource, componentBinding.getOwner().getNodeName() );
 		componentBinding.setNodeName( xmlNodeName );
 		attribute.setNodeName( xmlNodeName );
 
 		return attribute;
 	}
 
 	private Property createBasicAttribute(
 			MappingDocument sourceDocument,
 			final SingularAttributeSourceBasic attributeSource,
 			SimpleValue value,
 			String containingClassName) {
 		final String attributeName = attributeSource.getName();
 
 		bindSimpleValueType(
 				sourceDocument,
 				attributeSource.getTypeInformation(),
 				value
 		);
 
 		relationalObjectBinder.bindColumnsAndFormulas(
 				sourceDocument,
 				attributeSource.getRelationalValueSources(),
 				value,
 				true,
 				new RelationalObjectBinder.ColumnNamingDelegate() {
 					@Override
 					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 						return implicitNamingStrategy.determineBasicColumnName( attributeSource );
 					}
 				}
 		);
 
 
 		prepareValueTypeViaReflection(
 				sourceDocument,
 				value,
 				containingClassName,
 				attributeName,
 				attributeSource.getAttributeRole()
 		);
 
 //		// this is done here 'cos we might only know the type here (ugly!)
 //		// TODO: improve this a lot:
 //		if ( value instanceof ToOne ) {
 //			ToOne toOne = (ToOne) value;
 //			String propertyRef = toOne.getReferencedEntityAttributeName();
 //			if ( propertyRef != null ) {
 //				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
 //			}
 //			toOne.setCascadeDeleteEnabled( "cascade".equals( subnode.attributeValue( "on-delete" ) ) );
 //		}
 //		else if ( value instanceof Collection ) {
 //			Collection coll = (Collection) value;
 //			String propertyRef = coll.getReferencedEntityAttributeName();
 //			// not necessarily a *unique* property reference
 //			if ( propertyRef != null ) {
 //				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
 //			}
 //		}
 
 		value.createForeignKey();
 
 		Property property = new Property();
 		property.setValue( value );
 		bindProperty(
 				sourceDocument,
 				attributeSource,
 				property
 		);
 
 		return property;
 	}
 
 	private Property createOneToOneAttribute(
 			MappingDocument sourceDocument,
 			SingularAttributeSourceOneToOne oneToOneSource,
 			OneToOne oneToOneBinding,
 			String containingClassName) {
 		bindOneToOne( sourceDocument, oneToOneSource, oneToOneBinding );
 
 		prepareValueTypeViaReflection(
 				sourceDocument,
 				oneToOneBinding,
 				containingClassName,
 				oneToOneSource.getName(),
 				oneToOneSource.getAttributeRole()
 		);
 
 		final String propertyRef = oneToOneBinding.getReferencedPropertyName();
 		if ( propertyRef != null ) {
 			handlePropertyReference(
 					sourceDocument,
 					oneToOneBinding.getReferencedEntityName(),
 					propertyRef,
 					true,
 					"<one-to-one name=\"" + oneToOneSource.getName() + "\"/>"
 			);
 		}
 
 		oneToOneBinding.createForeignKey();
 
 		Property prop = new Property();
 		prop.setValue( oneToOneBinding );
 		bindProperty(
 				sourceDocument,
 				oneToOneSource,
 				prop
 		);
 
 		return prop;
 	}
 
 	private void handlePropertyReference(
 			MappingDocument mappingDocument,
 			String referencedEntityName,
 			String referencedPropertyName,
 			boolean isUnique,
 			String sourceElementSynopsis) {
 		PersistentClass entityBinding = mappingDocument.getMetadataCollector().getEntityBinding( referencedEntityName );
 		if ( entityBinding == null ) {
 			// entity may just not have been processed yet - set up a delayed handler
 			registerDelayedPropertyReferenceHandler(
 					new DelayedPropertyReferenceHandlerImpl(
 							referencedEntityName,
 							referencedPropertyName,
 							isUnique,
 							sourceElementSynopsis,
 							mappingDocument.getOrigin()
 					),
 					mappingDocument
 			);
 		}
 		else {
 			Property propertyBinding = entityBinding.getReferencedProperty( referencedPropertyName );
 			if ( propertyBinding == null ) {
 				// attribute may just not have been processed yet - set up a delayed handler
 				registerDelayedPropertyReferenceHandler(
 						new DelayedPropertyReferenceHandlerImpl(
 								referencedEntityName,
 								referencedPropertyName,
 								isUnique,
 								sourceElementSynopsis,
 								mappingDocument.getOrigin()
 						),
 						mappingDocument
 				);
 			}
 			else {
 				log.tracef(
 						"Property [%s.%s] referenced by property-ref [%s] was available - no need for delayed handling",
 						referencedEntityName,
 						referencedPropertyName,
 						sourceElementSynopsis
 				);
 				if ( isUnique ) {
 					( (SimpleValue) propertyBinding.getValue() ).setAlternateUniqueKey( true );
 				}
 			}
 		}
 	}
 
 	private void registerDelayedPropertyReferenceHandler(
 			DelayedPropertyReferenceHandlerImpl handler,
 			MetadataBuildingContext buildingContext) {
 		log.tracef(
 				"Property [%s.%s] referenced by property-ref [%s] was not yet available - creating delayed handler",
 				handler.referencedEntityName,
 				handler.referencedPropertyName,
 				handler.sourceElementSynopsis
 		);
 		buildingContext.getMetadataCollector().addDelayedPropertyReferenceHandler( handler );
 	}
 
 	public void bindOneToOne(
 			final MappingDocument sourceDocument,
 			final SingularAttributeSourceOneToOne oneToOneSource,
 			final OneToOne oneToOneBinding) {
 		oneToOneBinding.setPropertyName( oneToOneSource.getName() );
 
 		relationalObjectBinder.bindFormulas(
 				sourceDocument,
 				oneToOneSource.getFormulaSources(),
 				oneToOneBinding
 		);
 
 
 		if ( oneToOneSource.isConstrained() ) {
 			if ( oneToOneSource.getCascadeStyleName() != null
 					&& oneToOneSource.getCascadeStyleName().contains( "delete-orphan" ) ) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"one-to-one attribute [%s] cannot specify orphan delete cascading as it is constrained",
 								oneToOneSource.getAttributeRole().getFullPath()
 						),
 						sourceDocument.getOrigin()
 				);
 			}
 			oneToOneBinding.setConstrained( true );
 			oneToOneBinding.setForeignKeyType( ForeignKeyDirection.FROM_PARENT );
 		}
 		else {
 			oneToOneBinding.setForeignKeyType( ForeignKeyDirection.TO_PARENT );
 		}
 
 		oneToOneBinding.setLazy( oneToOneSource.getFetchCharacteristics().getFetchTiming() == FetchTiming.DELAYED );
 		oneToOneBinding.setFetchMode(
 				oneToOneSource.getFetchCharacteristics().getFetchStyle() == FetchStyle.SELECT
 						? FetchMode.SELECT
 						: FetchMode.JOIN
 		);
 		oneToOneBinding.setUnwrapProxy( oneToOneSource.getFetchCharacteristics().isUnwrapProxies() );
 
 
 		if ( StringHelper.isNotEmpty( oneToOneSource.getReferencedEntityAttributeName() ) ) {
 			oneToOneBinding.setReferencedPropertyName( oneToOneSource.getReferencedEntityAttributeName() );
 			oneToOneBinding.setReferenceToPrimaryKey( false );
 		}
 		else {
 			oneToOneBinding.setReferenceToPrimaryKey( true );
 		}
 
 		// todo : probably need some reflection here if null
 		oneToOneBinding.setReferencedEntityName( oneToOneSource.getReferencedEntityName() );
 
 		if ( oneToOneSource.isEmbedXml() == Boolean.TRUE ) {
 			DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfEmbedXmlSupport();
 		}
-		oneToOneBinding.setEmbedded( oneToOneSource.isEmbedXml() != Boolean.FALSE );
 
 		if ( StringHelper.isNotEmpty( oneToOneSource.getExplicitForeignKeyName() ) ) {
 			oneToOneBinding.setForeignKeyName( oneToOneSource.getExplicitForeignKeyName() );
 		}
 
 		oneToOneBinding.setCascadeDeleteEnabled( oneToOneSource.isCascadeDeleteEnabled() );
 	}
 
 	private Property createManyToOneAttribute(
 			MappingDocument sourceDocument,
 			SingularAttributeSourceManyToOne manyToOneSource,
 			ManyToOne manyToOneBinding,
 			String containingClassName) {
 		final String attributeName = manyToOneSource.getName();
 
 		final String referencedEntityName;
 		if ( manyToOneSource.getReferencedEntityName() != null ) {
 			referencedEntityName = manyToOneSource.getReferencedEntityName();
 		}
 		else {
 			Class reflectedPropertyClass = Helper.reflectedPropertyClass( sourceDocument, containingClassName, attributeName );
 			if ( reflectedPropertyClass != null ) {
 				referencedEntityName = reflectedPropertyClass.getName();
 			}
 			else {
 				prepareValueTypeViaReflection(
 						sourceDocument,
 						manyToOneBinding,
 						containingClassName,
 						attributeName,
 						manyToOneSource.getAttributeRole()
 				);
 				referencedEntityName = manyToOneBinding.getTypeName();
 			}
 		}
 
 		if ( manyToOneSource.isUnique() ) {
 			manyToOneBinding.markAsLogicalOneToOne();
 		}
 
 		bindManyToOneAttribute( sourceDocument, manyToOneSource, manyToOneBinding, referencedEntityName );
 
 		final String propertyRef = manyToOneBinding.getReferencedPropertyName();
 
 		if ( propertyRef != null ) {
 			handlePropertyReference(
 					sourceDocument,
 					manyToOneBinding.getReferencedEntityName(),
 					propertyRef,
 					true,
 					"<many-to-one name=\"" + manyToOneSource.getName() + "\"/>"
 			);
 		}
 
 		Property prop = new Property();
 		prop.setValue( manyToOneBinding );
 		bindProperty(
 				sourceDocument,
 				manyToOneSource,
 				prop
 		);
 
 		if ( StringHelper.isNotEmpty( manyToOneSource.getCascadeStyleName() ) ) {
 			// todo : would be better to delay this the end of binding (second pass, etc)
 			// in order to properly allow for a singular unique column for a many-to-one to
 			// also trigger a "logical one-to-one".  As-is, this can occasionally lead to
 			// false exceptions if the many-to-one column binding is delayed and the
 			// uniqueness is indicated on the <column/> rather than on the <many-to-one/>
 			//
 			// Ideally, would love to see a SimpleValue#validate approach, rather than a
 			// SimpleValue#isValid that is then handled at a higher level (Property, etc).
 			// The reason being that the current approach misses the exact reason
 			// a "validation" fails since it loses "context"
 			if ( manyToOneSource.getCascadeStyleName().contains( "delete-orphan" ) ) {
 				if ( !manyToOneBinding.isLogicalOneToOne() ) {
 					throw new MappingException(
 							String.format(
 									Locale.ENGLISH,
 									"many-to-one attribute [%s] specified delete-orphan but is not specified as unique; " +
 											"remove delete-orphan cascading or specify unique=\"true\"",
 									manyToOneSource.getAttributeRole().getFullPath()
 							),
 							sourceDocument.getOrigin()
 					);
 				}
 			}
 		}
 
 		return prop;
 	}
 
 	private void bindManyToOneAttribute(
 			final MappingDocument sourceDocument,
 			final SingularAttributeSourceManyToOne manyToOneSource,
 			ManyToOne manyToOneBinding,
 			String referencedEntityName) {
 		// NOTE : no type information to bind
 
 		manyToOneBinding.setReferencedEntityName( referencedEntityName );
 		if ( StringHelper.isNotEmpty( manyToOneSource.getReferencedEntityAttributeName() ) ) {
 			manyToOneBinding.setReferencedPropertyName( manyToOneSource.getReferencedEntityAttributeName() );
 			manyToOneBinding.setReferenceToPrimaryKey( false );
 		}
 		else {
 			manyToOneBinding.setReferenceToPrimaryKey( true );
 		}
 
 		manyToOneBinding.setLazy( manyToOneSource.getFetchCharacteristics().getFetchTiming() == FetchTiming.DELAYED );
 		manyToOneBinding.setUnwrapProxy( manyToOneSource.getFetchCharacteristics().isUnwrapProxies() );
 		manyToOneBinding.setFetchMode(
 				manyToOneSource.getFetchCharacteristics().getFetchStyle() == FetchStyle.SELECT
 						? FetchMode.SELECT
 						: FetchMode.JOIN
 		);
 
 		if ( manyToOneSource.isEmbedXml() == Boolean.TRUE ) {
 			DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfEmbedXmlSupport();
 		}
-		manyToOneBinding.setEmbedded( manyToOneSource.isEmbedXml() != Boolean.FALSE );
 
 		manyToOneBinding.setIgnoreNotFound( manyToOneSource.isIgnoreNotFound() );
 
 		if ( StringHelper.isNotEmpty( manyToOneSource.getExplicitForeignKeyName() ) ) {
 			manyToOneBinding.setForeignKeyName( manyToOneSource.getExplicitForeignKeyName() );
 		}
 
 		final ManyToOneColumnBinder columnBinder = new ManyToOneColumnBinder(
 				sourceDocument,
 				manyToOneSource,
 				manyToOneBinding,
 				referencedEntityName
 		);
 		final boolean canBindColumnsImmediately = columnBinder.canProcessImmediately();
 		if ( canBindColumnsImmediately ) {
 			columnBinder.doSecondPass( null );
 		}
 		else {
 			sourceDocument.getMetadataCollector().addSecondPass( columnBinder );
 		}
 
 		if ( !manyToOneSource.isIgnoreNotFound() ) {
 			// we skip creating the FK here since this setting tells us there
 			// cannot be a suitable/proper FK
 			final ManyToOneFkSecondPass fkSecondPass = new ManyToOneFkSecondPass(
 					sourceDocument,
 					manyToOneSource,
 					manyToOneBinding,
 					referencedEntityName
 			);
 
 			if ( canBindColumnsImmediately && fkSecondPass.canProcessImmediately() ) {
 				fkSecondPass.doSecondPass( null );
 			}
 			else {
 				sourceDocument.getMetadataCollector().addSecondPass( fkSecondPass );
 			}
 		}
 
 		manyToOneBinding.setCascadeDeleteEnabled( manyToOneSource.isCascadeDeleteEnabled() );
 	}
 
 	private Property createAnyAssociationAttribute(
 			MappingDocument sourceDocument,
 			SingularAttributeSourceAny anyMapping,
 			Any anyBinding,
 			String entityName) {
 		final String attributeName = anyMapping.getName();
 
 		bindAny( sourceDocument, anyMapping, anyBinding, anyMapping.getAttributeRole(), anyMapping.getAttributePath() );
 
 		prepareValueTypeViaReflection( sourceDocument, anyBinding, entityName, attributeName, anyMapping.getAttributeRole() );
 
 		anyBinding.createForeignKey();
 
 		Property prop = new Property();
 		prop.setValue( anyBinding );
 		bindProperty(
 				sourceDocument,
 				anyMapping,
 				prop
 		);
 
 		return prop;
 	}
 
 	private void bindAny(
 			MappingDocument sourceDocument,
 			final AnyMappingSource anyMapping,
 			Any anyBinding,
 			final AttributeRole attributeRole,
 			AttributePath attributePath) {
 		final TypeResolution keyTypeResolution = resolveType(
 				sourceDocument,
 				anyMapping.getKeySource().getTypeSource()
 		);
 		if ( keyTypeResolution != null ) {
 			anyBinding.setIdentifierType( keyTypeResolution.typeName );
 		}
 
 		final TypeResolution discriminatorTypeResolution = resolveType(
 				sourceDocument,
 				anyMapping.getDiscriminatorSource().getTypeSource()
 		);
 
 		if ( discriminatorTypeResolution != null ) {
 			anyBinding.setMetaType( discriminatorTypeResolution.typeName );
 			try {
 				final DiscriminatorType metaType = (DiscriminatorType) sourceDocument.getMetadataCollector()
 						.getTypeResolver()
 						.heuristicType( discriminatorTypeResolution.typeName );
 
 				final HashMap anyValueBindingMap = new HashMap();
 				for ( Map.Entry<String,String> discriminatorValueMappings : anyMapping.getDiscriminatorSource().getValueMappings().entrySet() ) {
 					try {
 						final Object discriminatorValue = metaType.stringToObject( discriminatorValueMappings.getKey() );
 						final String mappedEntityName = sourceDocument.qualifyClassName( discriminatorValueMappings.getValue() );
 
 						//noinspection unchecked
 						anyValueBindingMap.put( discriminatorValue, mappedEntityName );
 					}
 					catch (Exception e) {
 						throw new MappingException(
 								String.format(
 										Locale.ENGLISH,
 										"Unable to interpret <meta-value value=\"%s\" class=\"%s\"/> defined as part of <any/> attribute [%s]",
 										discriminatorValueMappings.getKey(),
 										discriminatorValueMappings.getValue(),
 										attributeRole.getFullPath()
 								),
 								e,
 								sourceDocument.getOrigin()
 						);
 					}
 
 				}
 				anyBinding.setMetaValues( anyValueBindingMap );
 			}
 			catch (ClassCastException e) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"Specified meta-type [%s] for <any/> attribute [%s] did not implement DiscriminatorType",
 								discriminatorTypeResolution.typeName,
 								attributeRole.getFullPath()
 						),
 						e,
 						sourceDocument.getOrigin()
 				);
 			}
 		}
 
 		relationalObjectBinder.bindColumnOrFormula(
 				sourceDocument,
 				anyMapping.getDiscriminatorSource().getRelationalValueSource(),
 				anyBinding,
 				true,
 				new RelationalObjectBinder.ColumnNamingDelegate() {
 					@Override
 					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 						return implicitNamingStrategy.determineAnyDiscriminatorColumnName(
 								anyMapping.getDiscriminatorSource()
 						);
 					}
 				}
 		);
 
 		relationalObjectBinder.bindColumnsAndFormulas(
 				sourceDocument,
 				anyMapping.getKeySource().getRelationalValueSources(),
 				anyBinding,
 				true,
 				new RelationalObjectBinder.ColumnNamingDelegate() {
 					@Override
 					public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
 						return implicitNamingStrategy.determineAnyKeyColumnName(
 								anyMapping.getKeySource()
 						);
 					}
 				}
 		);
 	}
 
 	private void prepareValueTypeViaReflection(
 			MappingDocument sourceDocument,
 			Value value,
 			String containingClassName,
 			String propertyName,
 			AttributeRole attributeRole) {
 		if ( StringHelper.isEmpty( propertyName ) ) {
 			throw new MappingException(
 					String.format(
 							Locale.ENGLISH,
 							"Attribute mapping must define a name attribute: containingClassName=[%s], propertyName=[%s], role=[%s]",
 							containingClassName,
 							propertyName,
 							attributeRole.getFullPath()
 					),
 					sourceDocument.getOrigin()
 			);
 		}
 
 		try {
 			value.setTypeUsingReflection( containingClassName, propertyName );
 		}
 		catch (org.hibernate.MappingException ome) {
 			throw new MappingException(
 					String.format(
 							Locale.ENGLISH,
 							"Error calling Value#setTypeUsingReflection: containingClassName=[%s], propertyName=[%s], role=[%s]",
 							containingClassName,
 							propertyName,
 							attributeRole.getFullPath()
 					),
 					ome,
 					sourceDocument.getOrigin()
 			);
 		}
 	}
 
 	private void bindProperty(
 			MappingDocument mappingDocument,
 			AttributeSource propertySource,
 			Property property) {
 		property.setName( propertySource.getName() );
 		property.setNodeName( determineXmlNodeName( propertySource, null ) );
 
 		property.setPropertyAccessorName(
 				StringHelper.isNotEmpty( propertySource.getPropertyAccessorName() )
 						? propertySource.getPropertyAccessorName()
 						: mappingDocument.getMappingDefaults().getImplicitPropertyAccessorName()
 		);
 
 		if ( propertySource instanceof CascadeStyleSource ) {
 			final CascadeStyleSource cascadeStyleSource = (CascadeStyleSource) propertySource;
 
 			property.setCascade(
 					StringHelper.isNotEmpty( cascadeStyleSource.getCascadeStyleName() )
 							? cascadeStyleSource.getCascadeStyleName()
 							: mappingDocument.getMappingDefaults().getImplicitCascadeStyleName()
 			);
 		}
 
 		property.setOptimisticLocked( propertySource.isIncludedInOptimisticLocking() );
 
 		if ( propertySource.isSingular() ) {
 			final SingularAttributeSource singularAttributeSource = (SingularAttributeSource) propertySource;
 
 			property.setInsertable( singularAttributeSource.isInsertable() );
 			property.setUpdateable( singularAttributeSource.isUpdatable() );
 
 			// NOTE : Property#is refers to whether a property is lazy via bytecode enhancement (not proxies)
 			property.setLazy( singularAttributeSource.isBytecodeLazy() );
 
 			final GenerationTiming generationTiming = singularAttributeSource.getGenerationTiming();
 			if ( generationTiming == GenerationTiming.ALWAYS || generationTiming == GenerationTiming.INSERT ) {
 				// we had generation specified...
 				//   	HBM only supports "database generated values"
 				property.setValueGenerationStrategy( new GeneratedValueGeneration( generationTiming ) );
 
 				// generated properties can *never* be insertable...
 				if ( property.isInsertable() ) {
 					if ( singularAttributeSource.isInsertable() == null ) {
 						// insertable simply because that is the user did not specify
 						// anything; just override it
 						property.setInsertable( false );
 					}
 					else {
 						// the user specifically supplied insert="true",
 						// which constitutes an illegal combo
 						throw new MappingException(
 								String.format(
 										Locale.ENGLISH,
 										"Cannot specify both insert=\"true\" and generated=\"%s\" for property %s",
 										generationTiming.name().toLowerCase(Locale.ROOT),
 										propertySource.getName()
 								),
 								mappingDocument.getOrigin()
 						);
 					}
 				}
 
 				// properties generated on update can never be updatable...
 				if ( property.isUpdateable() && generationTiming == GenerationTiming.ALWAYS ) {
 					if ( singularAttributeSource.isUpdatable() == null ) {
 						// updatable only because the user did not specify
 						// anything; just override it
 						property.setUpdateable( false );
 					}
 					else {
 						// the user specifically supplied update="true",
 						// which constitutes an illegal combo
 						throw new MappingException(
 								String.format(
 										Locale.ENGLISH,
 										"Cannot specify both update=\"true\" and generated=\"%s\" for property %s",
 										generationTiming.name().toLowerCase(Locale.ROOT),
 										propertySource.getName()
 								),
 								mappingDocument.getOrigin()
 						);
 					}
 				}
 			}
 		}
 
 		property.setMetaAttributes( propertySource.getToolingHintContext().getMetaAttributeMap() );
 
 		if ( log.isDebugEnabled() ) {
 			final StringBuilder message = new StringBuilder()
 					.append( "Mapped property: " )
 					.append( propertySource.getName() )
 					.append( " -> [" );
 			final Iterator itr = property.getValue().getColumnIterator();
 			while ( itr.hasNext() ) {
 				message.append( ( (Selectable) itr.next() ).getText() );
 				if ( itr.hasNext() ) {
 					message.append( ", " );
 				}
 			}
 			message.append( "]" );
 			log.debug( message.toString() );
 		}
 	}
 
 	private String determineXmlNodeName(AttributeSource propertySource, String fallbackXmlNodeName) {
 		String nodeName = propertySource.getXmlNodeName();
 		if ( StringHelper.isNotEmpty( nodeName ) ) {
 			DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfDomEntityModeSupport();
 		}
 		else {
 			nodeName = propertySource.getName();
 		}
 		if ( nodeName == null ) {
 			nodeName = fallbackXmlNodeName;
 		}
 
 		return nodeName;
 	}
 
 	private void bindComponent(
 			MappingDocument sourceDocument,
 			EmbeddableSource embeddableSource,
 			Component component,
 			String containingClassName,
 			String propertyName,
 			String xmlNodeName,
 			boolean isVirtual) {
 		final String fullRole = embeddableSource.getAttributeRoleBase().getFullPath();
 		final String explicitComponentClassName = extractExplicitComponentClassName( embeddableSource );
 
 		bindComponent(
 				sourceDocument,
 				fullRole,
 				embeddableSource,
 				component,
 				explicitComponentClassName,
 				containingClassName,
 				propertyName,
 				isVirtual,
 				embeddableSource.isDynamic(),
 				xmlNodeName
 		);
 	}
 
 	private String extractExplicitComponentClassName(EmbeddableSource embeddableSource) {
 		if ( embeddableSource.getTypeDescriptor() == null ) {
 			return null;
 		}
 
 		return embeddableSource.getTypeDescriptor().getName();
 	}
 
 	private void bindComponent(
 			MappingDocument sourceDocument,
 			String role,
 			EmbeddableSource embeddableSource,
 			Component componentBinding,
 			String explicitComponentClassName,
 			String containingClassName,
 			String propertyName,
 			boolean isVirtual,
 			boolean isDynamic,
 			String xmlNodeName) {
 
 		componentBinding.setMetaAttributes( embeddableSource.getToolingHintContext().getMetaAttributeMap() );
 
 		componentBinding.setRoleName( role );
 
 		componentBinding.setEmbedded( isVirtual );
 
 		// todo : better define the conditions in this if/else
 		if ( isDynamic ) {
 			// dynamic is represented as a Map
 			log.debugf( "Binding dynamic-component [%s]", role );
 			componentBinding.setDynamic( true );
 		}
 		else if ( isVirtual ) {
 			// virtual (what used to be called embedded) is just a conceptual composition...
 			// <properties/> for example
 			if ( componentBinding.getOwner().hasPojoRepresentation() ) {
 				log.debugf( "Binding virtual component [%s] to owner class [%s]", role, componentBinding.getOwner().getClassName() );
 				componentBinding.setComponentClassName( componentBinding.getOwner().getClassName() );
 			}
 			else {
 				log.debugf( "Binding virtual component [%s] as dynamic", role );
 				componentBinding.setDynamic( true );
 			}
 		}
 		else {
 			log.debugf( "Binding component [%s]", role );
 			if ( StringHelper.isNotEmpty( explicitComponentClassName ) ) {
 				log.debugf( "Binding component [%s] to explicitly specified class", role, explicitComponentClassName );
 				componentBinding.setComponentClassName( explicitComponentClassName );
 			}
 			else if ( componentBinding.getOwner().hasPojoRepresentation() ) {
 				log.tracef( "Attempting to determine component class by reflection %s", role );
 				final Class reflectedComponentClass;
 				if ( StringHelper.isNotEmpty( containingClassName ) && StringHelper.isNotEmpty( propertyName ) ) {
 					reflectedComponentClass = Helper.reflectedPropertyClass(
 							sourceDocument,
 							containingClassName,
 							propertyName
 					);
 				}
 				else {
 					reflectedComponentClass = null;
 				}
 
 				if ( reflectedComponentClass == null ) {
 					log.debugf(
 							"Unable to determine component class name via reflection, and explicit " +
 									"class name not given; role=[%s]",
 							role
 					);
 				}
 				else {
 					componentBinding.setComponentClassName( reflectedComponentClass.getName() );
 				}
 			}
 			else {
 				componentBinding.setDynamic( true );
 			}
 		}
 
 		String nodeName = xmlNodeName;
 		if ( StringHelper.isNotEmpty( nodeName ) ) {
 			DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfDomEntityModeSupport();
 		}
 		else {
 			nodeName = propertyName;
 		}
 		if ( nodeName == null ) {
 			nodeName = componentBinding.getOwner().getNodeName();
 		}
 		componentBinding.setNodeName( nodeName );
 
 		// todo : anything else to pass along?
 		bindAllCompositeAttributes(
 				sourceDocument,
 				embeddableSource,
 				componentBinding
 		);
 
 		if ( embeddableSource.getParentReferenceAttributeName() != null ) {
 			componentBinding.setParentProperty( embeddableSource.getParentReferenceAttributeName() );
 		}
 
 		if ( embeddableSource.isUnique() ) {
 			final ArrayList<Column> cols = new ArrayList<Column>();
 			final Iterator itr = componentBinding.getColumnIterator();
 			while ( itr.hasNext() ) {
 				final Object selectable = itr.next();
 				// skip formulas.  ugh, yes terrible naming of these methods :(
 				if ( !Column.class.isInstance( selectable ) ) {
 					continue;
 				}
 				cols.add( (Column) selectable );
 			}
 			// todo : we may need to delay this
 			componentBinding.getOwner().getTable().createUniqueKey( cols );
 		}
 
 		if ( embeddableSource.getTuplizerClassMap() != null ) {
 			if ( embeddableSource.getTuplizerClassMap().size() > 1 ) {
 				DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfMultipleEntityModeSupport();
 			}
 			for ( Map.Entry<EntityMode,String> tuplizerEntry : embeddableSource.getTuplizerClassMap().entrySet() ) {
 				componentBinding.addTuplizer(
 						tuplizerEntry.getKey(),
 						tuplizerEntry.getValue()
 				);
 			}
 		}
 	}
 
 	private void prepareComponentType(
 			MappingDocument sourceDocument,
 			String fullRole,
 			Component componentBinding,
 			String explicitComponentClassName,
 			String containingClassName,
 			String propertyName,
 			boolean isVirtual,
 			boolean isDynamic) {
 	}
 
 	private void bindAllCompositeAttributes(
 			MappingDocument sourceDocument,
 			EmbeddableSource embeddableSource,
 			Component component) {
 
 		for ( AttributeSource attributeSource : embeddableSource.attributeSources() ) {
 			Property attribute = null;
 
 			if ( SingularAttributeSourceBasic.class.isInstance( attributeSource ) ) {
 				attribute = createBasicAttribute(
 						sourceDocument,
 						(SingularAttributeSourceBasic) attributeSource,
 						new SimpleValue( sourceDocument.getMetadataCollector(), component.getTable() ),
 						component.getComponentClassName()
 				);
 			}
 			else if ( SingularAttributeSourceEmbedded.class.isInstance( attributeSource ) ) {
 				attribute = createEmbeddedAttribute(
 						sourceDocument,
 						(SingularAttributeSourceEmbedded) attributeSource,
 						new Component( sourceDocument.getMetadataCollector(), component ),
 						component.getComponentClassName()
 				);
 			}
 			else if ( SingularAttributeSourceManyToOne.class.isInstance( attributeSource ) ) {
 				attribute = createManyToOneAttribute(
 						sourceDocument,
 						(SingularAttributeSourceManyToOne) attributeSource,
 						new ManyToOne( sourceDocument.getMetadataCollector(), component.getTable() ),
 						component.getComponentClassName()
 				);
 			}
 			else if ( SingularAttributeSourceOneToOne.class.isInstance( attributeSource ) ) {
 				attribute = createOneToOneAttribute(
 						sourceDocument,
 						(SingularAttributeSourceOneToOne) attributeSource,
 						new OneToOne( sourceDocument.getMetadataCollector(), component.getTable(), component.getOwner() ),
 						component.getComponentClassName()
 				);
 			}
 			else if ( SingularAttributeSourceAny.class.isInstance( attributeSource ) ) {
 				attribute = createAnyAssociationAttribute(
 						sourceDocument,
 						(SingularAttributeSourceAny) attributeSource,
 						new Any( sourceDocument.getMetadataCollector(), component.getTable() ),
 						component.getComponentClassName()
 				);
 			}
 			else if ( PluralAttributeSource.class.isInstance( attributeSource ) ) {
 				attribute = createPluralAttribute(
 						sourceDocument,
 						(PluralAttributeSource) attributeSource,
 						component.getOwner()
 				);
 			}
 			else {
 				throw new AssertionFailure(
 						String.format(
 								Locale.ENGLISH,
 								"Unexpected AttributeSource sub-type [%s] as part of composite [%s]",
 								attributeSource.getClass().getName(),
 								attributeSource.getAttributeRole().getFullPath()
 						)
 
 				);
 			}
 
 			component.addProperty( attribute );
 		}
 	}
 
 	private static void bindSimpleValueType(
 			MappingDocument mappingDocument,
 			HibernateTypeSource typeSource,
 			SimpleValue simpleValue) {
 		if ( mappingDocument.getBuildingOptions().useNationalizedCharacterData() ) {
 			simpleValue.makeNationalized();
 		}
 
 		final TypeResolution typeResolution = resolveType( mappingDocument, typeSource );
 		if ( typeResolution == null ) {
 			// no explicit type info was found
 			return;
 		}
 
 		if ( CollectionHelper.isNotEmpty( typeResolution.parameters ) ) {
 			simpleValue.setTypeParameters( typeResolution.parameters );
 		}
 
 		if ( typeResolution.typeName != null ) {
 			simpleValue.setTypeName( typeResolution.typeName );
 		}
 	}
 
 	private static class TypeResolution {
 		private final String typeName;
 		private final Properties parameters;
 
 		public TypeResolution(String typeName, Properties parameters) {
 			this.typeName = typeName;
 			this.parameters = parameters;
 		}
 	}
 
 	private static TypeResolution resolveType(
 			MappingDocument sourceDocument,
 			HibernateTypeSource typeSource) {
 		if ( StringHelper.isEmpty( typeSource.getName() ) ) {
 			return null;
 		}
 
 		String typeName = typeSource.getName();
 		Properties typeParameters = new Properties();;
 
 		final TypeDefinition typeDefinition = sourceDocument.getMetadataCollector().getTypeDefinition( typeName );
 		if ( typeDefinition != null ) {
 			// the explicit name referred to a type-def
 			typeName = typeDefinition.getTypeImplementorClass().getName();
 			if ( typeDefinition.getParameters() != null ) {
 				typeParameters.putAll( typeDefinition.getParameters() );
 			}
 		}
 //		else {
 //			final BasicType basicType = sourceDocument.getMetadataCollector().getTypeResolver().basic( typeName );
 //			if ( basicType == null ) {
 //				throw new MappingException(
 //						String.format(
 //								Locale.ENGLISH,
 //								"Mapping named an explicit type [%s] which could not be resolved",
 //								typeName
 //						),
 //						sourceDocument.getOrigin()
 //				);
 //			}
 //		}
 
 		// parameters on the property mapping should override parameters in the type-def
 		if ( typeSource.getParameters() != null ) {
 			typeParameters.putAll( typeSource.getParameters() );
 		}
 
 		return new TypeResolution( typeName, typeParameters );
 	}
 
 	private Table bindEntityTableSpecification(
 			final MappingDocument mappingDocument,
 			TableSpecificationSource tableSpecSource,
 			Table denormalizedSuperTable,
 			final EntitySource entitySource,
 			PersistentClass entityDescriptor) {
 		final Namespace namespace = database.locateNamespace(
 				determineCatalogName( tableSpecSource ),
 				determineSchemaName( tableSpecSource )
 		);
 
 		final boolean isTable = TableSource.class.isInstance( tableSpecSource );
 		final boolean isAbstract = entityDescriptor.isAbstract() == null ? false : entityDescriptor.isAbstract();
 		final String subselect;
 		final Identifier logicalTableName;
 		final Table table;
 		if ( isTable ) {
 			final TableSource tableSource = (TableSource) tableSpecSource;
 
 			if ( StringHelper.isNotEmpty( tableSource.getExplicitTableName() ) ) {
 				logicalTableName = database.toIdentifier( tableSource.getExplicitTableName() );
 			}
 			else {
 				final ImplicitEntityNameSource implicitNamingSource = new ImplicitEntityNameSource() {
 					@Override
 					public EntityNaming getEntityNaming() {
 						return entitySource.getEntityNamingSource();
 					}
 
 					@Override
 					public MetadataBuildingContext getBuildingContext() {
 						return mappingDocument;
 					}
 				};
 				logicalTableName = mappingDocument.getBuildingOptions()
 						.getImplicitNamingStrategy()
 						.determinePrimaryTableName( implicitNamingSource );
 			}
 
 			if ( denormalizedSuperTable == null ) {
 				table = namespace.createTable( logicalTableName, isAbstract );
 			}
 			else {
 				table = namespace.createDenormalizedTable(
 						logicalTableName,
 						isAbstract,
 						denormalizedSuperTable
 				);
 			}
 		}
 		else {
 			final InLineViewSource inLineViewSource = (InLineViewSource) tableSpecSource;
 			subselect = inLineViewSource.getSelectStatement();
 			logicalTableName = database.toIdentifier( inLineViewSource.getLogicalName() );
 			if ( denormalizedSuperTable == null ) {
 				table = new Table( namespace, subselect, isAbstract );
 			}
 			else {
 				table = new DenormalizedTable( namespace, subselect, isAbstract, denormalizedSuperTable );
 			}
 			table.setName( logicalTableName.render() );
 		}
 
 		EntityTableXref superEntityTableXref = null;
 
 		if ( entitySource.getSuperType() != null ) {
 			//noinspection SuspiciousMethodCalls
 			final String superEntityName = ( (EntitySource) entitySource.getSuperType() ).getEntityNamingSource()
 					.getEntityName();
 			superEntityTableXref = mappingDocument.getMetadataCollector().getEntityTableXref( superEntityName );
 			if ( superEntityTableXref == null ) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"Unable to locate entity table xref for entity [%s] super-type [%s]",
 								entityDescriptor.getEntityName(),
 								superEntityName
 						),
 						mappingDocument.getOrigin()
 				);
 			}
 		}
 
 		mappingDocument.getMetadataCollector().addEntityTableXref(
 				entitySource.getEntityNamingSource().getEntityName(),
 				logicalTableName,
 				table,
 				superEntityTableXref
 		);
 
 		if ( isTable ) {
 			final TableSource tableSource = (TableSource) tableSpecSource;
 			table.setRowId( tableSource.getRowId() );
 			table.setComment( tableSource.getComment() );
 			if ( StringHelper.isNotEmpty( tableSource.getCheckConstraint() ) ) {
 				table.addCheckConstraint( tableSource.getCheckConstraint() );
 			}
 		}
 
 		mappingDocument.getMetadataCollector().addTableNameBinding( logicalTableName, table );
 
 		return table;
 	}
 
 	private Identifier determineCatalogName(TableSpecificationSource tableSpecSource) {
 		if ( StringHelper.isNotEmpty( tableSpecSource.getExplicitCatalogName() ) ) {
 			return database.toIdentifier( tableSpecSource.getExplicitCatalogName() );
 		}
 		else {
 			return database.getDefaultNamespace().getName().getCatalog();
 		}
 	}
 
 	private Identifier determineSchemaName(TableSpecificationSource tableSpecSource) {
 		if ( StringHelper.isNotEmpty( tableSpecSource.getExplicitSchemaName() ) ) {
 			return database.toIdentifier( tableSpecSource.getExplicitSchemaName() );
 		}
 		else {
 			return database.getDefaultNamespace().getName().getSchema();
 		}
 	}
 
 	private static void bindCustomSql(
 			MappingDocument sourceDocument,
 			EntitySource entitySource,
 			PersistentClass entityDescriptor) {
 		if ( entitySource.getCustomSqlInsert() != null ) {
 			entityDescriptor.setCustomSQLInsert(
 					entitySource.getCustomSqlInsert().getSql(),
 					entitySource.getCustomSqlInsert().isCallable(),
 					entitySource.getCustomSqlInsert().getCheckStyle()
 			);
 		}
 
 		if ( entitySource.getCustomSqlUpdate() != null ) {
 			entityDescriptor.setCustomSQLUpdate(
 					entitySource.getCustomSqlUpdate().getSql(),
 					entitySource.getCustomSqlUpdate().isCallable(),
 					entitySource.getCustomSqlUpdate().getCheckStyle()
 			);
 		}
 
 		if ( entitySource.getCustomSqlDelete() != null ) {
 			entityDescriptor.setCustomSQLDelete(
 					entitySource.getCustomSqlDelete().getSql(),
 					entitySource.getCustomSqlDelete().isCallable(),
 					entitySource.getCustomSqlDelete().getCheckStyle()
 			);
 		}
 
 		entityDescriptor.setLoaderName( entitySource.getCustomLoaderName() );
 	}
 
 	private static void bindCustomSql(
 			MappingDocument sourceDocument,
 			SecondaryTableSource secondaryTableSource,
 			Join secondaryTable) {
 		if ( secondaryTableSource.getCustomSqlInsert() != null ) {
 			secondaryTable.setCustomSQLInsert(
 					secondaryTableSource.getCustomSqlInsert().getSql(),
 					secondaryTableSource.getCustomSqlInsert().isCallable(),
 					secondaryTableSource.getCustomSqlInsert().getCheckStyle()
 			);
 		}
 
 		if ( secondaryTableSource.getCustomSqlUpdate() != null ) {
 			secondaryTable.setCustomSQLUpdate(
 					secondaryTableSource.getCustomSqlUpdate().getSql(),
 					secondaryTableSource.getCustomSqlUpdate().isCallable(),
 					secondaryTableSource.getCustomSqlUpdate().getCheckStyle()
 			);
 		}
 
 		if ( secondaryTableSource.getCustomSqlDelete() != null ) {
 			secondaryTable.setCustomSQLDelete(
 					secondaryTableSource.getCustomSqlDelete().getSql(),
 					secondaryTableSource.getCustomSqlDelete().isCallable(),
 					secondaryTableSource.getCustomSqlDelete().getCheckStyle()
 			);
 		}
 	}
 
 	private void registerSecondPass(SecondPass secondPass, MetadataBuildingContext context) {
 		context.getMetadataCollector().addSecondPass( secondPass );
 	}
 
 
 
 	public static final class DelayedPropertyReferenceHandlerImpl implements InFlightMetadataCollector.DelayedPropertyReferenceHandler {
 		public final String referencedEntityName;
 		public final String referencedPropertyName;
 		public final boolean isUnique;
 		private final String sourceElementSynopsis;
 		public final Origin propertyRefOrigin;
 
 		public DelayedPropertyReferenceHandlerImpl(
 				String referencedEntityName,
 				String referencedPropertyName,
 				boolean isUnique,
 				String sourceElementSynopsis,
 				Origin propertyRefOrigin) {
 			this.referencedEntityName = referencedEntityName;
 			this.referencedPropertyName = referencedPropertyName;
 			this.isUnique = isUnique;
 			this.sourceElementSynopsis = sourceElementSynopsis;
 			this.propertyRefOrigin = propertyRefOrigin;
 		}
 
 		public void process(InFlightMetadataCollector metadataCollector) {
 			log.tracef(
 					"Performing delayed property-ref handling [%s, %s, %s]",
 					referencedEntityName,
 					referencedPropertyName,
 					sourceElementSynopsis
 			);
 
 			PersistentClass entityBinding = metadataCollector.getEntityBinding( referencedEntityName );
 			if ( entityBinding == null ) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"property-ref [%s] referenced an unmapped entity [%s]",
 								sourceElementSynopsis,
 								referencedEntityName
 						),
 						propertyRefOrigin
 				);
 			}
 
 			Property propertyBinding = entityBinding.getReferencedProperty( referencedPropertyName );
 			if ( propertyBinding == null ) {
 				throw new MappingException(
 						String.format(
 								Locale.ENGLISH,
 								"property-ref [%s] referenced an unknown entity property [%s#%s]",
 								sourceElementSynopsis,
 								referencedEntityName,
 								referencedPropertyName
 						),
 						propertyRefOrigin
 				);
 			}
 
 			if ( isUnique ) {
 				( (SimpleValue) propertyBinding.getValue() ).setAlternateUniqueKey( true );
 			}
 		}
 	}
 
 
 	private abstract class AbstractPluralAttributeSecondPass implements SecondPass {
 		private final MappingDocument mappingDocument;
 		private final PluralAttributeSource pluralAttributeSource;
 		private final Collection collectionBinding;
 
 		protected AbstractPluralAttributeSecondPass(
 				MappingDocument mappingDocument,
 				PluralAttributeSource pluralAttributeSource,
 				Collection collectionBinding) {
 			this.mappingDocument = mappingDocument;
 			this.pluralAttributeSource = pluralAttributeSource;
 			this.collectionBinding = collectionBinding;
 		}
 
 		public MappingDocument getMappingDocument() {
 			return mappingDocument;
 		}
 
 		public PluralAttributeSource getPluralAttributeSource() {
 			return pluralAttributeSource;
 		}
 
 		public Collection getCollectionBinding() {
 			return collectionBinding;
 		}
 
 		@Override
 		public void doSecondPass(Map persistentClasses) throws org.hibernate.MappingException {
 			bindCollectionTable();
 
 			bindCollectionKey();
 			bindCollectionIdentifier();
 			bindCollectionIndex();
 			bindCollectionElement();
 
 			createBackReferences();
 
 			collectionBinding.createAllKeys();
 
 			if ( debugEnabled ) {
 				log.debugf( "Mapped collection : " + getPluralAttributeSource().getAttributeRole().getFullPath() );
 				log.debugf( "   + table -> " + getCollectionBinding().getTable().getName() );
 				log.debugf( "   + key -> " + columns( getCollectionBinding().getKey() ) );
 				if ( getCollectionBinding().isIndexed() ) {
 					log.debugf( "   + index -> " + columns( ( (IndexedCollection) getCollectionBinding() ).getIndex() ) );
 				}
 				if ( getCollectionBinding().isOneToMany() ) {
 					log.debugf( "   + one-to-many -> " + ( (OneToMany) getCollectionBinding().getElement() ).getReferencedEntityName() );
 				}
 				else {
 					log.debugf( "   + element -> " + columns( getCollectionBinding().getElement() ) );
 				}
 			}
 		}
 
 		private String columns(Value value) {
 			final StringBuilder builder = new StringBuilder();
 			final Iterator<Selectable> selectableItr = value.getColumnIterator();
 			while ( selectableItr.hasNext() ) {
 				builder.append( selectableItr.next().getText() );
 				if ( selectableItr.hasNext() ) {
 					builder.append( ", " );
 				}
 			}
 			return builder.toString();
 		}
 
 		private void bindCollectionTable() {
 			// 2 main branches here:
 			//		1) one-to-many
 			//		2) everything else
 
 			if ( pluralAttributeSource.getElementSource() instanceof PluralAttributeElementSourceOneToMany ) {
 				// For one-to-many mappings, the "collection table" is the same as the table
 				// of the associated entity (the entity making up the collection elements).
 				// So lookup the associated entity and use its table here
 
 				final PluralAttributeElementSourceOneToMany elementSource =
 						(PluralAttributeElementSourceOneToMany) pluralAttributeSource.getElementSource();
 
 				final PersistentClass persistentClass = mappingDocument.getMetadataCollector()
 						.getEntityBinding( elementSource.getReferencedEntityName() );
 				if ( persistentClass == null ) {
 					throw new MappingException(
 							String.format(
 									Locale.ENGLISH,
 									"Association [%s] references an unmapped entity [%s]",
 									pluralAttributeSource.getAttributeRole().getFullPath(),
 									pluralAttributeSource.getAttributeRole().getFullPath()
 							),
 							mappingDocument.getOrigin()
 					);
 				}
 
 				// even though <key/> defines a property-ref I do not see where legacy
 				// code ever attempts to use that to "adjust" the table in its use to
 				// the actual table the referenced property belongs to.
 				// todo : for correctness, though, we should look into that ^^
 				collectionBinding.setCollectionTable( persistentClass.getTable() );
 			}
 			else {
 				final TableSpecificationSource tableSpecSource = pluralAttributeSource.getCollectionTableSpecificationSource();
 				final Identifier logicalCatalogName = determineCatalogName( tableSpecSource );
 				final Identifier logicalSchemaName = determineSchemaName( tableSpecSource );
 				final Namespace namespace = database.locateNamespace( logicalCatalogName, logicalSchemaName );
 
 				final Table collectionTable;
 
 				if ( tableSpecSource instanceof TableSource ) {
 					final TableSource tableSource = (TableSource) tableSpecSource;
 					Identifier logicalName;
 
 					if ( StringHelper.isNotEmpty( tableSource.getExplicitTableName() ) ) {
 						logicalName = Identifier.toIdentifier(
 								tableSource.getExplicitTableName(),
 								mappingDocument.getMappingDefaults().shouldImplicitlyQuoteIdentifiers()
 						);
 					}
 					else {
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
index 35e1b1803b..9bbd3157f7 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
@@ -1,312 +1,311 @@
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
-					manyToOne.setEmbedded( value.isEmbedded() );
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
index 4f92d78464..fb7c0230af 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
@@ -1,726 +1,707 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Properties;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.FilterConfiguration;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.CollectionType;
 import org.hibernate.type.Type;
 
 /**
  * Mapping for a collection. Subclasses specialize to particular collection styles.
  *
  * @author Gavin King
  */
 public abstract class Collection implements Fetchable, Value, Filterable {
 
 	public static final String DEFAULT_ELEMENT_COLUMN_NAME = "elt";
 	public static final String DEFAULT_KEY_COLUMN_NAME = "id";
 
 	private final MetadataImplementor metadata;
 	private PersistentClass owner;
 
 	private KeyValue key;
 	private Value element;
 	private Table collectionTable;
 	private String role;
 	private boolean lazy;
 	private boolean extraLazy;
 	private boolean inverse;
 	private boolean mutable = true;
 	private boolean subselectLoadable;
 	private String cacheConcurrencyStrategy;
 	private String cacheRegionName;
 	private String orderBy;
 	private String where;
 	private String manyToManyWhere;
 	private String manyToManyOrderBy;
 	private String referencedPropertyName;
 	private String nodeName;
 	private String elementNodeName;
 	private String mappedByProperty;
 	private boolean sorted;
 	private Comparator comparator;
 	private String comparatorClassName;
 	private boolean orphanDelete;
 	private int batchSize = -1;
 	private FetchMode fetchMode;
 	private boolean embedded = true;
 	private boolean optimisticLocked = true;
 	private Class collectionPersisterClass;
 	private String typeName;
 	private Properties typeParameters;
 	private final java.util.List filters = new ArrayList();
 	private final java.util.List manyToManyFilters = new ArrayList();
 	private final java.util.Set<String> synchronizedTables = new HashSet<String>();
 
 	private String customSQLInsert;
 	private boolean customInsertCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private String customSQLUpdate;
 	private boolean customUpdateCallable;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private String customSQLDelete;
 	private boolean customDeleteCallable;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 	private String customSQLDeleteAll;
 	private boolean customDeleteAllCallable;
 	private ExecuteUpdateResultCheckStyle deleteAllCheckStyle;
 
 	private String loaderName;
 
 	protected Collection(MetadataImplementor metadata, PersistentClass owner) {
 		this.metadata = metadata;
 		this.owner = owner;
 	}
 
 	public MetadataImplementor getMetadata() {
 		return metadata;
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return getMetadata().getMetadataBuildingOptions().getServiceRegistry();
 	}
 
 	public boolean isSet() {
 		return false;
 	}
 
 	public KeyValue getKey() {
 		return key;
 	}
 
 	public Value getElement() {
 		return element;
 	}
 
 	public boolean isIndexed() {
 		return false;
 	}
 
 	public Table getCollectionTable() {
 		return collectionTable;
 	}
 
 	public void setCollectionTable(Table table) {
 		this.collectionTable = table;
 	}
 
 	public boolean isSorted() {
 		return sorted;
 	}
 
 	public Comparator getComparator() {
 		if ( comparator == null && comparatorClassName != null ) {
 			try {
 				final ClassLoaderService classLoaderService = getMetadata().getMetadataBuildingOptions()
 						.getServiceRegistry()
 						.getService( ClassLoaderService.class );
 				setComparator( (Comparator) classLoaderService.classForName( comparatorClassName ).newInstance() );
 			}
 			catch (Exception e) {
 				throw new MappingException(
 						"Could not instantiate comparator class [" + comparatorClassName
 								+ "] for collection " + getRole()
 				);
 			}
 		}
 		return comparator;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public abstract CollectionType getDefaultCollectionType() throws MappingException;
 
 	public boolean isPrimitiveArray() {
 		return false;
 	}
 
 	public boolean isArray() {
 		return false;
 	}
 
 	public boolean hasFormula() {
 		return false;
 	}
 
 	public boolean isOneToMany() {
 		return element instanceof OneToMany;
 	}
 
 	public boolean isInverse() {
 		return inverse;
 	}
 
 	public String getOwnerEntityName() {
 		return owner.getEntityName();
 	}
 
 	public String getOrderBy() {
 		return orderBy;
 	}
 
 	public void setComparator(Comparator comparator) {
 		this.comparator = comparator;
 	}
 
 	public void setElement(Value element) {
 		this.element = element;
 	}
 
 	public void setKey(KeyValue key) {
 		this.key = key;
 	}
 
 	public void setOrderBy(String orderBy) {
 		this.orderBy = orderBy;
 	}
 
 	public void setRole(String role) {
 		this.role = role;
 	}
 
 	public void setSorted(boolean sorted) {
 		this.sorted = sorted;
 	}
 
 	public void setInverse(boolean inverse) {
 		this.inverse = inverse;
 	}
 
 	public PersistentClass getOwner() {
 		return owner;
 	}
 
 	/**
 	 * @param owner The owner
 	 *
 	 * @deprecated Inject the owner into constructor.
 	 */
 	@Deprecated
 	public void setOwner(PersistentClass owner) {
 		this.owner = owner;
 	}
 
 	public String getWhere() {
 		return where;
 	}
 
 	public void setWhere(String where) {
 		this.where = where;
 	}
 
 	public String getManyToManyWhere() {
 		return manyToManyWhere;
 	}
 
 	public void setManyToManyWhere(String manyToManyWhere) {
 		this.manyToManyWhere = manyToManyWhere;
 	}
 
 	public String getManyToManyOrdering() {
 		return manyToManyOrderBy;
 	}
 
 	public void setManyToManyOrdering(String orderFragment) {
 		this.manyToManyOrderBy = orderFragment;
 	}
 
 	public boolean isIdentified() {
 		return false;
 	}
 
 	public boolean hasOrphanDelete() {
 		return orphanDelete;
 	}
 
 	public void setOrphanDelete(boolean orphanDelete) {
 		this.orphanDelete = orphanDelete;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public void setBatchSize(int i) {
 		batchSize = i;
 	}
 
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public void setFetchMode(FetchMode fetchMode) {
 		this.fetchMode = fetchMode;
 	}
 
 	public void setCollectionPersisterClass(Class persister) {
 		this.collectionPersisterClass = persister;
 	}
 
 	public Class getCollectionPersisterClass() {
 		return collectionPersisterClass;
 	}
 
 	public void validate(Mapping mapping) throws MappingException {
 		assert getKey() != null : "Collection key not bound : " + getRole();
 		assert getElement() != null : "Collection element not bound : " + getRole();
 
 		if ( getKey().isCascadeDeleteEnabled() && ( !isInverse() || !isOneToMany() ) ) {
 			throw new MappingException(
 					"only inverse one-to-many associations may use on-delete=\"cascade\": "
 							+ getRole()
 			);
 		}
 		if ( !getKey().isValid( mapping ) ) {
 			throw new MappingException(
 					"collection foreign key mapping has wrong number of columns: "
 							+ getRole()
 							+ " type: "
 							+ getKey().getType().getName()
 			);
 		}
 		if ( !getElement().isValid( mapping ) ) {
 			throw new MappingException(
 					"collection element mapping has wrong number of columns: "
 							+ getRole()
 							+ " type: "
 							+ getElement().getType().getName()
 			);
 		}
 
 		checkColumnDuplication();
 
 		if ( elementNodeName != null && elementNodeName.startsWith( "@" ) ) {
 			throw new MappingException( "element node must not be an attribute: " + elementNodeName );
 		}
 		if ( elementNodeName != null && elementNodeName.equals( "." ) ) {
 			throw new MappingException( "element node must not be the parent: " + elementNodeName );
 		}
 		if ( nodeName != null && nodeName.indexOf( '@' ) > -1 ) {
 			throw new MappingException( "collection node must not be an attribute: " + elementNodeName );
 		}
 	}
 
 	private void checkColumnDuplication(java.util.Set distinctColumns, Iterator columns)
 			throws MappingException {
 		while ( columns.hasNext() ) {
 			Selectable s = (Selectable) columns.next();
 			if ( !s.isFormula() ) {
 				Column col = (Column) s;
 				if ( !distinctColumns.add( col.getName() ) ) {
 					throw new MappingException(
 							"Repeated column in mapping for collection: "
 									+ getRole()
 									+ " column: "
 									+ col.getName()
 					);
 				}
 			}
 		}
 	}
 
 	private void checkColumnDuplication() throws MappingException {
 		HashSet cols = new HashSet();
 		checkColumnDuplication( cols, getKey().getColumnIterator() );
 		if ( isIndexed() ) {
 			checkColumnDuplication(
 					cols, ( (IndexedCollection) this )
 							.getIndex()
 							.getColumnIterator()
 			);
 		}
 		if ( isIdentified() ) {
 			checkColumnDuplication(
 					cols, ( (IdentifierCollection) this )
 							.getIdentifier()
 							.getColumnIterator()
 			);
 		}
 		if ( !isOneToMany() ) {
 			checkColumnDuplication( cols, getElement().getColumnIterator() );
 		}
 	}
 
 	public Iterator<Selectable> getColumnIterator() {
 		return Collections.<Selectable>emptyList().iterator();
 	}
 
 	public int getColumnSpan() {
 		return 0;
 	}
 
 	public Type getType() throws MappingException {
 		return getCollectionType();
 	}
 
 	public CollectionType getCollectionType() {
 		if ( typeName == null ) {
 			return getDefaultCollectionType();
 		}
 		else {
 			return metadata.getTypeResolver()
 					.getTypeFactory()
 					.customCollection( typeName, typeParameters, role, referencedPropertyName );
 		}
 	}
 
 	public boolean isNullable() {
 		return true;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return false;
 	}
 
 	public Table getTable() {
 		return owner.getTable();
 	}
 
 	public void createForeignKey() {
 	}
 
 	public boolean isSimpleValue() {
 		return false;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		return true;
 	}
 
 	private void createForeignKeys() throws MappingException {
 		// if ( !isInverse() ) { // for inverse collections, let the "other end" handle it
 		if ( referencedPropertyName == null ) {
 			getElement().createForeignKey();
 			key.createForeignKeyOfEntity( getOwner().getEntityName() );
 		}
 		// }
 	}
 
 	abstract void createPrimaryKey();
 
 	public void createAllKeys() throws MappingException {
 		createForeignKeys();
 		if ( !isInverse() ) {
 			createPrimaryKey();
 		}
 	}
 
 	public String getCacheConcurrencyStrategy() {
 		return cacheConcurrencyStrategy;
 	}
 
 	public void setCacheConcurrencyStrategy(String cacheConcurrencyStrategy) {
 		this.cacheConcurrencyStrategy = cacheConcurrencyStrategy;
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) {
 	}
 
 	public String getCacheRegionName() {
 		return cacheRegionName == null ? role : cacheRegionName;
 	}
 
 	public void setCacheRegionName(String cacheRegionName) {
 		this.cacheRegionName = cacheRegionName;
 	}
 
 
 	public void setCustomSQLInsert(String customSQLInsert, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLInsert = customSQLInsert;
 		this.customInsertCallable = callable;
 		this.insertCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLInsert() {
 		return customSQLInsert;
 	}
 
 	public boolean isCustomInsertCallable() {
 		return customInsertCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLInsertCheckStyle() {
 		return insertCheckStyle;
 	}
 
 	public void setCustomSQLUpdate(String customSQLUpdate, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLUpdate = customSQLUpdate;
 		this.customUpdateCallable = callable;
 		this.updateCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLUpdate() {
 		return customSQLUpdate;
 	}
 
 	public boolean isCustomUpdateCallable() {
 		return customUpdateCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLUpdateCheckStyle() {
 		return updateCheckStyle;
 	}
 
 	public void setCustomSQLDelete(String customSQLDelete, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLDelete = customSQLDelete;
 		this.customDeleteCallable = callable;
 		this.deleteCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLDelete() {
 		return customSQLDelete;
 	}
 
 	public boolean isCustomDeleteCallable() {
 		return customDeleteCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteCheckStyle() {
 		return deleteCheckStyle;
 	}
 
 	public void setCustomSQLDeleteAll(
 			String customSQLDeleteAll,
 			boolean callable,
 			ExecuteUpdateResultCheckStyle checkStyle) {
 		this.customSQLDeleteAll = customSQLDeleteAll;
 		this.customDeleteAllCallable = callable;
 		this.deleteAllCheckStyle = checkStyle;
 	}
 
 	public String getCustomSQLDeleteAll() {
 		return customSQLDeleteAll;
 	}
 
 	public boolean isCustomDeleteAllCallable() {
 		return customDeleteAllCallable;
 	}
 
 	public ExecuteUpdateResultCheckStyle getCustomSQLDeleteAllCheckStyle() {
 		return deleteAllCheckStyle;
 	}
 
 	public void addFilter(
 			String name,
 			String condition,
 			boolean autoAliasInjection,
 			java.util.Map<String, String> aliasTableMap,
 			java.util.Map<String, String> aliasEntityMap) {
 		filters.add(
 				new FilterConfiguration(
 						name,
 						condition,
 						autoAliasInjection,
 						aliasTableMap,
 						aliasEntityMap,
 						null
 				)
 		);
 	}
 
 	public java.util.List getFilters() {
 		return filters;
 	}
 
 	public void addManyToManyFilter(
 			String name,
 			String condition,
 			boolean autoAliasInjection,
 			java.util.Map<String, String> aliasTableMap,
 			java.util.Map<String, String> aliasEntityMap) {
 		manyToManyFilters.add(
 				new FilterConfiguration(
 						name,
 						condition,
 						autoAliasInjection,
 						aliasTableMap,
 						aliasEntityMap,
 						null
 				)
 		);
 	}
 
 	public java.util.List getManyToManyFilters() {
 		return manyToManyFilters;
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
 	public java.util.Set<String> getSynchronizedTables() {
 		return synchronizedTables;
 	}
 
 	public String getLoaderName() {
 		return loaderName;
 	}
 
 	public void setLoaderName(String name) {
 		this.loaderName = name;
 	}
 
 	public String getReferencedPropertyName() {
 		return referencedPropertyName;
 	}
 
 	public void setReferencedPropertyName(String propertyRef) {
 		this.referencedPropertyName = propertyRef;
 	}
 
 	public boolean isOptimisticLocked() {
 		return optimisticLocked;
 	}
 
 	public void setOptimisticLocked(boolean optimisticLocked) {
 		this.optimisticLocked = optimisticLocked;
 	}
 
 	public boolean isMap() {
 		return false;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 
 	public void setTypeName(String typeName) {
 		this.typeName = typeName;
 	}
 
 	public Properties getTypeParameters() {
 		return typeParameters;
 	}
 
 	public void setTypeParameters(Properties parameterMap) {
 		this.typeParameters = parameterMap;
 	}
 
 	public void setTypeParameters(java.util.Map parameterMap) {
 		if ( parameterMap instanceof Properties ) {
 			this.typeParameters = (Properties) parameterMap;
 		}
 		else {
 			this.typeParameters = new Properties();
 			typeParameters.putAll( parameterMap );
 		}
 	}
 
 	public boolean[] getColumnInsertability() {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
 	public boolean[] getColumnUpdateability() {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
 	public String getNodeName() {
 		return nodeName;
 	}
 
 	public void setNodeName(String nodeName) {
 		this.nodeName = nodeName;
 	}
 
 	public String getElementNodeName() {
 		return elementNodeName;
 	}
 
 	public void setElementNodeName(String elementNodeName) {
 		this.elementNodeName = elementNodeName;
 	}
 
-	/**
-	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public boolean isEmbedded() {
-		return embedded;
-	}
-
-	/**
-	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public void setEmbedded(boolean embedded) {
-		this.embedded = embedded;
-	}
-
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 
-
 	public void setSubselectLoadable(boolean subqueryLoadable) {
 		this.subselectLoadable = subqueryLoadable;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public void setMutable(boolean mutable) {
 		this.mutable = mutable;
 	}
 
 	public boolean isExtraLazy() {
 		return extraLazy;
 	}
 
 	public void setExtraLazy(boolean extraLazy) {
 		this.extraLazy = extraLazy;
 	}
 
 	public boolean hasOrder() {
 		return orderBy != null || manyToManyOrderBy != null;
 	}
 
 	public void setComparatorClassName(String comparatorClassName) {
 		this.comparatorClassName = comparatorClassName;
 	}
 
 	public String getComparatorClassName() {
 		return comparatorClassName;
 	}
 
 	public String getMappedByProperty() {
 		return mappedByProperty;
 	}
 
 	public void setMappedByProperty(String mappedByProperty) {
 		this.mappedByProperty = mappedByProperty;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java b/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
index 3b1da02065..a462df9795 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
@@ -1,171 +1,153 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import java.util.Iterator;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * A mapping for a one-to-many association
  *
  * @author Gavin King
  */
 public class OneToMany implements Value {
 	private final MetadataImplementor metadata;
 	private final Table referencingTable;
 
 	private String referencedEntityName;
 	private PersistentClass associatedClass;
 	private boolean embedded;
 	private boolean ignoreNotFound;
 
 	public OneToMany(MetadataImplementor metadata, PersistentClass owner) throws MappingException {
 		this.metadata = metadata;
 		this.referencingTable = ( owner == null ) ? null : owner.getTable();
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return metadata.getMetadataBuildingOptions().getServiceRegistry();
 	}
 
 	private EntityType getEntityType() {
 		return metadata.getTypeResolver().getTypeFactory().manyToOne(
 				getReferencedEntityName(),
 				true,
 				null,
 				false,
 				false,
 				isIgnoreNotFound(),
 				false
 		);
 	}
 
 	public PersistentClass getAssociatedClass() {
 		return associatedClass;
 	}
 
 	/**
 	 * Associated entity on the many side
 	 */
 	public void setAssociatedClass(PersistentClass associatedClass) {
 		this.associatedClass = associatedClass;
 	}
 
 	public void createForeignKey() {
 		// no foreign key element of for a one-to-many
 	}
 
 	public Iterator<Selectable> getColumnIterator() {
 		return associatedClass.getKey().getColumnIterator();
 	}
 
 	public int getColumnSpan() {
 		return associatedClass.getKey().getColumnSpan();
 	}
 
 	public FetchMode getFetchMode() {
 		return FetchMode.JOIN;
 	}
 
 	/**
 	 * Table of the owner entity (the "one" side)
 	 */
 	public Table getTable() {
 		return referencingTable;
 	}
 
 	public Type getType() {
 		return getEntityType();
 	}
 
 	public boolean isNullable() {
 		return false;
 	}
 
 	public boolean isSimpleValue() {
 		return false;
 	}
 
 	public boolean isAlternateUniqueKey() {
 		return false;
 	}
 
 	public boolean hasFormula() {
 		return false;
 	}
 
 	public boolean isValid(Mapping mapping) throws MappingException {
 		if ( referencedEntityName == null ) {
 			throw new MappingException( "one to many association must specify the referenced entity" );
 		}
 		return true;
 	}
 
 	public String getReferencedEntityName() {
 		return referencedEntityName;
 	}
 
 	/**
 	 * Associated entity on the "many" side
 	 */
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName == null ? null : referencedEntityName.intern();
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) {
 	}
 
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept( this );
 	}
 
 
 	public boolean[] getColumnInsertability() {
 		//TODO: we could just return all false...
 		throw new UnsupportedOperationException();
 	}
 
 	public boolean[] getColumnUpdateability() {
 		//TODO: we could just return all false...
 		throw new UnsupportedOperationException();
 	}
 
-	/**
-	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public boolean isEmbedded() {
-		return embedded;
-	}
-
-	/**
-	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public void setEmbedded(boolean embedded) {
-		this.embedded = embedded;
-	}
-
 	public boolean isIgnoreNotFound() {
 		return ignoreNotFound;
 	}
 
 	public void setIgnoreNotFound(boolean ignoreNotFound) {
 		this.ignoreNotFound = ignoreNotFound;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java b/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
index 39890c294f..2351c4e157 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
@@ -1,128 +1,110 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.mapping;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
 import org.hibernate.boot.spi.MetadataImplementor;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.type.Type;
 
 /**
  * A simple-point association (ie. a reference to another entity).
  * @author Gavin King
  */
 public abstract class ToOne extends SimpleValue implements Fetchable {
 	private FetchMode fetchMode;
 	protected String referencedPropertyName;
 	private String referencedEntityName;
 	private boolean embedded;
 	private boolean lazy = true;
 	protected boolean unwrapProxy;
 	protected boolean referenceToPrimaryKey = true;
 
 	protected ToOne(MetadataImplementor metadata, Table table) {
 		super( metadata, table );
 	}
 
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public void setFetchMode(FetchMode fetchMode) {
 		this.fetchMode=fetchMode;
 	}
 
 	public abstract void createForeignKey() throws MappingException;
 	public abstract Type getType() throws MappingException;
 
 	public String getReferencedPropertyName() {
 		return referencedPropertyName;
 	}
 
 	public void setReferencedPropertyName(String name) {
 		referencedPropertyName = name==null ? null : name.intern();
 	}
 
 	public String getReferencedEntityName() {
 		return referencedEntityName;
 	}
 
 	public void setReferencedEntityName(String referencedEntityName) {
 		this.referencedEntityName = referencedEntityName==null ? 
 				null : referencedEntityName.intern();
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) throws MappingException {
 		if (referencedEntityName == null) {
 			final ClassLoaderService cls = getMetadata().getMetadataBuildingOptions()
 					.getServiceRegistry()
 					.getService( ClassLoaderService.class );
 			referencedEntityName = ReflectHelper.reflectedPropertyClass( className, propertyName, cls ).getName();
 		}
 	}
 
 	public boolean isTypeSpecified() {
 		return referencedEntityName!=null;
 	}
 	
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 
-	/**
-	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public boolean isEmbedded() {
-		return embedded;
-	}
-
-	/**
-	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public void setEmbedded(boolean embedded) {
-		this.embedded = embedded;
-	}
-
 	public boolean isValid(Mapping mapping) throws MappingException {
 		if (referencedEntityName==null) {
 			throw new MappingException("association must specify the referenced entity");
 		}
 		return super.isValid( mapping );
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 	
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public boolean isUnwrapProxy() {
 		return unwrapProxy;
 	}
 
 	public void setUnwrapProxy(boolean unwrapProxy) {
 		this.unwrapProxy = unwrapProxy;
 	}
 
 	public boolean isReferenceToPrimaryKey() {
 		return referenceToPrimaryKey;
 	}
 
 	public void setReferenceToPrimaryKey(boolean referenceToPrimaryKey) {
 		this.referenceToPrimaryKey = referenceToPrimaryKey;
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
index 60268dad47..c35fb7cd6a 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/DiscriminatorType.java
@@ -1,149 +1,140 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.persister.entity;
 
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.compare.EqualsHelper;
 import org.hibernate.type.AbstractType;
 import org.hibernate.type.Type;
 
 import org.dom4j.Node;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class DiscriminatorType extends AbstractType {
 	private final Type underlyingType;
 	private final Loadable persister;
 
 	public DiscriminatorType(Type underlyingType, Loadable persister) {
 		this.underlyingType = underlyingType;
 		this.persister = persister;
 	}
 
 	public Class getReturnedClass() {
 		return Class.class;
 	}
 
 	public String getName() {
 		return getClass().getName();
 	}
 
 	public boolean isMutable() {
 		return false;
 	}
 
 	public Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		return nullSafeGet( rs, names[0], session, owner );
 	}
 
 	public Object nullSafeGet(
 			ResultSet rs,
 			String name,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		final Object discriminatorValue = underlyingType.nullSafeGet( rs, name, session, owner );
 		final String entityName = persister.getSubclassForDiscriminatorValue( discriminatorValue );
 		if ( entityName == null ) {
 			throw new HibernateException( "Unable to resolve discriminator value [" + discriminatorValue + "] to entity name" );
 		}
 		final EntityPersister entityPersister = session.getEntityPersister( entityName, null );
 		return ( EntityMode.POJO == entityPersister.getEntityMode() ) ? entityPersister.getMappedClass() : entityName;
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		nullSafeSet( st, value, index, session );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 		String entityName = session.getFactory().getClassMetadata((Class) value).getEntityName();
 		Loadable entityPersister = (Loadable) session.getFactory().getEntityPersister(entityName);
 		underlyingType.nullSafeSet(st, entityPersister.getDiscriminatorValue(), index, session);
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		return value == null ? "[null]" : value.toString();
 	}
 
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return value;
 	}
 
 	public Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache)
 			throws HibernateException {
 		return original;
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return value == null
 				? ArrayHelper.FALSE
 				: ArrayHelper.TRUE;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return EqualsHelper.equals( old, current );
 	}
 
 
 	// simple delegation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return underlyingType.sqlTypes( mapping );
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return underlyingType.dictatedSizes( mapping );
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return underlyingType.defaultSizes( mapping );
 	}
 
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		return underlyingType.getColumnSpan( mapping );
 	}
-
-	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
-	}
-
-	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
-		// todo : ???
-		return null;
-	}
-
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
index e203ca19a8..41d1cc45b9 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractStandardBasicType.java
@@ -1,386 +1,377 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Environment;
 import org.hibernate.engine.jdbc.LobCreator;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.type.descriptor.WrapperOptions;
 import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
 import org.hibernate.type.descriptor.java.MutabilityPlan;
 import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
 
 import org.dom4j.Node;
 
 /**
  * Convenience base class for {@link BasicType} implementations
  *
  * @author Steve Ebersole
  * @author Brett Meyer
  */
 public abstract class AbstractStandardBasicType<T>
 		implements BasicType, StringRepresentableType<T>, ProcedureParameterExtractionAware<T> {
 
 	private static final Size DEFAULT_SIZE = new Size( 19, 2, 255, Size.LobMultiplier.NONE ); // to match legacy behavior
 	private final Size dictatedSize = new Size();
 
 	// Don't use final here.  Need to initialize after-the-fact
 	// by DynamicParameterizedTypes.
 	private SqlTypeDescriptor sqlTypeDescriptor;
 	private JavaTypeDescriptor<T> javaTypeDescriptor;
 	// sqlTypes need always to be in sync with sqlTypeDescriptor
 	private int[] sqlTypes;
 
 	public AbstractStandardBasicType(SqlTypeDescriptor sqlTypeDescriptor, JavaTypeDescriptor<T> javaTypeDescriptor) {
 		this.sqlTypeDescriptor = sqlTypeDescriptor;
 		this.sqlTypes = new int[] { sqlTypeDescriptor.getSqlType() };
 		this.javaTypeDescriptor = javaTypeDescriptor;
 	}
 
 	public T fromString(String string) {
 		return javaTypeDescriptor.fromString( string );
 	}
 
 	public String toString(T value) {
 		return javaTypeDescriptor.toString( value );
 	}
 
 	public T fromStringValue(String xml) throws HibernateException {
 		return fromString( xml );
 	}
 
 	protected MutabilityPlan<T> getMutabilityPlan() {
 		return javaTypeDescriptor.getMutabilityPlan();
 	}
 
 	protected T getReplacement(T original, T target, SessionImplementor session) {
 		if ( !isMutable() ) {
 			return original;
 		}
 		else if ( isEqual( original, target ) ) {
 			return original;
 		}
 		else {
 			return deepCopy( original );
 		}
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return value == null ? ArrayHelper.FALSE : ArrayHelper.TRUE;
 	}
 
 	public String[] getRegistrationKeys() {
 		return registerUnderJavaType()
 				? new String[] { getName(), javaTypeDescriptor.getJavaTypeClass().getName() }
 				: new String[] { getName() };
 	}
 
 	protected boolean registerUnderJavaType() {
 		return false;
 	}
 
 	protected static Size getDefaultSize() {
 		return DEFAULT_SIZE;
 	}
 
 	protected Size getDictatedSize() {
 		return dictatedSize;
 	}
 	
 	// final implementations ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public final JavaTypeDescriptor<T> getJavaTypeDescriptor() {
 		return javaTypeDescriptor;
 	}
 	
 	public final void setJavaTypeDescriptor( JavaTypeDescriptor<T> javaTypeDescriptor ) {
 		this.javaTypeDescriptor = javaTypeDescriptor;
 	}
 
 	public final SqlTypeDescriptor getSqlTypeDescriptor() {
 		return sqlTypeDescriptor;
 	}
 
 	public final void setSqlTypeDescriptor( SqlTypeDescriptor sqlTypeDescriptor ) {
 		this.sqlTypeDescriptor = sqlTypeDescriptor;
 		this.sqlTypes = new int[] { sqlTypeDescriptor.getSqlType() };
 	}
 
 	public final Class getReturnedClass() {
 		return javaTypeDescriptor.getJavaTypeClass();
 	}
 
 	public final int getColumnSpan(Mapping mapping) throws MappingException {
 		return 1;
 	}
 
 	public final int[] sqlTypes(Mapping mapping) throws MappingException {
 		return sqlTypes;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return new Size[] { getDictatedSize() };
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return new Size[] { getDefaultSize() };
 	}
 
 	public final boolean isAssociationType() {
 		return false;
 	}
 
 	public final boolean isCollectionType() {
 		return false;
 	}
 
 	public final boolean isComponentType() {
 		return false;
 	}
 
 	public final boolean isEntityType() {
 		return false;
 	}
 
 	public final boolean isAnyType() {
 		return false;
 	}
 
 	public final boolean isXMLElement() {
 		return false;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isSame(Object x, Object y) {
 		return isEqual( x, y );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
 		return isEqual( x, y );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final boolean isEqual(Object one, Object another) {
 		return javaTypeDescriptor.areEqual( (T) one, (T) another );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final int getHashCode(Object x) {
 		return javaTypeDescriptor.extractHashCode( (T) x );
 	}
 
 	public final int getHashCode(Object x, SessionFactoryImplementor factory) {
 		return getHashCode( x );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final int compare(Object x, Object y) {
 		return javaTypeDescriptor.getComparator().compare( (T) x, (T) y );
 	}
 
 	public final boolean isDirty(Object old, Object current, SessionImplementor session) {
 		return isDirty( old, current );
 	}
 
 	public final boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) {
 		return checkable[0] && isDirty( old, current );
 	}
 
 	protected final boolean isDirty(Object old, Object current) {
 		return !isSame( old, current );
 	}
 
 	public final boolean isModified(
 			Object oldHydratedState,
 			Object currentState,
 			boolean[] checkable,
 			SessionImplementor session) {
 		return isDirty( oldHydratedState, currentState );
 	}
 
 	public final Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws SQLException {
 		return nullSafeGet( rs, names[0], session );
 	}
 
 	public final Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 			throws SQLException {
 		return nullSafeGet( rs, name, session );
 	}
 
 	public final T nullSafeGet(ResultSet rs, String name, final SessionImplementor session) throws SQLException {
 		final WrapperOptions options = getOptions(session);
 		return nullSafeGet( rs, name, options );
 	}
 
 	protected final T nullSafeGet(ResultSet rs, String name, WrapperOptions options) throws SQLException {
 		return remapSqlTypeDescriptor( options ).getExtractor( javaTypeDescriptor ).extract( rs, name, options );
 	}
 
 	public Object get(ResultSet rs, String name, SessionImplementor session) throws HibernateException, SQLException {
 		return nullSafeGet( rs, name, session );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			final SessionImplementor session) throws SQLException {
 		final WrapperOptions options = getOptions(session);
 		nullSafeSet( st, value, index, options );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	protected final void nullSafeSet(PreparedStatement st, Object value, int index, WrapperOptions options) throws SQLException {
 		remapSqlTypeDescriptor( options ).getBinder( javaTypeDescriptor ).bind( st, ( T ) value, index, options );
 	}
 
 	protected SqlTypeDescriptor remapSqlTypeDescriptor(WrapperOptions options) {
 		return options.remapSqlTypeDescriptor( sqlTypeDescriptor );
 	}
 
 	public void set(PreparedStatement st, T value, int index, SessionImplementor session) throws HibernateException, SQLException {
 		nullSafeSet( st, value, index, session );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final String toLoggableString(Object value, SessionFactoryImplementor factory) {
 		return javaTypeDescriptor.extractLoggableRepresentation( (T) value );
 	}
 
-	@SuppressWarnings({ "unchecked" })
-	public final void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) {
-		node.setText( toString( (T) value ) );
-	}
-
-	public final Object fromXMLNode(Node xml, Mapping factory) {
-		return fromString( xml.getText() );
-	}
-
 	public final boolean isMutable() {
 		return getMutabilityPlan().isMutable();
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return deepCopy( (T) value );
 	}
 
 	protected final T deepCopy(T value) {
 		return getMutabilityPlan().deepCopy( value );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return getMutabilityPlan().disassemble( (T) value );
 	}
 
 	public final Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
 		return getMutabilityPlan().assemble( cached );
 	}
 
 	public final void beforeAssemble(Serializable cached, SessionImplementor session) {
 	}
 
 	public final Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return nullSafeGet(rs, names, session, owner);
 	}
 
 	public final Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return value;
 	}
 
 	public final Object semiResolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return value;
 	}
 
 	public final Type getSemiResolvedType(SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public final Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache) {
 		return getReplacement( (T) original, (T) target, session );
 	}
 
 	@SuppressWarnings({ "unchecked" })
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache,
 			ForeignKeyDirection foreignKeyDirection) {
 		return ForeignKeyDirection.FROM_PARENT == foreignKeyDirection
 				? getReplacement( (T) original, (T) target, session )
 				: target;
 	}
 
 	@Override
 	public boolean canDoExtraction() {
 		return true;
 	}
 
 	@Override
 	public T extract(CallableStatement statement, int startIndex, final SessionImplementor session) throws SQLException {
 		final WrapperOptions options = getOptions(session);
 		return remapSqlTypeDescriptor( options ).getExtractor( javaTypeDescriptor ).extract(
 				statement,
 				startIndex,
 				options
 		);
 	}
 
 	@Override
 	public T extract(CallableStatement statement, String[] paramNames, final SessionImplementor session) throws SQLException {
 		final WrapperOptions options = getOptions(session);
 		return remapSqlTypeDescriptor( options ).getExtractor( javaTypeDescriptor ).extract( statement, paramNames, options );
 	}
 	
 	// TODO : have SessionImplementor extend WrapperOptions
 	private WrapperOptions getOptions(final SessionImplementor session) {
 		return new WrapperOptions() {
 			public boolean useStreamForLobBinding() {
 				return Environment.useStreamsForBinary()
 						|| session.getFactory().getDialect().useInputStreamToInsertBlob();
 			}
 
 			public LobCreator getLobCreator() {
 				return Hibernate.getLobCreator( session );
 			}
 
 			public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
 				final SqlTypeDescriptor remapped = sqlTypeDescriptor.canBeRemapped()
 						? session.getFactory().getDialect().remapSqlTypeDescriptor( sqlTypeDescriptor )
 						: sqlTypeDescriptor;
 				return remapped == null ? sqlTypeDescriptor : remapped;
 			}
 		};
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java b/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
index 493d3de649..86d7632853 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AbstractType.java
@@ -1,172 +1,158 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.compare.EqualsHelper;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 
 /**
  * Abstract superclass of the built in Type hierarchy.
  * 
  * @author Gavin King
  */
 public abstract class AbstractType implements Type {
 	protected static final Size LEGACY_DICTATED_SIZE = new Size();
 	protected static final Size LEGACY_DEFAULT_SIZE = new Size( 19, 2, 255, Size.LobMultiplier.NONE ); // to match legacy behavior
 
 	public boolean isAssociationType() {
 		return false;
 	}
 
 	public boolean isCollectionType() {
 		return false;
 	}
 
 	public boolean isComponentType() {
 		return false;
 	}
 
 	public boolean isEntityType() {
 		return false;
 	}
 	
-	public boolean isXMLElement() {
-		return false;
-	}
-
 	public int compare(Object x, Object y) {
 		return ( (Comparable) x ).compareTo(y);
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 
 		if (value==null) {
 			return null;
 		}
 		else {
 			return (Serializable) deepCopy( value, session.getFactory() );
 		}
 	}
 
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 	throws HibernateException {
 		if ( cached==null ) {
 			return null;
 		}
 		else {
 			return deepCopy( cached, session.getFactory() );
 		}
 	}
 
 	public boolean isDirty(Object old, Object current, SessionImplementor session) throws HibernateException {
 		return !isSame( old, current );
 	}
 
 	public Object hydrate(
 		ResultSet rs,
 		String[] names,
 		SessionImplementor session,
 		Object owner)
 	throws HibernateException, SQLException {
 		// TODO: this is very suboptimal for some subclasses (namely components),
 		// since it does not take advantage of two-phase-load
 		return nullSafeGet(rs, names, session, owner);
 	}
 
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 		return value;
 	}
 
 	public Object semiResolve(Object value, SessionImplementor session, Object owner) 
 	throws HibernateException {
 		return value;
 	}
 	
 	public boolean isAnyType() {
 		return false;
 	}
 
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
 	throws HibernateException {
 		return isDirty(old, current, session);
 	}
 	
 	public boolean isSame(Object x, Object y) throws HibernateException {
 		return isEqual(x, y );
 	}
 
 	public boolean isEqual(Object x, Object y) {
 		return EqualsHelper.equals(x, y);
 	}
 	
 	public int getHashCode(Object x) {
 		return x.hashCode();
 	}
 
 	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
 		return isEqual(x, y );
 	}
 	
 	public int getHashCode(Object x, SessionFactoryImplementor factory) {
 		return getHashCode(x );
 	}
 	
-	protected static void replaceNode(Node container, Element value) {
-		if ( container!=value ) { //not really necessary, I guess...
-			Element parent = container.getParent();
-			container.detach();
-			value.setName( container.getName() );
-			value.detach();
-			parent.add(value);
-		}
-	}
-	
 	public Type getSemiResolvedType(SessionFactoryImplementor factory) {
 		return this;
 	}
 
 	public Object replace(
 			Object original, 
 			Object target, 
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache, 
 			ForeignKeyDirection foreignKeyDirection) 
 	throws HibernateException {
 		boolean include;
 		if ( isAssociationType() ) {
 			AssociationType atype = (AssociationType) this;
 			include = atype.getForeignKeyDirection()==foreignKeyDirection;
 		}
 		else {
 			include = ForeignKeyDirection.FROM_PARENT ==foreignKeyDirection;
 		}
 		return include ? replace(original, target, session, owner, copyCache) : target;
 	}
 
 	public void beforeAssemble(Serializable cached, SessionImplementor session) {}
 
 	/*public Object copy(Object original, Object target, SessionImplementor session, Object owner, Map copyCache)
 	throws HibernateException {
 		if (original==null) return null;
 		return assemble( disassemble(original, session), session, owner );
 	}*/
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
index 5b931cfb0b..67b951c50f 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AnyType.java
@@ -1,519 +1,502 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.TransientObjectException;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.HibernateProxyHelper;
 import org.hibernate.proxy.LazyInitializer;
 
 import org.dom4j.Node;
 
 /**
  * Handles "any" mappings
  * 
  * @author Gavin King
  */
 public class AnyType extends AbstractType implements CompositeType, AssociationType {
 	private final TypeFactory.TypeScope scope;
 	private final Type identifierType;
 	private final Type discriminatorType;
 
 	/**
 	 * Intended for use only from legacy {@link ObjectType} type definition
 	 *
 	 * @param discriminatorType
 	 * @param identifierType
 	 */
 	protected AnyType(Type discriminatorType, Type identifierType) {
 		this( null, discriminatorType, identifierType );
 	}
 
 	public AnyType(TypeFactory.TypeScope scope, Type discriminatorType, Type identifierType) {
 		this.scope = scope;
 		this.discriminatorType = discriminatorType;
 		this.identifierType = identifierType;
 	}
 
 	public Type getIdentifierType() {
 		return identifierType;
 	}
 
 	public Type getDiscriminatorType() {
 		return discriminatorType;
 	}
 
 
 	// general Type metadata ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public String getName() {
 		return "object";
 	}
 
 	@Override
 	public Class getReturnedClass() {
 		return Object.class;
 	}
 
 	@Override
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.sqlTypes( mapping ), identifierType.sqlTypes( mapping ) );
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.dictatedSizes( mapping ), identifierType.dictatedSizes( mapping ) );
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return ArrayHelper.join( discriminatorType.defaultSizes( mapping ), identifierType.defaultSizes( mapping ) );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, EntityMode entityMode) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public boolean isAnyType() {
 		return true;
 	}
 
 	@Override
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	@Override
 	public boolean isComponentType() {
 		return true;
 	}
 
 	@Override
 	public boolean isEmbedded() {
 		return false;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	@Override
 	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return value;
 	}
 
 
 	// general Type functionality ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public int compare(Object x, Object y) {
 		if ( x == null ) {
 			// if y is also null, return that they are the same (no option for "UNKNOWN")
 			// if y is not null, return that y is "greater" (-1 because the result is from the perspective of
 			// 		the first arg: x)
 			return y == null ? 0 : -1;
 		}
 		else if ( y == null ) {
 			// x is not null, but y is.  return that x is "greater"
 			return 1;
 		}
 
 		// At this point we know both are non-null.
 		final Object xId = extractIdentifier( x );
 		final Object yId = extractIdentifier( y );
 
 		return getIdentifierType().compare( xId, yId );
 	}
 
 	private Object extractIdentifier(Object entity) {
 		final EntityPersister concretePersister = guessEntityPersister( entity );
 		return concretePersister == null
 				? null
 				: concretePersister.getEntityTuplizer().getIdentifier( entity, null );
 	}
 
 	private EntityPersister guessEntityPersister(Object object) {
 		if ( scope == null ) {
 			return null;
 		}
 
 		String entityName = null;
 
 		// this code is largely copied from Session's bestGuessEntityName
 		Object entity = object;
 		if ( entity instanceof HibernateProxy ) {
 			final LazyInitializer initializer = ( (HibernateProxy) entity ).getHibernateLazyInitializer();
 			if ( initializer.isUninitialized() ) {
 				entityName = initializer.getEntityName();
 			}
 			entity = initializer.getImplementation();
 		}
 
 		if ( entityName == null ) {
 			for ( EntityNameResolver resolver : scope.resolveFactory().iterateEntityNameResolvers() ) {
 				entityName = resolver.resolveEntityName( entity );
 				if ( entityName != null ) {
 					break;
 				}
 			}
 		}
 
 		if ( entityName == null ) {
 			// the old-time stand-by...
 			entityName = object.getClass().getName();
 		}
 
 		return scope.resolveFactory().getEntityPersister( entityName );
 	}
 
 	@Override
 	public boolean isSame(Object x, Object y) throws HibernateException {
 		return x == y;
 	}
 
 	@Override
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		if ( current == null ) {
 			return old != null;
 		}
 		else if ( old == null ) {
 			return true;
 		}
 
 		final ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) old;
 		final boolean[] idCheckable = new boolean[checkable.length-1];
 		System.arraycopy( checkable, 1, idCheckable, 0, idCheckable.length );
 		return ( checkable[0] && !holder.entityName.equals( session.bestGuessEntityName( current ) ) )
 				|| identifierType.isModified( holder.id, getIdentifier( current, session ), idCheckable, session );
 	}
 
 	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		final boolean[] result = new boolean[ getColumnSpan( mapping ) ];
 		if ( value != null ) {
 			Arrays.fill( result, true );
 		}
 		return result;
 	}
 
 	@Override
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return isDirty( old, current, session );
 	}
 
 	@Override
 	public int getColumnSpan(Mapping session) {
 		return 2;
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
 			throws HibernateException, SQLException {
 		return resolveAny(
 				(String) discriminatorType.nullSafeGet( rs, names[0], session, owner ),
 				(Serializable) identifierType.nullSafeGet( rs, names[1], session, owner ),
 				session
 		);
 	}
 
 	@Override
 	public Object hydrate(ResultSet rs,	String[] names,	SessionImplementor session,	Object owner)
 			throws HibernateException, SQLException {
 		final String entityName = (String) discriminatorType.nullSafeGet( rs, names[0], session, owner );
 		final Serializable id = (Serializable) identifierType.nullSafeGet( rs, names[1], session, owner );
 		return new ObjectTypeCacheEntry( entityName, id );
 	}
 
 	@Override
 	public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		final ObjectTypeCacheEntry holder = (ObjectTypeCacheEntry) value;
 		return resolveAny( holder.entityName, holder.id, session );
 	}
 
 	private Object resolveAny(String entityName, Serializable id, SessionImplementor session)
 			throws HibernateException {
 		return entityName==null || id==null
 				? null
 				: session.internalLoad( entityName, id, false, false );
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, SessionImplementor session)
 			throws HibernateException, SQLException {
 		nullSafeSet( st, value, index, null, session );
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value,	int index, boolean[] settable, SessionImplementor session)
 			throws HibernateException, SQLException {
 		Serializable id;
 		String entityName;
 		if ( value == null ) {
 			id = null;
 			entityName = null;
 		}
 		else {
 			entityName = session.bestGuessEntityName( value );
 			id = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, value, session );
 		}
 
 		// discriminatorType is assumed to be single-column type
 		if ( settable == null || settable[0] ) {
 			discriminatorType.nullSafeSet( st, entityName, index, session );
 		}
 		if ( settable == null ) {
 			identifierType.nullSafeSet( st, id, index+1, session );
 		}
 		else {
 			final boolean[] idSettable = new boolean[ settable.length-1 ];
 			System.arraycopy( settable, 1, idSettable, 0, idSettable.length );
 			identifierType.nullSafeSet( st, id, index+1, idSettable, session );
 		}
 	}
 
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		//TODO: terrible implementation!
 		return value == null
 				? "null"
 				: factory.getTypeHelper()
 				.entity( HibernateProxyHelper.getClassWithoutInitializingProxy( value ) )
 				.toLoggableString( value, factory );
 	}
 
 	@Override
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
 		final ObjectTypeCacheEntry e = (ObjectTypeCacheEntry) cached;
 		return e == null ? null : session.internalLoad( e.entityName, e.id, false, false );
 	}
 
 	@Override
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			return new ObjectTypeCacheEntry(
 					session.bestGuessEntityName( value ),
 					ForeignKeys.getEntityIdentifierIfNotUnsaved(
 							session.bestGuessEntityName( value ),
 							value,
 							session
 					)
 			);
 		}
 	}
 
 	@Override
 	public Object replace(Object original, Object target, SessionImplementor session, Object owner, Map copyCache)
 			throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		else {
 			final String entityName = session.bestGuessEntityName( original );
 			final Serializable id = ForeignKeys.getEntityIdentifierIfNotUnsaved( entityName, original, session );
 			return session.internalLoad( entityName, id, false, false );
 		}
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs,	String name, SessionImplementor session, Object owner) {
 		throw new UnsupportedOperationException( "object is a multicolumn type" );
 	}
 
 	@Override
 	public Object semiResolve(Object value, SessionImplementor session, Object owner) {
 		throw new UnsupportedOperationException( "any mappings may not form part of a property-ref" );
 	}
 
-	@Override
-	public void setToXMLNode(Node xml, Object value, SessionFactoryImplementor factory) {
-		throw new UnsupportedOperationException("any types cannot be stringified");
-	}
-
-	@Override
-	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
-		throw new UnsupportedOperationException();
-	}
-
-
-
 	// CompositeType implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	private static final String[] PROPERTY_NAMES = new String[] { "class", "id" };
 
 	@Override
 	public String[] getPropertyNames() {
 		return PROPERTY_NAMES;
 	}
 
 	@Override
 	public Object getPropertyValue(Object component, int i, SessionImplementor session) throws HibernateException {
 		return i==0
 				? session.bestGuessEntityName( component )
 				: getIdentifier( component, session );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, SessionImplementor session) throws HibernateException {
 		return new Object[] {
 				session.bestGuessEntityName( component ),
 				getIdentifier( component, session )
 		};
 	}
 
 	private Serializable getIdentifier(Object value, SessionImplementor session) throws HibernateException {
 		try {
 			return ForeignKeys.getEntityIdentifierIfNotUnsaved(
 					session.bestGuessEntityName( value ),
 					value,
 					session
 			);
 		}
 		catch (TransientObjectException toe) {
 			return null;
 		}
 	}
 
 	@Override
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) {
 		throw new UnsupportedOperationException();
 	}
 
 	private static final boolean[] NULLABILITY = new boolean[] { false, false };
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return NULLABILITY;
 	}
 
 	@Override
 	public Type[] getSubtypes() {
 		return new Type[] {discriminatorType, identifierType };
 	}
 
 	@Override
 	public CascadeStyle getCascadeStyle(int i) {
 		return CascadeStyles.NONE;
 	}
 
 	@Override
 	public FetchMode getFetchMode(int i) {
 		return FetchMode.SELECT;
 	}
 
 
 	// AssociationType implementation ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	@Override
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FROM_PARENT;
 	}
 
 	@Override
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 
 	@Override
 	public String getLHSPropertyName() {
 		return null;
 	}
 
 	public boolean isReferenceToPrimaryKey() {
 		return true;
 	}
 
 	@Override
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	@Override
 	public boolean isAlwaysDirtyChecked() {
 		return false;
 	}
 
 	@Override
-	public boolean isEmbeddedInXML() {
-		return false;
-	}
-
-	@Override
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
 	@Override
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
 		throw new UnsupportedOperationException("any types do not have a unique referenced persister");
 	}
 
 	@Override
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
 		throw new UnsupportedOperationException();
 	}
 
 	@Override
 	public String getOnCondition(
 			String alias,
 			SessionFactoryImplementor factory,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * Used to externalize discrimination per a given identifier.  For example, when writing to
 	 * second level cache we write the discrimination resolved concrete type for each entity written.
 	 */
 	public static final class ObjectTypeCacheEntry implements Serializable {
 		final String entityName;
 		final Serializable id;
 
 		ObjectTypeCacheEntry(String entityName, Serializable id) {
 			this.entityName = entityName;
 			this.id = id;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java b/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
index 675c3295d5..f6df74c276 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ArrayType.java
@@ -1,139 +1,128 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Array;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.collection.internal.PersistentArrayHolder;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 /**
  * A type for persistent arrays.
  * @author Gavin King
  */
 public class ArrayType extends CollectionType {
 
 	private final Class elementClass;
 	private final Class arrayClass;
 
-	/**
-	 * @deprecated Use {@link #ArrayType(TypeFactory.TypeScope, String, String, Class )} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public ArrayType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Class elementClass, boolean isEmbeddedInXML) {
-		super( typeScope, role, propertyRef, isEmbeddedInXML );
-		this.elementClass = elementClass;
-		arrayClass = Array.newInstance(elementClass, 0).getClass();
-	}
-
 	public ArrayType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Class elementClass) {
 		super( typeScope, role, propertyRef );
 		this.elementClass = elementClass;
 		arrayClass = Array.newInstance(elementClass, 0).getClass();
 	}
 
 	public Class getReturnedClass() {
 		return arrayClass;
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key)
 	throws HibernateException {
 		return new PersistentArrayHolder(session, persister);
 	}
 
 	/**
 	 * Not defined for collections of primitive type
 	 */
 	public Iterator getElementsIterator(Object collection) {
 		return Arrays.asList( (Object[]) collection ).iterator();
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object array) {
 		return new PersistentArrayHolder(session, array);
 	}
 
 	public boolean isArrayType() {
 		return true;
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		int length = Array.getLength(value);
 		List list = new ArrayList(length);
 		Type elemType = getElementType(factory);
 		for ( int i=0; i<length; i++ ) {
 			list.add( elemType.toLoggableString( Array.get(value, i), factory ) );
 		}
 		return list.toString();
 	}
 	
 	public Object instantiateResult(Object original) {
 		return Array.newInstance( elementClass, Array.getLength(original) );
 	}
 
 	public Object replaceElements(
 		Object original,
 		Object target,
 		Object owner, 
 		Map copyCache, 
 		SessionImplementor session)
 	throws HibernateException {
 		
 		int length = Array.getLength(original);
 		if ( length!=Array.getLength(target) ) {
 			//note: this affects the return value!
 			target=instantiateResult(original);
 		}
 		
 		Type elemType = getElementType( session.getFactory() );
 		for ( int i=0; i<length; i++ ) {
 			Array.set( target, i, elemType.replace( Array.get(original, i), null, session, owner, copyCache ) );
 		}
 		
 		return target;
 	
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		throw new UnsupportedOperationException();
 	}
 
 	public Object indexOf(Object array, Object element) {
 		int length = Array.getLength(array);
 		for ( int i=0; i<length; i++ ) {
 			//TODO: proxies!
 			if ( Array.get(array, i)==element ) {
 				return i;
 			}
 		}
 		return null;
 	}
 
 	@Override
 	protected boolean initializeImmediately() {
 		return true;
 	}
 
 	@Override
 	public boolean hasHolder() {
 		return true;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java b/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
index 983176037e..9ff06e14c7 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/AssociationType.java
@@ -1,91 +1,78 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.persister.entity.Joinable;
 
 /**
  * A type that represents some kind of association between entities.
  * @see org.hibernate.engine.internal.Cascade
  * @author Gavin King
  */
 public interface AssociationType extends Type {
 
 	/**
 	 * Get the foreign key directionality of this association
 	 */
 	public ForeignKeyDirection getForeignKeyDirection();
 
 	//TODO: move these to a new JoinableType abstract class,
 	//extended by EntityType and PersistentCollectionType:
 
 	/**
 	 * Is the primary key of the owning entity table
 	 * to be used in the join?
 	 */
 	public boolean useLHSPrimaryKey();
 	/**
 	 * Get the name of a property in the owning entity 
 	 * that provides the join key (null if the identifier)
 	 */
 	public String getLHSPropertyName();
 	
 	/**
 	 * The name of a unique property of the associated entity 
 	 * that provides the join key (null if the identifier of
 	 * an entity, or key of a collection)
 	 */
 	public String getRHSUniqueKeyPropertyName();
 
 	/**
 	 * Get the "persister" for this association - a class or
 	 * collection persister
 	 */
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) throws MappingException;
 	
 	/**
 	 * Get the entity name of the associated entity
 	 */
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) throws MappingException;
 	
 	/**
 	 * Get the "filtering" SQL fragment that is applied in the 
 	 * SQL on clause, in addition to the usual join condition
 	 */	
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) 
 	throws MappingException;
 
 	/**
 	 * Get the "filtering" SQL fragment that is applied in the
 	 * SQL on clause, in addition to the usual join condition
 	 */
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters, Set<String> treatAsDeclarations);
 	
 	/**
 	 * Do we dirty check this association, even when there are
 	 * no columns to be updated?
 	 */
 	public abstract boolean isAlwaysDirtyChecked();
-
-	/**
-	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public boolean isEmbeddedInXML();
 }
-
-
-
-
-
-
diff --git a/hibernate-core/src/main/java/org/hibernate/type/BagType.java b/hibernate-core/src/main/java/org/hibernate/type/BagType.java
index 40874c4345..5e1d13d7b8 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/BagType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/BagType.java
@@ -1,51 +1,42 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collection;
 
 import org.hibernate.HibernateException;
 import org.hibernate.collection.internal.PersistentBag;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class BagType extends CollectionType {
 
-	/**
-	 * @deprecated Use {@link #BagType(TypeFactory.TypeScope, String, String )}
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public BagType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
-		super( typeScope, role, propertyRef, isEmbeddedInXML );
-	}
-
 	public BagType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
 		super( typeScope, role, propertyRef );
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key)
 	throws HibernateException {
 		return new PersistentBag(session);
 	}
 
 	public Class getReturnedClass() {
 		return java.util.Collection.class;
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentBag( session, (Collection) collection );
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0 ? new ArrayList() : new ArrayList( anticipatedSize + 1 );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
index a9ad4a1b7f..818e321ca5 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CollectionType.java
@@ -1,833 +1,793 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.SortedMap;
 import java.util.TreeMap;
 
 import org.hibernate.EntityMode;
 import org.hibernate.Hibernate;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.CollectionEntry;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.MarkerObject;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.collection.QueryableCollection;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.proxy.LazyInitializer;
 
 import org.jboss.logging.Logger;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 
 /**
  * A type that handles Hibernate <tt>PersistentCollection</tt>s (including arrays).
  * 
  * @author Gavin King
  */
 public abstract class CollectionType extends AbstractType implements AssociationType {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionType.class.getName());
 
 	private static final Object NOT_NULL_COLLECTION = new MarkerObject( "NOT NULL COLLECTION" );
 	public static final Object UNFETCHED_COLLECTION = new MarkerObject( "UNFETCHED COLLECTION" );
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String role;
 	private final String foreignKeyPropertyName;
-	private final boolean isEmbeddedInXML;
-
-	/**
-	 * @deprecated Use {@link #CollectionType(TypeFactory.TypeScope, String, String)} instead
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public CollectionType(TypeFactory.TypeScope typeScope, String role, String foreignKeyPropertyName, boolean isEmbeddedInXML) {
-		this.typeScope = typeScope;
-		this.role = role;
-		this.foreignKeyPropertyName = foreignKeyPropertyName;
-		this.isEmbeddedInXML = isEmbeddedInXML;
-	}
 
 	public CollectionType(TypeFactory.TypeScope typeScope, String role, String foreignKeyPropertyName) {
 		this.typeScope = typeScope;
 		this.role = role;
 		this.foreignKeyPropertyName = foreignKeyPropertyName;
-		this.isEmbeddedInXML = true;
-	}
-
-	@Override
-	public boolean isEmbeddedInXML() {
-		return isEmbeddedInXML;
 	}
 
 	public String getRole() {
 		return role;
 	}
 
 	public Object indexOf(Object collection, Object element) {
 		throw new UnsupportedOperationException( "generic collections don't have indexes" );
 	}
 
 	public boolean contains(Object collection, Object childObject, SessionImplementor session) {
 		// we do not have to worry about queued additions to uninitialized
 		// collections, since they can only occur for inverse collections!
 		Iterator elems = getElementsIterator( collection, session );
 		while ( elems.hasNext() ) {
 			Object element = elems.next();
 			// worrying about proxies is perhaps a little bit of overkill here...
 			if ( element instanceof HibernateProxy ) {
 				LazyInitializer li = ( (HibernateProxy) element ).getHibernateLazyInitializer();
 				if ( !li.isUninitialized() ) {
 					element = li.getImplementation();
 				}
 			}
 			if ( element == childObject ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	@Override
 	public boolean isCollectionType() {
 		return true;
 	}
 
 	@Override
 	public final boolean isEqual(Object x, Object y) {
 		return x == y
 			|| ( x instanceof PersistentCollection && ( (PersistentCollection) x ).wasInitialized() && ( (PersistentCollection) x ).isWrapper( y ) )
 			|| ( y instanceof PersistentCollection && ( (PersistentCollection) y ).wasInitialized() && ( (PersistentCollection) y ).isWrapper( x ) );
 	}
 
 	@Override
 	public int compare(Object x, Object y) {
 		return 0; // collections cannot be compared
 	}
 
 	@Override
 	public int getHashCode(Object x) {
 		throw new UnsupportedOperationException( "cannot doAfterTransactionCompletion lookups on collections" );
 	}
 
 	/**
 	 * Instantiate an uninitialized collection wrapper or holder. Callers MUST add the holder to the
 	 * persistence context!
 	 *
 	 * @param session The session from which the request is originating.
 	 * @param persister The underlying collection persister (metadata)
 	 * @param key The owner key.
 	 * @return The instantiated collection.
 	 */
 	public abstract PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key);
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner) throws SQLException {
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String[] name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( null, session, owner );
 	}
 
 	@Override
 	public final void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		//NOOP
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value, int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 	}
 
 	@Override
 	public int[] sqlTypes(Mapping session) throws MappingException {
 		return ArrayHelper.EMPTY_INT_ARRAY;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return new Size[] { LEGACY_DICTATED_SIZE };
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return new Size[] { LEGACY_DEFAULT_SIZE };
 	}
 
 	@Override
 	public int getColumnSpan(Mapping session) throws MappingException {
 		return 0;
 	}
 
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		else if ( !Hibernate.isInitialized( value ) ) {
 			return "<uninitialized>";
 		}
 		else {
 			return renderLoggableString( value, factory );
 		}
 	}
 
 	protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		final List<String> list = new ArrayList<String>();
 		Type elemType = getElementType( factory );
 		Iterator itr = getElementsIterator( value );
 		while ( itr.hasNext() ) {
 			list.add( elemType.toLoggableString( itr.next(), factory ) );
 		}
 		return list.toString();
 	}
 
 	@Override
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return value;
 	}
 
 	@Override
 	public String getName() {
 		return getReturnedClass().getName() + '(' + getRole() + ')';
 	}
 
 	/**
 	 * Get an iterator over the element set of the collection, which may not yet be wrapped
 	 *
 	 * @param collection The collection to be iterated
 	 * @param session The session from which the request is originating.
 	 * @return The iterator.
 	 */
 	public Iterator getElementsIterator(Object collection, SessionImplementor session) {
 		return getElementsIterator( collection );
 	}
 
 	/**
 	 * Get an iterator over the element set of the collection in POJO mode
 	 *
 	 * @param collection The collection to be iterated
 	 * @return The iterator.
 	 */
 	protected Iterator getElementsIterator(Object collection) {
 		return ( (Collection) collection ).iterator();
 	}
 
 	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	@Override
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//remember the uk value
 		
 		//This solution would allow us to eliminate the owner arg to disassemble(), but
 		//what if the collection was null, and then later had elements added? seems unsafe
 		//session.getPersistenceContext().getCollectionEntry( (PersistentCollection) value ).getKey();
 		
 		final Serializable key = getKeyOfOwner(owner, session);
 		if (key==null) {
 			return null;
 		}
 		else {
 			return getPersister(session)
 					.getKeyType()
 					.disassemble( key, session, owner );
 		}
 	}
 
 	@Override
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//we must use the "remembered" uk value, since it is 
 		//not available from the EntityEntry during assembly
 		if (cached==null) {
 			return null;
 		}
 		else {
 			final Serializable key = (Serializable) getPersister(session)
 					.getKeyType()
 					.assemble( cached, session, owner);
 			return resolveKey( key, session, owner );
 		}
 	}
 
 	/**
 	 * Is the owning entity versioned?
 	 *
 	 * @param session The session from which the request is originating.
 	 * @return True if the collection owner is versioned; false otherwise.
 	 * @throws org.hibernate.MappingException Indicates our persister could not be located.
 	 */
 	private boolean isOwnerVersioned(SessionImplementor session) throws MappingException {
 		return getPersister( session ).getOwnerEntityPersister().isVersioned();
 	}
 
 	/**
 	 * Get our underlying collection persister (using the session to access the
 	 * factory).
 	 *
 	 * @param session The session from which the request is originating.
 	 * @return The underlying collection persister
 	 */
 	private CollectionPersister getPersister(SessionImplementor session) {
 		return session.getFactory().getCollectionPersister( role );
 	}
 
 	@Override
 	public boolean isDirty(Object old, Object current, SessionImplementor session)
 			throws HibernateException {
 
 		// collections don't dirty an unversioned parent entity
 
 		// TODO: I don't really like this implementation; it would be better if
 		// this was handled by searchForDirtyCollections()
 		return super.isDirty( old, current, session );
 		// return false;
 
 	}
 
 	@Override
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return isDirty(old, current, session);
 	}
 
 	/**
 	 * Wrap the naked collection instance in a wrapper, or instantiate a
 	 * holder. Callers <b>MUST</b> add the holder to the persistence context!
 	 *
 	 * @param session The session from which the request is originating.
 	 * @param collection The bare collection to be wrapped.
 	 * @return The wrapped collection.
 	 */
 	public abstract PersistentCollection wrap(SessionImplementor session, Object collection);
 
 	/**
 	 * Note: return true because this type is castable to <tt>AssociationType</tt>. Not because
 	 * all collections are associations.
 	 */
 	@Override
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	@Override
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.TO_PARENT;
 	}
 
 	/**
 	 * Get the key value from the owning entity instance, usually the identifier, but might be some
 	 * other unique key, in the case of property-ref
 	 *
 	 * @param owner The collection owner
 	 * @param session The session from which the request is originating.
 	 * @return The collection owner's key
 	 */
 	public Serializable getKeyOfOwner(Object owner, SessionImplementor session) {
 		
 		EntityEntry entityEntry = session.getPersistenceContext().getEntry( owner );
 		if ( entityEntry == null ) {
 			// This just handles a particular case of component
 			// projection, perhaps get rid of it and throw an exception
 			return null;
 		}
 		
 		if ( foreignKeyPropertyName == null ) {
 			return entityEntry.getId();
 		}
 		else {
 			// TODO: at the point where we are resolving collection references, we don't
 			// know if the uk value has been resolved (depends if it was earlier or
 			// later in the mapping document) - now, we could try and use e.getStatus()
 			// to decide to semiResolve(), trouble is that initializeEntity() reuses
 			// the same array for resolved and hydrated values
 			Object id;
 			if ( entityEntry.getLoadedState() != null ) {
 				id = entityEntry.getLoadedValue( foreignKeyPropertyName );
 			}
 			else {
 				id = entityEntry.getPersister().getPropertyValue( owner, foreignKeyPropertyName );
 			}
 
 			// NOTE VERY HACKISH WORKAROUND!!
 			// TODO: Fix this so it will work for non-POJO entity mode
 			Type keyType = getPersister( session ).getKeyType();
 			if ( !keyType.getReturnedClass().isInstance( id ) ) {
 				id = keyType.semiResolve(
 						entityEntry.getLoadedValue( foreignKeyPropertyName ),
 						session,
 						owner 
 				);
 			}
 
 			return (Serializable) id;
 		}
 	}
 
 	/**
 	 * Get the id value from the owning entity key, usually the same as the key, but might be some
 	 * other property, in the case of property-ref
 	 *
 	 * @param key The collection owner key
 	 * @param session The session from which the request is originating.
 	 * @return The collection owner's id, if it can be obtained from the key;
 	 * otherwise, null is returned
 	 */
 	public Serializable getIdOfOwnerOrNull(Serializable key, SessionImplementor session) {
 		Serializable ownerId = null;
 		if ( foreignKeyPropertyName == null ) {
 			ownerId = key;
 		}
 		else {
 			Type keyType = getPersister( session ).getKeyType();
 			EntityPersister ownerPersister = getPersister( session ).getOwnerEntityPersister();
 			// TODO: Fix this so it will work for non-POJO entity mode
 			Class ownerMappedClass = ownerPersister.getMappedClass();
 			if ( ownerMappedClass.isAssignableFrom( keyType.getReturnedClass() ) &&
 					keyType.getReturnedClass().isInstance( key ) ) {
 				// the key is the owning entity itself, so get the ID from the key
 				ownerId = ownerPersister.getIdentifier( key, session );
 			}
 			else {
 				// TODO: check if key contains the owner ID
 			}
 		}
 		return ownerId;
 	}
 
 	@Override
 	public Object hydrate(ResultSet rs, String[] name, SessionImplementor session, Object owner) {
 		// can't just return null here, since that would
 		// cause an owning component to become null
 		return NOT_NULL_COLLECTION;
 	}
 
 	@Override
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		
 		return resolveKey( getKeyOfOwner( owner, session ), session, owner );
 	}
 	
 	private Object resolveKey(Serializable key, SessionImplementor session, Object owner) {
 		// if (key==null) throw new AssertionFailure("owner identifier unknown when re-assembling
 		// collection reference");
 		return key == null ? null : // TODO: can this case really occur??
 			getCollection( key, session, owner );
 	}
 
 	@Override
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		throw new UnsupportedOperationException(
 			"collection mappings may not form part of a property-ref" );
 	}
 
 	public boolean isArrayType() {
 		return false;
 	}
 
 	@Override
 	public boolean useLHSPrimaryKey() {
 		return foreignKeyPropertyName == null;
 	}
 
 	@Override
 	public String getRHSUniqueKeyPropertyName() {
 		return null;
 	}
 
 	@Override
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory)
 			throws MappingException {
 		return (Joinable) factory.getCollectionPersister( role );
 	}
 
 	@Override
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
 		return false;
 	}
 
 	@Override
 	public String getAssociatedEntityName(SessionFactoryImplementor factory)
 			throws MappingException {
 		try {
 			
 			QueryableCollection collectionPersister = (QueryableCollection) factory
 					.getCollectionPersister( role );
 			
 			if ( !collectionPersister.getElementType().isEntityType() ) {
 				throw new MappingException( 
 						"collection was not an association: " + 
 						collectionPersister.getRole() 
 				);
 			}
 			
 			return collectionPersister.getElementPersister().getEntityName();
 			
 		}
 		catch (ClassCastException cce) {
 			throw new MappingException( "collection role is not queryable " + role );
 		}
 	}
 
 	/**
 	 * Replace the elements of a collection with the elements of another collection.
 	 *
 	 * @param original The 'source' of the replacement elements (where we copy from)
 	 * @param target The target of the replacement elements (where we copy to)
 	 * @param owner The owner of the collection being merged
 	 * @param copyCache The map of elements already replaced.
 	 * @param session The session from which the merge event originated.
 	 * @return The merged collection.
 	 */
 	public Object replaceElements(
 			Object original,
 			Object target,
 			Object owner,
 			Map copyCache,
 			SessionImplementor session) {
 		// TODO: does not work for EntityMode.DOM4J yet!
 		java.util.Collection result = ( java.util.Collection ) target;
 		result.clear();
 
 		// copy elements into newly empty target collection
 		Type elemType = getElementType( session.getFactory() );
 		Iterator iter = ( (java.util.Collection) original ).iterator();
 		while ( iter.hasNext() ) {
 			result.add( elemType.replace( iter.next(), null, session, owner, copyCache ) );
 		}
 
 		// if the original is a PersistentCollection, and that original
 		// was not flagged as dirty, then reset the target's dirty flag
 		// here after the copy operation.
 		// </p>
 		// One thing to be careful of here is a "bare" original collection
 		// in which case we should never ever ever reset the dirty flag
 		// on the target because we simply do not know...
 		if ( original instanceof PersistentCollection ) {
 			if ( result instanceof PersistentCollection ) {
 				final PersistentCollection originalPersistentCollection = (PersistentCollection) original;
 				final PersistentCollection resultPersistentCollection = (PersistentCollection) result;
 
 				preserveSnapshot( originalPersistentCollection, resultPersistentCollection, elemType, owner, copyCache, session );
 
 				if ( ! originalPersistentCollection.isDirty() ) {
 					resultPersistentCollection.clearDirty();
 				}
 			}
 		}
 
 		return result;
 	}
 
 	private void preserveSnapshot(
 			PersistentCollection original,
 			PersistentCollection result,
 			Type elemType,
 			Object owner,
 			Map copyCache,
 			SessionImplementor session) {
 		Serializable originalSnapshot = original.getStoredSnapshot();
 		Serializable resultSnapshot = result.getStoredSnapshot();
 		Serializable targetSnapshot;
 
 		if ( originalSnapshot instanceof List ) {
 			targetSnapshot = new ArrayList(
 					( (List) originalSnapshot ).size() );
 			for ( Object obj : (List) originalSnapshot ) {
 				( (List) targetSnapshot ).add( elemType.replace( obj, null, session, owner, copyCache ) );
 			}
 
 		}
 		else if ( originalSnapshot instanceof Map ) {
 			if ( originalSnapshot instanceof SortedMap ) {
 				targetSnapshot = new TreeMap( ( (SortedMap) originalSnapshot ).comparator() );
 			}
 			else {
 				targetSnapshot = new HashMap(
 						CollectionHelper.determineProperSizing( ( (Map) originalSnapshot ).size() ),
 						CollectionHelper.LOAD_FACTOR
 				);
 			}
 
 			for ( Map.Entry<Object, Object> entry : ( (Map<Object, Object>) originalSnapshot ).entrySet() ) {
 				Object key = entry.getKey();
 				Object value = entry.getValue();
 				Object resultSnapshotValue = ( resultSnapshot == null )
 						? null
 						: ( (Map<Object, Object>) resultSnapshot ).get( key );
 
 				Object newValue = elemType.replace( value, resultSnapshotValue, session, owner, copyCache );
 
 				if ( key == value ) {
 					( (Map) targetSnapshot ).put( newValue, newValue );
 
 				}
 				else {
 					( (Map) targetSnapshot ).put( key, newValue );
 				}
 
 			}
 
 		}
 		else if ( originalSnapshot instanceof Object[] ) {
 			Object[] arr = (Object[]) originalSnapshot;
 			for ( int i = 0; i < arr.length; i++ ) {
 				arr[i] = elemType.replace( arr[i], null, session, owner, copyCache );
 			}
 			targetSnapshot = originalSnapshot;
 
 		}
 		else {
 			// retain the same snapshot
 			targetSnapshot = resultSnapshot;
 
 		}
 
 		CollectionEntry ce = session.getPersistenceContext().getCollectionEntry( result );
 		if ( ce != null ) {
 			ce.resetStoredSnapshot( result, targetSnapshot );
 		}
 
 	}
 
 	/**
 	 * Instantiate a new "underlying" collection exhibiting the same capacity
 	 * charactersitcs and the passed "original".
 	 *
 	 * @param original The original collection.
 	 * @return The newly instantiated collection.
 	 */
 	protected Object instantiateResult(Object original) {
 		// by default just use an unanticipated capacity since we don't
 		// know how to extract the capacity to use from original here...
 		return instantiate( -1 );
 	}
 
 	/**
 	 * Instantiate an empty instance of the "underlying" collection (not a wrapper),
 	 * but with the given anticipated size (i.e. accounting for initial capacity
 	 * and perhaps load factor).
 	 *
 	 * @param anticipatedSize The anticipated size of the instaniated collection
 	 * after we are done populating it.
 	 * @return A newly instantiated collection to be wrapped.
 	 */
 	public abstract Object instantiate(int anticipatedSize);
 
 	@Override
 	public Object replace(
 			final Object original,
 			final Object target,
 			final SessionImplementor session,
 			final Object owner,
 			final Map copyCache) throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		if ( !Hibernate.isInitialized( original ) ) {
 			return target;
 		}
 
 		// for a null target, or a target which is the same as the original, we
 		// need to put the merged elements in a new collection
 		Object result = target == null || target == original ? instantiateResult( original ) : target;
 		
 		//for arrays, replaceElements() may return a different reference, since
 		//the array length might not match
 		result = replaceElements( original, result, owner, copyCache, session );
 
 		if ( original == target ) {
 			// get the elements back into the target making sure to handle dirty flag
 			boolean wasClean = PersistentCollection.class.isInstance( target ) && !( ( PersistentCollection ) target ).isDirty();
 			//TODO: this is a little inefficient, don't need to do a whole
 			//      deep replaceElements() call
 			replaceElements( result, target, owner, copyCache, session );
 			if ( wasClean ) {
 				( ( PersistentCollection ) target ).clearDirty();
 			}
 			result = target;
 		}
 
 		return result;
 	}
 
 	/**
 	 * Get the Hibernate type of the collection elements
 	 *
 	 * @param factory The session factory.
 	 * @return The type of the collection elements
 	 * @throws MappingException Indicates the underlying persister could not be located.
 	 */
 	public final Type getElementType(SessionFactoryImplementor factory) throws MappingException {
 		return factory.getCollectionPersister( getRole() ).getElementType();
 	}
 
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
 	@Override
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
 			throws MappingException {
 		return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
 	}
 
 	@Override
 	public String getOnCondition(
 			String alias,
 			SessionFactoryImplementor factory,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters, treatAsDeclarations );
 	}
 
 	/**
 	 * instantiate a collection wrapper (called when loading an object)
 	 *
 	 * @param key The collection owner key
 	 * @param session The session from which the request is originating.
 	 * @param owner The collection owner
 	 * @return The collection
 	 */
 	public Object getCollection(Serializable key, SessionImplementor session, Object owner) {
 
 		CollectionPersister persister = getPersister( session );
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		final EntityMode entityMode = persister.getOwnerEntityPersister().getEntityMode();
 
 		// check if collection is currently being loaded
 		PersistentCollection collection = persistenceContext.getLoadContexts().locateLoadingCollection( persister, key );
 		
 		if ( collection == null ) {
 			
 			// check if it is already completely loaded, but unowned
 			collection = persistenceContext.useUnownedCollection( new CollectionKey(persister, key, entityMode) );
 			
 			if ( collection == null ) {
 				// create a new collection wrapper, to be initialized later
 				collection = instantiate( session, persister, key );
 				
 				collection.setOwner(owner);
 	
 				persistenceContext.addUninitializedCollection( persister, collection, key );
 	
 				// some collections are not lazy:
 				if ( initializeImmediately() ) {
 					session.initializeCollection( collection, false );
 				}
 				else if ( !persister.isLazy() ) {
 					persistenceContext.addNonLazyCollection( collection );
 				}
 	
 				if ( hasHolder() ) {
 					session.getPersistenceContext().addCollectionHolder( collection );
 				}
 				
 			}
 			
 			if ( LOG.isTraceEnabled() ) {
 				LOG.tracef( "Created collection wrapper: %s",
 						MessageHelper.collectionInfoString( persister, collection,
 								key, session ) );
 			}
 			
 		}
 		
 		collection.setOwner(owner);
 
 		return collection.getValue();
 	}
 
 	public boolean hasHolder() {
 		return false;
 	}
 
 	protected boolean initializeImmediately() {
 		return false;
 	}
 
 	@Override
 	public String getLHSPropertyName() {
 		return foreignKeyPropertyName;
 	}
 
-	@Override
-	public boolean isXMLElement() {
-		return true;
-	}
-
-	@Override
-	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
-		return xml;
-	}
-
-	@Override
-	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory)
-			throws HibernateException {
-		if ( !isEmbeddedInXML ) {
-			node.detach();
-		}
-		else {
-			replaceNode( node, (Element) value );
-		}
-	}
-	
 	/**
 	 * We always need to dirty check the collection because we sometimes 
 	 * need to incremement version number of owner and also because of 
 	 * how assemble/disassemble is implemented for uks
 	 */
 	@Override
 	public boolean isAlwaysDirtyChecked() {
 		return true; 
 	}
 
 	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
index b53ac07a3f..7e05804258 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ComponentType.java
@@ -1,838 +1,823 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.CallableStatement;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.PropertyNotFoundException;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.tuple.component.ComponentTuplizer;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 
 /**
  * Handles "component" mappings
  *
  * @author Gavin King
  */
 public class ComponentType extends AbstractType implements CompositeType, ProcedureParameterExtractionAware {
 
 	private final TypeFactory.TypeScope typeScope;
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyNullability;
 	protected final int propertySpan;
 	private final CascadeStyle[] cascade;
 	private final FetchMode[] joinedFetch;
 	private final boolean isKey;
 	private boolean hasNotNullProperty;
 
 	protected final EntityMode entityMode;
 	protected final ComponentTuplizer componentTuplizer;
 
 	public ComponentType(TypeFactory.TypeScope typeScope, ComponentMetamodel metamodel) {
 		this.typeScope = typeScope;
 		// for now, just "re-flatten" the metamodel since this is temporary stuff anyway (HHH-1907)
 		this.isKey = metamodel.isKey();
 		this.propertySpan = metamodel.getPropertySpan();
 		this.propertyNames = new String[propertySpan];
 		this.propertyTypes = new Type[propertySpan];
 		this.propertyNullability = new boolean[propertySpan];
 		this.cascade = new CascadeStyle[propertySpan];
 		this.joinedFetch = new FetchMode[propertySpan];
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			StandardProperty prop = metamodel.getProperty( i );
 			this.propertyNames[i] = prop.getName();
 			this.propertyTypes[i] = prop.getType();
 			this.propertyNullability[i] = prop.isNullable();
 			this.cascade[i] = prop.getCascadeStyle();
 			this.joinedFetch[i] = prop.getFetchMode();
 			if ( !prop.isNullable() ) {
 				hasNotNullProperty = true;
 			}
 		}
 
 		this.entityMode = metamodel.getEntityMode();
 		this.componentTuplizer = metamodel.getComponentTuplizer();
 	}
 
 	public boolean isKey() {
 		return isKey;
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	public ComponentTuplizer getComponentTuplizer() {
 		return componentTuplizer;
 	}
 
 	@Override
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		int span = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			span += propertyTypes[i].getColumnSpan( mapping );
 		}
 		return span;
 	}
 
 	@Override
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		int[] sqlTypes = new int[getColumnSpan( mapping )];
 		int n = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int[] subtypes = propertyTypes[i].sqlTypes( mapping );
 			for ( int subtype : subtypes ) {
 				sqlTypes[n++] = subtype;
 			}
 		}
 		return sqlTypes;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[getColumnSpan( mapping )];
 		int soFar = 0;
 		for ( Type propertyType : propertyTypes ) {
 			final Size[] propertySizes = propertyType.dictatedSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[getColumnSpan( mapping )];
 		int soFar = 0;
 		for ( Type propertyType : propertyTypes ) {
 			final Size[] propertySizes = propertyType.defaultSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 
 	@Override
 	public final boolean isComponentType() {
 		return true;
 	}
 
 	public Class getReturnedClass() {
 		return componentTuplizer.getMappedClass();
 	}
 
 	@Override
 	public boolean isSame(Object x, Object y) throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		Object[] xvalues = getPropertyValues( x, entityMode );
 		Object[] yvalues = getPropertyValues( y, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isSame( xvalues[i], yvalues[i] ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public boolean isEqual(final Object x, final Object y) throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( getPropertyValue( x, i ), getPropertyValue( y, i ) ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public boolean isEqual(final Object x, final Object y, final SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( x == y ) {
 			return true;
 		}
 		if ( x == null || y == null ) {
 			return false;
 		}
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( !propertyTypes[i].isEqual( getPropertyValue( x, i ), getPropertyValue( y, i ), factory ) ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public int compare(final Object x, final Object y) {
 		if ( x == y ) {
 			return 0;
 		}
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int propertyCompare = propertyTypes[i].compare( getPropertyValue( x, i ), getPropertyValue( y, i ) );
 			if ( propertyCompare != 0 ) {
 				return propertyCompare;
 			}
 		}
 		return 0;
 	}
 
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	@Override
 	public int getHashCode(final Object x) {
 		int result = 17;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = getPropertyValue( x, i );
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y );
 			}
 		}
 		return result;
 	}
 
 	@Override
 	public int getHashCode(final Object x, final SessionFactoryImplementor factory) {
 		int result = 17;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			Object y = getPropertyValue( x, i );
 			result *= 37;
 			if ( y != null ) {
 				result += propertyTypes[i].getHashCode( y, factory );
 			}
 		}
 		return result;
 	}
 
 	@Override
 	public boolean isDirty(final Object x, final Object y, final SessionImplementor session) throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
 		for ( int i = 0; i < propertySpan; i++ ) {
 			if ( propertyTypes[i].isDirty( getPropertyValue( x, i ), getPropertyValue( y, i ), session ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public boolean isDirty(final Object x, final Object y, final boolean[] checkable, final SessionImplementor session)
 			throws HibernateException {
 		if ( x == y ) {
 			return false;
 		}
 		if ( x == null || y == null ) {
 			return true;
 		}
 		int loc = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			if ( len <= 1 ) {
 				final boolean dirty = ( len == 0 || checkable[loc] ) &&
 						propertyTypes[i].isDirty( getPropertyValue( x, i ), getPropertyValue( y, i ), session );
 				if ( dirty ) {
 					return true;
 				}
 			}
 			else {
 				boolean[] subcheckable = new boolean[len];
 				System.arraycopy( checkable, loc, subcheckable, 0, len );
 				final boolean dirty = propertyTypes[i].isDirty(
 						getPropertyValue( x, i ),
 						getPropertyValue( y, i ),
 						subcheckable,
 						session
 				);
 				if ( dirty ) {
 					return true;
 				}
 			}
 			loc += len;
 		}
 		return false;
 	}
 
 	@Override
 	public boolean isModified(
 			final Object old,
 			final Object current,
 			final boolean[] checkable,
 			final SessionImplementor session) throws HibernateException {
 		if ( current == null ) {
 			return old != null;
 		}
 		if ( old == null ) {
 			return true;
 		}
 		Object[] oldValues = (Object[]) old;
 		int loc = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			boolean[] subcheckable = new boolean[len];
 			System.arraycopy( checkable, loc, subcheckable, 0, len );
 			if ( propertyTypes[i].isModified( oldValues[i], getPropertyValue( current, i ), subcheckable, session ) ) {
 				return true;
 			}
 			loc += len;
 		}
 		return false;
 
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return resolve( hydrate( rs, names, session, owner ), session, owner );
 	}
 
 	@Override
 	public void nullSafeSet(PreparedStatement st, Object value, int begin, SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, entityMode );
 
 		for ( int i = 0; i < propertySpan; i++ ) {
 			propertyTypes[i].nullSafeSet( st, subvalues[i], begin, session );
 			begin += propertyTypes[i].getColumnSpan( session.getFactory() );
 		}
 	}
 
 	@Override
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int begin,
 			boolean[] settable,
 			SessionImplementor session)
 			throws HibernateException, SQLException {
 
 		Object[] subvalues = nullSafeGetValues( value, entityMode );
 
 		int loc = 0;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int len = propertyTypes[i].getColumnSpan( session.getFactory() );
 			//noinspection StatementWithEmptyBody
 			if ( len == 0 ) {
 				//noop
 			}
 			else if ( len == 1 ) {
 				if ( settable[loc] ) {
 					propertyTypes[i].nullSafeSet( st, subvalues[i], begin, session );
 					begin++;
 				}
 			}
 			else {
 				boolean[] subsettable = new boolean[len];
 				System.arraycopy( settable, loc, subsettable, 0, len );
 				propertyTypes[i].nullSafeSet( st, subvalues[i], begin, subsettable, session );
 				begin += ArrayHelper.countTrue( subsettable );
 			}
 			loc += len;
 		}
 	}
 
 	private Object[] nullSafeGetValues(Object value, EntityMode entityMode) throws HibernateException {
 		if ( value == null ) {
 			return new Object[propertySpan];
 		}
 		else {
 			return getPropertyValues( value, entityMode );
 		}
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	@Override
 	public Object getPropertyValue(Object component, int i, SessionImplementor session)
 			throws HibernateException {
 		return getPropertyValue( component, i );
 	}
 
 	public Object getPropertyValue(Object component, int i, EntityMode entityMode)
 			throws HibernateException {
 		return getPropertyValue( component, i );
 	}
 
 	public Object getPropertyValue(Object component, int i)
 			throws HibernateException {
 		if ( component instanceof Object[] ) {
 			// A few calls to hashCode pass the property values already in an
 			// Object[] (ex: QueryKey hash codes for cached queries).
 			// It's easiest to just check for the condition here prior to
 			// trying reflection.
 			return ( (Object[]) component )[i];
 		}
 		else {
 			return componentTuplizer.getPropertyValue( component, i );
 		}
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, SessionImplementor session)
 			throws HibernateException {
 		return getPropertyValues( component, entityMode );
 	}
 
 	@Override
 	public Object[] getPropertyValues(Object component, EntityMode entityMode)
 			throws HibernateException {
 		if ( component instanceof Object[] ) {
 			// A few calls to hashCode pass the property values already in an 
 			// Object[] (ex: QueryKey hash codes for cached queries).
 			// It's easiest to just check for the condition here prior to
 			// trying reflection.
 			return (Object[]) component;
 		}
 		else {
 			return componentTuplizer.getPropertyValues( component );
 		}
 	}
 
 	@Override
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode)
 			throws HibernateException {
 		componentTuplizer.setPropertyValues( component, values );
 	}
 
 	@Override
 	public Type[] getSubtypes() {
 		return propertyTypes;
 	}
 
 	@Override
 	public String getName() {
 		return "component" + ArrayHelper.toString( propertyNames );
 	}
 
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 
 		if ( entityMode == null ) {
 			throw new ClassCastException( value.getClass().getName() );
 		}
 		Map<String, String> result = new HashMap<String, String>();
 		Object[] values = getPropertyValues( value, entityMode );
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			result.put( propertyNames[i], propertyTypes[i].toLoggableString( values[i], factory ) );
 		}
 		return StringHelper.unqualify( getName() ) + result.toString();
 	}
 
 	@Override
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
 
 	@Override
 	public Object deepCopy(Object component, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( component == null ) {
 			return null;
 		}
 
 		Object[] values = getPropertyValues( component, entityMode );
 		for ( int i = 0; i < propertySpan; i++ ) {
 			values[i] = propertyTypes[i].deepCopy( values[i], factory );
 		}
 
 		Object result = instantiate( entityMode );
 		setPropertyValues( result, values, entityMode );
 
 		//not absolutely necessary, but helps for some
 		//equals()/hashCode() implementations
 		if ( componentTuplizer.hasParentProperty() ) {
 			componentTuplizer.setParent( result, componentTuplizer.getParent( component ), factory );
 		}
 
 		return result;
 	}
 
 	@Override
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache)
 			throws HibernateException {
 
 		if ( original == null ) {
 			return null;
 		}
 		//if ( original == target ) return target;
 
 		final Object result = target == null
 				? instantiate( owner, session )
 				: target;
 
 		Object[] values = TypeHelper.replace(
 				getPropertyValues( original, entityMode ),
 				getPropertyValues( result, entityMode ),
 				propertyTypes,
 				session,
 				owner,
 				copyCache
 		);
 
 		setPropertyValues( result, values, entityMode );
 		return result;
 	}
 
 	@Override
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache,
 			ForeignKeyDirection foreignKeyDirection)
 			throws HibernateException {
 
 		if ( original == null ) {
 			return null;
 		}
 		//if ( original == target ) return target;
 
 		final Object result = target == null ?
 				instantiate( owner, session ) :
 				target;
 
 		Object[] values = TypeHelper.replace(
 				getPropertyValues( original, entityMode ),
 				getPropertyValues( result, entityMode ),
 				propertyTypes,
 				session,
 				owner,
 				copyCache,
 				foreignKeyDirection
 		);
 
 		setPropertyValues( result, values, entityMode );
 		return result;
 	}
 
 	/**
 	 * This method does not populate the component parent
 	 */
 	public Object instantiate(EntityMode entityMode) throws HibernateException {
 		return componentTuplizer.instantiate();
 	}
 
 	public Object instantiate(Object parent, SessionImplementor session)
 			throws HibernateException {
 
 		Object result = instantiate( entityMode );
 
 		if ( componentTuplizer.hasParentProperty() && parent != null ) {
 			componentTuplizer.setParent(
 					result,
 					session.getPersistenceContext().proxyFor( parent ),
 					session.getFactory()
 			);
 		}
 
 		return result;
 	}
 
 	@Override
 	public CascadeStyle getCascadeStyle(int i) {
 		return cascade[i];
 	}
 
 	@Override
 	public boolean isMutable() {
 		return true;
 	}
 
 	@Override
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			Object[] values = getPropertyValues( value, entityMode );
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				values[i] = propertyTypes[i].disassemble( values[i], session, owner );
 			}
 			return values;
 		}
 	}
 
 	@Override
 	public Object assemble(Serializable object, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( object == null ) {
 			return null;
 		}
 		else {
 			Object[] values = (Object[]) object;
 			Object[] assembled = new Object[values.length];
 			for ( int i = 0; i < propertyTypes.length; i++ ) {
 				assembled[i] = propertyTypes[i].assemble( (Serializable) values[i], session, owner );
 			}
 			Object result = instantiate( owner, session );
 			setPropertyValues( result, assembled, entityMode );
 			return result;
 		}
 	}
 
 	@Override
 	public FetchMode getFetchMode(int i) {
 		return joinedFetch[i];
 	}
 
 	@Override
 	public Object hydrate(
 			final ResultSet rs,
 			final String[] names,
 			final SessionImplementor session,
 			final Object owner)
 			throws HibernateException, SQLException {
 
 		int begin = 0;
 		boolean notNull = false;
 		Object[] values = new Object[propertySpan];
 		for ( int i = 0; i < propertySpan; i++ ) {
 			int length = propertyTypes[i].getColumnSpan( session.getFactory() );
 			String[] range = ArrayHelper.slice( names, begin, length ); //cache this
 			Object val = propertyTypes[i].hydrate( rs, range, session, owner );
 			if ( val == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[i] = val;
 			begin += length;
 		}
 
 		return notNull ? values : null;
 	}
 
 	@Override
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 
 		if ( value != null ) {
 			Object result = instantiate( owner, session );
 			Object[] values = (Object[]) value;
 			Object[] resolvedValues = new Object[values.length]; //only really need new array during semiresolve!
 			for ( int i = 0; i < values.length; i++ ) {
 				resolvedValues[i] = propertyTypes[i].resolve( values[i], session, owner );
 			}
 			setPropertyValues( result, resolvedValues, entityMode );
 			return result;
 		}
 		else {
 			return null;
 		}
 	}
 
 	@Override
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		//note that this implementation is kinda broken
 		//for components with many-to-one associations
 		return resolve( value, session, owner );
 	}
 
 	@Override
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	@Override
-	public boolean isXMLElement() {
-		return true;
-	}
-
-	@Override
-	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
-		return xml;
-	}
-
-	@Override
-	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
-		replaceNode( node, (Element) value );
-	}
-
-	@Override
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[getColumnSpan( mapping )];
 		if ( value == null ) {
 			return result;
 		}
 		Object[] values = getPropertyValues( value, EntityMode.POJO ); //TODO!!!!!!!
 		int loc = 0;
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			boolean[] propertyNullness = propertyTypes[i].toColumnNullness( values[i], mapping );
 			System.arraycopy( propertyNullness, 0, result, loc, propertyNullness.length );
 			loc += propertyNullness.length;
 		}
 		return result;
 	}
 
 	@Override
 	public boolean isEmbedded() {
 		return false;
 	}
 
 	public int getPropertyIndex(String name) {
 		String[] names = getPropertyNames();
 		for ( int i = 0, max = names.length; i < max; i++ ) {
 			if ( names[i].equals( name ) ) {
 				return i;
 			}
 		}
 		throw new PropertyNotFoundException(
 				"Unable to locate property named " + name + " on " + getReturnedClass().getName()
 		);
 	}
 
 	private Boolean canDoExtraction;
 
 	@Override
 	public boolean canDoExtraction() {
 		if ( canDoExtraction == null ) {
 			canDoExtraction = determineIfProcedureParamExtractionCanBePerformed();
 		}
 		return canDoExtraction;
 	}
 
 	private boolean determineIfProcedureParamExtractionCanBePerformed() {
 		for ( Type propertyType : propertyTypes ) {
 			if ( !ProcedureParameterExtractionAware.class.isInstance( propertyType ) ) {
 				return false;
 			}
 			if ( !( (ProcedureParameterExtractionAware) propertyType ).canDoExtraction() ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	@Override
 	public Object extract(CallableStatement statement, int startIndex, SessionImplementor session) throws SQLException {
 		Object[] values = new Object[propertySpan];
 
 		int currentIndex = startIndex;
 		boolean notNull = false;
 		for ( int i = 0; i < propertySpan; i++ ) {
 			// we know this cast is safe from canDoExtraction
 			final ProcedureParameterExtractionAware propertyType = (ProcedureParameterExtractionAware) propertyTypes[i];
 			final Object value = propertyType.extract( statement, currentIndex, session );
 			if ( value == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[i] = value;
 			currentIndex += propertyType.getColumnSpan( session.getFactory() );
 		}
 
 		if ( !notNull ) {
 			values = null;
 		}
 
 		return resolve( values, session, null );
 	}
 
 	@Override
 	public Object extract(CallableStatement statement, String[] paramNames, SessionImplementor session)
 			throws SQLException {
 		// for this form to work all sub-property spans must be one (1)...
 
 		Object[] values = new Object[propertySpan];
 
 		int indx = 0;
 		boolean notNull = false;
 		for ( String paramName : paramNames ) {
 			// we know this cast is safe from canDoExtraction
 			final ProcedureParameterExtractionAware propertyType = (ProcedureParameterExtractionAware) propertyTypes[indx];
 			final Object value = propertyType.extract( statement, new String[] {paramName}, session );
 			if ( value == null ) {
 				if ( isKey ) {
 					return null; //different nullability rules for pk/fk
 				}
 			}
 			else {
 				notNull = true;
 			}
 			values[indx] = value;
 		}
 
 		if ( !notNull ) {
 			values = null;
 		}
 
 		return resolve( values, session, null );
 	}
 
 	public boolean hasNotNullProperty() {
 		return hasNotNullProperty;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java b/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
index 6c7f528a8e..c77d3446e1 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CompositeCustomType.java
@@ -1,290 +1,281 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.lang.reflect.Method;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadeStyles;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.LoggableUserType;
 
 import org.dom4j.Element;
 import org.dom4j.Node;
 
 /**
  * Adapts {@link CompositeUserType} to the {@link Type} interface
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class CompositeCustomType extends AbstractType implements CompositeType, BasicType {
 	private final CompositeUserType userType;
 	private final String[] registrationKeys;
 	private final String name;
 	private final boolean customLogging;
 
 	public CompositeCustomType(CompositeUserType userType) {
 		this( userType, ArrayHelper.EMPTY_STRING_ARRAY );
 	}
 
 	public CompositeCustomType(CompositeUserType userType, String[] registrationKeys) {
 		this.userType = userType;
 		this.name = userType.getClass().getName();
 		this.customLogging = LoggableUserType.class.isInstance( userType );
 		this.registrationKeys = registrationKeys;
 	}
 
 	public String[] getRegistrationKeys() {
 		return registrationKeys;
 	}
 
 	public CompositeUserType getUserType() {
 		return userType;
 	}
 
 	public boolean isMethodOf(Method method) {
 		return false;
 	}
 
 	public Type[] getSubtypes() {
 		return userType.getPropertyTypes();
 	}
 
 	public String[] getPropertyNames() {
 		return userType.getPropertyNames();
 	}
 
 	public Object[] getPropertyValues(Object component, SessionImplementor session) throws HibernateException {
 		return getPropertyValues( component, EntityMode.POJO );
 	}
 
 	public Object[] getPropertyValues(Object component, EntityMode entityMode) throws HibernateException {
 		int len = getSubtypes().length;
 		Object[] result = new Object[len];
 		for ( int i = 0; i < len; i++ ) {
 			result[i] = getPropertyValue( component, i );
 		}
 		return result;
 	}
 
 	public void setPropertyValues(Object component, Object[] values, EntityMode entityMode) throws HibernateException {
 		for ( int i = 0; i < values.length; i++ ) {
 			userType.setPropertyValue( component, i, values[i] );
 		}
 	}
 
 	public Object getPropertyValue(Object component, int i, SessionImplementor session) throws HibernateException {
 		return getPropertyValue( component, i );
 	}
 
 	public Object getPropertyValue(Object component, int i) throws HibernateException {
 		return userType.getPropertyValue( component, i );
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return CascadeStyles.NONE;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return FetchMode.DEFAULT;
 	}
 
 	public boolean isComponentType() {
 		return true;
 	}
 
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return userType.deepCopy( value );
 	}
 
 	public Object assemble(
 			Serializable cached,
 			SessionImplementor session,
 			Object owner) throws HibernateException {
 		return userType.assemble( cached, session, owner );
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		return userType.disassemble( value, session );
 	}
 
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache)
 			throws HibernateException {
 		return userType.replace( original, target, session, owner );
 	}
 
 	public boolean isEqual(Object x, Object y)
 			throws HibernateException {
 		return userType.equals( x, y );
 	}
 
 	public int getHashCode(Object x) {
 		return userType.hashCode( x );
 	}
 
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		Type[] types = userType.getPropertyTypes();
 		int n = 0;
 		for ( Type type : types ) {
 			n += type.getColumnSpan( mapping );
 		}
 		return n;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public Class getReturnedClass() {
 		return userType.returnedClass();
 	}
 
 	public boolean isMutable() {
 		return userType.isMutable();
 	}
 
 	public Object nullSafeGet(
 			ResultSet rs,
 			String columnName,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		return userType.nullSafeGet( rs, new String[] {columnName}, session, owner );
 	}
 
 	public Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		return userType.nullSafeGet( rs, names, session, owner );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 		userType.nullSafeSet( st, value, index, session );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		userType.nullSafeSet( st, value, index, session );
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		int[] result = new int[getColumnSpan( mapping )];
 		int n = 0;
 		for ( Type type : userType.getPropertyTypes() ) {
 			for ( int sqlType : type.sqlTypes( mapping ) ) {
 				result[n++] = sqlType;
 			}
 		}
 		return result;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[getColumnSpan( mapping )];
 		int soFar = 0;
 		for ( Type propertyType : userType.getPropertyTypes() ) {
 			final Size[] propertySizes = propertyType.dictatedSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		//Not called at runtime so doesn't matter if its slow :)
 		final Size[] sizes = new Size[getColumnSpan( mapping )];
 		int soFar = 0;
 		for ( Type propertyType : userType.getPropertyTypes() ) {
 			final Size[] propertySizes = propertyType.defaultSizes( mapping );
 			System.arraycopy( propertySizes, 0, sizes, soFar, propertySizes.length );
 			soFar += propertySizes.length;
 		}
 		return sizes;
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		else if ( customLogging ) {
 			return ( (LoggableUserType) userType ).toLoggableString( value, factory );
 		}
 		else {
 			return value.toString();
 		}
 	}
 
 	public boolean[] getPropertyNullability() {
 		return null;
 	}
 
-	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
-		return xml;
-	}
-
-	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory)
-			throws HibernateException {
-		replaceNode( node, (Element) value );
-	}
-
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[getColumnSpan( mapping )];
 		if ( value == null ) {
 			return result;
 		}
 		Object[] values = getPropertyValues( value, EntityMode.POJO ); //TODO!!!!!!!
 		int loc = 0;
 		Type[] propertyTypes = getSubtypes();
 		for ( int i = 0; i < propertyTypes.length; i++ ) {
 			boolean[] propertyNullness = propertyTypes[i].toColumnNullness( values[i], mapping );
 			System.arraycopy( propertyNullness, 0, result, loc, propertyNullness.length );
 			loc += propertyNullness.length;
 		}
 		return result;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return isDirty( old, current, session );
 	}
 
 	public boolean isEmbedded() {
 		return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java b/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
index b9e03cbcfd..20747bb4f7 100755
--- a/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CustomCollectionType.java
@@ -1,121 +1,105 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.usertype.LoggableUserType;
 import org.hibernate.usertype.UserCollectionType;
 
 /**
  * A custom type for mapping user-written classes that implement <tt>PersistentCollection</tt>
  * 
  * @see org.hibernate.collection.spi.PersistentCollection
  * @see org.hibernate.usertype.UserCollectionType
  * @author Gavin King
  */
 public class CustomCollectionType extends CollectionType {
 
 	private final UserCollectionType userType;
 	private final boolean customLogging;
 
-	/**
-	 * @deprecated Use {@link #CustomCollectionType(TypeFactory.TypeScope, Class, String, String )} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public CustomCollectionType(
-			TypeFactory.TypeScope typeScope,
-			Class userTypeClass,
-			String role,
-			String foreignKeyPropertyName,
-			boolean isEmbeddedInXML) {
-		super( typeScope, role, foreignKeyPropertyName, isEmbeddedInXML );
-		userType = createUserCollectionType( userTypeClass );
-		customLogging = LoggableUserType.class.isAssignableFrom( userTypeClass );
-	}
-
 	public CustomCollectionType(
 			TypeFactory.TypeScope typeScope,
 			Class userTypeClass,
 			String role,
 			String foreignKeyPropertyName) {
 		super( typeScope, role, foreignKeyPropertyName );
 		userType = createUserCollectionType( userTypeClass );
 		customLogging = LoggableUserType.class.isAssignableFrom( userTypeClass );
 	}
 
 	private static UserCollectionType createUserCollectionType(Class userTypeClass) {
 		if ( !UserCollectionType.class.isAssignableFrom( userTypeClass ) ) {
 			throw new MappingException( "Custom type does not implement UserCollectionType: " + userTypeClass.getName() );
 		}
 
 		try {
 			return ( UserCollectionType ) userTypeClass.newInstance();
 		}
 		catch ( InstantiationException ie ) {
 			throw new MappingException( "Cannot instantiate custom type: " + userTypeClass.getName() );
 		}
 		catch ( IllegalAccessException iae ) {
 			throw new MappingException( "IllegalAccessException trying to instantiate custom type: " + userTypeClass.getName() );
 		}
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key)
 	throws HibernateException {
 		return userType.instantiate(session, persister);
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return userType.wrap(session, collection);
 	}
 
 	public Class getReturnedClass() {
 		return userType.instantiate( -1 ).getClass();
 	}
 
 	public Object instantiate(int anticipatedType) {
 		return userType.instantiate( anticipatedType );
 	}
 
 	public Iterator getElementsIterator(Object collection) {
 		return userType.getElementsIterator(collection);
 	}
 	public boolean contains(Object collection, Object entity, SessionImplementor session) {
 		return userType.contains(collection, entity);
 	}
 	public Object indexOf(Object collection, Object entity) {
 		return userType.indexOf(collection, entity);
 	}
 
 	public Object replaceElements(Object original, Object target, Object owner, Map copyCache, SessionImplementor session)
 	throws HibernateException {
 		CollectionPersister cp = session.getFactory().getCollectionPersister( getRole() );
 		return userType.replaceElements(original, target, cp, owner, copyCache, session);
 	}
 
 	protected String renderLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( customLogging ) {
 			return ( ( LoggableUserType ) userType ).toLoggableString( value, factory );
 		}
 		else {
 			return super.renderLoggableString( value, factory );
 		}
 	}
 
 	public UserCollectionType getUserType() {
 		return userType;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/CustomType.java b/hibernate-core/src/main/java/org/hibernate/type/CustomType.java
index c0a1983d84..d90ff10ecb 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/CustomType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/CustomType.java
@@ -1,262 +1,253 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.usertype.EnhancedUserType;
 import org.hibernate.usertype.LoggableUserType;
 import org.hibernate.usertype.Sized;
 import org.hibernate.usertype.UserType;
 import org.hibernate.usertype.UserVersionType;
 
 import org.dom4j.Node;
 
 /**
  * Adapts {@link UserType} to the generic {@link Type} interface, in order
  * to isolate user code from changes in the internal Type contracts.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public class CustomType
 		extends AbstractType
 		implements IdentifierType, DiscriminatorType, VersionType, BasicType, StringRepresentableType {
 
 	private final UserType userType;
 	private final String name;
 	private final int[] types;
 	private final Size[] dictatedSizes;
 	private final Size[] defaultSizes;
 	private final boolean customLogging;
 	private final String[] registrationKeys;
 
 	public CustomType(UserType userType) throws MappingException {
 		this( userType, ArrayHelper.EMPTY_STRING_ARRAY );
 	}
 
 	public CustomType(UserType userType, String[] registrationKeys) throws MappingException {
 		this.userType = userType;
 		this.name = userType.getClass().getName();
 		this.types = userType.sqlTypes();
 		this.dictatedSizes = Sized.class.isInstance( userType )
 				? ( (Sized) userType ).dictatedSizes()
 				: new Size[ types.length ];
 		this.defaultSizes = Sized.class.isInstance( userType )
 				? ( (Sized) userType ).defaultSizes()
 				: new Size[ types.length ];
 		this.customLogging = LoggableUserType.class.isInstance( userType );
 		this.registrationKeys = registrationKeys;
 	}
 
 	public UserType getUserType() {
 		return userType;
 	}
 
 	public String[] getRegistrationKeys() {
 		return registrationKeys;
 	}
 
 	public int[] sqlTypes(Mapping pi) {
 		return types;
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return dictatedSizes;
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return defaultSizes;
 	}
 
 	public int getColumnSpan(Mapping session) {
 		return types.length;
 	}
 
 	public Class getReturnedClass() {
 		return userType.returnedClass();
 	}
 
 	public boolean isEqual(Object x, Object y) throws HibernateException {
 		return userType.equals( x, y );
 	}
 
 	public int getHashCode(Object x) {
 		return userType.hashCode(x);
 	}
 
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return userType.nullSafeGet(rs, names, session, owner);
 	}
 
 	public Object nullSafeGet(ResultSet rs, String columnName, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return nullSafeGet(rs, new String[] { columnName }, session, owner);
 	}
 
 
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 			throws HibernateException {
 		return userType.assemble(cached, owner);
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 			throws HibernateException {
 		return userType.disassemble(value);
 	}
 
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache) throws HibernateException {
 		return userType.replace( original, target, owner );
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session)
 			throws HibernateException, SQLException {
 		if ( settable[0] ) {
 			userType.nullSafeSet( st, value, index, session );
 		}
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
 			throws HibernateException, SQLException {
 		userType.nullSafeSet( st, value, index, session );
 	}
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public String toXMLString(Object value, SessionFactoryImplementor factory) {
 		return toString( value );
 	}
 
 	@SuppressWarnings({ "UnusedDeclaration" })
 	public Object fromXMLString(String xml, Mapping factory) {
 		return fromStringValue( xml );
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		return userType.deepCopy(value);
 	}
 
 	public boolean isMutable() {
 		return userType.isMutable();
 	}
 
 	public Object stringToObject(String xml) {
 		return fromStringValue( xml );
 	}
 
 	public String objectToSQLString(Object value, Dialect dialect) throws Exception {
 		return ( (EnhancedUserType) userType ).objectToSQLString(value);
 	}
 
 	public Comparator getComparator() {
 		return (Comparator) userType;
 	}
 
 	public Object next(Object current, SessionImplementor session) {
 		return ( (UserVersionType) userType ).next( current, session );
 	}
 
 	public Object seed(SessionImplementor session) {
 		return ( (UserVersionType) userType ).seed( session );
 	}
 
-	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
-		return fromXMLString( xml.getText(), factory );
-	}
-
-	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory)
-			throws HibernateException {
-		node.setText( toXMLString(value, factory) );
-	}
-
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 			throws HibernateException {
 		if ( value == null ) {
 			return "null";
 		}
 		else if ( customLogging ) {
 			return ( ( LoggableUserType ) userType ).toLoggableString( value, factory );
 		}
 		else {
 			return toXMLString( value, factory );
 		}
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan(mapping) ];
 		if ( value != null ) {
 			Arrays.fill(result, true);
 		}
 		return result;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session)
 			throws HibernateException {
 		return checkable[0] && isDirty(old, current, session);
 	}
 
 	@Override
 	@SuppressWarnings("unchecked")
 	public String toString(Object value) throws HibernateException {
 		if ( StringRepresentableType.class.isInstance( userType ) ) {
 			return ( (StringRepresentableType) userType ).toString( value );
 		}
 		if ( value == null ) {
 			return null;
 		}
 		if ( EnhancedUserType.class.isInstance( userType ) ) {
 			//noinspection deprecation
 			return ( (EnhancedUserType) userType ).toXMLString( value );
 		}
 		return value.toString();
 	}
 
 	@Override
 	public Object fromStringValue(String string) throws HibernateException {
 		if ( StringRepresentableType.class.isInstance( userType ) ) {
 			return ( (StringRepresentableType) userType ).fromStringValue( string );
 		}
 		if ( EnhancedUserType.class.isInstance( userType ) ) {
 			//noinspection deprecation
 			return ( (EnhancedUserType) userType ).fromXMLString( string );
 		}
 		throw new HibernateException(
 				String.format(
 						"Could not process #fromStringValue, UserType class [%s] did not implement %s or %s",
 						name,
 						StringRepresentableType.class.getName(),
 						EnhancedUserType.class.getName()
 				)
 		);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
index 174d077add..17419aa996 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
@@ -1,780 +1,690 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
-import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.spi.EntityUniqueKey;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.PersistenceContext;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.proxy.HibernateProxy;
-import org.hibernate.tuple.ElementWrapper;
-
-import org.dom4j.Element;
-import org.dom4j.Node;
 
 /**
  * Base for types which map associations to persistent entities.
  *
  * @author Gavin King
  */
 public abstract class EntityType extends AbstractType implements AssociationType {
 
 	private final TypeFactory.TypeScope scope;
 	private final String associatedEntityName;
 	protected final String uniqueKeyPropertyName;
-	protected final boolean isEmbeddedInXML;
 	private final boolean eager;
 	private final boolean unwrapProxy;
 	private final boolean referenceToPrimaryKey;
 
 	/**
 	 * Cached because of performance
 	 *
 	 * @see #getIdentifierType(SessionImplementor)
 	 * @see #getIdentifierType(Mapping)
 	 */
 	private transient volatile Type associatedIdentifierType;
 
 	/**
 	 * Cached because of performance
 	 *
 	 * @see #getAssociatedEntityPersister
 	 */
 	private transient volatile EntityPersister associatedEntityPersister;
 
 	private transient Class returnedClass;
 
 	/**
 	 * Constructs the requested entity type mapping.
 	 *
 	 * @param scope The type scope
 	 * @param entityName The name of the associated entity.
 	 * @param uniqueKeyPropertyName The property-ref name, or null if we
 	 * reference the PK of the associated entity.
 	 * @param eager Is eager fetching enabled.
-	 * @param isEmbeddedInXML Should values of this mapping be embedded in XML modes?
-	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
-	 * says to return the "implementation target" of lazy prooxies; typically only possible
-	 * with lazy="no-proxy".
-	 *
-	 * @deprecated Use {@link #EntityType(org.hibernate.type.TypeFactory.TypeScope, String, boolean, String, boolean, boolean)} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	protected EntityType(
-			TypeFactory.TypeScope scope,
-			String entityName,
-			String uniqueKeyPropertyName,
-			boolean eager,
-			boolean isEmbeddedInXML,
-			boolean unwrapProxy) {
-		this( scope, entityName, uniqueKeyPropertyName == null, uniqueKeyPropertyName, eager, unwrapProxy );
-	}
-
-	/**
-	 * Constructs the requested entity type mapping.
-	 *
-	 * @param scope The type scope
-	 * @param entityName The name of the associated entity.
-	 * @param uniqueKeyPropertyName The property-ref name, or null if we
-	 * reference the PK of the associated entity.
-	 * @param eager Is eager fetching enabled.
 	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
 	 * says to return the "implementation target" of lazy prooxies; typically only possible
 	 * with lazy="no-proxy".
 	 *
 	 * @deprecated Use {@link #EntityType(org.hibernate.type.TypeFactory.TypeScope, String, boolean, String, boolean, boolean)} instead.
 	 */
 	@Deprecated
 	protected EntityType(
 			TypeFactory.TypeScope scope,
 			String entityName,
 			String uniqueKeyPropertyName,
 			boolean eager,
 			boolean unwrapProxy) {
 		this( scope, entityName, uniqueKeyPropertyName == null, uniqueKeyPropertyName, eager, unwrapProxy );
 	}
 
 	/**
 	 * Constructs the requested entity type mapping.
 	 *
 	 * @param scope The type scope
 	 * @param entityName The name of the associated entity.
 	 * @param referenceToPrimaryKey True if association references a primary key.
 	 * @param uniqueKeyPropertyName The property-ref name, or null if we
 	 * reference the PK of the associated entity.
 	 * @param eager Is eager fetching enabled.
 	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
 	 * says to return the "implementation target" of lazy prooxies; typically only possible
 	 * with lazy="no-proxy".
 	 */
 	protected EntityType(
 			TypeFactory.TypeScope scope,
 			String entityName,
 			boolean referenceToPrimaryKey,
 			String uniqueKeyPropertyName,
 			boolean eager,
 			boolean unwrapProxy) {
 		this.scope = scope;
 		this.associatedEntityName = entityName;
 		this.uniqueKeyPropertyName = uniqueKeyPropertyName;
-		this.isEmbeddedInXML = true;
 		this.eager = eager;
 		this.unwrapProxy = unwrapProxy;
 		this.referenceToPrimaryKey = referenceToPrimaryKey;
 	}
 
 	protected TypeFactory.TypeScope scope() {
 		return scope;
 	}
 
 	/**
 	 * An entity type is a type of association type
 	 *
 	 * @return True.
 	 */
 	@Override
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	/**
 	 * Explicitly, an entity type is an entity type ;)
 	 *
 	 * @return True.
 	 */
 	@Override
 	public final boolean isEntityType() {
 		return true;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return false;
 	}
 
 	/**
 	 * Generates a string representation of this type.
 	 *
 	 * @return string rep
 	 */
 	@Override
 	public String toString() {
 		return getClass().getName() + '(' + getAssociatedEntityName() + ')';
 	}
 
 	/**
 	 * For entity types, the name correlates to the associated entity name.
 	 */
 	@Override
 	public String getName() {
 		return associatedEntityName;
 	}
 
 	/**
 	 * Does this association foreign key reference the primary key of the other table?
 	 * Otherwise, it references a property-ref.
 	 *
 	 * @return True if this association reference the PK of the associated entity.
 	 */
 	public boolean isReferenceToPrimaryKey() {
 		return referenceToPrimaryKey;
 	}
 
 	@Override
 	public String getRHSUniqueKeyPropertyName() {
 		// Return null if this type references a PK.  This is important for
 		// associations' use of mappedBy referring to a derived ID.
 		return referenceToPrimaryKey ? null : uniqueKeyPropertyName;
 	}
 
 	@Override
 	public String getLHSPropertyName() {
 		return null;
 	}
 
 	public String getPropertyName() {
 		return null;
 	}
 
 	/**
 	 * The name of the associated entity.
 	 *
 	 * @return The associated entity name.
 	 */
 	public final String getAssociatedEntityName() {
 		return associatedEntityName;
 	}
 
 	/**
 	 * The name of the associated entity.
 	 *
 	 * @param factory The session factory, for resolution.
 	 *
 	 * @return The associated entity name.
 	 */
 	@Override
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
 		return getAssociatedEntityName();
 	}
 
 	/**
 	 * Retrieves the {@link Joinable} defining the associated entity.
 	 *
 	 * @param factory The session factory.
 	 *
 	 * @return The associated joinable
 	 *
 	 * @throws MappingException Generally indicates an invalid entity name.
 	 */
 	@Override
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) throws MappingException {
 		return (Joinable) getAssociatedEntityPersister( factory );
 	}
 
 	/**
 	 * This returns the wrong class for an entity with a proxy, or for a named
 	 * entity.  Theoretically it should return the proxy class, but it doesn't.
 	 * <p/>
 	 * The problem here is that we do not necessarily have a ref to the associated
 	 * entity persister (nor to the session factory, to look it up) which is really
 	 * needed to "do the right thing" here...
 	 *
 	 * @return The entiyt class.
 	 */
 	@Override
 	public final Class getReturnedClass() {
 		if ( returnedClass == null ) {
 			returnedClass = determineAssociatedEntityClass();
 		}
 		return returnedClass;
 	}
 
 	private Class determineAssociatedEntityClass() {
 		final String entityName = getAssociatedEntityName();
 		try {
 			return ReflectHelper.classForName( entityName );
 		}
 		catch (ClassNotFoundException cnfe) {
 			return this.scope.resolveFactory().getEntityPersister( entityName ).
 					getEntityTuplizer().getMappedClass();
 		}
 	}
 
 	@Override
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 			throws HibernateException, SQLException {
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	@Override
 	public final Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		return resolve( hydrate( rs, names, session, owner ), session, owner );
 	}
 
 	/**
 	 * Two entities are considered the same when their instances are the same.
 	 *
 	 * @param x One entity instance
 	 * @param y Another entity instance
 	 *
 	 * @return True if x == y; false otherwise.
 	 */
 	@Override
 	public final boolean isSame(Object x, Object y) {
 		return x == y;
 	}
 
 	@Override
 	public int compare(Object x, Object y) {
 		return 0; //TODO: entities CAN be compared, by PK, fix this! -> only if/when we can extract the id values....
 	}
 
 	@Override
 	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return value; //special case ... this is the leaf of the containment graph, even though not immutable
 	}
 
 	@Override
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache) throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		Object cached = copyCache.get( original );
 		if ( cached != null ) {
 			return cached;
 		}
 		else {
 			if ( original == target ) {
 				return target;
 			}
 			if ( session.getContextEntityIdentifier( original ) == null &&
 					ForeignKeys.isTransient( associatedEntityName, original, Boolean.FALSE, session ) ) {
 				final Object copy = session.getEntityPersister( associatedEntityName, original )
 						.instantiate( null, session );
 				copyCache.put( original, copy );
 				return copy;
 			}
 			else {
 				Object id = getIdentifier( original, session );
 				if ( id == null ) {
 					throw new AssertionFailure(
 							"non-transient entity has a null id: " + original.getClass()
 									.getName()
 					);
 				}
 				id = getIdentifierOrUniqueKeyType( session.getFactory() )
 						.replace( id, null, session, owner, copyCache );
 				return resolve( id, session, owner );
 			}
 		}
 	}
 
 	@Override
 	public int getHashCode(Object x, SessionFactoryImplementor factory) {
 		EntityPersister persister = getAssociatedEntityPersister( factory );
 		if ( !persister.canExtractIdOutOfEntity() ) {
 			return super.getHashCode( x );
 		}
 
 		final Serializable id;
 		if ( x instanceof HibernateProxy ) {
 			id = ( (HibernateProxy) x ).getHibernateLazyInitializer().getIdentifier();
 		}
 		else {
 			final Class mappedClass = persister.getMappedClass();
 			if ( mappedClass.isAssignableFrom( x.getClass() ) ) {
 				id = persister.getIdentifier( x );
 			}
 			else {
 				id = (Serializable) x;
 			}
 		}
 		return persister.getIdentifierType().getHashCode( id, factory );
 	}
 
 	@Override
 	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) {
 		// associations (many-to-one and one-to-one) can be null...
 		if ( x == null || y == null ) {
 			return x == y;
 		}
 
 		EntityPersister persister = getAssociatedEntityPersister( factory );
 		if ( !persister.canExtractIdOutOfEntity() ) {
 			return super.isEqual( x, y );
 		}
 
 		final Class mappedClass = persister.getMappedClass();
 		Serializable xid;
 		if ( x instanceof HibernateProxy ) {
 			xid = ( (HibernateProxy) x ).getHibernateLazyInitializer()
 					.getIdentifier();
 		}
 		else {
 			if ( mappedClass.isAssignableFrom( x.getClass() ) ) {
 				xid = persister.getIdentifier( x );
 			}
 			else {
 				//JPA 2 case where @IdClass contains the id and not the associated entity
 				xid = (Serializable) x;
 			}
 		}
 
 		Serializable yid;
 		if ( y instanceof HibernateProxy ) {
 			yid = ( (HibernateProxy) y ).getHibernateLazyInitializer()
 					.getIdentifier();
 		}
 		else {
 			if ( mappedClass.isAssignableFrom( y.getClass() ) ) {
 				yid = persister.getIdentifier( y );
 			}
 			else {
 				//JPA 2 case where @IdClass contains the id and not the associated entity
 				yid = (Serializable) y;
 			}
 		}
 
 		return persister.getIdentifierType()
 				.isEqual( xid, yid, factory );
 	}
 
 	@Override
-	public boolean isEmbeddedInXML() {
-		return isEmbeddedInXML;
-	}
-
-	@Override
-	public boolean isXMLElement() {
-		return isEmbeddedInXML;
-	}
-
-	@Override
-	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
-		if ( !isEmbeddedInXML ) {
-			return getIdentifierType( factory ).fromXMLNode( xml, factory );
-		}
-		else {
-			return xml;
-		}
-	}
-
-	@Override
-	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
-		if ( !isEmbeddedInXML ) {
-			getIdentifierType( factory ).setToXMLNode( node, value, factory );
-		}
-		else {
-			Element elt = (Element) value;
-			replaceNode( node, new ElementWrapper( elt ) );
-		}
-	}
-
-	@Override
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters) {
 		return getOnCondition( alias, factory, enabledFilters, null );
 	}
 
 	@Override
 	public String getOnCondition(
 			String alias,
 			SessionFactoryImplementor factory,
 			Map enabledFilters,
 			Set<String> treatAsDeclarations) {
 		if ( isReferenceToPrimaryKey() && ( treatAsDeclarations == null || treatAsDeclarations.isEmpty() ) ) {
 			return "";
 		}
 		else {
 			return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters, treatAsDeclarations );
 		}
 	}
 
 	/**
 	 * Resolve an identifier or unique key value
 	 */
 	@Override
 	public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
-		if ( isNotEmbedded( session ) ) {
-			return value;
-		}
-
 		if ( value != null && !isNull( owner, session ) ) {
 			if ( isReferenceToPrimaryKey() ) {
 				return resolveIdentifier( (Serializable) value, session );
 			}
 			else if ( uniqueKeyPropertyName != null ) {
 				return loadByUniqueKey( getAssociatedEntityName(), uniqueKeyPropertyName, value, session );
 			}
 		}
 
 		return null;
 	}
 
 	@Override
 	public Type getSemiResolvedType(SessionFactoryImplementor factory) {
 		return getAssociatedEntityPersister( factory ).getIdentifierType();
 	}
 
 	protected EntityPersister getAssociatedEntityPersister(final SessionFactoryImplementor factory) {
 		final EntityPersister persister = associatedEntityPersister;
 		//The following branch implements a simple lazy-initialization, but rather than the canonical
 		//form it returns the local variable to avoid a second volatile read: associatedEntityPersister
 		//needs to be volatile as the initialization might happen by a different thread than the readers.
 		if ( persister == null ) {
 			associatedEntityPersister = factory.getEntityPersister( getAssociatedEntityName() );
 			return associatedEntityPersister;
 		}
 		else {
 			return persister;
 		}
 	}
 
 	protected final Object getIdentifier(Object value, SessionImplementor session) throws HibernateException {
-		if ( isNotEmbedded( session ) ) {
-			return value;
-		}
-
 		if ( isReferenceToPrimaryKey() || uniqueKeyPropertyName == null ) {
 			return ForeignKeys.getEntityIdentifierIfNotUnsaved(
 					getAssociatedEntityName(),
 					value,
 					session
 			); //tolerates nulls
 		}
 		else if ( value == null ) {
 			return null;
 		}
 		else {
 			EntityPersister entityPersister = getAssociatedEntityPersister( session.getFactory() );
 			Object propertyValue = entityPersister.getPropertyValue( value, uniqueKeyPropertyName );
 			// We now have the value of the property-ref we reference.  However,
 			// we need to dig a little deeper, as that property might also be
 			// an entity type, in which case we need to resolve its identitifier
 			Type type = entityPersister.getPropertyType( uniqueKeyPropertyName );
 			if ( type.isEntityType() ) {
 				propertyValue = ( (EntityType) type ).getIdentifier( propertyValue, session );
 			}
 
 			return propertyValue;
 		}
 	}
 
 	/**
-	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	protected boolean isNotEmbedded(SessionImplementor session) {
-//		return !isEmbeddedInXML;
-		return false;
-	}
-
-	/**
 	 * Generate a loggable representation of an instance of the value mapped by this type.
 	 *
 	 * @param value The instance to be logged.
 	 * @param factory The session factory.
 	 *
 	 * @return The loggable string.
 	 *
 	 * @throws HibernateException Generally some form of resolution problem.
 	 */
 	@Override
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) {
 		if ( value == null ) {
 			return "null";
 		}
 
 		EntityPersister persister = getAssociatedEntityPersister( factory );
 		StringBuilder result = new StringBuilder().append( associatedEntityName );
 
 		if ( persister.hasIdentifierProperty() ) {
-			final EntityMode entityMode = persister.getEntityMode();
 			final Serializable id;
-			if ( entityMode == null ) {
-				if ( isEmbeddedInXML ) {
-					throw new ClassCastException( value.getClass().getName() );
-				}
-				id = (Serializable) value;
-			}
-			else if ( value instanceof HibernateProxy ) {
+			if ( value instanceof HibernateProxy ) {
 				HibernateProxy proxy = (HibernateProxy) value;
 				id = proxy.getHibernateLazyInitializer().getIdentifier();
 			}
 			else {
 				id = persister.getIdentifier( value );
 			}
 
 			result.append( '#' )
 					.append( persister.getIdentifierType().toLoggableString( id, factory ) );
 		}
 
 		return result.toString();
 	}
 
 	/**
 	 * Is the association modeled here defined as a 1-1 in the database (physical model)?
 	 *
 	 * @return True if a 1-1 in the database; false otherwise.
 	 */
 	public abstract boolean isOneToOne();
 
 	/**
 	 * Is the association modeled here a 1-1 according to the logical moidel?
 	 *
 	 * @return True if a 1-1 in the logical model; false otherwise.
 	 */
 	public boolean isLogicalOneToOne() {
 		return isOneToOne();
 	}
 
 	/**
 	 * Convenience method to locate the identifier type of the associated entity.
 	 *
 	 * @param factory The mappings...
 	 *
 	 * @return The identifier type
 	 */
 	Type getIdentifierType(final Mapping factory) {
 		final Type type = associatedIdentifierType;
 		//The following branch implements a simple lazy-initialization, but rather than the canonical
 		//form it returns the local variable to avoid a second volatile read: associatedIdentifierType
 		//needs to be volatile as the initialization might happen by a different thread than the readers.
 		if ( type == null ) {
 			associatedIdentifierType = factory.getIdentifierType( getAssociatedEntityName() );
 			return associatedIdentifierType;
 		}
 		else {
 			return type;
 		}
 	}
 
 	/**
 	 * Convenience method to locate the identifier type of the associated entity.
 	 *
 	 * @param session The originating session
 	 *
 	 * @return The identifier type
 	 */
 	Type getIdentifierType(final SessionImplementor session) {
 		final Type type = associatedIdentifierType;
 		if ( type == null ) {
 			associatedIdentifierType = getIdentifierType( session.getFactory() );
 			return associatedIdentifierType;
 		}
 		else {
 			return type;
 		}
 	}
 
 	/**
 	 * Determine the type of either (1) the identifier if we reference the
 	 * associated entity's PK or (2) the unique key to which we refer (i.e.
 	 * the property-ref).
 	 *
 	 * @param factory The mappings...
 	 *
 	 * @return The appropriate type.
 	 *
 	 * @throws MappingException Generally, if unable to resolve the associated entity name
 	 * or unique key property name.
 	 */
 	public final Type getIdentifierOrUniqueKeyType(Mapping factory) throws MappingException {
 		if ( isReferenceToPrimaryKey() || uniqueKeyPropertyName == null ) {
 			return getIdentifierType( factory );
 		}
 		else {
 			Type type = factory.getReferencedPropertyType( getAssociatedEntityName(), uniqueKeyPropertyName );
 			if ( type.isEntityType() ) {
 				type = ( (EntityType) type ).getIdentifierOrUniqueKeyType( factory );
 			}
 			return type;
 		}
 	}
 
 	/**
 	 * The name of the property on the associated entity to which our FK
 	 * refers
 	 *
 	 * @param factory The mappings...
 	 *
 	 * @return The appropriate property name.
 	 *
 	 * @throws MappingException Generally, if unable to resolve the associated entity name
 	 */
 	public final String getIdentifierOrUniqueKeyPropertyName(Mapping factory)
 			throws MappingException {
 		if ( isReferenceToPrimaryKey() || uniqueKeyPropertyName == null ) {
 			return factory.getIdentifierPropertyName( getAssociatedEntityName() );
 		}
 		else {
 			return uniqueKeyPropertyName;
 		}
 	}
 
 	protected abstract boolean isNullable();
 
 	/**
 	 * Resolve an identifier via a load.
 	 *
 	 * @param id The entity id to resolve
 	 * @param session The orginating session.
 	 *
 	 * @return The resolved identifier (i.e., loaded entity).
 	 *
 	 * @throws org.hibernate.HibernateException Indicates problems performing the load.
 	 */
 	protected final Object resolveIdentifier(Serializable id, SessionImplementor session) throws HibernateException {
 		boolean isProxyUnwrapEnabled = unwrapProxy &&
 				getAssociatedEntityPersister( session.getFactory() )
 						.isInstrumented();
 
 		Object proxyOrEntity = session.internalLoad(
 				getAssociatedEntityName(),
 				id,
 				eager,
 				isNullable() && !isProxyUnwrapEnabled
 		);
 
 		if ( proxyOrEntity instanceof HibernateProxy ) {
 			( (HibernateProxy) proxyOrEntity ).getHibernateLazyInitializer()
 					.setUnwrap( isProxyUnwrapEnabled );
 		}
 
 		return proxyOrEntity;
 	}
 
 	protected boolean isNull(Object owner, SessionImplementor session) {
 		return false;
 	}
 
 	/**
 	 * Load an instance by a unique key that is not the primary key.
 	 *
 	 * @param entityName The name of the entity to load
 	 * @param uniqueKeyPropertyName The name of the property defining the uniqie key.
 	 * @param key The unique key property value.
 	 * @param session The originating session.
 	 *
 	 * @return The loaded entity
 	 *
 	 * @throws HibernateException generally indicates problems performing the load.
 	 */
 	public Object loadByUniqueKey(
 			String entityName,
 			String uniqueKeyPropertyName,
 			Object key,
 			SessionImplementor session) throws HibernateException {
 		final SessionFactoryImplementor factory = session.getFactory();
 		UniqueKeyLoadable persister = (UniqueKeyLoadable) factory.getEntityPersister( entityName );
 
 		//TODO: implement caching?! proxies?!
 
 		EntityUniqueKey euk = new EntityUniqueKey(
 				entityName,
 				uniqueKeyPropertyName,
 				key,
 				getIdentifierOrUniqueKeyType( factory ),
 				persister.getEntityMode(),
 				session.getFactory()
 		);
 
 		final PersistenceContext persistenceContext = session.getPersistenceContext();
 		Object result = persistenceContext.getEntity( euk );
 		if ( result == null ) {
 			result = persister.loadByUniqueKey( uniqueKeyPropertyName, key, session );
 		}
 		return result == null ? null : persistenceContext.proxyFor( result );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java b/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
index 56f5befce7..7a8d8b3ba8 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/IdentifierBagType.java
@@ -1,59 +1,44 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 
 import org.hibernate.HibernateException;
 import org.hibernate.collection.internal.PersistentIdentifierBag;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class IdentifierBagType extends CollectionType {
 
-	/**
-	 * @deprecated Use {@link #IdentifierBagType(org.hibernate.type.TypeFactory.TypeScope, String, String)}
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public IdentifierBagType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
-		super( typeScope, role, propertyRef, isEmbeddedInXML );
-	}
-
 	public IdentifierBagType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
 		super( typeScope, role, propertyRef );
 	}
 
 	public PersistentCollection instantiate(
 		SessionImplementor session,
 		CollectionPersister persister, Serializable key)
 		throws HibernateException {
 
 		return new PersistentIdentifierBag(session);
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0 ? new ArrayList() : new ArrayList( anticipatedSize + 1 );
 	}
 	
 	public Class getReturnedClass() {
 		return java.util.Collection.class;
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentIdentifierBag( session, (java.util.Collection) collection );
 	}
 
 }
-
-
-
-
-
-
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ListType.java b/hibernate-core/src/main/java/org/hibernate/type/ListType.java
index 8f3aaa9aa1..2503a1daaa 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ListType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ListType.java
@@ -1,61 +1,52 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.collection.internal.PersistentList;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class ListType extends CollectionType {
 
-	/**
-	 * @deprecated Use {@link #ListType(org.hibernate.type.TypeFactory.TypeScope, String, String)}
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public ListType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
-		super( typeScope, role, propertyRef, isEmbeddedInXML );
-	}
-
 	public ListType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
 		super( typeScope, role, propertyRef );
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		return new PersistentList(session);
 	}
 
 	public Class getReturnedClass() {
 		return List.class;
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentList( session, (List) collection );
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0 ? new ArrayList() : new ArrayList( anticipatedSize + 1 );
 	}
 	
 	public Object indexOf(Object collection, Object element) {
 		List list = (List) collection;
 		for ( int i=0; i<list.size(); i++ ) {
 			//TODO: proxies!
 			if ( list.get(i)==element ) {
 				return i;
 			}
 		}
 		return null;
 	}
 	
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
index 952adb773f..9c1ca0dc79 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
@@ -1,328 +1,304 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Arrays;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * A many-to-one association to an entity.
  *
  * @author Gavin King
  */
 public class ManyToOneType extends EntityType {
 	private final boolean ignoreNotFound;
 	private boolean isLogicalOneToOne;
 
 	/**
 	 * Creates a many-to-one association type with the given referenced entity.
 	 *
 	 * @param scope The scope for this instance.
 	 * @param referencedEntityName The name iof the referenced entity
 	 */
 	public ManyToOneType(TypeFactory.TypeScope scope, String referencedEntityName) {
 		this( scope, referencedEntityName, false );
 	}
 
 	/**
 	 * Creates a many-to-one association type with the given referenced entity and the
 	 * given laziness characteristic
 	 *
 	 * @param scope The scope for this instance.
 	 * @param referencedEntityName The name iof the referenced entity
 	 * @param lazy Should the association be handled lazily
 	 */
 	public ManyToOneType(TypeFactory.TypeScope scope, String referencedEntityName, boolean lazy) {
 		this( scope, referencedEntityName, true, null, lazy, true, false, false );
 	}
 
 
 	/**
 	 * @deprecated Use {@link #ManyToOneType(TypeFactory.TypeScope, String, boolean, String, boolean, boolean, boolean, boolean ) } instead.
 	 */
 	@Deprecated
 	public ManyToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			boolean ignoreNotFound,
 			boolean isLogicalOneToOne) {
 		this( scope, referencedEntityName, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, ignoreNotFound, isLogicalOneToOne );
 	}
 
-	/**
-	 * @deprecated Use {@link #ManyToOneType(TypeFactory.TypeScope, String, boolean, String, boolean, boolean, boolean, boolean ) } instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public ManyToOneType(
-			TypeFactory.TypeScope scope,
-			String referencedEntityName,
-			String uniqueKeyPropertyName,
-			boolean lazy,
-			boolean unwrapProxy,
-			boolean ignoreNotFound,
-			boolean isLogicalOneToOne) {
-		this( scope, referencedEntityName, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, ignoreNotFound, isLogicalOneToOne );
-	}
-
 	public ManyToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			boolean referenceToPrimaryKey,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean ignoreNotFound,
 			boolean isLogicalOneToOne) {
 		super( scope, referencedEntityName, referenceToPrimaryKey, uniqueKeyPropertyName, !lazy, unwrapProxy );
 		this.ignoreNotFound = ignoreNotFound;
 		this.isLogicalOneToOne = isLogicalOneToOne;
 	}
 
 	protected boolean isNullable() {
 		return ignoreNotFound;
 	}
 
 	public boolean isAlwaysDirtyChecked() {
 		// always need to dirty-check, even when non-updateable;
 		// this ensures that when the association is updated,
 		// the entity containing this association will be updated
 		// in the cache
 		return true;
 	}
 
 	public boolean isOneToOne() {
 		return false;
 	}
 
 	public boolean isLogicalOneToOne() {
 		return isLogicalOneToOne;
 	}
 
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		return requireIdentifierOrUniqueKeyType( mapping ).getColumnSpan( mapping );
 	}
 
 	private Type requireIdentifierOrUniqueKeyType(Mapping mapping) {
 		final Type fkTargetType = getIdentifierOrUniqueKeyType( mapping );
 		if ( fkTargetType == null ) {
 			throw new MappingException(
 					"Unable to determine FK target Type for many-to-one mapping: " +
 							"referenced-entity-name=[" + getAssociatedEntityName() +
 							"], referenced-entity-attribute-name=[" + getLHSPropertyName() + "]"
 			);
 		}
 		return fkTargetType;
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return requireIdentifierOrUniqueKeyType( mapping ).sqlTypes( mapping );
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return requireIdentifierOrUniqueKeyType( mapping ).dictatedSizes( mapping );
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return requireIdentifierOrUniqueKeyType( mapping ).defaultSizes( mapping );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		requireIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeSet( st, getIdentifier( value, session ), index, settable, session );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 		requireIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeSet( st, getIdentifier( value, session ), index, session );
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FROM_PARENT;
 	}
 
 	public Object hydrate(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		// return the (fully resolved) identifier value, but do not resolve
 		// to the actual referenced entity instance
 		// NOTE: the owner of the association is not really the owner of the id!
 		final Serializable id = (Serializable) getIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeGet( rs, names, session, null );
 		scheduleBatchLoadIfNeeded( id, session );
 		return id;
 	}
 
 	/**
 	 * Register the entity as batch loadable, if enabled
 	 */
 	@SuppressWarnings({ "JavaDoc" })
 	private void scheduleBatchLoadIfNeeded(Serializable id, SessionImplementor session) throws MappingException {
 		//cannot batch fetch by unique key (property-ref associations)
 		if ( uniqueKeyPropertyName == null && id != null ) {
 			final EntityPersister persister = getAssociatedEntityPersister( session.getFactory() );
 			if ( persister.isBatchLoadable() ) {
 				final EntityKey entityKey = session.generateEntityKey( id, persister );
 				if ( !session.getPersistenceContext().containsEntity( entityKey ) ) {
 					session.getPersistenceContext().getBatchFetchQueue().addBatchLoadableEntityKey( entityKey );
 				}
 			}
 		}
 	}
 
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 
 	public boolean isModified(
 			Object old,
 			Object current,
 			boolean[] checkable,
 			SessionImplementor session) throws HibernateException {
 		if ( current == null ) {
 			return old!=null;
 		}
 		if ( old == null ) {
 			// we already know current is not null...
 			return true;
 		}
 		// the ids are fully resolved, so compare them with isDirty(), not isModified()
 		return getIdentifierOrUniqueKeyType( session.getFactory() )
 				.isDirty( old, getIdentifier( current, session ), session );
 	}
 
 	public Serializable disassemble(
 			Object value,
 			SessionImplementor session,
 			Object owner) throws HibernateException {
 
-		if ( isNotEmbedded( session ) ) {
-			return getIdentifierType( session ).disassemble( value, session, owner );
-		}
-		
 		if ( value == null ) {
 			return null;
 		}
 		else {
 			// cache the actual id of the object, not the value of the
 			// property-ref, which might not be initialized
 			Object id = ForeignKeys.getEntityIdentifierIfNotUnsaved(
 					getAssociatedEntityName(),
 					value,
 					session
 			);
 			if ( id == null ) {
 				throw new AssertionFailure(
 						"cannot cache a reference to an object with a null id: " + 
 						getAssociatedEntityName()
 				);
 			}
 			return getIdentifierType( session ).disassemble( id, session, owner );
 		}
 	}
 
 	public Object assemble(
 			Serializable oid,
 			SessionImplementor session,
 			Object owner) throws HibernateException {
 		
 		//TODO: currently broken for unique-key references (does not detect
 		//      change to unique key property of the associated object)
 		
 		Serializable id = assembleId( oid, session );
 
-		if ( isNotEmbedded( session ) ) {
-			return id;
-		}
-		
 		if ( id == null ) {
 			return null;
 		}
 		else {
 			return resolveIdentifier( id, session );
 		}
 	}
 
 	private Serializable assembleId(Serializable oid, SessionImplementor session) {
 		//the owner of the association is not the owner of the id
 		return ( Serializable ) getIdentifierType( session ).assemble( oid, session, null );
 	}
 
 	public void beforeAssemble(Serializable oid, SessionImplementor session) {
 		scheduleBatchLoadIfNeeded( assembleId( oid, session ), session );
 	}
 	
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		boolean[] result = new boolean[ getColumnSpan( mapping ) ];
 		if ( value != null ) {
 			Arrays.fill( result, true );
 		}
 		return result;
 	}
 	
 	public boolean isDirty(
 			Object old,
 			Object current,
 			SessionImplementor session) throws HibernateException {
 		if ( isSame( old, current ) ) {
 			return false;
 		}
 		Object oldid = getIdentifier( old, session );
 		Object newid = getIdentifier( current, session );
 		return getIdentifierType( session ).isDirty( oldid, newid, session );
 	}
 
 	public boolean isDirty(
 			Object old,
 			Object current,
 			boolean[] checkable,
 			SessionImplementor session) throws HibernateException {
 		if ( isAlwaysDirtyChecked() ) {
 			return isDirty( old, current, session );
 		}
 		else {
 			if ( isSame( old, current ) ) {
 				return false;
 			}
 			Object oldid = getIdentifier( old, session );
 			Object newid = getIdentifier( current, session );
 			return getIdentifierType( session ).isDirty( oldid, newid, checkable, session );
 		}
 		
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/MapType.java b/hibernate-core/src/main/java/org/hibernate/type/MapType.java
index 988b54589e..9ff55c9daf 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/MapType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/MapType.java
@@ -1,95 +1,86 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.collection.internal.PersistentMap;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 
 public class MapType extends CollectionType {
 
-	/**
-	 * @deprecated Use {@link #MapType(TypeFactory.TypeScope, String, String) } instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public MapType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
-		super( typeScope, role, propertyRef, isEmbeddedInXML );
-	}
-
 	public MapType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
 		super( typeScope, role, propertyRef );
 	}
 
 	public PersistentCollection instantiate(
 			SessionImplementor session,
 			CollectionPersister persister,
 			Serializable key) {
 		return new PersistentMap( session );
 	}
 
 	public Class getReturnedClass() {
 		return Map.class;
 	}
 
 	public Iterator getElementsIterator(Object collection) {
 		return ( (java.util.Map) collection ).values().iterator();
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentMap( session, (java.util.Map) collection );
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0
 				? new HashMap()
 				: new HashMap( anticipatedSize + (int) ( anticipatedSize * .75f ), .75f );
 	}
 
 	public Object replaceElements(
 			final Object original,
 			final Object target,
 			final Object owner,
 			final java.util.Map copyCache,
 			final SessionImplementor session) throws HibernateException {
 		CollectionPersister cp = session.getFactory().getCollectionPersister( getRole() );
 
 		java.util.Map result = (java.util.Map) target;
 		result.clear();
 
 		for ( Object o : ( (Map) original ).entrySet() ) {
 			Map.Entry me = (Map.Entry) o;
 			Object key = cp.getIndexType().replace( me.getKey(), null, session, owner, copyCache );
 			Object value = cp.getElementType().replace( me.getValue(), null, session, owner, copyCache );
 			result.put( key, value );
 		}
 
 		return result;
 
 	}
 
 	public Object indexOf(Object collection, Object element) {
 		Iterator iter = ( (Map) collection ).entrySet().iterator();
 		while ( iter.hasNext() ) {
 			Map.Entry me = (Map.Entry) iter.next();
 			//TODO: proxies!
 			if ( me.getValue() == element ) {
 				return me.getKey();
 			}
 		}
 		return null;
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/MetaType.java b/hibernate-core/src/main/java/org/hibernate/type/MetaType.java
index 2704e3e9f8..44bcff2896 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/MetaType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/MetaType.java
@@ -1,161 +1,153 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.jdbc.Size;
 
 import org.dom4j.Node;
 
 /**
  * @author Gavin King
  */
 public class MetaType extends AbstractType {
 	public static final String[] REGISTRATION_KEYS = new String[0];
 
 	private final Type baseType;
 	private final Map<Object,String> discriminatorValuesToEntityNameMap;
 	private final Map<String,Object> entityNameToDiscriminatorValueMap;
 
 	public MetaType(Map<Object,String> discriminatorValuesToEntityNameMap, Type baseType) {
 		this.baseType = baseType;
 		this.discriminatorValuesToEntityNameMap = discriminatorValuesToEntityNameMap;
 		this.entityNameToDiscriminatorValueMap = new HashMap<String,Object>();
 		for ( Map.Entry<Object,String> entry : discriminatorValuesToEntityNameMap.entrySet() ) {
 			entityNameToDiscriminatorValueMap.put( entry.getValue(), entry.getKey() );
 		}
 	}
 
 	public String[] getRegistrationKeys() {
 		return REGISTRATION_KEYS;
 	}
 
 	public Map<Object, String> getDiscriminatorValuesToEntityNameMap() {
 		return discriminatorValuesToEntityNameMap;
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return baseType.sqlTypes(mapping);
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return baseType.dictatedSizes( mapping );
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return baseType.defaultSizes( mapping );
 	}
 
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		return baseType.getColumnSpan(mapping);
 	}
 
 	public Class getReturnedClass() {
 		return String.class;
 	}
 
 	public Object nullSafeGet(
 		ResultSet rs,
 		String[] names,
 		SessionImplementor session,
 		Object owner)
 	throws HibernateException, SQLException {
 		Object key = baseType.nullSafeGet(rs, names, session, owner);
 		return key==null ? null : discriminatorValuesToEntityNameMap.get(key);
 	}
 
 	public Object nullSafeGet(
 		ResultSet rs,
 		String name,
 		SessionImplementor session,
 		Object owner)
 	throws HibernateException, SQLException {
 		Object key = baseType.nullSafeGet(rs, name, session, owner);
 		return key==null ? null : discriminatorValuesToEntityNameMap.get(key);
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
 	throws HibernateException, SQLException {
 		baseType.nullSafeSet(st, value==null ? null : entityNameToDiscriminatorValueMap.get(value), index, session);
 	}
 	
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			boolean[] settable, 
 			SessionImplementor session) throws HibernateException, SQLException {
 		if ( settable[0] ) {
 			nullSafeSet(st, value, index, session);
 		}
 	}
 
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) throws HibernateException {
 		return toXMLString(value, factory);
 	}
 	
 	public String toXMLString(Object value, SessionFactoryImplementor factory)
 		throws HibernateException {
 		return (String) value; //value is the entity name
 	}
 
 	public Object fromXMLString(String xml, Mapping factory)
 		throws HibernateException {
 		return xml; //xml is the entity name
 	}
 
 	public String getName() {
 		return baseType.getName(); //TODO!
 	}
 
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 	throws HibernateException {
 		return value;
 	}
 
 	public Object replace(
 			Object original, 
 			Object target,
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache) {
 		return original;
 	}
 	
 	public boolean isMutable() {
 		return false;
 	}
 
-	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
-		return fromXMLString( xml.getText(), factory );
-	}
-
-	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
-		node.setText( toXMLString(value, factory) );
-	}
-
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		throw new UnsupportedOperationException();
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) throws HibernateException {
 		return checkable[0] && isDirty(old, current, session);
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
index ad57733105..07a95b9557 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
@@ -1,195 +1,176 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.jdbc.Size;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * A one-to-one association to an entity
  * @author Gavin King
  */
 public class OneToOneType extends EntityType {
 
 	private final ForeignKeyDirection foreignKeyType;
 	private final String propertyName;
 	private final String entityName;
 
 	/**
 	 * @deprecated Use {@link #OneToOneType(TypeFactory.TypeScope, String, ForeignKeyDirection, boolean, String, boolean, boolean, String, String)}
 	 *  instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public OneToOneType(
-			TypeFactory.TypeScope scope,
-			String referencedEntityName,
-			ForeignKeyDirection foreignKeyType,
-			String uniqueKeyPropertyName,
-			boolean lazy,
-			boolean unwrapProxy,
-			boolean isEmbeddedInXML,
-			String entityName,
-			String propertyName) {
-		this( scope, referencedEntityName, foreignKeyType, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName );
-	}
-
-	/**
-	 * @deprecated Use {@link #OneToOneType(TypeFactory.TypeScope, String, ForeignKeyDirection, boolean, String, boolean, boolean, String, String)}
-	 *  instead.
 	 */
 	@Deprecated
 	public OneToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			ForeignKeyDirection foreignKeyType,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			String entityName,
 			String propertyName) {
 		this( scope, referencedEntityName, foreignKeyType, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName );
 	}
 
 	public OneToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			ForeignKeyDirection foreignKeyType,
 			boolean referenceToPrimaryKey,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			String entityName,
 			String propertyName) {
 		super( scope, referencedEntityName, referenceToPrimaryKey, uniqueKeyPropertyName, !lazy, unwrapProxy );
 		this.foreignKeyType = foreignKeyType;
 		this.propertyName = propertyName;
 		this.entityName = entityName;
 	}
 
 	public String getPropertyName() {
 		return propertyName;
 	}
 	
 	public boolean isNull(Object owner, SessionImplementor session) {
 		if ( propertyName != null ) {
 			final EntityPersister ownerPersister = session.getFactory().getEntityPersister( entityName );
 			final Serializable id = session.getContextEntityIdentifier( owner );
 			final EntityKey entityKey = session.generateEntityKey( id, ownerPersister );
 			return session.getPersistenceContext().isPropertyNull( entityKey, getPropertyName() );
 		}
 		else {
 			return false;
 		}
 	}
 
 	public int getColumnSpan(Mapping session) throws MappingException {
 		return 0;
 	}
 
 	public int[] sqlTypes(Mapping session) throws MappingException {
 		return ArrayHelper.EMPTY_INT_ARRAY;
 	}
 
 	private static final Size[] SIZES = new Size[0];
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return SIZES;
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return SIZES;
 	}
 
 	public boolean[] toColumnNullness(Object value, Mapping mapping) {
 		return ArrayHelper.EMPTY_BOOLEAN_ARRAY;
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session) {
 		//nothing to do
 	}
 
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) {
 		//nothing to do
 	}
 
 	public boolean isOneToOne() {
 		return true;
 	}
 
 	public boolean isDirty(Object old, Object current, SessionImplementor session) {
 		return false;
 	}
 
 	public boolean isDirty(Object old, Object current, boolean[] checkable, SessionImplementor session) {
 		return false;
 	}
 
 	public boolean isModified(Object old, Object current, boolean[] checkable, SessionImplementor session) {
 		return false;
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return foreignKeyType;
 	}
 
 	public Object hydrate(
 		ResultSet rs,
 		String[] names,
 		SessionImplementor session,
 		Object owner)
 	throws HibernateException, SQLException {
 
 		return session.getContextEntityIdentifier(owner);
 	}
 
 	protected boolean isNullable() {
 		return foreignKeyType==ForeignKeyDirection.TO_PARENT;
 	}
 
 	public boolean useLHSPrimaryKey() {
 		return true;
 	}
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 		return null;
 	}
 
 	public Object assemble(Serializable oid, SessionImplementor session, Object owner)
 	throws HibernateException {
 		//this should be a call to resolve(), not resolveIdentifier(), 
 		//'cos it might be a property-ref, and we did not cache the
 		//referenced value
 		return resolve( session.getContextEntityIdentifier(owner), session, owner );
 	}
 	
 	/**
 	 * We don't need to dirty check one-to-one because of how 
 	 * assemble/disassemble is implemented and because a one-to-one 
 	 * association is never dirty
 	 */
 	public boolean isAlwaysDirtyChecked() {
 		//TODO: this is kinda inconsistent with CollectionType
 		return false; 
 	}
 	
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java b/hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java
index 9e0f0378a4..49a82630d9 100755
--- a/hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/OrderedMapType.java
@@ -1,43 +1,26 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 import java.util.LinkedHashMap;
 
 /**
  * A specialization of the map type, with (resultset-based) ordering.
  */
 public class OrderedMapType extends MapType {
 
-	/**
-	 * Constructs a map type capable of creating ordered maps of the given
-	 * role.
-	 *
-	 * @param role The collection role name.
-	 * @param propertyRef The property ref name.
-	 * @param isEmbeddedInXML Is this collection to embed itself in xml
-	 *
-	 * @deprecated Use {@link #OrderedMapType(TypeFactory.TypeScope, String, String)} instead.
-	 *  instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public OrderedMapType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
-		super( typeScope, role, propertyRef, isEmbeddedInXML );
-	}
-
 	public OrderedMapType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
 		super( typeScope, role, propertyRef );
 	}
 
 	@Override
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize > 0
 				? new LinkedHashMap( anticipatedSize )
 				: new LinkedHashMap();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java b/hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java
index 7bdb6837ff..e0947beae6 100755
--- a/hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/OrderedSetType.java
@@ -1,44 +1,26 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 import java.util.LinkedHashSet;
 
 /**
  * A specialization of the set type, with (resultset-based) ordering.
  */
 public class OrderedSetType extends SetType {
 
-	/**
-	 * Constructs a set type capable of creating ordered sets of the given
-	 * role.
-	 *
-	 * @param typeScope The scope for this type instance.
-	 * @param role The collection role name.
-	 * @param propertyRef The property ref name.
-	 * @param isEmbeddedInXML Is this collection to embed itself in xml
-	 *
-	 * @deprecated Use {@link #OrderedSetType(org.hibernate.type.TypeFactory.TypeScope, String, String)}
-	 * instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public OrderedSetType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
-		super( typeScope, role, propertyRef, isEmbeddedInXML );
-	}
-
 	public OrderedSetType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
 		super( typeScope, role, propertyRef );
 	}
 
 	@Override
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize > 0
 				? new LinkedHashSet( anticipatedSize )
 				: new LinkedHashSet();
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/SetType.java b/hibernate-core/src/main/java/org/hibernate/type/SetType.java
index e0b68fc10a..57b2c807c9 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/SetType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SetType.java
@@ -1,50 +1,41 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.HashSet;
 
 import org.hibernate.collection.internal.PersistentSet;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class SetType extends CollectionType {
 
-	/**
-	 * @deprecated Use {@link #SetType(org.hibernate.type.TypeFactory.TypeScope, String, String)} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public SetType(TypeFactory.TypeScope typeScope, String role, String propertyRef, boolean isEmbeddedInXML) {
-		super( typeScope, role, propertyRef, isEmbeddedInXML );
-	}
-
 	public SetType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
 		super( typeScope, role, propertyRef );
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		return new PersistentSet(session);
 	}
 
 	public Class getReturnedClass() {
 		return java.util.Set.class;
 	}
 
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentSet( session, (java.util.Set) collection );
 	}
 
 	public Object instantiate(int anticipatedSize) {
 		return anticipatedSize <= 0
 				? new HashSet()
 				: new HashSet( anticipatedSize + (int)( anticipatedSize * .75f ), .75f );
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java b/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
index b8c47aabec..ee1bf1ba1b 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SortedMapType.java
@@ -1,64 +1,53 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.TreeMap;
 
 import org.hibernate.collection.internal.PersistentSortedMap;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 
 public class SortedMapType extends MapType {
 
 	private final Comparator comparator;
 
-	/**
-	 * @deprecated Use {@link #SortedMapType(org.hibernate.type.TypeFactory.TypeScope, String, String, java.util.Comparator)}
-	 * instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public SortedMapType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Comparator comparator, boolean isEmbeddedInXML) {
-		super( typeScope, role, propertyRef, isEmbeddedInXML );
-		this.comparator = comparator;
-	}
-
 	public SortedMapType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Comparator comparator) {
 		super( typeScope, role, propertyRef );
 		this.comparator = comparator;
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		PersistentSortedMap map = new PersistentSortedMap(session);
 		map.setComparator(comparator);
 		return map;
 	}
 
 	public Class getReturnedClass() {
 		return java.util.SortedMap.class;
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public Object instantiate(int anticipatedSize) {
 		return new TreeMap(comparator);
 	}
 	
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentSortedMap( session, (java.util.SortedMap) collection );
 	}
 
 }
 
 
 
 
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java b/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
index 7208932f10..91171fc273 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SortedSetType.java
@@ -1,55 +1,44 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.TreeSet;
 
 import org.hibernate.collection.internal.PersistentSortedSet;
 import org.hibernate.collection.spi.PersistentCollection;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 
 public class SortedSetType extends SetType {
 	private final Comparator comparator;
 
-	/**
-	 * @deprecated Use {@link #SortedSetType(org.hibernate.type.TypeFactory.TypeScope, String, String, java.util.Comparator)}
-	 * instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public SortedSetType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Comparator comparator, boolean isEmbeddedInXML) {
-		super( typeScope, role, propertyRef, isEmbeddedInXML );
-		this.comparator = comparator;
-	}
-
 	public SortedSetType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Comparator comparator) {
 		super( typeScope, role, propertyRef );
 		this.comparator = comparator;
 	}
 
 	public PersistentCollection instantiate(SessionImplementor session, CollectionPersister persister, Serializable key) {
 		PersistentSortedSet set = new PersistentSortedSet(session);
 		set.setComparator(comparator);
 		return set;
 	}
 
 	public Class getReturnedClass() {
 		return java.util.SortedSet.class;
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public Object instantiate(int anticipatedSize) {
 		return new TreeSet(comparator);
 	}
 	
 	public PersistentCollection wrap(SessionImplementor session, Object collection) {
 		return new PersistentSortedSet( session, (java.util.SortedSet) collection );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
index 05a7735b6e..d54412d354 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
@@ -1,142 +1,134 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.internal.ForeignKeys;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.jdbc.Size;
 
 /**
  * A one-to-one association that maps to specific formula(s)
  * instead of the primary key column of the owning entity.
  * 
  * @author Gavin King
  */
 public class SpecialOneToOneType extends OneToOneType {
 	
 	/**
 	 * @deprecated Use {@link #SpecialOneToOneType(org.hibernate.type.TypeFactory.TypeScope, String, ForeignKeyDirection, boolean, String, boolean, boolean, String, String)} instead.
 	 */
 	@Deprecated
 	public SpecialOneToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			ForeignKeyDirection foreignKeyType, 
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			String entityName,
 			String propertyName) {
 		this( scope, referencedEntityName, foreignKeyType, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName );
 	}
 	
 	public SpecialOneToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			ForeignKeyDirection foreignKeyType,
 			boolean referenceToPrimaryKey, 
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			String entityName,
 			String propertyName) {
 		super(
 				scope,
 				referencedEntityName, 
 				foreignKeyType,
 				referenceToPrimaryKey, 
 				uniqueKeyPropertyName, 
 				lazy,
 				unwrapProxy,
 				entityName, 
 				propertyName
 			);
 	}
 	
 	public int getColumnSpan(Mapping mapping) throws MappingException {
 		return super.getIdentifierOrUniqueKeyType( mapping ).getColumnSpan( mapping );
 	}
 	
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return super.getIdentifierOrUniqueKeyType( mapping ).sqlTypes( mapping );
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return super.getIdentifierOrUniqueKeyType( mapping ).dictatedSizes( mapping );
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return super.getIdentifierOrUniqueKeyType( mapping ).defaultSizes( mapping );
 	}
 
 	public boolean useLHSPrimaryKey() {
 		return false;
 	}
 	
 	public Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException {
 		return super.getIdentifierOrUniqueKeyType( session.getFactory() )
 			.nullSafeGet(rs, names, session, owner);
 	}
 	
 	// TODO: copy/paste from ManyToOneType
 
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner)
 	throws HibernateException {
 
-		if ( isNotEmbedded(session) ) {
-			return getIdentifierType(session).disassemble(value, session, owner);
-		}
-		
 		if (value==null) {
 			return null;
 		}
 		else {
 			// cache the actual id of the object, not the value of the
 			// property-ref, which might not be initialized
 			Object id = ForeignKeys.getEntityIdentifierIfNotUnsaved( getAssociatedEntityName(), value, session );
 			if (id==null) {
 				throw new AssertionFailure(
 						"cannot cache a reference to an object with a null id: " + 
 						getAssociatedEntityName() 
 				);
 			}
 			return getIdentifierType(session).disassemble(id, session, owner);
 		}
 	}
 
 	public Object assemble(Serializable oid, SessionImplementor session, Object owner)
 	throws HibernateException {
 		//TODO: currently broken for unique-key references (does not detect
 		//      change to unique key property of the associated object)
 		Serializable id = (Serializable) getIdentifierType(session).assemble(oid, session, null); //the owner of the association is not the owner of the id
 
-		if ( isNotEmbedded(session) ) {
-			return id;
-		}
-		
 		if (id==null) {
 			return null;
 		}
 		else {
 			return resolveIdentifier(id, session);
 		}
 	}
 	
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/Type.java b/hibernate-core/src/main/java/org/hibernate/type/Type.java
index dad2b696ee..ab498ca8d8 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/Type.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/Type.java
@@ -1,601 +1,561 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.Map;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.jdbc.Size;
 
 import org.dom4j.Node;
 
 /**
  * Defines a mapping between a Java type and one or more JDBC {@linkplain java.sql.Types types}, as well
  * as describing the in-memory semantics of the given java type (how do we check it for 'dirtiness', how do
  * we copy values, etc).
  * <p/>
  * Application developers needing custom types can implement this interface (either directly or via subclassing an
  * existing impl) or by the (slightly more stable, though more limited) {@link org.hibernate.usertype.UserType}
  * interface.
  * <p/>
  * Implementations of this interface must certainly be thread-safe.  It is recommended that they be immutable as
  * well, though that is difficult to achieve completely given the no-arg constructor requirement for custom types.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public interface Type extends Serializable {
 	/**
 	 * Return true if the implementation is castable to {@link AssociationType}. This does not necessarily imply that
 	 * the type actually represents an association.  Essentially a polymorphic version of
 	 * {@code (type instanceof AssociationType.class)}
 	 *
 	 * @return True if this type is also an {@link AssociationType} implementor; false otherwise.
 	 */
 	public boolean isAssociationType();
 
 	/**
 	 * Return true if the implementation is castable to {@link CollectionType}. Essentially a polymorphic version of
 	 * {@code (type instanceof CollectionType.class)}
 	 * <p/>
 	 * A {@link CollectionType} is additionally an {@link AssociationType}; so if this method returns true,
 	 * {@link #isAssociationType()} should also return true.
 	 *
 	 * @return True if this type is also an {@link CollectionType} implementor; false otherwise.
 	 */
 	public boolean isCollectionType();
 
 	/**
 	 * Return true if the implementation is castable to {@link EntityType}. Essentially a polymorphic
 	 * version of {@code (type instanceof EntityType.class)}.
 	 * <p/>
 	 * An {@link EntityType} is additionally an {@link AssociationType}; so if this method returns true,
 	 * {@link #isAssociationType()} should also return true.
 	 *
 	 * @return True if this type is also an {@link EntityType} implementor; false otherwise.
 	 */
 	public boolean isEntityType();
 
 	/**
 	 * Return true if the implementation is castable to {@link AnyType}. Essentially a polymorphic
 	 * version of {@code (type instanceof AnyType.class)}.
 	 * <p/>
 	 * An {@link AnyType} is additionally an {@link AssociationType}; so if this method returns true,
 	 * {@link #isAssociationType()} should also return true.
 	 *
 	 * @return True if this type is also an {@link AnyType} implementor; false otherwise.
 	 */
 	public boolean isAnyType();
 
 	/**
 	 * Return true if the implementation is castable to {@link CompositeType}. Essentially a polymorphic
 	 * version of {@code (type instanceof CompositeType.class)}.  A component type may own collections or
 	 * associations and hence must provide certain extra functionality.
 	 *
 	 * @return True if this type is also an {@link CompositeType} implementor; false otherwise.
 	 */
 	public boolean isComponentType();
 
 	/**
 	 * How many columns are used to persist this type.  Always the same as {@code sqlTypes(mapping).length}
 	 *
 	 * @param mapping The mapping object :/
 	 *
 	 * @return The number of columns
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
 	public int getColumnSpan(Mapping mapping) throws MappingException;
 
 	/**
 	 * Return the JDBC types codes (per {@link java.sql.Types}) for the columns mapped by this type.
 	 * <p/>
 	 * NOTE: The number of elements in this array matches the return from {@link #getColumnSpan}.
 	 *
 	 * @param mapping The mapping object :/
 	 *
 	 * @return The JDBC type codes.
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
 	public int[] sqlTypes(Mapping mapping) throws MappingException;
 
 	/**
 	 * Return the column sizes dictated by this type.  For example, the mapping for a {@code char}/{@link Character} would
 	 * have a dictated length limit of 1; for a string-based {@link java.util.UUID} would have a size limit of 36; etc.
 	 * <p/>
 	 * NOTE: The number of elements in this array matches the return from {@link #getColumnSpan}.
 	 *
 	 * @param mapping The mapping object :/
 	 * @todo Would be much much better to have this aware of Dialect once the service/metamodel split is done
 	 *
 	 * @return The dictated sizes.
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException;
 
 	/**
 	 * Defines the column sizes to use according to this type if the user did not explicitly say (and if no
 	 * {@link #dictatedSizes} were given).
 	 * <p/>
 	 * NOTE: The number of elements in this array matches the return from {@link #getColumnSpan}.
 	 *
 	 * @param mapping The mapping object :/
 	 * @todo Would be much much better to have this aware of Dialect once the service/metamodel split is done
 	 *
 	 * @return The default sizes.
 	 *
 	 * @throws MappingException Generally indicates an issue accessing the passed mapping object.
 	 */
 	public Size[] defaultSizes(Mapping mapping) throws MappingException;
 
 	/**
 	 * The class returned by {@link #nullSafeGet} methods. This is used to  establish the class of an array of
 	 * this type.
 	 *
 	 * @return The java type class handled by this type.
 	 */
 	public Class getReturnedClass();
 
 	/**
-	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@SuppressWarnings( {"UnusedDeclaration"})
-	@Deprecated
-	public boolean isXMLElement();
-
-	/**
 	 * Compare two instances of the class mapped by this type for persistence "equality" (equality of persistent
 	 * state) taking a shortcut for entity references.
 	 * <p/>
 	 * For most types this should equate to an {@link Object#equals equals} check on the values.  For associations
 	 * the implication is a bit different.  For most types it is conceivable to simply delegate to {@link #isEqual}
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 *
 	 * @return True if there are considered the same (see discussion above).
 	 *
 	 * @throws HibernateException A problem occurred performing the comparison
 	 */
 	public boolean isSame(Object x, Object y) throws HibernateException;
 
 	/**
 	 * Compare two instances of the class mapped by this type for persistence "equality" (equality of persistent
 	 * state).
 	 * <p/>
 	 * This should always equate to some form of comparison of the value's internal state.  As an example, for
 	 * something like a date the comparison should be based on its internal "time" state based on the specific portion
 	 * it is meant to represent (timestamp, date, time).
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 *
 	 * @return True if there are considered equal (see discussion above).
 	 *
 	 * @throws HibernateException A problem occurred performing the comparison
 	 */
 	public boolean isEqual(Object x, Object y) throws HibernateException;
 
 	/**
 	 * Compare two instances of the class mapped by this type for persistence "equality" (equality of persistent
 	 * state).
 	 * <p/>
 	 * This should always equate to some form of comparison of the value's internal state.  As an example, for
 	 * something like a date the comparison should be based on its internal "time" state based on the specific portion
 	 * it is meant to represent (timestamp, date, time).
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 * @param factory The session factory
 	 *
 	 * @return True if there are considered equal (see discussion above).
 	 *
 	 * @throws HibernateException A problem occurred performing the comparison
 	 */
 	public boolean isEqual(Object x, Object y, SessionFactoryImplementor factory) throws HibernateException;
 
 	/**
 	 * Get a hash code, consistent with persistence "equality".  Again for most types the normal usage is to
 	 * delegate to the value's {@link Object#hashCode hashCode}.
 	 *
 	 * @param x The value for which to retrieve a hash code
 	 * @return The hash code
 	 *
 	 * @throws HibernateException A problem occurred calculating the hash code
 	 */
 	public int getHashCode(Object x) throws HibernateException;
 
 	/**
 	 * Get a hash code, consistent with persistence "equality".  Again for most types the normal usage is to
 	 * delegate to the value's {@link Object#hashCode hashCode}.
 	 *
 	 * @param x The value for which to retrieve a hash code
 	 * @param factory The session factory
 	 *
 	 * @return The hash code
 	 *
 	 * @throws HibernateException A problem occurred calculating the hash code
 	 */
 	public int getHashCode(Object x, SessionFactoryImplementor factory) throws HibernateException;
 	
 	/**
 	 * Perform a {@link java.util.Comparator} style comparison between values
 	 *
 	 * @param x The first value
 	 * @param y The second value
 	 *
 	 * @return The comparison result.  See {@link java.util.Comparator#compare} for a discussion.
 	 */
 	public int compare(Object x, Object y);
 
 	/**
 	 * Should the parent be considered dirty, given both the old and current value?
 	 * 
 	 * @param old the old value
 	 * @param current the current value
 	 * @param session The session from which the request originated.
 	 *
 	 * @return true if the field is dirty
 	 *
 	 * @throws HibernateException A problem occurred performing the checking
 	 */
 	public boolean isDirty(Object old, Object current, SessionImplementor session) throws HibernateException;
 
 	/**
 	 * Should the parent be considered dirty, given both the old and current value?
 	 *
 	 * @param oldState the old value
 	 * @param currentState the current value
 	 * @param checkable An array of booleans indicating which columns making up the value are actually checkable
 	 * @param session The session from which the request originated.
 	 *
 	 * @return true if the field is dirty
 	 *
 	 * @throws HibernateException A problem occurred performing the checking
 	 */
 	public boolean isDirty(Object oldState, Object currentState, boolean[] checkable, SessionImplementor session)
 			throws HibernateException;
 
 	/**
 	 * Has the value been modified compared to the current database state?  The difference between this
 	 * and the {@link #isDirty} methods is that here we need to account for "partially" built values.  This is really
 	 * only an issue with association types.  For most type implementations it is enough to simply delegate to
 	 * {@link #isDirty} here/
 	 *
 	 * @param dbState the database state, in a "hydrated" form, with identifiers unresolved
 	 * @param currentState the current state of the object
 	 * @param checkable which columns are actually updatable
 	 * @param session The session from which the request originated.
 	 *
 	 * @return true if the field has been modified
 	 *
 	 * @throws HibernateException A problem occurred performing the checking
 	 */
 	public boolean isModified(Object dbState, Object currentState, boolean[] checkable, SessionImplementor session)
 			throws HibernateException;
 
 	/**
 	 * Extract a value of the {@link #getReturnedClass() mapped class} from the JDBC result set. Implementors
 	 * should handle possibility of null values.
 	 *
 	 * @param rs The result set from which to extract value.
 	 * @param names the column names making up this type value (use to read from result set)
 	 * @param session The originating session
 	 * @param owner the parent entity
 	 *
 	 * @return The extracted value
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 * @throws SQLException An error from the JDBC driver
 	 *
 	 * @see Type#hydrate(ResultSet, String[], SessionImplementor, Object) alternative, 2-phase property initialization
 	 */
 	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException;
 
 	/**
 	 * Extract a value of the {@link #getReturnedClass() mapped class} from the JDBC result set. Implementors
 	 * should handle possibility of null values.  This form might be called if the type is known to be a
 	 * single-column type.
 	 *
 	 * @param rs The result set from which to extract value.
 	 * @param name the column name making up this type value (use to read from result set)
 	 * @param session The originating session
 	 * @param owner the parent entity
 	 *
 	 * @return The extracted value
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 * @throws SQLException An error from the JDBC driver
 	 */
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException;
 
 	/**
 	 * Bind a value represented by an instance of the {@link #getReturnedClass() mapped class} to the JDBC prepared
 	 * statement, ignoring some columns as dictated by the 'settable' parameter.  Implementors should handle the
 	 * possibility of null values.  A multi-column type should bind parameters starting from <tt>index</tt>.
 	 *
 	 * @param st The JDBC prepared statement to which to bind
 	 * @param value the object to write
 	 * @param index starting parameter bind index
 	 * @param settable an array indicating which columns to bind/ignore
 	 * @param session The originating session
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 * @throws SQLException An error from the JDBC driver
 	 */
 	public void nullSafeSet(PreparedStatement st, Object value, int index, boolean[] settable, SessionImplementor session)
 	throws HibernateException, SQLException;
 
 	/**
 	 * Bind a value represented by an instance of the {@link #getReturnedClass() mapped class} to the JDBC prepared
 	 * statement.  Implementors should handle possibility of null values.  A multi-column type should bind parameters
 	 * starting from <tt>index</tt>.
 	 *
 	 * @param st The JDBC prepared statement to which to bind
 	 * @param value the object to write
 	 * @param index starting parameter bind index
 	 * @param session The originating session
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 * @throws SQLException An error from the JDBC driver
 	 */
 	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
 	throws HibernateException, SQLException;
 
 	/**
 	 * Generate a representation of the value for logging purposes.
 	 *
 	 * @param value The value to be logged
 	 * @param factory The session factory
 	 *
 	 * @return The loggable representation
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public String toLoggableString(Object value, SessionFactoryImplementor factory)
 	throws HibernateException;
 
 	/**
-	 * A representation of the value to be embedded in an XML element.
-	 *
-	 * @param node The XML node to which to write the value
-	 * @param value The value to write
-	 * @param factory The session factory
-	 *
-	 * @throws HibernateException An error from Hibernate
-	 *
-	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory)
-	throws HibernateException;
-
-	/**
-	 * Parse the XML representation of an instance.
-	 *
-	 * @param xml The XML node from which to read the value
-	 * @param factory The session factory
-	 *
-	 * @return an instance of the {@link #getReturnedClass() mapped class}
-	 *
-	 * @throws HibernateException An error from Hibernate
-	 *
-	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException;
-
-	/**
 	 * Returns the abbreviated name of the type.
 	 *
 	 * @return String the Hibernate type name
 	 */
 	public String getName();
 
 	/**
 	 * Return a deep copy of the persistent state, stopping at entities and at collections.
 	 *
 	 * @param value The value to be copied
 	 * @param factory The session factory
 	 *
 	 * @return The deep copy
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object deepCopy(Object value, SessionFactoryImplementor factory)
 	throws HibernateException;
 
 	/**
 	 * Are objects of this type mutable. (With respect to the referencing object ...
 	 * entities and collections are considered immutable because they manage their
 	 * own internal state.)
 	 *
 	 * @return boolean
 	 */
 	public boolean isMutable();
 
 	/**
 	 * Return a disassembled representation of the object.  This is the value Hibernate will use in second level
 	 * caching, so care should be taken to break values down to their simplest forms; for entities especially, this
 	 * means breaking them down into their constituent parts.
 	 *
 	 * @param value the value to cache
 	 * @param session the originating session
 	 * @param owner optional parent entity object (needed for collections)
 	 *
 	 * @return the disassembled, deep cloned state
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public Serializable disassemble(Object value, SessionImplementor session, Object owner) throws HibernateException;
 
 	/**
 	 * Reconstruct the object from its disassembled state.  This method is the reciprocal of {@link #disassemble}
 	 *
 	 * @param cached the disassembled state from the cache
 	 * @param session the originating session
 	 * @param owner the parent entity object
 	 *
 	 * @return the (re)assembled object
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object assemble(Serializable cached, SessionImplementor session, Object owner)
 	throws HibernateException;
 	
 	/**
 	 * Called before assembling a query result set from the query cache, to allow batch fetching
 	 * of entities missing from the second-level cache.
 	 *
 	 * @param cached The key
 	 * @param session The originating session
 	 */
 	public void beforeAssemble(Serializable cached, SessionImplementor session);
 
 	/**
 	 * Extract a value from the JDBC result set.  This is useful for 2-phase property initialization - the second
 	 * phase is a call to {@link #resolve}
 	 * This hydrated value will be either:<ul>
 	 *     <li>in the case of an entity or collection type, the key</li>
 	 *     <li>otherwise, the value itself</li>
 	 * </ul>
 	 * 
 	 * @param rs The JDBC result set
 	 * @param names the column names making up this type value (use to read from result set)
 	 * @param session The originating session
 	 * @param owner the parent entity
 	 *
 	 * @return An entity or collection key, or an actual value.
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 * @throws SQLException An error from the JDBC driver
 	 *
 	 * @see #resolve
 	 */
 	public Object hydrate(ResultSet rs, String[] names, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException;
 
 	/**
 	 * The second phase of 2-phase loading.  Only really pertinent for entities and collections.  Here we resolve the
 	 * identifier to an entity or collection instance
 	 * 
 	 * @param value an identifier or value returned by <tt>hydrate()</tt>
 	 * @param owner the parent entity
 	 * @param session the session
 	 * 
 	 * @return the given value, or the value associated with the identifier
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 *
 	 * @see #hydrate
 	 */
 	public Object resolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException;
 	
 	/**
 	 * Given a hydrated, but unresolved value, return a value that may be used to reconstruct property-ref
 	 * associations.
 	 *
 	 * @param value The unresolved, hydrated value
 	 * @param session THe originating session
 	 * @param owner The value owner
 	 *
 	 * @return The semi-resolved value
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object semiResolve(Object value, SessionImplementor session, Object owner)
 	throws HibernateException;
 	
 	/**
 	 * As part of 2-phase loading, when we perform resolving what is the resolved type for this type?  Generally
 	 * speaking the type and its semi-resolved type will be the same.  The main deviation from this is in the
 	 * case of an entity where the type would be the entity type and semi-resolved type would be its identifier type
 	 *
 	 * @param factory The session factory
 	 *
 	 * @return The semi-resolved type
 	 */
 	public Type getSemiResolvedType(SessionFactoryImplementor factory);
 
 	/**
 	 * During merge, replace the existing (target) value in the entity we are merging to
 	 * with a new (original) value from the detached entity we are merging. For immutable
 	 * objects, or null values, it is safe to simply return the first parameter. For
 	 * mutable objects, it is safe to return a copy of the first parameter. For objects
 	 * with component values, it might make sense to recursively replace component values.
 	 *
 	 * @param original the value from the detached entity being merged
 	 * @param target the value in the managed entity
 	 * @param session The originating session
 	 * @param owner The owner of the value
 	 * @param copyCache The cache of already copied/replaced values
 	 *
 	 * @return the value to be merged
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object replace(
 			Object original, 
 			Object target, 
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache) throws HibernateException;
 	
 	/**
 	 * During merge, replace the existing (target) value in the entity we are merging to
 	 * with a new (original) value from the detached entity we are merging. For immutable
 	 * objects, or null values, it is safe to simply return the first parameter. For
 	 * mutable objects, it is safe to return a copy of the first parameter. For objects
 	 * with component values, it might make sense to recursively replace component values.
 	 *
 	 * @param original the value from the detached entity being merged
 	 * @param target the value in the managed entity
 	 * @param session The originating session
 	 * @param owner The owner of the value
 	 * @param copyCache The cache of already copied/replaced values
 	 * @param foreignKeyDirection For associations, which direction does the foreign key point?
 	 *
 	 * @return the value to be merged
 	 *
 	 * @throws HibernateException An error from Hibernate
 	 */
 	public Object replace(
 			Object original, 
 			Object target, 
 			SessionImplementor session, 
 			Object owner, 
 			Map copyCache, 
 			ForeignKeyDirection foreignKeyDirection) throws HibernateException;
 	
 	/**
 	 * Given an instance of the type, return an array of boolean, indicating
 	 * which mapped columns would be null.
 	 * 
 	 * @param value an instance of the type
 	 * @param mapping The mapping abstraction
 	 *
 	 * @return array indicating column nullness for a value instance
 	 */
 	public boolean[] toColumnNullness(Object value, Mapping mapping);
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
index aaa3b6fd07..92eca04b34 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
@@ -1,485 +1,345 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
  * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
  */
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Properties;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.classic.Lifecycle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.tuple.component.ComponentMetamodel;
 import org.hibernate.usertype.CompositeUserType;
 import org.hibernate.usertype.ParameterizedType;
 import org.hibernate.usertype.UserType;
 
 import static org.hibernate.internal.CoreLogging.messageLogger;
 
 /**
  * Used internally to build instances of {@link Type}, specifically it builds instances of
  * <p/>
  * <p/>
  * Used internally to obtain instances of <tt>Type</tt>. Applications should use static methods
  * and constants on <tt>org.hibernate.Hibernate</tt>.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 @SuppressWarnings({"unchecked"})
 public final class TypeFactory implements Serializable {
 	private static final CoreMessageLogger LOG = messageLogger( TypeFactory.class );
 
 	private final TypeScopeImpl typeScope = new TypeScopeImpl();
 
 	public static interface TypeScope extends Serializable {
 		public SessionFactoryImplementor resolveFactory();
 	}
 
 	private static class TypeScopeImpl implements TypeFactory.TypeScope {
 		private SessionFactoryImplementor factory;
 
 		public void injectSessionFactory(SessionFactoryImplementor factory) {
 			if ( this.factory != null ) {
 				LOG.scopingTypesToSessionFactoryAfterAlreadyScoped( this.factory, factory );
 			}
 			else {
 				LOG.tracev( "Scoping types to session factory {0}", factory );
 			}
 			this.factory = factory;
 		}
 
 		public SessionFactoryImplementor resolveFactory() {
 			if ( factory == null ) {
 				throw new HibernateException( "SessionFactory for type scoping not yet known" );
 			}
 			return factory;
 		}
 	}
 
 	public void injectSessionFactory(SessionFactoryImplementor factory) {
 		typeScope.injectSessionFactory( factory );
 	}
 
 	public SessionFactoryImplementor resolveSessionFactory() {
 		return typeScope.resolveFactory();
 	}
 
 	public Type byClass(Class clazz, Properties parameters) {
 		if ( Type.class.isAssignableFrom( clazz ) ) {
 			return type( clazz, parameters );
 		}
 
 		if ( CompositeUserType.class.isAssignableFrom( clazz ) ) {
 			return customComponent( clazz, parameters );
 		}
 
 		if ( UserType.class.isAssignableFrom( clazz ) ) {
 			return custom( clazz, parameters );
 		}
 
 		if ( Lifecycle.class.isAssignableFrom( clazz ) ) {
 			// not really a many-to-one association *necessarily*
 			return manyToOne( clazz.getName() );
 		}
 
 		if ( Serializable.class.isAssignableFrom( clazz ) ) {
 			return serializable( clazz );
 		}
 
 		return null;
 	}
 
 	public Type type(Class<Type> typeClass, Properties parameters) {
 		try {
 			Type type = typeClass.newInstance();
 			injectParameters( type, parameters );
 			return type;
 		}
 		catch (Exception e) {
 			throw new MappingException( "Could not instantiate Type: " + typeClass.getName(), e );
 		}
 	}
 
 	// todo : can a Properties be wrapped in unmodifiable in any way?
 	private final static Properties EMPTY_PROPERTIES = new Properties();
 
 	public static void injectParameters(Object type, Properties parameters) {
 		if ( ParameterizedType.class.isInstance( type ) ) {
 			if ( parameters == null ) {
 				( (ParameterizedType) type ).setParameterValues( EMPTY_PROPERTIES );
 			}
 			else {
 				( (ParameterizedType) type ).setParameterValues( parameters );
 			}
 		}
 		else if ( parameters != null && !parameters.isEmpty() ) {
 			throw new MappingException( "type is not parameterized: " + type.getClass().getName() );
 		}
 	}
 
 	public CompositeCustomType customComponent(Class<CompositeUserType> typeClass, Properties parameters) {
 		return customComponent( typeClass, parameters, typeScope );
 	}
 
 	/**
 	 * @deprecated Only for use temporary use by {@link org.hibernate.Hibernate}
 	 */
 	@Deprecated
 	@SuppressWarnings({"JavaDoc"})
 	public static CompositeCustomType customComponent(
 			Class<CompositeUserType> typeClass,
 			Properties parameters,
 			TypeScope scope) {
 		try {
 			CompositeUserType userType = typeClass.newInstance();
 			injectParameters( userType, parameters );
 			return new CompositeCustomType( userType );
 		}
 		catch (Exception e) {
 			throw new MappingException( "Unable to instantiate custom type: " + typeClass.getName(), e );
 		}
 	}
 
-	/**
-	 * @deprecated Use {@link #customCollection(String, java.util.Properties, String, String)}
-	 * instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public CollectionType customCollection(
-			String typeName,
-			Properties typeParameters,
-			String role,
-			String propertyRef,
-			boolean embedded) {
-		Class typeClass;
-		try {
-			typeClass = ReflectHelper.classForName( typeName );
-		}
-		catch (ClassNotFoundException cnfe) {
-			throw new MappingException( "user collection type class not found: " + typeName, cnfe );
-		}
-		CustomCollectionType result = new CustomCollectionType( typeScope, typeClass, role, propertyRef, embedded );
-		if ( typeParameters != null ) {
-			injectParameters( result.getUserType(), typeParameters );
-		}
-		return result;
-	}
-
 	public CollectionType customCollection(
 			String typeName,
 			Properties typeParameters,
 			String role,
 			String propertyRef) {
 		Class typeClass;
 		try {
 			typeClass = ReflectHelper.classForName( typeName );
 		}
 		catch (ClassNotFoundException cnfe) {
 			throw new MappingException( "user collection type class not found: " + typeName, cnfe );
 		}
 		CustomCollectionType result = new CustomCollectionType( typeScope, typeClass, role, propertyRef );
 		if ( typeParameters != null ) {
 			injectParameters( result.getUserType(), typeParameters );
 		}
 		return result;
 	}
 
 	public CustomType custom(Class<UserType> typeClass, Properties parameters) {
 		return custom( typeClass, parameters, typeScope );
 	}
 
 	/**
 	 * @deprecated Only for use temporary use by {@link org.hibernate.Hibernate}
 	 */
 	@Deprecated
 	public static CustomType custom(Class<UserType> typeClass, Properties parameters, TypeScope scope) {
 		try {
 			UserType userType = typeClass.newInstance();
 			injectParameters( userType, parameters );
 			return new CustomType( userType );
 		}
 		catch (Exception e) {
 			throw new MappingException( "Unable to instantiate custom type: " + typeClass.getName(), e );
 		}
 	}
 
 	/**
 	 * Build a {@link SerializableType} from the given {@link Serializable} class.
 	 *
 	 * @param serializableClass The {@link Serializable} class.
 	 * @param <T> The actual class type (extends Serializable)
 	 *
 	 * @return The built {@link SerializableType}
 	 */
 	public static <T extends Serializable> SerializableType<T> serializable(Class<T> serializableClass) {
 		return new SerializableType<T>( serializableClass );
 	}
 
 
 	// one-to-one type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public EntityType oneToOne(
 			String persistentClass,
 			ForeignKeyDirection foreignKeyType,
 			boolean referenceToPrimaryKey,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			String entityName,
 			String propertyName) {
 		return new OneToOneType(
 				typeScope, persistentClass, foreignKeyType, referenceToPrimaryKey,
 				uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName
 		);
 	}
 
 	public EntityType specialOneToOne(
 			String persistentClass,
 			ForeignKeyDirection foreignKeyType,
 			boolean referenceToPrimaryKey,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			String entityName,
 			String propertyName) {
 		return new SpecialOneToOneType(
 				typeScope, persistentClass, foreignKeyType, referenceToPrimaryKey,
 				uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName
 		);
 	}
 
 
 	// many-to-one type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public EntityType manyToOne(String persistentClass) {
 		return new ManyToOneType( typeScope, persistentClass );
 	}
 
 	public EntityType manyToOne(String persistentClass, boolean lazy) {
 		return new ManyToOneType( typeScope, persistentClass, lazy );
 	}
 
 	/**
 	 * @deprecated Use {@link #manyToOne(String, boolean, String, boolean, boolean, boolean, boolean)} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public EntityType manyToOne(
-			String persistentClass,
-			String uniqueKeyPropertyName,
-			boolean lazy,
-			boolean unwrapProxy,
-			boolean isEmbeddedInXML,
-			boolean ignoreNotFound,
-			boolean isLogicalOneToOne) {
-		return manyToOne(
-				persistentClass,
-				uniqueKeyPropertyName == null,
-				uniqueKeyPropertyName,
-				lazy,
-				unwrapProxy,
-				ignoreNotFound,
-				isLogicalOneToOne
-		);
-	}
-
-	/**
-	 * @deprecated Use {@link #manyToOne(String, boolean, String, boolean, boolean, boolean, boolean)} instead.
 	 */
 	@Deprecated
 	public EntityType manyToOne(
 			String persistentClass,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean ignoreNotFound,
 			boolean isLogicalOneToOne) {
 		return manyToOne(
 				persistentClass,
 				uniqueKeyPropertyName == null,
 				uniqueKeyPropertyName,
 				lazy,
 				unwrapProxy,
 				ignoreNotFound,
 				isLogicalOneToOne
 		);
 	}
 
 	public EntityType manyToOne(
 			String persistentClass,
 			boolean referenceToPrimaryKey,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean ignoreNotFound,
 			boolean isLogicalOneToOne) {
 		return new ManyToOneType(
 				typeScope,
 				persistentClass,
 				referenceToPrimaryKey,
 				uniqueKeyPropertyName,
 				lazy,
 				unwrapProxy,
 				ignoreNotFound,
 				isLogicalOneToOne
 		);
 	}
 
 	// collection type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
-	/**
-	 * @deprecated Use {@link #array(String, String, Class)} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public CollectionType array(String role, String propertyRef, boolean embedded, Class elementClass) {
-		return new ArrayType( typeScope, role, propertyRef, elementClass, embedded );
-	}
-
 	public CollectionType array(String role, String propertyRef, Class elementClass) {
 		return new ArrayType( typeScope, role, propertyRef, elementClass );
 	}
 
-	/**
-	 * @deprecated Use {@link #list(String, String)} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public CollectionType list(String role, String propertyRef, boolean embedded) {
-		return new ListType( typeScope, role, propertyRef, embedded );
-	}
-
 	public CollectionType list(String role, String propertyRef) {
 		return new ListType( typeScope, role, propertyRef );
 	}
 
-	/**
-	 * @deprecated Use {@link #bag(String, String)} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public CollectionType bag(String role, String propertyRef, boolean embedded) {
-		return new BagType( typeScope, role, propertyRef, embedded );
-	}
-
 	public CollectionType bag(String role, String propertyRef) {
 		return new BagType( typeScope, role, propertyRef );
 	}
 
-	/**
-	 * @deprecated Use {@link #idbag(String, String)} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public CollectionType idbag(String role, String propertyRef, boolean embedded) {
-		return new IdentifierBagType( typeScope, role, propertyRef, embedded );
-	}
-
 	public CollectionType idbag(String role, String propertyRef) {
 		return new IdentifierBagType( typeScope, role, propertyRef );
 	}
 
-	/**
-	 * @deprecated Use {@link #map(String, String)} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public CollectionType map(String role, String propertyRef, boolean embedded) {
-		return new MapType( typeScope, role, propertyRef, embedded );
-	}
-
 	public CollectionType map(String role, String propertyRef) {
 		return new MapType( typeScope, role, propertyRef );
 	}
 
-	/**
-	 * @deprecated Use {@link #orderedMap(String, String)} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public CollectionType orderedMap(String role, String propertyRef, boolean embedded) {
-		return new OrderedMapType( typeScope, role, propertyRef, embedded );
-	}
-
 	public CollectionType orderedMap(String role, String propertyRef) {
 		return new OrderedMapType( typeScope, role, propertyRef );
 	}
 
-	/**
-	 * @deprecated Use {@link #sortedMap(String, String, java.util.Comparator)} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public CollectionType sortedMap(String role, String propertyRef, boolean embedded, Comparator comparator) {
-		return new SortedMapType( typeScope, role, propertyRef, comparator, embedded );
-	}
-
 	public CollectionType sortedMap(String role, String propertyRef, Comparator comparator) {
 		return new SortedMapType( typeScope, role, propertyRef, comparator );
 	}
 
-	/**
-	 * @deprecated Use {@link #set(String, String)} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public CollectionType set(String role, String propertyRef, boolean embedded) {
-		return new SetType( typeScope, role, propertyRef, embedded );
-	}
-
 	public CollectionType set(String role, String propertyRef) {
 		return new SetType( typeScope, role, propertyRef );
 	}
 
-	/**
-	 * @deprecated Use {@link #orderedSet(String, String)} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public CollectionType orderedSet(String role, String propertyRef, boolean embedded) {
-		return new OrderedSetType( typeScope, role, propertyRef, embedded );
-	}
-
 	public CollectionType orderedSet(String role, String propertyRef) {
 		return new OrderedSetType( typeScope, role, propertyRef );
 	}
 
-	/**
-	 * @deprecated Use {@link #sortedSet(String, String, java.util.Comparator)} instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
-	 */
-	@Deprecated
-	public CollectionType sortedSet(String role, String propertyRef, boolean embedded, Comparator comparator) {
-		return new SortedSetType( typeScope, role, propertyRef, comparator, embedded );
-	}
-
 	public CollectionType sortedSet(String role, String propertyRef, Comparator comparator) {
 		return new SortedSetType( typeScope, role, propertyRef, comparator );
 	}
 
 	// component type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public ComponentType component(ComponentMetamodel metamodel) {
 		return new ComponentType( typeScope, metamodel );
 	}
 
 	public EmbeddedComponentType embeddedComponent(ComponentMetamodel metamodel) {
 		return new EmbeddedComponentType( typeScope, metamodel );
 	}
 
 
 	// any type builder ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Type any(Type metaType, Type identifierType) {
 		return new AnyType( typeScope, metaType, identifierType );
 	}
 }
