diff --git a/hibernate-core/src/main/java/org/hibernate/InvalidMappingException.java b/hibernate-core/src/main/java/org/hibernate/InvalidMappingException.java
index afee84203c..61396392a3 100644
--- a/hibernate-core/src/main/java/org/hibernate/InvalidMappingException.java
+++ b/hibernate-core/src/main/java/org/hibernate/InvalidMappingException.java
@@ -1,79 +1,79 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 20082011, Red Hat Inc. or third-party contributors as
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
 package org.hibernate;
 
 import org.hibernate.internal.util.xml.XmlDocument;
-import org.hibernate.metamodel.source.Origin;
+import org.hibernate.metamodel.binder.Origin;
 
 /**
  * Thrown when a mapping is found to be invalid.
  * Similar to MappingException, but this contains more info about the path and type of mapping (e.g. file, resource or url)
  * 
  * @author Max Rydahl Andersen
  * @author Steve Ebersole
  */
 public class InvalidMappingException extends MappingException {
 	private final String path;
 	private final String type;
 
 	public InvalidMappingException(String customMessage, String type, String path, Throwable cause) {
 		super(customMessage, cause);
 		this.type=type;
 		this.path=path;
 	}
 
 	public InvalidMappingException(String customMessage, String type, String path) {
 		super(customMessage);
 		this.type=type;
 		this.path=path;
 	}
 
 	public InvalidMappingException(String customMessage, XmlDocument xmlDocument, Throwable cause) {
 		this( customMessage, xmlDocument.getOrigin().getType(), xmlDocument.getOrigin().getName(), cause );
 	}
 
 	public InvalidMappingException(String customMessage, XmlDocument xmlDocument) {
 		this( customMessage, xmlDocument.getOrigin().getType(), xmlDocument.getOrigin().getName() );
 	}
 
 	public InvalidMappingException(String customMessage, Origin origin) {
 		this( customMessage, origin.getType().toString(), origin.getName() );
 	}
 
 	public InvalidMappingException(String type, String path) {
 		this("Could not parse mapping document from " + type + (path==null?"":" " + path), type, path);
 	}
 
 	public InvalidMappingException(String type, String path, Throwable cause) {
 		this("Could not parse mapping document from " + type + (path==null?"":" " + path), type, path, cause);		
 	}
 
 	public String getType() {
 		return type;
 	}
 	
 	public String getPath() {
 		return path;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index 5077150352..f28d1ed1b0 100644
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
-			 * Right now HbmSourceProcessor does not support the
+			 * Right now HbmSourceProcessorImpl does not support the
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
index 2696736bb8..7481c79e78 100644
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
-			 * HbmSourceProcessor does this only when property-ref != null, but IMO, it makes sense event if it is null
+			 * HbmSourceProcessorImpl does this only when property-ref != null, but IMO, it makes sense event if it is null
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
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/OptimisticLockStyle.java b/hibernate-core/src/main/java/org/hibernate/engine/OptimisticLockStyle.java
new file mode 100644
index 0000000000..3d63e76dc9
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/engine/OptimisticLockStyle.java
@@ -0,0 +1,46 @@
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
+package org.hibernate.engine;
+
+/**
+ * @author Steve Ebersole
+ */
+public enum OptimisticLockStyle {
+	/**
+	 * no optimistic locking
+	 */
+	NONE,
+	/**
+	 * use a dedicated version column
+	 */
+	VERSION,
+	/**
+	 * dirty columns are compared
+	 */
+	DIRTY,
+	/**
+	 * all columns are compared
+	 */
+	ALL
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/engine/internal/Versioning.java b/hibernate-core/src/main/java/org/hibernate/engine/internal/Versioning.java
index 3083742a9d..f9bb14cc25 100644
--- a/hibernate-core/src/main/java/org/hibernate/engine/internal/Versioning.java
+++ b/hibernate-core/src/main/java/org/hibernate/engine/internal/Versioning.java
@@ -1,183 +1,186 @@
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
 package org.hibernate.engine.internal;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.type.VersionType;
 
 /**
  * Utilities for dealing with optimisitic locking values.
  *
  * @author Gavin King
  */
 public final class Versioning {
+
+	// todo : replace these constants with references to org.hibernate.annotations.OptimisticLockType enum
+
 	/**
 	 * Apply no optimistic locking
 	 */
 	public static final int OPTIMISTIC_LOCK_NONE = -1;
 
 	/**
 	 * Apply optimisitc locking based on the defined version or timestamp
 	 * property.
 	 */
 	public static final int OPTIMISTIC_LOCK_VERSION = 0;
 
 	/**
 	 * Apply optimisitc locking based on the a current vs. snapshot comparison
 	 * of <b>all</b> properties.
 	 */
 	public static final int OPTIMISTIC_LOCK_ALL = 2;
 
 	/**
 	 * Apply optimisitc locking based on the a current vs. snapshot comparison
 	 * of <b>dirty</b> properties.
 	 */
 	public static final int OPTIMISTIC_LOCK_DIRTY = 1;
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, Versioning.class.getName());
 
 	/**
 	 * Private constructor disallowing instantiation.
 	 */
 	private Versioning() {}
 
 	/**
 	 * Create an initial optimisitc locking value according the {@link VersionType}
 	 * contract for the version property.
 	 *
 	 * @param versionType The version type.
 	 * @param session The originating session
 	 * @return The initial optimisitc locking value
 	 */
 	private static Object seed(VersionType versionType, SessionImplementor session) {
 		Object seed = versionType.seed( session );
         LOG.trace("Seeding: " + seed);
 		return seed;
 	}
 
 	/**
 	 * Create an initial optimisitc locking value according the {@link VersionType}
 	 * contract for the version property <b>if required</b> and inject it into
 	 * the snapshot state.
 	 *
 	 * @param fields The current snapshot state
 	 * @param versionProperty The index of the version property
 	 * @param versionType The version type
 	 * @param session The orginating session
 	 * @return True if we injected a new version value into the fields array; false
 	 * otherwise.
 	 */
 	public static boolean seedVersion(
 	        Object[] fields,
 	        int versionProperty,
 	        VersionType versionType,
 	        SessionImplementor session) {
 		Object initialVersion = fields[versionProperty];
 		if (
 			initialVersion==null ||
 			// This next bit is to allow for both unsaved-value="negative"
 			// and for "older" behavior where version number did not get
 			// seeded if it was already set in the object
 			// TODO: shift it into unsaved-value strategy
 			( (initialVersion instanceof Number) && ( (Number) initialVersion ).longValue()<0 )
 		) {
 			fields[versionProperty] = seed( versionType, session );
 			return true;
 		}
         LOG.trace("Using initial version: " + initialVersion);
         return false;
 	}
 
 
 	/**
 	 * Generate the next increment in the optimisitc locking value according
 	 * the {@link VersionType} contract for the version property.
 	 *
 	 * @param version The current version
 	 * @param versionType The version type
 	 * @param session The originating session
 	 * @return The incremented optimistic locking value.
 	 */
 	public static Object increment(Object version, VersionType versionType, SessionImplementor session) {
 		Object next = versionType.next( version, session );
         if (LOG.isTraceEnabled()) LOG.trace("Incrementing: " + versionType.toLoggableString(version, session.getFactory()) + " to "
                                             + versionType.toLoggableString(next, session.getFactory()));
 		return next;
 	}
 
 	/**
 	 * Inject the optimisitc locking value into the entity state snapshot.
 	 *
 	 * @param fields The state snapshot
 	 * @param version The optimisitc locking value
 	 * @param persister The entity persister
 	 */
 	public static void setVersion(Object[] fields, Object version, EntityPersister persister) {
 		if ( !persister.isVersioned() ) {
 			return;
 		}
 		fields[ persister.getVersionProperty() ] = version;
 	}
 
 	/**
 	 * Extract the optimisitc locking value out of the entity state snapshot.
 	 *
 	 * @param fields The state snapshot
 	 * @param persister The entity persister
 	 * @return The extracted optimisitc locking value
 	 */
 	public static Object getVersion(Object[] fields, EntityPersister persister) {
 		if ( !persister.isVersioned() ) {
 			return null;
 		}
 		return fields[ persister.getVersionProperty() ];
 	}
 
 	/**
 	 * Do we need to increment the version number, given the dirty properties?
 	 *
 	 * @param dirtyProperties The array of property indexes which were deemed dirty
 	 * @param hasDirtyCollections Were any collections found to be dirty (structurally changed)
 	 * @param propertyVersionability An array indicating versionability of each property.
 	 * @return True if a version increment is required; false otherwise.
 	 */
 	public static boolean isVersionIncrementRequired(
 			final int[] dirtyProperties,
 			final boolean hasDirtyCollections,
 			final boolean[] propertyVersionability) {
 		if ( hasDirtyCollections ) {
 			return true;
 		}
 		for ( int i = 0; i < dirtyProperties.length; i++ ) {
 			if ( propertyVersionability[ dirtyProperties[i] ] ) {
 				return true;
 			}
 		}
 	    return false;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerServiceInitiator.java
index b773609c9f..04a49bf5c9 100644
--- a/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/event/service/internal/EventListenerServiceInitiator.java
@@ -1,61 +1,61 @@
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
 package org.hibernate.event.service.internal;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.event.service.spi.EventListenerRegistry;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 
 /**
  * Service initiator for {@link EventListenerRegistry}
  *
  * @author Steve Ebersole
  */
 public class EventListenerServiceInitiator implements SessionFactoryServiceInitiator<EventListenerRegistry> {
 	public static final EventListenerServiceInitiator INSTANCE = new EventListenerServiceInitiator();
 
 	@Override
 	public Class<EventListenerRegistry> getServiceInitiated() {
 		return EventListenerRegistry.class;
 	}
 
 	@Override
 	public EventListenerRegistry initiateService(
 			SessionFactoryImplementor sessionFactory,
 			Configuration configuration,
 			ServiceRegistryImplementor registry) {
 		return new EventListenerRegistryImpl();
 	}
 
 	@Override
 	public EventListenerRegistry initiateService(
 			SessionFactoryImplementor sessionFactory,
 			MetadataImplementor metadata,
 			ServiceRegistryImplementor registry) {
 		return new EventListenerRegistryImpl();
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
index 03ee76c9e3..2b95802fb2 100644
--- a/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/internal/SessionFactoryImpl.java
@@ -1,1118 +1,1117 @@
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
 package org.hibernate.internal;
 
 import javax.naming.NamingException;
 import javax.naming.Reference;
 import javax.naming.StringRefAddr;
 import java.io.IOException;
 import java.io.InvalidObjectException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.io.ObjectStreamException;
 import java.io.Serializable;
 import java.sql.Connection;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.concurrent.ConcurrentMap;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.Cache;
 import org.hibernate.ConnectionReleaseMode;
 import org.hibernate.EmptyInterceptor;
-import org.hibernate.EntityMode;
 import org.hibernate.EntityNameResolver;
 import org.hibernate.HibernateException;
 import org.hibernate.Interceptor;
 import org.hibernate.MappingException;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.QueryException;
 import org.hibernate.Session;
 import org.hibernate.SessionBuilder;
 import org.hibernate.SessionFactory;
 import org.hibernate.SessionFactoryObserver;
 import org.hibernate.StatelessSession;
 import org.hibernate.StatelessSessionBuilder;
 import org.hibernate.TypeHelper;
 import org.hibernate.cache.internal.CacheDataDescriptionImpl;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.CollectionRegion;
 import org.hibernate.cache.spi.EntityRegion;
 import org.hibernate.cache.spi.QueryCache;
 import org.hibernate.cache.spi.Region;
 import org.hibernate.cache.spi.UpdateTimestampsCache;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.access.RegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.cfg.Environment;
 import org.hibernate.cfg.Settings;
 import org.hibernate.cfg.SettingsFactory;
 import org.hibernate.context.internal.JTASessionContext;
 import org.hibernate.context.internal.ManagedSessionContext;
 import org.hibernate.context.internal.ThreadLocalSessionContext;
 import org.hibernate.context.spi.CurrentSessionContext;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.dialect.function.SQLFunctionRegistry;
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.jdbc.spi.JdbcServices;
 import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
 import org.hibernate.engine.profile.Association;
 import org.hibernate.engine.profile.Fetch;
 import org.hibernate.engine.profile.FetchProfile;
 import org.hibernate.engine.query.spi.QueryPlanCache;
 import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.transaction.internal.TransactionCoordinatorImpl;
 import org.hibernate.engine.transaction.spi.TransactionEnvironment;
 import org.hibernate.exception.spi.SQLExceptionConverter;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.UUIDGenerator;
 import org.hibernate.id.factory.IdentifierGeneratorFactory;
 import org.hibernate.integrator.spi.Integrator;
 import org.hibernate.integrator.spi.IntegratorService;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.CollectionHelper;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metadata.CollectionMetadata;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Loadable;
 import org.hibernate.persister.entity.Queryable;
 import org.hibernate.persister.spi.PersisterFactory;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.config.spi.ConfigurationService;
 import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;
 import org.hibernate.service.jndi.spi.JndiService;
 import org.hibernate.service.jta.platform.spi.JtaPlatform;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 import org.hibernate.stat.Statistics;
 import org.hibernate.stat.spi.StatisticsImplementor;
 import org.hibernate.tool.hbm2ddl.SchemaExport;
 import org.hibernate.tool.hbm2ddl.SchemaUpdate;
 import org.hibernate.tool.hbm2ddl.SchemaValidator;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeResolver;
 
 
 /**
  * Concrete implementation of the <tt>SessionFactory</tt> interface. Has the following
  * responsibilities
  * <ul>
  * <li>caches configuration settings (immutably)
  * <li>caches "compiled" mappings ie. <tt>EntityPersister</tt>s and
  *     <tt>CollectionPersister</tt>s (immutable)
  * <li>caches "compiled" queries (memory sensitive cache)
  * <li>manages <tt>PreparedStatement</tt>s
  * <li> delegates JDBC <tt>Connection</tt> management to the <tt>ConnectionProvider</tt>
  * <li>factory for instances of <tt>SessionImpl</tt>
  * </ul>
  * This class must appear immutable to clients, even if it does all kinds of caching
  * and pooling under the covers. It is crucial that the class is not only thread
  * safe, but also highly concurrent. Synchronization must be used extremely sparingly.
  *
  * @see org.hibernate.service.jdbc.connections.spi.ConnectionProvider
  * @see org.hibernate.Session
  * @see org.hibernate.hql.spi.QueryTranslator
  * @see org.hibernate.persister.entity.EntityPersister
  * @see org.hibernate.persister.collection.CollectionPersister
  * @author Gavin King
  */
 public final class SessionFactoryImpl
 		implements SessionFactory, SessionFactoryImplementor {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, SessionFactoryImpl.class.getName());
 	private static final IdentifierGenerator UUID_GENERATOR = UUIDGenerator.buildSessionFactoryUniqueIdentifierGenerator();
 
 	private final String name;
 	private final String uuid;
 
 	private final transient Map entityPersisters;
 	private final transient Map<String,ClassMetadata> classMetadata;
 	private final transient Map collectionPersisters;
 	private final transient Map collectionMetadata;
 	private final transient Map<String,Set<String>> collectionRolesByEntityParticipant;
 	private final transient Map identifierGenerators;
 	private final transient Map<String, NamedQueryDefinition> namedQueries;
 	private final transient Map<String, NamedSQLQueryDefinition> namedSqlQueries;
 	private final transient Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
 	private final transient Map<String, FilterDefinition> filters;
 	private final transient Map fetchProfiles;
 	private final transient Map<String,String> imports;
 	private final transient SessionFactoryServiceRegistry serviceRegistry;
         private final transient JdbcServices jdbcServices;
         private final transient Dialect dialect;
 	private final transient Settings settings;
 	private final transient Properties properties;
 	private transient SchemaExport schemaExport;
 	private final transient QueryCache queryCache;
 	private final transient UpdateTimestampsCache updateTimestampsCache;
 	private final transient Map<String,QueryCache> queryCaches;
 	private final transient ConcurrentMap<String,Region> allCacheRegions = new ConcurrentHashMap<String, Region>();
 	private final transient CurrentSessionContext currentSessionContext;
 	private final transient SQLFunctionRegistry sqlFunctionRegistry;
 	private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
 	private final transient ConcurrentHashMap<EntityNameResolver,Object> entityNameResolvers = new ConcurrentHashMap<EntityNameResolver, Object>();
 	private final transient QueryPlanCache queryPlanCache;
 	private final transient Cache cacheAccess = new CacheImpl();
 	private transient boolean isClosed = false;
 	private final transient TypeResolver typeResolver;
 	private final transient TypeHelper typeHelper;
 	private final transient TransactionEnvironment transactionEnvironment;
 	private final transient SessionFactoryOptions sessionFactoryOptions;
 
 	@SuppressWarnings( {"unchecked"} )
 	public SessionFactoryImpl(
 			final Configuration cfg,
 	        Mapping mapping,
 			ServiceRegistry serviceRegistry,
 	        Settings settings,
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.debug( "Building session factory" );
 
 		sessionFactoryOptions = new SessionFactoryOptions() {
 			private EntityNotFoundDelegate entityNotFoundDelegate;
 
 			@Override
 			public Interceptor getInterceptor() {
 				return cfg.getInterceptor();
 			}
 
 			@Override
 			public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 				if ( entityNotFoundDelegate == null ) {
 					if ( cfg.getEntityNotFoundDelegate() != null ) {
 						entityNotFoundDelegate = cfg.getEntityNotFoundDelegate();
 					}
 					else {
 						entityNotFoundDelegate = new EntityNotFoundDelegate() {
 							public void handleEntityNotFound(String entityName, Serializable id) {
 								throw new ObjectNotFoundException( id, entityName );
 							}
 						};
 					}
 				}
 				return entityNotFoundDelegate;
 			}
 		};
 
 		this.settings = settings;
 
 		this.properties = new Properties();
 		this.properties.putAll( cfg.getProperties() );
 
 		this.serviceRegistry = serviceRegistry.getService( SessionFactoryServiceRegistryFactory.class ).buildServiceRegistry(
 				this,
 				cfg
 		);
                 this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
                 this.dialect = this.jdbcServices.getDialect();
 		this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = cfg.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		this.filters.putAll( cfg.getFilterDefinitions() );
 
         LOG.debugf("Session factory constructed with filter configurations : %s", filters);
         LOG.debugf("Instantiating session factory with properties: %s", properties);
 
 		// Caches
 		settings.getRegionFactory().start( settings, properties );
 		this.queryPlanCache = new QueryPlanCache( this );
 
 		// todo : everything above here consider implementing as standard SF service.  specifically: stats, caches, types, function-reg
 
 		class IntegratorObserver implements SessionFactoryObserver {
 			private ArrayList<Integrator> integrators = new ArrayList<Integrator>();
 
 			@Override
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 
 			@Override
 			public void sessionFactoryClosed(SessionFactory factory) {
 				for ( Integrator integrator : integrators ) {
 					integrator.disintegrate( SessionFactoryImpl.this, SessionFactoryImpl.this.serviceRegistry );
 				}
 			}
 		}
 
 		final IntegratorObserver integratorObserver = new IntegratorObserver();
 		this.observer.addObserver( integratorObserver );
 		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
 			integrator.integrate( cfg, this, this.serviceRegistry );
 			integratorObserver.integrators.add( integrator );
 		}
 
 		//Generators:
 
 		identifierGenerators = new HashMap();
 		Iterator classes = cfg.getClassMappings();
 		while ( classes.hasNext() ) {
 			PersistentClass model = (PersistentClass) classes.next();
 			if ( !model.isInherited() ) {
 				IdentifierGenerator generator = model.getIdentifier().createIdentifierGenerator(
 						cfg.getIdentifierGeneratorFactory(),
 						getDialect(),
 				        settings.getDefaultCatalogName(),
 				        settings.getDefaultSchemaName(),
 				        (RootClass) model
 				);
 				identifierGenerators.put( model.getEntityName(), generator );
 			}
 		}
 
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		final String cacheRegionPrefix = settings.getCacheRegionPrefix() == null ? "" : settings.getCacheRegionPrefix() + ".";
 
 		entityPersisters = new HashMap();
 		Map entityAccessStrategies = new HashMap();
 		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
 		classes = cfg.getClassMappings();
 		while ( classes.hasNext() ) {
 			final PersistentClass model = (PersistentClass) classes.next();
 			model.prepareTemporaryTables( mapping, getDialect() );
 			final String cacheRegionName = cacheRegionPrefix + model.getRootClass().getCacheRegionName();
 			// cache region is defined by the root-class in the hierarchy...
 			EntityRegionAccessStrategy accessStrategy = ( EntityRegionAccessStrategy ) entityAccessStrategies.get( cacheRegionName );
 			if ( accessStrategy == null && settings.isSecondLevelCacheEnabled() ) {
 				final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 				if ( accessType != null ) {
                     LOG.trace("Building cache for entity data [" + model.getEntityName() + "]");
 					EntityRegion entityRegion = settings.getRegionFactory().buildEntityRegion( cacheRegionName, properties, CacheDataDescriptionImpl.decode( model ) );
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					allCacheRegions.put( cacheRegionName, entityRegion );
 				}
 			}
 			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
 					model,
 					accessStrategy,
 					this,
 					mapping
 			);
 			entityPersisters.put( model.getEntityName(), cp );
 			classMeta.put( model.getEntityName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap();
 		Iterator collections = cfg.getCollectionMappings();
 		while ( collections.hasNext() ) {
 			Collection model = (Collection) collections.next();
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
                 LOG.trace("Building cache for collection data [" + model.getRole() + "]");
 				CollectionRegion collectionRegion = settings.getRegionFactory().buildCollectionRegion( cacheRegionName, properties, CacheDataDescriptionImpl
 						.decode( model ) );
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				allCacheRegions.put( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister = serviceRegistry.getService( PersisterFactory.class ).createCollectionPersister(
 					cfg,
 					model,
 					accessStrategy,
 					this
 			) ;
 			collectionPersisters.put( model.getRole(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		collectionMetadata = Collections.unmodifiableMap(collectionPersisters);
 		Iterator itr = tmpEntityToCollectionRoleMap.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			entry.setValue( Collections.unmodifiableSet( ( Set ) entry.getValue() ) );
 		}
 		collectionRolesByEntityParticipant = Collections.unmodifiableMap( tmpEntityToCollectionRoleMap );
 
 		//Named Queries:
 		namedQueries = new HashMap<String, NamedQueryDefinition>( cfg.getNamedQueries() );
 		namedSqlQueries = new HashMap<String, NamedSQLQueryDefinition>( cfg.getNamedSQLQueries() );
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>( cfg.getSqlResultSetMappings() );
 		imports = new HashMap<String,String>( cfg.getImports() );
 
 		// after *all* persisters and named queries are registered
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final EntityPersister persister = ( ( EntityPersister ) iter.next() );
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister );
 
 		}
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final CollectionPersister persister = ( ( CollectionPersister ) iter.next() );
 			persister.postInstantiate();
 		}
 
 		//JNDI + Serialization:
 
 		name = settings.getSessionFactoryName();
 		try {
 			uuid = (String) UUID_GENERATOR.generate(null, null);
 		}
 		catch (Exception e) {
 			throw new AssertionFailure("Could not generate UUID");
 		}
 		SessionFactoryRegistry.INSTANCE.addSessionFactory( uuid, name, this, serviceRegistry.getService( JndiService.class ) );
 
         LOG.debugf("Instantiated session factory");
 
 		if ( settings.isAutoCreateSchema() ) {
 			new SchemaExport( serviceRegistry, cfg ).create( false, true );
 		}
 		if ( settings.isAutoUpdateSchema() ) {
 			new SchemaUpdate( serviceRegistry, cfg ).execute( false, true );
 		}
 		if ( settings.isAutoValidateSchema() ) {
 			new SchemaValidator( serviceRegistry, cfg ).validate();
 		}
 		if ( settings.isAutoDropSchema() ) {
 			schemaExport = new SchemaExport( serviceRegistry, cfg );
 		}
 
 		currentSessionContext = buildCurrentSessionContext();
 
 		if ( settings.isQueryCacheEnabled() ) {
 			updateTimestampsCache = new UpdateTimestampsCache(settings, properties);
 			queryCache = settings.getQueryCacheFactory()
 			        .getQueryCache(null, updateTimestampsCache, settings, properties);
 			queryCaches = new HashMap<String,QueryCache>();
 			allCacheRegions.put( updateTimestampsCache.getRegion().getName(), updateTimestampsCache.getRegion() );
 			allCacheRegions.put( queryCache.getRegion().getName(), queryCache.getRegion() );
 		}
 		else {
 			updateTimestampsCache = null;
 			queryCache = null;
 			queryCaches = null;
 		}
 
 		//checking for named queries
 		if ( settings.isNamedQueryStartupCheckingEnabled() ) {
 			Map errors = checkNamedQueries();
 			if ( !errors.isEmpty() ) {
 				Set keys = errors.keySet();
 				StringBuffer failingQueries = new StringBuffer( "Errors in named queries: " );
 				for ( Iterator iterator = keys.iterator() ; iterator.hasNext() ; ) {
 					String queryName = ( String ) iterator.next();
 					HibernateException e = ( HibernateException ) errors.get( queryName );
 					failingQueries.append( queryName );
                     if (iterator.hasNext()) failingQueries.append(", ");
                     LOG.namedQueryError(queryName, e);
 				}
 				throw new HibernateException( failingQueries.toString() );
 			}
 		}
 
 		// this needs to happen after persisters are all ready to go...
 		this.fetchProfiles = new HashMap();
 		itr = cfg.iterateFetchProfiles();
 		while ( itr.hasNext() ) {
 			final org.hibernate.mapping.FetchProfile mappingProfile =
 					( org.hibernate.mapping.FetchProfile ) itr.next();
 			final FetchProfile fetchProfile = new FetchProfile( mappingProfile.getName() );
 			Iterator fetches = mappingProfile.getFetches().iterator();
 			while ( fetches.hasNext() ) {
 				final org.hibernate.mapping.FetchProfile.Fetch mappingFetch =
 						( org.hibernate.mapping.FetchProfile.Fetch ) fetches.next();
 				// resolve the persister owning the fetch
 				final String entityName = getImportedClassName( mappingFetch.getEntity() );
 				final EntityPersister owner = ( EntityPersister ) ( entityName == null ? null : entityPersisters.get( entityName ) );
 				if ( owner == null ) {
 					throw new HibernateException(
 							"Unable to resolve entity reference [" + mappingFetch.getEntity()
 									+ "] in fetch profile [" + fetchProfile.getName() + "]"
 					);
 				}
 
 				// validate the specified association fetch
 				Type associationType = owner.getPropertyType( mappingFetch.getAssociation() );
 				if ( associationType == null || !associationType.isAssociationType() ) {
 					throw new HibernateException( "Fetch profile [" + fetchProfile.getName() + "] specified an invalid association" );
 				}
 
 				// resolve the style
 				final Fetch.Style fetchStyle = Fetch.Style.parse( mappingFetch.getStyle() );
 
 				// then construct the fetch instance...
 				fetchProfile.addFetch( new Association( owner, mappingFetch.getAssociation() ), fetchStyle );
 				( ( Loadable ) owner ).registerAffectingFetchProfile( fetchProfile.getName() );
 			}
 			fetchProfiles.put( fetchProfile.getName(), fetchProfile );
 		}
 
 		this.transactionEnvironment = new TransactionEnvironmentImpl( this );
 		this.observer.sessionFactoryCreated( this );
 	}
 
 	public SessionFactoryImpl(
 			MetadataImplementor metadata,
 			SessionFactoryOptions sessionFactoryOptions,
 			SessionFactoryObserver observer) throws HibernateException {
         LOG.debug( "Building session factory" );
 
 		// TODO: remove initialization of final variables; just setting to null to make compiler happy
 		this.name = null;
 		this.uuid = null;
 		this.fetchProfiles = null;
 		this.queryCache = null;
 		this.updateTimestampsCache = null;
 		this.queryCaches = null;
 		this.currentSessionContext = null;
 		this.sqlFunctionRegistry = null;
 		this.transactionEnvironment = null;
 
 		this.sessionFactoryOptions = sessionFactoryOptions;
 
 		this.properties = createPropertiesFromMap(
 				metadata.getServiceRegistry().getService( ConfigurationService.class ).getSettings()
 		);
 
 		// TODO: these should be moved into SessionFactoryOptions
 		this.settings = new SettingsFactory().buildSettings(
 				properties,
 				metadata.getServiceRegistry()
 		);
 
 		this.serviceRegistry =
 				metadata.getServiceRegistry()
 						.getService( SessionFactoryServiceRegistryFactory.class )
 						.buildServiceRegistry( this, metadata );
 
 		this.jdbcServices = this.serviceRegistry.getService( JdbcServices.class );
 		this.dialect = this.jdbcServices.getDialect();
 
 		// TODO: get SQL functions from a new service
 		// this.sqlFunctionRegistry = new SQLFunctionRegistry( getDialect(), cfg.getSqlFunctions() );
 
 		if ( observer != null ) {
 			this.observer.addObserver( observer );
 		}
 
 		this.typeResolver = metadata.getTypeResolver().scope( this );
 		this.typeHelper = new TypeLocatorImpl( typeResolver );
 
 		this.filters = new HashMap<String, FilterDefinition>();
 		for ( FilterDefinition filterDefinition : metadata.getFilterDefinitions() ) {
 			filters.put( filterDefinition.getFilterName(), filterDefinition );
 		}
 
         LOG.debugf("Session factory constructed with filter configurations : %s", filters);
         LOG.debugf("Instantiating session factory with properties: %s", properties );
 
 		// TODO: get RegionFactory from service registry
 		settings.getRegionFactory().start( settings, properties );
 		this.queryPlanCache = new QueryPlanCache( this );
 
 		class IntegratorObserver implements SessionFactoryObserver {
 			private ArrayList<Integrator> integrators = new ArrayList<Integrator>();
 
 			@Override
 			public void sessionFactoryCreated(SessionFactory factory) {
 			}
 
 			@Override
 			public void sessionFactoryClosed(SessionFactory factory) {
 				for ( Integrator integrator : integrators ) {
 					integrator.disintegrate( SessionFactoryImpl.this, SessionFactoryImpl.this.serviceRegistry );
 				}
 			}
 		}
 
 		final IntegratorObserver integratorObserver = new IntegratorObserver();
 		this.observer.addObserver( integratorObserver );
 		for ( Integrator integrator : serviceRegistry.getService( IntegratorService.class ).getIntegrators() ) {
 			// TODO: add Integrator.integrate(MetadataImplementor, ...)
 			// integrator.integrate( cfg, this, this.serviceRegistry );
 			integratorObserver.integrators.add( integrator );
 		}
 
 
 		//Generators:
 
 		identifierGenerators = new HashMap();
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
 			if ( entityBinding.isRoot() ) {
 				// TODO: create the IdentifierGenerator while the metadata is being build, then simply
 				// use EntityBinding.getIdentifierGenerator() (also remove getIdentifierGeneratorFactory from Mappings)
 				// TODO: this is broken; throws NullPointerException
 				//IdentifierGenerator generator = entityBinding.getEntityIdentifier().createIdentifierGenerator(
 				//		metadata.getIdentifierGeneratorFactory()
 				//);
 				//identifierGenerators.put( entityBinding.getEntity().getName(), generator );
 			}
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// Prepare persisters and link them up with their cache
 		// region/access-strategy
 
 		StringBuilder stringBuilder = new StringBuilder();
 		if ( settings.getCacheRegionPrefix() != null) {
 			stringBuilder
 					.append( settings.getCacheRegionPrefix() )
 					.append( '.' );
 		}
 		final String cacheRegionPrefix = stringBuilder.toString();
 
 		entityPersisters = new HashMap();
 		Map<String, RegionAccessStrategy> entityAccessStrategies = new HashMap<String, RegionAccessStrategy>();
 		Map<String,ClassMetadata> classMeta = new HashMap<String,ClassMetadata>();
 		for ( EntityBinding model : metadata.getEntityBindings() ) {
 			// TODO: should temp table prep happen when metadata is being built?
 			//model.prepareTemporaryTables( metadata, getDialect() );
 			// cache region is defined by the root-class in the hierarchy...
 			EntityBinding rootEntityBinding = metadata.getRootEntityBinding( model.getEntity().getName() );
 			EntityRegionAccessStrategy accessStrategy = null;
 			if ( settings.isSecondLevelCacheEnabled() &&
 					rootEntityBinding.getCaching() != null &&
 					model.getCaching() != null &&
 					model.getCaching().getAccessType() != null ) {
 				final String cacheRegionName = cacheRegionPrefix + rootEntityBinding.getCaching().getRegion();
 				accessStrategy = EntityRegionAccessStrategy.class.cast( entityAccessStrategies.get( cacheRegionName ) );
 				if ( accessStrategy == null ) {
 					final AccessType accessType = model.getCaching().getAccessType();
 					LOG.trace("Building cache for entity data [" + model.getEntity().getName() + "]");
 					EntityRegion entityRegion =
 							settings.getRegionFactory().buildEntityRegion(
 									cacheRegionName,
 									properties,
 									CacheDataDescriptionImpl.decode( model )
 							);
 					accessStrategy = entityRegion.buildAccessStrategy( accessType );
 					entityAccessStrategies.put( cacheRegionName, accessStrategy );
 					allCacheRegions.put( cacheRegionName, entityRegion );
 				}
 			}
 			EntityPersister cp = serviceRegistry.getService( PersisterFactory.class ).createEntityPersister(
 					model, accessStrategy, this, metadata
 			);
 			entityPersisters.put( model.getEntity().getName(), cp );
 			classMeta.put( model.getEntity().getName(), cp.getClassMetadata() );
 		}
 		this.classMetadata = Collections.unmodifiableMap(classMeta);
 
 		Map<String,Set<String>> tmpEntityToCollectionRoleMap = new HashMap<String,Set<String>>();
 		collectionPersisters = new HashMap();
 		for ( PluralAttributeBinding model : metadata.getCollectionBindings() ) {
 			if ( model.getAttribute() == null ) {
 				throw new IllegalStateException( "No attribute defined for a PluralAttributeBinding: " +  model );
 			}
 			if ( model.getAttribute().isSingular() ) {
 				throw new IllegalStateException(
 						"PluralAttributeBinding has a Singular attribute defined: " + model.getAttribute().getName()
 				);
 			}
 			// TODO: Add PluralAttributeBinding.getCaching()
 			final String cacheRegionName = cacheRegionPrefix + model.getCacheRegionName();
 			final AccessType accessType = AccessType.fromExternalName( model.getCacheConcurrencyStrategy() );
 			CollectionRegionAccessStrategy accessStrategy = null;
 			if ( accessType != null && settings.isSecondLevelCacheEnabled() ) {
 				// TODO: is model.getAttribute().getName() the collection's role??? For now, assuming it is
                 LOG.trace("Building cache for collection data [" + model.getAttribute().getName() + "]");
 				CollectionRegion collectionRegion =
 						settings.getRegionFactory()
 								.buildCollectionRegion(
 										cacheRegionName, properties, CacheDataDescriptionImpl.decode( model )
 								);
 				accessStrategy = collectionRegion.buildAccessStrategy( accessType );
 				entityAccessStrategies.put( cacheRegionName, accessStrategy );
 				allCacheRegions.put( cacheRegionName, collectionRegion );
 			}
 			CollectionPersister persister =
 					serviceRegistry
 							.getService( PersisterFactory.class )
 							.createCollectionPersister( metadata, model, accessStrategy, this );
 			// TODO: is model.getAttribute().getName() the collection's role??? For now, assuming it is
 			collectionPersisters.put( model.getAttribute().getName(), persister.getCollectionMetadata() );
 			Type indexType = persister.getIndexType();
 			if ( indexType != null && indexType.isAssociationType() && !indexType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) indexType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 			Type elementType = persister.getElementType();
 			if ( elementType.isAssociationType() && !elementType.isAnyType() ) {
 				String entityName = ( ( AssociationType ) elementType ).getAssociatedEntityName( this );
 				Set roles = tmpEntityToCollectionRoleMap.get( entityName );
 				if ( roles == null ) {
 					roles = new HashSet();
 					tmpEntityToCollectionRoleMap.put( entityName, roles );
 				}
 				roles.add( persister.getRole() );
 			}
 		}
 		collectionMetadata = Collections.unmodifiableMap(collectionPersisters);
 		Iterator itr = tmpEntityToCollectionRoleMap.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			entry.setValue( Collections.unmodifiableSet( ( Set ) entry.getValue() ) );
 		}
 		collectionRolesByEntityParticipant = Collections.unmodifiableMap( tmpEntityToCollectionRoleMap );
 
 		//Named Queries:
 		namedQueries = new HashMap<String,NamedQueryDefinition>();
 		for ( NamedQueryDefinition namedQueryDefinition :  metadata.getNamedQueryDefinitions() ) {
 			namedQueries.put( namedQueryDefinition.getName(), namedQueryDefinition );
 		}
 		namedSqlQueries = new HashMap<String, NamedSQLQueryDefinition>();
 		for ( NamedSQLQueryDefinition namedNativeQueryDefinition: metadata.getNamedNativeQueryDefinitions() ) {
 			namedSqlQueries.put( namedNativeQueryDefinition.getName(), namedNativeQueryDefinition );
 		}
 		sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
 		for( ResultSetMappingDefinition resultSetMappingDefinition : metadata.getResultSetMappingDefinitions() ) {
 			sqlResultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 		}
 		imports = new HashMap<String,String>();
 		for ( Map.Entry<String,String> importEntry : metadata.getImports() ) {
 			imports.put( importEntry.getKey(), importEntry.getValue() );
 		}
 
 		// after *all* persisters and named queries are registered
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final EntityPersister persister = ( ( EntityPersister ) iter.next() );
 			persister.postInstantiate();
 			registerEntityNameResolvers( persister );
 
 		}
 		iter = collectionPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			final CollectionPersister persister = ( ( CollectionPersister ) iter.next() );
 			persister.postInstantiate();
 		}
 
 		// TODO: implement
 	}
 
 	@SuppressWarnings( {"unchecked"} )
 	private static Properties createPropertiesFromMap(Map map) {
 		Properties properties = new Properties();
 		properties.putAll( map );
 		return properties;
 	}
 
 	public Session openSession() throws HibernateException {
 		return withOptions().openSession();
 	}
 
 	public Session openTemporarySession() throws HibernateException {
 		return withOptions()
 				.autoClose( false )
 				.flushBeforeCompletion( false )
 				.connectionReleaseMode( ConnectionReleaseMode.AFTER_STATEMENT )
 				.openSession();
 	}
 
 	public Session getCurrentSession() throws HibernateException {
 		if ( currentSessionContext == null ) {
 			throw new HibernateException( "No CurrentSessionContext configured!" );
 		}
 		return currentSessionContext.currentSession();
 	}
 
 	@Override
 	public SessionBuilder withOptions() {
 		return new SessionBuilderImpl( this );
 	}
 
 	@Override
 	public StatelessSessionBuilder withStatelessOptions() {
 		return new StatelessSessionBuilderImpl( this );
 	}
 
 	public StatelessSession openStatelessSession() {
 		return withStatelessOptions().openStatelessSession();
 	}
 
 	public StatelessSession openStatelessSession(Connection connection) {
 		return withStatelessOptions().connection( connection ).openStatelessSession();
 	}
 
 	@Override
 	public void addObserver(SessionFactoryObserver observer) {
 		this.observer.addObserver( observer );
 	}
 
 	public TransactionEnvironment getTransactionEnvironment() {
 		return transactionEnvironment;
 	}
 
 	public Properties getProperties() {
 		return properties;
 	}
 
 	public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
 		return null;
 	}
 
 	public TypeResolver getTypeResolver() {
 		return typeResolver;
 	}
 
 	private void registerEntityNameResolvers(EntityPersister persister) {
 		if ( persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizer() == null ) {
 			return;
 		}
 		registerEntityNameResolvers( persister.getEntityMetamodel().getTuplizer() );
 	}
 
 	private void registerEntityNameResolvers(EntityTuplizer tuplizer) {
 		EntityNameResolver[] resolvers = tuplizer.getEntityNameResolvers();
 		if ( resolvers == null ) {
 			return;
 		}
 
 		for ( EntityNameResolver resolver : resolvers ) {
 			registerEntityNameResolver( resolver );
 		}
 	}
 
 	private static final Object ENTITY_NAME_RESOLVER_MAP_VALUE = new Object();
 
 	public void registerEntityNameResolver(EntityNameResolver resolver) {
 		entityNameResolvers.put( resolver, ENTITY_NAME_RESOLVER_MAP_VALUE );
 	}
 
 	public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
 		return entityNameResolvers.keySet();
 	}
 
 	public QueryPlanCache getQueryPlanCache() {
 		return queryPlanCache;
 	}
 
 	private Map checkNamedQueries() throws HibernateException {
 		Map errors = new HashMap();
 
 		// Check named HQL queries
 		if(LOG.isDebugEnabled())
         LOG.debugf("Checking %s named HQL queries", namedQueries.size());
 		Iterator itr = namedQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedQueryDefinition qd = ( NamedQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
                 LOG.debugf("Checking named query: %s", queryName);
 				//TODO: BUG! this currently fails for named queries for non-POJO entities
 				queryPlanCache.getHQLQueryPlan( qd.getQueryString(), false, CollectionHelper.EMPTY_MAP );
 			}
 			catch ( QueryException e ) {
 				errors.put( queryName, e );
 			}
 			catch ( MappingException e ) {
 				errors.put( queryName, e );
 			}
 		}
 		if(LOG.isDebugEnabled())
         LOG.debugf("Checking %s named SQL queries", namedSqlQueries.size());
 		itr = namedSqlQueries.entrySet().iterator();
 		while ( itr.hasNext() ) {
 			final Map.Entry entry = ( Map.Entry ) itr.next();
 			final String queryName = ( String ) entry.getKey();
 			final NamedSQLQueryDefinition qd = ( NamedSQLQueryDefinition ) entry.getValue();
 			// this will throw an error if there's something wrong.
 			try {
                 LOG.debugf("Checking named SQL query: %s", queryName);
 				// TODO : would be really nice to cache the spec on the query-def so as to not have to re-calc the hash;
 				// currently not doable though because of the resultset-ref stuff...
 				NativeSQLQuerySpecification spec;
 				if ( qd.getResultSetRef() != null ) {
 					ResultSetMappingDefinition definition = ( ResultSetMappingDefinition ) sqlResultSetMappings.get( qd.getResultSetRef() );
 					if ( definition == null ) {
 						throw new MappingException( "Unable to find resultset-ref definition: " + qd.getResultSetRef() );
 					}
 					spec = new NativeSQLQuerySpecification(
 							qd.getQueryString(),
 					        definition.getQueryReturns(),
 					        qd.getQuerySpaces()
 					);
 				}
 				else {
 					spec =  new NativeSQLQuerySpecification(
 							qd.getQueryString(),
 					        qd.getQueryReturns(),
 					        qd.getQuerySpaces()
 					);
 				}
 				queryPlanCache.getNativeSQLQueryPlan( spec );
 			}
 			catch ( QueryException e ) {
 				errors.put( queryName, e );
 			}
 			catch ( MappingException e ) {
 				errors.put( queryName, e );
 			}
 		}
 
 		return errors;
 	}
 
 	public EntityPersister getEntityPersister(String entityName) throws MappingException {
 		EntityPersister result = (EntityPersister) entityPersisters.get(entityName);
 		if (result==null) {
 			throw new MappingException( "Unknown entity: " + entityName );
 		}
 		return result;
 	}
 
 	public CollectionPersister getCollectionPersister(String role) throws MappingException {
 		CollectionPersister result = (CollectionPersister) collectionPersisters.get(role);
 		if (result==null) {
 			throw new MappingException( "Unknown collection role: " + role );
 		}
 		return result;
 	}
 
 	public Settings getSettings() {
 		return settings;
 	}
 
 	@Override
 	public SessionFactoryOptions getSessionFactoryOptions() {
 		return sessionFactoryOptions;
 	}
 
 	public JdbcServices getJdbcServices() {
 		return jdbcServices;
 	}
 
 	public Dialect getDialect() {
 		if ( serviceRegistry == null ) {
 			throw new IllegalStateException( "Cannot determine dialect because serviceRegistry is null." );
 		}
 		return dialect;
 	}
 
 	public Interceptor getInterceptor() {
 		return sessionFactoryOptions.getInterceptor();
 	}
 
 	public SQLExceptionConverter getSQLExceptionConverter() {
 		return getSQLExceptionHelper().getSqlExceptionConverter();
 	}
 
 	public SqlExceptionHelper getSQLExceptionHelper() {
 		return getJdbcServices().getSqlExceptionHelper();
 	}
 
 	public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
 		return collectionRolesByEntityParticipant.get( entityName );
 	}
 
 	@Override
 	public Reference getReference() throws NamingException {
 		// from javax.naming.Referenceable
         LOG.debug( "Returning a Reference to the SessionFactory" );
 		return new Reference(
 				SessionFactoryImpl.class.getName(),
 				new StringRefAddr("uuid", uuid),
 				SessionFactoryRegistry.ObjectFactoryImpl.class.getName(),
 				null
 		);
 	}
 
 	private Object readResolve() throws ObjectStreamException {
         LOG.trace("Resolving serialized SessionFactory");
 		// look for the instance by uuid
 		Object result = SessionFactoryRegistry.INSTANCE.getSessionFactory( uuid );
 		if ( result == null ) {
 			// in case we were deserialized in a different JVM, look for an instance with the same name
 			// (alternatively we could do an actual JNDI lookup here....)
 			result = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory( name );
             if ( result == null ) {
 				throw new InvalidObjectException( "Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]" );
 			}
             LOG.debugf("Resolved SessionFactory by name");
         }
 		else {
 			LOG.debugf("Resolved SessionFactory by UUID");
 		}
 		return result;
 	}
 
 	public NamedQueryDefinition getNamedQuery(String queryName) {
 		return (NamedQueryDefinition) namedQueries.get(queryName);
 	}
 
 	public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
 		return (NamedSQLQueryDefinition) namedSqlQueries.get(queryName);
 	}
 
 	public ResultSetMappingDefinition getResultSetMapping(String resultSetName) {
 		return (ResultSetMappingDefinition) sqlResultSetMappings.get(resultSetName);
 	}
 
 	public Type getIdentifierType(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierType();
 	}
 	public String getIdentifierPropertyName(String className) throws MappingException {
 		return getEntityPersister(className).getIdentifierPropertyName();
 	}
 
 	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
         LOG.trace( "Deserializing" );
 		in.defaultReadObject();
         LOG.debugf( "Deserialized: %s", uuid );
 	}
 
 	private void writeObject(ObjectOutputStream out) throws IOException {
         LOG.debugf("Serializing: %s", uuid);
 		out.defaultWriteObject();
         LOG.trace("Serialized");
 	}
 
 	public Type[] getReturnTypes(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, CollectionHelper.EMPTY_MAP ).getReturnMetadata().getReturnTypes();
 	}
 
 	public String[] getReturnAliases(String queryString) throws HibernateException {
 		return queryPlanCache.getHQLQueryPlan( queryString, false, CollectionHelper.EMPTY_MAP ).getReturnMetadata().getReturnAliases();
 	}
 
 	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
 		return getClassMetadata( persistentClass.getName() );
 	}
 
 	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
 		return (CollectionMetadata) collectionMetadata.get(roleName);
 	}
 
 	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
 		return classMetadata.get(entityName);
 	}
 
 	/**
 	 * Return the names of all persistent (mapped) classes that extend or implement the
 	 * given class or interface, accounting for implicit/explicit polymorphism settings
 	 * and excluding mapped subclasses/joined-subclasses of other classes in the result.
 	 */
 	public String[] getImplementors(String className) throws MappingException {
 
 		final Class clazz;
 		try {
 			clazz = ReflectHelper.classForName(className);
 		}
 		catch (ClassNotFoundException cnfe) {
 			return new String[] { className }; //for a dynamic-class
 		}
 
 		ArrayList results = new ArrayList();
 		Iterator iter = entityPersisters.values().iterator();
 		while ( iter.hasNext() ) {
 			//test this entity to see if we must query it
 			EntityPersister testPersister = (EntityPersister) iter.next();
 			if ( testPersister instanceof Queryable ) {
 				Queryable testQueryable = (Queryable) testPersister;
 				String testClassName = testQueryable.getEntityName();
 				boolean isMappedClass = className.equals(testClassName);
 				if ( testQueryable.isExplicitPolymorphism() ) {
 					if ( isMappedClass ) {
 						return new String[] {className}; //NOTE EARLY EXIT
 					}
 				}
 				else {
 					if (isMappedClass) {
 						results.add(testClassName);
 					}
 					else {
 						final Class mappedClass = testQueryable.getMappedClass();
 						if ( mappedClass!=null && clazz.isAssignableFrom( mappedClass ) ) {
 							final boolean assignableSuperclass;
 							if ( testQueryable.isInherited() ) {
 								Class mappedSuperclass = getEntityPersister( testQueryable.getMappedSuperclass() ).getMappedClass();
 								assignableSuperclass = clazz.isAssignableFrom(mappedSuperclass);
 							}
 							else {
 								assignableSuperclass = false;
 							}
 							if ( !assignableSuperclass ) {
 								results.add( testClassName );
 							}
 						}
 					}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSources.java b/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSources.java
index 21fdeb22ac..dc59dbbf46 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSources.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/MetadataSources.java
@@ -1,382 +1,382 @@
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
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Enumeration;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.jar.JarFile;
 import java.util.zip.ZipEntry;
 
 import org.jboss.logging.Logger;
 import org.w3c.dom.Document;
 import org.xml.sax.EntityResolver;
 
 import org.hibernate.cfg.EJB3DTDEntityResolver;
 import org.hibernate.cfg.EJB3NamingStrategy;
 import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.metamodel.source.MappingException;
-import org.hibernate.metamodel.source.MappingNotFoundException;
-import org.hibernate.metamodel.source.Origin;
-import org.hibernate.metamodel.source.SourceType;
-import org.hibernate.metamodel.source.internal.JaxbHelper;
-import org.hibernate.metamodel.source.internal.JaxbRoot;
-import org.hibernate.metamodel.source.internal.MetadataBuilderImpl;
+import org.hibernate.metamodel.binder.MappingException;
+import org.hibernate.metamodel.binder.MappingNotFoundException;
+import org.hibernate.metamodel.binder.Origin;
+import org.hibernate.metamodel.binder.SourceType;
+import org.hibernate.metamodel.binder.source.internal.JaxbHelper;
+import org.hibernate.metamodel.binder.source.internal.JaxbRoot;
+import org.hibernate.metamodel.binder.source.internal.MetadataBuilderImpl;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * @author Steve Ebersole
  */
 public class MetadataSources {
 	private static final Logger LOG = Logger.getLogger( MetadataSources.class );
 
 	private List<JaxbRoot> jaxbRootList = new ArrayList<JaxbRoot>();
 	private LinkedHashSet<Class<?>> annotatedClasses = new LinkedHashSet<Class<?>>();
 	private LinkedHashSet<String> annotatedPackages = new LinkedHashSet<String>();
 
 	private final JaxbHelper jaxbHelper;
 
 	private final BasicServiceRegistry serviceRegistry;
 	private final EntityResolver entityResolver;
 	private final NamingStrategy namingStrategy;
 
 	private final MetadataBuilderImpl metadataBuilder;
 
 	public MetadataSources(BasicServiceRegistry serviceRegistry) {
 		this( serviceRegistry, EJB3DTDEntityResolver.INSTANCE, EJB3NamingStrategy.INSTANCE );
 	}
 
 	public MetadataSources(BasicServiceRegistry serviceRegistry, EntityResolver entityResolver, NamingStrategy namingStrategy) {
 		this.serviceRegistry = serviceRegistry;
 		this.entityResolver = entityResolver;
 		this.namingStrategy = namingStrategy;
 
 		this.jaxbHelper = new JaxbHelper( this );
 		this.metadataBuilder = new MetadataBuilderImpl( this );
 	}
 
 	public List<JaxbRoot> getJaxbRootList() {
 		return jaxbRootList;
 	}
 
 	public Iterable<String> getAnnotatedPackages() {
 		return annotatedPackages;
 	}
 
 	public Iterable<Class<?>> getAnnotatedClasses() {
 		return annotatedClasses;
 	}
 
 	public BasicServiceRegistry getServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
 	public MetadataBuilder getMetadataBuilder() {
 		return metadataBuilder;
 	}
 
 	public Metadata buildMetadata() {
 		return getMetadataBuilder().buildMetadata();
 	}
 
 	/**
 	 * Read metadata from the annotations attached to the given class.
 	 *
 	 * @param annotatedClass The class containing annotations
 	 *
 	 * @return this (for method chaining)
 	 */
 	public MetadataSources addAnnotatedClass(Class annotatedClass) {
 		annotatedClasses.add( annotatedClass );
 		return this;
 	}
 
 	/**
 	 * Read package-level metadata.
 	 *
 	 * @param packageName java package name without trailing '.', cannot be {@code null}
 	 *
 	 * @return this (for method chaining)
 	 */
 	public MetadataSources addPackage(String packageName) {
 		if ( packageName == null ) {
 			throw new IllegalArgumentException( "The specified package name cannot be null" );
 		}
 		if ( packageName.endsWith( "." ) ) {
 			packageName = packageName.substring( 0, packageName.length() - 1 );
 		}
 		annotatedPackages.add( packageName );
 		return this;
 	}
 
 	/**
 	 * Read mappings as a application resourceName (i.e. classpath lookup).
 	 *
 	 * @param name The resource name
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addResource(String name) {
 		LOG.tracef( "reading mappings from resource : %s", name );
 
 		final Origin origin = new Origin( SourceType.RESOURCE, name );
 		InputStream resourceInputStream = classLoaderService().locateResourceStream( name );
 		if ( resourceInputStream == null ) {
 			throw new MappingNotFoundException( origin );
 		}
 		add( resourceInputStream, origin, true );
 
 		return this;
 	}
 
 	private ClassLoaderService classLoaderService() {
 		return serviceRegistry.getService( ClassLoaderService.class );
 	}
 
 	private JaxbRoot add(InputStream inputStream, Origin origin, boolean close) {
 		try {
 			JaxbRoot jaxbRoot = jaxbHelper.unmarshal( inputStream, origin );
 			jaxbRootList.add( jaxbRoot );
 			return jaxbRoot;
 		}
 		finally {
 			if ( close ) {
 				try {
 					inputStream.close();
 				}
 				catch ( IOException ignore ) {
 					LOG.trace( "Was unable to close input stream" );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Read a mapping as an application resource using the convention that a class named {@code foo.bar.Foo} is
 	 * mapped by a file named {@code foo/bar/Foo.hbm.xml} which can be resolved as a classpath resource.
 	 *
 	 * @param entityClass The mapped class. Cannot be {@code null} null.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addClass(Class entityClass) {
 		if ( entityClass == null ) {
 			throw new IllegalArgumentException( "The specified class cannot be null" );
 		}
 		LOG.debugf( "adding resource mappings from class convention : %s", entityClass.getName() );
 		final String mappingResourceName = entityClass.getName().replace( '.', '/' ) + ".hbm.xml";
 		addResource( mappingResourceName );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param path The path to a file.  Expected to be resolvable by {@link File#File(String)}
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @see #addFile(java.io.File)
 	 */
 	public MetadataSources addFile(String path) {
 		return addFile( new File( path ) );
 	}
 
 	/**
 	 * Read mappings from a particular XML file
 	 *
 	 * @param file The reference to the XML file
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addFile(File file) {
 		final String name = file.getAbsolutePath();
 		LOG.tracef( "reading mappings from file : %s", name );
 		final Origin origin = new Origin( SourceType.FILE, name );
 		try {
 			add( new FileInputStream( file ), origin, true );
 		}
 		catch ( FileNotFoundException e ) {
 			throw new MappingNotFoundException( e, origin );
 		}
 		return this;
 	}
 
 	/**
 	 * See {@link #addCacheableFile(java.io.File)} for description
 	 *
 	 * @param path The path to a file.  Expected to be resolvable by {@link File#File(String)}
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @see #addCacheableFile(java.io.File)
 	 */
 	public MetadataSources addCacheableFile(String path) {
 		return this; // todo : implement method body
 	}
 
 	/**
 	 * Add a cached mapping file.  A cached file is a serialized representation of the DOM structure of a
 	 * particular mapping.  It is saved from a previous call as a file with the name {@code {xmlFile}.bin}
 	 * where {@code {xmlFile}} is the name of the original mapping file.
 	 * </p>
 	 * If a cached {@code {xmlFile}.bin} exists and is newer than {@code {xmlFile}}, the {@code {xmlFile}.bin}
 	 * file will be read directly. Otherwise {@code {xmlFile}} is read and then serialized to {@code {xmlFile}.bin} for
 	 * use the next time.
 	 *
 	 * @param file The cacheable mapping file to be added, {@code {xmlFile}} in above discussion.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addCacheableFile(File file) {
 		return this; // todo : implement method body
 	}
 
 	/**
 	 * Read metadata from an {@link InputStream}.
 	 *
 	 * @param xmlInputStream The input stream containing a DOM.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addInputStream(InputStream xmlInputStream) {
 		add( xmlInputStream, new Origin( SourceType.INPUT_STREAM, "<unknown>" ), false );
 		return this;
 	}
 
 	/**
 	 * Read mappings from a {@link URL}
 	 *
 	 * @param url The url for the mapping document to be read.
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addURL(URL url) {
 		final String urlExternalForm = url.toExternalForm();
 		LOG.debugf( "Reading mapping document from URL : %s", urlExternalForm );
 
 		final Origin origin = new Origin( SourceType.URL, urlExternalForm );
 		try {
 			add( url.openStream(), origin, true );
 		}
 		catch ( IOException e ) {
 			throw new MappingNotFoundException( "Unable to open url stream [" + urlExternalForm + "]", e, origin );
 		}
 		return this;
 	}
 
 	/**
 	 * Read mappings from a DOM {@link Document}
 	 *
 	 * @param document The DOM document
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addDocument(Document document) {
 		final Origin origin = new Origin( SourceType.DOM, "<unknown>" );
 		JaxbRoot jaxbRoot = jaxbHelper.unmarshal( document, origin );
 		jaxbRootList.add( jaxbRoot );
 		return this;
 	}
 
 	/**
 	 * Read all mappings from a jar file.
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param jar a jar file
 	 *
 	 * @return this (for method chaining purposes)
 	 */
 	public MetadataSources addJar(File jar) {
 		LOG.debugf( "Seeking mapping documents in jar file : %s", jar.getName() );
 		final Origin origin = new Origin( SourceType.JAR, jar.getAbsolutePath() );
 		try {
 			JarFile jarFile = new JarFile( jar );
 			try {
 				Enumeration jarEntries = jarFile.entries();
 				while ( jarEntries.hasMoreElements() ) {
 					final ZipEntry zipEntry = (ZipEntry) jarEntries.nextElement();
 					if ( zipEntry.getName().endsWith( ".hbm.xml" ) ) {
 						LOG.tracef( "found mapping document : %s", zipEntry.getName() );
 						try {
 							add( jarFile.getInputStream( zipEntry ), origin, true );
 						}
 						catch ( Exception e ) {
 							throw new MappingException( "could not read mapping documents", e, origin );
 						}
 					}
 				}
 			}
 			finally {
 				try {
 					jarFile.close();
 				}
 				catch ( Exception ignore ) {
 				}
 			}
 		}
 		catch ( IOException e ) {
 			throw new MappingNotFoundException( e, origin );
 		}
 		return this;
 	}
 
 	/**
 	 * Read all mapping documents from a directory tree.
 	 * <p/>
 	 * Assumes that any file named <tt>*.hbm.xml</tt> is a mapping document.
 	 *
 	 * @param dir The directory
 	 *
 	 * @return this (for method chaining purposes)
 	 *
 	 * @throws org.hibernate.MappingException Indicates problems reading the jar file or
 	 * processing the contained mapping documents.
 	 */
 	public MetadataSources addDirectory(File dir) {
 		File[] files = dir.listFiles();
 		for ( File file : files ) {
 			if ( file.isDirectory() ) {
 				addDirectory( file );
 			}
 			else if ( file.getName().endsWith( ".hbm.xml" ) ) {
 				addFile( file );
 			}
 		}
 		return this;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/EntityBinder.java
index 9df240b1be..7260b52fcc 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/EntityBinder.java
@@ -1,128 +1,179 @@
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
 package org.hibernate.metamodel.binder;
 
 import java.util.Map;
 
 import org.hibernate.EntityMode;
-import org.hibernate.metamodel.binder.view.EntityView;
-import org.hibernate.metamodel.binder.view.TableView;
+import org.hibernate.metamodel.binder.source.BindingContext;
+import org.hibernate.metamodel.binder.source.DiscriminatorSubClassEntityDescriptor;
+import org.hibernate.metamodel.binder.source.EntityDescriptor;
+import org.hibernate.metamodel.binder.source.JoinedSubClassEntityDescriptor;
+import org.hibernate.metamodel.binder.source.RootEntityDescriptor;
+import org.hibernate.metamodel.binder.source.UnionSubClassEntityDescriptor;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.JavaType;
-import org.hibernate.metamodel.relational.Identifier;
-import org.hibernate.metamodel.relational.Schema;
-import org.hibernate.metamodel.relational.Table;
-import org.hibernate.metamodel.source.MappingException;
-import org.hibernate.metamodel.source.spi.BindingContext;
 
 /**
  * @author Steve Ebersole
  */
 public class EntityBinder {
 	private final BindingContext bindingContext;
 
 	public EntityBinder(BindingContext bindingContext) {
 		this.bindingContext = bindingContext;
 	}
 
-	public EntityBinding createEntityBinding(EntityView entityView) {
+	public EntityBinding createEntityBinding(EntityDescriptor entityDescriptor) {
+		final InheritanceType inheritanceType = entityDescriptor.getEntityInheritanceType();
+		if ( inheritanceType == InheritanceType.NO_INHERITANCE ) {
+			// root, also doubles as a type check since the cast would fail
+			return makeEntityBinding( (RootEntityDescriptor) entityDescriptor );
+		}
+		else {
+			if ( entityDescriptor.getSuperEntityName() == null ) {
+				throw new MappingException(
+						"Encountered inheritance strategy, but no super type found",
+						entityDescriptor.getOrigin()
+				);
+			}
+
+			if ( inheritanceType == InheritanceType.SINGLE_TABLE ) {
+				// discriminator subclassing
+				return makeEntityBinding( (DiscriminatorSubClassEntityDescriptor) entityDescriptor );
+			}
+			else if ( inheritanceType == InheritanceType.JOINED ) {
+				// joined subclassing
+				return makeEntityBinding( (JoinedSubClassEntityDescriptor) entityDescriptor );
+			}
+			else if ( inheritanceType == InheritanceType.TABLE_PER_CLASS ) {
+				return makeEntityBinding( (UnionSubClassEntityDescriptor) entityDescriptor );
+			}
+			else {
+				throw new IllegalStateException( "Unexpected inheritance type [" + inheritanceType + "]" );
+			}
+		}
+	}
+
+	protected EntityBinding makeEntityBinding(RootEntityDescriptor entityDescriptor) {
 		final EntityBinding entityBinding = new EntityBinding();
 
-		// todo : Entity will need both entityName and className to be effective
-		final Entity entity = new Entity( entityView.getEntityName(), entityView.getSuperType(), bindingContext.makeJavaType( entityView.getClassName() ) );
+		final Entity entity = new Entity( entityDescriptor.getEntityName(), null, bindingContext.makeJavaType( entityDescriptor.getClassName() ) );
 		entityBinding.setEntity( entity );
 
-		final TableView baseTableView = entityView.getBaseTable();
-
-		final String schemaName = baseTableView.getExplicitSchemaName() == null
-				? bindingContext.getMappingDefaults().getSchemaName()
-				: baseTableView.getExplicitSchemaName();
-		final String catalogName = baseTableView.getExplicitCatalogName() == null
-				? bindingContext.getMappingDefaults().getCatalogName()
-				: baseTableView.getExplicitCatalogName();
-		final Schema.Name fullSchemaName = new Schema.Name( schemaName, catalogName );
-		final Schema schema = bindingContext.getMetadataImplementor().getDatabase().getSchema( fullSchemaName );
-		final Identifier tableName = Identifier.toIdentifier( baseTableView.getTableName() );
-		final Table baseTable = schema.locateOrCreateTable( tableName );
-		entityBinding.setBaseTable( baseTable );
-
-		// inheritance
-		if ( entityView.getEntityInheritanceType() != InheritanceType.NO_INHERITANCE ) {
-			// if there is any inheritance strategy, there has to be a super type.
-			if ( entityView.getSuperType() == null ) {
-				throw new MappingException( "Encountered inheritance strategy, but no super type", entityView.getOrigin() );
-			}
-		}
-		entityBinding.setRoot( entityView.isRoot() );
-		entityBinding.setInheritanceType( entityView.getEntityInheritanceType() );
+		performBasicEntityBind( entityBinding, entityDescriptor );
+
+		entityBinding.setMutable( entityDescriptor.isMutable() );
+		entityBinding.setExplicitPolymorphism( entityDescriptor.isExplicitPolymorphism() );
+		entityBinding.setWhereFilter( entityDescriptor.getWhereFilter() );
+		entityBinding.setRowId( entityDescriptor.getRowId() );
+		entityBinding.setOptimisticLockStyle( entityDescriptor.getOptimisticLockStyle() );
+		entityBinding.setCaching( entityDescriptor.getCaching() );
+
+		return entityBinding;
+	}
+
+	protected EntityBinding makeEntityBinding(DiscriminatorSubClassEntityDescriptor entityDescriptor) {
+		// temporary!!!
+
+		final EntityBinding entityBinding = new EntityBinding();
+
+		final Entity entity = new Entity( entityDescriptor.getEntityName(), null, bindingContext.makeJavaType( entityDescriptor.getClassName() ) );
+		entityBinding.setEntity( entity );
+
+		performBasicEntityBind( entityBinding, entityDescriptor );
+
+		return entityBinding;
+	}
 
-		entityBinding.setJpaEntityName( entityView.getJpaEntityName() );
-		entityBinding.setEntityMode( entityView.getEntityMode() );
+	protected EntityBinding makeEntityBinding(JoinedSubClassEntityDescriptor entityDescriptor) {
+		// temporary!!!
 
-		if ( entityView.getEntityMode() == EntityMode.POJO ) {
-			if ( entityView.getProxyInterfaceName() != null ) {
-				entityBinding.setProxyInterfaceType( bindingContext.makeJavaType( entityView.getProxyInterfaceName() ) );
+		final EntityBinding entityBinding = new EntityBinding();
+
+		final Entity entity = new Entity( entityDescriptor.getEntityName(), null, bindingContext.makeJavaType( entityDescriptor.getClassName() ) );
+		entityBinding.setEntity( entity );
+
+		performBasicEntityBind( entityBinding, entityDescriptor );
+
+		return entityBinding;
+	}
+
+	protected EntityBinding makeEntityBinding(UnionSubClassEntityDescriptor entityDescriptor) {
+		// temporary!!!
+
+		final EntityBinding entityBinding = new EntityBinding();
+
+		final Entity entity = new Entity( entityDescriptor.getEntityName(), null, bindingContext.makeJavaType( entityDescriptor.getClassName() ) );
+		entityBinding.setEntity( entity );
+
+		performBasicEntityBind( entityBinding, entityDescriptor );
+
+		return entityBinding;
+	}
+
+	protected void performBasicEntityBind(EntityBinding entityBinding, EntityDescriptor entityDescriptor) {
+		entityBinding.setInheritanceType( entityDescriptor.getEntityInheritanceType() );
+
+		entityBinding.setJpaEntityName( entityDescriptor.getJpaEntityName() );
+		entityBinding.setEntityMode( entityDescriptor.getEntityMode() );
+
+		if ( entityDescriptor.getEntityMode() == EntityMode.POJO ) {
+			if ( entityDescriptor.getProxyInterfaceName() != null ) {
+				entityBinding.setProxyInterfaceType( bindingContext.makeJavaType( entityDescriptor.getProxyInterfaceName() ) );
 				entityBinding.setLazy( true );
 			}
-			else if ( entityView.isLazy() ) {
-				entityBinding.setProxyInterfaceType( entity.getJavaType() );
+			else if ( entityDescriptor.isLazy() ) {
+				entityBinding.setProxyInterfaceType( entityBinding.getEntity().getJavaType() );
 				entityBinding.setLazy( true );
 			}
 		}
 		else {
 			entityBinding.setProxyInterfaceType( new JavaType( Map.class ) );
-			entityBinding.setLazy( entityView.isLazy() );
+			entityBinding.setLazy( entityDescriptor.isLazy() );
 		}
 
-		entityBinding.setCustomEntityTuplizerClass( entityView.getCustomEntityTuplizerClass() );
-		entityBinding.setCustomEntityPersisterClass( entityView.getCustomEntityPersisterClass() );
-
-		entityBinding.setCaching( entityView.getCaching() );
-		entityBinding.setMetaAttributeContext( entityView.getMetaAttributeContext() );
-
-		entityBinding.setMutable( entityView.isMutable() );
-		entityBinding.setExplicitPolymorphism( entityView.isExplicitPolymorphism() );
-		entityBinding.setWhereFilter( entityView.getWhereFilter() );
-		entityBinding.setRowId( entityView.getRowId() );
-		entityBinding.setDynamicUpdate( entityView.isDynamicUpdate() );
-		entityBinding.setDynamicInsert( entityView.isDynamicInsert() );
-		entityBinding.setBatchSize( entityView.getBatchSize() );
-		entityBinding.setSelectBeforeUpdate( entityView.isSelectBeforeUpdate() );
-		entityBinding.setOptimisticLockMode( entityView.getOptimisticLockMode() );
-		entityBinding.setAbstract( entityView.isAbstract() );
-
-		entityBinding.setCustomLoaderName( entityView.getCustomLoaderName() );
-		entityBinding.setCustomInsert( entityView.getCustomInsert() );
-		entityBinding.setCustomUpdate( entityView.getCustomUpdate() );
-		entityBinding.setCustomDelete( entityView.getCustomDelete() );
-
-		if ( entityView.getSynchronizedTableNames() != null ) {
-			entityBinding.addSynchronizedTableNames( entityView.getSynchronizedTableNames() );
-		}
+		entityBinding.setCustomEntityTuplizerClass( entityDescriptor.getCustomEntityTuplizerClass() );
+		entityBinding.setCustomEntityPersisterClass( entityDescriptor.getCustomEntityPersisterClass() );
 
-		return entityBinding;
+		entityBinding.setMetaAttributeContext( entityDescriptor.getMetaAttributeContext() );
+
+		entityBinding.setDynamicUpdate( entityDescriptor.isDynamicUpdate() );
+		entityBinding.setDynamicInsert( entityDescriptor.isDynamicInsert() );
+		entityBinding.setBatchSize( entityDescriptor.getBatchSize() );
+		entityBinding.setSelectBeforeUpdate( entityDescriptor.isSelectBeforeUpdate() );
+		entityBinding.setAbstract( entityDescriptor.isAbstract() );
+
+		entityBinding.setCustomLoaderName( entityDescriptor.getCustomLoaderName() );
+		entityBinding.setCustomInsert( entityDescriptor.getCustomInsert() );
+		entityBinding.setCustomUpdate( entityDescriptor.getCustomUpdate() );
+		entityBinding.setCustomDelete( entityDescriptor.getCustomDelete() );
+
+		if ( entityDescriptor.getSynchronizedTableNames() != null ) {
+			entityBinding.addSynchronizedTableNames( entityDescriptor.getSynchronizedTableNames() );
+		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/MappingException.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/MappingException.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/MappingException.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/MappingException.java
index 279879e41f..35a6032e02 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/MappingException.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/MappingException.java
@@ -1,50 +1,50 @@
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
 
-package org.hibernate.metamodel.source;
+package org.hibernate.metamodel.binder;
 
 import org.hibernate.HibernateException;
 
 /**
  * Indicates a problem parsing a mapping document.
  *
  * @author Steve Ebersole
  */
 public class MappingException extends HibernateException {
 	private final Origin origin;
 
 	public MappingException(String message, Origin origin) {
 		super( message );
 		this.origin = origin;
 	}
 
 	public MappingException(String message, Throwable root, Origin origin) {
 		super( message, root );
 		this.origin = origin;
 	}
 
 	public Origin getOrigin() {
 		return origin;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/MappingNotFoundException.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/MappingNotFoundException.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/MappingNotFoundException.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/MappingNotFoundException.java
index 3e56633c35..f1b73f1649 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/MappingNotFoundException.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/MappingNotFoundException.java
@@ -1,46 +1,46 @@
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
 
-package org.hibernate.metamodel.source;
+package org.hibernate.metamodel.binder;
 
 /**
  * @author Steve Ebersole
  */
 public class MappingNotFoundException extends MappingException {
 	public MappingNotFoundException(String message, Origin origin) {
 		super( message, origin );
 	}
 
 	public MappingNotFoundException(Origin origin) {
 		super( String.format( "Mapping (%s) not found : %s", origin.getType(), origin.getName() ), origin );
 	}
 
 	public MappingNotFoundException(String message, Throwable root, Origin origin) {
 		super( message, root, origin );
 	}
 
 	public MappingNotFoundException(Throwable root, Origin origin) {
 		super( String.format( "Mapping (%s) not found : %s", origin.getType(), origin.getName() ), root, origin );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/Origin.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/Origin.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/Origin.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/Origin.java
index 824d433bd8..0ef00b2979 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/Origin.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/Origin.java
@@ -1,60 +1,60 @@
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
-package org.hibernate.metamodel.source;
+package org.hibernate.metamodel.binder;
 
 import java.io.Serializable;
 
 /**
  * Describes the origin of an xml document
  *
  * @author Steve Ebersole
  */
 public class Origin implements Serializable {
 	private final SourceType type;
 	private final String name;
 
 	public Origin(SourceType type, String name) {
 		this.type = type;
 		this.name = name;
 	}
 
 	/**
 	 * Retrieve the type of origin.
 	 *
 	 * @return The origin type.
 	 */
 	public SourceType getType() {
 		return type;
 	}
 
 	/**
 	 * The name of the document origin.  Interpretation is relative to the type, but might be the
 	 * resource name or file URL.
 	 *
 	 * @return The name.
 	 */
 	public String getName() {
 		return name;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/SourceType.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/SourceType.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/SourceType.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/SourceType.java
index 07eb6046ce..cd8484a6d9 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/SourceType.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/SourceType.java
@@ -1,41 +1,41 @@
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
 
-package org.hibernate.metamodel.source;
+package org.hibernate.metamodel.binder;
 
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
 	JAR,
 	OTHER
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/XsdException.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/XsdException.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/XsdException.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/XsdException.java
index fac0104bf9..7a2a7cb004 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/XsdException.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/XsdException.java
@@ -1,50 +1,50 @@
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
 
-package org.hibernate.metamodel.source;
+package org.hibernate.metamodel.binder;
 
 import org.hibernate.HibernateException;
 
 /**
  * Indicates an issue finding or loading an XSD schema.
  * 
  * @author Steve Ebersole
  */
 public class XsdException extends HibernateException {
 	private final String xsdName;
 
 	public XsdException(String message, String xsdName) {
 		super( message );
 		this.xsdName = xsdName;
 	}
 
 	public XsdException(String message, Throwable root, String xsdName) {
 		super( message, root );
 		this.xsdName = xsdName;
 	}
 
 	public String getXsdName() {
 		return xsdName;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/BindingContext.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/BindingContext.java
index 66502c678d..0eaae02bb3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/BindingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/BindingContext.java
@@ -1,51 +1,49 @@
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
+package org.hibernate.metamodel.binder.source;
 
 import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.metamodel.binder.view.EntityView;
 import org.hibernate.metamodel.domain.JavaType;
-import org.hibernate.persister.entity.EntityPersister;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/DiscriminatorSubClassEntityDescriptor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/DiscriminatorSubClassEntityDescriptor.java
new file mode 100644
index 0000000000..806eb16631
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/DiscriminatorSubClassEntityDescriptor.java
@@ -0,0 +1,30 @@
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
+package org.hibernate.metamodel.binder.source;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface DiscriminatorSubClassEntityDescriptor extends EntityDescriptor {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/EntityView.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/EntityDescriptor.java
similarity index 86%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/EntityView.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/EntityDescriptor.java
index e74b589c31..b1b6399b77 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/EntityView.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/EntityDescriptor.java
@@ -1,151 +1,132 @@
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
-package org.hibernate.metamodel.binder.view;
+package org.hibernate.metamodel.binder.source;
 
 import java.util.Set;
 
 import org.hibernate.EntityMode;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * Represents the normalized set of mapping information about a specific entity.
  *
  * @author Gail Badner
  * @author Steve Ebersole
  */
-public interface EntityView extends NormalizedViewObject {
+public interface EntityDescriptor extends UnifiedDescriptorObject {
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
 
 	/**
-	 * Is this entity the root of a mapped inheritance hierarchy?
-	 *
-	 * @return {@code true} if this entity is an inheritance root; {@code false} otherwise.
-	 */
-	public boolean isRoot();
-
-	/**
 	 * Obtains the type of inheritance defined for this entity hierarchy
 	 *
 	 * @return The inheritance strategy for this entity.
 	 */
 	public InheritanceType getEntityInheritanceType();
 
 	/**
 	 * Obtain the super type for this entity.
 	 *
 	 * @return This entity's super type.
 	 */
-	public Hierarchical getSuperType();
+	public String getSuperEntityName();
 
 	/**
 	 * Obtain the custom {@link EntityPersister} class defined in this mapping.  {@code null} indicates the default
 	 * should be used.
 	 *
 	 * @return The custom {@link EntityPersister} class to use; or {@code null}
 	 */
 	public Class<? extends EntityPersister> getCustomEntityPersisterClass();
 
 	/**
 	 * Obtain the custom {@link EntityTuplizer} class defined in this mapping.  {@code null} indicates the default
 	 * should be used.
 	 *
 	 * @return The custom {@link EntityTuplizer} class to use; or {@code null}
 	 */
 	public Class<? extends EntityTuplizer> getCustomEntityTuplizerClass();
 
-	Caching getCaching();
 
 	boolean isLazy();
 
-	boolean isMutable();
-
-	boolean isExplicitPolymorphism();
-
-	String getWhereFilter();
-
-	String getRowId();
-
 	boolean isDynamicUpdate();
 
 	boolean isDynamicInsert();
 
 	int getBatchSize();
 
 	boolean isSelectBeforeUpdate();
 
-	int getOptimisticLockMode();
-
 	Boolean isAbstract();
 
 	String getCustomLoaderName();
 
 	CustomSQL getCustomInsert();
 
 	CustomSQL getCustomUpdate();
 
 	CustomSQL getCustomDelete();
 
 	Set<String> getSynchronizedTableNames();
 
 
-	public TableView getBaseTable();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/JoinedSubClassEntityDescriptor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/JoinedSubClassEntityDescriptor.java
new file mode 100644
index 0000000000..1aef70103c
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/JoinedSubClassEntityDescriptor.java
@@ -0,0 +1,30 @@
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
+package org.hibernate.metamodel.binder.source;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface JoinedSubClassEntityDescriptor extends EntityDescriptor {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MappingDefaults.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/MappingDefaults.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MappingDefaults.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/MappingDefaults.java
index 8dd1ebd98f..9ff478aabc 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MappingDefaults.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/MappingDefaults.java
@@ -1,92 +1,92 @@
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
+package org.hibernate.metamodel.binder.source;
 
 /**
  * Defines a (contextual) set of values to use as defaults in the absence of related mapping information.  The
  * context here is conceptually a stack.  The "global" level is configuration settings.
  *
  * @author Gail Badner
  * @author Steve Ebersole
  */
 public interface MappingDefaults {
 	/**
 	 * Identifies the default package name to use if none specified in the mapping.  Really only pertinent for
 	 * {@code hbm.xml} mappings.
 	 *
 	 * @return The default package name.
 	 */
 	public String getPackageName();
 
 	/**
 	 * Identifies the default database schema name to use if none specified in the mapping.
 	 *
 	 * @return The default schema name
 	 */
 	public String getSchemaName();
 
 	/**
 	 * Identifies the default database catalog name to use if none specified in the mapping.
 	 *
 	 * @return The default catalog name
 	 */
 	public String getCatalogName();
 
 	/**
 	 * Identifies the default column name to use for the identifier column if none specified in the mapping.
 	 *
 	 * @return The default identifier column name
 	 */
 	public String getIdColumnName();
 
 	/**
 	 * Identifies the default column name to use for the discriminator column if none specified in the mapping.
 	 *
 	 * @return The default discriminator column name
 	 */
 	public String getDiscriminatorColumnName();
 
 	/**
 	 * Identifies the default cascade style to apply to associations if none specified in the mapping.
 	 *
 	 * @return The default cascade style
 	 */
 	public String getCascadeStyle();
 
 	/**
 	 * Identifies the default {@link org.hibernate.property.PropertyAccessor} name to use if none specified in the
 	 * mapping.
 	 *
 	 * @return The default property accessor name
 	 * @see org.hibernate.property.PropertyAccessorFactory
 	 */
 	public String getPropertyAccessorName();
 
 	/**
 	 * Identifies whether associations are lazy by default if not specified in the mapping.
 	 *
 	 * @return The default association laziness
 	 */
 	public boolean areAssociationsLazy();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetaAttributeContext.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/MetaAttributeContext.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetaAttributeContext.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/MetaAttributeContext.java
index c7d744fa5a..231f730314 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetaAttributeContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/MetaAttributeContext.java
@@ -1,88 +1,88 @@
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
+package org.hibernate.metamodel.binder.source;
 
 import java.util.HashSet;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
 import org.hibernate.metamodel.binding.MetaAttribute;
 
 /**
  * @author Steve Ebersole
  */
 public class MetaAttributeContext {
 	private final MetaAttributeContext parentContext;
 	private final ConcurrentHashMap<String, MetaAttribute> metaAttributeMap = new ConcurrentHashMap<String, MetaAttribute>();
 
 	public MetaAttributeContext() {
 		this( null );
 	}
 
 	public MetaAttributeContext(MetaAttributeContext parentContext) {
 		this.parentContext = parentContext;
 	}
 
 
 	// read contract ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public Iterable<String> getKeys() {
 		HashSet<String> keys = new HashSet<String>();
 		addKeys( keys );
 		return keys;
 	}
 
 	private void addKeys(Set<String> keys) {
 		keys.addAll( metaAttributeMap.keySet() );
 		if ( parentContext != null ) {
 			// recursive call
 			parentContext.addKeys( keys );
 		}
 	}
 
 	public Iterable<String> getLocalKeys() {
 		return metaAttributeMap.keySet();
 	}
 
 	public MetaAttribute getMetaAttribute(String key) {
 		MetaAttribute value = getLocalMetaAttribute( key );
 		if ( value == null ) {
 			// recursive call
 			value = parentContext.getMetaAttribute( key );
 		}
 		return value;
 	}
 
 	public MetaAttribute getLocalMetaAttribute(String key) {
 		return metaAttributeMap.get( key );
 	}
 
 
 	// write contract ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public void add(MetaAttribute metaAttribute) {
 		metaAttributeMap.put( metaAttribute.getName(), metaAttribute );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/MetadataImplementor.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/MetadataImplementor.java
index d8a80dd0cd..23624cd22d 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetadataImplementor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/MetadataImplementor.java
@@ -1,78 +1,78 @@
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
+package org.hibernate.metamodel.binder.source;
 
 import org.hibernate.engine.ResultSetMappingDefinition;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.metamodel.Metadata;
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
 
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject);
 
 	// todo : this needs to move to AnnotationBindingContext
 	public void setGloballyQuotedIdentifiers(boolean b);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/RootEntityDescriptor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/RootEntityDescriptor.java
new file mode 100644
index 0000000000..519712de25
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/RootEntityDescriptor.java
@@ -0,0 +1,51 @@
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
+package org.hibernate.metamodel.binder.source;
+
+import org.hibernate.annotations.OptimisticLockType;
+import org.hibernate.engine.OptimisticLockStyle;
+import org.hibernate.metamodel.binding.Caching;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface RootEntityDescriptor extends EntityDescriptor {
+	public boolean isMutable();
+
+	public boolean isExplicitPolymorphism();
+
+	public String getWhereFilter();
+
+	public String getRowId();
+
+	public Caching getCaching();
+
+	public OptimisticLockStyle getOptimisticLockStyle();
+
+	public TableDescriptor getBaseTable();
+
+	// todo : add ->
+	//		1) identifier descriptor
+	//		2) discriminator descriptor
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/SourceProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/SourceProcessor.java
similarity index 98%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/SourceProcessor.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/SourceProcessor.java
index 7d2ea0c6d3..b42f0cf61a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/SourceProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/SourceProcessor.java
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
-package org.hibernate.metamodel.source.spi;
+package org.hibernate.metamodel.binder.source;
 
 import java.util.List;
 
 import org.hibernate.metamodel.MetadataSources;
 
 /**
  * Handles the processing of metadata sources in a dependency-ordered manner.
  *
  * @author Steve Ebersole
  */
 public interface SourceProcessor {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/TableView.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/TableDescriptor.java
similarity index 91%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/TableView.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/TableDescriptor.java
index 50fd75e732..3383f55770 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/TableView.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/TableDescriptor.java
@@ -1,33 +1,33 @@
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
-package org.hibernate.metamodel.binder.view;
+package org.hibernate.metamodel.binder.source;
 
 /**
  * @author Steve Ebersole
  */
-public interface TableView extends NormalizedViewObject {
+public interface TableDescriptor extends UnifiedDescriptorObject {
 	public String getExplicitSchemaName();
 	public String getExplicitCatalogName();
 	public String getTableName();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/NormalizedViewObject.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/UnifiedDescriptorObject.java
similarity index 82%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/NormalizedViewObject.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/UnifiedDescriptorObject.java
index 1c4ff816da..76233a88df 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/NormalizedViewObject.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/UnifiedDescriptorObject.java
@@ -1,36 +1,35 @@
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
-package org.hibernate.metamodel.binder.view;
+package org.hibernate.metamodel.binder.source;
 
-import org.hibernate.metamodel.source.Origin;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
+import org.hibernate.metamodel.binder.Origin;
 
 /**
  * @author Steve Ebersole
  */
-public interface NormalizedViewObject {
-	public NormalizedViewObject getContainingViewObject();
+public interface UnifiedDescriptorObject {
 	public Origin getOrigin();
+	public UnifiedDescriptorObject getContainingDescriptor();
 	public MetaAttributeContext getMetaAttributeContext();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/UnionSubClassEntityDescriptor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/UnionSubClassEntityDescriptor.java
new file mode 100644
index 0000000000..f8883f4b35
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/UnionSubClassEntityDescriptor.java
@@ -0,0 +1,30 @@
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
+package org.hibernate.metamodel.binder.source;
+
+/**
+ * @author Steve Ebersole
+ */
+public interface UnionSubClassEntityDescriptor extends EntityDescriptor {
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/hbm/EntityViewImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/AbstractEntityDescriptorImpl.java
similarity index 64%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/hbm/EntityViewImpl.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/AbstractEntityDescriptorImpl.java
index 0a8ebb9b93..97999c65c7 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/hbm/EntityViewImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/AbstractEntityDescriptorImpl.java
@@ -1,424 +1,316 @@
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
-package org.hibernate.metamodel.binder.view.hbm;
+package org.hibernate.metamodel.binder.source.hbm;
 
 import java.util.HashSet;
 import java.util.Set;
 
 import org.hibernate.EntityMode;
-import org.hibernate.MappingException;
-import org.hibernate.cache.spi.access.AccessType;
-import org.hibernate.engine.internal.Versioning;
-import org.hibernate.metamodel.binder.view.EntityView;
-import org.hibernate.metamodel.binder.view.NormalizedViewObject;
-import org.hibernate.metamodel.binder.view.TableView;
-import org.hibernate.metamodel.binding.Caching;
+import org.hibernate.metamodel.binder.Origin;
+import org.hibernate.metamodel.binder.source.EntityDescriptor;
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
+import org.hibernate.metamodel.binder.source.UnifiedDescriptorObject;
+import org.hibernate.metamodel.binder.source.hbm.xml.mapping.EntityElement;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
-import org.hibernate.metamodel.domain.Hierarchical;
-import org.hibernate.metamodel.source.Origin;
-import org.hibernate.metamodel.source.hbm.HbmBindingContext;
-import org.hibernate.metamodel.source.hbm.HbmHelper;
-import org.hibernate.metamodel.source.hbm.util.MappingHelper;
-import org.hibernate.metamodel.source.hbm.xml.mapping.EntityElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLCacheElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlDeleteElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlInsertElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlUpdateElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSynchronizeElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLTuplizerElement;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
- * TODO : thinking it might be better to have one of these for distinct mapping type (root, subclass, joined, etc)
+ * Convenience base class for handling commonality between the different type of {@link EntityDescriptor}
+ * implementations.
  *
  * @author Gail Badner
  * @author Steve Ebersole
  */
-public class EntityViewImpl implements EntityView {
+public abstract class AbstractEntityDescriptorImpl implements EntityDescriptor {
 	private final HbmBindingContext bindingContext;
 
 	private final String entityName;
 	private final EntityMode entityMode;
 
 	private final String className;
 	private final String proxyInterfaceName;
 
 	private final Class<EntityPersister> entityPersisterClass;
 	private final Class<EntityTuplizer> tuplizerClass;
 
 	private final MetaAttributeContext metaAttributeContext;
 
-	private final Hierarchical superType;
-	private final boolean isRoot;
+	private final String superEntityName;
 	private final InheritanceType entityInheritanceType;
 
-	private final Caching caching;
-
 	private final boolean lazy;
-	private final boolean mutable;
-	private final boolean explicitPolymorphism;
-	private final String whereFilter;
-	private final String rowId;
 
 	private final boolean dynamicUpdate;
 	private final boolean dynamicInsert;
 
 	private final int batchSize;
 	private final boolean selectBeforeUpdate;
-	private final int optimisticLockMode;
 
 	private final Boolean isAbstract;
 
 	private final String customLoaderName;
 	private final CustomSQL customInsert;
 	private final CustomSQL customUpdate;
 	private final CustomSQL customDelete;
 
 	private final Set<String> synchronizedTableNames;
 
-	private final TableView baseTableView;
 
-	public EntityViewImpl(
-			Hierarchical superType,
+	public AbstractEntityDescriptorImpl(
 			EntityElement entityClazz,
-			boolean isRoot,
+			String superEntityName,
 			InheritanceType inheritanceType,
 			HbmBindingContext bindingContext) {
 
 		this.bindingContext = bindingContext;
 
-		this.superType = superType;
+		this.superEntityName = superEntityName;
 		this.entityName = bindingContext.determineEntityName( entityClazz );
 
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
 		this.tuplizerClass = customTuplizerClassName != null
 				? bindingContext.<EntityTuplizer>locateClassByName( customTuplizerClassName )
 				: null;
 		this.entityPersisterClass = entityClazz.getPersister() == null
 				? null
 				: bindingContext.<EntityPersister>locateClassByName( entityClazz.getPersister() );
 
-		this.isRoot = isRoot;
 		this.entityInheritanceType = inheritanceType;
 
-		this.caching = isRoot ? createCaching( entityClazz, this.entityName ) : null;
 
 		this.metaAttributeContext = HbmHelper.extractMetaAttributeContext(
 				entityClazz.getMeta(), true, bindingContext.getMetaAttributeContext()
 		);
 
 		// go ahead and set the lazy here, since pojo.proxy can override it.
 		this.lazy = MappingHelper.getBooleanValue(
 				entityClazz.isLazy(), bindingContext.getMappingDefaults().areAssociationsLazy()
 		);
-		this.mutable = entityClazz.isMutable();
 
-		this.explicitPolymorphism = "explicit".equals( entityClazz.getPolymorphism() );
-		this.whereFilter = entityClazz.getWhere();
-		this.rowId = entityClazz.getRowid();
 		this.dynamicUpdate = entityClazz.isDynamicUpdate();
 		this.dynamicInsert = entityClazz.isDynamicInsert();
 		this.batchSize = MappingHelper.getIntValue( entityClazz.getBatchSize(), 0 );
 		this.selectBeforeUpdate = entityClazz.isSelectBeforeUpdate();
-		this.optimisticLockMode = getOptimisticLockMode();
 
 		this.customLoaderName = entityClazz.getLoader().getQueryRef();
 
 		XMLSqlInsertElement sqlInsert = entityClazz.getSqlInsert();
 		if ( sqlInsert != null ) {
 			this.customInsert = HbmHelper.getCustomSql(
 					sqlInsert.getValue(),
 					sqlInsert.isCallable(),
 					sqlInsert.getCheck().value()
 			);
 		}
 		else {
 			this.customInsert = null;
 		}
 
 		XMLSqlDeleteElement sqlDelete = entityClazz.getSqlDelete();
 		if ( sqlDelete != null ) {
 			this.customDelete = HbmHelper.getCustomSql(
 					sqlDelete.getValue(),
 					sqlDelete.isCallable(),
 					sqlDelete.getCheck().value()
 			);
 		}
 		else {
 			this.customDelete = null;
 		}
 
 		XMLSqlUpdateElement sqlUpdate = entityClazz.getSqlUpdate();
 		if ( sqlUpdate != null ) {
 			this.customUpdate = HbmHelper.getCustomSql(
 					sqlUpdate.getValue(),
 					sqlUpdate.isCallable(),
 					sqlUpdate.getCheck().value()
 			);
 		}
 		else {
 			this.customUpdate = null;
 		}
 
 		if ( entityClazz.getSynchronize() != null ) {
 			this.synchronizedTableNames = new HashSet<String>( entityClazz.getSynchronize().size() );
 			for ( XMLSynchronizeElement synchronize : entityClazz.getSynchronize() ) {
 				this.synchronizedTableNames.add( synchronize.getTable() );
 			}
 		}
 		else {
 			this.synchronizedTableNames = null;
 		}
 
 		this.isAbstract = entityClazz.isAbstract();
+	}
 
-		this.baseTableView = new TableViewImpl(
-				entityClazz.getSchema(),
-				entityClazz.getCatalog(),
-				entityClazz.getTable(),
-				this,
-				bindingContext
-		);
+	protected boolean isRoot() {
+		return entityInheritanceType == InheritanceType.NO_INHERITANCE;
 	}
 
 	private String extractCustomTuplizerClassName(EntityElement entityMapping, EntityMode entityMode) {
 		if ( entityMapping.getTuplizer() == null ) {
 			return null;
 		}
 		for ( XMLTuplizerElement tuplizerElement : entityMapping.getTuplizer() ) {
 			if ( entityMode == EntityMode.parse( tuplizerElement.getEntityMode() ) ) {
 				return tuplizerElement.getClazz();
 			}
 		}
 		return null;
 	}
 
-	private static Caching createCaching(EntityElement entityMapping, String entityName) {
-		if ( ! XMLHibernateMapping.XMLClass.class.isInstance( entityMapping ) ) {
-			// only the root entity can define caching
-			return null;
-		}
-		final XMLHibernateMapping.XMLClass rootEntityMapping = (XMLHibernateMapping.XMLClass) entityMapping;
-		XMLCacheElement cache = rootEntityMapping.getCache();
-		if ( cache == null ) {
-			return null;
-		}
-		final String region = cache.getRegion() != null ? cache.getRegion() : entityName;
-		final AccessType accessType = Enum.valueOf( AccessType.class, cache.getUsage() );
-		final boolean cacheLazyProps = !"non-lazy".equals( cache.getInclude() );
-		return new Caching( region, accessType, cacheLazyProps );
-	}
-
-	private static int createOptimisticLockMode(XMLHibernateMapping.XMLClass entityClazz) {
-		String optimisticLockModeString = MappingHelper.getStringValue( entityClazz.getOptimisticLock(), "version" );
-		int optimisticLockMode;
-		if ( "version".equals( optimisticLockModeString ) ) {
-			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_VERSION;
-		}
-		else if ( "dirty".equals( optimisticLockModeString ) ) {
-			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_DIRTY;
-		}
-		else if ( "all".equals( optimisticLockModeString ) ) {
-			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_ALL;
-		}
-		else if ( "none".equals( optimisticLockModeString ) ) {
-			optimisticLockMode = Versioning.OPTIMISTIC_LOCK_NONE;
-		}
-		else {
-			throw new MappingException( "Unsupported optimistic-lock style: " + optimisticLockModeString );
-		}
-		return optimisticLockMode;
-	}
-
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
-	public Hierarchical getSuperType() {
-		return superType;
-	}
-
-	@Override
-	public boolean isRoot() {
-		return isRoot;
+	public String getSuperEntityName() {
+		return superEntityName;
 	}
 
 	@Override
 	public InheritanceType getEntityInheritanceType() {
 		return entityInheritanceType;
 	}
 
 	@Override
-	public Caching getCaching() {
-		return caching;
-	}
-
-	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	@Override
-	public boolean isMutable() {
-		return mutable;
-	}
-
-	@Override
-	public boolean isExplicitPolymorphism() {
-		return explicitPolymorphism;
-	}
-
-	@Override
-	public String getWhereFilter() {
-		return whereFilter;
-	}
-
-	@Override
-	public String getRowId() {
-		return rowId;
-	}
-
-	@Override
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
-	public int getOptimisticLockMode() {
-		return optimisticLockMode;
-	}
-
-	@Override
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
 
 	@Override
 	public String getCustomLoaderName() {
 		return customLoaderName;
 	}
 
 	@Override
-	public TableView getBaseTable() {
-		return baseTableView;
-	}
-
-	@Override
-	public NormalizedViewObject getContainingViewObject() {
+	public UnifiedDescriptorObject getContainingDescriptor() {
 		return null;
 	}
 
 	@Override
 	public Origin getOrigin() {
 		return bindingContext.getOrigin();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/DiscriminatedSubClassEntityDescriptorImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/DiscriminatedSubClassEntityDescriptorImpl.java
new file mode 100644
index 0000000000..4efbfe0d3d
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/DiscriminatedSubClassEntityDescriptorImpl.java
@@ -0,0 +1,79 @@
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
+package org.hibernate.metamodel.binder.source.hbm;
+
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.metamodel.binder.MappingException;
+import org.hibernate.metamodel.binder.source.DiscriminatorSubClassEntityDescriptor;
+import org.hibernate.metamodel.binder.source.hbm.xml.mapping.EntityElement;
+import org.hibernate.metamodel.binding.InheritanceType;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSubclassElement;
+
+/**
+ * Unified descriptor for discriminator-based inheritance strategies.
+ * 
+ * @author Steve Ebersole
+ */
+public class DiscriminatedSubClassEntityDescriptorImpl
+		extends AbstractEntityDescriptorImpl
+		implements DiscriminatorSubClassEntityDescriptor {
+	/**
+	 * This form used when an explicit {@code extends} attribute names this mapping's super entity.
+	 *
+	 * @param entityClazz The JAXB entity mapping
+	 * @param bindingContext The context for the binding process.
+	 */
+	public DiscriminatedSubClassEntityDescriptorImpl(
+			EntityElement entityClazz,
+			HbmBindingContext bindingContext) {
+		this( entityClazz, extractExtendsName( entityClazz, bindingContext ), bindingContext );
+	}
+
+	private static String extractExtendsName(EntityElement entityClazz, HbmBindingContext bindingContext) {
+		final String extendsName = ( (XMLSubclassElement) entityClazz ).getExtends();
+		if ( StringHelper.isEmpty( extendsName ) ) {
+			throw new MappingException(
+					"Subclass entity mapping [" + bindingContext.determineEntityName( entityClazz )
+							+ "] was not contained in super entity mapping",
+					bindingContext.getOrigin()
+			);
+		}
+		return extendsName;
+	}
+
+	/**
+	 * This form would be used when the subclass definition if nested within its super type mapping.
+	 *
+	 * @param entityClazz The JAXB entity mapping
+	 * @param superEntityName The name of the containing (and thus super) entity
+	 * @param bindingContext The context for the binding process.
+	 */
+	public DiscriminatedSubClassEntityDescriptorImpl(
+			EntityElement entityClazz,
+			String superEntityName,
+			HbmBindingContext bindingContext) {
+		super( entityClazz, superEntityName, InheritanceType.SINGLE_TABLE, bindingContext );
+	}
+
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmBindingContext.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HbmBindingContext.java
similarity index 80%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmBindingContext.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HbmBindingContext.java
index 9c3ff888b1..201c524071 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmBindingContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HbmBindingContext.java
@@ -1,48 +1,48 @@
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
-package org.hibernate.metamodel.source.hbm;
+package org.hibernate.metamodel.binder.source.hbm;
 
 import java.util.List;
 
-import org.hibernate.metamodel.source.Origin;
-import org.hibernate.metamodel.source.hbm.xml.mapping.EntityElement;
+import org.hibernate.metamodel.binder.Origin;
+import org.hibernate.metamodel.binder.source.BindingContext;
+import org.hibernate.metamodel.binder.source.hbm.xml.mapping.EntityElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLFetchProfileElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
-import org.hibernate.metamodel.source.spi.BindingContext;
 
 /**
+ * Defines features specific to the {@code hbm.xml} variety of a {@link BindingContext}
+ * 
  * @author Steve Ebersole
  */
 public interface HbmBindingContext extends BindingContext {
 	public boolean isAutoImport();
 
 	public Origin getOrigin();
 
-	public String extractEntityName(XMLHibernateMapping.XMLClass entityClazz);
 	public String determineEntityName(EntityElement entityElement);
 
 	public String getClassName(String unqualifiedName);
 
 	public void processFetchProfiles(List<XMLFetchProfileElement> fetchProfiles, String containingEntityName);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmHelper.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HbmHelper.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmHelper.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HbmHelper.java
index 119d86125f..525273fdcb 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HbmHelper.java
@@ -1,171 +1,171 @@
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
-package org.hibernate.metamodel.source.hbm;
+package org.hibernate.metamodel.binder.source.hbm;
 
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.dom4j.Attribute;
 import org.dom4j.Element;
 
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
+import org.hibernate.metamodel.binder.source.hbm.xml.mapping.EntityElement;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.MetaAttribute;
-import org.hibernate.metamodel.source.hbm.xml.mapping.EntityElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLMetaElement;
-import org.hibernate.metamodel.source.hbm.util.MappingHelper;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
 /**
- * TODO : javadoc
- *
  * @author Steve Ebersole
  */
 public class HbmHelper {
+
+	// todo : merge this and MappingHelper together
+
 	public static boolean isCallable(Element e) {
 		return isCallable( e, true );
 	}
 
 	public static boolean isCallable(Element element, boolean supportsCallable) {
 		Attribute attrib = element.attribute( "callable" );
 		if ( attrib != null && "true".equals( attrib.getValue() ) ) {
 			if ( !supportsCallable ) {
 				throw new MappingException( "callable attribute not supported yet!" );
 			}
 			return true;
 		}
 		return false;
 	}
 
 	public static ExecuteUpdateResultCheckStyle getResultCheckStyle(String check, boolean callable) {
 		if ( check == null ) {
 			// use COUNT as the default.  This mimics the old behavior, although
 			// NONE might be a better option moving forward in the case of callable
 			return ExecuteUpdateResultCheckStyle.COUNT;
 		}
 		return ExecuteUpdateResultCheckStyle.fromExternalName( check );
 	}
 
 	public static final Map<String, MetaAttribute> extractMetas(List<XMLMetaElement> meta, Map<String, MetaAttribute> baseline) {
 		return extractMetas( meta, false, baseline );
 	}
 
 	public static final Map<String, MetaAttribute> extractMetas(List<XMLMetaElement> metaList, boolean onlyInheritable, Map<String, MetaAttribute> baseline) {
 		Map<String, MetaAttribute> extractedMetas = new HashMap<String, MetaAttribute>();
 		extractedMetas.putAll( baseline );
 		for ( XMLMetaElement meta : metaList ) {
 			boolean inheritable = meta.isInherit();
 			if ( onlyInheritable & !inheritable ) {
 				continue;
 			}
 
 			final String name = meta.getAttribute();
 			final MetaAttribute inheritedMetaAttribute = baseline.get( name );
 			MetaAttribute metaAttribute = extractedMetas.get( name );
 			if ( metaAttribute == null || metaAttribute == inheritedMetaAttribute ) {
 				metaAttribute = new MetaAttribute( name );
 				extractedMetas.put( name, metaAttribute );
 			}
 			metaAttribute.addValue( meta.getValue() );
 		}
 		return extractedMetas;
 	}
 
 	public static String extractEntityName(XMLClass entityClazz, String unqualifiedPackageName) {
 		return extractEntityName( entityClazz.getEntityName(), entityClazz.getName(), unqualifiedPackageName );
 	}
 
 	public static String extractEntityName(String entityName, String entityClassName, String unqualifiedPackageName) {
 		return entityName == null ? getClassName( entityClassName, unqualifiedPackageName ) : entityName;
 	}
 
 	public static String determineEntityName(EntityElement entityElement, String packageName) {
 		return extractEntityName( entityElement.getEntityName(), entityElement.getName(), packageName );
 	}
 
 	public static String determineClassName(EntityElement entityElement, String packageName) {
 		return getClassName( entityElement.getName(), packageName );
 	}
 
 	public static String getClassName(Attribute att, String unqualifiedPackageName) {
 		if ( att == null ) {
 			return null;
 		}
 		return getClassName( att.getValue(), unqualifiedPackageName );
 	}
 
 	public static String getClassName(String unqualifiedName, String unqualifiedPackageName) {
 		if ( unqualifiedName == null ) {
 			return null;
 		}
 		if ( unqualifiedName.indexOf( '.' ) < 0 && unqualifiedPackageName != null ) {
 			return unqualifiedPackageName + '.' + unqualifiedName;
 		}
 		return unqualifiedName;
 	}
 
 	public static CustomSQL getCustomSql(String sql, boolean isCallable, String check) {
 		return new CustomSQL( sql.trim(), isCallable, getResultCheckStyle( check, isCallable ) );
 	}
 
 	public static String getPropertyAccessorName(String access, boolean isEmbedded, String defaultAccess) {
 		return MappingHelper.getStringValue(
 				access,
 				isEmbedded ? "embedded" : defaultAccess
 		);
 	}
 
 	public static MetaAttributeContext extractMetaAttributeContext(
 			List<XMLMetaElement> metaElementList,
 			MetaAttributeContext parentContext) {
 		return extractMetaAttributeContext( metaElementList, false, parentContext );
 	}
 
 	public static MetaAttributeContext extractMetaAttributeContext(
 			List<XMLMetaElement> metaElementList,
 			boolean onlyInheritable,
 			MetaAttributeContext parentContext) {
 		final MetaAttributeContext subContext = new MetaAttributeContext( parentContext );
 
 		for ( XMLMetaElement metaElement : metaElementList ) {
 			if ( onlyInheritable & !metaElement.isInherit() ) {
 				continue;
 			}
 
 			final String name = metaElement.getAttribute();
 			final MetaAttribute inheritedMetaAttribute = parentContext.getMetaAttribute( name );
 			MetaAttribute metaAttribute = subContext.getLocalMetaAttribute( name );
 			if ( metaAttribute == null || metaAttribute == inheritedMetaAttribute ) {
 				metaAttribute = new MetaAttribute( name );
 				subContext.add( metaAttribute );
 			}
 			metaAttribute.addValue( metaElement.getValue() );
 		}
 
 		return subContext;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmSourceProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HbmSourceProcessorImpl.java
similarity index 75%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmSourceProcessor.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HbmSourceProcessorImpl.java
index 8ae68b1c35..003fc0176f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmSourceProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HbmSourceProcessorImpl.java
@@ -1,181 +1,178 @@
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
-package org.hibernate.metamodel.source.hbm;
+package org.hibernate.metamodel.binder.source.hbm;
 
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.metamodel.MetadataSources;
-import org.hibernate.metamodel.source.hbm.xml.mapping.EntityElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.SubclassEntityElement;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
+import org.hibernate.metamodel.binder.source.SourceProcessor;
+import org.hibernate.metamodel.binder.source.hbm.xml.mapping.EntityElement;
+import org.hibernate.metamodel.binder.source.hbm.xml.mapping.SubclassEntityElement;
+import org.hibernate.metamodel.binder.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
-import org.hibernate.metamodel.source.internal.JaxbRoot;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
-import org.hibernate.metamodel.source.spi.SourceProcessor;
 
 /**
- * Responsible for performing binding of hbm xml.
+ * The {@link SourceProcessor} implementation responsible for processing {@code hbm.xml} sources.
+ *
+ * @author Steve Ebersole
  */
-public class HbmSourceProcessor implements SourceProcessor {
+public class HbmSourceProcessorImpl implements SourceProcessor {
 	private final MetadataImplementor metadata;
 	private List<HibernateMappingProcessor> processors;
 
-	public HbmSourceProcessor(MetadataImplementor metadata) {
+	public HbmSourceProcessorImpl(MetadataImplementor metadata) {
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
-		// Lets get the entities into a better order for processing based on inheritance hierarchy to avoid the need
-		// for an "extends queue".  Really, for correctly, we localize the "extends queue" to just this method stack.
-		//
-		// The variable entityMappingByEntityNameMap holds the "resolved" mappings, keyed by entity name.  It uses a
-		// linked map because the order is important here as we will use it to track which entities depend on which
-		// other entities.
-		//
-		// The extendsQueue variable is a temporary queue where we place mappings which have an extends but for which
-		// we could not find the referenced entity being extended.
-
-
+		// Lets get the entities (the mapping processors really) into a better order for processing based on
+		// inheritance hierarchy to avoid the need for an "extends queue".  Really, correctly speaking, we are
+		// localizing the "extends queue" to just this method stack.
 
+		// 'orderedProcessors' represents the processors we know are not waiting on any super-types in the entity
+		// hierarchy to be found.  'extendsQueue', conversely, holds processors (and other information) relating
+		// we know have to wait on at least one of the super-types in their entity hierarchy to be found.
+		final LinkedHashSet<HibernateMappingProcessor> orderedProcessors = new LinkedHashSet<HibernateMappingProcessor>();
+		final Set<ExtendsQueueEntry> extendsQueue = new HashSet<ExtendsQueueEntry>();
 
+		// 'availableEntityNames' holds all of the available entity names.  This means the incoming set of
+		// 'processedEntityNames' as well as any entity-names found here as they are added to 'orderedProcessors'
 		final Set<String> availableEntityNames = new HashSet<String>();
 		availableEntityNames.addAll( processedEntityNames );
 
-
-
-
-
-
-
-
-		final LinkedHashSet<HibernateMappingProcessor> orderedProcessors = new LinkedHashSet<HibernateMappingProcessor>();
-		final Set<ExtendsQueueEntry> extendsQueue = new HashSet<ExtendsQueueEntry>();
-
+		// this loop is essentially splitting processors into those that can be processed immediately and those that
+		// have to wait on entities not yet seen.  Those that have to wait go in the extendsQueue.  Those that can be
+		// processed immediately go into 'orderedProcessors'.
 		for ( HibernateMappingProcessor processor : processors ) {
 			final HibernateMappingInformation hibernateMappingInformation = new HibernateMappingInformation( processor );
 			ExtendsQueueEntry extendsQueueEntry = null;
 			for ( Object entityElementO : processor.getHibernateMapping().getClazzOrSubclassOrJoinedSubclass() ) {
 				final EntityElement entityElement = (EntityElement) entityElementO;
 				final String entityName = processor.determineEntityName( entityElement );
 				hibernateMappingInformation.includedEntityNames.add( entityName );
 				if ( SubclassEntityElement.class.isInstance( entityElement ) ) {
 					final String entityItExtends = ( (SubclassEntityElement) entityElement ).getExtends();
 					if ( ! availableEntityNames.contains( entityItExtends ) ) {
 						if ( extendsQueueEntry == null ) {
 							extendsQueueEntry = new ExtendsQueueEntry( hibernateMappingInformation );
 							extendsQueue.add( extendsQueueEntry );
 						}
 						extendsQueueEntry.waitingOnEntityNames.add( entityItExtends );
 					}
 				}
 			}
 			if ( extendsQueueEntry == null ) {
 				// we found no extends names that we have to wait on
 				orderedProcessors.add( processor );
 				availableEntityNames.addAll( hibernateMappingInformation.includedEntityNames );
 			}
 		}
 
+		// This loop tries to move entries from 'extendsQueue' into 'orderedProcessors', stopping when we cannot
+		// process any more or they have all been processed.
 		while ( ! extendsQueue.isEmpty() ) {
 			// set up a pass over the queue
 			int numberOfMappingsProcessed = 0;
 			Iterator<ExtendsQueueEntry> iterator = extendsQueue.iterator();
 			while ( iterator.hasNext() ) {
 				final ExtendsQueueEntry entry = iterator.next();
 				if ( availableEntityNames.containsAll( entry.waitingOnEntityNames ) ) {
 					// all the entity names this entry was waiting on have been made available
 					iterator.remove();
 					orderedProcessors.add( entry.hibernateMappingInformation.processor );
 					availableEntityNames.addAll( entry.hibernateMappingInformation.includedEntityNames );
 					numberOfMappingsProcessed++;
 				}
 			}
 
 			if ( numberOfMappingsProcessed == 0 ) {
 				// todo : we could log the waiting dependencies...
 				throw new MappingException( "Unable to process extends dependencies in hbm files" );
 			}
 		}
 
+		// This loop executes the processors.
 		for ( HibernateMappingProcessor processor : orderedProcessors ) {
 			processor.processMappingMetadata( processedEntityNames );
 		}
 	}
 
 	private static class HibernateMappingInformation {
 		private final HibernateMappingProcessor processor;
 		private final Set<String> includedEntityNames = new HashSet<String>();
 
 		private HibernateMappingInformation(HibernateMappingProcessor processor) {
 			this.processor = processor;
 		}
 	}
 
 	private static class ExtendsQueueEntry {
 		private HibernateMappingInformation hibernateMappingInformation;
 		private final Set<String> waitingOnEntityNames = new HashSet<String>();
 
 		private ExtendsQueueEntry(HibernateMappingInformation hibernateMappingInformation) {
 			this.hibernateMappingInformation = hibernateMappingInformation;
 		}
 	}
 
 	@Override
 	public void processMappingDependentMetadata(MetadataSources sources) {
 		for ( HibernateMappingProcessor processor : processors ) {
 			processor.processMappingDependentMetadata();
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HibernateMappingProcessor.java
similarity index 84%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HibernateMappingProcessor.java
index 1b243fa281..94fb6e9f2e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HibernateMappingProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/HibernateMappingProcessor.java
@@ -1,404 +1,412 @@
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
-package org.hibernate.metamodel.source.hbm;
+package org.hibernate.metamodel.binder.source.hbm;
 
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.Value;
+import org.hibernate.metamodel.binder.EntityBinder;
+import org.hibernate.metamodel.binder.MappingException;
+import org.hibernate.metamodel.binder.Origin;
+import org.hibernate.metamodel.binder.source.EntityDescriptor;
+import org.hibernate.metamodel.binder.source.MappingDefaults;
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
+import org.hibernate.metamodel.binder.source.hbm.xml.mapping.EntityElement;
+import org.hibernate.metamodel.binder.source.internal.JaxbRoot;
+import org.hibernate.metamodel.binder.source.internal.OverriddenMappingDefaults;
+import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.BasicAuxiliaryDatabaseObjectImpl;
-import org.hibernate.metamodel.source.MappingException;
-import org.hibernate.metamodel.source.Origin;
-import org.hibernate.metamodel.source.hbm.xml.mapping.EntityElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLFetchProfileElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLJoinedSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLParamElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlQueryElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSubclassElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLUnionSubclassElement;
-import org.hibernate.metamodel.source.internal.JaxbRoot;
-import org.hibernate.metamodel.source.internal.OverriddenMappingDefaults;
-import org.hibernate.metamodel.source.spi.MappingDefaults;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 import org.hibernate.type.Type;
 
 /**
  * Responsible for processing a {@code <hibernate-mapping/>} element.  Allows processing to be coordinated across
  * all hbm files in an ordered fashion.  The order is essentially the same as defined in
- * {@link org.hibernate.metamodel.source.spi.SourceProcessor}
+ * {@link org.hibernate.metamodel.binder.source.SourceProcessor}
  *
  * @author Steve Ebersole
  */
 public class HibernateMappingProcessor implements HbmBindingContext {
 	private final MetadataImplementor metadata;
 	private final JaxbRoot<XMLHibernateMapping> jaxbRoot;
 
 	private final XMLHibernateMapping hibernateMapping;
 
 	private final MappingDefaults mappingDefaults;
 	private final MetaAttributeContext metaAttributeContext;
 
+
+	private final EntityBinder entityBinder;
+
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
 				null,	// idColumnName
 				null,	// discriminatorColumnName
 				hibernateMapping.getDefaultCascade(),
 				hibernateMapping.getDefaultAccess(),
 				hibernateMapping.isDefaultLazy()
 		);
 
-		autoImport = hibernateMapping.isAutoImport();
+		this.autoImport = hibernateMapping.isAutoImport();
 
-		metaAttributeContext = extractMetaAttributes();
+		this.entityBinder = new EntityBinder( metadata );
+
+		this.metaAttributeContext = extractMetaAttributes();
 	}
 
 	private MetaAttributeContext extractMetaAttributes() {
 		return hibernateMapping.getMeta() == null
 				? new MetaAttributeContext( metadata.getMetaAttributeContext() )
 				: HbmHelper.extractMetaAttributeContext( hibernateMapping.getMeta(), true, metadata.getMetaAttributeContext() );
 	}
 
 	XMLHibernateMapping getHibernateMapping() {
 		return hibernateMapping;
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
 
 	public void processIndependentMetadata() {
 		processDatabaseObjectDefinitions();
 		processTypeDefinitions();
 	}
 
 	private void processDatabaseObjectDefinitions() {
 		if ( hibernateMapping.getDatabaseObject() == null ) {
 			return;
 		}
 		for ( XMLHibernateMapping.XMLDatabaseObject databaseObjectElement : hibernateMapping.getDatabaseObject() ) {
 			final AuxiliaryDatabaseObject auxiliaryDatabaseObject;
 			if ( databaseObjectElement.getDefinition() != null ) {
 				final String className = databaseObjectElement.getDefinition().getClazz();
 				try {
 					auxiliaryDatabaseObject = (AuxiliaryDatabaseObject) classLoaderService.getValue().classForName( className ).newInstance();
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
 
 	private void processTypeDefinitions() {
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
 
 	public void processTypeDependentMetadata() {
 		processFilterDefinitions();
 		processIdentifierGenerators();
 	}
 
 	private void processFilterDefinitions() {
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
 
 	private void processIdentifierGenerators() {
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
 
-
 	public void processMappingMetadata(List<String> processedEntityNames) {
 		if ( hibernateMapping.getClazzOrSubclassOrJoinedSubclass() == null ) {
 			return;
 		}
 
 		for ( Object entityElementO : hibernateMapping.getClazzOrSubclassOrJoinedSubclass() ) {
 			final EntityElement entityElement = (EntityElement) entityElementO;
 
+			// determine the type of root element we have and build appropriate entity descriptor.  Might be:
+			//		1) <class/>
+			//		2) <subclass/>
+			//		3) <joined-subclass/>
+			//		4) <union-subclass/>
 
-		}
-
-		for ( Object clazzOrSubclass : hibernateMapping.getClazzOrSubclassOrJoinedSubclass() ) {
-			if ( XMLHibernateMapping.XMLClass.class.isInstance( clazzOrSubclass ) ) {
-				XMLHibernateMapping.XMLClass clazz =
-						XMLHibernateMapping.XMLClass.class.cast( clazzOrSubclass );
-				new RootEntityProcessor( this, clazz ).process( clazz );
+			final EntityDescriptor entityDescriptor;
+			if ( XMLHibernateMapping.XMLClass.class.isInstance( entityElement ) ) {
+				entityDescriptor = new RootEntityDescriptorImpl( entityElement, this );
 			}
-			else if ( XMLSubclassElement.class.isInstance( clazzOrSubclass ) ) {
-//					PersistentClass superModel = getSuperclass( mappings, element );
-//					handleSubclass( superModel, mappings, element, inheritedMetas );
+			else if ( XMLSubclassElement.class.isInstance( entityElement ) ) {
+				entityDescriptor = new DiscriminatedSubClassEntityDescriptorImpl( entityElement, this );
 			}
-			else if ( XMLJoinedSubclassElement.class.isInstance( clazzOrSubclass ) ) {
-//					PersistentClass superModel = getSuperclass( mappings, element );
-//					handleJoinedSubclass( superModel, mappings, element, inheritedMetas );
+			else if ( XMLJoinedSubclassElement.class.isInstance( entityElement ) ) {
+				entityDescriptor = new JoinedSubClassEntityDescriptorImpl( entityElement, this );
 			}
-			else if ( XMLUnionSubclassElement.class.isInstance( clazzOrSubclass ) ) {
-//					PersistentClass superModel = getSuperclass( mappings, element );
-//					handleUnionSubclass( superModel, mappings, element, inheritedMetas );
+			else if ( XMLUnionSubclassElement.class.isInstance( entityElement ) ) {
+				entityDescriptor = new UnionSubClassEntityDescriptorImpl( entityElement, this );
 			}
 			else {
-				throw new org.hibernate.metamodel.source.MappingException(
-						"unknown type of class or subclass: " +
-								clazzOrSubclass.getClass().getName(), jaxbRoot.getOrigin()
+				throw new MappingException(
+						"unknown type of class or subclass: " + entityElement.getClass().getName(),
+						jaxbRoot.getOrigin()
 				);
 			}
+
+			if ( processedEntityNames.contains( entityDescriptor.getEntityName() ) ) {
+				continue;
+			}
+
+			final EntityBinding entityBinding = entityBinder.createEntityBinding( entityDescriptor );
+			metadata.addEntity( entityBinding );
+			processedEntityNames.add( entityBinding.getEntity().getName() );
 		}
 	}
 
 	public void processMappingDependentMetadata() {
 		processFetchProfiles();
 		processImports();
 		processResultSetMappings();
 		processNamedQueries();
 	}
 
 	private void processFetchProfiles(){
 		if ( hibernateMapping.getFetchProfile() == null ) {
 			return;
 		}
 		processFetchProfiles( hibernateMapping.getFetchProfile(), null );
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
 							jaxbRoot.getOrigin()
 					);
 				}
 				fetches.add( new FetchProfile.Fetch( entityName, fetch.getAssociation(), fetch.getStyle() ) );
 			}
 			metadata.addFetchProfile( new FetchProfile( profileName, fetches ) );
 		}
 	}
 
 	private void processImports() {
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
 
 	private void processResultSetMappings() {
 		if ( hibernateMapping.getResultset() == null ) {
 			return;
 		}
 //			bindResultSetMappingDefinitions( element, null, mappings );
 	}
 
 	private void processNamedQueries() {
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
 
 	private Value<ClassLoaderService> classLoaderService = new Value<ClassLoaderService>(
 			new Value.DeferredInitializer<ClassLoaderService>() {
 				@Override
 				public ClassLoaderService initialize() {
 					return metadata.getServiceRegistry().getService( ClassLoaderService.class );
 				}
 			}
 	);
 
 	@Override
-	public String extractEntityName(XMLHibernateMapping.XMLClass entityClazz) {
-		return HbmHelper.extractEntityName( entityClazz, mappingDefaults.getPackageName() );
-	}
-
-	@Override
 	public String getClassName(String unqualifiedName) {
 		return HbmHelper.getClassName( unqualifiedName, mappingDefaults.getPackageName() );
 	}
 
 	@Override
 	public String determineEntityName(EntityElement entityElement) {
 		return HbmHelper.determineEntityName( entityElement, mappingDefaults.getPackageName() );
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/JoinedSubClassEntityDescriptorImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/JoinedSubClassEntityDescriptorImpl.java
new file mode 100644
index 0000000000..81a7854abd
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/JoinedSubClassEntityDescriptorImpl.java
@@ -0,0 +1,80 @@
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
+package org.hibernate.metamodel.binder.source.hbm;
+
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.metamodel.binder.MappingException;
+import org.hibernate.metamodel.binder.source.JoinedSubClassEntityDescriptor;
+import org.hibernate.metamodel.binder.source.hbm.xml.mapping.EntityElement;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLJoinedSubclassElement;
+
+import static org.hibernate.metamodel.binding.InheritanceType.JOINED;
+
+/**
+ * Unified descriptor for (SQL) join-based inheritance strategies.
+ * 
+ * @author Steve Ebersole
+ */
+public class JoinedSubClassEntityDescriptorImpl
+		extends AbstractEntityDescriptorImpl
+		implements JoinedSubClassEntityDescriptor {
+
+	/**
+	 * This form used when an explicit {@code extends} attribute names this mapping's super entity.
+	 *
+	 * @param entityClazz The JAXB entity mapping
+	 * @param bindingContext The context for the binding process.
+	 */
+	public JoinedSubClassEntityDescriptorImpl(
+			EntityElement entityClazz,
+			HbmBindingContext bindingContext) {
+		this( entityClazz, extractExtendsName( entityClazz, bindingContext ), bindingContext );
+	}
+
+	private static String extractExtendsName(EntityElement entityClazz, HbmBindingContext bindingContext) {
+		final String extendsName = ( (XMLJoinedSubclassElement) entityClazz ).getExtends();
+		if ( StringHelper.isEmpty( extendsName ) ) {
+			throw new MappingException(
+					"Subclass entity mapping [" + bindingContext.determineEntityName( entityClazz )
+							+ "] was not contained in super entity mapping",
+					bindingContext.getOrigin()
+			);
+		}
+		return extendsName;
+	}
+
+	/**
+	 * This form would be used when the subclass definition if nested within its super type mapping.
+	 *
+	 * @param entityClazz The JAXB entity mapping
+	 * @param superEntityName The name of the containing (and thus super) entity
+	 * @param bindingContext The context for the binding process.
+	 */
+	public JoinedSubClassEntityDescriptorImpl(
+			EntityElement entityClazz,
+			String superEntityName,
+			HbmBindingContext bindingContext) {
+		super( entityClazz, superEntityName, JOINED, bindingContext );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/util/MappingHelper.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/MappingHelper.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/util/MappingHelper.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/MappingHelper.java
index d884f1f6ac..79d3aa2828 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/util/MappingHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/MappingHelper.java
@@ -1,84 +1,87 @@
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
-package org.hibernate.metamodel.source.hbm.util;
+package org.hibernate.metamodel.binder.source.hbm;
 
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.hibernate.MappingException;
 import org.hibernate.service.ServiceRegistry;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.classloading.spi.ClassLoadingException;
 
 /**
  * Helper class.
  *
  * @author Gail Badner
  */
 public class MappingHelper {
+
+	// todo : merge this and HbmHelper together
+
 	private MappingHelper() {
 	}
 
 	public static String getStringValue(String value, String defaultValue) {
 		return value == null ? defaultValue : value;
 	}
 
 	public static int getIntValue(String value, int defaultValue) {
 		return value == null ? defaultValue : Integer.parseInt( value );
 	}
 
 	public static boolean getBooleanValue(String value, boolean defaultValue) {
 		return value == null ? defaultValue : Boolean.valueOf( value );
 	}
 
 	public static boolean getBooleanValue(Boolean value, boolean defaultValue) {
 		return value == null ? defaultValue : value;
 	}
 
 	public static Set<String> getStringValueTokens(String str, String delimiters) {
 		if ( str == null ) {
 			return Collections.emptySet();
 		}
 		else {
 			StringTokenizer tokenizer = new StringTokenizer( str, delimiters );
 			Set<String> tokens = new HashSet<String>();
 			while ( tokenizer.hasMoreTokens() ) {
 				tokens.add( tokenizer.nextToken() );
 			}
 			return tokens;
 		}
 	}
 
 	public static Class classForName(String className, ServiceRegistry serviceRegistry) {
 		ClassLoaderService classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		try {
 			return classLoaderService.classForName( className );
 		}
 		catch ( ClassLoadingException e ) {
 			throw new MappingException( "Could not find class: " + className );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/RootEntityDescriptorImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/RootEntityDescriptorImpl.java
new file mode 100644
index 0000000000..153f7a9526
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/RootEntityDescriptorImpl.java
@@ -0,0 +1,134 @@
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
+package org.hibernate.metamodel.binder.source.hbm;
+
+import org.hibernate.cache.spi.access.AccessType;
+import org.hibernate.engine.OptimisticLockStyle;
+import org.hibernate.metamodel.binder.MappingException;
+import org.hibernate.metamodel.binder.source.RootEntityDescriptor;
+import org.hibernate.metamodel.binder.source.TableDescriptor;
+import org.hibernate.metamodel.binder.source.hbm.xml.mapping.EntityElement;
+import org.hibernate.metamodel.binding.Caching;
+import org.hibernate.metamodel.binding.InheritanceType;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLCacheElement;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
+
+/**
+ * Unified descriptor for root entity (no inheritance strategy).
+ *
+ * @author Steve Ebersole
+ */
+public class RootEntityDescriptorImpl extends AbstractEntityDescriptorImpl implements RootEntityDescriptor {
+	private final boolean mutable;
+	private final boolean explicitPolymorphism;
+	private final String whereFilter;
+	private final String rowId;
+	private final Caching caching;
+	private final OptimisticLockStyle optimisticLockStyle;
+
+	private final TableDescriptor baseTableDescriptor;
+
+	public RootEntityDescriptorImpl(EntityElement entityClazz, HbmBindingContext bindingContext) {
+		super( entityClazz, null, InheritanceType.NO_INHERITANCE, bindingContext );
+
+		// the mapping has to be <class/>
+		final XMLHibernateMapping.XMLClass xmlClass = (XMLHibernateMapping.XMLClass) entityClazz;
+
+		this.mutable = xmlClass.isMutable();
+		this.explicitPolymorphism = "explicit".equals( xmlClass.getPolymorphism() );
+		this.whereFilter = xmlClass.getWhere();
+		this.rowId = xmlClass.getRowid();
+		this.caching = interpretCaching( xmlClass, getEntityName() );
+		this.optimisticLockStyle = interpretOptimisticLockStyle( xmlClass, bindingContext );
+
+		this.baseTableDescriptor = new TableDescriptorImpl(
+				xmlClass.getSchema(),
+				xmlClass.getCatalog(),
+				xmlClass.getTable(),
+				this,
+				bindingContext
+		);
+	}
+
+	private static Caching interpretCaching(XMLHibernateMapping.XMLClass xmlClass, String entityName) {
+		final XMLCacheElement cache = xmlClass.getCache();
+		if ( cache == null ) {
+			return null;
+		}
+		final String region = cache.getRegion() != null ? cache.getRegion() : entityName;
+		final AccessType accessType = Enum.valueOf( AccessType.class, cache.getUsage() );
+		final boolean cacheLazyProps = !"non-lazy".equals( cache.getInclude() );
+		return new Caching( region, accessType, cacheLazyProps );
+	}
+
+	private static OptimisticLockStyle interpretOptimisticLockStyle(
+			XMLHibernateMapping.XMLClass entityClazz,
+			HbmBindingContext bindingContext) {
+		final String optimisticLockModeString = MappingHelper.getStringValue( entityClazz.getOptimisticLock(), "version" );
+		try {
+			return OptimisticLockStyle.valueOf( optimisticLockModeString.toUpperCase() );
+		}
+		catch (Exception e) {
+			throw new MappingException(
+					"Unknown optimistic-lock value : " + optimisticLockModeString,
+					bindingContext.getOrigin()
+			);
+		}
+	}
+
+	@Override
+	public boolean isMutable() {
+		return mutable;
+	}
+
+	@Override
+	public boolean isExplicitPolymorphism() {
+		return explicitPolymorphism;
+	}
+
+	@Override
+	public String getWhereFilter() {
+		return whereFilter;
+	}
+
+	@Override
+	public String getRowId() {
+		return rowId;
+	}
+
+	@Override
+	public Caching getCaching() {
+		return caching;
+	}
+
+	@Override
+	public OptimisticLockStyle getOptimisticLockStyle() {
+		return optimisticLockStyle;
+	}
+
+	@Override
+	public TableDescriptor getBaseTable() {
+		return baseTableDescriptor;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/hbm/TableViewImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/TableDescriptorImpl.java
similarity index 63%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/hbm/TableViewImpl.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/TableDescriptorImpl.java
index 5427136844..8765839653 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/view/hbm/TableViewImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/TableDescriptorImpl.java
@@ -1,62 +1,86 @@
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
-package org.hibernate.metamodel.binder.view.hbm;
+package org.hibernate.metamodel.binder.source.hbm;
 
-import org.hibernate.metamodel.binder.view.TableView;
-import org.hibernate.metamodel.source.hbm.HbmBindingContext;
+import org.hibernate.metamodel.binder.Origin;
+import org.hibernate.metamodel.binder.source.EntityDescriptor;
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
+import org.hibernate.metamodel.binder.source.TableDescriptor;
+import org.hibernate.metamodel.binder.source.UnifiedDescriptorObject;
 
 /**
  * @author Steve Ebersole
  */
-public class TableViewImpl implements TableView {
+public class TableDescriptorImpl implements TableDescriptor {
 	private final String explicitSchemaName;
 	private final String explicitCatalogName;
 	private final String tableName;
 
-	public TableViewImpl(
+	private final EntityDescriptor entityDescriptor;
+	private final HbmBindingContext bindingContext;
+
+	public TableDescriptorImpl(
 			String explicitSchemaName,
 			String explicitCatalogName,
 			String tableName,
-			EntityViewImpl entityView,
+			EntityDescriptor entityDescriptor,
 			HbmBindingContext bindingContext) {
 		this.explicitSchemaName = explicitSchemaName;
 		this.explicitCatalogName = explicitCatalogName;
 		this.tableName = tableName;
+
+		this.entityDescriptor = entityDescriptor;
+		this.bindingContext = bindingContext;
 	}
 
 	@Override
 	public String getExplicitSchemaName() {
 		return explicitSchemaName;
 	}
 
 	@Override
 	public String getExplicitCatalogName() {
 		return explicitCatalogName;
 	}
 
 	@Override
 	public String getTableName() {
 		return tableName;
 	}
+
+	@Override
+	public Origin getOrigin() {
+		return bindingContext.getOrigin();
+	}
+
+	@Override
+	public UnifiedDescriptorObject getContainingDescriptor() {
+		return entityDescriptor;
+	}
+
+	@Override
+	public MetaAttributeContext getMetaAttributeContext() {
+		return bindingContext.getMetaAttributeContext();
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/UnionSubClassEntityDescriptorImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/UnionSubClassEntityDescriptorImpl.java
new file mode 100644
index 0000000000..01792b9080
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/UnionSubClassEntityDescriptorImpl.java
@@ -0,0 +1,80 @@
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
+package org.hibernate.metamodel.binder.source.hbm;
+
+import org.hibernate.internal.util.StringHelper;
+import org.hibernate.metamodel.binder.MappingException;
+import org.hibernate.metamodel.binder.source.JoinedSubClassEntityDescriptor;
+import org.hibernate.metamodel.binder.source.hbm.xml.mapping.EntityElement;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLUnionSubclassElement;
+
+import static org.hibernate.metamodel.binding.InheritanceType.JOINED;
+
+/**
+ * Unified descriptor for (SQL) union-based inheritance strategies.
+ *
+ * @author Steve Ebersole
+ */
+public class UnionSubClassEntityDescriptorImpl
+		extends AbstractEntityDescriptorImpl
+		implements JoinedSubClassEntityDescriptor {
+
+	/**
+	 * This form used when an explicit {@code extends} attribute names this mapping's super entity.
+	 *
+	 * @param entityClazz The JAXB entity mapping
+	 * @param bindingContext The context for the binding process.
+	 */
+	public UnionSubClassEntityDescriptorImpl(
+			EntityElement entityClazz,
+			HbmBindingContext bindingContext) {
+		this( entityClazz, extractExtendsName( entityClazz, bindingContext ), bindingContext );
+	}
+
+	private static String extractExtendsName(EntityElement entityClazz, HbmBindingContext bindingContext) {
+		final String extendsName = ( (XMLUnionSubclassElement) entityClazz ).getExtends();
+		if ( StringHelper.isEmpty( extendsName ) ) {
+			throw new MappingException(
+					"Subclass entity mapping [" + bindingContext.determineEntityName( entityClazz )
+							+ "] was not contained in super entity mapping",
+					bindingContext.getOrigin()
+			);
+		}
+		return extendsName;
+	}
+
+	/**
+	 * This form would be used when the subclass definition if nested within its super type mapping.
+	 *
+	 * @param entityClazz The JAXB entity mapping
+	 * @param superEntityName The name of the containing (and thus super) entity
+	 * @param bindingContext The context for the binding process.
+	 */
+	public UnionSubClassEntityDescriptorImpl(
+			EntityElement entityClazz,
+			String superEntityName,
+			HbmBindingContext bindingContext) {
+		super( entityClazz, superEntityName, JOINED, bindingContext );
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/xml/mapping/Discriminated.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/xml/mapping/Discriminated.java
similarity index 94%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/xml/mapping/Discriminated.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/xml/mapping/Discriminated.java
index 7f4a653007..f20ff854c9 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/xml/mapping/Discriminated.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/xml/mapping/Discriminated.java
@@ -1,31 +1,31 @@
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
-package org.hibernate.metamodel.source.hbm.xml.mapping;
+package org.hibernate.metamodel.binder.source.hbm.xml.mapping;
 
 /**
  * @author Steve Ebersole
  */
 public interface Discriminated {
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/xml/mapping/EntityElement.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/xml/mapping/EntityElement.java
similarity index 74%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/xml/mapping/EntityElement.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/xml/mapping/EntityElement.java
index d3bac5b6e4..e81dec0678 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/xml/mapping/EntityElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/xml/mapping/EntityElement.java
@@ -1,58 +1,67 @@
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
-package org.hibernate.metamodel.source.hbm.xml.mapping;
+package org.hibernate.metamodel.binder.source.hbm.xml.mapping;
 
 import java.util.List;
 
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLFetchProfileElement;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLLoaderElement;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLResultsetElement;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlDeleteElement;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlInsertElement;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlUpdateElement;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSynchronizeElement;
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLTuplizerElement;
+
 /**
  * @author Steve Ebersole
  */
 public interface EntityElement extends MetaAttributeContainer {
 	public String getName();
 	public String getEntityName();
 
     public Boolean isAbstract();
     public Boolean isLazy();
     public String getProxy();
     public String getBatchSize();
     public boolean isDynamicInsert();
     public boolean isDynamicUpdate();
     public boolean isSelectBeforeUpdate();
 
 	public List<XMLTuplizerElement> getTuplizer();
     public String getPersister();
 
 	public XMLLoaderElement getLoader();
 	public XMLSqlInsertElement getSqlInsert();
 	public XMLSqlUpdateElement getSqlUpdate();
 	public XMLSqlDeleteElement getSqlDelete();
 
 	public List<XMLSynchronizeElement> getSynchronize();
 
 	public List<XMLFetchProfileElement> getFetchProfile();
 
     public List<XMLResultsetElement> getResultset();
 
     public List<Object> getQueryOrSqlQuery();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/xml/mapping/MetaAttributeContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/xml/mapping/MetaAttributeContainer.java
similarity index 89%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/xml/mapping/MetaAttributeContainer.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/xml/mapping/MetaAttributeContainer.java
index c1e195f013..1c153dbd37 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/xml/mapping/MetaAttributeContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/xml/mapping/MetaAttributeContainer.java
@@ -1,33 +1,35 @@
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
-package org.hibernate.metamodel.source.hbm.xml.mapping;
+package org.hibernate.metamodel.binder.source.hbm.xml.mapping;
 
 import java.util.List;
 
+import org.hibernate.metamodel.source.hbm.xml.mapping.XMLMetaElement;
+
 /**
  * @author Steve Ebersole
  */
 public interface MetaAttributeContainer {
 	public List<XMLMetaElement> getMeta();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/xml/mapping/SubclassEntityElement.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/xml/mapping/SubclassEntityElement.java
similarity index 94%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/xml/mapping/SubclassEntityElement.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/xml/mapping/SubclassEntityElement.java
index ea92ec3b97..71ba1d74ab 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/xml/mapping/SubclassEntityElement.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/hbm/xml/mapping/SubclassEntityElement.java
@@ -1,31 +1,31 @@
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
-package org.hibernate.metamodel.source.hbm.xml.mapping;
+package org.hibernate.metamodel.binder.source.hbm.xml.mapping;
 
 /**
  * @author Steve Ebersole
  */
 public interface SubclassEntityElement extends EntityElement {
     public String getExtends();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/EntityReferenceResolver.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/EntityReferenceResolver.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/EntityReferenceResolver.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/EntityReferenceResolver.java
index f8a866b1ce..9e4de6227d 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/EntityReferenceResolver.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/EntityReferenceResolver.java
@@ -1,75 +1,75 @@
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
-package org.hibernate.metamodel.source.internal;
+package org.hibernate.metamodel.binder.source.internal;
 
 import org.hibernate.MappingException;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.EntityReferencingAttributeBinding;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
 
 /**
  * @author Gail Badner
  */
 class EntityReferenceResolver {
 
 	private final MetadataImplementor metadata;
 
 	EntityReferenceResolver(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	void resolve() {
 		for ( EntityBinding entityBinding : metadata.getEntityBindings() ) {
 			for ( EntityReferencingAttributeBinding attributeBinding :  entityBinding.getEntityReferencingAttributeBindings() ) {
 				resolve( attributeBinding );
 			}
 		}
 	}
 
 	private void resolve(EntityReferencingAttributeBinding attributeBinding) {
 		if ( attributeBinding.getReferencedEntityName() == null ) {
 			throw new IllegalArgumentException( "attributeBinding has null entityName: " + attributeBinding.getAttribute().getName() );
 		}
 		EntityBinding entityBinding = metadata.getEntityBinding( attributeBinding.getReferencedEntityName() );
 		if ( entityBinding == null ) {
 			throw new org.hibernate.MappingException(
 					"Attribute [" + attributeBinding.getAttribute().getName() +
 					"] refers to unknown entity: [" + attributeBinding.getReferencedEntityName() + "]" );
 		}
 		AttributeBinding referencedAttributeBinding =
 				attributeBinding.isPropertyReference() ?
 						entityBinding.getAttributeBinding( attributeBinding.getReferencedAttributeName() ) :
 						entityBinding.getEntityIdentifier().getValueBinding();
 		if ( referencedAttributeBinding == null ) {
 			// TODO: does attribute name include path w/ entity name?
 			throw new MappingException(
 					"Attribute [" + attributeBinding.getAttribute().getName() +
 					"] refers to unknown attribute: [" + attributeBinding.getReferencedEntityName() + "]"
 			);
 		}
 		attributeBinding.resolveReference( referencedAttributeBinding );
 		referencedAttributeBinding.addEntityReferencingAttributeBinding( attributeBinding );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/JaxbHelper.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/JaxbHelper.java
similarity index 97%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/JaxbHelper.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/JaxbHelper.java
index 729a43a0ee..d0042dac3f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/JaxbHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/JaxbHelper.java
@@ -1,312 +1,312 @@
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
 
-package org.hibernate.metamodel.source.internal;
+package org.hibernate.metamodel.binder.source.internal;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.net.URL;
 import javax.xml.XMLConstants;
 import javax.xml.bind.JAXBContext;
 import javax.xml.bind.JAXBException;
 import javax.xml.bind.Unmarshaller;
 import javax.xml.bind.ValidationEvent;
 import javax.xml.bind.ValidationEventHandler;
 import javax.xml.bind.ValidationEventLocator;
 import javax.xml.namespace.QName;
 import javax.xml.stream.XMLEventReader;
 import javax.xml.stream.XMLInputFactory;
 import javax.xml.stream.XMLStreamException;
 import javax.xml.stream.events.Attribute;
 import javax.xml.stream.events.XMLEvent;
 import javax.xml.transform.dom.DOMSource;
 import javax.xml.transform.stream.StreamSource;
 import javax.xml.validation.Schema;
 import javax.xml.validation.SchemaFactory;
 
 import org.jboss.logging.Logger;
 import org.w3c.dom.Document;
 import org.w3c.dom.Element;
 import org.xml.sax.SAXException;
 
 import org.hibernate.metamodel.MetadataSources;
-import org.hibernate.metamodel.source.MappingException;
-import org.hibernate.metamodel.source.Origin;
-import org.hibernate.metamodel.source.XsdException;
+import org.hibernate.metamodel.binder.MappingException;
+import org.hibernate.metamodel.binder.Origin;
+import org.hibernate.metamodel.binder.XsdException;
 import org.hibernate.metamodel.source.annotation.xml.XMLEntityMappings;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * Helper class for unmarshalling xml configuration using StAX and JAXB.
  *
  * @author Steve Ebersole
  * @author Hardy Ferentschik
  */
 public class JaxbHelper {
 	private static final Logger log = Logger.getLogger( JaxbHelper.class );
 
 	public static final String ASSUMED_ORM_XSD_VERSION = "2.0";
 
 	private final MetadataSources metadataSources;
 
 	public JaxbHelper(MetadataSources metadataSources) {
 		this.metadataSources = metadataSources;
 	}
 
 	public JaxbRoot unmarshal(InputStream stream, Origin origin) {
 		try {
 			XMLEventReader staxReader = staxFactory().createXMLEventReader( stream );
 			try {
 				return unmarshal( staxReader, origin );
 			}
 			finally {
 				try {
 					staxReader.close();
 				}
 				catch ( Exception ignore ) {
 				}
 			}
 		}
 		catch ( XMLStreamException e ) {
 			throw new MappingException( "Unable to create stax reader", e, origin );
 		}
 	}
 
 	private XMLInputFactory staxFactory;
 
 	private XMLInputFactory staxFactory() {
 		if ( staxFactory == null ) {
 			staxFactory = buildStaxFactory();
 		}
 		return staxFactory;
 	}
 
 	@SuppressWarnings( { "UnnecessaryLocalVariable" })
 	private XMLInputFactory buildStaxFactory() {
 		XMLInputFactory staxFactory = XMLInputFactory.newInstance();
 		return staxFactory;
 	}
 
 	private static final QName ORM_VERSION_ATTRIBUTE_QNAME = new QName( "version" );
 
 	@SuppressWarnings( { "unchecked" })
 	private JaxbRoot unmarshal(XMLEventReader staxEventReader, final Origin origin) {
 		XMLEvent event;
 		try {
 			event = staxEventReader.peek();
 			while ( event != null && !event.isStartElement() ) {
 				staxEventReader.nextEvent();
 				event = staxEventReader.peek();
 			}
 		}
 		catch ( Exception e ) {
 			throw new MappingException( "Error accessing stax stream", e, origin );
 		}
 
 		if ( event == null ) {
 			throw new MappingException( "Could not locate root element", origin );
 		}
 
 		final Schema validationSchema;
 		final Class jaxbTarget;
 
 		final String elementName = event.asStartElement().getName().getLocalPart();
 
 		if ( "entity-mappings".equals( elementName ) ) {
 			final Attribute attribute = event.asStartElement().getAttributeByName( ORM_VERSION_ATTRIBUTE_QNAME );
 			final String explicitVersion = attribute == null ? null : attribute.getValue();
 			validationSchema = resolveSupportedOrmXsd( explicitVersion );
 			jaxbTarget = XMLEntityMappings.class;
 		}
 		else {
 			validationSchema = hbmSchema();
 			jaxbTarget = XMLHibernateMapping.class;
 		}
 
 		final Object target;
 		final ContextProvidingValidationEventHandler handler = new ContextProvidingValidationEventHandler();
 		try {
 			JAXBContext jaxbContext = JAXBContext.newInstance( jaxbTarget );
 			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
 			unmarshaller.setSchema( validationSchema );
 			unmarshaller.setEventHandler( handler );
 			target = unmarshaller.unmarshal( staxEventReader );
 		}
 
 		catch ( JAXBException e ) {
 			StringBuilder builder = new StringBuilder();
 			builder.append( "Unable to perform unmarshalling at line number " );
 			builder.append( handler.getLineNumber() );
 			builder.append( " and column " );
 			builder.append( handler.getColumnNumber() );
 			builder.append( ". Message: " );
 			builder.append( handler.getMessage() );
 			throw new MappingException( builder.toString(), e, origin );
 		}
 
 		return new JaxbRoot( target, origin );
 	}
 
 	@SuppressWarnings( { "unchecked" })
 	public JaxbRoot unmarshal(Document document, Origin origin) {
 		Element rootElement = document.getDocumentElement();
 		if ( rootElement == null ) {
 			throw new MappingException( "No root element found", origin );
 		}
 
 		final Schema validationSchema;
 		final Class jaxbTarget;
 
 		if ( "entity-mappings".equals( rootElement.getNodeName() ) ) {
 			final String explicitVersion = rootElement.getAttribute( "version" );
 			validationSchema = resolveSupportedOrmXsd( explicitVersion );
 			jaxbTarget = XMLEntityMappings.class;
 		}
 		else {
 			validationSchema = hbmSchema();
 			jaxbTarget = XMLHibernateMapping.class;
 		}
 
 		final Object target;
 		try {
 			JAXBContext jaxbContext = JAXBContext.newInstance( jaxbTarget );
 			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
 			unmarshaller.setSchema( validationSchema );
 			target = unmarshaller.unmarshal( new DOMSource( document ) );
 		}
 		catch ( JAXBException e ) {
 			throw new MappingException( "Unable to perform unmarshalling", e, origin );
 		}
 
 		return new JaxbRoot( target, origin );
 	}
 
 	private Schema resolveSupportedOrmXsd(String explicitVersion) {
 		final String xsdVersionString = explicitVersion == null ? ASSUMED_ORM_XSD_VERSION : explicitVersion;
 		if ( "1.0".equals( xsdVersionString ) ) {
 			return orm1Schema();
 		}
 		else if ( "2.0".equals( xsdVersionString ) ) {
 			return orm2Schema();
 		}
 		throw new IllegalArgumentException( "Unsupported orm.xml XSD version encountered [" + xsdVersionString + "]" );
 	}
 
 	public static final String HBM_SCHEMA_NAME = "org/hibernate/hibernate-mapping-4.0.xsd";
 	public static final String ORM_1_SCHEMA_NAME = "org/hibernate/ejb/orm_1_0.xsd";
 	public static final String ORM_2_SCHEMA_NAME = "org/hibernate/ejb/orm_2_0.xsd";
 
 	private Schema hbmSchema;
 
 	private Schema hbmSchema() {
 		if ( hbmSchema == null ) {
 			hbmSchema = resolveLocalSchema( HBM_SCHEMA_NAME );
 		}
 		return hbmSchema;
 	}
 
 	private Schema orm1Schema;
 
 	private Schema orm1Schema() {
 		if ( orm1Schema == null ) {
 			orm1Schema = resolveLocalSchema( ORM_1_SCHEMA_NAME );
 		}
 		return orm1Schema;
 	}
 
 	private Schema orm2Schema;
 
 	private Schema orm2Schema() {
 		if ( orm2Schema == null ) {
 			orm2Schema = resolveLocalSchema( ORM_2_SCHEMA_NAME );
 		}
 		return orm2Schema;
 	}
 
 	private Schema resolveLocalSchema(String schemaName) {
 		return resolveLocalSchema( schemaName, XMLConstants.W3C_XML_SCHEMA_NS_URI );
 	}
 
 	private Schema resolveLocalSchema(String schemaName, String schemaLanguage) {
 		URL url = metadataSources.getServiceRegistry()
 				.getService( ClassLoaderService.class )
 				.locateResource( schemaName );
 		if ( url == null ) {
 			throw new XsdException( "Unable to locate schema [" + schemaName + "] via classpath", schemaName );
 		}
 		try {
 			InputStream schemaStream = url.openStream();
 			try {
 				StreamSource source = new StreamSource( url.openStream() );
 				SchemaFactory schemaFactory = SchemaFactory.newInstance( schemaLanguage );
 				return schemaFactory.newSchema( source );
 			}
 			catch ( SAXException e ) {
 				throw new XsdException( "Unable to load schema [" + schemaName + "]", e, schemaName );
 			}
 			catch ( IOException e ) {
 				throw new XsdException( "Unable to load schema [" + schemaName + "]", e, schemaName );
 			}
 			finally {
 				try {
 					schemaStream.close();
 				}
 				catch ( IOException e ) {
 					log.debugf( "Problem closing schema stream [%s]", e.toString() );
 				}
 			}
 		}
 		catch ( IOException e ) {
 			throw new XsdException( "Stream error handling schema url [" + url.toExternalForm() + "]", schemaName );
 		}
 	}
 
 	static class ContextProvidingValidationEventHandler implements ValidationEventHandler {
 		private int lineNumber;
 		private int columnNumber;
 		private String message;
 
 		@Override
 		public boolean handleEvent(ValidationEvent validationEvent) {
 			ValidationEventLocator locator = validationEvent.getLocator();
 			lineNumber = locator.getLineNumber();
 			columnNumber = locator.getColumnNumber();
 			message = validationEvent.getMessage();
 			return false;
 		}
 
 		public int getLineNumber() {
 			return lineNumber;
 		}
 
 		public int getColumnNumber() {
 			return columnNumber;
 		}
 
 		public String getMessage() {
 			return message;
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/JaxbRoot.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/JaxbRoot.java
similarity index 94%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/JaxbRoot.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/JaxbRoot.java
index 888b05aecb..4152de5308 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/JaxbRoot.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/JaxbRoot.java
@@ -1,60 +1,60 @@
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
-package org.hibernate.metamodel.source.internal;
+package org.hibernate.metamodel.binder.source.internal;
 
-import org.hibernate.metamodel.source.Origin;
+import org.hibernate.metamodel.binder.Origin;
 
 /**
  * Holds information about a JAXB-unmarshalled XML document.
  *
  * @author Hardy Ferentschik
  * @author Steve Ebersole
  */
 public class JaxbRoot<T> {
 	private final T root;
 	private final Origin origin;
 
 	public JaxbRoot(T root, Origin origin) {
 		this.root = root;
 		this.origin = origin;
 	}
 
 	/**
 	 * Obtain the root JAXB bound object
 	 *
 	 * @return The JAXB root object
 	 */
 	public T getRoot() {
 		return root;
 	}
 
 	/**
 	 * Obtain the metadata about the document's origin
 	 *
 	 * @return The origin
 	 */
 	public Origin getOrigin() {
 		return origin;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/MetadataBuilderImpl.java
similarity index 99%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/MetadataBuilderImpl.java
index 0bb2ea2cce..bc22b1c112 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/MetadataBuilderImpl.java
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
-package org.hibernate.metamodel.source.internal;
+package org.hibernate.metamodel.binder.source.internal;
 
 import javax.persistence.SharedCacheMode;
 
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.cfg.EJB3NamingStrategy;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.metamodel.Metadata;
 import org.hibernate.metamodel.MetadataBuilder;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SourceProcessingOrder;
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
 	public MetadataBuilder with(SourceProcessingOrder sourceProcessingOrder) {
 		this.options.sourceProcessingOrder = sourceProcessingOrder;
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
 		private SourceProcessingOrder sourceProcessingOrder = SourceProcessingOrder.HBM_FIRST;
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
 		public SourceProcessingOrder getSourceProcessingOrder() {
 			return sourceProcessingOrder;
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/MetadataImpl.java
similarity index 93%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/MetadataImpl.java
index fdf64d3f2a..50d29788ca 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/MetadataImpl.java
@@ -1,551 +1,560 @@
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
-package org.hibernate.metamodel.source.internal;
+package org.hibernate.metamodel.binder.source.internal;
 
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
+import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
 import org.hibernate.metamodel.SourceProcessingOrder;
+import org.hibernate.metamodel.binder.source.MappingDefaults;
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
+import org.hibernate.metamodel.binder.source.SourceProcessor;
+import org.hibernate.metamodel.binder.source.hbm.HbmSourceProcessorImpl;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.metamodel.source.annotations.AnnotationSourceProcessor;
-import org.hibernate.metamodel.source.hbm.HbmSourceProcessor;
-import org.hibernate.metamodel.source.spi.MappingDefaults;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
-import org.hibernate.metamodel.source.spi.SourceProcessor;
 import org.hibernate.persister.spi.PersisterClassResolver;
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
-	private final org.hibernate.internal.util.Value<ClassLoaderService> classLoaderService;
+
+	private final Value<ClassLoaderService> classLoaderService;
+	private final Value<PersisterClassResolver> persisterClassResolverService;
 
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
 
 		final SourceProcessor[] sourceProcessors;
 		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
 			sourceProcessors = new SourceProcessor[] {
-					new HbmSourceProcessor( this ),
+					new HbmSourceProcessorImpl( this ),
 					new AnnotationSourceProcessor( this )
 			};
 		}
 		else {
 			sourceProcessors = new SourceProcessor[] {
 					new AnnotationSourceProcessor( this ),
-					new HbmSourceProcessor( this )
+					new HbmSourceProcessorImpl( this )
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
 
 		prepare( sourceProcessors, metadataSources );
 		bindIndependentMetadata( sourceProcessors, metadataSources );
 		bindTypeDependentMetadata( sourceProcessors, metadataSources );
 		bindMappingMetadata( sourceProcessors, metadataSources, processedEntityNames );
 		bindMappingDependentMetadata( sourceProcessors, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
 		new EntityReferenceResolver( this ).resolve();
 		new AttributeTypeResolver( this ).resolve();
 	}
 
 	private void prepare(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.prepare( metadataSources );
 		}
 	}
 
 	private void bindIndependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.processIndependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindTypeDependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.processTypeDependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindMappingMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources, List<String> processedEntityNames) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.processMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
 	private void bindMappingDependentMetadata(SourceProcessor[] sourceProcessors, MetadataSources metadataSources) {
 		for ( SourceProcessor sourceProcessor : sourceProcessors ) {
 			sourceProcessor.processMappingDependentMetadata( metadataSources );
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
-		if ( def == null || def.getName() == null ) {
-			throw new IllegalArgumentException( "Named query definition object or name is null: " + def.getQueryString() );
+		if ( def == null ) {
+			throw new IllegalArgumentException( "Named query definition is null" );
+		}
+		else if ( def.getName() == null ) {
+			throw new IllegalArgumentException( "Named query definition name is null: " + def.getQueryString() );
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
-			throw new IllegalArgumentException( "Resultset mappping object or name is null: " + resultSetMappingDefinition );
+			throw new IllegalArgumentException( "Result-set mapping object or name is null: " + resultSetMappingDefinition );
 		}
 		resultSetMappings.put( resultSetMappingDefinition.getName(), resultSetMappingDefinition );
 	}
 
 	@Override
 	public Iterable<ResultSetMappingDefinition> getResultSetMappingDefinitions() {
 		return resultSetMappings.values();
 	}
 
 	@Override
 	public void addTypeDefinition(TypeDef typeDef) {
-		if ( typeDef == null || typeDef.getName() == null ) {
-			throw new IllegalArgumentException( "Type definition object or name is null: " + typeDef.getTypeClass() );
+		if ( typeDef == null ) {
+			throw new IllegalArgumentException( "Type definition is null" );
+		}
+		else if ( typeDef.getName() == null ) {
+			throw new IllegalArgumentException( "Type definition name is null: " + typeDef.getTypeClass() );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/OverriddenMappingDefaults.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/OverriddenMappingDefaults.java
similarity index 96%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/OverriddenMappingDefaults.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/OverriddenMappingDefaults.java
index b94256b8ad..c04547c087 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/OverriddenMappingDefaults.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/OverriddenMappingDefaults.java
@@ -1,108 +1,108 @@
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
-package org.hibernate.metamodel.source.internal;
+package org.hibernate.metamodel.binder.source.internal;
 
-import org.hibernate.metamodel.source.spi.MappingDefaults;
+import org.hibernate.metamodel.binder.source.MappingDefaults;
 
 /**
  * Represents a "nested level" in the mapping defaults stack.
  *
  * @author Steve Ebersole
  */
 public class OverriddenMappingDefaults implements MappingDefaults {
 	private MappingDefaults overriddenValues;
 
 	private final String packageName;
 	private final String schemaName;
 	private final String catalogName;
 	private final String idColumnName;
 	private final String discriminatorColumnName;
 	private final String cascade;
 	private final String propertyAccess;
 	private final Boolean associationLaziness;
 
 	public OverriddenMappingDefaults(
 			MappingDefaults overriddenValues,
 			String packageName,
 			String schemaName,
 			String catalogName,
 			String idColumnName,
 			String discriminatorColumnName,
 			String cascade,
 			String propertyAccess,
 			Boolean associationLaziness) {
 		if ( overriddenValues == null ) {
 			throw new IllegalArgumentException( "Overridden values cannot be null" );
 		}
 		this.overriddenValues = overriddenValues;
 		this.packageName = packageName;
 		this.schemaName = schemaName;
 		this.catalogName = catalogName;
 		this.idColumnName = idColumnName;
 		this.discriminatorColumnName = discriminatorColumnName;
 		this.cascade = cascade;
 		this.propertyAccess = propertyAccess;
 		this.associationLaziness = associationLaziness;
 	}
 
 	@Override
 	public String getPackageName() {
 		return packageName == null ? overriddenValues.getPackageName() : packageName;
 	}
 
 	@Override
 	public String getSchemaName() {
 		return schemaName == null ? overriddenValues.getSchemaName() : schemaName;
 	}
 
 	@Override
 	public String getCatalogName() {
 		return catalogName == null ? overriddenValues.getCatalogName() : catalogName;
 	}
 
 	@Override
 	public String getIdColumnName() {
 		return idColumnName == null ? overriddenValues.getIdColumnName() : idColumnName;
 	}
 
 	@Override
 	public String getDiscriminatorColumnName() {
 		return discriminatorColumnName == null ? overriddenValues.getDiscriminatorColumnName() : discriminatorColumnName;
 	}
 
 	@Override
 	public String getCascadeStyle() {
 		return cascade == null ? overriddenValues.getCascadeStyle() : cascade;
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return propertyAccess == null ? overriddenValues.getPropertyAccessorName() : propertyAccess;
 	}
 
 	@Override
 	public boolean areAssociationsLazy() {
 		return associationLaziness == null ? overriddenValues.areAssociationsLazy() : associationLaziness;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/SessionFactoryBuilderImpl.java
similarity index 95%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImpl.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/SessionFactoryBuilderImpl.java
index c3ec4978eb..3908ac359f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binder/source/internal/SessionFactoryBuilderImpl.java
@@ -1,88 +1,88 @@
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
-package org.hibernate.metamodel.source.internal;
+package org.hibernate.metamodel.binder.source.internal;
 
 import java.io.Serializable;
 
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.Interceptor;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.internal.SessionFactoryImpl;
 import org.hibernate.metamodel.SessionFactoryBuilder;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 
 /**
  * @author Gail Badner
  */
 public class SessionFactoryBuilderImpl implements SessionFactoryBuilder {
 	SessionFactoryOptionsImpl options;
 
 	private final MetadataImplementor metadata;
 
 	/* package-protected */
 	SessionFactoryBuilderImpl(MetadataImplementor metadata) {
 		this.metadata = metadata;
 		options = new SessionFactoryOptionsImpl();
 	}
 
 	@Override
 	public SessionFactoryBuilder with(Interceptor interceptor) {
 		this.options.interceptor = interceptor;
 		return this;
 	}
 
 	@Override
 	public SessionFactoryBuilder with(EntityNotFoundDelegate entityNotFoundDelegate) {
 		this.options.entityNotFoundDelegate = entityNotFoundDelegate;
 		return this;
 	}
 
 	@Override
 	public SessionFactory buildSessionFactory() {
 		return new SessionFactoryImpl(metadata, options, null );
 	}
 
 	private static class SessionFactoryOptionsImpl implements SessionFactory.SessionFactoryOptions {
 		private Interceptor interceptor = EmptyInterceptor.INSTANCE;
 
 		// TODO: should there be a DefaultEntityNotFoundDelegate.INSTANCE?
 		private EntityNotFoundDelegate entityNotFoundDelegate = new EntityNotFoundDelegate() {
 				public void handleEntityNotFound(String entityName, Serializable id) {
 					throw new ObjectNotFoundException( id, entityName );
 				}
 		};
 
 		@Override
 		public Interceptor getInterceptor() {
 			return interceptor;
 		}
 
 		@Override
 		public EntityNotFoundDelegate getEntityNotFoundDelegate() {
 			return entityNotFoundDelegate;
 		}
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
index a1c6c578c3..960548a079 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AbstractAttributeBinding.java
@@ -1,278 +1,278 @@
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
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.MappingException;
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
 import org.hibernate.metamodel.binding.state.AttributeBindingState;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Tuple;
 import org.hibernate.metamodel.relational.Value;
 import org.hibernate.metamodel.relational.state.SimpleValueRelationalState;
 import org.hibernate.metamodel.relational.state.TupleRelationalState;
 import org.hibernate.metamodel.relational.state.ValueCreator;
 import org.hibernate.metamodel.relational.state.ValueRelationalState;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractAttributeBinding implements AttributeBinding {
 	private final HibernateTypeDescriptor hibernateTypeDescriptor = new HibernateTypeDescriptor();
 	private final EntityBinding entityBinding;
 	private final Set<EntityReferencingAttributeBinding> entityReferencingAttributeBindings = new HashSet<EntityReferencingAttributeBinding>();
 
 	private Attribute attribute;
 	private Value value;
 
 	private boolean isLazy;
 	private String propertyAccessorName;
 	private boolean isAlternateUniqueKey;
 	private Set<CascadeType> cascadeTypes;
 	private boolean optimisticLockable;
 
 	// DOM4J specific...
 	private String nodeName;
 
 	private MetaAttributeContext metaAttributeContext;
 
 	protected AbstractAttributeBinding(EntityBinding entityBinding) {
 		this.entityBinding = entityBinding;
 	}
 
 	protected void initialize(AttributeBindingState state) {
 		hibernateTypeDescriptor.setTypeName( state.getTypeName() );
 		hibernateTypeDescriptor.setTypeParameters( state.getTypeParameters() );
 		isLazy = state.isLazy();
 		propertyAccessorName = state.getPropertyAccessorName();
 		isAlternateUniqueKey = state.isAlternateUniqueKey();
 		cascadeTypes = state.getCascadeTypes();
 		optimisticLockable = state.isOptimisticLockable();
 		nodeName = state.getNodeName();
 		metaAttributeContext = state.getMetaAttributeContext();
 	}
 
 	@Override
 	public EntityBinding getEntityBinding() {
 		return entityBinding;
 	}
 
 	@Override
 	public Attribute getAttribute() {
 		return attribute;
 	}
 
 	protected void setAttribute(Attribute attribute) {
 		this.attribute = attribute;
 	}
 
 	protected boolean forceNonNullable() {
 		return false;
 	}
 
 	protected boolean forceUnique() {
 		return false;
 	}
 
 	protected final boolean isPrimaryKey() {
 		return this == getEntityBinding().getEntityIdentifier().getValueBinding();
 	}
 
 	protected void initializeValueRelationalState(ValueRelationalState state) {
 		// TODO: change to have ValueRelationalState generate the value
 		value = ValueCreator.createValue(
 				getEntityBinding().getBaseTable(),
 				getAttribute().getName(),
 				state,
 				forceNonNullable(),
 				forceUnique()
 		);
 		// TODO: not sure I like this here...
 		if ( isPrimaryKey() ) {
 			if ( SimpleValue.class.isInstance( value ) ) {
 				if ( !Column.class.isInstance( value ) ) {
 					// this should never ever happen..
 					throw new MappingException( "Simple ID is not a column." );
 				}
 				entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( value ) );
 			}
 			else {
 				for ( SimpleValueRelationalState val : TupleRelationalState.class.cast( state )
 						.getRelationalStates() ) {
 					if ( Column.class.isInstance( val ) ) {
 						entityBinding.getBaseTable().getPrimaryKey().addColumn( Column.class.cast( val ) );
 					}
 				}
 			}
 		}
 	}
 
 	@Override
 	public Value getValue() {
 		return value;
 	}
 
 	@Override
 	public HibernateTypeDescriptor getHibernateTypeDescriptor() {
 		return hibernateTypeDescriptor;
 	}
 
 	public Set<CascadeType> getCascadeTypes() {
 		return cascadeTypes;
 	}
 
 	public boolean isOptimisticLockable() {
 		return optimisticLockable;
 	}
 
 	public String getNodeName() {
 		return nodeName;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	@Override
 	public int getValuesSpan() {
 		if ( value == null ) {
 			return 0;
 		}
 		else if ( value instanceof Tuple ) {
 			return ( ( Tuple ) value ).valuesSpan();
 		}
 		else {
 			return 1;
 		}
 	}
 
 
 	@Override
 	public Iterable<SimpleValue> getValues() {
 		return value == null
 				? Collections.<SimpleValue>emptyList()
 				: value instanceof Tuple
 				? ( (Tuple) value ).values()
 				: Collections.singletonList( (SimpleValue) value );
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return propertyAccessorName;
 	}
 
 	@Override
 	public boolean isBasicPropertyAccessor() {
 		return propertyAccessorName==null || "property".equals( propertyAccessorName );
 	}
 
 
 	@Override
 	public boolean hasFormula() {
 		for ( SimpleValue simpleValue : getValues() ) {
 			if ( simpleValue instanceof DerivedValue ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	@Override
 	public boolean isAlternateUniqueKey() {
 		return isAlternateUniqueKey;
 	}
 
 	public void setAlternateUniqueKey(boolean alternateUniqueKey) {
 		this.isAlternateUniqueKey = alternateUniqueKey;
 	}
 
 	@Override
 	public boolean isNullable() {
 		for ( SimpleValue simpleValue : getValues() ) {
 			if ( simpleValue instanceof DerivedValue ) {
 				return true;
 			}
 			Column column = (Column) simpleValue;
 			if ( column.isNullable() ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	@Override
 	public boolean[] getColumnInsertability() {
 		List<Boolean> tmp = new ArrayList<Boolean>();
 		for ( SimpleValue simpleValue : getValues() ) {
 			tmp.add( !( simpleValue instanceof DerivedValue ) );
 		}
 		boolean[] rtn = new boolean[tmp.size()];
 		int i = 0;
 		for ( Boolean insertable : tmp ) {
 			rtn[i++] = insertable.booleanValue();
 		}
 		return rtn;
 	}
 
 	@Override
 	public boolean[] getColumnUpdateability() {
 		return getColumnInsertability();
 	}
 
 	@Override
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	protected void setLazy(boolean isLazy) {
 		this.isLazy = isLazy;
 	}
 
 	public void addEntityReferencingAttributeBinding(EntityReferencingAttributeBinding referencingAttributeBinding) {
 		entityReferencingAttributeBindings.add( referencingAttributeBinding );
 	}
 
 	public Set<EntityReferencingAttributeBinding> getEntityReferencingAttributeBindings() {
 		return Collections.unmodifiableSet( entityReferencingAttributeBindings );
 	}
 
 	public void validate() {
 		if ( !entityReferencingAttributeBindings.isEmpty() ) {
 			// TODO; validate that this AttributeBinding can be a target of an entity reference
 			// (e.g., this attribute is the primary key or there is a unique-key)
 			// can a unique attribute be used as a target? if so, does it need to be non-null?
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java
index e6796ba643..a7ae3df2f3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/AttributeBinding.java
@@ -1,115 +1,115 @@
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
 
 import java.util.Set;
 
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Value;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
 /**
  * The basic contract for binding between an {@link #getAttribute() attribute} and a {@link #getValue() value}
  *
  * @author Steve Ebersole
  */
 public interface AttributeBinding {
 	/**
 	 * Obtain the entity binding to which this attribute binding exists.
 	 *
 	 * @return The entity binding.
 	 */
 	public EntityBinding getEntityBinding();
 
 	/**
 	 * Obtain the attribute bound.
 	 *
 	 * @return The attribute.
 	 */
 	public Attribute getAttribute();
 
 	/**
 	 * Obtain the value bound
 	 *
 	 * @return The value
 	 */
 	public Value getValue();
 
 	/**
 	 * Obtain the descriptor for the Hibernate Type for this binding.
 	 *
 	 * @return The type descriptor
 	 */
 	public HibernateTypeDescriptor getHibernateTypeDescriptor();
 
 	/**
 	 * Obtain the meta attributes associated with this binding
 	 *
 	 * @return The meta attributes
 	 */
 	public MetaAttributeContext getMetaAttributeContext();
 
 	/**
 	 * Returns the number of {@link org.hibernate.metamodel.relational.SimpleValue}
 	 * objects that will be returned by {@link #getValues()}
 	 *
 	 * @return the number of objects that will be returned by {@link #getValues()}.
 	 *
 	 * @see {@link org.hibernate.metamodel.relational.SimpleValue}
 	 * @see {@link #getValues()}
 	 */
 	public int getValuesSpan();
 
 	/**
 	 * @return In the case that {@link #getValue()} represents a {@link org.hibernate.metamodel.relational.Tuple} this method
 	 *         gives access to its compound values.  In the case of {@link org.hibernate.metamodel.relational.SimpleValue},
 	 *         we return an Iterable over that single simple value.
 	 */
 	public Iterable<SimpleValue> getValues();
 
 	public String getPropertyAccessorName();
 
 	public boolean isBasicPropertyAccessor();
 
 	public boolean hasFormula();
 
 	public boolean isAlternateUniqueKey();
 
 	public boolean isNullable();
 
 	public boolean[] getColumnUpdateability();
 
 	public boolean[] getColumnInsertability();
 
 	public boolean isSimpleValue();
 
 	public boolean isLazy();
 
 	public void addEntityReferencingAttributeBinding(EntityReferencingAttributeBinding attributeBinding);
 
 	public Set<EntityReferencingAttributeBinding> getEntityReferencingAttributeBindings();
 
 	public void validate();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
index e74c78846c..c1a0c663c3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/EntityBinding.java
@@ -1,518 +1,513 @@
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
+import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.spi.FilterDefinition;
-import org.hibernate.metamodel.binder.view.EntityView;
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
 import org.hibernate.metamodel.domain.Attribute;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.JavaType;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.TableSpecification;
-import org.hibernate.metamodel.source.spi.BindingContext;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
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
 
 	private Class<? extends EntityPersister> customEntityPersisterClass;
 	private Class<? extends EntityTuplizer> customEntityTuplizerClass;
 
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
-	private int optimisticLockMode;
+	private OptimisticLockStyle optimisticLockStyle;
 
 	private Boolean isAbstract;
 
 	private String customLoaderName;
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
 	private Set<String> synchronizedTableNames = new HashSet<String>();
 
-	public EntityBinding initialize(BindingContext bindingContext, EntityView state) {
-		// todo : Entity will need both entityName and className to be effective
-		this.entity = new Entity(
-				state.getEntityName(),
-				state.getSuperType(),
-				bindingContext.makeJavaType( state.getClassName() )
-		);
-
-		this.isRoot = state.isRoot();
-		this.entityInheritanceType = state.getEntityInheritanceType();
-
-		this.entityMode = state.getEntityMode();
-		this.jpaEntityName = state.getJpaEntityName();
-
-		// todo : handle the entity-persister-resolver stuff
-		this.customEntityPersisterClass = state.getCustomEntityPersisterClass();
-		this.customEntityTuplizerClass = state.getCustomEntityTuplizerClass();
-
-		this.caching = state.getCaching();
-		this.metaAttributeContext = state.getMetaAttributeContext();
-
-		if ( entityMode == EntityMode.POJO ) {
-			if ( state.getProxyInterfaceName() != null ) {
-				this.proxyInterfaceType = bindingContext.makeJavaType( state.getProxyInterfaceName() );
-				this.lazy = true;
-			}
-			else if ( state.isLazy() ) {
-				this.proxyInterfaceType = entity.getJavaType();
-				this.lazy = true;
-			}
-		}
-		else {
-			this.proxyInterfaceType = new JavaType( Map.class );
-			this.lazy = state.isLazy();
-		}
-
-		this.mutable = state.isMutable();
-		this.explicitPolymorphism = state.isExplicitPolymorphism();
-		this.whereFilter = state.getWhereFilter();
-		this.rowId = state.getRowId();
-		this.dynamicUpdate = state.isDynamicUpdate();
-		this.dynamicInsert = state.isDynamicInsert();
-		this.batchSize = state.getBatchSize();
-		this.selectBeforeUpdate = state.isSelectBeforeUpdate();
-		this.optimisticLockMode = state.getOptimisticLockMode();
-		this.isAbstract = state.isAbstract();
-		this.customInsert = state.getCustomInsert();
-		this.customUpdate = state.getCustomUpdate();
-		this.customDelete = state.getCustomDelete();
-		if ( state.getSynchronizedTableNames() != null ) {
-			for ( String synchronizedTableName : state.getSynchronizedTableNames() ) {
-				addSynchronizedTable( synchronizedTableName );
-			}
-		}
-		return this;
-	}
+//	public EntityBinding initialize(BindingContext bindingContext, EntityDescriptor state) {
+//		// todo : Entity will need both entityName and className to be effective
+//		this.entity = new Entity( state.getEntityName(), state.getSuperType(), bindingContext.makeJavaType( state.getClassName() ) );
+//
+//		this.isRoot = state.isRoot();
+//		this.entityInheritanceType = state.getEntityInheritanceType();
+//
+//		this.entityMode = state.getEntityMode();
+//		this.jpaEntityName = state.getJpaEntityName();
+//
+//		// todo : handle the entity-persister-resolver stuff
+//		this.customEntityPersisterClass = state.getCustomEntityPersisterClass();
+//		this.customEntityTuplizerClass = state.getCustomEntityTuplizerClass();
+//
+//		this.caching = state.getCaching();
+//		this.metaAttributeContext = state.getMetaAttributeContext();
+//
+//		if ( entityMode == EntityMode.POJO ) {
+//			if ( state.getProxyInterfaceName() != null ) {
+//				this.proxyInterfaceType = bindingContext.makeJavaType( state.getProxyInterfaceName() );
+//				this.lazy = true;
+//			}
+//			else if ( state.isLazy() ) {
+//				this.proxyInterfaceType = entity.getJavaType();
+//				this.lazy = true;
+//			}
+//		}
+//		else {
+//			this.proxyInterfaceType = new JavaType( Map.class );
+//			this.lazy = state.isLazy();
+//		}
+//
+//		this.mutable = state.isMutable();
+//		this.explicitPolymorphism = state.isExplicitPolymorphism();
+//		this.whereFilter = state.getWhereFilter();
+//		this.rowId = state.getRowId();
+//		this.dynamicUpdate = state.isDynamicUpdate();
+//		this.dynamicInsert = state.isDynamicInsert();
+//		this.batchSize = state.getBatchSize();
+//		this.selectBeforeUpdate = state.isSelectBeforeUpdate();
+//		this.optimisticLockMode = state.getOptimisticLockMode();
+//		this.isAbstract = state.isAbstract();
+//		this.customInsert = state.getCustomInsert();
+//		this.customUpdate = state.getCustomUpdate();
+//		this.customDelete = state.getCustomDelete();
+//		if ( state.getSynchronizedTableNames() != null ) {
+//			for ( String synchronizedTableName : state.getSynchronizedTableNames() ) {
+//				addSynchronizedTable( synchronizedTableName );
+//			}
+//		}
+//		return this;
+//	}
 
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
 
 	public boolean isRoot() {
 		return isRoot;
 	}
 
 	public void setRoot(boolean isRoot) {
 		this.isRoot = isRoot;
 	}
 
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
 
 	public void setCaching(Caching caching) {
 		this.caching = caching;
 	}
 
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	public void setMetaAttributeContext(MetaAttributeContext metaAttributeContext) {
 		this.metaAttributeContext = metaAttributeContext;
 	}
 
 	public boolean isMutable() {
 		return mutable;
 	}
 
 	public void setMutable(boolean mutable) {
 		this.mutable = mutable;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public JavaType getProxyInterfaceType() {
 		return proxyInterfaceType;
 	}
 
 	public void setProxyInterfaceType(JavaType proxyInterfaceType) {
 		this.proxyInterfaceType = proxyInterfaceType;
 	}
 
 	public String getWhereFilter() {
 		return whereFilter;
 	}
 
 	public void setWhereFilter(String whereFilter) {
 		this.whereFilter = whereFilter;
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return explicitPolymorphism;
 	}
 
 	public void setExplicitPolymorphism(boolean explicitPolymorphism) {
 		this.explicitPolymorphism = explicitPolymorphism;
 	}
 
 	public String getRowId() {
 		return rowId;
 	}
 
 	public void setRowId(String rowId) {
 		this.rowId = rowId;
 	}
 
 	public String getDiscriminatorValue() {
 		return entityDiscriminator == null ? null : entityDiscriminator.getDiscriminatorValue();
 	}
 
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public void setDynamicUpdate(boolean dynamicUpdate) {
 		this.dynamicUpdate = dynamicUpdate;
 	}
 
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	public void setDynamicInsert(boolean dynamicInsert) {
 		this.dynamicInsert = dynamicInsert;
 	}
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public void setBatchSize(int batchSize) {
 		this.batchSize = batchSize;
 	}
 
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
 		this.selectBeforeUpdate = selectBeforeUpdate;
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	/* package-protected */
 	void setSubselectLoadableCollections(boolean hasSubselectLoadableCollections) {
 		this.hasSubselectLoadableCollections = hasSubselectLoadableCollections;
 	}
 
-	public int getOptimisticLockMode() {
-		return optimisticLockMode;
+	public OptimisticLockStyle getOptimisticLockStyle() {
+		return optimisticLockStyle;
 	}
 
-	public void setOptimisticLockMode(int optimisticLockMode) {
-		this.optimisticLockMode = optimisticLockMode;
+	public void setOptimisticLockStyle(OptimisticLockStyle optimisticLockStyle) {
+		this.optimisticLockStyle = optimisticLockStyle;
 	}
 
 	public Class<? extends EntityPersister> getCustomEntityPersisterClass() {
 		return customEntityPersisterClass;
 	}
 
 	public void setCustomEntityPersisterClass(Class<? extends EntityPersister> customEntityPersisterClass) {
 		this.customEntityPersisterClass = customEntityPersisterClass;
 	}
 
 	public Class<? extends EntityTuplizer> getCustomEntityTuplizerClass() {
 		return customEntityTuplizerClass;
 	}
 
 	public void setCustomEntityTuplizerClass(Class<? extends EntityTuplizer> customEntityTuplizerClass) {
 		this.customEntityTuplizerClass = customEntityTuplizerClass;
 	}
 
 	public Boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public void setAbstract(Boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	public Set<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
 
 	public void addSynchronizedTable(String tableName) {
 		synchronizedTableNames.add( tableName );
 	}
 
 	public void addSynchronizedTableNames(java.util.Collection<String> synchronizedTableNames) {
 		this.synchronizedTableNames.addAll( synchronizedTableNames );
 	}
 
 	public EntityMode getEntityMode() {
 		return entityMode;
 	}
 
 	public void setEntityMode(EntityMode entityMode) {
 		this.entityMode = entityMode;
 	}
 
 	public String getJpaEntityName() {
 		return jpaEntityName;
 	}
 
 	public void setJpaEntityName(String jpaEntityName) {
 		this.jpaEntityName = jpaEntityName;
 	}
 
 	public String getCustomLoaderName() {
 		return customLoaderName;
 	}
 
 	public void setCustomLoaderName(String customLoaderName) {
 		this.customLoaderName = customLoaderName;
 	}
 
 	public CustomSQL getCustomInsert() {
 		return customInsert;
 	}
 
 	public void setCustomInsert(CustomSQL customInsert) {
 		this.customInsert = customInsert;
 	}
 
 	public CustomSQL getCustomUpdate() {
 		return customUpdate;
 	}
 
 	public void setCustomUpdate(CustomSQL customUpdate) {
 		this.customUpdate = customUpdate;
 	}
 
 	public CustomSQL getCustomDelete() {
 		return customDelete;
 	}
 
 	public void setCustomDelete(CustomSQL customDelete) {
 		this.customDelete = customDelete;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/AttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/AttributeBindingState.java
index d1ab828718..37fd2da6ab 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/AttributeBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/AttributeBindingState.java
@@ -1,55 +1,55 @@
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
 package org.hibernate.metamodel.binding.state;
 
 import java.util.Map;
 import java.util.Set;
 
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
 import org.hibernate.metamodel.binding.CascadeType;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
 /**
  * @author Gail Badner
  */
 public interface AttributeBindingState {
 	String getAttributeName();
 
 	String getTypeName();
 
 	Map<String, String> getTypeParameters();
 
 	boolean isLazy();
 
 	String getPropertyAccessorName();
 
 	boolean isAlternateUniqueKey();
 
 	Set<CascadeType> getCascadeTypes();
 
 	boolean isOptimisticLockable();
 
 	String getNodeName();
 
 	public MetaAttributeContext getMetaAttributeContext();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationSourceProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationSourceProcessor.java
index c743ad417c..b40276a6c4 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationSourceProcessor.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/AnnotationSourceProcessor.java
@@ -1,194 +1,198 @@
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
 import org.hibernate.internal.util.Value;
 import org.hibernate.metamodel.MetadataSources;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
+import org.hibernate.metamodel.binder.source.SourceProcessor;
+import org.hibernate.metamodel.binder.source.internal.MetadataImpl;
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
+import org.hibernate.metamodel.binder.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.annotations.xml.PseudoJpaDotNames;
-import org.hibernate.metamodel.source.internal.JaxbRoot;
-import org.hibernate.metamodel.source.internal.MetadataImpl;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
-import org.hibernate.metamodel.source.spi.SourceProcessor;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * Main class responsible to creating and binding the Hibernate meta-model from annotations.
  * This binder only has to deal with the (jandex) annotation index/repository. XML configuration is already processed
  * and pseudo annotations are created.
  *
  * @author Hardy Ferentschik
  * @see org.hibernate.metamodel.source.annotations.xml.OrmXmlParser
  */
 public class AnnotationSourceProcessor implements SourceProcessor {
 	private static final Logger LOG = Logger.getLogger( AnnotationSourceProcessor.class );
 
 	private final MetadataImplementor metadata;
 	private final Value<ClassLoaderService> classLoaderService;
 
 	private Index index;
 
 	public AnnotationSourceProcessor(MetadataImpl metadata) {
 		this.metadata = metadata;
 		this.classLoaderService = new Value<ClassLoaderService>(
 				new Value.DeferredInitializer<ClassLoaderService>() {
 					@Override
 					public ClassLoaderService initialize() {
 						return AnnotationSourceProcessor.this.metadata.getServiceRegistry().getService( ClassLoaderService.class );
 					}
 				}
 		);
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
 			// todo : this needs to move to AnnotationBindingContext
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
 		InputStream stream = classLoaderService.getValue().locateResourceStream( className );
 		try {
 			indexer.index( stream );
 		}
 		catch ( IOException e ) {
 			throw new HibernateException( "Unable to open input stream for class " + className, e );
 		}
 	}
 
 	@Override
 	public void processIndependentMetadata(MetadataSources sources) {
         TypeDefBinder.bind( metadata, index );
 	}
 
 	@Override
 	public void processTypeDependentMetadata(MetadataSources sources) {
         IdGeneratorBinder.bind( metadata, index );
 	}
 
 	@Override
 	public void processMappingMetadata(MetadataSources sources, List<String> processedEntityNames) {
 		AnnotationBindingContext context = new AnnotationBindingContext( index, metadata.getServiceRegistry() );
 
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
 					LOG.debugf( "Binding entity from annotated class: %s", entityClass.getName() );
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
 
+	private Set<ConfiguredClassHierarchy> createEntityHierarchies() {
+		return ConfiguredClassHierarchyBuilder.createEntityHierarchies( index, metadata.getServiceRegistry() );
+	}
+
 	@Override
 	public void processMappingDependentMetadata(MetadataSources sources) {
 		TableBinder.bind( metadata, index );
 		FetchProfileBinder.bind( metadata, index );
 		QueryBinder.bind( metadata, index );
 		FilterDefBinder.bind( metadata, index );
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/state/binding/AttributeBindingStateImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/state/binding/AttributeBindingStateImpl.java
index e8da6d3e85..5b814f14a0 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/state/binding/AttributeBindingStateImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/state/binding/AttributeBindingStateImpl.java
@@ -1,125 +1,126 @@
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
 package org.hibernate.metamodel.source.annotations.attribute.state.binding;
 
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
 import org.hibernate.metamodel.binding.CascadeType;
 import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.source.annotations.attribute.SimpleAttribute;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
+
 
 /**
  * Implementation of the attribute binding state via annotation configuration.
  *
  * @author Hardy Ferentschik
  * @todo in the end we can maybe just let MappedAttribute implement SimpleAttributeBindingState. (HF)
  */
 public class AttributeBindingStateImpl implements SimpleAttributeBindingState {
 	private final SimpleAttribute mappedAttribute;
 
 	public AttributeBindingStateImpl(SimpleAttribute mappedAttribute) {
 		this.mappedAttribute = mappedAttribute;
 	}
 
 	@Override
 	public String getAttributeName() {
 		return mappedAttribute.getName();
 	}
 
 	@Override
 	public PropertyGeneration getPropertyGeneration() {
 		return mappedAttribute.getPropertyGeneration();
 	}
 
 	@Override
 	public boolean isInsertable() {
 		return mappedAttribute.isInsertable();
 	}
 
 	@Override
 	public boolean isUpdatable() {
 		return mappedAttribute.isUpdatable();
 	}
 
 	@Override
 	public String getTypeName() {
 		return mappedAttribute.getType();
 	}
 
 	@Override
 	public Map<String, String> getTypeParameters() {
 		return mappedAttribute.getTypeParameters();
 	}
 
 	@Override
 	public boolean isLazy() {
 		return mappedAttribute.isLazy();
 	}
 
 	@Override
 	public boolean isOptimisticLockable() {
 		return mappedAttribute.isOptimisticLockable();
 	}
 
 	@Override
 	public boolean isKeyCascadeDeleteEnabled() {
 		return false;
 	}
 
 	// TODO find out more about these methods. How are they relevant for a simple attribute
 	@Override
 	public String getUnsavedValue() {
 		return null;
 	}
 
 	@Override
 	public String getPropertyAccessorName() {
 		return null;
 	}
 
 	@Override
 	public boolean isAlternateUniqueKey() {
 		return false;
 	}
 
 	@Override
 	public Set<CascadeType> getCascadeTypes() {
 		return null;
 	}
 
 	@Override
 	public String getNodeName() {
 		return null;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return null;
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/state/relational/ColumnRelationalStateImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/state/relational/ColumnRelationalStateImpl.java
index 4e3c63e164..9ed2072bd8 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/state/relational/ColumnRelationalStateImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/attribute/state/relational/ColumnRelationalStateImpl.java
@@ -1,224 +1,224 @@
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
 package org.hibernate.metamodel.source.annotations.attribute.state.relational;
 
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Set;
 
 import org.jboss.jandex.AnnotationInstance;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.relational.state.ColumnRelationalState;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.attribute.ColumnValues;
 import org.hibernate.metamodel.source.annotations.attribute.SimpleAttribute;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
 
 /**
  * @author Hardy Ferentschik
  */
 public class ColumnRelationalStateImpl implements ColumnRelationalState {
 	private final NamingStrategy namingStrategy;
 	private final String columnName;
 	private final boolean unique;
 	private final boolean nullable;
     private final boolean globallyQuotedIdentifiers;
 	private final Size size;
 	private final String checkCondition;
 	private final String customWriteFragment;
 	private final String customReadFragment;
 	private final Set<String> indexes;
 
 	// todo - what about these annotations !?
 	private String defaultString;
 	private String sqlType;
 	private String comment;
 	private Set<String> uniqueKeys = new HashSet<String>();
 
 
 	public ColumnRelationalStateImpl(SimpleAttribute attribute, MetadataImplementor meta) {
 		ColumnValues columnValues = attribute.getColumnValues();
 		namingStrategy = meta.getOptions().getNamingStrategy();
         globallyQuotedIdentifiers = meta.isGloballyQuotedIdentifiers();
 		columnName = columnValues.getName().isEmpty() ? attribute.getName() : columnValues.getName();
 		unique = columnValues.isUnique();
 		nullable = columnValues.isNullable();
 		size = createSize( columnValues.getLength(), columnValues.getScale(), columnValues.getPrecision() );
 		checkCondition = parseCheckAnnotation( attribute );
 		indexes = parseIndexAnnotation( attribute );
 
 		String[] readWrite;
 		List<AnnotationInstance> columnTransformerAnnotations = getAllColumnTransformerAnnotations( attribute );
 		readWrite = createCustomReadWrite( columnTransformerAnnotations );
 		customReadFragment = readWrite[0];
 		customWriteFragment = readWrite[1];
 	}
 
 	@Override
 	public NamingStrategy getNamingStrategy() {
 		return namingStrategy;
 	}
 
     @Override
     public boolean isGloballyQuotedIdentifiers() {
         return globallyQuotedIdentifiers;
     }
 
     @Override
 	public String getExplicitColumnName() {
 		return columnName;
 	}
 
 	@Override
 	public boolean isUnique() {
 		return unique;
 	}
 
 	@Override
 	public Size getSize() {
 		return size;
 	}
 
 	@Override
 	public boolean isNullable() {
 		return nullable;
 	}
 
 	@Override
 	public String getCheckCondition() {
 		return checkCondition;
 	}
 
 	@Override
 	public String getDefault() {
 		return defaultString;
 	}
 
 	@Override
 	public String getSqlType() {
 		return sqlType;
 	}
 
 	@Override
 	public String getCustomWriteFragment() {
 		return customWriteFragment;
 	}
 
 	@Override
 	public String getCustomReadFragment() {
 		return customReadFragment;
 	}
 
 	@Override
 	public String getComment() {
 		return comment;
 	}
 
 	@Override
 	public Set<String> getUniqueKeys() {
 		return uniqueKeys;
 	}
 
 	@Override
 	public Set<String> getIndexes() {
 		return indexes;
 	}
 
 	private Size createSize(int length, int scale, int precision) {
 		Size size = new Size();
 		size.setLength( length );
 		size.setScale( scale );
 		size.setPrecision( precision );
 		return size;
 	}
 
 	private List<AnnotationInstance> getAllColumnTransformerAnnotations(SimpleAttribute attribute) {
 		List<AnnotationInstance> allColumnTransformerAnnotations = new ArrayList<AnnotationInstance>();
 
 		// not quite sure about the usefulness of @ColumnTransformers (HF)
 		AnnotationInstance columnTransformersAnnotations = attribute.getIfExists( HibernateDotNames.COLUMN_TRANSFORMERS );
 		if ( columnTransformersAnnotations != null ) {
 			AnnotationInstance[] annotationInstances = allColumnTransformerAnnotations.get( 0 ).value().asNestedArray();
 			allColumnTransformerAnnotations.addAll( Arrays.asList( annotationInstances ) );
 		}
 
 		AnnotationInstance columnTransformerAnnotation = attribute.getIfExists( HibernateDotNames.COLUMN_TRANSFORMER );
 		if ( columnTransformerAnnotation != null ) {
 			allColumnTransformerAnnotations.add( columnTransformerAnnotation );
 		}
 		return allColumnTransformerAnnotations;
 	}
 
 	private String[] createCustomReadWrite(List<AnnotationInstance> columnTransformerAnnotations) {
 		String[] readWrite = new String[2];
 
 		boolean alreadyProcessedForColumn = false;
 		for ( AnnotationInstance annotationInstance : columnTransformerAnnotations ) {
 			String forColumn = annotationInstance.value( "forColumn" ) == null ?
 					null : annotationInstance.value( "forColumn" ).asString();
 
 			if ( forColumn != null && !forColumn.equals( columnName ) ) {
 				continue;
 			}
 
 			if ( alreadyProcessedForColumn ) {
 				throw new AnnotationException( "Multiple definition of read/write conditions for column " + columnName );
 			}
 
 			readWrite[0] = annotationInstance.value( "read" ) == null ?
 					null : annotationInstance.value( "read" ).asString();
 			readWrite[1] = annotationInstance.value( "write" ) == null ?
 					null : annotationInstance.value( "write" ).asString();
 
 			alreadyProcessedForColumn = true;
 		}
 		return readWrite;
 	}
 
 	private String parseCheckAnnotation(SimpleAttribute attribute) {
 		String checkCondition = null;
 		AnnotationInstance checkAnnotation = attribute.getIfExists( HibernateDotNames.CHECK );
 		if ( checkAnnotation != null ) {
 			checkCondition = checkAnnotation.value( "constraints" ).toString();
 		}
 		return checkCondition;
 	}
 
 	private Set<String> parseIndexAnnotation(SimpleAttribute attribute) {
 		Set<String> indexNames = new HashSet<String>();
 		AnnotationInstance indexAnnotation = attribute.getIfExists( HibernateDotNames.INDEX );
 		if ( indexAnnotation != null ) {
 			String indexName = indexAnnotation.value( "name" ).toString();
 			indexNames.add( indexName );
 		}
 		return indexNames;
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
index 2282c76c3f..ac3a5984e7 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/EntityBinder.java
@@ -1,841 +1,841 @@
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
 
 import javax.persistence.GenerationType;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.List;
 import java.util.Map;
 
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
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
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
-import org.hibernate.metamodel.source.annotations.entity.state.binding.EntityViewImpl;
+import org.hibernate.metamodel.source.annotations.entity.state.binding.AbstractEntityDescriptorImpl;
 import org.hibernate.metamodel.source.annotations.global.IdGeneratorBinder;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
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
-		EntityViewImpl entityBindingState = new EntityViewImpl( superType, entityClass );
+		AbstractEntityDescriptorImpl entityBindingState = new AbstractEntityDescriptorImpl( getSuperType(), entityClass );
 
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
 
-	private void bindWhereFilter(EntityViewImpl entityBindingState) {
+	private void bindWhereFilter(AbstractEntityDescriptorImpl entityBindingState) {
 		AnnotationInstance whereAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.WHERE
 		);
 		if ( whereAnnotation != null ) {
 			// no null check needed, it is a required attribute
 			entityBindingState.setWhereFilter( JandexHelper.getValue( whereAnnotation, "clause", String.class ) );
 		}
 	}
 
-	private void bindHibernateCaching(EntityViewImpl entityBindingState) {
+	private void bindHibernateCaching(AbstractEntityDescriptorImpl entityBindingState) {
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
-	private void bindJpaCaching(EntityViewImpl entityBindingState) {
+	private void bindJpaCaching(AbstractEntityDescriptorImpl entityBindingState) {
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
 
-	private void bindProxy(EntityViewImpl entityBindingState) {
+	private void bindProxy(AbstractEntityDescriptorImpl entityBindingState) {
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
 
-	private void bindSynchronize(EntityViewImpl entityBindingState) {
+	private void bindSynchronize(AbstractEntityDescriptorImpl entityBindingState) {
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
 
-	private void bindCustomSQL(EntityViewImpl entityBindingState) {
+	private void bindCustomSQL(AbstractEntityDescriptorImpl entityBindingState) {
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
 
-	private void bindRowId(EntityViewImpl entityBindingState) {
+	private void bindRowId(AbstractEntityDescriptorImpl entityBindingState) {
 		AnnotationInstance rowIdAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.ROW_ID
 		);
 
 		if ( rowIdAnnotation != null ) {
 			entityBindingState.setRowId( rowIdAnnotation.value().asString() );
 		}
 	}
 
-	private void bindBatchSize(EntityViewImpl entityBindingState) {
+	private void bindBatchSize(AbstractEntityDescriptorImpl entityBindingState) {
 		AnnotationInstance batchSizeAnnotation = JandexHelper.getSingleAnnotation(
 				entityClass.getClassInfo(), HibernateDotNames.BATCH_SIZE
 		);
 
 		if ( batchSizeAnnotation != null ) {
 			entityBindingState.setBatchSize( batchSizeAnnotation.value( "size" ).asInt() );
 		}
 	}
 
-	private Caching createCachingForCacheableAnnotation(EntityViewImpl entityBindingState) {
+	private Caching createCachingForCacheableAnnotation(AbstractEntityDescriptorImpl entityBindingState) {
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
 		return schema.locateOrCreateTable( tableNameIdentifier );
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
 
 
-	private void bindJpaEntityAnnotation(EntityViewImpl entityBindingState) {
+	private void bindJpaEntityAnnotation(AbstractEntityDescriptorImpl entityBindingState) {
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
 
-	private void bindHibernateEntityAnnotation(EntityViewImpl entityBindingState) {
+	private void bindHibernateEntityAnnotation(AbstractEntityDescriptorImpl entityBindingState) {
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityViewImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/AbstractEntityDescriptorImpl.java
similarity index 61%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityViewImpl.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/AbstractEntityDescriptorImpl.java
index 0b383bc376..08e8544061 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/EntityViewImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/AbstractEntityDescriptorImpl.java
@@ -1,309 +1,259 @@
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
 
-import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
-import org.hibernate.annotations.OptimisticLockType;
-import org.hibernate.engine.internal.Versioning;
-import org.hibernate.metamodel.binding.Caching;
+import org.hibernate.metamodel.binder.Origin;
+import org.hibernate.metamodel.binder.source.BindingContext;
+import org.hibernate.metamodel.binder.source.EntityDescriptor;
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
+import org.hibernate.metamodel.binder.source.UnifiedDescriptorObject;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
-import org.hibernate.metamodel.binder.view.EntityView;
-import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.source.annotations.entity.EntityClass;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.tuple.entity.EntityTuplizer;
 
 /**
  * @author Hardy Ferentschik
  */
-public class EntityViewImpl implements EntityView {
-	private String entityName;
+public abstract class AbstractEntityDescriptorImpl implements EntityDescriptor {
+	private final BindingContext bindingContext;
 
 	private final String className;
-	private String proxyInterfaceName;
-
-	private final Hierarchical superType;
-	private final boolean isRoot;
+	private final String superEntityName;
 	private final InheritanceType inheritanceType;
 
+	private String jpaEntityName;
 
-	private Caching caching;
+	private boolean lazy;
+	private String proxyInterfaceName;
 
-	private boolean mutable;
-	private boolean explicitPolymorphism;
-	private String whereFilter;
-	private String rowId;
+	private Class<? extends EntityPersister> persisterClass;
+	private Class<? extends EntityTuplizer> tuplizerClass;
 
 	private boolean dynamicUpdate;
 	private boolean dynamicInsert;
 
-	private int batchSize;
+	private int batchSize = -1;
 	private boolean selectBeforeUpdate;
-	private OptimisticLockType optimisticLock;
-
-	private Class<EntityPersister> persisterClass;
-
-	private boolean lazy;
 
+	private String customLoaderName;
 	private CustomSQL customInsert;
 	private CustomSQL customUpdate;
 	private CustomSQL customDelete;
 
-	private Set<String> synchronizedTableNames;
+	private Set<String> synchronizedTableNames = new HashSet<String>();
+
+	public AbstractEntityDescriptorImpl(
+			EntityClass entityClass,
+			String superEntityName,
+			BindingContext bindingContext) {
+		this.bindingContext = bindingContext;
 
-	public EntityViewImpl(Hierarchical superType, EntityClass entityClass) {
 		this.className = entityClass.getName();
-		this.superType = superType;
-		this.isRoot = entityClass.isEntityRoot();
+		this.superEntityName = superEntityName;
 		this.inheritanceType = entityClass.getInheritanceType();
-		this.synchronizedTableNames = new HashSet<String>();
-		this.batchSize = -1;
 	}
 
 	@Override
-	public String getJpaEntityName() {
-		return entityName;
-	}
-
-	public void setJpaEntityName(String entityName) {
-		this.entityName = entityName;
-	}
-
-	@Override
-	public EntityMode getEntityMode() {
-		return EntityMode.POJO;
-	}
-
 	public String getEntityName() {
 		return className;
 	}
 
 	@Override
 	public String getClassName() {
 		return className;
 	}
 
 	@Override
-	public Class<EntityTuplizer> getCustomEntityTuplizerClass() {
-		return null; // todo : implement method body
-	}
-
-	@Override
-	public Hierarchical getSuperType() {
-		return superType;
-	}
-
-	public void setCaching(Caching caching) {
-		this.caching = caching;
-	}
-
-	public void setMutable(boolean mutable) {
-		this.mutable = mutable;
-	}
-
-	public void setExplicitPolymorphism(boolean explicitPolymorphism) {
-		this.explicitPolymorphism = explicitPolymorphism;
-	}
-
-	public void setWhereFilter(String whereFilter) {
-		this.whereFilter = whereFilter;
-	}
-
-	public void setDynamicUpdate(boolean dynamicUpdate) {
-		this.dynamicUpdate = dynamicUpdate;
-	}
-
-	public void setDynamicInsert(boolean dynamicInsert) {
-		this.dynamicInsert = dynamicInsert;
-	}
-
-	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
-		this.selectBeforeUpdate = selectBeforeUpdate;
-	}
-
-	public void setOptimisticLock(OptimisticLockType optimisticLock) {
-		this.optimisticLock = optimisticLock;
-	}
-
-	public void setPersisterClass(Class<EntityPersister> persisterClass) {
-		this.persisterClass = persisterClass;
-	}
-
-	public void setLazy(boolean lazy) {
-		this.lazy = lazy;
-	}
-
-	public void setProxyInterfaceName(String proxyInterfaceName) {
-		this.proxyInterfaceName = proxyInterfaceName;
-	}
-
-	public void setRowId(String rowId) {
-		this.rowId = rowId;
-	}
-
-	public void setBatchSize(int batchSize) {
-		this.batchSize = batchSize;
-	}
-
-	public void addSynchronizedTableName(String tableName) {
-		synchronizedTableNames.add( tableName );
-	}
-
-	public void setCustomInsert(CustomSQL customInsert) {
-		this.customInsert = customInsert;
+	public String getJpaEntityName() {
+		return jpaEntityName;
 	}
 
-	public void setCustomUpdate(CustomSQL customUpdate) {
-		this.customUpdate = customUpdate;
+	public void setJpaEntityName(String entityName) {
+		this.jpaEntityName = entityName;
 	}
 
-	public void setCustomDelete(CustomSQL customDelete) {
-		this.customDelete = customDelete;
+	@Override
+	public EntityMode getEntityMode() {
+		return EntityMode.POJO;
 	}
 
 	@Override
-	public boolean isRoot() {
-		return isRoot;
-
+	public String getSuperEntityName() {
+		return superEntityName;
 	}
 
 	@Override
 	public InheritanceType getEntityInheritanceType() {
 		return inheritanceType;
 	}
 
 	@Override
-	public Caching getCaching() {
-		return caching;
+	public Boolean isAbstract() {
+		// no annotations equivalent
+		return Boolean.FALSE;
 	}
 
 	@Override
-	public MetaAttributeContext getMetaAttributeContext() {
-		// not needed for annotations!? (HF)
-		return null;
+	public boolean isLazy() {
+		return lazy;
+	}
+
+	public void setLazy(boolean lazy) {
+		this.lazy = lazy;
 	}
 
 	@Override
 	public String getProxyInterfaceName() {
 		return proxyInterfaceName;
 	}
 
-	@Override
-	public boolean isLazy() {
-		return lazy;
+	public void setProxyInterfaceName(String proxyInterfaceName) {
+		this.proxyInterfaceName = proxyInterfaceName;
 	}
 
 	@Override
-	public boolean isMutable() {
-		return mutable;
+	public Class<? extends EntityPersister> getCustomEntityPersisterClass() {
+		return persisterClass;
 	}
 
-	@Override
-	public boolean isExplicitPolymorphism() {
-		return explicitPolymorphism;
+	public void setPersisterClass(Class<? extends EntityPersister> persisterClass) {
+		this.persisterClass = persisterClass;
 	}
 
 	@Override
-	public String getWhereFilter() {
-		return whereFilter;
+	public Class<? extends EntityTuplizer> getCustomEntityTuplizerClass() {
+		return tuplizerClass;
 	}
 
-	@Override
-	public String getRowId() {
-		return rowId;
+	public void setTuplizerClass(Class<? extends EntityTuplizer> tuplizerClass) {
+		this.tuplizerClass = tuplizerClass;
 	}
 
 	@Override
 	public boolean isDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
+	public void setDynamicUpdate(boolean dynamicUpdate) {
+		this.dynamicUpdate = dynamicUpdate;
+	}
+
 	@Override
 	public boolean isDynamicInsert() {
 		return dynamicInsert;
 	}
 
+	public void setDynamicInsert(boolean dynamicInsert) {
+		this.dynamicInsert = dynamicInsert;
+	}
+
 	@Override
 	public int getBatchSize() {
 		return batchSize;
 	}
 
+	public void setBatchSize(int batchSize) {
+		this.batchSize = batchSize;
+	}
+
 	@Override
 	public boolean isSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
-	@Override
-	public int getOptimisticLockMode() {
-        switch ( optimisticLock ){
-            case ALL:
-                return Versioning.OPTIMISTIC_LOCK_ALL;
-            case NONE:
-                return Versioning.OPTIMISTIC_LOCK_NONE;
-            case DIRTY:
-                return Versioning.OPTIMISTIC_LOCK_DIRTY;
-            case VERSION:
-                return Versioning.OPTIMISTIC_LOCK_VERSION;
-            default:
-                throw new AssertionFailure( "Unexpected optimistic lock type: " + optimisticLock );
-        }
+	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
+		this.selectBeforeUpdate = selectBeforeUpdate;
 	}
 
 	@Override
-	public Class<EntityPersister> getCustomEntityPersisterClass() {
-		return persisterClass;
+	public String getCustomLoaderName() {
+		return customLoaderName;
 	}
 
-	@Override
-	public Boolean isAbstract() {
-		// no annotations equivalent
-		return false;
+	public void setCustomLoaderName(String customLoaderName) {
+		this.customLoaderName = customLoaderName;
 	}
 
 	@Override
 	public CustomSQL getCustomInsert() {
 		return customInsert;
 	}
 
+	public void setCustomInsert(CustomSQL customInsert) {
+		this.customInsert = customInsert;
+	}
+
 	@Override
 	public CustomSQL getCustomUpdate() {
 		return customUpdate;
 	}
 
+	public void setCustomUpdate(CustomSQL customUpdate) {
+		this.customUpdate = customUpdate;
+	}
+
 	@Override
 	public CustomSQL getCustomDelete() {
 		return customDelete;
 	}
 
+	public void setCustomDelete(CustomSQL customDelete) {
+		this.customDelete = customDelete;
+	}
+
 	@Override
 	public Set<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
+
+	public void addSynchronizedTableName(String tableName) {
+		synchronizedTableNames.add( tableName );
+	}
+
+	@Override
+	public MetaAttributeContext getMetaAttributeContext() {
+		// not needed for annotations!? (HF)
+		// probably not; this is a tools/generation thing (SE)
+		return null;
+	}
+
+	@Override
+	public Origin getOrigin() {
+		// (steve) - not sure how to best handle this.  Origin should essentially name the class file from which
+		// this information came
+		return null;
+	}
+
+	@Override
+	public UnifiedDescriptorObject getContainingDescriptor() {
+		// probably makes most sense as none for annotations.
+		return this;
+	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/RootEntityDescriptorImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/RootEntityDescriptorImpl.java
new file mode 100644
index 0000000000..83bb8ec234
--- /dev/null
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/entity/state/binding/RootEntityDescriptorImpl.java
@@ -0,0 +1,141 @@
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
+package org.hibernate.metamodel.source.annotations.entity.state.binding;
+
+import org.hibernate.annotations.OptimisticLockType;
+import org.hibernate.engine.OptimisticLockStyle;
+import org.hibernate.metamodel.binder.source.BindingContext;
+import org.hibernate.metamodel.binder.source.RootEntityDescriptor;
+import org.hibernate.metamodel.binder.source.TableDescriptor;
+import org.hibernate.metamodel.binding.Caching;
+import org.hibernate.metamodel.binding.InheritanceType;
+import org.hibernate.metamodel.source.annotations.entity.ConfiguredClass;
+
+/**
+ * @author Steve Ebersole
+ * @author Hardy Ferentschik
+ */
+public class RootEntityDescriptorImpl
+		extends AbstractEntityDescriptorImpl
+		implements RootEntityDescriptor {
+
+	private boolean mutable;
+	private boolean explicitPolymorphism;
+	private String whereFilter;
+	private String rowId;
+	private OptimisticLockStyle optimisticLockStyle;
+
+	private Caching caching;
+
+	private TableDescriptor baseTableDescriptor;
+
+	public RootEntityDescriptorImpl(
+			ConfiguredClass configuredClass,
+			String superEntityName,
+			BindingContext bindingContext) {
+		super( configuredClass, superEntityName, bindingContext );
+		if ( configuredClass.getInheritanceType() != InheritanceType.NO_INHERITANCE ) {
+			// throw exception?
+		}
+	}
+
+	@Override
+	public boolean isMutable() {
+		return mutable;
+	}
+
+	public void setMutable(boolean mutable) {
+		this.mutable = mutable;
+	}
+
+	@Override
+	public boolean isExplicitPolymorphism() {
+		return explicitPolymorphism;
+	}
+
+	public void setExplicitPolymorphism(boolean explicitPolymorphism) {
+		this.explicitPolymorphism = explicitPolymorphism;
+	}
+
+	@Override
+	public String getWhereFilter() {
+		return whereFilter;
+	}
+
+	public void setWhereFilter(String whereFilter) {
+		this.whereFilter = whereFilter;
+	}
+
+	@Override
+	public String getRowId() {
+		return rowId;
+	}
+
+	public void setRowId(String rowId) {
+		this.rowId = rowId;
+	}
+
+	@Override
+	public OptimisticLockStyle getOptimisticLockStyle() {
+		return optimisticLockStyle;
+	}
+
+	public void setOptimisticLockType(OptimisticLockType optimisticLockType) {
+		switch ( optimisticLockType ) {
+			case NONE: {
+				this.optimisticLockStyle = OptimisticLockStyle.NONE;
+				break;
+			}
+			case DIRTY: {
+				this.optimisticLockStyle = OptimisticLockStyle.DIRTY;
+				break;
+			}
+			case ALL: {
+				this.optimisticLockStyle = OptimisticLockStyle.ALL;
+				break;
+			}
+			default: {
+				this.optimisticLockStyle = OptimisticLockStyle.VERSION;
+			}
+		}
+	}
+
+	@Override
+	public Caching getCaching() {
+		return caching;
+	}
+
+	public void setCaching(Caching caching) {
+		this.caching = caching;
+	}
+
+	@Override
+	public TableDescriptor getBaseTable() {
+		return baseTableDescriptor;
+	}
+
+	public void setBaseTableDescriptor(TableDescriptor baseTableDescriptor) {
+		this.baseTableDescriptor = baseTableDescriptor;
+	}
+}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinder.java
index c3fac4de9a..96bd4ddd5c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinder.java
@@ -1,93 +1,93 @@
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
 
 import java.util.HashSet;
 import java.util.Set;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.Index;
 
 import org.hibernate.MappingException;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.FetchProfiles;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.FetchProfile;
 import org.hibernate.metamodel.binding.FetchProfile.Fetch;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
 
 /**
  * Binds fetch profiles found in annotations.
  *
  * @author Hardy Ferentschik
  */
 public class FetchProfileBinder {
 
 	/**
 	 * Binds all {@link FetchProfiles} and {@link org.hibernate.annotations.FetchProfile} annotations to the supplied metadata.
 	 *
 	 * @param metadata the global metadata
 	 * @param jandex the jandex index
 	 */
 	// TODO verify that association exists. See former VerifyFetchProfileReferenceSecondPass
 	public static void bind(MetadataImplementor metadata, Index jandex) {
 		for ( AnnotationInstance fetchProfile : jandex.getAnnotations( HibernateDotNames.FETCH_PROFILE ) ) {
 			bind( metadata, fetchProfile );
 		}
 		for ( AnnotationInstance fetchProfiles : jandex.getAnnotations( HibernateDotNames.FETCH_PROFILES ) ) {
 			AnnotationInstance[] fetchProfileAnnotations = JandexHelper.getValue(
 					fetchProfiles,
 					"value",
 					AnnotationInstance[].class
 			);
 			for ( AnnotationInstance fetchProfile : fetchProfileAnnotations ) {
 				bind( metadata, fetchProfile );
 			}
 		}
 	}
 
 	private static void bind(MetadataImplementor metadata, AnnotationInstance fetchProfile) {
 		String name = JandexHelper.getValue( fetchProfile, "name", String.class );
 		Set<Fetch> fetches = new HashSet<Fetch>();
 		AnnotationInstance[] overrideAnnotations = JandexHelper.getValue(
 				fetchProfile,
 				"fetchOverrides",
 				AnnotationInstance[].class
 		);
 		for ( AnnotationInstance override : overrideAnnotations ) {
 			FetchMode fetchMode = JandexHelper.getValueAsEnum( override, "mode", FetchMode.class );
 			if ( !fetchMode.equals( org.hibernate.annotations.FetchMode.JOIN ) ) {
 				throw new MappingException( "Only FetchMode.JOIN is currently supported" );
 			}
 			String entityName = JandexHelper.getValue( override, "entity", String.class );
 			String associationName = JandexHelper.getValue( override, "association", String.class );
 			fetches.add( new Fetch( entityName, associationName, fetchMode.toString().toLowerCase() ) );
 		}
 		metadata.addFetchProfile( new FetchProfile( name, fetches ) );
 	}
 
 	private FetchProfileBinder() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FilterDefBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FilterDefBinder.java
index 48482a77f7..5993c5f05b 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FilterDefBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/FilterDefBinder.java
@@ -1,92 +1,92 @@
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
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.Index;
 import org.jboss.logging.Logger;
 
 import org.hibernate.annotations.FilterDef;
 import org.hibernate.annotations.FilterDefs;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.CoreMessageLogger;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.type.Type;
 
 public class FilterDefBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			FilterDefBinder.class.getName()
 	);
 
 	/**
 	 * Binds all {@link FilterDefs} and {@link FilterDef} annotations to the supplied metadata.
 	 *
 	 * @param metadata the global metadata
 	 * @param jandex the jandex index
 	 */
 	public static void bind(MetadataImplementor metadata, Index jandex) {
 		for ( AnnotationInstance filterDef : jandex.getAnnotations( HibernateDotNames.FILTER_DEF ) ) {
 			bind( metadata, filterDef );
 		}
 		for ( AnnotationInstance filterDefs : jandex.getAnnotations( HibernateDotNames.FILTER_DEFS ) ) {
 			AnnotationInstance[] filterDefAnnotations = JandexHelper.getValue(
 					filterDefs,
 					"value",
 					AnnotationInstance[].class
 			);
 			for ( AnnotationInstance filterDef : filterDefAnnotations ) {
 				bind( metadata, filterDef );
 			}
 		}
 	}
 
 	private static void bind(MetadataImplementor metadata, AnnotationInstance filterDef) {
 		String name = JandexHelper.getValue( filterDef, "name", String.class );
 		Map<String, Type> prms = new HashMap<String, Type>();
 		for ( AnnotationInstance prm : JandexHelper.getValue( filterDef, "parameters", AnnotationInstance[].class ) ) {
 			prms.put(
 					JandexHelper.getValue( prm, "name", String.class ),
 					metadata.getTypeResolver().heuristicType( JandexHelper.getValue( prm, "type", String.class ) )
 			);
 		}
 		metadata.addFilterDefinition(
 				new FilterDefinition(
 						name,
 						JandexHelper.getValue( filterDef, "defaultCondition", String.class ),
 						prms
 				)
 		);
 		LOG.debugf( "Binding filter definition: %s", name );
 	}
 
 	private FilterDefBinder() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/IdGeneratorBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/IdGeneratorBinder.java
index d7a202dd1e..9e941d8843 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/IdGeneratorBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/IdGeneratorBinder.java
@@ -1,216 +1,216 @@
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
 
 import java.util.HashMap;
 import java.util.Map;
 import javax.persistence.GenerationType;
 import javax.persistence.SequenceGenerator;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.Index;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.annotations.GenericGenerator;
 import org.hibernate.annotations.GenericGenerators;
 import org.hibernate.cfg.AvailableSettings;
 import org.hibernate.id.MultipleHiLoPerTableGenerator;
 import org.hibernate.id.PersistentIdentifierGenerator;
 import org.hibernate.id.SequenceHiLoGenerator;
 import org.hibernate.id.TableHiLoGenerator;
 import org.hibernate.id.enhanced.SequenceStyleGenerator;
 import org.hibernate.id.enhanced.TableGenerator;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.IdGenerator;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
 
 public class IdGeneratorBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			IdGeneratorBinder.class.getName()
 	);
 
 	private IdGeneratorBinder() {
 	}
 
 	private static void addStringParameter(AnnotationInstance annotation,
 										   String element,
 										   Map<String, String> parameters,
 										   String parameter) {
 		String string = JandexHelper.getValue( annotation, element, String.class );
 		if ( StringHelper.isNotEmpty( string ) ) {
 			parameters.put( parameter, string );
 		}
 	}
 
 	/**
 	 * Binds all {@link SequenceGenerator}, {@link javax.persistence.TableGenerator}, {@link GenericGenerator}, and {
 	 * {@link GenericGenerators} annotations to the supplied metadata.
 	 *
 	 * @param metadata the global metadata
 	 * @param jandex the jandex index
 	 */
 	public static void bind(MetadataImplementor metadata, Index jandex) {
 		for ( AnnotationInstance generator : jandex.getAnnotations( JPADotNames.SEQUENCE_GENERATOR ) ) {
 			bindSequenceGenerator( metadata, generator );
 		}
 		for ( AnnotationInstance generator : jandex.getAnnotations( JPADotNames.TABLE_GENERATOR ) ) {
 			bindTableGenerator( metadata, generator );
 		}
 		for ( AnnotationInstance generator : jandex.getAnnotations( HibernateDotNames.GENERIC_GENERATOR ) ) {
 			bindGenericGenerator( metadata, generator );
 		}
 		for ( AnnotationInstance generators : jandex.getAnnotations( HibernateDotNames.GENERIC_GENERATORS ) ) {
 			for ( AnnotationInstance generator : JandexHelper.getValue(
 					generators,
 					"value",
 					AnnotationInstance[].class
 			) ) {
 				bindGenericGenerator( metadata, generator );
 			}
 		}
 	}
 
 	private static void bindGenericGenerator(MetadataImplementor metadata, AnnotationInstance generator) {
 		String name = JandexHelper.getValue( generator, "name", String.class );
 		Map<String, String> parameterMap = new HashMap<String, String>();
 		AnnotationInstance[] parameterAnnotations = JandexHelper.getValue(
 				generator,
 				"parameters",
 				AnnotationInstance[].class
 		);
 		for ( AnnotationInstance parameterAnnotation : parameterAnnotations ) {
 			parameterMap.put(
 					JandexHelper.getValue( parameterAnnotation, "name", String.class ),
 					JandexHelper.getValue( parameterAnnotation, "value", String.class )
 			);
 		}
 		metadata.addIdGenerator(
 				new IdGenerator(
 						name,
 						JandexHelper.getValue( generator, "strategy", String.class ),
 						parameterMap
 				)
 		);
 		LOG.tracef( "Add generic generator with name: %s", name );
 	}
 
 	private static void bindSequenceGenerator(MetadataImplementor metadata, AnnotationInstance generator) {
 		String name = JandexHelper.getValue( generator, "name", String.class );
 		String strategy;
 		Map<String, String> prms = new HashMap<String, String>();
 		addStringParameter( generator, "sequenceName", prms, SequenceStyleGenerator.SEQUENCE_PARAM );
 		boolean useNewIdentifierGenerators = metadata.getOptions().useNewIdentifierGenerators();
 		strategy = generatorType( GenerationType.SEQUENCE, useNewIdentifierGenerators );
 		if ( useNewIdentifierGenerators ) {
 			addStringParameter( generator, "catalog", prms, PersistentIdentifierGenerator.CATALOG );
 			addStringParameter( generator, "schema", prms, PersistentIdentifierGenerator.SCHEMA );
 			prms.put(
 					SequenceStyleGenerator.INCREMENT_PARAM,
 					String.valueOf( JandexHelper.getValue( generator, "allocationSize", Integer.class ) )
 			);
 			prms.put(
 					SequenceStyleGenerator.INITIAL_PARAM,
 					String.valueOf( JandexHelper.getValue( generator, "initialValue", Integer.class ) )
 			);
 		}
 		else {
 			if ( JandexHelper.getValue( generator, "initialValue", Integer.class ) != 1 ) {
 				LOG.unsupportedInitialValue( AvailableSettings.USE_NEW_ID_GENERATOR_MAPPINGS );
 			}
 			prms.put(
 					SequenceHiLoGenerator.MAX_LO,
 					String.valueOf( JandexHelper.getValue( generator, "allocationSize", Integer.class ) - 1 )
 			);
 		}
 		metadata.addIdGenerator( new IdGenerator( name, strategy, prms ) );
 		LOG.tracef( "Add sequence generator with name: %s", name );
 	}
 
 	private static void bindTableGenerator(MetadataImplementor metadata, AnnotationInstance generator) {
 		String name = JandexHelper.getValue( generator, "name", String.class );
 		String strategy;
 		Map<String, String> prms = new HashMap<String, String>();
 		addStringParameter( generator, "catalog", prms, PersistentIdentifierGenerator.CATALOG );
 		addStringParameter( generator, "schema", prms, PersistentIdentifierGenerator.SCHEMA );
 		boolean useNewIdentifierGenerators = metadata.getOptions().useNewIdentifierGenerators();
 		strategy = generatorType( GenerationType.TABLE, useNewIdentifierGenerators );
 		if ( useNewIdentifierGenerators ) {
 			prms.put( TableGenerator.CONFIG_PREFER_SEGMENT_PER_ENTITY, "true" );
 			addStringParameter( generator, "table", prms, TableGenerator.TABLE_PARAM );
 			addStringParameter( generator, "pkColumnName", prms, TableGenerator.SEGMENT_COLUMN_PARAM );
 			addStringParameter( generator, "pkColumnValue", prms, TableGenerator.SEGMENT_VALUE_PARAM );
 			addStringParameter( generator, "valueColumnName", prms, TableGenerator.VALUE_COLUMN_PARAM );
 			prms.put(
 					TableGenerator.INCREMENT_PARAM,
 					String.valueOf( JandexHelper.getValue( generator, "allocationSize", String.class ) )
 			);
 			prms.put(
 					TableGenerator.INITIAL_PARAM,
 					String.valueOf( JandexHelper.getValue( generator, "initialValue", String.class ) + 1 )
 			);
 		}
 		else {
 			addStringParameter( generator, "table", prms, MultipleHiLoPerTableGenerator.ID_TABLE );
 			addStringParameter( generator, "pkColumnName", prms, MultipleHiLoPerTableGenerator.PK_COLUMN_NAME );
 			addStringParameter( generator, "pkColumnValue", prms, MultipleHiLoPerTableGenerator.PK_VALUE_NAME );
 			addStringParameter( generator, "valueColumnName", prms, MultipleHiLoPerTableGenerator.VALUE_COLUMN_NAME );
 			prms.put(
 					TableHiLoGenerator.MAX_LO,
 					String.valueOf( JandexHelper.getValue( generator, "allocationSize", Integer.class ) - 1 )
 			);
 		}
 		if ( JandexHelper.getValue( generator, "uniqueConstraints", AnnotationInstance[].class ).length > 0 ) {
 			LOG.ignoringTableGeneratorConstraints( name );
 		}
 		metadata.addIdGenerator( new IdGenerator( name, strategy, prms ) );
 		LOG.tracef( "Add table generator with name: %s", name );
 	}
 
 	public static String generatorType(GenerationType generatorEnum, boolean useNewGeneratorMappings) {
 		switch ( generatorEnum ) {
 			case IDENTITY:
 				return "identity";
 			case AUTO:
 				return useNewGeneratorMappings
 						? "enhanced-sequence"
 						: "native";
 			case TABLE:
 				return useNewGeneratorMappings
 						? "enhanced-table"
 						: MultipleHiLoPerTableGenerator.class.getName();
 			case SEQUENCE:
 				return useNewGeneratorMappings
 						? "enhanced-sequence"
 						: "seqhilo";
 		}
 		throw new AssertionFailure( "Unknown GeneratorType: " + generatorEnum );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java
index 29be2c520c..ccf6743bd9 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/QueryBinder.java
@@ -1,326 +1,326 @@
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
 
 import java.util.HashMap;
 import javax.persistence.NamedNativeQueries;
 import javax.persistence.NamedNativeQuery;
 import javax.persistence.NamedQueries;
 import javax.persistence.NamedQuery;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.Index;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.CacheMode;
 import org.hibernate.FlushMode;
 import org.hibernate.LockMode;
 import org.hibernate.annotations.QueryHints;
 import org.hibernate.cfg.NotYetImplementedException;
 import org.hibernate.engine.query.spi.sql.NativeSQLQueryRootReturn;
 import org.hibernate.engine.spi.NamedQueryDefinition;
 import org.hibernate.engine.spi.NamedSQLQueryDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
 
 public class QueryBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			QueryBinder.class.getName()
 	);
 
 	private QueryBinder() {
 	}
 
 	/**
 	 * Binds all {@link NamedQuery}, {@link NamedQueries}, {@link NamedNativeQuery}, {@link NamedNativeQueries},
 	 * {@link org.hibernate.annotations.NamedQuery}, {@link org.hibernate.annotations.NamedQueries},
 	 * {@link org.hibernate.annotations.NamedNativeQuery}, and {@link org.hibernate.annotations.NamedNativeQueries}
 	 * annotations to the supplied metadata.
 	 *
 	 * @param metadata the global metadata
 	 * @param jandex the jandex index
 	 */
 	public static void bind(MetadataImplementor metadata, Index jandex) {
 		for ( AnnotationInstance query : jandex.getAnnotations( JPADotNames.NAMED_QUERY ) ) {
 			bindNamedQuery( metadata, query );
 		}
 		for ( AnnotationInstance queries : jandex.getAnnotations( JPADotNames.NAMED_QUERIES ) ) {
 			for ( AnnotationInstance query : JandexHelper.getValue( queries, "value", AnnotationInstance[].class ) ) {
 				bindNamedQuery( metadata, query );
 			}
 		}
 		for ( AnnotationInstance query : jandex.getAnnotations( JPADotNames.NAMED_NATIVE_QUERY ) ) {
 			bindNamedNativeQuery( metadata, query );
 		}
 		for ( AnnotationInstance queries : jandex.getAnnotations( JPADotNames.NAMED_NATIVE_QUERIES ) ) {
 			for ( AnnotationInstance query : JandexHelper.getValue( queries, "value", AnnotationInstance[].class ) ) {
 				bindNamedNativeQuery( metadata, query );
 			}
 		}
 		for ( AnnotationInstance query : jandex.getAnnotations( HibernateDotNames.NAMED_QUERY ) ) {
 			bindNamedQuery( metadata, query );
 		}
 		for ( AnnotationInstance queries : jandex.getAnnotations( HibernateDotNames.NAMED_QUERIES ) ) {
 			for ( AnnotationInstance query : JandexHelper.getValue( queries, "value", AnnotationInstance[].class ) ) {
 				bindNamedQuery( metadata, query );
 			}
 		}
 		for ( AnnotationInstance query : jandex.getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERY ) ) {
 			bindNamedNativeQuery( metadata, query );
 		}
 		for ( AnnotationInstance queries : jandex.getAnnotations( HibernateDotNames.NAMED_NATIVE_QUERIES ) ) {
 			for ( AnnotationInstance query : JandexHelper.getValue( queries, "value", AnnotationInstance[].class ) ) {
 				bindNamedNativeQuery( metadata, query );
 			}
 		}
 	}
 
 	/**
 	 * Binds {@link javax.persistence.NamedQuery} as well as {@link org.hibernate.annotations.NamedQuery}.
 	 *
 	 * @param metadata the current metadata
 	 * @param annotation the named query annotation
 	 */
 	private static void bindNamedQuery(MetadataImplementor metadata, AnnotationInstance annotation) {
 		String name = JandexHelper.getValue( annotation, "name", String.class );
 		if ( StringHelper.isEmpty( name ) ) {
 			throw new AnnotationException( "A named query must have a name when used in class or package level" );
 		}
 
 		String query = JandexHelper.getValue( annotation, "query", String.class );
 
 		AnnotationInstance[] hints = JandexHelper.getValue( annotation, "hints", AnnotationInstance[].class );
 
 		String cacheRegion = getString( hints, QueryHints.CACHE_REGION );
 		if ( StringHelper.isEmpty( cacheRegion ) ) {
 			cacheRegion = null;
 		}
 
 		Integer timeout = getTimeout( hints, query );
 		if ( timeout != null && timeout < 0 ) {
 			timeout = null;
 		}
 
 		Integer fetchSize = getInteger( hints, QueryHints.FETCH_SIZE, name );
 		if ( fetchSize != null && fetchSize < 0 ) {
 			fetchSize = null;
 		}
 
 		String comment = getString( hints, QueryHints.COMMENT );
 		if ( StringHelper.isEmpty( comment ) ) {
 			comment = null;
 		}
 
 		metadata.addNamedQuery(
 				new NamedQueryDefinition(
 						name,
 						query, getBoolean( hints, QueryHints.CACHEABLE, name ), cacheRegion,
 						timeout, fetchSize, getFlushMode( hints, QueryHints.FLUSH_MODE, name ),
 						getCacheMode( hints, QueryHints.CACHE_MODE, name ),
 						getBoolean( hints, QueryHints.READ_ONLY, name ), comment, null
 				)
 		);
 		LOG.debugf( "Binding named query: %s => %s", name, query );
 	}
 
 	private static void bindNamedNativeQuery(MetadataImplementor metadata, AnnotationInstance annotation) {
 		String name = JandexHelper.getValue( annotation, "name", String.class );
 		if ( StringHelper.isEmpty( name ) ) {
 			throw new AnnotationException( "A named native query must have a name when used in class or package level" );
 		}
 
 		String query = JandexHelper.getValue( annotation, "query", String.class );
 
 		String resultSetMapping = JandexHelper.getValue( annotation, "resultSetMapping", String.class );
 
 		AnnotationInstance[] hints = JandexHelper.getValue( annotation, "hints", AnnotationInstance[].class );
 
 		boolean cacheable = getBoolean( hints, "org.hibernate.cacheable", name );
 		String cacheRegion = getString( hints, QueryHints.CACHE_REGION );
 		if ( StringHelper.isEmpty( cacheRegion ) ) {
 			cacheRegion = null;
 		}
 
 		Integer timeout = getTimeout( hints, query );
 		if ( timeout != null && timeout < 0 ) {
 			timeout = null;
 		}
 
 		Integer fetchSize = getInteger( hints, QueryHints.FETCH_SIZE, name );
 		if ( fetchSize != null && fetchSize < 0 ) {
 			fetchSize = null;
 		}
 
 		FlushMode flushMode = getFlushMode( hints, QueryHints.FLUSH_MODE, name );
 		CacheMode cacheMode = getCacheMode( hints, QueryHints.CACHE_MODE, name );
 
 		boolean readOnly = getBoolean( hints, QueryHints.READ_ONLY, name );
 
 		String comment = getString( hints, QueryHints.COMMENT );
 		if ( StringHelper.isEmpty( comment ) ) {
 			comment = null;
 		}
 
 		boolean callable = getBoolean( hints, QueryHints.CALLABLE, name );
 		NamedSQLQueryDefinition def;
 		if ( StringHelper.isNotEmpty( resultSetMapping ) ) {
 			def = new NamedSQLQueryDefinition(
 					name,
 					query, resultSetMapping, null, cacheable,
 					cacheRegion, timeout, fetchSize,
 					flushMode, cacheMode, readOnly, comment,
 					null, callable
 			);
 		}
 		else {
 			AnnotationValue annotationValue = annotation.value( "resultClass" );
 			if ( annotationValue == null ) {
 				throw new NotYetImplementedException( "Pure native scalar queries are not yet supported" );
 			}
 			NativeSQLQueryRootReturn queryRoots[] = new NativeSQLQueryRootReturn[] {
 					new NativeSQLQueryRootReturn(
 							"alias1",
 							annotationValue.asString(),
 							new HashMap<String, String[]>(),
 							LockMode.READ
 					)
 			};
 			def = new NamedSQLQueryDefinition(
 					name,
 					query,
 					queryRoots,
 					null,
 					cacheable,
 					cacheRegion,
 					timeout,
 					fetchSize,
 					flushMode,
 					cacheMode,
 					readOnly,
 					comment,
 					null,
 					callable
 			);
 		}
 		metadata.addNamedNativeQuery( def );
 		LOG.debugf( "Binding named native query: %s => %s", name, query );
 	}
 
 	private static boolean getBoolean(AnnotationInstance[] hints, String element, String query) {
 		String val = getString( hints, element );
 		if ( val == null || val.equalsIgnoreCase( "false" ) ) {
 			return false;
 		}
 		if ( val.equalsIgnoreCase( "true" ) ) {
 			return true;
 		}
 		throw new AnnotationException( "Not a boolean in hint: " + query + ":" + element );
 	}
 
 	private static CacheMode getCacheMode(AnnotationInstance[] hints, String element, String query) {
 		String val = getString( hints, element );
 		if ( val == null ) {
 			return null;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.GET.toString() ) ) {
 			return CacheMode.GET;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.IGNORE.toString() ) ) {
 			return CacheMode.IGNORE;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.NORMAL.toString() ) ) {
 			return CacheMode.NORMAL;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.PUT.toString() ) ) {
 			return CacheMode.PUT;
 		}
 		if ( val.equalsIgnoreCase( CacheMode.REFRESH.toString() ) ) {
 			return CacheMode.REFRESH;
 		}
 		throw new AnnotationException( "Unknown CacheMode in hint: " + query + ":" + element );
 	}
 
 	private static FlushMode getFlushMode(AnnotationInstance[] hints, String element, String query) {
 		String val = getString( hints, element );
 		if ( val == null ) {
 			return null;
 		}
 		if ( val.equalsIgnoreCase( FlushMode.ALWAYS.toString() ) ) {
 			return FlushMode.ALWAYS;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.AUTO.toString() ) ) {
 			return FlushMode.AUTO;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.COMMIT.toString() ) ) {
 			return FlushMode.COMMIT;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.NEVER.toString() ) ) {
 			return FlushMode.MANUAL;
 		}
 		else if ( val.equalsIgnoreCase( FlushMode.MANUAL.toString() ) ) {
 			return FlushMode.MANUAL;
 		}
 		else {
 			throw new AnnotationException( "Unknown FlushMode in hint: " + query + ":" + element );
 		}
 	}
 
 	private static Integer getInteger(AnnotationInstance[] hints, String element, String query) {
 		String val = getString( hints, element );
 		if ( val == null ) {
 			return null;
 		}
 		try {
 			return Integer.decode( val );
 		}
 		catch ( NumberFormatException nfe ) {
 			throw new AnnotationException( "Not an integer in hint: " + query + ":" + element, nfe );
 		}
 	}
 
 	private static String getString(AnnotationInstance[] hints, String element) {
 		for ( AnnotationInstance hint : hints ) {
 			if ( element.equals( JandexHelper.getValue( hint, "name", String.class ) ) ) {
 				return JandexHelper.getValue( hint, "value", String.class );
 			}
 		}
 		return null;
 	}
 
 	private static Integer getTimeout(AnnotationInstance[] hints, String query) {
 		Integer timeout = getInteger( hints, QueryHints.TIMEOUT_JPA, query );
 		if ( timeout == null ) {
 			return getInteger( hints, QueryHints.TIMEOUT_HIBERNATE, query ); // timeout is already in seconds
 		}
 		return ( ( timeout + 500 ) / 1000 ); // convert milliseconds to seconds (rounded)
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TableBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TableBinder.java
index 8f3e0054e6..153942978b 100644
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
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.ObjectName;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
 
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
 		Table table = schema.locateTable( objectName.getName() );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TypeDefBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TypeDefBinder.java
index 9835403dc5..2e89fd1015 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TypeDefBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/global/TypeDefBinder.java
@@ -1,118 +1,118 @@
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
 
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.Index;
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.annotations.TypeDefs;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.TypeDef;
 import org.hibernate.metamodel.source.annotations.HibernateDotNames;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
 
 public class TypeDefBinder {
 
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			TypeDefBinder.class.getName()
 	);
 
 	/**
 	 * Binds all {@link org.hibernate.annotations.TypeDef} and {@link TypeDefs} annotations to the supplied metadata.
 	 *
 	 * @param metadata the global metadata
 	 * @param jandex the jandex jandex
 	 */
 	public static void bind(MetadataImplementor metadata, Index jandex) {
 		for ( AnnotationInstance typeDef : jandex.getAnnotations( HibernateDotNames.TYPE_DEF ) ) {
 			bind( metadata, typeDef );
 		}
 		for ( AnnotationInstance typeDefs : jandex.getAnnotations( HibernateDotNames.TYPE_DEFS ) ) {
 			AnnotationInstance[] typeDefAnnotations = JandexHelper.getValue(
 					typeDefs,
 					"value",
 					AnnotationInstance[].class
 			);
 			for ( AnnotationInstance typeDef : typeDefAnnotations ) {
 				bind( metadata, typeDef );
 			}
 		}
 	}
 
 	private static void bind(MetadataImplementor metadata, AnnotationInstance typeDefAnnotation) {
 		String name = JandexHelper.getValue( typeDefAnnotation, "name", String.class );
 		String defaultForType = JandexHelper.getValue( typeDefAnnotation, "defaultForType", String.class );
 		String typeClass = JandexHelper.getValue( typeDefAnnotation, "typeClass", String.class );
 
 		boolean noName = StringHelper.isEmpty( name );
 		boolean noDefaultForType = defaultForType == null || defaultForType.equals( void.class.getName() );
 
 		if ( noName && noDefaultForType ) {
 			throw new AnnotationException(
 					"Either name or defaultForType (or both) attribute should be set in TypeDef having typeClass "
 							+ typeClass
 			);
 		}
 
 		Map<String, String> parameterMaps = new HashMap<String, String>();
 		AnnotationInstance[] parameterAnnotations = JandexHelper.getValue(
 				typeDefAnnotation,
 				"parameters",
 				AnnotationInstance[].class
 		);
 		for ( AnnotationInstance parameterAnnotation : parameterAnnotations ) {
 			parameterMaps.put(
 					JandexHelper.getValue( parameterAnnotation, "name", String.class ),
 					JandexHelper.getValue( parameterAnnotation, "value", String.class )
 			);
 		}
 
 		if ( !noName ) {
 			bind( name, typeClass, parameterMaps, metadata );
 		}
 		if ( !noDefaultForType ) {
 			bind( defaultForType, typeClass, parameterMaps, metadata );
 		}
 	}
 
 	private static void bind(
 			String name,
 			String typeClass,
 			Map<String, String> prms,
 			MetadataImplementor metadata) {
 		LOG.debugf( "Binding type definition: %s", name );
 		metadata.addTypeDefinition( new TypeDef( name, typeClass, prms ) );
 	}
 
 	private TypeDefBinder() {
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParser.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParser.java
index e9cc4cf149..cb2988258e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParser.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParser.java
@@ -1,64 +1,64 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc..
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
 package org.hibernate.metamodel.source.annotations.xml;
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.jboss.jandex.Index;
 
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
+import org.hibernate.metamodel.binder.source.internal.JaxbRoot;
 import org.hibernate.metamodel.source.annotation.xml.XMLEntityMappings;
 import org.hibernate.metamodel.source.annotations.xml.mocker.EntityMappingsMocker;
-import org.hibernate.metamodel.source.internal.JaxbRoot;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
 
 /**
  * @author Hardy Ferentschik
  * @todo Is this still class really still necessary? Maybe it should be removed (HF)
  */
 public class OrmXmlParser {
 	private final MetadataImplementor metadata;
 
 	public OrmXmlParser(MetadataImplementor metadata) {
 		this.metadata = metadata;
 	}
 
 	/**
 	 * Parses the given xml configuration files and returns a updated annotation index
 	 *
 	 * @param mappings list of {@code XMLEntityMappings} created from the specified orm xml files
 	 * @param annotationIndex the annotation index based on scanned annotations
 	 *
 	 * @return a new updated annotation index, enhancing and modifying the existing ones according to the jpa xml rules
 	 */
 	public Index parseAndUpdateIndex(List<JaxbRoot<XMLEntityMappings>> mappings, Index annotationIndex) {
 		List<XMLEntityMappings> list = new ArrayList<XMLEntityMappings>( mappings.size() );
 		for ( JaxbRoot<XMLEntityMappings> jaxbRoot : mappings ) {
 			list.add( jaxbRoot.getRoot() );
 		}
 		return new EntityMappingsMocker( list, annotationIndex, metadata.getServiceRegistry() ).mockNewIndex();
 	}
 }
 
 
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/xml/mocker/GlobalAnnotations.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/xml/mocker/GlobalAnnotations.java
index 9dd1f2f2c6..0306ee8c4c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/xml/mocker/GlobalAnnotations.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/annotations/xml/mocker/GlobalAnnotations.java
@@ -1,299 +1,299 @@
 /*
  * Hibernate, Relational Persistence for Idiomatic Java
  *
  * Copyright (c) 2011, Red Hat Inc. or third-party contributors as
  * indicated by the @author tags or express copyright attribution
  * statements applied by the authors.  All third-party contributions are
  * distributed under license by Red Hat Inc..
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
 package org.hibernate.metamodel.source.annotations.xml.mocker;
 
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.jandex.AnnotationInstance;
 import org.jboss.jandex.AnnotationValue;
 import org.jboss.jandex.DotName;
 import org.jboss.logging.Logger;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
-import org.hibernate.metamodel.source.MappingException;
+import org.hibernate.metamodel.binder.MappingException;
 import org.hibernate.metamodel.source.annotation.xml.XMLAttributes;
 import org.hibernate.metamodel.source.annotation.xml.XMLEntity;
 import org.hibernate.metamodel.source.annotation.xml.XMLEntityMappings;
 import org.hibernate.metamodel.source.annotation.xml.XMLId;
 import org.hibernate.metamodel.source.annotation.xml.XMLNamedNativeQuery;
 import org.hibernate.metamodel.source.annotation.xml.XMLNamedQuery;
 import org.hibernate.metamodel.source.annotation.xml.XMLSequenceGenerator;
 import org.hibernate.metamodel.source.annotation.xml.XMLSqlResultSetMapping;
 import org.hibernate.metamodel.source.annotation.xml.XMLTableGenerator;
 import org.hibernate.metamodel.source.annotations.JPADotNames;
 
 /**
  * @author Strong Liu
  */
 class GlobalAnnotations implements JPADotNames {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger(
 			CoreMessageLogger.class,
 			GlobalAnnotations.class.getName()
 	);
 	private Map<String, XMLSequenceGenerator> sequenceGeneratorMap = new HashMap<String, XMLSequenceGenerator>();
 	private Map<String, XMLTableGenerator> tableGeneratorMap = new HashMap<String, XMLTableGenerator>();
 	private Map<String, XMLNamedQuery> namedQueryMap = new HashMap<String, XMLNamedQuery>();
 	private Map<String, XMLNamedNativeQuery> namedNativeQueryMap = new HashMap<String, XMLNamedNativeQuery>();
 	private Map<String, XMLSqlResultSetMapping> sqlResultSetMappingMap = new HashMap<String, XMLSqlResultSetMapping>();
 	private Map<DotName, List<AnnotationInstance>> annotationInstanceMap = new HashMap<DotName, List<AnnotationInstance>>();
 	private List<AnnotationInstance> indexedAnnotationInstanceList = new ArrayList<AnnotationInstance>();
 	//---------------------------
 	private Set<String> defaultNamedNativeQueryNames = new HashSet<String>();
 	private Set<String> defaultNamedQueryNames = new HashSet<String>();
 	private Set<String> defaultNamedGenerators = new HashSet<String>();
 	private Set<String> defaultSqlResultSetMappingNames = new HashSet<String>();
 
 	Map<DotName, List<AnnotationInstance>> getAnnotationInstanceMap() {
 		return annotationInstanceMap;
 	}
 
 	AnnotationInstance push(DotName name, AnnotationInstance annotationInstance) {
 		if ( name == null || annotationInstance == null ) {
 			return null;
 		}
 		List<AnnotationInstance> list = annotationInstanceMap.get( name );
 		if ( list == null ) {
 			list = new ArrayList<AnnotationInstance>();
 			annotationInstanceMap.put( name, list );
 		}
 		list.add( annotationInstance );
 		return annotationInstance;
 	}
 
 
 	void addIndexedAnnotationInstance(List<AnnotationInstance> annotationInstanceList) {
 		if ( MockHelper.isNotEmpty( annotationInstanceList ) ) {
 			indexedAnnotationInstanceList.addAll( annotationInstanceList );
 		}
 	}
 
 	/**
 	 * do the orm xmls define global configurations?
 	 */
 	boolean hasGlobalConfiguration() {
 		return !( namedQueryMap.isEmpty() && namedNativeQueryMap.isEmpty() && sequenceGeneratorMap.isEmpty() && tableGeneratorMap
 				.isEmpty() && sqlResultSetMappingMap.isEmpty() );
 	}
 
 	Map<String, XMLNamedNativeQuery> getNamedNativeQueryMap() {
 		return namedNativeQueryMap;
 	}
 
 	Map<String, XMLNamedQuery> getNamedQueryMap() {
 		return namedQueryMap;
 	}
 
 	Map<String, XMLSequenceGenerator> getSequenceGeneratorMap() {
 		return sequenceGeneratorMap;
 	}
 
 	Map<String, XMLSqlResultSetMapping> getSqlResultSetMappingMap() {
 		return sqlResultSetMappingMap;
 	}
 
 	Map<String, XMLTableGenerator> getTableGeneratorMap() {
 		return tableGeneratorMap;
 	}
 
 
 	public void filterIndexedAnnotations() {
 		for ( AnnotationInstance annotationInstance : indexedAnnotationInstanceList ) {
 			pushIfNotExist( annotationInstance );
 		}
 	}
 
 	private void pushIfNotExist(AnnotationInstance annotationInstance) {
 		DotName annName = annotationInstance.name();
 		boolean isNotExist = false;
 		if ( annName.equals( SQL_RESULT_SET_MAPPINGS ) ) {
 			AnnotationInstance[] annotationInstances = annotationInstance.value().asNestedArray();
 			for ( AnnotationInstance ai : annotationInstances ) {
 				pushIfNotExist( ai );
 			}
 		}
 		else {
 			AnnotationValue value = annotationInstance.value( "name" );
 			String name = value.asString();
 			isNotExist = ( annName.equals( TABLE_GENERATOR ) && !tableGeneratorMap.containsKey( name ) ) ||
 					( annName.equals( SEQUENCE_GENERATOR ) && !sequenceGeneratorMap.containsKey( name ) ) ||
 					( annName.equals( NAMED_QUERY ) && !namedQueryMap.containsKey( name ) ) ||
 					( annName.equals( NAMED_NATIVE_QUERY ) && !namedNativeQueryMap.containsKey( name ) ) ||
 					( annName.equals( SQL_RESULT_SET_MAPPING ) && !sqlResultSetMappingMap.containsKey( name ) );
 		}
 		if ( isNotExist ) {
 			push( annName, annotationInstance );
 		}
 	}
 
 	void collectGlobalMappings(XMLEntityMappings entityMappings, EntityMappingsMocker.Default defaults) {
 		for ( XMLSequenceGenerator generator : entityMappings.getSequenceGenerator() ) {
 			put( generator, defaults );
 			defaultNamedGenerators.add( generator.getName() );
 		}
 		for ( XMLTableGenerator generator : entityMappings.getTableGenerator() ) {
 			put( generator, defaults );
 			defaultNamedGenerators.add( generator.getName() );
 		}
 		for ( XMLNamedQuery namedQuery : entityMappings.getNamedQuery() ) {
 			put( namedQuery );
 			defaultNamedQueryNames.add( namedQuery.getName() );
 		}
 		for ( XMLNamedNativeQuery namedNativeQuery : entityMappings.getNamedNativeQuery() ) {
 			put( namedNativeQuery );
 			defaultNamedNativeQueryNames.add( namedNativeQuery.getName() );
 		}
 		for ( XMLSqlResultSetMapping sqlResultSetMapping : entityMappings.getSqlResultSetMapping() ) {
 			put( sqlResultSetMapping );
 			defaultSqlResultSetMappingNames.add( sqlResultSetMapping.getName() );
 		}
 	}
 
 	void collectGlobalMappings(XMLEntity entity, EntityMappingsMocker.Default defaults) {
 		for ( XMLNamedQuery namedQuery : entity.getNamedQuery() ) {
 			if ( !defaultNamedQueryNames.contains( namedQuery.getName() ) ) {
 				put( namedQuery );
 			}
 			else {
 				LOG.warn( "Named Query [" + namedQuery.getName() + "] duplicated." );
 			}
 		}
 		for ( XMLNamedNativeQuery namedNativeQuery : entity.getNamedNativeQuery() ) {
 			if ( !defaultNamedNativeQueryNames.contains( namedNativeQuery.getName() ) ) {
 				put( namedNativeQuery );
 			}
 			else {
 				LOG.warn( "Named native Query [" + namedNativeQuery.getName() + "] duplicated." );
 			}
 		}
 		for ( XMLSqlResultSetMapping sqlResultSetMapping : entity.getSqlResultSetMapping() ) {
 			if ( !defaultSqlResultSetMappingNames.contains( sqlResultSetMapping.getName() ) ) {
 				put( sqlResultSetMapping );
 			}
 		}
 		XMLSequenceGenerator sequenceGenerator = entity.getSequenceGenerator();
 		if ( sequenceGenerator != null ) {
 			if ( !defaultNamedGenerators.contains( sequenceGenerator.getName() ) ) {
 				put( sequenceGenerator, defaults );
 			}
 		}
 		XMLTableGenerator tableGenerator = entity.getTableGenerator();
 		if ( tableGenerator != null ) {
 			if ( !defaultNamedGenerators.contains( tableGenerator.getName() ) ) {
 				put( tableGenerator, defaults );
 			}
 		}
 		XMLAttributes attributes = entity.getAttributes();
 		if ( attributes != null ) {
 			for ( XMLId id : attributes.getId() ) {
 				sequenceGenerator = id.getSequenceGenerator();
 				if ( sequenceGenerator != null ) {
 					put( sequenceGenerator, defaults );
 				}
 				tableGenerator = id.getTableGenerator();
 				if ( tableGenerator != null ) {
 					put( tableGenerator, defaults );
 				}
 			}
 		}
 	}
 
 	/**
 	 * Override SequenceGenerator using info definded in EntityMappings/Persistence-Metadata-Unit
 	 */
 	private static XMLSequenceGenerator overrideGenerator(XMLSequenceGenerator generator, EntityMappingsMocker.Default defaults) {
 		if ( StringHelper.isEmpty( generator.getSchema() ) && defaults != null ) {
 			generator.setSchema( defaults.getSchema() );
 		}
 		if ( StringHelper.isEmpty( generator.getCatalog() ) && defaults != null ) {
 			generator.setCatalog( defaults.getCatalog() );
 		}
 		return generator;
 	}
 
 	/**
 	 * Override TableGenerator using info definded in EntityMappings/Persistence-Metadata-Unit
 	 */
 	private static XMLTableGenerator overrideGenerator(XMLTableGenerator generator, EntityMappingsMocker.Default defaults) {
 		if ( StringHelper.isEmpty( generator.getSchema() ) && defaults != null ) {
 			generator.setSchema( defaults.getSchema() );
 		}
 		if ( StringHelper.isEmpty( generator.getCatalog() ) && defaults != null ) {
 			generator.setCatalog( defaults.getCatalog() );
 		}
 		return generator;
 	}
 
 	private void put(XMLNamedNativeQuery query) {
 		if ( query != null ) {
 			checkQueryName( query.getName() );
 			namedNativeQueryMap.put( query.getName(), query );
 		}
 	}
 
 	private void checkQueryName(String name) {
 		if ( namedQueryMap.containsKey( name ) || namedNativeQueryMap.containsKey( name ) ) {
 			throw new MappingException( "Duplicated query mapping " + name, null );
 		}
 	}
 
 	private void put(XMLNamedQuery query) {
 		if ( query != null ) {
 			checkQueryName( query.getName() );
 			namedQueryMap.put( query.getName(), query );
 		}
 	}
 
 	private void put(XMLSequenceGenerator generator, EntityMappingsMocker.Default defaults) {
 		if ( generator != null ) {
 			Object old = sequenceGeneratorMap.put( generator.getName(), overrideGenerator( generator, defaults ) );
 			if ( old != null ) {
 				LOG.duplicateGeneratorName( generator.getName() );
 			}
 		}
 	}
 
 	private void put(XMLTableGenerator generator, EntityMappingsMocker.Default defaults) {
 		if ( generator != null ) {
 			Object old = tableGeneratorMap.put( generator.getName(), overrideGenerator( generator, defaults ) );
 			if ( old != null ) {
 				LOG.duplicateGeneratorName( generator.getName() );
 			}
 		}
 	}
 
 	private void put(XMLSqlResultSetMapping mapping) {
 		if ( mapping != null ) {
 			Object old = sqlResultSetMappingMap.put( mapping.getName(), mapping );
 			if ( old != null ) {
 				throw new MappingException( "Duplicated SQL result set mapping " +  mapping.getName(), null );
 			}
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
deleted file mode 100644
index 4810aab849..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
+++ /dev/null
@@ -1,541 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.metamodel.source.hbm;
-
-import org.hibernate.AssertionFailure;
-import org.hibernate.EntityMode;
-import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.internal.util.StringHelper;
-import org.hibernate.metamodel.binder.view.hbm.EntityViewImpl;
-import org.hibernate.metamodel.binding.AttributeBinding;
-import org.hibernate.metamodel.binding.BagBinding;
-import org.hibernate.metamodel.binding.CollectionElementType;
-import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.InheritanceType;
-import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
-import org.hibernate.metamodel.binding.SimpleAttributeBinding;
-import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
-import org.hibernate.metamodel.binding.state.PluralAttributeBindingState;
-import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
-import org.hibernate.metamodel.domain.Attribute;
-import org.hibernate.metamodel.domain.Hierarchical;
-import org.hibernate.metamodel.relational.Schema;
-import org.hibernate.metamodel.relational.Table;
-import org.hibernate.metamodel.relational.TableSpecification;
-import org.hibernate.metamodel.relational.UniqueKey;
-import org.hibernate.metamodel.relational.state.ManyToOneRelationalState;
-import org.hibernate.metamodel.relational.state.TupleRelationalState;
-import org.hibernate.metamodel.relational.state.ValueRelationalState;
-import org.hibernate.metamodel.source.hbm.state.binding.HbmManyToOneAttributeBindingState;
-import org.hibernate.metamodel.source.hbm.state.binding.HbmPluralAttributeBindingState;
-import org.hibernate.metamodel.source.hbm.state.binding.HbmSimpleAttributeBindingState;
-import org.hibernate.metamodel.source.hbm.state.relational.HbmManyToOneRelationalStateContainer;
-import org.hibernate.metamodel.source.hbm.state.relational.HbmSimpleValueRelationalStateContainer;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLAnyElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLBagElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLComponentElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLDynamicComponentElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLFilterElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLIdbagElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLJoinElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLJoinedSubclassElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLListElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLManyToOneElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLMapElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLOneToOneElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLPropertiesElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLPropertyElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLQueryElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLResultsetElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSetElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlQueryElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSubclassElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLTuplizerElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLUnionSubclassElement;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
-
-/**
- * TODO : javadoc
- *
- * @author Steve Ebersole
- */
-abstract class AbstractEntityBinder {
-	private final HbmBindingContext bindingContext;
-	private final Schema.Name schemaName;
-
-	AbstractEntityBinder(HbmBindingContext bindingContext, XMLHibernateMapping.XMLClass entityClazz) {
-		this.bindingContext = bindingContext;
-		this.schemaName = new Schema.Name(
-				entityClazz.getSchema() == null
-						? bindingContext.getMappingDefaults().getSchemaName()
-						: entityClazz.getSchema(),
-				entityClazz.getCatalog() == null
-						? bindingContext.getMappingDefaults().getCatalogName() :
-						entityClazz.getCatalog()
-		);
-	}
-
-	public boolean isRoot() {
-		return false;
-	}
-
-	public abstract InheritanceType getInheritanceType();
-
-	public HbmBindingContext getBindingContext() {
-		return bindingContext;
-	}
-
-	protected MetadataImplementor getMetadata() {
-		return bindingContext.getMetadataImplementor();
-	}
-
-	protected Schema.Name getSchemaName() {
-		return schemaName;
-	}
-
-	protected NamingStrategy getNamingStrategy() {
-		return getMetadata().getOptions().getNamingStrategy();
-	}
-
-	protected void basicEntityBinding(
-			XMLHibernateMapping.XMLClass entityClazz,
-			EntityBinding entityBinding,
-			Hierarchical superType) {
-		entityBinding.initialize(
-				bindingContext,
-				new EntityViewImpl(
-						superType,
-						entityClazz,
-						isRoot(),
-						getInheritanceType(),
-						bindingContext
-				)
-		);
-
-		final String entityName = entityBinding.getEntity().getName();
-
-		if ( entityClazz.getFetchProfile() != null ) {
-			bindingContext.processFetchProfiles( entityClazz.getFetchProfile(), entityName );
-		}
-
-		getMetadata().addImport( entityName, entityName );
-		if ( bindingContext.isAutoImport() ) {
-			if ( entityName.indexOf( '.' ) > 0 ) {
-				getMetadata().addImport( StringHelper.unqualify( entityName ), entityName );
-			}
-		}
-	}
-
-	protected String getDefaultAccess() {
-		return bindingContext.getMappingDefaults().getPropertyAccessorName();
-	}
-
-	/**
-	 * Locate any explicit tuplizer definition in the metadata, for the given entity-mode.
-	 *
-	 * @param container The containing element (representing the entity/component)
-	 * @param entityMode The entity-mode for which to locate the tuplizer element
-	 *
-	 * @return The tuplizer element, or null.
-	 */
-	private static XMLTuplizerElement locateTuplizerDefinition(XMLHibernateMapping.XMLClass container,
-															   EntityMode entityMode) {
-		for ( XMLTuplizerElement tuplizer : container.getTuplizer() ) {
-			if ( entityMode.toString().equals( tuplizer.getEntityMode() ) ) {
-				return tuplizer;
-			}
-		}
-		return null;
-	}
-
-	protected String getClassTableName(
-			XMLHibernateMapping.XMLClass entityClazz,
-			EntityBinding entityBinding,
-			Table denormalizedSuperTable) {
-		final String entityName = entityBinding.getEntity().getName();
-		String logicalTableName;
-		String physicalTableName;
-		if ( entityClazz.getTable() == null ) {
-			logicalTableName = StringHelper.unqualify( entityName );
-			physicalTableName = getMetadata()
-					.getOptions()
-					.getNamingStrategy()
-					.classToTableName( entityName );
-		}
-		else {
-			logicalTableName = entityClazz.getTable();
-			physicalTableName = getMetadata()
-					.getOptions()
-					.getNamingStrategy()
-					.tableName( logicalTableName );
-		}
-// todo : find out the purpose of these logical bindings
-//			mappings.addTableBinding( schema, catalog, logicalTableName, physicalTableName, denormalizedSuperTable );
-		return physicalTableName;
-	}
-
-	protected void buildAttributeBindings(XMLHibernateMapping.XMLClass entityClazz,
-										  EntityBinding entityBinding) {
-		// null = UniqueKey (we are not binding a natural-id mapping)
-		// true = mutable, by default properties are mutable
-		// true = nullable, by default properties are nullable.
-		buildAttributeBindings( entityClazz, entityBinding, null, true, true );
-	}
-
-	/**
-	 * This form is essentially used to create natural-id mappings.  But the processing is the same, aside from these
-	 * extra parameterized values, so we encapsulate it here.
-	 *
-	 * @param entityClazz
-	 * @param entityBinding
-	 * @param uniqueKey
-	 * @param mutable
-	 * @param nullable
-	 */
-	protected void buildAttributeBindings(
-			XMLHibernateMapping.XMLClass entityClazz,
-			EntityBinding entityBinding,
-			UniqueKey uniqueKey,
-			boolean mutable,
-			boolean nullable) {
-		final boolean naturalId = uniqueKey != null;
-
-		final String entiytName = entityBinding.getEntity().getName();
-		final TableSpecification tabe = entityBinding.getBaseTable();
-
-		AttributeBinding attributeBinding = null;
-		for ( Object attribute : entityClazz.getPropertyOrManyToOneOrOneToOne() ) {
-			if ( XMLBagElement.class.isInstance( attribute ) ) {
-				XMLBagElement collection = XMLBagElement.class.cast( attribute );
-				BagBinding collectionBinding = makeBagAttributeBinding( collection, entityBinding );
-				bindingContext.getMetadataImplementor().addCollection( collectionBinding );
-				attributeBinding = collectionBinding;
-			}
-			else if ( XMLIdbagElement.class.isInstance( attribute ) ) {
-				XMLIdbagElement collection = XMLIdbagElement.class.cast( attribute );
-				//BagBinding collectionBinding = entityBinding.makeBagAttributeBinding( collection.getName() );
-				//bindIdbag( collection, bagBinding, entityBinding, PluralAttributeNature.BAG, collection.getName() );
-				// todo: handle identifier
-				//attributeBinding = collectionBinding;
-				//hibernateMappingBinder.getHibernateXmlBinder().getMetadata().addCollection( attributeBinding );
-			}
-			else if ( XMLSetElement.class.isInstance( attribute ) ) {
-				XMLSetElement collection = XMLSetElement.class.cast( attribute );
-				//BagBinding collectionBinding = entityBinding.makeBagAttributeBinding( collection.getName() );
-				//bindSet( collection, collectionBinding, entityBinding, PluralAttributeNature.SET, collection.getName() );
-				//attributeBinding = collectionBinding;
-				//hibernateMappingBinder.getHibernateXmlBinder().getMetadata().addCollection( attributeBinding );
-			}
-			else if ( XMLListElement.class.isInstance( attribute ) ) {
-				XMLListElement collection = XMLListElement.class.cast( attribute );
-				//ListBinding collectionBinding = entityBinding.makeBagAttributeBinding( collection.getName() );
-				//bindList( collection, bagBinding, entityBinding, PluralAttributeNature.LIST, collection.getName() );
-				// todo : handle list index
-				//attributeBinding = collectionBinding;
-				//hibernateMappingBinder.getHibernateXmlBinder().getMetadata().addCollection( attributeBinding );
-			}
-			else if ( XMLMapElement.class.isInstance( attribute ) ) {
-				XMLMapElement collection = XMLMapElement.class.cast( attribute );
-				//BagBinding bagBinding = entityBinding.makeBagAttributeBinding( collection.getName() );
-				//bindMap( collection, bagBinding, entityBinding, PluralAttributeNature.MAP, collection.getName() );
-				// todo : handle map key
-				//hibernateMappingBinder.getHibernateXmlBinder().getMetadata().addCollection( attributeBinding );
-			}
-			else if ( XMLManyToOneElement.class.isInstance( attribute ) ) {
-				XMLManyToOneElement manyToOne = XMLManyToOneElement.class.cast( attribute );
-				attributeBinding = makeManyToOneAttributeBinding( manyToOne, entityBinding );
-			}
-			else if ( XMLAnyElement.class.isInstance( attribute ) ) {
-// todo : implement
-//				value = new Any( mappings, table );
-//				bindAny( subElement, (Any) value, nullable, mappings );
-			}
-			else if ( XMLOneToOneElement.class.isInstance( attribute ) ) {
-// todo : implement
-//				value = new OneToOne( mappings, table, persistentClass );
-//				bindOneToOne( subElement, (OneToOne) value, propertyName, true, mappings );
-			}
-			else if ( XMLPropertyElement.class.isInstance( attribute ) ) {
-				XMLPropertyElement property = XMLPropertyElement.class.cast( attribute );
-				attributeBinding = bindProperty( property, entityBinding );
-			}
-			else if ( XMLComponentElement.class.isInstance( attribute )
-					|| XMLDynamicComponentElement.class.isInstance( attribute )
-					|| XMLPropertiesElement.class.isInstance( attribute ) ) {
-// todo : implement
-//				String subpath = StringHelper.qualify( entityName, propertyName );
-//				value = new Component( mappings, persistentClass );
-//
-//				bindComponent(
-//						subElement,
-//						(Component) value,
-//						persistentClass.getClassName(),
-//						propertyName,
-//						subpath,
-//						true,
-//						"properties".equals( subElementName ),
-//						mappings,
-//						inheritedMetas,
-//						false
-//					);
-			}
-		}
-
-		/*
-Array
-PrimitiveArray
-*/
-		for ( XMLJoinElement join : entityClazz.getJoin() ) {
-// todo : implement
-//			Join join = new Join();
-//			join.setPersistentClass( persistentClass );
-//			bindJoin( subElement, join, mappings, inheritedMetas );
-//			persistentClass.addJoin( join );
-		}
-		for ( XMLSubclassElement subclass : entityClazz.getSubclass() ) {
-// todo : implement
-//			handleSubclass( persistentClass, mappings, subElement, inheritedMetas );
-		}
-		for ( XMLJoinedSubclassElement subclass : entityClazz.getJoinedSubclass() ) {
-// todo : implement
-//			handleJoinedSubclass( persistentClass, mappings, subElement, inheritedMetas );
-		}
-		for ( XMLUnionSubclassElement subclass : entityClazz.getUnionSubclass() ) {
-// todo : implement
-//			handleUnionSubclass( persistentClass, mappings, subElement, inheritedMetas );
-		}
-		for ( XMLFilterElement filter : entityClazz.getFilter() ) {
-// todo : implement
-//				parseFilter( subElement, entityBinding );
-		}
-		if ( entityClazz.getNaturalId() != null ) {
-// todo : implement
-//				UniqueKey uk = new UniqueKey();
-//				uk.setName("_UniqueKey");
-//				uk.setTable(table);
-//				//by default, natural-ids are "immutable" (constant)
-//				boolean mutableId = "true".equals( subElement.attributeValue("mutable") );
-//				createClassProperties(
-//						subElement,
-//						persistentClass,
-//						mappings,
-//						inheritedMetas,
-//						uk,
-//						mutableId,
-//						false,
-//						true
-//					);
-//				table.addUniqueKey(uk);
-		}
-		if ( entityClazz.getQueryOrSqlQuery() != null ) {
-			for ( Object queryOrSqlQuery : entityClazz.getQueryOrSqlQuery() ) {
-				if ( XMLQueryElement.class.isInstance( queryOrSqlQuery ) ) {
-// todo : implement
-//				bindNamedQuery(subElement, persistentClass.getEntityName(), mappings);
-				}
-				else if ( XMLSqlQueryElement.class.isInstance( queryOrSqlQuery ) ) {
-// todo : implement
-//			bindNamedSQLQuery(subElement, persistentClass.getEntityName(), mappings);
-				}
-			}
-		}
-		if ( entityClazz.getResultset() != null ) {
-			for ( XMLResultsetElement resultSet : entityClazz.getResultset() ) {
-// todo : implement
-//				bindResultSetMappingDefinition( subElement, persistentClass.getEntityName(), mappings );
-			}
-		}
-//			if ( value != null ) {
-//				Property property = createProperty( value, propertyName, persistentClass
-//					.getClassName(), subElement, mappings, inheritedMetas );
-//				if ( !mutable ) property.setUpdateable(false);
-//				if ( naturalId ) property.setNaturalIdentifier(true);
-//				persistentClass.addProperty( property );
-//				if ( uniqueKey!=null ) uniqueKey.addColumns( property.getColumnIterator() );
-//			}
-
-	}
-
-	protected SimpleAttributeBinding bindProperty(
-			XMLPropertyElement property,
-			EntityBinding entityBinding) {
-		SimpleAttributeBindingState bindingState = new HbmSimpleAttributeBindingState(
-				entityBinding.getEntity().getJavaType().getName(),
-				bindingContext,
-				entityBinding.getMetaAttributeContext(),
-				property
-		);
-
-		// boolean (true here) indicates that by default column names should be guessed
-		ValueRelationalState relationalState =
-				convertToSimpleValueRelationalStateIfPossible(
-						new HbmSimpleValueRelationalStateContainer(
-								bindingContext,
-								true,
-								property
-						)
-				);
-
-		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
-		return entityBinding.makeSimpleAttributeBinding( attribute )
-				.initialize( bindingState )
-				.initialize( relationalState );
-	}
-
-	protected static ValueRelationalState convertToSimpleValueRelationalStateIfPossible(ValueRelationalState state) {
-		// TODO: should a single-valued tuple always be converted???
-		if ( !TupleRelationalState.class.isInstance( state ) ) {
-			return state;
-		}
-		TupleRelationalState tupleRelationalState = TupleRelationalState.class.cast( state );
-		return tupleRelationalState.getRelationalStates().size() == 1 ?
-				tupleRelationalState.getRelationalStates().get( 0 ) :
-				state;
-	}
-
-	protected BagBinding makeBagAttributeBinding(
-			XMLBagElement collection,
-			EntityBinding entityBinding) {
-
-		PluralAttributeBindingState bindingState =
-				new HbmPluralAttributeBindingState(
-						entityBinding.getEntity().getJavaType().getName(),
-						bindingContext,
-						entityBinding.getMetaAttributeContext(),
-						collection
-				);
-
-		BagBinding collectionBinding = entityBinding.makeBagAttributeBinding(
-				bindingState.getAttributeName(),
-				getCollectionElementType( collection )
-		)
-				.initialize( bindingState );
-
-		// todo : relational model binding
-		return collectionBinding;
-	}
-
-	private CollectionElementType getCollectionElementType(XMLBagElement collection) {
-		if ( collection.getElement() != null ) {
-			return CollectionElementType.BASIC;
-		}
-		else if ( collection.getCompositeElement() != null ) {
-			return CollectionElementType.COMPOSITE;
-		}
-		else if ( collection.getManyToMany() != null ) {
-			return CollectionElementType.MANY_TO_MANY;
-		}
-		else if ( collection.getOneToMany() != null ) {
-			return CollectionElementType.ONE_TO_MANY;
-		}
-		else if ( collection.getManyToAny() != null ) {
-			return CollectionElementType.MANY_TO_ANY;
-		}
-		else {
-			throw new AssertionFailure( "Unknown collection element type: " + collection );
-		}
-	}
-
-	private ManyToOneAttributeBinding makeManyToOneAttributeBinding(XMLManyToOneElement manyToOne,
-																	EntityBinding entityBinding) {
-		ManyToOneAttributeBindingState bindingState =
-				new HbmManyToOneAttributeBindingState(
-						entityBinding.getEntity().getJavaType().getName(),
-						bindingContext,
-						entityBinding.getMetaAttributeContext(),
-						manyToOne
-				);
-
-		// boolean (true here) indicates that by default column names should be guessed
-		ManyToOneRelationalState relationalState =
-				new HbmManyToOneRelationalStateContainer(
-						bindingContext,
-						true,
-						manyToOne
-				);
-
-		entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
-		ManyToOneAttributeBinding manyToOneAttributeBinding =
-				entityBinding.makeManyToOneAttributeBinding( bindingState.getAttributeName() )
-						.initialize( bindingState )
-						.initialize( relationalState );
-
-		return manyToOneAttributeBinding;
-	}
-
-//	private static Property createProperty(
-//			final Value value,
-//	        final String propertyName,
-//			final String className,
-//	        final Element subnode,
-//	        final Mappings mappings,
-//			java.util.Map inheritedMetas) throws MappingException {
-//
-//		if ( StringHelper.isEmpty( propertyName ) ) {
-//			throw new MappingException( subnode.getName() + " mapping must defined a name attribute [" + className + "]" );
-//		}
-//
-//		value.setTypeUsingReflection( className, propertyName );
-//
-//		// this is done here 'cos we might only know the type here (ugly!)
-//		// TODO: improve this a lot:
-//		if ( value instanceof ToOne ) {
-//			ToOne toOne = (ToOne) value;
-//			String propertyRef = toOne.getReferencedPropertyName();
-//			if ( propertyRef != null ) {
-//				mappings.addUniquePropertyReference( toOne.getReferencedEntityName(), propertyRef );
-//			}
-//		}
-//		else if ( value instanceof Collection ) {
-//			Collection coll = (Collection) value;
-//			String propertyRef = coll.getReferencedPropertyName();
-//			// not necessarily a *unique* property reference
-//			if ( propertyRef != null ) {
-//				mappings.addPropertyReference( coll.getOwnerEntityName(), propertyRef );
-//			}
-//		}
-//
-//		value.createForeignKey();
-//		Property prop = new Property();
-//		prop.setValue( value );
-//		bindProperty( subnode, prop, mappings, inheritedMetas );
-//		return prop;
-//	}
-
-
-//	protected HbmRelationalState processValues(Element identifierElement, TableSpecification baseTable, String propertyPath, boolean isSimplePrimaryKey) {
-	// first boolean (false here) indicates that by default columns are nullable
-	// second boolean (true here) indicates that by default column names should be guessed
-// todo : logical 1-1 handling
-//			final Attribute uniqueAttribute = node.attribute( "unique" );
-//			if ( uniqueAttribute != null
-//					&& "true".equals( uniqueAttribute.getValue() )
-//					&& ManyToOne.class.isInstance( simpleValue ) ) {
-//				( (ManyToOne) simpleValue ).markAsLogicalOneToOne();
-//			}
-	//return processValues( identifierElement, baseTable, false, true, propertyPath, isSimplePrimaryKey );
-
-
-}
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityProcessor.java
deleted file mode 100644
index 8ea29c9e7b..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/EntityProcessor.java
+++ /dev/null
@@ -1,57 +0,0 @@
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
-package org.hibernate.metamodel.source.hbm;
-
-import org.hibernate.metamodel.binder.EntityBinder;
-import org.hibernate.metamodel.binder.view.hbm.EntityViewImpl;
-import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.source.hbm.xml.mapping.EntityElement;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
-
-/**
- * @author Steve Ebersole
- */
-public abstract class EntityProcessor {
-	private final HbmBindingContext bindingContext;
-	private final EntityBinder entityBinder;
-
-	public EntityProcessor(HbmBindingContext bindingContext) {
-		this.bindingContext = bindingContext;
-		this.entityBinder = new EntityBinder( bindingContext.getMetadataImplementor() );
-	}
-
-	public void process(EntityElement entityMapping) {
-		EntityBinding entityBinding = entityBinder.createEntityBinding(
-				new EntityViewImpl(
-						null,		// superType
-						entityMapping,
-						true,		// isRoot
-						null,		// inheritanceType
-						bindingContext
-				)
-		);
-
-		bindingContext.getMetadataImplementor().addEntity( entityBinding );
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityProcessor.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityProcessor.java
deleted file mode 100644
index 03e70cddf3..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/RootEntityProcessor.java
+++ /dev/null
@@ -1,334 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.metamodel.source.hbm;
-
-import org.hibernate.InvalidMappingException;
-import org.hibernate.MappingException;
-import org.hibernate.internal.util.StringHelper;
-import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.binding.InheritanceType;
-import org.hibernate.metamodel.binding.state.DiscriminatorBindingState;
-import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
-import org.hibernate.metamodel.domain.Attribute;
-import org.hibernate.metamodel.relational.Identifier;
-import org.hibernate.metamodel.relational.InLineView;
-import org.hibernate.metamodel.relational.Schema;
-import org.hibernate.metamodel.relational.Table;
-import org.hibernate.metamodel.relational.state.ValueRelationalState;
-import org.hibernate.metamodel.source.hbm.state.binding.HbmDiscriminatorBindingState;
-import org.hibernate.metamodel.source.hbm.state.binding.HbmSimpleAttributeBindingState;
-import org.hibernate.metamodel.source.hbm.state.relational.HbmSimpleValueRelationalStateContainer;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLCompositeId;
-import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLId;
-
-/**
- * TODO : javadoc
- *
- * @author Steve Ebersole
- */
-class RootEntityProcessor extends AbstractEntityBinder {
-
-	RootEntityProcessor(HbmBindingContext bindingContext, XMLClass xmlClazz) {
-		super( bindingContext, xmlClazz );
-	}
-
-	public boolean isRoot() {
-		return true;
-	}
-
-	public InheritanceType getInheritanceType() {
-		return InheritanceType.SINGLE_TABLE;
-	}
-
-	public void process(XMLClass xmlClazz) {
-		String entityName = getBindingContext().extractEntityName( xmlClazz );
-		if ( entityName == null ) {
-			throw new MappingException( "Unable to determine entity name" );
-		}
-
-		EntityBinding entityBinding = new EntityBinding();
-		basicEntityBinding( xmlClazz, entityBinding, null );
-		basicTableBinding( xmlClazz, entityBinding );
-
-		bindIdentifier( xmlClazz, entityBinding );
-		bindDiscriminator( xmlClazz, entityBinding );
-		bindVersionOrTimestamp( xmlClazz, entityBinding );
-
-		// called createClassProperties in HBMBinder...
-		buildAttributeBindings( xmlClazz, entityBinding );
-
-		getMetadata().addEntity( entityBinding );
-	}
-
-	private void basicTableBinding(XMLClass xmlClazz,
-								   EntityBinding entityBinding) {
-		final Schema schema = getMetadata().getDatabase().getSchema( getSchemaName() );
-
-		final String subSelect =
-				xmlClazz.getSubselectAttribute() == null ? xmlClazz.getSubselect() : xmlClazz.getSubselectAttribute();
-		if ( subSelect != null ) {
-			final String logicalName = entityBinding.getEntity().getName();
-			InLineView inLineView = schema.getInLineView( logicalName );
-			if ( inLineView == null ) {
-				inLineView = schema.createInLineView( logicalName, subSelect );
-			}
-			entityBinding.setBaseTable( inLineView );
-		}
-		else {
-            String classTableName = getClassTableName( xmlClazz, entityBinding, null );
-            if ( getBindingContext().isGloballyQuotedIdentifiers() ) {
-                classTableName = StringHelper.quote( classTableName );
-            }
-			final Identifier tableName = Identifier.toIdentifier( classTableName );
-			final Table table = schema.locateOrCreateTable( tableName );
-			entityBinding.setBaseTable( table );
-			String comment = xmlClazz.getComment();
-			if ( comment != null ) {
-				table.addComment( comment.trim() );
-			}
-			String check = xmlClazz.getCheck();
-			if ( check != null ) {
-				table.addCheckConstraint( check );
-			}
-		}
-	}
-
-	private void bindIdentifier(XMLClass xmlClazz,
-								EntityBinding entityBinding) {
-		if ( xmlClazz.getId() != null ) {
-			bindSimpleId( xmlClazz.getId(), entityBinding );
-			return;
-		}
-
-		if ( xmlClazz.getCompositeId() != null ) {
-			bindCompositeId( xmlClazz.getCompositeId(), entityBinding );
-		}
-
-		throw new InvalidMappingException(
-				"Entity [" + entityBinding.getEntity().getName() + "] did not contain identifier mapping",
-				getBindingContext().getOrigin()
-		);
-	}
-
-	private void bindSimpleId(XMLId id, EntityBinding entityBinding) {
-		SimpleAttributeBindingState bindingState = new HbmSimpleAttributeBindingState(
-				entityBinding.getEntity().getJavaType().getName(),
-				getBindingContext(),
-				entityBinding.getMetaAttributeContext(),
-				id
-		);
-		// boolean (true here) indicates that by default column names should be guessed
-		HbmSimpleValueRelationalStateContainer relationalStateContainer = new HbmSimpleValueRelationalStateContainer(
-				getBindingContext(), true, id
-		);
-		if ( relationalStateContainer.getRelationalStates().size() > 1 ) {
-			throw new MappingException( "ID is expected to be a single column, but has more than 1 value" );
-		}
-
-		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
-		entityBinding.makeSimpleIdAttributeBinding( attribute )
-				.initialize( bindingState )
-				.initialize( relationalStateContainer.getRelationalStates().get( 0 ) );
-
-		// if ( propertyName == null || entity.getPojoRepresentation() == null ) {
-		// bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
-		// if ( !id.isTypeSpecified() ) {
-		// throw new MappingException( "must specify an identifier type: " + entity.getEntityName()
-		// );
-		// }
-		// }
-		// else {
-		// bindSimpleValue( idNode, id, false, propertyName, mappings );
-		// PojoRepresentation pojo = entity.getPojoRepresentation();
-		// id.setTypeUsingReflection( pojo.getClassName(), propertyName );
-		//
-		// Property prop = new Property();
-		// prop.setValue( id );
-		// bindProperty( idNode, prop, mappings, inheritedMetas );
-		// entity.setIdentifierProperty( prop );
-		// }
-
-//		if ( propertyName == null ) {
-//			bindSimpleValue( idNode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings );
-//		}
-//		else {
-//			bindSimpleValue( idNode, id, false, propertyName, mappings );
-//		}
-//
-//		if ( propertyName == null || !entity.hasPojoRepresentation() ) {
-//			if ( !id.isTypeSpecified() ) {
-//				throw new MappingException( "must specify an identifier type: "
-//					+ entity.getEntityName() );
-//			}
-//		}
-//		else {
-//			id.setTypeUsingReflection( entity.getClassName(), propertyName );
-//		}
-//
-//		if ( propertyName != null ) {
-//			Property prop = new Property();
-//			prop.setValue( id );
-//			bindProperty( idNode, prop, mappings, inheritedMetas );
-//			entity.setIdentifierProperty( prop );
-//		}
-
-		// TODO:
-		/*
-		 * if ( id.getHibernateType().getReturnedClass().isArray() ) throw new MappingException(
-		 * "illegal use of an array as an identifier (arrays don't reimplement equals)" );
-		 */
-//		makeIdentifier( idNode, id, mappings );
-	}
-
-	private static void bindCompositeId(XMLCompositeId compositeId, EntityBinding entityBinding) {
-		final String explicitName = compositeId.getName();
-
-//		String propertyName = idNode.attributeValue( "name" );
-//		Component id = new Component( mappings, entity );
-//		entity.setIdentifier( id );
-//		bindCompositeId( idNode, id, entity, propertyName, mappings, inheritedMetas );
-//		if ( propertyName == null ) {
-//			entity.setEmbeddedIdentifier( id.isEmbedded() );
-//			if ( id.isEmbedded() ) {
-//				// todo : what is the implication of this?
-//				id.setDynamic( !entity.hasPojoRepresentation() );
-//				/*
-//				 * Property prop = new Property(); prop.setName("id");
-//				 * prop.setPropertyAccessorName("embedded"); prop.setValue(id);
-//				 * entity.setIdentifierProperty(prop);
-//				 */
-//			}
-//		}
-//		else {
-//			Property prop = new Property();
-//			prop.setValue( id );
-//			bindProperty( idNode, prop, mappings, inheritedMetas );
-//			entity.setIdentifierProperty( prop );
-//		}
-//
-//		makeIdentifier( idNode, id, mappings );
-
-	}
-
-	private void bindDiscriminator(XMLClass xmlEntityClazz,
-								   EntityBinding entityBinding) {
-		if ( xmlEntityClazz.getDiscriminator() == null ) {
-			return;
-		}
-
-		DiscriminatorBindingState bindingState = new HbmDiscriminatorBindingState(
-				entityBinding.getEntity().getJavaType().getName(),
-				entityBinding.getEntity().getName(),
-				getBindingContext(),
-				xmlEntityClazz
-		);
-
-		// boolean (true here) indicates that by default column names should be guessed
-		ValueRelationalState relationalState = convertToSimpleValueRelationalStateIfPossible(
-				new HbmSimpleValueRelationalStateContainer(
-						getBindingContext(),
-						true,
-						xmlEntityClazz.getDiscriminator()
-				)
-		);
-
-
-		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
-		entityBinding.makeEntityDiscriminator( attribute )
-				.initialize( bindingState )
-				.initialize( relationalState );
-	}
-
-	private void bindVersionOrTimestamp(XMLClass xmlEntityClazz,
-										EntityBinding entityBinding) {
-		if ( xmlEntityClazz.getVersion() != null ) {
-			bindVersion(
-					xmlEntityClazz.getVersion(),
-					entityBinding
-			);
-		}
-		else if ( xmlEntityClazz.getTimestamp() != null ) {
-			bindTimestamp(
-					xmlEntityClazz.getTimestamp(),
-					entityBinding
-			);
-		}
-	}
-
-	protected void bindVersion(XMLHibernateMapping.XMLClass.XMLVersion version,
-							   EntityBinding entityBinding) {
-		SimpleAttributeBindingState bindingState =
-				new HbmSimpleAttributeBindingState(
-						entityBinding.getEntity().getJavaType().getName(),
-						getBindingContext(),
-						entityBinding.getMetaAttributeContext(),
-						version
-				);
-
-		// boolean (true here) indicates that by default column names should be guessed
-		ValueRelationalState relationalState =
-				convertToSimpleValueRelationalStateIfPossible(
-						new HbmSimpleValueRelationalStateContainer(
-								getBindingContext(),
-								true,
-								version
-						)
-				);
-
-		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
-		entityBinding.makeVersionBinding( attribute )
-				.initialize( bindingState )
-				.initialize( relationalState );
-	}
-
-	protected void bindTimestamp(XMLHibernateMapping.XMLClass.XMLTimestamp timestamp,
-								 EntityBinding entityBinding) {
-
-		SimpleAttributeBindingState bindingState =
-				new HbmSimpleAttributeBindingState(
-						entityBinding.getEntity().getJavaType().getName(),
-						getBindingContext(),
-						entityBinding.getMetaAttributeContext(),
-						timestamp
-				);
-
-		// relational model has not been bound yet
-		// boolean (true here) indicates that by default column names should be guessed
-		ValueRelationalState relationalState =
-				convertToSimpleValueRelationalStateIfPossible(
-						new HbmSimpleValueRelationalStateContainer(
-								getBindingContext(),
-								true,
-								timestamp
-						)
-				);
-
-		Attribute attribute = entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
-		entityBinding.makeVersionBinding( attribute )
-				.initialize( bindingState )
-				.initialize( relationalState );
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/AbstractHbmAttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/AbstractHbmAttributeBindingState.java
index cbbf21ac34..e0ca19546a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/AbstractHbmAttributeBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/AbstractHbmAttributeBindingState.java
@@ -1,154 +1,154 @@
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
 package org.hibernate.metamodel.source.hbm.state.binding;
 
 import java.util.HashSet;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.metamodel.binder.source.BindingContext;
+import org.hibernate.metamodel.binder.source.MappingDefaults;
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
+import org.hibernate.metamodel.binder.source.hbm.MappingHelper;
 import org.hibernate.metamodel.binding.CascadeType;
 import org.hibernate.metamodel.binding.state.AttributeBindingState;
-import org.hibernate.metamodel.source.hbm.util.MappingHelper;
-import org.hibernate.metamodel.source.spi.BindingContext;
-import org.hibernate.metamodel.source.spi.MappingDefaults;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
 /**
  * @author Gail Badner
  */
 public abstract class AbstractHbmAttributeBindingState implements AttributeBindingState {
 	private final String ownerClassName;
 	private final String attributeName;
 	private final BindingContext bindingContext;
 	private final String nodeName;
 	private final String accessorName;
 	private final boolean isOptimisticLockable;
 	private final MetaAttributeContext metaAttributeContext;
 
 	public AbstractHbmAttributeBindingState(
 			String ownerClassName,
 			String attributeName,
 			BindingContext bindingContext,
 			String nodeName,
 			MetaAttributeContext metaAttributeContext,
 			String accessorName,
 			boolean isOptimisticLockable) {
 		if ( attributeName == null ) {
 			throw new MappingException(
 					"Attribute name cannot be null."
 			);
 		}
 
 		this.ownerClassName = ownerClassName;
 		this.attributeName = attributeName;
 		this.bindingContext = bindingContext;
 		this.nodeName = nodeName;
 		this.metaAttributeContext = metaAttributeContext;
 		this.accessorName = accessorName;
 		this.isOptimisticLockable = isOptimisticLockable;
 	}
 
 	// TODO: really don't like this here...
 	protected String getOwnerClassName() {
 		return ownerClassName;
 	}
 
 	protected Set<CascadeType> determineCascadeTypes(String cascade) {
 		String commaSeparatedCascades = MappingHelper.getStringValue( cascade, getBindingContext().getMappingDefaults().getCascadeStyle() );
 		Set<String> cascades = MappingHelper.getStringValueTokens( commaSeparatedCascades, "," );
 		Set<CascadeType> cascadeTypes = new HashSet<CascadeType>( cascades.size() );
 		for ( String s : cascades ) {
 			CascadeType cascadeType = CascadeType.getCascadeType( s );
 			if ( cascadeType == null ) {
 				throw new MappingException( "Invalid cascading option " + s );
 			}
 			cascadeTypes.add( cascadeType );
 		}
 		return cascadeTypes;
 	}
 
 	protected final String getTypeNameByReflection() {
 		Class ownerClass = bindingContext.locateClassByName( ownerClassName );
 		return ReflectHelper.reflectedPropertyClass( ownerClass, attributeName ).getName();
 	}
 
 	public String getAttributeName() {
 		return attributeName;
 	}
 
 	public BindingContext getBindingContext() {
 		return bindingContext;
 	}
 
 	@Deprecated
 	protected final MappingDefaults getDefaults() {
 		return getBindingContext().getMappingDefaults();
 	}
 
 	@Override
 	public final String getPropertyAccessorName() {
 		return accessorName;
 	}
 
 	@Override
 	public final boolean isAlternateUniqueKey() {
 		//TODO: implement
 		return false;
 	}
 
 	@Override
 	public final boolean isOptimisticLockable() {
 		return isOptimisticLockable;
 	}
 
 	@Override
 	public final String getNodeName() {
 		return nodeName == null ? getAttributeName() : nodeName;
 	}
 
 	@Override
 	public MetaAttributeContext getMetaAttributeContext() {
 		return metaAttributeContext;
 	}
 
 	public PropertyGeneration getPropertyGeneration() {
 		return PropertyGeneration.NEVER;
 	}
 
 	public boolean isKeyCascadeDeleteEnabled() {
 		return false;
 	}
 
 	public String getUnsavedValue() {
 		//TODO: implement
 		return null;
 	}
 
 	public Map<String, String> getTypeParameters() {
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmDiscriminatorBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmDiscriminatorBindingState.java
index a52e9dc256..d9864bb38f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmDiscriminatorBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmDiscriminatorBindingState.java
@@ -1,100 +1,100 @@
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
 package org.hibernate.metamodel.source.hbm.state.binding;
 
 import java.util.Set;
 
+import org.hibernate.metamodel.binder.source.BindingContext;
+import org.hibernate.metamodel.binder.source.hbm.MappingHelper;
 import org.hibernate.metamodel.binding.CascadeType;
 import org.hibernate.metamodel.binding.state.DiscriminatorBindingState;
-import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLDiscriminator;
-import org.hibernate.metamodel.source.spi.BindingContext;
 
 /**
  * @author Gail Badner
  */
 public class HbmDiscriminatorBindingState
 		extends AbstractHbmAttributeBindingState
 		implements DiscriminatorBindingState {
 	private final String discriminatorValue;
 	private final boolean isForced;
 	private final boolean isInserted;
 	private final String typeName;
 
 	public HbmDiscriminatorBindingState(
 			String entityName,
 			String ownerClassName,
 			BindingContext bindingContext,
 			XMLHibernateMapping.XMLClass xmlEntityClazz) {
 		super(
 				ownerClassName,
 				bindingContext.getMappingDefaults().getDiscriminatorColumnName(),
 				bindingContext,
 				null,
 				null,
 				null,
 				true
 		);
 		XMLDiscriminator discriminator = xmlEntityClazz.getDiscriminator();
 		this.discriminatorValue =  MappingHelper.getStringValue(
-					xmlEntityClazz.getDiscriminatorValue(), entityName
+				xmlEntityClazz.getDiscriminatorValue(), entityName
 		);
 		this.isForced = xmlEntityClazz.getDiscriminator().isForce();
 		this.isInserted = discriminator.isInsert();
 		this.typeName =  discriminator.getType() == null ? "string" : discriminator.getType();
 	}
 
 	public Set<CascadeType> getCascadeTypes() {
 		return null;
 	}
 
 	protected boolean isEmbedded() {
 		return false;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 
 	@Override
 	public boolean isLazy() {
 		return false;
 	}
 
 	@Override
 	public boolean isInserted() {
 		return isInserted;
 	}
 
 	@Override
 	public String getDiscriminatorValue() {
 		return discriminatorValue;
 	}
 
 	@Override
 	public boolean isForced() {
 		return isForced;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmManyToOneAttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmManyToOneAttributeBindingState.java
index d402c1cf9f..42ba80107c 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmManyToOneAttributeBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmManyToOneAttributeBindingState.java
@@ -1,184 +1,184 @@
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
 package org.hibernate.metamodel.source.hbm.state.binding;
 
 import java.util.Set;
 
 import org.hibernate.FetchMode;
 import org.hibernate.internal.util.ReflectHelper;
+import org.hibernate.metamodel.binder.source.BindingContext;
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
+import org.hibernate.metamodel.binder.source.hbm.HbmHelper;
+import org.hibernate.metamodel.binder.source.hbm.MappingHelper;
 import org.hibernate.metamodel.binding.CascadeType;
 import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
-import org.hibernate.metamodel.source.hbm.HbmHelper;
-import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLManyToOneElement;
-import org.hibernate.metamodel.source.spi.BindingContext;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
 /**
  * @author Gail Badner
  */
 public class HbmManyToOneAttributeBindingState
 		extends AbstractHbmAttributeBindingState
 		implements ManyToOneAttributeBindingState {
 
 	private final FetchMode fetchMode;
 	private final boolean isUnwrapProxy;
 	private final boolean isLazy;
 	private final Set<CascadeType> cascadeTypes;
 	private final boolean isEmbedded;
 	private final String referencedPropertyName;
 	private final String referencedEntityName;
 	private final boolean ignoreNotFound;
 	private final boolean isInsertable;
 	private final boolean isUpdateable;
 
 	public HbmManyToOneAttributeBindingState(
 			String ownerClassName,
 			BindingContext bindingContext,
 			MetaAttributeContext parentMetaAttributeContext,
 			XMLManyToOneElement manyToOne) {
 		super(
 				ownerClassName,
 				manyToOne.getName(),
 				bindingContext,
 				manyToOne.getNode(),
 				HbmHelper.extractMetaAttributeContext( manyToOne.getMeta(), parentMetaAttributeContext ),
 				HbmHelper.getPropertyAccessorName(
 						manyToOne.getAccess(),
 						manyToOne.isEmbedXml(),
 						bindingContext.getMappingDefaults().getPropertyAccessorName()
 				),
 				manyToOne.isOptimisticLock()
 		);
 		fetchMode = getFetchMode( manyToOne );
 		isUnwrapProxy = manyToOne.getLazy() != null && "no-proxy".equals( manyToOne.getLazy().value() );
 		//TODO: better to degrade to lazy="false" if uninstrumented
 		isLazy = manyToOne.getLazy() == null ||
 				isUnwrapProxy ||
 				"proxy".equals( manyToOne.getLazy().value() );
 		cascadeTypes = determineCascadeTypes( manyToOne.getCascade() );
 		isEmbedded = manyToOne.isEmbedXml();
 		referencedEntityName = getReferencedEntityName( ownerClassName, manyToOne, bindingContext );
 		referencedPropertyName = manyToOne.getPropertyRef();
 		ignoreNotFound = "ignore".equals( manyToOne.getNotFound().value() );
 		isInsertable = manyToOne.isInsert();
 		isUpdateable = manyToOne.isUpdate();
 	}
 
 	// TODO: is this needed???
 	protected boolean isEmbedded() {
 		return isEmbedded;
 	}
 
 	private static String getReferencedEntityName(
 			String ownerClassName,
 			XMLManyToOneElement manyToOne,
 			BindingContext bindingContext) {
 		String referencedEntityName;
 		if ( manyToOne.getEntityName() != null ) {
 			referencedEntityName = manyToOne.getEntityName();
 		}
 		else if ( manyToOne.getClazz() != null ) {
 			referencedEntityName = HbmHelper.getClassName(
 					manyToOne.getClazz(), bindingContext.getMappingDefaults().getPackageName()
 			);
 		}
 		else {
 			Class ownerClazz = MappingHelper.classForName( ownerClassName, bindingContext.getServiceRegistry() );
 			referencedEntityName = ReflectHelper.reflectedPropertyClass( ownerClazz, manyToOne.getName() ).getName();
 		}
 		return referencedEntityName;
 	}
 
 	// same as for plural attributes...
 	private static FetchMode getFetchMode(XMLManyToOneElement manyToOne) {
 		FetchMode fetchMode;
 		if ( manyToOne.getFetch() != null ) {
 			fetchMode = "join".equals( manyToOne.getFetch().value() ) ? FetchMode.JOIN : FetchMode.SELECT;
 		}
 		else {
 			String jfNodeValue = ( manyToOne.getOuterJoin() == null ? "auto" : manyToOne.getOuterJoin().value() );
 			if ( "auto".equals( jfNodeValue ) ) {
 				fetchMode = FetchMode.DEFAULT;
 			}
 			else if ( "true".equals( jfNodeValue ) ) {
 				fetchMode = FetchMode.JOIN;
 			}
 			else {
 				fetchMode = FetchMode.SELECT;
 			}
 		}
 		return fetchMode;
 	}
 
 	public String getTypeName() {
 		return referencedEntityName;
 	}
 
 	public FetchMode getFetchMode() {
 		return fetchMode;
 	}
 
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public boolean isUnwrapProxy() {
 		return isUnwrapProxy;
 	}
 
 	public String getReferencedAttributeName() {
 		return referencedPropertyName;
 	}
 
 	public String getReferencedEntityName() {
 		return referencedEntityName;
 	}
 
 	public Set<CascadeType> getCascadeTypes() {
 		return cascadeTypes;
 	}
 
 	public boolean ignoreNotFound() {
 		return ignoreNotFound;
 	}
 
 	public boolean isInsertable() {
 		return isInsertable;
 	}
 
 	public boolean isUpdatable() {
 		return isUpdateable;
 	}
 
 	public boolean isKeyCascadeDeleteEnabled() {
 		//TODO: implement
 		return false;
 	}
 
 	public String getUnsavedValue() {
 		//TODO: implement
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmPluralAttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmPluralAttributeBindingState.java
index bef4ac5152..e3cf675f4f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmPluralAttributeBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmPluralAttributeBindingState.java
@@ -1,306 +1,306 @@
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
 package org.hibernate.metamodel.source.hbm.state.binding;
 
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Set;
 
 import org.hibernate.FetchMode;
+import org.hibernate.metamodel.binder.source.BindingContext;
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
+import org.hibernate.metamodel.binder.source.hbm.HbmHelper;
+import org.hibernate.metamodel.binder.source.hbm.MappingHelper;
 import org.hibernate.metamodel.binding.CascadeType;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.state.PluralAttributeBindingState;
-import org.hibernate.metamodel.source.hbm.HbmHelper;
-import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLBagElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlDeleteAllElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlDeleteElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlInsertElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlUpdateElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSynchronizeElement;
-import org.hibernate.metamodel.source.spi.BindingContext;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
 /**
  * @author Gail Badner
  */
 public class HbmPluralAttributeBindingState extends AbstractHbmAttributeBindingState
 		implements PluralAttributeBindingState {
 	private final XMLBagElement collection;
 	private final Class collectionPersisterClass;
 	private final String typeName;
 	private final Set<CascadeType> cascadeTypes;
 
 	public HbmPluralAttributeBindingState(
 			String ownerClassName,
 			BindingContext bindingContext,
 			MetaAttributeContext parentMetaAttributeContext,
 			XMLBagElement collection) {
 		super(
 				ownerClassName,
 				collection.getName(),
 				bindingContext,
 				collection.getNode(),
 				HbmHelper.extractMetaAttributeContext( collection.getMeta(), parentMetaAttributeContext ),
 				HbmHelper.getPropertyAccessorName(
 						collection.getAccess(), collection.isEmbedXml(), bindingContext.getMappingDefaults().getPropertyAccessorName()
 				),
 				collection.isOptimisticLock()
 		);
 		this.collection = collection;
 		this.collectionPersisterClass = MappingHelper.classForName(
 				collection.getPersister(), getBindingContext().getServiceRegistry()
 		);
 		this.cascadeTypes = determineCascadeTypes( collection.getCascade() );
 
 		//Attribute typeNode = collectionElement.attribute( "collection-type" );
 		//if ( typeNode != null ) {
 		// TODO: implement when typedef binding is implemented
 		/*
 		   String typeName = typeNode.getValue();
 		   TypeDef typeDef = mappings.getTypeDef( typeName );
 		   if ( typeDef != null ) {
 			   collectionBinding.setTypeName( typeDef.getTypeClass() );
 			   collectionBinding.setTypeParameters( typeDef.getParameters() );
 		   }
 		   else {
 			   collectionBinding.setTypeName( typeName );
 		   }
 		   */
 		//}
 		typeName = collection.getCollectionType();
 	}
 
 	public FetchMode getFetchMode() {
 		FetchMode fetchMode;
 		if ( collection.getFetch() != null ) {
 			fetchMode = "join".equals( collection.getFetch().value() ) ? FetchMode.JOIN : FetchMode.SELECT;
 		}
 		else {
 			String jfNodeValue = ( collection.getOuterJoin().value() == null ? "auto" : collection.getOuterJoin()
 					.value() );
 			if ( "auto".equals( jfNodeValue ) ) {
 				fetchMode = FetchMode.DEFAULT;
 			}
 			else if ( "true".equals( jfNodeValue ) ) {
 				fetchMode = FetchMode.JOIN;
 			}
 			else {
 				fetchMode = FetchMode.SELECT;
 			}
 		}
 		return fetchMode;
 	}
 
 	public boolean isLazy() {
 		return isExtraLazy() ||
 				MappingHelper.getBooleanValue(
 						collection.getLazy().value(), getBindingContext().getMappingDefaults().areAssociationsLazy()
 				);
 	}
 
 	public boolean isExtraLazy() {
 		return ( "extra".equals( collection.getLazy().value() ) );
 	}
 
 	public String getElementTypeName() {
 		return collection.getElement().getTypeAttribute();
 
 	}
 
 	public String getElementNodeName() {
 		return collection.getElement().getNode();
 	}
 
 	public boolean isInverse() {
 		return collection.isInverse();
 	}
 
 	public boolean isMutable() {
 		return collection.isMutable();
 	}
 
 	public boolean isSubselectLoadable() {
 		return "subselect".equals( collection.getFetch().value() );
 	}
 
 	public String getCacheConcurrencyStrategy() {
 		return collection.getCache() == null ?
 				null :
 				collection.getCache().getUsage();
 	}
 
 	public String getCacheRegionName() {
 		return collection.getCache() == null ?
 				null :
 				collection.getCache().getRegion();
 	}
 
 	public String getOrderBy() {
 		return collection.getOrderBy();
 	}
 
 	public String getWhere() {
 		return collection.getWhere();
 	}
 
 	public String getReferencedPropertyName() {
 		return collection.getKey().getPropertyRef();
 	}
 
 	public boolean isSorted() {
 		// SORT
 		// unsorted, natural, comparator.class.name
 		return ( !"unsorted".equals( getSortString() ) );
 	}
 
 	public Comparator getComparator() {
 		return null;
 	}
 
 	public String getComparatorClassName() {
 		String sortString = getSortString();
 		return (
 				isSorted() && !"natural".equals( sortString ) ?
 						sortString :
 						null
 		);
 	}
 
 	private String getSortString() {
 		//TODO: Bag does not define getSort(); update this when there is a Collection subtype
 		// collection.getSort() == null ? "unsorted" : collection.getSort();
 		return "unsorted";
 	}
 
 	public boolean isOrphanDelete() {
 		// ORPHAN DELETE (used for programmer error detection)
 		return true;
 		//return ( getCascade().indexOf( "delete-orphan" ) >= 0 );
 	}
 
 	public int getBatchSize() {
 		return MappingHelper.getIntValue( collection.getBatchSize(), 0 );
 	}
 
 	@Override
 	public boolean isEmbedded() {
 		return collection.isEmbedXml();
 	}
 
 	public boolean isOptimisticLocked() {
 		return collection.isOptimisticLock();
 	}
 
 	public Class getCollectionPersisterClass() {
 		return collectionPersisterClass;
 	}
 
 	public java.util.Map getFilters() {
 		// TODO: IMPLEMENT
 		//Iterator iter = collectionElement.elementIterator( "filter" );
 		//while ( iter.hasNext() ) {
 		//	final Element filter = (Element) iter.next();
 		//	parseFilter( filter, collectionElement, collectionBinding );
 		//}
 		return new HashMap();
 	}
 
 	public java.util.Set getSynchronizedTables() {
 		java.util.Set<String> synchronizedTables = new HashSet<String>();
 		for ( XMLSynchronizeElement sync : collection.getSynchronize() ) {
 			synchronizedTables.add( sync.getTable() );
 		}
 		return synchronizedTables;
 	}
 
 	public CustomSQL getCustomSQLInsert() {
 		XMLSqlInsertElement sqlInsert = collection.getSqlInsert();
 		return sqlInsert == null ?
 				null :
 				HbmHelper.getCustomSql(
 						collection.getSqlInsert().getValue(),
 						collection.getSqlInsert().isCallable(),
 						collection.getSqlInsert().getCheck().value()
 				);
 	}
 
 	public CustomSQL getCustomSQLUpdate() {
 		XMLSqlUpdateElement sqlUpdate = collection.getSqlUpdate();
 		return sqlUpdate == null ?
 				null :
 				HbmHelper.getCustomSql(
 						collection.getSqlUpdate().getValue(),
 						collection.getSqlUpdate().isCallable(),
 						collection.getSqlUpdate().getCheck().value()
 				);
 	}
 
 	public CustomSQL getCustomSQLDelete() {
 		XMLSqlDeleteElement sqlDelete = collection.getSqlDelete();
 		return sqlDelete == null ?
 				null :
 				HbmHelper.getCustomSql(
 						collection.getSqlDelete().getValue(),
 						collection.getSqlDelete().isCallable(),
 						collection.getSqlDelete().getCheck().value()
 				);
 	}
 
 	public CustomSQL getCustomSQLDeleteAll() {
 		XMLSqlDeleteAllElement sqlDeleteAll = collection.getSqlDeleteAll();
 		return sqlDeleteAll == null ?
 				null :
 				HbmHelper.getCustomSql(
 						collection.getSqlDeleteAll().getValue(),
 						collection.getSqlDeleteAll().isCallable(),
 						collection.getSqlDeleteAll().getCheck().value()
 				);
 	}
 
 	public String getLoaderName() {
 		return collection.getLoader() == null ?
 				null :
 				collection.getLoader().getQueryRef();
 	}
 
 	public Set<CascadeType> getCascadeTypes() {
 		return cascadeTypes;
 	}
 
 	public boolean isKeyCascadeDeleteEnabled() {
 		//TODO: implement
 		return false;
 	}
 
 	public String getUnsavedValue() {
 		//TODO: implement
 		return null;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmSimpleAttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmSimpleAttributeBindingState.java
index c9f66109aa..be47be1b36 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmSimpleAttributeBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmSimpleAttributeBindingState.java
@@ -1,280 +1,284 @@
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
 package org.hibernate.metamodel.source.hbm.state.binding;
 
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.mapping.PropertyGeneration;
+import org.hibernate.metamodel.binder.source.BindingContext;
+import org.hibernate.metamodel.binder.source.MappingDefaults;
+import org.hibernate.metamodel.binder.source.MetaAttributeContext;
+import org.hibernate.metamodel.binder.source.hbm.HbmHelper;
+import org.hibernate.metamodel.binder.source.hbm.MappingHelper;
 import org.hibernate.metamodel.binding.CascadeType;
 import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
-import org.hibernate.metamodel.source.hbm.HbmHelper;
-import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLId;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLTimestamp;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLVersion;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLParamElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLPropertyElement;
-import org.hibernate.metamodel.source.spi.BindingContext;
-import org.hibernate.metamodel.source.spi.MappingDefaults;
-import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
 /**
  * @author Gail Badner
  */
 public class HbmSimpleAttributeBindingState extends AbstractHbmAttributeBindingState
 		implements SimpleAttributeBindingState {
 	private final String typeName;
 	private final Map<String, String> typeParameters = new HashMap<String, String>();
 
 	private final boolean isLazy;
 	private final PropertyGeneration propertyGeneration;
 	private final boolean isInsertable;
 	private final boolean isUpdatable;
 
 	public HbmSimpleAttributeBindingState(
 			String ownerClassName,
 			BindingContext bindingContext,
 			MetaAttributeContext parentMetaAttributeContext,
 			XMLId id) {
 		super(
 				ownerClassName,
 				id.getName() != null ? id.getName() : bindingContext.getMappingDefaults().getIdColumnName(),
 				bindingContext,
 				id.getNode(),
 				HbmHelper.extractMetaAttributeContext( id.getMeta(), parentMetaAttributeContext ),
 				HbmHelper.getPropertyAccessorName( id.getAccess(), false, bindingContext.getMappingDefaults().getPropertyAccessorName() ),
 				true
 		);
 
 		this.isLazy = false;
 		if ( id.getTypeAttribute() != null ) {
 			typeName = maybeConvertToTypeDefName( id.getTypeAttribute(), bindingContext.getMappingDefaults() );
 		}
 		else if ( id.getType() != null ) {
 			typeName = maybeConvertToTypeDefName( id.getType().getName(), bindingContext.getMappingDefaults() );
 		}
 		else {
 			typeName = getTypeNameByReflection();
 		}
 
 		// TODO: how should these be set???
 		this.propertyGeneration = PropertyGeneration.parse( null );
 		this.isInsertable = true;
 
 		this.isUpdatable = false;
 	}
 
 	private static String maybeConvertToTypeDefName(String typeName, MappingDefaults defaults) {
 		String actualTypeName = typeName;
 		if ( typeName != null ) {
 			// TODO: tweak for typedef...
 		}
 		else {
 		}
 		return actualTypeName;
 	}
 
 	public HbmSimpleAttributeBindingState(
 			String ownerClassName,
 			BindingContext bindingContext,
 			MetaAttributeContext parentMetaAttributeContext,
 			XMLVersion version) {
 		super(
 				ownerClassName,
 				version.getName(),
 				bindingContext,
 				version.getNode(),
 				HbmHelper.extractMetaAttributeContext( version.getMeta(), parentMetaAttributeContext ),
 				HbmHelper.getPropertyAccessorName( version.getAccess(), false, bindingContext.getMappingDefaults().getPropertyAccessorName() ),
 				true
 		);
 		this.typeName = version.getType() == null ? "integer" : version.getType();
 
 		this.isLazy = false;
 
 		// for version properties marked as being generated, make sure they are "always"
 		// generated; aka, "insert" is invalid; this is dis-allowed by the DTD,
 		// but just to make sure.
 		this.propertyGeneration = PropertyGeneration.parse( version.getGenerated().value() );
 		if ( propertyGeneration == PropertyGeneration.INSERT ) {
 			throw new MappingException( "'generated' attribute cannot be 'insert' for versioning property" );
 		}
 		this.isInsertable = MappingHelper.getBooleanValue( version.isInsert(), true );
 		this.isUpdatable = true;
 	}
 
 	public HbmSimpleAttributeBindingState(
 			String ownerClassName,
 			BindingContext bindingContext,
 			MetaAttributeContext parentMetaAttributeContext,
 			XMLTimestamp timestamp) {
 
 		super(
 				ownerClassName,
 				timestamp.getName(),
 				bindingContext,
 				timestamp.getNode(),
 				HbmHelper.extractMetaAttributeContext( timestamp.getMeta(), parentMetaAttributeContext ),
 				HbmHelper.getPropertyAccessorName( timestamp.getAccess(), false, bindingContext.getMappingDefaults().getPropertyAccessorName() ),
 				true
 		);
 
 		// Timestamp.getType() is not defined
 		this.typeName = "db".equals( timestamp.getSource() ) ? "dbtimestamp" : "timestamp";
 		this.isLazy = false;
 
 		// for version properties marked as being generated, make sure they are "always"
 		// generated; aka, "insert" is invalid; this is dis-allowed by the DTD,
 		// but just to make sure.
 		this.propertyGeneration = PropertyGeneration.parse( timestamp.getGenerated().value() );
 		if ( propertyGeneration == PropertyGeneration.INSERT ) {
 			throw new MappingException( "'generated' attribute cannot be 'insert' for versioning property" );
 		}
 		this.isInsertable = true; //TODO: is this right????
 		this.isUpdatable = true;
 	}
 
 	public HbmSimpleAttributeBindingState(
 			String ownerClassName,
 			BindingContext bindingContext,
 			MetaAttributeContext parentMetaAttributeContext,
 			XMLPropertyElement property) {
 		super(
 				ownerClassName,
 				property.getName(),
 				bindingContext,
 				property.getNode(),
 				HbmHelper.extractMetaAttributeContext( property.getMeta(), parentMetaAttributeContext ),
-				HbmHelper.getPropertyAccessorName( property.getAccess(), false, bindingContext.getMappingDefaults().getPropertyAccessorName() ),
+				HbmHelper.getPropertyAccessorName(
+						property.getAccess(),
+						false,
+						bindingContext.getMappingDefaults().getPropertyAccessorName()
+				),
 				property.isOptimisticLock()
 		);
 		this.isLazy = property.isLazy();
 		this.propertyGeneration = PropertyGeneration.parse( property.getGenerated() );
 
 		if ( propertyGeneration == PropertyGeneration.ALWAYS || propertyGeneration == PropertyGeneration.INSERT ) {
 			// generated properties can *never* be insertable.
 			if ( property.isInsert() != null && property.isInsert() ) {
 				// the user specifically supplied insert="true", which constitutes an illegal combo
 				throw new MappingException(
 						"cannot specify both insert=\"true\" and generated=\"" + propertyGeneration.getName() +
 								"\" for property: " +
 								property.getName()
 				);
 			}
 			isInsertable = false;
 		}
 		else {
 			isInsertable = MappingHelper.getBooleanValue( property.isInsert(), true );
 		}
 		if ( propertyGeneration == PropertyGeneration.ALWAYS ) {
 			if ( property.isUpdate() != null && property.isUpdate() ) {
 				// the user specifically supplied update="true",
 				// which constitutes an illegal combo
 				throw new MappingException(
 						"cannot specify both update=\"true\" and generated=\"" + propertyGeneration.getName() +
 								"\" for property: " +
 								property.getName()
 				);
 			}
 			isUpdatable = false;
 		}
 		else {
 			isUpdatable = MappingHelper.getBooleanValue( property.isUpdate(), true );
 		}
 
 		if ( property.getTypeAttribute() != null ) {
 			typeName = maybeConvertToTypeDefName( property.getTypeAttribute(), bindingContext.getMappingDefaults() );
 		}
 		else if ( property.getType() != null ) {
 			typeName = maybeConvertToTypeDefName( property.getType().getName(), bindingContext.getMappingDefaults() );
 			for ( XMLParamElement typeParameter : property.getType().getParam() ) {
 				//TODO: add parameters from typedef
 				typeParameters.put( typeParameter.getName(), typeParameter.getValue().trim() );
 			}
 		}
 		else {
 			typeName = getTypeNameByReflection();
 		}
 
 
 		// TODO: check for typedef first
 		/*
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
         */
 	}
 
 	protected boolean isEmbedded() {
 		return false;
 	}
 
 	public String getTypeName() {
 		return typeName;
 	}
 
 	public Map<String, String> getTypeParameters() {
 		return typeParameters;
 	}
 
 	public boolean isLazy() {
 		return isLazy;
 	}
 
 	public PropertyGeneration getPropertyGeneration() {
 		return propertyGeneration;
 	}
 
 	public boolean isInsertable() {
 		return isInsertable;
 	}
 
 	public boolean isUpdatable() {
 		return isUpdatable;
 	}
 
 	public Set<CascadeType> getCascadeTypes() {
 		return null;
 	}
 
 	public boolean isKeyCascadeDeleteEnabled() {
 		//TODO: implement
 		return false;
 	}
 
 	public String getUnsavedValue() {
 		//TODO: implement
 		return null;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmColumnRelationalState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmColumnRelationalState.java
index e4680699e8..48b55a3d6e 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmColumnRelationalState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmColumnRelationalState.java
@@ -1,276 +1,276 @@
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
 package org.hibernate.metamodel.source.hbm.state.relational;
 
 import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.metamodel.binder.source.hbm.MappingHelper;
 import org.hibernate.metamodel.relational.Size;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLColumnElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLDiscriminator;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLId;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLTimestamp;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLVersion;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLManyToOneElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLPropertyElement;
-import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.relational.state.ColumnRelationalState;
 
 // TODO: remove duplication after Id, Discriminator, Version, Timestamp, and Property extend a common interface.
 
 /**
  * @author Gail Badner
  */
 public class HbmColumnRelationalState implements ColumnRelationalState {
 	private final HbmSimpleValueRelationalStateContainer container;
 	private final String explicitColumnName;
 	private final Size size;
 	private final boolean isNullable;
 	private final boolean isUnique;
 	private final String checkCondition;
 	private final String defaultColumnValue;
 	private final String sqlType;
 	private final String customWrite;
 	private final String customRead;
 	private final String comment;
 	private final Set<String> uniqueKeys;
 	private final Set<String> indexes;
 
 	/* package-protected */
 	HbmColumnRelationalState(XMLColumnElement columnElement,
 							 HbmSimpleValueRelationalStateContainer container) {
 		this.container = container;
 		this.explicitColumnName = columnElement.getName();
 		this.size = createSize( columnElement.getLength(), columnElement.getScale(), columnElement.getPrecision() );
 		this.isNullable = !MappingHelper.getBooleanValue( columnElement.isNotNull(), true );
 		this.isUnique = MappingHelper.getBooleanValue( columnElement.isUnique(), true );
 		this.checkCondition = columnElement.getCheck();
 		this.defaultColumnValue = columnElement.getDefault();
 		this.sqlType = columnElement.getSqlType();
 		this.customWrite = columnElement.getWrite();
 		if ( customWrite != null && !customWrite.matches( "[^?]*\\?[^?]*" ) ) {
 			throw new MappingException( "write expression must contain exactly one value placeholder ('?') character" );
 		}
 		this.customRead = columnElement.getRead();
 		this.comment = columnElement.getComment() == null ? null : columnElement.getComment().trim();
 		this.uniqueKeys = MappingHelper.getStringValueTokens( columnElement.getUniqueKey(), ", " );
 		this.uniqueKeys.addAll( container.getPropertyUniqueKeys() );
 		this.indexes = MappingHelper.getStringValueTokens( columnElement.getIndex(), ", " );
 		this.indexes.addAll( container.getPropertyIndexes() );
 	}
 
 	HbmColumnRelationalState(XMLPropertyElement property,
 							 HbmSimpleValueRelationalStateContainer container) {
 		this.container = container;
 		this.explicitColumnName = property.getName();
 		this.size = createSize( property.getLength(), property.getScale(), property.getPrecision() );
 		this.isUnique = MappingHelper.getBooleanValue( property.isUnique(), true );
 		this.isNullable = !MappingHelper.getBooleanValue( property.isNotNull(), true );
 		this.checkCondition = null;
 		this.defaultColumnValue = null;
 		this.sqlType = null;
 		this.customWrite = null;
 		this.customRead = null;
 		this.comment = null;
 		this.uniqueKeys = MappingHelper.getStringValueTokens( property.getUniqueKey(), ", " );
 		this.uniqueKeys.addAll( container.getPropertyUniqueKeys() );
 		this.indexes = MappingHelper.getStringValueTokens( property.getIndex(), ", " );
 		this.indexes.addAll( container.getPropertyIndexes() );
 	}
 
 	HbmColumnRelationalState(XMLManyToOneElement manyToOne,
 							 HbmSimpleValueRelationalStateContainer container) {
 		this.container = container;
 		this.explicitColumnName = manyToOne.getName();
 		this.size = new Size();
 		this.isNullable = !MappingHelper.getBooleanValue( manyToOne.isNotNull(), false );
 		this.isUnique = manyToOne.isUnique();
 		this.checkCondition = null;
 		this.defaultColumnValue = null;
 		this.sqlType = null;
 		this.customWrite = null;
 		this.customRead = null;
 		this.comment = null;
 		this.uniqueKeys = MappingHelper.getStringValueTokens( manyToOne.getUniqueKey(), ", " );
 		this.uniqueKeys.addAll( container.getPropertyUniqueKeys() );
 		this.indexes = MappingHelper.getStringValueTokens( manyToOne.getIndex(), ", " );
 		this.indexes.addAll( container.getPropertyIndexes() );
 	}
 
 	HbmColumnRelationalState(XMLId id,
 							 HbmSimpleValueRelationalStateContainer container) {
 		if ( id.getColumn() != null && !id.getColumn().isEmpty() ) {
 			throw new IllegalArgumentException( "This method should not be called with non-empty id.getColumnElement()" );
 		}
 		this.container = container;
 		this.explicitColumnName = id.getName();
 		this.size = createSize( id.getLength(), null, null );
 		this.isNullable = false;
 		this.isUnique = true;
 		this.checkCondition = null;
 		this.defaultColumnValue = null;
 		this.sqlType = null;
 		this.customWrite = null;
 		this.customRead = null;
 		this.comment = null;
 		this.uniqueKeys = container.getPropertyUniqueKeys();
 		this.indexes = container.getPropertyIndexes();
 	}
 
 	HbmColumnRelationalState(XMLDiscriminator discriminator,
 							 HbmSimpleValueRelationalStateContainer container) {
 		if ( discriminator.getColumn() != null ) {
 			throw new IllegalArgumentException(
 					"This method should not be called with null discriminator.getColumnElement()"
 			);
 		}
 		this.container = container;
 		this.explicitColumnName = null;
 		this.size = createSize( discriminator.getLength(), null, null );
 		this.isNullable = false;
 		this.isUnique = true;
 		this.checkCondition = null;
 		this.defaultColumnValue = null;
 		this.sqlType = null;
 		this.customWrite = null;
 		this.customRead = null;
 		this.comment = null;
 		this.uniqueKeys = container.getPropertyUniqueKeys();
 		this.indexes = container.getPropertyIndexes();
 	}
 
 	HbmColumnRelationalState(XMLVersion version,
 							 HbmSimpleValueRelationalStateContainer container) {
 		this.container = container;
 		this.explicitColumnName = version.getColumnAttribute();
 		if ( version.getColumn() != null && !version.getColumn().isEmpty() ) {
 			throw new IllegalArgumentException(
 					"This method should not be called with non-empty version.getColumnElement()"
 			);
 		}
 		// TODO: should set default
 		this.size = new Size();
 		this.isNullable = false;
 		this.isUnique = false;
 		this.checkCondition = null;
 		this.defaultColumnValue = null;
 		this.sqlType = null;
 		this.customWrite = null;
 		this.customRead = null;
 		this.comment = null;
 		this.uniqueKeys = container.getPropertyUniqueKeys();
 		this.indexes = container.getPropertyIndexes();
 	}
 
 	HbmColumnRelationalState(XMLTimestamp timestamp,
 							 HbmSimpleValueRelationalStateContainer container) {
 		this.container = container;
 		this.explicitColumnName = timestamp.getColumn();
 		// TODO: should set default
 		this.size = new Size();
 		this.isNullable = false;
 		this.isUnique = true; // well, it should hopefully be unique...
 		this.checkCondition = null;
 		this.defaultColumnValue = null;
 		this.sqlType = null;
 		this.customWrite = null;
 		this.customRead = null;
 		this.comment = null;
 		this.uniqueKeys = container.getPropertyUniqueKeys();
 		this.indexes = container.getPropertyIndexes();
 	}
 
 	public NamingStrategy getNamingStrategy() {
 		return container.getNamingStrategy();
 	}
 
     public boolean isGloballyQuotedIdentifiers(){
         return  container.isGloballyQuotedIdentifiers();
     }
 
 	public String getExplicitColumnName() {
 		return explicitColumnName;
 	}
 
 	public Size getSize() {
 		return size;
 	}
 
 	protected static Size createSize(String length, String scale, String precision) {
 		// TODO: should this set defaults if length, scale, precision is not specified?
 		Size size = new Size();
 		if ( length != null ) {
 			size.setLength( Integer.parseInt( length ) );
 		}
 		if ( scale != null ) {
 			size.setScale( Integer.parseInt( scale ) );
 		}
 		if ( precision != null ) {
 			size.setPrecision( Integer.parseInt( precision ) );
 		}
 		// TODO: is there an attribute for lobMultiplier?
 		return size;
 	}
 
 	public boolean isNullable() {
 		return isNullable;
 	}
 
 	public boolean isUnique() {
 		return isUnique;
 	}
 
 	public String getCheckCondition() {
 		return checkCondition;
 	}
 
 	public String getDefault() {
 		return defaultColumnValue;
 	}
 
 	public String getSqlType() {
 		return sqlType;
 	}
 
 	public String getCustomWriteFragment() {
 		return customWrite;
 	}
 
 	public String getCustomReadFragment() {
 		return customRead;
 	}
 
 	public String getComment() {
 		return comment;
 	}
 
 	public Set<String> getUniqueKeys() {
 		return uniqueKeys;
 	}
 
 	public Set<String> getIndexes() {
 		return indexes;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmManyToOneRelationalStateContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmManyToOneRelationalStateContainer.java
index 0c9af595c6..fd20ba5f3a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmManyToOneRelationalStateContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmManyToOneRelationalStateContainer.java
@@ -1,55 +1,55 @@
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
 package org.hibernate.metamodel.source.hbm.state.relational;
 
+import org.hibernate.metamodel.binder.source.BindingContext;
 import org.hibernate.metamodel.relational.state.ManyToOneRelationalState;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLManyToOneElement;
-import org.hibernate.metamodel.source.spi.BindingContext;
 
 /**
  * @author Gail Badner
  */
 public class HbmManyToOneRelationalStateContainer extends HbmSimpleValueRelationalStateContainer
 implements ManyToOneRelationalState {
 
 	private final boolean isLogicalOneToOne;
 	private final String foreignKeyName;
 
 	public HbmManyToOneRelationalStateContainer(
 			BindingContext bindingContext,
 			boolean autoColumnCreation,
 			XMLManyToOneElement manyToOne ) {
 		super( bindingContext, autoColumnCreation, manyToOne );
 		this.isLogicalOneToOne = manyToOne.isUnique();
 		this.foreignKeyName = manyToOne.getForeignKey();
 	}
 
 	public boolean isLogicalOneToOne() {
 		return isLogicalOneToOne;
 	}
 
 	public String getForeignKeyName() {
 		return foreignKeyName;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java
index bcca4af761..f2f00445a3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/relational/HbmSimpleValueRelationalStateContainer.java
@@ -1,225 +1,225 @@
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
 package org.hibernate.metamodel.source.hbm.state.relational;
 
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 import java.util.Set;
 
 import org.hibernate.MappingException;
 import org.hibernate.cfg.NamingStrategy;
+import org.hibernate.metamodel.binder.source.BindingContext;
 import org.hibernate.metamodel.binding.HibernateTypeDescriptor;
 import org.hibernate.metamodel.relational.state.SimpleValueRelationalState;
 import org.hibernate.metamodel.relational.state.TupleRelationalState;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLColumnElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLDiscriminator;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLId;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLTimestamp;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLVersion;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLManyToOneElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLPropertyElement;
-import org.hibernate.metamodel.source.spi.BindingContext;
 
 /**
  * @author Gail Badner
  */
 public class HbmSimpleValueRelationalStateContainer implements TupleRelationalState {
 	private final BindingContext bindingContext;
 	private final Set<String> propertyUniqueKeys;
 	private final Set<String> propertyIndexes;
 	private final List<SimpleValueRelationalState> simpleValueStates;
 	private final HibernateTypeDescriptor hibernateTypeDescriptor = new HibernateTypeDescriptor();
 
 	public BindingContext getBindingContext() {
 		return bindingContext;
 	}
     public boolean isGloballyQuotedIdentifiers(){
         return getBindingContext().isGloballyQuotedIdentifiers();
     }
 	public NamingStrategy getNamingStrategy() {
 		return getBindingContext().getNamingStrategy();
 	}
 
 	// TODO: remove duplication after Id, Discriminator, Version, Timestamp, and Property extend a common interface.
 
 	public HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			boolean autoColumnCreation,
 			XMLId id) {
 		this( bindingContext, id.getColumn() );
 		if ( simpleValueStates.isEmpty() ) {
 			if ( id.getColumn() == null && ! autoColumnCreation ) {
 				throw new MappingException( "No columns to map and auto column creation is disabled." );
 			}
 			simpleValueStates.add( new HbmColumnRelationalState( id, this ) );
 		}
 		else if ( id.getColumn() != null ) {
 			throw new MappingException( "column attribute may not be used together with <column> subelement" );
 		}
 		this.hibernateTypeDescriptor.setTypeName( id.getTypeAttribute() );
 	}
 
 	public HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			boolean autoColumnCreation,
 			XMLDiscriminator discriminator) {
 		this( bindingContext, discriminator.getFormula(), discriminator.getColumn() );
 		if ( simpleValueStates.isEmpty() ) {
 			if ( discriminator.getColumn() == null && discriminator.getFormula() == null &&  ! autoColumnCreation ) {
 				throw new MappingException( "No column or formula to map and auto column creation is disabled." );
 			}
 			simpleValueStates.add( new HbmColumnRelationalState( discriminator, this ) );
 		}
 		else if ( discriminator.getColumn() != null || discriminator.getFormula() != null) {
 			throw new MappingException( "column/formula attribute may not be used together with <column>/<formula> subelement" );
 		}
 		this.hibernateTypeDescriptor.setTypeName( discriminator.getType() == null ? "string" : discriminator.getType() );
 	}
 
 	public HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			boolean autoColumnCreation,
 			XMLVersion version) {
 		this( bindingContext, version.getColumn() );
 		if ( simpleValueStates.isEmpty() ) {
 			if ( version.getColumn() == null && ! autoColumnCreation ) {
 				throw new MappingException( "No column or formula to map and auto column creation is disabled." );
 			}
 			simpleValueStates.add( new HbmColumnRelationalState( version, this ) );
 		}
 		else if ( version.getColumn() != null ) {
 			throw new MappingException( "column attribute may not be used together with <column> subelement" );
 		}
 		this.hibernateTypeDescriptor.setTypeName( version.getType() == null ? "integer" : version.getType() );
 	}
 
 	public HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			boolean autoColumnCreation,
 			XMLTimestamp timestamp) {
 		this( bindingContext, null );
 		if ( simpleValueStates.isEmpty() ) {
 			if ( timestamp.getColumn() == null && ! autoColumnCreation ) {
 				throw new MappingException( "No columns to map and auto column creation is disabled." );
 			}
 			simpleValueStates.add( new HbmColumnRelationalState( timestamp, this ) );
 		}
 		else if ( timestamp.getColumn() != null ) {
 			throw new MappingException( "column attribute may not be used together with <column> subelement" );
 		}
 		this.hibernateTypeDescriptor.setTypeName( "db".equals( timestamp.getSource() ) ? "dbtimestamp" : "timestamp" );
 	}
 
 	public HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			boolean autoColumnCreation,
 			XMLPropertyElement property) {
 		this( bindingContext, property.getColumnOrFormula() );
 		if ( simpleValueStates.isEmpty() ) {
 			if ( property.getColumn() == null && property.getFormula() == null &&  ! autoColumnCreation ) {
 				throw new MappingException( "No column or formula to map and auto column creation is disabled." );
 			}
 			simpleValueStates.add( new HbmColumnRelationalState( property, this ) );
 		}
 		else if ( property.getColumn() != null || property.getFormula() != null) {
 			throw new MappingException( "column/formula attribute may not be used together with <column>/<formula> subelement" );
 		}
 		this.hibernateTypeDescriptor.setTypeName( property.getTypeAttribute() );
 	}
 
 	public HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			boolean autoColumnCreation,
 			XMLManyToOneElement manyToOne) {
 		this( bindingContext, manyToOne.getColumnOrFormula() );
 		if ( simpleValueStates.isEmpty() ) {
 			if ( manyToOne.getColumn() == null && manyToOne.getFormula() == null &&  ! autoColumnCreation ) {
 				throw new MappingException( "No column or formula to map and auto column creation is disabled." );
 			}
 			simpleValueStates.add( new HbmColumnRelationalState( manyToOne, this ) );
 		}
 		else if ( manyToOne.getColumn() != null || manyToOne.getFormula() != null) {
 			throw new MappingException( "column/formula attribute may not be used together with <column>/<formula> subelement" );
 		}
 	}
 
 	private HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			String formulaElement,
 			XMLColumnElement columnElement) {
 		this( bindingContext,
 				formulaElement != null
 						? Collections.singletonList( formulaElement )
 						: columnElement != null
 								? Collections.singletonList( columnElement )
 								: Collections.<Object>emptyList()
 		);
 	}
 
 	private HbmSimpleValueRelationalStateContainer(
 			BindingContext bindingContext,
 			List mappedColumnsOrFormulas) {
 		this.bindingContext = bindingContext;
 		this.propertyUniqueKeys = Collections.emptySet();
 		this.propertyIndexes = Collections.emptySet();
 		simpleValueStates = new ArrayList<SimpleValueRelationalState>(
 							mappedColumnsOrFormulas == null || mappedColumnsOrFormulas.isEmpty()
 									? 1
 									: mappedColumnsOrFormulas.size()
 		);
 		if ( mappedColumnsOrFormulas != null && ! mappedColumnsOrFormulas.isEmpty() ) {
 			for ( Object mappedColumnOrFormula : mappedColumnsOrFormulas ) {
 				simpleValueStates.add( createColumnOrFormulaRelationalState( this, mappedColumnOrFormula ) );
 			}
 		}
 	}
 
 	private static SimpleValueRelationalState createColumnOrFormulaRelationalState(
 			HbmSimpleValueRelationalStateContainer container,
 			Object columnOrFormula) {
 		if ( XMLColumnElement.class.isInstance( columnOrFormula ) ) {
 			return new HbmColumnRelationalState(
 					XMLColumnElement.class.cast( columnOrFormula ),
 					container
 			);
 		}
 		else if ( String.class.isInstance( columnOrFormula ) ) {
 			return new HbmDerivedValueRelationalState( String.class.cast( columnOrFormula ) );
 		}
 		throw new MappingException( "unknown type of column or formula: " + columnOrFormula.getClass().getName() );
 	}
 
 	public List<SimpleValueRelationalState> getRelationalStates() {
 		return simpleValueStates;
 	}
 
 	Set<String> getPropertyUniqueKeys() {
 		return propertyUniqueKeys;
 	}
 
 	Set<String> getPropertyIndexes() {
 		return propertyIndexes;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/ExtendsQueue.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/ExtendsQueue.java
deleted file mode 100644
index 33ab5fdeba..0000000000
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/ExtendsQueue.java
+++ /dev/null
@@ -1,99 +0,0 @@
-/*
- * Hibernate, Relational Persistence for Idiomatic Java
- *
- * Copyright (c) 2010, Red Hat Inc. or third-party contributors as
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
-package org.hibernate.metamodel.source.internal;
-
-import java.io.Serializable;
-import java.util.HashSet;
-import java.util.Iterator;
-import java.util.Set;
-
-import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.MappingException;
-import org.hibernate.cfg.ExtendsQueueEntry;
-import org.hibernate.metamodel.source.hbm.HbmHelper;
-
-import org.jboss.logging.Logger;
-
-/**
- * TODO : javadoc
- *
- * @author Steve Ebersole
- */
-public class ExtendsQueue implements Serializable {
-
-    private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, ExtendsQueue.class.getName());
-
-	private final MetadataImpl metadata;
-	private Set<ExtendsQueueEntry> extendsQueue = new HashSet<ExtendsQueueEntry>();
-
-	public ExtendsQueue(MetadataImpl metadata) {
-		this.metadata = metadata;
-	}
-
-	public void add(ExtendsQueueEntry extendsQueueEntry) {
-		extendsQueue.add( extendsQueueEntry );
-	}
-
-	public int processExtendsQueue() {
-		LOG.debug( "processing extends queue" );
-		int added = 0;
-//		ExtendsQueueEntry extendsQueueEntry = findPossibleExtends();
-//		while ( extendsQueueEntry != null ) {
-//			metadata.getMetadataSourceQueue().processHbmXml( extendsQueueEntry.getMetadataXml(), extendsQueueEntry.getEntityNames() );
-//			extendsQueueEntry = findPossibleExtends();
-//		}
-//
-//		if ( extendsQueue.size() > 0 ) {
-//			Iterator iterator = extendsQueue.iterator();
-//			StringBuffer buf = new StringBuffer( "Following super classes referenced in extends not found: " );
-//			while ( iterator.hasNext() ) {
-//				final ExtendsQueueEntry entry = ( ExtendsQueueEntry ) iterator.next();
-//				buf.append( entry.getExplicitName() );
-//				if ( entry.getMappingPackage() != null ) {
-//					buf.append( "[" ).append( entry.getMappingPackage() ).append( "]" );
-//				}
-//				if ( iterator.hasNext() ) {
-//					buf.append( "," );
-//				}
-//			}
-//			throw new MappingException( buf.toString() );
-//		}
-//
-		return added;
-	}
-
-	protected ExtendsQueueEntry findPossibleExtends() {
-		Iterator<ExtendsQueueEntry> itr = extendsQueue.iterator();
-		while ( itr.hasNext() ) {
-			final ExtendsQueueEntry entry = itr.next();
-			boolean found = metadata.getEntityBinding( entry.getExplicitName() ) == null
-					&& metadata.getEntityBinding( HbmHelper.getClassName( entry.getExplicitName(), entry.getMappingPackage() ) ) != null;
-			if ( found ) {
-				itr.remove();
-				return entry;
-			}
-		}
-		return null;
-	}
-}
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
index 71741cd580..3076c610fa 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/AbstractEntityPersister.java
@@ -1,4403 +1,4408 @@
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
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.FetchMode;
 import org.hibernate.HibernateException;
-import org.hibernate.dialect.Dialect;
-import org.hibernate.dialect.function.SQLFunctionRegistry;
+import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.EntityEntry;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.engine.spi.LoadQueryInfluencers;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.engine.spi.ValueInclusion;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.LockMode;
 import org.hibernate.LockOptions;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.StaleObjectStateException;
 import org.hibernate.StaleStateException;
 import org.hibernate.bytecode.instrumentation.internal.FieldInterceptionHelper;
 import org.hibernate.bytecode.instrumentation.spi.FieldInterceptor;
 import org.hibernate.bytecode.instrumentation.spi.LazyPropertyInitializer;
 import org.hibernate.cache.spi.CacheKey;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cache.spi.entry.CacheEntry;
 import org.hibernate.cache.spi.entry.CacheEntryStructure;
 import org.hibernate.cache.spi.entry.StructuredCacheEntry;
 import org.hibernate.cache.spi.entry.UnstructuredCacheEntry;
 import org.hibernate.dialect.lock.LockingStrategy;
 import org.hibernate.engine.spi.CascadeStyle;
 import org.hibernate.engine.spi.CascadingAction;
 import org.hibernate.engine.jdbc.batch.internal.BasicBatchKey;
 import org.hibernate.id.IdentifierGenerator;
 import org.hibernate.id.PostInsertIdentifierGenerator;
 import org.hibernate.id.PostInsertIdentityPersister;
 import org.hibernate.id.insert.Binder;
 import org.hibernate.id.insert.InsertGeneratedIdentifierDelegate;
 import org.hibernate.internal.FilterHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.jdbc.Expectation;
 import org.hibernate.jdbc.Expectations;
 import org.hibernate.jdbc.TooManyRowsAffectedException;
 import org.hibernate.loader.entity.BatchingEntityLoader;
 import org.hibernate.loader.entity.CascadeEntityLoader;
 import org.hibernate.loader.entity.EntityLoader;
 import org.hibernate.loader.entity.UniqueEntityLoader;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.metadata.ClassMetadata;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.relational.DerivedValue;
 import org.hibernate.metamodel.relational.Identifier;
 import org.hibernate.metamodel.relational.SimpleValue;
 import org.hibernate.pretty.MessageHelper;
 import org.hibernate.property.BackrefPropertyAccessor;
 import org.hibernate.sql.Alias;
 import org.hibernate.sql.Delete;
 import org.hibernate.sql.Insert;
 import org.hibernate.sql.JoinFragment;
 import org.hibernate.sql.JoinType;
 import org.hibernate.sql.Select;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.sql.SimpleSelect;
 import org.hibernate.sql.Template;
 import org.hibernate.sql.Update;
 import org.hibernate.tuple.entity.EntityMetamodel;
 import org.hibernate.tuple.entity.EntityTuplizer;
 import org.hibernate.type.AssociationType;
 import org.hibernate.type.CompositeType;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 import org.hibernate.type.TypeHelper;
 import org.hibernate.type.VersionType;
 
 /**
  * Basic functionality for persisting an entity via JDBC
  * through either generated or custom SQL
  *
  * @author Gavin King
  */
 public abstract class AbstractEntityPersister
 		implements OuterJoinLoadable, Queryable, ClassMetadata, UniqueKeyLoadable,
 				   SQLLoadable, LazyPropertyInitializer, PostInsertIdentityPersister, Lockable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class,
                                                                        AbstractEntityPersister.class.getName());
 
 	public static final String ENTITY_CLASS = "class";
 
 	// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	private final SessionFactoryImplementor factory;
 	private final EntityRegionAccessStrategy cacheAccessStrategy;
 	private final boolean isLazyPropertiesCacheable;
 	private final CacheEntryStructure cacheEntryStructure;
 	private final EntityMetamodel entityMetamodel;
 	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	private final String[] rootTableKeyColumnNames;
 	private final String[] rootTableKeyColumnReaders;
 	private final String[] rootTableKeyColumnReaderTemplates;
 	private final String[] identifierAliases;
 	private final int identifierColumnSpan;
 	private final String versionColumnName;
 	private final boolean hasFormulaProperties;
 	private final int batchSize;
 	private final boolean hasSubselectLoadableCollections;
 	protected final String rowIdName;
 
 	private final Set lazyProperties;
 
 	// The optional SQL string defined in the where attribute
 	private final String sqlWhereString;
 	private final String sqlWhereStringTemplate;
 
 	//information about properties of this class,
 	//including inherited properties
 	//(only really needed for updatable/insertable properties)
 	private final int[] propertyColumnSpans;
 	private final String[] propertySubclassNames;
 	private final String[][] propertyColumnAliases;
 	private final String[][] propertyColumnNames;
 	private final String[][] propertyColumnFormulaTemplates;
 	private final String[][] propertyColumnReaderTemplates;
 	private final String[][] propertyColumnWriters;
 	private final boolean[][] propertyColumnUpdateable;
 	private final boolean[][] propertyColumnInsertable;
 	private final boolean[] propertyUniqueness;
 	private final boolean[] propertySelectable;
 
 	//information about lazy properties of this class
 	private final String[] lazyPropertyNames;
 	private final int[] lazyPropertyNumbers;
 	private final Type[] lazyPropertyTypes;
 	private final String[][] lazyPropertyColumnAliases;
 
 	//information about all properties in class hierarchy
 	private final String[] subclassPropertyNameClosure;
 	private final String[] subclassPropertySubclassNameClosure;
 	private final Type[] subclassPropertyTypeClosure;
 	private final String[][] subclassPropertyFormulaTemplateClosure;
 	private final String[][] subclassPropertyColumnNameClosure;
 	private final String[][] subclassPropertyColumnReaderClosure;
 	private final String[][] subclassPropertyColumnReaderTemplateClosure;
 	private final FetchMode[] subclassPropertyFetchModeClosure;
 	private final boolean[] subclassPropertyNullabilityClosure;
 	private final boolean[] propertyDefinedOnSubclass;
 	private final int[][] subclassPropertyColumnNumberClosure;
 	private final int[][] subclassPropertyFormulaNumberClosure;
 	private final CascadeStyle[] subclassPropertyCascadeStyleClosure;
 
 	//information about all columns/formulas in class hierarchy
 	private final String[] subclassColumnClosure;
 	private final boolean[] subclassColumnLazyClosure;
 	private final String[] subclassColumnAliasClosure;
 	private final boolean[] subclassColumnSelectableClosure;
 	private final String[] subclassColumnReaderTemplateClosure;
 	private final String[] subclassFormulaClosure;
 	private final String[] subclassFormulaTemplateClosure;
 	private final String[] subclassFormulaAliasClosure;
 	private final boolean[] subclassFormulaLazyClosure;
 
 	// dynamic filters attached to the class-level
 	private final FilterHelper filterHelper;
 
 	private final Set affectingFetchProfileNames = new HashSet();
 
 	private final Map uniqueKeyLoaders = new HashMap();
 	private final Map lockers = new HashMap();
 	private final Map loaders = new HashMap();
 
 	// SQL strings
 	private String sqlVersionSelectString;
 	private String sqlSnapshotSelectString;
 	private String sqlLazySelectString;
 
 	private String sqlIdentityInsertString;
 	private String sqlUpdateByRowIdString;
 	private String sqlLazyUpdateByRowIdString;
 
 	private String[] sqlDeleteStrings;
 	private String[] sqlInsertStrings;
 	private String[] sqlUpdateStrings;
 	private String[] sqlLazyUpdateStrings;
 
 	private String sqlInsertGeneratedValuesSelectString;
 	private String sqlUpdateGeneratedValuesSelectString;
 
 	//Custom SQL (would be better if these were private)
 	protected boolean[] insertCallable;
 	protected boolean[] updateCallable;
 	protected boolean[] deleteCallable;
 	protected String[] customSQLInsert;
 	protected String[] customSQLUpdate;
 	protected String[] customSQLDelete;
 	protected ExecuteUpdateResultCheckStyle[] insertResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] updateResultCheckStyles;
 	protected ExecuteUpdateResultCheckStyle[] deleteResultCheckStyles;
 
 	private InsertGeneratedIdentifierDelegate identityDelegate;
 
 	private boolean[] tableHasColumns;
 
 	private final String loaderName;
 
 	private UniqueEntityLoader queryLoader;
 
 	private final String temporaryIdTableName;
 	private final String temporaryIdTableDDL;
 
 	private final Map subclassPropertyAliases = new HashMap();
 	private final Map subclassPropertyColumnNames = new HashMap();
 
 	protected final BasicEntityPropertyMapping propertyMapping;
 
 	protected void addDiscriminatorToInsert(Insert insert) {}
 
 	protected void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {}
 
 	protected abstract int[] getSubclassColumnTableNumberClosure();
 
 	protected abstract int[] getSubclassFormulaTableNumberClosure();
 
 	public abstract String getSubclassTableName(int j);
 
 	protected abstract String[] getSubclassTableKeyColumns(int j);
 
 	protected abstract boolean isClassOrSuperclassTable(int j);
 
 	protected abstract int getSubclassTableSpan();
 
 	protected abstract int getTableSpan();
 
 	protected abstract boolean isTableCascadeDeleteEnabled(int j);
 
 	protected abstract String getTableName(int j);
 
 	protected abstract String[] getKeyColumns(int j);
 
 	protected abstract boolean isPropertyOfTable(int property, int j);
 
 	protected abstract int[] getPropertyTableNumbersInSelect();
 
 	protected abstract int[] getPropertyTableNumbers();
 
 	protected abstract int getSubclassPropertyTableNumber(int i);
 
 	protected abstract String filterFragment(String alias) throws MappingException;
 
 	private static final String DISCRIMINATOR_ALIAS = "clazz_";
 
 	public String getDiscriminatorColumnName() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	public String getDiscriminatorColumnReaders() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	public String getDiscriminatorColumnReaderTemplate() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	protected String getDiscriminatorAlias() {
 		return DISCRIMINATOR_ALIAS;
 	}
 
 	protected String getDiscriminatorFormulaTemplate() {
 		return null;
 	}
 
 	protected boolean isInverseTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableTable(int j) {
 		return false;
 	}
 
 	protected boolean isNullableSubclassTable(int j) {
 		return false;
 	}
 
 	protected boolean isInverseSubclassTable(int j) {
 		return false;
 	}
 
 	public boolean isSubclassEntityName(String entityName) {
 		return entityMetamodel.getSubclassEntityNames().contains(entityName);
 	}
 
 	private boolean[] getTableHasColumns() {
 		return tableHasColumns;
 	}
 
 	public String[] getRootTableKeyColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	protected String[] getSQLUpdateByRowIdStrings() {
 		if ( sqlUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan() + 1];
 		result[0] = sqlUpdateByRowIdString;
 		System.arraycopy( sqlUpdateStrings, 0, result, 1, getTableSpan() );
 		return result;
 	}
 
 	protected String[] getSQLLazyUpdateByRowIdStrings() {
 		if ( sqlLazyUpdateByRowIdString == null ) {
 			throw new AssertionFailure( "no update by row id" );
 		}
 		String[] result = new String[getTableSpan()];
 		result[0] = sqlLazyUpdateByRowIdString;
 		for ( int i = 1; i < getTableSpan(); i++ ) {
 			result[i] = sqlLazyUpdateStrings[i];
 		}
 		return result;
 	}
 
 	protected String getSQLSnapshotSelectString() {
 		return sqlSnapshotSelectString;
 	}
 
 	protected String getSQLLazySelectString() {
 		return sqlLazySelectString;
 	}
 
 	protected String[] getSQLDeleteStrings() {
 		return sqlDeleteStrings;
 	}
 
 	protected String[] getSQLInsertStrings() {
 		return sqlInsertStrings;
 	}
 
 	protected String[] getSQLUpdateStrings() {
 		return sqlUpdateStrings;
 	}
 
 	protected String[] getSQLLazyUpdateStrings() {
 		return sqlLazyUpdateStrings;
 	}
 
 	/**
 	 * The query that inserts a row, letting the database generate an id
 	 *
 	 * @return The IDENTITY-based insertion query.
 	 */
 	protected String getSQLIdentityInsertString() {
 		return sqlIdentityInsertString;
 	}
 
 	protected String getVersionSelectString() {
 		return sqlVersionSelectString;
 	}
 
 	protected boolean isInsertCallable(int j) {
 		return insertCallable[j];
 	}
 
 	protected boolean isUpdateCallable(int j) {
 		return updateCallable[j];
 	}
 
 	protected boolean isDeleteCallable(int j) {
 		return deleteCallable[j];
 	}
 
 	protected boolean isSubclassPropertyDeferred(String propertyName, String entityName) {
 		return false;
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return false;
 	}
 
 	public boolean hasSequentialSelect() {
 		return false;
 	}
 
 	/**
 	 * Decide which tables need to be updated.
 	 * <p/>
 	 * The return here is an array of boolean values with each index corresponding
 	 * to a given table in the scope of this persister.
 	 *
 	 * @param dirtyProperties The indices of all the entity properties considered dirty.
 	 * @param hasDirtyCollection Whether any collections owned by the entity which were considered dirty.
 	 *
 	 * @return Array of booleans indicating which table require updating.
 	 */
 	protected boolean[] getTableUpdateNeeded(final int[] dirtyProperties, boolean hasDirtyCollection) {
 
 		if ( dirtyProperties == null ) {
 			return getTableHasColumns(); // for objects that came in via update()
 		}
 		else {
 			boolean[] updateability = getPropertyUpdateability();
 			int[] propertyTableNumbers = getPropertyTableNumbers();
 			boolean[] tableUpdateNeeded = new boolean[ getTableSpan() ];
 			for ( int i = 0; i < dirtyProperties.length; i++ ) {
 				int property = dirtyProperties[i];
 				int table = propertyTableNumbers[property];
 				tableUpdateNeeded[table] = tableUpdateNeeded[table] ||
 						( getPropertyColumnSpan(property) > 0 && updateability[property] );
 			}
 			if ( isVersioned() ) {
 				tableUpdateNeeded[0] = tableUpdateNeeded[0] ||
 					Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
 			}
 			return tableUpdateNeeded;
 		}
 	}
 
 	public boolean hasRowId() {
 		return rowIdName != null;
 	}
 
 	protected boolean[][] getPropertyColumnUpdateable() {
 		return propertyColumnUpdateable;
 	}
 
 	protected boolean[][] getPropertyColumnInsertable() {
 		return propertyColumnInsertable;
 	}
 
 	protected boolean[] getPropertySelectable() {
 		return propertySelectable;
 	}
 
 	public AbstractEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory) throws HibernateException {
 
 		// moved up from AbstractEntityPersister ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		isLazyPropertiesCacheable = persistentClass.isLazyPropertiesCacheable();
 		this.cacheEntryStructure = factory.getSettings().isStructuredCacheEntriesEnabled() ?
 				(CacheEntryStructure) new StructuredCacheEntry(this) :
 				(CacheEntryStructure) new UnstructuredCacheEntry();
 
 		this.entityMetamodel = new EntityMetamodel( persistentClass, factory );
 		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 		int batch = persistentClass.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 		hasSubselectLoadableCollections = persistentClass.hasSubselectLoadableCollections();
 
 		propertyMapping = new BasicEntityPropertyMapping( this );
 
 		// IDENTIFIER
 
 		identifierColumnSpan = persistentClass.getIdentifier().getColumnSpan();
 		rootTableKeyColumnNames = new String[identifierColumnSpan];
 		rootTableKeyColumnReaders = new String[identifierColumnSpan];
 		rootTableKeyColumnReaderTemplates = new String[identifierColumnSpan];
 		identifierAliases = new String[identifierColumnSpan];
 
 		rowIdName = persistentClass.getRootTable().getRowId();
 
 		loaderName = persistentClass.getLoaderName();
 
 		Iterator iter = persistentClass.getIdentifier().getColumnIterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			Column col = ( Column ) iter.next();
 			rootTableKeyColumnNames[i] = col.getQuotedName( factory.getDialect() );
 			rootTableKeyColumnReaders[i] = col.getReadExpr( factory.getDialect() );
 			rootTableKeyColumnReaderTemplates[i] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			identifierAliases[i] = col.getAlias( factory.getDialect(), persistentClass.getRootTable() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( persistentClass.isVersioned() ) {
 			versionColumnName = ( ( Column ) persistentClass.getVersion().getColumnIterator().next() ).getQuotedName( factory.getDialect() );
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( persistentClass.getWhere() ) ? "( " + persistentClass.getWhere() + ") " : null;
 		sqlWhereStringTemplate = sqlWhereString == null ?
 				null :
 				Template.renderWhereStringTemplate( sqlWhereString, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		// PROPERTIES
 
 		final boolean lazyAvailable = isInstrumented();
 
 		int hydrateSpan = entityMetamodel.getPropertySpan();
 		propertyColumnSpans = new int[hydrateSpan];
 		propertySubclassNames = new String[hydrateSpan];
 		propertyColumnAliases = new String[hydrateSpan][];
 		propertyColumnNames = new String[hydrateSpan][];
 		propertyColumnFormulaTemplates = new String[hydrateSpan][];
 		propertyColumnReaderTemplates = new String[hydrateSpan][];
 		propertyColumnWriters = new String[hydrateSpan][];
 		propertyUniqueness = new boolean[hydrateSpan];
 		propertySelectable = new boolean[hydrateSpan];
 		propertyColumnUpdateable = new boolean[hydrateSpan][];
 		propertyColumnInsertable = new boolean[hydrateSpan][];
 		HashSet thisClassProperties = new HashSet();
 
 		lazyProperties = new HashSet();
 		ArrayList lazyNames = new ArrayList();
 		ArrayList lazyNumbers = new ArrayList();
 		ArrayList lazyTypes = new ArrayList();
 		ArrayList lazyColAliases = new ArrayList();
 
 		iter = persistentClass.getPropertyClosureIterator();
 		i = 0;
 		boolean foundFormula = false;
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 			thisClassProperties.add( prop );
 
 			int span = prop.getColumnSpan();
 			propertyColumnSpans[i] = span;
 			propertySubclassNames[i] = prop.getPersistentClass().getEntityName();
 			String[] colNames = new String[span];
 			String[] colAliases = new String[span];
 			String[] colReaderTemplates = new String[span];
 			String[] colWriters = new String[span];
 			String[] formulaTemplates = new String[span];
 			Iterator colIter = prop.getColumnIterator();
 			int k = 0;
 			while ( colIter.hasNext() ) {
 				Selectable thing = ( Selectable ) colIter.next();
 				colAliases[k] = thing.getAlias( factory.getDialect() , prop.getValue().getTable() );
 				if ( thing.isFormula() ) {
 					foundFormula = true;
 					formulaTemplates[k] = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 				}
 				else {
 					Column col = (Column)thing;
 					colNames[k] = col.getQuotedName( factory.getDialect() );
 					colReaderTemplates[k] = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					colWriters[k] = col.getWriteExpr();
 				}
 				k++;
 			}
 			propertyColumnNames[i] = colNames;
 			propertyColumnFormulaTemplates[i] = formulaTemplates;
 			propertyColumnReaderTemplates[i] = colReaderTemplates;
 			propertyColumnWriters[i] = colWriters;
 			propertyColumnAliases[i] = colAliases;
 
 			if ( lazyAvailable && prop.isLazy() ) {
 				lazyProperties.add( prop.getName() );
 				lazyNames.add( prop.getName() );
 				lazyNumbers.add( i );
 				lazyTypes.add( prop.getValue().getType() );
 				lazyColAliases.add( colAliases );
 			}
 
 			propertyColumnUpdateable[i] = prop.getValue().getColumnUpdateability();
 			propertyColumnInsertable[i] = prop.getValue().getColumnInsertability();
 
 			propertySelectable[i] = prop.isSelectable();
 
 			propertyUniqueness[i] = prop.getValue().isAlternateUniqueKey();
 
 			i++;
 
 		}
 		hasFormulaProperties = foundFormula;
 		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
 		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
 		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
 		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );
 
 		// SUBCLASS PROPERTY CLOSURE
 
 		ArrayList columns = new ArrayList();
 		ArrayList columnsLazy = new ArrayList();
 		ArrayList columnReaderTemplates = new ArrayList();
 		ArrayList aliases = new ArrayList();
 		ArrayList formulas = new ArrayList();
 		ArrayList formulaAliases = new ArrayList();
 		ArrayList formulaTemplates = new ArrayList();
 		ArrayList formulasLazy = new ArrayList();
 		ArrayList types = new ArrayList();
 		ArrayList names = new ArrayList();
 		ArrayList classes = new ArrayList();
 		ArrayList templates = new ArrayList();
 		ArrayList propColumns = new ArrayList();
 		ArrayList propColumnReaders = new ArrayList();
 		ArrayList propColumnReaderTemplates = new ArrayList();
 		ArrayList joinedFetchesList = new ArrayList();
 		ArrayList cascades = new ArrayList();
 		ArrayList definedBySubclass = new ArrayList();
 		ArrayList propColumnNumbers = new ArrayList();
 		ArrayList propFormulaNumbers = new ArrayList();
 		ArrayList columnSelectables = new ArrayList();
 		ArrayList propNullables = new ArrayList();
 
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = ( Property ) iter.next();
 			names.add( prop.getName() );
 			classes.add( prop.getPersistentClass().getEntityName() );
 			boolean isDefinedBySubclass = !thisClassProperties.contains( prop );
 			definedBySubclass.add( Boolean.valueOf( isDefinedBySubclass ) );
 			propNullables.add( Boolean.valueOf( prop.isOptional() || isDefinedBySubclass ) ); //TODO: is this completely correct?
 			types.add( prop.getType() );
 
 			Iterator colIter = prop.getColumnIterator();
 			String[] cols = new String[prop.getColumnSpan()];
 			String[] readers = new String[prop.getColumnSpan()];
 			String[] readerTemplates = new String[prop.getColumnSpan()];
 			String[] forms = new String[prop.getColumnSpan()];
 			int[] colnos = new int[prop.getColumnSpan()];
 			int[] formnos = new int[prop.getColumnSpan()];
 			int l = 0;
 			Boolean lazy = Boolean.valueOf( prop.isLazy() && lazyAvailable );
 			while ( colIter.hasNext() ) {
 				Selectable thing = ( Selectable ) colIter.next();
 				if ( thing.isFormula() ) {
 					String template = thing.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					formnos[l] = formulaTemplates.size();
 					colnos[l] = -1;
 					formulaTemplates.add( template );
 					forms[l] = template;
 					formulas.add( thing.getText( factory.getDialect() ) );
 					formulaAliases.add( thing.getAlias( factory.getDialect() ) );
 					formulasLazy.add( lazy );
 				}
 				else {
 					Column col = (Column)thing;
 					String colName = col.getQuotedName( factory.getDialect() );
 					colnos[l] = columns.size(); //before add :-)
 					formnos[l] = -1;
 					columns.add( colName );
 					cols[l] = colName;
 					aliases.add( thing.getAlias( factory.getDialect(), prop.getValue().getTable() ) );
 					columnsLazy.add( lazy );
 					columnSelectables.add( Boolean.valueOf( prop.isSelectable() ) );
 
 					readers[l] = col.getReadExpr( factory.getDialect() );
 					String readerTemplate = col.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 					readerTemplates[l] = readerTemplate;
 					columnReaderTemplates.add( readerTemplate );
 				}
 				l++;
 			}
 			propColumns.add( cols );
 			propColumnReaders.add( readers );
 			propColumnReaderTemplates.add( readerTemplates );
 			templates.add( forms );
 			propColumnNumbers.add( colnos );
 			propFormulaNumbers.add( formnos );
 
 			joinedFetchesList.add( prop.getValue().getFetchMode() );
 			cascades.add( prop.getCascadeStyle() );
 		}
 		subclassColumnClosure = ArrayHelper.toStringArray( columns );
 		subclassColumnAliasClosure = ArrayHelper.toStringArray( aliases );
 		subclassColumnLazyClosure = ArrayHelper.toBooleanArray( columnsLazy );
 		subclassColumnSelectableClosure = ArrayHelper.toBooleanArray( columnSelectables );
 		subclassColumnReaderTemplateClosure = ArrayHelper.toStringArray( columnReaderTemplates );
 
 		subclassFormulaClosure = ArrayHelper.toStringArray( formulas );
 		subclassFormulaTemplateClosure = ArrayHelper.toStringArray( formulaTemplates );
 		subclassFormulaAliasClosure = ArrayHelper.toStringArray( formulaAliases );
 		subclassFormulaLazyClosure = ArrayHelper.toBooleanArray( formulasLazy );
 
 		subclassPropertyNameClosure = ArrayHelper.toStringArray( names );
 		subclassPropertySubclassNameClosure = ArrayHelper.toStringArray( classes );
 		subclassPropertyTypeClosure = ArrayHelper.toTypeArray( types );
 		subclassPropertyNullabilityClosure = ArrayHelper.toBooleanArray( propNullables );
 		subclassPropertyFormulaTemplateClosure = ArrayHelper.to2DStringArray( templates );
 		subclassPropertyColumnNameClosure = ArrayHelper.to2DStringArray( propColumns );
 		subclassPropertyColumnReaderClosure = ArrayHelper.to2DStringArray( propColumnReaders );
 		subclassPropertyColumnReaderTemplateClosure = ArrayHelper.to2DStringArray( propColumnReaderTemplates );
 		subclassPropertyColumnNumberClosure = ArrayHelper.to2DIntArray( propColumnNumbers );
 		subclassPropertyFormulaNumberClosure = ArrayHelper.to2DIntArray( propFormulaNumbers );
 
 		subclassPropertyCascadeStyleClosure = new CascadeStyle[cascades.size()];
 		iter = cascades.iterator();
 		int j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyCascadeStyleClosure[j++] = ( CascadeStyle ) iter.next();
 		}
 		subclassPropertyFetchModeClosure = new FetchMode[joinedFetchesList.size()];
 		iter = joinedFetchesList.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			subclassPropertyFetchModeClosure[j++] = ( FetchMode ) iter.next();
 		}
 
 		propertyDefinedOnSubclass = new boolean[definedBySubclass.size()];
 		iter = definedBySubclass.iterator();
 		j = 0;
 		while ( iter.hasNext() ) {
 			propertyDefinedOnSubclass[j++] = ( ( Boolean ) iter.next() ).booleanValue();
 		}
 
 		// Handle any filters applied to the class level
 		filterHelper = new FilterHelper( persistentClass.getFilterMap(), factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		temporaryIdTableName = persistentClass.getTemporaryIdTableName();
 		temporaryIdTableDDL = persistentClass.getTemporaryIdTableDDL();
 	}
 
 
 	public AbstractEntityPersister(
 			final EntityBinding entityBinding,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory) throws HibernateException {
 		// TODO: Implement! Initializing final fields to make compiler happy
 		this.factory = factory;
 		this.cacheAccessStrategy = cacheAccessStrategy;
 		this.isLazyPropertiesCacheable =
 				entityBinding.getCaching() == null ?
 						false :
 						entityBinding.getCaching().isCacheLazyProperties();
 		this.cacheEntryStructure =
 				factory.getSettings().isStructuredCacheEntriesEnabled() ?
 						new StructuredCacheEntry(this) :
 						new UnstructuredCacheEntry();
 		this.entityMetamodel = new EntityMetamodel( entityBinding, factory );
 		int batch = entityBinding.getBatchSize();
 		if ( batch == -1 ) {
 			batch = factory.getSettings().getDefaultBatchFetchSize();
 		}
 		batchSize = batch;
 		hasSubselectLoadableCollections = entityBinding.hasSubselectLoadableCollections();
 
 		propertyMapping = new BasicEntityPropertyMapping( this );
 
 		// IDENTIFIER
 
 		identifierColumnSpan = entityBinding.getEntityIdentifier().getValueBinding().getValuesSpan();
 		rootTableKeyColumnNames = new String[identifierColumnSpan];
 		rootTableKeyColumnReaders = new String[identifierColumnSpan];
 		rootTableKeyColumnReaderTemplates = new String[identifierColumnSpan];
 		identifierAliases = new String[identifierColumnSpan];
 
 		rowIdName = entityBinding.getRowId();
 
-		loaderName = entityBinding.getLoaderName();
+		loaderName = entityBinding.getCustomLoaderName();
 
 		int i = 0;
 		for ( org.hibernate.metamodel.relational.Column col : entityBinding.getBaseTable().getPrimaryKey().getColumns() ) {
 			rootTableKeyColumnNames[i] = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 			if ( col.getReadFragment() == null ) {
 				rootTableKeyColumnReaders[i] = rootTableKeyColumnNames[i];
 				rootTableKeyColumnReaderTemplates[i] = getTemplateFromColumn( col, factory );
 			}
 			else {
 				rootTableKeyColumnReaders[i] = col.getReadFragment();
 				rootTableKeyColumnReaderTemplates[i] = getTemplateFromString( col.getReadFragment(), factory );
 			}
 			// TODO: Fix when HHH-6337 is fixed; for now assume entityBinding is the root
 			// identifierAliases[i] = col.getAlias( factory.getDialect(), entityBinding.getRootEntityBinding().getBaseTable() );
 			identifierAliases[i] = col.getAlias( factory.getDialect() );
 			i++;
 		}
 
 		// VERSION
 
 		if ( entityBinding.isVersioned() ) {
 			// Use AttributeBinding.getValues() due to HHH-6380
 			Iterator<SimpleValue> valueIterator = entityBinding.getVersioningValueBinding().getValues().iterator();
 			SimpleValue versionValue = valueIterator.next();
 			if ( ! ( versionValue instanceof org.hibernate.metamodel.relational.Column ) || valueIterator.hasNext() ) {
 				throw new MappingException( "Version must be a single column value." );
 			}
 			org.hibernate.metamodel.relational.Column versionColumn =
 					( org.hibernate.metamodel.relational.Column ) versionValue;
 			versionColumnName = versionColumn.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 		}
 		else {
 			versionColumnName = null;
 		}
 
 		//WHERE STRING
 
 		sqlWhereString = StringHelper.isNotEmpty( entityBinding.getWhereFilter() ) ? "( " + entityBinding.getWhereFilter() + ") " : null;
 		sqlWhereStringTemplate = getTemplateFromString( sqlWhereString, factory );
 
 		// PROPERTIES
 
 		final boolean lazyAvailable = isInstrumented();
 
 		int hydrateSpan = entityMetamodel.getPropertySpan();
 		propertyColumnSpans = new int[hydrateSpan];
 		propertySubclassNames = new String[hydrateSpan];
 		propertyColumnAliases = new String[hydrateSpan][];
 		propertyColumnNames = new String[hydrateSpan][];
 		propertyColumnFormulaTemplates = new String[hydrateSpan][];
 		propertyColumnReaderTemplates = new String[hydrateSpan][];
 		propertyColumnWriters = new String[hydrateSpan][];
 		propertyUniqueness = new boolean[hydrateSpan];
 		propertySelectable = new boolean[hydrateSpan];
 		propertyColumnUpdateable = new boolean[hydrateSpan][];
 		propertyColumnInsertable = new boolean[hydrateSpan][];
 		HashSet thisClassProperties = new HashSet();
 
 		lazyProperties = new HashSet();
 		ArrayList lazyNames = new ArrayList();
 		ArrayList lazyNumbers = new ArrayList();
 		ArrayList lazyTypes = new ArrayList();
 		ArrayList lazyColAliases = new ArrayList();
 
 		i = 0;
 		boolean foundFormula = false;
 		for ( AttributeBinding prop : entityBinding.getAttributeBindingClosure() ) {
 			if ( prop == entityBinding.getEntityIdentifier().getValueBinding() ) {
 				// entity identifier is not considered a "normal" property
 				continue;
 			}
 
 			thisClassProperties.add( prop );
 
 			int span = prop.getValuesSpan();
 			propertyColumnSpans[i] = span;
 			propertySubclassNames[i] = prop.getEntityBinding().getEntity().getName();
 			String[] colNames = new String[span];
 			String[] colAliases = new String[span];
 			String[] colReaderTemplates = new String[span];
 			String[] colWriters = new String[span];
 			String[] formulaTemplates = new String[span];
 			int k = 0;
 			for ( SimpleValue thing : prop.getValues() ) {
 				colAliases[k] = thing.getAlias( factory.getDialect() );
 				if ( thing instanceof DerivedValue ) {
 					foundFormula = true;
 					formulaTemplates[ k ] = getTemplateFromString( ( (DerivedValue) thing ).getExpression(), factory );
 				}
 				else {
 					org.hibernate.metamodel.relational.Column col = ( org.hibernate.metamodel.relational.Column ) thing;
 					colNames[k] = col.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 					colReaderTemplates[k] = getTemplateFromColumn( col, factory );
 					colWriters[k] = col.getWriteFragment();
 				}
 				k++;
 			}
 			propertyColumnNames[i] = colNames;
 			propertyColumnFormulaTemplates[i] = formulaTemplates;
 			propertyColumnReaderTemplates[i] = colReaderTemplates;
 			propertyColumnWriters[i] = colWriters;
 			propertyColumnAliases[i] = colAliases;
 
 			if ( lazyAvailable && prop.isLazy() ) {
 				lazyProperties.add( prop.getAttribute().getName() );
 				lazyNames.add( prop.getAttribute().getName() );
 				lazyNumbers.add( i );
 				lazyTypes.add( prop.getHibernateTypeDescriptor().getExplicitType());
 				lazyColAliases.add( colAliases );
 			}
 
 			propertyColumnUpdateable[i] = prop.getColumnUpdateability();
 			propertyColumnInsertable[i] = prop.getColumnInsertability();
 
 			// TODO: fix this when backrefs are working
 			//propertySelectable[i] = prop.isBackRef();
 			propertySelectable[i] = true;
 
 			propertyUniqueness[i] = prop.isAlternateUniqueKey();
 
 			i++;
 
 		}
 		hasFormulaProperties = foundFormula;
 		lazyPropertyColumnAliases = ArrayHelper.to2DStringArray( lazyColAliases );
 		lazyPropertyNames = ArrayHelper.toStringArray( lazyNames );
 		lazyPropertyNumbers = ArrayHelper.toIntArray( lazyNumbers );
 		lazyPropertyTypes = ArrayHelper.toTypeArray( lazyTypes );
 
 		// SUBCLASS PROPERTY CLOSURE
 
 		ArrayList columns = new ArrayList();
 		ArrayList columnsLazy = new ArrayList();
 		ArrayList columnReaderTemplates = new ArrayList();
 		ArrayList aliases = new ArrayList();
 		ArrayList formulas = new ArrayList();
 		ArrayList formulaAliases = new ArrayList();
 		ArrayList formulaTemplates = new ArrayList();
 		ArrayList formulasLazy = new ArrayList();
 		ArrayList types = new ArrayList();
 		ArrayList names = new ArrayList();
 		ArrayList classes = new ArrayList();
 		ArrayList templates = new ArrayList();
 		ArrayList propColumns = new ArrayList();
 		ArrayList propColumnReaders = new ArrayList();
 		ArrayList propColumnReaderTemplates = new ArrayList();
 		ArrayList<FetchMode> joinedFetchesList = new ArrayList<FetchMode>();
 		ArrayList<CascadeStyle> cascades = new ArrayList<CascadeStyle>();
 		ArrayList<Boolean> definedBySubclass = new ArrayList<Boolean>();
 		ArrayList propColumnNumbers = new ArrayList();
 		ArrayList propFormulaNumbers = new ArrayList();
 		ArrayList columnSelectables = new ArrayList();
 		ArrayList propNullables = new ArrayList();
 
 		subclassColumnClosure = ArrayHelper.toStringArray( columns );
 		subclassColumnAliasClosure = ArrayHelper.toStringArray( aliases );
 		subclassColumnLazyClosure = ArrayHelper.toBooleanArray( columnsLazy );
 		subclassColumnSelectableClosure = ArrayHelper.toBooleanArray( columnSelectables );
 		subclassColumnReaderTemplateClosure = ArrayHelper.toStringArray( columnReaderTemplates );
 
 		subclassFormulaClosure = ArrayHelper.toStringArray( formulas );
 		subclassFormulaTemplateClosure = ArrayHelper.toStringArray( formulaTemplates );
 		subclassFormulaAliasClosure = ArrayHelper.toStringArray( formulaAliases );
 		subclassFormulaLazyClosure = ArrayHelper.toBooleanArray( formulasLazy );
 
 		subclassPropertyNameClosure = ArrayHelper.toStringArray( names );
 		subclassPropertySubclassNameClosure = ArrayHelper.toStringArray( classes );
 		subclassPropertyTypeClosure = ArrayHelper.toTypeArray( types );
 		subclassPropertyNullabilityClosure = ArrayHelper.toBooleanArray( propNullables );
 		subclassPropertyFormulaTemplateClosure = ArrayHelper.to2DStringArray( templates );
 		subclassPropertyColumnNameClosure = ArrayHelper.to2DStringArray( propColumns );
 		subclassPropertyColumnReaderClosure = ArrayHelper.to2DStringArray( propColumnReaders );
 		subclassPropertyColumnReaderTemplateClosure = ArrayHelper.to2DStringArray( propColumnReaderTemplates );
 		subclassPropertyColumnNumberClosure = ArrayHelper.to2DIntArray( propColumnNumbers );
 		subclassPropertyFormulaNumberClosure = ArrayHelper.to2DIntArray( propFormulaNumbers );
 
 		subclassPropertyCascadeStyleClosure = cascades.toArray( new CascadeStyle[ cascades.size() ] );
 		subclassPropertyFetchModeClosure = joinedFetchesList.toArray( new FetchMode[ joinedFetchesList.size() ] );
 
 		propertyDefinedOnSubclass = ArrayHelper.toBooleanArray( definedBySubclass );
 
 		Map<String, String> filterDefaultConditionsByName = new HashMap<String, String>();
 		for ( FilterDefinition filterDefinition : entityBinding.getFilterDefinitions() ) {
 			filterDefaultConditionsByName.put( filterDefinition.getFilterName(), filterDefinition.getDefaultFilterCondition() );
 		}
 		filterHelper = new FilterHelper( filterDefaultConditionsByName, factory.getDialect(), factory.getSqlFunctionRegistry() );
 
 		temporaryIdTableName = null;
 		temporaryIdTableDDL = null;
 	}
 
 	protected static String getTemplateFromString(String string, SessionFactoryImplementor factory) {
 		return string == null ?
 				null :
 				Template.renderWhereStringTemplate( string, factory.getDialect(), factory.getSqlFunctionRegistry() );
 	}
 
 	public String getTemplateFromColumn(org.hibernate.metamodel.relational.Column column, SessionFactoryImplementor factory) {
 		String templateString;
 		if ( column.getReadFragment() != null ) {
 			templateString = getTemplateFromString( column.getReadFragment(), factory );
 		}
 		else {
 			String columnName = column.getColumnName().encloseInQuotesIfQuoted( factory.getDialect() );
 			templateString = Template.TEMPLATE + '.' + columnName;
 		}
 		return templateString;
 	}
 
 	protected String generateLazySelectString() {
 
 		if ( !entityMetamodel.hasLazyProperties() ) {
 			return null;
 		}
 
 		HashSet tableNumbers = new HashSet();
 		ArrayList columnNumbers = new ArrayList();
 		ArrayList formulaNumbers = new ArrayList();
 		for ( int i = 0; i < lazyPropertyNames.length; i++ ) {
 			// all this only really needs to consider properties
 			// of this class, not its subclasses, but since we
 			// are reusing code used for sequential selects, we
 			// use the subclass closure
 			int propertyNumber = getSubclassPropertyIndex( lazyPropertyNames[i] );
 
 			int tableNumber = getSubclassPropertyTableNumber( propertyNumber );
 			tableNumbers.add(  tableNumber );
 
 			int[] colNumbers = subclassPropertyColumnNumberClosure[propertyNumber];
 			for ( int j = 0; j < colNumbers.length; j++ ) {
 				if ( colNumbers[j]!=-1 ) {
 					columnNumbers.add( colNumbers[j] );
 				}
 			}
 			int[] formNumbers = subclassPropertyFormulaNumberClosure[propertyNumber];
 			for ( int j = 0; j < formNumbers.length; j++ ) {
 				if ( formNumbers[j]!=-1 ) {
 					formulaNumbers.add( formNumbers[j] );
 				}
 			}
 		}
 
 		if ( columnNumbers.size()==0 && formulaNumbers.size()==0 ) {
 			// only one-to-one is lazy fetched
 			return null;
 		}
 
 		return renderSelect( ArrayHelper.toIntArray( tableNumbers ),
 				ArrayHelper.toIntArray( columnNumbers ),
 				ArrayHelper.toIntArray( formulaNumbers ) );
 
 	}
 
 	public Object initializeLazyProperty(String fieldName, Object entity, SessionImplementor session)
 			throws HibernateException {
 
 		final Serializable id = session.getContextEntityIdentifier( entity );
 
 		final EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 		if ( entry == null ) {
 			throw new HibernateException( "entity is not associated with the session: " + id );
 		}
 
         if ( LOG.isTraceEnabled() ) {
 			LOG.trace(
 					"Initializing lazy properties of: " +
 							MessageHelper.infoString( this, id, getFactory() ) +
 							", field access: " + fieldName
 			);
 		}
 
 		if ( hasCache() ) {
 			CacheKey cacheKey = session.generateCacheKey( id, getIdentifierType(), getEntityName() );
 			Object ce = getCacheAccessStrategy().get( cacheKey, session.getTimestamp() );
 			if (ce!=null) {
 				CacheEntry cacheEntry = (CacheEntry) getCacheEntryStructure().destructure(ce, factory);
 				if ( !cacheEntry.areLazyPropertiesUnfetched() ) {
 					//note early exit here:
 					return initializeLazyPropertiesFromCache( fieldName, entity, session, entry, cacheEntry );
 				}
 			}
 		}
 
 		return initializeLazyPropertiesFromDatastore( fieldName, entity, session, id, entry );
 
 	}
 
 	private Object initializeLazyPropertiesFromDatastore(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Serializable id,
 			final EntityEntry entry) {
 
         if (!hasLazyProperties()) throw new AssertionFailure("no lazy properties");
 
         LOG.trace("Initializing lazy properties from datastore");
 
 		try {
 
 			Object result = null;
 			PreparedStatement ps = null;
 			try {
 				final String lazySelect = getSQLLazySelectString();
 				ResultSet rs = null;
 				try {
 					if ( lazySelect != null ) {
 						// null sql means that the only lazy properties
 						// are shared PK one-to-one associations which are
 						// handled differently in the Type#nullSafeGet code...
 						ps = session.getTransactionCoordinator()
 								.getJdbcCoordinator()
 								.getStatementPreparer()
 								.prepareStatement( lazySelect );
 						getIdentifierType().nullSafeSet( ps, id, 1, session );
 						rs = ps.executeQuery();
 						rs.next();
 					}
 					final Object[] snapshot = entry.getLoadedState();
 					for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 						Object propValue = lazyPropertyTypes[j].nullSafeGet( rs, lazyPropertyColumnAliases[j], session, entity );
 						if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 							result = propValue;
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						rs.close();
 					}
 				}
 			}
 			finally {
 				if ( ps != null ) {
 					ps.close();
 				}
 			}
 
             LOG.trace("Done initializing lazy properties");
 
 			return result;
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not initialize lazy properties: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getSQLLazySelectString()
 				);
 		}
 	}
 
 	private Object initializeLazyPropertiesFromCache(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final EntityEntry entry,
 			final CacheEntry cacheEntry
 	) {
 
         LOG.trace("Initializing lazy properties from second-level cache");
 
 		Object result = null;
 		Serializable[] disassembledValues = cacheEntry.getDisassembledState();
 		final Object[] snapshot = entry.getLoadedState();
 		for ( int j = 0; j < lazyPropertyNames.length; j++ ) {
 			final Object propValue = lazyPropertyTypes[j].assemble(
 					disassembledValues[ lazyPropertyNumbers[j] ],
 					session,
 					entity
 				);
 			if ( initializeLazyProperty( fieldName, entity, session, snapshot, j, propValue ) ) {
 				result = propValue;
 			}
 		}
 
         LOG.trace("Done initializing lazy properties");
 
 		return result;
 	}
 
 	private boolean initializeLazyProperty(
 			final String fieldName,
 			final Object entity,
 			final SessionImplementor session,
 			final Object[] snapshot,
 			final int j,
 			final Object propValue) {
 		setPropertyValue( entity, lazyPropertyNumbers[j], propValue );
 		if ( snapshot != null ) {
 			// object have been loaded with setReadOnly(true); HHH-2236
 			snapshot[ lazyPropertyNumbers[j] ] = lazyPropertyTypes[j].deepCopy( propValue, factory );
 		}
 		return fieldName.equals( lazyPropertyNames[j] );
 	}
 
 	public boolean isBatchable() {
-		return optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_NONE ||
-			( !isVersioned() && optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_VERSION ) ||
-			getFactory().getSettings().isJdbcBatchVersionedData();
+		return optimisticLockStyle() == OptimisticLockStyle.NONE
+				|| ( !isVersioned() && optimisticLockStyle() == OptimisticLockStyle.VERSION )
+				|| getFactory().getSettings().isJdbcBatchVersionedData();
 	}
 
 	public Serializable[] getQuerySpaces() {
 		return getPropertySpaces();
 	}
 
 	protected Set getLazyProperties() {
 		return lazyProperties;
 	}
 
 	public boolean isBatchLoadable() {
 		return batchSize > 1;
 	}
 
 	public String[] getIdentifierColumnNames() {
 		return rootTableKeyColumnNames;
 	}
 
 	public String[] getIdentifierColumnReaders() {
 		return rootTableKeyColumnReaders;
 	}
 
 	public String[] getIdentifierColumnReaderTemplates() {
 		return rootTableKeyColumnReaderTemplates;
 	}
 
 	protected int getIdentifierColumnSpan() {
 		return identifierColumnSpan;
 	}
 
 	protected String[] getIdentifierAliases() {
 		return identifierAliases;
 	}
 
 	public String getVersionColumnName() {
 		return versionColumnName;
 	}
 
 	protected String getVersionedTableName() {
 		return getTableName( 0 );
 	}
 
 	protected boolean[] getSubclassColumnLazyiness() {
 		return subclassColumnLazyClosure;
 	}
 
 	protected boolean[] getSubclassFormulaLazyiness() {
 		return subclassFormulaLazyClosure;
 	}
 
 	/**
 	 * We can't immediately add to the cache if we have formulas
 	 * which must be evaluated, or if we have the possibility of
 	 * two concurrent updates to the same item being merged on
 	 * the database. This can happen if (a) the item is not
 	 * versioned and either (b) we have dynamic update enabled
 	 * or (c) we have multiple tables holding the state of the
 	 * item.
 	 */
 	public boolean isCacheInvalidationRequired() {
 		return hasFormulaProperties() ||
 				( !isVersioned() && ( entityMetamodel.isDynamicUpdate() || getTableSpan() > 1 ) );
 	}
 
 	public boolean isLazyPropertiesCacheable() {
 		return isLazyPropertiesCacheable;
 	}
 
 	public String selectFragment(String alias, String suffix) {
 		return identifierSelectFragment( alias, suffix ) +
 				propertySelectFragment( alias, suffix, false );
 	}
 
 	public String[] getIdentifierAliases(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getIdentiferColumnNames() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return new Alias( suffix ).toAliasStrings( getIdentifierAliases() );
 	}
 
 	public String[] getPropertyAliases(String suffix, int i) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		return new Alias( suffix ).toUnquotedAliasStrings( propertyColumnAliases[i] );
 	}
 
 	public String getDiscriminatorAlias(String suffix) {
 		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
 		// was toUnqotedAliasStrings( getdiscriminatorColumnName() ) before - now tried
 		// to remove that unqoting and missing aliases..
 		return entityMetamodel.hasSubclasses() ?
 				new Alias( suffix ).toAliasString( getDiscriminatorAlias() ) :
 				null;
 	}
 
 	public String identifierSelectFragment(String name, String suffix) {
 		return new SelectFragment()
 				.setSuffix( suffix )
 				.addColumns( name, getIdentifierColumnNames(), getIdentifierAliases() )
 				.toFragmentString()
 				.substring( 2 ); //strip leading ", "
 	}
 
 
 	public String propertySelectFragment(String tableAlias, String suffix, boolean allProperties) {
 		return propertySelectFragmentFragment( tableAlias, suffix, allProperties ).toFragmentString();
 	}
 
 	public SelectFragment propertySelectFragmentFragment(
 			String tableAlias,
 			String suffix,
 			boolean allProperties) {
 		SelectFragment select = new SelectFragment()
 				.setSuffix( suffix )
 				.setUsedAliases( getIdentifierAliases() );
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < getSubclassColumnClosure().length; i++ ) {
 			boolean selectable = ( allProperties || !subclassColumnLazyClosure[i] ) &&
 				!isSubclassTableSequentialSelect( columnTableNumbers[i] ) &&
 				subclassColumnSelectableClosure[i];
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, columnTableNumbers[i] );
 				select.addColumnTemplate( subalias, columnReaderTemplates[i], columnAliases[i] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < getSubclassFormulaTemplateClosure().length; i++ ) {
 			boolean selectable = ( allProperties || !subclassFormulaLazyClosure[i] )
 				&& !isSubclassTableSequentialSelect( formulaTableNumbers[i] );
 			if ( selectable ) {
 				String subalias = generateTableAlias( tableAlias, formulaTableNumbers[i] );
 				select.addFormula( subalias, formulaTemplates[i], formulaAliases[i] );
 			}
 		}
 
 		if ( entityMetamodel.hasSubclasses() ) {
 			addDiscriminatorToSelect( select, tableAlias, suffix );
 		}
 
 		if ( hasRowId() ) {
 			select.addColumn( tableAlias, rowIdName, ROWID_ALIAS );
 		}
 
 		return select;
 	}
 
 	public Object[] getDatabaseSnapshot(Serializable id, SessionImplementor session)
 			throws HibernateException {
 
         if (LOG.isTraceEnabled()) LOG.trace("Getting current persistent state for: "
                                             + MessageHelper.infoString(this, id, getFactory()));
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getSQLSnapshotSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				//if ( isVersioned() ) getVersionType().nullSafeSet( ps, version, getIdentifierColumnSpan()+1, session );
 				ResultSet rs = ps.executeQuery();
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					//otherwise return the "hydrated" state (ie. associations are not resolved)
 					Type[] types = getPropertyTypes();
 					Object[] values = new Object[types.length];
 					boolean[] includeProperty = getPropertyUpdateability();
 					for ( int i = 0; i < types.length; i++ ) {
 						if ( includeProperty[i] ) {
 							values[i] = types[i].hydrate( rs, getPropertyAliases( "", i ), session, null ); //null owner ok??
 						}
 					}
 					return values;
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				ps.close();
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 			        getSQLSnapshotSelectString()
 			);
 		}
 
 	}
 
 	/**
 	 * Generate the SQL that selects the version number by id
 	 */
 	protected String generateSelectVersionString() {
 		SimpleSelect select = new SimpleSelect( getFactory().getDialect() )
 				.setTableName( getVersionedTableName() );
 		if ( isVersioned() ) {
 			select.addColumn( versionColumnName );
 		}
 		else {
 			select.addColumns( rootTableKeyColumnNames );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get version " + getEntityName() );
 		}
 		return select.addCondition( rootTableKeyColumnNames, "=?" ).toStatementString();
 	}
 
 	public boolean[] getPropertyUniqueness() {
 		return propertyUniqueness;
 	}
 
 	protected String generateInsertGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( getPropertyInsertGenerationInclusions() );
 	}
 
 	protected String generateUpdateGeneratedValuesSelectString() {
 		return generateGeneratedValuesSelectString( getPropertyUpdateGenerationInclusions() );
 	}
 
 	private String generateGeneratedValuesSelectString(ValueInclusion[] inclusions) {
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get generated state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 
 		// Here we render the select column list based on the properties defined as being generated.
 		// For partial component generation, we currently just re-select the whole component
 		// rather than trying to handle the individual generated portions.
 		String selectClause = concretePropertySelectFragment( getRootAlias(), inclusions );
 		selectClause = selectClause.substring( 2 );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuffer()
 			.append( StringHelper.join( "=? and ", aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	protected static interface InclusionChecker {
 		public boolean includeProperty(int propertyNumber);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final ValueInclusion[] inclusions) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					// TODO : currently we really do not handle ValueInclusion.PARTIAL...
 					// ValueInclusion.PARTIAL would indicate parts of a component need to
 					// be included in the select; currently we then just render the entire
 					// component into the select clause in that case.
 					public boolean includeProperty(int propertyNumber) {
 						return inclusions[propertyNumber] != ValueInclusion.NONE;
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, final boolean[] includeProperty) {
 		return concretePropertySelectFragment(
 				alias,
 				new InclusionChecker() {
 					public boolean includeProperty(int propertyNumber) {
 						return includeProperty[propertyNumber];
 					}
 				}
 		);
 	}
 
 	protected String concretePropertySelectFragment(String alias, InclusionChecker inclusionChecker) {
 		int propertyCount = getPropertyNames().length;
 		int[] propertyTableNumbers = getPropertyTableNumbersInSelect();
 		SelectFragment frag = new SelectFragment();
 		for ( int i = 0; i < propertyCount; i++ ) {
 			if ( inclusionChecker.includeProperty( i ) ) {
 				frag.addColumnTemplates(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnReaderTemplates[i],
 						propertyColumnAliases[i]
 				);
 				frag.addFormulas(
 						generateTableAlias( alias, propertyTableNumbers[i] ),
 						propertyColumnFormulaTemplates[i],
 						propertyColumnAliases[i]
 				);
 			}
 		}
 		return frag.toFragmentString();
 	}
 
 	protected String generateSnapshotSelectString() {
 
 		//TODO: should we use SELECT .. FOR UPDATE?
 
 		Select select = new Select( getFactory().getDialect() );
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current state " + getEntityName() );
 		}
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String selectClause = StringHelper.join( ", ", aliasedIdColumns ) +
 				concretePropertySelectFragment( getRootAlias(), getPropertyUpdateability() );
 
 		String fromClause = fromTableFragment( getRootAlias() ) +
 				fromJoinFragment( getRootAlias(), true, false );
 
 		String whereClause = new StringBuffer()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		/*if ( isVersioned() ) {
 			where.append(" and ")
 				.append( getVersionColumnName() )
 				.append("=?");
 		}*/
 
 		return select.setSelectClause( selectClause )
 				.setFromClause( fromClause )
 				.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 	}
 
 	public Object forceVersionIncrement(Serializable id, Object currentVersion, SessionImplementor session) {
 		if ( !isVersioned() ) {
 			throw new AssertionFailure( "cannot force version increment on non-versioned entity" );
 		}
 
 		if ( isVersionPropertyGenerated() ) {
 			// the difficulty here is exactly what do we update in order to
 			// force the version to be incremented in the db...
 			throw new HibernateException( "LockMode.FORCE is currently not supported for generated version properties" );
 		}
 
 		Object nextVersion = getVersionType().next( currentVersion, session );
         if (LOG.isTraceEnabled()) LOG.trace("Forcing version increment [" + MessageHelper.infoString(this, id, getFactory()) + "; "
                                             + getVersionType().toLoggableString(currentVersion, getFactory()) + " -> "
                                             + getVersionType().toLoggableString(nextVersion, getFactory()) + "]");
 
 		// todo : cache this sql...
 		String versionIncrementString = generateVersionIncrementUpdateString();
 		PreparedStatement st = null;
 		try {
 			st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( versionIncrementString, false );
 			try {
 				getVersionType().nullSafeSet( st, nextVersion, 1, session );
 				getIdentifierType().nullSafeSet( st, id, 2, session );
 				getVersionType().nullSafeSet( st, currentVersion, 2 + getIdentifierColumnSpan(), session );
 				int rows = st.executeUpdate();
 				if ( rows != 1 ) {
 					throw new StaleObjectStateException( getEntityName(), id );
 				}
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not retrieve version: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 				);
 		}
 
 		return nextVersion;
 	}
 
 	private String generateVersionIncrementUpdateString() {
 		Update update = new Update( getFactory().getDialect() );
 		update.setTableName( getTableName( 0 ) );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "forced version increment" );
 		}
 		update.addColumn( getVersionColumnName() );
 		update.addPrimaryKeyColumns( getIdentifierColumnNames() );
 		update.setVersionColumnName( getVersionColumnName() );
 		return update.toStatementString();
 	}
 
 	/**
 	 * Retrieve the version number
 	 */
 	public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
 
         if (LOG.isTraceEnabled()) LOG.trace("Getting version: " + MessageHelper.infoString(this, id, getFactory()));
 
 		try {
 			PreparedStatement st = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( getVersionSelectString() );
 			try {
 				getIdentifierType().nullSafeSet( st, id, 1, session );
 				ResultSet rs = st.executeQuery();
 				try {
 					if ( !rs.next() ) {
 						return null;
 					}
 					if ( !isVersioned() ) {
 						return this;
 					}
 					return getVersionType().nullSafeGet( rs, getVersionColumnName(), session, null );
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				st.close();
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve version: " + MessageHelper.infoString( this, id, getFactory() ),
 					getVersionSelectString()
 			);
 		}
 	}
 
 	protected void initLockers() {
 		lockers.put( LockMode.READ, generateLocker( LockMode.READ ) );
 		lockers.put( LockMode.UPGRADE, generateLocker( LockMode.UPGRADE ) );
 		lockers.put( LockMode.UPGRADE_NOWAIT, generateLocker( LockMode.UPGRADE_NOWAIT ) );
 		lockers.put( LockMode.FORCE, generateLocker( LockMode.FORCE ) );
 		lockers.put( LockMode.PESSIMISTIC_READ, generateLocker( LockMode.PESSIMISTIC_READ ) );
 		lockers.put( LockMode.PESSIMISTIC_WRITE, generateLocker( LockMode.PESSIMISTIC_WRITE ) );
 		lockers.put( LockMode.PESSIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.PESSIMISTIC_FORCE_INCREMENT ) );
 		lockers.put( LockMode.OPTIMISTIC, generateLocker( LockMode.OPTIMISTIC ) );
 		lockers.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, generateLocker( LockMode.OPTIMISTIC_FORCE_INCREMENT ) );
 	}
 
 	protected LockingStrategy generateLocker(LockMode lockMode) {
 		return factory.getDialect().getLockingStrategy( this, lockMode );
 	}
 
 	private LockingStrategy getLocker(LockMode lockMode) {
 		return ( LockingStrategy ) lockers.get( lockMode );
 	}
 
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockMode lockMode,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockMode ).lock( id, version, object, LockOptions.WAIT_FOREVER, session );
 	}
 
 	public void lock(
 			Serializable id,
 	        Object version,
 	        Object object,
 	        LockOptions lockOptions,
 	        SessionImplementor session) throws HibernateException {
 		getLocker( lockOptions.getLockMode() ).lock( id, version, object, lockOptions.getTimeOut(), session );
 	}
 
 	public String getRootTableName() {
 		return getSubclassTableName( 0 );
 	}
 
 	public String getRootTableAlias(String drivingAlias) {
 		return drivingAlias;
 	}
 
 	public String[] getRootTableIdentifierColumnNames() {
 		return getRootTableKeyColumnNames();
 	}
 
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		return propertyMapping.toColumns( alias, propertyName );
 	}
 
 	public String[] toColumns(String propertyName) throws QueryException {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	public Type toType(String propertyName) throws QueryException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public String[] getPropertyColumnNames(String propertyName) {
 		return propertyMapping.getColumnNames( propertyName );
 	}
 
 	/**
 	 * Warning:
 	 * When there are duplicated property names in the subclasses
 	 * of the class, this method may return the wrong table
 	 * number for the duplicated subclass property (note that
 	 * SingleTableEntityPersister defines an overloaded form
 	 * which takes the entity name.
 	 */
 	public int getSubclassPropertyTableNumber(String propertyPath) {
 		String rootPropertyName = StringHelper.root(propertyPath);
 		Type type = propertyMapping.toType(rootPropertyName);
 		if ( type.isAssociationType() ) {
 			AssociationType assocType = ( AssociationType ) type;
 			if ( assocType.useLHSPrimaryKey() ) {
 				// performance op to avoid the array search
 				return 0;
 			}
 			else if ( type.isCollectionType() ) {
 				// properly handle property-ref-based associations
 				rootPropertyName = assocType.getLHSPropertyName();
 			}
 		}
 		//Enable for HHH-440, which we don't like:
 		/*if ( type.isComponentType() && !propertyName.equals(rootPropertyName) ) {
 			String unrooted = StringHelper.unroot(propertyName);
 			int idx = ArrayHelper.indexOf( getSubclassColumnClosure(), unrooted );
 			if ( idx != -1 ) {
 				return getSubclassColumnTableNumberClosure()[idx];
 			}
 		}*/
 		int index = ArrayHelper.indexOf( getSubclassPropertyNameClosure(), rootPropertyName); //TODO: optimize this better!
 		return index==-1 ? 0 : getSubclassPropertyTableNumber(index);
 	}
 
 	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
 		int tableIndex = getSubclassPropertyTableNumber( propertyPath );
 		if ( tableIndex == 0 ) {
 			return Declarer.CLASS;
 		}
 		else if ( isClassOrSuperclassTable( tableIndex ) ) {
 			return Declarer.SUPERCLASS;
 		}
 		else {
 			return Declarer.SUBCLASS;
 		}
 	}
 
 	private DiscriminatorMetadata discriminatorMetadata;
 
 	public DiscriminatorMetadata getTypeDiscriminatorMetadata() {
 		if ( discriminatorMetadata == null ) {
 			discriminatorMetadata = buildTypeDiscriminatorMetadata();
 		}
 		return discriminatorMetadata;
 	}
 
 	private DiscriminatorMetadata buildTypeDiscriminatorMetadata() {
 		return new DiscriminatorMetadata() {
 			public String getSqlFragment(String sqlQualificationAlias) {
 				return toColumns( sqlQualificationAlias, ENTITY_CLASS )[0];
 			}
 
 			public Type getResolutionType() {
 				return new DiscriminatorType( getDiscriminatorType(), AbstractEntityPersister.this );
 			}
 		};
 	}
 
 	protected String generateTableAlias(String rootAlias, int tableNumber) {
 		if ( tableNumber == 0 ) {
 			return rootAlias;
 		}
 		StringBuffer buf = new StringBuffer().append( rootAlias );
 		if ( !rootAlias.endsWith( "_" ) ) {
 			buf.append( '_' );
 		}
 		return buf.append( tableNumber ).append( '_' ).toString();
 	}
 
 	public String[] toColumns(String name, final int i) {
 		final String alias = generateTableAlias( name, getSubclassPropertyTableNumber( i ) );
 		String[] cols = getSubclassPropertyColumnNames( i );
 		String[] templates = getSubclassPropertyFormulaTemplateClosure()[i];
 		String[] result = new String[cols.length];
 		for ( int j = 0; j < cols.length; j++ ) {
 			if ( cols[j] == null ) {
 				result[j] = StringHelper.replace( templates[j], Template.TEMPLATE, alias );
 			}
 			else {
 				result[j] = StringHelper.qualify( alias, cols[j] );
 			}
 		}
 		return result;
 	}
 
 	private int getSubclassPropertyIndex(String propertyName) {
 		return ArrayHelper.indexOf(subclassPropertyNameClosure, propertyName);
 	}
 
 	protected String[] getPropertySubclassNames() {
 		return propertySubclassNames;
 	}
 
 	public String[] getPropertyColumnNames(int i) {
 		return propertyColumnNames[i];
 	}
 
 	public String[] getPropertyColumnWriters(int i) {
 		return propertyColumnWriters[i];
 	}
 
 	protected int getPropertyColumnSpan(int i) {
 		return propertyColumnSpans[i];
 	}
 
 	protected boolean hasFormulaProperties() {
 		return hasFormulaProperties;
 	}
 
 	public FetchMode getFetchMode(int i) {
 		return subclassPropertyFetchModeClosure[i];
 	}
 
 	public CascadeStyle getCascadeStyle(int i) {
 		return subclassPropertyCascadeStyleClosure[i];
 	}
 
 	public Type getSubclassPropertyType(int i) {
 		return subclassPropertyTypeClosure[i];
 	}
 
 	public String getSubclassPropertyName(int i) {
 		return subclassPropertyNameClosure[i];
 	}
 
 	public int countSubclassProperties() {
 		return subclassPropertyTypeClosure.length;
 	}
 
 	public String[] getSubclassPropertyColumnNames(int i) {
 		return subclassPropertyColumnNameClosure[i];
 	}
 
 	public boolean isDefinedOnSubclass(int i) {
 		return propertyDefinedOnSubclass[i];
 	}
 
 	protected String[][] getSubclassPropertyFormulaTemplateClosure() {
 		return subclassPropertyFormulaTemplateClosure;
 	}
 
 	protected Type[] getSubclassPropertyTypeClosure() {
 		return subclassPropertyTypeClosure;
 	}
 
 	protected String[][] getSubclassPropertyColumnNameClosure() {
 		return subclassPropertyColumnNameClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderClosure() {
 		return subclassPropertyColumnReaderClosure;
 	}
 
 	public String[][] getSubclassPropertyColumnReaderTemplateClosure() {
 		return subclassPropertyColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassPropertyNameClosure() {
 		return subclassPropertyNameClosure;
 	}
 
 	protected String[] getSubclassPropertySubclassNameClosure() {
 		return subclassPropertySubclassNameClosure;
 	}
 
 	protected String[] getSubclassColumnClosure() {
 		return subclassColumnClosure;
 	}
 
 	protected String[] getSubclassColumnAliasClosure() {
 		return subclassColumnAliasClosure;
 	}
 
 	public String[] getSubclassColumnReaderTemplateClosure() {
 		return subclassColumnReaderTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaClosure() {
 		return subclassFormulaClosure;
 	}
 
 	protected String[] getSubclassFormulaTemplateClosure() {
 		return subclassFormulaTemplateClosure;
 	}
 
 	protected String[] getSubclassFormulaAliasClosure() {
 		return subclassFormulaAliasClosure;
 	}
 
 	public String[] getSubclassPropertyColumnAliases(String propertyName, String suffix) {
 		String rawAliases[] = ( String[] ) subclassPropertyAliases.get( propertyName );
 
 		if ( rawAliases == null ) {
 			return null;
 		}
 
 		String result[] = new String[rawAliases.length];
 		for ( int i = 0; i < rawAliases.length; i++ ) {
 			result[i] = new Alias( suffix ).toUnquotedAliasString( rawAliases[i] );
 		}
 		return result;
 	}
 
 	public String[] getSubclassPropertyColumnNames(String propertyName) {
 		//TODO: should we allow suffixes on these ?
 		return ( String[] ) subclassPropertyColumnNames.get( propertyName );
 	}
 
 
 
 	//This is really ugly, but necessary:
 	/**
 	 * Must be called by subclasses, at the end of their constructors
 	 */
 	protected void initSubclassPropertyAliasesMap(PersistentClass model) throws MappingException {
 
 		// ALIASES
 		internalInitSubclassPropertyAliasesMap( null, model.getSubclassPropertyClosureIterator() );
 
 		// aliases for identifier ( alias.id ); skip if the entity defines a non-id property named 'id'
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			subclassPropertyAliases.put( ENTITY_ID, getIdentifierAliases() );
 			subclassPropertyColumnNames.put( ENTITY_ID, getIdentifierColumnNames() );
 		}
 
 		// aliases named identifier ( alias.idname )
 		if ( hasIdentifierProperty() ) {
 			subclassPropertyAliases.put( getIdentifierPropertyName(), getIdentifierAliases() );
 			subclassPropertyColumnNames.put( getIdentifierPropertyName(), getIdentifierColumnNames() );
 		}
 
 		// aliases for composite-id's
 		if ( getIdentifierType().isComponentType() ) {
 			// Fetch embedded identifiers propertynames from the "virtual" identifier component
 			CompositeType componentId = ( CompositeType ) getIdentifierType();
 			String[] idPropertyNames = componentId.getPropertyNames();
 			String[] idAliases = getIdentifierAliases();
 			String[] idColumnNames = getIdentifierColumnNames();
 
 			for ( int i = 0; i < idPropertyNames.length; i++ ) {
 				if ( entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 					subclassPropertyAliases.put(
 							ENTITY_ID + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							ENTITY_ID + "." + getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 //				if (hasIdentifierProperty() && !ENTITY_ID.equals( getIdentifierPropertyName() ) ) {
 				if ( hasIdentifierProperty() ) {
 					subclassPropertyAliases.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 				else {
 					// embedded composite ids ( alias.idname1, alias.idname2 )
 					subclassPropertyAliases.put( idPropertyNames[i], new String[] { idAliases[i] } );
 					subclassPropertyColumnNames.put( idPropertyNames[i],  new String[] { idColumnNames[i] } );
 				}
 			}
 		}
 
 		if ( entityMetamodel.isPolymorphic() ) {
 			subclassPropertyAliases.put( ENTITY_CLASS, new String[] { getDiscriminatorAlias() } );
 			subclassPropertyColumnNames.put( ENTITY_CLASS, new String[] { getDiscriminatorColumnName() } );
 		}
 
 	}
 
 	/**
 	 * Must be called by subclasses, at the end of their constructors
 	 */
 	protected void initSubclassPropertyAliasesMap(EntityBinding model) throws MappingException {
 
 		// ALIASES
 
 		// TODO: Fix when subclasses are working (HHH-6337)
 		//internalInitSubclassPropertyAliasesMap( null, model.getSubclassPropertyClosureIterator() );
 
 		// aliases for identifier ( alias.id ); skip if the entity defines a non-id property named 'id'
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			subclassPropertyAliases.put( ENTITY_ID, getIdentifierAliases() );
 			subclassPropertyColumnNames.put( ENTITY_ID, getIdentifierColumnNames() );
 		}
 
 		// aliases named identifier ( alias.idname )
 		if ( hasIdentifierProperty() ) {
 			subclassPropertyAliases.put( getIdentifierPropertyName(), getIdentifierAliases() );
 			subclassPropertyColumnNames.put( getIdentifierPropertyName(), getIdentifierColumnNames() );
 		}
 
 		// aliases for composite-id's
 		if ( getIdentifierType().isComponentType() ) {
 			// Fetch embedded identifiers propertynames from the "virtual" identifier component
 			CompositeType componentId = ( CompositeType ) getIdentifierType();
 			String[] idPropertyNames = componentId.getPropertyNames();
 			String[] idAliases = getIdentifierAliases();
 			String[] idColumnNames = getIdentifierColumnNames();
 
 			for ( int i = 0; i < idPropertyNames.length; i++ ) {
 				if ( entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 					subclassPropertyAliases.put(
 							ENTITY_ID + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							ENTITY_ID + "." + getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 //				if (hasIdentifierProperty() && !ENTITY_ID.equals( getIdentifierPropertyName() ) ) {
 				if ( hasIdentifierProperty() ) {
 					subclassPropertyAliases.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idAliases[i] }
 					);
 					subclassPropertyColumnNames.put(
 							getIdentifierPropertyName() + "." + idPropertyNames[i],
 							new String[] { idColumnNames[i] }
 					);
 				}
 				else {
 					// embedded composite ids ( alias.idname1, alias.idname2 )
 					subclassPropertyAliases.put( idPropertyNames[i], new String[] { idAliases[i] } );
 					subclassPropertyColumnNames.put( idPropertyNames[i],  new String[] { idColumnNames[i] } );
 				}
 			}
 		}
 
 		if ( entityMetamodel.isPolymorphic() ) {
 			subclassPropertyAliases.put( ENTITY_CLASS, new String[] { getDiscriminatorAlias() } );
 			subclassPropertyColumnNames.put( ENTITY_CLASS, new String[] { getDiscriminatorColumnName() } );
 		}
 
 	}
 
 	private void internalInitSubclassPropertyAliasesMap(String path, Iterator propertyIterator) {
 		while ( propertyIterator.hasNext() ) {
 
 			Property prop = ( Property ) propertyIterator.next();
 			String propname = path == null ? prop.getName() : path + "." + prop.getName();
 			if ( prop.isComposite() ) {
 				Component component = ( Component ) prop.getValue();
 				Iterator compProps = component.getPropertyIterator();
 				internalInitSubclassPropertyAliasesMap( propname, compProps );
 			}
 			else {
 				String[] aliases = new String[prop.getColumnSpan()];
 				String[] cols = new String[prop.getColumnSpan()];
 				Iterator colIter = prop.getColumnIterator();
 				int l = 0;
 				while ( colIter.hasNext() ) {
 					Selectable thing = ( Selectable ) colIter.next();
 					aliases[l] = thing.getAlias( getFactory().getDialect(), prop.getValue().getTable() );
 					cols[l] = thing.getText( getFactory().getDialect() ); // TODO: skip formulas?
 					l++;
 				}
 
 				subclassPropertyAliases.put( propname, aliases );
 				subclassPropertyColumnNames.put( propname, cols );
 			}
 		}
 
 	}
 
 	public Object loadByUniqueKey(
 			String propertyName,
 			Object uniqueKey,
 			SessionImplementor session) throws HibernateException {
 		return getAppropriateUniqueKeyLoader( propertyName, session ).loadByUniqueKey( session, uniqueKey );
 	}
 
 	private EntityLoader getAppropriateUniqueKeyLoader(String propertyName, SessionImplementor session) {
 		final boolean useStaticLoader = !session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& !session.getLoadQueryInfluencers().hasEnabledFetchProfiles()
 				&& propertyName.indexOf('.')<0; //ugly little workaround for fact that createUniqueKeyLoaders() does not handle component properties
 
 		if ( useStaticLoader ) {
 			return ( EntityLoader ) uniqueKeyLoaders.get( propertyName );
 		}
 		else {
 			return createUniqueKeyLoader(
 					propertyMapping.toType( propertyName ),
 					propertyMapping.toColumns( propertyName ),
 					session.getLoadQueryInfluencers()
 			);
 		}
 	}
 
 	public int getPropertyIndex(String propertyName) {
 		return entityMetamodel.getPropertyIndex(propertyName);
 	}
 
 	protected void createUniqueKeyLoaders() throws MappingException {
 		Type[] propertyTypes = getPropertyTypes();
 		String[] propertyNames = getPropertyNames();
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( propertyUniqueness[i] ) {
 				//don't need filters for the static loaders
 				uniqueKeyLoaders.put(
 						propertyNames[i],
 						createUniqueKeyLoader(
 								propertyTypes[i],
 								getPropertyColumnNames( i ),
 								LoadQueryInfluencers.NONE
 						)
 				);
 				//TODO: create uk loaders for component properties
 			}
 		}
 	}
 
 	private EntityLoader createUniqueKeyLoader(
 			Type uniqueKeyType,
 			String[] columns,
 			LoadQueryInfluencers loadQueryInfluencers) {
 		if ( uniqueKeyType.isEntityType() ) {
 			String className = ( ( EntityType ) uniqueKeyType ).getAssociatedEntityName();
 			uniqueKeyType = getFactory().getEntityPersister( className ).getIdentifierType();
 		}
 		return new EntityLoader(
 				this,
 				columns,
 				uniqueKeyType,
 				1,
 				LockMode.NONE,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected String getSQLWhereString(String alias) {
 		return StringHelper.replace( sqlWhereStringTemplate, Template.TEMPLATE, alias );
 	}
 
 	protected boolean hasWhere() {
 		return sqlWhereString != null;
 	}
 
 	private void initOrdinaryPropertyPaths(Mapping mapping) throws MappingException {
 		for ( int i = 0; i < getSubclassPropertyNameClosure().length; i++ ) {
 			propertyMapping.initPropertyPaths( getSubclassPropertyNameClosure()[i],
 					getSubclassPropertyTypeClosure()[i],
 					getSubclassPropertyColumnNameClosure()[i],
 					getSubclassPropertyColumnReaderClosure()[i],
 					getSubclassPropertyColumnReaderTemplateClosure()[i],
 					getSubclassPropertyFormulaTemplateClosure()[i],
 					mapping );
 		}
 	}
 
 	private void initIdentifierPropertyPaths(Mapping mapping) throws MappingException {
 		String idProp = getIdentifierPropertyName();
 		if ( idProp != null ) {
 			propertyMapping.initPropertyPaths( idProp, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 		if ( entityMetamodel.getIdentifierProperty().isEmbedded() ) {
 			propertyMapping.initPropertyPaths( null, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 		if ( ! entityMetamodel.hasNonIdentifierPropertyNamedId() ) {
 			propertyMapping.initPropertyPaths( ENTITY_ID, getIdentifierType(), getIdentifierColumnNames(),
 					getIdentifierColumnReaders(), getIdentifierColumnReaderTemplates(), null, mapping );
 		}
 	}
 
 	private void initDiscriminatorPropertyPath(Mapping mapping) throws MappingException {
 		propertyMapping.initPropertyPaths( ENTITY_CLASS,
 				getDiscriminatorType(),
 				new String[]{getDiscriminatorColumnName()},
 				new String[]{getDiscriminatorColumnReaders()},
 				new String[]{getDiscriminatorColumnReaderTemplate()},
 				new String[]{getDiscriminatorFormulaTemplate()},
 				getFactory() );
 	}
 
 	protected void initPropertyPaths(Mapping mapping) throws MappingException {
 		initOrdinaryPropertyPaths(mapping);
 		initOrdinaryPropertyPaths(mapping); //do two passes, for collection property-ref!
 		initIdentifierPropertyPaths(mapping);
 		if ( entityMetamodel.isPolymorphic() ) {
 			initDiscriminatorPropertyPath( mapping );
 		}
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockMode lockMode,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoader.createBatchingEntityLoader(
 				this,
 				batchSize,
 				lockMode,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected UniqueEntityLoader createEntityLoader(
 			LockOptions lockOptions,
 			LoadQueryInfluencers loadQueryInfluencers) throws MappingException {
 		//TODO: disable batch loading if lockMode > READ?
 		return BatchingEntityLoader.createBatchingEntityLoader(
 				this,
 				batchSize,
 			lockOptions,
 				getFactory(),
 				loadQueryInfluencers
 		);
 	}
 
 	protected UniqueEntityLoader createEntityLoader(LockMode lockMode) throws MappingException {
 		return createEntityLoader( lockMode, LoadQueryInfluencers.NONE );
 	}
 
 	protected boolean check(int rows, Serializable id, int tableNumber, Expectation expectation, PreparedStatement statement) throws HibernateException {
 		try {
 			expectation.verifyOutcome( rows, statement, -1 );
 		}
 		catch( StaleStateException e ) {
 			if ( !isNullableTable( tableNumber ) ) {
 				if ( getFactory().getStatistics().isStatisticsEnabled() ) {
 					getFactory().getStatisticsImplementor()
 							.optimisticFailure( getEntityName() );
 				}
 				throw new StaleObjectStateException( getEntityName(), id );
 			}
 			return false;
 		}
 		catch( TooManyRowsAffectedException e ) {
 			throw new HibernateException(
 					"Duplicate identifier in table for: " +
 					MessageHelper.infoString( this, id, getFactory() )
 			);
 		}
 		catch ( Throwable t ) {
 			return false;
 		}
 		return true;
 	}
 
 	protected String generateUpdateString(boolean[] includeProperty, int j, boolean useRowId) {
 		return generateUpdateString( includeProperty, j, null, useRowId );
 	}
 
 	/**
 	 * Generate the SQL that updates a row by id (and version)
 	 */
 	protected String generateUpdateString(final boolean[] includeProperty,
 										  final int j,
 										  final Object[] oldFields,
 										  final boolean useRowId) {
 
 		Update update = new Update( getFactory().getDialect() ).setTableName( getTableName( j ) );
 
 		// select the correct row by either pk or rowid
 		if ( useRowId ) {
 			update.addPrimaryKeyColumns( new String[]{rowIdName} ); //TODO: eventually, rowIdName[j]
 		}
 		else {
 			update.addPrimaryKeyColumns( getKeyColumns( j ) );
 		}
 
 		boolean hasColumns = false;
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this is a property of the table, which we are updating
 				update.addColumns( getPropertyColumnNames(i), propertyColumnUpdateable[i], propertyColumnWriters[i] );
 				hasColumns = hasColumns || getPropertyColumnSpan( i ) > 0;
 			}
 		}
 
-		if ( j == 0 && isVersioned() && entityMetamodel.getOptimisticLockMode() == Versioning.OPTIMISTIC_LOCK_VERSION ) {
+		if ( j == 0 && isVersioned() && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 			// this is the root (versioned) table, and we are using version-based
 			// optimistic locking;  if we are not updating the version, also don't
 			// check it (unless this is a "generated" version column)!
 			if ( checkVersion( includeProperty ) ) {
 				update.setVersionColumnName( getVersionColumnName() );
 				hasColumns = true;
 			}
 		}
-		else if ( entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION && oldFields != null ) {
+		else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 			// we are using "all" or "dirty" property-based optimistic locking
 
-			boolean[] includeInWhere = entityMetamodel.getOptimisticLockMode() == Versioning.OPTIMISTIC_LOCK_ALL ?
-					getPropertyUpdateability() : //optimistic-lock="all", include all updatable properties
-					includeProperty; //optimistic-lock="dirty", include all properties we are updating this time
+			boolean[] includeInWhere = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
+					? getPropertyUpdateability() //optimistic-lock="all", include all updatable properties
+					: includeProperty; 			 //optimistic-lock="dirty", include all properties we are updating this time
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				boolean include = includeInWhere[i] &&
 						isPropertyOfTable( i, j ) &&
 						versionability[i];
 				if ( include ) {
 					// this property belongs to the table, and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					String[] propertyColumnWriters = getPropertyColumnWriters( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( oldFields[i], getFactory() );
 					for ( int k=0; k<propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							update.addWhereColumn( propertyColumnNames[k], "=" + propertyColumnWriters[k] );
 						}
 						else {
 							update.addWhereColumn( propertyColumnNames[k], " is null" );
 						}
 					}
 				}
 			}
 
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			update.setComment( "update " + getEntityName() );
 		}
 
 		return hasColumns ? update.toStatementString() : null;
 	}
 
 	private boolean checkVersion(final boolean[] includeProperty) {
         return includeProperty[ getVersionProperty() ] ||
 				entityMetamodel.getPropertyUpdateGenerationInclusions()[ getVersionProperty() ] != ValueInclusion.NONE;
 	}
 
 	protected String generateInsertString(boolean[] includeProperty, int j) {
 		return generateInsertString( false, includeProperty, j );
 	}
 
 	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty) {
 		return generateInsertString( identityInsert, includeProperty, 0 );
 	}
 
 	/**
 	 * Generate the SQL that inserts a row
 	 */
 	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty, int j) {
 
 		// todo : remove the identityInsert param and variations;
 		//   identity-insert strings are now generated from generateIdentityInsertString()
 
 		Insert insert = new Insert( getFactory().getDialect() )
 				.setTableName( getTableName( j ) );
 
 		// add normal properties
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		if ( j == 0 ) {
 			addDiscriminatorToInsert( insert );
 		}
 
 		// add the primary key
 		if ( j == 0 && identityInsert ) {
 			insert.addIdentityColumn( getKeyColumns( 0 )[0] );
 		}
 		else {
 			insert.addColumns( getKeyColumns( j ) );
 		}
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 
 		String result = insert.toStatementString();
 
 		// append the SQL to return the generated identifier
 		if ( j == 0 && identityInsert && useInsertSelectIdentity() ) { //TODO: suck into Insert
 			result = getFactory().getDialect().appendIdentitySelectToInsert( result );
 		}
 
 		return result;
 	}
 
 	/**
 	 * Used to generate an insery statement against the root table in the
 	 * case of identifier generation strategies where the insert statement
 	 * executions actually generates the identifier value.
 	 *
 	 * @param includeProperty indices of the properties to include in the
 	 * insert statement.
 	 * @return The insert SQL statement string
 	 */
 	protected String generateIdentityInsertString(boolean[] includeProperty) {
 		Insert insert = identityDelegate.prepareIdentifierGeneratingInsert();
 		insert.setTableName( getTableName( 0 ) );
 
 		// add normal properties
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, 0 ) ) {
 				// this property belongs on the table and is to be inserted
 				insert.addColumns( getPropertyColumnNames(i), propertyColumnInsertable[i], propertyColumnWriters[i] );
 			}
 		}
 
 		// add the discriminator
 		addDiscriminatorToInsert( insert );
 
 		// delegate already handles PK columns
 
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			insert.setComment( "insert " + getEntityName() );
 		}
 
 		return insert.toStatementString();
 	}
 
 	/**
 	 * Generate the SQL that deletes a row by id (and version)
 	 */
 	protected String generateDeleteString(int j) {
 		Delete delete = new Delete()
 				.setTableName( getTableName( j ) )
 				.addPrimaryKeyColumns( getKeyColumns( j ) );
 		if ( j == 0 ) {
 			delete.setVersionColumnName( getVersionColumnName() );
 		}
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			delete.setComment( "delete " + getEntityName() );
 		}
 		return delete.toStatementString();
 	}
 
 	protected int dehydrate(
 			Serializable id,
 			Object[] fields,
 			boolean[] includeProperty,
 			boolean[][] includeColumns,
 			int j,
 			PreparedStatement st,
 			SessionImplementor session) throws HibernateException, SQLException {
 		return dehydrate( id, fields, null, includeProperty, includeColumns, j, st, session, 1 );
 	}
 
 	/**
 	 * Marshall the fields of a persistent instance to a prepared statement
 	 */
 	protected int dehydrate(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final boolean[][] includeColumns,
 	        final int j,
 	        final PreparedStatement ps,
 	        final SessionImplementor session,
 	        int index) throws SQLException, HibernateException {
 
         if (LOG.isTraceEnabled()) LOG.trace("Dehydrating entity: " + MessageHelper.infoString(this, id, getFactory()));
 
 		for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 			if ( includeProperty[i] && isPropertyOfTable( i, j ) ) {
 				getPropertyTypes()[i].nullSafeSet( ps, fields[i], index, includeColumns[i], session );
 				//index += getPropertyColumnSpan( i );
 				index += ArrayHelper.countTrue( includeColumns[i] ); //TODO:  this is kinda slow...
 			}
 		}
 
 		if ( rowId != null ) {
 			ps.setObject( index, rowId );
 			index += 1;
 		}
 		else if ( id != null ) {
 			getIdentifierType().nullSafeSet( ps, id, index, session );
 			index += getIdentifierColumnSpan();
 		}
 
 		return index;
 
 	}
 
 	/**
 	 * Unmarshall the fields of a persistent instance from a result set,
 	 * without resolving associations or collections. Question: should
 	 * this really be here, or should it be sent back to Loader?
 	 */
 	public Object[] hydrate(
 			final ResultSet rs,
 	        final Serializable id,
 	        final Object object,
 	        final Loadable rootLoadable,
 	        final String[][] suffixedPropertyColumns,
 	        final boolean allProperties,
 	        final SessionImplementor session) throws SQLException, HibernateException {
 
         if (LOG.isTraceEnabled()) LOG.trace("Hydrating entity: " + MessageHelper.infoString(this, id, getFactory()));
 
 		final AbstractEntityPersister rootPersister = (AbstractEntityPersister) rootLoadable;
 
 		final boolean hasDeferred = rootPersister.hasSequentialSelect();
 		PreparedStatement sequentialSelect = null;
 		ResultSet sequentialResultSet = null;
 		boolean sequentialSelectEmpty = false;
 		try {
 
 			if ( hasDeferred ) {
 				final String sql = rootPersister.getSequentialSelect( getEntityName() );
 				if ( sql != null ) {
 					//TODO: I am not so sure about the exception handling in this bit!
 					sequentialSelect = session.getTransactionCoordinator()
 							.getJdbcCoordinator()
 							.getStatementPreparer()
 							.prepareStatement( sql );
 					rootPersister.getIdentifierType().nullSafeSet( sequentialSelect, id, 1, session );
 					sequentialResultSet = sequentialSelect.executeQuery();
 					if ( !sequentialResultSet.next() ) {
 						// TODO: Deal with the "optional" attribute in the <join> mapping;
 						// this code assumes that optional defaults to "true" because it
 						// doesn't actually seem to work in the fetch="join" code
 						//
 						// Note that actual proper handling of optional-ality here is actually
 						// more involved than this patch assumes.  Remember that we might have
 						// multiple <join/> mappings associated with a single entity.  Really
 						// a couple of things need to happen to properly handle optional here:
 						//  1) First and foremost, when handling multiple <join/>s, we really
 						//      should be using the entity root table as the driving table;
 						//      another option here would be to choose some non-optional joined
 						//      table to use as the driving table.  In all likelihood, just using
 						//      the root table is much simplier
 						//  2) Need to add the FK columns corresponding to each joined table
 						//      to the generated select list; these would then be used when
 						//      iterating the result set to determine whether all non-optional
 						//      data is present
 						// My initial thoughts on the best way to deal with this would be
 						// to introduce a new SequentialSelect abstraction that actually gets
 						// generated in the persisters (ok, SingleTable...) and utilized here.
 						// It would encapsulated all this required optional-ality checking...
 						sequentialSelectEmpty = true;
 					}
 				}
 			}
 
 			final String[] propNames = getPropertyNames();
 			final Type[] types = getPropertyTypes();
 			final Object[] values = new Object[types.length];
 			final boolean[] laziness = getPropertyLaziness();
 			final String[] propSubclassNames = getSubclassPropertySubclassNameClosure();
 
 			for ( int i = 0; i < types.length; i++ ) {
 				if ( !propertySelectable[i] ) {
 					values[i] = BackrefPropertyAccessor.UNKNOWN;
 				}
 				else if ( allProperties || !laziness[i] ) {
 					//decide which ResultSet to get the property value from:
 					final boolean propertyIsDeferred = hasDeferred &&
 							rootPersister.isSubclassPropertyDeferred( propNames[i], propSubclassNames[i] );
 					if ( propertyIsDeferred && sequentialSelectEmpty ) {
 						values[i] = null;
 					}
 					else {
 						final ResultSet propertyResultSet = propertyIsDeferred ? sequentialResultSet : rs;
 						final String[] cols = propertyIsDeferred ? propertyColumnAliases[i] : suffixedPropertyColumns[i];
 						values[i] = types[i].hydrate( propertyResultSet, cols, session, object );
 					}
 				}
 				else {
 					values[i] = LazyPropertyInitializer.UNFETCHED_PROPERTY;
 				}
 			}
 
 			if ( sequentialResultSet != null ) {
 				sequentialResultSet.close();
 			}
 
 			return values;
 
 		}
 		finally {
 			if ( sequentialSelect != null ) {
 				sequentialSelect.close();
 			}
 		}
 	}
 
 	protected boolean useInsertSelectIdentity() {
 		return !useGetGeneratedKeys() && getFactory().getDialect().supportsInsertSelectIdentity();
 	}
 
 	protected boolean useGetGeneratedKeys() {
 		return getFactory().getSettings().isGetGeneratedKeysEnabled();
 	}
 
 	protected String getSequentialSelect(String entityName) {
 		throw new UnsupportedOperationException("no sequential selects");
 	}
 
 	/**
 	 * Perform an SQL INSERT, and then retrieve a generated identifier.
 	 * <p/>
 	 * This form is used for PostInsertIdentifierGenerator-style ids (IDENTITY,
 	 * select, etc).
 	 */
 	protected Serializable insert(
 			final Object[] fields,
 	        final boolean[] notNull,
 	        String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
         if (LOG.isTraceEnabled()) {
             LOG.trace("Inserting entity: " + getEntityName() + " (native id)");
             if (isVersioned()) LOG.trace("Version: " + Versioning.getVersion(fields, this));
 		}
 
 		Binder binder = new Binder() {
 			public void bindValues(PreparedStatement ps) throws SQLException {
 				dehydrate( null, fields, notNull, propertyColumnInsertable, 0, ps, session );
 			}
 			public Object getEntity() {
 				return object;
 			}
 		};
 		return identityDelegate.performInsert( sql, session, binder );
 	}
 
 	public String getIdentitySelectString() {
 		//TODO: cache this in an instvar
 		return getFactory().getDialect().getIdentitySelectString(
 				getTableName(0),
 				getKeyColumns(0)[0],
 				getIdentifierType().sqlTypes( getFactory() )[0]
 		);
 	}
 
 	public String getSelectByUniqueKeyString(String propertyName) {
 		return new SimpleSelect( getFactory().getDialect() )
 			.setTableName( getTableName(0) )
 			.addColumns( getKeyColumns(0) )
 			.addCondition( getPropertyColumnNames(propertyName), "=?" )
 			.toStatementString();
 	}
 
 	private BasicBatchKey inserBatchKey;
 
 	/**
 	 * Perform an SQL INSERT.
 	 * <p/>
 	 * This for is used for all non-root tables as well as the root table
 	 * in cases where the identifier value is known before the insert occurs.
 	 */
 	protected void insert(
 			final Serializable id,
 	        final Object[] fields,
 	        final boolean[] notNull,
 	        final int j,
 	        final String sql,
 	        final Object object,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		//note: it is conceptually possible that a UserType could map null to
 		//	  a non-null value, so the following is arguable:
 		if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 			return;
 		}
 
         if (LOG.isTraceEnabled()) {
             LOG.trace("Inserting entity: " + MessageHelper.infoString(this, id, getFactory()));
             if (j == 0 && isVersioned()) LOG.trace("Version: " + Versioning.getVersion(fields, this));
 		}
 
 		// TODO : shouldn't inserts be Expectations.NONE?
 		final Expectation expectation = Expectations.appropriateExpectation( insertResultCheckStyles[j] );
 		// we can't batch joined inserts, *especially* not if it is an identity insert;
 		// nor can we batch statements where the expectation is based on an output param
 		final boolean useBatch = j == 0 && expectation.canBeBatched();
 		if ( useBatch && inserBatchKey == null ) {
 			inserBatchKey = new BasicBatchKey(
 					getEntityName() + "#INSERT",
 					expectation
 			);
 		}
 		final boolean callable = isInsertCallable( j );
 
 		try {
 			// Render the SQL query
 			final PreparedStatement insert;
 			if ( useBatch ) {
 				insert = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( inserBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				insert = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				int index = 1;
 				index += expectation.prepare( insert );
 
 				// Write the values of fields onto the prepared statement - we MUST use the state at the time the
 				// insert was issued (cos of foreign key constraints). Not necessarily the object's current state
 
 				dehydrate( id, fields, null, notNull, propertyColumnInsertable, j, insert, session, index );
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( inserBatchKey ).addToBatch();
 				}
 				else {
 					expectation.verifyOutcome( insert.executeUpdate(), insert, -1 );
 				}
 
 			}
 			catch ( SQLException e ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw e;
 			}
 			finally {
 				if ( !useBatch ) {
 					insert.close();
 				}
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not insert: " + MessageHelper.infoString( this ),
 					sql
 			);
 		}
 
 	}
 
 	/**
 	 * Perform an SQL UPDATE or SQL INSERT
 	 */
 	protected void updateOrInsert(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		if ( !isInverseTable( j ) ) {
 
 			final boolean isRowToUpdate;
 			if ( isNullableTable( j ) && oldFields != null && isAllNull( oldFields, j ) ) {
 				//don't bother trying to update, we know there is no row there yet
 				isRowToUpdate = false;
 			}
 			else if ( isNullableTable( j ) && isAllNull( fields, j ) ) {
 				//if all fields are null, we might need to delete existing row
 				isRowToUpdate = true;
 				delete( id, oldVersion, j, object, getSQLDeleteStrings()[j], session, null );
 			}
 			else {
 				//there is probably a row there, so try to update
 				//if no rows were updated, we will find out
 				isRowToUpdate = update( id, fields, oldFields, rowId, includeProperty, j, oldVersion, object, sql, session );
 			}
 
 			if ( !isRowToUpdate && !isAllNull( fields, j ) ) {
 				// assume that the row was not there since it previously had only null
 				// values, so do an INSERT instead
 				//TODO: does not respect dynamic-insert
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 
 		}
 
 	}
 
 	private BasicBatchKey updateBatchKey;
 
 	protected boolean update(
 			final Serializable id,
 	        final Object[] fields,
 	        final Object[] oldFields,
 	        final Object rowId,
 	        final boolean[] includeProperty,
 	        final int j,
 	        final Object oldVersion,
 	        final Object object,
 	        final String sql,
 	        final SessionImplementor session) throws HibernateException {
 
 		final Expectation expectation = Expectations.appropriateExpectation( updateResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && expectation.canBeBatched() && isBatchable(); //note: updates to joined tables can't be batched...
 		if ( useBatch && updateBatchKey == null ) {
 			updateBatchKey = new BasicBatchKey(
 					getEntityName() + "#UPDATE",
 					expectation
 			);
 		}
 		final boolean callable = isUpdateCallable( j );
 		final boolean useVersion = j == 0 && isVersioned();
 
         if (LOG.isTraceEnabled()) {
             LOG.trace("Updating entity: " + MessageHelper.infoString(this, id, getFactory()));
             if (useVersion) LOG.trace("Existing version: " + oldVersion + " -> New version:" + fields[getVersionProperty()]);
 		}
 
 		try {
 			int index = 1; // starting index
 			final PreparedStatement update;
 			if ( useBatch ) {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( updateBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				update = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 				index+= expectation.prepare( update );
 
 				//Now write the values of fields onto the prepared statement
 				index = dehydrate( id, fields, rowId, includeProperty, propertyColumnUpdateable, j, update, session, index );
 
 				// Write any appropriate versioning conditional parameters
-				if ( useVersion && Versioning.OPTIMISTIC_LOCK_VERSION == entityMetamodel.getOptimisticLockMode() ) {
+				if ( useVersion && entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.VERSION ) {
 					if ( checkVersion( includeProperty ) ) {
 						getVersionType().nullSafeSet( update, oldVersion, index, session );
 					}
 				}
-				else if ( entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION && oldFields != null ) {
+				else if ( isAllOrDirtyOptLocking() && oldFields != null ) {
 					boolean[] versionability = getPropertyVersionability(); //TODO: is this really necessary????
-					boolean[] includeOldField = entityMetamodel.getOptimisticLockMode() == Versioning.OPTIMISTIC_LOCK_ALL ?
-							getPropertyUpdateability() : includeProperty;
+					boolean[] includeOldField = entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL
+							? getPropertyUpdateability()
+							: includeProperty;
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						boolean include = includeOldField[i] &&
 								isPropertyOfTable( i, j ) &&
 								versionability[i]; //TODO: is this really necessary????
 						if ( include ) {
 							boolean[] settable = types[i].toColumnNullness( oldFields[i], getFactory() );
 							types[i].nullSafeSet(
 									update,
 									oldFields[i],
 									index,
 									settable,
 									session
 								);
 							index += ArrayHelper.countTrue(settable);
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( updateBatchKey ).addToBatch();
 					return true;
 				}
 				else {
 					return check( update.executeUpdate(), id, j, expectation, update );
 				}
 
 			}
 			catch ( SQLException e ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw e;
 			}
 			finally {
 				if ( !useBatch ) {
 					update.close();
 				}
 			}
 
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not update: " + MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 		}
 	}
 
 	private BasicBatchKey deleteBatchKey;
 
 	/**
 	 * Perform an SQL DELETE
 	 */
 	protected void delete(
 			final Serializable id,
 			final Object version,
 			final int j,
 			final Object object,
 			final String sql,
 			final SessionImplementor session,
 			final Object[] loadedState) throws HibernateException {
 
 		if ( isInverseTable( j ) ) {
 			return;
 		}
 
 		final boolean useVersion = j == 0 && isVersioned();
 		final boolean callable = isDeleteCallable( j );
 		final Expectation expectation = Expectations.appropriateExpectation( deleteResultCheckStyles[j] );
 		final boolean useBatch = j == 0 && isBatchable() && expectation.canBeBatched();
 		if ( useBatch && deleteBatchKey == null ) {
 			deleteBatchKey = new BasicBatchKey(
 					getEntityName() + "#DELETE",
 					expectation
 			);
 		}
 
         if (LOG.isTraceEnabled()) {
             LOG.trace("Deleting entity: " + MessageHelper.infoString(this, id, getFactory()));
             if (useVersion) LOG.trace("Version: " + version);
 		}
 
 		if ( isTableCascadeDeleteEnabled( j ) ) {
             LOG.trace("Delete handled by foreign key constraint: " + getTableName(j));
 			return; //EARLY EXIT!
 		}
 
 		try {
 			//Render the SQL query
 			PreparedStatement delete;
 			int index = 1;
 			if ( useBatch ) {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getBatch( deleteBatchKey )
 						.getBatchStatement( sql, callable );
 			}
 			else {
 				delete = session.getTransactionCoordinator()
 						.getJdbcCoordinator()
 						.getStatementPreparer()
 						.prepareStatement( sql, callable );
 			}
 
 			try {
 
 				index += expectation.prepare( delete );
 
 				// Do the key. The key is immutable so we can use the _current_ object state - not necessarily
 				// the state at the time the delete was issued
 				getIdentifierType().nullSafeSet( delete, id, index, session );
 				index += getIdentifierColumnSpan();
 
 				// We should use the _current_ object state (ie. after any updates that occurred during flush)
 
 				if ( useVersion ) {
 					getVersionType().nullSafeSet( delete, version, index, session );
 				}
-				else if ( entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION && loadedState != null ) {
+				else if ( isAllOrDirtyOptLocking() && loadedState != null ) {
 					boolean[] versionability = getPropertyVersionability();
 					Type[] types = getPropertyTypes();
 					for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 						if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 							// this property belongs to the table and it is not specifically
 							// excluded from optimistic locking by optimistic-lock="false"
 							boolean[] settable = types[i].toColumnNullness( loadedState[i], getFactory() );
 							types[i].nullSafeSet( delete, loadedState[i], index, settable, session );
 							index += ArrayHelper.countTrue( settable );
 						}
 					}
 				}
 
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().getBatch( deleteBatchKey ).addToBatch();
 				}
 				else {
 					check( delete.executeUpdate(), id, j, expectation, delete );
 				}
 
 			}
 			catch ( SQLException sqle ) {
 				if ( useBatch ) {
 					session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
 				}
 				throw sqle;
 			}
 			finally {
 				if ( !useBatch ) {
 					delete.close();
 				}
 			}
 
 		}
 		catch ( SQLException sqle ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					sqle,
 					"could not delete: " +
 					MessageHelper.infoString( this, id, getFactory() ),
 					sql
 				);
 
 		}
 
 	}
 
 	private String[] getUpdateStrings(boolean byRowId, boolean lazy) {
 		if ( byRowId ) {
 			return lazy ? getSQLLazyUpdateByRowIdStrings() : getSQLUpdateByRowIdStrings();
 		}
 		else {
 			return lazy ? getSQLLazyUpdateStrings() : getSQLUpdateStrings();
 		}
 	}
 
 	/**
 	 * Update an object
 	 */
 	public void update(
 			final Serializable id,
 	        final Object[] fields,
 	        final int[] dirtyFields,
 	        final boolean hasDirtyCollection,
 	        final Object[] oldFields,
 	        final Object oldVersion,
 	        final Object object,
 	        final Object rowId,
 	        final SessionImplementor session) throws HibernateException {
 
 		//note: dirtyFields==null means we had no snapshot, and we couldn't get one using select-before-update
 		//	  oldFields==null just means we had no snapshot to begin with (we might have used select-before-update to get the dirtyFields)
 
 		final boolean[] tableUpdateNeeded = getTableUpdateNeeded( dirtyFields, hasDirtyCollection );
 		final int span = getTableSpan();
 
 		final boolean[] propsToUpdate;
 		final String[] updateStrings;
 		EntityEntry entry = session.getPersistenceContext().getEntry( object );
 
 		// Ensure that an immutable or non-modifiable entity is not being updated unless it is
 		// in the process of being deleted.
 		if ( entry == null && ! isMutable() ) {
 			throw new IllegalStateException( "Updating immutable entity that is not in session yet!" );
 		}
 		if ( ( entityMetamodel.isDynamicUpdate() && dirtyFields != null ) ) {
 			// We need to generate the UPDATE SQL when dynamic-update="true"
 			propsToUpdate = getPropertiesToUpdate( dirtyFields, hasDirtyCollection );
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else if ( ! isModifiableEntity( entry ) ) {
 			// We need to generate UPDATE SQL when a non-modifiable entity (e.g., read-only or immutable)
 			// needs:
 			// - to have references to transient entities set to null before being deleted
 			// - to have version incremented do to a "dirty" association
 			// If dirtyFields == null, then that means that there are no dirty properties to
 			// to be updated; an empty array for the dirty fields needs to be passed to
 			// getPropertiesToUpdate() instead of null.
 			propsToUpdate = getPropertiesToUpdate(
 					( dirtyFields == null ? ArrayHelper.EMPTY_INT_ARRAY : dirtyFields ),
 					hasDirtyCollection
 			);
 			// don't need to check laziness (dirty checking algorithm handles that)
 			updateStrings = new String[span];
 			for ( int j = 0; j < span; j++ ) {
 				updateStrings[j] = tableUpdateNeeded[j] ?
 						generateUpdateString( propsToUpdate, j, oldFields, j == 0 && rowId != null ) :
 						null;
 			}
 		}
 		else {
 			// For the case of dynamic-update="false", or no snapshot, we use the static SQL
 			updateStrings = getUpdateStrings(
 					rowId != null,
 					hasUninitializedLazyProperties( object )
 			);
 			propsToUpdate = getPropertyUpdateability( object );
 		}
 
 		for ( int j = 0; j < span; j++ ) {
 			// Now update only the tables with dirty properties (and the table with the version number)
 			if ( tableUpdateNeeded[j] ) {
 				updateOrInsert(
 						id,
 						fields,
 						oldFields,
 						j == 0 ? rowId : null,
 						propsToUpdate,
 						j,
 						oldVersion,
 						object,
 						updateStrings[j],
 						session
 					);
 			}
 		}
 	}
 
 	public Serializable insert(Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		final Serializable id;
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			id = insert( fields, notNull, generateInsertString( true, notNull ), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			id = insert( fields, getPropertyInsertability(), getSQLIdentityInsertString(), object, session );
 			for ( int j = 1; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 		return id;
 	}
 
 	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session)
 			throws HibernateException {
 
 		final int span = getTableSpan();
 		if ( entityMetamodel.isDynamicInsert() ) {
 			// For the case of dynamic-insert="true", we need to generate the INSERT SQL
 			boolean[] notNull = getPropertiesToInsert( fields );
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, notNull, j, generateInsertString( notNull, j ), object, session );
 			}
 		}
 		else {
 			// For the case of dynamic-insert="false", use the static SQL
 			for ( int j = 0; j < span; j++ ) {
 				insert( id, fields, getPropertyInsertability(), j, getSQLInsertStrings()[j], object, session );
 			}
 		}
 	}
 
 	/**
 	 * Delete an object
 	 */
 	public void delete(Serializable id, Object version, Object object, SessionImplementor session)
 			throws HibernateException {
 		final int span = getTableSpan();
-		boolean isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && entityMetamodel.getOptimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION;
+		boolean isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && isAllOrDirtyOptLocking();
 		Object[] loadedState = null;
 		if ( isImpliedOptimisticLocking ) {
 			// need to treat this as if it where optimistic-lock="all" (dirty does *not* make sense);
 			// first we need to locate the "loaded" state
 			//
 			// Note, it potentially could be a proxy, so doAfterTransactionCompletion the location the safe way...
 			final EntityKey key = session.generateEntityKey( id, this );
 			Object entity = session.getPersistenceContext().getEntity( key );
 			if ( entity != null ) {
 				EntityEntry entry = session.getPersistenceContext().getEntry( entity );
 				loadedState = entry.getLoadedState();
 			}
 		}
 
 		final String[] deleteStrings;
 		if ( isImpliedOptimisticLocking && loadedState != null ) {
 			// we need to utilize dynamic delete statements
 			deleteStrings = generateSQLDeletStrings( loadedState );
 		}
 		else {
 			// otherwise, utilize the static delete statements
 			deleteStrings = getSQLDeleteStrings();
 		}
 
 		for ( int j = span - 1; j >= 0; j-- ) {
 			delete( id, version, j, object, deleteStrings[j], session, loadedState );
 		}
 
 	}
 
+	private boolean isAllOrDirtyOptLocking() {
+		return entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.DIRTY
+				|| entityMetamodel.getOptimisticLockStyle() == OptimisticLockStyle.ALL;
+	}
+
 	private String[] generateSQLDeletStrings(Object[] loadedState) {
 		int span = getTableSpan();
 		String[] deleteStrings = new String[span];
 		for ( int j = span - 1; j >= 0; j-- ) {
 			Delete delete = new Delete()
 					.setTableName( getTableName( j ) )
 					.addPrimaryKeyColumns( getKeyColumns( j ) );
 			if ( getFactory().getSettings().isCommentsEnabled() ) {
 				delete.setComment( "delete " + getEntityName() + " [" + j + "]" );
 			}
 
 			boolean[] versionability = getPropertyVersionability();
 			Type[] types = getPropertyTypes();
 			for ( int i = 0; i < entityMetamodel.getPropertySpan(); i++ ) {
 				if ( isPropertyOfTable( i, j ) && versionability[i] ) {
 					// this property belongs to the table and it is not specifically
 					// excluded from optimistic locking by optimistic-lock="false"
 					String[] propertyColumnNames = getPropertyColumnNames( i );
 					boolean[] propertyNullness = types[i].toColumnNullness( loadedState[i], getFactory() );
 					for ( int k = 0; k < propertyNullness.length; k++ ) {
 						if ( propertyNullness[k] ) {
 							delete.addWhereFragment( propertyColumnNames[k] + " = ?" );
 						}
 						else {
 							delete.addWhereFragment( propertyColumnNames[k] + " is null" );
 						}
 					}
 				}
 			}
 			deleteStrings[j] = delete.toStatementString();
 		}
 		return deleteStrings;
 	}
 
 	protected void logStaticSQL() {
         if (LOG.isDebugEnabled()) {
             LOG.debugf("Static SQL for entity: %s", getEntityName());
             if (sqlLazySelectString != null) LOG.debugf(" Lazy select: %s", sqlLazySelectString);
             if (sqlVersionSelectString != null) LOG.debugf(" Version select: %s", sqlVersionSelectString);
             if (sqlSnapshotSelectString != null) LOG.debugf(" Snapshot select: %s", sqlSnapshotSelectString);
 			for ( int j = 0; j < getTableSpan(); j++ ) {
                 LOG.debugf(" Insert %s: %s", j, getSQLInsertStrings()[j]);
                 LOG.debugf(" Update %s: %s", j, getSQLUpdateStrings()[j]);
                 LOG.debugf(" Delete %s: %s", j, getSQLDeleteStrings()[j]);
 			}
             if (sqlIdentityInsertString != null) LOG.debugf(" Identity insert: %s", sqlIdentityInsertString);
             if (sqlUpdateByRowIdString != null) LOG.debugf(" Update by row id (all fields): %s", sqlUpdateByRowIdString);
             if (sqlLazyUpdateByRowIdString != null) LOG.debugf(" Update by row id (non-lazy fields): %s",
                                                                sqlLazyUpdateByRowIdString);
             if (sqlInsertGeneratedValuesSelectString != null) LOG.debugf("Insert-generated property select: %s",
                                                                          sqlInsertGeneratedValuesSelectString);
             if (sqlUpdateGeneratedValuesSelectString != null) LOG.debugf("Update-generated property select: %s",
                                                                          sqlUpdateGeneratedValuesSelectString);
 		}
 	}
 
 	public String filterFragment(String alias, Map enabledFilters) throws MappingException {
 		final StringBuffer sessionFilterFragment = new StringBuffer();
 		filterHelper.render( sessionFilterFragment, generateFilterConditionAlias( alias ), enabledFilters );
 
 		return sessionFilterFragment.append( filterFragment( alias ) ).toString();
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return rootAlias;
 	}
 
 	public String oneToManyFilterFragment(String alias) throws MappingException {
 		return "";
 	}
 
 	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toFromFragmentString();
 	}
 
 	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
 		return getSubclassTableSpan() == 1 ?
 				"" : //just a performance opt!
 				createJoin( alias, innerJoin, includeSubclasses ).toWhereFragmentString();
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return false;
 	}
 
 	protected JoinFragment createJoin(String name, boolean innerJoin, boolean includeSubclasses) {
 		final String[] idCols = StringHelper.qualify( name, getIdentifierColumnNames() ); //all joins join to the pk of the driving table
 		final JoinFragment join = getFactory().getDialect().createOuterJoinFragment();
 		final int tableSpan = getSubclassTableSpan();
 		for ( int j = 1; j < tableSpan; j++ ) { //notice that we skip the first table; it is the driving table!
 			final boolean joinIsIncluded = isClassOrSuperclassTable( j ) ||
 					( includeSubclasses && !isSubclassTableSequentialSelect( j ) && !isSubclassTableLazy( j ) );
 			if ( joinIsIncluded ) {
 				join.addJoin( getSubclassTableName( j ),
 						generateTableAlias( name, j ),
 						idCols,
 						getSubclassTableKeyColumns( j ),
 						innerJoin && isClassOrSuperclassTable( j ) && !isInverseTable( j ) && !isNullableTable( j ) ?
 						JoinType.INNER_JOIN : //we can inner join to superclass tables (the row MUST be there)
 						JoinType.LEFT_OUTER_JOIN //we can never inner join to subclass tables
 					);
 			}
 		}
 		return join;
 	}
 
 	protected JoinFragment createJoin(int[] tableNumbers, String drivingAlias) {
 		final String[] keyCols = StringHelper.qualify( drivingAlias, getSubclassTableKeyColumns( tableNumbers[0] ) );
 		final JoinFragment jf = getFactory().getDialect().createOuterJoinFragment();
 		for ( int i = 1; i < tableNumbers.length; i++ ) { //skip the driving table
 			final int j = tableNumbers[i];
 			jf.addJoin( getSubclassTableName( j ),
 					generateTableAlias( getRootAlias(), j ),
 					keyCols,
 					getSubclassTableKeyColumns( j ),
 					isInverseSubclassTable( j ) || isNullableSubclassTable( j ) ?
 					JoinType.LEFT_OUTER_JOIN :
 					JoinType.INNER_JOIN );
 		}
 		return jf;
 	}
 
 	protected SelectFragment createSelect(final int[] subclassColumnNumbers,
 										  final int[] subclassFormulaNumbers) {
 
 		SelectFragment selectFragment = new SelectFragment();
 
 		int[] columnTableNumbers = getSubclassColumnTableNumberClosure();
 		String[] columnAliases = getSubclassColumnAliasClosure();
 		String[] columnReaderTemplates = getSubclassColumnReaderTemplateClosure();
 		for ( int i = 0; i < subclassColumnNumbers.length; i++ ) {
 			int columnNumber = subclassColumnNumbers[i];
 			if ( subclassColumnSelectableClosure[columnNumber] ) {
 				final String subalias = generateTableAlias( getRootAlias(), columnTableNumbers[columnNumber] );
 				selectFragment.addColumnTemplate( subalias, columnReaderTemplates[columnNumber], columnAliases[columnNumber] );
 			}
 		}
 
 		int[] formulaTableNumbers = getSubclassFormulaTableNumberClosure();
 		String[] formulaTemplates = getSubclassFormulaTemplateClosure();
 		String[] formulaAliases = getSubclassFormulaAliasClosure();
 		for ( int i = 0; i < subclassFormulaNumbers.length; i++ ) {
 			int formulaNumber = subclassFormulaNumbers[i];
 			final String subalias = generateTableAlias( getRootAlias(), formulaTableNumbers[formulaNumber] );
 			selectFragment.addFormula( subalias, formulaTemplates[formulaNumber], formulaAliases[formulaNumber] );
 		}
 
 		return selectFragment;
 	}
 
 	protected String createFrom(int tableNumber, String alias) {
 		return getSubclassTableName( tableNumber ) + ' ' + alias;
 	}
 
 	protected String createWhereByKey(int tableNumber, String alias) {
 		//TODO: move to .sql package, and refactor with similar things!
 		return StringHelper.join( "=? and ",
 				StringHelper.qualify( alias, getSubclassTableKeyColumns( tableNumber ) ) ) + "=?";
 	}
 
 	protected String renderSelect(
 			final int[] tableNumbers,
 	        final int[] columnNumbers,
 	        final int[] formulaNumbers) {
 
 		Arrays.sort( tableNumbers ); //get 'em in the right order (not that it really matters)
 
 		//render the where and from parts
 		int drivingTable = tableNumbers[0];
 		final String drivingAlias = generateTableAlias( getRootAlias(), drivingTable ); //we *could* regerate this inside each called method!
 		final String where = createWhereByKey( drivingTable, drivingAlias );
 		final String from = createFrom( drivingTable, drivingAlias );
 
 		//now render the joins
 		JoinFragment jf = createJoin( tableNumbers, drivingAlias );
 
 		//now render the select clause
 		SelectFragment selectFragment = createSelect( columnNumbers, formulaNumbers );
 
 		//now tie it all together
 		Select select = new Select( getFactory().getDialect() );
 		select.setSelectClause( selectFragment.toFragmentString().substring( 2 ) );
 		select.setFromClause( from );
 		select.setWhereClause( where );
 		select.setOuterJoins( jf.toFromFragmentString(), jf.toWhereFragmentString() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "sequential select " + getEntityName() );
 		}
 		return select.toStatementString();
 	}
 
 	private String getRootAlias() {
 		return StringHelper.generateAlias( getEntityName() );
 	}
 
 	protected void postConstruct(Mapping mapping) throws MappingException {
 		initPropertyPaths(mapping);
 
 		//insert/update/delete SQL
 		final int joinSpan = getTableSpan();
 		sqlDeleteStrings = new String[joinSpan];
 		sqlInsertStrings = new String[joinSpan];
 		sqlUpdateStrings = new String[joinSpan];
 		sqlLazyUpdateStrings = new String[joinSpan];
 
 		sqlUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getPropertyUpdateability(), 0, true );
 		sqlLazyUpdateByRowIdString = rowIdName == null ?
 				null :
 				generateUpdateString( getNonLazyPropertyUpdateability(), 0, true );
 
 		for ( int j = 0; j < joinSpan; j++ ) {
 			sqlInsertStrings[j] = customSQLInsert[j] == null ?
 					generateInsertString( getPropertyInsertability(), j ) :
 					customSQLInsert[j];
 			sqlUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlLazyUpdateStrings[j] = customSQLUpdate[j] == null ?
 					generateUpdateString( getNonLazyPropertyUpdateability(), j, false ) :
 					customSQLUpdate[j];
 			sqlDeleteStrings[j] = customSQLDelete[j] == null ?
 					generateDeleteString( j ) :
 					customSQLDelete[j];
 		}
 
 		tableHasColumns = new boolean[joinSpan];
 		for ( int j = 0; j < joinSpan; j++ ) {
 			tableHasColumns[j] = sqlUpdateStrings[j] != null;
 		}
 
 		//select SQL
 		sqlSnapshotSelectString = generateSnapshotSelectString();
 		sqlLazySelectString = generateLazySelectString();
 		sqlVersionSelectString = generateSelectVersionString();
 		if ( hasInsertGeneratedProperties() ) {
 			sqlInsertGeneratedValuesSelectString = generateInsertGeneratedValuesSelectString();
 		}
 		if ( hasUpdateGeneratedProperties() ) {
 			sqlUpdateGeneratedValuesSelectString = generateUpdateGeneratedValuesSelectString();
 		}
 		if ( isIdentifierAssignedByInsert() ) {
 			identityDelegate = ( ( PostInsertIdentifierGenerator ) getIdentifierGenerator() )
 					.getInsertGeneratedIdentifierDelegate( this, getFactory().getDialect(), useGetGeneratedKeys() );
 			sqlIdentityInsertString = customSQLInsert[0] == null
 					? generateIdentityInsertString( getPropertyInsertability() )
 					: customSQLInsert[0];
 		}
 		else {
 			sqlIdentityInsertString = null;
 		}
 
 		logStaticSQL();
 
 	}
 
 	public void postInstantiate() throws MappingException {
 
 		createLoaders();
 		createUniqueKeyLoaders();
 		createQueryLoader();
 
 	}
 
 	//needed by subclasses to override the createLoader strategy
 	protected Map getLoaders() {
 		return loaders;
 	}
 
 	//Relational based Persisters should be content with this implementation
 	protected void createLoaders() {
 		final Map loaders = getLoaders();
 		loaders.put( LockMode.NONE, createEntityLoader( LockMode.NONE ) );
 
 		UniqueEntityLoader readLoader = createEntityLoader( LockMode.READ );
 		loaders.put( LockMode.READ, readLoader );
 
 		//TODO: inexact, what we really need to know is: are any outer joins used?
 		boolean disableForUpdate = getSubclassTableSpan() > 1 &&
 				hasSubclasses() &&
 				!getFactory().getDialect().supportsOuterJoinForUpdate();
 
 		loaders.put(
 				LockMode.UPGRADE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE )
 			);
 		loaders.put(
 				LockMode.UPGRADE_NOWAIT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.UPGRADE_NOWAIT )
 			);
 		loaders.put(
 				LockMode.FORCE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.FORCE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_READ,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_READ )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_WRITE,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_WRITE )
 			);
 		loaders.put(
 				LockMode.PESSIMISTIC_FORCE_INCREMENT,
 				disableForUpdate ?
 						readLoader :
 						createEntityLoader( LockMode.PESSIMISTIC_FORCE_INCREMENT )
 			);
 		loaders.put( LockMode.OPTIMISTIC, createEntityLoader( LockMode.OPTIMISTIC) );
 		loaders.put( LockMode.OPTIMISTIC_FORCE_INCREMENT, createEntityLoader(LockMode.OPTIMISTIC_FORCE_INCREMENT) );
 
 		loaders.put(
 				"merge",
 				new CascadeEntityLoader( this, CascadingAction.MERGE, getFactory() )
 			);
 		loaders.put(
 				"refresh",
 				new CascadeEntityLoader( this, CascadingAction.REFRESH, getFactory() )
 			);
 	}
 
 	protected void createQueryLoader() {
 		if ( loaderName != null ) {
 			queryLoader = new NamedQueryLoader( loaderName, this );
 		}
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) {
 		return load( id, optionalObject, new LockOptions().setLockMode(lockMode), session );
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	public Object load(Serializable id, Object optionalObject, LockOptions lockOptions, SessionImplementor session)
 			throws HibernateException {
 
         if (LOG.isTraceEnabled()) LOG.trace("Fetching entity: " + MessageHelper.infoString(this, id, getFactory()));
 
 		final UniqueEntityLoader loader = getAppropriateLoader(lockOptions, session );
 		return loader.load( id, optionalObject, session, lockOptions );
 	}
 
 	public void registerAffectingFetchProfile(String fetchProfileName) {
 		affectingFetchProfileNames.add( fetchProfileName );
 	}
 
 	private boolean isAffectedByEnabledFetchProfiles(SessionImplementor session) {
 		Iterator itr = session.getLoadQueryInfluencers().getEnabledFetchProfileNames().iterator();
 		while ( itr.hasNext() ) {
 			if ( affectingFetchProfileNames.contains( itr.next() ) ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	private boolean isAffectedByEnabledFilters(SessionImplementor session) {
 		return session.getLoadQueryInfluencers().hasEnabledFilters()
 				&& filterHelper.isAffectedBy( session.getLoadQueryInfluencers().getEnabledFilters() );
 	}
 
 	private UniqueEntityLoader getAppropriateLoader(LockOptions lockOptions, SessionImplementor session) {
 		if ( queryLoader != null ) {
 			// if the user specified a custom query loader we need to that
 			// regardless of any other consideration
 			return queryLoader;
 		}
 		else if ( isAffectedByEnabledFilters( session ) ) {
 			// because filters affect the rows returned (because they add
 			// restirctions) these need to be next in precendence
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( session.getLoadQueryInfluencers().getInternalFetchProfile() != null && LockMode.UPGRADE.greaterThan( lockOptions.getLockMode() ) ) {
 			// Next, we consider whether an 'internal' fetch profile has been set.
 			// This indicates a special fetch profile Hibernate needs applied
 			// (for its merge loading process e.g.).
 			return ( UniqueEntityLoader ) getLoaders().get( session.getLoadQueryInfluencers().getInternalFetchProfile() );
 		}
 		else if ( isAffectedByEnabledFetchProfiles( session ) ) {
 			// If the session has associated influencers we need to adjust the
 			// SQL query used for loading based on those influencers
 			return createEntityLoader(lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else if ( lockOptions.getTimeOut() != LockOptions.WAIT_FOREVER ) {
 			return createEntityLoader( lockOptions, session.getLoadQueryInfluencers() );
 		}
 		else {
 			return ( UniqueEntityLoader ) getLoaders().get( lockOptions.getLockMode() );
 		}
 	}
 
 	private boolean isAllNull(Object[] array, int tableNumber) {
 		for ( int i = 0; i < array.length; i++ ) {
 			if ( isPropertyOfTable( i, tableNumber ) && array[i] != null ) {
 				return false;
 			}
 		}
 		return true;
 	}
 
 	public boolean isSubclassPropertyNullable(int i) {
 		return subclassPropertyNullabilityClosure[i];
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is dirty
 	 */
 	protected final boolean[] getPropertiesToUpdate(final int[] dirtyProperties, final boolean hasDirtyCollection) {
 		final boolean[] propsToUpdate = new boolean[ entityMetamodel.getPropertySpan() ];
 		final boolean[] updateability = getPropertyUpdateability(); //no need to check laziness, dirty checking handles that
 		for ( int j = 0; j < dirtyProperties.length; j++ ) {
 			int property = dirtyProperties[j];
 			if ( updateability[property] ) {
 				propsToUpdate[property] = true;
 			}
 		}
 		if ( isVersioned() && updateability[getVersionProperty() ]) {
 			propsToUpdate[ getVersionProperty() ] =
 				Versioning.isVersionIncrementRequired( dirtyProperties, hasDirtyCollection, getPropertyVersionability() );
 		}
 		return propsToUpdate;
 	}
 
 	/**
 	 * Transform the array of property indexes to an array of booleans,
 	 * true when the property is insertable and non-null
 	 */
 	protected boolean[] getPropertiesToInsert(Object[] fields) {
 		boolean[] notNull = new boolean[fields.length];
 		boolean[] insertable = getPropertyInsertability();
 		for ( int i = 0; i < fields.length; i++ ) {
 			notNull[i] = insertable[i] && fields[i] != null;
 		}
 		return notNull;
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param currentState The current state of the entity (the state to be checked).
 	 * @param previousState The previous state of the entity (the state to be checked against).
 	 * @param entity The entity for which we are checking state dirtiness.
 	 * @param session The session in which the check is ccurring.
 	 * @return <tt>null</tt> or the indices of the dirty properties
 	 * @throws HibernateException
 	 */
 	public int[] findDirty(Object[] currentState, Object[] previousState, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findDirty(
 				entityMetamodel.getProperties(),
 				currentState,
 				previousState,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Locate the property-indices of all properties considered to be dirty.
 	 *
 	 * @param old The old state of the entity.
 	 * @param current The current state of the entity.
 	 * @param entity The entity for which we are checking state modification.
 	 * @param session The session in which the check is ccurring.
 	 * @return <tt>null</tt> or the indices of the modified properties
 	 * @throws HibernateException
 	 */
 	public int[] findModified(Object[] old, Object[] current, Object entity, SessionImplementor session)
 	throws HibernateException {
 		int[] props = TypeHelper.findModified(
 				entityMetamodel.getProperties(),
 				current,
 				old,
 				propertyColumnUpdateable,
 				hasUninitializedLazyProperties( entity ),
 				session
 			);
 		if ( props == null ) {
 			return null;
 		}
 		else {
 			logDirtyProperties( props );
 			return props;
 		}
 	}
 
 	/**
 	 * Which properties appear in the SQL update?
 	 * (Initialized, updateable ones!)
 	 */
 	protected boolean[] getPropertyUpdateability(Object entity) {
 		return hasUninitializedLazyProperties( entity )
 				? getNonLazyPropertyUpdateability()
 				: getPropertyUpdateability();
 	}
 
 	private void logDirtyProperties(int[] props) {
         if (LOG.isTraceEnabled()) {
 			for ( int i = 0; i < props.length; i++ ) {
 				String propertyName = entityMetamodel.getProperties()[ props[i] ].getName();
                 LOG.trace(StringHelper.qualify(getEntityName(), propertyName) + " is dirty");
 			}
 		}
 	}
 
 	public SessionFactoryImplementor getFactory() {
 		return factory;
 	}
 
 	public EntityMetamodel getEntityMetamodel() {
 		return entityMetamodel;
 	}
 
 	public boolean hasCache() {
 		return cacheAccessStrategy != null;
 	}
 
 	public EntityRegionAccessStrategy getCacheAccessStrategy() {
 		return cacheAccessStrategy;
 	}
 
 	public CacheEntryStructure getCacheEntryStructure() {
 		return cacheEntryStructure;
 	}
 
 	public Comparator getVersionComparator() {
 		return isVersioned() ? getVersionType().getComparator() : null;
 	}
 
 	// temporary ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 	public final String getEntityName() {
 		return entityMetamodel.getName();
 	}
 
 	public EntityType getEntityType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isPolymorphic() {
 		return entityMetamodel.isPolymorphic();
 	}
 
 	public boolean isInherited() {
 		return entityMetamodel.isInherited();
 	}
 
 	public boolean hasCascades() {
 		return entityMetamodel.hasCascades();
 	}
 
 	public boolean hasIdentifierProperty() {
 		return !entityMetamodel.getIdentifierProperty().isVirtual();
 	}
 
 	public VersionType getVersionType() {
 		return ( VersionType ) locateVersionType();
 	}
 
 	private Type locateVersionType() {
 		return entityMetamodel.getVersionProperty() == null ?
 				null :
 				entityMetamodel.getVersionProperty().getType();
 	}
 
 	public int getVersionProperty() {
 		return entityMetamodel.getVersionPropertyIndex();
 	}
 
 	public boolean isVersioned() {
 		return entityMetamodel.isVersioned();
 	}
 
 	public boolean isIdentifierAssignedByInsert() {
 		return entityMetamodel.getIdentifierProperty().isIdentifierAssignedByInsert();
 	}
 
 	public boolean hasLazyProperties() {
 		return entityMetamodel.hasLazyProperties();
 	}
 
 //	public boolean hasUninitializedLazyProperties(Object entity) {
 //		if ( hasLazyProperties() ) {
 //			InterceptFieldCallback callback = ( ( InterceptFieldEnabled ) entity ).getInterceptFieldCallback();
 //			return callback != null && !( ( FieldInterceptor ) callback ).isInitialized();
 //		}
 //		else {
 //			return false;
 //		}
 //	}
 
 	public void afterReassociate(Object entity, SessionImplementor session) {
 		//if ( hasLazyProperties() ) {
 		if ( FieldInterceptionHelper.isInstrumented( entity ) ) {
 			FieldInterceptor interceptor = FieldInterceptionHelper.extractFieldInterceptor( entity );
 			if ( interceptor != null ) {
 				interceptor.setSession( session );
 			}
 			else {
 				FieldInterceptor fieldInterceptor = FieldInterceptionHelper.injectFieldInterceptor(
 						entity,
 						getEntityName(),
 						null,
 						session
 				);
 				fieldInterceptor.dirty();
 			}
 		}
 	}
 
 	public Boolean isTransient(Object entity, SessionImplementor session) throws HibernateException {
 		final Serializable id;
 		if ( canExtractIdOutOfEntity() ) {
 			id = getIdentifier( entity, session );
 		}
 		else {
 			id = null;
 		}
 		// we *always* assume an instance with a null
 		// identifier or no identifier property is unsaved!
 		if ( id == null ) {
 			return Boolean.TRUE;
 		}
 
 		// check the version unsaved-value, if appropriate
 		final Object version = getVersion( entity );
 		if ( isVersioned() ) {
 			// let this take precedence if defined, since it works for
 			// assigned identifiers
 			Boolean result = entityMetamodel.getVersionProperty()
 					.getUnsavedValue().isUnsaved( version );
 			if ( result != null ) {
 				return result;
 			}
 		}
 
 		// check the id unsaved-value
 		Boolean result = entityMetamodel.getIdentifierProperty()
 				.getUnsavedValue().isUnsaved( id );
 		if ( result != null ) {
 			return result;
 		}
 
 		// check to see if it is in the second-level cache
 		if ( hasCache() ) {
 			CacheKey ck = session.generateCacheKey( id, getIdentifierType(), getRootEntityName() );
 			if ( getCacheAccessStrategy().get( ck, session.getTimestamp() ) != null ) {
 				return Boolean.FALSE;
 			}
 		}
 
 		return null;
 	}
 
 	public boolean hasCollections() {
 		return entityMetamodel.hasCollections();
 	}
 
 	public boolean hasMutableProperties() {
 		return entityMetamodel.hasMutableProperties();
 	}
 
 	public boolean isMutable() {
 		return entityMetamodel.isMutable();
 	}
 
 	private boolean isModifiableEntity(EntityEntry entry) {
 
 		return ( entry == null ? isMutable() : entry.isModifiableEntity() );
 	}
 
 	public boolean isAbstract() {
 		return entityMetamodel.isAbstract();
 	}
 
 	public boolean hasSubclasses() {
 		return entityMetamodel.hasSubclasses();
 	}
 
 	public boolean hasProxy() {
 		return entityMetamodel.isLazy();
 	}
 
 	public IdentifierGenerator getIdentifierGenerator() throws HibernateException {
 		return entityMetamodel.getIdentifierProperty().getIdentifierGenerator();
 	}
 
 	public String getRootEntityName() {
 		return entityMetamodel.getRootName();
 	}
 
 	public ClassMetadata getClassMetadata() {
 		return this;
 	}
 
 	public String getMappedSuperclass() {
 		return entityMetamodel.getSuperclass();
 	}
 
 	public boolean isExplicitPolymorphism() {
 		return entityMetamodel.isExplicitPolymorphism();
 	}
 
 	protected boolean useDynamicUpdate() {
 		return entityMetamodel.isDynamicUpdate();
 	}
 
 	protected boolean useDynamicInsert() {
 		return entityMetamodel.isDynamicInsert();
 	}
 
 	protected boolean hasEmbeddedCompositeIdentifier() {
 		return entityMetamodel.getIdentifierProperty().isEmbedded();
 	}
 
 	public boolean canExtractIdOutOfEntity() {
 		return hasIdentifierProperty() || hasEmbeddedCompositeIdentifier() || hasIdentifierMapper();
 	}
 
 	private boolean hasIdentifierMapper() {
 		return entityMetamodel.getIdentifierProperty().hasIdentifierMapper();
 	}
 
 	public String[] getKeyColumnNames() {
 		return getIdentifierColumnNames();
 	}
 
 	public String getName() {
 		return getEntityName();
 	}
 
 	public boolean isCollection() {
 		return false;
 	}
 
 	public boolean consumesEntityAlias() {
 		return true;
 	}
 
 	public boolean consumesCollectionAlias() {
 		return false;
 	}
 
 	public Type getPropertyType(String propertyName) throws MappingException {
 		return propertyMapping.toType( propertyName );
 	}
 
 	public Type getType() {
 		return entityMetamodel.getEntityType();
 	}
 
 	public boolean isSelectBeforeUpdateRequired() {
 		return entityMetamodel.isSelectBeforeUpdate();
 	}
 
-	protected final int optimisticLockMode() {
-		return entityMetamodel.getOptimisticLockMode();
+	protected final OptimisticLockStyle optimisticLockStyle() {
+		return entityMetamodel.getOptimisticLockStyle();
 	}
 
 	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException {
 		return entityMetamodel.getTuplizer().createProxy( id, session );
 	}
 
 	public String toString() {
 		return StringHelper.unqualify( getClass().getName() ) +
 				'(' + entityMetamodel.getName() + ')';
 	}
 
 	public final String selectFragment(
 			Joinable rhs,
 			String rhsAlias,
 			String lhsAlias,
 			String entitySuffix,
 			String collectionSuffix,
 			boolean includeCollectionColumns) {
 		return selectFragment( lhsAlias, entitySuffix );
 	}
 
 	public boolean isInstrumented() {
 		return getEntityTuplizer().isInstrumented();
 	}
 
 	public boolean hasInsertGeneratedProperties() {
 		return entityMetamodel.hasInsertGeneratedValues();
 	}
 
 	public boolean hasUpdateGeneratedProperties() {
 		return entityMetamodel.hasUpdateGeneratedValues();
 	}
 
 	public boolean isVersionPropertyGenerated() {
 		return isVersioned() && ( getPropertyUpdateGenerationInclusions() [ getVersionProperty() ] != ValueInclusion.NONE );
 	}
 
 	public boolean isVersionPropertyInsertable() {
 		return isVersioned() && getPropertyInsertability() [ getVersionProperty() ];
 	}
 
 	public void afterInitialize(Object entity, boolean lazyPropertiesAreUnfetched, SessionImplementor session) {
 		getEntityTuplizer().afterInitialize( entity, lazyPropertiesAreUnfetched, session );
 	}
 
 	public String[] getPropertyNames() {
 		return entityMetamodel.getPropertyNames();
 	}
 
 	public Type[] getPropertyTypes() {
 		return entityMetamodel.getPropertyTypes();
 	}
 
 	public boolean[] getPropertyLaziness() {
 		return entityMetamodel.getPropertyLaziness();
 	}
 
 	public boolean[] getPropertyUpdateability() {
 		return entityMetamodel.getPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyCheckability() {
 		return entityMetamodel.getPropertyCheckability();
 	}
 
 	public boolean[] getNonLazyPropertyUpdateability() {
 		return entityMetamodel.getNonlazyPropertyUpdateability();
 	}
 
 	public boolean[] getPropertyInsertability() {
 		return entityMetamodel.getPropertyInsertability();
 	}
 
 	public ValueInclusion[] getPropertyInsertGenerationInclusions() {
 		return entityMetamodel.getPropertyInsertGenerationInclusions();
 	}
 
 	public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
 		return entityMetamodel.getPropertyUpdateGenerationInclusions();
 	}
 
 	public boolean[] getPropertyNullability() {
 		return entityMetamodel.getPropertyNullability();
 	}
 
 	public boolean[] getPropertyVersionability() {
 		return entityMetamodel.getPropertyVersionability();
 	}
 
 	public CascadeStyle[] getPropertyCascadeStyles() {
 		return entityMetamodel.getCascadeStyles();
 	}
 
 	public final Class getMappedClass() {
 		return getEntityTuplizer().getMappedClass();
 	}
 
 	public boolean implementsLifecycle() {
 		return getEntityTuplizer().isLifecycleImplementor();
 	}
 
 	public Class getConcreteProxyClass() {
 		return getEntityTuplizer().getConcreteProxyClass();
 	}
 
 	public void setPropertyValues(Object object, Object[] values) {
 		getEntityTuplizer().setPropertyValues( object, values );
 	}
 
 	public void setPropertyValue(Object object, int i, Object value) {
 		getEntityTuplizer().setPropertyValue( object, i, value );
 	}
 
 	public Object[] getPropertyValues(Object object) {
 		return getEntityTuplizer().getPropertyValues( object );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, int i) {
 		return getEntityTuplizer().getPropertyValue( object, i );
 	}
 
 	@Override
 	public Object getPropertyValue(Object object, String propertyName) {
 		return getEntityTuplizer().getPropertyValue( object, propertyName );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object object) {
 		return getEntityTuplizer().getIdentifier( object, null );
 	}
 
 	@Override
 	public Serializable getIdentifier(Object entity, SessionImplementor session) {
 		return getEntityTuplizer().getIdentifier( entity, session );
 	}
 
 	@Override
 	public void setIdentifier(Object entity, Serializable id, SessionImplementor session) {
 		getEntityTuplizer().setIdentifier( entity, id, session );
 	}
 
 	@Override
 	public Object getVersion(Object object) {
 		return getEntityTuplizer().getVersion( object );
 	}
 
 	@Override
 	public Object instantiate(Serializable id, SessionImplementor session) {
 		return getEntityTuplizer().instantiate( id, session );
 	}
 
 	@Override
 	public boolean isInstance(Object object) {
 		return getEntityTuplizer().isInstance( object );
 	}
 
 	@Override
 	public boolean hasUninitializedLazyProperties(Object object) {
 		return getEntityTuplizer().hasUninitializedLazyProperties( object );
 	}
 
 	@Override
 	public void resetIdentifier(Object entity, Serializable currentId, Object currentVersion, SessionImplementor session) {
 		getEntityTuplizer().resetIdentifier( entity, currentId, currentVersion, session );
 	}
 
 	@Override
 	public EntityPersister getSubclassEntityPersister(Object instance, SessionFactoryImplementor factory) {
 		if ( !hasSubclasses() ) {
 			return this;
 		}
 		else {
 			final String concreteEntityName = getEntityTuplizer().determineConcreteSubclassEntityName(
 					instance,
 					factory
 			);
 			if ( concreteEntityName == null || getEntityName().equals( concreteEntityName ) ) {
 				// the contract of EntityTuplizer.determineConcreteSubclassEntityName says that returning null
 				// is an indication that the specified entity-name (this.getEntityName) should be used.
 				return this;
 			}
 			else {
 				return factory.getEntityPersister( concreteEntityName );
 			}
 		}
 	}
 
 	public boolean isMultiTable() {
 		return false;
 	}
 
 	public String getTemporaryIdTableName() {
 		return temporaryIdTableName;
 	}
 
 	public String getTemporaryIdTableDDL() {
 		return temporaryIdTableDDL;
 	}
 
 	protected int getPropertySpan() {
 		return entityMetamodel.getPropertySpan();
 	}
 
 	public Object[] getPropertyValuesToInsert(Object object, Map mergeMap, SessionImplementor session) throws HibernateException {
 		return getEntityTuplizer().getPropertyValuesToInsert( object, mergeMap, session );
 	}
 
 	public void processInsertGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasInsertGeneratedProperties() ) {
 			throw new AssertionFailure("no insert-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlInsertGeneratedValuesSelectString, getPropertyInsertGenerationInclusions() );
 	}
 
 	public void processUpdateGeneratedProperties(Serializable id, Object entity, Object[] state, SessionImplementor session) {
 		if ( !hasUpdateGeneratedProperties() ) {
 			throw new AssertionFailure("no update-generated properties");
 		}
 		processGeneratedProperties( id, entity, state, session, sqlUpdateGeneratedValuesSelectString, getPropertyUpdateGenerationInclusions() );
 	}
 
 	private void processGeneratedProperties(
 			Serializable id,
 	        Object entity,
 	        Object[] state,
 	        SessionImplementor session,
 	        String selectionSQL,
 	        ValueInclusion[] includeds) {
 		// force immediate execution of the insert batch (if one)
 		session.getTransactionCoordinator().getJdbcCoordinator().executeBatch();
 
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( selectionSQL );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = ps.executeQuery();
 				try {
 					if ( !rs.next() ) {
 						throw new HibernateException(
 								"Unable to locate row for retrieval of generated properties: " +
 								MessageHelper.infoString( this, id, getFactory() )
 							);
 					}
 					for ( int i = 0; i < getPropertySpan(); i++ ) {
 						if ( includeds[i] != ValueInclusion.NONE ) {
 							Object hydratedState = getPropertyTypes()[i].hydrate( rs, getPropertyAliases( "", i ), session, entity );
 							state[i] = getPropertyTypes()[i].resolve( hydratedState, session, entity );
 							setPropertyValue( entity, i, state[i] );
 						}
 					}
 				}
 				finally {
 					if ( rs != null ) {
 						rs.close();
 					}
 				}
 			}
 			finally {
 				ps.close();
 			}
 		}
 		catch( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"unable to select generated column values",
 					selectionSQL
 			);
 		}
 
 	}
 
 	public String getIdentifierPropertyName() {
 		return entityMetamodel.getIdentifierProperty().getName();
 	}
 
 	public Type getIdentifierType() {
 		return entityMetamodel.getIdentifierProperty().getType();
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 
 	public int[] getNaturalIdentifierProperties() {
 		return entityMetamodel.getNaturalIdentifierProperties();
 	}
 
 	public Object[] getNaturalIdentifierSnapshot(Serializable id, SessionImplementor session) throws HibernateException {
 		if ( !hasNaturalIdentifier() ) {
 			throw new MappingException( "persistent class did not define a natural-id : " + MessageHelper.infoString( this ) );
 		}
         if (LOG.isTraceEnabled()) LOG.trace("Getting current natural-id snapshot state for: "
                                             + MessageHelper.infoString(this, id, getFactory()));
 
 		int[] naturalIdPropertyIndexes = getNaturalIdentifierProperties();
 		int naturalIdPropertyCount = naturalIdPropertyIndexes.length;
 		boolean[] naturalIdMarkers = new boolean[ getPropertySpan() ];
 		Type[] extractionTypes = new Type[ naturalIdPropertyCount ];
 		for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 			extractionTypes[i] = getPropertyTypes()[ naturalIdPropertyIndexes[i] ];
 			naturalIdMarkers[ naturalIdPropertyIndexes[i] ] = true;
 		}
 
 		///////////////////////////////////////////////////////////////////////
 		// TODO : look at perhaps caching this...
 		Select select = new Select( getFactory().getDialect() );
 		if ( getFactory().getSettings().isCommentsEnabled() ) {
 			select.setComment( "get current natural-id state " + getEntityName() );
 		}
 		select.setSelectClause( concretePropertySelectFragmentSansLeadingComma( getRootAlias(), naturalIdMarkers ) );
 		select.setFromClause( fromTableFragment( getRootAlias() ) + fromJoinFragment( getRootAlias(), true, false ) );
 
 		String[] aliasedIdColumns = StringHelper.qualify( getRootAlias(), getIdentifierColumnNames() );
 		String whereClause = new StringBuffer()
 			.append( StringHelper.join( "=? and ",
 					aliasedIdColumns ) )
 			.append( "=?" )
 			.append( whereJoinFragment( getRootAlias(), true, false ) )
 			.toString();
 
 		String sql = select.setOuterJoins( "", "" )
 				.setWhereClause( whereClause )
 				.toStatementString();
 		///////////////////////////////////////////////////////////////////////
 
 		Object[] snapshot = new Object[ naturalIdPropertyCount ];
 		try {
 			PreparedStatement ps = session.getTransactionCoordinator()
 					.getJdbcCoordinator()
 					.getStatementPreparer()
 					.prepareStatement( sql );
 			try {
 				getIdentifierType().nullSafeSet( ps, id, 1, session );
 				ResultSet rs = ps.executeQuery();
 				try {
 					//if there is no resulting row, return null
 					if ( !rs.next() ) {
 						return null;
 					}
 					final EntityKey key = session.generateEntityKey( id, this );
 					Object owner = session.getPersistenceContext().getEntity( key );
 					for ( int i = 0; i < naturalIdPropertyCount; i++ ) {
 						snapshot[i] = extractionTypes[i].hydrate( rs, getPropertyAliases( "", naturalIdPropertyIndexes[i] ), session, null );
 						if (extractionTypes[i].isEntityType()) {
 							snapshot[i] = extractionTypes[i].resolve(snapshot[i], session, owner);
 						}
 					}
 					return snapshot;
 				}
 				finally {
 					rs.close();
 				}
 			}
 			finally {
 				ps.close();
 			}
 		}
 		catch ( SQLException e ) {
 			throw getFactory().getSQLExceptionHelper().convert(
 					e,
 					"could not retrieve snapshot: " + MessageHelper.infoString( this, id, getFactory() ),
 			        sql
 			);
 		}
 	}
 
 	protected String concretePropertySelectFragmentSansLeadingComma(String alias, boolean[] include) {
 		String concretePropertySelectFragment = concretePropertySelectFragment( alias, include );
 		int firstComma = concretePropertySelectFragment.indexOf( ", " );
 		if ( firstComma == 0 ) {
 			concretePropertySelectFragment = concretePropertySelectFragment.substring( 2 );
 		}
 		return concretePropertySelectFragment;
 	}
 
 	public boolean hasNaturalIdentifier() {
 		return entityMetamodel.hasNaturalIdentifier();
 	}
 
 	public void setPropertyValue(Object object, String propertyName, Object value) {
 		getEntityTuplizer().setPropertyValue( object, propertyName, value );
 	}
 
 	@Override
 	public EntityMode getEntityMode() {
 		return entityMetamodel.getEntityMode();
 	}
 
 	@Override
 	public EntityTuplizer getEntityTuplizer() {
 		return entityMetamodel.getTuplizer();
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
index c8a87aa48b..e0540d435b 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/entity/JoinedSubclassEntityPersister.java
@@ -1,836 +1,837 @@
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
 package org.hibernate.persister.entity;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.AssertionFailure;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.QueryException;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
+import org.hibernate.engine.OptimisticLockStyle;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.Subclass;
 import org.hibernate.mapping.Table;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.sql.CaseFragment;
 import org.hibernate.sql.SelectFragment;
 import org.hibernate.type.StandardBasicTypes;
 import org.hibernate.type.Type;
 
 /**
  * An <tt>EntityPersister</tt> implementing the normalized "table-per-subclass"
  * mapping strategy
  *
  * @author Gavin King
  */
 public class JoinedSubclassEntityPersister extends AbstractEntityPersister {
 
 	// the class hierarchy structure
 	private final int tableSpan;
 	private final String[] tableNames;
 	private final String[] naturalOrderTableNames;
 	private final String[][] tableKeyColumns;
 	private final String[][] tableKeyColumnReaders;
 	private final String[][] tableKeyColumnReaderTemplates;
 	private final String[][] naturalOrderTableKeyColumns;
 	private final String[][] naturalOrderTableKeyColumnReaders;
 	private final String[][] naturalOrderTableKeyColumnReaderTemplates;
 	private final boolean[] naturalOrderCascadeDeleteEnabled;
 
 	private final String[] spaces;
 
 	private final String[] subclassClosure;
 
 	private final String[] subclassTableNameClosure;
 	private final String[][] subclassTableKeyColumnClosure;
 	private final boolean[] isClassOrSuperclassTable;
 
 	// properties of this class, including inherited properties
 	private final int[] naturalOrderPropertyTableNumbers;
 	private final int[] propertyTableNumbers;
 
 	// the closure of all properties in the entire hierarchy including
 	// subclasses and superclasses of this class
 	private final int[] subclassPropertyTableNumberClosure;
 
 	// the closure of all columns used by the entire hierarchy including
 	// subclasses and superclasses of this class
 	private final int[] subclassColumnTableNumberClosure;
 	private final int[] subclassFormulaTableNumberClosure;
 
 	private final boolean[] subclassTableSequentialSelect;
 	private final boolean[] subclassTableIsLazyClosure;
 
 	// subclass discrimination works by assigning particular
 	// values to certain combinations of null primary key
 	// values in the outer join using an SQL CASE
 	private final Map subclassesByDiscriminatorValue = new HashMap();
 	private final String[] discriminatorValues;
 	private final String[] notNullColumnNames;
 	private final int[] notNullColumnTableNumbers;
 
 	private final String[] constraintOrderedTableNames;
 	private final String[][] constraintOrderedKeyColumnNames;
 
 	private final String discriminatorSQLString;
 
 	// Span of the tables directly mapped by this entity and super-classes, if any
 	private final int coreTableSpan;
 	// only contains values for SecondaryTables, ie. not tables part of the "coreTableSpan"
 	private final boolean[] isNullableTable;
 
 	//INITIALIZATION:
 
 	public JoinedSubclassEntityPersister(
 			final PersistentClass persistentClass,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 
 		super( persistentClass, cacheAccessStrategy, factory );
 
 		// DISCRIMINATOR
 
 		final Object discriminatorValue;
 		if ( persistentClass.isPolymorphic() ) {
 			try {
 				discriminatorValue = new Integer( persistentClass.getSubclassId() );
 				discriminatorSQLString = discriminatorValue.toString();
 			}
 			catch ( Exception e ) {
 				throw new MappingException( "Could not format discriminator value to SQL string", e );
 			}
 		}
 		else {
 			discriminatorValue = null;
 			discriminatorSQLString = null;
 		}
 
-		if ( optimisticLockMode() > Versioning.OPTIMISTIC_LOCK_VERSION ) {
+		if ( optimisticLockStyle() == OptimisticLockStyle.ALL || optimisticLockStyle() == OptimisticLockStyle.DIRTY ) {
 			throw new MappingException( "optimistic-lock=all|dirty not supported for joined-subclass mappings [" + getEntityName() + "]" );
 		}
 
 		//MULTITABLES
 
 		final int idColumnSpan = getIdentifierColumnSpan();
 
 		ArrayList tables = new ArrayList();
 		ArrayList keyColumns = new ArrayList();
 		ArrayList keyColumnReaders = new ArrayList();
 		ArrayList keyColumnReaderTemplates = new ArrayList();
 		ArrayList cascadeDeletes = new ArrayList();
 		Iterator titer = persistentClass.getTableClosureIterator();
 		Iterator kiter = persistentClass.getKeyClosureIterator();
 		while ( titer.hasNext() ) {
 			Table tab = (Table) titer.next();
 			KeyValue key = (KeyValue) kiter.next();
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			tables.add( tabname );
 			String[] keyCols = new String[idColumnSpan];
 			String[] keyColReaders = new String[idColumnSpan];
 			String[] keyColReaderTemplates = new String[idColumnSpan];
 			Iterator citer = key.getColumnIterator();
 			for ( int k = 0; k < idColumnSpan; k++ ) {
 				Column column = (Column) citer.next();
 				keyCols[k] = column.getQuotedName( factory.getDialect() );
 				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
 				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			}
 			keyColumns.add( keyCols );
 			keyColumnReaders.add( keyColReaders );
 			keyColumnReaderTemplates.add( keyColReaderTemplates );
 			cascadeDeletes.add( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() );
 		}
 
 		//Span of the tables directly mapped by this entity and super-classes, if any
 		coreTableSpan = tables.size();
 
 		isNullableTable = new boolean[persistentClass.getJoinClosureSpan()];
 
 		int tableIndex = 0;
 		Iterator joinIter = persistentClass.getJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 
 			isNullableTable[tableIndex++] = join.isOptional();
 
 			Table table = join.getTable();
 
 			String tableName = table.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			tables.add( tableName );
 
 			KeyValue key = join.getKey();
 			int joinIdColumnSpan = key.getColumnSpan();
 
 			String[] keyCols = new String[joinIdColumnSpan];
 			String[] keyColReaders = new String[joinIdColumnSpan];
 			String[] keyColReaderTemplates = new String[joinIdColumnSpan];
 
 			Iterator citer = key.getColumnIterator();
 
 			for ( int k = 0; k < joinIdColumnSpan; k++ ) {
 				Column column = (Column) citer.next();
 				keyCols[k] = column.getQuotedName( factory.getDialect() );
 				keyColReaders[k] = column.getReadExpr( factory.getDialect() );
 				keyColReaderTemplates[k] = column.getTemplate( factory.getDialect(), factory.getSqlFunctionRegistry() );
 			}
 			keyColumns.add( keyCols );
 			keyColumnReaders.add( keyColReaders );
 			keyColumnReaderTemplates.add( keyColReaderTemplates );
 			cascadeDeletes.add( key.isCascadeDeleteEnabled() && factory.getDialect().supportsCascadeDelete() );
 		}
 
 		naturalOrderTableNames = ArrayHelper.toStringArray( tables );
 		naturalOrderTableKeyColumns = ArrayHelper.to2DStringArray( keyColumns );
 		naturalOrderTableKeyColumnReaders = ArrayHelper.to2DStringArray( keyColumnReaders );
 		naturalOrderTableKeyColumnReaderTemplates = ArrayHelper.to2DStringArray( keyColumnReaderTemplates );
 		naturalOrderCascadeDeleteEnabled = ArrayHelper.toBooleanArray( cascadeDeletes );
 
 		ArrayList subtables = new ArrayList();
 		ArrayList isConcretes = new ArrayList();
 		ArrayList isDeferreds = new ArrayList();
 		ArrayList isLazies = new ArrayList();
 
 		keyColumns = new ArrayList();
 		titer = persistentClass.getSubclassTableClosureIterator();
 		while ( titer.hasNext() ) {
 			Table tab = (Table) titer.next();
 			isConcretes.add( persistentClass.isClassOrSuperclassTable( tab ) );
 			isDeferreds.add( Boolean.FALSE );
 			isLazies.add( Boolean.FALSE );
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			subtables.add( tabname );
 			String[] key = new String[idColumnSpan];
 			Iterator citer = tab.getPrimaryKey().getColumnIterator();
 			for ( int k = 0; k < idColumnSpan; k++ ) {
 				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
 			}
 			keyColumns.add( key );
 		}
 
 		//Add joins
 		joinIter = persistentClass.getSubclassJoinClosureIterator();
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 
 			Table tab = join.getTable();
 
 			isConcretes.add( persistentClass.isClassOrSuperclassTable( tab ) );
 			isDeferreds.add( join.isSequentialSelect() );
 			isLazies.add( join.isLazy() );
 
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			subtables.add( tabname );
 			String[] key = new String[idColumnSpan];
 			Iterator citer = tab.getPrimaryKey().getColumnIterator();
 			for ( int k = 0; k < idColumnSpan; k++ ) {
 				key[k] = ( (Column) citer.next() ).getQuotedName( factory.getDialect() );
 			}
 			keyColumns.add( key );
 		}
 
 		String[] naturalOrderSubclassTableNameClosure = ArrayHelper.toStringArray( subtables );
 		String[][] naturalOrderSubclassTableKeyColumnClosure = ArrayHelper.to2DStringArray( keyColumns );
 		isClassOrSuperclassTable = ArrayHelper.toBooleanArray( isConcretes );
 		subclassTableSequentialSelect = ArrayHelper.toBooleanArray( isDeferreds );
 		subclassTableIsLazyClosure = ArrayHelper.toBooleanArray( isLazies );
 
 		constraintOrderedTableNames = new String[naturalOrderSubclassTableNameClosure.length];
 		constraintOrderedKeyColumnNames = new String[naturalOrderSubclassTableNameClosure.length][];
 		int currentPosition = 0;
 		for ( int i = naturalOrderSubclassTableNameClosure.length - 1; i >= 0; i--, currentPosition++ ) {
 			constraintOrderedTableNames[currentPosition] = naturalOrderSubclassTableNameClosure[i];
 			constraintOrderedKeyColumnNames[currentPosition] = naturalOrderSubclassTableKeyColumnClosure[i];
 		}
 
 		/**
 		 * Suppose an entity Client extends Person, mapped to the tables CLIENT and PERSON respectively.
 		 * For the Client entity:
 		 * naturalOrderTableNames -> PERSON, CLIENT; this reflects the sequence in which the tables are 
 		 * added to the meta-data when the annotated entities are processed.
 		 * However, in some instances, for example when generating joins, the CLIENT table needs to be 
 		 * the first table as it will the driving table.
 		 * tableNames -> CLIENT, PERSON
 		 */
 
 		tableSpan = naturalOrderTableNames.length;
 		tableNames = reverse( naturalOrderTableNames, coreTableSpan );
 		tableKeyColumns = reverse( naturalOrderTableKeyColumns, coreTableSpan );
 		tableKeyColumnReaders = reverse( naturalOrderTableKeyColumnReaders, coreTableSpan );
 		tableKeyColumnReaderTemplates = reverse( naturalOrderTableKeyColumnReaderTemplates, coreTableSpan );
 		subclassTableNameClosure = reverse( naturalOrderSubclassTableNameClosure, coreTableSpan );
 		subclassTableKeyColumnClosure = reverse( naturalOrderSubclassTableKeyColumnClosure, coreTableSpan );
 
 		spaces = ArrayHelper.join(
 				tableNames,
 				ArrayHelper.toStringArray( persistentClass.getSynchronizedTables() )
 		);
 
 		// Custom sql
 		customSQLInsert = new String[tableSpan];
 		customSQLUpdate = new String[tableSpan];
 		customSQLDelete = new String[tableSpan];
 		insertCallable = new boolean[tableSpan];
 		updateCallable = new boolean[tableSpan];
 		deleteCallable = new boolean[tableSpan];
 		insertResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
 		updateResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
 		deleteResultCheckStyles = new ExecuteUpdateResultCheckStyle[tableSpan];
 
 		PersistentClass pc = persistentClass;
 		int jk = coreTableSpan - 1;
 		while ( pc != null ) {
 			customSQLInsert[jk] = pc.getCustomSQLInsert();
 			insertCallable[jk] = customSQLInsert[jk] != null && pc.isCustomInsertCallable();
 			insertResultCheckStyles[jk] = pc.getCustomSQLInsertCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault(
 					customSQLInsert[jk], insertCallable[jk]
 			)
 					: pc.getCustomSQLInsertCheckStyle();
 			customSQLUpdate[jk] = pc.getCustomSQLUpdate();
 			updateCallable[jk] = customSQLUpdate[jk] != null && pc.isCustomUpdateCallable();
 			updateResultCheckStyles[jk] = pc.getCustomSQLUpdateCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( customSQLUpdate[jk], updateCallable[jk] )
 					: pc.getCustomSQLUpdateCheckStyle();
 			customSQLDelete[jk] = pc.getCustomSQLDelete();
 			deleteCallable[jk] = customSQLDelete[jk] != null && pc.isCustomDeleteCallable();
 			deleteResultCheckStyles[jk] = pc.getCustomSQLDeleteCheckStyle() == null
 					? ExecuteUpdateResultCheckStyle.determineDefault( customSQLDelete[jk], deleteCallable[jk] )
 					: pc.getCustomSQLDeleteCheckStyle();
 			jk--;
 			pc = pc.getSuperclass();
 		}
 
 		if ( jk != -1 ) {
 			throw new AssertionFailure( "Tablespan does not match height of joined-subclass hiearchy." );
 		}
 
 		joinIter = persistentClass.getJoinClosureIterator();
 		int j = coreTableSpan;
 		while ( joinIter.hasNext() ) {
 			Join join = (Join) joinIter.next();
 
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
 			j++;
 		}
 
 		// PROPERTIES
 		int hydrateSpan = getPropertySpan();
 		naturalOrderPropertyTableNumbers = new int[hydrateSpan];
 		propertyTableNumbers = new int[hydrateSpan];
 		Iterator iter = persistentClass.getPropertyClosureIterator();
 		int i = 0;
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			String tabname = prop.getValue().getTable().getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			propertyTableNumbers[i] = getTableId( tabname, tableNames );
 			naturalOrderPropertyTableNumbers[i] = getTableId( tabname, naturalOrderTableNames );
 			i++;
 		}
 
 		// subclass closure properties
 
 		//TODO: code duplication with SingleTableEntityPersister
 
 		ArrayList columnTableNumbers = new ArrayList();
 		ArrayList formulaTableNumbers = new ArrayList();
 		ArrayList propTableNumbers = new ArrayList();
 
 		iter = persistentClass.getSubclassPropertyClosureIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			Table tab = prop.getValue().getTable();
 			String tabname = tab.getQualifiedName(
 					factory.getDialect(),
 					factory.getSettings().getDefaultCatalogName(),
 					factory.getSettings().getDefaultSchemaName()
 			);
 			Integer tabnum = new Integer( getTableId( tabname, subclassTableNameClosure ) );
 			propTableNumbers.add( tabnum );
 
 			Iterator citer = prop.getColumnIterator();
 			while ( citer.hasNext() ) {
 				Selectable thing = (Selectable) citer.next();
 				if ( thing.isFormula() ) {
 					formulaTableNumbers.add( tabnum );
 				}
 				else {
 					columnTableNumbers.add( tabnum );
 				}
 			}
 
 		}
 
 		subclassColumnTableNumberClosure = ArrayHelper.toIntArray( columnTableNumbers );
 		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray( propTableNumbers );
 		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray( formulaTableNumbers );
 
 		// SUBCLASSES
 
 		int subclassSpan = persistentClass.getSubclassSpan() + 1;
 		subclassClosure = new String[subclassSpan];
 		subclassClosure[subclassSpan - 1] = getEntityName();
 		if ( persistentClass.isPolymorphic() ) {
 			subclassesByDiscriminatorValue.put( discriminatorValue, getEntityName() );
 			discriminatorValues = new String[subclassSpan];
 			discriminatorValues[subclassSpan - 1] = discriminatorSQLString;
 			notNullColumnTableNumbers = new int[subclassSpan];
 			final int id = getTableId(
 					persistentClass.getTable().getQualifiedName(
 							factory.getDialect(),
 							factory.getSettings().getDefaultCatalogName(),
 							factory.getSettings().getDefaultSchemaName()
 					),
 					subclassTableNameClosure
 			);
 			notNullColumnTableNumbers[subclassSpan - 1] = id;
 			notNullColumnNames = new String[subclassSpan];
 			notNullColumnNames[subclassSpan - 1] = subclassTableKeyColumnClosure[id][0]; //( (Column) model.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
 		}
 		else {
 			discriminatorValues = null;
 			notNullColumnTableNumbers = null;
 			notNullColumnNames = null;
 		}
 
 		iter = persistentClass.getSubclassIterator();
 		int k = 0;
 		while ( iter.hasNext() ) {
 			Subclass sc = (Subclass) iter.next();
 			subclassClosure[k] = sc.getEntityName();
 			try {
 				if ( persistentClass.isPolymorphic() ) {
 					// we now use subclass ids that are consistent across all
 					// persisters for a class hierarchy, so that the use of
 					// "foo.class = Bar" works in HQL
 					Integer subclassId = new Integer( sc.getSubclassId() );//new Integer(k+1);
 					subclassesByDiscriminatorValue.put( subclassId, sc.getEntityName() );
 					discriminatorValues[k] = subclassId.toString();
 					int id = getTableId(
 							sc.getTable().getQualifiedName(
 									factory.getDialect(),
 									factory.getSettings().getDefaultCatalogName(),
 									factory.getSettings().getDefaultSchemaName()
 							),
 							subclassTableNameClosure
 					);
 					notNullColumnTableNumbers[k] = id;
 					notNullColumnNames[k] = subclassTableKeyColumnClosure[id][0]; //( (Column) sc.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
 				}
 			}
 			catch ( Exception e ) {
 				throw new MappingException( "Error parsing discriminator value", e );
 			}
 			k++;
 		}
 
 		initLockers();
 
 		initSubclassPropertyAliasesMap( persistentClass );
 
 		postConstruct( mapping );
 
 	}
 
 	public JoinedSubclassEntityPersister(
 			final EntityBinding entityBinding,
 			final EntityRegionAccessStrategy cacheAccessStrategy,
 			final SessionFactoryImplementor factory,
 			final Mapping mapping) throws HibernateException {
 		super( entityBinding, cacheAccessStrategy, factory );
 		// TODO: implement!!! initializing final fields to null to make compiler happy
 		tableSpan = -1;
 		tableNames = null;
 		naturalOrderTableNames = null;
 		tableKeyColumns = null;
 		tableKeyColumnReaders = null;
 		tableKeyColumnReaderTemplates = null;
 		naturalOrderTableKeyColumns = null;
 		naturalOrderTableKeyColumnReaders = null;
 		naturalOrderTableKeyColumnReaderTemplates = null;
 		naturalOrderCascadeDeleteEnabled = null;
 		spaces = null;
 		subclassClosure = null;
 		subclassTableNameClosure = null;
 		subclassTableKeyColumnClosure = null;
 		isClassOrSuperclassTable = null;
 		naturalOrderPropertyTableNumbers = null;
 		propertyTableNumbers = null;
 		subclassPropertyTableNumberClosure = null;
 		subclassColumnTableNumberClosure = null;
 		subclassFormulaTableNumberClosure = null;
 		subclassTableSequentialSelect = null;
 		subclassTableIsLazyClosure = null;
 		discriminatorValues = null;
 		notNullColumnNames = null;
 		notNullColumnTableNumbers = null;
 		constraintOrderedTableNames = null;
 		constraintOrderedKeyColumnNames = null;
 		discriminatorSQLString = null;
 		coreTableSpan = -1;
 		isNullableTable = null;
 	}
 
 	protected boolean isNullableTable(int j) {
 		if ( j < coreTableSpan ) {
 			return false;
 		}
 		return isNullableTable[j - coreTableSpan];
 	}
 
 	protected boolean isSubclassTableSequentialSelect(int j) {
 		return subclassTableSequentialSelect[j] && !isClassOrSuperclassTable[j];
 	}
 
 	/*public void postInstantiate() throws MappingException {
 		super.postInstantiate();
 		//TODO: other lock modes?
 		loader = createEntityLoader(LockMode.NONE, CollectionHelper.EMPTY_MAP);
 	}*/
 
 	public String getSubclassPropertyTableName(int i) {
 		return subclassTableNameClosure[subclassPropertyTableNumberClosure[i]];
 	}
 
 	public Type getDiscriminatorType() {
 		return StandardBasicTypes.INTEGER;
 	}
 
 	public String getDiscriminatorSQLValue() {
 		return discriminatorSQLString;
 	}
 
 
 	public String getSubclassForDiscriminatorValue(Object value) {
 		return (String) subclassesByDiscriminatorValue.get( value );
 	}
 
 	public Serializable[] getPropertySpaces() {
 		return spaces; // don't need subclass tables, because they can't appear in conditions
 	}
 
 
 	protected String getTableName(int j) {
 		return naturalOrderTableNames[j];
 	}
 
 	protected String[] getKeyColumns(int j) {
 		return naturalOrderTableKeyColumns[j];
 	}
 
 	protected boolean isTableCascadeDeleteEnabled(int j) {
 		return naturalOrderCascadeDeleteEnabled[j];
 	}
 
 	protected boolean isPropertyOfTable(int property, int j) {
 		return naturalOrderPropertyTableNumbers[property] == j;
 	}
 
 	/**
 	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
 	 * depending upon the value of the <tt>lock</tt> parameter
 	 */
 	/*public Object load(Serializable id,	Object optionalObject, LockMode lockMode, SessionImplementor session)
 	throws HibernateException {
 
 		if ( log.isTraceEnabled() ) log.trace( "Materializing entity: " + MessageHelper.infoString(this, id) );
 
 		final UniqueEntityLoader loader = hasQueryLoader() ?
 				getQueryLoader() :
 				this.loader;
 		try {
 
 			final Object result = loader.load(id, optionalObject, session);
 
 			if (result!=null) lock(id, getVersion(result), result, lockMode, session);
 
 			return result;
 
 		}
 		catch (SQLException sqle) {
 			throw new JDBCException( "could not load by id: " +  MessageHelper.infoString(this, id), sqle );
 		}
 	}*/
 	private static final void reverse(Object[] objects, int len) {
 		Object[] temp = new Object[len];
 		for ( int i = 0; i < len; i++ ) {
 			temp[i] = objects[len - i - 1];
 		}
 		for ( int i = 0; i < len; i++ ) {
 			objects[i] = temp[i];
 		}
 	}
 
 
 	/**
 	 * Reverse the first n elements of the incoming array
 	 *
 	 * @param objects
 	 * @param n
 	 *
 	 * @return New array with the first n elements in reversed order
 	 */
 	private static String[] reverse(String[] objects, int n) {
 
 		int size = objects.length;
 		String[] temp = new String[size];
 
 		for ( int i = 0; i < n; i++ ) {
 			temp[i] = objects[n - i - 1];
 		}
 
 		for ( int i = n; i < size; i++ ) {
 			temp[i] = objects[i];
 		}
 
 		return temp;
 	}
 
 	/**
 	 * Reverse the first n elements of the incoming array
 	 *
 	 * @param objects
 	 * @param n
 	 *
 	 * @return New array with the first n elements in reversed order
 	 */
 	private static String[][] reverse(String[][] objects, int n) {
 		int size = objects.length;
 		String[][] temp = new String[size][];
 		for ( int i = 0; i < n; i++ ) {
 			temp[i] = objects[n - i - 1];
 		}
 
 		for ( int i = n; i < size; i++ ) {
 			temp[i] = objects[i];
 		}
 
 		return temp;
 	}
 
 
 	public String fromTableFragment(String alias) {
 		return getTableName() + ' ' + alias;
 	}
 
 	public String getTableName() {
 		return tableNames[0];
 	}
 
 	private static int getTableId(String tableName, String[] tables) {
 		for ( int j = 0; j < tables.length; j++ ) {
 			if ( tableName.equals( tables[j] ) ) {
 				return j;
 			}
 		}
 		throw new AssertionFailure( "Table " + tableName + " not found" );
 	}
 
 	public void addDiscriminatorToSelect(SelectFragment select, String name, String suffix) {
 		if ( hasSubclasses() ) {
 			select.setExtraSelectList( discriminatorFragment( name ), getDiscriminatorAlias() );
 		}
 	}
 
 	private CaseFragment discriminatorFragment(String alias) {
 		CaseFragment cases = getFactory().getDialect().createCaseFragment();
 
 		for ( int i = 0; i < discriminatorValues.length; i++ ) {
 			cases.addWhenColumnNotNull(
 					generateTableAlias( alias, notNullColumnTableNumbers[i] ),
 					notNullColumnNames[i],
 					discriminatorValues[i]
 			);
 		}
 
 		return cases;
 	}
 
 	public String filterFragment(String alias) {
 		return hasWhere() ?
 				" and " + getSQLWhereString( generateFilterConditionAlias( alias ) ) :
 				"";
 	}
 
 	public String generateFilterConditionAlias(String rootAlias) {
 		return generateTableAlias( rootAlias, tableSpan - 1 );
 	}
 
 	public String[] getIdentifierColumnNames() {
 		return tableKeyColumns[0];
 	}
 
 	public String[] getIdentifierColumnReaderTemplates() {
 		return tableKeyColumnReaderTemplates[0];
 	}
 
 	public String[] getIdentifierColumnReaders() {
 		return tableKeyColumnReaders[0];
 	}
 
 	public String[] toColumns(String alias, String propertyName) throws QueryException {
 		if ( ENTITY_CLASS.equals( propertyName ) ) {
 			// This doesn't actually seem to work but it *might*
 			// work on some dbs. Also it doesn't work if there
 			// are multiple columns of results because it
 			// is not accounting for the suffix:
 			// return new String[] { getDiscriminatorColumnName() };
 
 			return new String[] { discriminatorFragment( alias ).toFragmentString() };
 		}
 		else {
 			return super.toColumns( alias, propertyName );
 		}
 	}
 
 	protected int[] getPropertyTableNumbersInSelect() {
 		return propertyTableNumbers;
 	}
 
 	protected int getSubclassPropertyTableNumber(int i) {
 		return subclassPropertyTableNumberClosure[i];
 	}
 
 	public int getTableSpan() {
 		return tableSpan;
 	}
 
 	public boolean isMultiTable() {
 		return true;
 	}
 
 	protected int[] getSubclassColumnTableNumberClosure() {
 		return subclassColumnTableNumberClosure;
 	}
 
 	protected int[] getSubclassFormulaTableNumberClosure() {
 		return subclassFormulaTableNumberClosure;
 	}
 
 	protected int[] getPropertyTableNumbers() {
 		return naturalOrderPropertyTableNumbers;
 	}
 
 	protected String[] getSubclassTableKeyColumns(int j) {
 		return subclassTableKeyColumnClosure[j];
 	}
 
 	public String getSubclassTableName(int j) {
 		return subclassTableNameClosure[j];
 	}
 
 	public int getSubclassTableSpan() {
 		return subclassTableNameClosure.length;
 	}
 
 	protected boolean isSubclassTableLazy(int j) {
 		return subclassTableIsLazyClosure[j];
 	}
 
 
 	protected boolean isClassOrSuperclassTable(int j) {
 		return isClassOrSuperclassTable[j];
 	}
 
 	public String getPropertyTableName(String propertyName) {
 		Integer index = getEntityMetamodel().getPropertyIndexOrNull( propertyName );
 		if ( index == null ) {
 			return null;
 		}
 		return tableNames[propertyTableNumbers[index.intValue()]];
 	}
 
 	public String[] getConstraintOrderedTableNameClosure() {
 		return constraintOrderedTableNames;
 	}
 
 	public String[][] getContraintOrderedTableKeyColumnClosure() {
 		return constraintOrderedKeyColumnNames;
 	}
 
 	public String getRootTableName() {
 		return naturalOrderTableNames[0];
 	}
 
 	public String getRootTableAlias(String drivingAlias) {
 		return generateTableAlias( drivingAlias, getTableId( getRootTableName(), tableNames ) );
 	}
 
 	public Declarer getSubclassPropertyDeclarer(String propertyPath) {
 		if ( "class".equals( propertyPath ) ) {
 			// special case where we need to force incloude all subclass joins
 			return Declarer.SUBCLASS;
 		}
 		return super.getSubclassPropertyDeclarer( propertyPath );
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/persister/internal/PersisterFactoryImpl.java
index d2be88126f..aa086af441 100644
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
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
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
 		Class<? extends EntityPersister> persisterClass = metadata.getCustomEntityPersisterClass();
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
diff --git a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
index 7cc75f8c32..01a24332f0 100644
--- a/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/persister/spi/PersisterFactory.java
@@ -1,129 +1,129 @@
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
 package org.hibernate.persister.spi;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
 import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.PersistentClass;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.PluralAttributeBinding;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.persister.collection.CollectionPersister;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.service.Service;
 
 /**
  * Contract for creating persister instances (both {@link EntityPersister} and {@link CollectionPersister} varieties).
  *
  * @author Steve Ebersole
  */
 public interface PersisterFactory extends Service {
 
 	// TODO: is it really necessary to provide Configuration to CollectionPersisters ?
 	// Should it not be enough with associated class ? or why does EntityPersister's not get access to configuration ?
 	//
 	// The only reason I could see that Configuration gets passed to collection persisters
 	// is so that they can look up the dom4j node name of the entity element in case
 	// no explicit node name was applied at the collection element level.  Are you kidding me?
 	// Trivial to fix then.  Just store and expose the node name on the entity persister
 	// (which the collection persister looks up anyway via other means...).
 
 	/**
 	 * Create an entity persister instance.
 	 *
 	 * @param model The O/R mapping metamodel definition for the entity
 	 * @param cacheAccessStrategy The caching strategy for this entity
 	 * @param factory The session factory
 	 * @param cfg The overall mapping
 	 *
 	 * @return An appropriate entity persister instance.
 	 *
 	 * @throws HibernateException Indicates a problem building the persister.
 	 */
 	public EntityPersister createEntityPersister(
 			PersistentClass model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) throws HibernateException;
 
 	/**
 	 * Create an entity persister instance.
 	 *
 	 * @param model The O/R mapping metamodel definition for the entity
 	 * @param cacheAccessStrategy The caching strategy for this entity
 	 * @param factory The session factory
 	 * @param cfg The overall mapping
 	 *
 	 * @return An appropriate entity persister instance.
 	 *
 	 * @throws HibernateException Indicates a problem building the persister.
 	 */
 	public EntityPersister createEntityPersister(
 			EntityBinding model,
 			EntityRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory,
 			Mapping cfg) throws HibernateException;
 
 	/**
 	 * Create a collection persister instance.
 	 *
 	 * @param cfg The configuration
 	 * @param model The O/R mapping metamodel definition for the collection
 	 * @param cacheAccessStrategy The caching strategy for this collection
 	 * @param factory The session factory
 	 *
 	 * @return An appropriate collection persister instance.
 	 *
 	 * @throws HibernateException Indicates a problem building the persister.
 	 */
 	public CollectionPersister createCollectionPersister(
 			Configuration cfg,
 			Collection model,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException;
 
 	/**
 	 * Create a collection persister instance.
 	 *
 	 * @param metadata The metadata
 	 * @param model The O/R mapping metamodel definition for the collection
 	 * @param cacheAccessStrategy The caching strategy for this collection
 	 * @param factory The session factory
 	 *
 	 * @return An appropriate collection persister instance.
 	 *
 	 * @throws HibernateException Indicates a problem building the persister.
 	 */
 	public CollectionPersister createCollectionPersister(
 			MetadataImplementor metadata,
 			PluralAttributeBinding model,
 			CollectionRegionAccessStrategy cacheAccessStrategy,
 			SessionFactoryImplementor factory) throws HibernateException;
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
index 91c8a9c8a3..8e926af56f 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryFactoryImpl.java
@@ -1,60 +1,60 @@
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
 package org.hibernate.service.internal;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.service.Service;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
 
 /**
  * Acts as a {@link Service} in the {@link BasicServiceRegistryImpl} whose function is as a factory for
  * {@link SessionFactoryServiceRegistryImpl} implementations.
  *
  * @author Steve Ebersole
  */
 public class SessionFactoryServiceRegistryFactoryImpl implements SessionFactoryServiceRegistryFactory {
 	private final ServiceRegistryImplementor theBasicServiceRegistry;
 
 	public SessionFactoryServiceRegistryFactoryImpl(ServiceRegistryImplementor theBasicServiceRegistry) {
 		this.theBasicServiceRegistry = theBasicServiceRegistry;
 	}
 
 	@Override
 	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
 			SessionFactoryImplementor sessionFactory,
 			Configuration configuration) {
 		return new SessionFactoryServiceRegistryImpl( theBasicServiceRegistry, sessionFactory, configuration );
 	}
 
 	@Override
 	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
 			SessionFactoryImplementor sessionFactory,
 			MetadataImplementor metadata) {
 		return new SessionFactoryServiceRegistryImpl( theBasicServiceRegistry, sessionFactory, metadata );
 	}
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
index 38bb2053ef..82f5f04647 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/internal/SessionFactoryServiceRegistryImpl.java
@@ -1,107 +1,107 @@
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
 package org.hibernate.service.internal;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.service.Service;
 import org.hibernate.service.StandardSessionFactoryServiceInitiators;
 import org.hibernate.service.spi.ServiceInitiator;
 import org.hibernate.service.spi.ServiceRegistryAwareService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 import org.hibernate.service.spi.SessionFactoryServiceRegistry;
 
 /**
  * @author Steve Ebersole
  */
 public class SessionFactoryServiceRegistryImpl extends AbstractServiceRegistryImpl implements SessionFactoryServiceRegistry  {
 
 	// for now we need to hold on to the Configuration... :(
 	private final Configuration configuration;
 	private final MetadataImplementor metadata;
 	private final SessionFactoryImplementor sessionFactory;
 
 	@SuppressWarnings( {"unchecked"})
 	public SessionFactoryServiceRegistryImpl(
 			ServiceRegistryImplementor parent,
 			SessionFactoryImplementor sessionFactory,
 			Configuration configuration) {
 		super( parent );
 
 		this.sessionFactory = sessionFactory;
 		this.configuration = configuration;
 		this.metadata = null;
 
 		// for now, just use the standard initiator list
 		for ( SessionFactoryServiceInitiator initiator : StandardSessionFactoryServiceInitiators.LIST ) {
 			// create the bindings up front to help identify to which registry services belong
 			createServiceBinding( initiator );
 		}
 	}
 
 	@SuppressWarnings( {"unchecked"})
 	public SessionFactoryServiceRegistryImpl(
 			ServiceRegistryImplementor parent,
 			SessionFactoryImplementor sessionFactory,
 			MetadataImplementor metadata) {
 		super( parent );
 
 		this.sessionFactory = sessionFactory;
 		this.configuration = null;
 		this.metadata = metadata;
 
 		// for now, just use the standard initiator list
 		for ( SessionFactoryServiceInitiator initiator : StandardSessionFactoryServiceInitiators.LIST ) {
 			// create the bindings up front to help identify to which registry services belong
 			createServiceBinding( initiator );
 		}
 	}
 
 	@Override
 	public <R extends Service> R initiateService(ServiceInitiator<R> serviceInitiator) {
 		// todo : add check/error for unexpected initiator types?
 		SessionFactoryServiceInitiator<R> sessionFactoryServiceInitiator =
 				(SessionFactoryServiceInitiator<R>) serviceInitiator;
 		if ( metadata != null ) {
 			return sessionFactoryServiceInitiator.initiateService( sessionFactory, metadata, this );
 		}
 		else if ( configuration != null ) {
 			return sessionFactoryServiceInitiator.initiateService( sessionFactory, configuration, this );
 		}
 		else {
 			throw new IllegalStateException( "Both metadata and configuration are null." );
 		}
 	}
 
 	@Override
 	protected <T extends Service> void configureService(T service) {
 		applyInjections( service );
 
 		if ( ServiceRegistryAwareService.class.isInstance( service ) ) {
 			( (ServiceRegistryAwareService) service ).injectServices( this );
 		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java
index 047ff3f90f..c646784c85 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceInitiator.java
@@ -1,63 +1,63 @@
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
 package org.hibernate.service.spi;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.service.Service;
 
 /**
  * @author Steve Ebersole
  */
 public interface SessionFactoryServiceInitiator<R extends Service> extends ServiceInitiator<R>{
 	/**
 	 * Initiates the managed service.
 	 * <p/>
 	 * Note for implementors: signature is guaranteed to change once redesign of SessionFactory building is complete
 	 *
 	 * @param sessionFactory The session factory.  Note the the session factory is still in flux; care needs to be taken
 	 * in regards to what you call.
 	 * @param configuration The configuration.
 	 * @param registry The service registry.  Can be used to locate services needed to fulfill initiation.
 	 *
 	 * @return The initiated service.
 	 */
 	public R initiateService(SessionFactoryImplementor sessionFactory, Configuration configuration, ServiceRegistryImplementor registry);
 
 	/**
 	 * Initiates the managed service.
 	 * <p/>
 	 * Note for implementors: signature is guaranteed to change once redesign of SessionFactory building is complete
 	 *
 	 * @param sessionFactory The session factory.  Note the the session factory is still in flux; care needs to be taken
 	 * in regards to what you call.
 	 * @param metadata The configuration.
 	 * @param registry The service registry.  Can be used to locate services needed to fulfill initiation.
 	 *
 	 * @return The initiated service.
 	 */
 	public R initiateService(SessionFactoryImplementor sessionFactory, MetadataImplementor metadata, ServiceRegistryImplementor registry);
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
index d73c8fcb43..f1e7206e2f 100644
--- a/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/service/spi/SessionFactoryServiceRegistryFactory.java
@@ -1,70 +1,70 @@
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
 package org.hibernate.service.spi;
 
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.service.Service;
 import org.hibernate.service.internal.SessionFactoryServiceRegistryImpl;
 
 /**
  * Contract for builder of {@link SessionFactoryServiceRegistry} instances.  Defined as a service to
  * "sit inside" the {@link org.hibernate.service.BasicServiceRegistry}.
  *
  * @author Steve Ebersole
  */
 public interface SessionFactoryServiceRegistryFactory extends Service {
 	/**
 	 * Create the registry.
 	 *
 	 * @todo : fully expect this signature to change!
 	 *
 	 * @param sessionFactory The (in flux) session factory.  Generally this is useful for grabbing a reference for later
 	 * 		use.  However, care should be taken when invoking on the session factory until after it has been fully
 	 * 		initialized.
 	 * @param configuration The configuration object.
 	 *
 	 * @return The registry
 	 */
 	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
 			SessionFactoryImplementor sessionFactory,
 			Configuration configuration);
 
 	/**
 	 * Create the registry.
 	 *
 	 * @todo : fully expect this signature to change!
 	 *
 	 * @param sessionFactory The (in flux) session factory.  Generally this is useful for grabbing a reference for later
 	 * 		use.  However, care should be taken when invoking on the session factory until after it has been fully
 	 * 		initialized.
 	 * @param metadata The configuration object.
 	 *
 	 * @return The registry
 	 */
 	public SessionFactoryServiceRegistryImpl buildServiceRegistry(
 			SessionFactoryImplementor sessionFactory,
 			MetadataImplementor metadata);
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/stat/internal/StatisticsInitiator.java b/hibernate-core/src/main/java/org/hibernate/stat/internal/StatisticsInitiator.java
index bceb032269..212850290f 100644
--- a/hibernate-core/src/main/java/org/hibernate/stat/internal/StatisticsInitiator.java
+++ b/hibernate-core/src/main/java/org/hibernate/stat/internal/StatisticsInitiator.java
@@ -1,120 +1,120 @@
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
 package org.hibernate.stat.internal;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.HibernateException;
 import org.hibernate.cfg.Configuration;
 import org.hibernate.engine.spi.SessionFactoryImplementor;
 import org.hibernate.internal.CoreMessageLogger;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.config.spi.ConfigurationService;
 import org.hibernate.service.spi.ServiceRegistryImplementor;
 import org.hibernate.service.spi.SessionFactoryServiceInitiator;
 import org.hibernate.stat.spi.StatisticsFactory;
 import org.hibernate.stat.spi.StatisticsImplementor;
 
 /**
  * @author Steve Ebersole
  */
 public class StatisticsInitiator implements SessionFactoryServiceInitiator<StatisticsImplementor> {
 	private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, StatisticsInitiator.class.getName() );
 
 	public static final StatisticsInitiator INSTANCE = new StatisticsInitiator();
 
 	/**
 	 * Names the {@link StatisticsFactory} to use.  Recognizes both a class name as well as an instance of
 	 * {@link StatisticsFactory}.
 	 */
 	public static final String STATS_BUILDER = "hibernate.stats.factory";
 
 	@Override
 	public Class<StatisticsImplementor> getServiceInitiated() {
 		return StatisticsImplementor.class;
 	}
 
 	@Override
 	public StatisticsImplementor initiateService(
 			SessionFactoryImplementor sessionFactory,
 			Configuration configuration,
 			ServiceRegistryImplementor registry) {
 		final Object configValue = configuration.getProperties().get( STATS_BUILDER );
 		return initiateServiceInternal( sessionFactory, configValue, registry );
 	}
 
 	@Override
 	public StatisticsImplementor initiateService(
 			SessionFactoryImplementor sessionFactory,
 			MetadataImplementor metadata,
 			ServiceRegistryImplementor registry) {
 		ConfigurationService configurationService =  registry.getService( ConfigurationService.class );
 		final Object configValue = configurationService.getSetting( STATS_BUILDER, null );
 		return initiateServiceInternal( sessionFactory, configValue, registry );
 	}
 
 	private StatisticsImplementor initiateServiceInternal(
 			SessionFactoryImplementor sessionFactory,
 			Object configValue,
 			ServiceRegistryImplementor registry) {
 
 		StatisticsFactory statisticsFactory;
 		if ( configValue == null ) {
 			statisticsFactory = DEFAULT_STATS_BUILDER;
 		}
 		else if ( StatisticsFactory.class.isInstance( configValue ) ) {
 			statisticsFactory = (StatisticsFactory) configValue;
 		}
 		else {
 			// assume it names the factory class
 			final ClassLoaderService classLoaderService = registry.getService( ClassLoaderService.class );
 			try {
 				statisticsFactory = (StatisticsFactory) classLoaderService.classForName( configValue.toString() ).newInstance();
 			}
 			catch (HibernateException e) {
 				throw e;
 			}
 			catch (Exception e) {
 				throw new HibernateException(
 						"Unable to instantiate specified StatisticsFactory implementation [" + configValue.toString() + "]",
 						e
 				);
 			}
 		}
 
 		StatisticsImplementor statistics = statisticsFactory.buildStatistics( sessionFactory );
 		final boolean enabled = sessionFactory.getSettings().isStatisticsEnabled();
 		statistics.setStatisticsEnabled( enabled );
 		LOG.debugf( "Statistics initialized [enabled=%s]", enabled );
 		return statistics;
 	}
 
 	private static StatisticsFactory DEFAULT_STATS_BUILDER = new StatisticsFactory() {
 		@Override
 		public StatisticsImplementor buildStatistics(SessionFactoryImplementor sessionFactory) {
 			return new ConcurrentStatisticsImpl( sessionFactory );
 		}
 	};
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
index 1ada28542c..ebdc7e8178 100644
--- a/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
+++ b/hibernate-core/src/main/java/org/hibernate/tuple/entity/EntityMetamodel.java
@@ -1,928 +1,952 @@
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
+import org.hibernate.engine.OptimisticLockStyle;
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
-	private final int optimisticLockMode;
+	private final OptimisticLockStyle optimisticLockStyle;
 
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
 
-		optimisticLockMode = persistentClass.getOptimisticLockMode();
-		if ( optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION && !dynamicUpdate ) {
+		optimisticLockStyle = interpretOptLockMode( persistentClass.getOptimisticLockMode() );
+		final boolean isAllOrDirty =
+				optimisticLockStyle == OptimisticLockStyle.ALL
+						|| optimisticLockStyle == OptimisticLockStyle.DIRTY;
+		if ( isAllOrDirty && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
-		if ( versionPropertyIndex != NO_VERSION_INDX && optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION ) {
+		if ( versionPropertyIndex != NO_VERSION_INDX && isAllOrDirty ) {
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
 
+	private OptimisticLockStyle interpretOptLockMode(int optimisticLockMode) {
+		switch ( optimisticLockMode ) {
+			case Versioning.OPTIMISTIC_LOCK_NONE: {
+				return OptimisticLockStyle.NONE;
+			}
+			case Versioning.OPTIMISTIC_LOCK_DIRTY: {
+				return OptimisticLockStyle.DIRTY;
+			}
+			case Versioning.OPTIMISTIC_LOCK_ALL: {
+				return OptimisticLockStyle.ALL;
+			}
+			default: {
+				return OptimisticLockStyle.VERSION;
+			}
+		}
+	}
+
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
 
-		optimisticLockMode = entityBinding.getOptimisticLockMode();
-		if ( optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION && !dynamicUpdate ) {
+		optimisticLockStyle = entityBinding.getOptimisticLockStyle();
+		final boolean isAllOrDirty =
+				optimisticLockStyle == OptimisticLockStyle.ALL
+						|| optimisticLockStyle == OptimisticLockStyle.DIRTY;
+		if ( isAllOrDirty && !dynamicUpdate ) {
 			throw new MappingException( "optimistic-lock=all|dirty requires dynamic-update=\"true\": " + name );
 		}
-		if ( versionPropertyIndex != NO_VERSION_INDX && optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION ) {
+		if ( versionPropertyIndex != NO_VERSION_INDX && isAllOrDirty ) {
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
 		Class<? extends EntityTuplizer> tuplizerClass = entityBinding.getCustomEntityTuplizerClass();
 
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
 
-	public int getOptimisticLockMode() {
-		return optimisticLockMode;
+	public OptimisticLockStyle getOptimisticLockStyle() {
+		return optimisticLockStyle;
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
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
index e412abcfe4..80cd39066f 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/AbstractBasicBindingTests.java
@@ -1,187 +1,187 @@
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
 
 import java.sql.Types;
 import java.util.Iterator;
 import java.util.Set;
 
 import junit.framework.Assert;
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.metamodel.MetadataSources;
+import org.hibernate.metamodel.binder.source.MetadataImplementor;
+import org.hibernate.metamodel.binder.source.internal.MetadataImpl;
 import org.hibernate.metamodel.domain.BasicType;
 import org.hibernate.metamodel.domain.SingularAttribute;
 import org.hibernate.metamodel.domain.TypeNature;
 import org.hibernate.metamodel.relational.Column;
 import org.hibernate.metamodel.relational.Datatype;
 import org.hibernate.metamodel.relational.SimpleValue;
-import org.hibernate.metamodel.source.internal.MetadataImpl;
-import org.hibernate.metamodel.source.spi.MetadataImplementor;
 import org.hibernate.service.BasicServiceRegistry;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.type.LongType;
 import org.hibernate.type.StringType;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertFalse;
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertNull;
 import static org.junit.Assert.assertSame;
 import static org.junit.Assert.assertTrue;
 
 /**
  * Basic tests of {@code hbm.xml} and annotation binding code
  *
  * @author Steve Ebersole
  */
 public abstract class AbstractBasicBindingTests extends BaseUnitTestCase {
 
 	private BasicServiceRegistryImpl serviceRegistry;
 	private MetadataSources sources;
 
 	@Before
 	public void setUp() {
 		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 		sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	protected BasicServiceRegistry basicServiceRegistry() {
 		return serviceRegistry;
 	}
 
 	@Test
 	public void testSimpleEntityMapping() {
 		MetadataImpl metadata = addSourcesForSimpleEntityBinding( sources );
 		EntityBinding entityBinding = metadata.getEntityBinding( SimpleEntity.class.getName() );
 		assertRoot( metadata, entityBinding );
 		assertIdAndSimpleProperty( entityBinding );
 
 		assertNull( entityBinding.getVersioningValueBinding() );
 	}
 
 	@Test
 	public void testSimpleVersionedEntityMapping() {
 		MetadataImpl metadata = addSourcesForSimpleVersionedEntityBinding( sources );
 		EntityBinding entityBinding = metadata.getEntityBinding( SimpleVersionedEntity.class.getName() );
 		assertIdAndSimpleProperty( entityBinding );
 
 		assertNotNull( entityBinding.getVersioningValueBinding() );
 		assertNotNull( entityBinding.getVersioningValueBinding().getAttribute() );
 	}
 
 	@Test
 	public void testEntityWithManyToOneMapping() {
 		MetadataImpl metadata = addSourcesForManyToOne( sources );
 
 		EntityBinding simpleEntityBinding = metadata.getEntityBinding( SimpleEntity.class.getName() );
 		assertIdAndSimpleProperty( simpleEntityBinding );
 
 		Set<EntityReferencingAttributeBinding> referenceBindings = simpleEntityBinding.getAttributeBinding( "id" )
 				.getEntityReferencingAttributeBindings();
 		assertEquals( "There should be only one reference binding", 1, referenceBindings.size() );
 
 		EntityReferencingAttributeBinding referenceBinding = referenceBindings.iterator().next();
 		EntityBinding referencedEntityBinding = referenceBinding.getReferencedEntityBinding();
 		// TODO - Is this assertion correct (HF)?
 		assertEquals( "Should be the same entity binding", referencedEntityBinding, simpleEntityBinding );
 
 		EntityBinding entityWithManyToOneBinding = metadata.getEntityBinding( ManyToOneEntity.class.getName() );
 		Iterator<EntityReferencingAttributeBinding> it = entityWithManyToOneBinding.getEntityReferencingAttributeBindings()
 				.iterator();
 		assertTrue( it.hasNext() );
 		assertSame( entityWithManyToOneBinding.getAttributeBinding( "simpleEntity" ), it.next() );
 		assertFalse( it.hasNext() );
 	}
 
 	public abstract MetadataImpl addSourcesForSimpleVersionedEntityBinding(MetadataSources sources);
 
 	public abstract MetadataImpl addSourcesForSimpleEntityBinding(MetadataSources sources);
 
 	public abstract MetadataImpl addSourcesForManyToOne(MetadataSources sources);
 
 	protected void assertIdAndSimpleProperty(EntityBinding entityBinding) {
 		assertNotNull( entityBinding );
 		assertNotNull( entityBinding.getEntityIdentifier() );
 		assertNotNull( entityBinding.getEntityIdentifier().getValueBinding() );
 
 		AttributeBinding idAttributeBinding = entityBinding.getAttributeBinding( "id" );
 		assertNotNull( idAttributeBinding );
 		assertSame( idAttributeBinding, entityBinding.getEntityIdentifier().getValueBinding() );
 		assertSame( LongType.INSTANCE, idAttributeBinding.getHibernateTypeDescriptor().getExplicitType() );
 
 		assertTrue( idAttributeBinding.getAttribute().isSingular() );
 		assertNotNull( idAttributeBinding.getAttribute() );
 		SingularAttribute singularIdAttribute =  ( SingularAttribute ) idAttributeBinding.getAttribute();
 		assertSame( TypeNature.BASIC, singularIdAttribute.getSingularAttributeType().getNature() );
 		BasicType basicIdAttributeType = ( BasicType ) singularIdAttribute.getSingularAttributeType();
 		assertSame( Long.class, basicIdAttributeType.getJavaType().getClassReference() );
 
 		assertNotNull( idAttributeBinding.getValue() );
 		assertTrue( idAttributeBinding.getValue() instanceof Column );
 		Datatype idDataType = ( (Column) idAttributeBinding.getValue() ).getDatatype();
 		assertSame( Long.class, idDataType.getJavaType() );
 		assertSame( Types.BIGINT, idDataType.getTypeCode() );
 		assertSame( LongType.INSTANCE.getName(), idDataType.getTypeName() );
 
 		AttributeBinding nameBinding = entityBinding.getAttributeBinding( "name" );
 		assertNotNull( nameBinding );
 		assertSame( StringType.INSTANCE, nameBinding.getHibernateTypeDescriptor().getExplicitType() );
 		assertNotNull( nameBinding.getAttribute() );
 		assertNotNull( nameBinding.getValue() );
 
 		assertTrue( nameBinding.getAttribute().isSingular() );
 		assertNotNull( nameBinding.getAttribute() );
 		SingularAttribute singularNameAttribute =  ( SingularAttribute ) nameBinding.getAttribute();
 		assertSame( TypeNature.BASIC, singularNameAttribute.getSingularAttributeType().getNature() );
 		BasicType basicNameAttributeType = ( BasicType ) singularNameAttribute.getSingularAttributeType();
 		assertSame( String.class, basicNameAttributeType.getJavaType().getClassReference() );
 
 		assertNotNull( nameBinding.getValue() );
 		// until HHH-6380 is fixed, need to call getValues()
 		assertEquals( 1, nameBinding.getValuesSpan() );
 		Iterator<SimpleValue> it = nameBinding.getValues().iterator();
 		assertTrue( it.hasNext() );
 		SimpleValue nameValue = it.next();
 		assertTrue( nameValue instanceof Column );
 		Datatype nameDataType = nameValue.getDatatype();
 		assertSame( String.class, nameDataType.getJavaType() );
 		assertSame( Types.VARCHAR, nameDataType.getTypeCode() );
 		assertSame( StringType.INSTANCE.getName(), nameDataType.getTypeName() );
 	}
 
 	protected void assertRoot(MetadataImplementor metadata, EntityBinding entityBinding) {
 		assertTrue( entityBinding.isRoot() );
 		assertSame( entityBinding, metadata.getRootEntityBinding( entityBinding.getEntity().getName() ) );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicAnnotationBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicAnnotationBindingTests.java
index 2d907b2993..e6aedf4e9e 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicAnnotationBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicAnnotationBindingTests.java
@@ -1,50 +1,50 @@
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
 
 import org.hibernate.metamodel.MetadataSources;
-import org.hibernate.metamodel.source.internal.MetadataImpl;
+import org.hibernate.metamodel.binder.source.internal.MetadataImpl;
 
 /**
  * Basic tests of annotation based binding code
  *
  * @author Hardy Ferentschik
  */
 public class BasicAnnotationBindingTests extends AbstractBasicBindingTests {
 	public MetadataImpl addSourcesForSimpleEntityBinding(MetadataSources sources) {
 		sources.addAnnotatedClass( SimpleEntity.class );
 		return (MetadataImpl) sources.buildMetadata();
 	}
 
 	public MetadataImpl addSourcesForSimpleVersionedEntityBinding(MetadataSources sources) {
 		sources.addAnnotatedClass( SimpleVersionedEntity.class );
 		return (MetadataImpl) sources.buildMetadata();
 	}
 
 	public MetadataImpl addSourcesForManyToOne(MetadataSources sources) {
 		sources.addAnnotatedClass( ManyToOneEntity.class );
 		sources.addAnnotatedClass( SimpleEntity.class );
 		return (MetadataImpl) sources.buildMetadata();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicHbmBindingTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicHbmBindingTests.java
index 927354b15e..34fcbbe37a 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicHbmBindingTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/BasicHbmBindingTests.java
@@ -1,50 +1,50 @@
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
 
 import org.hibernate.metamodel.MetadataSources;
-import org.hibernate.metamodel.source.internal.MetadataImpl;
+import org.hibernate.metamodel.binder.source.internal.MetadataImpl;
 
 /**
  * Basic tests of {@code hbm.xml} binding code
  *
  * @author Steve Ebersole
  */
 public class BasicHbmBindingTests extends AbstractBasicBindingTests {
 	public MetadataImpl addSourcesForSimpleEntityBinding(MetadataSources sources) {
 		sources.addResource( "org/hibernate/metamodel/binding/SimpleEntity.hbm.xml" );
 		return (MetadataImpl) sources.buildMetadata();
 	}
 
 	public MetadataImpl addSourcesForSimpleVersionedEntityBinding(MetadataSources sources) {
 		sources.addResource( "org/hibernate/metamodel/binding/SimpleVersionedEntity.hbm.xml" );
 		return (MetadataImpl) sources.buildMetadata();
 	}
 
 	public MetadataImpl addSourcesForManyToOne(MetadataSources sources) {
 		sources.addResource( "org/hibernate/metamodel/binding/ManyToOneEntity.hbm.xml" );
 		sources.addResource( "org/hibernate/metamodel/binding/SimpleEntity.hbm.xml" );
 		return (MetadataImpl) sources.buildMetadata();
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/MiscAnnotationBindingTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/MiscAnnotationBindingTest.java
index 71ba4f96f6..c99f767194 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/binding/MiscAnnotationBindingTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/binding/MiscAnnotationBindingTest.java
@@ -1,40 +1,40 @@
 package org.hibernate.metamodel.binding;
 
 import javax.persistence.Entity;
 import javax.persistence.Id;
 
 import org.junit.Test;
 
 import org.hibernate.annotations.Where;
 import org.hibernate.metamodel.MetadataSources;
-import org.hibernate.metamodel.source.internal.MetadataImpl;
+import org.hibernate.metamodel.binder.source.internal.MetadataImpl;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.assertEquals;
 
 /**
  * @author Hardy Ferentschik
  */
 public class MiscAnnotationBindingTest extends BaseUnitTestCase {
 	@Test
 	public void testWhereFilter() {
 
 
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addAnnotatedClass( Foo.class );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		EntityBinding binding = metadata.getEntityBinding( MiscAnnotationBindingTest.class.getName() + "$" + Foo.class.getSimpleName() );
 		assertEquals( "Wrong where filter", "1=1", binding.getWhereFilter() );
 	}
 
 	@Entity
 	@Where(clause = "1=1")
 	class Foo {
 		@Id
 		private long id;
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java
index 98e64cde85..c13ebf165f 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/entity/BaseAnnotationBindingTestCase.java
@@ -1,76 +1,76 @@
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
 package org.hibernate.metamodel.source.annotations.entity;
 
 import org.junit.After;
 
 import org.hibernate.metamodel.MetadataSources;
+import org.hibernate.metamodel.binder.source.internal.MetadataImpl;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 /**
  * @author Hardy Ferentschik
  */
 public abstract class BaseAnnotationBindingTestCase extends BaseUnitTestCase {
 	protected MetadataSources sources;
 	protected MetadataImpl meta;
 
 	@After
 	public void tearDown() {
 		sources = null;
 		meta = null;
 	}
 
 	public void buildMetadataSources(String ormPath, Class<?>... classes) {
 		sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		if ( ormPath != null ) {
 			sources.addResource( ormPath );
 		}
 		for ( Class clazz : classes ) {
 			sources.addAnnotatedClass( clazz );
 		}
 	}
 
 	public void buildMetadataSources(Class<?>... classes) {
 		buildMetadataSources( null, classes );
 	}
 
 	public EntityBinding getEntityBinding(Class<?> clazz) {
 		if ( meta == null ) {
 			meta = (MetadataImpl) sources.buildMetadata();
 		}
 		return meta.getEntityBinding( clazz.getName() );
 	}
 
 	public EntityBinding getRootEntityBinding(Class<?> clazz) {
 		if ( meta == null ) {
 			meta = (MetadataImpl) sources.buildMetadata();
 		}
 		return meta.getRootEntityBinding( clazz.getName() );
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
index 08558e753a..99ec615b07 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/global/FetchProfileBinderTest.java
@@ -1,147 +1,147 @@
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
 
 import java.util.Iterator;
 
 import org.jboss.jandex.Index;
 
 import org.hibernate.MappingException;
 import org.hibernate.annotations.FetchMode;
 import org.hibernate.annotations.FetchProfile;
 import org.hibernate.annotations.FetchProfiles;
 import org.hibernate.metamodel.MetadataSources;
+import org.hibernate.metamodel.binder.source.internal.MetadataImpl;
 import org.hibernate.metamodel.source.annotations.util.JandexHelper;
-import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 import org.hibernate.service.internal.BasicServiceRegistryImpl;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.fail;
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 
 /**
  * @author Hardy Ferentschik
  */
 public class FetchProfileBinderTest extends BaseUnitTestCase {
 
 	private BasicServiceRegistryImpl serviceRegistry;
 	private ClassLoaderService service;
 	private MetadataImpl meta;
 
 	@Before
 	public void setUp() {
 		serviceRegistry = (BasicServiceRegistryImpl) new ServiceRegistryBuilder().buildServiceRegistry();
 		service = serviceRegistry.getService( ClassLoaderService.class );
 		meta = (MetadataImpl) new MetadataSources( serviceRegistry ).buildMetadata();
 	}
 
 	@After
 	public void tearDown() {
 		serviceRegistry.destroy();
 	}
 
 	@Test
 	public void testSingleFetchProfile() {
 		@FetchProfile(name = "foo", fetchOverrides = {
 				@FetchProfile.FetchOverride(entity = Foo.class, association = "bar", mode = FetchMode.JOIN)
 		})
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 
 		FetchProfileBinder.bind( meta, index );
 
 		Iterator<org.hibernate.metamodel.binding.FetchProfile> mappedFetchProfiles = meta.getFetchProfiles().iterator();
 		assertTrue( mappedFetchProfiles.hasNext() );
 		org.hibernate.metamodel.binding.FetchProfile profile = mappedFetchProfiles.next();
 		assertEquals( "Wrong fetch profile name", "foo", profile.getName() );
 		org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 		assertEquals( "Wrong association name", "bar", fetch.getAssociation() );
 		assertEquals( "Wrong association type", Foo.class.getName(), fetch.getEntity() );
 	}
 
 	@Test
 	public void testFetchProfiles() {
 		Index index = JandexHelper.indexForClass( service, FooBar.class );
 		FetchProfileBinder.bind( meta, index );
 
 		Iterator<org.hibernate.metamodel.binding.FetchProfile> mappedFetchProfiles = meta.getFetchProfiles().iterator();
 		assertTrue( mappedFetchProfiles.hasNext() );
 		org.hibernate.metamodel.binding.FetchProfile profile = mappedFetchProfiles.next();
 		assertProfiles( profile );
 
 		assertTrue( mappedFetchProfiles.hasNext() );
 		profile = mappedFetchProfiles.next();
 		assertProfiles( profile );
 	}
 
 	private void assertProfiles(org.hibernate.metamodel.binding.FetchProfile profile) {
 		if ( profile.getName().equals( "foobar" ) ) {
 			org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 			assertEquals( "Wrong association name", "foobar", fetch.getAssociation() );
 			assertEquals( "Wrong association type", FooBar.class.getName(), fetch.getEntity() );
 		}
 		else if ( profile.getName().equals( "fubar" ) ) {
 			org.hibernate.metamodel.binding.FetchProfile.Fetch fetch = profile.getFetches().iterator().next();
 			assertEquals( "Wrong association name", "fubar", fetch.getAssociation() );
 			assertEquals( "Wrong association type", FooBar.class.getName(), fetch.getEntity() );
 		}
 		else {
 			fail( "Wrong fetch name:" + profile.getName() );
 		}
 	}
 
 	@Test(expected = MappingException.class)
 	public void testNonJoinFetchThrowsException() {
 		@FetchProfile(name = "foo", fetchOverrides = {
 				@FetchProfile.FetchOverride(entity = Foo.class, association = "bar", mode = FetchMode.SELECT)
 		})
 		class Foo {
 		}
 		Index index = JandexHelper.indexForClass( service, Foo.class );
 
 		FetchProfileBinder.bind( meta, index );
 	}
 
 	@FetchProfiles( {
 			@FetchProfile(name = "foobar", fetchOverrides = {
 					@FetchProfile.FetchOverride(entity = FooBar.class, association = "foobar", mode = FetchMode.JOIN)
 			}),
 			@FetchProfile(name = "fubar", fetchOverrides = {
 					@FetchProfile.FetchOverride(entity = FooBar.class, association = "fubar", mode = FetchMode.JOIN)
 			})
 	})
 	class FooBar {
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParserTests.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParserTests.java
index ef2d70125d..aef0e32a01 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParserTests.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/OrmXmlParserTests.java
@@ -1,69 +1,69 @@
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
 package org.hibernate.metamodel.source.annotations.xml;
 
 import org.junit.Test;
 
 import org.hibernate.metamodel.MetadataSources;
+import org.hibernate.metamodel.binder.MappingException;
+import org.hibernate.metamodel.binder.source.internal.MetadataImpl;
 import org.hibernate.metamodel.binding.EntityBinding;
-import org.hibernate.metamodel.source.MappingException;
-import org.hibernate.metamodel.source.internal.MetadataImpl;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.assertNotNull;
 
 /**
  * @author Hardy Ferentschik
  */
 public class OrmXmlParserTests extends BaseUnitTestCase {
 	@Test
 	public void testSimpleOrmVersion2() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addResource( "org/hibernate/metamodel/source/annotations/xml/orm-father.xml" );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		EntityBinding binding = metadata.getEntityBinding( Father.class.getName() );
 		assertNotNull( binding );
 	}
 
 	@Test
 	public void testSimpleOrmVersion1() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addResource( "org/hibernate/metamodel/source/annotations/xml/orm-star.xml" );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		EntityBinding binding = metadata.getEntityBinding( Star.class.getName() );
 		assertNotNull( binding );
 	}
 
 	@Test(expected = MappingException.class)
 	public void testInvalidOrmXmlThrowsException() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addResource( "org/hibernate/metamodel/source/annotations/xml/orm-invalid.xml" );
 		sources.buildMetadata();
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/mocker/XmlHelper.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/mocker/XmlHelper.java
index a6f0115d8a..8e4091c024 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/mocker/XmlHelper.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/annotations/xml/mocker/XmlHelper.java
@@ -1,79 +1,79 @@
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
 package org.hibernate.metamodel.source.annotations.xml.mocker;
 
 import java.io.InputStream;
 import java.net.URL;
 import javax.xml.bind.JAXBContext;
 import javax.xml.bind.JAXBElement;
 import javax.xml.bind.JAXBException;
 import javax.xml.bind.Unmarshaller;
 import javax.xml.transform.stream.StreamSource;
 import javax.xml.validation.Schema;
 import javax.xml.validation.SchemaFactory;
 
 import org.hibernate.internal.CoreMessageLogger;
 import org.jboss.logging.Logger;
 import org.xml.sax.SAXException;
 
-import org.hibernate.metamodel.source.Origin;
-import org.hibernate.metamodel.source.internal.JaxbRoot;
+import org.hibernate.metamodel.binder.Origin;
+import org.hibernate.metamodel.binder.source.internal.JaxbRoot;
 import org.hibernate.service.classloading.spi.ClassLoaderService;
 
 /**
  * @author Hardy Ferentschik
  */
 public class XmlHelper {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger( CoreMessageLogger.class, XmlHelper.class.getName() );
 
     private XmlHelper() {
     }
 
     public static <T> JaxbRoot<T> unmarshallXml(String fileName, String schemaName, Class<T> clazz, ClassLoaderService classLoaderService)
             throws JAXBException {
         Schema schema = getMappingSchema( schemaName, classLoaderService );
         InputStream in = classLoaderService.locateResourceStream( fileName );
         JAXBContext jc = JAXBContext.newInstance( clazz );
         Unmarshaller unmarshaller = jc.createUnmarshaller();
         unmarshaller.setSchema( schema );
         StreamSource stream = new StreamSource( in );
         JAXBElement<T> elem = unmarshaller.unmarshal( stream, clazz );
         Origin origin = new Origin( null, fileName );
         return new JaxbRoot<T>( elem.getValue(), origin );
     }
 
     private static Schema getMappingSchema(String schemaVersion, ClassLoaderService classLoaderService) {
         URL schemaUrl = classLoaderService.locateResource( schemaVersion );
         SchemaFactory sf = SchemaFactory.newInstance( javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI );
         Schema schema = null;
         try {
             schema = sf.newSchema( schemaUrl );
         }
         catch ( SAXException e ) {
             LOG.debugf( "Unable to create schema for %s: %s", schemaVersion, e.getMessage() );
         }
         return schema;
     }
 }
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/MetadataImplTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/MetadataImplTest.java
index dd35e2836d..0ff4808c7c 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/MetadataImplTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/MetadataImplTest.java
@@ -1,113 +1,113 @@
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
 
 import java.util.Iterator;
 
 import org.junit.Test;
 
-import org.hibernate.EmptyInterceptor;
 import org.hibernate.HibernateException;
 import org.hibernate.SessionFactory;
 import org.hibernate.metamodel.Metadata;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
+import org.hibernate.metamodel.binder.source.internal.MetadataImpl;
+import org.hibernate.metamodel.binder.source.internal.SessionFactoryBuilderImpl;
 import org.hibernate.metamodel.binding.FetchProfile;
-import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 
 import static junit.framework.Assert.assertEquals;
 import static junit.framework.Assert.assertFalse;
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertSame;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * @author Hardy Ferentschik
  */
 public class MetadataImplTest extends BaseUnitTestCase {
 
 	@Test(expected = IllegalArgumentException.class)
 	public void testAddingNullClass() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addClass( null );
 		sources.buildMetadata();
 	}
 
 	@Test(expected = IllegalArgumentException.class)
 	public void testAddingNullPackageName() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addPackage( null );
 		sources.buildMetadata();
 	}
 
 	@Test(expected = HibernateException.class)
 	public void testAddingNonExistingPackageName() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addPackage( "not.a.package" );
 		sources.buildMetadata();
 	}
 
 	@Test
 	public void testAddingPackageName() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addPackage( "org.hibernate.metamodel.source.internal" );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		assertFetchProfile( metadata );
 	}
 
 	@Test
 	public void testAddingPackageNameWithTrailingDot() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addPackage( "org.hibernate.metamodel.source.internal." );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 
 		assertFetchProfile( metadata );
 	}
 
 	@Test
 	public void testGettingSessionFactoryBuilder() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		Metadata metadata = sources.buildMetadata();
 
 		SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();
 		assertNotNull( sessionFactoryBuilder );
 		assertTrue( SessionFactoryBuilderImpl.class.isInstance( sessionFactoryBuilder ) );
 
 		SessionFactory sessionFactory = metadata.buildSessionFactory();
 		assertNotNull( sessionFactory );
 	}
 
 	private void assertFetchProfile(MetadataImpl metadata) {
 		Iterator<FetchProfile> profiles = metadata.getFetchProfiles().iterator();
 		assertTrue( profiles.hasNext() );
 		FetchProfile profile = profiles.next();
 		assertEquals( "wrong profile name", "package-configured-profile", profile.getName() );
 		assertFalse( profiles.hasNext() );
 	}
 }
 
 
diff --git a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java
index 75c5ec0ef1..18a156e36b 100644
--- a/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/metamodel/source/internal/SessionFactoryBuilderImplTest.java
@@ -1,195 +1,197 @@
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
 import java.util.Iterator;
 
 import org.junit.Test;
 
 import org.hibernate.CallbackException;
 import org.hibernate.EmptyInterceptor;
 import org.hibernate.EntityMode;
 import org.hibernate.Interceptor;
 import org.hibernate.ObjectNotFoundException;
 import org.hibernate.SessionFactory;
 import org.hibernate.Transaction;
 import org.hibernate.metamodel.MetadataSources;
 import org.hibernate.metamodel.SessionFactoryBuilder;
+import org.hibernate.metamodel.binder.source.internal.MetadataImpl;
+import org.hibernate.metamodel.binder.source.internal.SessionFactoryBuilderImpl;
 import org.hibernate.proxy.EntityNotFoundDelegate;
 import org.hibernate.service.ServiceRegistryBuilder;
 import org.hibernate.testing.junit4.BaseUnitTestCase;
 import org.hibernate.type.Type;
 
 import static junit.framework.Assert.assertNotNull;
 import static junit.framework.Assert.assertSame;
 import static junit.framework.Assert.assertTrue;
 
 /**
  * @author Gail Badner
  */
 public class SessionFactoryBuilderImplTest extends BaseUnitTestCase {
 
 	@Test
 	public void testGettingSessionFactoryBuilder() {
 		SessionFactoryBuilder sessionFactoryBuilder = getSessionFactoryBuilder();
 		assertNotNull( sessionFactoryBuilder );
 		assertTrue( SessionFactoryBuilderImpl.class.isInstance( sessionFactoryBuilder ) );
 	}
 
 	@Test
 	public void testBuildSessionFactoryWithDefaultOptions() {
 		SessionFactoryBuilder sessionFactoryBuilder = getSessionFactoryBuilder();
 		SessionFactory sessionFactory = sessionFactoryBuilder.buildSessionFactory();
 		assertSame( EmptyInterceptor.INSTANCE, sessionFactory.getSessionFactoryOptions().getInterceptor() );
 		assertTrue( EntityNotFoundDelegate.class.isInstance(
 				sessionFactory.getSessionFactoryOptions().getEntityNotFoundDelegate()
 		) );
 	}
 
 	@Test
 	public void testBuildSessionFactoryWithUpdatedOptions() {
 		SessionFactoryBuilder sessionFactoryBuilder = getSessionFactoryBuilder();
 		Interceptor interceptor = new AnInterceptor();
 		EntityNotFoundDelegate entityNotFoundDelegate = new EntityNotFoundDelegate() {
 			@Override
 			public void handleEntityNotFound(String entityName, Serializable id) {
 				throw new ObjectNotFoundException( id, entityName );
 			}
 		};
 		sessionFactoryBuilder.with( interceptor );
 		sessionFactoryBuilder.with( entityNotFoundDelegate );
 		SessionFactory sessionFactory = sessionFactoryBuilder.buildSessionFactory();
 		assertSame( interceptor, sessionFactory.getSessionFactoryOptions().getInterceptor() );
 		assertSame( entityNotFoundDelegate, sessionFactory.getSessionFactoryOptions().getEntityNotFoundDelegate() );
 	}
 
 	private SessionFactoryBuilder getSessionFactoryBuilder() {
 		MetadataSources sources = new MetadataSources( new ServiceRegistryBuilder().buildServiceRegistry() );
 		sources.addAnnotatedClass( SimpleEntity.class );
 		MetadataImpl metadata = (MetadataImpl) sources.buildMetadata();
 		return  metadata.getSessionFactoryBuilder();
 	}
 
 	private static class AnInterceptor implements Interceptor {
 		private static final Interceptor INSTANCE = EmptyInterceptor.INSTANCE;
 
 		@Override
 		public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
 				throws CallbackException {
 			return INSTANCE.onLoad( entity, id, state, propertyNames, types );
 		}
 
 		@Override
 		public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types)
 				throws CallbackException {
 			return INSTANCE.onFlushDirty( entity, id, currentState, previousState, propertyNames, types );
 		}
 
 		@Override
 		public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
 				throws CallbackException {
 			return INSTANCE.onSave( entity, id, state, propertyNames, types );
 		}
 
 		@Override
 		public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
 				throws CallbackException {
 			INSTANCE.onDelete( entity, id, state, propertyNames, types );
 		}
 
 		@Override
 		public void onCollectionRecreate(Object collection, Serializable key) throws CallbackException {
 			INSTANCE.onCollectionRecreate( collection, key );
 		}
 
 		@Override
 		public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
 			INSTANCE.onCollectionRemove( collection, key );
 		}
 
 		@Override
 		public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
 			INSTANCE.onCollectionUpdate( collection, key );
 		}
 
 		@Override
 		public void preFlush(Iterator entities) throws CallbackException {
 			INSTANCE.preFlush( entities );
 		}
 
 		@Override
 		public void postFlush(Iterator entities) throws CallbackException {
 			INSTANCE.postFlush( entities );
 		}
 
 		@Override
 		public Boolean isTransient(Object entity) {
 			return INSTANCE.isTransient( entity );
 		}
 
 		@Override
 		public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
 			return INSTANCE.findDirty( entity, id, currentState, previousState, propertyNames, types );
 		}
 
 		@Override
 		public Object instantiate(String entityName, EntityMode entityMode, Serializable id)
 				throws CallbackException {
 			return INSTANCE.instantiate( entityName, entityMode, id );
 		}
 
 		@Override
 		public String getEntityName(Object object) throws CallbackException {
 			return INSTANCE.getEntityName( object );
 		}
 
 		@Override
 		public Object getEntity(String entityName, Serializable id) throws CallbackException {
 			return INSTANCE.getEntity( entityName, id );
 		}
 
 		@Override
 		public void afterTransactionBegin(Transaction tx) {
 			INSTANCE.afterTransactionBegin( tx );
 		}
 
 		@Override
 		public void beforeTransactionCompletion(Transaction tx) {
 			INSTANCE.beforeTransactionCompletion( tx );
 		}
 
 		@Override
 		public void afterTransactionCompletion(Transaction tx) {
 			INSTANCE.afterTransactionCompletion( tx );
 		}
 
 		@Override
 		public String onPrepareStatement(String sql) {
 			return INSTANCE.onPrepareStatement( sql );
 		}
 	}
 }
 
 
