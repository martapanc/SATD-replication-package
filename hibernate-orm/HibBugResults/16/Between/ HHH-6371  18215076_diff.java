diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 2b8013517e..5077150352 100644
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
-			 * Right now HbmBinder does not support the
+			 * Right now HbmSourceProcessor does not support the
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
index a4fa8138e4..7982fede1c 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/SettingsFactory.java
@@ -1,400 +1,400 @@
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
 
 import java.io.Serializable;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MultiTenancyStrategy;
 import org.hibernate.cache.internal.NoCachingRegionFactory;
 import org.hibernate.cache.internal.StandardQueryCacheFactory;
 import org.hibernate.cache.spi.QueryCacheFactory;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.engine.jdbc.spi.ExtractedDatabaseMetaData;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.transaction.spi.TransactionFactory;
 import org.hibernate.hql.spi.QueryTranslatorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.config.ConfigurationHelper;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.tuple.entity.EntityTuplizerFactory;
 
 /**
  * Reads configuration properties and builds a {@link Settings} instance.
  *
  * @author Gavin King
  */
 public class SettingsFactory implements Serializable {
 
     private static final long serialVersionUID = -1194386144994524825L;
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SettingsFactory.class.getName());
 
 	public static final String DEF_CACHE_REG_FACTORY = NoCachingRegionFactory.class.getName();
 
 	public SettingsFactory() {
 	}
 
 	public Settings buildSettings(Properties props, ServiceRegistry serviceRegistry) {
 		final JdbcServices jdbcServices = serviceRegistry.getService( JdbcServices.class );
 		Settings settings = new Settings();
 
 		//SessionFactory name:
 
 		String sessionFactoryName = props.getProperty(Environment.SESSION_FACTORY_NAME);
 		settings.setSessionFactoryName(sessionFactoryName);
 
 		//JDBC and connection settings:
 
 		//Interrogate JDBC metadata
 		ExtractedDatabaseMetaData meta = jdbcServices.getExtractedMetaDataSupport();
 
 		settings.setDataDefinitionImplicitCommit( meta.doesDataDefinitionCauseTransactionCommit() );
 		settings.setDataDefinitionInTransactionSupported( meta.supportsDataDefinitionInTransaction() );
 
 		//use dialect default properties
 		final Properties properties = new Properties();
 		properties.putAll( jdbcServices.getDialect().getDefaultProperties() );
 		properties.putAll( props );
 
 		// Transaction settings:
 		settings.setJtaPlatform( serviceRegistry.getService( JtaPlatform.class ) );
 
 		boolean flushBeforeCompletion = ConfigurationHelper.getBoolean(Environment.FLUSH_BEFORE_COMPLETION, properties);
         LOG.debugf( "Automatic flush during beforeCompletion(): %s", enabledDisabled(flushBeforeCompletion) );
 		settings.setFlushBeforeCompletionEnabled(flushBeforeCompletion);
 
 		boolean autoCloseSession = ConfigurationHelper.getBoolean(Environment.AUTO_CLOSE_SESSION, properties);
         LOG.debugf( "Automatic session close at end of transaction: %s", enabledDisabled(autoCloseSession) );
 		settings.setAutoCloseSessionEnabled(autoCloseSession);
 
 		//JDBC and connection settings:
 
 		int batchSize = ConfigurationHelper.getInt(Environment.STATEMENT_BATCH_SIZE, properties, 0);
 		if ( !meta.supportsBatchUpdates() ) {
 			batchSize = 0;
 		}
 		if ( batchSize > 0 ) {
 			LOG.debugf( "JDBC batch size: %s", batchSize );
 		}
 		settings.setJdbcBatchSize(batchSize);
 
 		boolean jdbcBatchVersionedData = ConfigurationHelper.getBoolean(Environment.BATCH_VERSIONED_DATA, properties, false);
         if ( batchSize > 0 ) {
 			LOG.debugf( "JDBC batch updates for versioned data: %s", enabledDisabled(jdbcBatchVersionedData) );
 		}
 		settings.setJdbcBatchVersionedData(jdbcBatchVersionedData);
 
 		boolean useScrollableResultSets = ConfigurationHelper.getBoolean(
 				Environment.USE_SCROLLABLE_RESULTSET,
 				properties,
 				meta.supportsScrollableResults()
 		);
         LOG.debugf( "Scrollable result sets: %s", enabledDisabled(useScrollableResultSets) );
 		settings.setScrollableResultSetsEnabled(useScrollableResultSets);
 
 		boolean wrapResultSets = ConfigurationHelper.getBoolean(Environment.WRAP_RESULT_SETS, properties, false);
         LOG.debugf( "Wrap result sets: %s", enabledDisabled(wrapResultSets) );
 		settings.setWrapResultSetsEnabled(wrapResultSets);
 
 		boolean useGetGeneratedKeys = ConfigurationHelper.getBoolean(Environment.USE_GET_GENERATED_KEYS, properties, meta.supportsGetGeneratedKeys());
         LOG.debugf( "JDBC3 getGeneratedKeys(): %s", enabledDisabled(useGetGeneratedKeys) );
 		settings.setGetGeneratedKeysEnabled(useGetGeneratedKeys);
 
 		Integer statementFetchSize = ConfigurationHelper.getInteger(Environment.STATEMENT_FETCH_SIZE, properties);
         if (statementFetchSize != null) {
 			LOG.debugf( "JDBC result set fetch size: %s", statementFetchSize );
 		}
 		settings.setJdbcFetchSize(statementFetchSize);
 
 		String releaseModeName = ConfigurationHelper.getString( Environment.RELEASE_CONNECTIONS, properties, "auto" );
         LOG.debugf( "Connection release mode: %s", releaseModeName );
 		ConnectionReleaseMode releaseMode;
 		if ( "auto".equals(releaseModeName) ) {
 			releaseMode = serviceRegistry.getService( TransactionFactory.class ).getDefaultReleaseMode();
 		}
 		else {
 			releaseMode = ConnectionReleaseMode.parse( releaseModeName );
 			if ( releaseMode == ConnectionReleaseMode.AFTER_STATEMENT &&
 					! jdbcServices.getConnectionProvider().supportsAggressiveRelease() ) {
                 LOG.unsupportedAfterStatement();
 				releaseMode = ConnectionReleaseMode.AFTER_TRANSACTION;
 			}
 		}
 		settings.setConnectionReleaseMode( releaseMode );
 
 		//SQL Generation settings:
 
 		String defaultSchema = properties.getProperty( Environment.DEFAULT_SCHEMA );
 		String defaultCatalog = properties.getProperty( Environment.DEFAULT_CATALOG );
         if ( defaultSchema != null ) {
 			LOG.debugf( "Default schema: %s", defaultSchema );
 		}
         if (defaultCatalog != null) {
 			LOG.debugf( "Default catalog: %s", defaultCatalog );
 		}
 		settings.setDefaultSchemaName( defaultSchema );
 		settings.setDefaultCatalogName( defaultCatalog );
 
 		Integer maxFetchDepth = ConfigurationHelper.getInteger( Environment.MAX_FETCH_DEPTH, properties );
         if ( maxFetchDepth != null ) {
 			LOG.debugf( "Maximum outer join fetch depth: %s", maxFetchDepth );
 		}
 		settings.setMaximumFetchDepth( maxFetchDepth );
 
 		int batchFetchSize = ConfigurationHelper.getInt(Environment.DEFAULT_BATCH_FETCH_SIZE, properties, 1);
         LOG.debugf( "Default batch fetch size: %s", batchFetchSize );
 		settings.setDefaultBatchFetchSize( batchFetchSize );
 
 		boolean comments = ConfigurationHelper.getBoolean( Environment.USE_SQL_COMMENTS, properties );
         LOG.debugf( "Generate SQL with comments: %s", enabledDisabled(comments) );
 		settings.setCommentsEnabled( comments );
 
 		boolean orderUpdates = ConfigurationHelper.getBoolean( Environment.ORDER_UPDATES, properties );
         LOG.debugf( "Order SQL updates by primary key: %s", enabledDisabled(orderUpdates) );
 		settings.setOrderUpdatesEnabled( orderUpdates );
 
 		boolean orderInserts = ConfigurationHelper.getBoolean(Environment.ORDER_INSERTS, properties);
         LOG.debugf( "Order SQL inserts for batching: %s", enabledDisabled(orderInserts) );
 		settings.setOrderInsertsEnabled( orderInserts );
 
 		//Query parser settings:
 
 		settings.setQueryTranslatorFactory( createQueryTranslatorFactory( properties, serviceRegistry ) );
 
         Map querySubstitutions = ConfigurationHelper.toMap( Environment.QUERY_SUBSTITUTIONS, " ,=;:\n\t\r\f", properties );
         LOG.debugf( "Query language substitutions: %s", querySubstitutions );
 		settings.setQuerySubstitutions( querySubstitutions );
 
 		boolean jpaqlCompliance = ConfigurationHelper.getBoolean( Environment.JPAQL_STRICT_COMPLIANCE, properties, false );
 		LOG.debugf( "JPA-QL strict compliance: %s", enabledDisabled(jpaqlCompliance) );
 		settings.setStrictJPAQLCompliance( jpaqlCompliance );
 
 		// Second-level / query cache:
 
 		boolean useSecondLevelCache = ConfigurationHelper.getBoolean( Environment.USE_SECOND_LEVEL_CACHE, properties, true );
         LOG.debugf( "Second-level cache: %s", enabledDisabled(useSecondLevelCache) );
 		settings.setSecondLevelCacheEnabled( useSecondLevelCache );
 
 		boolean useQueryCache = ConfigurationHelper.getBoolean(Environment.USE_QUERY_CACHE, properties);
         LOG.debugf( "Query cache: %s", enabledDisabled(useQueryCache) );
 		settings.setQueryCacheEnabled( useQueryCache );
 		if (useQueryCache) {
 			settings.setQueryCacheFactory( createQueryCacheFactory( properties, serviceRegistry ) );
 		}
 
 		// The cache provider is needed when we either have second-level cache enabled
 		// or query cache enabled.  Note that useSecondLevelCache is enabled by default
 		settings.setRegionFactory( createRegionFactory( properties, ( useSecondLevelCache || useQueryCache ), serviceRegistry ) );
 
 		boolean useMinimalPuts = ConfigurationHelper.getBoolean(
 				Environment.USE_MINIMAL_PUTS, properties, settings.getRegionFactory().isMinimalPutsEnabledByDefault()
 		);
         LOG.debugf( "Optimize cache for minimal puts: %s", enabledDisabled(useMinimalPuts) );
 		settings.setMinimalPutsEnabled( useMinimalPuts );
 
 		String prefix = properties.getProperty( Environment.CACHE_REGION_PREFIX );
 		if ( StringHelper.isEmpty(prefix) ) {
 			prefix=null;
 		}
         if (prefix != null) {
 			LOG.debugf( "Cache region prefix: %s", prefix );
 		}
 		settings.setCacheRegionPrefix( prefix );
 
 		boolean useStructuredCacheEntries = ConfigurationHelper.getBoolean( Environment.USE_STRUCTURED_CACHE, properties, false );
         LOG.debugf( "Structured second-level cache entries: %s", enabledDisabled(useStructuredCacheEntries) );
 		settings.setStructuredCacheEntriesEnabled( useStructuredCacheEntries );
 
 
 		//Statistics and logging:
 
 		boolean useStatistics = ConfigurationHelper.getBoolean( Environment.GENERATE_STATISTICS, properties );
 		LOG.debugf( "Statistics: %s", enabledDisabled(useStatistics) );
 		settings.setStatisticsEnabled( useStatistics );
 
 		boolean useIdentifierRollback = ConfigurationHelper.getBoolean( Environment.USE_IDENTIFIER_ROLLBACK, properties );
         LOG.debugf( "Deleted entity synthetic identifier rollback: %s", enabledDisabled(useIdentifierRollback) );
 		settings.setIdentifierRollbackEnabled( useIdentifierRollback );
 
 		//Schema export:
 
 		String autoSchemaExport = properties.getProperty( Environment.HBM2DDL_AUTO );
 		if ( "validate".equals(autoSchemaExport) ) {
 			settings.setAutoValidateSchema( true );
 		}
 		if ( "update".equals(autoSchemaExport) ) {
 			settings.setAutoUpdateSchema( true );
 		}
 		if ( "create".equals(autoSchemaExport) ) {
 			settings.setAutoCreateSchema( true );
 		}
 		if ( "create-drop".equals( autoSchemaExport ) ) {
 			settings.setAutoCreateSchema( true );
 			settings.setAutoDropSchema( true );
 		}
 		settings.setImportFiles( properties.getProperty( Environment.HBM2DDL_IMPORT_FILES ) );
 
 		EntityMode defaultEntityMode = EntityMode.parse( properties.getProperty( Environment.DEFAULT_ENTITY_MODE ) );
         LOG.debugf( "Default entity-mode: %s", defaultEntityMode );
 		settings.setDefaultEntityMode( defaultEntityMode );
 
 		boolean namedQueryChecking = ConfigurationHelper.getBoolean( Environment.QUERY_STARTUP_CHECKING, properties, true );
         LOG.debugf( "Named query checking : %s", enabledDisabled(namedQueryChecking) );
 		settings.setNamedQueryStartupCheckingEnabled( namedQueryChecking );
 
 		boolean checkNullability = ConfigurationHelper.getBoolean(Environment.CHECK_NULLABILITY, properties, true);
         LOG.debugf( "Check Nullability in Core (should be disabled when Bean Validation is on): %s", enabledDisabled(checkNullability) );
 		settings.setCheckNullability(checkNullability);
 
 		MultiTenancyStrategy multiTenancyStrategy = MultiTenancyStrategy.determineMultiTenancyStrategy( properties );
 		LOG.debugf( "multi-tenancy strategy : %s", multiTenancyStrategy );
 		settings.setMultiTenancyStrategy( multiTenancyStrategy );
 
 		// TODO: Does EntityTuplizerFactory really need to be configurable? revisit for HHH-6383
 		settings.setEntityTuplizerFactory( new EntityTuplizerFactory() );
 
 //		String provider = properties.getProperty( Environment.BYTECODE_PROVIDER );
 //		log.info( "Bytecode provider name : " + provider );
 //		BytecodeProvider bytecodeProvider = buildBytecodeProvider( provider );
 //		settings.setBytecodeProvider( bytecodeProvider );
 
 		return settings;
 
 	}
 
 //	protected BytecodeProvider buildBytecodeProvider(String providerName) {
 //		if ( "javassist".equals( providerName ) ) {
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //		else {
 //            LOG.debugf("Using javassist as bytecode provider by default");
 //			return new org.hibernate.bytecode.internal.javassist.BytecodeProviderImpl();
 //		}
 //	}
 
 	private static String enabledDisabled(boolean value) {
 		return value ? "enabled" : "disabled";
 	}
 
 	protected QueryCacheFactory createQueryCacheFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String queryCacheFactoryClassName = ConfigurationHelper.getString(
 				Environment.QUERY_CACHE_FACTORY, properties, StandardQueryCacheFactory.class.getName()
 		);
         LOG.debugf( "Query cache factory: %s", queryCacheFactoryClassName );
 		try {
 			return (QueryCacheFactory) serviceRegistry.getService( ClassLoaderService.class )
 					.classForName( queryCacheFactoryClassName )
 					.newInstance();
 		}
 		catch (Exception e) {
 			throw new HibernateException( "could not instantiate QueryCacheFactory: " + queryCacheFactoryClassName, e );
 		}
 	}
 
 	private static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled, ServiceRegistry serviceRegistry) {
 		String regionFactoryClassName = ConfigurationHelper.getString(
 				Environment.CACHE_REGION_FACTORY, properties, null
 		);
 		if ( regionFactoryClassName == null || !cachingEnabled) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
         LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
 						.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException e ) {
 				// no constructor accepting Properties found, try no arg constructor
                 LOG.debugf(
 						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
 						regionFactoryClassName
 				);
 				return (RegionFactory) serviceRegistry.getService( ClassLoaderService.class )
 						.classForName( regionFactoryClassName )
 						.newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 	//todo remove this once we move to new metamodel
 	public static RegionFactory createRegionFactory(Properties properties, boolean cachingEnabled) {
-		// todo : REMOVE!  THIS IS TOTALLY A TEMPORARY HACK FOR org.hibernate.cfg.AnnotationBinder which will be going away
+		// todo : REMOVE!  THIS IS TOTALLY A TEMPORARY HACK FOR org.hibernate.cfg.AnnotationSourceProcessor which will be going away
 		String regionFactoryClassName = ConfigurationHelper.getString(
 				Environment.CACHE_REGION_FACTORY, properties, null
 		);
 		if ( regionFactoryClassName == null ) {
 			regionFactoryClassName = DEF_CACHE_REG_FACTORY;
 		}
         LOG.debugf( "Cache region factory : %s", regionFactoryClassName );
 		try {
 			try {
 				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
 						.getConstructor( Properties.class )
 						.newInstance( properties );
 			}
 			catch ( NoSuchMethodException e ) {
 				// no constructor accepting Properties found, try no arg constructor
                 LOG.debugf(
 						"%s did not provide constructor accepting java.util.Properties; attempting no-arg constructor.",
 						regionFactoryClassName
 				);
 				return (RegionFactory) org.hibernate.internal.util.ReflectHelper.classForName( regionFactoryClassName )
 						.newInstance();
 			}
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate RegionFactory [" + regionFactoryClassName + "]", e );
 		}
 	}
 
 	protected QueryTranslatorFactory createQueryTranslatorFactory(Properties properties, ServiceRegistry serviceRegistry) {
 		String className = ConfigurationHelper.getString(
 				Environment.QUERY_TRANSLATOR, properties, "org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory"
 		);
 		LOG.debugf( "Query translator: %s", className );
 		try {
 			return (QueryTranslatorFactory) serviceRegistry.getService( ClassLoaderService.class )
 					.classForName( className )
 					.newInstance();
 		}
 		catch ( Exception e ) {
 			throw new HibernateException( "could not instantiate QueryTranslatorFactory: " + className, e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/ToOneFkSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/ToOneFkSecondPass.java
index eec7f0a9ac..2696736bb8 100644
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
-			//embedded property starts their path with 'id.' See PropertyPreloadedData( ) use when idClass != null in AnnotationBinder
+			//embedded property starts their path with 'id.' See PropertyPreloadedData( ) use when idClass != null in AnnotationSourceProcessor
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
-			 * HbmBinder does this only when property-ref != null, but IMO, it makes sense event if it is null
+			 * HbmSourceProcessor does this only when property-ref != null, but IMO, it makes sense event if it is null
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
index b0cf0ec6a6..af78201e1a 100644
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
-        LOG.debugf("Binder property %s with lazy=%s", name, lazy);
+        LOG.debugf("SourceProcessor property %s with lazy=%s", name, lazy);
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
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
index 24f61afd84..a11edaed74 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/CoreMessageLogger.java
@@ -1,1096 +1,1092 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2007-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.internal;
 
 import java.io.File;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.lang.reflect.Method;
 import java.net.URL;
 import java.sql.SQLException;
 import java.sql.SQLWarning;
 import java.util.Hashtable;
 import java.util.Properties;
 import java.util.Set;
 import javax.naming.NameNotFoundException;
 import javax.naming.NamingException;
 import javax.transaction.Synchronization;
 import javax.transaction.SystemException;
 
 import org.jboss.logging.BasicLogger;
 import org.jboss.logging.Cause;
 import org.jboss.logging.LogMessage;
 import org.jboss.logging.Message;
 import org.jboss.logging.MessageLogger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cache.CacheException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.loading.internal.CollectionLoadContext;
 import org.hibernate.engine.loading.internal.EntityLoadContext;
 import org.hibernate.engine.spi.CollectionKey;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.id.IntegralDataTypeHolder;
 import org.hibernate.service.jdbc.dialect.internal.AbstractDialectResolver;
 import org.hibernate.service.jndi.JndiException;
 import org.hibernate.service.jndi.JndiNameException;
 import org.hibernate.type.BasicType;
 import org.hibernate.type.SerializationException;
 import org.hibernate.type.Type;
 
 import static org.jboss.logging.Logger.Level.DEBUG;
 import static org.jboss.logging.Logger.Level.ERROR;
 import static org.jboss.logging.Logger.Level.INFO;
 import static org.jboss.logging.Logger.Level.WARN;
 
 /**
  * The jboss-logging {@link MessageLogger} for the hibernate-core module.  It reserves message ids ranging from
  * 00001 to 10000 inclusively.
  * <p/>
  * New messages must be added after the last message defined to ensure message codes are unique.
  */
 @MessageLogger(projectCode = "HHH")
 public interface CoreMessageLogger extends BasicLogger {
 
 	@LogMessage(level = WARN)
 	@Message(value = "Already session bound on call to bind(); make sure you clean up your sessions!", id = 2)
 	void alreadySessionBound();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Autocommit mode: %s", id = 6)
 	void autoCommitMode(boolean autocommit);
 
 	@LogMessage(level = WARN)
 	@Message(value = "JTASessionContext being used with JDBCTransactionFactory; auto-flush will not operate correctly with getCurrentSession()",
 			id = 8)
 	void autoFlushWillNotWork();
 
 	@LogMessage(level = INFO)
 	@Message(value = "On release of batch it still contained JDBC statements", id = 10)
 	void batchContainedStatementsOnRelease();
 
-	@LogMessage(level = DEBUG)
-	@Message(value = "Binding entity from annotated class: %s", id = 15)
-	void bindingEntityFromAnnotatedClass(String className);
-
 	@LogMessage(level = INFO)
 	@Message(value = "Bytecode provider name : %s", id = 21)
 	void bytecodeProvider(String provider);
 
 	@LogMessage(level = WARN)
 	@Message(value = "c3p0 properties were encountered, but the %s provider class was not found on the classpath; these properties are going to be ignored.",
 			id = 22)
 	void c3p0ProviderClassNotFound(String c3p0ProviderClassName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "I/O reported cached file could not be found : %s : %s", id = 23)
 	void cachedFileNotFound(String path,
 							FileNotFoundException error);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Cache provider: %s", id = 24)
 	void cacheProvider(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Calling joinTransaction() on a non JTA EntityManager", id = 27)
 	void callingJoinTransactionOnNonJtaEntityManager();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Cleaning up connection pool [%s]", id = 30)
 	void cleaningUpConnectionPool(String url);
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "Closing", id = 31)
 	void closing();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Collections fetched (minimize this): %s", id = 32)
 	void collectionsFetched(long collectionFetchCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Collections loaded: %s", id = 33)
 	void collectionsLoaded(long collectionLoadCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Collections recreated: %s", id = 34)
 	void collectionsRecreated(long collectionRecreateCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Collections removed: %s", id = 35)
 	void collectionsRemoved(long collectionRemoveCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Collections updated: %s", id = 36)
 	void collectionsUpdated(long collectionUpdateCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Columns: %s", id = 37)
 	void columns(Set keySet);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Composite-id class does not override equals(): %s", id = 38)
 	void compositeIdClassDoesNotOverrideEquals(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Composite-id class does not override hashCode(): %s", id = 39)
 	void compositeIdClassDoesNotOverrideHashCode(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Configuration resource: %s", id = 40)
 	void configurationResource(String resource);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Configured SessionFactory: %s", id = 41)
 	void configuredSessionFactory(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Configuring from file: %s", id = 42)
 	void configuringFromFile(String file);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Configuring from resource: %s", id = 43)
 	void configuringFromResource(String resource);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Configuring from URL: %s", id = 44)
 	void configuringFromUrl(URL url);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Configuring from XML document", id = 45)
 	void configuringFromXmlDocument();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Connection properties: %s", id = 46)
 	void connectionProperties(Properties connectionProps);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Connections obtained: %s", id = 48)
 	void connectionsObtained(long connectCount);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Container is providing a null PersistenceUnitRootUrl: discovery impossible", id = 50)
 	void containerProvidingNullPersistenceUnitRootUrl();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Ignoring bag join fetch [%s] due to prior collection join fetch", id = 51)
 	void containsJoinFetchedCollection(String role);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Creating subcontext: %s", id = 53)
 	void creatingSubcontextInfo(String intermediateContextName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Defining %s=true ignored in HEM", id = 59)
 	void definingFlushBeforeCompletionIgnoredInHem(String flushBeforeCompletion);
 
 	@LogMessage(level = WARN)
 	@Message(value = "@ForceDiscriminator is deprecated use @DiscriminatorOptions instead.", id = 62)
 	void deprecatedForceDescriminatorAnnotation();
 
 	@LogMessage(level = WARN)
 	@Message(value = "The Oracle9Dialect dialect has been deprecated; use either Oracle9iDialect or Oracle10gDialect instead",
 			id = 63)
 	void deprecatedOracle9Dialect();
 
 	@LogMessage(level = WARN)
 	@Message(value = "The OracleDialect dialect has been deprecated; use Oracle8iDialect instead", id = 64)
 	void deprecatedOracleDialect();
 
 	@LogMessage(level = WARN)
 	@Message(value = "DEPRECATED : use {} instead with custom {} implementation", id = 65)
 	void deprecatedUuidGenerator(String name,
 								 String name2);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Disallowing insert statement comment for select-identity due to Oracle driver bug", id = 67)
 	void disallowingInsertStatementComment();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Duplicate generator name %s", id = 69)
 	void duplicateGeneratorName(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Duplicate generator table: %s", id = 70)
 	void duplicateGeneratorTable(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Duplicate import: %s -> %s", id = 71)
 	void duplicateImport(String entityName,
 						 String rename);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Duplicate joins for class: %s", id = 72)
 	void duplicateJoins(String entityName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "entity-listener duplication, first event definition will be used: %s", id = 73)
 	void duplicateListener(String className);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Found more than one <persistence-unit-metadata>, subsequent ignored", id = 74)
 	void duplicateMetadata();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Entities deleted: %s", id = 76)
 	void entitiesDeleted(long entityDeleteCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Entities fetched (minimize this): %s", id = 77)
 	void entitiesFetched(long entityFetchCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Entities inserted: %s", id = 78)
 	void entitiesInserted(long entityInsertCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Entities loaded: %s", id = 79)
 	void entitiesLoaded(long entityLoadCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Entities updated: %s", id = 80)
 	void entitiesUpdated(long entityUpdateCount);
 
 	@LogMessage(level = WARN)
 	@Message(value = "@org.hibernate.annotations.Entity used on a non root entity: ignored for %s", id = 81)
 	void entityAnnotationOnNonRoot(String className);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Entity Manager closed by someone else (%s must not be used)", id = 82)
 	void entityManagerClosedBySomeoneElse(String autoCloseSession);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Entity [%s] is abstract-class/interface explicitly mapped as non-abstract; be sure to supply entity-names",
 			id = 84)
 	void entityMappedAsNonAbstract(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "%s %s found", id = 85)
 	void exceptionHeaderFound(String exceptionHeader,
 							  String metaInfOrmXml);
 
 	@LogMessage(level = INFO)
 	@Message(value = "%s No %s found", id = 86)
 	void exceptionHeaderNotFound(String exceptionHeader,
 								 String metaInfOrmXml);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Exception in interceptor afterTransactionCompletion()", id = 87)
 	void exceptionInAfterTransactionCompletionInterceptor(@Cause Throwable e);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Exception in interceptor beforeTransactionCompletion()", id = 88)
 	void exceptionInBeforeTransactionCompletionInterceptor(@Cause Throwable e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Sub-resolver threw unexpected exception, continuing to next : %s", id = 89)
 	void exceptionInSubResolver(String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Expected type: %s, actual value: %s", id = 91)
 	void expectedType(String name,
 					  String string);
 
 	@LogMessage(level = WARN)
 	@Message(value = "An item was expired by the cache while it was locked (increase your cache timeout): %s", id = 92)
 	void expired(Object key);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Bound factory to JNDI name: %s", id = 94)
 	void factoryBoundToJndiName(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "A factory was renamed from [%s] to [%s] in JNDI", id = 96)
 	void factoryJndiRename(String oldName, String newName);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Unbound factory from JNDI name: %s", id = 97)
 	void factoryUnboundFromJndiName(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "A factory was unbound from name: %s", id = 98)
 	void factoryUnboundFromName(String name);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "an assertion failure occured" + " (this may indicate a bug in Hibernate, but is more likely due"
 			+ " to unsafe use of the session): %s", id = 99)
 	void failed(Throwable throwable);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Fail-safe cleanup (collections) : %s", id = 100)
 	void failSafeCollectionsCleanup(CollectionLoadContext collectionLoadContext);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Fail-safe cleanup (entities) : %s", id = 101)
 	void failSafeEntitiesCleanup(EntityLoadContext entityLoadContext);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Fetching database metadata", id = 102)
 	void fetchingDatabaseMetadata();
 
 	@LogMessage(level = WARN)
 	@Message(value = "@Filter not allowed on subclasses (ignored): %s", id = 103)
 	void filterAnnotationOnSubclass(String className);
 
 	@LogMessage(level = WARN)
 	@Message(value = "firstResult/maxResults specified with collection fetch; applying in memory!", id = 104)
 	void firstOrMaxResultsSpecifiedWithCollectionFetch();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Flushes: %s", id = 105)
 	void flushes(long flushCount);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Forcing container resource cleanup on transaction completion", id = 106)
 	void forcingContainerResourceCleanup();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Forcing table use for sequence-style generator due to pooled optimizer selection where db does not support pooled sequences",
 			id = 107)
 	void forcingTableUse();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Foreign keys: %s", id = 108)
 	void foreignKeys(Set keySet);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Found mapping document in jar: %s", id = 109)
 	void foundMappingDocument(String name);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Getters of lazy classes cannot be final: %s.%s", id = 112)
 	void gettersOfLazyClassesCannotBeFinal(String entityName,
 										   String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "GUID identifier generated: %s", id = 113)
 	void guidGenerated(String result);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Handling transient entity in delete processing", id = 114)
 	void handlingTransientEntity();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Hibernate connection pool size: %s", id = 115)
 	void hibernateConnectionPoolSize(int poolSize);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Config specified explicit optimizer of [%s], but [%s=%s; honoring optimizer setting", id = 116)
 	void honoringOptimizerSetting(String none,
 								  String incrementParam,
 								  int incrementSize);
 
 	@LogMessage(level = INFO)
 	@Message(value = "HQL: %s, time: %sms, rows: %s", id = 117)
 	void hql(String hql,
 			 Long valueOf,
 			 Long valueOf2);
 
 	@LogMessage(level = WARN)
 	@Message(value = "HSQLDB supports only READ_UNCOMMITTED isolation", id = 118)
 	void hsqldbSupportsOnlyReadCommittedIsolation();
 
 	@LogMessage(level = WARN)
 	@Message(value = "On EntityLoadContext#clear, hydratingEntities contained [%s] entries", id = 119)
 	void hydratingEntitiesCount(int size);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Ignoring unique constraints specified on table generator [%s]", id = 120)
 	void ignoringTableGeneratorConstraints(String name);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Ignoring unrecognized query hint [%s]", id = 121)
 	void ignoringUnrecognizedQueryHint(String hintName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "IllegalArgumentException in class: %s, getter method of property: %s", id = 122)
 	void illegalPropertyGetterArgument(String name,
 									   String propertyName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "IllegalArgumentException in class: %s, setter method of property: %s", id = 123)
 	void illegalPropertySetterArgument(String name,
 									   String propertyName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "@Immutable used on a non root entity: ignored for %s", id = 124)
 	void immutableAnnotationOnNonRoot(String className);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Mapping metadata cache was not completely processed", id = 125)
 	void incompleteMappingMetadataCacheProcessing();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Indexes: %s", id = 126)
 	void indexes(Set keySet);
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "Could not bind JNDI listener", id = 127)
 	void couldNotBindJndiListener();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Instantiating explicit connection provider: %s", id = 130)
 	void instantiatingExplicitConnectionProvider(String providerClassName);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Array element type error\n%s", id = 132)
 	void invalidArrayElementType(String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Discriminator column has to be defined in the root entity, it will be ignored in subclass: %s",
 			id = 133)
 	void invalidDiscriminatorAnnotation(String className);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Application attempted to edit read only item: %s", id = 134)
 	void invalidEditOfReadOnlyItem(Object key);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Invalid JNDI name: %s", id = 135)
 	void invalidJndiName(String name,
 						 @Cause JndiNameException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Inapropriate use of @OnDelete on entity, annotation ignored: %s", id = 136)
 	void invalidOnDeleteAnnotation(String entityName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Root entity should not hold an PrimaryKeyJoinColum(s), will be ignored", id = 137)
 	void invalidPrimaryKeyJoinColumnAnnotation();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Mixing inheritance strategy in a entity hierarchy is not allowed, ignoring sub strategy in: %s",
 			id = 138)
 	void invalidSubStrategy(String className);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Illegal use of @Table in a subclass of a SINGLE_TABLE hierarchy: %s", id = 139)
 	void invalidTableAnnotation(String className);
 
 	@LogMessage(level = INFO)
 	@Message(value = "JACC contextID: %s", id = 140)
 	void jaccContextId(String contextId);
 
 	@LogMessage(level = INFO)
 	@Message(value = "java.sql.Types mapped the same code [%s] multiple times; was [%s]; now [%s]", id = 141)
 	void JavaSqlTypesMappedSameCodeMultipleTimes(int code,
 												 String old,
 												 String name);
 
 	@Message(value = "Javassist Enhancement failed: %s", id = 142)
 	String javassistEnhancementFailed(String entityName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "%s = false breaks the EJB3 specification", id = 144)
 	void jdbcAutoCommitFalseBreaksEjb3Spec(String autocommit);
 
 	@LogMessage(level = WARN)
 	@Message(value = "No JDBC Driver class was specified by property %s", id = 148)
 	void jdbcDriverNotSpecified(String driver);
 
 	@LogMessage(level = INFO)
 	@Message(value = "JDBC isolation level: %s", id = 149)
 	void jdbcIsolationLevel(String isolationLevelToString);
 
 	@Message(value = "JDBC rollback failed", id = 151)
 	String jdbcRollbackFailed();
 
 	@Message(value = "JDBC URL was not specified by property %s", id = 152)
 	String jdbcUrlNotSpecified(String url);
 
 	@LogMessage(level = INFO)
 	@Message(value = "JNDI InitialContext properties:%s", id = 154)
 	void jndiInitialContextProperties(Hashtable hash);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "JNDI name %s does not handle a session factory reference", id = 155)
 	void jndiNameDoesNotHandleSessionFactoryReference(String sfJNDIName,
 													  @Cause ClassCastException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Lazy property fetching available for: %s", id = 157)
 	void lazyPropertyFetchingAvailable(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "In CollectionLoadContext#endLoadingCollections, localLoadingCollectionKeys contained [%s], but no LoadingCollectionEntry was found in loadContexts",
 			id = 159)
 	void loadingCollectionKeyNotFound(CollectionKey collectionKey);
 
 	@LogMessage(level = WARN)
 	@Message(value = "On CollectionLoadContext#cleanup, localLoadingCollectionKeys contained [%s] entries", id = 160)
 	void localLoadingCollectionKeysCount(int size);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Logging statistics....", id = 161)
 	void loggingStatistics();
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "*** Logical connection closed ***", id = 162)
 	void logicalConnectionClosed();
 
 	@LogMessage(level = DEBUG)
 	@Message(value = "Logical connection releasing its physical connection", id = 163)
 	void logicalConnectionReleasingPhysicalConnection();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Max query time: %sms", id = 173)
 	void maxQueryTime(long queryExecutionMaxTime);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Function template anticipated %s arguments, but %s arguments encountered", id = 174)
 	void missingArguments(int anticipatedNumberOfArguments,
 						  int numberOfArguments);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Class annotated @org.hibernate.annotations.Entity but not javax.persistence.Entity (most likely a user error): %s",
 			id = 175)
 	void missingEntityAnnotation(String className);
 
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error in named query: %s", id = 177)
 	void namedQueryError(String queryName,
 						 @Cause HibernateException e);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Naming exception occurred accessing factory: %s", id = 178)
 	void namingExceptionAccessingFactory(NamingException exception);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Narrowing proxy to %s - this operation breaks ==", id = 179)
 	void narrowingProxy(Class concreteProxyClass);
 
 	@LogMessage(level = WARN)
 	@Message(value = "FirstResult/maxResults specified on polymorphic query; applying in memory!", id = 180)
 	void needsLimit();
 
 	@LogMessage(level = WARN)
 	@Message(value = "No appropriate connection provider encountered, assuming application will be supplying connections",
 			id = 181)
 	void noAppropriateConnectionProvider();
 
 	@LogMessage(level = INFO)
 	@Message(value = "No default (no-argument) constructor for class: %s (class must be instantiated by Interceptor)",
 			id = 182)
 	void noDefaultConstructor(String name);
 
 	@LogMessage(level = WARN)
 	@Message(value = "no persistent classes found for query class: %s", id = 183)
 	void noPersistentClassesFound(String query);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "No session factory with JNDI name %s", id = 184)
 	void noSessionFactoryWithJndiName(String sfJNDIName,
 									  @Cause NameNotFoundException e);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Optimistic lock failures: %s", id = 187)
 	void optimisticLockFailures(long optimisticFailureCount);
 
 	@LogMessage(level = WARN)
 	@Message(value = "@OrderBy not allowed for an indexed collection, annotation ignored.", id = 189)
 	void orderByAnnotationIndexedCollection();
 
 	@LogMessage(level = WARN)
 	@Message(value = "Overriding %s is dangerous, this might break the EJB3 specification implementation", id = 193)
 	void overridingTransactionStrategyDangerous(String transactionStrategy);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Package not found or wo package-info.java: %s", id = 194)
 	void packageNotFound(String packageName);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Parameter position [%s] occurred as both JPA and Hibernate positional parameter", id = 195)
 	void parameterPositionOccurredAsBothJpaAndHibernatePositionalParameter(Integer position);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error parsing XML (%s) : %s", id = 196)
 	void parsingXmlError(int lineNumber,
 						 String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Error parsing XML: %s(%s) %s", id = 197)
 	void parsingXmlErrorForFile(String file,
 								int lineNumber,
 								String message);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Warning parsing XML (%s) : %s", id = 198)
 	void parsingXmlWarning(int lineNumber,
 						   String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Warning parsing XML: %s(%s) %s", id = 199)
 	void parsingXmlWarningForFile(String file,
 								  int lineNumber,
 								  String message);
 
 	@LogMessage(level = WARN)
 	@Message(value = "Persistence provider caller does not implement the EJB3 spec correctly."
 			+ "PersistenceUnitInfo.getNewTempClassLoader() is null.", id = 200)
 	void persistenceProviderCallerDoesNotImplementEjb3SpecCorrectly();
 
 	@LogMessage(level = INFO)
 	@Message(value = "Pooled optimizer source reported [%s] as the initial value; use of 1 or greater highly recommended",
 			id = 201)
 	void pooledOptimizerReportedInitialValue(IntegralDataTypeHolder value);
 
 	@LogMessage(level = ERROR)
 	@Message(value = "PreparedStatement was already in the batch, [%s].", id = 202)
 	void preparedStatementAlreadyInBatch(String sql);
 
 	@LogMessage(level = WARN)
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
 
 	@LogMessage(level = ERROR)
 	@Message(value = "SQLException escaped proxy", id = 246)
 	void sqlExceptionEscapedProxy(@Cause SQLException e);
 
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
 	void unableToExecuteResolver(AbstractDialectResolver abstractDialectResolver,
 								 String message);
 
 	@LogMessage(level = INFO)
 	@Message(value = "Could not find any META-INF/persistence.xml file in the classpath", id = 318)
 	void unableToFindPersistenceXmlInClasspath();
 
 	@LogMessage(level = ERROR)
 	@Message(value = "Could not get database metadata", id = 319)
 	void unableToGetDatabaseMetadata(@Cause SQLException e);
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/EntityBinder.java
new file mode 100644
index 0000000000..9df240b1be
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/EntityBinder.java
@@ -0,0 +1,128 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
+package org.hibernate.metamodel.binder;
+
+import java.util.Map;
+
+import org.hibernate.EntityMode;
+import org.hibernate.metamodel.binder.view.EntityView;
+import org.hibernate.metamodel.binder.view.TableView;
+import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.binding.InheritanceType;
+import org.hibernate.metamodel.domain.Entity;
+import org.hibernate.metamodel.domain.JavaType;
+import org.hibernate.metamodel.relational.Identifier;
+import org.hibernate.metamodel.relational.Schema;
+import org.hibernate.metamodel.relational.Table;
+import org.hibernate.metamodel.source.MappingException;
+import org.hibernate.metamodel.source.spi.BindingContext;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EntityBinder {
+	private final BindingContext bindingContext;
+
+	public EntityBinder(BindingContext bindingContext) {
+		this.bindingContext = bindingContext;
+	}
+
+	public EntityBinding createEntityBinding(EntityView entityView) {
+		final EntityBinding entityBinding = new EntityBinding();
+
+		// todo : Entity will need both entityName and className to be effective
+		final Entity entity = new Entity( entityView.getEntityName(), entityView.getSuperType(), bindingContext.makeJavaType( entityView.getClassName() ) );
+		entityBinding.setEntity( entity );
+
+		final TableView baseTableView = entityView.getBaseTable();
+
+		final String schemaName = baseTableView.getExplicitSchemaName() == null
+				? bindingContext.getMappingDefaults().getSchemaName()
+				: baseTableView.getExplicitSchemaName();
+		final String catalogName = baseTableView.getExplicitCatalogName() == null
+				? bindingContext.getMappingDefaults().getCatalogName()
+				: baseTableView.getExplicitCatalogName();
+		final Schema.Name fullSchemaName = new Schema.Name( schemaName, catalogName );
+		final Schema schema = bindingContext.getMetadataImplementor().getDatabase().getSchema( fullSchemaName );
+		final Identifier tableName = Identifier.toIdentifier( baseTableView.getTableName() );
+		final Table baseTable = schema.locateOrCreateTable( tableName );
+		entityBinding.setBaseTable( baseTable );
+
+		// inheritance
+		if ( entityView.getEntityInheritanceType() != InheritanceType.NO_INHERITANCE ) {
+			// if there is any inheritance strategy, there has to be a super type.
+			if ( entityView.getSuperType() == null ) {
+				throw new MappingException( "Encountered inheritance strategy, but no super type", entityView.getOrigin() );
+			}
+		}
+		entityBinding.setRoot( entityView.isRoot() );
+		entityBinding.setInheritanceType( entityView.getEntityInheritanceType() );
+
+		entityBinding.setJpaEntityName( entityView.getJpaEntityName() );
+		entityBinding.setEntityMode( entityView.getEntityMode() );
+
+		if ( entityView.getEntityMode() == EntityMode.POJO ) {
+			if ( entityView.getProxyInterfaceName() != null ) {
+				entityBinding.setProxyInterfaceType( bindingContext.makeJavaType( entityView.getProxyInterfaceName() ) );
+				entityBinding.setLazy( true );
+			}
+			else if ( entityView.isLazy() ) {
+				entityBinding.setProxyInterfaceType( entity.getJavaType() );
+				entityBinding.setLazy( true );
+			}
+		}
+		else {
+			entityBinding.setProxyInterfaceType( new JavaType( Map.class ) );
+			entityBinding.setLazy( entityView.isLazy() );
+		}
+
+		entityBinding.setCustomEntityTuplizerClass( entityView.getCustomEntityTuplizerClass() );
+		entityBinding.setCustomEntityPersisterClass( entityView.getCustomEntityPersisterClass() );
+
+		entityBinding.setCaching( entityView.getCaching() );
+		entityBinding.setMetaAttributeContext( entityView.getMetaAttributeContext() );
+
+		entityBinding.setMutable( entityView.isMutable() );
+		entityBinding.setExplicitPolymorphism( entityView.isExplicitPolymorphism() );
+		entityBinding.setWhereFilter( entityView.getWhereFilter() );
+		entityBinding.setRowId( entityView.getRowId() );
+		entityBinding.setDynamicUpdate( entityView.isDynamicUpdate() );
+		entityBinding.setDynamicInsert( entityView.isDynamicInsert() );
+		entityBinding.setBatchSize( entityView.getBatchSize() );
+		entityBinding.setSelectBeforeUpdate( entityView.isSelectBeforeUpdate() );
+		entityBinding.setOptimisticLockMode( entityView.getOptimisticLockMode() );
+		entityBinding.setAbstract( entityView.isAbstract() );
+
+		entityBinding.setCustomLoaderName( entityView.getCustomLoaderName() );
+		entityBinding.setCustomInsert( entityView.getCustomInsert() );
+		entityBinding.setCustomUpdate( entityView.getCustomUpdate() );
+		entityBinding.setCustomDelete( entityView.getCustomDelete() );
+
+		if ( entityView.getSynchronizedTableNames() != null ) {
+			entityBinding.addSynchronizedTableNames( entityView.getSynchronizedTableNames() );
+		}
+
+		return entityBinding;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/EntityBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/EntityView.java
similarity index 66%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/EntityBindingState.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/EntityView.java
index 1fd0ff1aaa..e74b589c31 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/EntityBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/EntityView.java
@@ -1,123 +1,151 @@
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
-package org.hibernate.metamodel.binding.state;
+package org.hibernate.metamodel.binder.view;
 
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.domain.Hierarchical;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
- * Represents unified set of information about metadata specific to binding an entity.
+ * Represents the normalized set of mapping information about a specific entity.
  *
  * @author Gail Badner
  * @author Steve Ebersole
  */
-public interface EntityBindingState {
+public interface EntityView extends NormalizedViewObject {
 	/**
 	 * Obtain the Hibernate entity name.
 	 *
 	 * @return The entity name.
 	 */
 	public String getEntityName();
 
 	/**
 	 * Obtain the JPA entity name.
 	 *
 	 * @return  The JPA entity name
 	 */
 	public String getJpaEntityName();
 
 	/**
 	 * Obtain the entity mode represented by this state.
 	 *
 	 * @return The entity mode.
 	 */
 	public EntityMode getEntityMode();
 
 	/**
 	 * Obtain the name of the entity class.
 	 *
 	 * @return The entity class name.
 	 */
 	public String getClassName();
 
 	/**
 	 * The name of an interface to use for creating instance proxies for this entity.
 	 *
 	 * @return The name of the proxy interface.
 	 */
 	public String getProxyInterfaceName();
 
-	public Class<EntityPersister> getCustomEntityPersisterClass();
+	/**
+	 * Is this entity the root of a mapped inheritance hierarchy?
+	 *
+	 * @return {@code true} if this entity is an inheritance root; {@code false} otherwise.
+	 */
+	public boolean isRoot();
 
-	public Class<EntityTuplizer> getCustomEntityTuplizerClass();
+	/**
+	 * Obtains the type of inheritance defined for this entity hierarchy
+	 *
+	 * @return The inheritance strategy for this entity.
+	 */
+	public InheritanceType getEntityInheritanceType();
 
+	/**
+	 * Obtain the super type for this entity.
+	 *
+	 * @return This entity's super type.
+	 */
 	public Hierarchical getSuperType();
 
-	boolean isRoot();
+	/**
+	 * Obtain the custom {@link EntityPersister} class defined in this mapping.  {@code null} indicates the default
+	 * should be used.
+	 *
+	 * @return The custom {@link EntityPersister} class to use; or {@code null}
+	 */
+	public Class<? extends EntityPersister> getCustomEntityPersisterClass();
 
-	InheritanceType getEntityInheritanceType();
+	/**
+	 * Obtain the custom {@link EntityTuplizer} class defined in this mapping.  {@code null} indicates the default
+	 * should be used.
+	 *
+	 * @return The custom {@link EntityTuplizer} class to use; or {@code null}
+	 */
+	public Class<? extends EntityTuplizer> getCustomEntityTuplizerClass();
 
 	Caching getCaching();
 
-	MetaAttributeContext getMetaAttributeContext();
-
 	boolean isLazy();
 
 	boolean isMutable();
 
 	boolean isExplicitPolymorphism();
 
 	String getWhereFilter();
 
 	String getRowId();
 
 	boolean isDynamicUpdate();
 
 	boolean isDynamicInsert();
 
 	int getBatchSize();
 
 	boolean isSelectBeforeUpdate();
 
 	int getOptimisticLockMode();
 
-
 	Boolean isAbstract();
 
+	String getCustomLoaderName();
+
 	CustomSQL getCustomInsert();
 
 	CustomSQL getCustomUpdate();
 
 	CustomSQL getCustomDelete();
 
 	Set<String> getSynchronizedTableNames();
+
+
+	public TableView getBaseTable();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/Binder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/NormalizedViewObject.java
similarity index 69%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/Binder.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/NormalizedViewObject.java
index ee9d3d1879..1c4ff816da 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/Binder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/NormalizedViewObject.java
@@ -1,39 +1,36 @@
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
-package org.hibernate.metamodel.source.spi;
+package org.hibernate.metamodel.binder.view;
 
-import java.util.List;
-
-import org.hibernate.metamodel.MetadataSources;
+import org.hibernate.metamodel.source.Origin;
+import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
 /**
  * @author Steve Ebersole
  */
-public interface Binder {
-	public void prepare(MetadataSources sources);
-	public void bindIndependentMetadata(MetadataSources sources);
-	public void bindTypeDependentMetadata(MetadataSources sources);
-	public void bindMappingMetadata(MetadataSources sources, List<String> processedEntityNames);
-	public void bindMappingDependentMetadata(MetadataSources sources);
+public interface NormalizedViewObject {
+	public NormalizedViewObject getContainingViewObject();
+	public Origin getOrigin();
+	public MetaAttributeContext getMetaAttributeContext();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/TableView.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/TableView.java
new file mode 100644
index 0000000000..50fd75e732
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/TableView.java
@@ -0,0 +1,33 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
+package org.hibernate.metamodel.binder.view;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface TableView extends NormalizedViewObject {
+	public String getExplicitSchemaName();
+	public String getExplicitCatalogName();
+	public String getTableName();
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmEntityBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/hbm/EntityViewImpl.java
similarity index 80%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmEntityBindingState.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/hbm/EntityViewImpl.java
index fdd783d017..2415d2b50a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmEntityBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/hbm/EntityViewImpl.java
@@ -1,377 +1,416 @@
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
-package org.hibernate.metamodel.source.hbm.state.binding;
+package org.hibernate.metamodel.binder.view.hbm;
 
 import java.util.HashSet;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.engine.internal.Versioning;
+import org.hibernate.metamodel.binder.view.EntityView;
+import org.hibernate.metamodel.binder.view.NormalizedViewObject;
+import org.hibernate.metamodel.binder.view.TableView;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
-import org.hibernate.metamodel.binding.state.EntityBindingState;
 import org.hibernate.metamodel.domain.Hierarchical;
+import org.hibernate.metamodel.source.Origin;
 import org.hibernate.metamodel.source.hbm.HbmBindingContext;
 import org.hibernate.metamodel.source.hbm.HbmHelper;
 import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLCacheElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlDeleteElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlInsertElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlUpdateElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSynchronizeElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLTuplizerElement;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * @author Gail Badner
+ * @author Steve Ebersole
  */
-public class HbmEntityBindingState implements EntityBindingState {
+public class EntityViewImpl implements EntityView {
+	private final HbmBindingContext bindingContext;
+
 	private final String entityName;
 	private final EntityMode entityMode;
 
 	private final String className;
 	private final String proxyInterfaceName;
 
 	private final Class<EntityPersister> entityPersisterClass;
 	private final Class<EntityTuplizer> tuplizerClass;
 
 	private final MetaAttributeContext metaAttributeContext;
 
 	private final Hierarchical superType;
 	private final boolean isRoot;
 	private final InheritanceType entityInheritanceType;
 
 	private final Caching caching;
 
 	private final boolean lazy;
 	private final boolean mutable;
 	private final boolean explicitPolymorphism;
 	private final String whereFilter;
 	private final String rowId;
 
 	private final boolean dynamicUpdate;
 	private final boolean dynamicInsert;
 
 	private final int batchSize;
 	private final boolean selectBeforeUpdate;
 	private final int optimisticLockMode;
 
 	private final Boolean isAbstract;
 
+	private final String customLoaderName;
 	private final CustomSQL customInsert;
 	private final CustomSQL customUpdate;
 	private final CustomSQL customDelete;
 
 	private final Set<String> synchronizedTableNames;
 
-	public HbmEntityBindingState(
+	private final TableView baseTableView;
+
+	public EntityViewImpl(
 			Hierarchical superType,
 			XMLHibernateMapping.XMLClass entityClazz,
 			boolean isRoot,
 			InheritanceType inheritanceType,
 			HbmBindingContext bindingContext) {
 
+		this.bindingContext = bindingContext;
+
 		this.superType = superType;
 		this.entityName = bindingContext.extractEntityName( entityClazz );
 
 		final String verbatimClassName = entityClazz.getName();
 		this.entityMode = verbatimClassName == null ? EntityMode.MAP : EntityMode.POJO;
 
 		if ( this.entityMode == EntityMode.POJO ) {
 			this.className = bindingContext.getClassName( verbatimClassName );
 			this.proxyInterfaceName = entityClazz.getProxy();
 		}
 		else {
 			this.className = null;
 			this.proxyInterfaceName = null;
 		}
 
 		final String customTuplizerClassName = extractCustomTuplizerClassName( entityClazz, entityMode );
-		tuplizerClass = customTuplizerClassName != null
+		this.tuplizerClass = customTuplizerClassName != null
 				? bindingContext.<EntityTuplizer>locateClassByName( customTuplizerClassName )
 				: null;
+		this.entityPersisterClass = entityClazz.getPersister() == null
+				? null
+				: bindingContext.<EntityPersister>locateClassByName( entityClazz.getPersister() );
 
 		this.isRoot = isRoot;
 		this.entityInheritanceType = inheritanceType;
 
 		this.caching = createCaching( entityClazz, bindingContext.extractEntityName( entityClazz ) );
 
-		metaAttributeContext = HbmHelper.extractMetaAttributeContext(
+		this.metaAttributeContext = HbmHelper.extractMetaAttributeContext(
 				entityClazz.getMeta(), true, bindingContext.getMetaAttributeContext()
 		);
 
 		// go ahead and set the lazy here, since pojo.proxy can override it.
-		lazy = MappingHelper.getBooleanValue(
+		this.lazy = MappingHelper.getBooleanValue(
 				entityClazz.isLazy(), bindingContext.getMappingDefaults().areAssociationsLazy()
 		);
-		mutable = entityClazz.isMutable();
-
-		explicitPolymorphism = "explicit".equals( entityClazz.getPolymorphism() );
-		whereFilter = entityClazz.getWhere();
-		rowId = entityClazz.getRowid();
-		dynamicUpdate = entityClazz.isDynamicUpdate();
-		dynamicInsert = entityClazz.isDynamicInsert();
-		batchSize = MappingHelper.getIntValue( entityClazz.getBatchSize(), 0 );
-		selectBeforeUpdate = entityClazz.isSelectBeforeUpdate();
-		optimisticLockMode = getOptimisticLockMode();
-
-		// PERSISTER
-		entityPersisterClass = entityClazz.getPersister() == null
-				? null
-				: bindingContext.<EntityPersister>locateClassByName( entityClazz.getPersister() );
+		this.mutable = entityClazz.isMutable();
+
+		this.explicitPolymorphism = "explicit".equals( entityClazz.getPolymorphism() );
+		this.whereFilter = entityClazz.getWhere();
+		this.rowId = entityClazz.getRowid();
+		this.dynamicUpdate = entityClazz.isDynamicUpdate();
+		this.dynamicInsert = entityClazz.isDynamicInsert();
+		this.batchSize = MappingHelper.getIntValue( entityClazz.getBatchSize(), 0 );
+		this.selectBeforeUpdate = entityClazz.isSelectBeforeUpdate();
+		this.optimisticLockMode = getOptimisticLockMode();
+
+		this.customLoaderName = entityClazz.getLoader().getQueryRef();
 
-		// CUSTOM SQL
 		XMLSqlInsertElement sqlInsert = entityClazz.getSqlInsert();
 		if ( sqlInsert != null ) {
-			customInsert = HbmHelper.getCustomSql(
+			this.customInsert = HbmHelper.getCustomSql(
 					sqlInsert.getValue(),
 					sqlInsert.isCallable(),
 					sqlInsert.getCheck().value()
 			);
 		}
 		else {
-			customInsert = null;
+			this.customInsert = null;
 		}
 
 		XMLSqlDeleteElement sqlDelete = entityClazz.getSqlDelete();
 		if ( sqlDelete != null ) {
-			customDelete = HbmHelper.getCustomSql(
+			this.customDelete = HbmHelper.getCustomSql(
 					sqlDelete.getValue(),
 					sqlDelete.isCallable(),
 					sqlDelete.getCheck().value()
 			);
 		}
 		else {
-			customDelete = null;
+			this.customDelete = null;
 		}
 
 		XMLSqlUpdateElement sqlUpdate = entityClazz.getSqlUpdate();
 		if ( sqlUpdate != null ) {
-			customUpdate = HbmHelper.getCustomSql(
+			this.customUpdate = HbmHelper.getCustomSql(
 					sqlUpdate.getValue(),
 					sqlUpdate.isCallable(),
 					sqlUpdate.getCheck().value()
 			);
 		}
 		else {
-			customUpdate = null;
+			this.customUpdate = null;
 		}
 
 		if ( entityClazz.getSynchronize() != null ) {
-			synchronizedTableNames = new HashSet<String>( entityClazz.getSynchronize().size() );
+			this.synchronizedTableNames = new HashSet<String>( entityClazz.getSynchronize().size() );
 			for ( XMLSynchronizeElement synchronize : entityClazz.getSynchronize() ) {
-				synchronizedTableNames.add( synchronize.getTable() );
+				this.synchronizedTableNames.add( synchronize.getTable() );
 			}
 		}
 		else {
-			synchronizedTableNames = null;
+			this.synchronizedTableNames = null;
 		}
-		isAbstract = entityClazz.isAbstract();
+
+		this.isAbstract = entityClazz.isAbstract();
+
+		this.baseTableView = new TableViewImpl(
+				entityClazz.getSchema(),
+				entityClazz.getCatalog(),
+				entityClazz.getTable(),
+				this,
+				bindingContext
+		);
 	}
 
 	private String extractCustomTuplizerClassName(XMLHibernateMapping.XMLClass entityClazz, EntityMode entityMode) {
 		if ( entityClazz.getTuplizer() == null ) {
 			return null;
 		}
 		for ( XMLTuplizerElement tuplizerElement : entityClazz.getTuplizer() ) {
 			if ( entityMode == EntityMode.parse( tuplizerElement.getEntityMode() ) ) {
 				return tuplizerElement.getClazz();
 			}
 		}
 		return null;
 	}
 
 	private static Caching createCaching(XMLHibernateMapping.XMLClass entityClazz, String entityName) {
 		XMLCacheElement cache = entityClazz.getCache();
 		if ( cache == null ) {
 			return null;
 		}
 		final String region = cache.getRegion() != null ? cache.getRegion() : entityName;
 		final AccessType accessType = Enum.valueOf( AccessType.class, cache.getUsage() );
 		final boolean cacheLazyProps = !"non-lazy".equals( cache.getInclude() );
 		return new Caching( region, accessType, cacheLazyProps );
 	}
 
 	private static int createOptimisticLockMode(XMLHibernateMapping.XMLClass entityClazz) {
 		String optimisticLockModeString = MappingHelper.getStringValue( entityClazz.getOptimisticLock(), "version" );
 		int optimisticLockMode;
 		if ( "version".equals( optimisticLockModeString ) ) {
 			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_VERSION;
 		}
 		else if ( "dirty".equals( optimisticLockModeString ) ) {
 			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_DIRTY;
 		}
 		else if ( "all".equals( optimisticLockModeString ) ) {
 			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_ALL;
 		}
 		else if ( "none".equals( optimisticLockModeString ) ) {
 			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_NONE;
 		}
 		else {
 			throw new MappingException( "Unsupported optimistic-lock style: " + optimisticLockModeString );
 		}
 		return optimisticLockMode;
 	}
 
 	@Override
 	public String getEntityName() {
 		return entityName;
 	}
 
 	@Override
 	public String getJpaEntityName() {
 		return null;  // no such notion in hbm.xml files
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	@Override
 	public String getClassName() {
 		return className;
 	}
 
 	@Override
 	public String getProxyInterfaceName() {
 		return proxyInterfaceName;
 	}
 
 	@Override
 	public Class<EntityPersister> getCustomEntityPersisterClass() {
 		return entityPersisterClass;
 	}
 
 	@Override
 	public Class<EntityTuplizer> getCustomEntityTuplizerClass() {
 		return tuplizerClass;
 	}
 
 	@Override
 	public Hierarchical getSuperType() {
 		return superType;
 	}
 
 	@Override
 	public boolean isRoot() {
 		return isRoot;
 	}
 
 	@Override
 	public InheritanceType getEntityInheritanceType() {
 		return entityInheritanceType;
 	}
 
 	@Override
 	public Caching getCaching() {
 		return caching;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	@Override
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	@Override
 	public String getWhereFilter() {
 		return whereFilter;
 	}
 
 	@Override
 	public String getRowId() {
 		return rowId;
 	}
 
 	@Override
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	@Override
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	@Override
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	@Override
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	@Override
 	public int getOptimisticLockMode() {
 		return optimisticLockMode;
 	}
 
 	@Override
 	public Boolean isAbstract() {
 		return isAbstract;
 	}
 
 	@Override
 	public CustomSQL getCustomInsert() {
 		return customInsert;
 	}
 
 	@Override
 	public CustomSQL getCustomUpdate() {
 		return customUpdate;
 	}
 
 	@Override
 	public CustomSQL getCustomDelete() {
 		return customDelete;
 	}
 
 	@Override
 	public Set<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
+
+	@Override
+	public String getCustomLoaderName() {
+		return customLoaderName;
+	}
+
+	@Override
+	public TableView getBaseTable() {
+		return baseTableView;
+	}
+
+	@Override
+	public NormalizedViewObject getContainingViewObject() {
+		return null;
+	}
+
+	@Override
+	public Origin getOrigin() {
+		return bindingContext.getOrigin();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/hbm/TableViewImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/hbm/TableViewImpl.java
new file mode 100644
index 0000000000..5427136844
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/hbm/TableViewImpl.java
@@ -0,0 +1,62 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
+package org.hibernate.metamodel.binder.view.hbm;
+
+import org.hibernate.metamodel.binder.view.TableView;
+import org.hibernate.metamodel.source.hbm.HbmBindingContext;
+
+/**
+ * @author Steve Ebersole
+ */
+public class TableViewImpl implements TableView {
+	private final String explicitSchemaName;
+	private final String explicitCatalogName;
+	private final String tableName;
+
+	public TableViewImpl(
+			String explicitSchemaName,
+			String explicitCatalogName,
+			String tableName,
+			EntityViewImpl entityView,
+			HbmBindingContext bindingContext) {
+		this.explicitSchemaName = explicitSchemaName;
+		this.explicitCatalogName = explicitCatalogName;
+		this.tableName = tableName;
+	}
+
+	@Override
+	public String getExplicitSchemaName() {
+		return explicitSchemaName;
+	}
+
+	@Override
+	public String getExplicitCatalogName() {
+		return explicitCatalogName;
+	}
+
+	@Override
+	public String getTableName() {
+		return tableName;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
index 1aed477588..e74c78846c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
@@ -1,429 +1,518 @@
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
 package org.hibernate.metamodel.binding;
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.FilterDefinition;
-import org.hibernate.metamodel.binding.state.EntityBindingState;
+import org.hibernate.metamodel.binder.view.EntityView;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.source.spi.BindingContext;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * Provides the link between the domain and the relational model for an entity.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  * @author Gail Badner
  */
 public class EntityBinding {
 	private Entity entity;
 	private TableSpecification baseTable;
 
 	private EntityMode entityMode;
 	private JavaType proxyInterfaceType;
 
 	private String jpaEntityName;
 
-	private Class<EntityPersister> entityPersisterClass;
-	private Class<EntityTuplizer> entityTuplizerClass;
+	private Class<? extends EntityPersister> customEntityPersisterClass;
+	private Class<? extends EntityTuplizer> customEntityTuplizerClass;
 
 	private boolean isRoot;
 	private InheritanceType entityInheritanceType;
 
 	private final EntityIdentifier entityIdentifier = new EntityIdentifier( this );
 	private EntityDiscriminator entityDiscriminator;
 	private SimpleAttributeBinding versionBinding;
 
 	private Map<String, AttributeBinding> attributeBindingMap = new HashMap<String, AttributeBinding>();
 	private Set<FilterDefinition> filterDefinitions = new HashSet<FilterDefinition>( );
 	private Set<EntityReferencingAttributeBinding> entityReferencingAttributeBindings = new HashSet<EntityReferencingAttributeBinding>();
 
 	private Caching caching;
 
 	private MetaAttributeContext metaAttributeContext;
 
 	private boolean lazy;
 	private boolean mutable;
 	private boolean explicitPolymorphism;
 	private String whereFilter;
 	private String rowId;
 
 	private boolean dynamicUpdate;
 	private boolean dynamicInsert;
 
 	private int batchSize;
 	private boolean selectBeforeUpdate;
 	private boolean hasSubselectLoadableCollections;
 	private int optimisticLockMode;
 
 	private Boolean isAbstract;
 
+	private String customLoaderName;
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private Set<String> synchronizedTableNames = new HashSet<String>();
 
-	public EntityBinding initialize(BindingContext bindingContext, EntityBindingState state) {
+	public EntityBinding initialize(BindingContext bindingContext, EntityView state) {
 		// todo : Entity will need both entityName and className to be effective
 		this.entity = new Entity(
 				state.getEntityName(),
 				state.getSuperType(),
 				bindingContext.makeJavaType( state.getClassName() )
 		);
 
 		this.isRoot = state.isRoot();
 		this.entityInheritanceType = state.getEntityInheritanceType();
 
 		this.entityMode = state.getEntityMode();
 		this.jpaEntityName = state.getJpaEntityName();
 
 		// todo : handle the entity-persister-resolver stuff
-		this.entityPersisterClass = state.getCustomEntityPersisterClass();
-		this.entityTuplizerClass = state.getCustomEntityTuplizerClass();
+		this.customEntityPersisterClass = state.getCustomEntityPersisterClass();
+		this.customEntityTuplizerClass = state.getCustomEntityTuplizerClass();
 
 		this.caching = state.getCaching();
 		this.metaAttributeContext = state.getMetaAttributeContext();
 
 		if ( entityMode == EntityMode.POJO ) {
 			if ( state.getProxyInterfaceName() != null ) {
 				this.proxyInterfaceType = bindingContext.makeJavaType( state.getProxyInterfaceName() );
 				this.lazy = true;
 			}
 			else if ( state.isLazy() ) {
 				this.proxyInterfaceType = entity.getJavaType();
 				this.lazy = true;
 			}
 		}
 		else {
 			this.proxyInterfaceType = new JavaType( Map.class );
 			this.lazy = state.isLazy();
 		}
 
 		this.mutable = state.isMutable();
 		this.explicitPolymorphism = state.isExplicitPolymorphism();
 		this.whereFilter = state.getWhereFilter();
 		this.rowId = state.getRowId();
 		this.dynamicUpdate = state.isDynamicUpdate();
 		this.dynamicInsert = state.isDynamicInsert();
 		this.batchSize = state.getBatchSize();
 		this.selectBeforeUpdate = state.isSelectBeforeUpdate();
 		this.optimisticLockMode = state.getOptimisticLockMode();
 		this.isAbstract = state.isAbstract();
 		this.customInsert = state.getCustomInsert();
 		this.customUpdate = state.getCustomUpdate();
 		this.customDelete = state.getCustomDelete();
 		if ( state.getSynchronizedTableNames() != null ) {
 			for ( String synchronizedTableName : state.getSynchronizedTableNames() ) {
 				addSynchronizedTable( synchronizedTableName );
 			}
 		}
 		return this;
 	}
 
-	public boolean isRoot() {
-		return isRoot;
-	}
-
-	public void setRoot(boolean isRoot) {
-		this.isRoot = isRoot;
-	}
-
 	public Entity getEntity() {
 		return entity;
 	}
 
 	public void setEntity(Entity entity) {
 		this.entity = entity;
 	}
 
 	public TableSpecification getBaseTable() {
 		return baseTable;
 	}
 
 	public void setBaseTable(TableSpecification baseTable) {
 		this.baseTable = baseTable;
 	}
 
+	public boolean isRoot() {
+		return isRoot;
+	}
+
+	public void setRoot(boolean isRoot) {
+		this.isRoot = isRoot;
+	}
+
 	public EntityIdentifier getEntityIdentifier() {
 		return entityIdentifier;
 	}
 
 	public void bindEntityIdentifier(SimpleAttributeBinding attributeBinding) {
 		if ( !Column.class.isInstance( attributeBinding.getValue() ) ) {
 			throw new MappingException(
 					"Identifier value must be a Column; instead it is: " + attributeBinding.getValue().getClass()
 			);
 		}
 		entityIdentifier.setValueBinding( attributeBinding );
 		baseTable.getPrimaryKey().addColumn( Column.class.cast( attributeBinding.getValue() ) );
 	}
 
 	public EntityDiscriminator getEntityDiscriminator() {
 		return entityDiscriminator;
 	}
 
 	public void setInheritanceType(InheritanceType entityInheritanceType) {
 		this.entityInheritanceType = entityInheritanceType;
 	}
 
 	public InheritanceType getInheritanceType() {
 		return entityInheritanceType;
 	}
 
 	public boolean isVersioned() {
 		return versionBinding != null;
 	}
 
 	public SimpleAttributeBinding getVersioningValueBinding() {
 		return versionBinding;
 	}
 
 	public Iterable<AttributeBinding> getAttributeBindings() {
 		return attributeBindingMap.values();
 	}
 
 	public AttributeBinding getAttributeBinding(String name) {
 		return attributeBindingMap.get( name );
 	}
 
 	/**
 	 * Gets the number of attribute bindings defined on this class, including the
 	 * identifier attribute binding and attribute bindings defined
 	 * as part of a join.
 	 *
 	 * @return The number of attribute bindings
 	 */
 	public int getAttributeBindingClosureSpan() {
 		// TODO: fix this after HHH-6337 is fixed; for now just return size of attributeBindingMap
 		// if this is not a root, then need to include the superclass attribute bindings
 		return attributeBindingMap.size();
 	}
 
 	/**
 	 * Gets the attribute bindings defined on this class, including the
 	 * identifier attribute binding and attribute bindings defined
 	 * as part of a join.
 	 *
 	 * @return The attribute bindings.
 	 */
 	public Iterable<AttributeBinding> getAttributeBindingClosure() {
 		// TODO: fix this after HHH-6337 is fixed. for now, just return attributeBindings
 		// if this is not a root, then need to include the superclass attribute bindings
 		return getAttributeBindings();
 	}
 
 	public Iterable<FilterDefinition> getFilterDefinitions() {
 		return filterDefinitions;
 	}
 
 	public void addFilterDefinition(FilterDefinition filterDefinition) {
 		filterDefinitions.add( filterDefinition );
 	}
 
 	public Iterable<EntityReferencingAttributeBinding> getEntityReferencingAttributeBindings() {
 		return entityReferencingAttributeBindings;
 	}
 
 	public SimpleAttributeBinding makeSimpleIdAttributeBinding(Attribute attribute) {
 		final SimpleAttributeBinding binding = makeSimpleAttributeBinding( attribute, true, true );
 		getEntityIdentifier().setValueBinding( binding );
 		return binding;
 	}
 
 	public EntityDiscriminator makeEntityDiscriminator(Attribute attribute) {
 		if ( entityDiscriminator != null ) {
 			throw new AssertionFailure( "Creation of entity discriminator was called more than once" );
 		}
 		entityDiscriminator = new EntityDiscriminator();
 		entityDiscriminator.setValueBinding( makeSimpleAttributeBinding( attribute, true, false ) );
 		return entityDiscriminator;
 	}
 
 	public SimpleAttributeBinding makeVersionBinding(Attribute attribute) {
 		versionBinding = makeSimpleAttributeBinding( attribute, true, false );
 		return versionBinding;
 	}
 
 	public SimpleAttributeBinding makeSimpleAttributeBinding(Attribute attribute) {
 		return makeSimpleAttributeBinding( attribute, false, false );
 	}
 
 	private SimpleAttributeBinding makeSimpleAttributeBinding(Attribute attribute, boolean forceNonNullable, boolean forceUnique) {
 		final SimpleAttributeBinding binding = new SimpleAttributeBinding( this, forceNonNullable, forceUnique );
 		registerAttributeBinding( attribute.getName(), binding );
 		binding.setAttribute( attribute );
 		return binding;
 	}
 
 	public ManyToOneAttributeBinding makeManyToOneAttributeBinding(String attributeName) {
 		final ManyToOneAttributeBinding binding = new ManyToOneAttributeBinding( this );
 		registerAttributeBinding( attributeName, binding );
 		binding.setAttribute( entity.getAttribute( attributeName ) );
 		return binding;
 	}
 
 	public BagBinding makeBagAttributeBinding(String attributeName, CollectionElementType collectionElementType) {
 		final BagBinding binding = new BagBinding( this, collectionElementType );
 		registerAttributeBinding( attributeName, binding );
 		binding.setAttribute( entity.getAttribute( attributeName ) );
 		return binding;
 	}
 
 	private void registerAttributeBinding(String name, EntityReferencingAttributeBinding attributeBinding) {
 		entityReferencingAttributeBindings.add( attributeBinding );
 		registerAttributeBinding( name, (AttributeBinding) attributeBinding );
 	}
 
 	private void registerAttributeBinding(String name, AttributeBinding attributeBinding) {
 		attributeBindingMap.put( name, attributeBinding );
 	}
 
 	public Caching getCaching() {
 		return caching;
 	}
 
+	public void setCaching(Caching caching) {
+		this.caching = caching;
+	}
+
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
+	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
+		this.metaAttributeContext = metaAttributeContext;
+	}
+
 	public boolean isMutable() {
 		return mutable;
 	}
 
+	public void setMutable(boolean mutable) {
+		this.mutable = mutable;
+	}
+
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public JavaType getProxyInterfaceType() {
 		return proxyInterfaceType;
 	}
 
+	public void setProxyInterfaceType(JavaType proxyInterfaceType) {
+		this.proxyInterfaceType = proxyInterfaceType;
+	}
+
 	public String getWhereFilter() {
 		return whereFilter;
 	}
 
+	public void setWhereFilter(String whereFilter) {
+		this.whereFilter = whereFilter;
+	}
+
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
+	public void setExplicitPolymorphism(boolean explicitPolymorphism) {
+		this.explicitPolymorphism = explicitPolymorphism;
+	}
+
 	public String getRowId() {
 		return rowId;
 	}
 
+	public void setRowId(String rowId) {
+		this.rowId = rowId;
+	}
+
 	public String getDiscriminatorValue() {
 		return entityDiscriminator == null ? null : entityDiscriminator.getDiscriminatorValue();
 	}
 
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
+	public void setDynamicUpdate(boolean dynamicUpdate) {
+		this.dynamicUpdate = dynamicUpdate;
+	}
+
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
+	public void setDynamicInsert(boolean dynamicInsert) {
+		this.dynamicInsert = dynamicInsert;
+	}
+
 	public int getBatchSize() {
 		return batchSize;
 	}
 
+	public void setBatchSize(int batchSize) {
+		this.batchSize = batchSize;
+	}
+
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
+	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
+		this.selectBeforeUpdate = selectBeforeUpdate;
+	}
+
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	/* package-protected */
 	void setSubselectLoadableCollections(boolean hasSubselectLoadableCollections) {
 		this.hasSubselectLoadableCollections = hasSubselectLoadableCollections;
 	}
 
 	public int getOptimisticLockMode() {
 		return optimisticLockMode;
 	}
 
-	public Class<EntityPersister> getEntityPersisterClass() {
-		return entityPersisterClass;
+	public void setOptimisticLockMode(int optimisticLockMode) {
+		this.optimisticLockMode = optimisticLockMode;
+	}
+
+	public Class<? extends EntityPersister> getCustomEntityPersisterClass() {
+		return customEntityPersisterClass;
+	}
+
+	public void setCustomEntityPersisterClass(Class<? extends EntityPersister> customEntityPersisterClass) {
+		this.customEntityPersisterClass = customEntityPersisterClass;
+	}
+
+	public Class<? extends EntityTuplizer> getCustomEntityTuplizerClass() {
+		return customEntityTuplizerClass;
 	}
 
-	public Class<EntityTuplizer> getEntityTuplizerClass() {
-		return entityTuplizerClass;
+	public void setCustomEntityTuplizerClass(Class<? extends EntityTuplizer> customEntityTuplizerClass) {
+		this.customEntityTuplizerClass = customEntityTuplizerClass;
 	}
 
 	public Boolean isAbstract() {
 		return isAbstract;
 	}
 
-	protected void addSynchronizedTable(String tableName) {
-		synchronizedTableNames.add( tableName );
+	public void setAbstract(Boolean isAbstract) {
+		this.isAbstract = isAbstract;
 	}
 
 	public Set<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
 
-	// Custom SQL ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+	public void addSynchronizedTable(String tableName) {
+		synchronizedTableNames.add( tableName );
+	}
 
-	private String loaderName;
+	public void addSynchronizedTableNames(java.util.Collection<String> synchronizedTableNames) {
+		this.synchronizedTableNames.addAll( synchronizedTableNames );
+	}
+
+	public EntityMode getEntityMode() {
+		return entityMode;
+	}
+
+	public void setEntityMode(EntityMode entityMode) {
+		this.entityMode = entityMode;
+	}
+
+	public String getJpaEntityName() {
+		return jpaEntityName;
+	}
 
-	public String getLoaderName() {
-		return loaderName;
+	public void setJpaEntityName(String jpaEntityName) {
+		this.jpaEntityName = jpaEntityName;
 	}
 
-	public void setLoaderName(String loaderName) {
-		this.loaderName = loaderName;
+	public String getCustomLoaderName() {
+		return customLoaderName;
+	}
+
+	public void setCustomLoaderName(String customLoaderName) {
+		this.customLoaderName = customLoaderName;
 	}
 
 	public CustomSQL getCustomInsert() {
 		return customInsert;
 	}
 
+	public void setCustomInsert(CustomSQL customInsert) {
+		this.customInsert = customInsert;
+	}
+
 	public CustomSQL getCustomUpdate() {
 		return customUpdate;
 	}
 
+	public void setCustomUpdate(CustomSQL customUpdate) {
+		this.customUpdate = customUpdate;
+	}
+
 	public CustomSQL getCustomDelete() {
 		return customDelete;
 	}
+
+	public void setCustomDelete(CustomSQL customDelete) {
+		this.customDelete = customDelete;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Schema.java b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Schema.java
index 5960917e23..812393ba41 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Schema.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/relational/Schema.java
@@ -1,162 +1,170 @@
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
 package org.hibernate.metamodel.relational;
 
 import java.util.HashMap;
 import java.util.Map;
 
 /**
  * Represents a named schema/catalog pair and manages objects defined within.
  *
  * @author Steve Ebersole
  */
 public class Schema {
 	private final Name name;
 	private Map<String, InLineView> inLineViews = new HashMap<String, InLineView>();
 	private Map<Identifier, Table> tables = new HashMap<Identifier, Table>();
 
 	public Schema(Name name) {
 		this.name = name;
 	}
 
 	public Schema(Identifier schema, Identifier catalog) {
 		this( new Name( schema, catalog ) );
 	}
 
 	public Name getName() {
 		return name;
 	}
 
-	public Table getTable(Identifier name) {
+	public Table locateTable(Identifier name) {
 		return tables.get( name );
 	}
 
 	public Table createTable(Identifier name) {
 		Table table = new Table( this, name );
 		tables.put( name, table );
 		return table;
 	}
 
+	public Table locateOrCreateTable(Identifier name) {
+		final Table existing = locateTable( name );
+		if ( existing == null ) {
+			return createTable( name );
+		}
+		return existing;
+	}
+
 	public InLineView getInLineView(String logicalName) {
 		return inLineViews.get( logicalName );
 	}
 
 	public InLineView createInLineView(String logicalName, String subSelect) {
 		InLineView inLineView = new InLineView( this, logicalName, subSelect );
 		inLineViews.put( logicalName, inLineView );
 		return inLineView;
 	}
 
 	@Override
 	public String toString() {
 		final StringBuilder sb = new StringBuilder();
 		sb.append( "Schema" );
 		sb.append( "{name=" ).append( name );
 		sb.append( '}' );
 		return sb.toString();
 	}
 
 	@Override
 	public boolean equals(Object o) {
 		if ( this == o ) {
 			return true;
 		}
 		if ( o == null || getClass() != o.getClass() ) {
 			return false;
 		}
 
 		Schema schema = (Schema) o;
 
 		if ( name != null ? !name.equals( schema.name ) : schema.name != null ) {
 			return false;
 		}
 
 		return true;
 	}
 
 	@Override
 	public int hashCode() {
 		return name != null ? name.hashCode() : 0;
 	}
 
 	public static class Name {
 		private final Identifier schema;
 		private final Identifier catalog;
 
 		public Name(Identifier schema, Identifier catalog) {
 			this.schema = schema;
 			this.catalog = catalog;
 		}
 
 		public Name(String schema, String catalog) {
 			this( Identifier.toIdentifier( schema ), Identifier.toIdentifier( catalog ) );
 		}
 
 		public Identifier getSchema() {
 			return schema;
 		}
 
 		public Identifier getCatalog() {
 			return catalog;
 		}
 
 		@Override
 		public String toString() {
 			final StringBuilder sb = new StringBuilder();
 			sb.append( "Name" );
 			sb.append( "{schema=" ).append( schema );
 			sb.append( ", catalog=" ).append( catalog );
 			sb.append( '}' );
 			return sb.toString();
 		}
 
 		@Override
 		public boolean equals(Object o) {
 			if ( this == o ) {
 				return true;
 			}
 			if ( o == null || getClass() != o.getClass() ) {
 				return false;
 			}
 
 			Name name = (Name) o;
 
 			if ( catalog != null ? !catalog.equals( name.catalog ) : name.catalog != null ) {
 				return false;
 			}
 			if ( schema != null ? !schema.equals( name.schema ) : name.schema != null ) {
 				return false;
 			}
 
 			return true;
 		}
 
 		@Override
 		public int hashCode() {
 			int result = schema != null ? schema.hashCode() : 0;
 			result = 31 * result + ( catalog != null ? catalog.hashCode() : 0 );
 			return result;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/SourceType.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/SourceType.java
index e7a158f64f..07eb6046ce 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/SourceType.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/SourceType.java
@@ -1,40 +1,41 @@
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
 
 /**
  * From where did the metadata come from?
  *
  * @author Steve Ebersole
  */
 public enum SourceType {
 	RESOURCE,
 	FILE,
 	INPUT_STREAM,
 	URL,
 	STRING,
 	DOM,
-	JAR
+	JAR,
+	OTHER
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationSourceProcessor.java
similarity index 82%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBinder.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationSourceProcessor.java
index a7c3e67a69..c743ad417c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationSourceProcessor.java
@@ -1,194 +1,194 @@
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
 
 import org.hibernate.HibernateException;
-import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.domain.NonEntity;
 import org.hibernate.metamodel.domain.Superclass;
 import org.hibernate.metamodel.source.annotation.xml.XMLEntityMappings;
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
 import org.hibernate.metamodel.source.annotations.util.ConfiguredClassHierarchyBuilder;
 import org.hibernate.metamodel.source.annotations.xml.OrmXmlParser;
 import org.hibernate.metamodel.source.annotations.xml.PseudoJpaDotNames;
 import org.hibernate.metamodel.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.internal.MetadataImpl;
-import org.hibernate.metamodel.source.spi.Binder;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
+import org.hibernate.metamodel.source.spi.SourceProcessor;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * Main class responsible to creating and binding the Hibernate meta-model from annotations.
  * This binder only has to deal with the (jandex) annotation index/repository. XML configuration is already processed
  * and pseudo annotations are created.
  *
  * @author Hardy Ferentschik
  * @see org.hibernate.metamodel.source.annotations.xml.OrmXmlParser
  */
-public class AnnotationBinder implements Binder {
-	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
-			CoreMessageLogger.class,
-			AnnotationBinder.class.getName()
-	);
+public class AnnotationSourceProcessor implements SourceProcessor {
+	private static final Logger LOG = Logger.getLogger( AnnotationSourceProcessor.class );
 
-	private final MetadataImpl metadata;
+	private final MetadataImplementor metadata;
+	private final Value<ClassLoaderService> classLoaderService;
 
 	private Index index;
-	private ClassLoaderService classLoaderService;
 
-	public AnnotationBinder(MetadataImpl metadata) {
+	public AnnotationSourceProcessor(MetadataImpl metadata) {
 		this.metadata = metadata;
+		this.classLoaderService = new Value<ClassLoaderService>(
+				new Value.DeferredInitializer<ClassLoaderService>() {
+					@Override
+					public ClassLoaderService initialize() {
+						return AnnotationSourceProcessor.this.metadata.getServiceRegistry().getService( ClassLoaderService.class );
+					}
+				}
+		);
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
 
 		index = indexer.complete();
 
 		List<JaxbRoot<XMLEntityMappings>> mappings = new ArrayList<JaxbRoot<XMLEntityMappings>>();
 		for ( JaxbRoot<?> root : sources.getJaxbRootList() ) {
 			if ( root.getRoot() instanceof XMLEntityMappings ) {
 				mappings.add( (JaxbRoot<XMLEntityMappings>) root );
 			}
 		}
 		if ( !mappings.isEmpty() ) {
 			// process the xml configuration
 			final OrmXmlParser ormParser = new OrmXmlParser( metadata );
 			index = ormParser.parseAndUpdateIndex( mappings, index );
 		}
 
         if( index.getAnnotations( PseudoJpaDotNames.DEFAULT_DELIMITED_IDENTIFIERS ) != null ) {
+			// todo : this needs to move to AnnotationBindingContext
             metadata.setGloballyQuotedIdentifiers( true );
         }
 	}
 
 	/**
 	 * Adds the class w/ the specified name to the jandex index.
 	 *
 	 * @param indexer The jandex indexer
 	 * @param className the fully qualified class name to be indexed
 	 */
 	private void indexClass(Indexer indexer, String className) {
-		InputStream stream = classLoaderService().locateResourceStream( className );
+		InputStream stream = classLoaderService.getValue().locateResourceStream( className );
 		try {
 			indexer.index( stream );
 		}
 		catch ( IOException e ) {
 			throw new HibernateException( "Unable to open input stream for class " + className, e );
 		}
 	}
 
-	private ClassLoaderService classLoaderService() {
-		if ( classLoaderService == null ) {
-			classLoaderService = metadata.getServiceRegistry().getService( ClassLoaderService.class );
-		}
-		return classLoaderService;
-	}
-
 	@Override
-	public void bindIndependentMetadata(MetadataSources sources) {
-		TypeDefBinder.bind( metadata, index );
+	public void processIndependentMetadata(MetadataSources sources) {
+        TypeDefBinder.bind( metadata, index );
 	}
 
 	@Override
-	public void bindTypeDependentMetadata(MetadataSources sources) {
-		IdGeneratorBinder.bind( metadata, index );
+	public void processTypeDependentMetadata(MetadataSources sources) {
+        IdGeneratorBinder.bind( metadata, index );
 	}
 
 	@Override
-	public void bindMappingMetadata(MetadataSources sources, List<String> processedEntityNames) {
+	public void processMappingMetadata(MetadataSources sources, List<String> processedEntityNames) {
 		AnnotationBindingContext context = new AnnotationBindingContext( index, metadata.getServiceRegistry() );
+
 		// need to order our annotated entities into an order we can process
 		Set<ConfiguredClassHierarchy<EntityClass>> hierarchies = ConfiguredClassHierarchyBuilder.createEntityHierarchies(
 				context
 		);
 
 		// now we process each hierarchy one at the time
 		Hierarchical parent = null;
 		for ( ConfiguredClassHierarchy<EntityClass> hierarchy : hierarchies ) {
 			for ( EntityClass entityClass : hierarchy ) {
 				// for classes annotated w/ @Entity we create a EntityBinding
 				if ( ConfiguredClassType.ENTITY.equals( entityClass.getConfiguredClassType() ) ) {
-					LOG.bindingEntityFromAnnotatedClass( entityClass.getName() );
+					LOG.debugf( "Binding entity from annotated class: %s", entityClass.getName() );
 					EntityBinder entityBinder = new EntityBinder( metadata, entityClass, parent );
 					EntityBinding binding = entityBinder.bind();
 					parent = binding.getEntity();
 				}
 				// for classes annotated w/ @MappedSuperclass we just create the domain instance
 				// the attribute bindings will be part of the first entity subclass
 				else if ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( entityClass.getConfiguredClassType() ) ) {
 					parent = new Superclass( entityClass.getName(), parent );
 				}
 				// for classes which are not annotated at all we create the NonEntity domain class
 				// todo - not sure whether this is needed. It might be that we don't need this information (HF)
 				else {
 					parent = new NonEntity( entityClass.getName(), parent );
 				}
 			}
 		}
 	}
 
 	@Override
-	public void bindMappingDependentMetadata(MetadataSources sources) {
+	public void processMappingDependentMetadata(MetadataSources sources) {
 		TableBinder.bind( metadata, index );
 		FetchProfileBinder.bind( metadata, index );
 		QueryBinder.bind( metadata, index );
 		FilterDefBinder.bind( metadata, index );
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
index be80ef6927..2282c76c3f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
@@ -1,844 +1,841 @@
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
 package org.hibernate.metamodel.source.annotations.entity;
 
+import javax.persistence.GenerationType;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
-import javax.persistence.GenerationType;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.DotName;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.AssertionFailure;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.CacheConcurrencyStrategy;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.annotations.PolymorphismType;
 import org.hibernate.annotations.ResultCheckStyle;
 import org.hibernate.cache.spi.RegionFactory;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.EntityDiscriminator;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.metamodel.binding.state.DiscriminatorBindingState;
 import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
 import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.AttributeContainer;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.UniqueKey;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.attribute.AssociationAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.AttributeOverride;
 import org.hibernate.metamodel.source.annotations.attribute.MappedAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.SimpleAttribute;
 import org.hibernate.metamodel.source.annotations.attribute.state.binding.AttributeBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.binding.DiscriminatorBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.binding.ManyToOneBindingStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.ColumnRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.ManyToOneRelationalStateImpl;
 import org.hibernate.metamodel.source.annotations.attribute.state.relational.TupleRelationalStateImpl;
-import org.hibernate.metamodel.source.annotations.entity.state.binding.EntityBindingStateImpl;
+import org.hibernate.metamodel.source.annotations.entity.state.binding.EntityViewImpl;
 import org.hibernate.metamodel.source.annotations.global.IdGeneratorBinder;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.persister.entity.EntityPersister;
 
 /**
  * Creates the domain and relational metamodel for a configured class and <i>binds</i> them together.
  *
  * @author Hardy Ferentschik
  */
 public class EntityBinder {
 	private final EntityClass entityClass;
 	private final MetadataImplementor meta;
 	private final Hierarchical superType;
 
 	public EntityBinder(MetadataImplementor metadata, EntityClass entityClass, Hierarchical superType) {
 		this.entityClass = entityClass;
 		this.meta = metadata;
 		this.superType = superType;
 	}
 
 	public EntityBinding bind() {
 		EntityBinding entityBinding = new EntityBinding();
-		EntityBindingStateImpl entityBindingState = new EntityBindingStateImpl( superType, entityClass );
+		EntityViewImpl entityBindingState = new EntityViewImpl( superType, entityClass );
 
 		bindJpaEntityAnnotation( entityBindingState );
 		bindHibernateEntityAnnotation( entityBindingState ); // optional hibernate specific @org.hibernate.annotations.Entity
 		bindTable( entityBinding );
 
 		// bind entity level annotations
 		bindWhereFilter( entityBindingState );
 		bindJpaCaching( entityBindingState );
 		bindHibernateCaching( entityBindingState );
 		bindProxy( entityBindingState );
 		bindSynchronize( entityBindingState );
 		bindCustomSQL( entityBindingState );
 		bindRowId( entityBindingState );
 		bindBatchSize( entityBindingState );
 		entityBinding.initialize( meta, entityBindingState );
 
 		bindInheritance( entityBinding );
 
 		// bind all attributes - simple as well as associations
 		bindAttributes( entityBinding );
 		bindEmbeddedAttributes( entityBinding );
 
 		// take care of the id, attributes and relations
 		if ( entityClass.isEntityRoot() ) {
 			bindId( entityBinding );
 		}
 
 		bindTableUniqueConstraints( entityBinding );
 
 		// last, but not least we initialize and register the new EntityBinding
 		meta.addEntity( entityBinding );
 		return entityBinding;
 	}
 
 	private void bindTableUniqueConstraints(EntityBinding entityBinding) {
 		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(),
 				JPADotNames.TABLE
 		);
 		if ( tableAnnotation == null ) {
 			return;
 		}
 		TableSpecification table = entityBinding.getBaseTable();
 		bindUniqueConstraints( tableAnnotation, table );
 	}
 
 	/**
 	 * Bind {@link javax.persistence.UniqueConstraint} to table as a {@link UniqueKey}
 	 *
 	 * @param tableAnnotation JPA annotations which has a {@code uniqueConstraints} attribute.
 	 * @param table Table which the UniqueKey bind to.
 	 */
 	private void bindUniqueConstraints(AnnotationInstance tableAnnotation, TableSpecification table) {
 		AnnotationValue value = tableAnnotation.value( "uniqueConstraints" );
 		if ( value == null ) {
 			return;
 		}
 		AnnotationInstance[] uniqueConstraints = value.asNestedArray();
 		for ( AnnotationInstance unique : uniqueConstraints ) {
 			String name = unique.value( "name" ).asString();
 			UniqueKey uniqueKey = table.getOrCreateUniqueKey( name );
 			String[] columnNames = unique.value( "columnNames" ).asStringArray();
 			if ( columnNames.length == 0 ) {
 				//todo throw exception?
 			}
 			for ( String columnName : columnNames ) {
 				uniqueKey.addColumn( table.getOrCreateColumn( columnName ) );
 			}
 		}
 	}
 
 	private void bindInheritance(EntityBinding entityBinding) {
 		entityBinding.setInheritanceType( entityClass.getInheritanceType() );
 		switch ( entityClass.getInheritanceType() ) {
 			case SINGLE_TABLE: {
 				bindDiscriminatorColumn( entityBinding );
 				break;
 			}
 			case JOINED: {
 				// todo
 				break;
 			}
 			case TABLE_PER_CLASS: {
 				// todo
 				break;
 			}
 			default: {
 				// do nothing
 			}
 		}
 	}
 
 	private void bindDiscriminatorColumn(EntityBinding entityBinding) {
 		final Map<DotName, List<AnnotationInstance>> typeAnnotations = JandexHelper.getTypeAnnotations(
 				entityClass.getClassInfo()
 		);
 		SimpleAttribute discriminatorAttribute = SimpleAttribute.createDiscriminatorAttribute( typeAnnotations );
 		bindSingleMappedAttribute( entityBinding, entityBinding.getEntity(), discriminatorAttribute );
 	}
 
-	private void bindWhereFilter(EntityBindingStateImpl entityBindingState) {
+	private void bindWhereFilter(EntityViewImpl entityBindingState) {
 		AnnotationInstance whereAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.WHERE
 		);
 		if ( whereAnnotation != null ) {
 			// no null check needed, it is a required attribute
 			entityBindingState.setWhereFilter( JandexHelper.getValue( whereAnnotation, "clause", String.class ) );
 		}
 	}
 
-	private void bindHibernateCaching(EntityBindingStateImpl entityBindingState) {
+	private void bindHibernateCaching(EntityViewImpl entityBindingState) {
 		AnnotationInstance cacheAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.CACHE
 		);
 		if ( cacheAnnotation == null ) {
 			return;
 		}
 
 		String region;
 		if ( cacheAnnotation.value( "region" ) != null ) {
 			region = cacheAnnotation.value( "region" ).asString();
 		}
 		else {
 			region = entityBindingState.getEntityName();
 		}
 
 		boolean cacheLazyProperties = true;
 		if ( cacheAnnotation.value( "include" ) != null ) {
 			String tmp = cacheAnnotation.value( "include" ).asString();
 			if ( "all".equalsIgnoreCase( tmp ) ) {
 				cacheLazyProperties = true;
 			}
 			else if ( "non-lazy".equalsIgnoreCase( tmp ) ) {
 				cacheLazyProperties = false;
 			}
 			else {
 				throw new AnnotationException( "Unknown lazy property annotations: " + tmp );
 			}
 		}
 
 		CacheConcurrencyStrategy strategy = CacheConcurrencyStrategy.valueOf(
 				cacheAnnotation.value( "usage" ).asEnum()
 		);
 		Caching caching = new Caching( region, strategy.toAccessType(), cacheLazyProperties );
 		entityBindingState.setCaching( caching );
 	}
 
 	// This does not take care of any inheritance of @Cacheable within a class hierarchy as specified in JPA2.
 	// This is currently not supported (HF)
-	private void bindJpaCaching(EntityBindingStateImpl entityBindingState) {
+	private void bindJpaCaching(EntityViewImpl entityBindingState) {
 		AnnotationInstance cacheAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.CACHEABLE
 		);
 
 		boolean cacheable = true; // true is the default
 		if ( cacheAnnotation != null && cacheAnnotation.value() != null ) {
 			cacheable = cacheAnnotation.value().asBoolean();
 		}
 
 		Caching caching = null;
 		switch ( meta.getOptions().getSharedCacheMode() ) {
 			case ALL: {
 				caching = createCachingForCacheableAnnotation( entityBindingState );
 				break;
 			}
 			case ENABLE_SELECTIVE: {
 				if ( cacheable ) {
 					caching = createCachingForCacheableAnnotation( entityBindingState );
 				}
 				break;
 			}
 			case DISABLE_SELECTIVE: {
 				if ( cacheAnnotation == null || cacheable ) {
 					caching = createCachingForCacheableAnnotation( entityBindingState );
 				}
 				break;
 			}
 			default: {
 				// treat both NONE and UNSPECIFIED the same
 				break;
 			}
 		}
 		if ( caching != null ) {
 			entityBindingState.setCaching( caching );
 		}
 	}
 
-	private void bindProxy(EntityBindingStateImpl entityBindingState) {
+	private void bindProxy(EntityViewImpl entityBindingState) {
 		AnnotationInstance proxyAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.PROXY
 		);
 		boolean lazy = true;
 		String proxyInterfaceClass = null;
 
 		if ( proxyAnnotation != null ) {
 			AnnotationValue lazyValue = proxyAnnotation.value( "lazy" );
 			if ( lazyValue != null ) {
 				lazy = lazyValue.asBoolean();
 			}
 
 			AnnotationValue proxyClassValue = proxyAnnotation.value( "proxyClass" );
 			if ( proxyClassValue != null ) {
 				proxyInterfaceClass = proxyClassValue.asString();
 			}
 		}
 
 		entityBindingState.setLazy( lazy );
 		entityBindingState.setProxyInterfaceName( proxyInterfaceClass );
 	}
 
-	private void bindSynchronize(EntityBindingStateImpl entityBindingState) {
+	private void bindSynchronize(EntityViewImpl entityBindingState) {
 		AnnotationInstance synchronizeAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SYNCHRONIZE
 		);
 
 		if ( synchronizeAnnotation != null ) {
 			String[] tableNames = synchronizeAnnotation.value().asStringArray();
 			for ( String tableName : tableNames ) {
 				entityBindingState.addSynchronizedTableName( tableName );
 			}
 		}
 	}
 
-	private void bindCustomSQL(EntityBindingStateImpl entityBindingState) {
+	private void bindCustomSQL(EntityViewImpl entityBindingState) {
 		AnnotationInstance sqlInsertAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_INSERT
 		);
 		entityBindingState.setCustomInsert( createCustomSQL( sqlInsertAnnotation ) );
 
 		AnnotationInstance sqlUpdateAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_UPDATE
 		);
 		entityBindingState.setCustomUpdate( createCustomSQL( sqlUpdateAnnotation ) );
 
 		AnnotationInstance sqlDeleteAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_DELETE
 		);
 		entityBindingState.setCustomDelete( createCustomSQL( sqlDeleteAnnotation ) );
 
 		AnnotationInstance sqlDeleteAllAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.SQL_DELETE_ALL
 		);
 		if ( sqlDeleteAllAnnotation != null ) {
 			entityBindingState.setCustomDelete( createCustomSQL( sqlDeleteAllAnnotation ) );
 		}
 	}
 
 	private CustomSQL createCustomSQL(AnnotationInstance customSQLAnnotation) {
 		if ( customSQLAnnotation == null ) {
 			return null;
 		}
 
 		String sql = customSQLAnnotation.value( "sql" ).asString();
 		boolean isCallable = false;
 		AnnotationValue callableValue = customSQLAnnotation.value( "callable" );
 		if ( callableValue != null ) {
 			isCallable = callableValue.asBoolean();
 		}
 
 		ResultCheckStyle checkStyle = ResultCheckStyle.NONE;
 		AnnotationValue checkStyleValue = customSQLAnnotation.value( "check" );
 		if ( checkStyleValue != null ) {
 			checkStyle = Enum.valueOf( ResultCheckStyle.class, checkStyleValue.asEnum() );
 		}
 
 		return new CustomSQL(
 				sql,
 				isCallable,
 				Enum.valueOf( ExecuteUpdateResultCheckStyle.class, checkStyle.toString() )
 		);
 	}
 
-	private void bindRowId(EntityBindingStateImpl entityBindingState) {
+	private void bindRowId(EntityViewImpl entityBindingState) {
 		AnnotationInstance rowIdAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ROW_ID
 		);
 
 		if ( rowIdAnnotation != null ) {
 			entityBindingState.setRowId( rowIdAnnotation.value().asString() );
 		}
 	}
 
-	private void bindBatchSize(EntityBindingStateImpl entityBindingState) {
+	private void bindBatchSize(EntityViewImpl entityBindingState) {
 		AnnotationInstance batchSizeAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.BATCH_SIZE
 		);
 
 		if ( batchSizeAnnotation != null ) {
 			entityBindingState.setBatchSize( batchSizeAnnotation.value( "size" ).asInt() );
 		}
 	}
 
-	private Caching createCachingForCacheableAnnotation(EntityBindingStateImpl entityBindingState) {
+	private Caching createCachingForCacheableAnnotation(EntityViewImpl entityBindingState) {
 		String region = entityBindingState.getEntityName();
 		RegionFactory regionFactory = meta.getServiceRegistry().getService( RegionFactory.class );
 		AccessType defaultAccessType = regionFactory.getDefaultAccessType();
 		return new Caching( region, defaultAccessType, true );
 	}
 
 	private Table createTable() {
 		String schemaName = null;
 		String catalogName = null;
 		String tableName = null;
 
 		// is there an explicit @Table annotation?
 		AnnotationInstance tableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.TABLE
 		);
 		if ( tableAnnotation != null ) {
 			schemaName = JandexHelper.getValue( tableAnnotation, "schema", String.class );
 			catalogName = JandexHelper.getValue( tableAnnotation, "catalog", String.class );
 			String explicitTableName = JandexHelper.getValue( tableAnnotation, "name", String.class );
 			if ( StringHelper.isNotEmpty( explicitTableName ) ) {
 				tableName = meta.getNamingStrategy().tableName( explicitTableName );
 			}
 		}
 
 		// no explicit table name given, let's use the entity name as table name (taking inheritance into consideration
 		if ( StringHelper.isEmpty( tableName ) ) {
 			tableName = meta.getNamingStrategy().classToTableName( entityClass.getClassNameForTable() );
 		}
 
 		// check whether the names should be globally quoted
 		if ( meta.isGloballyQuotedIdentifiers() ) {
 			schemaName = StringHelper.quote( schemaName );
 			catalogName = StringHelper.quote( catalogName );
 			tableName = StringHelper.quote( tableName );
 		}
 
 		// last, but not least create the metamodel relational objects
 		final Identifier tableNameIdentifier = Identifier.toIdentifier( tableName );
 		final Schema schema = meta.getDatabase().getSchema( new Schema.Name( schemaName, catalogName ) );
-		Table table = schema.getTable( tableNameIdentifier );
-		if ( table == null ) {
-			table = schema.createTable( tableNameIdentifier );
-		}
-		return table;
+		return schema.locateOrCreateTable( tableNameIdentifier );
 	}
 
 
 	private void bindTable(EntityBinding entityBinding) {
 		Table table = createTable();
 		entityBinding.setBaseTable( table );
 
 		AnnotationInstance checkAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.CHECK
 		);
 		if ( checkAnnotation != null ) {
 			table.addCheckConstraint( checkAnnotation.value( "constraints" ).asString() );
 		}
 	}
 
 	private void bindId(EntityBinding entityBinding) {
 		switch ( entityClass.getIdType() ) {
 			case SIMPLE: {
 				bindSingleIdAnnotation( entityBinding );
 				break;
 			}
 			case COMPOSED: {
 				// todo
 				break;
 			}
 			case EMBEDDED: {
 				bindEmbeddedIdAnnotation( entityBinding );
 				break;
 			}
 			default: {
 			}
 		}
 	}
 
-	private void bindJpaEntityAnnotation(EntityBindingStateImpl entityBindingState) {
+
+	private void bindJpaEntityAnnotation(EntityViewImpl entityBindingState) {
 		AnnotationInstance jpaEntityAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.ENTITY
 		);
 		String name;
 		if ( jpaEntityAnnotation.value( "name" ) == null ) {
 			name = entityClass.getClass().getSimpleName();
 		}
 		else {
 			name = jpaEntityAnnotation.value( "name" ).asString();
 		}
 		entityBindingState.setJpaEntityName( name );
 	}
 
 	private void bindEmbeddedIdAnnotation(EntityBinding entityBinding) {
 		AnnotationInstance idAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.EMBEDDED_ID
 		);
 
 		String idName = JandexHelper.getPropertyName( idAnnotation.target() );
 		MappedAttribute idAttribute = entityClass.getMappedAttribute( idName );
 		if ( !( idAttribute instanceof SimpleAttribute ) ) {
 			throw new AssertionFailure( "Unexpected attribute type for id attribute" );
 		}
 
 		SingularAttribute attribute = entityBinding.getEntity().getOrCreateComponentAttribute( idName );
 
 
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleIdAttributeBinding( attribute );
 
 		attributeBinding.initialize( new AttributeBindingStateImpl( (SimpleAttribute) idAttribute ) );
 
 		TupleRelationalStateImpl state = new TupleRelationalStateImpl();
 		EmbeddableClass embeddableClass = entityClass.getEmbeddedClasses().get( idName );
 		for ( SimpleAttribute attr : embeddableClass.getSimpleAttributes() ) {
 			state.addValueState( new ColumnRelationalStateImpl( attr, meta ) );
 		}
 		attributeBinding.initialize( state );
 		Map<String, String> parms = new HashMap<String, String>( 1 );
 		parms.put( IdentifierGenerator.ENTITY_NAME, entityBinding.getEntity().getName() );
 		IdGenerator generator = new IdGenerator( "NAME", "assigned", parms );
 		entityBinding.getEntityIdentifier().setIdGenerator( generator );
 		// entityBinding.getEntityIdentifier().createIdentifierGenerator( meta.getIdentifierGeneratorFactory() );
 	}
 
 	private void bindSingleIdAnnotation(EntityBinding entityBinding) {
 		// we know we are dealing w/ a single @Id, but potentially it is defined in a mapped super class
 		ConfiguredClass configuredClass = entityClass;
 		EntityClass superEntity = entityClass.getEntityParent();
 		Hierarchical container = entityBinding.getEntity();
 		Iterator<SimpleAttribute> iter = null;
 		while ( configuredClass != null && configuredClass != superEntity ) {
 			iter = configuredClass.getIdAttributes().iterator();
 			if ( iter.hasNext() ) {
 				break;
 			}
 			configuredClass = configuredClass.getParent();
 			container = container.getSuperType();
 		}
 
 		// if we could not find the attribute our assumptions were wrong
 		if ( iter == null || !iter.hasNext() ) {
 			throw new AnnotationException(
 					String.format(
 							"Unable to find id attribute for class %s",
 							entityClass.getName()
 					)
 			);
 		}
 
 		// now that we have the id attribute we can create the attribute and binding
 		MappedAttribute idAttribute = iter.next();
 		Attribute attribute = container.getOrCreateSingularAttribute( idAttribute.getName() );
 
 		SimpleAttributeBinding attributeBinding = entityBinding.makeSimpleIdAttributeBinding( attribute );
 		attributeBinding.initialize( new AttributeBindingStateImpl( (SimpleAttribute) idAttribute ) );
 		attributeBinding.initialize( new ColumnRelationalStateImpl( (SimpleAttribute) idAttribute, meta ) );
 		bindSingleIdGeneratedValue( entityBinding, idAttribute.getName() );
 	}
 
 	private void bindSingleIdGeneratedValue(EntityBinding entityBinding, String idPropertyName) {
 		AnnotationInstance generatedValueAnn = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), JPADotNames.GENERATED_VALUE
 		);
 		if ( generatedValueAnn == null ) {
 			return;
 		}
 
 		String idName = JandexHelper.getPropertyName( generatedValueAnn.target() );
 		if ( !idPropertyName.equals( idName ) ) {
 			throw new AssertionFailure(
 					String.format(
 							"Attribute[%s.%s] with @GeneratedValue doesn't have a @Id.",
 							entityClass.getName(),
 							idPropertyName
 					)
 			);
 		}
 		String generator = JandexHelper.getValue( generatedValueAnn, "generator", String.class );
 		IdGenerator idGenerator = null;
 		if ( StringHelper.isNotEmpty( generator ) ) {
 			idGenerator = meta.getIdGenerator( generator );
 			if ( idGenerator == null ) {
 				throw new MappingException(
 						String.format(
 								"@GeneratedValue on %s.%s referring an undefined generator [%s]",
 								entityClass.getName(),
 								idName,
 								generator
 						)
 				);
 			}
 			entityBinding.getEntityIdentifier().setIdGenerator( idGenerator );
 		}
 		GenerationType generationType = JandexHelper.getValueAsEnum(
 				generatedValueAnn,
 				"strategy",
 				GenerationType.class
 		);
 		String strategy = IdGeneratorBinder.generatorType(
 				generationType,
 				meta.getOptions().useNewIdentifierGenerators()
 		);
 		if ( idGenerator != null && !strategy.equals( idGenerator.getStrategy() ) ) {
 			//todo how to ?
 			throw new MappingException(
 					String.format(
 							"Inconsistent Id Generation strategy of @GeneratedValue on %s.%s",
 							entityClass.getName(),
 							idName
 					)
 			);
 		}
 		if ( idGenerator == null ) {
 			idGenerator = new IdGenerator( "NAME", strategy, new HashMap<String, String>() );
 			entityBinding.getEntityIdentifier().setIdGenerator( idGenerator );
 		}
 //        entityBinding.getEntityIdentifier().createIdentifierGenerator( meta.getIdentifierGeneratorFactory() );
 	}
 
 	private void bindAttributes(EntityBinding entityBinding) {
 		// collect attribute overrides as we map the attributes
 		Map<String, AttributeOverride> attributeOverrideMap = new HashMap<String, AttributeOverride>();
 
 		// bind the attributes of this entity
 		AttributeContainer entity = entityBinding.getEntity();
 		bindAttributes( entityBinding, entity, entityClass, attributeOverrideMap );
 
 		// bind potential mapped super class attributes
 		attributeOverrideMap.putAll( entityClass.getAttributeOverrideMap() );
 		ConfiguredClass parent = entityClass.getParent();
 		Hierarchical superTypeContainer = entityBinding.getEntity().getSuperType();
 		while ( containsMappedSuperclassAttributes( parent ) ) {
 			bindAttributes( entityBinding, superTypeContainer, parent, attributeOverrideMap );
 			addNewOverridesToMap( parent, attributeOverrideMap );
 			parent = parent.getParent();
 			superTypeContainer = superTypeContainer.getSuperType();
 		}
 	}
 
 	private void addNewOverridesToMap(ConfiguredClass parent, Map<String, AttributeOverride> attributeOverrideMap) {
 		Map<String, AttributeOverride> overrides = parent.getAttributeOverrideMap();
 		for ( Map.Entry<String, AttributeOverride> entry : overrides.entrySet() ) {
 			if ( !attributeOverrideMap.containsKey( entry.getKey() ) ) {
 				attributeOverrideMap.put( entry.getKey(), entry.getValue() );
 			}
 		}
 	}
 
 	private boolean containsMappedSuperclassAttributes(ConfiguredClass parent) {
 		return parent != null && ( ConfiguredClassType.MAPPED_SUPERCLASS.equals( parent.getConfiguredClassType() ) ||
 				ConfiguredClassType.NON_ENTITY.equals( parent.getConfiguredClassType() ) );
 	}
 
 	/**
 	 * Creates attribute bindings for the attributes of {@code configuredClass}
 	 *
 	 * @param entityBinding The entity binding for the class we are currently binding
 	 * @param attributeContainer The domain attribute container to which to add the attribute (could be the entity itself, or a mapped super class
 	 * or a component)
 	 * @param configuredClass the configured containing the attributes to be bound
 	 * @param attributeOverrideMap a map with the accumulated attribute overrides
 	 */
 	private void bindAttributes(EntityBinding entityBinding, AttributeContainer attributeContainer, ConfiguredClass configuredClass, Map<String, AttributeOverride> attributeOverrideMap) {
 		for ( SimpleAttribute simpleAttribute : configuredClass.getSimpleAttributes() ) {
 			String attributeName = simpleAttribute.getName();
 
 			// if there is a override apply it
 			AttributeOverride override = attributeOverrideMap.get( attributeName );
 			if ( override != null ) {
 				simpleAttribute = SimpleAttribute.createSimpleAttribute( simpleAttribute, override.getColumnValues() );
 			}
 
 			bindSingleMappedAttribute(
 					entityBinding,
 					attributeContainer,
 					simpleAttribute
 			);
 		}
 		for ( AssociationAttribute associationAttribute : configuredClass.getAssociationAttributes() ) {
 			bindAssociationAttribute(
 					entityBinding,
 					attributeContainer,
 					associationAttribute
 			);
 		}
 	}
 
 	private void bindEmbeddedAttributes(EntityBinding entityBinding) {
 		AttributeContainer entity = entityBinding.getEntity();
 		bindEmbeddedAttributes( entityBinding, entity, entityClass );
 
 		// bind potential mapped super class embeddables
 		ConfiguredClass parent = entityClass.getParent();
 		Hierarchical superTypeContainer = entityBinding.getEntity().getSuperType();
 		while ( containsMappedSuperclassAttributes( parent ) ) {
 			bindEmbeddedAttributes( entityBinding, superTypeContainer, parent );
 			parent = parent.getParent();
 			superTypeContainer = superTypeContainer.getSuperType();
 		}
 	}
 
 	private void bindEmbeddedAttributes(EntityBinding entityBinding, AttributeContainer attributeContainer, ConfiguredClass configuredClass) {
 		for ( Map.Entry<String, EmbeddableClass> entry : configuredClass.getEmbeddedClasses().entrySet() ) {
 			String attributeName = entry.getKey();
 			EmbeddableClass embeddedClass = entry.getValue();
 			SingularAttribute component = attributeContainer.getOrCreateComponentAttribute( attributeName );
 			for ( SimpleAttribute simpleAttribute : embeddedClass.getSimpleAttributes() ) {
 				bindSingleMappedAttribute(
 						entityBinding,
 						component.getAttributeContainer(),
 						simpleAttribute
 				);
 			}
 			for ( AssociationAttribute associationAttribute : embeddedClass.getAssociationAttributes() ) {
 				bindAssociationAttribute(
 						entityBinding,
 						component.getAttributeContainer(),
 						associationAttribute
 				);
 			}
 		}
 	}
 
 	private void bindAssociationAttribute(EntityBinding entityBinding, AttributeContainer container, AssociationAttribute associationAttribute) {
 		switch ( associationAttribute.getAssociationType() ) {
 			case MANY_TO_ONE: {
 				container.getOrCreateSingularAttribute( associationAttribute.getName() );
 				ManyToOneAttributeBinding manyToOneAttributeBinding = entityBinding.makeManyToOneAttributeBinding(
 						associationAttribute.getName()
 				);
 
 				ManyToOneAttributeBindingState bindingState = new ManyToOneBindingStateImpl( associationAttribute );
 				manyToOneAttributeBinding.initialize( bindingState );
 
 				ManyToOneRelationalStateImpl relationalState = new ManyToOneRelationalStateImpl();
 				if ( entityClass.hasOwnTable() ) {
 					ColumnRelationalStateImpl columnRelationsState = new ColumnRelationalStateImpl(
 							associationAttribute, meta
 					);
 					relationalState.addValueState( columnRelationsState );
 				}
 				manyToOneAttributeBinding.initialize( relationalState );
 				break;
 			}
 			default: {
 				// todo
 			}
 		}
 	}
 
 	private void bindSingleMappedAttribute(EntityBinding entityBinding, AttributeContainer container, SimpleAttribute simpleAttribute) {
 		if ( simpleAttribute.isId() ) {
 			return;
 		}
 
 		Attribute attribute = container.getOrCreateSingularAttribute( simpleAttribute.getName() );
 		SimpleAttributeBinding attributeBinding;
 
 		if ( simpleAttribute.isDiscriminator() ) {
 			EntityDiscriminator entityDiscriminator = entityBinding.makeEntityDiscriminator( attribute );
 			DiscriminatorBindingState bindingState = new DiscriminatorBindingStateImpl( simpleAttribute );
 			entityDiscriminator.initialize( bindingState );
 			attributeBinding = entityDiscriminator.getValueBinding();
 		}
 		else if ( simpleAttribute.isVersioned() ) {
 			attributeBinding = entityBinding.makeVersionBinding( attribute );
 			SimpleAttributeBindingState bindingState = new AttributeBindingStateImpl( simpleAttribute );
 			attributeBinding.initialize( bindingState );
 		}
 		else {
 			attributeBinding = entityBinding.makeSimpleAttributeBinding( attribute );
 			SimpleAttributeBindingState bindingState = new AttributeBindingStateImpl( simpleAttribute );
 			attributeBinding.initialize( bindingState );
 		}
 
 		if ( entityClass.hasOwnTable() ) {
 			ColumnRelationalStateImpl columnRelationsState = new ColumnRelationalStateImpl(
 					simpleAttribute, meta
 			);
 			TupleRelationalStateImpl relationalState = new TupleRelationalStateImpl();
 			relationalState.addValueState( columnRelationsState );
 
 			attributeBinding.initialize( relationalState );
 		}
 	}
 
-	private void bindHibernateEntityAnnotation(EntityBindingStateImpl entityBindingState) {
+	private void bindHibernateEntityAnnotation(EntityViewImpl entityBindingState) {
 		// initialize w/ the defaults
 		boolean mutable = true;
 		boolean dynamicInsert = false;
 		boolean dynamicUpdate = false;
 		boolean selectBeforeUpdate = false;
 		PolymorphismType polymorphism = PolymorphismType.IMPLICIT;
 		OptimisticLockType optimisticLock = OptimisticLockType.VERSION;
 
 		AnnotationInstance hibernateEntityAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ENTITY
 		);
 
 		if ( hibernateEntityAnnotation != null ) {
 			if ( hibernateEntityAnnotation.value( "mutable" ) != null ) {
 				mutable = hibernateEntityAnnotation.value( "mutable" ).asBoolean();
 			}
 
 			if ( hibernateEntityAnnotation.value( "dynamicInsert" ) != null ) {
 				dynamicInsert = hibernateEntityAnnotation.value( "dynamicInsert" ).asBoolean();
 			}
 
 			if ( hibernateEntityAnnotation.value( "dynamicUpdate" ) != null ) {
 				dynamicUpdate = hibernateEntityAnnotation.value( "dynamicUpdate" ).asBoolean();
 			}
 
 			if ( hibernateEntityAnnotation.value( "selectBeforeUpdate" ) != null ) {
 				selectBeforeUpdate = hibernateEntityAnnotation.value( "selectBeforeUpdate" ).asBoolean();
 			}
 
 			if ( hibernateEntityAnnotation.value( "polymorphism" ) != null ) {
 				polymorphism = PolymorphismType.valueOf( hibernateEntityAnnotation.value( "polymorphism" ).asEnum() );
 			}
 
 			if ( hibernateEntityAnnotation.value( "optimisticLock" ) != null ) {
 				optimisticLock = OptimisticLockType.valueOf(
 						hibernateEntityAnnotation.value( "optimisticLock" ).asEnum()
 				);
 			}
 
 			if ( hibernateEntityAnnotation.value( "persister" ) != null ) {
 				final String persisterClassName = ( hibernateEntityAnnotation.value( "persister" ).toString() );
 				entityBindingState.setPersisterClass( meta.<EntityPersister>locateClassByName( persisterClassName ) );
 			}
 		}
 
 		// also check for the immutable annotation
 		AnnotationInstance immutableAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.IMMUTABLE
 		);
 		if ( immutableAnnotation != null ) {
 			mutable = false;
 		}
 
 		entityBindingState.setMutable( mutable );
 		entityBindingState.setDynamicInsert( dynamicInsert );
 		entityBindingState.setDynamicUpdate( dynamicUpdate );
 		entityBindingState.setSelectBeforeUpdate( selectBeforeUpdate );
 		entityBindingState.setExplicitPolymorphism( PolymorphismType.EXPLICIT.equals( polymorphism ) );
 		entityBindingState.setOptimisticLock( optimisticLock );
 	}
 }
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityBindingStateImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityViewImpl.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityBindingStateImpl.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityViewImpl.java
index 287b56df45..0b383bc376 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityBindingStateImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityViewImpl.java
@@ -1,309 +1,309 @@
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
 package org.hibernate.metamodel.source.annotations.entity.state.binding;
 
 import java.util.HashSet;
 import java.util.Set;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.annotations.OptimisticLockType;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
-import org.hibernate.metamodel.binding.state.EntityBindingState;
+import org.hibernate.metamodel.binder.view.EntityView;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.source.annotations.entity.EntityClass;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * @author Hardy Ferentschik
  */
-public class EntityBindingStateImpl implements EntityBindingState {
+public class EntityViewImpl implements EntityView {
 	private String entityName;
 
 	private final String className;
 	private String proxyInterfaceName;
 
 	private final Hierarchical superType;
 	private final boolean isRoot;
 	private final InheritanceType inheritanceType;
 
 
 	private Caching caching;
 
 	private boolean mutable;
 	private boolean explicitPolymorphism;
 	private String whereFilter;
 	private String rowId;
 
 	private boolean dynamicUpdate;
 	private boolean dynamicInsert;
 
 	private int batchSize;
 	private boolean selectBeforeUpdate;
 	private OptimisticLockType optimisticLock;
 
 	private Class<EntityPersister> persisterClass;
 
 	private boolean lazy;
 
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private Set<String> synchronizedTableNames;
 
-	public EntityBindingStateImpl(Hierarchical superType, EntityClass entityClass) {
+	public EntityViewImpl(Hierarchical superType, EntityClass entityClass) {
 		this.className = entityClass.getName();
 		this.superType = superType;
 		this.isRoot = entityClass.isEntityRoot();
 		this.inheritanceType = entityClass.getInheritanceType();
 		this.synchronizedTableNames = new HashSet<String>();
 		this.batchSize = -1;
 	}
 
 	@Override
 	public String getJpaEntityName() {
 		return entityName;
 	}
 
 	public void setJpaEntityName(String entityName) {
 		this.entityName = entityName;
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return EntityMode.POJO;
 	}
 
 	public String getEntityName() {
 		return className;
 	}
 
 	@Override
 	public String getClassName() {
 		return className;
 	}
 
 	@Override
 	public Class<EntityTuplizer> getCustomEntityTuplizerClass() {
 		return null; // todo : implement method body
 	}
 
 	@Override
 	public Hierarchical getSuperType() {
 		return superType;
 	}
 
 	public void setCaching(Caching caching) {
 		this.caching = caching;
 	}
 
 	public void setMutable(boolean mutable) {
 		this.mutable = mutable;
 	}
 
 	public void setExplicitPolymorphism(boolean explicitPolymorphism) {
 		this.explicitPolymorphism = explicitPolymorphism;
 	}
 
 	public void setWhereFilter(String whereFilter) {
 		this.whereFilter = whereFilter;
 	}
 
 	public void setDynamicUpdate(boolean dynamicUpdate) {
 		this.dynamicUpdate = dynamicUpdate;
 	}
 
 	public void setDynamicInsert(boolean dynamicInsert) {
 		this.dynamicInsert = dynamicInsert;
 	}
 
 	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
 		this.selectBeforeUpdate = selectBeforeUpdate;
 	}
 
 	public void setOptimisticLock(OptimisticLockType optimisticLock) {
 		this.optimisticLock = optimisticLock;
 	}
 
 	public void setPersisterClass(Class<EntityPersister> persisterClass) {
 		this.persisterClass = persisterClass;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public void setProxyInterfaceName(String proxyInterfaceName) {
 		this.proxyInterfaceName = proxyInterfaceName;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public void setBatchSize(int batchSize) {
 		this.batchSize = batchSize;
 	}
 
 	public void addSynchronizedTableName(String tableName) {
 		synchronizedTableNames.add( tableName );
 	}
 
 	public void setCustomInsert(CustomSQL customInsert) {
 		this.customInsert = customInsert;
 	}
 
 	public void setCustomUpdate(CustomSQL customUpdate) {
 		this.customUpdate = customUpdate;
 	}
 
 	public void setCustomDelete(CustomSQL customDelete) {
 		this.customDelete = customDelete;
 	}
 
 	@Override
 	public boolean isRoot() {
 		return isRoot;
 
 	}
 
 	@Override
 	public InheritanceType getEntityInheritanceType() {
 		return inheritanceType;
 	}
 
 	@Override
 	public Caching getCaching() {
 		return caching;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		// not needed for annotations!? (HF)
 		return null;
 	}
 
 	@Override
 	public String getProxyInterfaceName() {
 		return proxyInterfaceName;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	@Override
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	@Override
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	@Override
 	public String getWhereFilter() {
 		return whereFilter;
 	}
 
 	@Override
 	public String getRowId() {
 		return rowId;
 	}
 
 	@Override
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	@Override
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	@Override
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	@Override
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	@Override
 	public int getOptimisticLockMode() {
         switch ( optimisticLock ){
             case ALL:
                 return Versioning.OPTIMISTIC_LOCK_ALL;
             case NONE:
                 return Versioning.OPTIMISTIC_LOCK_NONE;
             case DIRTY:
                 return Versioning.OPTIMISTIC_LOCK_DIRTY;
             case VERSION:
                 return Versioning.OPTIMISTIC_LOCK_VERSION;
             default:
                 throw new AssertionFailure( "Unexpected optimistic lock type: " + optimisticLock );
         }
 	}
 
 	@Override
 	public Class<EntityPersister> getCustomEntityPersisterClass() {
 		return persisterClass;
 	}
 
 	@Override
 	public Boolean isAbstract() {
 		// no annotations equivalent
 		return false;
 	}
 
 	@Override
 	public CustomSQL getCustomInsert() {
 		return customInsert;
 	}
 
 	@Override
 	public CustomSQL getCustomUpdate() {
 		return customUpdate;
 	}
 
 	@Override
 	public CustomSQL getCustomDelete() {
 		return customDelete;
 	}
 
 	@Override
 	public Set<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TableBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TableBinder.java
index e2894b7e66..8f3e0054e6 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TableBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TableBinder.java
@@ -1,126 +1,126 @@
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
 package org.hibernate.metamodel.source.annotations.global;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.Index;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.ObjectName;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 
 /**
  * Binds table related information. This binder is called after the entities are bound.
  *
  * @author Hardy Ferentschik
  */
 public class TableBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			TableBinder.class.getName()
 	);
 
 	/**
 	 * Binds {@link org.hibernate.annotations.Tables} and {@link org.hibernate.annotations.Table} annotations to the supplied
 	 * metadata.
 	 *
 	 * @param metadata the global metadata
 	 * @param jandex the annotation index repository
 	 */
 	public static void bind(MetadataImplementor metadata, Index jandex) {
 		for ( AnnotationInstance tableAnnotation : jandex.getAnnotations( HibernateDotNames.TABLE ) ) {
 			bind( metadata, tableAnnotation );
 		}
 		for ( AnnotationInstance tables : jandex.getAnnotations( HibernateDotNames.TABLES ) ) {
 			for ( AnnotationInstance table : JandexHelper.getValue( tables, "value", AnnotationInstance[].class ) ) {
 				bind( metadata, table );
 			}
 		}
 	}
 
 	private static void bind(MetadataImplementor metadata, AnnotationInstance tableAnnotation) {
 		String tableName = JandexHelper.getValue( tableAnnotation, "appliesTo", String.class );
 		ObjectName objectName = new ObjectName( tableName );
 		Schema schema = metadata.getDatabase().getSchema( objectName.getSchema(), objectName.getCatalog() );
-		Table table = schema.getTable( objectName.getName() );
+		Table table = schema.locateTable( objectName.getName() );
 		if ( table != null ) {
 			bindHibernateTableAnnotation( table, tableAnnotation );
 		}
 	}
 
 	private static void bindHibernateTableAnnotation(Table table, AnnotationInstance tableAnnotation) {
 		for ( AnnotationInstance indexAnnotation : JandexHelper.getValue(
 				tableAnnotation,
 				"indexes",
 				AnnotationInstance[].class
 		) ) {
 			bindIndexAnnotation( table, indexAnnotation );
 		}
 		String comment = JandexHelper.getValue( tableAnnotation, "comment", String.class );
 		if ( StringHelper.isNotEmpty( comment ) ) {
 			table.addComment( comment.trim() );
 		}
 	}
 
 	private static void bindIndexAnnotation(Table table, AnnotationInstance indexAnnotation) {
 		String indexName = JandexHelper.getValue( indexAnnotation, "appliesTo", String.class );
 		String[] columnNames = JandexHelper.getValue( indexAnnotation, "columnNames", String[].class );
 		if ( columnNames == null ) {
 			LOG.noColumnsSpecifiedForIndex( indexName, table.toLoggableString() );
 			return;
 		}
 		org.hibernate.metamodel.relational.Index index = table.getOrCreateIndex( indexName );
 		for ( String columnName : columnNames ) {
 			Column column = findColumn( table, columnName );
 			if ( column == null ) {
 				throw new AnnotationException( "@Index references a unknown column: " + columnName );
 			}
 			index.addColumn( column );
 		}
 	}
 
 	private static Column findColumn(Table table, String columnName) {
 		Column column = null;
 		for ( SimpleValue value : table.values() ) {
 			if ( value instanceof Column && ( (Column) value ).getColumnName().getName().equals( columnName ) ) {
 				column = (Column) value;
 				break;
 			}
 		}
 		return column;
 	}
 
 	private TableBinder() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
index e82899f77d..ca7f58397a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
@@ -1,541 +1,541 @@
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
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.internal.util.StringHelper;
+import org.hibernate.metamodel.binder.view.hbm.EntityViewImpl;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.BagBinding;
 import org.hibernate.metamodel.binding.CollectionElementType;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
 import org.hibernate.metamodel.binding.state.PluralAttributeBindingState;
 import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.UniqueKey;
 import org.hibernate.metamodel.relational.state.ManyToOneRelationalState;
 import org.hibernate.metamodel.relational.state.TupleRelationalState;
 import org.hibernate.metamodel.relational.state.ValueRelationalState;
-import org.hibernate.metamodel.source.hbm.state.binding.HbmEntityBindingState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmManyToOneAttributeBindingState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmPluralAttributeBindingState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmSimpleAttributeBindingState;
 import org.hibernate.metamodel.source.hbm.state.relational.HbmManyToOneRelationalStateContainer;
 import org.hibernate.metamodel.source.hbm.state.relational.HbmSimpleValueRelationalStateContainer;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLAnyElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLBagElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLComponentElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLDynamicComponentElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLFilterElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLIdbagElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLJoinElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLListElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLManyToOneElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLMapElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLOneToOneElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLPropertiesElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLPropertyElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLResultsetElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSetElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLTuplizerElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLUnionSubclassElement;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 abstract class AbstractEntityBinder {
 	private final HbmBindingContext bindingContext;
 	private final Schema.Name schemaName;
 
 	AbstractEntityBinder(HbmBindingContext bindingContext, XMLHibernateMapping.XMLClass entityClazz) {
 		this.bindingContext = bindingContext;
 		this.schemaName = new Schema.Name(
 				entityClazz.getSchema() == null
 						? bindingContext.getMappingDefaults().getSchemaName()
 						: entityClazz.getSchema(),
 				entityClazz.getCatalog() == null
 						? bindingContext.getMappingDefaults().getCatalogName() :
 						entityClazz.getCatalog()
 		);
 	}
 
 	public boolean isRoot() {
 		return false;
 	}
 
 	public abstract InheritanceType getInheritanceType();
 
 	public HbmBindingContext getBindingContext() {
 		return bindingContext;
 	}
 
 	protected MetadataImplementor getMetadata() {
 		return bindingContext.getMetadataImplementor();
 	}
 
 	protected Schema.Name getSchemaName() {
 		return schemaName;
 	}
 
 	protected NamingStrategy getNamingStrategy() {
 		return getMetadata().getOptions().getNamingStrategy();
 	}
 
 	protected void basicEntityBinding(
 			XMLHibernateMapping.XMLClass entityClazz,
 			EntityBinding entityBinding,
 			Hierarchical superType) {
 		entityBinding.initialize(
 				bindingContext,
-				new HbmEntityBindingState(
+				new EntityViewImpl(
 						superType,
 						entityClazz,
 						isRoot(),
 						getInheritanceType(),
 						bindingContext
 				)
 		);
 
 		final String entityName = entityBinding.getEntity().getName();
 
 		if ( entityClazz.getFetchProfile() != null ) {
 			bindingContext.bindFetchProfiles( entityClazz.getFetchProfile(), entityName );
 		}
 
 		getMetadata().addImport( entityName, entityName );
 		if ( bindingContext.isAutoImport() ) {
 			if ( entityName.indexOf( '.' ) > 0 ) {
 				getMetadata().addImport( StringHelper.unqualify( entityName ), entityName );
 			}
 		}
 	}
 
 	protected String getDefaultAccess() {
 		return bindingContext.getMappingDefaults().getPropertyAccessorName();
 	}
 
 	/**
 	 * Locate any explicit tuplizer definition in the metadata, for the given entity-mode.
 	 *
 	 * @param container The containing element (representing the entity/component)
 	 * @param entityMode The entity-mode for which to locate the tuplizer element
 	 *
 	 * @return The tuplizer element, or null.
 	 */
 	private static XMLTuplizerElement locateTuplizerDefinition(XMLHibernateMapping.XMLClass container,
 															   EntityMode entityMode) {
 		for ( XMLTuplizerElement tuplizer : container.getTuplizer() ) {
 			if ( entityMode.toString().equals( tuplizer.getEntityMode() ) ) {
 				return tuplizer;
 			}
 		}
 		return null;
 	}
 
 	protected String getClassTableName(
 			XMLHibernateMapping.XMLClass entityClazz,
 			EntityBinding entityBinding,
 			Table denormalizedSuperTable) {
 		final String entityName = entityBinding.getEntity().getName();
 		String logicalTableName;
 		String physicalTableName;
 		if ( entityClazz.getTable() == null ) {
 			logicalTableName = StringHelper.unqualify( entityName );
 			physicalTableName = getMetadata()
 					.getOptions()
 					.getNamingStrategy()
 					.classToTableName( entityName );
 		}
 		else {
 			logicalTableName = entityClazz.getTable();
 			physicalTableName = getMetadata()
 					.getOptions()
 					.getNamingStrategy()
 					.tableName( logicalTableName );
 		}
 // todo : find out the purpose of these logical bindings
 //			mappings.addTableBinding( schema, catalog, logicalTableName, physicalTableName, denormalizedSuperTable );
 		return physicalTableName;
 	}
 
 	protected void buildAttributeBindings(XMLHibernateMapping.XMLClass entityClazz,
 										  EntityBinding entityBinding) {
 		// null = UniqueKey (we are not binding a natural-id mapping)
 		// true = mutable, by default properties are mutable
 		// true = nullable, by default properties are nullable.
 		buildAttributeBindings( entityClazz, entityBinding, null, true, true );
 	}
 
 	/**
 	 * This form is essentially used to create natural-id mappings.  But the processing is the same, aside from these
 	 * extra parameterized values, so we encapsulate it here.
 	 *
 	 * @param entityClazz
 	 * @param entityBinding
 	 * @param uniqueKey
 	 * @param mutable
 	 * @param nullable
 	 */
 	protected void buildAttributeBindings(
 			XMLHibernateMapping.XMLClass entityClazz,
 			EntityBinding entityBinding,
 			UniqueKey uniqueKey,
 			boolean mutable,
 			boolean nullable) {
 		final boolean naturalId = uniqueKey != null;
 
 		final String entiytName = entityBinding.getEntity().getName();
 		final TableSpecification tabe = entityBinding.getBaseTable();
 
 		AttributeBinding attributeBinding = null;
 		for ( Object attribute : entityClazz.getPropertyOrManyToOneOrOneToOne() ) {
 			if ( XMLBagElement.class.isInstance( attribute ) ) {
 				XMLBagElement collection = XMLBagElement.class.cast( attribute );
 				BagBinding collectionBinding = makeBagAttributeBinding( collection, entityBinding );
 				bindingContext.getMetadataImplementor().addCollection( collectionBinding );
 				attributeBinding = collectionBinding;
 			}
 			else if ( XMLIdbagElement.class.isInstance( attribute ) ) {
 				XMLIdbagElement collection = XMLIdbagElement.class.cast( attribute );
 				//BagBinding collectionBinding = entityBinding.makeBagAttributeBinding( collection.getName() );
 				//bindIdbag( collection, bagBinding, entityBinding, PluralAttributeNature.BAG, collection.getName() );
 				// todo: handle identifier
 				//attributeBinding = collectionBinding;
 				//hibernateMappingBinder.getHibernateXmlBinder().getMetadata().addCollection( attributeBinding );
 			}
 			else if ( XMLSetElement.class.isInstance( attribute ) ) {
 				XMLSetElement collection = XMLSetElement.class.cast( attribute );
 				//BagBinding collectionBinding = entityBinding.makeBagAttributeBinding( collection.getName() );
 				//bindSet( collection, collectionBinding, entityBinding, PluralAttributeNature.SET, collection.getName() );
 				//attributeBinding = collectionBinding;
 				//hibernateMappingBinder.getHibernateXmlBinder().getMetadata().addCollection( attributeBinding );
 			}
 			else if ( XMLListElement.class.isInstance( attribute ) ) {
 				XMLListElement collection = XMLListElement.class.cast( attribute );
 				//ListBinding collectionBinding = entityBinding.makeBagAttributeBinding( collection.getName() );
 				//bindList( collection, bagBinding, entityBinding, PluralAttributeNature.LIST, collection.getName() );
 				// todo : handle list index
 				//attributeBinding = collectionBinding;
 				//hibernateMappingBinder.getHibernateXmlBinder().getMetadata().addCollection( attributeBinding );
 			}
 			else if ( XMLMapElement.class.isInstance( attribute ) ) {
 				XMLMapElement collection = XMLMapElement.class.cast( attribute );
 				//BagBinding bagBinding = entityBinding.makeBagAttributeBinding( collection.getName() );
 				//bindMap( collection, bagBinding, entityBinding, PluralAttributeNature.MAP, collection.getName() );
 				// todo : handle map key
 				//hibernateMappingBinder.getHibernateXmlBinder().getMetadata().addCollection( attributeBinding );
 			}
 			else if ( XMLManyToOneElement.class.isInstance( attribute ) ) {
 				XMLManyToOneElement manyToOne = XMLManyToOneElement.class.cast( attribute );
 				attributeBinding = makeManyToOneAttributeBinding( manyToOne, entityBinding );
 			}
 			else if ( XMLAnyElement.class.isInstance( attribute ) ) {
 // todo : implement
 //				value = new Any( mappings, table );
 //				bindAny( subElement, (Any) value, nullable, mappings );
 			}
 			else if ( XMLOneToOneElement.class.isInstance( attribute ) ) {
 // todo : implement
 //				value = new OneToOne( mappings, table, persistentClass );
 //				bindOneToOne( subElement, (OneToOne) value, propertyName, true, mappings );
 			}
 			else if ( XMLPropertyElement.class.isInstance( attribute ) ) {
 				XMLPropertyElement property = XMLPropertyElement.class.cast( attribute );
 				attributeBinding = bindProperty( property, entityBinding );
 			}
 			else if ( XMLComponentElement.class.isInstance( attribute )
 					|| XMLDynamicComponentElement.class.isInstance( attribute )
 					|| XMLPropertiesElement.class.isInstance( attribute ) ) {
 // todo : implement
 //				String subpath = StringHelper.qualify( entityName, propertyName );
 //				value = new Component( mappings, persistentClass );
 //
 //				bindComponent(
 //						subElement,
 //						(Component) value,
 //						persistentClass.getClassName(),
 //						propertyName,
 //						subpath,
 //						true,
 //						"properties".equals( subElementName ),
 //						mappings,
 //						inheritedMetas,
 //						false
 //					);
 			}
 		}
 
 		/*
 Array
 PrimitiveArray
 */
 		for ( XMLJoinElement join : entityClazz.getJoin() ) {
 // todo : implement
 //			Join join = new Join();
 //			join.setPersistentClass( persistentClass );
 //			bindJoin( subElement, join, mappings, inheritedMetas );
 //			persistentClass.addJoin( join );
 		}
 		for ( XMLSubclassElement subclass : entityClazz.getSubclass() ) {
 // todo : implement
 //			handleSubclass( persistentClass, mappings, subElement, inheritedMetas );
 		}
 		for ( XMLJoinedSubclassElement subclass : entityClazz.getJoinedSubclass() ) {
 // todo : implement
 //			handleJoinedSubclass( persistentClass, mappings, subElement, inheritedMetas );
 		}
 		for ( XMLUnionSubclassElement subclass : entityClazz.getUnionSubclass() ) {
 // todo : implement
 //			handleUnionSubclass( persistentClass, mappings, subElement, inheritedMetas );
 		}
 		for ( XMLFilterElement filter : entityClazz.getFilter() ) {
 // todo : implement
 //				parseFilter( subElement, entityBinding );
 		}
 		if ( entityClazz.getNaturalId() != null ) {
 // todo : implement
 //				UniqueKey uk = new UniqueKey();
 //				uk.setName("_UniqueKey");
 //				uk.setTable(table);
 //				//by default, natural-ids are "immutable" (constant)
 //				boolean mutableId = "true".equals( subElement.attributeValue("mutable") );
 //				createClassProperties(
 //						subElement,
 //						persistentClass,
 //						mappings,
 //						inheritedMetas,
 //						uk,
 //						mutableId,
 //						false,
 //						true
 //					);
 //				table.addUniqueKey(uk);
 		}
 		if ( entityClazz.getQueryOrSqlQuery() != null ) {
 			for ( Object queryOrSqlQuery : entityClazz.getQueryOrSqlQuery() ) {
 				if ( XMLQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 // todo : implement
 //				bindNamedQuery(subElement, persistentClass.getEntityName(), mappings);
 				}
 				else if ( XMLSqlQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 // todo : implement
 //			bindNamedSQLQuery(subElement, persistentClass.getEntityName(), mappings);
 				}
 			}
 		}
 		if ( entityClazz.getResultset() != null ) {
 			for ( XMLResultsetElement resultSet : entityClazz.getResultset() ) {
 // todo : implement
 //				bindResultSetMappingDefinition( subElement, persistentClass.getEntityName(), mappings );
 			}
 		}
 //			if ( value != null ) {
 //				Property property = createProperty( value, propertyName, persistentClass
 //					.getClassName(), subElement, mappings, inheritedMetas );
 //				if ( !mutable ) property.setUpdateable(false);
 //				if ( naturalId ) property.setNaturalIdentifier(true);
 //				persistentClass.addProperty( property );
 //				if ( uniqueKey!=null ) uniqueKey.addColumns( property.getColumnIterator() );
 //			}
 
 	}
 
 	protected SimpleAttributeBinding bindProperty(
 			XMLPropertyElement property,
 			EntityBinding entityBinding) {
 		SimpleAttributeBindingState bindingState = new HbmSimpleAttributeBindingState(
 				entityBinding.getEntity().getJavaType().getName(),
 				bindingContext,
 				entityBinding.getMetaAttributeContext(),
 				property
 		);
 
 		// boolean (true here) indicates that by default column names should be guessed
 		ValueRelationalState relationalState =
 				convertToSimpleValueRelationalStateIfPossible(
 						new HbmSimpleValueRelationalStateContainer(
 								bindingContext,
 								true,
 								property
 						)
 				);
 
 		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		return entityBinding.makeSimpleAttributeBinding( attribute )
 				.initialize( bindingState )
 				.initialize( relationalState );
 	}
 
 	protected static ValueRelationalState convertToSimpleValueRelationalStateIfPossible(ValueRelationalState state) {
 		// TODO: should a single-valued tuple always be converted???
 		if ( !TupleRelationalState.class.isInstance( state ) ) {
 			return state;
 		}
 		TupleRelationalState tupleRelationalState = TupleRelationalState.class.cast( state );
 		return tupleRelationalState.getRelationalStates().size() == 1 ?
 				tupleRelationalState.getRelationalStates().get( 0 ) :
 				state;
 	}
 
 	protected BagBinding makeBagAttributeBinding(
 			XMLBagElement collection,
 			EntityBinding entityBinding) {
 
 		PluralAttributeBindingState bindingState =
 				new HbmPluralAttributeBindingState(
 						entityBinding.getEntity().getJavaType().getName(),
 						bindingContext,
 						entityBinding.getMetaAttributeContext(),
 						collection
 				);
 
 		BagBinding collectionBinding = entityBinding.makeBagAttributeBinding(
 				bindingState.getAttributeName(),
 				getCollectionElementType( collection )
 		)
 				.initialize( bindingState );
 
 		// todo : relational model binding
 		return collectionBinding;
 	}
 
 	private CollectionElementType getCollectionElementType(XMLBagElement collection) {
 		if ( collection.getElement() != null ) {
 			return CollectionElementType.BASIC;
 		}
 		else if ( collection.getCompositeElement() != null ) {
 			return CollectionElementType.COMPOSITE;
 		}
 		else if ( collection.getManyToMany() != null ) {
 			return CollectionElementType.MANY_TO_MANY;
 		}
 		else if ( collection.getOneToMany() != null ) {
 			return CollectionElementType.ONE_TO_MANY;
 		}
 		else if ( collection.getManyToAny() != null ) {
 			return CollectionElementType.MANY_TO_ANY;
 		}
 		else {
 			throw new AssertionFailure( "Unknown collection element type: " + collection );
 		}
 	}
 
 	private ManyToOneAttributeBinding makeManyToOneAttributeBinding(XMLManyToOneElement manyToOne,
 																	EntityBinding entityBinding) {
 		ManyToOneAttributeBindingState bindingState =
 				new HbmManyToOneAttributeBindingState(
 						entityBinding.getEntity().getJavaType().getName(),
 						bindingContext,
 						entityBinding.getMetaAttributeContext(),
 						manyToOne
 				);
 
 		// boolean (true here) indicates that by default column names should be guessed
 		ManyToOneRelationalState relationalState =
 				new HbmManyToOneRelationalStateContainer(
 						bindingContext,
 						true,
 						manyToOne
 				);
 
 		entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		ManyToOneAttributeBinding manyToOneAttributeBinding =
 				entityBinding.makeManyToOneAttributeBinding( bindingState.getAttributeName() )
 						.initialize( bindingState )
 						.initialize( relationalState );
 
 		return manyToOneAttributeBinding;
 	}
 
 //	private static Property createProperty(
 //			final Value value,
 //	        final String propertyName,
 //			final String className,
 //	        final Element subnode,
 //	        final Mappings mappings,
 //			java.util.Map inheritedMetas) throws MappingException {
 //
 //		if ( StringHelper.isEmpty( propertyName ) ) {
 //			throw new MappingException( subnode.getName() + " mapping must defined a name attribute [" + className + "]" );
 //		}
 //
 //		value.setTypeUsingReflection( className, propertyName );
 //
 //		// this is done here 'cos we might only know the type here (ugly!)
 //		// TODO: improve this a lot:
 //		if ( value instanceof ToOne ) {
 //			ToOne toOne = (ToOne) value;
 //			String propertyRef = toOne.getReferencedPropertyName();
 //			if ( propertyRef != null ) {
 //				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
 //			}
 //		}
 //		else if ( value instanceof Collection ) {
 //			Collection coll = (Collection) value;
 //			String propertyRef = coll.getReferencedPropertyName();
 //			// not necessarily a *unique* property reference
 //			if ( propertyRef != null ) {
 //				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
 //			}
 //		}
 //
 //		value.createForeignKey();
 //		Property prop = new Property();
 //		prop.setValue( value );
 //		bindProperty( subnode, prop, mappings, inheritedMetas );
 //		return prop;
 //	}
 
 
 //	protected HbmRelationalState processValues(Element identifierElement, TableSpecification baseTable, String propertyPath, boolean isSimplePrimaryKey) {
 	// first boolean (false here) indicates that by default columns are nullable
 	// second boolean (true here) indicates that by default column names should be guessed
 // todo : logical 1-1 handling
 //			final Attribute uniqueAttribute = node.attribute( "unique" );
 //			if ( uniqueAttribute != null
 //					&& "true".equals( uniqueAttribute.getValue() )
 //					&& ManyToOne.class.isInstance( simpleValue ) ) {
 //				( (ManyToOne) simpleValue ).markAsLogicalOneToOne();
 //			}
 	//return processValues( identifierElement, baseTable, false, true, propertyPath, isSimplePrimaryKey );
 
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityProcessor.java
new file mode 100644
index 0000000000..c4aeb977ca
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityProcessor.java
@@ -0,0 +1,56 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
+package org.hibernate.metamodel.source.hbm;
+
+import org.hibernate.metamodel.binder.EntityBinder;
+import org.hibernate.metamodel.binder.view.hbm.EntityViewImpl;
+import org.hibernate.metamodel.binding.EntityBinding;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
+
+/**
+ * @author Steve Ebersole
+ */
+public class EntityProcessor {
+	private final HbmBindingContext bindingContext;
+	private final EntityBinder entityBinder;
+
+	public EntityProcessor(HbmBindingContext bindingContext) {
+		this.bindingContext = bindingContext;
+		this.entityBinder = new EntityBinder( bindingContext.getMetadataImplementor() );
+	}
+
+	public void process(XMLHibernateMapping.XMLClass xmlClass) {
+		EntityBinding entityBinding = entityBinder.createEntityBinding(
+				new EntityViewImpl(
+						null,		// superType
+						xmlClass,
+						true,		// isRoot
+						null,		// inheritanceType
+						bindingContext
+				)
+		);
+
+		bindingContext.
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmSourceProcessor.java
similarity index 83%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmBinder.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmSourceProcessor.java
index b13b60f9a1..fe3f926ef3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmSourceProcessor.java
@@ -1,84 +1,84 @@
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
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.internal.JaxbRoot;
-import org.hibernate.metamodel.source.spi.Binder;
+import org.hibernate.metamodel.source.spi.SourceProcessor;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 
 /**
  * Responsible for performing binding of hbm xml.
  */
-public class HbmBinder implements Binder {
+public class HbmSourceProcessor implements SourceProcessor {
 	private final MetadataImplementor metadata;
 	private List<HibernateMappingProcessor> processors;
 
-	public HbmBinder(MetadataImplementor metadata) {
+	public HbmSourceProcessor(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public void prepare(MetadataSources sources) {
 		this.processors = new ArrayList<HibernateMappingProcessor>();
 		for ( JaxbRoot jaxbRoot : sources.getJaxbRootList() ) {
 			if ( jaxbRoot.getRoot() instanceof XMLHibernateMapping ) {
 				processors.add( new HibernateMappingProcessor( metadata, (JaxbRoot<XMLHibernateMapping>) jaxbRoot ) );
 			}
 		}
 	}
 
 	@Override
-	public void bindIndependentMetadata(MetadataSources sources) {
+	public void processIndependentMetadata(MetadataSources sources) {
 		for ( HibernateMappingProcessor processor : processors ) {
 			processor.bindIndependentMetadata();
 		}
 	}
 
 	@Override
-	public void bindTypeDependentMetadata(MetadataSources sources) {
+	public void processTypeDependentMetadata(MetadataSources sources) {
 		for ( HibernateMappingProcessor processor : processors ) {
 			processor.bindTypeDependentMetadata();
 		}
 	}
 
 	@Override
-	public void bindMappingMetadata(MetadataSources sources, List<String> processedEntityNames) {
+	public void processMappingMetadata(MetadataSources sources, List<String> processedEntityNames) {
 		for ( HibernateMappingProcessor processor : processors ) {
 			processor.bindMappingMetadata( processedEntityNames );
 		}
 	}
 
 	@Override
-	public void bindMappingDependentMetadata(MetadataSources sources) {
+	public void processMappingDependentMetadata(MetadataSources sources) {
 		for ( HibernateMappingProcessor processor : processors ) {
 			processor.bindMappingDependentMetadata();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
index 0e3bcb9931..09e44681a7 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
@@ -1,385 +1,386 @@
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
 
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.BasicAuxiliaryDatabaseObjectImpl;
 import org.hibernate.metamodel.source.MappingException;
 import org.hibernate.metamodel.source.Origin;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLFetchProfileElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLParamElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLUnionSubclassElement;
 import org.hibernate.metamodel.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.internal.OverriddenMappingDefaults;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 import org.hibernate.type.Type;
 
 /**
  * Responsible for processing a {@code <hibernate-mapping/>} element.  Allows processing to be coordinated across
  * all hbm files in an ordered fashion.  The order is essentially the same as defined in
- * {@link org.hibernate.metamodel.source.spi.Binder}
+ * {@link org.hibernate.metamodel.source.spi.SourceProcessor}
  *
  * @author Steve Ebersole
  */
 public class HibernateMappingProcessor implements HbmBindingContext {
 	private final MetadataImplementor metadata;
 	private final JaxbRoot<XMLHibernateMapping> jaxbRoot;
 
 	private final XMLHibernateMapping hibernateMapping;
 
 	private final MappingDefaults mappingDefaults;
 	private final MetaAttributeContext metaAttributeContext;
 
 	private final boolean autoImport;
 
 	public HibernateMappingProcessor(MetadataImplementor metadata, JaxbRoot<XMLHibernateMapping> jaxbRoot) {
 		this.metadata = metadata;
 		this.jaxbRoot = jaxbRoot;
 
 		this.hibernateMapping = jaxbRoot.getRoot();
 		this.mappingDefaults = new OverriddenMappingDefaults(
 				metadata.getMappingDefaults(),
 				hibernateMapping.getPackage(),
 				hibernateMapping.getSchema(),
 				hibernateMapping.getCatalog(),
 				null,
 				null,
 				hibernateMapping.getDefaultCascade(),
 				hibernateMapping.getDefaultAccess(),
 				hibernateMapping.isDefaultLazy()
 		);
 
 		autoImport = hibernateMapping.isAutoImport();
 
 		metaAttributeContext = extractMetaAttributes();
 	}
 
 	private MetaAttributeContext extractMetaAttributes() {
 		return hibernateMapping.getMeta() == null
 				? new MetaAttributeContext( metadata.getMetaAttributeContext() )
 				: HbmHelper.extractMetaAttributeContext( hibernateMapping.getMeta(), true, metadata.getMetaAttributeContext() );
 	}
 
 	@Override
 	public boolean isAutoImport() {
 		return autoImport;
 	}
 
 	@Override
 	public Origin getOrigin() {
 		return jaxbRoot.getOrigin();
 	}
 
 	@Override
 	public ServiceRegistry getServiceRegistry() {
 		return metadata.getServiceRegistry();
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return metadata.getOptions().getNamingStrategy();
 	}
 
     @Override
     public boolean isGloballyQuotedIdentifiers() {
         return metadata.isGloballyQuotedIdentifiers();
     }
 
     @Override
 	public MappingDefaults getMappingDefaults() {
 		return mappingDefaults;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	@Override
 	public MetadataImplementor getMetadataImplementor() {
 		return metadata;
 	}
 
 	@Override
 	public <T> Class<T> locateClassByName(String name) {
 		return metadata.locateClassByName( name );
 	}
 
 	@Override
 	public JavaType makeJavaType(String className) {
 		return metadata.makeJavaType( className );
 	}
 
 	public void bindIndependentMetadata() {
 		bindDatabaseObjectDefinitions();
 		bindTypeDefinitions();
 	}
 
 	private void bindDatabaseObjectDefinitions() {
 		if ( hibernateMapping.getDatabaseObject() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLDatabaseObject databaseObjectElement : hibernateMapping.getDatabaseObject() ) {
 			final AuxiliaryDatabaseObject auxiliaryDatabaseObject;
 			if ( databaseObjectElement.getDefinition() != null ) {
 				final String className = databaseObjectElement.getDefinition().getClazz();
 				try {
 					auxiliaryDatabaseObject = (AuxiliaryDatabaseObject) classLoaderService().classForName( className ).newInstance();
 				}
 				catch (ClassLoadingException e) {
 					throw e;
 				}
 				catch (Exception e) {
 					throw new MappingException(
 							"could not instantiate custom database object class [" + className + "]",
 							jaxbRoot.getOrigin()
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
 
 	private void bindTypeDefinitions() {
 		if ( hibernateMapping.getTypedef() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLTypedef typedef : hibernateMapping.getTypedef() ) {
 			final Map<String, String> parameters = new HashMap<String, String>();
 			for ( XMLParamElement paramElement : typedef.getParam() ) {
 				parameters.put( paramElement.getName(), paramElement.getValue() );
 			}
 			metadata.addTypeDefinition( new TypeDef( typedef.getName(), typedef.getClazz(), parameters ) );
 		}
 	}
 
 	public void bindTypeDependentMetadata() {
 		bindFilterDefinitions();
 		bindIdentifierGenerators();
 	}
 
 	private void bindFilterDefinitions() {
 		if(hibernateMapping.getFilterDef() == null){
 			return;
 		}
 		for ( XMLHibernateMapping.XMLFilterDef filterDefinition : hibernateMapping.getFilterDef() ) {
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
 					throw new MappingException( "Unrecognized nested filter content", jaxbRoot.getOrigin() );
 				}
 			}
 			if ( condition == null ) {
 				condition = filterDefinition.getCondition();
 			}
 			metadata.addFilterDefinition( new FilterDefinition( name, condition, parameters ) );
 		}
 	}
 
 	private void bindIdentifierGenerators() {
 		if ( hibernateMapping.getIdentifierGenerator() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLIdentifierGenerator identifierGeneratorElement : hibernateMapping.getIdentifierGenerator() ) {
 			metadata.registerIdentifierGenerator(
 					identifierGeneratorElement.getName(),
 					identifierGeneratorElement.getClazz()
 			);
 		}
 	}
 
+
 	public void bindMappingMetadata(List<String> processedEntityNames) {
 		if ( hibernateMapping.getClazzOrSubclassOrJoinedSubclass() == null ) {
 			return;
 		}
 		for ( Object clazzOrSubclass : hibernateMapping.getClazzOrSubclassOrJoinedSubclass() ) {
 			if ( XMLHibernateMapping.XMLClass.class.isInstance( clazzOrSubclass ) ) {
 				XMLHibernateMapping.XMLClass clazz =
 						XMLHibernateMapping.XMLClass.class.cast( clazzOrSubclass );
-				new RootEntityBinder( this, clazz ).process( clazz );
+				new RootEntityProcessor( this, clazz ).process( clazz );
 			}
 			else if ( XMLSubclassElement.class.isInstance( clazzOrSubclass ) ) {
 //					PersistentClass superModel = getSuperclass( mappings, element );
 //					handleSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( XMLJoinedSubclassElement.class.isInstance( clazzOrSubclass ) ) {
 //					PersistentClass superModel = getSuperclass( mappings, element );
 //					handleJoinedSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else if ( XMLUnionSubclassElement.class.isInstance( clazzOrSubclass ) ) {
 //					PersistentClass superModel = getSuperclass( mappings, element );
 //					handleUnionSubclass( superModel, mappings, element, inheritedMetas );
 			}
 			else {
 				throw new org.hibernate.metamodel.source.MappingException(
 						"unknown type of class or subclass: " +
 								clazzOrSubclass.getClass().getName(), jaxbRoot.getOrigin()
 				);
 			}
 		}
 	}
 
 	public void bindMappingDependentMetadata() {
 		bindFetchProfiles();
 		bindImports();
 		bindResultSetMappings();
 		bindNamedQueries();
 	}
 
 	private void bindFetchProfiles(){
 		if(hibernateMapping.getFetchProfile() == null){
 			return;
 		}
 		bindFetchProfiles( hibernateMapping.getFetchProfile(),null );
 	}
 
 	public void bindFetchProfiles(List<XMLFetchProfileElement> fetchProfiles, String containingEntityName) {
 		for ( XMLFetchProfileElement fetchProfile : fetchProfiles ) {
 			String profileName = fetchProfile.getName();
 			Set<FetchProfile.Fetch> fetches = new HashSet<FetchProfile.Fetch>();
 			for ( XMLFetchProfileElement.XMLFetch fetch : fetchProfile.getFetch() ) {
 				String entityName = fetch.getEntity() == null ? containingEntityName : fetch.getEntity();
 				if ( entityName == null ) {
 					throw new MappingException(
 							"could not determine entity for fetch-profile fetch [" + profileName + "]:[" +
 									fetch.getAssociation() + "]",
 							jaxbRoot.getOrigin()
 					);
 				}
 				fetches.add( new FetchProfile.Fetch( entityName, fetch.getAssociation(), fetch.getStyle() ) );
 			}
 			metadata.addFetchProfile( new FetchProfile( profileName, fetches ) );
 		}
 	}
 
 	private void bindImports() {
 		if ( hibernateMapping.getImport() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLImport importValue : hibernateMapping.getImport() ) {
 			String className = getClassName( importValue.getClazz() );
 			String rename = importValue.getRename();
 			rename = ( rename == null ) ? StringHelper.unqualify( className ) : rename;
 			metadata.addImport( className, rename );
 		}
 	}
 
 	private void bindResultSetMappings() {
 		if ( hibernateMapping.getResultset() == null ) {
 			return;
 		}
 //			bindResultSetMappingDefinitions( element, null, mappings );
 	}
 
 	private void bindNamedQueries() {
 		if ( hibernateMapping.getQueryOrSqlQuery() == null ) {
 			return;
 		}
 		for ( Object queryOrSqlQuery : hibernateMapping.getQueryOrSqlQuery() ) {
 			if ( XMLQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //					bindNamedQuery( element, null, mappings );
 			}
 			else if ( XMLSqlQueryElement.class.isInstance( queryOrSqlQuery ) ) {
 //				bindNamedSQLQuery( element, null, mappings );
 			}
 			else {
 				throw new MappingException(
 						"unknown type of query: " +
 								queryOrSqlQuery.getClass().getName(), jaxbRoot.getOrigin()
 				);
 			}
 		}
 	}
 
 	private ClassLoaderService classLoaderService;
 
 	private ClassLoaderService classLoaderService() {
 		if ( classLoaderService == null ) {
 			classLoaderService = metadata.getServiceRegistry().getService( ClassLoaderService.class );
 		}
 		return classLoaderService;
 	}
 
 	@Override
 	public String extractEntityName(XMLHibernateMapping.XMLClass entityClazz) {
 		return HbmHelper.extractEntityName( entityClazz, mappingDefaults.getPackageName() );
 	}
 
 	@Override
 	public String getClassName(String unqualifiedName) {
 		return HbmHelper.getClassName( unqualifiedName, mappingDefaults.getPackageName() );
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityProcessor.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityBinder.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityProcessor.java
index e4ac9542b8..03e70cddf3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityProcessor.java
@@ -1,336 +1,334 @@
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
 
 import org.hibernate.InvalidMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.state.DiscriminatorBindingState;
 import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.InLineView;
 import org.hibernate.metamodel.relational.Schema;
+import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.state.ValueRelationalState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmDiscriminatorBindingState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmSimpleAttributeBindingState;
 import org.hibernate.metamodel.source.hbm.state.relational.HbmSimpleValueRelationalStateContainer;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLCompositeId;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLId;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
-class RootEntityBinder extends AbstractEntityBinder {
+class RootEntityProcessor extends AbstractEntityBinder {
 
-	RootEntityBinder(HbmBindingContext bindingContext, XMLClass xmlClazz) {
+	RootEntityProcessor(HbmBindingContext bindingContext, XMLClass xmlClazz) {
 		super( bindingContext, xmlClazz );
 	}
 
 	public boolean isRoot() {
 		return true;
 	}
 
 	public InheritanceType getInheritanceType() {
 		return InheritanceType.SINGLE_TABLE;
 	}
 
 	public void process(XMLClass xmlClazz) {
 		String entityName = getBindingContext().extractEntityName( xmlClazz );
 		if ( entityName == null ) {
 			throw new MappingException( "Unable to determine entity name" );
 		}
 
 		EntityBinding entityBinding = new EntityBinding();
 		basicEntityBinding( xmlClazz, entityBinding, null );
 		basicTableBinding( xmlClazz, entityBinding );
 
 		bindIdentifier( xmlClazz, entityBinding );
 		bindDiscriminator( xmlClazz, entityBinding );
 		bindVersionOrTimestamp( xmlClazz, entityBinding );
 
 		// called createClassProperties in HBMBinder...
 		buildAttributeBindings( xmlClazz, entityBinding );
 
 		getMetadata().addEntity( entityBinding );
 	}
 
 	private void basicTableBinding(XMLClass xmlClazz,
 								   EntityBinding entityBinding) {
 		final Schema schema = getMetadata().getDatabase().getSchema( getSchemaName() );
 
 		final String subSelect =
 				xmlClazz.getSubselectAttribute() == null ? xmlClazz.getSubselect() : xmlClazz.getSubselectAttribute();
 		if ( subSelect != null ) {
 			final String logicalName = entityBinding.getEntity().getName();
 			InLineView inLineView = schema.getInLineView( logicalName );
 			if ( inLineView == null ) {
 				inLineView = schema.createInLineView( logicalName, subSelect );
 			}
 			entityBinding.setBaseTable( inLineView );
 		}
 		else {
             String classTableName = getClassTableName( xmlClazz, entityBinding, null );
-            if(getBindingContext().isGloballyQuotedIdentifiers()){
+            if ( getBindingContext().isGloballyQuotedIdentifiers() ) {
                 classTableName = StringHelper.quote( classTableName );
             }
 			final Identifier tableName = Identifier.toIdentifier( classTableName );
-			org.hibernate.metamodel.relational.Table table = schema.getTable( tableName );
-			if ( table == null ) {
-				table = schema.createTable( tableName );
-			}
+			final Table table = schema.locateOrCreateTable( tableName );
 			entityBinding.setBaseTable( table );
 			String comment = xmlClazz.getComment();
 			if ( comment != null ) {
 				table.addComment( comment.trim() );
 			}
 			String check = xmlClazz.getCheck();
 			if ( check != null ) {
 				table.addCheckConstraint( check );
 			}
 		}
 	}
 
 	private void bindIdentifier(XMLClass xmlClazz,
 								EntityBinding entityBinding) {
 		if ( xmlClazz.getId() != null ) {
 			bindSimpleId( xmlClazz.getId(), entityBinding );
 			return;
 		}
 
 		if ( xmlClazz.getCompositeId() != null ) {
 			bindCompositeId( xmlClazz.getCompositeId(), entityBinding );
 		}
 
 		throw new InvalidMappingException(
 				"Entity [" + entityBinding.getEntity().getName() + "] did not contain identifier mapping",
 				getBindingContext().getOrigin()
 		);
 	}
 
 	private void bindSimpleId(XMLId id, EntityBinding entityBinding) {
 		SimpleAttributeBindingState bindingState = new HbmSimpleAttributeBindingState(
 				entityBinding.getEntity().getJavaType().getName(),
 				getBindingContext(),
 				entityBinding.getMetaAttributeContext(),
 				id
 		);
 		// boolean (true here) indicates that by default column names should be guessed
 		HbmSimpleValueRelationalStateContainer relationalStateContainer = new HbmSimpleValueRelationalStateContainer(
 				getBindingContext(), true, id
 		);
 		if ( relationalStateContainer.getRelationalStates().size() > 1 ) {
 			throw new MappingException( "ID is expected to be a single column, but has more than 1 value" );
 		}
 
 		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		entityBinding.makeSimpleIdAttributeBinding( attribute )
 				.initialize( bindingState )
 				.initialize( relationalStateContainer.getRelationalStates().get( 0 ) );
 
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
 
 //		if ( propertyName == null ) {
 //			bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
 //		}
 //		else {
 //			bindSimpleValue( idNode, id, false, propertyName, mappings );
 //		}
 //
 //		if ( propertyName == null || !entity.hasPojoRepresentation() ) {
 //			if ( !id.isTypeSpecified() ) {
 //				throw new MappingException( "must specify an identifier type: "
 //					+ entity.getEntityName() );
 //			}
 //		}
 //		else {
 //			id.setTypeUsingReflection( entity.getClassName(), propertyName );
 //		}
 //
 //		if ( propertyName != null ) {
 //			Property prop = new Property();
 //			prop.setValue( id );
 //			bindProperty( idNode, prop, mappings, inheritedMetas );
 //			entity.setIdentifierProperty( prop );
 //		}
 
 		// TODO:
 		/*
 		 * if ( id.getHibernateType().getReturnedClass().isArray() ) throw new MappingException(
 		 * "illegal use of an array as an identifier (arrays don't reimplement equals)" );
 		 */
 //		makeIdentifier( idNode, id, mappings );
 	}
 
 	private static void bindCompositeId(XMLCompositeId compositeId, EntityBinding entityBinding) {
 		final String explicitName = compositeId.getName();
 
 //		String propertyName = idNode.attributeValue( "name" );
 //		Component id = new Component( mappings, entity );
 //		entity.setIdentifier( id );
 //		bindCompositeId( idNode, id, entity, propertyName, mappings, inheritedMetas );
 //		if ( propertyName == null ) {
 //			entity.setEmbeddedIdentifier( id.isEmbedded() );
 //			if ( id.isEmbedded() ) {
 //				// todo : what is the implication of this?
 //				id.setDynamic( !entity.hasPojoRepresentation() );
 //				/*
 //				 * Property prop = new Property(); prop.setName("id");
 //				 * prop.setPropertyAccessorName("embedded"); prop.setValue(id);
 //				 * entity.setIdentifierProperty(prop);
 //				 */
 //			}
 //		}
 //		else {
 //			Property prop = new Property();
 //			prop.setValue( id );
 //			bindProperty( idNode, prop, mappings, inheritedMetas );
 //			entity.setIdentifierProperty( prop );
 //		}
 //
 //		makeIdentifier( idNode, id, mappings );
 
 	}
 
 	private void bindDiscriminator(XMLClass xmlEntityClazz,
 								   EntityBinding entityBinding) {
 		if ( xmlEntityClazz.getDiscriminator() == null ) {
 			return;
 		}
 
 		DiscriminatorBindingState bindingState = new HbmDiscriminatorBindingState(
 				entityBinding.getEntity().getJavaType().getName(),
 				entityBinding.getEntity().getName(),
 				getBindingContext(),
 				xmlEntityClazz
 		);
 
 		// boolean (true here) indicates that by default column names should be guessed
 		ValueRelationalState relationalState = convertToSimpleValueRelationalStateIfPossible(
 				new HbmSimpleValueRelationalStateContainer(
 						getBindingContext(),
 						true,
 						xmlEntityClazz.getDiscriminator()
 				)
 		);
 
 
 		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		entityBinding.makeEntityDiscriminator( attribute )
 				.initialize( bindingState )
 				.initialize( relationalState );
 	}
 
 	private void bindVersionOrTimestamp(XMLClass xmlEntityClazz,
 										EntityBinding entityBinding) {
 		if ( xmlEntityClazz.getVersion() != null ) {
 			bindVersion(
 					xmlEntityClazz.getVersion(),
 					entityBinding
 			);
 		}
 		else if ( xmlEntityClazz.getTimestamp() != null ) {
 			bindTimestamp(
 					xmlEntityClazz.getTimestamp(),
 					entityBinding
 			);
 		}
 	}
 
 	protected void bindVersion(XMLHibernateMapping.XMLClass.XMLVersion version,
 							   EntityBinding entityBinding) {
 		SimpleAttributeBindingState bindingState =
 				new HbmSimpleAttributeBindingState(
 						entityBinding.getEntity().getJavaType().getName(),
 						getBindingContext(),
 						entityBinding.getMetaAttributeContext(),
 						version
 				);
 
 		// boolean (true here) indicates that by default column names should be guessed
 		ValueRelationalState relationalState =
 				convertToSimpleValueRelationalStateIfPossible(
 						new HbmSimpleValueRelationalStateContainer(
 								getBindingContext(),
 								true,
 								version
 						)
 				);
 
 		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		entityBinding.makeVersionBinding( attribute )
 				.initialize( bindingState )
 				.initialize( relationalState );
 	}
 
 	protected void bindTimestamp(XMLHibernateMapping.XMLClass.XMLTimestamp timestamp,
 								 EntityBinding entityBinding) {
 
 		SimpleAttributeBindingState bindingState =
 				new HbmSimpleAttributeBindingState(
 						entityBinding.getEntity().getJavaType().getName(),
 						getBindingContext(),
 						entityBinding.getMetaAttributeContext(),
 						timestamp
 				);
 
 		// relational model has not been bound yet
 		// boolean (true here) indicates that by default column names should be guessed
 		ValueRelationalState relationalState =
 				convertToSimpleValueRelationalStateIfPossible(
 						new HbmSimpleValueRelationalStateContainer(
 								getBindingContext(),
 								true,
 								timestamp
 						)
 				);
 
 		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		entityBinding.makeVersionBinding( attribute )
 				.initialize( bindingState )
 				.initialize( relationalState );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index eb3d2dbe19..fdf64d3f2a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,531 +1,551 @@
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
 package org.hibernate.metamodel.source.internal;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.DuplicateMappingException;
 import org.hibernate.MappingException;
 import org.hibernate.SessionFactory;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.id.factory.DefaultIdentifierGeneratorFactory;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.SourceProcessingOrder;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.relational.Database;
-import org.hibernate.metamodel.source.annotations.AnnotationBinder;
-import org.hibernate.metamodel.source.hbm.HbmBinder;
-import org.hibernate.metamodel.source.spi.Binder;
+import org.hibernate.metamodel.source.annotations.AnnotationSourceProcessor;
+import org.hibernate.metamodel.source.hbm.HbmSourceProcessor;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
+import org.hibernate.metamodel.source.spi.SourceProcessor;
+import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.type.Type;
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
-	private ClassLoaderService classLoaderService;
+	private final org.hibernate.internal.util.Value<ClassLoaderService> classLoaderService;
 
 	private TypeResolver typeResolver = new TypeResolver();
 
 	private SessionFactoryBuilder sessionFactoryBuilder = new SessionFactoryBuilderImpl( this );
 
 	private DefaultIdentifierGeneratorFactory identifierGeneratorFactory = new DefaultIdentifierGeneratorFactory();
 
 	private final Database database = new Database();
 
 	private final MappingDefaults mappingDefaults;
 
 	/**
 	 * Maps the fully qualified class name of an entity to its entity binding
 	 */
 	private Map<String, EntityBinding> entityBindingMap = new HashMap<String, EntityBinding>();
 	private Map<String, EntityBinding> rootEntityBindingMap = new HashMap<String, EntityBinding>();
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
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
-		final Binder[] binders;
+		final SourceProcessor[] sourceProcessors;
 		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
-			binders = new Binder[] {
-					new HbmBinder( this ),
-					new AnnotationBinder( this )
+			sourceProcessors = new SourceProcessor[] {
+					new HbmSourceProcessor( this ),
+					new AnnotationSourceProcessor( this )
 			};
 		}
 		else {
-			binders = new Binder[] {
-					new AnnotationBinder( this ),
-					new HbmBinder( this )
+			sourceProcessors = new SourceProcessor[] {
+					new AnnotationSourceProcessor( this ),
+					new HbmSourceProcessor( this )
 			};
 		}
 
+		this.classLoaderService = new org.hibernate.internal.util.Value<ClassLoaderService>(
+				new org.hibernate.internal.util.Value.DeferredInitializer<ClassLoaderService>() {
+					@Override
+					public ClassLoaderService initialize() {
+						return serviceRegistry.getService( ClassLoaderService.class );
+					}
+				}
+		);
+		this.persisterClassResolverService = new org.hibernate.internal.util.Value<PersisterClassResolver>(
+				new org.hibernate.internal.util.Value.DeferredInitializer<PersisterClassResolver>() {
+					@Override
+					public PersisterClassResolver initialize() {
+						return serviceRegistry.getService( PersisterClassResolver.class );
+					}
+				}
+		);
+
+
 		final ArrayList<String> processedEntityNames = new ArrayList<String>();
 
-		prepare( binders, metadataSources );
-		bindIndependentMetadata( binders, metadataSources );
-		bindTypeDependentMetadata( binders, metadataSources );
-		bindMappingMetadata( binders, metadataSources, processedEntityNames );
-		bindMappingDependentMetadata( binders, metadataSources );
+		prepare( sourceProcessors, metadataSources );
+		bindIndependentMetadata( sourceProcessors, metadataSources );
+		bindTypeDependentMetadata( sourceProcessors, metadataSources );
+		bindMappingMetadata( sourceProcessors, metadataSources, processedEntityNames );
+		bindMappingDependentMetadata( sourceProcessors, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
 		new EntityReferenceResolver( this ).resolve();
 		new AttributeTypeResolver( this ).resolve();
 	}
 
-	private void prepare(Binder[] binders, MetadataSources metadataSources) {
-		for ( Binder binder : binders ) {
-			binder.prepare( metadataSources );
+	private void prepare(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
+		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
+			sourceProcessor.prepare( metadataSources );
 		}
 	}
 
-	private void bindIndependentMetadata(Binder[] binders, MetadataSources metadataSources) {
-		for ( Binder binder : binders ) {
-			binder.bindIndependentMetadata( metadataSources );
+	private void bindIndependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
+		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
+			sourceProcessor.processIndependentMetadata( metadataSources );
 		}
 	}
 
-	private void bindTypeDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
-		for ( Binder binder : binders ) {
-			binder.bindTypeDependentMetadata( metadataSources );
+	private void bindTypeDependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
+		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
+			sourceProcessor.processTypeDependentMetadata( metadataSources );
 		}
 	}
 
-	private void bindMappingMetadata(Binder[] binders, MetadataSources metadataSources, List<String> processedEntityNames) {
-		for ( Binder binder : binders ) {
-			binder.bindMappingMetadata( metadataSources, processedEntityNames );
+	private void bindMappingMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources, List<String> processedEntityNames) {
+		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
+			sourceProcessor.processMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
-	private void bindMappingDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
-		for ( Binder binder : binders ) {
-			binder.bindMappingDependentMetadata( metadataSources );
+	private void bindMappingDependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
+		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
+			sourceProcessor.processMappingDependentMetadata( metadataSources );
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
 		if ( def == null || def.getName() == null ) {
 			throw new IllegalArgumentException( "Named query definition object or name is null: " + def.getQueryString() );
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
 			throw new IllegalArgumentException( "Resultset mappping object or name is null: " + resultSetMappingDefinition );
 		}
 		resultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 	}
 
 	@Override
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions() {
 		return resultSetMappings.values();
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDef typeDef) {
 		if ( typeDef == null || typeDef.getName() == null ) {
 			throw new IllegalArgumentException( "Type definition object or name is null: " + typeDef.getTypeClass() );
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
 
 	public TypeDef getTypeDef(String name) {
 		return typeDefs.get( name );
 	}
 
-	private ClassLoaderService classLoaderService(){
-		if(classLoaderService==null){
-			classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
-		}
-		return classLoaderService;
+	private ClassLoaderService classLoaderService() {
+		return classLoaderService.getValue();
+	}
+
+	private PersisterClassResolver persisterClassResolverService() {
+		return persisterClassResolverService.getValue();
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
 	public JavaType makeJavaType(String className) {
 		return new JavaType( className, classLoaderService() );
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
 		EntityBinding rootEntityBinding = rootEntityBindingMap.get( entityName );
 		if ( rootEntityBinding == null ) {
 			EntityBinding entityBinding = entityBindingMap.get( entityName );
 			if ( entityBinding == null ) {
 				throw new IllegalStateException( "Unknown entity binding: " + entityName );
 			}
 			if ( entityBinding.isRoot() ) {
 				rootEntityBinding = entityBinding;
 			}
 			else {
 				if ( entityBinding.getEntity().getSuperType() == null ) {
 					throw new IllegalStateException( "Entity binding has no root: " + entityName );
 				}
 				rootEntityBinding = getRootEntityBinding( entityBinding.getEntity().getSuperType().getName() );
 			}
 			rootEntityBindingMap.put( entityName, rootEntityBinding );
 		}
 		return rootEntityBinding;
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
 	public MetaAttributeContext getMetaAttributeContext() {
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
 	public Type getIdentifierType(String entityName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		return entityBinding
 				.getEntityIdentifier()
 				.getValueBinding()
 				.getHibernateTypeDescriptor()
 				.getExplicitType();
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
 	public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
 		EntityBinding entityBinding = getEntityBinding( entityName );
 		if ( entityBinding == null ) {
 			throw new MappingException( "Entity binding not known: " + entityName );
 		}
 		// TODO: should this call EntityBinding.getReferencedAttributeBindingString), which does not exist yet?
 		AttributeBinding attributeBinding = entityBinding.getAttributeBinding( propertyName );
 		if ( attributeBinding == null ) {
 			throw new MappingException( "unknown property: " + entityName + '.' + propertyName );
 		}
 		return attributeBinding.getHibernateTypeDescriptor().getExplicitType();
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
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java
index ae45398884..66502c678d 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java
@@ -1,49 +1,51 @@
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
 package org.hibernate.metamodel.source.spi;
 
 import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.metamodel.binder.view.EntityView;
 import org.hibernate.metamodel.domain.JavaType;
+import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.service.ServiceRegistry;
 
 /**
  * @author Steve Ebersole
  */
 public interface BindingContext {
     public ServiceRegistry getServiceRegistry();
 
     public NamingStrategy getNamingStrategy();
 
     public MappingDefaults getMappingDefaults();
 
     public MetaAttributeContext getMetaAttributeContext();
 
     public MetadataImplementor getMetadataImplementor();
 
     public <T> Class<T> locateClassByName(String name);
 
     public JavaType makeJavaType(String className);
 
     public boolean isGloballyQuotedIdentifiers();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
index 8f1f931ea2..d8a80dd0cd 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
@@ -1,74 +1,78 @@
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
 package org.hibernate.metamodel.source.spi;
 
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.Metadata;
-import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.type.TypeResolver;
 
 /**
  * @author Steve Ebersole
  */
 public interface MetadataImplementor extends Metadata, BindingContext, Mapping {
 	public BasicServiceRegistry getServiceRegistry();
 
 	public Database getDatabase();
 
 	public TypeResolver getTypeResolver();
 
 	public void addImport(String entityName, String entityName1);
 
 	public void addEntity(EntityBinding entityBinding);
 
 	public void addCollection(PluralAttributeBinding collectionBinding);
 
 	public void addFetchProfile(FetchProfile profile);
 
 	public void addTypeDefinition(TypeDef typeDef);
 
 	public void addFilterDefinition(FilterDefinition filterDefinition);
 
 	public void addIdGenerator(IdGenerator generator);
 
 	public void registerIdentifierGenerator(String name, String clazz);
 
 	public void addNamedNativeQuery(NamedSQLQueryDefinition def);
 
 	public void addNamedQuery(NamedQueryDefinition def);
 
 	public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition);
+
+	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject);
+
+	// todo : this needs to move to AnnotationBindingContext
+	public void setGloballyQuotedIdentifiers(boolean b);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/SourceProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/SourceProcessor.java
new file mode 100644
index 0000000000..7d2ea0c6d3
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/SourceProcessor.java
@@ -0,0 +1,81 @@
+/*
+ * Hibernate, Relational Persistence for Idiomatic Java
+ *
+ * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
+ * indicated by the @author tags or express copyright attribution
+ * statements applied by the authors.  All third-party contributions are
+ * distributed under license by Red Hat Inc.
+ *
+ * This copyrighted material is made available to anyone wishing to use, modify,
+ * copy, or redistribute it subject to the terms and conditions of the GNU
+ * Lesser General Public License, as published by the Free Software Foundation.
+ *
+ * This program is distributed in the hope that it will be useful,
+ * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
+ * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
+ * for more details.
+ *
+ * You should have received a copy of the GNU Lesser General Public License
+ * along with this distribution; if not, write to:
+ * Free Software Foundation, Inc.
+ * 51 Franklin Street, Fifth Floor
+ * Boston, MA  02110-1301  USA
+ */
+package org.hibernate.metamodel.source.spi;
+
+import java.util.List;
+
+import org.hibernate.metamodel.MetadataSources;
+
+/**
+ * Handles the processing of metadata sources in a dependency-ordered manner.
+ *
+ * @author Steve Ebersole
+ */
+public interface SourceProcessor {
+	/**
+	 * Prepare for processing the given sources.
+	 *
+	 * @param sources The metadata sources.
+	 */
+	public void prepare(MetadataSources sources);
+
+	/**
+	 * Process the independent metadata.  These have no dependency on other types of metadata being processed.
+	 *
+	 * @param sources The metadata sources.
+	 *
+	 * @see #prepare
+	 */
+	public void processIndependentMetadata(MetadataSources sources);
+
+	/**
+	 * Process the parts of the metadata that depend on type information (type definitions) having been processed
+	 * and available.
+	 *
+	 * @param sources The metadata sources.
+	 *
+	 * @see #processIndependentMetadata
+	 */
+	public void processTypeDependentMetadata(MetadataSources sources);
+
+	/**
+	 * Process the mapping (entities, et al) metadata.
+	 *
+	 * @param sources The metadata sources.
+	 * @param processedEntityNames Collection of any already processed entity names.
+	 *
+	 * @see #processTypeDependentMetadata
+	 */
+	public void processMappingMetadata(MetadataSources sources, List<String> processedEntityNames);
+
+	/**
+	 * Process the parts of the metadata that depend on mapping (entities, et al) information having been
+	 * processed and available.
+	 *
+	 * @param sources The metadata sources.
+	 *
+	 * @see #processMappingMetadata
+	 */
+	public void processMappingDependentMetadata(MetadataSources sources);
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
index fd81d4962b..d2be88126f 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
@@ -1,249 +1,249 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2008-2011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate.persister.internal;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.spi.PersisterClassResolver;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 
 import java.lang.reflect.Constructor;
 import java.lang.reflect.InvocationTargetException;
 
 /**
  * The standard Hibernate {@link PersisterFactory} implementation
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 public final class PersisterFactoryImpl implements PersisterFactory, ServiceRegistryAwareService {
 
 	/**
 	 * The constructor signature for {@link EntityPersister} implementations
 	 *
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 */
 	public static final Class[] ENTITY_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
 			PersistentClass.class,
 			EntityRegionAccessStrategy.class,
 			SessionFactoryImplementor.class,
 			Mapping.class
 	};
 
 	/**
 	 * The constructor signature for {@link EntityPersister} implementations using
 	 * an {@link EntityBinding}.
 	 *
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 * @todo change ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW to ENTITY_PERSISTER_CONSTRUCTOR_ARGS
 	 * when new metamodel is integrated
 	 */
 	public static final Class[] ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW = new Class[] {
 			EntityBinding.class,
 			EntityRegionAccessStrategy.class,
 			SessionFactoryImplementor.class,
 			Mapping.class
 	};
 
 	/**
 	 * The constructor signature for {@link CollectionPersister} implementations
 	 *
 	 * @todo still need to make collection persisters EntityMode-aware
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 */
 	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS = new Class[] {
 			Collection.class,
 			CollectionRegionAccessStrategy.class,
 			Configuration.class,
 			SessionFactoryImplementor.class
 	};
 
 	/**
 	 * The constructor signature for {@link CollectionPersister} implementations using
 	 * a {@link PluralAttributeBinding}
 	 *
 	 * @todo still need to make collection persisters EntityMode-aware
 	 * @todo make EntityPersister *not* depend on {@link SessionFactoryImplementor} if possible.
 	 * @todo change COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW to COLLECTION_PERSISTER_CONSTRUCTOR_ARGS
 	 * when new metamodel is integrated
 	 */
 	private static final Class[] COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW = new Class[] {
 			PluralAttributeBinding.class,
 			CollectionRegionAccessStrategy.class,
 			MetadataImplementor.class,
 			SessionFactoryImplementor.class
 	};
 
 	private ServiceRegistryImplementor serviceRegistry;
 
 	@Override
 	public void injectServices(ServiceRegistryImplementor serviceRegistry) {
 		this.serviceRegistry = serviceRegistry;
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public EntityPersister createEntityPersister(
 			PersistentClass metadata,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) {
 		Class<? extends EntityPersister> persisterClass = metadata.getEntityPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getEntityPersisterClass( metadata );
 		}
 		return create( persisterClass, ENTITY_PERSISTER_CONSTRUCTOR_ARGS, metadata, cacheAccessStrategy, factory, cfg );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public EntityPersister createEntityPersister(EntityBinding metadata,
 												 EntityRegionAccessStrategy cacheAccessStrategy,
 												 SessionFactoryImplementor factory,
 												 Mapping cfg) {
-		Class<? extends EntityPersister> persisterClass = metadata.getEntityPersisterClass();
+		Class<? extends EntityPersister> persisterClass = metadata.getCustomEntityPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getEntityPersisterClass( metadata );
 		}
 		return create( persisterClass, ENTITY_PERSISTER_CONSTRUCTOR_ARGS_NEW, metadata, cacheAccessStrategy, factory, cfg );
 	}
 
 	// TODO: change metadata arg type to EntityBinding when new metadata is integrated
 	private static EntityPersister create(
 			Class<? extends EntityPersister> persisterClass,
 			Class[] persisterConstructorArgs,
 			Object metadata,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) throws HibernateException {
 		try {
 			Constructor<? extends EntityPersister> constructor = persisterClass.getConstructor( persisterConstructorArgs );
 			try {
 				return constructor.newInstance( metadata, cacheAccessStrategy, factory, cfg );
 			}
 			catch (MappingException e) {
 				throw e;
 			}
 			catch (InvocationTargetException e) {
 				Throwable target = e.getTargetException();
 				if ( target instanceof HibernateException ) {
 					throw (HibernateException) target;
 				}
 				else {
 					throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), target );
 				}
 			}
 			catch (Exception e) {
 				throw new MappingException( "Could not instantiate persister " + persisterClass.getName(), e );
 			}
 		}
 		catch (MappingException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
 		}
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public CollectionPersister createCollectionPersister(
 			Configuration cfg,
 			Collection collectionMetadata,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException {
 		Class<? extends CollectionPersister> persisterClass = collectionMetadata.getCollectionPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( collectionMetadata );
 		}
 
 		return create( persisterClass, COLLECTION_PERSISTER_CONSTRUCTOR_ARGS, cfg, collectionMetadata, cacheAccessStrategy, factory );
 	}
 
 	@Override
 	@SuppressWarnings( {"unchecked"})
 	public CollectionPersister createCollectionPersister(MetadataImplementor metadata,
 														 PluralAttributeBinding collectionMetadata,
 														 CollectionRegionAccessStrategy cacheAccessStrategy,
 														 SessionFactoryImplementor factory) throws HibernateException {
 		Class<? extends CollectionPersister> persisterClass = collectionMetadata.getCollectionPersisterClass();
 		if ( persisterClass == null ) {
 			persisterClass = serviceRegistry.getService( PersisterClassResolver.class ).getCollectionPersisterClass( collectionMetadata );
 		}
 
 		return create( persisterClass, COLLECTION_PERSISTER_CONSTRUCTOR_ARGS_NEW, metadata, collectionMetadata, cacheAccessStrategy, factory );
 	}
 
 	// TODO: change collectionMetadata arg type to PluralAttributeBinding when new metadata is integrated
 	// TODO: change metadata arg type to MetadataImplementor when new metadata is integrated
 	private static CollectionPersister create(
 			Class<? extends CollectionPersister> persisterClass,
 			Class[] persisterConstructorArgs,
 			Object cfg,
 			Object collectionMetadata,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException {
 		try {
 			Constructor<? extends CollectionPersister> constructor = persisterClass.getConstructor( persisterConstructorArgs );
 			try {
 				return constructor.newInstance( collectionMetadata, cacheAccessStrategy, cfg, factory );
 			}
 			catch (MappingException e) {
 				throw e;
 			}
 			catch (InvocationTargetException e) {
 				Throwable target = e.getTargetException();
 				if ( target instanceof HibernateException ) {
 					throw (HibernateException) target;
 				}
 				else {
 					throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), target );
 				}
 			}
 			catch (Exception e) {
 				throw new MappingException( "Could not instantiate collection persister " + persisterClass.getName(), e );
 			}
 		}
 		catch (MappingException e) {
 			throw e;
 		}
 		catch (Exception e) {
 			throw new MappingException( "Could not get constructor for " + persisterClass.getName(), e );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
index 2c2c61615d..b441e78480 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
@@ -1,928 +1,928 @@
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
 package org.hibernate.tuple.entity;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.PropertyGeneration;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.domain.TypeNature;
 import org.hibernate.tuple.IdentifierProperty;
 import org.hibernate.tuple.PropertyFactory;
 import org.hibernate.tuple.StandardProperty;
 import org.hibernate.tuple.VersionProperty;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * Centralizes metamodel information about an entity.
  *
  * @author Steve Ebersole
  */
 public class EntityMetamodel implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityMetamodel.class.getName());
 
 	private static final int NO_VERSION_INDX = -66;
 
 	private final SessionFactoryImplementor sessionFactory;
 
 	private final String name;
 	private final String rootName;
 	private final EntityType entityType;
 
 	private final IdentifierProperty identifierProperty;
 	private final boolean versioned;
 
 	private final int propertySpan;
 	private final int versionPropertyIndex;
 	private final StandardProperty[] properties;
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final String[] propertyNames;
 	private final Type[] propertyTypes;
 	private final boolean[] propertyLaziness;
 	private final boolean[] propertyUpdateability;
 	private final boolean[] nonlazyPropertyUpdateability;
 	private final boolean[] propertyCheckability;
 	private final boolean[] propertyInsertability;
 	private final ValueInclusion[] insertInclusions;
 	private final ValueInclusion[] updateInclusions;
 	private final boolean[] propertyNullability;
 	private final boolean[] propertyVersionability;
 	private final CascadeStyle[] cascadeStyles;
 	private final boolean hasInsertGeneratedValues;
 	private final boolean hasUpdateGeneratedValues;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final Map<String, Integer> propertyIndexes = new HashMap<String, Integer>();
 	private final boolean hasCollections;
 	private final boolean hasMutableProperties;
 	private final boolean hasLazyProperties;
 	private final boolean hasNonIdentifierPropertyNamedId;
 
 	private final int[] naturalIdPropertyNumbers;
 	private final boolean hasImmutableNaturalId;
 
 	private boolean lazy; //not final because proxy factory creation can fail
 	private final boolean hasCascades;
 	private final boolean mutable;
 	private final boolean isAbstract;
 	private final boolean selectBeforeUpdate;
 	private final boolean dynamicUpdate;
 	private final boolean dynamicInsert;
 	private final int optimisticLockMode;
 
 	private final boolean polymorphic;
 	private final String superclass;  // superclass entity-name
 	private final boolean explicitPolymorphism;
 	private final boolean inherited;
 	private final boolean hasSubclasses;
 	private final Set subclassEntityNames = new HashSet();
 	private final Map entityNameByInheritenceClassMap = new HashMap();
 
 	private final EntityMode entityMode;
 	private final EntityTuplizer entityTuplizer;
 
 	public EntityMetamodel(PersistentClass persistentClass, SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 
 		name = persistentClass.getEntityName();
 		rootName = persistentClass.getRootClass().getEntityName();
 		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
 
 		identifierProperty = PropertyFactory.buildIdentifierProperty(
 		        persistentClass,
 		        sessionFactory.getIdentifierGenerator( rootName )
 			);
 
 		versioned = persistentClass.isVersioned();
 
 		boolean lazyAvailable = persistentClass.hasPojoRepresentation() &&
 		                        FieldInterceptionHelper.isInstrumented( persistentClass.getMappedClass() );
 		boolean hasLazy = false;
 
 		propertySpan = persistentClass.getPropertyClosureSpan();
 		properties = new StandardProperty[propertySpan];
 		List naturalIdNumbers = new ArrayList();
 		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		propertyNames = new String[propertySpan];
 		propertyTypes = new Type[propertySpan];
 		propertyUpdateability = new boolean[propertySpan];
 		propertyInsertability = new boolean[propertySpan];
 		insertInclusions = new ValueInclusion[propertySpan];
 		updateInclusions = new ValueInclusion[propertySpan];
 		nonlazyPropertyUpdateability = new boolean[propertySpan];
 		propertyCheckability = new boolean[propertySpan];
 		propertyNullability = new boolean[propertySpan];
 		propertyVersionability = new boolean[propertySpan];
 		propertyLaziness = new boolean[propertySpan];
 		cascadeStyles = new CascadeStyle[propertySpan];
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i = 0;
 		int tempVersionProperty = NO_VERSION_INDX;
 		boolean foundCascade = false;
 		boolean foundCollection = false;
 		boolean foundMutable = false;
 		boolean foundNonIdentifierPropertyNamedId = false;
 		boolean foundInsertGeneratedValue = false;
 		boolean foundUpdateGeneratedValue = false;
 		boolean foundUpdateableNaturalIdProperty = false;
 
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 
 			if ( prop == persistentClass.getVersion() ) {
 				tempVersionProperty = i;
 				properties[i] = PropertyFactory.buildVersionProperty( prop, lazyAvailable );
 			}
 			else {
 				properties[i] = PropertyFactory.buildStandardProperty( prop, lazyAvailable );
 			}
 
 			if ( prop.isNaturalIdentifier() ) {
 				naturalIdNumbers.add( i );
 				if ( prop.isUpdateable() ) {
 					foundUpdateableNaturalIdProperty = true;
 				}
 			}
 
 			if ( "id".equals( prop.getName() ) ) {
 				foundNonIdentifierPropertyNamedId = true;
 			}
 
 			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			boolean lazy = prop.isLazy() && lazyAvailable;
 			if ( lazy ) hasLazy = true;
 			propertyLaziness[i] = lazy;
 
 			propertyNames[i] = properties[i].getName();
 			propertyTypes[i] = properties[i].getType();
 			propertyNullability[i] = properties[i].isNullable();
 			propertyUpdateability[i] = properties[i].isUpdateable();
 			propertyInsertability[i] = properties[i].isInsertable();
 			insertInclusions[i] = determineInsertValueGenerationType( prop, properties[i] );
 			updateInclusions[i] = determineUpdateValueGenerationType( prop, properties[i] );
 			propertyVersionability[i] = properties[i].isVersionable();
 			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
 			propertyCheckability[i] = propertyUpdateability[i] ||
 					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
 
 			cascadeStyles[i] = properties[i].getCascadeStyle();
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			if ( properties[i].isLazy() ) {
 				hasLazy = true;
 			}
 
 			if ( properties[i].getCascadeStyle() != CascadeStyle.NONE ) {
 				foundCascade = true;
 			}
 
 			if ( indicatesCollection( properties[i].getType() ) ) {
 				foundCollection = true;
 			}
 
 			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
 				foundMutable = true;
 			}
 
 			if ( insertInclusions[i] != ValueInclusion.NONE ) {
 				foundInsertGeneratedValue = true;
 			}
 
 			if ( updateInclusions[i] != ValueInclusion.NONE ) {
 				foundUpdateGeneratedValue = true;
 			}
 
 			mapPropertyToIndex(prop, i);
 			i++;
 		}
 
 		if (naturalIdNumbers.size()==0) {
 			naturalIdPropertyNumbers = null;
 			hasImmutableNaturalId = false;
 		}
 		else {
 			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
 			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
 		}
 
 		hasInsertGeneratedValues = foundInsertGeneratedValue;
 		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
 
 		hasCascades = foundCascade;
 		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
 		versionPropertyIndex = tempVersionProperty;
 		hasLazyProperties = hasLazy;
         if (hasLazyProperties) LOG.lazyPropertyFetchingAvailable(name);
 
 		lazy = persistentClass.isLazy() && (
 				// TODO: this disables laziness even in non-pojo entity modes:
 				!persistentClass.hasPojoRepresentation() ||
 				!ReflectHelper.isFinalClass( persistentClass.getProxyInterface() )
 		);
 		mutable = persistentClass.isMutable();
 		if ( persistentClass.isAbstract() == null ) {
 			// legacy behavior (with no abstract attribute specified)
 			isAbstract = persistentClass.hasPojoRepresentation() &&
 			             ReflectHelper.isAbstractClass( persistentClass.getMappedClass() );
 		}
 		else {
 			isAbstract = persistentClass.isAbstract().booleanValue();
 			if ( !isAbstract && persistentClass.hasPojoRepresentation() &&
 			     ReflectHelper.isAbstractClass( persistentClass.getMappedClass() ) ) {
                 LOG.entityMappedAsNonAbstract(name);
 			}
 		}
 		selectBeforeUpdate = persistentClass.hasSelectBeforeUpdate();
 		dynamicUpdate = persistentClass.useDynamicUpdate();
 		dynamicInsert = persistentClass.useDynamicInsert();
 
 		polymorphic = persistentClass.isPolymorphic();
 		explicitPolymorphism = persistentClass.isExplicitPolymorphism();
 		inherited = persistentClass.isInherited();
 		superclass = inherited ?
 				persistentClass.getSuperclass().getEntityName() :
 				null;
 		hasSubclasses = persistentClass.hasSubclasses();
 
 		optimisticLockMode = persistentClass.getOptimisticLockMode();
 		if ( optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION ) {
 			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
 		}
 
 		hasCollections = foundCollection;
 		hasMutableProperties = foundMutable;
 
 		iter = persistentClass.getSubclassIterator();
 		while ( iter.hasNext() ) {
 			subclassEntityNames.add( ( (PersistentClass) iter.next() ).getEntityName() );
 		}
 		subclassEntityNames.add( name );
 
 		if ( persistentClass.hasPojoRepresentation() ) {
 			entityNameByInheritenceClassMap.put( persistentClass.getMappedClass(), persistentClass.getEntityName() );
 			iter = persistentClass.getSubclassIterator();
 			while ( iter.hasNext() ) {
 				final PersistentClass pc = ( PersistentClass ) iter.next();
 				entityNameByInheritenceClassMap.put( pc.getMappedClass(), pc.getEntityName() );
 			}
 		}
 
 		entityMode = persistentClass.hasPojoRepresentation() ? EntityMode.POJO : EntityMode.MAP;
 		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
 		final String tuplizerClassName = persistentClass.getTuplizerImplClassName( entityMode );
 		if ( tuplizerClassName == null ) {
 			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, persistentClass );
 		}
 		else {
 			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClassName, this, persistentClass );
 		}
 	}
 
 	public EntityMetamodel(EntityBinding entityBinding, SessionFactoryImplementor sessionFactory) {
 		this.sessionFactory = sessionFactory;
 
 		name = entityBinding.getEntity().getName();
 
 		// TODO: Fix after HHH-6337 is fixed; for now assume entityBinding is the root binding
 		//rootName = entityBinding.getRootEntityBinding().getName();
 		rootName = name;
 		entityType = sessionFactory.getTypeResolver().getTypeFactory().manyToOne( name );
 
 		identifierProperty = PropertyFactory.buildIdentifierProperty(
 		        entityBinding,
 		        sessionFactory.getIdentifierGenerator( rootName )
 		);
 
 		versioned = entityBinding.isVersioned();
 
 		boolean hasPojoRepresentation = false;
 		Class<?> mappedClass = null;
 		Class<?> proxyInterfaceClass = null;
 		boolean lazyAvailable = false;
 		if (  entityBinding.getEntity().getJavaType() != null ) {
 			hasPojoRepresentation = true;
 			mappedClass = entityBinding.getEntity().getJavaType().getClassReference();
 			proxyInterfaceClass = entityBinding.getProxyInterfaceType().getClassReference();
 			lazyAvailable = FieldInterceptionHelper.isInstrumented( mappedClass );
 		}
 
 		boolean hasLazy = false;
 
 		// TODO: Fix after HHH-6337 is fixed; for now assume entityBinding is the root binding
 		//SimpleAttributeBinding rootEntityIdentifier = entityBinding.getRootEntityBinding().getEntityIdentifier().getValueBinding();
 		SimpleAttributeBinding rootEntityIdentifier = entityBinding.getEntityIdentifier().getValueBinding();
 		// entityBinding.getAttributeClosureSpan() includes the identifier binding;
 		// "properties" here excludes the ID, so subtract 1 if the identifier binding is non-null
 		propertySpan = rootEntityIdentifier == null ?
 				entityBinding.getAttributeBindingClosureSpan() :
 				entityBinding.getAttributeBindingClosureSpan() - 1;
 
 		properties = new StandardProperty[propertySpan];
 		List naturalIdNumbers = new ArrayList();
 		// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		propertyNames = new String[propertySpan];
 		propertyTypes = new Type[propertySpan];
 		propertyUpdateability = new boolean[propertySpan];
 		propertyInsertability = new boolean[propertySpan];
 		insertInclusions = new ValueInclusion[propertySpan];
 		updateInclusions = new ValueInclusion[propertySpan];
 		nonlazyPropertyUpdateability = new boolean[propertySpan];
 		propertyCheckability = new boolean[propertySpan];
 		propertyNullability = new boolean[propertySpan];
 		propertyVersionability = new boolean[propertySpan];
 		propertyLaziness = new boolean[propertySpan];
 		cascadeStyles = new CascadeStyle[propertySpan];
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 
 		int i = 0;
 		int tempVersionProperty = NO_VERSION_INDX;
 		boolean foundCascade = false;
 		boolean foundCollection = false;
 		boolean foundMutable = false;
 		boolean foundNonIdentifierPropertyNamedId = false;
 		boolean foundInsertGeneratedValue = false;
 		boolean foundUpdateGeneratedValue = false;
 		boolean foundUpdateableNaturalIdProperty = false;
 
 		for ( AttributeBinding attributeBinding : entityBinding.getAttributeBindingClosure() ) {
 			if ( attributeBinding == rootEntityIdentifier ) {
 				// skip the identifier attribute binding
 				continue;
 			}
 
 			if ( attributeBinding == entityBinding.getVersioningValueBinding() ) {
 				tempVersionProperty = i;
 				properties[i] = PropertyFactory.buildVersionProperty( entityBinding.getVersioningValueBinding(), lazyAvailable );
 			}
 			else {
 				properties[i] = PropertyFactory.buildStandardProperty( attributeBinding, lazyAvailable );
 			}
 
 			// TODO: fix when natural IDs are added (HHH-6354)
 			//if ( attributeBinding.isNaturalIdentifier() ) {
 			//	naturalIdNumbers.add( i );
 			//	if ( attributeBinding.isUpdateable() ) {
 			//		foundUpdateableNaturalIdProperty = true;
 			//	}
 			//}
 
 			if ( "id".equals( attributeBinding.getAttribute().getName() ) ) {
 				foundNonIdentifierPropertyNamedId = true;
 			}
 
 			// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 			boolean lazy = attributeBinding.isLazy() && lazyAvailable;
 			if ( lazy ) hasLazy = true;
 			propertyLaziness[i] = lazy;
 
 			propertyNames[i] = properties[i].getName();
 			propertyTypes[i] = properties[i].getType();
 			propertyNullability[i] = properties[i].isNullable();
 			propertyUpdateability[i] = properties[i].isUpdateable();
 			propertyInsertability[i] = properties[i].isInsertable();
 			insertInclusions[i] = determineInsertValueGenerationType( attributeBinding, properties[i] );
 			updateInclusions[i] = determineUpdateValueGenerationType( attributeBinding, properties[i] );
 			propertyVersionability[i] = properties[i].isVersionable();
 			nonlazyPropertyUpdateability[i] = properties[i].isUpdateable() && !lazy;
 			propertyCheckability[i] = propertyUpdateability[i] ||
 					( propertyTypes[i].isAssociationType() && ( (AssociationType) propertyTypes[i] ).isAlwaysDirtyChecked() );
 
 			cascadeStyles[i] = properties[i].getCascadeStyle();
 			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 			if ( properties[i].isLazy() ) {
 				hasLazy = true;
 			}
 
 			if ( properties[i].getCascadeStyle() != CascadeStyle.NONE ) {
 				foundCascade = true;
 			}
 
 			if ( indicatesCollection( properties[i].getType() ) ) {
 				foundCollection = true;
 			}
 
 			if ( propertyTypes[i].isMutable() && propertyCheckability[i] ) {
 				foundMutable = true;
 			}
 
 			if ( insertInclusions[i] != ValueInclusion.NONE ) {
 				foundInsertGeneratedValue = true;
 			}
 
 			if ( updateInclusions[i] != ValueInclusion.NONE ) {
 				foundUpdateGeneratedValue = true;
 			}
 
 			mapPropertyToIndex(attributeBinding.getAttribute(), i);
 			i++;
 		}
 
 		if (naturalIdNumbers.size()==0) {
 			naturalIdPropertyNumbers = null;
 			hasImmutableNaturalId = false;
 		}
 		else {
 			naturalIdPropertyNumbers = ArrayHelper.toIntArray(naturalIdNumbers);
 			hasImmutableNaturalId = !foundUpdateableNaturalIdProperty;
 		}
 
 		hasInsertGeneratedValues = foundInsertGeneratedValue;
 		hasUpdateGeneratedValues = foundUpdateGeneratedValue;
 
 		hasCascades = foundCascade;
 		hasNonIdentifierPropertyNamedId = foundNonIdentifierPropertyNamedId;
 		versionPropertyIndex = tempVersionProperty;
 		hasLazyProperties = hasLazy;
 		if (hasLazyProperties) {
 			LOG.lazyPropertyFetchingAvailable( name );
 		}
 
 		lazy = entityBinding.isLazy() && (
 				// TODO: this disables laziness even in non-pojo entity modes:
 				! hasPojoRepresentation ||
 				! ReflectHelper.isFinalClass( proxyInterfaceClass )
 		);
 		mutable = entityBinding.isMutable();
 		if ( entityBinding.isAbstract() == null ) {
 			// legacy behavior (with no abstract attribute specified)
 			isAbstract = hasPojoRepresentation &&
 			             ReflectHelper.isAbstractClass( mappedClass );
 		}
 		else {
 			isAbstract = entityBinding.isAbstract().booleanValue();
 			if ( !isAbstract && hasPojoRepresentation &&
 					ReflectHelper.isAbstractClass( mappedClass ) ) {
 				LOG.entityMappedAsNonAbstract(name);
 			}
 		}
 		selectBeforeUpdate = entityBinding.isSelectBeforeUpdate();
 		dynamicUpdate = entityBinding.isDynamicUpdate();
 		dynamicInsert = entityBinding.isDynamicInsert();
 
 		// TODO: fix this when can get subclass info from EntityBinding (HHH-6337)
 		//  for now set hasSubclasses to false
 		//hasSubclasses = entityBinding.hasSubclasses();
 		hasSubclasses = false;
 
 		//polymorphic = ! entityBinding.isRoot() || entityBinding.hasSubclasses();
 		polymorphic = ! entityBinding.isRoot() || hasSubclasses;
 
 		explicitPolymorphism = entityBinding.isExplicitPolymorphism();
 		inherited = ! entityBinding.isRoot();
 		superclass = inherited ?
 				entityBinding.getEntity().getSuperType().getName() :
 				null;
 
 		optimisticLockMode = entityBinding.getOptimisticLockMode();
 		if ( optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
 		if ( versionPropertyIndex != NO_VERSION_INDX && optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION ) {
 			throw new MappingException( "version and optimistic-lock=all|dirty are not a valid combination : " + name );
 		}
 
 		hasCollections = foundCollection;
 		hasMutableProperties = foundMutable;
 
 		// TODO: fix this when can get subclass info from EntityBinding (HHH-6337)
 		// TODO: uncomment when it's possible to get subclasses from an EntityBinding
 		//iter = entityBinding.getSubclassIterator();
 		//while ( iter.hasNext() ) {
 		//	subclassEntityNames.add( ( (PersistentClass) iter.next() ).getEntityName() );
 		//}
 		subclassEntityNames.add( name );
 
 		if ( mappedClass != null ) {
 			entityNameByInheritenceClassMap.put( mappedClass, name );
 		// TODO: uncomment when it's possible to get subclasses from an EntityBinding
 		//	iter = entityBinding.getSubclassIterator();
 		//	while ( iter.hasNext() ) {
 		//		final EntityBinding subclassEntityBinding = ( EntityBinding ) iter.next();
 		//		entityNameByInheritenceClassMap.put(
 		//				subclassEntityBinding.getEntity().getPojoEntitySpecifics().getEntityClass(),
 		//				subclassEntityBinding.getEntity().getName() );
 		//	}
 		}
 
 		entityMode = hasPojoRepresentation ? EntityMode.POJO : EntityMode.MAP;
 		final EntityTuplizerFactory entityTuplizerFactory = sessionFactory.getSettings().getEntityTuplizerFactory();
-		Class<EntityTuplizer> tuplizerClass = entityBinding.getEntityTuplizerClass();
+		Class<EntityTuplizer> tuplizerClass = entityBinding.getCustomEntityTuplizerClass();
 
 		if ( tuplizerClass == null ) {
 			entityTuplizer = entityTuplizerFactory.constructDefaultTuplizer( entityMode, this, entityBinding );
 		}
 		else {
 			entityTuplizer = entityTuplizerFactory.constructTuplizer( tuplizerClass, this, entityBinding );
 		}
 	}
 
 	private ValueInclusion determineInsertValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isInsertGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialInsertComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
 	private ValueInclusion determineInsertValueGenerationType(AttributeBinding mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isInsertGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		// TODO: fix the following when components are working (HHH-6173)
 		//else if ( mappingProperty.getValue() instanceof ComponentAttributeBinding ) {
 		//	if ( hasPartialInsertComponentGeneration( ( ComponentAttributeBinding ) mappingProperty.getValue() ) ) {
 		//		return ValueInclusion.PARTIAL;
 		//	}
 		//}
 		return ValueInclusion.NONE;
 	}
 
 	private boolean hasPartialInsertComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			Property prop = ( Property ) subProperties.next();
 			if ( prop.getGeneration() == PropertyGeneration.ALWAYS || prop.getGeneration() == PropertyGeneration.INSERT ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialInsertComponentGeneration( ( Component ) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private ValueInclusion determineUpdateValueGenerationType(Property mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isUpdateGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		else if ( mappingProperty.getValue() instanceof Component ) {
 			if ( hasPartialUpdateComponentGeneration( ( Component ) mappingProperty.getValue() ) ) {
 				return ValueInclusion.PARTIAL;
 			}
 		}
 		return ValueInclusion.NONE;
 	}
 
 	private ValueInclusion determineUpdateValueGenerationType(AttributeBinding mappingProperty, StandardProperty runtimeProperty) {
 		if ( runtimeProperty.isUpdateGenerated() ) {
 			return ValueInclusion.FULL;
 		}
 		// TODO: fix the following when components are working (HHH-6173)
 		//else if ( mappingProperty.getValue() instanceof ComponentAttributeBinding ) {
 		//	if ( hasPartialUpdateComponentGeneration( ( ComponentAttributeBinding ) mappingProperty.getValue() ) ) {
 		//		return ValueInclusion.PARTIAL;
 		//	}
 		//}
 		return ValueInclusion.NONE;
 	}
 
 	private boolean hasPartialUpdateComponentGeneration(Component component) {
 		Iterator subProperties = component.getPropertyIterator();
 		while ( subProperties.hasNext() ) {
 			Property prop = ( Property ) subProperties.next();
 			if ( prop.getGeneration() == PropertyGeneration.ALWAYS ) {
 				return true;
 			}
 			else if ( prop.getValue() instanceof Component ) {
 				if ( hasPartialUpdateComponentGeneration( ( Component ) prop.getValue() ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	private void mapPropertyToIndex(Property prop, int i) {
 		propertyIndexes.put( prop.getName(), i );
 		if ( prop.getValue() instanceof Component ) {
 			Iterator iter = ( (Component) prop.getValue() ).getPropertyIterator();
 			while ( iter.hasNext() ) {
 				Property subprop = (Property) iter.next();
 				propertyIndexes.put(
 						prop.getName() + '.' + subprop.getName(),
 						i
 					);
 			}
 		}
 	}
 
 	private void mapPropertyToIndex(Attribute attribute, int i) {
 		propertyIndexes.put( attribute.getName(), i );
 		if ( attribute.isSingular() &&
 				( ( SingularAttribute ) attribute ).getSingularAttributeType().getNature() == TypeNature.COMPONENT ) {
 			org.hibernate.metamodel.domain.Component component =
 					( org.hibernate.metamodel.domain.Component ) ( ( SingularAttribute ) attribute ).getSingularAttributeType();
 			for ( Attribute subAttribute : component.getAttributes() ) {
 				propertyIndexes.put(
 						attribute.getName() + '.' + subAttribute.getName(),
 						i
 					);
 			}
 		}
 	}
 
 	public EntityTuplizer getTuplizer() {
 		return entityTuplizer;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return naturalIdPropertyNumbers;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return naturalIdPropertyNumbers!=null;
 	}
 
 	public boolean hasImmutableNaturalId() {
 		return hasImmutableNaturalId;
 	}
 
 	public Set getSubclassEntityNames() {
 		return subclassEntityNames;
 	}
 
 	private boolean indicatesCollection(Type type) {
 		if ( type.isCollectionType() ) {
 			return true;
 		}
 		else if ( type.isComponentType() ) {
 			Type[] subtypes = ( (CompositeType) type ).getSubtypes();
 			for ( int i = 0; i < subtypes.length; i++ ) {
 				if ( indicatesCollection( subtypes[i] ) ) {
 					return true;
 				}
 			}
 		}
 		return false;
 	}
 
 	public SessionFactoryImplementor getSessionFactory() {
 		return sessionFactory;
 	}
 
 	public String getName() {
 		return name;
 	}
 
 	public String getRootName() {
 		return rootName;
 	}
 
 	public EntityType getEntityType() {
 		return entityType;
 	}
 
 	public IdentifierProperty getIdentifierProperty() {
 		return identifierProperty;
 	}
 
 	public int getPropertySpan() {
 		return propertySpan;
 	}
 
 	public int getVersionPropertyIndex() {
 		return versionPropertyIndex;
 	}
 
 	public VersionProperty getVersionProperty() {
 		if ( NO_VERSION_INDX == versionPropertyIndex ) {
 			return null;
 		}
 		else {
 			return ( VersionProperty ) properties[ versionPropertyIndex ];
 		}
 	}
 
 	public StandardProperty[] getProperties() {
 		return properties;
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		Integer index = getPropertyIndexOrNull(propertyName);
 		if ( index == null ) {
 			throw new HibernateException("Unable to resolve property: " + propertyName);
 		}
 		return index.intValue();
 	}
 
 	public Integer getPropertyIndexOrNull(String propertyName) {
 		return (Integer) propertyIndexes.get( propertyName );
 	}
 
 	public boolean hasCollections() {
 		return hasCollections;
 	}
 
 	public boolean hasMutableProperties() {
 		return hasMutableProperties;
 	}
 
 	public boolean hasNonIdentifierPropertyNamedId() {
 		return hasNonIdentifierPropertyNamedId;
 	}
 
 	public boolean hasLazyProperties() {
 		return hasLazyProperties;
 	}
 
 	public boolean hasCascades() {
 		return hasCascades;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	public int getOptimisticLockMode() {
 		return optimisticLockMode;
 	}
 
 	public boolean isPolymorphic() {
 		return polymorphic;
 	}
 
 	public String getSuperclass() {
 		return superclass;
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	public boolean isInherited() {
 		return inherited;
 	}
 
 	public boolean hasSubclasses() {
 		return hasSubclasses;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public boolean isVersioned() {
 		return versioned;
 	}
 
 	public boolean isAbstract() {
 		return isAbstract;
 	}
 
 	/**
 	 * Return the entity-name mapped to the given class within our inheritance hierarchy, if any.
 	 *
 	 * @param inheritenceClass The class for which to resolve the entity-name.
 	 * @return The mapped entity-name, or null if no such mapping was found.
 	 */
 	public String findEntityNameByEntityClass(Class inheritenceClass) {
 		return ( String ) entityNameByInheritenceClassMap.get( inheritenceClass );
 	}
 
 	@Override
     public String toString() {
 		return "EntityMetamodel(" + name + ':' + ArrayHelper.toString(properties) + ')';
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public String[] getPropertyNames() {
 		return propertyNames;
 	}
 
 	public Type[] getPropertyTypes() {
 		return propertyTypes;
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return propertyLaziness;
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return propertyUpdateability;
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return propertyCheckability;
 	}
 
 	public boolean[] getNonlazyPropertyUpdateability() {
 		return nonlazyPropertyUpdateability;
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return propertyInsertability;
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return insertInclusions;
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return updateInclusions;
 	}
 
 	public boolean[] getPropertyNullability() {
 		return propertyNullability;
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return propertyVersionability;
 	}
 
 	public CascadeStyle[] getCascadeStyles() {
 		return cascadeStyles;
 	}
 
 	public boolean hasInsertGeneratedValues() {
 		return hasInsertGeneratedValues;
 	}
 
 	public boolean hasUpdateGeneratedValues() {
 		return hasUpdateGeneratedValues;
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/idclassgeneratedvalue/MultiplePK.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/idclassgeneratedvalue/MultiplePK.java
index d97f24739a..480a74f5d0 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/idclassgeneratedvalue/MultiplePK.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/idclassgeneratedvalue/MultiplePK.java
@@ -1,97 +1,97 @@
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
 package org.hibernate.test.annotations.idclassgeneratedvalue;
 import java.io.Serializable;
 
 /**
  * MultiplePK
  *
  * @author <a href="mailto:stale.pedersen@jboss.org">Stale W. Pedersen</a>
  */
 public class MultiplePK implements Serializable
 {
    private final Long id1;
    private final Long id2;
    private final Long id3;
-// AnnotationBinder (incorrectly) requires this to be transient; see HHH-4819 and HHH-4820
+// AnnotationSourceProcessor (incorrectly) requires this to be transient; see HHH-4819 and HHH-4820
    private final transient int cachedHashCode;
 
    private MultiplePK()
    {
       id1 = null;
       id2 = null;
       id3 = null;
       cachedHashCode = super.hashCode();
    }
    
    public MultiplePK(Long id1, Long id2, Long id3)
    {
       this.id1 = id1;
       this.id2 = id2;
       this.id3 = id3;
       this.cachedHashCode = calculateHashCode();
    }
    
 
    private int calculateHashCode() {
        int result = id1.hashCode();
        result = 31 * result + id2.hashCode();
        return result;
    }
 
    public Long getId1() {
        return id1;
    }
 
    public Long getId2() {
        return id2;
    }
    
    public Long getId3() {
       return id3;
   }
 
    @Override
    public boolean equals(Object o) 
    {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) 
        {
            return false;
        }
 
        MultiplePK multiplePK = (MultiplePK) o;
 
        return id1.equals( multiplePK.id1 )
                && id2.equals( multiplePK.id2 )
                && id3.equals( multiplePK.id3);
    }
 
    @Override
    public int hashCode() {
        return cachedHashCode;
    }
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/legacy/Wicked.hbm.xml b/hibernate-core/src/test/java/org/hibernate/test/legacy/Wicked.hbm.xml
index c79fbb5ce4..2e5424b251 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/legacy/Wicked.hbm.xml
+++ b/hibernate-core/src/test/java/org/hibernate/test/legacy/Wicked.hbm.xml
@@ -1,71 +1,71 @@
 <?xml version="1.0"?>
 <!DOCTYPE hibernate-mapping PUBLIC 
 	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
 	"http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">
 
-<!-- Mapping document mainly used for testing non-reflective Binder + meta inheritance -->
+<!-- Mapping document mainly used for testing non-reflective SourceProcessor + meta inheritance -->
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
 
