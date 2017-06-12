diff --git a/hibernate-core/src/main/java/org/hibernate/annotations/Filter.java b/hibernate-core/src/main/java/org/hibernate/annotations/Filter.java
index 167588e096..ae74009254 100644
--- a/hibernate-core/src/main/java/org/hibernate/annotations/Filter.java
+++ b/hibernate-core/src/main/java/org/hibernate/annotations/Filter.java
@@ -1,46 +1,47 @@
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
 package org.hibernate.annotations;
 import java.lang.annotation.Retention;
 import java.lang.annotation.Target;
 
 import static java.lang.annotation.ElementType.FIELD;
 import static java.lang.annotation.ElementType.METHOD;
 import static java.lang.annotation.ElementType.TYPE;
 import static java.lang.annotation.RetentionPolicy.RUNTIME;
 
 /**
  * Add filters to an entity or a target entity of a collection
  *
  * @author Emmanuel Bernard
  * @author Matthew Inger
  * @author Magnus Sandberg
+ * @author Rob Worsnop
  */
 @Target({TYPE, METHOD, FIELD})
 @Retention(RUNTIME)
 public @interface Filter {
 	String name();
-
+	String table() default "";
 	String condition() default "";
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index a699d71edf..55f98cc4e3 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -1617,1553 +1617,1555 @@ public final class HbmBinder {
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
 				uk.setName("_UniqueKey");
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
 		while ( filters.hasNext() ) {
 			final Element filterElement = ( Element ) filters.next();
 			final String name = filterElement.attributeValue( "name" );
+			final String tableName = filterElement.attributeValue("table");
 			String condition = filterElement.getTextTrim();
 			if ( StringHelper.isEmpty(condition) ) condition = filterElement.attributeValue( "condition" );
 			if ( StringHelper.isEmpty(condition) ) {
 				condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 			}
 			if ( condition==null) {
 				throw new MappingException("no filter condition found for filter: " + name);
 			}
 			if ( LOG.isDebugEnabled() ) {
 				LOG.debugf( "Applying many-to-many filter [%s] as [%s] to role [%s]", name, condition, collection.getRole() );
 			}
-			collection.addManyToManyFilter( name, condition );
+			collection.addManyToManyFilter( name, tableName, condition );
 		}
 	}
 
 	public static final FlushMode getFlushMode(String flushMode) {
 		if ( flushMode == null ) {
 			return null;
 		}
 		else if ( "auto".equals( flushMode ) ) {
 			return FlushMode.AUTO;
 		}
 		else if ( "commit".equals( flushMode ) ) {
 			return FlushMode.COMMIT;
 		}
 		else if ( "never".equals( flushMode ) ) {
 			return FlushMode.NEVER;
 		}
 		else if ( "manual".equals( flushMode ) ) {
 			return FlushMode.MANUAL;
 		}
 		else if ( "always".equals( flushMode ) ) {
 			return FlushMode.ALWAYS;
 		}
 		else {
 			throw new MappingException( "unknown flushmode" );
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
 
 		NamedQueryDefinition namedQuery = new NamedQueryDefinition(
 				queryName,
 				query,
 				cacheable,
 				region,
 				timeout,
 				fetchSize,
 				getFlushMode( queryElem.attributeValue( "flush-mode" ) ) ,
 				getCacheMode( cacheMode ),
 				readOnly,
 				comment,
 				getParameterTypes(queryElem)
 			);
 
 		mappings.addQuery( namedQuery.getName(), namedQuery );
 	}
 
 	public static CacheMode getCacheMode(String cacheMode) {
 		if (cacheMode == null) return null;
 		if ( "get".equals( cacheMode ) ) return CacheMode.GET;
 		if ( "ignore".equals( cacheMode ) ) return CacheMode.IGNORE;
 		if ( "normal".equals( cacheMode ) ) return CacheMode.NORMAL;
 		if ( "put".equals( cacheMode ) ) return CacheMode.PUT;
 		if ( "refresh".equals( cacheMode ) ) return CacheMode.REFRESH;
 		throw new MappingException("Unknown Cache Mode: " + cacheMode);
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
 
 	private static int getOptimisticLockMode(Attribute olAtt) throws MappingException {
 
 		if ( olAtt == null ) return Versioning.OPTIMISTIC_LOCK_VERSION;
 		String olMode = olAtt.getValue();
 		if ( olMode == null || "version".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_VERSION;
 		}
 		else if ( "dirty".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_DIRTY;
 		}
 		else if ( "all".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_ALL;
 		}
 		else if ( "none".equals( olMode ) ) {
 			return Versioning.OPTIMISTIC_LOCK_NONE;
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
 			if ( onlyInheritable & !inheritable ) {
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
+		final String tableName = filterElement.attributeValue("table");
 		String condition = filterElement.getTextTrim();
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = filterElement.attributeValue( "condition" );
 		}
 		//TODO: bad implementation, cos it depends upon ordering of mapping doc
 		//      fixing this requires that Collection/PersistentClass gain access
 		//      to the Mappings reference from Configuration (or the filterDefinitions
 		//      map directly) sometime during Configuration.buildSessionFactory
 		//      (after all the types/filter-defs are known and before building
 		//      persisters).
 		if ( StringHelper.isEmpty(condition) ) {
 			condition = model.getFilterDefinition(name).getDefaultFilterCondition();
 		}
 		if ( condition==null) {
 			throw new MappingException("no filter condition found for filter: " + name);
 		}
 		LOG.debugf( "Applying filter [%s] as [%s]", name, condition );
-		filterable.addFilter( name, condition );
+		filterable.addFilter( name, tableName, condition );
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
 		}
 	}
 
 	/**
 	 * For the given document, locate all extends attributes which refer to
 	 * entities (entity-name or class-name) not defined within said document.
 	 *
 	 * @param metadataXml The document to check
 	 * @param mappings The already processed mappings.
 	 * @return The list of unresolved extends names.
 	 */
 	public static java.util.List<String> getExtendsNeeded(XmlDocument metadataXml, Mappings mappings) {
 		java.util.List<String> extendz = new ArrayList<String>();
 		Iterator[] subclasses = new Iterator[3];
 		final Element hmNode = metadataXml.getDocumentTree().getRootElement();
 
 		Attribute packNode = hmNode.attribute( "package" );
 		final String packageName = packNode == null ? null : packNode.getValue();
 		if ( packageName != null ) {
 			mappings.setDefaultPackage( packageName );
 		}
 
 		// first, iterate over all elements capable of defining an extends attribute
 		// collecting all found extends references if they cannot be resolved
 		// against the already processed mappings.
 		subclasses[0] = hmNode.elementIterator( "subclass" );
 		subclasses[1] = hmNode.elementIterator( "joined-subclass" );
 		subclasses[2] = hmNode.elementIterator( "union-subclass" );
 
 		Iterator iterator = new JoinedIterator( subclasses );
 		while ( iterator.hasNext() ) {
 			final Element element = (Element) iterator.next();
 			final String extendsName = element.attributeValue( "extends" );
 			// mappings might contain either the "raw" extends name (in the case of
 			// an entity-name mapping) or a FQN (in the case of a POJO mapping).
 			if ( mappings.getClass( extendsName ) == null && mappings.getClass( getClassName( extendsName, mappings ) ) == null ) {
 				extendz.add( extendsName );
 			}
 		}
 
 		if ( !extendz.isEmpty() ) {
 			// we found some extends attributes referencing entities which were
 			// not already processed.  here we need to locate all entity-names
 			// and class-names contained in this document itself, making sure
 			// that these get removed from the extendz list such that only
 			// extends names which require us to delay processing (i.e.
 			// external to this document and not yet processed) are contained
 			// in the returned result
 			final java.util.Set<String> set = new HashSet<String>( extendz );
 			EntityElementHandler handler = new EntityElementHandler() {
 				public void handleEntity(String entityName, String className, Mappings mappings) {
 					if ( entityName != null ) {
 						set.remove( entityName );
 					}
 					else {
 						String fqn = getClassName( className, packageName );
 						set.remove( fqn );
 						if ( packageName != null ) {
 							set.remove( StringHelper.unqualify( fqn ) );
 						}
 					}
 				}
 			};
 			recognizeEntities( mappings, hmNode, handler );
 			extendz.clear();
 			extendz.addAll( set );
 		}
 
 		return extendz;
 	}
 
 	/**
 	 * Given an entity-containing-element (startNode) recursively locate all
 	 * entity names defined within that element.
 	 *
 	 * @param mappings The already processed mappings
 	 * @param startNode The containing element
 	 * @param handler The thing that knows what to do whenever we recognize an
 	 * entity-name
 	 */
 	private static void recognizeEntities(
 			Mappings mappings,
 	        final Element startNode,
 			EntityElementHandler handler) {
 		Iterator[] classes = new Iterator[4];
 		classes[0] = startNode.elementIterator( "class" );
 		classes[1] = startNode.elementIterator( "subclass" );
 		classes[2] = startNode.elementIterator( "joined-subclass" );
 		classes[3] = startNode.elementIterator( "union-subclass" );
 
 		Iterator classIterator = new JoinedIterator( classes );
 		while ( classIterator.hasNext() ) {
 			Element element = (Element) classIterator.next();
 			handler.handleEntity(
 					element.attributeValue( "entity-name" ),
 		            element.attributeValue( "name" ),
 			        mappings
 			);
 			recognizeEntities( mappings, element, handler );
 		}
 	}
 
 	private static interface EntityElementHandler {
 		public void handleEntity(String entityName, String className, Mappings mappings);
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
index 47bf034328..b62e923f35 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
@@ -1,1431 +1,1435 @@
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
 
 import javax.persistence.AttributeOverride;
 import javax.persistence.AttributeOverrides;
 import javax.persistence.ElementCollection;
 import javax.persistence.Embeddable;
 import javax.persistence.FetchType;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinColumns;
 import javax.persistence.JoinTable;
 import javax.persistence.ManyToMany;
 import javax.persistence.MapKey;
 import javax.persistence.MapKeyColumn;
 import javax.persistence.OneToMany;
 import java.util.Comparator;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Properties;
 
 import org.jboss.logging.Logger;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.BatchSize;
 import org.hibernate.annotations.Cache;
 import org.hibernate.annotations.CollectionId;
 import org.hibernate.annotations.CollectionType;
 import org.hibernate.annotations.Fetch;
 import org.hibernate.annotations.Filter;
 import org.hibernate.annotations.FilterJoinTable;
 import org.hibernate.annotations.FilterJoinTables;
 import org.hibernate.annotations.Filters;
 import org.hibernate.annotations.ForeignKey;
 import org.hibernate.annotations.Immutable;
 import org.hibernate.annotations.LazyCollection;
 import org.hibernate.annotations.LazyCollectionOption;
 import org.hibernate.annotations.Loader;
 import org.hibernate.annotations.ManyToAny;
 import org.hibernate.annotations.OptimisticLock;
 import org.hibernate.annotations.OrderBy;
 import org.hibernate.annotations.Parameter;
 import org.hibernate.annotations.Persister;
 import org.hibernate.annotations.SQLDelete;
 import org.hibernate.annotations.SQLDeleteAll;
 import org.hibernate.annotations.SQLInsert;
 import org.hibernate.annotations.SQLUpdate;
 import org.hibernate.annotations.Sort;
 import org.hibernate.annotations.SortType;
 import org.hibernate.annotations.Where;
 import org.hibernate.annotations.WhereJoinTable;
 import org.hibernate.annotations.common.AssertionFailure;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.annotations.common.reflection.XProperty;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotatedClassType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.CollectionSecondPass;
 import org.hibernate.cfg.Ejb3Column;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.IndexColumn;
 import org.hibernate.cfg.InheritanceState;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.PropertyData;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.PropertyHolderBuilder;
 import org.hibernate.cfg.PropertyInferredData;
 import org.hibernate.cfg.PropertyPreloadedData;
 import org.hibernate.cfg.SecondPass;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.Any;
 import org.hibernate.mapping.Backref;
 import org.hibernate.mapping.Collection;
 import org.hibernate.mapping.Column;
 import org.hibernate.mapping.Component;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.IdGenerator;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.KeyValue;
 import org.hibernate.mapping.ManyToOne;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.Property;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TypeDef;
 
 /**
  * Base class for binding different types of collections to Hibernate configuration objects.
  *
  * @author inger
  * @author Emmanuel Bernard
  */
 @SuppressWarnings({"unchecked", "serial"})
 public abstract class CollectionBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, CollectionBinder.class.getName());
 
 	protected Collection collection;
 	protected String propertyName;
 	PropertyHolder propertyHolder;
 	int batchSize;
 	private String mappedBy;
 	private XClass collectionType;
 	private XClass targetEntity;
 	private Mappings mappings;
 	private Ejb3JoinColumn[] inverseJoinColumns;
 	private String cascadeStrategy;
 	String cacheConcurrencyStrategy;
 	String cacheRegionName;
 	private boolean oneToMany;
 	protected IndexColumn indexColumn;
 	private String orderBy;
 	protected String hqlOrderBy;
 	private boolean isSorted;
 	private Class comparator;
 	private boolean hasToBeSorted;
 	protected boolean cascadeDeleteEnabled;
 	protected String mapKeyPropertyName;
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
 	protected HashMap<String, IdGenerator> localGenerators;
 	protected Map<XClass, InheritanceState> inheritanceStatePerClass;
 	private XClass declaringClass;
 	private boolean declaringClassSet;
 	private AccessType accessType;
 	private boolean hibernateExtensionMapping;
 
 	private String explicitType;
 	private Properties explicitTypeParameters = new Properties();
 
 	protected Mappings getMappings() {
 		return mappings;
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
 
 	public void setEjb3OrderBy(javax.persistence.OrderBy orderByAnn) {
 		if ( orderByAnn != null ) {
 			hqlOrderBy = orderByAnn.value();
 		}
 	}
 
 	public void setSqlOrderBy(OrderBy orderByAnn) {
 		if ( orderByAnn != null ) {
 			if ( !BinderHelper.isEmptyAnnotationValue( orderByAnn.clause() ) ) {
 				orderBy = orderByAnn.clause();
 			}
 		}
 	}
 
 	public void setSort(Sort sortAnn) {
 		if ( sortAnn != null ) {
 			isSorted = !SortType.UNSORTED.equals( sortAnn.type() );
 			if ( isSorted && SortType.COMPARATOR.equals( sortAnn.type() ) ) {
 				comparator = sortAnn.comparator();
 			}
 		}
 	}
 
 	/**
 	 * collection binder factory
 	 */
 	public static CollectionBinder getCollectionBinder(
 			String entityName,
 			XProperty property,
 			boolean isIndexed,
 			boolean isHibernateExtensionMapping,
 			Mappings mappings) {
 		CollectionBinder result;
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
 				result = new SetBinder();
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
 				result = new MapBinder();
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
-				collection.addManyToManyFilter( simpleFilter.name(), getCondition( simpleFilter ) );
+				collection.addManyToManyFilter( simpleFilter.name(), getTableName(simpleFilter), getCondition( simpleFilter ) );
 			}
 			else {
-				collection.addFilter( simpleFilter.name(), getCondition( simpleFilter ) );
+				collection.addFilter( simpleFilter.name(), getTableName(simpleFilter), getCondition( simpleFilter ) );
 			}
 		}
 		Filters filters = property.getAnnotation( Filters.class );
 		if ( filters != null ) {
 			for (Filter filter : filters.value()) {
 				if ( hasAssociationTable ) {
-					collection.addManyToManyFilter( filter.name(), getCondition( filter ) );
+					collection.addManyToManyFilter( filter.name(), getTableName(simpleFilter), getCondition( filter ) );
 				}
 				else {
-					collection.addFilter( filter.name(), getCondition( filter ) );
+					collection.addFilter( filter.name(), getTableName(simpleFilter), getCondition( filter ) );
 				}
 			}
 		}
 		FilterJoinTable simpleFilterJoinTable = property.getAnnotation( FilterJoinTable.class );
 		if ( simpleFilterJoinTable != null ) {
 			if ( hasAssociationTable ) {
-				collection.addFilter( simpleFilterJoinTable.name(), getCondition( simpleFilterJoinTable ) );
+				collection.addFilter( simpleFilterJoinTable.name(), null, getCondition( simpleFilterJoinTable ) );
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
-					collection.addFilter( filter.name(), getCondition( filter ) );
+					collection.addFilter( filter.name(), null, getCondition( filter ) );
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
+	
+	private String getTableName(Filter filter){
+		return BinderHelper.isEmptyAnnotationValue(filter.table())? null : filter.table();
+	}
 
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
 					.getRecursiveProperty( propRef )
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
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
index 012e6b912c..20a7676f0f 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/EntityBinder.java
@@ -1,999 +1,999 @@
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
 import java.util.List;
 import java.util.Map;
 import javax.persistence.Access;
 import javax.persistence.Entity;
 import javax.persistence.JoinColumn;
 import javax.persistence.JoinTable;
 import javax.persistence.PrimaryKeyJoinColumn;
 import javax.persistence.SecondaryTable;
 import javax.persistence.SecondaryTables;
 
 import org.jboss.logging.Logger;
 
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
 import org.hibernate.annotations.common.reflection.XAnnotatedElement;
 import org.hibernate.annotations.common.reflection.XClass;
 import org.hibernate.cfg.AccessType;
 import org.hibernate.cfg.AnnotationBinder;
 import org.hibernate.cfg.BinderHelper;
 import org.hibernate.cfg.Ejb3JoinColumn;
 import org.hibernate.cfg.InheritanceState;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.cfg.NamingStrategy;
 import org.hibernate.cfg.ObjectNameNormalizer;
 import org.hibernate.cfg.ObjectNameSource;
 import org.hibernate.cfg.PropertyHolder;
 import org.hibernate.cfg.UniqueConstraintHolder;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.FilterDefinition;
 import org.hibernate.internal.CoreMessageLogger;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.mapping.DependantValue;
 import org.hibernate.mapping.Join;
 import org.hibernate.mapping.PersistentClass;
 import org.hibernate.mapping.RootClass;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.mapping.Table;
 import org.hibernate.mapping.TableOwner;
 import org.hibernate.mapping.Value;
 
 /**
  * Stateful holder and processor for binding Entity information
  *
  * @author Emmanuel Bernard
  */
 public class EntityBinder {
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, EntityBinder.class.getName());
     private static final String NATURAL_ID_CACHE_SUFFIX = "##NaturalId";
 	
 	private String name;
 	private XClass annotatedClass;
 	private PersistentClass persistentClass;
 	private Mappings mappings;
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
 	private java.util.Map<String, Join> secondaryTables = new HashMap<String, Join>();
 	private java.util.Map<String, Object> secondaryTableJoins = new HashMap<String, Object>();
 	private String cacheConcurrentStrategy;
 	private String cacheRegion;
 	private String naturalIdCacheRegion;
 	private java.util.Map<String, String> filters = new HashMap<String, String>();
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
 			Mappings mappings) {
 		this.mappings = mappings;
 		this.persistentClass = persistentClass;
 		this.annotatedClass = annotatedClass;
 		bindEjb3Annotation( ejb3Ann );
 		bindHibernateAnnotation( hibAnn );
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
 		persistentClass.setNodeName( name );
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
 					? mappings.forceDiscriminatorInSelectsByDefault()
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
 		persistentClass.setOptimisticLockMode( getVersioning( optimisticLockType ) );
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
 					persister = ReflectHelper.classForName( entityAnn.persister() );
 				}
 				catch (ClassNotFoundException cnfe) {
 					throw new AnnotationException( "Could not find persister class: " + persister );
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
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlInsert.check().toString().toLowerCase() )
 			);
 
 		}
 		if ( sqlUpdate != null ) {
 			persistentClass.setCustomSQLUpdate( sqlUpdate.sql(), sqlUpdate.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlUpdate.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDelete != null ) {
 			persistentClass.setCustomSQLDelete( sqlDelete.sql(), sqlDelete.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDelete.check().toString().toLowerCase() )
 			);
 		}
 		if ( sqlDeleteAll != null ) {
 			persistentClass.setCustomSQLDelete( sqlDeleteAll.sql(), sqlDeleteAll.callable(),
 					ExecuteUpdateResultCheckStyle.fromExternalName( sqlDeleteAll.check().toString().toLowerCase() )
 			);
 		}
 		if ( loader != null ) {
 			persistentClass.setLoaderName( loader.namedQuery() );
 		}
 
 		if ( annotatedClass.isAnnotationPresent( Synchronize.class )) {
 			Synchronize synchronizedWith = annotatedClass.getAnnotation(Synchronize.class);
 
 			String [] tables = synchronizedWith.value();
 			for (String table : tables) {
 				persistentClass.addSynchronizedTable(table);
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
 
 		if ( !inheritanceState.hasParents() ) {
 			for ( Map.Entry<String, String> filter : filters.entrySet() ) {
 				String filterName = filter.getKey();
 				String cond = filter.getValue();
 				if ( BinderHelper.isEmptyAnnotationValue( cond ) ) {
 					FilterDefinition definition = mappings.getFilterDefinition( filterName );
 					cond = definition == null ? null : definition.getDefaultFilterCondition();
 					if ( StringHelper.isEmpty( cond ) ) {
 						throw new AnnotationException(
 								"no filter condition found for filter " + filterName + " in " + this.name
 						);
 					}
 				}
-				persistentClass.addFilter( filterName, cond );
+				persistentClass.addFilter( filterName, null, cond );
 			}
 		}
 		else if ( filters.size() > 0 ) {
 			LOG.filterAnnotationOnSubclass( persistentClass.getEntityName() );
 		}
 		LOG.debugf( "Import with entity name %s", name );
 		try {
 			mappings.addImport( persistentClass.getEntityName(), name );
 			String entityName = persistentClass.getEntityName();
 			if ( !entityName.equals( name ) ) {
 				mappings.addImport( entityName, entityName );
 			}
 		}
 		catch (MappingException me) {
 			throw new AnnotationException( "Use of the same entity name twice: " + name, me );
 		}
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
 
 	int getVersioning(OptimisticLockType type) {
 		switch ( type ) {
 			case VERSION:
 				return Versioning.OPTIMISTIC_LOCK_VERSION;
 			case NONE:
 				return Versioning.OPTIMISTIC_LOCK_NONE;
 			case DIRTY:
 				return Versioning.OPTIMISTIC_LOCK_DIRTY;
 			case ALL:
 				return Versioning.OPTIMISTIC_LOCK_ALL;
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
 				if ( AnnotationBinder.isDefault(
 						mappings.getReflectionManager().toXClass( proxy.proxyClass() ), mappings
 				) ) {
 					proxyClass = annotatedClass;
 				}
 				else {
 					proxyClass = mappings.getReflectionManager().toXClass( proxy.proxyClass() );
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
 
 	private static class EntityTableNamingStrategyHelper implements ObjectNameNormalizer.NamingStrategyHelper {
 		private final String entityName;
 
 		private EntityTableNamingStrategyHelper(String entityName) {
 			this.entityName = entityName;
 		}
 
 		public String determineImplicitName(NamingStrategy strategy) {
 			return strategy.classToTableName( entityName );
 		}
 
 		public String handleExplicitName(NamingStrategy strategy, String name) {
 			return strategy.tableName( name );
 		}
 	}
 
 	public void bindTable(
 			String schema,
 			String catalog,
 			String tableName,
 			List<UniqueConstraintHolder> uniqueConstraints,
 			String constraints,
 			Table denormalizedSuperclassTable) {
 		EntityTableObjectNameSource tableNameContext = new EntityTableObjectNameSource( tableName, name );
 		EntityTableNamingStrategyHelper namingStrategyHelper = new EntityTableNamingStrategyHelper( name );
 		final Table table = TableBinder.buildAndFillTable(
 				schema,
 				catalog,
 				tableNameContext,
 				namingStrategyHelper,
 				persistentClass.isAbstract(),
 				uniqueConstraints,
 				constraints,
 				denormalizedSuperclassTable,
 				mappings,
 				this.subselect
 		);
 		final RowId rowId = annotatedClass.getAnnotation( RowId.class );
 		if ( rowId != null ) {
 			table.setRowId( rowId.value() );
 		}
 
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
 		 * Those operations has to be done after the id definition of the persistence class.
 		 * ie after the properties parsing
 		 */
 		Iterator joins = secondaryTables.values().iterator();
 		Iterator joinColumns = secondaryTableJoins.values().iterator();
 
 		while ( joins.hasNext() ) {
 			Object uncastedColumn = joinColumns.next();
 			Join join = (Join) joins.next();
 			createPrimaryColumnsToSecondaryTable( uncastedColumn, propertyHolder, join );
 		}
 		mappings.addJoins( persistentClass, secondaryTables );
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
 					propertyHolder, mappings
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
 						propertyHolder, mappings
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
 								propertyHolder, mappings
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
 								propertyHolder, mappings
 						);
 					}
 				}
 			}
 		}
 
 		for (Ejb3JoinColumn joinColumn : ejb3JoinColumns) {
 			joinColumn.forceNotNull();
 		}
 		bindJoinToPersistentClass( join, ejb3JoinColumns, mappings );
 	}
 
 	private void bindJoinToPersistentClass(Join join, Ejb3JoinColumn[] ejb3JoinColumns, Mappings mappings) {
 		SimpleValue key = new DependantValue( mappings, join.getTable(), persistentClass.getIdentifier() );
 		join.setKey( key );
 		setFKNameIfDefined( join );
 		key.setCascadeDeleteEnabled( false );
 		TableBinder.bindFk( persistentClass, null, ejb3JoinColumns, key, false, mappings );
 		join.createPrimaryKey();
 		join.createForeignKey();
 		persistentClass.addJoin( join );
 	}
 
 	private void setFKNameIfDefined(Join join) {
 		org.hibernate.annotations.Table matchingTable = findMatchingComplimentTableAnnotation( join );
 		if ( matchingTable != null && !BinderHelper.isEmptyAnnotationValue( matchingTable.foreignKey().name() ) ) {
 			( (SimpleValue) join.getKey() ).setForeignKeyName( matchingTable.foreignKey().name() );
 		}
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
 
 	private static class SecondaryTableNamingStrategyHelper implements ObjectNameNormalizer.NamingStrategyHelper {
 		public String determineImplicitName(NamingStrategy strategy) {
 			// todo : throw an error?
 			return null;
 		}
 
 		public String handleExplicitName(NamingStrategy strategy, String name) {
 			return strategy.tableName( name );
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
 
 		if ( secondaryTable != null ) {
 			schema = secondaryTable.schema();
 			catalog = secondaryTable.catalog();
 			secondaryTableNameContext = new SecondaryTableNameSource( secondaryTable.name() );
 			joinColumns = secondaryTable.pkJoinColumns();
 			uniqueConstraintHolders = TableBinder.buildUniqueConstraintHolders( secondaryTable.uniqueConstraints() );
 		}
 		else if ( joinTable != null ) {
 			schema = joinTable.schema();
 			catalog = joinTable.catalog();
 			secondaryTableNameContext = new SecondaryTableNameSource( joinTable.name() );
 			joinColumns = joinTable.joinColumns();
 			uniqueConstraintHolders = TableBinder.buildUniqueConstraintHolders( joinTable.uniqueConstraints() );
 		}
 		else {
 			throw new AssertionFailure( "Both JoinTable and SecondaryTable are null" );
 		}
 
 		final Table table = TableBinder.buildAndFillTable(
 				schema,
 				catalog,
 				secondaryTableNameContext,
 				SEC_TBL_NS_HELPER,
 				false,
 				uniqueConstraintHolders,
 				null,
 				null,
 				mappings,
 				null
 		);
 
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
 								matchingTable.sqlInsert().check().toString().toLowerCase()
 						)
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlUpdate().sql() ) ) {
 				join.setCustomSQLUpdate( matchingTable.sqlUpdate().sql().trim(),
 						matchingTable.sqlUpdate().callable(),
 						ExecuteUpdateResultCheckStyle.fromExternalName(
 								matchingTable.sqlUpdate().check().toString().toLowerCase()
 						)
 				);
 			}
 			if ( !BinderHelper.isEmptyAnnotationValue( matchingTable.sqlDelete().sql() ) ) {
 				join.setCustomSQLDelete( matchingTable.sqlDelete().sql().trim(),
 						matchingTable.sqlDelete().callable(),
 						ExecuteUpdateResultCheckStyle.fromExternalName(
 								matchingTable.sqlDelete().check().toString().toLowerCase()
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
 
 	public void addFilter(String name, String condition) {
 		filters.put( name, condition );
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
 		TableBinder.addIndexes( hibTable, table.indexes(), mappings );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
index 91880f75d1..64b811b84c 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Collection.java
@@ -1,674 +1,660 @@
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
 import java.util.ArrayList;
 import java.util.Comparator;
-import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Properties;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
-import org.hibernate.internal.FilterConfiguration;
 import org.hibernate.internal.QualifiedTableNameFilterConfiguration;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
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
 
 	private final Mappings mappings;
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
 	private final java.util.Set synchronizedTables = new HashSet();
 
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
 
 	protected Collection(Mappings mappings, PersistentClass owner) {
 		this.mappings = mappings;
 		this.owner = owner;
 	}
 
 	public Mappings getMappings() {
 		return mappings;
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
 				setComparator( (Comparator) ReflectHelper.classForName( comparatorClassName ).newInstance() );
 			}
 			catch ( Exception e ) {
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
 		this.role = role==null ? null : role.intern();
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
 	 * @deprecated Inject the owner into constructor.
 	 *
 	 * @param owner The owner
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
 		if ( getKey().isCascadeDeleteEnabled() && ( !isInverse() || !isOneToMany() ) ) {
 			throw new MappingException(
 				"only inverse one-to-many associations may use on-delete=\"cascade\": " 
 				+ getRole() );
 		}
 		if ( !getKey().isValid( mapping ) ) {
 			throw new MappingException(
 				"collection foreign key mapping has wrong number of columns: "
 				+ getRole()
 				+ " type: "
 				+ getKey().getType().getName() );
 		}
 		if ( !getElement().isValid( mapping ) ) {
 			throw new MappingException( 
 				"collection element mapping has wrong number of columns: "
 				+ getRole()
 				+ " type: "
 				+ getElement().getType().getName() );
 		}
 
 		checkColumnDuplication();
 		
 		if ( elementNodeName!=null && elementNodeName.startsWith("@") ) {
 			throw new MappingException("element node must not be an attribute: " + elementNodeName );
 		}
 		if ( elementNodeName!=null && elementNodeName.equals(".") ) {
 			throw new MappingException("element node must not be the parent: " + elementNodeName );
 		}
 		if ( nodeName!=null && nodeName.indexOf('@')>-1 ) {
 			throw new MappingException("collection node must not be an attribute: " + elementNodeName );
 		}
 	}
 
 	private void checkColumnDuplication(java.util.Set distinctColumns, Iterator columns)
 			throws MappingException {
 		while ( columns.hasNext() ) {
 			Selectable s = (Selectable) columns.next();
 			if ( !s.isFormula() ) {
 				Column col = (Column) s;
 				if ( !distinctColumns.add( col.getName() ) ) {
 					throw new MappingException( "Repeated column in mapping for collection: "
 						+ getRole()
 						+ " column: "
 						+ col.getName() );
 				}
 			}
 		}
 	}
 
 	private void checkColumnDuplication() throws MappingException {
 		HashSet cols = new HashSet();
 		checkColumnDuplication( cols, getKey().getColumnIterator() );
 		if ( isIndexed() ) {
 			checkColumnDuplication( cols, ( (IndexedCollection) this )
 				.getIndex()
 				.getColumnIterator() );
 		}
 		if ( isIdentified() ) {
 			checkColumnDuplication( cols, ( (IdentifierCollection) this )
 				.getIdentifier()
 				.getColumnIterator() );
 		}
 		if ( !isOneToMany() ) {
 			checkColumnDuplication( cols, getElement().getColumnIterator() );
 		}
 	}
 
 	public Iterator getColumnIterator() {
 		return EmptyIterator.INSTANCE;
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
 			return mappings.getTypeResolver()
 					.getTypeFactory()
 					.customCollection( typeName, typeParameters, role, referencedPropertyName, isEmbedded() );
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
 		if ( !isInverse() ) createPrimaryKey();
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
 
 	public void setCustomSQLDeleteAll(String customSQLDeleteAll, boolean callable, ExecuteUpdateResultCheckStyle checkStyle) {
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
 
-	public void addFilter(String name, String condition) {
-		filters.add(toFilterConfiguration(name, condition));
+	public void addFilter(String name, String tableName, String condition) {
+		filters.add(new QualifiedTableNameFilterConfiguration(name, tableName, condition));
 	}
 
 	public java.util.List getFilters() {
 		return filters;
 	}
 
-	public void addManyToManyFilter(String name, String condition) {
-		manyToManyFilters.add(toFilterConfiguration(name, condition));
+	public void addManyToManyFilter(String name, String tableName, String condition) {
+		manyToManyFilters.add(new QualifiedTableNameFilterConfiguration(name, tableName, condition));
 	}
-	
-	private static FilterConfiguration toFilterConfiguration(String name, String condition){
-		String tableName = null;
-		String actualCondition = condition;
-		int pos = condition.lastIndexOf('.');
-		if (pos > -1){
-			tableName = condition.substring(0, pos);
-			actualCondition = condition.substring(pos+1);
-		}
-		return new QualifiedTableNameFilterConfiguration(name, tableName, actualCondition);
-	}
-
 
 	public java.util.List getManyToManyFilters() {
 		return manyToManyFilters;
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + getRole() + ')';
 	}
 
 	public java.util.Set getSynchronizedTables() {
 		return synchronizedTables;
 	}
 
 	public String getLoaderName() {
 		return loaderName;
 	}
 
 	public void setLoaderName(String name) {
 		this.loaderName = name==null ? null : name.intern();
 	}
 
 	public String getReferencedPropertyName() {
 		return referencedPropertyName;
 	}
 
 	public void setReferencedPropertyName(String propertyRef) {
 		this.referencedPropertyName = propertyRef==null ? null : propertyRef.intern();
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
 
 	public boolean isEmbedded() {
 		return embedded;
 	}
 
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
 	public boolean isSubselectLoadable() {
 		return subselectLoadable;
 	}
 	
 
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
 		return orderBy!=null || manyToManyOrderBy!=null;
 	}
 
 	public void setComparatorClassName(String comparatorClassName) {
 		this.comparatorClassName = comparatorClassName;		
 	}
 	
 	public String getComparatorClassName() {
 		return comparatorClassName;
 	}
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/Filterable.java b/hibernate-core/src/main/java/org/hibernate/mapping/Filterable.java
index 5552937414..25783bace9 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/Filterable.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/Filterable.java
@@ -1,36 +1,36 @@
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
 
 
 /**
  * Defines mapping elements to which filters may be applied.
  *
  * @author Steve Ebersole
  */
 public interface Filterable {
-	public void addFilter(String name, String condition);
+	public void addFilter(String name, String tableName, String condition);
 
 	public java.util.List getFilters();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
index a92ba5fbd9..c09de99f59 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/PersistentClass.java
@@ -1,870 +1,869 @@
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
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Iterator;
 import java.util.Set;
 import java.util.StringTokenizer;
 
 import org.hibernate.EntityMode;
 import org.hibernate.MappingException;
 import org.hibernate.dialect.Dialect;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.engine.spi.Mapping;
-import org.hibernate.internal.FilterConfiguration;
 import org.hibernate.internal.PersistentClassFilterConfiguration;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.internal.util.collections.EmptyIterator;
 import org.hibernate.internal.util.collections.JoinedIterator;
 import org.hibernate.internal.util.collections.SingletonIterator;
 import org.hibernate.sql.Alias;
 
 /**
  * Mapping for an entity.
  *
  * @author Gavin King
  */
 public abstract class PersistentClass implements Serializable, Filterable, MetaAttributable {
 
 	private static final Alias PK_ALIAS = new Alias(15, "PK");
 
 	public static final String NULL_DISCRIMINATOR_MAPPING = "null";
 	public static final String NOT_NULL_DISCRIMINATOR_MAPPING = "not null";
 
 	private String entityName;
 
 	private String className;
 	private String proxyInterfaceName;
 	
 	private String nodeName;
 	private String jpaEntityName;
 
 	private String discriminatorValue;
 	private boolean lazy;
 	private ArrayList properties = new ArrayList();
 	private ArrayList declaredProperties = new ArrayList();
 	private final ArrayList subclasses = new ArrayList();
 	private final ArrayList subclassProperties = new ArrayList();
 	private final ArrayList subclassTables = new ArrayList();
 	private boolean dynamicInsert;
 	private boolean dynamicUpdate;
 	private int batchSize=-1;
 	private boolean selectBeforeUpdate;
 	private java.util.Map metaAttributes;
 	private ArrayList joins = new ArrayList();
 	private final ArrayList subclassJoins = new ArrayList();
 	private final java.util.List filters = new ArrayList();
 	protected final java.util.Set synchronizedTables = new HashSet();
 	private String loaderName;
 	private Boolean isAbstract;
 	private boolean hasSubselectLoadableCollections;
 	private Component identifierMapper;
 
 	// Custom SQL
 	private String customSQLInsert;
 	private boolean customInsertCallable;
 	private ExecuteUpdateResultCheckStyle insertCheckStyle;
 	private String customSQLUpdate;
 	private boolean customUpdateCallable;
 	private ExecuteUpdateResultCheckStyle updateCheckStyle;
 	private String customSQLDelete;
 	private boolean customDeleteCallable;
 	private ExecuteUpdateResultCheckStyle deleteCheckStyle;
 
 	private String temporaryIdTableName;
 	private String temporaryIdTableDDL;
 
 	private java.util.Map tuplizerImpls;
 
 	protected int optimisticLockMode;
 	private MappedSuperclass superMappedSuperclass;
 	private Component declaredIdentifierMapper;
 
 	public String getClassName() {
 		return className;
 	}
 
 	public void setClassName(String className) {
 		this.className = className==null ? null : className.intern();
 	}
 
 	public String getProxyInterfaceName() {
 		return proxyInterfaceName;
 	}
 
 	public void setProxyInterfaceName(String proxyInterfaceName) {
 		this.proxyInterfaceName = proxyInterfaceName;
 	}
 
 	public Class getMappedClass() throws MappingException {
 		if (className==null) return null;
 		try {
 			return ReflectHelper.classForName(className);
 		}
 		catch (ClassNotFoundException cnfe) {
 			throw new MappingException("entity class not found: " + className, cnfe);
 		}
 	}
 
 	public Class getProxyInterface() {
 		if (proxyInterfaceName==null) return null;
 		try {
 			return ReflectHelper.classForName( proxyInterfaceName );
 		}
 		catch (ClassNotFoundException cnfe) {
 			throw new MappingException("proxy class not found: " + proxyInterfaceName, cnfe);
 		}
 	}
 	public boolean useDynamicInsert() {
 		return dynamicInsert;
 	}
 
 	abstract int nextSubclassId();
 	public abstract int getSubclassId();
 	
 	public boolean useDynamicUpdate() {
 		return dynamicUpdate;
 	}
 
 	public void setDynamicInsert(boolean dynamicInsert) {
 		this.dynamicInsert = dynamicInsert;
 	}
 
 	public void setDynamicUpdate(boolean dynamicUpdate) {
 		this.dynamicUpdate = dynamicUpdate;
 	}
 
 
 	public String getDiscriminatorValue() {
 		return discriminatorValue;
 	}
 
 	public void addSubclass(Subclass subclass) throws MappingException {
 		// inheritance cycle detection (paranoid check)
 		PersistentClass superclass = getSuperclass();
 		while (superclass!=null) {
 			if( subclass.getEntityName().equals( superclass.getEntityName() ) ) {
 				throw new MappingException(
 					"Circular inheritance mapping detected: " +
 					subclass.getEntityName() +
 					" will have it self as superclass when extending " +
 					getEntityName()
 				);
 			}
 			superclass = superclass.getSuperclass();
 		}
 		subclasses.add(subclass);
 	}
 
 	public boolean hasSubclasses() {
 		return subclasses.size() > 0;
 	}
 
 	public int getSubclassSpan() {
 		int n = subclasses.size();
 		Iterator iter = subclasses.iterator();
 		while ( iter.hasNext() ) {
 			n += ( (Subclass) iter.next() ).getSubclassSpan();
 		}
 		return n;
 	}
 	/**
 	 * Iterate over subclasses in a special 'order', most derived subclasses
 	 * first.
 	 */
 	public Iterator getSubclassIterator() {
 		Iterator[] iters = new Iterator[ subclasses.size() + 1 ];
 		Iterator iter = subclasses.iterator();
 		int i=0;
 		while ( iter.hasNext() ) {
 			iters[i++] = ( (Subclass) iter.next() ).getSubclassIterator();
 		}
 		iters[i] = subclasses.iterator();
 		return new JoinedIterator(iters);
 	}
 
 	public Iterator getSubclassClosureIterator() {
 		ArrayList iters = new ArrayList();
 		iters.add( new SingletonIterator(this) );
 		Iterator iter = getSubclassIterator();
 		while ( iter.hasNext() ) {
 			PersistentClass clazz = (PersistentClass)  iter.next();
 			iters.add( clazz.getSubclassClosureIterator() );
 		}
 		return new JoinedIterator(iters);
 	}
 	
 	public Table getIdentityTable() {
 		return getRootTable();
 	}
 	
 	public Iterator getDirectSubclasses() {
 		return subclasses.iterator();
 	}
 
 	public void addProperty(Property p) {
 		properties.add(p);
 		declaredProperties.add(p);
 		p.setPersistentClass(this);
 	}
 
 	public abstract Table getTable();
 
 	public String getEntityName() {
 		return entityName;
 	}
 
 	public abstract boolean isMutable();
 	public abstract boolean hasIdentifierProperty();
 	public abstract Property getIdentifierProperty();
 	public abstract Property getDeclaredIdentifierProperty();
 	public abstract KeyValue getIdentifier();
 	public abstract Property getVersion();
 	public abstract Property getDeclaredVersion();
 	public abstract Value getDiscriminator();
 	public abstract boolean isInherited();
 	public abstract boolean isPolymorphic();
 	public abstract boolean isVersioned();
 	public abstract String getNaturalIdCacheRegionName();
 	public abstract String getCacheConcurrencyStrategy();
 	public abstract PersistentClass getSuperclass();
 	public abstract boolean isExplicitPolymorphism();
 	public abstract boolean isDiscriminatorInsertable();
 
 	public abstract Iterator getPropertyClosureIterator();
 	public abstract Iterator getTableClosureIterator();
 	public abstract Iterator getKeyClosureIterator();
 
 	protected void addSubclassProperty(Property prop) {
 		subclassProperties.add(prop);
 	}
 	protected void addSubclassJoin(Join join) {
 		subclassJoins.add(join);
 	}
 	protected void addSubclassTable(Table subclassTable) {
 		subclassTables.add(subclassTable);
 	}
 	public Iterator getSubclassPropertyClosureIterator() {
 		ArrayList iters = new ArrayList();
 		iters.add( getPropertyClosureIterator() );
 		iters.add( subclassProperties.iterator() );
 		for ( int i=0; i<subclassJoins.size(); i++ ) {
 			Join join = (Join) subclassJoins.get(i);
 			iters.add( join.getPropertyIterator() );
 		}
 		return new JoinedIterator(iters);
 	}
 	public Iterator getSubclassJoinClosureIterator() {
 		return new JoinedIterator( getJoinClosureIterator(), subclassJoins.iterator() );
 	}
 	public Iterator getSubclassTableClosureIterator() {
 		return new JoinedIterator( getTableClosureIterator(), subclassTables.iterator() );
 	}
 
 	public boolean isClassOrSuperclassJoin(Join join) {
 		return joins.contains(join);
 	}
 
 	public boolean isClassOrSuperclassTable(Table closureTable) {
 		return getTable()==closureTable;
 	}
 
 	public boolean isLazy() {
 		return lazy;
 	}
 
 	public void setLazy(boolean lazy) {
 		this.lazy = lazy;
 	}
 
 	public abstract boolean hasEmbeddedIdentifier();
 	public abstract Class getEntityPersisterClass();
 	public abstract void setEntityPersisterClass(Class classPersisterClass);
 	public abstract Table getRootTable();
 	public abstract RootClass getRootClass();
 	public abstract KeyValue getKey();
 
 	public void setDiscriminatorValue(String discriminatorValue) {
 		this.discriminatorValue = discriminatorValue;
 	}
 
 	public void setEntityName(String entityName) {
 		this.entityName = entityName==null ? null : entityName.intern();
 	}
 
 	public void createPrimaryKey() {
 		//Primary key constraint
 		PrimaryKey pk = new PrimaryKey();
 		Table table = getTable();
 		pk.setTable(table);
 		pk.setName( PK_ALIAS.toAliasString( table.getName() ) );
 		table.setPrimaryKey(pk);
 
 		pk.addColumns( getKey().getColumnIterator() );
 	}
 
 	public abstract String getWhere();
 
 	public int getBatchSize() {
 		return batchSize;
 	}
 
 	public void setBatchSize(int batchSize) {
 		this.batchSize = batchSize;
 	}
 
 	public boolean hasSelectBeforeUpdate() {
 		return selectBeforeUpdate;
 	}
 
 	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
 		this.selectBeforeUpdate = selectBeforeUpdate;
 	}
 
 	/**
 	 * Build an iterator of properties which are "referenceable".
 	 *
 	 * @see #getReferencedProperty for a discussion of "referenceable"
 	 * @return The property iterator.
 	 */
 	public Iterator getReferenceablePropertyIterator() {
 		return getPropertyClosureIterator();
 	}
 
 	/**
 	 * Given a property path, locate the appropriate referenceable property reference.
 	 * <p/>
 	 * A referenceable property is a property  which can be a target of a foreign-key
 	 * mapping (an identifier or explcitly named in a property-ref).
 	 *
 	 * @param propertyPath The property path to resolve into a property reference.
 	 * @return The property reference (never null).
 	 * @throws MappingException If the property could not be found.
 	 */
 	public Property getReferencedProperty(String propertyPath) throws MappingException {
 		try {
 			return getRecursiveProperty( propertyPath, getReferenceablePropertyIterator() );
 		}
 		catch ( MappingException e ) {
 			throw new MappingException(
 					"property-ref [" + propertyPath + "] not found on entity [" + getEntityName() + "]", e
 			);
 		}
 	}
 
 	public Property getRecursiveProperty(String propertyPath) throws MappingException {
 		try {
 			return getRecursiveProperty( propertyPath, getPropertyIterator() );
 		}
 		catch ( MappingException e ) {
 			throw new MappingException(
 					"property [" + propertyPath + "] not found on entity [" + getEntityName() + "]", e
 			);
 		}
 	}
 
 	private Property getRecursiveProperty(String propertyPath, Iterator iter) throws MappingException {
 		Property property = null;
 		StringTokenizer st = new StringTokenizer( propertyPath, ".", false );
 		try {
 			while ( st.hasMoreElements() ) {
 				final String element = ( String ) st.nextElement();
 				if ( property == null ) {
 					Property identifierProperty = getIdentifierProperty();
 					if ( identifierProperty != null && identifierProperty.getName().equals( element ) ) {
 						// we have a mapped identifier property and the root of
 						// the incoming property path matched that identifier
 						// property
 						property = identifierProperty;
 					}
 					else if ( identifierProperty == null && getIdentifierMapper() != null ) {
 						// we have an embedded composite identifier
 						try {
 							identifierProperty = getProperty( element, getIdentifierMapper().getPropertyIterator() );
 							if ( identifierProperty != null ) {
 								// the root of the incoming property path matched one
 								// of the embedded composite identifier properties
 								property = identifierProperty;
 							}
 						}
 						catch( MappingException ignore ) {
 							// ignore it...
 						}
 					}
 
 					if ( property == null ) {
 						property = getProperty( element, iter );
 					}
 				}
 				else {
 					//flat recursive algorithm
 					property = ( ( Component ) property.getValue() ).getProperty( element );
 				}
 			}
 		}
 		catch ( MappingException e ) {
 			throw new MappingException( "property [" + propertyPath + "] not found on entity [" + getEntityName() + "]" );
 		}
 
 		return property;
 	}
 
 	private Property getProperty(String propertyName, Iterator iterator) throws MappingException {
 		while ( iterator.hasNext() ) {
 			Property prop = (Property) iterator.next();
 			if ( prop.getName().equals( StringHelper.root(propertyName) ) ) {
 				return prop;
 			}
 		}
 		throw new MappingException( "property [" + propertyName + "] not found on entity [" + getEntityName() + "]" );
 	}
 
 	public Property getProperty(String propertyName) throws MappingException {
 		Iterator iter = getPropertyClosureIterator();
 		Property identifierProperty = getIdentifierProperty();
 		if ( identifierProperty != null
 				&& identifierProperty.getName().equals( StringHelper.root(propertyName) )
 				) {
 			return identifierProperty;
 		}
 		else {
 			return getProperty( propertyName, iter );
 		}
 	}
 
 	abstract public int getOptimisticLockMode();
 
 	public void setOptimisticLockMode(int optimisticLockMode) {
 		this.optimisticLockMode = optimisticLockMode;
 	}
 
 	public void validate(Mapping mapping) throws MappingException {
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			if ( !prop.isValid(mapping) ) {
 				throw new MappingException(
 						"property mapping has wrong number of columns: " +
 						StringHelper.qualify( getEntityName(), prop.getName() ) +
 						" type: " +
 						prop.getType().getName()
 					);
 			}
 		}
 		checkPropertyDuplication();
 		checkColumnDuplication();
 	}
 	
 	private void checkPropertyDuplication() throws MappingException {
 		HashSet names = new HashSet();
 		Iterator iter = getPropertyIterator();
 		while ( iter.hasNext() ) {
 			Property prop = (Property) iter.next();
 			if ( !names.add( prop.getName() ) ) {
 				throw new MappingException( "Duplicate property mapping of " + prop.getName() + " found in " + getEntityName());
 			}
 		}
 	}
 
 	public boolean isDiscriminatorValueNotNull() {
 		return NOT_NULL_DISCRIMINATOR_MAPPING.equals( getDiscriminatorValue() );
 	}
 	public boolean isDiscriminatorValueNull() {
 		return NULL_DISCRIMINATOR_MAPPING.equals( getDiscriminatorValue() );
 	}
 
 	public java.util.Map getMetaAttributes() {
 		return metaAttributes;
 	}
 
 	public void setMetaAttributes(java.util.Map metas) {
 		this.metaAttributes = metas;
 	}
 
 	public MetaAttribute getMetaAttribute(String name) {
 		return metaAttributes==null?null:(MetaAttribute) metaAttributes.get(name);
 	}
 
 	@Override
     public String toString() {
 		return getClass().getName() + '(' + getEntityName() + ')';
 	}
 	
 	public Iterator getJoinIterator() {
 		return joins.iterator();
 	}
 
 	public Iterator getJoinClosureIterator() {
 		return joins.iterator();
 	}
 
 	public void addJoin(Join join) {
 		joins.add(join);
 		join.setPersistentClass(this);
 	}
 
 	public int getJoinClosureSpan() {
 		return joins.size();
 	}
 
 	public int getPropertyClosureSpan() {
 		int span = properties.size();
 		for ( int i=0; i<joins.size(); i++ ) {
 			Join join = (Join) joins.get(i);
 			span += join.getPropertySpan();
 		}
 		return span;
 	}
 
 	public int getJoinNumber(Property prop) {
 		int result=1;
 		Iterator iter = getSubclassJoinClosureIterator();
 		while ( iter.hasNext() ) {
 			Join join = (Join) iter.next();
 			if ( join.containsProperty(prop) ) return result;
 			result++;
 		}
 		return 0;
 	}
 
 	/**
 	 * Build an iterator over the properties defined on this class.  The returned
 	 * iterator only accounts for "normal" properties (i.e. non-identifier
 	 * properties).
 	 * <p/>
 	 * Differs from {@link #getUnjoinedPropertyIterator} in that the iterator
 	 * we return here will include properties defined as part of a join.
 	 *
 	 * @return An iterator over the "normal" properties.
 	 */
 	public Iterator getPropertyIterator() {
 		ArrayList iterators = new ArrayList();
 		iterators.add( properties.iterator() );
 		for ( int i = 0; i < joins.size(); i++ ) {
 			Join join = ( Join ) joins.get( i );
 			iterators.add( join.getPropertyIterator() );
 		}
 		return new JoinedIterator( iterators );
 	}
 
 	/**
 	 * Build an iterator over the properties defined on this class <b>which
 	 * are not defined as part of a join</b>.  As with {@link #getPropertyIterator},
 	 * the returned iterator only accounts for non-identifier properties.
 	 *
 	 * @return An iterator over the non-joined "normal" properties.
 	 */
 	public Iterator getUnjoinedPropertyIterator() {
 		return properties.iterator();
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
 
-	public void addFilter(String name, String condition) {
+	public void addFilter(String name, String tableName, String condition) {
 		filters.add(new PersistentClassFilterConfiguration(name, this, condition));
 	}
 
 	public java.util.List getFilters() {
 		return filters;
 	}
 
 	public boolean isForceDiscriminator() {
 		return false;
 	}
 
 	public abstract boolean isJoinedSubclass();
 
 	public String getLoaderName() {
 		return loaderName;
 	}
 
 	public void setLoaderName(String loaderName) {
 		this.loaderName = loaderName==null ? null : loaderName.intern();
 	}
 
 	public abstract java.util.Set getSynchronizedTables();
 	
 	public void addSynchronizedTable(String table) {
 		synchronizedTables.add(table);
 	}
 
 	public Boolean isAbstract() {
 		return isAbstract;
 	}
 
 	public void setAbstract(Boolean isAbstract) {
 		this.isAbstract = isAbstract;
 	}
 
 	protected void checkColumnDuplication(Set distinctColumns, Iterator columns) 
 	throws MappingException {
 		while ( columns.hasNext() ) {
 			Selectable columnOrFormula = (Selectable) columns.next();
 			if ( !columnOrFormula.isFormula() ) {
 				Column col = (Column) columnOrFormula;
 				if ( !distinctColumns.add( col.getName() ) ) {
 					throw new MappingException( 
 							"Repeated column in mapping for entity: " +
 							getEntityName() +
 							" column: " +
 							col.getName() + 
 							" (should be mapped with insert=\"false\" update=\"false\")"
 						);
 				}
 			}
 		}
 	}
 	
 	protected void checkPropertyColumnDuplication(Set distinctColumns, Iterator properties) 
 	throws MappingException {
 		while ( properties.hasNext() ) {
 			Property prop = (Property) properties.next();
 			if ( prop.getValue() instanceof Component ) { //TODO: remove use of instanceof!
 				Component component = (Component) prop.getValue();
 				checkPropertyColumnDuplication( distinctColumns, component.getPropertyIterator() );
 			}
 			else {
 				if ( prop.isUpdateable() || prop.isInsertable() ) {
 					checkColumnDuplication( distinctColumns, prop.getColumnIterator() );
 				}
 			}
 		}
 	}
 	
 	protected Iterator getNonDuplicatedPropertyIterator() {
 		return getUnjoinedPropertyIterator();
 	}
 	
 	protected Iterator getDiscriminatorColumnIterator() {
 		return EmptyIterator.INSTANCE;
 	}
 	
 	protected void checkColumnDuplication() {
 		HashSet cols = new HashSet();
 		if (getIdentifierMapper() == null ) {
 			//an identifier mapper => getKey will be included in the getNonDuplicatedPropertyIterator()
 			//and checked later, so it needs to be excluded
 			checkColumnDuplication( cols, getKey().getColumnIterator() );
 		}
 		checkColumnDuplication( cols, getDiscriminatorColumnIterator() );
 		checkPropertyColumnDuplication( cols, getNonDuplicatedPropertyIterator() );
 		Iterator iter = getJoinIterator();
 		while ( iter.hasNext() ) {
 			cols.clear();
 			Join join = (Join) iter.next();
 			checkColumnDuplication( cols, join.getKey().getColumnIterator() );
 			checkPropertyColumnDuplication( cols, join.getPropertyIterator() );
 		}
 	}
 	
 	public abstract Object accept(PersistentClassVisitor mv);
 	
 	public String getNodeName() {
 		return nodeName;
 	}
 	
 	public void setNodeName(String nodeName) {
 		this.nodeName = nodeName;
 	}
 
 	public String getJpaEntityName() {
 		return jpaEntityName;
 	}
 	
 	public void setJpaEntityName(String jpaEntityName) {
 		this.jpaEntityName = jpaEntityName;
 	}
 	
 	public boolean hasPojoRepresentation() {
 		return getClassName()!=null;
 	}
 
 	public boolean hasDom4jRepresentation() {
 		return getNodeName()!=null;
 	}
 
 	public boolean hasSubselectLoadableCollections() {
 		return hasSubselectLoadableCollections;
 	}
 	
 	public void setSubselectLoadableCollections(boolean hasSubselectCollections) {
 		this.hasSubselectLoadableCollections = hasSubselectCollections;
 	}
 
 	public void prepareTemporaryTables(Mapping mapping, Dialect dialect) {
 		if ( dialect.supportsTemporaryTables() ) {
 			temporaryIdTableName = dialect.generateTemporaryTableName( getTable().getName() );
 			Table table = new Table();
 			table.setName( temporaryIdTableName );
 			Iterator itr = getTable().getPrimaryKey().getColumnIterator();
 			while( itr.hasNext() ) {
 				Column column = (Column) itr.next();
 				table.addColumn( (Column) column.clone()  );
 			}
 			temporaryIdTableDDL = table.sqlTemporaryTableCreateString( dialect, mapping );
 		}
 	}
 
 	public String getTemporaryIdTableName() {
 		return temporaryIdTableName;
 	}
 
 	public String getTemporaryIdTableDDL() {
 		return temporaryIdTableDDL;
 	}
 
 	public Component getIdentifierMapper() {
 		return identifierMapper;
 	}
 
 	public Component getDeclaredIdentifierMapper() {
 		return declaredIdentifierMapper;
 	}
 
 	public void setDeclaredIdentifierMapper(Component declaredIdentifierMapper) {
 		this.declaredIdentifierMapper = declaredIdentifierMapper;
 	}
 
 	public boolean hasIdentifierMapper() {
 		return identifierMapper != null;
 	}
 
 	public void setIdentifierMapper(Component handle) {
 		this.identifierMapper = handle;
 	}
 
 	public void addTuplizer(EntityMode entityMode, String implClassName) {
 		if ( tuplizerImpls == null ) {
 			tuplizerImpls = new HashMap();
 		}
 		tuplizerImpls.put( entityMode, implClassName );
 	}
 
 	public String getTuplizerImplClassName(EntityMode mode) {
 		if ( tuplizerImpls == null ) return null;
 		return ( String ) tuplizerImpls.get( mode );
 	}
 
 	public java.util.Map getTuplizerMap() {
 		if ( tuplizerImpls == null ) {
 			return null;
 		}
 		return java.util.Collections.unmodifiableMap( tuplizerImpls );
 	}
 
 	public boolean hasNaturalId() {
 		Iterator props = getRootClass().getPropertyIterator();
 		while ( props.hasNext() ) {
 			if ( ( (Property) props.next() ).isNaturalIdentifier() ) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public abstract boolean isLazyPropertiesCacheable();
 
 	// The following methods are added to support @MappedSuperclass in the metamodel
 	public Iterator getDeclaredPropertyIterator() {
 		ArrayList iterators = new ArrayList();
 		iterators.add( declaredProperties.iterator() );
 		for ( int i = 0; i < joins.size(); i++ ) {
 			Join join = ( Join ) joins.get( i );
 			iterators.add( join.getDeclaredPropertyIterator() );
 		}
 		return new JoinedIterator( iterators );
 	}
 
 	public void addMappedsuperclassProperty(Property p) {
 		properties.add(p);
 		p.setPersistentClass(this);
 	}
 
 	public MappedSuperclass getSuperMappedSuperclass() {
 		return superMappedSuperclass;
 	}
 
 	public void setSuperMappedSuperclass(MappedSuperclass superMappedSuperclass) {
 		this.superMappedSuperclass = superMappedSuperclass;
 	}
 
 	// End of @Mappedsuperclass support
 
 }
\ No newline at end of file
diff --git a/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-4.0.xsd b/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-4.0.xsd
index 4f26e29b4c..8be7d1fce8 100644
--- a/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-4.0.xsd
+++ b/hibernate-core/src/main/resources/org/hibernate/hibernate-mapping-4.0.xsd
@@ -1,1679 +1,1680 @@
 <?xml version="1.0" encoding="UTF-8"?>
 
 <!-- Hibernate Mapping DTD.
 
 An instance of this XML document may contain mappings for an arbitrary 
 number of classes. The class mappings may contain associations to classes
 mapped in the same document or in another document. No class may be 
 mapped more than once. Each document may also contain definitions of an
 arbitrary number of queries, and import declarations of arbitrary classes. 
 
 -->
 <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema"
            xmlns="http://www.hibernate.org/xsd/hibernate-mapping"
            targetNamespace="http://www.hibernate.org/xsd/hibernate-mapping"
            elementFormDefault="qualified"
            version="4.0">
 
   <!--
   	The document root.
    -->
   <xs:element name="hibernate-mapping">
     <xs:complexType>
       <xs:sequence>
         <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
         <!--
             <identifier-generator.../> allows customized short-naming of IdentifierGenerator implementations.
         -->
         <xs:element name="identifier-generator" minOccurs="0" maxOccurs="unbounded">
           <xs:complexType>
             <xs:attribute name="class" use="required" type="xs:string"/>
             <xs:attribute name="name" use="required" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <!--
         	<typedef.../> allows defining a customized type mapping for a Hibernate type. May
         	contain parameters for parameterizable types.
         -->
         <xs:element name="typedef" minOccurs="0" maxOccurs="unbounded">
           <xs:complexType>
             <xs:sequence>
               <xs:element name="param" minOccurs="0" maxOccurs="unbounded" type="param-element"/>
             </xs:sequence>
             <xs:attribute name="class" use="required" type="xs:string"/>
             <xs:attribute name="name" use="required" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <!--
         	FILTER-DEF element; top-level filter definition.
         -->
         <xs:element name="filter-def" minOccurs="0" maxOccurs="unbounded">
           <xs:complexType mixed="true">
             <xs:sequence minOccurs="0" maxOccurs="unbounded">
               <!--
               	FILTER-PARAM element; qualifies parameters found within a FILTER-DEF
               	condition.
               -->
               <xs:element name="filter-param">
                 <xs:complexType>
                   <xs:attribute name="name" use="required" type="xs:string"/> <!-- The parameter name -->
                   <xs:attribute name="type" use="required" type="xs:string"/> <!-- The parameter type -->
                 </xs:complexType>
               </xs:element>
             </xs:sequence>
             <xs:attribute name="condition" type="xs:string"/>
             <xs:attribute name="name" use="required" type="xs:string"/> <!-- The filter name -->
           </xs:complexType>
         </xs:element>
         <!--
         	IMPORT element definition; an explicit query language "import"
         -->
         <xs:element name="import" minOccurs="0" maxOccurs="unbounded">
           <xs:complexType>
             <xs:attribute name="class" use="required" type="xs:string"/>
             <xs:attribute name="rename" type="xs:string"/> <!-- default: unqualified class name -->
           </xs:complexType>
         </xs:element>
         <xs:choice minOccurs="0" maxOccurs="unbounded">
           <!--
           	Root entity mapping.  Poorly named as entities do not have to be represented by 
           	classes at all.  Mapped entities may be represented via different methodologies 
           	(POJO, Map, Dom4j).
           -->
           <xs:element name="class">
             <xs:complexType>
               <xs:sequence>
                 <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
                 <xs:element name="subselect" minOccurs="0" type="xs:string"/>
                 <xs:element name="cache" minOccurs="0" type="cache-element"/>
                 <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
                 <!-- The comment element allows definition of a database table or column comment. -->
                 <xs:element name="comment" minOccurs="0" type="xs:string"/>
                 <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
                 <xs:choice>
                   <!-- Declares the id type, column and generation algorithm for an entity class.
                   If a name attribut is given, the id is exposed to the application through the 
                   named property of the class. If not, the id is only exposed to the application 
                   via Session.getIdentifier() -->
                   <xs:element name="id">
                     <xs:complexType>
                       <xs:sequence>
                         <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
                         <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
                         <xs:element name="type" minOccurs="0" type="type-element"/>
                         <xs:element name="generator" minOccurs="0" type="generator-element"/>
                       </xs:sequence>
                       <xs:attribute name="access" type="xs:string"/>
                       <xs:attribute name="column" type="xs:string"/>
                       <xs:attribute name="length" type="xs:string"/>
                       <xs:attribute name="name" type="xs:string"/>
                       <xs:attribute name="node" type="xs:string"/>
                       <xs:attribute name="type" type="xs:string"/>
                       <xs:attribute name="unsaved-value" type="xs:string"/> <!-- any|none|null|undefined|0|-1|... -->
                     </xs:complexType>
                   </xs:element>
                   <!-- A composite key may be modelled by a java class with a property for each 
                   key column. The class must implement java.io.Serializable and reimplement equals() 
                   and hashCode(). -->
                   <xs:element name="composite-id">
                     <xs:complexType>
                       <xs:sequence>
                         <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
                         <xs:choice maxOccurs="unbounded">
                           <xs:element name="key-property" type="key-property-element"/>
                           <xs:element name="key-many-to-one" type="key-many-to-one-element"/>
                         </xs:choice>
                         <xs:element name="generator" minOccurs="0" type="generator-element"/>
                       </xs:sequence>
                       <xs:attribute name="access" type="xs:string"/>
                       <xs:attribute name="class" type="xs:string"/>
                       <xs:attribute name="mapped" default="false" type="xs:boolean"/>
                       <xs:attribute name="name" type="xs:string"/>
                       <xs:attribute name="node" type="xs:string"/>
                       <xs:attribute name="unsaved-value" default="undefined">
                         <xs:simpleType>
                           <xs:restriction base="xs:token">
                             <xs:enumeration value="any"/>
                             <xs:enumeration value="none"/>
                             <xs:enumeration value="undefined"/>
                           </xs:restriction>
                         </xs:simpleType>
                       </xs:attribute>
                     </xs:complexType>
                   </xs:element>
                 </xs:choice>
                 <!-- Polymorphic data requires a column holding a class discriminator value. This
                 value is not directly exposed to the application. -->
                 <xs:element name="discriminator" minOccurs="0">
                   <xs:complexType>
                     <xs:sequence>
                       <xs:choice minOccurs="0">
                         <xs:element name="column" type="column-element"/>
                         <!-- The formula and subselect elements allow us to map derived properties and 
                         entities. -->
                         <xs:element name="formula" type="xs:string"/>
                       </xs:choice>
                     </xs:sequence>
                     <xs:attribute name="column" type="xs:string"/> <!-- default: "class"|none -->
                     <xs:attribute name="force" default="false" type="xs:boolean"/>
                     <xs:attribute name="formula" type="xs:string"/>
                     <xs:attribute name="insert" default="true" type="xs:boolean"/>
                     <xs:attribute name="length" type="xs:string"/>
                     <xs:attribute name="not-null" default="true" type="xs:boolean"/>
                     <xs:attribute name="type" default="string" type="xs:string"/>
                   </xs:complexType>
                 </xs:element>
                 <!-- A natural-id element allows declaration of the unique business key -->
                 <xs:element name="natural-id" minOccurs="0">
                   <xs:complexType>
                     <xs:sequence>
                       <xs:choice minOccurs="0" maxOccurs="unbounded">
                         <xs:element name="property" type="property-element"/>
                         <xs:element name="many-to-one" type="many-to-one-element"/>
                         <xs:element name="component" type="component-element"/>
                         <xs:element name="dynamic-component" type="dynamic-component-element"/>
                         <xs:element name="any" type="any-element"/>
                       </xs:choice>
                     </xs:sequence>
                     <xs:attribute name="mutable" default="false" type="xs:boolean"/>
                   </xs:complexType>
                 </xs:element>
                 <xs:choice minOccurs="0">
                   <!-- Versioned data requires a column holding a version number. This is exposed to the
                   application through a property of the Java class. -->
                   <xs:element name="version">
                     <xs:complexType>
                       <xs:sequence>
                         <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
                         <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
                       </xs:sequence>
                       <xs:attribute name="access" type="xs:string"/>
                       <xs:attribute name="column" type="xs:string"/>
                       <xs:attribute name="generated" default="never" type="generated-attribute"/>
                       <xs:attribute name="insert" type="xs:boolean"/>
                       <xs:attribute name="name" use="required" type="xs:string"/>
                       <xs:attribute name="node" type="xs:string"/>
                       <xs:attribute name="type" default="integer" type="xs:string"/>
                       <xs:attribute name="unsaved-value" default="undefined">
                         <xs:simpleType>
                           <xs:restriction base="xs:token">
                             <xs:enumeration value="negative"/>
                             <xs:enumeration value="null"/>
                             <xs:enumeration value="undefined"/>
                           </xs:restriction>
                         </xs:simpleType>
                       </xs:attribute>
                     </xs:complexType>
                   </xs:element>
                   <xs:element name="timestamp">
                     <xs:complexType>
                       <xs:sequence>
                         <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
                       </xs:sequence>
                       <xs:attribute name="access" type="xs:string"/>
                       <xs:attribute name="column" type="xs:string"/>
                       <xs:attribute name="generated" default="never" type="generated-attribute"/>
                       <xs:attribute name="name" use="required" type="xs:string"/>
                       <xs:attribute name="node" type="xs:string"/>
                       <xs:attribute name="source" default="vm">
                         <xs:simpleType>
                           <xs:restriction base="xs:token">
                             <xs:enumeration value="db"/>
                             <xs:enumeration value="vm"/>
                           </xs:restriction>
                         </xs:simpleType>
                       </xs:attribute>
                       <xs:attribute name="unsaved-value" default="null">
                         <xs:simpleType>
                           <xs:restriction base="xs:token">
                             <xs:enumeration value="null"/>
                             <xs:enumeration value="undefined"/>
                           </xs:restriction>
                         </xs:simpleType>
                       </xs:attribute>
                     </xs:complexType>
                   </xs:element>
                 </xs:choice>
                 <xs:choice minOccurs="0" maxOccurs="unbounded">
                   <xs:element name="property" type="property-element"/>
                   <xs:element name="many-to-one" type="many-to-one-element"/>
                   <xs:element name="one-to-one" type="one-to-one-element"/>
                   <xs:element name="component" type="component-element"/>
                   <xs:element name="dynamic-component" type="dynamic-component-element"/>
                   <xs:element name="properties" type="properties-element"/>
                   <xs:element name="any" type="any-element"/>
                   <xs:element name="map" type="map-element"/>
                   <xs:element name="set" type="set-element"/>
                   <xs:element name="list" type="list-element"/>
                   <xs:element name="bag" type="bag-element"/>
                   <xs:element name="idbag" type="idbag-element"/>
                   <xs:element name="array" type="array-element"/>
                   <xs:element name="primitive-array" type="primitive-array-element"/>
                 </xs:choice>
                 <xs:choice>
                   <xs:sequence>
                     <xs:element name="join" minOccurs="0" maxOccurs="unbounded" type="join-element"/>
                     <xs:element name="subclass" minOccurs="0" maxOccurs="unbounded" type="subclass-element"/>
                   </xs:sequence>
                   <xs:element name="joined-subclass" minOccurs="0" maxOccurs="unbounded" type="joined-subclass-element"/>
                   <xs:element name="union-subclass" minOccurs="0" maxOccurs="unbounded" type="union-subclass-element"/>
                 </xs:choice>
                 <xs:element name="loader" minOccurs="0" type="loader-element"/>
                 <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
                 <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
                 <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
                 <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
                 <xs:element name="fetch-profile" minOccurs="0" maxOccurs="unbounded" type="fetch-profile-element"/>
                 <xs:element name="resultset" minOccurs="0" maxOccurs="unbounded" type="resultset-element"/>
                 <xs:choice minOccurs="0" maxOccurs="unbounded">
                   <xs:element name="query" type="query-element"/>
                   <xs:element name="sql-query" type="sql-query-element"/>
                 </xs:choice>
               </xs:sequence>
               <xs:attribute name="abstract" type="xs:boolean"/>
               <xs:attribute name="batch-size" type="xs:string"/>
               <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
               <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
               <xs:attribute name="discriminator-value" type="xs:string"/> <!-- default: unqualified class name | none -->
               <xs:attribute name="dynamic-insert" default="false" type="xs:boolean"/>
               <xs:attribute name="dynamic-update" default="false" type="xs:boolean"/>
               <xs:attribute name="entity-name" type="xs:string"/>
               <xs:attribute name="lazy" type="xs:boolean"/>
               <xs:attribute name="mutable" default="true" type="xs:boolean"/>
               <xs:attribute name="name" type="xs:string"/> <!-- this is the class name -->
               <xs:attribute name="node" type="xs:string"/>
               <xs:attribute name="optimistic-lock" default="version">
                 <xs:simpleType>
                   <xs:restriction base="xs:token">
                     <xs:enumeration value="all"/>
                     <xs:enumeration value="dirty"/>
                     <xs:enumeration value="none"/>
                     <xs:enumeration value="version"/>
                   </xs:restriction>
                 </xs:simpleType>
               </xs:attribute>
               <xs:attribute name="persister" type="xs:string"/>
               <xs:attribute name="polymorphism" default="implicit">
                 <xs:simpleType>
                   <xs:restriction base="xs:token">
                     <xs:enumeration value="explicit"/>
                     <xs:enumeration value="implicit"/>
                   </xs:restriction>
                 </xs:simpleType>
               </xs:attribute>
               <xs:attribute name="proxy" type="xs:string"/> <!-- default: no proxy interface -->
               <xs:attribute name="rowid" type="xs:string"/>
               <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
               <xs:attribute name="select-before-update" default="false" type="xs:boolean"/>
               <xs:attribute name="subselect" type="xs:string"/>
               <xs:attribute name="table" type="xs:string"/> <!-- default: unqualified classname -->
               <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
             </xs:complexType>
           </xs:element>
           <xs:element name="subclass" type="subclass-element"/>
           <xs:element name="joined-subclass" type="joined-subclass-element"/>
           <xs:element name="union-subclass" type="union-subclass-element"/>
         </xs:choice>
         <xs:element name="resultset" minOccurs="0" maxOccurs="unbounded" type="resultset-element"/>
         <xs:choice minOccurs="0" maxOccurs="unbounded">
           <xs:element name="query" type="query-element"/>
           <xs:element name="sql-query" type="sql-query-element"/>
         </xs:choice>
         <xs:element name="fetch-profile" minOccurs="0" maxOccurs="unbounded" type="fetch-profile-element"/>
         <!--
             Element for defining "auxiliary" database objects.  Must be one of two forms:
             #1 :
                 <database-object>
                     <definition class="CustomClassExtendingAuxiliaryObject"/>
                 </database-object>
             #2 :
                 <database-object>
                     <create>CREATE OR REPLACE ....</create>
                     <drop>DROP ....</drop>
                 </database-object>
         -->
         <xs:element name="database-object" minOccurs="0" maxOccurs="unbounded">
           <xs:complexType>
             <xs:sequence>
               <xs:choice>
                 <xs:element name="definition">
                   <xs:complexType>
                     <xs:attribute name="class" use="required" type="xs:string"/>
                   </xs:complexType>
                 </xs:element>
                 <xs:sequence>
                   <xs:element name="create" type="xs:string"/>
                   <xs:element name="drop" type="xs:string"/>
                 </xs:sequence>
               </xs:choice>
               <!--
                   dialect-scope element allows scoping auxiliary-objects to a particular
                   Hibernate dialect implementation.
               -->
               <xs:element name="dialect-scope" minOccurs="0" maxOccurs="unbounded">
                 <xs:complexType mixed="true">
                   <xs:simpleContent>
                     <xs:extension base="xs:string">
                       <xs:attribute name="name" use="required" type="xs:string"/>
                     </xs:extension>
                   </xs:simpleContent>
                 </xs:complexType>
               </xs:element>
             </xs:sequence>
           </xs:complexType>
         </xs:element>
       </xs:sequence>
       <xs:attribute name="auto-import" default="true" type="xs:boolean"/>
       <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
       <xs:attribute name="default-access" default="property" type="xs:string"/>
       <xs:attribute name="default-cascade" default="none" type="xs:string"/>
       <xs:attribute name="default-lazy" default="true" type="xs:boolean"/>
       <xs:attribute name="package" type="xs:string"/> <!-- default: none -->
       <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     </xs:complexType>
   </xs:element>
 
   <!-- An "any" association is a polymorphic association to any table with
   the given identifier type. The first listed column is a VARCHAR column 
   holding the name of the class (for that row). -->
   <xs:complexType name="any-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="meta-value" minOccurs="0" maxOccurs="unbounded" type="meta-value-element"/>
       <xs:element name="column" type="column-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="id-type" use="required" type="xs:string"/>
     <xs:attribute name="index" type="xs:string"/> <!-- include the columns spanned by this association in an index -->
     <xs:attribute name="insert" default="true" type="xs:boolean"/>
     <xs:attribute name="lazy" default="false" type="xs:boolean"/>
     <xs:attribute name="meta-type" type="xs:string"/> <!--- default: Hibernate.STRING -->
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="update" default="true" type="xs:boolean"/>
   </xs:complexType>
 
   <xs:complexType name="array-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="cache" minOccurs="0" type="cache-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice>
         <xs:element name="index" type="index-element"/>
         <xs:element name="list-index" type="list-index-element"/>
       </xs:choice>
       <xs:choice>
         <xs:element name="element" type="element-element"/>
         <xs:element name="one-to-many" type="one-to-many-element"/>
         <xs:element name="many-to-many" type="many-to-many-element"/>
         <xs:element name="composite-element" type="composite-element-element"/>
         <xs:element name="many-to-any" type="many-to-any-element"/>
       </xs:choice>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="sql-delete-all" minOccurs="0" type="sql-delete-all-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="collection-type" type="xs:string"/>
     <xs:attribute name="element-class" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="fetch" type="fetch-attribute-with-subselect"/>
     <xs:attribute name="inverse" default="false" type="xs:boolean"/>
     <xs:attribute name="mutable" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: name -->
     <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
   </xs:complexType>
 
   <xs:complexType name="bag-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="cache" minOccurs="0" type="cache-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice>
         <xs:element name="element" type="element-element"/>
         <xs:element name="one-to-many" type="one-to-many-element"/>
         <xs:element name="many-to-many" type="many-to-many-element"/>
         <xs:element name="composite-element" type="composite-element-element"/>
         <xs:element name="many-to-any" type="many-to-any-element"/>
       </xs:choice>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="sql-delete-all" minOccurs="0" type="sql-delete-all-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="collection-type" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="fetch" type="fetch-attribute-with-subselect"/>
     <xs:attribute name="inverse" default="false" type="xs:boolean"/>
     <xs:attribute name="lazy" type="lazy-attribute-with-extra"/>
     <xs:attribute name="mutable" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="order-by" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: name -->
     <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
   </xs:complexType>
 
   <!-- The cache element enables caching of an entity class. -->
   <xs:complexType name="cache-element">
     <xs:attribute name="include" default="all">
       <xs:simpleType>
         <xs:restriction base="xs:token">
           <xs:enumeration value="all"/>
           <xs:enumeration value="non-lazy"/>
         </xs:restriction>
       </xs:simpleType>
     </xs:attribute>
     <xs:attribute name="region" type="xs:string"/> <!-- default: class or collection role name -->
     <xs:attribute name="usage" use="required">
       <xs:simpleType>
         <xs:restriction base="xs:token">
           <xs:enumeration value="nonstrict-read-write"/>
           <xs:enumeration value="read-only"/>
           <xs:enumeration value="read-write"/>
           <xs:enumeration value="transactional"/>
         </xs:restriction>
       </xs:simpleType>
     </xs:attribute>
   </xs:complexType>
 
   <!-- The column element is an alternative to column attributes and required for 
   mapping associations to classes with composite ids. -->
   <xs:complexType name="column-element">
     <xs:sequence>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
     </xs:sequence>
     <xs:attribute name="check" type="xs:string"/> <!-- default: no check constraint -->
     <xs:attribute name="default" type="xs:string"/> <!-- default: no default value -->
     <xs:attribute name="index" type="xs:string"/>
     <xs:attribute name="length" type="xs:string"/> <!-- default: 255 -->
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="not-null" type="xs:boolean"/> <!-- default: false (except for id properties) -->
     <xs:attribute name="precision" type="xs:string"/>
     <xs:attribute name="read" type="xs:string"/> <!-- default: column name -->
     <xs:attribute name="scale" type="xs:string"/>
     <xs:attribute name="sql-type" type="xs:string"/> <!-- override default column type for hibernate type -->
     <xs:attribute name="unique" type="xs:boolean"/> <!-- default: false (except for id properties) -->
     <xs:attribute name="unique-key" type="xs:string"/> <!-- default: no unique key -->
     <xs:attribute name="write" type="xs:string"/> <!-- default: parameter placeholder ('?') -->
   </xs:complexType>
 
   <!-- A component is a user-defined class, persisted along with its containing entity
   to the table of the entity class. JavaBeans style properties of the component are
   mapped to columns of the table of the containing entity. A null component reference
   is mapped to null values in all columns and vice versa. Components do not support
   shared reference semantics. -->
   <xs:complexType name="component-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
       <xs:element name="parent" minOccurs="0" type="parent-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="one-to-one" type="one-to-one-element"/>
         <xs:element name="component" type="component-element"/>
         <xs:element name="dynamic-component" type="dynamic-component-element"/>
         <xs:element name="any" type="any-element"/>
         <xs:element name="map" type="map-element"/>
         <xs:element name="set" type="set-element"/>
         <xs:element name="list" type="list-element"/>
         <xs:element name="bag" type="bag-element"/>
         <xs:element name="array" type="array-element"/>
         <xs:element name="primitive-array" type="primitive-array-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="insert" default="true" type="xs:boolean"/>
     <xs:attribute name="lazy" default="false" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
     <xs:attribute name="update" default="true" type="xs:boolean"/>
   </xs:complexType>
 
   <!-- A composite element allows a collection to hold instances of an arbitrary 
   class, without the requirement of joining to an entity table. Composite elements
   have component semantics - no shared references and ad hoc null value semantics. 
   Composite elements may not hold nested collections. -->
   <xs:complexType name="composite-element-element">
     <xs:sequence>
       <xs:sequence>
         <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       </xs:sequence>
       <xs:element name="parent" minOccurs="0" type="parent-element"/>
       <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="any" type="any-element"/>
         <xs:element name="nested-composite-element" type="nested-composite-element-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="class" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
   </xs:complexType>
 
   <!-- A dynamic-component maps columns of the database entity to a java.util.Map 
   at the Java level -->
   <xs:complexType name="dynamic-component-element">
     <xs:sequence>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="one-to-one" type="one-to-one-element"/>
         <xs:element name="component" type="component-element"/>
         <xs:element name="dynamic-component" type="dynamic-component-element"/>
         <xs:element name="any" type="any-element"/>
         <xs:element name="map" type="map-element"/>
         <xs:element name="set" type="set-element"/>
         <xs:element name="list" type="list-element"/>
         <xs:element name="bag" type="bag-element"/>
         <xs:element name="array" type="array-element"/>
         <xs:element name="primitive-array" type="primitive-array-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="insert" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
     <xs:attribute name="update" default="true" type="xs:boolean"/>
   </xs:complexType>
 
   <!-- Declares the element type of a collection of basic type -->
   <xs:complexType name="element-element">
     <xs:sequence>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="column" type="column-element"/>
         <!-- The formula and subselect elements allow us to map derived properties and 
         entities. -->
         <xs:element name="formula" type="xs:string"/>
       </xs:choice>
       <xs:element name="type" minOccurs="0" type="type-element"/>
     </xs:sequence>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="formula" type="xs:string"/>
     <xs:attribute name="length" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="not-null" default="false" type="xs:boolean"/>
     <xs:attribute name="precision" type="xs:string"/>
     <xs:attribute name="scale" type="xs:string"/>
     <xs:attribute name="type" type="xs:string"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
   </xs:complexType>
 
   <!--
   -->
   <xs:complexType name="fetch-profile-element">
     <xs:sequence>
       <!--
           The <fetch> element defines a single path to which the fetch
           refers, as well as the style of fetch to apply.  The 'root' of the
           path is different depending upon the context in which the
           containing <fetch-profile/> occurs; within a <class/> element,
           the entity-name of the containing class mapping is assumed...
       -->
       <xs:element name="fetch" minOccurs="0" maxOccurs="unbounded">
         <xs:complexType>
           <xs:attribute name="association" use="required" type="xs:string"/>
           <xs:attribute name="entity" type="xs:string"/> <!-- Implied as long as the containing fetch profile is contained in a class mapping -->
           <xs:attribute name="style" default="join">
             <xs:simpleType>
               <xs:restriction base="xs:token">
                 <xs:enumeration value="join"/>
                 <xs:enumeration value="select"/>
               </xs:restriction>
             </xs:simpleType>
           </xs:attribute>
         </xs:complexType>
       </xs:element>
     </xs:sequence>
     <xs:attribute name="name" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!--
   	FILTER element; used to apply a filter.
   -->
   <xs:complexType name="filter-element" mixed="true">
     <xs:simpleContent>
       <xs:extension base="xs:string">
+      	<xs:attribute name="table" type="xs:string"/>
         <xs:attribute name="condition" type="xs:string"/>
         <xs:attribute name="name" use="required" type="xs:string"/>
       </xs:extension>
     </xs:simpleContent>
   </xs:complexType>
 
   <!-- Generators generate unique identifiers. The class attribute specifies a Java 
   class implementing an id generation algorithm. -->
   <xs:complexType name="generator-element">
     <xs:sequence>
       <xs:element name="param" minOccurs="0" maxOccurs="unbounded" type="param-element"/>
     </xs:sequence>
     <xs:attribute name="class" use="required" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="idbag-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="cache" minOccurs="0" type="cache-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="collection-id">
         <xs:complexType>
           <xs:sequence>
             <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
             <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
             <xs:element name="generator" type="generator-element"/>
           </xs:sequence>
           <xs:attribute name="column" use="required" type="xs:string"/>
           <xs:attribute name="length" type="xs:string"/>
           <xs:attribute name="type" use="required" type="xs:string"/>
         </xs:complexType>
       </xs:element>
       <xs:element name="key" type="key-element"/>
       <xs:choice>
         <xs:element name="element" type="element-element"/>
         <xs:element name="many-to-many" type="many-to-many-element"/>
         <xs:element name="composite-element" type="composite-element-element"/>
         <xs:element name="many-to-any" type="many-to-any-element"/>
       </xs:choice>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="sql-delete-all" minOccurs="0" type="sql-delete-all-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="collection-type" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="fetch" type="fetch-attribute-with-subselect"/>
     <xs:attribute name="lazy" type="lazy-attribute-with-extra"/>
     <xs:attribute name="mutable" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="order-by" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: name -->
     <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
   </xs:complexType>
 
   <xs:complexType name="index-element">
     <xs:sequence>
       <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
     </xs:sequence>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="length" type="xs:string"/>
     <xs:attribute name="type" type="xs:string"/> <!-- required for maps -->
   </xs:complexType>
 
   <!-- A join allows some properties of a class to be persisted to a second table -->
   <xs:complexType name="join-element">
     <xs:sequence>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="component" type="component-element"/>
         <xs:element name="dynamic-component" type="dynamic-component-element"/>
         <xs:element name="any" type="any-element"/>
       </xs:choice>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
     </xs:sequence>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="fetch" default="join" type="fetch-attribute"/>
     <xs:attribute name="inverse" default="false" type="xs:boolean"/>
     <xs:attribute name="optional" default="false" type="xs:boolean"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!--
   	Joined subclasses are used for the normalized table-per-subclass mapping strategy
   	See the note on the class element regarding <pojo/> vs. @name usage...
   -->
   <xs:complexType name="joined-subclass-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="one-to-one" type="one-to-one-element"/>
         <xs:element name="component" type="component-element"/>
         <xs:element name="dynamic-component" type="dynamic-component-element"/>
         <xs:element name="properties" type="properties-element"/>
         <xs:element name="any" type="any-element"/>
         <xs:element name="map" type="map-element"/>
         <xs:element name="set" type="set-element"/>
         <xs:element name="list" type="list-element"/>
         <xs:element name="bag" type="bag-element"/>
         <xs:element name="idbag" type="idbag-element"/>
         <xs:element name="array" type="array-element"/>
         <xs:element name="primitive-array" type="primitive-array-element"/>
       </xs:choice>
       <xs:element name="joined-subclass" minOccurs="0" maxOccurs="unbounded" type="joined-subclass-element"/>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
       <xs:element name="fetch-profile" minOccurs="0" maxOccurs="unbounded" type="fetch-profile-element"/>
       <xs:element name="resultset" minOccurs="0" maxOccurs="unbounded" type="resultset-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="query" type="query-element"/>
         <xs:element name="sql-query" type="sql-query-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="abstract" type="xs:boolean"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/>
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="dynamic-insert" default="false" type="xs:boolean"/>
     <xs:attribute name="dynamic-update" default="false" type="xs:boolean"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="extends" type="xs:string"/> <!-- default: none when toplevel, otherwise the nearest class definition -->
     <xs:attribute name="lazy" type="xs:boolean"/>
     <xs:attribute name="name" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="proxy" type="xs:string"/> <!-- default: no proxy interface -->
     <xs:attribute name="schema" type="xs:string"/>
     <xs:attribute name="select-before-update" default="false" type="xs:boolean"/>
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: unqualified class name -->
   </xs:complexType>
 
   <!-- Declares the column name of a foreign key. -->
   <xs:complexType name="key-element">
     <xs:sequence>
       <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
     </xs:sequence>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="foreign-key" type="xs:string"/>
     <xs:attribute name="not-null" type="xs:boolean"/>
     <xs:attribute name="on-delete" default="noaction">
       <xs:simpleType>
         <xs:restriction base="xs:token">
           <xs:enumeration value="cascade"/>
           <xs:enumeration value="noaction"/>
         </xs:restriction>
       </xs:simpleType>
     </xs:attribute>
     <xs:attribute name="property-ref" type="xs:string"/>
     <xs:attribute name="unique" type="xs:boolean"/>
     <xs:attribute name="update" type="xs:boolean"/>
   </xs:complexType>
 
   <!-- A many-to-one association embedded in a composite identifier or map index 
   (always not-null, never cascade). -->
   <xs:complexType name="key-many-to-one-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="foreign-key" type="xs:string"/>
     <xs:attribute name="lazy" type="lazy-attribute"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!-- A property embedded in a composite identifier or map index (always not-null). -->
   <xs:complexType name="key-property-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
       <xs:element name="type" minOccurs="0" type="type-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="length" type="xs:string"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="type" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="list-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="cache" minOccurs="0" type="cache-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice>
         <xs:element name="index" type="index-element"/>
         <xs:element name="list-index" type="list-index-element"/>
       </xs:choice>
       <xs:choice>
         <xs:element name="element" type="element-element"/>
         <xs:element name="one-to-many" type="one-to-many-element"/>
         <xs:element name="many-to-many" type="many-to-many-element"/>
         <xs:element name="composite-element" type="composite-element-element"/>
         <xs:element name="many-to-any" type="many-to-any-element"/>
       </xs:choice>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="sql-delete-all" minOccurs="0" type="sql-delete-all-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="collection-type" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="fetch" type="fetch-attribute-with-subselect"/>
     <xs:attribute name="inverse" default="false" type="xs:boolean"/>
     <xs:attribute name="lazy" type="lazy-attribute-with-extra"/>
     <xs:attribute name="mutable" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: name -->
     <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
   </xs:complexType>
 
   <!-- Declares the type and column mapping for a collection index (array or
   list index, or key of a map). -->
   <xs:complexType name="list-index-element">
     <xs:sequence>
       <xs:element name="column" minOccurs="0" type="column-element"/>
     </xs:sequence>
     <xs:attribute name="base" default="0" type="xs:string"/>
     <xs:attribute name="column" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="load-collection-element">
     <xs:sequence minOccurs="0" maxOccurs="unbounded">
       <xs:element name="return-property" type="return-property-element"/>
     </xs:sequence>
     <xs:attribute name="alias" use="required" type="xs:string"/>
     <xs:attribute name="lock-mode" default="read" type="lock-mode-attribute"/>
     <xs:attribute name="role" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!-- The loader element allows specification of a named query to be used for fetching
   an entity or collection -->
   <xs:complexType name="loader-element">
     <xs:attribute name="query-ref" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!-- A "many to any" defines a polymorphic association to any table 
   with the given identifier type. The first listed column is a VARCHAR column 
   holding the name of the class (for that row). -->
   <xs:complexType name="many-to-any-element">
     <xs:sequence>
       <xs:element name="meta-value" minOccurs="0" maxOccurs="unbounded" type="meta-value-element"/>
       <xs:element name="column" type="column-element"/>
     </xs:sequence>
     <xs:attribute name="id-type" use="required" type="xs:string"/>
     <xs:attribute name="meta-type" type="xs:string"/> <!--- default: Hibernate.CLASS -->
   </xs:complexType>
 
   <!-- Many to many association. This tag declares the entity-class
   element type of a collection and specifies a many-to-many relational model -->
   <xs:complexType name="many-to-many-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="column" type="column-element"/>
         <!-- The formula and subselect elements allow us to map derived properties and 
         entities. -->
         <xs:element name="formula" type="xs:string"/>
       </xs:choice>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
     </xs:sequence>
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="fetch" type="fetch-attribute"/>
     <xs:attribute name="foreign-key" type="xs:string"/>
     <xs:attribute name="formula" type="xs:string"/>
     <xs:attribute name="lazy" type="lazy-attribute"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="not-found" default="exception" type="not-found-attribute"/>
     <xs:attribute name="order-by" type="xs:string"/>
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="property-ref" type="xs:string"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
     <xs:attribute name="where" type="xs:string"/>
   </xs:complexType>
 
   <!-- Declares an association between two entities (Or from a component, component element,
   etc. to an entity). -->
   <xs:complexType name="many-to-one-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="column" type="column-element"/>
         <!-- The formula and subselect elements allow us to map derived properties and 
         entities. -->
         <xs:element name="formula" type="xs:string"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="fetch" type="fetch-attribute"/>
     <xs:attribute name="foreign-key" type="xs:string"/>
     <xs:attribute name="formula" type="xs:string"/>
     <xs:attribute name="index" type="xs:string"/>
     <xs:attribute name="insert" default="true" type="xs:boolean"/>
     <xs:attribute name="lazy" type="lazy-attribute-with-no-proxy"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="not-found" default="exception" type="not-found-attribute"/>
     <xs:attribute name="not-null" type="xs:boolean"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="property-ref" type="xs:string"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
     <xs:attribute name="unique-key" type="xs:string"/>
     <xs:attribute name="update" default="true" type="xs:boolean"/>
   </xs:complexType>
 
   <!-- Collection declarations nested inside a class declaration indicate a foreign key 
   relationship from the collection table to the enclosing class. -->
   <xs:complexType name="map-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="cache" minOccurs="0" type="cache-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice>
         <xs:element name="map-key">
           <xs:complexType>
             <xs:sequence>
               <xs:choice minOccurs="0" maxOccurs="unbounded">
                 <xs:element name="column" type="column-element"/>
                 <!-- The formula and subselect elements allow us to map derived properties and 
                 entities. -->
                 <xs:element name="formula" type="xs:string"/>
               </xs:choice>
               <xs:element name="type" minOccurs="0" type="type-element"/>
             </xs:sequence>
             <xs:attribute name="column" type="xs:string"/>
             <xs:attribute name="formula" type="xs:string"/>
             <xs:attribute name="length" type="xs:string"/>
             <xs:attribute name="node" type="xs:string"/>
             <xs:attribute name="type" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <!-- Composite index of a map ie. a map keyed on components. -->
         <xs:element name="composite-map-key">
           <xs:complexType>
             <xs:sequence>
               <xs:choice maxOccurs="unbounded">
                 <xs:element name="key-property" type="key-property-element"/>
                 <xs:element name="key-many-to-one" type="key-many-to-one-element"/>
               </xs:choice>
             </xs:sequence>
             <xs:attribute name="class" use="required" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <!-- Many to many association mapped to the key of a map. ie. a map keyed
         on entities. -->
         <xs:element name="map-key-many-to-many">
           <xs:complexType>
             <xs:sequence>
               <xs:choice minOccurs="0" maxOccurs="unbounded">
                 <xs:element name="column" type="column-element"/>
                 <!-- The formula and subselect elements allow us to map derived properties and 
                 entities. -->
                 <xs:element name="formula" type="xs:string"/>
               </xs:choice>
             </xs:sequence>
             <xs:attribute name="class" type="xs:string"/>
             <xs:attribute name="column" type="xs:string"/>
             <xs:attribute name="entity-name" type="xs:string"/>
             <xs:attribute name="foreign-key" type="xs:string"/>
             <xs:attribute name="formula" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <xs:element name="index" type="index-element"/>
         <xs:element name="composite-index">
           <xs:complexType>
             <xs:sequence>
               <xs:choice maxOccurs="unbounded">
                 <xs:element name="key-property" type="key-property-element"/>
                 <xs:element name="key-many-to-one" type="key-many-to-one-element"/>
               </xs:choice>
             </xs:sequence>
             <xs:attribute name="class" use="required" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <xs:element name="index-many-to-many">
           <xs:complexType>
             <xs:sequence>
               <xs:element name="column" minOccurs="0" maxOccurs="unbounded" type="column-element"/>
             </xs:sequence>
             <xs:attribute name="class" use="required" type="xs:string"/>
             <xs:attribute name="column" type="xs:string"/>
             <xs:attribute name="entity-name" type="xs:string"/>
             <xs:attribute name="foreign-key" type="xs:string"/>
           </xs:complexType>
         </xs:element>
         <xs:element name="index-many-to-any">
           <xs:complexType>
             <xs:sequence>
               <xs:element name="column" type="column-element"/>
             </xs:sequence>
             <xs:attribute name="id-type" use="required" type="xs:string"/>
             <xs:attribute name="meta-type" type="xs:string"/> <!--- default: Hibernate.CLASS -->
           </xs:complexType>
         </xs:element>
       </xs:choice>
       <xs:choice>
         <xs:element name="element" type="element-element"/>
         <xs:element name="one-to-many" type="one-to-many-element"/>
         <xs:element name="many-to-many" type="many-to-many-element"/>
         <xs:element name="composite-element" type="composite-element-element"/>
         <xs:element name="many-to-any" type="many-to-any-element"/>
       </xs:choice>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="sql-delete-all" minOccurs="0" type="sql-delete-all-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="collection-type" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="fetch" type="fetch-attribute-with-subselect"/>
     <xs:attribute name="inverse" default="false" type="xs:boolean"/>
     <xs:attribute name="lazy" type="lazy-attribute-with-extra"/>
     <xs:attribute name="mutable" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="order-by" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="sort" default="unsorted" type="xs:string"/> <!-- unsorted|natural|"comparator class", default: unsorted -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: name -->
     <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
   </xs:complexType>
 
   <!--
   	<meta.../> is used to assign meta-level attributes to a class
   	or property.  Is currently used by codegenerator as a placeholder for
   	values that is not directly related to OR mappings.
   -->
   <xs:complexType name="meta-element" mixed="true">
     <xs:simpleContent>
       <xs:extension base="xs:string">
         <xs:attribute name="attribute" use="required" type="xs:string"/>
         <xs:attribute name="inherit" default="true" type="xs:boolean"/>
       </xs:extension>
     </xs:simpleContent>
   </xs:complexType>
 
   <xs:complexType name="meta-value-element">
     <xs:attribute name="class" use="required" type="xs:string"/>
     <xs:attribute name="value" use="required" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="nested-composite-element-element">
     <xs:sequence>
       <xs:element name="parent" minOccurs="0" type="parent-element"/>
       <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="any" type="any-element"/>
         <xs:element name="nested-composite-element" type="nested-composite-element-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="class" use="required" type="xs:string"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
   </xs:complexType>
 
   <!-- One to many association. This tag declares the entity-class
   element type of a collection and specifies a one-to-many relational model -->
   <xs:complexType name="one-to-many-element">
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="not-found" default="exception" type="not-found-attribute"/>
   </xs:complexType>
 
   <!-- Declares a one-to-one association between two entities (Or from a component, 
   component element, etc. to an entity). -->
   <xs:complexType name="one-to-one-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <!-- The formula and subselect elements allow us to map derived properties and 
       entities. -->
       <xs:element name="formula" minOccurs="0" maxOccurs="unbounded" type="xs:string"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="constrained" default="false" type="xs:boolean"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="fetch" type="fetch-attribute"/>
     <xs:attribute name="foreign-key" type="xs:string"/>
     <xs:attribute name="formula" type="xs:string"/>
     <xs:attribute name="lazy" type="lazy-attribute-with-no-proxy"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="property-ref" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="param-element" mixed="true">
     <xs:simpleContent>
       <xs:extension base="xs:string">
         <xs:attribute name="name" use="required" type="xs:string"/>
       </xs:extension>
     </xs:simpleContent>
   </xs:complexType>
 
   <!-- The parent element maps a property of the component class as a pointer back to
   the owning entity. -->
   <xs:complexType name="parent-element">
     <xs:attribute name="name" use="required" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="primitive-array-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="cache" minOccurs="0" type="cache-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice>
         <xs:element name="index" type="index-element"/>
         <xs:element name="list-index" type="list-index-element"/>
       </xs:choice>
       <xs:element name="element" type="element-element"/>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="sql-delete-all" minOccurs="0" type="sql-delete-all-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="collection-type" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="fetch" type="fetch-attribute-with-subselect"/>
     <xs:attribute name="mutable" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: name -->
     <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
   </xs:complexType>
 
   <!-- properties declares that the contained properties form an alternate key. The name
   attribute allows an alternate key to be used as the target of a property-ref. -->
   <xs:complexType name="properties-element">
     <xs:sequence>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="component" type="component-element"/>
         <xs:element name="dynamic-component" type="dynamic-component-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="insert" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
     <xs:attribute name="update" default="true" type="xs:boolean"/>
   </xs:complexType>
 
   <!-- Property of an entity class or component, component-element, composite-id, etc. 
   JavaBeans style properties are mapped to table columns. -->
   <xs:complexType name="property-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="column" type="column-element"/>
         <!-- The formula and subselect elements allow us to map derived properties and 
         entities. -->
         <xs:element name="formula" type="xs:string"/>
       </xs:choice>
       <xs:element name="type" minOccurs="0" type="type-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="formula" type="xs:string"/>
     <xs:attribute name="generated" default="never">
       <xs:simpleType>
         <xs:restriction base="xs:token">
           <xs:enumeration value="always"/>
           <xs:enumeration value="insert"/>
           <xs:enumeration value="never"/>
         </xs:restriction>
       </xs:simpleType>
     </xs:attribute>
     <xs:attribute name="index" type="xs:string"/> <!-- include the columns spanned by this property in an index -->
     <xs:attribute name="insert" type="xs:boolean"/>
     <xs:attribute name="lazy" default="false" type="xs:boolean"/>
     <xs:attribute name="length" type="xs:string"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="not-null" type="xs:boolean"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="precision" type="xs:string"/>
     <xs:attribute name="scale" type="xs:string"/>
     <xs:attribute name="type" type="xs:string"/>
     <xs:attribute name="unique" default="false" type="xs:boolean"/>
     <xs:attribute name="unique-key" type="xs:string"/>
     <xs:attribute name="update" type="xs:boolean"/>
   </xs:complexType>
 
   <!-- The query element declares a named Hibernate query string -->
   <xs:complexType name="query-element" mixed="true">
     <xs:sequence minOccurs="0" maxOccurs="unbounded">
       <xs:element name="query-param" type="query-param-element"/>
     </xs:sequence>
     <xs:attribute name="cache-mode" type="cache-mode-attribute"/>
     <xs:attribute name="cache-region" type="xs:string"/>
     <xs:attribute name="cacheable" default="false" type="xs:boolean"/>
     <xs:attribute name="comment" type="xs:string"/>
     <xs:attribute name="fetch-size" type="xs:string"/>
     <xs:attribute name="flush-mode" type="flush-mode-attribute"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="read-only" type="xs:boolean"/>
     <xs:attribute name="timeout" type="xs:string"/>
   </xs:complexType>
 
   <!-- The query-param element is used only by tools that generate
   finder methods for named queries -->
   <xs:complexType name="query-param-element">
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="type" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!-- The resultset element declares a named resultset mapping definition for SQL queries -->
   <xs:complexType name="resultset-element">
     <xs:choice minOccurs="0" maxOccurs="unbounded">
       <xs:element name="return-scalar" type="return-scalar-element"/>
       <xs:element name="return" type="return-element"/>
       <xs:element name="return-join" type="return-join-element"/>
       <xs:element name="load-collection" type="load-collection-element"/>
     </xs:choice>
     <xs:attribute name="name" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!--
   	Defines a return component for a sql-query.  Alias refers to the alias
   	used in the actual sql query; lock-mode specifies the locking to be applied
   	when the query is executed.  The class, collection, and role attributes are mutually exclusive;
   	class refers to the class name of a "root entity" in the object result; collection refers
   	to a collection of a given class and is used to define custom sql to load that owned collection
   	and takes the form "ClassName.propertyName"; role refers to the property path for an eager fetch
   	and takes the form "owningAlias.propertyName"
   -->
   <xs:complexType name="return-element">
     <xs:sequence minOccurs="0" maxOccurs="unbounded">
       <xs:element name="return-discriminator" minOccurs="0">
         <xs:complexType>
           <xs:attribute name="column" use="required" type="xs:string"/>
         </xs:complexType>
       </xs:element>
       <xs:element name="return-property" type="return-property-element"/>
     </xs:sequence>
     <xs:attribute name="alias" type="xs:string"/>
     <xs:attribute name="class" type="xs:string"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="lock-mode" default="read" type="lock-mode-attribute"/>
   </xs:complexType>
 
   <xs:complexType name="return-join-element">
     <xs:sequence minOccurs="0" maxOccurs="unbounded">
       <xs:element name="return-property" type="return-property-element"/>
     </xs:sequence>
     <xs:attribute name="alias" use="required" type="xs:string"/>
     <xs:attribute name="lock-mode" default="read" type="lock-mode-attribute"/>
     <xs:attribute name="property" use="required" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="return-property-element">
     <xs:sequence>
       <xs:element name="return-column" minOccurs="0" maxOccurs="unbounded">
         <xs:complexType>
           <xs:attribute name="name" use="required" type="xs:string"/>
         </xs:complexType>
       </xs:element>
     </xs:sequence>
     <xs:attribute name="column" type="xs:string"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="return-scalar-element">
     <xs:attribute name="column" use="required" type="xs:string"/>
     <xs:attribute name="type" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="set-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="cache" minOccurs="0" type="cache-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="key" type="key-element"/>
       <xs:choice>
         <xs:element name="element" type="element-element"/>
         <xs:element name="one-to-many" type="one-to-many-element"/>
         <xs:element name="many-to-many" type="many-to-many-element"/>
         <xs:element name="composite-element" type="composite-element-element"/>
         <xs:element name="many-to-any" type="many-to-any-element"/>
       </xs:choice>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="sql-delete-all" minOccurs="0" type="sql-delete-all-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
     </xs:sequence>
     <xs:attribute name="access" type="xs:string"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="cascade" type="xs:string"/>
     <xs:attribute name="catalog" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="check" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="collection-type" type="xs:string"/>
     <xs:attribute name="embed-xml" default="true" type="xs:boolean"/>
     <xs:attribute name="fetch" type="fetch-attribute-with-subselect"/>
     <xs:attribute name="inverse" default="false" type="xs:boolean"/>
     <xs:attribute name="lazy" type="lazy-attribute-with-extra"/>
     <xs:attribute name="mutable" default="true" type="xs:boolean"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="optimistic-lock" default="true" type="xs:boolean"/> <!-- only supported for properties of a class (not component) -->
     <xs:attribute name="order-by" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="outer-join" type="outer-join-attribute"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="schema" type="xs:string"/> <!-- default: none -->
     <xs:attribute name="sort" default="unsorted" type="xs:string"/> <!-- unsorted|natural|"comparator class" -->
     <xs:attribute name="subselect" type="xs:string"/>
     <xs:attribute name="table" type="xs:string"/> <!-- default: name -->
     <xs:attribute name="where" type="xs:string"/> <!-- default: none -->
   </xs:complexType>
 
   <xs:complexType name="sql-delete-all-element" mixed="true">
     <xs:simpleContent>
       <xs:extension base="xs:string">
         <xs:attribute name="callable" default="false" type="xs:boolean"/>
         <xs:attribute name="check" type="check-attribute"/>
       </xs:extension>
     </xs:simpleContent>
   </xs:complexType>
 
   <xs:complexType name="sql-delete-element" mixed="true">
     <xs:simpleContent>
       <xs:extension base="xs:string">
         <xs:attribute name="callable" default="false" type="xs:boolean"/>
         <xs:attribute name="check" type="check-attribute"/>
       </xs:extension>
     </xs:simpleContent>
   </xs:complexType>
 
   <!-- custom sql operations -->
   <xs:complexType name="sql-insert-element" mixed="true">
     <xs:simpleContent>
       <xs:extension base="xs:string">
         <xs:attribute name="callable" default="false" type="xs:boolean"/>
         <xs:attribute name="check" type="check-attribute"/>
       </xs:extension>
     </xs:simpleContent>
   </xs:complexType>
 
   <!-- The sql-query element declares a named SQL query string -->
   <xs:complexType name="sql-query-element" mixed="true">
     <xs:choice minOccurs="0" maxOccurs="unbounded">
       <xs:element name="return-scalar" type="return-scalar-element"/>
       <xs:element name="return" type="return-element"/>
       <xs:element name="return-join" type="return-join-element"/>
       <xs:element name="load-collection" type="load-collection-element"/>
       <xs:element name="synchronize" type="synchronize-element"/>
       <xs:element name="query-param" type="query-param-element"/>
     </xs:choice>
     <xs:attribute name="cache-mode" type="cache-mode-attribute"/>
     <xs:attribute name="cache-region" type="xs:string"/>
     <xs:attribute name="cacheable" default="false" type="xs:boolean"/>
     <xs:attribute name="callable" default="false" type="xs:boolean"/>
     <xs:attribute name="comment" type="xs:string"/>
     <xs:attribute name="fetch-size" type="xs:string"/>
     <xs:attribute name="flush-mode" type="flush-mode-attribute"/>
     <xs:attribute name="name" use="required" type="xs:string"/>
     <xs:attribute name="read-only" type="xs:boolean"/>
     <xs:attribute name="resultset-ref" type="xs:string"/>
     <xs:attribute name="timeout" type="xs:string"/>
   </xs:complexType>
 
   <xs:complexType name="sql-update-element" mixed="true">
     <xs:simpleContent>
       <xs:extension base="xs:string">
         <xs:attribute name="callable" default="false" type="xs:boolean"/>
         <xs:attribute name="check" type="check-attribute"/>
       </xs:extension>
     </xs:simpleContent>
   </xs:complexType>
 
   <!--
   	Subclass declarations are nested beneath the root class declaration to achieve
   	polymorphic persistence with the table-per-hierarchy mapping strategy.
   	See the note on the class element regarding <pojo/> vs. @name usage...
   -->
   <xs:complexType name="subclass-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="one-to-one" type="one-to-one-element"/>
         <xs:element name="component" type="component-element"/>
         <xs:element name="dynamic-component" type="dynamic-component-element"/>
         <xs:element name="any" type="any-element"/>
         <xs:element name="map" type="map-element"/>
         <xs:element name="set" type="set-element"/>
         <xs:element name="list" type="list-element"/>
         <xs:element name="bag" type="bag-element"/>
         <xs:element name="idbag" type="idbag-element"/>
         <xs:element name="array" type="array-element"/>
         <xs:element name="primitive-array" type="primitive-array-element"/>
       </xs:choice>
       <xs:element name="join" minOccurs="0" maxOccurs="unbounded" type="join-element"/>
       <xs:element name="subclass" minOccurs="0" maxOccurs="unbounded" type="subclass-element"/>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
       <xs:element name="fetch-profile" minOccurs="0" maxOccurs="unbounded" type="fetch-profile-element"/>
       <xs:element name="resultset" minOccurs="0" maxOccurs="unbounded" type="resultset-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="query" type="query-element"/>
         <xs:element name="sql-query" type="sql-query-element"/>
       </xs:choice>
     </xs:sequence>
     <xs:attribute name="abstract" type="xs:boolean"/>
     <xs:attribute name="batch-size" type="xs:string"/>
     <xs:attribute name="discriminator-value" type="xs:string"/> <!-- default: unqualified class name | none -->
     <xs:attribute name="dynamic-insert" default="false" type="xs:boolean"/>
     <xs:attribute name="dynamic-update" default="false" type="xs:boolean"/>
     <xs:attribute name="entity-name" type="xs:string"/>
     <xs:attribute name="extends" type="xs:string"/> <!-- default: empty when a toplevel, otherwise the nearest class definition -->
     <xs:attribute name="lazy" type="xs:boolean"/>
     <xs:attribute name="name" type="xs:string"/>
     <xs:attribute name="node" type="xs:string"/>
     <xs:attribute name="persister" type="xs:string"/>
     <xs:attribute name="proxy" type="xs:string"/> <!-- default: no proxy interface -->
     <xs:attribute name="select-before-update" default="false" type="xs:boolean"/>
   </xs:complexType>
 
   <xs:complexType name="synchronize-element">
     <xs:attribute name="table" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!--
       TUPLIZER element; defines tuplizer to use for a component/entity for a given entity-mode
   -->
   <xs:complexType name="tuplizer-element">
     <xs:attribute name="class" use="required" type="xs:string"/> <!-- the tuplizer class to use -->
     <xs:attribute name="entity-mode"> <!-- entity mode for which tuplizer is in effect -->
       <xs:simpleType>
         <xs:restriction base="xs:token">
           <xs:enumeration value="dom4j"/>
           <xs:enumeration value="dynamic-map"/>
           <xs:enumeration value="pojo"/>
         </xs:restriction>
       </xs:simpleType>
     </xs:attribute>
   </xs:complexType>
 
   <!-- Declares the type of the containing property (overrides an eventually existing type
   attribute of the property). May contain param elements to customize a ParametrizableType. -->
   <xs:complexType name="type-element">
     <xs:sequence>
       <xs:element name="param" minOccurs="0" maxOccurs="unbounded" type="param-element"/>
     </xs:sequence>
     <xs:attribute name="name" use="required" type="xs:string"/>
   </xs:complexType>
 
   <!--
   	Union subclasses are used for the table-per-concrete-class mapping strategy
   	See the note on the class element regarding <pojo/> vs. @name usage...
   -->
   <xs:complexType name="union-subclass-element">
     <xs:sequence>
       <xs:element name="meta" minOccurs="0" maxOccurs="unbounded" type="meta-element"/>
       <xs:element name="subselect" minOccurs="0" type="xs:string"/>
       <xs:element name="synchronize" minOccurs="0" maxOccurs="unbounded" type="synchronize-element"/>
       <!-- The comment element allows definition of a database table or column comment. -->
       <xs:element name="comment" minOccurs="0" type="xs:string"/>
       <xs:element name="tuplizer" minOccurs="0" maxOccurs="unbounded" type="tuplizer-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="property" type="property-element"/>
         <xs:element name="many-to-one" type="many-to-one-element"/>
         <xs:element name="one-to-one" type="one-to-one-element"/>
         <xs:element name="component" type="component-element"/>
         <xs:element name="dynamic-component" type="dynamic-component-element"/>
         <xs:element name="properties" type="properties-element"/>
         <xs:element name="any" type="any-element"/>
         <xs:element name="map" type="map-element"/>
         <xs:element name="set" type="set-element"/>
         <xs:element name="list" type="list-element"/>
         <xs:element name="bag" type="bag-element"/>
         <xs:element name="idbag" type="idbag-element"/>
         <xs:element name="array" type="array-element"/>
         <xs:element name="primitive-array" type="primitive-array-element"/>
       </xs:choice>
       <xs:element name="union-subclass" minOccurs="0" maxOccurs="unbounded" type="union-subclass-element"/>
       <xs:element name="loader" minOccurs="0" type="loader-element"/>
       <xs:element name="sql-insert" minOccurs="0" type="sql-insert-element"/>
       <xs:element name="sql-update" minOccurs="0" type="sql-update-element"/>
       <xs:element name="sql-delete" minOccurs="0" type="sql-delete-element"/>
       <xs:element name="filter" minOccurs="0" maxOccurs="unbounded" type="filter-element"/>
       <xs:element name="fetch-profile" minOccurs="0" maxOccurs="unbounded" type="fetch-profile-element"/>
       <xs:element name="resultset" minOccurs="0" maxOccurs="unbounded" type="resultset-element"/>
       <xs:choice minOccurs="0" maxOccurs="unbounded">
         <xs:element name="query" type="query-element"/>
