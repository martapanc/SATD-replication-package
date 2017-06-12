diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 15f4ad1fea..92022c865e 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -1098,2015 +1098,2035 @@ public final class HbmBinder {
 				else if ( columnElement.getName().equals( "formula" ) ) {
 					Formula formula = new Formula();
 					formula.setFormula( columnElement.getText() );
 					simpleValue.addFormula( formula );
 				}
 			}
 
 			// todo : another GoodThing would be to go back after all parsing and see if all the columns
 			// (and no formulas) are contained in a defined unique key that only contains these columns.
 			// That too would mark this as a logical one-to-one
 			final Attribute uniqueAttribute = node.attribute( "unique" );
 			if ( uniqueAttribute != null
 					&& "true".equals( uniqueAttribute.getValue() )
 					&& ManyToOne.class.isInstance( simpleValue ) ) {
 				( (ManyToOne) simpleValue ).markAsLogicalOneToOne();
 			}
 		}
 		else {
 			if ( node.elementIterator( "column" ).hasNext() ) {
 				throw new MappingException(
 					"column attribute may not be used together with <column> subelement" );
 			}
 			if ( node.elementIterator( "formula" ).hasNext() ) {
 				throw new MappingException(
 					"column attribute may not be used together with <formula> subelement" );
 			}
 
 			Column column = new Column();
 			column.setValue( simpleValue );
 			bindColumn( node, column, isNullable );
 			if ( column.isUnique() && ManyToOne.class.isInstance( simpleValue ) ) {
 				( (ManyToOne) simpleValue ).markAsLogicalOneToOne();
 			}
 			String columnName = columnAttribute.getValue();
 			String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
 					columnName, propertyPath
 			);
 			columnName = mappings.getNamingStrategy().columnName( columnName );
 			columnName = quoteIdentifier( columnName, mappings );
 			column.setName( columnName );
 			if ( table != null ) {
 				table.addColumn( column ); // table=null -> an association - fill
 				                           // it in later
 				//TODO fill in the mappings for table == null
 				mappings.addColumnBinding( logicalColumnName, column, table );
 			}
 			simpleValue.addColumn( column );
 			bindIndex( node.attribute( "index" ), table, column, mappings );
 			bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 		}
 
 		if ( autoColumn && simpleValue.getColumnSpan() == 0 ) {
 			Column column = new Column();
 			column.setValue( simpleValue );
 			bindColumn( node, column, isNullable );
 			String columnName = mappings.getNamingStrategy().propertyToColumnName( propertyPath );
 			columnName = quoteIdentifier( columnName, mappings );
 			column.setName( columnName );
 			String logicalName = mappings.getNamingStrategy().logicalColumnName( null, propertyPath );
 			mappings.addColumnBinding( logicalName, column, table );
 			/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
 			 * slightly higer level in the stack (to get all the information we need)
 			 * Right now HbmMetadataSourceProcessorImpl does not support the
 			 */
 			simpleValue.getTable().addColumn( column );
 			simpleValue.addColumn( column );
 			bindIndex( node.attribute( "index" ), table, column, mappings );
 			bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 		}
 
 	}
 
 	private static void bindIndex(Attribute indexAttribute, Table table, Column column, Mappings mappings) {
 		if ( indexAttribute != null && table != null ) {
 			StringTokenizer tokens = new StringTokenizer( indexAttribute.getValue(), ", " );
 			while ( tokens.hasMoreTokens() ) {
 				table.getOrCreateIndex( tokens.nextToken() ).addColumn( column );
 			}
 		}
 	}
 
 	private static void bindUniqueKey(Attribute uniqueKeyAttribute, Table table, Column column, Mappings mappings) {
 		if ( uniqueKeyAttribute != null && table != null ) {
 			StringTokenizer tokens = new StringTokenizer( uniqueKeyAttribute.getValue(), ", " );
 			while ( tokens.hasMoreTokens() ) {
 				table.getOrCreateUniqueKey( tokens.nextToken() ).addColumn( column );
 			}
 		}
 	}
 
 	// automatically makes a column with the default name if none is specifed by XML
 	public static void bindSimpleValue(Element node, SimpleValue simpleValue, boolean isNullable,
 			String path, Mappings mappings) throws MappingException {
 		bindSimpleValueType( node, simpleValue, mappings );
 
 		bindColumnsOrFormula( node, simpleValue, path, isNullable, mappings );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) simpleValue.setForeignKeyName( fkNode.getValue() );
 	}
 
 	private static void bindSimpleValueType(Element node, SimpleValue simpleValue, Mappings mappings)
 			throws MappingException {
 		String typeName = null;
 
 		Properties parameters = new Properties();
 
 		Attribute typeNode = node.attribute( "type" );
         if ( typeNode == null ) {
             typeNode = node.attribute( "id-type" ); // for an any
         }
         else {
             typeName = typeNode.getValue();
         }
 
 		Element typeChild = node.element( "type" );
 		if ( typeName == null && typeChild != null ) {
 			typeName = typeChild.attribute( "name" ).getValue();
 			Iterator typeParameters = typeChild.elementIterator( "param" );
 
 			while ( typeParameters.hasNext() ) {
 				Element paramElement = (Element) typeParameters.next();
 				parameters.setProperty(
 						paramElement.attributeValue( "name" ),
 						paramElement.getTextTrim()
 					);
 			}
 		}
 
 		resolveAndBindTypeDef(simpleValue, mappings, typeName, parameters);
 	}
 
 	private static void resolveAndBindTypeDef(SimpleValue simpleValue,
 			Mappings mappings, String typeName, Properties parameters) {
 		TypeDef typeDef = mappings.getTypeDef( typeName );
 		if ( typeDef != null ) {
 			typeName = typeDef.getTypeClass();
 			// parameters on the property mapping should
 			// override parameters in the typedef
 			Properties allParameters = new Properties();
 			allParameters.putAll( typeDef.getParameters() );
 			allParameters.putAll( parameters );
 			parameters = allParameters;
 		}else if (typeName!=null && !mappings.isInSecondPass()){
 			BasicType basicType=mappings.getTypeResolver().basic(typeName);
 			if (basicType==null) {
 				/*
 				 * If the referenced typeName isn't a basic-type, it's probably a typedef defined 
 				 * in a mapping file not read yet.
 				 * It should be solved by deferring the resolution and binding of this type until 
 				 * all mapping files are read - the second passes.
 				 * Fixes issue HHH-7300
 				 */
 				SecondPass resolveUserTypeMappingSecondPass=new ResolveUserTypeMappingSecondPass(simpleValue,typeName,mappings,parameters);
 				mappings.addSecondPass(resolveUserTypeMappingSecondPass);
 			}
 		}
 
 		if ( !parameters.isEmpty() ) simpleValue.setTypeParameters( parameters );
 
 		if ( typeName != null ) simpleValue.setTypeName( typeName );
 	}
 
 	public static void bindProperty(
 			Element node,
 	        Property property,
 	        Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		String propName = node.attributeValue( "name" );
 		property.setName( propName );
 		String nodeName = node.attributeValue( "node" );
 		if (nodeName==null) nodeName = propName;
 		property.setNodeName( nodeName );
 
 		// TODO:
 		//Type type = model.getValue().getType();
 		//if (type==null) throw new MappingException(
 		//"Could not determine a property type for: " + model.getName() );
 
 		Attribute accessNode = node.attribute( "access" );
 		if ( accessNode != null ) {
 			property.setPropertyAccessorName( accessNode.getValue() );
 		}
 		else if ( node.getName().equals( "properties" ) ) {
 			property.setPropertyAccessorName( "embedded" );
 		}
 		else {
 			property.setPropertyAccessorName( mappings.getDefaultAccess() );
 		}
 
 		Attribute cascadeNode = node.attribute( "cascade" );
 		property.setCascade( cascadeNode == null ? mappings.getDefaultCascade() : cascadeNode
 			.getValue() );
 
 		Attribute updateNode = node.attribute( "update" );
 		property.setUpdateable( updateNode == null || "true".equals( updateNode.getValue() ) );
 
 		Attribute insertNode = node.attribute( "insert" );
 		property.setInsertable( insertNode == null || "true".equals( insertNode.getValue() ) );
 
 		Attribute lockNode = node.attribute( "optimistic-lock" );
 		property.setOptimisticLocked( lockNode == null || "true".equals( lockNode.getValue() ) );
 
 		Attribute generatedNode = node.attribute( "generated" );
         String generationName = generatedNode == null ? null : generatedNode.getValue();
 
 		// Handle generated properties.
 		GenerationTiming generationTiming = GenerationTiming.parseFromName( generationName );
 		if ( generationTiming == GenerationTiming.ALWAYS || generationTiming == GenerationTiming.INSERT ) {
 			// we had generation specified...
 			//   	HBM only supports "database generated values"
 			property.setValueGenerationStrategy( new GeneratedValueGeneration( generationTiming ) );
 
 			// generated properties can *never* be insertable...
 			if ( property.isInsertable() ) {
 				if ( insertNode == null ) {
 					// insertable simply because that is the user did not specify
 					// anything; just override it
 					property.setInsertable( false );
 				}
 				else {
 					// the user specifically supplied insert="true",
 					// which constitutes an illegal combo
 					throw new MappingException(
 							"cannot specify both insert=\"true\" and generated=\"" + generationTiming.name().toLowerCase() +
 									"\" for property: " +
 									propName
 					);
 				}
 			}
 
 			// properties generated on update can never be updateable...
 			if ( property.isUpdateable() && generationTiming == GenerationTiming.ALWAYS ) {
 				if ( updateNode == null ) {
 					// updateable only because the user did not specify
 					// anything; just override it
 					property.setUpdateable( false );
 				}
 				else {
 					// the user specifically supplied update="true",
 					// which constitutes an illegal combo
 					throw new MappingException(
 							"cannot specify both update=\"true\" and generated=\"" + generationTiming.name().toLowerCase() +
 									"\" for property: " +
 									propName
 					);
 				}
 			}
 		}
 
 
 		boolean isLazyable = "property".equals( node.getName() ) ||
 				"component".equals( node.getName() ) ||
 				"many-to-one".equals( node.getName() ) ||
 				"one-to-one".equals( node.getName() ) ||
 				"any".equals( node.getName() );
 		if ( isLazyable ) {
 			Attribute lazyNode = node.attribute( "lazy" );
 			property.setLazy( lazyNode != null && "true".equals( lazyNode.getValue() ) );
 		}
 
 		if ( LOG.isDebugEnabled() ) {
 			String msg = "Mapped property: " + property.getName();
 			String columns = columns( property.getValue() );
 			if ( columns.length() > 0 ) msg += " -> " + columns;
 			// TODO: this fails if we run with debug on!
 			// if ( model.getType()!=null ) msg += ", type: " + model.getType().getName();
 			LOG.debug( msg );
 		}
 
 		property.setMetaAttributes( getMetas( node, inheritedMetas ) );
 
 	}
 
 	private static String columns(Value val) {
 		StringBuilder columns = new StringBuilder();
 		Iterator iter = val.getColumnIterator();
 		while ( iter.hasNext() ) {
 			columns.append( ( (Selectable) iter.next() ).getText() );
 			if ( iter.hasNext() ) columns.append( ", " );
 		}
 		return columns.toString();
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollection(Element node, Collection collection, String className,
 			String path, Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		// ROLENAME
 		collection.setRole(path);
 
 		Attribute inverseNode = node.attribute( "inverse" );
 		if ( inverseNode != null ) {
 			collection.setInverse( "true".equals( inverseNode.getValue() ) );
 		}
 
 		Attribute mutableNode = node.attribute( "mutable" );
 		if ( mutableNode != null ) {
 			collection.setMutable( !"false".equals( mutableNode.getValue() ) );
 		}
 
 		Attribute olNode = node.attribute( "optimistic-lock" );
 		collection.setOptimisticLocked( olNode == null || "true".equals( olNode.getValue() ) );
 
 		Attribute orderNode = node.attribute( "order-by" );
 		if ( orderNode != null ) {
 			collection.setOrderBy( orderNode.getValue() );
 		}
 		Attribute whereNode = node.attribute( "where" );
 		if ( whereNode != null ) {
 			collection.setWhere( whereNode.getValue() );
 		}
 		Attribute batchNode = node.attribute( "batch-size" );
 		if ( batchNode != null ) {
 			collection.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
 		}
 
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		collection.setNodeName( nodeName );
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
 		collection.setEmbedded( embed==null || "true".equals(embed) );
 
 
 		// PERSISTER
 		Attribute persisterNode = node.attribute( "persister" );
 		if ( persisterNode != null ) {
 			try {
 				collection.setCollectionPersisterClass( ReflectHelper.classForName( persisterNode
 					.getValue() ) );
 			}
 			catch (ClassNotFoundException cnfe) {
 				throw new MappingException( "Could not find collection persister class: "
 					+ persisterNode.getValue() );
 			}
 		}
 
 		Attribute typeNode = node.attribute( "collection-type" );
 		if ( typeNode != null ) {
 			String typeName = typeNode.getValue();
 			TypeDef typeDef = mappings.getTypeDef( typeName );
 			if ( typeDef != null ) {
 				collection.setTypeName( typeDef.getTypeClass() );
 				collection.setTypeParameters( typeDef.getParameters() );
 			}
 			else {
 				collection.setTypeName( typeName );
 			}
 		}
 
 		// FETCH STRATEGY
 
 		initOuterJoinFetchSetting( node, collection );
 
 		if ( "subselect".equals( node.attributeValue("fetch") ) ) {
 			collection.setSubselectLoadable(true);
 			collection.getOwner().setSubselectLoadableCollections(true);
 		}
 
 		initLaziness( node, collection, mappings, "true", mappings.isDefaultLazy() );
 		//TODO: suck this into initLaziness!
 		if ( "extra".equals( node.attributeValue("lazy") ) ) {
 			collection.setLazy(true);
 			collection.setExtraLazy(true);
 		}
 
 		Element oneToManyNode = node.element( "one-to-many" );
 		if ( oneToManyNode != null ) {
 			OneToMany oneToMany = new OneToMany( mappings, collection.getOwner() );
 			collection.setElement( oneToMany );
 			bindOneToMany( oneToManyNode, oneToMany, mappings );
 			// we have to set up the table later!! yuck
 		}
 		else {
 			// TABLE
 			Attribute tableNode = node.attribute( "table" );
 			String tableName;
 			if ( tableNode != null ) {
 				tableName = mappings.getNamingStrategy().tableName( tableNode.getValue() );
 			}
 			else {
 				//tableName = mappings.getNamingStrategy().propertyToTableName( className, path );
 				Table ownerTable = collection.getOwner().getTable();
 				//TODO mappings.getLogicalTableName(ownerTable)
 				String logicalOwnerTableName = ownerTable.getName();
 				//FIXME we don't have the associated entity table name here, has to be done in a second pass
 				tableName = mappings.getNamingStrategy().collectionTableName(
 						collection.getOwner().getEntityName(),
 						logicalOwnerTableName ,
 						null,
 						null,
 						path
 				);
 				if ( ownerTable.isQuoted() ) {
 					tableName = StringHelper.quote( tableName );
 				}
 			}
 			Attribute schemaNode = node.attribute( "schema" );
 			String schema = schemaNode == null ?
 					mappings.getSchemaName() : schemaNode.getValue();
 
 			Attribute catalogNode = node.attribute( "catalog" );
 			String catalog = catalogNode == null ?
 					mappings.getCatalogName() : catalogNode.getValue();
 
 			Table table = mappings.addTable(
 					schema,
 					catalog,
 					tableName,
 					getSubselect( node ),
 					false
 				);
 			collection.setCollectionTable( table );
 			bindComment(table, node);
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 			}
 		}
 
 		// SORT
 		Attribute sortedAtt = node.attribute( "sort" );
 		// unsorted, natural, comparator.class.name
 		if ( sortedAtt == null || sortedAtt.getValue().equals( "unsorted" ) ) {
 			collection.setSorted( false );
 		}
 		else {
 			collection.setSorted( true );
 			String comparatorClassName = sortedAtt.getValue();
 			if ( !comparatorClassName.equals( "natural" ) ) {
 				collection.setComparatorClassName(comparatorClassName);
 			}
 		}
 
 		// ORPHAN DELETE (used for programmer error detection)
 		Attribute cascadeAtt = node.attribute( "cascade" );
 		if ( cascadeAtt != null && cascadeAtt.getValue().indexOf( "delete-orphan" ) >= 0 ) {
 			collection.setOrphanDelete( true );
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, collection );
 		// set up second pass
 		if ( collection instanceof List ) {
 			mappings.addSecondPass( new ListSecondPass( node, mappings, (List) collection, inheritedMetas ) );
 		}
 		else if ( collection instanceof Map ) {
 			mappings.addSecondPass( new MapSecondPass( node, mappings, (Map) collection, inheritedMetas ) );
 		}
 		else if ( collection instanceof IdentifierCollection ) {
 			mappings.addSecondPass( new IdentifierCollectionSecondPass(
 					node,
 					mappings,
 					collection,
 					inheritedMetas
 				) );
 		}
 		else {
 			mappings.addSecondPass( new CollectionSecondPass( node, mappings, collection, inheritedMetas ) );
 		}
 
 		Iterator iter = node.elementIterator( "filter" );
 		while ( iter.hasNext() ) {
 			final Element filter = (Element) iter.next();
 			parseFilter( filter, collection, mappings );
 		}
 
 		Iterator tables = node.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			collection.getSynchronizedTables().add(
 				( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 
 		Element element = node.element( "loader" );
 		if ( element != null ) {
 			collection.setLoaderName( element.attributeValue( "query-ref" ) );
 		}
 
 		collection.setReferencedPropertyName( node.element( "key" ).attributeValue( "property-ref" ) );
 	}
 
 	private static void initLaziness(
 			Element node,
 			Fetchable fetchable,
 			Mappings mappings,
 			String proxyVal,
 			boolean defaultLazy
 	) {
 		Attribute lazyNode = node.attribute( "lazy" );
 		boolean isLazyTrue = lazyNode == null ?
 				defaultLazy && fetchable.isLazy() : //fetch="join" overrides default laziness
 				lazyNode.getValue().equals(proxyVal); //fetch="join" overrides default laziness
 		fetchable.setLazy( isLazyTrue );
 	}
 
 	private static void initLaziness(
 			Element node,
 			ToOne fetchable,
 			Mappings mappings,
 			boolean defaultLazy
 	) {
 		if ( "no-proxy".equals( node.attributeValue( "lazy" ) ) ) {
 			fetchable.setUnwrapProxy(true);
 			fetchable.setLazy( true );
 			//TODO: better to degrade to lazy="false" if uninstrumented
 		}
 		else {
 			initLaziness( node, fetchable, mappings, "proxy", defaultLazy );
 		}
 	}
 
 	private static void bindColumnsOrFormula(Element node, SimpleValue simpleValue, String path,
 			boolean isNullable, Mappings mappings) {
 		Attribute formulaNode = node.attribute( "formula" );
 		if ( formulaNode != null ) {
 			Formula f = new Formula();
 			f.setFormula( formulaNode.getText() );
 			simpleValue.addFormula( f );
 		}
 		else {
 			bindColumns( node, simpleValue, isNullable, true, path, mappings );
 		}
 	}
 
 	private static void bindComment(Table table, Element node) {
 		Element comment = node.element("comment");
 		if (comment!=null) table.setComment( comment.getTextTrim() );
 	}
 
 	public static void bindManyToOne(Element node, ManyToOne manyToOne, String path,
 			boolean isNullable, Mappings mappings) throws MappingException {
 
 		bindColumnsOrFormula( node, manyToOne, path, isNullable, mappings );
 		initOuterJoinFetchSetting( node, manyToOne );
 		initLaziness( node, manyToOne, mappings, true );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) {
 			manyToOne.setReferencedPropertyName( ukName.getValue() );
 		}
 		manyToOne.setReferenceToPrimaryKey( manyToOne.getReferencedPropertyName() == null );
 
 		manyToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
 		manyToOne.setEmbedded( embed == null || "true".equals( embed ) );
 
 		String notFound = node.attributeValue( "not-found" );
 		manyToOne.setIgnoreNotFound( "ignore".equals( notFound ) );
 
 		if( ukName != null && !manyToOne.isIgnoreNotFound() ) {
 			if ( !node.getName().equals("many-to-many") ) { //TODO: really bad, evil hack to fix!!!
 				mappings.addSecondPass( new ManyToOneSecondPass(manyToOne) );
 			}
 		}
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) manyToOne.setForeignKeyName( fkNode.getValue() );
 
 		String cascade = node.attributeValue( "cascade" );
 		if ( cascade != null && cascade.indexOf( "delete-orphan" ) >= 0 ) {
 			if ( !manyToOne.isLogicalOneToOne() ) {
 				throw new MappingException(
 						"many-to-one attribute [" + path + "] does not support orphan delete as it is not unique"
 				);
 			}
 		}
 	}
 
 	public static void bindAny(Element node, Any any, boolean isNullable, Mappings mappings)
 			throws MappingException {
 		any.setIdentifierType( getTypeFromXML( node ) );
 		Attribute metaAttribute = node.attribute( "meta-type" );
 		if ( metaAttribute != null ) {
 			any.setMetaType( metaAttribute.getValue() );
 
 			Iterator iter = node.elementIterator( "meta-value" );
 			if ( iter.hasNext() ) {
 				HashMap values = new HashMap();
 				org.hibernate.type.Type metaType = mappings.getTypeResolver().heuristicType( any.getMetaType() );
 				while ( iter.hasNext() ) {
 					Element metaValue = (Element) iter.next();
 					try {
 						Object value = ( (DiscriminatorType) metaType ).stringToObject( metaValue
 							.attributeValue( "value" ) );
 						String entityName = getClassName( metaValue.attribute( "class" ), mappings );
 						values.put( value, entityName );
 					}
 					catch (ClassCastException cce) {
 						throw new MappingException( "meta-type was not a DiscriminatorType: "
 							+ metaType.getName() );
 					}
 					catch (Exception e) {
 						throw new MappingException( "could not interpret meta-value", e );
 					}
 				}
 				any.setMetaValues( values );
 			}
 
 		}
 
 		bindColumns( node, any, isNullable, false, null, mappings );
 	}
 
 	public static void bindOneToOne(Element node, OneToOne oneToOne, String path, boolean isNullable,
 			Mappings mappings) throws MappingException {
 
 		bindColumns( node, oneToOne, isNullable, false, null, mappings );
 
 		Attribute constrNode = node.attribute( "constrained" );
 		boolean constrained = constrNode != null && constrNode.getValue().equals( "true" );
 		oneToOne.setConstrained( constrained );
 
 		oneToOne.setForeignKeyType( constrained ?
 				ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT :
 				ForeignKeyDirection.FOREIGN_KEY_TO_PARENT );
 
 		initOuterJoinFetchSetting( node, oneToOne );
 		initLaziness( node, oneToOne, mappings, true );
 
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
 		oneToOne.setEmbedded( "true".equals( embed ) );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) oneToOne.setForeignKeyName( fkNode.getValue() );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) oneToOne.setReferencedPropertyName( ukName.getValue() );
 		oneToOne.setReferenceToPrimaryKey( oneToOne.getReferencedPropertyName() == null );
 
 		oneToOne.setPropertyName( node.attributeValue( "name" ) );
 
 		oneToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String cascade = node.attributeValue( "cascade" );
 		if ( cascade != null && cascade.indexOf( "delete-orphan" ) >= 0 ) {
 			if ( oneToOne.isConstrained() ) {
 				throw new MappingException(
 						"one-to-one attribute [" + path + "] does not support orphan delete as it is constrained"
 				);
 			}
 		}
 	}
 
 	public static void bindOneToMany(Element node, OneToMany oneToMany, Mappings mappings)
 			throws MappingException {
 
 		oneToMany.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
 		// sometimes embed is set to the default value when not specified in the mapping,
 		// so can't seem to determine if an attribute was explicitly set;
 		// log a warning if embed has a value different from the default.
 		if ( !StringHelper.isEmpty( embed ) &&  !"true".equals( embed ) ) {
 			LOG.embedXmlAttributesNoLongerSupported();
 		}
 		oneToMany.setEmbedded( embed == null || "true".equals( embed ) );
 
 		String notFound = node.attributeValue( "not-found" );
 		oneToMany.setIgnoreNotFound( "ignore".equals( notFound ) );
 
 	}
 
 	public static void bindColumn(Element node, Column column, boolean isNullable) throws MappingException {
 		Attribute lengthNode = node.attribute( "length" );
 		if ( lengthNode != null ) column.setLength( Integer.parseInt( lengthNode.getValue() ) );
 		Attribute scalNode = node.attribute( "scale" );
 		if ( scalNode != null ) column.setScale( Integer.parseInt( scalNode.getValue() ) );
 		Attribute precNode = node.attribute( "precision" );
 		if ( precNode != null ) column.setPrecision( Integer.parseInt( precNode.getValue() ) );
 
 		Attribute nullNode = node.attribute( "not-null" );
 		column.setNullable( nullNode == null ? isNullable : nullNode.getValue().equals( "false" ) );
 
 		Attribute unqNode = node.attribute( "unique" );
 		if ( unqNode != null ) column.setUnique( unqNode.getValue().equals( "true" ) );
 
 		column.setCheckConstraint( node.attributeValue( "check" ) );
 		column.setDefaultValue( node.attributeValue( "default" ) );
 
 		Attribute typeNode = node.attribute( "sql-type" );
 		if ( typeNode != null ) column.setSqlType( typeNode.getValue() );
 
 		String customWrite = node.attributeValue( "write" );
 		if(customWrite != null && !customWrite.matches("[^?]*\\?[^?]*")) {
 			throw new MappingException("write expression must contain exactly one value placeholder ('?') character");
 		}
 		column.setCustomWrite( customWrite );
 		column.setCustomRead( node.attributeValue( "read" ) );
 
 		Element comment = node.element("comment");
 		if (comment!=null) column.setComment( comment.getTextTrim() );
 
 	}
 
 	/**
 	 * Called for arrays and primitive arrays
 	 */
 	public static void bindArray(Element node, Array array, String prefix, String path,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollection( node, array, prefix, path, mappings, inheritedMetas );
 
 		Attribute att = node.attribute( "element-class" );
 		if ( att != null ) array.setElementClassName( getClassName( att, mappings ) );
 
 	}
 
 	private static Class reflectedPropertyClass(String className, String propertyName)
 			throws MappingException {
 		if ( className == null ) return null;
 		return ReflectHelper.reflectedPropertyClass( className, propertyName );
 	}
 
 	public static void bindComposite(Element node, Component component, String path,
 			boolean isNullable, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 		bindComponent(
 				node,
 				component,
 				null,
 				null,
 				path,
 				isNullable,
 				false,
 				mappings,
 				inheritedMetas,
 				false
 		);
 	}
 
 	public static void bindCompositeId(Element node, Component component,
 			PersistentClass persistentClass, String propertyName, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		component.setKey( true );
 
 		String path = StringHelper.qualify(
 				persistentClass.getEntityName(),
 				propertyName == null ? "id" : propertyName );
 
 		bindComponent(
 				node,
 				component,
 				persistentClass.getClassName(),
 				propertyName,
 				path,
 				false,
 				node.attribute( "class" ) == null
 						&& propertyName == null,
 				mappings,
 				inheritedMetas,
 				false
 			);
 
 		if ( "true".equals( node.attributeValue("mapped") ) ) {
 			if ( propertyName!=null ) {
 				throw new MappingException("cannot combine mapped=\"true\" with specified name");
 			}
 			Component mapper = new Component( mappings, persistentClass );
 			bindComponent(
 					node,
 					mapper,
 					persistentClass.getClassName(),
 					null,
 					path,
 					false,
 					true,
 					mappings,
 					inheritedMetas,
 					true
 				);
 			persistentClass.setIdentifierMapper(mapper);
 			Property property = new Property();
 			property.setName( PropertyPath.IDENTIFIER_MAPPER_PROPERTY );
 			property.setNodeName("id");
 			property.setUpdateable(false);
 			property.setInsertable(false);
 			property.setValue(mapper);
 			property.setPropertyAccessorName( "embedded" );
 			persistentClass.addProperty(property);
 		}
 
 	}
 
 	public static void bindComponent(
 			Element node,
 			Component component,
 			String ownerClassName,
 			String parentProperty,
 			String path,
 			boolean isNullable,
 			boolean isEmbedded,
 			Mappings mappings,
 			java.util.Map inheritedMetas,
 			boolean isIdentifierMapper) throws MappingException {
 
 		component.setEmbedded( isEmbedded );
 		component.setRoleName( path );
 
 		inheritedMetas = getMetas( node, inheritedMetas );
 		component.setMetaAttributes( inheritedMetas );
 
 		Attribute classNode = isIdentifierMapper ? null : node.attribute( "class" );
 		if ( classNode != null ) {
 			component.setComponentClassName( getClassName( classNode, mappings ) );
 		}
 		else if ( "dynamic-component".equals( node.getName() ) ) {
 			component.setDynamic( true );
 		}
 		else if ( isEmbedded ) {
 			// an "embedded" component (composite ids and unique)
 			// note that this does not handle nested components
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				component.setComponentClassName( component.getOwner().getClassName() );
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 		else {
 			// todo : again, how *should* this work for non-pojo entities?
 			if ( component.getOwner().hasPojoRepresentation() ) {
 				Class reflectedClass = reflectedPropertyClass( ownerClassName, parentProperty );
 				if ( reflectedClass != null ) {
 					component.setComponentClassName( reflectedClass.getName() );
 				}
 			}
 			else {
 				component.setDynamic(true);
 			}
 		}
 
 		String nodeName = node.attributeValue( "node" );
 		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
 		if ( nodeName == null ) nodeName = component.getOwner().getNodeName();
 		component.setNodeName( nodeName );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = getPropertyName( subnode );
 			String subpath = propertyName == null ? null : StringHelper
 				.qualify( path, propertyName );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						subpath,
 						component.getOwner(),
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) || "key-many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindManyToOne( subnode, (ManyToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, component.getTable(), component.getOwner() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindOneToOne( subnode, (OneToOne) value, relativePath, isNullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, component.getTable() );
 				bindAny( subnode, (Any) value, isNullable, mappings );
 			}
 			else if ( "property".equals( name ) || "key-property".equals( name ) ) {
 				value = new SimpleValue( mappings, component.getTable() );
 				String relativePath;
 				if (isEmbedded) {
 					relativePath = propertyName;
 				}
 				else {
 					relativePath = subpath.substring( component.getOwner().getEntityName().length() + 1 );
 				}
 				bindSimpleValue( subnode, (SimpleValue) value, isNullable, relativePath, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "nested-composite-element".equals( name ) ) {
 				value = new Component( mappings, component ); // a nested composite element
 				bindComponent(
 						subnode,
 						(Component) value,
 						component.getComponentClassName(),
 						propertyName,
 						subpath,
 						isNullable,
 						isEmbedded,
 						mappings,
 						inheritedMetas,
 						isIdentifierMapper
 					);
 			}
 			else if ( "parent".equals( name ) ) {
 				component.setParentProperty( propertyName );
 			}
 
 			if ( value != null ) {
 				Property property = createProperty( value, propertyName, component
 					.getComponentClassName(), subnode, mappings, inheritedMetas );
 				if (isIdentifierMapper) {
 					property.setInsertable(false);
 					property.setUpdateable(false);
 				}
 				component.addProperty( property );
 			}
 		}
 
 		if ( "true".equals( node.attributeValue( "unique" ) ) ) {
 			iter = component.getColumnIterator();
 			ArrayList cols = new ArrayList();
 			while ( iter.hasNext() ) {
 				cols.add( iter.next() );
 			}
 			component.getOwner().getTable().createUniqueKey( cols );
 		}
 
 		iter = node.elementIterator( "tuplizer" );
 		while ( iter.hasNext() ) {
 			final Element tuplizerElem = ( Element ) iter.next();
 			EntityMode mode = EntityMode.parse( tuplizerElem.attributeValue( "entity-mode" ) );
 			component.addTuplizer( mode, tuplizerElem.attributeValue( "class" ) );
 		}
 	}
 
 	public static String getTypeFromXML(Element node) throws MappingException {
 		// TODO: handle TypeDefs
 		Attribute typeNode = node.attribute( "type" );
 		if ( typeNode == null ) typeNode = node.attribute( "id-type" ); // for an any
 		if ( typeNode == null ) return null; // we will have to use reflection
 		return typeNode.getValue();
 	}
 
 	private static void initOuterJoinFetchSetting(Element node, Fetchable model) {
 		Attribute fetchNode = node.attribute( "fetch" );
 		final FetchMode fetchStyle;
 		boolean lazy = true;
 		if ( fetchNode == null ) {
 			Attribute jfNode = node.attribute( "outer-join" );
 			if ( jfNode == null ) {
 				if ( "many-to-many".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// default to join and non-lazy for the "second join"
 					// of the many-to-many
 					lazy = false;
 					fetchStyle = FetchMode.JOIN;
 				}
 				else if ( "one-to-one".equals( node.getName() ) ) {
 					//NOTE SPECIAL CASE:
 					// one-to-one constrained=false cannot be proxied,
 					// so default to join and non-lazy
 					lazy = ( (OneToOne) model ).isConstrained();
 					fetchStyle = lazy ? FetchMode.DEFAULT : FetchMode.JOIN;
 				}
 				else {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 			}
 			else {
-				// use old (HB 2.1) defaults if outer-join is specified
-				String eoj = jfNode.getValue();
-				if ( "auto".equals( eoj ) ) {
-					fetchStyle = FetchMode.DEFAULT;
+				if ( "many-to-many".equals( node.getName() ) ) {
+					//NOTE <many-to-many outer-join="..." is deprecated.:
+					// Default to join and non-lazy for the "second join"
+					// of the many-to-many
+					LOG.deprecatedManyToManyOuterJoin();
+					lazy = false;
+					fetchStyle = FetchMode.JOIN;
 				}
 				else {
-					boolean join = "true".equals( eoj );
-					fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
+					// use old (HB 2.1) defaults if outer-join is specified
+					String eoj = jfNode.getValue();
+					if ( "auto".equals( eoj ) ) {
+						fetchStyle = FetchMode.DEFAULT;
+					}
+					else {
+						boolean join = "true".equals( eoj );
+						fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
+					}
 				}
 			}
 		}
 		else {
-			boolean join = "join".equals( fetchNode.getValue() );
-			//lazy = !join;
-			fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
+			if ( "many-to-many".equals( node.getName() ) ) {
+				//NOTE <many-to-many fetch="..." is deprecated.:
+				// Default to join and non-lazy for the "second join"
+				// of the many-to-many
+				LOG.deprecatedManyToManyFetch();
+				lazy = false;
+				fetchStyle = FetchMode.JOIN;
+			}
+			else {
+				boolean join = "join".equals( fetchNode.getValue() );
+				//lazy = !join;
+				fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
+			}
 		}
 		model.setFetchMode( fetchStyle );
 		model.setLazy(lazy);
 	}
 
 	private static void makeIdentifier(Element node, SimpleValue model, Mappings mappings) {
 
 		// GENERATOR
 		Element subnode = node.element( "generator" );
 		if ( subnode != null ) {
 			final String generatorClass = subnode.attributeValue( "class" );
 			model.setIdentifierGeneratorStrategy( generatorClass );
 
 			Properties params = new Properties();
 			// YUCK!  but cannot think of a clean way to do this given the string-config based scheme
 			params.put( PersistentIdentifierGenerator.IDENTIFIER_NORMALIZER, mappings.getObjectNameNormalizer() );
 
 			if ( mappings.getSchemaName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.SCHEMA,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getSchemaName() )
 				);
 			}
 			if ( mappings.getCatalogName() != null ) {
 				params.setProperty(
 						PersistentIdentifierGenerator.CATALOG,
 						mappings.getObjectNameNormalizer().normalizeIdentifierQuoting( mappings.getCatalogName() )
 				);
 			}
 
 			Iterator iter = subnode.elementIterator( "param" );
 			while ( iter.hasNext() ) {
 				Element childNode = (Element) iter.next();
 				params.setProperty( childNode.attributeValue( "name" ), childNode.getTextTrim() );
 			}
 
 			model.setIdentifierGeneratorProperties( params );
 		}
 
 		model.getTable().setIdentifierValue( model );
 
 		// ID UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			if ( "assigned".equals( model.getIdentifierGeneratorStrategy() ) ) {
 				model.setNullValue( "undefined" );
 			}
 			else {
 				model.setNullValue( null );
 			}
 		}
 	}
 
 	private static final void makeVersion(Element node, SimpleValue model) {
 
 		// VERSION UNSAVED-VALUE
 		Attribute nullValueNode = node.attribute( "unsaved-value" );
 		if ( nullValueNode != null ) {
 			model.setNullValue( nullValueNode.getValue() );
 		}
 		else {
 			model.setNullValue( "undefined" );
 		}
 
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 		createClassProperties(node, persistentClass, mappings, inheritedMetas, null, true, true, false);
 	}
 
 	protected static void createClassProperties(Element node, PersistentClass persistentClass,
 			Mappings mappings, java.util.Map inheritedMetas, UniqueKey uniqueKey,
 			boolean mutable, boolean nullable, boolean naturalId) throws MappingException {
 
 		String entityName = persistentClass.getEntityName();
 		Table table = persistentClass.getTable();
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = subnode.attributeValue( "name" );
 
 			CollectionType collectType = CollectionType.collectionTypeFromString( name );
 			Value value = null;
 			if ( collectType != null ) {
 				Collection collection = collectType.create(
 						subnode,
 						StringHelper.qualify( entityName, propertyName ),
 						persistentClass,
 						mappings, inheritedMetas
 					);
 				mappings.addCollection( collection );
 				value = collection;
 			}
 			else if ( "many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, table );
 				bindManyToOne( subnode, (ManyToOne) value, propertyName, nullable, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, table );
 				bindAny( subnode, (Any) value, nullable, mappings );
 			}
 			else if ( "one-to-one".equals( name ) ) {
 				value = new OneToOne( mappings, table, persistentClass );
 				bindOneToOne( subnode, (OneToOne) value, propertyName, true, mappings );
 			}
 			else if ( "property".equals( name ) ) {
 				value = new SimpleValue( mappings, table );
 				bindSimpleValue( subnode, (SimpleValue) value, nullable, propertyName, mappings );
 			}
 			else if ( "component".equals( name )
 				|| "dynamic-component".equals( name )
 				|| "properties".equals( name ) ) {
 				String subpath = StringHelper.qualify( entityName, propertyName );
 				value = new Component( mappings, persistentClass );
 
 				bindComponent(
 						subnode,
 						(Component) value,
 						persistentClass.getClassName(),
 						propertyName,
 						subpath,
 						true,
 						"properties".equals( name ),
 						mappings,
 						inheritedMetas,
 						false
 					);
 			}
 			else if ( "join".equals( name ) ) {
 				Join join = new Join();
 				join.setPersistentClass( persistentClass );
 				bindJoin( subnode, join, mappings, inheritedMetas );
 				persistentClass.addJoin( join );
 			}
 			else if ( "subclass".equals( name ) ) {
 				handleSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "joined-subclass".equals( name ) ) {
 				handleJoinedSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "union-subclass".equals( name ) ) {
 				handleUnionSubclass( persistentClass, mappings, subnode, inheritedMetas );
 			}
 			else if ( "filter".equals( name ) ) {
 				parseFilter( subnode, persistentClass, mappings );
 			}
 			else if ( "natural-id".equals( name ) ) {
 				UniqueKey uk = new UniqueKey();
 				uk.setTable(table);
 				//by default, natural-ids are "immutable" (constant)
 				boolean mutableId = "true".equals( subnode.attributeValue("mutable") );
 				createClassProperties(
 						subnode,
 						persistentClass,
 						mappings,
 						inheritedMetas,
 						uk,
 						mutableId,
 						false,
 						true
 					);
 				uk.setName( Constraint.generateName( uk.generatedConstraintNamePrefix(),
 						table, uk.getColumns() ) );
 				table.addUniqueKey(uk);
 			}
 			else if ( "query".equals(name) ) {
 				bindNamedQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "sql-query".equals(name) ) {
 				bindNamedSQLQuery(subnode, persistentClass.getEntityName(), mappings);
 			}
 			else if ( "resultset".equals(name) ) {
 				bindResultSetMappingDefinition( subnode, persistentClass.getEntityName(), mappings );
 			}
 
 			if ( value != null ) {
 				final Property property = createProperty(
 						value,
 						propertyName,
 						persistentClass.getClassName(),
 						subnode,
 						mappings,
 						inheritedMetas
 				);
 				if ( !mutable ) {
 					property.setUpdateable(false);
 				}
 				if ( naturalId ) {
 					property.setNaturalIdentifier( true );
 				}
 				persistentClass.addProperty( property );
 				if ( uniqueKey!=null ) {
 					uniqueKey.addColumns( property.getColumnIterator() );
 				}
 			}
 
 		}
 	}
 
 	private static Property createProperty(
 			final Value value,
 	        final String propertyName,
 			final String className,
 	        final Element subnode,
 	        final Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		if ( StringHelper.isEmpty( propertyName ) ) {
 			throw new MappingException( subnode.getName() + " mapping must defined a name attribute [" + className + "]" );
 		}
 
 		value.setTypeUsingReflection( className, propertyName );
 
 		// this is done here 'cos we might only know the type here (ugly!)
 		// TODO: improve this a lot:
 		if ( value instanceof ToOne ) {
 			ToOne toOne = (ToOne) value;
 			String propertyRef = toOne.getReferencedPropertyName();
 			if ( propertyRef != null ) {
 				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
 			}
 			toOne.setCascadeDeleteEnabled( "cascade".equals( subnode.attributeValue( "on-delete" ) ) );
 		}
 		else if ( value instanceof Collection ) {
 			Collection coll = (Collection) value;
 			String propertyRef = coll.getReferencedPropertyName();
 			// not necessarily a *unique* property reference
 			if ( propertyRef != null ) {
 				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
 			}
 		}
 
 		value.createForeignKey();
 		Property prop = new Property();
 		prop.setValue( value );
 		bindProperty( subnode, prop, mappings, inheritedMetas );
 		return prop;
 	}
 
 	private static void handleUnionSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		UnionSubclass subclass = new UnionSubclass( model );
 		bindUnionSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleJoinedSubclass(PersistentClass model, Mappings mappings,
 			Element subnode, java.util.Map inheritedMetas) throws MappingException {
 		JoinedSubclass subclass = new JoinedSubclass( model );
 		bindJoinedSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	private static void handleSubclass(PersistentClass model, Mappings mappings, Element subnode,
 			java.util.Map inheritedMetas) throws MappingException {
 		Subclass subclass = new SingleTableSubclass( model );
 		bindSubclass( subnode, subclass, mappings, inheritedMetas );
 		model.addSubclass( subclass );
 		mappings.addClass( subclass );
 	}
 
 	/**
 	 * Called for Lists, arrays, primitive arrays
 	 */
 	public static void bindListSecondPass(Element node, List list, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, list, classes, mappings, inheritedMetas );
 
 		Element subnode = node.element( "list-index" );
 		if ( subnode == null ) subnode = node.element( "index" );
 		SimpleValue iv = new SimpleValue( mappings, list.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				iv,
 				list.isOneToMany(),
 				IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 				mappings
 		);
 		iv.setTypeName( "integer" );
 		list.setIndex( iv );
 		String baseIndex = subnode.attributeValue( "base" );
 		if ( baseIndex != null ) list.setBaseIndex( Integer.parseInt( baseIndex ) );
 		list.setIndexNodeName( subnode.attributeValue("node") );
 
 		if ( list.isOneToMany() && !list.getKey().isNullable() && !list.isInverse() ) {
 			String entityName = ( (OneToMany) list.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + list.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( list.getRole() );
 			ib.setEntityName( list.getOwner().getEntityName() );
 			ib.setValue( list.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	public static void bindIdentifierCollectionSecondPass(Element node,
 			IdentifierCollection collection, java.util.Map persistentClasses, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, collection, persistentClasses, mappings, inheritedMetas );
 
 		Element subnode = node.element( "collection-id" );
 		SimpleValue id = new SimpleValue( mappings, collection.getCollectionTable() );
 		bindSimpleValue(
 				subnode,
 				id,
 				false,
 				IdentifierCollection.DEFAULT_IDENTIFIER_COLUMN_NAME,
 				mappings
 			);
 		collection.setIdentifier( id );
 		makeIdentifier( subnode, id, mappings );
 
 	}
 
 	/**
 	 * Called for Maps
 	 */
 	public static void bindMapSecondPass(Element node, Map map, java.util.Map classes,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindCollectionSecondPass( node, map, classes, mappings, inheritedMetas );
 
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "index".equals( name ) || "map-key".equals( name ) ) {
 				SimpleValue value = new SimpleValue( mappings, map.getCollectionTable() );
 				bindSimpleValue(
 						subnode,
 						value,
 						map.isOneToMany(),
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						mappings
 					);
 				if ( !value.isTypeSpecified() ) {
 					throw new MappingException( "map index element must specify a type: "
 						+ map.getRole() );
 				}
 				map.setIndex( value );
 				map.setIndexNodeName( subnode.attributeValue("node") );
 			}
 			else if ( "index-many-to-many".equals( name ) || "map-key-many-to-many".equals( name ) ) {
 				ManyToOne mto = new ManyToOne( mappings, map.getCollectionTable() );
 				bindManyToOne(
 						subnode,
 						mto,
 						IndexedCollection.DEFAULT_INDEX_COLUMN_NAME,
 						map.isOneToMany(),
 						mappings
 					);
 				map.setIndex( mto );
 
 			}
 			else if ( "composite-index".equals( name ) || "composite-map-key".equals( name ) ) {
 				Component component = new Component( mappings, map );
 				bindComposite(
 						subnode,
 						component,
 						map.getRole() + ".index",
 						map.isOneToMany(),
 						mappings,
 						inheritedMetas
 					);
 				map.setIndex( component );
 			}
 			else if ( "index-many-to-any".equals( name ) ) {
 				Any any = new Any( mappings, map.getCollectionTable() );
 				bindAny( subnode, any, map.isOneToMany(), mappings );
 				map.setIndex( any );
 			}
 		}
 
 		// TODO: this is a bit of copy/paste from IndexedCollection.createPrimaryKey()
 		boolean indexIsFormula = false;
 		Iterator colIter = map.getIndex().getColumnIterator();
 		while ( colIter.hasNext() ) {
 			if ( ( (Selectable) colIter.next() ).isFormula() ) indexIsFormula = true;
 		}
 
 		if ( map.isOneToMany() && !map.getKey().isNullable() && !map.isInverse() && !indexIsFormula ) {
 			String entityName = ( (OneToMany) map.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			IndexBackref ib = new IndexBackref();
 			ib.setName( '_' + map.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "IndexBackref" );
 			ib.setUpdateable( false );
 			ib.setSelectable( false );
 			ib.setCollectionRole( map.getRole() );
 			ib.setEntityName( map.getOwner().getEntityName() );
 			ib.setValue( map.getIndex() );
 			// ( (Column) ( (SimpleValue) ic.getIndex() ).getColumnIterator().next()
 			// ).setNullable(false);
 			referenced.addProperty( ib );
 		}
 	}
 
 	/**
 	 * Called for all collections
 	 */
 	public static void bindCollectionSecondPass(Element node, Collection collection,
 			java.util.Map persistentClasses, Mappings mappings, java.util.Map inheritedMetas)
 			throws MappingException {
 
 		if ( collection.isOneToMany() ) {
 			OneToMany oneToMany = (OneToMany) collection.getElement();
 			String assocClass = oneToMany.getReferencedEntityName();
 			PersistentClass persistentClass = (PersistentClass) persistentClasses.get( assocClass );
 			if ( persistentClass == null ) {
 				throw new MappingException( "Association references unmapped class: " + assocClass );
 			}
 			oneToMany.setAssociatedClass( persistentClass );
 			collection.setCollectionTable( persistentClass.getTable() );
 
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName() );
 			}
 		}
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) {
 			collection.getCollectionTable().addCheckConstraint( chNode.getValue() );
 		}
 
 		// contained elements:
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 
 			if ( "key".equals( name ) ) {
 				KeyValue keyVal;
 				String propRef = collection.getReferencedPropertyName();
 				if ( propRef == null ) {
 					keyVal = collection.getOwner().getIdentifier();
 				}
 				else {
 					keyVal = (KeyValue) collection.getOwner().getRecursiveProperty( propRef ).getValue();
 				}
 				SimpleValue key = new DependantValue( mappings, collection.getCollectionTable(), keyVal );
 				key.setCascadeDeleteEnabled( "cascade"
 					.equals( subnode.attributeValue( "on-delete" ) ) );
 				bindSimpleValue(
 						subnode,
 						key,
 						collection.isOneToMany(),
 						Collection.DEFAULT_KEY_COLUMN_NAME,
 						mappings
 					);
 				collection.setKey( key );
 
 				Attribute notNull = subnode.attribute( "not-null" );
 				( (DependantValue) key ).setNullable( notNull == null
 					|| notNull.getValue().equals( "false" ) );
 				Attribute updateable = subnode.attribute( "update" );
 				( (DependantValue) key ).setUpdateable( updateable == null
 					|| updateable.getValue().equals( "true" ) );
 
 			}
 			else if ( "element".equals( name ) ) {
 				SimpleValue elt = new SimpleValue( mappings, collection.getCollectionTable() );
 				collection.setElement( elt );
 				bindSimpleValue(
 						subnode,
 						elt,
 						true,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						mappings
 					);
 			}
 			else if ( "many-to-many".equals( name ) ) {
 				ManyToOne element = new ManyToOne( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindManyToOne(
 						subnode,
 						element,
 						Collection.DEFAULT_ELEMENT_COLUMN_NAME,
 						false,
 						mappings
 					);
 				bindManyToManySubelements( collection, subnode, mappings );
 			}
 			else if ( "composite-element".equals( name ) ) {
 				Component element = new Component( mappings, collection );
 				collection.setElement( element );
 				bindComposite(
 						subnode,
 						element,
 						collection.getRole() + ".element",
 						true,
 						mappings,
 						inheritedMetas
 					);
 			}
 			else if ( "many-to-any".equals( name ) ) {
 				Any element = new Any( mappings, collection.getCollectionTable() );
 				collection.setElement( element );
 				bindAny( subnode, element, true, mappings );
 			}
 			else if ( "cache".equals( name ) ) {
 				collection.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
 				collection.setCacheRegionName( subnode.attributeValue( "region" ) );
 			}
 
 			String nodeName = subnode.attributeValue( "node" );
 			if ( nodeName != null ) collection.setElementNodeName( nodeName );
 
 		}
 
 		if ( collection.isOneToMany()
 			&& !collection.isInverse()
 			&& !collection.getKey().isNullable() ) {
 			// for non-inverse one-to-many, with a not-null fk, add a backref!
 			String entityName = ( (OneToMany) collection.getElement() ).getReferencedEntityName();
 			PersistentClass referenced = mappings.getClass( entityName );
 			Backref prop = new Backref();
 			prop.setName( '_' + collection.getOwnerEntityName() + "." + node.attributeValue( "name" ) + "Backref" );
 			prop.setUpdateable( false );
 			prop.setSelectable( false );
 			prop.setCollectionRole( collection.getRole() );
 			prop.setEntityName( collection.getOwner().getEntityName() );
 			prop.setValue( collection.getKey() );
 			referenced.addProperty( prop );
 		}
 	}
 
 	private static void bindManyToManySubelements(
 	        Collection collection,
 	        Element manyToManyNode,
 	        Mappings model) throws MappingException {
 		// Bind the where
 		Attribute where = manyToManyNode.attribute( "where" );
 		String whereCondition = where == null ? null : where.getValue();
 		collection.setManyToManyWhere( whereCondition );
 
 		// Bind the order-by
 		Attribute order = manyToManyNode.attribute( "order-by" );
 		String orderFragment = order == null ? null : order.getValue();
 		collection.setManyToManyOrdering( orderFragment );
 
 		// Bind the filters
 		Iterator filters = manyToManyNode.elementIterator( "filter" );
 		if ( ( filters.hasNext() || whereCondition != null ) &&
 		        collection.getFetchMode() == FetchMode.JOIN &&
 		        collection.getElement().getFetchMode() != FetchMode.JOIN ) {
 			throw new MappingException(
 			        "many-to-many defining filter or where without join fetching " +
 			        "not valid within collection using join fetching [" + collection.getRole() + "]"
 				);
 		}
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		while ( filters.hasNext() ) {
 			final Element filterElement = ( Element ) filters.next();
 			final String name = filterElement.attributeValue( "name" );
 			String condition = filterElement.getTextTrim();
 			if ( StringHelper.isEmpty(condition) ) condition = filterElement.attributeValue( "condition" );
 			if ( StringHelper.isEmpty(condition) ) {
 				condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 			}
 			if ( condition==null) {
 				throw new MappingException("no filter condition found for filter: " + name);
 			}
 			Iterator aliasesIterator = filterElement.elementIterator("aliases");
 			java.util.Map<String, String> aliasTables = new HashMap<String, String>();
 			while (aliasesIterator.hasNext()){
 				Element alias = (Element) aliasesIterator.next();
 				aliasTables.put(alias.attributeValue("alias"), alias.attributeValue("table"));
 			}
 			if ( debugEnabled ) {
 				LOG.debugf( "Applying many-to-many filter [%s] as [%s] to role [%s]", name, condition, collection.getRole() );
 			}
 			String autoAliasInjectionText = filterElement.attributeValue("autoAliasInjection");
 			boolean autoAliasInjection = StringHelper.isEmpty(autoAliasInjectionText) ? true : Boolean.parseBoolean(autoAliasInjectionText);
 			collection.addManyToManyFilter(name, condition, autoAliasInjection, aliasTables, null);
 		}
 	}
 
 	private static void bindNamedQuery(Element queryElem, String path, Mappings mappings) {
 		String queryName = queryElem.attributeValue( "name" );
 		if (path!=null) queryName = path + '.' + queryName;
 		String query = queryElem.getText();
 		LOG.debugf( "Named query: %s -> %s", queryName, query );
 
 		boolean cacheable = "true".equals( queryElem.attributeValue( "cacheable" ) );
 		String region = queryElem.attributeValue( "cache-region" );
 		Attribute tAtt = queryElem.attribute( "timeout" );
 		Integer timeout = tAtt == null ? null : Integer.valueOf( tAtt.getValue() );
 		Attribute fsAtt = queryElem.attribute( "fetch-size" );
 		Integer fetchSize = fsAtt == null ? null : Integer.valueOf( fsAtt.getValue() );
 		Attribute roAttr = queryElem.attribute( "read-only" );
 		boolean readOnly = roAttr != null && "true".equals( roAttr.getValue() );
 		Attribute cacheModeAtt = queryElem.attribute( "cache-mode" );
 		String cacheMode = cacheModeAtt == null ? null : cacheModeAtt.getValue();
 		Attribute cmAtt = queryElem.attribute( "comment" );
 		String comment = cmAtt == null ? null : cmAtt.getValue();
 
 		NamedQueryDefinition namedQuery = new NamedQueryDefinitionBuilder().setName( queryName )
 				.setQuery( query )
 				.setCacheable( cacheable )
 				.setCacheRegion( region )
 				.setTimeout( timeout )
 				.setFetchSize( fetchSize )
 				.setFlushMode( FlushMode.interpretExternalSetting( queryElem.attributeValue( "flush-mode" ) ) )
 				.setCacheMode( CacheMode.interpretExternalSetting( cacheMode ) )
 				.setReadOnly( readOnly )
 				.setComment( comment )
 				.setParameterTypes( getParameterTypes( queryElem ) )
 				.createNamedQueryDefinition();
 
 		mappings.addQuery( namedQuery.getName(), namedQuery );
 	}
 
 	public static java.util.Map getParameterTypes(Element queryElem) {
 		java.util.Map result = new java.util.LinkedHashMap();
 		Iterator iter = queryElem.elementIterator("query-param");
 		while ( iter.hasNext() ) {
 			Element element = (Element) iter.next();
 			result.put( element.attributeValue("name"), element.attributeValue("type") );
 		}
 		return result;
 	}
 
 	private static void bindResultSetMappingDefinition(Element resultSetElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new ResultSetMappingSecondPass( resultSetElem, path, mappings ) );
 	}
 
 	private static void bindNamedSQLQuery(Element queryElem, String path, Mappings mappings) {
 		mappings.addSecondPass( new NamedSQLQuerySecondPass( queryElem, path, mappings ) );
 	}
 
 	private static String getPropertyName(Element node) {
 		return node.attributeValue( "name" );
 	}
 
 	private static PersistentClass getSuperclass(Mappings mappings, Element subnode)
 			throws MappingException {
 		String extendsName = subnode.attributeValue( "extends" );
 		PersistentClass superModel = mappings.getClass( extendsName );
 		if ( superModel == null ) {
 			String qualifiedExtendsName = getClassName( extendsName, mappings );
 			superModel = mappings.getClass( qualifiedExtendsName );
 		}
 
 		if ( superModel == null ) {
 			throw new MappingException( "Cannot extend unmapped class " + extendsName );
 		}
 		return superModel;
 	}
 
 	static class CollectionSecondPass extends org.hibernate.cfg.CollectionSecondPass {
 		Element node;
 
 		CollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super(mappings, collection, inheritedMetas);
 			this.node = node;
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindCollectionSecondPass(
 					node,
 					collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 	}
 
 	static class IdentifierCollectionSecondPass extends CollectionSecondPass {
 		IdentifierCollectionSecondPass(Element node, Mappings mappings, Collection collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindIdentifierCollectionSecondPass(
 					node,
 					(IdentifierCollection) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	static class MapSecondPass extends CollectionSecondPass {
 		MapSecondPass(Element node, Mappings mappings, Map collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindMapSecondPass(
 					node,
 					(Map) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 
 	static class ManyToOneSecondPass implements SecondPass {
 		private final ManyToOne manyToOne;
 
 		ManyToOneSecondPass(ManyToOne manyToOne) {
 			this.manyToOne = manyToOne;
 		}
 
 		public void doSecondPass(java.util.Map persistentClasses) throws MappingException {
 			manyToOne.createPropertyRefConstraints(persistentClasses);
 		}
 
 	}
 
 	static class ListSecondPass extends CollectionSecondPass {
 		ListSecondPass(Element node, Mappings mappings, List collection, java.util.Map inheritedMetas) {
 			super( node, mappings, collection, inheritedMetas );
 		}
 
 		public void secondPass(java.util.Map persistentClasses, java.util.Map inheritedMetas)
 				throws MappingException {
 			HbmBinder.bindListSecondPass(
 					node,
 					(List) collection,
 					persistentClasses,
 					mappings,
 					inheritedMetas
 				);
 		}
 
 	}
 
 	// This inner class implements a case statement....perhaps im being a bit over-clever here
 	abstract static class CollectionType {
 		private String xmlTag;
 
 		public abstract Collection create(Element node, String path, PersistentClass owner,
 				Mappings mappings, java.util.Map inheritedMetas) throws MappingException;
 
 		CollectionType(String xmlTag) {
 			this.xmlTag = xmlTag;
 		}
 
 		public String toString() {
 			return xmlTag;
 		}
 
 		private static final CollectionType MAP = new CollectionType( "map" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Map map = new Map( mappings, owner );
 				bindCollection( node, map, owner.getEntityName(), path, mappings, inheritedMetas );
 				return map;
 			}
 		};
 		private static final CollectionType SET = new CollectionType( "set" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Set set = new Set( mappings, owner );
 				bindCollection( node, set, owner.getEntityName(), path, mappings, inheritedMetas );
 				return set;
 			}
 		};
 		private static final CollectionType LIST = new CollectionType( "list" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				List list = new List( mappings, owner );
 				bindCollection( node, list, owner.getEntityName(), path, mappings, inheritedMetas );
 				return list;
 			}
 		};
 		private static final CollectionType BAG = new CollectionType( "bag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Bag bag = new Bag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType IDBAG = new CollectionType( "idbag" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				IdentifierBag bag = new IdentifierBag( mappings, owner );
 				bindCollection( node, bag, owner.getEntityName(), path, mappings, inheritedMetas );
 				return bag;
 			}
 		};
 		private static final CollectionType ARRAY = new CollectionType( "array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				Array array = new Array( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final CollectionType PRIMITIVE_ARRAY = new CollectionType( "primitive-array" ) {
 			public Collection create(Element node, String path, PersistentClass owner,
 					Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 				PrimitiveArray array = new PrimitiveArray( mappings, owner );
 				bindArray( node, array, owner.getEntityName(), path, mappings, inheritedMetas );
 				return array;
 			}
 		};
 		private static final HashMap INSTANCES = new HashMap();
 
 		static {
 			INSTANCES.put( MAP.toString(), MAP );
 			INSTANCES.put( BAG.toString(), BAG );
 			INSTANCES.put( IDBAG.toString(), IDBAG );
 			INSTANCES.put( SET.toString(), SET );
 			INSTANCES.put( LIST.toString(), LIST );
 			INSTANCES.put( ARRAY.toString(), ARRAY );
 			INSTANCES.put( PRIMITIVE_ARRAY.toString(), PRIMITIVE_ARRAY );
 		}
 
 		public static CollectionType collectionTypeFromString(String xmlTagName) {
 			return (CollectionType) INSTANCES.get( xmlTagName );
 		}
 	}
 
 	private static OptimisticLockStyle getOptimisticLockStyle(Attribute olAtt) throws MappingException {
 		if ( olAtt == null ) {
 			return OptimisticLockStyle.VERSION;
 		}
 
 		final String olMode = olAtt.getValue();
 		if ( olMode == null || "version".equals( olMode ) ) {
 			return OptimisticLockStyle.VERSION;
 		}
 		else if ( "dirty".equals( olMode ) ) {
 			return OptimisticLockStyle.DIRTY;
 		}
 		else if ( "all".equals( olMode ) ) {
 			return OptimisticLockStyle.ALL;
 		}
 		else if ( "none".equals( olMode ) ) {
 			return OptimisticLockStyle.NONE;
 		}
 		else {
 			throw new MappingException( "Unsupported optimistic-lock style: " + olMode );
 		}
 	}
 
 	private static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta) {
 		return getMetas( node, inheritedMeta, false );
 	}
 
 	public static final java.util.Map getMetas(Element node, java.util.Map inheritedMeta,
 			boolean onlyInheritable) {
 		java.util.Map map = new HashMap();
 		map.putAll( inheritedMeta );
 
 		Iterator iter = node.elementIterator( "meta" );
 		while ( iter.hasNext() ) {
 			Element metaNode = (Element) iter.next();
 			boolean inheritable = Boolean
 				.valueOf( metaNode.attributeValue( "inherit" ) )
 				.booleanValue();
 			if ( onlyInheritable && !inheritable ) {
 				continue;
 			}
 			String name = metaNode.attributeValue( "attribute" );
 
 			MetaAttribute meta = (MetaAttribute) map.get( name );
 			MetaAttribute inheritedAttribute = (MetaAttribute) inheritedMeta.get( name );
 			if ( meta == null  ) {
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			} else if (meta == inheritedAttribute) { // overriding inherited meta attribute. HBX-621 & HBX-793
 				meta = new MetaAttribute( name );
 				map.put( name, meta );
 			}
 			meta.addValue( metaNode.getText() );
 		}
 		return map;
 	}
 
 	public static String getEntityName(Element elem, Mappings model) {
 		String entityName = elem.attributeValue( "entity-name" );
 		return entityName == null ? getClassName( elem.attribute( "class" ), model ) : entityName;
 	}
 
 	private static String getClassName(Attribute att, Mappings model) {
 		if ( att == null ) return null;
 		return getClassName( att.getValue(), model );
 	}
 
 	public static String getClassName(String unqualifiedName, Mappings model) {
 		return getClassName( unqualifiedName, model.getDefaultPackage() );
 	}
 
 	public static String getClassName(String unqualifiedName, String defaultPackage) {
 		if ( unqualifiedName == null ) return null;
 		if ( unqualifiedName.indexOf( '.' ) < 0 && defaultPackage != null ) {
 			return defaultPackage + '.' + unqualifiedName;
 		}
 		return unqualifiedName;
 	}
 
 	private static void parseFilterDef(Element element, Mappings mappings) {
 		String name = element.attributeValue( "name" );
 		LOG.debugf( "Parsing filter-def [%s]", name );
 		String defaultCondition = element.getTextTrim();
 		if ( StringHelper.isEmpty( defaultCondition ) ) {
 			defaultCondition = element.attributeValue( "condition" );
 		}
 		HashMap paramMappings = new HashMap();
 		Iterator params = element.elementIterator( "filter-param" );
 		while ( params.hasNext() ) {
 			final Element param = (Element) params.next();
 			final String paramName = param.attributeValue( "name" );
 			final String paramType = param.attributeValue( "type" );
 			LOG.debugf( "Adding filter parameter : %s -> %s", paramName, paramType );
 			final Type heuristicType = mappings.getTypeResolver().heuristicType( paramType );
 			LOG.debugf( "Parameter heuristic type : %s", heuristicType );
 			paramMappings.put( paramName, heuristicType );
 		}
 		LOG.debugf( "Parsed filter-def [%s]", name );
 		FilterDefinition def = new FilterDefinition( name, defaultCondition, paramMappings );
 		mappings.addFilterDefinition( def );
 	}
 
 	private static void parseFilter(Element filterElement, Filterable filterable, Mappings model) {
 		final String name = filterElement.attributeValue( "name" );
 		String condition = filterElement.getTextTrim();
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = filterElement.attributeValue( "condition" );
 		}
 		//TODO: bad implementation, cos it depends upon ordering of mapping doc
 		//      fixing this requires that Collection/PersistentClass gain access
 		//      to the Mappings reference from Configuration (or the filterDefinitions
 		//      map directly) sometime during Configuration.build
 		//      (after all the types/filter-defs are known and before building
 		//      persisters).
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 		}
 		if ( condition==null) {
 			throw new MappingException("no filter condition found for filter: " + name);
 		}
 		Iterator aliasesIterator = filterElement.elementIterator("aliases");
 		java.util.Map<String, String> aliasTables = new HashMap<String, String>();
 		while (aliasesIterator.hasNext()){
 			Element alias = (Element) aliasesIterator.next();
 			aliasTables.put(alias.attributeValue("alias"), alias.attributeValue("table"));
 		}
 		LOG.debugf( "Applying filter [%s] as [%s]", name, condition );
 		String autoAliasInjectionText = filterElement.attributeValue("autoAliasInjection");
 		boolean autoAliasInjection = StringHelper.isEmpty(autoAliasInjectionText) ? true : Boolean.parseBoolean(autoAliasInjectionText);
 		filterable.addFilter(name, condition, autoAliasInjection, aliasTables, null);
 	}
 
 	private static void parseFetchProfile(Element element, Mappings mappings, String containingEntityName) {
 		String profileName = element.attributeValue( "name" );
 		FetchProfile profile = mappings.findOrCreateFetchProfile( profileName, MetadataSource.HBM );
 		Iterator itr = element.elementIterator( "fetch" );
 		while ( itr.hasNext() ) {
 			final Element fetchElement = ( Element ) itr.next();
 			final String association = fetchElement.attributeValue( "association" );
 			final String style = fetchElement.attributeValue( "style" );
 			String entityName = fetchElement.attributeValue( "entity" );
 			if ( entityName == null ) {
 				entityName = containingEntityName;
 			}
 			if ( entityName == null ) {
 				throw new MappingException( "could not determine entity for fetch-profile fetch [" + profileName + "]:[" + association + "]" );
 			}
 			profile.addFetch( entityName, association, style );
 		}
 	}
 
 	private static String getSubselect(Element element) {
 		String subselect = element.attributeValue( "subselect" );
 		if ( subselect != null ) {
 			return subselect;
 		}
 		else {
 			Element subselectElement = element.element( "subselect" );
 			return subselectElement == null ? null : subselectElement.getText();
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
index 248b2acb9a..d61f940ddc 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
@@ -657,1001 +657,1009 @@ public interface CoreMessageLogger extends BasicLogger {
 	@Message(value = "processEqualityExpression() : No expression to process!", id = 203)
 	void processEqualityExpression();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Processing PersistenceUnitInfo [\n\tname: %s\n\t...]", id = 204)
 	void processingPersistenceUnitInfoName(String persistenceUnitName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Loaded properties from resource hibernate.properties: %s", id = 205)
 	void propertiesLoaded(Properties maskOut);
 
 	@LogMessage(level = INFO)
 	@Message(value = "hibernate.properties not found", id = 206)
 	void propertiesNotFound();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Property %s not found in class but described in <mapping-file/> (possible typo error)", id = 207)
 	void propertyNotFound(String property);
 
 	@LogMessage(level = WARN)
 	@Message(value = "%s has been deprecated in favor of %s; that provider will be used instead.", id = 208)
 	void providerClassDeprecated(String providerClassName,
 								 String actualProviderClassName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "proxool properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.",
 			id = 209)
 	void proxoolProviderClassNotFound(String proxoolProviderClassName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Queries executed to database: %s", id = 210)
 	void queriesExecuted(long queryExecutionCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Query cache hits: %s", id = 213)
 	void queryCacheHits(long queryCacheHitCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Query cache misses: %s", id = 214)
 	void queryCacheMisses(long queryCacheMissCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Query cache puts: %s", id = 215)
 	void queryCachePuts(long queryCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "RDMSOS2200Dialect version: 1.0", id = 218)
 	void rdmsOs2200Dialect();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Reading mappings from cache file: %s", id = 219)
 	void readingCachedMappings(File cachedFile);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Reading mappings from file: %s", id = 220)
 	void readingMappingsFromFile(String path);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Reading mappings from resource: %s", id = 221)
 	void readingMappingsFromResource(String resourceName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "read-only cache configured for mutable collection [%s]", id = 222)
 	void readOnlyCacheConfiguredForMutableCollection(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Recognized obsolete hibernate namespace %s. Use namespace %s instead. Refer to Hibernate 3.6 Migration Guide!",
 			id = 223)
 	void recognizedObsoleteHibernateNamespace(String oldHibernateNamespace,
 											  String hibernateNamespace);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Property [%s] has been renamed to [%s]; update your properties appropriately", id = 225)
 	void renamedProperty(Object propertyName,
 						 Object newPropertyName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Required a different provider: %s", id = 226)
 	void requiredDifferentProvider(String provider);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Running hbm2ddl schema export", id = 227)
 	void runningHbm2ddlSchemaExport();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Running hbm2ddl schema update", id = 228)
 	void runningHbm2ddlSchemaUpdate();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Running schema validator", id = 229)
 	void runningSchemaValidator();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Schema export complete", id = 230)
 	void schemaExportComplete();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Schema export unsuccessful", id = 231)
 	void schemaExportUnsuccessful(@Cause Exception e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Schema update complete", id = 232)
 	void schemaUpdateComplete();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Scoping types to session factory %s after already scoped %s", id = 233)
 	void scopingTypesToSessionFactoryAfterAlreadyScoped(SessionFactoryImplementor factory,
 														SessionFactoryImplementor factory2);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Searching for mapping documents in jar: %s", id = 235)
 	void searchingForMappingDocuments(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Second level cache hits: %s", id = 237)
 	void secondLevelCacheHits(long secondLevelCacheHitCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Second level cache misses: %s", id = 238)
 	void secondLevelCacheMisses(long secondLevelCacheMissCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Second level cache puts: %s", id = 239)
 	void secondLevelCachePuts(long secondLevelCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Service properties: %s", id = 240)
 	void serviceProperties(Properties properties);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Sessions closed: %s", id = 241)
 	void sessionsClosed(long sessionCloseCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Sessions opened: %s", id = 242)
 	void sessionsOpened(long sessionOpenCount);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Setters of lazy classes cannot be final: %s.%s", id = 243)
 	void settersOfLazyClassesCannotBeFinal(String entityName,
 										   String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "@Sort not allowed for an indexed collection, annotation ignored.", id = 244)
 	void sortAnnotationIndexedCollection();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Manipulation query [%s] resulted in [%s] split queries", id = 245)
 	void splitQueries(String sourceQuery,
 					  int length);
 
 //	@LogMessage(level = ERROR)
 //	@Message(value = "SQLException escaped proxy", id = 246)
 //	void sqlExceptionEscapedProxy(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "SQL Error: %s, SQLState: %s", id = 247)
 	void sqlWarning(int errorCode,
 					String sqlState);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Starting query cache at region: %s", id = 248)
 	void startingQueryCache(String region);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Starting service at JNDI name: %s", id = 249)
 	void startingServiceAtJndiName(String boundName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Starting update timestamps cache at region: %s", id = 250)
 	void startingUpdateTimestampsCache(String region);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Start time: %s", id = 251)
 	void startTime(long startTime);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Statements closed: %s", id = 252)
 	void statementsClosed(long closeStatementCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Statements prepared: %s", id = 253)
 	void statementsPrepared(long prepareStatementCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Stopping service", id = 255)
 	void stoppingService();
 
 	@LogMessage(level = INFO)
 	@Message(value = "sub-resolver threw unexpected exception, continuing to next : %s", id = 257)
 	void subResolverException(String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Successful transactions: %s", id = 258)
 	void successfulTransactions(long committedTransactionCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Synchronization [%s] was already registered", id = 259)
 	void synchronizationAlreadyRegistered(Synchronization synchronization);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Exception calling user Synchronization [%s] : %s", id = 260)
 	void synchronizationFailed(Synchronization synchronization,
 							   Throwable t);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Table found: %s", id = 261)
 	void tableFound(String string);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Table not found: %s", id = 262)
 	void tableNotFound(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Transactions: %s", id = 266)
 	void transactions(long transactionCount);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Transaction started on non-root session", id = 267)
 	void transactionStartedOnNonRootSession();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Transaction strategy: %s", id = 268)
 	void transactionStrategy(String strategyClassName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Type [%s] defined no registration keys; ignoring", id = 269)
 	void typeDefinedNoRegistrationKeys(BasicType type);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Type registration [%s] overrides previous : %s", id = 270)
 	void typeRegistrationOverridesPrevious(String key,
 										   Type old);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Naming exception occurred accessing Ejb3Configuration", id = 271)
 	void unableToAccessEjb3Configuration(@Cause NamingException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error while accessing session factory with JNDI name %s", id = 272)
 	void unableToAccessSessionFactory(String sfJNDIName,
 									  @Cause NamingException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Error accessing type info result set : %s", id = 273)
 	void unableToAccessTypeInfoResultSet(String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to apply constraints on DDL for %s", id = 274)
 	void unableToApplyConstraints(String className,
 								  @Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not bind Ejb3Configuration to JNDI", id = 276)
 	void unableToBindEjb3ConfigurationToJndi(@Cause JndiException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not bind factory to JNDI", id = 277)
 	void unableToBindFactoryToJndi(@Cause JndiException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not bind value '%s' to parameter: %s; %s", id = 278)
 	void unableToBindValueToParameter(String nullSafeToString,
 									  int index,
 									  String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to build enhancement metamodel for %s", id = 279)
 	void unableToBuildEnhancementMetamodel(String className);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not build SessionFactory using the MBean classpath - will try again using client classpath: %s",
 			id = 280)
 	void unableToBuildSessionFactoryUsingMBeanClasspath(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to clean up callable statement", id = 281)
 	void unableToCleanUpCallableStatement(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to clean up prepared statement", id = 282)
 	void unableToCleanUpPreparedStatement(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to cleanup temporary id table after use [%s]", id = 283)
 	void unableToCleanupTemporaryIdTable(Throwable t);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error closing connection", id = 284)
 	void unableToCloseConnection(@Cause Exception e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error closing InitialContext [%s]", id = 285)
 	void unableToCloseInitialContext(String string);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error closing input files: %s", id = 286)
 	void unableToCloseInputFiles(String name,
 								 @Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not close input stream", id = 287)
 	void unableToCloseInputStream(@Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not close input stream for %s", id = 288)
 	void unableToCloseInputStreamForResource(String resourceName,
 											 @Cause IOException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to close iterator", id = 289)
 	void unableToCloseIterator(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close jar: %s", id = 290)
 	void unableToCloseJar(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error closing output file: %s", id = 291)
 	void unableToCloseOutputFile(String outputFile,
 								 @Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "IOException occurred closing output stream", id = 292)
 	void unableToCloseOutputStream(@Cause IOException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Problem closing pooled connection", id = 293)
 	void unableToClosePooledConnection(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close session", id = 294)
 	void unableToCloseSession(@Cause HibernateException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close session during rollback", id = 295)
 	void unableToCloseSessionDuringRollback(@Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "IOException occurred closing stream", id = 296)
 	void unableToCloseStream(@Cause IOException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not close stream on hibernate.properties: %s", id = 297)
 	void unableToCloseStreamError(IOException error);
 
 	@Message(value = "JTA commit failed", id = 298)
 	String unableToCommitJta();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not complete schema update", id = 299)
 	void unableToCompleteSchemaUpdate(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not complete schema validation", id = 300)
 	void unableToCompleteSchemaValidation(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to configure SQLExceptionConverter : %s", id = 301)
 	void unableToConfigureSqlExceptionConverter(HibernateException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to construct current session context [%s]", id = 302)
 	void unableToConstructCurrentSessionContext(String impl,
 												@Cause Throwable e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to construct instance of specified SQLExceptionConverter : %s", id = 303)
 	void unableToConstructSqlExceptionConverter(Throwable t);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not copy system properties, system properties will be ignored", id = 304)
 	void unableToCopySystemProperties();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not create proxy factory for:%s", id = 305)
 	void unableToCreateProxyFactory(String entityName,
 									@Cause HibernateException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error creating schema ", id = 306)
 	void unableToCreateSchema(@Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not deserialize cache file: %s : %s", id = 307)
 	void unableToDeserializeCache(String path,
 								  SerializationException error);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to destroy cache: %s", id = 308)
 	void unableToDestroyCache(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to destroy query cache: %s: %s", id = 309)
 	void unableToDestroyQueryCache(String region,
 								   String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to destroy update timestamps cache: %s: %s", id = 310)
 	void unableToDestroyUpdateTimestampsCache(String region,
 											  String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to determine lock mode value : %s -> %s", id = 311)
 	void unableToDetermineLockModeValue(String hintName,
 										Object value);
 
 	@Message(value = "Could not determine transaction status", id = 312)
 	String unableToDetermineTransactionStatus();
 
 	@Message(value = "Could not determine transaction status after commit", id = 313)
 	String unableToDetermineTransactionStatusAfterCommit();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to drop temporary id table after use [%s]", id = 314)
 	void unableToDropTemporaryIdTable(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Exception executing batch [%s]", id = 315)
 	void unableToExecuteBatch(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Error executing resolver [%s] : %s", id = 316)
 	void unableToExecuteResolver(DialectResolver abstractDialectResolver,
 								 String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not find any META-INF/persistence.xml file in the classpath", id = 318)
 	void unableToFindPersistenceXmlInClasspath();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not get database metadata", id = 319)
 	void unableToGetDatabaseMetadata(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to instantiate configured schema name resolver [%s] %s", id = 320)
 	void unableToInstantiateConfiguredSchemaNameResolver(String resolverClassName,
 														 String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to interpret specified optimizer [%s], falling back to noop", id = 321)
 	void unableToLocateCustomOptimizerClass(String type);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to instantiate specified optimizer [%s], falling back to noop", id = 322)
 	void unableToInstantiateOptimizer(String type);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to instantiate UUID generation strategy class : %s", id = 325)
 	void unableToInstantiateUuidGenerationStrategy(Exception ignore);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Cannot join transaction: do not override %s", id = 326)
 	void unableToJoinTransaction(String transactionStrategy);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error performing load command : %s", id = 327)
 	void unableToLoadCommand(HibernateException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to load/access derby driver class sysinfo to check versions : %s", id = 328)
 	void unableToLoadDerbyDriver(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Problem loading properties from hibernate.properties", id = 329)
 	void unableToLoadProperties();
 
 	@Message(value = "Unable to locate config file: %s", id = 330)
 	String unableToLocateConfigFile(String path);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to locate configured schema name resolver class [%s] %s", id = 331)
 	void unableToLocateConfiguredSchemaNameResolver(String resolverClassName,
 													String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to locate MBeanServer on JMX service shutdown", id = 332)
 	void unableToLocateMBeanServer();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to locate requested UUID generation strategy class : %s", id = 334)
 	void unableToLocateUuidGenerationStrategy(String strategyClassName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to log SQLWarnings : %s", id = 335)
 	void unableToLogSqlWarnings(SQLException sqle);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not log warnings", id = 336)
 	void unableToLogWarnings(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to mark for rollback on PersistenceException: ", id = 337)
 	void unableToMarkForRollbackOnPersistenceException(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to mark for rollback on TransientObjectException: ", id = 338)
 	void unableToMarkForRollbackOnTransientObjectException(@Cause Exception e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection metadata: %s", id = 339)
 	void unableToObjectConnectionMetadata(SQLException error);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection to query metadata: %s", id = 340)
 	void unableToObjectConnectionToQueryMetadata(SQLException error);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection metadata : %s", id = 341)
 	void unableToObtainConnectionMetadata(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not obtain connection to query metadata : %s", id = 342)
 	void unableToObtainConnectionToQueryMetadata(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not obtain initial context", id = 343)
 	void unableToObtainInitialContext(@Cause NamingException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not parse the package-level metadata [%s]", id = 344)
 	void unableToParseMetadata(String packageName);
 
 	@Message(value = "JDBC commit failed", id = 345)
 	String unableToPerformJdbcCommit();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error during managed flush [%s]", id = 346)
 	void unableToPerformManagedFlush(String message);
 
 	@Message(value = "Unable to query java.sql.DatabaseMetaData", id = 347)
 	String unableToQueryDatabaseMetadata();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to read class: %s", id = 348)
 	void unableToReadClass(String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not read column value from result set: %s; %s", id = 349)
 	void unableToReadColumnValueFromResultSet(String name,
 											  String message);
 
 	@Message(value = "Could not read a hi value - you need to populate the table: %s", id = 350)
 	String unableToReadHiValue(String tableName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not read or init a hi value", id = 351)
 	void unableToReadOrInitHiValue(@Cause SQLException e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to release batch statement...", id = 352)
 	void unableToReleaseBatchStatement();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not release a cache lock : %s", id = 353)
 	void unableToReleaseCacheLock(CacheException ce);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to release initial context: %s", id = 354)
 	void unableToReleaseContext(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to release created MBeanServer : %s", id = 355)
 	void unableToReleaseCreatedMBeanServer(String string);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to release isolated connection [%s]", id = 356)
 	void unableToReleaseIsolatedConnection(Throwable ignore);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to release type info result set", id = 357)
 	void unableToReleaseTypeInfoResultSet();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to erase previously added bag join fetch", id = 358)
 	void unableToRemoveBagJoinFetch();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not resolve aggregate function [%s]; using standard definition", id = 359)
 	void unableToResolveAggregateFunction(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to resolve mapping file [%s]", id = 360)
 	void unableToResolveMappingFile(String xmlFile);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to retreive cache from JNDI [%s]: %s", id = 361)
 	void unableToRetrieveCache(String namespace,
 							   String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to retrieve type info result set : %s", id = 362)
 	void unableToRetrieveTypeInfoResultSet(String string);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to rollback connection on exception [%s]", id = 363)
 	void unableToRollbackConnection(Exception ignore);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unable to rollback isolated transaction on error [%s] : [%s]", id = 364)
 	void unableToRollbackIsolatedTransaction(Exception e,
 											 Exception ignore);
 
 	@Message(value = "JTA rollback failed", id = 365)
 	String unableToRollbackJta();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error running schema update", id = 366)
 	void unableToRunSchemaUpdate(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not set transaction to rollback only", id = 367)
 	void unableToSetTransactionToRollbackOnly(@Cause SystemException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Exception while stopping service", id = 368)
 	void unableToStopHibernateService(@Cause Exception e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error stopping service [%s] : %s", id = 369)
 	void unableToStopService(Class class1,
 							 String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Exception switching from method: [%s] to a method using the column index. Reverting to using: [%<s]",
 			id = 370)
 	void unableToSwitchToMethodUsingColumnIndex(Method method);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not synchronize database state with session: %s", id = 371)
 	void unableToSynchronizeDatabaseStateWithSession(HibernateException he);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not toggle autocommit", id = 372)
 	void unableToToggleAutoCommit(@Cause Exception e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unable to transform class: %s", id = 373)
 	void unableToTransformClass(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Could not unbind factory from JNDI", id = 374)
 	void unableToUnbindFactoryFromJndi(@Cause JndiException e);
 
 	@Message(value = "Could not update hi value in: %s", id = 375)
 	Object unableToUpdateHiValue(String tableName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not updateQuery hi value in: %s", id = 376)
 	void unableToUpdateQueryHiValue(String tableName,
 									@Cause SQLException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Error wrapping result set", id = 377)
 	void unableToWrapResultSet(@Cause SQLException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "I/O reported error writing cached file : %s: %s", id = 378)
 	void unableToWriteCachedFile(String path,
 								 String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unexpected literal token type [%s] passed for numeric processing", id = 380)
 	void unexpectedLiteralTokenType(int type);
 
 	@LogMessage(level = WARN)
 	@Message(value = "JDBC driver did not return the expected number of row counts", id = 381)
 	void unexpectedRowCounts();
 
 	@LogMessage(level = WARN)
 	@Message(value = "unrecognized bytecode provider [%s], using javassist by default", id = 382)
 	void unknownBytecodeProvider(String providerName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unknown Ingres major version [%s]; using Ingres 9.2 dialect", id = 383)
 	void unknownIngresVersion(int databaseMajorVersion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unknown Oracle major version [%s]", id = 384)
 	void unknownOracleVersion(int databaseMajorVersion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unknown Microsoft SQL Server major version [%s] using SQL Server 2000 dialect", id = 385)
 	void unknownSqlServerVersion(int databaseMajorVersion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "ResultSet had no statement associated with it, but was not yet registered", id = 386)
 	void unregisteredResultSetWithoutStatement();
 
 	// Keep this at DEBUG level, rather than warn.  Numerous connection pool implementations can return a
 	// proxy/wrapper around the JDBC Statement, causing excessive logging here.  See HHH-8210.
 	@LogMessage(level = DEBUG)
 	@Message(value = "ResultSet's statement was not registered", id = 387)
 	void unregisteredStatement();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unsuccessful: %s", id = 388)
 	void unsuccessful(String sql);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Unsuccessful: %s", id = 389)
 	void unsuccessfulCreate(String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Overriding release mode as connection provider does not support 'after_statement'", id = 390)
 	void unsupportedAfterStatement();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Ingres 10 is not yet fully supported; using Ingres 9.3 dialect", id = 391)
 	void unsupportedIngresVersion();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Hibernate does not support SequenceGenerator.initialValue() unless '%s' set", id = 392)
 	void unsupportedInitialValue(String propertyName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "The %s.%s.%s version of H2 implements temporary table creation such that it commits current transaction; multi-table, bulk hql/jpaql will not work properly",
 			id = 393)
 	void unsupportedMultiTableBulkHqlJpaql(int majorVersion,
 										   int minorVersion,
 										   int buildId);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Oracle 11g is not yet fully supported; using Oracle 10g dialect", id = 394)
 	void unsupportedOracleVersion();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Usage of obsolete property: %s no longer supported, use: %s", id = 395)
 	void unsupportedProperty(Object propertyName,
 							 Object newPropertyName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Updating schema", id = 396)
 	void updatingSchema();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using ASTQueryTranslatorFactory", id = 397)
 	void usingAstQueryTranslatorFactory();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Explicit segment value for id generator [%s.%s] suggested; using default [%s]", id = 398)
 	void usingDefaultIdGeneratorSegmentValue(String tableName,
 											 String segmentColumnName,
 											 String defaultToUse);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using default transaction strategy (direct JDBC transactions)", id = 399)
 	void usingDefaultTransactionStrategy();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using dialect: %s", id = 400)
 	void usingDialect(Dialect dialect);
 
 	@LogMessage(level = INFO)
 	@Message(value = "using driver [%s] at URL [%s]", id = 401)
 	void usingDriver(String driverClassName,
 					 String url);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using Hibernate built-in connection pool (not for production use!)", id = 402)
 	void usingHibernateBuiltInConnectionPool();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Don't use old DTDs, read the Hibernate 3.x Migration Guide!", id = 404)
 	void usingOldDtd();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using bytecode reflection optimizer", id = 406)
 	void usingReflectionOptimizer();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using java.io streams to persist binary types", id = 407)
 	void usingStreams();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Using workaround for JVM bug in java.sql.Timestamp", id = 408)
 	void usingTimestampWorkaround();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Using %s which does not generate IETF RFC 4122 compliant UUID values; consider using %s instead",
 			id = 409)
 	void usingUuidHexGenerator(String name,
 							   String name2);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Hibernate Validator not found: ignoring", id = 410)
 	void validatorNotFound();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Hibernate Core {%s}", id = 412)
 	void version(String versionString);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Warnings creating temp table : %s", id = 413)
 	void warningsCreatingTempTable(SQLWarning warning);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Property hibernate.search.autoregister_listeners is set to false. No attempt will be made to register Hibernate Search event listeners.",
 			id = 414)
 	void willNotRegisterListeners();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Write locks via update not supported for non-versioned entities [%s]", id = 416)
 	void writeLocksNotSupported(String entityName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Writing generated schema to file: %s", id = 417)
 	void writingGeneratedSchemaToFile(String outputFile);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Adding override for %s: %s", id = 418)
 	void addingOverrideFor(String name,
 						   String name2);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Resolved SqlTypeDescriptor is for a different SQL code. %s has sqlCode=%s; type override %s has sqlCode=%s",
 			id = 419)
 	void resolvedSqlTypeDescriptorForDifferentSqlCode(String name,
 													  String valueOf,
 													  String name2,
 													  String valueOf2);
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "Closing un-released batch", id = 420)
 	void closingUnreleasedBatch();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as %s is true", id = 421)
 	void disablingContextualLOBCreation(String nonContextualLobCreation);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as connection was null", id = 422)
 	void disablingContextualLOBCreationSinceConnectionNull();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as JDBC driver reported JDBC version [%s] less than 4",
 			id = 423)
 	void disablingContextualLOBCreationSinceOldJdbcVersion(int jdbcMajorVersion);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disabling contextual LOB creation as createClob() method threw error : %s", id = 424)
 	void disablingContextualLOBCreationSinceCreateClobFailed(Throwable t);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not close session; swallowing exception[%s] as transaction completed", id = 425)
 	void unableToCloseSessionButSwallowingError(HibernateException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "You should set hibernate.transaction.manager_lookup_class if cache is enabled", id = 426)
 	void setManagerLookupClass();
 
 //	@LogMessage(level = WARN)
 //	@Message(value = "Using deprecated %s strategy [%s], use newer %s strategy instead [%s]", id = 427)
 //	void deprecatedTransactionManagerStrategy(String name,
 //											  String transactionManagerStrategy,
 //											  String name2,
 //											  String jtaPlatform);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Encountered legacy TransactionManagerLookup specified; convert to newer %s contract specified via %s setting",
 			id = 428)
 	void legacyTransactionManagerStrategy(String name,
 										  String jtaPlatform);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Setting entity-identifier value binding where one already existed : %s.", id = 429)
 	void entityIdentifierValueBindingExists(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "The DerbyDialect dialect has been deprecated; use one of the version-specific dialects instead",
 			id = 430)
 	void deprecatedDerbyDialect();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Unable to determine H2 database version, certain features may not work", id = 431)
 	void undeterminedH2Version();
 
 	@LogMessage(level = WARN)
 	@Message(value = "There were not column names specified for index %s on table %s", id = 432)
 	void noColumnsSpecifiedForIndex(String indexName, String tableName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "update timestamps cache puts: %s", id = 433)
 	void timestampCachePuts(long updateTimestampsCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "update timestamps cache hits: %s", id = 434)
 	void timestampCacheHits(long updateTimestampsCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "update timestamps cache misses: %s", id = 435)
 	void timestampCacheMisses(long updateTimestampsCachePutCount);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Entity manager factory name (%s) is already registered.  If entity manager will be clustered "+
 			"or passivated, specify a unique value for property '%s'", id = 436)
 	void entityManagerFactoryAlreadyRegistered(String emfName, String propertyName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Attempting to save one or more entities that have a non-nullable association with an unsaved transient entity. The unsaved transient entity must be saved in an operation prior to saving these dependent entities.\n" +
 			"\tUnsaved transient entity: (%s)\n\tDependent entities: (%s)\n\tNon-nullable association(s): (%s)" , id = 437)
 	void cannotResolveNonNullableTransientDependencies(String transientEntityString,
 													   Set<String> dependentEntityStrings,
 													   Set<String> nonNullableAssociationPaths);
 
 	@LogMessage(level = INFO)
 	@Message(value = "NaturalId cache puts: %s", id = 438)
 	void naturalIdCachePuts(long naturalIdCachePutCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "NaturalId cache hits: %s", id = 439)
 	void naturalIdCacheHits(long naturalIdCacheHitCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "NaturalId cache misses: %s", id = 440)
 	void naturalIdCacheMisses(long naturalIdCacheMissCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Max NaturalId query time: %sms", id = 441)
 	void naturalIdMaxQueryTime(long naturalIdQueryExecutionMaxTime);
 	
 	@LogMessage(level = INFO)
 	@Message(value = "NaturalId queries executed to database: %s", id = 442)
 	void naturalIdQueriesExecuted(long naturalIdQueriesExecutionCount);
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Dialect [%s] limits the number of elements in an IN predicate to %s entries.  " +
 					"However, the given parameter list [%s] contained %s entries, which will likely cause failures " +
 					"to execute the query in the database",
 			id = 443
 	)
 	void tooManyInExpressions(String dialectName, int limit, String paramName, int size);
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Encountered request for locking however dialect reports that database prefers locking be done in a " +
 					"separate select (follow-on locking); results will be locked after initial query executes",
 			id = 444
 	)
 	void usingFollowOnLocking();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Alias-specific lock modes requested, which is not currently supported with follow-on locking; " +
 					"all acquired locks will be [%s]",
 			id = 445
 	)
 	void aliasSpecificLockingWithFollowOnLocking(LockMode lockMode);
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "embed-xml attributes were intended to be used for DOM4J entity mode. Since that entity mode has been " +
 					"removed, embed-xml attributes are no longer supported and should be removed from mappings.",
 			id = 446
 	)
 	void embedXmlAttributesNoLongerSupported();
 
 	@LogMessage(level = WARN)
 	@Message(
 			value = "Explicit use of UPGRADE_SKIPLOCKED in lock() calls is not recommended; use normal UPGRADE locking instead",
 			id = 447
 	)
 	void explicitSkipLockedLockCombo();
 
 	@LogMessage(level = INFO)
 	@Message( value = "'javax.persistence.validation.mode' named multiple values : %s", id = 448 )
 	void multipleValidationModes(String modes);
 
 	@LogMessage(level = WARN)
 	@Message(
 			id = 449,
 			value = "@Convert annotation applied to Map attribute [%s] did not explicitly specify attributeName " +
 					"using 'key'/'value' as required by spec; attempting to DoTheRightThing"
 	)
 	void nonCompliantMapConversion(String collectionRole);
 
 	@LogMessage(level = WARN)
 	@Message(
 			id = 450,
 			value = "Encountered request for Service by non-primary service role [%s -> %s]; please update usage"
 	)
 	void alternateServiceRole(String requestedRole, String targetRole);
 
 	@LogMessage(level = WARN)
 	@Message(
 			id = 451,
 			value = "Transaction afterCompletion called by a background thread; " +
 					"delaying afterCompletion processing until the original thread can handle it. [status=%s]"
 	)
 	void rollbackFromBackgroundThread(int status);
 	
 	@LogMessage(level = WARN)
 	@Message(value = "Exception while loading a class or resource found during scanning", id = 452)
 	void unableToLoadScannedClassOrResource(@Cause Exception e);
 	
 	@LogMessage(level = WARN)
 	@Message(value = "Exception while discovering OSGi service implementations : %s", id = 453)
 	void unableToDiscoverOsgiService(String service, @Cause Exception e);
 
+	@LogMessage(level = WARN)
+	@Message(value = "The outer-join attribute on <many-to-many> has been deprecated. Instead of outer-join=\"false\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.", id = 454)
+	void deprecatedManyToManyOuterJoin();
+
+	@LogMessage(level = WARN)
+	@Message(value = "The fetch attribute on <many-to-many> has been deprecated. Instead of fetch=\"select\", use lazy=\"extra\" with <map>, <set>, <bag>, <idbag>, or <list>, which will only initialize entities (not as a proxy) as needed.", id = 455)
+	void deprecatedManyToManyFetch();
+
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
index 7a5fc29cc0..c56710181b 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/FooBarTest.java
@@ -3811,1180 +3811,1180 @@ public class FooBarTest extends LegacyTestCase {
 		assertTrue( "cascade update", fee.getQux()!=null );
 		assertTrue( "cascade update", fee.getQux().getStuff().equals("xxx") );
 		assertTrue( "update", fee.getAnotherFee()!=null );
 		assertTrue( "update", fee.getFee()!=null );
 		assertTrue( "update", fee.getAnotherFee().getFee()==fee.getFee() );
 		fee.getAnotherFee().setAnotherFee(null);
 		s.delete(fee);
 		doDelete( s, "from Fee fee" );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		assertTrue( s.createQuery( "from Fee fee" ).list().size()==0 );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testArraysOfTimes() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz() ;
 		s.save(baz);
 		baz.setDefaults();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz.getTimeArray()[2] = new Date(123);
 		baz.getTimeArray()[3] = new java.sql.Time(1234);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		s.delete( baz );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testComponents() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Foo foo = new Foo();
 //		foo.setComponent( new FooComponent("foo", 69, null, new FooComponent("bar", 96, null, null) ) );
 		s.save(foo);
 		foo.getComponent().setName( "IFA" );
 		txn.commit();
 		s.close();
 
 		foo.setComponent( null );
 
 		s = openSession();
 		txn = s.beginTransaction();
 		s.load( foo, foo.getKey() );
 		assertTrue(
 			"save components",
 			foo.getComponent().getName().equals("IFA") &&
 			foo.getComponent().getSubcomponent().getName().equals("bar")
 		);
 		assertTrue( "cascade save via component", foo.getComponent().getGlarch() != null );
 		foo.getComponent().getSubcomponent().setName("baz");
 		txn.commit();
 		s.close();
 
 		foo.setComponent(null);
 
 		s = openSession();
 		txn = s.beginTransaction();
 		s.load( foo, foo.getKey() );
 		assertTrue(
 			"update components",
 			foo.getComponent().getName().equals("IFA") &&
 			foo.getComponent().getSubcomponent().getName().equals("baz")
 		);
 		s.delete(foo);
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		foo = new Foo();
 		s.save( foo );
 		foo.setCustom( new String[] { "one", "two" } );
 		assertTrue( s.createQuery( "from Foo foo where foo.custom.s1 = 'one'" ).list().get(0)==foo );
 		s.delete( foo );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testNoForeignKeyViolations() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Glarch g1 = new Glarch();
 		Glarch g2 = new Glarch();
 		g1.setNext(g2);
 		g2.setNext(g1);
 		s.save(g1);
 		s.save(g2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List l = s.createQuery( "from Glarch g where g.next is not null" ).list();
 		s.delete( l.get(0) );
 		s.delete( l.get(1) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testLazyCollections() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Qux q = new Qux();
 		s.save(q);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		q = (Qux) s.load( Qux.class, q.getKey() );
 		s.getTransaction().commit();
 		s.close();
 
 		System.out.println("Two exceptions are supposed to occur:");
 		boolean ok = false;
 		try {
 			q.getMoreFums().isEmpty();
 		}
 		catch (LazyInitializationException e) {
 			ok = true;
 		}
 		assertTrue( "lazy collection with one-to-many", ok );
 
 		ok = false;
 		try {
 			q.getFums().isEmpty();
 		}
 		catch (LazyInitializationException e) {
 			ok = true;
 		}
 		assertTrue( "lazy collection with many-to-many", ok );
 
 		s = openSession();
 		s.beginTransaction();
 		q = (Qux) s.load( Qux.class, q.getKey() );
 		s.delete(q);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	@TestForIssue(jiraKey = "HHH-7603")
 	public void testLazyCollectionsTouchedDuringPreCommit() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Qux q = new Qux();
 		s.save( q );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		q = ( Qux ) s.load( Qux.class, q.getKey() );
 		s.getTransaction().commit();
 
 		//clear the session
 		s.clear();
 
 		//now reload the proxy and delete it
 		s.beginTransaction();
 
 		final Qux qToDelete = ( Qux ) s.load( Qux.class, q.getKey() );
 
 		//register a pre commit process that will touch the collection and delete the entity
 		( ( EventSource ) s ).getActionQueue().registerProcess( new BeforeTransactionCompletionProcess() {
 			@Override
 			public void doBeforeTransactionCompletion(SessionImplementor session) {
 				qToDelete.getFums().size();
 			}
 		} );
 
 		s.delete( qToDelete );
 		boolean ok = false;
 		try {
 			s.getTransaction().commit();
 		}
 		catch (LazyInitializationException e) {
 			ok = true;
 			s.getTransaction().rollback();
 		}
 		finally {
 			s.close();
 		}
 		assertTrue( "lazy collection should have blown in the before trans completion", ok );
 
 		s = openSession();
 		s.beginTransaction();
 		q = ( Qux ) s.load( Qux.class, q.getKey() );
 		s.delete( q );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@SkipForDialect(value = AbstractHANADialect.class, comment = "HANA currently requires specifying table name by 'FOR UPDATE of t1.c1' if there are more than one tables/views/subqueries in the FROM clause")
 	@Test
 	public void testNewSessionLifecycle() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Serializable fid = null;
 		try {
 			Foo f = new Foo();
 			s.save(f);
 			fid = s.getIdentifier(f);
 			s.getTransaction().commit();
 		}
 		catch (Exception e) {
 			s.getTransaction().rollback();
 			throw e;
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		s.beginTransaction();
 		try {
 			Foo f = new Foo();
 			s.delete(f);
 			s.getTransaction().commit();
 		}
 		catch (Exception e) {
 			s.getTransaction().rollback();
 		}
 		finally {
 			s.close();
 		}
 
 		s = openSession();
 		s.beginTransaction();
 		try {
 			Foo f = (Foo) s.load(Foo.class, fid, LockMode.UPGRADE);
 
 			s.delete(f);
 			s.flush();
 			s.getTransaction().commit();
 		}
 		catch (Exception e) {
 			s.getTransaction().rollback();
 			throw e;
 		}
 		finally {
 			assertTrue( s.close()==null );
 		}
 	}
 
 	@Test
 	public void testOrderBy() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		s.save(foo);
 		List list = s.createQuery(
 				"select foo from Foo foo, Fee fee where foo.dependent = fee order by foo.string desc, foo.component.count asc, fee.id"
 		).list();
 		assertTrue( "order by", list.size()==1 );
 		Foo foo2 = new Foo();
 		s.save(foo2);
 		foo.setFoo(foo2);
 		list = s.createQuery(
 				"select foo.foo, foo.dependent from Foo foo order by foo.foo.string desc, foo.component.count asc, foo.dependent.id"
 		).list();
 		assertTrue( "order by", list.size()==1 );
 		list = s.createQuery( "select foo from Foo foo order by foo.dependent.id, foo.dependent.fi" ).list();
 		assertTrue( "order by", list.size()==2 );
 		s.delete(foo);
 		s.delete(foo2);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		Many manyB = new Many();
 		s.save(manyB);
 		One oneB = new One();
 		s.save(oneB);
 		oneB.setValue("b");
 		manyB.setOne(oneB);
 		Many manyA = new Many();
 		s.save(manyA);
 		One oneA = new One();
 		s.save(oneA);
 		oneA.setValue("a");
 		manyA.setOne(oneA);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List results = s.createQuery( "SELECT one FROM " + One.class.getName() + " one ORDER BY one.value ASC" ).list();
 		assertEquals( 2, results.size() );
 		assertEquals( "'a' isn't first element", "a", ( (One) results.get(0) ).getValue() );
 		assertEquals( "'b' isn't second element", "b", ( (One) results.get(1) ).getValue() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		results = s.createQuery( "SELECT many.one FROM " + Many.class.getName() + " many ORDER BY many.one.value ASC, many.one.id" )
 				.list();
 		assertEquals( 2, results.size() );
 		assertEquals( 2, results.size() );
 		assertEquals( "'a' isn't first element", "a", ( (One) results.get(0) ).getValue() );
 		assertEquals( "'b' isn't second element", "b", ( (One) results.get(1) ).getValue() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		oneA = (One)s.load(One.class, oneA.getKey());
 		manyA = (Many)s.load(Many.class, manyA.getKey());
 		oneB = (One)s.load(One.class, oneB.getKey());
 		manyB = (Many)s.load(Many.class, manyB.getKey());
 		s.delete(manyA);
 		s.delete(oneA);
 		s.delete(manyB);
 		s.delete(oneB);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testManyToOne() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		One one = new One();
 		s.save(one);
 		one.setValue( "yada" );
 		Many many = new Many();
 		many.setOne( one );
 		s.save( many );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		one = (One) s.load( One.class, one.getKey() );
 		one.getManies().size();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		many = (Many) s.load( Many.class, many.getKey() );
 		assertTrue( "many-to-one assoc", many.getOne()!=null );
 		s.delete( many.getOne() );
 		s.delete(many);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testSaveDelete() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo f = new Foo();
 		s.save(f);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.delete( s.load( Foo.class, f.getKey() ) );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testProxyArray() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		GlarchProxy g = new Glarch();
 		Glarch g1 = new Glarch();
 		Glarch g2 = new Glarch();
 		g.setProxyArray( new GlarchProxy[] { g1, g2 } );
 		Glarch g3 = new Glarch();
 		s.save(g3);
 		g2.setProxyArray( new GlarchProxy[] {null, g3, g} );
 		Set set = new HashSet();
 		set.add(g1);
 		set.add(g2);
 		g.setProxySet(set);
 		s.save(g);
 		s.save(g1);
 		s.save(g2);
 		Serializable id = s.getIdentifier(g);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, id);
 		assertTrue( "array of proxies", g.getProxyArray().length==2 );
 		assertTrue( "array of proxies", g.getProxyArray()[0]!=null );
 		assertTrue("deferred load test",g.getProxyArray()[1].getProxyArray()[0]==null );
 		assertTrue("deferred load test",g.getProxyArray()[1].getProxyArray()[2]==g );
 		assertTrue( "set of proxies", g.getProxySet().size()==2 );
 		Iterator iter = s.createQuery( "from Glarch g" ).iterate();
 		while ( iter.hasNext() ) {
 			iter.next();
 			iter.remove();
 		}
 		s.getTransaction().commit();
 		s.disconnect();
 		SerializationHelper.deserialize( SerializationHelper.serialize(s) );
 		s.close();
 	}
 
 	@Test
 	public void testCache() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Immutable im = new Immutable();
 		s.save(im);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.load( im, im.getId() );
 		s.getTransaction().commit();
 		s.close();
 
 		final Session s2 = openSession();
 		s2.beginTransaction();
 		s2.load( im, im.getId() );
 		assertEquals(
 				"cached object identity",
 				im,
 				s2.createQuery( "from Immutable im where im = ?" ).setParameter(
 						0, im, s2.getTypeHelper().entity( Immutable.class )
 				).uniqueResult()
 		);
 		s2.doWork(
 				new AbstractWork() {
 					@Override
 					public void execute(Connection connection) throws SQLException {
 						Statement st = connection.createStatement();
 						st.executeUpdate( "delete from immut" );
 					}
 				}
 		);
 		s2.getTransaction().commit();
 		s2.close();
 	}
 
 	@Test
 	public void testFindLoad() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		FooProxy foo = new Foo();
 		s.save(foo);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (FooProxy) s.createQuery( "from Foo foo" ).list().get(0);
 		FooProxy foo2 = (FooProxy) s.load( Foo.class, foo.getKey() );
 		assertTrue( "find returns same object as load", foo == foo2 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo2 = (FooProxy) s.load( Foo.class, foo.getKey() );
 		foo = (FooProxy) s.createQuery( "from Foo foo" ).list().get(0);
 		assertTrue( "find returns same object as load", foo == foo2 );
 		doDelete( s, "from Foo foo" );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@SkipForDialect(value = AbstractHANADialect.class, comment = "HANA currently requires specifying table name by 'FOR UPDATE of t1.c1' if there are more than one tables/views/subqueries in the FROM clause")
 	@Test
 	public void testRefresh() throws Exception {
 		final Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		s.save( foo );
 		s.flush();
 		s.doWork(
 				new AbstractWork() {
 					@Override
 					public void execute(Connection connection) throws SQLException {
 						final String sql = "update " + getDialect().openQuote() + "foos" + getDialect().closeQuote() + " set long_ = -3";
 						Statement st = connection.createStatement();
 						st.executeUpdate( sql );
 					}
 				}
 		);
 		s.refresh(foo);
 		assertEquals( Long.valueOf( -3l ), foo.getLong() );
 		assertEquals( LockMode.READ, s.getCurrentLockMode( foo ) );
 		s.refresh(foo, LockMode.UPGRADE);
 		if ( getDialect().supportsOuterJoinForUpdate() ) {
 			assertEquals( LockMode.UPGRADE, s.getCurrentLockMode( foo ) );
 		}
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testAutoFlush() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		FooProxy foo = new Foo();
 		s.save(foo);
 		assertTrue( "autoflush create", s.createQuery( "from Foo foo" ).list().size()==1 );
 		foo.setChar( 'X' );
 		assertTrue( "autoflush update", s.createQuery( "from Foo foo where foo.char='X'" ).list().size()==1 );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		foo = (FooProxy) s.load( Foo.class, foo.getKey() );
 		//s.update( new Foo(), foo.getKey() );
 		//assertTrue( s.find("from Foo foo where not foo.char='X'").size()==1, "autoflush update" );
 		if ( !(getDialect() instanceof MySQLDialect) && !(getDialect() instanceof HSQLDialect) && !(getDialect() instanceof PointbaseDialect) )  {
 			foo.setBytes( "osama".getBytes() );
 			assertTrue( "autoflush collection update",
 					s.createQuery( "from Foo foo where 111 in elements(foo.bytes)" ).list().size()==1 );
 			foo.getBytes()[0] = 69;
 			assertTrue( "autoflush collection update",
 					s.createQuery( "from Foo foo where 69 in elements(foo.bytes)" ).list()
 							.size()==1 );
 		}
 		s.delete(foo);
 		assertTrue( "autoflush delete", s.createQuery( "from Foo foo" ).list().size()==0 );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testVeto() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Vetoer v = new Vetoer();
 		s.save(v);
 		s.save(v);
 		s.getTransaction().commit();
 		s.close();
 		s = openSession();
 		s.beginTransaction();
 		s.update( v );
 		s.update( v );
 		s.delete( v );
 		s.delete( v );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testSerializableType() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Vetoer v = new Vetoer();
 		v.setStrings( new String[] {"foo", "bar", "baz"} );
 		s.save( v ); Serializable id = s.save(v);
 		v.getStrings()[1] = "osama";
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		v = (Vetoer) s.load(Vetoer.class, id);
 		assertTrue( "serializable type", v.getStrings()[1].equals( "osama" ) );
 		s.delete(v); s.delete( v );
 		s.flush();
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testAutoFlushCollections() throws Exception {
 		Session s = openSession();
 		Transaction tx = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		tx.commit();
 		s.close();
 
 		s = openSession();
 		tx = s.beginTransaction();
 		baz = (Baz) s.load(Baz.class, baz.getCode());
 		baz.getStringArray()[0] = "bark";
 		Iterator i = s.createQuery( "select elements(baz.stringArray) from Baz baz" ).iterate();
 		boolean found = false;
 		while ( i.hasNext() ) {
 			if ( "bark".equals( i.next() ) ) found = true;
 		}
 		assertTrue(found);
 		baz.setStringArray(null);
 		i = s.createQuery( "select distinct elements(baz.stringArray) from Baz baz" ).iterate();
 		assertTrue( !i.hasNext() );
 		baz.setStringArray( new String[] { "foo", "bar" } );
 		i = s.createQuery( "select elements(baz.stringArray) from Baz baz" ).iterate();
 		assertTrue( i.hasNext() );
 
 		Foo foo = new Foo();
 		s.save(foo);
 		s.flush();
 		baz.setFooArray( new Foo[] {foo} );
 
 		i = s.createQuery( "select foo from Baz baz join baz.fooArray foo" ).iterate();
 		found = false;
 		while ( i.hasNext() ) {
 			if ( foo==i.next() ) found = true;
 		}
 		assertTrue(found);
 
 		baz.getFooArray()[0] = null;
 		i = s.createQuery( "select foo from Baz baz join baz.fooArray foo" ).iterate();
 		assertTrue( !i.hasNext() );
 		baz.getFooArray()[0] = foo;
 		i = s.createQuery( "select elements(baz.fooArray) from Baz baz" ).iterate();
 		assertTrue( i.hasNext() );
 
 		if ( !(getDialect() instanceof MySQLDialect)
 				&& !(getDialect() instanceof HSQLDialect)
 				&& !(getDialect() instanceof InterbaseDialect)
 				&& !(getDialect() instanceof PointbaseDialect)
 				&& !(getDialect() instanceof SAPDBDialect) )  {
 			baz.getFooArray()[0] = null;
 			i = s.createQuery( "from Baz baz where ? in elements(baz.fooArray)" )
 					.setParameter( 0, foo, s.getTypeHelper().entity( Foo.class ) )
 					.iterate();
 			assertTrue( !i.hasNext() );
 			baz.getFooArray()[0] = foo;
 			i = s.createQuery( "select foo from Foo foo where foo in (select elt from Baz baz join baz.fooArray elt)" )
 					.iterate();
 			assertTrue( i.hasNext() );
 		}
 		s.delete(foo);
 		s.delete(baz);
 		tx.commit();
 		s.close();
 	}
 
 	@Test
     @RequiresDialect(value = H2Dialect.class, comment = "this is more like a unit test")
 	public void testUserProvidedConnection() throws Exception {
 		ConnectionProvider dcp = ConnectionProviderBuilder.buildConnectionProvider();
 		Session s = sessionFactory().withOptions().connection( dcp.getConnection() ).openSession();
 		Transaction tx = s.beginTransaction();
 		s.createQuery( "from Fo" ).list();
 		tx.commit();
 		Connection c = s.disconnect();
 		assertTrue( c != null );
 		s.reconnect( c );
 		tx = s.beginTransaction();
 		s.createQuery( "from Fo" ).list();
 		tx.commit();
 		assertTrue( s.close() == c );
 		c.close();
 	}
 
 	@Test
 	public void testCachedCollection() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setDefaults();
 		s.save(baz);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		( (FooComponent) baz.getTopComponents().get(0) ).setCount(99);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		assertTrue( ((FooComponent) baz.getTopComponents().get( 0 )).getCount() == 99 );
 		s.delete( baz );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testComplicatedQuery() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		Foo foo = new Foo();
 		Serializable id = s.save(foo);
 		assertTrue( id != null );
 		Qux q = new Qux("q");
 		foo.getDependent().setQux(q);
 		s.save( q );
 		q.getFoo().setString( "foo2" );
 		//s.flush();
 		//s.connection().commit();
 		assertTrue(
 				s.createQuery( "from Foo foo where foo.dependent.qux.foo.string = 'foo2'" ).iterate().hasNext()
 		);
 		s.delete( foo );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testLoadAfterDelete() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Foo foo = new Foo();
 		Serializable id = s.save(foo);
 		s.flush();
 		s.delete(foo);
 		boolean err=false;
 		try {
 			s.load(Foo.class, id);
 		}
 		catch (ObjectNotFoundException ode) {
 			err=true;
 		}
 		assertTrue(err);
 		s.flush();
 		err=false;
 		try {
 			( (FooProxy) s.load(Foo.class, id) ).getBool();
 		}
 		catch (ObjectNotFoundException onfe) {
 			err=true;
 		}
 		assertTrue(err);
 		id = FumTest.fumKey( "abc" ); //yuck!!
 		Fo fo = Fo.newFo( (FumCompositeID) id );
 		s.save(fo);
 		s.flush();
 		s.delete(fo);
 		err=false;
 		try {
 			s.load(Fo.class, id);
 		}
 		catch (ObjectNotFoundException ode) {
 			err=true;
 		}
 		assertTrue(err);
 		s.flush();
 		err=false;
 		try {
 			s.load(Fo.class, id);
 		}
 		catch (ObjectNotFoundException onfe) {
 			err=true;
 		}
 		assertTrue(err);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testObjectType() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		GlarchProxy g = new Glarch();
 		Foo foo = new Foo();
 		g.setAny( foo );
 		Serializable gid = s.save( g );
 		s.save(foo);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		g = (GlarchProxy) s.load(Glarch.class, gid);
 		assertTrue( g.getAny()!=null && g.getAny() instanceof FooProxy );
 		s.delete( g.getAny() );
 		s.delete( g );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testAny() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		One one = new One();
 		BarProxy foo = new Bar();
 		foo.setObject(one);
 		Serializable fid = s.save(foo);
 		Serializable oid = one.getKey();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		List results = s.createQuery( "from Bar bar where bar.object.id = ? and bar.object.class = ?" )
 				.setParameter( 0, oid, StandardBasicTypes.LONG )
 				.setParameter( 1, new Character('O'), StandardBasicTypes.CHARACTER )
 				.list();
 		assertEquals( 1, results.size() );
 		results = s.createQuery( "select one from One one, Bar bar where bar.object.id = one.id and bar.object.class = 'O'" )
 				.list();
 		assertEquals( 1, results.size() );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		foo = (BarProxy) s.load(Foo.class, fid);
 		assertTrue( foo.getObject()!=null && foo.getObject() instanceof One && s.getIdentifier( foo.getObject() ).equals(oid) );
 		//s.delete( foo.getObject() );
 		s.delete(foo);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testEmbeddedCompositeID() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Location l = new Location();
 		l.setCountryCode("AU");
 		l.setDescription("foo bar");
 		l.setLocale( Locale.getDefault() );
 		l.setStreetName("Brunswick Rd");
 		l.setStreetNumber(300);
 		l.setCity("Melbourne");
 		s.save(l);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		s.setFlushMode(FlushMode.MANUAL);
 		l = (Location) s.createQuery( "from Location l where l.countryCode = 'AU' and l.description='foo bar'" )
 				.list()
 				.get(0);
 		assertTrue( l.getCountryCode().equals("AU") );
 		assertTrue( l.getCity().equals("Melbourne") );
 		assertTrue( l.getLocale().equals( Locale.getDefault() ) );
 		assertTrue( s.createCriteria(Location.class).add( Restrictions.eq( "streetNumber", new Integer(300) ) ).list().size()==1 );
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		l.setDescription("sick're");
 		s.update(l);
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		l = new Location();
 		l.setCountryCode("AU");
 		l.setDescription("foo bar");
 		l.setLocale(Locale.ENGLISH);
 		l.setStreetName("Brunswick Rd");
 		l.setStreetNumber(300);
 		l.setCity("Melbourne");
 		assertTrue( l==s.load(Location.class, l) );
 		assertTrue( l.getLocale().equals( Locale.getDefault() ) );
 		s.delete(l);
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testAutosaveChildren() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		Set bars = new HashSet();
 		baz.setCascadingBars(bars);
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		baz.getCascadingBars().add( new Bar() );
 		baz.getCascadingBars().add( new Bar() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		assertTrue( baz.getCascadingBars().size()==2 );
 		assertTrue( baz.getCascadingBars().iterator().next()!=null );
 		baz.getCascadingBars().clear(); //test all-delete-orphan;
 		s.flush();
 		assertTrue( s.createQuery( "from Bar bar" ).list().size()==0 );
 		s.delete(baz);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testOrphanDelete() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		Set bars = new HashSet();
 		baz.setCascadingBars(bars);
 		bars.add( new Bar() );
 		bars.add( new Bar() );
 		bars.add( new Bar() );
 		bars.add( new Bar() );
 		s.save(baz);
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		bars = baz.getCascadingBars();
 		assertEquals( 4, bars.size() );
 		bars.remove( bars.iterator().next() );
 		assertEquals( 3, s.createQuery( "From Bar bar" ).list().size() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		baz = (Baz) s.load( Baz.class, baz.getCode() );
 		bars = baz.getCascadingBars();
 		assertEquals( 3, bars.size() );
 		bars.remove( bars.iterator().next() );
 		s.delete(baz);
 		bars.remove( bars.iterator().next() );
 		assertEquals( 0, s.createQuery( "From Bar bar" ).list().size() );
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testTransientOrphanDelete() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		Set bars = new HashSet();
 		baz.setCascadingBars(bars);
 		bars.add( new Bar() );
 		bars.add( new Bar() );
 		bars.add( new Bar() );
 		List foos = new ArrayList();
 		foos.add( new Foo() );
 		foos.add( new Foo() );
 		baz.setFooBag(foos);
 		s.save(baz);
 		Iterator i = new JoinedIterator( new Iterator[] {foos.iterator(), bars.iterator()} );
 		while ( i.hasNext() ) {
 			FooComponent cmp = ( (Foo) i.next() ).getComponent();
 			s.delete( cmp.getGlarch() );
 			cmp.setGlarch(null);
 		}
 		t.commit();
 		s.close();
 
 		bars.remove( bars.iterator().next() );
 		foos.remove(1);
 		s = openSession();
 		t = s.beginTransaction();
 		s.update(baz);
 		assertEquals( 2, s.createQuery( "From Bar bar" ).list().size() );
 		assertEquals( 3, s.createQuery( "From Foo foo" ).list().size() );
 		t.commit();
 		s.close();
 
 		foos.remove(0);
 		s = openSession();
 		t = s.beginTransaction();
 		s.update(baz);
 		bars.remove( bars.iterator().next() );
 		assertEquals( 1, s.createQuery( "From Foo foo" ).list().size() );
 		s.delete(baz);
 		//s.flush();
 		assertEquals( 0, s.createQuery( "From Foo foo" ).list().size() );
 		t.commit();
 		s.close();
 	}
 
-	@Test
-	@FailureExpected( jiraKey = "HHH-8662")
+	@TestForIssue( jiraKey = "HHH-8662" )
 	public void testProxiesInCollections() throws Exception {
 		Session s = openSession();
 		s.beginTransaction();
 		Baz baz = new Baz();
 		Bar bar = new Bar();
 		Bar bar2 = new Bar();
 		s.save(bar);
 		Serializable bar2id = s.save(bar2);
 		baz.setFooArray( new Foo[] { bar, bar2 } );
 		HashSet set = new HashSet();
 		bar = new Bar();
 		s.save(bar);
 		set.add(bar);
 		baz.setFooSet(set);
 		set = new HashSet();
 		set.add( new Bar() );
 		set.add( new Bar() );
 		baz.setCascadingBars(set);
 		ArrayList list = new ArrayList();
 		list.add( new Foo() );
 		baz.setFooBag(list);
 		Serializable id = s.save(baz);
 		Serializable bid = ( (Bar) baz.getCascadingBars().iterator().next() ).getKey();
 		s.getTransaction().commit();
 		s.close();
 
 		s = openSession();
 		s.beginTransaction();
 		BarProxy barprox = (BarProxy) s.load(Bar.class, bid);
 		BarProxy bar2prox = (BarProxy) s.load(Bar.class, bar2id);
 		assertTrue(bar2prox instanceof HibernateProxy);
 		assertTrue(barprox instanceof HibernateProxy);
 		baz = (Baz) s.load(Baz.class, id);
 		Iterator i = baz.getCascadingBars().iterator();
 		BarProxy b1 = (BarProxy) i.next();
 		BarProxy b2 = (BarProxy) i.next();
 		assertTrue( ( b1==barprox && !(b2 instanceof HibernateProxy) ) || ( b2==barprox && !(b1 instanceof HibernateProxy) ) ); //one-to-many
-		assertTrue( baz.getFooArray()[0] instanceof HibernateProxy ); //many-to-many
+		// <many-to-many fetch="select" is deprecated by HHH-8662; so baz.getFooArray()[0] should not be a HibernateProxy.
+		assertFalse( baz.getFooArray()[0] instanceof HibernateProxy ); //many-to-many
 		assertTrue( baz.getFooArray()[1]==bar2prox );
 		if ( !isOuterJoinFetchingDisabled() ) assertTrue( !(baz.getFooBag().iterator().next() instanceof HibernateProxy) ); //many-to-many outer-join="true"
 		assertTrue( !(baz.getFooSet().iterator().next() instanceof HibernateProxy) ); //one-to-many
 		doDelete( s, "from Baz" );
 		doDelete( s, "from Foo" );
 		s.getTransaction().commit();
 		s.close();
 	}
 
 	@Test
 	public void testPSCache() throws Exception {
 		Session s = openSession();
 		Transaction txn = s.beginTransaction();
 		for ( int i=0; i<10; i++ ) s.save( new Foo() );
 		Query q = s.createQuery("from Foo");
 		q.setMaxResults(2);
 		q.setFirstResult(5);
 		assertTrue( q.list().size()==2 );
 		q = s.createQuery("from Foo");
 		assertTrue( q.list().size()==10 );
 		assertTrue( q.list().size()==10 );
 		q.setMaxResults(3);
 		q.setFirstResult(3);
 		assertTrue( q.list().size()==3 );
 		q = s.createQuery("from Foo");
 		assertTrue( q.list().size()==10 );
 		txn.commit();
 		s.close();
 
 		s = openSession();
 		txn = s.beginTransaction();
 		q = s.createQuery("from Foo");
 		assertTrue( q.list().size()==10 );
 		q.setMaxResults(5);
 		assertTrue( q.list().size()==5 );
 		doDelete( s, "from Foo" );
 		txn.commit();
 		s.close();
 	}
 
 	@Test
 	public void testForCertain() throws Exception {
 		Glarch g = new Glarch();
 		Glarch g2 = new Glarch();
 		List set = new ArrayList();
 		set.add("foo");
 		g2.setStrings(set);
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Serializable gid = s.save(g);
 		Serializable g2id = s.save(g2);
 		t.commit();
 		assertTrue( g.getVersion()==0 );
 		assertTrue( g2.getVersion()==0 );
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		g = (Glarch) s.get(Glarch.class, gid);
 		g2 = (Glarch) s.get(Glarch.class, g2id);
 		assertTrue( g2.getStrings().size()==1 );
 		s.delete(g);
 		s.delete(g2);
 		t.commit();
 		s.close();
 	}
 
 	@Test
 	public void testBagMultipleElements() throws Exception {
 		Session s = openSession();
 		Transaction t = s.beginTransaction();
 		Baz baz = new Baz();
 		baz.setBag( new ArrayList() );
 		baz.setByteBag( new ArrayList() );
 		s.save(baz);
 		baz.getBag().add("foo");
 		baz.getBag().add("bar");
 		baz.getByteBag().add( "foo".getBytes() );
 		baz.getByteBag().add( "bar".getBytes() );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		//put in cache
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertTrue( baz.getBag().size()==2 );
 		assertTrue( baz.getByteBag().size()==2 );
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		baz = (Baz) s.get( Baz.class, baz.getCode() );
 		assertTrue( baz.getBag().size()==2 );
 		assertTrue( baz.getByteBag().size()==2 );
 		baz.getBag().remove("bar");
  		baz.getBag().add("foo");
  		baz.getByteBag().add( "bar".getBytes() );
 		t.commit();
 		s.close();
 
  		s = openSession();
  		t = s.beginTransaction();
  		baz = (Baz) s.get( Baz.class, baz.getCode() );
  		assertTrue( baz.getBag().size()==2 );
  		assertTrue( baz.getByteBag().size()==3 );
  		s.delete(baz);
  		t.commit();
  		s.close();
  	}
 
 	@Test
 	public void testWierdSession() throws Exception {
  		Session s = openSession();
  		Transaction t = s.beginTransaction();
  		Serializable id =  s.save( new Foo() );
  		t.commit();
  		s.close();
 
  		s = openSession();
  		s.setFlushMode(FlushMode.MANUAL);
 		t = s.beginTransaction();
 		Foo foo = (Foo) s.get(Foo.class, id);
 		t.commit();
 
 		t = s.beginTransaction();
 		s.flush();
 		t.commit();
 		s.close();
 
 		s = openSession();
 		t = s.beginTransaction();
 		foo = (Foo) s.get(Foo.class, id);
 		s.delete(foo);
 		t.commit();
 		s.close();
 	}
 
 }
