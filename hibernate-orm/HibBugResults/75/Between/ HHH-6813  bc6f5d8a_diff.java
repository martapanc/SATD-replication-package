diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java b/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java
index 03aafe41db..962c868c21 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/BinderHelper.java
@@ -1,847 +1,848 @@
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
 			propertyNameBuffer.append( "_" ).append( columns[0].getPropertyName().replace( '.', '_' ) );
 			String syntheticPropertyName = propertyNameBuffer.toString();
 			//find properties associated to a certain column
 			Object columnOwner = findColumnOwner( ownerEntity, columns[0].getReferencedColumn(), mappings );
 			List<Property> properties = findPropertiesByColumns( columnOwner, columns, mappings );
 			//create an embeddable component
                         Property synthProp = null;
 			if ( properties != null ) {
                         //todo how about properties.size() == 1, this should be much simpler
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
                                 }
                                     synthProp = new SyntheticProperty();
 				synthProp.setName( syntheticPropertyName );
 				synthProp.setNodeName( syntheticPropertyName );
 				synthProp.setPersistentClass( ownerEntity );
 				synthProp.setUpdateable( false );
 				synthProp.setInsertable( false );
 				synthProp.setValue( embeddedComp );
 				synthProp.setPropertyAccessorName( "embedded" );
 				ownerEntity.addProperty( synthProp );
                                 //make it unique
 				TableBinder.createUniqueConstraint( embeddedComp );
                             }
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
+				( (ToOne) value ).setReferenceToPrimaryKey( syntheticPropertyName == null );
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
 }
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
index c721a072e7..0cea5956ae 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/HbmBinder.java
@@ -632,2099 +632,2101 @@ public final class HbmBinder {
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
 		entity.setOptimisticLockStyle( getOptimisticLockStyle( olNode ) );
 
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
 		        unionSubclass.isAbstract() != null && unionSubclass.isAbstract(),
 				getSubselect( node ),
 				denormalizedSuperTable
 			);
 		unionSubclass.setTable( mytable );
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping union-subclass: %s -> %s", unionSubclass.getEntityName(), unionSubclass.getTable().getName() );
 		}
 
 		createClassProperties( node, unionSubclass, mappings, inheritedMetas );
 
 	}
 
 	public static void bindSubclass(Element node, Subclass subclass, Mappings mappings,
 			java.util.Map inheritedMetas) throws MappingException {
 
 		bindClass( node, subclass, mappings, inheritedMetas );
 		inheritedMetas = getMetas( node, inheritedMetas, true ); // get meta's from <subclass>
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping subclass: %s -> %s", subclass.getEntityName(), subclass.getTable().getName() );
 		}
 
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
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping joined-subclass: %s -> %s", joinedSubclass.getEntityName(), joinedSubclass.getTable().getName() );
 		}
 
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
 
 		if ( LOG.isDebugEnabled() ) {
 			LOG.debugf( "Mapping class join: %s -> %s", persistentClass.getEntityName(), join.getTable().getName() );
 		}
 
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
+		manyToOne.setReferenceToPrimaryKey( manyToOne.getReferencedPropertyName() == null );
 
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
+		oneToOne.setReferenceToPrimaryKey( oneToOne.getReferencedPropertyName() == null );
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java b/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
index a922034d74..f99d3d64a7 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/OneToOneSecondPass.java
@@ -1,303 +1,308 @@
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
 import java.util.Map;
 
 import org.hibernate.AnnotationException;
 import org.hibernate.MappingException;
 import org.hibernate.annotations.ForeignKey;
 import org.hibernate.annotations.common.reflection.XClass;
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
+import org.hibernate.mapping.Selectable;
 import org.hibernate.mapping.SimpleValue;
 import org.hibernate.type.ForeignKeyDirection;
 
 /**
  * We have to handle OneToOne in a second pass because:
  * -
  */
 public class OneToOneSecondPass implements SecondPass {
 	private String mappedBy;
 	private Mappings mappings;
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
 			Mappings mappings) {
 		this.ownerEntity = ownerEntity;
 		this.ownerProperty = ownerProperty;
 		this.mappedBy = mappedBy;
 		this.propertyHolder = propertyHolder;
 		this.mappings = mappings;
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
 				mappings, propertyHolder.getTable(), propertyHolder.getPersistentClass()
 		);
 		final String propertyName = inferredData.getPropertyName();
 		value.setPropertyName( propertyName );
 		String referencedEntityName = ToOneBinder.getReferenceEntityName(inferredData, targetEntity, mappings);
 		value.setReferencedEntityName( referencedEntityName );  
 		AnnotationBinder.defineFetchingStrategy( value, inferredData.getProperty() );
 		//value.setFetchMode( fetchMode );
 		value.setCascadeDeleteEnabled( cascadeOnDelete );
 		//value.setLazy( fetchMode != FetchMode.JOIN );
 
 		if ( !optional ) value.setConstrained( true );
 		value.setForeignKeyType(
 				value.isConstrained() ?
 						ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT :
 						ForeignKeyDirection.FOREIGN_KEY_TO_PARENT
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
 				( new ToOneFkSecondPass(
 						value, joinColumns,
 						!optional, //cannot have nullabe and unique on certain DBs
 						propertyHolder.getEntityOwnerClassName(),
 						path, mappings
 				) ).doSecondPass( persistentClasses );
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
 					ManyToOne manyToOne = new ManyToOne( mappings, mappedByJoin.getTable() );
 					//FIXME use ignore not found here
 					manyToOne.setIgnoreNotFound( ignoreNotFound );
 					manyToOne.setCascadeDeleteEnabled( value.isCascadeDeleteEnabled() );
 					manyToOne.setEmbedded( value.isEmbedded() );
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
+				
+				value.setReferencedPropertyName( mappedBy );
 
 				// HHH-6813
-				// If otherSide's id is derived, do not set EntityType#uniqueKeyPropertyName.
-				// EntityType#isReferenceToPrimaryKey() assumes that, if it's set,
-				// a PK is not referenced.  Example:
-				//
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
-				String referencedPropertyName = referencesDerivedId ? null : mappedBy;
-				value.setReferencedPropertyName( referencedPropertyName );
+				boolean referenceToPrimaryKey  = referencesDerivedId || mappedBy == null;
+				value.setReferenceToPrimaryKey( referenceToPrimaryKey );
+				
+				// If the other side is a derived ID, prevent an infinite
+				// loop of attempts to resolve identifiers.
+				if ( referencesDerivedId ) {
+					( (ManyToOne) otherSideProperty.getValue() ).setReferenceToPrimaryKey( false );
+				}
 
 				String propertyRef = value.getReferencedPropertyName();
 				if ( propertyRef != null ) {
 					mappings.addUniquePropertyReference(
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
 		ForeignKey fk = inferredData.getProperty().getAnnotation( ForeignKey.class );
 		String fkName = fk != null ? fk.name() : "";
 		if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) value.setForeignKeyName( fkName );
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
 		SimpleValue key = new DependantValue( mappings, join.getTable(), persistentClass.getIdentifier() );
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
index b087052506..358a72893c 100644
--- a/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
+++ b/hibernate-core/src/main/java/org/hibernate/cfg/annotations/CollectionBinder.java
@@ -487,1044 +487,1045 @@ public abstract class CollectionBinder {
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
 
 	private void applySortingAndOrdering(Collection collection) {
 		boolean isSorted = isSortedCollection;
 
 		boolean hadOrderBy = false;
 		boolean hadExplicitSort = false;
 
 		Class<? extends Comparator> comparatorClass = null;
 
 		if ( jpaOrderBy == null && sqlOrderBy == null ) {
 			if ( deprecatedSort != null ) {
 				LOG.debug( "Encountered deprecated @Sort annotation; use @SortNatural or @SortComparator instead." );
 				if ( naturalSort != null || comparatorSort != null ) {
 					throw buildIllegalSortCombination();
 				}
 				hadExplicitSort = deprecatedSort.type() != SortType.UNSORTED;
 				if ( deprecatedSort.type() == SortType.NATURAL ) {
 					isSorted = true;
 				}
 				else if ( deprecatedSort.type() == SortType.COMPARATOR ) {
 					isSorted = true;
 					comparatorClass = deprecatedSort.comparator();
 				}
 			}
 			else if ( naturalSort != null ) {
 				if ( comparatorSort != null ) {
 					throw buildIllegalSortCombination();
 				}
 				hadExplicitSort = true;
 			}
 			else if ( comparatorSort != null ) {
 				hadExplicitSort = true;
 				comparatorClass = comparatorSort.value();
 			}
 		}
 		else {
 			if ( jpaOrderBy != null && sqlOrderBy != null ) {
 				throw new AnnotationException(
 						String.format(
 								"Illegal combination of @%s and @%s on %s",
 								javax.persistence.OrderBy.class.getName(),
 								OrderBy.class.getName(),
 								safeCollectionRole()
 						)
 				);
 			}
 
 			hadOrderBy = true;
 			hadExplicitSort = false;
 
 			// we can only apply the sql-based order by up front.  The jpa order by has to wait for second pass
 			if ( sqlOrderBy != null ) {
 				collection.setOrderBy( sqlOrderBy.clause() );
 			}
 		}
 
 		if ( isSortedCollection ) {
 			if ( ! hadExplicitSort && !hadOrderBy ) {
 				throw new AnnotationException(
 						"A sorted collection must define and ordering or sorting : " + safeCollectionRole()
 				);
 			}
 		}
 
 		collection.setSorted( isSortedCollection || hadExplicitSort );
 
 		if ( comparatorClass != null ) {
 			try {
 				collection.setComparator( comparatorClass.newInstance() );
 			}
 			catch (Exception e) {
 				throw new AnnotationException(
 						String.format(
 								"Could not instantiate comparator class [%s] for %s",
 								comparatorClass.getName(),
 								safeCollectionRole()
 						)
 				);
 			}
 		}
 	}
 
 	private AnnotationException buildIllegalSortCombination() {
 		return new AnnotationException(
 				String.format(
 						"Illegal combination of annotations on %s.  Only one of @%s, @%s and @%s can be used",
 						safeCollectionRole(),
 						Sort.class.getName(),
 						SortNatural.class.getName(),
 						SortComparator.class.getName()
 				)
 		);
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
 					ignoreNotFound,
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
 					associationTableBinder,
 					property,
 					propertyHolder,
 					mappings
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
 			Mappings mappings,
 			Map<XClass, InheritanceState> inheritanceStatePerClass) {
 
 		final boolean debugEnabled = LOG.isDebugEnabled();
 		if ( debugEnabled ) {
 			LOG.debugf( "Binding a OneToMany: %s.%s through a foreign key", propertyHolder.getEntityName(), propertyName );
 		}
 		org.hibernate.mapping.OneToMany oneToMany = new org.hibernate.mapping.OneToMany( mappings, collection.getOwner() );
 		collection.setElement( oneToMany );
 		oneToMany.setReferencedEntityName( collectionType.getName() );
 		oneToMany.setIgnoreNotFound( ignoreNotFound );
 
 		String assocClass = oneToMany.getReferencedEntityName();
 		PersistentClass associatedClass = (PersistentClass) persistentClasses.get( assocClass );
 		if ( jpaOrderBy != null ) {
 			final String jpaOrderByFragment = jpaOrderBy.value();
 			if ( StringHelper.isNotEmpty( jpaOrderByFragment ) ) {
 				final String orderByFragment = buildOrderByClauseFromHql(
 						jpaOrderBy.value(),
 						associatedClass,
 						collection.getRole()
 				);
 				if ( StringHelper.isNotEmpty( orderByFragment ) ) {
 					collection.setOrderBy( orderByFragment );
 				}
 			}
 		}
 
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
 		if ( debugEnabled ) {
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
 			prop.setName( '_' + fkJoinColumns[0].getPropertyName() + '_' + fkJoinColumns[0].getLogicalColumnName() + "Backref" );
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
 			Mappings mappings) throws MappingException {
 		if ( property == null ) {
 			throw new IllegalArgumentException( "null was passed for argument property" );
 		}
 
 		final PersistentClass collectionEntity = (PersistentClass) persistentClasses.get( collType.getName() );
 		final String hqlOrderBy = extractHqlOrderBy( jpaOrderBy );
 
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
 			// as per 11.1.38 of JPA 2.0 spec, default to primary key if no column is specified by @OrderBy.
 			if ( hqlOrderBy != null ) {
 				collValue.setManyToManyOrdering(
 						buildOrderByClauseFromHql( hqlOrderBy, collectionEntity, collValue.getRole() )
 				);
 			}
 			final ForeignKey fk = property.getAnnotation( ForeignKey.class );
 			String fkName = fk != null ? fk.inverseName() : "";
 			if ( !BinderHelper.isEmptyAnnotationValue( fkName ) ) {
 				element.setForeignKeyName( fkName );
 			}
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
 				elementBinder.setType( property, elementClass, collValue.getOwnerEntityName() );
 				elementBinder.setPersistentClassName( propertyHolder.getEntityName() );
 				elementBinder.setAccessType( accessType );
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
 
 	private String extractHqlOrderBy(javax.persistence.OrderBy jpaOrderBy) {
 		if ( jpaOrderBy != null ) {
 			return jpaOrderBy.value(); // Null not possible. In case of empty expression, apply default ordering.
 		}
 		return null; // @OrderBy not found.
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
+			( (ManyToOne) value ).setReferenceToPrimaryKey( referencedPropertyName == null );
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
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/ManyToOne.java b/hibernate-core/src/main/java/org/hibernate/mapping/ManyToOne.java
index 9736b5bf51..49cf99a548 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/ManyToOne.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/ManyToOne.java
@@ -1,118 +1,119 @@
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
 import java.util.Iterator;
 import java.util.Map;
 
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * A many-to-one association mapping
  * @author Gavin King
  */
 public class ManyToOne extends ToOne {
 	private boolean ignoreNotFound;
 	private boolean isLogicalOneToOne;
 	
 	public ManyToOne(Mappings mappings, Table table) {
 		super( mappings, table );
 	}
 
 	public Type getType() throws MappingException {
 		return getMappings().getTypeResolver().getTypeFactory().manyToOne(
-				getReferencedEntityName(), 
+				getReferencedEntityName(),
+				referenceToPrimaryKey, 
 				getReferencedPropertyName(),
 				isLazy(),
 				isUnwrapProxy(),
 				isIgnoreNotFound(),
 				isLogicalOneToOne
 		);
 	}
 
 	public void createForeignKey() throws MappingException {
 		// the case of a foreign key to something other than the pk is handled in createPropertyRefConstraints
 		if (referencedPropertyName==null && !hasFormula() ) {
 			createForeignKeyOfEntity( ( (EntityType) getType() ).getAssociatedEntityName() );
 		} 
 	}
 
 	public void createPropertyRefConstraints(Map persistentClasses) {
 		if (referencedPropertyName!=null) {
 			PersistentClass pc = (PersistentClass) persistentClasses.get(getReferencedEntityName() );
 			
 			Property property = pc.getReferencedProperty( getReferencedPropertyName() );
 			
 			if (property==null) {
 				throw new MappingException(
 						"Could not find property " + 
 						getReferencedPropertyName() + 
 						" on " + 
 						getReferencedEntityName() 
 					);
 			} 
 			else {
 				if ( !hasFormula() && !"none".equals( getForeignKeyName() ) ) {
 					java.util.List refColumns = new ArrayList();
 					Iterator iter = property.getColumnIterator();
 					while ( iter.hasNext() ) {
 						Column col = (Column) iter.next();
 						refColumns.add( col );							
 					}
 					
 					ForeignKey fk = getTable().createForeignKey( 
 							getForeignKeyName(), 
 							getConstraintColumns(), 
 							( (EntityType) getType() ).getAssociatedEntityName(), 
 							refColumns 
 						);
 					fk.setCascadeDeleteEnabled(isCascadeDeleteEnabled() );
 				}
 			}
 		}
 	}
 	
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 
 	public boolean isIgnoreNotFound() {
 		return ignoreNotFound;
 	}
 
 	public void setIgnoreNotFound(boolean ignoreNotFound) {
 		this.ignoreNotFound = ignoreNotFound;
 	}
 
 	public void markAsLogicalOneToOne() {
 		this.isLogicalOneToOne = true;
 	}
 
 	public boolean isLogicalOneToOne() {
 		return isLogicalOneToOne;
 	}
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java b/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
index 18b45cded1..9d15fb7381 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/OneToMany.java
@@ -1,179 +1,180 @@
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
 import java.util.Iterator;
 
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.Type;
 
 /**
  * A mapping for a one-to-many association
  * @author Gavin King
  */
 public class OneToMany implements Value {
 
 	private final Mappings mappings;
 	private final Table referencingTable;
 
 	private String referencedEntityName;
 	private PersistentClass associatedClass;
 	private boolean embedded;
 	private boolean ignoreNotFound;
 
 	private EntityType getEntityType() {
 		return mappings.getTypeResolver().getTypeFactory().manyToOne(
-				getReferencedEntityName(), 
+				getReferencedEntityName(),
+				true, 
 				null, 
 				false,
 				false,
 				isIgnoreNotFound(),
 				false
 			);
 	}
 
 	public OneToMany(Mappings mappings, PersistentClass owner) throws MappingException {
 		this.mappings = mappings;
 		this.referencingTable = (owner==null) ? null : owner.getTable();
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
 		if (referencedEntityName==null) {
 			throw new MappingException("one to many association must specify the referenced entity");
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
 		this.referencedEntityName = referencedEntityName==null ? null : referencedEntityName.intern();
 	}
 
 	public void setTypeUsingReflection(String className, String propertyName) {}
 	
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 	
 	public boolean[] getColumnInsertability() {
 		//TODO: we could just return all false...
 		throw new UnsupportedOperationException();
 	}
 	
 	public boolean[] getColumnUpdateability() {
 		//TODO: we could just return all false...
 		throw new UnsupportedOperationException();
 	}
 
 	/**
 	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public boolean isEmbedded() {
 		return embedded;
 	}
 
 	/**
 	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
 	public boolean isIgnoreNotFound() {
 		return ignoreNotFound;
 	}
 
 	public void setIgnoreNotFound(boolean ignoreNotFound) {
 		this.ignoreNotFound = ignoreNotFound;
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/OneToOne.java b/hibernate-core/src/main/java/org/hibernate/mapping/OneToOne.java
index ca8e196db6..596cfe4ebf 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/OneToOne.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/OneToOne.java
@@ -1,162 +1,164 @@
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
 import java.util.Iterator;
 
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Mappings;
 import org.hibernate.type.EntityType;
 import org.hibernate.type.ForeignKeyDirection;
 import org.hibernate.type.Type;
 
 /**
  * A one-to-one association mapping
  * @author Gavin King
  */
 public class OneToOne extends ToOne {
 
 	private boolean constrained;
 	private ForeignKeyDirection foreignKeyType;
 	private KeyValue identifier;
 	private String propertyName;
 	private String entityName;
 
 	public OneToOne(Mappings mappings, Table table, PersistentClass owner) throws MappingException {
 		super( mappings, table );
 		this.identifier = owner.getKey();
 		this.entityName = owner.getEntityName();
 	}
 
 	public String getPropertyName() {
 		return propertyName;
 	}
 
 	public void setPropertyName(String propertyName) {
 		this.propertyName = propertyName==null ? null : propertyName.intern();
 	}
 	
 	public String getEntityName() {
 		return entityName;
 	}
 
 	public void setEntityName(String propertyName) {
 		this.entityName = entityName==null ? null : entityName.intern();
 	}
 	
 	public Type getType() throws MappingException {
 		if ( getColumnIterator().hasNext() ) {
 			return getMappings().getTypeResolver().getTypeFactory().specialOneToOne(
 					getReferencedEntityName(), 
-					foreignKeyType, 
+					foreignKeyType,
+					referenceToPrimaryKey, 
 					referencedPropertyName,
 					isLazy(),
 					isUnwrapProxy(),
 					entityName,
 					propertyName
 			);
 		}
 		else {
 			return getMappings().getTypeResolver().getTypeFactory().oneToOne(
 					getReferencedEntityName(), 
-					foreignKeyType, 
+					foreignKeyType,
+					referenceToPrimaryKey, 
 					referencedPropertyName,
 					isLazy(),
 					isUnwrapProxy(),
 					entityName,
 					propertyName
 			);
 		}
 	}
 
 	public void createForeignKey() throws MappingException {
 		if ( constrained && referencedPropertyName==null) {
 			//TODO: handle the case of a foreign key to something other than the pk
 			createForeignKeyOfEntity( ( (EntityType) getType() ).getAssociatedEntityName() );
 		}
 	}
 
 	public java.util.List getConstraintColumns() {
 		ArrayList list = new ArrayList();
 		Iterator iter = identifier.getColumnIterator();
 		while ( iter.hasNext() ) list.add( iter.next() );
 		return list;
 	}
 	/**
 	 * Returns the constrained.
 	 * @return boolean
 	 */
 	public boolean isConstrained() {
 		return constrained;
 	}
 
 	/**
 	 * Returns the foreignKeyType.
 	 * @return AssociationType.ForeignKeyType
 	 */
 	public ForeignKeyDirection getForeignKeyType() {
 		return foreignKeyType;
 	}
 
 	/**
 	 * Returns the identifier.
 	 * @return Value
 	 */
 	public KeyValue getIdentifier() {
 		return identifier;
 	}
 
 	/**
 	 * Sets the constrained.
 	 * @param constrained The constrained to set
 	 */
 	public void setConstrained(boolean constrained) {
 		this.constrained = constrained;
 	}
 
 	/**
 	 * Sets the foreignKeyType.
 	 * @param foreignKeyType The foreignKeyType to set
 	 */
 	public void setForeignKeyType(ForeignKeyDirection foreignKeyType) {
 		this.foreignKeyType = foreignKeyType;
 	}
 
 	/**
 	 * Sets the identifier.
 	 * @param identifier The identifier to set
 	 */
 	public void setIdentifier(KeyValue identifier) {
 		this.identifier = identifier;
 	}
 
 	public boolean isNullable() {
 		return !constrained;
 	}
 
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java b/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
index 9a6ab734f8..c6150ac3ce 100644
--- a/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
+++ b/hibernate-core/src/main/java/org/hibernate/mapping/ToOne.java
@@ -1,133 +1,142 @@
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
 import org.hibernate.FetchMode;
 import org.hibernate.MappingException;
 import org.hibernate.cfg.Mappings;
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
+	protected boolean referenceToPrimaryKey = true;
 
 	protected ToOne(Mappings mappings, Table table) {
 		super( mappings, table );
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
 
 	public void setTypeUsingReflection(String className, String propertyName)
 	throws MappingException {
 		if (referencedEntityName==null) {
 			referencedEntityName = ReflectHelper.reflectedPropertyClass( className, propertyName ).getName();
 		}
 	}
 
 	public boolean isTypeSpecified() {
 		return referencedEntityName!=null;
 	}
 	
 	public Object accept(ValueVisitor visitor) {
 		return visitor.accept(this);
 	}
 
 	/**
 	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public boolean isEmbedded() {
 		return embedded;
 	}
 
 	/**
 	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public void setEmbedded(boolean embedded) {
 		this.embedded = embedded;
 	}
 
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
+
+	public boolean isReferenceToPrimaryKey() {
+		return referenceToPrimaryKey;
+	}
+
+	public void setReferenceToPrimaryKey(boolean referenceToPrimaryKey) {
+		this.referenceToPrimaryKey = referenceToPrimaryKey;
+	}
 	
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
old mode 100755
new mode 100644
index 4ee8ed7744..f6ae0946cf
--- a/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/EntityType.java
@@ -1,704 +1,726 @@
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
 package org.hibernate.type;
 
+import java.io.Serializable;
+import java.sql.ResultSet;
+import java.sql.SQLException;
+import java.util.Map;
+
 import org.dom4j.Element;
 import org.dom4j.Node;
 import org.hibernate.AssertionFailure;
 import org.hibernate.EntityMode;
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.internal.ForeignKeys;
-import org.hibernate.engine.spi.*;
+import org.hibernate.engine.spi.EntityUniqueKey;
+import org.hibernate.engine.spi.Mapping;
+import org.hibernate.engine.spi.PersistenceContext;
+import org.hibernate.engine.spi.SessionFactoryImplementor;
+import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.ReflectHelper;
 import org.hibernate.persister.entity.EntityPersister;
 import org.hibernate.persister.entity.Joinable;
 import org.hibernate.persister.entity.UniqueKeyLoadable;
 import org.hibernate.proxy.HibernateProxy;
 import org.hibernate.tuple.ElementWrapper;
 
-import java.io.Serializable;
-import java.sql.ResultSet;
-import java.sql.SQLException;
-import java.util.Map;
-
 /**
  * Base for types which map associations to persistent entities.
  *
  * @author Gavin King
  */
 public abstract class EntityType extends AbstractType implements AssociationType {
 
 	private final TypeFactory.TypeScope scope;
 	private final String associatedEntityName;
 	protected final String uniqueKeyPropertyName;
 	protected final boolean isEmbeddedInXML;
 	private final boolean eager;
 	private final boolean unwrapProxy;
+	private final boolean referenceToPrimaryKey;
 
 	private transient Class returnedClass;
 
 	/**
 	 * Constructs the requested entity type mapping.
 	 *
 	 * @param scope The type scope
 	 * @param entityName The name of the associated entity.
 	 * @param uniqueKeyPropertyName The property-ref name, or null if we
 	 * reference the PK of the associated entity.
 	 * @param eager Is eager fetching enabled.
 	 * @param isEmbeddedInXML Should values of this mapping be embedded in XML modes?
 	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
 	 * says to return the "implementation target" of lazy prooxies; typically only possible
 	 * with lazy="no-proxy".
 	 *
-	 * @deprecated Use {@link #EntityType(TypeFactory.TypeScope, String, String, boolean, boolean )} instead.
+	 * @deprecated Use {@link #EntityType(org.hibernate.type.TypeFactory.TypeScope, String, boolean, String, boolean, boolean)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	protected EntityType(
 			TypeFactory.TypeScope scope,
 			String entityName,
 			String uniqueKeyPropertyName,
 			boolean eager,
 			boolean isEmbeddedInXML,
 			boolean unwrapProxy) {
-		this.scope = scope;
-		this.associatedEntityName = entityName;
-		this.uniqueKeyPropertyName = uniqueKeyPropertyName;
-		this.isEmbeddedInXML = isEmbeddedInXML;
-		this.eager = eager;
-		this.unwrapProxy = unwrapProxy;
+		this( scope, entityName, uniqueKeyPropertyName == null, uniqueKeyPropertyName, eager, unwrapProxy );
 	}
 
 	/**
 	 * Constructs the requested entity type mapping.
 	 *
 	 * @param scope The type scope
 	 * @param entityName The name of the associated entity.
 	 * @param uniqueKeyPropertyName The property-ref name, or null if we
 	 * reference the PK of the associated entity.
 	 * @param eager Is eager fetching enabled.
 	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
 	 * says to return the "implementation target" of lazy prooxies; typically only possible
 	 * with lazy="no-proxy".
+	 * 
+	 * @deprecated Use {@link #EntityType(org.hibernate.type.TypeFactory.TypeScope, String, boolean, String, boolean, boolean)} instead.
 	 */
+	@Deprecated
 	protected EntityType(
 			TypeFactory.TypeScope scope,
 			String entityName,
 			String uniqueKeyPropertyName,
 			boolean eager,
 			boolean unwrapProxy) {
+		this( scope, entityName, uniqueKeyPropertyName == null, uniqueKeyPropertyName, eager, unwrapProxy );
+	}
+
+	/**
+	 * Constructs the requested entity type mapping.
+	 *
+	 * @param scope The type scope
+	 * @param entityName The name of the associated entity.
+	 * @param referenceToPrimaryKey True if association references a primary key.
+	 * @param uniqueKeyPropertyName The property-ref name, or null if we
+	 * reference the PK of the associated entity.
+	 * @param eager Is eager fetching enabled.
+	 * @param unwrapProxy Is unwrapping of proxies allowed for this association; unwrapping
+	 * says to return the "implementation target" of lazy prooxies; typically only possible
+	 * with lazy="no-proxy".
+	 */
+	protected EntityType(
+			TypeFactory.TypeScope scope,
+			String entityName,
+			boolean referenceToPrimaryKey,
+			String uniqueKeyPropertyName,
+			boolean eager,
+			boolean unwrapProxy) {
 		this.scope = scope;
 		this.associatedEntityName = entityName;
 		this.uniqueKeyPropertyName = uniqueKeyPropertyName;
 		this.isEmbeddedInXML = true;
 		this.eager = eager;
 		this.unwrapProxy = unwrapProxy;
+		this.referenceToPrimaryKey = referenceToPrimaryKey;
 	}
 
 	protected TypeFactory.TypeScope scope() {
 		return scope;
 	}
 
 	/**
 	 * An entity type is a type of association type
 	 *
 	 * @return True.
 	 */
 	public boolean isAssociationType() {
 		return true;
 	}
 
 	/**
 	 * Explicitly, an entity type is an entity type ;)
 	 *
 	 * @return True.
 	 */
 	public final boolean isEntityType() {
 		return true;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isMutable() {
 		return false;
 	}
 
 	/**
 	 * Generates a string representation of this type.
 	 *
 	 * @return string rep
 	 */
 	public String toString() {
 		return getClass().getName() + '(' + getAssociatedEntityName() + ')';
 	}
 
 	/**
 	 * For entity types, the name correlates to the associated entity name.
 	 */
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
-		return uniqueKeyPropertyName==null;
+		return referenceToPrimaryKey;
 	}
 
 	public String getRHSUniqueKeyPropertyName() {
 		return uniqueKeyPropertyName;
 	}
 
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
 	 * @return The associated entity name.
 	 */
 	public String getAssociatedEntityName(SessionFactoryImplementor factory) {
 		return getAssociatedEntityName();
 	}
 
 	/**
 	 * Retrieves the {@link Joinable} defining the associated entity.
 	 *
 	 * @param factory The session factory.
 	 * @return The associated joinable
 	 * @throws MappingException Generally indicates an invalid entity name.
 	 */
 	public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) throws MappingException {
 		return ( Joinable ) factory.getEntityPersister( associatedEntityName );
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
 	public final Class getReturnedClass() {
 		if ( returnedClass == null ) {
 			returnedClass = determineAssociatedEntityClass();
 		}
 		return returnedClass;
 	}
 
     private Class determineAssociatedEntityClass() {
         final String entityName = getAssociatedEntityName();
         try {
             return ReflectHelper.classForName(entityName);
         }
         catch ( ClassNotFoundException cnfe ) {
             return this.scope.resolveFactory().getEntityPersister(entityName).
                 getEntityTuplizer().getMappedClass();
         }
     }
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object nullSafeGet(ResultSet rs, String name, SessionImplementor session, Object owner)
 	throws HibernateException, SQLException {
 		return nullSafeGet( rs, new String[] {name}, session, owner );
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public final Object nullSafeGet(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		return resolve( hydrate(rs, names, session, owner), session, owner );
 	}
 
 	/**
 	 * Two entities are considered the same when their instances are the same.
 	 *
 	 *
 	 * @param x One entity instance
 	 * @param y Another entity instance
 	 * @return True if x == y; false otherwise.
 	 */
 	public final boolean isSame(Object x, Object y) {
 		return x == y;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public int compare(Object x, Object y) {
 		return 0; //TODO: entities CAN be compared, by PK, fix this! -> only if/when we can extract the id values....
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object deepCopy(Object value, SessionFactoryImplementor factory) {
 		return value; //special case ... this is the leaf of the containment graph, even though not immutable
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object replace(
 			Object original,
 			Object target,
 			SessionImplementor session,
 			Object owner,
 			Map copyCache) throws HibernateException {
 		if ( original == null ) {
 			return null;
 		}
 		Object cached = copyCache.get(original);
 		if ( cached != null ) {
 			return cached;
 		}
 		else {
 			if ( original == target ) {
 				return target;
 			}
 			if ( session.getContextEntityIdentifier( original ) == null  &&
 					ForeignKeys.isTransient( associatedEntityName, original, Boolean.FALSE, session ) ) {
 				final Object copy = session.getFactory().getEntityPersister( associatedEntityName )
 						.instantiate( null, session );
 				//TODO: should this be Session.instantiate(Persister, ...)?
 				copyCache.put( original, copy );
 				return copy;
 			}
 			else {
 				Object id = getIdentifier( original, session );
 				if ( id == null ) {
 					throw new AssertionFailure("non-transient entity has a null id");
 				}
 				id = getIdentifierOrUniqueKeyType( session.getFactory() )
 						.replace(id, null, session, owner, copyCache);
 				return resolve( id, session, owner );
 			}
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public int getHashCode(Object x, SessionFactoryImplementor factory) {
 		EntityPersister persister = factory.getEntityPersister(associatedEntityName);
 		if ( !persister.canExtractIdOutOfEntity() ) {
 			return super.getHashCode( x );
 		}
 
 		final Serializable id;
 		if (x instanceof HibernateProxy) {
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
 
 		EntityPersister persister = factory.getEntityPersister(associatedEntityName);
 		if ( !persister.canExtractIdOutOfEntity() ) {
 			return super.isEqual(x, y );
 		}
 
 		final Class mappedClass = persister.getMappedClass();
 		Serializable xid;
 		if (x instanceof HibernateProxy) {
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
 		if (y instanceof HibernateProxy) {
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
 				.isEqual(xid, yid, factory);
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isEmbeddedInXML() {
 		return isEmbeddedInXML;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public boolean isXMLElement() {
 		return isEmbeddedInXML;
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public Object fromXMLNode(Node xml, Mapping factory) throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			return getIdentifierType(factory).fromXMLNode(xml, factory);
 		}
 		else {
 			return xml;
 		}
 	}
 
 	/**
 	 * {@inheritDoc}
 	 */
 	public void setToXMLNode(Node node, Object value, SessionFactoryImplementor factory) throws HibernateException {
 		if ( !isEmbeddedInXML ) {
 			getIdentifierType(factory).setToXMLNode(node, value, factory);
 		}
 		else {
 			Element elt = (Element) value;
 			replaceNode( node, new ElementWrapper(elt) );
 		}
 	}
 
 	public String getOnCondition(String alias, SessionFactoryImplementor factory, Map enabledFilters)
 	throws MappingException {
 		if ( isReferenceToPrimaryKey() ) { //TODO: this is a bit arbitrary, expose a switch to the user?
 			return "";
 		}
 		else {
 			return getAssociatedJoinable( factory ).filterFragment( alias, enabledFilters );
 		}
 	}
 
 	/**
 	 * Resolve an identifier or unique key value
 	 */
 	public Object resolve(Object value, SessionImplementor session, Object owner) throws HibernateException {
 		if ( isNotEmbedded( session ) ) {
 			return value;
 		}
 
-		if ( value == null ) {
-			return null;
-		}
-		else {
-			if ( isNull( owner, session ) ) {
-				return null; //EARLY EXIT!
-			}
-
+		if ( value != null && !isNull( owner, session ) ) {
 			if ( isReferenceToPrimaryKey() ) {
 				return resolveIdentifier( (Serializable) value, session );
 			}
-			else {
+			else if ( uniqueKeyPropertyName != null ) {
 				return loadByUniqueKey( getAssociatedEntityName(), uniqueKeyPropertyName, value, session );
 			}
 		}
+		
+		return null;
 	}
 
 	public Type getSemiResolvedType(SessionFactoryImplementor factory) {
 		return factory.getEntityPersister( associatedEntityName ).getIdentifierType();
 	}
 
 	protected final Object getIdentifier(Object value, SessionImplementor session) throws HibernateException {
 		if ( isNotEmbedded(session) ) {
 			return value;
 		}
 
-		if ( isReferenceToPrimaryKey() ) {
+		if ( isReferenceToPrimaryKey() || uniqueKeyPropertyName == null ) {
 			return ForeignKeys.getEntityIdentifierIfNotUnsaved( getAssociatedEntityName(), value, session ); //tolerates nulls
 		}
 		else if ( value == null ) {
 			return null;
 		}
 		else {
 			EntityPersister entityPersister = session.getFactory().getEntityPersister( getAssociatedEntityName() );
 			Object propertyValue = entityPersister.getPropertyValue( value, uniqueKeyPropertyName );
 			// We now have the value of the property-ref we reference.  However,
 			// we need to dig a little deeper, as that property might also be
 			// an entity type, in which case we need to resolve its identitifier
 			Type type = entityPersister.getPropertyType( uniqueKeyPropertyName );
 			if ( type.isEntityType() ) {
 				propertyValue = ( ( EntityType ) type ).getIdentifier( propertyValue, session );
 			}
 
 			return propertyValue;
 		}
 	}
 
 	/**
 	 * @deprecated To be removed in 5.  Removed as part of removing the notion of DOM entity-mode.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	protected boolean isNotEmbedded(SessionImplementor session) {
 //		return !isEmbeddedInXML;
 		return false;
 	}
 
 	/**
 	 * Generate a loggable representation of an instance of the value mapped by this type.
 	 *
 	 * @param value The instance to be logged.
 	 * @param factory The session factory.
 	 * @return The loggable string.
 	 * @throws HibernateException Generally some form of resolution problem.
 	 */
 	public String toLoggableString(Object value, SessionFactoryImplementor factory) {
 		if ( value == null ) {
 			return "null";
 		}
 		
 		EntityPersister persister = factory.getEntityPersister( associatedEntityName );
 		StringBuilder result = new StringBuilder().append( associatedEntityName );
 
 		if ( persister.hasIdentifierProperty() ) {
 			final EntityMode entityMode = persister.getEntityMode();
 			final Serializable id;
 			if ( entityMode == null ) {
 				if ( isEmbeddedInXML ) {
 					throw new ClassCastException( value.getClass().getName() );
 				}
 				id = ( Serializable ) value;
 			} else if ( value instanceof HibernateProxy ) {
 				HibernateProxy proxy = ( HibernateProxy ) value;
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
 	 * @return The identifier type
 	 */
 	Type getIdentifierType(Mapping factory) {
 		return factory.getIdentifierType( getAssociatedEntityName() );
 	}
 
 	/**
 	 * Convenience method to locate the identifier type of the associated entity.
 	 *
 	 * @param session The originating session
 	 * @return The identifier type
 	 */
 	Type getIdentifierType(SessionImplementor session) {
 		return getIdentifierType( session.getFactory() );
 	}
 
 	/**
 	 * Determine the type of either (1) the identifier if we reference the
 	 * associated entity's PK or (2) the unique key to which we refer (i.e.
 	 * the property-ref).
 	 *
 	 * @param factory The mappings...
 	 * @return The appropriate type.
 	 * @throws MappingException Generally, if unable to resolve the associated entity name
 	 * or unique key property name.
 	 */
 	public final Type getIdentifierOrUniqueKeyType(Mapping factory) throws MappingException {
-		if ( isReferenceToPrimaryKey() ) {
+		if ( isReferenceToPrimaryKey() || uniqueKeyPropertyName == null ) {
 			return getIdentifierType(factory);
 		}
 		else {
 			Type type = factory.getReferencedPropertyType( getAssociatedEntityName(), uniqueKeyPropertyName );
 			if ( type.isEntityType() ) {
 				type = ( ( EntityType ) type).getIdentifierOrUniqueKeyType( factory );
 			}
 			return type;
 		}
 	}
 
 	/**
 	 * The name of the property on the associated entity to which our FK
 	 * refers
 	 *
 	 * @param factory The mappings...
 	 * @return The appropriate property name.
 	 * @throws MappingException Generally, if unable to resolve the associated entity name
 	 */
 	public final String getIdentifierOrUniqueKeyPropertyName(Mapping factory)
 	throws MappingException {
-		if ( isReferenceToPrimaryKey() ) {
+		if ( isReferenceToPrimaryKey() || uniqueKeyPropertyName == null ) {
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
 	 * @return The resolved identifier (i.e., loaded entity).
 	 * @throws org.hibernate.HibernateException Indicates problems performing the load.
 	 */
 	protected final Object resolveIdentifier(Serializable id, SessionImplementor session) throws HibernateException {
 		boolean isProxyUnwrapEnabled = unwrapProxy &&
 				session.getFactory()
 						.getEntityPersister( getAssociatedEntityName() )
 						.isInstrumented();
 
 		Object proxyOrEntity = session.internalLoad(
 				getAssociatedEntityName(),
 				id,
 				eager,
 				isNullable() && !isProxyUnwrapEnabled
 		);
 
 		if ( proxyOrEntity instanceof HibernateProxy ) {
 			( ( HibernateProxy ) proxyOrEntity ).getHibernateLazyInitializer()
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
 	 * @return The loaded entity
 	 * @throws HibernateException generally indicates problems performing the load.
 	 */
 	public Object loadByUniqueKey(
 			String entityName, 
 			String uniqueKeyPropertyName, 
 			Object key, 
 			SessionImplementor session) throws HibernateException {
 		final SessionFactoryImplementor factory = session.getFactory();
 		UniqueKeyLoadable persister = ( UniqueKeyLoadable ) factory.getEntityPersister( entityName );
 
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
diff --git a/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
index c7cd51db99..7fb9c8fe78 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/ManyToOneType.java
@@ -1,318 +1,332 @@
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
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.metamodel.relational.Size;
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
-		this( scope, referencedEntityName, null, lazy, true, false, false, false );
+		this( scope, referencedEntityName, true, null, lazy, true, false, false );
 	}
 
 
 	/**
-	 * @deprecated Use {@link #ManyToOneType(TypeFactory.TypeScope, String, String, boolean, boolean, boolean, boolean ) } instead.
-	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 * @deprecated Use {@link #ManyToOneType(TypeFactory.TypeScope, String, boolean, String, boolean, boolean, boolean, boolean ) } instead.
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
-		super( scope, referencedEntityName, uniqueKeyPropertyName, !lazy, isEmbeddedInXML, unwrapProxy );
-		this.ignoreNotFound = ignoreNotFound;
-		this.isLogicalOneToOne = isLogicalOneToOne;
+		this( scope, referencedEntityName, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, ignoreNotFound, isLogicalOneToOne );
+	}
+
+	/**
+	 * @deprecated Use {@link #ManyToOneType(TypeFactory.TypeScope, String, boolean, String, boolean, boolean, boolean, boolean ) } instead.
+	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
+	 */
+	@Deprecated
+	public ManyToOneType(
+			TypeFactory.TypeScope scope,
+			String referencedEntityName,
+			String uniqueKeyPropertyName,
+			boolean lazy,
+			boolean unwrapProxy,
+			boolean ignoreNotFound,
+			boolean isLogicalOneToOne) {
+		this( scope, referencedEntityName, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, ignoreNotFound, isLogicalOneToOne );
 	}
 
 	public ManyToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
+			boolean referenceToPrimaryKey,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean ignoreNotFound,
 			boolean isLogicalOneToOne) {
-		super( scope, referencedEntityName, uniqueKeyPropertyName, !lazy, unwrapProxy );
+		super( scope, referencedEntityName, referenceToPrimaryKey, uniqueKeyPropertyName, !lazy, unwrapProxy );
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
 		// our column span is the number of columns in the PK
 		return getIdentifierOrUniqueKeyType( mapping ).getColumnSpan( mapping );
 	}
 
 	public int[] sqlTypes(Mapping mapping) throws MappingException {
 		return getIdentifierOrUniqueKeyType( mapping ).sqlTypes( mapping );
 	}
 
 	@Override
 	public Size[] dictatedSizes(Mapping mapping) throws MappingException {
 		return getIdentifierOrUniqueKeyType( mapping ).dictatedSizes( mapping );
 	}
 
 	@Override
 	public Size[] defaultSizes(Mapping mapping) throws MappingException {
 		return getIdentifierOrUniqueKeyType( mapping ).defaultSizes( mapping );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			boolean[] settable,
 			SessionImplementor session) throws HibernateException, SQLException {
 		getIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeSet( st, getIdentifier( value, session ), index, settable, session );
 	}
 
 	public void nullSafeSet(
 			PreparedStatement st,
 			Object value,
 			int index,
 			SessionImplementor session) throws HibernateException, SQLException {
 		getIdentifierOrUniqueKeyType( session.getFactory() )
 				.nullSafeSet( st, getIdentifier( value, session ), index, session );
 	}
 
 	public ForeignKeyDirection getForeignKeyDirection() {
 		return ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT;
 	}
 
 	public Object hydrate(
 			ResultSet rs,
 			String[] names,
 			SessionImplementor session,
 			Object owner) throws HibernateException, SQLException {
 		// return the (fully resolved) identifier value, but do not resolve
 		// to the actual referenced entity instance
 		// NOTE: the owner of the association is not really the owner of the id!
 		Serializable id = (Serializable) getIdentifierOrUniqueKeyType( session.getFactory() )
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
 			final EntityPersister persister = session.getFactory().getEntityPersister( getAssociatedEntityName() );
 			final EntityKey entityKey = session.generateEntityKey( id, persister );
 			if ( entityKey.isBatchLoadable() && !session.getPersistenceContext().containsEntity( entityKey ) ) {
 				session.getPersistenceContext().getBatchFetchQueue().addBatchLoadableEntityKey( entityKey );
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
 
 		if ( isNotEmbedded( session ) ) {
 			return getIdentifierType( session ).disassemble( value, session, owner );
 		}
 		
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
 
 		if ( isNotEmbedded( session ) ) {
 			return id;
 		}
 		
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
diff --git a/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
index 838955bf72..960adf18d8 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/OneToOneType.java
@@ -1,197 +1,212 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.sql.PreparedStatement;
 import java.sql.ResultSet;
 import java.sql.SQLException;
 
 import org.hibernate.HibernateException;
 import org.hibernate.MappingException;
 import org.hibernate.engine.spi.EntityKey;
 import org.hibernate.engine.spi.Mapping;
 import org.hibernate.engine.spi.SessionImplementor;
 import org.hibernate.internal.util.collections.ArrayHelper;
 import org.hibernate.metamodel.relational.Size;
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
-	 * @deprecated Use {@link #OneToOneType(TypeFactory.TypeScope, String, ForeignKeyDirection, String, boolean, boolean, String, String)}
+	 * @deprecated Use {@link #OneToOneType(TypeFactory.TypeScope, String, ForeignKeyDirection, boolean, String, boolean, boolean, String, String)}
 	 *  instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public OneToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			ForeignKeyDirection foreignKeyType,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			String entityName,
 			String propertyName) {
-		super( scope, referencedEntityName, uniqueKeyPropertyName, !lazy, isEmbeddedInXML, unwrapProxy );
-		this.foreignKeyType = foreignKeyType;
-		this.propertyName = propertyName;
-		this.entityName = entityName;
+		this( scope, referencedEntityName, foreignKeyType, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName );
+	}
+
+	/**
+	 * @deprecated Use {@link #OneToOneType(TypeFactory.TypeScope, String, ForeignKeyDirection, boolean, String, boolean, boolean, String, String)}
+	 *  instead.
+	 */
+	@Deprecated
+	public OneToOneType(
+			TypeFactory.TypeScope scope,
+			String referencedEntityName,
+			ForeignKeyDirection foreignKeyType,
+			String uniqueKeyPropertyName,
+			boolean lazy,
+			boolean unwrapProxy,
+			String entityName,
+			String propertyName) {
+		this( scope, referencedEntityName, foreignKeyType, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName );
 	}
 
 	public OneToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			ForeignKeyDirection foreignKeyType,
+			boolean referenceToPrimaryKey,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			String entityName,
 			String propertyName) {
-		super( scope, referencedEntityName, uniqueKeyPropertyName, !lazy, unwrapProxy );
+		super( scope, referencedEntityName, referenceToPrimaryKey, uniqueKeyPropertyName, !lazy, unwrapProxy );
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
 		return foreignKeyType==ForeignKeyDirection.FOREIGN_KEY_TO_PARENT;
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
 
diff --git a/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java b/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
old mode 100755
new mode 100644
index 9ef9852d5c..70c86b3f6a
--- a/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/SpecialOneToOneType.java
@@ -1,140 +1,157 @@
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
 import org.hibernate.metamodel.relational.Size;
 
 /**
  * A one-to-one association that maps to specific formula(s)
  * instead of the primary key column of the owning entity.
  * 
  * @author Gavin King
  */
 public class SpecialOneToOneType extends OneToOneType {
 	
+	/**
+	 * @deprecated Use {@link #SpecialOneToOneType(org.hibernate.type.TypeFactory.TypeScope, String, ForeignKeyDirection, boolean, String, boolean, boolean, String, String)} instead.
+	 */
+	@Deprecated
 	public SpecialOneToOneType(
 			TypeFactory.TypeScope scope,
 			String referencedEntityName,
 			ForeignKeyDirection foreignKeyType, 
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			String entityName,
 			String propertyName) {
+		this( scope, referencedEntityName, foreignKeyType, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName );
+	}
+	
+	public SpecialOneToOneType(
+			TypeFactory.TypeScope scope,
+			String referencedEntityName,
+			ForeignKeyDirection foreignKeyType,
+			boolean referenceToPrimaryKey, 
+			String uniqueKeyPropertyName,
+			boolean lazy,
+			boolean unwrapProxy,
+			String entityName,
+			String propertyName) {
 		super(
 				scope,
 				referencedEntityName, 
-				foreignKeyType, 
+				foreignKeyType,
+				referenceToPrimaryKey, 
 				uniqueKeyPropertyName, 
 				lazy,
 				unwrapProxy,
-				true, 
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
 
 		if ( isNotEmbedded(session) ) {
 			return getIdentifierType(session).disassemble(value, session, owner);
 		}
 		
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
 
 		if ( isNotEmbedded(session) ) return id;
 		
 		if (id==null) {
 			return null;
 		}
 		else {
 			return resolveIdentifier(id, session);
 		}
 	}
 	
 
 
 }
diff --git a/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
index 131251349c..60c8427551 100644
--- a/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
+++ b/hibernate-core/src/main/java/org/hibernate/type/TypeFactory.java
@@ -1,483 +1,523 @@
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
 package org.hibernate.type;
 
 import java.io.Serializable;
 import java.util.Comparator;
 import java.util.Properties;
 
-import org.jboss.logging.Logger;
-
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
+import org.jboss.logging.Logger;
 
 /**
  * Used internally to build instances of {@link Type}, specifically it builds instances of
  *
  *
  * Used internally to obtain instances of <tt>Type</tt>. Applications should use static methods
  * and constants on <tt>org.hibernate.Hibernate</tt>.
  *
  * @author Gavin King
  * @author Steve Ebersole
  */
 @SuppressWarnings({ "unchecked" })
 public final class TypeFactory implements Serializable {
 
     private static final CoreMessageLogger LOG = Logger.getMessageLogger(CoreMessageLogger.class, TypeFactory.class.getName());
 
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
 
 	public static void injectParameters(Object type, Properties parameters) {
 		if ( ParameterizedType.class.isInstance( type ) ) {
 			( (ParameterizedType) type ).setParameterValues(parameters);
 		}
 		else if ( parameters!=null && !parameters.isEmpty() ) {
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
     @SuppressWarnings({ "JavaDoc" })
 	public static CompositeCustomType customComponent(Class<CompositeUserType> typeClass, Properties parameters, TypeScope scope) {
 		try {
 			CompositeUserType userType = typeClass.newInstance();
 			injectParameters( userType, parameters );
 			return new CompositeCustomType( userType );
 		}
 		catch ( Exception e ) {
 			throw new MappingException( "Unable to instantiate custom type: " + typeClass.getName(), e );
 		}
 	}
 
 	/**
 	 * @deprecated Use {@link #customCollection(String, java.util.Properties, String, String)}
 	 * instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public CollectionType customCollection(
 			String typeName,
 			Properties typeParameters,
 			String role,
 			String propertyRef,
 			boolean embedded) {
 		Class typeClass;
 		try {
 			typeClass = ReflectHelper.classForName( typeName );
 		}
 		catch ( ClassNotFoundException cnfe ) {
 			throw new MappingException( "user collection type class not found: " + typeName, cnfe );
 		}
 		CustomCollectionType result = new CustomCollectionType( typeScope, typeClass, role, propertyRef, embedded );
 		if ( typeParameters != null ) {
 			injectParameters( result.getUserType(), typeParameters );
 		}
 		return result;
 	}
 
 	public CollectionType customCollection(
 			String typeName,
 			Properties typeParameters,
 			String role,
 			String propertyRef) {
 		Class typeClass;
 		try {
 			typeClass = ReflectHelper.classForName( typeName );
 		}
 		catch ( ClassNotFoundException cnfe ) {
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
 		catch ( Exception e ) {
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
 
 	/**
-	 * @deprecated Use {@link #oneToOne(String, ForeignKeyDirection, String, boolean, boolean, String, String)}
-	 * instead.
+	 * @deprecated Use {@link #oneToOne(String, ForeignKeyDirection, String, boolean, boolean, String, String, boolean)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public EntityType oneToOne(
 			String persistentClass,
 			ForeignKeyDirection foreignKeyType,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			String entityName,
 			String propertyName) {
-		return new OneToOneType( typeScope, persistentClass, foreignKeyType, uniqueKeyPropertyName,
-				lazy, unwrapProxy, isEmbeddedInXML, entityName, propertyName );
+		return oneToOne( persistentClass, foreignKeyType, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, entityName,
+				propertyName );
 	}
 
+	/**
+	 * @deprecated Use {@link #oneToOne(String, ForeignKeyDirection, String, boolean, boolean, String, String, boolean)} instead.
+	 */
+	@Deprecated
 	public EntityType oneToOne(
 			String persistentClass,
 			ForeignKeyDirection foreignKeyType,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			String entityName,
 			String propertyName) {
-		return new OneToOneType( typeScope, persistentClass, foreignKeyType, uniqueKeyPropertyName,
-				lazy, unwrapProxy, entityName, propertyName );
+		return oneToOne( persistentClass, foreignKeyType, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, entityName,
+				propertyName );
 	}
 
+	public EntityType oneToOne(
+			String persistentClass,
+			ForeignKeyDirection foreignKeyType,
+			boolean referenceToPrimaryKey,
+			String uniqueKeyPropertyName,
+			boolean lazy,
+			boolean unwrapProxy,
+			String entityName,
+			String propertyName) {
+		return new OneToOneType( typeScope, persistentClass, foreignKeyType, referenceToPrimaryKey,
+				uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName );
+	}
+	
+	/**
+	 * @deprecated Use {@link #specialOneToOne(String, ForeignKeyDirection, String, boolean, boolean, String, String, boolean)} instead.
+	 */
+	@Deprecated
 	public EntityType specialOneToOne(
 			String persistentClass,
 			ForeignKeyDirection foreignKeyType,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			String entityName,
 			String propertyName) {
-		return new SpecialOneToOneType( typeScope, persistentClass, foreignKeyType, uniqueKeyPropertyName,
-				lazy, unwrapProxy, entityName, propertyName );
+		return specialOneToOne( persistentClass, foreignKeyType, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy,
+				entityName, propertyName );
+	}
+
+	public EntityType specialOneToOne(
+			String persistentClass,
+			ForeignKeyDirection foreignKeyType,
+			boolean referenceToPrimaryKey,
+			String uniqueKeyPropertyName,
+			boolean lazy,
+			boolean unwrapProxy,
+			String entityName,
+			String propertyName) {
+		return new SpecialOneToOneType( typeScope, persistentClass, foreignKeyType, referenceToPrimaryKey,
+				uniqueKeyPropertyName, lazy, unwrapProxy, entityName, propertyName );
 	}
 
 
 	// many-to-one type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	public EntityType manyToOne(String persistentClass) {
 		return new ManyToOneType( typeScope, persistentClass );
 	}
 
 	public EntityType manyToOne(String persistentClass, boolean lazy) {
 		return new ManyToOneType( typeScope, persistentClass, lazy );
 	}
 
 	/**
-	 * @deprecated Use {@link #manyToOne(String, String, boolean, boolean, boolean, boolean)}
-	 * instead.
+	 * @deprecated Use {@link #manyToOne(String, boolean, String, boolean, boolean, boolean, boolean)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public EntityType manyToOne(
 			String persistentClass,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean isEmbeddedInXML,
 			boolean ignoreNotFound,
 			boolean isLogicalOneToOne) {
-		return new ManyToOneType(
-				typeScope,
-				persistentClass,
-				uniqueKeyPropertyName,
-				lazy,
-				unwrapProxy,
-				isEmbeddedInXML,
-				ignoreNotFound,
-				isLogicalOneToOne
-		);
+		return manyToOne( persistentClass, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, ignoreNotFound,
+				isLogicalOneToOne );
+	}
+
+	/**
+	 * @deprecated Use {@link #manyToOne(String, boolean, String, boolean, boolean, boolean, boolean)} instead.
+	 */
+	@Deprecated
+	public EntityType manyToOne(
+			String persistentClass,
+			String uniqueKeyPropertyName,
+			boolean lazy,
+			boolean unwrapProxy,
+			boolean ignoreNotFound,
+			boolean isLogicalOneToOne) {
+		return manyToOne( persistentClass, uniqueKeyPropertyName == null, uniqueKeyPropertyName, lazy, unwrapProxy, ignoreNotFound,
+				isLogicalOneToOne );
 	}
 
 	public EntityType manyToOne(
 			String persistentClass,
+			boolean referenceToPrimaryKey,
 			String uniqueKeyPropertyName,
 			boolean lazy,
 			boolean unwrapProxy,
 			boolean ignoreNotFound,
 			boolean isLogicalOneToOne) {
 		return new ManyToOneType(
 				typeScope,
 				persistentClass,
+				referenceToPrimaryKey,
 				uniqueKeyPropertyName,
 				lazy,
 				unwrapProxy,
 				ignoreNotFound,
 				isLogicalOneToOne
 		);
 	}
 
 	// collection type builders ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
 	/**
 	 * @deprecated Use {@link #array(String, String, Class)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public CollectionType array(String role, String propertyRef, boolean embedded, Class elementClass) {
 		return new ArrayType( typeScope, role, propertyRef, elementClass, embedded );
 	}
 
 	public CollectionType array(String role, String propertyRef, Class elementClass) {
 		return new ArrayType( typeScope, role, propertyRef, elementClass );
 	}
 
 	/**
 	 * @deprecated Use {@link #list(String, String)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public CollectionType list(String role, String propertyRef, boolean embedded) {
 		return new ListType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType list(String role, String propertyRef) {
 		return new ListType( typeScope, role, propertyRef );
 	}
 
 	/**
 	 * @deprecated Use {@link #bag(String, String)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public CollectionType bag(String role, String propertyRef, boolean embedded) {
 		return new BagType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType bag(String role, String propertyRef) {
 		return new BagType( typeScope, role, propertyRef );
 	}
 
 	/**
 	 * @deprecated Use {@link #idbag(String, String)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public CollectionType idbag(String role, String propertyRef, boolean embedded) {
 		return new IdentifierBagType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType idbag(String role, String propertyRef) {
 		return new IdentifierBagType( typeScope, role, propertyRef );
 	}
 
 	/**
 	 * @deprecated Use {@link #map(String, String)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public CollectionType map(String role, String propertyRef, boolean embedded) {
 		return new MapType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType map(String role, String propertyRef) {
 		return new MapType( typeScope, role, propertyRef );
 	}
 
 	/**
 	 * @deprecated Use {@link #orderedMap(String, String)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public CollectionType orderedMap(String role, String propertyRef, boolean embedded) {
 		return new OrderedMapType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType orderedMap(String role, String propertyRef) {
 		return new OrderedMapType( typeScope, role, propertyRef );
 	}
 
 	/**
 	 * @deprecated Use {@link #sortedMap(String, String, java.util.Comparator)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public CollectionType sortedMap(String role, String propertyRef, boolean embedded, Comparator comparator) {
 		return new SortedMapType( typeScope, role, propertyRef, comparator, embedded );
 	}
 
 	public CollectionType sortedMap(String role, String propertyRef, Comparator comparator) {
 		return new SortedMapType( typeScope, role, propertyRef, comparator );
 	}
 
 	/**
 	 * @deprecated Use {@link #set(String, String)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public CollectionType set(String role, String propertyRef, boolean embedded) {
 		return new SetType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType set(String role, String propertyRef) {
 		return new SetType( typeScope, role, propertyRef );
 	}
 
 	/**
 	 * @deprecated Use {@link #orderedSet(String, String)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public CollectionType orderedSet(String role, String propertyRef, boolean embedded) {
 		return new OrderedSetType( typeScope, role, propertyRef, embedded );
 	}
 
 	public CollectionType orderedSet(String role, String propertyRef) {
 		return new OrderedSetType( typeScope, role, propertyRef );
 	}
 
 	/**
 	 * @deprecated Use {@link #sortedSet(String, String, java.util.Comparator)} instead.
 	 * See Jira issue: <a href="https://hibernate.onjira.com/browse/HHH-7771">HHH-7771</a>
 	 */
 	@Deprecated
 	public CollectionType sortedSet(String role, String propertyRef, boolean embedded, Comparator comparator) {
 		return new SortedSetType( typeScope, role, propertyRef, comparator, embedded );
 	}
 
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
 		return new AnyType( metaType, identifierType );
 	}
 }
diff --git a/hibernate-core/src/test/java/org/hibernate/test/annotations/derivedidentities/bidirectional/OneToOneWithDerivedIdentityTest.java b/hibernate-core/src/test/java/org/hibernate/test/annotations/derivedidentities/bidirectional/OneToOneWithDerivedIdentityTest.java
index d530da8612..a63ca1e92e 100644
--- a/hibernate-core/src/test/java/org/hibernate/test/annotations/derivedidentities/bidirectional/OneToOneWithDerivedIdentityTest.java
+++ b/hibernate-core/src/test/java/org/hibernate/test/annotations/derivedidentities/bidirectional/OneToOneWithDerivedIdentityTest.java
@@ -1,92 +1,91 @@
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
 package org.hibernate.test.annotations.derivedidentities.bidirectional;
 
 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 
 import org.hibernate.Session;
-import org.hibernate.testing.FailureExpected;
 import org.hibernate.testing.TestForIssue;
 import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
 import org.junit.Test;
 
 public class OneToOneWithDerivedIdentityTest extends BaseCoreFunctionalTestCase {
 	@Test
-	@FailureExpected(jiraKey = "HHH-5695")
+	@TestForIssue(jiraKey = "HHH-5695")
 	public void testInsertFooAndBarWithDerivedId() {
 		Session s = openSession();
 		s.beginTransaction();
 		Bar bar = new Bar();
 		bar.setDetails( "Some details" );
 		Foo foo = new Foo();
 		foo.setBar( bar );
 		bar.setFoo( foo );
 		s.persist( foo );
 		s.flush();
 		assertNotNull( foo.getId() );
 		assertEquals( foo.getId(), bar.getFoo().getId() );
 
 		s.clear();
 		Bar newBar = ( Bar ) s.createQuery( "SELECT b FROM Bar b WHERE b.foo.id = :id" )
 				.setParameter( "id", foo.getId() )
 				.uniqueResult();
 		assertNotNull( newBar );
 		assertEquals( "Some details", newBar.getDetails() );
 		s.getTransaction().rollback();
 		s.close();
 	}
 	
 	@Test
 	@TestForIssue(jiraKey = "HHH-6813")
 	public void testSelectWithDerivedId() {
 		Session s = openSession();
 		s.beginTransaction();
 		Bar bar = new Bar();
 		bar.setDetails( "Some details" );
 		Foo foo = new Foo();
 		foo.setBar( bar );
 		bar.setFoo( foo );
 		s.persist( foo );
 		s.flush();
 		assertNotNull( foo.getId() );
 		assertEquals( foo.getId(), bar.getFoo().getId() );
 
 		s.clear();
 		Foo newFoo = (Foo) s.createQuery( "SELECT f FROM Foo f" ).uniqueResult();
 		assertNotNull( newFoo );
 		assertEquals( "Some details", newFoo.getBar().getDetails() );
 		s.getTransaction().rollback();
 		s.close();
 	}
 
 	@Override
 	protected Class<?>[] getAnnotatedClasses() {
 		return new Class<?>[] {
 				Foo.class,
 				Bar.class
 		};
 	}
 
 }
