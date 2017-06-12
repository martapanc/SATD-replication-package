	public SingleTableEntityPersister(
			final PersistentClass persistentClass, 
			final EntityRegionAccessStrategy cacheAccessStrategy,
			final NaturalIdRegionAccessStrategy naturalIdRegionAccessStrategy,
			final SessionFactoryImplementor factory,
			final Mapping mapping) throws HibernateException {

		super( persistentClass, cacheAccessStrategy, naturalIdRegionAccessStrategy, factory );

		// CLASS + TABLE

		joinSpan = persistentClass.getJoinClosureSpan()+1;
		qualifiedTableNames = new String[joinSpan];
		isInverseTable = new boolean[joinSpan];
		isNullableTable = new boolean[joinSpan];
		keyColumnNames = new String[joinSpan][];
		final Table table = persistentClass.getRootTable();
		qualifiedTableNames[0] = table.getQualifiedName( 
				factory.getDialect(), 
				factory.getSettings().getDefaultCatalogName(), 
				factory.getSettings().getDefaultSchemaName() 
		);
		isInverseTable[0] = false;
		isNullableTable[0] = false;
		keyColumnNames[0] = getIdentifierColumnNames();
		cascadeDeleteEnabled = new boolean[joinSpan];

		// Custom sql
		customSQLInsert = new String[joinSpan];
		customSQLUpdate = new String[joinSpan];
		customSQLDelete = new String[joinSpan];
		insertCallable = new boolean[joinSpan];
		updateCallable = new boolean[joinSpan];
		deleteCallable = new boolean[joinSpan];
		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];
		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[joinSpan];

		customSQLInsert[0] = persistentClass.getCustomSQLInsert();
		insertCallable[0] = customSQLInsert[0] != null && persistentClass.isCustomInsertCallable();
		insertResultCheckStyles[0] = persistentClass.getCustomSQLInsertCheckStyle() == null
									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[0], insertCallable[0] )
									  : persistentClass.getCustomSQLInsertCheckStyle();
		customSQLUpdate[0] = persistentClass.getCustomSQLUpdate();
		updateCallable[0] = customSQLUpdate[0] != null && persistentClass.isCustomUpdateCallable();
		updateResultCheckStyles[0] = persistentClass.getCustomSQLUpdateCheckStyle() == null
									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[0], updateCallable[0] )
									  : persistentClass.getCustomSQLUpdateCheckStyle();
		customSQLDelete[0] = persistentClass.getCustomSQLDelete();
		deleteCallable[0] = customSQLDelete[0] != null && persistentClass.isCustomDeleteCallable();
		deleteResultCheckStyles[0] = persistentClass.getCustomSQLDeleteCheckStyle() == null
									  ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[0], deleteCallable[0] )
									  : persistentClass.getCustomSQLDeleteCheckStyle();

		// JOINS

		Iterator joinIter = persistentClass.getJoinClosureIterator();
		int j = 1;
		while ( joinIter.hasNext() ) {
			Join join = (Join) joinIter.next();
			qualifiedTableNames[j] = join.getTable().getQualifiedName( 
					factory.getDialect(), 
					factory.getSettings().getDefaultCatalogName(), 
					factory.getSettings().getDefaultSchemaName() 
			);
			isInverseTable[j] = join.isInverse();
			isNullableTable[j] = join.isOptional();
			cascadeDeleteEnabled[j] = join.getKey().isCascadeDeleteEnabled() && 
				factory.getDialect().supportsCascadeDelete();

			customSQLInsert[j] = join.getCustomSQLInsert();
			insertCallable[j] = customSQLInsert[j] != null && join.isCustomInsertCallable();
			insertResultCheckStyles[j] = join.getCustomSQLInsertCheckStyle() == null
			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLInsert[j], insertCallable[j] )
		                                  : join.getCustomSQLInsertCheckStyle();
			customSQLUpdate[j] = join.getCustomSQLUpdate();
			updateCallable[j] = customSQLUpdate[j] != null && join.isCustomUpdateCallable();
			updateResultCheckStyles[j] = join.getCustomSQLUpdateCheckStyle() == null
			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[j], updateCallable[j] )
		                                  : join.getCustomSQLUpdateCheckStyle();
			customSQLDelete[j] = join.getCustomSQLDelete();
			deleteCallable[j] = customSQLDelete[j] != null && join.isCustomDeleteCallable();
			deleteResultCheckStyles[j] = join.getCustomSQLDeleteCheckStyle() == null
			                              ? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[j], deleteCallable[j] )
		                                  : join.getCustomSQLDeleteCheckStyle();

			Iterator iter = join.getKey().getColumnIterator();
			keyColumnNames[j] = new String[ join.getKey().getColumnSpan() ];
			int i = 0;
			while ( iter.hasNext() ) {
				Column col = (Column) iter.next();
				keyColumnNames[j][i++] = col.getQuotedName( factory.getDialect() );
			}

			j++;
		}

		constraintOrderedTableNames = new String[qualifiedTableNames.length];
		constraintOrderedKeyColumnNames = new String[qualifiedTableNames.length][];
		for ( int i = qualifiedTableNames.length - 1, position = 0; i >= 0; i--, position++ ) {
			constraintOrderedTableNames[position] = qualifiedTableNames[i];
			constraintOrderedKeyColumnNames[position] = keyColumnNames[i];
		}

		spaces = ArrayHelper.join(
				qualifiedTableNames, 
				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
		);
		
		final boolean lazyAvailable = isInstrumented();

		boolean hasDeferred = false;
		ArrayList subclassTables = new ArrayList();
		ArrayList joinKeyColumns = new ArrayList();
		ArrayList<Boolean> isConcretes = new ArrayList<Boolean>();
		ArrayList<Boolean> isDeferreds = new ArrayList<Boolean>();
		ArrayList<Boolean> isInverses = new ArrayList<Boolean>();
		ArrayList<Boolean> isNullables = new ArrayList<Boolean>();
		ArrayList<Boolean> isLazies = new ArrayList<Boolean>();
		subclassTables.add( qualifiedTableNames[0] );
		joinKeyColumns.add( getIdentifierColumnNames() );
		isConcretes.add(Boolean.TRUE);
		isDeferreds.add(Boolean.FALSE);
		isInverses.add(Boolean.FALSE);
		isNullables.add(Boolean.FALSE);
		isLazies.add(Boolean.FALSE);
		joinIter = persistentClass.getSubclassJoinClosureIterator();
		while ( joinIter.hasNext() ) {
			Join join = (Join) joinIter.next();
			isConcretes.add( persistentClass.isClassOrSuperclassJoin(join) );
			isDeferreds.add( join.isSequentialSelect() );
			isInverses.add( join.isInverse() );
			isNullables.add( join.isOptional() );
			isLazies.add( lazyAvailable && join.isLazy() );
			if ( join.isSequentialSelect() && !persistentClass.isClassOrSuperclassJoin(join) ) hasDeferred = true;
			subclassTables.add( join.getTable().getQualifiedName( 
					factory.getDialect(), 
					factory.getSettings().getDefaultCatalogName(), 
					factory.getSettings().getDefaultSchemaName() 
			) );
			Iterator iter = join.getKey().getColumnIterator();
			String[] keyCols = new String[ join.getKey().getColumnSpan() ];
			int i = 0;
			while ( iter.hasNext() ) {
				Column col = (Column) iter.next();
				keyCols[i++] = col.getQuotedName( factory.getDialect() );
			}
			joinKeyColumns.add(keyCols);
		}
		
		subclassTableSequentialSelect = ArrayHelper.toBooleanArray(isDeferreds);
		subclassTableNameClosure = ArrayHelper.toStringArray(subclassTables);
		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray(isLazies);
		subclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( joinKeyColumns );
		isClassOrSuperclassTable = ArrayHelper.toBooleanArray(isConcretes);
		isInverseSubclassTable = ArrayHelper.toBooleanArray(isInverses);
		isNullableSubclassTable = ArrayHelper.toBooleanArray(isNullables);
		hasSequentialSelects = hasDeferred;

		// DISCRIMINATOR

		if ( persistentClass.isPolymorphic() ) {
			Value discrimValue = persistentClass.getDiscriminator();
			if (discrimValue==null) {
				throw new MappingException("discriminator mapping required for single table polymorphic persistence");
			}
			forceDiscriminator = persistentClass.isForceDiscriminator();
			Selectable selectable = (Selectable) discrimValue.getColumnIterator().next();
			if ( discrimValue.hasFormula() ) {
				Formula formula = (Formula) selectable;
				discriminatorFormula = formula.getFormula();
				discriminatorFormulaTemplate = formula.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
				discriminatorColumnName = null;
				discriminatorColumnReaders = null;
				discriminatorColumnReaderTemplate = null;
				discriminatorAlias = "clazz_";
			}
			else {
				Column column = (Column) selectable;
				discriminatorColumnName = column.getQuotedName( factory.getDialect() );
				discriminatorColumnReaders = column.getReadExpr( factory.getDialect() );
				discriminatorColumnReaderTemplate = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
				discriminatorAlias = column.getAlias( factory.getDialect(), persistentClass.getRootTable() );
				discriminatorFormula = null;
				discriminatorFormulaTemplate = null;
			}
			discriminatorType = persistentClass.getDiscriminator().getType();
			if ( persistentClass.isDiscriminatorValueNull() ) {
				discriminatorValue = NULL_DISCRIMINATOR;
				discriminatorSQLValue = InFragment.NULL;
				discriminatorInsertable = false;
			}
			else if ( persistentClass.isDiscriminatorValueNotNull() ) {
				discriminatorValue = NOT_NULL_DISCRIMINATOR;
				discriminatorSQLValue = InFragment.NOT_NULL;
				discriminatorInsertable = false;
			}
			else {
				discriminatorInsertable = persistentClass.isDiscriminatorInsertable() && !discrimValue.hasFormula();
				try {
					DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
					discriminatorValue = dtype.stringToObject( persistentClass.getDiscriminatorValue() );
					discriminatorSQLValue = dtype.objectToSQLString( discriminatorValue, factory.getDialect() );
				}
				catch (ClassCastException cce) {
					throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
				}
				catch (Exception e) {
					throw new MappingException("Could not format discriminator value to SQL string", e);
				}
			}
		}
		else {
			forceDiscriminator = false;
			discriminatorInsertable = false;
			discriminatorColumnName = null;
			discriminatorColumnReaders = null;
			discriminatorColumnReaderTemplate = null;
			discriminatorAlias = null;
			discriminatorType = null;
			discriminatorValue = null;
			discriminatorSQLValue = null;
			discriminatorFormula = null;
			discriminatorFormulaTemplate = null;
		}

		// PROPERTIES

		propertyTableNumbers = new int[ getPropertySpan() ];
		Iterator iter = persistentClass.getPropertyClosureIterator();
		int i=0;
		while( iter.hasNext() ) {
			Property prop = (Property) iter.next();
			propertyTableNumbers[i++] = persistentClass.getJoinNumber(prop);

		}

		//TODO: code duplication with JoinedSubclassEntityPersister
		
		ArrayList columnJoinNumbers = new ArrayList();
		ArrayList formulaJoinedNumbers = new ArrayList();
		ArrayList propertyJoinNumbers = new ArrayList();
		
		iter = persistentClass.getSubclassPropertyClosureIterator();
		while ( iter.hasNext() ) {
			Property prop = (Property) iter.next();
			Integer join = persistentClass.getJoinNumber(prop);
			propertyJoinNumbers.add(join);

			//propertyTableNumbersByName.put( prop.getName(), join );
			propertyTableNumbersByNameAndSubclass.put( 
					prop.getPersistentClass().getEntityName() + '.' + prop.getName(), 
					join 
			);

			Iterator citer = prop.getColumnIterator();
			while ( citer.hasNext() ) {
				Selectable thing = (Selectable) citer.next();
				if ( thing.isFormula() ) {
					formulaJoinedNumbers.add(join);
				}
				else {
					columnJoinNumbers.add(join);
				}
			}
		}
		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(columnJoinNumbers);
		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formulaJoinedNumbers);
		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propertyJoinNumbers);

		int subclassSpan = persistentClass.getSubclassSpan() + 1;
		subclassClosure = new String[subclassSpan];
		subclassClosure[0] = getEntityName();
		if ( persistentClass.isPolymorphic() ) {
			addSubclassByDiscriminatorValue( discriminatorValue, getEntityName() );
		}

		// SUBCLASSES
		if ( persistentClass.isPolymorphic() ) {
			iter = persistentClass.getSubclassIterator();
			int k=1;
			while ( iter.hasNext() ) {
				Subclass sc = (Subclass) iter.next();
				subclassClosure[k++] = sc.getEntityName();
				if ( sc.isDiscriminatorValueNull() ) {
					addSubclassByDiscriminatorValue( NULL_DISCRIMINATOR, sc.getEntityName() );
				}
				else if ( sc.isDiscriminatorValueNotNull() ) {
					addSubclassByDiscriminatorValue( NOT_NULL_DISCRIMINATOR, sc.getEntityName() );
				}
				else {
					try {
						DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
						addSubclassByDiscriminatorValue(
							dtype.stringToObject( sc.getDiscriminatorValue() ),
							sc.getEntityName()
						);
					}
					catch (ClassCastException cce) {
						throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
					}
					catch (Exception e) {
						throw new MappingException("Error parsing discriminator value", e);
					}
				}
			}
		}

		initLockers();

		initSubclassPropertyAliasesMap(persistentClass);
		
		postConstruct(mapping);

	}
