diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index f28d1ed1b0..0fc3a581fb 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -153,2001 +153,2001 @@ public final class HbmBinder {
 		extractRootAttributes( hibernateMappingElement, mappings );
 
 		Iterator rootChildren = hibernateMappingElement.elementIterator();
 		while ( rootChildren.hasNext() ) {
 			final Element element = (Element) rootChildren.next();
 			final String elementName = element.getName();
 
 			if ( "filter-def".equals( elementName ) ) {
 				parseFilterDef( element, mappings );
 			}
 			else if ( "fetch-profile".equals( elementName ) ) {
 				parseFetchProfile( element, mappings, null );
 			}
 			else if ( "identifier-generator".equals( elementName ) ) {
 				parseIdentifierGeneratorRegistration( element, mappings );
 			}
 			else if ( "typedef".equals( elementName ) ) {
 				bindTypeDef( element, mappings );
 			}
 			else if ( "class".equals( elementName ) ) {
 				RootClass rootclass = new RootClass();
 				bindRootClass( element, rootclass, mappings, inheritedMetas );
 				mappings.addClass( rootclass );
 			}
 			else if ( "subclass".equals( elementName ) ) {
 				PersistentClass superModel = getSuperclass( mappings, element );
 				handleSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( "joined-subclass".equals( elementName ) ) {
 				PersistentClass superModel = getSuperclass( mappings, element );
 				handleJoinedSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( "union-subclass".equals( elementName ) ) {
 				PersistentClass superModel = getSuperclass( mappings, element );
 				handleUnionSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( "query".equals( elementName ) ) {
 				bindNamedQuery( element, null, mappings );
 			}
 			else if ( "sql-query".equals( elementName ) ) {
 				bindNamedSQLQuery( element, null, mappings );
 			}
 			else if ( "resultset".equals( elementName ) ) {
 				bindResultSetMappingDefinition( element, null, mappings );
 			}
 			else if ( "import".equals( elementName ) ) {
 				bindImport( element, mappings );
 			}
 			else if ( "database-object".equals( elementName ) ) {
 				bindAuxiliaryDatabaseObject( element, mappings );
 			}
 		}
 	}
 
 	private static void parseIdentifierGeneratorRegistration(Element element, Mappings mappings) {
 		String strategy = element.attributeValue( "name" );
 		if ( StringHelper.isEmpty( strategy ) ) {
 			throw new MappingException( "'name' attribute expected for identifier-generator elements" );
 		}
 		String generatorClassName = element.attributeValue( "class" );
 		if ( StringHelper.isEmpty( generatorClassName ) ) {
 			throw new MappingException( "'class' attribute expected for identifier-generator [identifier-generator@name=" + strategy + "]" );
 		}
 
 		try {
 			Class generatorClass = ReflectHelper.classForName( generatorClassName );
 			mappings.getIdentifierGeneratorFactory().register( strategy, generatorClass );
 		}
 		catch ( ClassNotFoundException e ) {
 			throw new MappingException( "Unable to locate identifier-generator class [name=" + strategy + ", class=" + generatorClassName + "]" );
 		}
 
 	}
 
 	private static void bindImport(Element importNode, Mappings mappings) {
 		String className = getClassName( importNode.attribute( "class" ), mappings );
 		Attribute renameNode = importNode.attribute( "rename" );
 		String rename = ( renameNode == null ) ?
 						StringHelper.unqualify( className ) :
 						renameNode.getValue();
         LOG.debugf("Import: %s -> %s", rename, className);
 		mappings.addImport( className, rename );
 	}
 
 	private static void bindTypeDef(Element typedefNode, Mappings mappings) {
 		String typeClass = typedefNode.attributeValue( "class" );
 		String typeName = typedefNode.attributeValue( "name" );
 		Iterator paramIter = typedefNode.elementIterator( "param" );
 		Properties parameters = new Properties();
 		while ( paramIter.hasNext() ) {
 			Element param = (Element) paramIter.next();
 			parameters.setProperty( param.attributeValue( "name" ), param.getTextTrim() );
 		}
 		mappings.addTypeDef( typeName, typeClass, parameters );
 	}
 
 	private static void bindAuxiliaryDatabaseObject(Element auxDbObjectNode, Mappings mappings) {
 		AuxiliaryDatabaseObject auxDbObject = null;
 		Element definitionNode = auxDbObjectNode.element( "definition" );
 		if ( definitionNode != null ) {
 			try {
 				auxDbObject = ( AuxiliaryDatabaseObject ) ReflectHelper
 						.classForName( definitionNode.attributeValue( "class" ) )
 						.newInstance();
 			}
 			catch( ClassNotFoundException e ) {
 				throw new MappingException(
 						"could not locate custom database object class [" +
 						definitionNode.attributeValue( "class" ) + "]"
 					);
 			}
 			catch( Throwable t ) {
 				throw new MappingException(
 						"could not instantiate custom database object class [" +
 						definitionNode.attributeValue( "class" ) + "]"
 					);
 			}
 		}
 		else {
 			auxDbObject = new SimpleAuxiliaryDatabaseObject(
 					auxDbObjectNode.elementTextTrim( "create" ),
 					auxDbObjectNode.elementTextTrim( "drop" )
 				);
 		}
 
 		Iterator dialectScopings = auxDbObjectNode.elementIterator( "dialect-scope" );
 		while ( dialectScopings.hasNext() ) {
 			Element dialectScoping = ( Element ) dialectScopings.next();
 			auxDbObject.addDialectScope( dialectScoping.attributeValue( "name" ) );
 		}
 
 		mappings.addAuxiliaryDatabaseObject( auxDbObject );
 	}
 
 	private static void extractRootAttributes(Element hmNode, Mappings mappings) {
 		Attribute schemaNode = hmNode.attribute( "schema" );
 		mappings.setSchemaName( ( schemaNode == null ) ? null : schemaNode.getValue() );
 
 		Attribute catalogNode = hmNode.attribute( "catalog" );
 		mappings.setCatalogName( ( catalogNode == null ) ? null : catalogNode.getValue() );
 
 		Attribute dcNode = hmNode.attribute( "default-cascade" );
 		mappings.setDefaultCascade( ( dcNode == null ) ? "none" : dcNode.getValue() );
 
 		Attribute daNode = hmNode.attribute( "default-access" );
 		mappings.setDefaultAccess( ( daNode == null ) ? "property" : daNode.getValue() );
 
 		Attribute dlNode = hmNode.attribute( "default-lazy" );
 		mappings.setDefaultLazy( dlNode == null || dlNode.getValue().equals( "true" ) );
 
 		Attribute aiNode = hmNode.attribute( "auto-import" );
 		mappings.setAutoImport( ( aiNode == null ) || "true".equals( aiNode.getValue() ) );
 
 		Attribute packNode = hmNode.attribute( "package" );
 		if ( packNode != null ) mappings.setDefaultPackage( packNode.getValue() );
 	}
 
 	/**
 	 * Responsible for perfoming the bind operation related to an &lt;class/&gt; mapping element.
 	 *
 	 * @param node The DOM Element for the &lt;class/&gt; element.
 	 * @param rootClass The mapping instance to which to bind the information.
 	 * @param mappings The current bind state.
 	 * @param inheritedMetas Any inherited meta-tag information.
 	 * @throws MappingException
 	 */
 	public static void bindRootClass(Element node, RootClass rootClass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		bindClass( node, rootClass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <class>
 		bindRootPersistentClassCommonValues( node, inheritedMetas, mappings, rootClass );
 	}
 
 	private static void bindRootPersistentClassCommonValues(Element node,
 			java.util.Map inheritedMetas, Mappings mappings, RootClass entity)
 			throws MappingException {
 
 		// DB-OBJECTNAME
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table table = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( entity, node, schema, catalog, null, mappings ),
 				getSubselect( node ),
 		        entity.isAbstract() != null && entity.isAbstract().booleanValue()
 			);
 		entity.setTable( table );
 		bindComment(table, node);
 
         LOG.debugf( "Mapping class: %s -> %s", entity.getEntityName(), entity.getTable().getName() );
 
 		// MUTABLE
 		Attribute mutableNode = node.attribute( "mutable" );
 		entity.setMutable( ( mutableNode == null ) || mutableNode.getValue().equals( "true" ) );
 
 		// WHERE
 		Attribute whereNode = node.attribute( "where" );
 		if ( whereNode != null ) entity.setWhere( whereNode.getValue() );
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) table.addCheckConstraint( chNode.getValue() );
 
 		// POLYMORPHISM
 		Attribute polyNode = node.attribute( "polymorphism" );
 		entity.setExplicitPolymorphism( ( polyNode != null )
 			&& polyNode.getValue().equals( "explicit" ) );
 
 		// ROW ID
 		Attribute rowidNode = node.attribute( "rowid" );
 		if ( rowidNode != null ) table.setRowId( rowidNode.getValue() );
 
 		Iterator subnodes = node.elementIterator();
 		while ( subnodes.hasNext() ) {
 
 			Element subnode = (Element) subnodes.next();
 			String name = subnode.getName();
 
 			if ( "id".equals( name ) ) {
 				// ID
 				bindSimpleId( subnode, entity, mappings, inheritedMetas );
 			}
 			else if ( "composite-id".equals( name ) ) {
 				// COMPOSITE-ID
 				bindCompositeId( subnode, entity, mappings, inheritedMetas );
 			}
 			else if ( "version".equals( name ) || "timestamp".equals( name ) ) {
 				// VERSION / TIMESTAMP
 				bindVersioningProperty( table, subnode, mappings, name, entity, inheritedMetas );
 			}
 			else if ( "discriminator".equals( name ) ) {
 				// DISCRIMINATOR
 				bindDiscriminatorProperty( table, entity, subnode, mappings );
 			}
 			else if ( "cache".equals( name ) ) {
 				entity.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
 				entity.setCacheRegionName( subnode.attributeValue( "region" ) );
 				entity.setLazyPropertiesCacheable( !"non-lazy".equals( subnode.attributeValue( "include" ) ) );
 			}
 
 		}
 
 		// Primary key constraint
 		entity.createPrimaryKey();
 
 		createClassProperties( node, entity, mappings, inheritedMetas );
 	}
 
 	private static void bindSimpleId(Element idNode, RootClass entity, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		String propertyName = idNode.attributeValue( "name" );
 
 		SimpleValue id = new SimpleValue( mappings, entity.getTable() );
 		entity.setIdentifier( id );
 
 		// if ( propertyName == null || entity.getPojoRepresentation() == null ) {
 		// bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 		// if ( !id.isTypeSpecified() ) {
 		// throw new MappingException( "must specify an identifier type: " + entity.getEntityName()
 		// );
 		// }
 		// }
 		// else {
 		// bindSimpleValue( idNode, id, false, propertyName, mappings );
 		// PojoRepresentation pojo = entity.getPojoRepresentation();
 		// id.setTypeUsingReflection( pojo.getClassName(), propertyName );
 		//
 		// Property prop = new Property();
 		// prop.setValue( id );
 		// bindProperty( idNode, prop, mappings, inheritedMetas );
 		// entity.setIdentifierProperty( prop );
 		// }
 
 		if ( propertyName == null ) {
 			bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 		}
 		else {
 			bindSimpleValue( idNode, id, false, propertyName, mappings );
 		}
 
 		if ( propertyName == null || !entity.hasPojoRepresentation() ) {
 			if ( !id.isTypeSpecified() ) {
 				throw new MappingException( "must specify an identifier type: "
 					+ entity.getEntityName() );
 			}
 		}
 		else {
 			id.setTypeUsingReflection( entity.getClassName(), propertyName );
 		}
 
 		if ( propertyName != null ) {
 			Property prop = new Property();
 			prop.setValue( id );
 			bindProperty( idNode, prop, mappings, inheritedMetas );
 			entity.setIdentifierProperty( prop );
 		}
 
 		// TODO:
 		/*
 		 * if ( id.getHibernateType().getReturnedClass().isArray() ) throw new MappingException(
 		 * "illegal use of an array as an identifier (arrays don't reimplement equals)" );
 		 */
 		makeIdentifier( idNode, id, mappings );
 	}
 
 	private static void bindCompositeId(Element idNode, RootClass entity, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		String propertyName = idNode.attributeValue( "name" );
 		Component id = new Component( mappings, entity );
 		entity.setIdentifier( id );
 		bindCompositeId( idNode, id, entity, propertyName, mappings, inheritedMetas );
 		if ( propertyName == null ) {
 			entity.setEmbeddedIdentifier( id.isEmbedded() );
 			if ( id.isEmbedded() ) {
 				// todo : what is the implication of this?
 				id.setDynamic( !entity.hasPojoRepresentation() );
 				/*
 				 * Property prop = new Property(); prop.setName("id");
 				 * prop.setPropertyAccessorName("embedded"); prop.setValue(id);
 				 * entity.setIdentifierProperty(prop);
 				 */
 			}
 		}
 		else {
 			Property prop = new Property();
 			prop.setValue( id );
 			bindProperty( idNode, prop, mappings, inheritedMetas );
 			entity.setIdentifierProperty( prop );
 		}
 
 		makeIdentifier( idNode, id, mappings );
 
 	}
 
 	private static void bindVersioningProperty(Table table, Element subnode, Mappings mappings,
 			String name, RootClass entity, java.util.Map inheritedMetas) {
 
 		String propertyName = subnode.attributeValue( "name" );
 		SimpleValue val = new SimpleValue( mappings, table );
 		bindSimpleValue( subnode, val, false, propertyName, mappings );
 		if ( !val.isTypeSpecified() ) {
 			// this is either a <version/> tag with no type attribute,
 			// or a <timestamp/> tag
 			if ( "version".equals( name ) ) {
 				val.setTypeName( "integer" );
 			}
 			else {
 				if ( "db".equals( subnode.attributeValue( "source" ) ) ) {
 					val.setTypeName( "dbtimestamp" );
 				}
 				else {
 					val.setTypeName( "timestamp" );
 				}
 			}
 		}
 		Property prop = new Property();
 		prop.setValue( val );
 		bindProperty( subnode, prop, mappings, inheritedMetas );
 		// for version properties marked as being generated, make sure they are "always"
 		// generated; aka, "insert" is invalid; this is dis-allowed by the DTD,
 		// but just to make sure...
 		if ( prop.getGeneration() == PropertyGeneration.INSERT ) {
 			throw new MappingException( "'generated' attribute cannot be 'insert' for versioning property" );
 		}
 		makeVersion( subnode, val );
 		entity.setVersion( prop );
 		entity.addProperty( prop );
 	}
 
 	private static void bindDiscriminatorProperty(Table table, RootClass entity, Element subnode,
 			Mappings mappings) {
 		SimpleValue discrim = new SimpleValue( mappings, table );
 		entity.setDiscriminator( discrim );
 		bindSimpleValue(
 				subnode,
 				discrim,
 				false,
 				RootClass.DEFAULT_DISCRIMINATOR_COLUMN_NAME,
 				mappings
 			);
 		if ( !discrim.isTypeSpecified() ) {
 			discrim.setTypeName( "string" );
 			// ( (Column) discrim.getColumnIterator().next() ).setType(type);
 		}
 		entity.setPolymorphic( true );
 		if ( "true".equals( subnode.attributeValue( "force" ) ) )
 			entity.setForceDiscriminator( true );
 		if ( "false".equals( subnode.attributeValue( "insert" ) ) )
 			entity.setDiscriminatorInsertable( false );
 	}
 
 	public static void bindClass(Element node, PersistentClass persistentClass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 		// transfer an explicitly defined entity name
 		// handle the lazy attribute
 		Attribute lazyNode = node.attribute( "lazy" );
 		boolean lazy = lazyNode == null ?
 				mappings.isDefaultLazy() :
 				"true".equals( lazyNode.getValue() );
 		// go ahead and set the lazy here, since pojo.proxy can override it.
 		persistentClass.setLazy( lazy );
 
 		String entityName = node.attributeValue( "entity-name" );
 		if ( entityName == null ) entityName = getClassName( node.attribute("name"), mappings );
 		if ( entityName==null ) {
 			throw new MappingException( "Unable to determine entity name" );
 		}
 		persistentClass.setEntityName( entityName );
 
 		bindPojoRepresentation( node, persistentClass, mappings, inheritedMetas );
 		bindDom4jRepresentation( node, persistentClass, mappings, inheritedMetas );
 		bindMapRepresentation( node, persistentClass, mappings, inheritedMetas );
 
 		Iterator itr = node.elementIterator( "fetch-profile" );
 		while ( itr.hasNext() ) {
 			final Element profileElement = ( Element ) itr.next();
 			parseFetchProfile( profileElement, mappings, entityName );
 		}
 
 		bindPersistentClassCommonValues( node, persistentClass, mappings, inheritedMetas );
 	}
 
 	private static void bindPojoRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map metaTags) {
 
 		String className = getClassName( node.attribute( "name" ), mappings );
 		String proxyName = getClassName( node.attribute( "proxy" ), mappings );
 
 		entity.setClassName( className );
 
 		if ( proxyName != null ) {
 			entity.setProxyInterfaceName( proxyName );
 			entity.setLazy( true );
 		}
 		else if ( entity.isLazy() ) {
 			entity.setProxyInterfaceName( className );
 		}
 
 		Element tuplizer = locateTuplizerDefinition( node, EntityMode.POJO );
 		if ( tuplizer != null ) {
 			entity.addTuplizer( EntityMode.POJO, tuplizer.attributeValue( "class" ) );
 		}
 	}
 
 	private static void bindDom4jRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) {
 		String nodeName = node.attributeValue( "node" );
 		if (nodeName==null) nodeName = StringHelper.unqualify( entity.getEntityName() );
 		entity.setNodeName(nodeName);
 
 //		Element tuplizer = locateTuplizerDefinition( node, EntityMode.DOM4J );
 //		if ( tuplizer != null ) {
 //			entity.addTuplizer( EntityMode.DOM4J, tuplizer.attributeValue( "class" ) );
 //		}
 	}
 
 	private static void bindMapRepresentation(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) {
 		Element tuplizer = locateTuplizerDefinition( node, EntityMode.MAP );
 		if ( tuplizer != null ) {
 			entity.addTuplizer( EntityMode.MAP, tuplizer.attributeValue( "class" ) );
 		}
 	}
 
 	/**
 	 * Locate any explicit tuplizer definition in the metadata, for the given entity-mode.
 	 *
 	 * @param container The containing element (representing the entity/component)
 	 * @param entityMode The entity-mode for which to locate the tuplizer element
 	 * @return The tuplizer element, or null.
 	 */
 	private static Element locateTuplizerDefinition(Element container, EntityMode entityMode) {
 		Iterator itr = container.elements( "tuplizer" ).iterator();
 		while( itr.hasNext() ) {
 			final Element tuplizerElem = ( Element ) itr.next();
 			if ( entityMode.toString().equals( tuplizerElem.attributeValue( "entity-mode") ) ) {
 				return tuplizerElem;
 			}
 		}
 		return null;
 	}
 
 	private static void bindPersistentClassCommonValues(Element node, PersistentClass entity,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 		// DISCRIMINATOR
 		Attribute discriminatorNode = node.attribute( "discriminator-value" );
 		entity.setDiscriminatorValue( ( discriminatorNode == null )
 			? entity.getEntityName()
 			: discriminatorNode.getValue() );
 
 		// DYNAMIC UPDATE
 		Attribute dynamicNode = node.attribute( "dynamic-update" );
 		entity.setDynamicUpdate(
 				dynamicNode != null && "true".equals( dynamicNode.getValue() )
 		);
 
 		// DYNAMIC INSERT
 		Attribute insertNode = node.attribute( "dynamic-insert" );
 		entity.setDynamicInsert(
 				insertNode != null && "true".equals( insertNode.getValue() )
 		);
 
 		// IMPORT
 		mappings.addImport( entity.getEntityName(), entity.getEntityName() );
 		if ( mappings.isAutoImport() && entity.getEntityName().indexOf( '.' ) > 0 ) {
 			mappings.addImport(
 					entity.getEntityName(),
 					StringHelper.unqualify( entity.getEntityName() )
 				);
 		}
 
 		// BATCH SIZE
 		Attribute batchNode = node.attribute( "batch-size" );
 		if ( batchNode != null ) entity.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
 
 		// SELECT BEFORE UPDATE
 		Attribute sbuNode = node.attribute( "select-before-update" );
 		if ( sbuNode != null ) entity.setSelectBeforeUpdate( "true".equals( sbuNode.getValue() ) );
 
 		// OPTIMISTIC LOCK MODE
 		Attribute olNode = node.attribute( "optimistic-lock" );
 		entity.setOptimisticLockMode( getOptimisticLockMode( olNode ) );
 
 		entity.setMetaAttributes( getMetas( node, inheritedMetas ) );
 
 		// PERSISTER
 		Attribute persisterNode = node.attribute( "persister" );
 		if ( persisterNode != null ) {
 			try {
 				entity.setEntityPersisterClass( ReflectHelper.classForName(
 						persisterNode
 								.getValue()
 				) );
 			}
 			catch (ClassNotFoundException cnfe) {
 				throw new MappingException( "Could not find persister class: "
 					+ persisterNode.getValue() );
 			}
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, entity );
 
 		Iterator tables = node.elementIterator( "synchronize" );
 		while ( tables.hasNext() ) {
 			entity.addSynchronizedTable( ( (Element) tables.next() ).attributeValue( "table" ) );
 		}
 
 		Attribute abstractNode = node.attribute( "abstract" );
 		Boolean isAbstract = abstractNode == null
 				? null
 		        : "true".equals( abstractNode.getValue() )
 						? Boolean.TRUE
 	                    : "false".equals( abstractNode.getValue() )
 								? Boolean.FALSE
 	                            : null;
 		entity.setAbstract( isAbstract );
 	}
 
 	private static void handleCustomSQL(Element node, PersistentClass model)
 			throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "loader" );
 		if ( element != null ) {
 			model.setLoaderName( element.attributeValue( "query-ref" ) );
 		}
 	}
 
 	private static void handleCustomSQL(Element node, Join model) throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 	}
 
 	private static void handleCustomSQL(Element node, Collection model) throws MappingException {
 		Element element = node.element( "sql-insert" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLInsert( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLDelete( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-update" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLUpdate( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 
 		element = node.element( "sql-delete-all" );
 		if ( element != null ) {
 			boolean callable = isCallable( element, true );
 			model.setCustomSQLDeleteAll( element.getTextTrim(), callable, getResultCheckStyle( element, callable ) );
 		}
 	}
 
 	private static boolean isCallable(Element e) throws MappingException {
 		return isCallable( e, true );
 	}
 
 	private static boolean isCallable(Element element, boolean supportsCallable)
 			throws MappingException {
 		Attribute attrib = element.attribute( "callable" );
 		if ( attrib != null && "true".equals( attrib.getValue() ) ) {
 			if ( !supportsCallable ) {
 				throw new MappingException( "callable attribute not supported yet!" );
 			}
 			return true;
 		}
 		return false;
 	}
 
 	private static ExecuteUpdateResultCheckStyle getResultCheckStyle(Element element, boolean callable) throws MappingException {
 		Attribute attr = element.attribute( "check" );
 		if ( attr == null ) {
 			// use COUNT as the default.  This mimics the old behavior, although
 			// NONE might be a better option moving forward in the case of callable
 			return ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		return ExecuteUpdateResultCheckStyle.fromExternalName( attr.getValue() );
 	}
 
 	public static void bindUnionSubclass(Element node, UnionSubclass unionSubclass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, unionSubclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <subclass>
 
 		if ( unionSubclass.getEntityPersisterClass() == null ) {
 			unionSubclass.getRootClass().setEntityPersisterClass(
 				UnionSubclassEntityPersister.class );
 		}
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table denormalizedSuperTable = unionSubclass.getSuperclass().getTable();
 		Table mytable = mappings.addDenormalizedTable(
 				schema,
 				catalog,
 				getClassTableName(unionSubclass, node, schema, catalog, denormalizedSuperTable, mappings ),
 		        unionSubclass.isAbstract() != null && unionSubclass.isAbstract().booleanValue(),
 				getSubselect( node ),
 				denormalizedSuperTable
 			);
 		unionSubclass.setTable( mytable );
 
         LOG.debugf(
 				"Mapping union-subclass: %s -> %s", unionSubclass.getEntityName(), unionSubclass.getTable().getName()
 		);
 
 		createClassProperties( node, unionSubclass, mappings, inheritedMetas );
 
 	}
 
 	public static void bindSubclass(Element node, Subclass subclass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, subclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <subclass>
 
 		if ( subclass.getEntityPersisterClass() == null ) {
 			subclass.getRootClass()
 					.setEntityPersisterClass( SingleTableEntityPersister.class );
 		}
 
         LOG.debugf( "Mapping subclass: %s -> %s", subclass.getEntityName(), subclass.getTable().getName() );
 
 		// properties
 		createClassProperties( node, subclass, mappings, inheritedMetas );
 	}
 
 	private static String getClassTableName(
 			PersistentClass model,
 			Element node,
 			String schema,
 			String catalog,
 			Table denormalizedSuperTable,
 			Mappings mappings) {
 		Attribute tableNameNode = node.attribute( "table" );
 		String logicalTableName;
 		String physicalTableName;
 		if ( tableNameNode == null ) {
 			logicalTableName = StringHelper.unqualify( model.getEntityName() );
 			physicalTableName = mappings.getNamingStrategy().classToTableName( model.getEntityName() );
 		}
 		else {
 			logicalTableName = tableNameNode.getValue();
 			physicalTableName = mappings.getNamingStrategy().tableName( logicalTableName );
 		}
 		mappings.addTableBinding( schema, catalog, logicalTableName, physicalTableName, denormalizedSuperTable );
 		return physicalTableName;
 	}
 
 	public static void bindJoinedSubclass(Element node, JoinedSubclass joinedSubclass,
 			Mappings mappings, java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, joinedSubclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from
 																	// <joined-subclass>
 
 		// joined subclasses
 		if ( joinedSubclass.getEntityPersisterClass() == null ) {
 			joinedSubclass.getRootClass()
 				.setEntityPersisterClass( JoinedSubclassEntityPersister.class );
 		}
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 
 		Table mytable = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( joinedSubclass, node, schema, catalog, null, mappings ),
 				getSubselect( node ),
 				false
 			);
 		joinedSubclass.setTable( mytable );
 		bindComment(mytable, node);
 
         LOG.debugf(
 				"Mapping joined-subclass: %s -> %s", joinedSubclass.getEntityName(), joinedSubclass.getTable().getName()
 		);
 
 		// KEY
 		Element keyNode = node.element( "key" );
 		SimpleValue key = new DependantValue( mappings, mytable, joinedSubclass.getIdentifier() );
 		joinedSubclass.setKey( key );
 		key.setCascadeDeleteEnabled( "cascade".equals( keyNode.attributeValue( "on-delete" ) ) );
 		bindSimpleValue( keyNode, key, false, joinedSubclass.getEntityName(), mappings );
 
 		// model.getKey().setType( new Type( model.getIdentifier() ) );
 		joinedSubclass.createPrimaryKey();
 		joinedSubclass.createForeignKey();
 
 		// CHECK
 		Attribute chNode = node.attribute( "check" );
 		if ( chNode != null ) mytable.addCheckConstraint( chNode.getValue() );
 
 		// properties
 		createClassProperties( node, joinedSubclass, mappings, inheritedMetas );
 
 	}
 
 	private static void bindJoin(Element node, Join join, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		PersistentClass persistentClass = join.getPersistentClass();
 		String path = persistentClass.getEntityName();
 
 		// TABLENAME
 
 		Attribute schemaNode = node.attribute( "schema" );
 		String schema = schemaNode == null ?
 				mappings.getSchemaName() : schemaNode.getValue();
 		Attribute catalogNode = node.attribute( "catalog" );
 		String catalog = catalogNode == null ?
 				mappings.getCatalogName() : catalogNode.getValue();
 		Table primaryTable = persistentClass.getTable();
 		Table table = mappings.addTable(
 				schema,
 				catalog,
 				getClassTableName( persistentClass, node, schema, catalog, primaryTable, mappings ),
 				getSubselect( node ),
 				false
 			);
 		join.setTable( table );
 		bindComment(table, node);
 
 		Attribute fetchNode = node.attribute( "fetch" );
 		if ( fetchNode != null ) {
 			join.setSequentialSelect( "select".equals( fetchNode.getValue() ) );
 		}
 
 		Attribute invNode = node.attribute( "inverse" );
 		if ( invNode != null ) {
 			join.setInverse( "true".equals( invNode.getValue() ) );
 		}
 
 		Attribute nullNode = node.attribute( "optional" );
 		if ( nullNode != null ) {
 			join.setOptional( "true".equals( nullNode.getValue() ) );
 		}
 
 
         LOG.debugf( "Mapping class join: %s -> %s", persistentClass.getEntityName(), join.getTable().getName() );
 
 		// KEY
 		Element keyNode = node.element( "key" );
 		SimpleValue key = new DependantValue( mappings, table, persistentClass.getIdentifier() );
 		join.setKey( key );
 		key.setCascadeDeleteEnabled( "cascade".equals( keyNode.attributeValue( "on-delete" ) ) );
 		bindSimpleValue( keyNode, key, false, persistentClass.getEntityName(), mappings );
 
 		// join.getKey().setType( new Type( lazz.getIdentifier() ) );
 		join.createPrimaryKey();
 		join.createForeignKey();
 
 		// PROPERTIES
 		Iterator iter = node.elementIterator();
 		while ( iter.hasNext() ) {
 			Element subnode = (Element) iter.next();
 			String name = subnode.getName();
 			String propertyName = subnode.attributeValue( "name" );
 
 			Value value = null;
 			if ( "many-to-one".equals( name ) ) {
 				value = new ManyToOne( mappings, table );
 				bindManyToOne( subnode, (ManyToOne) value, propertyName, true, mappings );
 			}
 			else if ( "any".equals( name ) ) {
 				value = new Any( mappings, table );
 				bindAny( subnode, (Any) value, true, mappings );
 			}
 			else if ( "property".equals( name ) ) {
 				value = new SimpleValue( mappings, table );
 				bindSimpleValue( subnode, (SimpleValue) value, true, propertyName, mappings );
 			}
 			else if ( "component".equals( name ) || "dynamic-component".equals( name ) ) {
 				String subpath = StringHelper.qualify( path, propertyName );
 				value = new Component( mappings, join );
 				bindComponent(
 						subnode,
 						(Component) value,
 						join.getPersistentClass().getClassName(),
 						propertyName,
 						subpath,
 						true,
 						false,
 						mappings,
 						inheritedMetas,
 						false
 					);
 			}
 
 			if ( value != null ) {
 				Property prop = createProperty( value, propertyName, persistentClass
 					.getEntityName(), subnode, mappings, inheritedMetas );
 				prop.setOptional( join.isOptional() );
 				join.addProperty( prop );
 			}
 
 		}
 
 		// CUSTOM SQL
 		handleCustomSQL( node, join );
 
 	}
 
 	public static void bindColumns(final Element node, final SimpleValue simpleValue,
 			final boolean isNullable, final boolean autoColumn, final String propertyPath,
 			final Mappings mappings) throws MappingException {
 
 		Table table = simpleValue.getTable();
 
 		// COLUMN(S)
 		Attribute columnAttribute = node.attribute( "column" );
 		if ( columnAttribute == null ) {
 			Iterator itr = node.elementIterator();
 			int count = 0;
 			while ( itr.hasNext() ) {
 				Element columnElement = (Element) itr.next();
 				if ( columnElement.getName().equals( "column" ) ) {
 					Column column = new Column();
 					column.setValue( simpleValue );
 					column.setTypeIndex( count++ );
 					bindColumn( columnElement, column, isNullable );
 					final String columnName = columnElement.attributeValue( "name" );
 					String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
 							columnName, propertyPath
 					);
 					column.setName( mappings.getNamingStrategy().columnName(
 						columnName ) );
 					if ( table != null ) {
 						table.addColumn( column ); // table=null -> an association
 						                           // - fill it in later
 						//TODO fill in the mappings for table == null
 						mappings.addColumnBinding( logicalColumnName, column, table );
 					}
 
 
 					simpleValue.addColumn( column );
 					// column index
 					bindIndex( columnElement.attribute( "index" ), table, column, mappings );
 					bindIndex( node.attribute( "index" ), table, column, mappings );
 					//column unique-key
 					bindUniqueKey( columnElement.attribute( "unique-key" ), table, column, mappings );
 					bindUniqueKey( node.attribute( "unique-key" ), table, column, mappings );
 				}
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
 			final String columnName = columnAttribute.getValue();
 			String logicalColumnName = mappings.getNamingStrategy().logicalColumnName(
 					columnName, propertyPath
 			);
 			column.setName( mappings.getNamingStrategy().columnName( columnName ) );
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
 			column.setName( mappings.getNamingStrategy().propertyToColumnName( propertyPath ) );
 			String logicalName = mappings.getNamingStrategy().logicalColumnName( null, propertyPath );
 			mappings.addColumnBinding( logicalName, column, table );
 			/* TODO: joinKeyColumnName & foreignKeyColumnName should be called either here or at a
 			 * slightly higer level in the stack (to get all the information we need)
-			 * Right now HbmSourceProcessorImpl does not support the
+			 * Right now HbmMetadataSourceProcessorImpl does not support the
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
 		if ( typeNode == null ) typeNode = node.attribute( "id-type" ); // for an any
 		if ( typeNode != null ) typeName = typeNode.getValue();
 
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
 
 		TypeDef typeDef = mappings.getTypeDef( typeName );
 		if ( typeDef != null ) {
 			typeName = typeDef.getTypeClass();
 			// parameters on the property mapping should
 			// override parameters in the typedef
 			Properties allParameters = new Properties();
 			allParameters.putAll( typeDef.getParameters() );
 			allParameters.putAll( parameters );
 			parameters = allParameters;
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
         PropertyGeneration generation = PropertyGeneration.parse( generationName );
 		property.setGeneration( generation );
 
         if ( generation == PropertyGeneration.ALWAYS || generation == PropertyGeneration.INSERT ) {
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
 							"cannot specify both insert=\"true\" and generated=\"" + generation.getName() +
 							"\" for property: " +
 							propName
 					);
 		        }
 	        }
 
 	        // properties generated on update can never be updateable...
 	        if ( property.isUpdateable() && generation == PropertyGeneration.ALWAYS ) {
 		        if ( updateNode == null ) {
 			        // updateable only because the user did not specify
 			        // anything; just override it
 			        property.setUpdateable( false );
 		        }
 		        else {
 			        // the user specifically supplied update="true",
 			        // which constitutes an illegal combo
 					throw new MappingException(
 							"cannot specify both update=\"true\" and generated=\"" + generation.getName() +
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
 
         if (LOG.isDebugEnabled()) {
 			String msg = "Mapped property: " + property.getName();
 			String columns = columns( property.getValue() );
 			if ( columns.length() > 0 ) msg += " -> " + columns;
 			// TODO: this fails if we run with debug on!
 			// if ( model.getType()!=null ) msg += ", type: " + model.getType().getName();
             LOG.debugf(msg);
 		}
 
 		property.setMetaAttributes( getMetas( node, inheritedMetas ) );
 
 	}
 
 	private static String columns(Value val) {
 		StringBuffer columns = new StringBuffer();
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
 
             LOG.debugf(
 					"Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName()
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
 			fetchable.setLazy(true);
 			//TODO: better to degrade to lazy="false" if uninstrumented
 		}
 		else {
 			initLaziness(node, fetchable, mappings, "proxy", defaultLazy);
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
 
 		manyToOne.setReferencedEntityName( getEntityName( node, mappings ) );
 
 		String embed = node.attributeValue( "embed-xml" );
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
 
 		oneToOne.setEmbedded( "true".equals( node.attributeValue( "embed-xml" ) ) );
 
 		Attribute fkNode = node.attribute( "foreign-key" );
 		if ( fkNode != null ) oneToOne.setForeignKeyName( fkNode.getValue() );
 
 		Attribute ukName = node.attribute( "property-ref" );
 		if ( ukName != null ) oneToOne.setReferencedPropertyName( ukName.getValue() );
 
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
 			property.setName("_identifierMapper");
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
 				// use old (HB 2.1) defaults if outer-join is specified
 				String eoj = jfNode.getValue();
 				if ( "auto".equals( eoj ) ) {
 					fetchStyle = FetchMode.DEFAULT;
 				}
 				else {
 					boolean join = "true".equals( eoj );
 					fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
 				}
 			}
 		}
 		else {
 			boolean join = "join".equals( fetchNode.getValue() );
 			//lazy = !join;
 			fetchStyle = join ? FetchMode.JOIN : FetchMode.SELECT;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/ToOneFkSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/ToOneFkSecondPass.java
index 7481c79e78..e8677e5466 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/ToOneFkSecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/ToOneFkSecondPass.java
@@ -1,129 +1,129 @@
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
 
 import java.util.Iterator;
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.annotations.TableBinder;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.OneToOne;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.ToOne;
 
 /**
  * Enable a proper set of the FK columns in respect with the id column order
  * Allow the correct implementation of the default EJB3 values which needs both
  * sides of the association to be resolved
  *
  * @author Emmanuel Bernard
  */
 public class ToOneFkSecondPass extends FkSecondPass {
 	private boolean unique;
 	private Mappings mappings;
 	private String path;
 	private String entityClassName;
 
 	public ToOneFkSecondPass(
 			ToOne value,
 			Ejb3JoinColumn[] columns,
 			boolean unique,
 			String entityClassName,
 			String path,
 			Mappings mappings) {
 		super( value, columns );
 		this.mappings = mappings;
 		this.unique = unique;
 		this.entityClassName = entityClassName;
 		this.path = entityClassName != null ? path.substring( entityClassName.length() + 1 ) : path;
 	}
 
 	@Override
     public String getReferencedEntityName() {
 		return ( (ToOne) value ).getReferencedEntityName();
 	}
 
 	@Override
     public boolean isInPrimaryKey() {
 		if ( entityClassName == null ) return false;
 		final PersistentClass persistentClass = mappings.getClass( entityClassName );
 		Property property = persistentClass.getIdentifierProperty();
 		if ( path == null ) {
 			return false;
 		}
 		else if ( property != null) {
 			//try explicit identifier property
 			return path.startsWith( property.getName() + "." );
 		}
 		else {
 			//try the embedded property
 			//embedded property starts their path with 'id.' See PropertyPreloadedData( ) use when idClass != null in AnnotationSourceProcessor
 			if ( path.startsWith( "id." ) ) {
 				KeyValue valueIdentifier = persistentClass.getIdentifier();
 				String localPath = path.substring( 3 );
 				if ( valueIdentifier instanceof Component ) {
 					Iterator it = ( (Component) valueIdentifier ).getPropertyIterator();
 					while ( it.hasNext() ) {
 						Property idProperty = (Property) it.next();
 						if ( localPath.startsWith( idProperty.getName() ) ) return true;
 					}
 
 				}
 			}
 		}
 		return false;
 	}
 
 	public void doSecondPass(java.util.Map persistentClasses) throws MappingException {
 		if ( value instanceof ManyToOne ) {
 			ManyToOne manyToOne = (ManyToOne) value;
 			PersistentClass ref = (PersistentClass) persistentClasses.get( manyToOne.getReferencedEntityName() );
 			if ( ref == null ) {
 				throw new AnnotationException(
 						"@OneToOne or @ManyToOne on "
 								+ StringHelper.qualify( entityClassName, path )
 								+ " references an unknown entity: "
 								+ manyToOne.getReferencedEntityName()
 				);
 			}
 			BinderHelper.createSyntheticPropertyReference( columns, ref, null, manyToOne, false, mappings );
 			TableBinder.bindFk( ref, null, columns, manyToOne, unique, mappings );
 			/*
-			 * HbmSourceProcessorImpl does this only when property-ref != null, but IMO, it makes sense event if it is null
+			 * HbmMetadataSourceProcessorImpl does this only when property-ref != null, but IMO, it makes sense event if it is null
 			 */
 			if ( !manyToOne.isIgnoreNotFound() ) manyToOne.createPropertyRefConstraints( persistentClasses );
 		}
 		else if ( value instanceof OneToOne ) {
 			( (OneToOne) value ).createForeignKey();
 		}
 		else {
 			throw new AssertionFailure( "FkSecondPass for a wrong value type: " + value.getClass().getName() );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
index af78201e1a..74ed5bfb4d 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/PropertyBinder.java
@@ -1,340 +1,340 @@
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
 import org.hibernate.AnnotationException;
 import org.hibernate.internal.CoreMessageLogger;
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
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Value;
 
 import org.jboss.logging.Logger;
 
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
-        LOG.debugf("SourceProcessor property %s with lazy=%s", name, lazy);
+        LOG.debugf("MetadataSourceProcessor property %s with lazy=%s", name, lazy);
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
         LOG.debugf("Building property %s", name);
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
 		NaturalId naturalId = property != null ?
 				property.getAnnotation( NaturalId.class ) :
 				null;
 		if ( naturalId != null ) {
 			if ( !naturalId.mutable() ) {
 				updatable = false;
 			}
 			prop.setNaturalIdentifier( true );
 		}
 		prop.setInsertable( insertable );
 		prop.setUpdateable( updatable );
 		OptimisticLock lockAnn = property != null ?
 				property.getAnnotation( OptimisticLock.class ) :
 				null;
 		if ( lockAnn != null ) {
 			prop.setOptimisticLocked( !lockAnn.excluded() );
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
         LOG.trace("Cascading " + name + " with " + cascade);
 		this.mappingProperty = prop;
 		return prop;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java b/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
index 4584e228e3..34a05c823e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/Metadata.java
@@ -1,94 +1,94 @@
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
 
 package org.hibernate.metamodel;
 
 import java.util.Map;
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 
 /**
  * @author Steve Ebersole
  */
 public interface Metadata {
 	/**
 	 * Exposes the options used to produce a {@link Metadata} instance.
 	 */
 	public static interface Options {
-		public SourceProcessingOrder getSourceProcessingOrder();
+		public MetadataSourceProcessingOrder getMetadataSourceProcessingOrder();
 		public NamingStrategy getNamingStrategy();
 		public SharedCacheMode getSharedCacheMode();
 		public AccessType getDefaultAccessType();
 		public boolean useNewIdentifierGenerators();
         public boolean isGloballyQuotedIdentifiers();
 		public String getDefaultSchemaName();
 		public String getDefaultCatalogName();
 	}
 
 	public Options getOptions();
 
 	public SessionFactoryBuilder getSessionFactoryBuilder();
 
 	public SessionFactory buildSessionFactory();
 
 	public Iterable<EntityBinding> getEntityBindings();
 
 	public EntityBinding getEntityBinding(String entityName);
 
 	/**
 	 * Get the "root" entity binding
 	 * @param entityName
 	 * @return the "root entity binding; simply returns entityBinding if it is the root entity binding
 	 */
 	public EntityBinding getRootEntityBinding(String entityName);
 
 	public Iterable<PluralAttributeBinding> getCollectionBindings();
 
 	public TypeDef getTypeDefinition(String name);
 
 	public Iterable<TypeDef> getTypeDefinitions();
 
 	public Iterable<FilterDefinition> getFilterDefinitions();
 
 	public Iterable<NamedQueryDefinition> getNamedQueryDefinitions();
 
 	public Iterable<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions();
 
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions();
 
 	public Iterable<Map.Entry<String, String>> getImports();
 
 	public IdGenerator getIdGenerator(String name);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataBuilder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataBuilder.java
index 0746b152c2..d8d9351afc 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataBuilder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataBuilder.java
@@ -1,47 +1,47 @@
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
 package org.hibernate.metamodel;
 
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 
 /**
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public interface MetadataBuilder {
 	public MetadataBuilder with(NamingStrategy namingStrategy);
 
-	public MetadataBuilder with(SourceProcessingOrder sourceProcessingOrder);
+	public MetadataBuilder with(MetadataSourceProcessingOrder metadataSourceProcessingOrder);
 
 	public MetadataBuilder with(SharedCacheMode cacheMode);
 
 	public MetadataBuilder with(AccessType accessType);
 
 	public MetadataBuilder withNewIdentifierGeneratorsEnabled(boolean enabled);
 
 	public Metadata buildMetadata();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/SourceProcessingOrder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSourceProcessingOrder.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/SourceProcessingOrder.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSourceProcessingOrder.java
index 819ef1ee7d..5a099c6f05 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/SourceProcessingOrder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSourceProcessingOrder.java
@@ -1,35 +1,35 @@
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
 package org.hibernate.metamodel;
 
 /**
  * Enumeration of the possible orders for processing metadata sources.  The implication is in terms of precedence;
  * for duplicate information in different sources, whichever is processed first has precedence.
  *
  * @author Steve Ebersole
  */
-public enum SourceProcessingOrder {
+public enum MetadataSourceProcessingOrder {
 	ANNOTATIONS_FIRST,
 	HBM_FIRST
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/SourceProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataSourceProcessor.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/SourceProcessor.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataSourceProcessor.java
index 6f5c17f909..985d28aac0 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/SourceProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/MetadataSourceProcessor.java
@@ -1,81 +1,81 @@
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
 package org.hibernate.metamodel.source;
 
 import java.util.List;
 
 import org.hibernate.metamodel.MetadataSources;
 
 /**
  * Handles the processing of metadata sources in a dependency-ordered manner.
  *
  * @author Steve Ebersole
  */
-public interface SourceProcessor {
+public interface MetadataSourceProcessor {
 	/**
 	 * Prepare for processing the given sources.
 	 *
 	 * @param sources The metadata sources.
 	 */
 	public void prepare(MetadataSources sources);
 
 	/**
 	 * Process the independent metadata.  These have no dependency on other types of metadata being processed.
 	 *
 	 * @param sources The metadata sources.
 	 *
 	 * @see #prepare
 	 */
 	public void processIndependentMetadata(MetadataSources sources);
 
 	/**
 	 * Process the parts of the metadata that depend on type information (type definitions) having been processed
 	 * and available.
 	 *
 	 * @param sources The metadata sources.
 	 *
 	 * @see #processIndependentMetadata
 	 */
 	public void processTypeDependentMetadata(MetadataSources sources);
 
 	/**
 	 * Process the mapping (entities, et al) metadata.
 	 *
 	 * @param sources The metadata sources.
 	 * @param processedEntityNames Collection of any already processed entity names.
 	 *
 	 * @see #processTypeDependentMetadata
 	 */
 	public void processMappingMetadata(MetadataSources sources, List<String> processedEntityNames);
 
 	/**
 	 * Process the parts of the metadata that depend on mapping (entities, et al) information having been
 	 * processed and available.
 	 *
 	 * @param sources The metadata sources.
 	 *
 	 * @see #processMappingMetadata
 	 */
 	public void processMappingDependentMetadata(MetadataSources sources);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationMetadataSourceProcessorImpl.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationProcessor.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationMetadataSourceProcessorImpl.java
index 23ce3cc73f..20b0ed5cfa 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationMetadataSourceProcessorImpl.java
@@ -1,205 +1,205 @@
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
 package org.hibernate.metamodel.source.annotations;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Set;
 
 import org.jboss.jandex.Index;
 import org.jboss.jandex.Indexer;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.domain.NonEntity;
 import org.hibernate.metamodel.domain.Superclass;
 import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.metamodel.source.SourceProcessor;
+import org.hibernate.metamodel.source.MetadataSourceProcessor;
 import org.hibernate.metamodel.source.annotation.jaxb.XMLEntityMappings;
 import org.hibernate.metamodel.source.annotations.entity.ConfiguredClassHierarchy;
 import org.hibernate.metamodel.source.annotations.entity.ConfiguredClassType;
 import org.hibernate.metamodel.source.annotations.entity.EntityBinder;
 import org.hibernate.metamodel.source.annotations.entity.EntityClass;
 import org.hibernate.metamodel.source.annotations.global.FetchProfileBinder;
 import org.hibernate.metamodel.source.annotations.global.FilterDefBinder;
 import org.hibernate.metamodel.source.annotations.global.IdGeneratorBinder;
 import org.hibernate.metamodel.source.annotations.global.QueryBinder;
 import org.hibernate.metamodel.source.annotations.global.TableBinder;
 import org.hibernate.metamodel.source.annotations.global.TypeDefBinder;
 import org.hibernate.metamodel.source.annotations.xml.PseudoJpaDotNames;
 import org.hibernate.metamodel.source.annotations.xml.mocker.EntityMappingsMocker;
 import org.hibernate.metamodel.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * Main class responsible to creating and binding the Hibernate meta-model from annotations.
  * This binder only has to deal with the (jandex) annotation index/repository. XML configuration is already processed
  * and pseudo annotations are created.
  *
  * @author Hardy Ferentschik
  * @author Steve Ebersole
  */
-public class AnnotationProcessor implements SourceProcessor {
-	private static final Logger LOG = Logger.getLogger( AnnotationProcessor.class );
+public class AnnotationMetadataSourceProcessorImpl implements MetadataSourceProcessor {
+	private static final Logger LOG = Logger.getLogger( AnnotationMetadataSourceProcessorImpl.class );
 
 	private final MetadataImplementor metadata;
 	private AnnotationBindingContext bindingContext;
 
-	public AnnotationProcessor(MetadataImpl metadata) {
+	public AnnotationMetadataSourceProcessorImpl(MetadataImpl metadata) {
 		this.metadata = metadata;
 	}
 
 	@Override
 	@SuppressWarnings( { "unchecked" })
 	public void prepare(MetadataSources sources) {
 		// create a jandex index from the annotated classes
 		Indexer indexer = new Indexer();
 		for ( Class<?> clazz : sources.getAnnotatedClasses() ) {
 			indexClass( indexer, clazz.getName().replace( '.', '/' ) + ".class" );
 		}
 
 		// add package-info from the configured packages
 		for ( String packageName : sources.getAnnotatedPackages() ) {
 			indexClass( indexer, packageName.replace( '.', '/' ) + "/package-info.class" );
 		}
 
 		Index index = indexer.complete();
 
 		List<JaxbRoot<XMLEntityMappings>> mappings = new ArrayList<JaxbRoot<XMLEntityMappings>>();
 		for ( JaxbRoot<?> root : sources.getJaxbRootList() ) {
 			if ( root.getRoot() instanceof XMLEntityMappings ) {
 				mappings.add( (JaxbRoot<XMLEntityMappings>) root );
 			}
 		}
 		if ( !mappings.isEmpty() ) {
 			index = parseAndUpdateIndex( mappings, index );
 		}
 
 		if ( index.getAnnotations( PseudoJpaDotNames.DEFAULT_DELIMITED_IDENTIFIERS ) != null ) {
 			// todo : this needs to move to AnnotationBindingContext
 			// what happens right now is that specifying this in an orm.xml causes it to effect all orm.xmls
 			metadata.setGloballyQuotedIdentifiers( true );
 		}
 		bindingContext = new AnnotationBindingContextImpl( metadata, index );
 	}
 
 	@Override
 	public void processIndependentMetadata(MetadataSources sources) {
 		assertBindingContextExists();
 		TypeDefBinder.bind( bindingContext );
 	}
 
 	private void assertBindingContextExists() {
 		if ( bindingContext == null ) {
 			throw new AssertionFailure( "The binding context should exist. Has prepare been called!?" );
 		}
 	}
 
 	@Override
 	public void processTypeDependentMetadata(MetadataSources sources) {
 		assertBindingContextExists();
 		IdGeneratorBinder.bind( bindingContext );
 	}
 
 	@Override
 	public void processMappingMetadata(MetadataSources sources, List<String> processedEntityNames) {
 		assertBindingContextExists();
 		// need to order our annotated entities into an order we can process
 		Set<ConfiguredClassHierarchy<EntityClass>> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				bindingContext
 		);
 
 		// now we process each hierarchy one at the time
 		Hierarchical parent = null;
 		for ( ConfiguredClassHierarchy<EntityClass> hierarchy : hierarchies ) {
 			for ( EntityClass entityClass : hierarchy ) {
 				// for classes annotated w/ @Entity we create a EntityBinding
 				if ( ConfiguredClassType.ENTITY.equals( entityClass.getConfiguredClassType() ) ) {
 					LOG.debugf( "Binding entity from annotated class: %s", entityClass.getName() );
 					EntityBinder entityBinder = new EntityBinder( entityClass, parent, bindingContext );
 					EntityBinding binding = entityBinder.bind( processedEntityNames );
 					parent = binding.getEntity();
 				}
 				// for classes annotated w/ @MappedSuperclass we just create the domain instance
 				// the attribute bindings will be part of the first entity subclass
 				else if ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( entityClass.getConfiguredClassType() ) ) {
 					parent = new Superclass(
 							entityClass.getName(),
 							entityClass.getName(),
 							bindingContext.makeClassReference( entityClass.getName() ),
 							parent
 					);
 				}
 				// for classes which are not annotated at all we create the NonEntity domain class
 				// todo - not sure whether this is needed. It might be that we don't need this information (HF)
 				else {
 					parent = new NonEntity(
 							entityClass.getName(),
 							entityClass.getName(),
 							bindingContext.makeClassReference( entityClass.getName() ),
 							parent
 					);
 				}
 			}
 		}
 	}
 
 	@Override
 	public void processMappingDependentMetadata(MetadataSources sources) {
 		TableBinder.bind( bindingContext );
 		FetchProfileBinder.bind( bindingContext );
 		QueryBinder.bind( bindingContext );
 		FilterDefBinder.bind( bindingContext );
 	}
 
 	private Index parseAndUpdateIndex(List<JaxbRoot<XMLEntityMappings>> mappings, Index annotationIndex) {
 		List<XMLEntityMappings> list = new ArrayList<XMLEntityMappings>( mappings.size() );
 		for ( JaxbRoot<XMLEntityMappings> jaxbRoot : mappings ) {
 			list.add( jaxbRoot.getRoot() );
 		}
 		return new EntityMappingsMocker( list, annotationIndex, metadata.getServiceRegistry() ).mockNewIndex();
 	}
 
 	private void indexClass(Indexer indexer, String className) {
 		InputStream stream = metadata.getServiceRegistry().getService( ClassLoaderService.class ).locateResourceStream(
 				className
 		);
 		try {
 			indexer.index( stream );
 		}
 		catch ( IOException e ) {
 			throw new HibernateException( "Unable to open input stream for class " + className, e );
 		}
 	}
 
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSource.java
index b2e1dac57c..02ca5bbb20 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSource.java
@@ -1,40 +1,52 @@
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
 package org.hibernate.metamodel.source.binder;
 
 /**
+ * Contract for sources of persistent attribute descriptions.
+ *
  * @author Steve Ebersole
  */
 public interface AttributeSource {
 	/**
 	 * Obtain the attribute name.
 	 *
 	 * @return The attribute name.  {@code nulls} are NOT allowed!
 	 */
 	public String getName();
 
+	/**
+	 * Is this a singular attribute?  Specifically, can it be cast to {@link SingularAttributeSource}?
+	 *
+	 * @return {@code true} indicates this is castable to {@link SingularAttributeSource}; {@code false} otherwise.
+	 */
 	public boolean isSingular();
 
+	/**
+	 * Obtain the meta-attribute sources associated with this attribute.
+	 *
+	 * @return The meta-attribute sources.
+	 */
 	public Iterable<MetaAttributeSource> metaAttributes();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSourceContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSourceContainer.java
index 7bf16603f6..5371c4d29c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSourceContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/AttributeSourceContainer.java
@@ -1,31 +1,39 @@
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
 package org.hibernate.metamodel.source.binder;
 
 /**
+ * Contract for a container of {@link AttributeSource} references.  Both entities and components contain
+ * attributes.
+ *
  * @author Steve Ebersole
  */
 public interface AttributeSourceContainer {
+	/**
+	 Obtain this container's attribute sources.
+	 *
+	 * @return TYhe attribute sources.
+	 */
 	public Iterable<AttributeSource> attributeSources();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
index 0d713c05f2..2eacc9769f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/Binder.java
@@ -1,601 +1,610 @@
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
 package org.hibernate.metamodel.source.binder;
 
 import java.beans.BeanInfo;
 import java.beans.PropertyDescriptor;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.beans.BeanInfoHelper;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.MetaAttribute;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.Tuple;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.hbm.Helper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
+ * The common binder shared between annotations and {@code hbm.xml} processing.
+ * <p/>
+ * The API consists of {@link #Binder} and {@link #processEntityHierarchy}
+ *
  * @author Steve Ebersole
  */
 public class Binder {
 	private final MetadataImplementor metadata;
 	private final List<String> processedEntityNames;
 
 	private InheritanceType currentInheritanceType;
 	private EntityMode currentHierarchyEntityMode;
 	private LocalBindingContext currentBindingContext;
 
 	public Binder(MetadataImplementor metadata, List<String> processedEntityNames) {
 		this.metadata = metadata;
 		this.processedEntityNames = processedEntityNames;
 	}
 
+	/**
+	 * Process an entity hierarchy.
+	 *
+	 * @param entityHierarchy THe hierarchy to process.
+	 */
 	public void processEntityHierarchy(EntityHierarchy entityHierarchy) {
 		currentInheritanceType = entityHierarchy.getHierarchyInheritanceType();
 		EntityBinding rootEntityBinding = createEntityBinding( entityHierarchy.getRootEntitySource(), null );
 		if ( currentInheritanceType != InheritanceType.NO_INHERITANCE ) {
 			processHierarchySubEntities( entityHierarchy.getRootEntitySource(), rootEntityBinding );
 		}
 		currentHierarchyEntityMode = null;
 	}
 
 	private void processHierarchySubEntities(SubclassEntityContainer subclassEntitySource, EntityBinding superEntityBinding) {
 		for ( SubclassEntitySource subEntity : subclassEntitySource.subclassEntitySources() ) {
 			EntityBinding entityBinding = createEntityBinding( subEntity, superEntityBinding );
 			processHierarchySubEntities( subEntity, entityBinding );
 		}
 	}
 
 
 	// Entities ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private EntityBinding createEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		if ( processedEntityNames.contains( entitySource.getEntityName() ) ) {
 			return metadata.getEntityBinding( entitySource.getEntityName() );
 		}
 
 		currentBindingContext = entitySource.getBindingContext();
 		try {
 			final EntityBinding entityBinding = doCreateEntityBinding( entitySource, superEntityBinding );
 
 			metadata.addEntity( entityBinding );
 			processedEntityNames.add( entityBinding.getEntity().getName() );
 
 			processFetchProfiles( entitySource, entityBinding );
 
 			return entityBinding;
 		}
 		finally {
 			currentBindingContext = null;
 		}
 	}
 
 	private EntityBinding doCreateEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = createBasicEntityBinding( entitySource, superEntityBinding );
 
 		bindSecondaryTables( entitySource, entityBinding );
 		bindAttributes( entitySource, entityBinding );
 
 		bindTableUniqueConstraints( entitySource, entityBinding );
 
 		return entityBinding;
 	}
 
 	private EntityBinding createBasicEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		if ( superEntityBinding == null ) {
 			return makeRootEntityBinding( (RootEntitySource) entitySource );
 		}
 		else {
 			if ( currentInheritanceType == InheritanceType.SINGLE_TABLE ) {
 				return makeDiscriminatedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else if ( currentInheritanceType == InheritanceType.JOINED ) {
 				return makeJoinedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else if ( currentInheritanceType == InheritanceType.TABLE_PER_CLASS ) {
 				return makeUnionedSubclassBinding( (SubclassEntitySource) entitySource, superEntityBinding );
 			}
 			else {
 				// extreme internal error!
 				throw new AssertionFailure( "Internal condition failure" );
 			}
 		}
 	}
 
 	private EntityBinding makeRootEntityBinding(RootEntitySource entitySource) {
 		currentHierarchyEntityMode = entitySource.getEntityMode();
 
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, null );
 
 		bindPrimaryTable( entitySource, entityBinding );
 
 		bindIdentifier( entitySource, entityBinding );
 		bindVersion( entityBinding, entitySource );
 		bindDiscriminator( entitySource, entityBinding );
 
 		entityBinding.setMutable( entitySource.isMutable() );
 		entityBinding.setExplicitPolymorphism( entitySource.isExplicitPolymorphism() );
 		entityBinding.setWhereFilter( entitySource.getWhere() );
 		entityBinding.setRowId( entitySource.getRowId() );
 		entityBinding.setOptimisticLockStyle( entitySource.getOptimisticLockStyle() );
 		entityBinding.setCaching( entitySource.getCaching() );
 
 		return entityBinding;
 	}
 
 
 	private EntityBinding buildBasicEntityBinding(EntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = new EntityBinding();
 		entityBinding.setSuperEntityBinding( superEntityBinding );
 		entityBinding.setInheritanceType( currentInheritanceType );
 
 		entityBinding.setEntityMode( currentHierarchyEntityMode );
 
 		final String entityName = entitySource.getEntityName();
 		final String className = currentHierarchyEntityMode == EntityMode.POJO ? entitySource.getClassName() : null;
 
 		final Entity entity = new Entity(
 				entityName,
 				className,
 				currentBindingContext.makeClassReference( className ),
 				null
 		);
 		entityBinding.setEntity( entity );
 
 		entityBinding.setJpaEntityName( entitySource.getJpaEntityName() );
 
 		if ( entityBinding.getEntityMode() == EntityMode.POJO ) {
 			final String proxy = entitySource.getProxy();
 			if ( proxy != null ) {
 				entityBinding.setProxyInterfaceType(
 						currentBindingContext.makeClassReference(
 								currentBindingContext.qualifyClassName( proxy )
 						)
 				);
 				entityBinding.setLazy( true );
 			}
 			else if ( entitySource.isLazy() ) {
 				entityBinding.setProxyInterfaceType( entityBinding.getEntity().getClassReferenceUnresolved() );
 				entityBinding.setLazy( true );
 			}
 		}
 		else {
 			entityBinding.setProxyInterfaceType( null );
 			entityBinding.setLazy( entitySource.isLazy() );
 		}
 
 		final String customTuplizerClassName = entitySource.getCustomTuplizerClassName();
 		if ( customTuplizerClassName != null ) {
 			entityBinding.setCustomEntityTuplizerClass( currentBindingContext.<EntityTuplizer>locateClassByName( customTuplizerClassName ) );
 		}
 
 		final String customPersisterClassName = entitySource.getCustomPersisterClassName();
 		if ( customPersisterClassName != null ) {
 			entityBinding.setCustomEntityPersisterClass( currentBindingContext.<EntityPersister>locateClassByName( customPersisterClassName ) );
 		}
 
 		entityBinding.setMetaAttributeContext( buildMetaAttributeContext( entitySource ) );
 
 		entityBinding.setDynamicUpdate( entitySource.isDynamicUpdate() );
 		entityBinding.setDynamicInsert( entitySource.isDynamicInsert() );
 		entityBinding.setBatchSize( entitySource.getBatchSize() );
 		entityBinding.setSelectBeforeUpdate( entitySource.isSelectBeforeUpdate() );
 		entityBinding.setAbstract( entitySource.isAbstract() );
 
 		entityBinding.setCustomLoaderName( entitySource.getCustomLoaderName() );
 		entityBinding.setCustomInsert( entitySource.getCustomSqlInsert() );
 		entityBinding.setCustomUpdate( entitySource.getCustomSqlUpdate() );
 		entityBinding.setCustomDelete( entitySource.getCustomSqlDelete() );
 
 		if ( entitySource.getSynchronizedTableNames() != null ) {
 			entityBinding.addSynchronizedTableNames( entitySource.getSynchronizedTableNames() );
 		}
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeDiscriminatedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
 
 		entityBinding.setBaseTable( superEntityBinding.getBaseTable() );
 
 		bindDiscriminatorValue( entitySource, entityBinding );
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeJoinedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
 
 		bindPrimaryTable( entitySource, entityBinding );
 
 		// todo : join
 
 		return entityBinding;
 	}
 
 	private EntityBinding makeUnionedSubclassBinding(SubclassEntitySource entitySource, EntityBinding superEntityBinding) {
 		final EntityBinding entityBinding = buildBasicEntityBinding( entitySource, superEntityBinding );
 
 		bindPrimaryTable( entitySource, entityBinding );
 
 		// todo : ??
 
 		return entityBinding;
 	}
 
 
 	// Attributes ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private void bindAttributes(AttributeSourceContainer attributeSourceContainer, EntityBinding entityBinding) {
 		// todo : we really need the notion of a Stack here for the table from which the columns come for binding these attributes.
 		// todo : adding the concept (interface) of a source of attribute metadata would allow reuse of this method for entity, component, unique-key, etc
 		// for now, simply assume all columns come from the base table....
 
 		for ( AttributeSource attributeSource : attributeSourceContainer.attributeSources() ) {
 			if ( attributeSource.isSingular() ) {
 				doBasicSingularAttributeBindingCreation( (SingularAttributeSource) attributeSource, entityBinding );
 			}
 			// todo : components and collections
 		}
 	}
 
 	private void bindIdentifier(RootEntitySource entitySource, EntityBinding entityBinding) {
 		if ( entitySource.getIdentifierSource() == null ) {
 			throw new AssertionFailure( "Expecting identifier information on root entity descriptor" );
 		}
 		switch ( entitySource.getIdentifierSource().getNature() ) {
 			case SIMPLE: {
 				bindSimpleIdentifier( (SimpleIdentifierSource) entitySource.getIdentifierSource(), entityBinding );
 			}
 			case AGGREGATED_COMPOSITE: {
 				// composite id with an actual component class
 			}
 			case COMPOSITE: {
 				// what we used to term an "embedded composite identifier", which is not tobe confused with the JPA
 				// term embedded. Specifically a composite id where there is no component class, though there may
 				// be a @IdClass :/
 			}
 		}
 	}
 
 	private void bindSimpleIdentifier(SimpleIdentifierSource identifierSource, EntityBinding entityBinding) {
 		final SimpleAttributeBinding idAttributeBinding = doBasicSingularAttributeBindingCreation(
 				identifierSource.getIdentifierAttributeSource(), entityBinding
 		);
 
 		entityBinding.getEntityIdentifier().setValueBinding( idAttributeBinding );
 		entityBinding.getEntityIdentifier().setIdGenerator( identifierSource.getIdentifierGeneratorDescriptor() );
 
 		final org.hibernate.metamodel.relational.Value relationalValue = idAttributeBinding.getValue();
 
 		if ( SimpleValue.class.isInstance( relationalValue ) ) {
 			if ( !Column.class.isInstance( relationalValue ) ) {
 				// this should never ever happen..
 				throw new AssertionFailure( "Simple-id was not a column." );
 			}
 			entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( relationalValue ) );
 		}
 		else {
 			for ( SimpleValue subValue : ( (Tuple) relationalValue ).values() ) {
 				if ( Column.class.isInstance( subValue ) ) {
 					entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( subValue ) );
 				}
 			}
 		}
 	}
 
 	private void bindVersion(EntityBinding entityBinding, RootEntitySource entitySource) {
 		final SingularAttributeSource versioningAttributeSource = entitySource.getVersioningAttributeSource();
 		if ( versioningAttributeSource == null ) {
 			return;
 		}
 
 		SimpleAttributeBinding attributeBinding = doBasicSingularAttributeBindingCreation(
 				versioningAttributeSource, entityBinding
 		);
 		entityBinding.setVersionBinding( attributeBinding );
 	}
 
 	private void bindDiscriminator(RootEntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
 	private void bindDiscriminatorValue(SubclassEntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
 	private SimpleAttributeBinding doBasicSingularAttributeBindingCreation(
 			SingularAttributeSource attributeSource,
 			EntityBinding entityBinding) {
 		final SingularAttribute attribute = attributeSource.isVirtualAttribute()
 				? entityBinding.getEntity().locateOrCreateVirtualAttribute( attributeSource.getName() )
 				: entityBinding.getEntity().locateOrCreateSingularAttribute( attributeSource.getName() );
 
 		final SimpleAttributeBinding attributeBinding;
 		if ( attributeSource.getNature() == SingularAttributeNature.BASIC ) {
 			attributeBinding = entityBinding.makeSimpleAttributeBinding( attribute );
 			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
 		}
 		else if ( attributeSource.getNature() == SingularAttributeNature.MANY_TO_ONE ) {
 			attributeBinding = entityBinding.makeManyToOneAttributeBinding( attribute );
 			resolveTypeInformation( attributeSource.getTypeInformation(), attributeBinding );
 			resolveToOneReferenceInformation( (ToOneAttributeSource) attributeSource, (ManyToOneAttributeBinding) attributeBinding );
 		}
 		else {
 			throw new NotYetImplementedException();
 		}
 
 		attributeBinding.setInsertable( attributeSource.isInsertable() );
 		attributeBinding.setUpdatable( attributeSource.isUpdatable() );
 		attributeBinding.setGeneration( attributeSource.getGeneration() );
 		attributeBinding.setLazy( attributeSource.isLazy() );
 		attributeBinding.setIncludedInOptimisticLocking( attributeSource.isIncludedInOptimisticLocking() );
 
 		attributeBinding.setPropertyAccessorName(
 				Helper.getPropertyAccessorName(
 						attributeSource.getPropertyAccessorName(),
 						false,
 						currentBindingContext.getMappingDefaults().getPropertyAccessorName()
 				)
 		);
 
 		final org.hibernate.metamodel.relational.Value relationalValue = makeValue( attributeSource, attributeBinding );
 		attributeBinding.setValue( relationalValue );
 
 		attributeBinding.setMetaAttributeContext(
 				buildMetaAttributeContext( attributeSource.metaAttributes(), entityBinding.getMetaAttributeContext() )
 		);
 
 		return attributeBinding;
 	}
 
 	private void resolveTypeInformation(ExplicitHibernateTypeSource typeSource, SimpleAttributeBinding attributeBinding) {
 		final Class<?> attributeJavaType = determineJavaType( attributeBinding.getAttribute() );
 		if ( attributeJavaType != null ) {
 			attributeBinding.getHibernateTypeDescriptor().setJavaTypeName( attributeJavaType.getName() );
 			attributeBinding.getAttribute().resolveType( currentBindingContext.makeJavaType( attributeJavaType.getName() ) );
 		}
 
 		final String explicitTypeName = typeSource.getName();
 		if ( explicitTypeName != null ) {
 			final TypeDef typeDef = currentBindingContext.getMetadataImplementor().getTypeDefinition( explicitTypeName );
 			if ( typeDef != null ) {
 				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( typeDef.getTypeClass() );
 				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().putAll( typeDef.getParameters() );
 			}
 			else {
 				attributeBinding.getHibernateTypeDescriptor().setExplicitTypeName( explicitTypeName );
 			}
 			final Map<String,String> parameters = typeSource.getParameters();
 			if ( parameters != null ) {
 				attributeBinding.getHibernateTypeDescriptor().getTypeParameters().putAll( parameters );
 			}
 		}
 		else {
 			if ( attributeJavaType == null ) {
 				// we will have problems later determining the Hibernate Type to use.  Should we throw an
 				// exception now?  Might be better to get better contextual info
 			}
 		}
 	}
 
 	private Class<?> determineJavaType(final Attribute attribute) {
 		try {
 			final Class ownerClass = attribute.getAttributeContainer().getClassReference();
 			AttributeJavaTypeDeterminerDelegate delegate = new AttributeJavaTypeDeterminerDelegate( attribute.getName() );
 			BeanInfoHelper.visitBeanInfo( ownerClass, delegate );
 			return delegate.javaType;
 		}
 		catch ( Exception ignore ) {
 			// todo : log it?
 		}
 		return null;
 	}
 
 	private static class AttributeJavaTypeDeterminerDelegate implements BeanInfoHelper.BeanInfoDelegate {
 		private final String attributeName;
 		private Class<?> javaType = null;
 
 		private AttributeJavaTypeDeterminerDelegate(String attributeName) {
 			this.attributeName = attributeName;
 		}
 
 		@Override
 		public void processBeanInfo(BeanInfo beanInfo) throws Exception {
 			for ( PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors() ) {
 				if ( propertyDescriptor.getName().equals( attributeName ) ) {
 					javaType = propertyDescriptor.getPropertyType();
 					break;
 				}
 			}
 		}
 	}
 
 	private void resolveToOneReferenceInformation(ToOneAttributeSource attributeSource, ManyToOneAttributeBinding attributeBinding) {
 		final String referencedEntityName = attributeSource.getReferencedEntityName() != null
 				? attributeSource.getReferencedEntityName()
 				: attributeBinding.getAttribute().getSingularAttributeType().getClassName();
 		attributeBinding.setReferencedEntityName( referencedEntityName );
 		// todo : we should consider basing references on columns instead of property-ref, which would require a resolution (later) of property-ref to column names
 		attributeBinding.setReferencedAttributeName( attributeSource.getReferencedEntityAttributeName() );
 	}
 
 	private MetaAttributeContext buildMetaAttributeContext(EntitySource entitySource) {
 		return buildMetaAttributeContext(
 				entitySource.metaAttributes(),
 				true,
 				currentBindingContext.getMetadataImplementor().getGlobalMetaAttributeContext()
 		);
 	}
 
 	private static MetaAttributeContext buildMetaAttributeContext(
 			Iterable<MetaAttributeSource> metaAttributeSources,
 			MetaAttributeContext parentContext) {
 		return buildMetaAttributeContext( metaAttributeSources, false, parentContext );
 	}
 
 	private static MetaAttributeContext buildMetaAttributeContext(
 			Iterable<MetaAttributeSource> metaAttributeSources,
 			boolean onlyInheritable,
 			MetaAttributeContext parentContext) {
 		final MetaAttributeContext subContext = new MetaAttributeContext( parentContext );
 
 		for ( MetaAttributeSource metaAttributeSource : metaAttributeSources ) {
 			if ( onlyInheritable & !metaAttributeSource.isInheritable() ) {
 				continue;
 			}
 
 			final String name = metaAttributeSource.getName();
 			final MetaAttribute inheritedMetaAttribute = parentContext.getMetaAttribute( name );
 			MetaAttribute metaAttribute = subContext.getLocalMetaAttribute( name );
 			if ( metaAttribute == null || metaAttribute == inheritedMetaAttribute ) {
 				metaAttribute = new MetaAttribute( name );
 				subContext.add( metaAttribute );
 			}
 			metaAttribute.addValue( metaAttributeSource.getValue() );
 		}
 
 		return subContext;
 	}
 
 
 	// Relational ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private void bindPrimaryTable(EntitySource entitySource, EntityBinding entityBinding) {
 		final TableSource tableSource = entitySource.getPrimaryTable();
 		final String schemaName = StringHelper.isEmpty( tableSource.getExplicitSchemaName() )
 				? currentBindingContext.getMappingDefaults().getSchemaName()
 				: currentBindingContext.getMetadataImplementor().getOptions().isGloballyQuotedIdentifiers()
 						? StringHelper.quote( tableSource.getExplicitSchemaName() )
 						: tableSource.getExplicitSchemaName();
 		final String catalogName = StringHelper.isEmpty( tableSource.getExplicitCatalogName() )
 				? currentBindingContext.getMappingDefaults().getCatalogName()
 				: currentBindingContext.getMetadataImplementor().getOptions().isGloballyQuotedIdentifiers()
 						? StringHelper.quote( tableSource.getExplicitCatalogName() )
 						: tableSource.getExplicitCatalogName();
 
 		String tableName = tableSource.getExplicitTableName();
 		if ( StringHelper.isEmpty( tableName ) ) {
 			tableName = currentBindingContext.getNamingStrategy()
 					.classToTableName( entityBinding.getEntity().getClassName() );
 		}
 		else {
 			tableName = currentBindingContext.getNamingStrategy().tableName( tableName );
 		}
 		if ( currentBindingContext.isGloballyQuotedIdentifiers() ) {
 			tableName = StringHelper.quote( tableName );
 		}
 
 		final org.hibernate.metamodel.relational.Table table = currentBindingContext.getMetadataImplementor()
 				.getDatabase()
 				.getSchema( new Schema.Name( schemaName, catalogName ) )
 				.locateOrCreateTable( Identifier.toIdentifier( tableName ) );
 
 		entityBinding.setBaseTable( table );
 	}
 
 	private void bindSecondaryTables(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
 	private void bindTableUniqueConstraints(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : implement
 	}
 
 	private org.hibernate.metamodel.relational.Value makeValue(
-			RelationValueMetadataSource relationValueMetadataSource,
+			RelationalValueSourceContainer relationalValueSourceContainer,
 			SimpleAttributeBinding attributeBinding) {
 
 		// todo : to be completely correct, we need to know which table the value belongs to.
 		// 		There is a note about this somewhere else with ideas on the subject.
 		//		For now, just use the entity's base table.
 		final TableSpecification table = attributeBinding.getEntityBinding().getBaseTable();
 
-		if ( relationValueMetadataSource.relationalValueSources().size() > 0 ) {
+		if ( relationalValueSourceContainer.relationalValueSources().size() > 0 ) {
 			List<SimpleValue> values = new ArrayList<SimpleValue>();
-			for ( RelationalValueSource valueSource : relationValueMetadataSource.relationalValueSources() ) {
+			for ( RelationalValueSource valueSource : relationalValueSourceContainer.relationalValueSources() ) {
 				if ( ColumnSource.class.isInstance( valueSource ) ) {
 					final ColumnSource columnSource = ColumnSource.class.cast( valueSource );
 					final Column column = table.locateOrCreateColumn( columnSource.getName() );
 					column.setNullable( columnSource.isNullable() );
 					column.setDefaultValue( columnSource.getDefaultValue() );
 					column.setSqlType( columnSource.getSqlType() );
 					column.setSize( columnSource.getSize() );
 					column.setDatatype( columnSource.getDatatype() );
 					column.setReadFragment( columnSource.getReadFragment() );
 					column.setWriteFragment( columnSource.getWriteFragment() );
 					column.setUnique( columnSource.isUnique() );
 					column.setCheckCondition( columnSource.getCheckCondition() );
 					column.setComment( columnSource.getComment() );
 					values.add( column );
 				}
 				else {
 					values.add( table.locateOrCreateDerivedValue( ( (DerivedValueSource) valueSource ).getExpression() ) );
 				}
 			}
 			if ( values.size() == 1 ) {
 				return values.get( 0 );
 			}
 			Tuple tuple = new Tuple( table, null );
 			for ( SimpleValue value : values ) {
 				tuple.addValue( value );
 			}
 			return tuple;
 		}
 		else {
 			// assume a column named based on the NamingStrategy
 			final String name = metadata.getOptions()
 					.getNamingStrategy()
 					.propertyToColumnName( attributeBinding.getAttribute().getName() );
 			return table.locateOrCreateColumn( name );
 		}
 	}
 
 	private void processFetchProfiles(EntitySource entitySource, EntityBinding entityBinding) {
 		// todo : process the entity-local fetch-profile declaration
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ColumnSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ColumnSource.java
index ef0e34dbf6..b269128e84 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ColumnSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ColumnSource.java
@@ -1,55 +1,112 @@
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
 package org.hibernate.metamodel.source.binder;
 
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.Size;
 
 /**
+ * Contract for source information pertaining to a column definition.
+ *
  * @author Steve Ebersole
  */
 public interface ColumnSource extends RelationalValueSource {
+	/**
+	 * Obtain the name of the column.
+	 *
+	 * @return The name of the column.  Can be {@code null}, in which case a naming strategy is applied.
+	 */
 	public String getName();
 
+	/**
+	 * A SQL fragment to apply to the column value on read.
+	 *
+	 * @return The SQL read fragment
+	 */
+	public String getReadFragment();
+
+	/**
+	 * A SQL fragment to apply to the column value on write.
+	 *
+	 * @return The SQL write fragment
+	 */
+	public String getWriteFragment();
+
+	/**
+	 * Is this column nullable?
+	 *
+	 * @return {@code true} indicates it is nullable; {@code false} non-nullable.
+	 */
 	public boolean isNullable();
 
+	/**
+	 * Obtain a specified default value for the column
+	 *
+	 * @return THe column default
+	 */
 	public String getDefaultValue();
 
+	/**
+	 * Obtain the free-hand definition of the column's type.
+	 *
+	 * @return The free-hand column type
+	 */
 	public String getSqlType();
 
+	/**
+	 * The deduced (and dialect convertible) type for this column
+	 *
+	 * @return The column's SQL data type.
+	 */
 	public Datatype getDatatype();
 
+	/**
+	 * Obtain the specified column size.
+	 *
+	 * @return The column size.
+	 */
 	public Size getSize();
 
-	public String getReadFragment();
-
-	public String getWriteFragment();
-
+	/**
+	 * Is this column unique?
+	 *
+	 * @return {@code true} indicates it is unique; {@code false} non-unique.
+	 */
 	public boolean isUnique();
 
+	/**
+	 * Obtain the specified check constraint condition
+	 *
+	 * @return Check constraint condition
+	 */
 	public String getCheckCondition();
 
+	/**
+	 * Obtain the specified SQL comment
+	 *
+	 * @return SQL comment
+	 */
 	public String getComment();
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/DerivedValueSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/DerivedValueSource.java
index 6e3662f314..85b2738c49 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/DerivedValueSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/DerivedValueSource.java
@@ -1,31 +1,38 @@
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
 package org.hibernate.metamodel.source.binder;
 
 /**
+ * Contract describing source of a derived value (formula).
+ *
  * @author Steve Ebersole
  */
 public interface DerivedValueSource extends RelationalValueSource {
+	/**
+	 * Obtain the expression used to derive the value.
+	 *
+	 * @return The derived value expression.
+	 */
 	public String getExpression();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntityHierarchy.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntityHierarchy.java
index 2f363491d1..fdfdf9ec4f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntityHierarchy.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntityHierarchy.java
@@ -1,36 +1,49 @@
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
 package org.hibernate.metamodel.source.binder;
 
 import org.hibernate.metamodel.binding.InheritanceType;
 
 /**
  * Models the source-agnostic view of an entity hierarchy.
  *
  * @author Steve Ebersole
  */
 public interface EntityHierarchy {
+	/**
+	 * The inheritance type/strategy for the hierarchy.
+	 * <p/>
+	 * NOTE : The entire hierarchy must comply with the same inheritance strategy.
+	 *
+	 * @return The inheritance type.
+	 */
 	public InheritanceType getHierarchyInheritanceType();
+
+	/**
+	 * Obtain the hierarchy's root entity.
+	 *
+	 * @return THe root entity.
+	 */
 	public RootEntitySource getRootEntitySource();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java
index 4a18919c90..5512d92067 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/EntitySource.java
@@ -1,67 +1,189 @@
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
 package org.hibernate.metamodel.source.binder;
 
 import java.util.List;
 
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.source.LocalBindingContext;
 import org.hibernate.metamodel.source.Origin;
 
 /**
+ * Contract describing source of an entity
+ *
  * @author Steve Ebersole
  */
 public interface EntitySource extends SubclassEntityContainer, AttributeSourceContainer {
+	/**
+	 * Obtain the origin of this source.
+	 *
+	 * @return The origin of this source.
+	 */
 	public Origin getOrigin();
+
+	/**
+	 * Obtain the binding context local to this entity source.
+	 *
+	 * @return The local binding context
+	 */
 	public LocalBindingContext getBindingContext();
 
+	/**
+	 * Obtain the entity name
+	 *
+	 * @return The entity name
+	 */
 	public String getEntityName();
+
+	/**
+	 * Obtain the name of the entity {@link Class}
+	 *
+	 * @return THe entity class name
+	 */
 	public String getClassName();
+
+	/**
+	 * Obtain the JPA name of the entity
+	 *
+	 * @return THe JPA-specific entity name
+	 */
 	public String getJpaEntityName();
 
+	/**
+	 * Obtain the primary table for this entity.
+	 *
+	 * @return The primary table.
+	 */
 	public TableSource getPrimaryTable();
 
-    public boolean isAbstract();
+	/**
+	 * Obtain the name of a custom tuplizer class to be used.
+	 *
+	 * @return The custom tuplizer class name
+	 */
+	public String getCustomTuplizerClassName();
+
+	/**
+	 * Obtain the name of a custom persister class to be used.
+	 *
+	 * @return The custom persister class name
+	 */
+    public String getCustomPersisterClassName();
+
+	/**
+	 * Is this entity lazy (proxyable)?
+	 *
+	 * @return {@code true} indicates the entity is lazy; {@code false} non-lazy.
+	 */
     public boolean isLazy();
+
+	/**
+	 * For {@link #isLazy() lazy} entities, obtain the interface to use in constructing its proxies.
+	 *
+	 * @return The proxy interface name
+	 */
     public String getProxy();
+
+	/**
+	 * Obtain the batch-size to be applied when initializing proxies of this entity.
+	 *
+	 * @return THe batch-size.
+	 */
     public int getBatchSize();
+
+	/**
+	 * Is the entity abstract?
+	 * <p/>
+	 * The implication is whether the entity maps to a database table.
+	 *
+	 * @return {@code true} indicates the entity is abstract; {@code false} non-abstract.
+	 */
+    public boolean isAbstract();
+
+	/**
+	 * Did the source specify dynamic inserts?
+	 *
+	 * @return {@code true} indicates dynamic inserts will be used; {@code false} otherwise.
+	 */
     public boolean isDynamicInsert();
+
+	/**
+	 * Did the source specify dynamic updates?
+	 *
+	 * @return {@code true} indicates dynamic updates will be used; {@code false} otherwise.
+	 */
     public boolean isDynamicUpdate();
-    public boolean isSelectBeforeUpdate();
 
-	public String getCustomTuplizerClassName();
-    public String getCustomPersisterClassName();
+	/**
+	 * Did the source specify to perform selects to decide whether to perform (detached) updates?
+	 *
+	 * @return {@code true} indicates selects will be done; {@code false} otherwise.
+	 */
+    public boolean isSelectBeforeUpdate();
 
+	/**
+	 * Obtain the name of a named-query that will be used for loading this entity
+	 *
+	 * @return THe custom loader query name
+	 */
 	public String getCustomLoaderName();
+
+	/**
+	 * Obtain the custom SQL to be used for inserts for this entity
+	 *
+	 * @return The custom insert SQL
+	 */
 	public CustomSQL getCustomSqlInsert();
+
+	/**
+	 * Obtain the custom SQL to be used for updates for this entity
+	 *
+	 * @return The custom update SQL
+	 */
 	public CustomSQL getCustomSqlUpdate();
+
+	/**
+	 * Obtain the custom SQL to be used for deletes for this entity
+	 *
+	 * @return The custom delete SQL
+	 */
 	public CustomSQL getCustomSqlDelete();
 
+	/**
+	 * Obtain any additional table names on which to synchronize (auto flushing) this entity.
+	 *
+	 * @return Additional synchronized table names.
+	 */
 	public List<String> getSynchronizedTableNames();
 
+	/**
+	 * Obtain the meta-attribute sources associated with this entity.
+	 *
+	 * @return The meta-attribute sources.
+	 */
 	public Iterable<MetaAttributeSource> metaAttributes();
 
 //	public List<XMLFetchProfileElement> getFetchProfile();
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/IdentifierAttributeSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/IdentifierAttributeSource.java
deleted file mode 100644
index 794f778cfd..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/IdentifierAttributeSource.java
+++ /dev/null
@@ -1,31 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
- * indicated by the @author tags or express copyright attribution
- * statements applied by the authors.  All third-party contributions are
- * distributed under license by Red Hat Inc.
- *
- * This copyrighted material is made available to anyone wishing to use, modify,
- * copy, or redistribute it subject to the terms and conditions of the GNU
- * Lesser General Public License, as published by the Free Software Foundation.
- *
- * This program is distributed in the hope that it will be useful,
- * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
- * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
- * for more details.
- *
- * You should have received a copy of the GNU Lesser General Public License
- * along with this distribution; if not, write to:
- * Free Software Foundation, Inc.
- * 51 Franklin Street, Fifth Floor
- * Boston, MA  02110-1301  USA
- */
-package org.hibernate.metamodel.source.binder;
-
-/**
- * @author Steve Ebersole
- */
-public interface IdentifierAttributeSource {
-
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/IdentifierSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/IdentifierSource.java
index 414d527107..c6d25e59dc 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/IdentifierSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/IdentifierSource.java
@@ -1,37 +1,56 @@
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
 package org.hibernate.metamodel.source.binder;
 
 /**
+ * Contract describing source of identifier information for the entity.
+ * 
  * @author Steve Ebersole
  */
 public interface IdentifierSource {
 	public static enum Nature {
+		/**
+		 * A single, simple identifier.  Equivalent of an {@code <id/>} mapping or a single {@code @Id}
+		 * annotation.  Indicates the {@link IdentifierSource} is castable to {@link SimpleIdentifierSource}.
+		 */
 		SIMPLE,
+		/**
+		 * What we used to term an "embedded composite identifier", which is not to be confused with the JPA
+		 * term embedded.  Specifically a composite id where there is no component class, though there may be an
+		 * {@code @IdClass}.
+		 */
 		COMPOSITE,
+		/**
+		 * Composite identifier with an actual component class used to aggregate the individual attributes
+		 */
 		AGGREGATED_COMPOSITE
 	}
 
+	/**
+	 * Obtain the nature of this identifier source.
+	 *
+	 * @return The identifier source's nature.
+	 */
 	public Nature getNature();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSource.java
index 21c2e075c1..14fbab5d8a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSource.java
@@ -1,30 +1,35 @@
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
 package org.hibernate.metamodel.source.binder;
 
 /**
+ * Unifying interface for {@link ColumnSource} and {@link DerivedValueSource}.
+ *
  * @author Steve Ebersole
+ * 
+ * @see ColumnSource
+ * @see DerivedValueSource
  */
 public interface RelationalValueSource {
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationValueMetadataSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSourceContainer.java
similarity index 81%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationValueMetadataSource.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSourceContainer.java
index b0dab77434..6a0757c8fc 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationValueMetadataSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RelationalValueSourceContainer.java
@@ -1,33 +1,40 @@
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
 package org.hibernate.metamodel.source.binder;
 
 import java.util.List;
 
 /**
+ * Contract for a container of {@link RelationalValueSource} references.
+ *
  * @author Steve Ebersole
  */
-public interface RelationValueMetadataSource {
+public interface RelationalValueSourceContainer {
+	/**
+	 * Obtain the contained {@link RelationalValueSource} references.
+	 *
+	 * @return The contained {@link RelationalValueSource} references.
+	 */
 	public List<RelationalValueSource> relationalValueSources();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RootEntitySource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RootEntitySource.java
index 33de81d1aa..12c002a1b9 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RootEntitySource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/RootEntitySource.java
@@ -1,47 +1,103 @@
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
 package org.hibernate.metamodel.source.binder;
 
-import java.util.List;
-
 import org.hibernate.EntityMode;
 import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.metamodel.binding.Caching;
 
 /**
+ * Contract for the entity that is the root of an inheritance hierarchy.
+ *
  * @author Steve Ebersole
  */
 public interface RootEntitySource extends EntitySource {
+	/**
+	 * Obtain source information about this entity's identifier.
+	 *
+	 * @return Identifier source information.
+	 */
 	public IdentifierSource getIdentifierSource();
+
+	/**
+	 * Obtain the source information about the attribute used for versioning.
+	 *
+	 * @return
+	 */
 	public SingularAttributeSource getVersioningAttributeSource();
+
+	// todo : I think this needs to go away
 	public SingularAttributeSource getDiscriminatorAttributeSource();
 
+	/**
+	 * Obtain the entity mode for this entity.
+	 * <p/>
+	 * todo : I think this should probably move to EntityHierarchy.
+	 *
+	 * @return The entity mode.
+	 */
 	public EntityMode getEntityMode();
+
+	/**
+	 * Is this root entity mutable?
+	 *
+	 * @return {@code true} indicates mutable; {@code false} non-mutable.
+	 */
 	public boolean isMutable();
+
+	/**
+	 * Should explicit polymorphism (querying) be applied to this entity?
+	 *
+	 * @return {@code true} indicates explicit polymorphism; {@code false} implicit.
+	 */
 	public boolean isExplicitPolymorphism();
+
+	/**
+	 * Obtain the specified extra where condition to be applied to this entity.
+	 *
+	 * @return The extra where condition
+	 */
 	public String getWhere();
+
+	/**
+	 * Obtain the row-id name for this entity
+	 *
+	 * @return The row-id name
+	 */
 	public String getRowId();
+
+	/**
+	 * Obtain the optimistic locking style for this entity.
+	 *
+	 * @return The optimistic locking style.
+	 */
 	public OptimisticLockStyle getOptimisticLockStyle();
+
+	/**
+	 * Obtain the caching configuration for this entity.
+	 *
+	 * @return The caching configuration.
+	 */
 	public Caching getCaching();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SimpleIdentifierSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SimpleIdentifierSource.java
index 1f031f3f73..c543d997a2 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SimpleIdentifierSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SimpleIdentifierSource.java
@@ -1,34 +1,47 @@
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
 package org.hibernate.metamodel.source.binder;
 
 import org.hibernate.metamodel.binding.IdGenerator;
 
 /**
+ * Contract describing source of a simple identifier mapping.
+ *
  * @author Steve Ebersole
  */
 public interface SimpleIdentifierSource extends IdentifierSource {
+	/**
+	 * Obtain the source descriptor for the identifier attribute.
+	 *
+	 * @return The identifier attribute source.
+	 */
 	public SingularAttributeSource getIdentifierAttributeSource();
+
+	/**
+	 * Obtain the identifier generator source.
+	 *
+	 * @return The generator source.
+	 */
 	public IdGenerator getIdentifierGeneratorDescriptor();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SingularAttributeSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SingularAttributeSource.java
index 01d2a38f15..bb74e0a319 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SingularAttributeSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/SingularAttributeSource.java
@@ -1,107 +1,105 @@
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
 package org.hibernate.metamodel.source.binder;
 
-import java.util.List;
-
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.mapping.PropertyGeneration;
 
 /**
  * Source-agnostic description of information needed to bind a singular attribute.
  *
  * @author Steve Ebersole
  */
-public interface SingularAttributeSource extends AttributeSource, RelationValueMetadataSource {
+public interface SingularAttributeSource extends AttributeSource, RelationalValueSourceContainer {
 	/**
 	 * Determine whether this is a virtual attribute or whether it physically exists on the users domain model.
 	 *
 	 * @return {@code true} indicates the attribute is virtual, meaning it does NOT exist on the domain model;
 	 * {@code false} indicates the attribute physically exists.
 	 */
 	public boolean isVirtualAttribute();
 
 	/**
 	 * Obtain the nature of this attribute type.
 	 *
 	 * @return The attribute type nature
 	 */
 	public SingularAttributeNature getNature();
 
 	/**
 	 * Obtain information about the Hibernate type ({@link org.hibernate.type.Type}) for this attribute.
 	 *
 	 * @return The Hibernate type information
 	 */
 	public ExplicitHibernateTypeSource getTypeInformation();
 
 	/**
 	 * Obtain the name of the property accessor style used to access this attribute.
 	 *
 	 * @return The property accessor style for this attribute.
 	 * @see org.hibernate.property.PropertyAccessor
 	 */
 	public String getPropertyAccessorName();
 
 	/**
 	 * Determine whether this attribute is insertable.
 	 *
 	 * @return {@code true} indicates the attribute value should be used in the {@code SQL INSERT}; {@code false}
 	 * indicates it should not.
 	 */
 	public boolean isInsertable();
 
 	/**
 	 * Determine whether this attribute is updateable.
 	 *
 	 * @return {@code true} indicates the attribute value should be used in the {@code SQL UPDATE}; {@code false}
 	 * indicates it should not.
 	 */
 	public boolean isUpdatable();
 
 	/**
 	 * Obtain a description of if/when the attribute value is generated by the database.
 	 *
 	 * @return The attribute value generation information
 	 */
 	public PropertyGeneration getGeneration();
 
 	/**
 	 * Should the attribute be (bytecode enhancement) lazily loaded?
 	 *
 	 * @return {@code true} to indicate the attribute should be lazily loaded.
 	 */
 	public boolean isLazy();
 
 	/**
 	 * If the containing entity is using {@link org.hibernate.engine.OptimisticLockStyle#ALL} or
 	 * {@link org.hibernate.engine.OptimisticLockStyle#DIRTY} style optimistic locking, should this attribute
 	 * be used?
 	 *
 	 * @return {@code true} indicates it should be included; {@code false}, it should not.
 	 */
 	public boolean isIncludedInOptimisticLocking();
 
 	public Iterable<CascadeStyle> getCascadeStyle();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/TableSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/TableSource.java
index d1c3112271..0a4bbfd49e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/TableSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/TableSource.java
@@ -1,33 +1,52 @@
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
 package org.hibernate.metamodel.source.binder;
 
 /**
+ * Contract describing source of table information
+ *
  * @author Steve Ebersole
  */
 public interface TableSource {
+	/**
+	 * Obtain the supplied schema name
+	 *
+	 * @return The schema name.  If {@code null}, the binder will apply the default.
+	 */
 	public String getExplicitSchemaName();
+
+	/**
+	 * Obtain the supplied catalog name
+	 *
+	 * @return The catalog name.  If {@code null}, the binder will apply the default.
+	 */
 	public String getExplicitCatalogName();
+
+	/**
+	 * Obtain the supplied table name.
+	 *
+	 * @return The table name.
+	 */
 	public String getExplicitTableName();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ToOneAttributeSource.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ToOneAttributeSource.java
index ff0897f594..892778dc4a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ToOneAttributeSource.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/binder/ToOneAttributeSource.java
@@ -1,32 +1,47 @@
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
 package org.hibernate.metamodel.source.binder;
 
 /**
+ * Further contract for sources of {@code *-to-one} style associations.
+ *
  * @author Steve Ebersole
  */
 public interface ToOneAttributeSource extends SingularAttributeSource {
+	/**
+	 * Obtain the name of the referenced entity.
+	 *
+	 * @return The name of the referenced entity
+	 */
 	public String getReferencedEntityName();
+
+	/**
+	 * Obtain the name of the referenced attribute.  Typically the reference is built based on the identifier
+	 * attribute of the {@link #getReferencedEntityName() referenced entity}, but this value allows using a different
+	 * attribute instead.
+	 *
+	 * @return The name of the referenced attribute; {@code null} indicates the identifier attribute.
+	 */
 	public String getReferencedEntityAttributeName();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmSourceProcessorImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmMetadataSourceProcessorImpl.java
similarity index 90%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmSourceProcessorImpl.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmMetadataSourceProcessorImpl.java
index 5c9d6b1eab..c4611120f2 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmSourceProcessorImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmMetadataSourceProcessorImpl.java
@@ -1,98 +1,98 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.metamodel.source.SourceProcessor;
+import org.hibernate.metamodel.source.MetadataSourceProcessor;
 import org.hibernate.metamodel.source.binder.Binder;
 import org.hibernate.metamodel.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
 
 /**
- * The {@link SourceProcessor} implementation responsible for processing {@code hbm.xml} sources.
+ * The {@link org.hibernate.metamodel.source.MetadataSourceProcessor} implementation responsible for processing {@code hbm.xml} sources.
  *
  * @author Steve Ebersole
  */
-public class HbmSourceProcessorImpl implements SourceProcessor {
+public class HbmMetadataSourceProcessorImpl implements MetadataSourceProcessor {
 	private final MetadataImplementor metadata;
 
 	private List<HibernateMappingProcessor> processors = new ArrayList<HibernateMappingProcessor>();
 	private List<EntityHierarchyImpl> entityHierarchies;
 
-	public HbmSourceProcessorImpl(MetadataImplementor metadata) {
+	public HbmMetadataSourceProcessorImpl(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public void prepare(MetadataSources sources) {
 		final HierarchyBuilder hierarchyBuilder = new HierarchyBuilder();
 
 		for ( JaxbRoot jaxbRoot : sources.getJaxbRootList() ) {
 			if ( ! XMLHibernateMapping.class.isInstance( jaxbRoot.getRoot() ) ) {
 				continue;
 			}
 
 			final MappingDocument mappingDocument = new MappingDocument( jaxbRoot, metadata );
 			processors.add( new HibernateMappingProcessor( metadata, mappingDocument ) );
 
 			hierarchyBuilder.processMappingDocument( mappingDocument );
 		}
 
 		this.entityHierarchies = hierarchyBuilder.groupEntityHierarchies();
 	}
 
 	@Override
 	public void processIndependentMetadata(MetadataSources sources) {
 		for ( HibernateMappingProcessor processor : processors ) {
 			processor.processIndependentMetadata();
 		}
 	}
 
 	@Override
 	public void processTypeDependentMetadata(MetadataSources sources) {
 		for ( HibernateMappingProcessor processor : processors ) {
 			processor.processTypeDependentMetadata();
 		}
 	}
 
 	@Override
 	public void processMappingMetadata(MetadataSources sources, List<String> processedEntityNames) {
 		Binder binder = new Binder( metadata, processedEntityNames );
 		for ( EntityHierarchyImpl entityHierarchy : entityHierarchies ) {
 			binder.processEntityHierarchy( entityHierarchy );
 		}
 	}
 
 	@Override
 	public void processMappingDependentMetadata(MetadataSources sources) {
 		for ( HibernateMappingProcessor processor : processors ) {
 			processor.processMappingDependentMetadata();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
index b778a4c426..9657e22e1a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
@@ -1,285 +1,285 @@
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
 package org.hibernate.metamodel.source.hbm;
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.BasicAuxiliaryDatabaseObjectImpl;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.MetadataImplementor;
 import org.hibernate.metamodel.source.Origin;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLFetchProfileElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLParamElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLQueryElement;
 import org.hibernate.metamodel.source.hbm.jaxb.mapping.XMLSqlQueryElement;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 import org.hibernate.type.Type;
 
 /**
  * Responsible for processing a {@code <hibernate-mapping/>} element.  Allows processing to be coordinated across
  * all hbm files in an ordered fashion.  The order is essentially the same as defined in
- * {@link org.hibernate.metamodel.source.SourceProcessor}
+ * {@link org.hibernate.metamodel.source.MetadataSourceProcessor}
  *
  * @author Steve Ebersole
  */
 public class HibernateMappingProcessor {
 	private final MetadataImplementor metadata;
 	private final MappingDocument mappingDocument;
 
 	private Value<ClassLoaderService> classLoaderService = new Value<ClassLoaderService>(
 			new Value.DeferredInitializer<ClassLoaderService>() {
 				@Override
 				public ClassLoaderService initialize() {
 					return metadata.getServiceRegistry().getService( ClassLoaderService.class );
 				}
 			}
 	);
 
 	public HibernateMappingProcessor(MetadataImplementor metadata, MappingDocument mappingDocument) {
 		this.metadata = metadata;
 		this.mappingDocument = mappingDocument;
 	}
 
 	private XMLHibernateMapping mappingRoot() {
 		return mappingDocument.getMappingRoot();
 	}
 
 	private Origin origin() {
 		return mappingDocument.getOrigin();
 	}
 
 	private HbmBindingContext bindingContext() {
 		return mappingDocument.getMappingLocalBindingContext();
 	}
 
 	private <T> Class<T> classForName(String name) {
 		return classLoaderService.getValue().classForName( bindingContext().qualifyClassName( name ) );
 	}
 
 	public void processIndependentMetadata() {
 		processDatabaseObjectDefinitions();
 		processTypeDefinitions();
 	}
 
 	private void processDatabaseObjectDefinitions() {
 		if ( mappingRoot().getDatabaseObject() == null ) {
 			return;
 		}
 
 		for ( XMLHibernateMapping.XMLDatabaseObject databaseObjectElement : mappingRoot().getDatabaseObject() ) {
 			final AuxiliaryDatabaseObject auxiliaryDatabaseObject;
 			if ( databaseObjectElement.getDefinition() != null ) {
 				final String className = databaseObjectElement.getDefinition().getClazz();
 				try {
 					auxiliaryDatabaseObject = (AuxiliaryDatabaseObject) classForName( className ).newInstance();
 				}
 				catch (ClassLoadingException e) {
 					throw e;
 				}
 				catch (Exception e) {
 					throw new MappingException(
 							"could not instantiate custom database object class [" + className + "]",
 							origin()
 					);
 				}
 			}
 			else {
 				Set<String> dialectScopes = new HashSet<String>();
 				if ( databaseObjectElement.getDialectScope() != null ) {
 					for ( XMLHibernateMapping.XMLDatabaseObject.XMLDialectScope dialectScope : databaseObjectElement.getDialectScope() ) {
 						dialectScopes.add( dialectScope.getName() );
 					}
 				}
 				auxiliaryDatabaseObject = new BasicAuxiliaryDatabaseObjectImpl(
 						databaseObjectElement.getCreate(),
 						databaseObjectElement.getDrop(),
 						dialectScopes
 				);
 			}
 			metadata.getDatabase().addAuxiliaryDatabaseObject( auxiliaryDatabaseObject );
 		}
 	}
 
 	private void processTypeDefinitions() {
 		if ( mappingRoot().getTypedef() == null ) {
 			return;
 		}
 
 		for ( XMLHibernateMapping.XMLTypedef typedef : mappingRoot().getTypedef() ) {
 			final Map<String, String> parameters = new HashMap<String, String>();
 			for ( XMLParamElement paramElement : typedef.getParam() ) {
 				parameters.put( paramElement.getName(), paramElement.getValue() );
 			}
 			metadata.addTypeDefinition(
 					new TypeDef(
 							typedef.getName(),
 							typedef.getClazz(),
 							parameters
 					)
 			);
 		}
 	}
 
 	public void processTypeDependentMetadata() {
 		processFilterDefinitions();
 		processIdentifierGenerators();
 	}
 
 	private void processFilterDefinitions() {
 		if ( mappingRoot().getFilterDef() == null ) {
 			return;
 		}
 
 		for ( XMLHibernateMapping.XMLFilterDef filterDefinition : mappingRoot().getFilterDef() ) {
 			final String name = filterDefinition.getName();
 			final Map<String,Type> parameters = new HashMap<String, Type>();
 			String condition = null;
 			for ( Object o : filterDefinition.getContent() ) {
 				if ( o instanceof String ) {
 					// represents the condition
 					if ( condition != null ) {
 						// log?
 					}
 					condition = (String) o;
 				}
 				else if ( o instanceof XMLHibernateMapping.XMLFilterDef.XMLFilterParam ) {
 					final XMLHibernateMapping.XMLFilterDef.XMLFilterParam paramElement = (XMLHibernateMapping.XMLFilterDef.XMLFilterParam) o;
 					// todo : should really delay this resolution until later to allow typedef names
 					parameters.put(
 							paramElement.getName(),
 							metadata.getTypeResolver().heuristicType( paramElement.getType() )
 					);
 				}
 				else {
 					throw new MappingException( "Unrecognized nested filter content", origin() );
 				}
 			}
 			if ( condition == null ) {
 				condition = filterDefinition.getCondition();
 			}
 			metadata.addFilterDefinition( new FilterDefinition( name, condition, parameters ) );
 		}
 	}
 
 	private void processIdentifierGenerators() {
 		if ( mappingRoot().getIdentifierGenerator() == null ) {
 			return;
 		}
 
 		for ( XMLHibernateMapping.XMLIdentifierGenerator identifierGeneratorElement : mappingRoot().getIdentifierGenerator() ) {
 			metadata.registerIdentifierGenerator(
 					identifierGeneratorElement.getName(),
 					identifierGeneratorElement.getClazz()
 			);
 		}
 	}
 
 	public void processMappingDependentMetadata() {
 		processFetchProfiles();
 		processImports();
 		processResultSetMappings();
 		processNamedQueries();
 	}
 
 	private void processFetchProfiles(){
 		if ( mappingRoot().getFetchProfile() == null ) {
 			return;
 		}
 
 		processFetchProfiles( mappingRoot().getFetchProfile(), null );
 	}
 
 	public void processFetchProfiles(List<XMLFetchProfileElement> fetchProfiles, String containingEntityName) {
 		for ( XMLFetchProfileElement fetchProfile : fetchProfiles ) {
 			String profileName = fetchProfile.getName();
 			Set<FetchProfile.Fetch> fetches = new HashSet<FetchProfile.Fetch>();
 			for ( XMLFetchProfileElement.XMLFetch fetch : fetchProfile.getFetch() ) {
 				String entityName = fetch.getEntity() == null ? containingEntityName : fetch.getEntity();
 				if ( entityName == null ) {
 					throw new MappingException(
 							"could not determine entity for fetch-profile fetch [" + profileName + "]:[" +
 									fetch.getAssociation() + "]",
 							origin()
 					);
 				}
 				fetches.add( new FetchProfile.Fetch( entityName, fetch.getAssociation(), fetch.getStyle() ) );
 			}
 			metadata.addFetchProfile( new FetchProfile( profileName, fetches ) );
 		}
 	}
 
 	private void processImports() {
 		if ( mappingRoot().getImport() == null ) {
 			return;
 		}
 
 		for ( XMLHibernateMapping.XMLImport importValue : mappingRoot().getImport() ) {
 			String className = mappingDocument.getMappingLocalBindingContext().qualifyClassName( importValue.getClazz() );
 			String rename = importValue.getRename();
 			rename = ( rename == null ) ? StringHelper.unqualify( className ) : rename;
 			metadata.addImport( className, rename );
 		}
 	}
 
 	private void processResultSetMappings() {
 		if ( mappingRoot().getResultset() == null ) {
 			return;
 		}
 
 //			bindResultSetMappingDefinitions( element, null, mappings );
 	}
 
 	private void processNamedQueries() {
 		if ( mappingRoot().getQueryOrSqlQuery() == null ) {
 			return;
 		}
 
 		for ( Object queryOrSqlQuery : mappingRoot().getQueryOrSqlQuery() ) {
 			if ( XMLQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //					bindNamedQuery( element, null, mappings );
 			}
 			else if ( XMLSqlQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //				bindNamedSQLQuery( element, null, mappings );
 			}
 			else {
 				throw new MappingException(
 						"unknown type of query: " +
 								queryOrSqlQuery.getClass().getName(), origin()
 				);
 			}
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
index 0bb2ea2cce..92d65fbba4 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
@@ -1,196 +1,196 @@
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
 package org.hibernate.metamodel.source.internal;
 
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.EJB3NamingStrategy;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.metamodel.Metadata;
 import org.hibernate.metamodel.MetadataBuilder;
+import org.hibernate.metamodel.MetadataSourceProcessingOrder;
 import org.hibernate.metamodel.MetadataSources;
-import org.hibernate.metamodel.SourceProcessingOrder;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.config.spi.ConfigurationService;
 
 /**
  * @author Steve Ebersole
  */
 public class MetadataBuilderImpl implements MetadataBuilder {
 	private final MetadataSources sources;
 	private final OptionsImpl options;
 
 	public MetadataBuilderImpl(MetadataSources sources) {
 		this.sources = sources;
 		this.options = new OptionsImpl( sources.getServiceRegistry() );
 	}
 
 	@Override
 	public MetadataBuilder with(NamingStrategy namingStrategy) {
 		this.options.namingStrategy = namingStrategy;
 		return this;
 	}
 
 	@Override
-	public MetadataBuilder with(SourceProcessingOrder sourceProcessingOrder) {
-		this.options.sourceProcessingOrder = sourceProcessingOrder;
+	public MetadataBuilder with(MetadataSourceProcessingOrder metadataSourceProcessingOrder) {
+		this.options.metadataSourceProcessingOrder = metadataSourceProcessingOrder;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder with(SharedCacheMode sharedCacheMode) {
 		this.options.sharedCacheMode = sharedCacheMode;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder with(AccessType accessType) {
 		this.options.defaultCacheAccessType = accessType;
 		return this;
 	}
 
 	@Override
 	public MetadataBuilder withNewIdentifierGeneratorsEnabled(boolean enabled) {
 		this.options.useNewIdentifierGenerators = enabled;
 		return this;
 	}
 
 	@Override
 	public Metadata buildMetadata() {
 		return new MetadataImpl( sources, options );
 	}
 
 	private static class OptionsImpl implements Metadata.Options {
-		private SourceProcessingOrder sourceProcessingOrder = SourceProcessingOrder.HBM_FIRST;
+		private MetadataSourceProcessingOrder metadataSourceProcessingOrder = MetadataSourceProcessingOrder.HBM_FIRST;
 		private NamingStrategy namingStrategy = EJB3NamingStrategy.INSTANCE;
 		private SharedCacheMode sharedCacheMode = SharedCacheMode.ENABLE_SELECTIVE;
 		private AccessType defaultCacheAccessType;
         private boolean useNewIdentifierGenerators;
         private boolean globallyQuotedIdentifiers;
 		private String defaultSchemaName;
 		private String defaultCatalogName;
 
 		public OptionsImpl(BasicServiceRegistry serviceRegistry) {
 			ConfigurationService configService = serviceRegistry.getService( ConfigurationService.class );
 
 			// cache access type
 			defaultCacheAccessType = configService.getSetting(
 					AvailableSettings.DEFAULT_CACHE_CONCURRENCY_STRATEGY,
 					new ConfigurationService.Converter<AccessType>() {
 						@Override
 						public AccessType convert(Object value) {
 							return AccessType.fromExternalName( value.toString() );
 						}
 					}
 			);
 
 			useNewIdentifierGenerators = configService.getSetting(
 					AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS,
 					new ConfigurationService.Converter<Boolean>() {
 						@Override
 						public Boolean convert(Object value) {
 							return Boolean.parseBoolean( value.toString() );
 						}
 					},
 					false
 			);
 
 			defaultSchemaName = configService.getSetting(
 					AvailableSettings.DEFAULT_SCHEMA,
 					new ConfigurationService.Converter<String>() {
 						@Override
 						public String convert(Object value) {
 							return value.toString();
 						}
 					},
 					null
 			);
 
 			defaultCatalogName = configService.getSetting(
 					AvailableSettings.DEFAULT_CATALOG,
 					new ConfigurationService.Converter<String>() {
 						@Override
 						public String convert(Object value) {
 							return value.toString();
 						}
 					},
 					null
 			);
 
             globallyQuotedIdentifiers = configService.getSetting(
                     AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS,
                     new ConfigurationService.Converter<Boolean>() {
                         @Override
                         public Boolean convert(Object value) {
                             return Boolean.parseBoolean( value.toString() );
                         }
                     },
                     false
             );
 		}
 
 
 		@Override
-		public SourceProcessingOrder getSourceProcessingOrder() {
-			return sourceProcessingOrder;
+		public MetadataSourceProcessingOrder getMetadataSourceProcessingOrder() {
+			return metadataSourceProcessingOrder;
 		}
 
 		@Override
 		public NamingStrategy getNamingStrategy() {
 			return namingStrategy;
 		}
 
 		@Override
 		public AccessType getDefaultAccessType() {
 			return defaultCacheAccessType;
 		}
 
 		@Override
 		public SharedCacheMode getSharedCacheMode() {
 			return sharedCacheMode;
 		}
 
 		@Override
         public boolean useNewIdentifierGenerators() {
             return useNewIdentifierGenerators;
         }
 
         @Override
         public boolean isGloballyQuotedIdentifiers() {
             return globallyQuotedIdentifiers;
         }
 
         @Override
 		public String getDefaultSchemaName() {
 			return defaultSchemaName;
 		}
 
 		@Override
 		public String getDefaultCatalogName() {
 			return defaultCatalogName;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index c7df43caf6..3779e4e1e3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,595 +1,595 @@
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
 package org.hibernate.metamodel.source.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.Value;
+import org.hibernate.metamodel.MetadataSourceProcessingOrder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
-import org.hibernate.metamodel.SourceProcessingOrder;
 import org.hibernate.metamodel.source.MappingDefaults;
 import org.hibernate.metamodel.source.MetaAttributeContext;
 import org.hibernate.metamodel.source.MetadataImplementor;
-import org.hibernate.metamodel.source.SourceProcessor;
-import org.hibernate.metamodel.source.annotations.AnnotationProcessor;
-import org.hibernate.metamodel.source.hbm.HbmSourceProcessorImpl;
+import org.hibernate.metamodel.source.MetadataSourceProcessor;
+import org.hibernate.metamodel.source.annotations.AnnotationMetadataSourceProcessorImpl;
+import org.hibernate.metamodel.source.hbm.HbmMetadataSourceProcessorImpl;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.BasicType;
 import org.hibernate.metamodel.domain.Type;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.type.TypeResolver;
 
 /**
  * Container for configuration data collected during binding the metamodel.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  * @author Gail Badner
  */
 public class MetadataImpl implements MetadataImplementor, Serializable {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			MetadataImpl.class.getName()
 	);
 
 	private final BasicServiceRegistry serviceRegistry;
 	private final Options options;
 
 	private final Value<ClassLoaderService> classLoaderService;
 	private final Value<PersisterClassResolver> persisterClassResolverService;
 
 	private TypeResolver typeResolver = new TypeResolver();
 
 	private SessionFactoryBuilder sessionFactoryBuilder = new SessionFactoryBuilderImpl( this );
 
 	private DefaultIdentifierGeneratorFactory identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
 
 	private final Database database;
 
 	private final MappingDefaults mappingDefaults;
 
 	/**
 	 * Maps the fully qualified class name of an entity to its entity binding
 	 */
 	private Map<String, EntityBinding> entityBindingMap = new HashMap<String, EntityBinding>();
 
 	private Map<String, PluralAttributeBinding> collectionBindingMap = new HashMap<String, PluralAttributeBinding>();
 	private Map<String, FetchProfile> fetchProfiles = new HashMap<String, FetchProfile>();
 	private Map<String, String> imports = new HashMap<String, String>();
 	private Map<String, TypeDef> typeDefs = new HashMap<String, TypeDef>();
 	private Map<String, IdGenerator> idGenerators = new HashMap<String, IdGenerator>();
 	private Map<String, NamedQueryDefinition> namedQueryDefs = new HashMap<String, NamedQueryDefinition>();
 	private Map<String, NamedSQLQueryDefinition> namedNativeQueryDefs = new HashMap<String, NamedSQLQueryDefinition>();
 	private Map<String, ResultSetMappingDefinition> resultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 	private Map<String, FilterDefinition> filterDefs = new HashMap<String, FilterDefinition>();
 
     private boolean globallyQuotedIdentifiers = false;
 
 	public MetadataImpl(MetadataSources metadataSources, Options options) {
 		this.serviceRegistry = metadataSources.getServiceRegistry();
 		this.options = options;
 		this.database = new Database( options );
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
-		final SourceProcessor[] sourceProcessors;
-		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
-			sourceProcessors = new SourceProcessor[] {
-					new HbmSourceProcessorImpl( this ),
-					new AnnotationProcessor( this )
+		final MetadataSourceProcessor[] metadataSourceProcessors;
+		if ( options.getMetadataSourceProcessingOrder() == MetadataSourceProcessingOrder.HBM_FIRST ) {
+			metadataSourceProcessors = new MetadataSourceProcessor[] {
+					new HbmMetadataSourceProcessorImpl( this ),
+					new AnnotationMetadataSourceProcessorImpl( this )
 			};
 		}
 		else {
-			sourceProcessors = new SourceProcessor[] {
-					new AnnotationProcessor( this ),
-					new HbmSourceProcessorImpl( this )
+			metadataSourceProcessors = new MetadataSourceProcessor[] {
+					new AnnotationMetadataSourceProcessorImpl( this ),
+					new HbmMetadataSourceProcessorImpl( this )
 			};
 		}
 
 		this.classLoaderService = new org.hibernate.internal.util.Value<ClassLoaderService>(
 				new org.hibernate.internal.util.Value.DeferredInitializer<ClassLoaderService>() {
 					@Override
 					public ClassLoaderService initialize() {
 						return serviceRegistry.getService( ClassLoaderService.class );
 					}
 				}
 		);
 		this.persisterClassResolverService = new org.hibernate.internal.util.Value<PersisterClassResolver>(
 				new org.hibernate.internal.util.Value.DeferredInitializer<PersisterClassResolver>() {
 					@Override
 					public PersisterClassResolver initialize() {
 						return serviceRegistry.getService( PersisterClassResolver.class );
 					}
 				}
 		);
 
 
 		final ArrayList<String> processedEntityNames = new ArrayList<String>();
 
-		prepare( sourceProcessors, metadataSources );
-		bindIndependentMetadata( sourceProcessors, metadataSources );
-		bindTypeDependentMetadata( sourceProcessors, metadataSources );
-		bindMappingMetadata( sourceProcessors, metadataSources, processedEntityNames );
-		bindMappingDependentMetadata( sourceProcessors, metadataSources );
+		prepare( metadataSourceProcessors, metadataSources );
+		bindIndependentMetadata( metadataSourceProcessors, metadataSources );
+		bindTypeDependentMetadata( metadataSourceProcessors, metadataSources );
+		bindMappingMetadata( metadataSourceProcessors, metadataSources, processedEntityNames );
+		bindMappingDependentMetadata( metadataSourceProcessors, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
 		new EntityReferenceResolver( this ).resolve();
 		new AttributeTypeResolver( this ).resolve();
 	}
 
-	private void prepare(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
-		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
-			sourceProcessor.prepare( metadataSources );
+	private void prepare(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
+		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
+			metadataSourceProcessor.prepare( metadataSources );
 		}
 	}
 
-	private void bindIndependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
-		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
-			sourceProcessor.processIndependentMetadata( metadataSources );
+	private void bindIndependentMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
+		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
+			metadataSourceProcessor.processIndependentMetadata( metadataSources );
 		}
 	}
 
-	private void bindTypeDependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
-		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
-			sourceProcessor.processTypeDependentMetadata( metadataSources );
+	private void bindTypeDependentMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
+		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
+			metadataSourceProcessor.processTypeDependentMetadata( metadataSources );
 		}
 	}
 
-	private void bindMappingMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources, List<String> processedEntityNames) {
-		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
-			sourceProcessor.processMappingMetadata( metadataSources, processedEntityNames );
+	private void bindMappingMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources, List<String> processedEntityNames) {
+		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
+			metadataSourceProcessor.processMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
-	private void bindMappingDependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
-		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
-			sourceProcessor.processMappingDependentMetadata( metadataSources );
+	private void bindMappingDependentMetadata(MetadataSourceProcessor[] metadataSourceProcessors, MetadataSources metadataSources) {
+		for ( MetadataSourceProcessor metadataSourceProcessor : metadataSourceProcessors ) {
+			metadataSourceProcessor.processMappingDependentMetadata( metadataSources );
 		}
 	}
 
 	@Override
 	public void addFetchProfile(FetchProfile profile) {
 		if ( profile == null || profile.getName() == null ) {
 			throw new IllegalArgumentException( "Fetch profile object or name is null: " + profile );
 		}
 		fetchProfiles.put( profile.getName(), profile );
 	}
 
 	@Override
 	public void addFilterDefinition(FilterDefinition def) {
 		if ( def == null || def.getFilterName() == null ) {
 			throw new IllegalArgumentException( "Filter definition object or name is null: "  + def );
 		}
 		filterDefs.put( def.getFilterName(), def );
 	}
 
 	public Iterable<FilterDefinition> getFilterDefinitions() {
 		return filterDefs.values();
 	}
 
 	@Override
 	public void addIdGenerator(IdGenerator generator) {
 		if ( generator == null || generator.getName() == null ) {
 			throw new IllegalArgumentException( "ID generator object or name is null." );
 		}
 		idGenerators.put( generator.getName(), generator );
 	}
 
 	@Override
 	public IdGenerator getIdGenerator(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid generator name" );
 		}
 		return idGenerators.get( name );
 	}
 	@Override
 	public void registerIdentifierGenerator(String name, String generatorClassName) {
 		 identifierGeneratorFactory.register( name, classLoaderService().classForName( generatorClassName ) );
 	}
 
 	@Override
 	public void addNamedNativeQuery(NamedSQLQueryDefinition def) {
 		if ( def == null || def.getName() == null ) {
 			throw new IllegalArgumentException( "Named native query definition object or name is null: " + def.getQueryString() );
 		}
 		namedNativeQueryDefs.put( def.getName(), def );
 	}
 
 	public NamedSQLQueryDefinition getNamedNativeQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid native query name" );
 		}
 		return namedNativeQueryDefs.get( name );
 	}
 
 	@Override
 	public Iterable<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions() {
 		return namedNativeQueryDefs.values();
 	}
 
 	@Override
 	public void addNamedQuery(NamedQueryDefinition def) {
 		if ( def == null ) {
 			throw new IllegalArgumentException( "Named query definition is null" );
 		}
 		else if ( def.getName() == null ) {
 			throw new IllegalArgumentException( "Named query definition name is null: " + def.getQueryString() );
 		}
 		namedQueryDefs.put( def.getName(), def );
 	}
 
 	public NamedQueryDefinition getNamedQuery(String name) {
 		if ( name == null ) {
 			throw new IllegalArgumentException( "null is not a valid query name" );
 		}
 		return namedQueryDefs.get( name );
 	}
 
 	@Override
 	public Iterable<NamedQueryDefinition> getNamedQueryDefinitions() {
 		return namedQueryDefs.values();
 	}
 
 	@Override
 	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
 		if ( resultSetMappingDefinition == null || resultSetMappingDefinition.getName() == null ) {
 			throw new IllegalArgumentException( "Result-set mapping object or name is null: " + resultSetMappingDefinition );
 		}
 		resultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 	}
 
 	@Override
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions() {
 		return resultSetMappings.values();
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDef typeDef) {
 		if ( typeDef == null ) {
 			throw new IllegalArgumentException( "Type definition is null" );
 		}
 		else if ( typeDef.getName() == null ) {
 			throw new IllegalArgumentException( "Type definition name is null: " + typeDef.getTypeClass() );
 		}
 		final TypeDef previous = typeDefs.put( typeDef.getName(), typeDef );
 		if ( previous != null ) {
 			LOG.debugf( "Duplicate typedef name [%s] now -> %s", typeDef.getName(), typeDef.getTypeClass() );
 		}
 	}
 
 	@Override
 	public Iterable<TypeDef> getTypeDefinitions() {
 		return typeDefs.values();
 	}
 
 	@Override
 	public TypeDef getTypeDefinition(String name) {
 		return typeDefs.get( name );
 	}
 
 	private ClassLoaderService classLoaderService() {
 		return classLoaderService.getValue();
 	}
 
 	private PersisterClassResolver persisterClassResolverService() {
 		return persisterClassResolverService.getValue();
 	}
 
 	@Override
 	public Options getOptions() {
 		return options;
 	}
 
 	@Override
 	public SessionFactory buildSessionFactory() {
 		return sessionFactoryBuilder.buildSessionFactory();
 	}
 
 	@Override
 	public BasicServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public <T> Class<T> locateClassByName(String name) {
 		return classLoaderService().classForName( name );
 	}
 
 	@Override
 	public Type makeJavaType(String className) {
 		// todo : have this perform some analysis of the incoming type name to determine appropriate return
 		return new BasicType( className, makeClassReference( className ) );
 	}
 
 	@Override
 	public Value<Class<?>> makeClassReference(final String className) {
 		return new Value<Class<?>>(
 				new Value.DeferredInitializer<Class<?>>() {
 					@Override
 					public Class<?> initialize() {
 						return classLoaderService.getValue().classForName( className );
 					}
 				}
 		);
 	}
 
 	@Override
 	public String qualifyClassName(String name) {
 		return name;
 	}
 
 	@Override
 	public Database getDatabase() {
 		return database;
 	}
 
 	public EntityBinding getEntityBinding(String entityName) {
 		return entityBindingMap.get( entityName );
 	}
 
 	@Override
 	public EntityBinding getRootEntityBinding(String entityName) {
 		EntityBinding binding = entityBindingMap.get( entityName );
 		if ( binding == null ) {
 			throw new IllegalStateException( "Unknown entity binding: " + entityName );
 		}
 
 		do {
 			if ( binding.isRoot() ) {
 				return binding;
 			}
 			binding = binding.getSuperEntityBinding();
 		} while ( binding != null );
 
 		throw new AssertionFailure( "Entity binding has no root: " + entityName );
 	}
 
 	public Iterable<EntityBinding> getEntityBindings() {
 		return entityBindingMap.values();
 	}
 
 	public void addEntity(EntityBinding entityBinding) {
 		final String entityName = entityBinding.getEntity().getName();
 		if ( entityBindingMap.containsKey( entityName ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, entityName );
 		}
 		entityBindingMap.put( entityName, entityBinding );
 	}
 
 	public PluralAttributeBinding getCollection(String collectionRole) {
 		return collectionBindingMap.get( collectionRole );
 	}
 
 	@Override
 	public Iterable<PluralAttributeBinding> getCollectionBindings() {
 		return collectionBindingMap.values();
 	}
 
 	public void addCollection(PluralAttributeBinding pluralAttributeBinding) {
 		final String owningEntityName = pluralAttributeBinding.getEntityBinding().getEntity().getName();
 		final String attributeName = pluralAttributeBinding.getAttribute().getName();
 		final String collectionRole = owningEntityName + '.' + attributeName;
 		if ( collectionBindingMap.containsKey( collectionRole ) ) {
 			throw new DuplicateMappingException( DuplicateMappingException.Type.ENTITY, collectionRole );
 		}
 		collectionBindingMap.put( collectionRole, pluralAttributeBinding );
 	}
 
 	public void addImport(String importName, String entityName) {
 		if ( importName == null || entityName == null ) {
 			throw new IllegalArgumentException( "Import name or entity name is null" );
 		}
 		LOG.trace( "Import: " + importName + " -> " + entityName );
 		String old = imports.put( importName, entityName );
 		if ( old != null ) {
 			LOG.debug( "import name [" + importName + "] overrode previous [{" + old + "}]" );
 		}
 	}
 
 	public Iterable<Map.Entry<String, String>> getImports() {
 		return imports.entrySet();
 	}
 
 	public Iterable<FetchProfile> getFetchProfiles() {
 		return fetchProfiles.values();
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	@Override
 	public SessionFactoryBuilder getSessionFactoryBuilder() {
 		return sessionFactoryBuilder;
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return options.getNamingStrategy();
 	}
 
     @Override
     public boolean isGloballyQuotedIdentifiers() {
         return globallyQuotedIdentifiers || getOptions().isGloballyQuotedIdentifiers();
     }
 
     public void setGloballyQuotedIdentifiers(boolean globallyQuotedIdentifiers){
        this.globallyQuotedIdentifiers = globallyQuotedIdentifiers;
     }
 
     @Override
 	public MappingDefaults getMappingDefaults() {
 		return mappingDefaults;
 	}
 
 	private final MetaAttributeContext globalMetaAttributeContext = new MetaAttributeContext();
 
 	@Override
 	public MetaAttributeContext getGlobalMetaAttributeContext() {
 		return globalMetaAttributeContext;
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		return this;
 	}
 
 	private static final String DEFAULT_IDENTIFIER_COLUMN_NAME = "id";
 	private static final String DEFAULT_DISCRIMINATOR_COLUMN_NAME = "class";
 	private static final String DEFAULT_CASCADE = "none";
 	private static final String DEFAULT_PROPERTY_ACCESS = "property";
 
 	@Override
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return identifierGeneratorFactory;
 	}
 
 	@Override
 	public org.hibernate.type.Type getIdentifierType(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		return entityBinding
 				.getEntityIdentifier()
 				.getValueBinding()
 				.getHibernateTypeDescriptor()
 				.getResolvedTypeMapping();
 	}
 
 	@Override
 	public String getIdentifierPropertyName(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		AttributeBinding idBinding = entityBinding.getEntityIdentifier().getValueBinding();
 		return idBinding == null ? null : idBinding.getAttribute().getName();
 	}
 
 	@Override
 	public org.hibernate.type.Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
 		AttributeBinding attributeBinding = entityBinding.getAttributeBinding( propertyName );
 		if ( attributeBinding == null ) {
 			throw new MappingException( "unknown property: " + entityName + '.' + propertyName );
 		}
 		return attributeBinding.getHibernateTypeDescriptor().getResolvedTypeMapping();
 	}
 
 	private class MappingDefaultsImpl implements MappingDefaults {
 
 		@Override
 		public String getPackageName() {
 			return null;
 		}
 
 		@Override
 		public String getSchemaName() {
 			return options.getDefaultSchemaName();
 		}
 
 		@Override
 		public String getCatalogName() {
 			return options.getDefaultCatalogName();
 		}
 
 		@Override
 		public String getIdColumnName() {
 			return DEFAULT_IDENTIFIER_COLUMN_NAME;
 		}
 
 		@Override
 		public String getDiscriminatorColumnName() {
 			return DEFAULT_DISCRIMINATOR_COLUMN_NAME;
 		}
 
 		@Override
 		public String getCascadeStyle() {
 			return DEFAULT_CASCADE;
 		}
 
 		@Override
 		public String getPropertyAccessorName() {
 			return DEFAULT_PROPERTY_ACCESS;
 		}
 
 		@Override
 		public boolean areAssociationsLazy() {
 			return true;
 		}
 
 		private final Value<AccessType> regionFactorySpecifiedDefaultAccessType = new Value<AccessType>(
 				new Value.DeferredInitializer<AccessType>() {
 					@Override
 					public AccessType initialize() {
 						final RegionFactory regionFactory = getServiceRegistry().getService( RegionFactory.class );
 						return regionFactory.getDefaultAccessType();
 					}
 				}
 		);
 
 		@Override
 		public AccessType getCacheAccessType() {
 			return options.getDefaultAccessType() != null
 					? options.getDefaultAccessType()
 					: regionFactorySpecifiedDefaultAccessType.getValue();
 		}
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/Wicked.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/legacy/Wicked.hbm.xml
index 2e5424b251..f0c420863b 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/Wicked.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/Wicked.hbm.xml
@@ -1,71 +1,71 @@
 <?xml version="1.0"?>
 <!DOCTYPE hibernate-mapping PUBLIC 
 	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
 	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 
-<!-- Mapping document mainly used for testing non-reflective SourceProcessor + meta inheritance -->
+<!-- Mapping document mainly used for testing non-reflective MetadataSourceProcessor + meta inheritance -->
 <hibernate-mapping default-lazy="false">
   <meta attribute="global">global value</meta>
   <meta attribute="globalnoinherit" inherit="false">only visible at top level</meta>
   <meta attribute="globalmutated">top level</meta>
   
     <class name="org.hibernate.test.legacy.Wicked"
            table="WICKED"
            schema="HR">
            <meta attribute="implements">java.lang.Observer</meta>
            <meta attribute="implements">java.lang.Observer</meta>           
            <meta attribute="implements" inherit="false">org.foo.BogusVisitor</meta>                      
            <meta attribute="extends">AuditInfo</meta>
 		   <meta attribute="globalmutated">wicked level</meta>
         <id name="id"
             type="long"
             column="EMPLOYEE_ID">
             <generator class="assigned"/>
         </id>
         <version name="versionProp"       type="long"/>
         <property name="stringProp"       type="string"/>
         <property name="doubleProp"       type="double"/>
 	    <property name="objectDoubleProp" type="java.lang.Double"/>
         <property name="booleanProp"       type="boolean"/>
 	    <property name="objectBooleanProp" type="java.lang.Boolean"/>
   	    <property name="binaryProp"       type="binary"/>
         <many-to-one name="objectManyToOne"  class="org.hibernate.test.legacy.Employee" column="MANAGER_ID"/>
 		<component name="component" class="net.sf.hibern8ide.test.MonetaryAmount">
 		  <meta attribute="componentonly" inherit="true"/>
 		  <meta attribute="implements">AnotherInterface</meta>
 		  <meta attribute="allcomponent"/>
  		  <meta attribute="globalmutated">monetaryamount level</meta>
 			<property name="x" type="string">
 	  		    <meta attribute="globalmutated">monetaryamount x level</meta>
   		    </property>
 		</component>
 
 		<set name="sortedEmployee" sort="org.hibernate.test.legacy.NonExistingComparator">
   		     <meta attribute="globalmutated">sortedemployee level</meta>
 			 <key column="attrb_id"/> 
     	     <many-to-many class="org.hibernate.test.legacy.Employee" column="id"/>
 		</set>
 
         <bag name="anotherSet">
 			 <key column="attrb2_id"/> 			 
 			 <composite-element class="org.hibernate.test.legacy.Employee">
   		     <meta attribute="globalmutated">monetaryamount anotherSet composite level</meta>
   		      <property name="emp" type="string">
     		      <meta attribute="globalmutated">monetaryamount anotherSet composite property emp level</meta>
   		      </property>
   		      <many-to-one name="empinone" class="org.hibernate.test.legacy.Employee">
   		          <meta attribute="globalmutated">monetaryamount anotherSet composite property empinone level</meta>
   		      </many-to-one>
 			 </composite-element>
         </bag>	
 
    </class>
    
    <class name="org.hibernate.test.legacy.Employee">
         <composite-id class="X">
             <key-property name="comp" type="string"/>
         </composite-id>
 		
    </class>
 </hibernate-mapping>
 
