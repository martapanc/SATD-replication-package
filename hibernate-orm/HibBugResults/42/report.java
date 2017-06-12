File path: code/core/src/main/java/org/hibernate/cfg/HbmBinder.java
Comment: we have to set up the table later!! yuck
Initial commit id: d8d6d82e
Final commit id: 9caca0ce
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 1289
End block index: 1486
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
			if ( Environment.jvmSupportsLinkedHashCollections() || ( collection instanceof Bag ) ) {
				collection.setOrderBy( orderNode.getValue() );
			}
			else {
				log.warn( "Attribute \"order-by\" ignored in JDK1.3 or less" );
			}
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
			OneToMany oneToMany = new OneToMany( collection.getOwner() );
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

			log.info(
					"Mapping collection: " + collection.getRole() +
					" -> " + collection.getCollectionTable().getName()
				);
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
