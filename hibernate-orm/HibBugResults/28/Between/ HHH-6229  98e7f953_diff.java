diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/MetaAttribute.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/MetaAttribute.java
similarity index 82%
rename from hibernate-core/src/main/java/org/hibernate/metamodel/domain/MetaAttribute.java
rename to hibernate-core/src/main/java/org/hibernate/metamodel/binding/MetaAttribute.java
index 9644ac6758..18babda0e3 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/domain/MetaAttribute.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/MetaAttribute.java
@@ -1,72 +1,70 @@
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
-package org.hibernate.metamodel.domain;
+package org.hibernate.metamodel.binding;
 
 import java.io.Serializable;
 import java.util.ArrayList;
 import java.util.Collections;
+import java.util.List;
 
 /**
  * A meta attribute is a named value or values.
  * 
  * @author Gavin King
  */
 public class MetaAttribute implements Serializable {
-
-	// todo : this really belongs in the binding package
-
-	private String name;
-	private java.util.List values = new ArrayList();
+	private final String name;
+	private List<String> values = new ArrayList<String>();
 
 	public MetaAttribute(String name) {
 		this.name = name;
 	}
 	
 	public String getName() {
 		return name;
 	}	
 
-	public java.util.List getValues() {
+	public List<String> getValues() {
 		return Collections.unmodifiableList(values);
 	}
 
 	public void addValue(String value) {
-		values.add(value);
+		values.add( value );
 	}
 
 	public String getValue() {
-		if ( values.size()!=1 ) {
-			throw new IllegalStateException("no unique value");
+		if ( values.size() != 1 ) {
+			throw new IllegalStateException( "no unique value" );
 		}
-		return (String) values.get(0);
+		return values.get( 0 );
 	}
 
 	public boolean isMultiValued() {
 		return values.size()>1;
 	}
 
 	public String toString() {
 		return "[" + name + "=" + values + "]";
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/AttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/AttributeBindingState.java
index 4803da6b8d..d1ab828718 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/AttributeBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/binding/state/AttributeBindingState.java
@@ -1,56 +1,55 @@
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
 
 import org.hibernate.metamodel.binding.CascadeType;
-import org.hibernate.metamodel.domain.MetaAttribute;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
index b20b4b4d09..0ba10e8905 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/AbstractEntityBinder.java
@@ -1,586 +1,581 @@
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
 
-import org.dom4j.Attribute;
-
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
-import org.hibernate.MappingException;
 import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.engine.internal.Versioning;
 import org.hibernate.internal.util.StringHelper;
 import org.hibernate.metamodel.binding.AttributeBinding;
 import org.hibernate.metamodel.binding.BagBinding;
 import org.hibernate.metamodel.binding.CollectionElementType;
 import org.hibernate.metamodel.binding.EntityBinding;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.ManyToOneAttributeBinding;
 import org.hibernate.metamodel.binding.SimpleAttributeBinding;
 import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
 import org.hibernate.metamodel.domain.Entity;
 import org.hibernate.metamodel.domain.Hierarchical;
 import org.hibernate.metamodel.relational.Schema;
 import org.hibernate.metamodel.relational.Table;
 import org.hibernate.metamodel.relational.TableSpecification;
 import org.hibernate.metamodel.relational.UniqueKey;
 import org.hibernate.metamodel.relational.state.ManyToOneRelationalState;
 import org.hibernate.metamodel.source.hbm.state.binding.HbmEntityBindingState;
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
 import org.hibernate.metamodel.binding.state.PluralAttributeBindingState;
 import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.relational.state.TupleRelationalState;
 import org.hibernate.metamodel.relational.state.ValueRelationalState;
-import org.hibernate.metamodel.source.spi.BindingContext;
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
-						? bindingContext.getMappingDefaults().getDefaultSchemaName()
+						? bindingContext.getMappingDefaults().getSchemaName()
 						: entityClazz.getSchema(),
 				entityClazz.getCatalog() == null
-						? bindingContext.getMappingDefaults().getDefaultCatalogName() :
+						? bindingContext.getMappingDefaults().getCatalogName() :
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
 		entityBinding.setEntity( new Entity( bindingContext.extractEntityName( entityClazz ), superType ) );
 		entityBinding.initialize( new HbmEntityBindingState( isRoot(), getInheritanceType(), bindingContext, entityClazz ) );
 
 		// TODO: move this stuff out
 		// transfer an explicitly defined lazy attribute
 		bindPojoRepresentation( entityClazz, entityBinding );
 		bindDom4jRepresentation( entityClazz, entityBinding );
 		bindMapRepresentation( entityClazz, entityBinding );
 
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
-		return bindingContext.getMappingDefaults().getDefaultAccess();
+		return bindingContext.getMappingDefaults().getPropertyAccessorName();
 	}
 
 	private void bindPojoRepresentation(XMLHibernateMapping.XMLClass entityClazz,
 										EntityBinding entityBinding) {
 		String className = bindingContext.getClassName( entityClazz.getName() );
 		String proxyName = entityBinding.getProxyInterfaceName();
 
 		entityBinding.getEntity().getPojoEntitySpecifics().setClassName( className );
 
 		if ( proxyName != null ) {
 			entityBinding.getEntity().getPojoEntitySpecifics().setProxyInterfaceName( proxyName );
 			entityBinding.setLazy( true );
 		}
 		else if ( entityBinding.isLazy() ) {
 			entityBinding.getEntity().getPojoEntitySpecifics().setProxyInterfaceName( className );
 		}
 
 		XMLTuplizerElement tuplizer = locateTuplizerDefinition( entityClazz, EntityMode.POJO );
 		if ( tuplizer != null ) {
 			entityBinding.getEntity().getPojoEntitySpecifics().setTuplizerClassName( tuplizer.getClazz() );
 		}
 	}
 
 	private void bindDom4jRepresentation(XMLHibernateMapping.XMLClass entityClazz,
 										 EntityBinding entityBinding) {
 		String nodeName = entityClazz.getNode();
 		if ( nodeName == null ) {
 			nodeName = StringHelper.unqualify( entityBinding.getEntity().getName() );
 		}
 		entityBinding.getEntity().getDom4jEntitySpecifics().setNodeName( nodeName );
 
 		XMLTuplizerElement tuplizer = locateTuplizerDefinition( entityClazz, EntityMode.DOM4J );
 		if ( tuplizer != null ) {
 			entityBinding.getEntity().getDom4jEntitySpecifics().setTuplizerClassName( tuplizer.getClazz() );
 		}
 	}
 
 	private void bindMapRepresentation(XMLHibernateMapping.XMLClass entityClazz,
 									   EntityBinding entityBinding) {
 		XMLTuplizerElement tuplizer = locateTuplizerDefinition( entityClazz, EntityMode.MAP );
 		if ( tuplizer != null ) {
 			entityBinding.getEntity().getMapEntitySpecifics().setTuplizerClassName( tuplizer.getClazz() );
 		}
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
 				attributeBinding =  makeManyToOneAttributeBinding( manyToOne, entityBinding );
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
 				entityBinding.getEntity().getPojoEntitySpecifics().getClassName(),
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
 
 		entityBinding.getEntity().getOrCreateSingularAttribute( bindingState.getAttributeName() );
 		return entityBinding.makeSimpleAttributeBinding( bindingState.getAttributeName() )
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
 						entityBinding.getEntity().getPojoEntitySpecifics().getClassName(),
 						bindingContext,
 						entityBinding.getMetaAttributeContext(),
 						collection
 				);
 
 		BagBinding collectionBinding = entityBinding.makeBagAttributeBinding(
 				bindingState.getAttributeName(),
 				getCollectionElementType( collection ) )
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
 						entityBinding.getEntity().getPojoEntitySpecifics().getClassName(),
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmHelper.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmHelper.java
index 2c08f931a5..cd3529c850 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/HbmHelper.java
@@ -1,162 +1,162 @@
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
 
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 
 import org.dom4j.Attribute;
 import org.dom4j.Element;
 
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
 import org.hibernate.metamodel.binding.CustomSQL;
-import org.hibernate.metamodel.domain.MetaAttribute;
+import org.hibernate.metamodel.binding.MetaAttribute;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLMetaElement;
 import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
 /**
  * TODO : javadoc
  *
  * @author Steve Ebersole
  */
 public class HbmHelper {
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/AbstractHbmAttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/AbstractHbmAttributeBindingState.java
index 43cd1215c2..5b8cf038bf 100644
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
 import org.hibernate.metamodel.binding.CascadeType;
 import org.hibernate.metamodel.binding.state.AttributeBindingState;
 import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.spi.BindingContext;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
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
-		String commaSeparatedCascades = MappingHelper.getStringValue( cascade, getBindingContext().getMappingDefaults().getDefaultCascade() );
+		String commaSeparatedCascades = MappingHelper.getStringValue( cascade, getBindingContext().getMappingDefaults().getCascadeStyle() );
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
 		Class ownerClass = MappingHelper.classForName( ownerClassName, bindingContext.getServiceRegistry() );
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
index d65b055dfa..a52e9dc256 100644
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
 
 import org.hibernate.metamodel.binding.CascadeType;
 import org.hibernate.metamodel.binding.state.DiscriminatorBindingState;
 import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLDiscriminator;
 import org.hibernate.metamodel.source.spi.BindingContext;
 
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
-				bindingContext.getMappingDefaults().getDefaultDiscriminatorColumnName(),
+				bindingContext.getMappingDefaults().getDiscriminatorColumnName(),
 				bindingContext,
 				null,
 				null,
 				null,
 				true
 		);
 		XMLDiscriminator discriminator = xmlEntityClazz.getDiscriminator();
 		this.discriminatorValue =  MappingHelper.getStringValue(
 					xmlEntityClazz.getDiscriminatorValue(), entityName
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmEntityBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmEntityBindingState.java
index cac132dd6a..e1447f5889 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmEntityBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmEntityBindingState.java
@@ -1,301 +1,301 @@
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
 
 import java.util.ArrayList;
 import java.util.List;
 
 import org.hibernate.MappingException;
 import org.hibernate.cache.spi.access.AccessType;
 import org.hibernate.engine.internal.Versioning;
 import org.hibernate.metamodel.binding.Caching;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.InheritanceType;
 import org.hibernate.metamodel.binding.state.EntityBindingState;
 import org.hibernate.metamodel.source.hbm.HbmBindingContext;
 import org.hibernate.metamodel.source.hbm.HbmHelper;
 import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLCacheElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlDeleteElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlInsertElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlUpdateElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSynchronizeElement;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
 /**
  * @author Gail Badner
  */
 public class HbmEntityBindingState implements EntityBindingState {
 	private final boolean isRoot;
 	private final InheritanceType entityInheritanceType;
 	private final Caching caching;
 	private final MetaAttributeContext metaAttributeContext;
 	private final String proxyInterfaceName;
 
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
 
 	private final Class entityPersisterClass;
 	private final Boolean isAbstract;
 
 	private final CustomSQL customInsert;
 	private final CustomSQL customUpdate;
 	private final CustomSQL customDelete;
 
 	private final List<String> synchronizedTableNames;
 
 	public HbmEntityBindingState(
 			boolean isRoot,
 			InheritanceType inheritanceType,
 			HbmBindingContext bindingContext,
 			XMLHibernateMapping.XMLClass entityClazz) {
 		this.isRoot = isRoot;
 		this.entityInheritanceType = inheritanceType;
 
 		this.caching = createCaching( entityClazz,  bindingContext.extractEntityName( entityClazz ) );
 
 		metaAttributeContext = HbmHelper.extractMetaAttributeContext(
 				entityClazz.getMeta(), true, bindingContext.getMetaAttributeContext()
 		);
 
 		// go ahead and set the lazy here, since pojo.proxy can override it.
 		lazy = MappingHelper.getBooleanValue(
-				entityClazz.isLazy(), bindingContext.getMappingDefaults().isDefaultLazy()
+				entityClazz.isLazy(), bindingContext.getMappingDefaults().areAssociationsLazy()
 		);
 		mutable = entityClazz.isMutable();
 
 		explicitPolymorphism = "explicit".equals( entityClazz.getPolymorphism() );
 		whereFilter = entityClazz.getWhere();
 		rowId = entityClazz.getRowid();
 		proxyInterfaceName = entityClazz.getProxy();
 		dynamicUpdate = entityClazz.isDynamicUpdate();
 		dynamicInsert = entityClazz.isDynamicInsert();
 		batchSize = MappingHelper.getIntValue( entityClazz.getBatchSize(), 0 );
 		selectBeforeUpdate = entityClazz.isSelectBeforeUpdate();
 		optimisticLockMode = getOptimisticLockMode();
 
 		// PERSISTER
 		entityPersisterClass =
 				entityClazz.getPersister() == null ?
 						null :
 						MappingHelper.classForName( entityClazz.getPersister(), bindingContext.getServiceRegistry() );
 
 		// CUSTOM SQL
 		XMLSqlInsertElement sqlInsert = entityClazz.getSqlInsert();
 		if ( sqlInsert != null ) {
 			customInsert = HbmHelper.getCustomSql(
 					sqlInsert.getValue(),
 					sqlInsert.isCallable(),
 					sqlInsert.getCheck().value()
 			);
 		}
 		else {
 			customInsert = null;
 		}
 
 		XMLSqlDeleteElement sqlDelete = entityClazz.getSqlDelete();
 		if ( sqlDelete != null ) {
 			customDelete = HbmHelper.getCustomSql(
 					sqlDelete.getValue(),
 					sqlDelete.isCallable(),
 					sqlDelete.getCheck().value()
 			);
 		}
 		else {
 			customDelete = null;
 		}
 
 		XMLSqlUpdateElement sqlUpdate = entityClazz.getSqlUpdate();
 		if ( sqlUpdate != null ) {
 			customUpdate = HbmHelper.getCustomSql(
 					sqlUpdate.getValue(),
 					sqlUpdate.isCallable(),
 					sqlUpdate.getCheck().value()
 			);
 		}
 		else {
 			customUpdate = null;
 		}
 
 		if ( entityClazz.getSynchronize() != null ) {
 			synchronizedTableNames = new ArrayList<String>( entityClazz.getSynchronize().size() );
 			for ( XMLSynchronizeElement synchronize : entityClazz.getSynchronize() ) {
 				synchronizedTableNames.add( synchronize.getTable() );
 			}
 		}
 		else {
 			synchronizedTableNames = null;
 		}
 		isAbstract = entityClazz.isAbstract();
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
 		return optimisticLockMode;
 	}
 
 	@Override
 	public Class getEntityPersisterClass() {
 		return entityPersisterClass;
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
 	public List<String> getSynchronizedTableNames() {
 		return synchronizedTableNames;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmManyToOneAttributeBindingState.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmManyToOneAttributeBindingState.java
index 7087ff647d..d402c1cf9f 100644
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
 import org.hibernate.metamodel.binding.CascadeType;
 import org.hibernate.metamodel.binding.state.ManyToOneAttributeBindingState;
 import org.hibernate.metamodel.source.hbm.HbmHelper;
 import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLManyToOneElement;
 import org.hibernate.metamodel.source.spi.BindingContext;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
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
-						bindingContext.getMappingDefaults().getDefaultAccess()
+						bindingContext.getMappingDefaults().getPropertyAccessorName()
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
index 4662ad2021..bef4ac5152 100644
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
 import org.hibernate.metamodel.binding.CascadeType;
 import org.hibernate.metamodel.binding.CustomSQL;
 import org.hibernate.metamodel.binding.state.PluralAttributeBindingState;
 import org.hibernate.metamodel.source.hbm.HbmHelper;
 import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLBagElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlDeleteAllElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlDeleteElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlInsertElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSqlUpdateElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLSynchronizeElement;
 import org.hibernate.metamodel.source.spi.BindingContext;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
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
-						collection.getAccess(), collection.isEmbedXml(), bindingContext.getMappingDefaults().getDefaultAccess()
+						collection.getAccess(), collection.isEmbedXml(), bindingContext.getMappingDefaults().getPropertyAccessorName()
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
-						collection.getLazy().value(), getBindingContext().getMappingDefaults().isDefaultLazy()
+						collection.getLazy().value(), getBindingContext().getMappingDefaults().areAssociationsLazy()
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
index 47ec65ee38..c9f66109aa 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmSimpleAttributeBindingState.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/hbm/state/binding/HbmSimpleAttributeBindingState.java
@@ -1,280 +1,280 @@
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
 import org.hibernate.metamodel.binding.CascadeType;
 import org.hibernate.metamodel.binding.state.SimpleAttributeBindingState;
 import org.hibernate.metamodel.source.hbm.HbmHelper;
 import org.hibernate.metamodel.source.hbm.util.MappingHelper;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLId;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLTimestamp;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLHibernateMapping.XMLClass.XMLVersion;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLParamElement;
 import org.hibernate.metamodel.source.hbm.xml.mapping.XMLPropertyElement;
 import org.hibernate.metamodel.source.spi.BindingContext;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 
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
-				id.getName() != null ? id.getName() : bindingContext.getMappingDefaults().getDefaultIdColumnName(),
+				id.getName() != null ? id.getName() : bindingContext.getMappingDefaults().getIdColumnName(),
 				bindingContext,
 				id.getNode(),
 				HbmHelper.extractMetaAttributeContext( id.getMeta(), parentMetaAttributeContext ),
-				HbmHelper.getPropertyAccessorName( id.getAccess(), false, bindingContext.getMappingDefaults().getDefaultAccess() ),
+				HbmHelper.getPropertyAccessorName( id.getAccess(), false, bindingContext.getMappingDefaults().getPropertyAccessorName() ),
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
-				HbmHelper.getPropertyAccessorName( version.getAccess(), false, bindingContext.getMappingDefaults().getDefaultAccess() ),
+				HbmHelper.getPropertyAccessorName( version.getAccess(), false, bindingContext.getMappingDefaults().getPropertyAccessorName() ),
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
-				HbmHelper.getPropertyAccessorName( timestamp.getAccess(), false, bindingContext.getMappingDefaults().getDefaultAccess() ),
+				HbmHelper.getPropertyAccessorName( timestamp.getAccess(), false, bindingContext.getMappingDefaults().getPropertyAccessorName() ),
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
-				HbmHelper.getPropertyAccessorName( property.getAccess(), false, bindingContext.getMappingDefaults().getDefaultAccess() ),
+				HbmHelper.getPropertyAccessorName( property.getAccess(), false, bindingContext.getMappingDefaults().getPropertyAccessorName() ),
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
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
index 29cf6993bf..8a977c560d 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/MetadataImpl.java
@@ -1,526 +1,519 @@
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
-import java.util.Collections;
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
-import org.hibernate.metamodel.domain.MetaAttribute;
 import org.hibernate.metamodel.relational.AuxiliaryDatabaseObject;
 import org.hibernate.metamodel.relational.Database;
 import org.hibernate.metamodel.source.annotations.AnnotationBinder;
 import org.hibernate.metamodel.source.hbm.HbmBinder;
 import org.hibernate.metamodel.source.spi.Binder;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
 import org.hibernate.metamodel.source.spi.MetaAttributeContext;
 import org.hibernate.metamodel.source.spi.MetadataImplementor;
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
 	private ClassLoaderService classLoaderService;
 
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
 
 	// todo : keep as part of Database?
 	private List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjects = new ArrayList<AuxiliaryDatabaseObject>();
 
 	public MetadataImpl(MetadataSources metadataSources, Options options) {
 		this.serviceRegistry = metadataSources.getServiceRegistry();
 		this.options = options;
 
 		this.mappingDefaults = new MappingDefaultsImpl();
 
 		final Binder[] binders;
 		if ( options.getSourceProcessingOrder() == SourceProcessingOrder.HBM_FIRST ) {
 			binders = new Binder[] {
 					new HbmBinder( this ),
 					new AnnotationBinder( this )
 			};
 		}
 		else {
 			binders = new Binder[] {
 					new AnnotationBinder( this ),
 					new HbmBinder( this )
 			};
 		}
 
 		final ArrayList<String> processedEntityNames = new ArrayList<String>();
 
 		prepare( binders, metadataSources );
 		bindIndependentMetadata( binders, metadataSources );
 		bindTypeDependentMetadata( binders, metadataSources );
 		bindMappingMetadata( binders, metadataSources, processedEntityNames );
 		bindMappingDependentMetadata( binders, metadataSources );
 
 		// todo : remove this by coordinated ordering of entity processing
 		new EntityReferenceResolver( this ).resolve();
 	}
 
 	private void prepare(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.prepare( metadataSources );
 		}
 	}
 
 	private void bindIndependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindIndependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindTypeDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindTypeDependentMetadata( metadataSources );
 		}
 	}
 
 	private void bindMappingMetadata(Binder[] binders, MetadataSources metadataSources, List<String> processedEntityNames) {
 		for ( Binder binder : binders ) {
 			binder.bindMappingMetadata( metadataSources, processedEntityNames );
 		}
 	}
 
 	private void bindMappingDependentMetadata(Binder[] binders, MetadataSources metadataSources) {
 		for ( Binder binder : binders ) {
 			binder.bindMappingDependentMetadata( metadataSources );
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
 	public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
 		if ( auxiliaryDatabaseObject == null ) {
 			throw new IllegalArgumentException( "Auxiliary database object is null." );
 		}
 		auxiliaryDatabaseObjects.add( auxiliaryDatabaseObject );
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
 
 	private ClassLoaderService classLoaderService(){
 		if(classLoaderService==null){
 			classLoaderService = serviceRegistry.getService( ClassLoaderService.class );
 		}
 		return classLoaderService;
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
-		public String getDefaultSchemaName() {
+		public String getSchemaName() {
 			return options.getDefaultSchemaName();
 		}
 
 		@Override
-		public String getDefaultCatalogName() {
+		public String getCatalogName() {
 			return options.getDefaultCatalogName();
 		}
 
 		@Override
-		public String getDefaultIdColumnName() {
+		public String getIdColumnName() {
 			return DEFAULT_IDENTIFIER_COLUMN_NAME;
 		}
 
 		@Override
-		public String getDefaultDiscriminatorColumnName() {
+		public String getDiscriminatorColumnName() {
 			return DEFAULT_DISCRIMINATOR_COLUMN_NAME;
 		}
 
 		@Override
-		public String getDefaultCascade() {
+		public String getCascadeStyle() {
 			return DEFAULT_CASCADE;
 		}
 
 		@Override
-		public String getDefaultAccess() {
+		public String getPropertyAccessorName() {
 			return DEFAULT_PROPERTY_ACCESS;
 		}
 
 		@Override
-		public boolean isDefaultLazy() {
+		public boolean areAssociationsLazy() {
 			return true;
 		}
-
-		@Override
-		public Map<String, MetaAttribute> getMappingMetas() {
-			return Collections.emptyMap();
-		}
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/OverriddenMappingDefaults.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/OverriddenMappingDefaults.java
index 14fbabc782..b94256b8ad 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/OverriddenMappingDefaults.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/internal/OverriddenMappingDefaults.java
@@ -1,116 +1,108 @@
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
 
-import java.util.Map;
-
-import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.metamodel.domain.MetaAttribute;
 import org.hibernate.metamodel.source.spi.MappingDefaults;
-import org.hibernate.service.ServiceRegistry;
 
 /**
+ * Represents a "nested level" in the mapping defaults stack.
+ *
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
-	public String getDefaultSchemaName() {
-		return schemaName == null ? overriddenValues.getDefaultSchemaName() : schemaName;
-	}
-
-	@Override
-	public String getDefaultCatalogName() {
-		return catalogName == null ? overriddenValues.getDefaultCatalogName() : catalogName;
+	public String getSchemaName() {
+		return schemaName == null ? overriddenValues.getSchemaName() : schemaName;
 	}
 
 	@Override
-	public String getDefaultIdColumnName() {
-		return idColumnName == null ? overriddenValues.getDefaultIdColumnName() : idColumnName;
+	public String getCatalogName() {
+		return catalogName == null ? overriddenValues.getCatalogName() : catalogName;
 	}
 
 	@Override
-	public String getDefaultDiscriminatorColumnName() {
-		return discriminatorColumnName == null ? overriddenValues.getDefaultDiscriminatorColumnName() : discriminatorColumnName;
+	public String getIdColumnName() {
+		return idColumnName == null ? overriddenValues.getIdColumnName() : idColumnName;
 	}
 
 	@Override
-	public String getDefaultCascade() {
-		return cascade == null ? overriddenValues.getDefaultCascade() : cascade;
+	public String getDiscriminatorColumnName() {
+		return discriminatorColumnName == null ? overriddenValues.getDiscriminatorColumnName() : discriminatorColumnName;
 	}
 
 	@Override
-	public String getDefaultAccess() {
-		return propertyAccess == null ? overriddenValues.getDefaultAccess() : propertyAccess;
+	public String getCascadeStyle() {
+		return cascade == null ? overriddenValues.getCascadeStyle() : cascade;
 	}
 
 	@Override
-	public boolean isDefaultLazy() {
-		return associationLaziness == null ? overriddenValues.isDefaultLazy() : associationLaziness;
+	public String getPropertyAccessorName() {
+		return propertyAccess == null ? overriddenValues.getPropertyAccessorName() : propertyAccess;
 	}
 
 	@Override
-	public Map<String, MetaAttribute> getMappingMetas() {
-		return null; // todo : implement method body
+	public boolean areAssociationsLazy() {
+		return associationLaziness == null ? overriddenValues.areAssociationsLazy() : associationLaziness;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MappingDefaults.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MappingDefaults.java
index d71acbf813..8dd1ebd98f 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MappingDefaults.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MappingDefaults.java
@@ -1,58 +1,92 @@
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
 
-import java.util.Map;
-
-import org.hibernate.cfg.NamingStrategy;
-import org.hibernate.metamodel.domain.MetaAttribute;
-import org.hibernate.service.ServiceRegistry;
-
 /**
+ * Defines a (contextual) set of values to use as defaults in the absence of related mapping information.  The
+ * context here is conceptually a stack.  The "global" level is configuration settings.
+ *
  * @author Gail Badner
  * @author Steve Ebersole
  */
 public interface MappingDefaults {
+	/**
+	 * Identifies the default package name to use if none specified in the mapping.  Really only pertinent for
+	 * {@code hbm.xml} mappings.
+	 *
+	 * @return The default package name.
+	 */
+	public String getPackageName();
 
-	String getPackageName();
-
-	String getDefaultSchemaName();
-
-	String getDefaultCatalogName();
-
-	String getDefaultIdColumnName();
-
-	String getDefaultDiscriminatorColumnName();
+	/**
+	 * Identifies the default database schema name to use if none specified in the mapping.
+	 *
+	 * @return The default schema name
+	 */
+	public String getSchemaName();
 
-	String getDefaultCascade();
+	/**
+	 * Identifies the default database catalog name to use if none specified in the mapping.
+	 *
+	 * @return The default catalog name
+	 */
+	public String getCatalogName();
 
-	String getDefaultAccess();
+	/**
+	 * Identifies the default column name to use for the identifier column if none specified in the mapping.
+	 *
+	 * @return The default identifier column name
+	 */
+	public String getIdColumnName();
 
-	boolean isDefaultLazy();
+	/**
+	 * Identifies the default column name to use for the discriminator column if none specified in the mapping.
+	 *
+	 * @return The default discriminator column name
+	 */
+	public String getDiscriminatorColumnName();
 
+	/**
+	 * Identifies the default cascade style to apply to associations if none specified in the mapping.
+	 *
+	 * @return The default cascade style
+	 */
+	public String getCascadeStyle();
 
-	// Not happy about these here ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
+	/**
+	 * Identifies the default {@link org.hibernate.property.PropertyAccessor} name to use if none specified in the
+	 * mapping.
+	 *
+	 * @return The default property accessor name
+	 * @see org.hibernate.property.PropertyAccessorFactory
+	 */
+	public String getPropertyAccessorName();
 
-	Map<String, MetaAttribute> getMappingMetas();
+	/**
+	 * Identifies whether associations are lazy by default if not specified in the mapping.
+	 *
+	 * @return The default association laziness
+	 */
+	public boolean areAssociationsLazy();
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetaAttributeContext.java b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetaAttributeContext.java
index decafeeb24..c7d744fa5a 100644
--- a/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetaAttributeContext.java
+++ b/hibernate-core/src/main/java/org/hibernate/metamodel/source/spi/MetaAttributeContext.java
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
 package org.hibernate.metamodel.source.spi;
 
 import java.util.HashSet;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 
-import org.hibernate.metamodel.domain.MetaAttribute;
+import org.hibernate.metamodel.binding.MetaAttribute;
 
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
